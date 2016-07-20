////////////////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2006-2009 MStar Semiconductor, Inc.
// All rights reserved.
//
// Unless otherwise stipulated in writing, any and all information contained
// herein regardless in any format shall remain the sole proprietary of
// MStar Semiconductor Inc. and be kept in strict confidence
// (MStar Confidential Information) by the recipient.
// Any unauthorized act including without limitation unauthorized disclosure,
// copying, use, reproduction, sale, distribution, modification, disassembling,
// reverse engineering and compiling of the contents of MStar Confidential
// Information is unlawful and strictly prohibited. MStar hereby reserves the
// rights to any and all damages, losses, costs and expenses resulting therefrom.
//
////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////
/// @file Customer_Info.h
/// @brief \b Introduction: Support for Customer Identification Information.
///
/// @author MStar Semiconductor Inc.
///
/// Features:
/// - Constant definition for Customer Information.
///////////////////////////////////////////////////////////////////////////////////////////////////

//**************************************************************************
//********************** Customer Info Input Area **************************
//**************************************************************************

typedef unsigned char U8;

/// the low byte of customer ID (Example: 20= 0x0014)
#define INPUT_CUSTOMER_ID_LOW_BYTE  0x12
/// the high byte of customer ID (Example: 20= 0x0014)
#define INPUT_CUSTOMER_ID_HIGH_BYTE 0x00

/// the low byte of model ID (Example:0001 = 0x0001)
#define INPUT_MODEL_ID_LOW_BYTE     0x45
/// the high bytes of model ID (Example:0001 = 0x0001)
#define INPUT_MODEL_ID_HIGH_BYTE    0x75

/// the low byte of chip ID (Example:000B = 0x000B)
#define INPUT_CHIP_ID_LOW_BYTE      0x8D
/// the high byte of chip ID (Example:000B = 0x000B)
#define INPUT_CHIP_ID_HIGH_BYTE     0x00


/// the low byte of chip ID (Example:000B = 0x000B)
#define INPUT_DOLBY_VER_LOW_BYTE      0x00
/// the high byte of chip ID (Example:000B = 0x000B)
#define INPUT_DOLBY_VER_HIGH_BYTE     0x00


/// DivX DRM model ID //0x3130 is default ID ,Divx for Mstar
#define DRM_MODEL_ID  0x3130

/// INPUT_SW_PROJECT <br/>
/// Chakra      -   01 <br/>
/// POLLUX      -   02 <br/>
/// ARCHIMEDES  -   03 <br/>
/// Chakra2     V  04 <br/>
/// OBAMA       V  05 <br/>
/// Supernova   V  06 <br/>
#define INPUT_SW_PROJECT            0x04

/// the software generation
#define INPUT_SW_GENERATION         0x01

/// INPUT_PRODUCT_TYPE <br/>
/// TV-01 <br/>
/// STB-02 <br/>
#define INPUT_PRODUCT_TYPE          0x01

/// INPUT_TV_SYSTEM <br/>
/// ATSC        -   01 <br/>
/// DVBT        -   02 <br/>
/// DVBT2       -   03 <br/>
/// DVBC        -   04 <br/>
/// DVBS        -   05 <br/>
/// DMBT        -   06 <br/>
/// ATV Only    -   07 <br/>
#define INPUT_TV_SYSTEM             0x02

/// the low byte of label (Example:00000456 = 0x0001C8)
#define INPUT_LABEL_LOW_BYTE        0x08
/// the middel byte of label (Example:00000456 = 0x0001C8)
#define INPUT_LABEL_MIDDLE_BYTE     0x00
/// the high byte of label (Example:00000456 = 0x0001C8)
#define INPUT_LABEL_HIGH_BYTE       0x00

/// low byte of CL(Change-List) (Example:00101234 = 0x18B72)
#define INPUT_CL_LOW_BYTE           0x10
/// middle byte of CL(Change-List) (Example:00101234 = 0x18B72)
#define INPUT_CL_MIDDLE_BYTE        0x4C
/// high byte of CL(Change-List) (Example:00101234 = 0x18B72)
#define INPUT_CL_HIGH_BYTE          0x02

/// INPUT_RELEASE_QUALITY: <br/>
/// BOOTLEG     -   01 <br/>
/// Demo        -   02 <br/>
/// Pre-Alpha   -   03 <br/>
/// Alpha       -   04 <br/>
/// Beta        -   05 <br/>
/// RC          -   06 <br/>
/// RTM         -   0  <br/>
///Quality flag can be modified by release engineer only
#define INPUT_RELEASE_QUALITY       0x01

/// CPU INPUT_CPU_TYPE<br/>
/// MIPS        -   01<br/>
/// AEON        -   02
#define  INPUT_CPU_TYPE 0x01

///Customer IP
static const U8 gconstIP_Cntrol_Mapping_1[] = "00000000"; //Customer IP Control-1
static const U8 gconstIP_Cntrol_Mapping_2[] = "08002840"; //Customer IP Control-2
static const U8 gconstIP_Cntrol_Mapping_3[] = "20000104"; //Customer IP Control-3
static const U8 gconstIP_Cntrol_Mapping_4[] = "08F80800"; //Customer IP Control-4
static const U8 gconstCustomer_hash[] = {0x6a,0x52,0xa3,0x1b,0xc8,0x64,0x65,0x4d,0x9d,0x01,0x72,0xbe,0x36,0xbe,0xe4,0x13}; 
//6a52a31bc864654d9d0172be36bee413

//**************************************************************************
//**************************************************************************
//**************************************************************************
///Customer ID Buffer, customers can put their infomation to this array table, for example: model name, ...
static const U8 gconstCID_Buf[32] =
{
    //Fix Value: Do not Modify
    'M', 'S', 'I', 'F',         // (Do not modify)Mstar Information:MSIF
    '0', '1',                   // (Do not modifyCustomer Info Class: 01

    //Customer Info area
    INPUT_CUSTOMER_ID_LOW_BYTE,
    INPUT_CUSTOMER_ID_HIGH_BYTE,

    INPUT_MODEL_ID_LOW_BYTE,
    INPUT_MODEL_ID_HIGH_BYTE,

    INPUT_CHIP_ID_LOW_BYTE,
    INPUT_CHIP_ID_HIGH_BYTE,

    INPUT_DOLBY_VER_LOW_BYTE,
    INPUT_DOLBY_VER_HIGH_BYTE,

    INPUT_SW_PROJECT,
    INPUT_SW_GENERATION,
    INPUT_PRODUCT_TYPE,
    INPUT_TV_SYSTEM,

    INPUT_LABEL_LOW_BYTE,
    INPUT_LABEL_MIDDLE_BYTE,
    INPUT_LABEL_HIGH_BYTE,

    INPUT_CL_LOW_BYTE,
    INPUT_CL_MIDDLE_BYTE,
    INPUT_CL_HIGH_BYTE,

    INPUT_RELEASE_QUALITY,

    INPUT_CPU_TYPE,
    //Reserve
    '0', '0', '0', '0', '0', '0'   // Reserve
};
