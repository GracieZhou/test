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
#define LOG_TAG "FactoryManager"
#include <utils/Log.h>

#include <binder/IServiceManager.h>
#include <binder/IPCThreadState.h>
#include "FactoryManager.h"

Mutex FactoryManager::mLock;
sp<IFactoryManagerService> FactoryManager::mFactoryManagerService;
sp<FactoryManager::DeathNotifier> FactoryManager::mDeathNotifier;

const sp<IFactoryManagerService>& FactoryManager::getFactoryManagerService()
{
    ALOGV("getFactoryManagerService\n");
    Mutex::Autolock _l(FactoryManager::mLock);
    if(FactoryManager::mFactoryManagerService == NULL)
    {
        sp<IServiceManager> sm = defaultServiceManager();
        sp<IBinder> binder;
        uint32_t retry = 0;

        do
        {
            binder = sm->getService(String16("mstar.FactoryManager"));
            if((binder != NULL) || (retry >= 2))
            {
                break;
            }
            ALOGW("FactoryManagerService not published, waiting...\n");
            usleep(500000); // 0.5 s
            retry++;
        } while(true);

        if(binder != NULL)
        {
            if(FactoryManager::mDeathNotifier == NULL)
            {
                FactoryManager::mDeathNotifier = new DeathNotifier();
            }
            binder->linkToDeath(FactoryManager::mDeathNotifier);
            FactoryManager::mFactoryManagerService = interface_cast<IFactoryManagerService>(binder);
        }
        if(FactoryManager::mFactoryManagerService == NULL)
            ALOGE("Can't get mstar.FactoryManager service!\n");
    }

    return FactoryManager::mFactoryManagerService;
}

// ---------------------------------------------------------------------------

FactoryManager::FactoryManager()
{
    ALOGV("constructor\n\n");
}

FactoryManager::~FactoryManager()
{
    ALOGV("destructor\n\n");
   // disconnect();
    IPCThreadState::self()->flushCommands();
}

sp<FactoryManager> FactoryManager::connect()
{
    ALOGV("connect\n\n");

    sp<FactoryManager> src = new FactoryManager();
    const sp<IFactoryManagerService>& service = getFactoryManagerService();
    if(service != NULL)
    {
        src->mFactoryManager = service->connect(src);
    }

    if(src->mFactoryManager != NULL)
    {
        src->mFactoryManager->asBinder()->linkToDeath(src);
    }
    else
    {
        src.clear();
    }

    return src;

}

void FactoryManager::disconnect()
{
    ALOGV("disconnect\n\n");
    if(mFactoryManager == NULL)
    {
        return;
    }

    mFactoryManager->disconnect();
    mFactoryManager->asBinder()->unlinkToDeath(this);
    mFactoryManager.clear();
}


bool FactoryManager::autoAdc()
{
    ALOGV("autoAdc\n\n");
    if (mFactoryManager == NULL)
    {
        return -1;
    }

    return mFactoryManager->autoAdc();
}

void FactoryManager::copySubColorDataToAllSource()
{
    ALOGV("copySubColorDataToAllSource\n\n");
    if (mFactoryManager == NULL)
    {
        return;
    }

    return mFactoryManager->copySubColorDataToAllSource();
}


void FactoryManager::copyWhiteBalanceSettingToAllSource()
{
    ALOGV("copyWhiteBalanceSettingToAllSource\n\n");
    if (mFactoryManager == NULL)
    {
        return;
    }

    return mFactoryManager->copyWhiteBalanceSettingToAllSource();
}

bool FactoryManager::disablePVRRecordAll()
{
    ALOGV("disablePVRRecordAll\n\n");
    if (mFactoryManager == NULL)
    {
        return -1;
    }

    return mFactoryManager->disablePVRRecordAll();
}


bool FactoryManager::disableUart()
{
    ALOGV("disableUart\n\n");
    if (mFactoryManager == NULL)
    {
        return -1;
    }

    return mFactoryManager->disableUart();
}

bool FactoryManager::disableWdt()
{
    ALOGV("disableWdt\n\n");
    if (mFactoryManager == NULL)
    {
        return -1;
    }

    return mFactoryManager->disableWdt();
}


bool FactoryManager::enablePVRRecordAll()
{
    ALOGV("enablePVRRecordAll\n\n");
    if (mFactoryManager == NULL)
    {
        return -1;
    }

    return mFactoryManager->enablePVRRecordAll();
}

bool FactoryManager::enableUart()
{
    ALOGV("enableUart\n\n");
    if (mFactoryManager == NULL)
    {
        return -1;
    }

    return mFactoryManager->enableUart();
}


bool FactoryManager::enableWdt()
{
    ALOGV("enableWdt\n\n");
    if (mFactoryManager == NULL)
    {
        return -1;
    }

    return mFactoryManager->enableWdt();
}

void FactoryManager::getAdcGainOffset(int enWin, int eAdcIndex,PqlCalibrationData &pstADCGainOffsetOut)
{
    ALOGV("getAdcGainOffset\n\n");
    if (mFactoryManager == NULL)
    {

    }

    mFactoryManager->getAdcGainOffset(enWin,eAdcIndex,pstADCGainOffsetOut);
}

void FactoryManager::setUartEnv(bool on)
{
    ALOGV("setUartEnv\n\n");
    if (mFactoryManager == NULL)
    {

    }

    mFactoryManager->setUartEnv(on);
}

bool FactoryManager::getUartEnv()
{
    ALOGV("GetUartEnv\n\n");
    if (mFactoryManager == NULL)
    {
        return false;
    }

    return mFactoryManager->getUartEnv();
}


int FactoryManager::getDisplayResolution()
{
    ALOGV("getDisplayResolution\n\n");
    if (mFactoryManager == NULL)
    {
        return -1;
    }

    return mFactoryManager->getDisplayResolution();
}

void FactoryManager::getPictureModeValue(PictureModeValue &PModeValue)
{
    ALOGV("getPictureModeValue\n\n");
    if (mFactoryManager == NULL)
    {
       // return -1;
    }

   mFactoryManager->getPictureModeValue(PModeValue);
}

int FactoryManager::getQmapCurrentTableIdx(short ipIndex)
{
    ALOGV("getQmapCurrentTableIdx\n\n");
    if (mFactoryManager == NULL)
    {
        return -1;
    }

    return mFactoryManager->getQmapCurrentTableIdx(ipIndex);
}

String8 FactoryManager::getQmapIpName(short ipIndex)
{
    ALOGV("getQmapIpName\n\n");
    if (mFactoryManager == NULL)
    {
       // return;
    }

    return mFactoryManager->getQmapIpName(ipIndex);
}


int FactoryManager::getQmapIpNum()
{
    ALOGV("getQmapIpNum\n\n");
    if (mFactoryManager == NULL)
    {
        return -1;
    }

    return mFactoryManager->getQmapIpNum();
}


String8 FactoryManager::getQmapTableName(short ipIndex, short tableIndex)
{
    ALOGV("getQmapTableName\n\n");
    if (mFactoryManager == NULL)
    {
//        return -1;
    }

    return mFactoryManager->getQmapTableName(ipIndex,tableIndex);
}

int FactoryManager::getQmapTableNum(short ipIndex)
{
    ALOGV("getQmapTableNum\n\n");
    if (mFactoryManager == NULL)
    {
        return -1;
    }

    return mFactoryManager->getQmapTableNum(ipIndex);
}

void FactoryManager::getWbGainOffset(int eColorTemp ,WbGainOffset &GainOffset)
{
    ALOGV("getWbGainOffset\n\n");
    if (mFactoryManager == NULL)
    {
        return;
    }

    mFactoryManager->getWbGainOffset(eColorTemp, GainOffset);
}

void FactoryManager::getWbGainOffsetEx(int eColorTemp, int enSrcType ,WbGainOffsetEx &WbExOut)
{
    ALOGV("getWbGainOffsetEx\n\n");
    if (mFactoryManager == NULL)
    {
        return;
    }

    mFactoryManager->getWbGainOffsetEx(eColorTemp,enSrcType,WbExOut);
}

bool FactoryManager::isAgingModeOn()
{
    ALOGV("isAgingModeOn\n\n");
    if (mFactoryManager == NULL)
    {
        return -1;
    }

    return mFactoryManager->isAgingModeOn();
}

bool FactoryManager::isPVRRecordAllOn()
{
    ALOGV("isPVRRecordAllOn\n\n");
    if (mFactoryManager == NULL)
    {
        return -1;
    }

    return mFactoryManager->isPVRRecordAllOn();
}

bool FactoryManager::isUartOn()
{
    ALOGV("isUartOn\n\n");
    if (mFactoryManager == NULL)
    {
        return -1;
    }

    return mFactoryManager->isUartOn();
}

bool FactoryManager::isWdtOn()
{
    ALOGV("isWdtOn\n\n");
    if (mFactoryManager == NULL)
    {
        return -1;
    }

    return mFactoryManager->isWdtOn();
}


void FactoryManager::loadPqTable(int tableIndex, int ipIndex)
{
    ALOGV("loadPqTable\n\n");
    if (mFactoryManager == NULL)
    {
        return;
    }

    return mFactoryManager->loadPqTable(tableIndex,ipIndex);
}

bool FactoryManager::resetDisplayResolution()
{
    ALOGV("resetDisplayResolution\n\n");
    if (mFactoryManager == NULL)
    {
        return -1;
    }

    return mFactoryManager->resetDisplayResolution();
}

bool FactoryManager::restoreDbFromUsb()
{
    ALOGV("restoreDbFromUsb\n\n");
    if (mFactoryManager == NULL)
    {
        return -1;
    }

    return mFactoryManager->restoreDbFromUsb();
}

void FactoryManager::setAdcGainOffset(int enWin, int eAdcIndex, PqlCalibrationData
 stADCGainOffset)
{
    ALOGV("setAdcGainOffset\n\n");
    if (mFactoryManager == NULL)
    {
        return;
    }

    return mFactoryManager->setAdcGainOffset(enWin,eAdcIndex,stADCGainOffset);
}

bool FactoryManager::setBrightness(short subBrightness)
{
    ALOGV("setBrightness\n\n");
    if (mFactoryManager == NULL)
    {
        return -1;
    }

    return mFactoryManager->setBrightness(subBrightness);
}

bool FactoryManager::setContrast(short subContrast)
{
    ALOGV("setContrast\n\n");
    if (mFactoryManager == NULL)
    {
        return -1;
    }

    return mFactoryManager->setContrast(subContrast);
}


void FactoryManager::setFactoryVdInitParameter(FactoryNsVdSet factoryNsVdSetVo)
{
    ALOGV("setFactoryVdInitParameter\n\n");
    if (mFactoryManager == NULL)
    {
        return;
    }

    return mFactoryManager->setFactoryVdInitParameter(factoryNsVdSetVo);
}

void FactoryManager::setFactoryVDParameter(FactoryNsVdSet factoryNsVdSetVo)
{
    ALOGV("setFactoryVDParameter\n\n");
    if (mFactoryManager == NULL)
    {
        return;
    }

    return mFactoryManager->setFactoryVDParameter(factoryNsVdSetVo);
}

bool FactoryManager::setHue(short hue)
{
    ALOGV("setHue\n\n");
    if (mFactoryManager == NULL)
    {
        return -1;
    }

    return mFactoryManager->setHue(hue);
}

bool FactoryManager::setSaturation(short saturation)
{
    ALOGV("setSaturation\n\n");
    if (mFactoryManager == NULL)
    {
        return -1;
    }

    return mFactoryManager->setSaturation(saturation);
}

bool FactoryManager::setSharpness(short sharpness)
{
    ALOGV("setSharpness\n\n");
    if (mFactoryManager == NULL)
    {
        return -1;
    }

    return mFactoryManager->setSharpness(sharpness);
}

void FactoryManager::setVideoTestPattern(int enColor)
{
    ALOGV("setVideoTestPattern\n\n");
    if (mFactoryManager == NULL)
    {
        return;
    }

    return mFactoryManager->setVideoTestPattern(enColor);
}

bool FactoryManager::setVideoMuteColor(int enColor)
{
    ALOGV("setVideoMuteColor\n\n");
    if (mFactoryManager == NULL)
    {
        return false;
    }
    return mFactoryManager->setVideoMuteColor(enColor);
}

void FactoryManager::setWbGainOffset(int eColorTemp, short redGain, short greenGain, short blueGain, short redOffset, short greenOffset, short blueOffset)
{
    ALOGV("setWbGainOffset\n\n");
    if (mFactoryManager == NULL)
    {
        return;
    }

    return mFactoryManager->setWbGainOffset(eColorTemp,redGain,greenGain,blueGain,redOffset,greenOffset,blueOffset);
}

void FactoryManager::setWbGainOffsetEx(int eColorTemp, int redGain, int greenGain, int blueGain, int redOffset, int greenOffset, int blueOffset, int enSrcType)
{
    ALOGV("setWbGainOffsetEx\n\n");
    if (mFactoryManager == NULL)
    {
        return;
    }

    return mFactoryManager->setWbGainOffsetEx(eColorTemp,redGain,greenGain,blueGain,redOffset,greenOffset,blueOffset,enSrcType);
}

bool FactoryManager::storeDbToUsb()
{
    ALOGV("storeDbToUsb\n\n");
    if (mFactoryManager == NULL)
    {
        return -1;
    }

    return mFactoryManager->storeDbToUsb();
}


int32_t FactoryManager::getFwVersion(int32_t type)
{
     ALOGV("getFwVersion\n\n");
    if (mFactoryManager == NULL)
    {
        return -1;
    }

    return mFactoryManager->getFwVersion(type);
}

bool FactoryManager::updateSscParameter()
{

     ALOGV("updateSscParameter\n\n");
    if (mFactoryManager == NULL)
    {
        return -1;
    }

    return mFactoryManager->updateSscParameter();

}

void FactoryManager::setDebugMode(bool mode)
{
    ALOGV("setDebugMode\n\n");
    if (mFactoryManager == NULL)
    {
        return;
    }

    mFactoryManager->setDebugMode(mode);
}

void FactoryManager::getSoftwareVersion(String8 &version)
{
    ALOGV("getSoftwareVersion\n\n");
    if (mFactoryManager == NULL)
    {
        return;
    }

    mFactoryManager->getSoftwareVersion(version);
}

void FactoryManager::stopTvService()
{
    ALOGV("stopTvService\n\n");
    if (mFactoryManager == NULL)
    {
        return;
    }

    mFactoryManager->stopTvService();
}

void FactoryManager::restoreFactoryAtvProgramTable(short cityIndex)
{
    ALOGV("restoreFactoryAtvProgramTable\n\n");
    if (mFactoryManager == NULL)
    {
        return;
    }

    mFactoryManager->restoreFactoryAtvProgramTable(cityIndex);
}

void FactoryManager::restoreFactoryDtvProgramTable(short cityIndex)
{
    ALOGV("restoreFactoryDtvProgramTable\n\n");
    if (mFactoryManager == NULL)
    {
        return;
    }

    mFactoryManager->restoreFactoryDtvProgramTable(cityIndex);
}

void FactoryManager::setPQParameterViaUsbKey()
{
    ALOGV("setPQParameterViaUsbKey\n\n");
    if (mFactoryManager == NULL)
    {
        return;
    }

    mFactoryManager->setPQParameterViaUsbKey();
}

void FactoryManager::setHDCPKeyViaUsbKey()
{
    ALOGV("setHDCPKeyViaUsbKey\n\n");
    if (mFactoryManager == NULL)
    {
        return;
    }

    mFactoryManager->setHDCPKeyViaUsbKey();
}

void FactoryManager::setCIPlusKeyViaUsbKey()
{
    ALOGV("setCIPlusKeyViaUsbKey\n\n");
    if (mFactoryManager == NULL)
    {
        return;
    }

    mFactoryManager->setCIPlusKeyViaUsbKey();
}

void FactoryManager::setMACAddrViaUsbKey()
{
    ALOGV("setMACAddrViaUsbKey\n\n");
    if (mFactoryManager == NULL)
    {
        return;
    }

    mFactoryManager->setMACAddrViaUsbKey();
}

void FactoryManager::getMACAddrString(String8 &mac)
{
    ALOGV("getMACAddrString\n\n");
    if (mFactoryManager == NULL)
    {
        return;
    }

    mFactoryManager->getMACAddrString(mac);
}

bool FactoryManager::startUartDebug()
{
    ALOGV("startUartDebug\n");

    if (mFactoryManager == NULL)
    {
        return false;
    }

    return mFactoryManager->startUartDebug();
}

bool FactoryManager::uartSwitch()
{
    ALOGV("uartSwitch\n");

    if (mFactoryManager == NULL)
    {
        return false;
    }

    return mFactoryManager->uartSwitch();
}

bool FactoryManager::readBytesFromI2C(int32_t u32gID, uint8_t u8AddrSize, uint8_t *pu8Addr, uint16_t u16Size, uint8_t *pu8Data)
{
    ALOGV("readBytesFromI2C\n");

    if (mFactoryManager == NULL)
    {
        return false;
    }

    return mFactoryManager->readBytesFromI2C(u32gID, u8AddrSize, pu8Addr, u16Size, pu8Data);
}

bool FactoryManager::writeBytesToI2C(int32_t u32gID, uint8_t u8AddrSize, uint8_t *pu8Addr, uint16_t u16Size, uint8_t *pu8Data)
{
    ALOGV("writeBytesToI2C\n");

    if (mFactoryManager == NULL)
    {
        return false;
    }

    return mFactoryManager->writeBytesToI2C(u32gID, u8AddrSize, pu8Addr, u16Size, pu8Data);
}


int16_t FactoryManager::getResolutionMappingIndex(int32_t enCurrentInputType)
{
    ALOGV("getResolutionMappingIndex\n");

    if (mFactoryManager == NULL)
    {
        return false;
    }

    return mFactoryManager->getResolutionMappingIndex(enCurrentInputType);
}

bool FactoryManager::setEnvironmentPowerMode(int32_t ePowerMode)
{
    ALOGV("FactoryManager setEnvironmentPowerMode\n");

    if (mFactoryManager == NULL)
    {
        return false;
    }

    return mFactoryManager->setEnvironmentPowerMode(ePowerMode);
}

int32_t FactoryManager::getEnvironmentPowerMode()
{
    ALOGV("FactoryManager getEnvironmentPowerMode\n");

    if (mFactoryManager == NULL)
    {
        return false;
    }

    return mFactoryManager->getEnvironmentPowerMode();
}

bool FactoryManager::setEnvironmentPowerOnMusicVolume(uint8_t volume)
{
    ALOGV("FactoryManager setEnvironmentPowerOnMusicVolume\n");

    if (mFactoryManager == NULL)
    {
        return false;
    }

    return mFactoryManager->setEnvironmentPowerOnMusicVolume(volume);
}

uint8_t FactoryManager::getEnvironmentPowerOnMusicVolume()
{
    ALOGV("FactoryManager getEnvironmentPowerOnMusicVolume\n");

    if (mFactoryManager == NULL)
    {
        return false;
    }

    return mFactoryManager->getEnvironmentPowerOnMusicVolume();
}

bool FactoryManager::getUpdatePQFilePath(int32_t enumpqfile, String8 &filePath)
{
    ALOGV("FactoryManager getUpdatePQFilePath\n");

    if (mFactoryManager == NULL)
    {
        return false;
    }

    return mFactoryManager->getUpdatePQFilePath(enumpqfile, filePath);
}

void FactoryManager::updatePQiniFiles()
{
    ALOGV("FactoryManager updatePQiniFiles\n");

    if (mFactoryManager == NULL)
    {
        return;
    }

    mFactoryManager->updatePQiniFiles();
}

String8 FactoryManager::getPQVersion(int32_t escalerwindow)
{
    ALOGV("FactoryManager getPQVersion\n");

    if (mFactoryManager == NULL)
    {
        String8 str;
        return str;
    }

    return mFactoryManager->getPQVersion(escalerwindow);
}

bool FactoryManager::UrsaGetVersionInfo(Ursa_Version_Info *pVersionInfo)
{
    ALOGV("FactoryManager UrsaGetVersionInfo\n");

    if (mFactoryManager == NULL)
    {
        return false;
    }

    return mFactoryManager->UrsaGetVersionInfo(pVersionInfo);
}

void FactoryManager::setWOLEnableStatus(bool flag)
{
    ALOGV("FactoryManager setWOLEnableStatus\n");

    if (mFactoryManager == NULL)
    {
        return ;
    }

    mFactoryManager->setWOLEnableStatus(flag);
}

bool FactoryManager::getWOLEnableStatus()
{
    ALOGV("FactoryManager getWOLEnableStatus\n");

    if (mFactoryManager == NULL)
    {
        return 0;
    }

    return mFactoryManager->getWOLEnableStatus();
}

status_t FactoryManager::setListener(const sp<FactoryManagerListener>& listener)
{
    ALOGV("setListener\n\n");
    Mutex::Autolock _l(FactoryManager::mLock);
    mListener = listener;
    return NO_ERROR;
}

void FactoryManager::notify(int32_t msgType, int32_t ext1, int32_t ext2)
{
    sp<FactoryManagerListener> listener;
    {
        Mutex::Autolock _l(FactoryManager::mLock);
        listener = mListener;
    }
    if(listener != NULL)
    {
        listener->notify(msgType, ext1, ext2);
    }
}

void FactoryManager::PostEvent_Template(int32_t ext1, int32_t ext2)
{
    sp<FactoryManagerListener> listener;
    {
        Mutex::Autolock _l(FactoryManager::mLock);
        listener = mListener;
    }
    if(listener != NULL)
    {
        listener->PostEvent_Template(ext1, ext2);
    }
}

void FactoryManager::PostEvent_SnServiceDeadth(int32_t ext1, int32_t ext2)
{
    sp<FactoryManagerListener> listener;
    {
        Mutex::Autolock _l(FactoryManager::mLock);
        listener = mListener;
    }
    if(listener != NULL)
    {
        listener->PostEvent_SnServiceDeadth(ext1, ext2);
    }
}


void FactoryManager::binderDied(const wp<IBinder>& who)
{
    ALOGV("ITimer died!\n");
    mFactoryManager.clear();
    PostEvent_SnServiceDeadth(0,0);
}

FactoryManager::DeathNotifier::~DeathNotifier()
{
    ALOGV("DeathNotifier::~DeathNotifier");
    Mutex::Autolock _l(FactoryManager::mLock);
    if(FactoryManager::mFactoryManagerService != NULL)
    {
        FactoryManager::mFactoryManagerService->asBinder()->unlinkToDeath(this);
    }
}

void FactoryManager::DeathNotifier::binderDied(const wp<IBinder>& who)
{
    ALOGV("ITimerService died!\n");
    Mutex::Autolock _l(FactoryManager::mLock);
    FactoryManager::mFactoryManagerService.clear();
}

bool FactoryManager::setXvyccDataFromPanel(float fRedX, float fRedY,
                                            float fGreenX, float fGreenY,
                                            float fBlueX, float fBlueY,
                                            float fWhiteX, float fWhiteY, int32_t eWin)

{
    ALOGV("FactoryManager setXvyccDataFromPanel\n");
    if (mFactoryManager == NULL)
    {
        return false;
    }

    return mFactoryManager->setXvyccDataFromPanel(fRedX, fRedY, fGreenX, fGreenY,
                                            fBlueX, fBlueY, fWhiteX, fWhiteY, eWin);
}

void FactoryManager::getEnableIPInfo(uint8_t *pBitTable, int32_t sBitTableLen)
{
    ALOGV("FactoryManager getEnableIPInfo\n");
    if (mFactoryManager == NULL)
    {
        return;
    }

    mFactoryManager->getEnableIPInfo(pBitTable, sBitTableLen);
}
//------------------------------------------------------------------------------------
uint8_t FactoryManager::getAutoFineGain()
{
    ALOGV("FactoryManager getAutoFineGain\n");
    if (mFactoryManager == NULL)
    {
        return 0;
    }

    return mFactoryManager->getAutoFineGain();
}

bool FactoryManager::setFixedFineGain(uint8_t fineGain)
{
    ALOGV("FactoryManager setFixedFineGain\n");
    if (mFactoryManager == NULL)
    {
        return false;
    }

    return mFactoryManager->setFixedFineGain(fineGain);
}


uint8_t FactoryManager::getAutoRFGain()
{
    ALOGV("FactoryManager getAutoRFGain\n");
    if (mFactoryManager == NULL)
    {
        return 0;
    }

    return mFactoryManager->getAutoRFGain();
}


bool FactoryManager::setRFGain(uint8_t rfGain)
{
    ALOGV("FactoryManager setRFGain\n");
    if (mFactoryManager == NULL)
    {
        return false;
    }

    return mFactoryManager->setRFGain(rfGain);
}

bool FactoryManager::EosSetHDCPKey(const uint8_t *pu8Key, uint32_t u32Key_len, bool bVer2Flag)
{
    ALOGV("FactoryManager EosSetHDCPKey\n");
    if (mFactoryManager == NULL)
    {
        return false;
    }

    return mFactoryManager->EosSetHDCPKey(pu8Key, u32Key_len, bVer2Flag);
}

bool FactoryManager::EosGetHDCPKey(uint8_t *pu8Key, uint32_t u32Key_len, bool bVer2Flag)
{
    ALOGV("FactoryManager EosGetHDCPKey\n");
    if (mFactoryManager == NULL)
    {
        return false;
    }

    return mFactoryManager->EosGetHDCPKey(pu8Key, u32Key_len, bVer2Flag);
}
// EosTek Patch Begin
bool FactoryManager::getTunerStatus()
{
printf("=====jowen====FactoryManager::getTunerStatus()\n");
    ALOGV("FactoryManager getTunerStatus\n");

    if (mFactoryManager == NULL)
    {
        return false;
    }

    return mFactoryManager->getTunerStatus();
}
// EosTek Patch End

//------------------------------------------------------------------------------------

//------------------------------------------------------------------------------------
