
package com.google.tv.eoslauncher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.tv.eoslauncher.util.GoogleAnalyticsUtil;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class HomeApplication extends Application {

    private final static String TAG = HomeApplication.class.getSimpleName();

    private static HomeApplication instance;

    private DisplayImageOptions options;

    private ImageLoader mImageLoader;

    /**
     * the thread pool to check network state change
     */
    private ExecutorService networkThreadExecutor;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        initImageLoader(getApplicationContext());

        mImageLoader = ImageLoader.getInstance();

        networkThreadExecutor = Executors.newCachedThreadPool();

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
    public void addNewTask(Runnable runnable) {
        Log.i(TAG, "addNewTask");
        networkThreadExecutor.execute(runnable);
    }

    /**
     * init the UIL Image Loader
     * 
     * @param context The context object
     */
    public void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY).denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb // Mb
                /* .writeDebugLogs() */// Remove for release app
                .build();
        ImageLoader.getInstance().init(config);

        options = new DisplayImageOptions.Builder().cacheInMemory(false).cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
    }

    /**
     * download image from the url ,and set the image as backgroud for the
     * targetView
     * 
     * @param url The url to download image
     * @param targetView The target view to the download image as backgroud
     */
    public void displayImage(String url, DisplayImageOptions options, final ImageView targetView) {
        Log.v(TAG, "loadImage url = " + url);
        mImageLoader.displayImage(url, targetView, options, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingCancelled(String requestUri, View view) {
                super.onLoadingCancelled(requestUri, view);
            }

            @Override
            public void onLoadingComplete(String requestUri, View view, Object dataObject) {
                super.onLoadingComplete(requestUri, view, dataObject);
                Log.v(TAG, "onLoadingComplete ");
                if (dataObject != null) {
                    Bitmap bitmap = (Bitmap) dataObject;
                    targetView.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onLoadingFailed(String requestUri, View view, FailReason failReason) {
                super.onLoadingFailed(requestUri, view, failReason);
                Log.v(TAG, "onLoadingFailed " + failReason.getType());
            }

            @Override
            public void onLoadingStarted(String requestUri, View view) {
                super.onLoadingStarted(requestUri, view);
                Log.v(TAG, "onLoadingStarted ");
            }

        });
    }

    /**
     * 
     * @param url
     * @param listener
     */
    public void loadImage(String url, ImageLoadingListener listener) {
        mImageLoader.loadImage(url, options, listener);
    }

    public void loadImage(String url, final ImageView imageButton, ImageSize imageSize) {
        mImageLoader.loadImage(url, imageSize, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingCancelled(String requestUri, View view) {
                super.onLoadingCancelled(requestUri, view);
            }

            @Override
            public void onLoadingComplete(String requestUri, View view, Object dataObject) {
                super.onLoadingComplete(requestUri, view, dataObject);
                if (dataObject != null) {
                    Bitmap bitmap = (Bitmap) dataObject;
                    imageButton.setImageBitmap(bitmap);
                    Log.v(TAG, "onLoadingComplete ");
                }
            }

            @Override
            public void onLoadingFailed(String requestUri, View view, FailReason failReason) {
                super.onLoadingFailed(requestUri, view, failReason);
                Log.v(TAG, "onLoadingFailed " + failReason.getType());
            }

            @Override
            public void onLoadingStarted(String requestUri, View view) {
                super.onLoadingStarted(requestUri, view);
                Log.v(TAG, "onLoadingStarted ");
            }
        });
    }

}
