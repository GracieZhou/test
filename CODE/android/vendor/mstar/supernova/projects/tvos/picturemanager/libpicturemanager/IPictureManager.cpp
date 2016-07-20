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
#define LOG_TAG "IPictureManager"
#include <utils/Log.h>

#include "IPictureManager.h"
#include <sys/types.h>

#define DEBUGG printf("%s: [%d]::Client %s\n",__FILE__,__LINE__, __FUNCTION__);

enum
{
    DISCONNECT = IBinder::FIRST_CALL_TRANSACTION,
    PICTURE_DISABLEDLC =IBinder::FIRST_CALL_TRANSACTION+1,
    PICTURE_DISABLEOVERSCAN =IBinder::FIRST_CALL_TRANSACTION+2,
    PICTURE_DISABLEWINDOW =IBinder::FIRST_CALL_TRANSACTION+3,
    PICTURE_ENABLEDLC =IBinder::FIRST_CALL_TRANSACTION+4,
    PICTURE_ENABLEOVERSCAN =IBinder::FIRST_CALL_TRANSACTION+5,
    PICTURE_ENABLEWINDOW =IBinder::FIRST_CALL_TRANSACTION+6,
    PICTURE_FreezeImage =IBinder::FIRST_CALL_TRANSACTION+7,
    PICTURE_GETBACKLIGHT =IBinder::FIRST_CALL_TRANSACTION+8,
    PICTURE_GETBACKLIGHTMAXVALUE =IBinder::FIRST_CALL_TRANSACTION+9,
    PICTURE_GETBACKLIGHTMINVALUE =IBinder::FIRST_CALL_TRANSACTION+10,
    PICTURE_GetDemoMode =IBinder::FIRST_CALL_TRANSACTION+11,
    PICTURE_GetDynamicContrastCurve =IBinder::FIRST_CALL_TRANSACTION+12,
    PICTURE_GetPanelWidthHeight =IBinder::FIRST_CALL_TRANSACTION+13,
    PICTURE_getDlcAverageLuma =IBinder::FIRST_CALL_TRANSACTION+14,
    PICTURE_IsImageFreezed =IBinder::FIRST_CALL_TRANSACTION+15,
    PICTURE_IsOverScanEnabled =IBinder::FIRST_CALL_TRANSACTION+16,
    PICTURE_ScaleWindow =IBinder::FIRST_CALL_TRANSACTION+17,
    PICTURE_SelectWindow =IBinder::FIRST_CALL_TRANSACTION+18,
    PICTURE_SETASPECTRATIO =IBinder::FIRST_CALL_TRANSACTION+19,
    PICTURE_SetBacklight =IBinder::FIRST_CALL_TRANSACTION+20,
    PICTURE_SetColorTemperature =IBinder::FIRST_CALL_TRANSACTION+21,
    PICTURE_SetCropWindow =IBinder::FIRST_CALL_TRANSACTION+22,
    PICTURE_SetDemoMode =IBinder::FIRST_CALL_TRANSACTION+23,
    PICTURE_SetDisplayWindow =IBinder::FIRST_CALL_TRANSACTION+24,
    PICTURE_SetDynamicContrastCurve =IBinder::FIRST_CALL_TRANSACTION+25,
    PICTURE_SETFILM =IBinder::FIRST_CALL_TRANSACTION+26,
    PICTURE_SETMFC =IBinder::FIRST_CALL_TRANSACTION+27,
    PICTURE_SETMPEGNR =IBinder::FIRST_CALL_TRANSACTION+28,
    PICTURE_SetNoiseReduction =IBinder::FIRST_CALL_TRANSACTION+29,
    PICTURE_SetOutputPattern =IBinder::FIRST_CALL_TRANSACTION+30,
    PICTURE_SetOverScan =IBinder::FIRST_CALL_TRANSACTION+31,
    PICTURE_SetPictureModeBrightness1 =IBinder::FIRST_CALL_TRANSACTION+32,
    PICTURE_SetPictureModeBrightness2 =IBinder::FIRST_CALL_TRANSACTION+33,
    PICTURE_setPictureModeColor =IBinder::FIRST_CALL_TRANSACTION+34,
    PICTURE_setPictureModeContrast =IBinder::FIRST_CALL_TRANSACTION+35,
    PICTURE_setPictureModeSharpness =IBinder::FIRST_CALL_TRANSACTION+36,
    PICTURE_setPictureModeTint =IBinder::FIRST_CALL_TRANSACTION+37,
    PICTURE_SetPictureModeInputSource =IBinder::FIRST_CALL_TRANSACTION+38,
//    PICTURE_SetUltraClear =IBinder::FIRST_CALL_TRANSACTION+39,
    PICTURE_setWindowInvisible =IBinder::FIRST_CALL_TRANSACTION+40,
    PICTURE_setWindowVisible =IBinder::FIRST_CALL_TRANSACTION+41,
    PICTURE_UnFreezeImage =IBinder::FIRST_CALL_TRANSACTION+42,
    PICTURE_SetDebugMode =IBinder::FIRST_CALL_TRANSACTION+43,
    PICTURE_disableOsdWindow =IBinder::FIRST_CALL_TRANSACTION+44,
    PICTURE_disableAllOsdWindow =IBinder::FIRST_CALL_TRANSACTION+45,
    PICTURE_setOsdWindow =IBinder::FIRST_CALL_TRANSACTION+46,
    PICTURE_setColorRange =IBinder::FIRST_CALL_TRANSACTION+47,
    PICTURE_getCustomerPqRuleNumber =IBinder::FIRST_CALL_TRANSACTION+48,
    PICTURE_getStatusNumberByCustomerPqRule =IBinder::FIRST_CALL_TRANSACTION+49,
    PICTURE_setStatusByCustomerPqRule =IBinder::FIRST_CALL_TRANSACTION+50,
    PICTURE_moveWindow =IBinder::FIRST_CALL_TRANSACTION+51,
    PICTURE_enableBacklight =IBinder::FIRST_CALL_TRANSACTION+52,
    PICTURE_disableBacklight =IBinder::FIRST_CALL_TRANSACTION+53,
    PICTURE_getDlcLumArray =IBinder::FIRST_CALL_TRANSACTION+54,
    PICTURE_getDlcLumAverageTemporary =IBinder::FIRST_CALL_TRANSACTION+55,
    PICTURE_getDlcLumTotalCount =IBinder::FIRST_CALL_TRANSACTION+56,
    PICTURE_switchDlcCurve =IBinder::FIRST_CALL_TRANSACTION+57,
    PICTURE_getPixelRgb =IBinder::FIRST_CALL_TRANSACTION+58,
    PICTURE_setSwingLevel =IBinder::FIRST_CALL_TRANSACTION+59,
    PICTURE_getDlcHistogramMax =IBinder::FIRST_CALL_TRANSACTION+60,
    PICTURE_getDlcHistogramMin =IBinder::FIRST_CALL_TRANSACTION+61,
    PICTURE_forceFreerun =IBinder::FIRST_CALL_TRANSACTION+62,
    PICTURE_setLocalDimmingMode =IBinder::FIRST_CALL_TRANSACTION+63,
    PICTURE_setLocalDimmingBrightLevel =IBinder::FIRST_CALL_TRANSACTION+64,
    PICTURE_turnOffLocalDimmingBacklight =IBinder::FIRST_CALL_TRANSACTION+65,
    SETRESOLUTION =IBinder::FIRST_CALL_TRANSACTION+66,
    GETRESOLUTION =IBinder::FIRST_CALL_TRANSACTION+67,
    SETREPRODUCERATE =IBinder::FIRST_CALL_TRANSACTION+68,
    GETREPRODUCERATE =IBinder::FIRST_CALL_TRANSACTION+69,
    PICTURE_enter4K2KMode =IBinder::FIRST_CALL_TRANSACTION+70,
    PICTURE_setUltraClear =IBinder::FIRST_CALL_TRANSACTION+71,
    PICTURE_autoHDMIColorRange =IBinder::FIRST_CALL_TRANSACTION+72,
    PICTURE_getPixelInfo =IBinder::FIRST_CALL_TRANSACTION+73,
    PICTURE_GetPictureModeBrightness =IBinder::FIRST_CALL_TRANSACTION+74,
    PICTURE_GetPictureModeSaturation =IBinder::FIRST_CALL_TRANSACTION+75,
    PICTURE_GetPictureModeContrast =IBinder::FIRST_CALL_TRANSACTION+76,
    PICTURE_GetPictureModeSharpness =IBinder::FIRST_CALL_TRANSACTION+77,
    PICTURE_GetPictureModeHue =IBinder::FIRST_CALL_TRANSACTION+78,
    PICTURE_GETASPECTRATIO =IBinder::FIRST_CALL_TRANSACTION+79,
    PICTURE_disableAllDualWinMode =IBinder::FIRST_CALL_TRANSACTION+80,
    PICTURE_EnableVideoOut=IBinder::FIRST_CALL_TRANSACTION+81,
    PICTURE_SETHLINEARSCALING = IBinder::FIRST_CALL_TRANSACTION+82,
    PICTURE_SETMEMCMODE =IBinder::FIRST_CALL_TRANSACTION+83,
    PICTURE_is4K2KMode =IBinder::FIRST_CALL_TRANSACTION+84,
    PICTURE_SETSCALERGAMMABYINDEX =IBinder::FIRST_CALL_TRANSACTION+85,
    PICTURE_ENABLEXVYCCCOMPENSATION = IBinder::FIRST_CALL_TRANSACTION + 86,
	PICTURE_lock4K2KMode=IBinder::FIRST_CALL_TRANSACTION+87,
    PICTURE_get4K2KMode=IBinder::FIRST_CALL_TRANSACTION+88,
    PICTURE_set4K2KMode=IBinder::FIRST_CALL_TRANSACTION+89,
    PICTURE_keepScalerOutput4k2k=IBinder::FIRST_CALL_TRANSACTION+90,
    PICTURE_sethdmipc =IBinder::FIRST_CALL_TRANSACTION+91,
    PICTURE_gethdmipc =IBinder::FIRST_CALL_TRANSACTION+92,
//------------------------------------------------------------------------------------
    PICTURE_panelInitial=IBinder::FIRST_CALL_TRANSACTION+93,
    PICTURE_setGammaParameter=IBinder::FIRST_CALL_TRANSACTION+94,
    PICTURE_calGammaTable=IBinder::FIRST_CALL_TRANSACTION+95,
    PICTURE_setScalerGammaTable=IBinder::FIRST_CALL_TRANSACTION+96,
    PICTURE_getScalerMotion=IBinder::FIRST_CALL_TRANSACTION+97,
//------------------------------------------------------------------------------------
    PICTURE_is3DTVPlugedIn =IBinder::FIRST_CALL_TRANSACTION+98,
    PICTURE_isSupportedZoom = IBinder::FIRST_CALL_TRANSACTION+99,
    PICTURE_setxvYCCEnable=IBinder::FIRST_CALL_TRANSACTION+100,
    PICTURE_getHDMIColorFormat=IBinder::FIRST_CALL_TRANSACTION+101,
    PICTURE_SetColorTemperatureEX =IBinder::FIRST_CALL_TRANSACTION+102,
    PICTURE_setOsdResolution = IBinder::FIRST_CALL_TRANSACTION+103,
    PICTURE_lockOutputTiming = IBinder::FIRST_CALL_TRANSACTION+104,
    PICTURE_unlockOutputTiming = IBinder::FIRST_CALL_TRANSACTION+105,
    PICTURE_GetSupportedTimingList = IBinder::FIRST_CALL_TRANSACTION+106,
    PICTURE_GetSupportedTimingListCount = IBinder::FIRST_CALL_TRANSACTION+107,
    PICTURE_GetCurrentTimingId = IBinder::FIRST_CALL_TRANSACTION+108,
    PICTURE_GetHDRLevel =  IBinder::FIRST_CALL_TRANSACTION+109,
    PICTURE_SetHDRLevel = IBinder::FIRST_CALL_TRANSACTION+110,
    PICTURE_GetAutoDetectHdrLevel = IBinder::FIRST_CALL_TRANSACTION+111,
    PICTURE_SetAutoDetectHdrLevel =IBinder::FIRST_CALL_TRANSACTION+112,
    PICTURE_IsHdrEnable = IBinder::FIRST_CALL_TRANSACTION+113,
    PICTURE_GETPQDELAYTIME = IBinder::FIRST_CALL_TRANSACTION+114,
    PICTURE_GETCOLORTEMP = IBinder::FIRST_CALL_TRANSACTION+115,
    PICTURE_GETMFC =IBinder::FIRST_CALL_TRANSACTION+116,
    PICTURE_generateTestPattern =IBinder::FIRST_CALL_TRANSACTION+117,
	 // EosTek Patch Begin
    // ashton: for wb adjust 
    PICTURE_asGetWbAdjustStar =  IBinder::FIRST_CALL_TRANSACTION+118,
    PICTURE_asGetWbAdjustExit =  IBinder::FIRST_CALL_TRANSACTION+119,
    // EosTek Patch End
};

class BpPictureManager: public BpInterface<IPictureManager>
{
public:
    explicit BpPictureManager(const sp<IBinder>& impl);
    virtual void disconnect();
        //api add
    virtual void disableDlc(void);
    virtual void disableOverScan(void);
    virtual void disableWindow(void);
    virtual void enableDlc(void);
    virtual void enableOverScan(void);
    virtual void enableWindow(void);
    virtual bool freezeImage(void);
    virtual int32_t getBacklight();
    virtual int32_t getBacklightMaxValue();
    virtual int32_t getBacklightMinValue();
    virtual int32_t getDemoMode(void);
    virtual bool getDynamicContrastCurve(int32_t* outCurve);
    virtual void getPanelWidthHeight(PanelProperty &property);

    virtual int16_t getDlcAverageLuma();

    virtual bool IsImageFreezed(void);
    virtual bool isOverscanEnabled(void);

    virtual bool scaleWindow();
    virtual bool selectWindow(int32_t windowId);
    virtual void setAspectRatio(int32_t enAspectRatioTYpe);
    virtual int32_t getAspectRatio();

    virtual void setBacklight(int32_t value);
    virtual void setColorTemperature(PQL_COLOR_TEMP_DATA &pstColorTemp);
    virtual void setColorTemperatureEX(PQL_COLOR_TEMPEX_DATA &pstColorTemp);
    virtual void setCropWindow(int32_t h, int32_t w, int32_t y, int32_t x);
    virtual void setDemoMode(int32_t enMsMweType);
    virtual void setDisplayWindow(int32_t h, int32_t w, int32_t y, int32_t x);
    virtual void setDynamicContrastCurve(int32_t *normalCurve, int32_t *lightCurve, int32_t *darkCurve);

    virtual void setFilm(int32_t enMsFile);
    virtual void setMfc(EN_MFC_MODE enMode);
    virtual EN_MFC_MODE getMfc();
    virtual bool setMpegNoiseReduction(int32_t enMNRMode);
    virtual bool setNoiseReduction(int32_t nr);
    virtual void setOutputPattern(bool bEnable, int16_t u16Red, int16_t u16Green, int16_t u16Blue);

    virtual void setOverscan (int32_t bottom, int32_t top, int32_t right, int32_t left);

    virtual void setPictureModeBrightness(int16_t value);
    virtual void setPictureModeBrightness(int32_t setLocationType, int32_t value);
    virtual bool GetPictureModeBrightness(int16_t * const value);
    virtual void setPictureModeColor(int16_t value);
    virtual bool GetPictureModeSaturation(int16_t * const value);
    virtual void setPictureModeContrast(int16_t value);
    virtual bool GetPictureModeContrast(int16_t * const value);
    virtual void setPictureModeSharpness(int16_t value);
    virtual bool GetPictureModeSharpness(int16_t * const value);
    virtual void setPictureModeTint(int16_t value);
    virtual bool GetPictureModeHue(int16_t * const value);
    virtual void setPictureModeInputSource(int32_t inputSource);
    virtual void setWindowInvisible();
    virtual void setWindowVisible();

    virtual bool unFreezeImage(void);
    virtual void setDebugMode(bool mode);

    virtual bool disableOsdWindow(int32_t win);
    virtual bool disableAllOsdWindow();
    virtual bool setOsdWindow(int32_t win, uint16_t u16StartX, uint16_t u16Width, uint16_t u16StartY, uint16_t u16Height);
    virtual void setColorRange(bool colorRange0_255);

    virtual int32_t getCustomerPqRuleNumber();
    virtual int32_t getStatusNumberByCustomerPqRule(int32_t ruleType);
    virtual bool setStatusByCustomerPqRule(int32_t ruleType, int32_t ruleStatus);

    virtual bool moveWindow();

    virtual void enableBacklight();
    virtual void disableBacklight();
    virtual void generateTestPattern(TEST_PATTERN_MODE ePatternMode, uint32_t u32Length, void* para);

    virtual void getDlcLumArray(int32_t *pArray, int32_t arrayLen);
    virtual int32_t getDlcLumAverageTemporary();
    virtual int32_t getDlcLumTotalCount();

    virtual bool switchDlcCurve(int16_t dlcCurveIndex);

    virtual void getPixelRgb(int32_t eStage, int16_t x, int16_t y, int32_t eWindow, GET_RGB_DATA &rgbData);

    virtual bool getPixelInfo(Screen_Pixel_Info *pPixInfo);

    virtual bool setSwingLevel(int16_t swingLevel);
    virtual int16_t getDlcHistogramMax();
    virtual int16_t getDlcHistogramMin();

    virtual bool forceFreerun(bool bEnable,bool b3D);

    virtual bool setLocalDimmingMode(int16_t localDimingMode);
    virtual bool setLocalDimmingBrightLevel(int16_t localDimingBrightLevel);
    virtual bool turnOffLocalDimmingBacklight(bool bTurnOffLDBL);
    virtual void setResolution(int8_t resolution);
    virtual int8_t getResolution();
    virtual void setReproduceRate(int32_t rate);
    virtual int32_t getReproduceRate();
    virtual bool enter4K2KMode(bool bEnable);
    virtual void lock4K2KMode(bool bLock );
    virtual int32_t get4K2KMode();
    virtual bool set4K2KMode(int32_t enOutPutTimming,int32_t enUrsaMode);
    virtual bool is4K2KMode(bool bEnable);
    virtual bool setUltraClear(bool bEnable);
    virtual bool autoHDMIColorRange();
    virtual bool disableAllDualWinMode();
    virtual void EnableVideoOut(bool bEnable);
    virtual bool setHLinearScaling(bool bEnable, bool bSign, uint16_t u16Delta);
    virtual bool setMEMCMode(String8 cmd);
    virtual void setScalerGammaByIndex(int8_t u8Index);
    virtual bool enableXvyccCompensation(bool bEnable, int32_t eWin);
    virtual bool keepScalerOutput4k2k(bool bEnable);
    virtual bool setHdmiPc(bool bEn);
    virtual bool getHdmiPc();
//------------------------------------------------------------------------------------
    virtual bool panelInitial(String8 panelIniName);
    virtual bool setGammaParameter(int32_t index, int32_t value);
    virtual bool calGammaTable(GAMMA_TABLE *rgbData, int32_t MapMode);
    virtual bool setScalerGammaTable (GAMMA_TABLE *gammaTable);
    virtual uint8_t getScalerMotion();
//------------------------------------------------------------------------------------
    virtual bool is3DTVPlugedIn();
    virtual bool isSupportedZoom();
    virtual bool setxvYCCEnable(bool bEnable, int8_t u8xvYCCmode);
    virtual int32_t getHDMIColorFormat();

    virtual bool setOsdResolution(int32_t width, int32_t height);
    virtual bool lockOutputTiming(int32_t eOutputTiming);
    virtual bool unlockOutputTiming();
    virtual int16_t GetSupportedTimingList(St_Timing_Info *pTimingInfoList, int16_t u16ListSize);
    virtual int16_t GetSupportedTimingListCount();
    virtual uint16_t getCurrentTimingId();

    virtual bool getHdrLevel(E_HDR_LEVEL *pLevel, int32_t eWin);
    virtual bool setHdrLevel(E_HDR_LEVEL enlevel, int32_t eWin);
    virtual bool getAutoDetectHdrLevel(int32_t eWin);
    virtual bool setAutoDetectHdrLevel(bool bAuto, int32_t eWin);
    virtual bool IsHdrEnable(int32_t eWin);
    virtual uint16_t getPQDelayTime();
    virtual void getColorTemperature(PQL_COLOR_TEMPEX_DATA &pstColorTemp);
	// EosTek Patch Begin
    //ashton: for wb adjust
    virtual void asGetWbAdjustStar();	
    virtual void asGetWbAdjustExit();	
    // EosTek Patch End	   

};

BpPictureManager::BpPictureManager(const sp<IBinder>& impl)
: BpInterface<IPictureManager>(impl)
{
}


void BpPictureManager::disconnect()
{
    ALOGV("Send DISCONNECT\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(DISCONNECT, data, &reply);
}

//API ADD
void BpPictureManager::disableDlc(void)
{
    ALOGV("Send PICTURE_DISABLEDLC\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_DISABLEDLC, data, &reply);
    return;
}

void BpPictureManager::disableOverScan(void)
{
    ALOGV("Send PICTURE_DISABLEOVERSCAN\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_DISABLEOVERSCAN, data, &reply);
    return;
}

void BpPictureManager::disableWindow(void)
{
    ALOGV("Send PICTURE_DISABLEWINDOW\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_DISABLEWINDOW, data, &reply);
    return;
}

void BpPictureManager::enableDlc(void)
{
    ALOGV("Send PICTURE_ENABLEDLC\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_ENABLEDLC, data, &reply);
    return;
}

void BpPictureManager::enableOverScan(void)
{
    ALOGV("Send PICTURE_ENABLEOVERSCAN\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_ENABLEOVERSCAN, data, &reply);
    return;
}

void BpPictureManager::enableWindow(void)
{
    ALOGV("Send PICTURE_ENABLEWINDOW\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_ENABLEWINDOW, data, &reply);
    return;
}

bool BpPictureManager::freezeImage(void)
{
    ALOGV("Send PICTURE_FreezeImage\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_FreezeImage, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

int32_t BpPictureManager::getBacklight()
{
    ALOGV("Send PICTURE_GETBACKLIGHT\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_GETBACKLIGHT, data, &reply);
    return reply.readInt32();
}

int32_t BpPictureManager::getBacklightMaxValue()
{
    ALOGV("Send PICTURE_GETBACKLIGHTMAXVALUE\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_GETBACKLIGHTMAXVALUE, data, &reply);
    return reply.readInt32();
}

int32_t BpPictureManager::getBacklightMinValue()
{
    ALOGV("Send PICTURE_GETBACKLIGHTMINVALUE\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_GETBACKLIGHTMINVALUE, data, &reply);
    return reply.readInt32();
}

int32_t BpPictureManager::getDemoMode(void)
{
    ALOGV("Send PICTURE_GetDemoMode\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_GetDemoMode, data, &reply);
    return reply.readInt32();
}

bool BpPictureManager::getDynamicContrastCurve(int32_t* outCurve)
{
    ALOGV("Send PICTURE_GetDynamicContrastCurve\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_GetDynamicContrastCurve, data, &reply);
    reply.read(outCurve, CONTRAST_CURVE_LENGTH*sizeof(int32_t));
    return static_cast<bool>(reply.readInt32());
}

void BpPictureManager::getPanelWidthHeight(PanelProperty &property)
{
    ALOGV("Send PICTURE_GetPanelWidthHeight\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_GetPanelWidthHeight, data, &reply);
    property.width = reply.readInt32();
    property.height = reply.readInt32();
    return;
}

int16_t BpPictureManager::getDlcAverageLuma()
{
     ALOGV("Send PICTURE_getDlcAverageLuma\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_getDlcAverageLuma, data, &reply);
    return static_cast<int16_t>(reply.readInt32());
}

bool BpPictureManager::IsImageFreezed(void)
{

    ALOGV("Send PICTURE_IsImageFreezed\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_IsImageFreezed, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

bool BpPictureManager::isOverscanEnabled(void)
{
     ALOGV("Send PICTURE_IsOverScanEnabled\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_IsOverScanEnabled, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

bool BpPictureManager::scaleWindow()
{
     ALOGV("Send PICTURE_ScaleWindow\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_ScaleWindow, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

bool BpPictureManager::selectWindow(int32_t windowId)
{
    ALOGV("Send PICTURE_SelectWindow\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(windowId);
    remote()->transact(PICTURE_SelectWindow, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

void BpPictureManager::setAspectRatio(int32_t enAspectRatioTYpe)
{
    ALOGV("Send PICTURE_SETASPECTRATIO\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(enAspectRatioTYpe);
    remote()->transact(PICTURE_SETASPECTRATIO, data, &reply);
    return ;
}

int32_t BpPictureManager::getAspectRatio()
{
    ALOGV("Send PICTURE_SETASPECTRATIO\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_GETASPECTRATIO, data, &reply);
    return reply.readInt32();
}


void BpPictureManager::setBacklight(int32_t value)
{
    ALOGV("Send PICTURE_SetBacklight\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(value);
    remote()->transact(PICTURE_SetBacklight, data, &reply);
    return ;
}

void BpPictureManager::setColorTemperature(PQL_COLOR_TEMP_DATA &pstColorTemp)
{
    ALOGV("Send PICTURE_SetColorTemperature\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());

    data.writeInt32(pstColorTemp.u8RedGain);
    data.writeInt32(pstColorTemp.u8GreenGain);
    data.writeInt32(pstColorTemp.u8BlueGain);
    data.writeInt32(pstColorTemp.u8RedOffset);
    data.writeInt32(pstColorTemp.u8GreenOffset);
    data.writeInt32(pstColorTemp.u8BlueOffset);

    remote()->transact(PICTURE_SetColorTemperature, data, &reply);
    return ;
}

void BpPictureManager::setColorTemperatureEX(PQL_COLOR_TEMPEX_DATA &pstColorTemp)
{
    ALOGV("Send PICTURE_SetColorTemperature\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());

    data.writeInt32(pstColorTemp.u16RedGain);
    data.writeInt32(pstColorTemp.u16GreenGain);
    data.writeInt32(pstColorTemp.u16BlueGain);
    data.writeInt32(pstColorTemp.u16RedOffset);
    data.writeInt32(pstColorTemp.u16GreenOffset);
    data.writeInt32(pstColorTemp.u16BlueOffset);

    remote()->transact(PICTURE_SetColorTemperatureEX, data, &reply);
    return ;
}

void BpPictureManager::setCropWindow(int32_t h, int32_t w, int32_t y, int32_t x)
{
    ALOGV("Send PICTURE_SetCropWindow\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());

    data.writeInt32(h);
    data.writeInt32(w);
    data.writeInt32(y);
    data.writeInt32(x);

    remote()->transact(PICTURE_SetCropWindow, data, &reply);
    return ;
}

void BpPictureManager::setDemoMode(int32_t enMsMweType)
{
    ALOGV("Send PICTURE_SetDemoMode\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(enMsMweType);
    remote()->transact(PICTURE_SetDemoMode, data, &reply);
    return ;
}

void BpPictureManager::setDisplayWindow(int32_t h, int32_t w, int32_t y, int32_t x)
{
    ALOGV("Send PICTURE_SetDisplayWindow\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());

    data.writeInt32(h);
    data.writeInt32(w);
    data.writeInt32(y);
    data.writeInt32(x);

    remote()->transact(PICTURE_SetDisplayWindow, data, &reply);
    return ;
}

void BpPictureManager::setDynamicContrastCurve(int32_t *normalCurve, int32_t *lightCurve, int32_t *darkCurve)
{
    ALOGV("Send PICTURE_SetDynamicContrastCurve\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());

    data.write(normalCurve, INPUT_CONTRAST_CURVE_LENGTH*sizeof(int32_t));
    data.write(lightCurve, INPUT_CONTRAST_CURVE_LENGTH*sizeof(int32_t));
    data.write(darkCurve, INPUT_CONTRAST_CURVE_LENGTH*sizeof(int32_t));

    remote()->transact(PICTURE_SetDynamicContrastCurve, data, &reply);
    return ;
}

void BpPictureManager::setFilm(int32_t enMsFile)
{
     ALOGV("Send PICTURE_SETFILM\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(enMsFile);
    remote()->transact(PICTURE_SETFILM, data, &reply);
    return ;
}

void BpPictureManager::setMfc(EN_MFC_MODE enMode)
{
     ALOGV("Send PICTURE_SETMFC\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(enMode);
    remote()->transact(PICTURE_SETMFC, data, &reply);
    return ;
}

EN_MFC_MODE BpPictureManager::getMfc()
{
    ALOGV("Send PICTURE_GETMFC\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_GETMFC, data, &reply);
    return (EN_MFC_MODE)reply.readInt32();
}

bool BpPictureManager::setMpegNoiseReduction(int32_t enMNRMode)
{
     ALOGV("Send PICTURE_SETMPEGNR\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(enMNRMode);
    remote()->transact(PICTURE_SETMPEGNR, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

bool BpPictureManager::setNoiseReduction(int32_t nr)
{
    ALOGV("Send PICTURE_SetNoiseReduction\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(nr);
    remote()->transact(PICTURE_SetNoiseReduction, data, &reply);
    return static_cast<bool>(reply.readInt32());
}


void BpPictureManager::setOutputPattern(bool bEnable, int16_t u16Red, int16_t u16Green, int16_t u16Blue)
{
    ALOGV("Send PICTURE_SetOutputPattern\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());

    data.writeInt32(bEnable);
    data.writeInt32(u16Red);
    data.writeInt32(u16Green);
    data.writeInt32(u16Blue);

    remote()->transact(PICTURE_SetOutputPattern, data, &reply);
    return ;
}

void BpPictureManager::setOverscan(int32_t bottom, int32_t top, int32_t right, int32_t left)
{
    ALOGV("Send PICTURE_SetOverScan\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());

    data.writeInt32(bottom);
    data.writeInt32(top);
    data.writeInt32(right);
    data.writeInt32(left);

    remote()->transact(PICTURE_SetOverScan, data, &reply);
    return ;
}

void BpPictureManager::setPictureModeBrightness(int16_t value)
{
   ALOGV("Send PICTURE_SetPictureModeBrightness1\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(value);
    remote()->transact(PICTURE_SetPictureModeBrightness1, data, &reply);
    return ;
}

void BpPictureManager::setPictureModeBrightness(int32_t setLocationType, int32_t value)
{
   ALOGV("Send PICTURE_SetPictureModeBrightness2\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(setLocationType);
    data.writeInt32(value);
    remote()->transact(PICTURE_SetPictureModeBrightness2, data, &reply);
    return ;
}

bool BpPictureManager::GetPictureModeBrightness(int16_t * const value)
{
   ALOGV("Send GetPictureModeBrightness\n");

    Parcel data, reply;
    int32_t ret;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_GetPictureModeBrightness, data, &reply);
    *value= reply.readInt32();
    ret = reply.readInt32();
    return (bool)ret;
}
void BpPictureManager::setPictureModeColor(int16_t value)
{
   ALOGV("Send PICTURE_setPictureModeColor\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(value);
    remote()->transact(PICTURE_setPictureModeColor, data, &reply);
    return ;
}

bool BpPictureManager::GetPictureModeSaturation(int16_t * const value)
{
   ALOGV("Send GetPictureModeSaturation\n");

    Parcel data, reply;
    int32_t ret;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_GetPictureModeSaturation, data, &reply);
    *value= reply.readInt32();
    ret = reply.readInt32();
    return (bool)ret;
}
void BpPictureManager::setPictureModeContrast(int16_t value)
{
   ALOGV("Send PICTURE_setPictureModeContrast\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(value);
    remote()->transact(PICTURE_setPictureModeContrast, data, &reply);
    return ;
}

bool BpPictureManager::GetPictureModeContrast(int16_t * const value)
{
   ALOGV("Send GetPictureModeContrast\n");

    Parcel data, reply;
    int32_t ret;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_GetPictureModeContrast, data, &reply);
    *value= reply.readInt32();
    ret = reply.readInt32();
    return (bool)ret;
}
void BpPictureManager::setPictureModeSharpness(int16_t value)
{
   ALOGV("Send PICTURE_setPictureModeSharpness\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(value);
    remote()->transact(PICTURE_setPictureModeSharpness, data, &reply);
    return ;
}

bool BpPictureManager::GetPictureModeSharpness(int16_t * const value)
{
   ALOGV("Send GetPictureModeSharpness\n");

    Parcel data, reply;
    int32_t ret;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_GetPictureModeSharpness, data, &reply);
    *value= reply.readInt32();
    ret = reply.readInt32();
    return (bool)ret;
}
void BpPictureManager::setPictureModeTint(int16_t value)
{
   ALOGV("Send PICTURE_setPictureModeTint\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(value);
    remote()->transact(PICTURE_setPictureModeTint, data, &reply);
    return ;
}

bool BpPictureManager::GetPictureModeHue(int16_t * const value)
{
   ALOGV("Send GetPictureModeHue\n");

    Parcel data, reply;
    int32_t ret;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_GetPictureModeHue, data, &reply);
    *value= reply.readInt32();
    ret = reply.readInt32();
    return (bool)ret;
}
void BpPictureManager::setPictureModeInputSource(int32_t inputSource)
{
   ALOGV("Send PICTURE_SetPictureModeInputSource\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(inputSource);
    remote()->transact(PICTURE_SetPictureModeInputSource, data, &reply);
    return ;
}

void BpPictureManager::setWindowInvisible()
{
    ALOGV("Send PICTURE_setWindowInvisible\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_setWindowInvisible, data, &reply);
    return ;
}

void BpPictureManager::setWindowVisible()
{
    ALOGV("Send PICTURE_setWindowVisible\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_setWindowVisible, data, &reply);
    return ;
}

bool BpPictureManager::unFreezeImage(void)
{
    ALOGV("Send PICTURE_UnFreezeImage\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_UnFreezeImage, data, &reply);
    return static_cast<bool>(reply.readInt32());
}



void BpPictureManager::setDebugMode(bool mode)
{
    ALOGV("Send PICTURE_SetDebugMode\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(mode);
    remote()->transact(PICTURE_SetDebugMode, data, &reply);
    return ;
}

bool BpPictureManager::disableOsdWindow(int32_t win)
{
    ALOGV("Send PICTURE_disableOsdWindow\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(win);
    remote()->transact(PICTURE_disableOsdWindow, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

bool BpPictureManager::disableAllOsdWindow()
{
    ALOGV("Send PICTURE_disableAllOsdWindow\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_disableAllOsdWindow, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

bool BpPictureManager::setOsdWindow(int32_t win, uint16_t u16StartX, uint16_t u16Width, uint16_t u16StartY, uint16_t u16Height)
{
    ALOGV("Send PICTURE_setOsdWindow\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(win);
    data.writeInt32(u16StartX);
    data.writeInt32(u16Width);
    data.writeInt32(u16StartY);
    data.writeInt32(u16Height);
    remote()->transact(PICTURE_setOsdWindow, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

void BpPictureManager::setColorRange(bool colorRange0_255)
{
    ALOGV("Send PICTURE_setColorRange\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(colorRange0_255);
    remote()->transact(PICTURE_setColorRange, data, &reply);
}

bool BpPictureManager::setHdmiPc(bool bEn)
{
    ALOGV("Send PICTURE_setHdmiPc\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(bEn);
    remote()->transact(PICTURE_sethdmipc, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

bool BpPictureManager::getHdmiPc()
{
    ALOGV("Send PICTURE_getHdmiPc\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_gethdmipc, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

bool BpPictureManager::autoHDMIColorRange()
{
    ALOGV("Send autoHDMIColorRange\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_autoHDMIColorRange, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

int32_t BpPictureManager::getCustomerPqRuleNumber()
{
    ALOGV("Send PICTURE_getCustomerPqRuleNumber\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_getCustomerPqRuleNumber, data, &reply);
    return reply.readInt32();
}

int32_t BpPictureManager::getStatusNumberByCustomerPqRule(int32_t ruleType)
{
    ALOGV("Send PICTURE_getStatusNumberByCustomerPqRule\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(ruleType);
    remote()->transact(PICTURE_getStatusNumberByCustomerPqRule, data, &reply);
    return reply.readInt32();
}

bool BpPictureManager::setStatusByCustomerPqRule(int32_t ruleType, int32_t ruleStatus)
{
    ALOGV("Send PICTURE_setStatusByCustomerPqRule\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(ruleType);
    data.writeInt32(ruleStatus);
    remote()->transact(PICTURE_setStatusByCustomerPqRule, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

bool BpPictureManager::moveWindow()
{
        ALOGV("Send PICTURE_moveWindow\n");
        Parcel data, reply;
        data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
        remote()->transact(PICTURE_moveWindow, data, &reply);
        return static_cast<bool>(reply.readInt32());
}

void BpPictureManager::enableBacklight()
{
        ALOGV("Send PICTURE_enableBacklight\n");
        Parcel data, reply;
        data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
        remote()->transact(PICTURE_enableBacklight, data, &reply);
}

void BpPictureManager::disableBacklight()
{
        ALOGV("Send PICTURE_disableBacklight\n");
        Parcel data, reply;
        data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
        remote()->transact(PICTURE_disableBacklight, data, &reply);
}

void BpPictureManager::generateTestPattern(TEST_PATTERN_MODE ePatternMode, uint32_t u32Length, void* para)
{
        ALOGV("Send PICTURE_setMVOPTestPattern\n");
        Parcel data, reply;
        data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
        data.writeInt32(ePatternMode);
        data.writeInt32(u32Length);
        for(uint32_t i = 0; i < ((u32Length+sizeof(uint32_t)-1)/sizeof(uint32_t)); i++)
        {
            data.writeInt32(((int32_t*)para)[i]);
        }
        remote()->transact(PICTURE_generateTestPattern, data, &reply);
}

void BpPictureManager::getDlcLumArray(int32_t *pArray, int32_t arrayLen)
{
        ALOGV("Send PICTURE_getDlcLumArray\n");
        Parcel data, reply;
        data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
        data.writeInt32(arrayLen);
        remote()->transact(PICTURE_getDlcLumArray, data, &reply);
        reply.read(pArray, arrayLen*sizeof(int32_t));
}

int32_t BpPictureManager::getDlcLumAverageTemporary()
{
        ALOGV("Send PICTURE_getDlcLumAverageTemporary\n");
        Parcel data, reply;
        data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
        remote()->transact(PICTURE_getDlcLumAverageTemporary, data, &reply);
        return reply.readInt32();
}

int32_t BpPictureManager::getDlcLumTotalCount()
{
        ALOGV("Send PICTURE_getDlcLumTotalCount\n");
        Parcel data, reply;
        data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
        remote()->transact(PICTURE_getDlcLumTotalCount, data, &reply);
        return reply.readInt32();
}

bool BpPictureManager::switchDlcCurve(int16_t dlcCurveIndex)
{
        ALOGV("Send PICTURE_switchDlcCurve\n");
        Parcel data, reply;
        data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
        data.writeInt32(dlcCurveIndex);
        remote()->transact(PICTURE_switchDlcCurve, data, &reply);
        return static_cast<bool>(reply.readInt32());
}

void BpPictureManager::getPixelRgb(int32_t eStage, int16_t x, int16_t y, int32_t eWindow, GET_RGB_DATA &rgbData)
{
        ALOGV("Send PICTURE_getPixelRgb\n");
        Parcel data, reply;
        data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
        data.writeInt32(eStage);
        data.writeInt32(x);
        data.writeInt32(y);
        data.writeInt32(eWindow);
        remote()->transact(PICTURE_getPixelRgb, data, &reply);
        rgbData.u32r = reply.readInt32();
        rgbData.u32g = reply.readInt32();
        rgbData.u32b = reply.readInt32();
}

bool BpPictureManager::getPixelInfo(Screen_Pixel_Info *pPixInfo)
{
    ALOGV("Send PICTURE_getPixelInfo\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(pPixInfo->u16XStart);
    data.writeInt32(pPixInfo->u16YStart);
    data.writeInt32(pPixInfo->u16XEnd);
    data.writeInt32(pPixInfo->u16YEnd);
    remote()->transact(PICTURE_getPixelInfo, data, &reply);

    pPixInfo->u32ReportPixelInfo_Version = reply.readInt32();
    pPixInfo->u16ReportPixelInfo_Length = reply.readInt32();
    pPixInfo->enStage = static_cast<Pixel_RGB_Stage>(reply.readInt32());
    pPixInfo->u16RepWinColor = reply.readInt32();
    pPixInfo->u16XStart = reply.readInt32();
    pPixInfo->u16XEnd = reply.readInt32();
    pPixInfo->u16YStart = reply.readInt32();
    pPixInfo->u16YEnd = reply.readInt32();
    pPixInfo->u16RCrMin = reply.readInt32();
    pPixInfo->u16RCrMax = reply.readInt32();
    pPixInfo->u16GYMin = reply.readInt32();
    pPixInfo->u16GYMax = reply.readInt32();
    pPixInfo->u16BCbMin = reply.readInt32();
    pPixInfo->u16BCbMax = reply.readInt32();
    pPixInfo->u32RCrSum = reply.readInt32();
    pPixInfo->u32GYSum = reply.readInt32();
    pPixInfo->u32BCbSum = reply.readInt32();
    pPixInfo->bShowRepWin = static_cast<bool>(reply.readInt32());

    return static_cast<bool>(reply.readInt32());
}

bool BpPictureManager::setSwingLevel(int16_t swingLevel)
{
    ALOGV("Send PICTURE_setSwingLevel\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(swingLevel);
    remote()->transact(PICTURE_setSwingLevel, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

int16_t BpPictureManager::getDlcHistogramMax()
{
    ALOGV("Send PICTURE_getDlcHistogramMax\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_getDlcHistogramMax, data, &reply);
    return static_cast<int16_t>(reply.readInt32());
}

int16_t BpPictureManager::getDlcHistogramMin()
{
    ALOGV("Send PICTURE_getDlcHistogramMin\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_getDlcHistogramMin, data, &reply);
    return static_cast<int16_t>(reply.readInt32());
}

bool BpPictureManager::forceFreerun(bool bEnable,bool b3D)
{
    ALOGV("Send PICTURE_forceFreerun\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(bEnable);
    data.writeInt32(b3D);
    remote()->transact(PICTURE_forceFreerun, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

void BpPictureManager::setResolution(int8_t resolution)
{
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(resolution);
    remote()->transact(SETRESOLUTION, data, &reply);
}

int8_t BpPictureManager::getResolution()
{
    ALOGV("Send GETRESOLUTION\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(GETRESOLUTION, data, &reply);
    return reply.readInt32();
}
void BpPictureManager::setReproduceRate(int32_t rate)
{
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(rate);
    remote()->transact(SETREPRODUCERATE, data, &reply);
}

int32_t BpPictureManager::getReproduceRate()
{
    ALOGV("Send GETREPRODUCERATE\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(GETREPRODUCERATE, data, &reply);
    return reply.readInt32();
}

bool BpPictureManager::setLocalDimmingMode(int16_t localDimingMode)
{
    ALOGV("Send PICTURE_setLocalDimmingMode\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(localDimingMode);
    remote()->transact(PICTURE_setLocalDimmingMode, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

bool BpPictureManager::setLocalDimmingBrightLevel(int16_t localDimingBrightLevel)
{
    ALOGV("Send PICTURE_setLocalDimmingBrightLevel\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(localDimingBrightLevel);
    remote()->transact(PICTURE_setLocalDimmingBrightLevel, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

bool BpPictureManager::turnOffLocalDimmingBacklight(bool bTurnOffLDBL)
{
    ALOGV("Send PICTURE_turnOffLocalDimmingBacklight\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(bTurnOffLDBL);
    remote()->transact(PICTURE_turnOffLocalDimmingBacklight, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

bool BpPictureManager::enter4K2KMode(bool bEnable)
{
    ALOGV("Send PICTURE_enter4K2KMode\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(bEnable);
    remote()->transact(PICTURE_enter4K2KMode, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

void BpPictureManager::lock4K2KMode(bool bLock)
{
    ALOGV("Send PICTURE_lock4K2KMode\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(bLock);
    remote()->transact(PICTURE_lock4K2KMode, data, &reply);
    return;
}
int32_t BpPictureManager::get4K2KMode()
{
    ALOGV("Send PICTURE_get4K2KMode\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_get4K2KMode, data, &reply);
    return static_cast<int32_t>(reply.readInt32());
}
bool BpPictureManager::set4K2KMode(int32_t enOutPutTimming,int32_t enUrsaMode)
{
    ALOGV("Send PICTURE_set4K2KMode\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(enOutPutTimming);
    data.writeInt32(enUrsaMode);
    remote()->transact(PICTURE_set4K2KMode, data, &reply);
    return static_cast<bool>(reply.readInt32());
}
bool BpPictureManager::is3DTVPlugedIn()
{
    ALOGV("Send PICTURE_is3DTVPlugedIn\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_is3DTVPlugedIn, data, &reply);
    return static_cast<bool>(reply.readInt32());
}
bool BpPictureManager::is4K2KMode(bool bEnable)
{
    ALOGV("Send PICTURE_is4K2KMode\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(bEnable);
    remote()->transact(PICTURE_is4K2KMode, data, &reply);
    return static_cast<bool>(reply.readInt32());
}
bool BpPictureManager::setUltraClear(bool bEnable)
{
    ALOGV("Send PICTURE_setUltraClear\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(bEnable);
    remote()->transact(PICTURE_setUltraClear, data, &reply);
    return static_cast<bool>(reply.readInt32());
}
bool BpPictureManager::disableAllDualWinMode()
{
    ALOGV("Send PICTURE_disableAllDualWinMode\n");

    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_disableAllDualWinMode, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

void BpPictureManager::EnableVideoOut(bool bEnable)
{
    ALOGV("Send PICTURE_EnableVideoOut\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(bEnable);
    remote()->transact(PICTURE_EnableVideoOut, data, &reply);
}

bool BpPictureManager::setHLinearScaling(bool bEnable, bool bSign, uint16_t u16Delta)
{
    ALOGV("Send PICTURE_SETHLINEARSCALING\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(bEnable);
    data.writeInt32(bSign);
    data.writeInt32(u16Delta);
    remote()->transact(PICTURE_SETHLINEARSCALING, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

bool BpPictureManager::setMEMCMode(String8 cmd)
{
    ALOGV("Send PICTURE_SETMEMCMODE\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeString8(cmd);
    remote()->transact(PICTURE_SETMEMCMODE, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

void BpPictureManager::setScalerGammaByIndex(int8_t u8Index)
{
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(u8Index);
    remote()->transact(PICTURE_SETSCALERGAMMABYINDEX, data, &reply);
}

bool BpPictureManager::enableXvyccCompensation(bool bEnable, int32_t eWin)
{
    ALOGV("Send PICTURE_ENABLEXVYCCCOMPENSATION\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(bEnable);
    data.writeInt32(eWin);
    remote()->transact(PICTURE_ENABLEXVYCCCOMPENSATION, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

bool BpPictureManager::keepScalerOutput4k2k(bool bEnable)
{
    ALOGV("Send PICTURE_keepScalerOutput4k2k\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(bEnable);
    remote()->transact(PICTURE_keepScalerOutput4k2k, data, &reply);
    return static_cast<bool>(reply.readInt32());
}
//------------------------------------------------------------------------------------
bool BpPictureManager::panelInitial(String8 anelIniName)
{
    ALOGV("Send PICTURE_panelInitial\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeString8(anelIniName);
    remote()->transact(PICTURE_panelInitial, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

bool BpPictureManager::setGammaParameter(int32_t index, int32_t value)
{
    ALOGV("Send PICTURE_setGammaParameter\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(index);
    data.writeInt32(value);
    remote()->transact(PICTURE_setGammaParameter, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

bool BpPictureManager::calGammaTable(GAMMA_TABLE *rgbData, int32_t MapMode)
{
    ALOGV("Send PICTURE_calGammaTable\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.write(rgbData, sizeof(GAMMA_TABLE));
    data.writeInt32(MapMode);
    remote()->transact(PICTURE_calGammaTable, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

bool BpPictureManager::setScalerGammaTable (GAMMA_TABLE *gammaTable)
{
    ALOGV("Send PICTURE_setScalerGammaTable\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.write(gammaTable, sizeof(GAMMA_TABLE));
    remote()->transact(PICTURE_setScalerGammaTable, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

uint8_t BpPictureManager::getScalerMotion()
{
    ALOGV("Send PICTURE_getScalerMotion\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_getScalerMotion, data, &reply);
    return static_cast<uint8_t>(reply.readInt32());
}

bool BpPictureManager::isSupportedZoom ()
{
    ALOGV("Send PICTURE_isSupportedZoom\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_isSupportedZoom, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

bool BpPictureManager::setxvYCCEnable(bool bEnable, int8_t u8xvYCCmode)
{
    ALOGV("Send PICTURE_isSupportedZoom\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(bEnable);
    data.writeInt32(u8xvYCCmode);
    remote()->transact(PICTURE_setxvYCCEnable, data, &reply);
    return static_cast<bool>(reply.readInt32());
}
int32_t BpPictureManager::getHDMIColorFormat()
{
    ALOGV("Send PICTURE_getHDMIColorFormat\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_getHDMIColorFormat, data, &reply);
    return reply.readInt32();
}

bool BpPictureManager::setOsdResolution(int32_t width, int32_t height)
{
    ALOGV("Send PICTURE_setOsdResolution\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(width);
    data.writeInt32(height);
    remote()->transact(PICTURE_setOsdResolution, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

bool BpPictureManager::lockOutputTiming(int32_t eOutputTiming)
{
    ALOGV("Send PICTURE_lockOutputTiming\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(eOutputTiming);
    remote()->transact(PICTURE_lockOutputTiming, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

bool BpPictureManager::unlockOutputTiming()
{
    ALOGV("Send PICTURE_unlockOutputTiming\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_unlockOutputTiming, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

int16_t BpPictureManager::GetSupportedTimingList(St_Timing_Info *pTimingInfoList, int16_t u16ListSize)
{
    ALOGV("Send PICTURE_GetSupportedTimingList\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    //buffer size
    data.writeInt32(u16ListSize);

    remote()->transact(PICTURE_GetSupportedTimingList, data, &reply);

    //real list count
    int16_t ret =static_cast<int16_t>(reply.readInt32());

    if(ret > 0)
    {
        for (int32_t i=0; i< ret; i++)
        {
            pTimingInfoList[i].u16HResolution = static_cast<uint16_t>(reply.readInt32());
            pTimingInfoList[i].u16VResolution = static_cast<uint16_t>(reply.readInt32());
            pTimingInfoList[i].u16FrameRate   = static_cast<uint16_t>(reply.readInt32());
            pTimingInfoList[i].bProgressiveMode = static_cast<bool>(reply.readInt32());
            pTimingInfoList[i].u16TimingID = static_cast<uint16_t>(reply.readInt32());
        }
    }

    return ret;
}

int16_t BpPictureManager::GetSupportedTimingListCount()
{
    ALOGV("Send GetSupportedTimingListCount\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());

    remote()->transact(PICTURE_GetSupportedTimingListCount, data, &reply);

    return static_cast<int16_t>(reply.readInt32());
}

uint16_t BpPictureManager::getCurrentTimingId()
{
    ALOGV("Send getCurrentTimingId\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());

    remote()->transact(PICTURE_GetCurrentTimingId, data, &reply);

    return static_cast<uint16_t>(reply.readInt32());
}

bool BpPictureManager::getHdrLevel(E_HDR_LEVEL *pLevel, int32_t eWin)
{
    ALOGV("Send getHdrLevel\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(eWin);

    remote()->transact(PICTURE_GetHDRLevel, data, &reply);

    int16_t ret =static_cast<int16_t>(reply.readInt32());
    (*pLevel) =static_cast<E_HDR_LEVEL>(reply.readInt32());

    return ret;
}

bool BpPictureManager::setHdrLevel(E_HDR_LEVEL enlevel, int32_t eWin)
{
    ALOGV("Send setHdrLevel\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(enlevel);
    data.writeInt32(eWin);

    remote()->transact(PICTURE_SetHDRLevel, data, &reply);

    return static_cast<bool>(reply.readInt32());
}

bool BpPictureManager::getAutoDetectHdrLevel(int32_t eWin)
{
    ALOGV("Send getAutoDetectHdrLevel\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(eWin);

    remote()->transact(PICTURE_GetAutoDetectHdrLevel, data, &reply);

    int16_t ret =static_cast<int16_t>(reply.readInt32());

    return ret;
}

bool BpPictureManager::setAutoDetectHdrLevel(bool bAuto, int32_t eWin)
{
    ALOGV("Send setAutoDetectHdrLevel\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(bAuto);
    data.writeInt32(eWin);

    remote()->transact(PICTURE_SetAutoDetectHdrLevel, data, &reply);

    int16_t ret =static_cast<int16_t>(reply.readInt32());

    return ret;
}

bool BpPictureManager::IsHdrEnable(int32_t eWin)
{
    ALOGV("Send IsHdrEnable\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    data.writeInt32(eWin);

    remote()->transact(PICTURE_IsHdrEnable, data, &reply);

    int16_t ret =static_cast<int16_t>(reply.readInt32());

    return ret;
}

uint16_t BpPictureManager::getPQDelayTime()
{
    ALOGV("Send getPQDelayTime\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());

    remote()->transact(PICTURE_GETPQDELAYTIME, data, &reply);

    return static_cast<uint16_t>(reply.readInt32());
}

void BpPictureManager::getColorTemperature(PQL_COLOR_TEMPEX_DATA &pstColorTemp)
{
    ALOGV("Send getPQDelayTime\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    
    remote()->transact(PICTURE_GETCOLORTEMP, data, &reply);

    pstColorTemp.u16RedGain = static_cast<uint16_t>(reply.readInt32());
    pstColorTemp.u16GreenGain = static_cast<uint16_t>(reply.readInt32());
    pstColorTemp.u16BlueGain = static_cast<uint16_t>(reply.readInt32());
    pstColorTemp.u16RedOffset = static_cast<uint16_t>(reply.readInt32());
    pstColorTemp.u16GreenOffset = static_cast<uint16_t>(reply.readInt32());
    pstColorTemp.u16BlueOffset = static_cast<uint16_t>(reply.readInt32());
}

 // EosTek Patch Begin
//ashton: for wb adjust
void BpPictureManager::asGetWbAdjustStar()
{
     ALOGV("Send PICTURE_asGetWbAdjustStar\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_asGetWbAdjustStar, data, &reply);
    return ;
}

void BpPictureManager::asGetWbAdjustExit()
{
     ALOGV("Send PICTURE_asGetWbAdjustExit\n");
    Parcel data, reply;
    data.writeInterfaceToken(IPictureManager::getInterfaceDescriptor());
    remote()->transact(PICTURE_asGetWbAdjustExit, data, &reply);
    return ;
}   
// EosTek Patch End

//------------------------------------------------------------------------------------

IMPLEMENT_META_INTERFACE(PictureManager, "mstar.IPictureManager");

status_t BnPictureManager::onTransact(uint32_t code,
                                const Parcel& data,
                                Parcel* reply,
                                uint32_t flags)
{
    switch(code)
    {
        case DISCONNECT:
        {
            ALOGV("Receive DISCONNECT\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            disconnect();
            return NO_ERROR;
        } break;
        //api msrv

        case PICTURE_DISABLEDLC:
        {
            ALOGV("Receive PICTURE_DISABLEDLC\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            disableDlc();
            return NO_ERROR;
        }break;

        case PICTURE_DISABLEOVERSCAN:
        {
            ALOGV("Receive PICTURE_DISABLEOVERSCAN\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            disableOverScan();
            return NO_ERROR;
        }break;

        case PICTURE_DISABLEWINDOW:
        {
            ALOGV("Receive PICTURE_DISABLEWINDOW\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            disableWindow();
            return NO_ERROR;
        }break;

        case PICTURE_ENABLEDLC:
        {
            ALOGV("Receive PICTURE_ENABLEDLC\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            enableDlc();
            return NO_ERROR;
        }break;

        case PICTURE_ENABLEOVERSCAN:
        {
            ALOGV("Receive PICTURE_ENABLEOVERSCAN\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            enableOverScan();
            return NO_ERROR;
        }break;

        case PICTURE_ENABLEWINDOW:
        {
            ALOGV("Receive PICTURE_ENABLEWINDOW\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            enableWindow();
            return NO_ERROR;
        }break;

        case PICTURE_FreezeImage:
        {
            ALOGV("Receive PICTURE_FreezeImage\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32(freezeImage());
            return NO_ERROR;
        }break;

        case PICTURE_GETBACKLIGHT:
        {
            ALOGV("Receive PICTURE_GETBACKLIGHT\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32(getBacklight());
            return NO_ERROR;
        }break;

        case PICTURE_GETBACKLIGHTMAXVALUE:
        {
            ALOGV("Receive PICTURE_GETBACKLIGHTMAXVALUE\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32(getBacklightMaxValue());
            return NO_ERROR;
        }break;

        case PICTURE_GETBACKLIGHTMINVALUE:
        {
            ALOGV("Receive PICTURE_GETBACKLIGHTMINVALUE\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32(getBacklightMinValue());
            return NO_ERROR;
        }break;

        case PICTURE_GetDemoMode:
        {
            ALOGV("Receive PICTURE_GetDemoMode\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32(getDemoMode());
            return NO_ERROR;
        }break;

        case PICTURE_GetDynamicContrastCurve:
        {
            ALOGV("Receive PICTURE_GetDynamicContrastCurve\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            int32_t data[CONTRAST_CURVE_LENGTH] = {0};
            bool result = getDynamicContrastCurve(data);
            reply->write(data, CONTRAST_CURVE_LENGTH*sizeof(int32_t));
            reply->writeInt32(result);
            return NO_ERROR;
        }break;

        case PICTURE_GetPanelWidthHeight:
        {
            ALOGV("Receive PICTURE_GetPanelWidthHeight\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            PanelProperty property;
            getPanelWidthHeight(property);
            reply->writeInt32(property.width);
            reply->writeInt32(property.height);
            return NO_ERROR;

        }break;

        case PICTURE_getDlcAverageLuma:
        {
            ALOGV("Receive PICTURE_getDlcAverageLuma\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32(getDlcAverageLuma());
            return NO_ERROR;
        }break;

         case PICTURE_IsImageFreezed:
         {
            ALOGV("Receive PICTURE_IsImageFreezed\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32(IsImageFreezed());
            return NO_ERROR;
         }break;

         case PICTURE_IsOverScanEnabled:
         {
            ALOGV("Receive PICTURE_IsOverScanEnabled\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32(isOverscanEnabled());
            return NO_ERROR;
         }break;

        case PICTURE_ScaleWindow:
        {
            ALOGV("Receive PICTURE_ScaleWindow\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32(scaleWindow());
            return NO_ERROR;
        }break;

         case PICTURE_SelectWindow:
         {
            ALOGV("Receive PICTURE_SelectWindow\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            int32_t windowId = data.readInt32();
            reply->writeInt32(selectWindow(windowId));
            return NO_ERROR;
         }break;

         case PICTURE_SETASPECTRATIO:
         {
             ALOGV("Receive PICTURE_SETASPECTRATIO\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            int32_t enAspectRatioTYpe = data.readInt32();
            setAspectRatio(enAspectRatioTYpe);
            return NO_ERROR;
         }break;

         case PICTURE_GETASPECTRATIO:
         {
             ALOGV("Receive PICTURE_GETASPECTRATIO\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            int32_t enAspectRatioTYpe = getAspectRatio();
            reply->writeInt32(enAspectRatioTYpe);
            return NO_ERROR;
         }break;

         case PICTURE_SetBacklight:
         {
             ALOGV("Receive PICTURE_SetBacklight\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            int32_t value = data.readInt32();
            setBacklight(value);
            return NO_ERROR;
         }break;

         case PICTURE_SetColorTemperature:
         {
             ALOGV("Receive PICTURE_SetColorTemperature\n");
            CHECK_INTERFACE(IPictureManager, data, reply);

            PQL_COLOR_TEMP_DATA pstColorTemp;


            pstColorTemp.u8RedGain =  data.readInt32();
            pstColorTemp.u8GreenGain =  data.readInt32();
            pstColorTemp.u8BlueGain =  data.readInt32();
            pstColorTemp.u8RedOffset =  data.readInt32();
            pstColorTemp.u8GreenOffset =  data.readInt32();
            pstColorTemp.u8BlueOffset =  data.readInt32();

            setColorTemperature(pstColorTemp);
            return NO_ERROR;
         }break;

         case PICTURE_SetColorTemperatureEX:
         {
             ALOGV("Receive PICTURE_SetColorTemperatureEX\n");
            CHECK_INTERFACE(IPictureManager, data, reply);

            PQL_COLOR_TEMPEX_DATA pstColorTemp;


            pstColorTemp.u16RedGain =  data.readInt32();
            pstColorTemp.u16GreenGain =  data.readInt32();
            pstColorTemp.u16BlueGain =  data.readInt32();
            pstColorTemp.u16RedOffset =  data.readInt32();
            pstColorTemp.u16GreenOffset =  data.readInt32();
            pstColorTemp.u16BlueOffset =  data.readInt32();

            setColorTemperatureEX(pstColorTemp);
            return NO_ERROR;
         }break;

         case PICTURE_SetCropWindow:
         {
              ALOGV("Receive PICTURE_SetCropWindow\n");
            CHECK_INTERFACE(IPictureManager, data, reply);

            int32_t h =  data.readInt32();
            int32_t w =  data.readInt32();
            int32_t x =  data.readInt32();
            int32_t y =  data.readInt32();

            setCropWindow(h,w,x,y);
            return NO_ERROR;
         }break;

         case PICTURE_SetDemoMode:
         {
              ALOGV("Receive PICTURE_SetDemoMode\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            int32_t enMsMweType = data.readInt32();
            setDemoMode(enMsMweType);
            return NO_ERROR;
         }break;

         case PICTURE_SetDisplayWindow:
         {
             ALOGV("Receive PICTURE_SetDisplayWindow\n");
            CHECK_INTERFACE(IPictureManager, data, reply);

            int32_t h =  data.readInt32();
            int32_t w =  data.readInt32();
            int32_t x =  data.readInt32();
            int32_t y =  data.readInt32();

            setDisplayWindow(h,w,x,y);
            return NO_ERROR;
         }break;

        case PICTURE_SetDynamicContrastCurve:
        {
            ALOGV("Receive PICTURE_SetDynamicContrastCurve\n");
            CHECK_INTERFACE(IPictureManager, data, reply);

            int32_t normalCurve[INPUT_CONTRAST_CURVE_LENGTH] = {0};
            data.read(normalCurve, INPUT_CONTRAST_CURVE_LENGTH*sizeof(int32_t));
            int32_t lightCurve[INPUT_CONTRAST_CURVE_LENGTH] = {0};
            data.read(lightCurve, INPUT_CONTRAST_CURVE_LENGTH*sizeof(int32_t));
            int32_t darkCurve[INPUT_CONTRAST_CURVE_LENGTH] = {0};
            data.read(darkCurve, INPUT_CONTRAST_CURVE_LENGTH*sizeof(int32_t));

            setDynamicContrastCurve(normalCurve , lightCurve , darkCurve);
            return NO_ERROR;
        }break;

         case PICTURE_SETFILM:
         {
              ALOGV("Receive PICTURE_SETFILM\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            int32_t enMsFile = data.readInt32();
            setFilm(enMsFile);
            return NO_ERROR;
         }break;

         case PICTURE_SETMFC:
         {
                ALOGV("Receive PICTURE_SETMFC\n");
                CHECK_INTERFACE(IPictureManager, data, reply);
                EN_MFC_MODE enMode = static_cast<EN_MFC_MODE>(data.readInt32());
                setMfc(enMode);
                return NO_ERROR;
         }break;

         case PICTURE_GETMFC:
         {
            ALOGV("Receive PICTURE_SETMFC\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            int32_t enMfc = getMfc();
            reply->writeInt32(enMfc);
            return NO_ERROR;
         }break;

         case PICTURE_SETMPEGNR:
         {
              ALOGV("Receive PICTURE_SETMPEGNR\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            int32_t enMNRMode = data.readInt32();
            reply->writeInt32(setMpegNoiseReduction(enMNRMode));
            return NO_ERROR;
         }break;

         case PICTURE_SetNoiseReduction:
         {
              ALOGV("Receive PICTURE_SetNoiseReduction\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            int32_t nr = data.readInt32();
            reply->writeInt32(setNoiseReduction(nr));
            return NO_ERROR;
         }break;

         case PICTURE_SetOutputPattern:
         {
             ALOGV("Receive PICTURE_SetOutputPattern\n");
            CHECK_INTERFACE(IPictureManager, data, reply);

            int32_t bEnable =  data.readInt32();
            int32_t u16Red =  data.readInt32();
            int32_t u16Green =  data.readInt32();
            int32_t u16Blue =  data.readInt32();

            setOutputPattern(bEnable,u16Red,u16Green,u16Blue);
            return NO_ERROR;
         }break;

          case PICTURE_SetOverScan:
          {
             ALOGV("Receive PICTURE_SetOverScan\n");
            CHECK_INTERFACE(IPictureManager, data, reply);

            int32_t bottom =  data.readInt32();
            int32_t top =  data.readInt32();
            int32_t right =  data.readInt32();
            int32_t left =  data.readInt32();

            setOverscan(bottom,top,right,left);
            return NO_ERROR;
          }break;

          case PICTURE_SetPictureModeBrightness1:
          {
             ALOGV("Receive PICTURE_SetPictureModeBrightness1\n");
            CHECK_INTERFACE(IPictureManager, data, reply);

            int32_t value =  data.readInt32();
            setPictureModeBrightness(value);
            return NO_ERROR;
          }break;

          case PICTURE_SetPictureModeBrightness2:
          {
             ALOGV("Receive PICTURE_SetPictureModeBrightness2\n");
            CHECK_INTERFACE(IPictureManager, data, reply);

            int32_t setLocationType =  data.readInt32();
            int32_t value =  data.readInt32();

            setPictureModeBrightness(setLocationType,value);
            return NO_ERROR;
          }break;

          case PICTURE_GetPictureModeBrightness:
          {
             ALOGV("Receive PICTURE_SetPictureModeBrightness2\n");
             CHECK_INTERFACE(IPictureManager, data, reply);
             int32_t ret = false;
             int16_t value;
             ret = GetPictureModeBrightness(&value);
             reply->writeInt32(value);
             reply->writeInt32(ret);
             return NO_ERROR;
          }break;
          case PICTURE_setPictureModeColor:
          {
             ALOGV("Receive PICTURE_setPictureModeColor\n");
             CHECK_INTERFACE(IPictureManager, data, reply);

             int16_t value = static_cast<int16_t>(data.readInt32());

             setPictureModeColor(value);
             return NO_ERROR;
          }break;

          case PICTURE_GetPictureModeSaturation:
          {
             ALOGV("Receive PICTURE_GetPictureModeSaturation\n");
             CHECK_INTERFACE(IPictureManager, data, reply);
             int32_t ret = false;
             int16_t value;

             ret = GetPictureModeSaturation(&value);
             reply->writeInt32(value);
             reply->writeInt32(ret);
             return NO_ERROR;
          }break;

          case PICTURE_setPictureModeContrast:
          {
             ALOGV("Receive PICTURE_setPictureModeContrast\n");
             CHECK_INTERFACE(IPictureManager, data, reply);

             int16_t value = static_cast<int16_t>(data.readInt32());

             setPictureModeContrast(value);
             return NO_ERROR;
          }break;

          case PICTURE_GetPictureModeContrast:
          {
             ALOGV("Receive PICTURE_GetPictureModeContrast\n");
             CHECK_INTERFACE(IPictureManager, data, reply);
             int32_t ret = false;
             int16_t value;

             ret = GetPictureModeContrast(&value);
             reply->writeInt32(value);
             reply->writeInt32(ret);
             return NO_ERROR;
          }break;

          case PICTURE_setPictureModeSharpness:
          {
             ALOGV("Receive PICTURE_setPictureModeSharpness\n");
             CHECK_INTERFACE(IPictureManager, data, reply);

             int16_t value = static_cast<int16_t>(data.readInt32());

             setPictureModeSharpness(value);
             return NO_ERROR;
          }break;

          case PICTURE_GetPictureModeSharpness:
          {
             ALOGV("Receive PICTURE_GetPictureModeSharpness\n");
             CHECK_INTERFACE(IPictureManager, data, reply);
             int32_t ret = false;
             int16_t value;

             ret = GetPictureModeSharpness(&value);
             reply->writeInt32(value);
             reply->writeInt32(ret);
             return NO_ERROR;
          }break;

          case PICTURE_setPictureModeTint:
          {
             ALOGV("Receive PICTURE_setPictureModeTint\n");
             CHECK_INTERFACE(IPictureManager, data, reply);
             int16_t value = static_cast<int16_t>(data.readInt32());
             setPictureModeTint(value);
             return NO_ERROR;
          }break;

          case PICTURE_GetPictureModeHue:
          {
             ALOGV("Receive PICTURE_GetPictureModeHue\n");
             CHECK_INTERFACE(IPictureManager, data, reply);
             int32_t ret = false;
             int16_t value;
             ret = GetPictureModeHue(&value);
             reply->writeInt32(value);
             reply->writeInt32(ret);
             return NO_ERROR;
          }break;

          case PICTURE_SetPictureModeInputSource:
          {
             ALOGV("Receive PICTURE_SetPictureModeInputSource\n");
            CHECK_INTERFACE(IPictureManager, data, reply);

            int32_t inputSource =  data.readInt32();
            setPictureModeInputSource(inputSource);
            return NO_ERROR;
          }break;

        case PICTURE_setWindowInvisible:
        {
            ALOGV("Receive PICTURE_setWindowInvisible\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            setWindowInvisible();
            return NO_ERROR;
        }break;

        case PICTURE_setWindowVisible:
        {
            ALOGV("Receive PICTURE_setWindowVisible\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            setWindowVisible();
            return NO_ERROR;
        }break;

        case PICTURE_UnFreezeImage:
        {
            ALOGV("Receive PICTURE_UnFreezeImage\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32(unFreezeImage());
            return NO_ERROR;
        }break;

        case PICTURE_SetDebugMode:
        {
            ALOGV("Receive PICTURE_SetDebugMode\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            bool mode =  data.readInt32();
            setDebugMode(mode);
            return NO_ERROR;
        }break;

        case PICTURE_disableOsdWindow:
        {
            ALOGV("Receive PICTURE_disableOsdWindow\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            int32_t win = data.readInt32();
            reply->writeInt32( disableOsdWindow(win) );
            return NO_ERROR;
        }break;

        case PICTURE_disableAllOsdWindow:
        {
            ALOGV("Receive PICTURE_disableAllOsdWindow\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32( disableAllOsdWindow() );
            return NO_ERROR;
        }break;

        case PICTURE_setOsdWindow:
        {
            ALOGV("Receive PICTURE_disableOsdWindow\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            int32_t win = data.readInt32();
            uint16_t u16StartX = static_cast<uint16_t>(data.readInt32());
            uint16_t u16Width = static_cast<uint16_t>(data.readInt32());
            uint16_t u16StartY = static_cast<uint16_t>(data.readInt32());
            uint16_t u16Height = static_cast<uint16_t>(data.readInt32());
            reply->writeInt32( setOsdWindow(win, u16StartX, u16Width, u16StartY, u16Height) );
            return NO_ERROR;
        }break;

        case PICTURE_setColorRange:
        {
            ALOGV("Receive PICTURE_setColorRange\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            bool colorRange0_255 = static_cast<bool>(data.readInt32());
            setColorRange(colorRange0_255);
            return NO_ERROR;
        }break;

        case PICTURE_sethdmipc:
        {
            ALOGV("Receive PICTURE_sethdmipc\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            bool bEn = static_cast<bool>(data.readInt32());
            reply->writeInt32(setHdmiPc(bEn));
            return NO_ERROR;
        }break;
        case PICTURE_gethdmipc:
        {
            ALOGV("Receive PICTURE_gethdmipc\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32(getHdmiPc());
            return NO_ERROR;
        }break;

        case PICTURE_autoHDMIColorRange:
        {
            ALOGV("Receive PICTURE_autoHDMIColorRange\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            autoHDMIColorRange();
            return NO_ERROR;
        }break;

        case PICTURE_getCustomerPqRuleNumber:
        {
            ALOGV("Receive PICTURE_getCustomerPqRuleNumber\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32(getCustomerPqRuleNumber());
            return NO_ERROR;
        }break;

        case PICTURE_getStatusNumberByCustomerPqRule:
        {
            ALOGV("Receive PICTURE_getStatusNumberByCustomerPqRule\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            int32_t ruleType = data.readInt32();
            reply->writeInt32(getStatusNumberByCustomerPqRule(ruleType));
            return NO_ERROR;
        }break;

        case PICTURE_setStatusByCustomerPqRule:
        {
            ALOGV("Receive PICTURE_setStatusByCustomerPqRule\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            int32_t ruleType = data.readInt32();
            int32_t ruleStatus = data.readInt32();
            reply->writeInt32(setStatusByCustomerPqRule(ruleType, ruleStatus));
            return NO_ERROR;
        }break;

        case PICTURE_moveWindow:
        {
            ALOGV("Receive PICTURE_moveWindow\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32( moveWindow() );
            return NO_ERROR;
        } break;

        case PICTURE_enableBacklight:
        {
            ALOGV("Receive PICTURE_enableBacklight\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            enableBacklight();
            return NO_ERROR;
        } break;

        case PICTURE_disableBacklight:
        {
            ALOGV("Receive PICTURE_disableBacklight\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            disableBacklight();
            return NO_ERROR;
        } break;

        case PICTURE_generateTestPattern:
        {
            ALOGV("Receive PICTURE_generateTestPattern\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            TEST_PATTERN_MODE ePatternMode = static_cast<TEST_PATTERN_MODE>(data.readInt32());
            int32_t u32Length = static_cast<uint32_t>(data.readInt32());
            int32_t *para = new int32_t[u32Length];
            if(para != NULL)
            {
                for(uint32_t i = 0; i < ((u32Length+sizeof(uint32_t)-1) / sizeof(uint32_t)); i++)
                {
                    para[i] = static_cast<int32_t>(data.readInt32());
                }
                generateTestPattern(ePatternMode, u32Length, para);
                delete []para;
                return NO_ERROR;
            }
            else
            {
                return NO_ERROR;
            }
        } break;
        
        case PICTURE_getDlcLumArray:
        {
            ALOGV("Receive PICTURE_getDlcLumArray\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            int32_t arrayLen = data.readInt32();
            int32_t *pArray = new int32_t[arrayLen];
            if(pArray != NULL)
            {
                getDlcLumArray(pArray, arrayLen);
                reply->write(pArray, arrayLen*sizeof(int32_t));
                delete []pArray;
                return NO_ERROR;
            }
            else
            {
                return NO_ERROR;
            }
        } break;

        case PICTURE_getDlcLumAverageTemporary:
        {
            ALOGV("Receive PICTURE_getDlcLumAverageTemporary\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32(getDlcLumAverageTemporary());
            return NO_ERROR;
        } break;

        case PICTURE_getDlcLumTotalCount:
        {
            ALOGV("Receive PICTURE_getDlcLumTotalCount\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32(getDlcLumTotalCount());
            return NO_ERROR;
        } break;

        case PICTURE_switchDlcCurve:
        {
            ALOGV("Receive PICTURE_switchDlcCurve\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            int16_t dlcCurveIndex = static_cast<int16_t>(data.readInt32());
            reply->writeInt32(switchDlcCurve(dlcCurveIndex));
            return NO_ERROR;
        } break;

        case PICTURE_getPixelRgb:
        {
            ALOGV("Receive PICTURE_getPixelRgb\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            int32_t eStage = data.readInt32();
            int16_t x = static_cast<int16_t>(data.readInt32());
            int16_t y = static_cast<int16_t>(data.readInt32());
            int32_t eWindow = data.readInt32();
            GET_RGB_DATA rgbData;
            getPixelRgb(eStage, x, y, eWindow, rgbData);
            reply->writeInt32(rgbData.u32r);
            reply->writeInt32(rgbData.u32g);
            reply->writeInt32(rgbData.u32b);
            return NO_ERROR;
        } break;

        case PICTURE_getPixelInfo:
        {
            ALOGV("Receive PICTURE_getPixelInfo\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            Screen_Pixel_Info PinxelInfo;
            PinxelInfo.u16XStart = static_cast<int16_t>(data.readInt32());
            PinxelInfo.u16YStart = static_cast<int16_t>(data.readInt32());
            PinxelInfo.u16XEnd = static_cast<int16_t>(data.readInt32());
            PinxelInfo.u16YEnd = static_cast<int16_t>(data.readInt32());

            bool ret = getPixelInfo(&PinxelInfo);
            reply->writeInt32(PinxelInfo.u32ReportPixelInfo_Version);
            reply->writeInt32(PinxelInfo.u16ReportPixelInfo_Length);
            reply->writeInt32(PinxelInfo.enStage);
            reply->writeInt32(PinxelInfo.u16RepWinColor);
            reply->writeInt32(PinxelInfo.u16XStart);
            reply->writeInt32(PinxelInfo.u16XEnd);
            reply->writeInt32(PinxelInfo.u16YStart);
            reply->writeInt32(PinxelInfo.u16YEnd);
            reply->writeInt32(PinxelInfo.u16RCrMin);
            reply->writeInt32(PinxelInfo.u16RCrMax);
            reply->writeInt32(PinxelInfo.u16GYMin);
            reply->writeInt32(PinxelInfo.u16GYMax);
            reply->writeInt32(PinxelInfo.u16BCbMin);
            reply->writeInt32(PinxelInfo.u16BCbMax);
            reply->writeInt32(PinxelInfo.u32RCrSum);
            reply->writeInt32(PinxelInfo.u32GYSum);
            reply->writeInt32(PinxelInfo.u32BCbSum);
            reply->writeInt32(PinxelInfo.bShowRepWin);

            reply->writeInt32(ret);
            return NO_ERROR;
        } break;

        case PICTURE_setSwingLevel:
        {
            ALOGV("Receive PICTURE_setSwingLevel\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            int16_t swingLevel = static_cast<int16_t>(data.readInt32());
            reply->writeInt32(setSwingLevel(swingLevel));
            return NO_ERROR;
        } break;

        case PICTURE_getDlcHistogramMax:
        {
            ALOGV("Receive PICTURE_getDlcHistogramMax\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32(getDlcHistogramMax());
            return NO_ERROR;
        } break;

        case PICTURE_getDlcHistogramMin:
        {
            ALOGV("Receive PICTURE_getDlcHistogramMin\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32(getDlcHistogramMin());
            return NO_ERROR;
        } break;

        case PICTURE_forceFreerun:
        {
            ALOGV("Receive PICTURE_forceFreerun\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            bool bEnable = static_cast<bool>(data.readInt32());
            bool b3D = static_cast<bool>(data.readInt32());
            reply->writeInt32(forceFreerun(bEnable, b3D));
            return NO_ERROR;
        } break;

        case PICTURE_setLocalDimmingMode:
        {
            ALOGV("Receive PICTURE_setLocalDimmingMode\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            int16_t localDimingMode = static_cast<int16_t>(data.readInt32());
            reply->writeInt32(setLocalDimmingMode(localDimingMode));
            return NO_ERROR;
        } break;

        case PICTURE_setLocalDimmingBrightLevel:
        {
            ALOGV("Receive PICTURE_setLocalDimmingBrightLevel\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            int16_t localDimingBrightLevel = static_cast<int16_t>(data.readInt32());
            reply->writeInt32(setLocalDimmingBrightLevel(localDimingBrightLevel));
            return NO_ERROR;
        } break;

        case PICTURE_turnOffLocalDimmingBacklight:
        {
            ALOGV("Receive PICTURE_turnOffLocalDimmingBacklight\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            bool bTurnOffLDBL = static_cast<bool>(data.readInt32());
            reply->writeInt32(turnOffLocalDimmingBacklight(bTurnOffLDBL));
            return NO_ERROR;
        } break;

        case PICTURE_enter4K2KMode:
        {
            ALOGV("Receive PICTURE_enter4K2KMode\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            bool bEnable = static_cast<bool>(data.readInt32());
            reply->writeInt32(enter4K2KMode(bEnable));
            return NO_ERROR;
        } break;

        case PICTURE_lock4K2KMode:
        {
            ALOGV("Receive PICTURE_lock4K2KModes\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            bool bEnable = static_cast<bool>(data.readInt32());
            lock4K2KMode(bEnable);
            return NO_ERROR;
        } break;
        case PICTURE_get4K2KMode:
        {
            ALOGV("Receive PICTURE_get4K2KMode\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32(get4K2KMode());
            return NO_ERROR;
        } break;
        case PICTURE_set4K2KMode:
        {
            ALOGV("Receive PICTURE_set4K2KMode\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            int32_t enOutPutTimming = static_cast<int32_t>(data.readInt32());
            int32_t enUrsaMode = static_cast<int32_t>(data.readInt32());
            reply->writeInt32(set4K2KMode(enOutPutTimming,enUrsaMode));
            return NO_ERROR;
        } break;
        case PICTURE_is4K2KMode:
        {
            ALOGV("Receive PICTURE_is4K2KMode\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            bool bEnable = static_cast<bool>(data.readInt32());
            reply->writeInt32(is4K2KMode(bEnable));
            return NO_ERROR;
        } break;
        case PICTURE_is3DTVPlugedIn:
        {
            ALOGV("Receive PICTURE_is3DTVPlugedIn\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32(is3DTVPlugedIn());
            return NO_ERROR;
        } break;

        case PICTURE_setUltraClear:
        {
            ALOGV("Receive PICTURE_setUltraClear\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            bool bEnable = static_cast<bool>(data.readInt32());
            reply->writeInt32(setUltraClear(bEnable));
            return NO_ERROR;
        } break;

        case SETRESOLUTION:
        {
            CHECK_INTERFACE(IPictureManager, data, reply);
            int32_t resolution = data.readInt32();
            setResolution(resolution);
            return NO_ERROR;
        } break;
        case GETRESOLUTION:
        {
            ALOGV("Receive GETRESOLUTION\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32(getResolution());
            return NO_ERROR;
        } break;
        case SETREPRODUCERATE:
        {
            CHECK_INTERFACE(IPictureManager, data, reply);
            int32_t rate = data.readInt32();
            setReproduceRate(rate);
            return NO_ERROR;
        } break;
        case GETREPRODUCERATE:
        {
            ALOGV("Receive GETRESOLUTION\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32(getReproduceRate());
            return NO_ERROR;
        } break;
        case PICTURE_disableAllDualWinMode:
        {
            ALOGV("Receive PICTURE_disableAllDualWinMode\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            disableAllDualWinMode();
            return NO_ERROR;
        }break;
        case PICTURE_EnableVideoOut:
        {
            ALOGV("Receive PICTURE_EnableVideoOut\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            bool bEnable = static_cast<bool>(data.readInt32());
            EnableVideoOut(bEnable);
            return NO_ERROR;
        } break;
        case PICTURE_SETHLINEARSCALING:
        {
            ALOGV("Receive PICTURE_SETHLINEARSCALING\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            bool bEnable = static_cast<bool>(data.readInt32());
            bool bSign = static_cast<bool>(data.readInt32());
            uint16_t u16Delta = static_cast<uint16_t>(data.readInt32());
            reply->writeInt32(setHLinearScaling(bEnable, bSign,u16Delta));
            return NO_ERROR;
        } break;
        case PICTURE_SETMEMCMODE:
        {
            ALOGV("Receive PICTURE_SETMEMCMODE\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            String8 str = data.readString8();
            reply->writeInt32( setMEMCMode(str) );
            return NO_ERROR;
        } break;
        case PICTURE_SETSCALERGAMMABYINDEX:
        {
            CHECK_INTERFACE(IPictureManager, data, reply);
            int32_t u8Index = data.readInt32();
            setScalerGammaByIndex(u8Index);
            return NO_ERROR;
        } break;
        case PICTURE_ENABLEXVYCCCOMPENSATION:
        {
            ALOGV("Receive PICTURE_ENABLEXVYCCCOMPENSATION\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            bool bEnable = static_cast<bool>(data.readInt32());
            int32_t eWin = data.readInt32();
            enableXvyccCompensation(bEnable, eWin);
            return NO_ERROR;
        } break;
        case PICTURE_keepScalerOutput4k2k:
        {
            ALOGV("Receive PICTURE_keepScalerOutput4k2k\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            bool bEnable = static_cast<bool>(data.readInt32());
            reply->writeInt32(keepScalerOutput4k2k(bEnable));
            return NO_ERROR;
        } break;
//------------------------------------------------------------------------------------
        case PICTURE_panelInitial:
        {
            ALOGV("Receive PICTURE_panelInitial\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            String8 str = data.readString8();
            reply->writeInt32(panelInitial(str));
            return NO_ERROR;
        } break;
        case PICTURE_setGammaParameter:
        {
            ALOGV("Receive PICTURE_setGammaParameter\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            int32_t index = data.readInt32();
            int32_t value = data.readInt32();
            reply->writeInt32(setGammaParameter(index, value));
            return NO_ERROR;
        } break;
        case PICTURE_calGammaTable:
        {
            ALOGV("Receive PICTURE_calGammaTable\n");
            CHECK_INTERFACE(IPictureManager, data, reply);

            GAMMA_TABLE rgbData;
            data.read(&rgbData, sizeof(GAMMA_TABLE));
            int32_t MapMode = data.readInt32();
            reply->writeInt32(calGammaTable(&rgbData, MapMode));
            return NO_ERROR;
        } break;
        case PICTURE_setScalerGammaTable:
        {
            ALOGV("Receive PICTURE_setScalerGammaTable\n");
            CHECK_INTERFACE(IPictureManager, data, reply);

            GAMMA_TABLE gammaTable;
            data.read(&gammaTable, sizeof(GAMMA_TABLE));
            reply->writeInt32(setScalerGammaTable (&gammaTable));
            return NO_ERROR;
        } break;
        case PICTURE_getScalerMotion:
        {
            ALOGV("Receive PICTURE_getScalerMotion\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32(getScalerMotion());
            return NO_ERROR;
        } break;
        case PICTURE_isSupportedZoom:
        {
            ALOGV("Receive PICTURE_isSupportedZoom\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32(isSupportedZoom());
            return NO_ERROR;
        } break;
        case PICTURE_setxvYCCEnable:
        {
            ALOGV("Receive PICTURE_setxvYCCEnable\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            bool bEnable = static_cast<bool>(data.readInt32());
            int32_t u8xvYCCmode = data.readInt32();
            reply->writeInt32(setxvYCCEnable(bEnable, u8xvYCCmode));
            return NO_ERROR;
        } break;
        case PICTURE_getHDMIColorFormat:
        {
            ALOGV("Receive PICTURE_getHDMIColorFormat\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32(getHDMIColorFormat());
            return NO_ERROR;

        } break;

        case PICTURE_setOsdResolution:
        {
            ALOGV("Receive PICTURE_setOsdResolution\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            int32_t width = data.readInt32();
            int32_t height = data.readInt32();
            reply->writeInt32(setOsdResolution(width, height));
            return NO_ERROR;

        } break;
        case PICTURE_lockOutputTiming:
        {
            ALOGV("Receive PICTURE_lockOutputTiming\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            int32_t eOutputTiming = data.readInt32();
            reply->writeInt32(lockOutputTiming(eOutputTiming));
            return NO_ERROR;

        } break;
        case PICTURE_unlockOutputTiming:
        {
            ALOGV("Receive PICTURE_unlockOutputTiming\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32(unlockOutputTiming());
            return NO_ERROR;

        } break;
        case PICTURE_GetSupportedTimingList:
        {
            ALOGV("Receive PICTURE_GetSupportedTimingList\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            //buffer siez
            int16_t ListSize = static_cast<uint8_t>(data.readInt32());
            int16_t ret = 0;

            if(ListSize > 0)
            {
                St_Timing_Info *pTimingInfoList = new St_Timing_Info[ListSize];
                ret = GetSupportedTimingList(pTimingInfoList,ListSize);

                //list element count
                reply->writeInt32(ret);
                if(ret > 0)
                {
                    for (int32_t i=0 ;i<ret; i++)
                    {
                        reply->writeInt32(pTimingInfoList[i].u16HResolution);
                        reply->writeInt32(pTimingInfoList[i].u16VResolution);
                        reply->writeInt32(pTimingInfoList[i].u16FrameRate);
                        reply->writeInt32(pTimingInfoList[i].bProgressiveMode);
                        reply->writeInt32(pTimingInfoList[i].u16TimingID);
                    }
                }

                delete [] pTimingInfoList;
            }
            else
            {
                ret = GetSupportedTimingList(NULL,ListSize);
                //list element count
                reply->writeInt32(ret);
            }


            return NO_ERROR;

        } break;

        case PICTURE_GetSupportedTimingListCount:
        {
            ALOGV("Receive PICTURE_GetSupportedTimingListCount\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32(GetSupportedTimingListCount());
            return NO_ERROR;

        } break;

        case PICTURE_GetCurrentTimingId:
        {
            ALOGV("Receive PICTURE_GetCurrentTimingId\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32(getCurrentTimingId());
            return NO_ERROR;

        } break;

        case PICTURE_GetHDRLevel:
        {
            ALOGV("Receive PICTURE_GetHDRLevel\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            E_HDR_LEVEL hdrLevel;
            int32_t eWin = data.readInt32();
            int32_t ret = getHdrLevel(&hdrLevel, eWin);
            reply->writeInt32(ret);
            reply->writeInt32(hdrLevel);
            return NO_ERROR;

        } break;

        case PICTURE_SetHDRLevel:
        {
            ALOGV("Receive PICTURE_SetHDRLevel\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            int32_t hdrLevel = data.readInt32();
            int32_t eWin = data.readInt32();
            reply->writeInt32(setHdrLevel((E_HDR_LEVEL)hdrLevel, eWin));
            return NO_ERROR;

        } break;

        case PICTURE_GetAutoDetectHdrLevel:
        {
            ALOGV("Receive PICTURE_GetAutoDetectHdrLevel\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            int32_t eWin = data.readInt32();
            reply->writeInt32(getAutoDetectHdrLevel(eWin));
            return NO_ERROR;

        } break;

        case PICTURE_SetAutoDetectHdrLevel:
        {
            ALOGV("Receive PICTURE_SetAutoDetectHdrLevel\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            bool bAuto = (bool)data.readInt32();
            int32_t eWin = data.readInt32();
            reply->writeInt32(setAutoDetectHdrLevel(bAuto, eWin));
            return NO_ERROR;

        } break;

        case PICTURE_IsHdrEnable:
        {
            ALOGV("Receive PICTURE_IsHdrEnable\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            int32_t eWin = data.readInt32();
            reply->writeInt32(IsHdrEnable(eWin));
            return NO_ERROR;

        } break;

        case PICTURE_GETPQDELAYTIME:
        {
            ALOGV("Receive PICTURE_GETPQDELAYTIME\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            reply->writeInt32(getPQDelayTime());
            return NO_ERROR;

        } break;

        case PICTURE_GETCOLORTEMP:
        {
            ALOGV("Receive PICTURE_GETCOLORTEMP\n");
            CHECK_INTERFACE(IPictureManager, data, reply);
            PQL_COLOR_TEMPEX_DATA stColorTemp;
            getColorTemperature(stColorTemp);

            reply->writeInt32(stColorTemp.u16RedGain);
            reply->writeInt32(stColorTemp.u16GreenGain);
            reply->writeInt32(stColorTemp.u16BlueGain);
            reply->writeInt32(stColorTemp.u16RedOffset);
            reply->writeInt32(stColorTemp.u16GreenOffset);
            reply->writeInt32(stColorTemp.u16BlueOffset);
        }
		
		 // EosTek Patch Begin
        //ashton: for wb adjust
        case PICTURE_asGetWbAdjustStar:
           {
               ALOGV("Receive PICTURE_asGetWbAdjustStar\n");
               CHECK_INTERFACE(IPictureManager, data, reply);
               asGetWbAdjustStar();
               return NO_ERROR;
        
           } break;   

          case PICTURE_asGetWbAdjustExit:
           {
               ALOGV("Receive PICTURE_asGetWbAdjustExit\n");
               CHECK_INTERFACE(IPictureManager, data, reply);
               asGetWbAdjustExit();
               return NO_ERROR;
        
           } break;   
        // EosTek Patch End

		
//------------------------------------------------------------------------------------

        default:
            ALOGV("Receive unknown code(%08x)\n", code);
        return BBinder::onTransact(code, data, reply, flags);
    }
}

