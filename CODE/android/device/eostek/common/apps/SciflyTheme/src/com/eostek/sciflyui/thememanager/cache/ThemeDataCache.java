
package com.eostek.sciflyui.thememanager.cache;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
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

import com.eostek.sciflyui.thememanager.util.BitmapUtils;
import com.eostek.sciflyui.thememanager.util.Constant;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.utils.L;

/**
 * ThemeDataCache.
 */
public class ThemeDataCache {

    /**
     * TAG.
     */
    public static final String TAG = "ThemeDataCache";

    private static ImageLoader mImageLoader = null;

    private volatile static ThemeDataCache mMsgLoader;

    private volatile static DataCacheManager manager;

    private Context mContext;

    private ThemeDataCache(Context context) {
        this.mContext = context;
        // init(mContext);
        initDataCache(context);
    }

    private void initDataCache(Context context) {
        manager = getDataCacheManager();
    }

    /**
     * @param mContext mContext
     * @return ThemeDataCache
     */
    public static ThemeDataCache getCacheLoader(Context mContext) {

        if (mMsgLoader == null) {
            synchronized (ThemeDataCache.class) {
                mMsgLoader = new ThemeDataCache(mContext);
            }
        }

        return mMsgLoader;
    }

    // private static void init(Context mContext) {
    //
    // ImageLoaderConfiguration config = new
    // ImageLoaderConfiguration.Builder(mContext)
    // .threadPriority(Thread.MAX_PRIORITY).denyCacheImageMultipleSizesInMemory()
    // .diskCacheFileNameGenerator(new
    // Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO)
    // /* .writeDebugLogs() */// Remove for release app
    // .build();
    // ImageLoader.getInstance().init(config);
    //
    // mImageLoader = ImageLoader.getInstance();
    // }

    /**
     * @param url url
     * @param imageView imageView
     * @param defaultResId defaultResId
     * @param failResId failResId
     * @param loadingResId loadingResId
     */
    public void loadImageSync(final String url, final ImageView imageView, int defaultResId, int failResId,
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
     * @return DataCacheManager
     */
    public DataCacheManager getDataCacheManager() {

        if (manager == null) {
            synchronized (ThemeDataCache.class) {
                manager = DataCacheManager.getInstance();
                File cachePath = new File(Constant.CACHE_PATH);

                if (!cachePath.exists()) {
                    Log.i(TAG, "create path =" + Constant.CACHE_PATH);
                    cachePath.mkdirs();
                }

                DataCacheConfiguration config = new DataCacheConfiguration.Builder()
                        .maxDiskCacheSize(Constant.TOTAL_CACHE_SIZE).diskCacheSize(Constant.SINGLE_CACHE_SIZE)
                        .diskCacheDir(cachePath).threadPriority(Thread.MAX_PRIORITY).build();
                manager.init(mContext, config);
                L.writeDebugLogs(true);
            }
        }
        return manager;
    }

    /**
     * @param type type
     * @param uri uri
     * @param map map
     * @param listener listener
     * @param progressListener progressListener
     */
    public void loadCache(int type, String uri, Map<String, String> map, DataCacheListener listener,
            DataCacheProgressListener progressListener) {
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(false).needCheck(false)
                .cacheOnDisk(true).extraForDownloader(map).build();
        // manager.loadCache(type, uri, listener, progressListener);
        manager.loadCache(type, uri, null, options, listener, progressListener);
    }

    /**
     * @param url url
     * @param imageView imageView
     * @param defaultResId defaultResId
     * @param failResId failResId
     * @param loadingResId loadingResId
     * @param targetImgPath targetImgPath
     */
    public void loadImage(String url, ImageView imageView, int defaultResId, int failResId, int loadingResId,
            final String targetImgPath) {
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
                if (dataObject instanceof Bitmap) {
                    Log.i(TAG, "onLoadingComplete targetImgPath = " + targetImgPath);
                    if (targetImgPath != null) {
                        BitmapUtils.saveBitmap((Bitmap) dataObject, targetImgPath);
                    }
                }
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
     * @param url url
     * @param imageView imageView
     * @param defaultResId defaultResId
     * @param failResId failResId
     * @param loadingResId loadingResId
     */
    public void loadImage(String url, ImageView imageView, int defaultResId, int failResId, int loadingResId) {
        loadImage(url, imageView, defaultResId, failResId, loadingResId, null);
    }

    /**
     * @param urls urls
     */
    public void clearCache(Map<String, String> urls) {

        Iterator<String> iter = urls.keySet().iterator();

        String key;
        String value;

        while (iter.hasNext()) {

            key = iter.next();

            value = urls.get(key);

            manager.clearDiskCache(key, value);
            manager.clearMemoryCache(key);
        }

    }

}
