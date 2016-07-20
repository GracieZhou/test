
package com.heran.launcher.util;

public class Constants {

    public static final String serverUrl = "http://adv.heran.babao.com/interface/clientService.jsp";

    public static String serviceUrlAppStore = "http://www.babao.com/dl/test/appstore.json";

    public final static String FRIST_POWER_ON = "mstar.frist.power.on";

    public static final String FRIST_POWER_ON_STATUS_NONE = "0";

    public static final String defaultURL = "file:///android_asset/error.html";

    /** HomeActivty */
    public final static int TOUPDATETIME = 0x01;

    public final static int TODISMISSTIP = 0x02;

    public final static int OSDMESSAGE = 0x03;

    public final static int ADUPDATE = 0x04;

    public final static int APPADUPDATE = 0x05;

    public final static int ADAPPSTOREUPDATE = 0x06;

    public final static int SYSTEMICON = 0x07;

    public final static int OSDUPDATE = 0x08;

    public final static int UPDATE_AD_FLAG = 0x14;

    public final static int UPDATE_ADTEXT_FLAG = 0x15;

    public final static int UPDATE_APP_STORE = 0x16;

    public final static int UPDATE_APP_INFO = 0x17;

    public final static int GET_CURRENT_ACTIVITY = 0x18;

    public static final int FINISH_BOOT_AD = 0x19;

    public final static int DELAY_TIME = 1500;

    /** AppStore **/
    public final static int APPUPDATE = 0x09;

    public final static int ALLAPPUPDATE = 0x11;

    /** current view num **/
    public final static int HOMEVIEW = 0;

    public final static int APPVIEW = 1;

    public final static int MEDIAVIEW = 2;

    public final static int PANDORAVIEW = 3;

    public final static int SHOPWEBVIEW = 4;

    public final static int MSG_CITY_CHANGE = 100;

    public static final int UPDATE_WEATHER_VIEW = 101;

    public static final int NETWORK_CHANGED = 102;

    public final static int DELAYUPDATETIME = 10 * 1000;

    public final static int DELAYDISMISSTIP = 15 * 1000;

    public final static int DELAYDCHANGEPIC = 10 * 1000;

    public final static int DELAY_SYSTEM_ICON = 1 * 1000;

    /** HomeFragment **/
    public final static int DELAY_HOMEFRAGMENT_VIEWFLIPPER = 5 * 1000;

    public final static int INIT_HOMEFRAGMENT_VIEWFLIPPER = 0x07;

    public final static String POWER_STATE = "power_state";

    public final static String POWER_STATE_DEFAULT_VALUE = "0";

    public final static String POWER_STATE_CLICKED_VALUE = "1";

    public final static int UPDATE_AD_TEXT_TIME = 10 * 1000;

    public final static int SHOW_WEATHER = 0x10;

    /** Animation **/
    public final static int FADE_IN_FADE_OUT = 0;

    public final static int LETF_SHIFT = 1;

    public final static int SLIDE_UP = 2;

    public final static int ZOOM_IN_OUT = 3;

    public final static int ROTATION_RIGHT = 4;

    public final static int FLIP_HORIZONTAL = 5;

    public final static int ANIMATION_DEFAULT = FADE_IN_FADE_OUT;

    /** Network change udpate **/
    public final static int NETWORK_CHANGE_UPDATE = 0x11;

    public final static int CITY_CHANGE_UPDATE = 0x12;

    public final static int NETWORK_CONNECTION_TIMEOUT = 10 * 1000;

    public final static int NETWORK_READ_DATA_TIMEOUT = 4 * 1000;

    /** ScreenProtection **/
    public final static int DELAY_SHOW_SIGNAL_MSG = 0x13;

    public final static int NDELAY_SHOW_SIGNAL_TIME = 2 * 1000;

    public final static int SHOW_SIGNAL_TIPS = 0;

    public final static int DISMISS_SIGNAL_TIPS = 1;

    public final static int SHOW_LOCKED_TIPS = 2;

    /** theme path **/
    public final static String THEME_PATH = "/data/yp";

    public final static String THEME_ICON_PATH = "/data/yp/icon";

    public final static String THEME_DESCRIPTION_PATH = "/data/eostek/description.xml";

    public static String THEME_URL = "http://www.babao.com/dl/test/themes.json";

    /**
     * HelperActivity
     */
    public final static int DISSMISS_HELPER = 1;

    public final static int DISSMISS_TIME = 10 * 1000;

    public static final boolean kok_device = false;

    public final static String FIRST_POWER_ON = "mstar.frist.power.on";

    public static boolean CheckPW18 = false;

    public static boolean CheckJowin = false;

    public static String IAgree = "";

    public static String PPasswd = "";

    public static String POldPasswd = "";

    public static boolean PLocked = false;

    public static boolean Preset = false;

    public static String SkipPandora = "";

    public static String date = "";

}
