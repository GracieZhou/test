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

* Class : device_tuner_MXL661
* File  : device_tuner_MXL661.cpp
**********************************************************************/

#include <stdio.h>
#include <pthread.h>
#include "debug.h"
#include "device_tuner_mxl661.h"
#include "mapi_i2c_devTable.h"
#include "mapi_i2c.h"
#include "mapi_interface.h"
#include "mapi_vif_datatype.h"
#include "mapi_audio_datatype.h"
#include "mapi_demodulator_datatype.h"

#include "MxL661/MaxLinearDataTypes.h"
#include "MxL661/MxL661_OEM_Drv.c"
#include "MxL661/MxL661_TunerApi.c"
#include "MxL661/MxL661_TunerCfg.c"
#include "MxL661/MxL661_TunerSpurTable.c"

#define MSTAR_INTERN_VIF                      1
#define MXL661_I2C_ADDR                       0xC0
#define FRONTEND_IF_DEMODE_TYPE               MSTAR_INTERN_VIF
#define MAX_FINE_TUNE_STEP                    32
#define FREQ_STEP                             62.5//50  //Khz
#define TN_FREQ_STEP                          E_FREQ_STEP_62_5KHz
#define ATV_TUNER_IF_FREQ_KHz                 5000L
#define DTV_TUNER_IF_FREQ_KHz                 5000L
#define DTMB_TUNER_IF_FREQ_KHz                5000L
#define TUNER_L_PRIME_IF_FREQ_KHz             5000L
//#define IF_FREQ_A                       4100L
#define ATV_IF_STEP                       (ATV_TUNER_IF_FREQ_KHz/FREQ_STEP)
#define DTV_IF_STEP                       (DTV_TUNER_IF_FREQ_KHz/FREQ_STEP)
#define TUNER_PLL_STABLE_TIME             30
#define SECAM_L_PRIME_TUNER_PLL_STABLE_TIME     35

#define TUNER_DTV_IF_FREQ_KHz      DTV_TUNER_IF_FREQ_KHz
#define TUNER_DTV_IQ_SWAP           0

#define TUNER_VHF_LOWMIN_FREQ             47000L
#define TUNER_VHF_LOWMAX_FREQ             160000L
#define TUNER_VHF_HIGHMIN_FREQ            160000L
#define TUNER_VHF_HIGHMAX_FREQ            445000L
#define TUNER_UHF_MIN_FREQ_UK             445000L
#define TUNER_UHF_MIN_FREQ                434000L
#define TUNER_UHF_MAX_FREQ                894000L

//define lock range of AFT value
#define MIN_AFT_VALUE                          24
#define MAX_AFT_VALUE                         38
#define FINE_MIN_AFT_VALUE                  30
#define CENTER_AFT_VALUE                    31
#define FINE_MAX_AFT_VALUE                 2
#define MAX_ZIG_ZAG_NUM                      7
#define NTSC_TUNER_PLL_STABLE_TIME     20       //normally is smaller then 6ms
#define TUNER_HIBYTE(w)                       ((MAPI_U8) (((MAPI_U16) (w) >> 8) & 0xFF))
#define TUNER_LOBYTE(w)                      ((MAPI_U8) (w & 0xFF))

#define VIF_TUNER_TYPE 1     // 0: RF Tuner; 1: Silicon Tuner
#define VIF_CR_RATE_B                   0x000ABDA1 // 5+2.25M   // [21:0], CR_RATE for 6.4 MHz, HEX((xxx/43.2) * (2^22))
#define VIF_CR_INVERT_B                     0                   // Video carrier position; 0: high side; 1:low side
#define VIF_CR_RATE_GH                  0x000B7B42  //5+2.75M         // [21:0], CR_RATE for 1.52 MHz, HEX((xxx/43.2) * (2^22))
#define VIF_CR_INVERT_GH                    0                   // Video carrier position; 0: high side; 1:low side
#define VIF_CR_RATE_DK                  0x000B7B42// //5+2.75M           // [21:0], CR_RATE for 1.52 MHz, HEX((xxx/43.2) * (2^22))
#define VIF_CR_INVERT_DK                    0                   // Video carrier position; 0: high side; 1:low side
#define VIF_CR_RATE_I                   0x000B7B42 //5+2.75M          // [21:0], CR_RATE for 1.52 MHz, HEX((xxx/43.2) * (2^22))
#define VIF_CR_INVERT_I                     0                   // Video carrier position; 0: high side; 1:low side
#define VIF_CR_RATE_L                   0x000B7B42 // 5+2.75M         // [21:0], CR_RATE for 1.52 MHz, HEX((xxx/43.2) * (2^22))
#define VIF_CR_INVERT_L                     0                   // Video carrier position; 0: high side; 1:low side
#define VIF_CR_RATE_LL                  0x000B7B42// //5+2.75M          // [21:0], CR_RATE for 7.02 MHz, HEX((xxx/43.2) * (2^22))
#define VIF_CR_INVERT_LL                    0                   // Video carrier position; 0: high side; 1:low side
#define VIF_CR_RATE_MN                  0x000A0000// //5+1.75M         // [21:0], CR_RATE for 1.75 MHz, HEX((xxx/43.2) * (2^22))
#define VIF_CR_INVERT_MN                    0                   // Video carrier position; 0: high side; 1:low side

//EosTek Patch Begin
#define VIF_SOS_21_FILTER_C0            0x0262//0x01F3//0x0277//0x02A3//0x032D
#define VIF_SOS_21_FILTER_C1            0x06E4//0x0774//0x065C//0x0718//0x069B
#define VIF_SOS_21_FILTER_C2            0x01CE//0x01AF//0x01A4//0x01C3//0x0209
#define VIF_SOS_21_FILTER_C3            0x059E//0x060D//0x0589//0x055D//0x04D3
#define VIF_SOS_21_FILTER_C4            0x014E//0x00DD//0x0200//0x0126//0x015C

#define VIF_SOS_22_FILTER_C0            0x0262//0x01F3//0x0277//0x02A3//0x034D
#define VIF_SOS_22_FILTER_C1            0x06E4//0x0774//0x065C//0x0718//0x068E
#define VIF_SOS_22_FILTER_C2            0x01CE//0x01AF//0x01A4//0x01C3//0x01F8
#define VIF_SOS_22_FILTER_C3            0x059E//0x060D//0x0589//0x055D//0x04B3
#define VIF_SOS_22_FILTER_C4            0x014E//0x00DD//0x0200//0x0126//0x017A

#define VIF_SOS_31_FILTER_C0            0x0000//0x029F//0x0000//0x026F//0x0217
#define VIF_SOS_31_FILTER_C1            0x0000//0x06A8//0x0000//0x06E8//0x0754
#define VIF_SOS_31_FILTER_C2            0x0000//0x0158//0x0000//0x0118//0x00AC
#define VIF_SOS_31_FILTER_C3            0x0000//0x0562//0x0000//0x0592//0x05E9
#define VIF_SOS_31_FILTER_C4            0x0200//0x0200//0x0200

#define VIF_SOS_32_FILTER_C0            0x0000//0x02CD//0x0000//0x025A//0x0000
#define VIF_SOS_32_FILTER_C1            0x0000//0x0705//0x0000//0x0748//0x0000
#define VIF_SOS_32_FILTER_C2            0x0000//0x00FB//0x0000//0x00B8//0x0000
#define VIF_SOS_32_FILTER_C3            0x0000//0x0533//0x0000//0x05A7//0x0000
#define VIF_SOS_32_FILTER_C4            0x0200//0x0200//0x0200
//EosTek Patch End

//B_Stereo_A2 ========================
// 1.75Mhz
#define N_A1_C0_B_A2 0x03A4
#define N_A1_C1_B_A2 0x063C
#define N_A1_C2_B_A2 0x0421
// 1.51Mhz
#define N_A2_C0_B_A2 0x03AB
#define N_A2_C1_B_A2 0x063C
#define N_A2_C2_B_A2 0x0419
// 8.75Mhz
#define S_11_C0_B_A2 0x011B
#define S_11_C1_B_A2 0x063C
#define S_11_C2_B_A2 0x0200
#define S_11_C3_B_A2 0x06D3
#define S_11_C4_B_A2 0x0200
// 1.75Mhz
#define S_12_C0_B_A2 0x03A4
#define S_12_C1_B_A2 0x063C
#define S_12_C2_B_A2 0x0200
#define S_12_C3_B_A2 0x0421
#define S_12_C4_B_A2 0x0200

//B_Mono_NICAM ====================
// 1.75Mhz
#define N_A1_C0_B_NICAM 0x03A3
#define N_A1_C1_B_NICAM 0x063C
#define N_A1_C2_B_NICAM 0x0421
// 1.4Mhz
#define N_A2_C0_B_NICAM 0x03AF
#define N_A2_C1_B_NICAM 0x063C
#define N_A2_C2_B_NICAM 0x0415
// 8.75Mhz
#define S_11_C0_B_NICAM 0x011B
#define S_11_C1_B_NICAM 0x063C
#define S_11_C2_B_NICAM 0x0200
#define S_11_C3_B_NICAM 0x06D3
#define S_11_C4_B_NICAM 0x0200
// 1.75Mhz
#define S_12_C0_B_NICAM 0x03A4
#define S_12_C1_B_NICAM 0x063C
#define S_12_C2_B_NICAM 0x0200
#define S_12_C3_B_NICAM 0x0421
#define S_12_C4_B_NICAM 0x0200

//GH_Stereo_A2 =======================
// 2.25Mhz
#define N_A1_C0_GHA2 0x038F
#define N_A1_C1_GHA2 0x063C
#define N_A1_C2_GHA2 0x0436
// 2.01Mhz
#define N_A2_C0_GHA2 0x039A
#define N_A2_C1_GHA2 0x063C
#define N_A2_C2_GHA2 0x042B
// 10.25Mhz
#define S_11_C0_GHA2 0x004D
#define S_11_C1_GHA2 0x063C
#define S_11_C2_GHA2 0x0200
#define S_11_C3_GHA2 0x07AE
#define S_11_C4_GHA2 0x0200
// 2.25Mhz
#define S_12_C0_GHA2 0x038F
#define S_12_C1_GHA2 0x063C
#define S_12_C2_GHA2 0x0200
#define S_12_C3_GHA2 0x0436
#define S_12_C4_GHA2 0x0200

//GH_Mono_NICAM ===================
// 2.25Mhz
#define N_A1_C0_GHMN 0x038F
#define N_A1_C1_GHMN 0x063C
#define N_A1_C2_GHMN 0x0436
// 1.9Mhz
#define N_A2_C0_GHMN 0x039E
#define N_A2_C1_GHMN 0x063C
#define N_A2_C2_GHMN 0x0427
// 10.25Mhz
#define S_11_C0_GHMN 0x004D
#define S_11_C1_GHMN 0x063C
#define S_11_C2_GHMN 0x0200
#define S_11_C3_GHMN 0x07AE
#define S_11_C4_GHMN 0x0200
// 2.25Mhz
#define S_12_C0_GHMN 0x038F
#define S_12_C1_GHMN 0x063C
#define S_12_C2_GHMN 0x0200
#define S_12_C3_GHMN 0x0436
#define S_12_C4_GHMN 0x0200

//DK1_Stero_A2 ======================
// 1.25Mhz
#define N_A1_C0_DK1A2 0x03B3
#define N_A1_C1_DK1A2 0x063C
#define N_A1_C2_DK1A2 0x0411
// 1.49Mhz
#define N_A2_C0_DK1A2 0x03AC
#define N_A2_C1_DK1A2 0x063C
#define N_A2_C2_DK1A2 0x0418
// 9.25Mhz
#define S_11_C0_DK1A2 0x00D7
#define S_11_C1_DK1A2 0x063C
#define S_11_C2_DK1A2 0x0200
#define S_11_C3_DK1A2 0x071B
#define S_11_C4_DK1A2 0x0200
// 1.25Mhz
#define S_12_C0_DK1A2 0x03B3
#define S_12_C1_DK1A2 0x063C
#define S_12_C2_DK1A2 0x0200
#define S_12_C3_DK1A2 0x0411
#define S_12_C4_DK1A2 0x0200

//DK2_Stero_A2 ======================
// 1.25Mhz
#define N_A1_C0_DK2A2 0x03B3
#define N_A1_C1_DK2A2 0x063C
#define N_A1_C2_DK2A2 0x0411
// 1.01Mhz
#define N_A2_C0_DK2A2 0x03B3
#define N_A2_C1_DK2A2 0x063C
#define N_A2_C2_DK2A2 0x040B
// 9.25Mhz
#define S_11_C0_DK2A2 0x00D7
#define S_11_C1_DK2A2 0x063C
#define S_11_C2_DK2A2 0x0200
#define S_11_C3_DK2A2 0x071B
#define S_11_C4_DK2A2 0x0200
// 1.25Mhz
#define S_12_C0_DK2A2 0x03B3
#define S_12_C1_DK2A2 0x063C
#define S_12_C2_DK2A2 0x0200
#define S_12_C3_DK2A2 0x0411
#define S_12_C4_DK2A2 0x0200

//DK3_Stero_A2=====================
// 1.25Mhz
#define N_A1_C0_DK3A2 0x03B3
#define N_A1_C1_DK3A2 0x063C
#define N_A1_C2_DK3A2 0x0411
// 2.01Mhz
#define N_A2_C0_DK3A2 0x039A
#define N_A2_C1_DK3A2 0x063C
#define N_A2_C2_DK3A2 0x042B
// 9.25Mhz
#define S_11_C0_DK3A2 0x00D7
#define S_11_C1_DK3A2 0x063C
#define S_11_C2_DK3A2 0x0200
#define S_11_C3_DK3A2 0x071B
#define S_11_C4_DK3A2 0x0200
// 1.25Mhz
#define S_12_C0_DK3A2 0x03B3
#define S_12_C1_DK3A2 0x063C
#define S_12_C2_DK3A2 0x0200
#define S_12_C3_DK3A2 0x0411
#define S_12_C4_DK3A2 0x0200

//DK_Mono_NICAM===================
// 1.25Mhz
#define N_A1_C0_DKMN 0x03B3
#define N_A1_C1_DKMN 0x063C
#define N_A1_C2_DKMN 0x0411
// 1.9Mhz
#define N_A2_C0_DKMN 0x039E
#define N_A2_C1_DKMN 0x063C
#define N_A2_C2_DKMN 0x0427
// 9.25Mhz
#define S_11_C0_DKMN 0x00D7
#define S_11_C1_DKMN 0x063C
#define S_11_C2_DKMN 0x0200
#define S_11_C3_DKMN 0x071B
#define S_11_C4_DKMN 0x0200
// 1.25Mhz
#define S_12_C0_DKMN 0x03B3
#define S_12_C1_DKMN 0x063C
#define S_12_C2_DKMN 0x0200
#define S_12_C3_DKMN 0x0411
#define S_12_C4_DKMN 0x0200

//Sound : I ===========================
// 1.75Mhz
#define N_A1_C0_I 0x03A4
#define N_A1_C1_I 0x063C
#define N_A1_C2_I 0x0421
// 1.2Mhz
#define N_A2_C0_I 0x03B4
#define N_A2_C1_I 0x063C
#define N_A2_C2_I 0x0410
// 9.75Mhz
#define S_11_C0_I 0x0092
#define S_11_C1_I 0x063C
#define S_11_C2_I 0x0200
#define S_11_C3_I 0x0764
#define S_11_C4_I 0x0200
// 1.75Mhz
#define S_12_C0_I 0x03A4
#define S_12_C1_I 0x063C
#define S_12_C2_I 0x0200
#define S_12_C3_I 0x0421
#define S_12_C4_I 0x0200
//Sound : MN=========================
// 2.25Mhz
#define N_A1_C0_NTSC 0x038F
#define N_A1_C1_NTSC 0x063C
#define N_A1_C2_NTSC 0x0436
// 2.03Mhz
#define N_A2_C0_NTSC 0x0399
#define N_A2_C1_NTSC 0x063C
#define N_A2_C2_NTSC 0x042C
// 8.25Mhz
#define S_11_C0_NTSC 0x015D
#define S_11_C1_NTSC 0x063C
#define S_11_C2_NTSC 0x0200
#define S_11_C3_NTSC 0x068D
#define S_11_C4_NTSC 0x0200
// 2.25Mhz
#define S_12_C0_NTSC 0x038F
#define S_12_C1_NTSC 0x063C
#define S_12_C2_NTSC 0x0200
#define S_12_C3_NTSC 0x0436
#define S_12_C4_NTSC 0x0200

//Sound : L ==========================
// 1.25Mhz
#define N_A1_C0_L 0x03B3
#define N_A1_C1_L 0x063C
#define N_A1_C2_L 0x0411
// 1.9Mhz
#define N_A2_C0_L 0x039E
#define N_A2_C1_L 0x063C
#define N_A2_C2_L 0x0427
// 9.25Mhz
#define S_11_C0_L 0x00D7
#define S_11_C1_L 0x063C
#define S_11_C2_L 0x0200
#define S_11_C3_L 0x071B
#define S_11_C4_L 0x0200
// 1.25Mhz
#define S_12_C0_L 0x03B3
#define S_12_C1_L 0x063C
#define S_12_C2_L 0x0200
#define S_12_C3_L 0x0411
#define S_12_C4_L 0x0200

//End of Table===================

#define IF_OUT_CFG_GAIN_LEVEL_DVB        6 //4
#define IF_OUT_CFG_GAIN_LEVEL_DVB_T2     6
#define IF_OUT_CFG_GAIN_LEVEL_DTMB       10 // 15 //for external ATBM886x DTMB demod must use 15
#define IF_OUT_CFG_GAIN_LEVEL_ISDB       6 //13 update by sj.rao for RF
#define IF_OUT_CFG_GAIN_LEVEL_ATV_NORMAL 9 //8 //7
#define IF_OUT_CFG_GAIN_LEVEL_ATV_SCAN   9
#define IF_OUT_CFG_GAIN_LEVEL_ATV_SECAM  8

//static void MxL_API_ATV_Finetune(MXL_BOOL ScaleUp );
static void MxL_API_GetLockStatus(UINT8* RfLock, UINT8* RefLock);

device_tuner_MXL661::device_tuner_MXL661(void)
{
    m_bDeviceBusy = 0;
    apiCmd.MxLIf.cmdResetCfg.I2cSlaveAddr = MXL661_I2C_ADDR;
    apiCmd.MxLIf.cmdOverwriteDefault.I2cSlaveAddr = MXL661_I2C_ADDR;
    apiCmd.MxLIf.cmdXtalCfg.I2cSlaveAddr = MXL661_I2C_ADDR;
    apiCmd.MxLIf.cmdIfOutCfg.I2cSlaveAddr = MXL661_I2C_ADDR;
    apiCmd.MxLIf.cmdGpoCfg.I2cSlaveAddr = MXL661_I2C_ADDR;
    apiCmd.MxLIf.cmdDevInfoReq.I2cSlaveAddr = MXL661_I2C_ADDR;
    apiCmd.MxLIf.cmdGpoReq.I2cSlaveAddr = MXL661_I2C_ADDR;
    apiCmd.MxLIf.cmdModeCfg.I2cSlaveAddr = MXL661_I2C_ADDR;
    apiCmd.MxLIf.cmdTunerPoweUpCfg.I2cSlaveAddr = MXL661_I2C_ADDR;
    apiCmd.MxLIf.cmdStartTuneCfg.I2cSlaveAddr = MXL661_I2C_ADDR;
    apiCmd.MxLIf.cmdAgcSetCfg.I2cSlaveAddr = MXL661_I2C_ADDR;
    apiCmd.MxLIf.cmdChanTuneCfg.I2cSlaveAddr = MXL661_I2C_ADDR;
    apiCmd.MxLIf.cmdSuprShiftAdjCfg.I2cSlaveAddr = MXL661_I2C_ADDR;
    apiCmd.MxLIf.cmdFineTuneCfg.I2cSlaveAddr = MXL661_I2C_ADDR;
    apiCmd.MxLIf.cmdEnableFineTuneCfg.I2cSlaveAddr = MXL661_I2C_ADDR;
    apiCmd.MxLIf.cmdHlfrfCfg.I2cSlaveAddr = MXL661_I2C_ADDR;
    apiCmd.MxLIf.cmdTunerSignalTypeReq.I2cSlaveAddr = MXL661_I2C_ADDR;
    apiCmd.MxLIf.cmdTunerLockReq.I2cSlaveAddr = MXL661_I2C_ADDR;
    apiCmd.MxLIf.cmdTunerPwrReq.I2cSlaveAddr = MXL661_I2C_ADDR;
    apiCmd.MxLIf.cmdTunerAgcLockReq.I2cSlaveAddr = MXL661_I2C_ADDR;
    apiCmd.MxLIf.cmdTunerAfcCorectionReq.I2cSlaveAddr = MXL661_I2C_ADDR;

    curSignalMode = MXL661_SIGNAL_NA;
    m_bInATVScanMode = MAPI_FALSE;
    m_bNeedResetAGCMode = MAPI_FALSE;
    m_bLastInATVScanMode = m_bInATVScanMode;
    m_eLastTunerMode = E_TUNER_INVALID;
    m_u8LastOtherMode = E_AUDIOSTANDARD_NOTSTANDARD_;
    AGC_Pin_Internal = MXL661_AGC1; //default : AGC1
    AGC_Pin_External = MXL661_AGC2;//default : AGC2
    // EosTek Patch Begin
    m_bTunerStatus = 0;
   // EosTek Patch End
}

device_tuner_MXL661::~device_tuner_MXL661()
{
    pthread_mutex_destroy(&m_mutex_SetTune);
}

#if (STR_ENABLE == 1)
MAPI_BOOL device_tuner_MXL661::TunerReset(void)
{
    printf("Reset Tuner device_tuner_MXL601\n");
    m_bNeedResetAGCMode = MAPI_TRUE;
    m_bDeviceBusy = 0;
    Connect();
    return MAPI_TRUE;
}
#endif

MAPI_BOOL device_tuner_MXL661::TunerInit(void)
{
    if (m_bDeviceBusy == 0)
        Connect();

    int intPTHChk;
    intPTHChk = PTH_RET_CHK(pthread_mutex_init(&m_mutex_SetTune, NULL));
    ASSERT(intPTHChk == 0);

    return MAPI_TRUE;
}

MAPI_BOOL device_tuner_MXL661::Connect(void)
{
    if (m_bDeviceBusy==1)
        return MAPI_FALSE;

    MXL_STATUS status = MXL_FAILED;

    //Step 1 : Soft Reset MxL661
    apiCmd.commandId = MXL661_DEV_SOFT_RESET_CFG;
    status = MxLWare661_API_ConfigDevice(&apiCmd);
   // EosTek Patch Begin
    if (MXL_FAILED == status)
    {
    m_bTunerStatus = MAPI_FALSE;
        printf("Error! MXL_DEV_SOFT_RESET_CFG\n");
    }
    else
    {
    m_bTunerStatus = MAPI_TRUE;
    }
// EosTek Patch End
    //Step 2 : Overwrite Default
    apiCmd.commandId = MXL661_DEV_OVERWRITE_DEFAULT_CFG;
    apiCmd.MxLIf.cmdOverwriteDefault.SingleSupply_3_3V = MXL_ENABLE;
    status = MxLWare661_API_ConfigDevice(&apiCmd);
    if (MXL_FAILED == status)
    {
        printf("Error! MXL_DEV_OVERWRITE_DEFAULT_CFG\n");
    }

    //Step 3 : XTAL Setting
    apiCmd.commandId = MXL661_DEV_XTAL_SET_CFG;
    apiCmd.MxLIf.cmdXtalCfg.XtalFreqSel = MXL661_XTAL_16MHz;

    // Force XtalCap 0. Improve accuracy of XTAL from HW side...
    // Dont modify this value...Keep it XtalCap 0.
    // XtalCap = 0, ST18CM-2-E isnt working. (scan no channel).
    // XtalCap = 31 is fine.
    apiCmd.MxLIf.cmdXtalCfg.XtalCap = 31;
    apiCmd.MxLIf.cmdXtalCfg.ClkOutEnable = MXL_DISABLE;
    apiCmd.MxLIf.cmdXtalCfg.ClkOutDiv = MXL_DISABLE;
    apiCmd.MxLIf.cmdXtalCfg.SingleSupply_3_3V = MXL_ENABLE;
    // Comments by MXL LD, 05/22:  if update to v2.1.10.9, need add this parameter
    apiCmd.MxLIf.cmdXtalCfg.XtalSharingMode = MXL_DISABLE;
    status = MxLWare661_API_ConfigDevice(&apiCmd);
    if (MXL_FAILED == status)
    {
        printf("Error! MXL_DEV_XTAL_SET_CFG\n");
    }

    //Step 4 : IF Out setting
    apiCmd.commandId = MXL661_DEV_IF_OUT_CFG;
    apiCmd.MxLIf.cmdIfOutCfg.IFOutFreq = IfFreqConversion(DTV_TUNER_IF_FREQ_KHz);
    apiCmd.MxLIf.cmdIfOutCfg.ManualFreqSet= MXL_DISABLE;  //arvin;;m10
    //apiCmd.MxLIf.cmdIfOutCfg.ManualIFOutFreqInKHz = 5380;//4100;  //arvin;;
    apiCmd.MxLIf.cmdIfOutCfg.IFInversion = MXL_DISABLE;
    apiCmd.MxLIf.cmdIfOutCfg.GainLevel = IF_OUT_CFG_GAIN_LEVEL_DVB;
    apiCmd.MxLIf.cmdIfOutCfg.PathSel = MXL661_IF_PATH1;
    status = MxLWare661_API_ConfigDevice(&apiCmd);
    if (MXL_FAILED == status)
    {
        printf("Error! MXL_DEV_IF_OUT_CFG\n");
    }

    //Step 5 : AGC Setting
    apiCmd.commandId = MXL661_TUNER_AGC_CFG;
    apiCmd.MxLIf.cmdAgcSetCfg.AgcSel = MXL661_AGC1;
    apiCmd.MxLIf.cmdAgcSetCfg.AgcType = MXL661_AGC_EXTERNAL;
    apiCmd.MxLIf.cmdAgcSetCfg.SetPoint = 66;
    apiCmd.MxLIf.cmdAgcSetCfg.AgcPolarityInverstion = MXL_DISABLE;
    status = MxLWare661_API_ConfigTuner(&apiCmd);
    if (MXL_FAILED == status)
    {
        printf("Error! MXL_TUNER_AGC_CFG\n");
    }

    // Comments by MXL LD, 05/22: Move tuner power up setting API calling from Channel tune to init process
    //Step 9 : Power up setting
    apiCmd.commandId = MXL661_TUNER_POWER_UP_CFG;
    apiCmd.MxLIf.cmdTunerPoweUpCfg.Enable = MXL_ENABLE;
    status = MxLWare661_API_ConfigTuner(&apiCmd);
    if (MXL_FAILED == status)
    {
        printf("Error! MXL_TUNER_POWER_UP_CFG\n");
    }

    //Step 6 :set AFC range
    Tuner_AFC_RangeSet(MXL661_AFC_DISABLE);

    curSignalMode = MXL661_SIGNAL_NA;

    m_eLastTunerMode = E_TUNER_INVALID;
    m_u8LastOtherMode = E_AUDIOSTANDARD_NOTSTANDARD_;

    m_bDeviceBusy=1;
    m_bInATVFinetuneMode = MXL_DISABLE; //Add to fix fineTune garbage(Mantis 0586666)
    u32GlobalATVRfFreqKHz = 0; //Add to fix fineTune garbage(Mantis 0586666)
    u32chantuneFreq = 0;
    return MAPI_TRUE;
}
 // EosTek Patch Begin
MAPI_BOOL device_tuner_MXL661::TunerStatus(void)
{

    return m_bTunerStatus;
}
// EosTek Patch End


MAPI_BOOL device_tuner_MXL661::Disconnect()
{
    printf("device_tuner_MXL661: Disconnect\n");

    //   m_bDeviceBusy=0;
    return MAPI_TRUE;
}

// Comments by MXL LD, 05/22:  Tuner_AFC_RangeSet function is suitable for v2.1.10.5 version driver
//      in v2.1.10.9 driver, shall use Tuner AFC set API instead of calling below function.
/* This function is used to define the Tuner AFC range (in kHz unit) */
MAPI_BOOL device_tuner_MXL661::Tuner_AFC_RangeSet(MXL661_AFC_RANGE_E u32FreqOffsetKHz)
{
    MXL_STATUS status = MXL_FAILED;
    apiCmd.commandId = MXL661_TUNER_AFC_CFG;
    apiCmd.MxLIf.cmdAfcCfg.I2cSlaveAddr = MXL661_I2C_ADDR;
    apiCmd.MxLIf.cmdAfcCfg.AfcRangeInKHz = u32FreqOffsetKHz;
    status = MxLWare661_API_ConfigTuner(&apiCmd);
    return status;
}

MAPI_BOOL device_tuner_MXL661::ATV_SetTune( MAPI_U32 u32FreqKHz,RFBAND eBand, EN_TUNER_MODE eMode,MAPI_U8 u8OtherMode )
{
    MXL_STATUS status = MXL_FAILED;
    MXL661_IF_FREQ_E mxl_IF_Out_Freq;
    MAPI_U32 u32IFOutFreqinKHz = 5000;
    MXL_BOOL mxl_IF_Inversion;
    MXL_BOOL fineTuneScaleUp; //Add to fix fineTune garbage(Mantis 0586666)

    MAPI_U8  u8_lock1 = 0;
    MAPI_U8  u8_lock2 = 0;
    UNUSED(eBand);
    pthread_mutex_lock(&m_mutex_SetTune);
    if( (m_bInATVFinetuneMode == MXL_ENABLE) && (CheckFineTuneRange(u32FreqKHz) == MAPI_TRUE) )  // Do FineTune
    {

#if 0 //AFC is already disable in Connect()
        // when do fine tune , need stop tuner AFC function
        Tuner_AFC_RangeSet(MXL661_AFC_DISABLE);
#endif

        // Determine the fine tune direction
        if (u32FreqKHz > u32GlobalATVRfFreqKHz)
        {
            fineTuneScaleUp = MXL_FINE_TUNE_STEP_UP;
            printf("\x1b[34m[Hao] %s MXL_FINE_TUNE_STEP_UP [%d] \x1b[0m\n",__FUNCTION__,__LINE__);
        }
        else
        {
            fineTuneScaleUp = MXL_FINE_TUNE_STEP_DOWN;
            printf("\x1b[34m[Hao] %s MXL_FINE_TUNE_STEP_DOWN [%d] \x1b[0m\n",__FUNCTION__,__LINE__);
        }

        // call MXL_TUNER_ENABLE_FINE_TUNE_CFG and MXL_TUNER_FINE_TUNE_CFG API functions
        apiCmd.commandId = MXL661_TUNER_ENABLE_FINE_TUNE_CFG;
        apiCmd.MxLIf.cmdEnableFineTuneCfg.EnableFineTune = MXL_ENABLE;
        status = MxLWare661_API_ConfigTuner(&apiCmd);
        if (MXL_FAILED == status)
        {
            printf("Error! MXL661_TUNER_ENABLE_FINE_TUNE_CFG\n");
        }

        apiCmd.commandId = MXL661_TUNER_FINE_TUNE_CFG;
        apiCmd.MxLIf.cmdFineTuneCfg.ScaleUp = fineTuneScaleUp;
        status = MxLWare661_API_ConfigTuner(&apiCmd);
        if (MXL_FAILED == status)
        {
            printf("Error! MXL661_TUNER_FINE_TUNE_CFG\n");
        }
    }
    else    // Do SetAppMode and ChannelTune
    {

        if( (m_bInATVFinetuneMode == MXL_ENABLE) && (CheckFineTuneRange(u32FreqKHz) == MAPI_FALSE) )
        {
            // if the frequency is out of fine-tune range in fine-tune mode, do ChannelTune command instead of FineTune command.
            if( u32FreqKHz >= (u32chantuneFreq + MAX_FINE_TUNE_STEP*FREQ_STEP) )
            {
                u32FreqKHz = u32chantuneFreq + MAX_FINE_TUNE_STEP*FREQ_STEP;
            }
            else    // u32FreqKHz <= (u32chantuneFreq - MAX_FINE_TUNE_STEP*FREQ_STEP)
            {
                u32FreqKHz = u32chantuneFreq - MAX_FINE_TUNE_STEP*FREQ_STEP;
            }
        }

        if((m_eLastTunerMode != eMode) || (m_u8LastOtherMode != u8OtherMode) || (m_bLastInATVScanMode != m_bInATVScanMode))
        {
            m_eLastTunerMode = eMode;
            m_u8LastOtherMode = u8OtherMode;
            m_bLastInATVScanMode = m_bInATVScanMode;

            ConfigAGCMode(eMode);

            //Step 4 : IF Out setting
            if(eMode == E_TUNER_ATV_SECAM_L_PRIME_MODE)
            {
                mxl_IF_Out_Freq = MXL661_IF_5MHz;
                u32IFOutFreqinKHz = 5000;
                mxl_IF_Inversion = MXL_DISABLE;
                curSignalMode = MXL661_ANA_SECAM_L;
            }
            else
            {
                switch(u8OtherMode)
                {
                    case E_AUDIOSTANDARD_BG_:
                    case E_AUDIOSTANDARD_BG_A2_:
                    case E_AUDIOSTANDARD_BG_NICAM_:
                         mxl_IF_Out_Freq = MXL661_IF_5MHz;
                         u32IFOutFreqinKHz = 5000;
                         mxl_IF_Inversion = MXL_ENABLE;
                         curSignalMode = MXL661_ANA_PAL_BG;
                         break;
                    case E_AUDIOSTANDARD_I_:
                         mxl_IF_Out_Freq = MXL661_IF_5MHz;
                         u32IFOutFreqinKHz = 5000;
                         mxl_IF_Inversion = MXL_ENABLE;
                         curSignalMode = MXL661_ANA_PAL_I;
                         break;
                    case E_AUDIOSTANDARD_DK_:
                    case E_AUDIOSTANDARD_DK1_A2_:
                    case E_AUDIOSTANDARD_DK2_A2_:
                    case E_AUDIOSTANDARD_DK3_A2_:
                    case E_AUDIOSTANDARD_DK_NICAM_:
                         mxl_IF_Out_Freq = MXL661_IF_5MHz;
                         u32IFOutFreqinKHz = 5000;
                         mxl_IF_Inversion = MXL_ENABLE;
                         curSignalMode = MXL661_ANA_PAL_D;
                         break;
                    case E_AUDIOSTANDARD_M_:
                    case E_AUDIOSTANDARD_M_BTSC_:
                    case E_AUDIOSTANDARD_M_A2_:
                    case E_AUDIOSTANDARD_M_EIA_J_:
                         mxl_IF_Out_Freq = MXL661_IF_5MHz;
                         u32IFOutFreqinKHz = 5000;
                         mxl_IF_Inversion = MXL_ENABLE;
                         curSignalMode = MXL661_ANA_NTSC_MODE;
                         break;
                    case E_AUDIOSTANDARD_L_:
                         mxl_IF_Out_Freq = MXL661_IF_5MHz;
                         u32IFOutFreqinKHz = 5000;
                         mxl_IF_Inversion = MXL_ENABLE;
                         curSignalMode = MXL661_ANA_SECAM_L;
                         break;
                    case E_AUDIOSTANDARD_NOTSTANDARD_:
                    case 0xFF:
                    default:
                         mxl_IF_Out_Freq = MXL661_IF_5MHz;
                         u32IFOutFreqinKHz = 5000;
                         mxl_IF_Inversion = MXL_ENABLE;
                         curSignalMode = MXL661_ANA_PAL_D;
                         break;
                }
            }

            apiCmd.commandId = MXL661_DEV_IF_OUT_CFG;
            apiCmd.MxLIf.cmdIfOutCfg.IFOutFreq = mxl_IF_Out_Freq;
            apiCmd.MxLIf.cmdIfOutCfg.ManualFreqSet= MXL_DISABLE;
            apiCmd.MxLIf.cmdIfOutCfg.IFInversion = mxl_IF_Inversion;
            if(m_bInATVScanMode == MAPI_TRUE)
            {
                apiCmd.MxLIf.cmdIfOutCfg.GainLevel = IF_OUT_CFG_GAIN_LEVEL_ATV_SCAN;
            }
            else
            {
                if(eMode == E_TUNER_ATV_SECAM_L_PRIME_MODE || u8OtherMode == E_AUDIOSTANDARD_L_)
                {
                    apiCmd.MxLIf.cmdIfOutCfg.GainLevel = IF_OUT_CFG_GAIN_LEVEL_ATV_SECAM;
                }
                else
                {
                    apiCmd.MxLIf.cmdIfOutCfg.GainLevel = IF_OUT_CFG_GAIN_LEVEL_ATV_NORMAL;
                }
            }
            apiCmd.MxLIf.cmdIfOutCfg.PathSel = MXL661_IF_PATH1;

            status = MxLWare661_API_ConfigDevice(&apiCmd);
            if (MXL_FAILED == status)
            {
                printf("Error! MXL_DEV_IF_OUT_CFG\n");
            }

            //Step 6 : Application Mode setting
            apiCmd.commandId = MXL661_TUNER_MODE_CFG;

            if(eMode == E_TUNER_ATV_SECAM_L_PRIME_MODE)
            {
                if(u8OtherMode == E_AUDIOSTANDARD_I_)
                    apiCmd.MxLIf.cmdModeCfg.SignalMode = MXL661_ANA_SECAM_I;
                else
                    apiCmd.MxLIf.cmdModeCfg.SignalMode = MXL661_ANA_SECAM_L;
            }
            else if (eMode == E_TUNER_ATV_NTSC_MODE)
            {
                apiCmd.MxLIf.cmdModeCfg.SignalMode = MXL661_ANA_NTSC_MODE;
                apiCmd.MxLIf.cmdChanTuneCfg.BandWidth = MXL661_ANA_TV_DIG_CABLE_BW_6MHz;
            }
            else
            {
                switch(u8OtherMode)
                {
                    case E_AUDIOSTANDARD_BG_:
                    case E_AUDIOSTANDARD_BG_A2_:
                    case E_AUDIOSTANDARD_BG_NICAM_:
                         apiCmd.MxLIf.cmdModeCfg.SignalMode = MXL661_ANA_PAL_BG;
                         apiCmd.MxLIf.cmdChanTuneCfg.BandWidth = MXL661_ANA_TV_DIG_CABLE_BW_7MHz;
                         break;
                    case E_AUDIOSTANDARD_I_:
                         apiCmd.MxLIf.cmdModeCfg.SignalMode = MXL661_ANA_PAL_I;
                         apiCmd.MxLIf.cmdChanTuneCfg.BandWidth = MXL661_ANA_TV_DIG_CABLE_BW_8MHz;
                         break;
                    case E_AUDIOSTANDARD_DK_:
                    case E_AUDIOSTANDARD_DK1_A2_:
                    case E_AUDIOSTANDARD_DK2_A2_:
                    case E_AUDIOSTANDARD_DK3_A2_:
                    case E_AUDIOSTANDARD_DK_NICAM_:
                         apiCmd.MxLIf.cmdModeCfg.SignalMode = MXL661_ANA_PAL_D;
                         apiCmd.MxLIf.cmdChanTuneCfg.BandWidth = MXL661_ANA_TV_DIG_CABLE_BW_8MHz;
                         break;
                    case E_AUDIOSTANDARD_M_:
                    case E_AUDIOSTANDARD_M_BTSC_:
                    case E_AUDIOSTANDARD_M_A2_:
                    case E_AUDIOSTANDARD_M_EIA_J_:
                    case E_AUDIOSTANDARD_NOTSTANDARD_:
                         apiCmd.MxLIf.cmdModeCfg.SignalMode = MXL661_ANA_NTSC_MODE;
                         apiCmd.MxLIf.cmdChanTuneCfg.BandWidth = MXL661_ANA_TV_DIG_CABLE_BW_6MHz;
                         break;
                    case E_AUDIOSTANDARD_L_:
                         apiCmd.MxLIf.cmdModeCfg.SignalMode = MXL661_ANA_SECAM_L;
                         apiCmd.MxLIf.cmdChanTuneCfg.BandWidth = MXL661_ANA_TV_DIG_CABLE_BW_8MHz;
                         break;
                    case 0xFF:
                    default:
                         apiCmd.MxLIf.cmdModeCfg.SignalMode = MXL661_ANA_PAL_D;
                         apiCmd.MxLIf.cmdChanTuneCfg.BandWidth = MXL661_ANA_TV_DIG_CABLE_BW_8MHz;
                         break;
                }
            }

            apiCmd.MxLIf.cmdModeCfg.IFOutFreqinKHz = u32IFOutFreqinKHz;
            apiCmd.MxLIf.cmdModeCfg.XtalFreqSel = MXL661_XTAL_16MHz;
            if(m_bInATVScanMode == MAPI_TRUE)
            {
                apiCmd.MxLIf.cmdModeCfg.IFOutGainLevel = IF_OUT_CFG_GAIN_LEVEL_ATV_SCAN;
            }
            else
            {
                if(eMode == E_TUNER_ATV_SECAM_L_PRIME_MODE || u8OtherMode == E_AUDIOSTANDARD_L_)
                {
                    apiCmd.MxLIf.cmdModeCfg.IFOutGainLevel = IF_OUT_CFG_GAIN_LEVEL_ATV_SECAM;
                }
                else
                {
                    apiCmd.MxLIf.cmdModeCfg.IFOutGainLevel = IF_OUT_CFG_GAIN_LEVEL_ATV_NORMAL;
                }
            }

            status = MxLWare661_API_ConfigTuner(&apiCmd);
            if (MXL_FAILED == status)
            {
                printf("Error! MXL_TUNER_MODE_CFG\n");
            }
        }

        //fine-tune must be disabled right before doing channel tune, otherwise fine-tune will not works. 20121204
        apiCmd.commandId = MXL661_TUNER_ENABLE_FINE_TUNE_CFG;
        apiCmd.MxLIf.cmdEnableFineTuneCfg.I2cSlaveAddr = MXL661_I2C_ADDR;
        apiCmd.MxLIf.cmdEnableFineTuneCfg.EnableFineTune  = MXL_DISABLE;
        status = MxLWare661_API_ConfigTuner(&apiCmd);

        //Step 7 : Channel frequency & bandwidth setting
        apiCmd.commandId = MXL661_TUNER_CHAN_TUNE_CFG;
        apiCmd.MxLIf.cmdChanTuneCfg.TuneType = MXL661_VIEW_MODE;
        apiCmd.MxLIf.cmdChanTuneCfg.XtalFreqSel = MXL661_XTAL_16MHz;
        apiCmd.MxLIf.cmdChanTuneCfg.IFOutFreqinKHz = u32IFOutFreqinKHz;

        if(eMode == E_TUNER_ATV_SECAM_L_PRIME_MODE)
        {
            apiCmd.MxLIf.cmdChanTuneCfg.SignalMode = MXL661_ANA_SECAM_L;
            apiCmd.MxLIf.cmdChanTuneCfg.BandWidth = MXL661_ANA_TV_DIG_CABLE_BW_8MHz;
            apiCmd.MxLIf.cmdChanTuneCfg.FreqInHz =(u32FreqKHz-2750)*1000L;//Freq*100*1000;
        }
        else
        {
            switch(u8OtherMode)
            {
                case E_AUDIOSTANDARD_BG_:
                case E_AUDIOSTANDARD_BG_A2_:
                case E_AUDIOSTANDARD_BG_NICAM_:
                     apiCmd.MxLIf.cmdChanTuneCfg.SignalMode = MXL661_ANA_PAL_BG;
                     apiCmd.MxLIf.cmdChanTuneCfg.BandWidth = MXL661_ANA_TV_DIG_CABLE_BW_7MHz;
                     apiCmd.MxLIf.cmdChanTuneCfg.FreqInHz =(u32FreqKHz+2250)*1000L;//Freq*100*1000;
                     break;

                case E_AUDIOSTANDARD_I_:
                     apiCmd.MxLIf.cmdChanTuneCfg.SignalMode = MXL661_ANA_PAL_I;
                     apiCmd.MxLIf.cmdChanTuneCfg.BandWidth = MXL661_ANA_TV_DIG_CABLE_BW_8MHz;
                     apiCmd.MxLIf.cmdChanTuneCfg.FreqInHz =(u32FreqKHz+2750)*1000L;//Freq*100*1000;
                     break;

                case E_AUDIOSTANDARD_DK_:
                case E_AUDIOSTANDARD_DK1_A2_:
                case E_AUDIOSTANDARD_DK2_A2_:
                case E_AUDIOSTANDARD_DK3_A2_:
                case E_AUDIOSTANDARD_DK_NICAM_:
                     apiCmd.MxLIf.cmdChanTuneCfg.SignalMode = MXL661_ANA_PAL_D;
                     apiCmd.MxLIf.cmdChanTuneCfg.BandWidth = MXL661_ANA_TV_DIG_CABLE_BW_8MHz;
                     apiCmd.MxLIf.cmdChanTuneCfg.FreqInHz =(u32FreqKHz+2750)*1000L;//Freq*100*1000;
                     break;

                case E_AUDIOSTANDARD_M_:
                case E_AUDIOSTANDARD_M_BTSC_:
                case E_AUDIOSTANDARD_M_A2_:
                case E_AUDIOSTANDARD_M_EIA_J_:
                     apiCmd.MxLIf.cmdChanTuneCfg.SignalMode = MXL661_ANA_NTSC_MODE;
                     apiCmd.MxLIf.cmdChanTuneCfg.BandWidth = MXL661_ANA_TV_DIG_CABLE_BW_6MHz;
                     apiCmd.MxLIf.cmdChanTuneCfg.FreqInHz =(u32FreqKHz+1750)*1000L;//Freq*100*1000;
                     break;
                case E_AUDIOSTANDARD_L_:
                     apiCmd.MxLIf.cmdChanTuneCfg.SignalMode = MXL661_ANA_SECAM_L;
                     apiCmd.MxLIf.cmdChanTuneCfg.BandWidth = MXL661_ANA_TV_DIG_CABLE_BW_8MHz;
                     apiCmd.MxLIf.cmdChanTuneCfg.FreqInHz =(u32FreqKHz+2750)*1000L;//Freq*100*1000;
                     break;
                default:
                     apiCmd.MxLIf.cmdChanTuneCfg.SignalMode = MXL661_ANA_PAL_D;
                     apiCmd.MxLIf.cmdChanTuneCfg.BandWidth = MXL661_ANA_TV_DIG_CABLE_BW_8MHz;
                     apiCmd.MxLIf.cmdChanTuneCfg.FreqInHz =(u32FreqKHz+2750)*1000L;//Freq*100*1000;
                     break;
            }
        }

        status = MxLWare661_API_ConfigTuner(&apiCmd);
        if (MXL_FAILED == status)
        {
            printf("Error! MXL_TUNER_CHAN_TUNE_CFG\n");
        }

#if 0// 1 //Although AFC is already disabled in Connect(), disable fine-tune command(above) will enable AFC.
        //Step 8 : Disable AFC in tuner if scan
        if(m_bInATVScanMode == MAPI_TRUE)
        {
            Tuner_AFC_RangeSet(MXL661_AFC_DISABLE);
        }
        else
        {
            Tuner_AFC_RangeSet(MXL661_AFC_DISABLE);
        }
#endif

        // Comments by MXL LD, 05/22:  remove MXL_TUNER_POWER_UP_CFG API calling from tuner channel tune to init phase
#if 0
        //Step 9 : Power up setting
        apiCmd.commandId = MXL_TUNER_POWER_UP_CFG;
        apiCmd.MxLIf.cmdTunerPoweUpCfg.Enable = MXL_ENABLE;
        status = MxLWare661_API_ConfigTuner(&apiCmd);
        if (MXL_FAILED == status)
        {
            printf("Error! MXL_TUNER_POWER_UP_CFG\n");
        }
#else
        // Comments by MXL LD, 05/22: replace MXL_TUNER_POWER_UP_CFG calling with MXL_TUNER_START_TUNE_CFG API
    	//   First do disable tune operation
        //Step 10 : Sequencer setting: disable tune
        apiCmd.commandId = MXL661_TUNER_START_TUNE_CFG;
        apiCmd.MxLIf.cmdStartTuneCfg.StartTune = MXL_DISABLE;
        status = MxLWare661_API_ConfigTuner(&apiCmd);
        if (MXL_FAILED == status)
        {
            printf("Error! MXL_TUNER_START_TUNE_CFG\n");
        }
#endif
        //Step 10 : Sequencer setting : enable tune
        apiCmd.commandId = MXL661_TUNER_START_TUNE_CFG;
        apiCmd.MxLIf.cmdStartTuneCfg.StartTune = MXL_ENABLE;
        status = MxLWare661_API_ConfigTuner(&apiCmd);
        if (MXL_FAILED == status)
        {
            printf("Error! MXL_TUNER_START_TUNE_CFG\n");
        }

        // Save the frequency set by ChannelTune command
        u32chantuneFreq = u32FreqKHz;
    }
    // Save current RF frequency point
    u32GlobalATVRfFreqKHz = u32FreqKHz;

    MxL_API_GetLockStatus(&u8_lock1,&u8_lock2);
    if ((u8_lock1+u8_lock2) == 2)
        printf("MxL Tuner Lock....\n");
    else
        printf("MxL Tuner unLock....\n");

    pthread_mutex_unlock(&m_mutex_SetTune);
    // wait for signal stable
    if(eMode == E_TUNER_ATV_SECAM_L_PRIME_MODE)
    {
        MxLWare661_OEM_Sleep(SECAM_L_PRIME_TUNER_PLL_STABLE_TIME);
    }
    else
    {
        MxLWare661_OEM_Sleep(TUNER_PLL_STABLE_TIME); // added for ATV randomly missing channel.
    }

    return MAPI_TRUE;
}



MAPI_BOOL device_tuner_MXL661::DTV_SetTune( double Freq, RF_CHANNEL_BANDWIDTH eBandWidth, EN_TUNER_MODE eMode)
{
    MXL_STATUS status = MXL_FAILED;
    MAPI_U8  u8_lock1 = 0;
    MAPI_U8  u8_lock2 = 0;

//    if (eMode == E_TUNER_DTV_DVB_T2_MODE)
//        eMode = E_TUNER_DTV_DVB_T_MODE;

    if(m_eLastTunerMode != eMode)
    {
        m_eLastTunerMode = eMode;

        ConfigAGCMode(eMode);

        //Step 6 : Application Mode setting
        apiCmd.commandId = MXL661_TUNER_MODE_CFG;
        apiCmd.MxLIf.cmdModeCfg.IFOutFreqinKHz = TUNER_DTV_IF_FREQ_KHz;
        if(eMode == E_TUNER_DTV_DVB_T_MODE)
        {
            apiCmd.MxLIf.cmdModeCfg.SignalMode = MXL661_DIG_DVB_T_DTMB;
            apiCmd.MxLIf.cmdModeCfg.IFOutGainLevel = IF_OUT_CFG_GAIN_LEVEL_DVB;
        }
        else if(eMode == E_TUNER_DTV_DVB_T2_MODE)
        {
            apiCmd.MxLIf.cmdModeCfg.SignalMode = MXL661_DIG_DVB_T_DTMB;
            apiCmd.MxLIf.cmdModeCfg.IFOutGainLevel = IF_OUT_CFG_GAIN_LEVEL_DVB_T2;
        }
        else if(eMode == E_TUNER_DTV_DTMB_MODE)
        {
            apiCmd.MxLIf.cmdModeCfg.SignalMode = MXL661_DIG_DVB_T_DTMB;
            apiCmd.MxLIf.cmdModeCfg.IFOutGainLevel = IF_OUT_CFG_GAIN_LEVEL_DTMB;
            apiCmd.MxLIf.cmdModeCfg.IFOutFreqinKHz = DTMB_TUNER_IF_FREQ_KHz;
        }
        else if(eMode == E_TUNER_DTV_ISDB_MODE)
        {
            apiCmd.MxLIf.cmdModeCfg.SignalMode = MXL661_DIG_ISDBT_ATSC;
            apiCmd.MxLIf.cmdModeCfg.IFOutGainLevel = IF_OUT_CFG_GAIN_LEVEL_ISDB;
        }
        else
        {
            apiCmd.MxLIf.cmdModeCfg.SignalMode = MXL661_DIG_DVB_C;
            apiCmd.MxLIf.cmdModeCfg.IFOutGainLevel = IF_OUT_CFG_GAIN_LEVEL_DVB;
        }


        apiCmd.MxLIf.cmdModeCfg.XtalFreqSel = MXL661_XTAL_16MHz;
        status = MxLWare661_API_ConfigTuner(&apiCmd);
        if (MXL_FAILED == status)
        {
            printf("Error! MXL_TUNER_MODE_CFG\n");
        }

    }

    //Step 7 : Channel frequency & bandwidth setting
    apiCmd.commandId = MXL661_TUNER_CHAN_TUNE_CFG;
    apiCmd.MxLIf.cmdChanTuneCfg.TuneType = MXL661_VIEW_MODE;


    if ((eMode == E_TUNER_DTV_DVB_T_MODE) ||
        (eMode == E_TUNER_DTV_DVB_T2_MODE) ||
        (eMode == E_TUNER_DTV_DTMB_MODE) ||
        (eMode == E_TUNER_DTV_ISDB_MODE))
	{
		if(eBandWidth == E_RF_CH_BAND_6MHz)
		{
			apiCmd.MxLIf.cmdChanTuneCfg.BandWidth = MXL661_DIG_TERR_BW_6MHz;
		}
		else if(eBandWidth == E_RF_CH_BAND_7MHz)
		{
			apiCmd.MxLIf.cmdChanTuneCfg.BandWidth = MXL661_DIG_TERR_BW_7MHz;
		}
		else
		{
			apiCmd.MxLIf.cmdChanTuneCfg.BandWidth = MXL661_DIG_TERR_BW_8MHz;
		}
	}
	else
	{
		if(eBandWidth == E_RF_CH_BAND_6MHz)
		{
		    apiCmd.MxLIf.cmdChanTuneCfg.BandWidth = MXL661_ANA_TV_DIG_CABLE_BW_6MHz;
		}
		else if(eBandWidth == E_RF_CH_BAND_7MHz)
		{
		    apiCmd.MxLIf.cmdChanTuneCfg.BandWidth = MXL661_ANA_TV_DIG_CABLE_BW_7MHz;
		}
		else
		{
		    apiCmd.MxLIf.cmdChanTuneCfg.BandWidth = MXL661_ANA_TV_DIG_CABLE_BW_8MHz;
        }
    }

    if (eMode == E_TUNER_DTV_ATSC_MODE)
        apiCmd.MxLIf.cmdChanTuneCfg.FreqInHz =  Freq*1000L;
    else
        apiCmd.MxLIf.cmdChanTuneCfg.FreqInHz =  Freq*1000000L;

    apiCmd.MxLIf.cmdChanTuneCfg.IFOutFreqinKHz = TUNER_DTV_IF_FREQ_KHz;
    if(eMode == E_TUNER_DTV_DVB_T_MODE)
    {
        apiCmd.MxLIf.cmdChanTuneCfg.SignalMode =  MXL661_DIG_DVB_T_DTMB;
    }
    else if(eMode == E_TUNER_DTV_DVB_T2_MODE)
    {
        apiCmd.MxLIf.cmdChanTuneCfg.SignalMode =  MXL661_DIG_DVB_T_DTMB;
    }
    else if(eMode == E_TUNER_DTV_DTMB_MODE)
    {
        apiCmd.MxLIf.cmdChanTuneCfg.SignalMode =  MXL661_DIG_DVB_T_DTMB;
		apiCmd.MxLIf.cmdChanTuneCfg.IFOutFreqinKHz = DTMB_TUNER_IF_FREQ_KHz;
    }
    else if(eMode == E_TUNER_DTV_ISDB_MODE)
    {
        apiCmd.MxLIf.cmdChanTuneCfg.SignalMode =  MXL661_DIG_ISDBT_ATSC;
    }
    else
    {
        apiCmd.MxLIf.cmdChanTuneCfg.SignalMode =  MXL661_DIG_DVB_C;
    }

    apiCmd.MxLIf.cmdChanTuneCfg.XtalFreqSel = MXL661_XTAL_16MHz;

    status = MxLWare661_API_ConfigTuner(&apiCmd);
    if (MXL_FAILED == status)
    {
        printf("Error! MXL_TUNER_CHAN_TUNE_CFG\n");
    }

	// Comments by MXL LD, 05/22:  call MXL_TUNER_START_TUNE_CFG instead of
	//     call MXL_TUNER_POWER_UP_CFG
#if 0
    //Step 8 : Power up setting
    apiCmd.commandId = MXL_TUNER_POWER_UP_CFG;
    apiCmd.MxLIf.cmdTunerPoweUpCfg.Enable = MXL_ENABLE;
    status = MxLWare661_API_ConfigTuner(&apiCmd);
    if (MXL_FAILED == status)
    {
        printf("Error! MXL_TUNER_POWER_UP_CFG\n");
    }
#else
    //Step 9 : Sequencer setting
    apiCmd.commandId = MXL661_TUNER_START_TUNE_CFG;
    apiCmd.MxLIf.cmdStartTuneCfg.StartTune = MXL_DISABLE;
    status = MxLWare661_API_ConfigTuner(&apiCmd);
    if (MXL_FAILED == status)
    {
        printf("Error! MXL_TUNER_START_TUNE_CFG\n");
    }
#endif

    //Step 9 : Sequencer setting
    apiCmd.commandId = MXL661_TUNER_START_TUNE_CFG;
    apiCmd.MxLIf.cmdStartTuneCfg.StartTune = MXL_ENABLE;
    status = MxLWare661_API_ConfigTuner(&apiCmd);
    if (MXL_FAILED == status)
    {
        printf("Error! MXL_TUNER_START_TUNE_CFG\n");
    }

    MxL_API_GetLockStatus(&u8_lock1,&u8_lock2);
    if ((u8_lock1+u8_lock2) == 2)
        printf("MxL Tuner Lock....\n");
    else
        printf("MxL Tuner unLock....\n");

    // Wait 30ms
    MxLWare661_OEM_Sleep(TUNER_PLL_STABLE_TIME);

    return MAPI_TRUE;
}

MAPI_BOOL device_tuner_MXL661::ExtendCommand( MAPI_U8 u8SubCmd, MAPI_U32 u32Param1, MAPI_U32 u32Param2, void* pvoidParam3)
{
    switch(u8SubCmd)
    {
        case mapi_tuner_datatype::E_TUNER_SUBCMD_GET_FREQ_STEP:
        {
            EN_FREQ_STEP *eFreqStep = (EN_FREQ_STEP*)pvoidParam3;
            *eFreqStep = TN_FREQ_STEP;
        }
        break;

        case mapi_tuner_datatype::E_TUNER_SUBCMD_GET_IF_FREQ:
        {
            MAPI_U16 *u16IFFreq = (MAPI_U16 *)pvoidParam3;
            *u16IFFreq = ATV_TUNER_IF_FREQ_KHz;
        }
        break;

        case mapi_tuner_datatype::E_TUNER_SUBCMD_GET_L_PRIME_IF_FREQ:
        {
            MAPI_U16 *u16IFFreq = (MAPI_U16 *)pvoidParam3;
            *u16IFFreq = TUNER_L_PRIME_IF_FREQ_KHz;
        }
        break;

        case mapi_tuner_datatype::E_TUNER_SUBCMD_GET_VHF_LOWMIN_FREQ:
        {
            MAPI_U32 *u32Freq = (MAPI_U32 *)pvoidParam3;
            *u32Freq = TUNER_VHF_LOWMIN_FREQ;
        }
        break;

        case mapi_tuner_datatype::E_TUNER_SUBCMD_GET_VHF_LOWMAX_FREQ:
        {
            MAPI_U32 *u32Freq = (MAPI_U32 *)pvoidParam3;
            *u32Freq = TUNER_VHF_LOWMAX_FREQ;
        }
        break;

        case mapi_tuner_datatype::E_TUNER_SUBCMD_GET_VHF_HIGHMIN_FREQ:
        {
            MAPI_U32 *u32Freq = (MAPI_U32 *)pvoidParam3;
            *u32Freq = TUNER_VHF_HIGHMIN_FREQ;
        }
        break;

        case mapi_tuner_datatype::E_TUNER_SUBCMD_GET_VHF_HIGHMAX_FREQ:
        {
            MAPI_U32 *u32Freq = (MAPI_U32 *)pvoidParam3;
            *u32Freq = TUNER_VHF_HIGHMAX_FREQ;
        }
        break;

        case mapi_tuner_datatype::E_TUNER_SUBCMD_GET_UHF_MIN_FREQ:
        {
            MAPI_U32 *u32Freq = (MAPI_U32 *)pvoidParam3;
            *u32Freq = TUNER_UHF_MIN_FREQ;
        }
        break;

        case mapi_tuner_datatype::E_TUNER_SUBCMD_GET_UHF_MAX_FREQ:  //arvin;;
        {
            MAPI_U32 *u32Freq = (MAPI_U32 *)pvoidParam3;
            *u32Freq = TUNER_UHF_MAX_FREQ;
        }
        break;

        case mapi_tuner_datatype::E_TUNER_SUBCMD_GET_VIF_TUNER_TYPE:
        {
            MAPI_U8 *u8TunerType = (MAPI_U8 *)pvoidParam3;
            *u8TunerType = VIF_TUNER_TYPE;
        }
        break;

        case mapi_tuner_datatype::E_TUNER_SUBCMD_GET_VIF_PARA:
        {
            mapi_vif_datatype::stVIFInitialIn *stVIFPara = (mapi_vif_datatype::stVIFInitialIn *)pvoidParam3;
            stVIFPara->VifCrRate_B = VIF_CR_RATE_B;
            stVIFPara->VifCrInvert_B = VIF_CR_INVERT_B;
            stVIFPara->VifCrRate_GH = VIF_CR_RATE_GH;
            stVIFPara->VifCrInvert_GH =VIF_CR_INVERT_GH;
            stVIFPara->VifCrRate_DK = VIF_CR_RATE_DK;
            stVIFPara->VifCrInvert_DK = VIF_CR_INVERT_DK;
            stVIFPara->VifCrRate_I = VIF_CR_RATE_I;
            stVIFPara->VifCrInvert_I = VIF_CR_INVERT_I;
            stVIFPara->VifCrRate_L = VIF_CR_RATE_L;
            stVIFPara->VifCrInvert_L = VIF_CR_INVERT_L;
            stVIFPara->VifCrRate_LL = VIF_CR_RATE_LL;
            stVIFPara->VifCrInvert_LL = VIF_CR_INVERT_LL;
            stVIFPara->VifCrRate_MN = VIF_CR_RATE_MN;
            stVIFPara->VifCrInvert_MN = VIF_CR_INVERT_MN;

        }
        break;

        case mapi_tuner_datatype::E_TUNER_SUBCMD_GET_VIF_NOTCH_SOSFILTER:
        {
            MAPI_U16 *u16NotchArray = (MAPI_U16 *)pvoidParam3;
            MAPI_U16 *u16SosArray = &u16NotchArray[u32Param2];

            switch((mapi_vif_datatype::EN_VIF_SOUND_SYSTEM)u32Param1)
            {
                case mapi_vif_datatype::E_VIF_SOUND_B_STEREO_A2:
                {
                     u16NotchArray[0] = N_A1_C0_B_A2;
                     u16NotchArray[1] = N_A1_C1_B_A2;
                     u16NotchArray[2] = N_A1_C2_B_A2;

                     u16NotchArray[3] = N_A2_C0_B_A2;
                     u16NotchArray[4] = N_A2_C1_B_A2;
                     u16NotchArray[5] = N_A2_C2_B_A2;

                     u16SosArray[0] = S_11_C0_B_A2;
                     u16SosArray[1] = S_11_C1_B_A2;
                     u16SosArray[2] = S_11_C2_B_A2;
                     u16SosArray[3] = S_11_C3_B_A2;
                     u16SosArray[4] = S_11_C4_B_A2;

                     u16SosArray[5] = S_12_C0_B_A2;
                     u16SosArray[6] = S_12_C1_B_A2;
                     u16SosArray[7] = S_12_C2_B_A2;
                     u16SosArray[8] = S_12_C3_B_A2;
                     u16SosArray[9] = S_12_C4_B_A2;
                }
                break;

                case mapi_vif_datatype::E_VIF_SOUND_B_MONO_NICAM:
                {
                     u16NotchArray[0] = N_A1_C0_B_NICAM;
                     u16NotchArray[1] = N_A1_C1_B_NICAM;
                     u16NotchArray[2] = N_A1_C2_B_NICAM;

                     u16NotchArray[3] = N_A2_C0_B_NICAM;
                     u16NotchArray[4] = N_A2_C1_B_NICAM;
                     u16NotchArray[5] = N_A2_C2_B_NICAM;

                     u16SosArray[0] = S_11_C0_B_NICAM;
                     u16SosArray[1] = S_11_C1_B_NICAM;
                     u16SosArray[2] = S_11_C2_B_NICAM;
                     u16SosArray[3] = S_11_C3_B_NICAM;
                     u16SosArray[4] = S_11_C4_B_NICAM;

                     u16SosArray[5] = S_12_C0_B_NICAM;
                     u16SosArray[6] = S_12_C1_B_NICAM;
                     u16SosArray[7] = S_12_C2_B_NICAM;
                     u16SosArray[8] = S_12_C3_B_NICAM;
                     u16SosArray[9] = S_12_C4_B_NICAM;
                }
                break;

                case mapi_vif_datatype::E_VIF_SOUND_GH_STEREO_A2:
                {
                     u16NotchArray[0] = N_A1_C0_GHA2;
                     u16NotchArray[1] = N_A1_C1_GHA2;
                     u16NotchArray[2] = N_A1_C2_GHA2;

                     u16NotchArray[3] = N_A2_C0_GHA2;
                     u16NotchArray[4] = N_A2_C1_GHA2;
                     u16NotchArray[5] = N_A2_C2_GHA2;

                     u16SosArray[0] = S_11_C0_GHA2;
                     u16SosArray[1] = S_11_C1_GHA2;
                     u16SosArray[2] = S_11_C2_GHA2;
                     u16SosArray[3] = S_11_C3_GHA2;
                     u16SosArray[4] = S_11_C4_GHA2;

                     u16SosArray[5] = S_12_C0_GHA2;
                     u16SosArray[6] = S_12_C1_GHA2;
                     u16SosArray[7] = S_12_C2_GHA2;
                     u16SosArray[8] = S_12_C3_GHA2;
                     u16SosArray[9] = S_12_C4_GHA2;
                }
                break;

                case mapi_vif_datatype::E_VIF_SOUND_GH_MONO_NICAM:
                {
                     u16NotchArray[0] = N_A1_C0_GHMN;
                     u16NotchArray[1] = N_A1_C1_GHMN;
                     u16NotchArray[2] = N_A1_C2_GHMN;

                     u16NotchArray[3] = N_A2_C0_GHMN;
                     u16NotchArray[4] = N_A2_C1_GHMN;
                     u16NotchArray[5] = N_A2_C2_GHMN;

                     u16SosArray[0] = S_11_C0_GHMN;
                     u16SosArray[1] = S_11_C1_GHMN;
                     u16SosArray[2] = S_11_C2_GHMN;
                     u16SosArray[3] = S_11_C3_GHMN;
                     u16SosArray[4] = S_11_C4_GHMN;

                     u16SosArray[5] = S_12_C0_GHMN;
                     u16SosArray[6] = S_12_C1_GHMN;
                     u16SosArray[7] = S_12_C2_GHMN;
                     u16SosArray[8] = S_12_C3_GHMN;
                     u16SosArray[9] = S_12_C4_GHMN;
                }
                break;

                case mapi_vif_datatype::E_VIF_SOUND_I:
                {
                     u16NotchArray[0] = N_A1_C0_I;
                     u16NotchArray[1] = N_A1_C1_I;
                     u16NotchArray[2] = N_A1_C2_I;

                     u16NotchArray[3] = N_A2_C0_I;
                     u16NotchArray[4] = N_A2_C1_I;
                     u16NotchArray[5] = N_A2_C2_I;

                     u16SosArray[0] = S_11_C0_I;
                     u16SosArray[1] = S_11_C1_I;
                     u16SosArray[2] = S_11_C2_I;
                     u16SosArray[3] = S_11_C3_I;
                     u16SosArray[4] = S_11_C4_I;

                     u16SosArray[5] = S_12_C0_I;
                     u16SosArray[6] = S_12_C1_I;
                     u16SosArray[7] = S_12_C2_I;
                     u16SosArray[8] = S_12_C3_I;
                     u16SosArray[9] = S_12_C4_I;
                }
                break;

                case mapi_vif_datatype::E_VIF_SOUND_DK1_STEREO_A2:
                {
                     u16NotchArray[0] = N_A1_C0_DK1A2;
                     u16NotchArray[1] = N_A1_C1_DK1A2;
                     u16NotchArray[2] = N_A1_C2_DK1A2;

                     u16NotchArray[3] = N_A2_C0_DK1A2;
                     u16NotchArray[4] = N_A2_C1_DK1A2;
                     u16NotchArray[5] = N_A2_C2_DK1A2;

                     u16SosArray[0] = S_11_C0_DK1A2;
                     u16SosArray[1] = S_11_C1_DK1A2;
                     u16SosArray[2] = S_11_C2_DK1A2;
                     u16SosArray[3] = S_11_C3_DK1A2;
                     u16SosArray[4] = S_11_C4_DK1A2;

                     u16SosArray[5] = S_12_C0_DK1A2;
                     u16SosArray[6] = S_12_C1_DK1A2;
                     u16SosArray[7] = S_12_C2_DK1A2;
                     u16SosArray[8] = S_12_C3_DK1A2;
                     u16SosArray[9] = S_12_C4_DK1A2;
                }
                break;

                case mapi_vif_datatype::E_VIF_SOUND_DK2_STEREO_A2:
                {
                     u16NotchArray[0] = N_A1_C0_DK2A2;
                     u16NotchArray[1] = N_A1_C1_DK2A2;
                     u16NotchArray[2] = N_A1_C2_DK2A2;

                     u16NotchArray[3] = N_A2_C0_DK2A2;
                     u16NotchArray[4] = N_A2_C1_DK2A2;
                     u16NotchArray[5] = N_A2_C2_DK2A2;

                     u16SosArray[0] = S_11_C0_DK2A2;
                     u16SosArray[1] = S_11_C1_DK2A2;
                     u16SosArray[2] = S_11_C2_DK2A2;
                     u16SosArray[3] = S_11_C3_DK2A2;
                     u16SosArray[4] = S_11_C4_DK2A2;

                     u16SosArray[5] = S_12_C0_DK2A2;
                     u16SosArray[6] = S_12_C1_DK2A2;
                     u16SosArray[7] = S_12_C2_DK2A2;
                     u16SosArray[8] = S_12_C3_DK2A2;
                     u16SosArray[9] = S_12_C4_DK2A2;
                }
                break;

                case mapi_vif_datatype::E_VIF_SOUND_DK3_STEREO_A2:
                {
                     u16NotchArray[0] = N_A1_C0_DK3A2;
                     u16NotchArray[1] = N_A1_C1_DK3A2;
                     u16NotchArray[2] = N_A1_C2_DK3A2;

                     u16NotchArray[3] = N_A2_C0_DK3A2;
                     u16NotchArray[4] = N_A2_C1_DK3A2;
                     u16NotchArray[5] = N_A2_C2_DK3A2;

                     u16SosArray[0] = S_11_C0_DK3A2;
                     u16SosArray[1] = S_11_C1_DK3A2;
                     u16SosArray[2] = S_11_C2_DK3A2;
                     u16SosArray[3] = S_11_C3_DK3A2;
                     u16SosArray[4] = S_11_C4_DK3A2;

                     u16SosArray[5] = S_12_C0_DK3A2;
                     u16SosArray[6] = S_12_C1_DK3A2;
                     u16SosArray[7] = S_12_C2_DK3A2;
                     u16SosArray[8] = S_12_C3_DK3A2;
                     u16SosArray[9] = S_12_C4_DK3A2;
                }
                break;

                case mapi_vif_datatype::E_VIF_SOUND_DK_MONO_NICAM:
                {
                     u16NotchArray[0] = N_A1_C0_DKMN;
                     u16NotchArray[1] = N_A1_C1_DKMN;
                     u16NotchArray[2] = N_A1_C2_DKMN;

                     u16NotchArray[3] = N_A2_C0_DKMN;
                     u16NotchArray[4] = N_A2_C1_DKMN;
                     u16NotchArray[5] = N_A2_C2_DKMN;

                     u16SosArray[0] = S_11_C0_DKMN;
                     u16SosArray[1] = S_11_C1_DKMN;
                     u16SosArray[2] = S_11_C2_DKMN;
                     u16SosArray[3] = S_11_C3_DKMN;
                     u16SosArray[4] = S_11_C4_DKMN;

                     u16SosArray[5] = S_12_C0_DKMN;
                     u16SosArray[6] = S_12_C1_DKMN;
                     u16SosArray[7] = S_12_C2_DKMN;
                     u16SosArray[8] = S_12_C3_DKMN;
                     u16SosArray[9] = S_12_C4_DKMN;
                }
                break;

                case mapi_vif_datatype::E_VIF_SOUND_L:
                case mapi_vif_datatype::E_VIF_SOUND_LL:
                {
                     u16NotchArray[0] = N_A1_C0_L;
                     u16NotchArray[1] = N_A1_C1_L;
                     u16NotchArray[2] = N_A1_C2_L;

                     u16NotchArray[3] = N_A2_C0_L;
                     u16NotchArray[4] = N_A2_C1_L;
                     u16NotchArray[5] = N_A2_C2_L;

                     u16SosArray[0] = S_11_C0_L;
                     u16SosArray[1] = S_11_C1_L;
                     u16SosArray[2] = S_11_C2_L;
                     u16SosArray[3] = S_11_C3_L;
                     u16SosArray[4] = S_11_C4_L;

                     u16SosArray[5] = S_12_C0_L;
                     u16SosArray[6] = S_12_C1_L;
                     u16SosArray[7] = S_12_C2_L;
                     u16SosArray[8] = S_12_C3_L;
                     u16SosArray[9] = S_12_C4_L;
                }
                break;

                case mapi_vif_datatype::E_VIF_SOUND_MN:
                {
                     u16NotchArray[0] = N_A1_C0_NTSC;
                     u16NotchArray[1] = N_A1_C1_NTSC;
                     u16NotchArray[2] = N_A1_C2_NTSC;

                     u16NotchArray[3] = N_A2_C0_NTSC;
                     u16NotchArray[4] = N_A2_C1_NTSC;
                     u16NotchArray[5] = N_A2_C2_NTSC;

                     u16SosArray[0] = S_11_C0_NTSC;
                     u16SosArray[1] = S_11_C1_NTSC;
                     u16SosArray[2] = S_11_C2_NTSC;
                     u16SosArray[3] = S_11_C3_NTSC;
                     u16SosArray[4] = S_11_C4_NTSC;

                     u16SosArray[5] = S_12_C0_NTSC;
                     u16SosArray[6] = S_12_C1_NTSC;
                     u16SosArray[7] = S_12_C2_NTSC;
                     u16SosArray[8] = S_12_C3_NTSC;
                     u16SosArray[9] = S_12_C4_NTSC;
                }
                break;

                default:
                    break;
            }
        }
        break;

        case mapi_tuner_datatype::E_TUNER_SUBCMD_GET_DTV_IF_FREQ:
        {
            switch (u32Param1)
            {
                case E_TUNER_DTV_DVB_C_MODE:
                case E_TUNER_DTV_DVB_T_MODE:
                case E_TUNER_DTV_DVB_T2_MODE:
                    {
                        MAPI_U16 *u16DtvIFFreq = (MAPI_U16 *)pvoidParam3;
                        *u16DtvIFFreq = TUNER_DTV_IF_FREQ_KHz;
                    }
                    break;

                case E_TUNER_DTV_DTMB_MODE:
                    {
                        MAPI_U16 *u16DtvIFFreq = (MAPI_U16 *)pvoidParam3;
                        *u16DtvIFFreq = DTMB_TUNER_IF_FREQ_KHz;
                    }
                    break;

                case E_TUNER_DTV_ISDB_MODE:
					{
						MAPI_U16 *u16DtvIFFreq = (MAPI_U16 *)pvoidParam3;
						*u16DtvIFFreq = TUNER_DTV_IF_FREQ_KHz;
					}
					break;
                case E_TUNER_DTV_ATSC_MODE:
                    {
                        MAPI_U16 *u16DtvIFFreq = (MAPI_U16 *)pvoidParam3;
                        *u16DtvIFFreq = TUNER_DTV_IF_FREQ_KHz;
                    }
                    break;
                default:
                    printf("Error:%s(),%d\n",__func__,__LINE__);
                    return MAPI_FALSE;
            }
        }
            break;
        case mapi_tuner_datatype::E_TUNER_SUBCMD_GET_IQ_SWAP:
        {
            MAPI_U8 *u8IqSwap = (MAPI_U8 *)pvoidParam3;
            *u8IqSwap = TUNER_DTV_IQ_SWAP;
        }
            break;

        case mapi_tuner_datatype::E_TUNER_SUBCMD_GET_PEAKING_PARAMETER:
        {
            mapi_vif_datatype::stVIFUserFilter *VIF_UserFilter = (mapi_vif_datatype::stVIFUserFilter *)pvoidParam3;
            ASSERT(VIF_UserFilter);
            switch (u32Param1)
            {
                case E_RFBAND_VHF_LOW:
                    if((u32Param2==(MAPI_U32)mapi_demodulator_datatype::IF_FREQ_B)||(u32Param2==(MAPI_U32)mapi_demodulator_datatype::IF_FREQ_G))
                    {
                        VIF_UserFilter->VifSos21FilterC0  = VIF_SOS_21_FILTER_C0;
                        VIF_UserFilter->VifSos21FilterC1  = VIF_SOS_21_FILTER_C1;
                        VIF_UserFilter->VifSos21FilterC2  = VIF_SOS_21_FILTER_C2;
                        VIF_UserFilter->VifSos21FilterC3  = VIF_SOS_21_FILTER_C3;
                        VIF_UserFilter->VifSos21FilterC4  = VIF_SOS_21_FILTER_C4;

                        VIF_UserFilter->VifSos22FilterC0  = VIF_SOS_22_FILTER_C0;
                        VIF_UserFilter->VifSos22FilterC1  = VIF_SOS_22_FILTER_C1;
                        VIF_UserFilter->VifSos22FilterC2  = VIF_SOS_22_FILTER_C2;
                        VIF_UserFilter->VifSos22FilterC3  = VIF_SOS_22_FILTER_C3;
                        VIF_UserFilter->VifSos22FilterC4  = VIF_SOS_22_FILTER_C4;

                        VIF_UserFilter->VifSos31FilterC0  = VIF_SOS_31_FILTER_C0;
                        VIF_UserFilter->VifSos31FilterC1  = VIF_SOS_31_FILTER_C1;
                        VIF_UserFilter->VifSos31FilterC2  = VIF_SOS_31_FILTER_C2;
                        VIF_UserFilter->VifSos31FilterC3  = VIF_SOS_31_FILTER_C3;
                        VIF_UserFilter->VifSos31FilterC4  = VIF_SOS_31_FILTER_C4;
                        VIF_UserFilter->VifSos32FilterC0  = VIF_SOS_32_FILTER_C0;
                        VIF_UserFilter->VifSos32FilterC1  = VIF_SOS_32_FILTER_C1;
                        VIF_UserFilter->VifSos32FilterC2  = VIF_SOS_32_FILTER_C2;
                        VIF_UserFilter->VifSos32FilterC3  = VIF_SOS_32_FILTER_C3;
                        VIF_UserFilter->VifSos32FilterC4  = VIF_SOS_32_FILTER_C4;
                    }
                    else if(u32Param2==(MAPI_U32)mapi_demodulator_datatype::IF_FREQ_DK)
                    {
                        VIF_UserFilter->VifSos21FilterC0  = VIF_SOS_21_FILTER_C0;
                        VIF_UserFilter->VifSos21FilterC1  = VIF_SOS_21_FILTER_C1;
                        VIF_UserFilter->VifSos21FilterC2  = VIF_SOS_21_FILTER_C2;
                        VIF_UserFilter->VifSos21FilterC3  = VIF_SOS_21_FILTER_C3;
                        VIF_UserFilter->VifSos21FilterC4  = VIF_SOS_21_FILTER_C4;

                        VIF_UserFilter->VifSos22FilterC0  = VIF_SOS_22_FILTER_C0;
                        VIF_UserFilter->VifSos22FilterC1  = VIF_SOS_22_FILTER_C1;
                        VIF_UserFilter->VifSos22FilterC2  = VIF_SOS_22_FILTER_C2;
                        VIF_UserFilter->VifSos22FilterC3  = VIF_SOS_22_FILTER_C3;
                        VIF_UserFilter->VifSos22FilterC4  = VIF_SOS_22_FILTER_C4;

                        VIF_UserFilter->VifSos31FilterC0  = VIF_SOS_31_FILTER_C0;
                        VIF_UserFilter->VifSos31FilterC1  = VIF_SOS_31_FILTER_C1;
                        VIF_UserFilter->VifSos31FilterC2  = VIF_SOS_31_FILTER_C2;
                        VIF_UserFilter->VifSos31FilterC3  = VIF_SOS_31_FILTER_C3;
                        VIF_UserFilter->VifSos31FilterC4  = VIF_SOS_31_FILTER_C4;
                        VIF_UserFilter->VifSos32FilterC0  = VIF_SOS_32_FILTER_C0;
                        VIF_UserFilter->VifSos32FilterC1  = VIF_SOS_32_FILTER_C1;
                        VIF_UserFilter->VifSos32FilterC2  = VIF_SOS_32_FILTER_C2;
                        VIF_UserFilter->VifSos32FilterC3  = VIF_SOS_32_FILTER_C3;
                        VIF_UserFilter->VifSos32FilterC4  = VIF_SOS_32_FILTER_C4;
                    }
                    else if(u32Param2==(MAPI_U32)mapi_demodulator_datatype::IF_FREQ_I)
                    {
                        VIF_UserFilter->VifSos21FilterC0  = VIF_SOS_21_FILTER_C0;
                        VIF_UserFilter->VifSos21FilterC1  = VIF_SOS_21_FILTER_C1;
                        VIF_UserFilter->VifSos21FilterC2  = VIF_SOS_21_FILTER_C2;
                        VIF_UserFilter->VifSos21FilterC3  = VIF_SOS_21_FILTER_C3;
                        VIF_UserFilter->VifSos21FilterC4  = VIF_SOS_21_FILTER_C4;

                        VIF_UserFilter->VifSos22FilterC0  = VIF_SOS_22_FILTER_C0;
                        VIF_UserFilter->VifSos22FilterC1  = VIF_SOS_22_FILTER_C1;
                        VIF_UserFilter->VifSos22FilterC2  = VIF_SOS_22_FILTER_C2;
                        VIF_UserFilter->VifSos22FilterC3  = VIF_SOS_22_FILTER_C3;
                        VIF_UserFilter->VifSos22FilterC4  = VIF_SOS_22_FILTER_C4;

                        VIF_UserFilter->VifSos31FilterC0  = VIF_SOS_31_FILTER_C0;
                        VIF_UserFilter->VifSos31FilterC1  = VIF_SOS_31_FILTER_C1;
                        VIF_UserFilter->VifSos31FilterC2  = VIF_SOS_31_FILTER_C2;
                        VIF_UserFilter->VifSos31FilterC3  = VIF_SOS_31_FILTER_C3;
                        VIF_UserFilter->VifSos31FilterC4  = VIF_SOS_31_FILTER_C4;
                        VIF_UserFilter->VifSos32FilterC0  = VIF_SOS_32_FILTER_C0;
                        VIF_UserFilter->VifSos32FilterC1  = VIF_SOS_32_FILTER_C1;
                        VIF_UserFilter->VifSos32FilterC2  = VIF_SOS_32_FILTER_C2;
                        VIF_UserFilter->VifSos32FilterC3  = VIF_SOS_32_FILTER_C3;
                        VIF_UserFilter->VifSos32FilterC4  = VIF_SOS_32_FILTER_C4;
                    }
                    else if(u32Param2 == (MAPI_U32)mapi_demodulator_datatype::IF_FREQ_L)
                    {
                        VIF_UserFilter->VifSos21FilterC0  = VIF_SOS_21_FILTER_C0;
                        VIF_UserFilter->VifSos21FilterC1  = VIF_SOS_21_FILTER_C1;
                        VIF_UserFilter->VifSos21FilterC2  = VIF_SOS_21_FILTER_C2;
                        VIF_UserFilter->VifSos21FilterC3  = VIF_SOS_21_FILTER_C3;
                        VIF_UserFilter->VifSos21FilterC4  = VIF_SOS_21_FILTER_C4;

                        VIF_UserFilter->VifSos22FilterC0  = VIF_SOS_22_FILTER_C0;
                        VIF_UserFilter->VifSos22FilterC1  = VIF_SOS_22_FILTER_C1;
                        VIF_UserFilter->VifSos22FilterC2  = VIF_SOS_22_FILTER_C2;
                        VIF_UserFilter->VifSos22FilterC3  = VIF_SOS_22_FILTER_C3;
                        VIF_UserFilter->VifSos22FilterC4  = VIF_SOS_22_FILTER_C4;

                        VIF_UserFilter->VifSos31FilterC0  = VIF_SOS_31_FILTER_C0;
                        VIF_UserFilter->VifSos31FilterC1  = VIF_SOS_31_FILTER_C1;
                        VIF_UserFilter->VifSos31FilterC2  = VIF_SOS_31_FILTER_C2;
                        VIF_UserFilter->VifSos31FilterC3  = VIF_SOS_31_FILTER_C3;
                        VIF_UserFilter->VifSos31FilterC4  = VIF_SOS_31_FILTER_C4;
                        VIF_UserFilter->VifSos32FilterC0  = VIF_SOS_32_FILTER_C0;
                        VIF_UserFilter->VifSos32FilterC1  = VIF_SOS_32_FILTER_C1;
                        VIF_UserFilter->VifSos32FilterC2  = VIF_SOS_32_FILTER_C2;
                        VIF_UserFilter->VifSos32FilterC3  = VIF_SOS_32_FILTER_C3;
                        VIF_UserFilter->VifSos32FilterC4  = VIF_SOS_32_FILTER_C4;
                    }
                    else if(u32Param2 == (MAPI_U32)mapi_demodulator_datatype::IF_FREQ_L_PRIME)
                    {
                        VIF_UserFilter->VifSos21FilterC0  = VIF_SOS_21_FILTER_C0;
                        VIF_UserFilter->VifSos21FilterC1  = VIF_SOS_21_FILTER_C1;
                        VIF_UserFilter->VifSos21FilterC2  = VIF_SOS_21_FILTER_C2;
                        VIF_UserFilter->VifSos21FilterC3  = VIF_SOS_21_FILTER_C3;
                        VIF_UserFilter->VifSos21FilterC4  = VIF_SOS_21_FILTER_C4;

                        VIF_UserFilter->VifSos22FilterC0  = VIF_SOS_22_FILTER_C0;
                        VIF_UserFilter->VifSos22FilterC1  = VIF_SOS_22_FILTER_C1;
                        VIF_UserFilter->VifSos22FilterC2  = VIF_SOS_22_FILTER_C2;
                        VIF_UserFilter->VifSos22FilterC3  = VIF_SOS_22_FILTER_C3;
                        VIF_UserFilter->VifSos22FilterC4  = VIF_SOS_22_FILTER_C4;

                        VIF_UserFilter->VifSos31FilterC0  = VIF_SOS_31_FILTER_C0;
                        VIF_UserFilter->VifSos31FilterC1  = VIF_SOS_31_FILTER_C1;
                        VIF_UserFilter->VifSos31FilterC2  = VIF_SOS_31_FILTER_C2;
                        VIF_UserFilter->VifSos31FilterC3  = VIF_SOS_31_FILTER_C3;
                        VIF_UserFilter->VifSos31FilterC4  = VIF_SOS_31_FILTER_C4;
                        VIF_UserFilter->VifSos32FilterC0  = VIF_SOS_32_FILTER_C0;
                        VIF_UserFilter->VifSos32FilterC1  = VIF_SOS_32_FILTER_C1;
                        VIF_UserFilter->VifSos32FilterC2  = VIF_SOS_32_FILTER_C2;
                        VIF_UserFilter->VifSos32FilterC3  = VIF_SOS_32_FILTER_C3;
                        VIF_UserFilter->VifSos32FilterC4  = VIF_SOS_32_FILTER_C4;
                    }
                    else if(u32Param2 == (MAPI_U32)mapi_demodulator_datatype::IF_FREQ_MN)
                    {
                        VIF_UserFilter->VifSos21FilterC0  = VIF_SOS_21_FILTER_C0;
                        VIF_UserFilter->VifSos21FilterC1  = VIF_SOS_21_FILTER_C1;
                        VIF_UserFilter->VifSos21FilterC2  = VIF_SOS_21_FILTER_C2;
                        VIF_UserFilter->VifSos21FilterC3  = VIF_SOS_21_FILTER_C3;
                        VIF_UserFilter->VifSos21FilterC4  = VIF_SOS_21_FILTER_C4;

                        VIF_UserFilter->VifSos22FilterC0  = VIF_SOS_22_FILTER_C0;
                        VIF_UserFilter->VifSos22FilterC1  = VIF_SOS_22_FILTER_C1;
                        VIF_UserFilter->VifSos22FilterC2  = VIF_SOS_22_FILTER_C2;
                        VIF_UserFilter->VifSos22FilterC3  = VIF_SOS_22_FILTER_C3;
                        VIF_UserFilter->VifSos22FilterC4  = VIF_SOS_22_FILTER_C4;

                        VIF_UserFilter->VifSos31FilterC0  = VIF_SOS_31_FILTER_C0;
                        VIF_UserFilter->VifSos31FilterC1  = VIF_SOS_31_FILTER_C1;
                        VIF_UserFilter->VifSos31FilterC2  = VIF_SOS_31_FILTER_C2;
                        VIF_UserFilter->VifSos31FilterC3  = VIF_SOS_31_FILTER_C3;
                        VIF_UserFilter->VifSos31FilterC4  = VIF_SOS_31_FILTER_C4;
                        VIF_UserFilter->VifSos32FilterC0  = VIF_SOS_32_FILTER_C0;
                        VIF_UserFilter->VifSos32FilterC1  = VIF_SOS_32_FILTER_C1;
                        VIF_UserFilter->VifSos32FilterC2  = VIF_SOS_32_FILTER_C2;
                        VIF_UserFilter->VifSos32FilterC3  = VIF_SOS_32_FILTER_C3;
                        VIF_UserFilter->VifSos32FilterC4  = VIF_SOS_32_FILTER_C4;
                    }
                    else
                    {
                        printf("Error:%s(),%d\n",__func__,__LINE__);
                        return MAPI_FALSE;
                    }
                    break;
                case E_RFBAND_VHF_HIGH:
                    if((u32Param2 == (MAPI_U32)mapi_demodulator_datatype::IF_FREQ_B) || (u32Param2 == (MAPI_U32)mapi_demodulator_datatype::IF_FREQ_G))
                    {
                        VIF_UserFilter->VifSos21FilterC0  = VIF_SOS_21_FILTER_C0;
                        VIF_UserFilter->VifSos21FilterC1  = VIF_SOS_21_FILTER_C1;
                        VIF_UserFilter->VifSos21FilterC2  = VIF_SOS_21_FILTER_C2;
                        VIF_UserFilter->VifSos21FilterC3  = VIF_SOS_21_FILTER_C3;
                        VIF_UserFilter->VifSos21FilterC4  = VIF_SOS_21_FILTER_C4;

                        VIF_UserFilter->VifSos22FilterC0  = VIF_SOS_22_FILTER_C0;
                        VIF_UserFilter->VifSos22FilterC1  = VIF_SOS_22_FILTER_C1;
                        VIF_UserFilter->VifSos22FilterC2  = VIF_SOS_22_FILTER_C2;
                        VIF_UserFilter->VifSos22FilterC3  = VIF_SOS_22_FILTER_C3;
                        VIF_UserFilter->VifSos22FilterC4  = VIF_SOS_22_FILTER_C4;

                        VIF_UserFilter->VifSos31FilterC0  = VIF_SOS_31_FILTER_C0;
                        VIF_UserFilter->VifSos31FilterC1  = VIF_SOS_31_FILTER_C1;
                        VIF_UserFilter->VifSos31FilterC2  = VIF_SOS_31_FILTER_C2;
                        VIF_UserFilter->VifSos31FilterC3  = VIF_SOS_31_FILTER_C3;
                        VIF_UserFilter->VifSos31FilterC4  = VIF_SOS_31_FILTER_C4;
                        VIF_UserFilter->VifSos32FilterC0  = VIF_SOS_32_FILTER_C0;
                        VIF_UserFilter->VifSos32FilterC1  = VIF_SOS_32_FILTER_C1;
                        VIF_UserFilter->VifSos32FilterC2  = VIF_SOS_32_FILTER_C2;
                        VIF_UserFilter->VifSos32FilterC3  = VIF_SOS_32_FILTER_C3;
                        VIF_UserFilter->VifSos32FilterC4  = VIF_SOS_32_FILTER_C4;
                    }
                    else if(u32Param2 == (MAPI_U32)mapi_demodulator_datatype::IF_FREQ_DK)
                    {
                        VIF_UserFilter->VifSos21FilterC0  = VIF_SOS_21_FILTER_C0;
                        VIF_UserFilter->VifSos21FilterC1  = VIF_SOS_21_FILTER_C1;
                        VIF_UserFilter->VifSos21FilterC2  = VIF_SOS_21_FILTER_C2;
                        VIF_UserFilter->VifSos21FilterC3  = VIF_SOS_21_FILTER_C3;
                        VIF_UserFilter->VifSos21FilterC4  = VIF_SOS_21_FILTER_C4;

                        VIF_UserFilter->VifSos22FilterC0  = VIF_SOS_22_FILTER_C0;
                        VIF_UserFilter->VifSos22FilterC1  = VIF_SOS_22_FILTER_C1;
                        VIF_UserFilter->VifSos22FilterC2  = VIF_SOS_22_FILTER_C2;
                        VIF_UserFilter->VifSos22FilterC3  = VIF_SOS_22_FILTER_C3;
                        VIF_UserFilter->VifSos22FilterC4  = VIF_SOS_22_FILTER_C4;

                        VIF_UserFilter->VifSos31FilterC0  = VIF_SOS_31_FILTER_C0;
                        VIF_UserFilter->VifSos31FilterC1  = VIF_SOS_31_FILTER_C1;
                        VIF_UserFilter->VifSos31FilterC2  = VIF_SOS_31_FILTER_C2;
                        VIF_UserFilter->VifSos31FilterC3  = VIF_SOS_31_FILTER_C3;
                        VIF_UserFilter->VifSos31FilterC4  = VIF_SOS_31_FILTER_C4;
                        VIF_UserFilter->VifSos32FilterC0  = VIF_SOS_32_FILTER_C0;
                        VIF_UserFilter->VifSos32FilterC1  = VIF_SOS_32_FILTER_C1;
                        VIF_UserFilter->VifSos32FilterC2  = VIF_SOS_32_FILTER_C2;
                        VIF_UserFilter->VifSos32FilterC3  = VIF_SOS_32_FILTER_C3;
                        VIF_UserFilter->VifSos32FilterC4  = VIF_SOS_32_FILTER_C4;
                    }
                    else if(u32Param2 == (MAPI_U32)mapi_demodulator_datatype::IF_FREQ_I)
                    {
                        VIF_UserFilter->VifSos21FilterC0  = VIF_SOS_21_FILTER_C0;
                        VIF_UserFilter->VifSos21FilterC1  = VIF_SOS_21_FILTER_C1;
                        VIF_UserFilter->VifSos21FilterC2  = VIF_SOS_21_FILTER_C2;
                        VIF_UserFilter->VifSos21FilterC3  = VIF_SOS_21_FILTER_C3;
                        VIF_UserFilter->VifSos21FilterC4  = VIF_SOS_21_FILTER_C4;

                        VIF_UserFilter->VifSos22FilterC0  = VIF_SOS_22_FILTER_C0;
                        VIF_UserFilter->VifSos22FilterC1  = VIF_SOS_22_FILTER_C1;
                        VIF_UserFilter->VifSos22FilterC2  = VIF_SOS_22_FILTER_C2;
                        VIF_UserFilter->VifSos22FilterC3  = VIF_SOS_22_FILTER_C3;
                        VIF_UserFilter->VifSos22FilterC4  = VIF_SOS_22_FILTER_C4;

                        VIF_UserFilter->VifSos31FilterC0  = VIF_SOS_31_FILTER_C0;
                        VIF_UserFilter->VifSos31FilterC1  = VIF_SOS_31_FILTER_C1;
                        VIF_UserFilter->VifSos31FilterC2  = VIF_SOS_31_FILTER_C2;
                        VIF_UserFilter->VifSos31FilterC3  = VIF_SOS_31_FILTER_C3;
                        VIF_UserFilter->VifSos31FilterC4  = VIF_SOS_31_FILTER_C4;
                        VIF_UserFilter->VifSos32FilterC0  = VIF_SOS_32_FILTER_C0;
                        VIF_UserFilter->VifSos32FilterC1  = VIF_SOS_32_FILTER_C1;
                        VIF_UserFilter->VifSos32FilterC2  = VIF_SOS_32_FILTER_C2;
                        VIF_UserFilter->VifSos32FilterC3  = VIF_SOS_32_FILTER_C3;
                        VIF_UserFilter->VifSos32FilterC4  = VIF_SOS_32_FILTER_C4;
                    }
                    else if(u32Param2 == (MAPI_U32)mapi_demodulator_datatype::IF_FREQ_L)
                    {
                        VIF_UserFilter->VifSos21FilterC0  = VIF_SOS_21_FILTER_C0;
                        VIF_UserFilter->VifSos21FilterC1  = VIF_SOS_21_FILTER_C1;
                        VIF_UserFilter->VifSos21FilterC2  = VIF_SOS_21_FILTER_C2;
                        VIF_UserFilter->VifSos21FilterC3  = VIF_SOS_21_FILTER_C3;
                        VIF_UserFilter->VifSos21FilterC4  = VIF_SOS_21_FILTER_C4;

                        VIF_UserFilter->VifSos22FilterC0  = VIF_SOS_22_FILTER_C0;
                        VIF_UserFilter->VifSos22FilterC1  = VIF_SOS_22_FILTER_C1;
                        VIF_UserFilter->VifSos22FilterC2  = VIF_SOS_22_FILTER_C2;
                        VIF_UserFilter->VifSos22FilterC3  = VIF_SOS_22_FILTER_C3;
                        VIF_UserFilter->VifSos22FilterC4  = VIF_SOS_22_FILTER_C4;

                        VIF_UserFilter->VifSos31FilterC0  = VIF_SOS_31_FILTER_C0;
                        VIF_UserFilter->VifSos31FilterC1  = VIF_SOS_31_FILTER_C1;
                        VIF_UserFilter->VifSos31FilterC2  = VIF_SOS_31_FILTER_C2;
                        VIF_UserFilter->VifSos31FilterC3  = VIF_SOS_31_FILTER_C3;
                        VIF_UserFilter->VifSos31FilterC4  = VIF_SOS_31_FILTER_C4;
                        VIF_UserFilter->VifSos32FilterC0  = VIF_SOS_32_FILTER_C0;
                        VIF_UserFilter->VifSos32FilterC1  = VIF_SOS_32_FILTER_C1;
                        VIF_UserFilter->VifSos32FilterC2  = VIF_SOS_32_FILTER_C2;
                        VIF_UserFilter->VifSos32FilterC3  = VIF_SOS_32_FILTER_C3;
                        VIF_UserFilter->VifSos32FilterC4  = VIF_SOS_32_FILTER_C4;
                    }
                    else if(u32Param2 == (MAPI_U32)mapi_demodulator_datatype::IF_FREQ_L_PRIME)
                    {
                        VIF_UserFilter->VifSos21FilterC0  = VIF_SOS_21_FILTER_C0;
                        VIF_UserFilter->VifSos21FilterC1  = VIF_SOS_21_FILTER_C1;
                        VIF_UserFilter->VifSos21FilterC2  = VIF_SOS_21_FILTER_C2;
                        VIF_UserFilter->VifSos21FilterC3  = VIF_SOS_21_FILTER_C3;
                        VIF_UserFilter->VifSos21FilterC4  = VIF_SOS_21_FILTER_C4;

                        VIF_UserFilter->VifSos22FilterC0  = VIF_SOS_22_FILTER_C0;
                        VIF_UserFilter->VifSos22FilterC1  = VIF_SOS_22_FILTER_C1;
                        VIF_UserFilter->VifSos22FilterC2  = VIF_SOS_22_FILTER_C2;
                        VIF_UserFilter->VifSos22FilterC3  = VIF_SOS_22_FILTER_C3;
                        VIF_UserFilter->VifSos22FilterC4  = VIF_SOS_22_FILTER_C4;

                        VIF_UserFilter->VifSos31FilterC0  = VIF_SOS_31_FILTER_C0;
                        VIF_UserFilter->VifSos31FilterC1  = VIF_SOS_31_FILTER_C1;
                        VIF_UserFilter->VifSos31FilterC2  = VIF_SOS_31_FILTER_C2;
                        VIF_UserFilter->VifSos31FilterC3  = VIF_SOS_31_FILTER_C3;
                        VIF_UserFilter->VifSos31FilterC4  = VIF_SOS_31_FILTER_C4;
                        VIF_UserFilter->VifSos32FilterC0  = VIF_SOS_32_FILTER_C0;
                        VIF_UserFilter->VifSos32FilterC1  = VIF_SOS_32_FILTER_C1;
                        VIF_UserFilter->VifSos32FilterC2  = VIF_SOS_32_FILTER_C2;
                        VIF_UserFilter->VifSos32FilterC3  = VIF_SOS_32_FILTER_C3;
                        VIF_UserFilter->VifSos32FilterC4  = VIF_SOS_32_FILTER_C4;
                    }
                    else if(u32Param2 == (MAPI_U32)mapi_demodulator_datatype::IF_FREQ_MN)
                    {
                        VIF_UserFilter->VifSos21FilterC0  = VIF_SOS_21_FILTER_C0;
                        VIF_UserFilter->VifSos21FilterC1  = VIF_SOS_21_FILTER_C1;
                        VIF_UserFilter->VifSos21FilterC2  = VIF_SOS_21_FILTER_C2;
                        VIF_UserFilter->VifSos21FilterC3  = VIF_SOS_21_FILTER_C3;
                        VIF_UserFilter->VifSos21FilterC4  = VIF_SOS_21_FILTER_C4;

                        VIF_UserFilter->VifSos22FilterC0  = VIF_SOS_22_FILTER_C0;
                        VIF_UserFilter->VifSos22FilterC1  = VIF_SOS_22_FILTER_C1;
                        VIF_UserFilter->VifSos22FilterC2  = VIF_SOS_22_FILTER_C2;
                        VIF_UserFilter->VifSos22FilterC3  = VIF_SOS_22_FILTER_C3;
                        VIF_UserFilter->VifSos22FilterC4  = VIF_SOS_22_FILTER_C4;

                        VIF_UserFilter->VifSos31FilterC0  = VIF_SOS_31_FILTER_C0;
                        VIF_UserFilter->VifSos31FilterC1  = VIF_SOS_31_FILTER_C1;
                        VIF_UserFilter->VifSos31FilterC2  = VIF_SOS_31_FILTER_C2;
                        VIF_UserFilter->VifSos31FilterC3  = VIF_SOS_31_FILTER_C3;
                        VIF_UserFilter->VifSos31FilterC4  = VIF_SOS_31_FILTER_C4;
                        VIF_UserFilter->VifSos32FilterC0  = VIF_SOS_32_FILTER_C0;
                        VIF_UserFilter->VifSos32FilterC1  = VIF_SOS_32_FILTER_C1;
                        VIF_UserFilter->VifSos32FilterC2  = VIF_SOS_32_FILTER_C2;
                        VIF_UserFilter->VifSos32FilterC3  = VIF_SOS_32_FILTER_C3;
                        VIF_UserFilter->VifSos32FilterC4  = VIF_SOS_32_FILTER_C4;
                    }
                    else
                    {
                        printf("Error:%s(),%d\n",__func__,__LINE__);
                        return MAPI_FALSE;
                    }
                    break;
                case E_RFBAND_UHF:
                    if((u32Param2 == (MAPI_U32)mapi_demodulator_datatype::IF_FREQ_B) || (u32Param2 == (MAPI_U32)mapi_demodulator_datatype::IF_FREQ_G))
                    {
                        VIF_UserFilter->VifSos21FilterC0  = VIF_SOS_21_FILTER_C0;
                        VIF_UserFilter->VifSos21FilterC1  = VIF_SOS_21_FILTER_C1;
                        VIF_UserFilter->VifSos21FilterC2  = VIF_SOS_21_FILTER_C2;
                        VIF_UserFilter->VifSos21FilterC3  = VIF_SOS_21_FILTER_C3;
                        VIF_UserFilter->VifSos21FilterC4  = VIF_SOS_21_FILTER_C4;

                        VIF_UserFilter->VifSos22FilterC0  = VIF_SOS_22_FILTER_C0;
                        VIF_UserFilter->VifSos22FilterC1  = VIF_SOS_22_FILTER_C1;
                        VIF_UserFilter->VifSos22FilterC2  = VIF_SOS_22_FILTER_C2;
                        VIF_UserFilter->VifSos22FilterC3  = VIF_SOS_22_FILTER_C3;
                        VIF_UserFilter->VifSos22FilterC4  = VIF_SOS_22_FILTER_C4;

                        VIF_UserFilter->VifSos31FilterC0  = VIF_SOS_31_FILTER_C0;
                        VIF_UserFilter->VifSos31FilterC1  = VIF_SOS_31_FILTER_C1;
                        VIF_UserFilter->VifSos31FilterC2  = VIF_SOS_31_FILTER_C2;
                        VIF_UserFilter->VifSos31FilterC3  = VIF_SOS_31_FILTER_C3;
                        VIF_UserFilter->VifSos31FilterC4  = VIF_SOS_31_FILTER_C4;
                        VIF_UserFilter->VifSos32FilterC0  = VIF_SOS_32_FILTER_C0;
                        VIF_UserFilter->VifSos32FilterC1  = VIF_SOS_32_FILTER_C1;
                        VIF_UserFilter->VifSos32FilterC2  = VIF_SOS_32_FILTER_C2;
                        VIF_UserFilter->VifSos32FilterC3  = VIF_SOS_32_FILTER_C3;
                        VIF_UserFilter->VifSos32FilterC4  = VIF_SOS_32_FILTER_C4;
                    }
                    else if(u32Param2 == (MAPI_U32)mapi_demodulator_datatype::IF_FREQ_DK)
                    {
                        VIF_UserFilter->VifSos21FilterC0  = VIF_SOS_21_FILTER_C0;
                        VIF_UserFilter->VifSos21FilterC1  = VIF_SOS_21_FILTER_C1;
                        VIF_UserFilter->VifSos21FilterC2  = VIF_SOS_21_FILTER_C2;
                        VIF_UserFilter->VifSos21FilterC3  = VIF_SOS_21_FILTER_C3;
                        VIF_UserFilter->VifSos21FilterC4  = VIF_SOS_21_FILTER_C4;

                        VIF_UserFilter->VifSos22FilterC0  = VIF_SOS_22_FILTER_C0;
                        VIF_UserFilter->VifSos22FilterC1  = VIF_SOS_22_FILTER_C1;
                        VIF_UserFilter->VifSos22FilterC2  = VIF_SOS_22_FILTER_C2;
                        VIF_UserFilter->VifSos22FilterC3  = VIF_SOS_22_FILTER_C3;
                        VIF_UserFilter->VifSos22FilterC4  = VIF_SOS_22_FILTER_C4;

                        VIF_UserFilter->VifSos31FilterC0  = VIF_SOS_31_FILTER_C0;
                        VIF_UserFilter->VifSos31FilterC1  = VIF_SOS_31_FILTER_C1;
                        VIF_UserFilter->VifSos31FilterC2  = VIF_SOS_31_FILTER_C2;
                        VIF_UserFilter->VifSos31FilterC3  = VIF_SOS_31_FILTER_C3;
                        VIF_UserFilter->VifSos31FilterC4  = VIF_SOS_31_FILTER_C4;
                        VIF_UserFilter->VifSos32FilterC0  = VIF_SOS_32_FILTER_C0;
                        VIF_UserFilter->VifSos32FilterC1  = VIF_SOS_32_FILTER_C1;
                        VIF_UserFilter->VifSos32FilterC2  = VIF_SOS_32_FILTER_C2;
                        VIF_UserFilter->VifSos32FilterC3  = VIF_SOS_32_FILTER_C3;
                        VIF_UserFilter->VifSos32FilterC4  = VIF_SOS_32_FILTER_C4;
                    }
                    else if(u32Param2 == (MAPI_U32)mapi_demodulator_datatype::IF_FREQ_I)
                    {
                        VIF_UserFilter->VifSos21FilterC0  = VIF_SOS_21_FILTER_C0;
                        VIF_UserFilter->VifSos21FilterC1  = VIF_SOS_21_FILTER_C1;
                        VIF_UserFilter->VifSos21FilterC2  = VIF_SOS_21_FILTER_C2;
                        VIF_UserFilter->VifSos21FilterC3  = VIF_SOS_21_FILTER_C3;
                        VIF_UserFilter->VifSos21FilterC4  = VIF_SOS_21_FILTER_C4;

                        VIF_UserFilter->VifSos22FilterC0  = VIF_SOS_22_FILTER_C0;
                        VIF_UserFilter->VifSos22FilterC1  = VIF_SOS_22_FILTER_C1;
                        VIF_UserFilter->VifSos22FilterC2  = VIF_SOS_22_FILTER_C2;
                        VIF_UserFilter->VifSos22FilterC3  = VIF_SOS_22_FILTER_C3;
                        VIF_UserFilter->VifSos22FilterC4  = VIF_SOS_22_FILTER_C4;

                        VIF_UserFilter->VifSos31FilterC0  = VIF_SOS_31_FILTER_C0;
                        VIF_UserFilter->VifSos31FilterC1  = VIF_SOS_31_FILTER_C1;
                        VIF_UserFilter->VifSos31FilterC2  = VIF_SOS_31_FILTER_C2;
                        VIF_UserFilter->VifSos31FilterC3  = VIF_SOS_31_FILTER_C3;
                        VIF_UserFilter->VifSos31FilterC4  = VIF_SOS_31_FILTER_C4;
                        VIF_UserFilter->VifSos32FilterC0  = VIF_SOS_32_FILTER_C0;
                        VIF_UserFilter->VifSos32FilterC1  = VIF_SOS_32_FILTER_C1;
                        VIF_UserFilter->VifSos32FilterC2  = VIF_SOS_32_FILTER_C2;
                        VIF_UserFilter->VifSos32FilterC3  = VIF_SOS_32_FILTER_C3;
                        VIF_UserFilter->VifSos32FilterC4  = VIF_SOS_32_FILTER_C4;
                    }
                    else if(u32Param2 == (MAPI_U32)mapi_demodulator_datatype::IF_FREQ_L)
                    {
                        VIF_UserFilter->VifSos21FilterC0  = VIF_SOS_21_FILTER_C0;
                        VIF_UserFilter->VifSos21FilterC1  = VIF_SOS_21_FILTER_C1;
                        VIF_UserFilter->VifSos21FilterC2  = VIF_SOS_21_FILTER_C2;
                        VIF_UserFilter->VifSos21FilterC3  = VIF_SOS_21_FILTER_C3;
                        VIF_UserFilter->VifSos21FilterC4  = VIF_SOS_21_FILTER_C4;

                        VIF_UserFilter->VifSos22FilterC0  = VIF_SOS_22_FILTER_C0;
                        VIF_UserFilter->VifSos22FilterC1  = VIF_SOS_22_FILTER_C1;
                        VIF_UserFilter->VifSos22FilterC2  = VIF_SOS_22_FILTER_C2;
                        VIF_UserFilter->VifSos22FilterC3  = VIF_SOS_22_FILTER_C3;
                        VIF_UserFilter->VifSos22FilterC4  = VIF_SOS_22_FILTER_C4;

                        VIF_UserFilter->VifSos31FilterC0  = VIF_SOS_31_FILTER_C0;
                        VIF_UserFilter->VifSos31FilterC1  = VIF_SOS_31_FILTER_C1;
                        VIF_UserFilter->VifSos31FilterC2  = VIF_SOS_31_FILTER_C2;
                        VIF_UserFilter->VifSos31FilterC3  = VIF_SOS_31_FILTER_C3;
                        VIF_UserFilter->VifSos31FilterC4  = VIF_SOS_31_FILTER_C4;
                        VIF_UserFilter->VifSos32FilterC0  = VIF_SOS_32_FILTER_C0;
                        VIF_UserFilter->VifSos32FilterC1  = VIF_SOS_32_FILTER_C1;
                        VIF_UserFilter->VifSos32FilterC2  = VIF_SOS_32_FILTER_C2;
                        VIF_UserFilter->VifSos32FilterC3  = VIF_SOS_32_FILTER_C3;
                        VIF_UserFilter->VifSos32FilterC4  = VIF_SOS_32_FILTER_C4;
                    }
                    else if(u32Param2 == (MAPI_U32)mapi_demodulator_datatype::IF_FREQ_L_PRIME)
                    {
                        VIF_UserFilter->VifSos21FilterC0  = VIF_SOS_21_FILTER_C0;
                        VIF_UserFilter->VifSos21FilterC1  = VIF_SOS_21_FILTER_C1;
                        VIF_UserFilter->VifSos21FilterC2  = VIF_SOS_21_FILTER_C2;
                        VIF_UserFilter->VifSos21FilterC3  = VIF_SOS_21_FILTER_C3;
                        VIF_UserFilter->VifSos21FilterC4  = VIF_SOS_21_FILTER_C4;

                        VIF_UserFilter->VifSos22FilterC0  = VIF_SOS_22_FILTER_C0;
                        VIF_UserFilter->VifSos22FilterC1  = VIF_SOS_22_FILTER_C1;
                        VIF_UserFilter->VifSos22FilterC2  = VIF_SOS_22_FILTER_C2;
                        VIF_UserFilter->VifSos22FilterC3  = VIF_SOS_22_FILTER_C3;
                        VIF_UserFilter->VifSos22FilterC4  = VIF_SOS_22_FILTER_C4;

                        VIF_UserFilter->VifSos31FilterC0  = VIF_SOS_31_FILTER_C0;
                        VIF_UserFilter->VifSos31FilterC1  = VIF_SOS_31_FILTER_C1;
                        VIF_UserFilter->VifSos31FilterC2  = VIF_SOS_31_FILTER_C2;
                        VIF_UserFilter->VifSos31FilterC3  = VIF_SOS_31_FILTER_C3;
                        VIF_UserFilter->VifSos31FilterC4  = VIF_SOS_31_FILTER_C4;
                        VIF_UserFilter->VifSos32FilterC0  = VIF_SOS_32_FILTER_C0;
                        VIF_UserFilter->VifSos32FilterC1  = VIF_SOS_32_FILTER_C1;
                        VIF_UserFilter->VifSos32FilterC2  = VIF_SOS_32_FILTER_C2;
                        VIF_UserFilter->VifSos32FilterC3  = VIF_SOS_32_FILTER_C3;
                        VIF_UserFilter->VifSos32FilterC4  = VIF_SOS_32_FILTER_C4;
                    }
                    else if(u32Param2 == (MAPI_U32)mapi_demodulator_datatype::IF_FREQ_MN)
                    {
                        VIF_UserFilter->VifSos21FilterC0  = VIF_SOS_21_FILTER_C0;
                        VIF_UserFilter->VifSos21FilterC1  = VIF_SOS_21_FILTER_C1;
                        VIF_UserFilter->VifSos21FilterC2  = VIF_SOS_21_FILTER_C2;
                        VIF_UserFilter->VifSos21FilterC3  = VIF_SOS_21_FILTER_C3;
                        VIF_UserFilter->VifSos21FilterC4  = VIF_SOS_21_FILTER_C4;

                        VIF_UserFilter->VifSos22FilterC0  = VIF_SOS_22_FILTER_C0;
                        VIF_UserFilter->VifSos22FilterC1  = VIF_SOS_22_FILTER_C1;
                        VIF_UserFilter->VifSos22FilterC2  = VIF_SOS_22_FILTER_C2;
                        VIF_UserFilter->VifSos22FilterC3  = VIF_SOS_22_FILTER_C3;
                        VIF_UserFilter->VifSos22FilterC4  = VIF_SOS_22_FILTER_C4;

                        VIF_UserFilter->VifSos31FilterC0  = VIF_SOS_31_FILTER_C0;
                        VIF_UserFilter->VifSos31FilterC1  = VIF_SOS_31_FILTER_C1;
                        VIF_UserFilter->VifSos31FilterC2  = VIF_SOS_31_FILTER_C2;
                        VIF_UserFilter->VifSos31FilterC3  = VIF_SOS_31_FILTER_C3;
                        VIF_UserFilter->VifSos31FilterC4  = VIF_SOS_31_FILTER_C4;
                        VIF_UserFilter->VifSos32FilterC0  = VIF_SOS_32_FILTER_C0;
                        VIF_UserFilter->VifSos32FilterC1  = VIF_SOS_32_FILTER_C1;
                        VIF_UserFilter->VifSos32FilterC2  = VIF_SOS_32_FILTER_C2;
                        VIF_UserFilter->VifSos32FilterC3  = VIF_SOS_32_FILTER_C3;
                        VIF_UserFilter->VifSos32FilterC4  = VIF_SOS_32_FILTER_C4;
                    }
                    else
                    {
                        printf("Error:%s(),%d\n",__func__,__LINE__);
                        return MAPI_FALSE;
                    }
                    break;
                default:
                    printf("Error:%s(),%d\n",__func__,__LINE__);
                    return MAPI_FALSE;
                    break;
            }
        }
            break;
        case mapi_tuner_datatype::E_TUNER_SUBCMD_GET_RF_LEVEL:
        {
            MXL661_COMMAND_T mxl661_inst;

            mxl661_inst.commandId = MXL661_TUNER_RX_PWR_REQ;
            // fake i2c slave id.
            mxl661_inst.MxLIf.cmdTunerPwrReq.I2cSlaveAddr = 0xc0;

            if (MxLWare661_API_GetTunerStatus(&mxl661_inst) != MXL_TRUE)
            {
                return MAPI_FALSE;
            }

            *((float *)pvoidParam3) = (float)mxl661_inst.MxLIf.cmdTunerPwrReq.RxPwr/100.0;
        }
            break;
        case mapi_tuner_datatype::E_TUNER_SUBCMD_SELECT_AGC_PIN:
        {
           
            switch(u32Param1)
            {
                case E_TUNER_DTV_DTMB_MODE:
                case E_TUNER_DTV_DVB_C_MODE:
                case E_TUNER_DTV_DVB_T_MODE:
                case E_TUNER_ATV_PAL_MODE:
                case E_TUNER_ATV_SECAM_L_PRIME_MODE:
                case E_TUNER_ATV_NTSC_MODE:
                {  
                    if(u32Param2 == 0)
                    {
                        AGC_Pin_Internal = MXL661_AGC1;
                        printf("AGC_Pin_Internal = MXL661_AGC1 \n");
                    } 
                    else if(u32Param2 == 1)
                    {
                        AGC_Pin_Internal = MXL661_AGC2;
                        printf("AGC_Pin_Internal = MXL661_AGC2 \n");
                    }
                }
                    break;
           
                case E_TUNER_DTV_ISDB_MODE:
                case E_TUNER_DTV_DVB_T2_MODE:            
                {    
                    if(u32Param2 == 0)
                    {
                        AGC_Pin_External = MXL661_AGC1;
                        printf("AGC_Pin_External = MXL661_AGC1 \n");
                    }
                    else if(u32Param2 == 1)
                    {
                        AGC_Pin_External = MXL661_AGC2;
                        printf("AGC_Pin_External = MXL661_AGC2 \n");
                    }
                }
                    break;
                    
                default:
                    break;
            }
        }
            break;    
        default:
            UNUSED(u8SubCmd);
            UNUSED(u32Param1);
            UNUSED(u32Param2);
            UNUSED(pvoidParam3);
            return MAPI_FALSE;
            break;
    }
    return MAPI_TRUE;
}

MAPI_BOOL device_tuner_MXL661::ConfigAGCMode(EN_TUNER_MODE eMode)
{
    static EN_TUNER_MODE eLastMode = E_TUNER_INVALID;
    MXL_STATUS status = MXL_FAILED;
    if((eLastMode == eMode) && (m_bNeedResetAGCMode == MAPI_FALSE))//to avoid set repeatly
    {
        return MAPI_TRUE;
    }
    printf("\n===>ConfigAGCMode eMode %d\n",eMode);
    eLastMode = eMode;

    if(m_bInATVScanMode == MAPI_TRUE)//ATV scanning use external AGC to avoid channel missed
    {
        //IF Out setting:Done in ATV_SetTune()
        //external AGC1 Setting
        apiCmd.commandId = MXL661_TUNER_AGC_CFG;
        apiCmd.MxLIf.cmdAgcSetCfg.AgcSel = MXL661_AGC1;
        apiCmd.MxLIf.cmdAgcSetCfg.AgcType =MXL661_AGC_EXTERNAL;
        apiCmd.MxLIf.cmdAgcSetCfg.SetPoint = 66;
        apiCmd.MxLIf.cmdAgcSetCfg.AgcPolarityInverstion = MXL_DISABLE;
        status = MxLWare661_API_ConfigTuner(&apiCmd);
        if (MXL_FAILED == status)
        {
            printf("ATV Scanning Error!  MXL_TUNER_AGC_CFG\n");
            return MAPI_FALSE;
        }
    }
    else
    {
        switch(eMode)
        {
            case E_TUNER_DTV_DTMB_MODE:
                apiCmd.commandId = MXL661_DEV_IF_OUT_CFG;
                apiCmd.MxLIf.cmdIfOutCfg.IFOutFreq = IfFreqConversion(DTMB_TUNER_IF_FREQ_KHz);
                apiCmd.MxLIf.cmdIfOutCfg.ManualFreqSet= MXL_DISABLE;
                apiCmd.MxLIf.cmdIfOutCfg.IFInversion = MXL_DISABLE;
                apiCmd.MxLIf.cmdIfOutCfg.GainLevel = IF_OUT_CFG_GAIN_LEVEL_DTMB;

                apiCmd.MxLIf.cmdIfOutCfg.PathSel = MXL661_IF_PATH1;
                status = MxLWare661_API_ConfigDevice(&apiCmd);
                if (MXL_FAILED == status)
                {
                    printf("DTMB Error! MXL_DEV_IF_OUT_CFG\n");
                    return MAPI_FALSE;
                }

                //AGC Setting:AGC1 external
                apiCmd.commandId = MXL661_TUNER_AGC_CFG;
                apiCmd.MxLIf.cmdAgcSetCfg.AgcSel = AGC_Pin_Internal;//default: AGC1
                apiCmd.MxLIf.cmdAgcSetCfg.AgcType =MXL661_AGC_EXTERNAL;
                apiCmd.MxLIf.cmdAgcSetCfg.SetPoint = 66;
                apiCmd.MxLIf.cmdAgcSetCfg.AgcPolarityInverstion = MXL_DISABLE;
                status = MxLWare661_API_ConfigTuner(&apiCmd);
                if (MXL_FAILED == status)
                {
                    printf("\r\nDTMB Error! MXL_TUNER_AGC_CFG\n");
                    return MAPI_FALSE;
                }
                break;

			case E_TUNER_DTV_ISDB_MODE:
				apiCmd.commandId = MXL661_DEV_IF_OUT_CFG;
				apiCmd.MxLIf.cmdIfOutCfg.IFOutFreq = IfFreqConversion(DTV_TUNER_IF_FREQ_KHz);
				apiCmd.MxLIf.cmdIfOutCfg.ManualFreqSet= MXL_DISABLE;
				apiCmd.MxLIf.cmdIfOutCfg.IFInversion = MXL_ENABLE;
				apiCmd.MxLIf.cmdIfOutCfg.GainLevel = IF_OUT_CFG_GAIN_LEVEL_ISDB;

				apiCmd.MxLIf.cmdIfOutCfg.PathSel = MXL661_IF_PATH1;
				status = MxLWare661_API_ConfigDevice(&apiCmd);
				if (MXL_FAILED == status)
				{
					printf("ISDB Error! MXL_DEV_IF_OUT_CFG\n");
					return MAPI_FALSE;
				}

				//AGC Setting:AGC1 external
				apiCmd.commandId = MXL661_TUNER_AGC_CFG;
                apiCmd.MxLIf.cmdAgcSetCfg.AgcSel = AGC_Pin_External;//defaut: AGC2
				apiCmd.MxLIf.cmdAgcSetCfg.AgcType =MXL661_AGC_EXTERNAL;//MXL661_AGC_SELF;
				apiCmd.MxLIf.cmdAgcSetCfg.SetPoint = 66;
				apiCmd.MxLIf.cmdAgcSetCfg.AgcPolarityInverstion = MXL_DISABLE;
				status = MxLWare661_API_ConfigTuner(&apiCmd);
				if (MXL_FAILED == status)
				{
					printf("\r\nISDB Error! MXL_TUNER_AGC_CFG\n");
					return MAPI_FALSE;
				}
				break;

            case E_TUNER_DTV_DVB_C_MODE:
            case E_TUNER_DTV_DVB_T_MODE:
                apiCmd.commandId = MXL661_DEV_IF_OUT_CFG;
                apiCmd.MxLIf.cmdIfOutCfg.IFOutFreq = IfFreqConversion(DTV_TUNER_IF_FREQ_KHz);
                apiCmd.MxLIf.cmdIfOutCfg.ManualFreqSet= MXL_DISABLE;
                apiCmd.MxLIf.cmdIfOutCfg.IFInversion = MXL_DISABLE;
                apiCmd.MxLIf.cmdIfOutCfg.GainLevel = IF_OUT_CFG_GAIN_LEVEL_DVB;
                apiCmd.MxLIf.cmdIfOutCfg.PathSel = MXL661_IF_PATH1;
                status = MxLWare661_API_ConfigDevice(&apiCmd);
                if (MXL_FAILED == status)
                {
                    printf("DVBC Error! MXL_DEV_IF_OUT_CFG\n");
                    return MAPI_FALSE;
                }

                //AGC Setting:AGC1 external
                apiCmd.commandId = MXL661_TUNER_AGC_CFG;
                apiCmd.MxLIf.cmdAgcSetCfg.AgcSel = AGC_Pin_Internal;//default: AGC1
                apiCmd.MxLIf.cmdAgcSetCfg.AgcType =MXL661_AGC_EXTERNAL;
                apiCmd.MxLIf.cmdAgcSetCfg.SetPoint = 66;
                apiCmd.MxLIf.cmdAgcSetCfg.AgcPolarityInverstion = MXL_DISABLE;
                status = MxLWare661_API_ConfigTuner(&apiCmd);
                if (MXL_FAILED == status)
                {
                    printf("\r\nDVBC Error! MXL_TUNER_AGC_CFG\n");
                    return MAPI_FALSE;
                }
                break;
            case E_TUNER_DTV_DVB_T2_MODE:
                apiCmd.commandId = MXL661_DEV_IF_OUT_CFG;
                apiCmd.MxLIf.cmdIfOutCfg.IFOutFreq = IfFreqConversion(DTV_TUNER_IF_FREQ_KHz);
                apiCmd.MxLIf.cmdIfOutCfg.ManualFreqSet= MXL_DISABLE;
                apiCmd.MxLIf.cmdIfOutCfg.IFInversion = MXL_DISABLE;
                apiCmd.MxLIf.cmdIfOutCfg.GainLevel = IF_OUT_CFG_GAIN_LEVEL_DVB_T2;
                apiCmd.MxLIf.cmdIfOutCfg.PathSel = MXL661_IF_PATH1;
                status = MxLWare661_API_ConfigDevice(&apiCmd);
                if (MXL_FAILED == status)
                {
                    printf("DVBT2 Error! MXL_DEV_IF_OUT_CFG\n");
                    return MAPI_FALSE;
                }

                //AGC Setting:AGC1 external
                apiCmd.commandId = MXL661_TUNER_AGC_CFG;
                apiCmd.MxLIf.cmdAgcSetCfg.AgcSel = AGC_Pin_External;//defaut: AGC2
                apiCmd.MxLIf.cmdAgcSetCfg.AgcType =MXL661_AGC_EXTERNAL;
                apiCmd.MxLIf.cmdAgcSetCfg.SetPoint = 66;
                apiCmd.MxLIf.cmdAgcSetCfg.AgcPolarityInverstion = MXL_DISABLE;
                status = MxLWare661_API_ConfigTuner(&apiCmd);
                if (MXL_FAILED == status)
                {
                    printf("\r\nDVBT2 Error! MXL_TUNER_AGC_CFG\n");
                    return MAPI_FALSE;
                }
                break;

            case E_TUNER_ATV_PAL_MODE:
            case E_TUNER_ATV_SECAM_L_PRIME_MODE:
            case E_TUNER_ATV_NTSC_MODE:
                if(m_bNeedResetAGCMode == MAPI_TRUE)//complete gain level set  for manual scan finished ,normal mode and autoscan finished done in atv_settune()
                {
                    apiCmd.commandId = MXL661_DEV_IF_OUT_CFG;
                    apiCmd.MxLIf.cmdIfOutCfg.IFOutFreq = MXL661_IF_5MHz;
                    apiCmd.MxLIf.cmdIfOutCfg.ManualFreqSet= MXL_DISABLE;
                    apiCmd.MxLIf.cmdIfOutCfg.IFInversion = MXL_DISABLE;
                    apiCmd.MxLIf.cmdIfOutCfg.GainLevel = IF_OUT_CFG_GAIN_LEVEL_ATV_NORMAL;
                    apiCmd.MxLIf.cmdIfOutCfg.PathSel = MXL661_IF_PATH1;

                    status = MxLWare661_API_ConfigDevice(&apiCmd);
                    if (MXL_FAILED == status)
                    {
                        printf("ATV normal Error! MXL_DEV_IF_OUT_CFG\n");
                        return MAPI_FALSE;
                    }

                    //Application Mode setting
                    apiCmd.commandId = MXL661_TUNER_MODE_CFG;
                    apiCmd.MxLIf.cmdModeCfg.SignalMode = MXL661_ANA_PAL_D;
                    apiCmd.MxLIf.cmdChanTuneCfg.BandWidth = MXL661_ANA_TV_DIG_CABLE_BW_8MHz;
                    apiCmd.MxLIf.cmdModeCfg.IFOutFreqinKHz = 5000;
                    apiCmd.MxLIf.cmdModeCfg.XtalFreqSel = MXL661_XTAL_16MHz;
                    apiCmd.MxLIf.cmdModeCfg.IFOutGainLevel = IF_OUT_CFG_GAIN_LEVEL_ATV_NORMAL;

                    status = MxLWare661_API_ConfigTuner(&apiCmd);
                    if (MXL_FAILED == status)
                    {
                        printf("ATV normal Error! MXL_TUNER_MODE_CFG\n");
                        return MAPI_FALSE;
                    }
                }

                //AGC Setting:AGC1 external
                apiCmd.commandId = MXL661_TUNER_AGC_CFG;
                apiCmd.MxLIf.cmdAgcSetCfg.AgcSel = AGC_Pin_Internal;//default: AGC1
                apiCmd.MxLIf.cmdAgcSetCfg.AgcType =MXL661_AGC_SELF;//to avoid ATV vertical line scrambled
                apiCmd.MxLIf.cmdAgcSetCfg.SetPoint = 66;
                apiCmd.MxLIf.cmdAgcSetCfg.AgcPolarityInverstion = MXL_ENABLE;
                status = MxLWare661_API_ConfigTuner(&apiCmd);
                if (MXL_FAILED == status)
                {
                    printf("\r\nATV normal Error! MXL_TUNER_AGC_CFG\n");
                    return MAPI_FALSE;
                }
                break;


            default:
                apiCmd.commandId = MXL661_DEV_IF_OUT_CFG;
                apiCmd.MxLIf.cmdIfOutCfg.IFOutFreq = IfFreqConversion(DTV_TUNER_IF_FREQ_KHz);
                apiCmd.MxLIf.cmdIfOutCfg.ManualFreqSet= MXL_DISABLE;
                apiCmd.MxLIf.cmdIfOutCfg.IFInversion = MXL_DISABLE;
                apiCmd.MxLIf.cmdIfOutCfg.GainLevel = IF_OUT_CFG_GAIN_LEVEL_DVB;
                apiCmd.MxLIf.cmdIfOutCfg.PathSel = MXL661_IF_PATH1;
                status = MxLWare661_API_ConfigDevice(&apiCmd);
                if (MXL_FAILED == status)
                {
                    printf("DVBC Error! MXL_DEV_IF_OUT_CFG\n");
                    return MAPI_FALSE;
                }

                  //AGC Setting:AGC1 external
                apiCmd.commandId = MXL661_TUNER_AGC_CFG;
                apiCmd.MxLIf.cmdAgcSetCfg.AgcSel = AGC_Pin_Internal;//default: AGC1
                apiCmd.MxLIf.cmdAgcSetCfg.AgcType =MXL661_AGC_EXTERNAL;
                apiCmd.MxLIf.cmdAgcSetCfg.SetPoint = 66;
                apiCmd.MxLIf.cmdAgcSetCfg.AgcPolarityInverstion = MXL_DISABLE;
                status = MxLWare661_API_ConfigTuner(&apiCmd);
                if (MXL_FAILED == status)
                {
                    printf("\r\ndefault:Error! MXL_TUNER_AGC_CFG\n");
                    return MAPI_FALSE;
                }
                break;
        }
    }

    m_bNeedResetAGCMode = MAPI_FALSE;

    return MAPI_TRUE;
}


MAPI_BOOL device_tuner_MXL661::SetTunerInScanMode(MAPI_BOOL bScan)
{
    if(m_bInATVScanMode != bScan)
    {
        m_bInATVScanMode = bScan;
        m_bNeedResetAGCMode = MAPI_TRUE;
    }

    return MAPI_TRUE;
}

MXL661_IF_FREQ_E device_tuner_MXL661::IfFreqConversion(MAPI_U16 u16IfFreqKHz)
{
	MXL661_IF_FREQ_E enIfFreq = MXL661_IF_5MHz;
	switch(u16IfFreqKHz)
	{
		case 3650:
			enIfFreq = MXL661_IF_3_65MHz;
			break;

		case 4000:
			enIfFreq = MXL661_IF_4MHz;
			break;

		case 4100:
			enIfFreq = MXL661_IF_4_1MHz;
			break;

		case 4150:
			enIfFreq = MXL661_IF_4_15MHz;
			break;

		case 4500:
			enIfFreq = MXL661_IF_4_5MHz;
			break;

		case 4570:
			enIfFreq = MXL661_IF_4_57MHz;
			break;

		case 5000:
			enIfFreq = MXL661_IF_5MHz;
			break;

		case 5380:
			enIfFreq = MXL661_IF_5_38MHz;
			break;

		case 6000:
			enIfFreq = MXL661_IF_6MHz;
			break;

		case 6280:
			enIfFreq = MXL661_IF_6_28MHz;
			break;

		case 7200:
			enIfFreq = MXL661_IF_7_2MHz;
			break;

		case 8250:
			enIfFreq = MXL661_IF_8_25MHz;
			break;

		case 35250:
			enIfFreq = MXL661_IF_35_25MHz;
			break;

		case 36000:
			enIfFreq = MXL661_IF_36MHz;
			break;

		case 36150:
			enIfFreq = MXL661_IF_36_15MHz;
			break;

		case 36650:
			enIfFreq = MXL661_IF_36_65MHz;
			break;

		case 44000:
			enIfFreq = MXL661_IF_44MHz;
			break;

		default:
			enIfFreq = MXL661_IF_5MHz;
			break;
	}
	return enIfFreq;
}

static void MxL_API_GetLockStatus(UINT8* RfLock, UINT8* RefLock)
{
  MXL661_COMMAND_T apiCmd;

  // Read back Tuner lock status
  apiCmd.commandId = MXL661_TUNER_LOCK_STATUS_REQ;
  apiCmd.MxLIf.cmdTunerLockReq.I2cSlaveAddr = MXL661_I2C_ADDR;

  if (MXL_TRUE == MxLWare661_API_GetTunerStatus(&apiCmd))
  {
    *RfLock = apiCmd.MxLIf.cmdTunerLockReq.RfSynLock;
    *RefLock = apiCmd.MxLIf.cmdTunerLockReq.RefSynLock;
  }
  return;
}
/*
static void MxL_API_ATV_Finetune(MXL_BOOL ScaleUp )
{
  MXL_STATUS status;
  MXL661_COMMAND_T apiCmd;

  apiCmd.commandId = MXL661_TUNER_ENABLE_FINE_TUNE_CFG;
  apiCmd.MxLIf.cmdEnableFineTuneCfg.I2cSlaveAddr = MXL661_I2C_ADDR;
  apiCmd.MxLIf.cmdEnableFineTuneCfg.EnableFineTune  = MXL_ENABLE;
  status = MxLWare661_API_ConfigTuner(&apiCmd);
  if(status != MXL_TRUE)
    printf(" Enable Fine Tune status = %d\n",status);

  apiCmd.commandId = MXL661_TUNER_FINE_TUNE_CFG;
  apiCmd.MxLIf.cmdFineTuneCfg.I2cSlaveAddr = MXL661_I2C_ADDR;
  apiCmd.MxLIf.cmdFineTuneCfg.ScaleUp = ScaleUp;
  status = MxLWare661_API_ConfigTuner(&apiCmd);
  if(status != MXL_TRUE)
    printf("Fine Tune operation status = %d\n",status);

  return;
}*/

MAPI_BOOL device_tuner_MXL661::SetTunerInFinetuneMode(MAPI_BOOL bFinetune)
{
    m_bInATVFinetuneMode = bFinetune;
    return MAPI_TRUE;
}

MAPI_BOOL device_tuner_MXL661::CheckFineTuneRange(MAPI_U32 u32FreqKHz)
{
    if( (u32FreqKHz > (u32chantuneFreq - MAX_FINE_TUNE_STEP*FREQ_STEP)) && (u32FreqKHz < (u32chantuneFreq + MAX_FINE_TUNE_STEP*FREQ_STEP)) )
    {
        return MAPI_TRUE;
    }
    else
    {
        return MAPI_FALSE;
    }
}
