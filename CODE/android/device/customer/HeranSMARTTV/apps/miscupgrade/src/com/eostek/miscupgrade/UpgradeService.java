
package com.eostek.miscupgrade;

import java.io.File;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class UpgradeService extends IntentService {
    private UpgradeResult animResult;

    private UpgradeResult launchResult;

    private String[] mUpgradeFiles = Util.files;

    private String mExternalMediaRootPath = Util.path;

    private UpgradeHelper mUpgradeHelper = UpgradeHelper.geInstance(this);

    private boolean isLauncher;

    private boolean isAnimation;

    public UpgradeService() {
        super("UpgradeService");
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x123:
                    PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
                    pm.reboot(null);
                    break;
                case 0x120:
                    Toast.makeText(getApplicationContext(), animResult.name(), Toast.LENGTH_LONG).show();
                    break;
                case 0x121:
                    Toast.makeText(getApplicationContext(), launchResult.name(), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("vicent", "action = " + intent.getAction());
        if (intent.getAction().equals(UpgradeConstants.UPGRADESERVICE)) {
            if (mUpgradeFiles == null) {
                return;
            } else {
                for (String filename : mUpgradeFiles) {
                    if (UpgradeConstants.USER_BOOTANIMATION_NAME.equals(filename) && Util.hasAnimFile) {
                        animResult = mUpgradeHelper.upgrade(
                                mExternalMediaRootPath + UpgradeConstants.PARENT_DIR + filename,
                                UpgradeConstants.USER_BOOTANIMATION_PATH);
                        if (animResult.name().equals("UPGRADE_SUCCESS")) {
                            isAnimation = true;
                        }
                        handler.removeMessages(0x120);
                        handler.sendEmptyMessage(0x120);
                    } else if (UpgradeConstants.USER_LAUNCHER_NAME.equals(filename) && Util.hasLauncherFile) {
                        Log.v("xander", mExternalMediaRootPath);
                        launchResult = mUpgradeHelper.upgrade(mExternalMediaRootPath + UpgradeConstants.PARENT_DIR
                                + filename);
                        if (launchResult.name().equals("UPGRADE_SUCCESS")) {
                            isLauncher = true;
                        }
                        handler.removeMessages(0x121);
                        handler.sendEmptyMessage(0x121);
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if (isLauncher || isAnimation) {
                    handler.removeMessages(0x123);
                    handler.sendEmptyMessageDelayed(0x123, 4000);
                }
            }
            
        }
    }

}
