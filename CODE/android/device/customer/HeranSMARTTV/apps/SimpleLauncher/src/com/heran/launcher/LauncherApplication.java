
package com.heran.launcher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Application;
import android.util.Log;

/**
 * projectName： HLauncher2 moduleName：HomeApplication.java
 * 
 * @author laird.li
 * @version 1.0.0
 * @time 2016-03-30
 * @Copyright © 2016 Eos, Inc.
 */
public class LauncherApplication extends Application {

    private static final String TAG = LauncherApplication.class.getSimpleName();

    private static LauncherApplication instance;

    // the thread pool to check network state change
    private ExecutorService networkThreadExecutor;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        networkThreadExecutor = Executors.newSingleThreadExecutor();
    }

    public static LauncherApplication getInstance() {
        if (instance == null) {
            instance = new LauncherApplication();
        }
        return LauncherApplication.instance;
    }

    /**
     * add a new task to Thread pool to check network
     * 
     * @param runnable the runnable task
     */
    public void addNetworkTask(Runnable runnable) {
        Log.i(TAG, "addNetworkTask");
        if (networkThreadExecutor == null) {
            networkThreadExecutor = Executors.newSingleThreadExecutor();
        }
        networkThreadExecutor.execute(runnable);
    }

}
