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

import com.mstar.android.tvapi.common.vo.ColorTemperatureExData;
import com.mstar.android.tvapi.common.vo.VideoWindowType;
import com.mstar.android.tvapi.common.vo.VideoInfo;
import com.mstar.android.tvapi.common.vo.PanelProperty;
import com.mstar.android.tvapi.common.vo.TimingInfo;

interface ITvPicture {
    boolean setPictureModeIdx(in int ePicMode);
    int getPictureModeIdx();
    int getIsPcMode();
    void setITC(int ITC);
    int getITC();
    boolean setVideoArc(in int eArcIdx);
    int getVideoArc();
    boolean setVideoItem(in int eIndex, int value);
    int getVideoItem(in int eIndex);
    boolean setBacklight(int value);
    int getBacklight();
    boolean setColorTempIdx(in int eColorTemp);
    int getColorTempIdx();
    void setColorTempratureEx(in ColorTemperatureExData stColorTemp);
    ColorTemperatureExData getColorTempratureEx();
    boolean setNR(in int eNRIdx);
    int getNR();
    boolean  setPCHPos(int hpos);
    int getPCHPos();
    boolean  setPCVPos(int vpos);
    int getPCVPos();
    boolean  setPCClock(int clock);
    int getPCClock();
    boolean  setPCPhase(int phase);
    int getPCPhase();
    boolean execAutoPc();
    boolean freezeImage();
    boolean unFreezeImage();
    boolean isImageFreezed();
    void setDisplayWindow(in VideoWindowType videoWindowType);
    void enableBacklight();
    void disableBacklight();
    VideoInfo getVideoInfo();

    /**
     * Set HDMI color range index Rang: 0~1, 0: 0-255; 1: 16-235
     *
     * @param value
     * @return Result status.
     */
    boolean setColorRange(byte value);

    /**
     * Get HDMI color range index Rang: 0~1, 0: 0-255; 1: 16-235
     *
     * @return color range index
     */
    byte getColorRange();

    /**
     * Set video film mode
     *
     * @param eMode EnumFilm    Enumerate of film mode
     * @return Result status.
     */
    boolean setFilmMode(int eMode);

    /**
     * Get video film mode
     *
     * @return eMode EnumFilm    Enumerate of film mode
     */
    int getFilmMode();

    /**
     * Enable DLC
     * @return    TRUE - enable DLC success, FALSE - enable DLC fail.
     */
    boolean enableDlc();

    /**
     * Disable DLC
     * @return    TRUE - disable DLC success, FALSE - disable DLC fail.
     */
    boolean disableDlc();

    /**
     * Check if enable DLC or not
     * @return    TRUE - DLC enabled, FALSE - DLC disabled.
     */
    boolean isDlcEnabled();

    /**
     * Enable DCC
     * @return    TRUE - enable DCC success, FALSE - enable DCC fail.
     */
    boolean enableDcc();

    /**
     * Disable DCC
     * @return    TRUE - disable DCC success, FALSE - disable DCC fail.
     */
    boolean disableDcc();

    /**
     * Check if enable DCC or not
     * @return    TRUE - DCC enabled, FALSE - DCC disabled.
     */
    boolean isDccEnabled();

    /**
     * Enable DBC
     * @return    TRUE - enable DBC success, FALSE - enable DBC fail.
     */
    boolean enableDbc();

    /**
     * Disable DBC
     * @return    TRUE - disable DBC success, FALSE - disable DBC fail.
     */
    boolean disableDbc();

    /**
     * Check if enable DBC or not
     * @return    TRUE - DBC enabled, FALSE - DBC disabled.
     */
    boolean isDbcEnabled();

    /**
     * setVideoItem
     * @return    TRUE - set videoitem success, FALSE -  set videoitem fail.
     */
    boolean setVideoItemByInputSource(int eIndex, int value, int input);

    /**
     * getVideoItem
     * @return    get VideoItem value.
     */
    int getVideoItemByInputSource(int eIndex, int input);
    int getReproduceRate();
    void setReproduceRate(int rate);
    void setResolution(byte resolution);
    byte getResolution();
    boolean setMEMCMode(String interfaceCommand);

    /**
     * getPcModeInfo
     * @return PCClock PCPhase PCHPos PCVPos.
     */
    int[] getPcModeInfo();

    PanelProperty getPanelWidthHeight();

    void setDemoMode(int enMweType);

    int getDemoMode();

    boolean is4K2KMode(boolean bEn);

    void setUClearStatus(boolean bFlag);

    boolean isUClearOn();

    boolean setMpegNR(int eMpNRIdx);

    int getMpegNR();

    boolean enableXvyccCompensation(boolean bEn, int eWin);

    boolean setxvYCCEnable(boolean bEn, int eMode);

    boolean getxvYCCEnable();

    int getHDMIColorFormat();

    void forceThreadSleep(boolean flag);

    boolean turnOffLocalDimmingBacklight(boolean bOffFlag);

    void setMfcMode(int mode);

    boolean isSupportedZoom();

    /**
     * Get aspect ration list
     *
     * @return boolean[] : support aspect ration types list
     */
    boolean[] getAspectRationList();

    int getSupportedTimingListCount();

    int getCurrentTimingId();

    TimingInfo[] getSupportedTimingList();

    /**
     * EosTek Patch begin 
     */
    boolean setPictureValue(int curPicMode, int lastPicMode, int index, int value);
	
    boolean setColorTempIdxAndRGB(int pictureModeType,int eColorTemp, int curSource);
	
    ColorTemperatureExData getWbGainOffsetEx(int eColorTemp, int curSource);
	
    void setWbGainOffsetEx(in ColorTemperatureExData colorTempExData, int eColorTemp, int curSource);
	
    int[] getVideoItems(int inputSrc,int pictureMode);
	
    int[] getPCImage();

    /**
     * EosTek Patch End 
     */
}
