
package com.eostek.sciflyui.thememanager.util;

import android.os.Environment;

/**
 * constants.
 */
public class Constant {
    /**
     * thumbnail width.
     */
    public static final int THUMBNAIL_WIDTH = 400;

    /**
     * thumbnail height.
     */
    public static final int THUMBNAIL_HEIGHT = 225;

    /**
     * where description.xml saved.
     */
    public static final String LOCAL_THEME_DESC = "/data/eostek/description.xml";

    /**
     * cache path.
     */
    public static final String PREFIX = "Android/data/com.eostek.sciflyui.thememanager/cache/";

    /**
     * CACHE_PATH.
     * scifly.storage.Sot
     */
    public static final String CACHE_PATH = Environment.getExternalStoragePublicDirectory(PREFIX).getAbsolutePath();

    /**
     * DEFAULT_PATH.
     */
    public static final String SYSTEM_DEFAULT_PATH = "/system/media/theme/";

    /**
     * the path where thumbnail saved.
     */
    public static final String IMAGE_CACHE = CACHE_PATH + "/icon/";

    /**
     * 200M.
     */
    public static final int TOTAL_CACHE_SIZE = 200;

    /**
     * 20M.
     */
    public static final int SINGLE_CACHE_SIZE = 20;

    /**
     * time out.
     */
    public static final int INTERNET_TIMEOUT = 20;

    /**
     * total size.
     */
    public static final int RESOURCE_TOTAL_SIZE = 100;

}
