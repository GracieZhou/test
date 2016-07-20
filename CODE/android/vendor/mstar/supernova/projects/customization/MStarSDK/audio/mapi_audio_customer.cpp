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
/**********************************************************************
 Copyright (c) 2006-2009 MStar Semiconductor, Inc.
 All rights reserved.

 Unless otherwise stipulated in writing, any and all information contained
 herein regardless in any format shall remain the sole proprietary of
 MStar Semiconductor Inc. and be kept in strict confidence
 (MStar Confidential Information) by the recipient.
 Any unauthorized act including without limitation unauthorized disclosure,
 copying, use, reproduction, sale, distribution, modification, disassembling,
 reverse engineering and compiling of the contents of MStar Confidential
 Information is unlawful and strictly prohibited. MStar hereby reserves the
 rights to any and all damages, losses, costs and expenses resulting therefrom.

* Class : mapi_base
* File  : mapi_base.cpp
**********************************************************************/

// headers of standard C libs
#include <unistd.h>
#include <limits.h>
#include <math.h>
#include <string.h>
#include <sys/prctl.h>

// headers of standard C++ libs

// headers of the same layer's
#include "debug.h"
#include "mapi_utility.h"
#include "mapi_gpio.h"
#include "mapi_gpio_devTable.h"
#include "mapi_syscfg_fetch.h"
#include "mapi_pcb.h"
#include "mapi_audio_amp.h"
#include "mapi_system.h"
#if (STR_ENABLE == 1)
#include "mapi_str.h"
#endif

// headers of itself
#include "mapi_audio_customer.h"

#if (AACENCODER_ENABLE == 1)
// headers of 3rd party aac encoder library
#include "exp_emz_common.h"
#include "exp_Mp4AacEnc_API.h"
#endif

// headers of underlying layer's
#include "MMAPInfo.h"
#include "iniparser.h"

#include "apiAUDIO.h"
#include "apiDMX.h"
#include "drvAUDIO.h"
#include "drvAUDIO_if.h"
//#include "drvXC_HDMI_if.h"

#if (STB_ENABLE == 1)
#include "apiHDMITx.h"
#endif
#if defined(PQ_ENGINE) && (PQ_ENGINE == 1)
#include "mapi_ursa.h"
#include "mapi_pcb.h"
#include "mapi_interface.h"
#endif
#define MTS_NICAM_UNSTABLE 0    //For NICAM Unstable issue in Indonesia

#define SYS_INI_PATH_FILENAME          "/config/sys.ini"

/* AVC  default*/
#define DEFAULT_AVCMODE  2
#define DEFAULT_AVCAT  3
#define DEFAULT_AVCRT  1
#define DEFAULT_AVCTHRESHOLD  0x20

#if (MTS_NICAM_UNSTABLE)
MS_U16    g_NICAMEnable = 1;
MS_U8     g_CarrierStableCnt = 0;
#endif

// headers of underlying layer's

using namespace std;

#define AUDIOCUSMSG(x)      {printf("[MApi_Audio_customer] ");x;}
#define ZOOM_DENUMERATOR    1000

THR_TBL_TYPE  AuSifInitThreshold_PAL_SIF[] =
{
    {0x01 , 0x50 } ,    //A2_M     CARRIER1_ON_AMP
    {0x01 , 0x00 } ,    //A2_M     CARRIER1_OFF_AMP
    {0x12 , 0x00 } ,    //A2_M     CARRIER1_ON_NSR
    {0x7F , 0xFF } ,    //A2_M     CARRIER1_OFF_NSR
    {0x01 , 0x00 } ,    //A2_M     CARRIER2_ON_AMP
    {0x00 , 0x80 } ,    //A2_M     CARRIER2_OFF_AMP
    {0x11 , 0x00 } ,    //A2_M     CARRIER2_ON_NSR
    {0x15 , 0x00 } ,    //A2_M     CARRIER2_OFF_NSR
    {0x02 , 0x80 } ,    //A2_M     A2_PILOT_ON_AMP
    {0x01 , 0x00 } ,    //A2_M     A2_PILOT_OFF_AMP

    {0x01 , 0x50 } ,    //A2_BG    CARRIER1_ON_AMP
    {0x01 , 0x00 } ,    //A2_BG    CARRIER1_OFF_AMP
    {0x20 , 0x00 } ,    //A2_BG    CARRIER1_ON_NSR
    {0x7F , 0xFF } ,    //A2_BG    CARRIER1_OFF_AMP
    {0x00 , 0x40 } ,    //A2_BG    CARRIER2_ON_AMP
    {0x00 , 0x30 } ,    //A2_BG    CARRIER2_OFF_AMP
    {0x11 , 0x00 } ,    //A2_BG    CARRIER2_ON_NSR
    {0x30 , 0x00 } ,    //A2_BG    CARRIER2_OFF_NSR
    {0x02 , 0x80 } ,    //A2_BG    A2_PILOT_ON_AMP
    {0x01 , 0x00 } ,    //A2_BG    A2_PILOT_OFF_AMP

    {0x01 , 0x50 } ,    //A2_DK    CARRIER1_ON_AMP
    {0x01 , 0x00 } ,    //A2_DK    CARRIER1_OFF_AMP
    {0x13 , 0x00 } ,    //A2_DK    CARRIER1_ON_NSR
    {0x7F , 0xFF } ,    //A2_DK    CARRIER1_OFF_NSR
    {0x00 , 0x40 } ,    //A2_DK    CARRIER2_ON_AMP
    {0x00 , 0x30 } ,    //A2_DK    CARRIER2_OFF_AMP
    {0x11 , 0x00 } ,    //A2_DK    CARRIER2_ON_NSR
    {0x15 , 0x00 } ,    //A2_DK    CARRIER2_OFF_NSR
    {0x02 , 0x80 } ,    //A2_DK    A2_PILOT_ON_AMP
    {0x01 , 0x00 } ,    //A2_DK    A2_PILOT_OFF_AMP

    {0x01 , 0x50 } ,    //FM_I     CARRIER1_ON_AMP
    {0x01 , 0x00 } ,    //FM_I     CARRIER1_OFF_AMP
    {0x0E , 0x00 } ,    //FM_I     CARRIER1_ON_NSR
    {0x7F , 0xFF } ,    //FM_I     CARRIER1_OFF_NSR

    {0x07 , 0x00 } ,    //AM       CARRIER1_ON_AMP
    {0x06 , 0x00 } ,    //AM       CARRIER1_OFF_AMP

    {0x23 , 0x00 } ,    //NICAM_BG NICAM_ON_SIGERR
    {0x3f , 0x00 } ,    //NICAM_BG NICAM_OFF_SIGERR

    {0x23 , 0x00 } ,    //NICAM_I  NICAM_ON_SIGERR
    {0x3f , 0x00 } ,    //NICAM_I  NICAM_OFF_SIGERR

    {0x00 , 0x80 } ,    //HIDEV_M  CARRIER1_ON_AMP
    {0x00 , 0x40 } ,    //HIDEV_M  CARRIER1_OFF_AMP
    {0x12 , 0x00 } ,    //HIDEV_M  CARRIER1_ON_NSR
    {0x7F , 0xFF } ,    //HIDEV_M  CARRIER1_OFF_NSR

    {0x00 , 0xB0 } ,    //HIDEV_BG CARRIER1_ON_AMP
    {0x00 , 0x60 } ,    //HIDEV_BG CARRIER1_OFF_AMP
    {0x12 , 0x00 } ,    //HIDEV_BG CARRIER1_ON_NSR
    {0x7F , 0xFF } ,    //HIDEV_BG CARRIER1_OFF_NSR

    {0x00 , 0xB0 } ,    //HIDEV_DK CARRIER1_ON_AMP
    {0x00 , 0x60 } ,    //HIDEV_DK CARRIER1_OFF_AMP
    {0x12 , 0x00 } ,    //HIDEV_DK CARRIER1_ON_NSR
    {0x7F , 0xFF } ,    //HIDEV_DK CARRIER1_OFF_NSR

    {0x00 , 0xB0 } ,    //HIDEV_I  CARRIER1_ON_AMP
    {0x00 , 0x60 } ,    //HIDEV_I  CARRIER1_OFF_AMP
    {0x12 , 0x00 } ,    //HIDEV_I  CARRIER1_ON_NSR
    {0x7F , 0xFF } ,    //HIDEV_I  CARRIER1_OFF_NSR

    {0x5A , 0x5A } ,    // Delimiter for extension threshold setting
    {0xA5 , 0xA5 } ,    // Delimiter for extension threshold setting
    {0x40 , 0x00 } ,    // A2_M PILOT_PHASE_ON_THD
    {0x50 , 0x00 } ,    // A2_M PILOT_PHASE_OFF_THD
    {0x0A , 0x00 } ,    // A2_M POLIT_MODE_VALID_RATIO
    {0x00 , 0x90 } ,    // A2_M POLIT_MODE_INVALID_RATIO
    {0xFF , 0xFF } ,    // separtor
    {0x40 , 0x00 } ,    // A2_BG PILOT_PHASE_ON_THD
    {0x50 , 0x00 } ,    // A2_BG PILOT_PHASE_OFF_THD
    {0x0A , 0x00 } ,    // A2_BG POLIT_MODE_VALID_RATIO
    {0x00 , 0x90 } ,    // A2_BG POLIT_MODE_INVALID_RATIO
    {0xFF , 0xFF } ,    // separtor
    {0x40 , 0x00 } ,    // A2_DK PILOT_PHASE_ON_THD
    {0x50 , 0x00 } ,    // A2_DK PILOT_PHASE_OFF_THD
    {0x0A , 0x00 } ,    // A2_DK POLIT_MODE_VALID_RATIO
    {0x00 , 0x90 } ,    // A2_DK POLIT_MODE_INVALID_RATIO
    {0xFF , 0xFF } ,    // separtor
};

THR_TBL_TYPE AuSifInitThreshold_PAL_VIF[] =
{
    {0x03 , 0x00 } ,    //A2_M     CARRIER1_ON_AMP
    {0x02 , 0x00 } ,    //A2_M     CARRIER1_OFF_AMP
    {0x12 , 0x00 } ,    //A2_M     CARRIER1_ON_NSR
    {0x7F , 0xFF } ,    //A2_M     CARRIER1_OFF_NSR
    {0x02 , 0x00 } ,    //A2_M     CARRIER2_ON_AMP
    {0x01 , 0x80 } ,    //A2_M     CARRIER2_OFF_AMP
    {0x11 , 0x00 } ,    //A2_M     CARRIER2_ON_NSR
    {0x15 , 0x00 } ,    //A2_M     CARRIER2_OFF_NSR
    {0x02 , 0x80 } ,    //A2_M     A2_PILOT_ON_AMP
    {0x01 , 0x00 } ,    //A2_M     A2_PILOT_OFF_AMP

    {0x03 , 0x00 } ,    //A2_BG    CARRIER1_ON_AMP
    {0x02 , 0x00 } ,    //A2_BG    CARRIER1_OFF_AMP
    {0x20 , 0x00 } ,    //A2_BG    CARRIER1_ON_NSR
    {0x7F , 0xFF } ,    //A2_BG    CARRIER1_OFF_AMP
    {0x02 , 0x00 } ,    //A2_BG    CARRIER2_ON_AMP
    {0x01 , 0x80 } ,    //A2_BG    CARRIER2_OFF_AMP
    {0x11 , 0x00 } ,    //A2_BG    CARRIER2_ON_NSR
    {0x30 , 0x00 } ,    //A2_BG    CARRIER2_OFF_NSR
    {0x02 , 0x80 } ,    //A2_BG    A2_PILOT_ON_AMP
    {0x01 , 0x00 } ,    //A2_BG    A2_PILOT_OFF_AMP

    {0x03 , 0x00 } ,    //A2_DK    CARRIER1_ON_AMP
    {0x02 , 0x00 } ,    //A2_DK    CARRIER1_OFF_AMP
    {0x13 , 0x00 } ,    //A2_DK    CARRIER1_ON_NSR
    {0x7F , 0xFF } ,    //A2_DK    CARRIER1_OFF_NSR
    {0x02 , 0x00 } ,    //A2_DK    CARRIER2_ON_AMP
    {0x01 , 0x80 } ,    //A2_DK    CARRIER2_OFF_AMP
    {0x11 , 0x00 } ,    //A2_DK    CARRIER2_ON_NSR
    {0x15 , 0x00 } ,    //A2_DK    CARRIER2_OFF_NSR
    {0x02 , 0x80 } ,    //A2_DK    A2_PILOT_ON_AMP
    {0x01 , 0x00 } ,    //A2_DK    A2_PILOT_OFF_AMP

    {0x03 , 0x00 } ,    //FM_I     CARRIER1_ON_AMP
    {0x02 , 0x00 } ,    //FM_I     CARRIER1_OFF_AMP
    {0x0E , 0x00 } ,    //FM_I     CARRIER1_ON_NSR
    {0x7F , 0xFF } ,    //FM_I     CARRIER1_OFF_NSR
    {0x00 , 0xA0 } ,    //AM       CARRIER1_ON_AMP
    {0x00 , 0x80 } ,    //AM       CARRIER1_OFF_AMP

    {0x23 , 0x00 } ,    //NICAM_BG NICAM_ON_SIGERR
    {0x3f , 0x00 } ,    //NICAM_BG NICAM_OFF_SIGERR

    {0x23 , 0x00 } ,    //NICAM_I  NICAM_ON_SIGERR
    {0x3f , 0x00 } ,    //NICAM_I  NICAM_OFF_SIGERR

    {0x02 , 0x00 } ,    //HIDEV_M  CARRIER1_ON_AMP
    {0x01 , 0x00 } ,    //HIDEV_M  CARRIER1_OFF_AMP
    {0x12 , 0x00 } ,    //HIDEV_M  CARRIER1_ON_NSR
    {0x7F , 0xFF } ,    //HIDEV_M  CARRIER1_OFF_NSR

    {0x02 , 0x00 } ,    //HIDEV_BG CARRIER1_ON_AMP
    {0x01 , 0x00 } ,    //HIDEV_BG CARRIER1_OFF_AMP
    {0x12 , 0x00 } ,    //HIDEV_BG CARRIER1_ON_NSR
    {0x7F , 0xFF } ,    //HIDEV_BG CARRIER1_OFF_NSR

    {0x02 , 0x00 } ,    //HIDEV_DK CARRIER1_ON_AMP
    {0x01 , 0x00 } ,    //HIDEV_DK CARRIER1_OFF_AMP
    {0x12 , 0x00 } ,    //HIDEV_DK CARRIER1_ON_NSR
    {0x7F , 0xFF } ,    //HIDEV_DK CARRIER1_OFF_NSR

    {0x02 , 0x00 } ,    //HIDEV_I  CARRIER1_ON_AMP
    {0x01 , 0x00 } ,    //HIDEV_I  CARRIER1_OFF_AMP
    {0x12 , 0x00 } ,    //HIDEV_I  CARRIER1_ON_NSR
    {0x7F , 0xFF } ,    //HIDEV_I  CARRIER1_OFF_NSR

    {0x5A , 0x5A } ,    // Delimiter for extension threshold setting
    {0xA5 , 0xA5 } ,    // Delimiter for extension threshold setting
    {0x40 , 0x00 } ,    // A2_M PILOT_PHASE_ON_THD
    {0x50 , 0x00 } ,    // A2_M PILOT_PHASE_OFF_THD
    {0x0A , 0x00 } ,    // A2_M POLIT_MODE_VALID_RATIO
    {0x00 , 0x90 } ,    // A2_M POLIT_MODE_INVALID_RATIO
    {0xFF , 0xFF } ,    // separtor
    {0x40 , 0x00 } ,    // A2_BG PILOT_PHASE_ON_THD
    {0x50 , 0x00 } ,    // A2_BG PILOT_PHASE_OFF_THD
    {0x0A , 0x00 } ,    // A2_BG POLIT_MODE_VALID_RATIO
    {0x00 , 0x90 } ,    // A2_BG POLIT_MODE_INVALID_RATIO
    {0xFF , 0xFF } ,    // separtor
    {0x40 , 0x00 } ,    // A2_DK PILOT_PHASE_ON_THD
    {0x50 , 0x00 } ,    // A2_DK PILOT_PHASE_OFF_THD
    {0x0A , 0x00 } ,    // A2_DK POLIT_MODE_VALID_RATIO
    {0x00 , 0x90 } ,    // A2_DK POLIT_MODE_INVALID_RATIO
    {0xFF , 0xFF } ,    // separtor
};


THR_TBL_TYPE AuSifInitThreshold_BTSC[] =
{
    {0x33 , 0x33 } ,    //BTSC_MONO_ON_NSR_THRESHOLD
    {0x7f , 0xff } ,    //BTSC_MONO_OFF_NSR_THRESHOLD
    {0x14 , 0x00 } ,    //BTSC_PILOT_ON_AMPLITUDE_THRESHOLD
    {0x10 , 0x00 } ,    //BTSC_PILOT_OFF_AMPLITUDE_THRESHOLD
    {0x40 , 0x00 } ,    //BTSC_SAP_ON_NSR_THRESHOLD
    {0x7F , 0xFF } ,    //BTSC_SAP_OFF_NSR_THRESHOLD
    {0x33 , 0x33 } ,    //BTSC_STEREO_ON_THRESHOLD
    {0x7f , 0xff } ,    //BTSC_STEREO_OFF_THRESHOLD
    {0x00 , 0x40 } ,    //BTSC_SAP_ON_AMPLITUDE_THRESHOLD
    {0x00 , 0x28 } ,    //BTSC_SAP_OFF_AMPLITUDE_THRESHOLD
};

const MAPI_U16 u8Volume[MAPI_AUDIO_VOLUME_ARRAY_NUMBER] =             // UI:  Mute
//           Volume  Table     (High Byte : Integer part  ; Low Byte : Fraction part)
{
    //   1       2       3       4       5       6       7       8       9       10
    0x7F00, //  00
    0x4700, 0x4400, 0x4100, 0x3E00, 0x3C00, 0x3A00, 0x3800, 0x3600, 0x3400, 0x3200, //  10
    0x3000, 0x2E00, 0x2D00, 0x2C00, 0x2B00, 0x2A00, 0x2900, 0x2800, 0x2700, 0x2600, //  20
    0x2500, 0x2400, 0x2300, 0x2200, 0x2100, 0x2000, 0x1F00, 0x1E04, 0x1E00, 0x1D04, //  30
    0x1D00, 0x1C04, 0x1C00, 0x1B04, 0x1B00, 0x1A04, 0x1A00, 0x1904, 0x1900, 0x1804, //  40
    0x1800, 0x1704, 0x1700, 0x1604, 0x1600, 0x1504, 0x1502, 0x1500, 0x1406, 0x1404, //  50
    0x1402, 0x1400, 0x1306, 0x1304, 0x1302, 0x1300, 0x1206, 0x1204, 0x1202, 0x1200, //  60
    0x1106, 0x1104, 0x1102, 0x1100, 0x1006, 0x1004, 0x1002, 0x1000, 0x0F07, 0x0F06, //  70
    0x0F05, 0x0F04, 0x0F03, 0x0F02, 0x0F01, 0x0F00, 0x0E07, 0x0E06, 0x0E05, 0x0E04, //  80
    0x0E03, 0x0E02, 0x0E01, 0x0E00, 0x0D07, 0x0D06, 0x0D05, 0x0D04, 0x0D03, 0x0D02, //  90
    0x0D01, 0x0D00, 0x0C07, 0x0C06, 0x0C05, 0x0C04, 0x0C03, 0x0C02, 0x0C01, 0x0C00  //  100
};

const MAPI_U16 u8Volume_HP[MAPI_AUDIO_VOLUME_ARRAY_NUMBER] =			  // UI:  Mute      //keith : sync SVN 11654 |
//			 Volume  Table	   (High Byte : Integer part  ; Low Byte : Fraction part)
{
    //   1       2       3       4       5       6       7       8       9       10
    0x7F00, //  00
    0x2D00, 0x2A00, 0x2600, 0x2000, 0x1B00, 0x1804, 0x1701, 0x1600, 0x1500, 0x1400, //  10
    0x1302, 0x1204, 0x1106, 0x1101, 0x1004, 0x0F07, 0x0F03, 0x0E07, 0x0E04, 0x0E00, //  20
    0x0D05, 0x0D02, 0x0C06, 0x0C04, 0x0C00, 0x0B06, 0x0B03, 0x0B01, 0x0A06, 0x0A04, //  30
    0x0A02, 0x0A00, 0x0906, 0x0904, 0x0902, 0x0900, 0x0806, 0x0805, 0x0804, 0x0803, //  40
    0x0802, 0x0801, 0x0800, 0x0707, 0x0706, 0x0705, 0x0704, 0x0703, 0x0702, 0x0701, //  50
    0x0700, 0x0607, 0x0606, 0x0605, 0x0604, 0x0603, 0x0602, 0x0601, 0x0600, 0x0507, //  60
    0x0506, 0x0505, 0x0504, 0x0503, 0x0502, 0x0501, 0x0500, 0x0407, 0x0406, 0x0405, //  70
    0x0404, 0x0403, 0x0402, 0x0401, 0x0400, 0x0307, 0x0306, 0x0305, 0x0304, 0x0303, //  80
    0x0302, 0x0301, 0x0300, 0x0207, 0x0206, 0x0205, 0x0204, 0x0203, 0x0202, 0x0201, //  90
    0x0200, 0x0107, 0x0106, 0x0105, 0x0104, 0x0103, 0x0102, 0x0101, 0x0100, 0x0100,  //  100
};

const MAPI_U16 u8KTVVolume[MAPI_AUDIO_VOLUME_ARRAY_NUMBER] =             // UI:  Mute
//           Volume  Table     (High Byte : Integer part  ; Low Byte : Fraction part)
{
    //   1       2       3       4       5       6       7       8       9       10
    0x7F00, //  00
    0x1402, 0x1400, 0x1306, 0x1304, 0x1302, 0x1300, 0x1206, 0x1204, 0x1202, 0x1200, //  10
    0x1106, 0x1104, 0x1102, 0x1100, 0x1006, 0x1004, 0x1002, 0x1000, 0x0F07, 0x0F06, //  20
    0x0F05, 0x0F04, 0x0F03, 0x0F02, 0x0F01, 0x0F00, 0x0E07, 0x0E06, 0x0E05, 0x0E04, //  30
    0x0E03, 0x0E02, 0x0E01, 0x0E00, 0x0D07, 0x0D06, 0x0D05, 0x0D04, 0x0D03, 0x0D02, //  40
    0x0D01, 0x0D00, 0x0C07, 0x0C06, 0x0C05, 0x0C04, 0x0C03, 0x0C02, 0x0C01, 0xC000, //  50
    0x0BC2, 0x0BA0, 0x0B76, 0x0B44, 0x0B12, 0x0AC0, 0x0A96, 0x0A64, 0x0A32, 0x0A00, //  60
    0x09C6, 0x098A, 0x094A, 0x0900, 0x08E6, 0x0874, 0x0862, 0x0830, 0x0817, 0x0806, //  70
    0x07C5, 0x0784, 0x0713, 0x06C2, 0x0681, 0x0620, 0x05E7, 0x0586, 0x0535, 0x0504, //  80
    0x04C3, 0x0482, 0x0411, 0x03C0, 0x0387, 0x0320, 0x02E5, 0x0284, 0x0233, 0x0202, //  90
    0x020A, 0x01FA, 0x01BA, 0x0102, 0x00EB, 0x007A, 0x005B, 0x0030, 0x0010, 0x0000  //  100
};

const MAPI_U16 u8MicVolume[MAPI_AUDIO_VOLUME_ARRAY_NUMBER] =             // UI:  Mute
//           Volume  Table     (High Byte : Integer part  ; Low Byte : Fraction part)
{
    //   1       2       3       4       5       6       7       8       9       10
    0x7F00, //  00
    0x3000, 0x2E00, 0x2D00, 0x2C00, 0x2B00, 0x2A00, 0x2900, 0x2800, 0x2700, 0x2600, //  10
    0x2500, 0x2400, 0x2300, 0x2200, 0x2100, 0x2000, 0x1F00, 0x1E04, 0x1E00, 0x1D04, //  20
    0x1D00, 0x1C04, 0x1C00, 0x1B04, 0x1B00, 0x1A04, 0x1A00, 0x1904, 0x1900, 0x1804, //  30
    0x1800, 0x1704, 0x1700, 0x1604, 0x1600, 0x1504, 0x1502, 0x1500, 0x1406, 0x1404, //  40
    0x1402, 0x1400, 0x1306, 0x1304, 0x1302, 0x1300, 0x1206, 0x1204, 0x1202, 0x1200, //  50
    0x11E6, 0x11D4, 0x11C2, 0x11B0, 0x1196, 0x1174, 0x1152, 0x1145, 0x1127, 0x1116, //  60
    0x1106, 0x1104, 0x1102, 0x1100, 0x1006, 0x1004, 0x1002, 0x1000, 0x0F07, 0x0F06, //  70
    0x0F05, 0x0F04, 0x0F03, 0x0F02, 0x0F01, 0x0F00, 0x0E07, 0x0E06, 0x0E05, 0x0E04, //  80
    0x0E03, 0x0E02, 0x0E01, 0x0E00, 0x0D07, 0x0D06, 0x0D05, 0x0D04, 0x0D03, 0x0D02, //  90
    0x0D01, 0x0D00, 0x0C07, 0x0C06, 0x0C05, 0x0C04, 0x0C03, 0x0C02, 0x0C01, 0x0C00  //  100
};

const MAPI_U16 u8Mp3Volume[MAPI_AUDIO_VOLUME_ARRAY_NUMBER] =             // UI:  Mute
//           Volume  Table     (High Byte : Integer part  ; Low Byte : Fraction part)
{
    //   1       2       3       4       5       6       7       8       9       10
    0x7F00, //  00
    0x4700, 0x4400, 0x4100, 0x3E00, 0x3C00, 0x3A00, 0x3800, 0x3600, 0x3400, 0x3200, //  10
    0x3000, 0x2E00, 0x2D00, 0x2C00, 0x2B00, 0x2A00, 0x2900, 0x2800, 0x2700, 0x2600, //  20
    0x2500, 0x2400, 0x2300, 0x2200, 0x2100, 0x2000, 0x1F00, 0x1EA4, 0x1E50, 0x1DE0, //  30
    0x1DC0, 0x1DA0, 0x1D80, 0x1D60, 0x1D40, 0x1D20, 0x1CF0, 0x1CD0, 0x1CB0, 0x1CA0, //  40
    0x1C90, 0x1C80, 0x1C70, 0x1C60, 0x1C50, 0x1C40, 0x1C30, 0x1C20, 0x1C10, 0x1C00, //  50
    0x1BC2, 0x1B90, 0x1B66, 0x1B34, 0x1B02, 0x1AC0, 0x1A96, 0x1A64, 0x1A32, 0x1A00, //  60
    0x19C6, 0x19A6, 0x1986, 0x1966, 0x1946, 0x1926, 0x18F6, 0x18D6, 0x18C6, 0x18A6, //  70
    0x1876, 0x1856, 0x1836, 0x1816, 0x17F6, 0x17D6, 0x17B6, 0x1796, 0x1775, 0x1736, //  80
    0x1702, 0x16E7, 0x16CC, 0x16B1, 0x1696, 0x1678, 0x1660, 0x1645, 0x162A, 0x160F, //  90
    0x1602, 0x15D0, 0x15A7, 0x1576, 0x1555, 0x1544, 0x1533, 0x1522, 0x1510, 0x1500  //  100
};

// Parameters table for dbx-tv
MS_U32 APP_dbx_TotSonDM[1][95] = {
// Total Sonics TOTSON_STANDARD_TABLE
{
0x000001, 0x000064, 0xFFF0F4, 0x000023, 0x000064, 0x000BB9, 0x008000, 0x0010EF,
0x000000, 0x000000, 0x001400, 0xFFD800, 0xFFE200, 0x0006C0, 0x000001, 0xFF8493,
0x00275E, 0x000000, 0x000000, 0x000DA7, 0x008840, 0x000074, 0x00048D, 0xFFF0F4,
0x000023, 0x000064, 0x000BB9, 0x008000, 0x000000, 0x000000, 0x000000, 0x001400,
0xFFD800, 0xFFE200, 0x000000, 0x000001, 0xFF8651, 0x000DA7, 0x000000, 0x000000,
0x000DA7, 0x008840, 0x000074, 0x00048D, 0x000001, 0x0003E8, 0xFFC400, 0x000000,
0x000000, 0x000001, 0x000000, 0xFFFFFF, 0x006400, 0x000003, 0x000001, 0x000000,
0x000500, 0x000000, 0x000000, 0x000000, 0x000000, 0x000001, 0x0000C8, 0x000000,
0x000229, 0x000000, 0x000000, 0x0000B4, 0x0000B4, 0x000023, 0x0026F4, 0x0026F4,
0x000000, 0x00045E, 0x000000, 0xFFFC00, 0x000000, 0x000000, 0x000007, 0x000001,
0x000177, 0x000300, 0x000001, 0x0000B4, 0xFFE900, 0x000004, 0x000032, 0x0001F4,
0x007800, 0x000000, 0xFF9A42, 0x014E92, 0x001B4C, 0x0002BB, 0x066285,
},

};

MS_U32  APP_dbx_TotSonPM[1][100] = {
// Total Sonics TOTSON_STANDARD_TABLE
{
0x812F56, 0x3ED372, 0x0000B2, 0x000164, 0x0000B2,
0x812F56, 0x3ED372, 0x3F6907, 0x812DF2, 0x3F6907,
0xC15168, 0x000000, 0x402FA2, 0xC18109, 0x000000,
0xC17D90, 0x000000, 0x400000, 0xC17D90, 0x000000,
0xC17D90, 0x000000, 0x400000, 0xC17D90, 0x000000,
0xC17D90, 0x000000, 0x400000, 0xC17D90, 0x000000,
0x825E9F, 0x3DAC67, 0x3ED372, 0x82591C, 0x3ED372,
0x85EBB4, 0x3A575B, 0x02D453, 0x000000, 0xFD2BAD,
0x80D010, 0x3F4FA2, 0x40585E, 0x80D010, 0x3EF744,
0x8135C2, 0x3F012B, 0x3FC83C, 0x8135C2, 0x3F38EF,
0x819CB0, 0x3EBFA3, 0x4041EA, 0x819CB0, 0x3E7DBA,
0x83A72D, 0x3D74EE, 0x3F8B20, 0x83A72D, 0x3DE9CE,
0x9CCAC7, 0x2B82EA, 0x39FD42, 0x9CCAC7, 0x3185A8,
0xDE1A60, 0x25BA27, 0x4D03E8, 0xDE1A60, 0x18B63F,
0x9096BC, 0x335763, 0x429C70, 0x9096BC, 0x30BAF3,
0x84711B, 0x3BB50B, 0x000989, 0x001313, 0x000989,
0x84711B, 0x3BB50B, 0x000989, 0x001313, 0x000989,
0x84711B, 0x3BB50B, 0x3DD0FC, 0x845E08, 0x3DD0FC,
0x84711B, 0x3BB50B, 0x3DD0FC, 0x845E08, 0x3DD0FC,
0x8221F8, 0x3DE6FA, 0x3EF140, 0x821D80, 0x3EF140,

},

};


/** Total Volume presets */
MS_U32  APP_dbx_TotVolDM[2][95] = {
// Total Volume TOTVOL_NORMAL
{
0x000001, 0x0001C2, 0xFFEB02, 0x000023, 0x000000, 0x000BB9, 0x006000, 0x006000,
0x000000, 0x000000, 0x000600, 0xFFD800, 0xFFE480, 0x0006C0, 0x000001, 0xFF8493,
0x00275E, 0x000000, 0x000000, 0x00FFFF, 0x00FFFF, 0x000074, 0x00048D, 0xFFEB02,
0x000023, 0x000000, 0x000BB9, 0x006000, 0x006000, 0x000000, 0x000000, 0x000600,
0xFFD800, 0xFFE480, 0x000000, 0x000001, 0xFF8651, 0xFFFFFF, 0x000000, 0x000000,
0x00FFFF, 0x00FFFF, 0x000074, 0x00048D, 0x000001, 0x0003E8, 0xFFC400, 0x000000,
0x000000, 0x000001, 0x000000, 0x19999A, 0x006400, 0x000003, 0x000001, 0x00000C,
0x000500, 0x000000, 0x000000, 0x000000, 0x000000, 0x000001, 0x00007D, 0x000000,
0x000229, 0x000000, 0x000000, 0x0000B4, 0x0000B4, 0x000023, 0x0026F4, 0x0026F4,
0x000000, 0x00045E, 0x000001, 0xFFFC00, 0x000000, 0x000001, 0x000007, 0x000000,
0x000177, 0x000300, 0x000001, 0x0000B4, 0xFFE900, 0x000004, 0x000032, 0x0001F4,
0x007800, 0x000000, 0xFF9A42, 0x014E92, 0x001B4C, 0x0002BB, 0x066285,
},

//Total Volume TOTVOL_NIGHT
{
0x000001, 0x0001C2, 0xFFE80A, 0x000023, 0x000000, 0x000BB9, 0x007333, 0x007333,
0x000000, 0x000000, 0x000600, 0xFFD800, 0xFFE480, 0x0006C0, 0x000001, 0xFF8493,
0x00275E, 0x000000, 0x000000, 0x00FFFF, 0x00FFFF, 0x000074, 0x00048D, 0xFFE80A,
0x000023, 0x000000, 0x000BB9, 0x007333, 0x007333, 0x000000, 0x000000, 0x000600,
0xFFD800, 0xFFE480, 0x000000, 0x000001, 0xFF8651, 0xFFFFFF, 0x000000, 0x000000,
0x00FFFF, 0x00FFFF, 0x000074, 0x00048D, 0x000001, 0x0003E8, 0xFFC400, 0x000000,
0x000000, 0x000001, 0x000000, 0x19999A, 0x006400, 0x000003, 0x000001, 0xFFF400,
0x000500, 0x000000, 0x000000, 0x000000, 0x000000, 0x000001, 0x0000FA, 0x000000,
0x000229, 0x000000, 0x000000, 0x0000B4, 0x0000B4, 0x000023, 0x0026F4, 0x0026F4,
0x000000, 0x00045E, 0x000001, 0xFFFC00, 0x000000, 0x000001, 0x000007, 0x000000,
0x000177, 0x000300, 0x000001, 0x0000B4, 0xFFE900, 0x000004, 0x000032, 0x0001F4,
0x007800, 0x000000, 0xFF9A42, 0x014E92, 0x001B4C, 0x0002BB, 0x066285,
},

};

MS_U32  APP_dbx_TotVolPM[2][100] = {
//Total Volume TOTVOL_NORMAL
{
0x85544E, 0x3AE242, 0x000DA4, 0x001B48, 0x000DA4,
0x85544E, 0x3AE242, 0x3D637D, 0x853906, 0x3D637D,
0xC15168, 0x000000, 0x402FA2, 0xC18109, 0x000000,
0xC17D90, 0x000000, 0x400000, 0xC17D90, 0x000000,
0xC17D90, 0x000000, 0x400000, 0xC17D90, 0x000000,
0xC17D90, 0x000000, 0x400000, 0xC17D90, 0x000000,
0x817B2A, 0x3E892C, 0x3F4380, 0x817900, 0x3F4380,
0x85EBB4, 0x3A575B, 0x02D453, 0x000000, 0xFD2BAD,
0x80D010, 0x3F4FA2, 0x40585E, 0x80D010, 0x3EF744,
0x8135C2, 0x3F012B, 0x3FC83C, 0x8135C2, 0x3F38EF,
0x819CB0, 0x3EBFA3, 0x4041EA, 0x819CB0, 0x3E7DBA,
0x83A72D, 0x3D74EE, 0x3F8B20, 0x83A72D, 0x3DE9CE,
0x9CCAC7, 0x2B82EA, 0x39FD42, 0x9CCAC7, 0x3185A8,
0xDE1A60, 0x25BA27, 0x4D03E8, 0xDE1A60, 0x18B63F,
0x9096BC, 0x335763, 0x429C70, 0x9096BC, 0x30BAF3,
0x84711B, 0x3BB50B, 0x000989, 0x001313, 0x000989,
0x84711B, 0x3BB50B, 0x000989, 0x001313, 0x000989,
0x84711B, 0x3BB50B, 0x3DD0FC, 0x845E08, 0x3DD0FC,
0x84711B, 0x3BB50B, 0x3DD0FC, 0x845E08, 0x3DD0FC,
0x8221F8, 0x3DE6FA, 0x3EF140, 0x821D80, 0x3EF140,

},

//Total Volume TOTVOL_NIGHT
{
0x85544E, 0x3AE242, 0x000DA4, 0x001B48, 0x000DA4,
0x85544E, 0x3AE242, 0x3D637D, 0x853906, 0x3D637D,
0xC15168, 0x000000, 0x402FA2, 0xC18109, 0x000000,
0xC17D90, 0x000000, 0x400000, 0xC17D90, 0x000000,
0xC17D90, 0x000000, 0x400000, 0xC17D90, 0x000000,
0xC17D90, 0x000000, 0x400000, 0xC17D90, 0x000000,
0x82F63B, 0x3D1AEB, 0x3E892C, 0x82EDA8, 0x3E892C,
0x85EBB4, 0x3A575B, 0x02D453, 0x000000, 0xFD2BAD,
0x80D010, 0x3F4FA2, 0x40585E, 0x80D010, 0x3EF744,
0x8135C2, 0x3F012B, 0x3FC83C, 0x8135C2, 0x3F38EF,
0x819CB0, 0x3EBFA3, 0x4041EA, 0x819CB0, 0x3E7DBA,
0x83A72D, 0x3D74EE, 0x3F8B20, 0x83A72D, 0x3DE9CE,
0x9CCAC7, 0x2B82EA, 0x39FD42, 0x9CCAC7, 0x3185A8,
0xDE1A60, 0x25BA27, 0x4D03E8, 0xDE1A60, 0x18B63F,
0x9096BC, 0x335763, 0x429C70, 0x9096BC, 0x30BAF3,
0x84711B, 0x3BB50B, 0x000989, 0x001313, 0x000989,
0x84711B, 0x3BB50B, 0x000989, 0x001313, 0x000989,
0x84711B, 0x3BB50B, 0x3DD0FC, 0x845E08, 0x3DD0FC,
0x84711B, 0x3BB50B, 0x3DD0FC, 0x845E08, 0x3DD0FC,
0x8221F8, 0x3DE6FA, 0x3EF140, 0x821D80, 0x3EF140,

},

};


/** Total Surround presets */
MS_U32  APP_dbx_TotSurDM[1][95] = {
// Total Surround TOTSUR_NORMAL
{
0x000001, 0x0001C2, 0xFFEDB9, 0x000023, 0x000000, 0x000BB9, 0x006000, 0x006000,
0x000000, 0x000000, 0x000600, 0xFFD800, 0xFFE480, 0x0006C0, 0x000001, 0xFF8493,
0x00275E, 0x000000, 0x000000, 0x00FFFF, 0x00FFFF, 0x000074, 0x00048D, 0xFFEDB9,
0x000023, 0x000000, 0x000BB9, 0x006000, 0x006000, 0x000000, 0x000000, 0x000600,
0xFFD800, 0xFFE480, 0x000000, 0x000001, 0xFF8651, 0xFFFFFF, 0x000000, 0x000000,
0x00FFFF, 0x00FFFF, 0x000074, 0x00048D, 0x000001, 0x0003E8, 0xFFC400, 0x000000,
0x000000, 0x000001, 0x000000, 0x19999A, 0x006400, 0x000003, 0x000001, 0x00000C,
0x000500, 0x000000, 0x000000, 0x000000, 0x000000, 0x000001, 0x00007D, 0x000000,
0x000870, 0x000000, 0x000000, 0x0000B4, 0x0000B4, 0x000023, 0x0026F4, 0x0026F4,
0x000000, 0x00045E, 0x000000, 0x000500, 0x000000, 0x000001, 0x000007, 0x000000,
0x000177, 0x000300, 0x000001, 0x0000B4, 0xFFE900, 0x000004, 0x000032, 0x0001F4,
0x007800, 0x000000, 0xFF9A42, 0x014E92, 0x001B4C, 0x0002BB, 0x066285,
},
};

MS_U32  APP_dbx_TotSurPM[1][100] = {
// Total Surround TOTSUR_ON
//Total Surround TOTSUR_ON
{
0x85544E, 0x3AE242, 0x000DA4, 0x001B48, 0x000DA4,
0x85544E, 0x3AE242, 0x3D637D, 0x853906, 0x3D637D,
0xC0EBD0, 0x000000, 0x40C190, 0xC1AD5F, 0x000000,
0xC17D90, 0x000000, 0x400000, 0xC17D90, 0x000000,
0xC17D90, 0x000000, 0x400000, 0xC17D90, 0x000000,
0xC17D90, 0x000000, 0x400000, 0xC17D90, 0x000000,
0x817B2A, 0x3E892C, 0x3F4380, 0x817900, 0x3F4380,
0x85EBB4, 0x3A575B, 0x02D453, 0x000000, 0xFD2BAD,
0x80D010, 0x3F4FA2, 0x40585E, 0x80D010, 0x3EF744,
0x8135C2, 0x3F012B, 0x3FC83C, 0x8135C2, 0x3F38EF,
0x819CB0, 0x3EBFA3, 0x4041EA, 0x819CB0, 0x3E7DBA,
0x83A72D, 0x3D74EE, 0x3F8B20, 0x83A72D, 0x3DE9CE,
0x9CCAC7, 0x2B82EA, 0x39FD42, 0x9CCAC7, 0x3185A8,
0xDE1A60, 0x25BA27, 0x4D03E8, 0xDE1A60, 0x18B63F,
0x9096BC, 0x335763, 0x429C70, 0x9096BC, 0x30BAF3,
0x84711B, 0x3BB50B, 0x000989, 0x001313, 0x000989,
0x84711B, 0x3BB50B, 0x000989, 0x001313, 0x000989,
0x84711B, 0x3BB50B, 0x3DD0FC, 0x845E08, 0x3DD0FC,
0x84711B, 0x3BB50B, 0x3DD0FC, 0x845E08, 0x3DD0FC,
0x8221F8, 0x3DE6FA, 0x3EF140, 0x821D80, 0x3EF140,
},


};

const AUDIO_PEQ_PARAM PEQDefaultSetting[] =
{
    {  0, 120,  10,   0, 160},    // Band0
    {  1, 120,  10,   0, 160},    // Band1
    {  2, 120,  10,   0, 160},    // Band2
    {  3, 120,  10,   0, 160},    // Band3
    {  4, 120,  10,   0, 160},    // Band4
    {  5, 120,  10,   0, 160},    // Reserved
    {  6, 120,  10,   0, 160},    // Reserved
    {  7, 120,  10,   0, 160}     // Reserved
};

const Sound_PROCBUF_TBL_ ProcessBufDefaultSetting =
{
    /* Main Speaker : main process buf must be inside this range ( 20ms ~ 250 ms ) */
    32, ///  main_procBuf_DTV  <speaker process buf in DTV input source
    32, ///  main_procBuf_ATV  <speaker process buf in ATV input source
    32, ///  main_procBuf_AV   <speaker process buf in AV input source
    32, ///  main_procBuf_HDMI <speaker process buf in HDMI input source
    32, ///  main_procBuf_DVI  <speaker process buf in DVI input source
    64, ///  main_procBuf_MM   <speaker process buf in MM input source

    /* SPDIF : spdif process buf must be inside this range ( 5ms ~ 250ms ) */
    0,  ///  spdif_procBuf_DTV  <SPDIF process buf in DTV input source
    0,  ///  spdif_procBuf_ATV  <SPDIF process buf in ATV input source
    0,  ///  spdif_procBuf_AV   <SPDIF process buf in AV input source
    0,  ///  spdif_procBuf_HDMI <SPDIF process buf in HDMI input source
    0,  ///  spdif_procBuf_DVI  <SPDIF process buf in DVI input source
    0   ///  spdif_procBuf_MM   <SPDIF process buf in MM input source
};


AUDIOSTANDARD_TYPE SDK_eAudioStandard;
AUDIOSTATUS SDK_eAudioStatus;
AUDIOMODE_TYPE SDK_eAudioMode;

MAPI_U32 mapi_audio_customer::SDK_Audio_Scart_Input_Select=0;
//-------------------------------------------------------------------------
// Local Variable
//-------------------------------------------------------------------------
MAPI_U32 mapi_audio_customer::SDK_AUD_wLimitedTimeOfMute = 0;
MAPI_U32 mapi_audio_customer::SDK_AUD_UnmuteAudioAMP = 4;
MAPI_U32 mapi_audio_customer::SDK_Audio_Monitor_Cnt = 0;
MAPI_U32 mapi_audio_customer::SDK_Audio_PowerOn_Monitor_Cnt = 20;
MAPI_S32 mapi_audio_customer::SDK_SetSoundMuteStatusMutex = 0;
static MAPI_U32 SDK_Audio_per_50ms_Cnt = 0; // for SetAudioMuteDuringLimitedTime

#if (AACENCODER_ENABLE == 1)
// for aac encoder
#define ENCODEBUFLEN 6144
void *lBaseAudioEnc;
tMp4AacEncParams lAACParam;
tEmzInt32 AACEncodeLen;
tEmzUint8 AACEncodeOut[ENCODEBUFLEN] = {0};
#endif

//-------------------------------------------------------------------------------------------------
/// @brief  \b Function  \b Name: _AIdToAudioDriverId()
/// @brief  \b Function  \b Description: Remapping MMA dec id to utopia dec id
/// @param  audio_dec_id \b : The audio decoder ID which system want to check the request
/// @return \b : return utopia Audio dec id
//-------------------------------------------------------------------------------------------------
static AUDIO_DEC_ID _AIdToAudioDriverId(MMA_AUDIO_DEC_ID audio_dec_id)
{
    AUDIO_DEC_ID dec_id = AU_DEC_INVALID;

    switch ( audio_dec_id )
    {
        case AUDIO_DEC_ID1:
            dec_id = AU_DEC_ID1;
            break;

        case AUDIO_DEC_ID2:
            dec_id = AU_DEC_ID2;
            break;

        case AUDIO_DEC_ID3:
            dec_id = AU_DEC_ID3;
            break;

        case AUDIO_DEC_INVALID:
            dec_id = AU_DEC_INVALID;
            break;

        default:
            printf("%s: Err! AID is out of Range\n", __FUNCTION__);
            break;
    }

    return dec_id;
}

static AUDIO_DEVICE_TYPE  _SDKAudioDeviceTypeToDriverAudioDeviceType(SDK_AUDIO_CAPTURE_DEVICE_TYPE eAudioDeviceType)
{
    AUDIO_DEVICE_TYPE enRetDriverAudioDeviceType = E_DEVICE0;

    switch (eAudioDeviceType)
    {
        case CAPTURE_DEVICE_TYPE_DEVICE0:
            enRetDriverAudioDeviceType = E_DEVICE0;
            break;

        case CAPTURE_DEVICE_TYPE_DEVICE1:
            enRetDriverAudioDeviceType = E_DEVICE1;
            break;

        case CAPTURE_DEVICE_TYPE_DEVICE2:
            enRetDriverAudioDeviceType = E_DEVICE2;
            break;

        case CAPTURE_DEVICE_TYPE_DEVICE3:
            enRetDriverAudioDeviceType = E_DEVICE3;
            break;

        case CAPTURE_DEVICE_TYPE_DEVICE4:
            enRetDriverAudioDeviceType = E_DEVICE4;
            break;

        case CAPTURE_DEVICE_TYPE_DEVICE5:
            enRetDriverAudioDeviceType = E_DEVICE5;
            break;

        default:
            ASSERT(0);
            break;
    }

    return enRetDriverAudioDeviceType;
}

static AUDIO_CAPTURE_SOURCE_TYPE  _SDKAudioCaptureSourceToDriverAudioCaptureSource(SDK_AUDIO_CAPTURE_SOURCE eAudioCaptureSource)
{
    AUDIO_CAPTURE_SOURCE_TYPE enRetDriverAudioCaptureSource = E_CAPTURE_NULL;

    switch (eAudioCaptureSource)
    {
        case CAPTURE_SOURCE_MAIN_SOUND_:
        case CAPTURE_SOURCE_USER_DEFINE1_:
        case CAPTURE_SOURCE_USER_DEFINE2_:
            enRetDriverAudioCaptureSource = E_CAPTURE_PCM_SE;
            break;

        case CAPTURE_SOURCE_SUB_SOUND_:
            enRetDriverAudioCaptureSource = E_CAPTURE_CH7;

            break;

        case CAPTURE_SOURCE_MICROPHONE_SOUND_:
            enRetDriverAudioCaptureSource = E_CAPTURE_ADC;
            break;

        default:
            printf("\033[0;32m [AUDIO][%s] [%s] [%d] [Unsupported capture source(%d)!!] \033[0m \n", __FILE__, __FUNCTION__, __LINE__, eAudioCaptureSource);
            ASSERT(0);
            break;
    }

    return enRetDriverAudioCaptureSource;
}

static En_DVB_decSystemType _SDKDSPSystemTypeToDecSystemType(AUDIO_DSP_SYSTEM_ eAudioDSPSystem)
{
    En_DVB_decSystemType ret = MSAPI_AUD_DVB_INVALID;
    switch(eAudioDSPSystem)
    {
        case E_AUDIO_DSP_AC3_:
        case E_AUDIO_DSP_AC3_AD_:
        case E_AUDIO_DSP_AC3P_:
        case E_AUDIO_DSP_AC3P_AD_:
            ret = MSAPI_AUD_DVB_AC3P;
            break;

        case E_AUDIO_DSP_SIF_:
            if(IS_SBTVD_BRAZIL())
            {
                ret = MSAPI_AUD_ATV_BTSC;
            }
            else
            {
                if(mapi_syscfg_fetch::GetInstance()->get_ATVSystemType() == E_PAL_ENABLE)
                {
                    ret = MSAPI_AUD_ATV_PAL;
                }
                else if(mapi_syscfg_fetch::GetInstance()->get_ATVSystemType() == E_NTSC_ENABLE)
                {
                    if(mapi_syscfg_fetch::GetInstance()->get_AUDIOSystemType() == E_BTSC_ENABLE)
                    {
                        ret = MSAPI_AUD_ATV_BTSC;
                    }
                    else if(mapi_syscfg_fetch::GetInstance()->get_AUDIOSystemType() == E_A2_ENABLE)
                    {
                        ret = MSAPI_AUD_ATV_PAL;
                    }
                }
            }

            break;
        case E_AUDIO_DSP_AACP_AD_:
        case E_AUDIO_DSP_AACP_:
            ret = MSAPI_AUD_DVB_AAC;
            break;

        case E_AUDIO_DSP_DRA_AD:
        case E_AUDIO_DSP_DRA_:
            ret = MSAPI_AUD_DVB_DRA;
            break;

        case E_AUDIO_DSP_MPEG_AD_:
        case E_AUDIO_DSP_MPEG_:
        default:
            ret = MSAPI_AUD_DVB_MPEG;
            break;
    }
    return ret;
}


mapi_audio_customer::mapi_audio_customer()
{
    SDK_pAudioSIFThrTable = NULL;
    SDK_cUI_SPDIF_Mode = MSAPI_AUD_SPDIF_PCM_;
    SDK_Audio_currSoundMode = MSAPI_AUD_DVB_SOUNDMODE_STEREO_;
    SDK_Audio_Monitor_Cnt = 0;
    SDK_AUD_wLimitedTimeOfMute = 0;
    m_bthreadActive = MAPI_TRUE;
    m_channel7Mute = MAPI_FALSE;

    MainDecID = AUDIO_DEC_INVALID;
    SubDecID = AUDIO_DEC_INVALID;

    stMainAudio.eSourceType = MAPI_INPUT_SOURCE_DTV;
    stSubAudio.eSourceType = MAPI_INPUT_SOURCE_DTV2;

    stMainAudio.eDSPSystem = E_AUDIO_DSP_MPEG_;
    stSubAudio.eDSPSystem = E_AUDIO_DSP_MPEG_;

    stMainAudio.eAudioDecID = AUDIO_DEC_ID1;
    stSubAudio.eAudioDecID = AUDIO_DEC_ID3;

    memcpy(&(PEQParam), &PEQDefaultSetting, sizeof(PEQDefaultSetting));
    memcpy(&(m_procBufTbl), &ProcessBufDefaultSetting, sizeof(ProcessBufDefaultSetting));
    memset(&m_AudioThread, 0, sizeof(pthread_t));

    //================SIF output level adjustment============================
    //
    //                       Europe model(FLUKE)       East Asia model
    //  FM                     27kHz(54%)                 50kHz(100%)
    //  NICAM                     46%                       100%
    //  AM                        54%                       ----
    //  SPDIF out lv            -12.5dB                   -12.5dB
    //  m_SifOutFmOffset     EU_SIF_OUT_FM_OFFSET      EA_SIF_OUT_FM_OFFSET
    //  m_SifOutNicamOffset  EU_SIF_OUT_NICAM_OFFSET   EA_SIF_OUT_NICAM_OFFSET
    //=======================================================================
    m_SifOutFmOffset = EA_SIF_OUT_FM_OFFSET;
    m_SifOutNicamOffset = EA_SIF_OUT_NICAM_OFFSET;

}

mapi_audio_customer::~mapi_audio_customer()
{
    if (m_bthreadActive== MAPI_TRUE)
    {
        m_bthreadActive = MAPI_FALSE;
        if(m_AudioThread != 0)
        {
            int intPTHChk = PTH_RET_CHK(pthread_join(m_AudioThread,NULL));
            if(intPTHChk != 0)
            {
                ASSERT(0);
            }
        }
    }
}

mapi_audio * mapi_audio_customer::GetInstance()
{
    if (NULL == m_pInstance)
    {
        m_pInstance = new (std::nothrow) mapi_audio_customer();
        ASSERT(m_pInstance);
    }
    return m_pInstance;
}

MAPI_BOOL mapi_audio_customer::DestroyInstanceInterface()
{
    mapi_audio::DestroyInstance();
    return MAPI_TRUE;
}

#if (STR_ENABLE == 1)
//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: Get_m_bthreadActive()
/// @brief \b Function \b Description: Get m_bthreadActive
/// @param <IN>        \b  NONE    :
/// @param <OUT>       \b : 1 is on, 0 is Off
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
MAPI_BOOL mapi_audio_customer::Get_m_bthreadActive(void)
{
    return m_bthreadActive;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: Set_m_bthreadActive()
/// @brief \b Function \b Description: Set m_bthreadActive
/// @param <IN>        \b  : 1 is on, 0 is Off
/// @param <OUT>       \b  NONE    :
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::Set_m_bthreadActive(MAPI_BOOL thread_active)
{
    m_bthreadActive = thread_active;
    return;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: Get_m_AudioThread()
/// @brief \b Function \b Description: Get m_AudioThread
/// @param <IN>        \b  NONE    :
/// @param <OUT>       \b : m_AudioThread
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
pthread_t mapi_audio_customer::Get_m_AudioThread(void)
{
    return m_AudioThread;
}
#endif

/*
void mapi_audio_customer::AUDIO_COPY_Parameter()
{
    MApi_AUDIO_COPY_Parameter(DBX_COPY_TotSonDM,APP_dbx_TotSonDM[0], (sizeof(APP_dbx_TotSonDM)/sizeof(MS_U32)));
    MApi_AUDIO_COPY_Parameter(DBX_COPY_TotSonPM,APP_dbx_TotSonPM[0], (sizeof(APP_dbx_TotSonPM)/sizeof(MS_U32)));
    MApi_AUDIO_COPY_Parameter(DBX_COPY_TotVolDM,APP_dbx_TotVolDM[0], (sizeof(APP_dbx_TotVolDM)/sizeof(MS_U32)));
    MApi_AUDIO_COPY_Parameter(DBX_COPY_TotVolPM,APP_dbx_TotVolPM[0], (sizeof(APP_dbx_TotVolPM)/sizeof(MS_U32)));
    MApi_AUDIO_COPY_Parameter(DBX_COPY_TotSurDM,APP_dbx_TotSurDM[0], (sizeof(APP_dbx_TotSurDM)/sizeof(MS_U32)));
    MApi_AUDIO_COPY_Parameter(DBX_COPY_TotSurPM,APP_dbx_TotSurPM[0], (sizeof(APP_dbx_TotSurPM)/sizeof(MS_U32)));

  return;
}
*/


//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: InitAudioSystem()
/// @brief \b Function \b Description: Audio Initialize Function
/// @param  <IN>        \b NONE
/// @return <OUT>       \b NONE
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::InitAudioSystem()
{
    AUDIO_INIT_INFO SystemInfo;
    AUDIO_OUT_INFO OutputInfo;
    AUDIO_PATH_INFO PathInfo;

    const AudioOutputType_t* const p_AudioOutput = mapi_syscfg_fetch::GetInstance()->GetAudioOutputTypeInfo();
    const AudioPath_t* const p_AudioPath = mapi_syscfg_fetch::GetInstance()->GetAudioPathInfo();

    SystemInfo.miu = 0;
    MMapInfo_t* pMMap = NULL;
    m_AudioMuteByHotKey = 0;

    //======================================
    // Set system info
    //======================================
    if(mapi_syscfg_fetch::GetInstance()->get_ATVSystemType() == E_PAL_ENABLE)
    {
        SystemInfo.tv_system = TV_PAL;

        if(ISAUDIOSIF_EN())
            SDK_pAudioSIFThrTable = (ST_THR_TBL_TYPE_ *)&AuSifInitThreshold_PAL_SIF[0];
        else
            SDK_pAudioSIFThrTable = (ST_THR_TBL_TYPE_ *)&AuSifInitThreshold_PAL_VIF[0];
    }
    else if(mapi_syscfg_fetch::GetInstance()->get_ATVSystemType() == E_NTSC_ENABLE)
    {
        SystemInfo.tv_system = TV_NTSC;

        SDK_pAudioSIFThrTable = (ST_THR_TBL_TYPE_ *)&AuSifInitThreshold_BTSC[0];
    }
    else
    {
        SystemInfo.tv_system = TV_CHINA;
    }

    if(ISATSC())
    {
        SystemInfo.dtv_system = 1;  // For ATSC
    }
    else
    {
        SystemInfo.dtv_system = 0;  // For DVB
    }

    if(mapi_syscfg_fetch::GetInstance()->get_AUDIOSystemType() == E_BTSC_ENABLE)
    {
        SystemInfo.au_system_sel = AUDIO_SYSTEM_BTSC;
    }
    else if(mapi_syscfg_fetch::GetInstance()->get_AUDIOSystemType() == E_A2_ENABLE)
    {
        SystemInfo.au_system_sel = AUDIO_SYSTEM_A2;
    }
    else
    {
        SystemInfo.au_system_sel = AUDIO_SYSTEM_EIAJ;
    }

    MApi_AUDIO_SetSystemInfo(&SystemInfo);

    //======================================
    // Set output info
    //======================================

    OutputInfo.SpeakerOut = (AUDIO_OUTPUT_TYPE)_u32OutputToAudioOutputType(p_AudioOutput[MAPI_AUDIO_OUTPUT_MAIN_SPEAKER].u32Output);
    OutputInfo.HpOut = (AUDIO_OUTPUT_TYPE)_u32OutputToAudioOutputType(p_AudioOutput[MAPI_AUDIO_OUTPUT_HP].u32Output);
    OutputInfo.ScartOut = (AUDIO_OUTPUT_TYPE)_u32OutputToAudioOutputType(p_AudioOutput[MAPI_AUDIO_OUTPUT_SIFOUT].u32Output);
    OutputInfo.MonitorOut = (AUDIO_OUTPUT_TYPE)_u32OutputToAudioOutputType(p_AudioOutput[MAPI_AUDIO_OUTPUT_LINEOUT].u32Output);
    MApi_AUDIO_SetOutputInfo(&OutputInfo);

    //======================================
    // Set path info
    //======================================

    PathInfo.SpeakerOut = (AUDIO_PATH_TYPE)_u32PathToAudioPathType(p_AudioPath[MAPI_AUDIO_PATH_MAIN_SPEAKER].u32Path);
    PathInfo.HpOut = (AUDIO_PATH_TYPE)_u32PathToAudioPathType(p_AudioPath[MAPI_AUDIO_PATH_HP].u32Path);
    PathInfo.MonitorOut = (AUDIO_PATH_TYPE)_u32PathToAudioPathType(p_AudioPath[MAPI_AUDIO_PATH_LINEOUT].u32Path);
    PathInfo.SpdifOut = (AUDIO_PATH_TYPE)_u32PathToAudioPathType(p_AudioPath[MAPI_AUDIO_PATH_SPDIF].u32Path);
    PathInfo.ScartOut = (AUDIO_PATH_TYPE)_u32PathToAudioPathType(p_AudioPath[MAPI_AUDIO_PATH_SIFOUT].u32Path);
    MApi_AUDIO_SetPathInfo(&PathInfo);

    //======================================

    pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_MAD_DEC"));
    if(pMMap != NULL)
    {
        MAPI_U32 physicalAddr=0;

        if(pMMap->u32MiuNo == 0)
        {
            physicalAddr = pMMap->u32Addr;
        }
        else if(pMMap->u32MiuNo == 1)
        {
            physicalAddr = (pMMap->u32Addr | 0x01);  // 1 nibble bit x  for Audio Driver judge MIUx. (0x1=MIU1)
        }
        else if(pMMap->u32MiuNo == 2)
        {
            physicalAddr = (pMMap->u32Addr | 0x02);
        }

        MDrv_AUDIO_SetDspBaseAddr(DSP_DEC, 0 , physicalAddr);
    }

    pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_MAD_SE"));
    if(pMMap != NULL)
    {
        MAPI_U32 physicalAddr=0;

        if(pMMap->u32MiuNo == 0)
        {
            physicalAddr = pMMap->u32Addr;
        }
        else if(pMMap->u32MiuNo == 1)
        {
            physicalAddr = (pMMap->u32Addr | 0x01);
        }
        else if(pMMap->u32MiuNo == 2)
        {
            physicalAddr = (pMMap->u32Addr | 0x02);
        }

        MDrv_AUDIO_SetDspBaseAddr(DSP_SE, 0 ,physicalAddr);
    }

    pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_MAD_R2"));
    if(pMMap != NULL)
    {
        MAPI_U32 physicalAddr=0;

        if(pMMap->u32MiuNo == 0)
        {
            physicalAddr = pMMap->u32Addr;
        }
        else if(pMMap->u32MiuNo == 1)
        {
            physicalAddr = (pMMap->u32Addr | 0x01);
        }
        else if(pMMap->u32MiuNo == 2)
        {
            physicalAddr = (pMMap->u32Addr | 0x02);
        }

        MDrv_AUDIO_SetDspBaseAddr(DSP_ADV, 0 , physicalAddr);
    }

    // SIF,VIF mode selection ( This must be called before MApi_AUDIO_Initialize() )
    if(ISAUDIOSIF_EN())
    {
        MApi_AUDIO_SIF_SendCmd(MSAPI_AUD_SIF_CMD_SET_ADC_FROM_VIF_PATH, FALSE, NULL);
    }
    else
    {
        MApi_AUDIO_SIF_SendCmd(MSAPI_AUD_SIF_CMD_SET_ADC_FROM_VIF_PATH, TRUE, NULL);
    }

#if (HDMI_ARC_AC3_PLUS_ENABLE == 1)
    MApi_AUDIO_SetAC3PInfo(Audio_AC3P_infoType_hdmiTxBypass_enable, 1, 0);
#endif

    MApi_AUDIO_Initialize();

    if(mapi_syscfg_fetch::GetInstance()->get_ATVSystemType() == E_PAL_ENABLE)
    {
        MApi_AUDIO_SIF_SendCmd(MSAPI_AUD_SIF_CMD_DETECT_MAIN_STD_ONLY, TRUE, 0);
    }

#if (MTS_NICAM_UNSTABLE)
    g_CarrierStableCnt = 0;
    g_NICAMEnable = 1;
#endif

    // =========== Variable initialize ==========
    SDK_eAudioStandard = E_AUDIOSTANDARD_NOTSTANDARD;
    SDK_eAudioStatus = E_STATE_AUDIO_NO_CARRIER;
    SDK_eAudioMode = E_AUDIOMODE_INVALID;
    SDK_eAudioCurInputSrc = MAPI_INPUT_SOURCE_NONE;
    SDK_bIsAudioModeChanged = MAPI_FALSE;

    SDK_bMHEGApMute = MAPI_FALSE;
    SDK_bPermanentAudioMute = MAPI_FALSE;
    SDK_bMomentAudioMute = MAPI_FALSE;
    SDK_bByUserAudioMute = MAPI_FALSE;
    SDK_bBySyncAudioMute = MAPI_FALSE;
    SDK_bByVChipAudioMute = MAPI_FALSE;
    SDK_bByBlockAudioMute = MAPI_FALSE;
    SDK_bInternal1AudioMute = MAPI_FALSE;
    SDK_bInternal2AudioMute = MAPI_FALSE;
    SDK_bInternal3AudioMute = MAPI_FALSE;
    SDK_bInternal4AudioMute = MAPI_FALSE;
    SDK_bByDuringLimitedTimeAudioMute = MAPI_FALSE;
    SDK_bCIAudioMute = MAPI_FALSE;
    SDK_bByUserPcmCapture1Mute = MAPI_FALSE;
    SDK_bByUserPcmCapture2Mute = MAPI_FALSE;
#if (INPUT_SOURCE_LOCK_ENABLE == 1)
    SDK_bInputSourceLockAudioMute = MAPI_FALSE;
#endif

    SDK_SUB_bIsAudioModeChanged = MAPI_FALSE;
    SDK_SUB_bMHEGApMute = MAPI_FALSE;
    SDK_SUB_bPermanentAudioMute = MAPI_FALSE;
    SDK_SUB_bMomentAudioMute = MAPI_FALSE;
    SDK_SUB_bByUserAudioMute = MAPI_FALSE;
    SDK_SUB_bBySyncAudioMute = MAPI_FALSE;
    SDK_SUB_bByVChipAudioMute = MAPI_FALSE;
    SDK_SUB_bByBlockAudioMute = MAPI_FALSE;
    SDK_SUB_bInternal1AudioMute = MAPI_FALSE;
    SDK_SUB_bInternal2AudioMute = MAPI_FALSE;
    SDK_SUB_bInternal3AudioMute = MAPI_FALSE;
    SDK_SUB_bInternal4AudioMute = MAPI_FALSE;
    SDK_SUB_bByDuringLimitedTimeAudioMute = MAPI_FALSE;
    SDK_SUB_bCIAudioMute = MAPI_FALSE;
    SDK_SUB_bSourceSwitchAudioMute = MAPI_FALSE;
#if (INPUT_SOURCE_LOCK_ENABLE == 1)
    SDK_SUB_bInputSourceLockAudioMute = MAPI_FALSE;
#endif
#if (MSTAR_TVOS == 1)
    SDK_bAllAudioMuteCtrl = MAPI_FALSE;
#else
    SDK_bAllAudioMuteCtrl = MAPI_TRUE;
#endif
    SDK_bSourceSwitchAudioMute = MAPI_TRUE;
    SDK_bPowerOnMute = MAPI_TRUE;
    SDK_bUsrScart1AudioMute = MAPI_TRUE;
    SDK_bNullSourceMute = MAPI_FALSE;
    SDK_bByAppMute = MAPI_FALSE;

    m_dwAudioStartedTimeOfMute = 0;
    SDK_AUD_wLimitedTimeOfMute = 0;

    m_wAudioDownCountTimer = 0;
    m_cAudioVolumePercentage = 0;

    MApi_AUDIO_EnableTone(MAPI_TRUE);

    dictionary *pSystemini = iniparser_load(SYS_INI_PATH_FILENAME);
    ASSERT(pSystemini);
    char * pModelName = iniparser_getstr(pSystemini, "model:gModelName");
    ASSERT(pModelName);
    dictionary *pCustomerini = iniparser_load(pModelName);
    ASSERT(pCustomerini);

    MAPI_S8 s8Mode = iniparser_getint(pCustomerini, "AVC:AvcMode", -1);
    MAPI_S8 s8AT = iniparser_getint(pCustomerini, "AVC:AvcAT", -1);
    MAPI_S8 s8RT = iniparser_getint(pCustomerini, "AVC:AvcRT", -1);
    MAPI_S8 s8Threshold = iniparser_getint(pCustomerini, "AVC:AvcThreshold", -1);

    //printf("set AVC to %x  %x  %x  %x ! \n", s8Mode, s8AT, s8RT, s8Threshold);
    //========== For customer setting============
    MApi_AUDIO_SetAvcMode((s8Mode < 0)? DEFAULT_AVCMODE : s8Mode);                   // AVC= MStar mode
    MApi_AUDIO_SetAvcAT((s8AT < 0)? DEFAULT_AVCAT : s8AT);                     // Set AVC attack time to 1sec
    MApi_AUDIO_SetAvcRT((s8RT < 0)? DEFAULT_AVCRT : s8RT);                     // Set AVC release time to 2sec
    MApi_AUDIO_SetAvcThreshold((s8Threshold < 0)? DEFAULT_AVCTHRESHOLD : s8Threshold);           // -16 dBFS

    MApi_AUDIO_SetSurroundXA(0);
    MApi_AUDIO_SetSurroundXB(3);
    MApi_AUDIO_SetSurroundXK(0);
    MApi_AUDIO_SetSurroundLPFGain(2);

    MApi_AUDIO_EnableBalance(MAPI_TRUE);

    SND_SetBass(50);
    SND_SetTreble(50);
    SND_EnableAutoVolume(MAPI_FALSE);
    SND_EnableEQ(MAPI_TRUE);
    SND_ProcessEnable(Sound_ENABL_Type_DRC_, MAPI_FALSE);         // DRC
    SND_SetParam(Sound_SET_PARAM_Drc_Threshold_, 0x10, 0x00);   // -8 dBFS

    MDrv_AUDIO_SetPowerDownWait(MAPI_TRUE);
    MApi_AUDIO_SetDataCaptureSource(E_DEVICE0, E_CAPTURE_PCM_SE); // Set audio capture initial path
    MApi_AUDIO_SetDataCaptureSource(E_DEVICE1, E_CAPTURE_PCM_SE); // Set BT source initial path

    SetSoundMuteStatus(E_AUDIO_POWERON_MUTEON_,E_AUDIOMUTESOURCE_ACTIVESOURCE_);

    // UnMute Audio Amp (delay 200ms)
    SDK_AUD_UnmuteAudioAMP =4;
    SDK_Audio_PowerOn_Monitor_Cnt = 20; //20121204, customize code should not use


    m_bthreadActive  = MAPI_TRUE;
    memset(&m_AudioThread, 0, sizeof(pthread_t));
    AudioMonitorThread();

    m_AudioSystemInitialize = TRUE;

    //use 2nd DSP to decode HDMI non-PCM
    MApi_AUDIO_SetAC3PInfo(Audio_AC3P_infoType_hdmiAC3inSE, TRUE, 0);

    // Copy audio advanced effect (dbx) parameters from app to driver
    MApi_AUDIO_COPY_Parameter(DBX_COPY_TotSonDM,APP_dbx_TotSonDM[0], (sizeof(APP_dbx_TotSonDM)/sizeof(MS_U32)));
    MApi_AUDIO_COPY_Parameter(DBX_COPY_TotSonPM,APP_dbx_TotSonPM[0], (sizeof(APP_dbx_TotSonPM)/sizeof(MS_U32)));
    MApi_AUDIO_COPY_Parameter(DBX_COPY_TotVolDM,APP_dbx_TotVolDM[0], (sizeof(APP_dbx_TotVolDM)/sizeof(MS_U32)));
    MApi_AUDIO_COPY_Parameter(DBX_COPY_TotVolPM,APP_dbx_TotVolPM[0], (sizeof(APP_dbx_TotVolPM)/sizeof(MS_U32)));
    MApi_AUDIO_COPY_Parameter(DBX_COPY_TotSurDM,APP_dbx_TotSurDM[0], (sizeof(APP_dbx_TotSurDM)/sizeof(MS_U32)));
    MApi_AUDIO_COPY_Parameter(DBX_COPY_TotSurPM,APP_dbx_TotSurPM[0], (sizeof(APP_dbx_TotSurPM)/sizeof(MS_U32)));

    m_pGPIOAumuteOut = mapi_gpio::GetGPIO_Dev(AUMUTE_OUT);

    iniparser_freedict(pSystemini);
    iniparser_freedict(pCustomerini);
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: _GetAudioDecSource()
/// @brief \b Function \b Description: Get audio source according dec ID
/// @param <IN>        \b DecID : decoder ID,
/// @param <OUT>        \b RetAudioInputType : input path return value
/// @return <OUT>       \b TRUE: Success; FALSE:failure
//-------------------------------------------------------------------------------------------------
static MS_BOOL _GetAudioDecSource(MMA_AUDIO_DEC_ID DecID, AUDIO_INPUT_TYPE *RetAudioInputType)
{
    MS_BOOL bRet = FALSE;

    if (RetAudioInputType == NULL)
    {
        return bRet;
    }
    *RetAudioInputType = AUDIO_NULL_INPUT;

    if ((DecID == AUDIO_DEC_INVALID) || (DecID == AUDIO_DEC_MAX))
    {
        return bRet;
    }

    AUDIO_SOURCE_INFO_TYPE eSourceType = E_AUDIO_INFO_SPDIF_IN;
    AUDIO_DSP_ID eDSPId = AUDIO_DSP_ID_ALL;
    AudioDecStatus_t stAudioDecStatus;

    if (TRUE == MApi_AUDIO_GetDecodeSystem(_AIdToAudioDriverId(DecID), &stAudioDecStatus))
    {
        if (AUDIO_DSP_ID_ALL != stAudioDecStatus.eDSPId)
        {
            eDSPId = stAudioDecStatus.eDSPId;
        }
        else
        {
            return bRet;
        }

        eSourceType = stAudioDecStatus.eSourceType;
    }

    switch (eSourceType)
    {
        case E_AUDIO_INFO_DTV_IN:
        {
            if (eDSPId == AUDIO_DSP_ID_DEC)
            {
                *RetAudioInputType = AUDIO_DSP1_DVB_INPUT;
            }
            else if (eDSPId == AUDIO_DSP_ID_SND)
            {
                *RetAudioInputType = AUDIO_DSP3_DVB_INPUT;
            }
            bRet = TRUE;
        }
        break;
        case E_AUDIO_INFO_ATV_IN:
        {
            if (eDSPId == AUDIO_DSP_ID_DEC)
            {
                *RetAudioInputType = AUDIO_DSP1_SIF_INPUT;
            }
            else if (eDSPId == AUDIO_DSP_ID_SND)
            {
                *RetAudioInputType = AUDIO_DSP4_SIF_INPUT;
            }
            bRet = TRUE;
        }
        break;
        case E_AUDIO_INFO_HDMI_IN:
        {
            if (eDSPId == AUDIO_DSP_ID_DEC)
            {
                *RetAudioInputType = AUDIO_DSP1_HDMI_INPUT;
            }
            else if (eDSPId == AUDIO_DSP_ID_SND)
            {
                *RetAudioInputType = AUDIO_DSP3_HDMI_INPUT;
            }
            bRet = TRUE;
        }
        break;
        default:
        {
            bRet = FALSE;
        }
        break;
    }

    return bRet;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: InputSource_ChangeAudioSource()
/// @brief \b Function \b Description: Audio InputSource Change Setting
/// @param <IN>        \b enInputSourceType : InputSource Type
/// @return <OUT>       \b NONE
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::InputSource_ChangeAudioSource(MAPI_INPUT_SOURCE_TYPE enInputSourceType)
{
    if(SDK_eAudioCurInputSrc == enInputSourceType)
    {
        return;
    }

    SetSoundMuteStatus(E_AUDIO_NULL_SOURCE_MUTEOFF_,  E_AUDIOMUTESOURCE_ACTIVESOURCE_);
    SetSoundMuteStatus(E_AUDIO_SOURCESWITCH_MUTEON_, E_AUDIOMUTESOURCE_MAINSOURCE_);

    if(IsAudioInitDone() == FALSE)
    {
        printf("\n\033[0;31m [Warning!!] [%s] [%s()] IsAudioInitDone() == FALSE, Please Check!!  \033[0m \n", __FILE__, __FUNCTION__);
    }

    AUDIO_INPUT_TYPE eReMappingInput = AUDIO_NULL_INPUT;
    MAPI_BOOL  drcRFmode = FALSE;
    const AudioMux_t* const p_AudioInputMux = mapi_syscfg_fetch::GetInstance()->GetAudioInputMuxInfo();
//    MAPI_U32 scart_input_select = p_AudioInputMux[MAPI_AUDIO_SOURCE_ATV].u32Port;
    const AudioPath_t* const p_AudioPath = mapi_syscfg_fetch::GetInstance()->GetAudioPathInfo();
    MAPI_U8  tmp_spkr_prescale;
    MAPI_U8  tmp_main_procBuf, tmp_spdif_procBuf;

    tmp_spkr_prescale = 0;
    tmp_main_procBuf = 0;
    tmp_spdif_procBuf = 0;


    /* Down Mix mode setting */
    if(enInputSourceType == MAPI_INPUT_SOURCE_STORAGE) // in MM source, set Down Mix = LORO
    {
        MApi_AUDIO_SetAC3PInfo(Audio_AC3P_infoType_DownmixMode, (MS_U32)DOLBY_DOWNMIX_MODE_LORO, 0);
    }
    else // Others, set Down Mix = LTRT
    {
        MApi_AUDIO_SetAC3PInfo(Audio_AC3P_infoType_DownmixMode, (MS_U32)DOLBY_DOWNMIX_MODE_LTRT, 0);
    }

    CloseAudioDec(MainDecID);
    stMainAudio.eSourceType = enInputSourceType;
    stMainAudio.eProcessorType = AUDIO_PROCESSOR_MAIN;
    
    #if (MSTAR_TVOS == 1)   // Add this info before open Decode System
        stMainAudio.eMMType = MAPI_MM_OMX;
    #else
        stMainAudio.eMMType = MAPI_MM_VD;
    #endif
    
    MainDecID = OpenAudioDec(&stMainAudio);

    SDK_eAudioCurInputSrc = enInputSourceType;
    m_mainCurInputSrc = enInputSourceType;	    //Fix non-PIP model, HDMI non-PCM no sound problem

    SND_ProcessEnable(Sound_ENABL_Type_KTVEcho_, MAPI_FALSE);         // Mstar KTV Echo
    SetSoundMuteStatus(E_AUDIO_USER_SCART1_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
    switch(enInputSourceType)
    {
        case MAPI_INPUT_SOURCE_DTV:
            tmp_spkr_prescale = m_preScaleTbl.spkr_prescale_DTV;
            tmp_main_procBuf = m_procBufTbl.main_procBuf_DTV;
            tmp_spdif_procBuf = m_procBufTbl.spdif_procBuf_DTV;

            MApi_AUDIO_SetSourceInfo(E_AUDIO_INFO_DTV_IN);

            DECODER_SwitchAudioDSPSystem(E_AUDIO_DSP_MPEG_, AUDIO_PROCESSOR_MAIN);
            if (AUDIO_NULL_INPUT == (AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_DTV].u32Port))
            {
                SetSoundMuteStatus(E_AUDIO_NULL_SOURCE_MUTEON_,  E_AUDIOMUTESOURCE_ACTIVESOURCE_);
            }

            if (TRUE == _GetAudioDecSource(MainDecID, &eReMappingInput))
            {
                MApi_AUDIO_InputSwitch(eReMappingInput, E_AUDIO_GROUP_MAIN);
            }
            else
            {
                MApi_AUDIO_InputSwitch((AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_DTV].u32Port), E_AUDIO_GROUP_MAIN);
            }

            SetSoundMuteStatus(E_AUDIO_INTERNAL_3_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
            SDK_Audio_Scart_Input_Select = p_AudioInputMux[MAPI_AUDIO_SOURCE_DTV].u32Port;

            if(ISATSC())                                                                                        //Dolby ATSC Line mode
            {
                SPDIF_ChannelStatus_CTRL(SPDIF_CS_CategoryCode_, SPDIF_CS_Category_BroadCast_USA_);

                /* Dolby DRC Setting */
                drcRFmode = FALSE;
                printf("==> DTV ATSC Dolby Line Mode\n");
            }
            else
            {
                SPDIF_ChannelStatus_CTRL(SPDIF_CS_CategoryCode_, SPDIF_CS_Category_BroadCast_EU_);
                if(IS_SBTVD_BRAZIL())          //Dolby DVB Line mode
                {
                    /* Dolby DRC Setting */
                    drcRFmode = FALSE;
                    printf("==> DTV DVB Dolby Line Mode\n");
                }
                else                                                                                                 //Dolby DVB RF mode
                {
                    /* Dolby DRC Setting */
                    drcRFmode = TRUE;

                }
            }
            break;

        case MAPI_INPUT_SOURCE_ATV:
            tmp_spkr_prescale = m_preScaleTbl.spkr_prescale_ATV;
            tmp_main_procBuf = m_procBufTbl.main_procBuf_ATV;
            tmp_spdif_procBuf = m_procBufTbl.spdif_procBuf_ATV;
            MApi_AUDIO_SetSourceInfo(E_AUDIO_INFO_ATV_IN);


            if (NULL != SDK_pAudioSIFThrTable)
            {
                MApi_AUDIO_SIF_SetThreshold((THR_TBL_TYPE *)SDK_pAudioSIFThrTable);
            }

            if (AUDIO_NULL_INPUT == (AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_ATV].u32Port))
            {
                SetSoundMuteStatus(E_AUDIO_NULL_SOURCE_MUTEON_,  E_AUDIOMUTESOURCE_ACTIVESOURCE_);
            }

            if (TRUE == _GetAudioDecSource(MainDecID, &eReMappingInput))
            {
                MApi_AUDIO_InputSwitch(eReMappingInput, E_AUDIO_GROUP_MAIN);
            }
            else
            {
                MApi_AUDIO_InputSwitch((AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_ATV].u32Port), E_AUDIO_GROUP_MAIN);
            }

            DECODER_SwitchAudioDSPSystem(E_AUDIO_DSP_SIF_, AUDIO_PROCESSOR_MAIN);

            MDrv_AUDIO_TriggerSifPLL();
            SetSoundMuteStatus(E_AUDIO_INTERNAL_3_MUTEOFF_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
            SetSoundMuteStatus(E_AUDIO_BYBLOCK_MUTEOFF_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
            SetSoundMuteStatus(E_AUDIO_BYSYNC_MUTEON_, E_AUDIOMUTESOURCE_MAINSOURCE_); // Mute by Sync flag ;
            SPDIF_ChannelStatus_CTRL(SPDIF_CS_CategoryCode_, SPDIF_CS_Category_BroadCast_);
            SDK_Audio_Scart_Input_Select = p_AudioInputMux[MAPI_AUDIO_SOURCE_ATV].u32Port;
            break;

        case MAPI_INPUT_SOURCE_CVBS:
            tmp_spkr_prescale = m_preScaleTbl.spkr_prescale_AV;
            tmp_main_procBuf = m_procBufTbl.main_procBuf_AV;
            tmp_spdif_procBuf = m_procBufTbl.spdif_procBuf_AV;

            MApi_AUDIO_SetSourceInfo(E_AUDIO_INFO_ADC_IN);
            if (AUDIO_NULL_INPUT == (AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_AV].u32Port))
            {
                SetSoundMuteStatus(E_AUDIO_NULL_SOURCE_MUTEON_,  E_AUDIOMUTESOURCE_ACTIVESOURCE_);
            }
            MApi_AUDIO_InputSwitch((AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_AV].u32Port), E_AUDIO_GROUP_MAIN);
            SetSoundMuteStatus(E_AUDIO_INTERNAL_3_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
            SetSoundMuteStatus(E_AUDIO_BYBLOCK_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
            SPDIF_ChannelStatus_CTRL(SPDIF_CS_CategoryCode_, SPDIF_CS_Category_General_);
            SDK_Audio_Scart_Input_Select = p_AudioInputMux[MAPI_AUDIO_SOURCE_AV].u32Port; //CVBS1: Monitor mode
            break;

        case MAPI_INPUT_SOURCE_CVBS2:
            tmp_spkr_prescale = m_preScaleTbl.spkr_prescale_AV;
            tmp_main_procBuf = m_procBufTbl.main_procBuf_AV;
            tmp_spdif_procBuf = m_procBufTbl.spdif_procBuf_AV;

            MApi_AUDIO_SetSourceInfo(E_AUDIO_INFO_ADC_IN);
            if (AUDIO_NULL_INPUT == (AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_AV2].u32Port))
            {
                SetSoundMuteStatus(E_AUDIO_NULL_SOURCE_MUTEON_,  E_AUDIOMUTESOURCE_ACTIVESOURCE_);
            }
            MApi_AUDIO_InputSwitch((AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_AV2].u32Port), E_AUDIO_GROUP_MAIN);
            SetSoundMuteStatus(E_AUDIO_INTERNAL_3_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
            SetSoundMuteStatus(E_AUDIO_BYBLOCK_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
            SPDIF_ChannelStatus_CTRL(SPDIF_CS_CategoryCode_, SPDIF_CS_Category_General_);
            break;

        case MAPI_INPUT_SOURCE_SVIDEO:
            tmp_spkr_prescale = m_preScaleTbl.spkr_prescale_AV;
            tmp_main_procBuf = m_procBufTbl.main_procBuf_AV;
            tmp_spdif_procBuf = m_procBufTbl.spdif_procBuf_AV;

            MApi_AUDIO_SetSourceInfo(E_AUDIO_INFO_ADC_IN);
            if (AUDIO_NULL_INPUT == (AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_SV].u32Port))
            {
                SetSoundMuteStatus(E_AUDIO_NULL_SOURCE_MUTEON_,  E_AUDIOMUTESOURCE_ACTIVESOURCE_);
            }
            MApi_AUDIO_InputSwitch((AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_SV].u32Port), E_AUDIO_GROUP_MAIN);
            SetSoundMuteStatus(E_AUDIO_INTERNAL_3_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
            SetSoundMuteStatus(E_AUDIO_BYBLOCK_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
            SPDIF_ChannelStatus_CTRL(SPDIF_CS_CategoryCode_, SPDIF_CS_Category_General_);
            //scart_input_select = p_AudioInputMux[MAPI_AUDIO_SOURCE_SV].u32Port;
            break;

        case MAPI_INPUT_SOURCE_SCART:
            tmp_spkr_prescale = m_preScaleTbl.spkr_prescale_AV;
            tmp_main_procBuf = m_procBufTbl.main_procBuf_AV;
            tmp_spdif_procBuf = m_procBufTbl.spdif_procBuf_AV;

            MApi_AUDIO_SetSourceInfo(E_AUDIO_INFO_ADC_IN);
            if (AUDIO_NULL_INPUT == (AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_SCART].u32Port))
            {
                SetSoundMuteStatus(E_AUDIO_NULL_SOURCE_MUTEON_,  E_AUDIOMUTESOURCE_ACTIVESOURCE_);
            }
            MApi_AUDIO_InputSwitch((AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_SCART].u32Port), E_AUDIO_GROUP_MAIN);
            SetSoundMuteStatus(E_AUDIO_INTERNAL_3_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
            SetSoundMuteStatus(E_AUDIO_BYBLOCK_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
            SPDIF_ChannelStatus_CTRL(SPDIF_CS_CategoryCode_, SPDIF_CS_Category_General_);
            break;

        case MAPI_INPUT_SOURCE_SCART2:
            tmp_spkr_prescale = m_preScaleTbl.spkr_prescale_AV;

            MApi_AUDIO_SetSourceInfo(E_AUDIO_INFO_ADC_IN);
            if (AUDIO_NULL_INPUT == (AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_SCART2].u32Port))
            {
                SetSoundMuteStatus(E_AUDIO_NULL_SOURCE_MUTEON_,  E_AUDIOMUTESOURCE_ACTIVESOURCE_);
            }
            MApi_AUDIO_InputSwitch((AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_SCART2].u32Port), E_AUDIO_GROUP_MAIN);
            SetSoundMuteStatus(E_AUDIO_INTERNAL_3_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
            SetSoundMuteStatus(E_AUDIO_BYBLOCK_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
            SPDIF_ChannelStatus_CTRL(SPDIF_CS_CategoryCode_, SPDIF_CS_Category_General_);
            break;

        case MAPI_INPUT_SOURCE_YPBPR:
            tmp_spkr_prescale = m_preScaleTbl.spkr_prescale_AV;
            tmp_main_procBuf = m_procBufTbl.main_procBuf_AV;
            tmp_spdif_procBuf = m_procBufTbl.spdif_procBuf_AV;

            MApi_AUDIO_SetSourceInfo(E_AUDIO_INFO_ADC_IN);
            if (AUDIO_NULL_INPUT == (AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_YPBPR].u32Port))
            {
                SetSoundMuteStatus(E_AUDIO_NULL_SOURCE_MUTEON_,  E_AUDIOMUTESOURCE_ACTIVESOURCE_);
            }
            MApi_AUDIO_InputSwitch((AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_YPBPR].u32Port), E_AUDIO_GROUP_MAIN);
            SetSoundMuteStatus(E_AUDIO_INTERNAL_3_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
            SetSoundMuteStatus(E_AUDIO_BYBLOCK_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
            SPDIF_ChannelStatus_CTRL(SPDIF_CS_CategoryCode_, SPDIF_CS_Category_General_);
            break;

        case MAPI_INPUT_SOURCE_YPBPR2:
            tmp_spkr_prescale = m_preScaleTbl.spkr_prescale_AV;
            tmp_main_procBuf = m_procBufTbl.main_procBuf_AV;
            tmp_spdif_procBuf = m_procBufTbl.spdif_procBuf_AV;

            MApi_AUDIO_SetSourceInfo(E_AUDIO_INFO_ADC_IN);
            if (AUDIO_NULL_INPUT == (AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_YPBPR2].u32Port))
            {
                SetSoundMuteStatus(E_AUDIO_NULL_SOURCE_MUTEON_,  E_AUDIOMUTESOURCE_ACTIVESOURCE_);
            }
            MApi_AUDIO_InputSwitch((AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_YPBPR2].u32Port), E_AUDIO_GROUP_MAIN);
            SetSoundMuteStatus(E_AUDIO_INTERNAL_3_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
            SetSoundMuteStatus(E_AUDIO_BYBLOCK_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
            SPDIF_ChannelStatus_CTRL(SPDIF_CS_CategoryCode_, SPDIF_CS_Category_General_);
            break;

        case MAPI_INPUT_SOURCE_VGA:
            tmp_spkr_prescale = m_preScaleTbl.spkr_prescale_DVI;
            tmp_main_procBuf = m_procBufTbl.main_procBuf_DVI;
            tmp_spdif_procBuf = m_procBufTbl.spdif_procBuf_DVI;

            MApi_AUDIO_SetSourceInfo(E_AUDIO_INFO_ADC_IN);
            if (AUDIO_NULL_INPUT == (AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_PC].u32Port))
            {
                SetSoundMuteStatus(E_AUDIO_NULL_SOURCE_MUTEON_,  E_AUDIOMUTESOURCE_ACTIVESOURCE_);
            }
            MApi_AUDIO_InputSwitch((AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_PC].u32Port), E_AUDIO_GROUP_MAIN);
            SetSoundMuteStatus(E_AUDIO_INTERNAL_3_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
            SetSoundMuteStatus(E_AUDIO_BYBLOCK_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
            SPDIF_ChannelStatus_CTRL(SPDIF_CS_CategoryCode_, SPDIF_CS_Category_General_);
            break;

        case MAPI_INPUT_SOURCE_HDMI:
        case MAPI_INPUT_SOURCE_HDMI2:
        case MAPI_INPUT_SOURCE_HDMI3:
        case MAPI_INPUT_SOURCE_HDMI4:
            tmp_spkr_prescale = m_preScaleTbl.spkr_prescale_HDMI;
            tmp_main_procBuf = m_procBufTbl.main_procBuf_HDMI;
            tmp_spdif_procBuf = m_procBufTbl.spdif_procBuf_HDMI;

            SetSoundMuteStatus(E_AUDIO_INTERNAL_3_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
            MApi_AUDIO_SetSourceInfo(E_AUDIO_INFO_HDMI_IN);
            if (AUDIO_NULL_INPUT == (AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_HDMI].u32Port))
            {
                SetSoundMuteStatus(E_AUDIO_NULL_SOURCE_MUTEON_,  E_AUDIOMUTESOURCE_ACTIVESOURCE_);
            }

            if (TRUE == _GetAudioDecSource(MainDecID, &eReMappingInput))
            {
                MApi_AUDIO_InputSwitch(eReMappingInput, E_AUDIO_GROUP_MAIN);
            }
            else
            {
                MApi_AUDIO_InputSwitch((AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_HDMI].u32Port), E_AUDIO_GROUP_MAIN);
            }

            m_bf_HdmiIsRaw = MSAPI_HDMI_MODE_UNKNOWN;
            SetSoundMuteStatus(E_AUDIO_BYBLOCK_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
            SPDIF_ChannelStatus_CTRL(SPDIF_CS_CategoryCode_, SPDIF_CS_Category_General_);
            break;

        case MAPI_INPUT_SOURCE_DVI:
            //printf("\r\n Now is in DVI mode !!! Not HDMI ......");
            tmp_spkr_prescale = m_preScaleTbl.spkr_prescale_DVI;
            tmp_main_procBuf = m_procBufTbl.main_procBuf_DVI;
            tmp_spdif_procBuf = m_procBufTbl.spdif_procBuf_DVI;

            MApi_AUDIO_SetSourceInfo(E_AUDIO_INFO_ADC_IN);
            if (AUDIO_NULL_INPUT == (AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_DVI].u32Port))
            {
                SetSoundMuteStatus(E_AUDIO_NULL_SOURCE_MUTEON_,  E_AUDIOMUTESOURCE_ACTIVESOURCE_);
            }
            MApi_AUDIO_InputSwitch((AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_DVI].u32Port), E_AUDIO_GROUP_MAIN);
            SetSoundMuteStatus(E_AUDIO_INTERNAL_3_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
            SetSoundMuteStatus(E_AUDIO_BYBLOCK_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
            SPDIF_ChannelStatus_CTRL(SPDIF_CS_CategoryCode_, SPDIF_CS_Category_General_);
            break;

        case MAPI_INPUT_SOURCE_STORAGE:
            tmp_spkr_prescale = m_preScaleTbl.spkr_prescale_MM;
            tmp_main_procBuf = m_procBufTbl.main_procBuf_MM;
            if ( tmp_main_procBuf < 64 ) {
                printf("main_procBuf should be great than 64 ms in MM mode (%d)\r\n", tmp_main_procBuf);
                tmp_main_procBuf = 64;
            }
            tmp_spdif_procBuf = m_procBufTbl.spdif_procBuf_MM;

            MApi_AUDIO_SetSourceInfo(E_AUDIO_INFO_GAME_IN);
            DECODER_SwitchAudioDSPSystem(E_AUDIO_DSP_MPEG_,AUDIO_PROCESSOR_MAIN);     //don't need reload code
#if (MM_In_SEN == 1)
            if (AUDIO_NULL_INPUT == (AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_DTV2].u32Port))
            {
                SetSoundMuteStatus(E_AUDIO_NULL_SOURCE_MUTEON_,  E_AUDIOMUTESOURCE_ACTIVESOURCE_);
            }

            /*
             * A patch here!
             *
             * In TVOS, MM is running in DSP2 as default, when switching from ATV(PAL-SUM) to MM it would cause main speaker noise sound.
             * It's because audio's channel5 is connected to DSP1, and ATV is still running in DSP1.
             *
             * To avoid this issue, if PIP is disable (which means single input source),
             * we force to load MPEG to DSP1 to prevent ATV(PAL-SUM) keeping sending garbage data to channel5.
             */
            if (m_subCurInputSrc == MAPI_INPUT_SOURCE_NONE)
            {
                DECODER_SwitchAudioDSPSystem(E_AUDIO_DSP_MPEG_);
            }

            MApi_AUDIO_InputSwitch((AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_DTV2].u32Port), E_AUDIO_GROUP_MAIN);
#else
            if (AUDIO_NULL_INPUT == (AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_DTV].u32Port))
            {
                SetSoundMuteStatus(E_AUDIO_NULL_SOURCE_MUTEON_,  E_AUDIOMUTESOURCE_ACTIVESOURCE_);
            }
            MApi_AUDIO_InputSwitch((AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_DTV].u32Port), E_AUDIO_GROUP_MAIN);
#endif
            SetSoundMuteStatus(E_AUDIO_INTERNAL_3_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
            SetSoundMuteStatus(E_AUDIO_BYBLOCK_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
            SPDIF_ChannelStatus_CTRL(SPDIF_CS_CategoryCode_, SPDIF_CS_Category_General_);
            DECODER_SetADEnable(FALSE);//Play MM, need to disable AD, when switch to DTV => AV Monitor will enable it
            SetSoundMuteStatus(E_AUDIO_USER_SCART1_MUTEON_, E_AUDIOMUTESOURCE_MAINSOURCE_); // Play MM, need to mute Scart Out
            break;

        case MAPI_INPUT_SOURCE_KTV:
            tmp_spkr_prescale = m_preScaleTbl.spkr_prescale_MM;
            tmp_main_procBuf = m_procBufTbl.main_procBuf_MM;
            tmp_spdif_procBuf = m_procBufTbl.spdif_procBuf_MM;

            MApi_AUDIO_SetSourceInfo(E_AUDIO_INFO_KTV_IN);
            DECODER_SwitchAudioDSPSystem(E_AUDIO_DSP_MPEG_,AUDIO_PROCESSOR_MAIN);
            if (AUDIO_NULL_INPUT == (AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_KTV].u32Port))
            {
                SetSoundMuteStatus(E_AUDIO_NULL_SOURCE_MUTEON_,  E_AUDIOMUTESOURCE_ACTIVESOURCE_);
            }
            MApi_AUDIO_InputSwitch((AUDIO_INPUT_TYPE)_u32PortToAudioInputType(p_AudioInputMux[MAPI_AUDIO_SOURCE_KTV].u32Port), E_AUDIO_GROUP_MAIN);
            SetSoundMuteStatus(E_AUDIO_INTERNAL_3_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
            SetSoundMuteStatus(E_AUDIO_BYBLOCK_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
            SPDIF_ChannelStatus_CTRL(SPDIF_CS_CategoryCode_, SPDIF_CS_Category_General_);
            SND_ProcessEnable(Sound_ENABL_Type_KTVEcho_, MAPI_FALSE);         // Mstar KTV Echo
            break;

        default:
            break;
    }

    if(drcRFmode)
    {
        MApi_AUDIO_SetAC3Info(Audio_AC3_infoType_DrcMode, RF_MODE, 0);
    }
    else
    {
        MApi_AUDIO_SetAC3Info(Audio_AC3_infoType_DrcMode, LINE_MODE, 0);
    }

    if(SDK_cUI_SPDIF_Mode == MSAPI_AUD_SPDIF_PCM_)
    {
        SPDIF_SetMode(MSAPI_AUD_SPDIF_PCM_);
    }
    else
    {
        SPDIF_SetMode(MSAPI_AUD_SPDIF_NONPCM_);
    }

    if(mapi_syscfg_fetch::GetInstance()->get_AUDIOSystemType() == E_BTSC_ENABLE)
    {
        if(SDK_Audio_Scart_Input_Select == p_AudioInputMux[MAPI_AUDIO_SOURCE_ATV].u32Port)
        {
            DECODER_SetCommand(MSAPI_AUD_SIF_CMD_SET_PLAY_);
        }
    }
#if (MSTAR_TVOS == 0)
    if(SDK_Audio_Scart_Input_Select != m_prvScartInputSelect)
    {
        MApi_AUDIO_InputSwitch((AUDIO_INPUT_TYPE)_u32PortToAudioInputType(SDK_Audio_Scart_Input_Select), E_AUDIO_GROUP_SCART);
    }
    m_prvScartInputSelect = SDK_Audio_Scart_Input_Select;
#endif
    /* add Timer mute for pop issue */
    SetAudioMuteDuringLimitedTime(1000, AUDIO_PROCESSOR_MAIN); // mute 1s

    /* set prescale of speaker output */
    MApi_SND_SetParam1(Sound_SET_PARAM_PreScale, p_AudioPath[MAPI_AUDIO_PATH_MAIN_SPEAKER].u32Path, tmp_spkr_prescale);

    /* set process buf of each input */
    if((tmp_main_procBuf >= 20) && (tmp_main_procBuf < 250))
        MApi_AUDIO_SetBufferProcess(tmp_main_procBuf);

    if((tmp_spdif_procBuf >= 5) && (tmp_spdif_procBuf < 250))
        MApi_AUDIO_SetCommAudioInfo(Audio_Comm_infoType_setSpdif_BufferProcess, tmp_spdif_procBuf, 0);

   if(enInputSourceType != MAPI_INPUT_SOURCE_ATV) // Keep Mute in ATV mode until VD sync
   {
       SetSoundMuteStatus(E_AUDIO_BYSYNC_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);
   }

    //if(ISATSC())
    {
        if(enInputSourceType < MAPI_INPUT_SOURCE_CVBS || enInputSourceType > MAPI_INPUT_SOURCE_YPBPR_MAX)
        {
            SetSoundMuteStatus(E_AUDIO_SIGNAL_UNSTABLE_MUTEOFF_, E_AUDIOMUTESOURCE_ACTIVESOURCE_); //clear signal unstable mute
        }
    }

    SetSoundMuteStatus(E_AUDIO_SCAN_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_); //will be mute at MSrv_ATV_Player::DisableChannel
    SetSoundMuteStatus(E_AUDIO_SOURCESWITCH_MUTEOFF_, E_AUDIOMUTESOURCE_MAINSOURCE_);

}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: InputSource_ChangeAudioSource()
/// @brief \b Function \b Description: Audio InputSource Change Setting
/// @param <IN>        \b enInputSourceType : InputSource Type
/// @param <IN>        \b eProcessor : select processor Type to handle Main/Sub/scart sound
/// @return <OUT>       \b NONE
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::InputSource_ChangeAudioSource(MAPI_INPUT_SOURCE_TYPE enInputSourceType, MAPI_AUDIO_PROCESSOR_TYPE eProcessor)
{
    if(IsAudioInitDone() == FALSE)
    {
        printf("\n\033[0;31m [Warning!!] [%s] [%s()] IsAudioInitDone() == FALSE, Please Check!!  \033[0m \n", __FILE__, __FUNCTION__);
    }

    AUDIO_INPUT_TYPE eReMappingInput = AUDIO_NULL_INPUT;
    AUDIOMUTESOURCE_TYPE_ eMuteSource = E_AUDIOMUTESOURCE_ACTIVESOURCE_;

    if(eProcessor == AUDIO_PROCESSOR_SUB)
    {
        eMuteSource = E_AUDIOMUTESOURCE_SUBSOURCE_;
    }
    else
    {
        eMuteSource = E_AUDIOMUTESOURCE_ACTIVESOURCE_;
    }



    /* clear unnecessary mute flag */
    if(enInputSourceType < MAPI_INPUT_SOURCE_CVBS || enInputSourceType >= MAPI_INPUT_SOURCE_SVIDEO_MAX) // Keep Mute in cvbs/s-video mode until VD sync
    {
    SetSoundMuteStatus(E_AUDIO_SIGNAL_UNSTABLE_MUTEOFF_, eMuteSource); //clear signal unstable mute
    }
    SetSoundMuteStatus(E_AUDIO_SOURCESWITCH_MUTEON_, eMuteSource);

    switch(eProcessor)
    {
        case AUDIO_PROCESSOR_MAIN:
            InputSource_ChangeAudioSource(enInputSourceType);
            m_mainCurInputSrc = enInputSourceType;
            break;

        case AUDIO_PROCESSOR_SUB:
            SetSoundMuteStatus(E_AUDIO_BYSYNC_MUTEON_, eMuteSource);
            CloseAudioDec(SubDecID);
            stSubAudio.eSourceType = enInputSourceType;
            stSubAudio.eProcessorType = AUDIO_PROCESSOR_SUB;
            SubDecID = OpenAudioDec(&stSubAudio);
#if (STB_ENABLE == 0)
            if ((enInputSourceType != MAPI_INPUT_SOURCE_STORAGE) && (enInputSourceType != MAPI_INPUT_SOURCE_STORAGE2))
            {
                SetSoundMuteStatus(E_AUDIO_BYUSER_CH7_MUTEON_,eMuteSource);
            }
#endif

#if (MSTAR_TVOS == 1) // In OMX case, MM sound input from DMA reader, set CH5 input to NULL to avoid noise
            if((enInputSourceType == MAPI_INPUT_SOURCE_ATV) && (m_mainCurInputSrc == MAPI_INPUT_SOURCE_STORAGE))
            {
                MApi_AUDIO_InputSwitch(AUDIO_DSP2_DVB_INPUT, E_AUDIO_GROUP_MAIN);//morris add here!!!
            }
#endif

            if (TRUE == _GetAudioDecSource(SubDecID, &eReMappingInput))
            {
                MApi_AUDIO_InputSwitch(eReMappingInput, E_AUDIO_GROUP_SUB);
            }
            else
            {
                eReMappingInput = (AUDIO_INPUT_TYPE)_InputSourceTypeToAudioInputType(enInputSourceType);
                MApi_AUDIO_InputSwitch(eReMappingInput, E_AUDIO_GROUP_SUB);
            }

            if((enInputSourceType == MAPI_INPUT_SOURCE_HDMI)
            ||(enInputSourceType == MAPI_INPUT_SOURCE_HDMI2)
            ||(enInputSourceType == MAPI_INPUT_SOURCE_HDMI3)
            ||(enInputSourceType == MAPI_INPUT_SOURCE_HDMI4))
            {
                    m_bf_HdmiIsRaw = MSAPI_HDMI_MODE_UNKNOWN;
            }

            if((enInputSourceType == MAPI_INPUT_SOURCE_ATV)/* && (eProcessor == AUDIO_PROCESSOR_SUB)*/)
            {
                /* When Sub=ATV, need to reload SIF code & set threshold before input switch */
                DECODER_SwitchAudioDSPSystem(E_AUDIO_DSP_SIF_, AUDIO_PROCESSOR_SUB);
                if (NULL != SDK_pAudioSIFThrTable)
                {
                    MApi_AUDIO_SIF_SetThreshold((THR_TBL_TYPE *)SDK_pAudioSIFThrTable);
                }
                MDrv_AUDIO_TriggerSifPLL();
            }
            else if((enInputSourceType == MAPI_INPUT_SOURCE_DTV) && (eProcessor == AUDIO_PROCESSOR_SUB))
            {
                /* When Sub = DTV, reload MPEG as default to avoid noise */
                DECODER_SwitchAudioDSPSystem(E_AUDIO_DSP_MPEG_, AUDIO_PROCESSOR_SUB);
                MApi_AUDIO_InputSwitch(eReMappingInput, E_AUDIO_GROUP_SUB);
            }

            m_subCurInputSrc = enInputSourceType;
            if(enInputSourceType != MAPI_INPUT_SOURCE_DTV) // Clear mute status
            {
                SetSoundMuteStatus(E_AUDIO_BYBLOCK_MUTEOFF_, E_AUDIOMUTESOURCE_SUBSOURCE_);
            }
            SetSoundMuteStatus(E_AUDIO_INTERNAL_3_MUTEOFF_, E_AUDIOMUTESOURCE_SUBSOURCE_);

            // Clear all sub releated flag when PIP off
            if(enInputSourceType == MAPI_INPUT_SOURCE_NONE) // PIP off
            {
                ClearSubSourceMuteFlag();
            }

#if (MSTAR_TVOS==1)
            /* add Timer mute for pop issue */
            if(enInputSourceType != MAPI_INPUT_SOURCE_NONE)
            {
                SetAudioMuteDuringLimitedTime(1000, AUDIO_PROCESSOR_SUB); // mute 1s
            }
            m_subBfInputSrc = m_subCurInputSrc;
#endif
            break;

        case AUDIO_PROCESSOR_SCART:
#if (PIP_ENABLE == 0)
            DECODER_SwitchAudioDSPSystem(E_AUDIO_DSP_SIF_);
#endif
            break;

        default:
            break;
    }

    // Decode system new mode not work, use old mode
    if ((SubDecID == AUDIO_DEC_INVALID) && (MainDecID == AUDIO_DEC_INVALID))
    {

#if (MM_In_SEN == 1)
        if((m_mainCurInputSrc == MAPI_INPUT_SOURCE_STORAGE) ||(m_subCurInputSrc == MAPI_INPUT_SOURCE_STORAGE))
#else
        if((m_mainCurInputSrc == MAPI_INPUT_SOURCE_ATV) ||(m_subCurInputSrc == MAPI_INPUT_SOURCE_ATV))
#endif
        {
            MApi_AUDIO_SetAC3PInfo(Audio_AC3P_infoType_hdmiAC3inSE, FALSE, 0);
        }
        else
        {
            MApi_AUDIO_SetAC3PInfo(Audio_AC3P_infoType_hdmiAC3inSE, TRUE, 0);
        }
    }
   /* clear unnecessary mute flag */
   if(enInputSourceType != MAPI_INPUT_SOURCE_ATV) // Keep Mute in ATV mode until VD sync
   {
       SetSoundMuteStatus(E_AUDIO_BYSYNC_MUTEOFF_, eMuteSource);
   }

    SetSoundMuteStatus(E_AUDIO_SCAN_MUTEOFF_, eMuteSource); //will be mute at MSrv_ATV_Player::DisableChannel
    SetSoundMuteStatus(E_AUDIO_SOURCESWITCH_MUTEOFF_, eMuteSource);
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SetAbsoluteVolume()
/// @brief \b Function \b Description:
/// @param <IN>        \b uPercent: (0~100 percentage)
///                    \b uReserve: (no use now, reserved for future)
/// @param <OUT>       \b NONE    :
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::SetAbsoluteVolume(const MAPI_U8 u8Path, MAPI_U8 uPercent, const MAPI_U8 uReserve) const
{
    MAPI_U8  TransVolum_int = 0;
    MAPI_U8  TransVolum_fra = 0;

    UNUSED(uReserve);

    if(uPercent >= MAPI_AUDIO_VOLUME_ARRAY_NUMBER)
    {
        uPercent = MAPI_AUDIO_VOLUME_ARRAY_NUMBER - 1;
        printf("SetAbsoluteVolume: uPercent value overflow!!!\n");
    }

    //check if system configuration provide customized volume curve.
    const VolumeCurve_t* const curve = mapi_syscfg_fetch::GetInstance()->GetVolumeCurve();
    if ((curve != NULL) && (curve->bEnabled == 1))
    {
        //printf("SetAbsoluteVolume: curve->bEnabled\n");
        TransVolum_int = curve->u8Volume_Int[uPercent];
        TransVolum_fra = curve->u8Volume_Fra[uPercent];
    }
    else // Use default volume table.
    {
        //printf("SetAbsoluteVolume: Audio SDK Volume\n");
        TransVolum_int = (MAPI_U8)(u8Volume[uPercent] >> 8);
        TransVolum_fra = (MAPI_U8)(u8Volume[uPercent] & 0x00FF);
    }

    //printf("SetAbsoluteVolume: UI_Vol=%x\n",u8Vol1);
    //printf("SetAbsoluteVolume: Set Volume Value=%x\n", TransVolum_int);
    MApi_AUDIO_SetAbsoluteVolume(u8Path, TransVolum_int, TransVolum_fra);
}


//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: DECODER_SetADAbsoluteVolume()
/// @brief \b Function \b Description: Set DTV AD(Audio description) Volume
/// @param <IN>        \b uPercent: ( 0 ~ 100 percentage )
/// @return <OUT>       \b NONE
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::DECODER_SetADAbsoluteVolume(MAPI_U8 uPercent)
{
    MAPI_U8  TransADVolume;

    debugAudioPrint("UI OSD Volume = %d\n", uPercent);
    if(uPercent >= MAPI_AUDIO_VOLUME_ARRAY_NUMBER)
    {
        uPercent = MAPI_AUDIO_VOLUME_ARRAY_NUMBER - 1;
        printf("uPercent value overflow!!!\n");
    }

    //check if system configuration provide customized volume curve.
    const VolumeCurve_t* const pCurve = mapi_syscfg_fetch::GetInstance()->GetVolumeCurve();
    ASSERT(pCurve);
    if(pCurve->bEnabled)
    {
        TransADVolume = pCurve->u8Volume_Int[uPercent];
    }
    else
    {
        TransADVolume = (MAPI_U8)(u8Volume[uPercent] >> 8);
    }

    debugAudioPrint("UI Audio Volume = %x\n", TransADVolume);
    MApi_AUDIO_SetADAbsoluteVolume(TransADVolume);
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SetKTVAbsoluteVolume()
/// @brief \b Function \b Description:
/// @param <IN>        \b uPercent: (0~100 percentage)
///                    \b uReserve: (no use now, reserved for future)
/// @param <OUT>       \b NONE    :
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::SetKTVAbsoluteVolume(const MAPI_U8 u8Path, MAPI_U8 uPercent, const MAPI_U8 uReserve) const
{
    MAPI_U8  TransVolum_int = 0;
    MAPI_U8  TransVolum_fra = 0;

    UNUSED(uReserve);

    if(uPercent >= MAPI_AUDIO_VOLUME_ARRAY_NUMBER)
    {
        uPercent = MAPI_AUDIO_VOLUME_ARRAY_NUMBER - 1;
        printf("SetKTVAbsoluteVolume: uPercent value overflow!!!\n");
    }

    //check if system configuration provide customized volume curve.
    const VolumeCurve_t* const curve = mapi_syscfg_fetch::GetInstance()->GetVolumeCurve();
    if ((curve != NULL) && (curve->bEnabled == 1))
    {
        printf("SetKTVAbsoluteVolume: curve->bEnabled\n");
        TransVolum_int = curve->u8Volume_Int[uPercent];
        TransVolum_fra = curve->u8Volume_Fra[uPercent];
    }
    else // Use default volume table.
    {
        printf("SetKTVAbsoluteVolume: Audio SDK Volume \n source = KTV\nu8KTVVolume = %4X",u8KTVVolume[uPercent]);
        TransVolum_int = (MAPI_U8)(u8KTVVolume[uPercent] >> 8);
        TransVolum_fra = (MAPI_U8)(u8KTVVolume[uPercent] & 0x00FF);
    }

    printf("SetKTVAbsoluteVolume: UI_Vol=%d\n",uPercent);
    printf("SetKTVAbsoluteVolume: Set Volume Value=%2x\t%2x\n", TransVolum_int,TransVolum_fra);
    MApi_AUDIO_SetAbsoluteVolume(u8Path, TransVolum_int, TransVolum_fra);
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SetMixModeVolume()
/// @brief \b Function \b Description:
/// @param <IN>        \b sourceInfo: Audio Source Information Type
///                    \b VolType: Audio Mix Mode Mixer type
///                    \b uPercent: (0~100 percentage)
///                    \b uReserve: (no use now, reserved for future)
/// @param <OUT>       \b NONE    :
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::SetMixModeVolume(AUDIO_SOURCE_INFO_TYPE_ sourceInfo, AUDIO_MIX_VOL_TYPE_ VolType, MAPI_U8 uPercent, MAPI_U8 uReserve)
{
    MAPI_U8  TransVolum_int = 0;
    MAPI_U8  TransVolum_fra = 0;

    UNUSED(uReserve);

    if(uPercent >= MAPI_AUDIO_VOLUME_ARRAY_NUMBER)
    {
        uPercent = MAPI_AUDIO_VOLUME_ARRAY_NUMBER - 1;
        printf("SetMixModeVolume: uPercent value overflow!!!\n");
    }

    //check if system configuration provide customized volume curve.
    const VolumeCurve_t* const curve = mapi_syscfg_fetch::GetInstance()->GetVolumeCurve();
    if ((curve != NULL) && (curve->bEnabled == 1))
    {
        //printf("SetMixModeVolume: curve->bEnabled\n");
        TransVolum_int = curve->u8Volume_Int[uPercent];
        TransVolum_fra = curve->u8Volume_Fra[uPercent];
    }
    else // Use default volume table.
    {
        printf("SetMixModeVolume: Audio SDK Volume\n");
        switch(VolType)
        {
            case MIC_VOL_:
            {
                TransVolum_int = (MAPI_U8)(u8MicVolume[uPercent] >> 8);
                TransVolum_fra = (MAPI_U8)(u8MicVolume[uPercent] & 0x00FF);
            }
            break;
            case MP3_VOL_:
            {
                TransVolum_int = (MAPI_U8)(u8Mp3Volume[uPercent] >> 8);
                TransVolum_fra = (MAPI_U8)(u8Mp3Volume[uPercent] & 0x00FF);
            }
            break;
            default:
            {
                TransVolum_int = (MAPI_U8)(u8Volume[uPercent] >> 8);
                TransVolum_fra = (MAPI_U8)(u8Volume[uPercent] & 0x00FF);
            }
            break;
        }
    }

    //printf("SetMixModeVolume: UI_Vol=%x\n",u8Vol1);
    //printf("SetMixModeVolume: Set Mix Mode Volume Value=%x\n", TransVolum_int);
    MApi_AUDIO_SetMixModeVolume((AUDIO_SOURCE_INFO_TYPE)_SDKAudioSourceInfoTypeToDriverAudioSourceInfoType(sourceInfo), (AUDIO_MIX_VOL_TYPE)_SDKAudioMixVolTypeToDriverAudioMixVolType(VolType), TransVolum_int, TransVolum_fra);
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SIF_SetAudioStandard()
/// @brief \b Function \b Description: Set ATV audio standard
/// @param <IN>        \b eStandard_ :
/// @param <OUT>       \b NONE    :
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::SIF_SetAudioStandard(AUDIOSTANDARD_TYPE_ eStandard)
{
    SetSoundMuteStatus(E_AUDIO_INTERNAL_1_MUTEON_, CheckSourceType(MAPI_INPUT_SOURCE_ATV));
    MApi_AUDIO_SIF_SendCmd(MSAPI_AUD_SIF_STANDARD_RESET, NULL, NULL);

    MApi_AUDIO_SIF_SetStandard((AUDIOSTANDARD_TYPE)eStandard);
    SDK_eAudioStandard = (AUDIOSTANDARD_TYPE)eStandard;

#if (MTS_NICAM_UNSTABLE)
    g_NICAMEnable = 1;
    g_CarrierStableCnt = 0;
#endif

    {
        SIF_SetMtsMode(E_AUDIOMODE_MONO_); // No saving of audio type for ATV. Reset every time standard is changed
        SDK_eAudioStatus = E_STATE_AUDIO_NO_CARRIER;

        if((eStandard == E_AUDIOSTANDARD_BG_)
                || (eStandard == E_AUDIOSTANDARD_DK_)
                || (eStandard == E_AUDIOSTANDARD_M_))
        {
            debugAudioPrint("Reload MONO:0x%x", eStandard);
            if(FALSE == MApi_AUDIO_SIF_IsPALType(AU_SIF_PAL_MONO))
            {
                MApi_AUDIO_SIF_SetPALType(AU_SIF_PAL_MONO);
            }
        }
        else if((eStandard == E_AUDIOSTANDARD_BG_A2_)
                || (eStandard == E_AUDIOSTANDARD_DK1_A2_)
                || (eStandard == E_AUDIOSTANDARD_DK2_A2_)
                || (eStandard == E_AUDIOSTANDARD_DK3_A2_)
                || (eStandard == E_AUDIOSTANDARD_M_A2_))
        {
            debugAudioPrint("Reload A2:0x%x", eStandard);
            if(FALSE == MApi_AUDIO_SIF_IsPALType(AU_SIF_PAL_A2))
            {
                MApi_AUDIO_SIF_SetPALType(AU_SIF_PAL_A2);
            }
        }
        else if((eStandard == E_AUDIOSTANDARD_BG_NICAM_)
                || (eStandard == E_AUDIOSTANDARD_DK_NICAM_)
                || (eStandard == E_AUDIOSTANDARD_I_)
                || (eStandard == E_AUDIOSTANDARD_L_))
        {

            if(eStandard == E_AUDIOSTANDARD_I_)
            {
                if(FALSE == MApi_AUDIO_SIF_IsPALType(AU_SIF_PAL_MONO))
                {
                    MApi_AUDIO_SIF_SetPALType(AU_SIF_PAL_MONO);
                }
            }
            else
            {
                debugAudioPrint("Reload NICAM:0x%x", eStandard);
                if(FALSE == MApi_AUDIO_SIF_IsPALType(AU_SIF_PAL_NICAM))
                {
                    MApi_AUDIO_SIF_SetPALType(AU_SIF_PAL_NICAM);
                }
            }
        }
        else
        {
            debugAudioPrint("Error!!! Which DSP should be loaded for 0x%x audio standard", eStandard);
        }
    }

    //usleep(AU_DELAY_FOR_ENTERING_MUTE*1000);
    SetSoundMuteStatus(E_AUDIO_INTERNAL_1_MUTEOFF_, CheckSourceType(MAPI_INPUT_SOURCE_ATV));
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SIF_GetAudioStandard()
/// @brief \b Function \b Description: Get current audio standard which is saved at mapi_audio layer variable SDK_eAudioStandard.
/// @param <IN>        \b NONE    :
/// @param <OUT>       \b NONE    :
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
AUDIOSTANDARD_TYPE_ mapi_audio_customer::SIF_GetAudioStandard()
{
    //printf("SIF_GetAudioStandard=%x\n", SDK_eAudioStandard);
    return (AUDIOSTANDARD_TYPE_)SDK_eAudioStandard;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SIF_CheckAudioStandard()
/// @brief \b Function \b Description: Check the ATV signal and switch between mono, A2 and NICAM automatically
/// @param <IN>        \b NONE    :
/// @param <OUT>       \b NONE    :
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::SIF_CheckAudioStandard(void)
{
    AUDIOSTATUS eCurrentAudioStatus;

    if(TRUE != MApi_AUDIO_SIF_GetAudioStatus(&eCurrentAudioStatus))
        return;

    if(SDK_eAudioStatus != eCurrentAudioStatus)
    {
        SDK_eAudioStatus = eCurrentAudioStatus;
        usleep(5 * 1000);
        if(TRUE != MApi_AUDIO_SIF_GetAudioStatus(&eCurrentAudioStatus))
            return;

        if(SDK_eAudioStatus != eCurrentAudioStatus)         // Check twice for speed up detection, C.P.Chen 2007/12/06
        {
            SDK_eAudioStatus = eCurrentAudioStatus;
            return;
        }
    }

    if(mapi_syscfg_fetch::GetInstance()->get_ATVSystemType() == E_PAL_ENABLE)
    {
        if(SDK_eAudioStandard == E_AUDIOSTANDARD_L)         // need touch.
        {
            if(((SDK_eAudioStatus & (E_STATE_AUDIO_PRIMARY_CARRIER | E_STATE_AUDIO_NICAM)) == (E_STATE_AUDIO_PRIMARY_CARRIER | E_STATE_AUDIO_NICAM))
                    || ((SDK_eAudioStatus & E_STATE_AUDIO_PRIMARY_CARRIER) == E_STATE_AUDIO_PRIMARY_CARRIER))
            {
                SIF_CheckATVAudioMode();
            }
            return;
        }

        if((SDK_eAudioStatus & (E_STATE_AUDIO_PRIMARY_CARRIER | E_STATE_AUDIO_NICAM)) == (E_STATE_AUDIO_PRIMARY_CARRIER | E_STATE_AUDIO_NICAM))
        {
            #if (MTS_NICAM_UNSTABLE)
            if(g_NICAMEnable == 0)
            {
                return;
            }
            #endif

            if(FALSE == MApi_AUDIO_SIF_IsPALType(AU_SIF_PAL_NICAM))
            {
                switch(SDK_eAudioStandard)
                {
                    case E_AUDIOSTANDARD_BG:
                    case E_AUDIOSTANDARD_BG_A2:
                        SDK_eAudioStandard = E_AUDIOSTANDARD_BG_NICAM;
                        break;
                    case E_AUDIOSTANDARD_DK:
                    case E_AUDIOSTANDARD_DK1_A2:
                        SDK_eAudioStandard = E_AUDIOSTANDARD_DK_NICAM;
                        break;
                    default:
                        break;
                }
                MApi_AUDIO_SIF_SetStandard(SDK_eAudioStandard);
                MApi_AUDIO_SIF_SetPALType(AU_SIF_PAL_NICAM);
                usleep(50 * 1000);
            }
            SIF_CheckATVAudioMode();
        }
        else if((SDK_eAudioStatus &(E_STATE_AUDIO_SECONDARY_CARRIER | E_STATE_AUDIO_PILOT)) ==
                (E_STATE_AUDIO_SECONDARY_CARRIER | E_STATE_AUDIO_PILOT))
        {
            //Reload A2 while Pilot detected. C.P.Chen
            if(FALSE == MApi_AUDIO_SIF_IsPALType(AU_SIF_PAL_A2))
            {
                if((SDK_eAudioStatus & E_STATE_AUDIO_DK2) == E_STATE_AUDIO_DK2)
                    SDK_eAudioStandard = E_AUDIOSTANDARD_DK2_A2;
                else if((SDK_eAudioStatus & E_STATE_AUDIO_DK3) == E_STATE_AUDIO_DK3)
                    SDK_eAudioStandard = E_AUDIOSTANDARD_DK3_A2;

                MApi_AUDIO_SIF_SetStandard(SDK_eAudioStandard);
                MApi_AUDIO_SIF_SetPALType(AU_SIF_PAL_A2);

                usleep(50 * 1000);
            }

            SIF_CheckATVAudioMode();
        }
        else// if((SDK_eAudioStatus & E_STATE_AUDIO_PRIMARY_CARRIER) == E_STATE_AUDIO_PRIMARY_CARRIER)
        {
            SIF_CheckATVAudioMode();

            #if (MTS_NICAM_UNSTABLE)
            if( TRUE == MApi_AUDIO_SIF_IsPALType(AU_SIF_PAL_NICAM) )
            {
                if (g_CarrierStableCnt <= 30)
                {
                    g_CarrierStableCnt++;
                    return;
                }
                g_CarrierStableCnt = 0;
                g_NICAMEnable = 0;
            }
            #endif

            switch(SDK_eAudioStandard)
            {
                case E_AUDIOSTANDARD_BG_NICAM:
                    MApi_AUDIO_SIF_SetStandard(E_AUDIOSTANDARD_BG_A2);
                    SDK_eAudioStandard = E_AUDIOSTANDARD_BG_A2;
                    break;
                case E_AUDIOSTANDARD_DK_NICAM:
                case E_AUDIOSTANDARD_DK2_A2:
                case E_AUDIOSTANDARD_DK3_A2:
                    MApi_AUDIO_SIF_SetStandard(E_AUDIOSTANDARD_DK1_A2);
                    SDK_eAudioStandard = E_AUDIOSTANDARD_DK1_A2;
                    break;
                default:
                    break;
            }

            if(FALSE == MApi_AUDIO_SIF_IsPALType(AU_SIF_PAL_MONO))
            {
                MApi_AUDIO_SIF_SetPALType(AU_SIF_PAL_MONO);
            }
        }
    }
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SIF_CheckATVAudioMode()
/// @brief \b Function \b Description: Check the ATV signal, if advanced MTS mode is detected, set to advanced MTS mode automatically.
/// @param <IN>        \b NONE    :
/// @param <OUT>       \b NONE    :
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::SIF_CheckATVAudioMode(void)
{
    AUDIOMODE_TYPE eDetectedAudioMode;

    if( TRUE == MApi_AUDIO_SIF_IsPALType(AU_SIF_PAL_MONO) )
    {
        eDetectedAudioMode = E_AUDIOMODE_MONO;
    }
    else
    {
        eDetectedAudioMode = (AUDIOMODE_TYPE)MApi_AUDIO_SIF_GetSoundMode();
    }

    if(eDetectedAudioMode == E_AUDIOMODE_INVALID)
    {
        return;
    }

    if(SDK_eAudioMode == eDetectedAudioMode)
    {
        MApi_AUDIO_SIF_SetSoundMode(SDK_eAudioMode);
        return;
    }

    if(((SDK_eAudioMode == E_AUDIOMODE_DUAL_A) || (SDK_eAudioMode == E_AUDIOMODE_DUAL_B) || (SDK_eAudioMode == E_AUDIOMODE_DUAL_AB))
            && ((eDetectedAudioMode == E_AUDIOMODE_DUAL_A) || (eDetectedAudioMode == E_AUDIOMODE_DUAL_B) || (eDetectedAudioMode == E_AUDIOMODE_DUAL_AB)))
    {
        MApi_AUDIO_SIF_SetSoundMode(SDK_eAudioMode);
        return;
    }

    if(((SDK_eAudioMode == E_AUDIOMODE_NICAM_DUAL_A) || (SDK_eAudioMode == E_AUDIOMODE_NICAM_DUAL_B) || (SDK_eAudioMode == E_AUDIOMODE_NICAM_DUAL_AB))
            && ((eDetectedAudioMode == E_AUDIOMODE_NICAM_DUAL_A) || (eDetectedAudioMode == E_AUDIOMODE_NICAM_DUAL_B) || (eDetectedAudioMode == E_AUDIOMODE_NICAM_DUAL_AB)))
    {
        MApi_AUDIO_SIF_SetSoundMode(SDK_eAudioMode);
        return;
    }

    if((SDK_eAudioMode == E_AUDIOMODE_FORCED_MONO) && (eDetectedAudioMode != E_AUDIOMODE_DUAL_A))
    {
        MApi_AUDIO_SIF_SetSoundMode(SDK_eAudioMode);
        return;
    }

    SDK_eAudioMode = eDetectedAudioMode;

    //SetAudioMute(E_AUDIO_INTERNAL_1_MUTEON,E_AUDIOMUTESOURCE_ATV);
    MApi_AUDIO_SIF_SetSoundMode(SDK_eAudioMode);
    //MDrv_Timer_Delayms(DELAY_FOR_ENTERING_MUTE);
    //SetAudioMute(E_AUDIO_INTERNAL_1_MUTEOFF,E_AUDIOMUTESOURCE_ATV);
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SIF_SetMtsMode()
/// @brief \b Function \b Description:  Set the ATV MTS mode
/// @param <IN>        \b NONE    :
/// @param <OUT>       \b NONE    :
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::SIF_SetMtsMode(const MAPI_U8 u8SifSoundMode)
{
    SDK_eAudioMode = (AUDIOMODE_TYPE)u8SifSoundMode;

#if (MTS_NICAM_UNSTABLE)
    g_NICAMEnable = 1;
    g_CarrierStableCnt = 0;
#endif

    MApi_AUDIO_SIF_SetSoundMode(u8SifSoundMode);
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SIF_GetMtsMode()
/// @brief \b Function \b Description: Get the current ATV MTS mode
/// @param <IN>        \b NONE    :
/// @param <OUT>       \b NONE    :
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
MAPI_U8 mapi_audio_customer::SIF_GetMtsMode(void)
{
    return (MAPI_U8)SDK_eAudioMode;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: AudioMonitorThread()
/// @brief \b Function \b Description: To creat audio monitor thread
/// @param  <IN>        \b NONE
/// @return <OUT>       \b NONE
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::AudioMonitorThread(void)
{
    int intPTHChk;
    pthread_attr_t attr;
    pthread_attr_init(&attr);
    pthread_attr_setstacksize(&attr, PTHREAD_STACK_SIZE);

    intPTHChk = PTH_RET_CHK(pthread_create(&m_AudioThread, &attr, AudioMonitor, this));
    if(intPTHChk != 0)
    {
        ASSERT(0);
    }
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: AudioMonitor()
/// @brief \b Function \b Description: Audio monitor thread
/// @param  <IN>        \b NONE
/// @return <OUT>       \b NONE
//-------------------------------------------------------------------------------------------------
void *mapi_audio_customer::AudioMonitor(void *arg)
{
    mapi_audio_customer *_this   = (mapi_audio_customer*)arg;
    MAPI_BOOL  *pActive = (MAPI_BOOL *)(&(_this->m_bthreadActive));

    MAPI_U8 monitor_type = 0;

    prctl(PR_SET_NAME, (unsigned long)"AudioMonitor");
#if (STR_ENABLE == 1)
    mapi_str::AutoRegister _R;
#endif

    while(*pActive)
    {
        if(SDK_AUD_wLimitedTimeOfMute != 0)
        {
            if(SDK_AUD_wLimitedTimeOfMute > SDK_Audio_Monitor_Cnt)
                SDK_Audio_Monitor_Cnt = SDK_AUD_wLimitedTimeOfMute;

            SDK_AUD_wLimitedTimeOfMute = 0;
            monitor_type = 0x1;
        }

        if(SDK_Audio_Monitor_Cnt > 0)
        {
            SDK_Audio_Monitor_Cnt--;
            if(SDK_Audio_Monitor_Cnt == 0)
            {
                switch(monitor_type)
                {
                    case 0x1:
                        _this->SetSoundMuteStatus(E_AUDIO_DURING_LIMITED_TIME_MUTEOFF_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
                        _this->SetSoundMuteStatus(E_AUDIO_DURING_LIMITED_TIME_MUTEOFF_, E_AUDIOMUTESOURCE_SUBSOURCE_);
                        break;

                    default:
                        break;
                }
            }
        }

        if(SDK_AUD_UnmuteAudioAMP > 0)
        {
            SDK_AUD_UnmuteAudioAMP--;
            if (SDK_AUD_UnmuteAudioAMP == 0)
            {
                _this->SetSoundMute(SOUND_MUTE_AMP_, E_MUTE_OFF_);
            }
        }

        if(SDK_Audio_PowerOn_Monitor_Cnt > 0)
        {
            SDK_Audio_PowerOn_Monitor_Cnt--;
            if(SDK_Audio_PowerOn_Monitor_Cnt == 0)
            {
                _this->SetSoundMuteStatus(E_AUDIO_POWERON_MUTEOFF_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
            }
        }
#if (STR_ENABLE == 1)
        for(int i=0;i<50;i++)
        {
            if((*pActive)==FALSE)break;
            usleep(1000);
        }
#else
        usleep(50*1000);
#endif
        SDK_Audio_per_50ms_Cnt++;
    }
    return NULL;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: AUDIO_Monitor_Service()
/// @brief \b Function \b Description: Audio Auto-Recovery function used or HDMI/SPDIF relation monitor function used
/// @param  <IN>        \b NONE
/// @return <OUT>       \b NONE
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::AUDIO_Monitor_Service(void)
{
    if(m_AudioSystemInitialize)
    {
#if (STB_ENABLE == 1)
        MS_BOOL hdmi_tx_en;
        AUDIO_FS_TYPE fs_type;
        HDMI_TX_OUTPUT_TYPE outType;
        HDMITX_AUDIO_FREQUENCY hreq = HDMITX_AUDIO_FREQ_NO_SIG;
        HDMITX_AUDIO_CODING_TYPE htype = HDMITX_AUDIO_PCM;

        MApi_AUDIO_HDMI_Tx_GetStatus(&hdmi_tx_en, &fs_type, &outType);

        if( (SDK_cUI_SPDIF_Mode== MSAPI_AUD_SPDIF_NONPCM_) && (outType==HDMI_OUT_PCM) )
        {   //avoid nonPCM setting noise in boot process!!!
            HDMITx_SetMode(MSAPI_HDMI_MODE_RAW);
            MApi_AUDIO_HDMI_Tx_GetStatus(&hdmi_tx_en, &fs_type, &outType);
        }

        if(hdmi_tx_en)
        {
            if(outType == HDMI_OUT_NONPCM)
            {
                htype = HDMITX_AUDIO_NONPCM;
                switch(fs_type)
                {
                    case AUDIO_FS_32KHZ:
                        hreq = HDMITX_AUDIO_32K;
                        break;
                    case AUDIO_FS_44KHZ:
                        hreq = HDMITX_AUDIO_44K;
                        break;
                    case AUDIO_FS_176KHZ:
                        hreq = HDMITX_AUDIO_176K;
                        break;
                    case AUDIO_FS_192KHZ:
                        hreq = HDMITX_AUDIO_192K;
                        break;
                    case AUDIO_FS_48KHZ:
                    default:
                        hreq = HDMITX_AUDIO_48K;
                        break;
                }
            }
            else
            {
                htype = HDMITX_AUDIO_PCM;
                hreq = HDMITX_AUDIO_48K;                            //for PCM mode always SRC to 48K
            }

            //printf("Hdmx Tx:%x , %x\n", hreq, htype);
            MApi_HDMITx_SetAudioConfiguration(hreq, HDMITX_AUDIO_CH_2, htype);
            if(MApi_HDMITx_GetHDCPStatus() != E_HDCP_FAIL)
            {
            MApi_HDMITx_SetAudioOnOff(MAPI_TRUE);
        }
            else
            {
                MApi_HDMITx_SetAudioOnOff(MAPI_FALSE);
            }
        }
#endif

        //printf("Audio Monitor\n");
        if((SDK_eAudioCurInputSrc != MAPI_INPUT_SOURCE_ATV) && (SDK_eAudioCurInputSrc != MAPI_INPUT_SOURCE_DTV))
        {
            SIF_Monitor_Service();
        }
        MApi_Audio_SPDIF_Monitor();
        MApi_Audio_Monitor();
    }
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SetAudioMuteDuringLimitedTime()
/// @brief \b Function \b Description: Set audio mute during time limit
/// @param  <IN>        \b : Time thread per 50ms
/// @return <OUT>       \b NONE
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::SetAudioMuteDuringLimitedTime(MAPI_U32 per_50ms)
{
    MAPI_U32 mute_ms;

    mute_ms = per_50ms*50;
    SetAudioMuteDuringLimitedTime(mute_ms, AUDIO_PROCESSOR_MAIN);
}


//-------------------------------------------------------------------------------------------------
/// Set audio mute during time limit.
/// @param  mute_ms ( mute time limit(millisecond), need to > 50ms )
/// @param  eProcessorType ( to mute main channel(ch5)/sub channel(ch7))
///                         : AUDIO_PROCESSOR_MAIN,
///                         : AUDIO_PROCESSOR_SUB,
/// @return                 \b If success return MAPI_TRUE, else return MAPI_FALSE.
//-------------------------------------------------------------------------------------------------
MAPI_BOOL mapi_audio_customer::SetAudioMuteDuringLimitedTime(MAPI_U32 mute_ms, MAPI_AUDIO_PROCESSOR_TYPE eProcessorType)
{
    int remain_previous_per_50ms = m_audio_previous_per_50ms - SDK_Audio_per_50ms_Cnt;
    int mute_50ms_count = ((int)mute_ms + 49)/50;   // 0~50ms ==> mute 50ms

    if((eProcessorType != AUDIO_PROCESSOR_MAIN) && (eProcessorType != AUDIO_PROCESSOR_SUB))
    {
        printf("[Error]SetAudioMuteDuringLimitedTime: eProcessorType should be either AUDIO_PROCESSOR_MAIN or AUDIO_PROCESSOR_SUB!!!\n");
        return MAPI_FALSE;
    }

    if(eProcessorType == AUDIO_PROCESSOR_SUB)     // add timer mute for PIP's sub case
    {
        mapi_audio::SetSoundMuteStatus(E_AUDIO_DURING_LIMITED_TIME_MUTEON_, E_AUDIOMUTESOURCE_SUBSOURCE_);
    }
    else if(eProcessorType == AUDIO_PROCESSOR_MAIN)
    {
        mapi_audio::SetSoundMuteStatus(E_AUDIO_DURING_LIMITED_TIME_MUTEON_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
    }

    if(mute_50ms_count > remain_previous_per_50ms)
    {
        SDK_AUD_wLimitedTimeOfMute = mute_50ms_count;
    }
    else
    {
        SDK_AUD_wLimitedTimeOfMute = remain_previous_per_50ms;
    }

    m_audio_previous_per_50ms =  SDK_AUD_wLimitedTimeOfMute;
    SDK_Audio_per_50ms_Cnt = 0;

    return MAPI_TRUE;
}

void mapi_audio_customer::Init()
{
    if(SDK_SetSoundMuteStatusMutex == 0)
    {
        SDK_SetSoundMuteStatusMutex = MsOS_CreateMutex(E_MSOS_FIFO,
                                                         (char *)"Mutex AUDIO MuteStatus",
                                                         MSOS_PROCESS_SHARED);
        MS_ASSERT(SDK_SetSoundMuteStatusMutex > 0);
    }
    //Refine power on sequence for earphone & DAC pop noise issue
    AUDIO_PreInit();
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: DECODER_GetChannelMode()
/// @brief \b Function \b Description : This routine is used to report decoder channel mode information.
/// @retun <OUT>       \b AUDIO_DEC_ChannelMOD_Type
///                                         : AUDIO_DEC_ACMODE_NOTREADY
///                                         : AUDIO_DEC_ACMODE_DUALMONO1
///                                         : AUDIO_DEC_ACMODE_STEREO ...etc
//-------------------------------------------------------------------------------------------------
AUDIO_DEC_ChannelMOD_Type mapi_audio_customer::DECODER_GetChannelMode(void)
{
    AUDIO_DEC_ChannelMOD_Type DecChMod_Status=AUDIO_DEC_ACMODE_NOTREADY;
    AUD_CH_MODE_TYPE ChMod_Status_tmp;
    HDMI_AUDIO_MODE_ eHdmiMode;

    ChMod_Status_tmp = (AUD_CH_MODE_TYPE)MApi_AUDIO_GetCommAudioInfo(Audio_Comm_infoType_ADEC1_acmod);
   // printf("\r\n======DECODER_GetChannelMode %X ========\r\n", ChMod_Status_tmp);
    if((SDK_eAudioCurInputSrc==MAPI_INPUT_SOURCE_HDMI)||(SDK_eAudioCurInputSrc==MAPI_INPUT_SOURCE_HDMI2)
        ||(SDK_eAudioCurInputSrc==MAPI_INPUT_SOURCE_HDMI3)||(SDK_eAudioCurInputSrc==MAPI_INPUT_SOURCE_HDMI4))
    {
        eHdmiMode=HDMI_GetAudioMode();
        if(eHdmiMode==E_HDMI_PCM_)
          ChMod_Status_tmp=AUD_CH_MODE_STEREO;
    }

    switch(ChMod_Status_tmp)
    {
        case AUD_CH_MODE_STEREO:
        case AUD_CH_MODE_JOINT_STEREO:
            DecChMod_Status =AUDIO_DEC_ACMODE_STEREO;
            break;
        case AUD_CH_MODE_DUAL_MONO:
            DecChMod_Status =AUDIO_DEC_ACMODE_DUALMONO1;
            break;
        case AUD_CH_MODE_MONO:
            DecChMod_Status =AUDIO_DEC_ACMODE_MONO;
            break;
        case AUD_CH_MODE_NONE:
            DecChMod_Status =AUDIO_DEC_ACMODE_NOTREADY;
            break;
        default:
            DecChMod_Status = AUDIO_DEC_ACMODE_MULTICH;
            break;
    }

    return(DecChMod_Status);
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: _MapiPortToAudioOutportType()
/// @brief \b Function \b Description : This routine is used to transfer SDK port to Utopia output type
/// @param <IN>        \b mapiPort: SDK port
/// @retun <OUT>      \b  Utopia output type
//-------------------------------------------------------------------------------------------------
static AUDIO_OUTPORT_SOURCE_TYPE _MapiPortToAudioOutportType(MAPI_AUDIO_PROCESSOR_TYPE mapiPort)
{
    AUDIO_OUTPORT_SOURCE_TYPE enAudioOutportType = E_CONNECT_MAIN;
    switch(mapiPort)
    {
        case AUDIO_PROCESSOR_SUB:
        {
            enAudioOutportType = E_CONNECT_SUB;
        }
        break;
        case AUDIO_PROCESSOR_SCART:
        {
            enAudioOutportType = E_CONNECT_SCART;
        }
        break;
        case AUDIO_PROCESSOR_MAIN:
        default:
        {
            enAudioOutportType = E_CONNECT_MAIN;
        }
        break;
    }

    return enAudioOutportType;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: _MapiMMTypeToAudioMMType()
/// @brief \b Function \b Description : This routine is used to transfer SDK MM type to Utopia MM type
/// @param <IN>        \b mapiPort: SDK MM type
/// @retun <OUT>      \b  Utopia MM type
//-------------------------------------------------------------------------------------------------
static AUDIO_MM_TYPE _MapiMMTypeToAudioMMType(MAPI_AUDIO_MM_TYPE mapiType)
{
    AUDIO_MM_TYPE enMMType = AUDIO_MM_OMX;
    switch(mapiType)
    {
        case MAPI_MM_OMX:
        {
            enMMType = AUDIO_MM_OMX;
        }
        break;
        case MAPI_MM_VD:
        {
            enMMType = AUDIO_MM_VD;
        }
        break;
        case MAPI_MM_MAX:
        default:
        {
            enMMType = AUDIO_MM_OMX;
        }
        break;
    }

    return enMMType;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: _MapiStcSourceToAudioStcSource()
/// @brief \b Function \b Description : This routine is used to transfer SDK stc source to Utopia stc source
/// @param <IN>        \b mapiPort: SDK stc source
/// @retun <OUT>      \b  Utopia stc source
//-------------------------------------------------------------------------------------------------
static AUDIO_STC_SOURCE _MapiStcSourceToAudioStcSource(MAPI_AUDIO_STC_SOURCE mapiSource)
{
    AUDIO_STC_SOURCE enSTCSource = E_TSP_0;
    switch(mapiSource)
    {
        case MAPI_TSP_0:
        {
            enSTCSource = E_TSP_0;
        }
        break;
        case MAPI_TSP_1:
        {
            enSTCSource = E_TSP_1;
        }
        break;
        case MAPI_TSP_MAX:
        default:
        {
            enSTCSource = E_TSP_MAX;
        }
        break;
    }

    return enSTCSource;
}

//-------------------------------------------------------------------------------------------------
/// @brief  \b Function  \b Name: _AudioDriverIdToAId()
/// @brief  \b Function  \b Description: Remapping UTOPIA dec id to MMA dec id
/// @param  audio_dec_id \b : The audio decoder ID which system want to check the request
/// @return \b : return MMA Audio dec id
//-------------------------------------------------------------------------------------------------
static MMA_AUDIO_DEC_ID _AudioDriverIdToAId(AUDIO_DEC_ID au_dec_id)
{
    MMA_AUDIO_DEC_ID audio_dec_id = AUDIO_DEC_INVALID;

    switch ( au_dec_id )
    {
        case AU_DEC_ID1:
        {
            audio_dec_id = AUDIO_DEC_ID1;
        }
        break;
        case AU_DEC_ID2:
        {
            audio_dec_id = AUDIO_DEC_ID2;
        }
        break;
        case AU_DEC_ID3:
        {
            audio_dec_id = AUDIO_DEC_ID3;
        }
        break;
        case AU_DEC_INVALID:
        {
            audio_dec_id = AUDIO_DEC_INVALID;
        }
        break;
        default:
        {
            printf("%s: Err! AUDIO DRIVER ID is out of Range\n", __FUNCTION__);
        }
        break;
    }

    return audio_dec_id;
}

MMA_AUDIO_DEC_ID mapi_audio_customer::OpenAudioDec(AudioSDKDecStatus *p_AudioSDKDecStatus)
{
    if (NULL == p_AudioSDKDecStatus)
    {
        return AUDIO_DEC_INVALID;
    }

#if (MM_In_SEN == 1)
    if ((p_AudioSDKDecStatus->eSourceType == MAPI_INPUT_SOURCE_STORAGE) ||
         (p_AudioSDKDecStatus->eSourceType == MAPI_INPUT_SOURCE_STORAGE2))
    {
        // TVOS open decode system through OMX
        return AUDIO_DEC_INVALID;
    }
#endif

    if ((p_AudioSDKDecStatus->eSourceType != MAPI_INPUT_SOURCE_ATV) &&
    (p_AudioSDKDecStatus->eSourceType != MAPI_INPUT_SOURCE_DTV) &&
    (p_AudioSDKDecStatus->eSourceType != MAPI_INPUT_SOURCE_DTV2) &&
    (p_AudioSDKDecStatus->eSourceType != MAPI_INPUT_SOURCE_HDMI) &&
    (p_AudioSDKDecStatus->eSourceType != MAPI_INPUT_SOURCE_HDMI2) &&
    (p_AudioSDKDecStatus->eSourceType != MAPI_INPUT_SOURCE_HDMI3) &&
    (p_AudioSDKDecStatus->eSourceType != MAPI_INPUT_SOURCE_HDMI4) &&
    (p_AudioSDKDecStatus->eSourceType != MAPI_INPUT_SOURCE_STORAGE) &&
    (p_AudioSDKDecStatus->eSourceType != MAPI_INPUT_SOURCE_STORAGE2))
    {
        //printf("%s() %d\n",__FUNCTION__,__LINE__);
        return AUDIO_DEC_INVALID;
    }

    AudioDecStatus_t p_AudioDecStatus_t;

    // Initial Dec Info
    memset(&p_AudioDecStatus_t, 0x00, sizeof(AudioDecStatus_t));

    p_AudioDecStatus_t.bIsAD = p_AudioSDKDecStatus->bIsAD;
    p_AudioDecStatus_t.eAudFormat  = _SDKDSPSystemTypeToDecSystemType(p_AudioSDKDecStatus->eDSPSystem);
    // Need Implement

    p_AudioDecStatus_t.eSourceType = (AUDIO_SOURCE_INFO_TYPE)_SDK_APISourceType_To_DriverAudioSourceInfoType(p_AudioSDKDecStatus->eSourceType);
    p_AudioDecStatus_t.eGroup =  _MapiPortToAudioOutportType(p_AudioSDKDecStatus->eProcessorType);

    p_AudioDecStatus_t.eStcSource= _MapiStcSourceToAudioStcSource(p_AudioSDKDecStatus->eStcSource);
    p_AudioDecStatus_t.eMMType = _MapiMMTypeToAudioMMType(p_AudioSDKDecStatus->eMMType);

    p_AudioSDKDecStatus->eAudioDecID = _AudioDriverIdToAId(MApi_AUDIO_OpenDecodeSystem(&p_AudioDecStatus_t));

    return  p_AudioSDKDecStatus->eAudioDecID;
}

MAPI_BOOL mapi_audio_customer::CloseAudioDec(MMA_AUDIO_DEC_ID DecID)
{
    if ((DecID == AUDIO_DEC_INVALID) || (DecID == AUDIO_DEC_MAX))
    {
        return FALSE;
    }

    return (MApi_AUDIO_ReleaseDecodeSystem(_AIdToAudioDriverId(DecID)));
}

MAPI_BOOL mapi_audio_customer::SetAudioDec(MMA_AUDIO_DEC_ID DecID, AudioSDKDecStatus *p_AudioSDKDecStatus)
{
    if (NULL == p_AudioSDKDecStatus)
    {
        return FALSE;
    }

    if ((DecID == AUDIO_DEC_INVALID) || (DecID == AUDIO_DEC_MAX))
    {
        return FALSE;
    }

    AudioDecStatus_t stAudioDecStatus_t;
    MApi_AUDIO_GetDecodeSystem(_AIdToAudioDriverId(DecID), &stAudioDecStatus_t);

    stAudioDecStatus_t.eAudFormat  = _SDKDSPSystemTypeToDecSystemType(p_AudioSDKDecStatus->eDSPSystem);
    stAudioDecStatus_t.eMMType = _MapiMMTypeToAudioMMType(p_AudioSDKDecStatus->eMMType);
    stAudioDecStatus_t.eGroup =  _MapiPortToAudioOutportType(p_AudioSDKDecStatus->eProcessorType);
    stAudioDecStatus_t.eSourceType = (AUDIO_SOURCE_INFO_TYPE)_SDK_APISourceType_To_DriverAudioSourceInfoType(p_AudioSDKDecStatus->eSourceType);
    stAudioDecStatus_t.eStcSource= _MapiStcSourceToAudioStcSource(p_AudioSDKDecStatus->eStcSource);

    MApi_AUDIO_SetDecodeSystem(_AIdToAudioDriverId(DecID), &stAudioDecStatus_t);

    return  TRUE;
}

MAPI_BOOL mapi_audio_customer::SetAudioDecStatus(MAPI_AUDIO_PROCESSOR_TYPE  eProcessor, AudioSDKDecStatus *p_AudioSDKDecStatus)
{
    if (NULL == p_AudioSDKDecStatus)
    {
        return FALSE;
    }

    switch(eProcessor)
    {
        case AUDIO_PROCESSOR_MAIN:
            memcpy(&stMainAudio,p_AudioSDKDecStatus, sizeof(AudioSDKDecStatus));
            break;

        case AUDIO_PROCESSOR_SUB:
            memcpy(&stMainAudio,p_AudioSDKDecStatus, sizeof(AudioSDKDecStatus));
            break;

        case AUDIO_PROCESSOR_SCART:
            break;

        default:
            break;
    }
    return  TRUE;
}

MAPI_BOOL mapi_audio_customer::GetAudioDec(MMA_AUDIO_DEC_ID DecID, AudioSDKDecStatus *p_AudioSDKDecStatus)
{
    if ((DecID == AUDIO_DEC_INVALID) || (DecID == AUDIO_DEC_MAX))
    {
        return FALSE;
    }

    if (NULL == p_AudioSDKDecStatus)
    {
        return FALSE;
    }

    AudioDecStatus_t stAudioDecStatus;
    MApi_AUDIO_GetDecodeSystem(_AIdToAudioDriverId(DecID), &stAudioDecStatus);

    return  TRUE;
}

MAPI_BOOL mapi_audio_customer::GetAudioDecStatus(MAPI_AUDIO_PROCESSOR_TYPE  eProcessor, AudioSDKDecStatus *p_AudioSDKDecStatus)
{
    if (NULL == p_AudioSDKDecStatus)
    {
        return FALSE;
    }

    switch(eProcessor)
    {
        case AUDIO_PROCESSOR_MAIN:
            memcpy(p_AudioSDKDecStatus, &stMainAudio, sizeof(AudioSDKDecStatus));
            break;

        case AUDIO_PROCESSOR_SUB:
            memcpy(p_AudioSDKDecStatus, &stSubAudio, sizeof(AudioSDKDecStatus));
            break;

        case AUDIO_PROCESSOR_SCART:
            break;

        default:
            break;
    }
    return  TRUE;
}
//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: Audio_SIF_Monitor()
/// @brief \b Function \b Description: Audio handler
/// @param <IN>        \b NONE    :
/// @param <OUT>       \b NONE    :
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::SIF_Monitor_Service(void)
{
    if(m_wAudioDownCountTimer > 0)
    {
        m_wAudioDownCountTimer--;
        if(m_wAudioDownCountTimer > 0)
        {
            return;
        }
    }
    m_wAudioDownCountTimer = 50;

    SIF_CheckAudioStandard();
}

//------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SIF_SetSIFPrescale()
/// @brief \b Function \b Description: Apply ATV SIF prescale.
/// @param <IN>        \b NONE :
/// @param <OUT>       \b NONE :
/// @param <GLOBAL>    \b NONE :
//------------------------------------------------------------------------------------------
MAPI_BOOL mapi_audio_customer::SIF_SetSIFPrescale(void)
{
    if(IS_SBTVD_BRAZIL())
    {
        MApi_AUDIO_SIF_SetPrescale(SET_PRESCALE_BTSC, 0+m_SifOutFmOffset+m_stSIF_Prescale.Prescale_BTSC);
        MApi_AUDIO_SIF_SetPrescale(SET_PRESCALE_BTSC_MONO, 0+m_SifOutFmOffset+m_stSIF_Prescale.Prescale_BTSC_MONO);
        MApi_AUDIO_SIF_SetPrescale(SET_PRESCALE_BTSC_STEREO, 0+m_SifOutFmOffset+m_stSIF_Prescale.Prescale_BTSC_STEREO);
        MApi_AUDIO_SIF_SetPrescale(SET_PRESCALE_BTSC_SAP, 0+m_SifOutFmOffset+m_stSIF_Prescale.Prescale_BTSC_SAP);
    }
    else
    {
        if(mapi_syscfg_fetch::GetInstance()->get_ATVSystemType() == E_PAL_ENABLE)
        {
            if(ISAUDIOSIF_EN() == MAPI_FALSE)
            {
                //VIF mode
                MApi_AUDIO_SIF_SetPrescale(SET_PRESCALE_A2_FM, 5+m_SifOutFmOffset+m_stSIF_Prescale.Prescale_A2_FM);
                MApi_AUDIO_SIF_SetPrescale(SET_PRESCALE_FM_M, 5+m_SifOutFmOffset+m_stSIF_Prescale.Prescale_FM_M);
                MApi_AUDIO_SIF_SetPrescale(SET_PRESCALE_HIDEV, 5+m_SifOutFmOffset+m_stSIF_Prescale.Prescale_HIDEV);
                MApi_AUDIO_SIF_SetPrescale(SET_PRESCALE_HIDEV_M, 5+m_SifOutFmOffset+m_stSIF_Prescale.Prescale_HIDEV_M);
                MApi_AUDIO_SIF_SetPrescale(SET_PRESCALE_NICAM, 5+m_SifOutNicamOffset+m_stSIF_Prescale.Prescale_NICAM);
                MApi_AUDIO_SIF_SetPrescale(SET_PRESCALE_AM, 8+m_stSIF_Prescale.Prescale_AM);
            }
            else
            {
                //SIF mode
                MApi_AUDIO_SIF_SetPrescale(SET_PRESCALE_A2_FM, 6+m_SifOutFmOffset+m_stSIF_Prescale.Prescale_A2_FM);
                MApi_AUDIO_SIF_SetPrescale(SET_PRESCALE_FM_M, 6+m_SifOutFmOffset+m_stSIF_Prescale.Prescale_FM_M);
                MApi_AUDIO_SIF_SetPrescale(SET_PRESCALE_HIDEV, 6+m_SifOutFmOffset+m_stSIF_Prescale.Prescale_HIDEV);
                MApi_AUDIO_SIF_SetPrescale(SET_PRESCALE_HIDEV_M, 6+m_SifOutFmOffset+m_stSIF_Prescale.Prescale_HIDEV_M);
                MApi_AUDIO_SIF_SetPrescale(SET_PRESCALE_NICAM, 6+m_SifOutNicamOffset+m_stSIF_Prescale.Prescale_NICAM);
                MApi_AUDIO_SIF_SetPrescale(SET_PRESCALE_AM, 9+m_stSIF_Prescale.Prescale_AM);
            }
        }
        else if(mapi_syscfg_fetch::GetInstance()->get_ATVSystemType() == E_NTSC_ENABLE)
        {
            if(mapi_syscfg_fetch::GetInstance()->get_AUDIOSystemType() == E_BTSC_ENABLE)
            {
                MApi_AUDIO_SIF_SetPrescale(SET_PRESCALE_BTSC, 0+m_SifOutFmOffset+m_stSIF_Prescale.Prescale_BTSC);
                MApi_AUDIO_SIF_SetPrescale(SET_PRESCALE_BTSC_MONO, 0+m_SifOutFmOffset+m_stSIF_Prescale.Prescale_BTSC_MONO);
                MApi_AUDIO_SIF_SetPrescale(SET_PRESCALE_BTSC_STEREO, 0+m_SifOutFmOffset+m_stSIF_Prescale.Prescale_BTSC_STEREO);
                MApi_AUDIO_SIF_SetPrescale(SET_PRESCALE_BTSC_SAP, 0+m_SifOutFmOffset+m_stSIF_Prescale.Prescale_BTSC_SAP);
            }
            else if(mapi_syscfg_fetch::GetInstance()->get_AUDIOSystemType() == E_A2_ENABLE)
            {
                MApi_AUDIO_SIF_SetPrescale(SET_PRESCALE_FM_M, 5+m_SifOutFmOffset+m_stSIF_Prescale.Prescale_FM_M); //Korea A2
            }
        }
    }

    return MAPI_TRUE;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: AUDIO_HDMIRx_Monitor()
/// @brief \b Function \b Description: Monitor HDMI-Rx Mode PCM/nonPCM and Auto-Setting PCM/nonPCM Mode.
/// @param <IN>        \b NONE    :
/// @param <OUT>       \b NONE    :
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::AUDIO_HDMIRx_Monitor(void)
{
    MS_BOOL bHdmiInSub = FALSE;
    AUDIOMUTESOURCE_TYPE_ eMuteSource = E_AUDIOMUTESOURCE_MAINSOURCE_;
    MS_U32 c_bit, l_bit;
    HDMI_TYPE_ cur_hdmiIsRaw = (HDMI_TYPE_)MApi_AUDIO_HDMI_GetNonpcmFlag();
    m_crHDMI_RX_Mode = (AUDIO_HDMI_RX_TYPE_)MApi_AUDIO_HDMI_RX_GetNonPCM();
    m_crHdmiAC3inSE = MApi_AUDIO_GetAC3PInfo(Audio_AC3P_infoType_hdmiAC3inSE);

    if((cur_hdmiIsRaw == m_bf_HdmiIsRaw) &&
      (m_crHDMI_RX_Mode == m_bfHDMI_RX_Mode) &&
      (m_crHdmiAC3inSE == m_bfHdmiAC3inSE))
    {
        return;
    }

    if(((m_mainCurInputSrc != MAPI_INPUT_SOURCE_HDMI)
            && (m_mainCurInputSrc != MAPI_INPUT_SOURCE_HDMI2)
            && (m_mainCurInputSrc != MAPI_INPUT_SOURCE_HDMI3)
            && (m_mainCurInputSrc != MAPI_INPUT_SOURCE_HDMI4)
        )&&((m_subCurInputSrc != MAPI_INPUT_SOURCE_HDMI)
            && (m_subCurInputSrc != MAPI_INPUT_SOURCE_HDMI2)
            && (m_subCurInputSrc != MAPI_INPUT_SOURCE_HDMI3)
            && (m_subCurInputSrc != MAPI_INPUT_SOURCE_HDMI4)
        ))
    {
        return;
    }

    if((m_subCurInputSrc == MAPI_INPUT_SOURCE_HDMI)||(m_subCurInputSrc == MAPI_INPUT_SOURCE_HDMI2)
       ||(m_subCurInputSrc == MAPI_INPUT_SOURCE_HDMI3)||(m_subCurInputSrc == MAPI_INPUT_SOURCE_HDMI4))
    {
        bHdmiInSub = TRUE;
        eMuteSource = E_AUDIOMUTESOURCE_SUBSOURCE_;
    }

    c_bit = MApi_AUDIO_GetCommAudioInfo(Audio_Comm_infoType_getHDMI_CopyRight_C_Bit);
    l_bit = MApi_AUDIO_GetCommAudioInfo(Audio_Comm_infoType_getHDMI_CopyRight_L_Bit);
    MApi_AUDIO_SetCommAudioInfo(Audio_Comm_infoType_SetSCMS, c_bit, l_bit);

    if(cur_hdmiIsRaw)
    {
        DBG_MMA_MSG(printf("HDMI --> NonPcm mode\n"));
        //printf(" ---- m_crHDMI_RX_Mode = %x\n",m_crHDMI_RX_Mode);
        //printf("HDMI --> NonPcm mode\n");
        SetSoundMuteStatus(E_AUDIO_INTERNAL_4_MUTEON_, eMuteSource);

        if(bHdmiInSub)
        {
                MApi_AUDIO_HDMI_RX_SetNonpcm(0x11); //HDMI non-PCM setting in PIP Sub
        }
        else
        {
                MApi_AUDIO_HDMI_RX_SetNonpcm(0x01); //HDMI non-PCM setting in PIP Main
        }

        if(MApi_AUDIO_HDMI_RX_GetNonPCM() == HDMI_RX_DD)
        {
            MApi_AUDIO_SetAC3Info(Audio_AC3_infoType_DrcMode, LINE_MODE, 0);                                            //Line Mod
            MApi_AUDIO_SetAC3Info(Audio_AC3_infoType_DownmixMode, DOLBY_DOWNMIX_MODE_LTRT, 0);                          //LtRt
        }

        SetSoundMuteStatus(E_AUDIO_INTERNAL_4_MUTEOFF_, eMuteSource);

    }
    else
    {
        DBG_MMA_MSG(printf("HDMI  --> Pcm mode\n"));
        SetSoundMuteStatus(E_AUDIO_INTERNAL_4_MUTEON_, eMuteSource);

        if(bHdmiInSub)
        {
                MApi_AUDIO_HDMI_RX_SetNonpcm(0x10); //HDMI non-PCM setting in PIP Sub
        }
        else
        {
                MApi_AUDIO_HDMI_RX_SetNonpcm(0x00); //HDMI non-PCM setting in PIP Main
        }

//        m_crHDMI_RX_Mode = (AUDIO_HDMI_RX_TYPE_)MApi_AUDIO_HDMI_RX_SetNonpcm(FALSE);                                 // HDMI non-PCM setting
        SetSoundMuteStatus(E_AUDIO_INTERNAL_4_MUTEOFF_, eMuteSource);
    }

    SPDIF_SetMode(SDK_cUI_SPDIF_Mode);
    m_bfHdmiAC3inSE = m_crHdmiAC3inSE;
    m_bf_HdmiIsRaw = (HDMI_TYPE_)cur_hdmiIsRaw;
    m_bfHDMI_RX_Mode = m_crHDMI_RX_Mode;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SND_SetTreble()
/// @brief \b Function \b Description:
/// @param <IN>        \b NONE    :
/// @param <OUT>       \b NONE    :
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::SND_SetTreble(MAPI_U8 u8Treble)
{
    if(u8Treble  > 100)
    {
        printf("%s(%d):err! Max treble is 100\n", __FUNCTION__, u8Treble);
        u8Treble = 100;
    }

    MApi_AUDIO_EnableEQ(0);
    MApi_AUDIO_SetTreble(u8Treble);

    m_AudioSnd_TrebleValue = u8Treble;
    m_AudioSnd_EQ_Mode = false;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SND_GetTreble()
/// @brief \b Function \b Description:
/// @param <IN>        \b NONE    :
/// @param <OUT>       \b Current Treble values (0 ~ 100)
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
MAPI_U8 mapi_audio_customer::SND_GetTreble(void)
{
    if(m_AudioSnd_EQ_Mode)
    {
        return 0;
    }
    else
    {
        return m_AudioSnd_TrebleValue;
    }
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SND_SetBass()
/// @brief \b Function \b Description:
/// @param <IN>        \b Bass value (0 ~ 100):
/// @param <OUT>       \b NONE    :
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::SND_SetBass(MAPI_U8 u8Bass)
{
    if(u8Bass  > 100)
    {
        printf("%s(%d):err! Max treble is 100\n", __FUNCTION__, u8Bass);
        u8Bass = 100;
    }

    MApi_AUDIO_EnableEQ(0);
    MApi_AUDIO_SetBass(u8Bass);

    m_AudioSnd_BassValue = u8Bass;
    m_AudioSnd_EQ_Mode = false;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SND_GetBass()
/// @brief \b Function \b Description:
/// @param <IN>        \b NONE    :
/// @param <OUT>       \b Current bass values (0 ~ 100)
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
MAPI_U8 mapi_audio_customer::SND_GetBass(void)
{
    if(m_AudioSnd_EQ_Mode)
    {
        return 0;
    }
    else
    {
        return m_AudioSnd_BassValue;
    }
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SND_SetBalance()
/// @brief \b Function \b Description:
/// @param <IN>        \b Balance value (0 ~ 100)   :
/// @param <OUT>       \b NONE    :
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::SND_SetBalance(MAPI_U8 u8Balance)
{
    MApi_AUDIO_SetBalance(u8Balance);
    m_AudioSnd_BalanceValue = u8Balance;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SND_GetBalance()
/// @brief \b Function \b Description:
/// @param <IN>        \b NONE    :
/// @param <OUT>       \b Current balance values (0 ~ 100)
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
MAPI_U8 mapi_audio_customer::SND_GetBalance(void)
{
    return m_AudioSnd_BalanceValue;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SND_SetEq()
/// @brief \b Function \b Description:
/// @param <IN>        \b NONE    :
/// @param <OUT>       \b NONE    :
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::SND_SetEq(MAPI_U8 u8band, MAPI_U8 u8level)
{
    MApi_AUDIO_EnableEQ(TRUE);
    MApi_AUDIO_SetEq(u8band, u8level);
    m_AudioSnd_EQ_Mode = TRUE;
}

//-------------------------------------------------------------------------------------------------
/// @brief  \b Function  \b Name: CheckSourceType()
/// @brief  \b Function  \b Description: Check audio source type=main or sub
/// @param  eCurrectSource    \b : Current input source
/// @return \b AUDIOMUTESOURCE_TYPE_    : Main/Sub/all
//-------------------------------------------------------------------------------------------------
AUDIOMUTESOURCE_TYPE_ mapi_audio_customer::CheckSourceType(MAPI_INPUT_SOURCE_TYPE eCurrectSource)
{
    AUDIOMUTESOURCE_TYPE_ eRetType = E_AUDIOMUTESOURCE_ACTIVESOURCE_;

    if(eCurrectSource == m_mainCurInputSrc)
    {
        eRetType = E_AUDIOMUTESOURCE_MAINSOURCE_;
    }
    else if(eCurrectSource == m_subCurInputSrc)
    {
        eRetType = E_AUDIOMUTESOURCE_SUBSOURCE_;
    }
    else
    {
        eRetType = E_AUDIOMUTESOURCE_ACTIVESOURCE_;
    }

    return eRetType;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SetSoundMuteByHokey()
/// @brief \b Function \b Description: This routine is used to set IR key Mute  .
/// @param <IN>        \b eSoundMuteSource    : mute source type
/// @param <IN>        \b eOnOff    : mute or unmute
/// @param <OUT>       \b NONE    :
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::SetSoundMuteByHokey(SOUND_MUTE_TYPE_ eOnOff)
{
    m_AudioMuteByHotKey = eOnOff;

    if(eOnOff)
    {
        SetSoundMuteStatus(E_AUDIO_BYUSER_MUTEON_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
    }
    else
    {
        SetSoundMuteStatus(E_AUDIO_BYUSER_MUTEOFF_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
    }
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: MW_AUD_SetSoundMute()
/// @brief \b Function \b Description: This routine is used to set all kind of audio  .
/// @param <IN>        \b eSoundMuteSource    : mute source type
/// @param <IN>        \b eOnOff    : mute or unmute
/// @param <OUT>       \b NONE    :
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::SetSoundMute(SOUND_MUTE_SOURCE_ eSoundMuteSource, SOUND_MUTE_TYPE_ eOnOff)
{
    //if (m_AudioMuteByHotKey)
    //return;

#if (STR_ENABLE == 1)
    if(mapi_system::GetInstance()->QueryWakeupSource() == EN_WAKEUPSRC_AVLINK && eOnOff == E_MUTE_OFF_)
    {
        return;
    }
#endif

    const AudioPath_t* const p_AudioPath = mapi_syscfg_fetch::GetInstance()->GetAudioPathInfo();
    const AudioOutputType_t* const p_AudioOutput = mapi_syscfg_fetch::GetInstance()->GetAudioOutputTypeInfo();

    switch(eSoundMuteSource)
    {
        case SOUND_MUTE_TV_:
            if((AUDIO_OUTPUT_TYPE)_u32OutputToAudioOutputType(p_AudioOutput[MAPI_AUDIO_OUTPUT_MAIN_SPEAKER].u32Output) != AUDIO_I2S_OUTPUT)
            {
                MApi_AUDIO_SetMute(AUDIO_PATH_0, eOnOff); // Mute CH1 for CH5->SRC->CH1 case
            }

            MApi_AUDIO_SetMute((AUDIO_PATH_TYPE)_u32PathToAudioPathType(p_AudioPath[MAPI_AUDIO_PATH_MAIN_SPEAKER].u32Path), eOnOff);
            break;

        case SOUND_MUTE_SPEAKER_:
            if((AUDIO_OUTPUT_TYPE)_u32OutputToAudioOutputType(p_AudioOutput[MAPI_AUDIO_OUTPUT_MAIN_SPEAKER].u32Output) != AUDIO_I2S_OUTPUT)
            {
                MApi_AUDIO_SetMute(AUDIO_PATH_0, eOnOff);   // Mute CH1 for CH5->SRC->CH1 case
            }

            MApi_AUDIO_SetMute(_u32PathToAudioPathType(p_AudioPath[MAPI_AUDIO_PATH_MAIN_SPEAKER].u32Path), eOnOff);
            break;

       case SOUND_MUTE_INTERNAL_MAIN_:


            MApi_AUDIO_SetMute(AUDIO_T3_PATH_MIXER_MAIN, eOnOff);

         break;

        case SOUND_MUTE_HP_:
            MApi_AUDIO_SetMute((AUDIO_PATH_TYPE)_u32PathToAudioPathType(p_AudioPath[MAPI_AUDIO_PATH_HP].u32Path), eOnOff);
            break;

        case SOUND_MUTE_SCART_:
            MApi_AUDIO_SetMute((AUDIO_PATH_TYPE)_u32PathToAudioPathType(p_AudioPath[MAPI_AUDIO_PATH_SIFOUT].u32Path), eOnOff);
            break;

        case SOUND_MUTE_MONITOR_OUT_:
        case SOUND_MUTE_SCART2_:
            MApi_AUDIO_SetMute((AUDIO_PATH_TYPE)_u32PathToAudioPathType(p_AudioPath[MAPI_AUDIO_PATH_LINEOUT].u32Path), eOnOff);
            break;

        case SOUND_MUTE_SPDIF_:
            MApi_AUDIO_SetMute((AUDIO_PATH_TYPE)_u32PathToAudioPathType(p_AudioPath[MAPI_AUDIO_PATH_SPDIF].u32Path), eOnOff);
            break;

        case SOUND_MUTE_HDMITX_:
            MApi_AUDIO_SetMute(_u32PathToAudioPathType(p_AudioPath[MAPI_AUDIO_PATH_HDMI].u32Path), eOnOff);
            MApi_AUDIO_HDMI_Tx_SetMute(eOnOff);
            break;

        case SOUND_MUTE_DATA_IN_:
            MApi_AUDIO_SetMute(AUDIO_T3_PATH_MIXER_DMA_IN, eOnOff);
            break;
#if (STB_ENABLE == 0)
        case SOUND_MUTE_CH7_:
            MApi_AUDIO_SetMute(AUDIO_PATH_7, eOnOff);
            break;
#endif
        case SOUND_MUTE_PCM_CAPTURE1_:
            MApi_AUDIO_SetMute(AUDIO_T3_PATH_PCM_CAPTURE1, eOnOff);
            break;

        case SOUND_MUTE_PCM_CAPTURE2_:
            MApi_AUDIO_SetMute(AUDIO_T3_PATH_PCM_CAPTURE2, eOnOff);
            break;

        case SOUND_MUTE_ALL_EXCEPT_SCART_:
            if((AUDIO_OUTPUT_TYPE)_u32OutputToAudioOutputType(p_AudioOutput[MAPI_AUDIO_OUTPUT_MAIN_SPEAKER].u32Output) != AUDIO_I2S_OUTPUT)
            {
                MApi_AUDIO_SetMute(AUDIO_PATH_0, eOnOff);   // Mute CH1 for CH5->SRC->CH1 case
            }

            MApi_AUDIO_SetMute((AUDIO_PATH_TYPE)_u32PathToAudioPathType(p_AudioPath[MAPI_AUDIO_PATH_MAIN_SPEAKER].u32Path), eOnOff);
            MApi_AUDIO_SetMute((AUDIO_PATH_TYPE)_u32PathToAudioPathType(p_AudioPath[MAPI_AUDIO_PATH_HP].u32Path), eOnOff);
            MApi_AUDIO_SetMute((AUDIO_PATH_TYPE)_u32PathToAudioPathType(p_AudioPath[MAPI_AUDIO_PATH_LINEOUT].u32Path), eOnOff);
            MApi_AUDIO_SetMute((AUDIO_PATH_TYPE)_u32PathToAudioPathType(p_AudioPath[MAPI_AUDIO_PATH_SPDIF].u32Path), eOnOff);
            break;

        case SOUND_MUTE_ALL_:
            if((AUDIO_OUTPUT_TYPE)_u32OutputToAudioOutputType(p_AudioOutput[MAPI_AUDIO_OUTPUT_MAIN_SPEAKER].u32Output) != AUDIO_I2S_OUTPUT)
            {
                MApi_AUDIO_SetMute(AUDIO_PATH_0, eOnOff);   // Mute CH1 for CH5->SRC->CH1 case
            }

            MApi_AUDIO_SetMute((AUDIO_PATH_TYPE)_u32PathToAudioPathType(p_AudioPath[MAPI_AUDIO_PATH_MAIN_SPEAKER].u32Path), eOnOff);
            MApi_AUDIO_SetMute((AUDIO_PATH_TYPE)_u32PathToAudioPathType(p_AudioPath[MAPI_AUDIO_PATH_HP].u32Path), eOnOff);
            MApi_AUDIO_SetMute((AUDIO_PATH_TYPE)_u32PathToAudioPathType(p_AudioPath[MAPI_AUDIO_PATH_LINEOUT].u32Path), eOnOff);
            MApi_AUDIO_SetMute((AUDIO_PATH_TYPE)_u32PathToAudioPathType(p_AudioPath[MAPI_AUDIO_PATH_SIFOUT].u32Path), eOnOff);
            MApi_AUDIO_SetMute((AUDIO_PATH_TYPE)_u32PathToAudioPathType(p_AudioPath[MAPI_AUDIO_PATH_SPDIF].u32Path), eOnOff);
            break;

        case SOUND_MUTE_MIXER_SECONDARY_:
                MApi_AUDIO_SetMute(AUDIO_T3_PATH_MIXER_SECONDARY, eOnOff);
            break;

        case SOUND_MUTE_MIXER_DMA_IN_:
                MApi_AUDIO_SetMute(AUDIO_T3_PATH_MIXER_DMA_IN, eOnOff);
            break;

#if (ENABLE_LITE_SN == 0)
        case SOUND_MUTE_AMP_:
            {
#if defined(PQ_ENGINE) && (PQ_ENGINE == 1)
                int m_eConnect_Type = 0;
                int m_eUrsaType = E_URSA_NONE;
                mapi_syscfg_fetch::GetInstance()->GetModuleParameter_int("M_URSA:F_URSA_DIRECT_CONNECT", &m_eConnect_Type, 0);
                mapi_syscfg_fetch::GetInstance()->GetModuleParameter_int("M_URSA:F_URSA_URSA_TYPE", &m_eUrsaType, 0);
                if((m_eConnect_Type == 0) && (m_eUrsaType != E_URSA_NONE) && (m_eUrsaType != E_URSA_INX))
                {
                    mapi_ursa *pUrsa = mapi_interface::Get_mapi_pcb()->GetUrsa(0);
                    if(pUrsa)
                    {
                        pUrsa->Amp_Enable(eOnOff);
                    }
                }
#endif
                {
                    mapi_audio_amp *pAudioAmp = mapi_pcb::GetInstance()->GetAudioAmp(0);

                    if(pAudioAmp != NULL)
                    {
                        pAudioAmp->Mute((MAPI_BOOL)eOnOff);
                    }
                    else
                    {
                        mapi_gpio *gptr = mapi_gpio::GetGPIO_Dev(MUTE);
                        if(gptr != NULL)
                        {
                            if(E_MUTE_ON_ == eOnOff)
                            {
                                gptr->SetOn();
                            }
                            else
                            {
                                gptr->SetOff();
                            }
                        }
                    }
                }
            }
            break;
#endif
        default:
            break;
    }
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: IsMuteStateIsSet()
/// @brief \b Function \b Description: The main function of check mute state is set
/// @param <IN>        \b eAudioMuteType   :
/// @param <OUT>       \b BOOL    :
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
MAPI_BOOL mapi_audio_customer::UpdateMuteFlagStatus(AUDIOMUTETYPE_ eAudioMuteType, AUDIOMUTESOURCE_TYPE_ eAudioMuteSource)
{
    MAPI_BOOL bRet = FALSE;
#if (MSTAR_TVOS == 0)
    if((!SDK_bAllAudioMuteCtrl)&&(eAudioMuteType != E_AUDIO_ALL_MUTEON_)) // CP add for Skype
    {
        return TRUE;
    }
#endif
    switch(eAudioMuteSource)
    {
        case E_AUDIOMUTESOURCE_ACTIVESOURCE_:
        case E_AUDIOMUTESOURCE_MAINSOURCE_:
        case E_AUDIOMUTESOURCE_SUBSOURCE_:
            break;
         default :
            printf("\r\n======== Incorrect audio mute source !!==========\r\n");
            return FALSE;
            break;
    }


  if(eAudioMuteSource==E_AUDIOMUTESOURCE_SUBSOURCE_)
  {
    switch(eAudioMuteType)
    {
        case E_AUDIO_MHEGAP_MUTEOFF_:
            if(!SDK_SUB_bMHEGApMute)
            {
                bRet = TRUE;
            }

            SDK_SUB_bMHEGApMute = MAPI_FALSE;
            break;

        case E_AUDIO_MHEGAP_MUTEON_:
            if(SDK_SUB_bMHEGApMute)
            {
                bRet = TRUE;
            }

            SDK_SUB_bMHEGApMute = MAPI_TRUE;
            break;
        case E_AUDIO_PERMANENT_MUTEOFF_:
            if(!SDK_SUB_bPermanentAudioMute)
            {
                bRet = TRUE;
            }

            SDK_SUB_bPermanentAudioMute = MAPI_FALSE;
            break;
        case E_AUDIO_PERMANENT_MUTEON_:
            if(SDK_SUB_bPermanentAudioMute)
            {
                bRet = TRUE;
            }

            SDK_SUB_bPermanentAudioMute = MAPI_TRUE;
            break;
        case E_AUDIO_MOMENT_MUTEOFF_:
            if(!SDK_SUB_bMomentAudioMute)
            {
                bRet = TRUE;
            }

            SDK_SUB_bMomentAudioMute = MAPI_FALSE;
            break;
        case E_AUDIO_MOMENT_MUTEON_:
            if(SDK_SUB_bMomentAudioMute)
            {
                bRet = TRUE;
            }

            SDK_SUB_bMomentAudioMute = MAPI_TRUE;
            break;
        case E_AUDIO_BYUSER_MUTEOFF_:
            if(!SDK_SUB_bByUserAudioMute)
            {
                bRet = TRUE;
            }

            SDK_SUB_bByUserAudioMute = MAPI_FALSE;
            break;
        case E_AUDIO_BYUSER_MUTEON_:
            if(SDK_SUB_bByUserAudioMute)
            {
                bRet = TRUE;
            }

            SDK_SUB_bByUserAudioMute = MAPI_TRUE;
            break;
        case E_AUDIO_BYSYNC_MUTEOFF_:
            if(!SDK_SUB_bBySyncAudioMute)
            {
                bRet = TRUE;
            }

            SDK_SUB_bBySyncAudioMute = MAPI_FALSE;
            break;
        case E_AUDIO_BYSYNC_MUTEON_:
            if(SDK_SUB_bBySyncAudioMute)
            {
                bRet = TRUE;
            }

            SDK_SUB_bBySyncAudioMute = MAPI_TRUE;
            break;
        case E_AUDIO_BYVCHIP_MUTEOFF_:
            if(!SDK_SUB_bByVChipAudioMute)
            {
                bRet = TRUE;
            }

            SDK_SUB_bByVChipAudioMute = MAPI_FALSE;
            break;
        case E_AUDIO_BYVCHIP_MUTEON_:
            if(SDK_SUB_bByVChipAudioMute)
            {
                bRet = TRUE;
            }

            SDK_SUB_bByVChipAudioMute = MAPI_TRUE;
            break;
        case E_AUDIO_BYBLOCK_MUTEOFF_:
            if(!SDK_SUB_bByBlockAudioMute)
            {
                bRet = TRUE;
            }

            SDK_SUB_bByBlockAudioMute = MAPI_FALSE;
            break;
        case E_AUDIO_BYBLOCK_MUTEON_:
            if(SDK_SUB_bByBlockAudioMute)
            {
                bRet = TRUE;
            }

            SDK_SUB_bByBlockAudioMute = MAPI_TRUE;
            break;
        case E_AUDIO_INTERNAL_1_MUTEOFF_:
            if(!SDK_SUB_bInternal1AudioMute)
            {
                bRet = TRUE;
            }

            SDK_SUB_bInternal1AudioMute = MAPI_FALSE;
            break;
        case E_AUDIO_INTERNAL_1_MUTEON_:
            if(SDK_SUB_bInternal1AudioMute)
            {
                bRet = TRUE;
            }

            SDK_SUB_bInternal1AudioMute = MAPI_TRUE;
            break;
        case E_AUDIO_SIGNAL_UNSTABLE_MUTEOFF_:
            if(!SDK_SUB_bInternal2AudioMute)
            {
                bRet = TRUE;
            }

            SDK_SUB_bInternal2AudioMute = MAPI_FALSE;
            break;
        case E_AUDIO_SIGNAL_UNSTABLE_MUTEON_:
            if(SDK_SUB_bInternal2AudioMute)
            {
                bRet = TRUE;
            }

            SDK_SUB_bInternal2AudioMute = MAPI_TRUE;
            break;
        case E_AUDIO_INTERNAL_3_MUTEOFF_:
            if(!SDK_SUB_bInternal3AudioMute)
            {
                bRet = TRUE;
            }

            SDK_SUB_bInternal3AudioMute = MAPI_FALSE;
            break;
        case E_AUDIO_INTERNAL_3_MUTEON_:
            if(SDK_SUB_bInternal3AudioMute)
            {
                bRet = TRUE;
            }

            SDK_SUB_bInternal3AudioMute = MAPI_TRUE;
            break;
        case E_AUDIO_INTERNAL_4_MUTEOFF_:
            if(!SDK_SUB_bInternal4AudioMute)
            {
                bRet = TRUE;
            }

            SDK_SUB_bInternal4AudioMute = MAPI_FALSE;
            break;
        case E_AUDIO_INTERNAL_4_MUTEON_:
            if(SDK_SUB_bInternal4AudioMute)
            {
                bRet = TRUE;
            }

            SDK_SUB_bInternal4AudioMute = MAPI_TRUE;
            break;
        case E_AUDIO_DURING_LIMITED_TIME_MUTEOFF_:
            if(!SDK_SUB_bByDuringLimitedTimeAudioMute)
            {
                bRet = TRUE;
            }

            SDK_SUB_bByDuringLimitedTimeAudioMute = MAPI_FALSE;
            break;
        case E_AUDIO_DURING_LIMITED_TIME_MUTEON_:
            if(SDK_SUB_bByDuringLimitedTimeAudioMute)
            {
                bRet = TRUE;
            }

            SDK_SUB_bByDuringLimitedTimeAudioMute = MAPI_TRUE;
            break;

        case E_AUDIO_CI_MUTEOFF_:
        case E_AUDIO_CI_MUTEON_:
         // No need to handle CI scart out mute event in sub source (because no scart out)
            bRet = TRUE;
            break;

        case E_AUDIO_SCAN_MUTEOFF_:
            if(!SDK_SUB_bByScanInOutchgCHchg)
            {
                bRet = TRUE;
            }

            SDK_SUB_bByScanInOutchgCHchg = MAPI_FALSE;
            break;

        case E_AUDIO_SCAN_MUTEON_:
            if(SDK_SUB_bByScanInOutchgCHchg)
            {
                bRet = TRUE;
            }

            SDK_SUB_bByScanInOutchgCHchg = MAPI_TRUE;
            break;

        case E_AUDIO_SOURCESWITCH_MUTEOFF_:
            if(!SDK_SUB_bSourceSwitchAudioMute)
            {
                bRet = TRUE;
            }

            SDK_SUB_bSourceSwitchAudioMute = MAPI_FALSE;
            break;

        case E_AUDIO_SOURCESWITCH_MUTEON_:
            if(SDK_SUB_bSourceSwitchAudioMute)
            {
                bRet = TRUE;
            }

            SDK_SUB_bSourceSwitchAudioMute = MAPI_TRUE;
            break;
#if (STB_ENABLE == 0)
        case E_AUDIO_BYUSER_CH7_MUTEOFF_:
            if(!SDK_SUB_bByUserChannel7Mute)
            {
                bRet = TRUE;
            }

            SDK_SUB_bByUserChannel7Mute = MAPI_FALSE;
            break;

        case E_AUDIO_BYUSER_CH7_MUTEON_:
            if(SDK_SUB_bByUserChannel7Mute)
            {
                bRet = TRUE;
            }

            SDK_SUB_bByUserChannel7Mute = MAPI_TRUE;
            break;
#endif
#if (INPUT_SOURCE_LOCK_ENABLE == 1)
        case E_AUDIO_INPUT_SOURCE_LOCK_MUTEOFF_:
            if(!SDK_SUB_bInputSourceLockAudioMute)
            {
                bRet = MAPI_TRUE;
            }

            SDK_SUB_bInputSourceLockAudioMute = MAPI_FALSE;
            break;

        case E_AUDIO_INPUT_SOURCE_LOCK_MUTEON_:
            if(SDK_SUB_bInputSourceLockAudioMute)
            {
                bRet = MAPI_TRUE;
            }

            SDK_SUB_bInputSourceLockAudioMute = MAPI_TRUE;
            break;
#endif

        case E_AUDIO_ALL_MUTEOFF_:
        case E_AUDIO_ALL_MUTEON_:
        case E_AUDIO_USER_SPEAKER_MUTEOFF_:
        case E_AUDIO_USER_SPEAKER_MUTEON_:
        case E_AUDIO_USER_HP_MUTEOFF_:
        case E_AUDIO_USER_HP_MUTEON_:
        case E_AUDIO_USER_SPDIF_MUTEOFF_:
        case E_AUDIO_USER_SPDIF_MUTEON_:
        case E_AUDIO_USER_SCART1_MUTEOFF_:
        case E_AUDIO_USER_SCART1_MUTEON_:
        case E_AUDIO_USER_SCART2_MUTEOFF_:
        case E_AUDIO_USER_SCART2_MUTEON_:
        case E_AUDIO_DATA_IN_MUTEOFF_:
        case E_AUDIO_DATA_IN_MUTEON_:
        case E_AUDIO_POWERON_MUTEOFF_:
        case E_AUDIO_POWERON_MUTEON_:
        case E_AUDIO_USER_PCM_CAPTURE1_MUTEOFF_:
        case E_AUDIO_USER_PCM_CAPTURE1_MUTEON_:
        case E_AUDIO_USER_PCM_CAPTURE2_MUTEOFF_:
        case E_AUDIO_USER_PCM_CAPTURE2_MUTEON_:
        default:
            printf("\r\n===== This mute event can't be controlled byaudio sub source !!! ======\r\n");
            break;
      }
  }
  else //=============== For main audio source =====================
  {

    switch(eAudioMuteType)
    {
        case E_AUDIO_MHEGAP_MUTEOFF_:
            if(!SDK_bMHEGApMute)
            {
                bRet = TRUE;
            }

            SDK_bMHEGApMute = MAPI_FALSE;
            break;

        case E_AUDIO_MHEGAP_MUTEON_:
            if(SDK_bMHEGApMute)
            {
                bRet = TRUE;
            }

            SDK_bMHEGApMute = MAPI_TRUE;
            break;
        case E_AUDIO_PERMANENT_MUTEOFF_:
            if(!SDK_bPermanentAudioMute)
            {
                bRet = TRUE;
            }

            SDK_bPermanentAudioMute = MAPI_FALSE;
            break;
        case E_AUDIO_PERMANENT_MUTEON_:
            if(SDK_bPermanentAudioMute)
            {
                bRet = TRUE;
            }

            SDK_bPermanentAudioMute = MAPI_TRUE;
            break;
        case E_AUDIO_MOMENT_MUTEOFF_:
            if(!SDK_bMomentAudioMute)
            {
                bRet = TRUE;
            }

            SDK_bMomentAudioMute = MAPI_FALSE;
            break;
        case E_AUDIO_MOMENT_MUTEON_:
            if(SDK_bMomentAudioMute)
            {
                bRet = TRUE;
            }

            SDK_bMomentAudioMute = MAPI_TRUE;
            break;
        case E_AUDIO_BYUSER_MUTEOFF_:
            if(!SDK_bByUserAudioMute)
            {
                bRet = TRUE;
            }

            SDK_bByUserAudioMute = MAPI_FALSE;
            break;
        case E_AUDIO_BYUSER_MUTEON_:
            if(SDK_bByUserAudioMute)
            {
                bRet = TRUE;
            }

            SDK_bByUserAudioMute = MAPI_TRUE;
            break;
        case E_AUDIO_BYSYNC_MUTEOFF_:
            if(!SDK_bBySyncAudioMute)
            {
                bRet = TRUE;
            }

            SDK_bBySyncAudioMute = MAPI_FALSE;
            break;
        case E_AUDIO_BYSYNC_MUTEON_:
            if(SDK_bBySyncAudioMute)
            {
                bRet = TRUE;
            }

            SDK_bBySyncAudioMute = MAPI_TRUE;
            break;
        case E_AUDIO_BYVCHIP_MUTEOFF_:
            if(!SDK_bByVChipAudioMute)
            {
                bRet = TRUE;
            }

            SDK_bByVChipAudioMute = MAPI_FALSE;
            break;
        case E_AUDIO_BYVCHIP_MUTEON_:
            if(SDK_bByVChipAudioMute)
            {
                bRet = TRUE;
            }

            SDK_bByVChipAudioMute = MAPI_TRUE;
            break;
        case E_AUDIO_BYBLOCK_MUTEOFF_:
            if(!SDK_bByBlockAudioMute)
            {
                bRet = TRUE;
            }

            SDK_bByBlockAudioMute = MAPI_FALSE;
            break;
        case E_AUDIO_BYBLOCK_MUTEON_:
            if(SDK_bByBlockAudioMute)
            {
                bRet = TRUE;
            }

            SDK_bByBlockAudioMute = MAPI_TRUE;
            break;
        case E_AUDIO_INTERNAL_1_MUTEOFF_:
            if(!SDK_bInternal1AudioMute)
            {
                bRet = TRUE;
            }

            SDK_bInternal1AudioMute = MAPI_FALSE;
            break;
        case E_AUDIO_INTERNAL_1_MUTEON_:
            if(SDK_bInternal1AudioMute)
            {
                bRet = TRUE;
            }

            SDK_bInternal1AudioMute = MAPI_TRUE;
            break;
        case E_AUDIO_SIGNAL_UNSTABLE_MUTEOFF_:
            if(!SDK_bInternal2AudioMute)
            {
                bRet = TRUE;
            }

            SDK_bInternal2AudioMute = MAPI_FALSE;
            break;
        case E_AUDIO_SIGNAL_UNSTABLE_MUTEON_:
            if(SDK_bInternal2AudioMute)
            {
                bRet = TRUE;
            }

            SDK_bInternal2AudioMute = MAPI_TRUE;
            break;
        case E_AUDIO_INTERNAL_3_MUTEOFF_:
            if(!SDK_bInternal3AudioMute)
            {
                bRet = TRUE;
            }

            SDK_bInternal3AudioMute = MAPI_FALSE;
            break;
        case E_AUDIO_INTERNAL_3_MUTEON_:
            if(SDK_bInternal3AudioMute)
            {
                bRet = TRUE;
            }

            SDK_bInternal3AudioMute = MAPI_TRUE;
            break;
        case E_AUDIO_INTERNAL_4_MUTEOFF_:
            if(!SDK_bInternal4AudioMute)
            {
                bRet = TRUE;
            }

            SDK_bInternal4AudioMute = MAPI_FALSE;
            break;
        case E_AUDIO_INTERNAL_4_MUTEON_:
            if(SDK_bInternal4AudioMute)
            {
                bRet = TRUE;
            }

            SDK_bInternal4AudioMute = MAPI_TRUE;
            break;
        case E_AUDIO_DURING_LIMITED_TIME_MUTEOFF_:
            if(!SDK_bByDuringLimitedTimeAudioMute)
            {
                bRet = TRUE;
            }

            SDK_bByDuringLimitedTimeAudioMute = MAPI_FALSE;
            break;
        case E_AUDIO_DURING_LIMITED_TIME_MUTEON_:
            if(SDK_bByDuringLimitedTimeAudioMute)
            {
                bRet = TRUE;
            }

            SDK_bByDuringLimitedTimeAudioMute = MAPI_TRUE;
            break;

        case E_AUDIO_CI_MUTEOFF_:
            if(!SDK_bCIAudioMute)
            {
                bRet = TRUE;
            }

            SDK_bCIAudioMute = MAPI_FALSE;
            break;

        case E_AUDIO_CI_MUTEON_:
            if(SDK_bCIAudioMute)
            {
                bRet = TRUE;
            }

            SDK_bCIAudioMute = MAPI_TRUE;
            break;

        case E_AUDIO_SCAN_MUTEOFF_:
            if(!SDK_bByScanInOutchgCHchg)
            {
                bRet = TRUE;
            }

            SDK_bByScanInOutchgCHchg = MAPI_FALSE;
            break;

        case E_AUDIO_SCAN_MUTEON_:
            if(SDK_bByScanInOutchgCHchg)
            {
                bRet = TRUE;
            }

            SDK_bByScanInOutchgCHchg = MAPI_TRUE;
            break;

        case E_AUDIO_SOURCESWITCH_MUTEOFF_:
            if(!SDK_bSourceSwitchAudioMute)
            {
                bRet = TRUE;
            }

            SDK_bSourceSwitchAudioMute = MAPI_FALSE;
            break;

        case E_AUDIO_SOURCESWITCH_MUTEON_:
            if(SDK_bSourceSwitchAudioMute)
            {
                bRet = TRUE;
            }

            SDK_bSourceSwitchAudioMute = MAPI_TRUE;
            break;

         case E_AUDIO_ALL_MUTEOFF_:
            SDK_bIsAudioModeChanged               = MAPI_FALSE;
            SDK_bPermanentAudioMute               = MAPI_FALSE;
            SDK_bMomentAudioMute                  = MAPI_FALSE;
            SDK_bByUserAudioMute                  = MAPI_FALSE;
            SDK_bBySyncAudioMute                  = MAPI_FALSE;
            SDK_bByVChipAudioMute                 = MAPI_FALSE;
            SDK_bByBlockAudioMute                 = MAPI_FALSE;
            SDK_bInternal1AudioMute               = MAPI_FALSE;
            SDK_bInternal2AudioMute               = MAPI_FALSE;
            SDK_bInternal3AudioMute               = MAPI_FALSE;
            SDK_bInternal4AudioMute               = MAPI_FALSE;
            SDK_bByDuringLimitedTimeAudioMute     = MAPI_FALSE;
            SDK_bByScanInOutchgCHchg              = MAPI_FALSE;
            SDK_bMHEGApMute                       = MAPI_FALSE;
            SDK_bCIAudioMute                      = MAPI_FALSE;
            SDK_bSourceSwitchAudioMute             = MAPI_FALSE;
            SDK_bUsrSpkrAudioMute                 = MAPI_FALSE;
            SDK_bUsrHpAudioMute                   = MAPI_FALSE;
            SDK_bUsrSpdifAudioMute                = MAPI_FALSE;
            SDK_bUsrScart1AudioMute               = MAPI_FALSE;
            SDK_bUsrScart2AudioMute               = MAPI_FALSE;
            SDK_bAllAudioMuteCtrl                 = MAPI_FALSE;
            SDK_bPowerOnMute                      = MAPI_FALSE;
#if (INPUT_SOURCE_LOCK_ENABLE == 1)
            SDK_bInputSourceLockAudioMute         = MAPI_FALSE;
#endif

            SDK_AUD_UnmuteAudioAMP                = 1; // UnMute Audio Amp

            break;

        case E_AUDIO_ALL_MUTEON_:
            SDK_bAllAudioMuteCtrl                 = MAPI_TRUE;
            break;

        case E_AUDIO_USER_SPEAKER_MUTEOFF_:
            if(!SDK_bUsrSpkrAudioMute)
            {
                bRet = TRUE;
            }

            SDK_bUsrSpkrAudioMute = MAPI_FALSE;
            break;

        case E_AUDIO_USER_SPEAKER_MUTEON_:
            if(SDK_bUsrSpkrAudioMute)
            {
                bRet = TRUE;
            }

            SDK_bUsrSpkrAudioMute = MAPI_TRUE;
            break;

        case E_AUDIO_USER_HP_MUTEOFF_:
            if(!SDK_bUsrHpAudioMute)
            {
                bRet = TRUE;
            }

            SDK_bUsrHpAudioMute = MAPI_FALSE;
            break;

        case E_AUDIO_USER_HP_MUTEON_:
            if(SDK_bUsrHpAudioMute)
            {
                bRet = TRUE;
            }

            SDK_bUsrHpAudioMute = MAPI_TRUE;
            break;

        case E_AUDIO_USER_SPDIF_MUTEOFF_:
            if(!SDK_bUsrSpdifAudioMute)
            {
                bRet = TRUE;
            }

            SDK_bUsrSpdifAudioMute = MAPI_FALSE;
            break;

        case E_AUDIO_USER_SPDIF_MUTEON_:
            if(SDK_bUsrSpdifAudioMute)
            {
                bRet = TRUE;
            }

            SDK_bUsrSpdifAudioMute = MAPI_TRUE;
            break;

        case E_AUDIO_USER_SCART1_MUTEOFF_:
            if(!SDK_bUsrScart1AudioMute)
            {
                bRet = TRUE;
            }

            SDK_bUsrScart1AudioMute = MAPI_FALSE;
            break;

        case E_AUDIO_USER_SCART1_MUTEON_:
            if(SDK_bUsrScart1AudioMute)
            {
                bRet = TRUE;
            }

            SDK_bUsrScart1AudioMute = MAPI_TRUE;
            break;

        case E_AUDIO_USER_SCART2_MUTEOFF_:
            if(!SDK_bUsrScart2AudioMute)
            {
                bRet = TRUE;
            }

            SDK_bUsrScart2AudioMute = MAPI_FALSE;
            break;

        case E_AUDIO_USER_SCART2_MUTEON_:
            if(SDK_bUsrScart2AudioMute)
            {
                bRet = TRUE;
            }

            SDK_bUsrScart2AudioMute = MAPI_TRUE;
            break;

        case E_AUDIO_DATA_IN_MUTEOFF_:
            if(!SDK_bUsrDataInAudioMute)
            {
                bRet = TRUE;
            }
            SDK_bUsrDataInAudioMute = MAPI_FALSE;
            break;

        case E_AUDIO_DATA_IN_MUTEON_:
            if(SDK_bUsrDataInAudioMute)
            {
                bRet = TRUE;
            }
            SDK_bUsrDataInAudioMute = MAPI_TRUE;
            break;

        case E_AUDIO_POWERON_MUTEOFF_:
            if(!SDK_bPowerOnMute)
            {
                bRet = TRUE;
            }
            SDK_bPowerOnMute = MAPI_FALSE;
            break;

        case E_AUDIO_POWERON_MUTEON_:
            if(SDK_bPowerOnMute)
            {
                bRet = TRUE;
            }
            SDK_bPowerOnMute = MAPI_TRUE;
            break;
#if (STB_ENABLE == 0)
        case E_AUDIO_BYUSER_CH7_MUTEOFF_:
            if(!SDK_bByUserChannel7Mute)
            {
                bRet = TRUE;
            }

            SDK_bByUserChannel7Mute = MAPI_FALSE;
            break;

        case E_AUDIO_BYUSER_CH7_MUTEON_:
            if(SDK_bByUserChannel7Mute)
            {
                bRet = TRUE;
            }

            SDK_bByUserChannel7Mute = MAPI_TRUE;
            break;
#endif
        case E_AUDIO_USER_PCM_CAPTURE1_MUTEOFF_:
            if(!SDK_bByUserPcmCapture1Mute)
            {
                bRet = TRUE;
            }

            SDK_bByUserPcmCapture1Mute = MAPI_FALSE;
            break;

        case E_AUDIO_USER_PCM_CAPTURE1_MUTEON_:
            if(SDK_bByUserPcmCapture1Mute)
            {
                bRet = TRUE;
            }

            SDK_bByUserPcmCapture1Mute = MAPI_TRUE;
            break;

        case E_AUDIO_USER_PCM_CAPTURE2_MUTEOFF_:
            if(!SDK_bByUserPcmCapture2Mute)
            {
                bRet = TRUE;
            }

            SDK_bByUserPcmCapture2Mute = MAPI_FALSE;
            break;

        case E_AUDIO_USER_PCM_CAPTURE2_MUTEON_:
            if(SDK_bByUserPcmCapture2Mute)
            {
                bRet = TRUE;
            }

            SDK_bByUserPcmCapture2Mute = MAPI_TRUE;
            break;

        case E_AUDIO_NULL_SOURCE_MUTEOFF_:
            if(!SDK_bNullSourceMute)
            {
                bRet = TRUE;
            }
            SDK_bNullSourceMute = MAPI_FALSE;
            break;

        case E_AUDIO_NULL_SOURCE_MUTEON_:
            if(SDK_bNullSourceMute)
            {
                bRet = TRUE;
            }
            SDK_bNullSourceMute = MAPI_TRUE;
            break;

        case E_AUDIO_APP_MUTEOFF_:
            if(!SDK_bByAppMute)
            {
                bRet = TRUE;
            }
            SDK_bByAppMute = MAPI_FALSE;
            break;

        case E_AUDIO_APP_MUTEON_:
            if(SDK_bByAppMute)
            {
                bRet = TRUE;
            }
            SDK_bByAppMute = MAPI_TRUE;
            break;

#if (INPUT_SOURCE_LOCK_ENABLE == 1)
        case E_AUDIO_INPUT_SOURCE_LOCK_MUTEOFF_:
            if(!SDK_bInputSourceLockAudioMute)
            {
                bRet = MAPI_TRUE;
            }
            SDK_bInputSourceLockAudioMute = MAPI_FALSE;
            break;

        case E_AUDIO_INPUT_SOURCE_LOCK_MUTEON_:
            if(SDK_bInputSourceLockAudioMute)
            {
                bRet = MAPI_TRUE;
            }
            SDK_bInputSourceLockAudioMute = MAPI_TRUE;
            break;
#endif

        default:
            break;
    }
  }


    return bRet;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: ClearSubSourceMuteFlag()
/// @brief \b Function \b Description: This function is used to clear mute flag when PIP off.
/// @param <IN>        \b NONE :
/// @param <OUT>       \b NONE    :
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::ClearSubSourceMuteFlag(void)
{
    SDK_SUB_bIsAudioModeChanged = MAPI_FALSE;
    SDK_SUB_bMHEGApMute = MAPI_FALSE;
    SDK_SUB_bPermanentAudioMute = MAPI_FALSE;
    SDK_SUB_bMomentAudioMute = MAPI_FALSE;
    SDK_SUB_bByUserAudioMute = MAPI_FALSE;
    SDK_SUB_bBySyncAudioMute = MAPI_FALSE;
    SDK_SUB_bByVChipAudioMute = MAPI_FALSE;
    SDK_SUB_bByBlockAudioMute = MAPI_FALSE;
    SDK_SUB_bInternal1AudioMute = MAPI_FALSE;
    SDK_SUB_bInternal2AudioMute = MAPI_FALSE;
    SDK_SUB_bInternal3AudioMute = MAPI_FALSE;
    SDK_SUB_bInternal4AudioMute = MAPI_FALSE;
    SDK_SUB_bByDuringLimitedTimeAudioMute = MAPI_FALSE;
    SDK_SUB_bCIAudioMute = MAPI_FALSE;
    SDK_SUB_bSourceSwitchAudioMute = MAPI_FALSE;
#if (INPUT_SOURCE_LOCK_ENABLE == 1)
    SDK_SUB_bInputSourceLockAudioMute = MAPI_FALSE;
#endif

 // Toggle mute function to update H/W status
    SetSoundMuteStatus(E_AUDIO_PERMANENT_MUTEON_, E_AUDIOMUTESOURCE_SUBSOURCE_);
    SetSoundMuteStatus(E_AUDIO_PERMANENT_MUTEOFF_, E_AUDIOMUTESOURCE_SUBSOURCE_);
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SetSoundMuteStatus()
/// @brief \b Function \b Description: The main function of setting audio mute
/// @param <IN>        \b eAudioMuteType   :
///                    \b eAudioMuteSource :
/// @param <OUT>       \b NONE    :
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::SetSoundMuteStatus(AUDIOMUTETYPE_ eAudioMuteType, AUDIOMUTESOURCE_TYPE_ eAudioMuteSource)
{
    if(IsAudioInitDone() == FALSE)
    {
        printf("\n\033[0;31m [Warning!!] [%s] [%s()] IsAudioInitDone() == FALSE, Please Check!!  \033[0m \n", __FILE__, __FUNCTION__);
    }



    OS_OBTAIN_MUTEX(SDK_SetSoundMuteStatusMutex, MSOS_WAIT_FOREVER);
    //printf("========== SetSoundMuteStatus: %x , %x ===========\n", eAudioMuteType, eAudioMuteSource);
    m_muteStateIsSet = UpdateMuteFlagStatus(eAudioMuteType, eAudioMuteSource);

    if(m_muteStateIsSet == TRUE)
    {
        OS_RELEASE_MUTEX(SDK_SetSoundMuteStatusMutex);
        return;
    }

#if 0
    if (SDK_bPermanentAudioMute==1) printf("\033[1;31m \nAud Mute Status: p:%d, \033[0m ", SDK_bPermanentAudioMute); else printf("\033[1;32m \nAud Mute Status: p:%d, \033[0m ", SDK_bPermanentAudioMute);
    if (SDK_bMomentAudioMute==1) printf("\033[1;31m m:%d, \033[0m ", SDK_bMomentAudioMute); else printf("\033[1;32m m:%d, \033[0m ", SDK_bMomentAudioMute);
    if (SDK_bByUserAudioMute==1) printf("\033[1;31m u:%d, \033[0m ", SDK_bByUserAudioMute); else printf("\033[1;32m u:%d, \033[0m ", SDK_bByUserAudioMute);
    if (SDK_bBySyncAudioMute==1) printf("\033[1;31m s:%d, \033[0m ", SDK_bBySyncAudioMute); else printf("\033[1;32m s:%d, \033[0m ", SDK_bBySyncAudioMute);
    if (SDK_bByBlockAudioMute==1) printf("\033[1;31m b:%d, \033[0m ", SDK_bByBlockAudioMute); else printf("\033[1;32m b:%d, \033[0m ", SDK_bByBlockAudioMute);
    if (SDK_bByVChipAudioMute==1) printf("\033[1;31m v:%d, \033[0m ", SDK_bByVChipAudioMute); else printf("\033[1;32m v:%d, \033[0m ", SDK_bByVChipAudioMute);
    if (SDK_bInternal1AudioMute==1) printf("\033[1;31m i1:%d, \033[0m ", SDK_bInternal1AudioMute); else printf("\033[1;32m i1:%d, \033[0m ", SDK_bInternal1AudioMute);
    if (SDK_bInternal2AudioMute==1) printf("\033[1;31m i2:%d, \033[0m ", SDK_bInternal2AudioMute); else printf("\033[1;32m i2:%d, \033[0m ", SDK_bInternal2AudioMute);
    if (SDK_bInternal3AudioMute==1) printf("\033[1;31m i3:%d, \033[0m ", SDK_bInternal3AudioMute); else printf("\033[1;32m i3:%d, \033[0m ", SDK_bInternal3AudioMute);
    if (SDK_bInternal4AudioMute==1) printf("\033[1;31m i4:%d, \033[0m ", SDK_bInternal4AudioMute); else printf("\033[1;32m i4:%d, \033[0m ", SDK_bInternal4AudioMute);
    if (SDK_bByDuringLimitedTimeAudioMute==1) printf("\033[1;31m t:%d, \033[0m ", SDK_bByDuringLimitedTimeAudioMute); else printf("\033[1;32m t:%d, \033[0m ", SDK_bByDuringLimitedTimeAudioMute);
    if (SDK_bMHEGApMute==1) printf("\033[1;31m mhg:%d, \033[0m ", SDK_bMHEGApMute); else printf("\033[1;32m mhg:%d, \033[0m ", SDK_bMHEGApMute);
    if (SDK_bCIAudioMute==1) printf("\033[1;31m CI:%d, \033[0m ", SDK_bCIAudioMute); else printf("\033[1;32m CI:%d, \033[0m ", SDK_bCIAudioMute);
    if (SDK_bByScanInOutchgCHchg==1) printf("\033[1;31m sc:%d, \033[0m ", SDK_bByScanInOutchgCHchg); else printf("\033[1;32m sc:%d, \033[0m ", SDK_bByScanInOutchgCHchg);
    if (SDK_bSourceSwitchAudioMute==1) printf("\033[1;31m ss:%d, \033[0m ", SDK_bSourceSwitchAudioMute); else printf("\033[1;32m ss:%d, \033[0m ", SDK_bSourceSwitchAudioMute);
    if (SDK_bPowerOnMute==1) printf("\033[1;31m PowerOn:%d, \033[0m ", SDK_bPowerOnMute); else printf("\033[1;32m PowerOn:%d, \033[0m ", SDK_bPowerOnMute);
    if (SDK_bUsrSpkrAudioMute==1) printf("\033[1;31m User Mute Status: spkr:%d, \033[0m ", SDK_bUsrSpkrAudioMute); else printf("\033[1;32m User Mute Status: spkr:%d, \033[0m ", SDK_bUsrSpkrAudioMute);
    if (SDK_bUsrHpAudioMute==1) printf("\033[1;31m hp:%d, \033[0m", SDK_bUsrHpAudioMute); else printf("\033[1;32m hp:%d, \033[0m", SDK_bUsrHpAudioMute);
    if (SDK_bUsrScart1AudioMute==1) printf("\033[1;31m sc1:%d, \033[0m", SDK_bUsrScart1AudioMute); else printf("\033[1;32m sc1:%d, \033[0m", SDK_bUsrScart1AudioMute);
    if (SDK_bUsrScart2AudioMute==1) printf("\033[1;31m sc2:%d, \033[0m ", SDK_bUsrScart2AudioMute); else printf("\033[1;32m sc2:%d, \033[0m ", SDK_bUsrScart2AudioMute);
    if (SDK_bUsrDataInAudioMute==1) printf("\033[1;31m di:%d, \033[0m ", SDK_bUsrDataInAudioMute); else printf("\033[1;32m di:%d, \033[0m ", SDK_bUsrDataInAudioMute);
    if (SDK_bAllAudioMuteCtrl==1) printf("\033[1;31m AllMute:%d, \033[0m ", SDK_bAllAudioMuteCtrl); else printf("\033[1;32m AllMute:%d, \033[0m ", SDK_bAllAudioMuteCtrl);
    if (SDK_bUsrSpdifAudioMute==1) printf("\033[1;31m UsrSpdif:%d, \033[0m ", SDK_bUsrSpdifAudioMute); else printf("\033[1;32m UsrSpdif:%d, \033[0m ", SDK_bUsrSpdifAudioMute);
    if (SDK_bHPDetectMute==1) printf("\033[1;31m hp_d:%d, \033[0m ", SDK_bHPDetectMute); else printf("\033[1;32m hp_d:%d, \033[0m ", SDK_bHPDetectMute);
    if (SDK_bNullSourceMute==1) printf("\033[1;31m null:%d \033[0m \n", SDK_bNullSourceMute); else printf("\033[1;32m null:%d \033[0m \n", SDK_bNullSourceMute);
#if (INPUT_SOURCE_LOCK_ENABLE == 1)
    if (SDK_bInputSourceLockAudioMute==1) printf("\033[1;31m ISL:%d, \033[0m ", SDK_bInputSourceLockAudioMute); else printf("\033[1;32m ISL:%d, \033[0m ", SDK_bInputSourceLockAudioMute);
#endif
    if (SDK_SUB_bIsAudioModeChanged==1) printf("\033[1;31m S_Mode:%d, \033[0m ", SDK_SUB_bIsAudioModeChanged); else printf("\033[1;32m S_Mode:%d, \033[0m ", SDK_SUB_bIsAudioModeChanged);
    if (SDK_SUB_bPermanentAudioMute==1) printf("\033[1;31m S_p:%d, \033[0m ", SDK_SUB_bPermanentAudioMute); else printf("\033[1;32m S_p:%d, \033[0m ", SDK_SUB_bPermanentAudioMute);
    if (SDK_SUB_bMomentAudioMute==1) printf("\033[1;31m S_m:%d, \033[0m ", SDK_SUB_bMomentAudioMute); else printf("\033[1;32m S_m:%d, \033[0m ", SDK_SUB_bMomentAudioMute);
    if (SDK_SUB_bByUserAudioMute==1) printf("\033[1;31m S_u:%d, \033[0m ", SDK_SUB_bByUserAudioMute); else printf("\033[1;32m S_u:%d, \033[0m ", SDK_SUB_bByUserAudioMute);
    if (SDK_SUB_bBySyncAudioMute==1) printf("\033[1;31m S_s:%d, \033[0m ", SDK_SUB_bBySyncAudioMute); else printf("\033[1;32m S_s:%d, \033[0m ", SDK_SUB_bBySyncAudioMute);
    if (SDK_SUB_bByBlockAudioMute==1) printf("\033[1;31m S_b:%d, \033[0m ", SDK_SUB_bByBlockAudioMute); else printf("\033[1;32m S_b:%d, \033[0m ", SDK_SUB_bByBlockAudioMute);
    if (SDK_SUB_bByVChipAudioMute==1) printf("\033[1;31m S_v:%d, \033[0m ", SDK_SUB_bByVChipAudioMute); else printf("\033[1;32m S_v:%d, \033[0m ", SDK_SUB_bByVChipAudioMute);
    if (SDK_SUB_bInternal1AudioMute==1) printf("\033[1;31m S_i1:%d, \033[0m ", SDK_SUB_bInternal1AudioMute); else printf("\033[1;32m S_i1:%d, \033[0m ", SDK_SUB_bInternal1AudioMute);
    if (SDK_SUB_bInternal2AudioMute==1) printf("\033[1;31m S_i2:%d, \033[0m ", SDK_SUB_bInternal2AudioMute); else printf("\033[1;32m S_i2:%d, \033[0m ", SDK_SUB_bInternal2AudioMute);
    if (SDK_SUB_bInternal3AudioMute==1) printf("\033[1;31m S_i3:%d, \033[0m ", SDK_SUB_bInternal3AudioMute); else printf("\033[1;32m S_i3:%d, \033[0m ", SDK_SUB_bInternal3AudioMute);
    if (SDK_SUB_bInternal4AudioMute==1) printf("\033[1;31m S_i4:%d, \033[0m ", SDK_SUB_bInternal4AudioMute); else printf("\033[1;32m S_i4:%d, \033[0m ", SDK_SUB_bInternal4AudioMute);
    if (SDK_SUB_bByDuringLimitedTimeAudioMute==1) printf("\033[1;31m S_t:%d, \033[0m ", SDK_SUB_bByDuringLimitedTimeAudioMute); else printf("\033[1;32m S_t:%d, \033[0m ", SDK_SUB_bByDuringLimitedTimeAudioMute);
    if (SDK_SUB_bByScanInOutchgCHchg==1) printf("\033[1;31m S_sc:%d, \033[0m ", SDK_SUB_bByScanInOutchgCHchg); else printf("\033[1;32m S_sc:%d, \033[0m ", SDK_SUB_bByScanInOutchgCHchg);
    if (SDK_SUB_bSourceSwitchAudioMute==1) printf("\033[1;31m S_ss:%d, \033[0m ", SDK_SUB_bSourceSwitchAudioMute); else printf("\033[1;32m S_ss:%d, \033[0m ", SDK_SUB_bSourceSwitchAudioMute);
    if (SDK_SUB_bMHEGApMute==1) printf("\033[1;31m S_mhg:%d, \033[0m ", SDK_SUB_bMHEGApMute); else printf("\033[1;32m S_mhg:%d, \033[0m ", SDK_SUB_bMHEGApMute);
    if (SDK_SUB_bCIAudioMute==1) printf("\033[1;31m S_CI:%d, \033[0m \n", SDK_SUB_bCIAudioMute); else printf("\033[1;32m S_CI:%d, \033[0m \n", SDK_SUB_bCIAudioMute);
#if (INPUT_SOURCE_LOCK_ENABLE == 1)
    if (SDK_SUB_bInputSourceLockAudioMute==1) printf("\033[1;31m S_ISL:%d, \033[0m ", SDK_SUB_bInputSourceLockAudioMute); else printf("\033[1;32m S_ISL:%d, \033[0m ", SDK_SUB_bInputSourceLockAudioMute);
#endif
    if (SDK_bByUserPcmCapture1Mute==1) printf("\033[1;31m PcmCapture1:%d, \033[0m \n", SDK_bByUserPcmCapture1Mute); else printf("\033[1;32m PcmCapture1:%d, \033[0m \n", SDK_bByUserPcmCapture1Mute);
    if (SDK_bByUserPcmCapture2Mute==1) printf("\033[1;31m PcmCapture2:%d, \033[0m \n", SDK_bByUserPcmCapture2Mute); else printf("\033[1;32m PcmCapture2:%d, \033[0m \n", SDK_bByUserPcmCapture2Mute);
#endif


#if (ENABLE_LITE_SN == 0)
#if (MSTAR_TVOS == 1)
    m_internalMute = SDK_bPermanentAudioMute |
                 SDK_bMomentAudioMute |
                 SDK_bBySyncAudioMute |
                 SDK_bByBlockAudioMute |
                 SDK_bByVChipAudioMute |
                 SDK_bInternal1AudioMute |
                 SDK_bInternal2AudioMute |
                 SDK_bInternal3AudioMute |
                 SDK_bInternal4AudioMute |
                 //SDK_bCIAudioMute|                         //CI Mute just mute SCART, no need to mute SPEAKER
                 SDK_bByScanInOutchgCHchg |
                 SDK_bByDuringLimitedTimeAudioMute |
                 SDK_bSourceSwitchAudioMute |
                 SDK_bMHEGApMute |
                 SDK_bAllAudioMuteCtrl |
                 SDK_bPowerOnMute |
                 SDK_bNullSourceMute
#if (INPUT_SOURCE_LOCK_ENABLE == 1)
                 | SDK_bInputSourceLockAudioMute
#endif
                 ;

    //Don`t take speaker set mute when the input source change, just need to set internal channel mute
    m_spkrMute = SDK_bUsrSpkrAudioMute |
              //SDK_bByDuringLimitedTimeAudioMute |   //timer mute flag, in change input source have used ,should be remove
              //SDK_bSourceSwitchAudioMute |          //change input source mute flag ,should be remove
                SDK_bPowerOnMute|     // add power mute for pop issue when STR resume
                SDK_bByUserAudioMute; //563690 fix AV-out is mute by user IR key

    if(!m_internalMute)  // Release Ext_mute pin for analog out
    {
        if(m_pGPIOAumuteOut != NULL)
        {
            m_pGPIOAumuteOut->SetOff();
        }
    }

    m_DataInMute = SDK_bUsrDataInAudioMute;
    m_PcmCapture1Mute = SDK_bByUserPcmCapture1Mute;
    m_PcmCapture2Mute = SDK_bByUserPcmCapture2Mute;

#if (STB_ENABLE == 0)
    if(m_subCurInputSrc != MAPI_INPUT_SOURCE_NONE)
    {
        m_channel7Mute = SDK_SUB_bByUserChannel7Mute;
    }
    else
    {
        m_channel7Mute = SDK_bByUserChannel7Mute;
    }
#endif
    if(m_subCurInputSrc != MAPI_INPUT_SOURCE_NONE) // PIP enabled
    {
        m_hpMute = SDK_SUB_bPermanentAudioMute |
               SDK_SUB_bMomentAudioMute |
               SDK_SUB_bByUserAudioMute |
               SDK_SUB_bBySyncAudioMute |
               SDK_SUB_bByBlockAudioMute |
               SDK_SUB_bByVChipAudioMute |
               SDK_SUB_bInternal1AudioMute |
               SDK_SUB_bInternal2AudioMute |
               SDK_SUB_bInternal3AudioMute |
               SDK_SUB_bInternal4AudioMute |
               SDK_SUB_bByScanInOutchgCHchg |
               SDK_SUB_bByDuringLimitedTimeAudioMute |
               SDK_SUB_bSourceSwitchAudioMute |
               SDK_SUB_bMHEGApMute |
               SDK_SUB_bByUserChannel7Mute |
               SDK_bUsrHpAudioMute
#if (INPUT_SOURCE_LOCK_ENABLE == 1)
               | SDK_SUB_bInputSourceLockAudioMute
#endif
               ;
    }
    else  // PIP disabled ; mute event ref. main source
    {
    m_hpMute = SDK_bByUserAudioMute |             //remote mute cannot mute HP
               SDK_bPowerOnMute |
               SDK_bUsrHpAudioMute;
    }
#else
   m_spkrMute = SDK_bPermanentAudioMute |
                 SDK_bMomentAudioMute |
                 SDK_bByUserAudioMute |
                 SDK_bBySyncAudioMute |
                 SDK_bByBlockAudioMute |
                 SDK_bByVChipAudioMute |
                 SDK_bInternal1AudioMute |
                 SDK_bInternal2AudioMute |
                 SDK_bInternal3AudioMute |
                 SDK_bInternal4AudioMute |
                 //SDK_bCIAudioMute|                         //CI Mute just mute SCART, no need to mute SPEAKER
                 SDK_bByScanInOutchgCHchg |
                 SDK_bByDuringLimitedTimeAudioMute |
                 SDK_bSourceSwitchAudioMute |
                 SDK_bMHEGApMute |
                 SDK_bUsrSpkrAudioMute |
                 SDK_bHPDetectMute |
                 SDK_bNullSourceMute
#if (INPUT_SOURCE_LOCK_ENABLE == 1)
                 | SDK_bInputSourceLockAudioMute
#endif
                 ;

    if(!m_spkrMute)  // Release Ext_mute pin for analog out
    {
        if(m_pGPIOAumuteOut != NULL)
        {
            m_pGPIOAumuteOut->SetOff();
        }
    }

    if(m_subCurInputSrc != MAPI_INPUT_SOURCE_NONE) // PIP enabled
    {
        m_hpMute = SDK_SUB_bPermanentAudioMute |
                   SDK_SUB_bMomentAudioMute |
                   SDK_SUB_bByUserAudioMute |
                   SDK_SUB_bBySyncAudioMute |
                   SDK_SUB_bByBlockAudioMute |
                   SDK_SUB_bByVChipAudioMute |
                   SDK_SUB_bInternal1AudioMute |
                   SDK_SUB_bInternal2AudioMute |
                   SDK_SUB_bInternal3AudioMute |
                   SDK_SUB_bInternal4AudioMute |
                   SDK_SUB_bByScanInOutchgCHchg |
                   SDK_SUB_bByDuringLimitedTimeAudioMute |
                   SDK_SUB_bSourceSwitchAudioMute |
                   SDK_SUB_bMHEGApMute |
                   SDK_bUsrHpAudioMute
#if (INPUT_SOURCE_LOCK_ENABLE == 1)
                   | SDK_SUB_bInputSourceLockAudioMute
#endif
                   ;
    }
    else  // PIP disabled ; mute event ref. main source
    {
        m_hpMute = SDK_bPermanentAudioMute |
                   SDK_bMomentAudioMute |
                   SDK_bByUserAudioMute |             //remote mute cannot mute HP
                   SDK_bBySyncAudioMute |
                   SDK_bByBlockAudioMute |
                   SDK_bByVChipAudioMute |
                   SDK_bInternal1AudioMute |
                   SDK_bInternal2AudioMute |
                   SDK_bInternal3AudioMute |
                   SDK_bInternal4AudioMute |
                   //SDK_bCIAudioMute|                         //CI Mute just mute SCART, no need to mute SPEAKER
                   SDK_bByScanInOutchgCHchg |
                   SDK_bByDuringLimitedTimeAudioMute |
                   SDK_bSourceSwitchAudioMute |
                   SDK_bMHEGApMute |
                   SDK_bPowerOnMute |
                   SDK_bUsrHpAudioMute |
                   SDK_bNullSourceMute
#if (INPUT_SOURCE_LOCK_ENABLE == 1)
                   | SDK_bInputSourceLockAudioMute
#endif
                   ;
    }
#endif
#else
    m_spkrMute = SDK_bPermanentAudioMute |
                 SDK_bMomentAudioMute |
                 SDK_bByUserAudioMute |
                 SDK_bBySyncAudioMute |
                 SDK_bByBlockAudioMute |
                 SDK_bByVChipAudioMute |
                 SDK_bInternal1AudioMute |
                 SDK_bInternal2AudioMute |
                 SDK_bInternal3AudioMute |
                 SDK_bInternal4AudioMute |
                 //SDK_bCIAudioMute|                         //CI Mute just mute SCART, no need to mute SPEAKER
                 SDK_bByScanInOutchgCHchg |
                 SDK_bByDuringLimitedTimeAudioMute |
                 SDK_bSourceSwitchAudioMute |
                 SDK_bMHEGApMute |
                 SDK_bUsrSpkrAudioMute |
                 SDK_bHPDetectMute |
                 SDK_bNullSourceMute
#if (INPUT_SOURCE_LOCK_ENABLE == 1)
                 | SDK_bInputSourceLockAudioMute
#endif
                 ;

    if(!m_spkrMute)  // Release Ext_mute pin for analog out
    {
        if(m_pGPIOAumuteOut != NULL)
        {
            m_pGPIOAumuteOut->SetOff();
        }
    }

    m_hpMute = SDK_bPermanentAudioMute |
           SDK_bMomentAudioMute |
           SDK_bByUserAudioMute |             //remote mute cannot mute HP
           SDK_bBySyncAudioMute |
           SDK_bByBlockAudioMute |
           SDK_bByVChipAudioMute |
           SDK_bInternal1AudioMute |
           SDK_bInternal2AudioMute |
           SDK_bInternal3AudioMute |
           SDK_bInternal4AudioMute |
           //SDK_bCIAudioMute|                         //CI Mute just mute SCART, no need to mute SPEAKER
           SDK_bByScanInOutchgCHchg |
           SDK_bByDuringLimitedTimeAudioMute |
           SDK_bSourceSwitchAudioMute |
           SDK_bMHEGApMute |
           SDK_bPowerOnMute |
           SDK_bUsrHpAudioMute |
           SDK_bNullSourceMute
#if (INPUT_SOURCE_LOCK_ENABLE == 1)
           | SDK_bInputSourceLockAudioMute
#endif
           ;

    m_internalMute = SDK_bByAppMute;
#endif

    /* SIF SCART Out */
    m_scart1Mute =  SDK_bCIAudioMute |
                   SDK_bByScanInOutchgCHchg |
                   //SDK_bByDuringLimitedTimeAudioMute |   // Temp enable , should be remove
                   //SDK_bSourceSwitchAudioMute |                // Temp enable , should be remove
                   SDK_bMHEGApMute |
                   SDK_bPermanentAudioMute |
                   SDK_bByBlockAudioMute |
                   SDK_bPowerOnMute |
                   SDK_bUsrScart1AudioMute |
                   SDK_bNullSourceMute
#if (INPUT_SOURCE_LOCK_ENABLE == 1)
                   | SDK_bInputSourceLockAudioMute
#endif
                   ;

    if(SDK_eAudioCurInputSrc==MAPI_INPUT_SOURCE_ATV)
        m_scart1Mute|=SDK_bBySyncAudioMute;

    if(!m_scart1Mute)  // Release Ext_mute pin for analog out
    {
        if(m_pGPIOAumuteOut != NULL)
        {
            m_pGPIOAumuteOut->SetOff();
        }
    }

    /* Monitor out */
    m_scart2Mute = SDK_bPermanentAudioMute |
                   SDK_bMomentAudioMute |
                   //SDK_bByUserAudioMute|                     //remote mute cannot mute SCART
                   SDK_bBySyncAudioMute |
                   SDK_bByBlockAudioMute |
                   SDK_bByVChipAudioMute |
                   SDK_bInternal1AudioMute |
                   SDK_bInternal2AudioMute |
                   SDK_bInternal3AudioMute |
                   SDK_bInternal4AudioMute |
                   SDK_bCIAudioMute |
                   SDK_bByScanInOutchgCHchg |
                   SDK_bByDuringLimitedTimeAudioMute |
                   SDK_bSourceSwitchAudioMute |
                   SDK_bMHEGApMute |
                   SDK_bPowerOnMute |
                   SDK_bUsrScart2AudioMute|
                   SDK_bNullSourceMute
#if (INPUT_SOURCE_LOCK_ENABLE == 1)
                   | SDK_bInputSourceLockAudioMute
#endif
                   ;

    m_spdifMute = SDK_bPermanentAudioMute |
                  SDK_bMomentAudioMute |
#if (STB_ENABLE == 1) || (CONNECTTV_BOX == 1)
                  SDK_bByUserAudioMute |
#else
                  //SDK_bByUserAudioMute|                     //reomte mute cannot mute SPDIF
#endif
                  SDK_bBySyncAudioMute |
                  SDK_bByBlockAudioMute |
                  SDK_bByVChipAudioMute |
                  SDK_bInternal1AudioMute |
                  SDK_bInternal2AudioMute |
                  SDK_bInternal3AudioMute |
                  SDK_bInternal4AudioMute |
                  //SDK_bCIAudioMute|
                  SDK_bByScanInOutchgCHchg |
                  SDK_bByDuringLimitedTimeAudioMute |
                  SDK_bSourceSwitchAudioMute |
                  SDK_bMHEGApMute |
                  SDK_bPowerOnMute |
                  SDK_bUsrSpdifAudioMute |
                  SDK_bNullSourceMute |
                  SDK_bByAppMute //0622366: [RVU] HDMI-1 ARC does not mute on RVU input when the user mutes the TV.
#if (INPUT_SOURCE_LOCK_ENABLE == 1)
                  | SDK_bInputSourceLockAudioMute
#endif
                  ;


    if(m_hpMute == FALSE)
    {
        mapi_audio::SetSoundMute(SOUND_MUTE_HP_, E_MUTE_OFF_);
    }
    else
    {
        mapi_audio::SetSoundMute(SOUND_MUTE_HP_, E_MUTE_ON_);
    }

    if(m_scart1Mute == FALSE)
    {
        mapi_audio::SetSoundMute(SOUND_MUTE_SCART_, E_MUTE_OFF_);
    }
    else
    {
        mapi_audio::SetSoundMute(SOUND_MUTE_SCART_, E_MUTE_ON_);
    }

    if(m_scart2Mute == FALSE)
    {
        mapi_audio::SetSoundMute(SOUND_MUTE_SCART2_, E_MUTE_OFF_);
    }
    else
    {
        mapi_audio::SetSoundMute(SOUND_MUTE_SCART2_, E_MUTE_ON_);
    }

    if(m_spdifMute == TRUE)
    {
        mapi_audio::SetSoundMute(SOUND_MUTE_SPDIF_, E_MUTE_ON_);
    }
    else
    {
        mapi_audio::SetSoundMute(SOUND_MUTE_SPDIF_, E_MUTE_OFF_);
    }
#if (ENABLE_LITE_SN == 0)
#if (MSTAR_TVOS == 1)
    {
        if(m_DataInMute == FALSE)
        {
            mapi_audio::SetSoundMute(SOUND_MUTE_DATA_IN_, E_MUTE_OFF_);
        }
        else
        {
            mapi_audio::SetSoundMute(SOUND_MUTE_DATA_IN_, E_MUTE_ON_);
        }

        if(m_internalMute == FALSE)
        {
            // EosTek Patch Begin
            //mapi_audio::SetSoundMute(SOUND_MUTE_AMP_, E_MUTE_OFF_);
            // EosTek Patch End 
            mapi_audio::SetSoundMute(SOUND_MUTE_INTERNAL_MAIN_, E_MUTE_OFF_);
        }
        else
        {
             mapi_audio::SetSoundMute(SOUND_MUTE_INTERNAL_MAIN_, E_MUTE_ON_);
            // mapi_audio::SetSoundMute(SOUND_MUTE_AMP_, E_MUTE_ON_); //remove this because never need mute AMP

        }
#if (STB_ENABLE == 0)
        if(m_channel7Mute == FALSE)
        {
            mapi_audio::SetSoundMute(SOUND_MUTE_CH7_, E_MUTE_OFF_);
        }
        else
        {
            mapi_audio::SetSoundMute(SOUND_MUTE_CH7_, E_MUTE_ON_);
        }
#endif
    }
#endif
#else
    if(m_internalMute == FALSE)
    {
        mapi_audio::SetSoundMute(SOUND_MUTE_INTERNAL_MAIN_, E_MUTE_OFF_);
    }
    else
    {
        mapi_audio::SetSoundMute(SOUND_MUTE_INTERNAL_MAIN_, E_MUTE_ON_);
    }
#endif

    if(m_spkrMute == FALSE)
    {
        mapi_audio::SetSoundMute(SOUND_MUTE_TV_, E_MUTE_OFF_);
        mapi_audio::SetSoundMute(SOUND_MUTE_HDMITX_, E_MUTE_OFF_);
    }
    else
    {
        mapi_audio::SetSoundMute(SOUND_MUTE_TV_, E_MUTE_ON_);
        mapi_audio::SetSoundMute(SOUND_MUTE_HDMITX_, E_MUTE_ON_);
    }

    if(m_PcmCapture1Mute == FALSE)
    {
        mapi_audio::SetSoundMute(SOUND_MUTE_PCM_CAPTURE1_, E_MUTE_OFF_);
    }
    else
    {
        mapi_audio::SetSoundMute(SOUND_MUTE_PCM_CAPTURE1_, E_MUTE_ON_);
    }

    if(m_PcmCapture2Mute == FALSE)
    {
        mapi_audio::SetSoundMute(SOUND_MUTE_PCM_CAPTURE2_, E_MUTE_OFF_);
    }
    else
    {
        mapi_audio::SetSoundMute(SOUND_MUTE_PCM_CAPTURE2_, E_MUTE_ON_);
    }

    OS_RELEASE_MUTEX(SDK_SetSoundMuteStatusMutex);
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SND_GetSoundMuteResult()
/// @brief \b Function \b Description: The main function of setting audio mute
/// @param <IN>        \b eAudioMuteType   :
///                    \b eAudioMuteSource :
/// @param <OUT>       \b NONE    :
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
MAPI_BOOL mapi_audio_customer::SND_GetSoundMuteResult(MAPI_SOUND_AUDIO_SOURCE_TYPE audio_output_src)
{
    MAPI_BOOL status_rtn;

    switch(audio_output_src)
    {
        case AUD_SPEAKER_OUT:
            status_rtn = m_spkrMute;
            break;

        case AUD_HP_OUT:
            status_rtn = m_hpMute;
            break;

        case AUD_SCART1_OUT:
            status_rtn = m_scart1Mute;
            break;

        case AUD_SCART2_OUT:
            status_rtn = m_scart2Mute;
            break;

        case AUD_SPDIF_OUT:
            status_rtn = m_spdifMute;
            break;

        case AUD_ARC_OUT:
            status_rtn = m_spdifMute;
            break;

        case AUD_DATA_IN_PORT:
#if (ENABLE_LITE_SN == 0)
#if (MSTAR_TVOS == 1)
            status_rtn = m_DataInMute;
#else
            status_rtn = FALSE;
#endif
 #else
            status_rtn = FALSE;
#endif
            break;

        case AUD_PCM_CAPTURE1:
            status_rtn = m_PcmCapture1Mute;
            break;

        case AUD_PCM_CAPTURE2:
            status_rtn = m_PcmCapture2Mute;
            break;

        case AUD_MAIN_SOURCE_PORT:
            if(MAPI_INPUT_SOURCE_DTV == SND_GetAudioInputSource())
            {
                if((TRUE==MApi_AUDIO_GetMAD_LOCK()) && (m_internalMute ==0))
                    status_rtn = 0;
                else
                    status_rtn = 1;
            }
            else
            {
                status_rtn = m_internalMute;
            }    
            break;

        case AUD_SUB_SOURCE_PORT:
            status_rtn = m_hpMute;
            break;

        default:
            status_rtn = FALSE;
            break;
    }
    return(status_rtn);
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SND_GetSoundMuteStatusType()
/// @brief \b Function \b Description: The main function of setting audio mute
/// @param <IN>        \b eAudioMuteType   :
///                    \b eAudioMuteSource :
/// @param <OUT>       \b MAPI_SOUND_MUTE_STATUS_TYPE :
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
MAPI_BOOL mapi_audio_customer::SND_GetSoundMuteStatusType(MAPI_SOUND_MUTE_STATUS_TYPE mute_status_type)
{
    MAPI_BOOL status_rtn;

    switch(mute_status_type)
    {
        case Mute_Status_bIsAudioModeChanged:
            status_rtn = SDK_bIsAudioModeChanged;
            break;

        case Mute_Status_bPermanentAudioMute:
            status_rtn = SDK_bPermanentAudioMute;
            break;

        case Mute_Status_bMomentAudioMute:
            status_rtn = SDK_bMomentAudioMute;
            break;

        case Mute_Status_bByUserAudioMute:
            status_rtn = SDK_bByUserAudioMute;
            break;

        case Mute_Status_bBySyncAudioMute:
            status_rtn = SDK_bBySyncAudioMute;
            break;

        case Mute_Status_bByVChipAudioMute:
            status_rtn = SDK_bByVChipAudioMute;
            break;

        case Mute_Status_bByBlockAudioMute:
            status_rtn = SDK_bByBlockAudioMute;
            break;

        case Mute_Status_bInternal1AudioMute:
            status_rtn = SDK_bInternal1AudioMute;
            break;

        case Mute_Status_bInternal2AudioMute:
            status_rtn = SDK_bInternal2AudioMute;
            break;

        case Mute_Status_bInternal3AudioMute:
            status_rtn = SDK_bInternal3AudioMute;
            break;

        case Mute_Status_bInternal4AudioMute:
            status_rtn = SDK_bInternal4AudioMute;
            break;

        case Mute_Status_bByDuringLimitedTimeAudioMute:
            status_rtn = SDK_bByDuringLimitedTimeAudioMute;
            break;

        case Mute_Status_bByScanInOutchgCHchg:
            status_rtn = SDK_bByScanInOutchgCHchg;
            break;

        case Mute_Status_bMHEGApMute:
            status_rtn = SDK_bMHEGApMute;
            break;

        case Mute_Status_bCIAudioMute:
            status_rtn = SDK_bCIAudioMute;
            break;

        case Mute_Status_SourceSwitchAudioMute:
            status_rtn = SDK_bSourceSwitchAudioMute;
            break;

        case Mute_Status_bUsrSpkrAudioMute:
            status_rtn = SDK_bUsrSpkrAudioMute;
            break;

        case Mute_Status_bUsrHpAudioMute:
            status_rtn = SDK_bUsrHpAudioMute;
            break;

        case Mute_Status_bUsrSpdifAudioMute:
            status_rtn = SDK_bUsrSpdifAudioMute;
            break;

        case Mute_Status_bUsrScart1AudioMute:
            status_rtn = SDK_bUsrScart1AudioMute;
            break;

        case Mute_Status_bUsrScart2AudioMute:
            status_rtn = SDK_bUsrScart2AudioMute;
            break;

        case Mute_Status_bUsrDataInAudioMute:
            status_rtn = SDK_bUsrDataInAudioMute;
            break;

        case Mute_Status_bPowerOnMute:
            status_rtn = SDK_bPowerOnMute;
            break;

        case Mute_Status_bByUserPcmCapture1Mute:
            status_rtn = SDK_bByUserPcmCapture1Mute;
            break;

        case Mute_Status_bByUserPcmCapture2Mute:
            status_rtn = SDK_bByUserPcmCapture2Mute;
            break;

#if (INPUT_SOURCE_LOCK_ENABLE == 1)
        case Mute_Status_bInputSourceLockAudioMute:
            status_rtn = SDK_bInputSourceLockAudioMute;
            break;
#endif

        default:
            status_rtn = FALSE;
            break;
    }
    return(status_rtn);
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Description: This routine is used to set PEQ Coefficient.
/// @param <IN>        \b Band: 0~4
///                    \b Gain: 0~240
///                    \b Foh: 1~160
///                    \b Fol: 0~99
///                    \b QValue: 5~160
/// @param <OUT>       \b NONE:
/// @param <RET>       \b NONE:
/// @param <GLOBAL>    \b NONE
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::SND_SetPEQ(const MAPI_U8 Band, const MAPI_U8 Gain, const MAPI_U8 Foh, const MAPI_U8 Fol, const MAPI_U8 QValue) const
{
    float coef;
    float G, fc, Q;
    float fb,d,v0,H0,aBC,fm,fz,kb,fs;

    AUDIO_PEQ_COEF PEQCoef;
    MS_BOOL error_config = FALSE;
    PEQCoef.type        = AUDIO_COEF_PREVER_PEQ;
    PEQCoef.enable      = TRUE;
    PEQCoef.precision   = AUDIO_SINGLE_PRECISION;
    PEQCoef.scale = 1;

    if(Gain > 240)
    {
        printf("%s err! PEQ Gain should be 0~240\n", __FUNCTION__);
        error_config = TRUE;
    }

    if(Fol > 99)
    {
        printf("%s err! PEQ Fol should be 0~99\n", __FUNCTION__);
        error_config = TRUE;
    }

    if((Foh== 0) && (Fol < 50))
    {
         printf("%s err! PEQ Fo should be > 50\n", __FUNCTION__);
        error_config = TRUE;
    }

    if((QValue < 5) || (QValue > 160))
    {
        printf("%s err! PEQ QValue should be 5~160\n", __FUNCTION__);
        error_config = TRUE;
    }

    if(error_config == TRUE)
        return;

    PEQCoef.band = Band;
    fc = (float)(((int)Foh) * 100 + (int)Fol);

    for(PEQCoef.sfs = 0; PEQCoef.sfs <= 1; PEQCoef.sfs++)
    {
        if(PEQCoef.sfs == 0)
        {
            fs = 32000;
        }
        else
        {
            fs = 48000;
        }
        Q = ((float)QValue) / 10;

        G = ((float)(Gain - 120)) / 10;

        fc = (float)(((int)Foh) * 100 + (int)Fol);

        fb = fc / Q;
        d = - cos( 2 * 3.1415926 * fc / fs);
        v0 = powf(10.0, (G / 20.0));

        H0 = v0 -1;
        aBC=0;
        fm=0;
        fz=1;
        kb = tan ( 3.1415926 * fb / fs);

        if (G >=0)
        {
            fz = kb - 1;
            fm = kb + 1;
        }
        else
        {
            fz = kb - v0;
            fm = kb + v0;
        }

        aBC = fz / fm ;
        coef = 1 + (1 + aBC) * H0 / 2;
        PEQCoef.a0 = (long)(coef * 4194304);
        coef = d * (1 - aBC);
        PEQCoef.a1 = (long)(coef * 4194304);
        coef = -aBC - (1 + aBC) * H0 /2;
        PEQCoef.a2 = (long)(coef * 4194304);
        coef = d * (1 - aBC);
        PEQCoef.b1 = (long)(-coef * 4194304);
        coef = -aBC;
        PEQCoef.b2 = (long)(-coef * 4194304);

        AUDIOCUSMSG(printf("PEQ:%d %d %d %d\n",Gain,Foh,Fol,QValue));
        AUDIOCUSMSG(printf("a0:%x, ",PEQCoef.a0));
        AUDIOCUSMSG(printf("a1:%x, ",PEQCoef.a1));
        AUDIOCUSMSG(printf("a2:%x, ",PEQCoef.a2));
        AUDIOCUSMSG(printf("b1:%x, ",PEQCoef.b1));
        AUDIOCUSMSG(printf("b2:%x, ",PEQCoef.b2));
        AUDIOCUSMSG(printf("\n"));
        MApi_AUDIO_EnablePEQ(TRUE);
        MApi_AUDIO_SetPEQCoef(&PEQCoef);
    }
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Description: This routine is used to set HLPF Coefficient.
/// @param <IN>        \b Band: 0~7
///                    \b Type: 0:LPF 1:HPF
///                    \b Foh: 1~160
///                    \b Fol: 0~99
/// @param <OUT>       \b NONE:
/// @param <RET>       \b TRUE ( SUCCESS ) / FALSE (FAIL)
/// @param <GLOBAL>    \b NONE
//-------------------------------------------------------------------------------------------------
MAPI_BOOL mapi_audio_customer::SND_SetHLPF(const MAPI_U8 u8Band, const MAPI_U8 u8Type, const MAPI_U8 u8Foh, const MAPI_U8 u8Fol)
{
    float coef[5];
    float dem;
    float fc,Q;
    float k;
    float kpow2;
    AUDIO_PEQ_COEF PEQCoef;
    //PEQCoef.scale = 0;
    MAPI_BOOL bRet = TRUE;
    PEQCoef.type = AUDIO_COEF_HPF;

    //printf("\r\n===SND_SetHLPF Band=%X Type=%d Foh=%d Fol=%d ====",(int)u8Band,(int)u8Type,(int)u8Foh,(int)u8Fol);

    if(u8Foh * 100 + u8Fol < 10)
    {
        printf("%s err! HPF fc should be 50~20000\n", __FUNCTION__);
        bRet = FALSE;
    }

    if(u8Foh>200)
    {
        printf("%s err! HPF Foh should be 0~200\n", __FUNCTION__);
        bRet = FALSE;
    }

    if(u8Fol>99)
    {
        printf("%s err! HPF Fol should be 0~99\n", __FUNCTION__);
        bRet = FALSE;
    }

    if(bRet == FALSE)
        return bRet;

    PEQCoef.band = u8Band;
    fc = (float)(((int)u8Foh)*100+(int)u8Fol);

    PEQCoef.sfs = 1;
    //PEQCoef.scale = 1;

    k = tan((3.1415926*fc)/48000);
    Q = 1/sqrt(2);

    kpow2 = k*k;

    if (u8Type == 0)
    {
        //lpf
        dem = 1 + k/Q + kpow2;
        coef[0] = kpow2/dem;
        coef[1] = 2*kpow2/dem;
        coef[2] = kpow2/dem;
        coef[3] = 2*(kpow2-1)/dem;
        coef[4] = (1 - k/Q + kpow2)/dem;

        PEQCoef.a0 = (long)(coef[0] * 8388608/2);
        PEQCoef.a1 = (long)(coef[1] * 8388608/2);
        PEQCoef.a2 = (long)(coef[2] * 8388608/2);
        PEQCoef.b1 = (long)(-coef[3] * 8388608/2);
        PEQCoef.b2 = (long)(-coef[4] * 8388608/2);
    }
    else
    {
        //hpf
        dem = 1 + k/Q + kpow2;
        coef[0] = 1/dem;
        coef[1] = -2/dem;
        coef[2] = 1/dem;
        coef[3] = 2*(kpow2-1)/dem;
        coef[4] = (1 - k/Q + kpow2)/dem;

        PEQCoef.a0 = (long)(coef[0] * 8388608/2);
        PEQCoef.a1 = (long)(coef[1] * 8388608/2);
        PEQCoef.a2 = (long)(coef[2] * 8388608/2);
        PEQCoef.b1 = (long)(-coef[3] * 8388608/2);
        PEQCoef.b2 = (long)(-coef[4] * 8388608/2);
    }
    MApi_AUDIO_SetPEQCoef(&PEQCoef);
    return bRet;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Description: This routine is used to set TONE Coefficient.
/// @param <IN>        \b Type: 0:Bass 1:Treble
///                    \b Gain  : -200~200 (-20dB~20dB)
///                    \b Fo    : 10 ~ 16000(Hz)
///                    \b QValue: 50~90 (0.5~0.9)
/// @param <OUT>       \b NONE  :
/// @param <RET>       \b TRUE ( SUCCESS ) / FALSE (FAIL)
/// @param <GLOBAL>    \b NONE
//-------------------------------------------------------------------------------------------------
MAPI_BOOL mapi_audio_customer::SND_SetTONE(const MAPI_U8 Type, const MAPI_S16 Gain, const MAPI_U16 Fo, const MAPI_U8 QValue)
{
    float coef[5];
    float v0,dem,dem1,dem2;
    float G,fc,Q;

    float k;
    float kpow2,sqrtv0;
    AUDIO_PEQ_COEF PEQCoef;
    float maxCoef;
    int i;
    PEQCoef.scale = 0;  //CID 162294  Uninitialized scalar variable

    if(QValue < 50 || QValue > 90)
    {
        return FALSE;
    }
    if(Fo < 10 || Fo > 16000)
    {
        return FALSE;
    }
    if(Gain < -200 || Gain > 200 )
    {
        return FALSE;
    }

    fc = (float)Fo;

    if(Type == 0) //bass
    {
        PEQCoef.type = AUDIO_COEF_BASS;
        for(PEQCoef.sfs=0; PEQCoef.sfs<=1; PEQCoef.sfs++)
        {
            if(PEQCoef.sfs)
            {
                AUDIOCUSMSG(printf("48k: %x\r\n",1));
                k = tan((3.1415926*fc)/48000);
            }
            else
            {
                AUDIOCUSMSG(printf("32k: %x\r\n",0));
                k = tan((3.1415926*fc)/32000);
            }

            Q = ((float)QValue)/100.0;
            G = ((float)Gain)/10.0;
            fc = (float)Fo;

            kpow2 = k*k;

            AUDIOCUSMSG(printf("Gain: %d\r\n",(int)Gain));
            AUDIOCUSMSG(printf("fc: %d\r\n",(int)fc));
            AUDIOCUSMSG(printf("Q: %d\r\n",(int)Q));

            v0 = powf (10.0, fabs(G/20.0));
            sqrtv0 = sqrt(v0);

            if (G >= 0)
            {
                //bass,boost
                dem = 1 + k/Q + kpow2;
                coef[0] = (1+sqrtv0*k/Q + v0*kpow2)/dem;
                PEQCoef.a0 = (long)(coef[0] * 4194304);
                coef[0] = (2 *(v0*kpow2 - 1))/dem;
                PEQCoef.a1 = (long)(coef[0] * 4194304);
                coef[0] = (1 - sqrtv0*k/Q + v0*kpow2)/dem;
                PEQCoef.a2 = (long)(coef[0] * 4194304);
                coef[0] = 2*(kpow2 - 1)/dem;
                PEQCoef.b1 = (long)(-coef[0] * 4194304);
                coef[0] = (1 - k/Q + kpow2)/dem;
                PEQCoef.b2 = (long)(-coef[0] * 4194304);
            }
            else
            {
                //bass,cut
                dem = 1 + sqrtv0*k/Q + v0*kpow2;
                coef[0] = (1 + k/Q + kpow2) / dem;
                PEQCoef.a0 = (long)(coef[0] * 4194304);
                coef[0] = (2 * (kpow2 - 1) ) / dem;
                PEQCoef.a1 = (long)(coef[0] * 4194304);
                coef[0] = (1 - k/Q + kpow2) / dem;
                PEQCoef.a2 = (long)(coef[0] * 4194304);
                coef[0] = (2 * (v0*kpow2 - 1) ) / dem;
                PEQCoef.b1 = (long)(-coef[0] * 4194304);
                coef[0] = (1 - sqrtv0*k/Q + v0*kpow2) / dem;
                PEQCoef.b2 = (long)(-coef[0] * 4194304);
            }

            MApi_AUDIO_SetPEQCoef(&PEQCoef);

            AUDIOCUSMSG(printf("a0:%x, ",PEQCoef.a0));
            AUDIOCUSMSG(printf("a1:%x, ",PEQCoef.a1));
            AUDIOCUSMSG(printf("a2:%x, ",PEQCoef.a2));
            AUDIOCUSMSG(printf("b1:%x, ",PEQCoef.b1));
            AUDIOCUSMSG(printf("b2:%x, ",PEQCoef.b2));
            AUDIOCUSMSG(printf("\n"));
        }
    }
    else //Treble
    {
        PEQCoef.type = AUDIO_COEF_TREBLE;
        for(PEQCoef.sfs=0; PEQCoef.sfs<=1; PEQCoef.sfs++)
        {
            if(PEQCoef.sfs)
            {
                AUDIOCUSMSG(printf("48k: %x\r\n",1));
                k = tan((3.1415926*fc)/48000);
            }
            else
            {
                AUDIOCUSMSG(printf("32k: %x\r\n",0));
                k = tan((3.1415926*fc)/32000);
            }

            Q = ((float)QValue)/100.0;
            G = ((float)Gain)/10.0;
            fc = (float)Fo;

            kpow2 = k*k;
            AUDIOCUSMSG(printf("Gain: %d\r\n",(int)Gain));
            AUDIOCUSMSG(printf("fc: %d\r\n",(int)fc));
            AUDIOCUSMSG(printf("Q: %d\r\n",(int)Q));

            v0 = powf (10.0, fabs(G/20.0));
            sqrtv0 = sqrt(v0);

            if (G >= 0)
            {
                //treble,boost
                dem = 1 + k/Q + kpow2;
                coef[0] = (v0 + sqrtv0*k/Q + kpow2) / dem;
                coef[1] = (2 * (kpow2 - v0) ) / dem;
                coef[2] = (v0 - sqrtv0*k/Q + kpow2) / dem;
                coef[3] = (2 * (kpow2 - 1) ) / dem;
                coef[4] = (1 - k/Q + kpow2) / dem;
            }
            else
            {
                //treble,cut
                dem1 = v0 + sqrtv0*k/Q + kpow2;
                dem2 = 1 + k/(sqrtv0*Q) + (kpow2)/v0;
                coef[0] = (1 + k/Q + kpow2) / dem1;
                coef[1] = (2 * (kpow2 - 1) ) / dem1;
                coef[2] = (1 - k/Q + kpow2) / dem1;
                coef[3] = (2 * ((kpow2)/v0 - 1) ) / dem2;
                coef[4] = (1 - k/(sqrtv0*Q) + (kpow2)/v0) / dem2;
            }

            maxCoef = (float)fabs((double)coef[0]);
            for(i=1; i<5; i++)
            {
                if((float)fabs((double)coef[i]) > maxCoef)
                {
                    maxCoef = (float)fabs((double)coef[i]);
                }
            }

            if(maxCoef <= 1)
            {
                if(fc < 200)
                    PEQCoef.scale = 0;
                else
                    PEQCoef.scale = 1;
            }
            else if(maxCoef <= 2)
            {
                PEQCoef.scale = 1;
            }
            else if(maxCoef <= 4)
            {
                PEQCoef.scale = 2;
            }
            else if(maxCoef <= 8)
            {
                PEQCoef.scale = 3;
            }
            else if(maxCoef <= 16)
            {
                PEQCoef.scale = 4;
            }
            else if(maxCoef <= 32)
            {
                PEQCoef.scale = 5;
            }

            PEQCoef.a0 = (long)(coef[0] * 8388608/pow(2, PEQCoef.scale));
            PEQCoef.a1 = (long)(coef[1] * 8388608/pow(2, PEQCoef.scale));
            PEQCoef.a2 = (long)(coef[2] * 8388608/pow(2, PEQCoef.scale));
            PEQCoef.b1 = (long)(-coef[3] * 8388608/pow(2, PEQCoef.scale));
            PEQCoef.b2 = (long)(-coef[4] * 8388608/pow(2, PEQCoef.scale));

            MApi_AUDIO_SetPEQCoef(&PEQCoef);

            AUDIOCUSMSG(printf("a0:%x, ",PEQCoef.a0));
            AUDIOCUSMSG(printf("a1:%x, ",PEQCoef.a1));
            AUDIOCUSMSG(printf("a2:%x, ",PEQCoef.a2));
            AUDIOCUSMSG(printf("b1:%x, ",PEQCoef.b1));
            AUDIOCUSMSG(printf("b2:%x, ",PEQCoef.b2));
            AUDIOCUSMSG(printf("\n"));
        }
    }
    return TRUE;
}
//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Description: This routine is to initialize PEQ.
/// @param <IN>        \b enable
/// @param <IN>        \b Number of PEQ Band
/// @param <OUT>       \b NONE
/// @param <RET>       \b NONE
/// @param <GLOBAL>    \b NONE
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::SND_InitPEQ(const MAPI_BOOL bEnable, const AUDIO_PEQ_BAND_NUM BandNumber) const
{
    MAPI_U8 band = 0;
    MAPI_U8 NumPEqBand = 3;

    if(bEnable)
    {
        if(BandNumber == PEQ_5_BANDS)
        {
            NumPEqBand = 5;
        }
        else if(BandNumber == PEQ_RESERVED)
        {
            NumPEqBand = sizeof(PEQParam) / sizeof(AUDIO_PEQ_PARAM);
        }

        for(band = 0; band < NumPEqBand; band++)
        {
            SND_SetPEQ(PEQParam[band].Band, PEQParam[band].Gain, PEQParam[band].Foh, PEQParam[band].Fol, PEQParam[band].QValue);
        }
    }
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: MMA_Init()
/// @brief \b Function \b Description: Multi Media Audio (MMA) initialize
//                     \b              Free and Stop All Audio decoder
/// @param <IN>        \b NONE
/// @return <OUT>      \b NONE
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::MMA_Init(void)
{
    MAPI_S8 adec_idx;

    DBG_MMA_MSG(printf("%s\n", __FUNCTION__));

    /* STOP and Free All audio decoder */
    for ( adec_idx = AUDIO_DEC_ID1; adec_idx < AUDIO_DEC_MAX; adec_idx++ )
    {
        MMA_AudioControl((MMA_AUDIO_DEC_ID)adec_idx, MMA_STOP_);
        MMA_FreeAudioDecoder((MMA_AUDIO_DEC_ID)adec_idx);
    }

    MApi_AUDIO_SetCommAudioInfo(Audio_Comm_infoType_MMA_init, 0, 0);
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: MMA_Exit()
/// @brief \b Function \b Description: Multi Media Audio (MMA) exit
//                     \b              Stop and finish All audio decoder
/// @param <IN>        \b NONE
/// @param <OUT>       \b NONE
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::MMA_Exit(void)
{
    MAPI_S8 adec_idx;

    DBG_MMA_MSG(printf("%s\n", __FUNCTION__));

    /* STOP and Free All audio decoder */
    for ( adec_idx = AUDIO_DEC_ID1; adec_idx < AUDIO_DEC_MAX; adec_idx++ )
    {
        MMA_AudioControl((MMA_AUDIO_DEC_ID)adec_idx, MMA_STOP_);
        MMA_FreeAudioDecoder((MMA_AUDIO_DEC_ID)adec_idx);
    }

    MApi_AUDIO_SetCommAudioInfo(Audio_Comm_infoType_MMA_finish, 0, 0);
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: MMA_ForceAudioDecoder()
/// @brief \b Function \b Description: Force to use selected Audio decoder manually
/// @param <IN>        \b MMA_AUDIO_DEC_ID : The audio decoder which system want to use manually
///                    \b MMA_AudioType_ :   The audio type which system want to use
/// @param <OUT>       \b MAPI_BOOL        : True / False : success or fail
///                    \b                    If selected decoder can not support the audio type,
///                    \b                    it will return FALSE.
/// @param <GLOBAL>    \b NONE
//-------------------------------------------------------------------------------------------------
MAPI_BOOL mapi_audio_customer::MMA_ForceAudioDecoder(MMA_AUDIO_DEC_ID audio_dec_id, MMA_AudioType_ audType)
{
    MAPI_U16 info_index;
    MAPI_BOOL ret = false;

    m_mmaAudioDecType = audType;
    DBG_MMA_MSG(printf("%s,aid=%d, type=%x\n", __FUNCTION__, audio_dec_id, audType));

    info_index = Audio_Comm_infoType_ADEC1_capability + (audio_dec_id * 0x30);
    if(MApi_AUDIO_GetCommAudioInfo((Audio_COMM_infoType)info_index)&audType)
    {
        DBG_MMA_MSG(printf("%s:dec %d is selected\n", __FUNCTION__, audio_dec_id));
        info_index = Audio_Comm_infoType_ADEC1_setAudioDecoder + (audio_dec_id * 0x30);
        MMA_AudioControl((MMA_AUDIO_DEC_ID)audio_dec_id, MMA_STOP_);
        MApi_AUDIO_SetCommAudioInfo((Audio_COMM_infoType)info_index, audType, 0);

        MMA_audInUsed[audio_dec_id] = true;
        MMA_audType[audio_dec_id] = audType;
        MMA_decDataTag[audio_dec_id] = 0xFF;
        ret = true;
    }

    return ret;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: MMA_AllocAudioDecoder()
/// @brief \b Function \b Description: Allocate avalible audio decoder which can
//                     \b              support assigned audio type for system.
//                     \b              If no avalible audio decoder can support
//                     \b              this audio type, the return value will be AUDIO_ID_NONE.
/// @param <IN>        \b MMA_AudioType_   : The audio type which system want to use
/// @param <OUT>       \b MMA_AUDIO_DEC_ID : The avaliable audio decoder ID for playing the audio type
///                    \b                system wanted.
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
MMA_AUDIO_DEC_ID mapi_audio_customer::MMA_AllocAudioDecoder(MMA_AudioType_ audType)
{
    MAPI_S8 adec_idx, select_idx = -1;
    MAPI_U16 info_index;

    DBG_MMA_MSG(printf("%s, type=%x\n", __FUNCTION__, audType));

    /* search avalible and supported audio decoder */
    for(adec_idx = AUDIO_DEC_MAX - 1; adec_idx >= 0; adec_idx--)
    {
        if(select_idx >= 0)
        {
            continue;
        }

        info_index = Audio_Comm_infoType_ADEC1_capability + (adec_idx * 0x30);

        if(MApi_AUDIO_GetCommAudioInfo((Audio_COMM_infoType)info_index)&audType)
        {
            if(MMA_audInUsed[adec_idx] == false)
            {
                select_idx = adec_idx;
            }
            else
            {
                DBG_MMA_MSG(printf("%s:dec %d is in used\n", __FUNCTION__, adec_idx));
            }
        }
        else
        {
            DBG_MMA_MSG(printf("%s:dec %d is not supported\n", __FUNCTION__, adec_idx));
        }
    }

    // TODO: tempotory set to ID1 because ID2 not ready yet
    select_idx = AUDIO_DEC_ID1;

    /* if find avaliable decoder, set and init selected audio decoder */
    if(select_idx != -1)
    {
        DBG_MMA_MSG(printf("%s:dec %d is selected\n", __FUNCTION__, select_idx));
        info_index = Audio_Comm_infoType_ADEC1_setAudioDecoder + (select_idx * 0x30);
        MMA_AudioControl((MMA_AUDIO_DEC_ID)select_idx, MMA_STOP_);
        MApi_AUDIO_SetCommAudioInfo((Audio_COMM_infoType)info_index, audType, 0);

        MMA_audInUsed[select_idx] = true;
        MMA_audType[select_idx]   = audType;
        MMA_decDataTag[select_idx] = 0xFF;
    }

    return (MMA_AUDIO_DEC_ID)select_idx;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: MMA_FreeAudioDecoder()
/// @brief \b Function \b Description: Free the selected audio decoder
/// @param <IN>        \b MMA_AUDIO_DEC_ID : The audio decoder ID which system want to release
/// @param <OUT>       \b MAPI_BOOL        : True / False : success or fail
///                    \b                system wanted.
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
MAPI_BOOL mapi_audio_customer::MMA_FreeAudioDecoder(MMA_AUDIO_DEC_ID audio_dec_id)
{
    if ( (audio_dec_id <= AUDIO_DEC_INVALID) || (audio_dec_id >= AUDIO_DEC_MAX) )
    {
        printf("%s: Err! AID is out of Range\n", __FUNCTION__);
        return false;
    }

    if(MMA_audInUsed[audio_dec_id])
    {
        DBG_MMA_MSG(printf("%s:dec %d is released\n", __FUNCTION__, audio_dec_id));
    }

    MMA_AudioControl(audio_dec_id, MMA_STOP_);
    usleep(2 * 1000);
    MMA_decDataTag[audio_dec_id] = 0xFF;
    MMA_audType[audio_dec_id]   = Audio_DEC_NULL_;
    MMA_audInUsed[audio_dec_id] = false;
    return true;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: MMA_AudioControl()
/// @brief \b Function \b Description: Set Play / Stop / Pause / FF2x / ... to Audio decoder
///                    \b
/// @param <IN>        \b MMA_AUDIO_DEC_ID : The audio decoder ID which system want to control
///                    \b MM_AUDIO_CONTROL : Play / Stop / Pause / FF2x /
/// @param <OUT>       \b NONE
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::MMA_AudioControl(MMA_AUDIO_DEC_ID audio_dec_id, MMA_AUDIO_CONTROL_ aud_control)
{
    MAPI_U16 info_index;

    DBG_MMA_MSG(printf("%s: aid:%d, ctrl:%d\n", __FUNCTION__, audio_dec_id, aud_control));
    if((audio_dec_id < 0) || (audio_dec_id >= AUDIO_DEC_MAX))
    {
        printf("%s: Err! AID is out of Range\n", __FUNCTION__);
        ASSERT(0);
        return;
    }

    info_index = Audio_Comm_infoType_ADEC1_playControl + (audio_dec_id * 0x30);
    MApi_AUDIO_SetCommAudioInfo((Audio_COMM_infoType)info_index, aud_control, 0);

#if (STB_ENABLE == 1)
    if(aud_control == MMA_STOP_)
    {
        // mantis 138014
        usleep(30 * 1000); // 30 ms, suggested by audio team. Stop and play command have about 30ms time duration to let audio DSP recognize stop command.
    }
#endif
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: MMA_checkAudioDataRequest()
/// @brief \b Function \b Description: Check if decoder need more Audio Raw data to play.
///                    \b              If yes, this function will provide the Address and Size information for system.
///                    \b              System can prepare the audio raw data for audio decoder according this info.
/// @param <IN>        \b MMA_AUDIO_DEC_ID : The audio decoder ID which system want to check the request
///                    \b pU32WrtAddr : If Audio request Data, this func will set the address info in this variable
///                    \b pU32WrtBytes: If Audio request Data, this func will set the size info in this variable
/// @param <OUT>       \b MAPI_BOOL : If True, DSP need more data for decode.
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
MAPI_BOOL mapi_audio_customer::MMA_checkAudioDataRequest(MMA_AUDIO_DEC_ID audio_dec_id, MAPI_U32 *pU32WrtAddr, MAPI_U32 *pU32WrtBytes)
{
    MAPI_BOOL ret = FALSE;
    MAPI_U16 info_index1, info_index2, info_index3;

    if((audio_dec_id < 0) || (audio_dec_id >= AUDIO_DEC_MAX))
    {
        printf("%s: Err! AID is out of Range\n", __FUNCTION__);
        return ret;
    }

    info_index1 = Audio_Comm_infoType_ADEC1_esBuf_reqFlag + (audio_dec_id * 0x30);
    ret = MApi_AUDIO_GetCommAudioInfo((Audio_COMM_infoType) info_index1);

    if(ret)
    {
        info_index2 = Audio_Comm_infoType_ADEC1_esBuf_reqAddr + (audio_dec_id * 0x30);
        info_index3 = Audio_Comm_infoType_ADEC1_esBuf_reqSize + (audio_dec_id * 0x30);
        *pU32WrtAddr  = MS_PA2KSEG1(MApi_AUDIO_GetCommAudioInfo((Audio_COMM_infoType) info_index2));
        *pU32WrtBytes = MApi_AUDIO_GetCommAudioInfo((Audio_COMM_infoType) info_index3);
        DBG_MMA_MSG(printf("%s:dec %d, addr:0x%x, size:0x%x\n", __FUNCTION__, audio_dec_id, *pU32WrtAddr, *pU32WrtBytes));
    }

    return ret;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: MMA_informAudioDataReady()
/// @brief \b Function \b Description: After system refill the Audio Raw data which audio decoder needed,
///                    \b              call this function to inform DSP Data is Ready.
/// @param <IN>        \b MMA_AUDIO_DEC_ID : The audio decoder ID which system want to check the request
///                    \b paddingZeroSize : when input data is not enough, we need to padding zero to request size,
/// @param <OUT>       \b NONE
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::MMA_informAudioDataReady(MMA_AUDIO_DEC_ID audio_dec_id, MAPI_U32 paddingZeroSize)
{
    MAPI_U16 info_index;

    if((audio_dec_id < 0) || (audio_dec_id >= AUDIO_DEC_MAX))
    {
        printf("%s: Err! AID is out of Range\n", __FUNCTION__);
        return;
    }

    DBG_MMA_MSG(printf("%s:dec %d, tag:%x, size:%x\n", __FUNCTION__, audio_dec_id, MMA_decDataTag[audio_dec_id], paddingZeroSize));
    info_index = Audio_Comm_infoType_ADEC1_esBuf_informDataRdy + (audio_dec_id * 0x30);
    MApi_AUDIO_SetCommAudioInfo((Audio_COMM_infoType) info_index, MMA_decDataTag[audio_dec_id], paddingZeroSize);
    MMA_decDataTag[audio_dec_id]++;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: MMA_checkAudioPlayDone()
/// @brief \b Function \b Description: After the audio RAW data is used out,
///                    \b              system can call this function to check whether audio decoder is finished.
/// @param <IN>        \b MMA_AUDIO_DEC_ID : The audio decoder ID which system want to check the request
/// @param <OUT>       \b NONE
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
MAPI_BOOL mapi_audio_customer::MMA_checkAudioPlayDone(MMA_AUDIO_DEC_ID audio_dec_id)
{
    MAPI_U16 info_index;
    MAPI_BOOL ret = false;

    if((audio_dec_id < 0) || (audio_dec_id >= AUDIO_DEC_MAX))
    {
        printf("%s: Err! AID is out of Range\n", __FUNCTION__);
        return false;
    }

    info_index = Audio_Comm_infoType_ADEC1_pcmBuf_currLevel + (audio_dec_id * 0x30);

    if(MApi_AUDIO_GetCommAudioInfo((Audio_COMM_infoType) info_index) < 0x80)
        ret = true;

    DBG_MMA_MSG(printf("%s:%d(%x)\n", __FUNCTION__, ret, (MAPI_U16)MApi_AUDIO_GetCommAudioInfo((Audio_COMM_infoType) info_index)));
    return ret;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: MMA_getAudioInfo()
/// @brief \b Function \b Description: Get Audio decoder information
/// @param <IN>        \b MMA_AUDIO_DEC_ID : The audio decoder ID which system want to check the request
/// @param <IN>        \b MM_AUDIO_INFO_TYPE : The info type want to get, check enum ""
/// @param <OUT>       \b NONE
//-------------------------------------------------------------------------------------------------
MAPI_U64 mapi_audio_customer::MMA_getAudioInfo(MMA_AUDIO_DEC_ID audio_dec_id, MM_AUDIO_INFO_TYPE  infoType)
{
    MAPI_U64 result = 0;
    MAPI_U16 info_index;

    if ( (audio_dec_id <= AUDIO_DEC_INVALID) || (audio_dec_id >= AUDIO_DEC_MAX) )
    {
        printf("%s: Err! AID is out of Range\n", __FUNCTION__);
        return 0;
    }

    if(infoType < MMA_COMMINFO_END)
    {
        /* common info */
        switch(infoType)
        {
            case MMA_COMMINFO_DEC_AUDTYPE:
                result = MMA_audType[audio_dec_id];
                break;

            case MMA_COMMINFO_DEC_STATUS:
                info_index = Audio_Comm_infoType_ADEC1_currAudDecStatus + (0x30 * audio_dec_id);
                result = MApi_AUDIO_GetCommAudioInfo((Audio_COMM_infoType) info_index);
                break;

            case MMA_COMMINFO_REQ_DATASZ:
                info_index = Audio_Comm_infoType_ADEC1_reqDataSize + (0x30 * audio_dec_id);
                result = MApi_AUDIO_GetCommAudioInfo((Audio_COMM_infoType) info_index);
                break;

            case MMA_COMMINFO_DEC_SMPRATE:
                info_index = Audio_Comm_infoType_ADEC1_sampleRate + (0x30 * audio_dec_id);
                result = MApi_AUDIO_GetCommAudioInfo((Audio_COMM_infoType) info_index);
                break;

            case MMA_COMMINFO_DEC_BITRATE:
                info_index = Audio_Comm_infoType_ADEC1_bitRate + (0x30 * audio_dec_id);
                result = MApi_AUDIO_GetCommAudioInfo((Audio_COMM_infoType) info_index);
                break;

            case MMA_COMMINFO_DEC_ACMOD:
                info_index = Audio_Comm_infoType_ADEC1_acmod + (0x30 * audio_dec_id);
                result = MApi_AUDIO_GetCommAudioInfo((Audio_COMM_infoType) info_index);
                break;

            case MMA_COMMINFO_DEC_OK_FRMCNT:
                info_index = Audio_Comm_infoType_ADEC1_okFrmCnt + (0x30 * audio_dec_id);
                result = MApi_AUDIO_GetCommAudioInfo((Audio_COMM_infoType) info_index);
                break;

            case MMA_COMMINFO_DEC_ERR_FRMCNT:
                info_index = Audio_Comm_infoType_ADEC1_errFrmCnt + (0x30 * audio_dec_id);
                result = MApi_AUDIO_GetCommAudioInfo((Audio_COMM_infoType) info_index);
                break;

            case MMA_COMMINFO_DEC_TimeStamp:
                info_index = Audio_Comm_infoType_ADEC1_1ms_timeStamp + (0x30 * audio_dec_id);
                result = MApi_AUDIO_GetCommAudioInfo((Audio_COMM_infoType) info_index);
                break;

            case MMA_COMMINFO_DEC_33BIT_PTS:
                info_index = Audio_Comm_infoType_ADEC1_33bit_PTS + (0x30 * audio_dec_id);
                result = MApi_AUDIO_GetCommAudioInfo((Audio_COMM_infoType) info_index);
                break;

            case MMA_COMMINFO_DEC_33BIT_STCPTS_DIFF:
                info_index = Audio_Comm_infoType_ADEC1_33bit_STCPTS_DIFF + (0x30 * audio_dec_id);
                result = MApi_AUDIO_GetCommAudioInfo((Audio_COMM_infoType) info_index);
                break;

            case MMA_COMMINFO_DEC_ES_CURR_LEVEL:                 //UNIY: byte
                info_index = Audio_Comm_infoType_ADEC1_esBuf_currLevel + (0x30 * audio_dec_id);
                result = MApi_AUDIO_GetCommAudioInfo((Audio_COMM_infoType) info_index);
                break;

            case MMA_COMMINFO_DEC_PCM_CURR_LEVEL:                //UNIT: BYTES  / TIME JUMP
                info_index = Audio_Comm_infoType_ADEC1_pcmBuf_currLevel + (0x30 * audio_dec_id);
                result = MApi_AUDIO_GetCommAudioInfo((Audio_COMM_infoType) info_index);
                break;

            default:
                break;
        }
    }
    else
    {
        /* decoder info */
        info_index = Audio_Comm_infoType_ADEC1_getDecInfo1 + (0x30 * audio_dec_id) + ((MAPI_U16)infoType & 0xF);
        result = MApi_AUDIO_GetCommAudioInfo((Audio_COMM_infoType) info_index);
    }

    return result;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: MMA_getAudioInfo2()
/// @brief \b Function \b Description: Get Audio decoder information
/// @param <IN>        \b MMA_AUDIO_DEC_ID : The audio decoder ID which system want to check the request
/// @param <IN>        \b MM_AUDIO_INFO_TYPE : The info type want to get, check enum ""
/// @param <OUT>       \b NONE
//-------------------------------------------------------------------------------------------------
MAPI_BOOL mapi_audio_customer::MMA_getAudioInfo2(MMA_AUDIO_DEC_ID audio_dec_id, MM_AUDIO_INFO_TYPE infoType, MAPI_U32 info)
{
    MAPI_BOOL ret = true;
    AUDIO_DEC_ID dec_id;

    if ( (audio_dec_id <= AUDIO_DEC_INVALID) || (audio_dec_id >= AUDIO_DEC_MAX) )
    {
        printf("%s: Err! AID is out of Range\n", __FUNCTION__);
        return false;
    }

    dec_id = _AIdToAudioDriverId ( audio_dec_id );

    if ( ret == true )
    {
        /* common info */
        switch ( infoType )
        {
            case MMA_COMMINFO_DEC_AUDTYPE:
                {
                MMA_AudioType_ * pRet = (MMA_AudioType_ *) info;
                *pRet = MMA_audType[audio_dec_id];
                }
                break;

            case MMA_COMMINFO_DEC_STATUS:
                ret =  MApi_AUDIO_GetAudioInfo2(dec_id, Audio_infoType_DecStatus, (void *) info);
                break;

            case MMA_COMMINFO_DEC_TimeStamp:
                ret =  MApi_AUDIO_GetAudioInfo2(dec_id, Audio_infoType_1ms_timeStamp, (void *) info);
                break;

            case MMA_COMMINFO_DEC_ES_CURR_LEVEL:                 //UNIY: byte
                ret =  MApi_AUDIO_GetAudioInfo2(dec_id, Audio_infoType_esBuf_currLevel, (void *) info);
                break;

            case MMA_COMMINFO_DEC_PCM_CURR_LEVEL:                //UNIT: BYTES  / TIME JUMP
                ret =  MApi_AUDIO_GetAudioInfo2(dec_id, Audio_infoType_pcmBuf_currLevel, (void *) info);
                break;

            default:
                printf("%s: Err! COMMAND(%d) is out of Range\n", __FUNCTION__, infoType);
                ret = false;
                break;
        }
    }

    return ret;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: MMA_setAudioParam()
/// @brief \b Function \b Description: set Audio decoder parameter
/// @param <IN>        \b MMA_AUDIO_DEC_ID : The audio decoder ID which system want to check the request
/// @param <IN>        \b MM_AUDIO_PARAM_TYPE : The parameter type want to set, check enum ""
/// @param <IN>        \b param_value : The parameter value want to set
/// @param <OUT>       \b NONE
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::MMA_setAudioParam(MMA_AUDIO_DEC_ID audio_dec_id, MM_AUDIO_PARAM_TYPE paramType, MAPI_U32 param_value)
{
    MAPI_U16 info_index = 0;

    if ( (audio_dec_id <= AUDIO_DEC_INVALID) || (audio_dec_id >= AUDIO_DEC_MAX) )
    {
        printf("%s: Err! AID is out of Range\n", __FUNCTION__);
        return;
    }

    info_index = Audio_Comm_infoType_ADEC1_setDecParam + (audio_dec_id * 0x30);
    MApi_AUDIO_SetCommAudioInfo((Audio_COMM_infoType)info_index, paramType & 0x1F, param_value);
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: MMA_setAudioParam2()
/// @brief \b Function \b Description: set Audio decoder parameter
/// @param <IN>        \b MMA_AUDIO_DEC_ID : The audio decoder ID which system want to check the request
/// @param <IN>        \b MM_AUDIO_PARAM_TYPE : The parameter type want to set, check enum ""
/// @param <IN>        \b param_value : The parameter value want to set
/// @param <OUT>       \b NONE
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
MAPI_BOOL mapi_audio_customer::MMA_setAudioParam2(MMA_AUDIO_DEC_ID audio_dec_id, MM_AUDIO_SET_PARAM_TYPE paramType, MAPI_U32 param)
{
    MAPI_BOOL ret = false;
    AUDIO_DEC_ID dec_id;

    if ( (audio_dec_id <= AUDIO_DEC_INVALID) || (audio_dec_id >= AUDIO_DEC_MAX) )
    {
        printf("%s: Err! AID is out of Range\n", __FUNCTION__);
        return ret;
    }

    dec_id = _AIdToAudioDriverId ( audio_dec_id );

    switch ( paramType )
    {
        case MM_AUDIO_SET_PARAM_MUTE:
            ret = MApi_AUDIO_SetAudioParam2( dec_id, Audio_ParamType_mute, (MS_U32) param );
            break;

        case MM_AUDIO_SET_PARAM_SYNC_STC:
            ret = MApi_AUDIO_SetAudioParam2( dec_id, Audio_ParamType_syncSTC, (MS_U32) param );
            break;

        case MM_AUDIO_SET_PARAM_FFX2:
            ret = MApi_AUDIO_SetAudioParam2( dec_id, Audio_ParamType_MM_FFx2, (MS_U32) param );
            break;

        case MM_AUDIO_SET_PARAM_PLAY_CONTROL:
            ret = MApi_AUDIO_SetAudioParam2( dec_id, Audio_ParamType_playControl, (MS_U32) param );
            break;

        case MM_AUDIO_SET_PARAM_REQ_DATA_SIZE:
            ret = MApi_AUDIO_SetAudioParam2( dec_id, Audio_ParamType_reqDataSize, (MS_U32) param );
            break;

        case MM_AUDIO_SET_PARAM_WMA:
            {
            MM_Audio_ASF_Param * pAsfParam = (MM_Audio_ASF_Param *) param;
            Audio_ASF_Param AsfParam;

            AsfParam.u32Version          = pAsfParam->u32Version;
            AsfParam.u32Channels         = pAsfParam->u32Channels;
            AsfParam.u32SampleRate       = pAsfParam->u32SampleRate;
            AsfParam.u32ByteRate         = pAsfParam->u32ByteRate;
            AsfParam.u32BlockAlign       = pAsfParam->u32BlockAlign;
            AsfParam.u32Encopt           = pAsfParam->u32Encopt;
            AsfParam.u32ParsingByApp     = pAsfParam->u32ParsingByApp;
            AsfParam.u32BitsPerSample    = pAsfParam->u32BitsPerSample;
            AsfParam.u32ChannelMask      = pAsfParam->u32ChannelMask;
            AsfParam.u32DrcParamExist    = pAsfParam->u32DrcParamExist;
            AsfParam.u32DrcRmsAmpRef     = pAsfParam->u32DrcRmsAmpRef;
            AsfParam.u32DrcRmsAmpTarget  = pAsfParam->u32DrcRmsAmpTarget;
            AsfParam.u32DrcPeakAmpRef    = pAsfParam->u32DrcPeakAmpRef;
            AsfParam.u32DrcPeakAmpTarget = pAsfParam->u32DrcPeakAmpTarget;
            AsfParam.u32MaxPacketSize    = pAsfParam->u32MaxPacketSize;

            ret = MApi_AUDIO_SetAudioParam2( dec_id, Audio_ParamType_setWMADecParam, (MS_U32) &AsfParam );
            }
            break;

        case MM_AUDIO_SET_PARAM_COOK:
            {
            MM_Audio_COOK_Param * pCookParam = (MM_Audio_COOK_Param *) param;
            Audio_COOK_Param CookParam;
            MAPI_U8 cnt;

            CookParam.mNumCodecs  = pCookParam->mNumCodecs;
            CookParam.mSamples    = pCookParam->mSamples;
            CookParam.mSampleRate = pCookParam->mSampleRate;
            for ( cnt = 0; cnt < CookParam.mNumCodecs; cnt++ )
            {
                CookParam.Channels[cnt] = pCookParam->Channels[cnt];
                CookParam.Regions[cnt] = pCookParam->Regions[cnt];
                CookParam.cplStart[cnt] = pCookParam->cplStart[cnt];
                CookParam.cplQbits[cnt] = pCookParam->cplQbits[cnt];
                CookParam.FrameSize[cnt] = pCookParam->FrameSize[cnt];
            }

            ret = MApi_AUDIO_SetAudioParam2( dec_id, Audio_ParamType_setCOOKDecParam, (MS_U32) &CookParam );
            }
            break;

        case MM_AUDIO_SET_PARAM_XPCM:
            {
            MM_Audio_XPCM_Param * pXpcmParam = (MM_Audio_XPCM_Param *) param;
            Audio_XPCM_Param XpcmParam;

            switch ( pXpcmParam->audioType )
            {
                case LPCM_:       XpcmParam.audioType = LPCM;        break;
                case MS_ADPCM_:   XpcmParam.audioType = MS_ADPCM;    break;
                case LPCM_ALAW:   XpcmParam.audioType = G711_A_LAW;  break;
                case LPCM_MULAW:  XpcmParam.audioType = G711_u_LAW;  break;
                case IMA_ADPCM_:  XpcmParam.audioType = IMA_ADPCM;   break;
                default:          XpcmParam.audioType = LPCM;        break;
            }

            XpcmParam.sampleRate     = pXpcmParam->sampleRate;
            XpcmParam.blockSize      = pXpcmParam->blockSize;
            XpcmParam.samplePerBlock = pXpcmParam->samplePerBlock;
            XpcmParam.channels       = pXpcmParam->channels;
            XpcmParam.bitsPerSample  = pXpcmParam->bitsPerSample;

            ret = MApi_AUDIO_SetAudioParam2( dec_id, Audio_ParamType_setXPCMDecParam, (MS_U32) &XpcmParam );
            }
            break;

        default:
            printf("%s: Err! unknow param type %d\n", __FUNCTION__, paramType);
            break;
    }

    return ret;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: Music_Init()
/// @brief \b Function \b Description: MM Music mode Initial Audio decoder
/// @param <IN>        \b NONE    :
/// @param <OUT>       \b Audio decoder type
/// @param <GLOBAL>    \b NONE    :
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::Music_Init(const En_DVB_decSystemType_ enDecSystem)
{
    MAPI_BOOL  bMusicInit = FALSE;
    En_DVB_decSystemType enDriverDecSystem = MSAPI_AUD_DVB_INVALID;

    OLD_FUNCTION_WARNING_MSG
    m_mmAudioDSPSystem = enDecSystem;

    switch(enDecSystem)
    {
        case MSAPI_AUD_DVB_MPEG_:
            bMusicInit = TRUE;
            enDriverDecSystem = MSAPI_AUD_DVB_MPEG;
            break;
        case MSAPI_AUD_DVB_AC3_:
            bMusicInit = TRUE;
            enDriverDecSystem = MSAPI_AUD_DVB_AC3;
            break;
        case MSAPI_AUD_DVB_AC3P_:
            bMusicInit = TRUE;
            enDriverDecSystem = MSAPI_AUD_DVB_AC3P;
            break;
        case MSAPI_AUD_DVB_AAC_:
            bMusicInit = TRUE;
            enDriverDecSystem = MSAPI_AUD_DVB_AAC;
            break;
        case MSAPI_AUD_DVB_MP3_:
            bMusicInit = TRUE;
            enDriverDecSystem = MSAPI_AUD_DVB_MP3;
            break;
        case MSAPI_AUD_DVB_WMA_:
            bMusicInit = TRUE;
            enDriverDecSystem = MSAPI_AUD_DVB_WMA;
            break;
        case MSAPI_AUD_DVB_RA8LBR_:
            bMusicInit = TRUE;
            enDriverDecSystem = MSAPI_AUD_DVB_RA8LBR;
            break;
        case MSAPI_AUD_DVB_XPCM_:
            bMusicInit = TRUE;
            enDriverDecSystem = MSAPI_AUD_DVB_XPCM;
            break;
        case MSAPI_AUD_DVB_TONE_:
            bMusicInit = TRUE;
            enDriverDecSystem = MSAPI_AUD_DVB_TONE;
            break;
        case MSAPI_AUD_DVB_DTS_:
            bMusicInit = TRUE;
            enDriverDecSystem = MSAPI_AUD_DVB_DTS;
            break;
        case MSAPI_AUD_DVB_MS10_DDT_:
            bMusicInit = TRUE;
            enDriverDecSystem = MSAPI_AUD_DVB_MS10_DDT;
            break;
        case MSAPI_AUD_DVB_MS10_DDC_:
            bMusicInit = TRUE;
            enDriverDecSystem = MSAPI_AUD_DVB_MS10_DDC;
            break;
        case MSAPI_AUD_DVB_WMA_PRO_:
            bMusicInit = TRUE;
            enDriverDecSystem = MSAPI_AUD_DVB_WMA_PRO;
            break;
        case MSAPI_AUD_DVB_FLAC_:
            bMusicInit = TRUE;
            enDriverDecSystem = MSAPI_AUD_DVB_FLAC;
            break;
        case MSAPI_AUD_DVB_VORBIS_:
            bMusicInit = TRUE;
            enDriverDecSystem = MSAPI_AUD_DVB_VORBIS;
            break;
        case MSAPI_AUD_DVB_DTSLBR_:
            bMusicInit = TRUE;
            enDriverDecSystem = MSAPI_AUD_DVB_DTSLBR;
            break;
        case MSAPI_AUD_DVB_AMR_NB_:
            bMusicInit = TRUE;
            enDriverDecSystem = MSAPI_AUD_DVB_AMR_NB;
            break;
        case MSAPI_AUD_DVB_AMR_WB_:
            bMusicInit = TRUE;
            enDriverDecSystem = MSAPI_AUD_DVB_AMR_WB;
            break;
        case MSAPI_AUD_DVB_DRA_:
            bMusicInit = TRUE;
            enDriverDecSystem = MSAPI_AUD_DVB_DRA;
            break;
        default:
            break;
    }

    if(bMusicInit == TRUE)
    {
        MApi_AUDIO_Init(enDriverDecSystem);
    }
    else
    {
        ASSERT(0);
    }
}

MAPI_BOOL mapi_audio_customer::Key_Start(AUDIO_KEY_INFO *keyinfo, MAPI_U8* pu8file_addr, MAPI_U32 u32FileLength)//morris add, 20110831
{
    MAPI_U32 u32FileRemain;
    MAPI_U32 ES_Cnt;
    MAPI_U32 add0;

    MAPI_U32 u32PreBufWrPtrLine;

    u32FileRemain = u32FileLength;

    while(u32FileRemain > 0)
    {
        ES_Cnt = MApi_AUDIO_GetCommAudioInfo(Audio_Comm_infoType_Get_MENU_KEY_CNT);
        if(ES_Cnt == 0)
        {
            printf("---ES_Cnt = 0---\r\n");
            //fflush(stdout);
        }

        //printf("---ES[0x%x], FL[0x%x], DF[0x%x]---\r\n",(unsigned int)ES_Cnt, (unsigned int)u32FileRemain, (unsigned int)(keyinfo->u32BufTotalSize - ES_Cnt));
        if(WRITE_LINE_SIZE >= (keyinfo->u32BufTotalSize - ES_Cnt))
        {
            MsOS_DelayTask(2);
            //printf(">>>>>>>>>>>>>>>> Delay 2ms<<<<<<<<<<<<<<<<<<<<<\r\n");
            //fflush(stdout);
            continue;
        }

        //printf("---Left[0x%x], Wp[0x%x], Wphy[0x%x], Fp[0x%x]---\r\n", (unsigned int)u32FileRemain, (unsigned int)keyinfo->u32BufWrPtrLine, (unsigned int)(keyinfo->u32BufWrPtrLine*16 + keyinfo->u32BufStartAddr), (unsigned int)pu8file_addr);
        //fflush(stdout);
        memcpy((void *)MsOS_PA2KSEG1(keyinfo->u32BufWrPtrLine*16 + keyinfo->u32BufStartAddr), (void *)pu8file_addr, ((u32FileRemain>=WRITE_LINE_SIZE*16) ? WRITE_LINE_SIZE*16 : u32FileRemain));

        if(u32FileRemain < WRITE_BYTES)
        {
            add0 = WRITE_BYTES - u32FileRemain;
            memset((void *)MsOS_PA2KSEG1(keyinfo->u32BufStartAddr + keyinfo->u32BufWrPtrLine*16 + u32FileRemain), 0, add0);//add 0 at the end of data for fitting 128bit
            u32FileRemain = 0;
        }
        else
        {
            pu8file_addr += WRITE_LINE_SIZE*16;
            u32FileRemain -= WRITE_LINE_SIZE*16;
        }
        keyinfo->u32BufWrPtrLine += WRITE_LINE_SIZE;
        if(keyinfo->u32BufWrPtrLine == keyinfo->u32BufEndAddrLine)
            keyinfo->u32BufWrPtrLine = 0;

        MsOS_FlushMemory();
        MsOS_Sync();
        u32PreBufWrPtrLine = MApi_AUDIO_GetCommAudioInfo(Audio_Comm_infoType_Get_MENU_WT_PTR);
        MApi_AUDIO_SetCommAudioInfo(Audio_Comm_infoType_Set_MENU_WT_PTR, 0x7FFF&u32PreBufWrPtrLine, 0);
        //MsOS_DelayTask(1);
        MApi_AUDIO_SetCommAudioInfo(Audio_Comm_infoType_Set_MENU_WT_PTR, keyinfo->u32BufWrPtrLine, 0);
        //MsOS_DelayTask(1);
        MApi_AUDIO_SetCommAudioInfo(Audio_Comm_infoType_Set_MENU_WT_PTR, 0x8000|keyinfo->u32BufWrPtrLine, 0);
        MsOS_DelayTask(1);

    }
    return TRUE;
}

MAPI_BOOL mapi_audio_customer::Key_SetInfo(AUDIO_KEY_INFO *keyinfo)//morris add, 20110831
{
    keyinfo->u32BufWrPtrLine = 0;
    keyinfo->u32BufStartAddr = MDrv_AUDIO_GetDspMadBaseAddr(DSP_SE)+ MENU_KEY_DDR_BASE*16;
    keyinfo->u32BufStartAddrLine = keyinfo->u32BufStartAddr/16;//line base
    keyinfo->u32BufTotalSize = KEY_BUF_SIZE/16;
    keyinfo->u32BufEndAddrLine = KEY_BUF_SIZE/16;

    MApi_AUDIO_SetCommAudioInfo(Audio_Comm_infoType_Set_MENU_WT_PTR, 0x800, 0);

    return TRUE;
}

MAPI_BOOL mapi_audio_customer::SetDataCaptueSource(SDK_AUDIO_CAPTURE_DEVICE_TYPE eAudioDeviceType, SDK_AUDIO_CAPTURE_SOURCE eSource)
{
    MAPI_BOOL   bReturnValue = FALSE;

    //current only can select audio device 0 or 1
    if(eAudioDeviceType >= CAPTURE_DEVICE_TYPE_DEVICE2)
    {
        printf("\033[0;32m [AUDIO][%s] [%s] [%d] [Unsupported capture device(%d)!!] \033[0m \n", __FILE__, __FUNCTION__, __LINE__, eAudioDeviceType);
        return FALSE;
    }

    if(eSource >= CAPTURE_SOURCE_MAX_)
    {
        printf("\033[0;32m [AUDIO][%s] [%s] [%d] [Unsupported capture source(%d)!!] \033[0m \n", __FILE__, __FUNCTION__, __LINE__, eSource);
        return FALSE;
    }

    switch(eSource)
    {
        case CAPTURE_SOURCE_MAIN_SOUND_:
        case CAPTURE_SOURCE_USER_DEFINE1_:
        case CAPTURE_SOURCE_USER_DEFINE2_:
            MApi_AUDIO_SetDataCaptureSource(_SDKAudioDeviceTypeToDriverAudioDeviceType(eAudioDeviceType), E_CAPTURE_PCM_SE);
            break;

        case CAPTURE_SOURCE_SUB_SOUND_:
            MApi_AUDIO_SetDataCaptureSource(_SDKAudioDeviceTypeToDriverAudioDeviceType(eAudioDeviceType), E_CAPTURE_CH7);
            break;

        case CAPTURE_SOURCE_MICROPHONE_SOUND_:
            MApi_AUDIO_SetDataCaptureSource(_SDKAudioDeviceTypeToDriverAudioDeviceType(eAudioDeviceType), E_CAPTURE_ADC);
            break;

        case CAPTURE_SOURCE_MIXED_SOUND_:
            MApi_AUDIO_SetDataCaptureSource(_SDKAudioDeviceTypeToDriverAudioDeviceType(eAudioDeviceType), E_CAPTURE_MIXER);
            break;

        default:
            printf("\033[0;32m [AUDIO][%s] [%s] [%d] [Unsupported capture source(%d)!!] \033[0m \n", __FILE__, __FUNCTION__, __LINE__, eSource);
            break;
    }

    return bReturnValue;
}

MAPI_BOOL mapi_audio_customer::AudioCapture_Control(SDK_AUDIO_CAPTURE_CONTROL eControl, SDK_AUDIO_CAPTURE_PARAM * pParam)
{
    MAPI_BOOL bReturnValue = FALSE;
#if (AACENCODER_ENABLE == 1)
    MAPI_S16  sRet = 0;
#endif

    if (pParam == NULL)
    {
        printf("\033[0;32m [AUDIO][%s] [%s] [%d] [Invalid param pointer!!] \033[0m \n", __FILE__, __FUNCTION__, __LINE__);
        return FALSE;
    }

    //current only can select audio device 0 or 1
    if (pParam->eDevice >= CAPTURE_DEVICE_TYPE_DEVICE2)
    {
        printf("\033[0;32m [AUDIO][%s] [%s] [%d] [Unsupported capture device(%d)!!] \033[0m \n", __FILE__, __FUNCTION__, __LINE__, pParam->eDevice);
        return FALSE;
    }

    switch (eControl)
    {
        case E_CAPTURE_INIT:
            bReturnValue = MApi_AUDIO_PCMCapture_Init(_SDKAudioDeviceTypeToDriverAudioDeviceType(pParam->eDevice), _SDKAudioCaptureSourceToDriverAudioCaptureSource(pParam->eSource));

#if (AACENCODER_ENABLE == 1)       
            if (bReturnValue == TRUE)
            {
                switch (pParam->eEncode)
                {
                    case E_CAPTURE_ENCODE_AAC:
                        printf("\033[0;31m [AUDIO][%s] [%s] [%d] [AAC encoding is enabled] \033[0m \n", __FILE__, __FUNCTION__, __LINE__);
                        // fill encoder parameters
                        lAACParam.BandWidthSel = 100;
                        lAACParam.NumberOfChannels = 2;
                        lAACParam.SamplingFrequency = 48000;
                        lAACParam.OutputFormat = FORMAT_ADTS;
                        lAACParam.OutputBitRate = 128000;
                        lAACParam.ChannelMode = JOINT_STEREO;

                        sRet = Mpeg4AacEnc_Create(&lBaseAudioEnc);
                        if (sRet != E_EMZ_SUCCESS)
                        {
                            printf("\033[0;31m [AUDIO][%s] [%s] [%d] [Error in creation(%d) !!!!!] \033[0m \n", __FILE__, __FUNCTION__, __LINE__, sRet);  
                            bReturnValue = FALSE;
                        }

                        sRet = Mpeg4AacEnc_Reset(lBaseAudioEnc, &lAACParam);
                        if (sRet != E_EMZ_SUCCESS)
                        {
                            printf("\033[0;31m [AUDIO][%s] [%s] [%d] [Error in reset(%d) !!!!!] \033[0m \n", __FILE__, __FUNCTION__, __LINE__, lAACParam.OutputFormat);  
                            bReturnValue = FALSE;
                        }
                        break;

                    case E_CAPTURE_ENCODE_MP3:
                    case E_CAPTURE_ENCODE_PCM:
                    default:
                        break;
                }
            }
#endif
            break;

        case E_CAPTURE_START:
            bReturnValue = MApi_AUDIO_PCMCapture_Start(_SDKAudioDeviceTypeToDriverAudioDeviceType(pParam->eDevice));
            break;

        case E_CAPTURE_STOP:
            bReturnValue = MApi_AUDIO_PCMCapture_Stop(_SDKAudioDeviceTypeToDriverAudioDeviceType(pParam->eDevice));

#if (AACENCODER_ENABLE == 1)
            if (bReturnValue == TRUE)
            {
                switch (pParam->eEncode)
                {
                    case E_CAPTURE_ENCODE_AAC:
                        sRet = Mpeg4AacEnc_Delete(lBaseAudioEnc);
                        if (sRet < 0)
                        {
                            printf("\033[0;31m [AUDIO][%s] [%s] [%d] [Error in delete(%d) !!!!!] \033[0m \n", __FILE__, __FUNCTION__, __LINE__, sRet);  
                            bReturnValue = FALSE;
                        }
                        break;

                    case E_CAPTURE_ENCODE_MP3:
                    case E_CAPTURE_ENCODE_PCM:
                    default:
                        break;
                }
            }
#endif
            break;

        case E_CAPTURE_READ:
            if (pParam->pBuffer == NULL)
            {
                printf("\033[0;32m [AUDIO][%s] [%s] [%d] [Invalid buffer pointer!!] \033[0m \n", __FILE__, __FUNCTION__, __LINE__);
                return FALSE;
            }
            if (pParam->bufsize == NULL || *pParam->bufsize == 0)
            {
                printf("\033[0;32m [AUDIO][%s] [%s] [%d] [Invalid buffer size!!] \033[0m \n", __FILE__, __FUNCTION__, __LINE__);
                return FALSE;
            }
            bReturnValue = MApi_AUDIO_PCMCapture_Read(_SDKAudioDeviceTypeToDriverAudioDeviceType(pParam->eDevice), (void *)pParam->pBuffer, *pParam->bufsize);

#if (AACENCODER_ENABLE == 1)
            if (bReturnValue == TRUE)
            {
                switch (pParam->eEncode)
                {
                    case E_CAPTURE_ENCODE_AAC:
                        AACEncodeLen = ENCODEBUFLEN;
                        sRet = Mpeg4AacEnc_Encode(lBaseAudioEnc, (tEmzInt16 *)pParam->pBuffer, (tEmzInt32)(*pParam->bufsize), AACEncodeOut, &AACEncodeLen);
                        if (sRet < 0)
                        {
                            printf("\033[0;31m [AUDIO][%s] [%s] [%d] [Error in Encode(%d),len(%d), AACEncodeLen(%d) !!!!!] \033[0m \n", __FILE__, __FUNCTION__, __LINE__, sRet, *pParam->bufsize, AACEncodeLen);  
                            bReturnValue = FALSE;
                        }
                        else
                        {
                            if (AACEncodeLen <= (MAPI_S32)*pParam->bufsize)
                            {
                                memcpy(pParam->pBuffer, AACEncodeOut, AACEncodeLen);
                                *pParam->bufsize = AACEncodeLen;
                            }
                            else
                            {
                                printf("\033[0;31m [AUDIO][%s] [%s] [%d] [Encoded size(%d) is larger than request size(%d)!!] \033[0m \n", __FILE__, __FUNCTION__, __LINE__, AACEncodeLen, *pParam->bufsize);  
                                bReturnValue = FALSE;
                            }
                        }
                        break;

                    case E_CAPTURE_ENCODE_MP3:
                    case E_CAPTURE_ENCODE_PCM:
                    default:
                        break;
                }
            }
#endif
            break;

        case E_CAPTURE_SETSOURCE:
            bReturnValue = MApi_AUDIO_SetDataCaptureSource(_SDKAudioDeviceTypeToDriverAudioDeviceType(pParam->eDevice), _SDKAudioCaptureSourceToDriverAudioCaptureSource(pParam->eSource));
            break;

        default:
            printf("\033[0;32m [AUDIO][%s] [%s] [%d] [Unsupported control type(%d)!!] \033[0m \n", __FILE__, __FUNCTION__, __LINE__, eControl);
            break;
    }

    return bReturnValue;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: DECODER_SetCommand()
/// @brief \b Function \b Description: Set Audio Decoder commend
/// @param <IN>        \b enDecComamnd  : MSAPI_AUD_DVB_DECCMD_STOP_,
///                                     : MSAPI_AUD_DVB_DECCMD_PLAY_,
///                                     : MSAPI_AUD_DVB_DECCMD_PLAYFILE_,
///                                     : MSAPI_AUD_DVB_DECCMD_PAUSE_, ...etc
///                                     : see "enum En_DVB_decCmdType_"
/// @return <OUT>       \b NONE
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::DECODER_SetCommand(En_DVB_decCmdType_ enDecComamnd)
{
    MApi_AUDIO_SetCommand((En_DVB_decCmdType)enDecComamnd);
    if((MSAPI_AUD_DVB_DECCMD_STOP_ != enDecComamnd) &&
            (MSAPI_AUD_DVB2_DECCMD_STOP_ != enDecComamnd))
    {
        // Set SPDIF Mode
        SPDIF_SetMode(SDK_cUI_SPDIF_Mode);
    }
}

void mapi_audio_customer::DECODER_SetCommand(En_DVB_decCmdType_ enDecComamnd, MAPI_AUDIO_PROCESSOR_TYPE eProcessor)
{
    MMA_AUDIO_DEC_ID DecID = AUDIO_DEC_INVALID;

    if ((SubDecID == AUDIO_DEC_INVALID) &&
        (MainDecID == AUDIO_DEC_INVALID))
    {
        // For compatible
        switch (enDecComamnd)
        {
            case MSAPI_AUD_DVB_DECCMD_STOP_AD_:
                enDecComamnd = MSAPI_AUD_DVB2_DECCMD_STOP_;
                break;

            case MSAPI_AUD_DVB_DECCMD_PLAY_AD_:
                enDecComamnd = MSAPI_AUD_DVB2_DECCMD_PLAY_;
                break;

            default:
                break;
        }
        DECODER_SetCommand(enDecComamnd);
        return;
    }

    if (eProcessor == AUDIO_PROCESSOR_MAIN)
    {
        DecID =MainDecID;
    }
    else
    {
        DecID =SubDecID;
    }

    if ((DecID == AUDIO_DEC_INVALID) || (DecID == AUDIO_DEC_MAX))
    {
        return ;
    }

    printf("DecID = %d , enDecComamnd = %d %s() %d\n",DecID, enDecComamnd,__FUNCTION__,__LINE__);

    MApi_AUDIO_SetDecodeCmd(_AIdToAudioDriverId(DecID), (En_DVB_decCmdType)enDecComamnd);

}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: DECODER_SetCommand()
/// @brief \b Function \b Description: Set ATV Audio Decoder commend
/// @param <IN>        \b enDecComamnd  : MSAPI_AUD_SIF_CMD_SET_STOP_,
///                                     : MSAPI_AUD_SIF_CMD_SET_PLAY_.
///                                     : see "enum En_AUD_SIF_CmdType_"
/// @return <OUT>       \b NONE
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::DECODER_SetCommand(En_AUD_SIF_CmdType_ enDecComamnd)
{
    MApi_AUDIO_SIF_SendCmd((En_AUD_SIF_CmdType)enDecComamnd, 0, 0);
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: DECODER_SwitchAudioDSPSystem()
/// @brief \b Function \b Description: Audio Switch Decoder System Function,
/// @param <IN>        \b eAudioDSPSystem : Audio DSP System Type
///                                       : E_AUDIO_DSP_SIF_,
///                                       : E_AUDIO_DSP_MPEG_,
///                                       : E_AUDIO_DSP_AC3_,
///                                       : E_AUDIO_DSP_AC3P_,
///                                       : E_AUDIO_DSP_AACP_,
///                                       : E_AUDIO_DSP_MPEG_AD_, ...etc
///                                       : see "enum AUDIO_DSP_SYSTEM_"
/// @return <OUT>       \b NONE
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::DECODER_SwitchAudioDSPSystem(AUDIO_DSP_SYSTEM_ eAudioDSPSystem)
{
    switch(eAudioDSPSystem)
    {
        case E_AUDIO_DSP_AC3_:
            m_mmAudioDSPSystem = MSAPI_AUD_DVB_AC3_;
            MApi_AUDIO_SetSystem(MSAPI_AUD_DVB_AC3);
            break;

        case E_AUDIO_DSP_AC3_AD_:
            m_mmAudioDSPSystem = MSAPI_AUD_DVB2_AC3_;
            MApi_AUDIO_SetSystem(MSAPI_AUD_DVB2_AC3);
            break;

        case E_AUDIO_DSP_AC3P_:
            m_mmAudioDSPSystem = MSAPI_AUD_DVB_AC3P_;
            MApi_AUDIO_SetSystem(MSAPI_AUD_DVB_AC3P);
            break;

        case E_AUDIO_DSP_AC3P_AD_:
            m_mmAudioDSPSystem = MSAPI_AUD_DVB2_AC3P_;
            MApi_AUDIO_SetSystem(MSAPI_AUD_DVB2_AC3P);
            break;

        case E_AUDIO_DSP_SIF_:
            MApi_AUDIO_SIF_SendCmd(MSAPI_AUD_SIF_CMD_SET_STOP, 0, 0);// stop play
            if(IS_SBTVD_BRAZIL())
            {
                m_mmAudioDSPSystem = MSAPI_AUD_ATV_BTSC_;
                MApi_AUDIO_SetSystem(MSAPI_AUD_ATV_BTSC);
            }
            else
            {
                if(mapi_syscfg_fetch::GetInstance()->get_ATVSystemType() == E_PAL_ENABLE)
                {
                    m_mmAudioDSPSystem = MSAPI_AUD_ATV_PAL_;
                    MApi_AUDIO_SetSystem(MSAPI_AUD_ATV_PAL);
                }
                else if(mapi_syscfg_fetch::GetInstance()->get_ATVSystemType() == E_NTSC_ENABLE)
                {
                    if(mapi_syscfg_fetch::GetInstance()->get_AUDIOSystemType() == E_BTSC_ENABLE)
                    {
                        m_mmAudioDSPSystem = MSAPI_AUD_ATV_BTSC_;
                        MApi_AUDIO_SetSystem(MSAPI_AUD_ATV_BTSC);
                    }
                    else if(mapi_syscfg_fetch::GetInstance()->get_AUDIOSystemType() == E_A2_ENABLE)
                    {
                        m_mmAudioDSPSystem = MSAPI_AUD_ATV_PAL_;
                        MApi_AUDIO_SetSystem(MSAPI_AUD_ATV_PAL);
                    }
                }
            }

            if (NULL != SDK_pAudioSIFThrTable)
            {
                MApi_AUDIO_SIF_SetThreshold((THR_TBL_TYPE *)SDK_pAudioSIFThrTable);
            }
            SIF_SetSIFPrescale();
            MApi_AUDIO_SIF_SendCmd(MSAPI_AUD_SIF_CMD_SET_PLAY, 0, 0);
            break;

        case E_AUDIO_DSP_AACP_:
            m_mmAudioDSPSystem = MSAPI_AUD_DVB_AAC_;
            MApi_AUDIO_SetSystem(MSAPI_AUD_DVB_AAC);
            break;

        case E_AUDIO_DSP_MPEG_AD_:
            m_mmAudioDSPSystem = MSAPI_AUD_DVB2_MPEG_;
            MApi_AUDIO_SetSystem(MSAPI_AUD_DVB2_MPEG);
            break;

        case E_AUDIO_DSP_AACP_AD_:
            m_mmAudioDSPSystem = MSAPI_AUD_DVB2_AAC_;
            MApi_AUDIO_SetSystem(MSAPI_AUD_DVB2_AAC);
            break;
#if (STB_ENABLE == 0)
        case E_AUDIO_DSP_DRA_:
            m_mmAudioDSPSystem = MSAPI_AUD_DVB_DRA_;
            MApi_AUDIO_SetSystem(MSAPI_AUD_DVB_DRA);
            break;
#endif
        case E_AUDIO_DSP_MPEG_:
        default:
            m_mmAudioDSPSystem = MSAPI_AUD_DVB_MPEG_;
            MApi_AUDIO_SetSystem(MSAPI_AUD_DVB_MPEG);
            break;
    }
    m_eAudioDSPSystem = eAudioDSPSystem;
	SPDIF_SetMode(SDK_cUI_SPDIF_Mode);
}

void mapi_audio_customer::DECODER_SwitchAudioDSPSystem(AUDIO_DSP_SYSTEM_ eAudioDSPSystem , MAPI_AUDIO_PROCESSOR_TYPE eProcessor)
{

    if ((SubDecID == AUDIO_DEC_INVALID) &&
        (MainDecID == AUDIO_DEC_INVALID))
    {
        // For compatible old function
        DECODER_SwitchAudioDSPSystem(eAudioDSPSystem);
        return;
    }

    switch (eProcessor)
    {
        case AUDIO_PROCESSOR_MAIN:
                if (MainDecID != AUDIO_DEC_INVALID)
                {
                    stMainAudio.eDSPSystem = eAudioDSPSystem;
                    SetAudioDec(MainDecID, &stMainAudio);
                }
            break;

        case AUDIO_PROCESSOR_SUB:
                if (SubDecID != AUDIO_DEC_INVALID)
                {
                    stSubAudio.eDSPSystem = eAudioDSPSystem;
                    SetAudioDec(SubDecID, &stSubAudio);
                }
            break;

        default:
            break;
    }
	SPDIF_SetMode(SDK_cUI_SPDIF_Mode);
}


//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SIF_Shift(type)
/// @brief \b Function \b Description: SIF Clock Shift Function
/// @param type  \b <IN> :
//-------------------------------------------------------------------------------------------------
void mapi_audio_customer::SIF_Shift(En_AUD_VIF_Type_ type)
{
    MApi_AUDIO_SIF_Shift((En_AUD_VIF_Type)type);
    if (NULL != SDK_pAudioSIFThrTable)
    {
        MApi_AUDIO_SIF_SetThreshold((THR_TBL_TYPE *)SDK_pAudioSIFThrTable);
    }
    SIF_SetSIFPrescale();
}

void mapi_audio_customer::SetAudioSDKInfo(AudioSDKinfoType eInfoType, MAPI_U32 param1, MAPI_U32 param2 )
{
    UNUSED(param2);

    switch(eInfoType)
    {
        case stSDK_AUD_UnmuteAudioAMP:
            SDK_AUD_UnmuteAudioAMP = param1;
            break;
        case stSDK_AUD_wLimitedTimeOfMute:
            SDK_AUD_wLimitedTimeOfMute = param1;
            break;
        case stSDK_Audio_PowerOn_Monitor_Cnt:
            SDK_Audio_PowerOn_Monitor_Cnt = param1;
            break;
        default:
            printf("[Warning]Unknown AudioSDKinfoType type %d.\n", eInfoType);
            break;
    }
}

AUDIO_SOURCE_INFO_TYPE_  mapi_audio_customer::_SDK_APISourceType_To_DriverAudioSourceInfoType(MAPI_INPUT_SOURCE_TYPE enInputSourceType)
{
    AUDIO_SOURCE_INFO_TYPE  ret = E_AUDIO_INFO_SPDIF_IN;
    switch(enInputSourceType)
    {
        case MAPI_INPUT_SOURCE_DTV:
        case MAPI_INPUT_SOURCE_DTV2:
        case MAPI_INPUT_SOURCE_DTV3:
            ret = E_AUDIO_INFO_DTV_IN;
            break;

        case MAPI_INPUT_SOURCE_ATV:
            ret = E_AUDIO_INFO_ATV_IN;
            break;

        case MAPI_INPUT_SOURCE_CVBS:
        case MAPI_INPUT_SOURCE_CVBS2:
        case MAPI_INPUT_SOURCE_SVIDEO:
        case MAPI_INPUT_SOURCE_SCART:
        case MAPI_INPUT_SOURCE_SCART2:
        case MAPI_INPUT_SOURCE_YPBPR:
        case MAPI_INPUT_SOURCE_YPBPR2:
        case MAPI_INPUT_SOURCE_VGA:
        case MAPI_INPUT_SOURCE_DVI:
            ret = E_AUDIO_INFO_ADC_IN;
            break;

        case MAPI_INPUT_SOURCE_HDMI:
        case MAPI_INPUT_SOURCE_HDMI2:
        case MAPI_INPUT_SOURCE_HDMI3:
        case MAPI_INPUT_SOURCE_HDMI4:
            ret = E_AUDIO_INFO_HDMI_IN;
            break;

        case MAPI_INPUT_SOURCE_STORAGE:
            ret = E_AUDIO_INFO_GAME_IN;
            break;

        case MAPI_INPUT_SOURCE_KTV:
            ret = E_AUDIO_INFO_KTV_IN;
            break;

        default:
            break;
    }

    return (AUDIO_SOURCE_INFO_TYPE_)ret;
}


