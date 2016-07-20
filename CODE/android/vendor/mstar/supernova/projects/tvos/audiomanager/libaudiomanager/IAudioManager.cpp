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
#define LOG_TAG "IAudioManager"
#include <utils/Log.h>

#include "IAudioManager.h"
#include <binder/IMemory.h>
#include <binder/MemoryHeapBase.h>
#include <binder/MemoryBase.h>

enum
{
    DISCONNECT = IBinder::FIRST_CALL_TRANSACTION,
    CHECKATVSOUNDSYSTEM =IBinder::FIRST_CALL_TRANSACTION+1,
    SETAUDIOVOLUME =IBinder::FIRST_CALL_TRANSACTION+2,
    GETAUDIOVOLUME =IBinder::FIRST_CALL_TRANSACTION+3,
    SETATVINFO =IBinder::FIRST_CALL_TRANSACTION+4,
    GETATVINFO =IBinder::FIRST_CALL_TRANSACTION+5,
    SETAUDIOOUTPUT =IBinder::FIRST_CALL_TRANSACTION+6,
    ENABLEBASICSOUNDEFFECT =IBinder::FIRST_CALL_TRANSACTION+7,
//    ENABLEAUTOVOLUME =IBinder::FIRST_CALL_TRANSACTION+8,
//    DISABLEAUTOVOLUME =IBinder::FIRST_CALL_TRANSACTION+9,
//    ISAUTOVOLUMEENABLED =IBinder::FIRST_CALL_TRANSACTION+10,
    ENABLEMUTE =IBinder::FIRST_CALL_TRANSACTION+11,
    DISABLEMUTE =IBinder::FIRST_CALL_TRANSACTION+12,
    ISMUTEEABLE =IBinder::FIRST_CALL_TRANSACTION+13,
    SETATVMTSMODE =IBinder::FIRST_CALL_TRANSACTION+14,
    GETATVMTSMODE =IBinder::FIRST_CALL_TRANSACTION+15,
    SETTONEXTATVMTSMODE =IBinder::FIRST_CALL_TRANSACTION+16,
    SETATVSOUNDSYSTEM =IBinder::FIRST_CALL_TRANSACTION+17,
    GETATVSOUNDSYSTEM =IBinder::FIRST_CALL_TRANSACTION+18,
    SETDTVOUTPUTMODE =IBinder::FIRST_CALL_TRANSACTION+19,
    GETDTVOUTPUTMODE =IBinder::FIRST_CALL_TRANSACTION+20,
    SETBASICSOUNDEFFECT =IBinder::FIRST_CALL_TRANSACTION+21,
    GETBASICSOUNDEFFECT =IBinder::FIRST_CALL_TRANSACTION+22,
    SETINPUTLEVEL =IBinder::FIRST_CALL_TRANSACTION+23,
    GETINPUTLEVEL =IBinder::FIRST_CALL_TRANSACTION+24,
//    MUTEINPUT =IBinder::FIRST_CALL_TRANSACTION+25,
    SETDIGITALOUT =IBinder::FIRST_CALL_TRANSACTION+26,
    SETINPUTSOURCE =IBinder::FIRST_CALL_TRANSACTION+27,
    GETINPUTSOURCE =IBinder::FIRST_CALL_TRANSACTION+28,
    GETATVSOUNDMODE =IBinder::FIRST_CALL_TRANSACTION+29,
    AUDIOMANAGER_SET_AUDIO_MUTE =IBinder::FIRST_CALL_TRANSACTION+30,
    SETDEBUGMODE =IBinder::FIRST_CALL_TRANSACTION+31,
    AUDIOMANAGER_ENABLE_ADV_SOUND_EFFECT =IBinder::FIRST_CALL_TRANSACTION+32,
    AUDIOMANAGER_SET_ADV_SOUND_EFFECT =IBinder::FIRST_CALL_TRANSACTION+33,
    AUDIOMANAGER_GET_ADV_SOUND_EFFECT =IBinder::FIRST_CALL_TRANSACTION+34,
    AUDIOMANAGER_SET_SUB_WOOFER_VOLUME =IBinder::FIRST_CALL_TRANSACTION+35,
    AUDIOMANAGER_EXECUTE_AMPLIFIER_EXTENED_COMMAND = IBinder::FIRST_CALL_TRANSACTION+36,
    AUDIOMANAGER_SET_AMPLIFIER_MUTE = IBinder::FIRST_CALL_TRANSACTION+37,
    AUDIOMANAGER_SET_AMPLIFIER_EQUALIZER_BYMODE = IBinder::FIRST_CALL_TRANSACTION+38,
    AUDIOMANAGER_SOUND_SET_PARAM = IBinder::FIRST_CALL_TRANSACTION+39,
    AUDIOMANAGER_SOUND_GET_PARAM = IBinder::FIRST_CALL_TRANSACTION+40,
    AUDIOMANAGER_SET_SOUND_SPEAKER_DELAY = IBinder::FIRST_CALL_TRANSACTION+41,
    AUDIOMANAGER_KTV_SET_MIX_MODE_VOLUME = IBinder::FIRST_CALL_TRANSACTION+42,
    AUDIOMANAGER_ENABLE_KTV_MIX_MODE_MUTE = IBinder::FIRST_CALL_TRANSACTION+43,
    AUDIOMANAGER_DISABLE_KTV_MIX_MODE_MUTE = IBinder::FIRST_CALL_TRANSACTION+44,
    AUDIOMANAGER_SET_AUDIO_CAPTURE_SOURCE = IBinder::FIRST_CALL_TRANSACTION+45,
    AUDIOMANAGER_SET_KTV_SOUND_INFO = IBinder::FIRST_CALL_TRANSACTION+46,
    AUDIOMANAGER_GET_KTV_SOUND_INFO = IBinder::FIRST_CALL_TRANSACTION+47,
    AUDIOMANAGER_SET_KTV_SOUND_TRACK = IBinder::FIRST_CALL_TRANSACTION+48,
    AUDIOMANAGER_SET_COMMON_AUDIO_INFO = IBinder::FIRST_CALL_TRANSACTION+49,
    AUDIOMANAGER_SET_AUDIO_SOURCE = IBinder::FIRST_CALL_TRANSACTION+50,
    AUDIOMANAGER_SET_DTS_INFO   = IBinder::FIRST_CALL_TRANSACTION +51,
    AUDIOMANAGER_GET_AAC_INFO  =  IBinder::FIRST_CALL_TRANSACTION +52,
    AUDIOMANAGER_SET_AAC_INFO = IBinder::FIRST_CALL_TRANSACTION +53,
    AUDIOMANAGER_GET_AC3_INFO = IBinder::FIRST_CALL_TRANSACTION +54,
    AUDIOMANAGER_SET_AC3_INFO = IBinder::FIRST_CALL_TRANSACTION +55,
    AUDIOMANAGER_GET_MPEG_INFO= IBinder::FIRST_CALL_TRANSACTION +56,
    AUDIOMANAGER_GET_FRAMECNT = IBinder::FIRST_CALL_TRANSACTION +57,
    AUDIOMANAGER_GET_GETCOMMINFO=IBinder::FIRST_CALL_TRANSACTION + 58,
    AUDIOMANAGER_CHECK_INPUTREQUEST=IBinder::FIRST_CALL_TRANSACTION +59,
    AUDIOMANAGER_SETINPUT = IBinder::FIRST_CALL_TRANSACTION +60,
    AUDIOMANAGER_START_DECODE=IBinder::FIRST_CALL_TRANSACTION+61,
    AUDIOMANAGER_STOP_DECODE=IBinder::FIRST_CALL_TRANSACTION+62,
    AUDIOMANAGER_SWITCH_DSPSYSTEM=IBinder::FIRST_CALL_TRANSACTION+63,
    AUDIOMANAGER_SET_SPIDIF_OUTPUT=IBinder::FIRST_CALL_TRANSACTION+64,
    AUDIOMANAGER_SET_HDMI_OUTPUT=IBinder::FIRST_CALL_TRANSACTION+65,
    AUDIOMANAGER_SET_AUDIO_LANGUAGE1 = IBinder::FIRST_CALL_TRANSACTION+66,
    AUDIOMANAGER_SET_AUDIO_LANGUAGE2 = IBinder::FIRST_CALL_TRANSACTION+67,
    AUDIOMANAGER_GET_AUDIO_LANGUAGE1 = IBinder::FIRST_CALL_TRANSACTION+68,
    AUDIOMANAGER_GET_AUDIO_LANGUAGE2 = IBinder::FIRST_CALL_TRANSACTION+69,
    AUDIOMANAGER_SET_SOUND_SPDIF_DELAY = IBinder::FIRST_CALL_TRANSACTION+70,
    /*add by owen.qin begin*/
    AUDIOMANAGER_SET_AD_ENABLE = IBinder::FIRST_CALL_TRANSACTION+71,
    AUDIOMANAGER_SET_AD_ABSOLUTE_VOLUME = IBinder::FIRST_CALL_TRANSACTION+72,
    /*add by owen.qin end*/
    /*add by owen.qin begin*/
    AUDIOMANAGER_SET_HOH_STATUS = IBinder::FIRST_CALL_TRANSACTION+73,
    /*add by owen.qin end*/
    AUDIOMANAGER_SET_AUTO_VOLUME = IBinder::FIRST_CALL_TRANSACTION+74,
    AUDIOMANAGER_GET_AUTO_VOLUME = IBinder::FIRST_CALL_TRANSACTION+75,
    /*add by ken.bi begin*/
    AUDIOMANAGER_SET_OUTPUT_SOURCE_INFO = IBinder::FIRST_CALL_TRANSACTION+76,
    /*add by ken.bi end*/
    AUDIOMANAGER_GET_AC3P_INFO = IBinder::FIRST_CALL_TRANSACTION +77,
    AUDIOMANAGER_SET_AC3P_INFO = IBinder::FIRST_CALL_TRANSACTION +78,
    AUDIOMANAGER_SET_HDMITX_HDBYPASS = IBinder::FIRST_CALL_TRANSACTION +79,
    AUDIOMANAGER_GET_HDMITX_HDBYPASS = IBinder::FIRST_CALL_TRANSACTION +80,
    AUDIOMANAGER_GET_HDMITX_HDBYPASS_CAP = IBinder::FIRST_CALL_TRANSACTION +81,
    AUDIOMANAGER_SET_MIC_VAL = IBinder::FIRST_CALL_TRANSACTION + 82,
    AUDIOMANAGER_SET_MIC_ECHO = IBinder::FIRST_CALL_TRANSACTION + 83,
    AUDIOMANAGER_GET_MIC_VAL = IBinder::FIRST_CALL_TRANSACTION + 84,
    AUDIOMANAGER_GET_MIC_ECHO = IBinder::FIRST_CALL_TRANSACTION + 85,
};

class BpAudioManager: public BpInterface<IAudioManager>
{
public:
    explicit BpAudioManager(const sp<IBinder>& impl);
    virtual void disconnect();
    virtual int32_t checkAtvSoundSystem();
    virtual void setAudioVolume(int32_t enSoundPath, int8_t volumn);
    virtual int8_t getAudioVolume(int32_t volSrcType);

    virtual int32_t setAtvInfo(int32_t infoType, int32_t config);
    virtual int32_t getAtvInfo();
    virtual int32_t setAudioOutput(int32_t outputType, AudioOutParameter param);
    virtual int32_t enableBasicSoundEffect(int32_t soundType, bool enable);
    /*add by owen.qin begin*/
    virtual void setADEnable(bool enable);
    virtual void setADAbsoluteVolume(int32_t value);
    /*add by owen.qin end*/    
    
    /*add by owen.qin begin*/
    virtual void setAutoHOHEnable(bool enable);
    /*add by owen.qin end*/

#if 0
    virtual void enableAutoVolume();
    virtual void disableAutoVolume();
    virtual bool isAutoVolumeEnabled();
#endif
    virtual bool enableMute(int32_t enMuteType);
    virtual bool disableMute(int32_t enMuteType);
    virtual bool isMuteEnabled(int32_t enMuteType);
    virtual int32_t setAtvMtsMode(int32_t mode);
    virtual int32_t getAtvMtsMode();
    virtual int16_t setToNextAtvMtsMode();
    virtual bool setAtvSoundSystem(int32_t mode);
    virtual int32_t getAtvSoundSystem();
    virtual void setDtvOutputMode(int32_t mode);
    virtual int32_t getDtvOutputMode();
    virtual int32_t setBasicSoundEffect(int32_t soundEffectType,DtvSoundEffect &ef);
    virtual int32_t getBasicSoundEffect(int32_t effectType);
    virtual void setInputLevel(int32_t src,int16_t level);
    virtual int32_t getInputLevel(int32_t enAudioInputSource);
//    virtual void muteInput(int32_t src);
    virtual void setDigitalOut(int32_t mode);
    virtual void setInputSource(int32_t src);
    virtual int32_t getInputSource();
    virtual  int32_t getAtvSoundMode();
    virtual void setDebugMode(bool mode);
    virtual int8_t exectueAmplifierExtendedCommand(int8_t subCmd, int32_t param1, int32_t param2, void *param3, int32_t param3Size);
    virtual bool setAmplifierMute(bool bmute);
    virtual void setAmplifierEqualizerByMode(int equalizertype);
    virtual bool setMuteStatus(int32_t screenUnMuteTime, int32_t eSrcType);
    virtual int32_t enableAdvancedSoundEffect(int32_t soundType,int32_t subProcessType);
    virtual int32_t setAdvancedSoundEffect(int32_t advancedSoundParamType, AdvancedSoundParam advancedSoundParameterVo);
    virtual int32_t getAdvancedSoundEffect(int32_t advancedSoundParamType);
    virtual int16_t setSubWooferVolume(bool mute, int16_t value);
    virtual uint8_t setSoundParameter(const int32_t Type, const int16_t param1, const int16_t param2);
    virtual int16_t getSoundParameter(const int32_t Type, const int16_t param1);
    virtual uint8_t setSpeakerDelay(uint32_t delay);

    virtual uint8_t setSpdifDelay(uint32_t delay);

    virtual uint8_t setKtvMixModeVolume(int32_t VolType,  uint8_t u8Vol1, uint8_t u8Vol2);
    virtual uint8_t enableKtvMixModeMute(int32_t VolType);
    virtual uint8_t disableKtvMixModeMute(int32_t VolType);
    virtual int16_t setAudioCaptureSource(int32_t audioDeviceType, int32_t audioType);
    virtual int16_t setOutputSourceInfo(int32_t eAudioPath,int32_t eSource);
    virtual int16_t setKtvSoundInfo(int32_t ektvInfoType, int32_t param1, int32_t param2);
    virtual int32_t getKtvSoundInfo(int32_t ektvInfoType);
    virtual int32_t setKtvSoundTrack(int32_t enSoundMode);
    virtual bool setCommonAudioInfo(int32_t eInfoType, int32_t param1, int32_t param2);
    virtual int32_t setAudioSource(int32_t eInputSrc, int32_t eAudioProcessType);


    virtual void setAudioLanguage1(int32_t enLanguage);
    virtual void setAudioLanguage2(int32_t enLanguage);
    virtual int32_t getAudioLanguage1();
    virtual int32_t getAudioLanguage2();
    virtual void setAutoVolume(bool enAutoVol);
    virtual bool getAutoVolume();


    //jerry.wang
    virtual uint8_t SetSNDDTSInfo(int32_t infoType,uint32_t param1,uint32_t param2);
    virtual uint32_t GetSNDAACInfo(int32_t infoType);
    virtual uint8_t SetSNDAACInfo(int32_t infoType,uint32_t param1,uint32_t param2);
    virtual uint32_t GetSNDAC3Info(int32_t infoType);
    virtual uint8_t SetSNDAC3Info(int32_t infoType,uint32_t param1,uint32_t param2);
    virtual uint32_t GetSNDAC3PInfo(int32_t infoType);
    virtual uint8_t SetSNDAC3PInfo(int32_t infoType,uint32_t param1,uint32_t param2);
    virtual uint32_t GetSNDMpegInfo(int32_t infoType);
    virtual uint32_t GetMpegFrameCnt(void);
    virtual uint64_t GetAudioCommInfo(int32_t infoType);
    virtual uint8_t CheckInputRequest(uint32_t *pU32WrtAddr, uint32_t *pU32WrtBytes, uint8_t bCkeck) ;
    virtual void SetInput(void);
    virtual void StartDecode(void);
    virtual void StopDecode(void);
    virtual void SwitchAudioDSPSystem(int32_t eAudioDSPSystem);
    virtual void setAudioSpidifOutPut(int32_t eSpidif_output);
    virtual void setAudioHDMIOutPut(int32_t eHdmi_putput);
    virtual void setAudioHDMITx_HDBypass(bool bByPassOnOff);
    virtual bool getAudioHDMITx_HDBypass();
    virtual bool getAudioHDMITx_HDBypass_Capability();
     //Eostek Patch Begin
    virtual void setMicEcho(int8_t value);
    virtual void setMicSSound(int8_t value);
    virtual uint8_t getMicEcho();
    virtual uint8_t getMicSSound();
    // EosTek Patch End
};

BpAudioManager::BpAudioManager(const sp<IBinder>& impl)
    : BpInterface<IAudioManager>(impl)
{
}

void BpAudioManager::disconnect()
{
    ALOGV("Send DISCONNECT\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    remote()->transact(DISCONNECT, data, &reply);
}

int32_t BpAudioManager::checkAtvSoundSystem()
{
    ALOGV("Send CHECKATVSOUNDSYSTEM\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    remote()->transact(CHECKATVSOUNDSYSTEM, data, &reply);

    return reply.readInt32();
}

void BpAudioManager::setAudioVolume(int32_t enSoundPath, int8_t volumn)
{
    ALOGV("Send SETAUDIOVOLUME\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(enSoundPath);
    data.writeInt32(volumn);
    remote()->transact(SETAUDIOVOLUME, data, &reply);
}

int8_t BpAudioManager::getAudioVolume(int32_t volSrcType)
{
    ALOGV("Send GETAUDIOVOLUME\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(volSrcType);
    remote()->transact(GETAUDIOVOLUME, data, &reply);
    return reply.readInt32();
}

int32_t BpAudioManager::setAtvInfo(int32_t infoType, int32_t config)
{
    ALOGV("Send SETATVINFO\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(infoType);
    data.writeInt32(config);
    remote()->transact(SETATVINFO, data, &reply);
    return reply.readInt32();
}

int32_t BpAudioManager::getAtvInfo()
{
    ALOGV("Send GETATVINFO\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    remote()->transact(GETATVINFO, data, &reply);
    return reply.readInt32();
}


bool BpAudioManager::setMuteStatus(int32_t screenUnMuteTime, int32_t eSrcType)
{
    printf("Send AUDIOMANAGER_SET_AUDIO_MUTE\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(screenUnMuteTime);
    data.writeInt32(eSrcType);
    remote()->transact(AUDIOMANAGER_SET_AUDIO_MUTE, data, &reply);
    return static_cast<bool>(reply.readInt32());
}


int32_t BpAudioManager::setAudioOutput(int32_t outputType, AudioOutParameter param)
{
    ALOGV("Send SETAUDIOOUTPUT\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(outputType);
    data.writeInt32(param.spdifDelayTime);
    data.writeInt32(param.speakerDelayTime);
    data.writeInt32(param.spdifOutmodInUi);
    data.writeInt32(param.spdifOutmodActive);
    remote()->transact(SETAUDIOOUTPUT, data, &reply);
    return reply.readInt32();
}

int32_t BpAudioManager::enableBasicSoundEffect(int32_t soundType, bool enable)
{
    ALOGV("Send ENABLEBASICSOUNDEFFECT\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(soundType);
    data.writeInt32(enable);
    remote()->transact(ENABLEBASICSOUNDEFFECT, data, &reply);
    return reply.readInt32();
}

#if 0
void BpAudioManager::enableAutoVolume()
{
    ALOGV("Send ENABLEAUTOVOLUME\n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    remote()->transact(ENABLEAUTOVOLUME, data, &reply);
}

void BpAudioManager::disableAutoVolume()
{
    ALOGV("Send DISABLEAUTOVOLUME\n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    remote()->transact(DISABLEAUTOVOLUME, data, &reply);
}

bool BpAudioManager::isAutoVolumeEnabled()
{
    ALOGV("Send ISAUTOVOLUMEENABLED\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    remote()->transact(ISAUTOVOLUMEENABLED, data, &reply);
    return reply.readInt32();
}
#endif
bool BpAudioManager::enableMute(int32_t enMuteType)
{
    ALOGV("Send ENABLEMUTE\n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(enMuteType);
    remote()->transact(ENABLEMUTE, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

bool BpAudioManager::disableMute(int32_t enMuteType)
{
    ALOGV("Send DISABLEMUTE\n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(enMuteType);
    remote()->transact(DISABLEMUTE, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

bool BpAudioManager::isMuteEnabled(int32_t enMuteType)
{
    ALOGV("Send siMUTE\n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(enMuteType);
    remote()->transact(ISMUTEEABLE, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

int32_t BpAudioManager::setAtvMtsMode(int32_t mode)
{
    ALOGV("Send SETATVMTSMODE\n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(mode);
    remote()->transact(SETATVMTSMODE, data, &reply);
    return reply.readInt32();
}

int32_t BpAudioManager::getAtvMtsMode()
{
    ALOGV("Send GETATVMTSMODE\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    remote()->transact(GETATVMTSMODE, data, &reply);
    return reply.readInt32();
}

int16_t BpAudioManager::setToNextAtvMtsMode()
{
    ALOGV("Send SETTONEXTATVMTSMODE\n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    remote()->transact(SETTONEXTATVMTSMODE, data, &reply);
    return reply.readInt32();
}

bool BpAudioManager::setAtvSoundSystem(int32_t mode)
{
    ALOGV("Send SETATVSOUNDSYSTEM\n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(mode);
    remote()->transact(SETATVSOUNDSYSTEM, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

int32_t BpAudioManager::getAtvSoundSystem()
{
    ALOGV("Send GETATVSOUNDSYSTEM\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    remote()->transact(GETATVSOUNDSYSTEM, data, &reply);
    return reply.readInt32();
}

void BpAudioManager::setDtvOutputMode(int32_t mode)
{
    ALOGV("Send SETDTVOUTPUTMODE\n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(mode);
    remote()->transact(SETDTVOUTPUTMODE, data, &reply);
}

int32_t BpAudioManager::getDtvOutputMode()
{
    ALOGV("Send GETDTVOUTPUTMODE\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    remote()->transact(GETDTVOUTPUTMODE, data, &reply);
    return reply.readInt32();
}

int32_t BpAudioManager::setBasicSoundEffect(int32_t soundEffectType,DtvSoundEffect &ef)
{
    ALOGV("Send SETBASICSOUNDEFFECT\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(soundEffectType);
    data.writeInt32(ef.preScale);
    data.writeInt32(ef.treble);
    data.writeInt32(ef.bass);
    data.writeInt32(ef.balance);
    data.writeInt32(ef.eqBandNumber);
    data.writeInt32(ef.peqBandNumber);
    data.writeInt32(ef.avcThreshold);
    data.writeInt32(ef.avcAttachTime);
    data.writeInt32(ef.avcReleaseTime);
    data.writeInt32(ef.surroundXaValue);
    data.writeInt32(ef.surroundXbValue);
    data.writeInt32(ef.surroundXkValue);
    data.writeInt32(ef.soundDrcThreshold);
    data.writeInt32(ef.noiseReductionThreshold);
    data.writeInt32(ef.echoTime);
    int i;
    for(i =0; i < MAXEQNAD; i ++)
    {
        data.writeInt32(ef.soundParameterEqs[i].eqLevel);
    }

    for(i =0; i < MAXPEQNAD; i ++)
    {
        data.writeInt32(ef.soundParameterPeqs[i].peqGain);
        data.writeInt32(ef.soundParameterPeqs[i].peqGc);
        data.writeInt32(ef.soundParameterPeqs[i].peqQvalue);
    }
    remote()->transact(SETBASICSOUNDEFFECT, data, &reply);
    return reply.readInt32();
}

int32_t BpAudioManager::getBasicSoundEffect(int32_t effectType)
{
    ALOGV("Send GETBASICSOUNDEFFECT\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(effectType);
    remote()->transact(GETBASICSOUNDEFFECT, data, &reply);
    return reply.readInt32();
}

void BpAudioManager::setInputLevel(int32_t src,int16_t level)
{
    ALOGV("Send SETINPUTLEVEL\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(src);
    data.writeInt32(level);
    remote()->transact(SETINPUTLEVEL, data, &reply);
}

int32_t BpAudioManager::getInputLevel(int32_t enAudioInputSource)
{
    ALOGV("Send GETINPUTLEVEL\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(enAudioInputSource);
    remote()->transact(GETINPUTLEVEL, data, &reply);
    return reply.readInt32();
}

//void BpAudioManager::muteInput(int32_t src)
//{
//    ALOGV("Send MUTEINPUT\n");
//    Parcel data, reply;
//    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
//    data.writeInt32(src);
//    remote()->transact(MUTEINPUT, data, &reply);
//}



void BpAudioManager::setDigitalOut(int32_t mode)
{
    ALOGV("Send setDigitalOut\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(mode);
    remote()->transact(SETDIGITALOUT, data, &reply);
}

void BpAudioManager::setInputSource(int32_t src)
{
    ALOGV("Send SETINPUTSOURCE\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(src);
    remote()->transact(SETINPUTSOURCE, data, &reply);
}

int32_t BpAudioManager::getInputSource()
{
    ALOGV("Send GETINPUTSOURCE\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    remote()->transact(GETINPUTSOURCE, data, &reply);
    return reply.readInt32();
}

int32_t BpAudioManager::getAtvSoundMode()
{
    ALOGV("Send GETATVSOUNDMODE\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    remote()->transact(GETATVSOUNDMODE, data, &reply);
    return reply.readInt32();
}

void BpAudioManager::setDebugMode(bool mode)
{
    ALOGV("Send SETDEBUGMODE\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(mode);
    remote()->transact(SETDEBUGMODE, data, &reply);
}

int8_t BpAudioManager::exectueAmplifierExtendedCommand(int8_t subCmd, int32_t param1, int32_t param2, void *param3, int32_t param3Size)
{
    ALOGV("Send AUDIOMANAGER_EXECUTE_AMPLIFIER_EXTENED_COMMAND\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(subCmd);
    data.writeInt32(param1);
    data.writeInt32(param2);
    data.writeInt32(param3Size);
    data.write(param3, param3Size*sizeof(int32_t));
    remote()->transact(AUDIOMANAGER_EXECUTE_AMPLIFIER_EXTENED_COMMAND, data, &reply);
    return static_cast<int8_t>(reply.readInt32());
}

bool BpAudioManager::setAmplifierMute(bool bmute)
{
    ALOGV("Send AUDIOMANAGER_SET_AMPLIFIER_MUTE\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(bmute);
    remote()->transact(AUDIOMANAGER_SET_AMPLIFIER_MUTE, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

void BpAudioManager::setAmplifierEqualizerByMode(int equalizertype)
{
    ALOGV("Send AUDIOMANAGER_SET_AMPLIFIER_EQUALIZER_BYMODE\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(equalizertype);
    remote()->transact(AUDIOMANAGER_SET_AMPLIFIER_EQUALIZER_BYMODE, data, &reply);
}

int32_t BpAudioManager::enableAdvancedSoundEffect(int32_t soundType,int32_t subProcessType)
{
    ALOGV("Send AUDIOMANAGER_ENABLE_ADV_SOUND_EFFECT\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(soundType);
    data.writeInt32(subProcessType);
    remote()->transact(AUDIOMANAGER_ENABLE_ADV_SOUND_EFFECT, data, &reply);
    return reply.readInt32();
}

int32_t BpAudioManager::setAdvancedSoundEffect(int32_t advancedSoundParamType, AdvancedSoundParam advancedSoundParameterVo)
{
    ALOGV("Send AUDIOMANAGER_SET_ADV_SOUND_EFFECT\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(advancedSoundParamType);

    data.writeInt32(advancedSoundParameterVo.paramDolbyPl2vdpkSmod);
    data.writeInt32(advancedSoundParameterVo.paramDolbyPl2vdpkWmod);
    data.writeInt32(advancedSoundParameterVo.paramSrsTsxtSetInputGain);
    data.writeInt32(advancedSoundParameterVo.paramSrsTsxtSetDcGain);
    data.writeInt32(advancedSoundParameterVo.paramSrsTsxtSetTrubassGain);
    data.writeInt32(advancedSoundParameterVo.paramSrsTsxtSetSpeakerSize);
    data.writeInt32(advancedSoundParameterVo.paramSrsTsxtSetInputMode);
    data.writeInt32(advancedSoundParameterVo.paramSrsTsxtSetOutputGain);

    data.writeInt32(advancedSoundParameterVo.paramSrsTshdSetInputMode);
    data.writeInt32(advancedSoundParameterVo.paramSrsTshdSetOutputMode);
    data.writeInt32(advancedSoundParameterVo.paramSrsTshdSetSpeakerSize);
    data.writeInt32(advancedSoundParameterVo.paramSrsTshdSetTrubassControl);
    data.writeInt32(advancedSoundParameterVo.paramSrsTshdSetDefinitionControl);
    data.writeInt32(advancedSoundParameterVo.paramSrsTshdSetDcControl);
    data.writeInt32(advancedSoundParameterVo.paramSrsTshdSetSurroundLevel);
    data.writeInt32(advancedSoundParameterVo.paramSrsTshdSetInputGain);
    data.writeInt32(advancedSoundParameterVo.paramSrsTshdSetWowSpaceControl);
    data.writeInt32(advancedSoundParameterVo.paramSrsTshdSetWowCenterControl);
    data.writeInt32(advancedSoundParameterVo.paramSrsTshdSetWowHdSrs3dMode);
    data.writeInt32(advancedSoundParameterVo.paramSrsTshdSetLimiterControl);
    data.writeInt32(advancedSoundParameterVo.paramSrsTshdSetOutputGain);

    data.writeInt32(advancedSoundParameterVo.paramSrsTheaterSoundInputGain);
    data.writeInt32(advancedSoundParameterVo.paramSrsTheaterSoundDefinitionControl);
    data.writeInt32(advancedSoundParameterVo.paramSrsTheaterSoundDcControl);
    data.writeInt32(advancedSoundParameterVo.paramSrsTheaterSoundTrubassControl);
    data.writeInt32(advancedSoundParameterVo.paramSrsTheaterSoundSpeakerSize);
    data.writeInt32(advancedSoundParameterVo.paramSrsTheaterSoundHardLimiterLevel);
    data.writeInt32(advancedSoundParameterVo.paramSrsTheaterSoundHardLimiterBoostGain);
    data.writeInt32(advancedSoundParameterVo.paramSrsTheaterSoundHeadRoomGain);
    data.writeInt32(advancedSoundParameterVo.paramSrsTheaterSoundTruVolumeMode);
    data.writeInt32(advancedSoundParameterVo.paramSrsTheaterSoundTruVolumeRefLevel);
    data.writeInt32(advancedSoundParameterVo.paramSrsTheaterSoundTruVolumeMaxGain);
    data.writeInt32(advancedSoundParameterVo.paramSrsTheaterSoundTruVolumeNoiseMngrThld);
    data.writeInt32(advancedSoundParameterVo.paramSrsTheaterSoundTruVolumeCalibrate);
    data.writeInt32(advancedSoundParameterVo.paramSrsTheaterSoundTruVolumeInputGain);
    data.writeInt32(advancedSoundParameterVo.paramSrsTheaterSoundTruVolumeOutputGain);
    data.writeInt32(advancedSoundParameterVo.paramSrsTheaterSoundHpfFc);

    data.writeInt32(advancedSoundParameterVo.paramDtsUltraTvEvoMonoInput);
    data.writeInt32(advancedSoundParameterVo.paramDtsUltraTvEvoWideningon);
    data.writeInt32(advancedSoundParameterVo.paramDtsUltraTvEvoAdd3dBon);
    data.writeInt32(advancedSoundParameterVo.paramDtsUltraTvEvoPceLevel);
    data.writeInt32(advancedSoundParameterVo.paramDtsUltraTvEvoVlfeLevel);
    data.writeInt32(advancedSoundParameterVo.paramDtsUltraTvSymDefault);
    data.writeInt32(advancedSoundParameterVo.paramDtsUltraTvSymMode);
    data.writeInt32(advancedSoundParameterVo.paramDtsUltraTvSymLevel);
    data.writeInt32(advancedSoundParameterVo.paramDtsUltraTvSymReset);

    data.writeInt32(advancedSoundParameterVo.paramAudysseyDynamicVolCompressMode);
    data.writeInt32(advancedSoundParameterVo.paramAudysseyDynamicVolGc);
    data.writeInt32(advancedSoundParameterVo.paramAudysseyDynamicVolVolSetting);
    data.writeInt32(advancedSoundParameterVo.paramAudysseyDynamicEqEqOffset);
    data.writeInt32(advancedSoundParameterVo.paramAudysseyAbxGwet);
    data.writeInt32(advancedSoundParameterVo.paramAudysseyAbxGdry);
    data.writeInt32(advancedSoundParameterVo.paramAudysseyAbxFilset);

    data.writeInt32(advancedSoundParameterVo.paramSrsTheaterSoundTshdInputGain);
    data.writeInt32(advancedSoundParameterVo.paramSrsTheaterSoundTshdutputGain);
    data.writeInt32(advancedSoundParameterVo.paramSrsTheaterSoundSurrLevelControl);
    data.writeInt32(advancedSoundParameterVo.paramSrsTheaterSoundTrubassCompressorControl);
    data.writeInt32(advancedSoundParameterVo.paramSrsTheaterSoundTrubassProcessMode);
    data.writeInt32(advancedSoundParameterVo.paramSrsTheaterSoundTrubassSpeakerAudio);
    data.writeInt32(advancedSoundParameterVo.paramSrsTheaterSoundTrubassSpeakerAnalysis);

    remote()->transact(AUDIOMANAGER_SET_ADV_SOUND_EFFECT, data, &reply);
    return reply.readInt32();
}

int32_t BpAudioManager::getAdvancedSoundEffect(int32_t advancedSoundParamType)
{
    ALOGV("Send AUDIOMANAGER_GET_ADV_SOUND_EFFECT\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(advancedSoundParamType);
    remote()->transact(AUDIOMANAGER_GET_ADV_SOUND_EFFECT, data, &reply);
    return reply.readInt32();
}

int16_t BpAudioManager::setSubWooferVolume(bool mute, int16_t value)
{
    ALOGV("Send AUDIOMANAGER_SET_SUB_WOOFER_VOLUME\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(mute);
    data.writeInt32(value);
    remote()->transact(AUDIOMANAGER_SET_SUB_WOOFER_VOLUME, data, &reply);
    return static_cast<int16_t>(reply.readInt32());
}

uint8_t BpAudioManager::setSoundParameter(const int32_t Type, const int16_t param1, const int16_t param2)
{
    ALOGV("Send AUDIOMANAGER_SOUND_SET_PARAM\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(Type);
    data.writeInt32(param1);
    data.writeInt32(param2);
    remote()->transact(AUDIOMANAGER_SOUND_SET_PARAM, data, &reply);
    return static_cast<uint8_t>(reply.readInt32());
}

int16_t BpAudioManager::getSoundParameter(const int32_t Type, const int16_t param1)
{
    ALOGV("Send AUDIOMANAGER_SOUND_GET_PARAM\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(Type);
    data.writeInt32(param1);
    remote()->transact(AUDIOMANAGER_SOUND_GET_PARAM, data, &reply);
    return static_cast<int16_t>(reply.readInt32());
}

uint8_t BpAudioManager::setSpeakerDelay(uint32_t delay)
{
    ALOGV("Send AUDIOMANAGER_SET_SOUND_SPEAKER_DELAY\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(delay);
    remote()->transact(AUDIOMANAGER_SET_SOUND_SPEAKER_DELAY, data, &reply);
    return static_cast<uint8_t>(reply.readInt32());
}

uint8_t BpAudioManager::setSpdifDelay(uint32_t delay)
{
    ALOGV("Send AUDIOMANAGER_SET_SOUND_SPDIF_DELAY\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(delay);
    remote()->transact(AUDIOMANAGER_SET_SOUND_SPDIF_DELAY, data, &reply);
    return static_cast<uint32_t>(reply.readInt32());
}

uint8_t BpAudioManager::setKtvMixModeVolume(int32_t VolType,  uint8_t u8Vol1, uint8_t u8Vol2)
{
    ALOGV("Send AUDIOMANAGER_KTV_SET_MIX_MODE_VOLUME\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(VolType);
    data.writeInt32(u8Vol1);
    data.writeInt32(u8Vol2);
    remote()->transact(AUDIOMANAGER_KTV_SET_MIX_MODE_VOLUME, data, &reply);
    return static_cast<uint8_t>(reply.readInt32());
}

uint8_t BpAudioManager::enableKtvMixModeMute(int32_t VolType)
{
    ALOGV("Send AUDIOMANAGER_ENABLE_KTV_MIX_MODE_MUTE\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(VolType);
    remote()->transact(AUDIOMANAGER_ENABLE_KTV_MIX_MODE_MUTE, data, &reply);
    return static_cast<uint8_t>(reply.readInt32());
}

uint8_t BpAudioManager::disableKtvMixModeMute(int32_t VolType)
{
    ALOGV("Send AUDIOMANAGER_DISABLE_KTV_MIX_MODE_MUTE\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(VolType);
    remote()->transact(AUDIOMANAGER_DISABLE_KTV_MIX_MODE_MUTE, data, &reply);
    return static_cast<uint8_t>(reply.readInt32());
}

int16_t BpAudioManager::setAudioCaptureSource(int32_t audioDeviceType, int32_t audioType)
{
    ALOGV("Send AUDIOMANAGER_SET_AUDIO_CAPTURE_SOURCE\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(audioDeviceType);
    data.writeInt32(audioType);
    remote()->transact(AUDIOMANAGER_SET_AUDIO_CAPTURE_SOURCE, data, &reply);
    return static_cast<int16_t>(reply.readInt32());
}

int16_t BpAudioManager::setOutputSourceInfo(int32_t eAudioPath,int32_t eSource)
{
	ALOGV("Send AUDIOMANAGER_SET_OUTPUT_SOURCE_INFO\n");
	Parcel data, reply ;
	data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
	data.writeInt32(eAudioPath);
	data.writeInt32(eSource);
	
	remote()->transact(AUDIOMANAGER_SET_OUTPUT_SOURCE_INFO,data, &reply);
	return static_cast<int16_t>(reply.readInt32());
}

int16_t BpAudioManager::setKtvSoundInfo(int32_t ektvInfoType, int32_t param1, int32_t param2)
{
    ALOGV("Send AUDIOMANAGER_SET_KTV_SOUND_INFO\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(ektvInfoType);
    data.writeInt32(param1);
    data.writeInt32(param2);
    remote()->transact(AUDIOMANAGER_SET_KTV_SOUND_INFO, data, &reply);
    return static_cast<int16_t>(reply.readInt32());
}

int32_t BpAudioManager::getKtvSoundInfo(int32_t ektvInfoType)
{
    ALOGV("Send AUDIOMANAGER_GET_KTV_SOUND_INFO\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(ektvInfoType);
    remote()->transact(AUDIOMANAGER_GET_KTV_SOUND_INFO, data, &reply);
    return reply.readInt32();
}

int32_t BpAudioManager::setKtvSoundTrack(int32_t enSoundMode)
{
    ALOGV("Send AUDIOMANAGER_SET_KTV_SOUND_TRACK\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(enSoundMode);
    remote()->transact(AUDIOMANAGER_SET_KTV_SOUND_TRACK, data, &reply);
    return reply.readInt32();
}

bool BpAudioManager::setCommonAudioInfo(int32_t eInfoType, int32_t param1, int32_t param2)
{
    ALOGV("Send AUDIOMANAGER_SET_COMMON_AUDIO_INFO\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(eInfoType);
    data.writeInt32(param1);
    data.writeInt32(param2);
    remote()->transact(AUDIOMANAGER_SET_COMMON_AUDIO_INFO, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

void BpAudioManager::setAudioLanguage1(int32_t enLanguage)
{
    ALOGV("Send AUDIOMANAGER_SET_AUDIO_LANGUAGE1\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(enLanguage);
    remote()->transact(AUDIOMANAGER_SET_AUDIO_LANGUAGE1, data, &reply);
}

void BpAudioManager::setAudioLanguage2(int32_t enLanguage)
{
    ALOGV("Send AUDIOMANAGER_SET_AUDIO_LANGUAGE2\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(enLanguage);
    remote()->transact(AUDIOMANAGER_SET_AUDIO_LANGUAGE2, data, &reply);
}

int32_t BpAudioManager::getAudioLanguage1()
{
    ALOGV("Send AUDIOMANAGER_GET_AUDIO_LANGUAGE1\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    remote()->transact(AUDIOMANAGER_GET_AUDIO_LANGUAGE1, data, &reply);
    return reply.readInt32();
}

int32_t BpAudioManager::getAudioLanguage2()
{
    ALOGV("Send AUDIOMANAGER_GET_AUDIO_LANGUAGE2\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    remote()->transact(AUDIOMANAGER_GET_AUDIO_LANGUAGE2, data, &reply);
    return reply.readInt32();
}

int32_t BpAudioManager::setAudioSource(int32_t eInputSrc, int32_t eAudioProcessType)
{
    ALOGV("Send AUDIOMANAGER_SET_AUDIO_SOURCE\n");
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(eInputSrc);
    data.writeInt32(eAudioProcessType);
    remote()->transact(AUDIOMANAGER_SET_AUDIO_SOURCE, data, &reply);
    return reply.readInt32();
}
uint8_t BpAudioManager::SetSNDDTSInfo(int32_t infoType,uint32_t param1,uint32_t param2)
{
    ALOGV(" Send AUDIOMANAGER_SET_DTS_INFO \n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(infoType);
    data.writeInt32(param1);
    data.writeInt32(param2);
    remote()->transact(AUDIOMANAGER_SET_DTS_INFO, data, &reply);

    return static_cast<uint8_t>(reply.readInt32());

}
uint32_t BpAudioManager::GetSNDAACInfo(int32_t infoType)
{
    ALOGV(" Send AUDIOMANAGER_GET_AAC_INFO \n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(infoType);
    remote()->transact(AUDIOMANAGER_GET_AAC_INFO, data, &reply);

    return reply.readInt32();

}
uint8_t BpAudioManager::SetSNDAACInfo(int32_t infoType,uint32_t param1,uint32_t param2)
{
    ALOGV(" Send AUDIOMANAGER_SET_AAC_INFO \n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(infoType);
    data.writeInt32(param1);
    data.writeInt32(param2);

    remote()->transact(AUDIOMANAGER_SET_AAC_INFO, data, &reply);

    return static_cast<uint8_t>(reply.readInt32());

}
uint32_t BpAudioManager::GetSNDAC3Info(int32_t infoType)
{
    ALOGV(" Send AUDIOMANAGER_GET_AC3_INFO \n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(infoType);

    remote()->transact(AUDIOMANAGER_GET_AC3_INFO, data, &reply);

    return reply.readInt32();

}
uint8_t BpAudioManager::SetSNDAC3Info(int32_t infoType,uint32_t param1,uint32_t param2)
{
    ALOGV(" Send AUDIOMANAGER_SET_AC3_INFO \n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(infoType);
    data.writeInt32(param1);
    data.writeInt32(param2);

    remote()->transact(AUDIOMANAGER_SET_AC3_INFO, data, &reply);

    return static_cast<uint8_t>(reply.readInt32());

}
uint32_t BpAudioManager::GetSNDAC3PInfo(int32_t infoType)
{
    ALOGV(" Send AUDIOMANAGER_GET_AC3P_INFO \n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(infoType);

    remote()->transact(AUDIOMANAGER_GET_AC3P_INFO, data, &reply);

    return reply.readInt32();

}
uint8_t BpAudioManager::SetSNDAC3PInfo(int32_t infoType,uint32_t param1,uint32_t param2)
{
    ALOGV(" Send AUDIOMANAGER_SET_AC3P_INFO \n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(infoType);
    data.writeInt32(param1);
    data.writeInt32(param2);

    remote()->transact(AUDIOMANAGER_SET_AC3P_INFO, data, &reply);

    return static_cast<uint8_t>(reply.readInt32());

}
uint32_t BpAudioManager::GetSNDMpegInfo(int32_t infoType)
{
    ALOGV(" Send AUDIOMANAGER_GET_MPEG_INFO \n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(infoType);

    remote()->transact(AUDIOMANAGER_GET_MPEG_INFO, data, &reply);

    return reply.readInt32();

}
uint32_t BpAudioManager::GetMpegFrameCnt(void)
{
    ALOGV(" Send AUDIOMANAGER_GET_MPEG_INFO \n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    //data.writeInt32(infoType);

    remote()->transact(AUDIOMANAGER_GET_FRAMECNT, data, &reply);

    return reply.readInt32();

}
uint64_t BpAudioManager::GetAudioCommInfo(int32_t infoType)
{
    ALOGV(" Send AUDIOMANAGER_GET_GETCOMMINFO \n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(infoType);

    remote()->transact(AUDIOMANAGER_GET_GETCOMMINFO, data, &reply);

    //return static_cast<uint64_t>reply.readInt32();

    return reply.readInt64();

}
//refine the flow
uint8_t BpAudioManager::CheckInputRequest(uint32_t *pU32WrtAddr, uint32_t *pU32WrtBytes,uint8_t bCheck)
{
    ALOGV(" Send AUDIOMANAGER_CHECK_INPUTREQUEST \n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32((uint32_t)bCheck);

    if(bCheck == 0)//really need copy data
    {
        data.writeInt32(*pU32WrtBytes);
        sp<MemoryHeapBase> heap;
        sp<MemoryBase> buffer;
        void *ptr;

        heap = new MemoryHeapBase(*pU32WrtBytes);

        buffer = new MemoryBase(heap, 0, *pU32WrtBytes);
        ptr = heap->getBase();

        memcpy((uint8_t*)ptr, (uint8_t*)pU32WrtAddr, *pU32WrtBytes);

        data.writeStrongBinder(buffer->asBinder());

    }
    remote()->transact(AUDIOMANAGER_CHECK_INPUTREQUEST, data, &reply);

    return static_cast<uint8_t>(reply.readInt32());
}
void BpAudioManager::SetInput(void)
{
    ALOGV(" Send AUDIOMANAGER_SETINPUT \n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());

    remote()->transact(AUDIOMANAGER_SETINPUT, data, &reply);

}
void BpAudioManager::StartDecode(void)
{
    ALOGV(" Send AUDIOMANAGER_START_DECODE \n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());

    remote()->transact(AUDIOMANAGER_START_DECODE, data, &reply);


}
void BpAudioManager::StopDecode(void)
{
    ALOGV(" Send AUDIOMANAGER_STOP_DECODE \n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());

    remote()->transact(AUDIOMANAGER_STOP_DECODE, data, &reply);

}
void BpAudioManager::SwitchAudioDSPSystem(int32_t eAudioDSPSystem)
{
    ALOGV(" Send AUDIOMANAGER_STOP_DECODE \n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(eAudioDSPSystem);

    remote()->transact(AUDIOMANAGER_SWITCH_DSPSYSTEM, data, &reply);

}
void BpAudioManager::setAudioSpidifOutPut(int32_t eSpidif_output)
{
    ALOGV(" Send setAudioSpidifOutPut \n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(eSpidif_output);

    remote()->transact(AUDIOMANAGER_SET_SPIDIF_OUTPUT, data, &reply);
}
void BpAudioManager::setAudioHDMIOutPut(int32_t eHdmi_putput)
{
    ALOGV(" Send setAudioSpidifOutPut \n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(eHdmi_putput);

    remote()->transact(AUDIOMANAGER_SET_HDMI_OUTPUT, data, &reply);
}
void BpAudioManager::setAudioHDMITx_HDBypass(bool bByPassOnOff)
{
    ALOGV(" Send AUDIOMANAGER_SET_HDMITX_HDBYPASS \n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(bByPassOnOff);
    remote()->transact(AUDIOMANAGER_SET_HDMITX_HDBYPASS, data, &reply);
}

bool BpAudioManager::getAudioHDMITx_HDBypass()
{
    ALOGV(" Send AUDIOMANAGER_GET_HDMITX_HDBYPASS \n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    remote()->transact(AUDIOMANAGER_GET_HDMITX_HDBYPASS, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

bool BpAudioManager::getAudioHDMITx_HDBypass_Capability()
{
    ALOGV(" Send AUDIOMANAGER_GET_HDMITX_HDBYPASS_CAP \n");

    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    remote()->transact(AUDIOMANAGER_GET_HDMITX_HDBYPASS_CAP, data, &reply);
    return static_cast<bool>(reply.readInt32());
}
// EosTek Patch Begin
void BpAudioManager::setMicSSound(int8_t value)
{
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(value);
    remote()->transact(AUDIOMANAGER_SET_MIC_VAL, data, &reply);
}

void BpAudioManager::setMicEcho(int8_t value)
{
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    data.writeInt32(value);
    remote()->transact(AUDIOMANAGER_SET_MIC_ECHO, data, &reply);
}

uint8_t BpAudioManager::getMicSSound()
{
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    //data.writeInt32(value);
    remote()->transact(AUDIOMANAGER_GET_MIC_VAL, data, &reply);
    return reply.readInt32();
}

uint8_t BpAudioManager::getMicEcho()
{
    Parcel data, reply;
    data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
    // data.writeInt32(value);
    remote()->transact(AUDIOMANAGER_GET_MIC_ECHO, data, &reply);
    return reply.readInt32();
}
// EosTek Patch End

 /*add by owen.qin begin*/
void BpAudioManager::setADEnable(bool enable)
{
	printf("BpAudioManager::setADEnable(bool enable)\n");
	Parcel data,reply;
	data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
	data.writeInt32(enable);
	remote()->transact(AUDIOMANAGER_SET_AD_ENABLE,data,&reply);
}
void BpAudioManager::setADAbsoluteVolume(int32_t value)
{
	printf("BpAudioManager::setADAbsoluteVolume\n");
	Parcel data,reply;
	data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
	data.writeInt32(value);
	remote()->transact(AUDIOMANAGER_SET_AD_ABSOLUTE_VOLUME,data,&reply);
}
/*add by owen.qin end*/ 

/*add by owen.qin begin*/
void BpAudioManager::setAutoHOHEnable(bool enable)
{
	printf("BpAudioManager::setAutoHOHEnable\n");
	Parcel data,reply;
	data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
	data.writeInt32(enable);
	remote()->transact(AUDIOMANAGER_SET_HOH_STATUS,data,&reply);
}
/*add by owen.qin end*/
void BpAudioManager::setAutoVolume(bool enAutoVol)
{
	printf("BpAudioManager::setAutoVolume\n");
	Parcel data,reply;
	data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
	data.writeInt32(enAutoVol);
	remote()->transact(AUDIOMANAGER_SET_AUTO_VOLUME,data,&reply);
}

bool BpAudioManager::getAutoVolume()
{
	printf("BpAudioManager::getAutoVolume\n");
	Parcel data,reply;
	data.writeInterfaceToken(IAudioManager::getInterfaceDescriptor());
	remote()->transact(AUDIOMANAGER_GET_AUTO_VOLUME,data,&reply);
       return static_cast<bool>(reply.readInt32());
}

IMPLEMENT_META_INTERFACE(AudioManager, "mstar.IAudioManager");

status_t BnAudioManager::onTransact(uint32_t code,
                                    const Parcel& data,
                                    Parcel* reply,
                                    uint32_t flags)
{
    switch(code)
    {
    case DISCONNECT:
    {
        ALOGV("Receive DISCONNECT\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        disconnect();
        return NO_ERROR;
    }
    break;
    case CHECKATVSOUNDSYSTEM:
    {
        ALOGV("Receive CHECKATVSOUNDSYSTEM\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        reply->writeInt32(checkAtvSoundSystem());
        return NO_ERROR;
    }
    break;
    case SETAUDIOVOLUME:
    {
        ALOGV("Receive SETVOLUMN\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t enSoundPath = data.readInt32();
        int32_t volumn = data.readInt32();
        setAudioVolume(enSoundPath, volumn);
        return NO_ERROR;
    }
    break;
    case GETAUDIOVOLUME:
    {
        ALOGV("Receive GETVOLUME\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t volSrcType = data.readInt32();
        reply->writeInt32(getAudioVolume(volSrcType));
        return NO_ERROR;
    }
    break;
    case SETATVINFO:
    {
        ALOGV("Receive SETATVINFO\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t infoType = data.readInt32();
        int32_t config = data.readInt32();
        reply->writeInt32(setAtvInfo(infoType, config));
        return NO_ERROR;
    }
    break;
    case GETATVINFO:
    {
        ALOGV("Receive GETATVINFO\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        reply->writeInt32(getAtvInfo());
        return NO_ERROR;
    }
    break;
    case SETAUDIOOUTPUT:
    {
        ALOGV("Receive SETAUDIOOUTPUT\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t outputType = data.readInt32();
        AudioOutParameter param;
        param.spdifDelayTime = data.readInt32();
        param.speakerDelayTime = data.readInt32();
        param.spdifOutmodInUi = data.readInt32();
        param.spdifOutmodActive = data.readInt32();
        reply->writeInt32(setAudioOutput(outputType, param));
        return NO_ERROR;
    }
    break;
    case ENABLEBASICSOUNDEFFECT:
    {
        ALOGV("Receive ENABLEBASICSOUNDEFFECT\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t soundType = data.readInt32();
        bool enable = static_cast<bool>(data.readInt32());
        reply->writeInt32(enableBasicSoundEffect(soundType, enable));
        return NO_ERROR;
    }
    break;
#if 0
    case ENABLEAUTOVOLUME:
    {
        ALOGV("Receive ENABLEAUTOVOLUME\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        enableAutoVolume();
        return NO_ERROR;
    }
    break;
    case DISABLEAUTOVOLUME:
    {
        ALOGV("Receive DIABLEAUTOVOLUME\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        disableAutoVolume();
        return NO_ERROR;
    }
    break;
    case ISAUTOVOLUMEENABLED:
    {
        ALOGV("Receive ISAUTOVOLUMEENABLED\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        reply->writeInt32(isAutoVolumeEnabled());
        return NO_ERROR;
    }
    break;
#endif
    case ENABLEMUTE:
    {
        ALOGV("Receive ENABLEMUTE\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t enMuteType = data.readInt32();
        reply->writeInt32(enableMute(enMuteType));
        return NO_ERROR;
    }
    break;
    case DISABLEMUTE:
    {
        ALOGV("Receive DISABLEMUTE\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t enMuteType = data.readInt32();
        reply->writeInt32(disableMute(enMuteType));
        return NO_ERROR;
    }
    break;
    case ISMUTEEABLE:
    {
        ALOGV("Receive ISMUTE\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t enMuteType = data.readInt32();
        reply->writeInt32(isMuteEnabled(enMuteType));
        return NO_ERROR;
    }
    break;
    case SETATVMTSMODE:
    {
        ALOGV("Receive SETATVMTSMODE\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t mode = data.readInt32();
        reply->writeInt32(setAtvMtsMode(mode));
        return NO_ERROR;
    }
    break;
    case GETATVMTSMODE:
    {
        ALOGV("Receive GETATVMTSMODE\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        reply->writeInt32(getAtvMtsMode());
        return NO_ERROR;
    }
    break;
    case SETTONEXTATVMTSMODE:
    {
        ALOGV("Receive SETTONEXTATVMTSMODE\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        reply->writeInt32(setToNextAtvMtsMode());
        return NO_ERROR;
    }
    break;
    case SETATVSOUNDSYSTEM:
    {
        ALOGV("Receive SETATVSOUNDSYSTEM\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t mode = data.readInt32();
        reply->writeInt32(setAtvSoundSystem(mode));
        return NO_ERROR;
    }
    break;
    case GETATVSOUNDSYSTEM:
    {
        ALOGV("Receive GETATVSOUNDSYSTEM\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        reply->writeInt32(getAtvSoundSystem());
        return NO_ERROR;
    }
    break;
    case SETDTVOUTPUTMODE:
    {
        ALOGV("Receive SETDTVOUTPUTMODE\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t mode = data.readInt32();
        setDtvOutputMode(mode);
        return NO_ERROR;
    }
    break;
    case GETDTVOUTPUTMODE:
    {
        ALOGV("Receive GETDTVOUTPUTMODE\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        reply->writeInt32(getDtvOutputMode());
        return NO_ERROR;
    }
    break;
    case SETBASICSOUNDEFFECT:
    {
        ALOGV("Receive SETBASICSOUNDEFFECT\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        DtvSoundEffect ef;
        int32_t soundEffectType = data.readInt32();
        ef.preScale = data.readInt32();
        ef.treble  = data.readInt32();
        ef.bass = data.readInt32();
        ef.balance = data.readInt32();
        ef.eqBandNumber = data.readInt32();
        ef.peqBandNumber = data.readInt32();
        ef.avcThreshold = data.readInt32();
        ef.avcAttachTime = data.readInt32();
        ef.avcReleaseTime = data.readInt32();
        ef.surroundXaValue = data.readInt32();
        ef.surroundXbValue = data.readInt32();
        ef.surroundXkValue = data.readInt32();
        ef.soundDrcThreshold = data.readInt32();
        ef.noiseReductionThreshold = data.readInt32();
        ef.echoTime = data.readInt32();
        int i;
        for(i =0; i < MAXEQNAD; i ++)
        {
            ef.soundParameterEqs[i].eqLevel = data.readInt32();
        }

        for(i =0; i < MAXPEQNAD; i ++)
        {
            ef.soundParameterPeqs[i].peqGain = data.readInt32();
            ef.soundParameterPeqs[i].peqGc = data.readInt32();
            ef.soundParameterPeqs[i].peqQvalue = data.readInt32();
        }

        reply->writeInt32(setBasicSoundEffect(soundEffectType,ef));
        return NO_ERROR;
    }
    break;
    case GETBASICSOUNDEFFECT:
    {
        ALOGV("Receive GETBASICSOUNDEFFECT\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t effecttype = data.readInt32();
        reply->writeInt32(getBasicSoundEffect(effecttype));
        return NO_ERROR;
    }
    break;
    case SETINPUTLEVEL:
    {
        ALOGV("Receive SETINPUTLEVEL\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t src = data.readInt32();
        int32_t level = data.readInt32();

        setInputLevel(src,level);
        return NO_ERROR;
    }
    break;
    case GETINPUTLEVEL:
    {
        ALOGV("Receive GETINPUTLEVEL\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t enAudioInputSource = data.readInt32();
        reply->writeInt32(getInputLevel(enAudioInputSource));
        return NO_ERROR;
    }
    break;
//        case MUTEINPUT:
//        {
//            ALOGV("Receive MUTEINPUT\n");
//            CHECK_INTERFACE(IAudioManager, data, reply);
//            int32_t src = data.readInt32();
//            muteInput(src);
//            return NO_ERROR;
//        } break;

    case SETDIGITALOUT:
    {
        ALOGV("Receive SETDIGITALOUT\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t mode = data.readInt32();
        setDigitalOut(mode);
        return NO_ERROR;
    }
    break;
    case SETINPUTSOURCE:
    {
        ALOGV("Receive SETINPUTSOURCE\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t src = data.readInt32();
        setInputSource(src);
        return NO_ERROR;
    }
    break;
    case GETINPUTSOURCE:
    {
        ALOGV("Receive GETINPUTSOURCE\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        reply->writeInt32(getInputSource());
        return NO_ERROR;
    }
    break;
    case AUDIOMANAGER_SET_AUDIO_MUTE:
    {
        printf("Receive AUDIOMANAGER_SET_AUDIO_MUTE\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t screenUnMuteTime = data.readInt32();
        int32_t eSrcType= data.readInt32();
        reply->writeInt32(setMuteStatus(screenUnMuteTime, eSrcType));
        return NO_ERROR;
    }
    break;

    case GETATVSOUNDMODE:
    {
        ALOGV("Receive GETATVSOUNDMODE\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        reply->writeInt32(getAtvSoundMode());
        return NO_ERROR;
    }
    break;

    case SETDEBUGMODE:
    {
        ALOGV("Receive SETDEBUGMODE\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        bool mode = static_cast<bool>(data.readInt32());
        setDebugMode(mode);
        return NO_ERROR;
    }
    break;

    case AUDIOMANAGER_EXECUTE_AMPLIFIER_EXTENED_COMMAND:
    {
        ALOGV("Receive AUDIOMANAGER_EXECUTE_AMPLIFIER_EXTENED_COMMAND\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int8_t subCmd = data.readInt32();
        int32_t param1 = data.readInt32();
        int32_t param2 = data.readInt32();
        int32_t param3Size = data.readInt32();
        void *param3 = (void*)malloc(param3Size*sizeof(int32_t));
        data.read(param3, param3Size*sizeof(int32_t));
        reply->writeInt32( exectueAmplifierExtendedCommand(subCmd, param1, param2, param3, param3Size) );
        free(param3);
        return NO_ERROR;
    }
    break;

    case AUDIOMANAGER_SET_AMPLIFIER_MUTE:
    {
        ALOGV("Receive AUDIOMANAGER_SET_AMPLIFIER_MUTE\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        bool bmute = data.readInt32();
        reply->writeInt32( setAmplifierMute(bmute) );
        return NO_ERROR;
    }
    break;

    case AUDIOMANAGER_SET_AMPLIFIER_EQUALIZER_BYMODE:
    {
        ALOGV("Receive AUDIOMANAGER_SET_AMPLIFIER_EQUALIZER_BYMODE\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t equalizertype = data.readInt32();
        setAmplifierEqualizerByMode(equalizertype);
        return NO_ERROR;
    }
    break;

    case AUDIOMANAGER_ENABLE_ADV_SOUND_EFFECT:
    {
        ALOGV("Receive AUDIOMANAGER_ENABLE_ADV_SOUND_EFFECT\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t soundType = data.readInt32();
        int32_t subProcessType = data.readInt32();
        reply->writeInt32( enableAdvancedSoundEffect(soundType, subProcessType) );
        return NO_ERROR;
    }
    break;

    case AUDIOMANAGER_SET_ADV_SOUND_EFFECT:
    {
        ALOGV("Receive AUDIOMANAGER_SET_ADV_SOUND_EFFECT\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t advancedSoundParamType = data.readInt32();

        AdvancedSoundParam advancedSoundParameterVo;
        advancedSoundParameterVo.paramDolbyPl2vdpkSmod = data.readInt32();
        advancedSoundParameterVo.paramDolbyPl2vdpkWmod = data.readInt32();
        advancedSoundParameterVo.paramSrsTsxtSetInputGain = data.readInt32();
        advancedSoundParameterVo.paramSrsTsxtSetDcGain = data.readInt32();
        advancedSoundParameterVo.paramSrsTsxtSetTrubassGain = data.readInt32();
        advancedSoundParameterVo.paramSrsTsxtSetSpeakerSize = data.readInt32();
        advancedSoundParameterVo.paramSrsTsxtSetInputMode = data.readInt32();
        advancedSoundParameterVo.paramSrsTsxtSetOutputGain = data.readInt32();

        advancedSoundParameterVo.paramSrsTshdSetInputMode = data.readInt32();
        advancedSoundParameterVo.paramSrsTshdSetOutputMode = data.readInt32();
        advancedSoundParameterVo.paramSrsTshdSetSpeakerSize = data.readInt32();
        advancedSoundParameterVo.paramSrsTshdSetTrubassControl = data.readInt32();
        advancedSoundParameterVo.paramSrsTshdSetDefinitionControl = data.readInt32();
        advancedSoundParameterVo.paramSrsTshdSetDcControl = data.readInt32();
        advancedSoundParameterVo.paramSrsTshdSetSurroundLevel = data.readInt32();
        advancedSoundParameterVo.paramSrsTshdSetInputGain = data.readInt32();
        advancedSoundParameterVo.paramSrsTshdSetWowSpaceControl = data.readInt32();
        advancedSoundParameterVo.paramSrsTshdSetWowCenterControl = data.readInt32();
        advancedSoundParameterVo.paramSrsTshdSetWowHdSrs3dMode = data.readInt32();
        advancedSoundParameterVo.paramSrsTshdSetLimiterControl = data.readInt32();
        advancedSoundParameterVo.paramSrsTshdSetOutputGain = data.readInt32();

        advancedSoundParameterVo.paramSrsTheaterSoundInputGain = data.readInt32();
        advancedSoundParameterVo.paramSrsTheaterSoundDefinitionControl = data.readInt32();
        advancedSoundParameterVo.paramSrsTheaterSoundDcControl = data.readInt32();
        advancedSoundParameterVo.paramSrsTheaterSoundTrubassControl = data.readInt32();
        advancedSoundParameterVo.paramSrsTheaterSoundSpeakerSize = data.readInt32();
        advancedSoundParameterVo.paramSrsTheaterSoundHardLimiterLevel = data.readInt32();
        advancedSoundParameterVo.paramSrsTheaterSoundHardLimiterBoostGain = data.readInt32();
        advancedSoundParameterVo.paramSrsTheaterSoundHeadRoomGain = data.readInt32();
        advancedSoundParameterVo.paramSrsTheaterSoundTruVolumeMode = data.readInt32();
        advancedSoundParameterVo.paramSrsTheaterSoundTruVolumeRefLevel = data.readInt32();
        advancedSoundParameterVo.paramSrsTheaterSoundTruVolumeMaxGain = data.readInt32();
        advancedSoundParameterVo.paramSrsTheaterSoundTruVolumeNoiseMngrThld = data.readInt32();
        advancedSoundParameterVo.paramSrsTheaterSoundTruVolumeCalibrate = data.readInt32();
        advancedSoundParameterVo.paramSrsTheaterSoundTruVolumeInputGain = data.readInt32();
        advancedSoundParameterVo.paramSrsTheaterSoundTruVolumeOutputGain = data.readInt32();
        advancedSoundParameterVo.paramSrsTheaterSoundHpfFc = data.readInt32();

        advancedSoundParameterVo.paramDtsUltraTvEvoMonoInput = data.readInt32();
        advancedSoundParameterVo.paramDtsUltraTvEvoWideningon = data.readInt32();
        advancedSoundParameterVo.paramDtsUltraTvEvoAdd3dBon = data.readInt32();
        advancedSoundParameterVo.paramDtsUltraTvEvoPceLevel = data.readInt32();
        advancedSoundParameterVo.paramDtsUltraTvEvoVlfeLevel = data.readInt32();
        advancedSoundParameterVo.paramDtsUltraTvSymDefault = data.readInt32();
        advancedSoundParameterVo.paramDtsUltraTvSymMode = data.readInt32();
        advancedSoundParameterVo.paramDtsUltraTvSymLevel = data.readInt32();
        advancedSoundParameterVo.paramDtsUltraTvSymReset = data.readInt32();

        advancedSoundParameterVo.paramAudysseyDynamicVolCompressMode = data.readInt32();
        advancedSoundParameterVo.paramAudysseyDynamicVolGc = data.readInt32();
        advancedSoundParameterVo.paramAudysseyDynamicVolVolSetting = data.readInt32();
        advancedSoundParameterVo.paramAudysseyDynamicEqEqOffset = data.readInt32();
        advancedSoundParameterVo.paramAudysseyAbxGwet = data.readInt32();
        advancedSoundParameterVo.paramAudysseyAbxGdry = data.readInt32();
        advancedSoundParameterVo.paramAudysseyAbxFilset = data.readInt32();

        advancedSoundParameterVo.paramSrsTheaterSoundTshdInputGain = data.readInt32();
        advancedSoundParameterVo.paramSrsTheaterSoundTshdutputGain = data.readInt32();
        advancedSoundParameterVo.paramSrsTheaterSoundSurrLevelControl = data.readInt32();
        advancedSoundParameterVo.paramSrsTheaterSoundTrubassCompressorControl = data.readInt32();
        advancedSoundParameterVo.paramSrsTheaterSoundTrubassProcessMode = data.readInt32();
        advancedSoundParameterVo.paramSrsTheaterSoundTrubassSpeakerAudio = data.readInt32();
        advancedSoundParameterVo.paramSrsTheaterSoundTrubassSpeakerAnalysis = data.readInt32();

        reply->writeInt32(setAdvancedSoundEffect(advancedSoundParamType, advancedSoundParameterVo));
        return NO_ERROR;
    }
    break;

    case AUDIOMANAGER_GET_ADV_SOUND_EFFECT:
    {
        ALOGV("Receive AUDIOMANAGER_GET_ADV_SOUND_EFFECT\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t advancedSoundParamType = data.readInt32();
        reply->writeInt32( getAdvancedSoundEffect(advancedSoundParamType) );
        return NO_ERROR;
    }
    break;

    case AUDIOMANAGER_SET_SUB_WOOFER_VOLUME:
    {
        ALOGV("Receive AUDIOMANAGER_SET_SUB_WOOFER_VOLUME\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        bool mute = static_cast<bool>(data.readInt32());
        int16_t value = static_cast<int16_t>(data.readInt32());
        reply->writeInt32( setSubWooferVolume(mute, value) );
        return NO_ERROR;
    }
    break;

    case AUDIOMANAGER_SOUND_SET_PARAM:
    {
        ALOGV("Receive AUDIOMANAGER_SOUND_SET_PARAM\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t Type = data.readInt32();
        int16_t param1 = static_cast<int16_t>(data.readInt32());
        int16_t param2 = static_cast<int16_t>(data.readInt32());
        reply->writeInt32( setSoundParameter(Type, param1, param2) );
        return NO_ERROR;
    }
    break;

    case AUDIOMANAGER_SOUND_GET_PARAM:
    {
        ALOGV("Receive AUDIOMANAGER_SOUND_GET_PARAM\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t Type = data.readInt32();
        int16_t param1 = static_cast<int16_t>(data.readInt32());
        reply->writeInt32( getSoundParameter(Type, param1) );
        return NO_ERROR;
    }
    break;

    case AUDIOMANAGER_SET_SOUND_SPEAKER_DELAY:
    {
        ALOGV("Receive AUDIOMANAGER_SET_SOUND_SPEAKER_DELAY\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t delay = data.readInt32();
        reply->writeInt32( setSpeakerDelay(delay) );
        return NO_ERROR;
    }
    break;

    case AUDIOMANAGER_SET_SOUND_SPDIF_DELAY:
    {
        ALOGV("Receive AUDIOMANAGER_SET_SOUND_SPDIF_DELAY\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t delay = data.readInt32();
        reply->writeInt32( setSpdifDelay(delay) );
        return NO_ERROR;
    } break;

    case AUDIOMANAGER_KTV_SET_MIX_MODE_VOLUME:
    {
        ALOGV("Receive AUDIOMANAGER_KTV_SET_MIX_MODE_VOLUME\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t VolType = data.readInt32();
        uint8_t u8Vol1 = static_cast<uint8_t>(data.readInt32());
        uint8_t u8Vol2 = static_cast<uint8_t>(data.readInt32());
        reply->writeInt32( setKtvMixModeVolume(VolType, u8Vol1, u8Vol2) );
        return NO_ERROR;
    }
    break;

    case AUDIOMANAGER_ENABLE_KTV_MIX_MODE_MUTE:
    {
        ALOGV("Receive AUDIOMANAGER_KTV_SET_MIX_MODE_MUTE\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t VolType = data.readInt32();
        reply->writeInt32( enableKtvMixModeMute(VolType) );
        return NO_ERROR;
    }
    break;

    case AUDIOMANAGER_DISABLE_KTV_MIX_MODE_MUTE:
    {
        ALOGV("Receive AUDIOMANAGER_DISABLE_KTV_MIX_MODE_MUTE\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t VolType = data.readInt32();
        reply->writeInt32( disableKtvMixModeMute(VolType) );
        return NO_ERROR;
    }
    break;

    case AUDIOMANAGER_SET_AUDIO_CAPTURE_SOURCE:
    {
        ALOGV("Receive AUDIOMANAGER_SET_AUDIO_CAPTURE_SOURCE\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t audioDeviceType = data.readInt32();
        int32_t audioType = data.readInt32();
        reply->writeInt32( setAudioCaptureSource(audioDeviceType,audioType) );
        return NO_ERROR;
    }
    break;

	case AUDIOMANAGER_SET_OUTPUT_SOURCE_INFO:
	{	
		ALOGV("Receive AUDIOMANAGER_SET_OUTPUT_SOURCE_INFO\n");
		CHECK_INTERFACE(IAudioManager,data, reply);
		int32_t eAudioPath = data.readInt32();
		int32_t eSource = data.readInt32();
		reply->writeInt32(setOutputSourceInfo(eAudioPath,eSource));
		return NO_ERROR;
	}
	break;

    case AUDIOMANAGER_SET_KTV_SOUND_INFO:
    {
        ALOGV("Receive AUDIOMANAGER_SET_KTV_SOUND_INFO\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t ektvInfoType = data.readInt32();
        int32_t param1 = data.readInt32();
        int32_t param2 = data.readInt32();
        reply->writeInt32( setKtvSoundInfo(ektvInfoType, param1, param2) );
        return NO_ERROR;
    }
    break;

    case AUDIOMANAGER_GET_KTV_SOUND_INFO:
    {
        ALOGV("Receive AUDIOMANAGER_GET_KTV_SOUND_INFO\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t ektvInfoType = data.readInt32();
        reply->writeInt32( getKtvSoundInfo(ektvInfoType) );
        return NO_ERROR;
    }
    break;

    case AUDIOMANAGER_SET_KTV_SOUND_TRACK:
    {
        ALOGV("Receive AUDIOMANAGER_SET_KTV_SOUND_TRACK\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t enSoundMode = data.readInt32();
        reply->writeInt32( setKtvSoundTrack(enSoundMode) );
        return NO_ERROR;
    }
    break;

    case AUDIOMANAGER_SET_COMMON_AUDIO_INFO:
    {
        ALOGV("Receive AUDIOMANAGER_SET_COMMON_AUDIO_INFO\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t eInfoType = data.readInt32();
        int32_t param1 = data.readInt32();
        int32_t param2 = data.readInt32();
        reply->writeInt32( setCommonAudioInfo(eInfoType, param1, param2) );
        return NO_ERROR;
    }
    break;

    case AUDIOMANAGER_SET_AUDIO_LANGUAGE1:
    {
        ALOGV("Receive AUDIOMANAGER_SET_AUDIO_LANGUAGE1\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t enLanguage = data.readInt32();
        setAudioLanguage1(enLanguage);
        return NO_ERROR;
    } break;

    case AUDIOMANAGER_SET_AUDIO_LANGUAGE2:
    {
        ALOGV("Receive AUDIOMANAGER_SET_AUDIO_LANGUAGE2\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t enLanguage = data.readInt32();
        setAudioLanguage2(enLanguage);
        return NO_ERROR;
    } break;

    case AUDIOMANAGER_GET_AUDIO_LANGUAGE1:
    {
        ALOGV("Receive AUDIOMANAGER_GET_AUDIO_LANGUAGE1\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        reply->writeInt32(getAudioLanguage1());
        return NO_ERROR;
    } break;

    case AUDIOMANAGER_GET_AUDIO_LANGUAGE2:
    {
        ALOGV("Receive AUDIOMANAGER_GET_AUDIO_LANGUAGE2\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        reply->writeInt32(getAudioLanguage2());
        return NO_ERROR;
    } break;
    case AUDIOMANAGER_SET_AUDIO_SOURCE:
    {
        ALOGV("Receive AUDIOMANAGER_SET_AUDIO_SOURCE\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t eInputSrc = data.readInt32();
        int32_t eAudioProcessType = data.readInt32();
        reply->writeInt32( setAudioSource(eInputSrc, eAudioProcessType) );
        return NO_ERROR;
    }
    break;
    case AUDIOMANAGER_SET_DTS_INFO:
    {
        ALOGV("Receive AUDIOMANAGER_SET_DTS_INFO\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t infotype = data.readInt32();
        uint32_t param1 = static_cast<uint32_t>(data.readInt32());
        uint32_t param2 = static_cast<uint32_t>(data.readInt32());
        reply->writeInt32(SetSNDDTSInfo(infotype,param1,param2));
        return NO_ERROR;
        break;
    }
    case AUDIOMANAGER_GET_AAC_INFO:
    {
        ALOGV("Receive AUDIOMANAGER_GET_AAC_INFO\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t infotype = data.readInt32();
        reply->writeInt32(GetSNDAACInfo(infotype));

        return NO_ERROR;

        break;
    }
    case AUDIOMANAGER_SET_AAC_INFO:
    {
        ALOGV("Receive AUDIOMANAGER_SET_AAC_INFO\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t infotype = data.readInt32();
        uint32_t param1 = static_cast<uint32_t>(data.readInt32());
        uint32_t param2 = static_cast<uint32_t>(data.readInt32());
        reply->writeInt32(SetSNDAACInfo(infotype,param1,param2));

        return NO_ERROR;

        break;
    }
    case AUDIOMANAGER_GET_AC3_INFO:
    {

        ALOGV("Receive AUDIOMANAGER_GET_AC3_INFO\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t infotype = data.readInt32();
        reply->writeInt32(GetSNDAC3Info(infotype));

        return NO_ERROR;
        break;
    }
    case AUDIOMANAGER_SET_AC3_INFO:
    {
        ALOGV("Receive AUDIOMANAGER_SET_AC3_INFO\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t infotype = data.readInt32();
        uint32_t param1 = static_cast<uint32_t>(data.readInt32());
        uint32_t param2 = static_cast<uint32_t>(data.readInt32());
        reply->writeInt32(SetSNDAC3Info(infotype,param1,param2));

        return NO_ERROR;

        break;
    }
    case AUDIOMANAGER_GET_AC3P_INFO:
    {

        ALOGV("Receive AUDIOMANAGER_GET_AC3P_INFO\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t infotype = data.readInt32();
        reply->writeInt32(GetSNDAC3PInfo(infotype));

        return NO_ERROR;
        break;
    }
    case AUDIOMANAGER_SET_AC3P_INFO:
    {
        ALOGV("Receive AUDIOMANAGER_SET_AC3P_INFO\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t infotype = data.readInt32();
        uint32_t param1 = static_cast<uint32_t>(data.readInt32());
        uint32_t param2 = static_cast<uint32_t>(data.readInt32());
        reply->writeInt32(SetSNDAC3PInfo(infotype,param1,param2));

        return NO_ERROR;

        break;
    }
    case AUDIOMANAGER_GET_MPEG_INFO:
    {

        ALOGV("Receive AUDIOMANAGER_GET_MPEG_INFO\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t infotype = data.readInt32();
        reply->writeInt32(GetSNDMpegInfo(infotype));

        return NO_ERROR;

        break;
    }
    case AUDIOMANAGER_GET_FRAMECNT:
    {
        ALOGV("Receive AUDIOMANAGER_GET_FRAMECNT\n");

        CHECK_INTERFACE(IAudioManager, data, reply);

        reply->writeInt32(GetMpegFrameCnt());

        return NO_ERROR;
        break;
    }
    case AUDIOMANAGER_GET_GETCOMMINFO:
    {
        ALOGV("Receive AUDIOMANAGER_GET_GETCOMMINFO\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t infotype = data.readInt32();
        reply->writeInt32(GetAudioCommInfo(infotype));

        return NO_ERROR;

        break;
    }
    case AUDIOMANAGER_CHECK_INPUTREQUEST:
    {
        ALOGV("Receive AUDIOMANAGER_SETINPUT\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        uint8_t bCkeck = data.readInt32();
        if(bCkeck == 0)
        {
            uint32_t size=data.readInt32();
            sp<IMemory> buff = interface_cast<IMemory>(data.readStrongBinder());
            if (buff == NULL)
            {
                printf("FAILED to get IMemory in DmxCopyData\n");
                return -1;
            }
            uint8_t *puBuf = (uint8_t*)(malloc(size));
            if(puBuf == NULL )
            {
                printf("malloc memory failed\n");
                return -1;
            }
            // copy data from binder to user buffer
            memcpy(puBuf, buff->pointer(), size);

            CheckInputRequest((uint32_t*)puBuf, &size,bCkeck);
            free(puBuf);
            puBuf = NULL;

        }

        return NO_ERROR;
        break;
    }
    case AUDIOMANAGER_SETINPUT:
    {
        ALOGV("Receive AUDIOMANAGER_SETINPUT\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        SetInput();

        return NO_ERROR;
        break;
    }
    case AUDIOMANAGER_START_DECODE:
    {

        ALOGV("Receive AUDIOMANAGER_START_DECODE\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        StartDecode();

        return NO_ERROR;

        break;
    }
    case AUDIOMANAGER_STOP_DECODE:
    {

        ALOGV("Receive AUDIOMANAGER_STOP_DECODE\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        StopDecode();

        return NO_ERROR;

        break;
    }
    case AUDIOMANAGER_SWITCH_DSPSYSTEM:
    {
        ALOGV("Receive AUDIOMANAGER_SWITCH_DSPSYSTEM\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t AudioType = data.readInt32();


        SwitchAudioDSPSystem(AudioType);

        return NO_ERROR;
        break;
    }
    case AUDIOMANAGER_SET_SPIDIF_OUTPUT:
    {
        ALOGV("Receive AUDIOMANAGER_SET_SPIDIF_OUTPUT\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t eSpidif_output = data.readInt32();


        setAudioSpidifOutPut(eSpidif_output);

        return NO_ERROR;
        break;
    }
    case AUDIOMANAGER_SET_HDMI_OUTPUT:
    {
        ALOGV("Receive AUDIOMANAGER_SET_HDMI_OUTPUT\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        int32_t eHdmi_putput = data.readInt32();


        setAudioHDMIOutPut(eHdmi_putput);

        return NO_ERROR;
        break;
    }
        /*add by owen.qin begin*/
        case AUDIOMANAGER_SET_AD_ENABLE:
        {
            ALOGV("BnAudioManager::onTransact()->AUDIOMANAGER_SET_AD_ENABLE\n");
            CHECK_INTERFACE(IAudioManager, data, reply);
            bool enable=static_cast<bool>(data.readInt32());
            //todo  ?Ÿå??¯åœ¨AudioManagerä¸­å??
            setADEnable(enable);
            return NO_ERROR;
        
        } break;
        case AUDIOMANAGER_SET_AD_ABSOLUTE_VOLUME:
        {
            ALOGV("BnAudioManager::onTransact()->AUDIOMANAGER_SET_AD_ENABLE\n");
            CHECK_INTERFACE(IAudioManager, data, reply);
            int32_t volume=data.readInt32();
            //todo  ?Ÿå??¯åœ¨AudioManagerä¸­å??
            setADAbsoluteVolume(volume);
            return NO_ERROR;
        
        } break;
        /*add by owen.qin end*/
        
        /*add by owen.qin begin*/
        case AUDIOMANAGER_SET_HOH_STATUS:
        {
        	ALOGV("BnAudioManager::onTransact()-->AUDIOMANAGER_SET_HOH_STATUS\n");
        	CHECK_INTERFACE(IAudioManager, data, reply);
        	bool enable=static_cast<bool>(data.readInt32());
        	setAutoHOHEnable(enable);
        	return NO_ERROR;
        }break;
        /*add by owen.qin end*/
        case AUDIOMANAGER_SET_AUTO_VOLUME:
        {
        	ALOGV("Receive AUDIOMANAGER_SET_AUTO_VOLUME\n");
        	CHECK_INTERFACE(IAudioManager, data, reply);
        	bool enable=static_cast<bool>(data.readInt32());
        	setAutoVolume(enable);
        	return NO_ERROR;
        }break;
        case AUDIOMANAGER_GET_AUTO_VOLUME:
        {
        	ALOGV("Receive AUDIOMANAGER_GET_AUTO_VOLUME");
              reply->writeInt32(getAutoVolume());
        	return NO_ERROR;
        }break;
        case AUDIOMANAGER_SET_HDMITX_HDBYPASS:
        {
            ALOGV("Receive AUDIOMANAGER_SET_HDMITX_HDBYPASS");
            CHECK_INTERFACE(IAudioManager, data, reply);
            bool enable=static_cast<bool>(data.readInt32());
            setAudioHDMITx_HDBypass(enable);
            return NO_ERROR;
        }break;
        case AUDIOMANAGER_GET_HDMITX_HDBYPASS:
        {
            ALOGV("Receive AUDIOMANAGER_GET_HDMITX_HDBYPASS");
            reply->writeInt32(getAudioHDMITx_HDBypass());
            return NO_ERROR;
        }break;
        case AUDIOMANAGER_GET_HDMITX_HDBYPASS_CAP:
        {
            ALOGV("Receive AUDIOMANAGER_GET_HDMITX_HDBYPASS_CAP");
            reply->writeInt32(getAudioHDMITx_HDBypass_Capability());
            return NO_ERROR;
        }break;
            // EosTek Patch Begin
    case AUDIOMANAGER_SET_MIC_VAL:
    {
        ALOGV("Receive AUDIOMANAGER_SET_MIC_VAL\n");
        CHECK_INTERFACE(IAudioManager, data, reply);

        int8_t value = data.readInt32();

        setMicSSound(value);
        return NO_ERROR;
    }
    break;

    case AUDIOMANAGER_SET_MIC_ECHO:
    {
        ALOGV("Receive AUDIOMANAGER_SET_MIC_ECHO\n");
        CHECK_INTERFACE(IAudioManager, data, reply);

        int8_t value = data.readInt32();

        setMicEcho(value);
        return NO_ERROR;
    }
    break;
    case AUDIOMANAGER_GET_MIC_VAL:
    {
        ALOGV("Receive GETATVSOUNDSYSTEM\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        reply->writeInt32(getMicSSound());
        return NO_ERROR;

    }
    break;
    case AUDIOMANAGER_GET_MIC_ECHO:
    {
        ALOGV("Receive AUDIOMANAGER_GET_MIC_ECHO\n");
        CHECK_INTERFACE(IAudioManager, data, reply);
        reply->writeInt32(getMicEcho());
        return NO_ERROR;
    }
    break;
        // EosTek Patch End
    default:
        ALOGV("Receive unknown code(%08x)\n", code);
        return BBinder::onTransact(code, data, reply, flags);
    }
}

