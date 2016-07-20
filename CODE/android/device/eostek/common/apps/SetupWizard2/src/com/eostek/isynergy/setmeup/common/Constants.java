
package com.eostek.isynergy.setmeup.common;

public class Constants {
    public static enum ACTION_TYPE {
        // 客户端选择设备时通知activity显示配对码
        REQ_PAIRING_CODE(0),
        // 设置 wifi
        WIFI_SETTING(1),
        // 设置timezone
        TIMEZONE_SETTING(2),
        // 恢复出厂
        RESTORE_FACTORY(3),
        // 高级网络
        ADNET_SETTING(4),
        // 设置设备名称
        DEVICE_NAME_SETTING(5),
        // 链接wifi或者开启ap
        SWITCH_NETWORK(6),
        // 当链接wifi或者开启ap时通知activity 显示或者隐藏
        SWITCH_NETWORK_SHOW(7),
        // ENABLE wifi
        WIFI_ENABLED(8);

        int value = 0;

        ACTION_TYPE(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static enum ErrorCode {
        SERVICE_NOT_SUPPORTED(-1), FAILED_WIFI_SETTING(-2), INVALID_PARA(-3), FAILED_UPNP_DEV_NAME_SETTING(-4), FAILED_HD_NAME_SETTING(
                -5), FAILED_SWITCH_NETWORK(-6), INTERNAL_ERROR(-7);

        private int value;

        ErrorCode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static enum SuccessCode {
        SET_ME_UP_SUCCESS(0), CONNECT_TO_WIFI(1), SET_UP_WIFI_AP(2);

        private int value;

        SuccessCode(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public static final String NAME_ACTION_TYPE = "ACTION_TYPE";

    public static final String NAME_PARAMETER = "PARAMETER";

    public static final String NAME_PARAMETER_PHRASE = "PHRASE";

    public static final int MAX_WAITTING_CONNECT_WIFI_TIMES = 30;

    public static final String SET_ME_UP_PARA_SPLIT = ",";

    public static final String DEFAULT_DEV_NAME = "SciflyCast";

    /**
     * the name of custome dir that positing in /data/data/%pac%/file
     */
    public static final String CONFIG_CUSTOM_DIR = "config";

    public static final String CONFIG_FILE_DESCRIPTION = "setmeup.xml";

    public static final String CONFIG_FILE_ACTION = "settingcontrol.xml";

    public static final int MAX_READ_BUFFER_LEN = 1024;

    /**
     * sp 文件名称
     */
    public static final String SP_SET_ME_UP = "SETMEUP";

    /**
     * 关键字：获取上次保存在sp中的配置过的ssid
     */
    public static final String LAST_SETTING_WIFI_SSID = "SSID";

    /**
     * 关键字：获取上次保存在sp中的配置过的设备名称
     */
    public static final String LAST_SETTING_DEV_NAME = "DEV_NAME";

    public static final String KEY_MAIN_ACTIVITY = "MAIN_ACTIVITY";

    public static final String MAIN_ACTIVITY_NAME = "com.eostek.isynergy.setmeup.ui.SetmeupMainActivity";

    public static final String ACTION_RESET_BUTTON = "com.eostek.scifly.intent.action.RESET_BUTTON";

    public static final String ACTION_LONG_RESET_BUTTON = "com.eostek.scifly.intent.action.LONG_PRESS_RESET_BUTTON";

    public static final String ACTION_MODIFY_DEV_NAME = "com.eostek.scifly.intent.action.ACTION_DEVICE_INFO_CHANGED";

    /**
     * 通知mainactivity 退出
     */
    public static final int CONTROLLER_UI_FINISH = -2;

    /**
     * 通知mainactivity 显示
     */
    public static final int CONTROLLER_UI_SHOW = -1;

    public static final String PREFIX_AP = "SCIFLY_";

    public static final String ASSIGNMENT_TYPE_STATIC = "0";

    public static final String ASSIGNMENT_TYPE_STATIC_VALUE = "STATIC";
}
