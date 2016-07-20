
package scifly.middleware.network;

import java.lang.reflect.Constructor;

import android.content.Context;
import android.os.SystemProperties;
import android.util.Log;

public class EthernetManagerGlobal {

    private static final String TAG = "EthernetManagerGlobal";

    private static final String PLATFORM = SystemProperties.get("ro.board.platform", "");

    private static final String PLATFORM_MUJI = "muji";

    private static final String PLATFORM_MADISON = "madison";

    private IEthernetManager mEthernetManagerImpl;

    public EthernetManagerGlobal(Context context) {
        final String clsName;
        // mstar 4.4
        if (PLATFORM.equals(PLATFORM_MADISON)) {
            clsName = "scifly.middleware.network.mstar.kitkat.EthernetManagerImpl";
            // mstar 5.0
        } else if (PLATFORM.equals(PLATFORM_MUJI)) {
            clsName = "scifly.middleware.network.mstar.lollipop.EthernetManagerImpl";
        } else {
            Log.w(TAG, "unknown platform");
            return;
        }

        Class<?> c = null;
        Constructor<?> constructor = null;
        try {
            c = Class.forName(clsName);
            constructor = c.getConstructor(Context.class);
            mEthernetManagerImpl = (IEthernetManager) constructor.newInstance(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public IpConfig getConfiguration() {
        if (mEthernetManagerImpl == null) {
            return null;
        }
        return mEthernetManagerImpl.getConfiguration();
    }

    public void setConfiguration(IpConfig config) {
        if (mEthernetManagerImpl == null) {
            return;
        }
        mEthernetManagerImpl.setConfiguration(config);
    }

    public boolean isEnabled() {
        if (mEthernetManagerImpl == null) {
            return false;
        }
        return mEthernetManagerImpl.isEnabled();
    }

    public void setEnabled(boolean enable) {
        if (mEthernetManagerImpl == null) {
            return;
        }
        mEthernetManagerImpl.setEnabled(enable);
    }
}
