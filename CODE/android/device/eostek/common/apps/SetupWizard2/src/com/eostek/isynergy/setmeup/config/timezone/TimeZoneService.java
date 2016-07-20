
package com.eostek.isynergy.setmeup.config.timezone;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.eostek.isynergy.setmeup.common.Constants;
import com.eostek.isynergy.setmeup.common.Constants.ACTION_TYPE;
import com.eostek.isynergy.setmeup.config.IfService;
import com.eostek.isynergy.setmeup.service.WifiService;

public class TimeZoneService implements IfService {
    private final String TAG = "TimeZoneService";

    private Context context;

    public TimeZoneService(Context context) {
        this.context = context;
    }

    @Override
    public int doAction(ACTION_TYPE type, String paras) {
        Log.d(TAG, "paras = " + paras);

        int ret = Constants.ErrorCode.INVALID_PARA.getValue();

        if (paras != null && paras.length() > 0) {
            String paraArray[] = paras.split(Constants.SET_ME_UP_PARA_SPLIT);
            if (paraArray.length == 2) {
                AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                mAlarmManager.setTimeZone(paraArray[1]);

                Log.d(TAG, "finished to set timeZone..." + paraArray[1]);

                ret = Constants.SuccessCode.SET_ME_UP_SUCCESS.getValue();

                settingTimeZone(Constants.ACTION_TYPE.TIMEZONE_SETTING, paraArray[1]);
            }
        }

        return ret;
    }

    private int settingTimeZone(ACTION_TYPE type, String timeZone) {
        int ret = Constants.SuccessCode.SET_ME_UP_SUCCESS.getValue();
        Intent intent = new Intent(context, WifiService.class);
        intent.putExtra(Constants.NAME_ACTION_TYPE, type.getValue());
        intent.putExtra(Constants.NAME_PARAMETER, timeZone);
        context.startService(intent);
        return ret;
    }
}
