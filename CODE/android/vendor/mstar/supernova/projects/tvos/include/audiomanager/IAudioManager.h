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
#ifndef _IAudioManager_H_
#define _IAudioManager_H_

#include <binder/IInterface.h>
#include <binder/Parcel.h>
#include <binder/IPCThreadState.h>
#include "AudioManagerTypeDefine.h"
using namespace android;

class IAudioManager: public IInterface
{
public:
    DECLARE_META_INTERFACE(AudioManager);

    /*add by owen.qin begin*/
    virtual void setADEnable(bool enable)=0;
    virtual void setADAbsoluteVolume(int32_t value)=0;
    /*add by owen.qin end*/ 
    
    /*add by owen.qin begin*/
    virtual void setAutoHOHEnable(bool enable)=0; 
    /*add by owen.qin end*/

    virtual void disconnect() = 0;

    virtual void setAudioVolume(int32_t enSoundPath,int8_t volumn) = 0;
    virtual int8_t getAudioVolume(int32_t volSrcType) = 0;
    virtual int32_t checkAtvSoundSystem() = 0;
#if 0
    virtual void enableAutoVolume() = 0;
    virtual void disableAutoVolume() = 0;
    virtual bool isAutoVolumeEnabled() = 0;
#endif
    virtual bool enableMute(int32_t enMuteType) = 0;
    virtual bool disableMute(int32_t enMuteType) = 0;
    virtual bool isMuteEnabled(int32_t enMuteType) = 0;

    virtual int32_t setAtvMtsMode(int32_t mode) = 0;
    virtual int32_t getAtvMtsMode() = 0;
    virtual int16_t setToNextAtvMtsMode() = 0;

    virtual bool setAtvSoundSystem(int32_t mode) = 0;
    virtual int32_t getAtvSoundSystem() = 0;

    virtual int32_t setAtvInfo(int32_t infoType, int32_t config) = 0;
    virtual int32_t getAtvInfo() = 0;

    virtual int32_t setAudioOutput(int32_t outputType, AudioOutParameter param) = 0;

    virtual void setDtvOutputMode(int32_t mode) = 0;
    virtual int32_t getDtvOutputMode() = 0;

    virtual bool setMuteStatus(int32_t screenUnMuteTime, int32_t eSrcType) = 0;

    virtual int32_t enableBasicSoundEffect(int32_t soundType, bool enable) = 0;

    virtual int32_t setBasicSoundEffect(int32_t soundEffectType,DtvSoundEffect &ef) = 0;
    virtual int32_t getBasicSoundEffect(int32_t effectType) = 0;

    virtual void setInputLevel(int32_t src,int16_t level) = 0;
    virtual int32_t getInputLevel(int32_t enAudioInputSource) = 0;

    //virtual void muteInput(int32_t src) = 0;

    virtual void setDigitalOut(int32_t mode) = 0;

    virtual void setInputSource(int32_t src) = 0;
    virtual int32_t getInputSource() = 0;

    virtual int32_t getAtvSoundMode() = 0;

    virtual int32_t enableAdvancedSoundEffect(int32_t soundType,int32_t subProcessType) = 0;

    virtual int32_t setAdvancedSoundEffect(int32_t advancedSoundParamType, AdvancedSoundParam advancedSoundParameterVo) = 0;

    virtual int32_t getAdvancedSoundEffect(int32_t advancedSoundParamType) = 0;

    virtual int16_t setSubWooferVolume(bool mute, int16_t value) = 0;

    virtual void setDebugMode(bool mode) = 0;

    virtual int8_t exectueAmplifierExtendedCommand(int8_t subCmd, int32_t param1, int32_t param2, void *param3, int32_t param3Size) = 0;

    virtual bool setAmplifierMute(bool bmute) = 0;

    virtual void setAmplifierEqualizerByMode(int equalizertype) = 0;

    virtual uint8_t setSoundParameter(const int32_t Type, const int16_t param1, const int16_t param2) = 0;

    virtual int16_t getSoundParameter(const int32_t Type, const int16_t param1) = 0;

    virtual uint8_t setSpeakerDelay(uint32_t delay) = 0;

    virtual uint8_t setSpdifDelay(uint32_t delay) = 0;

    virtual uint8_t setKtvMixModeVolume(int32_t VolType,  uint8_t u8Vol1, uint8_t u8Vol2) = 0;

    virtual uint8_t enableKtvMixModeMute(int32_t VolType) = 0;

    virtual uint8_t disableKtvMixModeMute(int32_t VolType) = 0;

    virtual int16_t setAudioCaptureSource(int32_t audioDeviceType, int32_t audioType) = 0;

	virtual	int16_t setOutputSourceInfo(int32_t eAudioPath, int32_t eSource)=0;

    virtual int16_t setKtvSoundInfo(int32_t ektvInfoType, int32_t param1, int32_t param2) = 0;

    virtual int32_t getKtvSoundInfo(int32_t ektvInfoType) = 0;

    virtual int32_t setKtvSoundTrack(int32_t enSoundMode) = 0;

    virtual bool setCommonAudioInfo(int32_t eInfoType, int32_t param1, int32_t param2) = 0;

    virtual int32_t setAudioSource(int32_t eInputSrc, int32_t eAudioProcessType) = 0;


    virtual void setAudioLanguage1(int32_t enLanguage) = 0;
    virtual void setAudioLanguage2(int32_t enLanguage) = 0;
    virtual int32_t getAudioLanguage1() = 0;
    virtual int32_t getAudioLanguage2() = 0;
    virtual void setAutoVolume(bool enAutoVol) = 0;
    virtual bool getAutoVolume() = 0;

    virtual uint8_t SetSNDDTSInfo(int32_t infoType,uint32_t param1,uint32_t param2)=0;

    virtual uint32_t GetSNDAACInfo(int32_t infoType) = 0;

    virtual uint8_t SetSNDAACInfo(int32_t infoType,uint32_t param1,uint32_t param2)=0;

    virtual uint32_t GetSNDAC3Info(int32_t infoType)=0;

    virtual uint8_t SetSNDAC3Info(int32_t infoType,uint32_t param1,uint32_t param2)=0;

    virtual uint32_t GetSNDAC3PInfo(int32_t infoType)=0;

    virtual uint8_t SetSNDAC3PInfo(int32_t infoType,uint32_t param1,uint32_t param2)=0;

    virtual uint32_t GetSNDMpegInfo(int32_t infoType)=0;

    virtual uint32_t GetMpegFrameCnt(void)=0;

    virtual uint64_t GetAudioCommInfo(int32_t infoType)=0;

    virtual uint8_t CheckInputRequest(uint32_t *pU32WrtAddr, uint32_t *pU32WrtBytes,uint8_t bCkeck)  = 0;

    virtual void SetInput(void)=0;

    virtual void StartDecode(void)=0;

    virtual void StopDecode(void)=0;

    virtual void SwitchAudioDSPSystem(int32_t eAudioDSPSystem)=0;

    virtual void setAudioSpidifOutPut(int32_t eSpidif_output)=0;

    virtual void setAudioHDMIOutPut(int32_t eHdmi_putput)=0;
    
    virtual void setAudioHDMITx_HDBypass(bool bByPassOnOff) = 0;

    virtual bool getAudioHDMITx_HDBypass() = 0;

    virtual bool getAudioHDMITx_HDBypass_Capability() = 0;
   // EosTek Patch Begin
    virtual void setMicSSound(int8_t value) = 0;
    virtual void setMicEcho(int8_t value) = 0;
    virtual uint8_t getMicSSound() = 0;
    virtual uint8_t getMicEcho() = 0;
    // EosTek Patch End
};

// ----------------------------------------------------------------------------

class BnAudioManager: public BnInterface<IAudioManager>
{
public:

    virtual status_t onTransact(uint32_t code,
                                const Parcel& data,
                                Parcel* reply,
                                uint32_t flags = 0);

};

#endif // _IAudioManager_H_

