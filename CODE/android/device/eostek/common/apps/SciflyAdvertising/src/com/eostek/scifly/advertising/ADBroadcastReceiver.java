
package com.eostek.scifly.advertising;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * advertising broadcast class.
 * 
 * @author shirley
 */
public class ADBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "ADBroadcastReceiver";

    private static final String CPE_ACTION = "com.eostek.ads";

    private static final String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";

    private static final String CONN_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (CPE_ACTION.equals(action) || BOOT_ACTION.equals(action)
                || (CONN_CHANGE_ACTION.equals(action) && isNetConnected(context))) {
            Log.d(TAG, "接收到更新广告广播 描述信息 : " + intent.getExtras());
            Intent service = new Intent(context, AdvertisingIntentService.class);
            context.startService(service);
        }
    }

    private boolean isNetConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] infos = manager.getAllNetworkInfo();
        if (infos != null) {
            for (int i = 0; i < infos.length; i++) {
                NetworkInfo info = infos[i];
                if (info.isConnected()) {
                    Log.v(TAG, "isNetConnected = true");
                    return true;
                }
            }
        }
        Log.v(TAG, "isNetConnected = false");
        return false;
    }
}
