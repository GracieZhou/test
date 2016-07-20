
package com.eostek.tv.launcher.business.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.eostek.tv.launcher.util.LConstants;

/**
 * projectName： LLauncher moduleName： TimeChangedReceiver.java
 * 
 * @author kevin.duan
 * @version 1.0.0
 * @time 2015-7-1 上午11:02:13
 * @Copyright © 2015 Eos Inc.
 */
public class TimeChangedReceiver extends BroadcastReceiver {

    private static final String TAG = "TimeChangedReceiver";

    private Context mContext;

    private Handler mHandler;

    public TimeChangedReceiver(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    @Override
    public void onReceive(Context arg0, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "package action :" + action);
        Message msg = Message.obtain();
        msg.what = LConstants.UPDATE_WEATHER_TIME_VIEW;
        mHandler.sendMessage(msg);
    }
}
