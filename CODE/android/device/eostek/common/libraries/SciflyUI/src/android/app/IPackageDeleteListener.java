
package android.app;

/**
 * API for deletion callbacks from the Package Manager.
 *
 * @author charles.tai
 */
public interface IPackageDeleteListener {
    /**
     * Return the status of the package uninstalled.
     *
     * @param packageName the packageName of the App.
     * @param returnCode 1 stand for Succeed, other mean failed.
     */
    void packageDeleted(String packageName, int returnCode);
}
