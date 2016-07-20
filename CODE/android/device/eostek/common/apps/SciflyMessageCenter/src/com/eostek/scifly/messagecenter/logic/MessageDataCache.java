
package com.eostek.scifly.messagecenter.logic;

import static com.eostek.scifly.messagecenter.util.Constants.CACHE_PATH;
import static com.eostek.scifly.messagecenter.util.Constants.SINGLE_CACHE_SIZE;
import static com.eostek.scifly.messagecenter.util.Constants.TOTAL_CACHE_SIZE;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import scifly.datacache.DataCacheConfiguration;
import scifly.datacache.DataCacheListener;
import scifly.datacache.DataCacheManager;
import scifly.datacache.DataCacheProgressListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.utils.L;

/**
 * The manager of MessageDataCache.
 */
public class MessageDataCache {

    private static final String TAG = "MessageImageLoader";

    private static ImageLoader mImageLoader = null;

    private static MessageDataCache mMsgLoader;

    private static DataCacheManager manager;

    private Context mContext;

    private MessageDataCache(Context context) {
        this.mContext = context;
        // init(mContext);
        initDataCache(context);
    }

    private void initDataCache(Context context) {
        manager = getDataCacheManager();
    }

    /**
     * get instance of MessageDataCache
     * 
     * @param mContext
     * @return
     */
    public static MessageDataCache getCacheLoader(Context mContext) {
        if (mMsgLoader == null) {
            mMsgLoader = new MessageDataCache(mContext);
        }
        return mMsgLoader;
    }

    private static void init(Context mContext) {

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
                .threadPriority(Thread.MAX_PRIORITY).denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO)
                /* .writeDebugLogs() */// Remove for release app
                .build();
        ImageLoader.getInstance().init(config);

        mImageLoader = ImageLoader.getInstance();
    }

    private void loadImageSync(final String url, final ImageView imageView, int defaultResId, int failResId,
            int loadingResId) {

        // Bitmap bitmap = (Bitmap)
        // manager.loadCacheSync(DataCacheManager.DATA_CACHE_TYPE_IMAGE, url);
        // final DisplayImageOptions options = new
        // DisplayImageOptions.Builder().resetViewBeforeLoading(false)
        // .cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.RGB_565)
        // .considerExifParams(true).build();
        // mImageLoader.loadImageSync(url, options);
        // if (bitmap != null) {
        // imageView.setImageBitmap(bitmap);
        // } else {
        // imageView.setImageResource(defaultResId);
        // }

    }

    /** release resource. */
    public void destory() {
        if (mImageLoader != null) {
            mImageLoader.destroy();
        }
    }

    /**
     * get instance of DataCacheManager.
     * 
     * @return
     */
//    public DataCacheManager getDataCacheManager() {
//        if (manager == null) {
//            manager = DataCacheManager.getInstance();
//            File cachePath = new File(CACHE_PATH);
//
//            if (!cachePath.exists()) {
//                Log.i(TAG, "create path =" + CACHE_PATH);
//                if (!cachePath.mkdirs()) {
//                    Log.i(TAG, "create path =" + CACHE_PATH + " failed...");
//                }
//            }
//
//            DataCacheConfiguration config = new DataCacheConfiguration.Builder().maxDiskCacheSize(TOTAL_CACHE_SIZE)
//                    .diskCacheSize(SINGLE_CACHE_SIZE).diskCacheDir(cachePath).threadPriority(Thread.MAX_PRIORITY)
//                    .build();
//            manager.init(mContext, config);
//            L.writeDebugLogs(true);
//        }
//        return manager;
//    }
    public DataCacheManager getDataCacheManager() {
        if (manager == null) {
            manager = DataCacheManager.getInstance();
            File cachePath = new File(CACHE_PATH);

            if (!cachePath.exists()) {
                Log.i(TAG, "create path =" + CACHE_PATH);
                if (!cachePath.mkdirs()) {
                    Log.i(TAG, "create path =" + CACHE_PATH + " failed...");
                }
            }

            if (cachePath.exists()) {
                DataCacheConfiguration config = new DataCacheConfiguration.Builder().maxDiskCacheSize(TOTAL_CACHE_SIZE)
                        .diskCacheSize(SINGLE_CACHE_SIZE).diskCacheDir(cachePath).threadPriority(Thread.MAX_PRIORITY)
                        .build();
                manager.init(mContext, config);
                L.writeDebugLogs(true);
            }

        }
        return manager;
    }

    /**
     * load cache files.
     * 
     * @param type
     * @param uri
     * @param map
     * @param listener
     * @param progressListener
     */
    public void loadCache(int type, String uri, Map<String, String> map, DataCacheListener listener,
            DataCacheProgressListener progressListener) {
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(false).needCheck(false)
                .cacheOnDisk(true).extraForDownloader(map).build();
        // manager.loadCache(type, uri, listener, progressListener);
        manager.loadCache(type, uri, null, options, listener, progressListener);
    }

    /**
     * load image for data cache.
     * 
     * @param url
     * @param imageView
     * @param defaultResId
     * @param failResId
     * @param loadingResId
     */
    public void loadImage(String url, ImageView imageView, int defaultResId, int failResId, int loadingResId) {

        Map<String, String> map = new HashMap<String, String>();
        map.put(DataCacheManager.EXTRA_KEY_POSTFIX, ".png");

        DisplayImageOptions options = new DisplayImageOptions.Builder().needCheck(false).extraForDownloader(map)
                .showImageForEmptyUri(defaultResId).showImageOnFail(failResId).showImageOnLoading(loadingResId)
                .resetViewBeforeLoading(false).cacheInMemory(true).cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(0)).build();
        manager.displayCache(url, imageView, options, new DataCacheListener() {
            @Override
            public void onLoadingStarted(String requestUri, View view) {
                Log.i(TAG, "onLoadingStarted");
            }

            @Override
            public void onLoadingFailed(String requestUri, View view, FailReason failReason) {
                Log.i(TAG, "onLoadingFailed " + failReason.getCause());
            }

            @Override
            public void onLoadingComplete(String requestUri, View view, Object dataObject) {
                Log.i(TAG, "onLoadingComplete requestUri = " + requestUri);
            }

            @Override
            public void onLoadingCancelled(String requestUri, View view) {
                Log.i(TAG, "onLoadingCancelled");
            }

            @Override
            public void onCheckingComplete(String requestUri, View view, Object dataObject) {
                Log.i(TAG, "onCheckingComplete");
            }

        }, null);
    }

    /**
     * clear cache files from disk.
     * 
     * @param urls
     */
    public void clearCache(Map<String, String> urls) {
        for (Map.Entry<String, String> entry : urls.entrySet()) {
            manager.clearDiskCache(entry.getKey(), entry.getValue());
            manager.clearMemoryCache(entry.getKey());
        }

    }

}
