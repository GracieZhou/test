package com.eostek.scifly.devicemanager.data;

import android.content.Context;
import android.os.Environment;

import com.eostek.scifly.devicemanager.util.Constants;
import com.eostek.scifly.devicemanager.util.Debug;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.io.File;
import java.util.Map;

import scifly.datacache.DataCacheConfiguration;
import scifly.datacache.DataCacheListener;
import scifly.datacache.DataCacheManager;
import scifly.datacache.DataCacheProgressListener;

public class CacheManager {
    
    private static final String TAG = CacheManager.class.getSimpleName();
    
    private static CacheManager mCacheLoader;

    private static DataCacheManager manager;

    private Context mContext;
    
    private CacheManager(Context context) {
        this.mContext = context;
        initDataCache(context);
    }

    private void initDataCache(Context context) {
        manager = getDataCacheManager();
    }

    private DataCacheManager getDataCacheManager() {
        if (manager == null) {
            manager = DataCacheManager.getInstance();
            File cachePath = Environment.getExternalStoragePublicDirectory(Constants.DATACAHCE_DIRECTORY);

            if (!cachePath.exists()) {
                cachePath.mkdirs();
            }

            if (!manager.isInited()) {
                DataCacheConfiguration config = new DataCacheConfiguration.Builder()
                .diskCacheDir(cachePath)
                .diskCachePolicy(DataCacheManager.DISK_CACHE_POLICY_LRU)
                .maxDiskCacheSize(200)
                .diskCacheSize(10)
                .memoryCachePolicy(DataCacheManager.MEMORY_CACHE_POLICY_LFU)
                .maxMemoryCacheSize(16)
                .memoryCacheSize(10)
                .expireAge(60)
                .threadPoolSize(1)
                .threadPriority(Thread.MAX_PRIORITY)
                .build();

                manager.init(mContext, config);

                Debug.d(TAG, "DataCacheManager init");
            } else {
                Debug.d(TAG, "DataCacheManager is already inited");
            }

        }
        return manager;
    }
    
    public static CacheManager getCacheLoader(Context mContext) {
        if (mCacheLoader == null) {
            mCacheLoader = new CacheManager(mContext);
        }
        return mCacheLoader;
    }
    
    /**
     * load cache files.
     */
    public void loadTxtCache(String uri, Map<String, String> map, DataCacheListener listener, DataCacheProgressListener progressListener) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .needCheck(true)
            .cacheOnDisk(true)
            .extraForDownloader(map)
            .build();
        manager.loadCache(DataCacheManager.DATA_CACHE_TYPE_TXT, uri, null, options, listener, progressListener);
    }

    public void loadImageCache(String uri, DataCacheListener listener) {
        manager.loadCache(DataCacheManager.DATA_CACHE_TYPE_IMAGE, uri, listener, null);
    }
    
    public void clearCache() {
        if(manager != null ) {
            manager.clearMemoryCache();
            manager.clearDiskCache();
        }
    }
    
    public void stop() {
        manager.stop();
        manager.resume();
    }
}
