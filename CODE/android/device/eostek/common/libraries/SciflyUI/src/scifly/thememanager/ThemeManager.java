
package scifly.thememanager;

import java.io.IOException;
import java.util.zip.ZipException;

import android.os.RemoteException;
import android.util.Log;
import scifly.SciflyManager;
import scifly.thememanager.IThemeChangeListener;;
/**
 * Theme manager.
 * 
 * @author Youpeng
 * @date 2014-01-07.
 */
public class ThemeManager {
    private static final String mStoragePath = "/data/eostek";
    
    public static final String TAG = "ThemeManager";

    private static IThemeManager mService = null;

    private static ThemeManager mInstance;

    public static ThemeManager getInstance() {
        if (mInstance == null) {
            synchronized (ThemeManager.class) {
                if (mInstance == null) {
                    mInstance = new ThemeManager();
                }
            }
        }

        return mInstance;
    }

    private static IThemeManager getService() {
        if (mService == null) {
            mService = SciflyManager.getInstance().getThemeManager();
        }

        return mService;
    }

    /**
     * change theme by using the pointed path.
     * 
     * @param themePath where theme file stored.
     */
    public void changeTheme(String themePath,IThemeChangeListener listener) {

        IThemeManager themeService = getService();

        try {
            themeService.changeTheme(themePath,listener,true);
        } catch (RemoteException e) {
            Log.e(TAG, "can not get the status of theme service");
        }

    }

    public void debugEnable(boolean enable) {
        IThemeManager themeService = getService();

        try {
            themeService.debugEnable(enable);
        } catch (RemoteException e) {
            Log.e(TAG, "can not get the status of theme service");
        }
    }
    
    public void themeRollBack(IThemeChangeListener listener){
        IThemeManager themeService = getService();
        
        try {
            themeService.themeRollBack(listener);
        } catch (RemoteException e) {
            Log.e(TAG, "can not get the status of theme service");
        }
    }
    
    /**
     * 
     * 
     * @param themePath where zip-file saved.
     * @param listener status listener.
     * @param isUnzip if zip-file need unzip.
     */
    public void changeTheme(String themePath,IThemeChangeListener listener,boolean isUnzip) {

        IThemeManager themeService = getService();

        try {
            themeService.changeTheme(themePath,listener,isUnzip);
        } catch (RemoteException e) {
            Log.e(TAG, "can not get the status of theme service");
        }

    }
    
//    /**
//     * unzip theme.
//     * @param source where zip-file saved.
//     * @throws ZipException
//     * @throws IOException
//     */
//    public static void unzipThemeResources(String source)  throws ZipException, IOException{
//        ThemeManagerUtils.upZipFile(source, mStoragePath);
//    }
}
