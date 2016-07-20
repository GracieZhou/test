
package com.eostek.wasuwidgethost;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * projectName：WasuWidgetHost.
 * moduleName： WasuApplication.java
 * 
 */
public class WasuApplication extends Application {

    private static WasuApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initImageLoader(this);
    }

    public static WasuApplication getInstance() {
        return instance;
    }

    /**
     * This configuration tuning is custom. You can tune every option, you may
     * tune some of them, or you can create default configuration by
     * ImageLoaderConfiguration.createDefault(this); method.
     * 
     * @param context
     */
    public static void initImageLoader(Context context) {

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY).denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCacheSize(50 * 1024 * 1024).build();
        ImageLoader.getInstance().init(config);
    }

}
