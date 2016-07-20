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

#ifndef MSOS_TYPE_LINUX
#define MSOS_TYPE_LINUX
#endif

// headers of itself
#include "MSrv_SSSound.h"

// headers of standard C libs

// headers of standard C++ libs
#include <unistd.h>

// headers of the same layer's
#include "MSrv_Control.h"
#include "MSrv_System_Database.h"
#if (DVB_ENABLE == 1)
#include "MSrv_System_Database_DVB.h"
#else
#include "MSrv_System_Database_ATSC.h"
#endif

// headers of underlying layer's
#include "mapi_mheg5.h"
#include "CustomerKeycode.h"
#include "mapi_types.h"
#include "mapi_interface.h"
#include "mapi_audio.h"
#include "mapi_gpio.h"
#include "mapi_gpio_devTable.h"
#include "mapi_pcb.h"
#include "SystemInfo.h"
#include "mapi_demodulator.h"
#include "mapi_tuner.h"
#include "mapi_env_manager.h"
#include "MEvent.h"
#if (MSTAR_TVOS == 1)
#include "MSrv_DTV_Player.h"
#include "MSrv_ATV_Player.h"
#if (ATSC_SYSTEM_ENABLE == 0)
#include "MSrv_DTV_Player_DVB.h"
#endif
#endif
#if (KARAOKE_ENABLE==1)
#include "mapi_audio_amp.h"
#endif
BEGIN_EVENT_MAP(MSrv_SSSound, MSrv)
ON_EVENT(EV_MOUNT_NOTIFIER, &MSrv_SSSound::ProcessEvent)
END_EVENT_MAP()
#define SOUND_NLA_DBG(x)        //(x)

#define MSG_MSRV_SSSOUND_LEVEL0_EMERG(fmt...)   do {\
                                                  if(m_msrv_sssound_debug_level <= MSRV_SSSOUND_LEVEL0_EMERG)    \
                                                  {printf("\t---[mpai_audio.cpp]%s():", __FUNCTION__);         \
                                                  printf(fmt);}  \
                                                } while (0)

#define MSG_MSRV_SSSOUND_LEVEL1_ALERT(fmt...)   do {\
                                                  if(m_msrv_sssound_debug_level <= MSRV_SSSOUND_LEVEL1_ALERT)    \
                                                  {printf("\t---[mpai_audio.cpp]%s():", __FUNCTION__);         \
                                                  printf(fmt);}  \
                                                } while (0)

#define MSG_MSRV_SSSOUND_LEVEL2_CRIT(fmt...)    do {\
                                                  if(m_msrv_sssound_debug_level <= MSRV_SSSOUND_LEVEL2_CRIT)    \
                                                  {printf("\t---[mpai_audio.cpp]%s():", __FUNCTION__);         \
                                                  printf(fmt);}  \
                                                } while (0)

#define MSG_MSRV_SSSOUND_LEVEL3_ERR(fmt...)     do {\
                                                  if(m_msrv_sssound_debug_level <= MSRV_SSSOUND_LEVEL3_ERR)    \
                                                  {printf("\t---[mpai_audio.cpp]%s():", __FUNCTION__);         \
                                                  printf(fmt);}  \
                                                } while (0)

#define MSG_MSRV_SSSOUND_LEVEL4_WARNING(fmt...) do {\
                                                  if(m_msrv_sssound_debug_level <= MSRV_SSSOUND_LEVEL4_WARNING)    \
                                                  {printf("\t---[mpai_audio.cpp]%s():", __FUNCTION__);         \
                                                  printf(fmt);}  \
                                                } while (0)

#define MSG_MSRV_SSSOUND_LEVEL5_NOTICE(fmt...)  do {\
                                                  if(m_msrv_sssound_debug_level <= MSRV_SSSOUND_LEVEL5_NOTICE)    \
                                                  {printf("\t---[mpai_audio.cpp]%s():", __FUNCTION__);         \
                                                  printf(fmt);}  \
                                                } while (0)

#define MSG_MSRV_SSSOUND_LEVEL6_INFO(fmt...)    do {\
                                                  if(m_msrv_sssound_debug_level <= MSRV_SSSOUND_LEVEL6_INFO)    \
                                                  {printf("\t---[mpai_audio.cpp]%s():", __FUNCTION__);         \
                                                  printf(fmt);}  \
                                                } while (0)

#define MSG_MSRV_SSSOUND_LEVEL7_DEBUG(fmt...)   do {\
                                                  if(m_msrv_sssound_debug_level <= MSRV_SSSOUND_LEVEL7_DEBUG)    \
                                                  {printf("\t---[mpai_audio.cpp]%s():", __FUNCTION__);         \
                                                  printf(fmt);}  \
                                                } while (0)


#define SKYPE_DELAY_MS      20
#define SKYPE_MAX_VOLUME    70

#define AU_DELAY_FOR_SNDEFFECT_MUTE		10
#define AU_DELAY_FOR_SNDEFFECT_UNMUTE	30

/// @param <IN>        \b Band: 0~4
///                    \b Gain: 0~240
///                    \b Foh: 1~160
///                    \b Fol: 0~99
///                    \b QValue: 5~160
AUDIO_PEQ_PARAM Skype_PEQParam[] =
{
    {  0,  30,   2,  50,  20},    // Band0
    {  1,  30,  16,   0,  20},    // Band1
    {  2, 170,  60,   0,  40},    // Band2
    {  3, 120,  40,   0, 160},    // Band3
    {  4, 120,  50,   0, 160},    // Band4
};

/// @param <IN>        \b Band: 5~7
///                    \b Type: 0:LPF 1:HPF
///                    \b Foh: 1~160
///                    \b Fol: 0~99
///                    \b QValue: 0
AUDIO_PEQ_PARAM Skype_HLPFParam[] =
{
    {  5, 1,   3,   0, 0},    // Band5
    {  6, 1,   3,   0, 0},    // Band6
    {  7, 0,  90,   0, 0},    // Band7
};

MAPI_U8 Stereo[] =
{
    E_AUDIOMODE_FORCED_MONO_,
    E_AUDIOMODE_G_STEREO_,
};

MAPI_U8 A2_Dual[] =
{
    E_AUDIOMODE_DUAL_A_,
    E_AUDIOMODE_DUAL_B_,
    E_AUDIOMODE_DUAL_AB_,
};

MAPI_U8 NICAM_Stereo[] =
{
    E_AUDIOMODE_FORCED_MONO_,
    E_AUDIOMODE_NICAM_STEREO_,
};

MAPI_U8 NICAM_Dual[] =
{
    E_AUDIOMODE_FORCED_MONO_,
    E_AUDIOMODE_NICAM_DUAL_A_,
    E_AUDIOMODE_NICAM_DUAL_B_,
    E_AUDIOMODE_NICAM_DUAL_AB_,
};

MAPI_U8 NICAM_Mono[] =
{
    E_AUDIOMODE_FORCED_MONO_,
    E_AUDIOMODE_NICAM_MONO_ ,
};

MAPI_U8 BTSC_Mono_Sap[] =
{
    E_AUDIOMODE_MONO_,
    E_AUDIOMODE_MONO_SAP_ ,
};

MAPI_U8 BTSC_Stereo_Sap[] =
{
    E_AUDIOMODE_MONO_,
    E_AUDIOMODE_G_STEREO_ ,
    E_AUDIOMODE_STEREO_SAP_ ,
};

#if (STB_ENABLE == 1)
VolumeCurve_t stVolumeCurve =
{
    TRUE,
    {
        //   1       2       3       4       5       6       7       8       9       10
        0x7F, //  00
        0x47, 0x44, 0x41, 0x3E, 0x3C, 0x3A, 0x38, 0x36, 0x34, 0x32, //  10
        0x30, 0x2E, 0x2D, 0x2C, 0x2B, 0x2A, 0x29, 0x28, 0x27, 0x26, //  20
        0x25, 0x24, 0x23, 0x22, 0x21, 0x20, 0x1F, 0x1E, 0x1E, 0x1D, //  30
        0x1D, 0x1C, 0x1C, 0x1B, 0x1B, 0x1A, 0x1A, 0x19, 0x19, 0x18, //  40
        0x18, 0x17, 0x17, 0x16, 0x16, 0x15, 0x15, 0x15, 0x14, 0x14, //  50
        0x14, 0x14, 0x13, 0x13, 0x13, 0x13, 0x12, 0x12, 0x12, 0x12, //  60
        0x11, 0x11, 0x11, 0x11, 0x10, 0x10, 0x10, 0x10, 0x0F, 0x0F, //  70
        0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0E, 0x0E, 0x0E, 0x0E, //  80
        0x0E, 0x0E, 0x0E, 0x0E, 0x0D, 0x0D, 0x0D, 0x0D, 0x0D, 0x0D, //  90
        0x0D, 0x0D, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C  //  100
    },
    {
        //   1       2       3       4       5       6       7       8       9       10
        0x00, //  00
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, //  10
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, //  20
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04, 0x00, 0x04, //  30
        0x00, 0x04, 0x00, 0x04, 0x00, 0x04, 0x00, 0x04, 0x00, 0x04, //  40
        0x00, 0x04, 0x00, 0x04, 0x00, 0x04, 0x02, 0x00, 0x06, 0x04, //  50
        0x02, 0x00, 0x06, 0x04, 0x02, 0x00, 0x06, 0x04, 0x02, 0x00, //  60
        0x06, 0x04, 0x02, 0x00, 0x06, 0x04, 0x02, 0x00, 0x07, 0x06, //  70
        0x05, 0x04, 0x03, 0x02, 0x01, 0x00, 0x07, 0x06, 0x05, 0x04, //  80
        0x03, 0x02, 0x01, 0x00, 0x07, 0x06, 0x05, 0x04, 0x03, 0x02, //  90
        0x01, 0x00, 0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00  //  100
    }
};
#endif

static void _TransNLAPointToSNDNLAPoint(const MS_NLA_POINT &stNLAPoint, MSrv_SSSound::MS_SND_NLA_POINT &stNLASoundPoint)
{
    STATIC_ASSERT(sizeof(MS_NLA_POINT) == sizeof(MSrv_SSSound::MS_SND_NLA_POINT));

    stNLASoundPoint.u32OSD_V0 = stNLAPoint.u32OSD_V0;
    stNLASoundPoint.u32OSD_V25 = stNLAPoint.u32OSD_V25;
    stNLASoundPoint.u32OSD_V50 = stNLAPoint.u32OSD_V50;
    stNLASoundPoint.u32OSD_V75 = stNLAPoint.u32OSD_V75;
    stNLASoundPoint.u32OSD_V100 = stNLAPoint.u32OSD_V100;
}

static AUDIOMUTETYPE_ _TransMSrvMuteTypeToSDKMuteType(SSSOUND_MUTE_TYPE muteType, MAPI_BOOL onOff)
{
    AUDIOMUTETYPE_ RetMuteType = E_AUDIO_PERMANENT_MUTEOFF_;

    switch (muteType)
    {
    case MUTE_PERMANENT:
        if (onOff == TRUE)
        {
            RetMuteType = E_AUDIO_PERMANENT_MUTEON_;
        }
        else
        {
            RetMuteType = E_AUDIO_PERMANENT_MUTEOFF_;
        }
        break;

    case MUTE_MOMENT:
        if (onOff == TRUE)
        {
            RetMuteType = E_AUDIO_MOMENT_MUTEON_;
        }
        else
        {
            RetMuteType = E_AUDIO_MOMENT_MUTEOFF_;
        }
        break;

    case MUTE_BYUSER:
        if (onOff == TRUE)
        {
            RetMuteType = E_AUDIO_BYUSER_MUTEON_;
        }
        else
        {
            RetMuteType = E_AUDIO_BYUSER_MUTEOFF_;
        }
        break;

    case MUTE_BYSYNC:
        if (onOff == TRUE)
        {
            RetMuteType = E_AUDIO_BYSYNC_MUTEON_;
        }
        else
        {
            RetMuteType = E_AUDIO_BYSYNC_MUTEOFF_;
        }
        break;

    case MUTE_BYVCHIP:
        if (onOff == TRUE)
        {
            RetMuteType = E_AUDIO_BYVCHIP_MUTEON_;
        }
        else
        {
            RetMuteType = E_AUDIO_BYVCHIP_MUTEOFF_;
        }
        break;

    case MUTE_BYBLOCK:
        if (onOff == TRUE)
        {
            RetMuteType = E_AUDIO_BYBLOCK_MUTEON_;
        }
        else
        {
            RetMuteType = E_AUDIO_BYBLOCK_MUTEOFF_;
        }
        break;

    case MUTE_INTERNAL1:
        if (onOff == TRUE)
        {
            RetMuteType = E_AUDIO_INTERNAL_1_MUTEON_;
        }
        else
        {
            RetMuteType = E_AUDIO_INTERNAL_1_MUTEOFF_;
        }
        break;

    case MUTE_INTERNAL2: // SDK enum changed, but bit mapping still the same
        if (onOff == TRUE)
        {
            RetMuteType = E_AUDIO_SIGNAL_UNSTABLE_MUTEON_;
        }
        else
        {
            RetMuteType = E_AUDIO_SIGNAL_UNSTABLE_MUTEOFF_;
        }
        break;

    case MUTE_INTERNAL3:
        if (onOff == TRUE)
        {
            RetMuteType = E_AUDIO_INTERNAL_3_MUTEON_;
        }
        else
        {
            RetMuteType = E_AUDIO_INTERNAL_3_MUTEOFF_;
        }
        break;

    case MUTE_DURING_LIMITED_TIME:
        if (onOff == TRUE)
        {
            RetMuteType = E_AUDIO_DURING_LIMITED_TIME_MUTEON_;
        }
        else
        {
            RetMuteType = E_AUDIO_DURING_LIMITED_TIME_MUTEOFF_;
        }
        break;

    case MUTE_MHEGAP:
        if (onOff == TRUE)
        {
            RetMuteType = E_AUDIO_MHEGAP_MUTEON_;
        }
        else
        {
            RetMuteType = E_AUDIO_MHEGAP_MUTEOFF_;
        }
        break;

    case MUTE_CI:
        if (onOff == TRUE)
        {
            RetMuteType = E_AUDIO_CI_MUTEON_;
        }
        else
        {
            RetMuteType = E_AUDIO_CI_MUTEOFF_;
        }
        break;

    case MUTE_SCAN:
        if (onOff == TRUE)
        {
            RetMuteType = E_AUDIO_SCAN_MUTEON_;
        }
        else
        {
            RetMuteType = E_AUDIO_SCAN_MUTEOFF_;
        }
        break;

    case MUTE_SOURCESWITCH:
        if (onOff == TRUE)
        {
            RetMuteType = E_AUDIO_SOURCESWITCH_MUTEON_;
        }
        else
        {
            RetMuteType = E_AUDIO_SOURCESWITCH_MUTEOFF_;
        }
        break;

    case MUTE_USER_SPEAKER:
        if (onOff == TRUE)
        {
            RetMuteType = E_AUDIO_USER_SPEAKER_MUTEON_;
        }
        else
        {
            RetMuteType = E_AUDIO_USER_SPEAKER_MUTEOFF_;
        }
        break;

    case MUTE_USER_HP:
        if (onOff == TRUE)
        {
            RetMuteType = E_AUDIO_USER_HP_MUTEON_;
        }
        else
        {
            RetMuteType = E_AUDIO_USER_HP_MUTEOFF_;
        }
        break;

    case MUTE_USER_SPDIF:
        if (onOff == TRUE)
        {
            RetMuteType = E_AUDIO_USER_SPDIF_MUTEON_;
        }
        else
        {
            RetMuteType = E_AUDIO_USER_SPDIF_MUTEOFF_;
        }
        break;

    case MUTE_USER_SCART1:
        if (onOff == TRUE)
        {
            RetMuteType = E_AUDIO_USER_SCART1_MUTEON_;
        }
        else
        {
            RetMuteType = E_AUDIO_USER_SCART1_MUTEOFF_;
        }
        break;

    case MUTE_USER_SCART2:
        if (onOff == TRUE)
        {
            RetMuteType = E_AUDIO_USER_SCART2_MUTEON_;
        }
        else
        {
            RetMuteType = E_AUDIO_USER_SCART2_MUTEOFF_;
        }
        break;

    case MUTE_ALL:
        if (onOff == TRUE)
        {
            RetMuteType = E_AUDIO_ALL_MUTEON_;
        }
        else
        {
            RetMuteType = E_AUDIO_ALL_MUTEOFF_;
        }
        break;

    case MUTE_USER_DATA_IN:
        if (onOff == TRUE)
        {
            RetMuteType = E_AUDIO_DATA_IN_MUTEON_;
        }
        else
        {
            RetMuteType = E_AUDIO_DATA_IN_MUTEOFF_;
        }
        break;

    case MUTE_USER_PCM_CAPTURE1:
        if (onOff == TRUE)
        {
            RetMuteType = E_AUDIO_USER_PCM_CAPTURE1_MUTEON_;
        }
        else
        {
            RetMuteType = E_AUDIO_USER_PCM_CAPTURE1_MUTEOFF_;
        }
        break;

    case MUTE_USER_PCM_CAPTURE2:
        if (onOff == TRUE)
        {
            RetMuteType = E_AUDIO_USER_PCM_CAPTURE2_MUTEON_;
        }
        else
        {
            RetMuteType = E_AUDIO_USER_PCM_CAPTURE2_MUTEOFF_;
        }
        break;

    case MUTE_BY_APP:
        if (onOff == TRUE)
        {
            RetMuteType = E_AUDIO_APP_MUTEON_;
        }
        else
        {
            RetMuteType = E_AUDIO_APP_MUTEOFF_;
        }
        break;

#if (INPUT_SOURCE_LOCK_ENABLE == 1)
    case MUTE_INPUT_SOURCEL_LOCK:
        if (onOff == TRUE)
        {
            RetMuteType = E_AUDIO_INPUT_SOURCE_LOCK_MUTEON_;
        }
        else
        {
            RetMuteType = E_AUDIO_INPUT_SOURCE_LOCK_MUTEOFF_;
        }
        break;
#endif

    default:
        printf("%s: Err! muteType(%d) is out of Range\n", __FUNCTION__, muteType);
        break;
    }

    return RetMuteType;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: MSrv_SSSound()
/// @brief \b Function \b Description: Audio SSound Private Variables initialize
/// @param  <IN>        \b NONE
/// @return <OUT>       \b NONE
//-------------------------------------------------------------------------------------------------
MSrv_SSSound::MSrv_SSSound()
{
    //SetSurroundSound(E_SURROUND_MODE_OFF);
#if (STB_ENABLE == 1)
    SystemInfo::GetInstance()->SetVolumeCurveCfgBlock(&stVolumeCurve);
#endif

    m_bAudyessySRSMode = FALSE;
    IsAVCFlag          = FALSE;
    m_bNRenable        = MAPI_FALSE;
    m_surrMode = E_SURROUND_MODE_OFF;

    m_SpeakerVolumeValue = 0;
    m_HPVolumeValue = 0;
    m_LINE_OUT_VolumeValue = 0;
    m_SCART1_OUT_VolumeValue = 0;
    m_SCART2_OUT_VolumeValue = 0;
    m_SPDIF_OUT_VolumeValue = 0;
    m_AD_VolumeValue = 0;
    m_Premixer_KTV_MP3_VolumeValue = 0;
    m_Premixer_KTV_MIC_VolumeValue = 0;
    m_Premixer_GAME1_VolumeValue = 0;
    m_Premixer_GAME2_VolumeValue = 0;
    m_Premixer_ECHO1_VolumeValue = 0;
    m_Premixer_ECHO2_VolumeValue = 0;
    m_Premixer_ALSA_VolumeValue  = 0;
    m_PcmCapture1_VolumeValue = 0;
    m_PcmCapture2_VolumeValue = 0;
#if (MSTAR_TVOS == 1)
    m_VolumeCompensation = DEFAULT_CHANNEL_VOLUME_COMPENSATION;
#endif

    spkr_prescale = 0;
    hp_prescale = 0;
    sc1_prescale = 0;
    sc2_prescale = 0;
    spdif_prescale = 0;

    m_BSND_PARAM_PRESCALE       = 0;
    m_BSND_PARAM_TREBLE         = 0;
    m_BSND_PARAM_BASS           = 0;
    m_BSND_PARAM_TYPE_BALANCE   = 0;
    m_BSND_PARAM_EQ_BAND_NUM    = 0;
    m_BSND_PARAM_PEQ_BAND_NUM   = 0;

    for ( MAPI_U8 i=0; i<MAX_EQ_BAND_NUM; i++)
    {
        m_BSND_PARAM_EQ_LEVEL[i]  = 0;
    }
    for ( MAPI_U8 i=0; i<MAX_EQ_BAND_NUM; i++)
    {
        m_BSND_PARAM_PEQ_GAIN[i]    = 0;
        m_BSND_PARAM_PEQ_FC[i]      = 0;
        m_BSND_PARAM_PEQ_QVALUE[i]  = 0;
    }

    m_BSND_PARAM_AVC_THRESHOLD  = 0;
    m_BSND_PARAM_AVC_AT         = 0;
    m_BSND_PARAM_AVC_RT         = 0;
    m_BSND_PARAM_MSURR_XA       = 0;
    m_BSND_PARAM_MSURR_XB       = 0;
    m_BSND_PARAM_MSURR_XK       = 0;
    m_BSND_PARAM_DRC_THRESHOLD  = 0;
    m_BSND_PARAM_NR_THRESHOLD   = 0;
    m_BSND_PARAM_ECHO_TIME      = 0;

    m_ADVSND_DOLBY_PL2VDPK_SMOD = 0;
    m_ADVSND_DOLBY_PL2VDPK_WMOD = 0;
    m_ADVSND_SRS_TSXT_SET_INPUT_GAIN = 0;
    m_ADVSND_SRS_TSXT_SET_DC_GAIN = 0;
    m_ADVSND_SRS_TSXT_SET_TRUBASS_GAIN = 0;
    m_ADVSND_SRS_TSXT_SET_SPEAKERSIZE = 0;
    m_ADVSND_SRS_TSXT_SET_INPUT_MODE = 0;
    m_ADVSND_SRS_TSXT_SET_OUTPUT_GAIN = 0;
    m_ADVSND_SRS_TSHD_SET_INPUT_MODE = 0;
    m_ADVSND_SRS_TSHD_SET_OUTPUT_MODE = 0;
    m_ADVSND_SRS_TSHD_SET_SPEAKERSIZE = 0;
    m_ADVSND_SRS_TSHD_SET_TRUBASS_CONTROL = 0;
    m_ADVSND_SRS_TSHD_SET_DEFINITION_CONTROL = 0;
    m_ADVSND_SRS_TSHD_SET_DC_CONTROL = 0;
    m_ADVSND_SRS_TSHD_SET_SURROUND_LEVEL = 0;
    m_ADVSND_SRS_TSHD_SET_INPUT_GAIN = 0;
    m_ADVSND_SRS_TSHD_SET_WOWSPACE_CONTROL = 0;
    m_ADVSND_SRS_TSHD_SET_WOWCENTER_CONTROL = 0;
    m_ADVSND_SRS_TSHD_SET_WOWHDSRS3DMODE = 0;
    m_ADVSND_SRS_TSHD_SET_LIMITERCONTROL = 0;
    m_ADVSND_SRS_TSHD_SET_OUTPUT_GAIN = 0;
    m_ADVSND_SRS_THEATERSOUND_INPUT_GAIN = 0;
    m_ADVSND_SRS_THEATERSOUND_DEFINITION_CONTROL = 0;
    m_ADVSND_SRS_THEATERSOUND_DC_CONTROL = 0;
    m_ADVSND_SRS_THEATERSOUND_TRUBASS_CONTROL = 0;
    m_ADVSND_SRS_THEATERSOUND_SPEAKERSIZE = 0;
    m_ADVSND_SRS_THEATERSOUND_HARDLIMITER_LEVEL = 0;
    m_ADVSND_SRS_THEATERSOUND_HARDLIMITER_BOOST_GAIN = 0;
    m_ADVSND_SRS_THEATERSOUND_HEADROOM_GAIN = 0;
    m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_MODE = 0;
    m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_REF_LEVEL = 0;
    m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_MAX_GAIN = 0;
    m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_NOISE_MNGR_THLD = 0;
    m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_CALIBRATE = 0;
    m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_INPUT_GAIN = 0;
    m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_OUTPUT_GAIN = 0;
    m_ADVSND_SRS_THEATERSOUND_TSHD_INPUT_GAIN = 0;
    m_ADVSND_SRS_THEATERSOUND_TSHD_OUTPUT_GAIN = 0;
    m_ADVSND_SRS_THEATERSOUND_SURR_LEVEL_CONTROL = 0;
    m_ADVSND_SRS_THEATERSOUND_TRUBASS_COMPRESSOR_CONTROL = 0;
    m_ADVSND_SRS_THEATERSOUND_TRUBASS_PROCESS_MODE = 0;
    m_ADVSND_SRS_THEATERSOUND_TRUBASS_SPEAKER_AUDIO = 0;
    m_ADVSND_SRS_THEATERSOUND_TRUBASS_SPEAKER_ANALYSIS = 0;
    m_ADVSND_SRS_THEATERSOUND_HPF_FC = 0;
    m_ADVSND_DTS_ULTRATV_EVO_MONOINPUT  = 0;
    m_ADVSND_DTS_ULTRATV_EVO_WIDENINGON = 0;
    m_ADVSND_DTS_ULTRATV_EVO_ADD3DBON  = 0;
    m_ADVSND_DTS_ULTRATV_EVO_PCELEVEL  = 0;
    m_ADVSND_DTS_ULTRATV_EVO_VLFELEVEL = 0;
    m_ADVSND_DTS_ULTRATV_SYM_DEFAULT = 0;
    m_ADVSND_DTS_ULTRATV_SYM_MODE  = 0;
    m_ADVSND_DTS_ULTRATV_SYM_LEVEL = 0;
    m_ADVSND_DTS_ULTRATV_SYM_RESET = 0;
    m_ADVSND_AUDYSSEY_DYNAMICVOL_COMPRESS_MODE = 0;
    m_ADVSND_AUDYSSEY_DYNAMICVOL_GC = 0;
    m_ADVSND_AUDYSSEY_DYNAMICVOL_VOLSETTING = 0;
    m_ADVSND_AUDYSSEY_DYNAMICEQ_EQOFFSET = 0;
    m_ADVSND_AUDYSSEY_ABX_GWET   = 0;
    m_ADVSND_AUDYSSEY_ABX_GDRY   = 0;
    m_ADVSND_AUDYSSEY_ABX_FILSET = 0;
    m_ADVSND_SRS_PURESOUND_HL_INPUT_GAIN = 0;
    m_ADVSND_SRS_PURESOUND_HL_OUTPUT_GAIN = 0;
    m_ADVSND_SRS_PURESOUND_HL_BYPASS_GAIN = 0;
    m_ADVSND_SRS_PURESOUND_HL_LIMITERBOOST = 0;
    m_ADVSND_SRS_PURESOUND_HL_HARDLIMIT = 0;
    m_ADVSND_SRS_PURESOUND_HL_DELAYLEN = 0;
    m_ADVSND_SRS_PURESOUND_AEQ_INPUT_GAIN = 0;
    m_ADVSND_SRS_PURESOUND_AEQ_OUTPUT_GAIN = 0;
    m_ADVSND_SRS_PURESOUND_AEQ_BYPASS_GAIN = 0;
    m_ADVSND_SRS_PURESOUND_HPF_FREQUENCY = 0;
    m_ADVSND_SRS_PURESOUND_TBHD_TRUBASS_LEVEL = 0;
    m_ADVSND_SRS_PURESOUND_TBHD_SPEAKER_SIZE = 0;
    m_ADVSND_SRS_PURESOUND_TBHD_LEVEL_INDEPENDENT_EN = 0;
    m_ADVSND_SRS_PURESOUND_TBHD_COMPRESSOR_LEVEL = 0;
    m_ADVSND_SRS_PURESOUND_TBHD_MODE = 0;
    m_ADVSND_SRS_PURESOUND_TBHD_SPEAKER_AUDIO = 0;
    m_ADVSND_SRS_PURESOUND_TBHD_SPEAKER_ANALYSIS = 0;
    m_ADVSND_SRS_PURESOUND_INPUT_GAIN = 0;
    m_ADVSND_SRS_PURESOUND_OUTPUT_GAIN = 0;
    m_msrv_sssound_debug_level = MSRV_SSSOUND_LEVEL_MAX;

    PrescaleTable.spkr_prescale_DTV = 0;
    PrescaleTable.hp_prescale_DTV = 0;
    PrescaleTable.sc1_prescale_DTV = 0;
    PrescaleTable.sc2_prescale_DTV = 0;  // LINE OUT

    PrescaleTable.spkr_prescale_ATV = 0;
    PrescaleTable.hp_prescale_ATV = 0;
    PrescaleTable.sc1_prescale_ATV = 0;
    PrescaleTable.sc2_prescale_ATV = 0;  // LINE OUT;

    PrescaleTable.spkr_prescale_AV = 0;
    PrescaleTable.hp_prescale_AV = 0;
    PrescaleTable.sc1_prescale_AV = 0;
    PrescaleTable.sc2_prescale_AV = 0;  // LINE OUT;

    PrescaleTable.spkr_prescale_DVI = 0;
    PrescaleTable.hp_prescale_DVI = 0;
    PrescaleTable.sc1_prescale_DVI = 0;
    PrescaleTable.sc2_prescale_DVI = 0;  // LINE OUT;

    PrescaleTable.spkr_prescale_HDMI = 0;
    PrescaleTable.hp_prescale_HDMI = 0;
    PrescaleTable.sc1_prescale_HDMI = 0;
    PrescaleTable.sc2_prescale_HDMI = 0;  // LINE OUT;

    PrescaleTable.spkr_prescale_MM= 0;
    PrescaleTable.hp_prescale_MM = 0;
    PrescaleTable.sc1_prescale_MM = 0;
    PrescaleTable.sc2_prescale_MM = 0;  // LINE OUT;

    st_SIF_Prescale_Offset.Prescale_A2_FM = 0;
    st_SIF_Prescale_Offset.Prescale_FM_M = 0;
    st_SIF_Prescale_Offset.Prescale_HIDEV = 0;
    st_SIF_Prescale_Offset.Prescale_HIDEV_M = 0;
    st_SIF_Prescale_Offset.Prescale_NICAM = 0;
    st_SIF_Prescale_Offset.Prescale_AM = 0;
    st_SIF_Prescale_Offset.Prescale_BTSC = 0;
    st_SIF_Prescale_Offset.Prescale_BTSC_MONO = 0;
    st_SIF_Prescale_Offset.Prescale_BTSC_STEREO = 0;
    st_SIF_Prescale_Offset.Prescale_BTSC_SAP = 0;

    m_DTV_DMP_DECODER_CTRL_CMD   = (DTV_DMP_AUDIO_DEC_CTRLCMD_TYPE)DTV_DMP_DEC_STOP;
    m_DTV_DECOCDER_SOUNDMODE     = (DTV_DMP_AUDIO_DEC_SOUNDMOD_TYPE)DTV_DMP_DEC_SOUNDMODE_STEREO;
    m_pUpdateInfo    = NULL;
    m_pUpdateInfo = (void*)malloc(sizeof(UPDATE_INFO));
    if(m_pUpdateInfo == NULL)
    {
        printf("[ERROR] Can't malloc m_pUpdateInfo\n");
    }
    m_pMountNotifier = MSrv_MountNotifier::GetInstance();
    if(m_pMountNotifier == NULL)
    {
        printf("[ERROR] Can't get MSrv_MountNotifier::GetInstance()\n");
    }
    ASSERT(m_pMountNotifier);
    m_pMountNotifier->AddEventRecipient(this, MSrv_SSSound::m_pUpdateInfo);
}

MSrv_SSSound::~MSrv_SSSound()
{
    mapi_interface::Get_mapi_audio()->SetPowerOn(FALSE);
    if(m_pUpdateInfo != NULL)
    {
        free(m_pUpdateInfo);
        m_pUpdateInfo = NULL;
    }
    if(m_pMountNotifier != NULL)
    {
        m_pMountNotifier->RemoveEventRecipient(this);
        m_pMountNotifier = NULL;
    }
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: Initialize()
/// @brief \b Function \b Description: SSound Initialize Function
/// @param  <IN>        \b NONE
/// @return <OUT>       \b NONE
//-------------------------------------------------------------------------------------------------
void MSrv_SSSound::Initialize()
{
#if (PEQ_ENABLE == 1)
    AUDIO_PEQ_PARAM m_stPEQParam = {0};
    MAPI_BOOL peq_en = mapi_interface::Get_mapi_audio()->SND_GetPEQEnableValue();
    AUDIO_PEQ_BAND_NUM peqBand = mapi_interface::Get_mapi_audio()->m_PEQNumBand;
    MAPI_U8 NumPEqBand = 3;
#endif
#if (CHINA_ENABLE == 1)
    MS_FACTORY_HIDEV_INDEX eHidevIndex = EN_AUDIO_HIDEV_OFF;
#endif

    mapi_interface::Get_mapi_audio()->InitAudioSystem();

    // SIF prescale
    mapi_interface::Get_mapi_audio()->SIF_SetSIFPrescaleTable(&st_SIF_Prescale_Offset);

#if (PEQ_ENABLE == 1)
    if(peqBand == PEQ_5_BANDS)
    {
        NumPEqBand = 5;
    }
    else if(peqBand == PEQ_RESERVED)
    {
        NumPEqBand = sizeof(mapi_interface::Get_mapi_audio()->PEQParam) / sizeof(AUDIO_PEQ_PARAM);
    }
    if((NumPEqBand > 0) && (NumPEqBand < 7))
    {
        mapi_interface::Get_mapi_audio()->SND_InitPEQ(peq_en, peqBand);

        for(U8 i = 0; i < NumPEqBand; i++)
        {
            MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->GetPEQSetting((void *)&m_stPEQParam, i);
            mapi_interface::Get_mapi_audio()->SND_SetPEQ(i, m_stPEQParam.Gain,
                    m_stPEQParam.Foh,
                    m_stPEQParam.Fol,
                    m_stPEQParam.QValue);
        }
    }
#endif

#if (CHINA_ENABLE == 1)
    mapi_interface::Get_mapi_audio()->SIF_SetChinaModeEnable(TRUE);

    MSrv_Control::GetMSrvSystemDatabase()->GetFactoryExtSetting((void *)(&eHidevIndex), EN_FACTORY_EXT_HIDEV);

    STATIC_ASSERT((int)EN_AUDIO_HIDEV_OFF == (int)E_SOUND_HIDEV_OFF);
    STATIC_ASSERT((int)EN_AUDIO_HIDEV_BW_L1 == (int)E_SOUND_HIDEV_BW_L1);
    STATIC_ASSERT((int)EN_AUDIO_HIDEV_BW_L2 == (int)E_SOUND_HIDEV_BW_L2);
    STATIC_ASSERT((int)EN_AUDIO_HIDEV_BW_L3 == (int)E_SOUND_HIDEV_BW_L3);
    STATIC_ASSERT((int)EN_AUDIO_HIDEV_BW_MAX == (int)E_SOUND_HIDEV_BW_MAX);

    SoundSetHidevMode((SOUND_HIDEV_INDEX)eHidevIndex);
#endif

#if (STB_ENABLE == 0)
#if (ENABLE_LITE_SN != 1)
    mapi_audio_amp *pAudio_amp = mapi_interface::Get_mapi_pcb()->GetAudioAmp(0);
    if(pAudio_amp != NULL)
    {
        pAudio_amp->Init();
    }
    else
    {
        printf("%s ERROR: The audio amplifier doesn't found.\n", __FUNCTION__);
    }
#endif
#else
    MSrv_Control_common::SetGpioDeviceStatus(Audio_Amplifier, TRUE);
#endif
    /*get audio volume to set variable*/
    MS_USER_SOUND_SETTING stAudioSetting;
    MSrv_Control::GetMSrvSystemDatabase()->GetAudioSetting(&stAudioSetting);
    m_SpeakerVolumeValue = stAudioSetting.Volume;
    m_HPVolumeValue = stAudioSetting.HPVolume;

// Update audio SPDIF output type with UI setting
    MS_USER_SYSTEM_SETTING stSysSetting;
    MSrv_Control::GetMSrvSystemDatabase()->GetUserSystemSetting(&stSysSetting);
    SetSPDIFmode(stSysSetting.enSPDIFMODE);
    #if (KARAOKE_ENABLE==1)
    pAudio_amp->MicVol_Set((U8)stAudioSetting.MicVal);
    pAudio_amp->MicEcho_Set(12);
    #endif
    //Set Avc_Threshold
    BSND_PARAMETER param={0};
    param.BSND_PARAM_AVC_THRESHOLD = 0x1B;
    SetBasicSoundEffect(BSND_AVC,&param);
}

#if (STR_ENABLE == 1)
//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: Suspend()
/// @brief \b Function \b Description: SSound Suspend Function
/// @param  <IN>        \b NONE
/// @return <OUT>      \b True: Success, False: Failure
//-------------------------------------------------------------------------------------------------
MAPI_BOOL MSrv_SSSound::Suspend(void)
{
    // finalize amp
    EnableAmplifier(MAPI_FALSE);

    if (mapi_interface::Get_mapi_audio()->Get_m_bthreadActive()  == TRUE)
    {
        mapi_interface::Get_mapi_audio()->Set_m_bthreadActive(FALSE);

        if (mapi_interface::Get_mapi_audio()->Get_m_AudioThread() != 0)
        {
            int intPTHChk = pthread_join(mapi_interface::Get_mapi_audio()->Get_m_AudioThread(), NULL);

            if (intPTHChk != 0)
            {
                ASSERT(0);
            }
        }
    }

    mapi_interface::Get_mapi_audio()->SetPowerOn(FALSE);

    return TRUE;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: Resume()
/// @brief \b Function \b Description: SSound Resume Function
/// @param  <IN>        \b NONE
/// @return <OUT>      \b True: Success, False: Failure
//-------------------------------------------------------------------------------------------------
MAPI_BOOL MSrv_SSSound::Resume(void)
{
    mapi_interface::Get_mapi_audio()->SetSoundMute(SOUND_MUTE_AMP_, E_MUTE_ON_);
    mapi_interface::Get_mapi_audio()->AUDIO_PreInit();

    Initialize();
	mapi_interface::Get_mapi_audio()->SetAudioSDKInfo(stSDK_AUD_UnmuteAudioAMP, 10, 0);

    MS_USER_SOUND_SETTING stSoundSetting;
    MS_USER_SYSTEM_SETTING stSysSetting;

    MSrv_Control::GetMSrvSystemDatabase()->GetSoundSetting(&stSoundSetting);
    MSrv_Control::GetMSrvSystemDatabase()->GetUserSystemSetting(&stSysSetting);

    mapi_interface::Get_mapi_audio()->SND_SetBalance(stSoundSetting.Balance);
    // add AVL function
    mapi_interface::Get_mapi_audio()->SND_EnableAutoVolume((BOOLEAN)stSysSetting.fAutoVolume);
    SetAbsoluteVolume();

    MSrv_Control::GetMSrvSystemDatabase()->GetSoundSetting(&stSoundSetting);
    for(int i = 0 ; i < 5 ; i++)
    {
        U8 u8eqValue;
        switch(i)
        {
            case 0:
                u8eqValue = stSoundSetting.astSoundModeSetting[stSoundSetting.SoundMode].EqBand1;
                break;
            case 1:
                u8eqValue = stSoundSetting.astSoundModeSetting[stSoundSetting.SoundMode].EqBand2;
                break;
            case 2:
                u8eqValue = stSoundSetting.astSoundModeSetting[stSoundSetting.SoundMode].EqBand3;
                break;
            case 3:
                u8eqValue = stSoundSetting.astSoundModeSetting[stSoundSetting.SoundMode].EqBand4;
                break;
            case 4:
                u8eqValue = stSoundSetting.astSoundModeSetting[stSoundSetting.SoundMode].EqBand5;
                break;
            default:
                u8eqValue = 50;
                break;
        }
        SND_SetEq(i, u8eqValue);
    }

    mapi_interface::Get_mapi_audio()->SPDIF_UI_SetMode(stSysSetting.enSPDIFMODE);

    SetAudysessyDynaVol(stSoundSetting.AudysseyDynamicVolume);
    SetPEQ(stSoundSetting.AudysseyEQ);

    SetVolume(GetVolume());
    SetADVolume(GetADVolume());
    return TRUE;
}
#endif

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SetPreScale()
/// @brief \b Function \b Description: Set Audio Path PreScale.
/// @param <IN>        \b u8PreScale :  PreScale value
/// @return <OUT>      \b SSSOUND_OK : Success
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::SetPreScale( MAPI_U8 u8PreScale)
{
    const AudioPath_t* const p_AudioPath = SystemInfo::GetInstance()->GetAudioPathInfo();
    mapi_interface::Get_mapi_audio()->SetPreScale(p_AudioPath[MAPI_AUDIO_PATH_MAIN_SPEAKER].u32Path, u8PreScale ); // AUDIO_T3_PATH_AUOUT0
    return SSSOUND_OK;
}

void MSrv_SSSound::SetPreScaleTable( MAPI_INPUT_SOURCE_TYPE enCurrentInputType, MAPI_U8 u8PreScale)
{
    MS_USER_SOUND_SETTING stAudioSetting;
    MSrv_Control::GetMSrvSystemDatabase()->GetAudioSetting(&stAudioSetting);

    switch(enCurrentInputType)
    {
    case MAPI_INPUT_SOURCE_DTV:
        PrescaleTable.spkr_prescale_DTV = stAudioSetting.SpeakerPreScale[enCurrentInputType];
        PrescaleTable.hp_prescale_DTV = stAudioSetting.HeadPhonePreScale[enCurrentInputType];
        PrescaleTable.sc1_prescale_DTV = stAudioSetting.SCART1PreScale[enCurrentInputType];
        PrescaleTable.sc2_prescale_DTV = stAudioSetting.SCART2PreScale[enCurrentInputType];  // LINE OUT
        //p_PrescaleTable.spdif_prescale_DTV = pAudioSetting->SPDIFPreScale[enCurrentInputType];
             break;
    case MAPI_INPUT_SOURCE_ATV:
        PrescaleTable.spkr_prescale_ATV = stAudioSetting.SpeakerPreScale[enCurrentInputType];
        PrescaleTable.hp_prescale_ATV = stAudioSetting.HeadPhonePreScale[enCurrentInputType];
        PrescaleTable.sc1_prescale_ATV = stAudioSetting.SCART1PreScale[enCurrentInputType];
        PrescaleTable.sc2_prescale_ATV = stAudioSetting.SCART2PreScale[enCurrentInputType];  // LINE OUT;
        //p_PrescaleTable.spdif_prescale_ATV = pAudioSetting->SPDIFPreScale[enCurrentInputType];
        break;
    case MAPI_INPUT_SOURCE_CVBS:
    case MAPI_INPUT_SOURCE_CVBS2:
    case MAPI_INPUT_SOURCE_SVIDEO:
    case MAPI_INPUT_SOURCE_SCART:
    case MAPI_INPUT_SOURCE_SCART2:
    case MAPI_INPUT_SOURCE_YPBPR:
    case MAPI_INPUT_SOURCE_YPBPR2:
        PrescaleTable.spkr_prescale_AV = stAudioSetting.SpeakerPreScale[enCurrentInputType];
        PrescaleTable.hp_prescale_AV = stAudioSetting.HeadPhonePreScale[enCurrentInputType];
        PrescaleTable.sc1_prescale_AV = stAudioSetting.SCART1PreScale[enCurrentInputType];
        PrescaleTable.sc2_prescale_AV = stAudioSetting.SCART2PreScale[enCurrentInputType];  // LINE OUT;
        //p_PrescaleTable.spdif_prescale_AV = pAudioSetting->SPDIFPreScale[enCurrentInputType];
        break;
    case MAPI_INPUT_SOURCE_VGA:
    case MAPI_INPUT_SOURCE_VGA2:
    case MAPI_INPUT_SOURCE_VGA3:
    case MAPI_INPUT_SOURCE_DVI:
        PrescaleTable.spkr_prescale_DVI = stAudioSetting.SpeakerPreScale[enCurrentInputType];
        PrescaleTable.hp_prescale_DVI = stAudioSetting.HeadPhonePreScale[enCurrentInputType];
        PrescaleTable.sc1_prescale_DVI = stAudioSetting.SCART1PreScale[enCurrentInputType];
        PrescaleTable.sc2_prescale_DVI = stAudioSetting.SCART2PreScale[enCurrentInputType];  // LINE OUT;
        //p_PrescaleTable.spdif_prescale_DVI = pAudioSetting->SPDIFPreScale[enCurrentInputType];
        break;
    case MAPI_INPUT_SOURCE_HDMI:
    case MAPI_INPUT_SOURCE_HDMI2:
    case MAPI_INPUT_SOURCE_HDMI3:
    case MAPI_INPUT_SOURCE_HDMI4:
        PrescaleTable.spkr_prescale_HDMI = stAudioSetting.SpeakerPreScale[enCurrentInputType];
        PrescaleTable.hp_prescale_HDMI = stAudioSetting.HeadPhonePreScale[enCurrentInputType];
        PrescaleTable.sc1_prescale_HDMI = stAudioSetting.SCART1PreScale[enCurrentInputType];
        PrescaleTable.sc2_prescale_HDMI = stAudioSetting.SCART2PreScale[enCurrentInputType];  // LINE OUT;
        //p_PrescaleTable.spdif_prescale_HDMI = pAudioSetting->SPDIFPreScale[enCurrentInputType];
        break;
    case MAPI_INPUT_SOURCE_STORAGE:
    case MAPI_INPUT_SOURCE_KTV:
        PrescaleTable.spkr_prescale_MM= stAudioSetting.SpeakerPreScale[enCurrentInputType];
        PrescaleTable.hp_prescale_MM = stAudioSetting.HeadPhonePreScale[enCurrentInputType];
        PrescaleTable.sc1_prescale_MM = stAudioSetting.SCART1PreScale[enCurrentInputType];
        PrescaleTable.sc2_prescale_MM = stAudioSetting.SCART2PreScale[enCurrentInputType];  // LINE OUT;
        //p_PrescaleTable.spdif_prescale_MM = pAudioSetting->SPDIFPreScale[enCurrentInputType];
        break;
    default:
        break;

    }
    mapi_interface::Get_mapi_audio()->SND_SetPrescaleTable(&PrescaleTable);

}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SetSpeakerVolume()
/// @brief \b Function \b Description: Set Audio volume.
/// @param <IN>        \b u8Vol      : Volume value range from 0~100
/// @return <OUT>      \b SSSOUND_OK : Success
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::SetVolume(MAPI_U8 u8Vol)
{
#if(MSTAR_TVOS == 0)   // tvos project do it in java
    MS_USER_SOUND_SETTING stAudioSetting;
#endif
    const AudioPath_t* const p_AudioPath = SystemInfo::GetInstance()->GetAudioPathInfo();
    MS_SND_NLA_POINT  stNLCruve1;
    MS_NLA_POINT  stNLCruve2;

    if(u8Vol > 100)
    {
        printf("%s(%d): Saturate to Max volume 100\n", __FUNCTION__, u8Vol);
        u8Vol = 100;
    }
#if(MSTAR_TVOS == 0)   // tvos project do it in java
    MSrv_Control::GetMSrvSystemDatabase()->GetAudioSetting(&stAudioSetting);
    stAudioSetting.Volume = u8Vol;
    MSrv_Control::GetMSrvSystemDatabase()->SetAudioSetting(&stAudioSetting);
#endif

#if (MSTAR_TVOS == 1)
    if(SystemInfo::GetInstance()->GetVolumeCompensationFlag() == TRUE)
    {
        if(u8Vol>0)
        {
            if((MSrv_Control::GetInstance()->GetCurrentInputSource()) == MAPI_INPUT_SOURCE_ATV
                    ||(MSrv_Control::GetInstance()->GetCurrentInputSource()) == MAPI_INPUT_SOURCE_DTV)
            {
                u8Vol += m_VolumeCompensation;
                m_VolumeCompensation = DEFAULT_CHANNEL_VOLUME_COMPENSATION;
                if(u8Vol > 9)
                {
                    u8Vol -= DEFAULT_CHANNEL_VOLUME_COMPENSATION;
                }
                else
                {
                    u8Vol = 0;
                }
                u8Vol = MIN(u8Vol, 100);
            }
        }
    }
#endif
    //printf("\nenInputSrc=%d", MSrv_Control::GetInstance()->GetCurrentInputSource());
    MSrv_Control::GetMSrvSystemDatabase()->GetNLASetting(&stNLCruve2, EN_NLA_VOLUME, MSrv_Control::GetInstance()->GetCurrentInputSource());
    SOUND_NLA_DBG(printf("\n########################Volume"));
    SOUND_NLA_DBG(printf("\nOSD0:%d", (U32)stNLCruve2.u32OSD_V0));
    SOUND_NLA_DBG(printf("\nOSD25:%d", (U32)stNLCruve2.u32OSD_V25));
    SOUND_NLA_DBG(printf("\nOSD50:%d", (U32)stNLCruve2.u32OSD_V50));
    SOUND_NLA_DBG(printf("\nOSD75:%d", (U32)stNLCruve2.u32OSD_V75));
    SOUND_NLA_DBG(printf("\nOSD100:%d", (U32)stNLCruve2.u32OSD_V100));

    _TransNLAPointToSNDNLAPoint(stNLCruve2, stNLCruve1);

    SOUND_NLA_DBG(printf("\n#####Volume Pre Value=%d", u8Vol));
    u8Vol = Sound_NonLinearCalculate_100(&stNLCruve1, u8Vol);
    SOUND_NLA_DBG(printf("\n#####Volume Post Value=%d", u8Vol));

    mapi_interface::Get_mapi_audio()->SetAbsoluteVolume(p_AudioPath[MAPI_AUDIO_PATH_MAIN_SPEAKER].u32Path, u8Vol, spkr_prescale);   // AUDIO_T3_PATH_AUOUT0

    if(MSrv_Control::GetInstance()->IsPipModeEnable() == FALSE) // PIP off
    {
        SetHPVolume(u8Vol);
    }
    mapi_interface::Get_mapi_audio()->SetAbsoluteVolume(p_AudioPath[MAPI_AUDIO_PATH_HDMI].u32Path, u8Vol, 0);   // AUDIO_T3_PATH_HDMI

    if (MSrv_Control::GetInstance()->GetMuteFlag())
    {
        SetMuteStatus(MUTE_BYUSER, FALSE);
    }

    return SSSOUND_OK;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SetSpeakerVolume()
/// @brief \b Function \b Description: Set Audio volume.
/// @param <IN>        \b u8Vol           : Volume value range from 0~100
/// @param <IN>        \b enAudioPathType : Different output path type
/// @return <OUT>      \b SSSOUND_OK : Success
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::SetVolume(MAPI_U8 u8Vol, MAPI_AUDIO_PATH_TYPE enAudioPathType)
{
#if(MSTAR_TVOS == 0)   // tvos project do it in java
    MS_USER_SOUND_SETTING stAudioSetting;
#endif
    const AudioPath_t* const p_AudioPath = SystemInfo::GetInstance()->GetAudioPathInfo();
    MS_SND_NLA_POINT  stNLCruve1;
    MS_NLA_POINT  stNLCruve2;

    if(u8Vol > 100)
    {
        printf("%s(%d): Saturate to Max volume 100\n", __FUNCTION__, u8Vol);
        u8Vol = 100;
    }
#if(MSTAR_TVOS == 0)   // tvos project do it in java
    MSrv_Control::GetMSrvSystemDatabase()->GetAudioSetting(&stAudioSetting);
    if(enAudioPathType == MAPI_AUDIO_PATH_HP)
    {
        stAudioSetting.HPVolume = u8Vol;
    }
    else
    {
        stAudioSetting.Volume = u8Vol;
    }

    MSrv_Control::GetMSrvSystemDatabase()->SetAudioSetting(&stAudioSetting);
#endif
    MSrv_Control::GetMSrvSystemDatabase()->GetNLASetting(&stNLCruve2, EN_NLA_VOLUME, MSrv_Control::GetInstance()->GetCurrentInputSource());
    SOUND_NLA_DBG(printf("\n########################Volume"));
    SOUND_NLA_DBG(printf("\nOSD0:%d", (U32)stNLCruve2.u32OSD_V0));
    SOUND_NLA_DBG(printf("\nOSD25:%d", (U32)stNLCruve2.u32OSD_V25));
    SOUND_NLA_DBG(printf("\nOSD50:%d", (U32)stNLCruve2.u32OSD_V50));
    SOUND_NLA_DBG(printf("\nOSD75:%d", (U32)stNLCruve2.u32OSD_V75));
    SOUND_NLA_DBG(printf("\nOSD100:%d", (U32)stNLCruve2.u32OSD_V100));

    _TransNLAPointToSNDNLAPoint(stNLCruve2, stNLCruve1);

    SOUND_NLA_DBG(printf("\n#####Volume Pre Value=%d", u8Vol));
    u8Vol = Sound_NonLinearCalculate_100(&stNLCruve1, u8Vol);
    SOUND_NLA_DBG(printf("\n#####Volume Post Value=%d", u8Vol));

    mapi_interface::Get_mapi_audio()->SetAbsoluteVolume(p_AudioPath[enAudioPathType].u32Path, u8Vol, 0);

    if (MSrv_Control::GetInstance()->GetMuteFlag())
    {
        SetMuteStatus(MUTE_BYUSER, FALSE);
    }

    return SSSOUND_OK;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SetAbsoluteVolume()
/// @brief \b Function \b Description: reload volume
/// @return <OUT>      \b SSSOUND_OK : Success
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::SetAbsoluteVolume()
{
    const AudioPath_t* const p_AudioPath = SystemInfo::GetInstance()->GetAudioPathInfo();
    MAPI_U8 u8SpeakerVolume = 0;
    MAPI_U8 u8HPVolume = 0;
    MAPI_INPUT_SOURCE_TYPE enFocusInputSource = MSrv_Control::GetInstance()->GetCurrentInputSource();
    MAPI_INPUT_SOURCE_TYPE enMainInputSource = MSrv_Control::GetInstance()->GetCurrentMainInputSource();
    MS_SND_NLA_POINT stNLCruve1;
    MS_NLA_POINT stNLCruve2;

    #if (MSTAR_TVOS == 1)
    u8SpeakerVolume = GetAudioVolume(VOL_SOURCE_SPEAKER_OUT);
    #else
    MS_USER_SOUND_SETTING stAudioSetting;
    MSrv_Control::GetMSrvSystemDatabase()->GetAudioSetting(&stAudioSetting);
    u8SpeakerVolume = stAudioSetting.Volume;
    u8HPVolume = stAudioSetting.HPVolume;
    #endif

    memset(&stNLCruve1, 0, sizeof(stNLCruve1));
    memset(&stNLCruve2, 0, sizeof(stNLCruve2));
    if(enFocusInputSource == enMainInputSource)
    {
#if (MSTAR_TVOS == 1)
        if(SystemInfo::GetInstance()->GetVolumeCompensationFlag() == TRUE)
        {
            if(u8SpeakerVolume > 0)
            {
                if((MSrv_Control::GetInstance()->GetCurrentInputSource()) == MAPI_INPUT_SOURCE_ATV)
                {
                    u8SpeakerVolume += m_VolumeCompensation;
                    m_VolumeCompensation = DEFAULT_CHANNEL_VOLUME_COMPENSATION;
                    if(u8SpeakerVolume > 9)
                    {
                        u8SpeakerVolume -= DEFAULT_CHANNEL_VOLUME_COMPENSATION;
                    }
                    else
                    {
                        u8SpeakerVolume = 0;
                    }
                    u8SpeakerVolume = MIN(u8SpeakerVolume, 100);
                }
            }
        }
#endif
        MSrv_Control::GetMSrvSystemDatabase()->GetNLASetting(&stNLCruve2, EN_NLA_VOLUME, MSrv_Control::GetInstance()->GetCurrentInputSource());
        SOUND_NLA_DBG(printf("\n########################Volume"));
        SOUND_NLA_DBG(printf("\nOSD0:%d", (U32)stNLCruve2.u32OSD_V0));
        SOUND_NLA_DBG(printf("\nOSD25:%d", (U32)stNLCruve2.u32OSD_V25));
        SOUND_NLA_DBG(printf("\nOSD50:%d", (U32)stNLCruve2.u32OSD_V50));
        SOUND_NLA_DBG(printf("\nOSD75:%d", (U32)stNLCruve2.u32OSD_V75));
        SOUND_NLA_DBG(printf("\nOSD100:%d", (U32)stNLCruve2.u32OSD_V100));

        _TransNLAPointToSNDNLAPoint(stNLCruve2, stNLCruve1);

        SOUND_NLA_DBG(printf("\n#####Volume Pre Value=%d", u8SpeakerVolume));
        u8SpeakerVolume = Sound_NonLinearCalculate_100(&stNLCruve1, u8SpeakerVolume);
#if (MSTAR_TVOS == 1)
        if(MSrv_Control::GetInstance()->IsPipModeEnable() == FALSE)
        {
            u8HPVolume = u8SpeakerVolume;
        }
        else
        {
            MS_USER_SOUND_SETTING stAudioSetting;
            MSrv_Control::GetMSrvSystemDatabase()->GetAudioSetting(&stAudioSetting);
            u8HPVolume = stAudioSetting.HPVolume;
        }
#endif
        SOUND_NLA_DBG(printf("\n#####Volume Post Value=%d", u8SpeakerVolume));

        mapi_interface::Get_mapi_audio()->SetAbsoluteVolume(p_AudioPath[MAPI_AUDIO_PATH_MAIN_SPEAKER].u32Path, u8SpeakerVolume, 0);    // AUDIO_T3_PATH_AUOUT0
        mapi_interface::Get_mapi_audio()->SetAbsoluteVolume(p_AudioPath[MAPI_AUDIO_PATH_HP].u32Path, u8HPVolume, 0);
        mapi_interface::Get_mapi_audio()->SetAbsoluteVolume(p_AudioPath[MAPI_AUDIO_PATH_HDMI].u32Path, u8SpeakerVolume, 0);   // AUDIO_T3_PATH_HDMI
    }
    else
    {
#if(MSTAR_TVOS == 1)
        MS_USER_SOUND_SETTING stAudioSetting;
        MSrv_Control::GetMSrvSystemDatabase()->GetAudioSetting(&stAudioSetting);
        u8HPVolume = stAudioSetting.HPVolume;
#endif
        MSrv_Control::GetMSrvSystemDatabase()->GetNLASetting(&stNLCruve2, EN_NLA_VOLUME, MSrv_Control::GetInstance()->GetCurrentInputSource());
        SOUND_NLA_DBG(printf("\n########################Volume"));
        SOUND_NLA_DBG(printf("\nOSD0:%d", (U32)stNLCruve2.u32OSD_V0));
        SOUND_NLA_DBG(printf("\nOSD25:%d", (U32)stNLCruve2.u32OSD_V25));
        SOUND_NLA_DBG(printf("\nOSD50:%d", (U32)stNLCruve2.u32OSD_V50));
        SOUND_NLA_DBG(printf("\nOSD75:%d", (U32)stNLCruve2.u32OSD_V75));
        SOUND_NLA_DBG(printf("\nOSD100:%d", (U32)stNLCruve2.u32OSD_V100));

        _TransNLAPointToSNDNLAPoint(stNLCruve2, stNLCruve1);

        SOUND_NLA_DBG(printf("\n#####HPVolume Pre Value=%d", u8HPVolume));
        u8HPVolume = Sound_NonLinearCalculate_100(&stNLCruve1, u8HPVolume);
        SOUND_NLA_DBG(printf("\n#####HPVolume Post Value=%d", u8HPVolume));
        mapi_interface::Get_mapi_audio()->SetAbsoluteVolume(p_AudioPath[MAPI_AUDIO_PATH_HP].u32Path, u8HPVolume, hp_prescale);    // AUDIO_T3_PATH_AUOUT0
    }
    return SSSOUND_OK;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SetAbsoluteVolume(MAPI_SCALER_WIN eWin)
/// @brief \b Function \b Description: reload volume
/// @param eWin 		\b IN: set main or sub
/// @return <OUT>      \b SSSOUND_OK : Success
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::SetAbsoluteVolume(MAPI_SCALER_WIN eWin)
{
    const AudioPath_t* const p_AudioPath = SystemInfo::GetInstance()->GetAudioPathInfo();
    MAPI_U8 u8SpeakerVolume = 0;
    MAPI_U8 u8HPVolume = 0;
    MS_SND_NLA_POINT stNLCruve1;
    MS_NLA_POINT stNLCruve2;

    #if (MSTAR_TVOS == 1)
    u8SpeakerVolume = GetAudioVolume(VOL_SOURCE_SPEAKER_OUT);
    #else
    MS_USER_SOUND_SETTING stAudioSetting;
    MSrv_Control::GetMSrvSystemDatabase()->GetAudioSetting(&stAudioSetting);
    u8SpeakerVolume = stAudioSetting.Volume;
    u8HPVolume = stAudioSetting.HPVolume;
    #endif

    memset(&stNLCruve1, 0, sizeof(stNLCruve1));
    memset(&stNLCruve2, 0, sizeof(stNLCruve2));
    if(eWin == MAPI_MAIN_WINDOW)
    {
#if (MSTAR_TVOS == 1)
        if(SystemInfo::GetInstance()->GetVolumeCompensationFlag() == TRUE)
        {
            if(u8SpeakerVolume > 0)
            {
                if((MSrv_Control::GetInstance()->GetCurrentInputSource()) == MAPI_INPUT_SOURCE_ATV)
                {
                    u8SpeakerVolume += m_VolumeCompensation;
                    m_VolumeCompensation = DEFAULT_CHANNEL_VOLUME_COMPENSATION;
                    if(u8SpeakerVolume > 9)
                    {
                        u8SpeakerVolume -= DEFAULT_CHANNEL_VOLUME_COMPENSATION;
                    }
                    else
                    {
                        u8SpeakerVolume = 0;
                    }
                    u8SpeakerVolume = MIN(u8SpeakerVolume, 100);
                }
            }
        }
#endif
        MSrv_Control::GetMSrvSystemDatabase()->GetNLASetting(&stNLCruve2, EN_NLA_VOLUME, MSrv_Control::GetInstance()->GetCurrentInputSource());
        SOUND_NLA_DBG(printf("\n########################Volume"));
        SOUND_NLA_DBG(printf("\nOSD0:%d", (U32)stNLCruve2.u32OSD_V0));
        SOUND_NLA_DBG(printf("\nOSD25:%d", (U32)stNLCruve2.u32OSD_V25));
        SOUND_NLA_DBG(printf("\nOSD50:%d", (U32)stNLCruve2.u32OSD_V50));
        SOUND_NLA_DBG(printf("\nOSD75:%d", (U32)stNLCruve2.u32OSD_V75));
        SOUND_NLA_DBG(printf("\nOSD100:%d", (U32)stNLCruve2.u32OSD_V100));

        _TransNLAPointToSNDNLAPoint(stNLCruve2, stNLCruve1);

        SOUND_NLA_DBG(printf("\n#####Volume Pre Value=%d", u8SpeakerVolume));
        u8SpeakerVolume = Sound_NonLinearCalculate_100(&stNLCruve1, u8SpeakerVolume);
#if (MSTAR_TVOS == 1)
        if(MSrv_Control::GetInstance()->IsPipModeEnable() == FALSE)
        {
            u8HPVolume = u8SpeakerVolume;
        }
        else
        {
            MS_USER_SOUND_SETTING stAudioSetting;
            MSrv_Control::GetMSrvSystemDatabase()->GetAudioSetting(&stAudioSetting);
            u8HPVolume = stAudioSetting.HPVolume;
        }
#endif
        SOUND_NLA_DBG(printf("\n#####Volume Post Value=%d", u8SpeakerVolume));

        mapi_interface::Get_mapi_audio()->SetAbsoluteVolume(p_AudioPath[MAPI_AUDIO_PATH_MAIN_SPEAKER].u32Path, u8SpeakerVolume, 0);    // AUDIO_T3_PATH_AUOUT0
        mapi_interface::Get_mapi_audio()->SetAbsoluteVolume(p_AudioPath[MAPI_AUDIO_PATH_HP].u32Path, u8HPVolume, 0);
        mapi_interface::Get_mapi_audio()->SetAbsoluteVolume(p_AudioPath[MAPI_AUDIO_PATH_HDMI].u32Path, u8SpeakerVolume, 0);   // AUDIO_T3_PATH_HDMI
    }
    else
    {
#if(MSTAR_TVOS == 1)
        MS_USER_SOUND_SETTING stAudioSetting;
        MSrv_Control::GetMSrvSystemDatabase()->GetAudioSetting(&stAudioSetting);
        u8HPVolume = stAudioSetting.HPVolume;
#endif

        MSrv_Control::GetMSrvSystemDatabase()->GetNLASetting(&stNLCruve2, EN_NLA_VOLUME, MSrv_Control::GetInstance()->GetCurrentInputSource());
        SOUND_NLA_DBG(printf("\n########################Volume"));
        SOUND_NLA_DBG(printf("\nOSD0:%d", (U32)stNLCruve2.u32OSD_V0));
        SOUND_NLA_DBG(printf("\nOSD25:%d", (U32)stNLCruve2.u32OSD_V25));
        SOUND_NLA_DBG(printf("\nOSD50:%d", (U32)stNLCruve2.u32OSD_V50));
        SOUND_NLA_DBG(printf("\nOSD75:%d", (U32)stNLCruve2.u32OSD_V75));
        SOUND_NLA_DBG(printf("\nOSD100:%d", (U32)stNLCruve2.u32OSD_V100));

        _TransNLAPointToSNDNLAPoint(stNLCruve2, stNLCruve1);
        SOUND_NLA_DBG(printf("\n#####HPVolume Pre Value=%d", u8HPVolume));
        u8HPVolume = Sound_NonLinearCalculate_100(&stNLCruve1, u8HPVolume);
        SOUND_NLA_DBG(printf("\n#####HPVolume Post Value=%d", u8HPVolume));
        mapi_interface::Get_mapi_audio()->SetAbsoluteVolume(p_AudioPath[MAPI_AUDIO_PATH_HP].u32Path, u8HPVolume, hp_prescale);    // AUDIO_T3_PATH_AUOUT0
    }
    return SSSOUND_OK;
}
//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: GetSpeakerVolume()
/// @brief \b Function \b Description: Get Audio Volume.
/// @param <IN>        \b NONE
/// @return <OUT>      \b u8Vol      : Volume value range from 0~100
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::GetVolume()
{
    MS_USER_SOUND_SETTING stAudioSetting;

    MSrv_Control::GetMSrvSystemDatabase()->GetAudioSetting(&stAudioSetting);

    return stAudioSetting.Volume;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SetHPVolume()
/// @brief \b Function \b Description: Set Audio HeadPhone Volume.
/// @param <IN>        \b u8Vol      : Volume value range from 0~100
/// @return <OUT>      \b SSSOUND_OK : Success
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::SetHPVolume(MAPI_U8 u8Vol)
{
#if(MSTAR_TVOS == 0)   // tvos project do it in java
    MS_USER_SOUND_SETTING stAudioSetting;
#endif
    const AudioPath_t* const p_AudioPath = SystemInfo::GetInstance()->GetAudioPathInfo();

    if(u8Vol > 100)
    {
        printf("%s(%d): err !  Max HP volume is 100\n", __FUNCTION__, u8Vol);
        u8Vol = 100;
    }

#if(MSTAR_TVOS == 0)   // tvos project do it in java
    MSrv_Control::GetMSrvSystemDatabase()->GetAudioSetting(&stAudioSetting);
    stAudioSetting.HPVolume = u8Vol;
    MSrv_Control::GetMSrvSystemDatabase()->SetAudioSetting(&stAudioSetting);
#endif
    mapi_interface::Get_mapi_audio()->SetAbsoluteVolume(p_AudioPath[MAPI_AUDIO_PATH_HP].u32Path, u8Vol, 0);
    return SSSOUND_OK;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: GetHPVolume()
/// @brief \b Function \b Description: Get Audio HeadPhone Volume.
/// @param <IN>        \b NONE
/// @return <OUT>      \b u8Vol      : Volume value range from 0~100
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::GetHPVolume()
{
    MS_USER_SOUND_SETTING stAudioSetting;

    MSrv_Control::GetMSrvSystemDatabase()->GetAudioSetting(&stAudioSetting);

    return stAudioSetting.HPVolume;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SetADVolume
/// @brief \b Function \b Description: Set AD (Audio Description) volume
/// @param <IN>        \b u8Vol      : Volume value range from 0~100
/// @return <OUT>      \b SSSOUND_OK : Success
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::SetADVolume(MAPI_U8 u8Vol)
{
    MS_USER_SOUND_SETTING stAudioSetting;

    if(u8Vol > 100)
    {
        printf("%s(%d): err !  Max volume is 100\n", __FUNCTION__, u8Vol);
        u8Vol = 100;
    }

    MSrv_Control::GetMSrvSystemDatabase()->GetAudioSetting(&stAudioSetting);
#if(SQL_DB_ENABLE == 1 && MSTAR_TVOS == 1)
    MS_USER_SOUND_SETTING stOrgAudioSetting = stAudioSetting;
#endif
    stAudioSetting.ADVolume = u8Vol;
#if(SQL_DB_ENABLE == 1 && MSTAR_TVOS == 1)
    MSrv_Control::GetMSrvSystemDatabase()->SetAudioSetting(&stAudioSetting, &stOrgAudioSetting);
#else
    MSrv_Control::GetMSrvSystemDatabase()->SetAudioSetting(&stAudioSetting);
#endif
    mapi_interface::Get_mapi_audio()->DECODER_SetADAbsoluteVolume(u8Vol);
    return SSSOUND_OK;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: GetADVolume
/// @brief \b Function \b Description: Get AD (Audio Description) volume value
/// @param <IN>        \b NONE
/// @return <OUT>      \b MAPI_U8: AD volume value in system database
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::GetADVolume()
{
    MS_USER_SOUND_SETTING stAudioSetting;

    MSrv_Control::GetMSrvSystemDatabase()->GetAudioSetting(&stAudioSetting);

    return stAudioSetting.ADVolume;
}

/*add by owen.qin begin*/
//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SetADEnable
/// @brief \b Function \b Description: set AD (Audio Description) status
/// @param <IN>        \b enable
/// @return <OUT>      \b MAPI_U8:
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::SetADEnable(MAPI_BOOL enable)
{
#ifdef TARGET_BUILD
    MS_USER_SOUND_SETTING stAudioTemp;
    MSrv_Control::GetMSrvSystemDatabase()->GetAudioSetting(&stAudioTemp);
    stAudioTemp.bEnableAD = (BOOLEAN)enable;
    MSrv_Control::GetMSrvSystemDatabase()->SetAudioSetting(&stAudioTemp);
#endif
#ifdef TARGET_BUILD
#if (MSTAR_TVOS == 1)
    MAPI_INPUT_SOURCE_TYPE enSrcType = MSrv_Control::GetInstance()->GetCurrentInputSource();
    if(enSrcType == MAPI_INPUT_SOURCE_DTV)
    {
        MSrv_Control::GetMSrvDtv()->UpdateAudio();
    }
#endif
#endif
    return SSSOUND_OK;
}
/*add by owen.qin end*/

/*add by owen.qin begin*/
MAPI_U8 MSrv_SSSound::SetAutoHOHEnable(MAPI_BOOL enable)
{

	printf("MSrv_SSSound::SetAutoHOHEnable enable=%d\n",enable);
#if (DVB_ENABLE == 1)
    MS_USER_SUBTITLE_SETTING subtitle_setting;
    MSrv_System_Database_DVB *p;
    p = dynamic_cast<MSrv_System_Database_DVB *>(MSrv_Control::GetMSrvSystemDatabase());
    ASSERT(p);

    p->GetSubtitleSetting(&subtitle_setting);
    subtitle_setting.fHardOfHearing = (U8)enable;
    p->SetSubtitleSetting(&subtitle_setting);
    if(((MSrv_Control::GetInstance())->GetCurrentInputSource()) == MAPI_INPUT_SOURCE_DTV)
    {
    #if (MSTAR_TVOS == 1)
    #if (ATSC_SYSTEM_ENABLE == 0)
    ((MSrv_DTV_Player_DVB*)(MSrv_Control::GetMSrvDtv()))->ChangeSetting(HEARING_IMPAIRED_CHANGE);
    #endif
	#endif
    }
#endif

    return SSSOUND_OK;


}
/*add by owen.qin end*/
//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: EnableAmplifier()
/// @brief \b Function \b Description: Set Audio Amplifier ON or OFF.
/// @param <IN>        \b u8Vol      : Enable: 1, Disable: 0
/// @return <OUT>      \b SSSOUND_OK : Success
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::EnableAmplifier(MAPI_BOOL EnAmp)
{
#if (ENABLE_LITE_SN != 1)
    mapi_audio_amp *pAudioAmp = mapi_interface::Get_mapi_pcb()->GetAudioAmp(0);
    if(pAudioAmp != NULL)
    {
        if(MAPI_TRUE == EnAmp)
        {
            pAudioAmp->Init();
        }
        else
        {
            pAudioAmp->Finalize();
        }
    }
    else
#endif
    {
        MSrv_Control_common::SetGpioDeviceStatus(Audio_Amplifier, EnAmp);
    }

    return SSSOUND_OK;
}

MAPI_U8 MSrv_SSSound::SetSNDMpegInfo(const Audio_MPEG_infoType_ infoType, const MAPI_U32 param1, const MAPI_U32 param2)
{
    mapi_interface::Get_mapi_audio()->DECODER_SetMpegInfo(infoType,  param1,  param2);
    return SSSOUND_OK;
}

MAPI_U32 MSrv_SSSound::GetSNDMpegInfo(Audio_MPEG_infoType_ infoType)
{
    return mapi_interface::Get_mapi_audio()->DECODER_GetMpegInfo(infoType);

    //return SSSOUND_OK;
}

MAPI_U8 MSrv_SSSound::SetSNDAC3Info(const Audio_AC3_infoType_ infoType,MAPI_U32 param1,MAPI_U32 param2)
{
    mapi_interface::Get_mapi_audio()->DECODER_SetAC3Info(infoType,  param1,  param2);
    return SSSOUND_OK;
}

MAPI_U32 MSrv_SSSound::GetSNDAC3Info(Audio_AC3_infoType_ infoType)
{
    return mapi_interface::Get_mapi_audio()->DECODER_GetAC3Info(infoType);
}

MAPI_U8 MSrv_SSSound::SetSNDAC3PInfo(const Audio_AC3P_infoType_ infoType,MAPI_U32 param1,MAPI_U32 param2)
{
    mapi_interface::Get_mapi_audio()->DECODER_SetAC3PInfo(infoType,  param1,  param2);
    return SSSOUND_OK;
}

MAPI_U32 MSrv_SSSound::GetSNDAC3PInfo(Audio_AC3P_infoType_ infoType)
{
    return mapi_interface::Get_mapi_audio()->DECODER_GetAC3PInfo(infoType);
}

MAPI_U8 MSrv_SSSound::SetSNDAACInfo(const Audio_AAC_infoType_ infoType,MAPI_U32 param1,MAPI_U32 param2)
{
    mapi_interface::Get_mapi_audio()->DECODER_SetAACInfo(infoType,  param1,  param2);
    return SSSOUND_OK;
}

MAPI_U32 MSrv_SSSound::GetSNDAACInfo(Audio_AAC_infoType_ infoType)
{
    return mapi_interface::Get_mapi_audio()->DECODER_GetAACInfo(infoType);
}

MAPI_U8 MSrv_SSSound::SetSNDDTSInfo(const Audio_DTS_infoType_ infoType,MAPI_U32 param1,MAPI_U32 param2)
{
    //mapi_interface::Get_mapi_audio()->DECODER_SetDTSInfo(infoType,  param1,  param2);//TODO

    return SSSOUND_OK;
}

MAPI_U8 MSrv_SSSound::SetSNDWMAInfo( const Audio_WMA_infoType_ infoType,MAPI_U32 param1,MAPI_U32 param2)
{
    mapi_interface::Get_mapi_audio()->DECODER_SetWmaInfo( infoType, param1, param2);

    return SSSOUND_OK;

}
//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: GetMuteStatus()
/// @brief \b Function \b Description: Get the audio mute status by typing status type
/// @param <IN>        \b mute_status_type   :
/// @return <OUT>      \b True: mute on, False: mute off
//-------------------------------------------------------------------------------------------------
MAPI_BOOL MSrv_SSSound::GetMuteStatus(const MAPI_SOUND_MUTE_STATUS_TYPE mute_status_type)
{
    return mapi_interface::Get_mapi_audio()->SND_GetSoundMuteStatusType(mute_status_type);
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SetMuteStatus()
/// @brief \b Function \b Description: Set audio mute status
/// @param <IN>        \b muteType: mute type
///                    \b onOff: 1 is on, 0 is Off
/// @param eType    \b IN: For Main /SUB / SCART
/// @return <OUT>      \b SSSOUND_OK: Success
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::SetMuteStatus(SSSOUND_MUTE_TYPE muteType, MAPI_BOOL onOff, MSRV_AUDIO_PROCESSOR_TYPE eType)
{
    if( false == (MSrv_Control::GetInstance()->IsPipModeEnable()))
    {
        mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(_TransMSrvMuteTypeToSDKMuteType(muteType,onOff), E_AUDIOMUTESOURCE_ACTIVESOURCE_);
    }
    else
    {
        if(eType == MSRV_AUDIO_PROCESSOR_MAIN)
        {
            mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(_TransMSrvMuteTypeToSDKMuteType(muteType,onOff), E_AUDIOMUTESOURCE_MAINSOURCE_);
        }
        else if(eType == MSRV_AUDIO_PROCESSOR_SUB)
        {
            mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(_TransMSrvMuteTypeToSDKMuteType(muteType,onOff), E_AUDIOMUTESOURCE_SUBSOURCE_);
        }
        else
        {
            ASSERT(0);
        }
    }

    MSrv_Control::GetInstance()->SetMuteFlag(onOff);
    return SSSOUND_OK;
}

//-------------------------------------------------------------------------------------------------
/// Get Audio internal/ouput port mute status .
/// @param ePort   \b IN: output port type
/// @return MAPI_BOOL: RETURN_OK, RETURN_NOTOK, RETURN_NOTSUPPORT
//-------------------------------------------------------------------------------------------------
MAPI_BOOL MSrv_SSSound::GetOutputMuteStatus(AUDIO_BASIC_PORT_TYPE ePort)
{
    MAPI_BOOL bMuteStatus = FALSE;

    switch(ePort)
    {
    case PORT_SPEAKER:
        bMuteStatus = mapi_interface::Get_mapi_audio()->SND_GetSoundMuteResult(AUD_SPEAKER_OUT);
        break;

    case PORT_HP:
        bMuteStatus = mapi_interface::Get_mapi_audio()->SND_GetSoundMuteResult(AUD_HP_OUT);
        break;

    case PORT_MONITOR_OUT:
        bMuteStatus = mapi_interface::Get_mapi_audio()->SND_GetSoundMuteResult(AUD_SCART2_OUT);
        break;

    case PORT_SCART:
        bMuteStatus = mapi_interface::Get_mapi_audio()->SND_GetSoundMuteResult(AUD_SCART1_OUT);
        break;

    case PORT_SPDIF:
        bMuteStatus = mapi_interface::Get_mapi_audio()->SND_GetSoundMuteResult(AUD_SPDIF_OUT);
        break;

    case PORT_MAIN_CHANNEL:
        bMuteStatus = mapi_interface::Get_mapi_audio()->SND_GetSoundMuteResult(AUD_MAIN_SOURCE_PORT);
        break;

    case PORT_SECOND_CHANNEL:
        bMuteStatus = mapi_interface::Get_mapi_audio()->SND_GetSoundMuteResult(AUD_SUB_SOURCE_PORT);
        break;

    case PORT_DATA_READER:
        bMuteStatus = mapi_interface::Get_mapi_audio()->SND_GetSoundMuteResult(AUD_DATA_IN_PORT);
        break;

    case PORT_ARC:
        bMuteStatus = mapi_interface::Get_mapi_audio()->SND_GetSoundMuteResult(AUD_ARC_OUT);
        break;

    case PORT_USER_DEFINE1:
    case PORT_USER_DEFINE2:
    case PORT_USER_DEFINE3:
    case PORT_MAX:
    default:
        printf("\r\n=======Unsupport Mute Port !!=======");
        bMuteStatus = FALSE;
        break;
    }

    return bMuteStatus;
}


//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SetMtsMode
/// @brief \b Function \b Description: Set Audio ATV MTS mode
/// @param <IN>        \b u8SifSoundMode : MTS mode (Mono/Stereo/Dual)
/// @return <OUT>      \b SSSOUND_OK: Success
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::SetMtsMode(MAPI_U8 u8SifSoundMode)
{
    mapi_interface::Get_mapi_audio()->SIF_SetMtsMode(u8SifSoundMode);
    return SSSOUND_OK;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: GetMtsMode
/// @brief \b Function \b Description: Get Audio ATV MTS mode
/// @param <IN>        \b NONE
/// @return <OUT>      \b MTS mode (Mono/Stereo/Dual)
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::GetMtsMode()
{
    return mapi_interface::Get_mapi_audio()->SIF_GetMtsMode();
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SetToNextMtsMode
/// @brief \b Function \b Description: Set Audio ATV Next MTS mode
/// @param <IN>        \b NONE
/// @return <OUT>      \b SSSOUND_OK: Success
//-------------------------------------------------------------------------------------------------
U8 mtscircle = 0;
MAPI_U8 MSrv_SSSound::SetToNextMtsMode(void)
{
    U8 u8MtsModes, u8MtsSetMode = E_AUDIOMODE_MONO_;

    u8MtsModes = mapi_interface::Get_mapi_audio()->SIF_GetSoundMode();
    switch((AUDIOMODE_TYPE_)u8MtsModes)
    {
    case E_AUDIOMODE_MONO_:
        u8MtsSetMode = E_AUDIOMODE_MONO_;
        break;

    case E_AUDIOMODE_G_STEREO_:
    case E_AUDIOMODE_K_STEREO_:
        u8MtsSetMode = Stereo[mtscircle % sizeof(Stereo)];
        mtscircle++;
        break;

    case E_AUDIOMODE_DUAL_A_:
        u8MtsSetMode = A2_Dual[mtscircle % sizeof(A2_Dual)];
        mtscircle++;
        break;

    case E_AUDIOMODE_NICAM_STEREO_:
        u8MtsSetMode = NICAM_Stereo[mtscircle % sizeof(NICAM_Stereo)];
        mtscircle++;
        break;

    case E_AUDIOMODE_NICAM_DUAL_A_:
        u8MtsSetMode = NICAM_Dual[mtscircle % sizeof(NICAM_Dual)];
        mtscircle++;
        break;

    case E_AUDIOMODE_NICAM_MONO_:
        u8MtsSetMode = NICAM_Mono[mtscircle % sizeof(NICAM_Mono)];
        mtscircle++;
        break;

    case E_AUDIOMODE_MONO_SAP_:
        u8MtsSetMode = BTSC_Mono_Sap[mtscircle % sizeof(BTSC_Mono_Sap)];
        mtscircle++;
        break;

    case E_AUDIOMODE_STEREO_SAP_:
        u8MtsSetMode = BTSC_Stereo_Sap[mtscircle % sizeof(BTSC_Stereo_Sap)];
        mtscircle++;
        break;

    default:
        printf("\r\n sound mode unknown!! \r\n");
        break;
    }

    mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_INTERNAL_1_MUTEON_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
    mapi_interface::Get_mapi_audio()->SIF_SetMtsMode((AUDIOMODE_TYPE_)u8MtsSetMode);
    usleep(AU_DELAY_FOR_ENTERING_MUTE * 1000);
    mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_INTERNAL_1_MUTEOFF_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);

    return SSSOUND_OK;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: GetSoundMode()
/// @brief \b Function \b Description:  Get the available ATV MTS mode (Mono/Stereo/Dual)
/// @param <IN>        \b NONE
/// @return <OUT>      \b MTS mode (Mono/Stereo/Dual)
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::GetSoundMode()
{
    return mapi_interface::Get_mapi_audio()->SIF_GetSoundMode();
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: DECODER_SetOutputMode()
/// @brief \b Function \b Description: Set main speaker output to  Stereo/LL/RR/Mixed.
/// @param <IN>        \b mode: Stereo/LL/RR/Mixed LR
/// @return <OUT>      \b SSSOUND_OK: Success
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::DECODER_SetOutputMode(En_DVB_soundModeType_ mode)
{
    mapi_interface::Get_mapi_audio()->DECODER_SetOutputMode(mode);
    return SSSOUND_OK;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SND_SetTreble
/// @brief \b Function \b Description: Set sound effect treble setting.
/// @param <IN>        \b u8Treble \b treble setting ( 0 ~ 100 percent )
/// @return <OUT>      \b SSSOUND_OK: Success
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::SND_SetTreble(MAPI_U8 u8Treble)
{
    mapi_interface::Get_mapi_audio()->SND_SetTreble(u8Treble);
    return SSSOUND_OK;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SND_GetTreble
/// @brief \b Function \b Description: Get sound effect treble setting value
/// @param <IN>        \b NONE
/// @return <OUT>      \b treble setting value( 0 ~ 100 percent )
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::SND_GetTreble()
{
    return mapi_interface::Get_mapi_audio()->SND_GetTreble();
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SND_SetBass
/// @brief \b Function \b Description: Set sound effect bass setting.
/// @param <IN>        \b u8Bass : Bass value (0 ~ 100)
/// @return <OUT>      \b SSSOUND_OK: Success
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::SND_SetBass(MAPI_U8 u8Bass)
{
    mapi_interface::Get_mapi_audio()->SND_SetBass(u8Bass);
    return SSSOUND_OK;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SND_GetBass
/// @brief \b Function \b Description: Get bass value
/// @param <IN>        \b NONE
/// @return <OUT>      \b Current bass value, 0 ~ 100
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::SND_GetBass()
{
    return mapi_interface::Get_mapi_audio()->SND_GetBass();
}

//-------------------------------------------------------------------------------------------------
/// Set balance sound
/// @param u8Balance        \b IN: Balance value (0 ~ 100)
///                                - 0   :  R channel is muted ...
///                                - 50  :  L & R channel output the same level...
///                                - 100 :  L channel is muted .
///
/// @return SSSOUND_OK: Success
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::SND_SetBalance(MAPI_U8 u8Balance)
{
    mapi_interface::Get_mapi_audio()->SND_SetBalance(u8Balance);
    return SSSOUND_OK;
}

//-------------------------------------------------------------------------------------------------
/// Get balance value
///
/// @return MAPI_U8: Current balance value, 0 ~ 100
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::SND_GetBalance()
{
    return mapi_interface::Get_mapi_audio()->SND_GetBalance();
}

//-------------------------------------------------------------------------------------------------
/// Enable/Disable auto volume
/// @param enAutoVol         \b IN: On: 1, Off: 0
///
/// @return SSSOUND_OK: Success
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::SetAutoVolume(MAPI_BOOL enAutoVol)
{
    MAPI_BOOL usermute = MAPI_FALSE;

    if(GetMuteStatus(Mute_Status_bByUserAudioMute) == MAPI_TRUE)
    {
        usermute = MAPI_TRUE;
    }

    mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_BYUSER_MUTEON_, E_AUDIOMUTESOURCE_MAINSOURCE_);
    usleep(25 * 1000); // delay 25 ms
    mapi_interface::Get_mapi_audio()->SND_EnableAutoVolume(enAutoVol);
    usleep(25 * 1000); // delay 25 ms

    if(usermute == MAPI_TRUE)   // need to keep user mute after AVC on/off
    {
        mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_BYUSER_MUTEON_, E_AUDIOMUTESOURCE_MAINSOURCE_);
    }
    else
    {
        mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_BYUSER_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
    }

    return SSSOUND_OK;
}

//-------------------------------------------------------------------------------------------------
/// Get status of Auto volume
///
/// @return True: Enable Auto volume
/// @return False: Disable Auto volume
//-------------------------------------------------------------------------------------------------
MAPI_BOOL MSrv_SSSound::GetAutoVolume()
{
    return mapi_interface::Get_mapi_audio()->SND_GetAutoVolumeOnOff();
}

//-------------------------------------------------------------------------------------------------
/// Set surround sound effect to Off/On/SRS_TSXT
/// @param mode             \b IN: surround sound mode  Off: 0, On: 1, SRS_TSXT:2
///
/// @return SSSOUND_OK: Success
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::SetSurroundSound(SOUND_SURROUND_MODE mode)
{
    MAPI_BOOL usermute = MAPI_FALSE;
    usermute = GetMuteStatus(Mute_Status_bByUserAudioMute);

    if(usermute == MAPI_FALSE)
    {
        mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_BYUSER_MUTEON_, E_AUDIOMUTESOURCE_MAINSOURCE_);
        usleep(AU_DELAY_FOR_SNDEFFECT_MUTE*1000); // mute time for entering enable/disable
    }

    if(mode == E_SURROUND_MODE_OFF)
    {
        m_bAudyessySRSMode = MAPI_FALSE;
        mapi_interface::Get_mapi_audio()->SND_EnableSurround(FALSE);
        mapi_interface::Get_mapi_audio()->ADVSND_ProcessEnable(ADV_NONE_);
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_TSXT_TRUBASS_, FALSE);
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_TSXT_DC_, FALSE);
        m_surrMode = E_SURROUND_MODE_OFF;
    }
    else if(mode == E_SURROUND_MODE_ON)
    {
        m_bAudyessySRSMode = MAPI_FALSE;
        mapi_interface::Get_mapi_audio()->SND_EnableSurround(TRUE);
        mapi_interface::Get_mapi_audio()->ADVSND_ProcessEnable(ADV_NONE_);
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_TSXT_TRUBASS_, FALSE);
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_TSXT_DC_, FALSE);
        m_surrMode = E_SURROUND_MODE_ON;
    }
    else if(mode == E_SURROUND_MODE_SRS_TSXT)
    {
        m_bAudyessySRSMode = MAPI_TRUE;
        mapi_interface::Get_mapi_audio()->ADVSND_ProcessEnable(SRS_TSXT_);
        mapi_interface::Get_mapi_audio()->SND_EnableSurround(FALSE);        // disable MStar Surround while enable SRS
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_TSXT_TRUBASS_, TRUE);
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_TSXT_DC_, TRUE);
        m_surrMode = E_SURROUND_MODE_SRS_TSXT;

        mapi_interface::Get_mapi_audio()->ADVSND_ProcessEnable(SRS_TSHD_);
        mapi_interface::Get_mapi_audio()->SND_EnableSurround(FALSE);        // disable MStar Surround while enable SRS
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_TSHD_TRUBASS_, TRUE);
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_TSHD_DC_, TRUE);
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_TSHD_DEFINITION_, TRUE);
        m_surrMode = E_SURROUND_MODE_SRS_TSXT;
    }
#if (AU_SUPPORT_DBX == 1)
    else if(mode == E_SURROUND_MODE_DBX)
    {
        mapi_interface::Get_mapi_audio()->ADVSND_ProcessEnable(DBX_);
        mapi_interface::Get_mapi_audio()->SND_EnableSurround(FALSE);        // disable MStar Surround while enable SRS
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_TSXT_TRUBASS_, FALSE);
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_TSXT_DC_, FALSE);
        m_surrMode = E_SURROUND_MODE_DBX;
    }
#endif

    if(usermute == MAPI_FALSE) // check if bByUserAudioMute keep mute or not
    {
        usleep(AU_DELAY_FOR_SNDEFFECT_UNMUTE*1000); // mute time for leaving enable/disable, prevent un-mute pop noise
        mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_BYUSER_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
    }

    return SSSOUND_OK;
}

//-------------------------------------------------------------------------------------------------
/// Get surround sound mode
///
/// @return SOUND_SURROUND_MODE: Current surround mode  Off: 0, On: 1, SRS_TSXT:2
//-------------------------------------------------------------------------------------------------
SOUND_SURROUND_MODE MSrv_SSSound::GetSurroundSound()
{
    return m_surrMode;
}

//-------------------------------------------------------------------------------------------------
/// Set the specific band level of 5-band EQ
/// @param u8band                     \b IN: the band to set, 0 ~ 4
/// @param u8level                     \b IN: the level set to the band (0 ~ 100)
///
/// @return SSSOUND_OK: Success
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::SND_SetEq(MAPI_U8 u8band, MAPI_U8 u8level)
{
    mapi_interface::Get_mapi_audio()->SND_SetEq(u8band, u8level);
    return SSSOUND_OK;
}

#if (ENABLE_LITE_SN != 1)
//-------------------------------------------------------------------------------------------------
/// SND_SetSubWooferVolume:To Set sub woofer volume and mute.
/// @param bMute    \b IN: TRUE for Mute enable; FALSE for Mute disable.
/// @param u8Val    \b IN: Volume Value(0~100), set 0 is mute.
/// @return       \b OUT: MAPI_TRUE or MAPI_FALSE
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::SND_SetSubWooferVolume(MAPI_BOOL bMute, MAPI_U8 u8Val)
{
    mapi_audio_amp *pAudio_amp = mapi_interface::Get_mapi_pcb()->GetAudioAmp(0);
    if(pAudio_amp != NULL)
    {
        pAudio_amp->AmpSetSubWooferVolume(bMute,u8Val);
        return MAPI_TRUE;
    }
    else
    {
        printf("%s ERROR: The audio amplifier doesn't found.\n", __FUNCTION__);
    }
    return MAPI_FALSE;
}

//-------------------------------------------------------------------------------------------------
/// Reserve extend command for customer. If you don't need it, you skip it.
/// @param u8SubCmd     \b IN: Commad defined by the customer.
/// @param u32Param1    \b IN: Defined by the customer.
/// @param u32Param2    \b IN: Defined by the customer.
/// @param pvoidParam3    \b IN: Defined by the customer.
/// @return             \b OUT: MAPI_TRUE or MAPI_FALSE
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::SND_AmpExtendCmd(MAPI_U8 u8SubCmd,MAPI_U32 u32Param1,MAPI_U32 u32Param2,void * pvoidParam3)
{
    mapi_audio_amp *pAudio_amp = mapi_interface::Get_mapi_pcb()->GetAudioAmp(0);
    if(pAudio_amp != NULL)
    {
        pAudio_amp->ExtendCmd(u8SubCmd,u32Param1,u32Param2,pvoidParam3);
        return MAPI_TRUE;
    }
    else
    {
        printf("%s ERROR: The audio amplifier doesn't found.\n", __FUNCTION__);
    }
    return MAPI_FALSE;
}
#endif




//-------------------------------------------------------------------------------------------------
/// Set each volume in KTV mode
/// @param VolType          \b IN: Audio volume type
/// @param u8Vol1            \b IN: MSB 7-bit register value of 10-bit u8Volume (0x00 ~ 0x7E, gain: +12db to   -114db (-1 db per step))
/// @param u8Vol2            \b IN: LSB 3-bit register value of 10-bit u8Volume (0x00 ~ 0x07, gain:  -0db to -0.875db (-0.125 db per step))
///
/// @return SSSOUND_OK: Success
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::KTV_SetMixModeVolume(AUDIO_MIX_VOL_TYPE_ VolType,  MAPI_U8 u8Vol1, MAPI_U8 u8Vol2)
{
    mapi_interface::Get_mapi_audio()->KTV_SetMixModeVolume(VolType, u8Vol1, u8Vol2);
    return SSSOUND_OK;
}

//-------------------------------------------------------------------------------------------------
/// Enable/Disable each volume in KTV mode to mute
/// @param VolType          \b IN: Audio volume type
/// @param EnMute           \b IN: Enable: 1, Disable: 0
///
/// @return SSSOUND_OK: Success
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::KTV_SetMixModeMute(AUDIO_MIX_VOL_TYPE_ VolType, MAPI_BOOL EnMute)
{
    mapi_interface::Get_mapi_audio()->KTV_SetMixModeMute(VolType, EnMute);
    return SSSOUND_OK;
}

//-------------------------------------------------------------------------------------------------
/// This routine is used to set S/PDIF output mode
/// @param mode            \b IN: PCM mode: 0, SPDIF off: 1, nonPCM mode:2
///
/// @return SSSOUND_OK: Success.
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::SetSPDIFmode(SPDIF_TYPE_ mode)
{
  // Use User Spdif Mute to control the UI mute event 
    if(mode == MSAPI_AUD_SPDIF_OFF_)
    {
        mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_USER_SPDIF_MUTEON_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
    }
    else
    {
        mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_USER_SPDIF_MUTEOFF_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
    }

    mapi_interface::Get_mapi_audio()->SPDIF_UI_SetMode(mode);
    return SSSOUND_OK;
}

//-------------------------------------------------------------------------------------------------
/// This routine is used to set SPDIF delay time.
/// @param  delay       \b IN: Buffer Process Value, 0 ~ 250 (unit:ms).
/// @return SSSOUND_OK: Success.
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::SetSPDIFDelay(U32 delay)
{
    MS_USER_SOUND_SETTING stAudioTemp;
    MAPI_INPUT_SOURCE_TYPE enCurrentInputType;
    MSrv_Control::GetMSrvSystemDatabase()->GetAudioSetting(&stAudioTemp);
    stAudioTemp.SPDIF_Delay= (U8)delay;
    MSrv_Control::GetMSrvSystemDatabase()->SetAudioSetting(&stAudioTemp);

    enCurrentInputType = MSrv_Control::GetInstance()->GetCurrentInputSource();
    m_u8SPDIFDelayOffset = SystemInfo::GetInstance()->GetUseSPDIFDelayOffset(enCurrentInputType);
    mapi_interface::Get_mapi_audio()->SPDIF_SetBufferProcess(delay + m_u8SPDIFDelayOffset);
    return SSSOUND_OK;
}

//-------------------------------------------------------------------------------------------------
/// This routine is used to set SPDIF hardware enable.
/// @param  eHWMode       \b IN: enable/disable SPDIF hareware
/// @return SSSOUND_OK: Success.
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::SetSPDIFHWMode(SPDIF_HW_MODE_ eHWMode)
{
    mapi_interface::Get_mapi_audio()->SPDIF_HWEN((MAPI_BOOL)eHWMode);
    return SSSOUND_OK;
}

//-------------------------------------------------------------------------------------------------
/// This routine is used to  set S/PDIF mute.
/// @param bMute			\b IN: SPDIF On: 0, SPDIF off: 1
/// @return SSSOUND_OK: Success.
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::SetSPDIFMute(MAPI_BOOL bMute)
{
    mapi_interface::Get_mapi_audio()->SPDIF_SetMute((MAPI_BOOL)bMute);
    return SSSOUND_OK;
}

//-------------------------------------------------------------------------------------------------
/// This routine is used to set HDMI Tx mode.
/// @param  eHDMITxMode       \b IN: MSAPI_HDMI_MODE_PCM, MSAPI_HDMI_MODE_RAW, MSAPI_HDMI_MODE_UNKNOWN
/// @return SSSOUND_OK: Success.
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::SetHDMITxMode(HDMI_TYPE_ eHDMITxMode)
{
    mapi_interface::Get_mapi_audio()->HDMITx_SetMode(eHDMITxMode);
    return SSSOUND_OK;
}

//-------------------------------------------------------------------------------------------------
/// This routine is used to set the Audysessy Dynamic Volume.
/// @param  bOnoff      \b IN: Enable: 1, Disable: 0.
/// @return SSSOUND_OK: Success.
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::SetAudysessyDynaVol(MAPI_U8 mode)
{
    if(mode == 0)  //off
    {
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(AUDYSSEY_DYNAMICVOL_, FALSE);

    }
    else if(mode == 1) // light Mode
    {
        mapi_interface::Get_mapi_audio()->ADVSND_ProcessEnable(AUDYSSEY_);
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(AUDYSSEY_DYNAMICVOL_, TRUE);
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(AUDYSSEY_DYNAMICVOL_COMPRESS_MODE_, 2, 0);
    }
    else if(mode == 2) // Medium Mode
    {
        mapi_interface::Get_mapi_audio()->ADVSND_ProcessEnable(AUDYSSEY_);
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(AUDYSSEY_DYNAMICVOL_, TRUE);
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(AUDYSSEY_DYNAMICVOL_COMPRESS_MODE_, 1, 0);
    }
    else if(mode == 3) // Heavy Mode
    {
        mapi_interface::Get_mapi_audio()->ADVSND_ProcessEnable(AUDYSSEY_);
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(AUDYSSEY_DYNAMICVOL_, TRUE);
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(AUDYSSEY_DYNAMICVOL_COMPRESS_MODE_, 0, 0);
    }

    return SSSOUND_OK;

}

MAPI_U8 MSrv_SSSound::SetPEQ(MAPI_BOOL bOnoff)
{
    if(bOnoff)
    {
        mapi_interface::Get_mapi_audio()->SND_SetPEQ(0, 180, 3, 0, 4);
        mapi_interface::Get_mapi_audio()->SND_SetPEQ(1, 140, 11, 0, 8);
        mapi_interface::Get_mapi_audio()->SND_SetPEQ(2, 160, 16, 0, 4);
    }
    mapi_interface::Get_mapi_audio()->SND_ProcessEnable(Sound_ENABL_Type_PEQ_, bOnoff);
    return SSSOUND_OK;
}

MAPI_U8 MSrv_SSSound::SetSRS(MAPI_BOOL bOnoff)
{
    #if 0 //remove this api, please use SetSurroundSound instead
    m_bAudyessySRSMode = bOnoff;

    if(bOnoff)
    {
        //SRS TSXT/TSHD can be judged in Utopia LIB automaticly.
        //SRS TSXT Enable
        mapi_interface::Get_mapi_audio()->ADVSND_ProcessEnable(SRS_TSXT_);
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_TSXT_TRUBASS_, TRUE);
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_TSXT_DC_, TRUE);

        //SRS TSHD Enable
        mapi_interface::Get_mapi_audio()->ADVSND_ProcessEnable(SRS_TSHD_);
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TSHD_, TRUE);
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TRUBASS_, TRUE);
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_DC_, TRUE);
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_DEFINITION_, TRUE);
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_HARDLIMITER_, TRUE);
    }
    else
    {
        //SRS TSXT Disable
        mapi_interface::Get_mapi_audio()->ADVSND_ProcessEnable(ADV_NONE_);
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_TSXT_TRUBASS_, FALSE);
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_TSXT_DC_, FALSE);

        //SRS TSHD Disable
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TSHD_, FALSE);
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TRUBASS_, FALSE);
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_DC_, FALSE);
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_DEFINITION_, FALSE);
        mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_HARDLIMITER_, FALSE);
    }

    mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TRUVOLUME_, FALSE);
    #endif
    return SSSOUND_OK;
}
MAPI_BOOL MSrv_SSSound::GetSRSMode(void)
{
    return m_bAudyessySRSMode;
}

MAPI_U8 MSrv_SSSound::Sound_NonLinearCalculate_100(MS_SND_NLA_POINT * pNonLinearCurve, U8 AdjustValue)
{
    //dual direction
    U32 rValue, ucY0, ucY1, ucX0, ucX1, ucIntercept;
    U32 wDistanceOfY, wDistanceOfX, u32AdjustValue;

    if(AdjustValue < 25)
    {
        ucY0 = pNonLinearCurve->u32OSD_V0;
        ucY1 = pNonLinearCurve->u32OSD_V25;
        ucX0 = 0;
        ucX1 = 25;
    }
    else if(AdjustValue < 50)
    {
        ucY0 = pNonLinearCurve->u32OSD_V25;
        ucY1 = pNonLinearCurve->u32OSD_V50;
        ucX0 = 25;
        ucX1 = 50;
    }
    else if(AdjustValue < 75)
    {
        ucY0 = pNonLinearCurve->u32OSD_V50;
        ucY1 = pNonLinearCurve->u32OSD_V75;
        ucX0 = 50;
        ucX1 = 75;
    }
    else
    {
        ucY0 = pNonLinearCurve->u32OSD_V75;
        ucY1 = pNonLinearCurve->u32OSD_V100;
        ucX0 = 75;
        ucX1 = 100;
    }

    if(ucY1 > ucY0)
    {
        wDistanceOfY = ucY1 - ucY0;
        wDistanceOfX = ucX1 - ucX0;
        ucIntercept  = ucY0;
        u32AdjustValue  = ((U32)AdjustValue) - ucX0;
    }
    else
    {
        wDistanceOfY = ucY0 - ucY1;
        wDistanceOfX = ucX1 - ucX0;
        ucIntercept  = ucY1;
        u32AdjustValue  = ucX1 - ((U32)AdjustValue);
    }

    rValue = (wDistanceOfY * u32AdjustValue / (wDistanceOfX)) + ucIntercept;
    return (U8)rValue;
}

MAPI_U8 MSrv_SSSound::SetSNDSpeakerDelay(U32 delay)
{
    MS_USER_SOUND_SETTING stAudioTemp;
    MAPI_INPUT_SOURCE_TYPE enCurrentInputType;
    MSrv_Control::GetMSrvSystemDatabase()->GetAudioSetting(&stAudioTemp);
    stAudioTemp.Speaker_Delay= (U8)delay;
    MSrv_Control::GetMSrvSystemDatabase()->SetAudioSetting(&stAudioTemp);

    enCurrentInputType = MSrv_Control::GetInstance()->GetCurrentInputSource();
    m_u8AudioDelayOffset = SystemInfo::GetInstance()->GetUseAudioDelayOffset(enCurrentInputType);
    mapi_interface::Get_mapi_audio()->SND_Speaker_SetBufferProcess(delay + m_u8AudioDelayOffset);
    return SSSOUND_OK;
}

MAPI_S16 MSrv_SSSound::SND_GetParam(const Sound_GET_PARAM_Type_ Type,const MAPI_S16 param1)
{
    MAPI_S16 temp = 0;
    temp = mapi_interface::Get_mapi_audio()->SND_GetParam(Type, param1);
    return temp;
}

MAPI_U8 MSrv_SSSound::SND_SetParam(const Sound_SET_PARAM_Type_ Type, const MAPI_S16 param1, const MAPI_S16 param2)
{
    mapi_interface::Get_mapi_audio()->SND_SetParam(Type, param1, param2);
    return SSSOUND_OK;
}

MAPI_U64  MSrv_SSSound::SND_GetKTVInfo(const KTV_GET_INFO_TYPE_ infoType)
{
    MAPI_U64 temp;
    temp = mapi_interface::Get_mapi_audio()->KTV_GetInfo(infoType);
    return temp;
}

MAPI_U8 MSrv_SSSound::SND_KTV_SetInfo(const KTV_SET_INFO_TYPE_ infoType,const MAPI_U32 param1,const MAPI_U32 param2)
{
    mapi_interface::Get_mapi_audio()->KTV_SetInfo(infoType, param1, param2);
    return SSSOUND_OK;
}

MAPI_U8 MSrv_SSSound::SND_DmaReader_Init(const SAMPLE_RATE_ sampleRate,const AUDIO_SOURCE_INFO_TYPE_ sourceInfo)
{
    mapi_interface::Get_mapi_audio()->DmaReader_Init(sampleRate, sourceInfo);
    return SSSOUND_OK;
}

MAPI_U8 MSrv_SSSound::SND_DmaReader_SetCommand(const DMA_COMMAND_ DMAcommand)
{
    mapi_interface::Get_mapi_audio()->DmaReader_SetCommand(DMAcommand);
    return SSSOUND_OK;
}

MAPI_BOOL MSrv_SSSound::SND_DmaReader_WritePCM(void* const buffer,const MAPI_U32 bytes)
{
    MAPI_BOOL temp;
    temp = mapi_interface::Get_mapi_audio()->DmaReader_WritePCM(buffer, bytes);
    return temp;
}

MAPI_U8 MSrv_SSSound::SND_AUDIO_IO_PCMUploadCtrl(const PCMUPLOAD_COMMAND_ up_control)
{
    mapi_interface::Get_mapi_audio()->AUDIO_IO_PCMUploadCtrl(up_control);
    return SSSOUND_OK;
}

MAPI_BOOL MSrv_SSSound::SND_AUDIO_IO_CheckPCMUploadRequest(MAPI_U32 *  const pU32WrtAddr, MAPI_U32 *  const pU32WrtBytes, MAPI_U32 *  const index)
{
    MAPI_BOOL temp;
    temp = mapi_interface::Get_mapi_audio()->AUDIO_IO_CheckPCMUploadRequest(pU32WrtAddr, pU32WrtBytes, index);
    return temp;
}

MAPI_BOOL MSrv_SSSound::SND_ADV_SubProcessEnable(const ADVFUNC_ proc,const MAPI_BOOL enable)
{
    MAPI_BOOL temp;
    temp = mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(proc, enable);
    return temp;
}

MAPI_BOOL MSrv_SSSound::SND_ADV_SetParam(const ADVSND_PARAM_ param,const MAPI_U16 u16value1,const MAPI_U16 u16value2)
{
    MAPI_BOOL temp;
    temp = mapi_interface::Get_mapi_audio()->ADVSND_SetParam(param, u16value1, u16value2);
    return temp;
}

MAPI_BOOL MSrv_SSSound::SoundSetHidevMode(SOUND_HIDEV_INDEX eMode)
{
    Sound_Hidev_mode eHidevMode = Sound_Hidev_Off;
    switch(eMode)
    {
    case E_SOUND_HIDEV_OFF:
        eHidevMode = Sound_Hidev_Off;
        break;
    case E_SOUND_HIDEV_BW_L1:
        eHidevMode = Sound_Hidev_Bw_Lv1;
        break;
    case E_SOUND_HIDEV_BW_L2:
        eHidevMode = Sound_Hidev_Bw_Lv2;
        break;
    case E_SOUND_HIDEV_BW_L3:
        eHidevMode = Sound_Hidev_Bw_Lv3;
        break;
    default:
        ASSERT(0);
        break;
    }
    mapi_interface::Get_mapi_audio()->SetHidevMode(eHidevMode);
    return TRUE;
}


MAPI_U8 MSrv_SSSound::SND_Key_Start(AUDIO_KEY_INFO *keyinfo, MAPI_U8* FileSrc, MAPI_U32 FileSize)//morris add, 20110831
{
    mapi_interface::Get_mapi_audio()->Key_Start(keyinfo, FileSrc, FileSize);
    return SSSOUND_OK;
}

MAPI_U8 MSrv_SSSound::SND_Key_SetInfo(void)//morris add, 20110831
{
    AUDIO_KEY_INFO keyinfo;
    mapi_interface::Get_mapi_audio()->Key_SetInfo(&keyinfo);
    return SSSOUND_OK;
}

///////////////////////////////////////////////////////////////////////////////////////////////////
/// @MSrv_SSSound function for Java
/// @brief\b Sound related interface.
/// @author MStar Semiconductor Inc.
///
/// Sound related interface function is used to Java kind of sound property.
///
///////////////////////////////////////////////////////////////////////////////////////////////////
//-------------------------------------------------------------------------------------------------
/// Java Audio Initialize .
/// @param volSrcType   \b IN: NONE
/// @return MSRV_SSSND_RET: RETURN_OK
//-------------------------------------------------------------------------------------------------
MSRV_SSSND_RET MSrv_SSSound::SetInit(void)
{
    //memcpy(&g_LastBasicSoundSetting, &BasicSeInitSetting, sizeof(g_LastBasicSoundSetting));
    //memcpy(&g_LastAdvSoundSetting, &AdvSeInitSetting, sizeof(g_LastAdvSoundSetting));

    return RETURN_OK;
}

//-------------------------------------------------------------------------------------------------
/// Set Audio Volume .
/// @param volSrcType   \b IN: volume source type
/// @param u8Vol        \b IN: volume value range from 0~100
/// @return MSRV_SSSND_RET: RETURN_OK, RETURN_NOTOK, RETURN_NOTSUPPORT
//-------------------------------------------------------------------------------------------------
MSRV_SSSND_RET MSrv_SSSound::SetAudioVolume(AUDIO_VOL_SOURCE_TYPE volSrcType,MAPI_U8 u8Vol)
{
    MSRV_SSSND_RET return_tmp;
    MAPI_AUDIO_PATH_TYPE enAudioPathType;

    switch(volSrcType)
    {
    case VOL_SOURCE_SPEAKER_OUT:
        m_SpeakerVolumeValue = u8Vol;
        MSrv_Control::GetMSrvSSSound()->SetVolume(u8Vol);
        return_tmp = RETURN_OK;
        break;

    case VOL_SOURCE_HP_OUT:
        m_HPVolumeValue = u8Vol;
        MSrv_Control::GetMSrvSSSound()->SetHPVolume(u8Vol);
        return_tmp = RETURN_OK;
        break;

    case VOL_SOURCE_LINE_OUT:
        m_LINE_OUT_VolumeValue = u8Vol;
        enAudioPathType = MAPI_AUDIO_PATH_LINEOUT;
        MSrv_Control::GetMSrvSSSound()->SetVolume(u8Vol, enAudioPathType);
        return_tmp = RETURN_OK;
        break;

    case VOL_SOURCE_SCART1_OUT:
        m_SCART1_OUT_VolumeValue = u8Vol;
        enAudioPathType = MAPI_AUDIO_PATH_SCART1;
        MSrv_Control::GetMSrvSSSound()->SetVolume(u8Vol, enAudioPathType);
        return_tmp = RETURN_OK;
        break;

    case VOL_SOURCE_SCART2_OUT:
        m_SCART2_OUT_VolumeValue = u8Vol;
        enAudioPathType = MAPI_AUDIO_PATH_SCART2;
        MSrv_Control::GetMSrvSSSound()->SetVolume(u8Vol, enAudioPathType);
        return_tmp = RETURN_OK;
        break;

    case VOL_SOURCE_SPDIF_OUT:
        m_SPDIF_OUT_VolumeValue = u8Vol;
        enAudioPathType = MAPI_AUDIO_PATH_SPDIF;
        MSrv_Control::GetMSrvSSSound()->SetVolume(u8Vol, enAudioPathType);
        return_tmp = RETURN_OK;
        break;

    case VOL_SOURCE_PCM_CAPTURE1:
        m_PcmCapture1_VolumeValue = u8Vol;
        mapi_interface::Get_mapi_audio()->SetPcmCaptueVolume(MSAPI_PORT_PCM_CAPTURE1, u8Vol);
        return_tmp = RETURN_OK;
        break;

    case VOL_SOURCE_PCM_CAPTURE2:
        m_PcmCapture1_VolumeValue = u8Vol;
        mapi_interface::Get_mapi_audio()->SetPcmCaptueVolume(MSAPI_PORT_PCM_CAPTURE2, u8Vol);
        return_tmp = RETURN_OK;
        break;

#if (MSTAR_TVOS == 1)
    case VOL_SOURCE_COMPENSATION:
        m_VolumeCompensation = u8Vol;
        return_tmp = RETURN_OK;
        SystemInfo::GetInstance()->SetVolumeCompensationFlag(0x01 );
        break;
#endif

    default:
        return_tmp = RETURN_NOTOK;
        break;
    }
    return return_tmp;
}

//-------------------------------------------------------------------------------------------------
/// Set Audio internal/ouput port gain .
/// @param ePort   \b IN: volume source type
/// @param sGainStep        \b IN: Gain range from -114*8 ~ +12*8 (-114dB~+12db) ; 0.125dB/step
/// @return MSRV_SSSND_RET: RETURN_OK, RETURN_NOTOK, RETURN_NOTSUPPORT
//-------------------------------------------------------------------------------------------------
MSRV_SSSND_RET MSrv_SSSound::SetOutputGain(AUDIO_BASIC_PORT_TYPE ePort, int sGainStep)
{
    MSRV_SSSND_RET return_tmp=RETURN_OK;
    MAPI_U8 u8GainInteger=0x0C, u8GainFrac=0x00;
    int tempGain=0;

    if(sGainStep > (12*8)) // Max Gain=12*8 => +12dB
    {
        tempGain = 12*8 ;
    }
    else if(sGainStep < (-114*8)) // Min Gain=-114*8 => -114dB
    {
        tempGain = -114*8 ;
    }
    else
    {
        tempGain = sGainStep ;
    }
    tempGain=-sGainStep+12*8;   // mapping to audio volume register setting
    u8GainInteger=(MAPI_U8)(tempGain/8);
    u8GainFrac=(MAPI_U8)(tempGain%8);

    switch(ePort)
    {
    case PORT_SPEAKER:
        //TBD
        break;

    case PORT_HP:
        mapi_interface::Get_mapi_audio()->SetAbsoluteGain(MSAPI_PORT_HP, u8GainInteger, u8GainFrac);
        break;

    case PORT_MONITOR_OUT:
        mapi_interface::Get_mapi_audio()->SetAbsoluteGain(MSAPI_PORT_MONITOR_OUT, u8GainInteger, u8GainFrac);
        break;

    case PORT_SCART:
        mapi_interface::Get_mapi_audio()->SetAbsoluteGain(MSAPI_PORT_SCART, u8GainInteger, u8GainFrac);
        break;

    case PORT_SPDIF:
        mapi_interface::Get_mapi_audio()->SetAbsoluteGain(MSAPI_PORT_SPDIF, u8GainInteger, u8GainFrac);
        break;

    case PORT_MAIN_CHANNEL:
        mapi_interface::Get_mapi_audio()->SetAbsoluteGain(MSAPI_PORT_MAIN_CHANNEL, u8GainInteger, u8GainFrac);
        break;

    case PORT_SECOND_CHANNEL:
        mapi_interface::Get_mapi_audio()->SetAbsoluteGain(MSAPI_PORT_SECOND_CHANNEL, u8GainInteger, u8GainFrac);
        break;

    case PORT_DATA_READER:
        mapi_interface::Get_mapi_audio()->SetAbsoluteGain(MSAPI_PORT_DATA_READER, u8GainInteger, u8GainFrac);
        break;

    case PORT_ARC:
    case PORT_USER_DEFINE1:
    case PORT_USER_DEFINE2:
    case PORT_USER_DEFINE3:
    case PORT_MAX:
    default:
        printf("\r\n=======Unsupport Gain setting !!=======");
        return_tmp = RETURN_NOTOK;
        break;
    }

    return return_tmp;
}


//-------------------------------------------------------------------------------------------------
/// Get Audio Volume .
/// @param volSrcType   \b IN: volume source type
/// @return u8Vol       \b OUT: return volume value range from 0~100
//-------------------------------------------------------------------------------------------------
MAPI_U8  MSrv_SSSound::GetAudioVolume(AUDIO_VOL_SOURCE_TYPE volSrcType)
{
    MAPI_U8 u8VolValue = 0;
    switch(volSrcType)
    {
    case VOL_SOURCE_SPEAKER_OUT:
        u8VolValue = m_SpeakerVolumeValue;
        break;

    case VOL_SOURCE_HP_OUT:
        u8VolValue = m_HPVolumeValue;
        break;

    case VOL_SOURCE_LINE_OUT:
        u8VolValue = m_LINE_OUT_VolumeValue;
        break;

    case VOL_SOURCE_SCART1_OUT:
        u8VolValue = m_SCART1_OUT_VolumeValue;
        break;

    case VOL_SOURCE_SCART2_OUT:
        u8VolValue = m_SCART2_OUT_VolumeValue;
        break;

    case VOL_SOURCE_SPDIF_OUT:
        u8VolValue = m_SPDIF_OUT_VolumeValue;
        break;

    case VOL_SOURCE_PCM_CAPTURE1:
        u8VolValue = m_PcmCapture1_VolumeValue;
        break;

    case VOL_SOURCE_PCM_CAPTURE2:
        u8VolValue = m_PcmCapture2_VolumeValue;
        break;

#if (MSTAR_TVOS == 1)
    case VOL_SOURCE_COMPENSATION:
        u8VolValue = m_VolumeCompensation;
        break;
#endif

    default:
        break;
    }
    return u8VolValue;
}

//-------------------------------------------------------------------------------------------------
/// Set Audio Input Level .
/// @param inputLvlSrcType   \b IN: input level source type
/// @param u8Vol        \b IN: volume value range from 0~100
/// @return MSRV_SSSND_RET: RETURN_OK, RETURN_NOTOK, RETURN_NOTSUPPORT
//-------------------------------------------------------------------------------------------------
MSRV_SSSND_RET MSrv_SSSound::SetAudioInputLevel(AUDIO_INPUT_LEVEL_SOURCE_TYPE inputLvlSrcType, MAPI_U32 u8Vol)
{
    MSRV_SSSND_RET return_tmp;

    switch(inputLvlSrcType)
    {
    case VOL_SOURCE_AUDIO_DESCRIPTION_IN:
        m_AD_VolumeValue = u8Vol;
        MSrv_Control::GetMSrvSSSound()->SetADVolume(u8Vol);
        return_tmp = RETURN_OK;
        break;

    case VOL_SOURCE_PREMIXER_KTV_MP3_IN:
        m_Premixer_KTV_MP3_VolumeValue = u8Vol;
        mapi_interface::Get_mapi_audio()->SetMixModeVolume(E_AUDIO_INFO_KTV_IN_, MP3_VOL_,(MAPI_U8)u8Vol,0);
        return_tmp = RETURN_OK;
        break;

    case VOL_SOURCE_PREMIXER_KTV_MIC_IN:
        m_Premixer_KTV_MIC_VolumeValue = u8Vol;
        mapi_interface::Get_mapi_audio()->SetMixModeVolume(E_AUDIO_INFO_KTV_IN_, MIC_VOL_,(MAPI_U8)u8Vol,0);
        return_tmp = RETURN_OK;
        break;

    case VOL_SOURCE_PREMIXER_GAME1_IN:
        m_Premixer_GAME1_VolumeValue = u8Vol;
        mapi_interface::Get_mapi_audio()->SetMixModeVolume(E_AUDIO_INFO_GAME_IN_, GAME1_VOL_,(MAPI_U8)u8Vol,0);
        return_tmp = RETURN_OK;
        break;

    case VOL_SOURCE_PREMIXER_GAME2_IN:
        m_Premixer_GAME2_VolumeValue = u8Vol;
        mapi_interface::Get_mapi_audio()->SetMixModeVolume(E_AUDIO_INFO_GAME_IN_, GAME2_VOL_,(MAPI_U8)u8Vol, 0);
        return_tmp = RETURN_OK;
        break;

    case VOL_SOURCE_PREMIXER_ECHO1_IN:
        m_Premixer_ECHO1_VolumeValue = u8Vol;
        mapi_interface::Get_mapi_audio()->SetMixModeVolume(E_AUDIO_INFO_KTV_IN_, ECHO1_VOL_,(MAPI_U8)u8Vol, 0);
        return_tmp = RETURN_OK;
        break;

    case VOL_SOURCE_PREMIXER_ECHO2_IN:
        m_Premixer_ECHO2_VolumeValue = u8Vol;
        mapi_interface::Get_mapi_audio()->SetMixModeVolume(E_AUDIO_INFO_KTV_IN_, ECHO2_VOL_,(MAPI_U8)u8Vol, 0);
        return_tmp = RETURN_OK;
        break;

    case VOL_SOURCE_PREMIXER_ALSA_IN:
        m_Premixer_ALSA_VolumeValue = u8Vol;
        mapi_interface::Get_mapi_audio()->SetMixModeVolume(E_AUDIO_INFO_KTV_IN_, PCM_VOL_,(MAPI_U8)u8Vol, 0);
        return_tmp = RETURN_OK;
        break;


    default:
        return_tmp = RETURN_NOTOK;
        break;
    }
    return return_tmp;
}

//-------------------------------------------------------------------------------------------------
/// Get Audio Input Level .
/// @param inputLvlSrcType   \b IN: volume source type
/// @return u8Vol       \b OUT: return level value range from 0~100
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::GetAudioInputLevel(AUDIO_INPUT_LEVEL_SOURCE_TYPE inputLvlSrcType)
{
    MAPI_U8 u8VolValue = 0;
    switch(inputLvlSrcType)
    {
    case VOL_SOURCE_AUDIO_DESCRIPTION_IN:
        u8VolValue = m_AD_VolumeValue;
        break;

    case VOL_SOURCE_PREMIXER_KTV_MP3_IN:
        u8VolValue = m_Premixer_KTV_MP3_VolumeValue;
        break;

    case VOL_SOURCE_PREMIXER_KTV_MIC_IN:
        u8VolValue = m_Premixer_KTV_MIC_VolumeValue;
        break;

    case VOL_SOURCE_PREMIXER_GAME1_IN:
        u8VolValue = m_Premixer_GAME1_VolumeValue;
        break;

    case VOL_SOURCE_PREMIXER_GAME2_IN:
        u8VolValue = m_Premixer_GAME2_VolumeValue;
        break;

    case VOL_SOURCE_PREMIXER_ECHO1_IN:
        u8VolValue = m_Premixer_ECHO1_VolumeValue;
        break;

    case VOL_SOURCE_PREMIXER_ECHO2_IN:
        u8VolValue = m_Premixer_ECHO2_VolumeValue;
        break;

    case VOL_SOURCE_PREMIXER_ALSA_IN:
        u8VolValue = m_Premixer_ALSA_VolumeValue;
        break;

    default:
        break;
    }
    return u8VolValue;
}

//-------------------------------------------------------------------------------------------------
/// Enable/Disable Basic Sound Effect
/// @param BSndType   \b IN: BSOUND_EFFECT_TYPE
/// @return MSRV_SSSND_RET: RETURN_OK, RETURN_NOTOK, RETURN_NOTSUPPORT
//-------------------------------------------------------------------------------------------------
MSRV_SSSND_RET MSrv_SSSound::EnableBasicSoundEffect(BSOUND_EFFECT_TYPE BSndType, MAPI_BOOL BSOUND_OnOff)
{
    MSRV_SSSND_RET return_tmp;
    MAPI_BOOL usermute = MAPI_FALSE;

    usermute = GetMuteStatus(Mute_Status_bByUserAudioMute);

    if(usermute == MAPI_FALSE)
    {
        mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_BYUSER_MUTEON_, E_AUDIOMUTESOURCE_MAINSOURCE_);
        usleep(AU_DELAY_FOR_SNDEFFECT_MUTE*1000); // mute time for entering enable/disable
    }

    switch(BSndType)
    {
    case BSND_PRESCALE:
        return_tmp = RETURN_NOTOK;
        break;

    case BSND_TREBLE:
        mapi_interface::Get_mapi_audio()->SND_ProcessEnable(Sound_ENABL_Type_Tone_, BSOUND_OnOff);
        return_tmp = RETURN_OK;
        break;

    case BSND_BASS:
        mapi_interface::Get_mapi_audio()->SND_ProcessEnable(Sound_ENABL_Type_Tone_, BSOUND_OnOff);
        return_tmp = RETURN_OK;
        break;

    case BSND_BALANCE:
        mapi_interface::Get_mapi_audio()->SND_ProcessEnable(Sound_ENABL_Type_Balance_, BSOUND_OnOff);
        return_tmp = RETURN_OK;
        break;

    case BSND_EQ:
        mapi_interface::Get_mapi_audio()->SND_ProcessEnable(Sound_ENABL_Type_EQ_, BSOUND_OnOff);
        return_tmp = RETURN_OK;
        break;

    case BSND_PEQ:
        mapi_interface::Get_mapi_audio()->SND_ProcessEnable(Sound_ENABL_Type_PEQ_, BSOUND_OnOff);
        return_tmp = RETURN_OK;
        break;

    case BSND_AVC:
        mapi_interface::Get_mapi_audio()->SND_EnableAutoVolume(BSOUND_OnOff);
        return_tmp = RETURN_OK;
        break;

    case BSND_Surround:
        mapi_interface::Get_mapi_audio()->SND_ProcessEnable(Sound_ENABL_Type_Surround_, BSOUND_OnOff);
        return_tmp = RETURN_OK;
        break;

    case BSND_DRC:
        mapi_interface::Get_mapi_audio()->SND_ProcessEnable(Sound_ENABL_Type_DRC_, BSOUND_OnOff);
        return_tmp = RETURN_OK;
        break;

    case BSND_NR:
        m_bNRenable = BSOUND_OnOff;
        if (m_bNRenable)
        {
            mapi_interface::Get_mapi_audio()->SND_SetParam(Sound_SET_PARAM_NR_Threshold_, m_BSND_PARAM_NR_THRESHOLD, 0);
        }
        else
        {
            mapi_interface::Get_mapi_audio()->SND_SetParam(Sound_SET_PARAM_NR_Threshold_, 0, 0);
        }
        return_tmp = RETURN_OK;
        break;

    case BSND_ECHO:
        mapi_interface::Get_mapi_audio()->SND_ProcessEnable(Sound_ENABL_Type_KTVEcho_, BSOUND_OnOff);
        return_tmp = RETURN_OK;
        break;

    default:
        return_tmp = RETURN_NOTOK;
        break;
    }

    if(usermute == MAPI_FALSE) // check if bByUserAudioMute keep mute or not
    {
        usleep(AU_DELAY_FOR_SNDEFFECT_UNMUTE*1000); // mute time for leaving enable/disable, prevent un-mute pop noise
        mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_BYUSER_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
    }

    return return_tmp;
}

//-------------------------------------------------------------------------------------------------
/// Set Basic Sound Effect Parameter.
/// @param BSndType     \b IN: BSOUND_EFFECT_TYPE
/// @param *bsnd_param   \b IN: Parameter Structure Pointer
/// @return MSRV_SSSND_RET: RETURN_OK, RETURN_NOTOK, RETURN_NOTSUPPORT
//-------------------------------------------------------------------------------------------------
MSRV_SSSND_RET  MSrv_SSSound::SetBasicSoundEffect(BSOUND_EFFECT_TYPE BSndType, BSND_PARAMETER *bsnd_param)
{
    MSRV_SSSND_RET return_tmp;
    switch(BSndType)
    {
    case BSND_PRESCALE:
        m_BSND_PARAM_PRESCALE = bsnd_param->BSND_PARAM_PRESCALE;
        MSrv_Control::GetMSrvSSSound()->SetPreScale((MAPI_U8)bsnd_param->BSND_PARAM_PRESCALE);
        return_tmp = RETURN_OK;
        break;

    case BSND_TREBLE:
        m_BSND_PARAM_TREBLE = bsnd_param->BSND_PARAM_TREBLE;
        MSrv_Control::GetMSrvSSSound()->SND_SetTreble((MAPI_U8)bsnd_param->BSND_PARAM_TREBLE);
        return_tmp = RETURN_OK;
        break;

    case BSND_BASS:
        m_BSND_PARAM_BASS = bsnd_param->BSND_PARAM_BASS;
        MSrv_Control::GetMSrvSSSound()->SND_SetBass((MAPI_U8)bsnd_param->BSND_PARAM_BASS);
        return_tmp = RETURN_OK;
        break;

    case BSND_BALANCE:
        m_BSND_PARAM_TYPE_BALANCE = bsnd_param->BSND_PARAM_TYPE_BALANCE;
        MSrv_Control::GetMSrvSSSound()->SND_SetBalance((MAPI_U8)bsnd_param->BSND_PARAM_TYPE_BALANCE);
        return_tmp = RETURN_OK;
        break;

    case BSND_EQ:
        m_BSND_PARAM_EQ_BAND_NUM = bsnd_param->BSND_PARAM_EQ_BAND_NUM;
        for(MAPI_U8 i = 0; i < bsnd_param->BSND_PARAM_EQ_BAND_NUM; i++ )
        {
            m_BSND_PARAM_EQ_LEVEL[i] = bsnd_param->BSND_PARAM_EQ[i].BSND_PARAM_EQ_LEVEL;
            mapi_interface::Get_mapi_audio()->SND_SetEq(i, bsnd_param->BSND_PARAM_EQ[i].BSND_PARAM_EQ_LEVEL);
        }
        return_tmp = RETURN_OK;
        break;

    case BSND_PEQ:
        for(MAPI_U8 i = 0; i < bsnd_param->BSND_PARAM_PEQ_BAND_NUM; i++ )
        {
            MAPI_U8 Foh, Fol;
            m_BSND_PARAM_PEQ_GAIN[i] = bsnd_param->BSND_PARAM_PEQ[i].BSND_PARAM_PEQ_GAIN;
            m_BSND_PARAM_PEQ_FC[i] = bsnd_param->BSND_PARAM_PEQ[i].BSND_PARAM_PEQ_FC;
            m_BSND_PARAM_PEQ_QVALUE[i] = bsnd_param->BSND_PARAM_PEQ[i].BSND_PARAM_PEQ_QVALUE;

            MAPI_U16 fc = bsnd_param->BSND_PARAM_PEQ[i].BSND_PARAM_PEQ_FC;

            fc = bsnd_param->BSND_PARAM_PEQ[i].BSND_PARAM_PEQ_FC;

            Foh = (MAPI_U8)(fc / 100);
            Fol = (MAPI_U8)(fc % 100);

            mapi_interface::Get_mapi_audio()->SND_SetPEQ(i,
                    (MAPI_U8)bsnd_param->BSND_PARAM_PEQ[i].BSND_PARAM_PEQ_GAIN,
                    Foh,
                    Fol,
                    (MAPI_U8)bsnd_param->BSND_PARAM_PEQ[i].BSND_PARAM_PEQ_QVALUE);
        }
        return_tmp = RETURN_OK;
        break;

    case BSND_AVC:
        mapi_interface::Get_mapi_audio()->SND_EnableAutoVolume(TRUE);
        if (bsnd_param->BSND_PARAM_AVC_THRESHOLD != 0)
        {
            m_BSND_PARAM_AVC_THRESHOLD = bsnd_param->BSND_PARAM_AVC_THRESHOLD;
            mapi_interface::Get_mapi_audio()->SND_SetParam(Sound_SET_PARAM_Avc_Threshold_, bsnd_param->BSND_PARAM_AVC_THRESHOLD, 0);
        }
        if (bsnd_param->BSND_PARAM_AVC_AT != 0)
        {
            m_BSND_PARAM_AVC_AT = bsnd_param->BSND_PARAM_AVC_AT;
            mapi_interface::Get_mapi_audio()->SND_SetParam(Sound_SET_PARAM_Avc_AT_, bsnd_param->BSND_PARAM_AVC_AT, 0);
        }
        if (bsnd_param->BSND_PARAM_AVC_RT != 0)
        {
            m_BSND_PARAM_AVC_RT = bsnd_param->BSND_PARAM_AVC_RT;
            mapi_interface::Get_mapi_audio()->SND_SetParam(Sound_SET_PARAM_Avc_RT_, bsnd_param->BSND_PARAM_AVC_RT, 0);
        }
        return_tmp = RETURN_OK;
        break;

    case BSND_Surround:
        if (bsnd_param->BSND_PARAM_MSURR_XA != 0)
        {
            m_BSND_PARAM_MSURR_XA = bsnd_param->BSND_PARAM_MSURR_XA;
            mapi_interface::Get_mapi_audio()->SND_SetParam(Sound_SET_PARAM_Surround_XA_, bsnd_param->BSND_PARAM_MSURR_XA, 0);
        }
        if (bsnd_param->BSND_PARAM_MSURR_XB != 0)
        {
            m_BSND_PARAM_MSURR_XB = bsnd_param->BSND_PARAM_MSURR_XB;
            mapi_interface::Get_mapi_audio()->SND_SetParam(Sound_SET_PARAM_Surround_XB_, bsnd_param->BSND_PARAM_MSURR_XB, 0);
        }
        if (bsnd_param->BSND_PARAM_MSURR_XK != 0)
        {
            m_BSND_PARAM_MSURR_XK = bsnd_param->BSND_PARAM_MSURR_XK;
            mapi_interface::Get_mapi_audio()->SND_SetParam(Sound_SET_PARAM_Surround_XK_, bsnd_param->BSND_PARAM_MSURR_XK, 0);
        }
        if (bsnd_param->BSND_PARAM_MSURR_LPFGAIN!= 0)
        {
            m_BSND_PARAM_MSURR_LPFGAIN = bsnd_param->BSND_PARAM_MSURR_LPFGAIN;
            mapi_interface::Get_mapi_audio()->SND_SetParam(Sound_SET_PARAM_Surround_LPFGAIN_, bsnd_param->BSND_PARAM_MSURR_LPFGAIN, 0);
        }
        return_tmp = RETURN_OK;

        break;

    case BSND_DRC:
        m_BSND_PARAM_DRC_THRESHOLD = bsnd_param->BSND_PARAM_DRC_THRESHOLD;
        mapi_interface::Get_mapi_audio()->SND_SetParam(Sound_SET_PARAM_Drc_Threshold_, m_BSND_PARAM_NR_THRESHOLD, 0);
        return_tmp = RETURN_OK;
        break;

    case BSND_NR:
        m_BSND_PARAM_NR_THRESHOLD = bsnd_param->BSND_PARAM_NR_THRESHOLD;
        if (m_bNRenable)
        {
            mapi_interface::Get_mapi_audio()->SND_SetParam(Sound_SET_PARAM_NR_Threshold_, m_BSND_PARAM_NR_THRESHOLD, 0);
        }
        else
        {
            mapi_interface::Get_mapi_audio()->SND_SetParam(Sound_SET_PARAM_NR_Threshold_, 0, 0);
        }
        return_tmp = RETURN_OK;
        break;

    case BSND_ECHO:
        return_tmp = RETURN_NOTOK;
        break;

    default:
        return_tmp = RETURN_NOTOK;
        break;
    }
    return return_tmp;
}

//-------------------------------------------------------------------------------------------------
/// Get Basic Sound Effect Parameter.
/// @param BSndType     \b IN: BSOUND_EFFECT_TYPE
/// @return BSND_PARAMETER:
//-------------------------------------------------------------------------------------------------
MAPI_U16  MSrv_SSSound::GetBasicSoundEffect(BSND_GET_PARAMETER_TYPE bsnd_param)
{
    MAPI_U16 rtn_value = 0;

    switch(bsnd_param)
    {
    case BSND_GET_PRESCALE:
        rtn_value = m_BSND_PARAM_PRESCALE;
        break;

    case BSND_GET_TREBLE:
        rtn_value = m_BSND_PARAM_TREBLE;
        break;

    case BSND_GET_BASS:
        rtn_value = m_BSND_PARAM_BASS;
        break;

    case BSND_GET_BALANCE:
        rtn_value = m_BSND_PARAM_TYPE_BALANCE;
        break;

    case BSND_GET_EQ_BAND0_LEVEL:
        rtn_value = m_BSND_PARAM_EQ_LEVEL[0];
        break;

    case BSND_GET_EQ_BAND1_LEVEL:
        rtn_value = m_BSND_PARAM_EQ_LEVEL[1];
        break;

    case BSND_GET_EQ_BAND2_LEVEL:
        rtn_value = m_BSND_PARAM_EQ_LEVEL[2];
        break;

    case BSND_GET_EQ_BAND3_LEVEL:
        rtn_value = m_BSND_PARAM_EQ_LEVEL[3];
        break;

    case BSND_GET_EQ_BAND4_LEVEL:
        rtn_value = m_BSND_PARAM_EQ_LEVEL[4];
        break;

    case BSND_GET_PEQ_BAND0_GAIN:
        rtn_value = m_BSND_PARAM_PEQ_GAIN[0];
        break;

    case BSND_GET_PEQ_BAND1_GAIN:
        rtn_value = m_BSND_PARAM_PEQ_GAIN[1];
        break;

    case BSND_GET_PEQ_BAND2_GAIN:
        rtn_value = m_BSND_PARAM_PEQ_GAIN[2];
        break;

    case BSND_GET_PEQ_BAND3_GAIN:
        rtn_value = m_BSND_PARAM_PEQ_GAIN[3];
        break;

    case BSND_GET_PEQ_BAND4_GAIN:
        rtn_value = m_BSND_PARAM_PEQ_GAIN[4];
        break;

    case BSND_GET_PEQ_BAND0_FC:
        rtn_value = m_BSND_PARAM_PEQ_FC[0];
        break;

    case BSND_GET_PEQ_BAND1_FC:
        rtn_value = m_BSND_PARAM_PEQ_FC[1];
        break;

    case BSND_GET_PEQ_BAND2_FC:
        rtn_value = m_BSND_PARAM_PEQ_FC[2];
        break;

    case BSND_GET_PEQ_BAND3_FC:
        rtn_value = m_BSND_PARAM_PEQ_FC[3];
        break;

    case BSND_GET_PEQ_BAND4_FC:
        rtn_value = m_BSND_PARAM_PEQ_FC[4];
        break;

    case BSND_GET_PEQ_BAND0_QVALUE:
        rtn_value = m_BSND_PARAM_PEQ_QVALUE[0];
        break;

    case BSND_GET_PEQ_BAND1_QVALUE:
        rtn_value = m_BSND_PARAM_PEQ_QVALUE[1];
        break;

    case BSND_GET_PEQ_BAND2_QVALUE:
        rtn_value = m_BSND_PARAM_PEQ_QVALUE[2];
        break;

    case BSND_GET_PEQ_BAND3_QVALUE:
        rtn_value = m_BSND_PARAM_PEQ_QVALUE[3];
        break;

    case BSND_GET_PEQ_BAND4_QVALUE:
        rtn_value = m_BSND_PARAM_PEQ_QVALUE[4];
        break;

    case BSND_GET_AVC_ONOFF:
        if (IsAVCFlag)
        {
            rtn_value = 1;
        }
        else
        {
            rtn_value = 0;
        }
        break;

    case BSND_GET_AVC_THRESHOLD:
        rtn_value = m_BSND_PARAM_AVC_THRESHOLD;
        break;

    case BSND_GET_AVC_AT:
        rtn_value = m_BSND_PARAM_AVC_AT;
        break;

    case BSND_GET_AVC_RT:
        rtn_value = m_BSND_PARAM_AVC_RT;
        break;

    case BSND_GET_MSURR_XA:
        rtn_value = m_BSND_PARAM_MSURR_XA;
        break;

    case BSND_GET_MSURR_XB:
        rtn_value = m_BSND_PARAM_MSURR_XB;
        break;

    case BSND_GET_MSURR_XK:
        rtn_value = m_BSND_PARAM_MSURR_XK;
        break;

    case BSND_GET_MSURR_LPFGAIN:
        rtn_value = m_BSND_PARAM_MSURR_LPFGAIN;
        break;

    case BSND_GET_DRC_THRESHOLD:
        rtn_value = m_BSND_PARAM_DRC_THRESHOLD;
        break;

    case BSND_GET_NR_THRESHOLD:
        rtn_value = m_BSND_PARAM_NR_THRESHOLD;
        break;

    case BSND_GET_ECHO_TIME:
        break;

    default:
        break;
    }
    return rtn_value;
}

//-------------------------------------------------------------------------------------------------
/// Enable/Disable Advanced Sound Effect
/// @param AdvSndType   \b IN: ADVANCESND_TYPE
/// @param AdvSubProc   \b IN: ADVSND_SUBPROC
/// @return MAPI_BOOL: TRUE is OK, FALSE is NOTSUPPORT
//-------------------------------------------------------------------------------------------------
MSRV_SSSND_RET MSrv_SSSound::EnableAdvancedSoundEffect(ADVANCESND_TYPE AdvSndType, ADVSND_SUBPROC AdvSubProc)
{
    MSRV_SSSND_RET rtn_status=RETURN_NOTOK;
    static ADVANCESND_TYPE Pre_AdvSndType = ADVSND_NONE;
    MAPI_BOOL SndTypeMuteFlag = MAPI_FALSE;
    MAPI_BOOL usermute = MAPI_FALSE;

    usermute = GetMuteStatus(Mute_Status_bByUserAudioMute);

    if(Pre_AdvSndType != AdvSndType) // check first time AdvSndType switch, not consider AdvSubProc case
    {
        SndTypeMuteFlag = MAPI_TRUE;

        if(usermute == MAPI_FALSE)
        {
            mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_BYUSER_MUTEON_, E_AUDIOMUTESOURCE_MAINSOURCE_);
            usleep(AU_DELAY_FOR_SNDEFFECT_MUTE*1000); // mute time for entering enable/disable
        }
     }
    else
    {
        SndTypeMuteFlag = MAPI_FALSE;
    }
    Pre_AdvSndType = AdvSndType;

    switch(AdvSndType)
    {
    case ADVSND_DOLBY_PL2VDS:
        if(mapi_interface::Get_mapi_audio()->ADVSND_ProcessEnable(DOLBY_PL2VDS_))
        {
            rtn_status = RETURN_OK;
        }
        else
        {
            rtn_status = RETURN_NOTOK;
        }
        break;

    case ADVSND_DOLBY_PL2VDPK:
        if(mapi_interface::Get_mapi_audio()->ADVSND_ProcessEnable(DOLBY_PL2VDPK_))
        {
            rtn_status = RETURN_OK;
        }
        else
        {
            rtn_status = RETURN_NOTOK;
        }
        break;

    case ADVSND_BBE:
        if(mapi_interface::Get_mapi_audio()->ADVSND_ProcessEnable(BBE_))
        {
            rtn_status = RETURN_OK;
        }
        else
        {
            rtn_status = RETURN_NOTOK;
        }
        break;

    case ADVSND_SRS_TSXT:
        mapi_interface::Get_mapi_audio()->ADVSND_ProcessEnable(SRS_TSXT_);
        switch(AdvSubProc)
        {
        case SRS_TSXT_TRUEBASS_ON:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_TSXT_TRUBASS_, TRUE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_TSXT_TRUEBASS_OFF:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_TSXT_TRUBASS_, FALSE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_TSXT_DYNAMIC_CLARITY_ON:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_TSXT_DC_, TRUE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_TSXT_DYNAMIC_CLARITY_OFF:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_TSXT_DC_, FALSE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        default:
            rtn_status = RETURN_NOTOK;
            break;
        }
        break;

    case ADVSND_SRS_TSHD:
        mapi_interface::Get_mapi_audio()->ADVSND_ProcessEnable(SRS_TSHD_);
        switch(AdvSubProc)
        {
        case SRS_TSHD_TRUEBASS_ON:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_TSHD_TRUBASS_, TRUE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_TSHD_TRUEBASS_OFF:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_TSHD_TRUBASS_, FALSE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_TSHD_DYNAMIC_CLARITY_ON:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_TSHD_DC_, TRUE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_TSHD_DYNAMIC_CLARITY_OFF:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_TSHD_DC_, FALSE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_TSHD_DEFINITION_ON:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_TSHD_DC_, TRUE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_TSHD_DEFINITION_OFF:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_TSHD_DC_, FALSE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_TSHD_SRS3D_ON:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_TSHD_SRS3D_, TRUE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_TSHD_SRS3D_OFF:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_TSHD_SRS3D_, FALSE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_THEATERSOUND_TSHD_ON:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TSHD_, TRUE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_THEATERSOUND_TSHD_OFF:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TSHD_, FALSE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_THEATERSOUND_TSHD_SURR_ON:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TSHD_SURR_, TRUE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_THEATERSOUND_TSHD_SURR_OFF:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TSHD_SURR_, FALSE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_THEATERSOUND_TRUEBASS_ON:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TRUBASS_, TRUE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_THEATERSOUND_TRUEBASS_OFF:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TRUBASS_, FALSE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_THEATERSOUND_TRUBASS_LEVEL_INDP_ON:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TRUBASS_LEVEL_INDP_, TRUE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_THEATERSOUND_TRUBASS_LEVEL_INDP_OFF:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TRUBASS_LEVEL_INDP_, FALSE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_THEATERSOUND_DYNAMIC_CLARITY_ON:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_DC_, TRUE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_THEATERSOUND_DYNAMIC_CLARITY_OFF:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_DC_, FALSE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_THEATERSOUND_DEFINITION_ON:
            if(AdvSndType == ADVSND_SRS_TSXT)
            {
                printf("[WARNING]:Definition function is not allowed in TSXT license\r\n");
                break;
            }
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_DEFINITION_, TRUE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_THEATERSOUND_DEFINITION_OFF:
            if(AdvSndType == ADVSND_SRS_TSXT)
            {
                printf("[WARNING]:Definition function is not allowed in TSXT license\r\n");
                break;
            }
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_DEFINITION_, FALSE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        default:
            rtn_status = RETURN_NOTOK;
            break;
        }
        break;

        case ADVSND_SRS_THEATERSOUND:
        case ADVSND_SRS_THEATERSOUND3D:
            switch(AdvSubProc)//including THEATERSUNDHD && THEATERSOUND3D subprocess
            {
            case SRS_THEATERSOUND_TSHD_ON:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TSHD_, TRUE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_TSHD_OFF:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TSHD_, FALSE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_TSHD_SURR_ON:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TSHD_SURR_, TRUE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_TSHD_SURR_OFF:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TSHD_SURR_, FALSE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_TRUEBASS_ON:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TRUBASS_, TRUE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_TRUEBASS_OFF:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TRUBASS_, FALSE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_TRUBASS_LEVEL_INDP_ON:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TRUBASS_LEVEL_INDP_, TRUE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_TRUBASS_LEVEL_INDP_OFF:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TRUBASS_LEVEL_INDP_, FALSE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_DYNAMIC_CLARITY_ON:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_DC_, TRUE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_DYNAMIC_CLARITY_OFF:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_DC_, FALSE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_DEFINITION_ON:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_DEFINITION_, TRUE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_DEFINITION_OFF:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_DEFINITION_, FALSE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_TRUEVOLUME_ON:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TRUVOLUME_, TRUE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_TRUEVOLUME_OFF:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TRUVOLUME_, FALSE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_HARDLIMITER_ON:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_HARDLIMITER_, TRUE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_HARDLIMITER_OFF:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_HARDLIMITER_, FALSE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_HPF_ON:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_HPF_, TRUE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_HPF_OFF:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_HPF_, FALSE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_TRUEQ_ON:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TRUEQ_, TRUE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_TRUEQ_OFF:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TRUEQ_, FALSE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_TRUVOLUME_NOISE_MANAGER_ON:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TRUVOLUME_NOISE_MNGR_, TRUE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_TRUVOLUME_NOISE_MANAGER_OFF:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TRUVOLUME_NOISE_MNGR_, FALSE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_CS_ON:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_CS_, TRUE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_CS_OFF:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_CS_, FALSE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_TRUDIALOG_ON:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TRUDIALOG_, TRUE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_TRUDIALOG_OFF:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TRUDIALOG_, FALSE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_TRUVOLUME_NORMALIZER_ON:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TRUVOLUME_NORMALIZER_, TRUE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_TRUVOLUME_NORMALIZER_OFF:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TRUVOLUME_NORMALIZER_, FALSE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_TRUVOLUME_SMOOTH_ON:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TRUVOLUME_SMOOTH_, TRUE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_TRUVOLUME_SMOOTH_OFF:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_TRUVOLUME_SMOOTH_, FALSE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_HPF_END_ON:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_HPF_END_, TRUE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND_HPF_END_OFF:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_HPF_END_, FALSE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND3D_CC3D_ON:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_CC3D_EN_, TRUE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND3D_CC3D_OFF:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_CC3D_EN_, FALSE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND3D_CC3D_DEPTH_PROCESS_ON:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_CC3D_DEPTH_PROCESS_EN_, TRUE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND3D_CC3D_DEPTH_PROCESS_OFF:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_CC3D_DEPTH_PROCESS_EN_, FALSE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND3D_CC3D_3D_SURR_BOOST_ON:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_CC3D_3D_SURR_BOOST_EN_, TRUE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND3D_CC3D_3D_SURR_BOOST_OFF:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_CC3D_3D_SURR_BOOST_EN_, FALSE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND3D_CC3D_FADE_ON:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_CC3D_FADE_EN_, TRUE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND3D_CC3D_FADE_OFF:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_CC3D_FADE_EN_, FALSE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND3D_CC3D_TSHD_MIX_ON:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_CC3D_TSHD_MIX_EN_, TRUE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND3D_CC3D_TSHD_MIX_OFF:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_CC3D_TSHD_MIX_EN_, FALSE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND3D_CC3D_TBHDX_ON:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_CC3D_TBHDX_EN_, TRUE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND3D_CC3D_TBHDX_OFF:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_CC3D_TBHDX_EN_, FALSE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND3D_GEQ_ON:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_GEQ_EN_, TRUE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND3D_GEQ_OFF:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_GEQ_EN_, FALSE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND3D_ON:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_3D_EN_, TRUE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;

            case SRS_THEATERSOUND3D_OFF:
                if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_THEATERSOUND_3D_EN_, FALSE))
                {
                    rtn_status = RETURN_OK;
                }
                else
                {
                    rtn_status = RETURN_NOTOK;
                }
                break;
            default:
                rtn_status = RETURN_NOTOK;
                break;
            }
        break;

    case ADVSND_DTS_ULTRATV:
        mapi_interface::Get_mapi_audio()->ADVSND_ProcessEnable(DTS_ULTRATV_);
        switch(AdvSubProc)
        {
        case DTS_ULTRATV_ENVELO_ON:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(DTS_ULTRATV_EVO_, TRUE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case DTS_ULTRATV_ENVELO_OFF:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(DTS_ULTRATV_EVO_, FALSE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case DTS_ULTRATV_SYM_ON:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(DTS_ULTRATV_SYM_, TRUE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case DTS_ULTRATV_SYM_OFF:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(DTS_ULTRATV_SYM_, FALSE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        default:
            rtn_status = RETURN_NOTOK;
            break;
        }
        break;

    case ADVSND_AUDYSSEY:
        mapi_interface::Get_mapi_audio()->ADVSND_ProcessEnable(AUDYSSEY_);
        switch(AdvSubProc)
        {
        case AUDYSSEY_DYNAMIC_VOL_ON:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(AUDYSSEY_DYNAMICVOL_, TRUE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case AUDYSSEY_DYNAMIC_VOL_OFF:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(AUDYSSEY_DYNAMICVOL_, FALSE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case AUDYSSEY_DYNAMIC_EQ_ON:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(AUDYSSEY_DYNAMICEQ_, TRUE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case AUDYSSEY_DYNAMIC_EQ_OFF:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(AUDYSSEY_DYNAMICEQ_, FALSE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case AUDYSSEY_PEQ_ON:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(AUDYSSEY_EQ_, TRUE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case AUDYSSEY_PEQ_OFF:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(AUDYSSEY_EQ_, FALSE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case AUDYSSEY_ABX_ON:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(AUDYSSEY_ABX_, TRUE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case AUDYSSEY_ABX_OFF:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(AUDYSSEY_ABX_, FALSE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        default:
            rtn_status = RETURN_NOTOK;
            break;
        }
        break;

    case ADVSND_SUPER_VOICE:
        if(mapi_interface::Get_mapi_audio()->ADVSND_ProcessEnable(SUPER_VOICE_))
        {
            rtn_status = RETURN_OK;
        }
        else
        {
            rtn_status = RETURN_NOTOK;
        }
        break;

    case ADVSND_DBX:
        if(mapi_interface::Get_mapi_audio()->ADVSND_ProcessEnable(DBX_))
        {
            rtn_status = RETURN_OK;
        }
        else
        {
            rtn_status = RETURN_NOTOK;
        }
        break;

    case ADVSND_SRS_PURESOUND:
        switch(AdvSubProc)
        {
        case SRS_PURESND_ON:
        if(mapi_interface::Get_mapi_audio()->ADVSND_ProcessEnable(SRS_PURESND_))
        {
            rtn_status = RETURN_OK;
        }
        else
        {
            rtn_status = RETURN_NOTOK;
        }
        break;

        case SRS_PURESND_OFF:
            if(mapi_interface::Get_mapi_audio()->ADVSND_ProcessEnable(ADV_NONE_))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_PURESOUND_HL_ON:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_PURESOUND_HL_, TRUE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_PURESOUND_HL_OFF:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_PURESOUND_HL_, FALSE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_PURESOUND_AEQ_ON:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_PURESOUND_AEQ_, TRUE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_PURESOUND_AEQ_OFF:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_PURESOUND_AEQ_, FALSE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_PURESOUND_HPF_ON:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_PURESOUND_HPF_, TRUE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_PURESOUND_HPF_OFF:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_PURESOUND_HPF_, FALSE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_PURESOUND_TBHD_ON:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_PURESOUND_TBHD_, TRUE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

        case SRS_PURESOUND_TBHD_OFF:
            if(mapi_interface::Get_mapi_audio()->ADVSND_SubProcessEnable(SRS_PURESOUND_TBHD_, FALSE))
            {
                rtn_status = RETURN_OK;
            }
            else
            {
                rtn_status = RETURN_NOTOK;
            }
            break;

         default:
            rtn_status = RETURN_NOTOK;
            break;

        }
        break;

    case ADVSND_RESERVE4:
        if(mapi_interface::Get_mapi_audio()->ADVSND_ProcessEnable(RESERVE4_))
        {
            rtn_status = RETURN_OK;
        }
        else
        {
            rtn_status = RETURN_NOTOK;
        }
        break;

    case ADVSND_RESERVE5:
        if(mapi_interface::Get_mapi_audio()->ADVSND_ProcessEnable(RESERVE5_))
        {
            rtn_status = RETURN_OK;
        }
        else
        {
            rtn_status = RETURN_NOTOK;
        }
        break;

    case ADVSND_RESERVE6:
        if(mapi_interface::Get_mapi_audio()->ADVSND_ProcessEnable(RESERVE6_))
        {
            rtn_status = RETURN_OK;
        }
        else
        {
            rtn_status = RETURN_NOTOK;
        }
        break;

    case ADVSND_NONE:
        if(mapi_interface::Get_mapi_audio()->ADVSND_ProcessEnable(ADV_NONE_))
        {
            rtn_status = RETURN_OK;
        }
        else
        {
            rtn_status = RETURN_NOTOK;
        }
        break;

    default:
        rtn_status = RETURN_NOTOK;
        break;
    }

    if(SndTypeMuteFlag == MAPI_TRUE)
    {
        if(usermute == MAPI_FALSE) // check if bByUserAudioMute keep mute or not
        {
            usleep(AU_DELAY_FOR_SNDEFFECT_UNMUTE*1000); // mute time for leaving enable/disable, prevent un-mute pop noise
            mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_BYUSER_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
        }
    }

    return rtn_status;
}

//-------------------------------------------------------------------------------------------------
/// Set Advance Sound Effect Parameter.
/// @param advsnd_param_type     \b IN: ADVSND_PARAM_TYPE
/// @param *advsnd_param   \b IN: ST_ADVSND_PARAMETER Parameter Structure Pointer
/// @return MSRV_SSSND_RET: RETURN_OK, RETURN_NOTOK, RETURN_NOTSUPPORT
//-------------------------------------------------------------------------------------------------
MSRV_SSSND_RET  MSrv_SSSound::SetAdvancedSoundEffect(ADVSND_PARAM_TYPE advsnd_param_type, ST_ADVSND_PARAMETER *advsnd_param)
{
    MSRV_SSSND_RET return_tmp=RETURN_NOTOK;

    switch(advsnd_param_type)
    {
    case ADVSND_DOLBY_PL2VDPK_SMOD:
        m_ADVSND_DOLBY_PL2VDPK_SMOD = advsnd_param->PARAM_DOLBY_PL2VDPK_SMOD;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(DOLBY_PL2VDPK_SMOD_, advsnd_param->PARAM_DOLBY_PL2VDPK_SMOD, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_DOLBY_PL2VDPK_WMOD:
        m_ADVSND_DOLBY_PL2VDPK_WMOD = advsnd_param->PARAM_DOLBY_PL2VDPK_WMOD;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(DOLBY_PL2VDPK_WMOD_, advsnd_param->PARAM_DOLBY_PL2VDPK_WMOD, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_TSXT_SET_INPUT_GAIN:
        m_ADVSND_SRS_TSXT_SET_INPUT_GAIN = advsnd_param->PARAM_SRS_TSXT_SET_INPUT_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_TSXT_SET_INPUT_GAIN_, advsnd_param->PARAM_SRS_TSXT_SET_INPUT_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_TSXT_SET_DC_GAIN:
        m_ADVSND_SRS_TSXT_SET_DC_GAIN = advsnd_param->PARAM_SRS_TSXT_SET_DC_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_TSXT_SET_DC_GAIN_, advsnd_param->PARAM_SRS_TSXT_SET_DC_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_TSXT_SET_TRUBASS_GAIN:
        m_ADVSND_SRS_TSXT_SET_TRUBASS_GAIN = advsnd_param->PARAM_SRS_TSXT_SET_TRUBASS_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_TSXT_SET_TRUBASS_GAIN_, advsnd_param->PARAM_SRS_TSXT_SET_TRUBASS_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_TSXT_SET_SPEAKERSIZE:
        m_ADVSND_SRS_TSXT_SET_SPEAKERSIZE = advsnd_param->PARAM_SRS_TSXT_SET_SPEAKERSIZE;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_TSXT_SET_SPEAKERSIZE_, advsnd_param->PARAM_SRS_TSXT_SET_SPEAKERSIZE, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_TSXT_SET_INPUT_MODE:
        m_ADVSND_SRS_TSXT_SET_INPUT_MODE = advsnd_param->PARAM_SRS_TSXT_SET_INPUT_MODE;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_TSXT_SET_INPUT_MODE_, advsnd_param->PARAM_SRS_TSXT_SET_INPUT_MODE, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_TSXT_SET_OUTPUT_GAIN:
        m_ADVSND_SRS_TSXT_SET_OUTPUT_GAIN = advsnd_param->PARAM_SRS_TSXT_SET_OUTPUT_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_TSXT_SET_OUTPUT_GAIN_, advsnd_param->PARAM_SRS_TSXT_SET_OUTPUT_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

        //SRS TSHD Sub-Process Setting Parameter
    case ADVSND_SRS_TSHD_SET_INPUT_MODE:
        m_ADVSND_SRS_TSHD_SET_INPUT_MODE = advsnd_param->PARAM_SRS_TSHD_SET_INPUT_MODE;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_TSHD_SET_INPUT_MODE_, advsnd_param->PARAM_SRS_TSHD_SET_INPUT_MODE, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_TSHD_SET_OUTPUT_MODE:
        m_ADVSND_SRS_TSHD_SET_OUTPUT_MODE = advsnd_param->PARAM_SRS_TSHD_SET_OUTPUT_MODE;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_TSHD_SET_OUTPUT_MODE_, advsnd_param->PARAM_SRS_TSHD_SET_OUTPUT_MODE, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_TSHD_SET_SPEAKERSIZE:
        m_ADVSND_SRS_TSHD_SET_SPEAKERSIZE = advsnd_param->PARAM_SRS_TSHD_SET_SPEAKERSIZE;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_TSHD_SET_SPEAKERSIZE_, advsnd_param->PARAM_SRS_TSHD_SET_SPEAKERSIZE, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_TSHD_SET_TRUBASS_CONTROL:
        m_ADVSND_SRS_TSHD_SET_TRUBASS_CONTROL = advsnd_param->PARAM_SRS_TSHD_SET_TRUBASS_CONTROL;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_TSHD_SET_TRUBASS_CONTROL_, advsnd_param->PARAM_SRS_TSHD_SET_TRUBASS_CONTROL, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_TSHD_SET_DEFINITION_CONTROL:
        m_ADVSND_SRS_TSHD_SET_DEFINITION_CONTROL = advsnd_param->PARAM_SRS_TSHD_SET_DEFINITION_CONTROL;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_TSHD_SET_DEFINITION_CONTROL_, advsnd_param->PARAM_SRS_TSHD_SET_DEFINITION_CONTROL, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_TSHD_SET_DC_CONTROL:
        m_ADVSND_SRS_TSHD_SET_DC_CONTROL = advsnd_param->PARAM_SRS_TSHD_SET_DC_CONTROL;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_TSHD_SET_DC_CONTROL_, advsnd_param->PARAM_SRS_TSHD_SET_DC_CONTROL, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_TSHD_SET_SURROUND_LEVEL:
        m_ADVSND_SRS_TSHD_SET_SURROUND_LEVEL = advsnd_param->PARAM_SRS_TSHD_SET_SURROUND_LEVEL;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_TSHD_SET_SURROUND_LEVEL_, advsnd_param->PARAM_SRS_TSHD_SET_SURROUND_LEVEL, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_TSHD_SET_INPUT_GAIN:
        m_ADVSND_SRS_TSHD_SET_INPUT_GAIN = advsnd_param->PARAM_SRS_TSHD_SET_INPUT_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_TSHD_SET_INPUT_GAIN_, advsnd_param->PARAM_SRS_TSHD_SET_INPUT_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_TSHD_SET_WOWSPACE_CONTROL:
        m_ADVSND_SRS_TSHD_SET_WOWSPACE_CONTROL = advsnd_param->PARAM_SRS_TSHD_SET_WOWSPACE_CONTROL;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_TSHD_SET_WOWSPACE_CONTROL_, advsnd_param->PARAM_SRS_TSHD_SET_WOWSPACE_CONTROL, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_TSHD_SET_WOWCENTER_CONTROL:
        m_ADVSND_SRS_TSHD_SET_WOWCENTER_CONTROL = advsnd_param->PARAM_SRS_TSHD_SET_WOWCENTER_CONTROL;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_TSHD_SET_WOWCENTER_CONTROL_, advsnd_param->PARAM_SRS_TSHD_SET_WOWCENTER_CONTROL, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_TSHD_SET_WOWHDSRS3DMODE:
        m_ADVSND_SRS_TSHD_SET_WOWHDSRS3DMODE = advsnd_param->PARAM_SRS_TSHD_SET_WOWHDSRS3DMODE;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_TSHD_SET_WOWHDSRS3DMODE_, advsnd_param->PARAM_SRS_TSHD_SET_WOWHDSRS3DMODE, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_TSHD_SET_LIMITERCONTROL:
        m_ADVSND_SRS_TSHD_SET_LIMITERCONTROL = advsnd_param->PARAM_SRS_TSHD_SET_LIMITERCONTROL;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_TSHD_SET_LIMITERCONTROL_, advsnd_param->PARAM_SRS_TSHD_SET_LIMITERCONTROL, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_TSHD_SET_OUTPUT_GAIN:
        m_ADVSND_SRS_TSHD_SET_OUTPUT_GAIN = advsnd_param->PARAM_SRS_TSHD_SET_OUTPUT_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_TSHD_SET_OUTPUT_GAIN_, advsnd_param->PARAM_SRS_TSHD_SET_OUTPUT_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

        //SRS_THEATERSOUND Sub-Process Setting Parameter
    case ADVSND_SRS_THEATERSOUND_INPUT_GAIN:
        m_ADVSND_SRS_THEATERSOUND_INPUT_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND_INPUT_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_INPUT_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND_INPUT_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_OUTPUT_GAIN:
        m_ADVSND_SRS_THEATERSOUND_OUTPUT_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND_OUTPUT_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_OUTPUT_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND_OUTPUT_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_BYPASS_GAIN:
        m_ADVSND_SRS_THEATERSOUND_HEADROOM_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND_BYPASS_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_BYPASS_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND_BYPASS_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_HEADROOM_GAIN:
        m_ADVSND_SRS_THEATERSOUND_HEADROOM_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND_HEADROOM_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_HEADROOM_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND_HEADROOM_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_INPUT_MODE:
        m_ADVSND_SRS_THEATERSOUND_INPUT_MODE = advsnd_param->PARAM_SRS_THEATERSOUND_INPUT_MODE;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_INPUT_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND_INPUT_MODE, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_DEFINITION_CONTROL:
        m_ADVSND_SRS_THEATERSOUND_DEFINITION_CONTROL = advsnd_param->PARAM_SRS_THEATERSOUND_DEFINITION_CONTROL;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_DEFINITION_CONTROL_, advsnd_param->PARAM_SRS_THEATERSOUND_DEFINITION_CONTROL, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_DC_CONTROL:
        m_ADVSND_SRS_THEATERSOUND_DC_CONTROL = advsnd_param->PARAM_SRS_THEATERSOUND_DC_CONTROL;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_DC_CONTROL_, advsnd_param->PARAM_SRS_THEATERSOUND_DC_CONTROL, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_TRUBASS_CONTROL:
        m_ADVSND_SRS_THEATERSOUND_TRUBASS_CONTROL = advsnd_param->PARAM_SRS_THEATERSOUND_TRUBASS_CONTROL;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_TRUBASS_CONTROL_, advsnd_param->PARAM_SRS_THEATERSOUND_TRUBASS_CONTROL, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_SPEAKERSIZE:
        m_ADVSND_SRS_THEATERSOUND_SPEAKERSIZE = advsnd_param->PARAM_SRS_THEATERSOUND_SPEAKERSIZE;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_SPEAKERSIZE_, advsnd_param->PARAM_SRS_THEATERSOUND_SPEAKERSIZE, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_HARDLIMITER_LEVEL:
        m_ADVSND_SRS_THEATERSOUND_HARDLIMITER_LEVEL = advsnd_param->PARAM_SRS_THEATERSOUND_HARDLIMITER_LEVEL;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_HARDLIMITER_LEVEL_, advsnd_param->PARAM_SRS_THEATERSOUND_HARDLIMITER_LEVEL, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_HARDLIMITER_BOOST_GAIN:
        m_ADVSND_SRS_THEATERSOUND_HARDLIMITER_BOOST_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND_HARDLIMITER_BOOST_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_HARDLIMITER_BOOST_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND_HARDLIMITER_BOOST_GAIN, 0);
        return_tmp = RETURN_OK;
        break;
        // TRUVOLUME
    case ADVSND_SRS_THEATERSOUND_TRUVOLUME_MODE:
        m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_MODE = advsnd_param->PARAM_SRS_THEATERSOUND_TRUVOLUME_MODE;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_TRUVOLUME_MODE_, advsnd_param->PARAM_SRS_THEATERSOUND_TRUVOLUME_MODE, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_TRUVOLUME_REF_LEVEL:
        m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_REF_LEVEL = advsnd_param->PARAM_SRS_THEATERSOUND_TRUVOLUME_REF_LEVEL;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_TRUVOLUME_REF_LEVEL_, advsnd_param->PARAM_SRS_THEATERSOUND_TRUVOLUME_REF_LEVEL, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_TRUVOLUME_MAX_GAIN:
        m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_MAX_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND_TRUVOLUME_MAX_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_TRUVOLUME_MAX_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND_TRUVOLUME_MAX_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_TRUVOLUME_NOISE_MNGR_THLD:
        m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_NOISE_MNGR_THLD = advsnd_param->PARAM_SRS_THEATERSOUND_TRUVOLUME_NOISE_MNGR_THLD;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_TRUVOLUME_NOISE_MNGR_THLD_, advsnd_param->PARAM_SRS_THEATERSOUND_TRUVOLUME_NOISE_MNGR_THLD, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_TRUVOLUME_CALIBRATE:
        m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_CALIBRATE = advsnd_param->PARAM_SRS_THEATERSOUND_TRUVOLUME_CALIBRATE;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_TRUVOLUME_CALIBRATE_, advsnd_param->PARAM_SRS_THEATERSOUND_TRUVOLUME_CALIBRATE, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_TRUVOLUME_INPUT_GAIN:
        m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_INPUT_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND_TRUVOLUME_INPUT_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_TRUVOLUME_INPUT_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND_TRUVOLUME_INPUT_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_TRUVOLUME_OUTPUT_GAIN:
        m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_OUTPUT_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND_TRUVOLUME_OUTPUT_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_TRUVOLUME_OUTPUT_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND_TRUVOLUME_OUTPUT_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_TVOLHD_BYPASS_GAIN:
        m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_BYPASS_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND_TRUVOLUME_BYPASS_GAIN;
            mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_TRUVOLUME_BYPASS_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND_TRUVOLUME_BYPASS_GAIN, 0);
        return_tmp = RETURN_OK;
        break;
        // CS
    case ADVSND_SRS_THEATERSOUND_CS_INPUT_GAIN:
        m_ADVSND_SRS_THEATERSOUND_CS_INPUT_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND_CS_INPUT_GAIN;
            mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_CS_INPUT_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND_CS_INPUT_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_CS_PROCESS_MODE:
        m_ADVSND_SRS_THEATERSOUND_CS_PROCESS_MODE = advsnd_param->PARAM_SRS_THEATERSOUND_CS_PROCESS_MODE;
            mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_CS_PROCESS_MODE_, advsnd_param->PARAM_SRS_THEATERSOUND_CS_PROCESS_MODE, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_CS_LR_OUTPUT_GAIN:
        m_ADVSND_SRS_THEATERSOUND_CS_LR_OUTPUT_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND_CS_LR_OUTPUT_GAIN;
            mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_CS_LR_OUTPUT_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND_CS_LR_OUTPUT_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_CS_LSRS_OUTPUT_GAIN:
        m_ADVSND_SRS_THEATERSOUND_CS_LSRS_OUTPUT_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND_CS_LSRS_OUTPUT_GAIN;
            mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_CS_LsRs_OUTPUT_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND_CS_LSRS_OUTPUT_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_CS_CENTER_OUTPUT_GAIN:
        m_ADVSND_SRS_THEATERSOUND_CS_CENTER_OUTPUT_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND_CS_CENTER_OUTPUT_GAIN;
            mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_CS_Center_OUTPUT_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND_CS_CENTER_OUTPUT_GAIN, 0);
        return_tmp = RETURN_OK;
        break;
        // TRUDIALOG
    case ADVSND_SRS_THEATERSOUND_TRUDIALOG_INPUT_GAIN:
        m_ADVSND_SRS_THEATERSOUND_TRUDIALOG_INPUT_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND_TRUDIALOG_INPUT_GAIN;
            mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_TRUDIALOG_INPUT_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND_TRUDIALOG_INPUT_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_TRUDIALOG_OUTPUT_GAIN:
        m_ADVSND_SRS_THEATERSOUND_TRUDIALOG_OUTPUT_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND_TRUDIALOG_OUTPUT_GAIN;
            mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_TRUDIALOG_OUTPUT_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND_TRUDIALOG_OUTPUT_GAIN, 0);
        return_tmp = RETURN_OK;
        break;


    case ADVSND_SRS_THEATERSOUND_TRUDIALOG_BYPASS_GAIN:
        m_ADVSND_SRS_THEATERSOUND_TRUDIALOG_BYPASS_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND_TRUDIALOG_BYPASS_GAIN;
            mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_TRUDIALOG_BYPASS_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND_TRUDIALOG_BYPASS_GAIN, 0);
        return_tmp = RETURN_OK;
        break;


    case ADVSND_SRS_THEATERSOUND_TRUDIALOG_PROCESS_GAIN:
        m_ADVSND_SRS_THEATERSOUND_TRUDIALOG_PROCESS_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND_TRUDIALOG_PROCESS_GAIN;
            mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_TRUDIALOG_PROCESS_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND_TRUDIALOG_PROCESS_GAIN, 0);
        return_tmp = RETURN_OK;
        break;


    case ADVSND_SRS_THEATERSOUND_TRUDIALOG_CLARITY_GAIN:
        m_ADVSND_SRS_THEATERSOUND_TRUDIALOG_CLARITY_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND_TRUDIALOG_CLARITY_GAIN;
            mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_TRUDIALOG_CLARITY_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND_TRUDIALOG_CLARITY_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_TRUVOLUME_NORMALIZE_THRESH:
        m_ADVSND_SRS_THEATERSOUND_NORMALIZER_THRESH = advsnd_param->PARAM_SRS_THEATERSOUND_NORMALIZER_THRESH;
            mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_TRUVOLUME_NORMALIZE_THRESH_, advsnd_param->PARAM_SRS_THEATERSOUND_NORMALIZER_THRESH, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_HPF_FC:
        m_ADVSND_SRS_THEATERSOUND_HPF_FC = advsnd_param->PARAM_SRS_THEATERSOUND_HPF_FC;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_HPF_FC_, advsnd_param->PARAM_SRS_THEATERSOUND_HPF_FC, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_TSHD_INPUT_GAIN:
        m_ADVSND_SRS_THEATERSOUND_TSHD_INPUT_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND_TSHD_INPUT_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_TSHD_INPUT_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND_TSHD_INPUT_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_TSHD_OUTPUT_GAIN:
        m_ADVSND_SRS_THEATERSOUND_TSHD_OUTPUT_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND_TSHD_OUTPUT_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_TSHD_OUTPUT_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND_TSHD_OUTPUT_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_SURR_LEVEL_CONTROL:
        m_ADVSND_SRS_THEATERSOUND_SURR_LEVEL_CONTROL = advsnd_param->PARAM_SRS_THEATERSOUND_SURR_LEVEL_CONTROL;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_SURR_LEVEL_CONTROL_, advsnd_param->PARAM_SRS_THEATERSOUND_SURR_LEVEL_CONTROL, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_TRUBASS_COMPRESSOR_CONTROL:
        m_ADVSND_SRS_THEATERSOUND_TRUBASS_COMPRESSOR_CONTROL = advsnd_param->PARAM_SRS_THEATERSOUND_TRUBASS_COMPRESSOR_CONTROL;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_TRUBASS_COMPRESSOR_CONTROL_, advsnd_param->PARAM_SRS_THEATERSOUND_TRUBASS_COMPRESSOR_CONTROL, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_TRUBASS_PROCESS_MODE:
        m_ADVSND_SRS_THEATERSOUND_TRUBASS_PROCESS_MODE = advsnd_param->PARAM_SRS_THEATERSOUND_TRUBASS_PROCESS_MODE;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_TRUBASS_PROCESS_MODE_, advsnd_param->PARAM_SRS_THEATERSOUND_TRUBASS_PROCESS_MODE, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_TRUBASS_SPEAKER_AUDIO:
        m_ADVSND_SRS_THEATERSOUND_TRUBASS_SPEAKER_AUDIO = advsnd_param->PARAM_SRS_THEATERSOUND_TRUBASS_SPEAKER_AUDIO;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_TRUBASS_SPEAKER_AUDIO_, advsnd_param->PARAM_SRS_THEATERSOUND_TRUBASS_SPEAKER_AUDIO, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND_SPEAKER_ANALYSIS:
        m_ADVSND_SRS_THEATERSOUND_TRUBASS_SPEAKER_ANALYSIS = advsnd_param->PARAM_SRS_THEATERSOUND_TRUBASS_SPEAKER_ANALYSIS;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_TRUBASS_SPEAKER_ANALYSIS_, advsnd_param->PARAM_SRS_THEATERSOUND_TRUBASS_SPEAKER_ANALYSIS, 0);
        return_tmp = RETURN_OK;
        break;

        //TheaterSound 3D Setting Parameter
    case ADVSND_SRS_THEATERSOUND3D_CC3D_INPUT_GAIN:
        m_ADVSND_SRS_THEATERSOUND3D_CC3D_INPUT_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_INPUT_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_CC3D_INPUT_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_INPUT_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND3D_CC3D_OUTPUT_GAIN:
        m_ADVSND_SRS_THEATERSOUND3D_CC3D_OUTPUT_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_OUTPUT_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_CC3D_OUTPUT_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_OUTPUT_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND3D_CC3D_BYPASS_GAIN:
        m_ADVSND_SRS_THEATERSOUND3D_CC3D_BYPASS_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_BYPASS_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_CC3D_BYPASS_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_BYPASS_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND3D_CC3D_APERTURE:
        m_ADVSND_SRS_THEATERSOUND3D_CC3D_APERTURE = advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_APERTURE;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_CC3D_APERTURE_, advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_APERTURE, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND3D_CC3D_GAINLIMIT:
        m_ADVSND_SRS_THEATERSOUND3D_CC3D_GAINLIMIT = advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_GAINLIMIT;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_CC3D_GAINLIMIT_, advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_GAINLIMIT, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND3D_CC3D_FF_DEPTH:
        m_ADVSND_SRS_THEATERSOUND3D_CC3D_FF_DEPTH = advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_FF_DEPTH;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_CC3D_FF_DEPTH_, advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_FF_DEPTH, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND3D_CC3D_NF_DEPTH:
        m_ADVSND_SRS_THEATERSOUND3D_CC3D_NF_DEPTH = advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_NF_DEPTH;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_CC3D_NF_DEPTH_, advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_NF_DEPTH, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND3D_CC3D_TSHD_MIX_FADE_CTRL:
        m_ADVSND_SRS_THEATERSOUND3D_CC3D_TSHD_MIX_FADE_CTRL = advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_TSHD_MIX_FADE_CTRL;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_CC3D_TSHD_MIX_FADE_CTRL_, advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_TSHD_MIX_FADE_CTRL, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND3D_CC3D_TBHDX_INPUT_GAIN:
        m_ADVSND_SRS_THEATERSOUND3D_CC3D_TBHDX_INPUT_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_TBHDX_INPUT_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_CC3D_TBHDX_INPUT_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_TBHDX_INPUT_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND3D_CC3D_TBHDX_BASSLEVEL:
        m_ADVSND_SRS_THEATERSOUND3D_CC3D_TBHDX_BASSLEVEL = advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_TBHDX_BASSLEVEL;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_CC3D_TBHDX_BASSLEVEL_, advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_TBHDX_BASSLEVEL, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND3D_CC3D_TBHDX_SPEAKERSIZE:
        m_ADVSND_SRS_THEATERSOUND3D_CC3D_TBHDX_SPEAKERSIZE = advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_TBHDX_SPEAKERSIZE;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_CC3D_TBHDX_SPEAKERSIZE_, advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_TBHDX_SPEAKERSIZE, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND3D_CC3D_TBHDX_MODE:
        m_ADVSND_SRS_THEATERSOUND3D_CC3D_TBHDX_MODE = advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_TBHDX_MODE;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_CC3D_TBHDX_MODE_, advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_TBHDX_MODE, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND3D_CC3D_TBHDX_DYNAMICS:
        m_ADVSND_SRS_THEATERSOUND3D_CC3D_TBHDX_DYNAMICS = advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_TBHDX_DYNAMICS;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_CC3D_TBHDX_DYNAMICS_, advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_TBHDX_DYNAMICS, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND3D_CC3D_TBHDX_HP_ORDER:
        m_ADVSND_SRS_THEATERSOUND3D_CC3D_TBHDX_HP_ORDER = advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_TBHDX_HP_ORDER;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_CC3D_TBHDX_HP_ORDER_, advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_TBHDX_HP_ORDER, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND3D_CC3D_TBHDX_CUSTOM_FILTER:
        m_ADVSND_SRS_THEATERSOUND3D_CC3D_TBHDX_CUSTOM_FILTER = advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_TBHDX_CUSTOM_FILTER;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_CC3D_TBHDX_CUSTOM_FILTER_, advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_TBHDX_CUSTOM_FILTER, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND3D_GEQ_INPUT_GAIN:
        m_ADVSND_SRS_THEATERSOUND3D_GEQ_INPUT_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND3D_GEQ_INPUT_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_GEQ_INPUT_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND3D_GEQ_INPUT_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND3D_GEQ_BAND0_GAIN:
        m_ADVSND_SRS_THEATERSOUND3D_GEQ_BAND0_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND3D_GEQ_BAND0_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_GEQ_BAND0_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND3D_GEQ_BAND0_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND3D_GEQ_BAND1_GAIN:
        m_ADVSND_SRS_THEATERSOUND3D_GEQ_BAND1_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND3D_GEQ_BAND1_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_GEQ_BAND1_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND3D_GEQ_BAND1_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND3D_GEQ_BAND2_GAIN:
        m_ADVSND_SRS_THEATERSOUND3D_GEQ_BAND2_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND3D_GEQ_BAND2_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_GEQ_BAND2_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND3D_GEQ_BAND2_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND3D_GEQ_BAND3_GAIN:
        m_ADVSND_SRS_THEATERSOUND3D_GEQ_BAND3_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND3D_GEQ_BAND3_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_GEQ_BAND3_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND3D_GEQ_BAND3_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND3D_GEQ_BAND4_GAIN:
        m_ADVSND_SRS_THEATERSOUND3D_GEQ_BAND4_GAIN = advsnd_param->PARAM_SRS_THEATERSOUND3D_GEQ_BAND4_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_GEQ_BAND4_GAIN_, advsnd_param->PARAM_SRS_THEATERSOUND3D_GEQ_BAND4_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_THEATERSOUND3D_CC3D_PROCESS_PATH:
        m_ADVSND_SRS_THEATERSOUND3D_CC3D_PROCESS_PATH = advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_PROCESS_PATH;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_THEATERSOUND_CC3D_PROCESS_PATH_, advsnd_param->PARAM_SRS_THEATERSOUND3D_CC3D_PROCESS_PATH, 0);
        return_tmp = RETURN_OK;
        break;

        //DTS_ULTRATV Sub-Process Setting Parameter
    case ADVSND_DTS_ULTRATV_EVO_MONOINPUT:
        m_ADVSND_DTS_ULTRATV_EVO_MONOINPUT = advsnd_param->PARAM_DTS_ULTRATV_EVO_MONOINPUT;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(DTS_ULTRATV_EVO_MONOINPUT_, advsnd_param->PARAM_DTS_ULTRATV_EVO_MONOINPUT, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_DTS_ULTRATV_EVO_WIDENINGON:
        m_ADVSND_DTS_ULTRATV_EVO_WIDENINGON = advsnd_param->PARAM_DTS_ULTRATV_EVO_WIDENINGON;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(DTS_ULTRATV_EVO_WIDENINGON_, advsnd_param->PARAM_DTS_ULTRATV_EVO_WIDENINGON, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_DTS_ULTRATV_EVO_ADD3DBON:
        m_ADVSND_DTS_ULTRATV_EVO_ADD3DBON = advsnd_param->PARAM_DTS_ULTRATV_EVO_ADD3DBON;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(DTS_ULTRATV_EVO_ADD3DBON_, advsnd_param->PARAM_DTS_ULTRATV_EVO_ADD3DBON, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_DTS_ULTRATV_EVO_PCELEVEL:
        m_ADVSND_DTS_ULTRATV_EVO_PCELEVEL = advsnd_param->PARAM_DTS_ULTRATV_EVO_PCELEVEL;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(DTS_ULTRATV_EVO_PCELEVEL_, advsnd_param->PARAM_DTS_ULTRATV_EVO_PCELEVEL, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_DTS_ULTRATV_EVO_VLFELEVEL:
        m_ADVSND_DTS_ULTRATV_EVO_VLFELEVEL = advsnd_param->PARAM_DTS_ULTRATV_EVO_VLFELEVEL;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(DTS_ULTRATV_EVO_VLFELEVEL_, advsnd_param->PARAM_DTS_ULTRATV_EVO_VLFELEVEL, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_DTS_ULTRATV_SYM_DEFAULT:
        m_ADVSND_DTS_ULTRATV_SYM_DEFAULT = advsnd_param->PARAM_DTS_ULTRATV_SYM_DEFAULT;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(DTS_ULTRATV_SYM_DEFAULT_, advsnd_param->PARAM_DTS_ULTRATV_SYM_DEFAULT, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_DTS_ULTRATV_SYM_MODE:
        m_ADVSND_DTS_ULTRATV_SYM_MODE = advsnd_param->PARAM_DTS_ULTRATV_SYM_MODE;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(DTS_ULTRATV_SYM_MODE_, advsnd_param->PARAM_DTS_ULTRATV_SYM_MODE, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_DTS_ULTRATV_SYM_LEVEL:
        m_ADVSND_DTS_ULTRATV_SYM_LEVEL = advsnd_param->PARAM_DTS_ULTRATV_SYM_LEVEL;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(DTS_ULTRATV_SYM_LEVEL_, advsnd_param->PARAM_DTS_ULTRATV_SYM_LEVEL, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_DTS_ULTRATV_SYM_RESET:
        m_ADVSND_DTS_ULTRATV_SYM_RESET = advsnd_param->PARAM_DTS_ULTRATV_SYM_RESET;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(DTS_ULTRATV_SYM_RESET_, advsnd_param->PARAM_DTS_ULTRATV_SYM_RESET, 0);
        return_tmp = RETURN_OK;
        break;

        //AUDYSSEY Sub-Process Setting Parameter
    case ADVSND_AUDYSSEY_DYNAMICVOL_COMPRESS_MODE:
        m_ADVSND_AUDYSSEY_DYNAMICVOL_COMPRESS_MODE = advsnd_param->PARAM_AUDYSSEY_DYNAMICVOL_COMPRESS_MODE;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(AUDYSSEY_DYNAMICVOL_COMPRESS_MODE_, advsnd_param->PARAM_AUDYSSEY_DYNAMICVOL_COMPRESS_MODE, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_AUDYSSEY_DYNAMICVOL_GC:
        m_ADVSND_AUDYSSEY_DYNAMICVOL_GC = advsnd_param->PARAM_AUDYSSEY_DYNAMICVOL_GC;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(AUDYSSEY_DYNAMICVOL_GC_, advsnd_param->PARAM_AUDYSSEY_DYNAMICVOL_GC, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_AUDYSSEY_DYNAMICVOL_VOLSETTING:
        m_ADVSND_AUDYSSEY_DYNAMICVOL_VOLSETTING = advsnd_param->PARAM_AUDYSSEY_DYNAMICVOL_VOLSETTING;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(AUDYSSEY_DYNAMICVOL_VOLSETTING_, advsnd_param->PARAM_AUDYSSEY_DYNAMICVOL_VOLSETTING, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_AUDYSSEY_DYNAMICEQ_EQOFFSET:
        m_ADVSND_AUDYSSEY_DYNAMICEQ_EQOFFSET = advsnd_param->PARAM_AUDYSSEY_DYNAMICEQ_EQOFFSET;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(AUDYSSEY_DYNAMICEQ_EQOFFSET_, advsnd_param->PARAM_AUDYSSEY_DYNAMICEQ_EQOFFSET, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_AUDYSSEY_ABX_GWET:
        m_ADVSND_AUDYSSEY_ABX_GWET = advsnd_param->PARAM_AUDYSSEY_ABX_GWET;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(AUDYSSEY_ABX_GWET_, advsnd_param->PARAM_AUDYSSEY_ABX_GWET, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_AUDYSSEY_ABX_GDRY:
        m_ADVSND_AUDYSSEY_ABX_GDRY = advsnd_param->PARAM_AUDYSSEY_ABX_GDRY;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(AUDYSSEY_ABX_GDRY_, advsnd_param->PARAM_AUDYSSEY_ABX_GDRY, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_AUDYSSEY_ABX_FILSET:
        m_ADVSND_AUDYSSEY_ABX_FILSET = advsnd_param->PARAM_AUDYSSEY_ABX_FILSET;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(AUDYSSEY_ABX_FILSET_, advsnd_param->PARAM_AUDYSSEY_ABX_FILSET, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_PURESOUND_HL_INPUT_GAIN:
        m_ADVSND_SRS_PURESOUND_HL_INPUT_GAIN = advsnd_param->PARAM_SRS_PURESOUND_HL_INPUT_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_PURESOUND_HL_INPUT_GAIN_, advsnd_param->PARAM_SRS_PURESOUND_HL_INPUT_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_PURESOUND_HL_OUTPUT_GAIN:
        m_ADVSND_SRS_PURESOUND_HL_OUTPUT_GAIN = advsnd_param->PARAM_SRS_PURESOUND_HL_OUTPUT_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_PURESOUND_HL_OUTPUT_GAIN_, advsnd_param->PARAM_SRS_PURESOUND_HL_OUTPUT_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_PURESOUND_HL_BYPASS_GAIN:
        m_ADVSND_SRS_PURESOUND_HL_BYPASS_GAIN = advsnd_param->PARAM_SRS_PURESOUND_HL_BYPASS_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_PURESOUND_HL_BYPASS_GAIN_, advsnd_param->PARAM_SRS_PURESOUND_HL_BYPASS_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_PURESOUND_HL_LIMITERBOOST:
        m_ADVSND_SRS_PURESOUND_HL_LIMITERBOOST = advsnd_param->PARAM_SRS_PURESOUND_HL_LIMITERBOOST;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_PURESOUND_HL_LIMITERBOOST_, advsnd_param->PARAM_SRS_PURESOUND_HL_LIMITERBOOST, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_PURESOUND_HL_HARDLIMIT:
        m_ADVSND_SRS_PURESOUND_HL_HARDLIMIT = advsnd_param->PARAM_SRS_PURESOUND_HL_HARDLIMIT;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_PURESOUND_HL_HARDLIMIT_, advsnd_param->PARAM_SRS_PURESOUND_HL_HARDLIMIT, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_PURESOUND_HL_DELAYLEN:
        m_ADVSND_SRS_PURESOUND_HL_DELAYLEN = advsnd_param->PARAM_SRS_PURESOUND_HL_DELAYLEN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_PURESOUND_HL_DELAYLEN_, advsnd_param->PARAM_SRS_PURESOUND_HL_DELAYLEN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_PURESOUND_AEQ_INPUT_GAIN:
        m_ADVSND_SRS_PURESOUND_AEQ_INPUT_GAIN = advsnd_param->PARAM_SRS_PURESOUND_AEQ_INPUT_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_PURESOUND_AEQ_INPUT_GAIN_, advsnd_param->PARAM_SRS_PURESOUND_AEQ_INPUT_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_PURESOUND_AEQ_OUTPUT_GAIN:
        m_ADVSND_SRS_PURESOUND_AEQ_OUTPUT_GAIN = advsnd_param->PARAM_SRS_PURESOUND_AEQ_OUTPUT_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_PURESOUND_AEQ_OUTPUT_GAIN_, advsnd_param->PARAM_SRS_PURESOUND_AEQ_OUTPUT_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_PURESOUND_AEQ_BYPASS_GAIN:
        m_ADVSND_SRS_PURESOUND_AEQ_BYPASS_GAIN = advsnd_param->PARAM_SRS_PURESOUND_AEQ_BYPASS_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_PURESOUND_AEQ_BYPASS_GAIN_, advsnd_param->PARAM_SRS_PURESOUND_AEQ_BYPASS_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_PURESOUND_HPF_FREQUENCY:
        m_ADVSND_SRS_PURESOUND_HPF_FREQUENCY = advsnd_param->PARAM_SRS_PURESOUND_HPF_FREQUENCY;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_PURESOUND_HPF_FREQUENCY_, advsnd_param->PARAM_SRS_PURESOUND_HPF_FREQUENCY, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_PURESOUND_TBHD_TRUBASS_LEVEL:
        m_ADVSND_SRS_PURESOUND_TBHD_TRUBASS_LEVEL = advsnd_param->PARAM_SRS_PURESOUND_TBHD_TRUBASS_LEVEL;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_PURESOUND_TBHD_TRUBASS_LEVEL_, advsnd_param->PARAM_SRS_PURESOUND_TBHD_TRUBASS_LEVEL, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_PURESOUND_TBHD_SPEAKER_SIZE:
        m_ADVSND_SRS_PURESOUND_TBHD_SPEAKER_SIZE = advsnd_param->PARAM_SRS_PURESOUND_TBHD_SPEAKER_SIZE;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_PURESOUND_TBHD_SPEAKER_SIZE_, advsnd_param->PARAM_SRS_PURESOUND_TBHD_SPEAKER_SIZE, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_PURESOUND_TBHD_LEVEL_INDEPENDENT_EN:
        m_ADVSND_SRS_PURESOUND_TBHD_LEVEL_INDEPENDENT_EN = advsnd_param->PARAM_SRS_PURESOUND_TBHD_LEVEL_INDEPENDENT_EN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_PURESOUND_TBHD_LEVEL_INDEPENDENT_EN_, advsnd_param->PARAM_SRS_PURESOUND_TBHD_LEVEL_INDEPENDENT_EN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_PURESOUND_TBHD_COMPRESSOR_LEVEL:
        m_ADVSND_SRS_PURESOUND_TBHD_COMPRESSOR_LEVEL = advsnd_param->PARAM_SRS_PURESOUND_TBHD_COMPRESSOR_LEVEL;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_PURESOUND_TBHD_COMPRESSOR_LEVEL_, advsnd_param->PARAM_SRS_PURESOUND_TBHD_COMPRESSOR_LEVEL, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_PURESOUND_TBHD_MODE:
        m_ADVSND_SRS_PURESOUND_TBHD_MODE = advsnd_param->PARAM_SRS_PURESOUND_TBHD_MODE;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_PURESOUND_TBHD_MODE_, advsnd_param->PARAM_SRS_PURESOUND_TBHD_MODE, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_PURESOUND_TBHD_SPEAKER_AUDIO:
        m_ADVSND_SRS_PURESOUND_TBHD_SPEAKER_AUDIO = advsnd_param->PARAM_SRS_PURESOUND_TBHD_SPEAKER_AUDIO;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_PURESOUND_TBHD_SPEAKER_AUDIO_, advsnd_param->PARAM_SRS_PURESOUND_TBHD_SPEAKER_AUDIO, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_PURESOUND_TBHD_SPEAKER_ANALYSIS:
        m_ADVSND_SRS_PURESOUND_TBHD_SPEAKER_ANALYSIS = advsnd_param->PARAM_SRS_PURESOUND_TBHD_SPEAKER_ANALYSIS;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_PURESOUND_TBHD_SPEAKER_ANALYSIS_, advsnd_param->PARAM_SRS_PURESOUND_TBHD_SPEAKER_ANALYSIS, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_PURESOUND_INPUT_GAIN:
        m_ADVSND_SRS_PURESOUND_INPUT_GAIN = advsnd_param->PARAM_SRS_PURESOUND_INPUT_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_PURESOUND_INPUT_GAIN_, advsnd_param->PARAM_SRS_PURESOUND_INPUT_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    case ADVSND_SRS_PURESOUND_OUTPUT_GAIN:
        m_ADVSND_SRS_PURESOUND_OUTPUT_GAIN = advsnd_param->PARAM_SRS_PURESOUND_OUTPUT_GAIN;
        mapi_interface::Get_mapi_audio()->ADVSND_SetParam(SRS_PURESOUND_OUTPUT_GAIN_, advsnd_param->PARAM_SRS_PURESOUND_OUTPUT_GAIN, 0);
        return_tmp = RETURN_OK;
        break;

    default:
        return_tmp = RETURN_NOTOK;
        break;
    }
    return return_tmp;
}


//-------------------------------------------------------------------------------------------------
/// Get Advance Sound Effect Parameter.
/// @param advsnd_param_type     \b IN: Advacne Sound Parameter Type
/// @return ADVBSND_PARAMETER:
//-------------------------------------------------------------------------------------------------
MAPI_U16  MSrv_SSSound::GetAdvancedSoundEffect(ADVSND_PARAM_TYPE advsnd_param_type)
{
    MAPI_U16 rtn_value = 0;

    switch(advsnd_param_type)
    {
    case ADVSND_DOLBY_PL2VDPK_SMOD:
        rtn_value = m_ADVSND_DOLBY_PL2VDPK_SMOD;
        break;

    case ADVSND_DOLBY_PL2VDPK_WMOD:
        rtn_value = m_ADVSND_DOLBY_PL2VDPK_WMOD;
        break;

    case ADVSND_SRS_TSXT_SET_INPUT_GAIN:
        rtn_value = m_ADVSND_SRS_TSXT_SET_INPUT_GAIN;
        break;

    case ADVSND_SRS_TSXT_SET_DC_GAIN:
        rtn_value = m_ADVSND_SRS_TSXT_SET_DC_GAIN;
        break;

    case ADVSND_SRS_TSXT_SET_TRUBASS_GAIN:
        rtn_value = m_ADVSND_SRS_TSXT_SET_TRUBASS_GAIN;
        break;

    case ADVSND_SRS_TSXT_SET_SPEAKERSIZE:
        rtn_value = m_ADVSND_SRS_TSXT_SET_SPEAKERSIZE;
        break;

    case ADVSND_SRS_TSXT_SET_INPUT_MODE:
        rtn_value = m_ADVSND_SRS_TSXT_SET_INPUT_MODE;
        break;

    case ADVSND_SRS_TSXT_SET_OUTPUT_GAIN:
        rtn_value = m_ADVSND_SRS_TSXT_SET_OUTPUT_GAIN;
        break;

        //SRS TSHD Sub-Process Setting Parameter
    case ADVSND_SRS_TSHD_SET_INPUT_MODE:
        rtn_value = m_ADVSND_SRS_TSHD_SET_INPUT_MODE;
        break;

    case ADVSND_SRS_TSHD_SET_OUTPUT_MODE:
        rtn_value = m_ADVSND_SRS_TSHD_SET_OUTPUT_MODE;
        break;

    case ADVSND_SRS_TSHD_SET_SPEAKERSIZE:
        rtn_value = m_ADVSND_SRS_TSHD_SET_SPEAKERSIZE;
        break;

    case ADVSND_SRS_TSHD_SET_TRUBASS_CONTROL:
        rtn_value = m_ADVSND_SRS_TSHD_SET_TRUBASS_CONTROL;
        break;

    case ADVSND_SRS_TSHD_SET_DEFINITION_CONTROL:
        rtn_value = m_ADVSND_SRS_TSHD_SET_DEFINITION_CONTROL;
        break;

    case ADVSND_SRS_TSHD_SET_DC_CONTROL:
        rtn_value = m_ADVSND_SRS_TSHD_SET_DC_CONTROL;
        break;

    case ADVSND_SRS_TSHD_SET_SURROUND_LEVEL:
        rtn_value = m_ADVSND_SRS_TSHD_SET_SURROUND_LEVEL;
        break;

    case ADVSND_SRS_TSHD_SET_INPUT_GAIN:
        rtn_value = m_ADVSND_SRS_TSHD_SET_INPUT_GAIN;
        break;

    case ADVSND_SRS_TSHD_SET_WOWSPACE_CONTROL:
        rtn_value = m_ADVSND_SRS_TSHD_SET_WOWSPACE_CONTROL;
        break;

    case ADVSND_SRS_TSHD_SET_WOWCENTER_CONTROL:
        rtn_value = m_ADVSND_SRS_TSHD_SET_WOWCENTER_CONTROL;
        break;

    case ADVSND_SRS_TSHD_SET_WOWHDSRS3DMODE:
        rtn_value = m_ADVSND_SRS_TSHD_SET_WOWHDSRS3DMODE;
        break;

    case ADVSND_SRS_TSHD_SET_LIMITERCONTROL:
        rtn_value = m_ADVSND_SRS_TSHD_SET_LIMITERCONTROL;
        break;

    case ADVSND_SRS_TSHD_SET_OUTPUT_GAIN:
        rtn_value = m_ADVSND_SRS_TSHD_SET_OUTPUT_GAIN;
        break;

        //SRS_THEATERSOUND Sub-Process Setting Parameter
    case ADVSND_SRS_THEATERSOUND_INPUT_GAIN:
        rtn_value = m_ADVSND_SRS_THEATERSOUND_INPUT_GAIN;
        break;

    case ADVSND_SRS_THEATERSOUND_DEFINITION_CONTROL:
        rtn_value = m_ADVSND_SRS_THEATERSOUND_DEFINITION_CONTROL;
        break;

    case ADVSND_SRS_THEATERSOUND_DC_CONTROL:
        rtn_value = m_ADVSND_SRS_THEATERSOUND_DC_CONTROL;
        break;

    case ADVSND_SRS_THEATERSOUND_TRUBASS_CONTROL:
        rtn_value = m_ADVSND_SRS_THEATERSOUND_TRUBASS_CONTROL;
        break;

    case ADVSND_SRS_THEATERSOUND_SPEAKERSIZE:
        rtn_value = m_ADVSND_SRS_THEATERSOUND_SPEAKERSIZE;
        break;

    case ADVSND_SRS_THEATERSOUND_HARDLIMITER_LEVEL:
        rtn_value = m_ADVSND_SRS_THEATERSOUND_HARDLIMITER_LEVEL;
        break;

    case ADVSND_SRS_THEATERSOUND_HARDLIMITER_BOOST_GAIN:
        rtn_value = m_ADVSND_SRS_THEATERSOUND_HARDLIMITER_BOOST_GAIN;
        break;

    case ADVSND_SRS_THEATERSOUND_HEADROOM_GAIN:
        rtn_value = m_ADVSND_SRS_THEATERSOUND_HEADROOM_GAIN;
        break;

    case ADVSND_SRS_THEATERSOUND_TRUVOLUME_MODE:
        rtn_value = m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_MODE;
        break;

    case ADVSND_SRS_THEATERSOUND_TRUVOLUME_REF_LEVEL:
        rtn_value = m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_REF_LEVEL;
        break;

    case ADVSND_SRS_THEATERSOUND_TRUVOLUME_MAX_GAIN:
        rtn_value = m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_MAX_GAIN;
        break;

    case ADVSND_SRS_THEATERSOUND_TRUVOLUME_NOISE_MNGR_THLD:
        rtn_value = m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_NOISE_MNGR_THLD;
        break;

    case ADVSND_SRS_THEATERSOUND_TRUVOLUME_CALIBRATE:
        rtn_value = m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_CALIBRATE;
        break;

    case ADVSND_SRS_THEATERSOUND_TRUVOLUME_INPUT_GAIN:
        rtn_value = m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_INPUT_GAIN;
        break;

    case ADVSND_SRS_THEATERSOUND_TRUVOLUME_OUTPUT_GAIN:
        rtn_value = m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_OUTPUT_GAIN;
        break;

    case ADVSND_SRS_THEATERSOUND_HPF_FC:
        rtn_value = m_ADVSND_SRS_THEATERSOUND_HPF_FC;
        break;

        //DTS_ULTRATV Sub-Process Setting Parameter
    case ADVSND_DTS_ULTRATV_EVO_MONOINPUT:
        rtn_value = m_ADVSND_DTS_ULTRATV_EVO_MONOINPUT;
        break;

    case ADVSND_DTS_ULTRATV_EVO_WIDENINGON:
        rtn_value = m_ADVSND_DTS_ULTRATV_EVO_WIDENINGON;
        break;

    case ADVSND_DTS_ULTRATV_EVO_ADD3DBON:
        rtn_value = m_ADVSND_DTS_ULTRATV_EVO_ADD3DBON;
        break;

    case ADVSND_DTS_ULTRATV_EVO_PCELEVEL:
        rtn_value = m_ADVSND_DTS_ULTRATV_EVO_PCELEVEL;
        break;

    case ADVSND_DTS_ULTRATV_EVO_VLFELEVEL:
        rtn_value = m_ADVSND_DTS_ULTRATV_EVO_VLFELEVEL;
        break;

    case ADVSND_DTS_ULTRATV_SYM_DEFAULT:
        rtn_value = m_ADVSND_DTS_ULTRATV_SYM_DEFAULT;
        break;

    case ADVSND_DTS_ULTRATV_SYM_MODE:
        rtn_value = m_ADVSND_DTS_ULTRATV_SYM_MODE;
        break;

    case ADVSND_DTS_ULTRATV_SYM_LEVEL:
        rtn_value = m_ADVSND_DTS_ULTRATV_SYM_LEVEL;
        break;

    case ADVSND_DTS_ULTRATV_SYM_RESET:
        rtn_value = m_ADVSND_DTS_ULTRATV_SYM_RESET;
        break;

        //AUDYSSEY Sub-Process Setting Parameter
    case ADVSND_AUDYSSEY_DYNAMICVOL_COMPRESS_MODE:
        rtn_value = m_ADVSND_AUDYSSEY_DYNAMICVOL_COMPRESS_MODE;
        break;

    case ADVSND_AUDYSSEY_DYNAMICVOL_GC:
        rtn_value = m_ADVSND_AUDYSSEY_DYNAMICVOL_GC;
        break;

    case ADVSND_AUDYSSEY_DYNAMICVOL_VOLSETTING:
        rtn_value = m_ADVSND_AUDYSSEY_DYNAMICVOL_VOLSETTING;
        break;

    case ADVSND_AUDYSSEY_DYNAMICEQ_EQOFFSET:
        rtn_value = m_ADVSND_AUDYSSEY_DYNAMICEQ_EQOFFSET;
        break;

    case ADVSND_AUDYSSEY_ABX_GWET:
        rtn_value = m_ADVSND_AUDYSSEY_ABX_GWET;
        break;

    case ADVSND_AUDYSSEY_ABX_GDRY:
        rtn_value = m_ADVSND_AUDYSSEY_ABX_GDRY;
        break;

    case ADVSND_AUDYSSEY_ABX_FILSET:
        rtn_value = m_ADVSND_AUDYSSEY_ABX_FILSET;
        break;

    case ADVSND_SRS_PURESOUND_HL_INPUT_GAIN:
        rtn_value = m_ADVSND_SRS_PURESOUND_HL_INPUT_GAIN;
        break;

    case ADVSND_SRS_PURESOUND_HL_OUTPUT_GAIN:
        rtn_value = m_ADVSND_SRS_PURESOUND_HL_OUTPUT_GAIN;
        break;

    case ADVSND_SRS_PURESOUND_HL_BYPASS_GAIN:
        rtn_value = m_ADVSND_SRS_PURESOUND_HL_BYPASS_GAIN;
        break;

    case ADVSND_SRS_PURESOUND_HL_LIMITERBOOST:
        rtn_value = m_ADVSND_SRS_PURESOUND_HL_LIMITERBOOST;
        break;

    case ADVSND_SRS_PURESOUND_HL_HARDLIMIT:
        rtn_value = m_ADVSND_SRS_PURESOUND_HL_HARDLIMIT;
        break;

    case ADVSND_SRS_PURESOUND_HL_DELAYLEN:
        rtn_value = m_ADVSND_SRS_PURESOUND_HL_DELAYLEN;
        break;

    case ADVSND_SRS_PURESOUND_AEQ_INPUT_GAIN:
        rtn_value = m_ADVSND_SRS_PURESOUND_AEQ_INPUT_GAIN;
        break;

    case ADVSND_SRS_PURESOUND_AEQ_OUTPUT_GAIN:
        rtn_value = m_ADVSND_SRS_PURESOUND_AEQ_OUTPUT_GAIN;
        break;

    case ADVSND_SRS_PURESOUND_AEQ_BYPASS_GAIN:
        rtn_value = m_ADVSND_SRS_PURESOUND_AEQ_BYPASS_GAIN;
        break;

    case ADVSND_SRS_PURESOUND_HPF_FREQUENCY:
        rtn_value = m_ADVSND_SRS_PURESOUND_HPF_FREQUENCY;
        break;

    case ADVSND_SRS_PURESOUND_TBHD_TRUBASS_LEVEL:
        rtn_value = m_ADVSND_SRS_PURESOUND_TBHD_TRUBASS_LEVEL;
        break;

    case ADVSND_SRS_PURESOUND_TBHD_SPEAKER_SIZE:
        rtn_value = m_ADVSND_SRS_PURESOUND_TBHD_SPEAKER_SIZE;
        break;

    case ADVSND_SRS_PURESOUND_TBHD_LEVEL_INDEPENDENT_EN:
        rtn_value = m_ADVSND_SRS_PURESOUND_TBHD_LEVEL_INDEPENDENT_EN;
        break;

    case ADVSND_SRS_PURESOUND_TBHD_COMPRESSOR_LEVEL:
        rtn_value = m_ADVSND_SRS_PURESOUND_TBHD_COMPRESSOR_LEVEL;
        break;

    case ADVSND_SRS_PURESOUND_TBHD_MODE:
        rtn_value = m_ADVSND_SRS_PURESOUND_TBHD_MODE;
        break;

    case ADVSND_SRS_PURESOUND_TBHD_SPEAKER_AUDIO:
        rtn_value = m_ADVSND_SRS_PURESOUND_TBHD_SPEAKER_AUDIO;
        break;

    case ADVSND_SRS_PURESOUND_TBHD_SPEAKER_ANALYSIS:
        rtn_value = m_ADVSND_SRS_PURESOUND_TBHD_SPEAKER_ANALYSIS;
        break;

    case ADVSND_SRS_PURESOUND_INPUT_GAIN:
        rtn_value = m_ADVSND_SRS_PURESOUND_INPUT_GAIN;
        break;

    case ADVSND_SRS_PURESOUND_OUTPUT_GAIN:
        rtn_value = m_ADVSND_SRS_PURESOUND_OUTPUT_GAIN;
        break;


    default:
        rtn_value = 0;
        break;
    }
    return rtn_value;
}

//-------------------------------------------------------------------------------------------------
/// Set Audio Output.
/// @param type   \b IN: AUDIO_OUT_TYPE
/// @param *audout_param    \b IN: Parameter Pointer
/// @return MSRV_SSSND_RET: RETURN_OK, RETURN_NOTOK, RETURN_NOTSUPPORT
//-------------------------------------------------------------------------------------------------
MSRV_SSSND_RET  MSrv_SSSound::SetAudioOutput(AUDIO_OUT_TYPE type, AUDIO_OUT_PARAMETER *audout_param)
{
    MSRV_SSSND_RET return_tmp;

    switch(type)
    {
    case DELAY_SPEAKER:
        mapi_interface::Get_mapi_audio()->SND_Speaker_SetBufferProcess(audout_param->SPEAKER_DELAY_TIME);
        return_tmp = RETURN_OK;
        break;

    case DELAY_SPDIF:
        mapi_interface::Get_mapi_audio()->SPDIF_SetBufferProcess(audout_param->SPDIF_DELAY_TIME);
        return_tmp = RETURN_OK;
        break;

    case DIGITALOUT_SPDIF_UI_CONFIG:
        mapi_interface::Get_mapi_audio()->SPDIF_UI_SetMode(audout_param->SPDIF_OUTMOD_IN_UI);
        return_tmp = RETURN_OK;
        break;

    case DIGITALOUT_SPDIF_COMMON_CONFIG:
        mapi_interface::Get_mapi_audio()->SPDIF_SetMode(audout_param->SPDIF_OUTMOD_ACTIVE);
        return_tmp = RETURN_OK;
        break;


    default:
        return_tmp = RETURN_NOTOK;
        break;
    }
    return return_tmp;
}


//-------------------------------------------------------------------------------------------------
/// Set Audio InputSource.
/// @param eInputSrc    \b IN: MAPI_INPUT_SOURCE_TYPE Pointer
/// @return MSRV_SSSND_RET: RETURN_OK
//-------------------------------------------------------------------------------------------------
MSRV_SSSND_RET  MSrv_SSSound::SetInputSource(MAPI_INPUT_SOURCE_TYPE eInputSrc)
{
    mapi_interface::Get_mapi_audio()->InputSource_ChangeAudioSource(eInputSrc);

    MAPI_INPUT_SOURCE_TYPE enCurrentInputType;
    enCurrentInputType = MSrv_Control::GetInstance()->GetCurrentInputSource();
    switch(enCurrentInputType)
    {
    case MAPI_INPUT_SOURCE_DTV:
        spkr_prescale = PrescaleTable.spkr_prescale_DTV;
        hp_prescale = PrescaleTable.spkr_prescale_DTV;
        sc1_prescale = PrescaleTable.sc1_prescale_DTV;
        sc2_prescale = PrescaleTable.sc2_prescale_DTV;
        spdif_prescale = 0;;
    case MAPI_INPUT_SOURCE_ATV:
        spkr_prescale = PrescaleTable.spkr_prescale_ATV;
        hp_prescale = PrescaleTable.spkr_prescale_ATV;
        sc1_prescale = PrescaleTable.sc1_prescale_ATV;
        sc2_prescale = PrescaleTable.sc2_prescale_ATV;
        spdif_prescale = 0;;
        break;
    case MAPI_INPUT_SOURCE_CVBS:
    case MAPI_INPUT_SOURCE_CVBS2:
    case MAPI_INPUT_SOURCE_SVIDEO:
    case MAPI_INPUT_SOURCE_SCART:
    case MAPI_INPUT_SOURCE_SCART2:
    case MAPI_INPUT_SOURCE_YPBPR:
    case MAPI_INPUT_SOURCE_YPBPR2:
        spkr_prescale = PrescaleTable.spkr_prescale_AV;
        hp_prescale = PrescaleTable.spkr_prescale_AV;
        sc1_prescale = PrescaleTable.sc1_prescale_AV;
        sc2_prescale = PrescaleTable.sc2_prescale_AV;
        spdif_prescale = 0;;
        break;
    case MAPI_INPUT_SOURCE_VGA:
    case MAPI_INPUT_SOURCE_DVI:
        spkr_prescale = PrescaleTable.spkr_prescale_DVI;
        hp_prescale = PrescaleTable.spkr_prescale_DVI;
        sc1_prescale = PrescaleTable.sc1_prescale_DVI;
        sc2_prescale = PrescaleTable.sc2_prescale_DVI;
        spdif_prescale = 0;;
        break;
    case MAPI_INPUT_SOURCE_HDMI:
    case MAPI_INPUT_SOURCE_HDMI2:
    case MAPI_INPUT_SOURCE_HDMI3:
    case MAPI_INPUT_SOURCE_HDMI4:
        spkr_prescale = PrescaleTable.spkr_prescale_HDMI;
        hp_prescale = PrescaleTable.spkr_prescale_HDMI;
        sc1_prescale = PrescaleTable.sc1_prescale_HDMI;
        sc2_prescale = PrescaleTable.sc2_prescale_HDMI;
        spdif_prescale = 0;;
        break;
    case MAPI_INPUT_SOURCE_STORAGE:
    case MAPI_INPUT_SOURCE_KTV:
        spkr_prescale = PrescaleTable.spkr_prescale_MM;
        hp_prescale = PrescaleTable.spkr_prescale_MM;
        sc1_prescale = PrescaleTable.sc1_prescale_MM;
        sc2_prescale = PrescaleTable.sc2_prescale_MM;
        spdif_prescale = 0;;
        break;
    default:
        break;
    }

    return RETURN_OK;
}

//-------------------------------------------------------------------------------------------------
/// Select Main/Sub/Scart  source
/// @param eInputSrc    \b IN: MAPI_INPUT_SOURCE_TYPE Pointer
/// @param eType    \b IN: For Main /SUB / SCART
/// @return MSRV_SSSND_RET: RETURN_OK
//-------------------------------------------------------------------------------------------------
MSRV_SSSND_RET  MSrv_SSSound::SetAudioSource(MAPI_INPUT_SOURCE_TYPE eInputSrc, MSRV_AUDIO_PROCESSOR_TYPE eType)
{
    switch(eType)
    {
    case MSRV_AUDIO_PROCESSOR_MAIN:
        mapi_interface::Get_mapi_audio()->InputSource_ChangeAudioSource(eInputSrc, AUDIO_PROCESSOR_MAIN);
        break;

    case MSRV_AUDIO_PROCESSOR_SUB:
        //Force to set the Sub volume the same as the main's when the sub input source is none.
        if(eInputSrc == MAPI_INPUT_SOURCE_NONE)
        {
            MS_SND_NLA_POINT stNLCruve1;
            MS_NLA_POINT stNLCruve2;
            MAPI_U8 u8HPVolume = 0;
            
            const AudioPath_t* const p_AudioPath = SystemInfo::GetInstance()->GetAudioPathInfo();
            MS_USER_SOUND_SETTING stAudioSetting;
            MSrv_Control::GetMSrvSystemDatabase()->GetAudioSetting(&stAudioSetting);
            u8HPVolume = stAudioSetting.Volume;

            MSrv_Control::GetMSrvSystemDatabase()->GetNLASetting(&stNLCruve2, EN_NLA_VOLUME, MSrv_Control::GetInstance()->GetCurrentInputSource());
            _TransNLAPointToSNDNLAPoint(stNLCruve2, stNLCruve1);
            u8HPVolume = Sound_NonLinearCalculate_100(&stNLCruve1, u8HPVolume);
            mapi_interface::Get_mapi_audio()->SetAbsoluteVolume(p_AudioPath[MAPI_AUDIO_PATH_HP].u32Path, u8HPVolume, 0);
            printf("The Sub input source is none,so force to set the sub volume the same as the main's !\n");
        }
        
        mapi_interface::Get_mapi_audio()->InputSource_ChangeAudioSource(eInputSrc, AUDIO_PROCESSOR_SUB);
        break;

    case MSRV_AUDIO_PROCESSOR_SCART:
        mapi_interface::Get_mapi_audio()->InputSource_ChangeAudioSource(eInputSrc, AUDIO_PROCESSOR_SCART);
        break;

    default:
        break;
    }

    return RETURN_OK;
}


//-------------------------------------------------------------------------------------------------
///  Set Primary Audio Language
///
/// @code
///  SetAudioLanguage1(int);
/// @endcode
/// @param enLanguage    \b IN: Language Index
/// @return MSRV_SSSND_RET: RETURN_OK
//-------------------------------------------------------------------------------------------------
MSRV_SSSND_RET  MSrv_SSSound::SetAudioLanguage1(int enLanguage)
{
    MSrv_Control::GetMSrvSystemDatabase()->SetAudioLanguage1((MEMBER_LANGUAGE *)&enLanguage);
    return RETURN_OK;
}

//-------------------------------------------------------------------------------------------------
///  Set Secondary Audio Language
///
/// @code
///  SetAudioLanguage2(int);
/// @endcode
/// @param enLanguage    \b IN: Language Index
/// @return MSRV_SSSND_RET: RETURN_OK
//-------------------------------------------------------------------------------------------------
MSRV_SSSND_RET  MSrv_SSSound::SetAudioLanguage2(int enLanguage)
{
    MSrv_Control::GetMSrvSystemDatabase()->SetAudioLanguage2((MEMBER_LANGUAGE *)&enLanguage);
    return RETURN_OK;
}


 //-------------------------------------------------------------------------------------------------
///  Get Primary Audio Language
///
/// @code
///  GetAudioLanguage1();
/// @endcode
/// @return int: audio language 1
//-------------------------------------------------------------------------------------------------
int  MSrv_SSSound::GetAudioLanguage1()
{
    MEMBER_LANGUAGE lang;
    MSrv_Control::GetMSrvSystemDatabase()->GetAudioLanguage1(&lang);
    return lang;
}

//-------------------------------------------------------------------------------------------------
///  Get Secondary Audio Language
///
/// @code
///  GetAudioLanguage2();
/// @endcode
/// @return int:audo language2
//-------------------------------------------------------------------------------------------------
int  MSrv_SSSound::GetAudioLanguage2()
{
    MEMBER_LANGUAGE lang;
    MSrv_Control::GetMSrvSystemDatabase()->GetAudioLanguage2(&lang);
    return lang;
}



static SDK_AUDIO_CAPTURE_DEVICE_TYPE  _MSrvAudioDeviceTypeToSDKAudioDeviceType(MSRV_AUDIO_CAPTURE_DEVICE_TYPE eAudioDeviceType)
{
    SDK_AUDIO_CAPTURE_DEVICE_TYPE enRetSDKAudioDeviceType = CAPTURE_DEVICE_TYPE_DEVICE0;

    switch (eAudioDeviceType)
    {
    case MSRV_CAPTURE_DEVICE_TYPE_DEVICE0:
        enRetSDKAudioDeviceType = CAPTURE_DEVICE_TYPE_DEVICE0;
        break;

    case MSRV_CAPTURE_DEVICE_TYPE_DEVICE1:
        enRetSDKAudioDeviceType = CAPTURE_DEVICE_TYPE_DEVICE1;
        break;

    case MSRV_CAPTURE_DEVICE_TYPE_DEVICE2:
        enRetSDKAudioDeviceType = CAPTURE_DEVICE_TYPE_DEVICE0;
        break;

    case MSRV_CAPTURE_DEVICE_TYPE_DEVICE3:
        enRetSDKAudioDeviceType = CAPTURE_DEVICE_TYPE_DEVICE0;
        break;

    case MSRV_CAPTURE_DEVICE_TYPE_DEVICE4:
        enRetSDKAudioDeviceType = CAPTURE_DEVICE_TYPE_DEVICE0;
        break;

    case MSRV_CAPTURE_DEVICE_TYPE_DEVICE5:
        enRetSDKAudioDeviceType = CAPTURE_DEVICE_TYPE_DEVICE0;
        break;

    default:
        ASSERT(0);
        break;
    }

    return enRetSDKAudioDeviceType;
}

//-------------------------------------------------------------------------------------------------
/// Select audio source for data capture
/// @brief  \b Function  \b Description: Set Audio data Capture Source
/// @param  eSource    \b : data source type
/// @return \b MSRV_SSSND_RET    : RETURN_OK(success)/RETURN_NOTOK(fail)
//-------------------------------------------------------------------------------------------------
MSRV_SSSND_RET  MSrv_SSSound::SetAudioCaptureSource(MSRV_AUDIO_CAPTURE_DEVICE_TYPE eAudioDeviceType, MSRV_AUDIO_CAPTURE_SOURCE eSource)
{
    MSRV_SSSND_RET   bReturnValue=RETURN_OK;

    if(eAudioDeviceType >= MSRV_CAPTURE_DEVICE_TYPE_DEVICE2)
    {
        return RETURN_NOTOK;
    }

    if(eSource >= MSRV_CAPTURE_SOURCE_MAX)
    {
        return RETURN_NOTOK;
    }

    switch(eSource)
    {
    case MSRV_CAPTURE_MAIN_SOUND:
        mapi_interface::Get_mapi_audio()->SetDataCaptueSource(_MSrvAudioDeviceTypeToSDKAudioDeviceType(eAudioDeviceType), CAPTURE_SOURCE_MAIN_SOUND_);
        break;
    case MSRV_CAPTURE_SUB_SOUND:
        mapi_interface::Get_mapi_audio()->SetDataCaptueSource(_MSrvAudioDeviceTypeToSDKAudioDeviceType(eAudioDeviceType), CAPTURE_SOURCE_SUB_SOUND_);
        break;
    case MSRV_CAPTURE_MICROPHONE_SOUND:
        mapi_interface::Get_mapi_audio()->SetDataCaptueSource(_MSrvAudioDeviceTypeToSDKAudioDeviceType(eAudioDeviceType), CAPTURE_SOURCE_MICROPHONE_SOUND_);
        break;
    case MSRV_CAPTURE_MIXED_SOUND:
        mapi_interface::Get_mapi_audio()->SetDataCaptueSource(_MSrvAudioDeviceTypeToSDKAudioDeviceType(eAudioDeviceType), CAPTURE_SOURCE_MIXED_SOUND_);
        break;
    case MSRV_CAPTURE_USER_DEFINE1:
        mapi_interface::Get_mapi_audio()->SetDataCaptueSource(_MSrvAudioDeviceTypeToSDKAudioDeviceType(eAudioDeviceType), CAPTURE_SOURCE_USER_DEFINE1_);
        break;
    case MSRV_CAPTURE_USER_DEFINE2:
        mapi_interface::Get_mapi_audio()->SetDataCaptueSource(_MSrvAudioDeviceTypeToSDKAudioDeviceType(eAudioDeviceType), CAPTURE_SOURCE_USER_DEFINE2_);
        break;
    default:
        break;
    }

    return bReturnValue;
}

//-------------------------------------------------------------------------------------------------
	/// @brief \b Function \b Name: SetOutputSourceInfo(AUDIO_VOL_SOURCE_TYPE eAudioPath, MSRV_AUDIO_CAPTURE_SOURCE eSource)
	/// @brief \b Function \b Description: Get audio output port source information
	/// @param eAudioPath  \b	:	audio sourch path
	/// @param eSource        \b   : audio output port source information
	/// @return
	//-------------------------------------------------------------------------------------------------
MSRV_SSSND_RET MSrv_SSSound::SetOutputSourceInfo(AUDIO_VOL_SOURCE_TYPE eAudioPath, MSRV_AUDIO_PROCESSOR_TYPE eSource)
{
	MSRV_SSSND_RET   bReturnValue=RETURN_OK;

	if(eAudioPath > VOL_SOURCE_SPDIF_OUT)
	{
		return RETURN_NOTOK;
	}

	if(eSource >= MSRV_AUDIO_PROCESSOR_MAX)
    {
        return RETURN_NOTOK;
    }

	MAPI_AUDIO_PROCESSOR_TYPE eSouceType = AUDIO_PROCESSOR_MAX;

	MSAPI_AUDIO_OUTPORT_SOURCE_INFO pSourceInfo;

	mapi_interface::Get_mapi_audio()->GetOutputSourceInfo(&pSourceInfo);


	switch (eSource)
	{
		case MSRV_AUDIO_PROCESSOR_MAIN :
			eSouceType = AUDIO_PROCESSOR_MAIN;
		break;


		case MSRV_AUDIO_PROCESSOR_SUB:
			eSouceType = AUDIO_PROCESSOR_SUB;

		break;

		case MSRV_AUDIO_PROCESSOR_SCART :
			eSouceType = AUDIO_PROCESSOR_SCART;
		break;

		default :
			break;
	}



	switch(eAudioPath)
	{
		case VOL_SOURCE_SPEAKER_OUT :

			pSourceInfo.SpeakerOut = eSouceType ;

		break;
		case VOL_SOURCE_HP_OUT :

			pSourceInfo.HpOut = eSouceType ;

		break;

		case VOL_SOURCE_LINE_OUT :

			pSourceInfo.MonitorOut = eSouceType ;

		break;

		case VOL_SOURCE_SCART1_OUT :

			pSourceInfo.ScartOut = eSouceType ;

		break;

		case VOL_SOURCE_SCART2_OUT :

			pSourceInfo.ScartOut = eSouceType ;

		break;

		case VOL_SOURCE_SPDIF_OUT :

			pSourceInfo.SpdifOut = eSouceType ;

		break;

		default :
			break;
	}

	mapi_interface::Get_mapi_audio()->SetOutputSourceInfo(&pSourceInfo);

	return bReturnValue;
}


//-------------------------------------------------------------------------------------------------
/// Get Audio InputSource.
/// @return MSRV_SSSND_RET: RETURN_OK
//-------------------------------------------------------------------------------------------------
MAPI_INPUT_SOURCE_TYPE MSrv_SSSound::GetInputSource(void)
{
    return mapi_interface::Get_mapi_audio()->SND_GetAudioInputSource();
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: GetMute() .
/// @brief \b Function \b Description: Get the audio mute status by typing status type
/// @param <IN>        \b mute_status_type   :
/// @return <OUT>      \b True: mute on, False: mute off
//-------------------------------------------------------------------------------------------------
MAPI_BOOL MSrv_SSSound::GetMute(const MAPI_SOUND_MUTE_STATUS_TYPE mute_status_type)
{
    return GetMuteStatus(mute_status_type);
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SetMute() .
/// @brief \b Function \b Description: Set audio mute status
/// @param <IN>        \b muteType: mute type
///                    \b onOff: 1 is on, 0 is Off
/// @return <OUT>      \b SSSOUND_OK: Success
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::SetMute(SSSOUND_MUTE_TYPE muteType, MAPI_BOOL onOff)
{
    return SetMuteStatus(muteType, onOff);
}


//-------------------------------------------------------------------------------------------------
/// ATV related interface function is used to Java kind of sound property.
//-------------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------------
/// Set ATV Info .
/// @param infotype \b IN: ATV Info
/// @param Info_config1 \b IN: ATV Info CFG
/// @return MSRV_SSSND_RET: RETURN_OK
//-------------------------------------------------------------------------------------------------
MSRV_SSSND_RET MSrv_SSSound::SetATVInfo(ATV_INFO_TYPE infotype, ATV_INFO_MODE Info_mode)
{
    MSRV_SSSND_RET return_tmp;
    //switch(infotype)
    {
        //case ATV_HIDEV_INFO:
        mapi_interface::Get_mapi_audio()->SetHidevMode((Sound_Hidev_mode)Info_mode);
        Current_Hidev_Mode = (Sound_Hidev_mode)Info_mode;
        return_tmp = RETURN_OK;
        //break;
        //default:
        //return_tmp = RETURN_NOTOK;
        //break;
    }
    return return_tmp;
}

//-------------------------------------------------------------------------------------------------
/// Get ATV Info .
/// @param infotype \b IN: ATV Info
/// @return MSRV_SSSND_RET: RETURN_OK
//-------------------------------------------------------------------------------------------------
ATV_INFO_MODE MSrv_SSSound::GetATVInfo(ATV_INFO_TYPE infotype)
{
    ATV_INFO_MODE get_Info_mode;

    //switch(infotype)
    //{
    //case ATV_HIDEV_INFO:
    //default:
    get_Info_mode = (ATV_INFO_MODE)Current_Hidev_Mode;
    //break;
    //}
    return get_Info_mode;
}


//-------------------------------------------------------------------------------------------------
/// Get ATV sound system .
/// @return ATVInfo
//-------------------------------------------------------------------------------------------------
MAPI_U8 MSrv_SSSound::GetATVSoundSystem(void)
{
    AUDIOSTANDARD_TYPE_ eAudioStandard;

    eAudioStandard = mapi_interface::Get_mapi_audio()->SIF_GetAudioStandard();
    switch(mapi_interface::Get_mapi_audio()->SIF_ConvertToBasicAudioStandard(eAudioStandard))
    {
    case E_AUDIOSTANDARD_BG_:
        return (MAPI_U8) ATV_SYSTEM_STANDARDS_BG;
        break;

    case E_AUDIOSTANDARD_I_:
        return (MAPI_U8) ATV_SYSTEM_STANDARDS_I;
        break;

    case E_AUDIOSTANDARD_DK_:
        return (MAPI_U8) ATV_SYSTEM_STANDARDS_DK;
        break;

    case E_AUDIOSTANDARD_L_:
        return (MAPI_U8) ATV_SYSTEM_STANDARDS_L;
        break;

    case E_AUDIOSTANDARD_M_:
        return (MAPI_U8) ATV_SYSTEM_STANDARDS_M;
        break;

    default:
        return 0;
        break;
    }
}

//-------------------------------------------------------------------------------------------------
/// Set ATV sound system .
/// @param system_standard     \b IN: ATV system standard info
/// @return ATVInfo
//-------------------------------------------------------------------------------------------------
MSRV_SSSND_RET MSrv_SSSound::SetATVSoundSystem(ATV_SYSTEM_STANDARDS standard)
{
    U16 u16IFFreqKHz = 0;
    mapi_tuner *m_pTuner = mapi_interface::Get_mapi_pcb()->GetAtvTuner(0);
    ASSERT(m_pTuner);
    mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard((AUDIOSTANDARD_TYPE_)standard);

    //to set VIF
    AUDIOSTANDARD_TYPE_ eAudioStandard;
    eAudioStandard = mapi_interface::Get_mapi_audio()->SIF_GetAudioStandard();
    mapi_demodulator  *pDemodulator = mapi_interface::Get_mapi_pcb()->GetAtvDemod(0);
    if(pDemodulator != NULL)
    {
        if(eAudioStandard == E_AUDIOSTANDARD_L_)
        {
            m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_L_PRIME_IF_FREQ, 0, 0, &u16IFFreqKHz);
            //printf("\r\nIFFreqKHz (SECAM LL)= %u", u16IFFreqKHz);
            pDemodulator->ATV_SetVIF_IfFreq(u16IFFreqKHz);
            usleep(10 * 1000);
        }
        else
        {
            m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_IF_FREQ, 0, 0, &u16IFFreqKHz);
            // printf("\r\nIFFreqKHz = %u", u16IFFreqKHz);
            pDemodulator->ATV_SetVIF_IfFreq(u16IFFreqKHz);
            usleep(10 * 1000);
        }
        switch(eAudioStandard)
        {
        case E_AUDIOSTANDARD_BG_:
            pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_BG_);
            break;

        case E_AUDIOSTANDARD_NOTSTANDARD_:
        case E_AUDIOSTANDARD_BG_A2_:
            pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_BG_A2_);
            break;
        case E_AUDIOSTANDARD_BG_NICAM_:
            pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_BG_NICAM_);
            break;
        case E_AUDIOSTANDARD_I_:
            pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_I_);
            break;
        case E_AUDIOSTANDARD_DK_:
            pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_DK_);
            break;
        case E_AUDIOSTANDARD_DK1_A2_:
            pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_DK1_A2_);
            break;
        case E_AUDIOSTANDARD_DK2_A2_:
            pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_DK2_A2_);
            break;
        case E_AUDIOSTANDARD_DK3_A2_:
            pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_DK3_A2_);
            break;
        case E_AUDIOSTANDARD_DK_NICAM_:
            pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_DK_NICAM_);
            break;
        case E_AUDIOSTANDARD_L_:
            pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_L_);
            break;
        case E_AUDIOSTANDARD_M_:
            pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_);
            break;
        case E_AUDIOSTANDARD_M_BTSC_:
            pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_BTSC_);
            break;
        case E_AUDIOSTANDARD_M_A2_:
            pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_A2_);
            break;
        case E_AUDIOSTANDARD_M_EIA_J_:
            pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_EIA_J_);
            break;

        default:
            break;
        }
    }
    else
    {
        printf("\r\n============[%s:pdemodultor is null]===\r\n",__FUNCTION__);
    }

    return RETURN_OK;
}


//-------------------------------------------------------------------------------------------------
/// Check ATV sound system .
/// @return ATVInfo
//-------------------------------------------------------------------------------------------------
MSRV_SSSND_RET MSrv_SSSound::CheckATVSoundSystem(void)
{

    mapi_interface::Get_mapi_audio()->SIF_Monitor_Service();
    return RETURN_OK;
}

//-------------------------------------------------------------------------------------------------
/// Get the available ATV MTS mode .
/// @param mode     \b IN: ATV MTS Info
/// @return <OUT>      \b MTS mode (Mono/Stereo/Dual)
//-------------------------------------------------------------------------------------------------
MSRV_SSSND_RET MSrv_SSSound::SetATVMtsMode(MAPI_U8 mode)
{
    mapi_interface::Get_mapi_audio()->SIF_SetMtsMode(mode);
    return RETURN_OK;
}

//-------------------------------------------------------------------------------------------------
/// Set to next ATV MTS Info .
/// @return MSRV_SSSND_RET: RETURN_OK
//-------------------------------------------------------------------------------------------------
MSRV_SSSND_RET MSrv_SSSound::SetToNextATVMtsMode(void)
{
    MAPI_U8 tempmode, mode;
    MAPI_U8 u8SaveMtsModes = E_AUDIOMODE_MONO_;
    MAPI_U8 i=0;

    mode = E_AUDIOMODE_MONO_;
    tempmode = mapi_interface::Get_mapi_audio()->SIF_GetSoundMode();
    u8SaveMtsModes = mapi_interface::Get_mapi_audio()->SIF_GetMtsMode();
    switch((AUDIOMODE_TYPE_)tempmode)
    {
    case E_AUDIOMODE_MONO_:
        mode = E_AUDIOMODE_MONO_;
        break;

    case E_AUDIOMODE_G_STEREO_:
    case E_AUDIOMODE_K_STEREO_:
        for(i=0;i<sizeof(Stereo);i++)
        {
            if(Stereo[i % sizeof(Stereo)] == u8SaveMtsModes)
            {
                mtscircle = i;
                break;
            }
        }
        mtscircle = mtscircle + 1;
        mode = Stereo[(mtscircle) % sizeof(Stereo)];
        break;
    case E_AUDIOMODE_DUAL_A_:
        for(i=0;i<sizeof(A2_Dual);i++)
        {

            if(A2_Dual[i % sizeof(A2_Dual)] == u8SaveMtsModes)
            {
                mtscircle = i;
                break;
            }
        }
        mtscircle = mtscircle + 1;
        mode = A2_Dual[(mtscircle) % sizeof(A2_Dual)];
        break;
    case E_AUDIOMODE_NICAM_STEREO_:
        for(i=0;i<sizeof(NICAM_Stereo);i++)
        {
            if(NICAM_Stereo[i % sizeof(NICAM_Stereo)] == u8SaveMtsModes)
            {
                mtscircle = i;
                break;
            }
        }
        mtscircle = mtscircle + 1;
        mode = NICAM_Stereo[(mtscircle) % sizeof(NICAM_Stereo)];
        break;

    case E_AUDIOMODE_NICAM_DUAL_A_:
        for(i=0;i<sizeof(NICAM_Dual);i++)
        {
            if(NICAM_Dual[i % sizeof(NICAM_Dual)] == u8SaveMtsModes)
            {
                mtscircle = i;
                break;
            }
        }
        mtscircle = mtscircle + 1;
        mode = NICAM_Dual[(mtscircle) % sizeof(NICAM_Dual)];
        break;

    case E_AUDIOMODE_NICAM_MONO_:
        for(i=0;i<sizeof(NICAM_Mono);i++)
        {
            if(NICAM_Mono[i % sizeof(NICAM_Mono)] == u8SaveMtsModes)
            {
                mtscircle = i;
                break;
            }
        }
        mtscircle = mtscircle + 1;
        mode = NICAM_Mono[(mtscircle) % sizeof(NICAM_Mono)];
        break;

    case E_AUDIOMODE_MONO_SAP_:
        for(i=0;i<sizeof(BTSC_Mono_Sap);i++)
        {
            if(BTSC_Mono_Sap[i % sizeof(BTSC_Mono_Sap)] == u8SaveMtsModes)
            {
                mtscircle = i;
                break;
            }
        }
        mtscircle = mtscircle + 1;
        mode = BTSC_Mono_Sap[(mtscircle) % sizeof(BTSC_Mono_Sap)];
        break;

    case E_AUDIOMODE_STEREO_SAP_:
        for(i=0;i<sizeof(BTSC_Stereo_Sap);i++)
        {
            if(BTSC_Stereo_Sap[i % sizeof(BTSC_Stereo_Sap)] == u8SaveMtsModes)
            {
                mtscircle = i;
                break;
            }
        }
        mtscircle = mtscircle + 1;
        mode = BTSC_Stereo_Sap[(mtscircle) % sizeof(BTSC_Stereo_Sap)];
        break;

    default:
        mode = E_AUDIOMODE_MONO_;
        break;

    }

    mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_INTERNAL_1_MUTEON_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
    mapi_interface::Get_mapi_audio()->SIF_SetMtsMode((AUDIOMODE_TYPE_)mode);
    usleep(AU_DELAY_FOR_ENTERING_MUTE * 1000);
    mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_INTERNAL_1_MUTEOFF_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);

#if((STB_ENABLE == 0)&&(MSTAR_TVOS == 1))
    if(MSrv_Control::GetInstance()->GetCurrentInputSource() == MAPI_INPUT_SOURCE_ATV)
    {
        MSrv_Control::GetMSrvAtvDatabase()->CommondCmd(SET_AUDIO_MODE , 0 , MSrv_Control::GetMSrvSSSound()->GetMtsMode() ,NULL);
    }
#endif


    return RETURN_OK;
}

//-------------------------------------------------------------------------------------------------
/// Get the available ATV MTS mode .
/// @return <OUT>      \b MTS mode (Mono/Stereo/Dual)
//-------------------------------------------------------------------------------------------------
ATV_AUDIOMODE_TYPE MSrv_SSSound::GetATVSoundMode(void)
{
    MAPI_U8 mode;
    mode = mapi_interface::Get_mapi_audio()->SIF_GetSoundMode();

    return (ATV_AUDIOMODE_TYPE) mode;
}

//-------------------------------------------------------------------------------------------------
/// Get the available ATV MTS mode .
/// @return <OUT>      \b MTS mode (Mono/Stereo/Dual)
//-------------------------------------------------------------------------------------------------
ATV_AUDIOMODE_TYPE MSrv_SSSound::GetATVMtsMode(void)
{
    MAPI_U8 mode;
    mode = mapi_interface::Get_mapi_audio()->SIF_GetMtsMode();

    return (ATV_AUDIOMODE_TYPE) mode;
}

//-------------------------------------------------------------------------------------------------
/// DTV related interface function is used to Java kind of sound property.
//-------------------------------------------------------------------------------------------------

//-------------------------------------------------------------------------------------------------
/// Set DTV / DMP(Digital MultiMedia Player) Info .
/// @param info_type        \b IN: DTV_DMP Info
/// @param dec_id           \b IN: DTV_DMP Decoder ID
/// @param Info_config1     \b IN: DTV_DMP Info config-1
/// @param Info_config2     \b IN: DTV_DMP Info config-2
/// @return MSRV_SSSND_RET: RETURN_OK
//-------------------------------------------------------------------------------------------------
MSRV_SSSND_RET  MSrv_SSSound::SetDTVInfoDMPInfo(DTV_DMP_INFO_TYPE info_type, DTV_DMP_AUDIO_DEC_ID dec_id, DTV_DMP_INFO_CFG1 *Info_config1, DTV_DMP_INFO_CFG2 *Info_config2)
{
    MSRV_SSSND_RET return_tmp;

    Info_config2->DTVDMP_CFG2_NOT_NEED = FALSE;
    switch(info_type)
    {

    case DTV_DMP_DECODER_CTRL:
        m_DTV_DMP_DECODER_CTRL_CMD = Info_config1->DTV_DMP_DECODER_COMMAND;
        if ( dec_id == DTVDMP_DEC_INVALID )
        {
            return_tmp = RETURN_NOTOK;
        }
        else
        {
            mapi_interface::Get_mapi_audio()->MMA_AudioControl((MMA_AUDIO_DEC_ID)dec_id, (MMA_AUDIO_CONTROL_)Info_config1->DTV_DMP_DECODER_COMMAND);
            return_tmp = RETURN_OK;
        }
        break;

    case DTV_DMP_DECODER_SOUNDMODE_CTRL:
        m_DTV_DECOCDER_SOUNDMODE = Info_config1->DTV_DECOCDER_SOUNDMODE;
        mapi_interface::Get_mapi_audio()->DECODER_SetOutputMode((En_DVB_soundModeType_)Info_config1->DTV_DECOCDER_SOUNDMODE);
        return_tmp = RETURN_OK;
        break;

    case DTV_DMP_INFOTYPE_MPEG:
        mapi_interface::Get_mapi_audio()->DECODER_SetMpegInfo(Info_config1->DTV_MPEG_INFO, Info_config2->DTVDMP_PARAM1, Info_config2->DTVDMP_PARAM2);
        return_tmp = RETURN_OK;
        break;

    default:
        return_tmp = RETURN_NOTOK;
        break;
    }
    return return_tmp;
}

//-------------------------------------------------------------------------------------------------
/// Set sound track(Digital MultiMedia Player) Info .
/// @param enSoundMode        \b IN: DTV Audio Sound Mode (LL, RR, LR...)
/// @return MSRV_SSSND_RET: RETURN_OK
//-------------------------------------------------------------------------------------------------
MSRV_SSSND_RET  MSrv_SSSound::SetKTVSoundTrack(KTV_AUDIO_MPEG_SOUNDMODE enSoundMode)
{
    MSRV_SSSND_RET return_tmp;

    mapi_interface::Get_mapi_audio()->DECODER_SetMpegInfo(Audio_MPEG_infoType_SoundMode_, (U32)enSoundMode, 0);

    return_tmp = RETURN_OK;

    return return_tmp;
}

//-------------------------------------------------------------------------------------------------
/// Get main speaker output to Stereo/LL/RR/Mixed
/// @return En_DVB_soundModeType_: output mode Stereo: 0, LL: 1, RR: 2, Mixed: 3
//-------------------------------------------------------------------------------------------------
DTV_DMP_AUDIO_DEC_SOUNDMOD_TYPE MSrv_SSSound::GetDTVInfoDMPInfo_DECODER_SoundMode()
{
    return ((DTV_DMP_AUDIO_DEC_SOUNDMOD_TYPE)mapi_interface::Get_mapi_audio()->DECODER_GetCurrOutputMode());
}

//-------------------------------------------------------------------------------------------------
/// Get DTV or DMP Decoder Control Command
/// @return MMA_AUDIO_CONTROL_: MMA_STOP_, MMA_PLAY_,MMA_PLAY_FILETSP_, MMA_RESYNC_, MMA_PLAY_FILE_,
///                             MMA_BROWSE_, MMA_PAUSE_
//-------------------------------------------------------------------------------------------------
DTV_DMP_AUDIO_DEC_CTRLCMD_TYPE MSrv_SSSound::GetDTVInfoDMPInfo_DECODER_CtrlCmd()
{
    return m_DTV_DMP_DECODER_CTRL_CMD;
}

//-------------------------------------------------------------------------------------------------
/// Get DTV or DMP Decoder Channel Mode
/// @return DTV_AUDIO_DEC_CHMOD_Type: AUDIO_DEC_ACMODE_NOTREADY, AUDIO_DEC_ACMODE_DUALMONO1, AUDIO_DEC_ACMODE_STEREO ...etc
//-------------------------------------------------------------------------------------------------
DTV_AUDIO_DEC_CHMOD_Type MSrv_SSSound::GetDTVInfoDMPInfo_DECODER_ChannelMode()
{
    return  ((DTV_AUDIO_DEC_CHMOD_Type)mapi_interface::Get_mapi_audio()->DECODER_GetChannelMode());
}

//-------------------------------------------------------------------------------------------------
/// SetAmplifierMute: To Set Amplifier Mute
/// @param bMute        \b IN: TRUE for Mute; FALSE for unMute
/// @return RESULT
//-------------------------------------------------------------------------------------------------
MAPI_BOOL MSrv_SSSound::SetAmplifierMute(MAPI_BOOL bMute)
{
    //TBD
    printf("[%d]%s Not implemented!",__LINE__,__PRETTY_FUNCTION__);
    return MAPI_FALSE;
}

//-------------------------------------------------------------------------------------------------
/// SetAmplifierEQByMode: To Set EQ By Mode
/// @param Mode      \b IN: desktop mode or hungup mode.
/// @return NONE
//-------------------------------------------------------------------------------------------------
void MSrv_SSSound::SetAmplifierEQByMode(ENUM_SET_EQ_BY_MODE_TYPE Mode)
{
    //TBD
    printf("[%d]%s Not implemented!",__LINE__,__PRETTY_FUNCTION__);
}

void MSrv_SSSound::SetVolumeWithExternalScale(MAPI_S32 value, MAPI_S32 min, MAPI_S32 max)
{
    value = GetNormalizedVolume(value,
                                min,
                                max);
    SetVolume(value);
}

//-------------------------------------------------------------------------------------------------
/// Init Sound Effect setting wile entering skype mode
/// @return MSRV_SSSND_RET: RETURN_OK
//-------------------------------------------------------------------------------------------------
MSRV_SSSND_RET  MSrv_SSSound::EnterSkypeSoundSetting(void)
{
    MAPI_U8 u8band;

    printf("\n\033[0;35m [%s] [%s] \033[0m\n", __FILE__, __FUNCTION__);
    mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_MOMENT_MUTEON_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);

    // Init PEQ
    for(u8band=0; u8band<5; u8band++)
    {
        mapi_interface::Get_mapi_audio()->SND_SetPEQ(Skype_PEQParam[u8band].Band,
                Skype_PEQParam[u8band].Gain,
                Skype_PEQParam[u8band].Foh,
                Skype_PEQParam[u8band].Fol,
                Skype_PEQParam[u8band].QValue);
    }

    for(u8band=0; u8band<3; u8band++)
    {
        mapi_interface::Get_mapi_audio()->SND_SetHLPF(Skype_HLPFParam[u8band].Band,
                Skype_HLPFParam[u8band].Gain,
                Skype_HLPFParam[u8band].Foh,
                Skype_HLPFParam[u8band].Fol);
    }

    mapi_interface::Get_mapi_audio()->SND_ProcessEnable(Sound_ENABL_Type_EQ_, FALSE);
    mapi_interface::Get_mapi_audio()->SND_ProcessEnable(Sound_ENABL_Type_Tone_, FALSE);
    mapi_interface::Get_mapi_audio()->SND_ProcessEnable(Sound_ENABL_Type_Balance_, FALSE);
    mapi_interface::Get_mapi_audio()->SND_ProcessEnable(Sound_ENABL_Type_Surround_, FALSE);
    mapi_interface::Get_mapi_audio()->SND_Speaker_SetBufferProcess(SKYPE_DELAY_MS);
    mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_MOMENT_MUTEOFF_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
    return RETURN_OK;
}

//-------------------------------------------------------------------------------------------------
/// Restore Sound Effect setting wile exiting skype mode
/// @return MSRV_SSSND_RET: RETURN_OK
//-------------------------------------------------------------------------------------------------
MSRV_SSSND_RET  MSrv_SSSound::ExitSkypeSoundSetting(void)
{
    MAPI_U8 u8band;

    printf("\n\033[0;35m [%s] [%s] \033[0m\n", __FILE__, __FUNCTION__);
    mapi_interface::Get_mapi_audio()->SetAudioMuteDuringLimitedTime(1000, AUDIO_PROCESSOR_MAIN);

    mapi_interface::Get_mapi_audio()->SND_InitPEQ(TRUE, PEQ_5_BANDS);

    for(u8band=0; u8band<3; u8band++)
    {
        mapi_interface::Get_mapi_audio()->SND_SetPEQ(Skype_HLPFParam[u8band].Band,
                0x0,
                0x10,
                0x00,
                0x80);
    }

    mapi_interface::Get_mapi_audio()->SND_ProcessEnable(Sound_ENABL_Type_EQ_, TRUE);
    mapi_interface::Get_mapi_audio()->SND_ProcessEnable(Sound_ENABL_Type_Tone_, TRUE);
    mapi_interface::Get_mapi_audio()->SND_ProcessEnable(Sound_ENABL_Type_Balance_, TRUE);
    mapi_interface::Get_mapi_audio()->SND_ProcessEnable(Sound_ENABL_Type_Surround_, TRUE);

    return RETURN_OK;
}

//-------------------------------------------------------------------------------------------------
/// Get Skype Sound Setting
/// @param u8Option   \b IN: sound option
/// @return MAPI_U32: The value of Skype sound seeting
//-------------------------------------------------------------------------------------------------
MAPI_U32  MSrv_SSSound::GetSkypeSoundSetting(MAPI_U8 u8Option)
{
    MAPI_U32 u32Value = 0;

    if(u8Option == 0)
    {
        u32Value = SKYPE_MAX_VOLUME;
    }

    //printf("\n\033[0;35m [%s] [%s] [return == %d] \033[0m\n", __FILE__, __FUNCTION__, u32Value);
    return u32Value;
}

MAPI_U8 MSrv_SSSound::GetNormalizedVolume(MAPI_S32 value, MAPI_S32 min, MAPI_S32 max)
{
    //linear way
    if(value < min)
        value = min;
    else if(value > max)
        value = max;

    return (int)((float)value - min )*(VOLUME_SCALE_MAX - VOLUME_SCALE_MIN) / (max - min) + VOLUME_SCALE_MIN;
}

MAPI_U8 MSrv_SSSound::SND_SetNrThreshold(const MAPI_U32 value)
{
    mapi_interface::Get_mapi_audio()->SetNrThreshold(value);
    return SSSOUND_OK;
}

MSRV_SSSND_RET MSrv_SSSound::SetDebugMsgLevel(MSRV_SSSOUND_DEBUG_LEVEL eDebugLevel)
{
    MSRV_SSSND_RET bReturnValue=RETURN_NOTOK;

    switch(eDebugLevel)
    {
    case MSRV_SSSOUND_LEVEL0_EMERG:
        m_msrv_sssound_debug_level = MSRV_SSSOUND_LEVEL0_EMERG;
        bReturnValue=RETURN_OK;
        break;

    case MSRV_SSSOUND_LEVEL1_ALERT:
        m_msrv_sssound_debug_level = MSRV_SSSOUND_LEVEL1_ALERT;
        bReturnValue=RETURN_OK;
        break;

    case MSRV_SSSOUND_LEVEL2_CRIT:
        m_msrv_sssound_debug_level = MSRV_SSSOUND_LEVEL2_CRIT;
        bReturnValue=RETURN_OK;
        break;

    case MSRV_SSSOUND_LEVEL3_ERR:
        m_msrv_sssound_debug_level = MSRV_SSSOUND_LEVEL3_ERR;
        bReturnValue=RETURN_OK;
        break;

    case MSRV_SSSOUND_LEVEL4_WARNING:
        m_msrv_sssound_debug_level = MSRV_SSSOUND_LEVEL4_WARNING;
        bReturnValue=RETURN_OK;
        break;

    case MSRV_SSSOUND_LEVEL5_NOTICE:
        m_msrv_sssound_debug_level = MSRV_SSSOUND_LEVEL5_NOTICE;
        bReturnValue=RETURN_OK;
        break;

    case MSRV_SSSOUND_LEVEL7_DEBUG:
        m_msrv_sssound_debug_level = MSRV_SSSOUND_LEVEL7_DEBUG;
        bReturnValue=RETURN_OK;
        break;

    default:
        m_msrv_sssound_debug_level = MSRV_SSSOUND_LEVEL_MAX;
        bReturnValue=RETURN_OK;
        break;
    }

    return bReturnValue;
}

MAPI_BOOL MSrv_SSSound::SetCommAudioInfo(Audio_COMM_infoType_ eInfoType, MAPI_S32 param1, MAPI_S32 param2 )
{
    MAPI_BOOL bRet = FALSE;

    bRet = mapi_interface::Get_mapi_audio()->SetCommAudioInfo(eInfoType, param1, param2);

    return bRet;
}

//-------------------------------------------------------------------------------------------------
/// Set audio debug level
/// @param eDbgLvl   \b IN: audio system debug level
/// @return MAPI_U8: SSSOUND_OK
//-------------------------------------------------------------------------------------------------
MAPI_BOOL MSrv_SSSound::SetAudioDebugLevel(E_AUDIO_DBGLVL eDbgLvl)
{
    MAPI_U32 u32DbgLvl;
    switch(eDbgLvl)
    {
    case AUDIO_DEBUG_LEVEL_AVSYNC:
        u32DbgLvl = 0x1;
        break;
    case AUDIO_DEBUG_LEVEL_DEC1:
        u32DbgLvl = 0x2;
        break;
    case AUDIO_DEBUG_LEVEL_IOINFO:
        u32DbgLvl = 0x4;
        break;
    case AUDIO_DEBUG_LEVEL_ALL:
        u32DbgLvl = 0x7;
        break;
    case AUDIO_DEBUG_LEVEL_OFF:
    default:
        u32DbgLvl = 0x0;
        break;
    }
    mapi_interface::Get_mapi_audio()->AUDIO_SetDebugLevel(u32DbgLvl);
    return SSSOUND_OK;
}

MAPI_U8 MSrv_SSSound::SetDolbyBulletin11(MAPI_BOOL bEnable)
{
    // enable Dolby Bulletin11, attenuate by 11dB in HDMI/SPDIF output at decoded PCM level. Default is enabled.
    if(bEnable)
    {
        mapi_interface::Get_mapi_audio()->DECODER_SetAC3PInfo(Audio_AC3P_infoType_enableDolbyBulletin11_, 1, 0);
    }
    else
    {
        mapi_interface::Get_mapi_audio()->DECODER_SetAC3PInfo(Audio_AC3P_infoType_enableDolbyBulletin11_, 0, 0);
    }
    return SSSOUND_OK;
}

MAPI_U8 MSrv_SSSound::SetDDPlusHDMITxByPass(MAPI_BOOL bEnable)
{
    // DD+ audio can pass through HDMI Tx directly, or DD+ would be transcoded to DD first and then pass through HDMI Tx later
    if(bEnable)
    {
        mapi_interface::Get_mapi_audio()->DECODER_SetAC3PInfo(Audio_AC3P_infoType_hdmiTxBypass_enable_, 1, 0);
    }
    else
    {
        mapi_interface::Get_mapi_audio()->DECODER_SetAC3PInfo(Audio_AC3P_infoType_hdmiTxBypass_enable_, 0, 0);
    }
    return SSSOUND_OK;
}
//jerry.wang add
MAPI_U32 MSrv_SSSound::GetMpegFrameCnt(void)
{
    return mapi_interface::Get_mapi_audio()->DECODER_GetCurrentFrameNumber();
}
#if 0
MAPI_U8 MSrv_SSSound::SPDIF_HWEN(MAPI_BOOL spdif_en)
{
    mapi_interface::Get_mapi_audio()->SPDIF_HWEN(spdif_en);

    return SSSOUND_OK;
}

MAPI_U8 MSrv_SSSound::SPDIF_SetMode(SPDIF_TYPE_ spdif_mode)
{
    mapi_interface::Get_mapi_audio()->SPDIF_SetMode( spdif_mode);

    return SSSOUND_OK;
}
#endif
MAPI_U64 MSrv_SSSound::GetAudioCommInfo( MAPI_U32 infoType)
{
    //return mapi_interface::Get_mapi_audio()->GetCommAudioInfo( (Audio_COMM_infoType_) infoType );
    return TRUE;
}

MAPI_U8 MSrv_SSSound::CheckInputRequest(MAPI_U32 *pU32WrtAddr, MAPI_U32 *pU32WrtBytes)
{
    if( pU32WrtAddr == NULL ||pU32WrtBytes == NULL )
    {
        return 0; //FALSE
    }

    return mapi_interface::Get_mapi_audio()->CheckInputRequest( pU32WrtAddr,pU32WrtBytes);
}

void MSrv_SSSound::SetInput(void)
{
    mapi_interface::Get_mapi_audio()->SetInput();
}

void MSrv_SSSound::StartDecode(void)
{
    mapi_interface::Get_mapi_audio()->StartDecode();
}

void MSrv_SSSound::StopDecode(void)
{
    mapi_interface::Get_mapi_audio()->StopDecode();
}

void MSrv_SSSound::SwitchAudioDSPSystem(AUDIO_DSP_SYSTEM_ eAudioDSPSystem)
{
    mapi_interface::Get_mapi_audio()->DECODER_SwitchAudioDSPSystem( eAudioDSPSystem);

}
void MSrv_SSSound::SetAudioSpidifOutPut(SPDIF_TYPE_ eSpidif_output)
{
    mapi_interface::Get_mapi_audio()->SPDIF_SetMode( eSpidif_output);

}
void MSrv_SSSound::SetAudioHDMIOutPut(HDMI_TYPE_ eHdmi_putput)
{
    //mapi_interface::Get_mapi_audio()->HDMI_SetMode( eHdmi_putput);

}
void MSrv_SSSound::SetAQParamFromUSB(string sFilePath)
{
    printf("\033[1;32m [AUDIO][%s] [%s] [%d] [Set advance sound effect parameters from USB: %s] \033[0m \n", __FILE__, __FUNCTION__, __LINE__, sFilePath.c_str());

    ADVSND_TYPE_ enMapiAdvSndType = DOLBY_PL2VDS_;
    U32 u32value1 = 0;

    FILE* fp = NULL;
    fp = fopen(sFilePath.c_str(), "r");

    if(fp == NULL)
    {
        printf("\033[1;32m [AUDIO][%s] [%s] [%d] [%s doesn't exist]\033[0m \n", __FILE__, __FUNCTION__, __LINE__, sFilePath.c_str());
    }
    else
    {
        char sAdvSEType[64];
        fscanf(fp, "%s", sAdvSEType);
        printf("\033[1;32m [AUDIO][%s] [%s] [%d] [Start to read file, Type is %s]\033[0m \n", __FILE__, __FUNCTION__, __LINE__, sAdvSEType);

        if(strcmp(sAdvSEType,"ADVSND_DOLBY_PL2VDS")==0)
        {
            enMapiAdvSndType = DOLBY_PL2VDS_;
            u32value1 = 0;
        }
        else if(strcmp(sAdvSEType, "ADVSND_DOLBY_PL2VDPK")==0)
        {
            enMapiAdvSndType = DOLBY_PL2VDPK_;
            u32value1 = 0;
        }
        else if(strcmp(sAdvSEType, "ADVSND_BBE")==0)
        {
            enMapiAdvSndType = BBE_;
            u32value1 = 0;
        }
        else if(strcmp(sAdvSEType, "ADVSND_SRS_TSXT")==0)
        {
            enMapiAdvSndType = SRS_TSXT_;
            u32value1 = 0;
        }
        else if(strcmp(sAdvSEType, "ADVSND_SRS_TSHD")==0)
        {
            enMapiAdvSndType = SRS_TSHD_;
            u32value1 = 0;
        }
        else if(strcmp(sAdvSEType, "ADVSND_SRS_THEATERSOUND")==0)
        {
            enMapiAdvSndType = SRS_THEATERSOUND_;
            u32value1 = 0;
        }
        else if(strcmp(sAdvSEType, "ADVSND_DTS_ULTRATV")==0)
        {
            enMapiAdvSndType = DTS_ULTRATV_;
            u32value1 = 0;
        }
        else if(strcmp(sAdvSEType, "ADVSND_AUDYSSEY")==0)
        {
            enMapiAdvSndType = AUDYSSEY_;
            u32value1 = 0;
        }
        else if(strcmp(sAdvSEType, "ADVSND_SUPER_VOICE")==0)
        {
            enMapiAdvSndType = SUPER_VOICE_;
            u32value1 = 0;
        }
        else if(strcmp(sAdvSEType, "ADVSND_DBX")==0)
        {
            enMapiAdvSndType = DBX_;
            u32value1 = 0;
        }
        else if(strcmp(sAdvSEType, "ADVSND_SRS_PURESOUND")==0)
        {
            enMapiAdvSndType = SRS_PURESND_;
            u32value1 = DTS_AEQ_TABLE_NUM; //AEQ coefficient number
            U32 u32table[DTS_AEQ_TABLE_NUM] = {0};
            for(int i = 0; i < DTS_AEQ_TABLE_NUM; i++)
            {
                fscanf(fp, "%x", &u32table[i]);
                //printf("\033[1;32m u32table[%d] : 0x%08lX\033[m\n", i+1, u32table[i]);
            }
            mapi_interface::Get_mapi_audio()->ADVSND_SetTable(enMapiAdvSndType, u32value1, u32table);
        }
        else if(strcmp(sAdvSEType, "ADVSND_SRS_THEATERSOUND3D")==0)
        {
            enMapiAdvSndType = DTS_STUDIOSOUND_3D_;
            u32value1 = 0;
        }
        else
        {
            printf("No such advance sound effect type\n");
            fclose(fp);
            return;
        }
        fclose(fp);
        printf("\033[1;32m [AUDIO][%s] [%s] [%d] [Update Table Successfully]\033[0m \n", __FILE__, __FUNCTION__, __LINE__);
    }
}

void MSrv_SSSound::SetAudioHDMItx_HDBypass(MAPI_BOOL bEnable)
{
    mapi_interface::Get_mapi_audio()->AUDIO_SetHDMItx_HD_Bypass(bEnable);
}

MAPI_BOOL  MSrv_SSSound::GetAudioHDMItx_HDBypass(void)
{
    return mapi_interface::Get_mapi_audio()->AUDIO_GetHDMItx_HD_Bypass();
}

MAPI_BOOL  MSrv_SSSound::GetAudioHDMItx_HDBypass_Capability(void)
{
    return mapi_interface::Get_mapi_audio()->AUDIO_GetHDMItx_HD_Bypass_Capability();
}
void MSrv_SSSound::ProcessEvent(void * arg1, void * arg2, void * arg3)
{
    if(m_pUpdateInfo != NULL)
    {

        UPDATE_INFO *pUpdateInfo;
        pUpdateInfo = (UPDATE_INFO *)m_pUpdateInfo;

        IEnvManager* pEnvMan = IEnvManager::Instance();

         if (pEnvMan == NULL)
         {
             ASSERT(0);
         }

         IEnvManager_scope_lock block(pEnvMan);
         int size=pEnvMan->QueryLength("audio_upgrade");
         char audiotmp[size+1];
         memset(audiotmp,0,size+1);
         bool bAudioUpgradeInfo = pEnvMan->GetEnv_Protect("audio_upgrade",audiotmp,size);
         bool bAudioUpgradeEnable= !(strncmp(audiotmp, "1", 1));
         if( (bAudioUpgradeInfo) && (bAudioUpgradeEnable) && (pUpdateInfo->nty_mnt_status==NTY_MOUNT) )
         {
            string m_strFilePath;
            int data_size=512;

            FILE *pFile = NULL;
            char tmpCmd[data_size];
            char sFilePath[data_size];
            memset(tmpCmd, 0, sizeof(tmpCmd));
            sprintf(tmpCmd, "find %s -name %s", pUpdateInfo->udi.mount_path,AUDIO_UPGRADE_FILE);
            pFile = popen(tmpCmd, "r");
            if(pFile != NULL)
            {
                memset(sFilePath,0, data_size);
                fgets(sFilePath, data_size, pFile);
                pclose(pFile);
                if(strlen(sFilePath) > 0)
                {
                    m_strFilePath = sFilePath;
                    // remove the last char, line break '\n'.
                    m_strFilePath.erase(m_strFilePath.end() - 1);
                    SetAQParamFromUSB(m_strFilePath);
                }
            }
        }
    }
}
 #if (KARAOKE_ENABLE == 1)
void MSrv_SSSound::SetMicVol(MAPI_U8 Value)
{
     MS_USER_SOUND_SETTING  m_stSysSetting;
     MSrv_Control::GetMSrvSystemDatabase()->GetAudioSetting(&m_stSysSetting);
     MS_USER_SOUND_SETTING stOrgSystemSetting = m_stSysSetting;
     m_stSysSetting.MicVal=(U8)Value; 
       
    mapi_audio_amp *pAudioAmp = mapi_interface::Get_mapi_pcb()->GetAudioAmp(0);
    pAudioAmp->MicVol_Set((U8)Value);
    
    MSrv_Control::GetMSrvSystemDatabase()->SetAudioSetting(&m_stSysSetting, &stOrgSystemSetting);	 


}

void MSrv_SSSound::SetMicEcho(MAPI_U8 Value)
{

    mapi_audio_amp *pAudioAmp = mapi_interface::Get_mapi_pcb()->GetAudioAmp(0);
    pAudioAmp->MicEcho_Set((U8)Value);
}
MAPI_U8 MSrv_SSSound::GetMicVol()
{
    MS_USER_SOUND_SETTING stAudioSetting;

    MSrv_Control::GetMSrvSystemDatabase()->GetAudioSetting(&stAudioSetting);

    return stAudioSetting.MicVal;
}

MAPI_U8 MSrv_SSSound::GetMicEcho()
{
    MS_USER_SOUND_SETTING stAudioSetting;

    MSrv_Control::GetMSrvSystemDatabase()->GetAudioSetting(&stAudioSetting);

    return stAudioSetting.MicEchoVal;
}
#endif
