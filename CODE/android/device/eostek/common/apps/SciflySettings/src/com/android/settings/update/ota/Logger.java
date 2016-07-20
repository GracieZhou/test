
package com.android.settings.update.ota;

import android.util.Log;

public class Logger {

    private static final String TAG = "OTA";

    private static final boolean DBG = true;

    private String mSubClass;

    public Logger(Class<?> cls) {
        mSubClass = cls.getSimpleName();
    }

    public void verbose(String msg) {
        if (DBG) {
            Log.v(TAG, mSubClass + "\t" + msg);
        }
    }

    public void debug(String msg) {
        if (DBG) {
            Log.d(TAG, mSubClass + "\t" + msg);
        }
    }

    public void info(String msg) {
        Log.i(TAG, mSubClass + "\t" + msg);
    }

    public void error(String msg) {
        Log.e(TAG, mSubClass + "\t" + msg);
    }

}
