
package android.app;

import android.content.Context;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Slog;

/**
 * Class for retrieving various kinds of information related to the package.
 */
public class PackageManagerExtra {

    private static final String TAG = "PackageManagerExtra";

    private static final boolean DBG = true;

    private static PackageManagerExtra mInstance;

    private IPackageInstallListener mIPackageInstallListener;

    private IPackageDeleteListener mIPackageDeleteListener;

    private PackageManager mPackageManager;

    private PackageManagerExtra() {

    }

    /**
     * Get the instance of {@link PackageManagerExtra}.
     */
    public static PackageManagerExtra getInstance() {
        if (mInstance == null) {
            synchronized (PackageManagerExtra.class) {
                if (mInstance == null) {
                    mInstance = new PackageManagerExtra();
                }
            }
        }

        return mInstance;
    }

    /**
     * Check the apk using the package is system app or not.
     * 
     * @param pkg package name.
     * @return true if system app, otherwise false.
     */
    public boolean isSystemApp(String pkg) {
        if (DBG) {
            Slog.d(TAG, "check system app pkg : " + pkg);
        }
        if (TextUtils.isEmpty(pkg)) {
            return false;
        }

        try {
            return ActivityThread.getPackageManager().isSystemApp(pkg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Install a package. Since this may take a little while, the result will be
     * posted back to the given listener. An installation will fail if the
     * calling context lacks the
     * {@link android.Manifest.permission#INSTALL_PACKAGES} permission, if the
     * package named in the package file's manifest is already installed, or if
     * there's no space available on the device.
     *
     * @param context
     * @param packageURI The location of the package file to install.
     * @param listener An listener callback to get notified when the package
     *            installation is complete.
     *            {@link android.app.IPackageInstallListener#packageInstalled(String, int)}
     *            will be called when that happens. listener may be null to
     *            indicate that no callback is desired.
     */
    public void installPackage(Context context, Uri packageURI, IPackageInstallListener listener) {
        mIPackageInstallListener = listener;
        mPackageManager = context.getPackageManager();
        PackageInstallObserver observer = new PackageInstallObserver();
        mPackageManager.installPackage(packageURI, observer, PackageManager.INSTALL_REPLACE_EXISTING, null);
    }

    private class PackageInstallObserver extends IPackageInstallObserver.Stub {
        public void packageInstalled(String packageName, int returnCode) {
            mIPackageInstallListener.packageInstalled(packageName, returnCode);
            Slog.d(TAG, "PackageManager PackageName : " + packageName + " ReturnCode : " + returnCode);
        }
    }

    /**
     * Attempts to delete a package. Since this may take a little while, the
     * result will be posted back to the given listener. A deletion will fail if
     * the calling context lacks the
     * {@link android.Manifest.permission#DELETE_PACKAGES} permission, if the
     * named package cannot be found, or if the named package is a
     * "system package". (TODO: include pointer to documentation on
     * "system packages")
     *
     * @param context
     * @param packageName The name of the package to delete
     * @param listener An listener callback to get notified when the package
     *            deletion is complete.
     *            {@link android.app.IPackageDeleteListener#packageDeleted(String, int)}
     *            will be called when that happens. listener may be null to
     *            indicate that no callback is desired.
     */
    public void deletePackage(Context context, String packageName, IPackageDeleteListener listener) {
        mIPackageDeleteListener = listener;
        mPackageManager = context.getPackageManager();
        PackageDeleteObserver observer = new PackageDeleteObserver();
        mPackageManager.deletePackage(packageName, observer, 0);
    }

    private class PackageDeleteObserver extends IPackageDeleteObserver.Stub {
        public void packageDeleted(String packageName, int returnCode) {
            mIPackageDeleteListener.packageDeleted(packageName, returnCode);
            Slog.d(TAG, "PackageManager PackageName : " + packageName + " ReturnCode : " + returnCode);
        }
    }

}
