package com.eostek.tvmenu;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Application;
import android.util.Log;

public class TvMenuApplication extends Application {
    private static final String TAG = TvMenuApplication.class.getSimpleName();
    
    // the thread pool to handle tvapi invoke
    private ExecutorService mTvApiThreadExecutor;

    @Override
    public void onCreate() {
        super.onCreate();
        mTvApiThreadExecutor = Executors.newSingleThreadExecutor();
    }
    
    /**
     * add a new task to Thread pool to change source
     * 
     * @param runnable the runnable task
     */
    public void addTvApiTask(Runnable runnable) {
        Log.i(TAG, "addTvApiTask");
        if (mTvApiThreadExecutor == null) {
            mTvApiThreadExecutor = Executors.newSingleThreadExecutor();
        }
        mTvApiThreadExecutor.execute(runnable);
    }
}
