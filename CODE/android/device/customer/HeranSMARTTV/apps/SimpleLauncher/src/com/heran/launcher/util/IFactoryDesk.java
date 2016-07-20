package com.heran.launcher.util;

public interface IFactoryDesk {
    public final static short T_OverscanAdjust_IDX = 0x2B;

    public final static short T_HDMIOverscanSetting_IDX = 0x2E;

    public final static short T_YPbPrOverscanSetting_IDX = 0x2F;

    public final static short T_DTVOverscanSetting_IDX = 0x30;

    public final static short T_ATVOverscanSetting_IDX = 0x32;

    public enum MAPI_AVD_VideoStandardType {
        // / Video standard PAL BGHI
        E_MAPI_VIDEOSTANDARD_PAL_BGHI,
        // / Video standard NTSC M
        E_MAPI_VIDEOSTANDARD_NTSC_M,
        // / Video standard SECAM
        E_MAPI_VIDEOSTANDARD_SECAM,
        // / Video standard NTSC 44
        E_MAPI_VIDEOSTANDARD_NTSC_44,
        // / Video standard PAL M
        E_MAPI_VIDEOSTANDARD_PAL_M,
        // / Video standard PAL N
        E_MAPI_VIDEOSTANDARD_PAL_N,
        // / Video standard PAL 60
        E_MAPI_VIDEOSTANDARD_PAL_60,
        // / NOT Video standard
        E_MAPI_VIDEOSTANDARD_NOTSTANDARD,
        // / Video standard AUTO
        E_MAPI_VIDEOSTANDARD_AUTO,
        // / Max Number
        E_MAPI_VIDEOSTANDARD_MAX
    }

    public static enum MAPI_VIDEO_ARC_Type {
        // / Default
        E_AR_DEFAULT,
        // / 16x9
        E_AR_16x9,
        // / 4x3
        E_AR_4x3,
        // / Auto
        E_AR_AUTO,
        // / Panorama
        E_AR_Panorama,
        // / Just Scan
        E_AR_JustScan,
        // / Zoom 1
        E_AR_Zoom1,
        // / Zoom 2
        E_AR_Zoom2,
        // / 14:9
        E_AR_14x9,
        // / point to point
        E_AR_DotByDot,
        // / Subtitle
        E_AR_Subtitle,
        // / movie
        E_AR_Movie,
        // / Personal
        E_AR_Personal,

        // / 4x3 Panorama
        E_AR_4x3_PanScan,
        // / 4x3 Letter Box
        E_AR_4x3_LetterBox,
        // / 16x9 PillarBox
        E_AR_16x9_PillarBox,
        // / 16x9 PanScan
        E_AR_16x9_PanScan,
        // / 4x3 Combind
        E_AR_4x3_Combind,
        // / 16x9 Combind
        E_AR_16x9_Combind,
        // / Zoom 2X
        E_AR_Zoom_2x,
        // / Zoom 3X
        E_AR_Zoom_3x,
        // / Zoom 4X
        E_AR_Zoom_4x,

        // / Maximum value of this enum
        E_AR_MAX,
    }

    // /Define DTV resolution
    public static enum MAX_DTV_Resolution_Info {
        // /480i_60
        E_DTV480i_60,
        // /480p_60
        E_DTV480p_60,
        // /576i_50
        E_DTV576i_50,
        // /576p_50
        E_DTV576p_50,
        // /720p_60
        E_DTV720p_60,
        // /720p_50
        E_DTV720p_50,
        // /1080i_60
        E_DTV1080i_60,
        // /1080i_50
        E_DTV1080i_50,
        // /1080p_60
        E_DTV1080p_60,
        // /1080p_50
        E_DTV1080p_50,
        // /1080p_30
        E_DTV1080p_30,
        // /1080p_24
        E_DTV1080p_24,
        // /resolution number
        E_DTV_MAX
    }

    // /Define HDMI resolution type
    public static enum MAX_HDMI_Resolution_Info {
        // /480i_60
        E_HDMI480i_60,
        // /480p_60
        E_HDMI480p_60,
        // /576i_50
        E_HDMI576i_50,
        // /576p_50
        E_HDMI576p_50,
        // /720p_60
        E_HDMI720p_60,
        // /720p_50
        E_HDMI720p_50,
        // /1080i_60
        E_HDMI1080i_60,
        // /1080i_50
        E_HDMI1080i_50,
        // /1080p_60
        E_HDMI1080p_60,
        // /1080p_50
        E_HDMI1080p_50,
        // /1080p_30
        E_HDMI1080p_30,
        // /1080p_24
        E_HDMI1080p_24,
        // /1440x480i_60
        E_HDMI1440x480i_60,
        // /1440x480p_60
        E_HDMI1440x480p_60,
        // /1440x576i_50
        E_HDMI1440x576i_50,
        // /1440x576p_50
        E_HDMI1440x576p_50,
        // /MAX
        E_HDMI_MAX,
    }

    // /Define component resolution type
    public static enum MAX_YPbPr_Resolution_Info {
        // /480i_60
        E_YPbPr480i_60,
        // /480p_60
        E_YPbPr480p_60,
        // /576i_50
        E_YPbPr576i_50,
        // /576p_50
        E_YPbPr576p_50,
        // /720p_60
        E_YPbPr720p_60,
        // /720p_50
        E_YPbPr720p_50,
        // /1080i_60
        E_YPbPr1080i_60,
        // /1080i_50
        E_YPbPr1080i_50,
        // /1080p_60
        E_YPbPr1080p_60,
        // /1080p_50
        E_YPbPr1080p_50,
        // /1080p_30
        E_YPbPr1080p_30,
        // /1080p_24
        E_YPbPr1080p_24,
        // /1080p_25
        E_YPbPr1080p_25,
        // /Max
        E_YPbPr_MAX,
    }

    // /vd signal type
    public static enum EN_VD_SIGNALTYPE {
        // /NTSC
        SIG_NTSC,
        // /PAL
        SIG_PAL,
        // /SECAM
        SIG_SECAM,
        // /NTSC443
        SIG_NTSC_443,
        // /PAL_M
        SIG_PAL_M,
        // /PAL_NC
        SIG_PAL_NC,
        // /Signal number
        SIG_NUMS,
        // /Signal none
        SIG_NONE
    }

    public class ST_MAPI_VIDEO_WINDOW_INFO {
        public int u16H_CapStart; // /< Capture window H start

        public int u16V_CapStart; // /< Capture window V start

        public short u8HCrop_Left; // /< H Crop Left

        public short u8HCrop_Right; // /< H crop Right

        public short u8VCrop_Up; // /< V Crop Up

        public short u8VCrop_Down; // /< V Crop Down

        public ST_MAPI_VIDEO_WINDOW_INFO() {
            u16H_CapStart = 0;
            u16V_CapStart = 0;
            u8HCrop_Left = 50;
            u8HCrop_Right = 50;
            u8VCrop_Up = 50;
            u8VCrop_Down = 50;
        }
    }
}
