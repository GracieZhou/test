
package com.eostek.scifly.album;

public class Constants {
    public static final boolean isDebug = true;

    public static final String IMAGE_PATHS = "ImagePaths";

    public static final String CURRENT_POSITION = "currentPosition";

    public static final String AUTO_PLAY = "autopaly";

    public static final String SHAREPREFRER_STRING = "SciflyCloudAlbum";

    public static final String EFFECT_KEY = "effect";

    public static final String PERIOD_KEY = "timePeriod";

    public static final String ROOT_PHOTOS_KEY = "root_photos";

    public static final String USER_FILES_KEY = "user_files";

    public static final String ALBUM_INFO_KEY = "photo_albums";

    public static final String MASTER_NAME_KEY = "master_name";

    public static final String CLOUD_DISK_TYPE = "cloud_disk_type";

    public static final String BAIDU_TOKEN = "baidu_token";

    public static final String BAIDU_MASTER = "baidu_master";

    public static final String DROPBOX_TOKEN = "dropbox_token";

    public static final String SINA_AUTOLOGIN = "sina_autologin";

    public static final String ONEDRIVE_AUTOLOGIN = "onedrive_autologin";

    public static final String DROPBOX_AUTOLOGIN = "dropbox_autologin";

    public static final String ROOT_ALBUM_NAME = "Root";

    public static final int SLIDE_SHOW = 0x0001;

    public static final int BAIDU_CLOUD_DISK = 0x0002;

    public static final int SINA_VDISK = 0x0003;

    public static final int DROPBOX_CLOUD_DISK = 0x0004;

    public static final int ONEDRIVE_CLOUD_DISK = 0x0005;

    public static final String IMAG_EPAGER_ACTION = "android.intent.action.IMAGEPAGER";

    public final static String MENU_ACTION = "android.intent.action.MENU";

    public final static String mBrootPath = "/apps/wp2pcs"; // 用户测试的根目录

    /**
     * 获取当前用户空间配额信息url
     */
    public static final String GETQUOTA = "https://pcs.baidu.com/rest/2.0/pcs/quota";

    public static final String UPLOAD = "https://c.pcs.baidu.com/rest/2.0/pcs/file";

    /**
     * 获取指定图片文件的缩略图
     */
    public static final String THUMB_NAIL = "https://pcs.baidu.com/rest/2.0/pcs/thumbnail";

    /**
     * 以视频、音频、图片及文档四种类型的视图获取所创建应用程序下的文件列表url
     */
    public static final String STREAM_URL = "https://pcs.baidu.com/rest/2.0/pcs/stream";

    public static final String EFFECT_SETTING_ACTION = "android.intent.action.EFFECTSETTING";
}
