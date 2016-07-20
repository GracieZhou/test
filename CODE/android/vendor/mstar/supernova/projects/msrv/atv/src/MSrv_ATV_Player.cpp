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


#define _MSrv_ATV_Player_CPP_

// headers of itself
#include "MSrv_ATV_Player.h"

// headers of standard C libs
#include <sys/prctl.h>
#include <time.h>
#include <pthread.h>
#include <unistd.h>
#include <limits.h>

// headers of standard C++ libs

// headers of the same layer's
#include "MW_ATV_Scan_Customer.h"
#include "MSrv_Picture.h"
#include "MSrv_Control.h"
#include "MSrv_SSSound.h"
#include "MSrv_Factory_Mode.h"
#include "MSrv_System_Database.h"
#include "MSrv_Video.h"
#if (ENABLE_BACKEND == 1)
#include "MSrv_Backend.h"
#endif

#if (ATSC_SYSTEM_ENABLE == 1)
#include "MSrv_System_Database_ATSC.h"
#include "MSrv_ChannelManager_ATSC.h"
#endif

#if (ISDB_SYSTEM_ENABLE == 1)
#include "MSrv_System_Database_DVB.h"
#include "MSrv_ChannelManager_ISDB.h"
#endif

#if (ESASIA_NTSC_SYSTEM_ENABLE == 1)
#include "MSrv_System_Database_DVB.h"
#endif

#if (TTX_ENABLE == 1)
#include "MW_TTX.h"
#endif
#if ((ATSC_CC_ENABLE == 1)||(ISDB_CC_ENABLE == 1)||(NTSC_CC_ENABLE == 1))
#include "MW_CC.h"
#endif

#if (HBBTV_ENABLE == 1)
#include "MW_HBBTV.h"
#endif

#if ENABLE_CUSTOMER_ATS_TABLE
#include "msAPI_CNI.h"
#endif


#include "MW_ATV_Util.h"

#include "MSrv_Control_common.h"

#if (MWE_ENABLE == 1)
#include "MSrv_MWE.h"
#endif

// headers of underlying layer's
#include "debug.h"
#include "mapi_types.h"
#include "mapi_utility.h"
#include "mapi_vd.h"
#include "mapi_video_vd_cfg.h"
#include "mapi_video.h"
#include "mapi_video_out.h"
#include "mapi_gpio.h"
#include "mapi_gpio_devTable.h"
#include "mapi_cni.h"
#include "mapi_audio.h"
#include "mapi_vif.h"
#include "SystemInfo.h"
#include "mapi_pcb.h"
#include "mapi_interface.h"
#include "mapi_demodulator.h"
#include "MSrv_MWE.h"
#if (STR_ENABLE == 1)
#include "mapi_str.h"
#endif

#if (OFL_DET == 1)
#include "MSrv_SrcDetect.h"
#endif



// Debug Message
#define ATV_PLAYER_ERR(fmt, arg...)          printf((char *)fmt, ##arg)
#define ATV_PLAYER_DBG(fmt, arg...)          //printf((char *)fmt, ##arg)
#define ATV_PLAYER_IFO(fmt, arg...)          //printf((char *)fmt, ##arg)
#define ATV_PLAYER_FLOW(fmt, arg...)       //printf((char *)fmt, ##arg)


/*@ </Definitions> @*/

// PLL steps
#define PLLSTEP(x)                      (m_u16TunerPLL+(x))

#define TUNER_PLL_REFRESH               0
#define TUNER_PLL_PLUS_37p5KHz          ConvertFrequncyHzToPLL(37500)
#define TUNER_PLL_PLUS_62p5KHz          ConvertFrequncyHzToPLL(62500)
#define TUNER_PLL_PLUS_87p5KHz          ConvertFrequncyHzToPLL(87500)
#define TUNER_PLL_PLUS_112p5KHz         ConvertFrequncyHzToPLL(112500)
#define TUNER_PLL_PLUS_137p5KHz         ConvertFrequncyHzToPLL(137500)
#define TUNER_PLL_PLUS_162p5KHz         ConvertFrequncyHzToPLL(162500)
#define TUNER_PLL_PLUS_187p5KHz         ConvertFrequncyHzToPLL(187500)
#define TUNER_PLL_PLUS_0p2MHz           ConvertFrequncyHzToPLL(200000)
#define TUNER_PLL_PLUS_0p25MHz          ConvertFrequncyHzToPLL(250000)
#define TUNER_PLL_PLUS_0p5MHz           ConvertFrequncyHzToPLL(500000)
#define TUNER_PLL_PLUS_0p75MHz          ConvertFrequncyHzToPLL(750000)
#define TUNER_PLL_PLUS_1MHz             ConvertFrequncyHzToPLL(1000000)
#define TUNER_PLL_PLUS_1p25MHz          ConvertFrequncyHzToPLL(1250000)
#define TUNER_PLL_PLUS_1p5MHz           ConvertFrequncyHzToPLL(1500000)
#define TUNER_PLL_PLUS_1p75MHz          ConvertFrequncyHzToPLL(1750000)
#define TUNER_PLL_PLUS_2MHz             ConvertFrequncyHzToPLL(2000000)
#define TUNER_PLL_PLUS_2p25MHz          ConvertFrequncyHzToPLL(2250000)
#define TUNER_PLL_PLUS_2p5MHz           ConvertFrequncyHzToPLL(2500000)
#define TUNER_PLL_PLUS_2p75MHz          ConvertFrequncyHzToPLL(2750000)
#define TUNER_PLL_PLUS_3MHz             ConvertFrequncyHzToPLL(3000000)
#define TUNER_PLL_PLUS_4MHz             ConvertFrequncyHzToPLL(4000000)
#define TUNER_PLL_PLUS_4p5MHz           ConvertFrequncyHzToPLL(4500000)
#define TUNER_PLL_PLUS_5MHz             ConvertFrequncyHzToPLL(5000000)
#define TUNER_PLL_PLUS_5p25MHz          ConvertFrequncyHzToPLL(5250000)
#define TUNER_PLL_PLUS_6p5MHz           ConvertFrequncyHzToPLL(6500000)

#define TUNER_PLL_MINUS_37p5KHz         (-ConvertFrequncyHzToPLL(37500))
#define TUNER_PLL_MINUS_62p5KHz         (-ConvertFrequncyHzToPLL(62500))
#define TUNER_PLL_MINUS_87p5KHz         (-ConvertFrequncyHzToPLL(87500))
#define TUNER_PLL_MINUS_112p5KHz        (-ConvertFrequncyHzToPLL(112500))
#define TUNER_PLL_MINUS_137p5KHz        (-ConvertFrequncyHzToPLL(137500))
#define TUNER_PLL_MINUS_162p5KHz        (-ConvertFrequncyHzToPLL(162500))
#define TUNER_PLL_MINUS_187p5KHz        (-ConvertFrequncyHzToPLL(187500))
#define TUNER_PLL_MINUS_0p2MHz          (-ConvertFrequncyHzToPLL(200000))
#define TUNER_PLL_MINUS_0p25MHz         (-ConvertFrequncyHzToPLL(250000))
#define TUNER_PLL_MINUS_0p5MHz          (-ConvertFrequncyHzToPLL(500000))
#define TUNER_PLL_MINUS_0p75MHz         (-ConvertFrequncyHzToPLL(750000))
#define TUNER_PLL_MINUS_1MHz            (-ConvertFrequncyHzToPLL(1000000))
#define TUNER_PLL_MINUS_1p25MHz         (-ConvertFrequncyHzToPLL(1250000))
#define TUNER_PLL_MINUS_1p5MHz          (-ConvertFrequncyHzToPLL(1500000))
#define TUNER_PLL_MINUS_1p75MHz         (-ConvertFrequncyHzToPLL(1750000))
#define TUNER_PLL_MINUS_2MHz            (-ConvertFrequncyHzToPLL(2000000))
#define TUNER_PLL_MINUS_2p25MHz         (-ConvertFrequncyHzToPLL(2250000))
#define TUNER_PLL_MINUS_2p5MHz          (-ConvertFrequncyHzToPLL(2500000))
#define TUNER_PLL_MINUS_2p75MHz         (-ConvertFrequncyHzToPLL(2750000))
#define TUNER_PLL_MINUS_3MHz            (-ConvertFrequncyHzToPLL(3000000))
#define TUNER_PLL_MINUS_4MHz            (-ConvertFrequncyHzToPLL(4000000))
#define TUNER_PLL_MINUS_5MHz            (-ConvertFrequncyHzToPLL(5000000))
#define TUNER_PLL_MINUS_5p25MHz         (-ConvertFrequncyHzToPLL(5250000))

#define FINE_TUNE_STEP                  1
#define L_PRIME_BOUNDARY_FREQ           66500L // KHz

#define CHANNEL_BANDWIDTH_MASK          0x0F00
#define CHANNEL_BANDWIDTH_0M            0x0000
#define CHANNEL_BANDWIDTH_6M            0x0100
#define CHANNEL_BANDWIDTH_7M            0x0200
#define CHANNEL_BANDWIDTH_8M            0x0300
#define CHANNEL_BANDWIDTH_9M            0x0400
#define CHANNEL_BANDWIDTH_10M           0x0500
#define CHANNEL_BANDWIDTH_11M           0x0600
#define CHANNEL_BANDWIDTH_12M           0x0700

#define LEAD_CHANNEL_MASK               0x00FF

#define CCIR_UHF_FIRSTCHANNEL           21

#define TUNING_CHECK_TTX_COUNT          10
#define TUNING_CHECK_CNI_COUNT          20

#define AFT_EXT_STEP_PERIODIC           0x00
#define AFT_EXT_STEP_SEARCHALL          0x01
#define AFT_EXT_STEP_SEARCHONETOUP      0x02
#define AFT_EXT_STEP_SEARCHONETODOWN    0x03
#define AFT_EXT_STEP_SEARCHSTOP         0x04
#define AFT_TUNING_SUSPEND              0x05
#define AFT_TUNING_RESUME               0x06

#define MAX_VIDEOSTANDARDSTRING         8
#define MAX_VIDEOSTANDARDINFO           7
#define MAX_AUDIOSTANDARDSTRING         9
#define MAX_AUDIOSTANDARDINFO           14

#define ENABLE_AUDIO_AUTO_DETECT        1
#define TUNER_PLL_NEXT_CHANNEL_JUMP     TUNER_PLL_PLUS_5p25MHz

#if (ISDB_SYSTEM_ENABLE == 1)
#define ATV_FAST_ZAPPING_ENABLE         0
#define ATV_PROGRAM_CHANGE_BLACK_SCREEN_TIME    1000    //ms
#else
#define ATV_FAST_ZAPPING_ENABLE         1
#if (1 == ATV_FAST_ZAPPING_ENABLE)
#define ATV_PROGRAM_CHANGE_BLACK_SCREEN_TIME    300    //ms
#define ATV_PROGRAM_CHANGE_BLACK_SCREEN_TIME_MAX    600    //ms
#define ATV_PROGRAM_CHANGE_SKIP_CHECK_VIDEO_STANDARD_TIME 1500
#else
#define ATV_PROGRAM_CHANGE_BLACK_SCREEN_TIME    600    //ms
#endif
#endif
#define ATV_PROGRAM_CHANGE_RESET_VIF_TIME       200    //ms

#if(BRAZIL_CM_NUM_SEARCH == 1)
#define ATV_PROGRAM_CHANGE_SAVE_CHANNEL_TIME       3000    //ms
#endif

#define ATV_AFT_INTERVAL_TIME                   (600)//600ms
#define ATV_DELAY_AUDIO_UNMUTE_TIME             1000//1500
#define ATV_USER_MTSSETTING_INTERVAL_TIME       1000
#define DELAY_FOR_ENTERING_MUTE         150
#define DELAY_FOR_LEAVING_MUTE          10
#define DELAY_FOR_STABLE_VIDEO          500
#define DELAY_FOR_STABLE_TUNER          700
#define DELAY_FOR_STABLE_SIF            400
#define DELAY_FOR_STABLE_MPEG           1500
#define TVAVDelay(a)                    usleep((a)*1000)

#define POSTEVENT_INTERVAL_TIME         300
#define CHANGE_CHANNEL_AUDIO_MUTE_TIME  1500 //orginal is 30, now change the unit per_50ms to ms, so need to change to 30*50=1500 ms

#define USLEEP_TIME_10_MS               (10*1000)
#define USLEEP_TIME_100_MS              (100*1000)
#define THREAD_SIGNAL_MONITOR_EXECUTE_INTERVAL_MS       USLEEP_TIME_10_MS

#define THREAD_SIGNAL_MONITOR_VD_INTERVAL_COUNTER_100MS (USLEEP_TIME_100_MS / THREAD_SIGNAL_MONITOR_EXECUTE_INTERVAL_MS) //100ms / 10ms = 10
#define THREAD_SIGNAL_MONITOR_VD_INTERVAL_COUNTER_200MS (USLEEP_TIME_200_MS / THREAD_SIGNAL_MONITOR_EXECUTE_INTERVAL_MS) //100ms / 10ms = 10
#if (CHINA_ENABLE == 1)
#define GET_DESCRAMBLER_BOX_DELAY(x)  ((x) * 432ul / 100ul)
#endif
#ifndef FRONTEND_IF_DEMODE_TYPE
#if(MSTAR_TVOS == 1)
#define FRONTEND_IF_DEMODE_TYPE 1
#else
#define FRONTEND_IF_DEMODE_TYPE 0
#endif
#endif
#ifndef MSTAR_VIF
#define MSTAR_VIF 1
#endif
#ifndef MSTAR_INTERN_VIF
#define MSTAR_INTERN_VIF 2
#endif
#ifndef MSTAR_VIF_MSB1210
#define MSTAR_VIF_MSB1210 3
#endif

/// Define the video standard information string
typedef struct
{
    MAPI_AVD_VideoStandardType eVideoStandard;
    U8 sStandardSting[MAX_VIDEOSTANDARDSTRING];
} ST_VIDEO_STANDARD_INFO;

/// Define the audio standard information string
typedef struct
{
    AUDIOSTANDARD_TYPE_ eAudioStandard;
    U8 sStandardSting[MAX_AUDIOSTANDARDSTRING];
} ST_AUDIO_STANDARD_INFO;

ST_VIDEO_STANDARD_INFO m_VideoStandardInfo[MAX_VIDEOSTANDARDINFO] =
{
    {E_MAPI_VIDEOSTANDARD_PAL_BGHI,          "PAL    "},
    {E_MAPI_VIDEOSTANDARD_SECAM,             "SECAM  "},
    {E_MAPI_VIDEOSTANDARD_PAL_N,             "PAL N  "},
    {E_MAPI_VIDEOSTANDARD_PAL_M,             "PAL M  "},
    {E_MAPI_VIDEOSTANDARD_PAL_60,            "PAL 60 "},
    {E_MAPI_VIDEOSTANDARD_NTSC_M,            "NTSC M "},
    {E_MAPI_VIDEOSTANDARD_NTSC_44,           "NTSC 44"}
};

ST_AUDIO_STANDARD_INFO m_AudioStandardInfo[MAX_AUDIOSTANDARDINFO] =
{
    {E_AUDIOSTANDARD_BG_,            "BG MONO "},
    {E_AUDIOSTANDARD_BG_A2_,         "BG A2   "},
    {E_AUDIOSTANDARD_BG_NICAM_,      "BG NICAM"},
    {E_AUDIOSTANDARD_I_,             "I       "},
    {E_AUDIOSTANDARD_DK_,            "DK MONO "},
    {E_AUDIOSTANDARD_DK1_A2_,        "DK1 A2  "},
    {E_AUDIOSTANDARD_DK2_A2_,        "DK2 A2  "},
    {E_AUDIOSTANDARD_DK3_A2_,        "DK3 A2  "},
    {E_AUDIOSTANDARD_DK_NICAM_,      "DK NICAM"},
    {E_AUDIOSTANDARD_L_,             "L       "},
    {E_AUDIOSTANDARD_M_,             "M MONO  "},
    {E_AUDIOSTANDARD_M_BTSC_,        "M BTSC  "},
    {E_AUDIOSTANDARD_M_A2_,          "M A2    "},
    {E_AUDIOSTANDARD_M_EIA_J_,       "M EIA-J "}
};


MSrv_ATV_Player::ST_FREQ_CHANNEL m_FreqChannel_BG[] =
{
    { 1869, _AIR    | 21 | CHANNEL_BANDWIDTH_8M },  //(471.25 MHz - 4.0 MHz) / 0.25 MHz
    { 1195, _CABLE  | 21 | CHANNEL_BANDWIDTH_8M },  //(303.25 MHz - 4.5 MHz) / 0.25 MHz
    { 911 , _CABLE  | 11 | CHANNEL_BANDWIDTH_7M },  //(231.25 MHz - 3.5 MHz) / 0.25 MHz
    { 687 , _AIR    |  5 | CHANNEL_BANDWIDTH_7M },  //(175.25 MHz - 3.5 MHz) / 0.25 MHz
    { 405 , _CABLE  |  1 | CHANNEL_BANDWIDTH_7M },  //(105.25 MHz - 4.0 MHz) / 0.25 MHz
    { 263 , _CABLE  | 42 | CHANNEL_BANDWIDTH_7M },  //( 69.25 MHz - 3.5 MHz) / 0.25 MHz
    { 187 , _AIR    |  2 | CHANNEL_BANDWIDTH_7M },  //( 48.25 MHz - 1.5 MHz) / 0.25 MHz
    { 167 , _AIR    |  1 | CHANNEL_BANDWIDTH_7M }   //( 45.25 MHz - 3.5 MHz) / 0.25 MHz
};



MSrv_ATV_Player::ST_FREQ_CHANNEL m_FreqChannel_I[] =
{
    { 1869, _AIR    | 21 | CHANNEL_BANDWIDTH_8M },  //(471.25 MHz - 4.0 MHz) / 0.25 MHz
    { 1195, _CABLE  | 21 | CHANNEL_BANDWIDTH_8M },  //(303.25 MHz - 4.5 MHz) / 0.25 MHz
    { 909 , _CABLE  | 11 | CHANNEL_BANDWIDTH_7M },  //(231.25 MHz - 4.0 MHz) / 0.25 MHz
    { 687 , _AIR    |  4 | CHANNEL_BANDWIDTH_8M },  //(175.25 MHz - 3.5 MHz) / 0.25 MHz
    { 405 , _CABLE  |  1 | CHANNEL_BANDWIDTH_7M },  //(105.25 MHz - 4.0 MHz) / 0.25 MHz
    { 262 , _CABLE  | 42 | CHANNEL_BANDWIDTH_7M },  //( 69.25 MHz - 3.75MHz) / 0.25 MHz
    { 167 , _AIR    |  1 | CHANNEL_BANDWIDTH_8M }   //( 45.75 MHz - 4.0 MHz) / 0.25 MHz
};

MSrv_ATV_Player::ST_FREQ_CHANNEL m_FreqChannel_DK[] =
{
    { 1869, _AIR    | 21 | CHANNEL_BANDWIDTH_8M },  //(471.25 MHz - 4.0 MHz) / 0.25 MHz
    { 1195, _CABLE  | 21 | CHANNEL_BANDWIDTH_8M },  //(303.25 MHz - 4.5 MHz) / 0.25 MHz
    { 909 , _CABLE  | 11 | CHANNEL_BANDWIDTH_7M },  //(231.25 MHz - 4.0 MHz) / 0.25 MHz
    { 687 , _AIR    |  6 | CHANNEL_BANDWIDTH_8M },  //(175.25 MHz - 3.5 MHz) / 0.25 MHz
    { 397 , _CABLE  |  1 | CHANNEL_BANDWIDTH_7M },  //(105.25 MHz - 6.0 MHz) / 0.25 MHz
    { 293 , _AIR    |  3 | CHANNEL_BANDWIDTH_8M },  //( 77.25 MHz - 4.0 MHz) / 0.25 MHz
    { 257 , _CABLE  | 42 | CHANNEL_BANDWIDTH_8M },  //( 69.25 MHz - 5.0 MHz) / 0.25 MHz
    { 179 , _AIR    |  1 | CHANNEL_BANDWIDTH_10M }  //( 49.75 MHz - 5.0 MHz) / 0.25 MHz
};



MSrv_ATV_Player::ST_FREQ_CHANNEL m_FreqChannel_L[] =
{
    { 1869, _AIR    | 21 | CHANNEL_BANDWIDTH_8M },  //(471.25 MHz - 4.00 MHz) / 0.25 MHz
    { 1200, _CABLE  | 21 | CHANNEL_BANDWIDTH_8M },  //(303.25 MHz - 3.25 MHz) / 0.25 MHz
    { 1182, _CABLE  | 19 | CHANNEL_BANDWIDTH_7M },  //(296.75 MHz - 1.25 MHz) / 0.25 MHz
    { 1158, _CABLE  | 20 | CHANNEL_BANDWIDTH_7M },  //(294.25 MHz - 4.75 MHz) / 0.25 MHz
    { 882 , _CABLE  | 13 | CHANNEL_BANDWIDTH_12M }, //(224.75 MHz - 4.25 MHz) / 0.25 MHz
    { 857 , _AIR    | 10 | CHANNEL_BANDWIDTH_9M },  //(216.00 MHz - 1.75 MHz) / 0.25 MHz
    { 842 , _CABLE  | 12 | CHANNEL_BANDWIDTH_7M },  //(212.75 MHz - 2.25 MHz) / 0.25 MHz
    { 817 , _AIR    |  9 | CHANNEL_BANDWIDTH_7M },  //(208.00 MHz - 3.75 MHz) / 0.25 MHz
    { 802 , _CABLE  | 11 | CHANNEL_BANDWIDTH_8M },  //(200.75 MHz - 0.25 MHz) / 0.25 MHz
    { 761 , _AIR    |  7 | CHANNEL_BANDWIDTH_8M },  //(192.00 MHz - 1.75 MHz) / 0.25 MHz
    { 746 , _CABLE  | 10 | CHANNEL_BANDWIDTH_7M },  //(188.75 MHz - 2.25 MHz) / 0.25 MHz
    { 721 , _AIR    |  6 | CHANNEL_BANDWIDTH_7M },  //(184.00 MHz - 3.75 MHz) / 0.25 MHz
    { 706 , _CABLE  |  9 | CHANNEL_BANDWIDTH_8M },  //(176.75 MHz - 0.25 MHz) / 0.25 MHz
    { 681 , _AIR    |  5 | CHANNEL_BANDWIDTH_7M },  //(176.00 MHz - 5.75 MHz) / 0.25 MHz
    { 451 , _CABLE  |  4 | CHANNEL_BANDWIDTH_12M }, //(116.75 MHz - 4.00 MHz) / 0.25 MHz
    { 352 , _CABLE  |  1 | CHANNEL_BANDWIDTH_8M },  //( 92.75 MHz - 4.75 MHz) / 0.25 MHz
    { 266 , _CABLE  | 42 | CHANNEL_BANDWIDTH_7M },  //( 69.25 MHz - 2.75 MHz) / 0.25 MHz
    { 248 , _AIR    |  4 | CHANNEL_BANDWIDTH_7M },  //( 63.75 MHz - 1.75 MHz) / 0.25 MHz
    { 233 , _AIR    |  3 | CHANNEL_BANDWIDTH_7M },  //( 60.50 MHz - 2.25 MHz) / 0.25 MHz
    { 175 , _AIR    |  1 | CHANNEL_BANDWIDTH_8M }   //( 47.75 MHz - 4.00 MHz) / 0.25 MHz
};

#if ENABLE_CUSTOMER_ATS_TABLE
U16 tStationIdList_ext_GERMANY[] =
{
    STATION_ARD,
    STATION_ZDF,
    STATION_SWR1,
    STATION_RTL,
    STATION_SAT1,
    STATION_3SAT,
    STATION_PRO7,
    STATION_RTL2,
    STATION_SRTL,
    STATION_VOX,
    STATION_DSF,
    STATION_EURSP,
    STATION_PREMIER,
    STATION_KABEL,
    STATION_VIVA,
    STATION_NONE // End of table
};
#endif

MSrv_ATV_Player::MSrv_ATV_Player()
{
    m_u32PostEventIntervalTime = 0;
    m_bIsLSearch = FALSE;
    m_bIsAFTNeeded = FALSE;
    m_bCurrentProgramBlock = FALSE;
    m_bIsPreProgramDisabled = FALSE;
    m_bFlagThreadSignalMonitor_Active = FALSE;
    m_bReSendEvent = FALSE;
#if (TTX_ENABLE == 1)
    m_pcTTX = NULL;
#endif
#if (ATSC_SYSTEM_ENABLE == 1)
    m_u16ScreenSaverCounter = 0;
    m_bIsMTSMonitorEnabled = FALSE;
#endif
#if (ESASIA_NTSC_SYSTEM_ENABLE == 1)//Add for MTS detection issue: 0660220 20140710EL
    m_bIsMTSMonitorEnabled = FALSE;
#endif
    m_u16TunerPLL = 0;
    m_u16IdleTimer = 0;
    m_u32StartTime = 0;
    m_u8ChannelNumber = 0;
    m_eCurrentTuningState = 0;
    m_u8StartChangeProgram = 0;
    m_ManualScanRtUpdateVDandScaler = 0;
    m_u16IfFreqPre = 0;
    m_u8ChangeProgramvifreset = 0;
    m_u32IdleTimer = 0;

    m_eMedium = MSrv_ATV_Database::MEDIUM_CABLE;
    m_eChannelSearchType = E_CHANNEL_SEARCH_NONE;

    m_pTuner = NULL;
    m_pDemodulator = NULL;

    m_threadSignalMonitor_id = 0;
    m_threadSendScanInfo_id = 0;
    m_CurVideoStandard = E_MAPI_VIDEOSTANDARD_NOTSTANDARD;

    /// also initialized in Init() or at beginning of tuning
    m_au8CurrentStationName[0] = '\0';
    m_stAtvScannedInfo.u16ScannedChannelNum = 0;
    m_stAtvScannedInfo.u32FrequencyKHz = 0;
    m_stAtvScannedInfo.u8Percent = 0;
    m_stAtvScannedInfo.u16CurScannedChannel = 0;
#if (ISDB_SYSTEM_ENABLE == 1)
    m_stAtvScannedInfo.u16MaxScannedChannel = 0;
    bForceCheckAudioMode=FALSE;
#endif
    m_stAtvScanParam.aATVMannualTuneMode = E_MANUAL_TUNE_MODE_UNDEFINE;
    m_stAtvScanParam.u16TotalChannelNum = 0;
    m_stAtvScanParam.u32EndFrequency = 0;
    m_stAtvScanParam.u32EventInterval = 0;
    m_stAtvScanParam.u32StartFrequency = 0;
    m_stAtvScanParam.u8ScanState = E_SCAN_STATE_STOP;
    m_stAtvScanParam.u8ScanType = E_SCAN_AUTO_TUNING;
    m_stAtvScannedInfo.bIsScaningEnable = FALSE;

    m_u8MtsStatus = 0;
    m_bProgInfoChg = FALSE;
    m_bSmartScan = FALSE;
    m_bIsDoSetMTSMode = FALSE;

    m_stAtvEvenTimer.timerSwitch = FALSE;
    m_stAtvEvenTimer.startTime = 0;
    m_bBeforeFirstSetChannel = TRUE;
    m_bNeedReloadPQ = TRUE;

    m_u32UserMtsSettingIntervalTime = 0;
    m_u32DelayAudioUnmuteTime = 0;

    if(SystemInfo::GetInstance()->get_ATVSystemType() == E_PAL_ENABLE)
    {
        m_TV_SCAN_PAL_SECAM_ONCE = TRUE;
    }
    else
    {
        m_TV_SCAN_PAL_SECAM_ONCE = FALSE;
    }

    m_Antenna = MSrv_ATV_Database::MEDIUM_CABLE;

    m_u8AftOffset = AFT_OFFSET_0;
    m_u8AftOffset_pre = AFT_OFFSET_0;
    m_u8AftOffset_saved = AFT_OFFSET_0;
    m_u8AftStep = 0;
    m_bInitAtvDemodTuner = FALSE;

    m_pVptr = NULL;

    m_u16UhfMaxPll = 0;
    m_u16UhfMinPll = 0;
    m_u16VhfLowMinPll = 0;
    m_u16VhfHighMinPll = 0;

    m_u16DefaultPll = 0;
#if (CHANNEL_CHANGE_FREEZE_IMAGE_BYDFB_ENBALE == 1)||(ADVERT_BOOTING_ENABLE == 1)
    m_bChannelChangeFreezeImageFlags  = TRUE;//0;
#else
    m_bChannelChangeFreezeImageFlags  = FALSE; //TRUE;//0;
#endif
    m_u8ScreenModeStatus = 0xff;
    m_pTuner = mapi_interface::Get_mapi_pcb()->GetAtvTuner(0);
    ASSERT(m_pTuner);

    m_pDemodulator = mapi_interface::Get_mapi_pcb()->GetAtvDemod(0);
#if ENABLE_CUSTOMER_ATS_TABLE
    mapi_cni* pCniInstance = mapi_interface::Get_mapi_cni();
    if(pCniInstance)
    {
        pCniInstance->InstallCallback_CNI_Cus_GetExtATSTable(CNI_Cus_GetExtATSTableCallBack);
    }
#endif
    MW_ATV_Util::GetInstance()->Init();

    int intPTHChk;
    intPTHChk = PTH_RET_CHK(pthread_mutex_init(&m_mutex_Scan, NULL));
    ASSERT(intPTHChk == 0);
}

 MSrv_ATV_Player::~MSrv_ATV_Player()
{
    pthread_mutex_destroy(&m_mutex_Scan);
}

/*
 ********************************************,
 FUNCTION   : threadSignalMonitor
 USAGE      : Create after Msrv_ATV_Player Init
              Exit when disconnect.
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
void* MSrv_ATV_Player::threadSignalMonitor(void *arg)
{
    bool *pActive;

    BOOL IsUIAlive = FALSE;

    prctl(PR_SET_NAME, (unsigned long)"ATV SigMonitor Task");
#if (STR_ENABLE == 1)
    mapi_str::AutoRegister _R;
#endif
    pActive = (bool*)arg;
    while(*pActive)
    {
        if ( FALSE == IsUIAlive )
        {
            IsUIAlive = MSrv_Control::GetMSrvAtv()->PostEvent(0, EV_SIGNAL_LOCK, 0);
        }

        MSrv_Control::GetMSrvAtv()->ATVProc_Handler();
        //MSrv_Control::GetMSrvAtv()->UpdateMediumStatus();because it's a empty func
#if 0//(ATSC_SYSTEM_ENABLE == 1)
        MSrv_Control::GetMSrvAtv()->ScreenSaverMonitor();
#endif
        usleep((THREAD_SIGNAL_MONITOR_EXECUTE_INTERVAL_MS));
    }
    pthread_exit(NULL);
    return NULL;
}

/*
 ********************************************
 FUNCTION   : DoVideoInit
 USAGE      : To init the video of ATV_Player
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
BOOL MSrv_ATV_Player::DoVideoInit(MAPI_INPUT_SOURCE_TYPE eInputType, MAPI_SCALER_WIN eWin)
{
    if(m_bInit == TRUE)
    {
        ASSERT(0);
        return FALSE;
    }

    U32 u32UhfMaxFreq;
    U32 u32UhfMinFreq;
    U32 u32VhfLowMinFreq;
    U32 u32VhfHighMinFreq;
    U32 u32VhfLowMaxFreq;
    U32 u32VhfHighMaxFreq;
    EN_FREQ_STEP enFreqStep;
    U32 u32StepSize;

    m_PipXCWin = eWin;
    m_CurrentSrcType = eInputType;
    //Init Variable
    m_bFlagThreadSignalMonitor_Active = FALSE;
#if (CHANNEL_CHANGE_FREEZE_IMAGE_BYDFB_ENBALE == 1)
    m_bChannelChangeFreezeImageFlags = MSrv_Control::GetMSrvSystemDatabase()->GetChannelSwitchMode();
#else
    m_bChannelChangeFreezeImageFlags = FALSE;
#endif
    int intPTHChk;
    intPTHChk = PTH_RET_CHK(pthread_mutex_init(&m_mutex_ProgramChangeProcess, NULL));
    ASSERT(intPTHChk == 0);


    //To get the MSrv Instance
    m_u8StartChangeProgram = 0;
    m_ManualScanRtUpdateVDandScaler = 0;
    m_bCurrentProgramBlock = FALSE;
    m_u8ChangeProgramvifreset = 0;
    m_bBeforeFirstSetChannel = TRUE;
    m_bNeedReloadPQ = TRUE;

#if (ATSC_SYSTEM_ENABLE == 1 || ISDB_SYSTEM_ENABLE == 1 )
    ST_MEDIUM_SETTING MediumSetting;
    MSrv_Control::GetMSrvSystemDatabase()->GetMediumSetting(&MediumSetting);
    ATV_PLAYER_IFO("Set Antenna %d\n", MediumSetting.AntennaType);
    if(MediumSetting.AntennaType == E_ANTENNA_TYPE_AIR)
    {
        ATV_PLAYER_IFO("Set to Air\n");
        SetNTSCAntenna(MSrv_ATV_Database::MEDIUM_AIR);
    }
    else if(MediumSetting.AntennaType == E_ANTENNA_TYPE_CABLE)
    {
        ATV_PLAYER_IFO("Set to Cable\n");
        SetNTSCAntenna(MSrv_ATV_Database::MEDIUM_CABLE);
    }
    else
    {
        // error here
        ATV_PLAYER_ERR("!!!!! Error Medium Type\n");
    }

#endif

    //To Init VD
    m_pTuner = mapi_interface::Get_mapi_pcb()->GetAtvTuner(0);
    ASSERT(m_pTuner);
    m_pDemodulator = mapi_interface::Get_mapi_pcb()->GetAtvDemod(0);
    ASSERT(m_pDemodulator);

    mapi_interface::Get_mapi_vd()->SetToInternVIF(m_pDemodulator->ATV_IsInternalVIF());
    /*if( ((TRUE==MSrv_Control::GetInstance()->IsPipModeEnable()) && (eWin==MAPI_MAIN_WINDOW)) || (FALSE==MSrv_Control::GetInstance()->IsPipModeEnable()) )
    {
        MSrv_Control::GetInstance()->SetVideoMute(TRUE, mapi_video_datatype::E_SCREEN_MUTE_BLACK, 0, m_CurrentSrcType);
    }*/

#if(PIP_ENABLE == 1)
    EN_PIP_MODES enPipMode = MSrv_Control::GetInstance()->GetPipMode();

    if(enPipMode == E_PIP_MODE_OFF)
    {
        mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->Initialize(MAPI_INPUT_SOURCE_ATV, eWin);
    }
    else
    {
        if(eWin == MAPI_SUB_WINDOW)
        {
            mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stSubDispWin;
            MSrv_Control::GetInstance()->GetPipSubwindow(&stSubDispWin);
            mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->Initialize(MAPI_INPUT_SOURCE_ATV, eWin, &stSubDispWin);
        }
        else
        {
            mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->Initialize(MAPI_INPUT_SOURCE_ATV, eWin);
        }
    }
#else
    mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->Initialize(MAPI_INPUT_SOURCE_ATV, eWin);
#endif
    //Init Tuner and Vif(Demod)
    //Note: must init vif before VD, the flow is init vif->load VD code->VD MCU reset
    InitAtvDemodTuner();

    MS_Factory_NS_VD_SET VDNSValue;
    MSrv_Control::GetMSrvSystemDatabase()->GetFactoryExtSetting((&VDNSValue), EN_FACTORY_EXT_NSVD);
    MSrv_Control::GetMSrvFactoryMode()->SetFactoryVDInitParameter(&VDNSValue);
    mapi_interface::Get_mapi_vd()->InitVideoSystem();
    mapi_interface::Get_mapi_vd()->SetVideoSource(E_MAPI_INPUT_SOURCE_ATV);
    MSrv_Control::GetMSrvFactoryMode()->SetFactoryVDParameterAFECCF();
    MSrv_Control::GetMSrvFactoryMode()->SetFactoryVDParameter(&VDNSValue);

    mapi_interface::Get_mapi_vd()->SetSCWin(eWin);
#if (TV_FREQ_SHIFT_CLOCK)
    mapi_interface::Get_mapi_vd()->ResetTVShiftClk();
#endif


    mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetSourceType();

    //To Init Scaler
    m_pcVideo = mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV);
    //mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetVideoMute(TRUE, mapi_video_datatype::E_SCREEN_MUTE_BLACK, 0);
//    MSrv_Control::GetInstance()->SetVideoMute(TRUE, mapi_video_datatype::E_SCREEN_MUTE_BLACK, 0);
//    mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->Initialize(MAPI_INPUT_SOURCE_ATV, eWin);

    m_pcVideo->SetOverScanInfo(SystemInfo::GetInstance()->GetVideoWinInfo(E_CVBS));

    //Load overscan table from database
    //row
    m_pVptr = new(std::nothrow) ST_MAPI_VIDEO_WINDOW_INFO *[SIG_NUMS];
    ASSERT(m_pVptr);
    for(int i = 0 ; i < SIG_NUMS ; i++)
    {
        //column
        m_pVptr[i] = new(std::nothrow) ST_MAPI_VIDEO_WINDOW_INFO[mapi_video_datatype::E_AR_MAX];
        ASSERT(m_pVptr[i]);
    }
    SetOverscanFromDB(m_pVptr, TRUE);
    m_bIsScanning = FALSE;

    MEMBER_COUNTRY eCountry;
    eCountry = MSrv_Control::GetMSrvSystemDatabase()->GetSystemCountry();
    eCountry = eCountry;
#if (ATSC_SYSTEM_ENABLE == 1) // NTSC
    m_bIsMTSMonitorEnabled = FALSE;
    mScan = (MW_ATV_Scan *) new(std::nothrow) MW_ATV_Scan_NTSC;
    ASSERT(mScan);
#elif (ISDB_SYSTEM_ENABLE == 1)
    mScan = (MW_ATV_Scan *) new(std::nothrow) MW_ATV_Scan_Brazil;
    ASSERT(mScan);
#elif (CHINA_ATV_ENABLE == 1)  //Add for China ATV tuing 20110805EL
        mScan = (MW_ATV_Scan *) new(std::nothrow) MW_ATV_Scan_AsiaChina;
        ASSERT(mScan);
#elif (ESASIA_NTSC_SYSTEM_ENABLE == 1)  //Add for ES Asia/TW ATV tuing 20140526EL
        m_bIsMTSMonitorEnabled = FALSE;
        mScan = (MW_ATV_Scan *) new(std::nothrow) MW_ATV_Scan_ESAsia_NTSC;
        ASSERT(mScan);
#else
    if(eCountry == E_CHINA)
    {
        mScan = (MW_ATV_Scan *) new(std::nothrow) MW_ATV_Scan_AsiaChina;
        ASSERT(mScan);
    }
    else
    {
        mScan = (MW_ATV_Scan *) new(std::nothrow) MW_ATV_Scan_EU;
        ASSERT(mScan);
    }
#endif

    mScan->Init();


    ATV_PLAYER_IFO("---> MSrv_ATV_Player::Init-<3>.Init all the variables\n");
    m_threadSignalMonitor_id = 0;
    m_stAtvScannedInfo.u16ScannedChannelNum = 0;
    m_stAtvScannedInfo.u32FrequencyKHz = 42250;
    m_stAtvScannedInfo.u8Percent = 0;
    m_stAtvScanParam.u8ScanState = E_SCAN_STATE_STOP;

    //Init TTX
#if (TTX_ENABLE == 1)
    m_pcTTX = MW_TTX::GetInstance();
    ASSERT(m_pcTTX != NULL);
    #if (PIP_ENABLE == 1)
     if((FALSE == MSrv_Control::GetInstance()->IsPipModeEnable()) || (m_PipXCWin == MAPI_MAIN_WINDOW))
     #endif
    {
        m_pcTTX->KickOff();
        m_pcTTX->SetCountry(eCountry);
    }
#endif

    //Init Scart
#if (CVBSOUT_XCTOVE_ENABLE == 0)
#if (VE_ENABLE == 1 ||CVBSOUT_ENABLE==1)
    if(IsVideoOutFreeToUse() == TRUE)
    {
        if(mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_MONITOR_MODE)->IsDestTypeExistent(eWin))
        {
            mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_MONITOR_MODE)->SetToInternVIF(m_pDemodulator->ATV_IsInternalVIF(), eWin);
            mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_MONITOR_MODE)->Initialize(MAPI_INPUT_SOURCE_ATV, eWin);
            mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_MONITOR_MODE)->SetVideoMute(TRUE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, eWin);
        }

        //Set for Audio Mute for Scart Out
        //Turn On Scart Out Audio
        MSrv_Control_common::SetGpioDeviceStatus(SCART_OUT_1_MUTE, FALSE); //Temp;;need one enum name Mute_Scart_1, it will release by Alan
    }
#endif
#endif

    //for void threadSignalMonitor do mode change during init
    mapi_scope_lock(scopeLock, &m_mutex_ProgramChangeProcess);

    //To create Signal Monitor Thread
    if(m_bFlagThreadSignalMonitor_Active == FALSE)
    {
        m_bFlagThreadSignalMonitor_Active = TRUE;
        //m_u8VdUpdateInterval = 0;
        int intPTHChk;

        pthread_attr_t attr;
        pthread_attr_init(&attr);
        pthread_attr_setstacksize(&attr, PTHREAD_STACK_SIZE);
        intPTHChk = PTH_RET_CHK(pthread_create(&m_threadSignalMonitor_id, &attr, threadSignalMonitor, (void *) &m_bFlagThreadSignalMonitor_Active));

        if(intPTHChk != 0)
        {
            ATV_PLAYER_ERR("????? Create pthread threadSignalMonitor error!\n");
            m_bFlagThreadSignalMonitor_Active = FALSE;
            ASSERT(0);
            return(FALSE);
        }
    }
    else
    {
        ATV_PLAYER_IFO("????? MSrv_ATV_Player::Init --> threadSignalMonitor already existed...\n");
    }

#if 1//(ISDB_SYSTEM_ENABLE == 1)
    m_bIsPreProgramDisabled = FALSE;
#endif

    m_u32StartTime = mapi_time_utility::GetTime0();
    m_u8StartChangeProgram = 1;
    m_u8ScreenModeStatus = 0xff ;//reset screenmode status
    mapi_tuner* pTuner;
    pTuner = mapi_interface::Get_mapi_pcb()->GetAtvTuner(0);
    ASSERT(pTuner);

    pTuner->ExtendCommand(
        mapi_tuner_datatype::E_TUNER_SUBCMD_GET_UHF_MAX_FREQ,
        0,
        0,
        &u32UhfMaxFreq);
    pTuner->ExtendCommand(
        mapi_tuner_datatype::E_TUNER_SUBCMD_GET_UHF_MIN_FREQ,
        0,
        0,
        &u32UhfMinFreq);
    pTuner->ExtendCommand(
        mapi_tuner_datatype::E_TUNER_SUBCMD_GET_VHF_LOWMIN_FREQ,
        0,
        0,
        &u32VhfLowMinFreq);
    pTuner->ExtendCommand(
        mapi_tuner_datatype::E_TUNER_SUBCMD_GET_VHF_HIGHMIN_FREQ,
        0,
        0,
        &u32VhfHighMinFreq);
    pTuner->ExtendCommand(
        mapi_tuner_datatype::E_TUNER_SUBCMD_GET_VHF_HIGHMAX_FREQ,
        0,
        0,
        &u32VhfHighMaxFreq);
    pTuner->ExtendCommand(
        mapi_tuner_datatype::E_TUNER_SUBCMD_GET_VHF_LOWMAX_FREQ,
        0,
        0,
        &u32VhfLowMaxFreq);

    m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_FREQ_STEP, 0, 0, &enFreqStep);

    if(enFreqStep == E_FREQ_STEP_62_5KHz)
    {
        u32StepSize = 6250;
    }
    else if(enFreqStep == E_FREQ_STEP_50KHz)
    {
        u32StepSize = 5000;
    }
    else
    {
        u32StepSize = 3125;
    }
    m_u16UhfMaxPll = ((U16)(((u32UhfMaxFreq + 4000L) * 100) / u32StepSize));
    m_u16UhfMinPll = ((U16)(((u32VhfHighMaxFreq + ((u32UhfMinFreq - u32VhfHighMaxFreq) / 2)) * 100) / u32StepSize));
    m_u16VhfLowMinPll = ((U16)(((u32VhfLowMinFreq - 3000L) * 100) / u32StepSize));
    m_u16VhfHighMinPll = ((U16)(((u32VhfLowMaxFreq + ((u32VhfHighMinFreq - u32VhfLowMaxFreq) / 2)) * 100) / u32StepSize));
    m_u16DefaultPll = m_u16UhfMaxPll + 10;

    m_bInit = TRUE;

#if (HBBTV_ENABLE == 1)
    MW_HBBTV::GetInstance().SetCommandHandler(this);
#endif

    ATV_PLAYER_IFO("---> MSrv_ATV_Player::Init-Exit \n");
    return TRUE;
}

BOOL MSrv_ATV_Player::_Finalize()
{
    if(m_bInit == FALSE)
    {
        ASSERT(0);
    }
#if (CHANNEL_CHANGE_FREEZE_IMAGE_BYDFB_ENBALE == 1)
    if((mapi_interface::Get_mapi_video((MSrv_Control::GetInstance()->GetCurrentInputSource()))->IsFreezeImage()) == TRUE)
    {
       mapi_interface::Get_mapi_video((MSrv_Control::GetInstance()->GetCurrentInputSource()))->FreezeImage(FALSE);
    }
    if(IsShowFreezeImageByDFB() == TRUE)
    {
      ShowFreezeImageByDFB(FALSE);
    }
#endif

    void *thread_result;

    {
#if (ISDB_CC_ENABLE == 1)
        MW_CC::GetInstance()->StopCaption();
#elif ((ATSC_CC_ENABLE == 1)||(NTSC_CC_ENABLE == 1))
        MW_CC::GetInstance()->StopCaption();
        MW_CC::GetInstance()->DestoryCaption();
#endif
#if (VCHIP_ENABLE == 1)
        MSrv_Control::GetInstance()->VChipDisconnect();
#endif
    }

    mScan->Stop();
    if(m_bFlagThreadSignalMonitor_Active)
    {
        ATV_PLAYER_IFO("Start ThreadSignalMonitor join...\n");
        m_bFlagThreadSignalMonitor_Active = FALSE;

        int intPTHChk;
        intPTHChk = PTH_RET_CHK(pthread_join(m_threadSignalMonitor_id, &thread_result));
        if(intPTHChk != 0)
        {
            perror("threadSignalMonitor join failed");
            ASSERT(0);
        }
        else
        {
            ATV_PLAYER_IFO("Exit threadSignalMonitor Success.\n");
        }
    }


    if(mScan != NULL)
    {
        delete mScan;
        mScan = NULL;
    }



    DisableChannel();


    pthread_mutex_destroy(&m_mutex_ProgramChangeProcess);
    MSrv_Control::GetInstance()->SetVideoMute(TRUE, 0 , m_PipXCWin);

#if (STEREO_3D_ENABLE == 1)
#if (MSTAR_TVOS == 1)
 if (IsSignalStable() == TRUE)
 {
    if((MSrv_Control_common::GetMSrv3DManager())->GetCurrent3DFormat() != EN_3D_NONE)
    {
        (MSrv_Control_common::GetMSrv3DManager())->Enable3D(EN_3D_NONE);
    }
 }
#else
    Enable3D(FALSE, mapi_video_datatype::E_3D_INPUT_MODE_NONE, mapi_video_datatype::E_3D_OUTPUT_MODE_NONE, FALSE);
#endif
#endif
    MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_BYUSER_CH7_MUTEON_, m_CurrentSrcType);
    mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->Finalize();
    mapi_interface::Get_mapi_vd()->FinalizeVideoSystem();

#if (VE_ENABLE == 1 ||CVBSOUT_ENABLE==1)
#if (CVBSOUT_XCTOVE_ENABLE == 0)
    if(IsVideoOutFreeToUse() == TRUE)
    {
        if(mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_MONITOR_MODE)->IsDestTypeExistent(m_PipXCWin))
        {
            mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_MONITOR_MODE)->SetVideoMute(TRUE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, m_PipXCWin);
            mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_MONITOR_MODE)->Finalize();
        }
    }
#endif
    if (m_PipXCWin == MAPI_SUB_WINDOW)
    {
        FinalizeAtvDemodTuner();
    }
#else
    FinalizeAtvDemodTuner();
#endif

    for(int i = 0 ; i < SIG_NUMS ; i++)
    {
        if(m_pVptr[i] != NULL)
        {
            delete [] m_pVptr[i];
            m_pVptr[i] = NULL;
        }
    }
    if(m_pVptr != NULL)
    {
        delete [] m_pVptr;
        m_pVptr = NULL;
    }

    m_bInit = FALSE;

    SetCurrentProgramBlock(FALSE);

    MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_INTERNAL_1_MUTEOFF_, MAPI_INPUT_SOURCE_ATV);

    return TRUE;
}

#if (VE_ENABLE == 1 || CVBSOUT_ENABLE == 1)
BOOL MSrv_ATV_Player::InitVideoOut(MAPI_INPUT_SOURCE_TYPE eInputType, MAPI_SCALER_WIN eWin)
{
    if(mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_MONITOR_MODE)->IsDestTypeExistent(eWin))
    {
        mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_MONITOR_MODE)->SetToInternVIF(m_pDemodulator->ATV_IsInternalVIF(), eWin);
        mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_MONITOR_MODE)->Initialize(MAPI_INPUT_SOURCE_ATV, eWin);
        mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_MONITOR_MODE)->SetVideoMute(TRUE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, eWin);
    }

    //Set for Audio Mute for Scart Out
    //Turn On Scart Out Audio
    MSrv_Control_common::SetGpioDeviceStatus(SCART_OUT_1_MUTE, FALSE); //Temp;;need one enum name Mute_Scart_1, it will release by Alan
    return TRUE;
}

BOOL MSrv_ATV_Player::FinalizeVideoOut()
{
    if(mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_MONITOR_MODE)->IsDestTypeExistent(m_PipXCWin))
    {
        mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_MONITOR_MODE)->SetVideoMute(TRUE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, m_PipXCWin);
        mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_MONITOR_MODE)->Finalize();
    }
    return TRUE;
}
#endif

BOOL MSrv_ATV_Player::InitAtvDemodTuner(void)
{

    if(m_bInitAtvDemodTuner == TRUE)
    {
        // must init VIF again when change input source
        m_pDemodulator->ATV_VIF_Init();
        return TRUE;
    }
    else
    {


        mapi_interface::Get_mapi_vd()->SetToInternVIF(m_pDemodulator->ATV_IsInternalVIF());//?????

        //Init Demod
        m_pDemodulator->Connect(mapi_demodulator_datatype::E_DEVICE_DEMOD_ATV);
        m_pDemodulator->Power_On_Initialization();
        m_pDemodulator->ATV_VIF_Init();

        //Init Tuner
        mapi_interface::Get_mapi_pcb()->EnableTunerI2cPath(TRUE);
        m_pTuner->Connect();
        mapi_interface::Get_mapi_pcb()->EnableTunerI2cPath(FALSE);

        //Set program
        msAPI_FrontEnd_Init();

        //<3>.Init GPIO
        MSrv_Control_common::SetGpioDeviceStatus(EXT_RF_AGC, TRUE);
        MSrv_Control_common::SetGpioDeviceStatus(EXT_IF_AGC, FALSE);
        // MSrv_Control_common::SetGpioDeviceStatus(DVBT2_RESETZ, FALSE);

#if (CVBSOUT_XCTOVE_ENABLE == 0)
#if (VE_ENABLE == 1 ||CVBSOUT_ENABLE==1)
        if(IsVideoOutTVModeFreeToUse() == TRUE)
        {
#if (INPUT_SOURCE_LOCK_ENABLE == 1)
            //Check input source lock(ISL) status of current source
            if (MSrv_Control::GetInstance()->IsInputSourceLock(m_CurrentSrcType) == MAPI_FALSE)
#endif
            {
                //Original unmute flow without input source lock(ISL) status checking
                mapi_video_out *pVideoOut = mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_TV_MODE);
                if((pVideoOut != NULL) && (pVideoOut->IsDestTypeExistent(m_PipXCWin)))
                {
                    // For CVBS-Out-2, we only need to init once. When finalize-ATV_Player, we shpuld not finalize it. Because when source = AV, CVBS-Out-2 = ATV.
                    pVideoOut->SetToInternVIF(m_pDemodulator->ATV_IsInternalVIF(), m_PipXCWin);
#if (PIP_ENABLE == 1)
                    pVideoOut->Initialize(MAPI_INPUT_SOURCE_ATV, m_PipXCWin);
#else
                    pVideoOut->Initialize(MAPI_INPUT_SOURCE_ATV, MAPI_MAIN_WINDOW);
#endif
                    pVideoOut->SetVideoMute(FALSE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, m_PipXCWin);
                }
                //Set for Audio Mute
                MSrv_Control_common::SetGpioDeviceStatus(SCART_OUT_1_MUTE, FALSE); //Turn On Scart Out Audio
            }
        }
#endif
#endif

        m_bInitAtvDemodTuner = TRUE;
    }
    return TRUE;
}

BOOL MSrv_ATV_Player::FinalizeAtvDemodTuner(void)
{
    if(m_bInitAtvDemodTuner == TRUE)
    {
        m_pTuner->Disconnect();
        m_pDemodulator->Disconnect();
        m_bInitAtvDemodTuner = FALSE;

#if (CVBSOUT_XCTOVE_ENABLE == 0)
#if (VE_ENABLE == 1 ||CVBSOUT_ENABLE==1)
        //To mute video path for scart out
        mapi_video_out *pVideoOut = mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_TV_MODE);
        if(IsVideoOutTVModeFreeToUse() && (pVideoOut->IsDestTypeExistent(m_PipXCWin)))
        {
            pVideoOut->SetVideoMute(TRUE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, m_PipXCWin);
            if(pVideoOut->IsActive() == MAPI_TRUE)
            {
                pVideoOut->Finalize(m_PipXCWin);
            }
        }
#endif
#endif
    }

    return TRUE;
}

void MSrv_ATV_Player::GetVideoInfo(ST_VIDEO_INFO *pVideoInfo)
{
    MAPI_AVD_VideoStandardType enVideoStandard;
    enVideoStandard = (MAPI_AVD_VideoStandardType)GetVideoStandard();
    pVideoInfo->u8ModeIndex = 0xFF;

    switch(enVideoStandard)
    {
        case E_MAPI_VIDEOSTANDARD_NTSC_M:
        case E_MAPI_VIDEOSTANDARD_NTSC_44:
        case E_MAPI_VIDEOSTANDARD_PAL_60:
            //u16TempID = en_str_resolution_480i;
            pVideoInfo->u16HResolution = 720;
            pVideoInfo->u16VResolution = 480;
            pVideoInfo->u16FrameRate = 60;
            pVideoInfo->enScanType = E_INTERLACED;
            break;
        case E_MAPI_VIDEOSTANDARD_PAL_BGHI:
        case E_MAPI_VIDEOSTANDARD_PAL_M:
        case E_MAPI_VIDEOSTANDARD_PAL_N:
        case E_MAPI_VIDEOSTANDARD_SECAM:
            //u16TempID = en_str_resolution_576i;
            pVideoInfo->u16HResolution = 720;
            pVideoInfo->u16VResolution = 576;
            pVideoInfo->u16FrameRate = 50;
            pVideoInfo->enScanType = E_INTERLACED;
            break;
        default:
            break;
    }
}

BOOL MSrv_ATV_Player::SetAutoTuningStart(U32 u32EventInterval, U32 u32FrequencyStart, U32 u32FrequencyEnd,eAutoScanState eScanState)
{
#ifdef SIM_BUILD
    return TRUE;
#endif
    m_bIsScanning = TRUE;

    MEMBER_COUNTRY eCountry;
    eCountry = MSrv_Control::GetMSrvSystemDatabase()->GetSystemCountry();
    switch(eCountry)
    {
        case E_FRANCE:
            msAPI_Tuning_IsScanL(TRUE);
            break;
        default:
            msAPI_Tuning_IsScanL(FALSE);
            break;
    }

    if(m_bInit == FALSE) //Not In ATV source
    {
        return FALSE;
    }
    else
    {
        m_u8StartChangeProgram = 0;
        EnableScaler();
    }

    if(mScan->IsScanning() == FALSE)
    {
        mapi_scope_lock(scopeLock, &m_mutex_ProgramChangeProcess);
        ATV_PLAYER_IFO("---> SetAutoTuningStart...\n");

#if (AUTO_TEST == 1)
        printf("[AT][SN][ATV_AutoScan Function][%u]\n", mapi_time_utility::GetTime0());
#endif

        //Set Audio Mute before starting AutoScan
        //MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_PERMANENT_MUTEON_);

        /* for keeping freq stable */
        mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->XCSetFreerun();

        MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_SCAN_MUTEON_, m_CurrentSrcType);
    #if (ENABLE_ATV_NOSINGAL_BLACKSCREEN==1)
        MSrv_Control::GetInstance()->SetVideoMute(TRUE, 0 , m_PipXCWin);
    #else
        MSrv_Control::GetInstance()->SetVideoMute(FALSE, 0 , m_PipXCWin);
    #endif

#if (TV_FREQ_SHIFT_CLOCK)
        msAPI_Tuner_Patch_TVShiftClk(FALSE);
#endif
        usleep(800000);
        //!for bug: under dtv source ,auto search atv still black screen ;must use usleep ,otherwise cant unlock screen.-tongzhao.wang

        //To Init DataBase
        BOOL bSmartScan;
        GetSmartScanMode(&bSmartScan);
        if(FALSE==bSmartScan)
        {
#if (ISDB_SYSTEM_ENABLE == 1)
            MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER , MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CHANNEL_MIN, 0, 0, NULL) - 1 , 0 , NULL);
#else
            MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER , ATV_FIRST_PR_NUM , 0 , NULL);
#endif
            MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(RESET_CHANNEL_DATA , 0 , 0 , NULL);
        }
        else
        {
            MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER , MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_ACTIVE_PROGRAM_COUNT, 0, 0, NULL)  , 0 , NULL);
        }

        //To Init Variable
        m_stAtvScanParam.u32EventInterval = u32EventInterval;
        m_stAtvScanParam.u32StartFrequency = u32FrequencyStart;
        m_stAtvScanParam.u32EndFrequency = u32FrequencyEnd;
        m_stAtvScanParam.u8ScanType = E_SCAN_AUTO_TUNING;
        m_stAtvScanParam.u16TotalChannelNum = 0;
        m_stAtvScanParam.u8ScanState = E_SCAN_STATE_RUNNING;
        m_stAtvScanParam.aATVMannualTuneMode = E_MANUAL_TUNE_MODE_UNDEFINE;

        m_stAtvScannedInfo.u8Percent = 0 ;
        m_stAtvScannedInfo.u32FrequencyKHz = 0;
        m_stAtvScannedInfo.u16ScannedChannelNum = 0;

        //To Set the Tuning State
        ATV_PLAYER_IFO("start mScan\n");
        if(eScanState == E_AUTO_SCAN_ALL)
            mScan->m_AutoScanState = AUTO_SCAN_ALL;
        else
            mScan->m_AutoScanState = AUTO_SCAN_ATV;
        mScan->m_bIsFranceSearch = m_bIsLSearch;
        //mScan->ATV_Scan_Start(u32FrequencyStart,(U32)460000);
        m_pTuner->SetTunerInScanMode(MAPI_TRUE);
        mScan->Start(u32FrequencyStart, u32FrequencyEnd);

        if(m_bChannelChangeFreezeImageFlags == TRUE)
        {
            if((m_pcVideo->IsFreezeImage()) == TRUE)
                m_pcVideo->FreezeImage(FALSE);
        }
    #if (ENABLE_ATV_NOSINGAL_BLACKSCREEN==1)
        MSrv_Control::GetInstance()->SetVideoMute(TRUE, 0 , m_PipXCWin);
    #else
        MSrv_Control::GetInstance()->SetVideoMute(FALSE, 0, m_PipXCWin);
    #endif
#if (HBBTV_ENABLE == 1)
        if(MW_HBBTV::GetCheckedInstance())
        {
            MW_HBBTV::GetCheckedInstance()->StopHBBTVService();
        }
#endif

#if (TTX_ENABLE == 1)
    #if (PIP_ENABLE == 1)
     if((FALSE == MSrv_Control::GetInstance()->IsPipModeEnable()) || (m_PipXCWin == MAPI_MAIN_WINDOW))
    #endif
    {
        if(m_pcTTX->Connect(MAPI_INPUT_SOURCE_ATV) == FALSE)
        {
            ATV_PLAYER_ERR("Cannot create thread for TTX background handling\n");
        }
        m_pcTTX->SetCountry(eCountry);
    }
#endif

        //Installation Guide
        BOOL bFlag;
        bFlag = FALSE;
        MSrv_Control::GetMSrvSystemDatabase()->SetInstallationguideEnabled(&bFlag);

    }
    else
    {
        ATV_PLAYER_IFO("????? MSrv_ATV_Player::Init --> thread MW ATV Scan already existed...\n");
    }

    return TRUE;
}

#if ((ATSC_SYSTEM_ENABLE == 1)||(ISDB_SYSTEM_ENABLE == 1)||(ESASIA_NTSC_SYSTEM_ENABLE == 1))
void MSrv_ATV_Player::NTSCStartDirectTune(U16 u16MajorNum, U16 u16MinorNum)
{
    ST_ATV_MISC Misc;
    U32 u32CurrentFrequency;

    if(MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(IS_DIRECT_TUNED, u16MajorNum , 0, (U8 *)&Misc))
    {
        return;
    }
#if (TV_FREQ_SHIFT_CLOCK)
    msAPI_Tuner_Patch_TVShiftClk(FALSE);
#endif

    mScan ->m_u8AutoScanChannelStart = u16MajorNum;
    mScan ->m_u8AutoScanChannelEnd = u16MajorNum;
    mScan ->m_u8AutoScanChannel = u16MajorNum;
#if (ISDB_SYSTEM_ENABLE == 1)
    m_stAtvScannedInfo.u16ScannedChannelNum = 0;
#endif
    GetCurrentFrequency(&u32CurrentFrequency);
    mScan ->StartManualScan(u32CurrentFrequency , SearchUp);
}
#endif

BOOL MSrv_ATV_Player::SetAutoTuningPause()
{
#ifdef SIM_BUILD
    return TRUE;
#endif
    if(m_bInit == FALSE) //Not In ATV source
    {
        return FALSE;
    }
    mScan->Pause();

    return TRUE;
}

BOOL MSrv_ATV_Player::SetAutoTuningResume()
{
#ifdef SIM_BUILD
    return TRUE;
#endif
    if(m_bInit == FALSE) //Not In ATV source
    {
        return FALSE;
    }

    mScan->Resume();

    return TRUE;
}

BOOL MSrv_ATV_Player::SetAutoTuningEnd()
{
#ifdef SIM_BUILD
    return TRUE;
#endif
    if(m_bInit == FALSE) //Not In ATV source
    {
        return FALSE;
    }

    //To stop the search
    mScan->Stop();
    m_pTuner->SetTunerInScanMode(MAPI_FALSE);

#if (ISDB_SYSTEM_ENABLE == 1)
    MSrv_ChannelManager_ISDB* pCM = dynamic_cast<MSrv_ChannelManager_ISDB*>(MSrv_Control_common::GetMSrvChannelManager());
    ASSERT(pCM);
    pCM->GenMixProgList(FALSE);
#endif
    m_bIsScanning = FALSE;
#if (ATSC_SYSTEM_ENABLE == 1)
    U32 size;
    MSrv_ChannelManager_ATSC *pCMCtrl = dynamic_cast<MSrv_ChannelManager_ATSC *>(MSrv_Control_common::GetMSrvChannelManager());
    ASSERT(pCMCtrl);
    pCMCtrl->GenMainList(FALSE);
    pCMCtrl->GetMLSize(size);
    if(size == 0)
    {
        PostEvent(0, EV_SCREEN_SAVER_MODE, (U32)MSRV_ATV_SS_NO_CHANNEL);
    }
    else
    {
        PostEvent(0, EV_SCREEN_SAVER_MODE, (U32)MSRV_ATV_SS_NORMAL);
    }
#endif
    return TRUE;
}

BOOL MSrv_ATV_Player::SetManualTuningStart(U32 u32EventIntervalMs, U32 u32Frequency, eAtvManualTuneMode  eMode)
{
#ifdef SIM_BUILD
    return TRUE;
#endif

    if((E_MANUAL_TUNE_MODE_SEARCH_ONE_TO_UP == eMode) || (E_MANUAL_TUNE_MODE_SEARCH_ONE_TO_DOWN == eMode))
    {
    m_bIsScanning = TRUE;
#if (TV_FREQ_SHIFT_CLOCK)
    msAPI_Tuner_Patch_TVShiftClk(FALSE);
#endif
    }

    //To create Thread-B for sending Scan Info.
    //int intPTHChk;
    //U16 u16InputPLL;

     MEMBER_COUNTRY eCountry;
     eCountry = MSrv_Control::GetMSrvSystemDatabase()->GetSystemCountry();
     if(eCountry == E_FRANCE)
     {
         msAPI_Tuning_IsScanL(TRUE);
     }
     else
     {
         msAPI_Tuning_IsScanL(FALSE);
     }

    if(m_bInit == FALSE) //Not In ATV source
    {
        return FALSE;
    }
    else
    {
        m_u8StartChangeProgram = 0;
        EnableScaler();
    }


    /* for keeping freq stable */
    mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->XCSetFreerun();
#if (ENABLE_ATV_NOSINGAL_BLACKSCREEN==1)
    MSrv_Control::GetInstance()->SetVideoMute(TRUE, 0, m_PipXCWin);
#endif

#if (TTX_ENABLE == 1)
    #if (PIP_ENABLE == 1)
     if((FALSE == MSrv_Control::GetInstance()->IsPipModeEnable()) || (m_PipXCWin == MAPI_MAIN_WINDOW))
    #endif
     {
        if(m_pcTTX->Connect(MAPI_INPUT_SOURCE_ATV) == FALSE)
        {
            ATV_PLAYER_ERR("Cannot create thread for TTX background handling\n");
        }
     }
#endif

    switch(eMode)
    {
        case E_MANUAL_TUNE_MODE_FINE_TUNE_ONE_FREQUENCY:
            _SetTunerPLL(((u32Frequency * 10) / 625));//FIXME
            return TRUE;
            break;
        case E_MANUAL_TUNE_MODE_FINE_TUNE_UP:
#if 1
            MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_SIGNAL_UNSTABLE_MUTEON_, m_CurrentSrcType);
            msAPI_Tuner_AdjustUnlimitedFineTune(DIRECTION_UP);
            usleep(100 * 1000);
            MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_SIGNAL_UNSTABLE_MUTEOFF_, m_CurrentSrcType);
#else
            ATV_PLAYER_IFO("QQQ-1: u32Frequency = %d \n", (int)u32Frequency); //Temp;;Please remove;;
            u16InputPLL = ((u32Frequency * 100 / 625) + 5) / 10; //FIXME
            ATV_PLAYER_IFO("QQQ-2: u16InputPLL = %d \n", (int)u16InputPLL); //Temp;;Please remove;;
            u16InputPLL += FINE_TUNE_STEP;
            if(u16InputPLL > m_u16UhfMaxPll)
                u16InputPLL = m_u16VhfLowMinPll;
            ATV_PLAYER_IFO("QQQ-3: u16InputPLL = %d \n", (int)u16InputPLL); //Temp;;Please remove;;
            _SetTunerPLL(u16InputPLL);
#endif
            ATV_PLAYER_IFO("\n_ATV_ManualScan_:Freqoffset=%x!\n", m_pDemodulator->ATV_GetAFC_Distance());
            return TRUE;
            break;
        case E_MANUAL_TUNE_MODE_FINE_TUNE_DOWN:
#if 1
            MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_SIGNAL_UNSTABLE_MUTEON_, m_CurrentSrcType);
            msAPI_Tuner_AdjustUnlimitedFineTune(DIRECTION_DOWN);
            usleep(100 * 1000);
            MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_SIGNAL_UNSTABLE_MUTEOFF_, m_CurrentSrcType);
#else
            ATV_PLAYER_IFO("QQQ-4: u32Frequency = %d \n", (int)u32Frequency); //Temp;;Please remove;;
            u16InputPLL = ((u32Frequency * 100 / 625) + 5) / 10; //FIXME
            ATV_PLAYER_IFO("QQQ-5: u16InputPLL = %d \n", (int)u16InputPLL); //Temp;;Please remove;;
            u16InputPLL -= FINE_TUNE_STEP;
            if(u16InputPLL < m_u16VhfLowMinPll)
                u16InputPLL = m_u16UhfMaxPll;
            ATV_PLAYER_IFO("QQQ-6: u16InputPLL = %d \n", (int)u16InputPLL); //Temp;;Please remove;;
            _SetTunerPLL(u16InputPLL);
#endif

            ATV_PLAYER_IFO("\n_ATV_ManualScan_:Freqoffset=%x!\n", m_pDemodulator->ATV_GetAFC_Distance());
            return TRUE;
            break;
        case E_MANUAL_TUNE_MODE_SEARCH_ONE_TO_UP:
        case E_MANUAL_TUNE_MODE_SEARCH_ONE_TO_DOWN:
            if(mScan->IsScanning() == FALSE)
            {
                mapi_scope_lock(scopeLock, &m_mutex_ProgramChangeProcess);
                m_ManualScanRtUpdateVDandScaler = 1;
                //To Init Variable
                m_stAtvScanParam.u32EventInterval = u32EventIntervalMs;
                m_stAtvScanParam.u32StartFrequency = u32Frequency;
                m_stAtvScanParam.u32EndFrequency = 0;    //Do not need it.
                m_stAtvScanParam.u8ScanType = E_SCAN_MANUAL_TUNING;
                m_stAtvScanParam.u16TotalChannelNum = 0;
                m_stAtvScanParam.u8ScanState = E_SCAN_STATE_RUNNING;
                m_stAtvScanParam.aATVMannualTuneMode = eMode;

                m_stAtvScannedInfo.u8Percent = 0 ;
                m_stAtvScannedInfo.u32FrequencyKHz = 0;
                m_stAtvScannedInfo.u16ScannedChannelNum = 0;

                MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_SCAN_MUTEON_, m_CurrentSrcType);
                //To Set the Tuning State

                mScan->m_bIsFranceSearch = m_bIsLSearch;
                m_pTuner->SetTunerInScanMode(MAPI_TRUE);
                if(eMode == E_MANUAL_TUNE_MODE_SEARCH_ONE_TO_UP)
                {

                    mScan->StartManualScan(u32Frequency, SearchUp);

                }
                else if(eMode == E_MANUAL_TUNE_MODE_SEARCH_ONE_TO_DOWN)
                {
                    mScan->StartManualScan(u32Frequency, SearchDown);
                }

                if(m_bChannelChangeFreezeImageFlags == TRUE)
                {
                    if((m_pcVideo->IsFreezeImage()) == TRUE)
                    {
                        m_pcVideo->FreezeImage(FALSE);
                    }
            #if (ENABLE_ATV_NOSINGAL_BLACKSCREEN==1)
                 if(TRUE == IsSignalStable())
            #endif
                    MSrv_Control::GetInstance()->SetVideoMute(FALSE, 0, m_PipXCWin);
                }
                return TRUE;
            }
            else
            {
                ATV_PLAYER_IFO("????? MSrv_ATV_Player::Init --> threadi ATV Scan already existed...\n");
            }
            return FALSE;
            break;
        default:
            ATV_PLAYER_IFO("????? MSrv_ATV_Player::eMode not support...\n");
            break;
    }
    return FALSE;
}

void MSrv_ATV_Player::SetManualTuningEnd()
{
#ifdef SIM_BUILD
    return TRUE;
#else
    m_ManualScanRtUpdateVDandScaler = 1;
    mScan->Stop();
    m_pTuner->SetTunerInScanMode(MAPI_FALSE);
    m_bIsScanning = FALSE;

    MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_SCAN_MUTEOFF_, m_CurrentSrcType);
    //m_eChannelSearchType=E_CHANNEL_SEARCH_NONE;
    SetChannelSearchType(E_CHANNEL_SEARCH_NONE);
#if (ISDB_SYSTEM_ENABLE == 1)
    MSrv_ChannelManager_ISDB* pCM = dynamic_cast<MSrv_ChannelManager_ISDB*>(MSrv_Control_common::GetMSrvChannelManager());
    ASSERT(pCM);
    pCM->GenMixProgList(FALSE);
#endif
    return;
#endif
}

BOOL MSrv_ATV_Player::GetCurrentFrequency(U32 * pu32Frequency)
{
#ifdef SIM_BUILD
    return TRUE;
#else
    if(!m_u16TunerPLL)
    {
        _SetTunerPLL(MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_PROGRAM_PLL_DATA, MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER , 0, 0, NULL), 0, NULL));
    }
    *pu32Frequency = ConvertPLLtoFrequencyKHZ(m_u16TunerPLL);
    ATV_PLAYER_IFO("--------------------> m_u16TunerPLL = %d\n", (int)m_u16TunerPLL);
    ATV_PLAYER_IFO("--------------------> pu32Frequency = %d\n", (int)ConvertPLLtoFrequencyKHZ(m_u16TunerPLL));
    return TRUE;
#endif
}

BOOL MSrv_ATV_Player::SetFrequency(U32 u32Frequency)
{
#ifdef SIM_BUILD
    return TRUE;
#else
    if(m_bInit == FALSE) //Not In ATV source
    {
        return FALSE;
    }

    U16 u16PLL = ConvertFrequncyHzToPLL(u32Frequency);
    _SetTunerPLL(u16PLL);
    return TRUE;
#endif
}

BOOL MSrv_ATV_Player::GetCurrentChannelNumber(U16 *  pu16ChannelNum)
{
#ifdef SIM_BUILD
    return TRUE;
#else
    *pu16ChannelNum = (U16)MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL);
    return TRUE;
#endif
}

BOOL MSrv_ATV_Player::DisableChannel(void)
{
    if(m_bCurrentProgramBlock == FALSE)
    {
        MSrv_Control::GetMSrvAtv()->PostEvent(NULL, EV_POPUP_DIALOG, 1, 0);
    }
    if(m_bIsPreProgramDisabled == TRUE)
    {
        return TRUE;
    }
#if (OFL_DET == 1)
    MSrv_Control::GetMSrvSourceDetect()->SetForbidDetectionFlag(TRUE);
#endif

#if (MWE_ENABLE == 1)
    if(MSrv_MWE::GetInstance()->GetMWEStatus() != MSrv_MWE::E_EN_MS_MWE_OFF)
    {
        MSrv_MWE::GetInstance()->SetMWEStatus(MSrv_MWE::E_EN_MS_MWE_OFF);
    }
#endif

    //Unlock UI lock page
    //MSrv_Control::GetMSrvAtv()->PostEvent(NULL, EV_POPUP_DIALOG, 1, 0);

#if (TTX_ENABLE == 1)
    #if (PIP_ENABLE == 1)
     if((FALSE == MSrv_Control::GetInstance()->IsPipModeEnable()) || (m_PipXCWin == MAPI_MAIN_WINDOW))
    #endif
    {
        MW_TTX_STATUS ttx_status;
        m_pcTTX->GetStatus(&ttx_status);

        if(ttx_status.bOpen)
        {
            // if TTX is opened, close it!
            m_pcTTX->Close();
        }

        if(ttx_status.bConnect)
        {
            #if (NTSC_CC_ENABLE == 1)
            MW_TTX::GetInstance()->Wakeup();
            printf("Wakeup the TTX task !!!\n");
            #endif

            // close TTX background data handling
            m_pcTTX->Disconnect();
        }
    }
#endif

    {
#if (ISDB_CC_ENABLE == 1)
        MW_CC::GetInstance()->StopCaption();
#elif ((ATSC_CC_ENABLE == 1)||(NTSC_CC_ENABLE == 1))
        MW_CC::GetInstance()->StopCaption();
        MW_CC::GetInstance()->DestoryCaption();
#endif
#if (VCHIP_ENABLE == 1)
        MSrv_Control::GetInstance()->VChipDisconnect();
#endif
    }

    //To Set the MTS Audio to Mono
    MSrv_Control::GetMSrvSSSound()->GetMtsMode();

    //To mute the Screen
#if (CHANNEL_CHANGE_FREEZE_IMAGE_BYDFB_ENBALE == 1)
#if (STEREO_3D_ENABLE == 1)
    if((m_bChannelChangeFreezeImageFlags == FALSE) || (MSrv_Control::GetMSrv3DManager()->GetCurrent3DFormat() == EN_3D_DUALVIEW))
#else
    if((m_bChannelChangeFreezeImageFlags == FALSE))
#endif  //(STEREO_3D_ENABLE == 1)
#else
    if((m_bChannelChangeFreezeImageFlags == FALSE) || (IsDiffVideoStandByChannelAndVD() == TRUE)
#if (STEREO_3D_ENABLE == 1)
        || (MSrv_Control::GetMSrv3DManager()->GetCurrent3DFormat() != EN_3D_NONE)
#endif
        )
#endif
    {
        MSrv_Control::GetInstance()->SetVideoMute(TRUE, 0, m_PipXCWin);
    }
#if (STEREO_3D_ENABLE == 1)
 if (IsSignalStable() == TRUE)
 {
    MSrv_Control::GetMSrv3DManager()->ChannelChangeHandler();
 }
#endif

    //To mute the Audio
    //mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_PERMANENT_MUTEON_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
    //MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_PERMANENT_MUTEON_);
    //To Set Scart Out Mute
    //temp solution for switch input source
    //mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_MONITOR_MODE)->SetVideoMute(TRUE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN);
    //mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_TV_MODE)->SetVideoMute(TRUE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN);
    //To check the freeze status
    //m_bIsPreProgramDisabled = TRUE;
#if (ENABLE_V_RANGE_HANDLE_ATV)
    MApp_VD_RangeReset();
#endif

    /*Add for keep freq stable */
    mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->XCSetFreerun();

    return TRUE;
}

BOOL MSrv_ATV_Player::EnableChannel(void)
{
    if(m_bIsPreProgramDisabled == FALSE)
    {
        return FALSE;
    }
#if (VCHIP_ENABLE == 1)
        MSrv_Control::GetInstance()->VChipConnect();
#endif
    //To un-mute video
    if(m_bChannelChangeFreezeImageFlags == TRUE)
    {
        if((m_pcVideo->IsFreezeImage()) == TRUE)
            m_pcVideo->FreezeImage(FALSE);

        if(IsSignalStable() == TRUE)
        {
            printf("***11******SignalStable True**********\n");
            MSrv_Control::GetInstance()->SetVideoMute(FALSE, 0, m_PipXCWin);
            //MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_BYSYNC_MUTEOFF_, m_CurrentSrcType);
        }
        else
        {
            printf("***22******SignalStable False**********\n");
        #if (ENABLE_ATV_NOSINGAL_BLACKSCREEN==1)
            MSrv_Control::GetInstance()->SetVideoMute(TRUE, 0, m_PipXCWin);
        #else
            MSrv_Control::GetInstance()->SetVideoMute(FALSE, 0, m_PipXCWin);
        #endif
            //MSrv_Control::GetInstance()->SetVideoMute(TRUE, mapi_video_datatype::E_SCREEN_MUTE_BLACK, 0);
            //MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_BYSYNC_MUTEON_, m_CurrentSrcType);
        }
        #if (CHANNEL_CHANGE_FREEZE_IMAGE_BYDFB_ENBALE == 1)||(ADVERT_BOOTING_ENABLE == 1)
        if(((IsShowFreezeImageByDFB()) == TRUE))
        {
            ShowFreezeImageByDFB(FALSE);
        }
        #endif

        //project DVB should keep mts mode
#if ((ISDB_SYSTEM_ENABLE != 1) && (ATSC_SYSTEM_ENABLE != 1))
        //use interval 1 mute flag until RecoverUserMtsSetting
    #if(ESASIA_NTSC_SYSTEM_ENABLE != 1)
        MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_INTERNAL_1_MUTEON_, m_CurrentSrcType);
    #endif
#endif

        if((mapi_interface::Get_mapi_vd()->IsSyncLocked() == TRUE) && (msAPI_Tuner_IsTuningProcessorBusy() == false))
        {
            MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_SCAN_MUTEOFF_, m_CurrentSrcType);
        }
#if (PIP_ENABLE == 1)
        MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_BYUSER_CH7_MUTEOFF_, m_CurrentSrcType);
#endif
    }
    else
    {
        MS_USER_SYSTEM_SETTING stGetSystemSetting;
        MSrv_Control::GetMSrvSystemDatabase()->GetUserSystemSetting(&stGetSystemSetting);

        if(stGetSystemSetting.AudioOnly == FALSE)
        {
        #if (ENABLE_ATV_NOSINGAL_BLACKSCREEN==1)
            if(IsSignalStable() == TRUE)
            {
                MSrv_Control::GetInstance()->SetVideoMute(FALSE, 200, m_PipXCWin);
            }
            else
            {
                MSrv_Control::GetInstance()->SetVideoMute(TRUE, 200, m_PipXCWin);
            }
        #else
        #if (PIP_ENABLE == 1)
        if((TRUE == MSrv_Control::GetInstance()->IsPipModeEnable()) && (m_PipXCWin == MAPI_SUB_WINDOW))
        {
            MSrv_Control::GetInstance()->SetVideoMute(FALSE, 200, m_PipXCWin);
        }
        else
        #endif
        {
            MSrv_Control::GetInstance()->SetVideoMute(FALSE, 40, m_PipXCWin);   // add 40ms delay to avoid VIF unstable transient during changing channel
        }
        #endif
#if (AUTO_TEST == 1)
            printf("\033[0;35m [AUTO_TEST][ATV channel change][%u] : Unmute \033[0m\n", mapi_time_utility::GetTime0());
#endif
        }

        //project DVB should keep mts mode
#if ((ISDB_SYSTEM_ENABLE != 1) && (ATSC_SYSTEM_ENABLE != 1))
        //use interval 1 mute flag until RecoverUserMtsSetting
    #if(ESASIA_NTSC_SYSTEM_ENABLE != 1)
        MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_INTERNAL_1_MUTEON_, m_CurrentSrcType);
    #endif
#endif

        //To un-mute audio
        MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_SCAN_MUTEOFF_, m_CurrentSrcType);
    }

    //To Set Scart Out Mute = Off
#if (CVBSOUT_XCTOVE_ENABLE == 0)
#if (STB_ENABLE == 0)
#if (VE_ENABLE == 1 ||CVBSOUT_ENABLE==1) //When ATV Ch up/down, Scart need to mute, if not we will see the garbage
    if(IsVideoOutFreeToUse() == TRUE)
    {
#if (INPUT_SOURCE_LOCK_ENABLE == 1)
        //Check input source lock(ISL) status of current source
        if (MSrv_Control::GetInstance()->IsInputSourceLock(m_CurrentSrcType) == MAPI_FALSE)
#endif
        {
            //Original unmute flow without input source lock(ISL) status checking
            if(mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_MONITOR_MODE)->IsDestTypeExistent(m_PipXCWin))
            {
                mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_MONITOR_MODE)->SetVideoMute(FALSE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, m_PipXCWin);
            }

            if((mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_TV_MODE)->IsDestTypeExistent(m_PipXCWin)) && IsVideoOutTVModeFreeToUse())
            {
                mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_TV_MODE)->SetVideoMute(FALSE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, m_PipXCWin);
            }
        }
    }
#endif
#endif
#endif

#if (TTX_ENABLE == 1)
    #if (PIP_ENABLE == 1)
     if((FALSE == MSrv_Control::GetInstance()->IsPipModeEnable()) || (m_PipXCWin == MAPI_MAIN_WINDOW))
    #endif
    {
        if(m_pcTTX->Connect(MAPI_INPUT_SOURCE_ATV) == FALSE)
        {
            ATV_PLAYER_ERR("Cannot create thread for TTX background handling\n");
        }
#if (NTSC_CC_ENABLE == 1)
        else
        {
            m_pcTTX->Suspend();
            printf("Suspend the TTX task !!!\n");
        }
#endif
    }
#endif

    {
#if (ISDB_CC_ENABLE == 1)
        MW_CC::GetInstance()->StartCaption(E_NORMAL_CAPTION);
#elif ((ATSC_CC_ENABLE == 1)||(NTSC_CC_ENABLE == 1))
        MW_CC::GetInstance()->StartVchip();
        MW_CC::GetInstance()->StartCaption();
#endif
    }
    m_bIsPreProgramDisabled = FALSE;

#if ((ATSC_SYSTEM_ENABLE == 1) || (ESASIA_NTSC_SYSTEM_ENABLE == 1))
    m_bIsMTSMonitorEnabled = FALSE;
#endif

#if (OFL_DET == 1)
    MSrv_Control::GetMSrvSourceDetect()->SetForbidDetectionFlag(FALSE);
#endif

    //enable the timer of postponing audio unmute
    m_u32DelayAudioUnmuteTime = mapi_time_utility::GetTime0();

    m_u32UserMtsSettingIntervalTime = mapi_time_utility::GetTime0();
    return TRUE;
}

BOOL MSrv_ATV_Player::CheckAudioStandardChange(void)
{
    U16 u16RtnChannel = 0;
    U16 u16RtnAudioStandard = 0;
    U16 u16CurrentChannel = 0;
    U16 u16CurrentAudioStandard = 0;

    u16RtnChannel = MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_PAST_PROGRAM_NUMBER , 0, 0, NULL);
    u16RtnAudioStandard = MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_AUDIO_STANDARD , u16RtnChannel, 0, NULL);

    MSrv_Control::GetMSrvAtv()->GetCurrentChannelNumber(&u16CurrentChannel);
    u16CurrentAudioStandard = MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_AUDIO_STANDARD , u16CurrentChannel, 0, NULL);

    //DK -> M or I -> M or BG -> M, return TRUE.
    //Otherwise, return false.
    if((  (u16RtnAudioStandard==E_AUDIOSTANDARD_DK) && (u16CurrentAudioStandard==E_AUDIOSTANDARD_M) ) ||
      ( (u16RtnAudioStandard==E_AUDIOSTANDARD_I) && (u16CurrentAudioStandard==E_AUDIOSTANDARD_M) ) ||
      ( (u16RtnAudioStandard==E_AUDIOSTANDARD_BG) && (u16CurrentAudioStandard==E_AUDIOSTANDARD_M) ))
    {
        return TRUE;
    }

    return FALSE;

}

BOOL MSrv_ATV_Player::GetTotalChannelNumber(U16 *  pu16TotalChannelNum)
{
#ifdef SIM_BUILD
    *pu16TotalChannelNum = 0;

    return TRUE;
#else
    *pu16TotalChannelNum = 0;

#if (ISDB_SYSTEM_ENABLE == 1)
    for(U8 u8i = (MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CHANNEL_MIN , 0, 0, NULL) - 1) ; u8i <= (MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CHANNEL_MAX , 0, 0, NULL)) ; u8i++)
#else
    for(U8 u8i = ATV_FIRST_PR_NUM ; u8i <= MSrv_Control::GetMSrvAtvDatabase()->ATVGetChannelMax() ; u8i++)
#endif
    {
        if((BOOL)MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(IS_PROGRAM_NUMBER_ACTIVE , u8i , 0 , NULL))    //pDatabase->ATVIsProgramNumberActive(u8i))  //must;;
        {
            (*pu16TotalChannelNum)++;
        }
    }

    return TRUE;
#endif
}

BOOL MSrv_ATV_Player::IsDiffVideoStandByChannelAndVD(void)
{
    MAPI_AVD_VideoStandardType enVideoStandard,enCurrentVideoStandard;
    U16 nCurrentChannelNumber;
    MSrv_Control::GetMSrvAtv()->GetCurrentChannelNumber(&nCurrentChannelNumber);
    enVideoStandard = (MAPI_AVD_VideoStandardType)MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_VIDEO_STANDARD_OF_PROGRAM, nCurrentChannelNumber, 0, NULL);
  enCurrentVideoStandard = mapi_interface::Get_mapi_vd()->GetVideoStandard();

  if(enVideoStandard != enCurrentVideoStandard)
  {
    return TRUE;
  }
  else
  {
    return FALSE;
  }
}

MSrv_TV_Player::EN_SET_CHANNEL_ERROR_CODE MSrv_ATV_Player::StartChangeChannel(U8 u8ChannelNumber, BOOL bCheckBlock)
{
    //U8 u8SystemLock;
    mapi_scope_lock(scopeLock, &m_mutex_ProgramChangeProcess);
#if (AUTO_TEST == 1)
    printf("\033[0;35m [AUTO_TEST][ATV channel change][%u] : Start Change \033[0m\n", mapi_time_utility::GetTime0());
#endif

    if((m_u8StartChangeProgram == 1) && (m_bIsPreProgramDisabled == TRUE))
    {
        printf("\033[0;35m [Warning]ATV channel change has not finished yet! \033[0m\n");
        return E_SET_CHANNEL_FAIL;
    }

#if(MSTAR_TVOS == 1)
    U16 u16Num = 0;

    if(u8ChannelNumber > MSrv_Control::GetMSrvAtvDatabase()->ATVGetChannelMax())
    {
        return MSrv_TV_Player::E_SET_CHANNEL_FAIL;
    }

    GetTotalChannelNumber(&u16Num);

    if(u16Num&&bCheckBlock
            && MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(IS_PROGRAM_LOCKED, u8ChannelNumber, 0, NULL))
#else
    if(bCheckBlock
            && MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(IS_PROGRAM_LOCKED, u8ChannelNumber, 0, NULL))
#endif
    {
#if (MSTAR_TVOS == 1 && ATSC_SYSTEM_ENABLE == 1)
        MSrv_Control::GetInstance()->SetVideoMute(TRUE,0 , m_PipXCWin);
#endif
        MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_SCAN_MUTEON_, m_CurrentSrcType);
        m_bCurrentProgramBlock = TRUE;
    }
    else
    {
        MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_BYBLOCK_MUTEOFF_, m_CurrentSrcType);
    }

    MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_SCAN_MUTEON_, m_CurrentSrcType);
    //To Disable the Channel
    DisableChannel();
    //stop decoder when disable channel
    mapi_interface::Get_mapi_audio()->DECODER_SetCommand(MSAPI_AUD_SIF_CMD_SET_STOP_);
    //To check the Block Status
    //u8SystemLock = MSrv_Control::GetMSrvSystemDatabase()->IsSystemLocked();

#if (PIP_ENABLE == 1)
    MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_BYUSER_CH7_MUTEON_, m_CurrentSrcType);
#endif

#if(MSTAR_TVOS == 1)
    if(u16Num&&bCheckBlock
            && MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(IS_PROGRAM_LOCKED, u8ChannelNumber, 0, NULL))
#else
    if(bCheckBlock
            && MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(IS_PROGRAM_LOCKED, u8ChannelNumber, 0, NULL))
#endif
    {
        //This program => Block , need to mute screen and mute audio
        BOOL ret;
        m_bReSendEvent = FALSE;
        ret = MSrv_Control::GetMSrvAtv()->PostEvent(NULL, EV_POPUP_DIALOG, 0, 0);
        if(FALSE == ret)
        {
            m_bReSendEvent = TRUE;
        }
#if(MSTAR_TVOS == 1)
        m_u8StartChangeProgram = 0; // to show CH Locked in ATVProc_Handler (mantis 0675625)
#endif
        return E_SET_CHANNEL_BLOCK;
    }

#if (TV_FREQ_SHIFT_CLOCK)
     mapi_interface::Get_mapi_vd()->ResetTVShiftClk();
#endif

#if (CHANNEL_CHANGE_FREEZE_IMAGE_BYDFB_ENBALE == 1)
#if (STEREO_3D_ENABLE == 1)
        if((m_bBeforeFirstSetChannel == FALSE) && (m_bChannelChangeFreezeImageFlags == TRUE)
            && (MSrv_Control::GetMSrv3DManager()->GetCurrent3DFormat() != EN_3D_DUALVIEW))
#else
        if((m_bBeforeFirstSetChannel == FALSE) && (m_bChannelChangeFreezeImageFlags == TRUE))
#endif  //(STEREO_3D_ENABLE == 1)
        {
            if((IsDiffVideoStandByChannelAndVD() == FALSE))
            {
               m_bNeedReloadPQ = FALSE;
            }
            else
            {
               m_bNeedReloadPQ = TRUE;

               if(m_bBeforeFirstSetChannel == TRUE)
               {
               m_bBeforeFirstSetChannel = FALSE;
               }
            }

            if((m_pcVideo->IsFreezeImage()) == FALSE)
            {
              m_pcVideo->FreezeImage(TRUE);
            }

            if((IsShowFreezeImageByDFB()) == FALSE)
            {
              ShowFreezeImageByDFB(TRUE);

              if((m_pcVideo->IsFreezeImage()) == TRUE)
              {
                 m_pcVideo->FreezeImage(FALSE);
              }
              MSrv_Control::GetInstance()->SetVideoMute(TRUE,0 , m_PipXCWin);
            }

        }
        else
        {
               m_bNeedReloadPQ = TRUE;

               if(m_bBeforeFirstSetChannel == TRUE)
               {
                   m_bBeforeFirstSetChannel = FALSE;
               }
        }
#endif

    m_bCurrentProgramBlock = FALSE;

#if(ISDB_SYSTEM_ENABLE == 1)
    bForceCheckAudioMode=FALSE;
#endif

    //To load AFT paremeter
    m_bIsAFTNeeded = MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(IS_AFT_NEED , u8ChannelNumber, 0, NULL);
    m_u8AftOffset = MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_AFT_OFFSET, u8ChannelNumber, 0, NULL);
    m_u8AftOffset_saved = m_u8AftOffset;
    m_u8AftOffset_pre= m_u8AftOffset;
//#ifndef ATSC_PLAYER_ENABLE
    //To set the Audio for new channel

#if (CHINA_ATV_ENABLE == 0)
    if(m_CurrentSrcType == MAPI_INPUT_SOURCE_ATV)
    {
        mapi_interface::Get_mapi_audio()->SetAudioMuteDuringLimitedTime(CHANGE_CHANNEL_AUDIO_MUTE_TIME, AUDIO_PROCESSOR_SUB);
    }
    else
    {
        mapi_interface::Get_mapi_audio()->SetAudioMuteDuringLimitedTime(CHANGE_CHANNEL_AUDIO_MUTE_TIME, AUDIO_PROCESSOR_MAIN);
    }
#endif

    mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard((AUDIOSTANDARD_TYPE_)MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_AUDIO_STANDARD , u8ChannelNumber, 0, NULL));
//#endif

    //To set the Audio and the Tuner PLL
    _SetTunerPLL(MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_PROGRAM_PLL_DATA, u8ChannelNumber, 0, NULL));

    //After setting audio standard and PLL, set sound system of demodulator.
    msAPI_Tuner_SetIF();

#if (TV_FREQ_SHIFT_CLOCK)
#if (ATSC_SYSTEM_ENABLE == 0 && ISDB_SYSTEM_ENABLE == 0)    //for DVB-T
    MAPI_AVD_VideoStandardType type;
    if(m_bNeedReloadPQ == TRUE)
    {
        type = (MAPI_AVD_VideoStandardType)MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_VIDEO_STANDARD_OF_PROGRAM, u8ChannelNumber, 0, NULL);
        mapi_interface::Get_mapi_vd()->SetVideoStandard(type);
        MAPI_INPUT_SOURCE_TYPE enInputSrc = MAPI_INPUT_SOURCE_ATV;
        MSrv_Control::GetMSrvSystemDatabase()->SetLastVideoStandard(&type, &enInputSrc);
        //mapi_interface::Get_mapi_vd()->ForceVideoStandard(type);
    }
#endif
#endif

    //To set flag, signal monitor thread will set it to 0, after signal stable
    m_u32StartTime = mapi_time_utility::GetTime0();
    m_bIsPreProgramDisabled = TRUE;
    m_u8StartChangeProgram = 1;
    m_u8ScreenModeStatus = 0xff ;//reset screenmode status
    //mapi_interface::Get_mapi_vif()->VifReset();
    m_u8ChangeProgramvifreset = 1;

#if (1 == ATV_FAST_ZAPPING_ENABLE)
    m_CurVideoStandard = (MAPI_AVD_VideoStandardType)MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_VIDEO_STANDARD_OF_PROGRAM, u8ChannelNumber, 0, NULL);
    if(m_CurVideoStandard < E_MAPI_VIDEOSTANDARD_NOTSTANDARD)
    {
        mapi_interface::Get_mapi_vd()->SetVideoStandard(m_CurVideoStandard,FALSE,TRUE);
    }
#endif
    //enable decoder before unmute audio
    mapi_interface::Get_mapi_audio()->DECODER_SetCommand(MSAPI_AUD_SIF_CMD_SET_PLAY_);

    return E_SET_CHANNEL_SUCCESS;
}

MSrv_TV_Player::EN_SET_CHANNEL_ERROR_CODE MSrv_ATV_Player::SetChannel(U16 u16ChannelNum, BOOL bCheckBlock)
{
#ifdef SIM_BUILD
    return TRUE;
#else

    MSrv_TV_Player::EN_SET_CHANNEL_ERROR_CODE eRetCode;
    ATV_PLAYER_IFO("Set Channel:%d\n", (int)u16ChannelNum);

#if (ATSC_SYSTEM_ENABLE == 1 || ISDB_SYSTEM_ENABLE == 1)
#if (ISDB_SYSTEM_ENABLE == 1)
    if(u16ChannelNum < (MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CHANNEL_MIN , 0, 0, NULL) - 1))
#else
    if(u16ChannelNum < ATV_FIRST_PR_NUM)
#endif
    {
        return MSrv_TV_Player::E_SET_CHANNEL_FAIL;
    }
    else
    {
#if (MSTAR_TVOS == 1)
        eRetCode = StartChangeChannel(u16ChannelNum, bCheckBlock);
#if (PIP_ENABLE == 1)
        if(m_PipXCWin == MAPI_MAIN_WINDOW)
#endif
        {
            PostEvent(0, EV_DTV_CHANNELNAME_READY, 0);
        }
#else // not TVOS
    eRetCode = StartChangeChannel(u16ChannelNum, bCheckBlock);
#endif
    }
#else
    eRetCode = StartChangeChannel(u16ChannelNum, bCheckBlock);
#endif
    if(eRetCode != MSrv_TV_Player::E_SET_CHANNEL_FAIL)
    {
        MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER , (U8)u16ChannelNum , 0 , NULL);
    }
    return eRetCode;
#endif
}

MSrv_TV_Player::EN_SET_CHANNEL_ERROR_CODE MSrv_ATV_Player::SetToNextChannel(BOOL bIncludeSkipped, BOOL bCheckBlock)
{
#ifdef SIM_BUILD
    return TRUE;
#else
    U8 u8CurrentChannel;
    U8 u8NextChannel;
    EN_SET_CHANNEL_ERROR_CODE u8ChannelErrorCode;

    ATV_PLAYER_IFO("My Set To Next Channel\n");

    u8CurrentChannel = MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL);
    u8NextChannel = MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_NEXT_PROGRAM_NUMBER , u8CurrentChannel, bIncludeSkipped, NULL);

    //Let StartChangeChannel be in front of SetProgramCtrl.
    //To fix the video and prog info not match issue with pressing twice next/prev button quickly. 20140430EL
    u8ChannelErrorCode = StartChangeChannel(u8NextChannel, bCheckBlock);
    if( u8ChannelErrorCode == E_SET_CHANNEL_FAIL)
        return E_SET_CHANNEL_FAIL;

    MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER , u8NextChannel , 0 , NULL);
#if (MSTAR_TVOS == 1)
#if (PIP_ENABLE == 1)
   if(m_PipXCWin == MAPI_MAIN_WINDOW)
#endif
#endif
    {
        PostEvent(0, EV_DTV_CHANNELNAME_READY, 0);
    }
    return (u8ChannelErrorCode);
#endif
}

MSrv_TV_Player::EN_SET_CHANNEL_ERROR_CODE MSrv_ATV_Player::SetToPreChannel(BOOL bIncludeSkipped, BOOL bCheckBlock)
{
#ifdef SIM_BUILD
    return TRUE;
#else
    U8 u8CurrentChannel;
    U8 u8PreChannel;
    EN_SET_CHANNEL_ERROR_CODE u8ChannelErrorCode;

    ATV_PLAYER_IFO("My Set To Pre Channel\n");

    u8CurrentChannel = MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL);
    u8PreChannel = MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_PREV_PROGRAM_NUMBER, u8CurrentChannel, bIncludeSkipped, NULL);

    //Let StartChangeChannel be in front of SetProgramCtrl.
    //To fix the video and prog info not match issue with pressing twice next/prev button quickly. 20140430EL
    u8ChannelErrorCode = StartChangeChannel(u8PreChannel, bCheckBlock);
    if( u8ChannelErrorCode == E_SET_CHANNEL_FAIL)
        return E_SET_CHANNEL_FAIL;

    MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER , u8PreChannel , 0 , NULL);
#if (MSTAR_TVOS == 1)
#if (PIP_ENABLE == 1)
   if(m_PipXCWin == MAPI_MAIN_WINDOW)
#endif
#endif
    {
        PostEvent(0, EV_DTV_CHANNELNAME_READY, 0);
    }
    return (u8ChannelErrorCode);
#endif
}

MSrv_TV_Player::EN_SET_CHANNEL_ERROR_CODE MSrv_ATV_Player::SetToRtnChannel(BOOL bCheckBlock)
{
#ifdef SIM_BUILD
    return TRUE;
#else
    U8 u8RtnChannel;

    u8RtnChannel = MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_PAST_PROGRAM_NUMBER , 0, 0, NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER , u8RtnChannel, 0, NULL);
    return (StartChangeChannel(u8RtnChannel, bCheckBlock));
#endif
}

MSrv_TV_Player::EN_MSRV_UI_SOUND_SYSTEM MSrv_ATV_Player::GetSoundSystem(void)
{
    AUDIOSTANDARD_TYPE_ eAudioStandard;

    eAudioStandard = mapi_interface::Get_mapi_audio()->SIF_GetAudioStandard();
    switch(mapi_interface::Get_mapi_audio()->SIF_ConvertToBasicAudioStandard(eAudioStandard))
    {
        case E_AUDIOSTANDARD_BG_:
            return MSrv_TV_Player::E_SYSTEM_MODE_BG;
            break;

        case E_AUDIOSTANDARD_I_:
            return MSrv_TV_Player::E_SYSTEM_MODE_I;
            break;

        case E_AUDIOSTANDARD_DK_:
            return MSrv_TV_Player::E_SYSTEM_MODE_DK;
            break;

        case E_AUDIOSTANDARD_L_:
            return MSrv_TV_Player::E_SYSTEM_MODE_L;
            break;

        case E_AUDIOSTANDARD_M_:
            return MSrv_TV_Player::E_SYSTEM_MODE_M;
            break;

        default:
            break;
    }

    return MSrv_TV_Player::E_SYSTEM_MODE_BG;
}

void MSrv_ATV_Player::SetToSnowflakeScreen(void)
{
    //U8 u8SystemLock;
    mapi_scope_lock(scopeLock, &m_mutex_ProgramChangeProcess);

    //To Disable the Channel
    DisableChannel();
    MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_SCAN_MUTEON_, m_CurrentSrcType);
    if (m_bChannelChangeFreezeImageFlags==TRUE)
    {
        if((m_pcVideo->IsFreezeImage()) == FALSE)
        {
            m_pcVideo->FreezeImage(TRUE);
        }
    }

    mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard(E_AUDIOSTANDARD_NOTSTANDARD_);

    _SetTunerPLL(m_u16DefaultPll);

    //After setting audio standard and PLL, set sound system of demodulator.
    msAPI_Tuner_SetIF();

    m_u32StartTime = mapi_time_utility::GetTime0();
    m_u8StartChangeProgram = 1;
    m_u8ScreenModeStatus = 0xff ;//reset screenmode status

    mapi_interface::Get_mapi_vif()->VifReset();
    //EnableChannel();

    return;
}

BOOL MSrv_ATV_Player::SetForceSoundSystem(EN_MSRV_UI_SOUND_SYSTEM eForceSoundSystem)
{
#ifdef SIM_BUILD
    return TRUE;
#else
    AUDIOSTANDARD_TYPE_ eAudioStandard;

    //To set Audio
    switch(eForceSoundSystem)
    {
        case E_SYSTEM_MODE_BG:
            mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard(E_AUDIOSTANDARD_BG_);
            break;

        case E_SYSTEM_MODE_I:
            mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard(E_AUDIOSTANDARD_I_);
            break;

        case E_SYSTEM_MODE_DK:
            mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard(E_AUDIOSTANDARD_DK_);
            break;

        case E_SYSTEM_MODE_L:
            mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard(E_AUDIOSTANDARD_L_);
            break;

        case E_SYSTEM_MODE_M:
            mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard(E_AUDIOSTANDARD_M_);
            break;

        default:
            mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard(E_AUDIOSTANDARD_BG_);
            break;
    }

    //to set VIF
    eAudioStandard = mapi_interface::Get_mapi_audio()->SIF_GetAudioStandard();

    switch(eAudioStandard)
    {
        case E_AUDIOSTANDARD_BG_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_BG_);
            break;

        case E_AUDIOSTANDARD_NOTSTANDARD_:
        case E_AUDIOSTANDARD_BG_A2_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_BG_A2_);
            break;
        case E_AUDIOSTANDARD_BG_NICAM_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_BG_NICAM_);
            break;
        case E_AUDIOSTANDARD_I_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_I_);
            break;
        case E_AUDIOSTANDARD_DK_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_DK_);
            break;
        case E_AUDIOSTANDARD_DK1_A2_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_DK1_A2_);
            break;
        case E_AUDIOSTANDARD_DK2_A2_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_DK2_A2_);
            break;
        case E_AUDIOSTANDARD_DK3_A2_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_DK3_A2_);
            break;
        case E_AUDIOSTANDARD_DK_NICAM_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_DK_NICAM_);
            break;
        case E_AUDIOSTANDARD_L_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_L_);
            break;
        case E_AUDIOSTANDARD_M_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_);
            break;
        case E_AUDIOSTANDARD_M_BTSC_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_BTSC_);
            break;
        case E_AUDIOSTANDARD_M_A2_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_A2_);
            break;
        case E_AUDIOSTANDARD_M_EIA_J_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_EIA_J_);
            break;

        default:
            break;
        }

    return TRUE;
#endif
}
MSrv_TV_Player::EN_MSRV_ATV_VIDEOSTANDARD_TYPE MSrv_ATV_Player::DetectVideoStardSystem()
{
    MAPI_AVD_VideoStandardType enVideoStandard;
    enVideoStandard = (MAPI_AVD_VideoStandardType)mapi_interface::Get_mapi_vd()->GetVideoStandard();
    switch(enVideoStandard)
    {
        case E_MAPI_VIDEOSTANDARD_PAL_BGHI:
            return E_ATV_VIDEOSTANDARD_PAL_BGHI;
            break;

        case E_MAPI_VIDEOSTANDARD_NTSC_M:
            return E_ATV_VIDEOSTANDARD_NTSC_M;
            break;

        case E_MAPI_VIDEOSTANDARD_SECAM:
            return E_ATV_VIDEOSTANDARD_SECAM;
            break;

        case E_MAPI_VIDEOSTANDARD_NTSC_44:
            return E_ATV_VIDEOSTANDARD_NTSC_44;
            break;

        case E_MAPI_VIDEOSTANDARD_PAL_M:
            return E_ATV_VIDEOSTANDARD_PAL_M;
            break;

        case E_MAPI_VIDEOSTANDARD_PAL_N:
            return E_ATV_VIDEOSTANDARD_PAL_N;
            break;

        case E_MAPI_VIDEOSTANDARD_PAL_60:
            return E_ATV_VIDEOSTANDARD_PAL_60;
            break;

        case E_MAPI_VIDEOSTANDARD_NOTSTANDARD:
            return E_ATV_VIDEOSTANDARD_NOTSTANDARD;
            break;

        case E_MAPI_VIDEOSTANDARD_AUTO:
            return E_ATV_VIDEOSTANDARD_AUTO;
            break;
        default:
            ASSERT(0);
            return E_ATV_VIDEOSTANDARD_MAX;
            break;
    }
}
BOOL MSrv_ATV_Player::SetForceVideoStandardSystem(EN_MSRV_ATV_VIDEOSTANDARD_TYPE eForceVideoSystem)
{
#ifdef SIM_BUILD
    return TRUE;
#else

#if (TV_FREQ_SHIFT_CLOCK)
    mapi_interface::Get_mapi_vd()->ResetTVShiftClk();
#endif

    MAPI_AVD_VideoStandardType eVideoSystem;
    //To set Audio
    switch(eForceVideoSystem)
    {
        case E_ATV_VIDEOSTANDARD_PAL_BGHI:
            eVideoSystem = E_MAPI_VIDEOSTANDARD_PAL_BGHI;
            //mapi_interface::Get_mapi_vd()->SetVideoStandard(eVideoSystem);
            mapi_interface::Get_mapi_vd()->ForceVideoStandard(eVideoSystem);
            break;
        case E_ATV_VIDEOSTANDARD_NTSC_M:
            eVideoSystem = E_MAPI_VIDEOSTANDARD_NTSC_M;
            //mapi_interface::Get_mapi_vd()->SetVideoStandard(eVideoSystem);
            mapi_interface::Get_mapi_vd()->ForceVideoStandard(eVideoSystem);
            break;
        case E_ATV_VIDEOSTANDARD_SECAM:
            eVideoSystem = E_MAPI_VIDEOSTANDARD_SECAM;
            //mapi_interface::Get_mapi_vd()->SetVideoStandard(eVideoSystem);
            mapi_interface::Get_mapi_vd()->ForceVideoStandard(eVideoSystem);
            break;
        case E_ATV_VIDEOSTANDARD_NTSC_44:
            eVideoSystem = E_MAPI_VIDEOSTANDARD_NTSC_44;
            //mapi_interface::Get_mapi_vd()->SetVideoStandard(eVideoSystem);
            mapi_interface::Get_mapi_vd()->ForceVideoStandard(eVideoSystem);
            break;
        case E_ATV_VIDEOSTANDARD_PAL_M:
            eVideoSystem = E_MAPI_VIDEOSTANDARD_PAL_M;
            //mapi_interface::Get_mapi_vd()->SetVideoStandard(eVideoSystem);
            mapi_interface::Get_mapi_vd()->ForceVideoStandard(eVideoSystem);
            break;
        case E_ATV_VIDEOSTANDARD_PAL_N:
            eVideoSystem = E_MAPI_VIDEOSTANDARD_PAL_N;
            //mapi_interface::Get_mapi_vd()->SetVideoStandard(eVideoSystem);
            mapi_interface::Get_mapi_vd()->ForceVideoStandard(eVideoSystem);
            break;
        case E_ATV_VIDEOSTANDARD_PAL_60:
            eVideoSystem = E_MAPI_VIDEOSTANDARD_PAL_60;
            //mapi_interface::Get_mapi_vd()->SetVideoStandard(eVideoSystem);
            mapi_interface::Get_mapi_vd()->ForceVideoStandard(eVideoSystem);
            break;

        case E_ATV_VIDEOSTANDARD_AUTO:
        {
            mapi_interface::Get_mapi_vd()->StartAutoStandardDetection();
            break;
        }
        default:
            eVideoSystem = E_MAPI_VIDEOSTANDARD_PAL_BGHI;
            break;
    }
    if(eForceVideoSystem != E_ATV_VIDEOSTANDARD_AUTO)
    {
        MAPI_INPUT_SOURCE_TYPE enInputSrc = MAPI_INPUT_SOURCE_ATV;
        MSrv_Control::GetMSrvSystemDatabase()->SetLastVideoStandard(&eVideoSystem, &enInputSrc);
    }
    return TRUE;
#endif
}
#if (ISDB_SYSTEM_ENABLE == 1)
/******************************************************************************/
///- This function will set video standard for brazil
/// @param eForceVideoSystem \b IN: video standard detected
/// @return                     \b OUT: True or False
/******************************************************************************/
BOOL MSrv_ATV_Player::SetForceBrazilVideoStandardSystem(EN_MSRV_VIDEOSTANDARD_BRAZIL_TYPE eForceVideoSystem)
{
#ifdef SIM_BUILD
    return TRUE;
#else
    U8 u8CurrentProgramNumber;
    MAPI_AVD_VideoStandardType eVideoSystem ;
    u8CurrentProgramNumber = MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL);
    //To set Audio
    switch(eForceVideoSystem)
    {
        case E_VIDEOSTANDARD_BRAZIL_NTSC_M:
            eVideoSystem = E_MAPI_VIDEOSTANDARD_NTSC_M;
            MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_VIDEO_STANDARD_OF_PROGRAM , u8CurrentProgramNumber, eVideoSystem, NULL);
            MSrv_Control::GetInstance()->SetVideoMute(TRUE, 100, m_PipXCWin);
            //mapi_interface::Get_mapi_vd()->SetVideoStandard((MAPI_AVD_VideoStandardType)MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_VIDEO_STANDARD_OF_PROGRAM , u8CurrentProgramNumber, NULL, NULL));
            mapi_interface::Get_mapi_vd()->ForceVideoStandard(E_MAPI_VIDEOSTANDARD_NTSC_M);
            //  MSrv_Control::GetInstance()->SetVideoMute(FALSE, mapi_video_datatype::E_SCREEN_MUTE_BLACK, 0);
            break;

        case E_VIDEOSTANDARD_BRAZIL_PAL_M:
            eVideoSystem = E_MAPI_VIDEOSTANDARD_PAL_M;
            MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_VIDEO_STANDARD_OF_PROGRAM , u8CurrentProgramNumber, eVideoSystem, NULL);
            MSrv_Control::GetInstance()->SetVideoMute(TRUE, 100, m_PipXCWin);
            //mapi_interface::Get_mapi_vd()->SetVideoStandard((MAPI_AVD_VideoStandardType)MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_VIDEO_STANDARD_OF_PROGRAM , u8CurrentProgramNumber, NULL, NULL));
            mapi_interface::Get_mapi_vd()->ForceVideoStandard(E_MAPI_VIDEOSTANDARD_PAL_M);
            // MSrv_Control::GetInstance()->SetVideoMute(FALSE, mapi_video_datatype::E_SCREEN_MUTE_BLACK, 0);
            break;

        case E_VIDEOSTANDARD_BRAZIL_PAL_N:
            eVideoSystem = E_MAPI_VIDEOSTANDARD_PAL_N;
            MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_VIDEO_STANDARD_OF_PROGRAM , u8CurrentProgramNumber, eVideoSystem, NULL);
            MSrv_Control::GetInstance()->SetVideoMute(TRUE, 100, m_PipXCWin);
            //mapi_interface::Get_mapi_vd()->SetVideoStandard((MAPI_AVD_VideoStandardType)MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_VIDEO_STANDARD_OF_PROGRAM , u8CurrentProgramNumber, NULL, NULL));
            mapi_interface::Get_mapi_vd()->ForceVideoStandard(E_MAPI_VIDEOSTANDARD_PAL_N);
            // MSrv_Control::GetInstance()->SetVideoMute(FALSE, mapi_video_datatype::E_SCREEN_MUTE_BLACK, 0);
            break;

        case E_VIDEOSTANDARD_BRAZIL_AUTO:
        {
            // U16 wTmpVd;
            // wTmpVd=mapi_interface::Get_mapi_vd()->GetVideoStandard();
            MSrv_Control::GetInstance()->SetVideoMute(TRUE, 100);
            mapi_interface::Get_mapi_vd()->StartAutoStandardDetection();
            //mapi_interface::Get_mapi_vd()->SetVideoStandard((MAPI_AVD_VideoStandardType)wTmpVd);
            //MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_VIDEO_STANDARD_OF_PROGRAM, u8CurrentProgramNumber, wTmpVd, NULL);
            //   MSrv_Control::GetInstance()->SetVideoMute(FALSE, mapi_video_datatype::E_SCREEN_MUTE_BLACK, 0);
        }
        default:
            eVideoSystem = E_MAPI_VIDEOSTANDARD_NTSC_M;
            break;
    }
    if(eForceVideoSystem != E_VIDEOSTANDARD_BRAZIL_AUTO)
    {
        MAPI_INPUT_SOURCE_TYPE enInputSrc = MAPI_INPUT_SOURCE_ATV;
        MSrv_Control::GetMSrvSystemDatabase()->SetLastVideoStandard(&eVideoSystem, &enInputSrc);
    }
    return TRUE;
#endif
}
#endif

MSrv_ATV_Player::EN_CHANNEL_SEARCH_TYPE MSrv_ATV_Player::GetChannelSearchType(void)
{
#if (ATSC_SYSTEM_ENABLE == 1)
    return (E_CHANNEL_SEARCH_NONE);
#else
    return(m_eChannelSearchType);
#endif
}

BOOL MSrv_ATV_Player::SetChannelSearchType(EN_CHANNEL_SEARCH_TYPE enType)
{
#if (ATSC_SYSTEM_ENABLE == 1)
    return FALSE;
#else
    m_eChannelSearchType = enType;
    return TRUE;
#endif
}


BOOL MSrv_ATV_Player::GetProgramInfoByIndex(ST_MSRV_CHANNEL_INFO &stChannelInfo, U8 u8Index)
{
    memset(&stChannelInfo, 0, sizeof(ST_MSRV_CHANNEL_INFO));

    if((BOOL)MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(IS_PROGRAM_NUMBER_ACTIVE, u8Index, 0, NULL))
    {
        U16 u16PllData;
        u16PllData = MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_PROGRAM_PLL_DATA, u8Index, 0, NULL);
        stChannelInfo.u32FrequencyKhz = 1000 * (msAPI_CFT_ConvertPLLtoIntegerOfFrequency(u16PllData));
        stChannelInfo.u32FrequencyKhz += msAPI_CFT_ConvertPLLtoIntegerOfFrequency(u16PllData);
        stChannelInfo.u8ProgramNum = u8Index;

        MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_MISC, stChannelInfo.u8ProgramNum, 0, (U8 *) &(stChannelInfo.stMisc));
        MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_STATION_NAME, stChannelInfo.u8ProgramNum , 0, (U8 *)&(stChannelInfo.au8Name[0]));

        //ATV_PLAYER_IFO(">>> Get current list -%d: F=%d KHz, %c%c%c%c (L=%d,F=%d)(Num=%d)\n"
        //               , (int)u8Index
        //               , (int)stChannelInfo.u32FrequencyKhz
        //               , stChannelInfo.au8Name[0]
        //               , stChannelInfo.au8Name[1]
        //               , stChannelInfo.au8Name[2]
        //               , stChannelInfo.au8Name[3]
        //               , (int)(stChannelInfo.stMisc.bIsLock), (int)(stChannelInfo.stMisc.bIsFavorite)
        //               , stChannelInfo.stMisc.u8ChannelNumber);

    }
    else
    {
        stChannelInfo.u8ProgramNum = u8Index;
        return MAPI_FALSE;
    }

    return MAPI_TRUE;
}

BOOL MSrv_ATV_Player::GetCurrentProgramInfo(ST_MSRV_CHANNEL_INFO &stChannelInfo)
{
    BOOL bResult;
    U8 u8Index;
    U16 u16CurChannelNumber;

    bResult = GetCurrentChannelNumber(&u16CurChannelNumber);
    if(bResult == FALSE)
    {
        return bResult;
    }

    u8Index = (U8)u16CurChannelNumber;
    bResult = GetProgramInfoByIndex(stChannelInfo, u8Index);
    return bResult;
}

//***********************************************************************************
//************************* Mapp_ATV_Scan.cpp ***************************************
//***********************************************************************************


//***********************************************************************************
//************************* Mapp_ATVProc.c ******************************************
//***********************************************************************************

/*
 ********************************************
 FUNCTION   : ATVProc_Handler
 USAGE      : To do the signal monitor background, and Scan algorithm...
              It was called by the threadSignalMonitor
 INPUT      : None
  OUTPUT     : None
********************************************
*/
void MSrv_ATV_Player::ATVProc_Handler(void)
{

#ifdef SIM_BUILD
    return TRUE;
#else
    //****************************
    //*** Scan Algorithm *********
    //****************************

    mapi_scope_lock(scopeLock, &m_mutex_ProgramChangeProcess);

    //****************************
    //*** AVD Handle *************
    //****************************
    //mapi_interface::Get_mapi_vd()->VideoProcessor();
    pthread_mutex_lock(&m_mutex_Scan);

#if (1 == ATV_FAST_ZAPPING_ENABLE)
    //Because auto detect and shift clock combination, so don't delay the timing for set shift clock.
    //if((m_u8StartChangeProgram==0) && (mapi_time_utility::TimeDiffFromNow0(m_u32StartTime) > ATV_PROGRAM_CHANGE_SKIP_CHECK_VIDEO_STANDARD_TIME))
#endif
    {
        mapi_interface::Get_mapi_vd()->VideoProcessor();
    }
    m_pDemodulator->ATV_VIF_Handler(FALSE);
    pthread_mutex_unlock(&m_mutex_Scan);
    //***********************************
    //*** VD, Ration, Scaler Setting ****
    //***********************************
    if(m_u8StartChangeProgram == 1)
    {
        if(m_bIsPreProgramDisabled == TRUE)
        {
            ProgramChangeProcess();
        }
    }
    else
    {
        UpdateVDandScaler();

#if (MSTAR_TVOS == 0)
    }
#endif

    BOOL ret = TRUE;
    if(m_bReSendEvent == TRUE)
    {
        ret = PostEvent(NULL, EV_POPUP_DIALOG, 0, 0);
        if (FALSE == ret)
        {
            m_bReSendEvent = TRUE; // force re-send this event
        }
        else
        {
            m_bReSendEvent = FALSE;
        }
    }
    else
    {
        if(mapi_time_utility::TimeDiffFromNow0(m_u32PostEventIntervalTime) > POSTEVENT_INTERVAL_TIME)
        {
        #if (MSTAR_TVOS == 1)
           #if (PIP_ENABLE == 1)
           if(m_PipXCWin == MAPI_MAIN_WINDOW)
           #endif
        #endif
            {
#if (MSTAR_TVOS == 1 && ATSC_SYSTEM_ENABLE == 1)
                if((m_bCurrentProgramBlock==TRUE)
                    && MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(IS_PROGRAM_LOCKED, (U16)MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL), 0, NULL))
#else
                if((m_bCurrentProgramBlock==TRUE)&&(MSrv_Control::GetMSrvSystemDatabase()->IsSystemLocked())
                    && MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(IS_PROGRAM_LOCKED, (U16)MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL), 0, NULL))
#endif
                {
                    PostEvent(0, EV_SCREEN_SAVER_MODE, (U32)MSRV_DTV_SS_CH_BLOCK);//for ATV has no screensaver ,If ATV program is Locked,cant show CH Locked
                }
                else
                {
#if (MSTAR_TVOS == 1 && ATSC_SYSTEM_ENABLE == 1)
                    BOOL IsLock;
                    MSrv_Control::GetInstance()->GetCurVChipBlockStatus(&IsLock);
                    if (IsLock)
                    {
                        PostEvent(0, EV_SCREEN_SAVER_MODE, (U32)MSRV_DTV_SS_PARENTAL_BLOCK);
                    }
                    else
#endif
                    {
                        PostEvent(0, EV_SCREEN_SAVER_MODE, (U32)MSRV_DTV_SS_COMMON_VIDEO);
                    }
                }
            }
            m_u32PostEventIntervalTime = mapi_time_utility::GetTime0();
        }
    }
    if((mapi_interface::Get_mapi_vd()->IsSyncLocked() == TRUE) && (msAPI_Tuner_IsTuningProcessorBusy() == false))
    {
        U8 u8DetecMtsStatus;
        mapi_interface::Get_mapi_audio()->SIF_Monitor_Service();
    #if (ENABLE_ATV_NOSINGAL_BLACKSCREEN==1)
        mapi_video *pMapiVideo = mapi_interface::Get_mapi_video(m_CurrentSrcType);
    #endif

#if (ATSC_SYSTEM_ENABLE == 1)
        if(m_bIsMTSMonitorEnabled == TRUE)
        {
            CurrentMTSMonitor();
        }
        else
        {
            DefaultMTSSelection();
        }
#elif (ESASIA_NTSC_SYSTEM_ENABLE == 1)//Add for MTS detection issue: 0660220 20140710EL
        CurrentMTSMonitor();
#elif (ISDB_SYSTEM_ENABLE == 1)
        if(bForceCheckAudioMode == TRUE)
        {
            DefaultMTSSelection_Brazil();
        #if (BRAZIL_CM_NUM_SEARCH == 1)
        if(mapi_time_utility::TimeDiffFromNow0(m_u32StartTime) < ATV_PROGRAM_CHANGE_SAVE_CHANNEL_TIME)
            {
                if(!(MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(IS_DIRECT_TUNED, (U8)MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL) , 0 , NULL)))
                {
                    ATVSaveProgram((U8)MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL)); // transfer UI index to Msrv index
                    MSrv_ChannelManager_ISDB* pCM = dynamic_cast<MSrv_ChannelManager_ISDB*>(MSrv_Control_common::GetMSrvChannelManager());
                    ASSERT(pCM);
                    pCM->GenMixProgList(FALSE);
                }
            }
        #endif
        }
        else
        {
            CurrentMTSMonitor();
        }

#else
        if((m_bIsDoSetMTSMode == FALSE) &&
           (mapi_time_utility::TimeDiffFromNow0(m_u32UserMtsSettingIntervalTime) >= ATV_USER_MTSSETTING_INTERVAL_TIME))
        {
            RecoverUserMtsSetting();
            m_u32UserMtsSettingIntervalTime = mapi_time_utility::GetTime0();
        }
#endif

        u8DetecMtsStatus = MSrv_Control::GetMSrvSSSound()->GetMtsMode();
        if(u8DetecMtsStatus != m_u8MtsStatus)
        {
            m_u8MtsStatus = u8DetecMtsStatus;
            m_bProgInfoChg = TRUE;;
        }
#if (ATSC_SYSTEM_ENABLE == 1)
#if (VCHIP_ENABLE == 1)
        if(IsTVSourceBlock() == FALSE)
#endif
#endif

#if (ENABLE_ATV_NOSINGAL_BLACKSCREEN==1)
        if((pMapiVideo->IsVideoMute() == MAPI_TRUE)&&(m_u8StartChangeProgram == 0)&&(m_bIsScanning== false))
        {
            MSrv_Control::GetInstance()->SetVideoMute(FALSE, 1000, m_PipXCWin);
        }
#endif
        if(m_bIsScanning == false && m_u8StartChangeProgram == 0)    // to avoid that audio is unmuted before EnableChannel
        {
            MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_SCAN_MUTEOFF_, m_CurrentSrcType);
        }
        MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_BYSYNC_MUTEOFF_, m_CurrentSrcType);
#if (PIP_ENABLE == 1)
        MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_BYUSER_CH7_MUTEOFF_, m_CurrentSrcType);
#endif
    }
    else
    {
        MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_BYSYNC_MUTEON_, m_CurrentSrcType);
#if (PIP_ENABLE == 1)
        MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_BYUSER_CH7_MUTEON_, m_CurrentSrcType);
#endif
    }

    if(m_bProgInfoChg == TRUE)
    {
    #if (MSTAR_TVOS == 1)
        #if (PIP_ENABLE == 1)
        if(m_PipXCWin == MAPI_MAIN_WINDOW)
        #endif
    #endif
        {
        PostEvent(0, EV_DTV_PROGRAM_INFO_READY, 0);
        }
        m_bProgInfoChg = FALSE;;
    }

#if (MSTAR_TVOS == 1)
    }
#endif

    if(m_u8StartChangeProgram == 0)//Is not channel changing
    {
        //AFT function
        if((m_bIsAFTNeeded == TRUE)
                && (IS_RT_AFT_ENABLED == TRUE))
        {
            _msAPI_Tuning_AutoFineTuning();
        }
#if (ENABLE_V_RANGE_HANDLE_ATV)
        MApp_VD_SyncRangeHandler();
#endif
#if (1 == ATV_FAST_ZAPPING_ENABLE)

        //Add ATV channel database check condition to avoid not correct flow, ex: SetProgram info when ATV channel is empty.
        if((MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_ACTIVE_PROGRAM_COUNT, 0, 0, NULL)) > 0)
        {
            MAPI_AVD_VideoStandardType enVideoStandard=(MAPI_AVD_VideoStandardType)mapi_interface::Get_mapi_vd()->GetVideoStandard();
            if((m_CurVideoStandard!=enVideoStandard) && IsSignalStable())
            {
                m_CurVideoStandard=enVideoStandard;
                MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_VIDEO_STANDARD_OF_PROGRAM, MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL), m_CurVideoStandard, NULL);
            }
        }

#endif
    }
#endif
}

void MSrv_ATV_Player::SetCurrentProgramBlock(BOOL set)
{
    m_bCurrentProgramBlock = set;
}

BOOL MSrv_ATV_Player::GetCurrentProgramBlock(void)
{
   return m_bCurrentProgramBlock;
}

#if (ATSC_SYSTEM_ENABLE == 1)
#if (VCHIP_ENABLE == 1)
BOOL MSrv_ATV_Player::IsTVSourceBlock(void)
{
    BOOL AudioMuteType = FALSE;

#if (INPUT_SOURCE_LOCK_ENABLE == 1)
    AudioMuteType = MSrv_Control::GetInstance()->GetInputBlock(MAPI_INPUT_SOURCE_ATV);
#else
    ST_VCHIP_SETTING stVchipSetting;
    MSrv_System_Database_ATSC *p = dynamic_cast<MSrv_System_Database_ATSC *>(MSrv_Control::GetMSrvSystemDatabase());
    ASSERT(p);

    p->GetVchipSetting(&stVchipSetting);

    if(stVchipSetting.u16u16InputBlocFlag)
        AudioMuteType |= ((stVchipSetting.u16InputBlockItem >> 0) & 0x01);
    else
        AudioMuteType |= ((stVchipSetting.u16InputBlockItem_Loop>> 0) & 0x01);
#endif

    return AudioMuteType;
}
#endif
#endif


void MSrv_ATV_Player::UpdateMediumStatus(void)
{
#if 1
#ifdef SIM_BUILD
    return;
#else
    return;
#endif
#else
    ST_MEDIUM_SETTING MediumSetting;
    MSrv_Control::GetMSrvSystemDatabase()->GetMediumSetting(&MediumSetting);
    if(MediumSetting.AntennaType == E_ANTENNA_TYPE_AIR)
    {
        if(GetNTSCAntenna() != MSrv_ATV_Database::MEDIUM_AIR)
        {
            SetNTSCAntenna(MSrv_ATV_Database::MEDIUM_AIR);
        }
    }
    else if(MediumSetting.AntennaType == E_ANTENNA_TYPE_CABLE)
    {
        if(GetNTSCAntenna() != MSrv_ATV_Database::MEDIUM_CABLE)
        {
            SetNTSCAntenna(MSrv_ATV_Database::MEDIUM_CABLE);
        }
    }
    else
    {
        // error here
    }
#endif
}

#if 0//(ATSC_SYSTEM_ENABLE == 1)
void MSrv_ATV_Player::ScreenSaverMonitor(void)
{
#if 0
    if((m_u16ScreenSaverCounter < THREAD_MONITOR_CHECK_SIGNAL_STATUS_SEC) && (m_u16ScreenSaverCounter != 0))
    {
        m_u16ScreenSaverCounter++;
        return;
    }
    else
    {
        m_u16ScreenSaverCounter = 1;
        if(mapi_interface::Get_mapi_vd()->IsSyncLocked() == FALSE)
        {
            BOOL bFlag;
            MSrv_Control::GetMSrvSystemDatabase()->GetNoChannelFlag(&bFlag);
            if(bFlag == TRUE)
            {
                PostEvent(0, EV_SCREEN_SAVER_MODE, (U32)MSRV_ATV_SS_NO_CHANNEL);
            }
            else
            {
                PostEvent(0, EV_SCREEN_SAVER_MODE, (U32)MSRV_ATV_SS_NORMAL);
            }
        }
    }
#endif
}
#endif

void MSrv_ATV_Player::ProgramChangeProcess(void)
{
    if(mapi_time_utility::TimeDiffFromNow0(m_u32StartTime) > ATV_PROGRAM_CHANGE_RESET_VIF_TIME)
    {
       if(m_u8ChangeProgramvifreset == 1)
       {
           m_u8ChangeProgramvifreset = 0;
           mapi_interface::Get_mapi_vif()->VifReset();
       }
    }
    if(mapi_time_utility::TimeDiffFromNow0(m_u32StartTime) > ATV_PROGRAM_CHANGE_BLACK_SCREEN_TIME)
    {

#if (1 == ATV_FAST_ZAPPING_ENABLE)
        if(TRUE == CheckAudioStandardChange())
        {
            if(mapi_time_utility::TimeDiffFromNow0(m_u32StartTime) <= ATV_PROGRAM_CHANGE_BLACK_SCREEN_TIME_MAX)
            {
                if(FALSE == mapi_interface::Get_mapi_vd()->IsSyncLocked())
                {
                    return;
                }
            }
        }
#endif

        if(m_bCurrentProgramBlock == FALSE)
        {
            //Wait for the signal stable
            //if(IsSignalStable() == TRUE)
            {
                //Set Mode
                mapi_video_vd_cfg *pVDData = new(std::nothrow) mapi_video_vd_cfg;
                ASSERT(pVDData);
                pVDData->enVideoStandard = GetVideoStandard();

#if(ISDB_SYSTEM_ENABLE == 1)

                MS_USER_SYSTEM_SETTING stGetSystemSetting;
                MSrv_Control::GetMSrvSystemDatabase()->GetUserSystemSetting(&stGetSystemSetting);
                U16 nCurrentChannelNumber;
                MSrv_Control::GetMSrvAtv()->GetCurrentChannelNumber(&nCurrentChannelNumber);

                if(MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_ATV_AUTOCOLOR, nCurrentChannelNumber, 0, NULL))
                {

                    mapi_interface::Get_mapi_vd()->StartAutoStandardDetection();
                    pVDData->enVideoStandard = GetVideoStandard();
                    //printf(" \33[0;31m 333 <<<ETN>>>pVDData->enVideoStandard = 0x%x  \n \33[m",pVDData->enVideoStandard);
                    if(pVDData->enVideoStandard >= E_MAPI_VIDEOSTANDARD_NOTSTANDARD)
                    {
                        //pVDData->enVideoStandard = E_MAPI_VIDEOSTANDARD_AUTO; //Marked 20120531EL
                        MAPI_INPUT_SOURCE_TYPE enInputSrc = MAPI_INPUT_SOURCE_ATV;
                        MSrv_Control::GetMSrvSystemDatabase()->GetLastVideoStandard(&(pVDData->enVideoStandard), &enInputSrc);
                    }

                }
                else
                {
                    SetForceBrazilVideoStandardSystem((EN_MSRV_VIDEOSTANDARD_BRAZIL_TYPE)stGetSystemSetting.m_u8BrazilVideoStandardType);
                    switch((EN_MSRV_VIDEOSTANDARD_BRAZIL_TYPE)stGetSystemSetting.m_u8BrazilVideoStandardType)
                    {
                        case E_VIDEOSTANDARD_BRAZIL_NTSC_M:
                            pVDData->enVideoStandard = E_MAPI_VIDEOSTANDARD_NTSC_M;
                            break;
                        case E_VIDEOSTANDARD_BRAZIL_PAL_M:
                            pVDData->enVideoStandard = E_MAPI_VIDEOSTANDARD_PAL_M;
                            break;
                        case E_VIDEOSTANDARD_BRAZIL_PAL_N:
                            pVDData->enVideoStandard = E_MAPI_VIDEOSTANDARD_PAL_N;
                            break;
                        case E_VIDEOSTANDARD_BRAZIL_AUTO:
                        default:
                            //pVDData->enVideoStandard = E_MAPI_VIDEOSTANDARD_AUTO;  //Marked 20120531EL
                            pVDData->enVideoStandard = GetVideoStandard();
                            if(pVDData->enVideoStandard >= E_MAPI_VIDEOSTANDARD_NOTSTANDARD)
                            {
                                MAPI_INPUT_SOURCE_TYPE enInputSrc = MAPI_INPUT_SOURCE_ATV;
                                MSrv_Control::GetMSrvSystemDatabase()->GetLastVideoStandard(&(pVDData->enVideoStandard), &enInputSrc);
                            }
                            break;
                    }
                }
              mapi_interface::Get_mapi_vd()->SetVideoStandard((MAPI_AVD_VideoStandardType)(pVDData->enVideoStandard));

#elif(ATSC_SYSTEM_ENABLE == 1)

                if(pVDData->enVideoStandard >= E_MAPI_VIDEOSTANDARD_NOTSTANDARD)
                {
                    MAPI_INPUT_SOURCE_TYPE enInputSrc = MAPI_INPUT_SOURCE_ATV;
                    MSrv_Control::GetMSrvSystemDatabase()->GetLastVideoStandard(&(pVDData->enVideoStandard), &enInputSrc);
                    mapi_interface::Get_mapi_vd()->SetChannelChange(TRUE);
                }
                else
                {
                    mapi_interface::Get_mapi_vd()->SetChannelChange(FALSE);
                }

#else
                U16 nCurrentChannelNumber;
                MSrv_Control::GetMSrvAtv()->GetCurrentChannelNumber(&nCurrentChannelNumber);
                pVDData->enVideoStandard = (MAPI_AVD_VideoStandardType)MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_VIDEO_STANDARD_OF_PROGRAM, nCurrentChannelNumber, 0, NULL);

                if(pVDData->enVideoStandard >= E_MAPI_VIDEOSTANDARD_NOTSTANDARD)
                {
                    if(pVDData->enVideoStandard == E_MAPI_VIDEOSTANDARD_AUTO)
                    {
                        mapi_interface::Get_mapi_vd()->StartAutoStandardDetection();
                        #if (MSTAR_TVOS == 1)
                        usleep(1000*100);
                        #endif
                        pVDData->enVideoStandard = GetVideoStandard();
                        mapi_interface::Get_mapi_vd()->SetVideoStandard(pVDData->enVideoStandard);
                    }
                    else
                    {
                        MAPI_INPUT_SOURCE_TYPE enInputSrc = MAPI_INPUT_SOURCE_ATV;
                        MSrv_Control::GetMSrvSystemDatabase()->GetLastVideoStandard(&(pVDData->enVideoStandard), &enInputSrc);
                        //mapi_interface::Get_mapi_vd()->SetVideoStandard(pVDData->enVideoStandard);
                        mapi_interface::Get_mapi_vd()->ForceVideoStandard(pVDData->enVideoStandard);
                    }
                    mapi_interface::Get_mapi_vd()->SetChannelChange(TRUE);
                }
                else
                {
                    U16 nCurrentChannelNumber;
                    MSrv_Control::GetMSrvAtv()->GetCurrentChannelNumber(&nCurrentChannelNumber);
                    //mapi_interface::Get_mapi_vd()->SetVideoStandard(pVDData->enVideoStandard, FALSE);
                    //#if (MSTAR_TVOS == 1)
                    mapi_interface::Get_mapi_vd()->ForceVideoStandard(pVDData->enVideoStandard);
                    //#endif
                    mapi_interface::Get_mapi_vd()->SetChannelChange(FALSE);
                }
#endif
                MAPI_INPUT_SOURCE_TYPE enInputSrc = MAPI_INPUT_SOURCE_ATV;
                MSrv_Control::GetMSrvSystemDatabase()->SetLastVideoStandard(&(pVDData->enVideoStandard), &enInputSrc);
#if (CHINA_ATV_ENABLE == 1)
                mapi_interface::Get_mapi_audio()->SIF_SetMtsMode(E_AUDIOMODE_FORCED_MONO_);
#endif
                if(m_bNeedReloadPQ == TRUE)
                {
                    MSrv_Control::GetMSrvPicture()->Off();
                }
                mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetMode(pVDData);

                if(pVDData != NULL)
                {
                    delete pVDData;
                    pVDData = NULL;
                }

#if (STEREO_3D_ENABLE == 1)
                MSrv_3DManager* ptr3DMgr = MSrv_Control::GetMSrv3DManager();
                if (IsSignalStable() == TRUE)
                {
                    if(ptr3DMgr->Get3DFormatDetectFlag() && (ptr3DMgr->GetCurrent3DFormat() != EN_3D_NONE))
                    {
                        (MSrv_Control_common::GetMSrv3DManager())->Enable3D(EN_3D_NONE);
                    }
                }
#endif

                CheckOutputTiming();

#if (PIP_ENABLE == 1)
                // temp solution, need refine it later
                if(m_PipXCWin == MAPI_MAIN_WINDOW)
                {
                    if(m_bNeedReloadPQ == TRUE)
                    {
                        MSrv_Control::GetMSrvPicture()->On();
                    }
                }
                else
                {
                    MSrv_Control::GetMSrvVideo()->SelectWindow(MAPI_SUB_WINDOW);
                    if(m_bNeedReloadPQ == TRUE)
                    {
                        MSrv_Control::GetMSrvPicture()->On();
                    }
                    MSrv_Control::GetMSrvVideo()->SelectWindow(MAPI_MAIN_WINDOW);
                }
#else
                if(m_bNeedReloadPQ == TRUE)
                {
                    MSrv_Control::GetMSrvPicture()->On();
                }
#endif

#if (ENABLE_V_RANGE_HANDLE_ATV)
                MApp_VD_StartRangeHandle();
#endif
            }
        #if(ISDB_SYSTEM_ENABLE == 1)
            bForceCheckAudioMode=TRUE;
        #endif
            //Un-mute Screen,
            mapi_interface::Get_mapi_vd()->IsVideoFormatChanged();//To clear the change flag
            //To enable the channel
            m_bIsPreProgramDisabled = TRUE;
            mapi_interface::Get_mapi_audio()->SIF_CheckAudioStandard();
            EnableChannel();
        }
        m_u8StartChangeProgram = 0;
    }
    return;
}


#if (ENABLE_V_RANGE_HANDLE_ATV)
static U16 u16PreVtotal;

#define MAX_VTOTAL_COUNT     675
#define MID_VTOTAL_COUNT     575
#define MIN_VTOTAL_COUNT     475
#define PAL_VTOTAL_STD     625
#define NTSC_VTOTAL_STD     525
#define DELTA_VTOTAL     5
#define PAL_VTOTAL_MAX     (PAL_VTOTAL_STD+DELTA_VTOTAL)
#define PAL_VTOTAL_MIN     (PAL_VTOTAL_STD-DELTA_VTOTAL)
#define NTSC_VTOTAL_MAX     (NTSC_VTOTAL_STD+DELTA_VTOTAL)
#define NTSC_VTOTAL_MIN     (NTSC_VTOTAL_STD-DELTA_VTOTAL)

#define RANGE_HANDLE_STABLE_COUNTER     20

void MSrv_ATV_Player::MApp_VD_StartRangeHandle(void)
{
    const U16 wVtotal = mapi_interface::Get_mapi_vd()->GetVTotal();
    if((wVtotal < MAX_VTOTAL_COUNT) && (wVtotal > MID_VTOTAL_COUNT))
        u16PreVtotal = PAL_VTOTAL_STD;
    else
        u16PreVtotal = NTSC_VTOTAL_STD;
    ATV_PLAYER_DBG("u16PreVtotal[%d]\n", u16PreVtotal);
}

#if 0
//********************************************************************************************
// Program capture win/crop win/display window
//   parameter:
//     pDisplayWindow  - The display window before adjust it .i.e: adjust aspect ratio to it.
//     eWindow             - Display window id, i.e Main_WINDOW or SUB_WINDOW
//********************************************************************************************

void MSrv_ATV_Player::MApp_XC_check_crop_win(XC_SETWIN_INFO *pstXC_SetWin_Info)
{
    if(pstXC_SetWin_Info->stCropWin.width > pstXC_SetWin_Info->stCapWin.width)
    {
        ASSERT(0);
        pstXC_SetWin_Info->stCropWin.width = pstXC_SetWin_Info->stCapWin.width;
    }

    if(pstXC_SetWin_Info->stCropWin.height > pstXC_SetWin_Info->stCapWin.height)
    {
        ASSERT(0);
        pstXC_SetWin_Info->stCropWin.height = pstXC_SetWin_Info->stCapWin.height;
    }

    if(pstXC_SetWin_Info->stCropWin.x > pstXC_SetWin_Info->stCapWin.width - pstXC_SetWin_Info->stCropWin.width)
    {
        ASSERT(0);
        pstXC_SetWin_Info->stCropWin.x = pstXC_SetWin_Info->stCapWin.width - pstXC_SetWin_Info->stCropWin.width;
    }
    if(pstXC_SetWin_Info->stCropWin.y > pstXC_SetWin_Info->stCapWin.height - pstXC_SetWin_Info->stCropWin.height)
    {
        ASSERT(0);
        pstXC_SetWin_Info->stCropWin.y = pstXC_SetWin_Info->stCapWin.height - pstXC_SetWin_Info->stCropWin.height;
    }
}


/// Get current window settings: includes Capture window, Display window
/// and Crop window

void MSrv_ATV_Player::MApp_Scaler_GetWinInfo(XC_SETWIN_INFO* pWindowInfo, SCALER_WIN eWindow)
{
    // memcpy((void*)pWindowInfo, (void*)&stXC_SetWin_Info[eWindow],sizeof(XC_SETWIN_INFO));
}


void MSrv_ATV_Player::MApp_Scaler_SetCustomerWindow(MS_WINDOW_TYPE *ptSrcWin, MS_WINDOW_TYPE *ptCropWin, MS_WINDOW_TYPE *ptDstWin, SCALER_WIN eWindow)
{
    XC_SETWIN_INFO stSetWinInfo;

    //MApp_Scaler_GetWinInfo(&stSetWinInfo, eWindow);

    if(ptSrcWin)
        stSetWinInfo.stCapWin = *ptSrcWin;

    if(ptCropWin)
        stSetWinInfo.stCropWin = *ptCropWin;

    if(ptDstWin)
        stSetWinInfo.stDispWin = *ptDstWin;

    //MApp_XC_check_crop_win(&stSetWinInfo);

    if(MApi_XC_SetWindow(&stSetWinInfo, sizeof(XC_SETWIN_INFO), eWindow) == FALSE)
    {
        ATV_PLAYER_DBG("MApi_XC_SetWindow failed because of InitData wrong, please update header file and compile again\n");
    }
}
#endif

//////////////////////////////////////////
void MSrv_ATV_Player::MApp_VD_RangeReset(void)
{
    u16PreVtotal = 0;
    ATV_PLAYER_DBG("VD_RangeReset\n");
}
//////////////////////////////////////////
//Test item: change signal vfreq(+/- 4hz), to check display normal or not
//Purpose: don't see black margin or distort image in the display area.
//Analyze:
// 1. vfreq change, vd will report different vtotal. the active area will be changed.
// 2. SN cannot change capture window. Sn will change crop window.
// 3. the new crop window should be always in the active area, otherwise, garbage will not be cropped.
//Limitation:
// 1. the center could be moved to aside
// 2. the image could be zoom in
// 3. the crop win may needs refine for different signal input.
//Note: this setting is tuned by Happen.zheng in TCL A7P ISDB project.
// 1.
//below eight values is from tuning experienced values, can be adjusted by customers
//#define V_CAPTURE_SIZE_INC_X            73
//#define V_CAPTURE_SIZE_INC_Y            40
//#define V_CAPTURE_SIZE_INC_WIDTH_FACTOR 110
//#define V_CAPTURE_SIZE_INC_HEIGHT_FACTOR 35
//
//#define V_CAPTURE_SIZE_DEC_X            73
//#define V_CAPTURE_SIZE_DEC_Y            17
//#define V_CAPTURE_SIZE_DEC_WIDTH_FACTOR 110
//#define V_CAPTURE_SIZE_DEC_HEIGHT_FACTOR 40
//
//    if(u16V_CapSize > stCaptureWin.height)
//    {
//        stCorpWin.x = V_CAPTURE_SIZE_INC_X;
//        stCorpWin.y = V_CAPTURE_SIZE_INC_Y;
//        stCorpWin.width = (stCaptureWin.width- V_CAPTURE_SIZE_INC_WIDTH_FACTOR) - (u16V_CapSize - stCaptureWin.height);
//        stCorpWin.height = (stCaptureWin.height- V_CAPTURE_SIZE_INC_HEIGHT_FACTOR) - (u16V_CapSize - stCaptureWin.height);
//    }
//    else
//    {
//        stCorpWin.x = V_CAPTURE_SIZE_DEC_X;
//        stCorpWin.y = V_CAPTURE_SIZE_DEC_Y;
//        stCorpWin.width = (stCaptureWin.width- V_CAPTURE_SIZE_DEC_WIDTH_FACTOR) - (stCaptureWin.height - u16V_CapSize);
//        stCorpWin.height = (stCaptureWin.height- V_CAPTURE_SIZE_DEC_HEIGHT_FACTOR) - (stCaptureWin.height - u16V_CapSize);
//    }
//
// 2.
// remove m_pcVideo->SetCaptureWindowVstart(..)
//
// 3.add below 3 lines into void MSrv_ATV_Player::UpdateVDandScaler(void)
//    #if (ENABLE_V_RANGE_HANDLE_ATV)
//          MApp_VD_StartRangeHandle();
//    #endif
//
#define V_CAPTURE_SIZE_INC_X            73
#define V_CAPTURE_SIZE_INC_Y            40
#define V_CAPTURE_SIZE_INC_WIDTH_FACTOR 110
#define V_CAPTURE_SIZE_INC_HEIGHT_FACTOR 35
//
#define V_CAPTURE_SIZE_DEC_X            73
#define V_CAPTURE_SIZE_DEC_Y            17
#define V_CAPTURE_SIZE_DEC_WIDTH_FACTOR 110
#define V_CAPTURE_SIZE_DEC_HEIGHT_FACTOR 40

void  MSrv_ATV_Player::MApp_VD_SyncRangeHandler(void)
{
    U16 wVtotal;
    U16 g_cVSizeShift = 0;
    static U16 U16_VStartStd = 0;
    U16 u16V_CapSize;
    U16 u16_VCapSt;

    static U8 u8SyncStableCounter = 0;

#if 0
    MAPI_U8  u8FactoryIdentifier;

    MS_FACTORY_OTHER_SETTING  m_sFactoryOtherData;
    MSrv_Control::GetMSrvSystemDatabase()->Command(E_FACTORY_OTHER_DATA_GET, &m_sFactoryOtherData, sizeof(MS_FACTORY_OTHER_SETTING), NULL);
    u8FactoryIdentifier = m_sFactoryOtherData.u8FactoryIdentifier;

    if(!(u8FactoryIdentifier & 0x02))
    {
        return;
    }
    if(m_bFlagThreadSendScanInfo_Active)   return ;
#endif

    if(mapi_interface::Get_mapi_vd()->IsSyncLocked() == FALSE)  return ;
    if(mapi_interface::Get_mapi_vd()->GetVideoStandard() != E_MAPI_VIDEOSTANDARD_PAL_BGHI)  return ;

    wVtotal = mapi_interface::Get_mapi_vd()->GetVTotal();

    //ATV_PLAYER_DBG("KKK: MApp_VD_SyncRangeHandler u8SyncStableCounter = %d(wVtotal=%d)\n\n",u8SyncStableCounter,(int)wVtotal);

    if(((wVtotal <= PAL_VTOTAL_MAX) && (wVtotal >= PAL_VTOTAL_MIN) && (u16PreVtotal == PAL_VTOTAL_STD))
            || ((wVtotal <= NTSC_VTOTAL_MAX) && (wVtotal >= NTSC_VTOTAL_MIN) && (u16PreVtotal == NTSC_VTOTAL_STD)))
    {
        u8SyncStableCounter = 0;
        U16_VStartStd = 0;
        return;
    }

#if 0
    if((wVtotal < PAL_VTOTAL_MIN) && (wVtotal > MID_VTOTAL_COUNT))
    {
        //Type_A
        if(u16PreVtotal != wVtotal)
        {
            u16PreVtotal  = wVtotal;
            u8SyncStableCounter = 0;
        }
        else if(u8SyncStableCounter < RANGE_HANDLE_STABLE_COUNTER)
            u8SyncStableCounter ++;
    }
    else if((wVtotal < NTSC_VTOTAL_MIN) && (wVtotal > MIN_VTOTAL_COUNT))
    {
        //Type_B
        if(u16PreVtotal != wVtotal)
        {
            u16PreVtotal  = wVtotal;
            u8SyncStableCounter = 0;
        }
        else if(u8SyncStableCounter < RANGE_HANDLE_STABLE_COUNTER)
            u8SyncStableCounter ++;
    }
#if 0
    else if(((wVtotal <= PAL_VTOTAL_MAX) && (wVtotal >= PAL_VTOTAL_MIN)) || ((wVtotal <= NTSC_VTOTAL_MAX) && (wVtotal >= NTSC_VTOTAL_MIN)))
    {
        //Type_C
        if((u16PreVtotal > (wVtotal + DELTA_VTOTAL)) || (u16PreVtotal < (wVtotal - DELTA_VTOTAL)))
        {
            u16PreVtotal  = wVtotal;
            u8SyncStableCounter = 0;
        }
        else if(u8SyncStableCounter < 5)
            u8SyncStableCounter ++;
    }
#endif
    else u8SyncStableCounter = 0;
#else
    if(u16PreVtotal != wVtotal)
    {
        u16PreVtotal  = wVtotal;
        if((u16PreVtotal == PAL_VTOTAL_STD) || (u16PreVtotal == NTSC_VTOTAL_STD))
            u8SyncStableCounter = RANGE_HANDLE_STABLE_COUNTER;
        else
            u8SyncStableCounter = 0;
    }
    else if(u8SyncStableCounter < RANGE_HANDLE_STABLE_COUNTER)
        u8SyncStableCounter ++;
#endif
    ATV_PLAYER_DBG("\r\n\tn:%d", u8SyncStableCounter);
    if(u8SyncStableCounter == RANGE_HANDLE_STABLE_COUNTER)
    {
        mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stCorpWin;
        mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stCaptureWin;

        mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->GetWindowInfo(&stCaptureWin, NULL, NULL);

        ATV_PLAYER_DBG("KKK-0: x:%d,y:%d,w:%d,h:%d\n", stCaptureWin.x, stCaptureWin.y, stCaptureWin.width, stCaptureWin.height);
        u8SyncStableCounter = 0xFF;
        u16V_CapSize = stCaptureWin.height;

        if(!U16_VStartStd)
            U16_VStartStd = stCaptureWin.y;

        u16_VCapSt = U16_VStartStd;

        if(((wVtotal < PAL_VTOTAL_MIN) && (wVtotal > MID_VTOTAL_COUNT)) || ((wVtotal > PAL_VTOTAL_MAX) && (wVtotal < MAX_VTOTAL_COUNT)))
        {
            if(wVtotal > PAL_VTOTAL_STD)
            {
                g_cVSizeShift = wVtotal - PAL_VTOTAL_STD;
                //u16V_CapSize = 576 + g_cVSizeShift;
                u16_VCapSt = u16_VCapSt + g_cVSizeShift / 2;
                u16V_CapSize = u16V_CapSize + g_cVSizeShift;
            }
            else
            {
                g_cVSizeShift = PAL_VTOTAL_STD - wVtotal;
                //u16V_CapSize = 576 - g_cVSizeShift;     //576 is waite factory menu
                if(u16_VCapSt >= g_cVSizeShift / 2)
                {
                    u16_VCapSt = u16_VCapSt - g_cVSizeShift / 2;
                }
                u16V_CapSize = u16V_CapSize - g_cVSizeShift;
            }
        }
        else if(((wVtotal < NTSC_VTOTAL_MIN) && (wVtotal > MIN_VTOTAL_COUNT)) || ((wVtotal > NTSC_VTOTAL_MAX) && (wVtotal < MID_VTOTAL_COUNT)))
        {
            if(wVtotal > NTSC_VTOTAL_STD)
            {
                g_cVSizeShift = wVtotal - NTSC_VTOTAL_STD;
                u16_VCapSt = u16_VCapSt + g_cVSizeShift / 2;
                u16V_CapSize = u16V_CapSize + g_cVSizeShift;
            }
            else
            {
                g_cVSizeShift = NTSC_VTOTAL_STD - wVtotal;
                u16V_CapSize = u16V_CapSize - g_cVSizeShift;
            }
        }
        //ATV_PLAYER_DBG("KKK-1: x:%d,y:%d,w:%d,h:%d\n",stCaptureWin.x,stCaptureWin.y,stCaptureWin.width,stCaptureWin.height);

        stCaptureWin.height =  u16V_CapSize;

        {
            //Set Mode and window

            mapi_video_vd_cfg *pVDData = new(std::nothrow) mapi_video_vd_cfg;
            ASSERT(pVDData);
            pVDData->enVideoStandard = GetVideoStandard();

            if(pVDData->enVideoStandard >= E_MAPI_VIDEOSTANDARD_NOTSTANDARD)
            {
                MAPI_INPUT_SOURCE_TYPE enInputSrc = MAPI_INPUT_SOURCE_ATV;
                MSrv_Control::GetMSrvSystemDatabase()->GetLastVideoStandard(&(pVDData->enVideoStandard), &enInputSrc);
            }

            MSrv_Control::GetMSrvPicture()->Off();
            m_pcVideo->SetMode(pVDData);

            //mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stVideoWinType;
            mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO stVideoARCInfo;//eVideoARCType; //ARC type on "SCREEN" ( Result ARC )ST_MAPI_VIDEO_ARC_INFO
            //mapi_vd_datatype::ASPECT_RATIO_TYPE enWSSARCType;  //ARC type come from input source

            //enWSSARCType = mapi_interface::Get_mapi_vd()->GetAspectRatioCode();//waiting VD class
            //CalWSSWin(enWSSARCType, &stVideoWinType, &stVideoARCInfo);

            memset(&stVideoARCInfo, 0, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO));
            MAPI_INPUT_SOURCE_TYPE enCurrentInputType = MAPI_INPUT_SOURCE_ATV;
            MSrv_Control::GetMSrvSystemDatabase()->GetVideoArc(&stVideoARCInfo.enARCType, &enCurrentInputType);

            stVideoARCInfo.s16Adj_ARC_Left = 0;
            stVideoARCInfo.s16Adj_ARC_Right = 0;
            stVideoARCInfo.s16Adj_ARC_Up = 0;
            stVideoARCInfo.s16Adj_ARC_Down = 0;
            stVideoARCInfo.bSetCusWin = MAPI_FALSE;

            if(u16V_CapSize > stCaptureWin.height)
            {
                stCorpWin.x = V_CAPTURE_SIZE_INC_X;
                stCorpWin.y = V_CAPTURE_SIZE_INC_Y;
                stCorpWin.width = (stCaptureWin.width- V_CAPTURE_SIZE_INC_WIDTH_FACTOR) - (u16V_CapSize - stCaptureWin.height);
                stCorpWin.height = (stCaptureWin.height- V_CAPTURE_SIZE_INC_HEIGHT_FACTOR) - (u16V_CapSize - stCaptureWin.height);
            }
            else
            {
                stCorpWin.x = V_CAPTURE_SIZE_DEC_X;
                stCorpWin.y = V_CAPTURE_SIZE_DEC_Y;
                stCorpWin.width = (stCaptureWin.width- V_CAPTURE_SIZE_DEC_WIDTH_FACTOR) - (stCaptureWin.height - u16V_CapSize);
                stCorpWin.height = (stCaptureWin.height- V_CAPTURE_SIZE_DEC_HEIGHT_FACTOR) - (stCaptureWin.height - u16V_CapSize);
            }

            ATV_PLAYER_DBG("\nKKK-2: x:%d,y:%d,w:%d,h:%d\n", stCorpWin.x, stCorpWin.y, stCorpWin.width, stCorpWin.height);
            ATV_PLAYER_DBG("\r\nVstd:%d,Vcst:%d", U16_VStartStd, u16_VCapSt);
#if (PIP_ENABLE == 1)
            EN_PIP_MODES enPipMode = MSrv_Control::GetInstance()->GetPipMode();
            if(  (enPipMode == E_PIP_MODE_OFF)
               ||(enPipMode==E_PIP_MODE_TRAVELING && m_PipXCWin==MAPI_SUB_WINDOW))
            {
                m_pcVideo->SetWindow(&stCorpWin, NULL, &stVideoARCInfo);
            }
            else if(enPipMode == E_PIP_MODE_PIP)
            {
                mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stWinInfo;
                if(m_PipXCWin == MAPI_SUB_WINDOW)
                {
                    MSrv_Control::GetInstance()->GetPipSubwindow(&stWinInfo);
                    m_pcVideo->SetWindow(&stCorpWin, &stWinInfo, &stVideoARCInfo);
                }
                else
                {
                    MSrv_Control::GetInstance()->GetMainwindow(&stWinInfo);
                    m_pcVideo->SetWindow(&stCorpWin, NULL, &stVideoARCInfo);
                }
            }
            else
            {
                mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stWinInfo;
                if(m_PipXCWin == MAPI_SUB_WINDOW)
                    MSrv_Control::GetInstance()->GetPipSubwindow(&stWinInfo);
                else
                    MSrv_Control::GetInstance()->GetMainwindow(&stWinInfo);

                m_pcVideo->SetWindow(&stCorpWin, &stWinInfo, &stVideoARCInfo);
            }
#else
            m_pcVideo->SetWindow(&stCorpWin, NULL, &stVideoARCInfo);
#endif
#if 0
#if (PIP_ENABLE == 1)
            m_pcVideo->SetCaptureWindowVstart(u16_VCapSt, m_PipXCWin);
#else
            m_pcVideo->SetCaptureWindowVstart(u16_VCapSt, MAPI_MAIN_WINDOW);
#endif
#endif

#if (PIP_ENABLE == 1)
            // temp solution, need refine it later
            if(m_PipXCWin == MAPI_MAIN_WINDOW)
            {
                MSrv_Control::GetMSrvPicture()->On();
            }
            else
            {
                MSrv_Control::GetMSrvVideo()->SelectWindow(MAPI_SUB_WINDOW);
                MSrv_Control::GetMSrvPicture()->On();
                MSrv_Control::GetMSrvVideo()->SelectWindow(MAPI_MAIN_WINDOW);
            }
#else
            MSrv_Control::GetMSrvPicture()->On();
#endif
            //m_bHaveLoadPQ = TRUE;
            if(pVDData != NULL)
            {
                delete pVDData;
                pVDData = NULL;
            }
        }
        return ;
    }

}
#endif

/*
 ********************************************
 FUNCTION   : CalWSSWin
 USAGE      :
 INPUT      :
 OUTPUT     :
 ********************************************
*/
void MSrv_ATV_Player::CalWSSWin(mapi_vd_datatype::ASPECT_RATIO_TYPE enWSSARCType, mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *ptWinType, mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO *ptARCInfo)
{
    switch(enWSSARCType)
    {
        case mapi_vd_datatype::ARC4x3_FULL:
            ptARCInfo->enARCType = mapi_video_datatype::E_AR_4x3;
            //eVideoARCType  =   VIDEOSCREEN_NORMAL;
            break;

#if 1 //Jeff: Customized Window type and ARCType for customer here.
        case mapi_vd_datatype::ARC14x9_LETTERBOX_CENTER:
        case mapi_vd_datatype::ARC14x9_FULL_CENTER:
            ptARCInfo->enARCType = mapi_video_datatype::E_AR_Zoom1;
            break;

        case mapi_vd_datatype::ARC14x9_LETTERBOX_TOP:
            ptARCInfo->enARCType = mapi_video_datatype::E_AR_Zoom2;
            break;

        case mapi_vd_datatype::ARC16x9_LETTERBOX_CENTER:
        case mapi_vd_datatype::ARC_ABOVE16x9_LETTERBOX_CENTER:
            ptARCInfo->enARCType = mapi_video_datatype::E_AR_16x9;
            break;

        case mapi_vd_datatype::ARC16x9_LETTERBOX_TOP:
            ptARCInfo->enARCType = mapi_video_datatype::E_AR_JustScan;
            break;

        case mapi_vd_datatype::ARC16x9_ANAMORPHIC:
            ptARCInfo->enARCType = mapi_video_datatype::E_AR_Panorama;
            break;
#endif

        case mapi_vd_datatype::ARC_INVALID:
        default:
            ptARCInfo->enARCType = mapi_video_datatype::E_AR_AUTO;
            break;
    }
}

/*
 ********************************************
 FUNCTION   : _MApp_ATVProc_NotifyToMainSystem
 USAGE      : set VD and Scaler, ration
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
//void MSrv_ATV_Player::_MApp_ATVProc_NotifyToMainSystem(void)
void MSrv_ATV_Player::UpdateVDandScaler(void)
{
    BOOLEAN bIsTuningProcessorBusy;
    BOOLEAN bNeedTurnOnScreenAndAudio = FALSE;
    BOOLEAN bAspectRatioChanged;
    mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO stVideoARCInfoTmp;
    memset(&stVideoARCInfoTmp, 0 , sizeof(mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO));

    bIsTuningProcessorBusy = msAPI_Tuner_IsTuningProcessorBusy();

    //****************************
    // *** Video Mode Checking ***
    //****************************

    bAspectRatioChanged = mapi_interface::Get_mapi_vd()->IsAspectRatioChanged();
    if((mapi_interface::Get_mapi_vd()->IsVideoFormatChanged())
            || ((FALSE == bIsTuningProcessorBusy) && (TRUE == bAspectRatioChanged))
            || (TRUE == m_ManualScanRtUpdateVDandScaler))
    {
        if(FALSE == IsSignalStable())
        {
#if (ISDB_CC_ENABLE == 1)
            MW_CC::GetInstance()->StopCaption();
#elif((ATSC_CC_ENABLE == 1)||(NTSC_CC_ENABLE == 1))
            MW_CC::GetInstance()->StopCaption();
            MW_CC::GetInstance()->DestoryCaption();
#endif
        }

        ATV_PLAYER_IFO("ATV VD format changed.\n");
        mapi_video_vd_cfg *pVDData = new(std::nothrow) mapi_video_vd_cfg;
        ASSERT(pVDData);
        mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO stVideoARCInfo;
        MAPI_INPUT_SOURCE_TYPE enCurrentInputType = MAPI_INPUT_SOURCE_ATV;
        mapi_vd_datatype::ASPECT_RATIO_TYPE enWSSARCType;
        mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stVideoWinType;
        memset(&stVideoARCInfo, 0, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO));

        pVDData->enVideoStandard = GetVideoStandard();

        if(pVDData->enVideoStandard >= E_MAPI_VIDEOSTANDARD_NOTSTANDARD)
        {
            MAPI_INPUT_SOURCE_TYPE enInputSrc = MAPI_INPUT_SOURCE_ATV;
            MSrv_Control::GetMSrvSystemDatabase()->GetLastVideoStandard(&(pVDData->enVideoStandard), &enInputSrc);

            mapi_interface::Get_mapi_vd()->SetVideoStandard(pVDData->enVideoStandard,FALSE);  //To fix the issue there is a black edge when NTSC-> no signal(unplug RF cable)20110614EL
        }

#if (TV_FREQ_SHIFT_CLOCK == 1)
        if ((FALSE == IsSignalStable()) || (TRUE == m_ManualScanRtUpdateVDandScaler))
        {
            msAPI_Tuner_Patch_TVShiftClk(FALSE);
        }
        else
        {
            msAPI_Tuner_Patch_TVShiftClk(TRUE);
        }
#endif

#if (ISDB_SYSTEM_ENABLE == 1)
        {
            U8 u8CurrentProgramNumber;
            u8CurrentProgramNumber = (U8)MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL);
            if(MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_ATV_AUTOCOLOR , u8CurrentProgramNumber, 0, NULL))
            {
                MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_VIDEO_STANDARD_OF_PROGRAM, u8CurrentProgramNumber, (U16)pVDData->enVideoStandard, NULL);
            }
        }
#endif
        MSrv_Control::GetMSrvPicture()->Off();
        mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetMode(pVDData);


        enWSSARCType = mapi_interface::Get_mapi_vd()->GetAspectRatioCode();//waiting VD class
        MSrv_Control::GetMSrvSystemDatabase()->GetVideoArc(&stVideoARCInfo.enARCType, &enCurrentInputType);
        stVideoARCInfoTmp.enARCType = stVideoARCInfo.enARCType;


        if(((FALSE == bIsTuningProcessorBusy) && (TRUE == bAspectRatioChanged)) || (enWSSARCType != mapi_vd_datatype::ARC_INVALID))
        {

            //In channel edit screen, need to use FULL screen mode
            if(stVideoARCInfo.enARCType == mapi_video_datatype::E_AR_AUTO)
            {
                CalWSSWin(enWSSARCType, &stVideoWinType, &stVideoARCInfoTmp);
            }
        }

        //fixed ATV hot plug in and out cause aspect ratio error issue.
        stVideoARCInfo.s16Adj_ARC_Left = 0;
        stVideoARCInfo.s16Adj_ARC_Right = 0;
        stVideoARCInfo.s16Adj_ARC_Up = 0;
        stVideoARCInfo.s16Adj_ARC_Down = 0;
        stVideoARCInfo.bSetCusWin = MAPI_FALSE;

#if (STEREO_3D_ENABLE == 1)
        MSrv_3DManager* ptr3DMgr = MSrv_Control::GetMSrv3DManager();
        if (IsSignalStable() == TRUE)
        {
            if(ptr3DMgr->Get3DFormatDetectFlag() && (ptr3DMgr->GetCurrent3DFormat() != EN_3D_NONE))
            {
                (MSrv_Control_common::GetMSrv3DManager())->Enable3D(EN_3D_NONE);
            }
        }
#endif

        if(stVideoARCInfo.enARCType == mapi_video_datatype::E_AR_AUTO)
        {
#if (PIP_ENABLE == 1)
            EN_PIP_MODES enPipMode = MSrv_Control::GetInstance()->GetPipMode();
            if(  (enPipMode == E_PIP_MODE_OFF)
               ||(enPipMode==E_PIP_MODE_TRAVELING && m_PipXCWin==MAPI_SUB_WINDOW))
            {
                mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetWindow(NULL, NULL, &stVideoARCInfoTmp);
            }
            else if(enPipMode == E_PIP_MODE_PIP)
            {
                mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stWinInfo;
                if(m_PipXCWin == MAPI_SUB_WINDOW)
                {
                    MSrv_Control::GetInstance()->GetPipSubwindow(&stWinInfo);
                    mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetWindow(NULL, &stWinInfo, &stVideoARCInfoTmp);
                }
                else
                {
                    mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetWindow(NULL, NULL, &stVideoARCInfoTmp);
                }
            }
            else
            {
                mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stWinInfo;
                if(m_PipXCWin == MAPI_SUB_WINDOW)
                    MSrv_Control::GetInstance()->GetPipSubwindow(&stWinInfo);
                else
                    MSrv_Control::GetInstance()->GetMainwindow(&stWinInfo);

                mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetWindow(NULL, &stWinInfo, &stVideoARCInfoTmp);
            }
#else
            mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetWindow(NULL, NULL, &stVideoARCInfoTmp);
#endif
        }
        else
        {
#if (PIP_ENABLE == 1)
            EN_PIP_MODES enPipMode = MSrv_Control::GetInstance()->GetPipMode();
            if(  (enPipMode == E_PIP_MODE_OFF)
               ||(enPipMode==E_PIP_MODE_TRAVELING && m_PipXCWin==MAPI_SUB_WINDOW))
            {
                mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetWindow(NULL, NULL, &stVideoARCInfo);
            }
            else if(enPipMode == E_PIP_MODE_PIP)
            {
                mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stWinInfo;
                if(m_PipXCWin == MAPI_SUB_WINDOW)
                {
                    MSrv_Control::GetInstance()->GetPipSubwindow(&stWinInfo);
                    mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetWindow(NULL, &stWinInfo, &stVideoARCInfo);
                }
                else
                {
                    mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetWindow(NULL, NULL, &stVideoARCInfo);
                }
            }
            else
            {
                mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stWinInfo;
                if(m_PipXCWin == MAPI_SUB_WINDOW)
                    MSrv_Control::GetInstance()->GetPipSubwindow(&stWinInfo);
                else
                    MSrv_Control::GetInstance()->GetMainwindow(&stWinInfo);

                mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetWindow(NULL, &stWinInfo, &stVideoARCInfo);
            }
#else
            mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetWindow(NULL, NULL, &stVideoARCInfo);
#endif
        }

#if (PIP_ENABLE == 1)
        // temp solution, need refine it later
        if(m_PipXCWin == MAPI_MAIN_WINDOW)
        {
            MSrv_Control::GetMSrvPicture()->On();
        }
        else
        {
            MSrv_Control::GetMSrvVideo()->SelectWindow(MAPI_SUB_WINDOW);
            MSrv_Control::GetMSrvPicture()->On();
            MSrv_Control::GetMSrvVideo()->SelectWindow(MAPI_MAIN_WINDOW);
        }
#else
        MSrv_Control::GetMSrvPicture()->On();
#endif

#if (ENABLE_V_RANGE_HANDLE_ATV)
        MApp_VD_StartRangeHandle();
#endif
        bNeedTurnOnScreenAndAudio = TRUE;


        if(pVDData != NULL)
        {
            delete pVDData;
            pVDData = NULL;
        }
        m_ManualScanRtUpdateVDandScaler = 0;
    }

    if(mScan->IsScanning() == FALSE)
    {
        //****************************
        // *** Block Program Check
        //****************************
        if(m_bCurrentProgramBlock == TRUE)
        {
            bNeedTurnOnScreenAndAudio = FALSE;
        }

        //****************************
        // *** Mute Screen Check
        //****************************
        if(m_bChannelChangeFreezeImageFlags == TRUE)
        {
            if(bNeedTurnOnScreenAndAudio == TRUE)
            {
                if((m_pcVideo->IsFreezeImage()) == TRUE)
                    m_pcVideo->FreezeImage(FALSE);
                if(FALSE == IsSignalStable())
                {
                    printf("*********SignalStable False**********\n");
            #if (ENABLE_ATV_NOSINGAL_BLACKSCREEN==1)
                    MSrv_Control::GetInstance()->SetVideoMute(TRUE, 0 , m_PipXCWin);
            #else
                    MSrv_Control::GetInstance()->SetVideoMute(FALSE, 0 , m_PipXCWin);
            #endif
                    //MSrv_Control::GetInstance()->SetVideoMute(TRUE, mapi_video_datatype::E_SCREEN_MUTE_BLACK, 0);

                    MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_BYSYNC_MUTEON_, m_CurrentSrcType);
                }
                else //if(IsSignalStable() &&(ATVMute))
                {
                    printf("*********SignalStable True**********\n");
                    MSrv_Control::GetInstance()->SetVideoMute(FALSE, 0 , m_PipXCWin);
                    MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_PERMANENT_MUTEOFF_, m_CurrentSrcType);
                    MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_BYSYNC_MUTEOFF_, m_CurrentSrcType);
#if (ISDB_CC_ENABLE == 1)
                    MW_CC::GetInstance()->StartCaption(E_NORMAL_CAPTION);
#elif((ATSC_CC_ENABLE == 1)||(NTSC_CC_ENABLE == 1))
                    MW_CC::GetInstance()->StartVchip();
                    MW_CC::GetInstance()->StartCaption();
#endif


#if (VCHIP_ENABLE == 1)
                    MSrv_Control::GetInstance()->VChipConnect();
#endif
                }
            }

        }
        else
        {
            if(bNeedTurnOnScreenAndAudio == TRUE)
            {
                MS_USER_SYSTEM_SETTING stGetSystemSetting;
                MSrv_Control::GetMSrvSystemDatabase()->GetUserSystemSetting(&stGetSystemSetting);
                if(stGetSystemSetting.AudioOnly == FALSE)
                {
            #if (ENABLE_ATV_NOSINGAL_BLACKSCREEN==1)
                     if((TRUE == IsSignalStable())&&(m_bIsScanning ==FALSE))
            #endif
                    {
                        MSrv_Control::GetInstance()->SetVideoMute(FALSE, 0, m_PipXCWin);
                    }
                    if(TRUE == IsSignalStable())
                    {
#if (ISDB_CC_ENABLE == 1)
                MW_CC::GetInstance()->StartCaption(E_NORMAL_CAPTION);
#elif ((ATSC_CC_ENABLE == 1)||(NTSC_CC_ENABLE ==1))
                MW_CC::GetInstance()->StartVchip();
                MW_CC::GetInstance()->StartCaption();
#endif


#if (VCHIP_ENABLE == 1)
                    MSrv_Control::GetInstance()->VChipConnect();
#endif
                    }
                }
                m_u8StartChangeProgram = 0;
            }
        }

#if (CVBSOUT_XCTOVE_ENABLE == 0)
#if (VE_ENABLE == 1 ||CVBSOUT_ENABLE==1)
        if(IsVideoOutFreeToUse() == TRUE)
        {
            if(bNeedTurnOnScreenAndAudio)
            {
                if(mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_MONITOR_MODE)->IsDestTypeExistent(m_PipXCWin))
                {
                    mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_MONITOR_MODE)->SetMode(mapi_video_out_datatype::MAPI_VIDEO_OUT_VE_AUTO, m_PipXCWin);
#if (INPUT_SOURCE_LOCK_ENABLE == 1)
                    //Check input source lock(ISL) status of current source
                    if (MSrv_Control::GetInstance()->IsInputSourceLock(m_CurrentSrcType) == MAPI_FALSE)
#endif
                    {
                        //Original unmute flow without input source lock(ISL) status checking
                        mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_MONITOR_MODE)->SetVideoMute(FALSE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, m_PipXCWin);
                    }
                }

                if((mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_TV_MODE)->IsDestTypeExistent(m_PipXCWin)) && IsVideoOutTVModeFreeToUse())
                {
                    mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_TV_MODE)->SetMode(mapi_video_out_datatype::MAPI_VIDEO_OUT_VE_AUTO, m_PipXCWin);
#if (INPUT_SOURCE_LOCK_ENABLE == 1)
                    //Check input source lock(ISL) status of current source
                    if (MSrv_Control::GetInstance()->IsInputSourceLock(m_CurrentSrcType) == MAPI_FALSE)
#endif
                    {
                        //Original unmute flow without input source lock(ISL) status checking
                        mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_TV_MODE)->SetVideoMute(FALSE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, m_PipXCWin);
                    }
                }
            }
        }
#endif
#endif
    }
}

BOOL MSrv_ATV_Player::InitATVForOtherSource()
{
#if (AUTO_TEST == 1)
    printf("[AT][SN][init atv][%u]\n",  mapi_time_utility::GetPiuTimer1());
#endif
        U32 u32UhfMaxFreq;
        U32 u32UhfMinFreq;
        U32 u32VhfLowMinFreq;
        U32 u32VhfHighMinFreq;
        U32 u32VhfLowMaxFreq;
        U32 u32VhfHighMaxFreq;
        EN_FREQ_STEP enFreqStep;
        U32 u32StepSize;
        mapi_tuner* pTuner;
        pTuner = mapi_interface::Get_mapi_pcb()->GetAtvTuner(0);
        ASSERT(pTuner);
        pTuner->ExtendCommand(
        mapi_tuner_datatype::E_TUNER_SUBCMD_GET_UHF_MAX_FREQ,
            0,
            0,
            &u32UhfMaxFreq);
        pTuner->ExtendCommand(
        mapi_tuner_datatype::E_TUNER_SUBCMD_GET_UHF_MIN_FREQ,
            0,
            0,
            &u32UhfMinFreq);
        pTuner->ExtendCommand(
        mapi_tuner_datatype::E_TUNER_SUBCMD_GET_VHF_LOWMIN_FREQ,
            0,
            0,
            &u32VhfLowMinFreq);
        pTuner->ExtendCommand(
        mapi_tuner_datatype::E_TUNER_SUBCMD_GET_VHF_HIGHMIN_FREQ,
            0,
            0,
            &u32VhfHighMinFreq);
        pTuner->ExtendCommand(
        mapi_tuner_datatype::E_TUNER_SUBCMD_GET_VHF_HIGHMAX_FREQ,
            0,
            0,
            &u32VhfHighMaxFreq);
        pTuner->ExtendCommand(
        mapi_tuner_datatype::E_TUNER_SUBCMD_GET_VHF_LOWMAX_FREQ,
            0,
            0,
            &u32VhfLowMaxFreq);
        m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_FREQ_STEP, 0, 0, &enFreqStep);
        if(enFreqStep == E_FREQ_STEP_62_5KHz)
        {
            u32StepSize = 6250;
        }
        else if(enFreqStep == E_FREQ_STEP_50KHz)
        {
            u32StepSize = 5000;
        }
        else
        {
            u32StepSize = 3125;
        }
        m_u16UhfMaxPll = ((U16)(((u32UhfMaxFreq + 4000L) * 100) / u32StepSize));
        m_u16UhfMinPll = ((U16)(((u32VhfHighMaxFreq + ((u32UhfMinFreq - u32VhfHighMaxFreq) / 2)) * 100) / u32StepSize));
        m_u16VhfLowMinPll = ((U16)(((u32VhfLowMinFreq - 3000L) * 100) / u32StepSize));
        m_u16VhfHighMinPll = ((U16)(((u32VhfLowMaxFreq + ((u32VhfHighMinFreq - u32VhfLowMaxFreq) / 2)) * 100) / u32StepSize));
        m_u16DefaultPll = m_u16UhfMaxPll + 10;
        ATV_PLAYER_IFO("---> MSrv_ATV_Player::Init-Exit \n");
        m_bInitAtvDemodTuner = FALSE;
        EnableAFT(TRUE);
        // config audio path for scart out
        #if (PIP_ENABLE == 0)
        mapi_interface::Get_mapi_audio()->InputSource_ChangeAudioSource(MAPI_INPUT_SOURCE_ATV, AUDIO_PROCESSOR_SCART);
        #endif
        InitAtvDemodTuner();
        return TRUE;

}

void MSrv_ATV_Player::GetSmartScanMode(BOOL* const pbSmartScanMode)
{
    *pbSmartScanMode = m_bSmartScan;
    return;
}

void MSrv_ATV_Player::SetSmartScanMode(BOOL const bSmartScanMode)
{
    m_bSmartScan = bSmartScanMode;
    return;
}

void MSrv_ATV_Player::StartEvenTimer()
{
    if(FALSE == m_stAtvEvenTimer.timerSwitch)
    {
        m_stAtvEvenTimer.startTime = (U32)clock();
        m_stAtvEvenTimer.timerSwitch=TRUE;
        return ;
    }
    else
        return;
}
void MSrv_ATV_Player::GetEvenTimer(ST_ATV_EVENT_TIMER* const pTimer)
{
    pTimer->timerSwitch = m_stAtvEvenTimer.timerSwitch;
    pTimer->startTime = m_stAtvEvenTimer.startTime;
    return;
}

void MSrv_ATV_Player::StopEvenTimer()
{
     m_stAtvEvenTimer.timerSwitch = FALSE;
     m_stAtvEvenTimer.startTime = 0;
     return;
}

void MSrv_ATV_Player::GetAtvScanParam(ST_ATV_SCAN_PARAMETER* const pstAtvScanParam)
{
    pstAtvScanParam->u32EventInterval = m_stAtvScanParam.u32EventInterval;
    pstAtvScanParam->u32StartFrequency = m_stAtvScanParam.u32StartFrequency;
    pstAtvScanParam->u32EndFrequency = m_stAtvScanParam.u32EndFrequency;
    pstAtvScanParam->u16TotalChannelNum = m_stAtvScanParam.u16TotalChannelNum;
    pstAtvScanParam->u8ScanState = m_stAtvScanParam.u8ScanState;
    pstAtvScanParam->u8ScanType = m_stAtvScanParam.u8ScanType;
    pstAtvScanParam->aATVMannualTuneMode = m_stAtvScanParam.aATVMannualTuneMode;
    return;
}

/*
 ********************************************
 FUNCTION   : _SetDefaultStationName
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
void MSrv_ATV_Player::_SetDefaultStationName(U8 *sStationName)
{
    int i;

    for(i = 0; i < (MAX_STATION_NAME - 1); i++)
    {
        sStationName[i] = '-';
    }

    sStationName[MAX_STATION_NAME-1] = '\0';
}

/*
 ********************************************
 FUNCTION   : _IsLPrime
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
BOOLEAN MSrv_ATV_Player::_IsLPrime(void)
{
    U16 wLPrimeBoundaryPLL;
    EN_FREQ_STEP eFreqStep = E_FREQ_STEP_INVALD;

    m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_FREQ_STEP, 0, 0, &eFreqStep);

    switch(eFreqStep)
    {
        case E_FREQ_STEP_31_25KHz:
            wLPrimeBoundaryPLL = (U16)((L_PRIME_BOUNDARY_FREQ * 100) / 3125);
            break;
        case E_FREQ_STEP_50KHz:
            wLPrimeBoundaryPLL = (U16)(L_PRIME_BOUNDARY_FREQ / 50);
            break;
        case E_FREQ_STEP_62_5KHz:
            wLPrimeBoundaryPLL = (U16)((L_PRIME_BOUNDARY_FREQ * 10) / 625);
            break;
        default:
            ASSERT(0);
            break;
    }

    ATV_PLAYER_DBG("IsLPrime:  SIF_GetAudioStandard:%d =0x09? && tunerPLL:%d<%d?", mapi_interface::Get_mapi_audio()->SIF_GetAudioStandard(), m_u16TunerPLL, wLPrimeBoundaryPLL);
    if((mapi_interface::Get_mapi_audio()->SIF_GetAudioStandard() == E_AUDIOSTANDARD_L_)
            && (m_u16TunerPLL < wLPrimeBoundaryPLL))
    {
        return TRUE;
    }
    else
    {
        return FALSE;
    }
}

/*
 ********************************************
 FUNCTION   : _SetVifIfFreq
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
BOOLEAN MSrv_ATV_Player::_SetVifIfFreq(void)
{
    U16 u16IFFreqKHz = 0;

    if(FALSE == _IsLPrime())
    {
        m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_IF_FREQ, 0, 0, &u16IFFreqKHz);
    }
    else
    {
        m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_L_PRIME_IF_FREQ, 0, 0, &u16IFFreqKHz);
    }

    if(m_u16IfFreqPre != u16IFFreqKHz)
    {
        m_pDemodulator->ATV_SetVIF_IfFreq(u16IFFreqKHz);
        m_u16IfFreqPre = u16IFFreqKHz;
    }

    return TRUE;
}

/*
 ********************************************
 FUNCTION   : _SetTunerPLL
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
void MSrv_ATV_Player::_SetTunerPLL(U16 u16PLL)
{
    U32 u32FreqKHz = 0;
    U16 u16FinalPll;
    AUDIOSTANDARD_TYPE_ eAudioStandard;

    if(u16PLL < m_u16VhfLowMinPll)
    {
        u16PLL = m_u16VhfLowMinPll;
    }
    else if(m_u16UhfMaxPll < u16PLL)
    {
        if(u16PLL == m_u16DefaultPll) //DEFAULT_PLL
        {
            ; //keep original Default PLL
        }
        else
        {
            u16PLL = m_u16UhfMaxPll;
        }
    }

    m_u16TunerPLL = u16PLL;

    if((m_u8StartChangeProgram == 0) && IsAFTEnabled() && (IS_RT_AFT_ENABLED == TRUE))  //AFT
    {
        u16FinalPll = (m_u16TunerPLL - AFT_OFFSET_0) + m_u8AftOffset;
    }
    else
    {
        u16FinalPll = m_u16TunerPLL;
    }

    ATV_PLAYER_DBG("\n<%03u.%03u>\n",
                   msAPI_CFT_ConvertPLLtoIntegerOfFrequency(m_u16TunerPLL),
                   msAPI_CFT_ConvertPLLtoFractionOfFrequency(m_u16TunerPLL));

    u32FreqKHz = (U32) msAPI_CFT_ConvertPLLtoIntegerOfFrequency(u16FinalPll);
    u32FreqKHz = (u32FreqKHz * 1000) + msAPI_CFT_ConvertPLLtoFractionOfFrequency(u16FinalPll);

    ATV_PLAYER_IFO("SetTunerPLL:  Frequency = %d \n", (int)u32FreqKHz);
    mapi_interface::Get_mapi_pcb()->EnableTunerI2cPath(TRUE);
    eAudioStandard = mapi_interface::Get_mapi_audio()->SIF_GetAudioStandard();

    EN_TUNER_MODE eTunerMode = E_TUNER_INVALID;

    if(SystemInfo::GetInstance()->get_ATVSystemType() == E_NTSC_ENABLE)
    {
        eTunerMode = E_TUNER_ATV_NTSC_MODE;
    }
    else
    {
        if(_IsLPrime())
        {
            eTunerMode = E_TUNER_ATV_SECAM_L_PRIME_MODE;
        }
        else
        {
            eTunerMode = E_TUNER_ATV_PAL_MODE;
        }
    }
    m_pTuner->ATV_SetTune(u32FreqKHz, MW_ATV_Util::GetInstance()->GetBand(u16PLL), eTunerMode,(U8)eAudioStandard);

    mapi_interface::Get_mapi_pcb()->EnableTunerI2cPath(FALSE);

    if(m_pDemodulator->ATV_IsInternalVIF())
    {
        _SetVifIfFreq();
    }

#if (TV_FREQ_SHIFT_CLOCK)
    _Set_Shift_Freq(u32FreqKHz);
#endif

    switch(mapi_interface::Get_mapi_audio()->SIF_ConvertToBasicAudioStandard(eAudioStandard))
    {
        case E_AUDIOSTANDARD_BG_:
            m_pDemodulator->ATV_SetIF(mapi_demodulator_datatype::IF_FREQ_B);
            break;

        case E_AUDIOSTANDARD_I_:
            m_pDemodulator->ATV_SetIF(mapi_demodulator_datatype::IF_FREQ_I);
            break;

        case E_AUDIOSTANDARD_DK_:
            m_pDemodulator->ATV_SetIF(mapi_demodulator_datatype::IF_FREQ_DK);
            break;

        case E_AUDIOSTANDARD_L_:
            m_pDemodulator->ATV_SetIF(mapi_demodulator_datatype::IF_FREQ_L);
            break;

        case E_AUDIOSTANDARD_M_:
            m_pDemodulator->ATV_SetIF(mapi_demodulator_datatype::IF_FREQ_MN);
            break;
        default:
            break;
    }
    m_pDemodulator->ATV_SetPeakingParameters(MW_ATV_Util::GetInstance()->GetBand(u16PLL));
}


/*
 ********************************************
 FUNCTION   : _DetectStationName
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
void MSrv_ATV_Player::_DetectStationName(void)
{
    msAPI_Tuner_ConvertMediumAndChannelNumberToString(m_eMedium, m_u8ChannelNumber, m_au8CurrentStationName);
}

/*
 ********************************************
 FUNCTION   : msAPI_FrontEnd_Init
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
void MSrv_ATV_Player::msAPI_FrontEnd_Init(void)
{
#if(MSTAR_TVOS == 1)
    U16 u16Num=0;
    GetTotalChannelNumber(&u16Num);

    if(u16Num&&(MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(IS_PROGRAM_LOCKED, MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL), 0, NULL)))
#else
    if(MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(IS_PROGRAM_LOCKED, MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL), 0, NULL))
#endif
    {
      m_eCurrentTuningState = AFT_IDLE;
      return;
    }
    _SetTunerPLL(MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_PROGRAM_PLL_DATA, MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL), 0, NULL));
    msAPI_Tuner_SetIF();
    m_u16IdleTimer = WAIT_N_ms(0);//????? For Audio detection and fine tune
    m_bIsAFTNeeded = FALSE;
    //;;?????;; ATVGetMediumAndChannelNumber(ATVGetCurrentProgramNumber(), &m_eMedium, &m_u8ChannelNumber);
    m_eCurrentTuningState = AFT_IDLE;

    _SetDefaultStationName(m_au8CurrentStationName);
}

/*
 ********************************************
 FUNCTION   : msAPI_Tuner_IsTuningProcessorBusy
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
BOOLEAN MSrv_ATV_Player::msAPI_Tuner_IsTuningProcessorBusy(void)
{
    mapi_scope_lock(scopeLock, &m_mutex_Scan);

    if(m_eCurrentTuningState == AFT_IDLE)
    {
        return FALSE;
    }

    return TRUE;
}

/******************************************************************************/
///- This API is called by MApp_ATVProc_Handler to keep tuning work.
/// @param eState \b IN: AFT_EXT_STEP_PERIODIC - This enum is called by ATVProc_Handler(). Don't call any other place except ATVProc_Handler().
///                  IN: AFT_EXT_STEP_SEARCHALL - This enum will start auto-tuning from VHF low to UHF max.
///                  IN: AFT_EXT_STEP_SEARCHONETOUP - This enum will search up for next one available channel.
///                  IN: AFT_EXT_STEP_SEARCHONETODOWN - This enum will search up for next one available channel.
///                  IN: AFT_EXT_STEP_SEARCH_STOP - This enum will stop searching.
/******************************************************************************/
#if 0
/*
 ********************************************
 FUNCTION   : TunerChangeProgram
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/

/******************************************************************************/
///- This API is called by change program.
/******************************************************************************/
void MSrv_ATV_Player::TunerChangeProgram(void)
{
    AUDIOSTANDARD_TYPE_ eAudioStandard;
    AUDIOMODE_TYPE_ eSavedAudioMode;
    U8 u8CurrentProgramNumber;

    u8CurrentProgramNumber = MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, NULL, NULL, NULL);
    m_bIsAFTNeeded = MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(IS_AFT_NEED , u8CurrentProgramNumber, NULL, NULL);
    _SetTunerPLL(MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_PROGRAM_PLL_DATA, u8CurrentProgramNumber, NULL, NULL));

    if(m_bIsAFTNeeded == FALSE)
    {
        _SetTunerPLL(PLLSTEP(MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_FINE_TUNE , (u8CurrentProgramNumber)*FINE_TUNE_STEP, NULL, NULL)));
    }

    mapi_interface::Get_mapi_vd()->SetVideoStandard((MAPI_AVD_VideoStandardType)MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_VIDEO_STANDARD_OF_PROGRAM , u8CurrentProgramNumber, NULL, NULL), FALSE);

    eAudioStandard = (AUDIOSTANDARD_TYPE_)MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_AUDIO_STANDARD , u8CurrentProgramNumber, NULL, NULL); //????
    MSrv_Control::GetMSrvAtvDatabase()->CommondCmd(GET_AUDIO_MODE  , NULL , NULL , &eSavedAudioMode);

    //mapi_interface::Get_mapi_audio()->AdjustAudioFactor(E_ADJUST_AUDIOMUTE_DURING_LIMITED_TIME_, 700, E_AUDIOMUTESOURCE_ACTIVESOURCE_); //C.P.Chen 2007/12/06
    mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard(eAudioStandard);

    msAPI_Tuner_SetIF();

    if(FALSE == mapi_interface::Get_mapi_vd()->IsSyncLocked())
    {
        mapi_interface::Get_mapi_vd()->SetForcedFreeRun(TRUE);
        mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_BYSYNC_MUTEON_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
    }
    else
    {
        mapi_interface::Get_mapi_vd()->SetForcedFreeRun(FALSE);
        //FIXME mapi_interface::Get_mapi_vd()->WaitForVideoSyncLock();
        mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_BYSYNC_MUTEOFF_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
    }

    ATVGetMediumAndChannelNumber(u8CurrentProgramNumber, &m_eMedium , &m_u8ChannelNumber);
    MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_STATION_NAME , u8CurrentProgramNumber , NULL , m_au8CurrentStationName);
}
#endif


/*
 ********************************************
 FUNCTION   : TunerGetMedium
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
/******************************************************************************/
///- This function is called to get current tuning interface.
/// @return MEDIUM: MEDIUM_CABLE or MEDIUM_AIR.
/******************************************************************************/
MSrv_ATV_Database::MEDIUM MSrv_ATV_Player::TunerGetMedium(void)
{
    return m_eMedium;
}

/*
 ********************************************
 FUNCTION   : TunerGetChannelNumber
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
#if 0
/******************************************************************************/
///- This function is called to get current channel number.
/// @return U8: channel number.
/* ****************************************************************************/
U8 MSrv_ATV_Player::TunerGetChannelNumber(void)
{
    return m_u8ChannelNumber;
}
#endif

/*
 ********************************************
 FUNCTION   : msAPI_Tuner_AdjustUnlimitedFineTune
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/

/******************************************************************************/
///- This function is called to adjust fine-tune.
/// @param eDirection \b IN: Direction to adjust tuner PLL.
/******************************************************************************/
void MSrv_ATV_Player::msAPI_Tuner_AdjustUnlimitedFineTune(DIRECTION eDirection)
{
    U8 u8Ksel = 0;
    U8 u8PdInv = 0;

    m_bIsAFTNeeded = FALSE;

    //msAPI_AUD_EnableRealtimeAudioDetection(FALSE);
    m_pTuner->SetTunerInFinetuneMode(MAPI_TRUE);

    if(m_pDemodulator->ATV_IsInternalVIF())
    {
        mapi_interface::Get_mapi_vif()->VifSetParameter(mapi_vif_datatype::E_VIF_PARA_GET_K_SEL, &u8Ksel, sizeof(u8Ksel));
        mapi_interface::Get_mapi_vif()->VifSetParameter(mapi_vif_datatype::E_VIF_PARA_GET_PD_INV, &u8PdInv, sizeof(u8PdInv));

        if(u8Ksel == 1)
        {
            U32 kSel = 0;
            U32 hwKpKi = 0x11;
            U32 u32SetpPdInv=0x00; // disabl

            mapi_interface::Get_mapi_vif()->VifSetParameter(mapi_vif_datatype::E_VIF_PARA_K_SEL, &kSel, sizeof(kSel));
            mapi_interface::Get_mapi_vif()->VifSetParameter(mapi_vif_datatype::E_VIF_PARA_SET_HW_KPKI, &hwKpKi, sizeof(hwKpKi));
            if (u8PdInv==1)
            {
                mapi_interface::Get_mapi_vif()->VifSetParameter(mapi_vif_datatype::E_VIF_PARA_SET_PD_INV, &u32SetpPdInv, sizeof(u32SetpPdInv));
            }
        }
    }

    //m_bIsAFTNeeded = FALSE;

    if(eDirection == DIRECTION_UP)
    {
        if(TunerGetCurrentChannelPLL() >= m_u16UhfMaxPll)
        {
            m_u16TunerPLL = m_u16VhfLowMinPll;
        }

        _SetTunerPLL(PLLSTEP(FINE_TUNE_STEP));
    }
    else
    {
        if(TunerGetCurrentChannelPLL() <= m_u16VhfLowMinPll)
        {
            m_u16TunerPLL = m_u16UhfMaxPll;
        }

        _SetTunerPLL(PLLSTEP(-FINE_TUNE_STEP));
    }

    if(m_pDemodulator->ATV_IsInternalVIF())
    {
        if(u8Ksel == 1)
        {
            U32 kSel = 1;
            U32 hwKpKi;//=(VIF_CR_KI1<<4)|VIF_CR_KP1;
            U8 u8VifCrKi1 = 0, u8VifCrKp1 = 0;
            U32 u32SetpPdInv=0x01; // disabl

            m_pDemodulator->ExtendCmd(5, 0, 0, &u8VifCrKi1);
            m_pDemodulator->ExtendCmd(6, 0, 0, &u8VifCrKp1);
            hwKpKi = (u8VifCrKi1 << 4) | u8VifCrKp1;

            mapi_interface::Get_mapi_vif()->VifSetParameter(mapi_vif_datatype::E_VIF_PARA_K_SEL, &kSel, sizeof(kSel));
            mapi_interface::Get_mapi_vif()->VifSetParameter(mapi_vif_datatype::E_VIF_PARA_SET_HW_KPKI, &hwKpKi, sizeof(hwKpKi));

            if (u8PdInv==1)
            {
                mapi_interface::Get_mapi_vif()->VifSetParameter(mapi_vif_datatype::E_VIF_PARA_SET_PD_INV, &u32SetpPdInv, sizeof(u32SetpPdInv));
            }
        }
    }
    _DetectStationName();
    m_pTuner->SetTunerInFinetuneMode(MAPI_FALSE);
}

/*
 ********************************************
 FUNCTION   : msAPI_Tuner_SetIF
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
/******************************************************************************/
///- This function is called to check demodulator's setting. It depends on audio standard.
/******************************************************************************/
void MSrv_ATV_Player::msAPI_Tuner_SetIF(void)//;?????;;It's Sound System, need to change name?
{
    AUDIOSTANDARD_TYPE_ eAudioStandard = E_AUDIOSTANDARD_BG_;
    UNUSED(eAudioStandard);
    U16 u16IFFreqKHz = 0;

    if(_IsLPrime() == TRUE)// TV_SOUND_LL
    {
        m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_L_PRIME_IF_FREQ, 0, 0, &u16IFFreqKHz);
        //ATV_Scan_DBG("\r\nIFFreqKHz = %u", u16IFFreqKHz);
        m_pDemodulator->ATV_SetVIF_IfFreq(u16IFFreqKHz);
        usleep(10 * 1000);
    }
    else
    {
        m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_IF_FREQ, 0, 0, &u16IFFreqKHz);
        //ATV_Scan_DBG("\r\nIFFreqKHz = %u", u16IFFreqKHz);
        m_pDemodulator->ATV_SetVIF_IfFreq(u16IFFreqKHz);
        usleep(10 * 1000);
    }

    if(_IsLPrime() == TRUE)
    {
//;;?????         SECAM_L_PRIME_ON();
        m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_LP_);
        ATV_PLAYER_DBG("\n    IF_FREQ_L_PRIME\n");
    }
    else
    {
//;;?????          SECAM_L_PRIME_OFF();
        eAudioStandard = mapi_interface::Get_mapi_audio()->SIF_GetAudioStandard();
#if (ISDB_SYSTEM_ENABLE == 1)
        m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_);
        ATV_PLAYER_DBG("\n    IF_FREQ_MN\n");
#else
    switch(eAudioStandard)
    {
        case E_AUDIOSTANDARD_BG_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_BG_);
            break;

        case E_AUDIOSTANDARD_NOTSTANDARD_:
        case E_AUDIOSTANDARD_BG_A2_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_BG_A2_);
            break;
        case E_AUDIOSTANDARD_BG_NICAM_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_BG_NICAM_);
            break;
        case E_AUDIOSTANDARD_I_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_I_);
            break;
        case E_AUDIOSTANDARD_DK_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_DK_);
            break;
        case E_AUDIOSTANDARD_DK1_A2_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_DK1_A2_);
            break;
        case E_AUDIOSTANDARD_DK2_A2_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_DK2_A2_);
            break;
        case E_AUDIOSTANDARD_DK3_A2_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_DK3_A2_);
            break;
        case E_AUDIOSTANDARD_DK_NICAM_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_DK_NICAM_);
            break;
        case E_AUDIOSTANDARD_L_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_L_);
            break;
        case E_AUDIOSTANDARD_M_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_);
            break;
        case E_AUDIOSTANDARD_M_BTSC_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_BTSC_);
            break;
        case E_AUDIOSTANDARD_M_A2_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_A2_);
            break;
        case E_AUDIOSTANDARD_M_EIA_J_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_EIA_J_);
            break;

        default:
            break;
    }
#if (CHINA_ENABLE == 1)
    #if (TV_FREQ_SHIFT_CLOCK==0)//mark for ledgement distrubance in atv
    mapi_interface::Get_mapi_vif()->SetSSC(TRUE);
    #endif
    {//Mstar Add for ATV Descrambler box delay adjustment
        MS_Factory_NS_VIF_SET NSVIFValue;
        MSrv_Control::GetMSrvSystemDatabase()->GetFactoryExtSetting((&NSVIFValue), EN_FACTORY_EXT_NSVIF);
        //printf("\nChinaDescramblerBox :%d\n",NSVIFValue.ChinaDescramblerBox);
        MAPI_U32 u16ChinaDescramblerBoxDelay = 0;
        if( NSVIFValue.ChinaDescramblerBox == 0)
        {
            u16ChinaDescramblerBoxDelay = GET_DESCRAMBLER_BOX_DELAY((MAPI_U32)NSVIFValue.ChinaDescramblerBoxDelay + SystemInfo::GetInstance()->GetChinaDescramblerBoxDelayOffset());
            //printf("\nVifDelayReduce:%d, u16ChinaDescramblerBoxDelay :%lx\n",NSVIFValue.ChinaDescramblerBoxDelay , u16ChinaDescramblerBoxDelay);
            mapi_interface::Get_mapi_vif()->VifSetParameter(mapi_vif_datatype::E_VIF_PARA_SET_DESCRAMBLERBOX_DELAY, &u16ChinaDescramblerBoxDelay, sizeof(u16ChinaDescramblerBoxDelay));
        }
    }
#endif
#endif
    }
}

/*
 ********************************************
 FUNCTION   : TunerGetCurrentChannelPLL
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
/******************************************************************************/
///- This function is called to get current channel PLL.
/// @return U16: current PLL value of tuner.
/******************************************************************************/
U16 MSrv_ATV_Player::TunerGetCurrentChannelPLL(void)
{
    return m_u16TunerPLL;
}

/*
 ********************************************
 FUNCTION   : TunerIsCurrentChannelAndSavedChannelSame
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
BOOLEAN MSrv_ATV_Player::TunerIsCurrentChannelAndSavedChannelSame(void)
{
#if (ATSC_SYSTEM_ENABLE == 1) // NTSC
    return FALSE;
#else
    U16 u16SavedTunerPLL;
    u16SavedTunerPLL = MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_PROGRAM_PLL_DATA, MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL), 0, NULL);

    if((MW_ATV_Util::GetInstance()->CFTGetMedium(u16SavedTunerPLL) == MW_ATV_Util::GetInstance()->CFTGetMedium(m_u16TunerPLL))
            && (MW_ATV_Util::GetInstance()->CFTGetChannelNumber(u16SavedTunerPLL) == MW_ATV_Util::GetInstance()->CFTGetChannelNumber(m_u16TunerPLL)))
    {
        return TRUE;
    }

    return FALSE;
#endif
}
/*
 ********************************************
 FUNCTION   : msAPI_Tuner_ConvertMediumAndChannelNumberToString
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
void MSrv_ATV_Player::msAPI_Tuner_ConvertMediumAndChannelNumberToString(MSrv_ATV_Database::MEDIUM eMedium, U8 u8ChannelNumber, U8 * sStationName)
{
    if(eMedium == MSrv_ATV_Database::MEDIUM_AIR)
    {
        sStationName[0] = 'C';    // Air
    }
    else
    {
        sStationName[0] = 'S';    // Cable
    }

    sStationName[1] = '-';
    sStationName[2] = (u8ChannelNumber / 10) + '0';
    sStationName[3] = (u8ChannelNumber % 10) + '0';
    sStationName[4] = ' ';
    sStationName[5] = '\0';
}

#if 0
//No more use
/*
 ********************************************
 FUNCTION   : TunerGetCurrentStationName
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
void MSrv_ATV_Player::TunerGetCurrentStationName(U8 *sName)
{
#if (ATSC_SYSTEM_ENABLE == 0)
    mapi_scope_lock(scopeLock, &m_mutex_Scan);

    memcpy(sName, m_au8CurrentStationName, MAX_STATION_NAME);
#endif
}
#endif
/*
 ********************************************
 FUNCTION   : IsAFTEnabled
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
BOOLEAN MSrv_ATV_Player::TunerIsAFTNeeded(void)
{
    return m_bIsAFTNeeded;
}
BOOLEAN MSrv_ATV_Player::IsAFTEnabled(void)
{
    return m_bIsAFTNeeded;
}


//-------------------------------------------------------------------------------------------------
/// Set AFT flag for difference class
/// @param bAFTNeed \b  IN: AFT needed flag
/// @return none
//-------------------------------------------------------------------------------------------------
void MSrv_ATV_Player::SetTunerAFTNeeded(BOOLEAN bAFTNeed)
{
        m_bIsAFTNeeded = bAFTNeed;
}
void MSrv_ATV_Player::EnableAFT(BOOLEAN bAFTNeed)
{
    m_bIsAFTNeeded = bAFTNeed;
}
/*
 ********************************************
 FUNCTION   : msAPI_Tuner_ConvertMediumAndChannelNumberToString
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/

/*
 ********************************************
 FUNCTION   : msAPI_Tuning_IsScanL
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
void MSrv_ATV_Player::msAPI_Tuning_IsScanL(BOOLEAN bEnable)
{
    m_bIsLSearch = bEnable;
}
//------------------------------------------------------------------------------
// Local Functions.
//------------------------------------------------------------------------------

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//+++++++++++++++ Add New Function:msAPI_Tuning_PAL.cpp ++++++++++++++++++++++++
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

//------------------------------------------------------
//
//------------------------------------------------------
/*
 ********************************************
 FUNCTION   : _GetTuningStatus
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
MSrv_ATV_Player::TUNING_STATUS MSrv_ATV_Player::_GetTuningStatus(void)
{
    mapi_demodulator_datatype::AFC eFreqDev;

    eFreqDev = m_pDemodulator->ATV_GetAFC_Distance();
    ATV_PLAYER_DBG("AFCWIN=%x\n", (int)eFreqDev);

    switch(eFreqDev)
    {
        case mapi_demodulator_datatype::E_AFC_MINUS_62p5KHz:
        case mapi_demodulator_datatype::E_AFC_MINUS_37p5KHz:
        case mapi_demodulator_datatype::E_AFC_MINUS_12p5KHz:
        case mapi_demodulator_datatype::E_AFC_PLUS_12p5KHz:
        case mapi_demodulator_datatype::E_AFC_PLUS_37p5KHz:
        case mapi_demodulator_datatype::E_AFC_PLUS_62p5KHz:
            return E_TUNING_STATUS_GOOD;

        case mapi_demodulator_datatype::E_AFC_MINUS_162p5KHz:
        case mapi_demodulator_datatype::E_AFC_MINUS_137p5KHz:
        case mapi_demodulator_datatype::E_AFC_MINUS_112p5KHz:
        case mapi_demodulator_datatype::E_AFC_MINUS_87p5KHz:
            return E_TUNING_STATUS_UNDER;

        case mapi_demodulator_datatype::E_AFC_PLUS_162p5KHz:
        case mapi_demodulator_datatype::E_AFC_PLUS_137p5KHz:
        case mapi_demodulator_datatype::E_AFC_PLUS_112p5KHz:
        case mapi_demodulator_datatype::E_AFC_PLUS_87p5KHz:
            return E_TUNING_STATUS_OVER;

        case mapi_demodulator_datatype::E_AFC_BELOW_MINUS_187p5KHz:
            return E_TUNING_STATUS_UNDER_MORE;

        case mapi_demodulator_datatype::E_AFC_ABOVE_PLUS_187p5KHz:
            return E_TUNING_STATUS_OVER_MORE;

        default:
        case mapi_demodulator_datatype::E_AFC_OUT_OF_AFCWIN:
            return E_TUNING_STATUS_OUT_OF_AFCWIN;
    }
}

/*
 ********************************************
 FUNCTION   : CheckSaveAftOffsetValue
 USAGE      : Check AFT offset need save or not
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
void MSrv_ATV_Player::CheckSaveAftOffsetValue(void)
{
    U8 u8CurrentChannelNumber;

    if(!IsAFTEnabled())
    {
        return;
    }

    u8CurrentChannelNumber = MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL);
    if(abs(m_u8AftOffset - MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_AFT_OFFSET, u8CurrentChannelNumber, 0, NULL)) > 1)
    {
        MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_AFT_OFFSET, u8CurrentChannelNumber, m_u8AftOffset, NULL);
    }
}

/*
 ********************************************
 FUNCTION   : _msAPI_Tuning_AutoFineTuning
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
void MSrv_ATV_Player::_msAPI_Tuning_AutoFineTuning(void)
{
    //    BOOL bOverRange = 0;
    /*
    m_u16IdleTimer++;
    if(m_u16IdleTimer < WAIT_N_ms(500))
    {
        return;
    }
    */

    U32 u32CurrTime = 0;
    u32CurrTime = (U32)mapi_time_utility::GetTime0();
    if( abs( long(u32CurrTime - m_u32IdleTimer)) < ATV_AFT_INTERVAL_TIME )
    {
        return;
    }
    m_u32IdleTimer = u32CurrTime;
    m_u16IdleTimer = WAIT_N_ms(10);

    /*
    if (m_u8AftOffset >= (AFT_OFFSET_0 + TUNER_PLL_PLUS_2MHz)
     || m_u8AftOffset <= (AFT_OFFSET_0 + TUNER_PLL_MINUS_2MHz))
    {
        //wStatus = vdChkStatusLoop(SRC_TYPE_IS_TV(SRC1)?SRC1:SRC2);
        //if( !(wStatus&MAPI_VD_HSYNC_LOCKED) )
        {
            bOverRange = 1;
            m_u8AftStep = 0xFE;
        }
    }
    */

    static BOOL bSetTune = FALSE;

    EN_CABLE_STATUS eCableState = E_CABLE_UNKNOW;
    m_pTuner->GetCableStatus(&eCableState);

    if ((IsAFTEnabled() == FALSE) || (eCableState == E_CABLE_REMOVED))
    {
        return;
    }

    TUNING_STATUS eAFCWIN = _GetTuningStatus();
    // add the judgement of VD, since the audio carrier of last channel is locked by VIF in automatic fine tune(AFT) process. (mantis 0629409)
    if ((eAFCWIN != E_TUNING_STATUS_OUT_OF_AFCWIN) && (mapi_interface::Get_mapi_vd()->IsSyncLocked() == TRUE) && ((eCableState == E_CABLE_UNKNOW) ||(eCableState == E_CABLE_INSERTED)))
    {
        switch(eAFCWIN)
        {
            case E_TUNING_STATUS_GOOD:
                //ATV_PLAYER_IFO("AFC GOOD \n");
                //m_u8AftStep = 0;
                if(bSetTune == TRUE)
                {
                    bSetTune = FALSE;
                    if(IS_AFT_SAVE_ENABLED == TRUE)
                        CheckSaveAftOffsetValue();
                }
                return;

            case E_TUNING_STATUS_UNDER:
                ATV_PLAYER_IFO("AFC INC \n");
                m_u8AftOffset += TUNER_PLL_PLUS_62p5KHz;
                break;
            case E_TUNING_STATUS_OVER:
                ATV_PLAYER_IFO("AFC DEC \n");
                m_u8AftOffset += TUNER_PLL_MINUS_62p5KHz;
                break;
            default:
            case E_TUNING_STATUS_UNDER_MORE:
                ATV_PLAYER_IFO("AFC BIG INC \n");
                m_u8AftOffset += TUNER_PLL_PLUS_0p25MHz;
                break;
            case E_TUNING_STATUS_OVER_MORE:
                ATV_PLAYER_IFO("AFC BIG DEC \n");
                m_u8AftOffset += TUNER_PLL_MINUS_0p25MHz;
                break;

        }

        if(m_u8AftOffset >= (AFT_OFFSET_0 + TUNER_PLL_PLUS_2MHz))
        {
            m_u8AftOffset = AFT_OFFSET_0 + TUNER_PLL_PLUS_2MHz;
        }
        else if(m_u8AftOffset <= (AFT_OFFSET_0 + TUNER_PLL_MINUS_2MHz))
        {
            m_u8AftOffset = AFT_OFFSET_0 + TUNER_PLL_MINUS_2MHz;
        }

    }
    else
    {
        m_u8AftStep ++;
        if(m_u8AftStep > 8)
        {
            m_u8AftStep = 0;
        }
        //printf("\nmsAPI_Tuning_AutoFineTuning[%d][%d]\r\n",m_u8AftStep,m_u8AftOffset);
        switch(m_u8AftStep)
        {
            case 0:
            case 1:
                break;
            case 2:
                m_u8AftOffset = AFT_OFFSET_0 + TUNER_PLL_PLUS_0p5MHz; //+500k
                break;
            case 3:
                m_u8AftOffset = AFT_OFFSET_0 + TUNER_PLL_MINUS_0p5MHz; //-500k
                break;
            case 4:
                m_u8AftOffset = AFT_OFFSET_0 + TUNER_PLL_MINUS_1MHz; //-1M
                break;
            case 5:
                m_u8AftOffset = AFT_OFFSET_0 + TUNER_PLL_PLUS_1MHz; //+1M
                break;
            case 6:
                m_u8AftOffset = AFT_OFFSET_0 + TUNER_PLL_PLUS_1p5MHz; //+1.5M
                break;
            case 7:
                m_u8AftOffset = AFT_OFFSET_0 + TUNER_PLL_MINUS_1p5MHz; //-1.5M
                break;
            case 8:
                m_u8AftOffset = AFT_OFFSET_0;// + TUNER_PLL_MINUS_2MHz; //-2M
                break;
        }
    }
    //printf("m_u8AftOffset = %d ,%d\r\n",m_u8AftOffset,m_u8AftOffset_pre);
    if(abs(m_u8AftOffset - m_u8AftOffset_saved) == 1)
        m_u8AftOffset = m_u8AftOffset_saved;   //for change chanel garbage
    if(m_u8AftOffset != m_u8AftOffset_pre)
    {
        m_u8AftOffset_pre = m_u8AftOffset;
        bSetTune = TRUE;
    }
    else
    {
        bSetTune = FALSE;
    }
    if(bSetTune == TRUE)
    {
        MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_INTERNAL_1_MUTEON_, m_CurrentSrcType);
        _SetTunerPLL(m_u16TunerPLL);
        usleep(50 * 1000);

        MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_INTERNAL_1_MUTEOFF_, m_CurrentSrcType);
    }
    //msAPI_Tuner_SetIF();
}
#if (ATSC_SYSTEM_ENABLE == 1)
/*
 ********************************************
 FUNCTION   : msAPI_ATVGetStartChannelNumber
 USAGE      : Get ATV Start Channel Number
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
U8 MSrv_ATV_Player::msAPI_ATVGetStartChannelNumber(void)
{
    MW_ATV_Scan_NTSC *pAtscScan;
    U8 StartChannel;
    pAtscScan = dynamic_cast<MW_ATV_Scan_NTSC *>(mScan);
    StartChannel = (U8)(pAtscScan->StartChannel()) ;
    return StartChannel;
}
/*
 ********************************************
 FUNCTION   : msAPI_ATVCheckIsManualScan
 USAGE      : Check Is ATV Manual Scan or not
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
BOOL MSrv_ATV_Player::msAPI_ATVCheckIsManualScan(void)
{
    MW_ATV_Scan_NTSC *pAtscScan;
    BOOL IsManualScan;
    pAtscScan = dynamic_cast<MW_ATV_Scan_NTSC *>(mScan);
    IsManualScan = (U8)(pAtscScan->IsManualScan()) ;
    return IsManualScan;
}
#endif
#if 0
/******************************************************************************/
///- This API is called by MApp_ATVProc_Handler to keep tuning work.
/// @param eState \b IN: AFT_EXT_STEP_PERIODIC - This enum is called by ATVProc_Handler(). Don't call any other place except ATVProc_Handler().
///                  IN: AFT_EXT_STEP_SEARCHALL - This enum will start auto-tuning from VHF low to UHF max.
///                  IN: AFT_EXT_STEP_SEARCHONETOUP - This enum will search up for next one available channel.
///                  IN: AFT_EXT_STEP_SEARCHONETODOWN - This enum will search up for next one available channel.
///                  IN: AFT_EXT_STEP_SEARCH_STOP - This enum will stop searching.
/******************************************************************************/
/*
 ********************************************
 FUNCTION   : msAPI_Tuner_GetTuningProcessPercent
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
U8 MSrv_ATV_Player::msAPI_Tuner_GetTuningProcessPercent(void)
{
    U8 u8Percent;
    U32 u32UhfMaxFreq;
    u8Percent = 0;

    mapi_tuner* pTuner;
    pTuner = mapi_interface::Get_mapi_pcb()->GetAtvTuner(0);
    ASSERT(pTuner);

    pTuner->ExtendCommand(
        mapi_tuner_datatype::E_TUNER_SUBCMD_GET_UHF_MAX_FREQ,
        0,
        0,
        &u32UhfMaxFreq);

    if(m_stAtvScanParam.u8ScanType == E_SCAN_MANUAL_TUNING && mScan->IsSearched())
    {
        return 100;
    }

    if(m_stAtvScanParam.u8ScanType == E_SCAN_MANUAL_TUNING)
    {
        if(m_stAtvScanParam.aATVMannualTuneMode == E_MANUAL_TUNE_MODE_SEARCH_ONE_TO_UP)
        {
            if((m_stAtvScanParam.u32StartFrequency / 1000) <= 48250 / 1000)
            {
                if((mScan->GetCurrentFreq() / 1000) == (u32UhfMaxFreq / 1000))
                {
                    return 100;
                }
            }
            else
            {
                if((mScan->GetCurrentFreq() / 1000) == (m_stAtvScanParam.u32StartFrequency / 1000 - 1))
                {
                    return 100;
                }
            }
        }
        else
        {
            if((m_stAtvScanParam.u32StartFrequency / 1000) >= (u32UhfMaxFreq / 1000))
            {
                if((mScan->GetCurrentFreq() / 1000) <= 48250 / 1000)
                {
                    return 100;
                }
            }
            else
            {
                if((mScan->GetCurrentFreq() / 1000) == (m_stAtvScanParam.u32StartFrequency / 1000 + 1))
                {
                    return 100;
                }
            }
        }
    }

#if (ATSC_SYSTEM_ENABLE == 1)
    MW_ATV_Scan_NTSC *pAtscScan;

    pAtscScan = dynamic_cast<MW_ATV_Scan_NTSC *>(mScan);
    u8Percent = (U8)(((pAtscScan->NowChannel() - pAtscScan->GetMinChannelNO()) * 100) / (pAtscScan->GetMaxChannelNO() - pAtscScan->GetMinChannelNO())) ;
    printf("u8Percent=%d \tMin=%d \t Max=%d\n", u8Percent, pAtscScan->GetMinChannelNO(), pAtscScan->GetMaxChannelNO());

#elif ( ISDB_SYSTEM_ENABLE == 1 )
    MW_ATV_Scan_Brazil*pAtscScan;

    pAtscScan = dynamic_cast<MW_ATV_Scan_Brazil *>(mScan);
    u8Percent = (U8)(((pAtscScan->NowChannel() - pAtscScan->GetMinChannelNO()) * 100) / (pAtscScan->GetMaxChannelNO() - pAtscScan->GetMinChannelNO())) ;
    printf("u8Percent=%d \tMin=%d \t Max=%d\n", u8Percent, pAtscScan->GetMinChannelNO(), pAtscScan->GetMaxChannelNO());

#else

    u8Percent = (U8)((((mScan->GetCurrentFreq() - m_stAtvScanParam.u32StartFrequency) + 1) * 100) / ((m_stAtvScanParam.u32EndFrequency + 4000) - m_stAtvScanParam.u32StartFrequency)) ;

    if((m_bIsLSearch == TRUE) && (m_TV_SCAN_PAL_SECAM_ONCE == FALSE))
    {
        if(mapi_interface::Get_mapi_audio()->SIF_GetAudioStandard() == E_AUDIOSTANDARD_L_)
        {
            u8Percent = u8Percent / 2;
        }
        else
        {
            u8Percent = 50 + (u8Percent / 2);
        }
    }
#endif
    return u8Percent;
}
#endif


//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//+++++++++++++++++++++++++++++ msAPI_FreqTableATV.c ++++++++++++++++++++++++
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

/*
 ********************************************
 FUNCTION   :CFTIsValidMediumAndChannel
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
/******************************************************************************/
/// -This function will check is medium and channel valid
/// @param eMedium \b IN: Medium
/// - @see MEDIUM
/// @param cChannelNumber \b IN: Channel Number
/// @return BOOLEAN:
/// - TRUE: Default is valid
/// - FALSE: NO
/******************************************************************************/
BOOLEAN MSrv_ATV_Player::CFTIsValidMediumAndChannel(MSrv_ATV_Database::MEDIUM eMedium, U8 cChannelNumber)
{
#if (ATSC_SYSTEM_ENABLE == 1) // NTSC
    return TRUE;
#else
    U8 cMinChannel, cMaxChannel;

    msAPI_CFT_GetMinMaxChannel(eMedium, &cMinChannel, &cMaxChannel);

    if((cChannelNumber < cMinChannel) || (cMaxChannel < cChannelNumber))
    {
        return FALSE;
    }

    return TRUE;
#endif

}
/*
 ********************************************
 FUNCTION   : CFTGetMedium
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
/******************************************************************************/
/// -This function will Get Medium type
/// @param wChannelPLLData \b IN: Channel PLL Data
/// @return MEDIUM: Medium type
/// - @see MEDIUM
/******************************************************************************/
MSrv_ATV_Database::MEDIUM MSrv_ATV_Player::CFTGetMedium(U16 wChannelPLLData)
{
#if (ATSC_SYSTEM_ENABLE == 1) // NTSC
    return MSrv_ATV_Database::MEDIUM_AIR;
#else
    U8 i;
    U8 cTableSize;
    ST_FREQ_CHANNEL * pFreqChannel;
    U8 cLeadChannelIndex;
    U16 wCompressedFrequency;
    MSrv_ATV_Database::MEDIUM eMedium;

    wCompressedFrequency = ConvertPLLtoCompressedFrequency(wChannelPLLData);
    cTableSize = GetFreqChannelTable(&pFreqChannel);

    cLeadChannelIndex = 0;

    for(i = 0; i < cTableSize; i++)
    {
        if(wCompressedFrequency >= pFreqChannel[i].wFrequency)
        {
            cLeadChannelIndex = i;
            break;
        }
    }

    if(i == cTableSize)
    {
        printf("Hi. correct me.");
        return MSrv_ATV_Database::MEDIUM_AIR;
    }

    if((pFreqChannel[cLeadChannelIndex].wChannel & MEDIUM_MASK) == _CABLE)
    {
        eMedium = MSrv_ATV_Database::MEDIUM_CABLE;
    }
    else
    {
        eMedium = MSrv_ATV_Database::MEDIUM_AIR;
    }

    return eMedium;
#endif
}


/*
 ********************************************
 FUNCTION   :msAPI_CFT_GetMinMaxChannel
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
/******************************************************************************/
/// -This function will Get Min and Max Channel
/// @param eMedium \b IN: Medium type
/// -@see MEDIUM
/// @param pcMin \b IN: pointer to Min
/// @param pcMax \b IN: pointer to Max
/******************************************************************************/
void MSrv_ATV_Player::msAPI_CFT_GetMinMaxChannel(MSrv_ATV_Database::MEDIUM eMedium, U8 * pcMin, U8 * pcMax)
{
    AUDIOSTANDARD_TYPE_ eAudioStandard;
    U8 cMin = 0;
    U8 cMax = 0;

    eAudioStandard = mapi_interface::Get_mapi_audio()->SIF_GetAudioStandard();
    if(eMedium == MSrv_ATV_Database::MEDIUM_CABLE)
    {
        switch(mapi_interface::Get_mapi_audio()->SIF_ConvertToBasicAudioStandard(eAudioStandard))
        {
            case E_AUDIOSTANDARD_BG_:
                cMin = 1;
                cMax = 47;
                break;
            case E_AUDIOSTANDARD_I_:
                cMin = 1;
                cMax = 47;
                break;
            case E_AUDIOSTANDARD_DK_:
                cMin = 1;
                cMax = 47;
                break;
            case E_AUDIOSTANDARD_L_:
                cMin = 1;
                cMax = 47;
                break;
            default:
                break;
        }
    }
    else
    {
        switch(mapi_interface::Get_mapi_audio()->SIF_ConvertToBasicAudioStandard(eAudioStandard))
        {
            case E_AUDIOSTANDARD_BG_:
                cMin = 1;
                cMax = 69;
                break;
            case E_AUDIOSTANDARD_I_:
                cMin = 1;
                cMax = 69;
                break;
            case E_AUDIOSTANDARD_DK_:
                cMin = 1;
                cMax = 69;
                break;
            case E_AUDIOSTANDARD_L_:
                cMin = 1;
                cMax = 69;
                break;
            default:
                break;
        }
    }

    if(pcMin != NULL)
    {
        *pcMin = cMin;
    }

    if(pcMax != NULL)
    {
        *pcMax = cMax;
    }
}


/*
 ********************************************
 FUNCTION   :msAPI_CFT_ConvertPLLtoIntegerOfFrequency
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
/******************************************************************************/
/// -This function will Convert PLL to Integer Of Frequency
/// @param wPLL \b IN: PLL
/// @return U16: Integer Of Frequency
/******************************************************************************/
U16 MSrv_ATV_Player::msAPI_CFT_ConvertPLLtoIntegerOfFrequency(U16 wPLL)
{
    U16 wIntegerOfFreq;
    EN_FREQ_STEP eFreqStep = E_FREQ_STEP_INVALD;

    m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_FREQ_STEP, 0, 0, &eFreqStep);

    switch(eFreqStep)
    {
        case E_FREQ_STEP_31_25KHz:
            wIntegerOfFreq = (MAPI_U16)((((MAPI_U16)wPLL * 3125) / 100) / 1000);
            break;
        case E_FREQ_STEP_50KHz:
            wIntegerOfFreq = (MAPI_U16)(((MAPI_U16)wPLL * 50) / 1000);
            break;
        case E_FREQ_STEP_62_5KHz:
            wIntegerOfFreq = (MAPI_U16)((((MAPI_U16)wPLL * 625) / 10) / 1000);
            break;
        default:
            ASSERT(0);
            break;
    }

    return wIntegerOfFreq;
}
/*
 ********************************************
 FUNCTION   :ConvertPLLtoIntegerOfFrequencyKHZ
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
/******************************************************************************/
/// -This function will Convert PLL to Integer Of Frequency by KMHZ
/// @param wPLL \b IN: PLL
/// @return U16: Integer Of Frequency
/******************************************************************************/

U32 MSrv_ATV_Player::ConvertPLLtoFrequencyKHZ(U16 wPLL)
{
    U32 u32Freq;
    EN_FREQ_STEP enFreqStep = E_FREQ_STEP_INVALD;
    U32 u32StepSize;

    mapi_tuner* pTuner;
    pTuner = mapi_interface::Get_mapi_pcb()->GetAtvTuner(0);
    ASSERT(pTuner);

    pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_FREQ_STEP, 0, 0, &enFreqStep);

    if(enFreqStep == E_FREQ_STEP_62_5KHz)
    {
        u32StepSize = 6250;
    }
    else if(enFreqStep == E_FREQ_STEP_50KHz)
    {
        u32StepSize = 5000;
    }
    else
    {
        u32StepSize = 3125;
    }

    u32Freq = (((MAPI_U32)wPLL * u32StepSize) / 100);
    return u32Freq;

}
/*
 ********************************************
 FUNCTION   :msAPI_CFT_ConvertPLLtoFractionOfFrequency
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/

/******************************************************************************/
/// -This function will Convert PLL to Fraction Of Frequency
/// @param wPLL \b IN: PLL
/// @return U16: Fraction Of Frequency
/******************************************************************************/
U16 MSrv_ATV_Player::msAPI_CFT_ConvertPLLtoFractionOfFrequency(U16 wPLL)
{
    U16 wFractionOfFreq;
    EN_FREQ_STEP eFreqStep = E_FREQ_STEP_INVALD;

    m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_FREQ_STEP, 0, 0, &eFreqStep);

    switch(eFreqStep)
    {
        case E_FREQ_STEP_31_25KHz:
            wFractionOfFreq = (MAPI_U16)((((MAPI_U16)wPLL * 3125) / 100) % 1000);
            break;
        case E_FREQ_STEP_50KHz:
            wFractionOfFreq = (MAPI_U16)(((MAPI_U16)wPLL * 50) % 1000);
            break;
        case E_FREQ_STEP_62_5KHz:
            wFractionOfFreq = (MAPI_U16)((((MAPI_U16)wPLL * 625) / 10) % 1000);
            break;
        default:
            ASSERT(0);
            break;
    }

    return wFractionOfFreq;
}
/*
 ********************************************
 FUNCTION   :msAPI_CFT_ConvertFrequncyToPLL
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
/******************************************************************************/
/// -This function will Convert Frequncy To PLL
/// @param u16FreqKHz \b IN: Frequency (KHz)
/// @return U16: Tuner PLL
/******************************************************************************/
U16 MSrv_ATV_Player::ConvertFrequncyHzToPLL(U32 u32FreqHz)
{
    EN_FREQ_STEP eFreqStep = E_FREQ_STEP_INVALD;
    m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_FREQ_STEP, 0, 0, &eFreqStep);

    switch(eFreqStep)
    {
        case E_FREQ_STEP_31_25KHz:
            return (u32FreqHz / 31250);
            break;
        case E_FREQ_STEP_50KHz:
            return (u32FreqHz / 50000);
            break;
        case E_FREQ_STEP_62_5KHz:
            return (u32FreqHz / 62500);
            break;
        default:
            ASSERT(0);
            return 0;
            break;
    }
}
/*
 ********************************************
 FUNCTION   :GetChannelFreqTable
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/

//*****************************************************************************
// Start of private implementation
//****************************************************************************

/*
 ********************************************
 FUNCTION   :GetFreqChannelTable
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
U8 MSrv_ATV_Player::GetFreqChannelTable(ST_FREQ_CHANNEL ** ppFreqChannel)
{
    AUDIOSTANDARD_TYPE_ eAudioStandard;
    U8 cTableSize;

    eAudioStandard = mapi_interface::Get_mapi_audio()->SIF_GetAudioStandard();

    switch(mapi_interface::Get_mapi_audio()->SIF_ConvertToBasicAudioStandard(eAudioStandard))
    {
        case E_AUDIOSTANDARD_BG_:
            *ppFreqChannel = m_FreqChannel_BG;
            cTableSize = sizeof(m_FreqChannel_BG) / sizeof(ST_FREQ_CHANNEL);
            break;

        case E_AUDIOSTANDARD_I_:
            *ppFreqChannel = m_FreqChannel_I;
            cTableSize = sizeof(m_FreqChannel_I) / sizeof(ST_FREQ_CHANNEL);
            break;

        case E_AUDIOSTANDARD_DK_:
            *ppFreqChannel = m_FreqChannel_DK;
            cTableSize = sizeof(m_FreqChannel_DK) / sizeof(ST_FREQ_CHANNEL);
            break;

        case E_AUDIOSTANDARD_L_:
            *ppFreqChannel = m_FreqChannel_L;
            cTableSize = sizeof(m_FreqChannel_L) / sizeof(ST_FREQ_CHANNEL);
            break;

        default:
            *ppFreqChannel = m_FreqChannel_BG;
            cTableSize = sizeof(m_FreqChannel_BG) / sizeof(ST_FREQ_CHANNEL);
            break;
    }

    return cTableSize;
}

/*
 ********************************************
 FUNCTION   :ConvertPLLtoCompressedFrequency
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
U16 MSrv_ATV_Player::ConvertPLLtoCompressedFrequency(U16 wPLLData)
{
    EN_FREQ_STEP eFreqStep = E_FREQ_STEP_INVALD;

    m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_FREQ_STEP, 0, 0, &eFreqStep);

    switch(eFreqStep)
    {
        case E_FREQ_STEP_31_25KHz:
            return (wPLLData / 8);
            break;
        case E_FREQ_STEP_50KHz:
            return (wPLLData / 5);
            break;
        case E_FREQ_STEP_62_5KHz:
            return (wPLLData / 4);
            break;
        default:
            ASSERT(0);
            break;
    }
}

//------------------------------------------------------------------------------
/// -This function will get Medium and Channel Number
/// @param cProgramNumber \b IN: Program number
/// @param *peMedium \b IN: pointer to Medium for return
/// -@see MEDIUM
/// @param *pcChannelNumber \b IN: pointer to Channel number for return
//------------------------------------------------------------------------------
void MSrv_ATV_Player::ATVGetMediumAndChannelNumber(U8 u8ProgramNumber, MSrv_ATV_Database::MEDIUM * peMedium, U8 * pcChannelNumber)
{
    ST_ATV_MISC Misc;
    U16 wPLL;

    if(u8ProgramNumber > MSrv_Control::GetMSrvAtvDatabase()->ATVGetChannelMax())
    {
        if(peMedium != NULL)
        {
            *peMedium = MSrv_ATV_Database::DEFAULT_MEDIUM;
        }

        if(pcChannelNumber != NULL)
        {
            *pcChannelNumber = DEFAULT_CHANNELNUMBER;
        }

        return;
    }

    if(TRUE == MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_MISC, u8ProgramNumber, 0, (U8 *)&Misc))
        //if ( TRUE == MSrv_Control::GetMSrvAtvDatabase()->_GetPRTable(u8ProgramNumber, (U8 *)&Misc, PRDATA_MISC_PARAM) )
    {
        if(TRUE == CFTIsValidMediumAndChannel((MSrv_ATV_Database::MEDIUM)Misc.eMedium, Misc.u8ChannelNumber))
        {
            if(peMedium != NULL)
            {
                *peMedium = (MSrv_ATV_Database::MEDIUM)Misc.eMedium;
            }

            if(pcChannelNumber != NULL)
            {
                *pcChannelNumber = Misc.u8ChannelNumber;
            }

            return;
        }
    }

    //wPLL = ATVGetProgramPLLData(u8ProgramNumber); //must;;
    wPLL =  MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_PROGRAM_PLL_DATA, u8ProgramNumber, 0, NULL);

    if(peMedium != NULL)
    {
        *peMedium = (MSrv_ATV_Database::MEDIUM)MW_ATV_Util::GetInstance()->CFTGetMedium(wPLL);
    }

    if(pcChannelNumber != NULL)
    {
        *pcChannelNumber = MW_ATV_Util::GetInstance()->CFTGetChannelNumber(wPLL);
    }
}

//------------------------------------------------------------------------------
/// -This function will get Medium and Channel Number
/// @param cProgramNumber \b IN: Program number
/// @param eMedium \b IN: Medium type
/// -@see MEDIUM
/// @param cChannelNumber \b IN: Channel number
//------------------------------------------------------------------------------
void MSrv_ATV_Player::ATVSetMediumAndChannelNumber(U8 u8ProgramNumber, MSrv_ATV_Database::MEDIUM eMedium, U8 cChannelNumber)
{
    ST_ATV_MISC Misc;
    U16 wPLL;

    if(u8ProgramNumber > MSrv_Control::GetMSrvAtvDatabase()->ATVGetChannelMax())
    {
        return;
    }

    if(TRUE == MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_MISC , u8ProgramNumber , 0 , (U8 *)&Misc))
        //if ( TRUE == MSrv_Control::GetMSrvAtvDatabase()->_GetPRTable(u8ProgramNumber, (U8 *)&Misc, PRDATA_MISC_PARAM) )
    {
        if(TRUE == CFTIsValidMediumAndChannel(eMedium, cChannelNumber))
        {
            Misc.eMedium = eMedium;
            Misc.u8ChannelNumber = cChannelNumber;
        }
        else
        {
            //wPLL = ATVGetProgramPLLData(u8ProgramNumber); //must;;
            wPLL =  MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_PROGRAM_PLL_DATA, u8ProgramNumber, 0, NULL);
            Misc.eMedium = (MSrv_ATV_Database::MEDIUM)MW_ATV_Util::GetInstance()->CFTGetMedium(wPLL); //@FIXME
            Misc.u8ChannelNumber = MW_ATV_Util::GetInstance()->CFTGetChannelNumber(wPLL);
        }

        if(TRUE != MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_MISC , u8ProgramNumber , 0 , (U8 *)&Misc))
        {
            ASSERT(0);
        }
    }
}

//------------------------------------------------------------------------------
/// -This function will save program
/// @param cCurrentProgramNumber \b IN: current program number
//------------------------------------------------------------------------------
void MSrv_ATV_Player::ATVSaveProgram(U8 u8CurrentProgramNumber)
{
    U8 sStationName[MAX_STATION_NAME] = "\0";
#if (TTX_ENABLE == 1)
    U8 u8SortingPriority;
#endif
    BOOL bCNIStatus = FALSE;
    MSrv_ATV_Database::MEDIUM      eMedium;
    if(MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL) != u8CurrentProgramNumber)
    {
        //ATVSetCurrentProgramNumber(u8CurrentProgramNumber); //must;;
        MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER , u8CurrentProgramNumber , 0 , NULL);
    }

    if((FALSE == IsAFTEnabled())
            && (TRUE == TunerIsCurrentChannelAndSavedChannelSame()))
    {
        MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(NEED_AFT , u8CurrentProgramNumber, FALSE, NULL);
    }
    else
    {
        MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(NEED_AFT , u8CurrentProgramNumber, TRUE, NULL);
        m_bIsAFTNeeded = TRUE;
    }

    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SKIP_PROGRAM , u8CurrentProgramNumber, FALSE, NULL);

    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(HIDE_PROGRAM , u8CurrentProgramNumber, FALSE, NULL);

    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_DIRECT_TUNED , u8CurrentProgramNumber, TRUE, NULL);

    MSrv_Control::GetMSrvAtvDatabase()->SetFavoriteProgram(SET_FAVORITE_PROGRAM, u8CurrentProgramNumber, 0, NULL);

    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(LOCK_PROGRAM , u8CurrentProgramNumber, FALSE, NULL);

    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(ENABLE_REALTIME_AUDIO_DETECTION , u8CurrentProgramNumber, 1, NULL);


    //TunerGetCurrentStationName(sStationName);
#if (TTX_ENABLE == 1)
    #if (PIP_ENABLE == 1)
     if((FALSE == MSrv_Control::GetInstance()->IsPipModeEnable()) || (m_PipXCWin == MAPI_MAIN_WINDOW))
    #endif
    //if(MW_TTX::GetInstance()->DoesHaveTTXSignal())
    {
        if(mapi_interface::Get_mapi_cni()->GetStationName(sStationName, MAX_STATION_NAME, &u8SortingPriority))
        {
            bCNIStatus = TRUE;
        }
    }
#endif
    U8 m_u8ChannelNumber = 0;
    if(bCNIStatus == FALSE)
    {
        eMedium = (MSrv_ATV_Database::MEDIUM)MW_ATV_Util::GetInstance()->CFTGetMedium(TunerGetCurrentChannelPLL());
        m_u8ChannelNumber = MW_ATV_Util::GetInstance()->CFTGetChannelNumber(TunerGetCurrentChannelPLL());
        mScan->TunerConvertMediumAndChannelNumberToString((ATV_UTIL_MEDIUM)eMedium, m_u8ChannelNumber, sStationName); //@FIXME
    }


    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_STATION_NAME , u8CurrentProgramNumber , 0 , sStationName);

    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_AUDIO_STANDARD , u8CurrentProgramNumber, mapi_interface::Get_mapi_audio()->SIF_GetAudioStandard(), NULL);

    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_VIDEO_STANDARD_OF_PROGRAM , u8CurrentProgramNumber, GetVideoStandard(), NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_PROGRAM_PLL_DATA , u8CurrentProgramNumber, TunerGetCurrentChannelPLL(), NULL);

    {
        //ATVSetMediumAndChannelNumber(u8CurrentProgramNumber, pATV_Player->TunerGetMedium(), pATV_Player->TunerGetChannelNumber()); //must;;
        //U8 u8TempChannelNumber =  TunerGetChannelNumber();
        U8 u8TempChannelNumber = MW_ATV_Util::GetInstance()->CFTGetChannelNumber(TunerGetCurrentChannelPLL());
        ATVSetMediumAndChannelNumber(u8CurrentProgramNumber, TunerGetMedium(), u8TempChannelNumber);
    }
}

/*//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//!!!!!!!!!!!!!!!!! We need these functions ????? !!!!!!!!!!!!!!!!!!!!!!!!!!!!
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
*/

BOOL MSrv_ATV_Player::IsSignalStable(void)
{
    return mapi_interface::Get_mapi_vd()->IsSignalStable() ;
}

#if (ATSC_SYSTEM_ENABLE == 1 || ISDB_SYSTEM_ENABLE == 1)
BOOL MSrv_ATV_Player::SetNTSCAntenna(MSrv_ATV_Database::MEDIUM eAntenna)
{
    m_Antenna = eAntenna;
    ATV_PLAYER_IFO("Set my Antenna %d\n", eAntenna);
    MSrv_Control::GetMSrvAtvDatabase()->SetNTSCAntenna(eAntenna);
    return TRUE;
}
BOOL MSrv_ATV_Player::SetAntennaType(MSrv_ATV_Database::MEDIUM eAntenna)
{
    m_Antenna = eAntenna;
    MSrv_Control::GetMSrvAtvDatabase()->SetAntennaType(eAntenna);
    return TRUE;
}

MSrv_ATV_Database::MEDIUM MSrv_ATV_Player::GetNTSCAntenna()
{
    return m_Antenna;
}
#endif

void MSrv_ATV_Player::InitATVVIF(void)
{
    m_pDemodulator->ATV_VIF_Init();
    return;
}
void MSrv_ATV_Player::UpdateVIFSetting(void)
{
    m_pDemodulator->ATV_VIF_Init();
    return;
}

void MSrv_ATV_Player::SetAspectRatio(const mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO &stVideoARCInfo)
{
    if((m_pcVideo == NULL) || (m_pVptr == NULL))
    {
        printf("!!! [ATV] Video init not finish yet !!!\n");
        return;
    }

    mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO stCurrentARCInfo;
    GetAspectRatio(stCurrentARCInfo);
    mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stCapWin;
    mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stCropWin;
    mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stDispWin;
    memset(&stCapWin, 0, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE));
    memset(&stCropWin, 0, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE));
    memset(&stDispWin, 0, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE));

#if (SEAMLESS_ZOOMING_ENABLE == 1)
    if (IsShowFreezeImageByDFB() == FALSE)
    {
        if(m_pcVideo->IsFreezeImage() == FALSE)
        {
            m_pcVideo->FreezeImage(TRUE);
        }
        ShowFreezeImageByDFB(TRUE);
        m_pcVideo->FreezeImage(FALSE);
    }
#endif

    MSrv_Control::GetInstance()->SetVideoMute(TRUE,0 , m_PipXCWin);
    MSrv_Control::GetMSrvPicture()->Off();
    SetOverscanFromDB(m_pVptr, TRUE);

#if (MWE_ENABLE == 1)
    if(MSrv_MWE::GetInstance()->GetMWEStatus() != MSrv_MWE::E_EN_MS_MWE_OFF)
    {
        MSrv_MWE::GetInstance()->SetMWEStatus(MSrv_MWE::E_EN_MS_MWE_OFF);
    }
#endif

#if (PIP_ENABLE == 1)
    EN_PIP_MODES enPipMode = MSrv_Control::GetInstance()->GetPipMode();
    if(  (enPipMode == E_PIP_MODE_OFF)
       ||(enPipMode==E_PIP_MODE_TRAVELING && m_PipXCWin==MAPI_SUB_WINDOW))
    {
        m_pcVideo->SetWindow(NULL, NULL, &stVideoARCInfo);
    }
    else if(enPipMode == E_PIP_MODE_PIP)
    {
        mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stWinInfo;
        if(m_PipXCWin == MAPI_SUB_WINDOW)
        {
            MSrv_Control::GetInstance()->GetPipSubwindow(&stWinInfo);
            m_pcVideo->SetWindow(NULL, &stWinInfo, &stVideoARCInfo);
        }
        else
        {
            m_pcVideo->SetWindow(NULL, NULL, &stVideoARCInfo);
        }
    }
    else
    {
        mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stWinInfo;
        if(m_PipXCWin == MAPI_SUB_WINDOW)
            MSrv_Control::GetInstance()->GetPipSubwindow(&stWinInfo);
        else
            MSrv_Control::GetInstance()->GetMainwindow(&stWinInfo);

        m_pcVideo->SetWindow(NULL, &stWinInfo, &stVideoARCInfo);
    }
#else
    m_pcVideo->SetWindow(NULL, NULL, &stVideoARCInfo);
#endif

#if (PIP_ENABLE == 1)
    if(m_PipXCWin == MAPI_MAIN_WINDOW)
    {
        MSrv_Control::GetMSrvPicture()->On();
    }
    else
    {
        MSrv_Control::GetMSrvVideo()->SelectWindow(MAPI_SUB_WINDOW);
        MSrv_Control::GetMSrvPicture()->On();
        MSrv_Control::GetMSrvVideo()->SelectWindow(MAPI_MAIN_WINDOW);
    }
#else
    MSrv_Control::GetMSrvPicture()->On();
#endif

#if (ENABLE_ATV_NOSINGAL_BLACKSCREEN==1)
    if(TRUE == IsSignalStable())
#endif
    {
            // Do not unmute if current program is locked (mantsi 0678752)
        if(!(m_bCurrentProgramBlock==TRUE &&
            MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(IS_PROGRAM_LOCKED, (U16)MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL), 0, NULL)))
        {
#if(MSTAR_TVOS==1)
            MSrv_Control::GetInstance()->SetVideoMute(FALSE, 100, m_PipXCWin);
#else
            MSrv_Control::GetInstance()->SetVideoMute(FALSE, 0, m_PipXCWin);
#endif
        }

#if (SEAMLESS_ZOOMING_ENABLE == 1)
        if (IsShowFreezeImageByDFB() == TRUE)
        {
            ShowFreezeImageByDFB(FALSE);
        }
#endif

    }

    memcpy(&stReserveVideoARCInfo, &stVideoARCInfo, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO));
}

void MSrv_ATV_Player::GetAspectRatio(mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO &stVideoARCInfo)
{
    memcpy(&stVideoARCInfo, &stReserveVideoARCInfo, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO));
}

void MSrv_ATV_Player::FinetuneOverscan(const mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO &stVideoARCInfo)
{
    if(m_pcVideo == NULL)
    {
        return;
    }

    SetOverscanFromDB(m_pVptr, FALSE);
#if (PIP_ENABLE == 1)
    EN_PIP_MODES enPipMode = MSrv_Control::GetInstance()->GetPipMode();
    if(  (enPipMode == E_PIP_MODE_OFF)
       ||(enPipMode==E_PIP_MODE_TRAVELING && m_PipXCWin==MAPI_SUB_WINDOW))
    {
        m_pcVideo->SetWindow(NULL, NULL, &stVideoARCInfo);
    }
    else if(enPipMode == E_PIP_MODE_PIP)
    {
        mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stWinInfo;
        if(m_PipXCWin == MAPI_SUB_WINDOW)
        {
            MSrv_Control::GetInstance()->GetPipSubwindow(&stWinInfo);
            m_pcVideo->SetWindow(NULL, &stWinInfo, &stVideoARCInfo);
        }
        else
        {
            m_pcVideo->SetWindow(NULL, NULL, &stVideoARCInfo);
        }
    }
    else
    {
        mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stWinInfo;
        if(m_PipXCWin == MAPI_SUB_WINDOW)
            MSrv_Control::GetInstance()->GetPipSubwindow(&stWinInfo);
        else
            MSrv_Control::GetInstance()->GetMainwindow(&stWinInfo);

        m_pcVideo->SetWindow(NULL, &stWinInfo, &stVideoARCInfo);
    }
#else
    m_pcVideo->SetWindow(NULL, NULL, &stVideoARCInfo);
#endif
}

void MSrv_ATV_Player::SetOverscanFromDB(ST_MAPI_VIDEO_WINDOW_INFO **vptr, BOOL bSetAllDataFromDB)
{
#if  ENABLE_OVERSCAN_FOR_ALL_SOURCE
    MS_USER_SYSTEM_SETTING stGetSystemSetting;
    (MSrv_Control::GetMSrvSystemDatabase())->GetUserSystemSetting(&stGetSystemSetting);
    MAPI_BOOL bOverScan_flag = stGetSystemSetting.bOverScan;
#else
    MAPI_BOOL bOverScan_flag = MAPI_TRUE;
#endif

    MAPI_INPUT_SOURCE_TYPE enCurrentInputType = MAPI_INPUT_SOURCE_ATV;//Dawn :user overscan

    int beginI, endI, beginJ, endJ;

    if(bSetAllDataFromDB == TRUE)
    {
        beginI = 0;
        endI = SIG_NUMS;
        beginJ = 0;
        endJ = mapi_video_datatype::E_AR_MAX;
    }
    else
    {
        beginI = GetVideoSignalType();

        mapi_video_datatype::MAPI_VIDEO_ARC_Type eVideoARCType;
        MSrv_Control::GetMSrvSystemDatabase()->GetVideoArc(&eVideoARCType, &enCurrentInputType);
        beginJ = eVideoARCType;

        if((beginI < 0) || (beginJ < 0))
        {
            ASSERT(0);
        }

        endI = beginI + 1;
        endJ = beginJ + 1;

        if ((endI > SIG_NUMS) || (endJ > mapi_video_datatype::E_AR_MAX))
        {
            ASSERT(0);
        }
    }

    for(int i = beginI ; i < endI ; i++)
    {
        for(int j = beginJ; j < endJ ; j++)
        {
            MSrv_Control::GetMSrvSystemDatabase()->GetOverscanSetting( &vptr[i][j], (EN_VD_SIGNALTYPE)i, (mapi_video_datatype::MAPI_VIDEO_ARC_Type)j, m_CurrentSrcType);
            MSrv_Control::GetMSrvSystemDatabase()->GetVideoOverscanSetting(&vptr[i][j], &enCurrentInputType);    //Dawn :user overscan  , associated with GetOverscanSetting

            if (bOverScan_flag == MAPI_FALSE)
            {
                vptr[i][j].u8HCrop_Left = 0;
                vptr[i][j].u8HCrop_Right= 0;
                vptr[i][j].u8VCrop_Up = 0;
                vptr[i][j].u8VCrop_Down = 0;
            }
        }
    }

    m_pcVideo->SetOverScanInfo((const ST_MAPI_VIDEO_WINDOW_INFO**)vptr);
}

#if ((ATSC_CC_ENABLE == 1)||(NTSC_CC_ENABLE == 1)||(ISDB_CC_ENABLE == 1))
BOOL MSrv_ATV_Player::IsCCExist()
{
    return MW_CC::GetInstance()->CheckCCExist(E_608_CC);
}
#endif

#if (ISDB_SYSTEM_ENABLE == 1)
//------------------------------------------------------------------------------
/// -User changes MTS by himslef for Brazil
/// @return \none
//------------------------------------------------------------------------------
void MSrv_ATV_Player::DefaultMTSSelection_Brazil(void)
{
    AUDIOMODE_TYPE_ eSavedMTSMode, eDetectedSoundMode, eCurrentSoundMode;
    eDetectedSoundMode=(AUDIOMODE_TYPE_)MSrv_Control::GetMSrvSSSound()->GetSoundMode();
    if(eDetectedSoundMode == E_AUDIOMODE_INVALID_ )
    {
        return;
    }

    MSrv_Control::GetMSrvAtvDatabase()->CommondCmd(GET_AUDIO_MODE, 0, 0, &eSavedMTSMode);
    eCurrentSoundMode=(AUDIOMODE_TYPE_)MSrv_Control::GetMSrvSSSound()->GetMtsMode();

    if(eCurrentSoundMode == eDetectedSoundMode)
    {
        return;
    }

    if( ((eCurrentSoundMode == E_AUDIOMODE_DUAL_A_) || (eCurrentSoundMode == E_AUDIOMODE_DUAL_B_) || (eCurrentSoundMode == E_AUDIOMODE_DUAL_AB_)) &&
        ((eDetectedSoundMode == E_AUDIOMODE_DUAL_A_) || (eDetectedSoundMode == E_AUDIOMODE_DUAL_B_) || (eDetectedSoundMode == E_AUDIOMODE_DUAL_AB_)) )
    {
        return;
    }

    {
        switch (eDetectedSoundMode)
        {
            case E_AUDIOMODE_MONO_SAP_:
                if ((eSavedMTSMode == E_AUDIOMODE_FORCED_MONO_)
                    ||(eSavedMTSMode == E_AUDIOMODE_MONO_)
                    ||(eSavedMTSMode == E_AUDIOMODE_MONO_SAP_))
                    eDetectedSoundMode = eSavedMTSMode;
                else
                {
                    printf("\r\n Mono Sap to Mono\n");
                    eDetectedSoundMode=E_AUDIOMODE_FORCED_MONO_;
                }
            break;

            case E_AUDIOMODE_STEREO_SAP_:
                if ((eSavedMTSMode == E_AUDIOMODE_FORCED_MONO_)
                    ||(eSavedMTSMode == E_AUDIOMODE_MONO_)
                    ||(eSavedMTSMode == E_AUDIOMODE_G_STEREO_)
                    ||(eSavedMTSMode == E_AUDIOMODE_K_STEREO_)
                    ||(eSavedMTSMode == E_AUDIOMODE_STEREO_SAP_))
                    eDetectedSoundMode = eSavedMTSMode;
                else
                {
                    printf("\r\n Stereo Sap  to Stereo\n");
                    eDetectedSoundMode=E_AUDIOMODE_K_STEREO_;
                }
            break;

            case E_AUDIOMODE_MONO_:
                if ((eSavedMTSMode == E_AUDIOMODE_FORCED_MONO_)
                    ||(eSavedMTSMode == E_AUDIOMODE_MONO_))
                    eDetectedSoundMode = eSavedMTSMode;
                else
                {
                    eDetectedSoundMode= E_AUDIOMODE_MONO_;
                }
            break;

            default:
                break;

        }
        bForceCheckAudioMode=FALSE;
    }

    if(((eCurrentSoundMode == E_AUDIOMODE_NICAM_DUAL_A_) || (eCurrentSoundMode == E_AUDIOMODE_NICAM_DUAL_B_) || (eCurrentSoundMode == E_AUDIOMODE_NICAM_DUAL_AB_)) &&
        ((eDetectedSoundMode == E_AUDIOMODE_NICAM_DUAL_A_) || (eDetectedSoundMode == E_AUDIOMODE_NICAM_DUAL_B_) || (eDetectedSoundMode == E_AUDIOMODE_NICAM_DUAL_AB_)) )
    {
        return;
    }

    if(eCurrentSoundMode == eDetectedSoundMode)
    {
        return;
    }

    eSavedMTSMode=eDetectedSoundMode;
    MSrv_Control::GetMSrvSSSound()->SetMtsMode(eSavedMTSMode);
    MSrv_Control::GetMSrvAtvDatabase()->CommondCmd(SET_AUDIO_MODE , 0, eSavedMTSMode ,NULL);
}
#endif

//------------------------------------------------------------------------------
/// -Enable Scaler
/// @return \none
//------------------------------------------------------------------------------
void MSrv_ATV_Player::EnableScaler(void)
{
    //========== To enable scaler
    //Wait for the signal stable
    {
        //Set Mode
        mapi_video_vd_cfg *pVDData = new(std::nothrow) mapi_video_vd_cfg;
        ASSERT(pVDData);

        pVDData->enVideoStandard = GetVideoStandard();
        if(pVDData->enVideoStandard >= E_MAPI_VIDEOSTANDARD_NOTSTANDARD)
        {
            MAPI_INPUT_SOURCE_TYPE enInputSrc = MAPI_INPUT_SOURCE_ATV;
            MSrv_Control::GetMSrvSystemDatabase()->GetLastVideoStandard(&(pVDData->enVideoStandard), &enInputSrc);
            mapi_interface::Get_mapi_vd()->SetVideoStandard(pVDData->enVideoStandard,FALSE);    // To fix the issue that garbage is displayed during first autotuning (DTV+ATV)/ATV when boot up on DTV source.(mantis 0789526)
        }

        MSrv_Control::GetMSrvPicture()->Off();
        mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetMode(pVDData);

        if(pVDData != NULL)
        {
            delete pVDData;
            pVDData = NULL;
        }

        //Set window
        mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO stVideoARCInfo;//eVideoARCType; //ARC type on "SCREEN" ( Result ARC )ST_MAPI_VIDEO_ARC_INFO
        memset(&stVideoARCInfo, 0, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO));
        MAPI_INPUT_SOURCE_TYPE enCurrentInputType = MAPI_INPUT_SOURCE_ATV;
        MSrv_Control::GetMSrvSystemDatabase()->GetVideoArc(&stVideoARCInfo.enARCType, &enCurrentInputType);

        stVideoARCInfo.s16Adj_ARC_Left = 0;
        stVideoARCInfo.s16Adj_ARC_Right = 0;
        stVideoARCInfo.s16Adj_ARC_Up = 0;
        stVideoARCInfo.s16Adj_ARC_Down = 0;
        stVideoARCInfo.bSetCusWin = MAPI_FALSE;
#if (PIP_ENABLE == 1)
        EN_PIP_MODES enPipMode = MSrv_Control::GetInstance()->GetPipMode();
        if(  (enPipMode == E_PIP_MODE_OFF)
           ||(enPipMode==E_PIP_MODE_TRAVELING && m_PipXCWin==MAPI_SUB_WINDOW))
        {
            mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetWindow(NULL, NULL, &stVideoARCInfo);
        }
        else if(enPipMode == E_PIP_MODE_PIP)
        {
            mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stWinInfo;
            if(m_PipXCWin == MAPI_SUB_WINDOW)
            {
                MSrv_Control::GetInstance()->GetPipSubwindow(&stWinInfo);
                mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetWindow(NULL, &stWinInfo, &stVideoARCInfo);
            }
            else
            {
                mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetWindow(NULL, NULL, &stVideoARCInfo);
            }
        }
        else
        {
            mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stWinInfo;
            if(m_PipXCWin == MAPI_SUB_WINDOW)
                MSrv_Control::GetInstance()->GetPipSubwindow(&stWinInfo);
            else
                MSrv_Control::GetInstance()->GetMainwindow(&stWinInfo);

            mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetWindow(NULL, &stWinInfo, &stVideoARCInfo);
        }
#else
        mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetWindow(NULL, NULL, &stVideoARCInfo);
#endif

#if (PIP_ENABLE == 1)
        // temp solution, need refine it later
        if(m_PipXCWin == MAPI_MAIN_WINDOW)
        {
            MSrv_Control::GetMSrvPicture()->On();
        }
        else
        {
            MSrv_Control::GetMSrvVideo()->SelectWindow(MAPI_SUB_WINDOW);
            MSrv_Control::GetMSrvPicture()->On();
            MSrv_Control::GetMSrvVideo()->SelectWindow(MAPI_MAIN_WINDOW);
        }
#else
        MSrv_Control::GetMSrvPicture()->On();
#endif
        //Un-mute Screen,
        mapi_interface::Get_mapi_vd()->IsVideoFormatChanged();//To clear the change flag
    }
    //=====================
}
#if (ATSC_SYSTEM_ENABLE == 1)
void MSrv_ATV_Player::DefaultMTSSelection(void)
{
    ST_MISC_SETTING stMiscSetting;
    dynamic_cast<MSrv_System_Database_ATSC *>(MSrv_Control::GetMSrvSystemDatabase())->GetMiscSetting(&stMiscSetting);
    switch(MSrv_Control::GetMSrvSSSound()->GetSoundMode())
    {
        default:
        case E_AUDIOMODE_INVALID_:
        case E_AUDIOMODE_MONO_:
        case E_AUDIOMODE_FORCED_MONO_:
            if(MSrv_Control::GetMSrvSSSound()->GetMtsMode() != E_AUDIOMODE_MONO_)
                MSrv_Control::GetMSrvSSSound()->SetMtsMode(E_AUDIOMODE_MONO_);
            break;
        case E_AUDIOMODE_K_STEREO_:
        case E_AUDIOMODE_G_STEREO_:
            if(stMiscSetting.MTSSetting == E_MTS_MONO)
                MSrv_Control::GetMSrvSSSound()->SetMtsMode(E_AUDIOMODE_FORCED_MONO_);
            else
                MSrv_Control::GetMSrvSSSound()->SetMtsMode(E_AUDIOMODE_G_STEREO_);
            break;
        case E_AUDIOMODE_MONO_SAP_:
            if(stMiscSetting.MTSSetting == E_MTS_SAP)
                MSrv_Control::GetMSrvSSSound()->SetMtsMode(E_AUDIOMODE_MONO_SAP_);
            else
                MSrv_Control::GetMSrvSSSound()->SetMtsMode(E_AUDIOMODE_MONO_);
            break;
        case E_AUDIOMODE_STEREO_SAP_:
            if(stMiscSetting.MTSSetting == E_MTS_MONO)
                MSrv_Control::GetMSrvSSSound()->SetMtsMode(E_AUDIOMODE_MONO_);
            else if(stMiscSetting.MTSSetting == E_MTS_STEREO)
                MSrv_Control::GetMSrvSSSound()->SetMtsMode(E_AUDIOMODE_G_STEREO_);
            else
                MSrv_Control::GetMSrvSSSound()->SetMtsMode(E_AUDIOMODE_STEREO_SAP_);
            break;
    }

    m_bIsMTSMonitorEnabled = TRUE;
}

void MSrv_ATV_Player::CurrentMTSMonitor(void)
{
    MAPI_U8 u8MTSMode;

    u8MTSMode = MSrv_Control::GetMSrvSSSound()->GetMtsMode();

    switch(MSrv_Control::GetMSrvSSSound()->GetSoundMode())
    {
        default:
        case E_AUDIOMODE_INVALID_:
        case E_AUDIOMODE_MONO_:
        case E_AUDIOMODE_FORCED_MONO_:
            if(u8MTSMode != E_AUDIOMODE_MONO_)
                MSrv_Control::GetMSrvSSSound()->SetMtsMode(E_AUDIOMODE_MONO_);
            break;
        case E_AUDIOMODE_K_STEREO_:
        case E_AUDIOMODE_G_STEREO_:
            if(u8MTSMode == E_AUDIOMODE_MONO_SAP_ || u8MTSMode == E_AUDIOMODE_STEREO_SAP_)
                MSrv_Control::GetMSrvSSSound()->SetMtsMode(E_AUDIOMODE_G_STEREO_);
            break;
        case E_AUDIOMODE_MONO_SAP_:
            if(u8MTSMode == E_AUDIOMODE_K_STEREO_ || u8MTSMode == E_AUDIOMODE_G_STEREO_)
                MSrv_Control::GetMSrvSSSound()->SetMtsMode(E_AUDIOMODE_MONO_);
            break;
        case E_AUDIOMODE_STEREO_SAP_:
            break;
    }
}
#endif

#if (ESASIA_NTSC_SYSTEM_ENABLE == 1 || ISDB_SYSTEM_ENABLE == 1)//Add for MTS detection issue: 0660220 20140710EL
void MSrv_ATV_Player::CurrentMTSMonitor(void)
{
    MAPI_U8 u8MTSMode;

    u8MTSMode = MSrv_Control::GetMSrvSSSound()->GetMtsMode();

    switch(MSrv_Control::GetMSrvSSSound()->GetSoundMode())
    {
        default:
        case E_AUDIOMODE_INVALID_:
        case E_AUDIOMODE_MONO_:
        case E_AUDIOMODE_FORCED_MONO_:
            if(u8MTSMode != E_AUDIOMODE_MONO_)
                MSrv_Control::GetMSrvSSSound()->SetMtsMode(E_AUDIOMODE_MONO_);
            break;
        case E_AUDIOMODE_K_STEREO_:
        case E_AUDIOMODE_G_STEREO_:
            if(u8MTSMode == E_AUDIOMODE_MONO_SAP_ || u8MTSMode == E_AUDIOMODE_STEREO_SAP_)
                MSrv_Control::GetMSrvSSSound()->SetMtsMode(E_AUDIOMODE_G_STEREO_);
            break;
        case E_AUDIOMODE_MONO_SAP_:
            if(u8MTSMode == E_AUDIOMODE_K_STEREO_ || u8MTSMode == E_AUDIOMODE_G_STEREO_)
                MSrv_Control::GetMSrvSSSound()->SetMtsMode(E_AUDIOMODE_MONO_);
            break;
        case E_AUDIOMODE_STEREO_SAP_:
            break;
    }
}

#endif

void MSrv_ATV_Player::SetMTSModeFlag(BOOL bFlag)
{
   m_bIsDoSetMTSMode = bFlag;
}

#if ENABLE_CUSTOMER_ATS_TABLE
U16 *MSrv_ATV_Player::CNI_Cus_GetExtATSTableCallBack(void)
{
#if 0
    // Customize Here
    switch(g_ucCountryId)
    {
        case COUNTRYID_GERMANY:
            return tStationIdList_ext_GERMANY;
        default:
            break;
    }
#endif
    return NULL;
}
#endif

BOOL MSrv_ATV_Player::_PostVideoInit()
{
    return TRUE;
}

#if (HBBTV_ENABLE == 1)
#define CHECK_PLAYER_INITIAL_DONE()                                       \
    if( !m_bInit )                                                             \
    {                                                                          \
        printf("Error --- ATV Player not Initial !! \n");                      \
        ASSERT(0);                                                             \
        return FALSE;                                                          \
    }                                                                          \


bool MSrv_ATV_Player::HBBTV_ServiceRequestHandler(void* hbbtvCommand)
{
    HbbtvCommandPayload* command = (HbbtvCommandPayload*)(hbbtvCommand);
    if(command->instruct() == BrowserBackendGlue::Hbbtv::TunerCommand_ExitLiveIn)
    {
        CHECK_PLAYER_INITIAL_DONE();

        if(!MW_HBBTV::GetInstance().IsStreaming())
        {
            MW_HBBTV::GetInstance().SetStreamingMode(TRUE);
            DisableChannel();
            MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_SCAN_MUTEON_, m_CurrentSrcType);
            m_bIsPreProgramDisabled = TRUE;
            ChangeSource(MAPI_INPUT_SOURCE_STORAGE);
        }

        HbbtvServiceRequestAnswer answer(true);
        MW_HBBTV::GetInstance().BackendServiceRequestFinalHandler(command,
                                                                  answer);

        return TRUE;
    }
    else if(command->instruct() == BrowserBackendGlue::Hbbtv::TunerCommand_ReturnLiveIn)
    {
        if(MW_HBBTV::GetInstance().IsStreaming())
        {
            MW_HBBTV::GetInstance().SetStreamingMode(FALSE);
            ChangeSource(MAPI_INPUT_SOURCE_ATV);
            EnableChannel();
        }

        HbbtvServiceRequestAnswer answer(true);
        MW_HBBTV::GetInstance().BackendServiceRequestFinalHandler(command,
                                                                  answer);
        return TRUE;
    }
    else
    {
        bool returnValue =
            MW_HBBTV_CommandHandlerBase::HBBTV_ServiceRequestHandler(hbbtvCommand);

        return returnValue;
    }

}

#endif

#if (MSTAR_TVOS == 1)
void MSrv_ATV_Player::SetCountry(MEMBER_COUNTRY enCountry)
{
#if (TTX_ENABLE == 1)
    #if (PIP_ENABLE == 1)
     if((FALSE == MSrv_Control::GetInstance()->IsPipModeEnable()) || (m_PipXCWin == MAPI_MAIN_WINDOW))
    #endif
    {
        m_pcTTX->SetCountry(enCountry);
    }
#endif
    MSrv_Control::GetMSrvSystemDatabase()->SetSystemCountry(&enCountry);
}

void MSrv_ATV_Player::ResetFactoryAtvProgramData(U8 u8AtvProgramIndex, U32 u32FrequencyKHz, U8 u8AudioStandard, U8 u8VideoStandard)
{
    MAPI_U16 u16FreqKHz = 0;
    MAPI_U8  sStationName[MAX_STATION_NAME];
    MAPI_U8  i = u8AtvProgramIndex;

    if (u32FrequencyKHz == 0)
    {
       return;
    }

    memset(sStationName, 0,  sizeof(sStationName));
    sStationName[0] = 'A';
    sStationName[1] = 'T';
    sStationName[2] = 'V';

    //MSrv_Control::GetInstance()->GetMSrvAtvDatabase()->SetProgramCtrl(RESET_CHANNEL_DATA, 0, 0,NULL);

    u16FreqKHz = MSrv_Control::GetMSrvAtv()->ConvertFrequncyHzToPLL(u32FrequencyKHz*1000);
    MSrv_Control::GetInstance()->GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER, i, 0, NULL);
    MSrv_Control::GetInstance()->GetMSrvAtvDatabase()->SetProgramInfo(NEED_AFT, i, FALSE, NULL);
    MSrv_Control::GetInstance()->GetMSrvAtvDatabase()->SetProgramInfo(SKIP_PROGRAM, i, FALSE, NULL);
    MSrv_Control::GetInstance()->GetMSrvAtvDatabase()->SetFavoriteProgram(SET_FAVORITE_PROGRAM, i, FALSE, NULL);
    MSrv_Control::GetInstance()->GetMSrvAtvDatabase()->SetProgramInfo(LOCK_PROGRAM, i, FALSE, NULL);
    MSrv_Control::GetInstance()->GetMSrvAtvDatabase()->SetProgramInfo(ENABLE_REALTIME_AUDIO_DETECTION, i, FALSE, NULL);
    MSrv_Control::GetInstance()->GetMSrvAtvDatabase()->SetProgramInfo(SET_STATION_NAME, i, 0, sStationName);
    MSrv_Control::GetInstance()->GetMSrvAtvDatabase()->SetProgramInfo(SET_AUDIO_STANDARD, i, u8AudioStandard, NULL);
    MSrv_Control::GetInstance()->GetMSrvAtvDatabase()->SetProgramInfo(SET_VIDEO_STANDARD_OF_PROGRAM, i, u8VideoStandard, NULL);
    MSrv_Control::GetInstance()->GetMSrvAtvDatabase()->SetProgramInfo(SET_AFT_OFFSET, i, 0, NULL);
    MSrv_Control::GetInstance()->GetMSrvAtvDatabase()->SetProgramInfo(SET_PROGRAM_PLL_DATA, i, u16FreqKHz, NULL);
    MSrv_Control::GetInstance()->GetMSrvAtvDatabase()->CommondCmd(SET_AUDIO_MODE, 0, (U16)E_AUDIOMODE_FORCED_MONO_, NULL);
    MSrv_Control::GetMSrvAtv()->ATVSetMediumAndChannelNumber(i, (MSrv_ATV_Database::MEDIUM)0, 0);
}

void MSrv_ATV_Player::SetChannelVolumeCompensation(EN_CHANNEL_VOLUME_COM_STATE eChlVolumeComState, U16 u16ProgramNumber)
{
    U16 u16CurrentChannel = 0;
    U16 u16NextChannel = 0;
    U16 u16PreChannel = 0;
    ST_ATV_MISC AtvMisc;

    memset(&AtvMisc, 0, sizeof(AtvMisc));
    switch(eChlVolumeComState)
    {
        case E_CURRENT_CHNNEL_VOLUME_COMPENSATION:
            u16CurrentChannel = MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL);
            MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_MISC, u16CurrentChannel, 0, (U8 *) &(AtvMisc));
            break;

        case E_NEXT_CHNNEL_VOLUME_COMPENSATION:
            u16CurrentChannel = MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL);
            u16NextChannel = MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_NEXT_PROGRAM_NUMBER, u16CurrentChannel, 0, NULL);
            MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_MISC, u16NextChannel, 0, (U8 *) &(AtvMisc));
            break;

        case E_PRE_CHNNEL_VOLUME_COMPENSATION:
            u16CurrentChannel = MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL);
            u16PreChannel = MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_PREV_PROGRAM_NUMBER, u16CurrentChannel, 0, NULL);
            MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_MISC, u16PreChannel, 0, (U8 *) &(AtvMisc));
            break;

        case E_SEL_CHNNEL_VOLUME_COMPENSATION:
            if(u16ProgramNumber == 0xFFFF)
            {
                ASSERT(0);
            }
            MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_MISC, u16ProgramNumber, 0, (U8 *) &(AtvMisc));
            break;

        default:
            printf("*** SetChannelVolumeCompensation Error *** \n");
            break;
    }
    MSrv_Control::GetMSrvSSSound()->SetAudioVolume(VOL_SOURCE_COMPENSATION, AtvMisc.eVolumeComp);

}
#endif

#if (TV_FREQ_SHIFT_CLOCK)
void MSrv_ATV_Player::_TVShiftClk(MAPI_AVD_ATV_CLK_TYPE u8Mode)
{
    if( u8Mode != mapi_interface::Get_mapi_vd()->GetShiftClkLastMode())
    {
        printf("\r\nTv shift Clk=%d\n", u8Mode);
        switch (u8Mode)
        {
            case E_MAPI_ATV_CLK_TYPE1_42MHZ:
                printf("--E_ATV_CLK_TYPE1_42MHZ---\n");
                mapi_interface::Get_mapi_audio()->SIF_Shift(MSAPI_AUD_SIF_42M_);
                mapi_interface::Get_mapi_vif()->VifShiftClk((U8)u8Mode);
                mapi_interface::Get_mapi_vd()->SetAVDShiftClk(u8Mode);
                break;
            case E_MAPI_ATV_CLK_TYPE2_44P4MHZ:
                printf("--E_ATV_CLK_TYPE1_44p4MHZ---\n");
                mapi_interface::Get_mapi_audio()->SIF_Shift(MSAPI_AUD_SIF_44M_);
                mapi_interface::Get_mapi_vif()->VifShiftClk((U8)u8Mode);
                mapi_interface::Get_mapi_vd()->SetAVDShiftClk(u8Mode);
                break;
            case E_MAPI_ATV_CLK_ORIGIN_43P2MHZ:
            default:
                printf("--E_ATV_CLK_TYPE1_43.2MHZ--\n");
                mapi_interface::Get_mapi_audio()->SIF_Shift(MSAPI_AUD_SIF_43M_);
                mapi_interface::Get_mapi_vif()->VifShiftClk((U8)u8Mode);
                mapi_interface::Get_mapi_vd()->SetAVDShiftClk(u8Mode);
                break;
        }
        msAPI_Tuner_SetIF();
        mapi_interface::Get_mapi_vd()->SetShiftClkLastMode((U8)u8Mode);
    }
}
MAPI_AVD_ATV_CLK_TYPE MSrv_ATV_Player::_Get_Shift_Mode(U32 u32Freq)
{
    U32 u32Freq_KHz = u32Freq;

    if (u32Freq_KHz <= 237000)   // 0~237MHz
    {
        if (((u32Freq_KHz >= 85250)&&(u32Freq_KHz <= 87650)) ||
            ((u32Freq_KHz >= 127250)&&(u32Freq_KHz <= 130850)) ||
            ((u32Freq_KHz >= 141250)&&(u32Freq_KHz <= 145250)) ||/**/
            ((u32Freq_KHz >= 169250)&&(u32Freq_KHz <= 174050)) ||
            ((u32Freq_KHz >= 211250)&&(u32Freq_KHz <= 217250)))
        {
            return E_MAPI_ATV_CLK_TYPE1_42MHZ;
        }
        else if (((u32Freq_KHz >= 81400)&&(u32Freq_KHz <= 83800)) ||
            ((u32Freq_KHz >= 124600)&&(u32Freq_KHz <= 127000)) ||
            ((u32Freq_KHz >= 137400)&&(u32Freq_KHz <141250)) ||/**/
            ((u32Freq_KHz >= 167800)&&(u32Freq_KHz <= 169000)))
        {
            return E_MAPI_ATV_CLK_TYPE2_44P4MHZ;
        }
    }
    else if (u32Freq_KHz <= 453000)   // 237~453MHz
    {
        if (((u32Freq_KHz >= 253250)&&(u32Freq_KHz <= 260450)) ||
            ((u32Freq_KHz >= 281250)&&(u32Freq_KHz <= 289250)) ||/**/
            ((u32Freq_KHz >= 295250)&&(u32Freq_KHz <= 303650)) ||
            ((u32Freq_KHz >= 340600)&&(u32Freq_KHz <= 346850)) ||
            ((u32Freq_KHz >= 383800)&&(u32Freq_KHz <= 390050)) ||
            ((u32Freq_KHz >= 427000)&&(u32Freq_KHz <= 433250)))
        {
            return E_MAPI_ATV_CLK_TYPE1_42MHZ;
        }
    }
    else if (u32Freq_KHz <= 669000)   // 453~669MHz
    {
        if (((u32Freq_KHz >= 421250)&&(u32Freq_KHz <= 433250)) ||/**/
            ((u32Freq_KHz >= 470200)&&(u32Freq_KHz <= 476450)) ||
            ((u32Freq_KHz >= 513400)&&(u32Freq_KHz <= 519650)) ||
            ((u32Freq_KHz >= 556600)&&(u32Freq_KHz <= 562850)) ||
           ((u32Freq_KHz >= 561250)&&(u32Freq_KHz <= 577250)) ||/**/
            ((u32Freq_KHz >= 599800)&&(u32Freq_KHz <= 606050)) ||
            ((u32Freq_KHz >= 643000)&&(u32Freq_KHz <= 649250)))
        {
            return E_MAPI_ATV_CLK_TYPE1_42MHZ;
        }
    }
    else
    {
        if (((u32Freq_KHz >= 686200)&&(u32Freq_KHz <= 692450)) ||
            ((u32Freq_KHz >= 701250)&&(u32Freq_KHz <= 721250)) ||/**/
            ((u32Freq_KHz >= 729400)&&(u32Freq_KHz <= 735650)) ||
            ((u32Freq_KHz >= 772600)&&(u32Freq_KHz <= 778850)) ||
            ((u32Freq_KHz >= 815800)&&(u32Freq_KHz <= 822050)) ||
            ((u32Freq_KHz >= 859000)&&(u32Freq_KHz <= 865250)))
        {
            return E_MAPI_ATV_CLK_TYPE1_42MHZ;
        }
    }
    return E_MAPI_ATV_CLK_ORIGIN_43P2MHZ;
}
void MSrv_ATV_Player::_Set_Shift_Freq(U32 u32Freq)
{
    MAPI_AVD_ATV_CLK_TYPE u8FreqShiftMode = E_MAPI_ATV_CLK_ORIGIN_43P2MHZ;

#if((FRONTEND_IF_DEMODE_TYPE == MSTAR_VIF) ||(FRONTEND_IF_DEMODE_TYPE == MSTAR_INTERN_VIF) ||(FRONTEND_IF_DEMODE_TYPE == MSTAR_VIF_MSB1210)    )
    if (m_bIsScanning == TRUE)
#endif
    {
        _TVShiftClk(E_MAPI_ATV_CLK_ORIGIN_43P2MHZ);
        return;
    }
    u8FreqShiftMode=_Get_Shift_Mode(u32Freq);
    _TVShiftClk(u8FreqShiftMode);
}
void MSrv_ATV_Player::msAPI_Tuner_Patch_TVShiftClk(BOOL bEnable)
{
    U32 u32TunerFreq;

    if (bEnable)
    {
        u32TunerFreq = ConvertPLLtoFrequencyKHZ(m_u16TunerPLL);
        _Set_Shift_Freq(u32TunerFreq);
    }
    else
    {
        _TVShiftClk(E_MAPI_ATV_CLK_ORIGIN_43P2MHZ);
    }
}

#endif

void MSrv_ATV_Player::RecoverUserMtsSetting(void)
{

    U32 eSavedMTSMode = E_AUDIOMODE_INVALID_;
    U8 eDetectMTSMode = MSrv_Control::GetMSrvSSSound()->GetSoundMode();


    MSrv_Control::GetMSrvAtvDatabase()->CommondCmd(GET_AUDIO_MODE  , 0 , 0 , &eSavedMTSMode);

    switch(eSavedMTSMode)
    {
        case E_AUDIOMODE_MONO_:
        case E_AUDIOMODE_FORCED_MONO_:
            MSrv_Control::GetMSrvSSSound()->SetMtsMode(E_AUDIOMODE_FORCED_MONO_);
            break;

        case E_AUDIOMODE_DUAL_A_:
        case E_AUDIOMODE_DUAL_B_:
        case E_AUDIOMODE_DUAL_AB_:
            if(eDetectMTSMode == E_AUDIOMODE_DUAL_A_)
                MSrv_Control::GetMSrvSSSound()->SetMtsMode(eSavedMTSMode);
            else
                MSrv_Control::GetMSrvSSSound()->SetMtsMode(eDetectMTSMode);
            break;

        case E_AUDIOMODE_NICAM_DUAL_A_:
        case E_AUDIOMODE_NICAM_DUAL_B_:
        case E_AUDIOMODE_NICAM_DUAL_AB_:
            if(eDetectMTSMode == E_AUDIOMODE_NICAM_DUAL_A_)
                MSrv_Control::GetMSrvSSSound()->SetMtsMode(eSavedMTSMode);
            else
                MSrv_Control::GetMSrvSSSound()->SetMtsMode(eDetectMTSMode);
            break;

        default:
            MSrv_Control::GetMSrvSSSound()->SetMtsMode(eDetectMTSMode);
            break;
    }

    if((m_u32DelayAudioUnmuteTime != 0) && (mapi_time_utility::TimeDiffFromNow0(m_u32DelayAudioUnmuteTime) > ATV_DELAY_AUDIO_UNMUTE_TIME))
    {
        MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_INTERNAL_1_MUTEOFF_, m_CurrentSrcType);
        m_u32DelayAudioUnmuteTime = 0;
    }
}
#if (CHANNEL_CHANGE_FREEZE_IMAGE_BYDFB_ENBALE == 1)
void MSrv_ATV_Player::ChannelChangeFreezeImage(BOOL bEnable)
{
    if(bEnable)
    {
        if((IsDiffVideoStandByChannelAndVD() == FALSE))
        {
           m_bNeedReloadPQ = FALSE;
        }
        else
        {
           m_bNeedReloadPQ = TRUE;

           if(m_bBeforeFirstSetChannel == TRUE)
           {
           m_bBeforeFirstSetChannel = FALSE;
           }
        }

        if((m_pcVideo->IsFreezeImage()) == FALSE)
        {
          m_pcVideo->FreezeImage(TRUE);
        }

        if((IsShowFreezeImageByDFB()) == FALSE)
        {
          ShowFreezeImageByDFB(TRUE);
          if((IsShowFreezeImageByDFB()) == FALSE)
          {
             MSrv_Control::GetInstance()->SetVideoMute(TRUE, 0 , m_PipXCWin);
          }
        }

    }
    else
    {
        if((m_pcVideo->IsFreezeImage()) == TRUE)
        {
          m_pcVideo->FreezeImage(FALSE);
        }

        if((IsShowFreezeImageByDFB()) == TRUE)
        {
          ShowFreezeImageByDFB(FALSE);
          if((IsShowFreezeImageByDFB()) == TRUE)
          {
              MSrv_Control::GetInstance()->SetVideoMute(FALSE, 0, m_PipXCWin);
          }
        }
    }
}
#endif

void MSrv_ATV_Player::RefreshWindow()
{
    //Set window
    mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stVideoWinType;
    mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO stVideoARCInfo;//eVideoARCType; //ARC type on "SCREEN" ( Result ARC )ST_MAPI_VIDEO_ARC_INFO
    mapi_vd_datatype::ASPECT_RATIO_TYPE enWSSARCType;  //ARC type come from input source
    memset(&stVideoARCInfo, 0, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO));

    MAPI_INPUT_SOURCE_TYPE enCurrentInputType = MAPI_INPUT_SOURCE_ATV;
    MSrv_Control::GetMSrvSystemDatabase()->GetVideoArc(&stVideoARCInfo.enARCType, &enCurrentInputType);

    stVideoARCInfo.s16Adj_ARC_Left = 0;
    stVideoARCInfo.s16Adj_ARC_Right = 0;
    stVideoARCInfo.s16Adj_ARC_Up = 0;
    stVideoARCInfo.s16Adj_ARC_Down = 0;
    stVideoARCInfo.bSetCusWin = MAPI_FALSE;

    if (stVideoARCInfo.enARCType == mapi_video_datatype::E_AR_AUTO)
    {
        enWSSARCType = mapi_interface::Get_mapi_vd()->GetAspectRatioCode();//waiting VD class
        CalWSSWin(enWSSARCType, &stVideoWinType, &stVideoARCInfo);
    }

#if (PIP_ENABLE == 1)
    EN_PIP_MODES enPipMode = MSrv_Control::GetInstance()->GetPipMode();
    if ((enPipMode == E_PIP_MODE_OFF) || (enPipMode==E_PIP_MODE_TRAVELING && m_PipXCWin==MAPI_SUB_WINDOW))
    {
        mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetWindow(NULL, NULL, &stVideoARCInfo);
    }
    else if(enPipMode == E_PIP_MODE_PIP)
    {
        mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stWinInfo;
        if(m_PipXCWin == MAPI_SUB_WINDOW)
        {
            MSrv_Control::GetInstance()->GetPipSubwindow(&stWinInfo);
            mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetWindow(NULL, &stWinInfo, &stVideoARCInfo);
        }
        else
        {
            mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetWindow(NULL, NULL, &stVideoARCInfo);
        }
    }
    else
    {
        mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stWinInfo;
        if(m_PipXCWin == MAPI_SUB_WINDOW)
        {
            MSrv_Control::GetInstance()->GetPipSubwindow(&stWinInfo);
        }
        else
        {
            MSrv_Control::GetInstance()->GetMainwindow(&stWinInfo);
        }

        mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetWindow(NULL, &stWinInfo, &stVideoARCInfo);
    }
#else
    mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetWindow(NULL, NULL, &stVideoARCInfo);
#endif
}

#if (INPUT_SOURCE_LOCK_ENABLE == 1)
MAPI_BOOL MSrv_ATV_Player::EnableMiddleware(MAPI_BOOL bEnable)
{
    BOOL bRet = TRUE;

#if (TTX_ENABLE == 1)
    static U8 u8TTXmodeTmp = TTX_MODE_NONE + 1;
#endif

    if (bEnable == MAPI_TRUE)
    {
        //Enable HBBTV
#if (HBBTV_ENABLE == 1)
        if(MW_HBBTV::GetCheckedInstance())
        {
            MW_HBBTV::GetCheckedInstance()->Validate();
        }
#endif
        //Enable CC
#if (ISDB_CC_ENABLE == 1)
        bRet &= MW_CC::GetInstance()->StartCaption(E_NORMAL_CAPTION);
#elif ((ATSC_CC_ENABLE == 1) || (NTSC_CC_ENABLE == 1))
        bRet &= MW_CC::GetInstance()->StartCaption();
#endif
        //Enable TTX
#if (TTX_ENABLE == 1)
        if(m_pcTTX != NULL)
        {
            if (u8TTXmodeTmp <= TTX_MODE_NONE)
            {
                bRet &= m_pcTTX->Open((EN_TELETEXT_MODE)u8TTXmodeTmp);
            }
        }
#endif
    }
    else
    {
        //Disable TTX
#if (TTX_ENABLE == 1)
        if(m_pcTTX != NULL)
        {
            MW_TTX_STATUS ttxStatus;
            memset(&ttxStatus, 0, sizeof(MW_TTX_STATUS));
            m_pcTTX->GetStatus(&ttxStatus);
            if(ttxStatus.bOpen == TRUE)
            {
                u8TTXmodeTmp = (U8)ttxStatus.ttxOpenMode;
                bRet &= m_pcTTX->Close();
            }
        }
#endif
        //Disable CC
#if (ISDB_CC_ENABLE == 1)
        bRet &= MW_CC::GetInstance()->StopCaption();
#elif ((ATSC_CC_ENABLE == 1) || (NTSC_CC_ENABLE == 1))
        bRet &= MW_CC::GetInstance()->StopCaption();
        bRet &= MW_CC::GetInstance()->DestoryCaption();
#endif
        //Disable HBBTV
#if (HBBTV_ENABLE == 1)
        if(MW_HBBTV::GetCheckedInstance())
        {
            MW_HBBTV::GetCheckedInstance()->Invalidate();
        }
#endif
    }

    if (bRet == TRUE)
    {
        return MAPI_TRUE;
    }

    return MAPI_FALSE;
}
#endif
// EosTek Patch Begin
BOOL MSrv_ATV_Player::TunerStatus(void)
{
if(m_pTuner->TunerStatus() == TRUE)
{
		//printf("=====jowen====(m_pTuner->TunerStatus() == TRUE)\n");
        return TRUE;
    }
else
	{
		//printf("=====jowen====(m_pTuner->TunerStatus() == FLASE)\n");
	 return FALSE;
	}
}
// EosTek Patch End
/*@ </Operation ID=If58cf9dm12168c8928bmm640b> @*/
