
package com.eostek.scifly.browser;

import com.eostek.scifly.browser.util.Constants;
import com.eostek.scifly.browser.util.UIUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

/**
 * projectName： Browser moduleName： ConnChangedReceiver.java
 * 
 * @author Shirley.jiang
 * @time 2016-1-27 
 */
public class ConnChangedReceiver extends BroadcastReceiver {

    private final String TAG = ConnChangedReceiver.class.getSimpleName();

    public volatile static boolean bNetworkConnected = false;

    private Context mContext;

    private Handler mHandler;

    private final Object mLock = new Object();

    public ConnChangedReceiver(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, " onReceiver.action = " + action);
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            startAddTak(context);
        } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (networkInfo != null) {
                Log.d(TAG, "networkInfo.getDetailedState():" + networkInfo.getDetailedState());
                if (DetailedState.CONNECTED == networkInfo.getDetailedState()) {
                    startAddTak(context);
                }
            }

        }

    }

    private Runnable mNetworkRunnable = new Runnable() {

        @Override
        public void run() {
            synchronized (mLock) {
                if (UIUtil.isNetConnected(mContext)) {
                    bNetworkConnected = true;
                } else {
                    bNetworkConnected = false;
                }
                mHandler.removeMessages(Constants.MSG_GET_UPDATE_DATA);
                mHandler.sendEmptyMessageDelayed(Constants.MSG_GET_UPDATE_DATA, Constants.MSG_UPDATE_DATA_DELAY);
                Log.v(TAG, "checking network:" + bNetworkConnected);
            }
        }
    };

    public void startAddTak(Context context) {
        // start a Thread to handler network changed
        if (mContext == null) {
            mContext = context;
        }
        BrowserApplication.getInstance().addThreadTask(mNetworkRunnable);
    }

}
