package com.eostek.scifly.devicemanager.util;

import android.util.Log;

public final class Debug {
    
    private static final String TAG = "DeviceManager";
    
    private static volatile boolean dbg = true;

    public static void d(String tag, String msg) {
        if (dbg) {
            Log.d(TAG, "[" + tag + "]---> " + msg);
        }
    }

    public static void e(String tag, String msg) {
        Log.d(TAG, "[" + tag + "]---> " + msg);
    }
}
