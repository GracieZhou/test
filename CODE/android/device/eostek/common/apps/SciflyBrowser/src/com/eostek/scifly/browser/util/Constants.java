
package com.eostek.scifly.browser.util;

import android.os.Environment;

public class Constants {

    public static final int POSITION_HOME = 0;

    public static final int POSITION_COLLECT = 1;

    public static final int POSITION_SETTOOL = 2;

    public static final int SHOW_HISTORY = 3;

    public static final int MSG_GET_UPDATE_DATA = 4;

    public static final int MSG_GET_LOCAL_DATA = 5;

    public static final int MSG_UPDATE_UI = 6;

    public static final int MSG_UPDATE_DATA_DELAY = 1000;

    /** Constants for total cache size. */
    public static final int TOTAL_CACHE_SIZE = 200;

    /** Constants for single cache size. */
    public static final int SINGLE_CACHE_SIZE = 20;

    /** Constant for cache file path. */
    public static final String PREFIX = "Android/data/com.eostek.scifly.browser/cache/";

    /** Constants for cache file absolute path. */
    public static final String CACHE_PATH = Environment.getExternalStoragePublicDirectory(PREFIX).getAbsolutePath();

    public static final String SUGGEST_CONFIG_CACHE_PATH = CACHE_PATH + "/config";

    public static final String QR_CODE_NAME = "qrcode.png";

//    public static final String SERVICE_URL = "http://112.124.28.140:8081/hms/interface/clientService.jsp";
    public static final String SERVICE_HOST = "112.124.28.140:8081";

    public static final String SERVICE_URL = "http://scilfyinter.88popo.com:8300/interface/clientService.jsp";
}
