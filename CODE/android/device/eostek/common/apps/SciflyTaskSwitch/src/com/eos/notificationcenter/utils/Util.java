
package com.eos.notificationcenter.utils;

import java.lang.reflect.Method;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManagerExtra;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.SystemProperties;
import android.util.Log;

/**
 *  Util for program.
 */
public class Util {

    /**
     * Tag used to show in logcat.
     */
    public static String TAG = "Util";

    /**
     * Get the count of running thread.
     * @param context
     * @return
     */
    static public int getRunningThreadCount(Context context) {

        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        final List<RunningAppProcessInfo> recentTasks = mActivityManager.getRunningAppProcesses();// getRecentTasks(MAX_TASKS,
                                                                                                  // ActivityManager.RECENT_IGNORE_UNAVAILABLE);
        return recentTasks.size();
    }

    /**
     * Kill processes.
     */
    static public void killProcesses() {
        try {
            ActivityManagerExtra ame = ActivityManagerExtra.getInstance();
            ame.killAllBackgroundApks();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the diemnsion pixel size.
     * @param context
     * @param id
     * @return
     */
    public static int getDiemnsionPixelSize(Context context, int id) {
        return context.getResources().getDimensionPixelSize(id);
    }
    
    /**
     * Switch source while device has tv feature.
     * @param context
     * @return
     */
    public static void switchSource(Context context) {
        try {
        	String platform = SystemProperties.get("ro.scifly.platform", null);
        	if(platform.equals("tv")) {
        		//ClassLoader cl = context.getClassLoader();
                Class<?> tvCommonManager = Class.forName("com.mstar.android.tv.TvCommonManager");
                Class<?> enumInputSource =Class.forName("com.mstar.android.tvapi.common.vo.TvOsType$EnumInputSource");
                
                Object inputSource=enumInputSource.getField("E_INPUT_SOURCE_STORAGE").get("E_INPUT_SOURCE_STORAGE");
                
                Method getInstance = tvCommonManager.getMethod("getInstance");
                Method setInputSource = tvCommonManager.getMethod("setInputSource", enumInputSource);

                setInputSource.invoke(getInstance.invoke(tvCommonManager), inputSource);
        	} else if(platform.equals("dongle")) {
        		
        	}
        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
