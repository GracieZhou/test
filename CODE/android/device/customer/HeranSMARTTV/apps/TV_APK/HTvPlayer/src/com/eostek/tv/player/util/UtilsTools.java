
package com.eostek.tv.player.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;

public class UtilsTools {

    // EPG timer event type none/remider/recorder
    public static enum EnumEventTimerType {
        EPG_EVENT_NONE, EPG_EVENT_REMIDER, EPG_EVENT_RECORDER,
    }

    public static final String timeformat = "yyyy-MM-dd HH:mm";

    /**
     * formate the date.
     * 
     * @param time
     * @param format
     * @return
     */
    public static String formatDate(long time, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date date = new Date(time);
        String curTimeStr = formatter.format(date);
        return curTimeStr;
    }

    /**
     * get the current top activity name
     * 
     * @param context
     * @return
     */
    public static String getCurrentActivity(Context context) {
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

    public static void startPlayerActivity(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.eostek.tv.player", "com.eostek.tv.player.PlayerActivity");
        context.startActivity(intent);
    }

    /**
     * get current Lancher activity  name
     * 
     * @param context
     * @return lanucher activity name
     */
    public static String getLauncherActivityName(Context context) {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        final ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
        if (res.activityInfo == null) {
            // should not happen. A home is always installed, isn't it?
            return null;
        }
        if (res.activityInfo.packageName.equals("android")) {
            // if has more than one lancher,also not select one ,return null；
            return null;
        } else {
            return res.activityInfo.name;
        }
    }
}
