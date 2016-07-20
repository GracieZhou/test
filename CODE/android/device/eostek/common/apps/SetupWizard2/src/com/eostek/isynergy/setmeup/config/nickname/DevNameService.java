
package com.eostek.isynergy.setmeup.config.nickname;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.eostek.isynergy.setmeup.common.Constants;
import com.eostek.isynergy.setmeup.common.TimeoutThread;
import com.eostek.isynergy.setmeup.common.Constants.ACTION_TYPE;
import com.eostek.isynergy.setmeup.config.IfService;
import com.eostek.isynergy.setmeup.config.external.DeviceNameService;
import com.eostek.isynergy.setmeup.config.external.ExternalServiceManager;
import com.eostek.isynergy.setmeup.service.WifiService;

public class DevNameService implements IfService {
    private final String TAG = "DevNameService";

    private DeviceNameService extDevNameService;

    private Context context;

    public DevNameService(Context context) {
        this.context = context;

        extDevNameService = ExternalServiceManager.getInstance(context).getDevNameService();
    }

    @Override
    public int doAction(ACTION_TYPE type, String paras) {
        if (paras != null && paras.length() > 0) {
            String devName = paras;
            if (paras != null && paras.length() > 0) {
                String paraArray[] = paras.split(Constants.SET_ME_UP_PARA_SPLIT);
                if (paraArray != null && paraArray.length >= 2) {
                    devName = paraArray[1];
                }
            }

            Intent intent = setUPNPDevName(devName);

            int ret = setHDName(devName);
            if (ret != Constants.SuccessCode.SET_ME_UP_SUCCESS.getValue()) {
                Log.d(TAG, "fail to set system device name...");
                // TODO DEAL ERROR
                // return Constants.ErrorCode.FAILED_HD_NAME_SETTING.getValue();
            }

            SharedPreferences sp = context.getSharedPreferences(Constants.SP_SET_ME_UP, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(Constants.LAST_SETTING_DEV_NAME, devName);
            editor.apply();
            Log.d(TAG, "persist dev name " + devName);

            context.startService(intent);

            return Constants.SuccessCode.SET_ME_UP_SUCCESS.getValue();
        }

        return Constants.ErrorCode.INVALID_PARA.getValue();
    }

    /**
     * 修改upnp设备名称，
     * 
     * @param name
     * @return
     */
    private Intent setUPNPDevName(String name) {
        Intent intent = new Intent(context, WifiService.class);
        intent.putExtra(Constants.NAME_ACTION_TYPE, Constants.ACTION_TYPE.DEVICE_NAME_SETTING.getValue());
        intent.putExtra(Constants.NAME_PARAMETER, name);

        return intent;
    }

    private int setHDName(String name) {
        int ret = extDevNameService.setDeviceName(name);
        return ret;
    }

    public String getDevName() {

        String devName = extDevNameService.getDeviceName();
        Log.d(TAG, "got dev name from external interface ..." + devName);

        /*
         * SharedPreferences sp =
         * context.getSharedPreferences(Constants.SP_SET_ME_UP,
         * Context.MODE_PRIVATE); String lastDevName =
         * sp.getString(Constants.LAST_SETTING_DEV_NAME, null);
         * //设备名称已经被修改过，那么将不再从系统拿，除非系统主动发出修改通知，目前还没有建立类似的通知机制 if (lastDevName !=
         * null && lastDevName.length() > 0) { devName = lastDevName; }
         */

        if (devName == null || devName.length() == 0) {
            devName = Constants.DEFAULT_DEV_NAME;
        }

        return devName;
    }

}
