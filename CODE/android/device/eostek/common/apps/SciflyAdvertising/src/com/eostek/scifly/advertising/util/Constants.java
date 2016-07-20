package com.eostek.scifly.advertising.util;

import android.os.Environment;

/**
 * Provide a static constant values.
 */
public class Constants {

    /** server url. */
//    public static final String SERVER_URL = "http://172.23.65.164:8080/test/server.jsp";
//    public static final String SERVER_URL = "http://172.23.65.52/appStore/interface/clientService.jsp";
    public static final String SERVER_URL = "http://tvosapp.babao.com/interface/clientService.jsp";
//    public static final String SERVER_URL = "http://adv.heran.babao.com/interface/clientService.jsp";

    /** Constant for cache file path. */
    public static final String PREFIX = "Android/data/com.eostek.scifly.advertising/cache/";

    /** Constants for cache file absolute path. */
    public static final String CACHE_PATH = Environment.getExternalStoragePublicDirectory(PREFIX).getAbsolutePath();

    /** Constants for total cache size. */
    public static final int TOTAL_CACHE_SIZE = 200;

    /** Constants for single cache size. */
    public static final int SINGLE_CACHE_SIZE = 20;
}
