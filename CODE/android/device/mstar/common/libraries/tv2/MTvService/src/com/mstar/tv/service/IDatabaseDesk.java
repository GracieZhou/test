//<MStar Software>
//******************************************************************************
// MStar Software
// Copyright (c) 2010 - 2014 MStar Semiconductor, Inc. All rights reserved.
// All software, firmware and related documentation herein ("MStar Software") are
// intellectual property of MStar Semiconductor, Inc. ("MStar") and protected by
// law, including, but not limited to, copyright law and international treaties.
// Any use, modification, reproduction, retransmission, or republication of all
// or part of MStar Software is expressly prohibited, unless prior written
// permission has been granted by MStar.
//
// By accessing, browsing and/or using MStar Software, you acknowledge that you
// have read, understood, and agree, to be bound by below terms ("Terms") and to
// comply with all applicable laws and regulations:
//
// 1. MStar shall retain any and all right, ownership and interest to MStar
//    Software and any modification/derivatives thereof.
//    No right, ownership, or interest to MStar Software and any
//    modification/derivatives thereof is transferred to you under Terms.
//
// 2. You understand that MStar Software might include, incorporate or be
//    supplied together with third party's software and the use of MStar
//    Software may require additional licenses from third parties.
//    Therefore, you hereby agree it is your sole responsibility to separately
//    obtain any and all third party right and license necessary for your use of
//    such third party's software.
//
// 3. MStar Software and any modification/derivatives thereof shall be deemed as
//    MStar's confidential information and you agree to keep MStar's
//    confidential information in strictest confidence and not disclose to any
//    third party.
//
// 4. MStar Software is provided on an "AS IS" basis without warranties of any
//    kind. Any warranties are hereby expressly disclaimed by MStar, including
//    without limitation, any warranties of merchantability, non-infringement of
//    intellectual property rights, fitness for a particular purpose, error free
//    and in conformity with any international standard.  You agree to waive any
//    claim against MStar for any loss, damage, cost or expense that you may
//    incur related to your use of MStar Software.
//    In no event shall MStar be liable for any direct, indirect, incidental or
//    consequential damages, including without limitation, lost of profit or
//    revenues, lost or damage of data, and unauthorized system use.
//    You agree that this Section 4 shall still apply without being affected
//    even if MStar Software has been modified by MStar in accordance with your
//    request or instruction for your use, except otherwise agreed by both
//    parties in writing.
//
// 5. If requested, MStar may from time to time provide technical supports or
//    services in relation with MStar Software to you for your use of
//    MStar Software in conjunction with your or your customer's product
//    ("Services").
//    You understand and agree that, except otherwise agreed by both parties in
//    writing, Services are provided on an "AS IS" basis and the warranty
//    disclaimer set forth in Section 4 above shall apply.
//
// 6. Nothing contained herein shall be construed as by implication, estoppels
//    or otherwise:
//    (a) conferring any license or right to use MStar name, trademark, service
//        mark, symbol or any other identification;
//    (b) obligating MStar or any of its affiliates to furnish any person,
//        including without limitation, you and your customers, any assistance
//        of any kind whatsoever, or any information; or
//    (c) conferring any license or right under any intellectual property right.
//
// 7. These terms shall be governed by and construed in accordance with the laws
//    of Taiwan, R.O.C., excluding its conflict of law rules.
//    Any and all dispute arising out hereof or related hereto shall be finally
//    settled by arbitration referred to the Chinese Arbitration Association,
//    Taipei in accordance with the ROC Arbitration Law and the Arbitration
//    Rules of the Association by three (3) arbitrators appointed in accordance
//    with the said Rules.
//    The place of arbitration shall be in Taipei, Taiwan and the language shall
//    be English.
//    The arbitration award shall be final and binding to both parties.
//
//******************************************************************************
//<MStar Software>

package com.mstar.tv.service;

import java.util.List;
import com.mstar.android.tvapi.common.vo.CCSetting;
import com.mstar.android.tvapi.common.vo.CaptionOptionSetting;
import com.mstar.android.tvapi.common.vo.ColorTemperature;
import com.mstar.android.tvapi.common.vo.ColorTemperatureExData;
import com.mstar.android.tvapi.common.vo.EnumAtvAudioModeType;
import com.mstar.android.tvapi.common.vo.EnumAudioMode;
import com.mstar.android.tvapi.common.vo.EnumChannelSwitchMode;
import com.mstar.android.tvapi.common.vo.EnumDisplayTvFormat;
import com.mstar.android.tvapi.common.vo.EnumMaxDtvResolutionInfo;
import com.mstar.android.tvapi.common.vo.EnumPictureMode;
import com.mstar.android.tvapi.common.vo.EnumPowerOnLogoMode;
import com.mstar.android.tvapi.common.vo.EnumPowerOnMusicMode;
import com.mstar.android.tvapi.common.vo.EnumSoundAdOutput;
import com.mstar.android.tvapi.common.vo.EnumSoundHidevMode;
import com.mstar.android.tvapi.common.vo.EnumSoundMode;
import com.mstar.android.tvapi.common.vo.EnumSpdifType;
import com.mstar.android.tvapi.common.vo.EnumSurroundMode;
import com.mstar.android.tvapi.common.vo.EnumSurroundSystemType;
import com.mstar.android.tvapi.common.vo.EnumThreeDVideo3DOutputAspect;
import com.mstar.android.tvapi.common.vo.EnumThreeDVideoAutoStart;
import com.mstar.android.tvapi.common.vo.EnumThreeDVideoDisplayFormat;
import com.mstar.android.tvapi.common.vo.EnumThreeDVideoLrViewSwitch;
import com.mstar.android.tvapi.common.vo.EnumThreeDVideoSelfAdaptiveDetect;
import com.mstar.android.tvapi.common.vo.EnumCableOperator;
import com.mstar.android.tvapi.common.vo.MpegNoiseReduction.EnumMpegNoiseReduction;
import com.mstar.android.tvapi.common.vo.NoiseReduction.EnumNoiseReduction;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.mstar.android.tvapi.dtv.atsc.vo.RR5RatingPair;
import com.mstar.android.tvapi.dtv.atsc.vo.Regin5DimensionInformation;
import com.mstar.android.tvapi.dtv.atsc.vo.UsaMpaaRatingType;
import com.mstar.android.tvapi.dtv.atsc.vo.UsaTvRatingInformation;
import com.mstar.android.tvapi.factory.vo.FactoryNsVdSet;
import com.mstar.android.tvapi.factory.vo.PqlCalibrationData;

public interface IDatabaseDesk {
    public final static short T_3DInfo_IDX = 0x00;

    public final static short T_3DSetting_IDX = 0x01;

    public final static short T_BlockSysSetting_IDX = 0x02;

    public final static short T_CECSetting_IDX = 0x03;

    public final static short T_CISettineUpInfo_IDX = 0x13;

    public final static short T_PicMode_Setting_IDX = 0x14;

    public final static short T_PipSetting_IDX = 0x15;

    public final static short T_SoundMode_Setting_IDX = 0x16;

    public final static short T_SoundSetting_IDX = 0x17;

    public final static short T_SubtitleSetting_IDX = 0x18;

    public final static short T_SystemSetting_IDX = 0x19;

    public final static short T_ThreeDVideoMode_IDX = 0x1A;

    public final static short T_TimeSetting_IDX = 0x1B;

    public final static short T_USER_COLORTEMP_IDX = 0x1C;

    public final static short T_USER_COLORTEMP_EX_IDX = 0x1D;

    public final static short T_UserLocationSetting_IDX = 0x1E;

    public final static short T_UserMMSetting_IDX = 0x1F;

    public final static short T_UserOverScanMode_IDX = 0x20;

    public final static short T_IsdbUserSetting_IDX = 0x0B;

    public final static short T_MediumSetting_IDX = 0x0C;

    public final static short T_MfcMode_IDX = 0x0D;

    public final static short T_NRMode_IDX = 0x0E;

    public final static short T_NitInfo_IDX = 0x0F;

    public final static short T_Nit_TSInfo_IDX = 0x10;

    public final static short T_OADInfo_IDX = 0x11;

    public final static short T_OADInfo_UntDescriptor_IDX = 0x12;

    public final static short T_CISetting_IDX = 0x04;

    public final static short T_DB_VERSION_IDX = 0x05;

    public final static short T_DvbtPresetting_IDX = 0x06;

    public final static short T_EpgTimer_IDX = 0x07;

    public final static short T_FavTypeName_IDX = 0x08;

    public final static short T_InputSource_Type_IDX = 0x09;

    public final static short T_IsdbSysSetting_IDX = 0x0A;

    public final static short T_UserPCModeSetting_IDX = 0x21;

    public final static short T_VideoSetting_IDX = 0x22;

    public final static short T_ThreeDVideoRouterSetting_IDX = 0x23;

    // ------------------factory.db-----------------------------
    public final static short T_ADCAdjust_IDX = 0x24;

    public final static short T_FacrotyColorTemp_IDX = 0x25;

    public final static short T_FacrotyColorTempEx_IDX = 0x26;

    public final static short T_FactoryExtern_IDX = 0x27;

    public final static short T_NonStarndardAdjust_IDX = 0x28;

    public final static short T_SSCAdjust_IDX = 0x29;

    public final static short T_NonLinearAdjust_IDX = 0x2A;

    public final static short T_OverscanAdjust_IDX = 0x2B;

    public final static short T_PEQAdjust_IDX = 0x2C;

    public final static short T_Factory_DB_VERSION_IDX = 0x2D;

    public final static short T_HDMIOverscanSetting_IDX = 0x2E;

    public final static short T_YPbPrOverscanSetting_IDX = 0x2F;

    public final static short T_DTVOverscanSetting_IDX = 0x30;

    //AgingMode dirty flag
    public final static short T_FactoryExtern_AgingMode_IDX = 0x43;

    // ------------------customer.db-----------------------------
    public final static short T_ATVDefaultPrograms_IDX = 0x31;

    public final static short T_DTVDefaultPrograms_IDX = 0x32;

    // ------------------usersetting.db-----------------------------
    public final static short T_VChipSetting_IDX = 0x38;

    public final static short T_VChipMappItem_IDX = 0x39;

    public final static short T_RR5RatingPair_IDX = 0x3A;

    public final static short T_VChipRatingInfo_IDX = 0x3B;

    public final static short T_Regin5DimensionInfo_IDX = 0x3C;

    public final static short T_AbbRatingText_IDX = 0x3D;

    public final static short T_MiscSetting_IDX = 0x3E;

    public final static short T_mstCECSetting_IDX = 0x3F;

    public final static short T_CCSetting_IDX = 0x40;

    public final static short T_CCAdvancedSetting_IDX = 0x41;

    public final static short T_DvbUserSetting_IDX = 0x44;

    public final static short PEQ_BAND_NUM = 5;

    /** color temperature */
    // TODO: combine this with tvapi.EnumColorTemperature
    public enum EnumColorTemperature_ {
        E_COOL, E_NATURE, E_WARM, E_USER, E_NUM
    }

    /** color tempEx input source */
    public enum EnumColorTempExInputSource {
        E_VGA, E_ATV, E_CVBS, E_SVIDEO, E_YPBPR, E_SCART, E_HDMI, E_DTV, E_OTHERS, E_NUM, E_NONE,
    }

    /** advanced picture settings */
    public enum EnumAdvancedPictureSetting {
        E_OFF, E_LOW, E_MIDDLE, E_HIGH, E_AUTO, E_NUM,
    }

    /** define noise reduction setting */
    public enum EnumNrSetting {
        E_OFF, E_LOW, E_MIDDLE, E_HIGH, E_AUTO, E_NUM,
    }

    /** MPEG noise reduction setting */
    public enum EnumMpegNrSetting {
        E_OFF, E_LOW, E_MIDDLE, E_HIGH, E_NUM,
    }

    /** Define aspect ratio type */
    public enum EnumAspectRatioType {
        E_DEFAULT, E_16x9, E_4x3, E_AUTO, E_Panorama, E_JustScan, E_Zoom1, E_Zoom2, E_14x9, E_DotByDot, E_MAX,
    }

    /** the display resolution type */
    public enum EnumDisplayResolutionType {
        E_SEC32_LE32A_FULLHD,
        // For Normal LVDS panel
        E_RES_SXGA, // /< 1280x1024, Pnl_AU17_EN05_SXGA
        E_RES_WXGA, // /< 1366x768, Pnl_AU20_T200XW02_WXGA,
        E_RES_WXGA_PLUS, // /< 1440x900, Pnl_CMO19_M190A1_WXGA,
        // Pnl_AU19PW01_WXGA
        E_RES_WSXGA, // /< 1680x1050, Pnl_AU20_M201EW01_WSXGA,
        E_RES_FULL_HD, // /< 1920x1080, Pnl_AU37_T370HW01_HD,
        // Pnl_CMO216H1_L01_HD.h
        // / Maximum value of this enum
        E_DACOUT_576I, // pal
        E_DACOUT_576P, E_DACOUT_720P_50, E_DACOUT_1080P_24, E_DACOUT_1080P_25, E_DACOUT_1080I_50, E_DACOUT_1080P_50, E_DACOUT_480I, // ntsc
        E_DACOUT_480P, E_DACOUT_720P_60, E_DACOUT_1080P_30, E_DACOUT_1080I_60, E_DACOUT_1080P_60, E_DACOUT_AUTO, E_CMO_CMO260J2_WUXGA, // 1920*1200
        // For VGA OUTPUT 60HZ
        E_VGAOUT_640x480P_60,
        // For VGA OUTPUT 50HZ
        // For TTL output
        E_TTLOUT_480X272_60, E_RES_MAX_NUM,
    }

    /** Video out VE type */
    public enum EnumVideoOutVEType {
        E_NTSC, E_PAL, E_AUTO,
    }

    /** Video standard */
    public enum EnumVideoStandardType {
        E_PAL_BGHI, E_NTSC_M, E_SECAM, E_NTSC_44, E_PAL_M, E_PAL_N, E_PAL_60, E_NOTSTANDARD, E_AUTO, E_MAX
    }

    /** dynamic contrast settings */
    public enum EnumDynamicContrast {
        E_OFF, E_ON, E_NUM,
    }

    /** film mode settings */
    public enum EnumFilmModeSetting {
        E_OFF, E_ON, E_NUM,
    }

    /** 3D Video mode */
    public enum Enum3dVideoMode {
        E_OFF, E_2D_TO_3D, E_SIDE_BY_SIDE, E_TOP_BOTTOM, E_FRAME_INTERLEAVING, E_PACKING_1080at24p, E_PACKING_720at60p, E_PACKING_720at50p, E_CHESS_BOARD, E_NUM,
    }

    /** 3D Video self adaptive level */
    public enum Enum3dVideoSelfAdaptiveLevel {
        E_LOW, E_MIDDLE, E_HIGH, E_NUM,
    }

    /** 3D Video 3dto2d mode */
    public enum Enum3dVideo3dTo2dMode {
        E_NONE, E_SIDE_BY_SIDE, E_TOP_BOTTOM, E_FRAME_PACKING, E_LINE_ALTERNATIVE, E_AUTO, E_FRAME_ALTERNATIVE, E_NUM,
    }

    /** 3D Video 3D Depth */
    public enum Enum3dVideoDepth {
        E_LEVEL_0, E_LEVEL_1, E_LEVEL_2, E_LEVEL_3, E_LEVEL_4, E_LEVEL_5, E_LEVEL_6, E_LEVEL_7, E_LEVEL_8, E_LEVEL_9, E_LEVEL_10, E_LEVEL_11, E_LEVEL_12, E_LEVEL_13, E_LEVEL_14, E_LEVEL_15, E_LEVEL_16, E_LEVEL_17, E_LEVEL_18, E_LEVEL_19, E_LEVEL_20, E_LEVEL_21, E_LEVEL_22, E_LEVEL_23, E_LEVEL_24, E_LEVEL_25, E_LEVEL_26, E_LEVEL_27, E_LEVEL_28, E_LEVEL_29, E_LEVEL_30, E_LEVEL_31, E_NUM,
    }

    /** 3D Video 3D Offset */
    public enum Enum3dVideoOffset {
        E_LEVEL_0, E_LEVEL_1, E_LEVEL_2, E_LEVEL_3, E_LEVEL_4, E_LEVEL_5, E_LEVEL_6, E_LEVEL_7, E_LEVEL_8, E_LEVEL_9, E_LEVEL_10, E_LEVEL_11, E_LEVEL_12, E_LEVEL_13, E_LEVEL_14, E_LEVEL_15, E_LEVEL_16, E_LEVEL_17, E_LEVEL_18, E_LEVEL_19, E_LEVEL_20, E_LEVEL_21, E_LEVEL_22, E_LEVEL_23, E_LEVEL_24, E_LEVEL_25, E_LEVEL_26, E_LEVEL_27, E_LEVEL_28, E_LEVEL_29, E_LEVEL_30, E_LEVEL_31, E_NUM,
    }
	
	//EosTek Patch Begin
    /** video adjust item settings */
    // TODO: need to merge with tvapi.common.vo.EnumVideoItem
    public enum EN_MS_VIDEOITEM {
        E_BRIGHTNESS, E_CONTRAST, E_SATURATION, E_SHARPNESS, E_HUE, E_BACKLIGHT, E_COLORTEMP, E_NUM,
    }
	// EosTek Patch End

    /** ADC setting index */
    public enum EnumAdcSettingIndex {
        E_VGA, E_SD, E_HD, E_RGB, E_YPBPR2_SD, E_YPBPR2_HD, E_YPBPR3_SD, E_YPBPR3_HD, E_NUM,
    }

    public enum EnumSatellitePlatform {
        E_OTHER, E_HDPLUS, E_FREESAT,
    }

    public enum EnumSuperModeSettings {
        E_MIN, E_OFF, E_ON, E_NUM,
    }

    public enum EnumOfflineDetectMode {
        E_OFF, E_INDICATION, E_AUTO, E_NUM,
    }

    public enum EnumAudysseyDynamicVolumeMode {
        E_OFF, E_ON, E_NUM,
    }

    public enum EnumAudysseyEqMode {
        E_OFF, E_ON, E_NUM,
    }

    public enum EnumFactoryPowerOnMode {
        E_SECONDARY, E_MEMORY, E_DIRECT, E_NUM,
    }

    public enum EnumTestPatternMode {
        E_GRAY, E_RED, E_GREEN, E_BLUE, E_BLACK, E_OFF,
    }

    public enum EnumFactoryPreSet {
        E_ATV, E_DTV, E_NUM,
    }

    public enum Enum3DSelfAdaptiveLevel {
        EN_3D_SELFADAPTIVE_LEVEL_LOW, EN_3D_SELFADAPTIVE_LEVEL_MIDDLE, EN_3D_SELFADAPTIVE_LEVEL_HIGH, EN_3D_SELFADAPTIVE_LEVEL_MAX,
    }

    public enum EnumHdmiResolutionInfo {
        E_480I_60, E_480P_60, E_576I_50, E_576P_50, E_720P_60, E_720P_50, E_1080I_60, E_1080I_50, E_1080P_60, E_1080P_50, E_1080P_30, E_1080P_24, E_1440X480I_60, E_1440X480P_60, E_1440X576I_50, E_1440X576P_50, E_NUM,
    }

    public enum EnumYpbprResolutionInfo {
        E_480I_60, E_480P_60, E_576I_50, E_576P_50, E_720P_60, E_720P_50, E_1080I_60, E_1080I_50, E_1080P_60, E_1080P_50, E_1080P_30, E_1080P_24, E_1080P_25, E_NUM,
    }

    // TODO: need to merge with tvapi.common.vo.EnumNlaSetIndex
    public enum EnumNlaSetIndex {
        E_VOLUME, E_BRIGHTNESS, E_CONTRAST, E_SATURATION, E_SHARPNESS, E_HUE, E_BACKLIGHT, E_NUM,
    }

    public enum EnumVdSignalType {
        E_NTSC, E_PAL, E_SECAM, E_NTSC_443, E_PAL_M, E_PAL_NC, E_NUM, E_NONE,
    }

    public enum EnumS3dTableField {
        E_VIDEO, E_DISPLAYFORMAT, E_3D_DEPTH, E_AUTO_START, E_3D_OUTPUT_ASPECT, E_LR_VIEW_SWITCH, E_SELF_ADAPTIVE_DETECT, E_SELF_ADAPTIVE_LEVEL, E_3D_TO_2D, E_3D_OFFSET,
    }

    public enum EnumUserSettingField {
        E_ENABLE_WDT, E_UART_USB,
    }

    /** define enum for noise reduction and mpeg noise reduction */
    class NrMode {
        /** noise reduction setting */
        public EnumNrSetting eNR;

        /** MPEG noise reduction setting */
        public EnumMpegNrSetting eMPEG_NR;

        public NrMode(EnumNrSetting evalue1, EnumMpegNrSetting evalue2) {
            this.eNR = evalue1;
            this.eMPEG_NR = evalue2;
        }
    }

    /** SubColor Setting */
    public class SubColorSetting {
        /**
         * check sum <<checksum should be put at top of the structure, do not
         * move it to other place>>
         */
        int checkSum;

        public short subBrightness;

        public short subContrast;

        public SubColorSetting(int v1, short v2, short v3) {
            this.checkSum = v1;
            this.subBrightness = v2;
            this.subContrast = v3;
        }
    }

    /** setting of 3D Video */
    public class ThreeDimensionVideoMode {
        public Enum3dVideoMode eThreeDVideo;

        public EnumThreeDVideoSelfAdaptiveDetect eThreeDVideoSelfAdaptiveDetect;

        public EnumThreeDVideoDisplayFormat eThreeDVideoDisplayFormat;

        public Enum3dVideo3dTo2dMode eThreeDVideo3DTo2D;

        public Enum3dVideoDepth eThreeDVideo3DDepth;

        public Enum3dVideoOffset eThreeDVideo3DOffset;

        public EnumThreeDVideoAutoStart eThreeDVideoAutoStart;

        public EnumThreeDVideo3DOutputAspect eThreeDVideo3DOutputAspect;

        public EnumThreeDVideoLrViewSwitch eThreeDVideoLRViewSwitch;

        public ThreeDimensionVideoMode(Enum3dVideoMode eValue1,
                EnumThreeDVideoSelfAdaptiveDetect eValue2, EnumThreeDVideoDisplayFormat eValue4,
                Enum3dVideo3dTo2dMode eValue5, Enum3dVideoDepth eValue6, Enum3dVideoOffset eValue7,
                EnumThreeDVideoAutoStart eValue8, EnumThreeDVideo3DOutputAspect eValue9,
                EnumThreeDVideoLrViewSwitch eValue10) {
            this.eThreeDVideo = eValue1;
            this.eThreeDVideoSelfAdaptiveDetect = eValue2;
            this.eThreeDVideoDisplayFormat = eValue4;
            this.eThreeDVideo3DTo2D = eValue5;
            this.eThreeDVideo3DDepth = eValue6;
            this.eThreeDVideo3DOffset = eValue7;
            this.eThreeDVideoAutoStart = eValue8;
            this.eThreeDVideo3DOutputAspect = eValue9;
            this.eThreeDVideoLRViewSwitch = eValue10;
        }
    }

    /** define detail setting of picture mode */
    public class PictureModeSetting {
        public short backlight;

        public short contrast;

        public short brightness;

        public short saturation;

        public short sharpness;

        public short hue;

        public EnumColorTemperature_ eColorTemp;

        public EnumAdvancedPictureSetting eVibrantColour;

        public EnumAdvancedPictureSetting ePerfectClear;

        public EnumAdvancedPictureSetting eDynamicContrast;

        public EnumAdvancedPictureSetting eDynamicBacklight;

        public PictureModeSetting(short backlight, short con, short bri, short sat, short sha,
                short hue, EnumColorTemperature_ colortemp, EnumAdvancedPictureSetting evcolor,
                EnumAdvancedPictureSetting epClear, EnumAdvancedPictureSetting edcontrast,
                EnumAdvancedPictureSetting edbackling) {
            this.backlight = backlight;
            this.contrast = con;
            this.brightness = bri;
            this.saturation = sat;
            this.sharpness = sha;
            this.hue = hue;
            this.eColorTemp = colortemp;
            this.eVibrantColour = evcolor;
            this.ePerfectClear = epClear;
            this.eDynamicContrast = edcontrast;
            this.eDynamicBacklight = edbackling;
        }
    }

    /** overscan setting */
    public class UserOverScanSetting {
        public short overScanHposition;

        public short overScanVposition;

        public short overScanHRatio;

        public short overScanVRatio;

        public UserOverScanSetting(short x, short y, short w, short h) {
            this.overScanHposition = x;
            this.overScanVposition = y;
            this.overScanHRatio = w;
            this.overScanVRatio = h;
        }
    }

    /** define video setting for */
    public class VideoSetting {
        /**
         * check sum <<checksum should be put at top of the structure, do not
         * move it to other place>>
         */
        public int checkSum;

        public EnumPictureMode ePicture;

        /** picture mode detail setting, 24Byte */
        public PictureModeSetting pictureModeSettings[];

        public NrMode nrMode[];

        public SubColorSetting subColorSetting;

        public EnumAspectRatioType eARCType;

        public EnumDisplayResolutionType eDisplayResolutionType;

        public EnumVideoOutVEType eVideoOutVEType;

        public EnumVideoStandardType eVideoStandardType;

        public EnumAtvAudioModeType eAtvAudioModeType;

        public EnumDynamicContrast eDynamicContrast;

        public EnumFilmModeSetting eFilmModeSetting;

        public ThreeDimensionVideoMode threeDimensionVideoMode;

        public UserOverScanSetting userOverScanSetting;

        /** TV format setting (4:3/16:9SD/16:9HD) */
        public EnumDisplayTvFormat eDisplayTvFormat;

        /** PcMode Flag */
        public int bIsPcMode;
    }

    public class ColorTempExData {
        public int redGain;

        public int greenGain;

        public int blueGain;

        public int redOffset;

        public int greenOffset;

        public int blueOffset;

        public ColorTempExData(int v1, int v2, int v3, int v4, int v5, int v6) {
            this.redGain = v1;
            this.greenGain = v2;
            this.blueGain = v3;
            this.redOffset = v4;
            this.greenOffset = v5;
            this.blueOffset = v6;
        }
    }

    public class ColorTempModeSetting {
        /**
         * check sum <<checksum should be put at top of the structure, do not
         * move it to other place>>
         */
        public int checkSum;

        /** color temperature mode setting */
        public ColorTempExData colorTempExData[][]; // 24Byte

        public ColorTempModeSetting() {
            int i;
            int j;
            colorTempExData = new ColorTempExData[EnumColorTemperature_.E_NUM.ordinal()][EnumColorTempExInputSource.E_NUM
                    .ordinal()];
            for (i = 0; i < EnumColorTemperature_.E_NUM.ordinal(); i++) {
                for (j = 0; j < EnumColorTempExInputSource.E_NUM.ordinal(); j++) {
                    colorTempExData[i][j] = new ColorTempExData(0x80, 0x80, 0x80, 0x80, 0x80, 0x80);
                }
            }
            checkSum = 0xFFFF;
        }
    }

    public class CalbrationData {
        public int redGain;

        public int greenGain;

        public int blueGain;

        public int redOffset;

        public int greenOffset;

        public int blueOffset;

        public CalbrationData(int v1, int v2, int v3, int v4, int v5, int v6) {
            this.redGain = v1;
            this.greenGain = v2;
            this.blueGain = v3;
            this.redOffset = v4;
            this.greenOffset = v5;
            this.blueOffset = v6;
        }
    }

    public class UserSubtitleSetting {
        public int subtitleLanguage1;

        public int subtitleLanguage2;

        /** HardOfHearing setting, 0=Off, 1= On */
        public boolean isHoHEnable;

        /** subtitle enable or not, 0=Off, 1= On */
        public boolean isSubtitleEnable;

        public UserSubtitleSetting(int eLang1, int eLang2, boolean bHearing,
                boolean bSubtitle) {
            this.subtitleLanguage1 = eLang1;
            this.subtitleLanguage2 = eLang2;
            this.isHoHEnable = bHearing;
            this.isSubtitleEnable = bSubtitle;
        }
    }

    public class LocationSetting {
        /** the ID of Location. */
        public int locationNo;

        /** the Longitude value */
        public int manualLongitude;

        /** the Latitude value */
        public int manualLatitude;

        public LocationSetting(int v1, int v2, int v3) {
            this.locationNo = v1;
            this.manualLongitude = v2;
            this.manualLatitude = v3;
        }
    }

    public class UserSetting {
        /**
         * check sum <<checksum should be put at top of the struct, do not
         * move it to other place>>
         */
        public int checkSum;

        /** check to run InstallationGuide or not */
        public boolean isRunInstallationGuide;

        /** check if no channel to show banner */
        public boolean isNoChannel;

        /**
         * check SI auto update off or not,CableReady Manual scanning shall
         * set SI updates to "OFF" on all physical channels.
         */
        public boolean isDisableSiAutoUpdate;

        /** input source selection */
        public EnumInputSource enInputSourceType;

        /** Cable Operator setting */
        public EnumCableOperator eCableOperators;

        /** Satellite platform setting */
        public EnumSatellitePlatform eSatellitePlatform;

        /** network id */
        public int networkId;

        /** OSD language */
        public int osdLanguage;

        /** SPDIF mode setting */
        public EnumSpdifType eSpdifMode;

        /** SoftwareUpdate 0=Off, 1= On */
        public short softwareUpdate;

        /** OAD Update Time */
        public short oadTime;

        /** OAD Scan auto execution after system bootup 0=Off, 1=On */
        public short oadScanAfterWakeup;

        /** autovolume 0=Off, 1= On */
        public short autoVolume;

        /** DcPowerOFFMode 0= Power Off, 1= DC Power Off */
        public short dcPowerOFFMode;

        /** DTV Player Extend */
        public short dtvRoute;

        /** SCART output RGB */
        public short scartOutRGB;

        /** OSD Transparency, 0=0%, 1=25%, 2=50%, 3=75%, 4=100% */
        public short transparency;

        /** OSD timeout (seconds) */
        public long menuTimeOut;

        /** Audio Only */
        public short audioOnly;

        /** watch dog */
        public short isEnableWDT;

        /** Favorite Network Region */
        public short favoriteRegion;

        /** Bandwidth */
        public short bandwidth;

        /** Time Shift Size Type */
        public short timeShiftSizeType;

        /** Do OAD scan right now */
        public short oadScan;

        /** PVR Record All Enable in factory menu */
        public short enablePVRRecordAll;

        /** Color range mode 0-255\16-235 for HDMI */
        public short colorRangeMode;

        /** HDMI Audio Source 0: DVD 1: PC */
        public short hdmiAudioSource;

        /**
         * PVR enable always timeshift (1 bit, union with bEnablePVRRecordAll
         * if needed)
         */
        public short enableAlwaysTimeshift;

        /** MFC */
        // MFC_MODE MfcMode;

        /** enum for SUPER */
        public EnumSuperModeSettings eSUPER;

        /** check to Uart Bus */
        public boolean isCheckUartBus;

        /** For DTV AutoZoom */
        public short autoZoom;

        /** OverScan on/off for all source */
        public boolean isOverScanForAllSource;

        /** Brazil video system */
        public short brazilVideoStandardType;

        public short softwareUpdateMode;

        public long osdActiveTime;

        public boolean isMessageBoxExist;// 0 not exit 1 exit

        /** OAD SW VErsion */
        public int lastOADVersion;

        public boolean isAutoChannelUpdateEnable;// run standby scan and OAD

        /**
         * check when enter standby mode
         * OSD Duration Time 0: 5s 1: 10 2: 15s 3: 20 4: 30
         */
        public short osdDuration;

        /** Channel Switch mode */
        public EnumChannelSwitchMode eChannelSwitchMode;

        /** Offline Detection */
        public EnumOfflineDetectMode eOfflineDetectionMode;

        /** TV no signal mode: 0: Noise; 1: Blue Screen */
        public boolean bBlueScreen;

        /** Power On Music */
        public EnumPowerOnMusicMode ePowerOnMusic;

        /** Power On Logo */
        public EnumPowerOnLogoMode ePowerOnLogo;

        /** OAD Viewer Prompt */
        public boolean bViewerPrompt;

        /** Store Cookies: 0: disabled; 1: enabled */
        public boolean bEnableStoreCookies;
    }

    public class SoundModeSeting {
        public short bass;

        public short treble;

        public short eqBand1;

        public short eqBand2;

        public short eqBand3;

        public short eqBand4;

        public short eqBand5;

        public short eqBand6;

        public short eqBand7;

        public boolean isUserMode;

        public short balance;

        public EnumAudioMode eSoundAudioChannel;

        public SoundModeSeting(short Bass, short treble, short EqBand1, short EqBand2,
                short EqBand3, short EqBand4, short EqBand5) {
            this.bass = Bass;
            this.treble = treble;
            this.eqBand1 = EqBand1;
            this.eqBand2 = EqBand2;
            this.eqBand3 = EqBand3;
            this.eqBand4 = EqBand4;
            this.eqBand5 = EqBand5;
        }
    }

    public class UserSoundSetting {
        /**
         * check sum <<checksum should be put at top of the struct, do not
         * move it to other place>>
         */
        public int checkSum;

        /** Sound Mode Enumeration */
        public EnumSoundMode eSoundMode;

        /** Audyssey Dynamic Volume */
        public EnumAudysseyDynamicVolumeMode eAudysseyDynamicVolume;

        /** Audyssey EQ */
        public EnumAudysseyEqMode eAudysseyEQ;

        /** Surround Sound Mode */
        public EnumSurroundSystemType eSurroundSoundMode;

        /** AVC enable */
        public boolean isAVCEnable = false;

        /** Volume */
        public short volume;

        /** Headphone Volume */
        public short headphoneVolume;

        /** Balance */
        public short balance;

        /** Primary_Flag */
        public short primaryFlag;

        /** Audio language setting 1 */
        public int eSoundAudioLan1; // EN_LANGUAGE

        /** Audio language setting 2 */
        public int eSoundAudioLan2; // EN_LANGUAGE

        /** Audio mute */
        public short muteFlag; // for ATSC_TRUNK

        /** audio mode setting */
        public EnumAudioMode eSoundAudioChannel;

        /** AD enable */
        public boolean isADEnable;

        /** AD volume adjust */
        public short adVolume;

        /** sound ad output */
        public EnumSoundAdOutput eADOutput;

        /** the delay of SPDIF */
        public short spdifDelay;

        /** the delay of speaker */
        public short speakDelay;

        /** audo path prescale value */
        public short ch1PreScale;

        /** surroundMode */
        public EnumSurroundMode eSurroundMode = EnumSurroundMode.E_SURROUND_MODE_OFF;

        public UserSoundSetting() {
            eSoundMode = EnumSoundMode.E_STANDARD;
            this.balance = 50;
        }
    }

    public class FactorySscSetting {
        public int lvdsSscSpan;

        public int lvdsSscStep;

        public int miu0SscSpan;

        public int miu0SscStep;

        public int miu1SscSpan;

        public int miu1SscStep;

        public int miu2SscSpan;

        public int miu2SscStep;

        public boolean isLvdsSscEnable;

        public boolean isMiuSscEnable;

        public FactorySscSetting() {
            isLvdsSscEnable = false;
            isMiuSscEnable = false;
            lvdsSscSpan = 128;
            lvdsSscStep = 128;
            miu0SscSpan = 128;
            miu0SscStep = 128;
            miu1SscSpan = 128;
            miu1SscStep = 128;
        }
    }

    public class NonStandardVdSetting {
        /** AFEC D4 */
        public short u8AFEC_D4;

        /** AFEC D8 bit 0~3 */
        public short u8AFEC_D8_Bit3210;

        /** AFEC D8 bit2 */
        public short u8AFEC_D5_Bit2;// //[2]When CF[2]=1, K1/K2 Default Value,

        /**
         * K1=2E,K2=6A
         * AFEC D7 lower bound
         */
        public short u8AFEC_D7_LOW_BOUND;// Color kill

        /** AFEC D7 higher bound */
        public short u8AFEC_D7_HIGH_BOUND;// Color kill

        /** AFEC D9 bit 0 */
        public short u8AFEC_D9_Bit0;

        /** AFEC A0 */
        public short u8AFEC_A0; // only debug

        /** AFEC A1 */
        public short u8AFEC_A1; // only debug

        /** AFEC 66 bit 6~7 */
        public short u8AFEC_66_Bit76;// only debug

        /** AFEC 6E bit 4~7 */
        public short u8AFEC_6E_Bit7654;// only debug

        /** AFEC 6E bit 0~3 */
        public short u8AFEC_6E_Bit3210;// only debug

        /** AFEC 43 */
        public short u8AFEC_43;// auto or Fixed AGC

        /** AFEC 44 */
        public short u8AFEC_44;// AGC gain

        /** AFEC CB */
        public short u8AFEC_CB;

        public NonStandardVdSetting() {
            this.u8AFEC_D4 = 0x00;
            this.u8AFEC_D8_Bit3210 = 0x00;
            this.u8AFEC_D5_Bit2 = 0x00;
            this.u8AFEC_D7_LOW_BOUND = 0x00;
            this.u8AFEC_D7_HIGH_BOUND = 0x00;
            this.u8AFEC_D9_Bit0 = 0x00;
            this.u8AFEC_A0 = 0x00;
            this.u8AFEC_A1 = 0x00;
            this.u8AFEC_66_Bit76 = 0x00;
            this.u8AFEC_6E_Bit7654 = 0x00;
            this.u8AFEC_6E_Bit3210 = 0x00;
            this.u8AFEC_43 = 0x00;
            this.u8AFEC_44 = 0x00;
            this.u8AFEC_CB = 0x00;
        }
    };

    /** VID setting */
    public class NonStandardVifSetting {
        /** top */
        public short vifTop;

        /** VGA max */
        public int vifVgaMaximum;

        /** Gain distribution threshold */
        public int gainDistributionThr;

        /** VIF AGC VGA base */
        public short vifAgcVgaBase;

        /** china descrambler box mode: A1(0~5) J2(0~11) usefull */
        public short chinaDescramblerBox;

        public int chinaDescramblerBoxDelay;

        /** CRKP1 */
        public short vifCrKp1;

        /** CRKI1 */
        public short vifCrKi1;

        /** CRKP2 */
        public short vifCrKp2;

        /** CRKI2 */
        public short vifCrKi2;

        /** CRKP */
        public short vifCrKp;

        /** CRKI */
        public short vifCrKi;

        /** CR lock threshold */
        public int vifCrLockThr;

        /** CR threshold */
        public int vifCrThr;

        /** flag to indicate CR KPKI */
        public boolean vifCrKpKiAdjust;

        /** delay reduce */
        public short vifDelayReduce;

        /** over modulation */
        public boolean vifOverModulation;

        /** clamping values */
        public int vifClampgainClampOvNegative;

        /** clamping gain values */
        public int vifClampgainGainOvNegative;

        /** VIF AGC REF VALUE */
        public short vifACIAGCREF;

        /** VIF AGC REF NEGATIVE VALUE */
        public short vifAgcRefNegative;

        /** VIF_ASIA_SIGNAL_OPTION */
        public boolean vifAsiaSignalOption;

        /** Vif version */
        public short vifVersion;

        public NonStandardVifSetting() {
            this.vifTop = 0x00;
            this.vifVgaMaximum = 0x00;
            this.gainDistributionThr = 0x00;
            this.vifAgcVgaBase = 0x00;
            this.chinaDescramblerBox = 0x01;
            this.chinaDescramblerBoxDelay = 496;
            this.vifCrKp1 = 0x00;
            this.vifCrKi1 = 0x00;
            this.vifCrKp2 = 0x00;
            this.vifCrKi2 = 0x00;
            this.vifCrKp = 0x00;
            this.vifCrKi = 0x00;
            this.vifCrLockThr = 0x00;
            this.vifCrThr = 0x00;
            this.vifCrKpKiAdjust = false;
            this.vifDelayReduce = 0x00;
            this.vifOverModulation = false;
            this.vifClampgainClampOvNegative = 0x00;
            this.vifClampgainGainOvNegative = 0x00;
            this.vifACIAGCREF = 0x00;
            this.vifAgcRefNegative = 0x60;
            this.vifAsiaSignalOption = true;
            this.vifVersion = 0x00;
        }
    }

    public class ExternSetting {
        /**
         * check sum <<checksum should be put at top of the structure, do not
         * move it to other place>>
         */
        public int checkSum;

        public String softVersion = "0.0.1";

        public String boardType = "A3";

        public String panelType = "Full-HD";

        public String dayAndTime = "2011.9.22 12:00";

        public EnumTestPatternMode eTestPatternMode;

        public boolean isDtvAvAbnormalDelay;

        public EnumFactoryPreSet eFactoryPreset;

        public short panelSwingVal;

        public short audioPreScale;

        public EnumFactoryPowerOnMode ePowerOnMode;

        public EnumSoundHidevMode eHidevMode;

        public boolean isAgingModeEnable;

        public boolean isNoSignalAutoShutdownEnable;

        public short vdDspVersion;

        public short audioNrThr;

        public short audioSifThreshold;

        public short audioDspVersion;

        public ExternSetting() {
            eTestPatternMode = EnumTestPatternMode.E_OFF;
            eFactoryPreset = EnumFactoryPreSet.E_ATV;
            isDtvAvAbnormalDelay = false;
            panelSwingVal = 0;
            audioPreScale = 0;
            ePowerOnMode = EnumFactoryPowerOnMode.E_MEMORY;
            eHidevMode = EnumSoundHidevMode.E_SOUND_HIDEV_OFF;
            isAgingModeEnable = false;
            vdDspVersion = 0;
            audioNrThr = 0;
            audioSifThreshold = 0;
            audioDspVersion = 0;
        }
    }

    public class NonLinearAdjustPointSetting {
        public short u8OSD_V0;

        public short u8OSD_V25;

        public short u8OSD_V50;

        public short u8OSD_V75;

        public short u8OSD_V100;

        public NonLinearAdjustPointSetting() {
            u8OSD_V0 = 128;
            u8OSD_V25 = 128;
            u8OSD_V50 = 128;
            u8OSD_V75 = 128;
            u8OSD_V100 = 128;
        }
    }

    public class NonLinearAdjustSetting {
        /**
         * check sum <<checksum should be put at top of the structure, do not
         * move it to other place>>
         */
        public int checkSum;

        public EnumNlaSetIndex eNlaSetIndex;

        /** Point 0,25,50,75,100 */
        public NonLinearAdjustPointSetting nlaSetting[];

        public NonLinearAdjustSetting() {
            int i;
            nlaSetting = new NonLinearAdjustPointSetting[EnumNlaSetIndex.E_NUM.ordinal()];
            for (i = 0; i < EnumNlaSetIndex.E_NUM.ordinal(); i++) {
                nlaSetting[i] = new NonLinearAdjustPointSetting();
            }
            eNlaSetIndex = EnumNlaSetIndex.E_VOLUME;
        }
    }

    public class PeqParameter {
        public int band;

        public int gain;

        public int foh;

        public int fol;

        public int qValue;

        public PeqParameter() {
            band = 3;
            gain = 120;
            foh = 80;
            fol = 45;
            qValue = 80;
        }
    }

    public class PeqSetting {
        public int checkSum;

        public PeqParameter peqParams[];

        public PeqSetting() {
            int i = 0;
            peqParams = new PeqParameter[PEQ_BAND_NUM];
            for (i = 0; i < PEQ_BAND_NUM; i++) {
                peqParams[i] = new PeqParameter();
            }
        }
    }

    public class VideoWindowInfo {
        public int hCapStart;

        public int vCapStart;

        public short hCropLeft;

        public short hCropRight;

        public short vCropUp;

        public short vCropDown;

        public VideoWindowInfo() {
            hCapStart = 0;
            vCapStart = 0;
            hCropLeft = 50;
            hCropRight = 50;
            vCropUp = 50;
            vCropDown = 50;
        }
    }

    public class DtvOverscanSetting {
        public int checkSum;

        /** DTV overscan table */
        public VideoWindowInfo videoWindowInfo[][];

        public DtvOverscanSetting() {
            int i, j;
            videoWindowInfo = new VideoWindowInfo[EnumMaxDtvResolutionInfo.E_MAX.ordinal()][EnumAspectRatioType.E_MAX
                    .ordinal()];
            for (i = 0; i < EnumMaxDtvResolutionInfo.E_MAX.ordinal(); i++) {
                for (j = 0; j < EnumAspectRatioType.E_MAX.ordinal(); j++) {
                    videoWindowInfo[i][j] = new VideoWindowInfo();
                }
            }
        }
    }

    public class HdmiOverscanSetting {
        public int checkSum;

        /** HDMI overscan table */
        public VideoWindowInfo videoWindowInfo[][];

        public HdmiOverscanSetting() {
            int i, j;
            videoWindowInfo = new VideoWindowInfo[EnumHdmiResolutionInfo.E_NUM.ordinal()][EnumAspectRatioType.E_MAX
                    .ordinal()];
            for (i = 0; i < EnumMaxDtvResolutionInfo.E_MAX.ordinal(); i++) {
                for (j = 0; j < EnumAspectRatioType.E_MAX.ordinal(); j++) {
                    videoWindowInfo[i][j] = new VideoWindowInfo();
                }
            }
        }
    }

    public class YPbPrOverscanSetting {
        public int checkSum;

        /** YPbPr overscan table */
        public VideoWindowInfo videoWindowInfo[][];

        public YPbPrOverscanSetting() {
            int i, j;
            videoWindowInfo = new VideoWindowInfo[EnumYpbprResolutionInfo.E_NUM.ordinal()][EnumAspectRatioType.E_MAX
                    .ordinal()];
            for (i = 0; i < EnumMaxDtvResolutionInfo.E_MAX.ordinal(); i++) {
                for (j = 0; j < EnumAspectRatioType.E_MAX.ordinal(); j++) {
                    videoWindowInfo[i][j] = new VideoWindowInfo();
                }
            }
        }
    }

    public class VdOverscanSetting {
        public int checkSum;

        /** VD overscan table */
        public VideoWindowInfo videoWindowInfo[][];

        public VdOverscanSetting() {
            int i, j;
            videoWindowInfo = new VideoWindowInfo[EnumVdSignalType.E_NUM.ordinal()][EnumAspectRatioType.E_MAX
                    .ordinal()];
            for (i = 0; i < EnumMaxDtvResolutionInfo.E_MAX.ordinal(); i++) {
                for (j = 0; j < EnumAspectRatioType.E_MAX.ordinal(); j++) {
                    videoWindowInfo[i][j] = new VideoWindowInfo();
                }
            }
        }
    }

    /** Time Setting */
    public class TimeSetting {
        /** checksum */
        public int checkSum;
        /** is on time or not */
        public boolean onTimeFlag;
        /** is off time or not */
        public boolean offTimeFlag;
        /** the state of off time */
        public short offTimeState;
        /** the hour of off time */
        public short offTimeInfo_Hour;
        /** the minute of off time */
        public short offTimeInfo_Min;
        /** the state of on time */
        public short onTimeState;
        /** the hour of on time */
        public short onTimeInfo_Hour;
        /** the minute of on time */
        public short onTimeInfo_Min;
        /** the channel for on time */
        public int onTimeChannel;
        /** the input source for on time */
        public short onTimeTVSrc;
        /** the antenna type */
        public short onTimeAntennaType;
        /** the volume for on time */
        public short onTimeVolume;
        /** the time zone */
        public short timeZoneInfo;
        /** is 12-hour or 24-hour */
        public boolean is12Hour;
        /** is auto sync or not */
        public boolean isAutoSync;
        /** clock mode: auto or manual */
        public boolean isClockMode;
        /** is auto-sleep or not */
        public boolean autoSleepFlag;
        /** is daylight saving or not */
        public boolean isDaylightSaving;
        /** the boot up type */
        public int timerBootMode;
        /** offset time in seconds */
        public int offsetTime;
        /** daylight saving mode */
        public int daylightSavingMode;

        public TimeSetting() {
            onTimeFlag = false;
            offTimeFlag = false;
            offTimeState = 0;
            offTimeInfo_Hour = 0;
            offTimeInfo_Min = 0;
            onTimeState = 0;
            onTimeInfo_Hour = 12;
            onTimeInfo_Min = 0;
            onTimeChannel = 0;
            onTimeTVSrc = 0;
            onTimeAntennaType = 0;
            onTimeVolume = 30;
            timeZoneInfo = 0;
            is12Hour = false;
            isAutoSync = false;
            isClockMode = true;
            autoSleepFlag = false;
            isDaylightSaving = false;
            timerBootMode = 0;
            offsetTime = 0;
            daylightSavingMode = 0;
        }
    }

    public int querySpdifMode();

    public void updateSpdifMode(int mode);

    public int queryCountry();

    public EnumChannelSwitchMode queryChannelSwitchMode();

    public void updateChannelSwitchMode(EnumChannelSwitchMode eChSwMode);

    public int queryAntennaType();

    public void updateAntennaType(int antennaType);

    /**
     * query the data for usa's movie rating.
     *
     * @return
     */
    public UsaMpaaRatingType queryVchipMpaaItem();

    /**
     * update the data for usa's movie rating.
     *
     * @return
     */
    public void updateVchipMpaaItem(UsaMpaaRatingType value);

    /**
     * query the data for usa's tv rating.
     *
     * @return
     */
    public UsaTvRatingInformation queryVchipSetting();

    /**
     * update the data for usa's tv rating.
     *
     * @return
     */
    public void updateVchipSetting(int[] value);

    /**
     * query the data for canadian english rating.
     *
     * @return
     */
    public int queryCanadaEngRatingLock();

    /**
     * update the data for canadian english rating.
     *
     * @return
     */
    public void updateCanadaEngRatingLock(int value);

    /**
     * query the data for canadian french rating.
     *
     * @return
     */
    public int queryCanadaFreRatingLock();

    /**
     * update the data for canadian french rating.
     *
     * @return
     */
    public void updateCanadaFreRatingLock(int value);

    /**
     * query the block unlock unrated.
     *
     * @param enable
     */
    public boolean queryBlockUnlockUnrated();

    /**
     * update the block unlock unrated.
     *
     * @param enable
     */
    public void updateBlockUnlockUnrated(boolean enable);

    /**
     * query the RRT5NoDimension.
     *
     * @return
     */
    public int queryRRT5NoDimension();

    /**
     * to update the version no to default.
     */
    public void updateVersionNo();

    /**
     * to query the rrt5 dimension include
     * index,dimensionName,values_Defined,graduated_Scale.
     *
     * @return
     */
    public List<Regin5DimensionInformation> queryRRT5Dimension();

    /**
     * update the rr5 rating pair
     *
     * @param title
     * @param index
     * @param value
     */
    public void updateRR5RatingPair(int title, int index, int value);

    /**
     * query the rr5 rating pair's information include the title and value.
     *
     * @param index
     * @param count
     * @return
     */
    public List<RR5RatingPair> queryRR5RatingPair(int index, int count);

    /**
     * query the ccsetting about ccmode, basic selection and advanced selection.
     *
     * @return
     */
    public CCSetting queryCCSetting();

    /**
     * update the ccsetting.
     *
     * @param seting
     */
    public void updateCCSetting(CCSetting seting);

    /**
     * query the data for advanced setting.
     *
     * @param index
     * @return
     */
    public CaptionOptionSetting queryAdvancedSetting(int index);

    /**
     * update the advanced setting.
     *
     * @param setting
     * @param index
     */
    public void updateAdvancedSetting(CaptionOptionSetting setting, int index);

    /**
     * query the block system lock mode.
     *
     * @return
     */
    public int queryBlockSysLockMode();

    /**
     * update the block system lock mode.
     *
     * @param value
     */
    public void updateBlockSysLockMode(int value);

    /**
     * query the input block flag. TV:bit0 YUV:Bit1 VGA:Bit2 HDMI1:bit3
     * HDMI2:bit4 HDMI3:bit5 HDMI4:Bit6 CVBS:Bit7 SVIDEO:bit8
     *
     * @return
     */
    public int[] queryInputBlockFlag();

    /**
     * update the input block flag.TV:bit0 YUV:Bit1 VGA:Bit2 HDMI1:bit3
     * HDMI2:bit4 HDMI3:bit5 HDMI4:Bit6 CVBS:Bit7 SVIDEO:bit8
     *
     * @param value
     */
    public void updateInputBlockFlag(int[] value);

    /**
     * query the AudioLanguage default value.
     *
     * @return
     */
    public int queryAudioLanguageDefaultValue();

    /**
     * update the AudioLanguage default value.If the language exist in current
     * ts,to set it for the audiolanguage.
     *
     * @return
     */
    public void updateAudioLanguageDefaultValue(int value);

    /**
     * queryPcModeInfo
     *
     * @return PCClock PCPhase PCHPos PCVPos.
     */
    public int[] queryPcModeInfo();

    public void updateADCAdjust(CalbrationData model, int sourceId);

    public NonLinearAdjustSetting queryNonLinearAdjusts();

    public void updateNonStandardAdjust(NonStandardVdSetting nonStandSet);

    public void updateOverscanAdjust(int FactoryOverScanType, VideoWindowInfo[][] model);

    public int queryVideo3DLrSwitchMode(int inputSrcType);

    public EnumThreeDVideoDisplayFormat queryThreeDVideoDisplayFormat(int inputSrcType);

    public void updatePicModeSetting(EN_MS_VIDEOITEM eIndex, int inputSrcType, int pictureModeType,
            int value);

    public int queryPicModeSetting(EN_MS_VIDEOITEM eIndex, int inputSrcType, int pictureModeType);

    public PictureModeSetting queryPictureModeSettings(int eModeIdx, int inputSrcType);

    public int queryPictureMode(int inputSrcType);

    public void updatePictureMode(int ePicMode, int inputSrcType);

    public void updateColorTempIdx(int inputSrcType, int pictureModeType,
            EnumColorTemperature_ eColorTemp);

    public EnumColorTemperature_ queryColorTempIdx(int inputSrcType, int pictureModeType);

    public int queryArcMode(int inputSrcType);

    public void updateArcMode(int eArcMode, int inputSrcType);

    public int queryArcMode(int inputSrcType, int picturemode);

    public void updateArcMode(int eArcMode, int inputSrcType, int picturemode);

    public int queryFilmMode(int inputSrcType);

    public void updateFilmMode(int mode, int inputSrcType);

    public void updateVideoAstPicture(PictureModeSetting model, int inputSrcType,
            int pictureModeType);

    public void updateVideoNRMode(NrMode model, int inputSrcType, int NRModeIdx);

    public void updateVideoAstSubColor(SubColorSetting model, int inputSrcType);

    public void updateVideo3DMode(EnumS3dTableField field, int value, int inputSrcType);

    public int queryVideo3DMode(EnumS3dTableField field, int inputSrcType);

    public void updateVideoUserOverScanMode(UserOverScanSetting model, int inputSrcType);

    public void updateNR(int inputSrcType, int colorTmpIdx, EnumNoiseReduction eNRIdx);

    public EnumNrSetting queryNR(int inputSrcType, int colorTmpIdx);

    public EnumMpegNrSetting queryMpegNR(int inputSrcType, int colorTmpIdx);

    public void updateMpegNR(int inputSrcType, int colorTmpIdx, EnumMpegNoiseReduction eMpNRIdx);

    public ColorTemperatureExData queryUsrColorTmpExData(int nInputType);

    public void updateUsrColorTmpExData(ColorTemperatureExData model, int nInputType);

    public UserSetting queryUserSysSetting();

    public void updateUserSysSetting(UserSetting model);

    public UserSubtitleSetting queryUserSubtitleSetting();

    public void updateUserSubtitleSetting(UserSubtitleSetting model);

    public LocationSetting queryUserLocSetting();

    public void updateUserLocSetting(LocationSetting model);

    public int querySoundMode();

    public void updateSoundMode(int soudModeType);

    public int queryVolume();

    public void updateVolume(int vol);

    public UserSoundSetting querySoundSetting();

    public void updateSoundSetting(UserSoundSetting model);

    public void updateSoundModeSetting(SoundModeSeting model, int soundModeType);

    public SoundModeSeting querySoundModeSetting(int soundModeType);

    public int queryBass(int soundMode);

    public void updateBass(int bass, int soundMode);

    public int queryTreble(int soundMode);

    public void updateTreble(int treble, int soundMode);

    public int queryBalance();

    public void updateBalance(int balance);

    public int queryAvc();

    public void updateAvc(int isAvc);

    public int querySrr();

    public void updateSrr(int isSrr);

    public int queryPCHPos();

    public int queryPCVPos();

    public int queryPCClock();

    public int queryPCModeIndex(int id);

    public boolean isPCTimingNew();

    public int queryPCPhase();

    public void updatePowerOnSource(int eSour);

    public int queryPowerOnSource();

    public void updatePowerOnAVMute(boolean enable);

    public boolean queryPowerOnAVMute();

    public int queryTrueBass();

    public void updateTrueBass(int value);

    public int querySeparateHearing();

    public void updateSeparateHearing(int value);

    public int queryWallmusic();

    public void updateWallmusic(int value);

    public int queryPowerOnMusic();

    public void updatePowerOnMusic(int value);

    public int queryBassSwitch();

    public void updateBassSwitch(int value);

    public int queryBassVolume();

    public void updateBassVolume(int value);

    public int queryEarPhoneVolme();

    public void updateEarPhoneVolume(int vol);

    public int queryCurrentInputSource();

    public void updateCurrentInputSource(int inputSrc);

    public PqlCalibrationData queryADCAdjust(int adcIndex);

    public void updateADCAdjust(PqlCalibrationData model, int sourceId);

    public void updateNonLinearAdjust(NonLinearAdjustPointSetting dataModel, int curveTypeIndex);

    public FactoryNsVdSet queryNoStandSet();

    public NonStandardVifSetting queryNoStandVifSet();

    public void updateNonStandardAdjust(FactoryNsVdSet model);

    public void updateNonStandardAdjust(NonStandardVifSetting vifSet);

    public ExternSetting queryFactoryExtern();

    public void updateFactoryExtern(ExternSetting model);

    public int queryAudioPrescale();

    public VideoWindowInfo[][] queryOverscanAdjusts(int overScanType);

    public void updateOverscanAdjust(int overScanType, int arcMode, VideoWindowInfo[][] model);

    public void updateYPbPrOverscanAdjust(int overScanType, int arcMode, VideoWindowInfo[][] model);

    public void updateHDMIOverscanAdjust(int overScanType, int arcMode, VideoWindowInfo[][] model);

    public void updateDTVOverscanAdjust(int FactoryOverScanType, int arcMode,
            VideoWindowInfo[][] model);

    public void updateATVOverscanAdjust(int FactoryOverScanType, int arcMode,
            VideoWindowInfo[][] model);

    public PeqSetting queryPEQAdjusts();

    public void updatePEQAdjust(PeqParameter model, int index);

    public FactorySscSetting querySSCAdjust();

    public void updateSSCAdjust(FactorySscSetting model);

    public int queryIsPcMode(int inputSrcType);

    public void updateMonitorITC(int curStatue);

    public int queryMonitorITC();

    public void updatexvYCCEnable(boolean xvYCCEn);

    public boolean queryxvYCCEnable();

    public int queryePicMode(int inputSrcType);

    public int queryColorTmpIdx(int inputSrcType, int ePicture);

    public ColorTempModeSetting queryFactoryColorTempExData();

    public ColorTemperature queryFactoryColorTempExData(int inputsource, int colorTemperatureID);

    public ColorTemperatureExData queryFactoryColorTemperatureExData(int inputsource, int colorTemperatureID);

    public void updateFactoryColorTempExData(ColorTempExData model, int sourceId, int colorTmpId);

    public int queryUserSysSetting(EnumUserSettingField field);

    public void updateUserSysSetting(EnumUserSettingField field, int value);

    public void updateSelfAdaptiveLevel(int value, int inputSrcType);

    public int querySelfAdaptiveLevel(int inputSrcType);

    public void updateSpeakerDelay(int delay);

    public void updateSpdifDelay(int delay);

    public int querySourceIdent();

    public void updateSourceIdent(int currentState);

    public int querySourcePreview();

    public void updateSourcePreview(int currentState);

    public int querySourceSwitch();

    public void updateSourceSwitch(int currentState);

    public int queryDGClarity();

    public void updateDGClarity(int value);

    public int queryIsdbUserSettingATVCCMode();

    public void updateIsdbUserSettingATVCCMode(int ccMode);

    public int queryIsdbUserSettingDTVCCMode();

    public void updateIsdbUserSettingDTVCCMode(int ccMode);

    public void updateCiCamPinCode(int camPinCode);

    public int queryCiCamPinCode();

    public TimeSetting queryTimeSetting();

    public void updateTimeSetting(TimeSetting model);

    public void updateVideoMuteColor(int nColor);

    public int queryVideoMuteColor();

    /**
     * to query if the 7 MHz bandwidth over UHF enabled or not
     *
     * @return boolean true: enabled, false: disabled
     */
    public boolean queryDvbUhf7MhzEnabled();

    /**
     * to set 7 MHz bandwidth over UHF enable or not
     *
     * @param bEnable, true for enable 7 MHz bandwidth over UHF, false otherwise
     */
    public void updateDvbUhf7MhzEnabled(boolean bEnable);
}
