
package com.eostek.wasuwidgethost.business;

import com.eostek.wasuwidgethost.util.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

/**
 * projectName： WasuWidgetHost.
 * moduleName： NetworkReceiver.java.
 * 
 * @author chadm.xiang
 * @version 1.0.0
 * @time 2014-7-31 2:50:19 pm
 * @Copyright © 2014 Eos Inc.
 */

public class NetworkReceiver extends BroadcastReceiver {

    public volatile boolean networkConnected = false;

    private Context mContext;

    private final Object lock = new Object();

    private Handler mHandler;

    /*
     * (non-Javadoc)
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
     * android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        String action = intent.getAction();
        Log.v("NetworkReceiver", "action = " + action);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
            startCheckNetwork(mContext);
        }
    }

    private Runnable networkRunnable = new Runnable() {

        @Override
        public void run() {
            synchronized (lock) {
                if (Utils.isNetworkConnected(mContext)) {
                    networkConnected = true;
                    Intent intent = new Intent("com.eostek.network_ok");
                    mContext.sendBroadcast(intent);
                } else {
                    networkConnected = false;
                }
                Log.v("NetworkReceiver", "isNetworkState = " + networkConnected);
            }
        }
    };

    private void startCheckNetwork(final Context context) {
        // start a Thread to handler network changed
        new Thread(networkRunnable).start();
    }
}
