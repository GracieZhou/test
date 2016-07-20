
package com.heran.launcher2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.heran.launcher2.util.GoogleAnalyticsUtil;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

public class HomeApplication extends Application {

    private static final String TAG = HomeApplication.class.getSimpleName();

    private static HomeApplication instance;

    // the thread pool to check network state change
    private ExecutorService networkThreadExecutor;

    private ExecutorService weatherThreadExecutor;

    private ExecutorService newsThreadExecutor;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        networkThreadExecutor = Executors.newSingleThreadExecutor();
        weatherThreadExecutor = Executors.newSingleThreadExecutor();
        newsThreadExecutor = Executors.newSingleThreadExecutor();
        GoogleAnalyticsUtil.initData(this);
    }

    public static HomeApplication getInstance() {
        return instance;
    }

    /**
     * add a new task to Thread pool to check network
     * 
     * @param runnable the runnable task
     */
    public void addNetworkTask(Runnable runnable) {
        Log.i(TAG, "addNetworkTask");
        networkThreadExecutor.execute(runnable);
    }

    public void addWeatherTask(Runnable runnable) {
        Log.i(TAG, "addWeatherTask");
        weatherThreadExecutor.execute(runnable);
    }

    public void addNewsTask(Runnable runnable) {
        Log.i(TAG, "addNewsTask");
        newsThreadExecutor.execute(runnable);
    }

    public void glideLoadGif(Context mContext, String mString, ImageButton mButton) {
        Log.d(TAG, "------req image:" + mString);
        if (mString == null) {
            return;
        }
        if (isNumeric(mString)) {
            Glide.with(mContext).load(Integer.valueOf(mString)).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(mButton);
        } else {
            Glide.with(mContext).load(mString).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mButton);
        }
    }

    public void glideLoadGif(Context mContext, String mString, ImageView mImage) {
        Log.d(TAG, "------req image:" + mString);
        if (mString == null) {
            return;
        }
        if (isNumeric(mString)) {
            Glide.with(mContext).load(Integer.valueOf(mString)).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(mImage);
        } else {
            Glide.with(mContext).load(mString).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mImage);
        }
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

}
