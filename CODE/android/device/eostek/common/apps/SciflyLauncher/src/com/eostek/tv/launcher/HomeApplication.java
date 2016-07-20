
package com.eostek.tv.launcher;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.eostek.tv.launcher.R;
import com.eostek.tv.launcher.util.CrashHandler;
import com.eostek.tv.launcher.util.GoogleAnalyticsUtil;
import com.eostek.tv.launcher.util.LConstants;
import com.eostek.tv.launcher.util.UIUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

/*
 * projectName： TVLauncher
 * moduleName： HomeApplication.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-7-14 下午7:14:41
 * @Copyright © 2014 Eos Inc.
 */

public class HomeApplication extends Application {

    private static final String TAG = HomeApplication.class.getSimpleName();

    private static HomeApplication instance;

    private static class Config {
        public static final boolean DEVELOPER_MODE = false;
    }

    /** the flag whether has Tv Module **/
    private static boolean hasTVModule = false;

    // support mulit screen 1080P and 720P
    private static float factor = 1f;

    // focus type,static or dynamic or others
    private static int focusType = -1;

    private static int defaultXml = R.xml.page_default;

    // the thread pool to handler source change
    private ExecutorService sourceThreadExecutor;

    // the thread pool to check network state change
    private ExecutorService networkThreadExecutor;

    public static int getDefaultXml() {
        return defaultXml;
    }

    @SuppressWarnings("unused")
    @Override
    public void onCreate() {
        if (Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
        }
        super.onCreate();
        setInstance(HomeApplication.this);
        hasTVModule = isHaveTV();
        factor = UIUtil.getScreenHieghtPX(instance)[0] / LConstants.DEFAULT_SCREEN_WIDTH;
        focusType = LConstants.FOCUS_TYPE_STATIC;
        initImageLoader(getApplicationContext());

        if (hasTVModule) {
            sourceThreadExecutor = Executors.newSingleThreadExecutor();
        }
        networkThreadExecutor = Executors.newSingleThreadExecutor();

        CrashHandler crashHandler = CrashHandler.getInstance();
        // 注册crashHandler
        crashHandler.init(instance);

        GoogleAnalyticsUtil.initData(this);
    }

    /**
     * init the UIL Image Loader
     * 
     * @param context The context object
     */
    public static void initImageLoader(Context context) {
        File cacheDir = StorageUtils.getCacheDirectory(context, false);
        
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY).denyCacheImageMultipleSizesInMemory()
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb // Mb
                /* .writeDebugLogs() */// Remove for release app
                .build();
        ImageLoader.getInstance().init(config);
    }

    /**
     * whether to add TView in UI,if the function return true,add TView ,else
     * not
     * 
     * @return true if the platform is tv or box,else false
     */
    private static boolean isHaveTV() {
        boolean shouldAddTView = false;
        String board = UIUtil.get(instance, "ro.scifly.platform");
        Log.v(TAG, "SystemPropertiesProxy = " + board);
        if (LConstants.PLATFORM_DANGLE.equals(board)) {
            shouldAddTView = false;
            defaultXml = R.xml.page_default;
        } else if (LConstants.PLATFORM_TV.equals(board)) {
            shouldAddTView = true;
            defaultXml = R.xml.page_l;
        } else if (LConstants.PLATFORM_BOX.equals(board)) {
            shouldAddTView = true;
            defaultXml = R.xml.page_l;
        }
        defaultXml = R.xml.page_default;
        return shouldAddTView;
    }

    public static boolean isHasTVModule() {
        return hasTVModule;
    }

    public static HomeApplication getInstance() {
        return instance;
    }

    public static void setInstance(HomeApplication application) {
        HomeApplication.instance = application;
    }

    public static float getFactor() {
        return factor;
    }

    public static int getFocusType() {
        return focusType;
    }

    /**
     * add a new task to Thread pool to change source
     * 
     * @param runnable the runnable task
     */
    public void addSourceTask(Runnable runnable) {
        Log.i(TAG, "addSourceTask");
        if (sourceThreadExecutor == null) {
            sourceThreadExecutor = Executors.newSingleThreadExecutor();
        }
        sourceThreadExecutor.execute(runnable);
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

}
