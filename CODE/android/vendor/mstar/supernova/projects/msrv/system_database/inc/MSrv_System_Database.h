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
///////////////////////////////////////////////////////////////////////////////////////////////////
/// @file MSrv_System_Database.h
/// @brief\b System_Database interface enumeration and struct definitaiotn.
/// @author MStar Semiconductor Inc.
///
/// System_Database is provid API for UI layer use.
///
/// Features:
/// - Provide the enum and struct definiation for System_Database.
///////////////////////////////////////////////////////////////////////////////////////////////////

#ifndef _MSRV_SYSTEM_DATABASE_OLD_H_
#define _MSRV_SYSTEM_DATABASE_OLD_H_

#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include "debug.h"

#include "mapi_video.h"
#include "mapi_video_out.h"
#include "mapi_display.h"
#include "mapi_ci.h"
#include "mapi_vd.h"
#include "mapi_audio.h"
#if (CA_ENABLE == 1)
#include "mapi_dvb_utility.h"
#endif
#include "MW_DTV_CalculateAspectRatio.h"
#include "MSrv.h"
#include "MSrv_Timer.h"
#include "MSrv_Picture.h"
#include "MSrv_BacklightControl.h"
#include "MSrv_Network.h"

#if (OAD_ENABLE == 1)
#include "MW_OAD_Parser.h"
#endif

#if (STEREO_3D_ENABLE == 1)
#include "MSrv_3DManager.h"
#endif

///////////////////////////////////////////////////////////////////////////////////////////////////
// SQL DB Setting
///////////////////////////////////////////////////////////////////////////////////////////////////
#if(SQL_DB_ENABLE)
// forward declaration
struct sqlite3;
struct sqlite3_stmt;

#define SQL_TEST        1 //jazzy 20111126 add for system database test
#define SQL_AFRESHCNT   30 //prepare database more times while databse is locked
#define SQL_AFRESHSLP   10 //retry sleep ms
#define STR(tbl)    #tbl
#define SQL_DB_PATH  "/tvdatabase/Database/user_setting.db"
#define SQL_DB_FACTORY_PATH  "/tvdatabase/Database/factory.db"
#define SQL_DB_CUSTOMER_PATH  "/tvdatabase/Database/customer.db"
#define SQL_DB_BACKUP_PATH  "/tvconfig/TvBackup/Database/user_setting.db"
#define SQL_DB_FACTORY_BACKUP_PATH  "/tvconfig/TvBackup/Database/factory.db"
#define SQL_DB_CUSTOMER_BACKUP_PATH  "/tvconfig/TvBackup/Database/customer.db"

#define CHECK_TABLE_DB "select * from tbl_SystemSetting"
#define CHECK_TABLE_DB_FACTORY "select * from tbl_ADCAdjust"
#define CHECK_TABLE_DB_CUSTOMER "select * from tbl_ATVDefaultPrograms"

#define BUFFERSIZE 4096

///////////////////////////////////////////////////////////////////////////////////////////////////
// All SQL DB table name
///////////////////////////////////////////////////////////////////////////////////////////////////
//------------------user_setting.db-----------------------------
#define T_3DInfo  STR(tbl_3DInfo)
#define T_3DInfo_IDX 0x00
#define T_3DSetting  STR(tbl_3DSetting)
#define T_3DSetting_IDX 0x01
#define T_BlockSysSetting   STR(tbl_BlockSysSetting)
#define T_BlockSysSetting_IDX 0x02
#define T_CECSetting   STR(tbl_CECSetting)
#define T_CECSetting_IDX 0x03
#define T_CISetting   STR(tbl_CISetting)
#define T_CISetting_IDX 0x04
#define T_DB_VERSION   STR(tbl_DB_VERSION)
#define T_DB_VERSION_IDX 0x05
#define T_DvbtPresetting   STR(tbl_DvbtPresetting)
#define T_DvbtPresetting_IDX 0x06
#define T_EpgTimer   STR(tbl_EpgTimer)
#define T_EpgTimer_IDX 0x07
#define T_FavTypeName   STR(tbl_FavTypeName)
#define T_FavTypeName_IDX 0x08
#define T_InputSource_Type   STR(tbl_InputSource_Type)
#define T_InputSource_Type_IDX 0x09
#define T_IsdbSysSetting   STR(tbl_IsdbSysSetting)
#define T_IsdbSysSetting_IDX 0x0A
#define T_IsdbUserSetting   STR(tbl_IsdbUserSetting)
#define T_IsdbUserSetting_IDX 0x0B
#define T_MediumSetting   STR(tbl_MediumSetting)
#define T_MediumSetting_IDX 0x0C
#define T_MfcMode   STR(tbl_MfcMode)
#define T_MfcMode_IDX 0x0D
#define T_NRMode   STR(tbl_NRMode)
#define T_NRMode_IDX 0x0E
#define T_NitInfo   STR(tbl_NitInfo)
#define T_NitInfo_IDX 0x0F
#define T_Nit_TSInfo   STR(tbl_Nit_TSInfo)
#define T_Nit_TSInfo_IDX 0x10
#define T_OADInfo   STR(tbl_OADInfo)
#define T_OADInfo_IDX 0x11
#define T_OADInfo_UntDescriptor   STR(tbl_OADInfo_UntDescriptor)
#define T_OADInfo_UntDescriptor_IDX 0x12
#define T_OADWakeUpInfo   STR(tbl_OADWakeUpInfo)
#define T_OADWakeUpInfo_IDX 0x13
#define T_PicMode_Setting   STR(tbl_PicMode_Setting)
#define T_PicMode_Setting_IDX 0x14
#define T_PipSetting   STR(tbl_PipSetting)
#define T_PipSetting_IDX 0x15
#define T_SoundMode_Setting   STR(tbl_SoundModeSetting)
#define T_SoundMode_Setting_IDX 0x16
#define T_SoundSetting   STR(tbl_SoundSetting)
#define T_SoundSetting_IDX 0x17
#define T_SubtitleSetting   STR(tbl_SubtitleSetting)
#define T_SubtitleSetting_IDX 0x18
#define T_MHLAutoSwtich   STR(tbl_MHLAutoSwitch)
#define T_MHLAutoSwtich_IDX 0x37
#define T_VChipSetting STR(tbl_VChipSetting)
#define T_VChipSetting_IDX 0x38
#define T_VChipMpaaItem STR(tbl_VChipMpaaItem)
#define T_VChipMpaaItem_IDX 0x39
#define T_RR5RatingPair STR(tbl_RR5RatingPair)
#define T_RR5RatingPair_IDX 0x3A
#define T_VChipRatingInfo STR(tbl_VChipRatingInfo)
#define T_VChipRatingInfo_IDX 0x3B
#define T_Regin5DimensionInfo STR(tbl_Regin5DimensionInfo)
#define T_Regin5DimensionInfo_IDX 0x3C
#define T_AbbRatingText STR(tbl_AbbRatingText)
#define T_AbbRatingText_IDX 0x3D
#define T_MiscSetting STR(tbl_MiscSetting)
#define T_MiscSetting_IDX 0x3E
#define T_mstCECSetting STR(tbl_mstCECSetting)
#define T_mstCECSetting_IDX 0x3F
#define T_CCSetting STR(tbl_CCSetting)
#define T_CCSetting_IDX 0x40
#define T_CCAdvancedSetting STR(tbl_CCAdvancedSetting)
#define T_CCAdvancedSetting_IDX 0x41

#define T_SystemSetting   STR(tbl_SystemSetting)
#define T_SystemSetting_IDX 0x19
#define T_ThreeDVideoMode   STR(tbl_ThreeDVideoMode)
#define T_ThreeDVideoMode_IDX 0x1A
#define T_TimeSetting   STR(tbl_TimeSetting)
#define T_TimeSetting_IDX 0x1B
#define T_USER_COLORTEMP   STR(tbl_UserColorTemp)
#define T_USER_COLORTEMP_IDX 0x1C
#define T_USER_COLORTEMP_EX   STR(tbl_UserColorTempEx)
#define T_USER_COLORTEMP_EX_IDX 0x1D
#define T_UserLocationSetting   STR(tbl_UserLocationSetting)
#define T_UserLocationSetting_IDX 0x1E
#define T_UserMMSetting   STR(tbl_UserMMSetting)
#define T_UserMMSetting_IDX 0x1F
#define T_UserOverScanMode   STR(tbl_UserOverScanMode)
#define T_UserOverScanMode_IDX 0x20
#define T_UserPCModeSetting   STR(tbl_UserPCModeSetting)
#define T_UserPCModeSetting_IDX 0x21
#define T_VideoSetting   STR(tbl_VideoSetting)
#define T_VideoSetting_IDX 0x22
#define T_ThreeDVideoRouterSetting STR(tbl_ThreeDVideoRouterSetting)
#define T_ThreeDVideoRouterSetting_IDX 0x23
#define T_SNConfig  STR(tbl_SNConfig)
#define T_HdmiEdidInfo STR(tbl_HdmiEdidInfo)
#define T_HdmiEdidInfo_IDX 0x45

//------------------factory.db-----------------------------
#define T_ADCAdjust   STR(tbl_ADCAdjust)
#define T_ADCAdjust_IDX 0x24
#define T_FacrotyColorTemp  STR(tbl_FactoryColorTemp)
#define T_FacrotyColorTemp_IDX 0x25
#define T_FacrotyColorTempEx  STR(tbl_FactoryColorTempEx)
#define T_FacrotyColorTempEx_IDX 0x26
#define T_FactoryExtern   STR(tbl_FactoryExtern)
#define T_FactoryExtern_IDX 0x27
#define T_NonStarndardAdjust  STR(tbl_NonStandardAdjust)
#define T_NonStarndardAdjust_IDX 0x28
#define T_SSCAdjust  STR(tbl_SSCAdjust)
#define T_SSCAdjust_IDX 0x29
#define T_NonLinearAdjust  STR(tbl_NonLinearAdjust)
#define T_NonLinearAdjust_IDX 0x2A
#define T_OverscanAdjust  STR(tbl_OverscanAdjust)
#define T_OverscanAdjust_IDX 0x2B
#define T_PEQAdjust  STR(tbl_PEQAdjust)
#define T_PEQAdjust_IDX 0x2C
#define T_Factory_DB_VERSION  STR(tbl_Factory_DB_VERSION)
#define T_Factory_DB_VERSION_IDX 0x2D
#define T_HDMIOverscanSetting  STR(tbl_HDMIOverscanSetting)
#define T_HDMIOverscanSetting_IDX 0x2E
#define T_YPbPrOverscanSetting  STR(tbl_YPbPrOverscanSetting)
#define T_YPbPrOverscanSetting_IDX 0x2F
#define T_DTVOverscanSetting  STR(tbl_DTVOverscanSetting)
#define T_DTVOverscanSetting_IDX 0x30
#define T_FactoryAudioSetting  STR(tbl_FactoryAudioSetting)
#define T_FactoryAudioSetting_IDX 0x31
#define T_ATVOverscanSetting  STR(tbl_ATVOverscanSetting)
#define T_ATVOverscanSetting_IDX 0x32

//------------------customer.db-----------------------------
#define T_ATVDefaultPrograms  STR(tbl_ATVDefaultPrograms)
#define T_ATVDefaultPrograms_IDX 0x33
#define T_DTVDefaultPrograms  STR(tbl_DTVDefaultPrograms)
#define T_DTVDefaultPrograms_IDX 0x34

#define T_NonLinearAdjust3D  STR(tbl_NonLinearAdjust3D)
#define T_NonLinearAdjust3D_IDX 0x35

#define T_FacrotyColorTempEx3D  STR(tbl_FactoryColorTempEx3D)
#define T_FacrotyColorTempEx3D_IDX 0x36

#define T_CIAdjust  STR(tbl_CIAdjust)

#define T_BootSetting  STR(tbl_BootSetting)
#define T_BootSetting_IDX 0x42

#define T_FactoryExtern_AgingMode_IDX 0x43

#define T_DvbUserSetting  STR(tbl_DvbUserSetting)
#define T_DvbUserSetting_IDX 0x44

#if (INPUT_SOURCE_LOCK_ENABLE == 1)
#define T_InputSourceLockSetting STR(tbl_InputSourceLockSetting)
#define T_InputSourceLockSetting_IDX 0x46

#define T_MAX_IDX 0x47
#else
#define T_MAX_IDX 0x46
#endif

#define MultiRow_Table_id   0
#define MultiRow_Table_Name   1
#define MultiRow_Table_Value   2
#define MultiRow_Table_Remark   3

#define U8_ARRAY_TYPE   0x01
#define U16_ARRAY_TYPE   0x02
#define U32_ARRAY_TYPE   0x04

#define QUERY_STRING_LENGTH   908//128

#define MSG_SQL_INFO(x)    //x
#define MSG_SQL_DBG(x)     //x
#define MSG_SQL_TRACE(x)     x
#define MSG_SQL_ERROR(x)     x
#endif
///////////////////////////////////////////////////////////////////////////////////////////////////


#if 1 //DVBS2 multi-favorite list
///Max number of favorite list
#define MAX_FAVLIST_NUM    8
///Max size of favorite type name
#define MAX_FAVTYPE_NAME_SIZE   32
///NonLinearAdjust Type number
#define NonLinearAdjustTypeNum  6
#endif

#if (PEQ_ENABLE == 1)
///PEQ band number
#define PEQBandNum  5
#endif

/* from SW Config */
/// PIP enable indicator
#define IsPIPEnable()                   TRUE
/// PIP suppored indicator
#define IsPIPSupported()                TRUE
/// PIP database check
#define IsPIPDBCheck()                  TRUE
/* from SW Config */

/// basic database version number
#define BASIC_DB_VERSION                    0x10
/// system database version number
#define SYSTEM_DB_VERSION                   0x11
/// factory database version number
#define FACTORY_DB_VERSION                  0x10

/// invalid database address
#define INVALID_SYS_DB_ADDR             0xffffffff

///Input Source number
#define INPUTSOURCE_NAME_MAX_SIZE 256

/// Max length of a USB serial number string
#define USB_SERIAL_NUMBER_LENGTH            32

///min_video_h_start
#define MIN_VEDIO_H_START           -10
///max_video_h_start
#define MAX_VEDIO_H_START           (0-MIN_VEDIO_H_START)
///std_video_h_start
#define STD_VEDIO_H_START           (MAX_VEDIO_H_START + MIN_VEDIO_H_START)/2
///min_video_V_start
#define MIN_VEDIO_V_START           -10
///max_video_v_start
#define MAX_VEDIO_V_START           (0-MIN_VEDIO_V_START)
///std_video_v_start
#define STD_VEDIO_V_START           (MAX_VEDIO_V_START + MIN_VEDIO_V_START)/2
///min_video_h_size
#define MIN_VEDIO_H_SIZE            -30
///max_video_h_size
#define MAX_VEDIO_H_SIZE            (0-MIN_VEDIO_H_SIZE)
///min_video_V_size
#define MIN_VEDIO_V_SIZE            -30
///max_video_V_size
#define MAX_VEDIO_V_SIZE            (0-MIN_VEDIO_V_SIZE)

#if (CA_ENABLE == 1)
/// CA phone number size
#define CA_PHONE_NO_SIZE                 (16)
/// CA user name size
#define CA_USER_NAME_SIZE                (10)
/// CA password size
#define CA_PASSWORD_SIZE                 (20)
/// CA zone ID length
#define CA_ZONE_ID_LEN                      (6)
/// CA text message buffer size
#define CA_TEXT_MESSAGE_BUFFER_SIZE         (5)
/// CA text message length
#define CA_TEXT_MESSAGE_LEN                 (256)
/// CA attr display message length
#define CA_ATTR_DISPLAY_MESSAGE_LEN         (4096)
#endif

/// DB type
typedef enum
{
    /// system database
    EN_DB_TYPE_SYSTEM = 0,
    /// factory database
    EN_DB_TYPE_FACTORY
} EN_DB_TYPE;

/** system DB address index */
typedef enum
{
    /// version address
    EN_SYS_DB_ADDR_SYS_DB_VERSION,
    /// sys setting address
    EN_SYS_DB_ADDR_SYS_SETTING,
    /// sound setting address
    EN_SYS_DB_ADDR_SOUND_SETTING,
    /// time setting
    EN_SYS_DB_ADDR_TIME_DATA,
    /// program blocking setting
    EN_SYS_DB_ADDR_BLOCK_DATA,
    /// PIP setting
    EN_SYS_DB_ADDR_PIP_DATA,
    ///video setting
    EN_SYS_DB_ADDR_VIDEO_DATA,
    /// EPG timer setting
    EN_SYS_DB_ADDR_TIMER_MANUAL_EVENT,
    /// CRID timer setting
    EN_SYS_DB_ADDR_CRID_MANUAL_EVENT,

    /// PC mode setting
    EN_SYS_DB_ADDR_MODE_SETTING,
    /// AIR/Cable setting
    EN_SYS_DB_ADDR_MEDIUM_SETTING,
    /// networking setting
    EN_SYS_DB_ADDR_NETWORK_SETTING,
#if (OAD_ENABLE == 1)
    /// OAD info setting
    EN_SYS_DB_ADDR_OAD_INFO,
    /// OAD wakeup setting
    EN_SYS_DB_ADDR_OAD_WAKEUP_INFO,
#endif
    /// factory version
    EN_SYS_DB_ADDR_FACTORY_VERSION,
    /// color temp setting
    EN_SYS_DB_ADDR_FACTORY_COLOR_TEMP,
    /// color temp setting in 16 bit
    EN_SYS_DB_ADDR_FACTORY_COLOR_TEMPEX,
    /// video ADC setting
    EN_SYS_DB_ADDR_FACTORY_VIDEO_ADC,
    /// video ADC auto-calibration flag
    EN_SYS_DB_ADDR_FACTORY_VIDEO_ADC_AUTOTUNE,
    /// factory NLA address
    EN_SYS_DB_ADDR_FACTORY_NLA_START_ADDR,
    /// factory extend address
    EN_SYS_DB_ADDR_FACTORY_EXTERN_START_ADDR,
    ///dtv overscan table address
    EN_SYS_DB_ADDR_FACTORY_OVERSCAN_DTV,
    ///hdmi overscan table address
    EN_SYS_DB_ADDR_FACTORY_OVERSCAN_HDMI,
    ///comp overscan table address
    EN_SYS_DB_ADDR_FACTORY_OVERSCAN_YPBPR,
    ///vd overscan table address
    EN_SYS_DB_ADDR_FACTORY_OVERSCAN_VD,
#if (PEQ_ENABLE == 1)
    ///factory PEQ address
    EN_SYS_DB_ADDR_FACTORY_PEQ,
#endif
#if (CI_ENABLE == 1)
    ///factory CI address
    EN_SYS_DB_ADDR_FACTORY_CI,
#endif
    EN_SYS_DB_ADDR_FACTORY_AUDIO_SETTING,
//==== Database version control: For Customer DB reserve address ====
    /// DB Customer Reserve:
    EN_SYS_DB_ADDR_CUSTOMER_DB_RESERVE,
    /// Booting Setting
    EN_SYS_DB_ADDR_BOOT_DATA,

    /// Input source lock setting
    EN_SYS_DB_ADDR_INPUT_SOURCE_LOCK_SETTING,

    /// system db end address
    EN_SYS_DB_END_ADDRESS,
}EN_SYS_DB_ADDR;


//************************************************
//******General Setting Structure Definition******
//************************************************

/** define the struct of version for system database */
typedef struct
{
    /// the checksum
    U16 u16CheckSum;
    /// the version number
    U8 u8Version;
    /// system database size, fixed, after MP
    U32 u32SystemDBSize;
}MS_DB_VERSION;


//----------------------------2. MS_USER_SOUND_SETTING --------------------------------------------
/** Define sound mode setting */
typedef enum
{
    /// sound mode is standard
    SOUND_MODE_STANDARD,
    /// sound mode is music
    SOUND_MODE_MUSIC,
    /// sound mode is movie
    SOUND_MODE_MOVIE,
    /// sound mode is sport
    SOUND_MODE_SPORTS,
    /// sound mode is user
    SOUND_MODE_USER,
#if (MSTAR_TVOS == 1)
    /// sound mode is on-site1
    SOUND_MODE_ONSITE1,
    /// sound mode is on-site2
    SOUND_MODE_ONSITE2,
#endif
    SOUND_MODE_NUM
} EN_SOUND_MODE;

/** Define audio mode setting */
typedef enum
{
    /// audio mode is LR
    AUD_MODE_LR,
    /// audio mode is LL
    AUD_MODE_LL,
    /// audio mode is RR
    AUD_MODE_RR,
    AUD_MODE_NUM
} EN_AUD_MODE;

/** Define stSoundModeSeting */
typedef struct
{
    /// stSoundModeSeting is Bass
    U8 Bass;
    /// stSoundModeSeting is Treble
    U8 Treble;
    /// stSoundModeSeting is EqBand1
    U8 EqBand1;
    /// stSoundModeSeting is EqBand2
    U8 EqBand2;
    /// stSoundModeSeting is EqBand3
    U8 EqBand3;
    /// stSoundModeSeting is EqBand4
    U8 EqBand4;
    /// stSoundModeSeting is EqBand5
    U8 EqBand5;
    /// stSoundModeSeting is EqBand6
    U8 EqBand6;
    /// stSoundModeSeting is EqBand7
    U8 EqBand7;
    /// stSoundModeSeting is UserMode
    BOOLEAN UserMode;
    /// stSoundModeSeting is Balance
    U8 Balance;
    /// stSoundModeSeting is enSoundAudioChannel
    EN_AUD_MODE      enSoundAudioChannel;
} stSoundModeSeting;

/** Define SOUND_MTS_TYPE */
typedef enum
{
    /// SOUND_MTS_TYPE is SOUND_MTS_MONO
    SOUND_MTS_MONO,
    /// SOUND_MTS_TYPE is SOUND_MTS_STEREO
    SOUND_MTS_STEREO,
    /// SOUND_MTS_TYPE is SOUND_MTS_I
    SOUND_MTS_I,
    /// SOUND_MTS_TYPE is SOUND_MTS_II
    SOUND_MTS_II,
    /// SOUND_MTS_TYPE is SOUND_MTS_NUM
    SOUND_MTS_NUM,
    /// SOUND_MTS_TYPE is SOUND_MTS_LANG_AB
    SOUND_MTS_LANG_AB,
    /// SOUND_MTS_TYPE is SOUND_MTS_NICAM
    SOUND_MTS_NICAM,
    /// SOUND_MTS_TYPE is SOUND_MTS_AUTO
    SOUND_MTS_AUTO,
    SOUND_MTS_NONE
} EN_SOUND_MTS_TYPE;

/*
    The BBE, SRS, VDS and VSPK mode number is mapped to
    En_DVB_advsndType enumeration in drvAudioProcessor.h
*/
/** Audyssey dynamic volume setting */
typedef enum
{
    /// OFF
    AUDYSSEY_DYNAMIC_VOLUME_OFF = 0,
    /// ON
    AUDYSSEY_DYNAMIC_VOLUME_ON,
    /// number of the setting
    AUDYSSEY_DYNAMIC_VOLUME_NUM
} EN_AUDYSSEY_DYNAMIC_VOLUME_MODE;

/** Audyssey EQ setting */
typedef enum
{
    /// OFF
    AUDYSSEY_EQ_OFF = 0,
    /// ON
    AUDYSSEY_EQ_ON,
    /// number of the setting
    AUDYSSEY_EQ_NUM
} EN_AUDYSSEY_EQ_MODE;


/** Define surround system type */
typedef enum
{
/// Surroud type mask
#define SURROUND_SYSTEM_TYPE_MASK 0x07
    ///surround system type is OFF
    SURROUND_SYSTEM_OFF = 0x00,

    /// BBE mode bit
#define BBE_MODE_BIT 0x10        //BBE : 0 , Viva : 1
    /// BBE mode
#define BBE_MODE 0
    /// VIVA mode
#define VIVA_MODE 1
    ///surround system type is BBE
    SURROUND_SYSTEM_BBE = 0x01,

/// dialog clarity enable bit mask
#define DIALOG_CLARITY_BIT 0x10  //enable : 1 , disable : 0
/// TRUBASE mode bit mask
#define TRUBASS_BIT 0x20         //enable : 1 , disable : 0
    ///surround system type is SRS
    SURROUND_SYSTEM_SRS = 0x02,
    ///surround system type is VDS
    SURROUND_SYSTEM_VDS = 0x03,

/// wide mode mit mask
#define WIDE_MODE_BIT 0x10       // reference : 0 , wind : 1
/// surround mode bit mask
#define SURROUND_MODE_BIT 0x20   // movie : 0 , music : 1
/// wide mode BOOLEAN
#define WIDE_MODE 1
/// reference mode BOOLEAN
#define REFERENCE_MODE 0
/// MOVIE surround
#define SURROUND_MODE_MOVIE 0
/// MUSIC surround
#define SURROUND_MODE_MUSIC 1
    ///surround system type is VSPK
    SURROUND_SYSTEM_VSPK = 0x04,
    ///surround system type is surroundmax
    SURROUND_SYSTEM_SURROUNDMAX = 0x05,

    SURROUND_SYSTEM_NUMS,
} EN_SURROUND_SYSTEM_TYPE;

/** Define surround mode */
typedef enum
{
    /// surround mode is mountain
    SURROUND_MODE_MOUNTAIN,       //0
    /// surround mode is champaign
    SURROUND_MODE_CHAMPAIGN,      //1
    /// surround mode is city
    SURROUND_MODE_CITY,           //2
    /// surround mode is theater
    SURROUND_MODE_THEATER,        //3
    SURROUND_MODE_NUM
} EN_SURROUND_TYPE;

/** define sound ad output */
typedef enum
{
    /// sound ad output is speaker
    AD_SPEAKER,
    /// sound ad output is headphone
    AD_HEADPHONE,
    /// sound ad output is both
    AD_BOTH,
} EN_SOUND_AD_OUTPUT;

/** Audio type */
typedef enum
{
    /// Audio Type: normal
    E_AUDIO_NORMAL_MODE,
    /// Audio Type: hearing impaired
    E_AUDIO_HEARING_IMPAIRED,
    /// Audio Type: visual impaired
    E_AUDIO_VISUAL_IMPAIRED,
}EN_AUDIO_TRACK_TYPE;


#if (MSTAR_TVOS == 1)
/** HDMI Audio Source */
typedef enum
{
    /// Audio source HDMI: Keep original audio source
    E_AUDIO_SOURCE_ORIGINAL,
    /// Audio source VGA: Change audio source to VGA
    E_AUDIO_SOURCE_VGA,
}EN_HDMI_AUDIO_SOURCE;
#endif

/** define user _SoundSettingType */
typedef struct _SoundSettingType
{
    /// check sum <<checksum should be put at top of the struct, do not move it to other place>>
    U16    u16CheckSum;
    /// Sound Mode Enumeration
    EN_SOUND_MODE     SoundMode;
    /// The settings for each sound mode
    stSoundModeSeting astSoundModeSetting[SOUND_MODE_NUM];
    /// Audyssey Dynamic Volume
    EN_AUDYSSEY_DYNAMIC_VOLUME_MODE AudysseyDynamicVolume;
    /// Audyssey EQ
    EN_AUDYSSEY_EQ_MODE AudysseyEQ;
    /// Surround Sound Mode
    EN_SURROUND_SYSTEM_TYPE SurroundSoundMode;
    /// surround mode enum
    EN_SURROUND_TYPE  Surround;
    /// AVC enable
    BOOLEAN           bEnableAVC;
    /// Volume
    U8                Volume;
    /// Headphone Volume
    U8                HPVolume;
    /// Balance
    U8                Balance;
    /// Primary_Flag
    U8                Primary_Flag;
    /// Audio language setting 1
    MEMBER_LANGUAGE   enSoundAudioLan1;  //EN_LANGUAGE
    /// Audio language setting 2
    MEMBER_LANGUAGE   enSoundAudioLan2;  //EN_LANGUAGE
    /// Audio mute
    U8                MUTE_Flag;     // for ATSC_TRUNK
    /// audio mode setting
    EN_AUD_MODE       enSoundAudioChannel;
    /// AD enable
    BOOLEAN           bEnableAD;
    /// AD volume adjust
    U8                ADVolume;
    /// sound ad output
    EN_SOUND_AD_OUTPUT ADOutput;
    /// the delay of SPDIF
    U8 SPDIF_Delay;
    /// the delay of speaker
    U8 Speaker_Delay;
    //audo speaker prescale value
    U8 SpeakerPreScale[MAPI_INPUT_SOURCE_NUM];;
    //audo headphone prescale value
    U8 HeadPhonePreScale[MAPI_INPUT_SOURCE_NUM];;
    //audo line-out prescale value
    U8 LineOutPreScale[MAPI_INPUT_SOURCE_NUM];;
    //audo scart1 prescale value
    U8 SCART1PreScale[MAPI_INPUT_SOURCE_NUM];;
    //audo scart2 prescale value
    U8 SCART2PreScale[MAPI_INPUT_SOURCE_NUM];;
    /// DTV Audio Type
    EN_AUDIO_TRACK_TYPE enAudioType;
#if (MSTAR_TVOS == 1)
	/// hdmi1AudioSource
    EN_HDMI_AUDIO_SOURCE enHdmi1AudioSource;
    /// hdmi2AudioSource
    EN_HDMI_AUDIO_SOURCE enHdmi2AudioSource;
    /// hdmi3AudioSource
    EN_HDMI_AUDIO_SOURCE enHdmi3AudioSource;
    /// hdmi4AudioSource
    EN_HDMI_AUDIO_SOURCE enHdmi4AudioSource;
#endif
    /// DRC enable
    BOOLEAN           bEnableDRC;
#if (KARAOKE_ENABLE==1)
    U8                MicVal;
    U8                MicEchoVal;
#endif

} MS_USER_SOUND_SETTING;
//----------------------------2. MS_USER_SOUND_SETTING --------------------------------------------


//----------------------------6. MS_BLOCKSYS_SETTING--------------------------------------------
/** define program block setting */
typedef struct
{
    /// check sum <<checksum should be put at top of the struct, do not move it to other place>>
    U16 u16CheckSum;
    /// the mode of block system
    U8 u8BlockSysLockMode;
    /// the lock without rated
    U8 u8UnratedLoack;
    /// vidoe block mode
    U8 u8VideoBlockMode;
    /// the password status of block system
    U8 u8BlockSysPWSetStatus;
    /// parental control setting
    U8 u8ParentalControl;
    /// objectcontent of parental
    U8 u8ParentalObjectiveContent;
    /// lock page is enable/disable
    U8 u8EnterLockPage;
    /// reserver for future use
    U8 Reserved;
    /// the password of block system
    U16 u16BlockSysPassword;
} MS_BLOCKSYS_SETTING;

/** PIP mode setting */
typedef enum
{
    /// pip mode on
    EN_PIP_MODE_PIP,
    /// pip mode pop and full
    EN_PIP_MODE_POP_FULL,
    /// pip mode pop
    EN_PIP_MODE_POP,
    /// PIP mode off
    EN_PIP_MODE_OFF,
    /// PIP mode invalid
    EN_PIP_MODE_INVALID,
} EN_PIP_MODE;

/** PIP size setting */
typedef enum
{
    /// PIP size small
    EN_PIP_SIZE_SMALL,
    /// PIP size small
    EN_PIP_SIZE_MEDIUM,
    /// PIP size large
    EN_PIP_SIZE_LARGE,
    /// PIP size invalid
    EN_PIP_SIZE_INVALID,
} EN_PIP_SIZE;

/** PIP position setting */
typedef enum
{
    /// PIP position is letf and top
    EN_PIP_POSITION_LEFT_TOP,
    /// PIP position is right and top
    EN_PIP_POSITION_RIGHT_TOP,
    /// PIP position is letf and buttom
    EN_PIP_POSITION_LEFT_BOTTOM,
    /// PIP position is right and buttom
    EN_PIP_POSITION_RIGHT_BOTTOM,
    //EN_PIP_POSITION_USER,             /* We do not support user-defined position */
    /// PIP position invalid
    EN_PIP_POSITION_INVALID,
} EN_PIP_POSITION;

/** PIP sound SRC setting */
typedef enum
{
    /// PIP sound SRC main
    EN_PIP_SOUND_SRC_MAIN,
    /// PIP sound SRC sub
    EN_PIP_SOUND_SRC_SUB,
    /// PIP sound SRC invalid
    EN_PIP_SOUND_SRC_INVALID,
} EN_PIP_SOUND_SRC;

/** define of pip setting */
typedef struct  st_MS_PIP_SETTING
{
    /// check sum <<checksum should be put at top of the struct, do not move it to other place>>
    U16                   u16CheckSum;
    /// PIP mode setting
    EN_PIP_MODE            enPipMode;
    /// subInputSource setting
    MAPI_INPUT_SOURCE_TYPE enSubInputSourceType;
    /// Pip size setting
    EN_PIP_SIZE            enPipSize;
    /// Pip position setting
    EN_PIP_POSITION        enPipPosition;
    /// pip bolderenable setting
    BOOLEAN                bBolderEnable;
    /// pip sound src setting
    EN_PIP_SOUND_SRC       enPipSoundSrc;
    //==Used in Factory Menu=========================
    /// border width setting 1 ~ 10
    U8                     u8BorderWidth;
    /// pip enable setting
    BOOLEAN                bPipEnable;
    //===============================================
} MS_PIP_SETTING;

/** define of local dimm */
typedef enum
{
    /// local dimm min
    MS_LOCAL_DIMM_MIN,
    /// local dimm off
    MS_LOCAL_DIMM_OFF = MS_LOCAL_DIMM_MIN,
    /// local dimm low
    MS_LOCAL_DIMM_LOW,
    /// local dimm middle
    MS_LOCAL_DIMM_MIDDLE,
    /// local dimm high
    MS_LOCAL_DIMM_HIGH,
    /// total local dimm type number
    MS_LOCAL_DIMM_NUM,
} EN_LOCAL_DIMM_MODE;

//----------------------------MS_GENSETTING_EXT--------------------------------------------

/** XC - ADC setting */
typedef struct
{
    /// check sum <<checksum should be put at top of the struct, do not move it to other place>>
    U16 u16CheckSum;
    /// gain, offset setting for ADC
    MAPI_PQL_CALIBRATION_DATA stAdcGainOffsetSetting[ADC_SET_NUMS];
    /// current calibration mode
    EN_MAPI_CALIBRATION_MODE enADCCalibrationMode[ADC_SET_NUMS];
} MS_ADC_SETTING;

/** picture mode setting */
typedef enum
{
    /// picture mode min
    PICTURE_MIN,
    /// picture mode dynamic
    PICTURE_DYNAMIC = PICTURE_MIN,
    /// picture mode normal
    PICTURE_NORMAL,
    /// picture mode mild
    PICTURE_MOVIE,
    /// picture mode user
    PICTURE_USER,
    /// picture mode game
    PICTURE_GAME,
    /// picture mode auto, auto select PC mode or Normal mode for HDMI and VGA
    PICTURE_AUTO,
    /// picture mode pc
    PICTURE_PC,
//#if 0   //consistent to PICTURE_MODE_COUNT in MainMenuFrame
#if (MSTAR_TVOS == 1)
    PICTURE_VIVID,
    PICTURE_NATURAL,
    PICTURE_SPORTS,
#endif
    /// picture mode number
    PICTURE_NUMS
} EN_MS_PICTURE;


/** define detail setting of picture mode */
typedef struct
{
    /// backlilght
    U8 u8Backlight;
    /// contrast
    U8 u8Contrast;
    /// brightness
    U8 u8Brightness;
    /// Saturation
    U8 u8Saturation;
    /// Sharpness
    U8 u8Sharpness;
    /// Hue
    U8 u8Hue;
    /// color temperature setting
    MSrv_Picture::EN_MS_COLOR_TEMP eColorTemp;
    MSrv_Picture::EN_MS_PIC_ADV eVibrantColour;
    MSrv_Picture::EN_MS_PIC_ADV ePerfectClear;
    MSrv_Picture::EN_MS_PIC_ADV eDynamicContrast;
    MSrv_Picture::EN_MS_PIC_ADV eDynamicBacklight;
    /// Active backlight handler enable/disable
    EN_MS_ACTIVE_BACKLIGHT eActiveBackLight;
    /// Auto brightness sensor handler enable/disable
    EN_MS_AUTO_BRIGHTNESS eAutoBrightnessSensor;
} T_MS_PICTURE;  //T_MS_PICTURE

/** define noise reduction setting */
typedef enum
{
    /// noise reduction min
    MS_NR_MIN,
    /// noise reduction off
    MS_NR_OFF = MS_NR_MIN,
    /// noise reduction low
    MS_NR_LOW,
    /// noise reduction middle
    MS_NR_MIDDLE,
    /// noise reduction high
    MS_NR_HIGH,
    /// noise reduction auto
    MS_NR_AUTO,
    /// total noise reduction type number
    MS_NR_NUM,
} EN_MS_NR;

/** MPEG noise reduction setting */
typedef enum
{
    /// MPEG noise reduction min
    MS_MPEG_NR_MIN,
    /// MPEG noise reduction off
    MS_MPEG_NR_OFF = MS_MPEG_NR_MIN,
    /// MPEG noise reduction low
    MS_MPEG_NR_LOW,
    /// MPEG noise reduction middle
    MS_MPEG_NR_MIDDLE,
    /// MPEG noise reduction high
    MS_MPEG_NR_HIGH,
    /// total mpeg noise reduction type number
    MS_MPEG_NR_NUM,
} EN_MS_MPEG_NR;

/** 3D Video mode */
typedef enum
{
    DB_ThreeD_Video_OFF = 0,
    DB_ThreeD_Video_2D_TO_3D,
    DB_ThreeD_Video_SIDE_BY_SIDE,
    DB_ThreeD_Video_TOP_BOTTOM,
    DB_ThreeD_Video_FRAME_INTERLEAVING,
    DB_ThreeD_Video_PACKING_1080at24p,
    DB_ThreeD_Video_PACKING_720at60p,
    DB_ThreeD_Video_PACKING_720at50p,
    DB_ThreeD_Video_CHESS_BOARD,
    DB_ThreeD_Video_COUNT
} EN_ThreeD_Video;

/** 3D Video display mode */
typedef enum
{
    /// 2D mode
    DB_ThreeD_Video_DISPLAYMODE_2D = 0,
    /// 3D mode
    DB_ThreeD_Video_DISPLAYMODE_3D,
    /// 2D to 3D mode
    DB_ThreeD_Video_DISPLAYMODE_2D_TO_3D,
    /// 3D to 2D mode
    DB_ThreeD_Video_DISPLAYMODE_3D_TO_2D,
    /// total mode number
    DB_ThreeD_Video_DISPLAYMODE_COUNT
}EN_ThreeD_Video_DISPLAYMODE;

/** 3D Video display format */
typedef enum
{
    /// 3D Side by side mode
    DB_ThreeD_Video_DISPLAYFORMAT_SIDE_BY_SIDE = 0,
    /// 3D Top Bottom mode
    DB_ThreeD_Video_DISPLAYFORMAT_TOP_BOTTOM,
    /// 3D Frame Packing mode
    DB_ThreeD_Video_DISPLAYFORMAT_FRAME_PACKING,
    /// 3D Line alternative mode
    DB_ThreeD_Video_DISPLAYFORMAT_LINE_ALTERNATIVE,
    /// 3D Frame alternative mode
    DB_ThreeD_Video_DISPLAYFORMAT_FRAME_ALTERNATIVE,
    /// 3D off mode
    DB_ThreeD_Video_DISPLAYFORMAT_NATIVE,
    /// total format number
    DB_ThreeD_Video_DISPLAYFORMAT_COUNT
}EN_ThreeD_Video_DISPLAYFORMAT;

/** 3D Video 3D Depth */
typedef enum
{
    /// 3D Depth weak
    DB_ThreeD_Video_3DDEPTH_WEAK = 0,
    /// 3D Depth middle
    DB_ThreeD_Video_3DDEPTH_MIDDLE,
    /// 3D Depth strong
    DB_ThreeD_Video_3DDEPTH_STRONG,
    /// 3D Depth total number
    DB_ThreeD_Video_3DDEPTH_COUNT
}EN_ThreeD_Video_3DDEPTH;

/** 3D Video Auto Start */
typedef enum
{
    ///auto start off
    DB_ThreeD_Video_AUTOSTART_OFF = 0,
    ///auto start 2D
    DB_ThreeD_Video_AUTOSTART_2D,
    ///auto start 3D
    DB_ThreeD_Video_AUTOSTART_3D,
    ///auto start total number
    DB_ThreeD_Video_AUTOSTART_COUNT
}EN_ThreeD_Video_AUTOSTART;

/** 3D Video 3D Output Aspect */
typedef enum
{
    /// 3D output aspect in fullscreen
    DB_ThreeD_Video_3DOUTPUTASPECT_FULLSCREEN = 0,
    /// 3D output aspect in center
    DB_ThreeD_Video_3DOUTPUTASPECT_CENTER,
    /// 3D output aspect in auto adapted
    DB_ThreeD_Video_3DOUTPUTASPECT_AUTOADAPTED,
    /// 3D output aspect total number
    DB_ThreeD_Video_3DOUTPUTASPECT_COUNT
}EN_ThreeD_Video_3DOUTPUTASPECT;

/** 3D Video LR View Switch */
typedef enum
{
    /// 3D Left Right exchanging
    DB_ThreeD_Video_LRVIEWSWITCH_EXCHANGE = 0,
    /// 3D Left Right not exchanging
    DB_ThreeD_Video_LRVIEWSWITCH_NOTEXCHANGE,
    /// 3D Left Right exchanging total number
    DB_ThreeD_Video_LRVIEWSWITCH_COUNT
}EN_ThreeD_Video_LRVIEWSWITCH;

/** 3D Video self adaptive detect triple */
typedef enum
{
    // / 3D Self Adaptive detect Off
    DB_ThreeD_Video_SELF_ADAPTIVE_DETECT_OFF = 0,
    // / 3D Self Adaptive detect Right Now
    DB_ThreeD_Video_SELF_ADAPTIVE_DETECT_RIGHT_NOW,
    // / 3D Self Adaptive detect When Source Change
    DB_ThreeD_Video_SELF_ADAPTIVE_DETECT_WHEN_SOURCE_CHANGE,
    // / total detect number
    DB_ThreeD_Video_SELFADAPTIVE_DETECT_COUNT
}EN_ThreeD_Video_SELFADAPTIVE_DETECT;

/** setting of 3D Video */
typedef struct
{
    /// 3D Video Setting
    EN_ThreeD_Video eThreeDVideo;
    /// 3D Video Display Mode
    EN_ThreeD_Video_DISPLAYMODE eThreeDVideoDisplayMode;
    /// 3D Video Display Formmat
    EN_ThreeD_Video_DISPLAYFORMAT eThreeDVideoDisplayFormat;
    /// 3D Video 3D Depth
    EN_ThreeD_Video_3DDEPTH eThreeDVideo3DDepth;
    /// 3D Video Auto Start
    EN_ThreeD_Video_AUTOSTART eThreeDVideoAutoStart;
    /// 3D Video 3D Output Aspect
    EN_ThreeD_Video_3DOUTPUTASPECT eThreeDVideo3DOutputAspect;
    /// 3D Video 3D Left Rigth View Switch
    EN_ThreeD_Video_LRVIEWSWITCH eThreeDVideoLRViewSwitch;
} ThreeD_Video_MODE;

/** MFC mode */
typedef enum
{
    MS_MFC_OFF = 0,
    MS_MFC_LOW,
    MS_MFC_MIDDLE,
    MS_MFC_HIGH,
    MS_MFC_BYPASS,
    MS_MFC_COUNT
} EN_MFC;

/** setting of MFC */
typedef struct
{
    /// MFC setting
    EN_MFC eMFC;
} MFC_MODE;

#if (ENABLE_NETREADY == 1)
typedef struct
{
    BOOL nsEnable;
    BOOL usbScanEn;
    BOOL networkScanEn;
    BOOL readyToRunNewVersion;
    BOOL testThread;
    BOOL automaticallyCheck;
}T_MS_NETREADY;
#endif

/** dynamic contrast settings */
typedef enum
{
    /// Dynamic Contrast min
    MS_Dynamic_Contrast_MIN,
    /// Dynamic Contrast off
    MS_Dynamic_Contrast_OFF = MS_Dynamic_Contrast_MIN,
    /// Dynamic Contrast on
    MS_Dynamic_Contrast_ON,
    /// Dynamic Contrast type number
    MS_Dynamic_Contrast_NUM,
} EN_MS_Dynamic_Contrast;

/** film mode settings */
typedef enum
{
    /// FILM min
    MS_FILM_MIN,
    /// FILM off
    MS_FILM_OFF = MS_Dynamic_Contrast_MIN,
    /// FILM on
    MS_FILM_ON,
    /// FILM number
    MS_FILM_NUM,
}EN_MS_FILM;


/** Super mode settings */
typedef enum
{
    /// SUPER min
    MS_SUPER_MIN,
    /// SUPER off
    MS_SUPER_OFF = MS_Dynamic_Contrast_MIN,
    /// SUPER on
    MS_SUPER_ON,
    /// SUPER number
    MS_SUPER_NUM,
}EN_MS_SUPER;

//Dawn :color temp for each source
/** input source type for color temp */
typedef enum
{
    MS_INPUT_SOURCE_TYPE_VGA,          ///<VGA input
    MS_INPUT_SOURCE_TYPE_ATV,          ///<ATV input
    MS_INPUT_SOURCE_TYPE_CVBS,         ///<AV
    MS_INPUT_SOURCE_TYPE_SVIDEO,       ///<S-video
    MS_INPUT_SOURCE_TYPE_YPBPR,        ///<Component 1
    MS_INPUT_SOURCE_TYPE_SCART,        ///<Scart
    MS_INPUT_SOURCE_TYPE_HDMI,         ///<HDMI
    MS_INPUT_SOURCE_TYPE_DTV,          ///<DTV  <DTV2
    MS_INPUT_SOURCE_TYPE_OTHERS,       ///<DVI    <Storage    <KTV     <Storage2
    MS_INPUT_SOURCE_TYPE_RVU,          ///<RVU
    MS_INPUT_SOURCE_TYPE_MAX = MS_INPUT_SOURCE_TYPE_RVU,
    MS_INPUT_SOURCE_TYPE_NUM,       ///<number of the source
    MS_INPUT_SOURCE_TYPE_NONE = MS_INPUT_SOURCE_TYPE_NUM,    ///<NULL input
} EN_MS_INPUT_SOURCE_TYPE;

/** define enum for noise reduction and mpeg noise reduction */
typedef struct
{
    /// noise reduction setting
    EN_MS_NR eNR;
    /// MPEG noise reduction setting
    EN_MS_MPEG_NR eMPEG_NR;
} T_MS_NR_MODE;

/** define color temperatue mode setting */
typedef struct
{
    /// check sum <<checksum should be put at top of the struct, do not move it to other place>>
    U16 u16CheckSum;
    /// color temperature mode setting
    mapi_pql_datatype::MAPI_PQL_COLOR_TEMP_DATA astColorTemp[MSrv_Picture::MS_COLOR_TEMP_NUM];    //24Byte
} T_MS_COLOR_TEMP;

/** define color temperatue mode setting in 16 bit */
typedef struct
{
    /// check sum <<checksum should be put at top of the struct, do not move it to other place>>
    U16 u16CheckSum;
    /// color temperature mode setting for dvbt
    mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA astColorTempEx[MSrv_Picture::MS_COLOR_TEMP_NUM][MS_INPUT_SOURCE_TYPE_NUM];    //24*8 Byte    //Dawn :color temp for each source

} T_MS_COLOR_TEMPEX;

/** SubColor Setting */
typedef struct
{
    /// check sum <<checksum should be put at top of the struct, do not move it to other place>>
    U16 u16CheckSum;
    /// brightness
    U8 u8SubBrightness;
    /// contrast
    U8 u8SubContrast;
} T_MS_SUB_COLOR;

/** overscan setting */
typedef struct
{
   ///overscanHposition
    U8   OverScanHposition;
   ///overscanVposition
    U8   OverScanVposition;
   ///OverScanHRatio
    U8   OverScanHRatio;
   ///OverScanVRatio
    U8   OverScanVRatio;
}T_MS_OVERSCAN_SETTING_USER;

#if (STB_ENABLE == 1)
/** Auto detect resolution type */
typedef enum
{
    MS_AUTO_DETECT_RES_ON,
    MS_AUTO_DETECT_RES_OFF,
} EN_MS_AUTO_DETECT_RES;
#endif //STB_ENABLE

/** define video setting for */
typedef struct
{
    /// check sum <<checksum should be put at top of the struct, do not move it to other place>>
    U16 u16CheckSum;
    /// picture mode setting
    EN_MS_PICTURE ePicture;
    /// picture mode detail setting, 24Byte
    T_MS_PICTURE astPicture[PICTURE_NUMS];
    /// enum for noise reduction and mpeg noise reduction
    T_MS_NR_MODE eNRMode[MSrv_Picture::MS_COLOR_TEMP_NUM];
    /// SubColor Setting
    T_MS_SUB_COLOR g_astSubColor;
    /// Aspect ratio type
    mapi_video_datatype::MAPI_VIDEO_ARC_Type enARCType;
    /// resolution type setting
    mapi_display_datatype::EN_DISPLAY_RES_TYPE fOutput_RES;
    /// tv system setting
    mapi_video_out_datatype::MAPI_VIDEO_OUT_VE_SYS  tvsys;
    /// last video standard mode setting
    MAPI_AVD_VideoStandardType LastVideoStandardMode;
    /// last audio standard mode setting
    AUDIOMODE_TYPE_ LastAudioStandardMode;
    /// dynamic contrast mode
    EN_MS_Dynamic_Contrast eDynamic_Contrast;
    /// film mode
    EN_MS_FILM eFilm;
    /// ThreeD_Video
    ThreeD_Video_MODE ThreeDVideoMode;
    ///useroverscan_setting
    T_MS_OVERSCAN_SETTING_USER stUserOverScanMode;
    ///// SUPER
    //SUPER_MODE SUPERMode;
    /// TV format setting (4:3/16:9SD/16:9HD)
    mapi_display_datatype::EN_DISPLAY_TVFORMAT eTvFormat;
#if (STB_ENABLE == 1)
    // auto detect resolution type
    EN_MS_AUTO_DETECT_RES enAutoDetectRes;
    /// TV color depth
    mapi_display_datatype::EN_DISPLAY_HDMITX_DC_TYPE eTvColorDepth;
#endif //STB_ENABLE
    /// Auto mode aspect ratio type
    mapi_video_datatype::MAPI_VIDEO_ARC_Type enAutoModeARCType;
    /// PC mdoe aspect ratio type
    mapi_video_datatype::MAPI_VIDEO_ARC_Type enPcModeARCType;
    /// GAME mode aspect ratio type
    mapi_video_datatype::MAPI_VIDEO_ARC_Type enGameModeARCType;
    /// Current pciture mode is 0:Normal mode or 1:PC mode , only for auto mode
    BOOL bIsPcMode;
} T_MS_VIDEO;

/** define parental control setting */
typedef enum
{
    /// parental control lock min value
    EN_F4_LockSystem_Min = 3,
    /// parental control lock max value
    EN_F4_LockSystem_Max = 18,
} EN_MENU_F4_PARENTCONTROL_RATE;


//////////////////////////////////////////////////////////////////////////////////////////
//  System Setting Structure Type
//////////////////////////////////////////////////////////////////////////////////////////

/** define user subtitle language setting */
typedef struct
{
    /// user subtitle language 1
    MEMBER_LANGUAGE  SubtitleDefaultLanguage;
    /// user subtitle language 2
    MEMBER_LANGUAGE  SubtitleDefaultLanguage_2;
    /// HardOfHearing setting, 0=Off, 1= On
    U8 fHardOfHearing: 1;
    /// subtitle enable or not, 0=Off, 1= On
    U8 fEnableSubTitle: 1;
    /// reserve for future use
    U8 Reserved : 6;
} MS_USER_SUBTITLE_SETTING;

/** define user MM setting */
typedef struct
{
    /// MM text subtitle font size
    U8 u8SubtitleSpecific;
    /// Text subtitle background color
    U8 u8SubtitleBGColor;
    /// Text subtitle font color
    U8 u8SubtitleFontColor;
    /// Photo slide show time
    U8 u8SlideShowTime;
    /// Photo slide show mode
    U8 u8SlideShowMode;
    /// Movie Preview On or off
    U8 fPreviewOn       : 1;
    /// Movie Resume on or off
    U8 fResumePlay      : 1;
    /// Photo Preview On or off
    U8 fPhotoPreviewOn       : 1;
    /// Reserved
    U8 Reserved         : 5;
} MS_USER_MM_SETTING;

/** define net setting */
typedef enum
{
    /// DHCP
    EN_NETCONFIGRATION_DHCP,
    /// STATIC
    EN_NETCONFIGRATION_STATIC,
    /// the number of network types
    EN_NETCONFIGRATION_NUMS,
}EN_NETCONFIGURATION_TYPE;

/** Encoding Type settings */
typedef enum
{
    /// UTF8
    EN_ENCODING_TYPE_UTF8,
    /// UCS2LE
    EN_ENCODING_TYPE_UCS2LE,
    /// Big5
    EN_ENCODING_TYPE_BIG5,
    /// GB18030
    EN_ENCODING_TYPE_GB18030,
    /// Western European
    EN_ENCODING_TYPE_CP1252,
    /// Central European
    EN_ENCODING_TYPE_CP1250,
    /// Baltic
    EN_ENCODING_TYPE_CP1257,
    /// Cyrillic
    EN_ENCODING_TYPE_CP1251,
    /// Arabic
    EN_ENCODING_TYPE_CP1256,
    /// Greek
    EN_ENCODING_TYPE_CP1253,
    /// Turkish
    EN_ENCODING_TYPE_CP1254,
    /// ENCODING_TYPE number
    EN_ENCODING_TYPE_NUM,
}EN_ENCODING_TYPE;


/** networking setting */
typedef struct
{
    /// flags to indicate network enable
    BOOLEAN bnetSelected;
    /// dynamic or static IP
    EN_NETCONFIGURATION_TYPE u8Netconfig;
    /// IP address byte 0
    U8 u8IPAddr0;
    /// IP address byte 1
    U8 u8IPAddr1;
    /// IP address byte 2
    U8 u8IPAddr2;
    /// IP address byte 3
    U8 u8IPAddr3;

    /// netmask byte 0
    U8 u8Netmask0;
    /// netmask byte 1
    U8 u8Netmask1;
    /// netmask byte 2
    U8 u8Netmask2;
    /// netmask byte 3
    U8 u8Netmask3;

    /// gateway byte 0
    U8 u8Gateway0;
    /// gateway byte 1
    U8 u8Gateway1;
    /// gateway byte 2
    U8 u8Gateway2;
    /// gateway byte 3
    U8 u8Gateway3;

    /// DNS byte 0
    U8 u8DNS0;
    /// DNS byte 1
    U8 u8DNS1;
    /// DNS byte 2
    U8 u8DNS2;
    /// DNS byte 3
    U8 u8DNS3;

    /// IP address of the remote device
    char Ip[MAX_SMB_BUF_LEN];
    /// net device name
    char Ip_name[MAX_SMB_BUF_LEN];
    /// user name
    char NET_User_name[MAX_SMB_BUF_LEN];
    /// password
    char NET_Password[MAX_SMB_BUF_LEN];

}MS_NETWORK_SETTING;

/** define user location */
typedef struct
{
    /// the ID of Location.
    U16 u16LocationNo;
    /// the Longitude value
    S16 s16ManualLongitude;
    /// the Latitude value
    S16 s16ManualLatitude;
} MS_USER_LOCATION_SETTING;

#if (CA_ENABLE == 1)
/** MS_IPPV_CALLBACK_INFO */
typedef struct
{
    U8 u8PhoneNo[CA_PHONE_NO_SIZE];
    U32 u32IPAddress;
    U16 u16Port;
    U8 u8Status;
    U16 u16ReportDate;
    U16 u16ReportTime;
} MS_IPPV_CALLBACK_INFO;
/** MS_DATA_MINING_PARAM */
typedef struct
{
    MAPI_U8 u8PhoneNo[CA_PHONE_NO_SIZE];
    MAPI_U32 u32IPAddress;
    MAPI_U16 u16Port;
    MAPI_U16 u16ReportDate;
    MAPI_U16 u16ReportTime;
    MAPI_U8 u8UserName[CA_USER_NAME_SIZE];
    MAPI_U8 u8Password[CA_PASSWORD_SIZE];
    MAPI_U8 u8Status;
    MAPI_U8 u8Threshold;
    MAPI_U16 u16Delay;
} MS_DATA_MINING_PARAM;
/** MS_FORCE_STATUS_REPORT */
typedef struct
{
    MAPI_U8 u8PhoneNo[CA_PHONE_NO_SIZE];
    MAPI_U32 u32IPAddress;
    MAPI_U16 u16Port;
    MAPI_U8 u8UserName[CA_USER_NAME_SIZE];
    MAPI_U8 u8Password[CA_PASSWORD_SIZE];
    MAPI_U8 u8ReportType;
} MS_FORCE_STATUS_REPORT;
/** MS_DOWNLOAD_TYPE */
typedef struct
{
    U8 u8Application;
    U8 u8System;
} MS_DOWNLOAD_TYPE;
/** MS_CA_TEXT_MESSAGE */
typedef struct
{
    MAPI_U16 u16ClubNumber;
    MAPI_SI_TIME stDeletedDate;
    MAPI_BOOL bUsed;
    MAPI_BOOL bCompressed;
    MAPI_BOOL bClub;
    MAPI_U8 u8MessageClass;
    MAPI_U8 u8Priority;
    MAPI_U8 u8Length;
    MAPI_U8 pu8MessageByte[CA_TEXT_MESSAGE_LEN];
} MS_CA_TEXT_MESSAGE;
/** MS_CA_ATTR_DISPLAY_MESSAGE */
typedef struct
{
    MAPI_U16 u16Duration;
    MAPI_U16 u16Length;
    MAPI_U8 u8MessageType;
    MAPI_U8 u8DisplayMethod;
    MAPI_U8 u8FingerprintType;
    MAPI_U8 pu8TextByte[CA_ATTR_DISPLAY_MESSAGE_LEN];
} MS_CA_ATTR_DISPLAY_MESSAGE;
#endif

/** for hdmi edid info */
typedef struct
{
    /// Hdmi Edid version
    U16 u16HdmiEdidVer;
} MS_HDMI_EDID_INFO;

/** the user settings */
typedef struct
{
    /// check sum <<checksum should be put at top of the struct, do not move it to other place>>
    U16 u16CheckSum;
    /// check to run InstallationGuide or not
    BOOLEAN fRunInstallationGuide;
    /// check if no channel to show banner
    BOOLEAN fNoChannel;
    /// check SI auto update off or not,CableReady Manual scanning shall set SI updates to "OFF" on all physical channels.
    BOOLEAN bDisableSiAutoUpdate;
    /// input source selection
    MAPI_INPUT_SOURCE_TYPE enInputSourceType;
    /// country setting
    MEMBER_COUNTRY Country;
    /// Cable Operator setting
    EN_CABLE_OPERATORS enCableOperators;
    /// Satellite platform setting
    EN_SATELLITE_PLATFORM enSatellitePlatform;
    ///Network ID field
    U16 u16NetworkId;
    /// OSD language
    MEMBER_LANGUAGE Language;
    /// subtitle language setting
    MS_USER_SUBTITLE_SETTING stSubtitleSetting;
    /// color temperature setting
    mapi_pql_datatype::MAPI_PQL_COLOR_TEMP_DATA stUserColorTemp;
    /// color temperature setting for dvbt
    mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA stUserColorTempEx[MS_INPUT_SOURCE_TYPE_NUM];    //Dawn :color temp for each source type
    /// 3D settings
    mapi_video_datatype::ST_MAPI_3D_INFO st3DInfo;
#if (STEREO_3D_ENABLE == 1)
    /// 3D ARC
    mapi_video_datatype::MAPI_VIDEO_3D_ARC_Type en3DARC;
#endif
    /// SPDIF mode setting
    SPDIF_TYPE_ enSPDIFMODE;
    /// user's MM setting
    MS_USER_MM_SETTING stUserMMSetting;
    /// Network setting
    MS_NETWORK_SETTING stNetworkSetting;
    /// user location setting
    MS_USER_LOCATION_SETTING stUserLocationSetting;
    /// SoftwareUpdate 0=Off, 1= On
    U8 fSoftwareUpdate;
    /// OAD Upddate Time
    U8 U8OADTime;
    /// OAD Scan auto execution after system bootup 0=Off, 1=On
    U8 fOADScanAfterWakeup;
    /// autovolume 0=Off, 1= On
    U8 fAutoVolume;
    /// DcPowerOFFMode 0= Power Off, 1= DC Power Off
    U8 fDcPowerOFFMode;
    /// DTV Player Extend
    U8 DtvRoute;
    /// SCART output RGB
    U8 ScartOutRGB;
    /// OSD Transparency, 0=0%, 1=25%, 2=50%, 3=75%, 4=100%
    U8 U8Transparency;
    /// OSD timeout (seconds)
    U32 u32MenuTimeOut;
    /// Audio Only
    U8 AudioOnly;
    /// watch dog
    U8 bEnableWDT;
    ///Favorite Network Region
    U8 u8FavoriteRegion;
    ///Bandwidth
    U8 u8Bandwidth;
    ///Time Shift Size Type
    U8 u8TimeShiftSizeType;
    ///Do OAD scan right now
    U8 fOadScan;
    ///Color range mode 0-255\16-235  for HDMI
    U8 u8ColorRangeMode;
    ///HDMI Audio Source  0: DVD 1: PC
    U8 u8HDMIAudioSource;
    /// PVR enable always timeshift
    U8 bEnableAlwaysTimeshift;
    /// MFC
    MFC_MODE MfcMode;
    /// enum for SUPER
    EN_MS_SUPER eSUPER;

    #if 1 //DVBS2 multi-favorite list
    ///Favorite type name
    U8 FavTypeName[MAX_FAVLIST_NUM][MAX_FAVTYPE_NAME_SIZE];
    #endif

    /// DVBS ChannelList Program Sorting Type
    U8 u8ProgramSortingType;

    /// check to Uart Bus
    BOOLEAN bUartBus;



    #if AUTOZOOM_ENABLE
    ///For DTV AutoZoom
    u8 m_AutoZoom;
    #endif

    // OverScan on/off for all source
    #if ENABLE_OVERSCAN_FOR_ALL_SOURCE
    BOOLEAN bOverScan;
    #endif

    #if (STEREO_3D_ENABLE == 1)
    /// 3D settings
    MS_3D_SETTING st3DSetting;
    #endif
#if ((ISDB_SYSTEM_ENABLE == 1) || (ESASIA_NTSC_SYSTEM_ENABLE == 1))
    /// Brazil video system
    u8  m_u8BrazilVideoStandardType;
#endif

     u8 m_u8SoftwareUpdateMode;

    #if 1
    ///For rename inputSource TV
    char InputSourceName_TV[INPUTSOURCE_NAME_MAX_SIZE];
    ///For rename inputSource Componant1
    char InputSourceName_Componant1[INPUTSOURCE_NAME_MAX_SIZE];
    ///For rename inputSource PC_RGB
    char InputSourceName_PC_RGB[INPUTSOURCE_NAME_MAX_SIZE];
    ///For rename inputSource PC_RGB2
    char InputSourceName_PC_RGB2[INPUTSOURCE_NAME_MAX_SIZE];
    ///For rename inputSource PC_RGB3
    char InputSourceName_PC_RGB3[INPUTSOURCE_NAME_MAX_SIZE];
    ///For rename inputSource HDMI1
    char InputSourceName_HDMI1[INPUTSOURCE_NAME_MAX_SIZE];
    ///For rename inputSource HDMI2
    char InputSourceName_HDMI2[INPUTSOURCE_NAME_MAX_SIZE];
    ///For rename inputSource HDMI3
    char InputSourceName_HDMI3[INPUTSOURCE_NAME_MAX_SIZE];
    ///For rename inputSource HDMI4
    char InputSourceName_HDMI4[INPUTSOURCE_NAME_MAX_SIZE];
    ///For rename inputSource AV1
    char InputSourceName_AV1[INPUTSOURCE_NAME_MAX_SIZE];
    ///For rename inputSource RVU
    char InputSourceName_RVU[INPUTSOURCE_NAME_MAX_SIZE];
    #endif

    U32 OSD_Active_Time;

    BOOL m_MessageBoxExist;//0 not exit 1 exit

#if (FREEVIEW_AU_ENABLE == 1)
    ///OAD SW VErsion
    U16 u16LastOADVersion;
#endif

    BOOLEAN bEnableAutoChannelUpdate;//run standby scan and OAD check when enter standby mode

#if (PVR_ENABLE == 1)
    U8 u8PvrUsbDeviceSerialNumber[USB_SERIAL_NUMBER_LENGTH];
    U8 u8PvrSelectedPartition;
#endif
    U32 u32MsrvTimerCounter;

    EN_LOCAL_DIMM_MODE enLocalDimm;
    BOOLEAN bATVChSwitchFreeze;//ATV channel switch mode:0->black screen,1->freeze

#if (ENABLE_NETREADY == 1)
    T_MS_NETREADY nsConfig;
#endif

#if (OFL_DET == 1)
    /// offline detect 0=Off, 1= On
    BOOLEAN bSourceDetectEnable;
    /// Auto switch source if signal plug out/in, 0=Off, 1= On
    BOOLEAN bAutoSourceSwitch;
#endif
    BOOLEAN bDisableDynamicRescan;//disable dynamic rescan at least for country Germany/Austria/Switzerland.
    ///Last TV signal source in RF for Scart Out TV mode
    MAPI_INPUT_SOURCE_TYPE enLastTVInputSourceType;
    ///Auto switch MHL when plug out/in, 0 = Off, 1 = On
    BOOLEAN bAutoMHLSwitch;
#if (FREEVIEW_AU_ENABLE == 1)
    /// Viewer prompt to update
    BOOLEAN bViewerPrompt;
#endif
#if(CI_PLUS_ENABLE == 1)
    U8 u8OpMode;
    U32 u32CicamIdentifier;
#endif

#if (CA_ENABLE == 1)
    U32 u32FeatureConfigBitmap;
    U32 u32FeatureConfigExtendBitmap;
    U8 u8DebitLimitThreshold;
    MS_IPPV_CALLBACK_INFO stIppvCallbackInfo;
    MS_DATA_MINING_PARAM stDataMiningParam;
    MS_FORCE_STATUS_REPORT stForceStatusReport;
    MS_DOWNLOAD_TYPE stDownloadType;
    U32 u32IrdSequenceNum;
    U32 u32CaPinCodeId;
    U16 u16BouquetID;
    U8 u8ZoneID[CA_ZONE_ID_LEN];
    U8 u8CustomerServicePhoneNumber[CA_PHONE_NO_SIZE];
    U8 u8PPVOrderPhoneNumber[CA_PHONE_NO_SIZE];
    MS_CA_TEXT_MESSAGE astTextMessage[CA_TEXT_MESSAGE_BUFFER_SIZE];
    MS_CA_ATTR_DISPLAY_MESSAGE stAttributeDisplay;
#endif

#if (HBBTV_ENABLE == 1)
    BOOLEAN bEnableHbbtv;
    BOOLEAN bEnableStoreCookies;
#endif
#if (WOL_ENABLE == 1)
    BOOLEAN bWOLEnable;
#endif

#if (STR_ENABLE == 1)
    /// 0: always DC --- 1: always AC --- >1: how many times of DC before AC
    U32 u32StrPowerMode;
#endif

#if (XVYCC_ENABLE == 1)
    BOOLEAN bxvYCCOnOff;
#endif

#if (STB_ENABLE == 1)
    ///Deep color status, 0 = Off, 1 = On
    BOOLEAN bDeepColor;
#endif

#if (PVR_ENABLE == 1)
  /// check if it was recording before poweroff
    BOOLEAN bRecordStopByPoweroff;
#endif
    BOOLEAN bEnableACR;

    /// Monitor HDMI ITC  0: Off  1: On
    BOOLEAN bMonitorITC;
    /// For "KDG TD 0017 Digital TV Receiver Analysis", version 1.2.1, 5.3. Bouquet and Service Arrangement Update
    BOOL bServiceListNeedRearrange;
#if ((ISDB_SYSTEM_ENABLE == 1) || (ESASIA_NTSC_SYSTEM_ENABLE == 1))
    /// BIT1:SDT, BIT2:NIT, BIT3:PAT, BIT4:PMTa, BIT5:PMTo, BIT6:EIT, BIT_ALL: All. debug mode used to control SI Monitor ON/OFF
    U8 u8MonitorDebugMode;
#endif
#if (CI_PLUS_ENABLE == 1)
    /// Store if tuner occupited by CI for CU
    BOOL bCiOccupiedTuner;
    /// Store user's pin code setting
    U16 u16CiPinCode;
#endif
    /// Current Hdmi Edid Version
    U16 u16HdmidEdidVersion;
    /// Current Encoding type
    EN_ENCODING_TYPE enEncodingType;
    /// Main window auto HDR level.
    BOOL bMainAutoDetectHdrLevel;
    /// Sub window auto HDR level.
    BOOL bSubAutoDetectHdrLevel;
    /// Main window HDR level (Manual mode).
    U8 u8MainHdrLevel;
    /// Sub window HDR level (Manual mode).
    U8 u8SubHdrLevel;
    /// Hdmi Edid Info
    MS_HDMI_EDID_INFO stHdmiEdidInfo[HDMI_PORT_MAX];
    /// Main window HDR OnOff
    BOOL bMainHdrOn;
    /// Sub window HDR OnOff
    BOOL bSubHdrOn;
} MS_USER_SYSTEM_SETTING;

/** vd signal type */
typedef enum
{
    ///NTSC
    SIG_NTSC,
    ///PAL
    SIG_PAL,
    ///SECAM
    SIG_SECAM,
    ///NTSC443
    SIG_NTSC_443,
    ///PAL_M
    SIG_PAL_M,
    ///PAL_NC
    SIG_PAL_NC,
    ///Signal number
    SIG_NUMS,
    ///Signal none
    SIG_NONE = -1
} EN_VD_SIGNALTYPE;

/** DTV overscan setting */
typedef struct
{
    /// check sum
    U16 u16CheckSum;
    ///DTV overscan table
    ST_MAPI_VIDEO_WINDOW_INFO stVideoWinInfo[E_DTV_MAX][mapi_video_datatype::E_AR_MAX];
}MS_FACTORY_DTV_OVERSCAN_SETTING;

/** HDMI overscan setting */
typedef struct
{
    /// check sum
    U16 u16CheckSum;
    ///HDMI overscan table
    ST_MAPI_VIDEO_WINDOW_INFO stVideoWinInfo[mapi_video_datatype::E_HDMI_MAX][mapi_video_datatype::E_AR_MAX];
}MS_FACTORY_HDMI_OVERSCAN_SETTING;

/** YPbPr overscan setting */
typedef struct
{
    /// check sum
    U16 u16CheckSum;
    ///YPbPr overscan table
    ST_MAPI_VIDEO_WINDOW_INFO stVideoWinInfo[mapi_video_datatype::E_YPbPr_MAX][mapi_video_datatype::E_AR_MAX];
}MS_FACTORY_YPbPr_OVERSCAN_SETTING;

/** VD overscan setting */
typedef struct
{
    /// check sum
    U16 u16CheckSum;
    ///VD overscan table
    ST_MAPI_VIDEO_WINDOW_INFO stVideoWinInfo[SIG_NUMS][mapi_video_datatype::E_AR_MAX];
}MS_FACTORY_VD_OVERSCAN_SETTING;

/** factory Audio setting */
typedef struct
{
    /// check sum
    U16 u16CheckSum;
    /// main speaker out gain offset
    U8 u8MainSpeakerOutGainOffset;
    /// headphone out gain offset
    U8 u8HeadphoneOutGainOffset;
    /// line out gain offset
    U8 u8LineoutGainOffset;
    /// SPDIF out gain offset
    U8 u8SpdifOutGainOffset;
    /// scart1 out gain offset
    U8 u8Scart1OutGainOffset;
    /// scart2 out gain offset
    U8 u8Scart2OutGainOffset;
}ST_FACTORY_AUDIO_SETTING;


//----------------------------9. MS_PIP_SETTING--------------------------------------------


//----------------------------MS_GENSETTING_EXT--------------------------------------------

//////////////////////////////////////////////////////////////////////////////////////////
//  General Setting structure type
//////////////////////////////////////////////////////////////////////////////////////////

/// memory alignment mask
#define MemAlign(n, unit)           ((((n)+(unit)-1)/(unit))*(unit))

//*************************************************************************
//*************************************************************************
//*************************************************************************
//       *****  RestoreToDefault  *****
//*************************************************************************
//*************************************************************************
//*************************************************************************
////////////////////////////////////////////////////////////////////////////////

/// restore all mask
#define RESTORE_KEEP_NONE                       (0x0)

/// restore systen DB except language setting
#define RESTORE_KEEP_SYSTEM_LANGUAGE            (0x0001)
/// restore systen DB except password setting
#define RESTORE_KEEP_SYSTEM_PASSWORD            (0x0002)
/// restore systen DB except time setting
#define RESTORE_KEEP_SYSTEM_TIME                (0x0004)

/// restore general setting
#define RESTORE_GENSETTING                      0x01
/// restore whole system DB
#define RESTORE_DATABASE                        0x02


//*************************************************************************
//       *****  RestoreToDefault  *****
//*************************************************************************

/** define the type of command to access system database */
typedef enum
{
    /// command on set/get system lock
    E_SYSTEM_LOCK = 0,
    /// command on set/get passward
    E_PASSWORD,
    /// command on set/get country setting
    E_SYSTEM_COUNTRY,
    /// command on set/get OSD language setting
    E_OSD_LANGUAGE,
    /// command on set/get OSD time page setting
    E_OSD_TIME,
    /// command on set/get video ARC setting
    E_VIDEO_ARC,
    /// command on set/get video setting
    E_VIDEO,
    /// command on set/get video ADC setting
    E_VIDEO_ADC,
    /// command on set/get color temperature setting
    E_COLOR_TEMP,
    /// command on set/get user system setting
    E_USER_SYSTEM_SETTING,
    /// command on set/get audio setting
    E_AUDIO,
    /// command on set/get audio setting
    E_INSTALLATION_FLAG,
    /// command on set/get parental control setting
    E_PARENTAL_CONTROL,
    /// command on set/get audio langauge1 setting
    E_AUDIO_LANGUAGE1,
    /// command on set/get audio langauge2 setting
    E_AUDIO_LANGUAGE2,
    /// command on set/get subtitle setting
    E_SUBTITLE_SETTING,
    /// command on set/get audio ad setting
    E_AUDIO_AD_ENABLE,
    /// command on set/get pc_mode setting
    E_PC_MODE_TABLE,
    /// command on set/get epg_timer setting
    E_EPG_TIMER,
    /// command on set/get pip setting
    E_PIP,
    /// command on set/get last video standard setting
    E_LAST_VIDEO_STANDARD,
    /// command on set/get last audio standard setting
    E_LAST_AUDIO_STANDARD,
    /// command on set/get poweroff setting
    E_DC_POWEROFF_MODE,
    /// user location
    E_LOCATION_SETTING,
    /// command on set/get VChip setting
    E_VCHIP_SETTING,
    /// command on set/get VChip rating info
    E_VCHIP_RATING_INFO,
    /// command on set/get CC setting
    E_CC_SETTING,
    /// command on set/get Medium setting
    E_MEDIUM_SETTING,
    /// command on set/get Network setting
    E_NETWORK_SETTING,
    /// CI Plus setting
    E_CI_SETTING,
    // NIT INFO
    E_NIT_INFO,

    /// command number
    E_SYSTEM_DB_COMMAND_NUM,
} EN_SYSTEM_DB_COMMAND;

/** define pc_adc mode setting */
typedef struct
{
    /// check sum <<checksum should be put at top of the struct, do not move it to other place>>
    U16 u16CheckSum;
    /// horizontal start
    U16 u16HorizontalStart;
    /// vertical start
    U16 u16VerticalStart;
    /// horizontal size
    U16 u16HorizontalTotal;
    /// mode index
    U8 u8ModeIndex;
    /// ADC phase
    U16 u16Phase;
    /// Already after auto tuned or not
    U8 u8AutoSign;
    /// save order, the smallest is the oldest.
    U8 u8Order;
    /// UI horizontal start
    U16 u16UI_HorizontalStart;
    /// UI vertical start
    U16 u16UI_VorizontalStart;
#if(SQL_DB_ENABLE == 1 && MSTAR_TVOS == 1)
    /// UI clock
    U16 u16UI_Clock;
    /// UI phase
    U16 u16UI_Phase;
#endif
    /// SyncPolarity
    U32 u32SyncPolarity;
    /// HSyncStart
    U32 u32HSyncStart;
    /// HSyncEnd
    U32 u32HSyncEnd;

} ST_PCADC_USER_MODE_SETTING;

/** Medium setting */
typedef enum
{
    /// NONE
    E_ANTENNA_TYPE_NONE,
    /// Air
    E_ANTENNA_TYPE_AIR,
    /// Cable
    E_ANTENNA_TYPE_CABLE,
} EN_ANTENNA_TYPE;

/** Cable system */
typedef enum
{
    /// NONE
    E_CABLE_SYSTEM_NONE,
    /// STD
    E_CABLE_SYSTEM_STD,
    /// IRC
    E_CABLE_SYSTEM_IRC,
    /// HRC
    E_CABLE_SYSTEM_HRC,
    /// AUTO
    E_CABLE_SYSTEM_AUTO,
} EN_CABLE_SYSTEM;

/** the medium setting */
typedef struct
{
    /// check sum <<checksum should be put at top of the struct, do not move it to other place>>
    U16 CheckSum;
    /// the medium type
    EN_ANTENNA_TYPE AntennaType;
    /// the cable system
    EN_CABLE_SYSTEM CableSystem;
    /// antenna power, 0: off, 1: on
    U8 fAntennaPower: 1;
} ST_MEDIUM_SETTING;

/** define the Network info */
typedef struct
{
    /// check sum <<checksum should be put at top of the struct, do not move it to other place>>
    U16 CheckSum;

    /// Ethernet DB
    MS_NETWORK stCurNetworkSetting;

    //Wi-Fi DB

    /// Wi-FI enable flag
    BOOL bWiFiStatus;
    /// Wi-Fi interface setting
    ST_NetworkInfoDB stWlanSetting;
    /// Info of WEP
    ST_WEP_t stLastWEPInfo;
    /// Lastest connected AP infor
    WifiBaseStationInfo stLastestConnetAP;
    /// Wi-Fi WEP Key
    U8 LastestWEPKey[MAXWEPKEYSIZE];
    /// Wi-Fi WPA Key
    U8 LastestWPAKey[MAXWPAKEYSIZE];


} ST_NETWORK_SETTING;

#if (PEQ_ENABLE == 1)
/** define factory PEQ setting */
typedef struct
{
    ///check sum
    U16 u16CheckSum;
    ///PEQ param
    AUDIO_PEQ_PARAM stPEQParam[PEQBandNum];
}ST_FACTORY_PEQ_SETTING;
#endif

#if (CI_ENABLE == 1)
/** define factory CI setting */
typedef struct
{
    ///check sum
    U16 u16CheckSum;
#if (CI_PLUS_ENABLE == 1)
    /// CI credential mode
    U8  enCredentialMode;
#endif
    ///CI PerformanceMonitor
    MAPI_BOOL bPerformanceMonitor;
    ///CI debug level 0~4
    MAPI_U8 u8CIFunctionDebugLevel[EN_CI_FUNCTION_DEBUG_COUNT];
}ST_FACTORY_CI_SETTING;
#endif

/// PC mode number
#define MAX_USER_MODE_NUM 10

//------------------------------For MSrv_System_Database private use.---------------------------------

/** define MM-Subtitle font size */
typedef enum
{
    //Font size 24
    MS_FONT_NORMAL_24,
    //Font size 28
    MS_FONT_LARGE_28,
    //Font size 20
    MS_FONT_SMALL_20,
}EN_MS_SUBTITLE_FONT_SIZE;

/** define MM-Subtitle font color */
typedef enum
{
    //Red
    MS_FONT_RED,
    //Green
    MS_FONT_GREEN,
    //Blue
    MS_FONT_BLUE,
    //white
    MS_FONT_WHITE,
}EN_MS_SUBTITLE_FONT_COLOR;

/** define MM-Subtitle background color */
typedef enum
{
    //Light gray
    MS_FONT_BG_LIGHTGRAY,
    //White
    MS_FONT_BG_WHITE,
    //Grass green
    MS_FONT_BG_GRASS_GREEN,
    //Black
    MS_FONT_BG_BLACK_TRANS,
}EN_MS_SUBTITLE_FONT_BG;

/** define lock type of access system database */
typedef enum
{
    /// read lock
    EN_SYSTEM_DB_READ_LOCK,
    ///write lock
    EN_SYSTEM_DB_WRITE_LOCK
} EN_SYSTEM_DB_LOCK_TYPE;

/// EPG timer number
#define EPG_TIMER_MAX_NUM   25
/// CRID timer number
#define CRID_TIMER_MAX_NUM   25

/** Usr-NLA point setting */
typedef struct
{
    /// ponint 0
    U32 u32OSD_V0;
    /// ponint 25
    U32 u32OSD_V25;
    /// point 50
    U32 u32OSD_V50;
    /// point 75
    U32 u32OSD_V75;
    /// point 100
    U32 u32OSD_V100;
}MS_NLA_POINT;

/** define items which use no-linear adjust */
typedef enum
{
    /// volume
    EN_NLA_VOLUME,
    /// picture brightness
    EN_NLA_BRIGHTNESS,
    /// picture contrast
    EN_NLA_CONTRAST,
    /// picture saturation
    EN_NLA_SATURATION,
    /// picture sharpness
    EN_NLA_SHARPNESS,
    /// picture hue
    EN_NLA_HUE,
    /// picture back light
    EN_NLA_BACKLIGHT,
    /// the number of the index
    EN_NLA_NUMS
}MS_NLA_SET_INDEX;

/** Usr-NLA setting */
typedef struct
{
    /// check sum <<checksum should be put at top of the struct, do not move it to other place>>
    U16 u16CheckSum;
    /// Point 0,25,50,75,100
    MS_NLA_POINT stNLASetting[EN_NLA_NUMS];
} MS_NLA_SETTING;

/// MIU SSC default span value
#define MIU_SSC_SPAN_DEFAULT            20//35
/// MIU SSC default step value
#define MIU_SSC_STEP_DEFAULT            0//10
/// MIU SSC maximum span value
#define MIU_SSC_SPAN_MAX                40
/// MIU SSC maximum step value
#define MIU_SSC_STEP_MAX                10
/// LVDS SSC default span value
#define LVDS_SSC_SPAN_DEFAULT          350 // 350
/// LVDS SSC default step value
#define LVDS_SSC_STEP_DEFAULT           200 // 200
/// LVDS SSC maximum span value
#define LVDS_SSC_SPAN_MAX               400 // 500
/// LVDS SSC maximum step value
#define LVDS_SSC_STEP_MAX               300 // 300

/** factory extended setting index */
typedef enum
{
    /// POWER MODE
    EN_FACTORY_EXT_POWER_MODE,
    /// VD
    EN_FACTORY_EXT_NSVD,
    /// VIF
    EN_FACTORY_EXT_NSVIF,
    /// SSC
    EN_FACTORY_EXT_SSC,
    /// SSC2 (2 MIU settings)
    EN_FACTORY_EXT_SSC2,
    /// 6M30 SSC
    EN_FACTORY_EXT_6M30SSC,
    /// Hi-deviation
    EN_FACTORY_EXT_HIDEV,
    /// Non-Linear
    EN_FACTORY_EXT_NLA,
#if (MSTAR_TVOS == 1)
    /// Burn in mode, when no signal input, will display different no signal color periodically
    EN_FACTORY_EXT_BURN_IN_NOSIGNAL_RGB,
    /// Auto shutdown after 15min. no signal
    EN_FACTORY_EXT_NO_SIGNAL_AUTO_SHUTDOWN,
#endif
    /// the number of the index
    EN_FACTORY_EXT_MAX
}MS_FACTORY_EXT_ITEM_INDEX;

/// define for MIU maximum numbers
#define MIU_COUNTS_MAXIMUM 8

/** SSC settings */
typedef struct
{
    /// flag to indicate LVDS SSC enable
    BOOLEAN Lvds_SscEnable;
    /// flag to indicate MIU SSC enable
    BOOLEAN Miu_SscEnable;
    /// LVDS SSC span value
    U16     Lvds_SscSpan;
    /// LVDS SSC step value
    U16     Lvds_SscStep;
    /// MIU SSC span value
    U16     Miu_SscSpan[MIU_COUNTS_MAXIMUM];
    /// LVDS SSC step value
    U16     Miu_SscStep[MIU_COUNTS_MAXIMUM];
} MS_FACTORY_SSC_SET;

/** SSC setting (2 miu settings) */
typedef struct
{
    /// LVDS SSC span value
    U16     Lvds_SscSpan;
    /// LVDS SSC step value
    U16     Lvds_SscStep;
    /// MIU0 SSC span value
    U16     Miu0_SscSpan;
    /// MIU0 SSC step value
    U16     Miu0_SscStep;
    /// MIU1 SSC span value
    U16     Miu1_SscSpan;
    /// MIU1 SSC step value
    U16     Miu1_SscStep;
    /// flag to indicate LVDS SSC enable
    BOOLEAN Lvds_SscEnable;
    /// flag to indicate MIU SSC enable
    BOOLEAN Miu_SscEnable;
} MS_FACTORY_SSC2_SET;

/** 6M30 SSC setting  */
typedef struct
{
    /// LVDS 6M30SSC span value
    U8 Lvds_6M30SscSpan;
    /// LVDS 6M30SSC step value
    U8 Lvds_6M30SscStep;
    /// MIU 6M30SSC span value
    U8 Miu_6M30SscSpan;
    /// MIU 6M30SSC step value
    U8 Miu_6M30SscStep;
    /// flag to indicate LVDS 6M30SSC enable
    BOOLEAN Lvds_6M30SscEnable;
    /// flag to indicate MIU 6M30SSC enable
    BOOLEAN Miu_6M30SscEnable;
} MS_FACTORY_6M30SSC_SET;

/** Non-Linear setting  */
typedef struct
{
    /// Non-Linear CurveType value
    U8 NonLinearCurveType;
    /// Non-Linear OSD0 value
    U8 NonLinearOSD0;
    /// Non-Linear OSD25 value
    U8 NonLinearOSD25;
    /// Non-Linear OSD50 value
    U8 NonLinearOSD50;
    /// Non-Linear OSD75 value
    U8 NonLinearOSD75;
    /// Non-Linear OSD100 value
    U8 NonLinearOSD100;
} MS_FACTORY_NONLINEAR_SET;


/// AVD PLL TRACK SPEED type
typedef enum
{
    /// OFF, K1=0x10 K2=0x20
    AVD_PLL_TRACK_OFF = 0,
    /// SPECIAL FORCE, K1=0x2E K2=0x6A
    AVD_PLL_TRACK_SPECIAL,
    /// FORCE LOW, K1=0x82 K2=0x4
    AVD_PLL_TRACK_LO,
    /// FORCE MIDDLE LOW, K1=0x90 K2=0x20
    AVD_PLL_TRACK_MID_LO,
    /// FORCE MIDDLE HIGH, K1=0x9A K2=0x35
    AVD_PLL_TRACK_MID_HI,
    /// FORCE HI, K1=0xBC K2=0x6A
    AVD_PLL_TRACK_HI,
    /// UNDEFINE
    AVD_PLL_TRACK_UNDEFINE,
} EN_AVD_PLL_TRACK_TYPE;

/// AVD FORCE SLICE type
typedef enum
{
    /// OFF
    AVD_FORCE_SLICE_OFF = 0,
    /// FORCE SLICE H
    AVD_FORCE_SLICE_H,
    /// FORCE SLICE V
    AVD_FORCE_SLICE_V,
    /// FORCE SLICE H+V
    AVD_FORCE_SLICE_HV,
    /// UNDEFINE
    AVD_FORCE_SLICE_UNDEFINE,
} EN_AVD_FORCE_SLICE_TYPE;

/** VD setting */
typedef struct
{
    /// AFEC D4
    U8 u8AFEC_D4;
    /// AFEC D8 bit 0~3
    U8 u8AFEC_D8_Bit3210;
    /// AFEC D5 bit2
    U8 u8AFEC_D5_Bit2;//[2]When CF[2]=1, K1/K2 Default Value, K1=2E,K2=6A
    /// AFEC D7 lower bound
    U8 u8AFEC_D7_LOW_BOUND;//Color kill
    /// AFEC D7 higher bound
    U8 u8AFEC_D7_HIGH_BOUND;//Color kill
    /// AFEC D9 bit 0
    U8 u8AFEC_D9_Bit0;
    /// AFEC A0
    U8 u8AFEC_A0; //only debug
    /// AFEC  A1
    U8 u8AFEC_A1; //only debug
    /// AFEC 66 bit 6~7
    U8 u8AFEC_66_Bit76;//only debug
    /// AFEC 6E bit 4~7
    U8 u8AFEC_6E_Bit7654;//only debug
    /// AFEC 6E bit 0~3
    U8 u8AFEC_6E_Bit3210;//only debug
    /// AFEC 43
    U8 u8AFEC_43;//auto or Fixed AGC
    /// AFEC 44
    U8 u8AFEC_44;//AGC gain
    /// AFEC CB
    U8 u8AFEC_CB;
    /// AFEC CF bit2 ATV
    U8 u8AFEC_CF_Bit2_ATV;
    /// AFEC CF bit2 AV
    U8 u8AFEC_CF_Bit2_AV;
    /// AFEC PLL Track Type
    EN_AVD_PLL_TRACK_TYPE enPllTrackType;
    /// AFEC Force Slice Type
    EN_AVD_FORCE_SLICE_TYPE enForceSliceType;
    /// AFEC_Gain_AutoTune_OPTION
    BOOLEAN bEnableAutoTune;
}MS_Factory_NS_VD_SET;

/** VIF setting */
typedef struct
{
    /// top
    U8 VifTop;
    /// VGA max
    U16 VifVgaMaximum;
    /// Gain distribution threshold
    U16 GainDistributionThr;
    /// VIF AGC VGA base
    U8 VifAgcVgaBase;
    /// china descrambler box mode: A1(0~5) J2(0~11) usefull
    U8 ChinaDescramblerBox;
    /// CRKP1
    U8 VifCrKp1;
    /// CRKI1
    U8 VifCrKi1;
    /// CRKP2
    U8 VifCrKp2;
    /// CRKI2
    U8 VifCrKi2;
    /// CRKP
    U8 VifCrKp;
    /// CRKI
    U8 VifCrKi;
    /// CR lock threshold
    U16 VifCrLockThr;
    /// CR threshold
    U16 VifCrThr;
    /// flag to indicate CR KPKI
    BOOLEAN VifCrKpKiAdjust;
    /// delay reduce
    U8 VifDelayReduce;
    /// over modulation
    BOOLEAN VifOverModulation;
    /// clamping values
    U16 VifClampgainClampOvNegative;
    /// clamping gain values
    U16 VifClampgainGainOvNegative;
    /// VIF AGC REF VALUE
    U8 VifACIAGCREF;
    ///VIF AGC
    U8 VifAgcRefNegative;
    ///VIF_ASIA_SIGNAL_OPTION
    BOOLEAN VifAsiaSignalOption;
    /// china descrambler box deolay,:
    U16 ChinaDescramblerBoxDelay;
}MS_Factory_NS_VIF_SET;

/** Factory Power-On Mode */
typedef enum
{
    EN_POWER_MODE_SECONDARY,
    EN_POWER_MODE_MEMORY,
    EN_POWER_MODE_DIRECT,
    EN_POWER_MODE_MAX
}MS_FACTORY_POWER_MODE;

/** Hi-Dev bandwidth setting */
typedef enum
{
    /// OFF
    EN_AUDIO_HIDEV_OFF,
    /// L1
    EN_AUDIO_HIDEV_BW_L1,
    /// L2
    EN_AUDIO_HIDEV_BW_L2,
    /// L3
    EN_AUDIO_HIDEV_BW_L3,
    /// the number of the setting
    EN_AUDIO_HIDEV_BW_MAX
}MS_FACTORY_HIDEV_INDEX;

/** factory device setting */
typedef struct
{
    /// check sum <<checksum should be put at top of the struct, do not move it to other place>>
    U16 u16CheckSum;

    /// power mode setting
    MS_FACTORY_POWER_MODE stPowerMode;
    /// VD setting
    MS_Factory_NS_VD_SET stNSVDsetting;
    /// VIF setting
    MS_Factory_NS_VIF_SET stNSVIFsetting;
    /// SSC setting
    MS_FACTORY_SSC_SET stSSCsetting;
    /// SSC2 setting
    MS_FACTORY_SSC2_SET stSSC2setting;
    /// 6M30SSC setting
    MS_FACTORY_6M30SSC_SET st6M30SSCsetting;
    /// Hi-Devivation setting
    MS_FACTORY_HIDEV_INDEX eHidevMode;
    /// Non-linear setting
    MS_FACTORY_NONLINEAR_SET stNLA;
    /// factory aging mode on/off
    BOOL m_bAgingMode;
    /// no signal auto shutdown setting
    BOOL bNoSignalAutoShutdown;
}MS_FACTORY_EXTERN_SETTING;

/** power on logo setting */
typedef enum
{
    /// Power On Logo Off
    EN_POWERON_LOGO_OFF,
    /// Power On Logo Off
    EN_POWERON_LOGO_DEFAULT,
    /// Power On Logo Capture
    EN_POWERON_LOGO_CAPTURE,
    /// the number of the setting
    EN_POWERON_LOGO_MAX
}EN_POWERON_LOGO_MODE;

#if (CEC_ENABLE == 1)
/** CEC settings */
typedef struct
{
    /// check sum <<checksum should be put at top of the struct, do not move it to other place>>
    U16 u16CheckSum;
    /// CEC on/off; 0:off, 1:on
    U8 u8CECStatus;
    /// CEC auto standby; 0:off, 1:on
    U8 u8AutoStandby;
    /// CEC ARC; 0:off, 1:on
    U8 u8ARCStatus;
    /// Audio Mode; 0:off, 1:on
    U8 u8AudioModeStatus;
    /// TV can auto power by one touch play command
    U8 u8TvAutpPowerOn;
    /// CEC Amplifier control; 0:off, 1:on
    U8 u8AmplifierControl;
    /// CEC Spearker preference; 0:TV speakers, 1:Amplifier
    U8 u8SpearkerPreference;
    /// CEC Quick menu speaker preference; 0:TV speakers, 1:Amplifier
    U8 u8QuickMenuSpeakerPreference;
} MS_CEC_SETTING;
#endif

/** for customer reserved database */
typedef struct
{
    /// checksum
    U16 u16CheckSum;
    /// customer DB reserve variable 1
    U32 u32Customer_DB_Variable_1; //For DB Version 0x11 (Example)
    U32 u32Customer_DB_Variable_2; //Reserve, Customer can use it
    U32 u32Customer_DB_Variable_3; //Reserve, Customer can use it
    U32 u32Customer_DB_Variable_4; //Reserve, Customer can use it
    U32 u32Customer_DB_Variable_5; //Reserve, Customer can use it
    U32 u32Customer_DB_Variable_6; //Reserve, Customer can use it
    U32 u32Customer_DB_Variable_7; //Reserve, Customer can use it
    U32 u32Customer_DB_Variable_8; //Reserve, Customer can use it
    U32 u32Customer_DB_Variable_9; //Reserve, Customer can use it
    U32 u32Customer_DB_Variable_10; //Reserve, Customer can use it
    U32 u32Customer_DB_Variable_11; //Reserve, Customer can use it
    U32 u32Customer_DB_Variable_12; //Reserve, Customer can use it
    U32 u32Customer_DB_Variable_13; //Reserve, Customer can use it
    U32 u32Customer_DB_Variable_14; //Reserve, Customer can use it
    U32 u32Customer_DB_Variable_15; //Reserve, Customer can use it
    U32 u32Customer_DB_Variable_16; //Reserve, Customer can use it
    U32 u32Customer_DB_Variable_17; //Reserve, Customer can use it
    U32 u32Customer_DB_Variable_18; //Reserve, Customer can use it
    U32 u32Customer_DB_Variable_19; //Reserve, Customer can use it
    U32 u32Customer_DB_Variable_20; //Reserve, Customer can use it
    U32 u32Customer_DB_Variable_21; //Reserve, Customer can use it
    U32 u32Customer_DB_Variable_22; //Reserve, Customer can use it
    U32 u32Customer_DB_Variable_23; //Reserve, Customer can use it
    U32 u32Customer_DB_Variable_24; //Reserve, Customer can use it
    U32 u32Customer_DB_Variable_25; //Reserve, Customer can use it
    U32 u32Customer_DB_Variable_26; //Reserve, Customer can use it
    U32 u32Customer_DB_Variable_27; //Reserve, Customer can use it
    U32 u32Customer_DB_Variable_28; //Reserve, Customer can use it
    U32 u32Customer_DB_Variable_29; //Reserve, Customer can use it
    U32 u32Customer_DB_Variable_30; //Reserve, Customer can use it
} MS_SYS_CUSTOMER_DB_RESERVE;

#if (MSTAR_TVOS == 1 && SQL_DB_ENABLE == 1)

/** define Customer DB Index */
typedef enum
{
    /// 3D Router Name
    EN_DB_3DVideoRouterIndex,
    /// 3D To 2D Router Name
    EN_DB_3DTo2DVideoRouterIndex,
    /// Display Mode Router Name
    EN_DB_DisplayModeRouterIndex,
    /// ADC Adjust Name
    EN_DB_FactoryADCAdjustIndex,
    /// Color Temp Name
    EN_DB_FactoryColorTempIndex,
    /// Color TempEx Name
    EN_DB_FactoryColorTempExIndex,
    /// NonLiner Name
    EN_DB_NonLinearAdjustIndex,
    /// NonLiner Name for 3D
    EN_DB_NonLinearAdjust3DIndex,
    /// Color TempEx Name for 3D
    EN_DB_FactoryColorTempEx3DIndex,
    /// 4K2K 3D Router Name
    EN_DB_4K2K3DVideoRouterIndex,
    /// 4K2K 60Hz 3D Router Name
    EN_DB_4K2K60Hz3DVideoRouterIndex,
    /// Path Max
    EN_DB_INDEX_MAX,
}EN_DB_TYPE_INDEX;

#endif
/** for booting setting */
typedef struct
{
    /// check sum <<checksum should be put at top of the struct, do not move it to other place>>
    U16             u16CheckSum;
    /// Mute Color
    U32             enMuteColor;
    /// Backend Color
    U32             enBackendColor;
    /// Frame Color
    U32             enFrameColor;
    //===============================================
} MS_BOOT_SETTING;

#if (MSTAR_TVOS == 1)
///  Factory default ATV program setting
typedef struct
{
    /// ATV program count
    U8 u8AtvProgramCount;
    /// ATV program index
    U8 u8AtvProgramIndex;

    /// RF Frequency
    U32 u32FrequencyKHz;
    /// ATV Audio standard
    U8  u8AudioStandard;
    /// ATV u8Video standard
    U8 u8VideoStandard;
}MS_FACTORY_DEFAULT_ATV_SETTING;

///  Factory default DTV program setting
typedef struct
{
    /// DTV program count
    U8 u8DtvProgramCount;
    /// DTV program index
    U8 u8DtvProgramIndex;

    /// PROGRAM INFO
    /// RF channel number
    U8 u8RfChNumber;
    /// DVBC QAM mode
    U8 u8QAMMode;
    /// RF Frequency
    U32 u32Frequency;
    /// DVBC symbol rate
    U32 u32SymbolRate;

    /// TS INFO
    /// TS id
    U16 u16TSID;
    /// orig network id
    U16 u16ONID;
    /// network id
    U16 u16NID;
    /// PCR pid
    U16 u16PCRPID;
    /// LCN
    U16 u16LCN;
    /// Pmt pid
    U16 u16PmtPID;
    /// Service id
    U16 u16ServiceID;
    /// Video pid
    U16 u16VideoPID;
    /// Audio pid
    U16 u16AudioPID;
    /// Video type
    U8 u8VideoType;
    /// Audio type
    U8 u8AudioType;
    /// Nit version
    U8 u8NitVer;
    /// Pat version
    U8 u8PatVer;
    /// Pmt version
    U8 u8PmtVer;
    /// Sdt version
    U8 u8SdtVer;
}MS_FACTORY_DEFAULT_DTV_SETTING;
#endif

#if (INPUT_SOURCE_LOCK_ENABLE == 1)
/// Input source lock setting
typedef struct
{
    MAPI_BOOL bInputSourceLockStatus[MAPI_INPUT_SOURCE_NUM];
}ST_INPUT_SOURCE_LOCK_SETTING;
#endif

/// The base class for System Database
class MSrv_System_Database : public MSrv
{
public:

    // ------------------------------------------------------------
    // public operations
    // ------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------
    /// Class Constructor.
    //-------------------------------------------------------------------------------------------------
    MSrv_System_Database();

    //-------------------------------------------------------------------------------------------------
    /// Class Destructor.
    //-------------------------------------------------------------------------------------------------
    virtual ~MSrv_System_Database();

    //-------------------------------------------------------------------------------------------------
    /// pre initial the boot setting in system database
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void preInit(void);
    //-------------------------------------------------------------------------------------------------
    /// initial the setting in system database
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void Init(void);

    //-------------------------------------------------------------------------------------------------
    /// Check the iff database exist
    /// @param type: type of database
    /// @return TRUE: database file existed
    /// @return FALSE: database file does not exist
    //-------------------------------------------------------------------------------------------------
    virtual BOOL IsDatabaseExist(EN_DB_TYPE type);


    #if (MSTAR_TVOS == 1 && SQL_DB_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Get Custmer Sql DB Path
    /// @param eIndex       \b IN: Sql Db  index
    /// @param strDbPath    \b OUT:  Db Path strings
    /// @param u8PathSize   \b OUT: Path Size string size
    /// @return TRUE: Get SQL DB path successfully
    /// @return FALSE: Get SQL DB path fail
    //-------------------------------------------------------------------------------------------------
    virtual BOOL GetCustomerSqlDbPathInfo(EN_DB_TYPE_INDEX eIndex, char** strDbPath, U8* pu8PathSize);

    //-------------------------------------------------------------------------------------------------
    /// Get Custmer Sql DB table name
    /// @param eIndex               \b IN: Sql Db  index
    /// @param strDbTableName       \b OUT: Db table name strings
    /// @param u8NameSize           \b OUT: Db table name string size
    /// @return TRUE: Get SQL DB table name successfully
    /// @return FALSE: Get SQL DB table name fail
    //-------------------------------------------------------------------------------------------------
    virtual BOOL GetCustomerSqlDbTableName(EN_DB_TYPE_INDEX eIndex, char** strDbTableName, U8* pu8NameSize);
	#endif

    //-------------------------------------------------------------------------------------------------
    /// set the color temp data in factory mode
    /// @param enColorTempIdx       \b IN: Specify the database color temp array index
    /// @param stColorTemp          \b IN: Specify the color temp data to the database
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetFactoryColorTemp(const MSrv_Picture::EN_MS_COLOR_TEMP enColorTempIdx, const mapi_pql_datatype::MAPI_PQL_COLOR_TEMP_DATA &stColorTemp);

    //-------------------------------------------------------------------------------------------------
    /// set the color tempex data in factory mode
    /// @param enColorTempIdx         \b IN: Specify the database color tempex array index1
    /// @param enInputSourceTypeIdx   \b IN: Specify the database color tempex array index2
    /// @param stColorTempex          \b IN: Specify the color tempex data to the database
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetFactoryColorTempEx(const MSrv_Picture::EN_MS_COLOR_TEMP enColorTempIdx, const EN_MS_INPUT_SOURCE_TYPE enInputSourceTypeIdx, const mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA &stColorTempex);

    //-------------------------------------------------------------------------------------------------
    /// set the video adc data in factory mode
    /// @param enADCSetIdx          \b IN: Specify the database adc array index
    /// @param stCalibrationData    \b IN: Specify the calibration data to the database
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetFactoryVideoADC(const E_ADC_SET_INDEX enADCSetIdx, const MAPI_PQL_CALIBRATION_DATA &stCalibrationData);

    //-------------------------------------------------------------------------------------------------
    /// set the video adc calibration mode sw or hw
    /// @param enADCSetIdx              \b IN: Specify the database adc array index
    /// @param enADCCalibrationMode     \b IN: Specify the calibration mode to the database
    /// @return TRUE: set succeed
    /// @return FALSE: set fail.
    //-------------------------------------------------------------------------------------------------
    virtual BOOL SetADCCalibrationMode(const E_ADC_SET_INDEX enADCSetIdx, const EN_MAPI_CALIBRATION_MODE enADCCalibrationMode);

    //-------------------------------------------------------------------------------------------------
    /// set the data to defualt in factory mode
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetFactoryRestoreDefault(void);

    //TVOS: will be deleted
    //-------------------------------------------------------------------------------------------------
    /// Get the program block status
    /// @return TRUE: system blocked
    /// @return FALSE: system not blocked
    //-------------------------------------------------------------------------------------------------
    virtual U8 GetSystemLock();

    //-------------------------------------------------------------------------------------------------
    /// Get the program block status
    /// @return TRUE: system blocked
    /// @return FALSE: system not blocked
    //-------------------------------------------------------------------------------------------------
    virtual BOOL IsSystemLocked();

    //TVOS: will be deleted
    //-------------------------------------------------------------------------------------------------
    /// Get the status of ParentalControl setting
    /// @return U8: parent control setting
    //-------------------------------------------------------------------------------------------------
    virtual U8 GetParentalControl();

    //-------------------------------------------------------------------------------------------------
    /// Get the status of ParentalControl setting
    /// @return U8: parent control setting
    //-------------------------------------------------------------------------------------------------
    virtual U8 GetParentalControlRating();


    //-------------------------------------------------------------------------------------------------
    /// Get the status of ParentalObjectiveContent setting
    /// @return U8: Parental Objective Content setting
    //-------------------------------------------------------------------------------------------------
    virtual U8 GetParentalObjectiveContent();

    //TVOS: will be deleted
    //-------------------------------------------------------------------------------------------------
    /// Get the password in database for Block Program
    /// @return U16: system lock password
    //-------------------------------------------------------------------------------------------------
    virtual U16 GetPassword();

    //-------------------------------------------------------------------------------------------------
    /// Get the password in database for Block Program
    /// @return U16: system lock password
    //-------------------------------------------------------------------------------------------------
    virtual U16 GetSystemLockPassword();

    //-------------------------------------------------------------------------------------------------
    /// Get Current Auto MHL Switch status
    /// @return BOOLEAN: auto switch on/off
    //-------------------------------------------------------------------------------------------------
    virtual BOOLEAN GetAutoMHLSwitch();

    //-------------------------------------------------------------------------------------------------
    /// Set Current Auto MHL Switch status
    /// @param bIsOpened          \b IN: Indicates the Auto MHL Switch on/off
    //-------------------------------------------------------------------------------------------------
    virtual void SetAutoMHLSwitch(BOOLEAN bIsOpened);

    //-------------------------------------------------------------------------------------------------
    /// Get Current System Country Setting
    /// @return MEMBER_COUNTRY: System Country Setting
    //-------------------------------------------------------------------------------------------------
    virtual MEMBER_COUNTRY GetSystemCountry();

    //-------------------------------------------------------------------------------------------------
    /// Get Current System cable operators
    /// @return EN_CABLE_OPERATORS: System Cable Operators
    //-------------------------------------------------------------------------------------------------
    virtual EN_CABLE_OPERATORS GetSystemCableOperators();

    //TVOS: will be deleted
    //-------------------------------------------------------------------------------------------------
    /// Get System SI auto update off or not
    /// @return True: auto update on
    /// @return False: auto update off
    //-------------------------------------------------------------------------------------------------
    virtual BOOL GetDisableSiAutoUpdate();
#if (STB_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Get System Deep Color off or not
    /// @return True: Deep Color on
    /// @return False: Deep Color off
    //-------------------------------------------------------------------------------------------------
    virtual BOOL GetDeepColorStatus();
#endif
    //-------------------------------------------------------------------------------------------------
    /// Get Current System Network ID Field
    /// @return U16: System Network ID Field
    //-------------------------------------------------------------------------------------------------
    virtual U16 GetSystemNetworkId();

    //-------------------------------------------------------------------------------------------------
    /// Get Current OSD Language Setting
    /// @return MEMBER_LANGUAGE: OSD Language Setting
    //-------------------------------------------------------------------------------------------------
    virtual MEMBER_LANGUAGE GetOsdLanguage();

#if (FREEVIEW_AU_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Get last OAD version
    /// @return U16: Last OAD version
    //-------------------------------------------------------------------------------------------------
    virtual U16 GetLastOADVersion();
#endif

#if (CA_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// GetFeatureConfigBitmap
    /// @return U32 \b OUT: return feature config bitmap
    //-------------------------------------------------------------------------------------------------
    U32 GetFeatureConfigBitmap();

    //-------------------------------------------------------------------------------------------------
    /// GetFeatureConfigExtendBitmap
    /// @return U32 \b OUT: return feature config extend bitmap
    //-------------------------------------------------------------------------------------------------
    U32 GetFeatureConfigExtendBitmap();

    //-------------------------------------------------------------------------------------------------
    /// GetDebitLimitThreshold
    /// @return U8 \b OUT: return debit limit threshold
    //-------------------------------------------------------------------------------------------------
    U8 GetDebitLimitThreshold();

    //-------------------------------------------------------------------------------------------------
    /// GetIppvCallbackInfo
    /// @param pValue          \b OUT: return IPPV callback info
    /// @return BOOL \b OUT: TRUE: success, FALSE: failure
    //-------------------------------------------------------------------------------------------------
    BOOL GetIppvCallbackInfo(MS_IPPV_CALLBACK_INFO *pValue);

    //-------------------------------------------------------------------------------------------------
    /// GetDataMiningParam
    /// @param pValue          \b OUT: return data mining param
    /// @return BOOL \b OUT: TRUE: success, FALSE: failure
    //-------------------------------------------------------------------------------------------------
    BOOL GetDataMiningParam(MS_DATA_MINING_PARAM *pValue);

    //-------------------------------------------------------------------------------------------------
    /// GetForceStatusReport
    /// @param pValue          \b OUT: return force status report
    /// @return BOOL \b OUT: TRUE: success, FALSE: failure
    //-------------------------------------------------------------------------------------------------
    BOOL GetForceStatusReport(MS_FORCE_STATUS_REPORT *pValue);

    //-------------------------------------------------------------------------------------------------
    /// GetDownloadType
    /// @param pValue          \b OUT: return download type
    /// @return BOOL \b OUT: TRUE: success, FALSE: failure
    //-------------------------------------------------------------------------------------------------
    BOOL GetDownloadType(MS_DOWNLOAD_TYPE *pValue);

    //-------------------------------------------------------------------------------------------------
    /// GetIRDCmdSequenceNum
    /// @return U32 \b OUT: IRD command sequence number
    //-------------------------------------------------------------------------------------------------
    U32 GetIRDCmdSequenceNum();

    //-------------------------------------------------------------------------------------------------
    /// GetCaPinCodeId
    /// @return U32 \b OUT: CA pin code ID
    //-------------------------------------------------------------------------------------------------
    U32 GetCaPinCodeId();

    //-------------------------------------------------------------------------------------------------
    /// GetCaBouquetId
    /// @return U16 \b OUT: CA bouquet ID
    //-------------------------------------------------------------------------------------------------
    U16 GetCaBouquetId();

    //-------------------------------------------------------------------------------------------------
    /// GetZoneId
    /// @param pValue    \b OUT: return zone ID
    /// @return BOOL \b OUT: TRUE: success, FALSE: failure
    //-------------------------------------------------------------------------------------------------
    BOOL GetZoneId(U8 *pValue);

    //-------------------------------------------------------------------------------------------------
    /// GetCustomerServicePhone
    /// @param pValue    \b OUT: return customer service phone
    /// @return BOOL \b OUT: TRUE: success, FALSE: failure
    //-------------------------------------------------------------------------------------------------
    BOOL GetCustomerServicePhone(U8 *pValue);

    //-------------------------------------------------------------------------------------------------
    /// GetPPVOrderPhone
    /// @param pValue    \b OUT: PPV order phone
    /// @return BOOL \b OUT: TRUE: success, FALSE: failure
    //-------------------------------------------------------------------------------------------------
    BOOL GetPPVOrderPhone(U8 *pValue);

    //-------------------------------------------------------------------------------------------------
    /// GetCaTextMessage
    /// @param u8Index    \b IN: index for query
    /// @param pValue    \b OUT: return CA text message
    /// @return BOOL \b OUT: TRUE: success, FALSE: failure
    //-------------------------------------------------------------------------------------------------
    BOOL GetCaTextMessage(U8 u8Index, MS_CA_TEXT_MESSAGE *pValue);

    //-------------------------------------------------------------------------------------------------
    /// GetCaAttrDisplayMessage
    /// @param pValue    \b OUT: return CA attr display message
    /// @return BOOL \b OUT: TRUE: success, FALSE: failure
    //-------------------------------------------------------------------------------------------------
    BOOL GetCaAttrDisplayMessage(MS_CA_ATTR_DISPLAY_MESSAGE *pValue);
#endif

    //-------------------------------------------------------------------------------------------------
    /// Get DTV Route Status
    /// @return U8: index of EN_MSRV_DTV_ROUTE, 0 - (E_MSRV_DTV_ROUTE_MAX - 1)
    //-------------------------------------------------------------------------------------------------
    virtual U8 GetDtvRoute(void);

    //-------------------------------------------------------------------------------------------------
    /// Get the OSD Time
    /// @param pValue \b IN: the pointer of the structure MS_TIME
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetOsdTime(MS_TIME *pValue);

    //TVOS: will be deleted
    //-------------------------------------------------------------------------------------------------
    /// Get the video info about the picture mode setting, SubColor Setting, ...
    /// @param pValue \b IN: the pointer to the structure of T_MS_VIDEO
    /// @param pParam \b IN: the pointer to the Input Source
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetVideo(T_MS_VIDEO *pValue, MAPI_INPUT_SOURCE_TYPE *pParam);

    //-------------------------------------------------------------------------------------------------
    /// Get the video info about the picture mode setting, SubColor Setting, ...
    /// @param pValue \b IN: the pointer to the structure of T_MS_VIDEO
    /// @param pParam \b IN: the pointer to the Input Source
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetVideoSetting(T_MS_VIDEO *pValue, MAPI_INPUT_SOURCE_TYPE *pParam);

    //-------------------------------------------------------------------------------------------------
    /// Get the ADC Value in Database
    /// @param pValue \b IN: the pointer to the structure of mapi_pql_datatype::MAPI_PQL_CALIBRATION_DATA
    /// @param pParam \b IN: the pointer to the Input Source
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetVideoAdc(MAPI_PQL_CALIBRATION_DATA *pValue, void *pParam);

    //-------------------------------------------------------------------------------------------------
    /// Get the ADC calibration mode from Database
    /// @param enADCSetIdx \b IN: Specify the database adc set index
    /// @return EN_MAPI_CALIBRATION_MODE: \b: current calibration mode
    //-------------------------------------------------------------------------------------------------
    virtual EN_MAPI_CALIBRATION_MODE GetADCCalibrationMode(const E_ADC_SET_INDEX enADCSetIdx);

    //TVOS: will be deleted
    //-------------------------------------------------------------------------------------------------
    /// Get the current ADC Auto Tune Status
    /// @param pValue \b IN: the pointer to the Bool Variable.
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetVideoAdcAutoTune(BOOL *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Get the current ADC Auto Tune Status
    /// @param pValue \b IN: the pointer to the Bool Variable.
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void ISVideoAdcAutoTuneEnabled(BOOL *pValue);

    //TVOS: will be deleted
    //-------------------------------------------------------------------------------------------------
    /// Get Aging Mode On/off
    /// @param pValue \b IN: the pointer to the Bool Variable.
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetAgingMode(BOOL *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Get Aging Mode On/off
    /// @param pValue \b IN: the pointer to the Bool Variable.
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void IsAgingModeEnabled(BOOL *pValue);

    //Dawn :color temp for each source type
    //-------------------------------------------------------------------------------------------------
    /// Trans Mapi input source type to Ms input source type
    /// @param enInputSrc \b IN: the input source index
    /// @return EN_MS_INPUT_SOURCE_TYPE: input source type
    //-------------------------------------------------------------------------------------------------
    virtual EN_MS_INPUT_SOURCE_TYPE TransMapiInputSourceToMsInputSoutrceType(MAPI_INPUT_SOURCE_TYPE enInputSrc);

    //-------------------------------------------------------------------------------------------------
    /// Get the Color Tempurature
    /// @param pValue \b IN: the pointer to the data structure mapi_pql_datatype::MAPI_PQL_COLOR_TEMP_DATA
    /// @param pParam \b IN: the pointer to the InputSource
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetColorTemp(mapi_pql_datatype::MAPI_PQL_COLOR_TEMP_DATA *pValue, void *pParam);

    //-------------------------------------------------------------------------------------------------
    /// Get the Ex Color Tempurature
    /// @param pValue \b IN: the pointer to the data structure mapi_pql_datatype::MAPI_PQL_COLOR_TEMP_DATA
    /// @param pParam \b IN: the pointer to the InputSource
    /// @param enSrcType \b IN:InputSource type
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetColorTempEx(mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA *pValue, void *pParam, MAPI_INPUT_SOURCE_TYPE enSrcType);

    //-------------------------------------------------------------------------------------------------
    /// Get the Video Arc info
    /// @param pValue \b IN: mapi_video_datatype::MAPI_VIDEO_ARC_Type
    /// @param pParam \b IN: the pointer to the InputSource
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetVideoArc(mapi_video_datatype::MAPI_VIDEO_ARC_Type *pValue, MAPI_INPUT_SOURCE_TYPE *pParam);

    //Dawn :user overscan
    //-------------------------------------------------------------------------------------------------
    /// Set the user overscan setting
    /// @param pValue \b IN: T_MS_OVERSCAN_SETTING_USER
    /// @param pParam \b IN: the pointer to the InputSource
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetUserOverscanSetting(T_MS_OVERSCAN_SETTING_USER *pValue, MAPI_INPUT_SOURCE_TYPE *pParam);

    //Dawn :user overscan
    //-------------------------------------------------------------------------------------------------
    /// Get the user overscan setting
    /// @param pValue \b IN: T_MS_OVERSCAN_SETTING_USER
    /// @param pParam \b IN: the pointer to the InputSource
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetUserOverscanSetting(T_MS_OVERSCAN_SETTING_USER *pValue, MAPI_INPUT_SOURCE_TYPE *pParam);

    //Dawn :user overscan
    //-------------------------------------------------------------------------------------------------
    /// Get the real overscan value
    /// @param pValue \b IN: ST_MAPI_VIDEO_WINDOW_INFO
    /// @param pParam \b IN: the pointer to the InputSource
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetVideoOverscanSetting(ST_MAPI_VIDEO_WINDOW_INFO *pValue, MAPI_INPUT_SOURCE_TYPE *pParam);

    //-------------------------------------------------------------------------------------------------
    /// Get the Favorite Type Name for DVBS2 multi-favorite list
    /// @param u8FavType \b IN: 0~7
    /// @param pValue    \b IN: the pointer to the return values
    /// @return BOOL: True: OK
    /// @return BOOL: False: Fail
    //-------------------------------------------------------------------------------------------------
    virtual BOOL GetFavTypeName(U8 u8FavType,U8 *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Get the System Setting
    /// @param pValue \b IN: the pointer to the structure of MS_USER_SYSTEM_SETTING
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetUserSystemSetting(MS_USER_SYSTEM_SETTING *pValue);

    //TVOS: will be deleted
    //-------------------------------------------------------------------------------------------------
    /// Get the Audio setting info
    /// @param pValue \b IN: the pointer to the structure of MS_USER_SOUND_SETTING
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetAudio(MS_USER_SOUND_SETTING *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Get the Audio setting info
    /// @param pValue \b IN: the pointer to the structure of MS_USER_SOUND_SETTING
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetAudioSetting(MS_USER_SOUND_SETTING *pValue);

    //TVOS: will be deleted
    //-------------------------------------------------------------------------------------------------
    /// Get the Installation Flag (First time Boot Up => True)
    /// @param pValue \b IN: the pointer to the return Value. True: OK, False: Fail
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetInstallationFlag(BOOL *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Get the Installation Flag (First time Boot Up => True)
    /// @param pValue \b IN: the pointer to the return Value. True: OK, False: Fail
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void IsInstallationGuideEnabled(BOOL *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Get No Channel Flag
    /// @param pValue \b IN: the pointer to the return Value. True: No Channel, False: Channel Exist
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetNoChannelFlag(BOOL *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Get the Audio Language Type
    /// @param pValue \b IN: the pointer to the structure of MEMBER_LANGUAGE
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetAudioLanguage1(MEMBER_LANGUAGE *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Get the Audio Language2
    /// @param pValue \b IN: the pointer to the structure of MEMBER_LANGUAGE
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetAudioLanguage2(MEMBER_LANGUAGE *pValue);

    //TVOS: will be deleted
    //-------------------------------------------------------------------------------------------------
    /// Get the Audio Enable Status
    /// @param pValue \b IN: the pointer to the return Value. True: Enable, False: Disable
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetAudioAdEnable(BOOLEAN *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Get the Audio Enable Status
    /// @param pValue \b IN: the pointer to the return Value. True: Enable, False: Disable
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void IsAudioADEnabled(BOOLEAN *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Get the PC Mode Table
    /// @param pValue \b IN: the pointer to the structure of ST_PCADC_USER_MODE_SETTING
    /// @param pParam \b IN: the pointer to the input source
    /// @return TURE: get Success
    /// @return FALSE: get Fail
    //-------------------------------------------------------------------------------------------------
    virtual bool GetPcModeTable(ST_PCADC_USER_MODE_SETTING *pValue, void *pParam);

    //-------------------------------------------------------------------------------------------------
    /// check  pc mode index is exist
    /// @param u8ModeIdex         \b IN: pc mode index
    /// @return TURE: PC mode index existed
    /// @return FALSE: PC mode index does not exist
    //-------------------------------------------------------------------------------------------------
    virtual BOOL IsModeIndexExisted(U8 u8ModeIdex);

    //-------------------------------------------------------------------------------------------------
    /// Get the EPG Timer
    /// @param pValue \b IN: the pointer to the structure of ST_EPG_EVENT_TIMER_INFO
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetEpgTimer(ST_EPG_EVENT_TIMER_INFO *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Get the EPG Time List
    /// @param pValue \b IN: the pointer to the structure of ST_EPG_EVENT_TIMER_INFO
    /// @param size     \b IN: the number of the EPG events
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void UseEpgTimerList(ST_EPG_EVENT_TIMER_INFO **pValue, U32* size);

    //TVOS: will be deleted
    //-------------------------------------------------------------------------------------------------
    /// Get the auto channel update flag setting by user
    /// @return TURE: Enable auto channel update
    /// @return FALSE: Disable auto channel update
    //-------------------------------------------------------------------------------------------------
    virtual BOOLEAN GetAutoChannelUpdate(void);

    //-------------------------------------------------------------------------------------------------
    /// Get the auto channel update flag setting by user
    /// @return TURE: Enable auto channel update
    /// @return FALSE: Disable auto channel update
    //-------------------------------------------------------------------------------------------------
    virtual BOOLEAN IsAutoChannelUpdateEnabled(void);

    //-------------------------------------------------------------------------------------------------
    /// Init CRID timer setting
    /// @return none
    //-------------------------------------------------------------------------------------------------
    void InitCRIDTimerSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// Restore default CRID timer setting
    /// @return none
    //-------------------------------------------------------------------------------------------------
    void RestoreDefaultCRIDTimer(void);

    //-------------------------------------------------------------------------------------------------
    /// Load CRID timer setting
    /// @return none
    //-------------------------------------------------------------------------------------------------
    void LoadCRIDTimerSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// Get CRID timer List
    /// @param pValue \b OUT: return CRID timer info
    /// @return none
    //-------------------------------------------------------------------------------------------------
    void GetCRIDTimerList(ST_CRID_TIMER_INFO *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Save CRID timer setting
    /// @return none
    //-------------------------------------------------------------------------------------------------
    void SaveCRIDTimerSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// Use CRID timer list
    /// @param pValue \b OUT: return CRID timer info
    /// @param size \b OUT: return size
    /// @return none
    //-------------------------------------------------------------------------------------------------
    void UseCRIDTimerList(ST_CRID_TIMER_INFO **pValue, U32* size);

    //-------------------------------------------------------------------------------------------------
    /// Get the last Video Standard in database
    /// @param pValue \b IN: the pointer to the structure of MAPI_AVD_VideoStandardType
    /// @param pParam \b IN: the pointer to the return Value. True: Enable, False: Disable
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetLastVideoStandard(MAPI_AVD_VideoStandardType *pValue, MAPI_INPUT_SOURCE_TYPE *pParam);

    //-------------------------------------------------------------------------------------------------
    /// Get last AudioStandard in database
    /// @param pValue \b IN: the pointer to the structure of AUDIOMODE_TYPE_
    /// @param pParam \b IN: the pointer to the input source
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetLastAudioStandard(AUDIOMODE_TYPE_ *pValue, MAPI_INPUT_SOURCE_TYPE *pParam);

    //-------------------------------------------------------------------------------------------------
    /// Get the DC on/off status
    /// @return TURE: DC Power Off enable
    /// @return FALSE: DC Power Off disable
    //-------------------------------------------------------------------------------------------------
    virtual bool GetDcPoweroffMode();

    //-------------------------------------------------------------------------------------------------
    /// Get the Location Setting,
    /// @param pValue \b IN: the pointer to the structure of MS_USER_LOCATION_SETTING
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetLocationSetting(MS_USER_LOCATION_SETTING *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Get the Sound Setting
    /// @param pValue \b IN: the pointer to the structure of MS_USER_SOUND_SETTING
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetSoundSetting(MS_USER_SOUND_SETTING *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Get the Medium Setting
    /// @param pValue \b IN: the pointer to the structure of ST_MEDIUM_SETTING
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetMediumSetting(ST_MEDIUM_SETTING *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Get the Network Setting
    /// @param pValue \b IN: the pointer to the structure of ST_NETWORK_SETTING
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetNetworkSetting(ST_NETWORK_SETTING *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Get the Overscan Setting by input source
    /// @param pValue       \b IN(OUT): the pointer to the structure of ST_MAPI_VIDEO_WINDOW_INFO
    /// @param Res          \b IN: the resolution
    /// @param eARCType     \b IN: the video ARC type
    /// @param eInputType   \b IN: input source type
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetOverscanSetting(ST_MAPI_VIDEO_WINDOW_INFO *pValue, U32 Res, mapi_video_datatype::MAPI_VIDEO_ARC_Type eARCType, MAPI_INPUT_SOURCE_TYPE eInputType);

#if (PEQ_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Get PEQ Setting
    /// @param pValue       \b IN(OUT): the pointer to the structure of AUDIO_PEQ_PARAM
    /// @param u8BandIndex  \b IN: the band number
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetPEQSetting(void *pValue, U8 u8BandIndex);

    //-------------------------------------------------------------------------------------------------
    /// Set PEQ Setting
    /// @param pValue       \b IN: the pointer to the structure of AUDIO_PEQ_PARAM
    /// @param u8BandIndex  \b IN: the band number
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetPEQSetting(AUDIO_PEQ_PARAM *pValue, U8 u8BandIndex);
#endif

#if (CI_ENABLE == 1)
#if (CI_PLUS_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Set Credential Mode
    /// @param enCredentialMode \b IN: Credential Mode
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetCredentialMode(MAPI_U8 enCredentialMode);

    //-------------------------------------------------------------------------------------------------
    /// Get Credential Mode
    /// @return MAPI_U8: Credential Mode
    //-------------------------------------------------------------------------------------------------
    virtual MAPI_U8 GetCredentialMode(void);
#endif
    //-------------------------------------------------------------------------------------------------
    /// Set Performance Monitor
    /// @param bEnable \b IN: enable/disable
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetPerformanceMonitor(MAPI_BOOL bEnable);

    //-------------------------------------------------------------------------------------------------
    /// Get Performance Monitor
    /// @return TURE: Performance Monitor enable
    /// @return FALSE: Performance Monitor disable
    //-------------------------------------------------------------------------------------------------
    virtual MAPI_BOOL GetPerformanceMonitor(void);

    //-------------------------------------------------------------------------------------------------
    /// Set CI Debug Level
    /// @param enDebugIndex \b IN: target Debug Index
    /// @param u8DebugLevel \b IN: target Debug Level
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetCIDebugLevel(EN_CI_FUNCTION enDebugIndex, MAPI_U8 u8DebugLevel);

    //-------------------------------------------------------------------------------------------------
    /// Get CI Debug Level
    /// @param enDebugIndex \b IN: target Debug Index
    /// @return MAPI_U8: target Debug Level
    //-------------------------------------------------------------------------------------------------
    virtual MAPI_U8 GetCIDebugLevel(EN_CI_FUNCTION enDebugIndex);
#endif

    //-------------------------------------------------------------------------------------------------
    /// Set the Overscan Setting by input source
    /// @param pValue \b IN: the pointer to the structure of ST_MAPI_VIDEO_WINDOW_INFO
    /// @param Res \b IN: the resolution
    /// @param eARCType \b IN: the video ARC type
    /// @param eInputType             \b IN: input source type
    //-------------------------------------------------------------------------------------------------
    virtual void SetOverscanSetting(ST_MAPI_VIDEO_WINDOW_INFO *pValue, U32 Res, mapi_video_datatype::MAPI_VIDEO_ARC_Type eARCType, MAPI_INPUT_SOURCE_TYPE eInputType);

#if (OAD_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Get the OAD Info, StartTime, EndTime,MonitorState,ServiceInfoCheckStatus,...
    /// @param pValue \b IN: the pointer to the structure of MW_OAD_INFORMATION
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetOADInfo(U8 *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Get OAD Wake up Info
    /// @param pValue \b IN: the pointer to the structure of m_stOADWakeUpInfo
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetOADWakeUpInfo(U8 *pValue);
#endif

    //-------------------------------------------------------------------------------------------------
    /// Set the program block status
    /// @param bValue \b IN: The pointer to the variable which you want to set
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetSystemLock(BOOL bValue);

    //-------------------------------------------------------------------------------------------------
    /// Set the status of ParentalControl setting
    /// @param pValue \b IN: the pointer to structure of MS_BLOCKSYS_SETTING
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetParentalControl(U8 *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Set the status of ParentalObjectiveContent setting
    /// @param pValue \b IN: the pointer to structure of MS_BLOCKSYS_SETTING
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetParentalObjectiveContent(U8 *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Set the password in database for Block Program
    /// @param pValue \b IN: the pointer to variable of password
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetPassword(U16 *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Set System Country
    /// @param pValue \b IN: the pointer to structure of MEMBER_COUNTRY
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetSystemCountry(MEMBER_COUNTRY *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Set System Cable Operators
    /// @param pValue \b IN: the pointer to structure of enCableOperators
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetSystemCableOperators(EN_CABLE_OPERATORS *pValue);


    //TVOS: will be deleted
    //-------------------------------------------------------------------------------------------------
    /// Set System SI auto update off or not
    /// @param bValue \b IN: the SI auto update setting value
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetDisableSiAutoUpdate(BOOL bValue);

    //-------------------------------------------------------------------------------------------------
    /// Set DTV Network ID Field
    /// @param u16NetworkIdValue \b IN: DTV Network ID Field
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetSystemNetworkId(U16 u16NetworkIdValue);

    //-------------------------------------------------------------------------------------------------
    /// Set OSD Language Setting
    /// @param pValue \b IN: the pointer to structure of MEMBER_LANGUAGE
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetOsdLanguage(MEMBER_LANGUAGE *pValue);

#if (FREEVIEW_AU_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Set last OAD version
    /// @param pValue \b IN: the pointer to the last OAD version
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetLastOADVersion(U16 *pValue);
#endif

#if (CA_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// SetFeatureConfigBitmap
    /// @param pValue \b IN: set feature config bitmap
    /// @return BOOL \b OUT: TRUE: success, FALSE: failure
    //-------------------------------------------------------------------------------------------------
    BOOL SetFeatureConfigBitmap(U32 *pValue);

    //-------------------------------------------------------------------------------------------------
    /// SetFeatureConfigExtendBitmap
    /// @param pValue \b IN: set feature config extend bitmap
    /// @return BOOL \b OUT: TRUE: success, FALSE: failure
    //-------------------------------------------------------------------------------------------------
    BOOL SetFeatureConfigExtendBitmap(U32 *pValue);

    //-------------------------------------------------------------------------------------------------
    /// SetDebitLimitThreshold
    /// @param pValue \b IN: set debit limit threshold
    /// @return BOOL \b OUT: TRUE: success, FALSE: failure
    //-------------------------------------------------------------------------------------------------
    BOOL SetDebitLimitThreshold(U8 *pValue);

    //-------------------------------------------------------------------------------------------------
    /// SetIppvCallbackInfo
    /// @param pValue \b IN: set IPPV callback info
    /// @return BOOL \b OUT: TRUE: success, FALSE: failure
    //-------------------------------------------------------------------------------------------------
    BOOL SetIppvCallbackInfo(MS_IPPV_CALLBACK_INFO *pValue);

    //-------------------------------------------------------------------------------------------------
    /// SetDataMiningParam
    /// @param pValue \b IN: set data mining param
    /// @return BOOL \b OUT: TRUE: success, FALSE: failure
    //-------------------------------------------------------------------------------------------------
    BOOL SetDataMiningParam(MS_DATA_MINING_PARAM *pValue);

    //-------------------------------------------------------------------------------------------------
    /// SetForceStatusReport
    /// @param pValue \b IN: set force status report
    /// @return BOOL \b OUT: TRUE: success, FALSE: failure
    //-------------------------------------------------------------------------------------------------
    BOOL SetForceStatusReport(MS_FORCE_STATUS_REPORT *pValue);

    //-------------------------------------------------------------------------------------------------
    /// SetDownloadType
    /// @param pValue \b IN: set download type
    /// @return BOOL \b OUT: TRUE: success, FALSE: failure
    //-------------------------------------------------------------------------------------------------
    BOOL SetDownloadType(MS_DOWNLOAD_TYPE *pValue);

    //-------------------------------------------------------------------------------------------------
    /// SetIRDCmdSequenceNum
    /// @param pValue \b IN: set IRD command sequence number
    /// @return BOOL \b OUT: TRUE: success, FALSE: failure
    //-------------------------------------------------------------------------------------------------
    BOOL SetIRDCmdSequenceNum(U32 *pValue);

    //-------------------------------------------------------------------------------------------------
    /// SetCaPinCodeId
    /// @param pValue \b IN: set CA pin code ID
    /// @return BOOL \b OUT: TRUE: success, FALSE: failure
    //-------------------------------------------------------------------------------------------------
    BOOL SetCaPinCodeId(U32 *pValue);

    //-------------------------------------------------------------------------------------------------
    /// SetCaBouquetId
    /// @param pValue \b IN: set CA bouquet ID
    /// @return BOOL \b OUT: TRUE: success, FALSE: failure
    //-------------------------------------------------------------------------------------------------
    BOOL SetCaBouquetId(U16 *pValue);

    //-------------------------------------------------------------------------------------------------
    /// SetZoneId
    /// @param pValue \b IN: set zone ID
    /// @return BOOL \b OUT: TRUE: success, FALSE: failure
    //-------------------------------------------------------------------------------------------------
    BOOL SetZoneId(U8 *pValue);

    //-------------------------------------------------------------------------------------------------
    /// SetCustomerServicePhone
    /// @param pValue \b IN: set customer service phone
    /// @return BOOL \b OUT: TRUE: success, FALSE: failure
    //-------------------------------------------------------------------------------------------------
    BOOL SetCustomerServicePhone(U8 *pValue);

    //-------------------------------------------------------------------------------------------------
    /// SetPPVOrderPhone
    /// @param pValue \b IN: set PPV order phone
    /// @return BOOL \b OUT: TRUE: success, FALSE: failure
    //-------------------------------------------------------------------------------------------------
    BOOL SetPPVOrderPhone(U8 *pValue);

    //-------------------------------------------------------------------------------------------------
    /// SetCaTextMessage
    /// @param u8Index    \b IN: index for query
    /// @param pValue    \b IN: set CA text message
    /// @return BOOL \b OUT: TRUE: success, FALSE: failure
    //-------------------------------------------------------------------------------------------------
    BOOL SetCaTextMessage(U8 u8Index, MS_CA_TEXT_MESSAGE *pValue);

    //-------------------------------------------------------------------------------------------------
    /// SetCaAttrDisplayMessage
    /// @param pValue    \b IN: set CA attr display message
    /// @return BOOL \b OUT: TRUE: success, FALSE: failure
    //-------------------------------------------------------------------------------------------------
    BOOL SetCaAttrDisplayMessage(MS_CA_ATTR_DISPLAY_MESSAGE *pValue);
#endif

    //-------------------------------------------------------------------------------------------------
    /// Set DTV Route Status
    /// @param u8DtvRoute \b IN: DTV Route Status
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetDtvRoute(U8 u8DtvRoute);

    //-------------------------------------------------------------------------------------------------
    /// Set the OSD Time
    /// @param pValue \b IN: the pointer to structure of MS_TIME
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetOsdTime(MS_TIME *pValue);

    //TVOS: will be deleted
    //-------------------------------------------------------------------------------------------------
    /// Set Video Info
    /// @param pValue \b IN: the pointer to the structure of T_MS_VIDEO
    /// @param pParam \b IN: the pointer to the Input Source
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetVideo(T_MS_VIDEO *pValue, MAPI_INPUT_SOURCE_TYPE *pParam);

    //-------------------------------------------------------------------------------------------------
    /// Set Video Info
    /// @param pValue \b IN: the pointer to the structure of T_MS_VIDEO
    /// @param pParam \b IN: the pointer to the Input Source
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetVideoSetting(T_MS_VIDEO *pValue, MAPI_INPUT_SOURCE_TYPE *pParam);

    //-------------------------------------------------------------------------------------------------
    /// Set the ADC Value in Database
    /// @param pValue \b IN: the pointer to the structure of mapi_pql_datatype::MAPI_PQL_CALIBRATION_DATA
    /// @param pParam \b IN: the pointer to the Input Source
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetVideoAdc(MAPI_PQL_CALIBRATION_DATA *pValue, void *pParam);

    //TVOS: will be deleted
    //-------------------------------------------------------------------------------------------------
    /// Set the current ADC Auto Tune Status
    /// @param pValue \b IN: the pointer to the Bool Variable.(To Set this value)
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetVideoAdcAutoTune(BOOL *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Set the current ADC Auto Tune Status
    /// @param pValue \b IN: the pointer to the Bool Variable.(To Set this value)
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetVideoAdcAutoTuneEnable(BOOL *pValue);

    //TVOS: will be deleted
    //-------------------------------------------------------------------------------------------------
    /// Set Aging Mode
    /// @param pValue \b IN: the pointer to the Bool Variable.(To Set this value)
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetAgingMode(BOOL *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Set Aging Mode
    /// @param pValue \b IN: the pointer to the Bool Variable.(To Set this value)
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetAgingModeEnable(BOOL *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Set the Color Tempurature
    /// @param pValue \b IN: the pointer to the data structure mapi_pql_datatype::MAPI_PQL_COLOR_TEMP_DATA
    /// @param pParam \b IN: the pointer to the InputSource
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetColorTemp(mapi_pql_datatype::MAPI_PQL_COLOR_TEMP_DATA *pValue, void *pParam);

    //-------------------------------------------------------------------------------------------------
    /// Set the Ex Color Tempurature
    /// @param pValue \b IN: the pointer to the data structure mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA
    /// @param pParam \b IN: the pointer to the InputSource
    /// @param eCurrentInputType \b IN: InputSource type
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetColorTempEx(mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA *pValue, void *pParam, MAPI_INPUT_SOURCE_TYPE  eCurrentInputType);

    //-------------------------------------------------------------------------------------------------
    /// Set the Video Arc info
    /// @param pValue \b IN: mapi_video_datatype::MAPI_VIDEO_ARC_Type
    /// @param pParam \b IN: the pointer to the InputSource
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetVideoArc(mapi_video_datatype::MAPI_VIDEO_ARC_Type *pValue, MAPI_INPUT_SOURCE_TYPE *pParam);

    //-------------------------------------------------------------------------------------------------
    /// Set the Favorite Type Name for DVBS2 multi-favorite list
    /// @param u8FavType \b IN: 0~7
    /// @param pValue \b IN: the pointer to the return values
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetFavTypeName(U8 u8FavType,U8 *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Set the System Setting
    /// @param pValue \b IN: the pointer to the structure of MS_USER_SYSTEM_SETTING
    /// @param pOrgValue \b IN: the pointer to the structure of MS_USER_SYSTEM_SETTING only for SQL , if pOrgValue = NULL: Set All the data to SQL
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetUserSystemSetting(MS_USER_SYSTEM_SETTING *pValue, MS_USER_SYSTEM_SETTING *pOrgValue = NULL);

    //TVOS: will be deleted
    //-------------------------------------------------------------------------------------------------
    /// Set the Audio info
    /// @param pValue \b IN: the pointer to the structure of MS_USER_SOUND_SETTING
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetAudio(MS_USER_SOUND_SETTING *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Set the Audio info
    /// @param pValue \b IN: the pointer to the structure of MS_USER_SOUND_SETTING
    /// @param pOrgValue \b IN: the pointer to the structure of MS_USER_SOUND_SETTING only for SQL , if pOrgValue = NULL: Set All the data to SQL
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetAudioSetting(MS_USER_SOUND_SETTING *pValue, MS_USER_SOUND_SETTING *pOrgValue = NULL);

    //TVOS: will be deleted
    //-------------------------------------------------------------------------------------------------
    /// Set the Installation Flag (First time Boot Up => True)
    /// @param pValue \b IN: the pointer to the return Value. True: OK, False: Fail
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetInstallationFlag(BOOL *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Set the Installation Flag (First time Boot Up => True)
    /// @param pValue \b IN: the pointer to the return Value. True: OK, False: Fail
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetInstallationguideEnabled(BOOL *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Set No Channel Flag
    /// @param pValue \b IN: the pointer to the return Value. True: No Channel, False: Channel Exist
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetNoChannelFlag(BOOL *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Set the Audio Language Type
    /// @param pValue \b IN: the pointer to the structure of MEMBER_LANGUAGE
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetAudioLanguage1(MEMBER_LANGUAGE *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Set the Audio Language2
    /// @param pValue \b IN: the pointer to the structure of MEMBER_LANGUAGE
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetAudioLanguage2(MEMBER_LANGUAGE *pValue);

    //TVOS: will be deleted
    //-------------------------------------------------------------------------------------------------
    /// Set the Audio Enable Status
    /// @param pValue \b IN: the pointer to the return Value. True: Enable, False: Disable
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetAudioAdEnable(BOOLEAN *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Set the Audio Enable Status
    /// @param pValue \b IN: the pointer to the return Value. True: Enable, False: Disable
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetAudioAdEnabled(BOOLEAN *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Set the PC Mode Table
    /// @param pValue \b IN: the pointer to the structure of ST_PCADC_USER_MODE_SETTING
    /// @param pParam \b IN: the pointer to the input source
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetPcModeTable(ST_PCADC_USER_MODE_SETTING *pValue, void *pParam);

    //-------------------------------------------------------------------------------------------------
    /// Set EPG Timer
    /// @param pValue \b IN: the pointer to the structure of ST_EPG_EVENT_TIMER_INFO
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetEpgTimer(ST_EPG_EVENT_TIMER_INFO *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Get the GetPip Status of variable m_stPipSetting
    /// @param pValue \b IN: the pointer to the return Value. True: Enable, False: Disable
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetPip(U8 *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Set the status of variable m_stPipSetting
    /// @param pValue \b IN: the pointer to the return Value. True: Enable, False: Disable
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetPip(U8 *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Set the auto channel update enable or not
    /// @param pValue \b IN: the pointer to the return Value. True: Enable, False: Disable
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetAutoChannelUpdate(BOOL *pValue);
    //-------------------------------------------------------------------------------------------------
    /// Set the Dynamic Rescan enable or not
    /// @param bValue \b IN: True: Disable, False: Enable
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetDynamicRescanOff(BOOL bValue);

    //-------------------------------------------------------------------------------------------------
    /// Set the last Video Standard in database
    /// @param pValue \b IN: the pointer to the structure of MAPI_AVD_VideoStandardType
    /// @param pParam \b IN: the pointer to the return Value. True: Enable, False: Disable
    /// @return TURE: Success
    /// @return FALSE: Fail
    //-------------------------------------------------------------------------------------------------
    virtual bool SetLastVideoStandard(MAPI_AVD_VideoStandardType *pValue, MAPI_INPUT_SOURCE_TYPE *pParam);

    //-------------------------------------------------------------------------------------------------
    /// Set last AudioStandard in database
    /// @param pValue \b IN: the pointer to the structure of AUDIOMODE_TYPE_
    /// @param pParam \b IN: the pointer to the input source
    /// @return TURE: Success
    /// @return FALSE: Fail
    //-------------------------------------------------------------------------------------------------
    virtual bool SetLastAudioStandard(AUDIOMODE_TYPE_ *pValue, MAPI_INPUT_SOURCE_TYPE *pParam);

    //-------------------------------------------------------------------------------------------------
    /// Set the DC on/off status
    /// @param bValue \b IN: the bool of setting value
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetDcPoweroffMode(bool bValue);

    //-------------------------------------------------------------------------------------------------
    /// Set the Location Setting,
    /// @param pValue \b IN: the pointer to the structure of MS_USER_LOCATION_SETTING
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetLocationSetting(MS_USER_LOCATION_SETTING *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Set User Source Type
    /// @param pInputsource \b input source
    //-------------------------------------------------------------------------------------------------
    virtual void SetUserSourceType(MAPI_INPUT_SOURCE_TYPE* pInputsource);

#if (STEREO_3D_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Set 3D Setting
    /// @param pst3DSetting \b 3D Setting
    //-------------------------------------------------------------------------------------------------
    virtual void Set3DSetting(MS_3D_SETTING *pst3DSetting);
#endif

    //-------------------------------------------------------------------------------------------------
    /// set the  Medium Setting
    /// @param pValue \b IN: the pointer to the structure of ST_MEDIUM_SETTING
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetMediumSetting(ST_MEDIUM_SETTING *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Set the Network Setting
    /// @param pValue \b IN: the pointer to the structure of ST_NETWORK_SETTING
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetNetworkSetting(ST_NETWORK_SETTING *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Set the Software update enable/disable
    /// @param pValue \b IN: the pointer to control value
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetSoftwareUpdate(U8 *pValue);

#if (OAD_ENABLE == 1)

    //-------------------------------------------------------------------------------------------------
    /// Set the OAD Info, StartTime, EndTime,MonitorState,ServiceInfoCheckStatus,...
    /// @param pValue \b IN: the pointer to the structure of MW_OAD_INFORMATION
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetOADInfo(U8 *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Set OAD Wake up Info
    /// @param pValue \b IN: the pointer to the structure of m_stOADWakeUpInfo
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetOADWakeUpInfo(U8 *pValue);
#endif

    //-------------------------------------------------------------------------------------------------
    /// Set Boot Setting Info
    /// @param value    \b IN: The structure of MS_BOOT_SETTING
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetBootSetting(MS_BOOT_SETTING *value);

    //-------------------------------------------------------------------------------------------------
    /// Get Boot Setting Info
    /// @param value    \b IN: the pointer to the structure of MS_BOOT_SETTING
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetBootSetting(MS_BOOT_SETTING *value);

    //-------------------------------------------------------------------------------------------------
    /// save the general setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SaveGenSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// save the setting of EPG Timer
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SaveEPGTimerSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// get factory NLA settings
    /// @param pValue \b IN: the pointer to the structure of NLA items
    /// @param eIndex \b IN: the index to indicate which item of NLA setting is choosen
    /// @param pParam \b IN: the input to indicate which source of item of NLA setting is choosen
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetNLASetting(MS_NLA_POINT *pValue, MS_NLA_SET_INDEX eIndex, MAPI_INPUT_SOURCE_TYPE pParam);

    //TVOS: will be deleted
    //-------------------------------------------------------------------------------------------------
    /// get factory extended settings
    /// @param pValue \b IN: the pointer to the structure of different extension items
    /// @param eFEindex \b IN: the index to indicate which item of extended setting is choosen
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetFactoryExt_Setting(void *pValue, MS_FACTORY_EXT_ITEM_INDEX eFEindex);

    //-------------------------------------------------------------------------------------------------
    /// get factory extended settings
    /// @param pValue \b IN: the pointer to the structure of different extension items
    /// @param eFEindex \b IN: the index to indicate which item of extended setting is choosen
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetFactoryExtSetting(void *pValue, MS_FACTORY_EXT_ITEM_INDEX eFEindex);

    //-------------------------------------------------------------------------------------------------
    /// set factory NLA settings
    /// @param pValue \b IN: the pointer to the structure of NLA items
    /// @param eIndex \b IN: the index to indicate which item of NLA setting is chosen
    /// @param pParam \b IN: the input to indicate which source of item of NLA setting is chosen
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetNLASetting(MS_NLA_POINT *pValue, MS_NLA_SET_INDEX eIndex, MAPI_INPUT_SOURCE_TYPE pParam);

    //TVOS: will be deleted
    //-------------------------------------------------------------------------------------------------
    /// set factory extended settings
    /// @param pValue \b IN: the pointer to the structure of different extension items
    /// @param eFEindex \b IN: the index to indicate which item of extended setting is chosen
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetFactoryExt_Setting(void *pValue, MS_FACTORY_EXT_ITEM_INDEX eFEindex);

    //-------------------------------------------------------------------------------------------------
    /// set factory extended settings
    /// @param pValue \b IN: the pointer to the structure of different extension items
    /// @param eFEindex \b IN: the index to indicate which item of extended setting is chosen
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetFactoryExtSetting(void *pValue, MS_FACTORY_EXT_ITEM_INDEX eFEindex);

    //-------------------------------------------------------------------------------------------------
    /// initial the setting of EPG Timer
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitEPGTimerSetting(void);

#if (STEREO_3D_ENABLE == 1 && SQL_DB_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// load the setting of 3d video router setting
    /// @param eThreeDVideoRouter         \b IN: Specify the 3d video router setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void Get3DVideoRouterSetting(MS_3D_CONVERTRULE eThreeDVideoRouter[EN_3D_TYPE_NUM]);
    //-------------------------------------------------------------------------------------------------
    /// save the setting of 3d video router setting
    /// @param eThreeDVideoRouter         \b IN: Specify the 3d video router setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void Set3DVideoRouterSetting(MS_3D_CONVERTRULE eThreeDVideoRouter[EN_3D_TYPE_NUM]);
    //-------------------------------------------------------------------------------------------------
    /// load the setting of 3d to 2d video router setting
    /// @param e3DTo2DVideoRouter         \b IN: Specify the 3d to 2d video router setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void Get3DTo2DVideoRouterSetting(MS_3D_CONVERTRULE e3DTo2DVideoRouter[EN_3D_TYPE_NUM]);
    //-------------------------------------------------------------------------------------------------
    /// load the setting of 4K2K 3d video router setting
    /// @param e4K2K3DVideoRouter         \b IN: Specify the 4K2K 3d video router setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void Get4K2K3DVideoRouterSetting(MS_3D_CONVERTRULE e4K2K3DVideoRouter[EN_3D_TYPE_NUM]);
    //-------------------------------------------------------------------------------------------------
    /// load the setting of 4K2K 60Hz 3d video router setting
    /// @param e4K2K60Hz3DVideoRouter         \b IN: Specify the 4K2K 3d video router setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void Get4K2K60Hz3DVideoRouterSetting(MS_3D_CONVERTRULE e4K2K60Hz3DVideoRouter[EN_3D_TYPE_NUM]);
    //-------------------------------------------------------------------------------------------------
    /// load the setting of osd display mode router setting
    /// @param eDisplayModeRouter         \b IN: Specify the 4K2K 3d video router setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetDisplayModeRouterSetting(MS_3D_DISPLAYMODE_CONVERTRULE (&eDisplayModeRouter)[EN_3D_TYPE_NUM]);
#endif

#if(SQL_DB_ENABLE == 1 && MSTAR_TVOS == 1)
    //-------------------------------------------------------------------------------------------------
    /// Check This table is updated by Supernova.
    /// @param type: index of db table
    /// @return TRUE: native db is dirty
    /// @return FALSE: native db is normal.
    //-------------------------------------------------------------------------------------------------
    virtual BOOL IsNativeDbDirty(U8 index);

    //-------------------------------------------------------------------------------------------------
    /// Clear dirty flag of this database table.
    /// @param type: index of db table
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void ClearNativeDbDirty(U8 index);

    //-------------------------------------------------------------------------------------------------
    /// Set dirty flag indicate this database table be updated by android app.
    /// @param type: index of db table
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetJavaApDbDirty(U8 index);
#endif

#if (MSTAR_TVOS == 1)
    //-------------------------------------------------------------------------------------------------
    /// Get atv program count.
    /// @param pValue         \b IN: atv program counts
    /// @param u8CityIndex         \b IN: Specify the index of city
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetDefaultAtvProgramCounts(U8* pValue, U8 u8CityIndex);

    //-------------------------------------------------------------------------------------------------
    /// Get default atv program.
    /// @param pValue         \b IN: ATV program setting
    /// @param u8CityIndex         \b IN: Specify the index of city
    /// @param u8ProgramIndex         \b IN: Specify the index of program
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetDefaultAtvProgram(MS_FACTORY_DEFAULT_ATV_SETTING *pValue, U8 u8CityIndex, U8 u8ProgramIndex);

    //-------------------------------------------------------------------------------------------------
    /// Get dtv program count.
    /// @param pValue         \b IN: atv program counts
    /// @param u8CityIndex         \b IN: Specify the index of city
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetDefaultDtvProgramCounts(U8* pValue, U8 u8CityIndex);

    //-------------------------------------------------------------------------------------------------
    /// Get default dtv program.
    /// @param pValue         \b IN: DTV program setting
    /// @param u8CityIndex         \b IN: Specify the index of city
    /// @param u8ProgramIndex         \b IN: Specify the index of program
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetDefaultDtvProgram(MS_FACTORY_DEFAULT_DTV_SETTING *pValue, U8 u8CityIndex, U8 u8ProgramIndex);
#endif

#if(SQL_DB_ENABLE == 1 && MSTAR_TVOS == 1)
    //-------------------------------------------------------------------------------------------------
    /// Check HDMI auto 3D enable or not.
    /// @return TRUE: HDMI auto 3D enable
    /// @return FALSE: HDMI auto 3D disable
    //-------------------------------------------------------------------------------------------------
    virtual BOOL IsHDMIAuto3DEnable(void);
#endif

    //-------------------------------------------------------------------------------------------------
    /// get factory audio setting
    /// @param pValue: data struct of factory audio setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetFactoryAudioSetting(ST_FACTORY_AUDIO_SETTING *pValue);
    //-------------------------------------------------------------------------------------------------
    /// set factory audio setting
    /// @param pValue: data struct of factory audio setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetFactoryAudioSetting(ST_FACTORY_AUDIO_SETTING *pValue);
    //-------------------------------------------------------------------------------------------------
    /// load the setting of videosetting
    /// @param u8Index         \b IN: Specify the input source enable index
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadVideoSetting(U8 u8Index);
    //-------------------------------------------------------------------------------------------------
    /// load the setting of NLA
    /// @param u8Index
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadNLASetting(U8 u8Index);

    /// get nit frequency by dtv region
    /// @param eRegion         \b IN: Specify the region
    /// @param u32NITFreq    \b OUT: u32NITFreq
    /// @param u32NITFreq2    \b OUT: u32NITFreq2
    /// @return TRUE: success
    /// @return FALSE: fail
    //-------------------------------------------------------------------------------------------------
    virtual bool GetNITFreqByDTVRegion(E_CHINA_DVBCREGION eRegion,U32 &u32NITFreq, U32 &u32NITFreq2);

    //-------------------------------------------------------------------------------------------------
    /// get china dvbc region
    /// @return E_CHINA_DVBCREGION : DVBC region in China
    //-------------------------------------------------------------------------------------------------
    virtual E_CHINA_DVBCREGION getChinaDVBCRegion();

    //-------------------------------------------------------------------------------------------------
    /// set china dvbc region
    /// @param eRegion         \b region index
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void setChinaDVBCRegion(U8 eRegion);

    //-------------------------------------------------------------------------------------------------
    /// Get Current Satellite Platform
    /// @return EN_SATELLITE_PLATFORM: System Satellite Platform
    //-------------------------------------------------------------------------------------------------
    virtual EN_SATELLITE_PLATFORM GetSatellitePlatform(void);

    //-------------------------------------------------------------------------------------------------
    /// Set Local Dimm Mode
    /// @param pValue \b IN: the pointer to enum of enLocalDimm
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetLocalDimmMode(EN_LOCAL_DIMM_MODE *pValue);

#if (STEREO_3D_ENABLE == 1)
    //3D NLA
    //-------------------------------------------------------------------------------------------------
    /// initial the setting of No-liner-adjust under 3D
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitNLASetting3D(void);

    //-------------------------------------------------------------------------------------------------
    /// save the setting of NLA under 3D
    /// @param u8Index
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SaveNLASetting3D(U8 u8Index);

    //-------------------------------------------------------------------------------------------------
    /// restore the setting of NLA to default under 3D
    /// @param u8Index
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultNLA3D(U8 u8Index);

    //-------------------------------------------------------------------------------------------------
    /// get factory NLA settings under 3D
    /// @param pValue \b IN: the pointer to the structure of NLA items
    /// @param eIndex \b IN: the index to indicate which item of NLA setting is choosen
    /// @param pParam \b IN: the input to indicate which source of item of NLA setting is choosen
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetNLASetting3D(MS_NLA_POINT *pValue, MS_NLA_SET_INDEX eIndex, MAPI_INPUT_SOURCE_TYPE pParam);

    //-------------------------------------------------------------------------------------------------
    /// load the setting of NLA under 3D
    /// @param u8Index
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadNLASetting3D(U8 u8Index);

    //-------------------------------------------------------------------------------------------------
    /// Initial the setting of color temperatue in 16 bit under 3D mode
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitFactoryColorTempEx3D(void);

    //-------------------------------------------------------------------------------------------------
    /// restore the setting of color temperatue to default in 16 bit under 3D mode
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultColorTempEx3D(void);

    //-------------------------------------------------------------------------------------------------
    /// save the setting of color temperatue in 16 bit under 3D mode
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SaveColorTempEx3D(void);

    //-------------------------------------------------------------------------------------------------
    /// set the color tempex data in factory mode under 3D mode
    /// @param enColorTempIdx         \b IN: Specify the database color tempex array index1
    /// @param enInputSourceTypeIdx   \b IN: Specify the database color tempex array index2
    /// @param stColorTempex            \b IN: Specify the color tempex data to the database
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetFactoryColorTempEx3D(const MSrv_Picture::EN_MS_COLOR_TEMP enColorTempIdx, const EN_MS_INPUT_SOURCE_TYPE enInputSourceTypeIdx, const mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA &stColorTempex);

    //-------------------------------------------------------------------------------------------------
    /// load the setting of color temperatue in 16 bit under 3D mode
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadFactoryColorTempEx3D(void);

    //-------------------------------------------------------------------------------------------------
    /// Get the Color Tempurature under 3D mode
    /// @param pValue \b IN: the pointer to the data structure mapi_pql_datatype::MAPI_PQL_COLOR_TEMP_DATA
    /// @param pParam \b IN: the pointer to the InputSource
    /// @param enSrcType \b IN:InputSource type
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetColorTempEx3D(mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA *pValue, void *pParam, MAPI_INPUT_SOURCE_TYPE enSrcType);

#endif

#if(HBBTV_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Set HBBTV  enable or not.
    /// @param pValue \b IN/OUT: the pointer to the Bool Variable.
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetHbbtvEnable(BOOLEAN *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Check HBBTV  enable or not.
    /// @param pValue \b IN/OUT: the pointer to the Bool Variable.
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void GetHbbtvEnable(BOOLEAN *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Check HBBTV  enable or not.
    /// @param pValue \b IN/OUT: the pointer to the Bool Variable.
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void IsHbbtvEnable(BOOLEAN *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Set StoreCookies enable or not.
    /// @param pValue \b IN/OUT: the pointer to the Bool Variable.
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetStoreCookiesEnable(BOOLEAN pValue);

    //-------------------------------------------------------------------------------------------------
    /// Check StoreCookies enable or not.
    /// @param pValue \b IN/OUT: the pointer to the Bool Variable.
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void GetStoreCookiesEnable(BOOLEAN *pValue);

#endif

    //-------------------------------------------------------------------------------------------------
    /// Set ACR  enable or not.
    /// @param pValue \b IN/OUT: the pointer to the Bool Variable.
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetACREnable(BOOLEAN *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Check ACR  enable or not.
    /// @param pValue \b IN/OUT: the pointer to the Bool Variable.
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void IsACREnable(BOOLEAN *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Set service list rearrange status. (For "KDG TD 0017 Digital TV Receiver Analysis", Version 1.2.1, 5.3. Bouquet and Service Arrangement Update)
    /// @param bEnable              \b IN: TRUE: enable, FALSE: disable
    /// @return none
    //-------------------------------------------------------------------------------------------------
    void SetServiceListRearrangeStatus(BOOL bEnable);

    //-------------------------------------------------------------------------------------------------
    /// Get service list rearrange status. (For "KDG TD 0017 Digital TV Receiver Analysis", Version 1.2.1, 5.3. Bouquet and Service Arrangement Update)
    /// @return              \b TRUE: need to rearrange, FALSE: no need to rearrange
    //-------------------------------------------------------------------------------------------------
    BOOL GetServiceListRearrangeStatus(void);

    //-------------------------------------------------------------------------------------------------
    /// Get Channel Switch Mode. (black screen or Mirroring)
    /// @return              \b TRUE: Mirroring, FALSE: black screen
    //-------------------------------------------------------------------------------------------------
    virtual BOOL GetChannelSwitchMode();

    //-------------------------------------------------------------------------------------------------
    /// Get Current Hdmi Edid Version
    /// @return U16: Current Hdmi Edid Version
    //-------------------------------------------------------------------------------------------------
    virtual U16 GetHdmiEdidVersion();

#if (INPUT_SOURCE_LOCK_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Get the setting of input source lock
    /// @param pValue: the pointer to the structure of ST_INPUT_SOURCE_LOCK_SETTING, about the setting of input source lock
    /// @return MAPI_TRUE: get input source lock setting success
    /// @return MAPI_FALSE: get input source lock setting fail
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL GetInputSourceLockSetting(ST_INPUT_SOURCE_LOCK_SETTING *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Set the setting of input source lock
    /// @param pValue: the pointer to the structure of ST_INPUT_SOURCE_LOCK_SETTING, about the setting of input source lock
    /// @return MAPI_TRUE: get input source lock setting success
    /// @return MAPI_FALSE: get input source lock setting fail
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetInputSourceLockSetting(const ST_INPUT_SOURCE_LOCK_SETTING *pValue);
#endif

    //-------------------------------------------------------------------------------------------------
    /// Get the HdmiEdidInfo by index
    /// @param iHdmiEdidIndex    \b IN: HDMI EDID index
    /// @param pValue            \b OUT: the pointer to the structure of MS_HDMI_EDID_INFO
    /// @return                  \b MAPI_TRUE: Sucess, MAPI_FALSE: fail
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL GetHdmiEdidInfo(int iHdmiEdidIndex, MS_HDMI_EDID_INFO *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Set the HdmiEdidInfo by index
    /// @param iHdmiEdidIndex    \b IN: HDMI EDID index
    /// @param stValue           \b IN: the reference to the structure of MS_HDMI_EDID_INFO
    /// @return  none
    //-------------------------------------------------------------------------------------------------
    void SetHdmiEdidInfo(int iHdmiEdidIndex, const MS_HDMI_EDID_INFO &stValue);

protected:
    // protected functions

    //-------------------------------------------------------------------------------------------------
    /// read data from UserDatabase
    /// @param srcIndex         \b IN: Specify the index of src
    /// @param dstAddr         \b IN: Specify the address of buffer
    /// @param size         \b IN: Specify the size of buffer
    /// @return TURE: Success
    /// @return FALSE: Fail
    //-------------------------------------------------------------------------------------------------
    virtual BOOL ReadUserDatabase(U32 srcIndex, U8* dstAddr, U16 size);

    //-------------------------------------------------------------------------------------------------
    /// write data to UserDatabase
    /// @param dstIndex         \b IN: Specify the index of dst
    /// @param srcAddr         \b IN: Specify the address of buffer
    /// @param size         \b IN: Specify the size of buffer
    /// @return TURE: Success
    /// @return FALSE: Fail
    //-------------------------------------------------------------------------------------------------
    virtual BOOL WriteUserDatabase(U32 dstIndex, U8* srcAddr, U16 size);

    //-------------------------------------------------------------------------------------------------
    /// read data from FactoryDatabase
    /// @param srcIndex         \b IN: Specify the index of src
    /// @param dstAddr         \b IN: Specify the address of buffer
    /// @param size         \b IN: Specify the size of buffer
    /// @return TURE: Success
    /// @return FALSE: Fail
    //-------------------------------------------------------------------------------------------------
    virtual BOOL ReadFactoryDatabase(U32 srcIndex, U8* dstAddr, U16 size);

    //-------------------------------------------------------------------------------------------------
    /// write data to FactoryDatabase
    /// @param srcIndex         \b IN: Specify the index of src
    /// @param dstAddr         \b IN: Specify the address of buffer
    /// @param size         \b IN: Specify the size of buffer
    /// @return TURE: Success
    /// @return FALSE: Fail
    //-------------------------------------------------------------------------------------------------
    virtual BOOL WriteFactoryDatabase(U32 srcIndex, U8* dstAddr, U16 size);

    //-------------------------------------------------------------------------------------------------
    /// calculate the checksum
    /// @param *pBuf         \b IN: Specify the buffer
    /// @param u16BufLen         \b IN: Specify the length of buffer
    /// @return U16: value of checksum
    //-------------------------------------------------------------------------------------------------
    virtual U16 CalCheckSum(U8 *pBuf, U16 u16BufLen);

    //-------------------------------------------------------------------------------------------------
    /// restore the setting of SubColor setting to default
    /// @param u8Index         \b IN: Specify the InputSource enable index
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultSubColor(U8 u8Index);

    //-------------------------------------------------------------------------------------------------
    /// restore the value to default
    /// @param u16KeepSetting         \b IN: Specify the keep setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultValue(U16 u16KeepSetting);

    //-------------------------------------------------------------------------------------------------
    /// initialize the boot setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitBootSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// restore the value to default
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultBootSetting(void);
    //-------------------------------------------------------------------------------------------------
    /// Save the Boot Setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SaveBootSetting(void);
    //-------------------------------------------------------------------------------------------------
    /// Load Boot setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadBootSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// load the general setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadGenSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// initial the setting of pip
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitPipSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// load the setting of pip
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadPipSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// save the setting of pip
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SavePipSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// restore the setting of pip to default
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultPIP(void);

    //-------------------------------------------------------------------------------------------------
    /// initial the setting of system setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitSysSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// load the setting of system setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadSysSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// save the setting of system setting
    /// @param pOrgValue \b IN: the pointer to the structure of MS_USER_SYSTEM_SETTING only for SQL, if pOrgValue = NULL: Save All the data to SQL
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SaveSysSetting(MS_USER_SYSTEM_SETTING *pOrgValue = NULL);

    //-------------------------------------------------------------------------------------------------
    /// restore the setting of system setting to default
    /// @param u16KeepSetting         \b IN: Specify the keep setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultSystem(U16 u16KeepSetting);

    //=========VideoSetting============================================================
    //-------------------------------------------------------------------------------------------------
    /// Initial the setting of videosetting
    /// @param u8Index         \b IN: Specify the input source enable index
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitVideoSetting(U8 u8Index);

    //-------------------------------------------------------------------------------------------------
    /// restore the setting of videosetting to default
    /// @param u8Index         \b IN: Specify the input source enable index
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultVideo(U8 u8Index);

    //-------------------------------------------------------------------------------------------------
    /// Dump the setting of videosetting
    /// @param u8Index         \b IN: Specify the input source enable index
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void DumpVideoSetting(U8 u8Index);

    //-------------------------------------------------------------------------------------------------
    /// save the setting of videosetting
    /// @param u8Index         \b IN: Specify the input source enable index
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SaveVideoSetting(U8 u8Index);

    //=========SoundSetting============================================================
    //-------------------------------------------------------------------------------------------------
    /// Initial the setting of soundsetting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitSoundSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// load the setting of soundsetting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadSoundSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// save the setting of soundsetting
    /// @param pOrgValue \b IN: the pointer to the structure of MS_USER_SOUND_SETTING only for SQL, if pOrgValue = NULL: Save All the data to SQL
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SaveSoundSetting(MS_USER_SOUND_SETTING *pOrgValue = NULL);

    //-------------------------------------------------------------------------------------------------
    /// restore the setting to soundsetting default
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultSoundSetting(void);

    //=========TimeData================================================================
    //-------------------------------------------------------------------------------------------------
    /// Initial the setting of timedata
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitTimeData(void);

    //-------------------------------------------------------------------------------------------------
    /// load the setting of timedata
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadTimeData(void);

    //-------------------------------------------------------------------------------------------------
    /// save the setting of timedata
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SaveTimeData(void);

    //-------------------------------------------------------------------------------------------------
    /// restore the setting to timedata default
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultTime(void);

    //=========BlockData===============================================================
    //-------------------------------------------------------------------------------------------------
    /// Initial the setting of blockdata
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitBlockData(void);

    //-------------------------------------------------------------------------------------------------
    /// load the setting of blockdata
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadBlockData(void);

    //-------------------------------------------------------------------------------------------------
    /// save the setting of blockdata
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SaveBlockData(void);

    //-------------------------------------------------------------------------------------------------
    /// restore the setting to blockdata default
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultBlock(void);

    //=========User PC mode data=======================================================
    //-------------------------------------------------------------------------------------------------
    /// initial the userpcmode setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitUserPCModeSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// load the userpcmode setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadUserPCModeSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// save the userpcmode setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SaveUserPCModeSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// restore the setting to userpcmode default
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultUserPCModeSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// restore the setting to Factory default
    /// @param u8RestoreMask         \b IN: Specify the mask bit
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreFactoryDefault(U8 u8RestoreMask);

    // Factory Functions
    //-------------------------------------------------------------------------------------------------
    /// load the setting to Factory
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadFactorySetting(void);

    //-------------------------------------------------------------------------------------------------
    /// Initial all the general setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitAllGenSetting(void);

    // Color Temp
    //-------------------------------------------------------------------------------------------------
    /// load the setting of color temperatue
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadFactoryColorTemp(void);

    //-------------------------------------------------------------------------------------------------
    /// load the setting of color temperatue in 16 bit
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadFactoryColorTempEx(void);

    //-------------------------------------------------------------------------------------------------
    /// Initial the setting of color temperatue
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitFactoryColorTemp(void);

    //-------------------------------------------------------------------------------------------------
    /// Initial the setting of color temperatue in 16 bit
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitFactoryColorTempEx(void);

    //-------------------------------------------------------------------------------------------------
    /// restore the setting of color temperatue to default
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultColorTemp(void);

    //-------------------------------------------------------------------------------------------------
    /// restore the setting of color temperatue to default in 16 bit
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultColorTempEx(void);

    //-------------------------------------------------------------------------------------------------
    /// save the setting of color temperatue
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SaveColorTemp(void);

    //-------------------------------------------------------------------------------------------------
    /// save the setting of color temperatue in 16 bit
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SaveColorTempEx(void);

    // ADC
    //-------------------------------------------------------------------------------------------------
    /// initial the setting of ADC
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitADCSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// load the setting of ADC
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadADCSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// restore the setting of ADC to default
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultADC(void);

    //-------------------------------------------------------------------------------------------------
    /// save the setting of ADC
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SaveADCSetting(void);

    //EPG
    //-------------------------------------------------------------------------------------------------
    /// load the setting of EPG
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadEPGTimerSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// restore the setting of EPG Timer to default
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultEPGTimer(void);

    //Medium
    //-------------------------------------------------------------------------------------------------
    /// initial the setting of Medium
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitMediumSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// load the setting of Medium
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadMediumSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// restore the setting of Medium to default
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultMediumSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// save the setting of Medium
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SaveMediumSetting(void);

    //Network
    //-------------------------------------------------------------------------------------------------
    /// initial the setting of Netowrk
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitNetworkSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// load the setting of Netowrk
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadNetworkSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// restore the setting of Netowrk to default
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultNetworkSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// save the setting of Netowrk
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SaveNetworkSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// Initial the DTV overscan setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitDTVOverscanSet(void);

    //-------------------------------------------------------------------------------------------------
    /// Load the DTV overscan setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadDTVOverscanSet(void);

    //-------------------------------------------------------------------------------------------------
    /// Restore to default DTV overscan setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultDTVOverscanSet(void);

    //-------------------------------------------------------------------------------------------------
    /// Save DTV overscan setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SaveDTVOverscanSet(void);

    //-------------------------------------------------------------------------------------------------
    /// Initial the HDMI overscan setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitHDMIOverscanSet(void);

    //-------------------------------------------------------------------------------------------------
    /// Load the HDMI overscan setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadHDMIOverscanSet(void);

    //-------------------------------------------------------------------------------------------------
    /// Restore to default HDMI overscan setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultHDMIOverscanSet(void);

    //-------------------------------------------------------------------------------------------------
    /// Save the HDMI overscan setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SaveHDMIOverscanSet(void);

    //-------------------------------------------------------------------------------------------------
    /// Initial the component overscan setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitYPbPrOverscanSet(void);

    //-------------------------------------------------------------------------------------------------
    /// Load the component overscan setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadYPbPrOverscanSet(void);

    //-------------------------------------------------------------------------------------------------
    /// Restore to default component overscan setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultYPbPrOverscanSet(void);

    //-------------------------------------------------------------------------------------------------
    /// Save the component overscan setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SaveYPbPrOverscanSet(void);

    //-------------------------------------------------------------------------------------------------
    /// Initial the VD overscan setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitVDOverscanSet(void);

    //-------------------------------------------------------------------------------------------------
    /// Load the VD overscan setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadVDOverscanSet(void);

    //-------------------------------------------------------------------------------------------------
    /// Restore to default VD overscan setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultVDOverscanSet(void);

    //-------------------------------------------------------------------------------------------------
    /// Save the VD overscan setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SaveVDOverscanSet(void);

#if (SQL_DB_ENABLE == 1 && MSTAR_TVOS == 1)
    //-------------------------------------------------------------------------------------------------
    /// Load overscan setting separately
    /// @param Res \b IN: the input to indicate which resolution is chosen
    /// @param eARCType \b IN: mapi_video::MAPI_VIDEO_ARC_Type
    /// @param eInputType \b IN: the input to indicate which source of item of OverscanSet is chosen
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadOverscanSeparately(U32 Res, mapi_video_datatype::MAPI_VIDEO_ARC_Type eARCType, MAPI_INPUT_SOURCE_TYPE eInputType);

    //-------------------------------------------------------------------------------------------------
    /// Load the ATV overscan setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadATVOverscanSet(void);

    //-------------------------------------------------------------------------------------------------
    /// Save the ATV overscan setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SaveATVOverscanSet(void);

    //-------------------------------------------------------------------------------------------------
    /// Load the config setting of supernova
    /// @return TRUE: flag on ,
    /// @return FALSE: flag off.
    //-------------------------------------------------------------------------------------------------
    virtual BOOL LoadSNConfigDirtyFlag(void);

    //-------------------------------------------------------------------------------------------------
    /// Restore the config setting of supernova
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreSNConfigSetting(void);
#endif

#if (PEQ_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Init PEQ Setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitPEQSet(void);

    //-------------------------------------------------------------------------------------------------
    /// Load PEQ Setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadPEQSet(void);

    //-------------------------------------------------------------------------------------------------
    /// Restore to default PEQ Setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultPEQSet(void);

    //-------------------------------------------------------------------------------------------------
    /// Save PEQ setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SavePEQSet(void);
#endif

#if (CI_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Init CI Setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitCISet(void);

    //-------------------------------------------------------------------------------------------------
    /// Load CI Setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadCISet(void);

    //-------------------------------------------------------------------------------------------------
    /// Restore to default CI Setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultCISet(void);

    //-------------------------------------------------------------------------------------------------
    /// Save CI setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SaveCISet(void);
#endif

#if (OAD_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    //OAD Info
    //-------------------------------------------------------------------------------------------------
    /// initial oad info
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitOADInfo(void);

    //-------------------------------------------------------------------------------------------------
    /// initial oad info for wake up scan
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitOADWakeUpInfo(void);

    //-------------------------------------------------------------------------------------------------
    /// load oad info
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadOADInfo(void);

    //-------------------------------------------------------------------------------------------------
    /// restore oad info
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultOADInfo(void);

    //-------------------------------------------------------------------------------------------------
    /// save oad info
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SaveOADInfo(void);
#endif
    //-------------------------------------------------------------------------------------------------
    //Customer DB Reserve
    //-------------------------------------------------------------------------------------------------
    /// initial Customer DB Reserve
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitCustomer_DB_Reserve(void);

    //-------------------------------------------------------------------------------------------------
    /// load Customer DB Reserve
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadCustomer_DB_Reserve(void);

    //-------------------------------------------------------------------------------------------------
    /// save Customer DB Reserve
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SaveCustomer_DB_Reserve(void);

    //-------------------------------------------------------------------------------------------------
    /// restore Customer DB Reserve
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultCustomer_DB_Reserve(void);

    //-------------------------------------------------------------------------------------------------
    /// Set Customer DB Reserve
    /// @param pValue: the test type
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetCustomer_DB_Reserve(MS_SYS_CUSTOMER_DB_RESERVE *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Get Customer DB Reserve
    /// @param pValue: the test type
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetCustomer_DB_Reserve(MS_SYS_CUSTOMER_DB_RESERVE *pValue);

    //-------------------------------------------------------------------------------------------------
    /// Get the database address.
    /// @param  enType      \b IN : The address index.
    /// @param  para      \b IN: The extra parameter pointer.
    /// @return U32: Specify address of EN_SYS_DB_ADDR enum
    //-------------------------------------------------------------------------------------------------
    virtual U32 GetSysDbAddr(EN_SYS_DB_ADDR enType, void* para = NULL) = 0;

    //NLA
     //-------------------------------------------------------------------------------------------------
    /// initial the setting of No-liner-adjust
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitNLASetting(void);

    //-------------------------------------------------------------------------------------------------
    /// save the setting of NLA
    /// @param u8Index
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SaveNLASetting(U8 u8Index);

    //-------------------------------------------------------------------------------------------------
    /// restore the setting of NLA to default
    /// @param u8Index
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultNLA(U8 u8Index);

    //-------------------------------------------------------------------------------------------------
    /// Load Factory extern item
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadFactoryExtSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// Save Factory extern item
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SaveFactoryExtSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// Init Factory extern item to default
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitFactoryExtSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// restore the setting of Factory Power Mode  to default
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefault_PowerMode(void);

    //-------------------------------------------------------------------------------------------------
    /// restore the setting of Non-standard VD to default
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultNS_VDSet(void);

    //-------------------------------------------------------------------------------------------------
    /// restore the setting of Non-standard VIF to default
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultNS_VIFSet(void);

    //-------------------------------------------------------------------------------------------------
    /// restore the setting of SSC to default
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultNS_SSCSet(void);

    //-------------------------------------------------------------------------------------------------
    /// restore the setting of SSC of two MIU to default
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultNS_SSC2Set(void);

    //-------------------------------------------------------------------------------------------------
    /// restore the setting of 6M30SSC to default
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultNS_6M30SSCSet(void);

    //-------------------------------------------------------------------------------------------------
    /// restore the setting of NLASetting to default
    /// @return None
    //-------------------------------------------------------------------------------------------------
    //virtual void RestoreDefaultNS_NLASet(void);

    //-------------------------------------------------------------------------------------------------
    /// restore the setting of Factory extern setting to default
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultFactoryExternSet(void);

    //-------------------------------------------------------------------------------------------------
    /// set factory database default value
    /// @param ver: database version
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetFactoryDatabaseDefaultValue(int ver);

    //-------------------------------------------------------------------------------------------------
    /// set system database default value
    /// @param ver: database version
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetSystemDatabaseDefaultValue(int ver);

    //-------------------------------------------------------------------------------------------------
    /// update new version database struct by type
    /// @param type: type of database
    /// @param ver: database version
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void FillNewStructDefaultValue(EN_DB_TYPE type, int ver);

    //-------------------------------------------------------------------------------------------------
    /// Check the version of system database, if version is not the same, system will set default values.
    /// @param type: type of database
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual BOOL CheckVersionAndSetDefaultValue(EN_DB_TYPE type);

#if(SQL_DB_ENABLE)
    class SQL_Check
    {
    public:
        //-------------------------------------------------------------------------------------------------
        /// Class Constructor.
        //-------------------------------------------------------------------------------------------------
        SQL_Check(const char *dbpath, struct sqlite3 **ppdb, struct sqlite3_stmt **stmt, pthread_mutex_t *pmut);

        //-------------------------------------------------------------------------------------------------
        /// Class Destructor.
        //-------------------------------------------------------------------------------------------------
        virtual ~SQL_Check(void);

    protected:
        struct sqlite3 **m_ppdb;
        struct sqlite3_stmt **m_stmt;
        pthread_mutex_t *m_mutScopeLock;

        void copyFile(const char *srcfile,const char *desfile);
        virtual void recoveryDBFile(const char * dbFile);
    };

    class SQL_Connect
    {
    public:
        //-------------------------------------------------------------------------------------------------
        /// Class Constructor.
        //-------------------------------------------------------------------------------------------------
        SQL_Connect(void);
        SQL_Connect(const char *dbpath, sqlite3 **ppdb, pthread_mutex_t *pmut);

        //-------------------------------------------------------------------------------------------------
        /// Class Destructor.
        //-------------------------------------------------------------------------------------------------
        ~SQL_Connect(void);

        int SQL_Open(const char *dbpath, struct sqlite3 **ppdb);
        int SQL_Close(struct sqlite3 **ppdb);

    private:
        struct sqlite3 **m_ppdb;
        pthread_mutex_t *m_mutScopeLock;
    };


    class SQL_Transaction
    {
    public:
        //-------------------------------------------------------------------------------------------------
        /// Class Constructor.
        //-------------------------------------------------------------------------------------------------
        SQL_Transaction(struct sqlite3 *db);

        //-------------------------------------------------------------------------------------------------
        /// Class Destructor.
        //-------------------------------------------------------------------------------------------------
        ~SQL_Transaction(void);

    private:
        struct sqlite3 *pdb;
        int rc;
        char *zErrMsg;
    };

    class SQL_LookupPre
    {
    public:
        //-------------------------------------------------------------------------------------------------
        /// Class Constructor.
        //-------------------------------------------------------------------------------------------------
        SQL_LookupPre(struct sqlite3 *db, const char *table, int &ColNum, char * Condition = NULL);

        //-------------------------------------------------------------------------------------------------
        /// Class Destructor.
        //-------------------------------------------------------------------------------------------------
        ~SQL_LookupPre(void);

        struct sqlite3_stmt *pSqlstmt;
    };

    struct sqlite3 *pSqldb;
    struct sqlite3 *pSqlfactorydb;
    pthread_mutex_t m_DBmutex;
    BOOL abJavaDbTblDirtyFlag[T_MAX_IDX];
    BOOL abNativeDbTblDirtyFlag[T_MAX_IDX];

    //-------------------------------------------------------------------------------------------------
    /// SQL command package
    //-------------------------------------------------------------------------------------------------
    int SQL_Step(struct sqlite3_stmt *pstmt);
    BOOL SQL_SetU32(struct sqlite3 *db, const char *Table, const char *Title, const char *Condition, U32 Value);
    BOOL SQL_SetText(struct sqlite3 *db, const char *Table, const char *Title, const char *Condition, char *Text);
    BOOL SQL_SetBlob(struct sqlite3 *db, const char *Table, const char *Title, const char *Condition, U8 *BlobArray, U16 u16Size);
    BOOL SQL_SetArray(struct sqlite3 *db, const char *Table, const char *Title, const char *Condition, void *pArray, U16 u16Size, U8 ArrayType);
    U32 SQL_GetU32(struct sqlite3_stmt *stmt, const char * Title);
    const U8 *SQL_GetText(struct sqlite3_stmt *stmt, const char *Title);
    U8 *SQL_GetBlob(struct sqlite3_stmt *stmt, const char *Title, U16 u16Size);
    BOOL SQL_GetArray(struct sqlite3_stmt *stmt, const char *Title, void *pArray, U16 u16Size, U8 ArrayType);
    int SQL_GetColumnIndex(struct sqlite3_stmt *stmt,const char *ColName);
#endif

    //-------------------------------------------------------------------------------------------------
    /// init factory audio setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void InitFactoryAudioSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// restore default factory audio setting
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreDefaultFactoryAudioSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// load factory audio setting from database
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void LoadFactoryAudioSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// save factory audio setting from database
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SaveFactoryAudioSetting(void);

#if (INPUT_SOURCE_LOCK_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Initial the setting of input source lock
    /// @return MAPI_TRUE: initialize input source lock setting success
    /// @return MAPI_FALSE: initialize input source lock setting fail
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL InitInputSourceLockSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// Load the setting of input source lock
    /// @return MAPI_TRUE: load input source lock setting success
    /// @return MAPI_FALSE: load input source lock setting fail
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL LoadInputSourceLockSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// Save the setting of input source lock
    /// @return MAPI_TRUE: save input source lock setting success
    /// @return MAPI_FALSE: save input source lock setting fail
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SaveInputSourceLockSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// Restore the setting of input source lock to default
    /// @return MAPI_TRUE: restore load input source lock setting success
    /// @return MAPI_FALSE: restore input source lock setting fail
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL RestoreDefaultInputSourceLockSetting(void);
#endif

    /// mutex for system database
    pthread_mutex_t m_mutex;

    /// enable input source counts
    U8 m_u8EnableInputCnt;
    /// Store Each Input Source Type Accumulating Count Number.
    U8 m_u8InputSrcTypeToCntIdx[MAPI_INPUT_SOURCE_NUM];
    /// Store MAPI_INPUT_SOURCE_TYPE by index with 0 to total Eanble Input Count
    MAPI_INPUT_SOURCE_TYPE *m_pInputCntIdxToSrcType;

    /// last video setting
    T_MS_VIDEO *m_pastVideo;

    // user space database
    /// the system setting
    MS_USER_SYSTEM_SETTING m_stSysSetting;
    /// the sound setting
    MS_USER_SOUND_SETTING m_stSoundSetting;
    /// the time structure
    MS_TIME m_stTime;
    /// the block system setting
    MS_BLOCKSYS_SETTING m_stBlockSysSetting;
    /// the PIP setting
    MS_PIP_SETTING m_stPipSetting;
    /// the PC ADC user mode setting
    ST_PCADC_USER_MODE_SETTING m_astUserPCModeSetting[MAX_USER_MODE_NUM];
    /// the EPG timer
    ST_EPG_EVENT_TIMER_INFO m_stEpgTimer[EPG_TIMER_MAX_NUM];

    /// the CRID timer
    ST_CRID_TIMER_INFO m_stCRIDTimer[CRID_TIMER_MAX_NUM];

    /// the medium setting
    ST_MEDIUM_SETTING m_stMediumSetting;
    /// the network setting
    ST_NETWORK_SETTING m_stNetworkSetting;

#if (OAD_ENABLE == 1)
    /// OAD information saved in system database used for UNT
    MW_OAD_INFORMATION m_stOADInfo;
    /// OAD information saved in system database used by timer
    MW_OAD_WAKEUP_INFORMATION m_stOADWakeUpInfo;
#endif

    /// For Cunstmer DB Reserve
    MS_SYS_CUSTOMER_DB_RESERVE m_stCustomer_DB_Reserve;

    /// the audio setting
    U8 m_u8AudLangSelected;

    // default tables
    /// default sound mode setting
    static stSoundModeSeting m_astDefaultSoundModeSeting[SOUND_MODE_NUM];
    /// default ADC setting
    static MS_ADC_SETTING m_stDefaultADCSetting;
    /// default ADC auto-tune setting
    static BOOL m_bDefaultADCAutoTune;
    /// default time setting
    static MS_TIME m_stDefaultTimeData;
    /// default DTV video data table
    static T_MS_VIDEO m_stDefaultDtvVideoDataTbl;
    /// default ATV video data table
    static T_MS_VIDEO m_stDefaultAtvVideoDataTbl;
    /// default AV video data table
    static T_MS_VIDEO m_stDefaultAvVideoDataTbl;
    /// default Component video data table
    static T_MS_VIDEO m_stDefaultComponentVideoDataTbl;
    /// default VGA video data table
    static T_MS_VIDEO m_stDefaultVgaVideoDataTbl;
    /// default HDMI video data table
    static T_MS_VIDEO m_stDefaultHdmiVideoDataTbl;
    /// default SCART video data table
    static T_MS_VIDEO m_stDefaultScartVideoDataTbl;
    /// default SV video data table
    static T_MS_VIDEO m_stDefaultSvVideoDataTbl;
    /// default RVU video data table
    static T_MS_VIDEO m_stDefaultRvuVideoDataTbl;
    /// default color temperature
    static T_MS_COLOR_TEMP m_astDefaultColorTemp;
    /// default color temperature in 16 bit
    static T_MS_COLOR_TEMPEX m_astDefaultColorTempEx;

    /// the country setting
    static MEMBER_COUNTRY m_eOSDCountrySetting;

    /// the default medium setting
    static ST_MEDIUM_SETTING m_stDefaultMediumTbl;
    /// default network setting
    static ST_NETWORK_SETTING m_stDefaultNetworkTbl;
    /// default NLA table
    static MS_NLA_SETTING m_stDefaultNLAtbl;

#if (PEQ_ENABLE == 1)
    /// default PEQ setting
    static ST_FACTORY_PEQ_SETTING m_stDefaultPEQSet;
#endif

    // ------------------------------------------------------------
    // Factory Data
    // ------------------------------------------------------------
    /// factory color temperature
    T_MS_COLOR_TEMP m_stFactoryColorTemp;
    /// factory color temperature in 16 bit
    T_MS_COLOR_TEMPEX m_stFactoryColorTempEx;
#if (STEREO_3D_ENABLE == 1)
    T_MS_COLOR_TEMPEX m_stFactoryColorTempEx3D;
#endif
    /// factory ADC setting
    MS_ADC_SETTING m_stFactoryAdc;
    /// factory NLA setting
    MS_NLA_SETTING *m_pastNLASet;
#if (STEREO_3D_ENABLE == 1)
    /// factory 3D NLA setting
    MS_NLA_SETTING *m_pastNLASet3D;
#endif
    /// factory external setting
    MS_FACTORY_EXTERN_SETTING m_stFactoryExt;
    /// factory ADC auto-tune
    BOOL m_bADCAutoTune;
    /// factory DTV overscan setting
    MS_FACTORY_DTV_OVERSCAN_SETTING m_DTVOverscanSet;
    /// factory HDMI overscan setting
    MS_FACTORY_HDMI_OVERSCAN_SETTING m_HDMIOverscanSet;
    /// factory YPbPr overscan setting
    MS_FACTORY_YPbPr_OVERSCAN_SETTING m_YPbPrOverscanSet;
    /// factory VD overscan setting
    MS_FACTORY_VD_OVERSCAN_SETTING m_VDOverscanSet;
#if (SQL_DB_ENABLE == 1 && MSTAR_TVOS == 1)
    /// factory ATV overscan setting
    MS_FACTORY_VD_OVERSCAN_SETTING m_ATVOverscanSet;
#endif
#if (PEQ_ENABLE == 1)
    /// factory PEQ setting
    ST_FACTORY_PEQ_SETTING m_stPEQSet;
#endif
#if (CI_ENABLE == 1)
    /// factory CI setting
    ST_FACTORY_CI_SETTING m_stCISet;
#endif
    /// factory audio setting
    ST_FACTORY_AUDIO_SETTING m_stFactoryAudioSetting;
    /// default factory audio setting
    static ST_FACTORY_AUDIO_SETTING m_stDefaultFactoryAudioSetting;

    static MS_BOOT_SETTING m_stDefaultBootSetting;
    MS_BOOT_SETTING m_stBootSetting;

#if (INPUT_SOURCE_LOCK_ENABLE == 1)
    /// Input source lock setting
    ST_INPUT_SOURCE_LOCK_SETTING m_stInputSourceLockSetting;
#endif

private:


    //-------------------------------------------------------------------------------------------------
    /// InputSrcTypeToCntIdx
    /// @param enInputSource         \b IN: mapi input source type
    /// @return U8: source type enable count index
    //-------------------------------------------------------------------------------------------------
    virtual U8 _InputSrcTypeToCntIdx(MAPI_INPUT_SOURCE_TYPE enInputSource);

    //-------------------------------------------------------------------------------------------------
    /// _InputCntIdxToSrcType
    /// @param u8Index         \b IN: source type enable count index
    /// @return MAPI_INPUT_SOURCE_TYPE: mapi input source type
    //-------------------------------------------------------------------------------------------------
    virtual MAPI_INPUT_SOURCE_TYPE _InputCntIdxToSrcType(U8 u8Index);

    //-------------------------------------------------------------------------------------------------
    /// ValidateCalibrationMode
    /// @return TRUE: gainoffset is updated;
    /// @return FALSE: gainoffset is not updated
    //-------------------------------------------------------------------------------------------------
    virtual BOOL ValidateADCCalibrationMode(void);
};

#endif
