
package com.eostek.tv.launcher.business.receiver;

import com.eostek.tv.launcher.HomeApplication;
import com.eostek.tv.launcher.util.LConstants;
import com.eostek.tv.launcher.util.UIUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.util.Log;

/**
 * projectName： TVLauncher moduleName： LocalChangedReceiver.java
 * 
 * @author chadm.xiang
 * @version 1.0.0
 * @time 2014-8-6 下午7:38:14
 * @Copyright © 2014 Eos Inc.
 */

public class LocalChangedReceiver extends BroadcastReceiver {

    private final String TAG = LocalChangedReceiver.class.getSimpleName();

    public volatile static boolean networkConnected = false;

    private Context mContext;

    private Handler mHandler;

    private final Object lock = new Object();

    public LocalChangedReceiver(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, " action = " + action);
        if (action.equals(Intent.ACTION_LOCALE_CHANGED)) {
            // langauge change
            mHandler.sendEmptyMessage(LConstants.LOCAL_CHANGE);
        } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            boolean noConnect = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            Log.v(TAG, "EXTRA_NO_CONNECTIVITY = " + noConnect);
            if (!noConnect) {
                // check netowork,if is connected,send message to get upgrade
                startCheckNetwork(context);
            }
        }

    }

    private Runnable networkRunnable = new Runnable() {

        @Override
        public void run() {
            synchronized (lock) {
                if (UIUtil.isNetworkConnected(mContext)) {
                    networkConnected = true;
                    // mHandler.removeMessages(LConstants.MSG_GET_UPDATE_VERSION);
                    // mHandler.sendEmptyMessage(LConstants.MSG_GET_UPDATE_VERSION);
                    mHandler.removeMessages(LConstants.MSG_GET_UPDATE_DATA);
                    mHandler.sendEmptyMessage(LConstants.GET_WEATHER_INFO);
                    mHandler.sendEmptyMessageDelayed(LConstants.MSG_GET_UPDATE_DATA, 10 * 1000);
                } else {
                    networkConnected = false;
                }
                Log.v(TAG, "networkConnected = " + networkConnected);
            }
        }
    };

    private void startCheckNetwork(final Context context) {
        // start a Thread to handler network changed
        HomeApplication.getInstance().addNetworkTask(networkRunnable);
    }

}
