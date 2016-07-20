
package android.app;

/**
 * API for installation callbacks from the Package Manager.
 * 
 * @author charles.tai
 */
public interface IPackageInstallListener {
    /**
     * Return the status of the package installed.
     * 
     * @param packageName the packageName of the App.
     * @param returnCode 1 stand for Succeed, other mean failed.
     */
    void packageInstalled(String packageName, int returnCode);
}
