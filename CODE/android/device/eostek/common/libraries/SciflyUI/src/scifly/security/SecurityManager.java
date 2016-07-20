
package scifly.security;

import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import scifly.SciflyManager;
import scifly.security.ISecurityManager;
import scifly.security.IOnInstallEnableListener;

/**
 * Class for Security Manager. <br/>
 * <li>check apk to be installing whether it is in blacklist or not
 */
public class SecurityManager {

    private static final String TAG = "SecurityManager";

    private static SecurityManager mInstance = null;

    private static ISecurityManager mService = null;

    private OnInstallEnableListener mProxyListener = null;

    private final IOnInstallEnableListener.Stub mListener = new IOnInstallEnableListener.Stub() {

        public void onEnable(boolean enabled, String message, int level) {
            if (mProxyListener != null) {
                mProxyListener.onEnable(enabled, message, level);
            }
        }
    };

    private SecurityManager() {
    }

    private static ISecurityManager getService() {
        if (mService == null) {
            mService = SciflyManager.getInstance().getSecurityManager();
        }

        return mService;
    }

    /**
     * Get the instance of {@link SecurityManager}.
     * @since API 2.0
     */
    public static SecurityManager getInstance() {
        if (mInstance == null) {
            synchronized (SecurityManager.class) {
                if (mInstance == null) {
                    mInstance = new SecurityManager();
                }
            }
        }

        return mInstance;
    }

    /**
     * Query the black list to check the pkg is included.
     * 
     * @param pkg package name.
     * @param listener callback for notify install is enabled or not.
     * @since API 2.0
     */
    public void checkPkgFromBlacklist(String pkg, OnInstallEnableListener listener) {
        this.mProxyListener = listener;
        ISecurityManager service = getService();
        try {
            service.checkPkgFromBlacklist(pkg, mListener);
        } catch (RemoteException e) {
            Log.e(TAG, "checkPkgFromBlacklist occured exception.", e);
            e.printStackTrace();
        }
    }
}
