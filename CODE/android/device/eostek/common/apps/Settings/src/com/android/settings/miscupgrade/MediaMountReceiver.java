
package com.android.settings.miscupgrade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class MediaMountReceiver extends BroadcastReceiver {
    private final static String TAG = UpgradeConstants.TAG;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, intent.toString());

        String action = intent.getAction();
        Uri uri = intent.getData();
        if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
            if (Util.hasExternalUpdateFile(uri) && Util.checkAllFileMD5(context)) {
                Log.d(TAG,
                        "Name : UdiskUpgrade, Version : 1.0.0, Date : 2015-1-27, Publisher : Michael.Zhang, Revision :1.0.0 ");
                Intent intent1 = new Intent(context, UpgradeActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
            }
        }
    }
}
