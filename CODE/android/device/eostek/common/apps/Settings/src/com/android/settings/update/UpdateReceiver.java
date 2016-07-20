
package com.android.settings.update;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * This class is used to receive the static registration broadcast, especially
 * "android.intent.action.BOOT_COMPLETED".
 * 
 * @author Psso.Song
 */
public class UpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Constants.TAG, "UpdateReceiver->Receive {action=" + intent.getAction() + ", intent=" + intent.toString()
                + "}");
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            boolean needCheckUpdate = Utils.getBoolean(context, Constants.PREFERENCE_BOOT_COMPLETED, true);
            if (!needCheckUpdate) {
                Utils.putBoolean(context, Constants.PREFERENCE_BOOT_COMPLETED, true);
            }
        } else {
            intent.setClass(context, UpdateService.class);
            Log.d(Constants.TAG, "UpdateReceiver->Starting UpdateService...");
            context.startService(intent);
        }
    }
}
