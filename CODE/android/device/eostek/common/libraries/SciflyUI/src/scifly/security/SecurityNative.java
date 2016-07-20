
package scifly.security;

import android.util.Log;

public class SecurityNative {

    public static final String TAG = "SecurityNative";

    static {
        try {
            System.loadLibrary("scifly_security_jni");
        } catch (Exception e) {
            Log.e(TAG, "Failed to load libscifly_security_jni.so", e);
        }
    }

    public static native boolean checkPermission(int pid, String signature);
}
