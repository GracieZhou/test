
package com.heran.launcher2.util;

public class Constants {

    public static String serverUrl = "http://59.125.190.110/interface/clientService.jsp";

    public static String serverUrl_app = "http://adv.heran.babao.com/interface/clientService.jsp";

    public static String ADserverUrl = "http://test.jowinwin.com/hertv2msd/ad_test.php";

    public static String serverUrl_home = "http://www.jowinwin.com/hertv2msd/ad_test.php";

    public static String serviceUrlAppStore = "http://www.babao.com/dl/test/appstore.json";

    public static String NEWS_URL = "http://ws.chinatimes.com/WS/CtitvWebService.asmx/GetNCCategoryList";

    public static String NEWS_ARTICLE_URL = "http://ws.chinatimes.com/WS/CtitvWebService.asmx/GetNCNewsbyCtitv?CategoryID=";

    public static String TVPLAY_PKG = "com.eostek.tv.player";

    // 存放廣告影片網址
    public static final String JSON_ADDRESS = "http://www.test.jowinwin.com/hertv2msd/ad_test.php?position=startad&cust_type=18";

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

    public final static int FINISH_BOOT_AD = 0x18;

    public final static int UPDATE_NEWS_FLAG = 0x19;

    public final static int UPDATE_AD_FLAG = 0x20;

    public final static int SHOW_NEWVIEWS = 0x21;

    public final static int TIME_UPDATE = 0x22;

    public final static int TIME_NONET = 0x23;
    
    public final static int UPDATE_APP_INFO = 0x24;
    
    public final static int BOOTAD_IFFINISH = 0x25;

    public final static int PIFRAGMENTCHANGE = 0x90;

    /** AppStore **/
    public final static int APPUPDATE = 0x09;

    public final static int ALLAPPUPDATE = 0x11;

    public final static int NEWSUPDATE = 0x13;

    public final static int WEARHER_TODAY = 0x14;

    public final static int WEARHER_WEEK = 0x15;

    public final static int WEARHER_City = 0x16;

    public final static int WEARHER_Life = 0x17;

    /** current view num **/
    public final static int HOMEVIEW = 0;

    public final static int APPVIEW = 1;

    public final static int LIVINGAREAVIEW = 2;

    public final static int ENTERTAINMENT1 = 21;

    public final static int ENTERTAINMENT2 = 22;

    public final static int ENTERTAINMENT3 = 23;

    // public final static int SHOPWEBVIEW = 3;

    public final static int NEWSMAINFRAGMENT = 4;

    public final static int WEATHERMAINFRAGMENT = 5;

    public final static int CHARGFRAGMENT = 6;

    public final static int MESSAGEFRAGMENT = 7;

    public final static int NEWSVIEW = 0;

    public final static int WEATHERVIEW = 1;

    public final static int STOCKVIEW = 2;

    public final static int SHOPPINGVIEW = 3;

    public final static int BILLBOARDVIEW = 4;

    public final static int ADVIEW = 5;

    public final static int MEMBERVIEW = 6;

    public final static int MESSAGEVIEW = 7;
	
    public final static int CITYSELECT_OPEN = 9;
	
    public final static int CITYSELECT_CLOSE = 10;

    public final static int WEATHER_TODAY = 0;

    public final static int WEATHER_WEEK = 1;

    public final static int WEATHER_LIFE = 2;

    public final static int WEATHER_CITY = 3;

    public final static int WEATHER_ONEMIN = 4;

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

    public final static int NETWORK_CONNECTION_TIMEOUT = 2 * 1000;

    public final static int NETWORK_READ_DATA_TIMEOUT = 2 * 1000;

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

    // public static String THEME_URL =
    // "http://www.babao.com/dl/test/themes.json";
    public static String THEME_URL = "http://eos.jowinwin.com/test/themes.json";

    /**
     * HelperActivity
     */
    public final static int DISSMISS_HELPER = 1;

    public final static int DISSMISS_TIME = 10 * 1000;

    public static boolean kok_device = false;

    public static boolean CheckPW18 = false;

    public static boolean CheckJowin = false;

    public static String IAgree = "";

    public static String PPasswd = "";

    public static String POldPasswd = "";

    public static boolean PLocked = false;

    public static boolean Preset = false;

    public static String SkipPandora = "";

    public static String date = "";

    public static boolean rec = false;

    public static boolean newsRequesetCodeIsOk = false;

    public static boolean weatherRequesetCodeIsOk = false;

    public final static String FIRST_POWER_ON = "mstar.frist.power.on";
	
   	public static  boolean BOOTAD_FINISH = true;
   	
   	public static String USERNAME = "Guest";

}
