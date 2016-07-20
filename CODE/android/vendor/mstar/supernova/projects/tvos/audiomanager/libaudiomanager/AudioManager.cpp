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
#define LOG_TAG "AudioManager"
#include <utils/Log.h>

#include <binder/IServiceManager.h>
#include <binder/IPCThreadState.h>
#include "AudioManager.h"

Mutex AudioManager::mLock;
sp<IAudioManagerService> AudioManager::mAudioManagerService;
sp<AudioManager::DeathNotifier> AudioManager::mDeathNotifier;

const sp<IAudioManagerService>& AudioManager::getAudioManagerService()
{
    ALOGV("getAudioManagerService\n");
    Mutex::Autolock _l(AudioManager::mLock);
    if(AudioManager::mAudioManagerService == NULL)
    {
        sp<IServiceManager> sm = defaultServiceManager();
        sp<IBinder> binder;
        uint32_t retry = 0;

        do
        {
            binder = sm->getService(String16("mstar.AudioManager"));
            if((binder != NULL) || (retry >= 2))
            {
                break;
            }
            ALOGW("AudioManagerService not published, waiting...\n");
            usleep(500000); // 0.5 s
            retry++;
        }
        while(true);

        if(binder != NULL)
        {
            if(AudioManager::mDeathNotifier == NULL)
            {
                AudioManager::mDeathNotifier = new DeathNotifier();
            }
            binder->linkToDeath(AudioManager::mDeathNotifier);
            AudioManager::mAudioManagerService = interface_cast<IAudioManagerService>(binder);
        }
        if(AudioManager::mAudioManagerService == NULL)
            ALOGE("Can't get mstar.AudioManager service!\n");
    }

    return AudioManager::mAudioManagerService;
}

/*add by owen.qin begin*/
void AudioManager::setADEnable(bool enable)
{
	
	printf("AudioManager::setADEnable(bool enable):%d",enable);
	if(mAudioManager == NULL)
    	{
        	return;
    	}
    	mAudioManager->setADEnable(enable);


}
void AudioManager::setADAbsoluteVolume(int32_t value)
{
	
	printf("AudioManager::setADAbsoluteVolume(int32_t value):%d",value);
	if(mAudioManager == NULL)
    	{
        	return;
    	}
    	mAudioManager->setADAbsoluteVolume(value);
}
/*add by owen.qin end*/


/*add by owen.qin begin*/
void AudioManager::setAutoHOHEnable(bool enable)
{
	printf("AudioManager::setAutoHOHEnable(bool eanble): enable=%d\n",enable);
	if(mAudioManager == NULL)
	{
		return ;
	}
	mAudioManager->setAutoHOHEnable(enable);
	
}
/*add by owen.qin end*/

// ---------------------------------------------------------------------------

AudioManager::AudioManager()
{
    ALOGV("constructor\n\n");
}

AudioManager::~AudioManager()
{
    ALOGV("destructor\n\n");
    //disconnect();
    IPCThreadState::self()->flushCommands();
}

sp<AudioManager> AudioManager::connect()
{
    ALOGV("connect\n\n");

    sp<AudioManager> sound = new AudioManager();
    const sp<IAudioManagerService>& service = getAudioManagerService();
    if(service != NULL)
    {
        sound->mAudioManager = service->connect(sound);
    }

    if(sound->mAudioManager != NULL)
    {
        sound->mAudioManager->asBinder()->linkToDeath(sound);
    }
    else
    {
        sound.clear();
    }

    return sound;

}

void AudioManager::disconnect()
{
    ALOGV("disconnect\n\n");
    if(mAudioManager == NULL)
    {
        return;
    }

    mAudioManager->disconnect();
    mAudioManager->asBinder()->unlinkToDeath(this);
    mAudioManager.clear();
}

// ----------------------------------------------------------------------------
// AudioManager
// ----------------------------------------------------------------------------
int32_t AudioManager::checkAtvSoundSystem()
{
    ALOGV("checkAtvSoundSystem\n\n");
    if (mAudioManager == NULL)
    {
        return -1;
    }

    return mAudioManager->checkAtvSoundSystem();
}

void AudioManager::setAudioVolume(int32_t enSoundPath, int8_t volumn)
{
    ALOGV("setAudioVolume\n\n");
    if (mAudioManager == NULL)
    {
        return;
    }

    mAudioManager->setAudioVolume(enSoundPath,volumn);
}

int8_t AudioManager::getAudioVolume(int32_t volSrcType)
{
    ALOGV("getAudioVolume\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->getAudioVolume(volSrcType);
}

int32_t AudioManager::setAtvInfo(int32_t infoType, int32_t config)
{
    ALOGV("setAtvInfo\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->setAtvInfo(infoType, config);
}

int32_t AudioManager::getAtvInfo()
{
    ALOGV("getAtvInfo\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->getAtvInfo();
}

int32_t AudioManager::setAudioOutput(int32_t outputType, AudioOutParameter param)
{
    ALOGV("setAudioOutput\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->setAudioOutput(outputType, param);
}

int32_t AudioManager::enableBasicSoundEffect(int32_t soundType, bool enable)
{
    ALOGV("enableBasicSoundEffect\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->enableBasicSoundEffect(soundType, enable);
}

#if 0
void AudioManager::enableAutoVolume()
{
    ALOGV("enableAutoVolume\n\n");
    if (mAudioManager == NULL)
    {
        return;
    }

    mAudioManager->enableAutoVolume();
}

void AudioManager::disableAutoVolume()
{
    ALOGV("disableAutoVolume\n\n");
    if (mAudioManager == NULL)
    {
        return;
    }

    mAudioManager->disableAutoVolume();
}




bool AudioManager::isAutoVolumeEnabled()
{
    ALOGV("isAutoVolumeEnabled\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->isAutoVolumeEnabled();
}
#endif

bool AudioManager::setMuteStatus(int32_t screenUnMuteTime, int32_t eSrcType)
{
    ALOGV("AudioManager setMuteStatus\n");
    if (mAudioManager == NULL)
    {
        return false;
    }
    return mAudioManager->setMuteStatus(screenUnMuteTime, eSrcType);
}


bool AudioManager::enableMute(int32_t enMuteType)
{
    ALOGV("enableMute\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->enableMute(enMuteType);
}

bool AudioManager::disableMute(int32_t enMuteType)
{
    ALOGV("disableMute\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->disableMute(enMuteType);
}


bool AudioManager::isMuteEnabled(int32_t enMuteType)
{
    ALOGV("isMute\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->isMuteEnabled(enMuteType);
}

int32_t AudioManager::setAtvMtsMode(int32_t mode)
{
    ALOGV("setAtvMtsMode\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->setAtvMtsMode(mode);
}

int32_t AudioManager::getAtvMtsMode()
{
    ALOGV("getAtvMtsMode\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->getAtvMtsMode();
}

int16_t AudioManager::setToNextAtvMtsMode()
{
    ALOGV("setToNextAtvMtsMode\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->setToNextAtvMtsMode();
}

bool AudioManager::setAtvSoundSystem(int32_t mode)
{
    ALOGV("setAtvSoundSystem\n\n");
    if (mAudioManager == NULL)
    {
        return false;
    }

    return mAudioManager->setAtvSoundSystem(mode);
}

int32_t AudioManager::getAtvSoundSystem()
{
    ALOGV("getAtvSoundSystem\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->getAtvSoundSystem();
}

void AudioManager::setDtvOutputMode(int32_t mode)
{
    ALOGV("setDtvOutputMode\n\n");
    if (mAudioManager == NULL)
    {
        return;
    }

    mAudioManager->setDtvOutputMode(mode);
}

int32_t AudioManager::getDtvOutputMode()
{
    ALOGV("getDtvOutputMode\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->getDtvOutputMode();
}

int32_t AudioManager::setBasicSoundEffect(int32_t soundEffectType,DtvSoundEffect &ef)
{
    ALOGV("setBasicSoundEffect\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->setBasicSoundEffect(soundEffectType,ef);
}

int32_t AudioManager::getBasicSoundEffect(int32_t effectType)
{
    ALOGV("getBasicSoundEffect\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->getBasicSoundEffect(effectType);
}

void AudioManager::setInputLevel(int32_t src,int16_t level)
{
    ALOGV("setInputLevel\n\n");
    if (mAudioManager == NULL)
    {
        return;
    }

    mAudioManager->setInputLevel(src,level);
}

int32_t AudioManager::getInputLevel(int32_t enAudioInputSource)
{
    ALOGV("getInputLevel\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->getInputLevel(enAudioInputSource);
}

//void AudioManager::muteInput(int32_t src)
//{
//    ALOGV("muteInput\n\n");
//    if (mAudioManager == NULL)
//    {
//        return;
//    }
//
//    mAudioManager->muteInput(src);
//}



void AudioManager::setDigitalOut(int32_t mode)
{
    ALOGV("setDigitalOut\n\n");
    if (mAudioManager == NULL)
    {
        return;
    }

    mAudioManager->setDigitalOut(mode);
}

void AudioManager::setInputSource(int32_t src)
{
    ALOGV("setInputSource\n\n");
    if (mAudioManager == NULL)
    {
        return;
    }

    mAudioManager->setInputSource(src);
}

int32_t AudioManager::getInputSource()
{
    ALOGV("getInputSource\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->getInputSource();
}

int32_t AudioManager::getAtvSoundMode()
{
    ALOGV("getAtvSoundMode\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->getAtvSoundMode();
}

void AudioManager::setDebugMode(bool mode)
{
    ALOGV("setDebugMode\n\n");
    if (mAudioManager == NULL)
    {
        return;
    }

    mAudioManager->setDebugMode(mode);
}

int8_t AudioManager::exectueAmplifierExtendedCommand(int8_t subCmd, int32_t param1, int32_t param2, void *param3, int32_t param3Size)
{
    ALOGV("exectueAmplifierExtendedCommand\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->exectueAmplifierExtendedCommand(subCmd, param1, param2, param3, param3Size);
}

bool AudioManager::setAmplifierMute(bool bmute)
{
    ALOGV("setAmplifierMute\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->setAmplifierMute(bmute);
}

void AudioManager::setAmplifierEqualizerByMode(int equalizertype)
{
    ALOGV("setAmplifierEqualizerByMode\n\n");
    if (mAudioManager == NULL)
    {
        return;
    }

    mAudioManager->setAmplifierEqualizerByMode(equalizertype);
}

int32_t AudioManager::enableAdvancedSoundEffect(int32_t soundType,int32_t subProcessType)
{
    ALOGV("enableAdvancedSoundEffect\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->enableAdvancedSoundEffect(soundType, subProcessType);
}

int32_t AudioManager::setAdvancedSoundEffect(int32_t advancedSoundParamType, AdvancedSoundParam advancedSoundParameterVo)
{
    ALOGV("setAdvancedSoundEffect\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->setAdvancedSoundEffect(advancedSoundParamType, advancedSoundParameterVo);
}

int32_t AudioManager::getAdvancedSoundEffect(int32_t advancedSoundParamType)
{
    ALOGV("getAdvancedSoundEffect\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->getAdvancedSoundEffect(advancedSoundParamType);
}

int16_t AudioManager::setSubWooferVolume(bool mute, int16_t value)
{
    ALOGV("setSubWooferVolume\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->setSubWooferVolume(mute, value);
}

uint8_t AudioManager::setSoundParameter(const int32_t Type, const int16_t param1, const int16_t param2)
{
    ALOGV("setSoundParameter\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->setSoundParameter(Type, param1, param2);
}

int16_t AudioManager::getSoundParameter(const int32_t Type, const int16_t param1)
{
    ALOGV("getSoundParameter\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->getSoundParameter(Type, param1);
}

uint8_t AudioManager::setSpeakerDelay(uint32_t delay)
{
    ALOGV("setSpeakerDelay\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->setSpeakerDelay(delay);
}

uint8_t AudioManager::setSpdifDelay(uint32_t delay)
{
    ALOGV("setSpdifDelay\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->setSpdifDelay(delay);
}

uint8_t AudioManager::setKtvMixModeVolume(int32_t VolType,  uint8_t u8Vol1, uint8_t u8Vol2)
{
    ALOGV("setKtvMixModeVolume\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->setKtvMixModeVolume(VolType, u8Vol1, u8Vol2);
}

uint8_t AudioManager::enableKtvMixModeMute(int32_t VolType)
{
    ALOGV("enableKtvMixModeMute\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->enableKtvMixModeMute(VolType);
}

uint8_t AudioManager::disableKtvMixModeMute(int32_t VolType)
{
    ALOGV("disableKtvMixModeMute\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->disableKtvMixModeMute(VolType);
}

int16_t AudioManager::setAudioCaptureSource(int32_t audioDeviceType, int32_t audioType)
{
    ALOGV("setAudioCaptureSource\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->setAudioCaptureSource(audioDeviceType, audioType);
}

int16_t AudioManager::setOutputSourceInfo(int32_t eAudioPath,int32_t eSource)
{
	ALOGV("setOutputSourceInfo\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

	return mAudioManager->setOutputSourceInfo(eAudioPath,eSource);
}


int16_t AudioManager::setKtvSoundInfo(int32_t ektvInfoType, int32_t param1, int32_t param2)
{
    ALOGV("setKtvSoundInfo\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->setKtvSoundInfo(ektvInfoType, param1, param2);
}

int32_t AudioManager::getKtvSoundInfo(int32_t ektvInfoType)
{
    ALOGV("getKtvSoundInfo\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->getKtvSoundInfo(ektvInfoType);
}

int32_t AudioManager::setKtvSoundTrack(int32_t enSoundMode)
{
    ALOGV("setKtvSoundTrack\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->setKtvSoundTrack(enSoundMode);
}

bool AudioManager::setCommonAudioInfo(int32_t eInfoType, int32_t param1, int32_t param2)
{
    ALOGV("setCommonAudioInfo\n\n");
    if (mAudioManager == NULL)
    {
        return false;
    }

    return mAudioManager->setCommonAudioInfo(eInfoType, param1, param2);
}

int32_t AudioManager::setAudioSource(int32_t eInputSrc, int32_t eAudioProcessType)
{
    ALOGV("setAudioSource\n\n");
    if (mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->setAudioSource(eInputSrc, eAudioProcessType);
}

int32_t AudioManager::getAudioLanguage1()
{
    ALOGV("getAudioLanguage1\n\n");
    if(mAudioManager == NULL)
    {
        return 0;
    }
    return mAudioManager->getAudioLanguage1();
}

int32_t AudioManager::getAudioLanguage2()
{
    ALOGV("getAudioLanguage2\n\n");
    if(mAudioManager == NULL)
    {
        return 0;
    }
    return mAudioManager->getAudioLanguage2();
}

void AudioManager::setAudioLanguage1(int32_t enLanguage)
{
    ALOGV("setAudioLanguage1\n\n");
    if(mAudioManager == NULL)
    {
        return;
    }
    mAudioManager->setAudioLanguage1(enLanguage);
}

void AudioManager::setAudioLanguage2(int32_t enLanguage)
{
    ALOGV("setAudioLanguage2\n\n");
    if(mAudioManager == NULL)
    {
        return;
    }
    mAudioManager->setAudioLanguage2(enLanguage);
}
void AudioManager::setAutoVolume(bool enable)
{
	ALOGV("AudioManager::setAutoVolume(bool enable):%d",enable);
	if(mAudioManager == NULL)
    	{
        	return;
    	}
    	mAudioManager->setAutoVolume(enable);
}
bool AudioManager::getAutoVolume()
{
	ALOGV("AudioManager::getAutoVolume");
	if(mAudioManager == NULL)
    	{
        	return false;
    	}
    	return mAudioManager->getAutoVolume();
}
//jerry.wang add
uint8_t AudioManager::SetSNDDTSInfo(int32_t infoType,uint32_t param1,uint32_t param2)
{
    ALOGV("SetSNDDTSInfo \n");
    if( mAudioManager == NULL )
    {
        return 0;
    }

    return mAudioManager->SetSNDDTSInfo(infoType,param1,param2);
}

uint32_t AudioManager::GetSNDAACInfo(int32_t infoType)
{
    ALOGV("GetSNDAACINfo \n");
    if( mAudioManager == NULL )
    {
        return 0;
    }

    return mAudioManager->GetSNDAACInfo(infoType);
}

uint8_t AudioManager::SetSNDAACInfo(int32_t infoType,uint32_t param1,uint32_t param2)
{
    ALOGV(" SetSNDAACInfo \n");
    if( mAudioManager == NULL)
    {
        return 0;
    }

    return mAudioManager->SetSNDAACInfo(infoType,param1,param2);
}

uint32_t AudioManager::GetSNDAC3Info(int32_t infoType)
{
    ALOGV(" GetSNDAC3Info \n");
    if( mAudioManager == NULL)
    {
        return 0;
    }

    return  mAudioManager->GetSNDAC3Info(infoType);
}

uint8_t AudioManager::SetSNDAC3Info(int32_t infoType,uint32_t param1,uint32_t param2)
{
    ALOGV(" SetSNDAC3Info \n");
    if( mAudioManager == NULL)
    {
        return 0;
    }

    return  mAudioManager->SetSNDAC3Info(infoType, param1, param2);
}

uint32_t AudioManager::GetSNDAC3PInfo(int32_t infoType)
{
    ALOGV(" GetSNDAC3PInfo \n");
    if( mAudioManager == NULL)
    {
        return 0;
    }

    return  mAudioManager->GetSNDAC3PInfo(infoType);
}

uint8_t AudioManager::SetSNDAC3PInfo(int32_t infoType,uint32_t param1,uint32_t param2)
{
    ALOGV(" SetSNDAC3PInfo \n");
    if( mAudioManager == NULL)
    {
        return 0;
    }

    return  mAudioManager->SetSNDAC3PInfo(infoType, param1, param2);
}

uint32_t AudioManager::GetSNDMpegInfo(int32_t infoType)
{
    ALOGV(" GetSNDAC3Info \n");
    if( mAudioManager == NULL)
    {
        return 0;
    }

    return  mAudioManager->GetSNDMpegInfo(infoType);
}

uint32_t AudioManager::GetMpegFrameCnt(void)
{
    ALOGV(" GetMpegFrameCnt \n");
    if( mAudioManager == NULL)
    {
        return 0;
    }

    return  mAudioManager->GetMpegFrameCnt();

}

uint64_t AudioManager::GetAudioCommInfo(int32_t infoType)
{
    ALOGV("GetAudioCommInfo\n");
    if(mAudioManager == NULL)
    {
        return 0;
    }

    return
        mAudioManager->GetAudioCommInfo(infoType);
}

uint8_t AudioManager::CheckInputRequest(uint32_t *pU32WrtAddr, uint32_t *pU32WrtBytes,uint8_t bCheck)
{
    ALOGV(" CheckInputRequest \n");
    if( mAudioManager == NULL || pU32WrtAddr == NULL || pU32WrtBytes == NULL )
    {
        return 0;
    }

    return mAudioManager->CheckInputRequest(pU32WrtAddr,pU32WrtBytes,bCheck);
}

void AudioManager::SetInput(void)
{
    ALOGV("SetInput \n");
    if(mAudioManager == NULL)
        return;
    mAudioManager->SetInput();
}

void AudioManager::StartDecode(void)
{
    ALOGV(" StartDecode \n");
    if(mAudioManager == NULL)
        return;

    mAudioManager->StartDecode();
}

void AudioManager::StopDecode(void)
{
    ALOGV(" StopDecode \n");
    if(mAudioManager == NULL)
        return;

    mAudioManager->StopDecode();

}

void AudioManager::SwitchAudioDSPSystem(int32_t eAudioDSPSystem)
{
    ALOGV(" StopDecode \n");
    if(mAudioManager == NULL)
        return;

    mAudioManager->SwitchAudioDSPSystem(eAudioDSPSystem);

}

void AudioManager::setAudioSpidifOutPut(int32_t eSpidif_output)
{
    ALOGV(" setAudioSpidifOutPut \n");
    if(mAudioManager == NULL)
        return;

    mAudioManager->setAudioSpidifOutPut(eSpidif_output);

}

void AudioManager::setAudioHDMIOutPut(int32_t eHdmi_putput)
{
    ALOGV(" setAudioHDMIOutPut \n");
    if(mAudioManager == NULL)
        return;

    mAudioManager->setAudioHDMIOutPut(eHdmi_putput);

}

void AudioManager::setAudioHDMITx_HDBypass(bool bByPassOnOff)
{
    ALOGV(" setAudioHDMITx_HDBypass \n");
    if(mAudioManager == NULL)
        return;
    mAudioManager->setAudioHDMITx_HDBypass(bByPassOnOff);
}

bool AudioManager::getAudioHDMITx_HDBypass()
{
    ALOGV(" getAudioHDMITx_HDBypass \n");
    if(mAudioManager == NULL)
        return false;

    return mAudioManager->getAudioHDMITx_HDBypass();
}

bool AudioManager::getAudioHDMITx_HDBypass_Capability()
{
    ALOGV(" getAudioHDMITx_HDBypass_Capability \n");
    if(mAudioManager == NULL)
        return false;
    return mAudioManager->getAudioHDMITx_HDBypass_Capability();
}
// EosTek Patch Begin
void AudioManager::setMicSSound(int8_t value)
{
    ALOGV("setMicSSound\n\n");
    if (mAudioManager == NULL)
    {
        return;
    }
    mAudioManager->setMicSSound(value);
}

void AudioManager::setMicEcho(int8_t value)
{
    ALOGV("setMicEcho\n\n");
    if (mAudioManager == NULL)
    {
        return ;
    }
    mAudioManager->setMicEcho(value);
}
uint8_t AudioManager::getMicSSound()
{
    ALOGV("getMicSSound\n\n");
    if (mAudioManager == NULL)
    {
        return false;
    }
    return mAudioManager->getMicSSound();
}

uint8_t AudioManager::getMicEcho()
{
    ALOGV("getMicEcho\n\n");
    if (mAudioManager == NULL)
    {
        return false ;
    }
    return mAudioManager->getMicEcho();
}
// EosTek Patch End

status_t AudioManager::setListener(const sp<AudioManagerListener>& listener)
{
    ALOGV("setListener\n\n");
    Mutex::Autolock _l(AudioManager::mLock);
    mListener = listener;
    return NO_ERROR;
}

void AudioManager::PostEvent_Template(int32_t nEvt, int32_t ext1, int32_t ext2)
{
    sp<AudioManagerListener> listener;
    {
        Mutex::Autolock _l(AudioManager::mLock);
        listener = mListener;
    }
    if(listener != NULL)
    {
        listener->PostEvent_Template(nEvt, ext1, ext2);
    }
}

void AudioManager::PostEvent_ApSetVolume(int32_t nEvt, int32_t ext1, int32_t ext2)
{
    sp<AudioManagerListener> listener;
    {
        Mutex::Autolock _l(AudioManager::mLock);
        listener = mListener;
    }
    if(listener != NULL)
    {
        listener->PostEvent_ApSetVolume(nEvt, ext1, ext2);
    }
}
void AudioManager::PostEvent_SnServiceDeadth(int32_t nEvt, int32_t ext1, int32_t ext2)
{
    sp<AudioManagerListener> listener;
    {
        Mutex::Autolock _l(AudioManager::mLock);
        listener = mListener;
    }
    if(listener != NULL)
    {
        listener->PostEvent_SnServiceDeadth(nEvt, ext1, ext2);
    }
}





void AudioManager::binderDied(const wp<IBinder>& who)
{
    ALOGV("IAudioManager died!\n");
    mAudioManager.clear();
    PostEvent_SnServiceDeadth(0,0,0);
}

AudioManager::DeathNotifier::~DeathNotifier()
{
    ALOGV("DeathNotifier::~DeathNotifier");
    Mutex::Autolock _l(AudioManager::mLock);
    if(AudioManager::mAudioManagerService != NULL)
    {
        AudioManager::mAudioManagerService->asBinder()->unlinkToDeath(this);
    }
}

void AudioManager::DeathNotifier::binderDied(const wp<IBinder>& who)
{
    ALOGV("IAudioManagerService died!\n");
    Mutex::Autolock _l(AudioManager::mLock);
    AudioManager::mAudioManagerService.clear();
}

