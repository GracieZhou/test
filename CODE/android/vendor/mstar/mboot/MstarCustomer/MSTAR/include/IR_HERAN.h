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

#ifndef IR_FORMAT_H
#define IR_FORMAT_H

//*************************************************************************
// Customer IR Specification parameter define (Please modify them by IR SPEC)
//*************************************************************************
#define IR_MODE_SEL             IR_TYPE_FULLDECODE_MODE
#define IR_CHANNEL_USE_AS_UPDOWN   0
#define IR_VOLUME_USE_AS_LEFTRIGHT      0
// IR Header code define
#define IR_HEADER_CODE0         0x09    // Custom 0
#define IR_HEADER_CODE1         0xF6    // Custom 1

// IR Timing define
#define IR_HEADER_CODE_TIME     9000    // us
#define IR_OFF_CODE_TIME        4500    // us
#define IR_OFF_CODE_RP_TIME     2500    // us
#define IR_LOGI_01H_TIME        560     // us
#define IR_LOGI_0_TIME          1120    // us
#define IR_LOGI_1_TIME          2240    // us
#define IR_TIMEOUT_CYC          280000  // us

#define IR_HEADER_CODE_TIME_UB  20
#define IR_HEADER_CODE_TIME_LB  -20
#define IR_OFF_CODE_TIME_UB  20
#define IR_OFF_CODE_TIME_LB  -20
#define IR_OFF_CODE_RP_TIME_UB  20
#define IR_OFF_CODE_RP_TIME_LB  -20
#define IR_LOGI_01H_TIME_UB  35
#define IR_LOGI_01H_TIME_LB  -30
#define IR_LOGI_0_TIME_UB  20
#define IR_LOGI_0_TIME_LB  -20
#define IR_LOGI_1_TIME_UB  20
#define IR_LOGI_1_TIME_LB  -20
// IR Format define
#define IRKEY_DUMY              0xFF
#define IRDA_KEY_MAPPING_POWER  IRKEY_POWER

#define IR_LEADER_CODE_CHECKING_OPTION 0xBF
#define ENABLE_IR_MSTAR_SOURCE_HOTKEY   0
typedef enum _IrCommandType
{
//    IRKEY_TV_ANTENNA        = 0x0C,
    IRKEY_TV_RADIO          = 0xF1,
    IRKEY_CHANNEL_LIST      = 0x4E,
    IRKEY_CHANNEL_FAV_LIST  = 0xF3,
    IRKEY_CHANNEL_RETURN    = 0x02,
    IRKEY_CHANNEL_PLUS      = 0x50,
    IRKEY_CHANNEL_MINUS     = 0x56,

    IRKEY_AUDIO             = 0x41,
    IRKEY_VOLUME_PLUS       = 0x0F,
    IRKEY_VOLUME_MINUS      = 0x0E,

    IRKEY_UP                = 0x44,
    IRKEY_POWER             = 0x45,
    IRKEY_EXIT              = 0x19,
    IRKEY_MENU              = 0x5C,
    IRKEY_DOWN              = 0x1D,
    IRKEY_LEFT              = 0x1C,
    IRKEY_SELECT            = 0x51,
    IRKEY_RIGHT             = 0x48,

    IRKEY_NUM_0             = 0x03,
    IRKEY_NUM_1             = 0x04,
    IRKEY_NUM_2             = 0x05,
    IRKEY_NUM_3             = 0x5F,
    IRKEY_NUM_4             = 0x07,
    IRKEY_NUM_5             = 0x08,
    IRKEY_NUM_6             = 0x09,
    IRKEY_NUM_7             = 0x0A,
    IRKEY_NUM_8             = 0x0B,
    IRKEY_NUM_9             = 0x0C,

    IRKEY_MUTE              = 0x10,
    IRKEY_PAGE_UP           = 0x1A,
    IRKEY_PAGE_DOWN         = 0x1B,
    IRKEY_CLOCK             = 0x5A,

    IRKEY_INFO              = 0x40,
    IRKEY_RED               = 0x4A,
    IRKEY_GREEN             = 0x4B,
    IRKEY_YELLOW            = 0x4C,
    IRKEY_BLUE              = 0x4D,
    IRKEY_MTS               = 0x43,
    IRKEY_NINE_LATTICE      = IRKEY_DUMY,
    IRKEY_TTX               = 0x5B,
    IRKEY_CC                = 0x47,
    IRKEY_INPUT_SOURCE      = IRKEY_DUMY-14,//0x00,
    IRKEY_CRADRD            = IRKEY_DUMY-1,
//    IRKEY_PICTURE           = 0x40,
    IRKEY_ZOOM              = 0xF8,

    IRKEY_SLEEP             = 0x13,
    IRKEY_EPG               = 0x49,
    IRKEY_PIP               = 0xF5,

    IRKEY_P_CHECK           = 0x57,
    IRKEY_S_CHECK           = 0x5b,

  	IRKEY_MIX               = 0xF7,
    IRKEY_INDEX             = 0x18,
    IRKEY_HOLD              = 0x46,

    IRKEY_PREVIOUS          = 0x5E,
    IRKEY_NEXT              = 0x59,
    IRKEY_BACKWARD          = 0x14,
    IRKEY_FORWARD           = 0x15,
    IRKEY_PLAY              = 0x16,
    IRKEY_RECORD            = 0x1E,
    IRKEY_STOP              = 0x17,
    IRKEY_PAUSE             = 0xF6,

    IRKEY_POWERONLY         = 0xFE,

    IRKEY_TTX_MODE          = IRKEY_DUMY-7,
    IRKEY_UPDATE            = IRKEY_DUMY-8,
    IRKEY_SUBTITLE          = IRKEY_DUMY-9,
    IRKEY_TIME                 = IRKEY_DUMY-10,

    IRKEY_SIZE              = 0x4F,
    IRKEY_REVEAL            = 0x55,
    IRKEY_SUBCODE           = 0x12,
    IRKEY_RETURN            = 0x00,
    IRKEY_ADJUST            = IRKEY_DUMY-2,
    IRKEY_TV_INPUT          = IRKEY_DUMY-3,
    IRKEY_KEY_DISABLE_KEYPAD = IRKEY_DUMY-4,
    IRKEY_BACK              = IRKEY_DUMY-5,
    IRKEY_SUBPAGE   =   IRKEY_DUMY-6,

    IRKEY_PHOTO           = IRKEY_DUMY-11,
    IRKEY_MINU             = IRKEY_DUMY-12,
    IRKEY_GUIDE            = IRKEY_DUMY-13,


}IrCommandType;

#endif
