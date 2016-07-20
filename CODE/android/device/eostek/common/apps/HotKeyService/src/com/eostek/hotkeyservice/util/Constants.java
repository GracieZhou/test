
package com.eostek.hotkeyservice.util;

import java.util.List;

import scifly.view.KeyEventExtra;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.view.KeyEvent;

public class Constants {
    /**
     * item type.
     */
    public static final int ITEM_ENUM = 0;

    public static final int ITEM_DIGITAL = ITEM_ENUM + 1;

    public static final int ITEM_BUTTON = ITEM_ENUM + 2;

    /**
     * item title.for i500
     */
    public static final int PICTURE_MODE_FOR_I500 = 0;

    public static final int BRIGHTNESS_FOR_I500 = PICTURE_MODE_FOR_I500 + 1;

    public static final int CONTRAST_FOR_I500 = PICTURE_MODE_FOR_I500 + 2;

    public static final int COLOR_TEMPRATURE_FOR_I500 = PICTURE_MODE_FOR_I500 + 3;

    public static final int SOUND_MODE_FOR_I500 = PICTURE_MODE_FOR_I500 + 4;

    public static final int ASPECT_RATIO_FOR_I500 = PICTURE_MODE_FOR_I500 + 5;

    public static final int AUDIO_INPUT_SOURCE_FOR_I500 = PICTURE_MODE_FOR_I500 + 6;

    public static final int WALL_COLOR_FOR_I500 = PICTURE_MODE_FOR_I500 + 7;

    public static final int THREE_DIMENSION_FOR_I500 = PICTURE_MODE_FOR_I500 + 8;

    public static final int SETTINGS_FOR_I500 = PICTURE_MODE_FOR_I500 + 9;

    /**
     * for i300
     */

    public static final int PICTURE_MODE_FOR_I300 = 0;

    public static final int BRIGHTNESS_FOR_I300 = PICTURE_MODE_FOR_I300 + 1;

    public static final int CONTRAST_FOR_I300 = PICTURE_MODE_FOR_I300 + 2;

    public static final int COLOR_TEMPRATURE_FOR_I300 = PICTURE_MODE_FOR_I300 + 3;

    public static final int SOUND_MODE_FOR_I300 = PICTURE_MODE_FOR_I300 + 4;

    public static final int VOLUME_FOR_I300 = PICTURE_MODE_FOR_I300 + 5;

    public static final int MIC_VOLUME_FOR_I300 = PICTURE_MODE_FOR_I300 + 6;

    public static final int ASPECT_RATIO_FOR_I300 = PICTURE_MODE_FOR_I300 + 7;

    public static final int WALL_COLOR_FOR_I300 = PICTURE_MODE_FOR_I300 + 8;

    public static final int THREE_DIMENSION_FOR_I300 = PICTURE_MODE_FOR_I300 + 9;

    public static final int SETTINGS_FOR_I300 = PICTURE_MODE_FOR_I300 + 10;

    /**
     * picture mode.
     */
    public static final int PICTURE_MODE_BRIGHT = 0;

    public static final int PICTURE_MODE_VIVID = PICTURE_MODE_BRIGHT + 1;

    public static final int PICTURE_MODE_GAME = PICTURE_MODE_BRIGHT + 2;

    public static final int PICTURE_MODE_CINEMA = PICTURE_MODE_BRIGHT + 3;

    public static final int PICTURE_MODE_USER = PICTURE_MODE_BRIGHT + 4;

    /**
     * sound mode.
     */
    public static final int SOUND_MODE_MOVIE = 0;

    public static final int SOUND_MODE_MUSIC = SOUND_MODE_MOVIE + 1;

    public static final int SOUND_MODE_GAME = SOUND_MODE_MOVIE + 2;

    /**
     * aspect ratio.
     */
    public static final int ASPECT_RATIO_AUTO = 0;

    public static final int ASPECT_RATIO_REAL = ASPECT_RATIO_AUTO + 1;

    public static final int ASPECT_RATIO_4TO3 = ASPECT_RATIO_AUTO + 2;

    public static final int ASPECT_RATIO_16TO9 = ASPECT_RATIO_AUTO + 3;

    public static final int ASPECT_RATIO_ZOOM1 = ASPECT_RATIO_AUTO + 4;

    /**
     * wall color.
     */
    public static final int WALL_COLOR_OFF = 0;

    public static final int WALL_COLOR_LIGHT_YELLOW = WALL_COLOR_OFF + 1;

    public static final int WALL_COLOR_LIGHT_GREEN = WALL_COLOR_OFF + 2;

    public static final int WALL_COLOR_BLUE = WALL_COLOR_OFF + 3;

    public static final int WALL_COLOR_PINK = WALL_COLOR_OFF + 4;

    public static final int WALL_COLOR_BLACKGROUND = WALL_COLOR_OFF + 5;

    /**
     * handler parameter.
     */
    public static final int UPDATE_FOCUS_VIEW = 0;

    public static final int UPDATEALL = UPDATE_FOCUS_VIEW + 1;

    public static final int UPDATE_ITEM_ENUM = UPDATE_FOCUS_VIEW + 2;

    public static final int UPDATE_ITEM_DIGITAL = UPDATE_FOCUS_VIEW + 3;

    public static final int UPDATE_ITEM_BUTTON = UPDATE_FOCUS_VIEW + 4;

    public static final int UPDATE_LEFT_ROW = UPDATE_FOCUS_VIEW + 5;

    public static final int UPDATE_RIGHT_ROW = UPDATE_FOCUS_VIEW + 6;

    public static final int DELAY_HANDLE_500 = UPDATE_FOCUS_VIEW + 7;

    public static final int DELAY_HANDLE_ENUM_500 = UPDATE_FOCUS_VIEW + 8;

    public static final int DELAY_HANDLE_300 = UPDATE_FOCUS_VIEW + 9;

    public static final int DELAY_HANDLE_ENUM_300 = UPDATE_FOCUS_VIEW + 10;

    public static final int DELAY_HANDLE_OTHER_500 = UPDATE_FOCUS_VIEW + 11;

    public static final int DELAY_HANDLE_OTHER_300 = UPDATE_FOCUS_VIEW + 12;

    public static final int DELAY_TIME = 1;

    /**
     * Intent Extra===>VideoMode
     */
    public static final String EXTRA_VIDEOMODE = "menu";

    public static final String SHOW_ME_FROM_HIDE = "com.eostek.tv.SHOW.PICTUREMODE";

    public static final String START_PICTURE_MODE = "com.eostek.tv.PICTUREMODE";

    public static final String INDEX = "index";

    public static final String ACTION = "action";

    public static final String POSITION = "position";

    public static final String START_SETTING_MENU = "com.eostek.tv.SETTINGMENU";

    public static final String TV = "com.eostek.tv.player.PlayerActivity";

    public static final String MEDIA_PLAYER = "com.hrtvbic.usb.S6A918.videoplayer.VideoPlayerActivity";

    public static String getCurrentActivityName(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> forGroundActivity = activityManager.getRunningTasks(1);
        RunningTaskInfo currentActivity;
        currentActivity = forGroundActivity.get(0);
        String activityName = currentActivity.topActivity.getClassName();
        if (activityName == null) {
            return "";
        }
        return activityName;
    }

    public static final int DEFAULT_POSTION = -1;

    /**
     * menu item title.
     */
    public static final int LANGUAGE = 0;

    public static final int PROJECTOR = LANGUAGE + 1;

    public static final int AUTO_SEARCH = LANGUAGE + 2;

    public static final int TEST_PATTERN = LANGUAGE + 3;

    public static final int RESET = LANGUAGE + 4;

    public static final int AUTO_SLEEP = LANGUAGE + 5;

    public static final int AUTO_KEYSTONE = LANGUAGE + 6;

    public static final int KEYSTONE = LANGUAGE + 7;

    public static final int PHASE = LANGUAGE + 8;

    public static final int LED_MODE = LANGUAGE + 9;

    public static final int INFORMATION = LANGUAGE + 10;

    /**
     * language.
     */
    public static final int LANGUAGE_ENGLISH = 0;

    public static final int LANGUAGE_SIMPLE_CHINESE = LANGUAGE_ENGLISH + 1;

    public static final int LANGUAGE_TRADITIONAL_CHINESE = LANGUAGE_ENGLISH + 2;

    /**
     * projector position.
     */
    public static final int PROJECTOR_FRONT = 0;

    public static final int PROJECTOR_FRONT_CEILING = PROJECTOR_FRONT + 1;

    public static final int PROJECTOR_REAR = PROJECTOR_FRONT + 2;

    public static final int PROJECTOR_REAR_CEILING = PROJECTOR_FRONT + 3;

    /**
     * auto source search.
     */
    public static final int AUTO_SOURCE_OFF = 0;

    public static final int AUTO_SOURCE_ON = AUTO_SOURCE_OFF + 1;

    /**
     * test pattern.
     */
    public static final int TEST_PATTERN_OFF = 0;

    public static final int TEST_PATTERN_ON = TEST_PATTERN_OFF + 1;

    /**
     * auto sleep.
     */
    public static final int AUTO_POWER_OFF_DISABLE = 0;

    public static final int AUTO_POWER_OFF_5MIN = AUTO_POWER_OFF_DISABLE + 1;

    public static final int AUTO_POWER_OFF_15MIN = AUTO_POWER_OFF_DISABLE + 2;

    public static final int AUTO_POWER_OFF_20MIN = AUTO_POWER_OFF_DISABLE + 3;

    public static final String AUTO_SLEEP_FEILD = "auto_sleep";

    public static final String CALL_ON = "call_on";

    /**
     * auto keystone.
     */
    public static final int AUTO_KEYSTONE_OFF = 0;

    public static final int AUTO_KEYSTONE_ON = AUTO_KEYSTONE_OFF + 1;

    /**
     * led mode.
     */
    public static final int LED_MODE_NORMAL = 0;

    public static final int LED_MODE_ECONIMIC = LED_MODE_NORMAL + 1;

    /**
     * information item title.
     */
    public static final int SOURCE_NAME = 0;

    public static final int PICTURE_MODE = SOURCE_NAME + 1;

    public static final int RESOLUTION = SOURCE_NAME + 2;

    public static final int COLOR_SYSTEM = SOURCE_NAME + 3;

    public static final int EQUIVALENT_LAMP_HOUR = SOURCE_NAME + 4;

    public static final int RELEASE_NOTE = SOURCE_NAME + 5;

    public static final int BUILD_NUMBER = SOURCE_NAME + 6;

    /**
     * 3Dmode title.
     */
    public static final int OFF = 0;

    public static final int SIDE_BY_SIDE = OFF + 1;

    public static final int TOP_BOTTOM = OFF + 2;

    public static final int FRAME_PACKING = OFF + 3;

    public static final int FRAME_SEQUENTIAL = OFF + 4;

    public static final int DDD_SYNC_INVERT = OFF + 5;

    public static final int AUTO = OFF + 6;

    /**
     * update stone array ui .
     */
    public static final int UPDATE_STONE_UP = 0x003;

    public static final int UPDATE_STONE_DOWN = 0x004;

    public static final int UPDATE_KEYSTONE_VALUE = 0x005;

    public static final int DELAY_HANDLE_UP = 0x006;

    public static final int DELAY_HANDLE_DOWN = 0x007;

    public static final int DELAY_HANDLE_TIME = 250;

    public static final int DELAY_HANDLE_ENUM_TIME = 400;

    public static final int KEYSTONE_MAX_VALUE = 40;

    public static final int KEYSTONE_MIN_VALUE = -40;

    /**
     * FACTORYPAKAGE.
     */
    public static final String FACTORYPAKAGE = "mstar.factorymenu.ui";

    /**
     * FACTORYACTIVITY.
     */
    public static final String FACTORYACTIVITY = "mstar.tvsetting.factory.ui.designmenu.DesignMenuActivity";

    public static String SETTING_MENU_RESUME = "com.eostek.tv.SHOW.SETTINGMENU";

    /**
     * HDMI.
     */
    public static final int HDMI = 0;

    /**
     * PC.
     */
    public static final int PC = 1;

    /**
     * USB.
     */
    public static final int USB = 3;

    /**
     * USB_i500w
     */
    public static final int USB_i500w = 2;

    /**
     * USB_i300.
     */
    public static final int USB_i300 = 1;

    /**
     * MIRACAST.
     */
    public static final int MIRACAST = 2;

    /**
     * MIRACAST_i300.
     */
    public static final int MIRACAST_i300 = 2;

    /**
     * MIRACAST_i300.
     */
    public static final int SYSTEM = 3;

    /**
     * SLEEPTIME.
     */
    public static final int SLEEPTIME = 2000;

    /**
     * the default sourceType.
     */
    public static final int DEFAULTSOURCE = 0;

    /**
     * the delayTime of the task.
     */
    public static final long DELAYTIME = 8 * 1000;

    /**
     * the flag of the dismiss the SourceActivity.
     */
    public static final int DISSMISSMSG = 0x001;

    /**
     * the flag of the dismiss the CallOnActivity.
     */
    public static final int DISSMISSCALLONMSG = 0x002;

    /**
     * TVPAKAGE.
     */
    public static final String TVPAKAGE = "com.eostek.tv.player";

    /**
     * TVACTIVITY.
     */
    public static final String TVACTIVITY = "com.eostek.tv.player.PlayerActivity";

    /**
     * USBPAKAGE.
     */
    public static final String USBPAKAGE = "com.hrtvbic.usb.S6A918";

    /**
     * USBACTIVITY.
     */
    public static final String USBACTIVITY = "com.hrtvbic.usb.S6A918.MainActivity";

    /**
     * FACTORYACTIVITY.
     */

    public static final String HDMISTIRNG = "HDMI";

    public static final String PCSTRING = "PC";

    public static final String USBSTRING = "USB";

    public static final String MIRACASTSTRING = "Miracast";

    public static final String SOURCETYPESTRING = "sourceType";

    public static final String BENQ_I500 = "BenQ_i500";

    public static final String BENQ_I500w = "BenQ_i500w";

    public static final String BENQ_I300 = "BenQ_i300";

    /**
     * broadcast action name. OSDMENU include PICTUREMODE, SETTINGMENU.
     */

    public static final String SOURCE_ACTION = "com.eostek.tv.SOURCE";

    public static final String DISMISS_ACTION = "com.eostek.tv.DISMISS";

    public static final String AD_ACTION = "com.eostek.tv.BQAD";

    public static final int SOURCE_INDEX = 0;

    public static final int AD_INDEX = SOURCE_INDEX + 1;

    public static final String GOODKEYCODES = String.valueOf(KeyEvent.KEYCODE_DPAD_UP)
            + String.valueOf(KeyEvent.KEYCODE_DPAD_DOWN) + String.valueOf(KeyEvent.KEYCODE_DPAD_UP)
            + String.valueOf(KeyEvent.KEYCODE_DPAD_DOWN) + String.valueOf(KeyEvent.KEYCODE_DPAD_UP)
            + String.valueOf(KeyEvent.KEYCODE_DPAD_DOWN) + String.valueOf(KeyEvent.KEYCODE_DPAD_CENTER);

    public static final String GOODKEYCODES1 = String.valueOf(KeyEvent.KEYCODE_DPAD_UP)
            + String.valueOf(KeyEvent.KEYCODE_DPAD_DOWN) + String.valueOf(KeyEvent.KEYCODE_DPAD_UP)
            + String.valueOf(KeyEvent.KEYCODE_DPAD_DOWN) + String.valueOf(KeyEvent.KEYCODE_DPAD_UP)
            + String.valueOf(KeyEvent.KEYCODE_DPAD_DOWN) + String.valueOf(KeyEvent.KEYCODE_ENTER);

    /**
     * 匹配的按键序列3
     */
    public static final String GOODKEYCODES2 = String.valueOf(KeyEvent.KEYCODE_DPAD_DOWN)
            + String.valueOf(KeyEvent.KEYCODE_DPAD_UP) + String.valueOf(KeyEvent.KEYCODE_DPAD_DOWN)
            + String.valueOf(KeyEvent.KEYCODE_DPAD_UP) + String.valueOf(KeyEvent.KEYCODE_DPAD_DOWN)
            + String.valueOf(KeyEvent.KEYCODE_DPAD_UP) + String.valueOf(KeyEvent.KEYCODE_TV_INPUT);

    /**
     * 匹配的按键序列4
     */
    public static final String GOODKEYCODES3 = String.valueOf(KeyEvent.KEYCODE_DPAD_DOWN)
            + String.valueOf(KeyEvent.KEYCODE_DPAD_UP) + String.valueOf(KeyEvent.KEYCODE_DPAD_DOWN)
            + String.valueOf(KeyEvent.KEYCODE_DPAD_UP) + String.valueOf(KeyEvent.KEYCODE_DPAD_DOWN)
            + String.valueOf(KeyEvent.KEYCODE_DPAD_UP) + String.valueOf(KeyEventExtra.KEYCODE_SOURCE);

    public static final String GOODKEYCODES4 = String.valueOf(KeyEvent.KEYCODE_DPAD_LEFT)
            + String.valueOf(KeyEvent.KEYCODE_DPAD_DOWN) + String.valueOf(KeyEvent.KEYCODE_DPAD_LEFT)
            + String.valueOf(KeyEvent.KEYCODE_DPAD_UP);

    public static final String START_TV_SOURCE = "com.eostek.tv.SOURCE";

    public static final String INPUT_SOURCE_CHANGE = "com.mstar.tv.service.COMMON_EVENT_SIGNAL_STATUS_UPDATE";

    public static final String REBOOT_HOTKEY_SERVICE = "com.eostek.hotkeyservice.HotKeyService.REBOOT";

}
