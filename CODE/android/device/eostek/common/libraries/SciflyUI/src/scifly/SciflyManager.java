package scifly;

import android.content.Context;
import android.os.RemoteException;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

import scifly.virtualmouse.IVirtualMouseManager;
import scifly.thememanager.IThemeManager;
import scifly.security.ISecurityManager;

public class SciflyManager {

    private static final String TAG = "SciflyManager";

    static SciflyManager mInstance = null;

    ISciflyManager mService = null;

    private SciflyManager(ISciflyManager service) {
        Log.d(TAG, "construct SciflyManager");
        mService = service;
    }

    public static SciflyManager getInstance() {
        if (mInstance == null) {
            synchronized (SciflyManager.class) {
                if (mInstance == null) {
                    IBinder b = ServiceManager.getService(Context.SCIFLY_SERVICE);
                    mInstance = new SciflyManager(ISciflyManager.Stub.asInterface(b));
                }
            }
        }

        return mInstance;
    }

    public IVirtualMouseManager getVirtualMouseManager() {
        try {
            return mService.getVirtualMouseManager();
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public IThemeManager getThemeManager() {
        try {
            return mService.getThemeManager();
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ISecurityManager getSecurityManager() {
        try {
            return mService.getSecurityManager();
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

}
