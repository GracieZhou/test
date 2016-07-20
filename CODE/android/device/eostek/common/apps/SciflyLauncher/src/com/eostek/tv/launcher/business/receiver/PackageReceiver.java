
package com.eostek.tv.launcher.business.receiver;

import com.eostek.tv.launcher.util.LConstants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * projectName： TVLauncher 
 * moduleName： PackageReceiver.java
 * 
 * @author chadm.xiang
 * @version 1.0.0
 * @time 2014-7-24 下午4:29:03
 * @Copyright © 2014 Eos Inc.
 */
public class PackageReceiver extends BroadcastReceiver {

    private static final String TAG = "PackageReceiver";

    private Context mContext;

    private Handler mHandler;

    public PackageReceiver(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    @Override
    public void onReceive(Context arg0, Intent intent) {
        String action = intent.getAction();
        String[] pkgString = intent.getDataString().split(":");
        String pkgName = null;
        if (pkgString.length > 1) {
            pkgName = pkgString[1];
        }
        Log.d(TAG, "package action :" + action + ";pkgName = " + pkgName);
        Message msg = Message.obtain();
        msg.obj = pkgName;
        if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
            msg.what = LConstants.PACKAGE_ADDED;
            // mHandler.sendEmptyMessage(LConstants.PACKAGE_ADDED);
        } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
            msg.what = LConstants.PACKAGE_REMOVED;
            // mHandler.sendEmptyMessage(LConstants.PACKAGE_REMOVED);
        } else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
            msg.what = LConstants.PACKAGE_REPLACEED;
        }
        mHandler.sendMessage(msg);
    }
}
