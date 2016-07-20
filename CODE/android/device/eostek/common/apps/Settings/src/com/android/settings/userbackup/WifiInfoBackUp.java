
package com.android.settings.userbackup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * This class is used to back up the user's wifi data.
 * 
 * @author melody.xu
 * @date 2014-6-20
 */
public class WifiInfoBackUp extends BroadcastReceiver {

    private static final String TAG = "WifiInfoBackUp";

    private static Context mContext;

    /**
     * This method is backup the wifi info.
     * 
     * @param context context
     */
    public WifiInfoBackUp(Context context) {
        Log.d(TAG, "WifiInfoBackUp init");
        mContext = context;
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mContext.registerReceiver(this, filter);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "action on onReceive is : " + action);
        if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            final NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (networkInfo != null) {
                Log.d(TAG, "networkInfo.getDetailedState():" + networkInfo.getDetailedState());
                switch (networkInfo.getDetailedState()) {
                    case CONNECTED:
                        BackUpData.backupData("wifi", "wifi_ssid", "wifi_ssid");
                        break;
                    default:
                        break;
                }
            }

        }
    }

}
