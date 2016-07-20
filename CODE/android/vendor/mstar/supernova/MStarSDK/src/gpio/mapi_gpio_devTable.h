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

#ifndef __GPIO_DEVTABLE_H__
#define __GPIO_DEVTABLE_H__

#include "mapi_base.h"
#include "mapi_syscfg_table.h"

MAPI_U32 Get_GPIO_ID(MAPI_U32 u32GpioValue);

#define MUTE 0
#define FLASH_WP0 1
#define PANEL_CTL 2
#define EXT_RF_AGC 3
#define SECAM_L_PRIME 4
#define INV_CTL 5
#define POWER_ON_OFF 6
#define EEPROM_WP 7
#define LED_RED 8
#define LED_GRN 9
#define ANT_5V_CTL 10
#define BOOSTER 11
#define RGB_SW 12
#define SC_RE1 13
#define SC_RE2 14
#define TU_RESET_N 15
#define ScartRecord1 16
#define ScartRecord2 17
#define Tuner_PCMCIA 18
#define LAN 19
#define Demodulator 20
#define Tuner 21
#define Peripheral_Device_Reset 22
#define SCART_OUT 23
#define Audio_Amplifier 24
#define Adj_Volume 25
#define Demodulator_Reset 26
#define Panel_VCC 27
#define Panel_Backlight_VCC 28
#define EEPROM512_WP 29
#define EarPhone 30
#define USB_POWER_CTRL 31
#define USB_POWER_CTRL2 32
#define USBPower 33
#define USB0_Power 34
#define USB1_Power 35
#define LED_ON    36
#define LED_OFF   37
#define LED_WORK  38
#define SD_RSTn   39
#define SD_CDn    40
#define POWER_KEY 41
#define HDP       42
#define DTV_DETECT 43
#define PCM_POWER_CTRL 44
#define AUMUTE_OUT 45
#define SCART_OUT_1_MUTE 46
#define SCART_OUT_2_MUTE 47
#define DVBT2_TS_OEB    48
#define DVBS_TS_OEB     49
#define DVBS_RESETZ     50
#define DVBT2_RESETZ    51
#define EXT_IF_AGC    52
#define SPI_WP    53
#define SW_330    54
#define NAND_MODE 55
#define SPDIF_OUT 56
#define MUTE_POP  57
#define Audio_PreAmp 58
#define PANEL_3D_ENABLE 59
#define POWER_RST 60
#define PANEL_3D_DIM_SW 61
#define PANEL_3D_CTRL 62
#define WIFI_ONOFF_CTRL 63
#define BLUETOOTH_3D_SYNC_RESET 64
#define FRONTPNL_STB 65
#define FRONTPNL_CLOCK 66
#define FRONTPNL_DATA 67
#define BLUEDONGLE_REQ_CONFIG 68
#define SII_RESET 69
#define ROCKET_HDMI_PLUG 70
#define HDMITX_INT 71

// EosTek Patch Begin
#define HTV_TUNER_SWITCH 72
// EosTek Patch End
#define M62429_DATA       73
#define M62429_CLK          74

//MHL define
#define MHL_ELAND_POWER  0x70
#define MHL_ELAND_RESET  0x71
#define MHL_ELAND_CWAKE  0x72

//dual CI
#define PCM_POWER_CTRL1 0x73
#define BYPASS1_EN 0x74
#define BYPASS2_EN 0x75
#define TSP_SELECT0 0x76
#define TSP_SELECT1 0x77
#define TSP_SELECT2 0x78

#define GENERIC_GPIO_ID0 0x80
#define GENERIC_GPIO_ID1 0x81
#define GENERIC_GPIO_ID2 0x82
#define GENERIC_GPIO_ID3 0x83
#define GENERIC_GPIO_ID4 0x84
#define GENERIC_GPIO_ID5 0x85
#define GENERIC_GPIO_ID6 0x86
#define GENERIC_GPIO_ID7 0x87
#define GENERIC_GPIO_ID8 0x88
#define GENERIC_GPIO_ID9 0x89
#define GENERIC_GPIO_IDA 0x8A
#define GENERIC_GPIO_IDB 0x8B
#define GENERIC_GPIO_IDC 0x8C
#define GENERIC_GPIO_IDD 0x8D
#define GENERIC_GPIO_IDE 0x8E
#define GENERIC_GPIO_IDF 0x8F

#define GPIO_BOUNDARY_MIN 0x90
#define GPIO_BOUNDARY_MAX 0x6F
#define GENERIC_GPIO_ID(u32GpioValue) Get_GPIO_ID(u32GpioValue)

#endif
