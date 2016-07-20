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

* File  : Board.h
**********************************************************************/
#include "drvXC_IOPort.h"
#include "MSD93J2P.h"
#include "mapi_types.h"
#include "mapi_syscfg_table.h"
#include "mapi_gpio_devTable.h"
#include "mapi_i2c_devTable.h"
#include "mapi_mspi_devTable.h"
#include "mapi_video.h"
#include "drvAUDIO_if.h"
#include "apiDMX.h"
#include "VGA_EDID.h"
#include "HDMI_EDID.h"
#include "mapi_vd.h"

//For Example:(EN_LTH_HDMITX_AUDIO_SPDIF|EN_LTH_HDMITX_AUDIO_I2S|EN_LTH_HDMITX_AUDIO_NONE)
#define ROCKET_AUDIO_INPUT_MODE EN_LTH_HDMITX_AUDIO_SPDIF

#define BOARD_COMMON_NULL       0x00

// Board Name
#define BOARD_NAME                      "MST082B_10AJQ_TVOS_ES_ASIA_NTSC_ATV"

// software version
#define SOFTWARE_VERSION                "0001"
//**********************************************************************
//** System-TvParam
//**********************************************************************
#define BOARD_TV_PRODUCT_TYPE       E_ATV_Plus_DTV
#define BOARD_ATV_SYSTEM_TYPE       E_NTSC_ENABLE

//For Example:(DVBT_ENABLE|DVBC_ENABLE|DVBS_ENABLE |DVBT2_ENABLE|DVBS2_ENABLE|DTMB_ENABLE|ATSC_ENABLE|ISDB_ENABLE)
#define BOARD_DTV_TYPE              (DVBT_ENABLE|DVBC_ENABLE|DVBT2_ENABLE)
#define BOARD_AUDIO_SYSTEM_TYPE     E_BTSC_ENABLE
#define BOARD_STB_SYSTEM_TYPE       E_STB_DISABLE

//For Example:(_FBL_ENABLE|_PWS_ENABLE| _AUDIO_SIF_ENABLE | _PRE_SCALE_DOWN_ENABLE | _CCIR_VEOUT_ENABLE | _SCART_OUT_ENABLE | _MM_FRC_ENABLE | _MARCOVISION_ENABLE )
#define BOARD_IP_ENABLE                     BOARD_COMMON_NULL
#define BOARD_ROUTE_PATH_1                  E_ROUTE_DVBT2
#define BOARD_ROUTE_PATH_2                  E_ROUTE_DVBC
#define BOARD_ROUTE_PATH_3                  BOARD_COMMON_NULL
#define BOARD_ROUTE_PATH_4                  BOARD_COMMON_NULL

//#define BOARD_SAW_TYPE EXTERNAL_SINGLE_SAW
#define BOARD_SAW_TYPE SAW6

#define BOARD_SAR_CHANNEL E_SAR_NC

static S_TV_TYPE_INFO BOARD_TV_PARAM=
{
    BOARD_TV_PRODUCT_TYPE,
    BOARD_ATV_SYSTEM_TYPE,
    BOARD_DTV_TYPE,
    BOARD_AUDIO_SYSTEM_TYPE,
    BOARD_STB_SYSTEM_TYPE,
    BOARD_IP_ENABLE,
    {
        BOARD_ROUTE_PATH_1,
        BOARD_ROUTE_PATH_2,
        BOARD_ROUTE_PATH_3,
        BOARD_ROUTE_PATH_4,
    }

};


//**********************************************************************
//** Video relative setting
//**********************************************************************

//EN_Mode_1135,
//EN_Mode_1135_1P5
#define BOARD_VD_CAP_WIN_MODE           EN_Mode_Dynamic//EN_Mode_1135
#define D_BOARD_PWM_CH 2

//**********************************************************************
//** System-I2C
//**********************************************************************

//Bus Define
//  DDCR_CK&DA
//      0xA4    EEPROM_24C512
//      0xA8    EEPROM_24C04
//  TGPIO 2&3
//      0xC2    Tuner (LG & NXP),
//      0xC0 for write&C1 for read  Tuner(SEC-DTVS203FH201B)

//Bus Info Define
#define BOARD_I2C_SWBUS_NUM             1
#define BOARD_I2C_HWBUS_NUM             0
#define BOARD_I2C_DEVICE_NUM            8

static SWI2CBus_t BOARD_I2C_SWBUS[BOARD_I2C_SWBUS_NUM]=
{
    //Example for SW I2C => { 1, PAD_DDCR_CK, PAD_DDCR_DA, 60, }, //SW_I2C  /SCL_PAD /SDA_PAD /Delay
    // Bus-0
    {PAD_DDCR_CK, PAD_DDCR_DA, 80},    //IS_SW_I2C  /SCL_PAD /SDA_PAD /Delay
};

static HWI2CBus_t BOARD_I2C_HWBUS[BOARD_I2C_HWBUS_NUM];

//Device Info Define
static I2CDeviceInfo_s  Board_I2C_Dev[BOARD_I2C_DEVICE_NUM] =
{
    //Example => {TUNER1, 1, 0xC2},                 //gID(U32)  /i2c_bus(U8) /slave_id(U8)
    {TUNER1, 0, 0xC0},                              //gID(U32)  /i2c_bus(U8) /slave_id(U8)
    {EEPROM, 0, 0xA4},                              //gID(U32)  /i2c_bus(U8) /slave_id(U8)
    {URSA6_TCON, 0, 0x38},                          //gID(U32)  /i2c_bus(U8) /slave_id(U8)
    {NOVA_TCON, 0, 0x34},                           //gID(U32)  /i2c_bus(U8) /slave_id(U8)
    {MEMC_MST7420DZ, 0, 0x40},                      //gID(U32)  /i2c_bus(U8) /slave_id(U8)
    {DEMOD_DYNAMIC_SLAVE_ID_1, 0, 0xF2 /*0xA4*/ },  //gID(U32)  /i2c_bus(U8) /slave_id(U8)
    {DEMOD_DYNAMIC_SLAVE_ID_2, 0, 0xA2 /*0xA4*/ },  //gID(U32)  /i2c_bus(U8) /slave_id(U8)
    {TUNER_AV2011, 0, 0xC0},
};

//**********************************************************************
//** System-MSPI
//**********************************************************************
//Bus Info Define
#define BOARD_MSPI_DEVICE_NUM               1

static MSPI_pad_info_s Board_MSPI_Dev[BOARD_MSPI_DEVICE_NUM] =
{
    {DEMOD, 0xFF, 3},
};

//**********************************************************************
//** System-GPIO
//**********************************************************************
 #if (KARAOKE_ENABLE == 1)
 #define BOARD_GPIO_NUM                     11
 #else
#define BOARD_GPIO_NUM                      9
#endif
static GPIOInfo_t  Board_GPIO_Setting[BOARD_GPIO_NUM] =
{
    {PANEL_CTL, 0, PAD_SAR2},               //Panel_ON-P1 1: On 0: Off
    {INV_CTL, 0,  PAD_GPIO_PM0},            //VBLCTRL-P2: 1: On 0: Off
    {MUTE, 0, PAD_GPIO_PM5},                //MUTE: 1: Mute 0:Normal
    {PANEL_3D_ENABLE, 0, PAD_GPIO_PM1},     //PANEL_3D_ENABLE CTRL
    {Demodulator_Reset, 0, PAD_GPIO_PM7 },  //Demod reset Pin
    {HTV_TUNER_SWITCH,   0, PAD_PWM2},     //TUNER_SWITCH
    {MUTE_POP, 0, PAD_I2S_OUT_WS},      // EarPhone Pop
    { LED_ON, 1, PAD_GPIO_PM8},          //LED G
    { LED_WORK, 1, PAD_PWM_PM},          //LED R
    #if (KARAOKE_ENABLE == 1)
    { M62429_DATA, 1, PAD_I2S_OUT_MCK},          //PAD_I2S_OUT_MCK  kok_gpio5 TX--Data
    { M62429_CLK, 1, PAD_I2S_OUT_SD},          //PAD_I2S_OUT_SD   kok_gpio6 RX --Clk
    #endif
};

//**********************************************************************
//** System-IR
//**********************************************************************

#define BOARD_IR_CRYSTAL_CLK_HZ         12000000

static PM_IrRegCfg_t  Board_IR_Setting =
{
    1,                                  // IR mode selection
    0xBF,                               // IR enable control
    0x01,                               // IR enable control 1
    0x09,                               // IR header code 0 // EosTek Patch
    0xF6,                               // IR header code 1 // EosTek Patch
    140000,                             // IR timerout counter
    2,                                  // Customer codes: 1 or 2 bytes
    32,                                 // Code bits: 0x00~0x7F

    //Time, upper bound, low bound
    {9000,20,(-20)},                    // header code time
    {4500,20,(-20)},                    //off code time
    {2500,20,(-20)},                    // off code repeat time
    {560,35,(-30)},                     // logical 0/1 high time
    {1120,20,(-20)},                    // logical 0 time
    {2240,20,(-20)},                    // logical 1 time

};
// EosTek Patch Begin
#define Board_PowerUpKeyCode            0x45
// EosTek Patch End
//**********************************************************************
//** InputMux
//**********************************************************************

#define BOARD_Demodulator               0   //1: internal, 0:external
#define BOARD_ATV_InputSrc_Type         E_MAPI_INPUT_SOURCE_INVALID

#define BOARD_Scart_1_FAST_BLACKING_Ping16  2   //0:SCART_FB_NONE, 1:SCART_FB0, 2:SCART_FB1, 3:SCART_FB2, 4:
#define BOARD_Scart_2_FAST_BLACKING_Ping16  0   //0:SCART_FB_NONE, 1:SCART_FB0, 2:SCART_FB1, 3:SCART_FB2, 4:
#define BOARD_Scart_OUT_MODE            3   // 0: SCART1:TV, SCART2:MONITOR          //SCART_OUT_TV_MONITOR
                                                                                    // 1: SCART1:MONITOR, SCART2:TV     //SCART_OUT_MONITOR_TV
                                                                                    // 2: SCART1:TV, SCART2:NONE
                                                                                    // 3: SCART1:MONITOR, SCART2:NONE  //SCART_OUT_MONITOR_NONE
#define BOARD_Scart_1_AUTO_SCART_Ping8  1   //0: INT_PIN8_HSYNC0, 1:INT_PIN8_HSYNC1, 2:INT_PIN8_HSYNC2, 3:INT_PIN8_NONE
#define BOARD_Scart_2_AUTO_SCART_Ping8  3   //0: INT_PIN8_HSYNC0, 1:INT_PIN8_HSYNC1, 2:INT_PIN8_HSYNC2, 3:INT_PIN8_NONE

ATVExtDemodInfo_t BOARD_ExtDemodInfo =
{
    BOARD_Demodulator,
    BOARD_ATV_InputSrc_Type
};

#define SCART_ID_LEVEL_0V               0   //< Scart Pin8 ADC input digital value setting
//#define SCART_ID_LEVEL_1V             0
//#define SCART_ID_LEVEL_2V             0
#define SCART_ID_LEVEL_3V               7   //<Scart voltage 3V = 7(ADC regiater value)
#define SCART_ID_LEVEL_4V               14  //<Scart voltage 4V = 10(ADC regiater value)
//#define SCART_ID_LEVEL_4p5V           35
#define SCART_ID_LEVEL_5V               12  //<Scart voltage 5V = 12(ADC regiater value)
#define SCART_ID_LEVEL_6V               15  //<Scart voltage 6V = 15(ADC regiater value)
//#define SCART_ID_LEVEL_7V             35
//#define SCART_ID_LEVEL_8V             45  //60
#define SCART_ID_LEVEL_9V               23  //<Scart voltage 9V = 23(ADC regiater value)
//#define SCART_ID_LEVEL_9p5V           60
#define SCART_ID_LEVEL_10V              25  //<Scart voltage 10V = 26(ADC regiater value)

// === Scart ID Level (FIXME: just copy from S3P) ===
// Level 0: 0V ~ 2V
// Level 1A: 4.5V ~ 7V (aspect ratio 16:9)
// Level 1B: 9.5V ~ 12V
/// KEYPAD_ADC_CHANNEL
/*enum KEYPAD_ADC_CHANNEL
{
    KEYPAD_ADC_CHANNEL_1 = 0,
    KEYPAD_ADC_CHANNEL_2,
    KEYPAD_ADC_CHANNEL_3,
    KEYPAD_ADC_CHANNEL_4,
    KEYPAD_ADC_CHANNEL_5,
    KEYPAD_ADC_CHANNEL_6,
    KEYPAD_ADC_CHANNEL_7,
    KEYPAD_ADC_CHANNEL_8,
};
*/

#define SCART_ID1_SAR_CHANNEL           5   //<Scart1 input channel setting
#define SCART_ID2_SAR_CHANNEL           5   //<Scart2 input channel setting

#define BOARD_SCART_LEVEL_4_3           SCART_ID_LEVEL_10V
#define BOARD_SCART_LEVEL_16_9          SCART_ID_LEVEL_4V
#define BOARD_SCART_LEVEL_NONE          SCART_ID_LEVEL_0V
ScartInfo_t BOARD_ScarInfo =
{
    BOARD_Scart_1_FAST_BLACKING_Ping16,
    BOARD_Scart_2_FAST_BLACKING_Ping16,
    BOARD_Scart_OUT_MODE,
    BOARD_Scart_1_AUTO_SCART_Ping8,
    BOARD_Scart_2_AUTO_SCART_Ping8,
    BOARD_SCART_LEVEL_4_3,
    BOARD_SCART_LEVEL_16_9,
    BOARD_SCART_LEVEL_NONE,
};

// DVBT
/*
MS_U8 u8BOARD_DVBT_DSPRegInitExt[] =
{
    1,                                  // version, should be matched with library
    0,                                  // reserved
    0,                                  // Size_L
    0,                                  // Size_H
    E_DMD_DVBT_CFG_IQ_SWAP,             // Addr_L
    E_DMD_DVBT_CFG_IQ_SWAP >> 8,        // Addr_H
    0xFF,                               // Mask
    0x00                                // Value
};
*/
MS_U8 u8BOARD_DVBT_DSPRegInitExt[]={};
extern MS_U8 u8BOARD_DMD_DVBT_InitExtConf[SAW_NUMS][u32_BOARD_DMD_Init_Config_Length];
MS_U8* u8BOARD_DMD_DVBT_InitExt=u8BOARD_DMD_DVBT_InitExtConf[BOARD_SAW_TYPE];
MS_U8 u8BOARD_DMD_DVBT_InitExtConf[SAW_NUMS][u32_BOARD_DMD_Init_Config_Length]=
{
    {   // 0 : DUAL_SAW
        3,                              // version
        0,                              // reserved
        0xFF,                           // TS_CLK
        1,                              // RFAGC tristate control default value, 1:trisate 0:non-tristate,never modify unless you know the meaning
        (MS_U8)(36167 >> 24),           // IF Frequency
        (MS_U8)(36167 >> 16),
        (MS_U8)(36167 >> 8),
        (MS_U8)(36167 >> 0),
        (MS_U8)(45474 >> 24),           // FS Frequency
        (MS_U8)(45474 >> 16),
        (MS_U8)(45474 >> 8),
        (MS_U8)(45474 >> 0),
        0,                              // IQ Swap
        0,                              // u8ADCIQMode : 0=I path, 1=Q path, 2=both IQ
        0,                              // u8PadSel : 0=Normal, 1=analog pad
        0,                              // bPGAEnable : 0=disable, 1=enable
        5,                              // u8PGAGain : default 5
    },
    {   // 1 EXTERNAL_SINGLE_SAW
        3,                              // version
        0,                              // reserved
        0xFF,                           // TS_CLK
        1,                              // RFAGC tristate control default value, 1:trisate 0:non-tristate,never modify unless you know the meaning
        (MS_U8)(36167 >> 24),           // IF Frequency
        (MS_U8)(36167 >> 16),
        (MS_U8)(36167 >> 8),
        (MS_U8)(36167 >> 0),
        (MS_U8)(45474 >> 24),           // FS Frequency
        (MS_U8)(45474 >> 16),
        (MS_U8)(45474 >> 8),
        (MS_U8)(45474 >> 0),
        0,                              // IQ Swap
        0,                              // u8ADCIQMode : 0=I path, 1=Q path, 2=both IQ
        0,                              // u8PadSel : 0=Normal, 1=analog pad
        0,                              // bPGAEnable : 0=disable, 1=enable
        5                               // u8PGAGain : default 5
    },
    {   // 2 INTERNAL_SINGLE_SAW_DIF or SILICON_TUNER
        3,                              // version
        0,                              // reserved
        0xFF,                           // TS_CLK
        1,                              // RFAGC tristate control default value, 1:trisate 0:non-tristate,never modify unless you know the meaning
        (MS_U8)(36167 >> 24),           // IF Frequency
        (MS_U8)(36167 >> 16),
        (MS_U8)(36167 >> 8),
        (MS_U8)(36167 >> 0),
        (MS_U8)(45474 >> 24),           // FS Frequency
        (MS_U8)(45474 >> 16),
        (MS_U8)(45474 >> 8),
        (MS_U8)(45474 >> 0),
        0,                              // IQ Swap
        0,                              // u8ADCIQMode : 0=I path, 1=Q path, 2=both IQ
        0,                              // u8PadSel : 0=Normal, 1=analog pad
        0,                              // bPGAEnable : 0=disable, 1=enable
        5                               // u8PGAGain : default 5
    },
    {   // 3 NO_SAW
        3,                              // version
        0,                              // reserved
        0xFF,                           // TS_CLK
        1,                              // RFAGC tristate control default value, 1:trisate 0:non-tristate,never modify unless you know the meaning
        (MS_U8)(36167 >> 24),           // IF Frequency
        (MS_U8)(36167 >> 16),
        (MS_U8)(36167 >> 8),
        (MS_U8)(36167 >> 0),
        (MS_U8)(45474 >> 24),           // FS Frequency
        (MS_U8)(45474 >> 16),
        (MS_U8)(45474 >> 8),
        (MS_U8)(45474 >> 0),
        0,                              // IQ Swap
        0,                              // u8ADCIQMode : 0=I path, 1=Q path, 2=both IQ
        0,                              // u8PadSel : 0=Normal, 1=analog pad
        0,                              // bPGAEnable : 0=disable, 1=enable
        5                               // u8PGAGain : default 5
    },
    {   // 4 INTERNAL_SINGLE_SAW_VIF
        3,                              // version
        0,                              // reserved
        0xFF,                           // TS_CLK
        1,                              // RFAGC tristate control default value, 1:trisate 0:non-tristate,never modify unless you know the meaning
        (MS_U8)(36167 >> 24),           // IF Frequency
        (MS_U8)(36167 >> 16),
        (MS_U8)(36167 >> 8),
        (MS_U8)(36167 >> 0),
        (MS_U8)(45474 >> 24),           // FS Frequency
        (MS_U8)(45474 >> 16),
        (MS_U8)(45474 >> 8),
        (MS_U8)(45474 >> 0),
        0,                              // IQ Swap
        1,                              // u8ADCIQMode : 0=I path, 1=Q path, 2=both IQ
        1,                              // u8PadSel : 0=Normal, 1=analog pad
        0,                              // bPGAEnable : 0=disable, 1=enable
        5                               // u8PGAGain : default 5
    },
    {   // 5 NO_SAW (DIF)
        3,                              // version
        0,                              // reserved
        0xFF,                           // TS_CLK
        1,                              // RFAGC tristate control default value, 1:trisate 0:non-tristate,never modify unless you know the meaning
        (MS_U8)(36167 >> 24),           // IF Frequency
        (MS_U8)(36167 >> 16),
        (MS_U8)(36167 >> 8),
        (MS_U8)(36167 >> 0),
        (MS_U8)(45474 >> 24),           // FS Frequency
        (MS_U8)(45474 >> 16),
        (MS_U8)(45474 >> 8),
        (MS_U8)(45474 >> 0),
        0,                              // IQ Swap
        0,                              // u8ADCIQMode : 0=I path, 1=Q path, 2=both IQ
        0,                              // u8PadSel : 0=Normal, 1=analog pad
        0,                              // bPGAEnable : 0=disable, 1=enable
        5                               // u8PGAGain : default 5
    },
    {   // 6:SAW6
        3,                              // version
        0,                              // reserved
        0xFF,                           // TS_CLK
        1,                              // RFAGC tristate control default value, 1:trisate 0:non-tristate,never modify unless you know the meaning
        (MS_U8)(36167 >> 24),           // IF Frequency
        (MS_U8)(36167 >> 16),
        (MS_U8)(36167 >> 8),
        (MS_U8)(36167 >> 0),
        (MS_U8)(45474 >> 24),           // FS Frequency
        (MS_U8)(45474 >> 16),
        (MS_U8)(45474 >> 8),
        (MS_U8)(45474 >> 0),
        0,                              // IQ Swap
        1,                              // u8ADCIQMode : 0=I path, 1=Q path, 2=both IQ
        1,                              // u8PadSel : 0=Normal, 1=analog pad
        0,                              // bPGAEnable : 0=disable, 1=enable
        5                               // u8PGAGain : default 5
    }
};
// DVBC
/*
    E_DMD_DVBC_NO_SIGNAL_GAIN_TH_L,
    E_DMD_DVBC_NO_SIGNAL_GAIN_TH_H,
*/
/*
MS_U8 u8BOARD_DVBC_DSPRegInitExt[] =
{
    1,                                  // version, should be matched with library
    0,                                  // reserved
    0,                                  // Size_L
    0,                                  // Size_H
    E_DMD_DVBC_OP_NORMALIF_EN,          // Addr_L
    E_DMD_DVBC_OP_NORMALIF_EN >> 8,     // Addr_H
    0xFF,                               // Mask
    0x01                                // Value
};
*/
MS_U8 u8BOARD_DVBC_DSPRegInitExt[]={};
MS_U8 u8BOARD_DMD_DVBC_InitExtConf[SAW_NUMS][u32_BOARD_DMD_Init_Config_Length]=
{
    {   // 0:DUAL_SAW
        3,                              // version
        0,                              // reserved
        0xFF,                           // TS_CLK
        1,                              // RFAGC tristate control default value, 1:trisate 0:non-tristate,never modify unless you know the meaning
        (MS_U8)(36167 >> 24),           // IF Frequency
        (MS_U8)(36167 >> 16),
        (MS_U8)(36167 >> 8),
        (MS_U8)(36167 >> 0),
        (MS_U8)(45474 >> 24),           // FS Frequency
        (MS_U8)(45474 >> 16),
        (MS_U8)(45474 >> 8),
        (MS_U8)(45474 >> 0),
        0,                              // IQ Swap
        0,                              // u8ADCIQMode : 0=I path, 1=Q path, 2=both IQ
        0,                              // u8PadSel : 0=Normal, 1=analog pad
        0,                              // bPGAEnable : 0=disable, 1=enable
        5                               // u8PGAGain : default 5
    },
    {   // 1 EXTERNAL_SINGLE_SAW
        3,                              // version
        0,                              // reserved
        0xFF,                           // TS_CLK
        1,                              // RFAGC tristate control default value, 1:trisate 0:non-tristate,never modify unless you know the meaning
        (MS_U8)(36167 >> 24),           // IF Frequency
        (MS_U8)(36167 >> 16),
        (MS_U8)(36167 >> 8),
        (MS_U8)(36167 >> 0),
        (MS_U8)(45474 >> 24),           // FS Frequency
        (MS_U8)(45474 >> 16),
        (MS_U8)(45474 >> 8),
        (MS_U8)(45474 >> 0),
        0,                              // IQ Swap
        0,                              // u8ADCIQMode : 0=I path, 1=Q path, 2=both IQ
        0,                              // u8PadSel : 0=Normal, 1=analog pad
        0,                              // bPGAEnable : 0=disable, 1=enable
        5                               // u8PGAGain : default 5
    },
    {   // 2 INTERNAL_SINGLE_SAW_DIF or SILICON_TUNER
        3,                              // version
        0,                              // reserved
        0xFF,                           // TS_CLK
        1,                              // RFAGC tristate control default value, 1:trisate 0:non-tristate,never modify unless you know the meaning
        (MS_U8)(36167 >> 24),           // IF Frequency
        (MS_U8)(36167 >> 16),
        (MS_U8)(36167 >> 8),
        (MS_U8)(36167 >> 0),
        (MS_U8)(45474 >> 24),           // FS Frequency
        (MS_U8)(45474 >> 16),
        (MS_U8)(45474 >> 8),
        (MS_U8)(45474 >> 0),
        0,                              // IQ Swap
        0,                              // u8ADCIQMode : 0=I path, 1=Q path, 2=both IQ
        0,                              // u8PadSel : 0=Normal, 1=analog pad
        0,                              // bPGAEnable : 0=disable, 1=enable
        5                               // u8PGAGain : default 5
    },
    {   // 3 NO_SAW
        3,                              // version
        0,                              // reserved
        0xFF,                           // TS_CLK
        1,                              // RFAGC tristate control default value, 1:trisate 0:non-tristate,never modify unless you know the meaning
        (MS_U8)(36167 >> 24),           // IF Frequency
        (MS_U8)(36167 >> 16),
        (MS_U8)(36167 >> 8),
        (MS_U8)(36167 >> 0),
        (MS_U8)(45474 >> 24),           // FS Frequency
        (MS_U8)(45474 >> 16),
        (MS_U8)(45474 >> 8),
        (MS_U8)(45474 >> 0),
        0,                              // IQ Swap
        0,                              // u8ADCIQMode : 0=I path, 1=Q path, 2=both IQ
        0,                              // u8PadSel : 0=Normal, 1=analog pad
        0,                              // bPGAEnable : 0=disable, 1=enable
        5                               // u8PGAGain : default 5
    },
    {   // 4 INTERNAL_SINGLE_SAW_VIF
        3,                              // version
        0,                              // reserved
        0xFF,                           // TS_CLK
        1,                              // RFAGC tristate control default value, 1:trisate 0:non-tristate,never modify unless you know the meaning
        (MS_U8)(36167 >> 24),           // IF Frequency
        (MS_U8)(36167 >> 16),
        (MS_U8)(36167 >> 8),
        (MS_U8)(36167 >> 0),
        (MS_U8)(45474 >> 24),           // FS Frequency
        (MS_U8)(45474 >> 16),
        (MS_U8)(45474 >> 8),
        (MS_U8)(45474 >> 0),
        0,                              // IQ Swap
        1,                              // u8ADCIQMode : 0=I path, 1=Q path, 2=both IQ
        1,                              // u8PadSel : 0=Normal, 1=analog pad
        0,                              // bPGAEnable : 0=disable, 1=enable
        5                               // u8PGAGain : default 5
    },
    {   // 5 NO_SAW (DIF)
        3,                              // version
        0,                              // reserved
        0xFF,                           // TS_CLK
        1,                              // RFAGC tristate control default value, 1:trisate 0:non-tristate,never modify unless you know the meaning
        (MS_U8)(36167 >> 24),           // IF Frequency
        (MS_U8)(36167 >> 16),
        (MS_U8)(36167 >> 8),
        (MS_U8)(36167 >> 0),
        (MS_U8)(45474 >> 24),           // FS Frequency
        (MS_U8)(45474 >> 16),
        (MS_U8)(45474 >> 8),
        (MS_U8)(45474 >> 0),
        0,                              // IQ Swap
        0,                              // u8ADCIQMode : 0=I path, 1=Q path, 2=both IQ
        0,                              // u8PadSel : 0=Normal, 1=analog pad
        0,                              // bPGAEnable : 0=disable, 1=enable
        5                               // u8PGAGain : default 5
    },
    {   // 6:SAW6
        3,                              // version
        0,                              // reserved
        0xFF,                           // TS_CLK
        1,                              // RFAGC tristate control default value, 1:trisate 0:non-tristate,never modify unless you know the meaning
        (MS_U8)(36167 >> 24),           // IF Frequency
        (MS_U8)(36167 >> 16),
        (MS_U8)(36167 >> 8),
        (MS_U8)(36167 >> 0),
        (MS_U8)(45474 >> 24),           // FS Frequency
        (MS_U8)(45474 >> 16),
        (MS_U8)(45474 >> 8),
        (MS_U8)(45474 >> 0),
        0,                              // IQ Swap
        1,                              // u8ADCIQMode : 0=I path, 1=Q path, 2=both IQ
        1,                              // u8PadSel : 0=Normal, 1=analog pad
        0,                              // bPGAEnable : 0=disable, 1=enable
        5                               // u8PGAGain : default 5

    }
};

MS_U8* u8BOARD_DMD_DVBC_InitExt=u8BOARD_DMD_DVBC_InitExtConf[BOARD_SAW_TYPE];

#define BOARD_INPUTMUX_NUM              MAPI_INPUT_SOURCE_NUM

static MAPI_VIDEO_INPUTSRCTABLE Board_Input_Mux_Table[BOARD_INPUTMUX_NUM] =
{
    //1~10
    //MAPI_INPUT_SOURCE_VGA,
    {2,         {INPUT_PORT_ANALOG0,    INPUT_PORT_ANALOG0_SYNC}},  //<VGA input
    //MAPI_INPUT_SOURCE_ATV,
    {1,         {INPUT_PORT_YMUX_CVBS0, INPUT_PORT_NONE_PORT}},     //<ATV input
    //MAPI_INPUT_SOURCE_CVBS,
    {1,         {INPUT_PORT_YMUX_CVBS0, INPUT_PORT_NONE_PORT}},     //<AV 1 input
    //MAPI_INPUT_SOURCE_CVBS2,
    {0,         {INPUT_PORT_NONE_PORT,  INPUT_PORT_NONE_PORT}},     //<AV 2 input
    //MAPI_INPUT_SOURCE_CVBS3,
    {0,         {INPUT_PORT_NONE_PORT,  INPUT_PORT_NONE_PORT}},     //<AV 3 input
    //MAPI_INPUT_SOURCE_CVBS4,
    {0,         {INPUT_PORT_NONE_PORT,  INPUT_PORT_NONE_PORT}},     //<AV 4 input
    //MAPI_INPUT_SOURCE_CVBS5,
    {0,         {INPUT_PORT_NONE_PORT,  INPUT_PORT_NONE_PORT}},     //<AV 5 input
    //MAPI_INPUT_SOURCE_CVBS6,
    {0,         {INPUT_PORT_NONE_PORT,  INPUT_PORT_NONE_PORT}},     //<AV 6 input
    //MAPI_INPUT_SOURCE_CVBS7,
    {0,         {INPUT_PORT_NONE_PORT,  INPUT_PORT_NONE_PORT}},     //<AV 7 input
    //MAPI_INPUT_SOURCE_CVBS8,
    {0,         {INPUT_PORT_NONE_PORT,  INPUT_PORT_NONE_PORT}},     //<AV 8 input

    //11~20
    //MAPI_INPUT_SOURCE_CVBS_MAX,
    {0,         {INPUT_PORT_NONE_PORT,  INPUT_PORT_NONE_PORT}},     //<AV max
    //MAPI_INPUT_SOURCE_SVIDEO,
    {0,         {INPUT_PORT_NONE_PORT,  INPUT_PORT_NONE_PORT}},     //<S-video 1 input
    //MAPI_INPUT_SOURCE_SVIDEO2,
    {0,         {INPUT_PORT_NONE_PORT,  INPUT_PORT_NONE_PORT}},     //<S-video 2 input
    //MAPI_INPUT_SOURCE_SVIDEO3,
    {0,         {INPUT_PORT_NONE_PORT,  INPUT_PORT_NONE_PORT}},     //<S-video 3 input
    //MAPI_INPUT_SOURCE_SVIDEO4,
    {0,         {INPUT_PORT_NONE_PORT,  INPUT_PORT_NONE_PORT}},     //<S-video 4 input
    //MAPI_INPUT_SOURCE_SVIDEO_MAX,
    {0,         {INPUT_PORT_NONE_PORT,  INPUT_PORT_NONE_PORT}},     //<S-video max
    //MAPI_INPUT_SOURCE_YPBPR,
    {1,         {INPUT_PORT_ANALOG1,    INPUT_PORT_NONE_PORT}},     //<Component 1 input
    //MAPI_INPUT_SOURCE_YPBPR2,
    {0,         {INPUT_PORT_NONE_PORT,  INPUT_PORT_NONE_PORT}},     //<Component 2 input
    //MAPI_INPUT_SOURCE_YPBPR3,
    {0,         {INPUT_PORT_NONE_PORT,  INPUT_PORT_NONE_PORT}},     //<Component 3 input
    //MAPI_INPUT_SOURCE_YPBPR_MAX,
    {0,         {INPUT_PORT_NONE_PORT,  INPUT_PORT_NONE_PORT}},     //<Component max

    //21~30
    //MAPI_INPUT_SOURCE_SCART,
    {0,         {INPUT_PORT_NONE_PORT,  INPUT_PORT_NONE_PORT}},    //<Scart 1 input
    //MAPI_INPUT_SOURCE_SCART2,
    {0,         {INPUT_PORT_NONE_PORT,  INPUT_PORT_NONE_PORT}},     //<Scart 2 input
    //MAPI_INPUT_SOURCE_SCART_MAX,
    {0,         {INPUT_PORT_NONE_PORT,  INPUT_PORT_NONE_PORT}},     //<Scart max
    //MAPI_INPUT_SOURCE_HDMI,
    {1,         {INPUT_PORT_DVI1,       INPUT_PORT_NONE_PORT}},     //<HDMI 1 input
    //MAPI_INPUT_SOURCE_HDMI2,
    {1,         {INPUT_PORT_DVI0,       INPUT_PORT_NONE_PORT}},     //<HDMI 2 input
    //MAPI_INPUT_SOURCE_HDMI3,
    {1,         {INPUT_PORT_DVI2,       INPUT_PORT_NONE_PORT}},     //<HDMI 3 input
    //MAPI_INPUT_SOURCE_HDMI4,
    {0,         {INPUT_PORT_NONE_PORT,  INPUT_PORT_NONE_PORT}},     //<HDMI 4 input
    //MAPI_INPUT_SOURCE_HDMI_MAX,
    {0,         {INPUT_PORT_NONE_PORT,  INPUT_PORT_NONE_PORT}},     //<HDMI max input
    //MAPI_INPUT_SOURCE_DTV,
    {1,         {INPUT_PORT_MVOP,       INPUT_PORT_NONE_PORT}},     //<DTV input
    //MAPI_INPUT_SOURCE_DVI,
    {0,         {INPUT_PORT_NONE_PORT,  INPUT_PORT_NONE_PORT}},     //<DVI 1 input


    //31~
    //MAPI_INPUT_SOURCE_DVI2,
    {0,         {INPUT_PORT_NONE_PORT,  INPUT_PORT_NONE_PORT}},     //<DVI 2 input
    //MAPI_INPUT_SOURCE_DVI3,
    {0,         {INPUT_PORT_NONE_PORT,  INPUT_PORT_NONE_PORT}},     //<DVI 3 input
    //MAPI_INPUT_SOURCE_DVI4,
    {0,         {INPUT_PORT_NONE_PORT,  INPUT_PORT_NONE_PORT}},     //<DVI 4 input
    //MAPI_INPUT_SOURCE_DVI_MAX,
    {0,         {INPUT_PORT_NONE_PORT,  INPUT_PORT_NONE_PORT}},     //<DVI max

    // Application source
    //MAPI_INPUT_SOURCE_STORAGE,
    {1,         {INPUT_PORT_MVOP,       INPUT_PORT_NONE_PORT}},     //<Storage
    //MAPI_INPUT_SOURCE_KTV,
    {1,         {INPUT_PORT_MVOP,       INPUT_PORT_NONE_PORT}},     //<KTV
    //MAPI_INPUT_SOURCE_JPEG,
    {1,         {INPUT_PORT_MVOP,       INPUT_PORT_NONE_PORT}},     //<JPEG
    //MAPI_INPUT_SOURCE_DTV2,
    {1,         {INPUT_PORT_MVOP2,      INPUT_PORT_NONE_PORT}},     //<DTV 2 input
    //MAPI_INPUT_SOURCE_STORAGE2,
    {1,         {INPUT_PORT_MVOP2,      INPUT_PORT_NONE_PORT}},     //<Storage 2
    //MAPI_INPUT_SOURCE_DTV3,
    {1,         {INPUT_PORT_MVOP3,      INPUT_PORT_NONE_PORT}},     //<DTV 3 input
    //MAPI_INPUT_SOURCE_SCALER_OP,
    {1,         {INPUT_PORT_SCALER_OP,  INPUT_PORT_NONE_PORT}},     //<Scaler OP
};

//**********************************************************************
//** WatchDog
//**********************************************************************

#define BOARD_WATCHDOG_ENABLE           MAPI_TRUE
#define BOARD_WATCHDOB_TIMER_REG        0x00003008
#define BOARD_WATCHDOB_TIMER            10

static WDTInfo_t BOARD_WDTInfo =
{
    BOARD_WATCHDOG_ENABLE,
    BOARD_WATCHDOB_TIMER_REG,
    BOARD_WATCHDOB_TIMER
};

//**********************************************************************
//** AudioInputSrcMux
//**********************************************************************

#define BOARD_AUDIO_INPUT_SOURCE_TYPE_SIZE  20
#define BOARD_AUDIO_PATH_TYPE_SIZE      8
#define BOARD_AUDIO_OUTPUT_TYPE_SIZE    6

static AudioMux_t BOARD_AudioMux_t[BOARD_AUDIO_INPUT_SOURCE_TYPE_SIZE] =
{
    {AUDIO_DSP1_DVB_INPUT},             // AUDIO_SOURCE_DTV
    {AUDIO_DSP1_DVB_INPUT},             // AUDIO_SOURCE_DTV2
    {AUDIO_DSP4_SIF_INPUT},             // AUDIO_SOURCE_ATV
    {AUDIO_AUIN2_INPUT},                // AUDIO_SOURCE_PC
    {AUDIO_AUIN0_INPUT},                // AUDIO_SOURCE_YPbPr
    {AUDIO_NULL_INPUT},                 // AUDIO_SOURCE_YPbPr2
    {AUDIO_AUIN0_INPUT},                // AUDIO_SOURCE_AV
    {AUDIO_NULL_INPUT},                 // AUDIO_SOURCE_AV2
    {AUDIO_NULL_INPUT},                 // AUDIO_SOURCE_AV3
    {AUDIO_NULL_INPUT},                 // AUDIO_SOURCE_SV
    {AUDIO_NULL_INPUT},                 // AUDIO_SOURCE_SV2
    {AUDIO_NULL_INPUT},                 // AUDIO_SOURCE_SCART
    {AUDIO_NULL_INPUT},                 // AUDIO_SOURCE_SCART2
    {AUDIO_HDMI_INPUT},                 // AUDIO_SOURCE_HDMI
    {AUDIO_HDMI_INPUT},                 // AUDIO_SOURCE_HDMI2
    {AUDIO_HDMI_INPUT},                 // AUDIO_SOURCE_HDMI3
    {AUDIO_AUIN2_INPUT},                // AUDIO_SOURCE_DVI
    {AUDIO_AUIN2_INPUT},                // AUDIO_SOURCE_DVI2
    {AUDIO_AUIN2_INPUT},                // AUDIO_SOURCE_DVI3
    {AUDIO_NULL_INPUT},                 // AUDIO_SOURCE_KTV
};

static AudioMux_t BOARD_AudioPath_t[BOARD_AUDIO_PATH_TYPE_SIZE] =
{
    {AUDIO_T3_PATH_AUOUT1},             // AUDIO_PATH_MAIN_SPEAKER
    {AUDIO_PATH_NULL},                  // AUDIO_PATH_HP
    {AUDIO_T3_PATH_AUOUT2},             // AUDIO_PATH_LINEOUT
    {AUDIO_PATH_NULL},                  // AUDIO_PATH_SIFOUT
    {AUDIO_PATH_NULL},                  // AUDIO_PATH_SCART1 = SIF out
    {AUDIO_T3_PATH_AUOUT2},             // AUDIO_PATH_SCART2 = Lineout
    {AUDIO_T3_PATH_SPDIF},              // AUDIO_PATH_SPDIF
    {AUDIO_PATH_NULL},                  // AUDIO_PATH_HDMI
};

static AudioOutputType_t BOARD_AudioOutputType_t[BOARD_AUDIO_OUTPUT_TYPE_SIZE] =
{
    {AUDIO_AUOUT1_OUTPUT},               // AUDIO_OUPUT_MAIN_SPEAKER
    {AUDIO_NULL_OUTPUT},                // AUDIO_OUPUT_HP
    {AUDIO_AUOUT2_OUTPUT},              // AUDIO_OUPUT_LINEOUT
    {AUDIO_NULL_OUTPUT},                // AUDIO_OUPUT_SIFOUT
    {AUDIO_NULL_OUTPUT},                // AUDIO_OUPUT_SCART1
    {AUDIO_NULL_OUTPUT},                // AUDIO_OUPUT_SCART2
};

static AudioDefualtInit_t   BOARD_AudioDefaultInit_t =
{
    MAPI_AUDIO_SOURCE_DTV,              // Audio input source type
    MAPI_AUDIO_PATH_SIFOUT,             // Audio path type
    MAPI_AUDIO_OUTPUT_SIFOUT            // Audio output type
};

//**********************************************************************
//** Audio Amplifier
//**********************************************************************

//**********************************************************************
//** DemuxInfo
//**********************************************************************

//With CI Card
static DemuxInfo_t DemuxInfo_With_IC_Card =
{
    //MAPI_DMX_INPUT (u8DMX_Flow_input)
    DMX_FLOW_INPUT_EXT_INPUT0,
    //Clock phase inversion (bClkInv)
    MAPI_FALSE,
    //Sync by extension signal (bExtSync)
    MAPI_TRUE,
    //Parallel (bParallel)
    MAPI_TRUE
};

//Without CI Card
static DemuxInfo_t DemuxInfo_WITHOUT_IC_CARD =
{
    //MAPI_DMX_INPUT (u8DMX_Flow_input)
    DMX_FLOW_INPUT_DEMOD,
    //Clock phase inversion (bClkInv)
    MAPI_FALSE,
    //Sync by extension signal (bExtSync)
    MAPI_TRUE,
    //Parallel (bParallel)
    MAPI_FALSE
};

DMXConf_t BOARD_DMXConf_Table =
{
    DemuxInfo_With_IC_Card,
    DemuxInfo_WITHOUT_IC_CARD
};

//**********************************************************************
//** EDID Info
//**********************************************************************
#define BOARD_HDMI_EDID_InfoCount       4

//**********************************************************************
//** HDMI HOT PLUG INVERSE INFO
//** It supports 32 sets of HDMI now
//** Every bit in HOT_PLUG_INVERSE stands for hot plug inverse bit of corresponed HW HDMI port
//** eg: HOT_PLUG_INVERSE = 0xFFFE => INPUT_PORT_DVI0 is the only one that is not inversed.
//**********************************************************************
#define HOT_PLUG_INVERSE                0xFFFB

//**********************************************************************
//** CEC Port Select Info
//** On Mulit-Port CEC select which port support CEC
//** eg: CEC1_PORT_SELECT = 0x00 => mean CEC_PORT A
//**********************************************************************
#define CEC1_PORT_SELECT                0x02

//** HDMI 5V DETECT GPIO POOL SELECT
//** Select GPIO as HDMI 5V detect pin
//** Every Byte in HOT_5V_DETECT_GPIO_SELECT stands for selected 16 GPIO bits of corresponed HW HDMI port
//** eg: HOT_5V_DETECT_GPIO_SELECT = 0x0503070D
//** => PAD_GPIO_PM17 is selected for INPUT_PORT_DVI0
//** => PAD_GPIO_PM9   is selected for INPUT_PORT_DVI1
//** => PAD_GPIO_PM2   is selected for INPUT_PORT_DVI2
//** => PAD_GPIO_PM7   is selected for INPUT_PORT_DVI3
//**********************************************************************
//#define HOT_5V_DETECT_GPIO_SELECT          0x0503070D
