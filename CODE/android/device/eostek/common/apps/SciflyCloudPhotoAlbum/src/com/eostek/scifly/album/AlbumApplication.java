
package com.eostek.scifly.album;

import java.io.File;

import android.app.Application;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 * @ClassName: PhotoAlbumApplication.
 * @Description:PhotoAlbumApplication.
 * @author: lucky.li.
 * @date: Dec 3, 2015 5:20:00 PM.
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved.
 */
public class AlbumApplication extends Application {
    private static AlbumApplication instance;

    private static DisplayImageOptions mOptions;

    public AlbumApplication() {
    }

    public static AlbumApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoadConfig();
        instance = this;
    }

    public DisplayImageOptions getDisplayImageOptions() {
        return mOptions;
    }

    private void initImageLoadConfig() {
        File cacheDir = StorageUtils.getOwnCacheDirectory(this, "imageloader/Cache");
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
}
