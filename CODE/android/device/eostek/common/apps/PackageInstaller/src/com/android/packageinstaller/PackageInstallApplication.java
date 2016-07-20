
package com.android.packageinstaller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Application;
import android.util.Log;

public class PackageInstallApplication extends Application {

    private static final String TAG = PackageInstallApplication.class.getSimpleName();

    private static PackageInstallApplication instance;

    private ExecutorService networkThreadExecutor;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        networkThreadExecutor = Executors.newSingleThreadExecutor();
    }

    public static PackageInstallApplication getInstance() {
        if (instance == null) {
            instance = new PackageInstallApplication();
        }
        return PackageInstallApplication.instance;
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
