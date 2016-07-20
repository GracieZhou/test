package com.eostek.tvmenu.picture;

public class PictureConstants {
    final static int COLOR_TEMPERATURE = 16;
    
    final static short T_NRMode_IDX = 0x0E;

    final static short T_SystemSetting_IDX = 0x19;

    //tag to the msg of handler copying with item view updating
    final static int MSG_UPDATEVIEW_PICTURE_MODE = 0;
    
    final static int MSG_UPDATEVIEW_PICTURE_CONTRAST = 1;
    
    final static int MSG_UPDATEVIEW_PICTURE_BRIGHTNESS = 2;
    
    final static int MSG_UPDATEVIEW_PICTURE_HUE = 3;
    
    final static int MSG_UPDATEVIEW_PICTURE_SHARPNESS = 4;
    
    final static int MSG_UPDATEVIEW_PICTURE_SATURATION = 5;
    
    final static int MSG_UPDATEVIEW_PICTURE_BACKLIGHT = 6;
    
    final static int MSG_UPDATEVIEW_PICTURE_TEMPERATURE = 7;
    
    final static int MSG_UPDATEVIEW_RED = 8;
    
    final static int MSG_UPDATEVIEW_GREEN = 9;
    
    final static int MSG_UPDATEVIEW_BLUE = 10;
    
    final static int MSG_UPDATEVIEW_HDMI_FULL = 11;
    
    //item type
    public static final int TYPE_ITEM_ENUM = 0;
    
    public static final int TYPE_ITEM_DIGITAL = 1;
    
    public static final int TYPE_ITEM_BUTTON = 2;
    
    public static final int TYPE_ITEM_BOOL = 3;
    
    //item index
    final static int INDEX_PICTURE_MODE = 0;

    final static int INDEX_PICTURE_CONTRAST = 1;

    final static int INDEX_PICTURE_BRIGHTNESS = 2;

    final static int INDEX_PICTURE_HUE = 3;

    final static int INDEX_PICTURE_SHARPNESS = 4;

    final static int INDEX_PICTURE_SATURATION = 5;

    final static int INDEX_PICTURE_BACKLIGHT = 6;

    final static int INDEX_PICTURE_TEMPERATURE = 7;

    final static int INDEX_RED = 8;

    final static int INDEX_GREEN = 9;

    final static int INDEX_BLUE = 10;

    final static int INDEX_HDMI_FULL = 11;

    final static int INDEX_ZOOM_MODE = 12;

    final static int INDEX_IMAGE_NOISE_REDUCTION = 13;

    final static int INDEX_MPEG_NOISE_REDUCTION = 15;
    
    final static int INDEX_OSD_TRANSPARENCY = 14;
    
    public static final String CURSORVIDEO = "content://mstar.tv.usersetting/videosetting/inputsrc/";
    
    public static final String EPICTURE = "ePicture";
    
    public static final String CURSORPICMODE = "content://mstar.tv.usersetting/picmode_setting";
    
    public static final String PICTUREMODETYPE = "PictureModeType";
    
    public static final String ECOLORTEMP = "eColorTemp";
    
    public static final String CURSORNRMODE = "content://mstar.tv.usersetting/nrmode";
    
    public static final String RETSORNRMODE = "content://mstar.tv.usersetting/nrmode/nrmode/";
    
    public static final String NRMODE = "NRMode";
    
    public static final String ENR = "eNR";
    
    public static final String EMPEG_NR = "eMPEG_NR";
    
    public static final String INPUTSRC = "/inputsrc/";
    
    public static final String UPDATE_TBL_NRMODE_IGNORED = "update tbl_NRMode ignored";
    
    public static final String U8COLORRANGEMODE = "u8ColorRangeMode";
    
    public static final String SYSTEMSETTING = "content://mstar.tv.usersetting/systemsetting";
    
    public static final String UPDATE_TBL_SYSTEMSETTING_IGNORED = "update tbl_systemsetting ignored";
    
    public static final String HDMIUNDERSCAN = "hdmiunderscan";
    
}
