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
#define LOG_TAG "AudioManagerService"
#include <utils/Log.h>

#include <sys/shm.h>
#include <signal.h>
#include <binder/IServiceManager.h>
#include <binder/IPCThreadState.h>
#include "AudioManagerService.h"
#include "MSrv_Control.h"
#include "MSrv_SSSound.h"
#include "MsOS.h"
#include "apiAUDIO.h"
#include "MEvent.h"

#if(ATSC_CC_ENABLE == 1)
#include "MW_CC.h"
#include "MSrv_System_Database_ATSC.h"
#endif

#if(RELEASE_BINDER_TEST == 1)
#include "AudioManagerTest.h"
#endif

#define __GETCLIENTINIT(clients, x)        \
sp<Client> x;                   \
for(int kk=0; kk<(int)(clients.size()); kk++)    \
{\
    x = clients[kk].promote(); \
    if (x == NULL)  {   \
       ALOGV("audiomanager  client is null !!!!!\n");  \
       continue; \
    }


#define __GETCLIENTEND(client, remove_list)                                  \
}                                                         \
for (unsigned int jj=0;jj < remove_list.size();jj++) { \
    client.remove(remove_list[jj]);        \
}                                                         \
remove_list.clear();


//extern Mutex m_AudioFuncLock;
Mutex AudioManagerService::Client::m_AudioFuncLock; //use local lock due to the performance issue while volume adjustment
MSrv_SSSound *AudioManagerService::m_MSrvSSSound = NULL;

AudioManagerService* AudioManagerService::instantiate()
{
    AudioManagerService* comm = new AudioManagerService();
    ALOGV("AudioManagerService instantiate\n");
    defaultServiceManager()->addService(String16("mstar.AudioManager"), comm);
    m_MSrvSSSound = MSrv_Control::GetMSrvSSSound();

    return (comm);
}

// ----------------------------------------------------------------------------

AudioManagerService::AudioManagerService()
    : m_Users(0)
{
    ALOGV("AudioManagerService created\n");
}

AudioManagerService::~AudioManagerService()
{
    ALOGV("AudioManagerService destroyed\n");
}

sp<IAudioManager> AudioManagerService::connect(const sp<IAudioManagerClient>& client)
{
    TVOS_API_ESTIMATE_START()
    pid_t callingPid = IPCThreadState::self()->getCallingPid();
    ALOGV("AudioManagerService::connect(callingPid %d, client %p)\n", callingPid, client->asBinder().get());

    sp<Client> sp_client = new Client(this, client, callingPid);
    wp<Client> wp_client = sp_client;
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_Lock);
    TVOS_API_ESTIMATE_END(LOCK)
    m_Clients.add(wp_client);
    TVOS_API_ESTIMATE_END()
    return sp_client;
}

void AudioManagerService::removeClient(wp<Client> client)
{
    ALOGV("removeClient(callingPid %d)\n", IPCThreadState::self()->getCallingPid());

    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_Lock);
    TVOS_API_ESTIMATE_END(LOCK)
    if (m_bIsPosting == false)
    {    
        m_Clients.remove(client);
    }
    else
    {
        m_vWaitingRemoveClients.add(client);
    }
    TVOS_API_ESTIMATE_END()
}

int32_t AudioManagerService::incUsers()
{
    TVOS_API_ESTIMATE_START()
    int32_t ret = android_atomic_inc(&m_Users);
    TVOS_API_ESTIMATE_END()
    return ret;
}

void AudioManagerService::decUsers()
{
    TVOS_API_ESTIMATE_START()
    android_atomic_dec(&m_Users);
    TVOS_API_ESTIMATE_END()
}

bool AudioManagerService::PostEventToClient(U32 nEvt, U32 wParam, U32 lParam)
{
    TVOS_API_ESTIMATE_START()
    bool bEvtHandled = false;
    switch (nEvt)
    {
        case EV_AP_SETVOLUME_EVENT:
        {
            __GETCLIENTINIT(m_Clients, mCurrentClient)
            mCurrentClient->m_AudioManagerClient->PostEvent_ApSetVolume(nEvt, wParam, lParam);
            bEvtHandled = true;
            __GETCLIENTEND(m_Clients, m_vWaitingRemoveClients)
        }
        break;

        default:
        {
            __GETCLIENTINIT(m_Clients, mCurrentClient)
            mCurrentClient->m_AudioManagerClient->PostEvent_Template(nEvt, wParam, lParam);
            bEvtHandled = true;
            __GETCLIENTEND(m_Clients, m_vWaitingRemoveClients)
        }
    }
    TVOS_API_ESTIMATE_END()
    return bEvtHandled;

}

bool AudioManagerService::PostEvent(uint32_t nEvt, uint32_t wParam, uint32_t lParam, bool synchronous)
{
    TVOS_API_ESTIMATE_START()
    int flag = EVENT_FLAG_NONE;
    U32 toggleevt = NO_TOGGLE_EVT;
    PostEventToEM(nEvt, wParam, lParam,flag,toggleevt,synchronous);
    TVOS_API_ESTIMATE_END()
    return false;
}

AudioManagerService::Client::Client(const sp<AudioManagerService>& service,
                                    const sp<IAudioManagerClient>& client,
                                    pid_t clientPid)
    : m_AudioManagerService(service), m_AudioManagerClient(client), m_ClientPid(clientPid)
{
    TVOS_API_ESTIMATE_START()
    ALOGV("AudioManagerService::Client constructor(callingPid %d)\n", clientPid);
    m_ConnId = m_AudioManagerService->incUsers();
    m_bEnableDebug = false;
    TVOS_API_ESTIMATE_END()
}

AudioManagerService::Client::~Client()
{
    TVOS_API_ESTIMATE_START()
    ALOGV("AudioManagerService::Client destructor(callingPid %d)\n", m_ClientPid);
    disconnect();
    TVOS_API_ESTIMATE_END()
}

void AudioManagerService::Client::disconnect()
{
    TVOS_API_ESTIMATE_START()
    ALOGV("disconnect(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    wp<Client> client(this);
    m_AudioManagerService->removeClient(client);
    m_AudioManagerService->decUsers();
    m_AudioManagerClient.clear();
    TVOS_API_ESTIMATE_END()
}

// ----------------------------------------------------------------------------
// AudioManager
// ----------------------------------------------------------------------------

void AudioManagerService::Client::setAudioVolume(int32_t enSoundPath, int8_t volumn)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_SETAUDIOVOLUME(enSoundPath,volumn) ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setAudioVolume(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    m_MSrvSSSound->SetAudioVolume((AUDIO_VOL_SOURCE_TYPE)enSoundPath, volumn);
    TVOS_API_ESTIMATE_END()
}

int8_t AudioManagerService::Client::getAudioVolume(int32_t volSrcType)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_GETAUDIOVOLUME(volSrcType);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("getAudioVolume(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    int8_t ret = m_MSrvSSSound->GetAudioVolume((AUDIO_VOL_SOURCE_TYPE)volSrcType);
    TVOS_API_ESTIMATE_END()
    return ret;
}

int32_t AudioManagerService::Client::setAtvInfo(int32_t infoType, int32_t config)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_SETATVINFO(infoType,config);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setAtvInfo(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    
    int32_t ret = m_MSrvSSSound->SetATVInfo((ATV_INFO_TYPE)infoType, (ATV_INFO_MODE)config);

    TVOS_API_ESTIMATE_END()
    
    return ret;
}

int32_t AudioManagerService::Client::getAtvInfo()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_GETATVINFO();
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("getAtvInfo(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    int32_t ret = m_MSrvSSSound->GetATVInfo((ATV_INFO_TYPE)0);
    TVOS_API_ESTIMATE_END()
    return ret;
}

int32_t AudioManagerService::Client::setAudioOutput(int32_t outputType, AudioOutParameter param)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_SETAUDIOOUTPUT(outputType,param);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setAudioOutput(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    AUDIO_OUT_PARAMETER audout_param;
    audout_param.SPEAKER_DELAY_TIME = param.speakerDelayTime;
    audout_param.SPDIF_DELAY_TIME = param.spdifDelayTime;
    audout_param.SPDIF_OUTMOD_IN_UI = (SPDIF_TYPE_)param.spdifOutmodInUi;
    audout_param.SPDIF_OUTMOD_ACTIVE = (SPDIF_TYPE_)param.spdifOutmodActive;

    int32_t ret = m_MSrvSSSound->SetAudioOutput((AUDIO_OUT_TYPE)outputType, &audout_param);

    TVOS_API_ESTIMATE_END()
    return ret;
}

int32_t AudioManagerService::Client::checkAtvSoundSystem()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_CHECKATVSOUNDSYSTEM();
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("checkAtvSoundSystem(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    int32_t ret = m_MSrvSSSound->CheckATVSoundSystem();
    TVOS_API_ESTIMATE_END()
    return ret;
}

int32_t AudioManagerService::Client::enableBasicSoundEffect(int32_t soundType, bool enable)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_ENABLEBASICSOUNDEFFECT(soundType,enable);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("enableBasicSoundEffect(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    int32_t ret = m_MSrvSSSound->EnableBasicSoundEffect((BSOUND_EFFECT_TYPE)soundType, enable);
    TVOS_API_ESTIMATE_END()
    return ret;
}

#if 0
void AudioManagerService::Client::enableAutoVolume()
{

    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("enableAutoVolume(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    //m_MSrvSSSound->EnableAutoVolume();
    TVOS_API_ESTIMATE_END()
}

void AudioManagerService::Client::disableAutoVolume()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("disableAutoVolume(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
//    m_MSrvSSSound->DisableAutoVolume();
    TVOS_API_ESTIMATE_END()
}

bool AudioManagerService::Client::isAutoVolumeEnabled()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("isAutoVolumeEnabled(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    TVOS_API_ESTIMATE_END()
    return true;
    //    return m_MSrvSSSound->IsAutoVolumeEnabled();
}
#endif

bool AudioManagerService::Client::enableMute(int32_t enMuteType)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_ENABLEMUTE(enMuteType);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("enableMute(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
#if (STR_ENABLE == 1)
    MSrv_Control::GetInstance()->SetAudioMuteFlag(enMuteType,TRUE);
#endif
    bool bRev = m_MSrvSSSound->SetMute((SSSOUND_MUTE_TYPE)enMuteType,1);

#if (ATSC_CC_ENABLE == 1)
    ST_CC_SETTING stCCSetting;
    dynamic_cast<MSrv_System_Database_ATSC *>(MSrv_Control::GetMSrvSystemDatabase())->GetCcSetting(&stCCSetting);
    //CC is ON || CC is ON_MUTE
    if((stCCSetting.OnOffMode==0)||(stCCSetting.OnOffMode==2))
    {
        MW_CC::GetInstance()->StartCaption();
    }
#endif
    TVOS_API_ESTIMATE_END()
    return bRev;
}

bool AudioManagerService::Client::disableMute(int32_t enMuteType)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_DISABLEMUTE(enMuteType);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("disableMute(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
#if (STR_ENABLE == 1)
    MSrv_Control::GetInstance()->SetAudioMuteFlag(enMuteType,FALSE);
#endif
#if (ATSC_CC_ENABLE == 1)
    ST_CC_SETTING stCCSetting;
    dynamic_cast<MSrv_System_Database_ATSC *>(MSrv_Control::GetMSrvSystemDatabase())->GetCcSetting(&stCCSetting);
    //CC is OFF || CC is ON_MUTE
    if(stCCSetting.OnOffMode==1||(stCCSetting.OnOffMode==2))
    {
        MW_CC::GetInstance()->StopCaption();
    }
#endif
    bool ret = false;

    if( m_MSrvSSSound->SetMute((SSSOUND_MUTE_TYPE)enMuteType,0) == SSSOUND_OK)
    {
        ret = true;
    }
    else
    {
        ret = false;
    }
    TVOS_API_ESTIMATE_END()
    return ret;
}

/*add by owen.qin begin*/
void AudioManagerService::Client::setADEnable(bool enable)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    printf("AudioManagerService::Client::setADEnable(bool eanble)\n");
    m_MSrvSSSound->SetADEnable(enable);
    TVOS_API_ESTIMATE_END()

}
void AudioManagerService::Client::setADAbsoluteVolume(int32_t value)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    printf("AudioManagerService::Client::setADAbsoluteVolume(int32_t value)\n");
    //todo
    m_MSrvSSSound->SetADVolume(value);
    TVOS_API_ESTIMATE_END()

}
/*add by owen.qin end*/

/*add by owen.qin begin*/
void AudioManagerService::Client::setAutoHOHEnable(bool enable)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    printf("AudioManagerService::Client::setAutoHOHEnable(bool enable) enable=%d\n",enable);
    m_MSrvSSSound->SetAutoHOHEnable(enable);
    TVOS_API_ESTIMATE_END()

}
/*add by owen.qin end*/

bool AudioManagerService::Client::isMuteEnabled(int32_t enMuteType)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_ISMUTEENABLED(enMuteType);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    bool ret = m_MSrvSSSound->GetMute((MAPI_SOUND_MUTE_STATUS_TYPE )enMuteType);
    TVOS_API_ESTIMATE_END()
    return ret;
}

int32_t AudioManagerService::Client::setAtvMtsMode(int32_t mode)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_SETATVMTSMODE(mode);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setAtvMtsMode(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    int32_t ret = m_MSrvSSSound->SetATVMtsMode(mode);
    TVOS_API_ESTIMATE_END()
    return ret;
}

int32_t AudioManagerService::Client::getAtvMtsMode()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_GETATVMTSMODE();
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("getAtvMtsMode(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    int32_t ret = m_MSrvSSSound->GetATVMtsMode();
    TVOS_API_ESTIMATE_END()
    return ret;
}

bool AudioManagerService::Client::setMuteStatus(int32_t screenUnMuteTime, int32_t eSrcType)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_SETMUTESTATUS(screenUnMuteTime,eSrcType);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    printf("AudioManagerService::Client::setMuteStatus\n");
    bool ret = MSrv_Control::GetInstance()->SetAudioMute(screenUnMuteTime,(MAPI_INPUT_SOURCE_TYPE)eSrcType);
    TVOS_API_ESTIMATE_END()
    return ret;
}


int16_t AudioManagerService::Client::setToNextAtvMtsMode()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_SETTONEXTATVMTSMODE();
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setToNextAtvMtsMode(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    int16_t ret = m_MSrvSSSound->SetToNextATVMtsMode();
    TVOS_API_ESTIMATE_END()
    return ret;
}

bool AudioManagerService::Client::setAtvSoundSystem(int32_t mode)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_SETATVSOUNDSYSTEM(mode);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setAtvSoundSystem(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    MSRV_SSSND_RET ret = m_MSrvSSSound->SetATVSoundSystem((ATV_SYSTEM_STANDARDS)mode);

    if (ret == RETURN_OK)
    {
        TVOS_API_ESTIMATE_END()
        return true;
    }
    else
    {
        TVOS_API_ESTIMATE_END()
        return false;
    }
}

int32_t AudioManagerService::Client::getAtvSoundSystem()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_GETATVSOUNDSYSTEM();
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("getAtvSoundSystem(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    int32_t ret = m_MSrvSSSound->GetATVSoundSystem();
    TVOS_API_ESTIMATE_END()
    return ret;
}

void AudioManagerService::Client::setDtvOutputMode(int32_t mode)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_SETDTVOUTPUTMODE(mode);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setDtvOutputMode(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    m_MSrvSSSound->DECODER_SetOutputMode((En_DVB_soundModeType_)mode);
    TVOS_API_ESTIMATE_END()
}

int32_t AudioManagerService::Client::getDtvOutputMode()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_GETDTVOUTPUTMODE();
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("getDtvOutputMode(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    int32_t ret = (int32_t)m_MSrvSSSound->GetDTVInfoDMPInfo_DECODER_SoundMode();
    TVOS_API_ESTIMATE_END()
    return ret;
}



int32_t AudioManagerService::Client::setBasicSoundEffect(int32_t soundEffectType,DtvSoundEffect &ef)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_SETBASICSOUNDEFFECT(soundEffectType,ef);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setBasicSoundEffect(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    BSND_PARAMETER param={0};

    param.BSND_PARAM_PRESCALE = ef.preScale;
    param.BSND_PARAM_TREBLE = ef.treble;
    param.BSND_PARAM_BASS = ef.bass;
    param.BSND_PARAM_TYPE_BALANCE = ef.balance;
    param.BSND_PARAM_EQ_BAND_NUM = ef.eqBandNumber;
    param.BSND_PARAM_PEQ_BAND_NUM = ef.peqBandNumber;
    param.BSND_PARAM_AVC_THRESHOLD = ef.avcThreshold;
    param.BSND_PARAM_AVC_AT = ef.avcAttachTime;
    param.BSND_PARAM_AVC_RT = ef.avcReleaseTime;
    param.BSND_PARAM_MSURR_XA = ef.surroundXaValue;
    param.BSND_PARAM_MSURR_XB = ef.surroundXbValue;
    param.BSND_PARAM_MSURR_XK = ef.surroundXkValue;
    param.BSND_PARAM_DRC_THRESHOLD = ef.soundDrcThreshold;
    param.BSND_PARAM_NR_THRESHOLD = ef.noiseReductionThreshold;
    param.BSND_PARAM_ECHO_TIME = ef.echoTime;

    int i;
    for(i =0; i < MAXEQNAD; i ++)
    {
        param.BSND_PARAM_EQ[i].BSND_PARAM_EQ_LEVEL = ef.soundParameterEqs[i].eqLevel;
    }

    for(i =0; i < MAXPEQNAD; i ++)
    {
        param.BSND_PARAM_PEQ[i].BSND_PARAM_PEQ_GAIN   = ef.soundParameterPeqs[i].peqGain;
        param.BSND_PARAM_PEQ[i].BSND_PARAM_PEQ_FC     = ef.soundParameterPeqs[i].peqGc;
        param.BSND_PARAM_PEQ[i].BSND_PARAM_PEQ_QVALUE = ef.soundParameterPeqs[i].peqQvalue;
    }

    int32_t ret = (int32_t )m_MSrvSSSound->SetBasicSoundEffect((BSOUND_EFFECT_TYPE)soundEffectType,&param);
    TVOS_API_ESTIMATE_END()
    return ret;
}

int32_t AudioManagerService::Client::getBasicSoundEffect(int32_t effectType)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_GETBASICSOUNDEFFECT(effectType);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("getBasicSoundEffect(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    int32_t ret = (int32_t )m_MSrvSSSound->GetBasicSoundEffect((BSND_GET_PARAMETER_TYPE)effectType);
    TVOS_API_ESTIMATE_END()
    return ret;
}

void AudioManagerService::Client::setInputLevel(int32_t src,int16_t level)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_SETINPUTLEVEL(src,level);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setInputLevel(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    m_MSrvSSSound->SetAudioInputLevel((AUDIO_INPUT_LEVEL_SOURCE_TYPE)src, level);
    TVOS_API_ESTIMATE_END()
}

int32_t AudioManagerService::Client::getInputLevel(int32_t enAudioInputSource)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_GETINPUTLEVEL(enAudioInputSource);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("getInputLevel(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    int32_t ret = m_MSrvSSSound->GetAudioInputLevel((AUDIO_INPUT_LEVEL_SOURCE_TYPE)enAudioInputSource);
    TVOS_API_ESTIMATE_END()
    return ret;
}

//void AudioManagerService::Client::muteInput(int32_t src)
//{
//    Mutex::Autolock lock(m_AudioFuncLock);
//    ALOGV("muteInput(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
//    m_MSrvSSSound->MuteInput(src);
//}



void AudioManagerService::Client::setDigitalOut(int32_t mode)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_SETDIGITALOUT(mode);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setDigitalOut(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    m_MSrvSSSound->SetSPDIFmode((SPDIF_TYPE_)mode);
    TVOS_API_ESTIMATE_END()
}

void AudioManagerService::Client::setInputSource(int32_t src)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_SETINPUTSOURCE(src);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setInputSource(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    m_MSrvSSSound->SetInputSource((MAPI_INPUT_SOURCE_TYPE)src);
    TVOS_API_ESTIMATE_END()
}

int32_t AudioManagerService::Client::getInputSource()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_GETINPUTSOURCE();
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("getInputSource(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    int32_t ret = m_MSrvSSSound->GetInputSource();
    TVOS_API_ESTIMATE_END()
    return ret;
}

int32_t AudioManagerService::Client::getAtvSoundMode()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_GETATVSOUNDMODE();
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("getAtvSoundMode(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    int32_t ret = m_MSrvSSSound->GetATVSoundMode();
    TVOS_API_ESTIMATE_END()
    return ret;
}

int32_t AudioManagerService::Client::enableAdvancedSoundEffect(int32_t soundType,int32_t subProcessType)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_INT_INT2("enableAdvancedSoundEffect","soundType",soundType,"subProcessType",subProcessType);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("enableAdvancedSoundEffect(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    int32_t ret = m_MSrvSSSound->EnableAdvancedSoundEffect((ADVANCESND_TYPE)soundType, (ADVSND_SUBPROC)subProcessType);
    TVOS_API_ESTIMATE_END()
    return ret;
}

int32_t AudioManagerService::Client::setAdvancedSoundEffect(int32_t advancedSoundParamType, AdvancedSoundParam advancedSoundParameterVo)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_SETADVANCEDSOUNDEFFECT();
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setAdvancedSoundEffect(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    ST_ADVSND_PARAMETER param={0};
    param.PARAM_DOLBY_PL2VDPK_SMOD = advancedSoundParameterVo.paramDolbyPl2vdpkSmod;
    param.PARAM_DOLBY_PL2VDPK_WMOD = advancedSoundParameterVo.paramDolbyPl2vdpkWmod ;
    param.PARAM_SRS_TSXT_SET_INPUT_GAIN = advancedSoundParameterVo.paramSrsTsxtSetInputGain ;
    param.PARAM_SRS_TSXT_SET_DC_GAIN = advancedSoundParameterVo.paramSrsTsxtSetDcGain ;
    param.PARAM_SRS_TSXT_SET_TRUBASS_GAIN = advancedSoundParameterVo.paramSrsTsxtSetTrubassGain ;
    param.PARAM_SRS_TSXT_SET_SPEAKERSIZE = advancedSoundParameterVo.paramSrsTsxtSetSpeakerSize ;
    param.PARAM_SRS_TSXT_SET_INPUT_MODE = advancedSoundParameterVo.paramSrsTsxtSetInputMode ;
    param.PARAM_SRS_TSXT_SET_OUTPUT_GAIN = advancedSoundParameterVo.paramSrsTsxtSetOutputGain ;

    param.PARAM_SRS_TSHD_SET_INPUT_MODE = advancedSoundParameterVo.paramSrsTshdSetInputMode ;
    param.PARAM_SRS_TSHD_SET_OUTPUT_MODE = advancedSoundParameterVo.paramSrsTshdSetOutputMode ;
    param.PARAM_SRS_TSHD_SET_SPEAKERSIZE = advancedSoundParameterVo.paramSrsTshdSetSpeakerSize ;
    param.PARAM_SRS_TSHD_SET_TRUBASS_CONTROL = advancedSoundParameterVo.paramSrsTshdSetTrubassControl ;
    param.PARAM_SRS_TSHD_SET_DEFINITION_CONTROL = advancedSoundParameterVo.paramSrsTshdSetDefinitionControl ;
    param.PARAM_SRS_TSHD_SET_DC_CONTROL = advancedSoundParameterVo.paramSrsTshdSetDcControl ;
    param.PARAM_SRS_TSHD_SET_SURROUND_LEVEL = advancedSoundParameterVo.paramSrsTshdSetSurroundLevel ;
    param.PARAM_SRS_TSHD_SET_INPUT_GAIN = advancedSoundParameterVo.paramSrsTshdSetInputGain ;
    param.PARAM_SRS_TSHD_SET_WOWSPACE_CONTROL = advancedSoundParameterVo.paramSrsTshdSetWowSpaceControl ;
    param.PARAM_SRS_TSHD_SET_WOWCENTER_CONTROL = advancedSoundParameterVo.paramSrsTshdSetWowCenterControl ;
    param.PARAM_SRS_TSHD_SET_WOWHDSRS3DMODE = advancedSoundParameterVo.paramSrsTshdSetWowHdSrs3dMode ;
    param.PARAM_SRS_TSHD_SET_LIMITERCONTROL = advancedSoundParameterVo.paramSrsTshdSetLimiterControl ;
    param.PARAM_SRS_TSHD_SET_OUTPUT_GAIN = advancedSoundParameterVo.paramSrsTshdSetOutputGain ;

    param.PARAM_SRS_THEATERSOUND_INPUT_GAIN = advancedSoundParameterVo.paramSrsTheaterSoundInputGain ;
    param.PARAM_SRS_THEATERSOUND_DEFINITION_CONTROL = advancedSoundParameterVo.paramSrsTheaterSoundDefinitionControl ;
    param.PARAM_SRS_THEATERSOUND_DC_CONTROL = advancedSoundParameterVo.paramSrsTheaterSoundDcControl ;
    param.PARAM_SRS_THEATERSOUND_TRUBASS_CONTROL = advancedSoundParameterVo.paramSrsTheaterSoundTrubassControl ;
    param.PARAM_SRS_THEATERSOUND_SPEAKERSIZE = advancedSoundParameterVo.paramSrsTheaterSoundSpeakerSize ;
    param.PARAM_SRS_THEATERSOUND_HARDLIMITER_LEVEL = advancedSoundParameterVo.paramSrsTheaterSoundHardLimiterLevel ;
    param.PARAM_SRS_THEATERSOUND_HARDLIMITER_BOOST_GAIN = advancedSoundParameterVo.paramSrsTheaterSoundHardLimiterBoostGain ;
    param.PARAM_SRS_THEATERSOUND_HEADROOM_GAIN = advancedSoundParameterVo.paramSrsTheaterSoundHeadRoomGain ;
    param.PARAM_SRS_THEATERSOUND_TRUVOLUME_MODE = advancedSoundParameterVo.paramSrsTheaterSoundTruVolumeMode ;
    param.PARAM_SRS_THEATERSOUND_TRUVOLUME_REF_LEVEL = advancedSoundParameterVo.paramSrsTheaterSoundTruVolumeRefLevel ;
    param.PARAM_SRS_THEATERSOUND_TRUVOLUME_MAX_GAIN = advancedSoundParameterVo.paramSrsTheaterSoundTruVolumeMaxGain ;
    param.PARAM_SRS_THEATERSOUND_TRUVOLUME_NOISE_MNGR_THLD = advancedSoundParameterVo.paramSrsTheaterSoundTruVolumeNoiseMngrThld ;
    param.PARAM_SRS_THEATERSOUND_TRUVOLUME_CALIBRATE = advancedSoundParameterVo.paramSrsTheaterSoundTruVolumeCalibrate ;
    param.PARAM_SRS_THEATERSOUND_TRUVOLUME_INPUT_GAIN = advancedSoundParameterVo.paramSrsTheaterSoundTruVolumeInputGain ;
    param.PARAM_SRS_THEATERSOUND_TRUVOLUME_OUTPUT_GAIN = advancedSoundParameterVo.paramSrsTheaterSoundTruVolumeOutputGain ;
    param.PARAM_SRS_THEATERSOUND_HPF_FC = advancedSoundParameterVo.paramSrsTheaterSoundHpfFc ;

    param.PARAM_DTS_ULTRATV_EVO_MONOINPUT = advancedSoundParameterVo.paramDtsUltraTvEvoMonoInput ;
    param.PARAM_DTS_ULTRATV_EVO_WIDENINGON = advancedSoundParameterVo.paramDtsUltraTvEvoWideningon ;
    param.PARAM_DTS_ULTRATV_EVO_ADD3DBON = advancedSoundParameterVo.paramDtsUltraTvEvoAdd3dBon ;
    param.PARAM_DTS_ULTRATV_EVO_PCELEVEL = advancedSoundParameterVo.paramDtsUltraTvEvoPceLevel ;
    param.PARAM_DTS_ULTRATV_EVO_VLFELEVEL = advancedSoundParameterVo.paramDtsUltraTvEvoVlfeLevel ;
    param.PARAM_DTS_ULTRATV_SYM_DEFAULT = advancedSoundParameterVo.paramDtsUltraTvSymDefault ;
    param.PARAM_DTS_ULTRATV_SYM_MODE = advancedSoundParameterVo.paramDtsUltraTvSymMode ;
    param.PARAM_DTS_ULTRATV_SYM_LEVEL = advancedSoundParameterVo.paramDtsUltraTvSymLevel ;
    param.PARAM_DTS_ULTRATV_SYM_RESET = advancedSoundParameterVo.paramDtsUltraTvSymReset ;

    param.PARAM_AUDYSSEY_DYNAMICVOL_COMPRESS_MODE = advancedSoundParameterVo.paramAudysseyDynamicVolCompressMode ;
    param.PARAM_AUDYSSEY_DYNAMICVOL_GC = advancedSoundParameterVo.paramAudysseyDynamicVolGc ;
    param.PARAM_AUDYSSEY_DYNAMICVOL_VOLSETTING = advancedSoundParameterVo.paramAudysseyDynamicVolVolSetting ;
    param.PARAM_AUDYSSEY_DYNAMICEQ_EQOFFSET = advancedSoundParameterVo.paramAudysseyDynamicEqEqOffset ;
    param.PARAM_AUDYSSEY_ABX_GWET = advancedSoundParameterVo.paramAudysseyAbxGwet ;
    param.PARAM_AUDYSSEY_ABX_GDRY = advancedSoundParameterVo.paramAudysseyAbxGdry ;
    param.PARAM_AUDYSSEY_ABX_FILSET = advancedSoundParameterVo.paramAudysseyAbxFilset ;

    param.PARAM_SRS_THEATERSOUND_TSHD_INPUT_GAIN = advancedSoundParameterVo.paramSrsTheaterSoundTshdInputGain ;
    param.PARAM_SRS_THEATERSOUND_TSHD_OUTPUT_GAIN = advancedSoundParameterVo.paramSrsTheaterSoundTshdutputGain ;
    param.PARAM_SRS_THEATERSOUND_SURR_LEVEL_CONTROL = advancedSoundParameterVo.paramSrsTheaterSoundSurrLevelControl ;
    param.PARAM_SRS_THEATERSOUND_TRUBASS_COMPRESSOR_CONTROL = advancedSoundParameterVo.paramSrsTheaterSoundTrubassCompressorControl ;
    param.PARAM_SRS_THEATERSOUND_TRUBASS_PROCESS_MODE = advancedSoundParameterVo.paramSrsTheaterSoundTrubassProcessMode ;
    param.PARAM_SRS_THEATERSOUND_TRUBASS_SPEAKER_AUDIO = advancedSoundParameterVo.paramSrsTheaterSoundTrubassSpeakerAudio ;
    param.PARAM_SRS_THEATERSOUND_TRUBASS_SPEAKER_ANALYSIS = advancedSoundParameterVo.paramSrsTheaterSoundTrubassSpeakerAnalysis ;

    int32_t ret = m_MSrvSSSound->SetAdvancedSoundEffect((ADVSND_PARAM_TYPE)advancedSoundParamType, &param);
    TVOS_API_ESTIMATE_END()
    return ret;
}

int32_t AudioManagerService::Client::getAdvancedSoundEffect(int32_t advancedSoundParamType)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_INT_INT("getAdvancedSoundEffect","advancedSoundParamType",advancedSoundParamType);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("getAdvancedSoundEffect(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    int32_t ret = m_MSrvSSSound->GetAdvancedSoundEffect((ADVSND_PARAM_TYPE)advancedSoundParamType);
    TVOS_API_ESTIMATE_END()
    return ret;
}

int16_t AudioManagerService::Client::setSubWooferVolume(bool mute, int16_t value)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_INT_BOOL_INT("setSubWooferVolume","mute",mute,"value",value);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setSubWooferVolume(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    int16_t ret = m_MSrvSSSound->SND_SetSubWooferVolume(mute, value);
    TVOS_API_ESTIMATE_END()
    return ret;
}

void  AudioManagerService::Client::setDebugMode(bool mode)
{
    TVOS_API_ESTIMATE_START()
    m_bEnableDebug = mode;
    TVOS_API_ESTIMATE_END()
}

int8_t AudioManagerService::Client::exectueAmplifierExtendedCommand(int8_t subCmd, int32_t param1, int32_t param2, void *param3, int32_t param3Size)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_EXECUTEAMPLIFIEREXTENDEDCOMMAND("exectueAmplifierExtendedCommand", "subCmd",subCmd, "param1",param1, "param2", param2, "param3Size", param3Size);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("exectueAmplifierExtendedCommand(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    int8_t ret = m_MSrvSSSound->SND_AmpExtendCmd(subCmd, param1, param2, param3);
    TVOS_API_ESTIMATE_END()
    return ret;
}

bool AudioManagerService::Client::setAmplifierMute(bool bmute)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_INT_INT("setAmplifierMute","bmute",bmute);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setAmplifierMute(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    bool ret = m_MSrvSSSound->SetAmplifierMute(bmute);
    TVOS_API_ESTIMATE_END()
    return ret;
}

void AudioManagerService::Client::setAmplifierEqualizerByMode(int equalizertype)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_VOID_INT("setAmplifierMute","equalizertype",equalizertype);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setAmplifierEqualizerByMode(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    m_MSrvSSSound->SetAmplifierEQByMode((ENUM_SET_EQ_BY_MODE_TYPE)equalizertype);
    TVOS_API_ESTIMATE_END()
}

uint8_t AudioManagerService::Client::setSoundParameter(const int32_t Type, const int16_t param1, const int16_t param2)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_INT_INT3("setSoundParameter","Type",Type,"param1",param1,"param2",param2);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setSoundParameter(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    uint8_t ret = m_MSrvSSSound->SND_SetParam((Sound_SET_PARAM_Type_)Type, param1, param2);
    TVOS_API_ESTIMATE_END()
    return ret;
}

int16_t AudioManagerService::Client::getSoundParameter(const int32_t Type, const int16_t param1)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_INT_INT2("getSoundParameter","Type",Type,"param1",param1);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("getSoundParameter(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    int16_t ret = m_MSrvSSSound->SND_GetParam((Sound_GET_PARAM_Type_)Type, param1);
    TVOS_API_ESTIMATE_END()
    return ret;
}

uint8_t AudioManagerService::Client::setSpeakerDelay(uint32_t delay)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_INT_INT("setSpeakerDelay","delay",delay);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setSpeakerDelay(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    uint8_t ret = m_MSrvSSSound->SetSNDSpeakerDelay(delay);
    TVOS_API_ESTIMATE_END()
    return ret;
}

uint8_t AudioManagerService::Client::setSpdifDelay(uint32_t delay)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_INT_INT("setSpdifDelay","delay",delay);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setSpeakerDelay(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    uint8_t ret = m_MSrvSSSound->SetSPDIFDelay(delay);
    TVOS_API_ESTIMATE_END()
    return ret;
}

uint8_t AudioManagerService::Client::setKtvMixModeVolume(int32_t VolType,  uint8_t u8Vol1, uint8_t u8Vol2)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_INT_INT3("setKtvMixModeVolume","VolType",VolType, "u8Vol1", u8Vol1, "u8Vol2", u8Vol2);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setKtvMixModeVolume(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    uint8_t ret = m_MSrvSSSound->KTV_SetMixModeVolume((AUDIO_MIX_VOL_TYPE_)VolType, u8Vol1, u8Vol2);
    TVOS_API_ESTIMATE_END()
    return ret;
}

uint8_t AudioManagerService::Client::enableKtvMixModeMute(int32_t VolType)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_INT_INT("enableKtvMixModeMute","VolType",VolType);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("enableKtvMixModeMute(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    uint8_t ret = m_MSrvSSSound->KTV_SetMixModeMute((AUDIO_MIX_VOL_TYPE_)VolType, TRUE);
    TVOS_API_ESTIMATE_END()
    return ret;
}

uint8_t AudioManagerService::Client::disableKtvMixModeMute(int32_t VolType)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_INT_INT("disableKtvMixModeMute","VolType",VolType);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("disableKtvMixModeMute(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    uint8_t ret = m_MSrvSSSound->KTV_SetMixModeMute((AUDIO_MIX_VOL_TYPE_)VolType, FALSE);
    TVOS_API_ESTIMATE_END()
    return ret;
}

int16_t AudioManagerService::Client::setAudioCaptureSource(int32_t audioDeviceType, int32_t audioType)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_INT_INT("setAudioCaptureSource","audioType",audioType);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setAudioCaptureSource(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    int16_t ret = m_MSrvSSSound->SetAudioCaptureSource((MSRV_AUDIO_CAPTURE_DEVICE_TYPE)audioDeviceType, (MSRV_AUDIO_CAPTURE_SOURCE)audioType);
    TVOS_API_ESTIMATE_END()
    return ret;
}

int16_t AudioManagerService::Client::setOutputSourceInfo(int32_t eAudioPath,int32_t eSource)
{
#if(RELEASE_BINDER_TEST ==1)
    TEST_INT_INT2("setOutputSourceInfo","eAudioPath",eAudioPath,"eSource",eSource);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setOutputSourceInfo(callingPid %d, clientPid %d)\n",IPCThreadState::self()->getCallingPid(), m_ClientPid);
    int16_t ret = m_MSrvSSSound->SetOutputSourceInfo((AUDIO_VOL_SOURCE_TYPE)eAudioPath,(MSRV_AUDIO_PROCESSOR_TYPE) eSource);
    TVOS_API_ESTIMATE_END()
    return ret;
    }


int16_t AudioManagerService::Client::setKtvSoundInfo(int32_t ektvInfoType, int32_t param1, int32_t param2)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_INT_INT3("setKtvSoundInfo","ektvInfoType",ektvInfoType,"param1",param1,"param2",param2);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setKtvSoundInfo(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    int16_t ret = m_MSrvSSSound->SND_KTV_SetInfo((KTV_SET_INFO_TYPE_)ektvInfoType, param1, param2);
    TVOS_API_ESTIMATE_END()
    return ret;
}

int32_t AudioManagerService::Client::getKtvSoundInfo(int32_t ektvInfoType)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_INT_INT("getKtvSoundInfo","ektvInfoType",ektvInfoType);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("getKtvSoundInfo(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    int32_t ret = m_MSrvSSSound->SND_GetKTVInfo((KTV_GET_INFO_TYPE_)ektvInfoType);
    TVOS_API_ESTIMATE_END()
    return ret;
}

int32_t AudioManagerService::Client::setKtvSoundTrack(int32_t enSoundMode)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_INT_INT("setKtvSoundTrack","enSoundMode",enSoundMode);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setKtvSoundTrack(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    int32_t ret = m_MSrvSSSound->SetKTVSoundTrack((KTV_AUDIO_MPEG_SOUNDMODE)enSoundMode);
    TVOS_API_ESTIMATE_END()
    return ret;
}

bool AudioManagerService::Client::setCommonAudioInfo(int32_t eInfoType, int32_t param1, int32_t param2)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_INT_INT3("setCommonAudioInfo","eInfoType",eInfoType,"param1",param1,"param2",param2);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setCommonAudioInfo(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    bool ret = m_MSrvSSSound->SetCommAudioInfo((Audio_COMM_infoType_)eInfoType, param1, param2);
    TVOS_API_ESTIMATE_END()
    return ret;
}

int32_t AudioManagerService::Client::setAudioSource(int32_t eInputSrc, int32_t eAudioProcessType)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_INT_INT2("setAudioSource","eInputSrc",eInputSrc,"eAudioProcessType",eAudioProcessType);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setAudioSource(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    int32_t ret = m_MSrvSSSound->SetAudioSource( (MAPI_INPUT_SOURCE_TYPE)eInputSrc, (MSRV_AUDIO_PROCESSOR_TYPE) eAudioProcessType);
    TVOS_API_ESTIMATE_END()
    return ret;
}

void AudioManagerService::Client::setAudioLanguage1(int32_t enLanguage)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_VOID_INT("setAudioLanguage1","enLanguage",enLanguage);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setAudioLanguage1(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);

    m_MSrvSSSound->SetAudioLanguage1(enLanguage);
    TVOS_API_ESTIMATE_END()
}

void AudioManagerService::Client::setAudioLanguage2(int32_t enLanguage)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_VOID_INT("setAudioLanguage2","enLanguage",enLanguage);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setAudioLanguage1(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);

    m_MSrvSSSound->SetAudioLanguage2(enLanguage);
    TVOS_API_ESTIMATE_END()
}

int32_t AudioManagerService::Client::getAudioLanguage1()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("getAudioLanguage1(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);

    int32_t ret = m_MSrvSSSound->GetAudioLanguage1();
    TVOS_API_ESTIMATE_END()
    return ret;
}

int32_t AudioManagerService::Client::getAudioLanguage2()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("getAudioLanguage1(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);

    int32_t ret = m_MSrvSSSound->GetAudioLanguage2();
    TVOS_API_ESTIMATE_END()
    return ret;
}
void AudioManagerService::Client::setAutoVolume(bool enAutoVol)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setAutoVolume(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    m_MSrvSSSound->SetAutoVolume(enAutoVol);
}
bool AudioManagerService::Client::getAutoVolume()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("getAutoVolume(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    bool ret = m_MSrvSSSound->GetAutoVolume();
    TVOS_API_ESTIMATE_END()
    return ret;
}
//jerry.wang add
uint8_t AudioManagerService::Client::SetSNDDTSInfo(int32_t infoType,uint32_t param1,uint32_t param2)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("SetSNDDTSInfo(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);

    uint8_t ret = m_MSrvSSSound->SetSNDDTSInfo((Audio_DTS_infoType_)infoType,param1,param2);
    TVOS_API_ESTIMATE_END()
    return ret;
}
uint32_t AudioManagerService::Client::GetSNDAACInfo(int32_t infoType)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("GetSNDAACInfo(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);

    uint32_t ret = m_MSrvSSSound->GetSNDAACInfo((Audio_AAC_infoType_)infoType);
    TVOS_API_ESTIMATE_END()
    return ret;
}
uint8_t AudioManagerService::Client::SetSNDAACInfo(int32_t infoType,uint32_t param1,uint32_t param2)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("SetSNDAACInfo(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);

    uint8_t ret = m_MSrvSSSound->SetSNDAACInfo((Audio_AAC_infoType_)infoType, param1, param2);
    TVOS_API_ESTIMATE_END()
    return ret;
}
uint32_t AudioManagerService::Client::GetSNDAC3Info(int32_t infoType)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("GetSNDAC3Info (callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);

    uint32_t ret = m_MSrvSSSound->GetSNDAC3Info((Audio_AC3_infoType_ )infoType);
    TVOS_API_ESTIMATE_END()

    return ret;
}
uint8_t AudioManagerService::Client::SetSNDAC3Info(int32_t infoType,uint32_t param1,uint32_t param2)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("GetSNDAC3Info (callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);

    uint8_t ret = m_MSrvSSSound->SetSNDAC3Info((Audio_AC3_infoType_ )infoType, param1,param2);
    TVOS_API_ESTIMATE_END()

    return ret;
}

uint32_t AudioManagerService::Client::GetSNDAC3PInfo(int32_t infoType)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("GetSNDAC3PInfo (callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);

    uint32_t ret = m_MSrvSSSound->GetSNDAC3PInfo((Audio_AC3P_infoType_ )infoType);
    TVOS_API_ESTIMATE_END()

    return ret;
}
uint8_t AudioManagerService::Client::SetSNDAC3PInfo(int32_t infoType,uint32_t param1,uint32_t param2)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("SetSNDAC3PInfo (callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);

    uint8_t ret = m_MSrvSSSound->SetSNDAC3PInfo((Audio_AC3P_infoType_ )infoType, param1,param2);
    TVOS_API_ESTIMATE_END()

    return ret;
}

uint32_t AudioManagerService::Client::GetSNDMpegInfo(int32_t infoType)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("GetSNDMpegInfo (callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);

    uint32_t ret = m_MSrvSSSound->GetSNDMpegInfo((Audio_MPEG_infoType_ )infoType);
    TVOS_API_ESTIMATE_END()

    return ret;
}

uint32_t AudioManagerService::Client::GetMpegFrameCnt()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("GetMpegFrameCnt (callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);

    uint32_t ret = m_MSrvSSSound->GetMpegFrameCnt();
    TVOS_API_ESTIMATE_END()

    return ret;
}
uint64_t AudioManagerService::Client::GetAudioCommInfo(int32_t infoType)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("GetAduioCommInfo (callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);

    uint64_t ret = MApi_AUDIO_GetCommAudioInfo((Audio_COMM_infoType)infoType);
    TVOS_API_ESTIMATE_END()

    return ret;
    //return m_MSrvSSSound->GetAudioCommInfo(infoType);
}
uint8_t AudioManagerService::Client::CheckInputRequest(uint32_t *pU32WrtAddr, uint32_t *pU32WrtBytes,uint8_t bCheck)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("CheckInputRequest (callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    uint8_t retval=0;
    MAPI_U32 u32Addr=0;
    MAPI_U32 u32Size=0;
    retval=m_MSrvSSSound->CheckInputRequest((MAPI_U32*)&u32Addr,(MAPI_U32*)&u32Size);
    if(bCheck == 0)
    {
        memcpy((void*)(MS_PA2KSEG1(u32Addr)),(void*)pU32WrtAddr,*pU32WrtBytes);
    }

    TVOS_API_ESTIMATE_END()

    return retval;

}
void AudioManagerService::Client::SetInput(void)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("SetInput (callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);

    m_MSrvSSSound->SetInput();
    TVOS_API_ESTIMATE_END()
}
void AudioManagerService::Client::StartDecode(void)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("StartDecode (callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);

    m_MSrvSSSound->StartDecode();
    TVOS_API_ESTIMATE_END()

}
void AudioManagerService::Client::StopDecode(void)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("StopDecode (callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);

    m_MSrvSSSound->StopDecode();
    TVOS_API_ESTIMATE_END()
}
void AudioManagerService::Client::SwitchAudioDSPSystem(int32_t eAudioDSPSystem)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("SwitchAudioDSPSystem (callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);

    m_MSrvSSSound->SwitchAudioDSPSystem((AUDIO_DSP_SYSTEM_)eAudioDSPSystem);
    TVOS_API_ESTIMATE_END()

}
void AudioManagerService::Client::setAudioSpidifOutPut(int32_t eSpidif_output)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setAudioSpidifOutPut(eSpidif_output %d)\n", eSpidif_output);
    m_MSrvSSSound->SetAudioSpidifOutPut((SPDIF_TYPE_)eSpidif_output);
    TVOS_API_ESTIMATE_END()
}

void AudioManagerService::Client::setAudioHDMIOutPut(int32_t eHdmi_putput)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setAudioHDMIOutPut(eHdmi_putput %d)\n", eHdmi_putput);
    m_MSrvSSSound->SetAudioHDMIOutPut((HDMI_TYPE_)eHdmi_putput);
    TVOS_API_ESTIMATE_END()
}

void AudioManagerService::Client::setAudioHDMITx_HDBypass(bool bByPassOnOff)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("setAudioHDMITx HDBypass(bByPassOnOff %d)\n", bByPassOnOff);
    m_MSrvSSSound->SetAudioHDMItx_HDBypass(bByPassOnOff);
    TVOS_API_ESTIMATE_END()
}

bool AudioManagerService::Client::getAudioHDMITx_HDBypass()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    bool ret = m_MSrvSSSound->GetAudioHDMItx_HDBypass();
    ALOGV("getAudioHDMITx HDBypass(bByPassOnOff %d)\n", ret);
    TVOS_API_ESTIMATE_END()
    return ret;
}

bool AudioManagerService::Client::getAudioHDMITx_HDBypass_Capability()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    bool ret = m_MSrvSSSound->GetAudioHDMItx_HDBypass_Capability();
    ALOGV("getAudioHDMITx HDBypass Capability(%d)\n", ret);
    TVOS_API_ESTIMATE_END()
    return ret;
}
void AudioManagerService::Client::setMicSSound(int8_t value)
{
	TVOS_API_ESTIMATE_START()
	TVOS_API_ESTIMATE_START(LOCK)
	Mutex::Autolock lock(m_AudioFuncLock);
	TVOS_API_ESTIMATE_END(LOCK)
#if (KARAOKE_ENABLE == 1)
	m_MSrvSSSound->SetMicVol(value);
#endif
	ALOGV("setMicSSound(value %d)\n", value);
	TVOS_API_ESTIMATE_END()
}
void AudioManagerService::Client::setMicEcho(int8_t value)
{
	TVOS_API_ESTIMATE_START()
	TVOS_API_ESTIMATE_START(LOCK)
	Mutex::Autolock lock(m_AudioFuncLock);
	TVOS_API_ESTIMATE_END(LOCK)
#if (KARAOKE_ENABLE == 1)
	  m_MSrvSSSound->SetMicEcho(value);
#endif
	ALOGV("setMicEcho(value %d)\n", value);
	TVOS_API_ESTIMATE_END()
}
uint8_t AudioManagerService::Client::getMicSSound(void)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_AudioFuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
     uint8_t Value= 0;
#if (KARAOKE_ENABLE == 1)
     Value = m_MSrvSSSound->GetMicVol();
#endif
    ALOGV("getMicSSound(%d)\n", Value);
    TVOS_API_ESTIMATE_END()
    return Value;     
}
uint8_t AudioManagerService::Client::getMicEcho(void)
{
	TVOS_API_ESTIMATE_START()
	TVOS_API_ESTIMATE_START(LOCK)
	Mutex::Autolock lock(m_AudioFuncLock);
	TVOS_API_ESTIMATE_END(LOCK)
	 uint8_t Value= 0;
#if (KARAOKE_ENABLE == 1)
	Value = m_MSrvSSSound->GetMicEcho();
#endif
	ALOGV("getMicEcho(%d)\n", Value);
	TVOS_API_ESTIMATE_END()
	return Value;     
}
