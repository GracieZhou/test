
package com.eostek.documentui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Application;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

public class DocumentApplication extends Application {

    /**
     * the thread pool to handle tvapi invoke.
     */
    private ExecutorService mThreadExecutor;

    private static DocumentApplication instance;

    public DocumentApplication() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mThreadExecutor = Executors.newSingleThreadExecutor();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).memoryCacheExtraOptions(480, 800)
                .threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
                .tasksProcessingOrder(QueueProcessingType.FIFO).denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024)).memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13).imageDownloader(new BaseImageDownloader(this))
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()).writeDebugLogs().build();
        ImageLoader.getInstance().init(config);

    }

    public static DocumentApplication getInstance() {
        return instance;
    }

    /**
     * add a new task to Thread pool to change source.
     * 
     * @param runnable the runnable task
     */
    public void addTask(Runnable runnable) {
        mThreadExecutor.execute(runnable);
    }

}
