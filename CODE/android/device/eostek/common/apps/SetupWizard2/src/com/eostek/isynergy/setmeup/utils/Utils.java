
package com.eostek.isynergy.setmeup.utils;

import java.lang.reflect.Method;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

public class Utils {

    /** Anything worse than or equal to this will show 0 bars. */
    private static final int MIN_RSSI = -100;

    /** Anything better than or equal to this will show the max bars. */
    private static final int MAX_RSSI = -55;

    /** default level of wifi signal */
    private static final int DEFAULT_LEVEL = 4;

    public final static int WIFI_LEVEL_ERROR = -1;

    public final static int WIFI_LEVEL_WEAK = 1;

    public final static int WIFI_LEVEL_MIDDLE = 2;

    public final static int WIFI_LEVEL_STRONG = 3;

    public static int calculateSignalLevel(int rssi) {
        if (rssi == Integer.MAX_VALUE) {
            return WIFI_LEVEL_ERROR;
        }

        if (rssi <= MIN_RSSI) {
            return 0;
        }

        if (rssi >= MAX_RSSI) {
            return DEFAULT_LEVEL - 1;
        }

        float inputRange = (MAX_RSSI - MIN_RSSI);
        float outputRange = (DEFAULT_LEVEL - 1);
        return (int) ((float) (rssi - MIN_RSSI) * outputRange / inputRange);

    }

    /**
     * Get the value for the given key.
     * 
     * @return an empty string if the key isn't found
     */
    public static String get(Context context, String key) {
        String ret = "";
        try {
            ClassLoader cl = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class systemProperty = cl.loadClass("android.os.SystemProperties");

            // Parameters Types
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = new Class[1];
            paramTypes[0] = String.class;

            Method get = systemProperty.getMethod("get", paramTypes);

            // Parameters
            Object[] params = new Object[1];
            params[0] = new String(key);

            ret = (String) get.invoke(systemProperty, params);
        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            ret = "";
        }
        return ret;
    }

    public static void print(String tag, String str) {
        if (TextUtils.isEmpty(tag)) {
            tag = "Tools";
        }
        Log.i(tag, str);
    }
}
