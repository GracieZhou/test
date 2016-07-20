
package scifly.virtualmouse;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.KeyEvent;

import scifly.SciflyManager;

public class VirtualMouseManager {

    public static final String TAG = "VirtualMouseManager";

    private static VirtualMouseManager mInstance = null;

    private static IVirtualMouseManager mService = null;

    private VirtualMouseManager() {
    }

    private static IVirtualMouseManager getService() {
        if (mService == null) {
            mService = SciflyManager.getInstance().getVirtualMouseManager();
        }

        return mService;
    }

    public static VirtualMouseManager getInstance() {
        if (mInstance == null) {
            synchronized (VirtualMouseManager.class) {
                if (mInstance == null) {
                    mInstance = new VirtualMouseManager();
                }
            }
        }

        return mInstance;
    }

    public boolean isVirtualMouseEnabled() {
        IVirtualMouseManager service = getService();
        try {
            return service.isVirtualMouseEnabled();
        } catch (RemoteException e) {
            Log.e(TAG, "can not get the status of virtual mouse");
            return false;
        }
    }

    public void setVirtualMouseEnabled(boolean enabled) {
        IVirtualMouseManager service = getService();
        try {
            service.setVirtualMouseEnabled(enabled);
        } catch (RemoteException e) {
            Log.e(TAG, "can not set the status of virtual mouse");
        }
    }

    public void toggle() {
        IVirtualMouseManager service = getService();
        try {
            service.toggle();
        } catch (RemoteException e) {
            Log.e(TAG, "can not toggle the virtual mouse");
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        IVirtualMouseManager service = getService();
        try {
            return service.dispatchKeyEvent(event);
        } catch (RemoteException e) {
            Log.e(TAG, "can not dispatch key event of virtual mouse");
            return false;
        }
    }
}
