
package com.eostek.scifly.messagecenter.util;

import android.os.Environment;

/**
 * Provide a static constant values.
 */
public final class Constants {

    private Constants() {
    }

    /** Constants for voice message. */
    public static final int VOICE_THUMB_HEIGHT = 125;

    /** Constants for voice message. */
    public static final int VOICE_THUMB_WIDTH = 253;

    /** Constants for apk message height. */
    public static final int APK_THUMB_HEIGHT = 200;

    /** Constants for apk message width . */
    public static final int APK_THUMB_WIDTH = 200;

    /** Constants for video message height. */
    public static final int VIDEO_THUMB_HEIGHT = 169;

    /** Constants for video message width. */
    public static final int VIDEO_THUMB_WIDTH = 253;

    /** Constant for image message height. */
    public static final int IMAGE_THUMB_HEIGHT = 200;

    /** Constant for image message width. */
    public static final int IMAGE_THUMB_WIDTH = 200;

    /** Constant for epg message height. */
    public static final int EPG_THUMB_HEIGHT = 325;

    /** Constant for epg message width. */
    public static final int EPG_THUMB_WIDTH = 226;

    /** Constant for date format "yyyy-MM-dd". */
    public static final String TIME_YMD = "yyyy-MM-dd";

    /** Constant for date format "yyyy-MM-dd HH:mm". */
    public static final String TIME_YMD_HM = "yyyy-MM-dd HH:mm";

    /** Constant for date format "yyyy-MM-dd HH:mm:ss". */
    public static final String TIME_YMD_HMS = "yyyy-MM-dd HH:mm:ss";

    /** Constant for the status of Message is unread. */
    public static final int MESSAGE_UNREAD = 0;

    /** Constant for the status of Message is read. */
    public static final int MESSAGE_READ = 1;

    /** Constant for first message. */
    public static final int FIRST_MESSAGE = 0;

    /** Constant for the status of Message is read. */
    public static final String PREFIX = "Android/data/com.eostek.scifly.messagecenter/cache/";

    /** Constants for message date cache path. */
    public static final String CACHE_PATH = Environment.getExternalStoragePublicDirectory(PREFIX).getAbsolutePath();

    /** Constants for message total cache size. */
    public static final int TOTAL_CACHE_SIZE = 200;

    /** Constants for message single cache size. */
    public static final int SINGLE_CACHE_SIZE = 20;

    /** Constants for package name. */
    public static final String PACKAGE_NAME = "com.hrtvbic.usb.S6A918";

    /** Constants for class name. */
    public static final String IMG_PLAYER_CLASS = "com.hrtvbic.usb.S6A918.photoplayer.PhotoPlayerActivity";

    /** Constants for type of image. */
    public static final String IMG_PLAYER_TYPE = "image/*";

    /** Constants for key of image. */
    public static final String IMG_PLAYER_KEY = "fromMsgPhoto";

    /** Constants for class name. */
    public static final String VIDEO_PLAYER_CLASS = "com.hrtvbic.usb.S6A918.videoplayer.VideoPlayerActivity";

    /** Constants for type of video. */
    public static final String VIDEO_PLAYER_TYPE = "video/*";

    /** Constants for key of video. */
    public static final String VIDEO_PLAYER_KEY = "fromMsgVideo";

    /** Constants for class name. */
    public static final String AUDIO_PLAYER_CLASS = "com.android.music.MediaPlaybackActivity";

    /** Constants for type of audio. */
    public static final String AUDIO_PLAYER_TYPE = "audio/ogg";

    /** Constants for key of audio. */
    public static final String AUDIO_PLAYER_KEY = "fromMsgAudio";

    /** Constants of APK_PLAYER_CLASS. */
    public static final String APK_PLAYER_CLASS = null;

    /** Constants of APK_PLAYER_TYPE. */
    public static final String APK_PLAYER_TYPE = "application/vnd.android.package-archive";

    /** Constants of APK_PLAYER_KEY. */
    public static final String APK_PLAYER_KEY = null;

    /** Constants of NOT_CACHE_TYPE. */
    public static final int NOT_CACHE_TYPE = -1;

    /** Constants for file type of png. */
    public static final String IMG_POSTFIX = ".png";

    /** Constants for file type of mp4. */
    public static final String VIDEO_POSTFIX = ".mp4";

    /** Constants for file type of ogg. */
    public static final String VOICE_POSTFIX = ".ogg";

    /** Constants for file type of apk. */
    public static final String APK_POSTFIX = ".apk";

    /** Constants for file type of doc. */
    public static final String DOC_POSTFIX = ".doc";

    /** Constants for file type of docx. */
    public static final String DOCX_POSTFIX = ".docx";

    /** Constants for file type of ppt. */
    public static final String PPT_POSTFIX = ".ppt";

    /** Constants for file type of pptx. */
    public static final String PPTX_POSTFIX = ".pptx";

    /** Constants for file type of xlsx. */
    public static final String XLSX_POSTFIX = ".xlsx";

    /** Constants for file type of xls. */
    public static final String XLS_POSTFIX = ".xls";

    /** Constants for file type of pdf. */
    public static final String PDF_POSTFIX = ".pdf";

    /** Constants for file type of txt. */
    public static final String TXT_POSTFIX = ".txt";

    /** Constants for file type of epub. */
    public static final String EPUB_POSTFIX = ".epub";

    /** Constant for the status of sender is blocked.. */
    public static final int SENDER_BLOCKED = 1;

    /** Constant for the status of sender is not blocked.. */
    public static final int SENDER_UNBLOCKED = 0;

    public static final String LIVE_SCIFLYKU = "sciflykuName";

    public static final String LIVE_KEY_PLAYURL = "livePlayUrl";

    public static final String LIVE_KEY_PLAYTITLE = "livePlayTitle";

    public static final String LIVE_KEY_PGM_ID = "pgmContentId";

    public static final String LIVE_KEY_CONTENT_ID = "contentId";

    public static final String LIVE_KEY_CHANNEL_LOGO = "channelLogo";

    public static final String LIVE_FROM_MSG = "isFromMessage";

    public static final String SERVER_URL = "http://112.124.30.218:8081/wigAdmin/interface/bootVideoReport.jsp";
//    public static final String SERVER_URL = "http://172.23.65.62:8822/TMS/interface/bootVideoReport.jsp";

    public static final String SERVER_URL_PROPERTY = "persist.sys.service.url";

    /**
     * Sn+ task state type.
     */
    public static final int SN_TASK_STATE_READY = 0;

    public static final int SN_TASK_STATE_RUNNING = 1;

    public static final int SN_TASK_STATE_PAUSED = 2;

    public static final int SN_TASK_STATE_COMPLETE = 3;

    public static final int SN_TASK_STATE_ERROR = 4;

    public static final String DOWNLOAD_ENGINE_HTTP = "http";

    public static final String DOWNLOAD_ENGINE_P2P = "p2p";
}
