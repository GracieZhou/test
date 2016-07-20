
package com.eostek.hotkeyservice;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.eostek.hotkeyservice.util.Constants;

/**
 * @ClassName: BootCompleteReceiver
 * @Description:
 * @author: Android_Robot
 * @date: 2015-6-2 12:38:10
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    
    private final String TAG = "BootCompleteReceiver";

    private Context mContext;
    
    private final int INVALID = -1;
    
    private final int ON = 1;

    private final String HOTKEY_SERVICE_NAME = "com.eostek.hotkeyservice.HotKeyService";
    
    private boolean isServiceRunning(String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
        for (RunningServiceInfo info : serviceList) {
            if (info.service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
    
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mContext.startService(new Intent(mContext, HotKeyService.class));
        }
        
    };

    @Override
    public final void onReceive(Context context, Intent intent) {
        this.mContext = context;
        String action = intent.getAction();
        Log.v(TAG, "action:" + action);
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            context.startService(new Intent(context, HotKeyService.class));
        } else if (Constants.REBOOT_HOTKEY_SERVICE.equals(action)) {
            mHandler.sendEmptyMessageDelayed(0, 1000);
        }
    }

}
