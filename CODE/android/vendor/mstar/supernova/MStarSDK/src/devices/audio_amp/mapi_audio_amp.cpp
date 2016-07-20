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
////////////////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2008-2009 MStar Semiconductor, Inc.
// All rights reserved.
//
// Unless otherwise stipulated in writing, any and all information contained
// herein regardless in any format shall remain the sole proprietary of
// MStar Semiconductor Inc. and be kept in strict confidence
// ("MStar Confidential Information") by the recipient.
// Any unauthorized act including without limitation unauthorized disclosure,
// copying, use, reproduction, sale, distribution, modification, disassembling,
// reverse engineering and compiling of the contents of MStar Confidential
// Information is unlawful and strictly prohibited. MStar hereby reserves the
// rights to any and all damages, losses, costs and expenses resulting therefrom.
//
////////////////////////////////////////////////////////////////////////////////

#include "mapi_audio_amp.h"

//-------------------------------------------------------------------------------------------------
/// Constructor
/// @return None
//-------------------------------------------------------------------------------------------------
mapi_audio_amp::mapi_audio_amp(void)
{
    return;
}

mapi_audio_amp::~mapi_audio_amp()
{
}



//-------------------------------------------------------------------------------------------------
/// To finalize this Audio Amplifier
/// @return             \b OUT: MAPI_TRUE or MAPI_FALSE
//-------------------------------------------------------------------------------------------------
MAPI_BOOL mapi_audio_amp::Finalize(void)
{
    return MAPI_TRUE;
}

//-------------------------------------------------------------------------------------------------
/// To reset this Audio Amplifier
/// @param bEnable	    \b IN: TRUE for enable; FALSE for disable
/// @return             \b OUT: MAPI_TRUE or MAPI_FALSE
//-------------------------------------------------------------------------------------------------
MAPI_BOOL mapi_audio_amp::Reset(MAPI_BOOL bEnable)
{
    return MAPI_TRUE;
}

//-------------------------------------------------------------------------------------------------
/// Mute this Audio Amplifier
/// @param bMute        \b IN: TRUE for Mute; FALSE for unMute
/// @return             \b OUT: MAPI_TRUE or MAPI_FALSE
//-------------------------------------------------------------------------------------------------
MAPI_BOOL mapi_audio_amp::Mute(MAPI_BOOL bMute)
{
    return MAPI_TRUE;
}

//-------------------------------------------------------------------------------------------------
/// To Poweroff this Audio Amplifier
/// @param bEnable      \b IN: TRUE for PowerOff; FALSE for PowerOn
/// @return             \b OUT: MAPI_TRUE or MAPI_FALSE
//-------------------------------------------------------------------------------------------------
MAPI_BOOL mapi_audio_amp::Standby(MAPI_BOOL bEnable)
{
    return MAPI_TRUE;
}
 #if (KARAOKE_ENABLE==1)
 void mapi_audio_amp::MicVol_Set(MAPI_U8 VolValue)
 {

  }
 void mapi_audio_amp::MicEcho_Set(MAPI_U8 EchoValue)
 {

  }
 #endif
//-------------------------------------------------------------------------------------------------
/// Reserve extend command for customer. If you don't need it, you skip it.
/// @param u8SubCmd     \b IN: Commad defined by the customer.
/// @param u32Param1    \b IN: Defined by the customer.
/// @param u32Param2    \b IN: Defined by the customer.
/// @param pvoidParam3    \b IN: Defined by the customer.
/// @return             \b OUT: MAPI_TRUE or MAPI_FALSE
//-------------------------------------------------------------------------------------------------
MAPI_BOOL mapi_audio_amp::ExtendCmd(MAPI_U8 u8SubCmd, MAPI_U32 u32Param1, MAPI_U32 u32Param2, void* pvoidParam3)
{
    return MAPI_TRUE;
}

//-------------------------------------------------------------------------------------------------
/// AmpSwapOutputChannel: Swap Audio Amplifier ouput channel.
/// @param bEnable      \b IN: TRUE for do Swap; FALSE for do nothing.
/// @return             \b OUT: MAPI_TRUE or MAPI_FALSE
//-------------------------------------------------------------------------------------------------
MAPI_BOOL mapi_audio_amp::AmpSwapOutputChannel(MAPI_BOOL bEnable)
{
    return MAPI_TRUE;
}

//-------------------------------------------------------------------------------------------------
/// AmpSetSubWooferVolume:To Set sub woofer volume and mute.
/// @param bMute      \b IN: TRUE for Mute enable; FALSE for Mute disable.
/// @param u8Val      \b IN: Volume Value(0~100), set 0 is mute.
/// @return             \b OUT: MAPI_TRUE or MAPI_FALSE
//-------------------------------------------------------------------------------------------------
MAPI_BOOL mapi_audio_amp::AmpSetSubWooferVolume(MAPI_BOOL bMute, MAPI_U8 u8Val)
{
    return MAPI_TRUE;
}


//-------------------------------------------------------------------------------------------------
/// AMP_Api_AmpAutoDetect:To Auto Detect Audio Amplifier
/// @return             \b OUT: RESULT
//-------------------------------------------------------------------------------------------------
MAPI_U8 mapi_audio_amp::AMP_Api_AmpAutoDetect(void)
{
    return 0;
}

//-------------------------------------------------------------------------------------------------
/// AMP_Api_AmpGetAutoDetectType:To Get Auto Detect Type
/// @return             \b OUT: Detect Mode
//-------------------------------------------------------------------------------------------------
MAPI_U8 mapi_audio_amp::AMP_Api_AmpGetAutoDetectType(void)
{
    return 0;
}

//-------------------------------------------------------------------------------------------------
/// AMP_Api_AmpGetOutputMode:To Get Output Mode
/// @return             \b OUT: Output Mode
//-------------------------------------------------------------------------------------------------
MAPI_U8 mapi_audio_amp::AMP_Api_AmpGetOutputMode(void)
{
    return 0;
}

//-------------------------------------------------------------------------------------------------
/// AMP_Api_AmpGetSenceMode:To Get Sence Mode
/// @return             \b OUT: Sence Mode
//-------------------------------------------------------------------------------------------------
MAPI_U8 mapi_audio_amp::AMP_Api_AmpGetSenceMode(void)
{
    return 0;
}

//-------------------------------------------------------------------------------------------------
/// AMP_Api_AmpSetOutputMode:To Set Output Mode
/// @param u8OutMode      \b IN: Output Mode.
/// @return None
//-------------------------------------------------------------------------------------------------
void mapi_audio_amp::AMP_Api_AmpSetOutputMode(MAPI_U8 u8OutMode)
{
}

//-------------------------------------------------------------------------------------------------
/// AMP_Api_AmpSetSenceMode:To Set Sence Mode
/// @param u8SenceMode      \b IN: Sence Mode.
/// @return None
//-------------------------------------------------------------------------------------------------
void mapi_audio_amp::AMP_Api_AmpSetSenceMode(MAPI_U8 u8SenceMode)
{
}

//-------------------------------------------------------------------------------------------------
/// AMP_Api_Init_ResetOnOff:To Reset On/Off Audio Amplifier
/// @param bIsOn      \b IN: TRUE is ON; FALSE is Off.
/// @return None
//-------------------------------------------------------------------------------------------------
void mapi_audio_amp::AMP_Api_Init_ResetOnOff(MAPI_BOOL bIsOn)
{
}

//-------------------------------------------------------------------------------------------------
/// AMP_Api_Init_Device:To Init Audio Amplifier
/// @return             \b OUT: RESULT
//-------------------------------------------------------------------------------------------------
MAPI_U8 mapi_audio_amp::AMP_Api_Init_Device(void)
{
    return 0;
}

//-------------------------------------------------------------------------------------------------
/// AMP_Api_AmpSoftMuteOnOff: To Set Mute On/Off Audio Amplifier
/// @param bIsOn      \b IN: TRUE is ON; FALSE is Off.
/// @return None
//-------------------------------------------------------------------------------------------------
void mapi_audio_amp::AMP_Api_AmpSoftMuteOnOff(MAPI_BOOL bIsOn)
{
}

//-------------------------------------------------------------------------------------------------
/// AMP_Api_AmpSetEQByMode:To Set EQ By Mode
/// @param Mode      \b IN: desktop mode or hungup mode.
/// @return None
//-------------------------------------------------------------------------------------------------
void mapi_audio_amp::AMP_Api_AmpSetEQByMode(ENUM_SET_EQ_BY_MODE_TYPE Mode)
{
}

//-------------------------------------------------------------------------------------------------
/// AMP_Api_AmpSetChannelxGain:To Set Channel Gain
/// @param channel      \b IN: Channel Index.
/// @param val      \b IN: Value of Gain.
/// @return None
//-------------------------------------------------------------------------------------------------
void mapi_audio_amp::AMP_Api_AmpSetChannelxGain(MAPI_U8 channel, MAPI_U8 val)
{
}

//-------------------------------------------------------------------------------------------------
/// AMP_Api_AmpSetSampleRate:To Set SampleRate
/// @param SampleRate      \b IN: SampleRate.
/// @return None
//-------------------------------------------------------------------------------------------------
void mapi_audio_amp::AMP_Api_AmpSetSampleRate(MAPI_U8 SampleRate)
{
}

//-------------------------------------------------------------------------------------------------
/// AMP_Api_AmpSetSubChannelVolumeWithCurve:To Set Volume SubChannel with Curve
/// @param volume      \b IN: Volume.
/// @return None
//-------------------------------------------------------------------------------------------------
void mapi_audio_amp::AMP_Api_AmpSetSubChannelVolumeWithCurve(MAPI_U8 volume)
{
}

//-------------------------------------------------------------------------------------------------
/// AMP_Api_AmpReleaseSubChannelBandwidth:To Release BW SubChannel
/// @param bEnSBW      \b IN: En SBW.
/// @return None
//-------------------------------------------------------------------------------------------------
void mapi_audio_amp::AMP_Api_AmpReleaseSubChannelBandwidth(MAPI_U8 bEnSBW)
{
}

//-------------------------------------------------------------------------------------------------
/// AMP_Api_AmpSetSubChannelVolumeOnOff:To Set Volume On/Off SubChannel
/// @param bEnable      \b IN: Enable.
/// @param volume      \b IN: Volume.
/// @param bEnSBW      \b IN: En SBW.
/// @return None
//-------------------------------------------------------------------------------------------------
void mapi_audio_amp::AMP_Api_AmpSetSubChannelVolumeOnOff(MAPI_U8 bEnable, MAPI_U8 volume, MAPI_U8 bEnSBW)
{
}

