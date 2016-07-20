//<MStar Software>
//******************************************************************************
// MStar Software
// Copyright (c) 2010 - 2012 MStar Semiconductor, Inc. All rights reserved.
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

package com.mstar.android.tvapi.factory;

import java.lang.ref.WeakReference;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumColorTemperature;
import com.mstar.android.tvapi.common.vo.EnumScalerWindow;
import com.mstar.android.tvapi.common.vo.TvOsType;
import com.mstar.android.tvapi.factory.vo.DisplayResolutionType.EnumDisplayResolutionType;
import com.mstar.android.tvapi.factory.vo.EnumAcOnPowerOnMode;
import com.mstar.android.tvapi.factory.vo.EnumAdcSetIndexType;
import com.mstar.android.tvapi.factory.vo.EnumFwType;
import com.mstar.android.tvapi.factory.vo.EnumPqUpdateFile;
import com.mstar.android.tvapi.factory.vo.EnumScreenMute;
import com.mstar.android.tvapi.factory.vo.FactoryNsVdSet;
import com.mstar.android.tvapi.factory.vo.PictureModeValue;
import com.mstar.android.tvapi.factory.vo.PqlCalibrationData;
import com.mstar.android.tvapi.factory.vo.WbGainOffset;
import com.mstar.android.tvapi.factory.vo.WbGainOffsetEx;
import com.mstar.android.tvapi.factory.vo.UrsaVersionInfo;
import com.mstar.android.tvapi.factory.vo.PanelVersionInfo;
import android.os.SystemProperties;

/**
 * <p>
 * Title: FactoryManager
 * </p>
 * <p>
 * Description: This class provides factory manipulations for the TV system.
 * </p>
 * ----------------------------------------------------------------------------
 * ----<br>
 * [Sample 1] sample code to get FactoryManager and manipulate FactoryManager
 * class<br>
 * ----------------------------------------------------------------------------
 * ----<br>
 * <br>
 * ...<br>
 * private FactoryManager fm = TvManager.getFactoryManager();<br>
 * <br>
 * boolean return = false;<br>
 * try<br>
 * {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;return = fm.isUartOn();<br>
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
 * @author Kevin.Tang
 * @version 1.0
 */
public class FactoryManager {
    private static FactoryManager _factoryManager = null;

    public static FactoryManager getInstance() {
        if (_factoryManager == null) {
            synchronized (FactoryManager.class) {
                if (_factoryManager == null) {
                    _factoryManager = new FactoryManager();
                }
            }
        }
        return _factoryManager;
    }

    protected static FactoryManager getInstance(Object obj) {
        String objname = obj.getClass().getName();
        if (objname.equals("com.mstar.android.tvapi.common.TvFactoryManagerProxy")) {
            if (_factoryManager == null) {
                synchronized (FactoryManager.class) {
                    if (_factoryManager == null) {
                        _factoryManager = new FactoryManager();

                    }
                }
            }
        }
        return _factoryManager;
    }

    protected FactoryManager() {
        native_setup(new WeakReference<FactoryManager>(this));
    }

    private static void postEventFromNative(Object srv_ref, int what, int arg1, int arg2, Object obj) {
        return;
    }

    private static void PostEvent_SnServiceDeadth(Object srv_ref, int arg1, int arg2) {
    }

    private long mNativeContext; // accessed by native methods

    private int mFactoryManagerContext; // accessed by native methods
    /*
     * load local library
     */
    static {
        try {
            System.loadLibrary("factorymanager_jni");
            native_init();
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Cannot load factorymanager_jni library:\n" + e.toString());
        }
    }

    /**
     * designed for spec 110_Violet_FactoryAdjustMENU page 3: Video Adjustment
     * get CVBS auto adjustment result
     *
     * @return byte: CVBS auto adjustment result
     */
    public native final byte getAutoFineGain() throws TvCommonException;

    /**
     * designed for spec 110_Violet_FactoryAdjustMENU page 3: Video Adjustment
     * set CVBS fine gain
     *
     * @param fineGain: CVBS fixed fine gain
     * @return boolean: true for success, false for failure
     */
    public native final boolean setFixedFineGain(byte fineGain) throws TvCommonException;

    /**
     * designed for spec 110_Violet_FactoryAdjustMENU page 3: Video Adjustment
     * get tuner RF gain auto adjustment result
     *
     * @return byte: RF auto adjustment result
     */
    public native final byte getAutoRFGain() throws TvCommonException;

    /**
     * designed for spec 110_Violet_FactoryAdjustMENU page 3: Video Adjustment
     * set fixed tuner RF gain adjustment result
     *
     * @param gain: fixed RF gain
     * @return boolean: true for success, false for failure
     */
    public native final boolean setRFGain(byte gain) throws TvCommonException;

    private static native final void native_init();

    private native final void native_setup(Object msrv_this);

    private native final void native_finalize();

    public void release() throws Throwable {
        _factoryManager = null;
    }

    @Override
    public void finalize() throws Throwable {
        super.finalize();
        native_finalize();
        _factoryManager = null;
    }

    /**
     * Reset Display Resolution
     *
     * @return boolean
     * @throws TvCommonException
     */
    public native final boolean resetDisplayResolution() throws TvCommonException;

    /**
     * Gets Display Resolution index
     *
     * @return EnumDisplayResolutionType Display resolution type
     * @throws TvCommonException
     */
    public final EnumDisplayResolutionType getDisplayResolution() throws TvCommonException {
        // @todo: EnumDisplayResolutionType need to use getValue to match the
        // data
        int iReturn = native_getDisplayResolution();
        int iordinal = EnumDisplayResolutionType.getOrdinalThroughValue(iReturn);
        if (iordinal != -1) {
            return EnumDisplayResolutionType.values()[iordinal];
        } else {
            throw new TvCommonException("funtion getDisplayResolution fail");
        }
    }

    private native final int native_getDisplayResolution() throws TvCommonException;

    /**
     * Sets Video Test Pattern
     *
     * @param color EnumScreenMute (E_SCREEN_MUTE_OFF, E_SCREEN_MUTE_WHITE,
     *            E_SCREEN_MUTE_RED, E_SCREEN_MUTE_GREEN, E_SCREEN_MUTE_BLUE,
     *            E_SCREEN_MUTE_BLACK, E_SCREEN_MUTE_NUMBER)
     * @throws TvCommonException
     */
    public final void setVideoTestPattern(EnumScreenMute color) throws TvCommonException {
        native_setVideoTestPattern(color.ordinal());
    }

    private native final void native_setVideoTestPattern(int color) throws TvCommonException;

    /**
     * Set Video Mute Color
     *
     * @param testPatternMode test pattern mode
     * @see com.mstar.android.tv.TvFactoryManager#SCREEN_MUTE_OFF
     * @see com.mstar.android.tv.TvFactoryManager#SCREEN_MUTE_WHITE
     * @see com.mstar.android.tv.TvFactoryManager#SCREEN_MUTE_RED
     * @see com.mstar.android.tv.TvFactoryManager#SCREEN_MUTE_GREEN
     * @see com.mstar.android.tv.TvFactoryManager#SCREEN_MUTE_BLUE
     * @see com.mstar.android.tv.TvFactoryManager#SCREEN_MUTE_BLACK
     * @return <code>true</code>:set successfully, <code>false</code>:set failed
     */
    public native final boolean setVideoMuteColor(int indexColor) throws TvCommonException;

    /**
     * Sets picture quality ADC R/G/B Gain and offset values.
     *
     * @param enWin EnumScalerWindow (Main of sub window in PIP)
     * @param eAdcIndex EnumAdcSetIndexType (The ADC structure array index)
     * @param stADCGainOffset PQLCalibrationDataVO (the ADC R/G/B Gain and
     *            offset values)
     * @throws TvCommonException
     */
    public final void setAdcGainOffset(EnumScalerWindow enWin, EnumAdcSetIndexType eAdcIndex,
                                       PqlCalibrationData stADCGainOffset) throws TvCommonException {
        native_setAdcGainOffset(enWin.ordinal(), eAdcIndex.ordinal(), stADCGainOffset);
    }

    private native final void native_setAdcGainOffset(int enWin, int eAdcIndex,
            PqlCalibrationData stADCGainOffset) throws TvCommonException;

    /**
     * Gets picture quality ADC R/G/B Gain and offset values.
     *
     * @param enWin EnumScalerWindow (Main of sub window in PIP)
     * @param eAdcIndex EnumAdcSetIndexType (The ADC structure array index)
     * @return PQLCalibrationDataVO
     * @throws TvCommonException
     */
    public final PqlCalibrationData getAdcGainOffset(EnumScalerWindow enWin,
            EnumAdcSetIndexType eAdcIndex) throws TvCommonException {
        return native_getAdcGainOffset(enWin.ordinal(), eAdcIndex.ordinal());
    }

    private native final PqlCalibrationData native_getAdcGainOffset(int enWin, int eAdcIndex)
    throws TvCommonException;

    /**
     * To do auto ADC R/G/B Gain and offset
     *
     * @return boolean
     * @throws TvCommonException
     */
    public native final boolean autoAdc() throws TvCommonException;

    /**
     * To adjust brightness value of video (not in use now)
     *
     * @param subBrightness short (Brightness value to set)
     * @return boolean
     * @throws TvCommonException
     */
    public native final boolean setBrightness(short subBrightness) throws TvCommonException;

    /**
     * To adjust contrast value of video (not in use now)
     *
     * @param subContrast short (Contrast value to set)
     * @return boolean
     * @throws TvCommonException
     */
    public native final boolean setContrast(short subContrast) throws TvCommonException;

    /**
     * To adjust saturation value of video (not in use now)
     *
     * @param saturation short (Saturation value to set)
     * @return boolean
     * @throws TvCommonException
     */
    public native final boolean setSaturation(short saturation) throws TvCommonException;

    /**
     * To adjust Sharpness value of video (not in use now)
     *
     * @param sharpness short (Sharpness value to set)
     * @return boolean
     * @throws TvCommonException
     */
    public native final boolean setSharpness(short sharpness) throws TvCommonException;

    /**
     * To adjust Hue value of video (not in use now)
     *
     * @param hue short (Hue value to set)
     * @return boolean
     * @throws TvCommonException
     */
    public native final boolean setHue(short hue) throws TvCommonException;

    /**
     * To get picture mode Brightness,Contrast,Saturation,Sharpness,Hue values
     *
     * @return PictureModeValueVO
     * @throws TvCommonException
     */
    public native final PictureModeValue getPictureModeValue() throws TvCommonException;

    /**
     * Copy sub color data to all source input
     *
     * @throws TvCommonException
     */
    public native final void copySubColorDataToAllSource() throws TvCommonException;

    /**
     * To set white ballance R/G/B Gain and offset values in 8 bit
     *
     * @param eColorTemp EnumColorTemperature (E_COLOR_TEMP_MIN,
     *            E_COLOR_TEMP_COOL, E_COLOR_TEMP_NATURE, E_COLOR_TEMP_WARM,
     *            E_COLOR_TEMP_USER, E_COLOR_TEMP_MAX, E_COLOR_TEMP_NUM)
     * @param redGain short
     * @param greenGain short
     * @param blueGain short
     * @param redOffset short
     * @param greenOffset short
     * @param blueOffset short
     * @throws TvCommonException
     */
    public final void setWbGainOffset(EnumColorTemperature eColorTemp, short redGain,
                                      short greenGain, short blueGain, short redOffset, short greenOffset, short blueOffset)
    throws TvCommonException {
        native_setWbGainOffset(eColorTemp.getValue(), redGain, greenGain, blueGain, redOffset,
                               greenOffset, blueOffset);
    }

    private native final void native_setWbGainOffset(int eColorTemp, short redGain,
            short greenGain, short blueGain, short redOffset, short greenOffset, short blueOffset)
    throws TvCommonException;

    /**
     * To set white ballance R/G/B Gain and offset values in 16bit
     *
     * @param eColorTemp EnumColorTemperature (E_COLOR_TEMP_MIN,
     *            E_COLOR_TEMP_COOL, E_COLOR_TEMP_NATURE, E_COLOR_TEMP_WARM,
     *            E_COLOR_TEMP_USER, E_COLOR_TEMP_MAX, E_COLOR_TEMP_NUM)
     * @param redGain int
     * @param greenGain int
     * @param blueGain int
     * @param redOffset int
     * @param greenOffset int
     * @param blueOffset int
     * @param enSrcType TvOsType.EnumInputSource one of TvOsType.EnumInputSource
     * @throws TvCommonException
     */
    public final void setWbGainOffsetEx(EnumColorTemperature eColorTemp, int redGain,
                                        int greenGain, int blueGain, int redOffset, int greenOffset, int blueOffset,
                                        TvOsType.EnumInputSource enSrcType) throws TvCommonException {
        native_setWbGainOffsetEx(eColorTemp.getValue(), redGain, greenGain, blueGain, redOffset,
                                 greenOffset, blueOffset, enSrcType.ordinal());
    }

    private native final void native_setWbGainOffsetEx(int eColorTemp, int redGain, int greenGain,
            int blueGain, int redOffset, int greenOffset, int blueOffset, int enSrcType)
    throws TvCommonException;

    /**
     * Gets white ballance R/G/B Gain and offset values in 8bit.
     *
     * @param eColorTemp EnumColorTemperature
     * @return WBGainOffsetVO
     * @throws TvCommonException
     */
    public final WbGainOffset getWbGainOffset(EnumColorTemperature eColorTemp)
    throws TvCommonException {
        return naitve_getWbGainOffset(eColorTemp.getValue());
    }

    private native final WbGainOffset naitve_getWbGainOffset(int eColorTemp)
    throws TvCommonException;

    /**
     * Gets white ballance R/G/B Gain and offset values in 16bit.
     *
     * @param eColorTemp EnumColorTemperature
     * @param enSrcType int
     * @return WBGainOffsetExVO
     * @throws TvCommonException
     */
    public final WbGainOffsetEx getWbGainOffsetEx(EnumColorTemperature eColorTemp, int enSrcType)
    throws TvCommonException {
        return native_getWbGainOffsetEx(eColorTemp.getValue(), enSrcType);
    }

    private native final WbGainOffsetEx native_getWbGainOffsetEx(int eColorTemp, int enSrcType)
    throws TvCommonException;

    /**
     * Copy while balance setting to all source input
     *
     * @throws TvCommonException
     */
    public native final void copyWhiteBalanceSettingToAllSource() throws TvCommonException;

    /**
     * Gets QMAP IP number
     *
     * @return int
     * @throws TvCommonException
     */
    public native final int getQmapIpNum() throws TvCommonException;

    /**
     * Gets QMAP Table number
     *
     * @param ipIndex short
     * @return int
     * @throws TvCommonException
     */
    public native final int getQmapTableNum(short ipIndex) throws TvCommonException;

    /**
     * get PQ version
     *
     * @param escalerwindow EnumScalerWindow which the pq version will get
     * @return String the pq version
     * @throws TvCommonException
     * @deprecated Use {@link getPQVersion(int scalerWindow)}.
     */
    @Deprecated
    public final String getPQVersion(EnumScalerWindow escalerwindow) throws TvCommonException {
        return native_getPQVersion(escalerwindow.ordinal());
    }

    /**
     * get PQ version
     *
     * @param int scalerWindow
     * @return String the pq version
     * @throws TvCommonException
     */
    public final String getPQVersion(int scalerWindow) throws TvCommonException {
        return native_getPQVersion(scalerWindow);
    }

    public native final String native_getPQVersion(int escalerwindow) throws TvCommonException;

    /**
     * To get QMAP Current Table Index
     *
     * @param ipIndex short
     * @return int
     * @throws TvCommonException
     */
    public native final int getQmapCurrentTableIdx(short ipIndex) throws TvCommonException;

    /**
     * Gets QMAP IP Name
     *
     * @param ipIndex short ( IP index)
     * @return String (IP name)
     * @throws TvCommonException
     */
    public native final String getQmapIpName(short ipIndex) throws TvCommonException;

    /**
     * To get QMAP Table Name
     *
     * @param ipIndex short (IP index)
     * @param tableIndex short (Table index)
     * @return String (Table name)
     * @throws TvCommonException
     */
    public native final String getQmapTableName(short ipIndex, short tableIndex)
    throws TvCommonException;

    /**
     * Load PQ table
     *
     * @param tableIndex int (Table index)
     * @param ipIndex int (IP Index)
     * @throws TvCommonException
     */
    public native final void loadPqTable(int tableIndex, int ipIndex) throws TvCommonException;

    /**
     * Check watch dog status
     *
     * @return boolean
     * @throws TvCommonException
     */
    public native final boolean isWdtOn() throws TvCommonException;

    /**
     * Enable watch dog status
     *
     * @return boolean
     * @throws TvCommonException
     */
    public native final boolean enableWdt() throws TvCommonException;

    /**
     * Disable watch dog status
     *
     * @return boolean
     * @throws TvCommonException
     */
    public native final boolean disableWdt() throws TvCommonException;

    /**
     * Check Uart Bus status
     *
     * @return boolean
     * @throws TvCommonException
     */
    public native final boolean isUartOn() throws TvCommonException;

    /**
     * Check AgingMode status
     *
     * @return boolean
     * @throws TvCommonException
     */
    public native final boolean isAgingModeOn() throws TvCommonException;

    /**
     * Enable Uart
     *
     * @return boolean
     * @throws TvCommonException
     */
    public native final boolean enableUart() throws TvCommonException;

    /**
     * Disable Uart
     *
     * @return boolean
     * @throws TvCommonException
     */
    public native final boolean disableUart() throws TvCommonException;

    /**
     * Store database to USB
     *
     * @return boolean
     * @throws TvCommonException
     */
    public native final boolean storeDbToUsb() throws TvCommonException;

    /**
     * Restore database from USB
     *
     * @return boolean
     * @throws TvCommonException
     */
    public native final boolean restoreDbFromUsb() throws TvCommonException;

    /**
     * Set VD parameters for non-standard signal in factory menu.
     *
     * @param factoryNsVdSetVo FactoryNsVdSetVO
     * @throws TvCommonException
     */
    public native final void setFactoryVdParameter(FactoryNsVdSet factoryNsVdSetVo)
    throws TvCommonException;

    /**
     * Sets initial VD parameters for non-standard signal in factory menu.
     *
     * @param factoryNsVdSetVo FactoryNsVdSetVO
     * @throws TvCommonException
     */
    public native final void setFactoryVdInitParameter(FactoryNsVdSet factoryNsVdSetVo)
    throws TvCommonException;

    /**
     * To set MBoot UART on/off env
     *
     * @param on boolean
     * @throws TvCommonException
     */
    public native final void setUartEnv(boolean on) throws TvCommonException;

    /**
     * To determine that wether the uart debug message is printed by setting
     * environment parameter of MBoot.
     *
     * @return boolean
     * @throws TvCommonException
     */
    public native final boolean getUartEnv() throws TvCommonException;

    /**
     * Update SSC Settting
     *
     * @return boolean
     * @throws TvCommonException
     */
    public native final boolean updateSscParameter() throws TvCommonException;

    /**
     * Get fireware version
     *
     * @param type EnumFwType
     * @return int
     * @throws TvCommonException
     */
    public int getFwVersion(EnumFwType type) throws TvCommonException {
        return native_getFwVersion(type.ordinal());
    }

    private native int native_getFwVersion(int type);

    /**
     * Get software version.
     *
     * @return String version string
     * @throws TvCommonException
     */
    public native String getSoftwareVersion() throws TvCommonException;

    public native void setDebugMode(boolean mode) throws TvCommonException;

    /**
     * Stop tv service
     *
     * @throws TvCommonException
     */
    public native void stopTvService() throws TvCommonException;

    /**
     * Restore atv program to factory default value.
     *
     * @param cityIndex short Specify the index of city
     * @return None
     * @throws TvCommonException
     */

    public native void restoreFactoryAtvProgramTable(short cityIndex) throws TvCommonException;

    /**
     * Restore dtv program to factory default value.
     *
     * @param cityIndex short Specify the index of city
     * @throws TvCommonException
     */

    public native void restoreFactoryDtvProgramTable(short cityIndex) throws TvCommonException;

    /**
     * set PQ parameter via usb key
     *
     * @throws TvCommonException
     */
    public native void setPQParameterViaUsbKey() throws TvCommonException;

    /**
     * set HDCP key via usb key
     *
     * @throws TvCommonException
     */
    public native void setHDCPKeyViaUsbKey() throws TvCommonException;

    /**
     * set CI Plus key via usb key
     *
     * @throws TvCommonException
     */
    public native void setCIPlusKeyViaUsbKey() throws TvCommonException;

    /**
     * set MAC address via usb key
     *
     * @throws TvCommonException
     */
    public native void setMACAddrViaUsbKey() throws TvCommonException;

    /**
     * Get MAC address.
     *
     * @return String version string
     * @throws TvCommonException
     */
    public native String getMACAddrString() throws TvCommonException;

    /**
     * enable uart debug
     *
     * @return boolean true is set succeed, false is failed
     * @throws TvCommonException
     * @deprecated Use {@link startUartDebug()}.
     */
    @Deprecated
    public native boolean enableUartDebug() throws TvCommonException;

    /**
     * start uart debug
     *
     * @return boolean true is set succeed, false is failed
     * @throws TvCommonException
     */
    public native boolean startUartDebug() throws TvCommonException;

    /**
     * switch uart
     *
     * @return boolean true is switch succeed, false is failed
     * @throws TvCommonException
     * @deprecated Use {@link uartSwitch()}.
     */
    @Deprecated
    public native boolean switchUart() throws TvCommonException;

    /**
     * uart switch
     *
     * @return boolean true is switch succeed, false is failed
     * @throws TvCommonException
     */
    public native boolean uartSwitch() throws TvCommonException;


    /**
     * get ResolutionMapping index
     *
     * @param eInputSrc TvOsType.EnumInputSource
     * @return short the ResolutionMapping index
     * @throws TvCommonException
     */
    public short getResolutionMappingIndex(TvOsType.EnumInputSource eInputSrc)
    throws TvCommonException {
        return native_getResolutionMappingIndex(eInputSrc.ordinal());
    }

    private native short native_getResolutionMappingIndex(int inputSource) throws TvCommonException;

    /**
     * set Environment for power mode
     *
     * @param ePowerMode EnumAcOnPowerOnMode
     * @return boolean false is set environment failed .true is set environment
     *         success
     * @throws TvCommonException
     */
    public boolean setEnvironmentPowerMode(EnumAcOnPowerOnMode ePowerMode) throws TvCommonException {
        return native_setEnvironmentPowerMode(ePowerMode.ordinal());
    }

    private native boolean native_setEnvironmentPowerMode(int powerMode) throws TvCommonException;

    /**
     * get Environment for power mode
     *
     * @return EnumAcOnPowerOnMode the power on mode
     * @throws TvCommonException
     */

    public EnumAcOnPowerOnMode getEnvironmentPowerMode() throws TvCommonException {
        int iReturn = native_getEnvironmentPowerMode();
        if (iReturn < EnumAcOnPowerOnMode.E_ACON_POWERON_SECONDARY.ordinal()
                || iReturn > EnumAcOnPowerOnMode.E_ACON_POWERON_MAX.ordinal()) {
            throw new TvCommonException("get EnvironmentPowerMode failed \n");
        } else {
            return EnumAcOnPowerOnMode.values()[iReturn];
        }
    }

    private native int native_getEnvironmentPowerMode() throws TvCommonException;

    /**
     * set Environment for power on music volume
     *
     * @param volume short power on music volume
     * @return boolean false is set env failed true is set env success
     * @throws TvCommonException
     */

    public native boolean setEnvironmentPowerOnMusicVolume(short volume) throws TvCommonException;

    /**
     * get Environment for power on music volume
     *
     * @return short get Env Power On music volume
     * @throws TvCommonException
     */
    public native short getEnvironmentPowerOnMusicVolume() throws TvCommonException;

    /**
     * read bytes from i2c
     *
     * @param deviceId int the i2c device id
     * @param address short[] the i2c address
     * @param size short the size will read
     * @return short[] the bytes from i2c
     * @throws TvCommonException
     */
    public native short[] readBytesFromI2C(int deviceId, short[] address, short size)
    throws TvCommonException;

    /**
     * write bytes to i2c devices
     *
     * @param deviceId int the i2c device id
     * @param address short[] the i2c address
     * @param writeData short[] the data will write to i2c
     * @return boolean true is write is succeed false is failed to write
     * @throws TvCommonException
     */
    public native boolean writeBytesToI2C(int deviceId, short[] address, short[] writeData)
    throws TvCommonException;

    /**
     * get PQ file path base on
     *
     * @param epqUpdateFile EnumPqUpdateFile which the pq path will update
     * @return String the pq path
     * @throws TvCommonException
     */
    public final String getUpdatePqFilePath(EnumPqUpdateFile epqUpdateFile)
    throws TvCommonException {
        return getUpdatePqFilePath(epqUpdateFile.ordinal());
    }

    private native final String getUpdatePqFilePath(int pqUpdateFile) throws TvCommonException;

    /**
     * update pq ini files
     *
     * @throws TvCommonException
     */
    public native final void updatePqIniFiles() throws TvCommonException;

    /**
     * disable PVR RecordAll
     *
     * @throws TvCommonException
     */
    public native final boolean disablePVRRecordAll() throws TvCommonException;

    /**
     * enable PVR RecordAll
     *
     * @throws TvCommonException
     */
    public native final boolean enablePVRRecordAll() throws TvCommonException;

    /**
     * is PVR RecordAll On or not
     *
     * @throws TvCommonException
     */
    public native final boolean isPVRRecordAllOn() throws TvCommonException;

    /**
     * Get ursa Version Info
     *
     * @throws TvCommonException
     */
    public native final UrsaVersionInfo ursaGetVersionInfo() throws TvCommonException;

    /**
     * Get panel Version Info
     *
     * @throws TvCommonException
     */
    public native final PanelVersionInfo panelGetVersionInfo() throws TvCommonException;

    /**
     * Set Wake on lan Status
     *
     * @throws TvCommonException
     */
    public native void setWOLEnableStatus(boolean flag) throws TvCommonException;

    /**
     * Get Wake on lan Status
     *
     * @throws TvCommonException
     */
    public native boolean getWOLEnableStatus() throws TvCommonException;
    /**
     * Get Wake on lan Status
     *
     * @throws TvCommonException
     */
    public native boolean setXvyccDataFromPanel(float fRedX, float fRedY, float fGreenX, float fGreenY, float fBlueX, float fBlueY, float fWhiteX, float fWhiteY, int eWin) throws TvCommonException;

    /**
     * Get ip info
     *
     * @param void
     * @return the ip info enabled
     * @throws TvCommonException
     */
    public native byte[] getEnableIPInfo() throws TvCommonException;

    /**
     * set hdcpkey to env
     *
     * @param data short[] data to be set
     * @param miraflag boolean to be set miracast hdcpkey yes or no
     * @return boolean: true for success, false for failure
     */
    public native final boolean EosSetHDCPKey(short[] data, boolean miraflag) throws TvCommonException;

    /**
     * get hdcpkey from env
     *
     * @param size int the size will read
     * @param miraflag boolean to be set miracast hdcpkey yes or no
     * @return short[] the bytes from env
     */
    public native final short[] EosGetHDCPKey(int size, boolean miraflag) throws TvCommonException;
       /*
     * getTunerStatus
     * 
     * @return boolean
     * @throws TvCommonException
     */
  
    public native final boolean getTunerStatus() throws TvCommonException;
}
