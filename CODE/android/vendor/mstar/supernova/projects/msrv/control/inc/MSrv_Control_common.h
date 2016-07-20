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
/// @file MSrv_Control_common.h
/// @brief\b Provide general access to players and settings.
/// @author MStar Semiconductor Inc.
///
/// Application use MSrv_Control_common as a access entry to players and settings.
///
/// Features:
/// - Provide a general access entry beyond players and settings.
///////////////////////////////////////////////////////////////////////////////////////////////////

#ifndef _MSRV_CONTROL_COMMON_H_
#define _MSRV_CONTROL_COMMON_H_

#include <vector>
#include <string>
#include <map>

#include "mapi_video.h"
#include "mapi_video_out.h"
#include "mapi_audio.h"
#include "mapi_display.h"
#include "mapi_utility.h"
#include "MSrv.h"
#include "MSrv_Timing.h"

//forward define
class MSrv_Factory_Mode;
class MSrv_Timer;
class MSrv_Picture;
#if (ENABLE_BACKEND == 1)
class MSrv_Backend;
#endif
class MSrv_Video;
class MSrv_System_Database;
class MSrv_SSSound;
#if (AUTO_TEST == 1)
class AT_CmdManager;
#endif
#if (MODULE_TEST == 1)
class MT_CmdManager;
#endif

class MSrv_ChannelManager;
class MSrv_Player;
class MSrv_Network_Control;
class MSrv_DivX_DRM;
#if (MHL_ENABLE == 1)
class MSrv_MHL;
#endif //MHL_ENABLE
#if (HDMITX_ENABLE == 1)
class MSrv_HDMITX;
#endif
#if (CEC_ENABLE == 1)
class MSrv_CEC;
#endif

#if (STEREO_3D_ENABLE == 1)
class MSrv_3DManager;
#endif

#if (RVU_ENABLE == 1)
class MSrv_RVU_Player;
#endif

#if (VCHIP_ENABLE == 1) //check me
class MW_VCHIP;
#endif

#if (ENABLE_NETREADY == 1)
class MSrv_DeviceAgent;
#endif

#if (ENABLE_LITE_SN == 0)
class MSrv_BacklightControl;
class MSrv_SrcDetect;
class MSrv_PreSharedKey;
class MSrv_Advert_Player;
#endif //ENABLE_LITE_SN

#if (MSTAR_TVOS == 1) // for tvapp and supernova mutually send message
#define MAX_DATA_BLOCK_LEN 256

//only for hisense
#define EV_TVOS_UTIITY_EVENT_SOURCE_CHANGE  0x0111
#define EV_TVOS_UTIITY_EVENT_SIGNAL_STATUS_UPDATED 0x0112
#define EV_TVOS_UTIITY_EVENT_SIGNAL_AUTO_SWITCH 0x113
#define EV_TVOS_UTIITY_EVENT_CURRENT_SOURCE_PLUG_OUT 0x114
/// for atsc tvos input block
#define EV_TVOS_UTIITY_EVENT_INPUT_BLOCK    0x117
#define ENABLE_3D_FORMAT 0x767
#define DISABLE_3D_FORMAT 0x768
#define EN_SYNC_TIME_ANDROID 0x789
#define EXIT_SUBWIN 0x300
#define EN_4K1K_FP_Mode 0x8100

#define TVOS_COMMON_CMD_GET_SOURCE_STATUS    "GetInputSourceStatus"
#define TVOS_COMMON_CMD_CHECK_DIRECT_TUNE_DTV_RF_VALID    "CheckDTVRfValiD"
#define TVOS_COMMON_CMD_CHECK_DIRECT_TUNE_ATV_RF_VALID    "CheckATVRfValiD"

#endif

#if (STR_ENABLE == 1)
typedef enum
{
    /// Write str_max_cnt file
    E_STR_CMD_SET_MAX_CNT = 0,
    /// Set the last input source
    E_STR_CMD_SET_LAST_INPUT_SOURCE,
    /// Set STR status
    E_STR_CMD_SET_STATUS,
    /// MAX
    E_STR_CMD_MAX
} EN_STR_CMD;
#endif



///define 250ms of thread monitor interval
#define THREAD_MONITOR_INTERVAL_MS                  250 //250ms

///Time interval of 5 seconds
#define THREAD_MONITOR_CHECK_NO_SIGNAL_SEC_5        ((5*1000)/(THREAD_MONITOR_INTERVAL_MS))  //5 sec
///Time interval of 10 seconds
#define THREAD_MONITOR_CHECK_NO_SIGNAL_SEC_10       ((10*1000)/(THREAD_MONITOR_INTERVAL_MS)) //10 sec
///Time interval of 15 seconds
#define THREAD_MONITOR_CHECK_NO_SIGNAL_SEC_15       ((15*1000)/(THREAD_MONITOR_INTERVAL_MS)) //15 sec
///Default time interval
#define THREAD_MONITOR_CHECK_NO_SIGNAL_SEC_DEFAULT  THREAD_MONITOR_CHECK_NO_SIGNAL_SEC_5
///timeout for wait audio init
#define AUDIO_INIT_WAIT_TIMEOUT 1000//1000ms



///UPGRADE_MODE : NULL USB OAD NET
#define UPGRADE_MODE                "upgrade_mode"
/// Usb update script file name
#define USB_SCRIPT_FILE_NAME        "/Customer/usb_auto_update.txt"
/// Usb update flag file name
#define USB_FLAG_FILE_NAME          "/Customer/usb.txt"
/// Usb update bin file name
#define USB_BIN_FILE_NAME           "/MstarUpgrade.bin"
/// Secure usb update bin file name
#define SECURE_USB_BIN_FILE_NAME           "/MstarUpgrade.bin"
/// Env variable name for the port with usb stick
#define USB_UPGRADE_PORT            "usb_upgrade_port"
///Env variable name for the path of usb upgrade image in usb stick
#define USB_UPGRADE_PATH            "usb_upgrade_path"
/// The flag array index used when BYPASS_BIST is enabled in MBoot for usb upgrade
#define USB_UPGRADE_BYPASS_BIST_INFO_OFFSET 0
/// The path to read boot cmd from Mboot
#define PROC_CMD_LINE               "/proc/cmdline"
/// The env variable prefix of MBoot env location
#define ENV_CFG_PREFIX              "ENV="
/// The MBoot env location definition of NAND
#define ENV_IN_NAND                 "NAND"
/// The MBoot env location definition of UBI
#define ENV_IN_UBI                  "UBI"
/// The MBoot env location definition of SERIAL
#define ENV_IN_SERIAL               "SERIAL"
/// The MBoot env location definition of MMC
#define ENV_IN_EMMC               "EMMC"
/// The secure env variable prefix of MBoot env location
#define SECURITY_CFG_PREFIX              "SECURITY="
/// The Security flag in Mboot is "ON"
#define SECURITY_IS_ON                 "ON"
/// The Security flag in Mboot is "OFF"
#define SECURITY_IS_OFF                 "OFF"
/// The max length of usb path
#define MAX_USB_PATH_LEN            256

///Env variable name for the path of nand upgrade image
#define LOAD_UPGRADEFILE_PATH  "upgradefile_path"

// As system powerdown, reserve a file to check if powerdown flow right
#define FOLDER_FOR_POWEROFF_FLAG  "/Customer/dc_poweroff"


#if (OAD_ENABLE == 1)
#if (MSTAR_TVOS == 1)
/// The path to store OAD download flag (When info_exchange == ubifile)
#define OAD_ENV_FILE "/cache/MstarUpgrade.txt"
/// The path to store OAD download file
#define OAD_BIN_FILE "/cache/update_signed.zip"
/// The path to store SECURE OAD download file
#define SECURE_OAD_BIN_FILE "/cache/update_signed.zip"
#else
/// The path to store OAD download flag (When info_exchange == ubifile)
#define OAD_ENV_FILE "/OAD/MstarUpgrade.txt"
/// The path to store OAD download file
#define OAD_BIN_FILE "/OAD/MstarUpgrade.bin"
/// The path to store SECURE OAD download file
#define SECURE_OAD_BIN_FILE "/OAD/MstarUpgrade.bin"
#endif
/// The path to read boot cmd from Mboot
#define OAD_IN_MBOOT_CFG_PATH "/proc/cmdline"
/// The string flag used in boot cmd to indicate OAD download in MBoot
#define OAD_IN_MBOOT_STR "OAD_IN_MBOOT"
/// Env variable name for the frequency used for OAD in MBoot
#define OAD_UPGRADE_FREQ            "oad_upgrade_freq"
/// Env variable name for the bandwidth used for OAD in MBoot
#define OAD_UPGRADE_BAND            "oad_upgrade_band"
/// Env variable name for the pid used for OAD in MBoot
#define OAD_UPGRADE_PID            "oad_upgrade_pid"
#if (SDTT_OAD_ENABLE == 1)
/// Env variable name for the sdtt flag used for OAD in MBoot
#define OAD_IS_SDTT            "oad_is_sdtt"
/// Env variable name for the group pid used for OAD in MBoot
#define OAD_GROUP_PID            "oad_group_pid"
#endif
/// The flag array index used when BYPASS_BIST is enabled in MBoot for oad upgrade
#define OAD_UPGRADE_BYPASS_BIST_INFO_OFFSET 1
#endif
/// The flag array index used when BYPASS_BIST is enabled in MBoot for oad upgrade
#define NETWORK_UPGRADE_BYPASS_BIST_INFO_OFFSET 2
/// Env variable name for if PM51 is running on DRAM
#define INFO_PM51_RUN_ON_DRAM           "51OnRam"

/// cstomer info size
#define MSRV_CUS_INFO_SIZE      49
/// cstomer hash size
#define MSRV_CUS_HASH_SIZE      16

///tm, year since 1900
#define MSRV_TM_START_YEAR                           1900

/// filename buffer
#define MSRV_MAX_BUFFER 63

/// define power on music volume
#define DEFAULT_POWERON_MUSIC_VOL  30

#define MAX_POWERON_MUSIC_VOL 100

/// define unused dtv route type
#define UNUSED_DTV_ROUTE  MAXROUTECOUNT

/// define status for player is not in mainwindow and subwindow
#define MAPI_UNDEFINE_WINDOW MAPI_MAX_WINDOW

#if (PREVIEW_MODE_ENABLE == 1)
/// Traveling mode callback information
typedef struct
{
    /// Traveling mode callback data info
    ST_TRAVELING_CALLBACK_DATA_INFO pstTravelDataInfo;
    /// Traveling mode callback engine type
    int enEngineType;
} ST_TRAVELING_MODE_CALLBACK_INFORMATION;
#endif

/// Preview mode process information
typedef enum
{
    /// Preview mode process result, fail
    EN_PREVIEW_MODE_PROCESS_FAIL = 0,
    /// Preview mode process result, no signal or signal unstable
    EN_PREVIEW_MODE_SIGNAL_UNSTABLE,
    /// Preview mode process result, main source is conflict with sub source
    EN_PREVIEW_MODE_SOURCE_CONFLICT,
    /// Preview mode process result, success
    EN_PREVIEW_MODE_PROCESS_SUCCESS,
    /// Preview mode process result max
    EN_PREVIEW_MODE_MAX,
}EN_PREVIEW_MODE_PROCESS_INFORMATION;

/// PVR record status
typedef enum
{
    E_RECORD_BACKGROUND,
    E_RECORD_SUB,
    E_RECORD_SUB_BACKGROUND,
    E_RECORD_NONE,
    E_RECORD_SOURCE_MAX
}EN_PVR_RECORD_STATUS;

/// PQ Update file
typedef enum
{
    /// DLC file
    E_MSRV_DLC_FILE,
    /// Color Matrix file
    E_MSRV_COLOR_MATRiX_FILE,
    /// Bandwidth Table file
    E_MSRV_BANDWIDTH_REG_TABLE_FILE,
    /// PQ Main file
    E_MSRV_PQ_MAIN_FILE,
    /// PQ Main Text file
    E_MSRV_PQ_MAIN_TEXT_FILE,
    /// PQ Main Ex file
    E_MSRV_PQ_MAIN_EX_FILE,
    /// PQ Main Ex Text file
    E_MSRV_PQ_MAIN_EX_TEXT_FILE,
    /// PQ Sub file
    E_MSRV_PQ_SUB_FILE,
    /// PQ Sub Text file
    E_MSRV_PQ_SUB_TEXT_FILE,
    /// PQ Sub Ex file
    E_MSRV_PQ_SUB_EX_FILE,
    /// PQ Sub Ex Text file
    E_MSRV_PQ_SUB_EX_TEXT_FILE,
    /// Gamma0 file
    E_MSRV_GAMMA0_FILE,
} EN_MSRV_PQ_UPDATE_FILE;

///power on mode
typedef enum
{
    /// power on secondary
    EN_ACON_POWERON_SECONDARY,
    /// power on memory
    EN_ACON_POWERON_MEMORY,
    /// power on directly
    EN_ACON_POWERON_DIRECT,
    /// power on max
    EN_ACON_POWERON_MAX,
} EN_ACON_POWERON_MODE;

/// power on logo mode,max must <=9
typedef enum
{
    /// poweron logo off
    EN_LOGO_OFF,
    /// default logo
    EN_LOGO_DEFAULT,
    /// capture1 logo
    EN_LOGO_CAPTURE1,
    /// capture2 logo
    EN_LOGO_CAPTURE2,
    /// logo num
    EN_LOGO_MAX,
} EN_LOGO_MODE;

/// power on music mode,max must <=9
typedef enum
{
    /// poweron music off
    EN_POWERON_MUSIC_OFF,
    /// default poweron music
    EN_POWERON_MUSIC_DEFAULT,
    /// music1
    EN_POWERON_MUSIC_ONE,
    /// music num
    EN_POWERON_MUSIC_MAX,
} EN_POWERON_MUSIC_MODE;


/// OAD info
typedef struct
{
    /// flag to indicate kernel upgrade
    BOOL IsUpgradeKernel;
    /// flag to indicate ROOTFS upgrade
    BOOL IsUpgradeRootfs;
    /// flag to indicate MSLIB upgrade
    BOOL IsUpgradeMslib;
    /// flag to indicate APPLICATIONS upgrade
    BOOL IsUpgradeApplication;
    /// flag to indicate Config upgrade
    BOOL IsUpgradeConfig;
    /// future use
    char* acPath;
} USBUpgradeCfg;

enum
{
    E_USB_UPGRADE_KERNEL,
    E_USB_UPGRADE_ROOTFS,
    E_USB_UPGRADE_MSLIB,
    E_USB_UPGRADE_APPLICATION,
    E_USB_UPGRADE_CONFIG,
    E_USB_UPGRADE_START
};

#if (STB_ENABLE == 1)
enum
{

    E_NoSignal_MaxReTryCount = 5,
    E_NoSignal_State,
};

enum
{
    E_Lock_MaxReTryCount = 0,
    E_Lock_State,
};
#endif

enum
{
    SCART_MONITOR_AND_TV_MODE =1,
    SCART_TV_AND_NONE_MODE,
    SCART_MONITOR_AND_NONE_MODE,
    SCART_MODE_COUNT = 3
};


typedef enum
{
    /// flag to indicate all block off.
    EN_AUDIO_ALL_BLOCK_OFF = 0x0,
    /// flag to indicate input block on.
    EN_AUDIO_INPUT_BLOCK_ON,
    /// flag to indicate input block off.
    EN_AUDIO_INPUT_BLOCK_OFF,
    /// flag to indicate vchip block on.
    EN_AUDIO_VCHIP_BLOCK_ON,
    /// flag to indicate vchip block off.
    EN_AUDIO_VCHIP_BLOCK_OFF,
} EN_AUDIO_BLOCK_STATUS;

/// monitor thread information
typedef struct
{
    /// the pointer to the object which create the thread
    void* p_class;
    /// flag to indicate the thread active
    BOOL m_bFlagThreadMonitorActive;
} Monitor_t;

///OAD upgrade config
typedef struct
{
    /// control mboot to display OAD upgrade OSD or not.
    BOOL bDisplayUpgradeOSD;
    /// control mboot to reboot or sleep after OAD upgraded
    BOOL bRebootAfterUpgrade;
} ST_OAD_UPDGRADE_CONFIG;

#if (MODULE_TEST == 1)
typedef enum
{
    EN_PROFILE_CP_CC_BEGIN=0,
    EN_PROFILE_CP_CC_END,
    EN_PROFILE_CP_DC_BEGIN,
    EN_PROFILE_CP_SI_STOP,
    EN_PROFILE_CP_VDECSTOP,
    EN_PROFILE_CP_VIDEOMUTE,
    EN_PROFILE_CP_DC_END,
    EN_PROFILE_CP_EC_BEGIN,
    EN_PROFILE_CP_VDECSETVDECTYPE,
    EN_PROFILE_CP_AUDPLAY,
    EN_PROFILE_CP_SPS,
    EN_PROFILE_CP_SETWINDOW,
    EN_PROFILE_CP_PICON,
    EN_PROFILE_CP_VIDEOUNMUTE,
    EN_PROFILE_CP_VE_ON,
    EN_PROFILE_CP_DOSETCOUNT,
    EN_PROFILE_CP_CUR_VPID,
    EN_PROFILE_CP_RESVERD,
} EN_PROFILE_CHECKPOINT;

/// define thread arg information
typedef struct
{
    //Channel Change
    U32 m_u32CC_begin;
    U32 m_u32CC_end;
    //Disable Channel
    U32 m_u32DC_begin;
    U32 m_u32DC_SI_Stop;
    U32 m_u32DC_VDecStop;
    U32 m_u32DC_videoMute;
    U32 m_u32DC_end;
    U32 m_u32EC_begin;

    U32 m_u32_VDecSetVdecType;
    U32 m_u32_audPlay;
    U32 m_u32_SPS;
    U32 m_u32_setWindow;
    U32 m_u32_picOn;
    U32 m_u32_videoUnmute;
    U32 m_u32_VE_On;
    U32 DoSetCount;
    U32 cur_vpid;
    U32 m_u32_reserved_checkpoint[10];
}ST_PROFILE_CONTAINER;
#endif

/// Heart beat interval
#define THREAD_HEART_BEAT_TIME      1000
/// Heart beat status
typedef enum
{
    /// Alive
    EN_THREAD_HEART_BEAT_ALIVE,
    /// finalize
    EN_THREAD_HEART_BEAT_FINALIZE,
    /// Suspend
    EN_THREAD_HEART_BEAT_MONITOR_SUSPEND,
    /// Resume
    EN_THREAD_HEART_BEAT_MONITOR_RESUME,
} EN_THREAD_HEART_BEAT_STATUS;

/// thread heart beat info
typedef struct
{
    /// heart beat status
    EN_THREAD_HEART_BEAT_STATUS enHearBeatStatus;
    /// thread instance
    pthread_t thread;
    /// thread name
    string sName;
    /// thread ID
    U32 threadId;
    /// heart beat time
    U32 u32Time;
} THREAD_HEART_BEAT_INFO;

#if ( FREEVIEW_AU_ENABLE == 1 )
/// customer oad info
typedef struct
{
    /// 3 bytes OUI
    U32 u32OUI;
    /// 2 bytes HW model
    U16 u16HWModel;
    /// 2 bytes HW version
    U16 u16HWVersion;
    /// 2 bytes SW model
    U16 u16SWAPModel;
    /// 2 bytes SW version
    U16 u16SWAPVersion;
}ST_CUSTOMER_OAD_INFO;
#endif

/// PIP mode setting
typedef enum
{
    /// PIP mode off
    E_PIP_MODE_OFF = 0,
    /// pip mode on
    E_PIP_MODE_PIP,
    /// pip mode pop
    E_PIP_MODE_POP,
    /// pip mode Traveling, only means E_TRAVELING_2ND_VIDEO case
    E_PIP_MODE_TRAVELING,
} EN_PIP_MODES;

#if (PIP_ENABLE == 1)
typedef enum
{
    E_PIP_NOT_SUPPORT,
    E_PIP_WINDOW_SETTING_ERROR,
    E_PIP_SUCCESS,
    E_PIP_PIP_MODE_OPENED,
    E_PIP_POP_MODE_OPENED,
    E_PIP_TRAVELING_MODE_OPENED,
    E_PIP_3D_MODE_OPENED,
    E_PIP_INPUT_PARAMETER_ERROR,
    E_PIP_FAIL_UNKNOWN, //For unknown or strange error
} EN_PIP_RETURN;

/// define the PIP sub input source enum
typedef enum
{
    EN_PIP_SUB_SOURCE_NONE = 0,
    EN_PIP_SUB_SOURCE_DVBT,
    EN_PIP_SUB_SOURCE_DVBC,
    EN_PIP_SUB_SOURCE_DVBS,
    EN_PIP_SUB_SOURCE_ISDB,
    EN_PIP_SUB_SOURCE_ATV,
    EN_PIP_SUB_SOURCE_SCART,
    EN_PIP_SUB_SOURCE_SCART2,
    EN_PIP_SUB_SOURCE_SCART_MAX,
    EN_PIP_SUB_SOURCE_YPBPR,
    EN_PIP_SUB_SOURCE_YPBPR2,
    EN_PIP_SUB_SOURCE_YPBPR3,
    EN_PIP_SUB_SOURCE_YPBPR_MAX,
    EN_PIP_SUB_SOURCE_VGA,
    EN_PIP_SUB_SOURCE_HDMI,
    EN_PIP_SUB_SOURCE_HDMI2,
    EN_PIP_SUB_SOURCE_HDMI3,
    EN_PIP_SUB_SOURCE_HDMI4,
    EN_PIP_SUB_SOURCE_HDMI_MAX,
    EN_PIP_SUB_SOURCE_CVBS,
    EN_PIP_SUB_SOURCE_CVBS2,
    EN_PIP_SUB_SOURCE_CVBS3,
    EN_PIP_SUB_SOURCE_CVBS4,
    EN_PIP_SUB_SOURCE_CVBS5,
    EN_PIP_SUB_SOURCE_CVBS6,
    EN_PIP_SUB_SOURCE_CVBS7,
    EN_PIP_SUB_SOURCE_CVBS8,
    EN_PIP_SUB_SOURCE_CVBS_MAX,
    EN_PIP_SUB_SOURCE_SVIDEO,
    EN_PIP_SUB_SOURCE_SVIDEO2,
    EN_PIP_SUB_SOURCE_SVIDEO3,
    EN_PIP_SUB_SOURCE_SVIDEO4,
    EN_PIP_SUB_SOURCE_SVIDEO_MAX,
    EN_PIP_SUB_SOURCE_VGA2,
    EN_PIP_SUB_SOURCE_VGA3,
    EN_PIP_SUB_SOURCE_NUM
} EN_PIP_SUB_SOURCE_LIST;
#endif

#if (CEC_ENABLE == 1)
/// CEC Thread Parameters structure
typedef struct ST_INPUTSOURCE
{
    MAPI_INPUT_SOURCE_TYPE CurrInputSrc;
    MAPI_INPUT_SOURCE_TYPE OriInputSrc;
}ST_INPUTSOURCE;
#endif

/// Request System Control to async execute cmd
typedef enum
{
    /// None
    E_TV_CMD_NONE =0,
    /// Switch Route Request from CI
    E_TV_CMD_SWITCH_ROUTE_CI ,
    /// Switch CI slot Request from CI
    E_TV_CMD_SWITCH_DTV_CI_SLOT ,
    /// MAX
    E_TV_CMD_MAX
}EN_TV_ASYNC_CMD;

/// Notify Complete Callback function interface for Request System Control to async execute cmd
typedef void (*MSrvAsyncCmdCallBack)(void *param1, void *param2);

/// Event for Request System Control to async execute cmd
typedef struct
{
    /// commend
    EN_TV_ASYNC_CMD enCmd;
    /// call back parameter
    MSrvAsyncCmdCallBack pCallback;
    /// parameter
    MAPI_U32 u32Param1;
    /// parameter
    MAPI_U32 u32Param2;
} MSRV_TV_ASYNC_CMD_EVENT;

#if (ACTIVE_STANDBY_MODE_ENABLE == 1)
//task which was in the ActiveStandbyMode.
typedef enum
{
    E_ACTIVE_STANDBY_TASK_PVR = 0,
    E_ACTIVE_STANDBY_TASK_STANDBY_SCAN,
    E_ACTIVE_STANDBY_TASK_OAD,
    E_ACTIVE_STANDBY_TASK_MHL,
    E_ACTIVE_STANDBY_TASK_NUM,
}EN_ACTIVE_STANDBY_MODE_TASK;

typedef struct
{
    EN_ACTIVE_STANDBY_MODE_TASK TaskId;
    BOOL bInActiveStandby;
    BOOL bFinished;
    U8     u8TaskPriority;
}ST_ACTIVE_STANDBY_TASK_LIST;

typedef enum
{
    /// None
    E_ACTIVE_STANDBY_CMD_NONE =0,
    /// Set Active Standby Mode
    E_ACTIVE_STANDBY_CMD_SET_ACTIVE_STANDBY,
    /// MAX
    E_ACTIVE_STANDBY_CMD_MAX
}EN_ACTIVE_STANDBY_CMD;

#endif

///Mute Engine define
typedef enum
{
    ENGINE_XC = 0,
    ENGINE_SOC,
    ENGINE_BACKEND,
    ENGINE_BACKEND_VIDEO_ONLY,
    ENGINE_NONE,
} EN_MUTE_ENGINE;

/// define thread arg information
typedef struct
{
    /// class
    void * pclass;
    /// active
    BOOL bActive;
}ST_THREAD_ARG_INFO;

/// supported features
typedef enum
{
    E_MSRV_SUPPORTED_FEATURE_INVALID = 0,
    E_MSRV_SUPPORTED_FEATURE_4K2K_PIP,
    E_MSRV_SUPPORTED_FEATURE_MAX,
}EN_MSRV_SUPPORTED_FEATURES;

/// Boot IR key
typedef enum
{
    /// None
    EN_BOOT_KEY_NONE = 0,
    /// Power on by power key
    EN_BOOT_KEY_POWER_KEY,
    /// Power on by netflix key
    EN_BOOT_KEY_NETFLIX_KEY
} EN_MSRV_BOOT_KEY;

#if (INPUT_SOURCE_LOCK_ENABLE == 1)
/// Input source lock(ISL) event data
typedef enum
{
    E_INPUT_SOURCE_LOCK_EVENT_ON,
    E_INPUT_SOURCE_LOCK_EVENT_OFF,
    E_INPUT_SOURCE_LOCK_EVENT_UNDEFINED = 0xFF
} EN_INPUT_SOURCE_LOCK_EVENT_DATA;
#endif

/// Lock output timing operation.
typedef enum
{
    E_OP_LOCK_TIMING_STATE_NONE = 0,
    E_OP_LOCK_TIMING_STATE_LOCK_TIMING,
    E_OP_LOCK_TIMING_STATE_UNLOCK_TIMING,
    E_OP_LOCK_TIMING_STATE_MAX
} EN_OP_LOCK_TIMING_STATE;

/// Set output timing operation. Bit wise function
/// Each bit represents one operation.
typedef enum
{
    E_SET_OP_TIMING_OPERATION_NO_OPERATION = 0,
    E_SET_OP_TIMING_OPERATION_SAVE_TIMING = 1,
    E_SET_OP_TIMING_OPERATION_MAX
} EN_SET_OP_TIMING_OPERATION;

// Below define is used to control wether to run av output related flow:
// "VE Traveling" can not enable av out
#define IsVideoOutFreeToUse() (MSrv_Control::GetInstance()->IsTravelingModeEnable(E_TRAVELING_ENGINE_TYPE_SD) == FALSE)
// "PIP" or "sub win Traveling" can not enable tv mode's av out (for audio resource conflict)
// Note: video out tv mode=always output ATV source, monitor mode= output main source in av/atv/dtv case
#define IsVideoOutTVModeFreeToUse() (IsVideoOutFreeToUse() && (MSrv_Control::GetInstance()->IsPipModeEnable() == FALSE))

///define Allocate check marco
#define MSRV_CONTROL_ALLOC_CHK(x)           \
    {                                       \
        ASSERT((x) != NULL);                \
        if((x) == NULL)                     \
            goto CREATE_ERR_EXIT;           \
    }

/// MSrv_Loader_Info
class MSrv_Loader_Info
{
public:
    /// sboot version
    U8 pSbootVersion[16];
    /// uboot version
    U8 pUbootVersion[16];
    /// reserved for future use
    void *reserv;
};

/// Define struct of conflict state
typedef struct
{
    /// The string of conflict message
    string strConflictMsg;
} ST_CONFLICT_MSG;


/// the base class for controlling the whole MSrv layer
class MSrv_Control_common : public MSrv
{
#if (ACTIVE_STANDBY_MODE_ENABLE == 1)
    DECLARE_EVENT_MAP();
#endif
public:
    // ------------------------------------------------------------
    // public operations
    // ------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------
    /// start send ir key
    /// @return MAPI_U32:Success 0 Else Error
    //-------------------------------------------------------------------------------------------------
    static MAPI_U32 SendIRKeyStart(void);
    //-------------------------------------------------------------------------------------------------
    /// send ir key
    /// @param i32sendkeyvalue     \b MAPI_U32: i32sendkeyvalue
    /// @return MAPI_U32:Success 0 Else Error
    //-------------------------------------------------------------------------------------------------
    static MAPI_U32 SendIRKey(MAPI_S32 i32sendkeyvalue);
    //-------------------------------------------------------------------------------------------------
    /// stop send ir key
    /// @return MAPI_U32:Success 0 Else Error
    //-------------------------------------------------------------------------------------------------
    static MAPI_U32 SendIRKeyStop(void);
    //-------------------------------------------------------------------------------------------------
    /// Destructor of MSrv_Control_common.
    /// @return  None
    //-------------------------------------------------------------------------------------------------
    virtual ~MSrv_Control_common();

protected:
    //-------------------------------------------------------------------------------------------------
    /// Clear browser's cache, cookie, local storage and application cache after reset to factory default.
    /// @return MAPI_TRUE for successfully clear else fail.
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL browserClearLocalStorage(void);

    /// define the MSrv enum
    typedef enum
    {
        // @IMPORTANT: MSRV_SYSTEM_DATABASE must be create at first and destroy at the last one
        /// System database
        E_MSRV_SYSTEM_DATABASE = 0,
        /// ATV database
        E_MSRV_ATV_DATABASE,
        /// CIMMI
        E_MSRV_CIMMI,
        /// PVR browser
        E_MSRV_PVRBROWSER,
        /// picture setting
        E_MSRV_PICTURE,
        /// factory mode
        E_MSRV_FACTORY,
        /// Timer
        E_MSRV_TIMER,
        /// sound effect
        E_MSRV_SSSOUND,
        /// networking
        E_MSRV_NETWORK,
#if (CEC_ENABLE == 1)
        /// HDMI CEC
        E_MSRV_CEC,
#endif
        /// NETFLIX
        E_MSRV_ISYSTEM,
        /// ORMCRYPTO
        E_MSRV_OEMCRYPTO,
        /// VUDUDRM
        E_MSRV_VUDUDRM,
        /// SECURESYS
        E_MSRV_SECURESYS,
        /// SECURESTORAGE
        E_MSRV_SECURE_STORAGE,
        /// PRESHAREDKEY
        E_MSRV_PRESHAREDKEY,
        /// SECURE
        E_MSRV_SECURE,
#if (AUTO_TEST == 1)
        /// auto test
        E_MSRV_AUTO_TEST,
#endif
        /// source detection
        E_MSRV_SRC_DET,
        /// DTV channel manager
        E_MSRV_CHANNEL_MANAGER,

#if (STEREO_3D_ENABLE == 1)
        /// 3D Manager
        E_MSRV_3DMANAGER,
#endif
        /// DivX DRM
        E_MSRV_DIVX_DRM,
#if (MHL_ENABLE == 1)
        /// HDMI MHL
        E_MSRV_MHL,
#endif //MHL_ENABLE
        /// backlight control
        E_MSRV_BACKLIGHT_CONTROL,
        /// Recording Scheduler, dealing with PVR bookings.
        E_MSRV_RECORDINGSCHEDULER,
#if (CA_ENABLE == 1)//colin@2012-0302
                /// ca
       E_MSRV_CA,
#endif
#if (ENABLE_NETREADY == 1)
        /// The Name of updateLogic updateTV
        E_MSRV_DEVICE_AGENT,
#endif
        /// Advertisment Player
        E_MSRV_ADVERT_PLAYER,
        /// CIMMI extend
        E_MSRV_CIMMI_EX,
        /// video setting
        E_MSRV_VIDEO,
#if (ENABLE_BACKEND == 1)
        ///Ursa funtion
        E_MSRV_BACKEND,
#endif
#if (HDMITX_ENABLE == 1)
        /// HDMI TX
        E_MSRV_HDMITX,
#endif //HDMITX_ENABLE
        /// the number of MSrv type
        E_MSRV_MAX,
        /// Invalid indicator
        E_MSRV_NULL = E_MSRV_MAX,
    } EN_MSRV_LIST;

    /// define the MSrvPlayer enum
    typedef enum
    {
        /// DTV player 0
        E_MSRV_DTV_PLAYER_0 = 0,
        /// DTV player 1
        E_MSRV_DTV_PLAYER_1,
        /// DTV player 2
        E_MSRV_DTV_PLAYER_2,
        /// DTV player 3
        E_MSRV_DTV_PLAYER_3,
        /// ATV platyer
        E_MSRV_ATV_PLAYER,
        /// Scart player
        E_MSRV_SCART_PLAYER,
        /// AV player
        E_MSRV_AV_PLAYER,
        /// S-Video player
        E_MSRV_SV_PLAYER,
        /// Component player
        E_MSRV_COMPONENT_PLAYER,
        /// HDMI player
        E_MSRV_HDMI_PLAYER,
        /// VGA player 0
        E_MSRV_VGA_PLAYER,
        /// storage database
        E_MSRV_STORAGE_PLAYER,
        /// storage database_2
        E_MSRV_STORAGE_PLAYER_2,
        /// RVU player
        E_MSRV_RVU_PLAYER,
        /// the number of Player type
        E_MSRV_PLAYER_MAX,
        /// Invalid indicator
        E_MSRV_PLAYER_NULL = E_MSRV_PLAYER_MAX,
    } EN_MSRV_PLAYER_LIST;

#if (PIP_ENABLE == 1) || (TWIN_TUNER == 1)
    /// Define the Conflict type
    typedef enum
    {
        /// Not conflict
        EN_CONFLICT_NONE = 0,
        /// DTV Route conflict
        EN_CONFLICT_ROUTE,
        /// Input source conflict
        EN_CONFLICT_SOURCE,
        /// Background route conflict
        EN_CONFLICT_BACKGROUND,
        /// Foreground record conflict
        EN_CONFLICT_FGRECORD
    } EN_CONFLICT_TYPE;
#endif

    typedef enum
    {
        /// route 0
        E_MSRV_DTV_ROUTE_0 = 0, // E_MSRV_DTV_PLAYER_0
        /// route 1
        E_MSRV_DTV_ROUTE_1, // E_MSRV_DTV_PLAYER_1
        /// route 2
        E_MSRV_DTV_ROUTE_2, // E_MSRV_DTV_PLAYER_2
        /// route 3
        E_MSRV_DTV_ROUTE_3, // E_MSRV_DTV_PLAYER_3
        /// route 4
        E_MSRV_DTV_ROUTE_MAX = MAXROUTECOUNT
    } EN_MSRV_DTV_ROUTE;

    typedef struct
    {
        /// DTV route type
        MAPI_U8 u8DTV_RouteType;
        /// DTV route match frontend,it will alaways 0 expect have same player.
        MAPI_U8 u8DTV_FrontEnd;
        /// this Route is active or not
        MAPI_BOOL bIsActive;
    } MSRV_DTV_ROUTEINFO;

    /// Define the PlayerState struct
    typedef struct
    {
        /// PIP current input source vector
        vector<MAPI_INPUT_SOURCE_TYPE> v_PIPSource;
        /// PIP current dtv route vector
        vector<U8> v_PIPRoute;
        /// PVR current dtv route vector
        vector<U8> v_PVRRoute;
    } ST_PLAYER_STATE;

    /// The class for maintain all player state
    class MSrv_PlayerControl
    {
    private:
        // ------------------------------------------------------------
        // private operations
        // ------------------------------------------------------------

#if (PIP_ENABLE == 1) || (TWIN_TUNER == 1)
        /// Define tuner table struct, support which ones signal type
        typedef struct
        {
            ///Tuner have support analogy signal
            BOOL bATVSupport;
            ///Tuner have support which ones digital signal
            vector<EN_DTV_TYPE> v_stDTVsRoute;
        } ST_TUNER_TABLE;

        /// The class for base config table
        class MSrv_PlayerConfigTable
        {
        public:
            // ------------------------------------------------------------
            // public operations
            // ------------------------------------------------------------

            //-------------------------------------------------------------------------------------------------
            /// Constructor of MSrv_PlayerConfigTable
            /// @return  None
            //-------------------------------------------------------------------------------------------------
            MSrv_PlayerConfigTable();

            //-------------------------------------------------------------------------------------------------
            /// Check state is conflict, and feedback proposal state
            /// @param stPlayerState \b IN: want be checked state struct
            /// @return BOOL       \b OUT: true is conflict, false is not conflict
            //-------------------------------------------------------------------------------------------------
            virtual BOOL CheckConflict(ST_PLAYER_STATE& stPlayerState) const = 0;

        protected:
            // ------------------------------------------------------------
            // protected operations
            // ------------------------------------------------------------
            const vector<ST_TUNER_TABLE>* GetTunerModeTable(void) const;

        private:
            // ------------------------------------------------------------
            // private operations
            // ------------------------------------------------------------

            //-------------------------------------------------------------------------------------------------
            /// Read tuner mode table from system information
            /// @return BOOL       \b OUT: true is success, false is failure
            //-------------------------------------------------------------------------------------------------
            BOOL SetTunerModeTable(void);

            /// The vector of tuner table struct
            static vector<ST_TUNER_TABLE> *m_pTunerTable;
        };

        /// The class for tuner config table
        class MSrv_PlayerConfigTable_Tuner : MSrv_PlayerConfigTable
        {
        public:
            // ------------------------------------------------------------
            // public operations
            // ------------------------------------------------------------

            //-------------------------------------------------------------------------------------------------
            /// Get the instance of MSrv_PlayerConfigTable_Tuner
            /// @return  the pointer to the instance
            //-------------------------------------------------------------------------------------------------
            static MSrv_PlayerConfigTable_Tuner* GetInstance(void);

            //-------------------------------------------------------------------------------------------------
            /// Check state is conflict, and feedback proposal state
            /// @param stPlayerState \b IN: want be checked state struct
            /// @return BOOL       \b OUT: true is conflict, false is not conflict
            //-------------------------------------------------------------------------------------------------
            BOOL CheckConflict(ST_PLAYER_STATE& stPlayerState) const;

        private:
            // ------------------------------------------------------------
            // private operations
            // ------------------------------------------------------------

            //-------------------------------------------------------------------------------------------------
            /// Constructor of MSrv_PlayerConfigTable_Tuner
            /// @return  None
            //-------------------------------------------------------------------------------------------------
            MSrv_PlayerConfigTable_Tuner();

            /// The point of singleton object
            static MSrv_PlayerConfigTable_Tuner* m_pInstance;
        };

        /// The class for PIP config table
        class MSrv_PlayerConfigTable_PIP : MSrv_PlayerConfigTable
        {
        public:
            // ------------------------------------------------------------
            // public operations
            // ------------------------------------------------------------

            //-------------------------------------------------------------------------------------------------
            /// Get the instance of MSrv_PlayerConfigTable_PIP
            /// @return  the pointer to the instance
            //-------------------------------------------------------------------------------------------------
            static MSrv_PlayerConfigTable_PIP* GetInstance(void);

            //-------------------------------------------------------------------------------------------------
            /// Check state is conflict, and feedback proposal state
            /// @param stPlayerState \b IN: want be checked state struct
            /// @return BOOL       \b OUT: true is conflict, false is not conflict
            //-------------------------------------------------------------------------------------------------
            BOOL CheckConflict(ST_PLAYER_STATE& stPlayerState) const;

        private:
            // ------------------------------------------------------------
            // private operations
            // ------------------------------------------------------------

            /// The map of main and sub input source be support
            map< MAPI_INPUT_SOURCE_TYPE, vector<MAPI_INPUT_SOURCE_TYPE> > m_conflict;

            /// The point of singleton object
            static MSrv_PlayerConfigTable_PIP* m_pInstance;
        };
#endif

    public:
        // ------------------------------------------------------------
        // public operations
        // ------------------------------------------------------------

        //-------------------------------------------------------------------------------------------------
        /// Get the instance of MSrv_PlayerControl
        /// @return  the pointer to the instance
        //-------------------------------------------------------------------------------------------------
        static MSrv_PlayerControl* GetInstance(void);

        //-------------------------------------------------------------------------------------------------
        /// Get the DTV route total number, the number depend on board define
        /// @return  the number of DTV route total number
        //-------------------------------------------------------------------------------------------------
        U8 GetDtvRouteCount(void);

        //-------------------------------------------------------------------------------------------------
        /// Get the DTV route index by window
        /// @param eWin \b IN: Window index
        /// @return  the route index of DTV
        //-------------------------------------------------------------------------------------------------
        U8 GetCurrentDtvRoute(MAPI_SCALER_WIN eWin = MAPI_MAIN_WINDOW);

#if (VE_ENABLE == 1 || CVBSOUT_ENABLE == 1)
        //-------------------------------------------------------------------------------------------------
        /// Init scart out
        /// @param eOldInputSrc \b IN: previous input source
        /// @param eInputSrc \b IN: current input source
        /// @return BOOL       \b OUT: true is success, false is failure
        //-------------------------------------------------------------------------------------------------
        BOOL InitScartOut(MAPI_INPUT_SOURCE_TYPE eOldInputSrc, MAPI_INPUT_SOURCE_TYPE eInputSrc) const;

        //-------------------------------------------------------------------------------------------------
        /// Finalize scart out
        /// @param eOldInputSrc \b IN: previous input source
        /// @param eInputSrc \b IN: current input source
        /// @return BOOL       \b OUT: true is success, false is failure
        //-------------------------------------------------------------------------------------------------
        BOOL FinalizeScartOut(MAPI_INPUT_SOURCE_TYPE eOldInputSrc, MAPI_INPUT_SOURCE_TYPE eInputSrc) const;
#endif

        //-------------------------------------------------------------------------------------------------
        /// Create all msrv players
        /// @return BOOL       \b OUT: true is success, false is failure
        //-------------------------------------------------------------------------------------------------
        BOOL CreateAllMSrvPlayers(void);

        //-------------------------------------------------------------------------------------------------
        /// Get current player state
        /// @param stPlayerState \b IN: Player state struct
        /// @return BOOL       \b OUT: true is success, false is failure
        //-------------------------------------------------------------------------------------------------
        BOOL GetPlayerState(ST_PLAYER_STATE& stPlayerState);

        //-------------------------------------------------------------------------------------------------
        /// Set current PIP input source for main or sub window
        /// @param eSource \b IN: Input source
        /// @param eWin \b IN: Window index main or sub
        /// @return BOOL       \b OUT: true is success, false is failure
        //-------------------------------------------------------------------------------------------------
#if (MSTAR_TVOS == 1)
        BOOL SetPIPSourceType(MAPI_INPUT_SOURCE_TYPE eSource, MAPI_SCALER_WIN eWin = MAPI_MAIN_WINDOW);
#endif
        //-------------------------------------------------------------------------------------------------
        /// Set current input source or dtv route for main or sub window
        /// @param stPlayerState \b IN: Want be changed player state
        /// @return BOOL       \b OUT: true is success, false is failure
        //-------------------------------------------------------------------------------------------------
        BOOL SwitchInputSource(const ST_PLAYER_STATE& stPlayerState);

        //-------------------------------------------------------------------------------------------------
        /// Get conflict message
        /// @param stConflictMsg \b IN: Conflict message struct
        /// @return BOOL       \b OUT: true is success, false is failure
        //-------------------------------------------------------------------------------------------------
        BOOL GetConflictMessage(ST_CONFLICT_MSG& stConflictMsg);

        //-------------------------------------------------------------------------------------------------
        /// Set DTV record route and program
        /// @param stService \b IN: Record program triple ID
        /// @param u8RouteIndex \b IN: Record DTV route index
        /// @return BOOL       \b OUT: true is success, false is failure
        //-------------------------------------------------------------------------------------------------
        BOOL SetRecordServiceByRoute(const ST_TRIPLE_ID &stService, const U8 u8RouteIndex, BOOL bForceSet = TRUE);

        //-------------------------------------------------------------------------------------------------
        /// Start DTV record by route
        /// @param u8RouteIndex \b IN: Record DTV route index
        /// @param u16CachedPinCode \b IN: Cached PIN code
        /// @return BOOL       \b OUT: true is success, false is failure
        //-------------------------------------------------------------------------------------------------
        U8 StartRecordRoute(U8 u8DtvRouteIndex, U16 u16CachedPinCode = 0xFFFF);

        //-------------------------------------------------------------------------------------------------
        /// Stop DTV record by route
        /// @param u8RouteIndex \b IN: Record DTV route index
        /// @return BOOL       \b OUT: true is success, false is failure
        //-------------------------------------------------------------------------------------------------
        BOOL StopRecordRoute(U8 u8DtvRouteIndex);

#if (PIP_ENABLE == 1) || (TWIN_TUNER == 1)
        //-------------------------------------------------------------------------------------------------
        /// Get conflict message
        /// @param stConflictMsg \b IN: Conflict message struct
        /// @return EN_CONFLICT_TYPE       \b OUT: Conflict type
        //-------------------------------------------------------------------------------------------------
        EN_CONFLICT_TYPE CheckConflict(ST_PLAYER_STATE& stPlayerState);
#endif

        //-------------------------------------------------------------------------------------------------
        /// Get current PIP input source for main or sub window
        /// @param eWin \b IN: Window index main or sub
        /// @return MAPI_INPUT_SOURCE_TYPE       \b OUT: Input source type
        //-------------------------------------------------------------------------------------------------
        MAPI_INPUT_SOURCE_TYPE GetPIPSourceType(MAPI_SCALER_WIN eWin = MAPI_MAIN_WINDOW);

        //-------------------------------------------------------------------------------------------------
        /// Get msrv player
        /// @param eMSrvPlayer \b IN: Player enum
        /// @return MSrv_Player       \b OUT: Msrv player
        //-------------------------------------------------------------------------------------------------
        MSrv_Player* GetMSrvPlayer(EN_MSRV_PLAYER_LIST eMSrvPlayer);

        //-------------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------
        /// Check DTV route in foreground
        /// @param stPlayerState \b IN: Player state struct
        /// @param u8DtvRouteIndex \b IN: DTV route index
        /// @return BOOL       \b OUT: true is success, false is failure
        //-------------------------------------------------------------------------------------------------
        BOOL CheckDtvRouteInForeground(const ST_PLAYER_STATE& stPlayerState, U8 u8DtvRouteIndex) const;

    private:
        // ------------------------------------------------------------
        // private operations
        // ------------------------------------------------------------

        //-------------------------------------------------------------------------------------------------
        /// Constructor of MSrv_PlayerControl
        /// @return  None
        //-------------------------------------------------------------------------------------------------
        MSrv_PlayerControl();

        //-------------------------------------------------------------------------------------------------
        /// Destructor of MSrv_PlayerControl
        /// @return  None
        //-------------------------------------------------------------------------------------------------
        ~MSrv_PlayerControl();

        BOOL SetPlayerState(const ST_PLAYER_STATE& stPlayerState);
        BOOL UpdateRecordStatusNotify(void);

        static MSrv_PlayerControl* m_pInstance;
        static MSrv *m_pMSrvPlayerList[E_MSRV_PLAYER_MAX];

        EN_PVR_RECORD_STATUS m_enPVRStatus;
        ST_PLAYER_STATE m_stPlayerState;
        ST_CONFLICT_MSG m_stConflictMsg;
        U8 m_u8DtvRouteCount;
        const MAPI_VIDEO_INPUTSRCTABLE* m_pInputSrcTable;
        pthread_mutex_t m_MutexPlayerControl;
    };

public:
    // ------------------------------------------------------------
    // public operations
    // ------------------------------------------------------------
#if(MSTAR_TVOS == 1)
    //-------------------------------------------------------------------------------------------------
    /// excute the pathname command,the function returns until end of the command execution
    /// @param pathname: command path
    /// @param argv: command param
    /// @return     \b 0: Operation success, or \b -1: Operation failure.
    //-------------------------------------------------------------------------------------------------
    U8 Command_execv(const char *pathname, char * const argv[]);
#endif

#if (PREVIEW_MODE_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Capture screen image from OP+OSD by using Traveling Mode
    /// @return     \b TRUE: Operation success, \b FALSE: Operation failure.
    //-------------------------------------------------------------------------------------------------
    BOOL TravelingModeCaptureImage(void);

    //-------------------------------------------------------------------------------------------------
    /// Enable Preview Mode Thread
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void EnablePreviewModeThread();

    //-------------------------------------------------------------------------------------------------
    /// Disable Preview Mode
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void DisablePreviewModeThread();

    //-------------------------------------------------------------------------------------------------
    /// Set First Preview Mode Input Source
    /// @param enInputSourceType   \b MAPI_INPUT_SOURCE_TYPE: Input Source
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetFirstPreviewModeInputSource(MAPI_INPUT_SOURCE_TYPE enInputSourceType);
#endif//#if (PREVIEW_MODE_ENABLE == 1)

#if (ENABLE_LITE_SN != 1)
    //-------------------------------------------------------------------------------------------------
    /// Get Enable IP Mapping
    /// @param BitTable   \b MAPI_U8: Bit Table
    /// @param BitTableLen \b IN: the length of bit table
    /// @return None
    //-------------------------------------------------------------------------------------------------
     virtual void GetEnableIPInfo(MAPI_U8 * BitTable,const int  BitTableLen);
#endif

    //-------------------------------------------------------------------------------------------------
    /// Get Customer info
    /// @param Customer_info     \b MAPI_U8: Customer info
    /// @param CustomerInfoLen \b IN: the length of Customer info
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetCustomerInfo(MAPI_U8 * Customer_info,const int CustomerInfoLen);

    //-------------------------------------------------------------------------------------------------
    /// Get audio mute flag
    /// @return MAPI_BOOL       \b IN: 1 is on, 0 is Off
    //-------------------------------------------------------------------------------------------------
    virtual MAPI_BOOL GetMuteFlag();

    //-------------------------------------------------------------------------------------------------
    /// Set audio mute flag
    /// @param IsMute           \b IN: On: 1, Off: 0
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetMuteFlag(MAPI_BOOL IsMute);

     //-------------------------------------------------------------------------------------------------
    /// Toggle audio mute
    /// @return SSSOUND_OK: Success
    //-------------------------------------------------------------------------------------------------
    virtual MAPI_BOOL ToggleMute();

    //-------------------------------------------------------------------------------------------------
    /// Notify audio mute
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void NotifyMute();

    //-------------------------------------------------------------------------------------------------
    /// Notify audio unmute
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void NotifyUnmute();

    //-------------------------------------------------------------------------------------------------
    /// Set audio volume change
    /// @param keycode          \b IN: volume Keycode value
    /// @return SSSOUND_OK: Success
    //-------------------------------------------------------------------------------------------------
    virtual MAPI_BOOL APVolumeChange(U32 keycode);

    //-------------------------------------------------------------------------------------------------
    /// Set audio volume up
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void APVolumeUp();

    //-------------------------------------------------------------------------------------------------
    /// Set audio volume down
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void APVolumeDown();

    //-------------------------------------------------------------------------------------------------
    /// Set audio volume
    /// @param u8Vol                    \b IN: Vol 0~100
    ///
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void APSetVolume(U8 u8Vol);
    //-------------------------------------------------------------------------------------------------
    /// To Finalize all Database.
    /// @return                 \b TRUE: Finalize success, \b FALSE: Finalize failure.
    //-------------------------------------------------------------------------------------------------
    virtual BOOL FinalizeDB();
    //-------------------------------------------------------------------------------------------------
    /// Set Color for Video mute
    /// @param  enColor         \b IN: Color
    /// @param  engine              \b IN: mute engine
    /// @return                     None
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL setVideoMuteColor(mapi_video_datatype::MAPI_VIDEO_Screen_Mute_Color enColor, EN_MUTE_ENGINE engine = ENGINE_XC);
    //-------------------------------------------------------------------------------------------------
    /// Set Frame Color for Video
    /// @param  FrameColor         \b IN: Color
    /// @param  engine              \b IN: mute engine
    /// @return                     None
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL setVideoFrameColor(mapi_video_datatype::MAPI_VIDEO_Screen_Mute_Color FrameColor, EN_MUTE_ENGINE engine = ENGINE_XC);
    //-------------------------------------------------------------------------------------------------
    /// Get Video mute.
    /// @param  eWIN                \b IN: main or sub window
    /// @param  engine              \b IN: mute engine
    /// @return                     \b TRUE: Operation success, or \b FALSE: Operation failure.
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL GetVideoMute( MAPI_SCALER_WIN eWIN = MAPI_MAIN_WINDOW, EN_MUTE_ENGINE engine = ENGINE_XC);

    //-------------------------------------------------------------------------------------------------
    /// Set Video mute.
    /// @param  bVideoMute         \b IN: mute or unmute video.
    /// @param  u16VideoUnMuteTime \b IN: video unmute time.
    /// @param  eWIN                \b IN: main or sub window
    /// @param  engine              \b IN: mute engine
    /// @return                     \b TRUE: Operation success, or \b FALSE: Operation failure.
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetVideoMute(MAPI_BOOL bVideoMute , MAPI_U16 u16VideoUnMuteTime = 0,
                                            MAPI_SCALER_WIN eWIN = MAPI_MAIN_WINDOW,
                                            EN_MUTE_ENGINE engine = ENGINE_XC);
    //-------------------------------------------------------------------------------------------------
    /// Set Video mute.  (This API will be remove in next version)
    /// @param  bVideoMute         \b IN: mute or unmute video.
    /// @param  enColor             \b IN: mute video color.
    /// @param  u16VideoUnMuteTime \b IN: video unmute time.
    /// @param  eMapiSrcType        \b IN: The input source.
    /// @return                     \b TRUE: Operation success, or \b FALSE: Operation failure.
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetVideoMute(MAPI_BOOL bVideoMute,
                       mapi_video_datatype::MAPI_VIDEO_Screen_Mute_Color enColor,
                       MAPI_U16 u16VideoUnMuteTime ,
                       MAPI_INPUT_SOURCE_TYPE eMapiSrcType );

    //-------------------------------------------------------------------------------------------------
    /// Force Set Video mute.
    /// @param  bVideoMute          \b IN: mute or unmute video.
    /// @param  eInputSrcType       \b IN: input source
    /// @param  enMuteEngine        \b IN: mute engine
    /// @param  ...                 \b IN: Ext info
    /// @return                     \b TRUE: Operation success, or \b FALSE: Operation failure.
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetVideoMuteFromAPP(MAPI_BOOL bVideoMute, MAPI_INPUT_SOURCE_TYPE eInputSrcType = MAPI_INPUT_SOURCE_NONE, EN_MUTE_ENGINE enMuteEngine = ENGINE_XC, ...);

    //-------------------------------------------------------------------------------------------------
    /// Set autio mute.
    /// @param  eAudioMuteType      \b IN: mute type.
    /// @param  eMapiSrcType        \b IN: The input source.
    /// @return                     \b OUT: TRUE: Operation success, or FALSE: Operation failure.
    //-------------------------------------------------------------------------------------------------
    virtual BOOL SetAudioMute(MAPI_U16 eAudioMuteType = E_AUDIO_PERMANENT_MUTEOFF_,
                      MAPI_INPUT_SOURCE_TYPE eMapiSrcType = MAPI_INPUT_SOURCE_NONE);

    //-------------------------------------------------------------------------------------------------
    /// Reset all settings to factory default value.
    /// This API do the following jobs
    /// 1. Reloads default value to system database
    /// 2. Finalize class objects
    /// 3. Run reset script of APs
    /// 4. Sleep
    /// 5. Turn off panel
    /// 6. Reboot the system
    ///
    /// @return                 \b OUT: TRUE: Operation success, or FALSE: Operation failure.
    ///
    /// @code
    ///
    ///    GetParentApp()->Hide(0);
    ///    GetParentApp()->SuependIR();
    ///    MSrv_Control::GetInstance()->ResetToFactoryDefault();
    ///    // MainMenuFrame::MainMenu_LEFT_KEY() in Supernova/projects/ui/nebula/dvbt/src/MainMenuFrame.cpp
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual BOOL ResetToFactoryDefault(void)=0;

    //-------------------------------------------------------------------------------------------------
    /// Pre-build mapi_system class
    /// This API do the following jobs
    /// 1. Mask off all signals
    /// 2. Set compile flag for MSDK (?)
    /// 3. Initialize MAPI classes objects (mapi_video_customer, mapi_interface, mapi_pql_customer)
    /// 4. Initialize SystemInfo
    /// 5. Check cmdline from bootloader to decide if enter auto test or HSL
    ///
    /// @return   None
    ///
    /// @code
    ///
    ///    MSrv_Control_common::PreBuildSystem();
    ///    // MSrv_Control_TV::Build() in Supernova/projects/msrv/control/src/MSrv_Control_TV.cpp
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    static void PreBuildSystem();
    //-------------------------------------------------------------------------------------------------
    /// Post-build mapi_system class
    /// This API do the following jobs
    /// 1. Initialize System database
    /// 2. Load EDID
    /// 3. Pull all HPD
    ///
    /// @return   None
    ///
    /// @code
    //-------------------------------------------------------------------------------------------------
    static void PostBuildSystem();
    //-------------------------------------------------------------------------------------------------
    /// To finalize the MSrv_Control_common.
    /// @return                 \b TRUE: Finalize success, FALSE: Finalize failure.
    //-------------------------------------------------------------------------------------------------
    virtual BOOL Finalize();

    //-------------------------------------------------------------------------------------------------
    /// To control the poweroff flag
    /// @param bEnable           \b IN: the value of poweroff flag
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetPowerOffFlag(MAPI_BOOL bEnable);

    //-------------------------------------------------------------------------------------------------
    /// Set system into PM sleep mode (DC power off mode)
    /// This API do the following jobs
    /// 1. Set watchdog (For error handling of sleep fail)
    /// 2. Mute
    /// 3. Turn off back light
    /// 4. Enter MHL standby mode
    /// 5. Turn off power reset (GPIO)
    /// 6. Store DC off flag (Will be used by bootloader)
    /// 7. Flush data in system database into physical storage
    /// 8. Enter CEC standby mode
    /// 9. Reset IR/SAR function of PM
    /// 10. Configure PM wake up criteria
    ///
    /// @param bMode \b IN: MAPI_TRUE for enable Standby_Init(), MAPI_FALSE for disable Standby_Init()
    /// @param bNoSignalPwDn \b IN: MAPI_TRUE for no signal power down, MAPI_FALSE for not no signal power down
    /// @return None
    ///
    /// @code
    ///
    ///    // Case 1:
    ///    MSrv_Control::GetInstance()->EnterSleepMode();
    ///    // PowerOnOffFrame::OnKey() in Supernova/projects/ui/nebula/dvbt/src/PowerOnOffFrame.cpp
    ///
    ///    // Case 2:
    ///    MSrv_Control::GetInstance()->EnterSleepMode(TRUE,TRUE);
    ///    // MSrv_Timer::MonitorOffModeTimer() in Supernova/projects/msrv/common/src/MSrv_Timer.cpp
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual void EnterSleepMode(MAPI_BOOL bMode = TRUE,MAPI_BOOL bNoSignalPwDn =FALSE );

    //-------------------------------------------------------------------------------------------------
    /// Query "AC on" or "DC on"
    /// @return Enum EN_POWER_ON_MODE
    //-------------------------------------------------------------------------------------------------
    virtual EN_POWER_ON_MODE QueryPowerOnMode(void);

    //-------------------------------------------------------------------------------------------------
    /// Query Wakeup Source from PM mode
    /// @return Enum EN_WAKEUP_SOURCE
    //-------------------------------------------------------------------------------------------------
    virtual EN_WAKEUP_SOURCE QueryWakeupSource(void);

    //-------------------------------------------------------------------------------------------------
    /// Check wake up source by RTC or not
    /// @return MAPI_BOOL   /b TRUE or FALSE
    //-------------------------------------------------------------------------------------------------
    virtual BOOL IsWakeUpByRTC(void);

    //-------------------------------------------------------------------------------------------------
    /// To Get TV Info.
    /// @return S_TV_TYPE_INFO
    //-------------------------------------------------------------------------------------------------
    static const S_TV_TYPE_INFO & GetTVInfo(void);

    //-------------------------------------------------------------------------------------------------
    /// To Get TV Mode by Route Index.
    /// @param u8RouteIndex \b IN: index of route (0~(MAXROUTECOUNT-1))
    /// @return Route TV Mode
    //-------------------------------------------------------------------------------------------------
    static MAPI_U8 GetRouteTVMode(U8 u8RouteIndex);

    //-------------------------------------------------------------------------------------------------
    /// To Get DTV Route Index by TV Mode.
    /// @param routeType \b IN: DTV route type
    /// @return DTV Route Index
    //-------------------------------------------------------------------------------------------------
    static MAPI_U8 GetRouteIndexByTVMode(EN_TV_ROUTE_TYPE routeType);

#if (PVR_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Check any dtv player is recording.
    /// @return MAPI_BOOL   /b TRUE or FALSE
    //-------------------------------------------------------------------------------------------------
    BOOL IsAnyDTVPlayerRecording(void);

    //-------------------------------------------------------------------------------------------------
    /// To get dtv player vector, the players are recording.
    /// @param vDTVPlayer \b IN: recording dtv player vecotr
    /// @return MAPI_BOOL   /b TRUE or FALSE
    //-------------------------------------------------------------------------------------------------
    BOOL GetRECMSrvDTVPlayer(vector<U8>& vDTVRoute);
#endif

#if (ENABLE_LITE_SN != 1)
    //-------------------------------------------------------------------------------------------------
    /// To Get ATV System Type.
    /// @return ATV System Type
    //-------------------------------------------------------------------------------------------------
    static MAPI_U8 GetATVSystemType(void);

    //-------------------------------------------------------------------------------------------------
    /// To Get DTV System Type.
    /// @return DTV System Type
    //-------------------------------------------------------------------------------------------------
    static MAPI_U8 GetDTVSystemType(void);

    //-------------------------------------------------------------------------------------------------
    /// To Get STB System Type.
    /// @return ATV System Type
    //-------------------------------------------------------------------------------------------------
    static MAPI_U8 GetSTBSystemType(void);
#endif

    //-------------------------------------------------------------------------------------------------
    /// Is support the DTV system type
    /// @param u8Type \b IN: DTV system type
    /// @return TRUE: support, FALSE: not support
    //-------------------------------------------------------------------------------------------------
    static MAPI_BOOL IsSupportTheDTVSystemType(MAPI_U8 u8Type);

#if (ENABLE_LITE_SN != 1)
    //------------------------------------------------------------------------------
    /// Set SCART Bypass
    /// @return
    //------------------------------------------------------------------------------
    virtual void BypassScart();

    //------------------------------------------------------------------------------
    /// Set display timing
    /// @param enDisplayRes               \b IN: resolution type
    /// @return None
    //------------------------------------------------------------------------------
    virtual void SetDisplayTiming(mapi_display_datatype::EN_DISPLAY_RES_TYPE enDisplayRes);
#endif

    //-------------------------------------------------------------------------------------------------
    /// Get RTC system time
    /// @return  MAPI_U32           \b OUT: RTC system time
    //-------------------------------------------------------------------------------------------------
    virtual MAPI_U32 RTCGetCLK();

    //-------------------------------------------------------------------------------------------------
    /// Get offset time
    /// @return MAPI_S32           \b OUT: Offset time
    //-------------------------------------------------------------------------------------------------
    virtual MAPI_S32 GetClockOffset(void);

    //-------------------------------------------------------------------------------------------------
    /// Check Inpute Source. In PIP mode, the function will check main/sub inputsource.
    /// @return  eInputSrcType    \b IN: set inputsource
    /// @return  MAPI_BOOL    \b OUT: Ture is Valid, otherwise
    //-------------------------------------------------------------------------------------------------
    virtual MAPI_BOOL CheckCurrentInputSource(MAPI_INPUT_SOURCE_TYPE eInputSrcType );
    //-------------------------------------------------------------------------------------------------
    /// Get current focus input source. In PIP mode, the function will return main/sub inputsource.
    /// The main/sub inputsource is set by SetPipDisplayFocusWindow() function.
    /// @return  enum of input source type.
    //-------------------------------------------------------------------------------------------------
    virtual MAPI_INPUT_SOURCE_TYPE GetCurrentInputSource(void);

    //-------------------------------------------------------------------------------------------------
    /// Get current focus player.
    /// @return MSrv_Player           \b OUT: Current focus player instance
    //-------------------------------------------------------------------------------------------------
    virtual MSrv_Player* GetCurrentFocusPlayer(void);

    //-------------------------------------------------------------------------------------------------
    /// Get current main input source, .
    /// @return  enum of input source type.
    //-------------------------------------------------------------------------------------------------
    virtual MAPI_INPUT_SOURCE_TYPE GetCurrentMainInputSource(void);

    //-------------------------------------------------------------------------------------------------
    /// Get current sub input source.
    /// @return  enum of input source type.
    //-------------------------------------------------------------------------------------------------
    virtual MAPI_INPUT_SOURCE_TYPE GetCurrentSubInputSource(void);

#if (PIP_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Set the focus PIP display window and then the follow channel up/down, volume up/down acts
    /// according to the chosen display window
    /// @param  eWin        \b IN: output scaler win
    /// @return                 \b None
    //-------------------------------------------------------------------------------------------------
    virtual void SetPipDisplayFocusWindow(MAPI_SCALER_WIN eWin);

    //-------------------------------------------------------------------------------------------------
    /// Change current sub input source for pure SN PIP.
    /// @param  eSubSource       \b IN: new input source.
    /// @param  pstDispWin       \b IN: display window struct
    /// @param  bForceSet        \b IN: force set this source
    /// @return                 \b TRUE: success, or FALSE: failure.
    //-------------------------------------------------------------------------------------------------
    virtual BOOL SetPIPSubInputSource(EN_PIP_SUB_SOURCE_LIST eSubSource, const mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstDispWin = NULL, BOOL bForceSet = FALSE);
#endif

    //-------------------------------------------------------------------------------------------------
    /// Change current input source.
    /// @param  eInputSrc       \b IN: new input source.
    /// @param  bWriteDB        \b IN: write this seeting to database or not.
    /// @param  bUpdateLock        \b IN: if update lock flag
    /// @param  bLock        \b IN: if lock change input source
    /// @param  eWin        \b IN: output scaler win
    /// @param  bForceSet        \b IN: force set this source
    /// @return                 \b TRUE: success, or FALSE: failure.
    //-------------------------------------------------------------------------------------------------
    virtual BOOL SetInputSource(MAPI_INPUT_SOURCE_TYPE eInputSrc, BOOL bWriteDB = TRUE, BOOL bUpdateLock = FALSE, BOOL bLock = FALSE, MAPI_SCALER_WIN eWin = MAPI_MAIN_WINDOW, BOOL bForceSet = FALSE);

    //-------------------------------------------------------------------------------------------------
    /// Get conflict message
    /// @param stConflictMsg \b IN: Conflict message struct
    /// @return BOOL       \b OUT: true is success, false is failure
    //-------------------------------------------------------------------------------------------------
    virtual BOOL GetConflictMessage(ST_CONFLICT_MSG& stConflictMsg) const;

    //-------------------------------------------------------------------------------------------------
    /// Switch DTV player route.
    /// @param  u8DtvRoute \b IN: DTV Player Route
    /// @param  eWin       \b IN: DTV Scaler Window
    /// @param  bForceSet  \b IN: Force Switch Route
    /// @param  bFromUser  \b IN: CMD Is Transmit From User
    /// @return            \b OUT: TRUE: Switch success, or FALSE: Switch failure.
    //-------------------------------------------------------------------------------------------------
    virtual BOOL SwitchMSrvDtvRoute(U8 u8DtvRoute, MAPI_SCALER_WIN eWin = MAPI_MAIN_WINDOW, BOOL bForceSet = FALSE, BOOL bFromUser = TRUE);


#if (ATSC_SYSTEM_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Get last tv input source.
    /// @return  enum of input source type.
    //-------------------------------------------------------------------------------------------------
    virtual MAPI_INPUT_SOURCE_TYPE GetLastTVInputSource(void);
#endif

    //-------------------------------------------------------------------------------------------------
    /// Get the object of system database.
    /// @return  pointer to MSrv_System_Database object.
    //-------------------------------------------------------------------------------------------------
    static MSrv_System_Database * GetMSrvSystemDatabase(void);

    //-------------------------------------------------------------------------------------------------
    /// Get the object of channel manager.
    /// @return  pointer to MSrv_ChannelManager object.
    //-------------------------------------------------------------------------------------------------
    static MSrv_ChannelManager * GetMSrvChannelManager(void);

    //-------------------------------------------------------------------------------------------------
    /// Get the object of picture settings.
    /// @return  pointer to MSrv_Picture object.
    //-------------------------------------------------------------------------------------------------
    static MSrv_Picture * GetMSrvPicture(void);

    //-------------------------------------------------------------------------------------------------
    /// Get the object of video settings.
    /// @return  pointer to MSrv_Video object.
    //-------------------------------------------------------------------------------------------------
    static MSrv_Video * GetMSrvVideo(void);
#if (ENABLE_BACKEND == 1)
    //-------------------------------------------------------------------------------------------------
    /// Get the object of Ursa settings.
    /// @return  pointer to MSrv_Backend object.
    //-------------------------------------------------------------------------------------------------
    static MSrv_Backend * GetMSrvBackend(void);
#endif
    //-------------------------------------------------------------------------------------------------
    /// Get the object of factory settings.
    /// @return  pointer to MSrv_Factory_Mode object.
    //-------------------------------------------------------------------------------------------------
    static MSrv_Factory_Mode * GetMSrvFactoryMode(void);

    //-------------------------------------------------------------------------------------------------
    /// Get the object of MSrv_Timer.
    /// @return  pointer to MSrv_Timer object.
    //-------------------------------------------------------------------------------------------------
    static MSrv_Timer *GetMSrvTimer(void);

    //-------------------------------------------------------------------------------------------------
    /// Get the object of sound settings.
    /// @return  pointer to MSrv_SSSound object.
    //-------------------------------------------------------------------------------------------------
    static MSrv_SSSound * GetMSrvSSSound(void);

    //-------------------------------------------------------------------------------------------------
    /// Get the object of network settings.
    /// @return  pointer to MSrv_Network object.
    //-------------------------------------------------------------------------------------------------
    static MSrv_Network_Control * GetMSrvNetwork(void);

    //-------------------------------------------------------------------------------------------------
    /// Get the object of general video player.
    /// @param  eMapiSrcType      \b IN: Identification of input source.
    /// @return  pointer to MSrv_Player object.
    //-------------------------------------------------------------------------------------------------
    virtual MSrv_Player* GetMSrvPlayer(MAPI_INPUT_SOURCE_TYPE eMapiSrcType) = 0;

#if (STEREO_3D_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Get the object of 3D manager.
    /// @return  pointer to MSrv_SSSound object.
    //-------------------------------------------------------------------------------------------------
    static MSrv_3DManager * GetMSrv3DManager(void);
#endif
#if (RVU_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Get the object of RVU player.
    /// @return  pointer to MSrv_PC_Player object.
    //-------------------------------------------------------------------------------------------------
    static MSrv_RVU_Player* GetMSrvRvu(void);
#endif
#if (AUTO_TEST == 1)
    //-------------------------------------------------------------------------------------------------
    /// Get the object of auto test.
    /// @return  pointer to MSrv_AutoTest object.
    //-------------------------------------------------------------------------------------------------
    static AT_CmdManager* GetMSrvAutoTest(void);
#endif
#if (MODULE_TEST == 1)
    //-------------------------------------------------------------------------------------------------
    /// Get the object of module test.
    /// @return  pointer to MSrv_ModuleTest object.
    //-------------------------------------------------------------------------------------------------
    static MT_CmdManager* GetMSrvModuleTest(void);

    //-------------------------------------------------------------------------------------------------
    /// Set value to profile container
    //-------------------------------------------------------------------------------------------------
    void SetProfileValue(EN_PROFILE_CHECKPOINT eCheckPoint, U8 u8ReservedIndex, U32 u32Value);

    //-------------------------------------------------------------------------------------------------
    /// Get profile container
    /// @return  ST_PROFILE_CONTAINER object.
    //-------------------------------------------------------------------------------------------------
    ST_PROFILE_CONTAINER GetProfileContainer(void);

    //-------------------------------------------------------------------------------------------------
    /// Enable Channel Change Profile Message
    //-------------------------------------------------------------------------------------------------
    void EnableChannelChangeProfile();

    //-------------------------------------------------------------------------------------------------
    /// Get channel change profile status
    /// @return                     \b OUT: TRUE: ChannelChange Profile msg enable, or FALSE: ChannelChange Profile msg disable.
    //-------------------------------------------------------------------------------------------------
    BOOL GetChannelChangeProfileFlag();

    //-------------------------------------------------------------------------------------------------
    /// /// Enable Vdec Decode Info Profile Message
    //-------------------------------------------------------------------------------------------------
    void EnableVdecDecodeInfoPorfile();

    //-------------------------------------------------------------------------------------------------
    /// Get vdec decode info profile status
    /// @return                     \b OUT: TRUE: vdec decode info Profile msg enable, or FALSE: vdec decode info Profile msg disable.
    //-------------------------------------------------------------------------------------------------
    BOOL GetVdecDecodeInfoProfileFlag();
#endif

#if (ENABLE_NETREADY == 1)
    //-------------------------------------------------------------------------------------------------
    /// Get the object of Device Agent
    /// @return  pointer to MSrv_DeviceAgent object.
    //-------------------------------------------------------------------------------------------------
    static MSrv_DeviceAgent* GetMSrvDeviceAgent(void);

#endif

#if (ENABLE_DIVXDRM == 1)
    //-------------------------------------------------------------------------------------------------
    /// Get the object of divx drm
    /// @return  pointer to MSrv_DivXDRM object.
    //-------------------------------------------------------------------------------------------------
    static MSrv_DivX_DRM* GetMSrvDivXDRM(void);
#endif

    //-------------------------------------------------------------------------------------------------
    /// Get the object of input source table.
    /// @return  pointer to MAPI_VIDEO_INPUTSRCTABLE object.
    //-------------------------------------------------------------------------------------------------
    virtual const MAPI_VIDEO_INPUTSRCTABLE * GetSourceList(void);

    //-------------------------------------------------------------------------------------------------
    /// Upgrade  Mboot from usb.
    /// @param  pFilename          \b IN: upgrade mboot file
    /// @return                     \b OUT: TRUE: Operation success, or FALSE: Operation failure.
    //-------------------------------------------------------------------------------------------------
    virtual BOOL ResetForMbootUpgrade(char *pFilename);

    //-------------------------------------------------------------------------------------------------
    /// Set flag and reset to let mboot to read the flag and upgrade from nand.
    /// To use this API, the upgrade file should be stored into the physical media (NAND) first
    /// This API do the following jobs
    /// 1. Store SW upgrade configurations into physical storage
    /// 2. Turn of display
    /// 3. Reboot the system
    ///
    /// @return                     \b OUT: TRUE: Operation success, or FALSE: Operation failure.
    ///
    /// @code
    ///
    ///    bUpgrade=MSrv_Control::GetInstance()->ResetForNandUpgrade();
    ///    // NewFactoryModeAppFrame::OnTimer() in Supernova/projects/ui/nebula/china/src/NewFactoryModeAppFrame.cpp
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual BOOL ResetForNandUpgrade(void);

    //-------------------------------------------------------------------------------------------------
    /// Set flag and reset to let mboot to read the flag and upgrade from usb.
    /// @param  pData          \b IN: The buffer address of usb upgrade flag.
    /// @param  u16Size       \b IN: usb upgrade flag struct size.
    /// @return                     \b OUT: TRUE: Operation success, or FALSE: Operation failure.
    ///
    /// @code
    ///
    ///    MSrv_Control::GetInstance()->ResetForNetworkUpgrade();
    ///    // MSrv_NetworkUpgrade::ExitDownloadPhase() in Supernova/projects/msrv/control/src/MSrv_Control_common.cpp
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual BOOL ResetForUSBUpgrade(const U8 *pData, const U16 u16Size);

#if (OAD_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Set flag and reset to let mboot to read the flag and upgrade from OAD.
    /// This API do the following jobs
    /// 1. Store SW upgrade configurations into physical storage
    /// 2. Store OAD configurations into physical storage
    /// 3. Turn of display
    /// 4. Reboot the system
    ///
    /// @param  pData          \b IN: The buffer address of OAD upgrade flag.
    /// @param  u16Size       \b IN: usb upgrade flag struct size.
    /// @return                     \b OUT: TRUE: Operation success, or FALSE: Operation failure.
    ///
    /// @code
    ///
    ///   OAD_DL_INFO stOADInfo;
    ///   memset(&stOADInfo, 0, sizeof(stOADInfo));
    ///   MS_USER_SYSTEM_SETTING stGetSystemSetting;
    ///   MSrv_Control::GetMSrvSystemDatabase()->GetUserSystemSetting(&stGetSystemSetting);
    ///   if(stGetSystemSetting.fOadScan)
    ///   {
    ///      memcpy(&stOADInfo, &m_stCurrOADInfo, sizeof(m_stCurrOADInfo));
    ///   }
    ///   else
    ///   {
    ///       //set OAD Info
    ///       if(m_pfPlayerCb != NULL)
    ///       {
    ///           m_pfPlayerCb(&stOADInfo.freq, &stOADInfo.pid, &stOADInfo.band, m_pcPlayer);
    ///       }
    ///       else
    ///       {
    ///           ASSERT(0);
    ///       }
    ///   }
    ///   stOADInfo.pid = m_wOad_PID;
    ///   MSrv_Control::GetInstance()->ResetForOADUpgrade((U8*)&stOADInfo, sizeof(stOADInfo));
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual BOOL ResetForOADUpgrade(const U8 *pData, const U16 u16Size);

    //-------------------------------------------------------------------------------------------------
    /// Reset to let mboot to read the flag and upgrade from OAD.
    /// This API do the following jobs
    /// 1. Store SW upgrade configurations into physical storage
    /// 2. Store OAD configurations into physical storage
    /// 3. Turn of display
    /// 4. Reboot the system
    ///
    /// @param  pOADUpgradeConfig          \b IN: Extra config to control OAD update behavior
    /// @return                     \b OUT: TRUE: Operation success, or FALSE: Operation failure.
    ///
    /// @code
    ///
    ///    // Case 1:
    ///    ST_OAD_UPDGRADE_CONFIG stOADConfig;
    ///    memset(&stOADConfig, 0 , sizeof(stOADConfig));
    ///    stOADConfig.bDisplayUpgradeOSD = FALSE;
    ///    stOADConfig.bRebootAfterUpgrade = FALSE;
    ///    MSrv_Control::GetInstance()->ResetForOADUpgrade(&stOADConfig);
    ///    // MSrv_DTV_Player_DVB::OADMonitorNotify() in Supernova/projects/msrv/dvb/base/src/MSrv_DTV_Player_DVB_OAD.cpp
    ///
    ///    // Case 2:
    ///    MSrv_Control::GetInstance()->ResetForOADUpgrade();
    ///    // MSrv_DTV_Player_DVB::OADMonitorNotify() in Supernova/projects/msrv/dvb/base/src/MSrv_DTV_Player_DVB_OAD.cpp
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual BOOL ResetForOADUpgrade(ST_OAD_UPDGRADE_CONFIG* pOADUpgradeConfig = NULL);

    //-------------------------------------------------------------------------------------------------
    /// upgrade from OAD and Standby off
    /// @return                     \b OUT: TRUE: Operation success, or FALSE: Operation failure.
    //-------------------------------------------------------------------------------------------------
    virtual BOOL StandbyForOADUpgrade(void);
#endif

    //-------------------------------------------------------------------------------------------------
    /// Reset to let mboot to read the flag and upgrade from network.
    /// This API do the following jobs
    /// 1. Store SW upgrade configurations into physical storage
    /// 2. Turn of display
    /// 3. Reboot the system
    /// @return                     \b OUT: TRUE: Operation success, or FALSE: Operation failure.
    ///
    /// @code
    ///
    ///    MSrv_Control::GetInstance()->ResetForNetworkUpgrade();
    ///    // MSrv_NetworkUpgrade::ExitDownloadPhase() in Supernova/projects/msrv/common/src/MSrv_NetworkUpgrade.cpp
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual BOOL ResetForNetworkUpgrade(void);

#if (ENABLE_NETREADY == 1)
    //-------------------------------------------------------------------------------------------------
    /// Reset to let mboot to read the flag and upgrade from netready.
    /// This API do the following jobs
    /// 1. Store SW upgrade configurations into physical storage
    /// 2. Turn of display
    /// 3. Reboot the system
    /// @return                     \b OUT: TRUE: Operation success, or FALSE: Operation failure.
    ///
    /// @code
    ///
    ///    MSrv_Control::GetInstance()->ResetForNetReadyUpgrade();
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual BOOL ResetForNetReadyUpgrade(void);
#endif

#if (OAD_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Get if OAD in MBoot
    /// @return                     \b OUT: TRUE: OAD download in MBoot, otherwise, OAD download in AP.
    //-------------------------------------------------------------------------------------------------
    virtual BOOL IsOADInMBoot();
#endif

    //-------------------------------------------------------------------------------------------------
    /// Send Heart beat
    /// @param  enStatus       \b IN: Heart Beat status
    /// @return None
    //-------------------------------------------------------------------------------------------------
    static void SendHeartBeat(EN_THREAD_HEART_BEAT_STATUS enStatus);

    //-------------------------------------------------------------------------------------------------
    /// Get if ready to do USB upgrade. check 1. USB stick is detected; 2. upgrade file is available
    /// @return                     \b OUT: TRUE: ready to upgrade, or FALSE: not ready.
    //-------------------------------------------------------------------------------------------------
    virtual BOOL IsUSBUpgradeFileValid(void);

    //-------------------------------------------------------------------------------------------------
    /// Get if ready to do USB upgrade. check 1. USB stick is detected; 2. upgrade file is available
    /// @param  pFilename       \b IN: check upgrade file name
    /// @return                     \b OUT: TRUE: ready to upgrade, or FALSE: not ready.
    //-------------------------------------------------------------------------------------------------
    virtual BOOL IsUSBUpgradeFileValid(char *pFilename);

#if ( (OAD_ENABLE == 1) && ( FREEVIEW_AU_ENABLE == 1 ) )
    //-------------------------------------------------------------------------------------------------
    /// Set OAD Info.
    /// @param  pstOADInfo            \b IN: The OAD info struct.
    /// @return                       \b OUT: TRUE: Operation success, or FALSE: Operation failure.
    //-------------------------------------------------------------------------------------------------
    virtual BOOL SetOADInfo(ST_CUSTOMER_OAD_INFO *pstOADInfo);

    //-------------------------------------------------------------------------------------------------
    /// Get OAD Info.
    /// @param  pstOADInfo            \b OUT: The OAD info struct.
    /// @return                       \b OUT: TRUE: Operation success, or FALSE: Operation failure.
    //-------------------------------------------------------------------------------------------------
    virtual BOOL GetOADInfo(ST_CUSTOMER_OAD_INFO *pstOADInfo);
#endif

    //-------------------------------------------------------------------------------------------------
    /// to initialize customer IP authentication
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void ipSecurityAuthentication(void);
#if (SSC_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    ///    Update SSC Settting
    ///    @return None
    //-------------------------------------------------------------------------------------------------
    static BOOL UpdateSSCPara(void);
#endif

#if (ENABLE_LITE_SN != 1)
    //-------------------------------------------------------------------------------------------------
    /// To board name for board def
    /// @return                 \b OUT: board name
    //-------------------------------------------------------------------------------------------------
    virtual char * GetSystemBoardName(void);
    //-------------------------------------------------------------------------------------------------
    /// To SoftWare Ver for board def
    /// @return                 \b OUT: SoftWare Ver name
    //-------------------------------------------------------------------------------------------------
    virtual char * GetSystemSoftWareVer(void);
    //-------------------------------------------------------------------------------------------------
    /// To panel Ver for Customer ini
    /// @return                 \b OUT: Panel name
    //-------------------------------------------------------------------------------------------------
    virtual char * GetSystemPanelName(void);
#endif

    //-------------------------------------------------------------------------------------------------
    /// Get current number of gamma table
    /// @return                 \b OUT: current gamma table No
    //-------------------------------------------------------------------------------------------------
    virtual U16 GetSystemCurrentGammaTableNo(void);
    //-------------------------------------------------------------------------------------------------
    /// Get total number of gamma table
    /// @return                 \b OUT: total gamma table No
    //-------------------------------------------------------------------------------------------------
    virtual U16 GetSystemTotalGammaTableNo(void);

#if (ENABLE_LITE_SN != 1)
    //-------------------------------------------------------------------------------------------------
    /// To update  panel ini file
    /// @param    pKeycode    \b ini file key code
    /// @param    pKeyvalue    \b ini file key value
     /// @return                 \b OUT: true success ;False Fail
    //-------------------------------------------------------------------------------------------------
    virtual BOOL UpdatePanelIniFile(char * pKeycode,char * pKeyvalue);

    //-------------------------------------------------------------------------------------------------
    /// To update  customer  ini file
    /// @param    pKeycode    \b ini file key code
    /// @param    pKeyValue    \b ini file key value
    /// @return                 \b OUT: true success ;False Fail
    //-------------------------------------------------------------------------------------------------
    virtual BOOL UpdateCustomerIniFile(char *pKeycode ,char * pKeyValue);
#endif

    //-------------------------------------------------------------------------------------------------
    /// To update PQ parameter via USB key
    /// @return                 \b OUT: true success ;False Fail
    //-------------------------------------------------------------------------------------------------
    virtual BOOL UpdatePQParameterViaUsbKey(void);

#if (ENABLE_LITE_SN != 1)
    //-------------------------------------------------------------------------------------------------
    /// Get Update PQ File Path
    /// @param  pFilePath      \b OUT: file path buffer array[63]
    /// @param  enPQFile      \b IN: PQ file
    /// @return                 \b TRUE: success, or FALSE: failure.
    //-------------------------------------------------------------------------------------------------
    BOOL GetUpdatePQFilePath(char pFilePath[MSRV_MAX_BUFFER], const EN_MSRV_PQ_UPDATE_FILE enPQFile);

    //-------------------------------------------------------------------------------------------------
    /// Update PQ ini Files
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void UpdatePQiniFiles();
#endif

    //-------------------------------------------------------------------------------------------------
    /// Reboot system
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SystemReboot(void);
#if (POWEROFF_MUSIC_ENABLE)
    //-------------------------------------------------------------------------------------------------
    /// To play power off music when system power down
    /// @param    const char *path   power off music patch
    /// @param    timer_us   music play timer
    /// @return   NULL
    //-------------------------------------------------------------------------------------------------
    virtual void Play_Poweroff_Music(const char *path,int timer_us=5000);
#endif
    //-------------------------------------------------------------------------------------------------
    /// To set Power On Logo Type
    /// @param    ePowerOnLogoMode    \b Logo type
     /// @return                 \b OUT: true success ;False Fail
    //-------------------------------------------------------------------------------------------------
    virtual BOOL WritePowerOnLogoMode(MAPI_U32 ePowerOnLogoMode);
    //-------------------------------------------------------------------------------------------------
    /// To set Power On Music On/Off
    /// @param    bPowerOnMusic    \b On/Off
     /// @return                 \b OUT: true success ;False Fail
    //-------------------------------------------------------------------------------------------------
    virtual BOOL WritePowerOnMusicEnable(BOOL bPowerOnMusic);

#if (CEC_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Get the object of CEC settings.
    /// @return  pointer to MSrv_CEC object.
    //-------------------------------------------------------------------------------------------------
    static MSrv_CEC * GetMSrvCEC(void);
#endif

    //-------------------------------------------------------------------------------------------------
    /// Get boot-up initialization flag
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual BOOL IsTVFinishBooting();

    //-------------------------------------------------------------------------------------------------
    /// To start monitor thread
    /// @return  None
    //-------------------------------------------------------------------------------------------------
    virtual void StartThreadMonitor();

#if (STR_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// To stop monitor thread
    /// @return  None
    //-------------------------------------------------------------------------------------------------
    virtual void StopThreadMonitor();
#endif

#if (HBBTV_ENABLE==1)
    virtual void RedirectKeyEvent(int key, bool bypass);
#endif

    //-------------------------------------------------------------------------------------------------
    /// Query if currently the Pip mode enable
    /// @return                 \b TRUE: Enable, or FALSE: Disable
    //-------------------------------------------------------------------------------------------------
    virtual BOOL IsPipModeEnable();

    //-------------------------------------------------------------------------------------------------
    /// Query if currently inputsource is sub source
    /// @return                 \b TRUE: Yes, or FALSE: No
    //-------------------------------------------------------------------------------------------------
    virtual BOOL IsFocusOnSubSource(void);

    //-------------------------------------------------------------------------------------------------
    /// Query if currently the traveling mode enable
    /// @return                 \b TRUE: Enable, or FALSE: Disable
    //-------------------------------------------------------------------------------------------------
    virtual BOOL IsTravelingModeEnable(EN_TRAVELING_ENGINE_TYPE enEngineType=E_TRAVELING_ENGINE_TYPE_SD);

    //-------------------------------------------------------------------------------------------------
    /// Query the capability of the 'enEngineType'
    /// @param  enEngineType    \b IN: which engine's caps you want to get
    /// @param  pstTravelingCaps    \b OUT: ST_TRAVELING_ENGINE_CAPS
    /// @return                 \b EN_TRAVELING_RETURN
    //-------------------------------------------------------------------------------------------------
    EN_TRAVELING_RETURN GetTravelingEngineCaps(ST_TRAVELING_ENGINE_CAPS *pstTravelingCaps, EN_TRAVELING_ENGINE_TYPE enEngineType);

#if (PIP_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Get pip/pop/travelingmode supported sub-inputsources base on the current main inputsource
    /// @param  pipMode        \b IN: PIP/POP/Traveling
    /// @param  pSubInputSourceList        \b OUT: subinputsource support/not support table
    /// @param pListSize \b OUT: size of the subInputSourceList
    /// @return                 \b TRUE:pipMode is pip/pop/traveling, FALSE:pipMode is Off
    //-------------------------------------------------------------------------------------------------
    virtual BOOL GetPipSupportedSubInputSourceList(EN_PIP_MODES pipMode, BOOL *pSubInputSourceList, U32 *pListSize, EN_TRAVELING_ENGINE_TYPE enEngineType=E_TRAVELING_ENGINE_TYPE_SD);

    //-------------------------------------------------------------------------------------------------
    /// Get current PIP mode
    /// @return                 \b OUT: PIP mode
    //-------------------------------------------------------------------------------------------------
    virtual EN_PIP_MODES GetPipMode(void);

    //-------------------------------------------------------------------------------------------------
    /// Check PIP support main/sub inputsource combination or not
    /// @param  eMainInputSrc       \b IN: Inputsource that will display on main window
    /// @param  eSubInputSrc       \b IN: Inputsource that will display on sub window
    /// @return                 \b TRUE: support, or FALSE: not support
    //-------------------------------------------------------------------------------------------------
    virtual BOOL CheckPipSupport(MAPI_INPUT_SOURCE_TYPE eMainInputSrc, MAPI_INPUT_SOURCE_TYPE eSubInputSrc);

    //-------------------------------------------------------------------------------------------------
    /// Set PIP mode with main/sub inputsource, the sub inputsource must be tv inputsource
    /// with the position and size of the output of the sub input source
    /// @param  eMainInputSrc       \b IN: Inputsource that will display on main window
    /// @param  eSubInputSrc       \b IN: Inputsource that will display on sub window
    /// @param ptDispWin            \b IN: the setting x, y, w, h of display window
    /// @return                 \b EN_PIP_RETURN
    //-------------------------------------------------------------------------------------------------
    virtual EN_PIP_RETURN EnablePipTV(MAPI_INPUT_SOURCE_TYPE eMainInputSrc, MAPI_INPUT_SOURCE_TYPE eSubInputSrc, const mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstDispWin);

    //-------------------------------------------------------------------------------------------------
    /// Set PIP mode with main/MM inputsource
    /// @param  eMainInputSrc       \b IN: Inputsource that will display on main window
    /// with the position and size of the output of the sub input source
    /// @param ptDispWin            \b IN: the setting x, y, w, h of display window
    /// @return                 \b EN_PIP_RETURN
    //-------------------------------------------------------------------------------------------------
    virtual EN_PIP_RETURN EnablePipMM(MAPI_INPUT_SOURCE_TYPE eMainInputSrc, const mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstDispWin);

    //-------------------------------------------------------------------------------------------------
    /// Disable Pip and set main/sub inputsource to full screen
    /// When Sub is storage, set sub inputsource to full screen isn't allowable
    /// @param eWin         \b IN: the setting to set main/sub inputsource to full screen
    /// @return                 \b TRUE: success, or FALSE: failure.
    //-------------------------------------------------------------------------------------------------
    virtual BOOL DisablePip(MAPI_SCALER_WIN eWin = MAPI_MAIN_WINDOW);

    //------------------------------------------------------------------------------
    /// Set pip sub window
    /// @param pstSubDispWin            \b IN: the setting x, y, w, h of display window
    /// @return                 \b TRUE: success, or FALSE: failure.
    //------------------------------------------------------------------------------
    virtual BOOL SetPipSubwindow(const mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstSubDispWin);

    //------------------------------------------------------------------------------
    /// Get pip sub window info
    /// @param pstSubDispWin            \b OUT: the setting x, y, w, h of display window
    /// @return                 \b TRUE: success, or FALSE: failure.
    //------------------------------------------------------------------------------
    virtual BOOL GetPipSubwindow(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstSubDispWin);

    //------------------------------------------------------------------------------
    /// Get pip main window info
    /// @param pstMainDispWin            \b OUT: the setting x, y, w, h of display window
    /// @return                 \b TRUE: success, or FALSE: failure.
    //------------------------------------------------------------------------------
    virtual BOOL GetMainwindow(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstMainDispWin);

    //-------------------------------------------------------------------------------------------------
    /// Check POP support main/sub inputsource combination or not
    /// @param  eMainInputSrc       \b IN: Inputsource that will display on main window
    /// @param  eSubInputSrc       \b IN: Inputsource that will display on sub window
    /// @return                 \b TRUE: support, or FALSE: not support
    //-------------------------------------------------------------------------------------------------
    virtual BOOL CheckPopSupport(MAPI_INPUT_SOURCE_TYPE eMainInputSrc, MAPI_INPUT_SOURCE_TYPE eSubInputSrc);

    //-------------------------------------------------------------------------------------------------
    /// Set POP mode with main/sub inputsource, the sub inputsource must be tv inputsource
    /// @param  eMainInputSrc       \b IN: Inputsource that will display on main window
    /// @param  eSubInputSrc       \b IN: Inputsource that will display on sub window
    /// @return                 \b EN_PIP_RETURN
    //-------------------------------------------------------------------------------------------------
    virtual EN_PIP_RETURN EnablePopTV(MAPI_INPUT_SOURCE_TYPE eMainInputSrc, MAPI_INPUT_SOURCE_TYPE eSubInputSrc, const mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstDispMainWin=NULL, const mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstDispSubWin=NULL);

    //-------------------------------------------------------------------------------------------------
    /// Set POP mode with main/MM inputsource
    /// @param  eMainInputSrc       \b IN: Inputsource that will display on main window
    /// @return                 \b EN_PIP_RETURN
    //-------------------------------------------------------------------------------------------------
    virtual EN_PIP_RETURN EnablePopMM(MAPI_INPUT_SOURCE_TYPE eMainInputSrc, const mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstDispMainWin=NULL, const mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstDispSubWin=NULL);

    //-------------------------------------------------------------------------------------------------
    /// Disable Pop and set main/sub inputsource to full screen
    /// When Sub is storage, set sub inputsource to full screen isn't allowable
    /// @param eWin         \b IN: the setting to set main/sub inputsource to full screen
    /// @return                 \b TRUE: success, or FALSE: failure.
    //-------------------------------------------------------------------------------------------------
    virtual BOOL DisablePop(MAPI_SCALER_WIN eWin = MAPI_MAIN_WINDOW);
#endif// PIP_ENABLE

#if (TRAVELING_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Check traveling mode support main/sub inputsource combination or not
    /// @param  eMainInputSrc       \b IN: Inputsource that will display on main window
    /// @param  eSubInputSrc       \b IN: Inputsource that will transmit to VE for encoding
    /// @return                 \b TRUE: support, or FALSE: not support
    //-------------------------------------------------------------------------------------------------
    virtual BOOL CheckTravelingModeSupport(MAPI_INPUT_SOURCE_TYPE eMainInputSrc, MAPI_INPUT_SOURCE_TYPE eSubInputSrc, EN_TRAVELING_ENGINE_TYPE enEngineType=E_TRAVELING_ENGINE_TYPE_SD);

    //-------------------------------------------------------------------------------------------------
    /// Get the Traveling mode info
    /// @param stTravelModeInfo      \b IN: The information structure for Traveing mode setting
    /// @return                    \b None
    //-------------------------------------------------------------------------------------------------
    virtual void GetTravelingModeInfo(ST_TRAVELING_MODE_INFO *pstTravelModeInfo, EN_TRAVELING_ENGINE_TYPE enEngineType=E_TRAVELING_ENGINE_TYPE_SD);

    //-------------------------------------------------------------------------------------------------
    /// Set Traveling mode with info in ST_TRAVELING_MODE_INFO
    /// The sub inputsource will transmit to the remote cellphone/PAD
    /// if input sub input source equal to the main inputsource, the main inputsource do traveling mode,
    /// else the chosen sub inputsource do traveling mode
    /// @param  enEngineType       \b IN: Inputsource that will display on main window
    /// @param stTravelModeInfo      \b IN: The information structure for Traveing mode setting
    /// @return                    \b EN_PIP_RETURN
    //-------------------------------------------------------------------------------------------------
    virtual EN_TRAVELING_RETURN InitTravelingMode(ST_TRAVELING_MODE_INFO *pstTravelModeInfo, EN_TRAVELING_ENGINE_TYPE enEngineType=E_TRAVELING_ENGINE_TYPE_SD, EN_TV_ROUTE_TYPE enTvRoutType = E_ROUTE_NONE);

    //-------------------------------------------------------------------------------------------------
    /// For PIP manager: start to capture, when one frame is done, call back function( void (*data_cb) ) will be called
    /// @param pstTravelInfo  \b OUT: The information structure of current finished capture frame
    /// @return                 \b MAPI_BOOL
    //-------------------------------------------------------------------------------------------------
    virtual EN_TRAVELING_RETURN StartTravelingMode(TRAVELMODEDATACALLBACK pTravelDataCallback, TRAVELMODEEVENTCALLBACK pTravelEventCallback,  EN_TRAVELING_ENGINE_TYPE enEngineType=E_TRAVELING_ENGINE_TYPE_SD);

    //-------------------------------------------------------------------------------------------------
    /// For PIP manager: Stop Traveling mode Capture
    /// @return                 \b TRUE: success, or FALSE: failure.
    //-------------------------------------------------------------------------------------------------
    virtual EN_TRAVELING_RETURN StopTravelingMode(EN_TRAVELING_ENGINE_TYPE enEngineType=E_TRAVELING_ENGINE_TYPE_SD);

    //-------------------------------------------------------------------------------------------------
    /// Clear the frame that APP just waited on
    /// if clear, means APP has finish copying the data, and the memory will be writed on new coming data
    //-------------------------------------------------------------------------------------------------
    virtual EN_TRAVELING_RETURN FrameProcessDone(MAPI_U32 u32Index, EN_TRAVELING_ENGINE_TYPE enEngineType=E_TRAVELING_ENGINE_TYPE_SD);

    //-------------------------------------------------------------------------------------------------
    /// Disable ENableTravelingMode
    /// @return                 \b TRUE: success, or FALSE: failure.
    //-------------------------------------------------------------------------------------------------
    virtual EN_TRAVELING_RETURN FinalizeTravelingMode(EN_TRAVELING_ENGINE_TYPE enEngineType=E_TRAVELING_ENGINE_TYPE_SD);

    //-------------------------------------------------------------------------------------------------
    /// Event callback function for traveling event change things, eg: source conflict and quit traveling
    /// @param  enEngineType       \b IN: event call back for engine "enEngineType"
    /// @param  pstTravelEventInfo      \b IN: The information structure for event call back
    /// @return                    \b None
    //-------------------------------------------------------------------------------------------------
    static void MSrvTravelingEventCallback(void *pstTravelEventInfo, int enEngineType=E_TRAVELING_ENGINE_TYPE_SD);
    //------------------------------------------------------------------------------
    /// Set Memory Traveling mode with info in ST_MEMORY_TRAVELING_INFO
    /// @param pstMemInfo          \b IN: memory info for traveling
    /// @param enEngineType        \b IN: engine type for traveling
    /// @return                    \b MAPI_BOOL
    //------------------------------------------------------------------------------
    virtual EN_TRAVELING_RETURN SetMemoryTravelingConfig(ST_MEMORY_TRAVELING_INFO *pstMemInfo,EN_TRAVELING_ENGINE_TYPE enEngineType=E_TRAVELING_ENGINE_TYPE_SD);
#endif//#if (TRAVELING_ENABLE == 1)

#if (MHL_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Get the object of mhl settings.
    /// @return  pointer to MSrv_MHL object.
    //-------------------------------------------------------------------------------------------------
    static MSrv_MHL * GetMSrvMHL(void);
#endif //MHL_ENABLE

#if (HDMITX_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Get the object of HDMITx.
    /// @return  pointer to MSrv_HDMITX object.
    //-------------------------------------------------------------------------------------------------
    static MSrv_HDMITX * GetMSrvHdmiTx(void);
#endif

#if (ENABLE_LITE_SN != 1)
    //-------------------------------------------------------------------------------------------------
    /// Get Binary Name By FilterStr
    /// @param pPath \b IN: input search path
    /// @param pFilterStr \b IN: filter string
    /// @param pOutFileName \b IN: if find the bin who has filter string,fill the bin name to pOutFileName
    /// @param iOutFnLen   \b IN: the output buffer length of pOutFileName
    /// @return                 \b TRUE: success, or FALSE: failure.
    //-------------------------------------------------------------------------------------------------
    virtual BOOL GetBinNameByFilterStr(char *pPath,char *pFilterStr,char* pOutFileName,unsigned int iOutFnLen);
    //-------------------------------------------------------------------------------------------------
    /// Search bin File who contains filterstring in all mounted usb device
    /// @param pFilterStr \b IN: filter string
    /// @param pOutFilePath \b IN: if find the bin who has filter string,fill the bin name to pOutFileName
    /// @param iOutFilePathLen   \b IN: the output buffer length of pOutFileName
    /// @return    \b how many bins who contains filter string in all usb device
    //-------------------------------------------------------------------------------------------------
    virtual int SearchFileInUsbDevByFilter(char* pFilterStr,char* pOutFilePath,unsigned int iOutFilePathLen);
#endif

#if (ACTIVE_STANDBY_MODE_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Set Active Standby Mode
    /// @param bActive      \b IN: enable/disable
    /// @return                 \b TRUE: Active Success , FALSE: Active Fail.
    //-------------------------------------------------------------------------------------------------
    static BOOL SetActiveStandbyMode(BOOL bActive);

    //-------------------------------------------------------------------------------------------------
    /// Get Active Standby Mode
    /// @return                 \b TRUE: Active standby mode , FALSE: Not Active standby mode.
    //-------------------------------------------------------------------------------------------------
    static BOOL GetActiveStandbyMode(void);

    //-------------------------------------------------------------------------------------------------
    /// StandbyModeActiveProcess
    /// @return                 \b TRUE: Active Process Success , FALSE: Active Process Fail.
    //-------------------------------------------------------------------------------------------------
    static BOOL StandbyModeActiveProcess(void);

    //-------------------------------------------------------------------------------------------------
    /// Set Active Standby Mode flag
    /// @param bActive           \b IN: TRUE: enable, FALSE: disable
    //-------------------------------------------------------------------------------------------------
    static void SetIsActiveStandbyMode(BOOL bActive);

    //-------------------------------------------------------------------------------------------------
    /// OnEvent_ActiveStandbyMode
    /// @param arg1 \b IN: enable/disable
    /// @param arg2 \b IN:
    /// @param arg3 \b IN:
    /// @return         \b NA
    //-------------------------------------------------------------------------------------------------
    BOOL OnEvent_ActiveStandbyMode(void* arg1, void* arg2, void* arg3);


    //-------------------------------------------------------------------------------------------------
    /// RequestActiveStandbyMode
    /// @param arg1 \b IN: command
    /// @param arg2 \b IN: task id
    /// @param arg3 \b IN: enable / disable
    /// @param arg4 \b IN: task finished or not
    /// @return         \b TRUE: send request command successfully, FALSE: send command failed
    //-------------------------------------------------------------------------------------------------
    BOOL RequestActiveStandbyMode(EN_ACTIVE_STANDBY_CMD enActiveCmd,EN_ACTIVE_STANDBY_MODE_TASK taskId , BOOL bActiveStandby, BOOL bFinished);


   //-------------------------------------------------------------------------------------------------
    /// CheckAllActiveStandbyTaskFinished
    /// @return         \b TRUE: all tasks finished at the active standby mode, FALSE: some tasks worked at the active standby mdoe
    //-------------------------------------------------------------------------------------------------
    BOOL CheckAllActiveStandbyTaskFinished(void);

#endif

    //-------------------------------------------------------------------------------------------------
    /// Check if the Active Standby mode can be used or not.
    /// @return            \b TRUE: Active Standby mode is enabled, FALSE: Active standby mode is not enabled.
    //-------------------------------------------------------------------------------------------------
    BOOL IsSupportActiveStandBy();

    //-------------------------------------------------------------------------------------------------
    /// Set Gpio Status
    /// @param u32PinID \b IN: Gpio Index
    /// @param bEnable \  Set gpio status ture:high false :low
    /// @return                 \b TRUE: success, or FALSE: failure.
    //-------------------------------------------------------------------------------------------------
    static BOOL SetGpioDeviceStatus(const U32 u32PinID, const BOOL bEnable);

    //-------------------------------------------------------------------------------------------------
    /// Get Gpio Status
    /// @param u32PinID \b IN: Gpio Index
    /// @return                 \b  0: Gpio low, 1:Gpio high, others Gpio  is  not define
    //-------------------------------------------------------------------------------------------------
    static U32 GetGpioDeviceStatus(const U32 u32PinID);

    //-------------------------------------------------------------------------------------------------
    /// To Read the data from spiflash by bank
    /// @param u8Bank          \b IN: Bank select
    /// @param pu8Buffer       \b OUT: target address, you want to read data from spiflash to target address
    /// @param u32Size         \b IN: Size
    /// @return                \b OUT: TRUE, or FALSE
    //-------------------------------------------------------------------------------------------------
    virtual BOOL ReadFromSPIFlashByBank(const U8 u8Bank,  U8 * const pu8Buffer, const U32 u32Size);

    //-------------------------------------------------------------------------------------------------
    /// To Read the data from flash by bank
    /// @param u8Bank          \b IN: Bank select
    /// @param pu8Buffer       \b OUT: target address, you want to read data from spiflash to target address
    /// @param u32Size         \b IN: Size
    /// @return                \b OUT: TRUE, or FALSE
    //-------------------------------------------------------------------------------------------------
    virtual BOOL ReadFromFlashByBank(const U8 u8Bank,  U8 * const pu8Buffer, const U32 u32Size);
    //-------------------------------------------------------------------------------------------------
    /// To Read the data from flash by address
    /// @param u32FlashAddr   \b IN: source address in spiflash
    /// @param u32ReadSize    \b IN: Size to read
    /// @param pu8Readbuffer  \b OUT: target address, you want to read data from spiflash to target address
    /// @return               \b OUT: TRUE, or FALSE
    //-------------------------------------------------------------------------------------------------
    virtual BOOL ReadFromFlashByAddr(const U32 u32FlashAddr, const U32 u32ReadSize, U8* const pu8Readbuffer);
    //-------------------------------------------------------------------------------------------------
    /// To Write the data to flash by bank
    /// @param u8Bank          \b IN: Bank select
    /// @param pu8Buffer       \b IN: Source address which the source data is stored in
    /// @param u32Size         \b IN: Size
    /// @return                \b OUT: TRUE, or FALSE
    //-------------------------------------------------------------------------------------------------
    virtual BOOL WriteToFlashByBank(const U8 u8Bank, const U8 * const pu8Buffer, const U32 u32Size);
    //-------------------------------------------------------------------------------------------------
    /// To Write the data to spiflash by address
    /// @param u32FlashAddr   \b IN: source address in spiflash
    /// @param u32WriteSize   \b IN: Size to Write
    /// @param pu8Writebuffer \b IN: target address, you want to write data to spiflash to target address
    /// @return               \b OUT: TRUE, or FALSE
    //-------------------------------------------------------------------------------------------------
    virtual BOOL WriteToFlashByAddr(const U32 u32FlashAddr, const U32 u32WriteSize, U8* const pu8Writebuffer);
    //-------------------------------------------------------------------------------------------------
    /// To Read the data from spiflash by address
    /// @param u32FlashAddr   \b IN: source address in spiflash
    /// @param u32ReadSize    \b IN: Size to read
    /// @param pu8Readbuffer  \b OUT: target address, you want to read data from spiflash to target address
    /// @return               \b OUT: TRUE, or FALSE
    //-------------------------------------------------------------------------------------------------
    virtual BOOL ReadFromSPIFlashByAddr(const U32 u32FlashAddr, const U32 u32ReadSize, U8* const pu8Readbuffer);

    //-------------------------------------------------------------------------------------------------
    /// To Write the data to spiflash by bank
    /// @param u8Bank          \b IN: Bank select
    /// @param pu8Buffer       \b IN: Source address which the source data is stored in
    /// @param u32Size         \b IN: Size
    /// @return                \b OUT: TRUE, or FALSE
    //-------------------------------------------------------------------------------------------------
    virtual BOOL WriteToSPIFlashByBank(const U8 u8Bank, const U8 * const pu8Buffer, const U32 u32Size);

    //-------------------------------------------------------------------------------------------------
    /// To Write the data to spiflash by address
    /// @param u32FlashAddr   \b IN: source address in spiflash
    /// @param u32WriteSize   \b IN: Size to Write
    /// @param pu8Writebuffer \b IN: target address, you want to write data to spiflash to target address
    /// @return               \b OUT: TRUE, or FALSE
    //-------------------------------------------------------------------------------------------------
    virtual BOOL WriteToSPIFlashByAddr(const U32 u32FlashAddr, const U32 u32WriteSize, U8* const pu8Writebuffer);

#if (ENABLE_LITE_SN != 1)
    //-------------------------------------------------------------------------------------------------
    /// To Read the data from eeprom
    /// @param u16Index     \b IN: source address in eeprom which is stored the source data
    /// @param pu8Buffer    \b IN: target address, you want to read data from eeprom to target address(in RAM1)
    /// @param u16Size      \b IN: Size
    /// @return             \b OUT: TRUE, or FALSE
    //-------------------------------------------------------------------------------------------------
    virtual BOOL ReadFromEeprom(const U16 u16Index, U8 * const pu8Buffer, const U16 u16Size);

    //-------------------------------------------------------------------------------------------------
    /// To Write the data to eeprom
    /// @param u16Index     \b IN: target address in eeprom
    /// @param pu8Buffer    \b IN: Source address which the source data is stored in
    /// @param u16Size      \b IN: Size
    /// @return             \b OUT: TRUE, or FALSE
    //-------------------------------------------------------------------------------------------------
    virtual BOOL WriteToEeprom(const U16 u16Index, U8 * const pu8Buffer, const U16 u16Size);

    //-------------------------------------------------------------------------------------------------
    /// Get Sar adc level
    /// @param u8Channel     \b IN: select adc channel
    /// @return             \b OUT: adc level value
    //-------------------------------------------------------------------------------------------------
    virtual U8 GetSarAdcLevel(U8 u8Channel);

    //-------------------------------------------------------------------------------------------------
    /// send message to PM initialize PWM
    /// @param bPWMONFF \b IN: pm pwm on /off
    ///  @return       \b OUT: TRUE, or FALSE
    //-------------------------------------------------------------------------------------------------
    BOOL PM51PWMInitialize(MAPI_BOOL bPWMONFF);

    //-------------------------------------------------------------------------------------------------
    /// send message to PM config PWM
    /// @param pPmPWMCfg \b IN: pwm paramter
    ///  @return       \b OUT: TRUE, or FALSE
    //-------------------------------------------------------------------------------------------------
    BOOL PM51PWMConfig(MAPI_PWM_SimIR_CFG *pPmPWMCfg);

    //-------------------------------------------------------------------------------------------------
    /// Get the object of Source Detection
    /// @return  pointer to MSrv_SrcDetect object.
    //-------------------------------------------------------------------------------------------------
    static MSrv_SrcDetect *GetMSrvSourceDetect(void);

    //-------------------------------------------------------------------------------------------------
    /// Get the object of backlight control settings.
    /// @return  pointer to MSrv_BacklightControl object.
    //-------------------------------------------------------------------------------------------------
    static MSrv_BacklightControl * GetMSrvBacklightControl(void);
#if (SECURE_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Get the object of presharedkey
    /// @return  pointer to MSrv_PreSharedKey object.
    //-------------------------------------------------------------------------------------------------
    static MSrv_PreSharedKey* GetMSrvPreSharedKey(void);
#endif
    //-------------------------------------------------------------------------------------------------
    /// Get the object of advert player.
    /// @return  pointer to MSrv_Advert_Player object.
    //-------------------------------------------------------------------------------------------------
    static MSrv_Advert_Player * GetMSrvAdvertPlayer(void);

    //-------------------------------------------------------------------------------------------------
    /// Enable or Disable I2C function
    /// @param u32gID \b IN: I2C device ID
    /// @param bEnabled \b IN: Enable(TRUE) or Disable(FALSE) I2C function
    //-------------------------------------------------------------------------------------------------
    void SwitchI2COnOff(MAPI_U32 u32gID , BOOL bEnabled);

    //-------------------------------------------------------------------------------------------------
    /// set power mode
    /// @param ePowerMode \b IN: power mode
    ///  @return       \b OUT: TRUE, or FALSE
    //-------------------------------------------------------------------------------------------------
    virtual BOOL SetEnvPowerMode(EN_ACON_POWERON_MODE ePowerMode);

    //-------------------------------------------------------------------------------------------------
    ///get power mode
    ///  @return       \b OUT: power mode
    //-------------------------------------------------------------------------------------------------
    virtual EN_ACON_POWERON_MODE GetEnvPowerMode(void);

    //-------------------------------------------------------------------------------------------------
    /// set logo mode
    /// @param eLogoMode \b IN: logo mode
    ///  @return       \b OUT: TRUE, or FALSE
    //-------------------------------------------------------------------------------------------------
    virtual BOOL SetEnvPowerOnLogoMode(EN_LOGO_MODE eLogoMode);

    //-------------------------------------------------------------------------------------------------
    /// get logo mode
    ///  @return       \b OUT: logo mode
    //-------------------------------------------------------------------------------------------------
    virtual EN_LOGO_MODE GetEnvPowerOnLogoMode(void);

    //-------------------------------------------------------------------------------------------------
    /// set music mode
    /// @param eMusicMode \b IN: power on music mode
    ///  @return       \b OUT: TRUE, or FALSE
    //-------------------------------------------------------------------------------------------------
    virtual BOOL SetEnvPowerOnMusicMode(EN_POWERON_MUSIC_MODE eMusicMode);

    //-------------------------------------------------------------------------------------------------
    /// get power mode
    ///  @return       \b OUT: power on music mode
    //-------------------------------------------------------------------------------------------------
    virtual EN_POWERON_MUSIC_MODE GetEnvPowerOnMusicMode(void);

    //-------------------------------------------------------------------------------------------------
    /// set poweron music volume
    /// @param u8Volume \b IN: power on music volume
    ///  @return       \b OUT: TRUE, or FALSE
    //-------------------------------------------------------------------------------------------------
    virtual BOOL SetEnvPowerOnMusicVolume(U8 u8Volume);

    //-------------------------------------------------------------------------------------------------
    /// get poweron music volume
    ///  @return       \b OUT: power on music mode
    //-------------------------------------------------------------------------------------------------
    virtual MAPI_U8 GetEnvPowerOnMusicVolume(void);
#endif

    //-------------------------------------------------------------------------------------------------
    /// Request System to Asyncly Switch Dtv Rout.
    ///@param  enTvCmd      \b IN: EN_TV_ASYNC_CMD command type
    ///@param  u8OriginalDtvRoute      \b IN: original route
    ///@param  u8TargetDtvRoute      \b IN: route to switch
    ///@param  pfunc        \b IN: async callback function to notify cmd complete
    /// @return                 \b TRUE: success, or FALSE: failure.
    //-------------------------------------------------------------------------------------------------
    virtual BOOL RequestAsynclyCommand(EN_TV_ASYNC_CMD enTvCmd, U8 u8OriginalDtvRoute, U8 u8TargetDtvRoute ,MSrvAsyncCmdCallBack pfunc=0);
    //-------------------------------------------------------------------------------------------------
    ///  For UI to Handle  Service Cmd, it should not called by user.
    ///  @return       \b TRUE: success, or FALSE: failure.
    //-------------------------------------------------------------------------------------------------
    virtual BOOL HandleUICmdService();

#if (ENABLE_LITE_SN != 1)
    //-------------------------------------------------------------------------------------------------
    /// Read bytes from I2C
    /// @param u32gID     \b IN: Device ID
    /// @param u8AddrSize \b IN:  register NO. to read, this parameter is the NO. of register offsets in pu8addr buffer, it should be 0 when *pu8addr = NULL.
    /// @param *pu8Addr   \b IN:  pointer to a buffer containing target register offsets to read
    /// @param u16Size    \b IN:  data length (in byte) to read
    /// @param *pu8Data   \b OUT:  pointer to the data buffer for read
    /// @return           \b OUT: TRUE, or FALSE
    //-------------------------------------------------------------------------------------------------
    BOOL ReadBytesFromI2C(MAPI_U32 u32gID, MAPI_U8 u8AddrSize, MAPI_U8 *pu8Addr, MAPI_U16 u16Size, MAPI_U8 *pu8Data);

    //-------------------------------------------------------------------------------------------------
    /// Write bytes to I2C
    /// @param u32gID     \b IN: Device ID
    /// @param u8AddrSize \b IN:  register NO. to write, this parameter is the NO. of register offsets in pu8addr buffer, it should be 0 when *pu8addr = NULL.
    /// @param *pu8Addr   \b IN:  pointer to a buffer containing target register offsets to write
    /// @param u16Size    \b IN:  data length (in byte) to write
    /// @param *pu8Data   \b IN:  pointer to the data buffer for write
    /// @return           \b OUT: TRUE, or FALSE
    //-------------------------------------------------------------------------------------------------
    BOOL WriteBytesToI2C(MAPI_U32 u32gID, MAPI_U8 u8AddrSize, MAPI_U8 *pu8Addr, MAPI_U16 u16Size, MAPI_U8 *pu8Data);
#endif

// EosTek Patch Begin
    //-------------------------------------------------------------------------------------------------
    /// Control the IR led on/off when record and standby.
    /// @param u32gID	  \b IN: TRUE, or FALSE
    /// @return 		  \b OUT: void
    //-------------------------------------------------------------------------------------------------
    void SetIRLedByOnOff(BOOL OnOff);

    //-------------------------------------------------------------------------------------------------
    /// Control the EarPhone on/off when record and standby.
    /// @param u32gID	  \b IN: TRUE, or FALSE
    /// @return 		  \b OUT: void
    //-------------------------------------------------------------------------------------------------
    void SetEarPhoneByOnOff(BOOL OnOff);
// EosTek Patch End

#if(MSTAR_TVOS == 1)  // for tvapp and supernova mutually send message
    //-------------------------------------------------------------------------------------------------
    /// Utility Get Interface
    ///@param  pCharDataBlock      \b OUT: target command buffer point
    ///@param  u8length      \b IN: target command buffer length
    /// @return                 \b TRUE: success, or FALSE: failure.
    //-------------------------------------------------------------------------------------------------
    virtual BOOL GetTvosInterfaceCMD(char* pCharDataBlock,  const U8 u8length);

    //-------------------------------------------------------------------------------------------------
    /// Utility Set Interface
    ///@param  pCharCommand      \b IN: source command buffer point (need null-terminated)
    /// @return                 \b TRUE: success, or FALSE: failure.
    //-------------------------------------------------------------------------------------------------
    virtual BOOL SetTvosInterfaceCMD(const char* pCharCommand);

    //-------------------------------------------------------------------------------------------------
    /// Set Tvos Common Command
    ///@param  interfaceCommand      \b IN: source command buffer point (need null-terminated)
    ///@param  u8length      \b IN: u16 array buffer length
    /// @return                 \b u16 array
    //-------------------------------------------------------------------------------------------------
    virtual U16* SetTvosCommonCommand(const char* pCharCommand, int* u8length);

    //-------------------------------------------------------------------------------------------------
    /// Utility Post Event
    ///@param  nEvt      \b IN: event enum
    ///@param  wParam    \b parameter
    /// @return                 \b TRUE: success, or FALSE: failure.
    //-------------------------------------------------------------------------------------------------
    BOOL SendTvosUtilityEvent(U32 nEvt, U32 wParam);

    //-------------------------------------------------------------------------------------------------
    /// Post Event to Enter 3D Format
    ///@param  _3dFormat    \b 3D Format
    /// @return                 \b TRUE: success, or FALSE: failure.
    //-------------------------------------------------------------------------------------------------
    BOOL SendTvos3DFormat(U32 _3dFormat);

    //-------------------------------------------------------------------------------------------------
    /// Post Event to Exit 3D Format
    /// @return                 \b TRUE: success, or FALSE: failure.
    //-------------------------------------------------------------------------------------------------
    BOOL SendExitTvos3DFormat(void);
#endif

#if (CI_PLUS_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// AskHcRelease
    ///     Ask CI CAM to do HC release.
    ///     This function should be called by UI and will be blocked!!!
    ///     Until ReleaseReply be received, or 10 second timeout.
    /// @return                     \b TRUE when release OK, or 10 second timeout; FLASE when release refused
    //-------------------------------------------------------------------------------------------------
    BOOL AskCiHcRelease(void);

    //-------------------------------------------------------------------------------------------------
    /// SendCiHcReleaseReply
    ///     When received OK / Refused by CAM, or timeout achieved, send event to unblock AskCiHcRelease.
    /// @return  None
    //-------------------------------------------------------------------------------------------------
    void SendCiHcReleaseReply(BOOL bIsReleaseOk);
#endif

    //-------------------------------------------------------------------------------------------------
    /// WaitBootComplete
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void WaitBootComplete(void);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: SetBootComplete
    /// @brief \b Function \b Description: SetBootComplete
    /// @param <IN> bIsComplete   \b IN: Boot complete or not
    /// @return <OUT>      \b NONE
    //-------------------------------------------------------------------------------------------------
    void SetBootComplete(BOOL bIsComplete);

#if (STR_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Suspend
    /// @return
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL Suspend(void);

    //-------------------------------------------------------------------------------------------------
    /// Resume
    /// @return
    //-------------------------------------------------------------------------------------------------
    void Resume(void);

    //-------------------------------------------------------------------------------------------------
    /// Initialize STR
    /// @return
    //-------------------------------------------------------------------------------------------------
    void InitializeStr(void);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: GetAndroidAudioMuteFlag()
    /// @brief \b Function \b Description: Get android audio mute flag
    /// @param <IN>        \b NONE
    /// @return <OUT>      \b True: mute on, False: mute off
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL GetAndroidAudioMuteFlag();

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: SetAndroidAudioMuteFlag()
    /// @brief \b Function \b Description: Set android audio mute flag
    /// @param <IN>        \b True: mute on, False: mute off
    /// @return <OUT>      \b NONE
    //-------------------------------------------------------------------------------------------------
    void SetAndroidAudioMuteFlag(MAPI_BOOL bAndroidAudioMute);
    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: GetAudioMuteFlag
    /// @brief \b Function \b Description: Get audio mute flag of  type MuteMute
    /// @param <IN>        \b NONE
    /// @return <OUT>      \b True: mute on, False: mute off
    //-------------------------------------------------------------------------------------------------
     MAPI_BOOL GetAudioMuteFlag(int MuteType);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: SetAudioMuteFlag
    /// @brief \b Function \b Description: Set android audio mute flag of Type MuteMute
    /// @param <IN>        \b True: mute on, False: mute off
    /// @return <OUT>      \b NONE
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetAudioMuteFlag(int MuteType,MAPI_BOOL bAudioMute);
    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: SendStrCommand
    /// @brief \b Function \b Description: An command api for STR
    /// @param eCmd    \b <IN> : Command
    /// @param wParam \b <IN> : param1
    /// @param lParam   \b <IN> : param2
    /// @return       \b <OUT> : True: success, False: failure
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SendStrCommand(EN_STR_CMD eCmd, int wParam, int lParam);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: CheckEnterSTRSupspendMode
    /// Check if system need do ste suspend
    /// @return                 \b MAPI_TRUE:Suspend, MAPI_FALSE
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL CheckEnterSTRSuspendMode();
#endif

#if (ENABLE_PARTIAL_STANDBY == 1)
    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: EnterTVPWOnControlMode
    /// Attend into TV Power On Control Sleep mode
    /// @return                 \b TRUE:
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL EnterTVPWOnControlMode();
#endif

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: GetCompilerFlag
    /// Get compiler flag value
    /// @param FlagName         \n <IN> : Name of compiler flag
    /// @param Value            \n <OUT> : Value of compiler flag
    /// @return                 \b <OUT> : NONE
    //-------------------------------------------------------------------------------------------------
    void GetCompilerFlag(string FlagName, string& Value);

    //-------------------------------------------------------------------------------------------------
    /// Refresh window, specify main or sub window.
    /// @param eWin                \n <IN> : Main or sub window.
    /// @return                         \b <OUT> : If success return MAPI_TRUE, otherwise return MAPI_FALSE.
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL RefreshWindow(MAPI_SCALER_WIN eWin);

    //------------------------------------------------------------------------------
    /// Query wheather the input feature is supported.
    /// @param enFeature                                 \b IN:  Specify the feature to be queried.
    /// @param pParam                                    \b IN:  Pointer for private use of the specific feature.
    /// @return                                          \b OUT: BOOL TURE for supported feature,
    ///                                                          BOOL FALSE for unsurpported feature.
    //------------------------------------------------------------------------------
    BOOL IsSupportedFeature(EN_MSRV_SUPPORTED_FEATURES enFeature, void *pParam);


    //-------------------------------------------------------------------------------------------------
    /// set blind screen on VE output
    /// @return  None
    //-------------------------------------------------------------------------------------------------
    virtual void SetBlindScreen(void);

    //------------------------------------------------------------------------------
    /// Set output timing by SOC output  timing (panel timing). This function always used on STB/HDMITX platform when user change output timing.
    /// @param enTiming                          \b IN: Specify SOC output timing.
    /// @param u32Operation                      \b IN: Specify the additional opeartoin to perform.
    /// @param enOpLockState                     \b IN: Specify output timing lock state.
    ///                                                 E_OP_LOCK_TIMING_STATE_NONE         : Change output timing only when it's currently in E_OP_LOCK_TIMING_STATE_UNLOCK_TIMING state.
    ///                                                 E_OP_LOCK_TIMING_STATE_LOCK_TIMING  : Lock output timing
    ///                                                 E_OP_LOCK_TIMING_STATE_UNLOCK_TIMING: Unlock output timing
    /// @return                                  \b OUT: ERROR_VIDEO_SUCCESS                : If the function succeeds.
    ///                                                  ERROR_VIDEO_TIMING_NO_CHANGE       : If the function no need change output timing.
    ///                                                  ERROR_VIDEO_SET_OUTPUT_TIMING_FAIL : If specified SOC output timing set fail.
    ///                                                  ERROR_VIDEO_TRANSFORM_NOT_EXIST    : If transform doesn't exist.
    ///                                                  ERROR_VIDEO_FAIL                   : Otherwise.
    MAPI_U16 SetOutputTiming(EN_TIMING enTiming, MAPI_U32 u32Operation = E_SET_OP_TIMING_OPERATION_SAVE_TIMING, EN_OP_LOCK_TIMING_STATE enOpLockState = E_OP_LOCK_TIMING_STATE_NONE);


#if (MSTAR_TVOS == 0)

    //------------------------------------------------------------------------------
    /// Set CVBS out TV system (PAL/NTSC)
    /// @param enSystem                           \b IN: TV system.
    /// @return                                           \b OUT: If the function succeeds, it returns ERROR_VIDEO_SUCCESS.
    ///                                                                     If the function no need change CVBS TV system, it returns ERROR_CVBS_OUT_NO_CHANGE.
    ///                                                                     If the function no need change output timing, it returns ERROR_VIDEO_TIMING_NO_CHANGE.
    ///                                                                     If transform doesn't exist, it returns ERROR_VIDEO_TRANSFORM_NOT_EXIST.
    ///                                                                     If the function can't transform current timing to new tv system timing, it returns ERROR_CVBS_OUT_SET_OUTPUT_TIMING_FAIL.
    ///                                                                     Otherwise it returns ERROR_VIDEO_FAIL.
    MAPI_U16 SetCvbsOutSystem(EN_CVBS_OUT_SYSTEM enSystem);

#endif

    ///-----------------------------------------------------------------------------------------------------
    ///IsSourceChanging
    ///@return                 \b OUT: TRUE or FALSE
    ///-----------------------------------------------------------------------------------------------------
    MAPI_BOOL IsSourceChanging(void);

    ///-----------------------------------------------------------------------------------------------------
    ///SetSourceChangingFlag
    ///@param  bSourceChange      \b IN: source changing or finish source changing
    ///@return                 \b OUT: TRUE or FALSE
    ///-----------------------------------------------------------------------------------------------------
    void SetSourceChangingFlag(BOOL bSourceChange);

    //------------------------------------------------------------------------------
    /// Get Boot source.
    /// @return                         \b OUT: Boot source.
    //------------------------------------------------------------------------------
    virtual EN_MSRV_BOOT_KEY GetPowerOnIRKey();

    //-------------------------------------------------------------------------------------------------
    /// Updata HDMI EDID Info
    /// @return  NONE
    //-------------------------------------------------------------------------------------------------
    static void InitHDMIEDIDInfoSet();

    //-------------------------------------------------------------------------------------------------
    /// Turn off the bootlogo
    /// @return TRUE if OK, FALSE otherwise
    //-------------------------------------------------------------------------------------------------
    BOOL FinishBootlogo();

    //-------------------------------------------------------------------------------------------------
    /// configure watchdog timer timeout value
    /// @param u16Second \b IN: watchdog timeout value
    /// @return  None
    //-------------------------------------------------------------------------------------------------
    void SetWatchdogTimer(U16 u16Second);

    //-------------------------------------------------------------------------------------------------
    /// Check input source lock status
    /// @param eMapiSrcType: the input source
    /// @return MAPI_TRUE: locked
    /// @return MAPI_FALSE: unlocked
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL IsInputSourceLock(MAPI_INPUT_SOURCE_TYPE eMapiSrcType);

    //-------------------------------------------------------------------------------------------------
    /// Set input source lock status and mute the output of video/audio
    /// @param bLock: lock status, MAPI_TRUE: lock, MAPI_FALSE: unlock
    /// @param eMapiSrcType: the input source
    /// @return MAPI_TRUE: set input source lock success
    /// @return MAPI_FALSE: set input source lock fail
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetInputSourceLock(MAPI_BOOL bLock, MAPI_INPUT_SOURCE_TYPE eMapiSrcType);

    //-------------------------------------------------------------------------------------------------
    /// Reset all input source lock to default
    /// @param NONE
    /// @return MAPI_TRUE: reset input source lock success
    /// @return MAPI_FALSE: reset input source lock fail
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL ResetInputSourceLock(void);

protected:
    //-------------------------------------------------------------------------------------------------
    /// Get Env info and Clear upgrade flag.
    /// @return None
    /// @code
    ///
    ///
    ///    U8 u8FirstProgramNumber = GetFavoriteProgram(GET_FIRST_FAVORITE_PROGRAM, 0, 0, NULL);
    ///
    ///    U8 u8LastProgramNumber = GetFavoriteProgram(GET_LAST_FAVORITE_PROGRAM, 0, 0, NULL);
    ///
    ///    U8 u8PreviousProgramNumber = GetFavoriteProgram(GET_PREVIOUS_FAVORITE_PROGRAM, const U16 u16Program, 0, NULL);
    ///
    ///    if(FALSE != GetFavoriteProgram(GET_STATUS_IS_FAVORITE_PROGRAM, const U16 u16Program, 0, NULL))
    ///    {
    ///        //the given program number is favorite.
    ///    }
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual void InitializeEnv();
    //-------------------------------------------------------------------------------------------------
    /// Set Video mute by child class
    /// @param  bVideoMute         \b IN: mute or unmute video.
    /// @param  u16VideoUnMuteTime \b IN: video unmute time.
    /// @param  eMapiSrcType        \b IN: The input source.
    /// @param  engine              \b IN: mute engine.
    /// @return                     \b TRUE: Operation success, or \b FALSE: Operation failure.
    //-------------------------------------------------------------------------------------------------
    virtual BOOL _SetVideoMute(MAPI_BOOL bVideoMute ,
                       MAPI_U16 u16VideoUnMuteTime ,
                       MAPI_INPUT_SOURCE_TYPE eMapiSrcType ,EN_MUTE_ENGINE engine ) = 0;

#if (CEC_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Start initial CEC in thread
    /// @param arg \b IN: struct ST_INPUTSOURCE
    /// @return  None
    //-------------------------------------------------------------------------------------------------
    static void* InitCECThread(void* arg);
#endif


    //-------------------------------------------------------------------------------------------------
    /// Send Boot up event to notify starting Post-Init tasks
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SendBootUpEvent();

    //-------------------------------------------------------------------------------------------------
    /// handle the functionalities after video init
    /// @return  TRUE if OK, FALSE otherwise
    //-------------------------------------------------------------------------------------------------
    virtual BOOL PostVideoInit();
    //-------------------------------------------------------------------------------------------------
    /// wait audio  finish initialize
    /// @param u32WaitTime \b IN: wait time
    /// @return  TRUE if OK, FALSE otherwise
    //-------------------------------------------------------------------------------------------------
    BOOL WaitAudioInit(U32 u32WaitTime);

    /// commands
    typedef enum
    {
        /// Set Current Input Source
        E_CMD_SET_CURRENT_INPUT_SOURCE,
        /// Set Current Input Route
        E_CMD_SET_DTV_ROUTE,
        // <BGPVR
        /// Set the route and the service before performing record.
        /// Param1 is ( ONID << 16 | TSID ),
        /// Param2 is SID, and
        /// Param3 is route index.
        /// The return value is assigned to Param1 as a BOOL.
        E_CMD_SET_RECORD_SERVICE_BY_ROUTE,
        /// Start record a DTV route.
        /// No parameter is required.
        /// The return value is assigned to Param1 as a EN_PVR_STATUS.
        E_CMD_START_RECORD_ROUTE,
        /// Stop record the recording DTV route.
        /// No parameter and return value is required.
        E_CMD_STOP_RECORD_ROUTE,
        // BGPVR>
#if (CI_PLUS_ENABLE == 1)
        //CI PLUS HC ask release command
        E_CMD_CI_HC_ASK_RELEASE,
        //CI PLUS HC ask release command reply
        E_CMD_CI_HC_ASK_RELEASE_REPLY,
#endif
    } EN_MSRV_CMD;

    /// define of command structure
    typedef struct
    {
        /// commend
        EN_MSRV_CMD enCmd;
        /// parameter
        MAPI_U32 u32Param1;
        /// parameter
        MAPI_U32 u32Param2;
        /// parameter
        MAPI_U32 u32Param3;
        /// parameter
        MAPI_U32 u32Param4;
    } MSRV_CMD;

    //static MSrv_Control_common *m_pInstance;

    /// the pointers of all MSrv objects
    static MSrv *m_pMSrvList[E_MSRV_MAX];   // FIXME : move this to nonstatic..!

    /// has been initialized or not
    BOOL m_bInit;

    /// the pointer of input source table
    const MAPI_VIDEO_INPUTSRCTABLE *m_pInputSrcTable;

#if (STR_ENABLE == 1)
    MAPI_INPUT_SOURCE_TYPE m_enStrFocusSource;
    /// store android audio mute status
    MAPI_BOOL m_bAndroidAudioMute;
    map<int,BOOL> m_bAudioMute;
#endif

    //BOOL m_bBootComplete;
    pthread_mutex_t m_BootCompleteMutex;

    /// current Pip Mode
    EN_PIP_MODES m_enPipMode;
    /// is pip mode finished
    MAPI_BOOL m_bPipFinished;

#if (PIP_ENABLE == 1)
    EN_PIP_MODES m_enPrePipMode;
#endif

#if (TRAVELING_ENABLE == 1)
    ST_TRAVELING_MODE_INFO m_stTravelModeInfo[E_TRAVELING_ENGINE_TYPE_MAX];
#endif
    /// last input source
    MAPI_INPUT_SOURCE_TYPE m_enLastTVSource;
    /// the mutex for switching input source
    pthread_mutex_t m_MutexInputSrcSwitch;
    /// the thread ID for monitor thread
    pthread_t m_pthreadMonitor_id;
#if (FRONTPNL_ENABLE == 1)
    pthread_t pthread_Keydetect_id;
#endif //FRONTPNL_ENABLE


    //BOOL m_bFlagThreadMonitorActive;
    /// monitor thread info
    static Monitor_t m_monitor_st;

    /// Heart beat time stamp
    MAPI_U32 m_u32HeartBeatTime;

    /// constructor
    MSrv_Control_common(); // singleton

    /// Heart beat monitor thread function
    static void *HeartBeatMonitor(void* ptr);
    /// Heart beat event
    static mapi_event<THREAD_HEART_BEAT_INFO> *m_pEvHeartBeat;
    /// Heart beat thread
    static pthread_t m_pthreadHeartBeat;
    static pthread_mutex_t m_MutHeartBeat;
    /// Heart beat information queue
    static vector<THREAD_HEART_BEAT_INFO> m_vThreadInfo;
    /// Heart beat monitor flag
    static BOOL m_bHeartBeatMonitor;

    /// command event
    mapi_event<MSRV_CMD> *m_pCmdEvt;
    /// ack event to the command
    mapi_event<MSRV_CMD> *m_pCmdAckEvt;

    mapi_event<MSRV_TV_ASYNC_CMD_EVENT> *m_pTVAsyncCmdEvt;


#if (CEC_ENABLE == 1)
    pthread_t m_CECThread;
    /// struct of cec thread information
    static ST_THREAD_ARG_INFO m_stCecThreadInfo;
#endif

#if (PREVIEW_MODE_ENABLE == 1)
    BOOL IsPreviewModeRunning(void);
#endif
    /// if source is changing
    BOOL m_bSourceChange;

private:

    /// current focus input source, focus may be main/sub input source
    MAPI_INPUT_SOURCE_TYPE m_enCurrentFocusSource;
    //Soc output timing locked
    MAPI_BOOL m_bSocOutputTimingLock;

#if (PREVIEW_MODE_ENABLE == 1)
    BOOL m_bPreviewModeRunning;
    pthread_t m_PreviewModeThread;
    pthread_mutex_t m_PreviewModeMutex;
    BOOL m_bPreviewModeExit;
    MAPI_INPUT_SOURCE_TYPE enFirstPreviewModeInputSource;
    static void *PreviewModeThreadFunc(void *arg);
    static mapi_event<ST_TRAVELING_MODE_CALLBACK_INFORMATION> *m_pEvPreviewMode;
    static void TravelingModeDataCallBackFunc(void *pstTravelDataInfo, int enEngineType);
    static void TravelingModeEventCallBackFunc(void *pstTravelEventInfo, int enEngineType);
    static BOOL _ProcessDipData(ST_TRAVELING_CALLBACK_DATA_INFO stTravelDataInfo, int enEngineType, const char *cFileName);
#endif//end of #if (PREVIEW_MODE_ENABLE == 1)

#if (FREEVIEW_AU_ENABLE==1)
    //-------------------------------------------------------------------------------------------------
    /// To Save OAD wake up status.
    /// @return                 \b TRUE: Finalize success, FALSE: Finalize failure.
    //-------------------------------------------------------------------------------------------------
    virtual BOOL SaveOadWakeUpStatus(void);
#endif
    static S8 m_USBUpgradePort;
#if (OAD_ENABLE == 1)
    BOOL m_bOADInMBoot;
#if (FREEVIEW_AU_ENABLE == 1)
    ST_CUSTOMER_OAD_INFO m_stOADInfo;
#endif
#endif

#if (TRAVELING_ENABLE == 1)
    static TRAVELMODEDATACALLBACK m_pMSrvTravelDataCallback[E_TRAVELING_ENGINE_TYPE_MAX];
    static TRAVELMODEEVENTCALLBACK m_pMSrvTravelEventCallback[E_TRAVELING_ENGINE_TYPE_MAX];
    static void MSrvTravelingDataCallback(void *pstTravelDataInfo, int enEngineType=E_TRAVELING_ENGINE_TYPE_SD);
#endif
    //------------------------------------------------------------------------------
    /// From Window transfer to InputSource
    /// @param eWIN            \b IN: MAIN or SUB window
    /// @return                 \b INPUTSOURCE
    //------------------------------------------------------------------------------
    MAPI_INPUT_SOURCE_TYPE Win2InputSource(MAPI_SCALER_WIN eWIN);

    //------------------------------------------------------------------------------
    /// From InputSource transfer to Window
    /// @param src            \b IN: input source
    /// @return                 \b MAIN or SUB window
    //------------------------------------------------------------------------------
    MAPI_SCALER_WIN InputSource2Win(MAPI_INPUT_SOURCE_TYPE src);
#if (PIP_ENABLE == 1)
    static U16 m_PipHardwareLimitation; //by hardware limiation, in PIP/POP, with only support to 944 pixels
    mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE m_stMainWinInfo;
    mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE m_stSubWinInfo;
    virtual BOOL resetPipPopToMainSource();
    typedef enum
    {
        E_INPUT_SOURCE_SWITCH_DENY = -1,
        E_INPUT_SOURCE_SWITCH_NONE = 0,
        E_INPUT_SOURCE_SWITCH_MAIN,
        E_INPUT_SOURCE_SWITCH_SUB,
        E_INPUT_SOURCE_SWITCH_BOTH,
    } EN_PIP_CONDIITON;

    //------------------------------------------------------------------------------
    /// reset PQ map when none PIP->PIP/POP, or PIP/POP -> none PIP
    /// @return                 \b None
    //------------------------------------------------------------------------------
    virtual void resetPQ();

    //------------------------------------------------------------------------------
    /// Set pip main window
    /// @param pstMainDispWin            \b IN: the setting x, y, w, h of display window
    /// @return                 \b TRUE: success, or FALSE: failure.
    //------------------------------------------------------------------------------
    virtual BOOL setMainwindow(const mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstMainDispWin);

    //-------------------------------------------------------------------------------------------------
    /// check the PIP condition before set inputsource
    /// @param  eMainInputSrc       \b IN: Inputsource that will display on main window
    /// @param  eSubInputSrc       \b IN: Inputsource that will display on sub window or remote device
    /// @param  ePipMode       \b IN: Pip/Pop/Traveling mode
    /// @return                 \b NONE: Not switch inputsource, MAIN: Switch only main inputsource, SUB: Switch only sub inputsource, BOTH: Switch both main/sub inputsource
    //-------------------------------------------------------------------------------------------------
    virtual EN_PIP_CONDIITON checkPipInputSourceSwitch(MAPI_INPUT_SOURCE_TYPE eMainInputSrc, MAPI_INPUT_SOURCE_TYPE eSubInputSrc, EN_PIP_MODES ePipMode, EN_TRAVELING_ENGINE_TYPE enEngineType=E_TRAVELING_ENGINE_TYPE_SD);

    //-------------------------------------------------------------------------------------------------
    /// check the PIP condition before set inputsource
    /// @param  eMainInputSrc       \b IN: Inputsource that will display on main window
    /// @param  eSubInputSrc       \b IN: Inputsource that will display on sub window or remote device
    /// @return                 \b NONE: Not switch inputsource, MAIN: Switch only main inputsource, SUB: Switch only sub inputsource, BOTH: Switch both main/sub inputsource
    //-------------------------------------------------------------------------------------------------
    //virtual EN_PIP_CONDIITON checkPopInputSourceSwitch(MAPI_INPUT_SOURCE_TYPE eMainInputSrc, MAPI_INPUT_SOURCE_TYPE eSubInputSrc);

    //-------------------------------------------------------------------------------------------------
    /// check the PIP condition before set inputsource
    /// @param  eMainInputSrc       \b IN: Inputsource that will display on main window
    /// @param  eSubInputSrc       \b IN: Inputsource that will display on sub window or remote device
    /// @return                 \b NONE: Not switch inputsource, MAIN: Switch only main inputsource, SUB: Switch only sub inputsource, BOTH: Switch both main/sub inputsource
    //-------------------------------------------------------------------------------------------------
    //virtual EN_PIP_CONDIITON checkTravelingInputSourceSwitch(MAPI_INPUT_SOURCE_TYPE eMainInputSrc, MAPI_INPUT_SOURCE_TYPE eSubInputSrc);

    //-------------------------------------------------------------------------------------------------
    /// Set PIP mode
    /// @param  ePipMode       \b IN: the desired PIP mode
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void setPipMode(EN_PIP_MODES ePipMode);

    //-------------------------------------------------------------------------------------------------
    /// Set PIP Main/Sub Input Source
    /// @param  eMainInputSrc       \b IN: Inputsource that will display on main window
    /// @param  eSubInputSrc       \b IN: Inputsource that will display on sub window
    /// @return                 \b TRUE: success, or FALSE: failure.
    //-------------------------------------------------------------------------------------------------
    virtual BOOL setPipInputSource(MAPI_INPUT_SOURCE_TYPE eMainInputSrc, MAPI_INPUT_SOURCE_TYPE eSubInputSrc);
#endif

    //-------------------------------------------------------------------------------------------------
    /// SetSystemInfo
    /// This API do the following jobs
    /// 1.Initialize SystemInfo
    ///
    /// @return   None
    ///
    /// @code
    ///
    ///    MSrv_Control_common::SetSystemInfo();
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    static void SetSystemInfo();

    pthread_mutex_t m_MutexPostVideoInit;
#if (ACTIVE_STANDBY_MODE_ENABLE == 1)
    pthread_mutex_t m_MutexSetActiveStandby;
#endif
    mapi_event<BOOL>* m_pBootUpEvent;
    BOOL m_bBootupInit;

    BOOL m_block;
    pthread_mutex_t m_MutexLock;
    virtual BOOL SetInputSourceCmd(MAPI_INPUT_SOURCE_TYPE eInputSrc, BOOL bWriteDB, MAPI_SCALER_WIN eWin = MAPI_MAIN_WINDOW) = 0;
    MAPI_BOOL IsMuteFlag;
    //-------------------------------------------------------------------------------------------------
    /// Initialize and active Heart beat
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void InitHeartBeat(void);

#if (ENABLE_LITE_SN != 1)
    virtual BOOL StartUartDebug();
#endif

    BOOL StartWatchDog();

    //-------------------------------------------------------------------------------------------------
    /// Get boot config from MBoot env
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void GetBootConfig(void);


#if (CEC_ENABLE == 1)
    void CECStart(void);
#endif
    void StartInsertModule();

#if (AUTO_TEST == 1)
    void StartAutotest();
#endif
#if (MODULE_TEST == 1)
    void StartModuletest();
    ST_PROFILE_CONTAINER m_stProfileContainer;
    BOOL m_bEnableChannelChangeProfileMSG;
    BOOL m_bEnableVdecDecodeInfoProfileMSG;
#endif
    /// if PM code is running on DRAM
    BOOL m_bPmOnRam;
#if (CI_PLUS_ENABLE == 1)
    BOOL m_bOperatorProfile;
#endif
#if (ACTIVE_STANDBY_MODE_ENABLE == 1)
    static BOOL m_bStandbyModeActive;
#endif

#if(MSTAR_TVOS == 1) // for tvapp and supernova mutually send message
    char m_aCharDataBlock[MAX_DATA_BLOCK_LEN];
    U16 m_u16DataBlock[MAX_DATA_BLOCK_LEN];
    pthread_mutex_t m_TvosInterfaceCMDMutex;
#endif

    //-------------------------------------------------------------------------------------------------
    /// Select adc table according to calibration mode for calibration source
    /// @param  enInputSrc       \b IN: Inputsource that will display on main window
    /// @return                  \b MAPI_TRUE: success, or MAPI_FALSE: failure.
    //-------------------------------------------------------------------------------------------------
    BOOL selectAdcTableByCalibrationMode(const MAPI_INPUT_SOURCE_TYPE enInputSrc);

#if (MSTAR_TVOS == 1)
    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: GetResolution()
    /// @brief \b Function \b Description: Get Resolution
    /// @return <OUT>      \b u8Vol      :resolution_index value range from 0~10
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetResolution(void);

    MAPI_BOOL CompareResolution(MAPI_U32 res,MAPI_U8 res_indx);

    //-------------------------------------------------------------------------------------------------
    ///Set Rate
    /// @param u32Rate      \b IN: index of rate
    /// @return RESULT
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetReproduceRate(MAPI_U32 u32Rate);

    //-------------------------------------------------------------------------------------------------
    ///Get Rate
    ///@return                        \b OUT: U32  value of Rate
    //-------------------------------------------------------------------------------------------------
    MAPI_U32 GetReproduceRate();

    //------------------------------------------------------------------------------
    /// Save environment setting for mboot.
    /// @param enTiming          \b IN: Specify output timing
    /// @return                         \b OUT: if success return true, otherwise false.
    //------------------------------------------------------------------------------
    MAPI_BOOL SaveEnvForMBoot(EN_TIMING enTiming);
#endif

#if (INPUT_SOURCE_LOCK_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Check input source type is supported for input source lock
    /// @param eMapiSrcType: the input source
    /// @return MAPI_TRUE: Support
    /// @return MAPI_FALSE: Unsupport
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL CheckInputSourceLockSupport(MAPI_INPUT_SOURCE_TYPE eMapiSrcType);
#endif
};

#endif
