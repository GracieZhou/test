
package com.android.settings.update.ota;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class IntentReceiver extends BroadcastReceiver implements Constants {
    private static Logger sLog = new Logger(IntentReceiver.class);

    @Override
    public void onReceive(Context context, Intent intent) {
        sLog.debug("onReceive: " + intent.toString());

        String action = intent.getAction();
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action) || Intent.ACTION_BOOT_COMPLETED.equals(action)) {

            intent.setClass(context, UpdateService.class);
            context.startService(intent);
        }
    }

}
