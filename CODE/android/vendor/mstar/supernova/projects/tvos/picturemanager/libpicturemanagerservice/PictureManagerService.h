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
#ifndef _PictureManager_SERVICE_H_
#define _PictureManager_SERVICE_H_

#include <utils/threads.h>
#include <utils/SortedVector.h>
#include "IPictureManagerService.h"
#include "IPictureManager.h"
#include "TVOS_Common.h"
#include "mapi_syscfg_table.h"
#include "MsTypes.h"

using namespace android;

class PictureManagerService : public BnPictureManagerService , public TVOS_Service
{
    class Client;

public:
    static PictureManagerService* instantiate();

    // IPictureManagerService interface
    virtual sp<IPictureManager> connect(const sp<IPictureManagerClient>& PictureManagerClient);

    void removeClient(wp<Client> client);

    bool PostEvent(uint32_t nEvt, uint32_t wParam, uint32_t lParam, bool synchronous = false);

    //Post a event to client, Internal use only!!!
    //Only deal with things for PostEvent when synchronous=true
    bool PostEventToClient(U32 nEvt, U32 wParam, U32 lParam);

private:

// ----------------------------------------------------------------------------

    class Client : public BnPictureManager, public TVOS_Utility
    {
    public:
        virtual void disconnect();
    //no use in UI
    virtual void disableDlc(void);
    //MainMenuFrame.cpp
    virtual void disableOverScan(void);
    virtual void disableWindow(void);
    //no use in UI,new one
    virtual void enableDlc(void);
    //MainMenuFrame.cpp
    virtual void enableOverScan(void);
    virtual void enableWindow(void);
    //HotkeyControlFrame.cpp
    virtual bool freezeImage(void);
    virtual int32_t getBacklight();
    virtual int32_t getBacklightMaxValue();
    virtual int32_t getBacklightMinValue();

    //no use in UI ,just define
    virtual int32_t getDemoMode(void);

    virtual bool getDynamicContrastCurve(int32_t* outCurve);

    //DMPFileSelectorFrame.cpp
    virtual void getPanelWidthHeight(PanelProperty &property);

    virtual int16_t getDlcAverageLuma();

    //no use in UI,new one, now defined in msrv
    virtual bool IsImageFreezed(void);
    //no use in UI,new one , only in msrv
    virtual bool isOverscanEnabled(void);

    virtual bool scaleWindow();

     //no use  ,just define
    virtual bool selectWindow(int32_t windowId);
     //MainMenuFrame.cpp
    virtual void setAspectRatio(int32_t enAspectRatioTYpe);

    virtual int32_t getAspectRatio();

    virtual void setBacklight(int32_t value);
      //MainMenuFrame.cpp
    virtual void setColorTemperature(PQL_COLOR_TEMP_DATA &pstColorTemp);
    virtual void getColorTemperature(PQL_COLOR_TEMPEX_DATA &pstColorTemp);
    virtual void setColorTemperatureEX(PQL_COLOR_TEMPEX_DATA &pstColorTemp);

     //no use in UI ,just define
    virtual void setCropWindow(int32_t h, int32_t w, int32_t y, int32_t x);
     //no use in UI ,just define
    virtual void setDemoMode(int32_t enMsMweType);
     //no use in UI ,just define
    virtual void setDisplayWindow(int32_t h, int32_t w, int32_t y, int32_t x);

     virtual void setDynamicContrastCurve(int32_t *normalCurve, int32_t *lightCurve, int32_t *darkCurve);

   //MainMenuFrame.cpp
    virtual void setFilm(int32_t enMsFile);
    virtual void setMfc(EN_MFC_MODE enMode);
    virtual EN_MFC_MODE getMfc();
   //no use in UI ,just define
    virtual bool setMpegNoiseReduction(int32_t enMNRMode);
   //no use in UI ,just define
    virtual bool setNoiseReduction(int32_t nr);


    //NewFactoryModeAppFrame.cpp
    virtual void setOutputPattern(bool bEnable, int16_t u16Red, int16_t u16Green, int16_t u16Blue);
    //no use , new one, no use in ui
    virtual void setOverscan (int32_t bottom, int32_t top, int32_t right, int32_t left);

    //FactoryModeFrame.cpp
    virtual void setPictureModeBrightness(int16_t value);
    //FactoryModeFrame.cpp
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

    //FactoryModeFrame.cpp
    virtual void setPictureModeInputSource(int32_t inputSource);
    virtual void setWindowInvisible();
    virtual void setWindowVisible();
    virtual void setDebugMode(bool mode);

    virtual bool disableOsdWindow(int32_t win);
    virtual bool disableAllOsdWindow();
    virtual bool setOsdWindow(int32_t win, uint16_t u16StartX, uint16_t u16Width, uint16_t u16StartY, uint16_t u16Height);
    virtual void setColorRange(bool colorRange0_255);

    virtual int32_t getCustomerPqRuleNumber();
    virtual int32_t getStatusNumberByCustomerPqRule(int32_t ruleType);
    virtual bool setStatusByCustomerPqRule(int32_t ruleType, int32_t ruleStatus);

    // no use ,new one
    bool unFreezeImage(void);

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
    virtual void setResolution(int8_t resolution);
    virtual int8_t getResolution();
    virtual void setReproduceRate(int32_t rate);
    virtual int32_t getReproduceRate();

    virtual bool forceFreerun(bool bEnable,bool b3D);

    virtual bool setLocalDimmingMode(int16_t localDimingMode);
    virtual bool setLocalDimmingBrightLevel(int16_t localDimingBrightLevel);
    virtual bool turnOffLocalDimmingBacklight(bool bTurnOffLDBL);
    virtual bool enter4K2KMode(bool bEnable);
    virtual void lock4K2KMode(bool bLock );
    virtual int32_t get4K2KMode();
    virtual bool set4K2KMode(int32_t enOutPutTimming,int32_t enUrsaMode);
    virtual bool is4K2KMode(bool bEnable);
    virtual bool is3DTVPlugedIn();
    virtual bool setUltraClear(bool bEnable);
    virtual bool autoHDMIColorRange();
    virtual bool disableAllDualWinMode();
    virtual void EnableVideoOut(bool bEnable);
    virtual bool setHLinearScaling(bool bEnable, bool bSign, uint16_t u16Delta);
    virtual bool setMEMCMode(String8 cmd);
    virtual void setScalerGammaByIndex(int8_t u8Index);
    //no use, enableXvyccCompensation will be replaced by setxvYCCEnable
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
    // EosTek Patch Begin
    //ashton: for wb adjust
    virtual void asGetWbAdjustStar();  
    virtual void asGetWbAdjustExit();  	
    // EosTek Patch End


        const sp<IPictureManagerClient>& getPictureManagerClient() const
        {
            return m_PictureManagerClient;
        };

    private:
        friend class PictureManagerService;
        Client(const sp<PictureManagerService>& PictureManagerService,
               const sp<IPictureManagerClient>& PictureManagerClient,
               pid_t clientPid);
        ~Client();

        mutable Mutex m_Lock;
        sp<PictureManagerService> m_PictureManagerService;
        sp<IPictureManagerClient> m_PictureManagerClient;
        pid_t m_ClientPid;
        int32_t m_ConnId;
        bool m_bEnableDebug ;
    };

// ----------------------------------------------------------------------------

    PictureManagerService();
    ~PictureManagerService();

//    mutable Mutex m_Lock;
    //wp<Client> m_Client;
    SortedVector< wp<Client> > m_Clients;
    SortedVector< wp<Client> > m_vWaitingRemoveClients;

    volatile int32_t m_Users;
    virtual int32_t incUsers();
    virtual void decUsers();

};

#endif // _PictureManager_SERVICE_H_
