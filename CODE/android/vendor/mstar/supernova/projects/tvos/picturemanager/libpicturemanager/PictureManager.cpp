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
#define LOG_TAG "PictureManager"
#include <utils/Log.h>

#include <binder/IServiceManager.h>
#include <binder/IPCThreadState.h>
#include "PictureManager.h"
#include "PictureManagerTypeDefine.h"

Mutex PictureManager::mLock;
sp<IPictureManagerService> PictureManager::mPictureManagerService;
sp<PictureManager::DeathNotifier> PictureManager::mDeathNotifier;

const sp<IPictureManagerService>& PictureManager::getPictureManagerService()
{
    ALOGV("getPictureService\n");
    Mutex::Autolock _l(PictureManager::mLock);
    if(PictureManager::mPictureManagerService == NULL)
    {
        sp<IServiceManager> sm = defaultServiceManager();
        sp<IBinder> binder;
        uint32_t retry = 0;

        do
        {
            binder = sm->getService(String16("mstar.PictureManager"));
            if((binder != NULL) || (retry >= 2))
            {
                break;
            }
            ALOGW("PictureService not published, waiting...\n");
            usleep(500000); // 0.5 s
            retry++;
        } while(true);

        if(binder != NULL)
        {
            if(PictureManager::mDeathNotifier == NULL)
            {
                PictureManager::mDeathNotifier = new DeathNotifier();
            }
            binder->linkToDeath(PictureManager::mDeathNotifier);
            PictureManager::mPictureManagerService = interface_cast<IPictureManagerService>(binder);
        }
        if(PictureManager::mPictureManagerService == NULL)
            ALOGE("Can't get mstar.Picture service!\n");
    }

    return PictureManager::mPictureManagerService;
}

// ---------------------------------------------------------------------------

PictureManager::PictureManager()
{
    ALOGV("constructor\n\n");
}

PictureManager::~PictureManager()
{
    ALOGV("destructor\n\n");
   // disconnect();
    IPCThreadState::self()->flushCommands();
}

sp<PictureManager> PictureManager::connect()
{
    ALOGV("connect\n\n");

    sp<PictureManager> picture = new PictureManager();
    const sp<IPictureManagerService>& service = getPictureManagerService();
    if(service != NULL)
    {
        picture->mPictureManager = service->connect(picture);
    }

    if(picture->mPictureManager != NULL)
    {
        picture->mPictureManager->asBinder()->linkToDeath(picture);
    }
    else
    {
        picture.clear();
    }

    return picture;

}

void PictureManager::disconnect()
{
    ALOGV("disconnect\n\n");
    if(mPictureManager == NULL)
    {
        return;
    }

    mPictureManager->disconnect();
    mPictureManager->asBinder()->unlinkToDeath(this);
    mPictureManager.clear();
}

status_t PictureManager::setListener(const sp<PictureManagerListener>& listener)
{
    ALOGV("setListener\n\n");
    Mutex::Autolock _l(PictureManager::mLock);
    mListener = listener;
    return NO_ERROR;
}

void PictureManager::notify(int32_t msgType, int32_t ext1, int32_t ext2)
{
    sp<PictureManagerListener> listener;
    {
        Mutex::Autolock _l(PictureManager::mLock);
        listener = mListener;
    }
    if(listener != NULL)
    {
        listener->notify(msgType, ext1, ext2);
    }
}

void PictureManager::PostEvent_Template(int32_t nEvt, int32_t ext1, int32_t ext2)
{
    sp<PictureManagerListener> listener;
    {
        Mutex::Autolock _l(PictureManager::mLock);
        listener = mListener;
    }
    if(listener != NULL)
    {
        listener->PostEvent_Template(nEvt, ext1, ext2);
    }
}

void PictureManager::PostEvent_SetAspectratio(int32_t ext1, int32_t ext2)
{
    sp<PictureManagerListener> listener;
    {
        Mutex::Autolock _l(PictureManager::mLock);
        listener = mListener;
    }
    if(listener != NULL)
    {
        listener->PostEvent_SetAspectratio(ext1, ext2);
    }
}
void PictureManager::PostEvent_4K2KPhotoDisablePip(int32_t ext1, int32_t ext2)
{
    sp<PictureManagerListener> listener;
    {
        Mutex::Autolock _l(PictureManager::mLock);
        listener = mListener;
    }
    if(listener != NULL)
    {
        listener->PostEvent_4K2KPhotoDisablePip(ext1, ext2);
    }
}
void PictureManager::PostEvent_4K2KPhotoDisablePop(int32_t ext1, int32_t ext2)
{
    sp<PictureManagerListener> listener;
    {
        Mutex::Autolock _l(PictureManager::mLock);
        listener = mListener;
    }
    if(listener != NULL)
    {
        listener->PostEvent_4K2KPhotoDisablePop(ext1, ext2);
    }
}
void PictureManager::PostEvent_4K2KPhotoDisableDualview(int32_t ext1, int32_t ext2)
{
    sp<PictureManagerListener> listener;
    {
        Mutex::Autolock _l(PictureManager::mLock);
        listener = mListener;
    }
    if(listener != NULL)
    {
        listener->PostEvent_4K2KPhotoDisableDualview(ext1, ext2);
    }
}
void PictureManager::PostEvent_4K2KPhotoDisableTravelingmode(int32_t ext1, int32_t ext2)
{
    sp<PictureManagerListener> listener;
    {
        Mutex::Autolock _l(PictureManager::mLock);
        listener = mListener;
    }
    if(listener != NULL)
    {
        listener->PostEvent_4K2KPhotoDisableTravelingmode(ext1, ext2);
    }
}
void PictureManager::PostEvent_SnServiceDeadth(int32_t nEvt, int32_t ext1, int32_t ext2)
{
    sp<PictureManagerListener> listener;
    {
        Mutex::Autolock _l(PictureManager::mLock);
        listener = mListener;
    }
    if(listener != NULL)
    {
        listener->PostEvent_SnServiceDeadth(nEvt, ext1, ext2);
    }
}


void PictureManager::binderDied(const wp<IBinder>& who)
{
    ALOGV("IPicture died!\n");
    mPictureManager.clear();
    PostEvent_SnServiceDeadth(0,0,0);
}

PictureManager::DeathNotifier::~DeathNotifier()
{
    ALOGV("DeathNotifier::~DeathNotifier");
    Mutex::Autolock _l(PictureManager::mLock);
    if(PictureManager::mPictureManagerService != NULL)
    {
        PictureManager::mPictureManagerService->asBinder()->unlinkToDeath(this);
    }
}

void PictureManager::DeathNotifier::binderDied(const wp<IBinder>& who)
{
    ALOGV("IPictureService died!\n");
    Mutex::Autolock _l(PictureManager::mLock);
    PictureManager::mPictureManagerService.clear();
}

//tony add for Msrv_Picture

void PictureManager::disableDlc(void)
{
    if(mPictureManager == NULL)
       return;
    mPictureManager->disableDlc();
}

void PictureManager::disableOverScan(void)
{
    if(mPictureManager == NULL)
       return;
    mPictureManager->disableOverScan();
}

void PictureManager::disableWindow(void)
{
    if(mPictureManager == NULL)
       return;
    mPictureManager->disableWindow();
}

void PictureManager::enableDlc(void)
{
    if(mPictureManager == NULL)
       return;
    mPictureManager->enableDlc();
}

void PictureManager::enableOverScan(void)
{
    if(mPictureManager == NULL)
       return ;
    mPictureManager->enableOverScan();
}

void PictureManager::enableWindow(void)
{
    if(mPictureManager == NULL)
       return;
    mPictureManager->enableWindow();
}

bool PictureManager::freezeImage(void)
{
   if(mPictureManager == NULL)
       return false;
   return  mPictureManager->freezeImage();
}

int32_t PictureManager::getBacklight()
{
   if(mPictureManager == NULL)
       return -1;
   return  mPictureManager->getBacklight();
}

int32_t PictureManager::getBacklightMaxValue()
{
   if(mPictureManager == NULL)
       return -1;
   return  mPictureManager->getBacklightMaxValue();
}

int32_t PictureManager::getBacklightMinValue()
{
   if(mPictureManager == NULL)
       return -1;
   return  mPictureManager->getBacklightMinValue();
}

int32_t PictureManager::getDemoMode(void)
{
    if(mPictureManager == NULL)
       return 0;
    return  mPictureManager->getDemoMode();

}

bool PictureManager::getDynamicContrastCurve(int32_t* outCurve)
{
   if(mPictureManager == NULL)
       return -1;
   return  mPictureManager->getDynamicContrastCurve(outCurve);
}

void PictureManager::getPanelWidthHeight(PanelProperty &property)
{
    if(mPictureManager == NULL)
       return;
    mPictureManager->getPanelWidthHeight(property);
}

bool PictureManager::IsImageFreezed(void)
{
    if(mPictureManager == NULL)
       return false;
    return mPictureManager->IsImageFreezed();
}

bool PictureManager::isOverscanEnabled(void)
{
    if(mPictureManager == NULL)
       return false;
    return mPictureManager->isOverscanEnabled();
}

bool PictureManager::scaleWindow()
{
    if(mPictureManager == NULL)
       return false;
    return mPictureManager->scaleWindow();
}

bool PictureManager::selectWindow(int32_t windowId)
{
    if(mPictureManager == NULL)
       return false;
    return mPictureManager->selectWindow(windowId);
}

void PictureManager::setAspectRatio(int32_t enAspectRatioTYpe)
{
    if(mPictureManager == NULL)
       return;
    return mPictureManager->setAspectRatio(enAspectRatioTYpe);
}

int32_t PictureManager::getAspectRatio()
{
    if(mPictureManager == NULL)
        return -1;
    return mPictureManager->getAspectRatio();
}

void PictureManager::setBacklight(int32_t value)
{
printf("PictureManager::setBacklight:%d\n", value);

    if(mPictureManager == NULL)
       return;
    return mPictureManager->setBacklight(value);
}

void PictureManager::setColorTemperature(PQL_COLOR_TEMP_DATA &pstColorTemp)
{
    if(mPictureManager == NULL)
       return;
    return mPictureManager->setColorTemperature(pstColorTemp);
}

void PictureManager::setColorTemperatureEX(PQL_COLOR_TEMPEX_DATA &pstColorTemp)
{
    if(mPictureManager == NULL)
       return;
    return mPictureManager->setColorTemperatureEX(pstColorTemp);
}

void PictureManager::setCropWindow(VideoWindowType videoWindowType)
{
    if(mPictureManager == NULL)
       return;
    return mPictureManager->setCropWindow(videoWindowType.height,videoWindowType.width,videoWindowType.y,videoWindowType.x);
}


void PictureManager::setDemoMode(int32_t enMsMweType)
{
    if(mPictureManager == NULL)
       return;
    return mPictureManager->setDemoMode(enMsMweType);
}

void PictureManager::setDisplayWindow(VideoWindowType videoWindowType)
{
    if(mPictureManager == NULL)
       return;
    return mPictureManager->setDisplayWindow(videoWindowType.height,videoWindowType.width,videoWindowType.y,videoWindowType.x);
}

void PictureManager::setDynamicContrastCurve(int32_t *normalCurve, int32_t *lightCurve, int32_t *darkCurve)
{
    if(mPictureManager == NULL)
       return;
    return mPictureManager->setDynamicContrastCurve(normalCurve, lightCurve, darkCurve);
}

void PictureManager::setFilm(int32_t enMsFile)
{
    if(mPictureManager == NULL)
       return;
    return mPictureManager->setFilm(enMsFile);
}

void PictureManager::setMfc(EN_MFC_MODE enMode)
{
    if(mPictureManager == NULL)
       return;
    return mPictureManager->setMfc(enMode);
}

EN_MFC_MODE PictureManager::getMfc()
{
    if(mPictureManager == NULL)
       return E_MFC_OFF;
    return mPictureManager->getMfc();
}

bool PictureManager::setMpegNoiseReduction(int32_t enMNRMode)
{
    if(mPictureManager == NULL)
       return false;
    return mPictureManager->setMpegNoiseReduction(enMNRMode);
}

bool PictureManager::setNoiseReduction(int32_t nr)
{
    if(mPictureManager == NULL)
       return false;
    return mPictureManager->setNoiseReduction(nr);
}

void PictureManager::setOutputPattern(bool bEnable, int16_t u16Red, int16_t u16Green, int16_t u16Blue)
{
    if(mPictureManager == NULL)
       return;
    return mPictureManager->setOutputPattern(bEnable,u16Red,u16Green,u16Blue);
}

void PictureManager::setOverscan(int32_t bottom, int32_t top, int32_t right, int32_t left)
{
    if(mPictureManager == NULL)
       return;
    return mPictureManager->setOverscan(bottom,top,right,left);
}

void PictureManager::setPictureModeBrightness(int16_t value)
{
    if(mPictureManager == NULL)
       return ;
    return mPictureManager->setPictureModeBrightness(value);
}

void PictureManager::setPictureModeBrightness(int32_t setLocationType, int32_t value)
{
    if(mPictureManager == NULL)
       return ;
    return mPictureManager->setPictureModeBrightness(setLocationType,value);
}

bool PictureManager::GetPictureModeBrightness(int16_t * const value)
{
    if(mPictureManager == NULL)
       return false;
    return mPictureManager->GetPictureModeBrightness(value);
}
void PictureManager::setPictureModeColor(int16_t value)
{
    if(mPictureManager == NULL)
       return ;
    return mPictureManager->setPictureModeColor(value);
}

bool PictureManager::GetPictureModeSaturation(int16_t * const value)
{
    if(mPictureManager == NULL)
       return false;
    return mPictureManager->GetPictureModeSaturation(value);
}
void PictureManager::setPictureModeContrast(int16_t value)
{
    if(mPictureManager == NULL)
       return ;
    return mPictureManager->setPictureModeContrast(value);
}

bool PictureManager::GetPictureModeContrast(int16_t * const value)
{
    if(mPictureManager == NULL)
       return false;
    return mPictureManager->GetPictureModeContrast(value);
}
void PictureManager::setPictureModeSharpness(int16_t value)
{
    if(mPictureManager == NULL)
       return ;
    return mPictureManager->setPictureModeSharpness(value);
}

bool PictureManager::GetPictureModeSharpness(int16_t * const value)
{
    if(mPictureManager == NULL)
       return false;
    return mPictureManager->GetPictureModeSharpness(value);
}
void PictureManager::setPictureModeTint(int16_t value)
{
    if(mPictureManager == NULL)
       return ;
    return mPictureManager->setPictureModeTint(value);
}
bool PictureManager::GetPictureModeHue(int16_t * const value)
{
    if(mPictureManager == NULL)
       return false;
    return mPictureManager->GetPictureModeHue(value);
}

void PictureManager::setPictureModeInputSource(int32_t inputSource)
{
    if(mPictureManager == NULL)
       return ;
    return mPictureManager->setPictureModeInputSource(inputSource);
}


bool PictureManager::setHdmiPc(bool bEn)
{
    if(mPictureManager == NULL)
       return false;
    return mPictureManager->setHdmiPc(bEn);
}


bool PictureManager::getHdmiPc()
{
    if(mPictureManager == NULL)
       return false;
    return mPictureManager->getHdmiPc();
}

void PictureManager::setDebugMode(bool mode)
{
    if(mPictureManager == NULL)
       return ;
    return mPictureManager->setDebugMode(mode);
}

void PictureManager::setWindowInvisible()
{
    if(mPictureManager == NULL)
       return ;
    return mPictureManager->setWindowInvisible();
}

void PictureManager::setWindowVisible()
{
    if(mPictureManager == NULL)
       return ;
    return mPictureManager->setWindowVisible();
}

bool PictureManager::unFreezeImage(void)
{
    if(mPictureManager == NULL)
       return false;
    return mPictureManager->unFreezeImage();
}

int16_t PictureManager::getDlcAverageLuma()
{
    if(mPictureManager == NULL)
       return 0;
    return mPictureManager->getDlcAverageLuma();
}

bool PictureManager::disableOsdWindow(int32_t win)
{
    if(mPictureManager == NULL)
       return false;

    return mPictureManager->disableOsdWindow(win);
}

bool PictureManager::disableAllOsdWindow()
{
    if(mPictureManager == NULL)
       return false;

    return mPictureManager->disableAllOsdWindow();
}

bool PictureManager::setOsdWindow(int32_t win, uint16_t u16StartX, uint16_t u16Width, uint16_t u16StartY, uint16_t u16Height)
{
    if(mPictureManager == NULL)
       return false;

    return mPictureManager->setOsdWindow(win, u16StartX, u16Width, u16StartY, u16Height);
}

void PictureManager::setColorRange(bool colorRange0_255)
{
    if(mPictureManager == NULL)
       return;

    mPictureManager->setColorRange(colorRange0_255);
}

bool PictureManager::autoHDMIColorRange()
{
    if(mPictureManager == NULL)
       return false;

    return mPictureManager->autoHDMIColorRange();
}

int32_t PictureManager::getHDMIColorFormat()
{
    ALOGV("PictureManager getHDMIColorFormat\n");
    if(mPictureManager == NULL)
       return false;
    return mPictureManager->getHDMIColorFormat();
}


int32_t PictureManager::getCustomerPqRuleNumber()
{
    if(mPictureManager == NULL)
       return 0;

    return mPictureManager->getCustomerPqRuleNumber();
}

int32_t PictureManager::getStatusNumberByCustomerPqRule(int32_t ruleType)
{
    if(mPictureManager == NULL)
       return 0;

    return mPictureManager->getStatusNumberByCustomerPqRule(ruleType);
}

bool PictureManager::setStatusByCustomerPqRule(int32_t ruleType, int32_t ruleStatus)
{
    if(mPictureManager == NULL)
       return false;

    return mPictureManager->setStatusByCustomerPqRule(ruleType, ruleStatus);
}

bool PictureManager::moveWindow()
{
    if(mPictureManager == NULL)
       return false;

    ALOGV("PictureManager moveWindow\n");
    return mPictureManager->moveWindow();
}

void PictureManager::enableBacklight()
{
    ALOGV("PictureManager enableBacklight\n");
    if(mPictureManager == NULL)
       return;

    mPictureManager->enableBacklight();
}

void PictureManager::disableBacklight()
{
    ALOGV("PictureManager disableBacklight\n");
    if(mPictureManager == NULL)
       return;

    mPictureManager->disableBacklight();
}

void PictureManager::generateTestPattern(TEST_PATTERN_MODE ePatternMode, uint32_t u32Length, void* para)
{
    ALOGV("PictureManager setMVOPTestPattern[PMode=%d,uL=%d]\n",ePatternMode,u32Length);
    if(mPictureManager == NULL)
       return;

    mPictureManager->generateTestPattern(ePatternMode, u32Length, para);
}

void PictureManager::getDlcLumArray(int32_t *pArray, int32_t arrayLen)
{
    ALOGV("PictureManager getDlcLumArray\n");
    if(mPictureManager == NULL)
       return;

    mPictureManager->getDlcLumArray(pArray, arrayLen);
}

int32_t PictureManager::getDlcLumAverageTemporary()
{
    ALOGV("PictureManager getDlcLumAverageTemporary\n");
    if(mPictureManager == NULL)
       return 0;

    return mPictureManager->getDlcLumAverageTemporary();
}

int32_t PictureManager::getDlcLumTotalCount()
{
    ALOGV("PictureManager getDlcLumTotalCount\n");
    if(mPictureManager == NULL)
       return 0;

    return mPictureManager->getDlcLumTotalCount();
}

bool PictureManager::switchDlcCurve(int16_t dlcCurveIndex)
{
    ALOGV("PictureManager switchDlcCurve\n");
    if(mPictureManager == NULL)
       return false;

    return mPictureManager->switchDlcCurve(dlcCurveIndex);
}

void PictureManager::getPixelRgb(int32_t eStage, int16_t x, int16_t y, int32_t eWindow, GET_RGB_DATA &rgbData)
{
    ALOGV("PictureManager getPixelRgb\n");
    if(mPictureManager == NULL)
       return;

    mPictureManager->getPixelRgb(eStage, x, y, eWindow, rgbData);
}

bool PictureManager::getPixelInfo(Screen_Pixel_Info *pPixInfo)
{
    ALOGV("PictureManager getPixelInfo\n");
    if(mPictureManager == NULL)
       return false;

    return mPictureManager->getPixelInfo(pPixInfo);
}

bool PictureManager::setSwingLevel(int16_t swingLevel)
{
    ALOGV("PictureManager setSwingLevel\n");
    if(mPictureManager == NULL)
       return false;

    return mPictureManager->setSwingLevel(swingLevel);
}

int16_t PictureManager::getDlcHistogramMax()
{
    ALOGV("PictureManager getDlcHistogramMax\n");
    if(mPictureManager == NULL)
       return -1;

    return mPictureManager->getDlcHistogramMax();
}

int16_t PictureManager::getDlcHistogramMin()
{
    ALOGV("PictureManager getDlcHistogramMin\n");
    if(mPictureManager == NULL)
       return -1;

    return mPictureManager->getDlcHistogramMin();
}

bool PictureManager::forceFreerun(bool bEnable,bool b3D)
{
    ALOGV("PictureManager forceFreerun\n");
    if(mPictureManager == NULL)
       return false;

    return mPictureManager->forceFreerun(bEnable, b3D);
}

bool PictureManager::setLocalDimmingMode(int16_t localDimingMode)
{
    ALOGV("PictureManager setLocalDimmingMode\n");
    if(mPictureManager == NULL)
       return false;

    return mPictureManager->setLocalDimmingMode(localDimingMode);
}

bool PictureManager::setLocalDimmingBrightLevel(int16_t localDimingBrightLevel)
{
    ALOGV("PictureManager setLocalDimmingBrightLevel\n");
    if(mPictureManager == NULL)
       return false;

    return mPictureManager->setLocalDimmingBrightLevel(localDimingBrightLevel);
}

bool PictureManager::turnOffLocalDimmingBacklight(bool bTurnOffLDBL)
{
    ALOGV("PictureManager turnOffLocalDimmingBacklight\n");
    if(mPictureManager == NULL)
       return false;

    return mPictureManager->turnOffLocalDimmingBacklight(bTurnOffLDBL);
}

bool PictureManager::enter4K2KMode(bool bEnable)
{
    ALOGV("PictureManager enter4K2KMode\n");
    if(mPictureManager == NULL)
       return false;

    return mPictureManager->enter4K2KMode(bEnable);
}

void PictureManager::lock4K2KMode(bool bLock )
{
    ALOGV("PictureManager enter4K2KMode\n");
    if(mPictureManager == NULL)
       return ;
    mPictureManager->lock4K2KMode(bLock);
}
int32_t PictureManager::get4K2KMode()
{
    ALOGV("PictureManager get4K2KMode\n");
    if(mPictureManager == NULL)
       return false;
    return mPictureManager->get4K2KMode();
}
bool PictureManager::set4K2KMode(int32_t enOutPutTimming,int32_t enUrsaMode)
{
    ALOGV("PictureManager set4K2KMode\n");
    if(mPictureManager == NULL)
       return false;
    return mPictureManager->set4K2KMode( enOutPutTimming, enUrsaMode);
}
bool PictureManager::is3DTVPlugedIn()
{

    ALOGV("PictureManager is3DTVPlugedIn\n");
    if(mPictureManager == NULL)
       return false;

    return mPictureManager->is3DTVPlugedIn();
}
bool PictureManager::is4K2KMode(bool bEnable)
{

    ALOGV("PictureManager is4K2KMode\n");
    if(mPictureManager == NULL)
       return false;

    return mPictureManager->is4K2KMode(bEnable);
}
bool PictureManager::setUltraClear(bool bEnable)
{
    ALOGV("PictureManager setUltraClear\n");
    if(mPictureManager == NULL)
       return false;

    return mPictureManager->setUltraClear(bEnable);
}

void PictureManager::setResolution(int8_t resolution)
{
    ALOGV("PictureManager setResolution\n");
    if (mPictureManager == NULL)
    {
        return;
    }

    mPictureManager->setResolution(resolution);
}

int8_t PictureManager::getResolution( )
{
    ALOGV("PictureManager getResolution\n");
    if (mPictureManager == NULL)
    {
        return 0;
    }

    return mPictureManager->getResolution();
}

void PictureManager::setReproduceRate(int32_t rate)
{
    ALOGV("PictureManager setReproduceRate\n");
    if (mPictureManager == NULL)
    {
        return;
    }

    mPictureManager->setReproduceRate(rate);
}

int32_t PictureManager::getReproduceRate()
{
    ALOGV("PictureManager getReproduceRate\n");
    if (mPictureManager == NULL)
    {
        return 0;
    }

    return mPictureManager->getReproduceRate();
}
bool PictureManager::disableAllDualWinMode()
{
    if(mPictureManager == NULL)
       return false;

    return mPictureManager->disableAllDualWinMode();
}

void PictureManager::EnableVideoOut(bool bEnable)
{
    ALOGV("PictureManager EnableVideoOut\n");
    if (mPictureManager == NULL)
    {
        return;
    }

    mPictureManager->EnableVideoOut(bEnable);
}

bool PictureManager::setHLinearScaling(bool bEnable, bool bSign, uint16_t u16Delta)
{
    ALOGV("PictureManager setHLinearScaling\n");
    if(mPictureManager == NULL)
       return false;

    return mPictureManager->setHLinearScaling(bEnable,bSign,u16Delta);
}

bool PictureManager::setMEMCMode(String8 cmd)
{
    ALOGV("PictureManager setMEMCMode\n");
    if(mPictureManager == NULL)
       return false;

    return mPictureManager->setMEMCMode(cmd);
}

void PictureManager::setScalerGammaByIndex(int8_t u8Index)
{
    ALOGV("PictureManager setScalerGammaByIndex\n");
    if (mPictureManager == NULL)
    {
        return;
    }

    mPictureManager->setScalerGammaByIndex(u8Index);
}

bool PictureManager::enableXvyccCompensation(bool bEnable, int32_t eWin)
{
    ALOGV("PictureManager enableXvyccCompensation\n");
    if (mPictureManager == NULL)
    {
        return false;
    }

    return mPictureManager->enableXvyccCompensation(bEnable, eWin);
}

bool PictureManager::keepScalerOutput4k2k(bool bEnable)
{
    ALOGV("PictureManager keepScalerOutput4k2k\n");
    if (mPictureManager == NULL)
    {
        return false;
    }
    return mPictureManager->keepScalerOutput4k2k(bEnable);
}

bool PictureManager::setOsdResolution(int32_t width, int32_t height)
{
    ALOGV("PictureManager setOsdResolution\n");
    if (mPictureManager == NULL)
    {
        return false;
    }
    return mPictureManager->setOsdResolution(width, height);
}

bool PictureManager::lockOutputTiming(int32_t eOutputTiming)
{
    ALOGV("PictureManager lockOutputTiming\n");
    if (mPictureManager == NULL)
    {
        return false;
    }
    return mPictureManager->lockOutputTiming(eOutputTiming);
}

bool PictureManager::unlockOutputTiming()
{
    ALOGV("PictureManager unlockOutputTiming\n");
    if (mPictureManager == NULL)
    {
        return false;
    }
    return mPictureManager->unlockOutputTiming();
}

//------------------------------------------------------------------------------------
bool PictureManager::panelInitial(String8 panelIniName)
{
    ALOGV("PictureManager panelInitial\n");
    if (mPictureManager == NULL)
    {
        return false;
    }

    return mPictureManager->panelInitial(panelIniName);
}

bool PictureManager::setGammaParameter(int32_t index, int32_t value)
{
    ALOGV("PictureManager setGammaParameter\n");
    if (mPictureManager == NULL)
    {
        return false;
    }

    return mPictureManager->setGammaParameter(index, value);
}

bool PictureManager::calGammaTable(GAMMA_TABLE *rgbData, int32_t MapMode)
{
    ALOGV("PictureManager calGammaTable\n");
    if (mPictureManager == NULL)
    {
        return false;
    }

    return mPictureManager->calGammaTable(rgbData, MapMode);
}

bool PictureManager::setScalerGammaTable (GAMMA_TABLE *gammaTable)
{
    ALOGV("PictureManager setScalerGammaTable\n");
    if (mPictureManager == NULL)
    {
        return false;
    }

    return mPictureManager->setScalerGammaTable(gammaTable);
}

uint8_t PictureManager::getScalerMotion()
{
    ALOGV("PictureManager getScalerMotion\n");
    if (mPictureManager == NULL)
    {
        return 0;
    }

    return mPictureManager->getScalerMotion();
}

bool PictureManager::setxvYCCEnable(bool bEnable, int8_t u8xvYCCmode)
{
    ALOGV("PictureManager setxvYCCEnable\n");
    if (mPictureManager == NULL)
    {
        return false;
    }

    return mPictureManager->setxvYCCEnable(bEnable, u8xvYCCmode);
}

int16_t PictureManager::GetSupportedTimingList(St_Timing_Info *pTimingInfoList, int16_t u16ListSize)
{
    ALOGV("PictureManager GetSupportedTimingList\n");
    if (mPictureManager == NULL)
    {
        return 0;
    }

    return mPictureManager->GetSupportedTimingList(pTimingInfoList, u16ListSize);
}

int16_t PictureManager::GetSupportedTimingListCount()
{
    ALOGV("PictureManager GetSupportedTimingListCount\n");
    if (mPictureManager == NULL)
    {
        return 0;
    }

    return mPictureManager->GetSupportedTimingListCount();
}

uint16_t PictureManager::getCurrentTimingId()
{
    ALOGV("PictureManager getCurrentTimingId\n");
    if (mPictureManager == NULL)
    {
        return 0;
    }

    return mPictureManager->getCurrentTimingId();
}

bool PictureManager::getHdrLevel(E_HDR_LEVEL *pLevel, int32_t eWin)
{
    ALOGV("PictureManager getHdrLevel\n");
    if (mPictureManager == NULL)
    {
        return 0;
    }

    return mPictureManager->getHdrLevel(pLevel, eWin);
}

bool PictureManager::setHdrLevel(E_HDR_LEVEL enlevel, int32_t eWin)
{
    ALOGV("PictureManager setHdrLevel\n");
    if (mPictureManager == NULL)
    {
        return 0;
    }

    return mPictureManager->setHdrLevel(enlevel, eWin);
}

bool PictureManager::getAutoDetectHdrLevel(int32_t eWin)
{
    ALOGV("PictureManager getAutoDetectHdrLevel\n");
    if (mPictureManager == NULL)
    {
        return 0;
    }

    return mPictureManager->getAutoDetectHdrLevel(eWin);
}

bool PictureManager::setAutoDetectHdrLevel(bool bAuto, int32_t eWin)
{
    ALOGV("PictureManager setAutoDetectHdrLevel\n");
    if (mPictureManager == NULL)
    {
        return 0;
    }

    return mPictureManager->setAutoDetectHdrLevel(bAuto, eWin);
}

bool PictureManager::IsHdrEnable(int32_t eWin)
{
    ALOGV("PictureManager IsHdrEnable\n");
    if (mPictureManager == NULL)
    {
        return 0;
    }

    return mPictureManager->IsHdrEnable(eWin);
}

uint16_t PictureManager::getPQDelayTime()
{
    ALOGV("PictureManager getPQDelayTime\n");
    if (mPictureManager == NULL)
    {
        return 0;
    }

    return mPictureManager->getPQDelayTime();
}

// EosTek Patch Begin
//ashton: for wb adjust
void PictureManager::asGetWbAdjustStar()
{	
    if(mPictureManager == NULL)
       return;
    return mPictureManager->asGetWbAdjustStar();
}

void PictureManager::asGetWbAdjustExit()
{	
    if(mPictureManager == NULL)
       return;
    return mPictureManager->asGetWbAdjustExit();
}

// EosTek Patch End

//------------------------------------------------------------------------------------

bool PictureManager::isSupportedZoom ()
{
    ALOGV("PictureManager isSupportedZoom\n");
    if (mPictureManager == NULL)
    {
        return false;
    }

    return mPictureManager->isSupportedZoom();
}

void PictureManager::getColorTemperature(PQL_COLOR_TEMPEX_DATA &pstColorTemp)
{
    ALOGV("PictureManager getColorTemperature\n");
    if(mPictureManager == NULL)
    {
        return;
    }

    mPictureManager->getColorTemperature(pstColorTemp);
}

