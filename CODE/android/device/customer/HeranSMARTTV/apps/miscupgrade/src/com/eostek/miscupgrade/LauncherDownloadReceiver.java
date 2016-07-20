
package com.eostek.miscupgrade;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

public class LauncherDownloadReceiver extends BroadcastReceiver {

    private final static String TAG = "LauncherDownloadReceiver";

    private static final String LAUNCHER_RECEIVER = "com.heran.launcher_download_success";
    
    private static final String LAUNCHERBACKUPDIR = "/sdcard/Download/HLauncher.apk";
    
    private UpgradeHelper mUpgradeHelper;
    
    Context mContext;
    
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x123:
                    PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
                    pm.reboot(null);
                    break;
                case 0x124:
                    UpgradeResult result = mUpgradeHelper.upgrade(LAUNCHERBACKUPDIR);
                    if (result.name().equals("UPGRADE_SUCCESS")) {
                        Log.i("vicent", "UPGRADE_SUCCESS");
                        mHandler.sendEmptyMessageDelayed(0x123, 5000);
                    }
                    break;
            }
        }
    };


    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;
        mUpgradeHelper = UpgradeHelper.geInstance(context);
        
        String action = intent.getAction();
        Log.i(TAG, "action = " + action);
        if (LAUNCHER_RECEIVER.equals(action)) {
            File file = new File(LAUNCHERBACKUPDIR);
            Log.i(TAG, file.exists() + "");
            if (file.exists() && checkLauncherMd5(context)) {
                mHandler.sendEmptyMessageDelayed(0x124, 500);
            }
        }
    }
    
    private boolean checkLauncherMd5(Context context) {
        String userLauncherPath = UpgradeHelper.getUserLauncherPath(context);
        Log.i("vicent", "userLauncherPath = " + userLauncherPath);
        boolean hasLauncher = Util.checkMd5(LAUNCHERBACKUPDIR, userLauncherPath);
        Log.i("vicent", "hasLauncher = " + hasLauncher);
        return hasLauncher;
    }
}
