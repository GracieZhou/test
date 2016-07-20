
package com.eostek.tv.launcher.business.receiver;

import com.eostek.tv.launcher.util.LConstants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

/*
 * projectName： TVLauncher
 * moduleName： WallPaperListener.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-11-12 下午5:08:02
 * @Copyright © 2014 Eos Inc.
 */

public class WallPaperListener extends BroadcastReceiver {

    private final String TAG = WallPaperListener.class.getSimpleName();
    
    private Handler mHandler;
    
    public WallPaperListener(Handler handler){
        this.mHandler = handler;
    }

    /*
     * (non-Javadoc)
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
     * android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, "action = " + action);
        if (Intent.ACTION_WALLPAPER_CHANGED.equals(action)) {
//            mHandler.sendEmptyMessage(LConstants.WALL_PAPER_CHANGE);
        }
    }

}
