
package com.eostek.scifly.browser;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import com.eostek.scifly.browser.util.Constants;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 * projectName： Browser moduleName： BrowserApplication.java
 * 
 * @author Shirley.jiang & Ahri.chen
 * @time 2016-1-27 
 */
public class BrowserApplication extends Application {

    private static final String TAG = "BrowserApplication";

    private static BrowserApplication mInstance;

    private static DisplayImageOptions mOptions;

    private ExecutorService mThreadExecutor;

    public BrowserApplication() {
    }

    public static BrowserApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoadConfig();
        // init single thread pool.
        mThreadExecutor = Executors.newSingleThreadExecutor();
        mInstance = this;
    }

    public DisplayImageOptions getDisplayImageOptions() {
        if (mOptions == null) {
            initImageLoadConfig();
        }
        return mOptions;
    }

    private void initImageLoadConfig() {
        File cacheDir = new File(Constants.CACHE_PATH);
        Log.d(TAG, "cacheDir=" + cacheDir);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2).tasksProcessingOrder(QueueProcessingType.FIFO)
                .denyCacheImageMultipleSizesInMemory().memoryCache(new LruMemoryCache(5 * 1024 * 1024))
                .memoryCacheSize(5 * 1024 * 1024).memoryCacheSizePercentage(13).diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100).diskCache(new UnlimitedDiscCache(cacheDir))
                .imageDownloader(new BaseImageDownloader(this))
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()).writeDebugLogs().build();
        ImageLoader.getInstance().init(config);
        mOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
                .showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image)
                .showImageOnLoading(R.drawable.default_image).bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    /**
     * add long time task.
     * @param runnable
     */
    public void addThreadTask(Runnable runnable) {
        Log.i(TAG, "addThreadTask");
        mThreadExecutor.execute(runnable);
    }
}
