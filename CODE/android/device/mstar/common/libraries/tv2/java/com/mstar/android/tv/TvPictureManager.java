//<MStar Software>
//******************************************************************************
// MStar Software
// Copyright (c) 2010 - 2015 MStar Semiconductor, Inc. All rights reserved.
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

package com.mstar.android.tv;

import android.util.Log;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.ServiceManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import com.mstar.android.tv.ITvPicture;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.mstar.android.tvapi.common.vo.ColorTemperatureExData;
import com.mstar.android.tvapi.common.vo.NoiseReduction.EnumNoiseReduction;
import com.mstar.android.tvapi.common.vo.MpegNoiseReduction.EnumMpegNoiseReduction;
import com.mstar.android.tvapi.common.vo.VideoInfo;
import com.mstar.android.tvapi.common.vo.VideoWindowType;
import com.mstar.android.tvapi.common.vo.EnumColorTemperature;
import com.mstar.android.tvapi.common.vo.EnumVideoArcType;
import com.mstar.android.tvapi.common.vo.Film.EnumFilm;
import com.mstar.android.tvapi.common.vo.EnumPictureMode;
import com.mstar.android.tvapi.common.vo.EnumVideoItem;
import com.mstar.android.tvapi.common.vo.PanelProperty;
import com.mstar.android.tvapi.common.vo.MweType.EnumMweType;
import com.mstar.android.tvapi.common.vo.TimingInfo;

/**
 * <b>TvPictureManager class is for purpose of controlling picture management
 * from client APK.</b><br/>
 */
public class TvPictureManager {
    private final static String TAG = "TvPictureManager";

    /* This value is mapping to EN_MFC_MODE */
    /** Mfc mode Off */
    public static final int MFC_MODE_OFF = 0;
    /** Mfc mode low */
    public static final int MFC_MODE_LOW = 1;
    /** Mfc mode high */
    public static final int MFC_MODE_HIGH = 2;
    /** Mfc mode middle */
    public static final int MFC_MODE_MIDDLE = 3;
    /** Mfc mode bypass */
    public static final int MFC_MODE_BYPASS = 4;

    /* This value is mapping to EN_MS_PIC_NR */
    /** noise reduction off */
    public static final int NR_MODE_OFF = 0;
    /** noise reduction low */
    public static final int NR_MODE_LOW = 1;
    /** noise reduction middle */
    public static final int NR_MODE_MIDDLE = 2;
    /** noise reduction high */
    public static final int NR_MODE_HIGH = 3;
    /** noise reduction auto */
    public static final int NR_MODE_AUTO = 4;

    /* This value is mapping to EN_MS_PIC_MPEG_NR */
    /** Mpeg noise reduction off */
    public static final int MPEG_NR_MODE_OFF = 0;
    /** Mpeg noise reduction low */
    public static final int MPEG_NR_MODE_LOW = 1;
    /** Mpeg noise reduction middle */
    public static final int MPEG_NR_MODE_MIDDLE = 2;
    /** Mpeg noise reduction high */
    public static final int MPEG_NR_MODE_HIGH = 3;

    /* This value is mapping to EN_MS_MWE_TYPE */
    /** MWE demo mode off */
    public static final int MWE_DEMO_MODE_OFF = 0;
    /** MWE demo mode optimize */
    public static final int MWE_DEMO_MODE_OPTIMIZE = 1;
    /** MWE demo mode enhance */
    public static final int MWE_DEMO_MODE_ENHANC = 2;
    /** MWE demo mode side by side */
    public static final int MWE_DEMO_MODE_SIDE_BY_SIDE = 3;
    /** MWE demo mode dynamic compare */
    public static final int MWE_DEMO_MODE_DYNAMICCOMPARE = 4;
    /** MWE demo mode center based scale */
    public static final int MWE_DEMO_MODE_CENTERBASEDSCALE = 5;
    /** MWE demo mode move along */
    public static final int MWE_DEMO_MODE_MOVEALON = 6;
    /** MWE demo mode golden eyes */
    public static final int MWE_DEMO_MODE_GOLDENEYES = 7;
    /** MWE demo mode true color analysis ascension */
    public static final int MWE_DEMO_MODE_TRUE_COLOR_ANALYSIS_ASCENSION = 8;
    /** MWE demo mode led backlight control */
    public static final int MWE_DEMO_MODE_LED_BACKLIGHT_CONTROL = 9;
    /** MWE demo mode high speed movement processing */
    public static final int MWE_DEMO_MODE_HIGH_SPEED_MOVEMENT_PROCESSINGF = 10;
    /** MWE demo mode square move */
    public static final int MWE_DEMO_MODE_SQUAREMOVE = 11;
    /** MWE demo mode reserve for customer1 */
    public static final int MWE_DEMO_MODE_CUSTOMER1 = 12;
    /** MWE demo mode reserve for customer2 */
    public static final int MWE_DEMO_MODE_CUSTOMER2 = 13;
    /** MWE demo mode reserve for customer3 */
    public static final int MWE_DEMO_MODE_CUSTOMER3 = 14;
    /** MWE demo mode reserve for customer4 */
    public static final int MWE_DEMO_MODE_CUSTOMER4 = 15;
    /** MWE demo mode reserve for customer5 */
    public static final int MWE_DEMO_MODE_CUSTOMER5 = 16;
    /** MWE demo mode reserve for customer6 */
    public static final int MWE_DEMO_MODE_CUSTOMER6 = 17;
    /** MWE demo mode reserve for customer7 */
    public static final int MWE_DEMO_MODE_CUSTOMER7 = 18;
    /** MWE demo mode reserve for customer8 */
    public static final int MWE_DEMO_MODE_CUSTOMER8 = 19;

    /** picture brightness */
    public static final int PICTURE_BRIGHTNESS = 0;
    /** picture contrast */
    public static final int PICTURE_CONTRAST = 1;
    /** picture saturation */
    public static final int PICTURE_SATURATION = 2;
    /** picture sharpness */
    public static final int PICTURE_SHARPNESS = 3;
    /** picture hue */
    public static final int PICTURE_HUE = 4;
    /** picture backlight */
    public static final int PICTURE_BACKLIGHT = 5;

    /** picture mode dynamic */
    public static final int PICTURE_MODE_DYNAMIC = 0;
    /** picture mode normal */
    public static final int PICTURE_MODE_NORMAL = 1;
    /** picture mode mild */
    public static final int PICTURE_MODE_SOFT = 2;
    /** picture mode user */
    public static final int PICTURE_MODE_USER = 3;
    /** picture game mode */
    public static final int PICTURE_MODE_GAME = 4;
    /** picture auto mode */
    public static final int PICTURE_MODE_AUTO = 5;
    /** picture pc mode */
    public static final int PICTURE_MODE_PC = 6;
    /** picture mode vivid */
    public static final int PICTURE_MODE_VIVID = 7;
    /** picture mode natural */
    public static final int PICTURE_MODE_NATURAL = 8;
    /** picture mode sports */
    public static final int PICTURE_MODE_SPORTS = 9;

    /* This value is mapping to EN_MS_FILM */
    /** film mode off */
    public static final int FILM_MODE_OFF = 0;
    /** film mode on */
    public static final int FILM_MODE_ON = 1;

    /* This value is mapping to MAPI_VIDEO_ARC_Type */
    /** video ARC type default */
    public static final int VIDEO_ARC_DEFAULT = 0;
    /** video ARC type 16x9 */
    public static final int VIDEO_ARC_16x9 = 1;
    /** video ARC type 4x3 */
    public static final int VIDEO_ARC_4x3 = 2;
    /** video ARC type auto */
    public static final int VIDEO_ARC_AUTO = 3;
    /** video ARC type panorama */
    public static final int VIDEO_ARC_PANORAMA = 4;
    /** video ARC type just scan */
    public static final int VIDEO_ARC_JUSTSCAN = 5;
    /** video ARC type zoom 1 */
    public static final int VIDEO_ARC_ZOOM1 = 6;
    /** video ARC type zoom 2 */
    public static final int VIDEO_ARC_ZOOM2 = 7;
    /** video ARC type 14x9 */
    public static final int VIDEO_ARC_14x9 = 8;
    /** video ARC type point-to-point */
    public static final int VIDEO_ARC_DOTBYDOT = 9;
    /** video ARC type subtitle */
    public static final int VIDEO_ARC_SUBTITLE = 10;
    /** video ARC type movie */
    public static final int VIDEO_ARC_MOVIE = 11;
    /** video ARC type personal */
    public static final int VIDEO_ARC_PERSONAL = 12;
    /** video ARC type 4x3 panorama */
    public static final int VIDEO_ARC_4x3_PANSCAN = 13;
    /** video ARC type letter box */
    public static final int VIDEO_ARC_4x3_LETTERBOX = 14;
    /** video ARC type pillar box */
    public static final int VIDEO_ARC_16x9_PILLARBOX = 15;
    /** video ARC type 16x9 panorama */
    public static final int VIDEO_ARC_16x9_PANSCAN = 16;
    /** video ARC type 4x3 combind */
    public static final int VIDEO_ARC_4x3_COMBIND = 17;
    /** video ARC type 16x9 combind */
    public static final int VIDEO_ARC_16x9_COMBIND = 18;
    /** video ARC type zoom 2x */
    public static final int VIDEO_ARC_Zoom_2x = 19;
    /** video ARC type zoom 3x */
    public static final int VIDEO_ARC_Zoom_3x = 20;
    /** video ARC type zoom 4x */
    public static final int VIDEO_ARC_Zoom_4x = 21;
    /** video ARC type cutomize */
    public static final int VIDEO_ARC_CUSTOMIZE = 32;
    /** video ARC type maximum value */
    public static final int VIDEO_ARC_MAX = 64;

    /* This value is mapping to EN_MS_COLOR_TEMP */
    /** color temperature cool */
    public static final int COLOR_TEMP_COOL = 0;
    /** color temperature standard */
    public static final int COLOR_TEMP_NATURE = 1;
    /** color temperature warm */
    public static final int COLOR_TEMP_WARM = 2;
    /** color temperature user 1 */
    public static final int COLOR_TEMP_USER1 = 3;
    /** color temperature user 2 */
    public static final int COLOR_TEMP_USER2 = 4;

    /* This value is HDMI Color Format */
    /**HDMI RGB 444 Color Format*/
    public static final int HDMI_COLOR_RGB = 0;
    /**HDMI YUV 422 Color Format*/
    public static final int HDMI_COLOR_YUV_422 = 1;
    /**HDMI YUV 444 Color Format*/
    public static final int HDMI_COLOR_YUV_444 = 2;

    static TvPictureManager mInstance = null;

    private static ITvPicture mService = null;

    private TvPictureManager() {
    }

    public static TvPictureManager getInstance() {
        /* Double-checked locking */
        if (mInstance == null) {
            synchronized (TvPictureManager.class) {
                if (mInstance == null) {
                    mInstance = new TvPictureManager();
                }
            }
        }

        return mInstance;
    }

    private static ITvPicture getService() {
        if (mService != null) {
            return mService;
        }

        mService = TvManager.getInstance().getTvPicture();
        return mService;
    }

    /**
     * set Idx of picture mode.
     *
     * @param ePicMode:member of EnumPictureMode,which signed picture mode
     * @return TRUE:Success, or FALSE:failed.
     * @deprecated Use {@link setPictureMode(int pictureMode)}.
     */
    @Deprecated
    public boolean setPictureModeIdx(EnumPictureMode ePicMode) {
        Log.d(TAG, "setPictureModeIdx(), paras ePicMode = " + ePicMode);
        return setPictureMode(ePicMode.ordinal());
    }

    /**
     * Get picture mode idx.
     *
     * @return member of EnumPictureMode which signed picture mode.
     * @deprecated Use {@link getPictureMode()}.
     */
    @Deprecated
    public EnumPictureMode getPictureModeIdx() {
        Log.d(TAG, "getPictureModeIdx()");
        return EnumPictureMode.values()[getPictureMode()];
    }

    /**
     * set picture mode.
     *
     * @param pictureMode
     * <p> The supported type are:
     * <ul>
     * <li> {@link #PICTURE_MODE_DYNAMIC}
     * <li> {@link #PICTURE_MODE_NORMAL}
     * <li> {@link #PICTURE_MODE_SOFT}
     * <li> {@link #PICTURE_MODE_USER}
     * <li> {@link #PICTURE_MODE_GAME}
     * <li> {@link #PICTURE_MODE_AUTO}
     * <li> {@link #PICTURE_MODE_PC}
     * <li> {@link #PICTURE_MODE_VIVID}
     * <li> {@link #PICTURE_MODE_NATURAL}
     * <li> {@link #PICTURE_MODE_SPORTS}
     * </ul>
     *
     * @return boolean TRUE:Success, or FALSE:failed.
     */
    public boolean setPictureMode(int pictureMode) {
        Log.d(TAG, "setPictureMode(), paras pictureMode = " + pictureMode);
        ITvPicture service = getService();
        try {
            return service.setPictureModeIdx(pictureMode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get picture mode
     *
     * @return int current picture mode
     * @see #PICTURE_MODE_DYNAMIC
     * @see #PICTURE_MODE_NORMAL
     * @see #PICTURE_MODE_SOFT
     * @see #PICTURE_MODE_USER
     * @see #PICTURE_MODE_GAME
     * @see #PICTURE_MODE_AUTO
     * @see #PICTURE_MODE_PC
     * @see #PICTURE_MODE_VIVID
     * @see #PICTURE_MODE_NATURAL
     * @see #PICTURE_MODE_SPORTS
     */
    public int getPictureMode() {
        Log.d(TAG, "getPictureMode()");
        ITvPicture service = getService();
        try {
            return service.getPictureModeIdx();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return PICTURE_MODE_NORMAL;

    }

    /**
     * Get pc mode flag.
     *
     * @return zero for pc mode off. no-zero for on.
     */
    public int getIsPcMode() {
        ITvPicture service = getService();
        int en = 0;
        try {
            en = service.getIsPcMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getIsPcMode(), return " + en);
        return en;

    }

    /**
     * Set ITC value.
     *
     * @param ITC
     *
     */
    public void setITC(int ITC) {
        ITvPicture service = getService();
        try {
            service.setITC(ITC);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get ITC value.
     *
     * @return int integer value of ITC.
     */
    public int getITC() {
        ITvPicture service = getService();
        int en = 0;
        try {
            en = service.getITC();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "GetITC(), return " + en);
        return en;
    }

    /**
     * Set video Arc. The enum oridinal value bigger than E_AR_DotByDot including
     * E_AR_Subtitle,
     * E_AR_Movie,
     * E_AR_Personal,
     * E_4x3_PanScan,
     * E_4x3_LetterBox,
     * E_16x9_PillarBox,
     * E_16x9_PanScan,
     * E_4x3_Combind,
     * E_16x9_Combind,
     * E_Zoom_2x,
     * E_Zoom_3x,
     * E_Zoom_4x,
     * currently "not" supported in TV platform.
     *  The following is current supported input source and video arc matrix.
     *                   ATV    DTV    S-Video    CVBS    HDMI/DVI    VGA    YPbPr    Storage
     *  E_DEFAULT          o        o        o              o           o                 o        o           o
     *  E_16x9             o        o        o              o           o                 o        o           o
     *  E_4x3              o        o        o              o           o                 o        o           o
     *  E_AUTO             o        o        o              o           o                 o        o           o
     *  E_Panorama         o        o        o              o           o                 o        o           x
     *  E_JustScan         o        o        o              o           o                 o        o           o
     *  E_Zoom1            o        o        o              o           o                 o        o           o
     *  E_Zoom2            o        o        o              o           o                 o        o           o
     *  E_14x9,
     *  //point to point                                (HDMI RGB,HDMI YUV444)
     *  E_AR_DotByDot      x        x        x              x           o                 o        x           x
     *
     * @param eArcIdx:member of EnumVideoArcType,which signed video arc type.
     * @return boolean TRUE:Success, or FALSE:failed.
     * @deprecated Use {@link setVideoArcType(int arcType)}
     */
    @Deprecated
    public boolean setVideoArc(EnumVideoArcType eArcIdx) {
        Log.d(TAG, "setVideoArc(), paras eArcIdx = " + eArcIdx);
        return setVideoArcType(eArcIdx.ordinal());
    }

    /**
     * Get video arc.
     *
     * @return member of EnumVideoArcType,which signed video arc type.
     * @deprecated Use {@link getVideoArcType()}
     */
    @Deprecated
    public EnumVideoArcType getVideoArc() {
        Log.d(TAG, "getVideoArc()");
        return EnumVideoArcType.values()[getVideoArcType()];
    }

    /**
     * Set video Arc.
     * The enum oridinal value bigger than E_AR_DotByDot including
     * E_AR_Subtitle,
     * E_AR_Movie,
     * E_AR_Personal,
     * E_4x3_PanScan,
     * E_4x3_LetterBox,
     * E_16x9_PillarBox,
     * E_16x9_PanScan,
     * E_4x3_Combind,
     * E_16x9_Combind,
     * E_Zoom_2x,
     * E_Zoom_3x,
     * E_Zoom_4x,
     * currently "not" supported in TV platform.
     *  The following is current supported input source and video arc matrix.
     *                   ATV    DTV    S-Video    CVBS    HDMI/DVI    VGA    YPbPr    Storage
     *  E_DEFAULT          o        o        o              o           o                 o        o           o
     *  E_16x9             o        o        o              o           o                 o        o           o
     *  E_4x3              o        o        o              o           o                 o        o           o
     *  E_AUTO             o        o        o              o           o                 o        o           o
     *  E_Panorama         o        o        o              o           o                 o        o           x
     *  E_JustScan         o        o        o              o           o                 o        o           o
     *  E_Zoom1            o        o        o              o           o                 o        o           o
     *  E_Zoom2            o        o        o              o           o                 o        o           o
     *  E_14x9,
     *  //point to point                                (HDMI RGB,HDMI YUV444)
     *  E_AR_DotByDot      x        x        x              x           o                 o        x           x
     *
     * @param arcType video arc type
     * <p> The supported type are:
     * <ul>
     * <li> {@link #VIDEO_ARC_DEFAULT}
     * <li> {@link #VIDEO_ARC_16x9}
     * <li> {@link #VIDEO_ARC_4x3}
     * <li> {@link #VIDEO_ARC_AUTO}
     * <li> {@link #VIDEO_ARC_PANORAMA}
     * <li> {@link #VIDEO_ARC_JUSTSCAN}
     * <li> {@link #VIDEO_ARC_ZOOM1}
     * <li> {@link #VIDEO_ARC_ZOOM2}
     * <li> {@link #VIDEO_ARC_14x9}
     * <li> {@link #VIDEO_ARC_DOTBYDOT}
     * <li> {@link #VIDEO_ARC_SUBTITLE}
     * <li> {@link #VIDEO_ARC_MOVIE}
     * <li> {@link #VIDEO_ARC_PERSONAL}
     * <li> {@link #VIDEO_ARC_4x3_PANSCAN}
     * <li> {@link #VIDEO_ARC_4x3_LETTERBOX}
     * <li> {@link #VIDEO_ARC_16x9_PILLARBOX}
     * <li> {@link #VIDEO_ARC_16x9_PANSCAN}
     * <li> {@link #VIDEO_ARC_4x3_COMBIND}
     * <li> {@link #VIDEO_ARC_16x9_COMBIND}
     * <li> {@link #VIDEO_ARC_Zoom_2x}
     * <li> {@link #VIDEO_ARC_Zoom_3x}
     * <li> {@link #VIDEO_ARC_Zoom_4x}
     * <li> {@link #VIDEO_ARC_CUSTOMIZE}
     * </ul>
     *
     * @return boolean TRUE:Success, or FALSE:failed.
     */
    public boolean setVideoArcType(int arcType) {
        Log.d(TAG, "setVideoArcType(), paras arcType = " + arcType);
        ITvPicture service = getService();
        try {
            return service.setVideoArc(arcType);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get video arc type.
     *
     * @return int current video arc type
     * @see #VIDEO_ARC_DEFAULT
     * @see #VIDEO_ARC_16x9
     * @see #VIDEO_ARC_4x3
     * @see #VIDEO_ARC_AUTO
     * @see #VIDEO_ARC_PANORAMA
     * @see #VIDEO_ARC_JUSTSCAN
     * @see #VIDEO_ARC_ZOOM1
     * @see #VIDEO_ARC_ZOOM2
     * @see #VIDEO_ARC_14x9
     * @see #VIDEO_ARC_DOTBYDOT
     * @see #VIDEO_ARC_SUBTITLE
     * @see #VIDEO_ARC_MOVIE
     * @see #VIDEO_ARC_PERSONAL
     * @see #VIDEO_ARC_4x3_PANSCAN
     * @see #VIDEO_ARC_4x3_LETTERBOX
     * @see #VIDEO_ARC_16x9_PILLARBOX
     * @see #VIDEO_ARC_16x9_PANSCAN
     * @see #VIDEO_ARC_4x3_COMBIND
     * @see #VIDEO_ARC_16x9_COMBIND
     * @see #VIDEO_ARC_Zoom_2x
     * @see #VIDEO_ARC_Zoom_3x
     * @see #VIDEO_ARC_Zoom_4x
     * @see #VIDEO_ARC_CUSTOMIZE
     */
    public int getVideoArcType() {
        Log.d(TAG, "getVideoArcType()");
        ITvPicture service = getService();
        try {
            return service.getVideoArc();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return VIDEO_ARC_16x9;
    }

    /**
     * Set picture related value depends on EnumVideoItem
     *
     * @param eIndex is defined by EnumVideoItem,
     *               it included brightness, contrast, sharpness, color, tint
     * @param value present indicated video items's value
     * @return boolean true if setting successful, otherwise return false
     * @deprecated Use {@link setVideoItem(int pictureItem, int value)}.
     */
    @Deprecated
    public boolean setVideoItem(EnumVideoItem eIndex, int value) {
        Log.d(TAG, "setVideoItem(), paras eIndex = " + eIndex + ", value = " + value);
        return setVideoItem(eIndex.ordinal(), value);
    }

    /**
     * Get picture related value depends on EnumVideoItem
     *
     * @param eIndex is defined by EnumVideoItem,
     *               it included brightness, contrast, sharpness, color, tint
     * @return int the value what present indicated by eIndex
     * @deprecated Use {@link getVideoItem(int pictureItem)}.
     */
    @Deprecated
    public int getVideoItem(EnumVideoItem eIndex) {
        Log.d(TAG, "getVideoItem(), paras eIndex = " + eIndex);
        return getVideoItem(eIndex.ordinal());
    }

    /**
     * Set value for specified picture mode
     *
     * @param pictureItem item of picture mode
     * <p> The supported type are:
     * <ul>
     * <li> {@link #PICTURE_BRIGHTNESS}
     * <li> {@link #PICTURE_CONTRAST}
     * <li> {@link #PICTURE_SATURATION}
     * <li> {@link #PICTURE_SHARPNESS}
     * <li> {@link #PICTURE_HUE}
     * <li> {@link #PICTURE_BACKLIGHT}
     * </ul>
     * @param value present indicated video items's value
     *
     * @return boolean true if setting successful, otherwise return false
     */
    public boolean setVideoItem(int pictureItem, int value) {
        Log.d(TAG, "setVideoItem(), paras pictureItem = " + pictureItem + ", value = " + value);
        ITvPicture service = getService();
        try {
            return service.setVideoItem(pictureItem, value);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get value for specified picture mode
     *
     * @param pictureItem
     * <p> The supported type are:
     * <ul>
     * <li> {@link #PICTURE_BRIGHTNESS}
     * <li> {@link #PICTURE_CONTRAST}
     * <li> {@link #PICTURE_SATURATION}
     * <li> {@link #PICTURE_SHARPNESS}
     * <li> {@link #PICTURE_HUE}
     * <li> {@link #PICTURE_BACKLIGHT}
     * </ul>
     * @param value present indicated video items's value
     *
     * @return int the value what by present picture mode
     */
    public int getVideoItem(int pictureItem) {
        Log.d(TAG, "getVideoItem(), paras pictureItem = " + pictureItem);
        ITvPicture service = getService();
        int i = -1;
        try {
            i = service.getVideoItem(pictureItem);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getVideoItem(), return int " + i);
        return i;
    }

    /**
     * Set backlight by indicated value
     *
     * @param value Auto Bringhtness ON = Backlight: -50~50; Auto Brightness OFF - Backlight: 0~100
     * @return boolean true: Success  false: Failure
     */
    public boolean setBacklight(int value) {
        Log.d(TAG, "setBacklight(), paras value = " + value);
        ITvPicture service = getService();
        try {
            return service.setBacklight(value);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get backlight
     *
     * @return int Auto Brightness ON - Backlight: -50~50; Auto Brightness OFF - Backlight: 0~100
     */
    public int getBacklight() {
        ITvPicture service = getService();
        int i = -1;
        try {
            i = service.getBacklight();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getBacklight(), return int " + i);
        return i;

    }

    /**
     * Setting the Ex color temperature by current input source
     *
     * @param eColorTemp indicated the color temperature index i
     * @return boolean true if setting successful, otherwise return false
     * @deprecated Use {@link setColorTempratureIdx(int colorTempIdx)}
     */
    @Deprecated
    public boolean setColorTempIdx(EnumColorTemperature eColorTemp) {
        Log.d(TAG, "setColorTempIdx(), paras eColorTemp = " + eColorTemp);
        return setColorTempratureIdx(eColorTemp.getValue());
    }

    /**
     * Get current input source's color temperature i
     *
     * @return EnumColorTemperature current input source's color temperature
     * @deprecated Use {@link getColorTempratureIdx()}
     */
    @Deprecated
    public EnumColorTemperature getColorTempIdx() {
        for (EnumColorTemperature en : EnumColorTemperature.values()) {
            if (en.getValue() == getColorTempratureIdx()) {
                Log.d(TAG, "getColorTempIdx(), return EnumColorTemperature " + en);
                return en;
            }
        }
        return null;
    }

    /**
     * Setting the Ex color temperature by current input source
     *
     * @param colorTemp indicated the color temperature
     * <p> The supported type are:
     * <ul>
     * <li> {@link #COLOR_TEMP_COOL}
     * <li> {@link #COLOR_TEMP_NATURE}
     * <li> {@link #COLOR_TEMP_WARM}
     * <li> {@link #COLOR_TEMP_USER1}
     * <li> {@link #COLOR_TEMP_USER2}
     * </ul>
     *
     * @return boolean true: success, false: fail
     * @deprecated Use {@link setColorTempratureIdx(int colorTempIdx)}
     */
    @Deprecated
    public boolean setColorTemprature(int colorTemp) {
        Log.d(TAG, "setColorTemprature(), paras colorTemp = " + colorTemp);
        ITvPicture service = getService();
        try {
            return service.setColorTempIdx(colorTemp);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get current input source's color temperature
     *
     * @return int current input source's color temperature
     * @see #COLOR_TEMP_COOL
     * @see #COLOR_TEMP_NATURE
     * @see #COLOR_TEMP_WARM
     * @see #COLOR_TEMP_USER1
     * @see #COLOR_TEMP_USER2
     * @deprecated Use {@link getColorTempratureIdx()}
     */
    @Deprecated
    public int getColorTemprature() {
        Log.d(TAG, "getColorTemprature()");
        ITvPicture service = getService();
        try {
            return service.getColorTempIdx();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return COLOR_TEMP_NATURE;
    }


    /**
     * Setting the Ex color temperature by current input source
     *
     * @param colorTemp indicated the color temperature
     * <p> The supported type are:
     * <ul>
     * <li> {@link #COLOR_TEMP_COOL}
     * <li> {@link #COLOR_TEMP_NATURE}
     * <li> {@link #COLOR_TEMP_WARM}
     * <li> {@link #COLOR_TEMP_USER1}
     * <li> {@link #COLOR_TEMP_USER2}
     * </ul>
     *
     * @return boolean true: success, false: fail
     */
    public boolean setColorTempratureIdx(int colorTempIdx) {
        Log.d(TAG, "setColorTempratureIdx(), paras colorTempIdx = " + colorTempIdx);
        ITvPicture service = getService();
        try {
            return service.setColorTempIdx(colorTempIdx);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get current input source's color temperature
     *
     * @return int current input source's color temperature
     * @see #COLOR_TEMP_COOL
     * @see #COLOR_TEMP_NATURE
     * @see #COLOR_TEMP_WARM
     * @see #COLOR_TEMP_USER1
     * @see #COLOR_TEMP_USER2
     */
    public int getColorTempratureIdx() {
        Log.d(TAG, "getColorTempratureIdx()");
        ITvPicture service = getService();
        try {
            return service.getColorTempIdx();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return COLOR_TEMP_NATURE;
    }

    /**
     * Setting current input source color temperature by indicated parameter
     *
     * @param eColorTemp indicated the color temperature index i
     * @return boolean true if setting successful, otherwise return false
     */
    public void setColorTempratureEx(ColorTemperatureExData stColorTempEx) {
        Log.d(TAG, "setColorTemprature");
        Log.d(TAG, "setColorTemprature, paras ColorTemperatureExData stColorTemp.redGain = "
                + stColorTempEx.redGain + ", stColorTemp.blueGain = " + stColorTempEx.blueGain
                + ", stColorTemp.greenGain = " + stColorTempEx.greenGain
                + ", stColorTemp.redOffset = " + stColorTempEx.redOffset
                + ", stColorTemp.blueOffse = " + stColorTempEx.blueOffset
                + ", stColorTemp.greenOffset = " + stColorTempEx.greenOffset);
        ITvPicture service = getService();
        try {
            service.setColorTempratureEx(stColorTempEx);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /**
     * Get current input source color temperature i
     * @return ColorTemperatureExData current input source color temperature
     */
    public ColorTemperatureExData getColorTempratureEx() {
        ITvPicture service = getService();
        ColorTemperatureExData tv = null;
        try {
            tv = service.getColorTempratureEx();
            Log.d(TAG, "getColorTemprature, return ColorTemperatureExData redGain = " + tv.redGain
                    + ", blueGain = " + tv.blueGain + ", greenGain = " + tv.greenGain
                    + ", redOffset = " + tv.redOffset + ", blueOffse = " + tv.blueOffset
                    + ", greenOffset = " + tv.greenOffset);
            ;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return tv;
    }

    /**
     * Update current input source NR setting
     *
     * @param eNRIdx noise reduction NR_OFF, NR_LOW, NR_MIDDLE, NR_HIGH, NR_AUTO
     * @return boolean true if setting successful, otherwise return false
     * @deprecated Use {@link setNoiseReduction(int NR)}.
     */
     @Deprecated
    public boolean setNR(EnumNoiseReduction eNRIdx) {
        Log.d(TAG, "setNR(), paras eNRIdx = " + eNRIdx);
        ITvPicture service = getService();
        try {
            return service.setNR(eNRIdx.getValue());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get current input source NR setting
     * @return EnumNoiseReduction current input source NR
     * setting NR_OFF, NR_LOW, NR_MIDDLE, NR_HIGH, NR_AUTO
     * @deprecated Use {@link getNoiseReduction()}.
     */
    @Deprecated
    public EnumNoiseReduction getNR() {
        ITvPicture service = getService();
        try {
            for (EnumNoiseReduction en : EnumNoiseReduction.values()) {
                if (en.getValue() == service.getNR()) {
                    Log.d(TAG, "getNR(), return en " + en);
                    return en;
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update current input source NR setting
     * <p> The supported type are:
     * <ul>
     * <li> {@link #NR_MODE_OFF}
     * <li> {@link #NR_MODE_LOW}
     * <li> {@link #NR_MODE_MIDDLE}
     * <li> {@link #NR_MODE_HIGH}
     * <li> {@link #NR_MODE_AUTO}
     * </ul>
     *
     * @param NR
     * @see #NR_MODE_OFF
     * @see #NR_MODE_LOW
     * @see #NR_MODE_MIDDLE
     * @see #NR_MODE_HIGH
     * @see #NR_MODE_AUTO
     *
     * @return boolean true if setting successful, otherwise return false
     */
    public boolean setNoiseReduction(int NR) {
        Log.d(TAG, "setNR(), paras NR = " + NR);
        ITvPicture service = getService();
        try {
            return service.setNR(NR);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get current input source NR setting
     * <p> The supported type are:
     * <ul>
     * <li> {@link #NR_MODE_OFF}
     * <li> {@link #NR_MODE_LOW}
     * <li> {@link #NR_MODE_MIDDLE}
     * <li> {@link #NR_MODE_HIGH}
     * <li> {@link #NR_MODE_AUTO}
     * </ul>
     *
     * @return int current input source NR
     * @see #NR_MODE_OFF
     * @see #NR_MODE_LOW
     * @see #NR_MODE_MIDDLE
     * @see #NR_MODE_HIGH
     * @see #NR_MODE_AUTO
     */
    public int getNoiseReduction() {
        ITvPicture service = getService();
        try {
            return service.getNR();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return NR_MODE_OFF;
    }

    /**
     * Update current Mpeg NR setting
     *
     * @param eMpNRIdx noise reduction
     *         E_NR_MPEG_OFF, E_NR_MPEG_LOW, E_NR_MPEG_MIDDLE,
     *         E_NR_MPEG_HIGH, E_NR_MPEG_AUTO
     * @return boolean true if setting successful, otherwise return false
     * @deprecated Use {@link setMpegNoiseReduction(int MpegNR)}.
     */
    @Deprecated
    public boolean setMpegNR(EnumMpegNoiseReduction eMpNRIdx) {
        Log.d(TAG, "setMpegNR(), paras eMpNRIdx = " + eMpNRIdx);
        ITvPicture service = getService();
        try {
            return service.setMpegNR(eMpNRIdx.getValue());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get current Mpeg NR setting
     *
     * @return EnumMpegNoiseReduction current Mpeg NR setting
     *         E_NR_MPEG_OFF, E_NR_MPEG_LOW, E_NR_MPEG_MIDDLE,
     *         E_NR_MPEG_HIGH, E_NR_MPEG_AUTO
     * @deprecated Use {@link getMpegNoiseReduction()}.
     */
    @Deprecated
    public EnumMpegNoiseReduction getMpegNR() {
        ITvPicture service = getService();
        try {
            for (EnumMpegNoiseReduction en : EnumMpegNoiseReduction.values()) {
                if (en.getValue() == service.getMpegNR()) {
                    Log.d(TAG, "getMpegNR(), return en " + en);
                    return en;
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update current input source Mpeg NR setting
     *
     * @param MpegNR noise reduction
     * <p> The supported type are:
     * <ul>
     * <li> {@link #MPEG_NR_MODE_OFF}
     * <li> {@link #MPEG_NR_MODE_LOW}
     * <li> {@link #MPEG_NR_MODE_MIDDLE}
     * <li> {@link #MPEG_NR_MODE_HIGH}
     * </ul>
     *
     * @param MpegNR
     * @see #MPEG_NR_MODE_OFF
     * @see #MPEG_NR_MODE_LOW
     * @see #MPEG_NR_MODE_MIDDLE
     * @see #MPEG_NR_MODE_HIGH
     * @return boolean true if setting successful, otherwise return false
     */
    public boolean setMpegNoiseReduction(int MpegNR) {
        Log.d(TAG, "setMpegNoiseReduction(), paras MpegNR = " + MpegNR);
        ITvPicture service = getService();
        try {
            return service.setMpegNR(MpegNR);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get current input source Mpeg NR setting
     * <p> The supported type are:
     * <ul>
     * <li> {@link #MPEG_NR_MODE_OFF}
     * <li> {@link #MPEG_NR_MODE_LOW}
     * <li> {@link #MPEG_NR_MODE_MIDDLE}
     * <li> {@link #MPEG_NR_MODE_HIGH}
     * </ul>
     *
     * @return int current input source NR
     * @see #MPEG_NR_MODE_OFF
     * @see #MPEG_NR_MODE_LOW
     * @see #MPEG_NR_MODE_MIDDLE
     * @see #MPEG_NR_MODE_HIGH
     */
    public int getMpegNoiseReduction() {
        ITvPicture service = getService();
        try {
            return service.getMpegNR();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return NR_MODE_OFF;
    }



    /**
     * Adjust the horizontal start position at PC mode.
     *
     * @param hpos horizontal start position i
     * @return boolean true if setting successful, otherwise return false
     */
    public boolean setPCHPos(int hpos) {
        Log.d(TAG, "setPCHPos(), paras hpos = " + hpos);
        ITvPicture service = getService();
        try {
            return service.setPCHPos(hpos);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get the horizontal value of start position. i
     * @return int Position in pixel.
     */
    public int getPCHPos() {
        ITvPicture service = getService();
        int i = -1;
        try {
            i = service.getPCHPos();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getPCHPos(), return int " + i);
        return i;

    }

    /**
     * Adjust the vertical start position at PC mode. i
     * @param vpos vertical start position i
     * @return boolean true if setting successful, otherwise return false
     */
    public boolean setPCVPos(int vpos) {
        Log.d(TAG, "setPCVPos(), paras vpos = " + vpos);
        ITvPicture service = getService();
        try {
            return service.setPCVPos(vpos);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get the vertical value of start position. i
     * @return int Position in pixel.
     */
    public int getPCVPos() {
        ITvPicture service = getService();
        int i = -1;
        try {
            i = service.getPCVPos();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getPCVPos(), return int " + i);
        return i;
    }

    /**
     * Get the PC clock value
     *
     * @param clock input clock value i
     * @return boolean true if setting successful,otherwise return false
     */
    public boolean setPCClock(int clock) {
        Log.d(TAG, "setPCClock(), paras clock = " + clock);
        ITvPicture service = getService();
        try {
            return service.setPCClock(clock);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get the PC clock value i
     * @return int PC clock value
     */
    public int getPCClock() {
        ITvPicture service = getService();
        int i = -1;
        try {
            i = service.getPCClock();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getPCClock(), return int " + i);
        return i;
    }

    /**
     * Adjust the ADC phase at PC mode.
     *
     * @param phase ADC phase i
     * @return boolean true if setting successful, otherwise return false
     */
    public boolean setPCPhase(int phase) {
        Log.d(TAG, "setPCPhase(), paras phase = " + phase);
        ITvPicture service = getService();
        try {
            return service.setPCPhase(phase);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get the PC phase value i
     * @return int PC phase value
     */
    public int getPCPhase() {
        ITvPicture service = getService();
        int i = -1;
        try {
            i = service.getPCPhase();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getPCPhase(), return int " + i);
        return i;
    }

    /**
     * Auto tune the screen position at PC mode. i
     * @return boolean true if setting successful, otherwise return false
     */
    public boolean execAutoPc() {
        ITvPicture service = getService();
        boolean ret = false;
        try {
            ret = service.execAutoPc();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "execAutoPc() return = "
+ ret);
        return ret;
    }

    /**
     * To freeze image i
     * @return boolean true if setting successful, otherwise return false
     */
    public boolean freezeImage() {
        ITvPicture service = getService();
        boolean ret = false;
        try {
            ret = service.freezeImage();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "freezeImage() return = " + ret);
        return ret;
    }

    /**
     * To un-freeze image i
     * @return boolean true if setting successful, otherwise return false
     */
    public boolean unFreezeImage() {
        Log.d(TAG, "unFreezeImage()");
        ITvPicture service = getService();
        boolean ret = false;
        try {
            ret = service.unFreezeImage();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "unFreezeImage() return = " + ret);
        return ret;
    }

    /**
     * To get freeze-image setting status
     * @return boolean true: freezed  false: not freezed
     */
    public boolean isImageFreezed() {
        ITvPicture service = getService();
        boolean ret = false;
        try {
            ret = service.isImageFreezed();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "isImageFreezed() return = " + ret);
        return ret;
    }

    /**
     * Set display window region
     *
     * @param videoWindowType present display window region, it include
     *            position(x,y) and width x height
     */
    public void setDisplayWindow(VideoWindowType videoWindowType) {
        Log.d(TAG, "setDisplayWindow, paras VideoWindowType videoWindowType.x = "
                + videoWindowType.x + ", videoWindowType.y = " + videoWindowType.y
                + ", videoWindowType.width = " + videoWindowType.width
                + ", videoWindowType.height = " + videoWindowType.height);
        ITvPicture service = getService();
        try {
            service.setDisplayWindow(videoWindowType);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the information of current video.
     *
     * @return VideoInfo video information by type VideoInfo
     */
    public VideoInfo getVideoInfo() {
        ITvPicture service = getService();
        VideoInfo videoInfo = null;
        try {
            videoInfo = service.getVideoInfo();
            Log.d(TAG, "getVideoInfo, return VideoInfo videoInfo.frameRate = "
                    + videoInfo.frameRate + ", videoInfo.hResolution = " + videoInfo.hResolution
                    + ", videoInfo.modeIndex = " + videoInfo.modeIndex
                    + ", videoInfo.vResolution = " + videoInfo.vResolution);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return videoInfo;
    }

    /**
     * Set HDMI color range index Rang: 0~1, 0: 0-255; 1: 16-235
     *
     * @param value
     * @return boolean true if setting successful, otherwise return false
     */
    public boolean setColorRange(byte value) {
        Log.d(TAG, "setAtvChannel(), paras value = " + value);
        ITvPicture service = getService();
        try {
            return service.setColorRange(value);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get HDMI color range index Rang: 0~1, 0: 0-255; 1: 16-235
     *
     * @return byte color range index
     */
    public byte getColorRange() {
        ITvPicture service = getService();
        byte b = -1;
        try {
            b = service.getColorRange();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getColorRange(), return byte " + b);
        return b;
    }

    /**
     * Set video film mode
     *
     * @param eMode EnumFilm Enumerate of film mode
     * @return boolean true if setting successful, otherwise return false
     * @deprecated Use {@link setFilm(int filmMode)}
     */
    @Deprecated
    public boolean setFilmMode(EnumFilm eMode) {
        Log.d(TAG, "setFilmMode(), paras eMode = " + eMode);
        return setFilm(eMode.ordinal());
    }

    /**
     * Get video film mode
     *
     * @return eMode EnumFilm Enumerate of film mode
     * @deprecated Use {@link getFilm()}
     */
    @Deprecated
    public EnumFilm getFilmMode() {
        Log.d(TAG, "getFilmMode()");
        for (EnumFilm en : EnumFilm.values()) {
            if (en.getValue() == getFilm()) {
                Log.d(TAG, "getFilmMode(), return EnumFilm " + en);
                return en;
            }
        }
        return null;
    }

    /**
     * Set video film mode
     *
     * @param int filmMode
     * <p> The supported type are:
     * <ul>
     * <li> {@link #FILM_MODE_OFF}
     * <li> {@link #FILM_MODE_ON}
     * </ul>
     *
     * @return boolean result true: success , false: fail
     */
    public boolean setFilm(int filmMode) {
        Log.d(TAG, "setFilm(), paras filmMode = " + filmMode);
        ITvPicture service = getService();
        try {
            return service.setFilmMode(filmMode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get video film mode
     *
     * @return int film mode
     * @see #FILM_MODE_OFF
     * @see #FILM_MODE_ON
     */
    public int getFilm() {
        Log.d(TAG, "getFilm()");
        ITvPicture service = getService();
        try {
            return service.getFilmMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return FILM_MODE_OFF;
    }

    /**
     * Enable DLC
     *
     * @return boolean TRUE - enable DLC success, FALSE - enable DLC fail.
     */
    public boolean enableDlc() {
        ITvPicture service = getService();
        boolean ret = false;
        try {
            ret = service.enableDlc();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "enableDlc() return = " + ret);
        return ret;
    }

    /**
     * Disable DLC
     *
     * @return boolean TRUE - disable DLC success, FALSE - disable DLC fail.
     */
    public boolean disableDlc() {
        ITvPicture service = getService();
        boolean ret = false;
        try {
            ret = service.disableDlc();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "disableDlc() return = " + ret);
        return ret;
    }

    /**
     * Check if enable DLC or not
     *
     * @return boolean TRUE - DLC enabled, FALSE - DLC disabled.
     */
    public boolean isDlcEnabled() {
        ITvPicture service = getService();
        boolean ret = false;
        try {
            ret = service.isDlcEnabled();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "isDlcEnabled() return = " + ret);
        return ret;
    }

    /**
     * Enable DCC
     *
     * @return boolean TRUE - enable DCC success, FALSE - enable DCC fail.
     */
    public boolean enableDcc() {
        ITvPicture service = getService();
        boolean ret = false;
        try {
            ret = service.enableDcc();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "enableDcc() return = " + ret);
        return ret;
    }

    /**
     * Disable DCC
     *
     * @return boolean TRUE - disable DCC success, FALSE - disable DCC fail.
     */
    public boolean disableDcc() {
        ITvPicture service = getService();
        boolean ret = false;
        try {
            ret = service.disableDcc();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "disableDcc() return = " + ret);
        return ret;
    }

    /**
     * Check if enable DCC or not
     *
     * @return boolean TRUE - DCC enabled, FALSE - DCC disabled.
     */
    public boolean isDccEnabled() {
        ITvPicture service = getService();
        boolean ret = false;
        try {
            ret = service.isDccEnabled();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "isDccEnabled() return = " + ret);
        return ret;
    }

    /**
     * Enable DBC
     *
     * @return boolean TRUE - enable DBC success, FALSE - enable DBC fail.
     */
    public boolean enableDbc() {
        ITvPicture service = getService();
        boolean ret = false;
        try {
            ret = service.enableDbc();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "enableDbc() return = " + ret);
        return false;
    }

    /**
     * Disable DBC
     *
     * @return Tboolean RUE - disable DBC success, FALSE - disable DBC fail.
     */
    public boolean disableDbc() {
        ITvPicture service = getService();
        boolean ret = false;
        try {
            ret = service.disableDbc();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "disableDbc() return = " + ret);
        return ret;
    }

    /**
     * Check if enable DBC or not
     *
     * @return boolean TRUE - DBC enabled, FALSE - DBC disabled.
     */
    public boolean isDbcEnabled() {
        ITvPicture service = getService();
        boolean ret = false;
        try {
            ret = service.isDbcEnabled();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "isDbcEnabled() return = " + ret);
        return ret;
    }

    /**
     * Setting indicated video itme(brightness, contrast, hue, sat., sharpness,
     * backlight) value by indicated input source
     *
     * @param eIndex present what kind of video item you want to update
     * @param value updated value to indicated video item
     * @param input pesent which input source you want to modify
     * @return boolean TRUE - set videoitem success, FALSE -  set videoitem fail.
     * @deprecated Use {@link setVideoItemByInputSource(int pictureItem, int value, int inputSrc)}.
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public boolean setVideoItemByInputSource(EnumVideoItem eIndex, int value, EnumInputSource input) {
        Log.d(TAG, "setVideoItem(), paras eIndex = " + eIndex + ", value = " + value + ", input = "
                + input);
        return setVideoItemByInputSource(eIndex.ordinal(), value, input.ordinal());
    }

    /**
     * Get indicated video itme(brightness, contrast, hue, sat., sharpness,
     * backlight) value by indicated input source
     *
     * @param eIndex present what kind of video item you want to get
     * @param input pesent which input source you want to modify
     * @return int value from indicated video item
     * @deprecated Use {@link getVideoItemByInputSource(int pictureItem, int inputSrc)}.
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public int getVideoItemByInputSource(EnumVideoItem eIndex, EnumInputSource input) {
        Log.d(TAG, "getVideoItem(), paras eIndex = " + eIndex + ", input = " + input);
        return getVideoItemByInputSource(eIndex.ordinal(), input.ordinal());
    }

    /**
     * Setting indicated video itme(brightness, contrast, hue, sat., sharpness,
     * backlight) value by indicated input source
     *
     * @param pictureItem present what kind of video item you want to update
     * <p> The supported type are:
     * <ul>
     * <li> {@link #PICTURE_BRIGHTNESS}
     * <li> {@link #PICTURE_CONTRAST}
     * <li> {@link #PICTURE_SATURATION}
     * <li> {@link #PICTURE_SHARPNESS}
     * <li> {@link #PICTURE_HUE}
     * <li> {@link #PICTURE_BACKLIGHT}
     * </ul>
     * @param value updated value to indicated video item
     * @param inputSrc present which input source you want to modify
     * <p> The supported type are:
     * <ul>
     * <li> {@link TvCommonManager#INPUT_SOURCE_VGA}
     * <li> {@link TvCommonManager#INPUT_SOURCE_ATV}
     * <li> {@link TvCommonManager#INPUT_SOURCE_CVBS}
     * <li> {@link TvCommonManager#INPUT_SOURCE_CVBS2}
     * <li> {@link TvCommonManager#INPUT_SOURCE_CVBS3}
     * <li> {@link TvCommonManager#INPUT_SOURCE_CVBS4}
     * <li> {@link TvCommonManager#INPUT_SOURCE_CVBS5}
     * <li> {@link TvCommonManager#INPUT_SOURCE_CVBS6}
     * <li> {@link TvCommonManager#INPUT_SOURCE_CVBS7}
     * <li> {@link TvCommonManager#INPUT_SOURCE_CVBS8}
     * <li> {@link TvCommonManager#INPUT_SOURCE_CVBS3}
     * <li> {@link TvCommonManager#INPUT_SOURCE_CVBS4}
     * <li> {@link TvCommonManager#INPUT_SOURCE_SVIDEO}
     * <li> {@link TvCommonManager#INPUT_SOURCE_SVIDEO2}
     * <li> {@link TvCommonManager#INPUT_SOURCE_SVIDEO3}
     * <li> {@link TvCommonManager#INPUT_SOURCE_SVIDEO4}
     * <li> {@link TvCommonManager#INPUT_SOURCE_YPBPR}
     * <li> {@link TvCommonManager#INPUT_SOURCE_YPBPR2}
     * <li> {@link TvCommonManager#INPUT_SOURCE_YPBPR3}
     * <li> {@link TvCommonManager#INPUT_SOURCE_SCART}
     * <li> {@link TvCommonManager#INPUT_SOURCE_SCART2}
     * <li> {@link TvCommonManager#INPUT_SOURCE_HDMI}
     * <li> {@link TvCommonManager#INPUT_SOURCE_HDMI2}
     * <li> {@link TvCommonManager#INPUT_SOURCE_HDMI3}
     * <li> {@link TvCommonManager#INPUT_SOURCE_HDMI4}
     * <li> {@link TvCommonManager#INPUT_SOURCE_DTV}
     * <li> {@link TvCommonManager#INPUT_SOURCE_DVI}
     * <li> {@link TvCommonManager#INPUT_SOURCE_DVI2}
     * <li> {@link TvCommonManager#INPUT_SOURCE_DVI3}
     * <li> {@link TvCommonManager#INPUT_SOURCE_DVI4}
     * <li> {@link TvCommonManager#INPUT_SOURCE_STORAGE}
     * <li> {@link TvCommonManager#INPUT_SOURCE_KTV}
     * <li> {@link TvCommonManager#INPUT_SOURCE_JPEG}
     * <li> {@link TvCommonManager#INPUT_SOURCE_STORAGE2}
     * <li> {@link TvCommonManager#INPUT_SOURCE_VGA2}
     * <li> {@link TvCommonManager#INPUT_SOURCE_VGA3}
     * </ul>
     * @return boolean TRUE - set videoitem success, FALSE -  set videoitem fail.
     */
    public boolean setVideoItemByInputSource(int pictureItem, int value, int inputSrc) {
        Log.d(TAG, "setVideoItemByInputSource(), paras pictureItem = " + pictureItem + ", value = " + value + ", inputSrc = "
                + inputSrc);
        ITvPicture service = getService();
        boolean ret = false;
        try {
            ret = service.setVideoItemByInputSource(pictureItem, value, inputSrc);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "setVideoItemByInputSource() return = " + ret);
        return ret;
    }

    /**
     * Get indicated video itme(brightness, contrast, hue, sat., sharpness,
     * backlight) value by indicated input source
     *
     * @param pictureItem present what kind of video item you want to get
     * <p> The supported type are:
     * <ul>
     * <li> {@link #PICTURE_BRIGHTNESS}
     * <li> {@link #PICTURE_CONTRAST}
     * <li> {@link #PICTURE_SATURATION}
     * <li> {@link #PICTURE_SHARPNESS}
     * <li> {@link #PICTURE_HUE}
     * <li> {@link #PICTURE_BACKLIGHT}
     * </ul>
     * @param inputSrc present which input source you want to modify
     * <p> The supported type are:
     * <ul>
     * <li> {@link TvCommonManager#INPUT_SOURCE_VGA}
     * <li> {@link TvCommonManager#INPUT_SOURCE_ATV}
     * <li> {@link TvCommonManager#INPUT_SOURCE_CVBS}
     * <li> {@link TvCommonManager#INPUT_SOURCE_CVBS2}
     * <li> {@link TvCommonManager#INPUT_SOURCE_CVBS3}
     * <li> {@link TvCommonManager#INPUT_SOURCE_CVBS4}
     * <li> {@link TvCommonManager#INPUT_SOURCE_CVBS5}
     * <li> {@link TvCommonManager#INPUT_SOURCE_CVBS6}
     * <li> {@link TvCommonManager#INPUT_SOURCE_CVBS7}
     * <li> {@link TvCommonManager#INPUT_SOURCE_CVBS8}
     * <li> {@link TvCommonManager#INPUT_SOURCE_CVBS3}
     * <li> {@link TvCommonManager#INPUT_SOURCE_CVBS4}
     * <li> {@link TvCommonManager#INPUT_SOURCE_SVIDEO}
     * <li> {@link TvCommonManager#INPUT_SOURCE_SVIDEO2}
     * <li> {@link TvCommonManager#INPUT_SOURCE_SVIDEO3}
     * <li> {@link TvCommonManager#INPUT_SOURCE_SVIDEO4}
     * <li> {@link TvCommonManager#INPUT_SOURCE_YPBPR}
     * <li> {@link TvCommonManager#INPUT_SOURCE_YPBPR2}
     * <li> {@link TvCommonManager#INPUT_SOURCE_YPBPR3}
     * <li> {@link TvCommonManager#INPUT_SOURCE_SCART}
     * <li> {@link TvCommonManager#INPUT_SOURCE_SCART2}
     * <li> {@link TvCommonManager#INPUT_SOURCE_HDMI}
     * <li> {@link TvCommonManager#INPUT_SOURCE_HDMI2}
     * <li> {@link TvCommonManager#INPUT_SOURCE_HDMI3}
     * <li> {@link TvCommonManager#INPUT_SOURCE_HDMI4}
     * <li> {@link TvCommonManager#INPUT_SOURCE_DTV}
     * <li> {@link TvCommonManager#INPUT_SOURCE_DVI}
     * <li> {@link TvCommonManager#INPUT_SOURCE_DVI2}
     * <li> {@link TvCommonManager#INPUT_SOURCE_DVI3}
     * <li> {@link TvCommonManager#INPUT_SOURCE_DVI4}
     * <li> {@link TvCommonManager#INPUT_SOURCE_STORAGE}
     * <li> {@link TvCommonManager#INPUT_SOURCE_KTV}
     * <li> {@link TvCommonManager#INPUT_SOURCE_JPEG}
     * <li> {@link TvCommonManager#INPUT_SOURCE_STORAGE2}
     * <li> {@link TvCommonManager#INPUT_SOURCE_VGA2}
     * <li> {@link TvCommonManager#INPUT_SOURCE_VGA3}
     * </ul>
     * @return boolean value from indicated video item
     */
    public int getVideoItemByInputSource(int pictureItem, int inputSrc) {
        Log.d(TAG, "getVideoItemByInputSource(), paras pictureItem = " + pictureItem + ", inputSrc = " + inputSrc);
        ITvPicture service = getService();
        int i = -1;
        try {
            i = service.getVideoItemByInputSource(pictureItem, inputSrc);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getVideoItem(), return int = " + i);
        return i;
    }

    /**
     * To turns on backlight
     */
    public void enableBacklight() {
        Log.d(TAG, "enableBacklight");
        ITvPicture service = getService();
        try {
            service.enableBacklight();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * To turns off backlight
     */
    public void disableBacklight() {
        Log.d(TAG, "disableBacklight");
        ITvPicture service = getService();
        try {
            service.disableBacklight();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * To set video resolution
     *
     * @param resolution presented by enum EN_DISPLAY_RES_TYPE
     */
    public void SetResolution(byte resloution) {
        Log.d(TAG, "SetResolution() resolution = " + resloution);
        ITvPicture service = getService();
        try {
            service.setResolution(resloution);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * To get video resolution
     *
     * @return byte resolution presented by enum EN_DISPLAY_RES_TYPE
     */
    public byte GetResloution() {
        ITvPicture service = getService();
        byte ret = 0x00;
        try {
            ret = service.getResolution();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "GetResloution() return = " + ret);
        return ret;
    }

    /**
     * [SET-BOX only] set OSD ratio
     *
     * @param ratio
     */
    public void SetReproduce(int rate) {
        Log.d(TAG, "SetReproduce()");
        ITvPicture service = getService();
        try {
            service.setReproduceRate(rate);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * [SET-BOX only] Get OSD ratio
     *
     * @return ratio
     */
    public int GetReproduce() {
        ITvPicture service = getService();
        int ret = 0;
        try {
            ret = service.getReproduceRate();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "GetReproduce() return = " + ret);
        return ret;
    }

    /**
     * Setting MEMC mode if 4k2k or Ursa mode turns on
     *
     * @param interfaceCommand to turns on MEMC mode, and the string included as following
     *           4K_MEMC_DEMO_OFF_
     *           4K_MEMC_DEMO_LEFT_RIGHT_
     *           4K_MEMC_DEMO_FULL_SCREEN_
     *           4K_MEMC_MODE_OFF_
     *           4K_MEMC_MODE_LOW_
     *           4K_MEMC_MODE_MID_
     *           4K_MEMC_MODE_HIGH_
     *           IMAGE_ENHANCEMENT_PROCESSING_OFF_MODE
     *           IMAGE_ENHANCEMENT_PROCESSING_ON_MODE
     *           IMAGE_ENHANCEMENT_PROCESSING_DEMO_MODE
     * @return boolean true if function exis, otherwise false
     */
    public boolean setMEMCMode(String interfaceCommand) {
        ITvPicture service = getService();
        boolean ret = false;
        try {
            ret = service.setMEMCMode(interfaceCommand);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "setMEMCMode() return = " + ret);
        return ret;
    }

    /**
     * getPcModeInfo
     *
     * @return int PCClock PCPhase PCHPos PCVPos.
     */
    public int[] getPcModeInfo() {
        Log.d(TAG, "getPcModeInfo()");
        ITvPicture service = getService();
        try {
            return service.getPcModeInfo();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get Panel Width and Height
     *
     * @return PanelPropertyVO
     */
    public PanelProperty getPanelWidthHeight() {
        ITvPicture service = getService();
        try {
            PanelProperty pnlPty = service.getPanelWidthHeight();
            Log.d(TAG, "get Panel Width = " + pnlPty.width
                + " Height = " + pnlPty.height);
            return pnlPty;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets demo mode.
     *
     * @param enMweType EnumMweType mwe type
     * @deprecated Use {@link setMWEDemoMode(int demoMode)}.
     */
    @Deprecated
    public void setDemoMode(EnumMweType enMweType) {
        Log.d(TAG, "setDemoMode() MweType = " + enMweType);
        setMWEDemoMode(enMweType.ordinal());
    }

    /**
     * Get system in demo mode
     *
     * @return EnumMweType
     * @deprecated Use {@link getMWEDemoMode()}.
     */
    @Deprecated
    public EnumMweType getDemoMode() {
        return EnumMweType.values()[getMWEDemoMode()];
    }

    /**
     * Set MWE demo mode
     * <p> The supported type are:
     * <ul>
     * <li> {@link #MWE_DEMO_MODE_OFF}
     * <li> {@link #MWE_DEMO_MODE_OPTIMIZE}
     * <li> {@link #MWE_DEMO_MODE_ENHANC}
     * <li> {@link #MWE_DEMO_MODE_SIDE_BY_SIDE}
     * <li> {@link #MWE_DEMO_MODE_DYNAMICCOMPARE}
     * <li> {@link #MWE_DEMO_MODE_CENTERBASEDSCALE}
     * <li> {@link #MWE_DEMO_MODE_MOVEALON}
     * <li> {@link #MWE_DEMO_MODE_GOLDENEYES}
     * <li> {@link #MWE_DEMO_MODE_TRUE_COLOR_ANALYSIS_ASCENSION}
     * <li> {@link #MWE_DEMO_MODE_LED_BACKLIGHT_CONTROL}
     * <li> {@link #MWE_DEMO_MODE_HIGH_SPEED_MOVEMENT_PROCESSINGF}
     * <li> {@link #MWE_DEMO_MODE_SQUAREMOVE}
     * <li> {@link #MWE_DEMO_MODE_CUSTOMER1}
     * <li> {@link #MWE_DEMO_MODE_CUSTOMER2}
     * <li> {@link #MWE_DEMO_MODE_CUSTOMER3}
     * <li> {@link #MWE_DEMO_MODE_CUSTOMER4}
     * <li> {@link #MWE_DEMO_MODE_CUSTOMER5}
     * <li> {@link #MWE_DEMO_MODE_CUSTOMER6}
     * <li> {@link #MWE_DEMO_MODE_CUSTOMER7}
     * <li> {@link #MWE_DEMO_MODE_CUSTOMER8}
     * <li> {@link #MWE_DEMO_MODE_DEFAULT}
     * </ul>
     *
     * @param demoMode
     * @see #MWE_DEMO_MODE_OFF
     * @see #MWE_DEMO_MODE_OPTIMIZE
     * @see #MWE_DEMO_MODE_ENHANC
     * @see #MWE_DEMO_MODE_SIDE_BY_SIDE
     * @see #MWE_DEMO_MODE_DYNAMICCOMPARE
     * @see #MWE_DEMO_MODE_CENTERBASEDSCALE
     * @see #MWE_DEMO_MODE_MOVEALON
     * @see #MWE_DEMO_MODE_GOLDENEYES
     * @see #MWE_DEMO_MODE_TRUE_COLOR_ANALYSIS_ASCENSION
     * @see #MWE_DEMO_MODE_LED_BACKLIGHT_CONTROL
     * @see #MWE_DEMO_MODE_HIGH_SPEED_MOVEMENT_PROCESSINGF
     * @see #MWE_DEMO_MODE_SQUAREMOVE
     * @see #MWE_DEMO_MODE_CUSTOMER1
     * @see #MWE_DEMO_MODE_CUSTOMER2
     * @see #MWE_DEMO_MODE_CUSTOMER3
     * @see #MWE_DEMO_MODE_CUSTOMER4
     * @see #MWE_DEMO_MODE_CUSTOMER5
     * @see #MWE_DEMO_MODE_CUSTOMER6
     * @see #MWE_DEMO_MODE_CUSTOMER7
     * @see #MWE_DEMO_MODE_CUSTOMER8
     * @see #MWE_DEMO_MODE_DEFAULT
     *
     */
    public void setMWEDemoMode(int demoMode) {
        Log.d(TAG, "setMWEDemoMode(), paras demoMode = " + demoMode);
        ITvPicture service = getService();
        try {
            service.setDemoMode(demoMode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get current MWE demo mode
     * <p> The supported type are:
     * <ul>
     * <li> {@link #MWE_DEMO_MODE_OFF}
     * <li> {@link #MWE_DEMO_MODE_OPTIMIZE}
     * <li> {@link #MWE_DEMO_MODE_ENHANC}
     * <li> {@link #MWE_DEMO_MODE_SIDE_BY_SIDE}
     * <li> {@link #MWE_DEMO_MODE_DYNAMICCOMPARE}
     * <li> {@link #MWE_DEMO_MODE_CENTERBASEDSCALE}
     * <li> {@link #MWE_DEMO_MODE_MOVEALON}
     * <li> {@link #MWE_DEMO_MODE_GOLDENEYES}
     * <li> {@link #MWE_DEMO_MODE_TRUE_COLOR_ANALYSIS_ASCENSION}
     * <li> {@link #MWE_DEMO_MODE_LED_BACKLIGHT_CONTROL}
     * <li> {@link #MWE_DEMO_MODE_HIGH_SPEED_MOVEMENT_PROCESSINGF}
     * <li> {@link #MWE_DEMO_MODE_SQUAREMOVE}
     * <li> {@link #MWE_DEMO_MODE_CUSTOMER1}
     * <li> {@link #MWE_DEMO_MODE_CUSTOMER2}
     * <li> {@link #MWE_DEMO_MODE_CUSTOMER3}
     * <li> {@link #MWE_DEMO_MODE_CUSTOMER4}
     * <li> {@link #MWE_DEMO_MODE_CUSTOMER5}
     * <li> {@link #MWE_DEMO_MODE_CUSTOMER6}
     * <li> {@link #MWE_DEMO_MODE_CUSTOMER7}
     * <li> {@link #MWE_DEMO_MODE_CUSTOMER8}
     * <li> {@link #MWE_DEMO_MODE_DEFAULT}
     * </ul>
     *
     * @return int current demo mode
     * @see #MWE_DEMO_MODE_OFF
     * @see #MWE_DEMO_MODE_OPTIMIZE
     * @see #MWE_DEMO_MODE_ENHANC
     * @see #MWE_DEMO_MODE_SIDE_BY_SIDE
     * @see #MWE_DEMO_MODE_DYNAMICCOMPARE
     * @see #MWE_DEMO_MODE_CENTERBASEDSCALE
     * @see #MWE_DEMO_MODE_MOVEALON
     * @see #MWE_DEMO_MODE_GOLDENEYES
     * @see #MWE_DEMO_MODE_TRUE_COLOR_ANALYSIS_ASCENSION
     * @see #MWE_DEMO_MODE_LED_BACKLIGHT_CONTROL
     * @see #MWE_DEMO_MODE_HIGH_SPEED_MOVEMENT_PROCESSINGF
     * @see #MWE_DEMO_MODE_SQUAREMOVE
     * @see #MWE_DEMO_MODE_CUSTOMER1
     * @see #MWE_DEMO_MODE_CUSTOMER2
     * @see #MWE_DEMO_MODE_CUSTOMER3
     * @see #MWE_DEMO_MODE_CUSTOMER4
     * @see #MWE_DEMO_MODE_CUSTOMER5
     * @see #MWE_DEMO_MODE_CUSTOMER6
     * @see #MWE_DEMO_MODE_CUSTOMER7
     * @see #MWE_DEMO_MODE_CUSTOMER8
     * @see #MWE_DEMO_MODE_DEFAULT
     */
    public int getMWEDemoMode() {
        ITvPicture service = getService();
        try {
            return service.getDemoMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return MWE_DEMO_MODE_OFF;
    }

    /**
     * Is 4K2K Mode
     * @param bEn useless
     * @return boolean
     *
     */
    public boolean is4K2KMode(boolean bEn) {
        ITvPicture service = getService();
        boolean ret = false;
        try {
            ret = service.is4K2KMode(bEn);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "is4K2KMode() return = " + ret);
        return ret;
    }

    /**
     * get Reproduce Rate
     * @return int Rate
     */
    public int getReproduceRate() {
        ITvPicture service = getService();
        int ret = 0;
        try {
            ret = service.getReproduceRate();
            Log.d(TAG, "getReproduceRate return = " + ret);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * set UClear Status on/off
     * @param bFlag true: on  false: off
     */
    public void setUClearStatus(boolean bFlag) {
        Log.d(TAG, "setUClearStatus bFlag = " + bFlag);
        ITvPicture service = getService();
        try {
            service.setUClearStatus(bFlag);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * get UClear Status
     * @return boolean true: on  false: off
     */
    public boolean isUClearOn() {
        ITvPicture service = getService();
        boolean ret = false;
        try {
            ret = service.isUClearOn();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
            Log.d(TAG, "isUClearOn return " + ret);
        return ret;
    }

    /**
     * enable Xvycc Compensation
     * @param bEn: true :on false:off,
     * @param eWin: 0:mainwindow 1:subwindow
     * @return boolean true: success  false: failure
     */
    public boolean enableXvyccCompensation(boolean bEn, int eWin) {
        Log.d(TAG, "enableXvyccCompensation bEn = " + bEn + " eWin = " + eWin);
        ITvPicture service = getService();
        boolean ret = false;
        try {
            ret = service.enableXvyccCompensation(bEn, eWin);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "enableXvyccCompensation return = " + ret);
        return ret;
    }

    /**
     * set xvYCC Enable
     * @param bEn: true :on false:off,
     * @param eMode: 0: normal 1: xvycc 2: sRGB mode
     * @return boolean true: success  false: failure
     */
    public boolean setxvYCCEnable(boolean bEn, int eMode) {
        Log.d(TAG, "setxvYCCEnable bEn = " + bEn + " eMode = " + eMode);
        ITvPicture service = getService();
        boolean ret = false;
        try {
            ret = service.setxvYCCEnable(bEn, eMode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "setxvYCCEnable return = " + ret);
        return ret;
    }

    /**
     * get xvYCC Enable
     * @return boolean xvYCCEnable
     */
    public boolean getxvYCCEnable() {
        ITvPicture service = getService();
        boolean ret = false;
        try {
            ret = service.getxvYCCEnable();
            Log.d(TAG, "getxvYCCEnable return = " + ret);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * get HDMI Color Format
     * @return int The current scale value in the range 0~2; RGB , YUV_422 , YUV_444
     * @see #HDMI_COLOR_RGB
     * @see #HDMI_COLOR_YUV_422
     * @see #HDMI_COLOR_YUV_444
     */
    public int getHDMIColorFormat() {
        ITvPicture service = getService();
        int ret = 0;
        try {
            ret = service.getHDMIColorFormat();
            Log.d(TAG, "getHDMIColorFormat return = " + ret);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * set dynamic backlight control thread status
     * @param bFlag: true :run false:sleep
     */
    public void setDynamicBackLightThreadSleep(boolean bFlag) {
        Log.d(TAG, "setDynamicBackLightThreadSleep bFlag = " + bFlag);
        ITvPicture service = getService();
        try {
            service.forceThreadSleep(bFlag);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * turn Off Local Dimming Backlight
     * @param bOffFlag: true :Turn OFF LDB false:Tunr ON LDB
     * @return boolean true:success  false: failure
     */
    public boolean turnOffLocalDimmingBacklight(boolean bOffFlag) {
        Log.d(TAG, "turnOffLocalDimmingBacklight bOffFlag = " + bOffFlag);
        ITvPicture service = getService();
        boolean ret = false;
        try {
            ret = service.turnOffLocalDimmingBacklight(bOffFlag);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ret;
    }


    /**
     * Set mfc mode
     *
     * @param eMode mfc mode
     * <p> The supported type are:
     * <ul>
     * <li> {@link #MFC_MODE_OFF}
     * <li> {@link #MFC_MODE_LOW}
     * <li> {@link #MFC_MODE_HIGH}
     * <li> {@link #MFC_MODE_MIDDLE}
     * <li> {@link #MFC_MODE_BYPASS}
     * </ul>
     */
    public void setMfcMode(int eMode) {
        Log.d(TAG, "setMfcMode(), paras eMode = " + eMode);
        ITvPicture service = getService();
        try {
            service.setMfcMode(eMode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Query is Zooming Operation Supported
     *
     * return boolean false: unsupported, true: supported
     */
    public boolean isSupportedZoom() {
        ITvPicture service = getService();
        try {
            return service.isSupportedZoom();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get aspect ration List
     *
     * return boolean array: supported aspect ration types list
     */
    public boolean[] getAspectRationList() {
        ITvPicture service = getService();
        try {
            return service.getAspectRationList();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get supported timing list count
     *
     * @return Supported timing list count
     * @throws TvCommonException
     */
    public int getSupportedTimingListCount() {
        ITvPicture service = getService();
        try {
            return service.getSupportedTimingListCount();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get current timing id
     *
     * @return Current timing id
     * @throws TvCommonException
     */
    public int getCurrentTimingId() {
        ITvPicture service = getService();
        try {
            return service.getCurrentTimingId();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get supported timing list
     *
     * @return Supported timing list
     * @throws TvCommonException
     */
    public TimingInfo[] getSupportedTimingList() {
        ITvPicture service = getService();
        try {
            return service.getSupportedTimingList();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }
	
	// EosTek Patch Begin
	public boolean setPictureValue(int curPicMode, int lastPicMode, int index, int value) {
        ITvPicture service = getService();
        try {
            return service.setPictureValue(curPicMode, lastPicMode, index, value);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

	public boolean setColorTempIdx(int pictureModeType,int eColorTemp, EnumInputSource curSource) {
        ITvPicture service = getService();
        try {
            return service.setColorTempIdxAndRGB(pictureModeType, eColorTemp, curSource.ordinal());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

	public boolean setColorTempIdxAndRGB(int pictureModeType,int eColorTemp, int curSource){
		ITvPicture service = getService();
        try {
            return service. setColorTempIdxAndRGB(pictureModeType, eColorTemp, curSource);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
	}

	public ColorTemperatureExData getWbGainOffsetEx(int eColorTemp, int curSource){
		ITvPicture service = getService();
        try {
            return service. getWbGainOffsetEx(eColorTemp, curSource);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
	}

	public void setWbGainOffsetEx(ColorTemperatureExData colorTempExData, int eColorTemp, int curSource){
		ITvPicture service = getService();
        try {
            service.setWbGainOffsetEx(colorTempExData, eColorTemp, curSource);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
	}

	/**
	 *	values[0]:Brightness,values[1]:Contrast,values[2]:Hue,values[3]:Saturation,values[4]:Sharpness,values[5]:Backlight
	 */
	public int[] getVideoItems(EnumInputSource inputSrc,EnumPictureMode pictureMode){
		ITvPicture service = getService();
        try {
            return service.getVideoItems(inputSrc.ordinal(), pictureMode.ordinal());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		return null;
	}

	/**
	 *	values[0]:pcClock,values[1]:pcPhase,values[2]:pcHposition,values[3]:pcVposition
	 */
	public int[] getPCImage(){
		ITvPicture service = getService();
        try {
            return service.getPCImage();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
		return null;
	}
	// EosTek Patch End
}
