package scifly.datacache;

import java.io.IOException;
import java.util.HashMap;

import android.content.Context;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiscCache;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LimitedAgeMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.utils.L;

public class DataCacheManager{
    public static final int DISK_CACHE_POLICY_UNLIMITED      = 0;
    public static final int DISK_CACHE_POLICY_LIMITED_AGE    = 1;
    public static final int DISK_CACHE_POLICY_LRU            = 2;

    public static final int MEMORY_CACHE_POLICY_FIFO         = 3;
    public static final int MEMORY_CACHE_POLICY_FUZZY_KEY    = 4;
    public static final int MEMORY_CACHE_POLICY_LARGEST      = 5;
    public static final int MEMORY_CACHE_POLICY_LIMITED_AGE  = 6;
    public static final int MEMORY_CACHE_POLICY_LRU          = 7;
    public static final int MEMORY_CACHE_POLICY_LFU          = 8;
    public static final int MEMORY_CACHE_POLICY_WEAK         = 9;

    public static final int DATA_CACHE_TYPE_IMAGE            = 10;
    public static final int DATA_CACHE_TYPE_TXT              = 11;
    public static final int DATA_CACHE_TYPE_FILE             = 12;

    private static final int DEFAULT_LIMITED_AGE = 300;    //5 mins

    public static final String EXTRA_KEY_POSTFIX = "postfix";

    public static final String EXTRA_KEY_POST = "post";

    public ImageLoader imageLoader;

    private volatile static DataCacheManager instance;

    public static DataCacheManager getInstance() {
        if (instance == null) {
            synchronized (DataCacheManager.class) {
                if (instance == null) {
                    instance = new DataCacheManager();
                }
            }
        }
        return instance;
    }

    protected DataCacheManager() {
        imageLoader = ImageLoader.getInstance();
    }

    private DiskCache getDiskCache(DataCacheConfiguration configuration) {
        switch(configuration.diskCachePolicy) {
            case DataCacheManager.DISK_CACHE_POLICY_UNLIMITED:
                return new UnlimitedDiscCache(configuration.diskCacheDir);
            case DataCacheManager.DISK_CACHE_POLICY_LIMITED_AGE:
                return new LimitedAgeDiscCache(configuration.diskCacheDir,
                        configuration.expireAge,
                        configuration.diskCacheSize);
            case DataCacheManager.DISK_CACHE_POLICY_LRU:
                try {
                    return new LruDiscCache(configuration.diskCacheDir,
                            null,
                            new HashCodeFileNameGenerator(),
                            configuration.maxDiskCacheSize,
                            0,
                            configuration.diskCacheSize);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            default:
        }
        return null;
    }

    private MemoryCache getMemoryCache(DataCacheConfiguration configuration) {
        switch(configuration.memoryCachePolicy) {
            case DataCacheManager.MEMORY_CACHE_POLICY_FIFO:
                return new FIFOLimitedMemoryCache(configuration.maxMemoryCacheSize);
            case DataCacheManager.MEMORY_CACHE_POLICY_FUZZY_KEY:
                break;
            case DataCacheManager.MEMORY_CACHE_POLICY_LARGEST:
                return new LargestLimitedMemoryCache(configuration.maxMemoryCacheSize);
            case DataCacheManager.MEMORY_CACHE_POLICY_LIMITED_AGE:
                return new LimitedAgeMemoryCache(
                        new LruMemoryCache(configuration.maxMemoryCacheSize),
                        configuration.expireAge,
                        configuration.memoryCacheSize);
            case DataCacheManager.MEMORY_CACHE_POLICY_LRU:
                return new LruMemoryCache(configuration.maxMemoryCacheSize);
            case DataCacheManager.MEMORY_CACHE_POLICY_LFU:
                return new UsingFreqLimitedMemoryCache(
                        configuration.maxMemoryCacheSize,
                        configuration.memoryCacheSize);
            case DataCacheManager.MEMORY_CACHE_POLICY_WEAK:
                return new WeakMemoryCache();
                default:
        }
        return null;
    }

    public synchronized void init(Context context, DataCacheConfiguration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("ERROR_INIT_CONFIG_WITH_NULL");
        }
        if (!imageLoader.isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                    .diskCache(getDiskCache(configuration))
                    .memoryCache(getMemoryCache(configuration))
                    .threadPoolSize(configuration.threadPoolSize)
                    .threadPriority(configuration.threadPriority)
                    .build();
            imageLoader.init(config);
        } else {
            L.w("WARNING_RE_INIT_CONFIG");
        }
    }

    public boolean isInited() {
        return imageLoader.isInited();
    }

    public void displayCache(String uri, ImageView imageView, DisplayImageOptions options,
            DataCacheListener listener, ImageLoadingProgressListener progressListener) {
        if (isInited()) {
            imageLoader.displayImage(uri, imageView, options, listener, progressListener);
        }
    }

    public void loadCache(int type, String uri, DataCacheListener listener) {
        loadCache(type, uri, listener, null);
    }

    public void loadCache(int type, String uri, DataCacheListener listener, DataCacheProgressListener progressListener) {
        loadCache(type, uri, null, null, listener, progressListener);
    }

    public void loadCache(int type, String uri, ImageSize targetImageSize, DisplayImageOptions options,
            ImageLoadingListener listener, ImageLoadingProgressListener progressListener) {
        if (options == null) {
            options = new DisplayImageOptions.Builder()
                    .cloneFrom(DisplayImageOptions.createSimple())
                    .cacheInMemory(type != DataCacheManager.DATA_CACHE_TYPE_FILE)
                    .cacheOnDisk(true)
                    .build();
        }
        if (isInited()) {
            imageLoader.loadImage(type, uri, targetImageSize, options, listener, progressListener);
        }
    }

    public Object loadCacheSync(int type, String uri) {
        if (isInited()) {
            return imageLoader.loadImageSync(type, uri);
        }
        return null;
    }

    public Object loadCacheSync(int type, String uri, ImageSize targetImageSize, DisplayImageOptions options) {
        if (isInited()) {
            return imageLoader.loadImageSync(type, uri, targetImageSize, options);
        }
        return null;
    }

    public void clearMemoryCache() {
        if (isInited()) {
            imageLoader.clearMemoryCache();
        }
    }

    public Object clearMemoryCache(String uri) {
        if (isInited()) {
            return imageLoader.clearMemoryCache(uri);
        }
        return null;
    }

    public void clearDiskCache() {
        if (isInited()) {
            imageLoader.clearDiskCache();
        }
    }

    public boolean clearDiskCache(String uri) {
        return clearDiskCache(uri, null);
    }

    public boolean clearDiskCache(String uri, String postfix) {
        if (isInited()) {
            return imageLoader.clearDiskCache(uri, postfix);
        }
        return false;
    }

    public void cancelTask(ImageView imageView) {
        if (isInited()) {
            imageLoader.cancelDisplayTask(imageView);
        }
    }

    public void cancelTask(String uri) {
        if (isInited()) {
            imageLoader.cancelDisplayTask(uri);
        }
    }

    public void pause() {
        if (isInited()) {
            imageLoader.pause();
        }
    }

    public void resume() {
        if (isInited()) {
            imageLoader.resume();
        }
    }

    public void stop() {
        if (isInited()) {
            imageLoader.stop();
        }
    }

    public void desdroy() {
        if (isInited()) {
            imageLoader.destroy();
        }
    }

    public static String getPostfix(final DisplayImageOptions options) {
        Object object = options.getExtraForDownloader();
        if (object != null && object instanceof HashMap<?, ?>) {
            HashMap<String, String> map = (HashMap<String, String>) object;
            String postfix = map.get(DataCacheManager.EXTRA_KEY_POSTFIX);
            if (postfix != null && !postfix.isEmpty() && postfix.startsWith(".")) {
                return postfix;
            }
        }
        return null;
    }
}