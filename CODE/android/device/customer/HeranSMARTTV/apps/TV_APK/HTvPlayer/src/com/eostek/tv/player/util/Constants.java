
package com.eostek.tv.player.util;

public final class Constants {
    public static String serverUrl = "http://adv.heran.babao.com/interface/clientService.jsp";
    
    public final static int NETWORK_CONNECTION_TIMEOUT = 2 * 1000;

    public final static int NETWORK_READ_DATA_TIMEOUT = 2 * 1000;
    
    public final static String LAUNCHER_PACKAGE = "com.heran.launcher2";
    
    public final static String LAUNCHER_ACTIVITY_APPSTORE = "com.heran.launcher2.eosweb.MyWebViewActivity";
    
    public final static int DENSITY_1280 = 160;
    
    public final static int DENSITY_1920 = 240;
    
    public static final String CANCELSTANDBY = "com.eostek.tv.player.cancelstandyby";

    public static final String STARTSTANDBY = "com.eostek.tv.player.startstandyby";

    public static final String SHOWCOUTDOWN = "com.eostek.tv.player.showcountdown";

    public static final String DISMISSCOUTDOWN = "com.eostek.tv.player.dismisscountdown";

    public static final String START_COUNTERDOWN = "com.eostek.tv.player.intent.action.CounterDown";

    public enum EnumScreenMode {
        // / The screen saver mode is invalid service.
        MSRV_DTV_SS_INVALID_SERVICE,
        // / The screen saver mode is no CI module.
        MSRV_DTV_SS_NO_CI_MODULE,
        // / The screen saver mode is CI+ Authentication.
        MSRV_DTV_SS_CI_PLUS_AUTHENTICATION,
        // / The screen saver mode is scrambled program.
        MSRV_DTV_SS_SCRAMBLED_PROGRAM,
        // / The screen saver mode is channel block.
        MSRV_DTV_SS_CH_BLOCK,
        // / The screen saver mode is parental block.
        MSRV_DTV_SS_PARENTAL_BLOCK,
        // / The screen saver mode is audio only.
        MSRV_DTV_SS_AUDIO_ONLY,
        // / The screen saver mode is data only.
        MSRV_DTV_SS_DATA_ONLY,
        // / The screen saver mode is common video.
        MSRV_DTV_SS_COMMON_VIDEO,
        // / The screen saver mode is Unsupported Format.
        MSRV_DTV_SS_UNSUPPORTED_FORMAT,
        // / The screen saver mode is invalid pmt.
        MSRV_DTV_SS_INVALID_PMT,
        // / The screen saver mode support type.
        MSRV_DTV_SS_MAX,

        MSRV_DTV_SS_CA_NOTIFY
    };

    public enum EnumSignalProgSyncStatus {
        // /< Input timing stable, no input sync detected
        E_SIGNALPROC_NOSYNC,
        // /< Input timing stable, has stable input sync and support this timing
        E_SIGNALPROC_STABLE_SUPPORT_MODE,
        // /< Input timing stable, has stable input sync but this timing is not
        // supported
        E_SIGNALPROC_STABLE_UN_SUPPORT_MODE,
        // /< Timing change, has to wait InfoFrame if HDMI input
        E_SIGNALPROC_UNSTABLE,
        // /< Timing change, has to auto adjust if PCRGB input
        E_SIGNALPROC_AUTO_ADJUST,
    };

    public enum EnumDeskEvent {
        // 0
        EV_DTV_CHANNELNAME_READY,
        // 1
        EV_ATV_AUTO_TUNING_SCAN_INFO,
        // 2
        EV_ATV_MANUAL_TUNING_SCAN_INFO,
        // 3
        EV_DTV_AUTO_TUNING_SCAN_INFO,
        // 4
        EV_DTV_PROGRAM_INFO_READY,
        // 5
        EV_SIGNAL_LOCK,
        // 6
        EV_SIGNAL_UNLOCK,
        // 7
        EV_POPUP_DIALOG,
        // 8
        EV_SCREEN_SAVER_MODE,
        // 9
        EV_CI_LOAD_CREDENTIAL_FAIL,
        // 10
        EV_EPGTIMER_SIMULCAST,
        // 11
        EV_HBBTV_STATUS_MODE,
        // 12
        EV_MHEG5_STATUS_MODE,
        // 13
        EV_MHEG5_RETURN_KEY,
        // 14
        EV_OAD_HANDLER,
        // 15
        EV_OAD_DOWNLOAD,
        // 16
        EV_PVR_NOTIFY_PLAYBACK_TIME,
        // 17
        EV_PVR_NOTIFY_PLAYBACK_SPEED_CHANGE,
        // 18
        EV_PVR_NOTIFY_RECORD_TIME,
        // 19
        EV_PVR_NOTIFY_RECORD_SIZE,
        // 20
        EV_PVR_NOTIFY_RECORD_STOP,
        // 21
        EV_PVR_NOTIFY_PLAYBACK_STOP,
        // 22
        EV_PVR_NOTIFY_PLAYBACK_BEGIN,
        // 23
        EV_PVR_NOTIFY_TIMESHIFT_OVERWRITES_BEFORE,
        // 24
        EV_PVR_NOTIFY_TIMESHIFT_OVERWRITES_AFTER,
        // 25
        EV_PVR_NOTIFY_OVER_RUN,
        // 26
        EV_PVR_NOTIFY_USB_REMOVED,
        // 27
        EV_PVR_NOTIFY_CI_PLUS_PROTECTION,
        // 28
        EV_PVR_NOTIFY_PARENTAL_CONTROL,
        // 29
        EV_PVR_NOTIFY_ALWAYS_TIMESHIFT_PROGRAM_READY,
        // 30
        EV_PVR_NOTIFY_ALWAYS_TIMESHIFT_PROGRAM_NOTREADY,
        // 32
        EV_PVR_NOTIFY_CI_PLUS_RETENTION_LIMIT_UPDATE,
        // 33
        EV_DTV_AUTO_UPDATE_SCAN,
        // 34
        EV_TS_CHANGE,
        // 35
        EV_POPUP_SCAN_DIALOGE_LOSS_SIGNAL,
        // 36
        EV_POPUP_SCAN_DIALOGE_NEW_MULTIPLEX,
        // 37
        EV_POPUP_SCAN_DIALOGE_FREQUENCY_CHANGE,
        // 37
        EV_RCT_PRESENCE, EV_CHANGE_TTX_STATUS,
        // 38
        EV_DTV_PRI_COMPONENT_MISSING,
        // 39
        EV_AUDIO_MODE_CHANGE,
        // 40
        EV_MHEG5_EVENT_HANDLER,
        // 41
        EV_OAD_TIMEOUT,
        // 42
        EV_GINGA_STATUS_MODE,
        // 43
        EV_HBBTV_UI_EVENT,
        // 44
        EV_ATV_PROGRAM_INFO_READY,
    }
}
