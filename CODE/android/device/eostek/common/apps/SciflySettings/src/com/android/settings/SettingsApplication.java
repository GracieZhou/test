
package com.android.settings;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

public class SettingsApplication extends Application {

    private static final String TAG = "SettingsApplication";

    private static final boolean DEBUG = true;

    private static ExecutorService mExecutorService;

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public static void LOGD(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void LOGD(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Log.d(tag, msg, tr);
        }
    }

    public static ExecutorService prepareExecutorService() {
        LOGD(TAG, "create ExecutorService");
        if (mExecutorService == null) {
            synchronized (SettingsApplication.class) {
                if (mExecutorService == null) {
                    // FIXME
                    mExecutorService = Executors.newFixedThreadPool(1);
                }
            }
        }

        return mExecutorService;
    }

    public static void execute(Runnable runnable) {
        if (mExecutorService == null) {
            prepareExecutorService();
        }

        mExecutorService.execute(runnable);
    }

    public void shutdown() {
        if (mExecutorService != null) {
            mExecutorService.shutdown();
            mExecutorService = null;
        }
    }
}
