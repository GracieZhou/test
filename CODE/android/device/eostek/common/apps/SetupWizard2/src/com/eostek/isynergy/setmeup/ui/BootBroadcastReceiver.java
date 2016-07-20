
package com.eostek.isynergy.setmeup.ui;

import com.eostek.isynergy.setmeup.service.WifiService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {

    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    private final String TAG = "SetMeUpReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!"dongle".equals(SystemProperties.get("ro.scifly.platform", ""))) {
            return;
        }
        Log.d(TAG, "action from intent " + intent.getAction() + " final action is " + ACTION);

        Log.d(TAG, "BootBroadcastReceiver ...");

        Log.d(TAG, "onReceive::time is " + System.currentTimeMillis());
        StartTask task = new StartTask(context);
        Thread thread = new Thread(task);
        thread.start();

        Log.d(TAG, "onReceive::time is " + System.currentTimeMillis());
    }
}

class StartTask implements Runnable {
    private Context context;

    protected StartTask(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        Intent wifiServiceIntent = new Intent(context, WifiService.class);
        context.startService(wifiServiceIntent);
    }

}
