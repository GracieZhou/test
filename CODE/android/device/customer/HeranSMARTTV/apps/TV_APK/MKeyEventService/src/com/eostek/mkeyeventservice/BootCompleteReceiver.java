
package com.eostek.mkeyeventservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

public class BootCompleteReceiver extends BroadcastReceiver {

    private final String TAG = "BootCompleteReceiver";

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, "action:" + action);
        if (Settings.System.getInt(context.getContentResolver(), "MModel", 0) == 1
                && Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            context.startService(new Intent(context, MKeyEventService.class));
        }
    }
}
