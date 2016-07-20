
package com.eostek.tv.launcher.util;

/*
 * projectName： TVLauncher
 * moduleName： LConstants.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-7-17 下午7:59:53
 * @Copyright © 2014 Eos Inc.
 */

public final class LConstants {

    private LConstants() {
    }

    public static final boolean DEBUG = true;

    public static final int NETWORK_CONNECTION_TIMEOUT = 2 * 1000;

    public static final int NETWORK_READ_DATA_TIMEOUT = 2 * 1000;

    public static final int GET_BLUR_DELAY_TIME = 2 * 1000;

    public static final int UPDATE_METRO_DATA = 0x010;

    public static final int UPDATE_DATA_FAIL = 0x020;

    public static final int FINISH_UPDATE_METRO_DATA = 0x011;

    public static final int UPDATE_METRO_DELAY_TIME = 5 * 1000;

    /**************** NetworkReceiver Message ***************************/
    public static final int MSG_GET_WEATHER = 0x101;

    public static final int MSG_SHOW_WEATHER = 0x102;

    public static final int MSG_GET_UPDATE_DATA = 0x103;

    public static final int MSG_UPDATE_IMAGE_RESOURCE = 0x104;

    public static final int MSG_DISMISS_DIALOG = 0x105;

    public static final int MSG_GET_BLUR_BITMAP = 0x106;

    public static final int MSG_SHOW_TOAST = 0x107;

    public static final int MSG_SHOW_NETWOEK_TOAST = 0x108;

    public static final int MSG_GET_UPDATE_VERSION = 0x109;

    public static final int MSG_ALPHA_CHANG = 0x110;

    public static final String SERVER_URL = "http://scilfyinter.88popo.com:8300/interface/clientService.jsp";

    public static final String UPGRADE_SERVER_URL = "http://tvosapp.babao.com/interface/clientService.jsp";

    /**************** PackageReceiver Message ***************************/
    public static final int PACKAGE_ADDED = 0;

    public static final int PACKAGE_REMOVED = 1;

    public static final int PACKAGE_REPLACEED = 2;

    /** MetroInfo item tyep **/
    public static final int METRO_ITEM_APK = 1;

    public static final int METRO_ITEM_WEB_URL = 2;

    public static final int METRO_ITEM_WIDGET = 3;

    public static final int METRO_ITEM_SERVICE = 4;

    public static final int METRO_ITEM_OTHERS = 5;

    public static final String PLATFORM_TV = "tv";

    public static final String PLATFORM_BOX = "box";

    public static final String PLATFORM_DANGLE = "dongle";
    
    public static final String PLATFORM_BENQ_828 = "muji";

    public static final int APPWIDGETHOST_ID = 1023;

    public static final int FOCUS_TYPE_STATIC = 0;

    public static final int FOCUS_TYPE_DYNAMIC = 1;

    public static final float DEFAULT_SCREEN_WIDTH = 1920f;

    /** Network change udpate **/
    public static final int NETWORK_CHANGE_UPDATE = 0x11;

    /** local change udpate **/
    public static final int LOCAL_CHANGE = 0x12;

    /** wall paper change **/
    public static final int WALL_PAPER_CHANGE = 0x13;

    /** general setting **/
    public static final int SHARPNESS_ITEM = 0;

    public static final int ASPECT_RATIO_ITEM = 1;

    public static final int SKIP_START_END_ITEM = 2;

    public static final int REFRESH_HOME_ITEM = 3;

    public static final int CHECK_UPDATE_ITEM = 4;

    public static final int RESET_ITEM = 5;

    public static final int ITEM_BUTTON = 0;

    public static final int ITEM_DIGITAL = 1;

    public static final int ITEM_ENUM = 2;

    public static final String SCIFLY_VIDEO = "com.eostek.scifly.video";

    public static final String TV_PLAYER_PKG = "com.eostek.tv";

    public static final String TV_PLAYER_CLS = "com.eostek.tv.PlayerActivity";

    public static final String TV_PLAYER_ACTION = "com.eostek.action.tvplayer";

    public static final String MTA_CLS = "com.utsmta.app.MainActivity";

    /** from version code 24,add different language support **/
    public static final int NEW_VERSION_CODE = 24;

    public static final String DEFAULT_ETAG = "If-None-Match";
    
    public static final int GET_WEATHER_INFO = 100;

    public static final int UPDATE_WEATHER_VIEW = 101;

    public static final int MSG_CITY_CHANGE = 102;

    public static final int HIDE_WEATHER_VIEW = 103;

    public static final int DOWNLOAD = 104;

    public static final int DOWNLOAD_FINISH = 105;

    public static final int INSTALL_SUCCESS = 106;

    public static final int INSTALL_FAIL = 107;

    public static final int DOWNLOAD_NO_NETWORK = 108;

    public static final int UPDATE_WEATHER_TIME_VIEW = 109;

    public static final int DOWNLOAD_FINISH_MSG_DELAY = 1000;

    /**************** alpha ******************/
    public static final float ALPHA_LOW = 0.1f;

    public static final float ALPHA_HIGH = 1f;

    public static final String PREFS_KEY_ISLOCALECHANGE = "isLocaleChange";

    public static final String PROP_SCIFLY_PLATFORM = "ro.scifly.platform";

    public static final String SCIFLY_PROVIDER_GLOBAL = "content://com.eostek.scifly.provider/global";

    public static final String ACTION_QUICKLAUNCH = "eostek.launcheraction.intent.action.QUICKLAUNCH";

    public static final String ACTION_TV_INPUT_BUTTON = "com.mstar.android.intent.action.TV_INPUT_BUTTON";

    public static final String ACTION_HISTORY = "eostek.launcheraction.intent.action.HISTORY";

    public static final String ACTION_UPDATE_WIDGET = "com.eostek.wasuwidgethost.updatewidget";

    public static final String PREFS_KEY_CACHE = "com.eostek.wasuwidgethost.CacheHandleProvider";

    public static final String PREFS_KEY_WEATHER = "com.eostek.wasuwidgethost.WeatherWidgetProvider";

    public static final String EXTRA_KEY_CACHE = "cacheId";

    public static final String EXTRA_KEY_WEATHER = "weatherId";

    public static final String PREFS_KEY_SETMEUP = "setmeup";

    public static final String PKG_UTSMTA = "com.utsmta.app";

    public static final String CLS_UTSMTA = "com.utsmta.app.MainActivity";

    public static final String PKG_SETMEUP = "com.eostek.isynergy.setmeup";

    public static final String CLS_SETMEUP = "com.eostek.isynergy.setmeup.ui.SetmeupMainActivity";

    public static final String VERSION_CODE = "versionCode";

    public static final String EXTRA_KEY_NEWVERNAME = "newversionname";

    public static final String EXTRA_KEY_NEWVERFILESIZE = "newversionfilesize";

    public static final String EXTRA_KEY_NEWVERDESC = "newversiondesc";

    public static final String PREFS_WEATHER = "weatherPreference";

    public static final String PREFS_WEATHER_LOCATION = "location";

    public static final String PREFS_WEATHER_TOP = "top";

    public static final String PREFS_WEATHER_BOTTOM = "bottom";

    public static final int HUNDRED = 100;
    
    public final static String FEATURE_BENQ_828 = "EOS0MUJIBENQTV01";
}
