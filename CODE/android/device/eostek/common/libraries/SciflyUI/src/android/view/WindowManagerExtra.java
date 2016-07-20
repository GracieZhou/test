package android.view;

import java.util.ArrayList;
import java.util.List;

import android.os.RemoteException;
import android.util.Slog;

/**
 * Class WindowManagerExtra is used to manager apk add view. <br/>
 * 
 * <li> add/remove package name into/from white list to prevent apk from add view or not.
 * <li> get all package name in white list.
 */
public class WindowManagerExtra {

    private static final String TAG = "WindowManagerExtra";

    private static final boolean DBG = true;

    private static WindowManagerExtra mInstance = null;

    private static IWindowManager sWindowManagerService;

    private WindowManagerExtra() {
        sWindowManagerService = WindowManagerGlobal.getWindowManagerService();
    }

    /**
     * Get the instance of {@link WindowManagerExtra}.
     */
    public static WindowManagerExtra getInstance() {
        if (mInstance == null) {
            synchronized (WindowManagerExtra.class) {
                if (mInstance == null) {
                    mInstance = new WindowManagerExtra();
                }
            }
        }

        return mInstance;
    }

    /**
     * Add/remove package to white list for preventing from adding view or not.
     * 
     * @param pkg package name.
     * @param enabled true for enable, otherwise false.
     */
    public void setAddViewEnabledForApk(String pkg, boolean enabled) {
        if (DBG) {
            Slog.d(TAG, (enabled ? "allow " : "prevent ") + pkg + " add view");
        }
        try {
            sWindowManagerService.setAddViewEnabledForApk(pkg, enabled);
        } catch (RemoteException e) {
            Slog.d(TAG, "add/remove " + pkg + " failed.");
        }
    }

    /**
     * @return white list of add view for apk.
     */
    public List<String> getAddViewWhiteListForApk() {
        try {
            return sWindowManagerService.getAddViewWhiteListForApk();
        } catch (RemoteException e) {
            Slog.d(TAG, "get white list failed.");
        }
        return new ArrayList<String>();
    }

    /**
     * Check application using the package is able to add view or not.
     * 
     * @param pkg package name.
     * @return true if enabled, otherwise false.
     */
    public boolean areAddViewEnabledForApk(String pkg) {
        try {
            return sWindowManagerService.areAddViewEnabledForApk(pkg);
        } catch (RemoteException e) {
            Slog.d(TAG, "check add view enabled failed.");
        }

        return false;
    }

}
