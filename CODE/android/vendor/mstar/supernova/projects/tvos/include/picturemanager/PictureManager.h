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
/*
    Rule:
    1. directory: lowcase
       filename: 1st letter is uppercase
    2. parameter/return type:
       use basic types, including "void, bool, int8_t, int16_t, int32_t"
       enum -> int32_t
       struct/return:  define xxxType.h and put into tvos\include\xxx folder
       normally no class as parameter & return type
       if paramter is output, must add note.
    3. add phase1/2/3 priority
       Level 1: switch input source, auto/manual scan, atv play, dvbc play,
                channel change, program list, volume adjust, video mute,
                audio mute, power off
       Level 2: picture quality, sound effect, Component/AV/SV/PC/HDMI player/factory
       Level 3: others, API is not confirmed
*/

#ifndef _PICTUREMANAGER_H_
#define _PICTUREMANAGER_H_

#include <utils/threads.h>
#include "IPictureManagerClient.h"
#include "IPictureManagerService.h"
#include "PictureManagerTypeDefine.h"
using namespace android;

typedef struct VideoWindowType
{
    int height;
    int width;
    int x;
    int y;
}VideoWindowType;

// ref-counted object for callbacks
class PictureManagerListener : virtual public RefBase
{
public:
    virtual void notify(int32_t msgType, int32_t ext1, int32_t ext2) = 0;
    virtual void PostEvent_Template(int32_t nEvt, int32_t ext1, int32_t ext2) = 0;
    virtual void PostEvent_SnServiceDeadth(int32_t nEvt, int32_t ext1, int32_t ext2) = 0;
    virtual void PostEvent_SetAspectratio(int32_t ext1, int32_t ext2) = 0;
    virtual void PostEvent_4K2KPhotoDisablePip(int32_t ext1, int32_t ext2) = 0;
    virtual void PostEvent_4K2KPhotoDisablePop(int32_t ext1, int32_t ext2) = 0;
    virtual void PostEvent_4K2KPhotoDisableDualview(int32_t ext1, int32_t ext2) = 0;
    virtual void PostEvent_4K2KPhotoDisableTravelingmode(int32_t ext1, int32_t ext2) = 0;

};

class PictureManager : public BnPictureManagerClient, public IBinder::DeathRecipient
{
public:


    PictureManager();
    ~PictureManager();

    static sp<PictureManager> connect();
    void disconnect();

    status_t setListener(const sp<PictureManagerListener>& listener);

    // IPictureClient interface
     void notify(int32_t msgType, int32_t ext, int32_t ext2);

     void PostEvent_Template(int32_t nEvt, int32_t ext1, int32_t ext2);
     void PostEvent_SnServiceDeadth(int32_t nEvt, int32_t ext1, int32_t ext2);
    void PostEvent_SetAspectratio(int32_t ext1, int32_t ext2);
    void PostEvent_4K2KPhotoDisablePip(int32_t ext1, int32_t ext2);
    void PostEvent_4K2KPhotoDisablePop(int32_t ext1, int32_t ext2);
    void PostEvent_4K2KPhotoDisableDualview(int32_t ext1, int32_t ext2);
    void PostEvent_4K2KPhotoDisableTravelingmode(int32_t ext1, int32_t ext2);

    //api add
    void disableDlc(void);
    void disableOverScan(void);
    void disableWindow(void);
    void enableDlc(void);
    void enableOverScan(void);
    void enableWindow(void);
    bool freezeImage(void);
    int32_t getBacklight();
    int32_t getBacklightMaxValue();
    int32_t getBacklightMinValue();
    int32_t getDemoMode(void);
    bool getDynamicContrastCurve(int32_t* outCurve);
    void getPanelWidthHeight(PanelProperty &property);
    int16_t getDlcAverageLuma();

    bool IsImageFreezed(void);
    bool isOverscanEnabled(void);

    bool scaleWindow();
    bool selectWindow(int32_t windowId);
    void setAspectRatio(int32_t enAspectRatioTYpe);
    int32_t getAspectRatio();
    void setBacklight(int32_t value);
    void setColorTemperature(PQL_COLOR_TEMP_DATA &pstColorTemp);
    void setColorTemperatureEX(PQL_COLOR_TEMPEX_DATA &pstColorTemp);
    void setCropWindow(VideoWindowType videoWindowType);
    void setDemoMode(int32_t enMsMweType);
    void setDisplayWindow(VideoWindowType videoWindowType);
    void setDynamicContrastCurve(int32_t *normalCurve, int32_t *lightCurve, int32_t *darkCurve);

    void setFilm(int32_t enMsFile);
    void setMfc(EN_MFC_MODE enMode);
    EN_MFC_MODE getMfc();
    bool setMpegNoiseReduction(int32_t enMNRMode);
    bool setNoiseReduction(int32_t nr);
    void setOutputPattern(bool bEnable, int16_t u16Red, int16_t u16Green, int16_t u16Blue);

    void setOverscan (int32_t bottom, int32_t top, int32_t right, int32_t left);

    void setPictureModeBrightness(int16_t value);
    void setPictureModeBrightness(int32_t setLocationType, int32_t value);
    bool GetPictureModeBrightness(int16_t * const value);
    void setPictureModeColor(int16_t value);
    bool GetPictureModeSaturation(int16_t * const value);
    void setPictureModeContrast(int16_t value);
    bool GetPictureModeContrast(int16_t * const value);
    void setPictureModeSharpness(int16_t value);
    bool GetPictureModeSharpness(int16_t * const value);
    void setPictureModeTint(int16_t value);
    bool GetPictureModeHue(int16_t * const value);
    void setPictureModeInputSource(int32_t inputSource);
    void setWindowInvisible();
    void setWindowVisible();

    bool unFreezeImage(void);

    void setDebugMode(bool mode);

    bool disableOsdWindow(int32_t win);
    bool disableAllOsdWindow();
    bool setOsdWindow(int32_t win, uint16_t u16StartX, uint16_t u16Width, uint16_t u16StartY, uint16_t u16Height);
    void setColorRange(bool colorRange0_255);

    int32_t getCustomerPqRuleNumber();
    int32_t getStatusNumberByCustomerPqRule(int32_t ruleType);
    bool setStatusByCustomerPqRule(int32_t ruleType, int32_t ruleStatus);
    bool moveWindow();

    void enableBacklight();
    void disableBacklight();
    
    void generateTestPattern(TEST_PATTERN_MODE ePatternMode, uint32_t u32Length, void* para);

    void getDlcLumArray(int32_t *pArray, int32_t arrayLen);
    int32_t getDlcLumAverageTemporary();
    int32_t getDlcLumTotalCount();

    bool switchDlcCurve(int16_t dlcCurveIndex);

    void getPixelRgb(int32_t eStage, int16_t x, int16_t y, int32_t eWindow, GET_RGB_DATA &rgbData);

    bool getPixelInfo(Screen_Pixel_Info *pPixInfo);

    bool setSwingLevel(int16_t swingLevel);
    int16_t getDlcHistogramMax();
    int16_t getDlcHistogramMin();

    bool forceFreerun(bool bEnable,bool b3D);

    bool setLocalDimmingMode(int16_t localDimingMode);
    bool setLocalDimmingBrightLevel(int16_t localDimingBrightLevel);
    bool turnOffLocalDimmingBacklight(bool bTurnOffLDBL);

    int8_t getResolution();
    int32_t getReproduceRate();
    void setResolution(int8_t resolution);
    void setReproduceRate(int32_t rate);
    bool enter4K2KMode(bool bEnable);
    void lock4K2KMode(bool bLock );
    int32_t get4K2KMode();
    bool set4K2KMode(int32_t enOutPutTimming,int32_t enUrsaMode);
    bool is4K2KMode(bool bEnable);
    bool is3DTVPlugedIn();
    bool setUltraClear(bool bEnable);
    bool autoHDMIColorRange();
    bool disableAllDualWinMode();
    void EnableVideoOut(bool bEnable);
    bool setHLinearScaling(bool bEnable, bool bSign, uint16_t u16Delta);
    bool setMEMCMode(String8 cmd);
    void setScalerGammaByIndex(int8_t u8Index);
    bool enableXvyccCompensation(bool bEnable, int32_t eWin);
    bool keepScalerOutput4k2k(bool bEnable);
    bool setHdmiPc(bool bEn);
    bool getHdmiPc();
    int32_t getHDMIColorFormat();
//------------------------------------------------------------------------------------
    bool panelInitial(String8 panelIniName);
    bool setGammaParameter(int32_t index, int32_t value);
    bool calGammaTable(GAMMA_TABLE *rgbData, int32_t MapMode);
    bool setScalerGammaTable (GAMMA_TABLE *gammaTable);
    uint8_t getScalerMotion();
//------------------------------------------------------------------------------------
    bool isSupportedZoom();
    bool setxvYCCEnable(bool bEnable, int8_t u8xvYCCmode);

    bool setOsdResolution(int32_t width, int32_t height);
    bool lockOutputTiming(int32_t eOutputTiming);
    bool unlockOutputTiming();
    int16_t GetSupportedTimingList(St_Timing_Info *pTimingInfoList, int16_t u16ListSize);
    int16_t GetSupportedTimingListCount();
    uint16_t getCurrentTimingId();

    bool getHdrLevel(E_HDR_LEVEL *pLevel, int32_t eWin);
    bool setHdrLevel(E_HDR_LEVEL enlevel, int32_t eWin);
    bool getAutoDetectHdrLevel(int32_t eWin);
    bool setAutoDetectHdrLevel(bool bAuto, int32_t eWin);
    bool IsHdrEnable(int32_t eWin);
    uint16_t getPQDelayTime();
    void getColorTemperature(PQL_COLOR_TEMPEX_DATA &pstColorTemp);
	 // EosTek Patch Begin
    //ashton: for wb adjust
    void asGetWbAdjustStar();
    void asGetWbAdjustExit();	
    // EosTek Patch End
private:
    static const sp<IPictureManagerService>& getPictureManagerService();

    static Mutex mLock;
    static sp<IPictureManagerService> mPictureManagerService;
    sp<IPictureManager> mPictureManager;
    sp<PictureManagerListener> mListener;

// ----------------------------------------------------------------------------

    virtual void binderDied(const wp<IBinder>& who);
    class DeathNotifier : public IBinder::DeathRecipient
    {
    public:
        ~DeathNotifier();

        virtual void binderDied(const wp<IBinder>& who);
    };
    static sp<DeathNotifier> mDeathNotifier;

// ----------------------------------------------------------------------------
};

#endif // _PICTURE_H_
