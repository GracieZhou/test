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

package com.mstar.android.tvapi.common;

import java.lang.ref.WeakReference;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.Film.EnumFilm;
import com.mstar.android.tvapi.common.vo.GetPixelRgbStage.EnumGetPixelRgbStage;
import com.mstar.android.tvapi.common.vo.MpegNoiseReduction.EnumMpegNoiseReduction;
import com.mstar.android.tvapi.common.vo.MweType.EnumMweType;
import com.mstar.android.tvapi.common.vo.*;
import com.mstar.android.tvapi.common.vo.NoiseReduction.EnumNoiseReduction;
import com.mstar.android.tvapi.common.vo.SetLocationType.EnumSetLocationType;
import com.mstar.android.tvapi.common.vo.EnumMfcMode;
import com.mstar.android.tvapi.common.vo.EnumVideoArcType;
import android.os.SystemProperties;
import com.mstar.android.tvapi.common.vo.ScreenPixelInfo;
import com.mstar.android.tvapi.common.vo.ScreenPixelInfo.EnumPixelRGBStage;
import com.mstar.android.tvapi.common.listener.OnPictureEventListener;
import com.mstar.android.tvapi.common.vo.EnumPanelTiming;
import com.mstar.android.tvapi.common.vo.EnumUrsaMode;
import com.mstar.android.tvapi.common.vo.TimingInfo;

/**
 * <p>
 * Title: PictureManager
 * </p>
 * <p>
 * Description: This class provides the functions to manipulate picture quality.
 * </p>
 * ----------------------------------------------------------------------------
 * --------------------<br>
 * [Sample 1] sample code to get PictureManager and manipulate PictureManager
 * class<br>
 * ----------------------------------------------------------------------------
 * --------------------<br>
 * <br>
 * ...<br>
 * private PictureManager pm = TvManager.getPictureManager();<br>
 * <br>
 * try<br>
 * {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp; pm.enableDlc();<br>
 * }<br>
 * catch (TvCommonException e)<br>
 * {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp; e.printStackTrace();<br>
 * }<br>
 * ...<br>
 * <br>
 * <br>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Mstarsemi Inc.
 * </p>
 *
 * @author Jacky.Lin
 * @version 1.0
 */
public final class PictureManager {

    /*
     * parameters for panelInitial
     */
    public static final String FHD_40_NON_DS = "FullHD40";
    public static final String FHD_46_NON_DS = "FullHD46";
    public static final String FHD_52_DS = "DoubleScan52";
    public static final String FHD_60_DS = "DoubleScan60";
    public static final String FHD_70_DS = "DoubleScan70";
    public static final String WXGA32 = "Wxga32";
    public static final String FHD_40_INX = "InxFullHD40";
    public static final String FHD_50_INX = "InxFullHD50";

    /*
     * key-map parameters for setCustomerGammaParameter
     * key: INDEX_xxx
     * value: VALUE_xxx
     */
    public static final int INDEX_IN_POINT_LOW = 0;
    public static final int INDEX_IN_POINT_HIGH = 1;
    public static final int INDEX_R_LV_LOW = 2;
    public static final int INDEX_G_LV_LOW = 3;
    public static final int INDEX_B_LV_LOW = 4;
    public static final int INDEX_R_LV_HIGH = 5;
    public static final int INDEX_G_LV_HIGH = 6;
    public static final int INDEX_B_LV_HIGH = 7;
    public static final int INDEX_R_LV_MAX = 8;
    public static final int INDEX_G_LV_MAX = 9;
    public static final int INDEX_B_LV_MAX = 10;
    public static final int INDEX_LOW_TEMP_OFFSET = 11;
    public static final int INDEX_LOW_LEVEL_FIX = 12;
    public static final int INDEX_HIGH_LEVEL_FIX = 13;
    public static final int INDEX_MAX_LEVEL_FIX = 14;

    public static final int VALUE_ENABLE = 1;
    public static final int VALUE_DISABLE = -1;

    /*
     * parameters for map mode
     */
    public static final int MODE_8_BIT = 0;
    public static final int MODE_10_BIT = 1;

    protected enum EVENT {
        EV_SET_ASPECTRATIO,
        EV_4K2K_PHOTO_DISABLE_PIP,
        EV_4K2K_PHOTO_DISABLE_POP,
        EV_4K2K_PHOTO_DISABLE_DUALVIEW,
        EV_4K2K_PHOTO_DISABLE_TRAVELINGMODE,
        EV_MAX
    }

    /*
     * load local library
     */
    static {
        try {
            System.loadLibrary("picturemanager_jni");
            native_init();
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Cannot load picturemanager_jni library:\n" + e.toString());
        }
    }

    private static PictureManager _pictureManager = null;

    private long mNativeContext; // accessed by native methods

    private int mPictureManagerContext; // accessed by native methods

    private OnPictureEventListener mOnEventListener;

    private EventHandler mEventHandler;

    public static PictureManager getInstance() {
        if (_pictureManager == null) {
            synchronized (PictureManager.class) {
                if (_pictureManager == null) {
                    _pictureManager = new PictureManager();
                }
            }
        }
        return _pictureManager;
    }

    /**
     * do Panel initial
     *
     * @return TRUE: success, FALSE: fail
     */
    public native boolean panelInitial(String panelIniName);

    /**
     * set Customer Gamma Parameter
     * @param index       <br/> @see INDEX_IN_POINT_LOW...
     * @param value       <br/> @see VALUE_ENABLE...
     * @return TRUE: success, FALSE: fail
     */
    public native boolean setCustomerGammaParameter(int index, int value);

    /**
     * This function will calculate the gamma table by Customer Algorithm
     *
     * @param gammaTable    <br/> @see com.mstar.android.tvapi.common.vo.GammaTable
     * @param MapMode       <br/> @see MODE_8_BIT...
     * @return boolean TRUE: success, FALSE: fail
     */
    public boolean calCustomerGammaTable(GammaTable gammaTable, int MapMode){
        return calGammaTable(gammaTable, MapMode);
    }

    private native boolean calGammaTable(GammaTable gammaTable, int MapMode);

    /**
     * set Scaler Gamma correction table.
     *
     * @param gammaTable        <br/> @see com.mstar.android.tvapi.common.vo.GammaTable
     * @return boolean TRUE: success, FALSE: fail
     */
    public native boolean setScalerGammaTable(GammaTable gammaTable);

    /**
     * get scaler motion value
     *
     * @return byte range: 0~255
     */
    public native byte getScalerMotion();

    /**
     * Return the overscan function is enabled or not.
     *
     * @return TRUE: overscan, FALSE: not overscan
     * @throws TvCommonException
     */
    public native final boolean isOverscanEnabled() throws TvCommonException;

    /**
     * Set all the window region into driver to make window scaled
     *
     * @return boolean TURE for successs, FALSE for fail
     * @throws TvCommonException
     */
    public native final boolean scaleWindow() throws TvCommonException;

    /**
     * Selects the working window for window operation including setCropWindow,
     * setDisplayWindow, setWindowEnabled, setWindowVisibled.
     *
     * @param windowId EnumScalerWindow scaler window id
     * @return boolean
     * @throws TvCommonException
     */
    public boolean selectWindow(EnumScalerWindow windowId) throws TvCommonException {
        return native_selectWindow(windowId.ordinal());

    }

    private native final boolean native_selectWindow(int windowId) throws TvCommonException;

    /**
     * Sets the aspect ratio of the crop window.
     *
     * @param videoArcType EnumVideoArcType indicate the type of aspect ratio.
     * @throws TvCommonException
     */

    public final void setAspectRatio(EnumVideoArcType videoArcType) throws TvCommonException {
        native_setAspectRatio(videoArcType.ordinal());
    }

    private native final void native_setAspectRatio(int enAspectRatioType) throws TvCommonException;

    /**
     * Get the aspect ratio of the crop window.
     *
     * @return videoArcType EnumVideoArcType indicate the type of aspect ratio.
     * @throws TvCommonException
     */

    public final EnumVideoArcType getAspectRatio() throws TvCommonException {

        int iReturn = native_getAspectRatio();

        if (iReturn < EnumVideoArcType.E_DEFAULT.ordinal()
                || iReturn > EnumVideoArcType.E_MAX.ordinal()) {
            throw new TvCommonException("native_getAspectRatio failed");
        } else {
            return EnumVideoArcType.values()[iReturn];
        }
    }

    private native final int native_getAspectRatio() throws TvCommonException;

    /**
     * Sets the crop window size.
     *
     * @param videoWindowType Crop window region
     * @throws TvCommonException
     */

    public native final void setCropWindow(VideoWindowType videoWindowType)
    throws TvCommonException;

    /**
     * Sets display window size .
     *
     * @param videoWindowType Display window region
     * @throws TvCommonException
     */
    public native final void setDisplayWindow(VideoWindowType videoWindowType)
    throws TvCommonException;

    /**
     * @param bottom bottom distance to the window edge
     * @param top left distance to the window edge
     * @param right right distance to the window edge
     * @param left left distance to the window edge
     * @throws TvCommonException
     */
    public native final void setOverscan(int bottom, int top, int right, int left)
    throws TvCommonException;

    /**
     * Enable overscan
     *
     * @throws TvCommonException
     */
    public native final void enableOverScan() throws TvCommonException;

    /**
     * Disable overscan
     *
     * @throws TvCommonException
     */
    public native final void disableOverScan() throws TvCommonException;

    /**
     * Sets the brightness of picture mode
     *
     * @param value short indicate the brightness value
     * @throws TvCommonException
     */
    public native final void setPictureModeBrightness(short value) throws TvCommonException;

    /**
     * Sets picture mode contrast
     *
     * @param value short set contrast curve value
     * @throws TvCommonException
     */
    public native final void setPictureModeContrast(short value) throws TvCommonException;

    /**
     * Set picture mode sharpness
     *
     * @param value short set Sharpness curve value
     * @throws TvCommonException
     */
    public native final void setPictureModeSharpness(short value) throws TvCommonException;

    /**
     * Sets picture mode color
     *
     * @param value short set Color curve value
     * @throws TvCommonException
     */
    public native final void setPictureModeColor(short value) throws TvCommonException;

    /**
     * Set picture mode tint
     *
     * @param value short set Tint curve value
     * @throws TvCommonException
     */
    public native final void setPictureModeTint(short value) throws TvCommonException;

    /**
     * Need to selectWindows() first. Set the window not visible
     *
     * @throws TvCommonException
     */
    public native final void setWindowInvisible() throws TvCommonException;

    /**
     * Need to selectWindows() first. Set the window visible.
     *
     * @throws TvCommonException
     */
    public native final void setWindowVisible() throws TvCommonException;

    /**
     * Set the brightness of the picture mode of the certain location
     *
     * @param setLocationType EnumSetLocationType set location type
     * @param value the brightness value
     * @throws TvCommonException
     */
    public final void setPictureModeBrightness(EnumSetLocationType setLocationType, int value)
    throws TvCommonException {
        native_setPictureModeBrightness(setLocationType.getValue(), value);
    }

    private native final void native_setPictureModeBrightness(int setLocationType, int value)
    throws TvCommonException;

    /**
     * Sets color temperature.
     *
     * @param vo ColorTemperatureVO
     * @throws TvCommonException
     */
    public native final void setColorTemperature(ColorTemperatureExData vo) throws TvCommonException;

    /**
     * Sets demoe mode.
     *
     * @param enMweType EnumMweType mwe type
     * @throws TvCommonException
     */
    public void setDemoMode(EnumMweType enMweType) throws TvCommonException {
        native_setDemoMode(enMweType.getValue());
    }

    private native final void native_setDemoMode(int enMsMweType) throws TvCommonException;

    /**
     * Set system in demo mode
     *
     * @return EnumMweType
     * @throws TvCommonException
     */
    public final EnumMweType getDemoMode() throws TvCommonException {
        int iReturn = native_getDemoMode();
        int iordinal = EnumMweType.getOrdinalThroughValue(iReturn);
        if (iordinal != -1) {
            return EnumMweType.values()[iordinal];
        } else {
            throw new TvCommonException("get demomode error ");
        }

    }

    private native final int native_getDemoMode() throws TvCommonException;

    /**
     * Enable DLC function
     *
     * @throws TvCommonException
     */
    public native final void enableDlc() throws TvCommonException;

    /**
     * Disable DLC function
     *
     * @throws TvCommonException
     */
    public native final void disableDlc() throws TvCommonException;

    /**
     * Sets mpeg noise reduction
     *
     * @param mpegNR EnumMpegNr one of EnumMpegNr
     * @return boolean
     * @throws TvCommonException
     */
    public final boolean setMpegNoiseReduction(EnumMpegNoiseReduction mpegNR)
    throws TvCommonException {
        return native_setMpegNoiseReduction(mpegNR.getValue());
    }

    private native final boolean native_setMpegNoiseReduction(int mpegNR) throws TvCommonException;

    /**
     * Sets noise reduction
     *
     * @param nr EnumNoiseReduction on of EnumNoiseReduction
     * @return boolean
     * @throws TvCommonException
     */
    public final boolean setNoiseReduction(EnumNoiseReduction nr) throws TvCommonException {
        return native_setNoiseReduction(nr.getValue());
    }

    private native final boolean native_setNoiseReduction(int nr) throws TvCommonException;

    /**
     * Set film mode
     *
     * @param enMsFilm EnumFilm film mode id
     * @throws TvCommonException
     */
    public void setFilm(EnumFilm enMsFilm) throws TvCommonException {
        native_setFilm(enMsFilm.getValue());
    }

    private native final void native_setFilm(int enMsFilm) throws TvCommonException;

    /**
     * Freeze image
     *
     * @return boolean
     * @throws TvCommonException
     */
    public native final boolean freezeImage() throws TvCommonException;

    /**
     * Un freeze image
     *
     * @return boolean
     * @throws TvCommonException
     */
    public native final boolean unFreezeImage() throws TvCommonException;

    /**
     * Check if image is freezed or not
     *
     * @return boolean
     * @throws TvCommonException
     */
    public native final boolean isImageFreezed() throws TvCommonException;

    /**
     * Get Panel Width and Height
     *
     * @return PanelPropertyVO
     * @throws TvCommonException
     */
    public native final PanelProperty getPanelWidthHeight() throws TvCommonException;

    /**
     * Sets Output Pattern
     *
     * @param bEnable boolean Enable Output pattern or not
     * @param red int Input Red
     * @param Green int Input Greeen
     * @param blue int Input Blue
     * @throws TvCommonException
     */
    public native final void setOutputPattern(boolean bEnable, int red, int Green, int blue)
    throws TvCommonException;

    /**
     * Set the ursa5 DDC command by mfc mode
     *
     * @param mfcMode EnumMfcMode mfc mode
     * @throws TvCommonException
     */
    public final void setMfc(EnumMfcMode mfcMode) throws TvCommonException {
        native_setMfc(mfcMode.ordinal());
    }

    private native final void native_setMfc(int mfcMode) throws TvCommonException;

    /**
     * To set backlight
     *
     * @param value int The current scale value in the range 0-100
     * @throws TvCommonException
     */
    public native final void setBacklight(int value) throws TvCommonException;

    /**
     * To get minimized value for backlight
     *
     * @return int The minimized scale value
     * @throws TvCommonException
     */
    public native final int getBacklightMinValue() throws TvCommonException;

    /**
     * To get maximized value for backlight
     *
     * @return int The maximized scale value
     * @throws TvCommonException
     */
    public native final int getBacklightMaxValue() throws TvCommonException;

    /**
     * To get backlight
     *
     * @return int The current scale value in the range 0-100
     * @throws TvCommonException
     */
    public native final int getBacklight() throws TvCommonException;

    /**
     * Set DLC dynamic contrast curve data
     *
     * @param normalCurve int[] int curve data for normal
     * @param lightCurve int[] int curve data for light
     * @param darkCurve int[] int curve data for dark
     * @throws TvCommonException
     */
    public native final void setDynamicContrastCurve(int[] normalCurve, int[] lightCurve,
            int[] darkCurve) throws TvCommonException;

    /**
     * Get DLC dynamic contrast curve data
     *
     * @return int[]
     * @throws TvCommonException
     */
    public native final int[] getDynamicContrastCurve() throws TvCommonException;

    /**
     * This function will get Averave Luma value
     *
     * @return short averave Luma value
     * @throws TvCommonException
     */
    public native short getDlcAverageLuma() throws TvCommonException;

    /**
     * This function will set set server debugmode to check the java ->
     * jni->binder ok
     *
     * @param mode boolean TRUE: set to debug mode, False: disable debug mode
     * @throws TvCommonException
     */
    public native void setDebugMode(boolean mode) throws TvCommonException;

    /**
     * Disable osd window
     *
     * @param mfcOsdWindow EnumMfcOsdWindow win id
     * @return boolean TRUE: success,FALSE: failure
     * @throws TvCommonException
     */
    public final boolean disableOsdWindow(EnumMfcOsdWindow mfcOsdWindow) throws TvCommonException {
        return native_disableOsdWindow(mfcOsdWindow.ordinal());
    }

    private native boolean native_disableOsdWindow(int mfcOsdWindow) throws TvCommonException;

    /**
     * Disable all osd window
     *
     * @return boolean TRUE: success,FALSE: failure
     * @throws TvCommonException
     */
    public final boolean disableAllOsdWindow() throws TvCommonException {
        return native_disableAllOsdWindow();
    }

    private native boolean native_disableAllOsdWindow() throws TvCommonException;

    /**
     * Sets osd window
     *
     * @param mfcOsdWindow EnumMfcOsdWindow win id
     * @param startX int X start
     * @param width int win width
     * @param startY int Y start
     * @param height int win height
     * @return boolean TRUE: success,FALSE: failure
     * @throws TvCommonException
     */
    public final boolean setOsdWindow(EnumMfcOsdWindow mfcOsdWindow, int startX, int width,
                                      int startY, int height) throws TvCommonException {
        return native_setOsdWindow(mfcOsdWindow.ordinal(), startX, width, startY, height);
    }

    private native boolean native_setOsdWindow(int mfcOsdWindow, int startX, int width, int startY,
            int height) throws TvCommonException;

    /**
     * To Set color range mode of input source
     *
     * @param colorRange0_255 boolean TRUE: color rang 0~255, FALSE: color range
     *            16~235
     * @throws TvCommonException
     */
    public native void setColorRange(boolean colorRange0_255) throws TvCommonException;

    /**
     * To get total customer picture quality rule number
     *
     * @return int total customer picture quality rule number
     * @throws TvCommonException
     */
    public native int getCustomerPqRuleNumber() throws TvCommonException;

    /**
     * To get status number by customer picture quality rule
     *
     * @param ruleType int customer picture quality rule
     * @return int 0: fail; non-0: success
     * @throws TvCommonException
     */
    public native int getStatusNumberByCustomerPqRule(int ruleType) throws TvCommonException;

    /**
     * To set customer picture quality rule status
     *
     * @param ruleType int customer picture quality rule
     * @param ruleStatus int customer picture quality rule status
     * @return boolean
     * @throws TvCommonException
     */
    public native boolean setStatusByCustomerPqRule(int ruleType, int ruleStatus)
    throws TvCommonException;

    /**
     * Move window position, only change window x,y
     *
     * @return boolean
     * @throws TvCommonException
     */
    public native boolean moveWindow() throws TvCommonException;

    /**
     * To enable backlight
     *
     * @throws TvCommonException
     */
    public native void enableBacklight() throws TvCommonException;

    /**
     * To disable backlight
     *
     * @throws TvCommonException
     */
    public native void disableBacklight() throws TvCommonException;

    /**
     * To get dynamic Laffer curve (DLC) luminance Array return int[] DlcLum
     * array
     *
     * @throws TvCommonException
     */
    public native int[] getDlcLumArray(int dlcLumArrayLength) throws TvCommonException;

    /**
     * To get dynamic Laffer curve (DLC) luminance temporary
     *
     * @return int DLCLum average temporary
     * @throws TvCommonException
     */
    public native int getDlcLumAverageTemporary() throws TvCommonException;

    /**
     * get dynamic Laffer curve (DLC) luminance total count
     *
     * @return int DLCLum count
     * @throws TvCommonException
     */
    public native int getDlcLumTotalCount() throws TvCommonException;

    /**
     * switch dlc curve
     *
     * @param dlcCurveIndex short range append the DLC.ini the max number is 9
     * @return boolean true is success false is failed
     * @throws TvCommonException
     */
    public native boolean switchDlcCurve(short dlcCurveIndex) throws TvCommonException;

    /**
     * get pixel RGB
     *
     * @param eStage EnumGetPixelRgbStage
     * @param x int
     * @param y int
     * @param eWindow EnumScalerWindow
     * @return Rgb_Data the RGB value of the point(x,y)
     * @throws TvCommonException
     */

    public Rgb_Data getPixelRgb(EnumGetPixelRgbStage eStage, short x, short y,
                                EnumScalerWindow eWindow) throws TvCommonException {
        return native_getPixelRgb(eStage.getValue(), x, y, eWindow.ordinal());
    }

    private native Rgb_Data native_getPixelRgb(int eStage, short x, short y, int eWindow)
    throws TvCommonException;

    /**
     * set swing level
     *
     * @param swingLevel short
     * @return boolean
     * @throws TvCommonException
     */
    public native boolean setSwingLevel(short swingLevel) throws TvCommonException;

    /**
     * get dlc histogram Max
     *
     * @return short
     * @throws TvCommonException
     */
    public native short getDlcHistogramMax() throws TvCommonException;

    /**
     * get dlc histogram min
     *
     * @return short
     * @throws TvCommonException
     */
    public native short getDlcHistogramMin() throws TvCommonException;

    /**
     * get HDMI color format
     *
     * @return int The current scale value in the range 0~2; RGB , YUV_422 , YUV_444
     * @throws TvCommonException
     */
    public native int getHDMIColorFormat() throws TvCommonException;

    /**
     * force free run
     *
     * @param bEnable boolean disable or enable free run
     * @param b3D boolean is 3D mode
     * @return boolean
     * @throws TvCommonException
     */
    public native boolean forceFreerun(boolean bEnable, boolean b3D) throws TvCommonException;

    /**
     * set HLinear Scaling
     *
     * @param bEnable boolean enable or disable h linear scaling
     * @param bSign boolean is signed or not
     * @param u16Delta short is Delta
     * @return boolean
     * @throws TvCommonException
     */
    public native boolean setHLinearScaling(boolean bEnable, boolean bSign, int u16Delta) throws TvCommonException;

    /**
     * To set MEMC Mode
     *
     * @param interfaceCommand String tvos interface command
     * @return boolean set interface Cmd succeed or failed
     * @throws TvCommonException
     * @hide
     */
    public native boolean setMEMCMode(String interfaceCommand) throws TvCommonException;

    /**
     * Set Local dimming mode for SPI iwat
     *
     * @param localDimingMode EnumLocalDimmingMode localdimming mode
     * @return boolean
     * @throws TvCommonException
     */

    public boolean setLocalDimmingMode(EnumLocalDimmingMode localDimingMode)
    throws TvCommonException {
        return native_setLocalDimmingMode(localDimingMode.ordinal());
    }

    private native boolean native_setLocalDimmingMode(int localDimingModeNumber)
    throws TvCommonException;

    /**
     * Set Local dimming bright Level
     *
     * @param localDimingBrightLevel short local Dimming brigth level 0-150
     * @return boolean
     * @throws TvCommonException
     */

    public native boolean setLocalDimmingBrightLevel(short localDimingBrightLevel)
    throws TvCommonException;;

    /**
     * Set turn off Local dimming Backlight
     *
     * @param bTrunOff boolean disable or enable localdimming
     * @return boolean
     * @throws TvCommonException
     * @deprecated Use {@link turnOffLocalDimmingBacklight(bTrunOff)}.
     */
    @Deprecated
    public native boolean setTurnOffLocalDimmingBacklight(boolean bTrunOff)
    throws TvCommonException;;

    /**
     * Turn off Local dimming Backlight
     *
     * @param bTrunOff boolean disable or enable localdimming
     * @return boolean
     * @throws TvCommonException
     */
    public native boolean turnOffLocalDimmingBacklight(boolean bTrunOff)
    throws TvCommonException;;

    /**
     * disabl all dual win mode
     *
     * @return boolean
     * @throws TvCommonException
     */
    public native boolean disableAllDualWinMode() throws TvCommonException;

    /**
     * set Ultra Clear
     *
     * @param bEnable boolean set Ultra Clear or not
     * @return boolean
     * @throws TvCommonException
     */
    public native boolean setUltraClear(boolean bEnable) throws TvCommonException;;

    /**
     * Gets the resolution
     *
     * @return byte 0~100
     * @throws TvCommonException
     */
    public final byte getResolution() throws TvCommonException {
        return native_getResolution();
    }

    private native final byte native_getResolution() throws TvCommonException;

    /**
     * Sets the resolution value
     *
     * @param resolution source type
     * @throws TvCommonException
     */
    public void setResolution(byte resolution) throws TvCommonException {
        native_setResolution(resolution);
    }

    private native final void native_setResolution(byte resolution) throws TvCommonException;

    /**
     * Gets the reproduce rate
     *
     * @return rate
     * @throws TvCommonException
     */
    public final int getReproduceRate() throws TvCommonException {
        return native_getReproduceRate();
    }

    private native final int native_getReproduceRate() throws TvCommonException;

    /**
     * Sets the reproduce rate
     *
     * @param rate
     * @throws TvCommonException
     */
    public void setReproduceRate(int rate) throws TvCommonException {
        native_setReproduceRate(rate);
    }

    private native final void native_setReproduceRate(int rate) throws TvCommonException;

    /**
     * autoHDMIColorRange
     *
     * @throws TvCommonException
     */
    public native boolean autoHDMIColorRange() throws TvCommonException;

    /**
     * enter 4K2K Mode
     *
     * @throws TvCommonException
     */
    public native boolean enter4K2KMode(boolean bEn) throws TvCommonException;

    /**
     * Is 4K2K Mode
     *
     * @throws TvCommonException
     */
    public native final void asGetWbAdjustStar() throws TvCommonException;	 
    public native final void asGetWbAdjustExit() throws TvCommonException;	
    public native boolean is4K2KMode(boolean bEn) throws TvCommonException;

    /**
     * set Scaler Gamma by Index
     *
     * @throws TvCommonException
     */
    public native void setScalerGammaByIndex(byte Index) throws TvCommonException;

    /**
     * get PixelInfo
     *
     * @return ScreenPixelInfo
     * @throws TvCommonException
     */
    public ScreenPixelInfo getPixelInfo(int x, int y, int w, int h) throws TvCommonException {
        ScreenPixelInfo PixelInfo = native_getPixelInfo(x, y, w, h);
        if (PixelInfo != null) {
            PixelInfo.enStage = ScreenPixelInfo.EnumPixelRGBStage.valueOf(PixelInfo.tmpStage);
        }
        return PixelInfo;
    }

    private native ScreenPixelInfo native_getPixelInfo(int x, int y, int w, int h)
    throws TvCommonException;

    /**
     * enable Xvycc Compensation
     *
     * @throws TvCommonException
     */
    public native boolean enableXvyccCompensation(boolean bEn, int eWin) throws TvCommonException;

    /**
     * set xvYCC Enable
     * @param bEn: true: on false: off,
     * @param eMode: 0: normal 1: xvycc 2: sRGB mode
     * @throws TvCommonException
     */
    public native boolean setxvYCCEnable(boolean bEn, int eMode) throws TvCommonException;

    /**
     * lock 4K2K Mode
     *
     * @throws TvCommonException
     */
    public native void lock4K2KMode(boolean bEn) throws TvCommonException;

    /**
     * get ursa 4K2K Mode
     *
     * @throws TvCommonException
     */
    public EnumUrsaMode get4K2KMode() throws TvCommonException {
        int iReturn = native_get4K2KMode();
        if (iReturn < EnumUrsaMode.E_URSA_4K2K_MODE_FULLHD.ordinal()
                || iReturn > EnumUrsaMode.E_URSA_4K2K_MODE_UNDEFINED.ordinal()) {
            throw new TvCommonException("native_get4K2KMode failed");
        } else {
            return EnumUrsaMode.values()[iReturn];
        }
    }
    private native int native_get4K2KMode() throws TvCommonException;

    /**
     * set 4K2K Mode
     *
     * @throws TvCommonException
     */
    public boolean set4K2KMode(EnumPanelTiming enOutPutTimming, EnumUrsaMode enUrsaMode) throws TvCommonException {
        return native_set4K2KMode(enOutPutTimming.ordinal(), enUrsaMode.ordinal());
    }
    private native boolean native_set4K2KMode(int enOutPutTimming, int enUrsaMode) throws TvCommonException;

    public native boolean keepScalerOutput4k2k(boolean enable) throws TvCommonException;

    public native boolean is3DTVPlugedIn() throws TvCommonException;

    public native boolean isSupportedZoom() throws TvCommonException;

    public native final TimingInfo[] native_getSupportedTimingList() throws TvCommonException;

    public native int native_getSupportedTimingListCount() throws TvCommonException;

    public native int native_getCurrentTimingId() throws TvCommonException;

    // -------------------------------------------------------------------------------------
    // ---------------------------------format----------------------------------------------
    protected PictureManager() {
        // -------------------here should sign a event handale
        Looper looper;
        if ((looper = Looper.myLooper()) != null) {
            mEventHandler = new EventHandler(this, looper);
        } else if ((looper = Looper.getMainLooper()) != null) {
            mEventHandler = new EventHandler(this, looper);
        } else {
            mEventHandler = null;
        }
        native_setup(new WeakReference<PictureManager>(this));
    }

    /**
     * Register by setOnPictureEventListener(OnPictureEventListener listener).
     * Your listener will be triggered when the event posted from native
     * code.</p>
     *
     * @param listener OnPictureEventListener
     */
    public void setOnPictureEventListener(OnPictureEventListener listener) {
        mOnEventListener = listener;
    }

    private class EventHandler extends Handler {
        private PictureManager mMSrv;

        public EventHandler(PictureManager srv, Looper looper) {
            super(looper);
            mMSrv = srv;
        }

        @Override
        public void handleMessage(Message msg) {
            if (mMSrv.mNativeContext == 0) {

                return;
            }

            PictureManager.EVENT[] ev = PictureManager.EVENT.values();

            if (msg.what > EVENT.EV_MAX.ordinal() || msg.what < EVENT.EV_SET_ASPECTRATIO.ordinal()) {
                Log.e(this.getClass().getCanonicalName(), "Native post event out of bound:"
                      + Integer.toString(msg.what));
                return;
            }

            switch (ev[msg.what]) {
                case EV_SET_ASPECTRATIO:
                    if (mOnEventListener != null) {
                        mOnEventListener.onSetAspectratio(msg.what, msg.arg1, msg.arg2);
                    }
                    return;
                case EV_4K2K_PHOTO_DISABLE_PIP:
                    if (mOnEventListener != null) {
                        mOnEventListener.on4K2KPhotoDisablePip(msg.what, msg.arg1, msg.arg2);
                    }
                    return;
                case EV_4K2K_PHOTO_DISABLE_POP:
                    if (mOnEventListener != null) {
                        mOnEventListener.on4K2KPhotoDisablePop(msg.what, msg.arg1, msg.arg2);
                    }
                    return;
                case EV_4K2K_PHOTO_DISABLE_DUALVIEW:
                    if (mOnEventListener != null) {
                        mOnEventListener.on4K2KPhotoDisableDualview(msg.what, msg.arg1, msg.arg2);
                    }
                    return;
                case EV_4K2K_PHOTO_DISABLE_TRAVELINGMODE:
                    if (mOnEventListener != null)
                        mOnEventListener.on4K2KPhotoDisableTravelingmode(msg.what, msg.arg1,
                                msg.arg2);
                    return;
                default:
                    System.err.println("Unknown message type " + msg.what);
                    return;
            }
        }
    }

    private static void postEventFromNative(Object srv_ref, int what, int arg1, int arg2, Object obj) {
        System.out.println("picturemanager callback  \n");
        return;
    }

    private static void PostEvent_SnServiceDeadth(Object srv_ref, int arg1, int arg2) {
    }

    private static void PostEvent_SetAspectratio(Object srv_ref, int arg1, int arg2) {
        PictureManager srv = (PictureManager)((WeakReference) srv_ref).get();
        if (srv == null) {
            return;
        }
        if (srv.mEventHandler != null) {
            Message m = srv.mEventHandler.obtainMessage(EVENT.EV_SET_ASPECTRATIO.ordinal(), arg1,
                        arg2);
            srv.mEventHandler.sendMessage(m);
        }

        return;
    }

    private static void PostEvent_4K2KPhotoDisablePip(Object srv_ref, int arg1, int arg2) {
        PictureManager srv = (PictureManager)((WeakReference) srv_ref).get();
        if (srv == null) {
            return;
        }
        if (srv.mEventHandler != null) {
            Message m = srv.mEventHandler.obtainMessage(EVENT.EV_4K2K_PHOTO_DISABLE_PIP.ordinal(),
                        arg1, arg2);
            srv.mEventHandler.sendMessage(m);
        }

        return;
    }

    private static void PostEvent_4K2KPhotoDisablePop(Object srv_ref, int arg1, int arg2) {
        PictureManager srv = (PictureManager)((WeakReference) srv_ref).get();
        if (srv == null) {
            return;
        }
        if (srv.mEventHandler != null) {
            Message m = srv.mEventHandler.obtainMessage(EVENT.EV_4K2K_PHOTO_DISABLE_POP.ordinal(),
                        arg1, arg2);
            srv.mEventHandler.sendMessage(m);
        }

        return;
    }

    private static void PostEvent_4K2KPhotoDisableDualview(Object srv_ref, int arg1, int arg2) {
        PictureManager srv = (PictureManager)((WeakReference) srv_ref).get();
        if (srv == null) {
            return;
        }
        if (srv.mEventHandler != null) {
            Message m = srv.mEventHandler.obtainMessage(
                            EVENT.EV_4K2K_PHOTO_DISABLE_DUALVIEW.ordinal(), arg1, arg2);
            srv.mEventHandler.sendMessage(m);
        }

        return;
    }

    private static void PostEvent_4K2KPhotoDisableTravelingmode(Object srv_ref, int arg1, int arg2) {
        PictureManager srv = (PictureManager)((WeakReference) srv_ref).get();
        if (srv == null) {
            return;
        }
        if (srv.mEventHandler != null) {
            Message m = srv.mEventHandler.obtainMessage(
                            EVENT.EV_4K2K_PHOTO_DISABLE_TRAVELINGMODE.ordinal(), arg1, arg2);
            srv.mEventHandler.sendMessage(m);
        }

        return;
    }

    private static native final void native_init();

    private native final void native_setup(Object msrv_this);

    private native final void native_finalize();

    protected void release() throws Throwable {
        _pictureManager = null;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        native_finalize();
        _pictureManager = null;
    }
    // --------------------------------end
    // format----------------------------------
    // ----------------------------------------------------------------------------
}
