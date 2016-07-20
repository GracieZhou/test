
package com.eostek.tv.player.standby;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import com.eostek.tv.player.util.Constants;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;

/**
 * @projectName： EosTvPlayer
 * @moduleName： StandbyBroadReceiver.java
 * @author jachensy.chen
 * @version 1.0.0
 * @time 2014-1-21
 * @Copyright © 2013 EOSTEK, Inc.
 */
public class StandbyBroadReceiver extends BroadcastReceiver {
    private static final String TAG = "StandbyBroadReceiver";

    private static int count = 60;

    private static Context mContext;

    private static final String STAND_BY = "standby";

    private static final int AUTO_SLEEP_DELAY = 2 * 60 * 1000;
    
    private static final int START_COUNTERDOWN_DELAY =500;

    private static final int STANDBY = 0x10000;
    
    private static final int STARTCOUNTERDOWN= 0x10001;

    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (TvCommonManager.getInstance().getCurrentInputSource()
                    .equals(EnumInputSource.E_INPUT_SOURCE_STORAGE)) {
                return;
            }
            switch (msg.what) {
                case STANDBY:
                    if (count == 0) {
                        Intent intentStandby = new Intent(Constants.DISMISSCOUTDOWN);
                        intentStandby.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.sendBroadcast(intentStandby);
                        Log.e(TAG, "do the action fo standby system.");
                        // TvCommonManager.getInstance().standbySystem(STAND_BY);
                        TvCommonManager.getInstance().enterSleepMode(true, true);
                    } else if (count > 0) {
                        // if the saving mode is off, we will don't do standby.
                        boolean isVGA = TvCommonManager.getInstance().getCurrentTvInputSource() == TvCommonManager.INPUT_SOURCE_VGA;
                        Log.d(TAG, "isVGA :" + isVGA);
                        if (Settings.System.getInt(mContext.getContentResolver(), "savemode", 0) == 0 && !isVGA) {
                            Log.e(TAG, "Save mode is off. You can set it in advance setting.");
                            return;
                        }
                        Log.e(TAG, "left time:::" + count);
                        if (count == 60) {
                            sendEmptyMessageDelayed(STARTCOUNTERDOWN, START_COUNTERDOWN_DELAY);
                        }
                        mHandler.sendEmptyMessageDelayed(STANDBY, 1000);
                        Intent intentStandby = new Intent(Constants.SHOWCOUTDOWN);
                        intentStandby.putExtra("LeftTime", count);
                        mContext.sendBroadcast(intentStandby);
                        count--;
                    }
                    break;
                case STARTCOUNTERDOWN:
                    // only in TV start CountDownActivity
                    Log.d(TAG, "getCurrentActivity :" + getCurrentActivity(mContext));
                    if (!getCurrentActivity(mContext).equals("com.eostek.tv.player.PlayerActivity")) {
                        return;
                    }
                    Intent intent = new Intent("com.eostek.tv.player.intent.action.CounterDown");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if (intent.getAction().equals(Constants.CANCELSTANDBY)) {
            mHandler.removeMessages(STANDBY);
            if (!TvChannelManager.getInstance().isSignalStabled()) {
                count = 60;
                mHandler.sendEmptyMessageDelayed(STANDBY, AUTO_SLEEP_DELAY);
            }
            Intent intentStandby = new Intent(Constants.DISMISSCOUTDOWN);
            intentStandby.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.sendBroadcast(intentStandby);
        } else if (intent.getAction().equals(Constants.STARTSTANDBY)) {
            count = 60;
            mHandler.removeMessages(STANDBY);
            mHandler.sendEmptyMessageDelayed(STANDBY, AUTO_SLEEP_DELAY);
        }
    }
    
    public static String getCurrentActivity(Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> forGroundActivity = activityManager.getRunningTasks(1);
        RunningTaskInfo currentActivity;
        currentActivity = forGroundActivity.get(0);
        String activityName = currentActivity.topActivity.getClassName();
        if (activityName == null) {
            return "";
        }
        return activityName;
    }
}
