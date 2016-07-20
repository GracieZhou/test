
package android.app;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.os.RemoteException;
import android.util.Slog;

/**
 * Class for manager processes background or retrieving information related to the package. <br/>
 * 
 * <li> add/remove package into/from white list to prevent apk from auto-start or not.
 * <li> get all package in white list.
 * <li> kill all background processes.
 */
public class ActivityManagerExtra {

    private static final String TAG = "ActivityManagerExtra";

    private static final boolean DBG = true;

    private static ActivityManagerExtra mInstance = null;

    private ActivityManagerExtra() {
    }

    /**
     * Get the instance of {@link ActivityManagerExtra}.
     */
    public static ActivityManagerExtra getInstance() {
        if (mInstance == null) {
            synchronized (ActivityManagerExtra.class) {
                if (mInstance == null) {
                    mInstance = new ActivityManagerExtra();
                }
            }
        }

        return mInstance;
    }

    /**
     * Add/remove package to white list for preventing from auto-start or not.
     * 
     * @param pkg package name.
     * @param enabled true for enable, otherwise false.
     */
    public void setAutoStartEnabledForApk(String pkg, boolean enabled) {
        if (DBG) {
            Slog.d(TAG, (enabled ? "allow " : "prevent ") + pkg + " auto-start");
        }
        try {
            ActivityManagerNative.getDefault().setAutoStartEnabledForApk(pkg, enabled);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return white list of auto-start for apk.
     */
    public List<String> getAutoStartWhiteListForApk() {
        try {
            return ActivityManagerNative.getDefault().getAutoStartWhiteListForApk();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return new ArrayList<String>();
    }

    /**
     * Kill all processes running background including apk under /system/app.
     */
    public void killAllBackgroundApks() {
        try {
            ActivityManagerNative.getDefault().killAllBackgroundApks();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the package name of the top Activity in Task.
     */
    public static String getTopAppPackageName() {
        List<RunningTaskInfo> topApp = null;
        try {
            topApp = ActivityManagerNative.getDefault().getTasks(1, 0);
        } catch (RemoteException e) {
            return "";
        }

        if (topApp.size() == 1) {
            RunningTaskInfo taskInfo = topApp.get(0);
            return taskInfo.topActivity.getPackageName();
        }

        return "";
    }

}
