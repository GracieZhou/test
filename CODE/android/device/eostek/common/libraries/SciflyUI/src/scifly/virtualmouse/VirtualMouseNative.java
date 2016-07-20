
package scifly.virtualmouse;

import android.util.Log;

public class VirtualMouseNative {

    public static final String TAG = "VirtualMouseNative";

    static {
        try {
            System.loadLibrary("scifly_virtualmouse_jni");
        } catch (Exception e) {
            Log.e(TAG, "Failed to load libvirtualmouse_jni.so while initializing"
                    + " scifly.virtualmouse; exception was ", e);
        }
    }

    public static native void native_open();
    public static native void native_close();
    public static native void native_move(int x, int y);
    public static native void native_click(int keyCode);

}
