
package com.eostek.isynergy.setmeup.devsel;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.eostek.isynergy.setmeup.common.Constants;
import com.eostek.isynergy.setmeup.common.Constants.ACTION_TYPE;
import com.eostek.isynergy.setmeup.config.IfService;
import com.eostek.isynergy.setmeup.service.WifiService;
import com.eostek.isynergy.setmeup.ui.SetmeupMainActivity;

public class SelDeviceService implements IfService {
    private final String TAG = "SelDeviceService";

    private Context context;

    public SelDeviceService(Context context) {
        this.context = context;
    }

    @Override
    public int doAction(ACTION_TYPE type, String paras) {
        int ret = Constants.SuccessCode.SET_ME_UP_SUCCESS.getValue();

        switch (type) {
            case REQ_PAIRING_CODE: {
                Log.d(TAG, "REQ_PAIRING_CODE");
                ret = reqPairingCode(type, paras);
            }
                break;
            default: {
                Log.d(TAG, "default");
                ret = Constants.ErrorCode.SERVICE_NOT_SUPPORTED.getValue();
            }

        }

        return ret;
    }

    private int reqPairingCode(ACTION_TYPE type, String paras) {
        Log.d(TAG, "notify service to ...");
        int ret = Constants.SuccessCode.SET_ME_UP_SUCCESS.getValue();
        Intent intent = new Intent(context, WifiService.class);
        intent.putExtra(Constants.NAME_ACTION_TYPE, type.getValue());
        intent.putExtra(Constants.NAME_PARAMETER, paras);
        context.startService(intent);
        return ret;
    }
}
