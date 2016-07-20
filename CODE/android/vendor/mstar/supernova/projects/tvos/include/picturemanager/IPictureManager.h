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
//    supplied together with third party`s software and the use of MStar
//    Software may require additional licenses from third parties.
//    Therefore, you hereby agree it is your sole responsibility to separately
//    obtain any and all third party right and license necessary for your use of
//    such third party`s software.
//
// 3. MStar Software and any modification/derivatives thereof shall be deemed as
//    MStar`s confidential information and you agree to keep MStar`s
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
//    MStar Software in conjunction with your or your customer`s product
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
#ifndef _IPICTUREMANAGER_H_
#define _IPICTUREMANAGER_H_

#include <binder/IInterface.h>
#include <binder/Parcel.h>
#include <binder/IPCThreadState.h>
#include "PictureManagerTypeDefine.h"
using namespace android;

class IPictureManager: public IInterface
{
public:

    DECLARE_META_INTERFACE(PictureManager);
    virtual void disconnect() = 0;
    //API ADD

    virtual void disableDlc(void) =0;
    virtual void disableOverScan(void) = 0;
    virtual void disableWindow(void) = 0;
    virtual void enableDlc(void) = 0;
    virtual void enableOverScan(void) = 0;
    virtual void enableWindow(void) = 0;
    virtual bool freezeImage(void) = 0;
    virtual int32_t getBacklight() = 0;
    virtual int32_t getBacklightMaxValue() = 0;
    virtual int32_t getBacklightMinValue() = 0;
    virtual int32_t getDemoMode(void) = 0;
    virtual bool getDynamicContrastCurve(int32_t* outCurve) = 0;
    virtual void getPanelWidthHeight(PanelProperty &property) = 0;
    virtual int16_t getDlcAverageLuma() = 0;

    virtual bool IsImageFreezed(void) = 0;
    virtual bool isOverscanEnabled(void) = 0;

    virtual bool scaleWindow() = 0;
    virtual bool selectWindow(int32_t windowId) = 0;
    virtual void setAspectRatio(int32_t enAspectRatioTYpe) = 0;
    virtual int32_t getAspectRatio() = 0;
    virtual void setBacklight(int32_t value) = 0;
    virtual void setColorTemperature(PQL_COLOR_TEMP_DATA &pstColorTemp) = 0;
    virtual void setColorTemperatureEX(PQL_COLOR_TEMPEX_DATA &pstColorTemp) = 0;
    virtual void setCropWindow(int32_t h, int32_t w, int32_t y, int32_t x) = 0;
    virtual void setDemoMode(int32_t enMsMweType) = 0;
    virtual void setDisplayWindow(int32_t h, int32_t w, int32_t y, int32_t x) = 0;
    virtual void setDynamicContrastCurve(int32_t *normalCurve, int32_t *lightCurve, int32_t *darkCurve) = 0;

    virtual void setFilm(int32_t enMsFile) = 0;
    virtual void setMfc(EN_MFC_MODE enMode) = 0;
    virtual EN_MFC_MODE getMfc() = 0;
    virtual bool setMpegNoiseReduction(int32_t enMNRMode) = 0;
    virtual bool setNoiseReduction(int32_t nr) = 0;
    virtual void setOutputPattern(bool bEnable, int16_t u16Red, int16_t u16Green, int16_t u16Blue) = 0;

    virtual void setOverscan (int32_t bottom, int32_t top, int32_t right, int32_t left) = 0;

    virtual void setPictureModeBrightness(int16_t value) = 0;
    virtual void setPictureModeBrightness(int32_t setLocationType, int32_t value) = 0;
    virtual bool GetPictureModeBrightness(int16_t * const value) = 0;
    virtual void setPictureModeColor(int16_t value) = 0;
    virtual bool GetPictureModeSaturation(int16_t * const value) = 0;
    virtual void setPictureModeContrast(int16_t value) = 0;
    virtual bool GetPictureModeContrast(int16_t * const value) = 0;
    virtual void setPictureModeSharpness(int16_t value) = 0;
    virtual bool GetPictureModeSharpness(int16_t * const value) = 0;
    virtual void setPictureModeTint(int16_t value) = 0;
    virtual bool GetPictureModeHue(int16_t * const value) = 0;
    virtual void setPictureModeInputSource(int32_t inputSource) = 0;

    virtual void setWindowInvisible() = 0;
    virtual void setWindowVisible() = 0;

    virtual bool unFreezeImage(void) = 0;
    virtual void setDebugMode(bool mode)=0;

    virtual bool disableOsdWindow(int32_t win) = 0;
    virtual bool disableAllOsdWindow() = 0;
    virtual bool setOsdWindow(int32_t win, uint16_t u16StartX, uint16_t u16Width, uint16_t u16StartY, uint16_t u16Height) = 0;
    virtual void setColorRange(bool colorRange0_255) = 0;

    virtual int32_t getCustomerPqRuleNumber() = 0;
    virtual int32_t getStatusNumberByCustomerPqRule(int32_t ruleType) = 0;
    virtual bool setStatusByCustomerPqRule(int32_t ruleType, int32_t ruleStatus) = 0;

    virtual bool moveWindow() =0;

    virtual void enableBacklight() =0;
    virtual void disableBacklight() =0;
    
    virtual void generateTestPattern(TEST_PATTERN_MODE ePatternMode, uint32_t u32Length, void* para) = 0;

    virtual void getDlcLumArray(int32_t *pArray, int32_t arrayLen) =0;
    virtual int32_t getDlcLumAverageTemporary() =0;
    virtual int32_t getDlcLumTotalCount() =0;

    virtual void getPixelRgb(int32_t eStage, int16_t x, int16_t y, int32_t eWindow, GET_RGB_DATA &rgbData) =0;

    virtual bool getPixelInfo(Screen_Pixel_Info *pPixInfo) = 0;

    virtual bool switchDlcCurve(int16_t dlcCurveIndex) =0;

    virtual bool setSwingLevel(int16_t swingLevel) =0;
    virtual int16_t getDlcHistogramMax() =0;
    virtual int16_t getDlcHistogramMin() =0;

    virtual bool forceFreerun(bool bEnable,bool b3D) =0;

    virtual bool setLocalDimmingMode(int16_t localDimingMode) =0;
    virtual bool setLocalDimmingBrightLevel(int16_t localDimingBrightLevel) =0;
    virtual bool turnOffLocalDimmingBacklight(bool bTurnOffLDBL) =0;
    virtual void setResolution(int8_t resolution) = 0;
    virtual int8_t getResolution() = 0;
    virtual void setReproduceRate(int32_t rate) = 0;
    virtual int32_t getReproduceRate() = 0;
    virtual bool enter4K2KMode(bool bEnable) = 0;
    virtual void lock4K2KMode(bool bLock ) = 0;
    virtual int32_t get4K2KMode() = 0;
    virtual bool set4K2KMode(int32_t enOutPutTimming,int32_t enUrsaMode) = 0;
    virtual bool is4K2KMode(bool bEnable) = 0;
    virtual bool is3DTVPlugedIn() = 0;
    virtual bool setUltraClear(bool bEnable) = 0;
    virtual bool autoHDMIColorRange() = 0;
    virtual int32_t getHDMIColorFormat() = 0;
    virtual bool disableAllDualWinMode() = 0;
    virtual void EnableVideoOut(bool bEnable) = 0;
    virtual bool setHLinearScaling(bool bEnable, bool bSign, uint16_t u16Delta) =0;
    virtual bool setMEMCMode(String8 cmd) =0;
    virtual void setScalerGammaByIndex(int8_t u8Index) = 0;
    virtual bool enableXvyccCompensation(bool bEnable, int32_t eWin) = 0;
    virtual bool keepScalerOutput4k2k(bool bEnable) = 0;
    virtual bool setHdmiPc(bool bEn) = 0;
    virtual bool getHdmiPc() = 0;
//------------------------------------------------------------------------------------
    virtual bool panelInitial(String8 panelIniName) = 0;
    virtual bool setGammaParameter(int32_t index, int32_t value) = 0;
    virtual bool calGammaTable(GAMMA_TABLE *rgbData, int32_t MapMode) = 0;
    virtual bool setScalerGammaTable (GAMMA_TABLE *gammaTable) = 0;
    virtual uint8_t getScalerMotion() = 0;
//------------------------------------------------------------------------------------
    virtual bool isSupportedZoom() = 0;
    virtual bool setxvYCCEnable(bool bEnable, int8_t u8xvYCCmode) = 0;

    virtual bool setOsdResolution(int32_t width, int32_t height) = 0;
    virtual bool lockOutputTiming(int32_t eOutputTiming) = 0;
    virtual bool unlockOutputTiming() = 0;
    virtual int16_t GetSupportedTimingList(St_Timing_Info *pTimingInfoList, int16_t u16ListSize) = 0;
    virtual int16_t GetSupportedTimingListCount() = 0;
    virtual uint16_t getCurrentTimingId() = 0;

    virtual bool getHdrLevel(E_HDR_LEVEL *pLevel, int32_t eWin) = 0;
    virtual bool setHdrLevel(E_HDR_LEVEL enlevel, int32_t eWin) = 0;
    virtual bool getAutoDetectHdrLevel(int32_t eWin) = 0;
    virtual bool setAutoDetectHdrLevel(bool bAuto, int32_t eWin) = 0;
    virtual bool IsHdrEnable(int32_t eWin) = 0;
    virtual uint16_t getPQDelayTime() = 0;
    virtual void getColorTemperature(PQL_COLOR_TEMPEX_DATA &pstColorTemp) = 0;
	// EosTek Patch Begin
     //ashton: for wb adjust
      virtual void asGetWbAdjustStar() = 0;
     virtual void asGetWbAdjustExit() = 0;
    // EosTek Patch End	
};

// ----------------------------------------------------------------------------

class BnPictureManager: public BnInterface<IPictureManager>
{
public:

    virtual status_t onTransact(uint32_t code,
                                const Parcel& data,
                                Parcel* reply,
                                uint32_t flags = 0);

};

#endif // _IPICTURE_H_
