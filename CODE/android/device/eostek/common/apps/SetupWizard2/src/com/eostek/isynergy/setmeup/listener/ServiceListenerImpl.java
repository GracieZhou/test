
package com.eostek.isynergy.setmeup.listener;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.eostek.isynergy.setmeup.common.Constants;
import com.eostek.isynergy.setmeup.config.ServiceManager;

public class ServiceListenerImpl implements ServiceListener {
    private final String TAG = "ServiceListenerImpl";

    private ServiceManager serManager;

    public ServiceListenerImpl(Context context, Handler handler) {
        serManager = ServiceManager.getInstance(context);
    }

    @Override
    public int onCallService(int instanceID, int actionType, String content) {
        Log.d(TAG, "receive action to deal and actionID:" + actionType + " paras=" + content);
        Log.d(TAG, "REQ_PAIRING_CODE=" + Constants.ACTION_TYPE.REQ_PAIRING_CODE.getValue());
        Log.d(TAG, "WIFI_SETTING=" + Constants.ACTION_TYPE.WIFI_SETTING.getValue());
        Log.d(TAG, "TIMEZONE_SETTING=" + Constants.ACTION_TYPE.TIMEZONE_SETTING.getValue());
        Log.d(TAG, "RESTORE_FACTORY=" + Constants.ACTION_TYPE.RESTORE_FACTORY.getValue());
        Log.d(TAG, "DEVICE_NAME_SETTING=" + Constants.ACTION_TYPE.DEVICE_NAME_SETTING.getValue());
        Log.d(TAG, "ADNET_SETTING=" + Constants.ACTION_TYPE.ADNET_SETTING.getValue());
        if (serManager != null) {
            if (actionType == Constants.ACTION_TYPE.REQ_PAIRING_CODE.getValue()) {
                int ret = serManager.doAction(Constants.ACTION_TYPE.REQ_PAIRING_CODE, content);

                return ret;
            } else if (actionType == Constants.ACTION_TYPE.WIFI_SETTING.getValue()) {
                int ret = serManager.doAction(Constants.ACTION_TYPE.WIFI_SETTING, content);

                return ret;
            } else if (actionType == Constants.ACTION_TYPE.TIMEZONE_SETTING.getValue()) {
                int ret = serManager.doAction(Constants.ACTION_TYPE.TIMEZONE_SETTING, content);

                return ret;
            } else if (actionType == Constants.ACTION_TYPE.RESTORE_FACTORY.getValue()) {
                int ret = serManager.doAction(Constants.ACTION_TYPE.RESTORE_FACTORY, content);

                return ret;
            } else if (actionType == Constants.ACTION_TYPE.ADNET_SETTING.getValue()) {
                int ret = serManager.doAction(Constants.ACTION_TYPE.ADNET_SETTING, content);

                return ret;
            } else if (actionType == Constants.ACTION_TYPE.DEVICE_NAME_SETTING.getValue()) {
                int ret = serManager.doAction(Constants.ACTION_TYPE.DEVICE_NAME_SETTING, content);

                return ret;

            }

            Log.d(TAG, "SERVICE_NOT_SUPPORTED");
            return Constants.ErrorCode.SERVICE_NOT_SUPPORTED.getValue();
        } else {
            Log.d(TAG, "INTERNAL_ERROR");
            return Constants.ErrorCode.INTERNAL_ERROR.getValue();
        }
    }
}
