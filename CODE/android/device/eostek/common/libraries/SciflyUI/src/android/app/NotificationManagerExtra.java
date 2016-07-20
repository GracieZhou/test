
package android.app;

import java.util.ArrayList;
import java.util.List;

import android.os.RemoteException;
import android.util.Slog;

/**
 * Class NotificationManagerExtra is used to manager notification. <br/>
 * 
 * <li> add/remove package name into/from white list to prevent apk from notifying or not.
 * <li> get all package name in white list.
 */
public class NotificationManagerExtra {

    private static final String TAG = "NotificationManagerExtra";

    private static final boolean DBG = true;

    private static NotificationManagerExtra mInstance = null;

    private NotificationManagerExtra() {
    }

    /**
     * Get the instance of {@link NotificationManagerExtra}.
     */
    public static NotificationManagerExtra getInstance() {
        if (mInstance == null) {
            synchronized (NotificationManagerExtra.class) {
                if (mInstance == null) {
                    mInstance = new NotificationManagerExtra();
                }
            }
        }

        return mInstance;
    }

    /**
     * Add/remove package to white list for preventing from notifying or not.
     * 
     * @param pkg package name.
     * @param enabled true for enable, otherwise false.
     */
    public void setNotificationsEnabledForApk(String pkg, boolean enabled) {
        if (DBG) {
            Slog.d(TAG, (enabled ? "allow " : "prevent ") + pkg + " notification");
        }
        INotificationManager service = NotificationManager.getService();
        try {
            service.setNotificationsEnabledForApk(pkg, enabled);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return white list of notification for apk.
     */
    public List<String> getNotificationsWhiteListForApk() {
        INotificationManager service = NotificationManager.getService();
        List<String> whiteList = new ArrayList<String>();
        try {
            whiteList = service.getNotificationsWhiteListForApk();
        } catch (RemoteException e) {
            e.printStackTrace();
            return new ArrayList<String>();
        }

        return whiteList;
    }

    /**
     * Check app using the package is able to notifiy or not.
     * 
     * @param pkg package name.
     * @return true if is able to notify, otherwise false.
     */
    public boolean areNotificationsEnabledForApk(String pkg) {
        INotificationManager service = NotificationManager.getService();
        boolean enabled = false;
        try {
            enabled = service.areNotificationsEnabledForApk(pkg);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }

        return enabled;
    }

}
