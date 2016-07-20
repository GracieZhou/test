
package com.eos.notificationcenter.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 *  Shared preference to save value.
 */
public class MySharedPreference {
    static SharedPreferences sharedPreferences;

    public static final String FILE_NAME = "tipable";

    public static final String SHARED_KEY = "able";

    public static boolean readSharedPreferences(String key, Context context) {
        sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        boolean able = sharedPreferences.getBoolean(key, true);
        Log.i("DiskChangeBroadcastReceiver", "***************" + able);
        return able;
    }

    public static void writeSharedPreferences(String key, boolean tipable, Context context) {
        sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        Editor ed = sharedPreferences.edit();
        ed.putBoolean(key, tipable);
        ed.commit();

    }

}
