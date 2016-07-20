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

// headers of itself
#include "SystemInfo.h"

using std::vector;
using std::string;

// headers of standard C libs

// headers of standard C++ libs


// headers of the same layer's
#include "Board.h"
#include "OverScan.h"
#include "SN_Board_Default_Setting.h"
#include "VideoConfig.h"

#include "iniparser.h"

// headers of underlying layer's
#include "mapi_utility.h"

#include "apiPNL.h"
#include "apiXC_Dlc.h"

#include "mapi_env_manager.h"

#include "drvMMIO.h"
#include "drvSERFLASH.h"
#include <fcntl.h>

#include <iterator>


/* define VGA and HDMI EDID lenght */
#define VGA_EDID_LEN    128
#define HDMI_EDID_LEN   256
#define CRC_PATTERN_SIZE    12
#define CUSTOMER_PATH                               "/Customer/"
#define BKP_CUSTOMER_PATH                       "/CustomerBackup/"
#define CUSTOMER_INI_PATH_FILENAME          "/Customer/Customer.ini"
#define BKP_CUSTOMER_INI_PATH_FILENAME  "/CustomerBackup/Customer.ini"
#define CUSTOMER_SECTION_NAME           "customer"


#define DLC_INI_PATH_FILENAME                       "/config/DLC/DLC.ini"
#define BKP_DLC_PATH_FILENAME                       "/config/DLC/DLC.ini"
#define Cmd_Recovery_DLC                                "cp -vf /config/DLC/DLC.ini /config/DLC/DLC.ini"
#define Cmd_Recovery_DLCBackup                      "cp -vf /config/DLC/DLC.ini /config/DLC/DLC.ini"
#define SYS_INI_PATH_FILENAME          "/config/sys.ini"
#define MWBLAUNCHER_INI_PATH_FILENAME          "/applications/MWB/MWBLauncher.ini"
#define BKP_SYS_INI_PATH_FILENAME  "/config/sys.ini"
#define Cmd_Recovery_Sys                       "cp -vf /config/sys.ini /config/sys.ini"
#define Cmd_Recovery_SysBackup             "cp -vf  /config/sys.ini /config/sys.ini"
#define PATH_BOARD_INI "/config/board.ini"
#define PATH_BOARD_INI_BACKUP "/config/board.ini"
#define CMD_RECOVERY_BOARD_INI "cp -vf /config/board.ini /config/board.ini"
#define CMD_RECOVERY_BOARD_INI_BACKUP "cp -vf /config/board.ini /config/board.ini"

#define DLC_TABLE_COUNT_MAX 10

/// The path to read boot cmd from Mboot
#define CMD_LINE_PATH               "/proc/cmdline"
/// the size of cmd line
#define CMD_LINE_SIZE               2048
/// The env variable prefix of MBoot env location
#define ENV_CFG_PREFIX              "ENV="
/// The MBoot env location definition of NAND
#define ENV_IN_NAND                 "NAND"
/// The MBoot env location definition of UBI
#define ENV_IN_UBI                  "UBI"
/// The MBoot env location definition of SERIAL
#define ENV_IN_SERIAL               "SERIAL"
/// The MBoot env location definition of MMC
#define ENV_IN_EMMC                 "EMMC"

#if (SECURE_BOOTING_ENABLE == 1 && MSTAR_TVOS == 1)
#define EMMC_ENV_CONFIG_PATH "/dev/block/platform/mstar_mci.0/by-name/MPOOL"
#elif(BOOT_FROM_ROM == 1 && MSTAR_TVOS == 1)
#define EMMC_ENV_CONFIG_PATH "/dev/block/mmcblk0p2"
#else
#define EMMC_ENV_CONFIG_PATH "/dev/mmcblk2"
#endif

#define CEILING(x) (x>(int)x)?((int)(x+1.0)):((int)x)
//#define safestrcpy(dest,src) snprintf(dest, sizeof(dest), "%s", src)
#define WIDI_INI_CHECKSUM 0x40010001

typedef struct
{
    //system.ini
    char * ModelName;
    char *PanelName;
    MAPI_U8 PQBinDefault;    //if PQBinDefault==1, use the PQ binary file; if PQBinDefault ==0, use the customer pq binary file.
    MAPI_U8 PQPathName[64];
    int GammaTableNo;
    int TotalGammaTableNo;
    MAPI_U8 TunerSAWType;
    MAPI_U8 AudioAmpSelect;
    MAPI_BOOL UrsaEanble;
    MAPI_U8 UrsaSelect;
    MAPI_BOOL MEMCPanelEnable;
    MAPI_U8 MEMCPanelSelect;
    MAPI_BOOL bGammabinflags;
    MAPI_BOOL bDotByDotAble;
    MAPI_U16 u16AVSyncDelay;
    MAPI_BOOL bMirrorVideo;
    MAPI_U8 u8MirrorType;
    MAPI_BOOL bEnable3DOverScan;
    MAPI_BOOL bHbbtvDelayInitFlag;
    MAPI_BOOL bNandHdcpEnable;
    MAPI_BOOL bSPIHdcpEnable;
    MAPI_U8 u8HdcpSPIBank;
    MAPI_U16 u16HdcpSPIOffset;
    MAPI_BOOL bEEPROMHdcpEnable;
    MAPI_U8 u8HdcpEEPROMAddr;
    char *pHDCPKeyFileName;
    MAPI_BOOL bSPIMacEnable;
    MAPI_U8 u8MacSPIBank;
    MAPI_U16 u16MacSPIOffset;
    MAPI_BOOL bLocalDIMMINGEnable;
    MAPI_U8 u8LocalDIMMINGPanelSelect;
    char BoardName[32];
    char SoftWareVer[32];
    char SysPanelName[64];
    MAPI_BOOL bPipPairInfo[MAPI_INPUT_SOURCE_NUM][MAPI_INPUT_SOURCE_NUM];
    MAPI_BOOL bPopPairInfo[MAPI_INPUT_SOURCE_NUM][MAPI_INPUT_SOURCE_NUM];
    MAPI_BOOL bTravelingPairInfo[MAPI_INPUT_SOURCE_NUM][MAPI_INPUT_SOURCE_NUM];
    MAPI_BOOL bPanel4K2KModeNeedCmd;
    MAPI_U8  clk_en;
    MAPI_U8  bypass_en;
    MAPI_U8  invAlpha;
    MAPI_U8  hsync_vfde;
    MAPI_U8  hfde_vfde;
    MAPI_U32 OsdcLpllType;
    MAPI_U8 u8AudioDelayOffset[MAPI_AudioDelay_SOURCE_NUM];
    MAPI_U8 u8SPDIFDelayOffset[MAPI_AudioDelay_SOURCE_NUM];
} __attribute__((packed))IniInfo;




#define SYSINFO_DEBUG 0
IniInfo SysIniBlock={NULL,NULL, 1, {0}, 0, 0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,NULL,0,0,0,0,0,{0},{0},{0}, {{0}}, {{0}}, {{0}}, 0, 0, 0, 0, 0, 0, 0 ,{0},{0}};

typedef struct
{
    EN_MAPI_TIMING enTiming;
    char cName[32];
} PanelPath;

#define PANEL_NAME_LIST_SIZE 41
PanelPath g_PanelPath[PANEL_NAME_LIST_SIZE] =
{
    {E_MAPI_TIMING_480, "panel:m_p480PanelName"},
    {E_MAPI_TIMING_480P_60, "panel:m_p480_60PPanelName"},
    {E_MAPI_TIMING_480I_60, "panel:m_p480_60IPanelName"},
    {E_MAPI_TIMING_576, "panel:m_p576PanelName"},
    {E_MAPI_TIMING_576P_50, "panel:m_p576_50PPanelName"},
    {E_MAPI_TIMING_576I_50, "panel:m_p576_50IPanelName"},
    {E_MAPI_TIMING_720, "panel:m_p720PanelName"},
    {E_MAPI_TIMING_720P_60, "panel:m_p720_60PPanelName"},
    {E_MAPI_TIMING_720P_50, "panel:m_p720_50PPanelName"},
    {E_MAPI_TIMING_1440, "panel:m_p1440PanelName"},
    {E_MAPI_TIMING_1440P_50, "panel:m_p1440_50PPanelName"},
    {E_MAPI_TIMING_1470, "panel:m_p1470PanelName"},
    {E_MAPI_TIMING_1470P_60, "panel:m_p1470_60PPanelName"},
    {E_MAPI_TIMING_1470P_30, "panel:m_p1470_30PPanelName"},
    {E_MAPI_TIMING_1470P_24, "panel:m_p1470_24PPanelName"},
    {E_MAPI_TIMING_2K1K, "panel:m_p2K1KPanelName"},
    {E_MAPI_TIMING_2K1KP_60, "panel:m_p2K1K_60PPanelName"},
    {E_MAPI_TIMING_2K1KI_60, "panel:m_p2K1K_60IPanelName"},
    {E_MAPI_TIMING_2K1KP_50, "panel:m_p2K1K_50PPanelName"},
    {E_MAPI_TIMING_2K1KI_50, "panel:m_p2K1K_50IPanelName"},
    {E_MAPI_TIMING_2K1KP_30, "panel:m_p2K1K_30PPanelName"},
    {E_MAPI_TIMING_2K1KP_25, "panel:m_p2K1K_25PPanelName"},
    {E_MAPI_TIMING_2K1KP_24, "panel:m_p2K1K_24PPanelName"},
    {E_MAPI_TIMING_2K2K, "panel:m_p2K2KPanelName"},
    {E_MAPI_TIMING_2K2KP_60, "panel:m_p2K2K_60PPanelName"},
    {E_MAPI_TIMING_2K2KP_30, "panel:m_p2K2K_30PPanelName"},
    {E_MAPI_TIMING_2K2KP_25, "panel:m_p2K2K_25PPanelName"},
    {E_MAPI_TIMING_2K2KP_24, "panel:m_p2K2K_24PPanelName"},
    {E_MAPI_TIMING_4K540, "panel:m_p4K540PanelName"},
    {E_MAPI_TIMING_4K540P_240, "panel:m_p4K540_240PPanelName"},
    {E_MAPI_TIMING_4K1K, "panel:m_p4K1KPanelName"},
    {E_MAPI_TIMING_4K1KP_120, "panel:m_p4K1K_120PPanelName"},
    {E_MAPI_TIMING_4K1KP_60, "panel:m_p4K1K_60PPanelName"},
    {E_MAPI_TIMING_4K1KP_30, "panel:m_p4K1K_30PPanelName"},
    {E_MAPI_TIMING_4K2K, "panel:m_p4K2KPanelName"},
    {E_MAPI_TIMING_4K2KP_60, "panel:m_p4K2K_60PPanelName"},
    {E_MAPI_TIMING_4K2KP_50, "panel:m_p4K2K_50PPanelName"},
    {E_MAPI_TIMING_4K2KP_30, "panel:m_p4K2K_30PPanelName"},
    {E_MAPI_TIMING_4K2KP_25, "panel:m_p4K2K_25PPanelName"},
    {E_MAPI_TIMING_4K2KP_24, "panel:m_p4K2K_24PPanelName"},
    {E_MAPI_TIMING_4096x2160P_24, "panel:m_p4096x2160_24PPanelName"}
};

S_TV_TYPE_INFO SystemInfo::m_TV_info_table  = {E_ATV_Only, E_NTSC_ENABLE, DVBT_ENABLE, E_BTSC_ENABLE, E_STB_DISABLE, 0, {0}};

MuxSize SystemInfo::m_MuxConf = {0};
MAPI_VIDEO_INPUTSRCTABLE* SystemInfo::m_pU32MuxInfo = NULL;

MAPI_U32* SystemInfo::m_pU32HdmiEdidVersionList = NULL;

ATVExtDemodInfo_t SystemInfo::m_ATVExtDemod = { FALSE, 0 };

ScartInfo_t SystemInfo::m_ScartInfo = { 0 };

WDTInfo_t SystemInfo::m_WDTInfo = {FALSE, 0 , 10};

unsigned char SystemInfo::m_bHbbtvDelayInitFlag= FALSE;

GPIOConfig_t SystemInfo::m_gpioconfig = {0};
GPIOInfo_t* SystemInfo::m_pGPIOColl = NULL;

I2CConfig_t SystemInfo::m_i2cconfig  = {0, 0};
I2CBus_t* SystemInfo::m_pI2CBuses  = NULL;
I2CDeviceInfo_t* SystemInfo::m_pI2CDevices = NULL;

MSPI_pad_info_t* SystemInfo::m_pMSPIPadInfo = NULL;
MSPI_config_t SystemInfo::m_mspiconfig = {0, 0};
MSPIConfig_t* SystemInfo::m_pMSPIDevices = NULL;

EN_VD_CAPTURE_WINDOW_MODE SystemInfo::m_VDCaptureWinMode = EN_Mode_Dynamic;//EN_Mode_1135;

DMXConf_t SystemInfo::m_DMXRoute[MAXROUTECOUNT] = {{{0, FALSE, FALSE, FALSE}, {3, FALSE, FALSE, TRUE}},  \
    {{0, FALSE, FALSE, FALSE}, {0, FALSE, FALSE, FALSE}}
};

AudioMux_t* SystemInfo::m_pAudioMuxInfo = NULL;
AudioPath_t* SystemInfo::m_pAudioPathInfo = NULL;
AudioOutputType_t* SystemInfo::m_pAudioOutputTypeInfo = NULL;
AudioDefualtInit_t SystemInfo::m_AudioDefaultInit = {MAPI_AUDIO_SOURCE_DTV, MAPI_AUDIO_PATH_MAIN_SPEAKER, MAPI_AUDIO_OUTPUT_MAIN_SPEAKER};
VolumeCurve_t SystemInfo::m_VolumeCurve = { FALSE };

unsigned char SystemInfo::m_bEnablePictureModeUseFacCurve = FALSE;
PictureModeCurve_t SystemInfo::m_PictureModeCurve = {{0},{0},{0},{0},{0}};

GammaTableSize_t SystemInfo::m_GammaConf = {0};
GAMMA_TABLE_t* SystemInfo::m_pGammaTableInfo = NULL;
unsigned char SystemInfo::m_u8DefaultGammaIdx = 0;


std::string  SystemInfo::pMainPQPath;
std::string  SystemInfo::pSubPQPath;
#if ( INTEL_WIDI_ENABLE == 1 )
WidiInfo_t SystemInfo::m_WidiInfo = {0};
#endif
#if(MULTI_DEMOD==1)
stMultiExtendDemodCfg SystemInfo::m_MultiDemodParam={0,0};
#endif

#if (ENABLE_LITE_SN == 0)
PanelInfo_t* SystemInfo::m_p4K2KModelInfo= NULL;
#endif
PanelBacklightPWMInfo SystemInfo::m_PanelBacklightPWMInfo={0,0,0,0,0,0};
PanelModPvddPowerInfo SystemInfo::m_PanelModPvddPowerInfo={FALSE, 1};
unsigned short SystemInfo::m_u16LVDS_Output_type = 0;

unsigned short SystemInfo::m_u16PanelLinkExtType = E_MAPI_LINK_EXT;

MAPI_AUTO_NR_INIT_PARAM SystemInfo::m_AutoNrParam = {0, 0, 0, 0, 0};

SawArchitecture SystemInfo::enSawType = EXTERNAL_SINGLE_SAW;

SarChannel SystemInfo::enSarChannel = E_SAR_NC;

unsigned char* SystemInfo::m_u8BOARD_DVBT_DSPRegInitExt = NULL;
unsigned char* SystemInfo::m_u8BOARD_DMD_DVBT_InitExt = NULL;
unsigned char* SystemInfo::m_u8BOARD_DVBC_DSPRegInitExt = NULL;
unsigned char* SystemInfo::m_u8BOARD_DMD_DVBC_InitExt = NULL;
unsigned char* SystemInfo::m_u8BOARD_ATSC_DSPRegInitExt = NULL;
unsigned char* SystemInfo::m_u8BOARD_DMD_ATSC_InitExt = NULL;

PcModeTimingTable_t* SystemInfo::m_pPcModeTimingTable = NULL;
unsigned char SystemInfo::m_u8PcModeTimingTableCount = 0;

VGA_EDID_Info_t SystemInfo::m_VgaEdidInfo = { FALSE, FALSE };
HDMI_EDID_InfoSet_t SystemInfo::m_HdmiEdidInfoSet = { 0 };
#if (STB_ENABLE == 1)
HDMITx_Analog_Info_t *SystemInfo::m_pHdmiTxAnalogInfo = NULL;
#endif

unsigned char SystemInfo::m_bMirrorVideo    = FALSE;
unsigned char SystemInfo::m_u8MirrorMode   =MIRROR_HV;
unsigned char SystemInfo::m_bEnable3DOverScan = FALSE;
#if (ENABLE_LITE_SN == 0)
unsigned short SystemInfo::m_u16SwingLevel = 250;   //the value is utopia default's value
#endif

unsigned char SystemInfo::m_bEnable3DPanelLRInverse = FALSE;
unsigned char SystemInfo::m_bEnableFreerun[2] = {FALSE, FALSE};
unsigned char SystemInfo::m_bIsSGPanel =  FALSE;
unsigned char SystemInfo::m_bIsXCOutput120hzSGPanel = FALSE;
MAPI_U32 SystemInfo::m_u32FBLThreshold = 0XFFFFFFFF;


ResolutionInfoSize SystemInfo::m_ResoSize[5] = {{0}, {0}, {0}, {0}, {0}};
void **SystemInfo::m_pVideoWinInfo[5] = {NULL, NULL, NULL, NULL, NULL};
MAPI_U32 SystemInfo::m_u32HotPlugInverse = 0xFFFF;
MAPI_U32 SystemInfo::m_u32Hdmi5vDetectGpioSelect = 0xFFFF;
unsigned char SystemInfo::m_bDotByDotAble   = FALSE;

unsigned char SystemInfo::m_bEnableVolumeCom = FALSE;
MAPI_U16 SystemInfo::m_u16AvSyncDelay = 0;
MAPI_U8 SystemInfo::m_u8UseAudioDelayOffset = 0;
MAPI_U8 SystemInfo::m_u8UseSPDIFDelayOffset = 0;
MAPI_S16 SystemInfo::ChinaDescramblerBoxDelayOffset = 0;

TunerPWMInfo SystemInfo::m_TunerPWMInfo = {0xFF,0,0,0,0};
unsigned char  SystemInfo::m_u8UrsaEnable = FALSE;
unsigned char  SystemInfo::m_u8UrsaSelect = 0x00;
unsigned char  SystemInfo::m_u8MEMCPanelEnable = FALSE;
unsigned char  SystemInfo::m_u8MEMCPanelSelect = 0x00;
unsigned char  SystemInfo::m_u8AudioAmpSelect = 0x00;
MAPI_UrsaType SystemInfo::m_UrsaInfo = {0, 0, 0, 0, 0, 0, 0};
unsigned char  SystemInfo::m_u8EepromType = RM_DEFAULT_TYPE;
#if (SQL_DB_ENABLE == 1)
char* SystemInfo::SQL_DB_3DVideoRouterPath = NULL;
char* SystemInfo::SQL_DB_3DVideoRouterTableName = NULL;
char* SystemInfo::SQL_DB_DisplayModeRouterPath = NULL;
char* SystemInfo::SQL_DB_DisplayModeRouterTableName = NULL;
char* SystemInfo::SQL_DB_FactoryADCAdjustPath = NULL;
char* SystemInfo::SQL_DB_FactoryADCAdjustTableName = NULL;
char* SystemInfo::SQL_DB_FactoryColorTempPath = NULL;
char* SystemInfo::SQL_DB_FactoryColorTempTableName = NULL;
char* SystemInfo::SQL_DB_FactoryColorTempExPath = NULL;
char* SystemInfo::SQL_DB_FactoryColorTempExTableName = NULL;
char* SystemInfo::SQL_DB_NonLinearAdjustExPath = NULL;
char* SystemInfo::SQL_DB_NonLinearAdjustTableName = NULL;
char* SystemInfo::SQL_DB_NonLinearAdjust3DExPath = NULL;
char* SystemInfo::SQL_DB_NonLinearAdjust3DTableName = NULL;
char* SystemInfo::SQL_DB_FactoryColorTempEx3DPath = NULL;
char* SystemInfo::SQL_DB_FactoryColorTempEx3DTableName = NULL;
#endif

#if (ENABLE_LITE_SN == 0)

//for storage hdcp config
unsigned char SystemInfo::m_bNandHdcpEnable = FALSE;
unsigned char SystemInfo::m_bSPIHdcpEnable = FALSE;
unsigned char SystemInfo::m_u8HdcpSPIBank = 0x00;
unsigned short SystemInfo::m_u16HdcpSPIOffset = 0x0000;
unsigned char SystemInfo::m_bEEPROMHdcpEnable = FALSE;
unsigned char SystemInfo::m_u8HdcpEEPROMAddr = 0x00;


//for storage mac config
unsigned char SystemInfo::m_bSPIMacEnable = FALSE;
unsigned char SystemInfo::m_u8MacSPIBank = 0x00;
unsigned short SystemInfo::m_u16MacSPIOffset = 0x0000;

//for local dimming
unsigned char SystemInfo::m_bLocalDIMMINGEnable = FALSE;
unsigned char SystemInfo::m_u8LocalDIMMINGPanelSelect = 0x00;
#endif

//signal detected count for pc and hdmi source
stSignalDetectCount SystemInfo::stPcDetectCountInfo = {0,0};
stSignalDetectCount SystemInfo::stHDMIDetectCountInfo = {0,0};
stSignalDetectCount SystemInfo::stCompDetectCountInfo={0,0};

std::string  SystemInfo::pAmpInitBinPath;
std::string  SystemInfo::m_bHDCPKeyFileName;
vector<std::string>  SystemInfo::m_sTunerSelect;

unsigned char SystemInfo::m_bEnableHdcp[HDMI_PORT_MAX] = {TRUE, TRUE,TRUE,TRUE};

char* SystemInfo::m_u8VideoFileName = NULL;

//EosTek Patch Begin
char* SystemInfo::m_u8VideoFileNameAlternative = NULL;
//EosTek Patch End

MAPI_U8 SystemInfo::m_u8PowerOnNetflixKey = 0xFF;

// video zoom info
ST_MAPI_VIDEO_ZOOM_INFO SystemInfo::m_stDefaultZoomInfo[2] =
{
    {100, 100, 120, 120 }, // zoom 2
    { 50,  50,  60,  60 }, // zoom 1
};

ST_MAPI_VIDEO_ZOOM_INFO SystemInfo::m_stCvbsZoomInfo[2] =
{
    {100, 100, 120, 120 }, // zoom 2
    { 50,  50,  60,  60 }, // zoom 1
};
ST_MAPI_VIDEO_ZOOM_INFO SystemInfo::m_stYpbprZoomInfo[2] =
{
    {100, 100, 120, 120}, //Zoom2
    { 50,  50,  60,  60}, //Zoom1
};
ST_MAPI_VIDEO_ZOOM_INFO SystemInfo::m_stHdmiZoomInfo[2] =
{
    {100, 100, 120, 120}, //Zoom2
    { 50,  50,  60,  60}, //Zoom1
};
ST_MAPI_VIDEO_ZOOM_INFO SystemInfo::m_stDtvZoomInfo[2] =
{
    {100, 100, 120, 120}, //Zoom2
    { 50,  50,  60,  60}, //Zoom1
};

MAPI_XC_HDR_LEVEL_ATTRIBUTES SystemInfo::m_stHdrLevelAttributes[E_MAPI_XC_HDR_MAX] =
{
    {17, 410, 920, 64, 420, 712, 410, 420, 300, 500},
    {17, 438, 920, 54, 485, 712, 430, 480, 400, 400},
    {17, 418, 920, 54, 510, 712, 418, 510, 200, 500}
};

unsigned char SystemInfo::m_u8CISlotCount = 0;
MAPI_SECTION_FILTER_CONFIG SystemInfo::m_stTspSectionFilterCfg = {0};
MAPI_U32 SystemInfo::m_u32PesFilterNumber = 0;

unsigned char SystemInfo::m_u8DLCTableCount = 0;
MAPI_XC_DLC_init *SystemInfo::m_psXC_DLC_InitData = NULL;
MAPI_COLOR_MATRIX *SystemInfo::m_pColorMatrix = NULL;

PanelInfo_t * SystemInfo::m_pPanelInfo = NULL;


#if 1
#define SYSTEM_INFO_ERR(format, ...)   \
                            do{\
                                    printf("[SYSTEM][INFO][ERROR] %s:%d: ",__FUNCTION__,__LINE__);\
                                    printf(format, ##__VA_ARGS__);\
                            }while(0)
#define SYSTEM_INFO_DBG(format, ...)
#define SYSTEM_INFO_IFO(format, ...)
#define SYSTEM_INFO_FLOW(format, ...)
#else
#define SYSTEM_INFO_ERR(format, ...)   \
                            do{\
                                    printf("[SYSTEM][INFO][ERROR] %s:%d: ",__FUNCTION__,__LINE__);\
                                    printf(format, ##__VA_ARGS__);\
                            }while(0)

#define SYSTEM_INFO_DBG(format, ...)  \
                            do{\
                                    printf("[SYSTEM][INFO][DEBUG] %s:%d: ",__FUNCTION__,__LINE__);\
                                    printf(format, ##__VA_ARGS__);\
                            }while(0)

#define SYSTEM_INFO_IFO(format, ...)   \
                            do{\
                                    printf("[SYSTEM][INFO][INFO] %s:%d: ",__FUNCTION__,__LINE__);\
                                    printf(format, ##__VA_ARGS__);\
                            }while(0)

#define SYSTEM_INFO_FLOW(format, ...)   \
                            do{\
                                    printf("[SYSTEM][INFO][FLOW] %s:%d: ",__FUNCTION__,__LINE__);\
                                    printf(format, ##__VA_ARGS__);\
                            }while(0)
#endif


#if (STB_ENABLE == 1)
#define DLC_INI_UPDATE_PATH "/config/DLC/DLC.ini"
#define COLORMATRIX_INI_UPDATE_PATH "/config/ColorMatrix/ColorMatrix.ini"
#else
#define DLC_INI_UPDATE_PATH "/Customer/DLC/DLC.ini"
#define COLORMATRIX_INI_UPDATE_PATH "/Customer/ColorMatrix/ColorMatrix.ini"
#endif
#define I2C_BIN_PATH "/config/I2C_BIN/I2C.bin"
#define MOUNT_FOLDER "/Customer/UpdatePQSetting"
#define UPDATE_PQ_LOG "/Customer/UpdatePQSetting/updatePQ.log"
#define DLC_UPDATE_SOURCE "/Customer/UpdatePQSetting/DLC.ini"
#define COLORMATRIX_UPDATE_SOURCE "/Customer/UpdatePQSetting/ColorMatrix.ini"
#define GAMMA0_UPDATE_SOURCE "/Customer/UpdatePQSetting/gamma0.ini"
#define BANDWIDTH_REG_TABLE_FILE "Bandwidth_RegTable.bin"
#define PQ_MAIN_FILE "Main.bin"
#define PQ_MAIN_TEXT_FILE "Main_Text.bin"
#define PQ_MAIN_EX_FILE "Main_Ex.bin"
#define PQ_MAIN_EX_TEXT_FILE "Main_Ex_Text.bin"
#define PQ_SUB_FILE "Sub.bin"
#define PQ_SUB_TEXT_FILE "Sub_Text.bin"
#define PQ_SUB_EX_FILE "Sub_Ex.bin"
#define PQ_SUB_EX_TEXT_FILE "Sub_Ex_Text.bin"
#define UPDATE_PQ_FILES_COUNT 12

#define CusBackupModelMode  1
#define CusBackupPanelMode  2
#define CusBackupDLCMode  3
#define CusBackupColorMatrixMode  4
#define CusBackupPcMode  5

#define PANEL_NAME_LEN  32
unsigned int* iniparser_getU32array(dictionary * pdic, const char * pkey,unsigned int &count);

inline ssize_t mmc_read(int fd, unsigned char *buf, size_t count,int offset)
{
    ssize_t _n =0;
    ssize_t n;

    do {
        n = pread(fd, buf+_n, count,offset+_n);
    } while (n < 0 && errno == EINTR);
    _n += n;

    return _n;
}

pthread_mutex_t SystemInfo::m_moduleParameter_mutex = PTHREAD_MUTEX_INITIALIZER;

void SystemInfo::SetSystemIniDefaultValue(int ver)
{
    switch(ver)
    {
//      Example:
//      case    SystemIniVer00010503:
//                 Default Value For Ver1.5.3
//                 User can add init default value here...
//      break;
//


        default:
            SYSTEM_INFO_ERR("Wrong SetSystemIniDefaultValue (ver = %d)!\n", (int)ver);
            ASSERT(0);
            break;
    }


}

void SystemInfo::SetPanelIniDefaultValue(int ver)
{
    switch(ver)
    {
//      Example:
//      case    PanelIniVer00010503:
//                 Default Value For Ver1.5.3
//                 User can add init default value here...
//      break;
//

            SYSTEM_INFO_ERR("Wrong SetSystemIniDefaultValue (ver = %d)!\n", (int)ver);
            ASSERT(0);

    }

}

void SystemInfo::SetDCLIniDefaultValue(int ver)
{
    switch(ver)
    {
//      Example:
//      case    DCLIniVer00010503:
//                 Default Value For Ver1.5.3
//                 User can add init default value here...
//      break;
//
        default:
            SYSTEM_INFO_ERR("Wrong SetSystemIniDefaultValue (ver = %d)!\n", (int)ver);
            ASSERT(0);
            break;
    }

}

void SystemInfo::FillNewStructDefaultValue(IniType type, int ver)
{

    switch(type)
    {
        case SystemIniType:
            SetSystemIniDefaultValue(ver);
            break;
        case PanelIniType:
            SetPanelIniDefaultValue(ver);
            break;
        case DCLIniType:
            SetDCLIniDefaultValue(ver);
            break;
        default:
            SYSTEM_INFO_ERR("Wrong FillNewStructDefaultValue type(type = %d)!\n", (int)type);
            ASSERT(0);
            break;
    }
}

void SystemInfo::CheckVersionAndSetDefaultValue(IniType type)
{
    int SystemIniVerList[SystemIniVerNum]={SystemIniVer001};
    int PanelIniVerList[SystemIniVerNum]={PanelIniVer001};
    int DCLIniVerList[SystemIniVerNum]={DCLIniVer001};
    int ClrCorrectIniVerList[SystemIniVerNum]={ColorCorrectionIniVer001};

    dictionary *pDic=NULL;

    int iIniCurrentVer;
    int iLastestVer;
    int iIniVerNum;
    int *pVerList;

    if(type==0)
    {
        pDic = m_pCustomerini;
        iLastestVer=  SystemIniVersion;
        iIniVerNum= SystemIniVerNum;
        pVerList=SystemIniVerList;
    }
    else if(type==1)
    {
        pDic = m_pPanelini;
        iLastestVer=  PanelIniVersion;
        iIniVerNum= PanelIniVerNum;
        pVerList=PanelIniVerList;
    }
    else if(type==2)
    {
        pDic = m_pDCLIni;
        iLastestVer=  DCLIniVersion;
        iIniVerNum= DCLIniVerNum;
        pVerList=DCLIniVerList;
    }
    else if(type==3)
    {
        pDic = m_pMatrixIni;
        iLastestVer=  ColorCorrectionIniVersion;
        iIniVerNum= ColorCorrectionIniVerNum;
        pVerList=ClrCorrectIniVerList;
    }
    else
    {
        SYSTEM_INFO_ERR("Wrong FillNewStructDefaultValue type(type = %d)!\n", (int)type);
        ASSERT(0);
     }

    if(pDic==NULL)
    {
        SYSTEM_INFO_ERR("Can not open ini file. \n");
        ASSERT(0);
    }

    iIniCurrentVer     = iniparser_getint(pDic, "IniVersion:Ver", -1);


//printf("iIniCurrentVer : %d\n",iIniCurrentVer);
//printf("iLastestVer : %d\n",iLastestVer);


    if(iIniCurrentVer<iLastestVer)
    {
        //update ini file data to the latest version.
        for(int i=0; i<iIniVerNum; i++)
        {
            if(iIniCurrentVer < pVerList[i])
            {
                   FillNewStructDefaultValue(type, pVerList[i]);
            }
        }
     }

}

SystemInfo::SystemInfo()
{
    m_pSystemini = NULL;
    m_pCustomerini = NULL;
    m_pPanelini = NULL;
    m_pDCLIni = NULL;
    m_pDBIni = NULL;
    m_pMatrixIni = NULL;
    m_pPcModeIni = NULL;
    m_pBoardini = NULL;
    m_pModuleDefaultIni = NULL;
    m_pModuleIni = NULL;

#if (PIP_ENABLE == 1)
    m_pPipModeIni = NULL;
    m_pPopModeIni = NULL;
    m_pTravelingModeIni = NULL;
#endif
    m_u16SpiProjectID = 0xFFFF;
    m_pModelName = NULL;

    m_bLoadIniStatus = FALSE;

#if (STEREO_3D_ENABLE == 1)
    memset(&m_stGlobalPanelInfo, 0, sizeof(MAPI_PanelType));
#endif

#if (SQL_DB_ENABLE==1)
    m_bIsSqlDbSet=false;
    m_p3DVideoRouterPath=NULL;
    m_p3DVideoRouterTableName=NULL;
    m_p3DTo2DVideoRouterPath=NULL;
    m_p3DTo2DVideoRouterTableName=NULL;
    m_pDisplayModeRouterPath=NULL;
    m_pDisplayModeRouterTableName=NULL;
    m_p4K2K3DVideoRouterPath=NULL;
    m_p4K2K3DVideoRouterTableName=NULL;
    m_p4K2K60Hz3DVideoRouterPath=NULL;
    m_p4K2K60Hz3DVideoRouterTableName=NULL;
    m_pFactoryADCAdjustPath=NULL;
    m_pFactoryADCAdjustTableName=NULL;
    m_pFactoryColorTempPath=NULL;
    m_pFactoryColorTempTableName=NULL;
    m_pFactoryColorTempExPath=NULL;
    m_pFactoryColorTempExTableName=NULL;
    m_pNonLinearAdjustPath=NULL;
    m_pNonLinearAdjustTableName=NULL;
    m_pNonLinearAdjust3DPath=NULL;
    m_pNonLinearAdjust3DTableName=NULL;
    m_pFactoryColorTempEx3DPath=NULL;
    m_pFactoryColorTempEx3DTableName=NULL;
#endif

    m_u16PanelSize = 0;
    m_u16MaxPanelSize = 0;
    m_enActivePanelTiming = E_MAPI_TIMING_UNDEFINED;
    m_PanelPathMap.clear();
    m_PanelMap.clear();

    memset(m_u16Vb18VChannelOrder, 0, sizeof(m_u16Vb18VChannelOrder));
    memset(m_u16Vb14VChannelOrder, 0, sizeof(m_u16Vb14VChannelOrder));
    memset(m_u16Vb12VChannelOrder, 0, sizeof(m_u16Vb12VChannelOrder));
    memset(m_u16Vb14OChannelOrder, 0, sizeof(m_u16Vb14OChannelOrder));
    memset(m_u16Vb12OChannelOrder, 0, sizeof(m_u16Vb12OChannelOrder));

}

SystemInfo::~SystemInfo()
{
#if (STB_ENABLE == 0)
    if(m_pModuleDefaultIni!=NULL)
    {
        iniparser_freedict(m_pModuleDefaultIni);
        m_pModuleDefaultIni=NULL;
    }
    if(m_pModuleIni!=NULL)
    {
        iniparser_freedict(m_pModuleIni);
        m_pModuleIni=NULL;
    }
    if(m_pCustomerini!=NULL)
    {
        iniparser_freedict(m_pCustomerini);
        m_pCustomerini=NULL;
    }
#endif

    if(m_pU32MuxInfo != NULL)
    {
        delete [] m_pU32MuxInfo;
        m_pU32MuxInfo = NULL;
    }

    delete [] m_pGPIOColl;
    m_pGPIOColl = NULL;

    delete [] m_pI2CBuses;
    m_pI2CBuses = NULL;
    delete [] m_pI2CDevices;
    m_pI2CDevices = NULL;
    delete [] m_pMSPIPadInfo;
    m_pMSPIPadInfo = NULL;

    if(m_pAudioMuxInfo != NULL)
    {
        delete [] m_pAudioMuxInfo;
        m_pAudioMuxInfo = NULL;
    }

    if(m_pAudioPathInfo != NULL)
    {
        delete [] m_pAudioPathInfo;
        m_pAudioPathInfo = NULL;
    }

    if(m_pAudioOutputTypeInfo != NULL)
    {
        delete [] m_pAudioOutputTypeInfo;
        m_pAudioOutputTypeInfo = NULL;
    }

    if(m_pGammaTableInfo != NULL)
    {
        delete [] m_pGammaTableInfo;
        m_pGammaTableInfo = NULL;
    }

    m_PanelPathMap.clear();
    m_PanelMap.clear();
    DestoryPanelInfo(m_u16MaxPanelSize);

    if (m_pPcModeTimingTable)
    {
        delete [] m_pPcModeTimingTable;
        m_pPcModeTimingTable = NULL;
    }

    if(m_pU32HdmiEdidVersionList != NULL)
    {
        delete [] m_pU32HdmiEdidVersionList;
        m_pU32HdmiEdidVersionList = NULL;
    }

    ST_MAPI_VIDEO_WINDOW_INFO **vptr = NULL;
    for(int i = 0 ; i < (int)E_VIDEOINFO_ID_MAX ; i++)
    {
        vptr = (ST_MAPI_VIDEO_WINDOW_INFO **)m_pVideoWinInfo[i];
        if(vptr != NULL)
        {
            for(int j=0; j< m_ResoSize[i].nResolutionSize ; j ++)
            {
                if(vptr[j] != NULL)
                {
                    delete [] vptr[j] ;
                    vptr[j] = NULL;
                }
            }
            delete [] vptr;
            vptr = NULL;
        }
    }

#if (SQL_DB_ENABLE==1)
    if(NULL!=m_p3DVideoRouterPath)
    {
        free(m_p3DVideoRouterPath);
    }
    if(NULL!=m_p3DVideoRouterTableName)
    {
        free(m_p3DVideoRouterTableName);
    }
    if(NULL!=m_p3DTo2DVideoRouterPath)
    {
        free(m_p3DTo2DVideoRouterPath);
    }
    if(NULL!=m_pDisplayModeRouterPath)
    {
        free(m_pDisplayModeRouterPath);
    }
    if(NULL!=m_pDisplayModeRouterTableName)
    {
        free(m_pDisplayModeRouterTableName);
    }
    if(NULL!=m_p3DTo2DVideoRouterTableName)
    {
        free(m_p3DTo2DVideoRouterTableName);
    }
    if(NULL!=m_p4K2K3DVideoRouterPath)
    {
        free(m_p4K2K3DVideoRouterPath);
    }
    if(NULL!=m_p4K2K3DVideoRouterTableName)
    {
        free(m_p4K2K3DVideoRouterTableName);
    }
    if(NULL!=m_p4K2K60Hz3DVideoRouterPath)
    {
        free(m_p4K2K60Hz3DVideoRouterPath);
    }
    if(NULL!=m_p4K2K60Hz3DVideoRouterTableName)
    {
        free(m_p4K2K60Hz3DVideoRouterTableName);
    }
    if(NULL!=m_pFactoryADCAdjustPath)
    {
        free(m_pFactoryADCAdjustPath);
    }
    if(NULL!=m_pFactoryADCAdjustTableName)
    {
        free(m_pFactoryADCAdjustTableName);
    }
    if(NULL!=m_pFactoryColorTempPath)
    {
        free(m_pFactoryColorTempPath);
    }
    if(NULL!=m_pFactoryColorTempTableName)
    {
        free(m_pFactoryColorTempTableName);
    }
    if(NULL!=m_pFactoryColorTempExPath)
    {
        free(m_pFactoryColorTempExPath);
    }
    if(NULL!=m_pFactoryColorTempExTableName)
    {
        free(m_pFactoryColorTempExTableName);
    }
    if(NULL!=m_pNonLinearAdjustTableName)
    {
        free(m_pNonLinearAdjustTableName);
    }
    if(NULL!=m_pNonLinearAdjust3DPath)
    {
        free(m_pNonLinearAdjust3DPath);
    }
    if(NULL!=m_pNonLinearAdjust3DTableName)
    {
        free(m_pNonLinearAdjust3DTableName);
    }
    if(NULL!=m_pNonLinearAdjustPath)
    {
        free(m_pNonLinearAdjustPath);
    }
    if(NULL!=m_pFactoryColorTempEx3DPath)
    {
        free(m_pFactoryColorTempEx3DPath);
    }
    if(NULL!=m_pFactoryColorTempEx3DTableName)
    {
        free(m_pFactoryColorTempEx3DTableName);
    }

    if(SQL_DB_3DVideoRouterPath != NULL)
    {
        delete [] SQL_DB_3DVideoRouterPath;
        SQL_DB_3DVideoRouterPath = NULL;
    }

    if(SQL_DB_3DVideoRouterTableName != NULL)
    {
        delete [] SQL_DB_3DVideoRouterTableName;
        SQL_DB_3DVideoRouterTableName = NULL;
    }

    if(SQL_DB_FactoryADCAdjustPath != NULL)
    {
        delete [] SQL_DB_FactoryADCAdjustPath;
        SQL_DB_FactoryADCAdjustPath = NULL;
    }

    if(SQL_DB_FactoryADCAdjustTableName != NULL)
    {
        delete [] SQL_DB_FactoryADCAdjustTableName;
        SQL_DB_FactoryADCAdjustTableName = NULL;
    }

    if(SQL_DB_FactoryColorTempPath != NULL)
    {
        delete [] SQL_DB_FactoryColorTempPath;
        SQL_DB_FactoryColorTempPath = NULL;
    }

    if(SQL_DB_FactoryColorTempTableName != NULL)
    {
        delete [] SQL_DB_FactoryColorTempTableName;
        SQL_DB_FactoryColorTempTableName = NULL;
    }

    if(SQL_DB_FactoryColorTempExPath != NULL)
    {
        delete [] SQL_DB_FactoryColorTempExPath;
        SQL_DB_FactoryColorTempExPath = NULL;
    }

    if(SQL_DB_FactoryColorTempExTableName != NULL)
    {
        delete [] SQL_DB_FactoryColorTempExTableName;
        SQL_DB_FactoryColorTempExTableName = NULL;
    }

    if(SQL_DB_NonLinearAdjustExPath != NULL)
    {
        delete [] SQL_DB_NonLinearAdjustExPath;
        SQL_DB_NonLinearAdjustExPath = NULL;
    }

    if(SQL_DB_NonLinearAdjustTableName != NULL)
    {
        delete [] SQL_DB_NonLinearAdjustTableName;
        SQL_DB_NonLinearAdjustTableName = NULL;
    }

    if(SQL_DB_NonLinearAdjust3DExPath != NULL)
    {
        delete [] SQL_DB_NonLinearAdjust3DExPath;
        SQL_DB_NonLinearAdjust3DExPath = NULL;
    }

    if(SQL_DB_NonLinearAdjust3DTableName != NULL)
    {
        delete [] SQL_DB_NonLinearAdjust3DTableName;
        SQL_DB_NonLinearAdjust3DTableName = NULL;
    }

    if(SQL_DB_FactoryColorTempEx3DPath != NULL)
    {
        delete [] SQL_DB_FactoryColorTempEx3DPath;
        SQL_DB_FactoryColorTempEx3DPath = NULL;
    }

    if(SQL_DB_FactoryColorTempEx3DTableName != NULL)
    {
        delete [] SQL_DB_FactoryColorTempEx3DTableName;
        SQL_DB_FactoryColorTempEx3DTableName = NULL;
    }
#endif

    if (m_pColorMatrix)
    {
        delete [] m_pColorMatrix;
        m_pColorMatrix = NULL;
    }

    if (m_psXC_DLC_InitData)
    {
        delete [] m_psXC_DLC_InitData;
        m_psXC_DLC_InitData= NULL;
    }

    m_pInstance = NULL;
}

SystemInfo* SystemInfo::GetInstance(void)
{
    if(m_pInstance == NULL)
    {
        m_pInstance = new (std::nothrow) SystemInfo;
        ASSERT(m_pInstance);
    }
    return (SystemInfo*)m_pInstance;
}

MAPI_BOOL SystemInfo::Init(void)
{
    return TRUE;
}


MAPI_BOOL SystemInfo::Finalize(void)
{
    return TRUE;
}


MAPI_BOOL SystemInfo::CheckPattern(MAPI_U8 *pu8Buffer, MAPI_U32 u32ReadSize)
{
    MAPI_BOOL bFindPattern=0;
    MAPI_U32 u32Index=0;

    while(u32ReadSize>0)
    {
        if ((pu8Buffer[u32Index] == '#') && (pu8Buffer[u32Index+1] =='@') && (pu8Buffer[u32Index+2] == 'C') ) //find pattern
        {
            SYSTEM_INFO_DBG("find CS pattern \n");
            SYSTEM_INFO_DBG("CS data1 [0x%x] \n",pu8Buffer[u32Index+8]);
            SYSTEM_INFO_DBG("CS data2 [0x%x] \n",pu8Buffer[u32Index+9]);
            SYSTEM_INFO_DBG("CS data3 [0x%x] \n",pu8Buffer[u32Index+10]);
            SYSTEM_INFO_DBG("CS data4 [0x%x] \n",pu8Buffer[u32Index+11]);
            u32ReadSize =0;
            bFindPattern=1;
        }
        else
        {
            u32Index++;
        }

        if (u32Index >=5 )
        {
            u32ReadSize=0;
            bFindPattern=0;
            SYSTEM_INFO_DBG("not find CS pattern \n");
        }
    }
    return bFindPattern;
}


MAPI_U32 SystemInfo::CalculateSimpleCS(unsigned char *pu8Buffer, MAPI_S32 s32Filelength)
{
    MAPI_U32 u32CalculateCS=0;
    MAPI_U32 u32Index=0;

    while(s32Filelength > 0 )
    {
        u32CalculateCS += pu8Buffer[u32Index];
        if (u32CalculateCS >= 0xFFFF)
        {
            u32CalculateCS =0;
        }
        s32Filelength--;
        u32Index++;
    }
    return  u32CalculateCS;
}

unsigned char SystemInfo::iniparser_UpdateCS(const char * ininame)
{
   SYSTEM_INFO_DBG(" %s\n",__FUNCTION__);

   unsigned char bRes = 0;
   FILE *pFile =NULL;
   unsigned char u8FindPattern=0;
   int iFilelength=0;
   unsigned int u32FileSize=0;
   unsigned char *pu8Buffer =NULL;
   unsigned int u32ReadSize=0;
   //unsigned int u32Index=0;
   unsigned int  u32CalculateCS=0;

   pFile = fopen(ininame,"rb");
   if(pFile == NULL)
   {
       SYSTEM_INFO_DBG("Open file Failed\n");
       return bRes;
   }

    fseek(pFile, 0, SEEK_END);
    iFilelength = ftell(pFile);
    if ((iFilelength == 0) || (iFilelength <= CRC_PATTERN_SIZE) )
    {
       SYSTEM_INFO_DBG("file size fail \n");
       fclose(pFile);
       pFile =NULL;
       return bRes;
    }

    fseek(pFile, -CRC_PATTERN_SIZE, SEEK_END); //seek to pattern
    pu8Buffer = (unsigned char*) malloc(iFilelength+CRC_PATTERN_SIZE);
    if (pu8Buffer == NULL)
    {
        SYSTEM_INFO_DBG("malloc fail \n");
        fclose(pFile);
        pFile =NULL;
        return bRes;
    }

    //check pattern
    memset(pu8Buffer,0x0,CRC_PATTERN_SIZE+iFilelength);
    //u32Index =0;
    u32ReadSize = fread(pu8Buffer, 1, CRC_PATTERN_SIZE, pFile);
    u8FindPattern=CheckPattern(pu8Buffer, u32ReadSize);

    rewind(pFile);
    memset(pu8Buffer,0x0,CRC_PATTERN_SIZE+iFilelength);
    u32ReadSize = fread(pu8Buffer, 1, iFilelength, pFile);// read total data

    fclose(pFile);
    pFile = NULL;

    // Calculate CS
    //u32Index =0;
    u32CalculateCS =0;
    u32FileSize = iFilelength;
    if (u8FindPattern)
    {
        iFilelength -=CRC_PATTERN_SIZE;
    }
    u32CalculateCS=CalculateSimpleCS(pu8Buffer, iFilelength);

    //u32Index=0;
    iFilelength = u32FileSize;

    SYSTEM_INFO_DBG("Calculate CS [0x%x] \n",u32CalculateCS);

    // update CS
    if (u8FindPattern)
    {
        pu8Buffer[iFilelength -4]  = (((u32CalculateCS>>12)&0x0F) <= 9)? (((u32CalculateCS>>12)&0x0F)+0x30):( ((u32CalculateCS>>12)&0x0F)-0x0A + 0x41);
        pu8Buffer[iFilelength -3]  = (((u32CalculateCS>>8)&0x0F) <= 9)? (((u32CalculateCS>>8)&0x0F)+0x30):( ((u32CalculateCS>>8)&0x0F)-0x0A + 0x41);
        pu8Buffer[iFilelength -2]  = (((u32CalculateCS>>4)&0x0F) <= 9)? (((u32CalculateCS>>4)&0x0F)+0x30):( ((u32CalculateCS>>4)&0x0F)-0x0A + 0x41);
        pu8Buffer[iFilelength -1]  = (((u32CalculateCS>>0)&0x0F) <= 9)? (((u32CalculateCS>>0)&0x0F)+0x30):( ((u32CalculateCS>>0)&0x0F)-0x0A + 0x41);
        SYSTEM_INFO_DBG("Updated CS data1 [0x%x] \n",pu8Buffer[iFilelength -4]);
        SYSTEM_INFO_DBG("Updated CS data2 [0x%x] \n",pu8Buffer[iFilelength -3]);
        SYSTEM_INFO_DBG("Updated CS data3 [0x%x] \n",pu8Buffer[iFilelength -2]);
        SYSTEM_INFO_DBG("Updated CS data4 [0x%x] \n",pu8Buffer[iFilelength -1]);

    }
    else
    {
        pu8Buffer[iFilelength] = '#';
        pu8Buffer[iFilelength+1] = '@';
        pu8Buffer[iFilelength+2] = 'C';
        pu8Buffer[iFilelength+3] = 'R';
        pu8Buffer[iFilelength+4] = 'C';
        pu8Buffer[iFilelength+5] = '=';
        pu8Buffer[iFilelength+6] = '0';
        pu8Buffer[iFilelength+7] = 'x';
        pu8Buffer[iFilelength+8]  = (((u32CalculateCS>>12)&0x0F) <= 9)? (((u32CalculateCS>>12)&0x0F)+0x30):( ((u32CalculateCS>>12)&0x0F)-0x0A + 0x41);
        pu8Buffer[iFilelength+9]  = (((u32CalculateCS>>8)&0x0F) <= 9)? (((u32CalculateCS>>8)&0x0F)+0x30):( ((u32CalculateCS>>8)&0x0F)-0x0A + 0x41);
        pu8Buffer[iFilelength+10]  = (((u32CalculateCS>>4)&0x0F) <= 9)? (((u32CalculateCS>>4)&0x0F)+0x30):( ((u32CalculateCS>>4)&0x0F)-0x0A + 0x41);
        pu8Buffer[iFilelength+11]  = (((u32CalculateCS>>0)&0x0F) <= 9)? (((u32CalculateCS>>0)&0x0F)+0x30):( ((u32CalculateCS>>0)&0x0F)-0x0A + 0x41);
        iFilelength = u32FileSize+CRC_PATTERN_SIZE;
        SYSTEM_INFO_DBG(">>Updated CS data1 [0x%x]<< \n",pu8Buffer[iFilelength -4]);
        SYSTEM_INFO_DBG(">>Updated CS data2 [0x%x]<< \n",pu8Buffer[iFilelength -3]);
        SYSTEM_INFO_DBG(">>Updated CS data3 [0x%x]<< \n",pu8Buffer[iFilelength -2]);
        SYSTEM_INFO_DBG(">>Updated CS data4 [0x%x]<< \n",pu8Buffer[iFilelength -1]);
    }

    // write CS
   pFile=fopen(ininame,"w");
   if(pFile == NULL)
   {
       SYSTEM_INFO_DBG("Open wirte file Failed\n");
        free(pu8Buffer);
       return bRes;
   }

    if(!fwrite(pu8Buffer, 1, iFilelength,  pFile))
    {
        SYSTEM_INFO_DBG("file write fail!!!!!!!!!!!!!!!!!\n");
        bRes =0;
    }
    else
    {
        SYSTEM_INFO_DBG("file write success!!!!!!!!!!!!!!!!!\n");
        bRes =1;
    }
    SYSTEM_INFO_DBG("===> file write %d bytes\n", iFilelength);


   fclose(pFile);
   pFile = NULL;
   free(pu8Buffer);
   pu8Buffer =NULL;

   return bRes;


}
unsigned char SystemInfo::iniparser_CheckCS(const char * ininame)
{
    unsigned char bRes = 0;
    FILE *pFile =NULL;
    int iFilelength=0;
    unsigned char u32ReadSize=0;
    unsigned short u16CS=0;
    unsigned char u8Index=0;
    unsigned int  u32CalculateCS=0;
    unsigned char u8ReadChar=0;
    unsigned char pu8Buffer[CRC_PATTERN_SIZE]={0};
    MAPI_BOOL bFindPattern=0;

    pFile = fopen(ininame,"rb");
    if(pFile == NULL)
    {
       SYSTEM_INFO_DBG("Open file Failed\n");
       return bRes;
    }

    fseek(pFile, 0, SEEK_END);
    iFilelength = ftell(pFile);
    if ((iFilelength == 0) || (iFilelength <= CRC_PATTERN_SIZE) )
    {
       SYSTEM_INFO_DBG("file size fail \n");
       fclose(pFile);
       pFile =NULL;
       return bRes;
    }

    fseek(pFile, -CRC_PATTERN_SIZE, SEEK_END); //seek to pattern


    //check pattern, read 12 bytes
    u8Index =0;
    u32ReadSize = fread(pu8Buffer, 1, CRC_PATTERN_SIZE, pFile);
    bFindPattern=CheckPattern(pu8Buffer, u32ReadSize);


    if(bFindPattern==1)
    {
        pu8Buffer[u8Index+8]  = (pu8Buffer[u8Index+8]>=0x41) ? (pu8Buffer[u8Index+8]-0x41+0x0A)   : (pu8Buffer[u8Index+8]-0x30);
        pu8Buffer[u8Index+9]  = (pu8Buffer[u8Index+9]>=0x41) ? (pu8Buffer[u8Index+9]-0x41+0x0A)   : (pu8Buffer[u8Index+9]-0x30);
        pu8Buffer[u8Index+10] = (pu8Buffer[u8Index+10]>=0x41)? (pu8Buffer[u8Index+10]-0x41+0x0A) : (pu8Buffer[u8Index+10]-0x30);
        pu8Buffer[u8Index+11] = (pu8Buffer[u8Index+11]>=0x41)? (pu8Buffer[u8Index+11]-0x41+0x0A) : (pu8Buffer[u8Index+11]-0x30);

        u16CS = (pu8Buffer[u8Index+8] << 12) |( pu8Buffer[u8Index+9] << 8) | ( pu8Buffer[u8Index+10] << 4) | ( pu8Buffer[u8Index+11] );
        SYSTEM_INFO_DBG("CS at file [0x%x] \n",u16CS);
    }
    else
    {
        SYSTEM_INFO_DBG("not find pattern \n");
        fclose(pFile);
        pFile =NULL;
        return bRes;
    }


    rewind(pFile);

    iFilelength = iFilelength -CRC_PATTERN_SIZE;
    u32CalculateCS =0;
    while((iFilelength>0) && (!feof(pFile)))
    {
        u8ReadChar = (unsigned char)fgetc(pFile);
        u32CalculateCS += u8ReadChar;

        if (u32CalculateCS >= 0xFFFF)
        {
            u32CalculateCS =0;
        }
        iFilelength--;
    }

    SYSTEM_INFO_DBG("Calculate CS [0x%x] \n",u32CalculateCS);

    if (u32CalculateCS == u16CS)
    {
        bRes = 1;
    }

    fclose(pFile);
    pFile = NULL;
   return bRes;
}

MAPI_BOOL SystemInfo::CustomerBackupPathFilename (const char * pPathFileName, char * pBkpFileFullName, int iSize,int mode )
{
    int ilen=0, iCustomerBackupPathLen=0, iNameLen=0,iModeLen;
    const char pCustomerBackupPath[]= BKP_CUSTOMER_PATH;
    char *pName=NULL;

    if((pPathFileName ==NULL) || (pBkpFileFullName ==NULL))
        return false;

    //copy the path and filename.
    ilen = strlen(pPathFileName);
    char *pPathFileName2  = new (std::nothrow) char[ilen+1];
    ASSERT(pPathFileName2);
    memset(pPathFileName2 , 0 , ilen + 1);
    memcpy(pPathFileName2 , pPathFileName, ilen);

    //panel filename
    pName= strrchr(pPathFileName2,'/');
    ASSERT(pName);
    pName++;
    SYSTEM_INFO_DBG("pName : %s \n", pName);

    // customerbackup path and  file name
    iCustomerBackupPathLen= strlen(pCustomerBackupPath);
    iNameLen = strlen(pName);
    iModeLen= 0;
    if(mode==CusBackupModelMode)///customerbackup/model/..
    {
        iModeLen=strlen("model/");
        strncpy (pBkpFileFullName,pCustomerBackupPath,iCustomerBackupPathLen);
        strncat (pBkpFileFullName,"model/",iModeLen);
        strncat (pBkpFileFullName,pName,iNameLen);
    }
    else if(mode==CusBackupPanelMode)///customerbackup/panel/..
    {
        iModeLen=strlen("panel/");
        strncpy (pBkpFileFullName,pCustomerBackupPath,iCustomerBackupPathLen);
        strncat (pBkpFileFullName,"panel/",iModeLen);
        strncat (pBkpFileFullName,pName,iNameLen);
    }
    else if(mode==CusBackupDLCMode)///customerbackup/DLC/..
    {
        iModeLen=strlen("DLC/");
        strncpy (pBkpFileFullName,pCustomerBackupPath,iCustomerBackupPathLen);
        strncat (pBkpFileFullName,"DLC/",iModeLen);
        strncat (pBkpFileFullName,pName,iNameLen);
    }
    else if(mode==CusBackupColorMatrixMode)///customerbackup/ColorCorrection/..
    {
        iModeLen=strlen("ColorMatrix/");
        strncpy (pBkpFileFullName,pCustomerBackupPath,iCustomerBackupPathLen);
        strncat (pBkpFileFullName,"ColorMatrix/",iModeLen);
        strncat (pBkpFileFullName,pName,iNameLen);
    }
    else if (mode==CusBackupPcMode)///customerbackup/pcmode/..
    {
        iModeLen=strlen("pcmode/");
        strncpy (pBkpFileFullName,pCustomerBackupPath,iCustomerBackupPathLen);
        strncat (pBkpFileFullName,"pcmode/",iModeLen);
        strncat (pBkpFileFullName,pName,iNameLen);
    }
    else
    {
        strncpy (pBkpFileFullName,pCustomerBackupPath,iCustomerBackupPathLen);
        strncat (pBkpFileFullName,pName,iNameLen);
    }

    delete [] pPathFileName2;
    pPathFileName2 = NULL;

    if(iSize < (iCustomerBackupPathLen+iNameLen+iModeLen))
          return false;

    return true;


}




MAPI_BOOL SystemInfo::CheckIniCS(void)
{
    const char pSysIniName[]=SYS_INI_PATH_FILENAME;
    const char pBkpSysIniName[]=BKP_SYS_INI_PATH_FILENAME;
    const char pPathBoardIni[] = PATH_BOARD_INI;
    const char pPathBoardIniBackup[] = PATH_BOARD_INI_BACKUP;
    MAPI_BOOL bCKS_Result1=0, bCKS_Result2=0;
   // char pDLCName[]=DLC_INI_PATH_FILENAME;
   // char pBkpDLCName[]={0};//BKP_DLC_PATH_FILENAME;
    char *pPanelPathFileName=NULL;
    const int iSize=255;
    char pBkpPanelFileFullName[iSize]={0};
    int iPanelPathFileNameLen=0;
    int iBkpPanelFileFullNameLen=0;
    int iPathFileNameLen = 0;
    int  iBkpFileFullNameLen = 0;
    char pCmdstr[iSize]={0};
    const char pCmdcp[] = "cp -vf ";
    char *pModelPathFileName=NULL;
    char pBkModelFileFullName[iSize]={0};
    char pCustomerIniName[iSize]={0};
    char pBkpName[iSize]={0};
    char *pDLCFullName;
    char *pClrCorrectFullName;
    char *pPcmodePathFileName;
    char pBkpPcmodeFileFullName[iSize]={0};
    int iPcmodePathFileNameLen = 0;
    int iBkpPcmodeFileFullNameLen = 0;

    //sys.ini
    bCKS_Result1=iniparser_CheckCS(pSysIniName);
    bCKS_Result2=iniparser_CheckCS(pBkpSysIniName);
    if((bCKS_Result1==0) || (bCKS_Result2==0))
    {
        if(bCKS_Result2 > bCKS_Result1)
        {
            SYSTEM_INFO_DBG("sys.ini checksum is error!!!!!!\n");
            system(Cmd_Recovery_Sys);
            sync();
        }
        else if(bCKS_Result2 < bCKS_Result1)
        {
            SYSTEM_INFO_DBG("Backup sys.ini checksum is error!!!!!\n");
            SystemCmd(Cmd_Recovery_SysBackup);
            sync();
        }
        else
        {
            SYSTEM_INFO_DBG("Both of sys.ini file and backup sys.ini file are checksum error!!!!!!\n");
            ASSERT(0);
        }
    }
    else
    {
        SYSTEM_INFO_DBG("Both of sys.ini file and backup sys.ini file are checksum correct\n");
    }

    //board.ini
    bCKS_Result1=iniparser_CheckCS(pPathBoardIni);
    bCKS_Result2=iniparser_CheckCS(pPathBoardIniBackup);
    if((bCKS_Result1==0) || (bCKS_Result2==0))
    {
        if(bCKS_Result2 > bCKS_Result1)
        {
            SYSTEM_INFO_DBG("board.ini checksum is error!!!!!!\n");
            system(CMD_RECOVERY_BOARD_INI);
            sync();
        }
        else if(bCKS_Result2 < bCKS_Result1)
        {
            SYSTEM_INFO_DBG("Backup board.ini checksum is error!!!!!\n");
            SystemCmd(CMD_RECOVERY_BOARD_INI_BACKUP);
            sync();
        }
        else
        {
            SYSTEM_INFO_DBG("Both of board.ini file and backup board.ini file are checksum error!!!!!!\n");
            ASSERT(0);
        }
    }
    else
    {
         SYSTEM_INFO_DBG("Both of board.ini file and backup board.ini file are checksum correct\n");
    }

    //model.ini
    pModelPathFileName = iniparser_getstr(m_pSystemini, "model:gModelName");
    if(pModelPathFileName == NULL)
    {
        SYSTEM_INFO_ERR("ERROR: The model name is empty.\n");
        ASSERT(0);
    }
    strncpy(pCustomerIniName,pModelPathFileName,strlen(pModelPathFileName)+1);

    if(CustomerBackupPathFilename (pModelPathFileName, pBkModelFileFullName, iSize, CusBackupModelMode)==false)
    {
        SYSTEM_INFO_DBG("CustomerBackupPathFilename() == false\n");
    }

    strncpy(pCmdstr ,pCmdcp, strlen(pCmdcp));

    bCKS_Result1=iniparser_CheckCS(pModelPathFileName);
    bCKS_Result2=iniparser_CheckCS(pBkModelFileFullName);
    if((bCKS_Result1==0) || (bCKS_Result2==0))
    {
        if(bCKS_Result2 > bCKS_Result1)
        {
            SYSTEM_INFO_DBG("model.ini checksum is error!!!!!!\n");
            strncat(pCmdstr, pBkModelFileFullName, strlen(pBkModelFileFullName));
            strncat(pCmdstr, " ", 1);
            strncat(pCmdstr,pModelPathFileName, strlen(pModelPathFileName));
            SystemCmd(pCmdstr);
            sync();
        }
        else if(bCKS_Result2 < bCKS_Result1)
        {
            SYSTEM_INFO_DBG("Backup model.ini checksum is error!!!!!\n");
            strncat(pCmdstr, pModelPathFileName, strlen(pModelPathFileName));
            strncat(pCmdstr, " ", 1);
            strncat(pCmdstr,pBkModelFileFullName, strlen(pBkModelFileFullName));
            SystemCmd(pCmdstr);
            sync();
        }
        else
        {
            SYSTEM_INFO_DBG("Both of model.ini file and backup model.ini file are checksum error!!!!!!\n");
            ASSERT(0);
        }
    }
    else
    {
        SYSTEM_INFO_DBG("Both of model.ini file and backup model.ini file are checksum correct\n");
    }

    //DLC
    pDLCFullName = iniparser_getstr(m_pCustomerini, "DLC:m_pDLCName");
    if(CustomerBackupPathFilename (pDLCFullName, pBkpName, iSize , CusBackupDLCMode)==false)
    {
        SYSTEM_INFO_DBG("CustomerBackupPathFilename() == false\n");
    }
    iPathFileNameLen = strlen(pDLCFullName);
    iBkpFileFullNameLen = strlen(pBkpName);
    strncpy(pCmdstr ,pCmdcp, strlen(pCmdcp));

    bCKS_Result1=iniparser_CheckCS(pDLCFullName);
    bCKS_Result2=iniparser_CheckCS(pBkpName);
    if((bCKS_Result1==0) || (bCKS_Result2==0))
    {
        if(bCKS_Result2 > bCKS_Result1)
        {
            SYSTEM_INFO_DBG("DLC.ini checksum is error!!!!!!\n");
            strncat(pCmdstr, pBkpName, iBkpFileFullNameLen);
            strncat(pCmdstr, " ", 1);
            strncat(pCmdstr,pDLCFullName, iPathFileNameLen);
            SystemCmd(pCmdstr);
            sync();

        }
        else if(bCKS_Result2 < bCKS_Result1)
        {
            SYSTEM_INFO_DBG("Backup DLC.ini checksum is error!!!!!\n");
            strncat(pCmdstr, pDLCFullName, iPathFileNameLen);
            strncat(pCmdstr, " ", 1);
            strncat(pCmdstr,pBkpName, iBkpFileFullNameLen);
            SystemCmd(pCmdstr);
            sync();
        }
        else
        {
            SYSTEM_INFO_DBG("Both of DLC.ini file and backup DLC.ini file are checksum error!!!!!!\n");
            ASSERT(0);
        }
    }
    else
    {
        SYSTEM_INFO_DBG("Both of DLC.ini file and backup DLC.ini file are checksum correct\n");
    }

    pClrCorrectFullName = iniparser_getstr(m_pCustomerini, "ColorMatrix:MatrixName");
    printf("pClrCorrectFullName = %s\n", pClrCorrectFullName);
    memset(pBkpName, 0, sizeof(pBkpName));

    //Color Correction
    if(CustomerBackupPathFilename (pClrCorrectFullName, pBkpName, iSize , CusBackupColorMatrixMode)==false)
    {
        SYSTEM_INFO_DBG("CustomerBackupPathFilename() == false\n");
    }
    iPathFileNameLen = strlen(pClrCorrectFullName);
    iBkpFileFullNameLen = strlen(pBkpName);
    strncpy(pCmdstr ,pCmdcp, strlen(pCmdcp));

    bCKS_Result1=iniparser_CheckCS(pClrCorrectFullName);
    bCKS_Result2=iniparser_CheckCS(pBkpName);
    if((bCKS_Result1==0) || (bCKS_Result2==0))
    {
        if(bCKS_Result2 > bCKS_Result1)
        {
            SYSTEM_INFO_DBG("%s checksum is error!!!!!!\n", pClrCorrectFullName);
            strncat(pCmdstr, pBkpName, iBkpFileFullNameLen);
            strncat(pCmdstr, " ", 1);
            strncat(pCmdstr, pClrCorrectFullName, iPathFileNameLen);
            SystemCmd(pCmdstr);
            sync();

        }
        else if(bCKS_Result2 < bCKS_Result1)
        {
            SYSTEM_INFO_DBG("Backup %s checksum is error!!!!!\n", pBkpName);
            strncat(pCmdstr, pClrCorrectFullName, iPathFileNameLen);
            strncat(pCmdstr, " ", 1);
            strncat(pCmdstr, pBkpName, iBkpFileFullNameLen);
            SystemCmd(pCmdstr);
            sync();
        }
        else
        {
            SYSTEM_INFO_DBG("Both of %s file and backup %s file are checksum error!!!!!!\n", pClrCorrectFullName, pBkpName);
            ASSERT(0);
        }
    }
    else
    {
        SYSTEM_INFO_DBG("Both of %s file and backup %s file are checksum correct\n", pClrCorrectFullName, pBkpName);
    }


   //panel.ini
    pPanelPathFileName = iniparser_getstr(m_pCustomerini, "panel:m_pPanelName");
    if(CustomerBackupPathFilename (pPanelPathFileName, pBkpPanelFileFullName, iSize , CusBackupPanelMode)==false)
    {
        SYSTEM_INFO_DBG("CustomerBackupPathFilename() == false\n");
    }

    iPanelPathFileNameLen = strlen(pPanelPathFileName);
    iBkpPanelFileFullNameLen = strlen(pBkpPanelFileFullName);

    strncpy(pCmdstr ,pCmdcp, strlen(pCmdcp));

    bCKS_Result1=iniparser_CheckCS(pPanelPathFileName);
    bCKS_Result2=iniparser_CheckCS(pBkpPanelFileFullName);
    if((bCKS_Result1==0) || (bCKS_Result2==0))
    {
        if(bCKS_Result2 > bCKS_Result1)
        {
            SYSTEM_INFO_DBG("panelxxx.ini checksum is error!!!!!!\n");
            strncat(pCmdstr, pBkpPanelFileFullName, iBkpPanelFileFullNameLen);
            strncat(pCmdstr, " ", 1);
            strncat(pCmdstr,pPanelPathFileName, iPanelPathFileNameLen);
            SystemCmd(pCmdstr);
            sync();

        }
        else if(bCKS_Result2 < bCKS_Result1)
        {
            SYSTEM_INFO_DBG("Backup panelxxx.ini checksum is error!!!!!\n");
            strncat(pCmdstr,pPanelPathFileName, iPanelPathFileNameLen);
            strncat(pCmdstr, " ", 1);
            strncat(pCmdstr, pBkpPanelFileFullName, iBkpPanelFileFullNameLen);
            SystemCmd(pCmdstr);
            sync();
        }
        else
        {
            SYSTEM_INFO_DBG("Both of panelxxx.ini file and backup panelxxx.ini file are checksum error!!!!!!\n");
            ASSERT(0);
        }
    }
    else
    {
        SYSTEM_INFO_DBG("Both of panelxxx.ini file and backup panelxxx.ini file are checksum correct\n");
    }

    // pcmodetimingtable.ini
#if ENABLE_RGB_SUPPORT_85HZ
    pPcmodePathFileName = iniparser_getstr(m_pCustomerini, "PcModeSupport85HZ:m_pPcModeTable");
#else
    pPcmodePathFileName = iniparser_getstr(m_pCustomerini, "PcModeTable:m_pPcModeTable");
#endif
    ASSERT(pPcmodePathFileName);

    if(CustomerBackupPathFilename (pPcmodePathFileName, pBkpPcmodeFileFullName, iSize , CusBackupPcMode)==false)
    {
        SYSTEM_INFO_DBG("CustomerBackupPathFilename() == false\n");
    }

    iPcmodePathFileNameLen = strlen(pPcmodePathFileName);
    iBkpPcmodeFileFullNameLen = strlen(pBkpPcmodeFileFullName);

    strncpy(pCmdstr ,pCmdcp, strlen(pCmdcp));

    bCKS_Result1=iniparser_CheckCS(pPcmodePathFileName);
    bCKS_Result2=iniparser_CheckCS(pBkpPcmodeFileFullName);
    if((bCKS_Result1==0) || (bCKS_Result2==0))
    {
        if(bCKS_Result2 > bCKS_Result1)
        {
            SYSTEM_INFO_DBG("pcmode.ini checksum is error!!!!!!\n");
            strncat(pCmdstr, pBkpPcmodeFileFullName, iBkpPcmodeFileFullNameLen);
            strncat(pCmdstr, " ", 1);
            strncat(pCmdstr,pPcmodePathFileName, iPcmodePathFileNameLen);
            SystemCmd(pCmdstr);
            sync();
        }
        else if(bCKS_Result2 < bCKS_Result1)
        {
            SYSTEM_INFO_DBG("Backup pcmodexxx.ini checksum is error!!!!!\n");
            strncat(pCmdstr,pPcmodePathFileName, iPcmodePathFileNameLen);
            strncat(pCmdstr, " ", 1);
            strncat(pCmdstr, pBkpPcmodeFileFullName, iBkpPcmodeFileFullNameLen);
            SystemCmd(pCmdstr);
            sync();
        }
        else
        {
            SYSTEM_INFO_DBG("Both of pcmodexxx.ini file and backup pcmodexxx.ini file are checksum error!!!!!!\n");
            ASSERT(0);
        }
    }
    else
    {
        SYSTEM_INFO_DBG("Both of pcmodexxx.ini file and backup pcmodexxx.ini file are checksum correct\n");
    }
    return TRUE;

}


MAPI_BOOL SystemInfo::UpdateCSandBackupIni(const char * pPathFileName, MAPI_U8 u8Mode )
{
    char pBkpPanelFileFullName[255]={0};
    const int iSize=255;
    int nCmdLen = 0;
    char pCmdstr[iSize]={0};
    const char pCmdcp[] = "cp -vf \0";

    iniparser_UpdateCS(pPathFileName);
    if(CustomerBackupPathFilename (pPathFileName,pBkpPanelFileFullName, iSize,u8Mode)==false)
    {
        SYSTEM_INFO_DBG("CustomerBackupPathFilename() == false\n");
    }

    nCmdLen+=strlen(pCmdcp);
    nCmdLen+=strlen(pBkpPanelFileFullName);
    nCmdLen+=strlen(" ");
    nCmdLen+=strlen(pPathFileName);
    if(nCmdLen>=iSize)
    {
        printf("pCmdstr[] will overflow!!!\n");
        return FALSE;
    }
    strncat(pCmdstr, pCmdcp, strlen(pCmdcp));
    strncat(pCmdstr, pBkpPanelFileFullName, strlen(pBkpPanelFileFullName));
    strncat(pCmdstr, " ", strlen(" "));
    strncat(pCmdstr, pPathFileName, strlen(pPathFileName));

    system(pCmdstr);
    return true;
}

//-----------------------------------------------------------------------------
// Function: PreLoadSystemIni
// Describion: Pre-load the INI File (Customer.ini) to SysIniBlock
//-----------------------------------------------------------------------------
MAPI_BOOL SystemInfo::PreLoadSystemIni(void)
{
    char * pu8ini_strval=NULL;

    /* Load the system ini file */
    ASSERT(m_pSystemini);

    /* Read the model name */
    if(m_pModelName == NULL)
    {
        SYSTEM_INFO_ERR("ERROR: The model name is empty.\n");
        ASSERT(0);
    }

    int iTypeId = iniparser_getint(m_pCustomerini, "eeprom:TypeID",-1);
    if(iTypeId != -1)
        {
           if(Set_Eeprom_Type((MAPI_U8)iTypeId) == TRUE)
            {
                SYSTEM_INFO_DBG("\r\n===Set eeprom type success==\r\n");
            }
           else
            {
                 SYSTEM_INFO_DBG("\r\n===invalid eeprom type and use default==\r\n");
            }
        }
    else
        {
           SYSTEM_INFO_DBG("\r\n===use default eep type setting==\r\n");
        }

    int iModelNameLen =(int)strlen(m_pModelName);
    SysIniBlock.ModelName = new (std::nothrow) char[iModelNameLen + 1];
    ASSERT( SysIniBlock.ModelName);


    memset(SysIniBlock.ModelName, 0, iModelNameLen + 1);
    memcpy(SysIniBlock.ModelName, m_pModelName, iModelNameLen);

    ASSERT(m_pCustomerini);

    if(SysIniBlock.ModelName !=NULL)
    {
        delete [] SysIniBlock.ModelName;
        SysIniBlock.ModelName = NULL;
    }
    char *pPanelName = iniparser_getstr(m_pCustomerini, "panel:m_pPanelName");

    /* Assign the panel name to SysIniBlock */
    if(pPanelName != NULL)
    {
        int iPanelNameLen = (int)strlen(pPanelName);

        SysIniBlock.PanelName = new (std::nothrow) char[iPanelNameLen + 1];
        ASSERT(SysIniBlock.PanelName);

        memset(SysIniBlock.PanelName, 0, iPanelNameLen + 1);
        memcpy(SysIniBlock.PanelName, pPanelName, iPanelNameLen);
    }

    /* Read and assign the gamma bin file flags  number to SysIniBlock */
    SysIniBlock.bGammabinflags = iniparser_getint(m_pCustomerini, "GAMMA_BIN:bGammaBinFlags", -1);

    /* Read and assign the gamma table number to SysIniBlock */
    SysIniBlock.GammaTableNo = iniparser_getint(m_pCustomerini, "panel:gammaTableNo", -1);

    /* Read and assign the tuner SAW type to SysIniBlock */
    SysIniBlock.TunerSAWType = iniparser_getint(m_pCustomerini, "Tuner:TunerSawType", -1);

    /* Read abd assign the audio amplifier number to SysIniBlock */
    SysIniBlock.AudioAmpSelect = iniparser_getint(m_pCustomerini, "AudioAmp:AudioAmpSelect", -1);

#if (ENABLE_BACKEND == 1)
    /* Read abd assign the ursa enable to SysIniBlock */
    SysIniBlock.UrsaEanble= iniparser_getboolean(m_pCustomerini, "Ursa:UrsaEnable", 0);
    /* Read abd assign the Ursa number to SysIniBlock */
    SysIniBlock.UrsaSelect = iniparser_getint(m_pCustomerini, "Ursa:UrsaSelect", -1);
    /* Read abd assign the MEMC Panel enable to SysIniBlock */
    SysIniBlock.MEMCPanelEnable= iniparser_getboolean(m_pCustomerini, "MEMCPanel:MEMCPanelEnable", 0);
    /* Read abd assign the MEMC Panel number to SysIniBlock */
    SysIniBlock.MEMCPanelSelect = iniparser_getint(m_pCustomerini, "MEMCPanel:MEMCPanelSelect", -1);
#endif
    /* Read abd assign the PQ use default value  to SysIniBlock */
    SysIniBlock.PQBinDefault= iniparser_getint(m_pCustomerini, "panel:bPQUseDefaultValue", -1);

    /* Read and assign the bDotByDotAble to SysIniBlock */
    SysIniBlock.bDotByDotAble= iniparser_getboolean(m_pCustomerini, "MISC:bDotByDotAble", 0);

    /* Read and assign the AVSYnc delay value*/
    SysIniBlock.u16AVSyncDelay = iniparser_getint(m_pCustomerini, "MISC:AVSyncDelay", 0);

    /* Read and assign the bMirrorVideo to SysIniBlock */
    SysIniBlock.bMirrorVideo = iniparser_getboolean(m_pCustomerini, "MISC_MIRROR_CFG:MIRROR_VIDEO", 0);

    /* Read and assign the u8MirrorType to SysIniBlock */
    SysIniBlock.u8MirrorType= iniparser_getint(m_pCustomerini, "MISC_MIRROR_CFG:MIRROR_VIDEO_TYPE", 0);

    /* Read and assign the bEnable3DOverScan to SysIniBlock */
    SysIniBlock.bEnable3DOverScan = iniparser_getboolean(m_pCustomerini, "3DOverScan:b3DOverScanEnable", 0);

    /* Read and assign the hbbtvDelayInit to SysIniBlock */
    SysIniBlock.bHbbtvDelayInitFlag = iniparser_getboolean(m_pCustomerini, "hbbtv:hbbtvDelayInitFlag", 1);


#if (LOCAL_DIMMING == 1)
    //for storage hdcp config
    /* Read and assign the bIsNandHdcpEnable to SysIniBlock */
    SysIniBlock.bNandHdcpEnable = iniparser_getboolean(m_pCustomerini, "StorageHDCP:bNandHdcpEnable", 0);

    /* Read and assign the bIsSPIHdcpEnable to SysIniBlock */
    SysIniBlock.bSPIHdcpEnable = iniparser_getboolean(m_pCustomerini, "StorageHDCP:bSPIHdcpEnable", 0);

    /* Read and assign the HdcpSPIBank to SysIniBlock */
    SysIniBlock.u8HdcpSPIBank = iniparser_getint(m_pCustomerini, "StorageHDCP:HdcpSPIBank", -1);

    /* Read and assign the HdcpSPIOffset to SysIniBlock */
    SysIniBlock.u16HdcpSPIOffset = iniparser_getint(m_pCustomerini, "StorageHDCP:HdcpSPIOffset", -1);

    /* Read and assign the bIsEEPROMHdcpenable to SysIniBlock */
    SysIniBlock.bEEPROMHdcpEnable = iniparser_getboolean(m_pCustomerini, "StorageHDCP:bEEPROMHdcpEnable", 0);

    /* Read and assign the HdcpEEPROMAddr to SysIniBlock */
    SysIniBlock.u8HdcpEEPROMAddr = iniparser_getint(m_pCustomerini, "StorageHDCP:HdcpEEPROMAddr", -1);

    //for storage MAC config
    /* Read and assign the bIsSPIMacEnable to SysIniBlock */
    SysIniBlock.bSPIMacEnable = iniparser_getboolean(m_pCustomerini, "StorageMAC:bSPIMacEnable", 0);

    /* Read and assign the MacSPIBank to SysIniBlock */
    SysIniBlock.u8MacSPIBank = iniparser_getint(m_pCustomerini, "StorageMAC:MacSPIBank", -1);

    /* Read and assign the HdcpSPIOffset to SysIniBlock */
    SysIniBlock.u16MacSPIOffset = iniparser_getint(m_pCustomerini, "StorageMAC:MacSPIOffset", -1);


    /* Read and assign the LocalDIMMINGEnable to SysIniBlock */
    SysIniBlock.bLocalDIMMINGEnable = iniparser_getboolean(m_pCustomerini, "LocalDIMMING:bLocalDIMMINGEnable", 0);

    /* Read and assign the LocalDIMMINGPanelSelect to SysIniBlock */
    SysIniBlock.u8LocalDIMMINGPanelSelect = iniparser_getint(m_pCustomerini, "LocalDIMMING:PanelSelect", 0);

    /*set panel swing level */
    MAPI_U16 u16SwingLevel = iniparser_getint(m_pCustomerini, "PANEL_SWING_LEVEL:SWING_LEVEL", 250);
    SetSwingLevel(u16SwingLevel);
#endif

    /* Read and assign the bPanel4K2KModeNeedCmd to SysIniBlock */
    SysIniBlock.bPanel4K2KModeNeedCmd = iniparser_getboolean(m_pCustomerini, "panel:bPanel4K2KModeNeedCmd", TRUE);

    /* Read abd assign the path of customer PQ binary file to SysIniBlock */
    pu8ini_strval = iniparser_getstr(m_pCustomerini, "panel:PQBinPathName");
    ASSERT(pu8ini_strval);
    int len = strlen(pu8ini_strval);
    if(len>=64)
    {
        ASSERT(0);
    }
    memset(SysIniBlock.PQPathName , 0 , len + 1);
    memcpy(SysIniBlock.PQPathName ,(void*)pu8ini_strval, len);

    ASSERT(m_pPanelini);

    char *pSysPanelName = iniparser_getstr(m_pPanelini, "panel:m_pPanelName");
    if(pSysPanelName == NULL)
        SysIniBlock.SysPanelName[0]='\0';
    else
    {
        memset(SysIniBlock.SysPanelName ,0 ,64);
        strncpy(SysIniBlock.SysPanelName, pSysPanelName,strlen(pSysPanelName));
        SysIniBlock.SysPanelName[63]='\0';
    }
    SYSTEM_INFO_DBG("SysPanelName : %s \n", SysIniBlock.SysPanelName);

    memset(SysIniBlock.BoardName ,0 ,32);
    memcpy(SysIniBlock.BoardName, BOARD_NAME, strlen(BOARD_NAME));
    memset(SysIniBlock.SoftWareVer ,0 ,32);
    memcpy (SysIniBlock.SoftWareVer, SOFTWARE_VERSION, strlen(SOFTWARE_VERSION));
    return TRUE;
}


MAPI_BOOL iniparser_getU8array(dictionary * pdic, const char * pkey, const MAPI_U16 u16OutDataLen, MAPI_U8 * pOutDataVal)
{
    char * pIniString;
    MAPI_U16 len=0;
    char pVal[10]={0};
    MAPI_U16 read_index=0;
    MAPI_U16 write_index=0;
    MAPI_U16 check_x=0;

    MAPI_U16 u16OutDataIndex=0;
    MAPI_U8 carry=0;
    MAPI_U32 temp_value=0;
    MAPI_U32 temp_pOutDataVal=0;

    if((pdic==NULL) || (pkey==NULL) || (pOutDataVal==NULL))
    {
        ASSERT(0);
    }
    memset(pOutDataVal, 0, u16OutDataLen);

    pIniString = iniparser_getstr(pdic, pkey);

    if(pIniString==NULL)
    {
        //ASSERT(0);
        return FALSE;
    }
    len = strlen(pIniString);

    //
    //skip space
    //
    while((pIniString[read_index] == ' ') && (read_index<len))
    {
        read_index++;
    }

    //
    //seek the start of array '{'
    //
    if((pIniString[read_index] == '{'))
    {
        read_index++;
    }
    else
    {
        SYSTEM_INFO_ERR("ERROR: Wrong volumn curve table in ini file :: 1 \n");
        ASSERT(0);
    }

    //
    //skip space
    //
    while((pIniString[read_index] == ' ') && (read_index<len))
    {
        read_index++;
    }

    //
    // read string and parser data, then store data
    //
    while((pIniString[read_index] != '}') && (read_index<len))
    {
        //check 0~9
        if(!((pIniString[read_index]>='0') && (pIniString[read_index]<='9')))
        {
            SYSTEM_INFO_ERR("ERROR: Wrong volumn curve table in ini file ::  2 - %c  \n",pIniString[read_index]);
            ASSERT(0);
        }

        //check 0~9, but not ',' or '}'
        write_index=0;
        while(((pIniString[read_index]>='0') && (pIniString[read_index]<='9')) || ((pIniString[read_index] != ',') && (pIniString[read_index] != '}')))
        {

            pVal[write_index]= pIniString[read_index];
            write_index++;
            read_index++;

            check_x=0;
            while((pIniString[read_index] == 'x') || ((pIniString[read_index]>='0') && (pIniString[read_index]<='9')) || ((pIniString[read_index]>='a') && (pIniString[read_index]<='f'))  || (pIniString[read_index]== 'X') || ((pIniString[read_index]>='A') && (pIniString[read_index]<='F')))
            {
                pVal[write_index]= pIniString[read_index];
                write_index++;
                read_index++;
                check_x++;
                if((pIniString[read_index] == 'x') && (check_x !=1))
                {
                    SYSTEM_INFO_ERR("ERROR: Wrong volumn curve table in ini file ::  3 \n");
                    ASSERT(0);
                }
            }
             //skip space
            while((pIniString[read_index] == ' '))
            {
                read_index++;
            }
        }

        //check ','
        if(pIniString[read_index] == ',')
        {
            pVal[write_index]='\0';
            read_index++;
        }
        else
        {
            if(pIniString[read_index] != '}')
            {
                SYSTEM_INFO_ERR("ERROR: Wrong volumn curve table in ini file :: 3 - %c  \n", pIniString[read_index]);
                ASSERT(0);
            }
        }

        //
        //  transfer string into MAPI_U8 and store it
        //
        write_index=0;
        if(pVal[write_index+1]=='x')
        {
            carry = 16;
            write_index=write_index+2;
        }
        else
        {
            carry = 10;
        }
        temp_pOutDataVal=0;
        while(pVal[write_index]!='\0')
        {
            if(((pVal[write_index]>='0') && (pVal[write_index]<='9')))
                temp_value = (pVal[write_index] - '0' );
            else if(((pVal[write_index]>='A') && (pVal[write_index]<='F')))
                temp_value = (pVal[write_index]-'A' +10 );
            else if(((pVal[write_index]>='a') && (pVal[write_index]<='f')))
                temp_value = (pVal[write_index] -'a' +10 );

            temp_pOutDataVal=temp_value +temp_pOutDataVal*carry;
            write_index++;
        }
        //check overflow
        if(temp_pOutDataVal > 0xFF)
        {
            ASSERT(0);
        }
        pOutDataVal[u16OutDataIndex]=temp_pOutDataVal;
        u16OutDataIndex++;

        //
        //skip space
        //
        while((pIniString[read_index] == ' ') && (read_index<len))
        {
            read_index++;
        }
    }

    if(read_index>=len)
    {
        SYSTEM_INFO_ERR("ERROR: Wrong volumn curve table in ini file :: 5\n");
        ASSERT(0);
    }

    return TRUE;
}
unsigned char SystemInfo::Get_bSelectModelViaProjectID()
{
    m_pSystemini = iniparser_load(SYS_INI_PATH_FILENAME);
    ASSERT(m_pSystemini);
    unsigned char bSelectModelViaProjectID = FALSE;
    bSelectModelViaProjectID = iniparser_getboolean(m_pSystemini, "select_model_via_project_id:bEnabled",0);
    if(bSelectModelViaProjectID != TRUE)
    {
        //customer
        m_pModelName = iniparser_getstr(m_pSystemini, "model:gModelName");
        ASSERT(m_pModelName);
        m_pCustomerini = iniparser_load(m_pModelName);
        ASSERT(m_pCustomerini);
    }
    return bSelectModelViaProjectID;

}
char* SystemInfo::getLocalStoragePathOfMWBLauncherIni()
{
    m_pMWBLauncherini = iniparser_load(MWBLAUNCHER_INI_PATH_FILENAME);
    ASSERT(m_pMWBLauncherini);

    char* retPath = NULL;
    retPath = iniparser_getstr(m_pMWBLauncherini, "SETTINGS:LOCAL_STORAGE_PATH");
    if (NULL == retPath) {
        printf("%s():%d iniparser_load() fail\n",__FUNCTION__, __LINE__);
        ASSERT(0);
    }
    return retPath;
}
unsigned short SystemInfo::Get_MaxProjectID()
{
    return iniparser_getint(m_pSystemini, "select_model_via_project_id:MAX_MODEL_INDEX", -1);
}
MAPI_U32 SystemInfo::Get_u32ProjectIdSpiAddr()
{
    return iniparser_getunsignedint(m_pSystemini, "select_model_via_project_id:project_id_spi_addr", 0);
}
MAPI_U32 SystemInfo::Get_u32ProjectIdBackupSpiAddr()
{
    return iniparser_getunsignedint(m_pSystemini, "select_model_via_project_id:project_id_backup_spi_addr", 0);
}
MAPI_U16 SystemInfo::GetProjectId()
{
    MAPI_U16 MaxProjectID  = 0xffff;
    MAPI_U8 tmp[2]={0xFF,0xFF};
    MAPI_U8 u8Retry = 0;
    MAPI_U32 u32ProjectIdSpiAddr = 0;
    MAPI_U32 u32ProjectIdBackupSpiAddr = 0;

    if(m_u16SpiProjectID != 0xFFFF)
    {
        return m_u16SpiProjectID;
    }

    MaxProjectID = Get_MaxProjectID();
    u32ProjectIdSpiAddr = Get_u32ProjectIdSpiAddr();
    u32ProjectIdBackupSpiAddr = Get_u32ProjectIdBackupSpiAddr();

    char str[CMD_LINE_SIZE];
    if (CmdLineParser(str, ENV_CFG_PREFIX) == TRUE)
    {
        if(0 == strncmp(str, ENV_IN_SERIAL, strlen(ENV_IN_SERIAL)))
        {
            printf("ENV in SPI...\n");

            MDrv_MMIO_Init();
            MDrv_SERFLASH_Init();

            while((MDrv_SERFLASH_Read(u32ProjectIdSpiAddr, 2, &tmp[0]) == FALSE) && (u8Retry != 3))
            {
                u8Retry++;
            }
            if(u8Retry == 3)
            {
                ASSERT(0);
            }
            u8Retry = 0;
            m_u16SpiProjectID = ((MAPI_U16)tmp[0]<<8)+tmp[1];

            if((m_u16SpiProjectID > MaxProjectID) || (m_u16SpiProjectID == 0x0000)) //Read Data from backup
            {
                while((MDrv_SERFLASH_Read(u32ProjectIdBackupSpiAddr, 2, &tmp[0]) == FALSE) && (u8Retry != 3))
                {
                    u8Retry++;
                }
                if(u8Retry == 3)
                {
                    ASSERT(0);
                }
                u8Retry = 0;

                m_u16SpiProjectID = ((MAPI_U16)tmp[0]<<8)+tmp[1];
                if(m_u16SpiProjectID > MaxProjectID || m_u16SpiProjectID == 0x0000) //set to default project ID
                {
                    m_u16SpiProjectID = 1;
                }
            }
        }
        else if(0 == strncmp(str, ENV_IN_NAND, strlen(ENV_IN_NAND)))
        {
            printf("ENV in NAND...\n");
            m_u16SpiProjectID = 1;
        }
        else if(0 == strncmp(str, ENV_IN_UBI, strlen(ENV_IN_UBI)))
        {
            printf("ENV in UBI...\n");
            m_u16SpiProjectID = 1;
        }
        else if(0 == strncmp(str, ENV_IN_EMMC, strlen(ENV_IN_EMMC)))
        {
            printf("ENV in EMMC...\n");
            int fd = open(EMMC_ENV_CONFIG_PATH, O_RDONLY);
            if(fd == -1)
            {
                perror("open");
                printf("open fail!!!!\n");
                ASSERT(0);
                return MAPI_FALSE;
            }
            mmc_read(fd, &tmp[0], 2, u32ProjectIdSpiAddr);
            m_u16SpiProjectID = ((MAPI_U16)tmp[0] << 8) + tmp[1];
            if((m_u16SpiProjectID > MaxProjectID) || (m_u16SpiProjectID == 0x0000))
            {
                mmc_read(fd, &tmp[0], 2, u32ProjectIdBackupSpiAddr);
                m_u16SpiProjectID = ((MAPI_U16)tmp[0] << 8) + tmp[1];
                if(m_u16SpiProjectID > MaxProjectID || m_u16SpiProjectID == 0x0000) //set to default project ID
                {
                    m_u16SpiProjectID = 1;
                }
            }
            close(fd);
        }
    }
    return m_u16SpiProjectID;
}
unsigned char SystemInfo::Set_SpiProjectID(unsigned short u16SpiProjectID)
{
    char TempBuffer[MAX_BUFFER+1]={0};
    SYSTEM_INFO_IFO("SystemInfo::LoadIniFile, m_u16SpiProjectID = 0x%x \n", u16SpiProjectID);

    snprintf(TempBuffer,MAX_BUFFER,"select_model_via_project_id:Model_%d",u16SpiProjectID);
    SYSTEM_INFO_IFO("SystemInfo::LoadIniFile, TempBuffer = %s \n", TempBuffer);
    m_pModelName = iniparser_getstr(m_pSystemini, TempBuffer);
    if (NULL == m_pModelName)
    {
        m_pModelName = iniparser_getstr(m_pSystemini, "model:gModelName");
    }
    SYSTEM_INFO_IFO("SystemInfo::LoadIniFile, m_pModelName = %s \n", m_pModelName);
    ASSERT(m_pModelName);
    m_pCustomerini = iniparser_load(m_pModelName);
    ASSERT(m_pCustomerini);
    return TRUE;
}

MAPI_BOOL SystemInfo::SetSystemInfo(void)
{
    LoadIniFile();

#if (PIP_ENABLE == 1)
#if (TRAVELING_ENABLE == 1)
    SetTravelingInfoSet();
#endif
#endif

#if (SQL_DB_ENABLE == 1)
    SetSQLDBdata();
#endif

    // Read customer.ini, DLC.ini and panel.ini and check each of them's customer is correct or not.
    // If the checksum is not correct, it will be recoveried.
#ifndef BOARD_ALL_INI_DISABLE
#if (SYSINFO_DISABLE_INIT_CHECKSUM == 1)
    CheckIniCS();
#endif
#endif

    //update ini file data to the latest version.
#ifndef BOARD_ALL_INI_DISABLE
    CheckVersionAndSetDefaultValue(SystemIniType);
#endif

    ASSERT(m_pCustomerini);

    LoadHdrLevelAttributes(m_pCustomerini, m_stHdrLevelAttributes);

    char *pHDCPFileName = iniparser_getstr(m_pCustomerini, "StorageHDCP:pHDCPFileName");
    /* Assign the panel name to SysIniBlock */
    if(pHDCPFileName != NULL)
    {
        int iHDCPFileNameLen = (int)strlen(pHDCPFileName);

        SysIniBlock.pHDCPKeyFileName = new (std::nothrow) char[iHDCPFileNameLen + 1];
        ASSERT(SysIniBlock.pHDCPKeyFileName);

        memset(SysIniBlock.pHDCPKeyFileName, 0, iHDCPFileNameLen + 1);
        memcpy(SysIniBlock.pHDCPKeyFileName, pHDCPFileName, iHDCPFileNameLen);
    }

//ini file lenght is too large.   CheckVersionAndSetDefaultValue(PanelIniType);
//to do....    CheckVersionAndSetDefaultValue(DCLIniType);
#ifndef BOARD_ALL_INI_DISABLE
    /* Pre-load the INI File (Customer.ini) to SysIniBlock */
    PreLoadSystemIni();
#endif

#ifndef BOARD_ALL_INI_DISABLE
    SYSTEM_INFO_DBG("  = SetPQAutoNRParam = \n");
    SystemInfo::SetPQAutoNRParam();
#endif


#if 0
    printf("--------------------System.ini START-------------------\n");
    printf("Panel:[%s]\n", SysIniBlock.PanelName);
    printf("Panel:[%s]\n", SysIniBlock.PanelName);
    printf("GammaTableNo:[%d]\n", SysIniBlock.GammaTableNo);
    printf("--------------------System.ini  END-------------------\n");
#endif

    SYSTEM_INFO_DBG(" ======== SYSTEM_INFO_ENABLE Start ==========\n");

#if ( INTEL_WIDI_ENABLE == 1 )
    //widi.ini
    SYSTEM_INFO_DBG("  = SetWidiInfo= \n");
    SystemInfo::SetWidiInfo();
#endif
    //panel.ini
    SYSTEM_INFO_DBG("  = SetPanelInfo= \n");
    SystemInfo::SetPanelInfo();

#ifndef BOARD_ALL_INI_DISABLE
    SYSTEM_INFO_DBG("  = SetGammaTable = \n");
    if(SysIniBlock.bGammabinflags)
        SystemInfo::LoadGammaBinInfo();
    else
        SystemInfo:: SetGammaTable(SysIniBlock.GammaTableNo);
#endif
    //DCL.ini
#ifndef BOARD_ALL_INI_DISABLE
    SYSTEM_INFO_DBG("  = SetDLCInfo = \n");
    SystemInfo::SetDLCInfo();
#endif

#ifndef BOARD_ALL_INI_DISABLE
#if (STB_ENABLE == 0)
    SYSTEM_INFO_DBG("  = SetColorMatrix = \n");
    SystemInfo::SetColorMatrix();
#endif
#endif
    //board.h
    SYSTEM_INFO_DBG("  = SetTVParam = \n");
    SystemInfo::SetTVParam();

    char *pTunerModeName = NULL;
    pTunerModeName = iniparser_getstr(m_pCustomerini, "Pip:TunerTableName");
    m_pTunerModeIni = iniparser_load(pTunerModeName);
    ReadTunerModeTable();

    SYSTEM_INFO_DBG("  = SetI2CCfg = \n");
    SystemInfo::SetI2CCfg();
    SYSTEM_INFO_DBG("  = SetGPIOCfg = \n");
    SystemInfo::SetGPIOCfg();
    SYSTEM_INFO_DBG("  = SetInputMux = \n");
    SystemInfo::SetInputMux();
    SYSTEM_INFO_DBG("  = SetATVExtDemodInfo = \n");
    SystemInfo::SetATVExtDemodInfo();
    SYSTEM_INFO_DBG("  = SetScartInfo = \n");
    SystemInfo::SetScartInfo();
    SYSTEM_INFO_DBG("  = SetVideoInfo = \n");
    SystemInfo::SetVideoInfo();
    SYSTEM_INFO_DBG("  = SetWDTCfg = \n");
    SystemInfo::SetWDTCfg();
    SYSTEM_INFO_DBG("  = SetAudioInputMuxCfg = \n");
    SystemInfo::SetAudioInputMuxCfg();
    SYSTEM_INFO_DBG("  = SetMSPIPadInfo =  \n");
    SystemInfo::SetMSPIPadInfo();
    //SetHDMIEDIDInfoSet & SetVGAEDIDInfo

    //The flow had move to MSrv_Control_common::InitHDMIEDIDInfoSet,after DB initialized
/*
#if (STB_ENABLE == 1)
    SYSTEM_INFO_DBG("  = SetHDMIEDIDInfoSet = \n");
    SystemInfo::SetHDMIEDIDInfoSet();
#else
#ifndef BOARD_ALL_INI_DISABLE
    SYSTEM_INFO_DBG("  = SetHDMIEDIDInfoSet = \n");
    SystemInfo::SetHDMIEDIDInfoSet();
#endif
#endif
*/
#ifndef BOARD_ALL_INI_DISABLE
    SYSTEM_INFO_DBG("  = SetVGAEDIDInfo = \n");
    SystemInfo::SetVGAEDIDInfo();
#endif
    SYSTEM_INFO_DBG("  = SetHDMITxAnalogInfo = \n");
    SystemInfo::SetHDMITxAnalogInfo();

    SYSTEM_INFO_DBG("  = SetDemuxInfo = \n");
    SystemInfo::SetDemuxInfo();

    SYSTEM_INFO_DBG("  = SetSAWType = \n");
    SystemInfo::SetSAWType();

    SYSTEM_INFO_DBG("  = SetSARChannel = \n");
    SystemInfo::SetSARChannel();

    SYSTEM_INFO_DBG("  = SetDemodConfig = \n");
    SystemInfo::SetDemodConfig();

    SYSTEM_INFO_DBG("  = SetPanelModPvddInfo = \n");
    SystemInfo::SetPanelModPvddInfo();

#if (ENABLE_BACKEND == 1)
    SYSTEM_INFO_DBG("  = SetUrsaEnable = \n");
    SystemInfo::SetUrsaEnable();
    SYSTEM_INFO_DBG("  = SetUrsaSelect = \n");
    SystemInfo::SetUrsaSelect();
    SYSTEM_INFO_DBG("  = SetMEMCPanelEnable = \n");
    SystemInfo::SetMEMCPanelEnable();
    SYSTEM_INFO_DBG("  = SetMEMCPanelSelect = \n");
    SystemInfo::SetMEMCPanelSelect();
#endif

    SYSTEM_INFO_DBG("  = SetTunerSelect = \n");
    SystemInfo::SetTunerSelect();

    SYSTEM_INFO_DBG("  = SetChinaDescramblerBoxDelayOffset = \n");
    SystemInfo::SetChinaDescramblerBoxDelayOffset();

    SYSTEM_INFO_DBG("  = SetTunerPWMInfo = \n");
    SystemInfo::SetTunerPWMInfo();
#if (DYNAMIC_I2C_ENABLE == 1)
    if(!SystemInfo::LoadI2CBinInfo())
    {
        SYSTEM_INFO_DBG("Load Tuner.bin failed!\n");
    }
#endif
    SYSTEM_INFO_DBG("  = SetAudioAmpSelect = \n");
    SystemInfo::SetAudioAmpSelect();

#if (STB_ENABLE == 0)
    SYSTEM_INFO_DBG("  = SetVolumeCurve = \n");
    SetVolumeCurve();

    SYSTEM_INFO_DBG("  = SetPictureModeUseFacCurveFlag = \n");
    SetPictureModeUseFacCurveFlag();

    SYSTEM_INFO_DBG("  = SetPictureModeCurve = \n");
    SetPictureModeCurve();

    SYSTEM_INFO_DBG("  = SetPcModeTimingTable = \n");
    SetPcModeTimingTable();
#endif

    // Customer AVSyncDelay
    SYSTEM_INFO_DBG("  = SetAVSyncDelay = \n");
    SystemInfo::SetAVSyncDelay();

    SYSTEM_INFO_DBG("  = CheckBootParameter = \n");

    SYSTEM_INFO_DBG("  = SetMirrorFlag = \n");
    SystemInfo::SetMirrorFlag();

    SYSTEM_INFO_DBG("  = Set3DOverScan = \n");
    SystemInfo::Set3DOverScanEnable();

    SYSTEM_INFO_DBG("  = SetHbbtvDelayInit = \n");
    SystemInfo::SetHbbtvDelayInitFlag();

    SYSTEM_INFO_DBG("  = SetVolumeCompensation = \n");
    SystemInfo::SetVolumeCompensation();

    //for storage hdcp config
    SYSTEM_INFO_DBG("  = SetStorageHDCPCfg = \n");
    SystemInfo::SetStorageHDCPCfg();

#if (MSTAR_TVOS == 1)
    SYSTEM_INFO_DBG("  = SetHDMI_HdcpEnable = \n");
    SystemInfo::SetHDMI_HdcpEnable();

    //for storage mac config
    SYSTEM_INFO_DBG("  = SetStorageMACCfg = \n");
    SystemInfo::SetStorageMACCfg();
#endif

#if (LOCAL_DIMMING == 1)
    //for local dimming config
    SYSTEM_INFO_DBG("  = SetLocalDIMMINGCfg = \n");
    SystemInfo::SetLocalDIMMINGCfg();
#endif

    SYSTEM_INFO_DBG(" =Mode Detect=\n");
    SystemInfo::SetModeDectectCount();
#if(MULTI_DEMOD==1)
    SYSTEM_INFO_DBG(" =Multi Demod=\n");
    SystemInfo::SetMultiDemodCfg();
#endif
    SYSTEM_INFO_DBG(" = SetAMPInfo\n");
    SystemInfo::SetAMPInfo();

    SYSTEM_INFO_DBG(" = SetCIModelCount\n");
#ifdef BOARD_CI_SLOT_NUM
    SetCISlotCount(BOARD_CI_SLOT_NUM);
#else
    SetCISlotCount(1);
#endif
    MAPI_BOOL bEnable_OSDC = MAPI_FALSE;
    SystemInfo::GetModuleParameter_bool("M_BACKEND:F_BACKEND_ENABLE_OSDC", &bEnable_OSDC);
    if( bEnable_OSDC == MAPI_TRUE )
    {
        SYSTEM_INFO_DBG(" = LoadOSDCInfo\n");
        LoadOSDCInfo();
    }

#if (NETFLIX_ENABLE==1)
    SYSTEM_INFO_DBG(" = SetNetflixInfo\n");
    SystemInfo::SetNetflixInfo();
#endif

    SYSTEM_INFO_DBG("  = SetUseAudioDelayOffset = \n");
    SystemInfo::SetUseAudioDelayOffset();
    SystemInfo::SetUseSPDIFDelayOffset();

    SYSTEM_INFO_DBG("  = SetVideoFileName= \n");
    SystemInfo::SetVideoFileName();
	//EosTek Patch Begin
	SystemInfo::SetVideoFileNameAlternative();
	//EosTek Patch End

    SYSTEM_INFO_DBG("  = SetHDMIEDIDVersionList = \n");
    SystemInfo::SetHDMIEDIDVersionList();

    SYSTEM_INFO_DBG(" ======== SYSTEM_INFO_ENABLE End ==========\n");
#ifndef BOARD_ALL_INI_DISABLE
    delete [] SysIniBlock.PanelName;
    SysIniBlock.PanelName = NULL;
#endif

    if(SysIniBlock.pHDCPKeyFileName != NULL)
    {
        delete [] SysIniBlock.pHDCPKeyFileName;
        SysIniBlock.pHDCPKeyFileName = NULL;
    }

    ParseVb1ChannelOrder(m_pCustomerini, "VB1_Connector:8V_Order", m_u16Vb18VChannelOrder);
    ParseVb1ChannelOrder(m_pCustomerini, "VB1_Connector:4V_Order", m_u16Vb14VChannelOrder);
    ParseVb1ChannelOrder(m_pCustomerini, "VB1_Connector:2V_Order", m_u16Vb12VChannelOrder);
    ParseVb1ChannelOrder(m_pCustomerini, "VB1_Connector:4O_Order", m_u16Vb14OChannelOrder);
    ParseVb1ChannelOrder(m_pCustomerini, "VB1_Connector:2O_Order", m_u16Vb12OChannelOrder);

    FreeIniFile();
    return TRUE;
}

#if ( INTEL_WIDI_ENABLE == 1 )
MAPI_BOOL SystemInfo::SetWidiInfo(void)
{
    memset(&m_WidiInfo, 0, sizeof(WidiInfo_t));
    return TRUE;
}
#endif

MAPI_BOOL SystemInfo::SetPanelInfo(void)
{
    ASSERT(m_pBoardini);

#if (STB_ENABLE == 1)
    // Set panel configuration in STB.
    //m_u16LVDS_Output_type = BOARD_LVDS_CONNECT_TYPE;

    MAPI_PanelType stPanelInfo;
    stPanelInfo.pPanelName = new char[PANEL_NAME_LEN];
    memset((void*)stPanelInfo.pPanelName, 0, PANEL_NAME_LEN);
    LoadPanelInfo(m_pPanelini, &stPanelInfo);

    //mapi_system set pnl.
    MAPI_U16 u16LVDS_Output_typ = BOARD_LVDS_CONNECT_TYPE;
    SetDefaultPanelInfoCfg(&stPanelInfo, u16LVDS_Output_typ, NULL);

    if (stPanelInfo.pPanelName != NULL)
    {
        delete[] stPanelInfo.pPanelName;
    }
#else
    // Set panel configuration in TV.
    ASSERT(m_pPanelini);

    //Load default panel info
    MAPI_PanelType stPanelInfo;
    memset(&stPanelInfo, 0, sizeof(MAPI_PanelType));
    stPanelInfo.pPanelName = new char[PANEL_NAME_LEN];
    memset((void*)stPanelInfo.pPanelName, 0, PANEL_NAME_LEN);
    LoadPanelInfo(m_pPanelini, &stPanelInfo);

    //panel backlight PWM information
    PanelBacklightPWMInfo ptPanelBacklightPWMInfo;
    ptPanelBacklightPWMInfo.u32PeriodPWM              = iniparser_getint(m_pPanelini, "panel:u32PeriodPWM", 0);
    ptPanelBacklightPWMInfo.u32DutyPWM                = iniparser_getint(m_pPanelini, "panel:u32DutyPWM", 0);
    ptPanelBacklightPWMInfo.u16DivPWM                 = iniparser_getint(m_pPanelini, "panel:u16DivPWM", 0);
    ptPanelBacklightPWMInfo.u16MaxPWMvalue            = ((MAPI_U16)(((MAPI_U32)iniparser_getint(m_pPanelini, "panel:u16MaxPWMvalue", 0))&0x0000ffff));
    ptPanelBacklightPWMInfo.u16MaxPWMvalueMSB     = ((MAPI_U16)(((MAPI_U32)iniparser_getint(m_pPanelini, "panel:u16MaxPWMvalue", 0))>>16));
    ptPanelBacklightPWMInfo.u16MinPWMvalue             = ((MAPI_U16)(((MAPI_U32)iniparser_getint(m_pPanelini, "panel:u16MinPWMvalue", 0))&0x0000ffff));
    ptPanelBacklightPWMInfo.u16MinPWMvalueMSB      = ((MAPI_U16)(((MAPI_U32)iniparser_getint(m_pPanelini, "panel:u16MinPWMvalue", 0))>>16));
    ptPanelBacklightPWMInfo.u8PWMPort = iniparser_getint(m_pBoardini, "PanelRelativeSetting:m_u8BOARD_PWM_PORT", 0);
    ptPanelBacklightPWMInfo.bPolPWM                      = iniparser_getboolean(m_pPanelini, "panel:bPolPWM", TRUE);
    ptPanelBacklightPWMInfo.bBakclightFreq2Vfreq         = iniparser_getboolean(m_pPanelini, "panel:bBakclightFreq2Vfreq", FALSE);

#if (STEREO_3D_ENABLE == 1)
    memcpy(&m_stGlobalPanelInfo, &stPanelInfo, sizeof(MAPI_PanelType));
    Set3DPanelLRInverseCfg(stPanelInfo.bPanelReverseFlag);
    SetFreerunCfg(FALSE, stPanelInfo.bPanel2DFreerunFlag);
    SetFreerunCfg(TRUE, stPanelInfo.bPanel3DFreerunFlag);
    SetSGPanelCfg(stPanelInfo.bSGPanelFlag);
    SetXCOutput120hzSGPanelCfg(stPanelInfo.bXCOutput120hzSGPanelFlag);
    SYSTEM_INFO_DBG("%s <#> Got backup panel info\n\n", __FUNCTION__);
    static char *tmp = NULL;
    if (tmp)
    {
        delete[] tmp;
    }
    int len = strlen(stPanelInfo.pPanelName);
    tmp = new (std::nothrow) char[len+1];
    ASSERT(tmp);
    memcpy(tmp, stPanelInfo.pPanelName, len);
    tmp[len] = '\0';
    m_stGlobalPanelInfo.pPanelName = tmp;
    SYSTEM_INFO_DBG("%s <#> %s %s\n", __FUNCTION__, stPanelInfo.pPanelName, m_stGlobalPanelInfo.pPanelName);
#endif

    //mapi_system set pnl.
    MAPI_U16 u16LVDS_Output_typ = iniparser_getint(m_pBoardini, "PanelRelativeSetting:m_u16BOARD_LVDS_CONNECT_TYPE", 0);
    SetDefaultPanelInfoCfg(&stPanelInfo, u16LVDS_Output_typ, &ptPanelBacklightPWMInfo);

    //Customer PQ
    char *pMainPQPath;
    char *pSubPQPath;

    pMainPQPath = iniparser_getstring(m_pPanelini, "CUSTOMER_PQ:Main_pq_path", NULL);
    pSubPQPath = iniparser_getstring(m_pPanelini, "CUSTOMER_PQ:Sub_pq_path", NULL);

    SetCustomerPQCfg(pMainPQPath, MAPI_PQ_MAIN_WINDOW);
    SetCustomerPQCfg(pSubPQPath, MAPI_PQ_SUB_WINDOW);

    if (stPanelInfo.pPanelName != NULL)
    {
        delete[] stPanelInfo.pPanelName;
    }

#if (ENABLE_BACKEND == 1)
    if (m_pPanelini != NULL)
    {
        MAPI_UrsaType ursaInfo;
        ursaInfo.PanelBitNums = iniparser_getint(m_pPanelini, "ursa:m_PanelBitNums",0);
        ursaInfo.bTIMode = iniparser_getint(m_pPanelini, "ursa:m_bTIMode", 0);
        ursaInfo.bSwapPol = iniparser_getint(m_pPanelini, "ursa:m_bSwapPol", 0);
        ursaInfo.bShiftPair = iniparser_getint(m_pPanelini, "ursa:m_bShiftPair", 0);
        ursaInfo.bSwapPair = iniparser_getint(m_pPanelini, "ursa:m_bSwapPair", 0);
        ursaInfo.ucSwap = iniparser_getint(m_pPanelini, "ursa:m_ucSwap", 0);
        ursaInfo.ucSwing = iniparser_getint(m_pPanelini, "ursa:m_ucSwing", 0);

        SetUrsaInfoCfg(&ursaInfo);
    }
#endif

#endif

    SetFBLModeThreshold(iniparser_getunsignedint(m_pBoardini, "FBL_SETTINGS:FBL_THRESHOLD", 0XFFFFFFFF));

#if 0
    SetTVParam();
    SetInputMux();
#endif
    return TRUE;
}

MAPI_BOOL SystemInfo::SetPanelInfo(EN_MAPI_TIMING enTiming)
{
    std::map<EN_MAPI_TIMING, PanelInfo_t *>::iterator iter = m_PanelMap.find(enTiming);
    if (iter != m_PanelMap.end())
    {
        if (iter->second == NULL)
        {
            // The panel has load again, but panel doesn't exist.
            SYSTEM_INFO_ERR("Specified panel %x has load, but panel doesn't exist.\n", enTiming);
            ASSERT(0);
            return MAPI_FALSE;
        }
    }

    std::map<EN_MAPI_TIMING, std::string>::iterator iter1 = m_PanelPathMap.find(enTiming);
    if (iter1 == m_PanelPathMap.end())
    {
        // Specified panel doesn't exist in m_PanelPathMap. Please check Customer_1.ini file.
        SYSTEM_INFO_ERR("Specified panel %x doesn't exist in Customer_1.ini.\n", enTiming);
        ASSERT(0);
        return MAPI_FALSE;
    }

    // Read board ini
    if (m_pBoardini == NULL)
    {
        m_pBoardini = iniparser_load(m_strBoardIniFileName.c_str());
    }

    // panel.ini -> dictionary
    dictionary *pPanelini = NULL;
    pPanelini = iniparser_load(iter1->second.c_str());
    if (pPanelini == NULL)
    {
        SYSTEM_INFO_ERR("Cannot open panel ini file: %s\n", iter1->second.c_str());
        m_PanelMap[enTiming] = NULL;
        ASSERT(pPanelini);
        return MAPI_FALSE;
    }

    // dictionary -> MAPI_PanelType
    MAPI_PanelType stPanelInfo;
    stPanelInfo.pPanelName = new char[PANEL_NAME_LEN];
    memset((void*)stPanelInfo.pPanelName, 0, PANEL_NAME_LEN);
    LoadPanelInfo(pPanelini, &stPanelInfo);
    iniparser_freedict(pPanelini);
    pPanelini = NULL;

    iniparser_freedict(m_pBoardini);
    m_pBoardini = NULL;

    SYSTEM_INFO_FLOW("Load panel name : %s.\n", stPanelInfo.pPanelName);

    SetPanelInfoCfg(enTiming, &stPanelInfo);

    if (stPanelInfo.pPanelName != NULL)
    {
        delete[] stPanelInfo.pPanelName;
    }

    return MAPI_TRUE;

}

MAPI_BOOL SystemInfo::LoadPanelInfo(dictionary* pPanelini, MAPI_PanelType* stPanelInfo)
{
    strncpy((char*)stPanelInfo->pPanelName, iniparser_getstr(pPanelini, "panel:m_pPanelName"), PANEL_NAME_LEN);

    stPanelInfo->bPanelDither          = iniparser_getboolean(pPanelini, "panel:m_bPanelDither", TRUE);
    stPanelInfo->ePanelLinkType        = (MAPI_APIPNL_LINK_TYPE)iniparser_getint(pPanelini, "panel:m_ePanelLinkType", E_MAPI_LINK_LVDS);
    stPanelInfo->bPanelDualPort        = iniparser_getboolean(pPanelini, "panel:m_bPanelDualPort", TRUE);
    stPanelInfo->bPanelSwapPort        = iniparser_getboolean(pPanelini, "panel:m_bPanelSwapPort", TRUE);
    stPanelInfo->bPanelSwapOdd_ML      = iniparser_getboolean(pPanelini, "panel:m_bPanelSwapOdd_ML", TRUE);
    stPanelInfo->bPanelSwapEven_ML     = iniparser_getboolean(pPanelini, "panel:m_bPanelSwapEven_ML", TRUE);
    stPanelInfo->bPanelSwapOdd_RB      = iniparser_getboolean(pPanelini, "panel:m_bPanelSwapOdd_RB", TRUE);
    stPanelInfo->bPanelSwapEven_RB     = iniparser_getboolean(pPanelini, "panel:m_bPanelSwapEven_RB", TRUE);

    //when mapi_display.cpp set MApi_PNL_SkipTimingChange(TRUE), here panel setting just be skip, can't setting to utopia
#if (STB_ENABLE == 1) || (CONNECTTV_BOX == 1) || (A3_STB_ENABLE == 1)
    stPanelInfo->bPanelSwapLVDS_POL    = iniparser_getboolean(pPanelini, "panel:m_bPanelSwapLVDS_POL", TRUE);
    stPanelInfo->bPanelSwapLVDS_CH     = iniparser_getboolean(pPanelini, "panel:m_bPanelSwapLVDS_CH", TRUE);
    stPanelInfo->bPanelPDP10BIT        = iniparser_getboolean(pPanelini, "panel:m_bPanelPDP10BIT", TRUE);
#else
    stPanelInfo->bPanelSwapLVDS_POL    = iniparser_getboolean(m_pBoardini, "PanelRelativeSetting:m_bPANEL_SWAP_LVDS_POL", TRUE);
    stPanelInfo->bPanelSwapLVDS_CH     = iniparser_getboolean(m_pBoardini, "PanelRelativeSetting:m_bPANEL_SWAP_LVDS_CH", TRUE);
    stPanelInfo->bPanelPDP10BIT        = iniparser_getboolean(m_pBoardini, "PanelRelativeSetting:m_bPANEL_PDP_10BIT", TRUE);
#endif

    stPanelInfo->bPanelLVDS_TI_MODE    = iniparser_getboolean(pPanelini, "panel:m_bPanelLVDS_TI_MODE", TRUE);
    stPanelInfo->ucPanelDCLKDelay      = iniparser_getint(pPanelini, "panel:m_ucPanelDCLKDelay", 0);
    stPanelInfo->bPanelInvDCLK         = iniparser_getboolean(pPanelini, "panel:m_bPanelInvDCLK", TRUE);
    stPanelInfo->bPanelInvDE           = iniparser_getboolean(pPanelini, "panel:m_bPanelInvDE", TRUE);
    stPanelInfo->bPanelInvHSync        = iniparser_getboolean(pPanelini, "panel:m_bPanelInvHSync", TRUE);
    stPanelInfo->bPanelInvVSync        = iniparser_getboolean(pPanelini, "panel:m_bPanelInvVSync", TRUE);

    stPanelInfo->ucPanelDCKLCurrent    = iniparser_getint(pPanelini, "panel:m_ucPanelDCKLCurrent", 0);
    stPanelInfo->ucPanelDECurrent      = iniparser_getint(pPanelini, "panel:m_ucPanelDECurrent", 0);
    stPanelInfo->ucPanelODDDataCurrent = iniparser_getint(pPanelini, "panel:m_ucPanelODDDataCurrent", 0);
    stPanelInfo->ucPanelEvenDataCurrent= iniparser_getint(pPanelini, "panel:m_ucPanelEvenDataCurrent", 0);

    stPanelInfo->wPanelOnTiming1       = iniparser_getint(pPanelini, "panel:m_wPanelOnTiming1", 0);
    stPanelInfo->wPanelOnTiming2       = iniparser_getint(pPanelini, "panel:m_wPanelOnTiming2", 0);
    stPanelInfo->wPanelOffTiming1      = iniparser_getint(pPanelini, "panel:m_wPanelOffTiming1", 0);
    stPanelInfo->wPanelOffTiming2      = iniparser_getint(pPanelini, "panel:m_wPanelOffTiming2", 0);

    stPanelInfo->ucPanelHSyncWidth     = iniparser_getint(pPanelini, "panel:m_ucPanelHSyncWidth", 0);
    stPanelInfo->ucPanelHSyncBackPorch = iniparser_getint(pPanelini, "panel:m_ucPanelHSyncBackPorch", 0);

    stPanelInfo->ucPanelVSyncWidth     = iniparser_getint(pPanelini, "panel:m_ucPanelVSyncWidth", 0);
    stPanelInfo->ucPanelVBackPorch     = iniparser_getint(pPanelini, "panel:m_ucPanelVBackPorch", 0);

    stPanelInfo->wPanelHStart          = iniparser_getint(pPanelini, "panel:m_wPanelHStart", 0);
    stPanelInfo->wPanelVStart          = iniparser_getint(pPanelini, "panel:m_wPanelVStart", 0);

    stPanelInfo->wPanelWidth           = iniparser_getint(pPanelini, "panel:m_wPanelWidth", 0);
    stPanelInfo->wPanelHeight          = iniparser_getint(pPanelini, "panel:m_wPanelHeight", 0);

    stPanelInfo->wPanelMaxHTotal       = iniparser_getint(pPanelini, "panel:m_wPanelMaxHTotal", 0);
    stPanelInfo->wPanelHTotal          = iniparser_getint(pPanelini, "panel:m_wPanelHTotal", 0);
    stPanelInfo->wPanelMinHTotal       = iniparser_getint(pPanelini, "panel:m_wPanelMinHTotal", 0);

    stPanelInfo->wPanelMaxVTotal       = iniparser_getint(pPanelini, "panel:m_wPanelMaxVTotal", 0);
    stPanelInfo->wPanelVTotal          = iniparser_getint(pPanelini, "panel:m_wPanelVTotal", 0);
    stPanelInfo->wPanelMinVTotal       = iniparser_getint(pPanelini, "panel:m_wPanelMinVTotal", 0);

    stPanelInfo->dwPanelMaxDCLK        = (MAPI_U8)iniparser_getint(pPanelini, "panel:m_dwPanelMaxDCLK", 0);
    stPanelInfo->dwPanelDCLK           = (MAPI_U8)iniparser_getint(pPanelini, "panel:m_dwPanelDCLK", 0);
    stPanelInfo->dwPanelMinDCLK        = (MAPI_U8)iniparser_getint(pPanelini, "panel:m_dwPanelMinDCLK", 0);

    stPanelInfo->wSpreadSpectrumStep   = iniparser_getint(pPanelini, "panel:m_wSpreadSpectrumStep", 0);
    stPanelInfo->wSpreadSpectrumSpan   = iniparser_getint(pPanelini, "panel:m_wSpreadSpectrumSpan", 0);

    stPanelInfo->ucDimmingCtl          = iniparser_getint(pPanelini, "panel:m_ucDimmingCtl", 0);
    stPanelInfo->ucMaxPWMVal           = iniparser_getint(pPanelini, "panel:m_ucMaxPWMVal", 0);
    stPanelInfo->ucMinPWMVal           = iniparser_getint(pPanelini, "panel:m_ucMinPWMVal", 0);

    stPanelInfo->bPanelDeinterMode     = iniparser_getboolean(pPanelini, "panel:m_bPanelDeinterMode", TRUE);

    stPanelInfo->ucPanelAspectRatio    = (MAPI_PNL_ASPECT_RATIO)iniparser_getint(pPanelini, "panel:m_ucPanelAspectRatio", 0);

#if (STB_ENABLE == 1) || (CONNECTTV_BOX == 1) || (A3_STB_ENABLE == 1)
    stPanelInfo->u16LVDSTxSwapValue = iniparser_getint(pPanelini, "panel:m_u16LVDSTxSwapValue", 0);
#else
    stPanelInfo->u16LVDSTxSwapValue    = (iniparser_getint(m_pBoardini, "PanelRelativeSetting:m_u16LVDS_PN_SWAP_H", 0) << 8) | (iniparser_getint(m_pBoardini, "PanelRelativeSetting:m_u16LVDS_PN_SWAP_L", 0));
#endif

#ifdef TI_BIT_MODE
    stPanelInfo->ucTiBitMode           = (MAPI_APIPNL_TIBITMODE)TI_BIT_MODE;
#else
    stPanelInfo->ucTiBitMode           = (MAPI_APIPNL_TIBITMODE)iniparser_getint(pPanelini, "panel:m_ucTiBitMode", -1);
#endif

    stPanelInfo->ucOutputFormatBitMode = (MAPI_APIPNL_OUTPUTFORMAT_BITMODE)iniparser_getint(pPanelini, "panel:m_ucOutputFormatBitMode", 0);
    stPanelInfo->bPanelSwapOdd_RG      = iniparser_getboolean(pPanelini, "panel:m_bPanelSwapOdd_RG", TRUE);
    stPanelInfo->bPanelSwapEven_RG     = iniparser_getboolean(pPanelini, "panel:m_bPanelSwapEven_RG", TRUE);
    stPanelInfo->bPanelSwapOdd_GB      = iniparser_getboolean(pPanelini, "panel:m_bPanelSwapOdd_GB", TRUE);
    stPanelInfo->bPanelSwapEven_GB     = iniparser_getboolean(pPanelini, "panel:m_bPanelSwapEven_GB", TRUE);

    stPanelInfo->bPanelDoubleClk       = iniparser_getboolean(pPanelini, "panel:m_bPanelDoubleClk", TRUE);
    stPanelInfo->dwPanelMaxSET         = iniparser_getint(pPanelini, "panel:m_dwPanelMaxSET", 0);
    stPanelInfo->dwPanelMinSET         = iniparser_getint(pPanelini, "panel:m_dwPanelMinSET", 0);

    stPanelInfo->u16tRx = iniparser_getint(pPanelini, "hdr:Rx", 32000); // target Rx
    stPanelInfo->u16tRy = iniparser_getint(pPanelini, "hdr:Ry", 16500); // target Ry
    stPanelInfo->u16tGx = iniparser_getint(pPanelini, "hdr:Gx", 15000); // target Gx
    stPanelInfo->u16tGy = iniparser_getint(pPanelini, "hdr:Gy", 30000); // target Gy
    stPanelInfo->u16tBx = iniparser_getint(pPanelini, "hdr:Bx", 7500); // target Bx
    stPanelInfo->u16tBy = iniparser_getint(pPanelini, "hdr:By", 3000); // target By
    stPanelInfo->u16tWx = iniparser_getint(pPanelini, "hdr:Wx", 15635); // target Wx
    stPanelInfo->u16tWy = iniparser_getint(pPanelini, "hdr:Wy", 16450); // target Wy

#if (STB_ENABLE == 1) || (CONNECTTV_BOX == 1) || (A3_STB_ENABLE == 1)
#else
    stPanelInfo->bPanel3DFreerunFlag     = iniparser_getboolean(pPanelini, "panel:bPanel3DFreerunFlag", 0);
    stPanelInfo->bPanel2DFreerunFlag     = iniparser_getboolean(pPanelini, "panel:bPanel2DFreerunFlag", 0);
    stPanelInfo->bPanelReverseFlag       = iniparser_getboolean(pPanelini, "panel:bPanelReverseFlag", 0);
    stPanelInfo->bSGPanelFlag            = iniparser_getboolean(pPanelini, "panel:bSGPanelFlag", 0);
    stPanelInfo->bXCOutput120hzSGPanelFlag  = iniparser_getboolean(pPanelini, "panel:bXCOutput120hzSGPanelFlag", 0);
#endif

#if (AUTO_TEST == 1)
#define CMDLINE_PATH "/proc/cmdline"
#define CMDLINE_SIZE 2048
#define KEYWORD_TO_AUTOTEST "autotest=true"

    FILE *cmdLine;
    char cmdLineBuf[CMDLINE_SIZE];

    cmdLine=fopen(CMDLINE_PATH, "r");
    if(cmdLine != NULL)
    {
        fgets(cmdLineBuf, CMDLINE_SIZE, cmdLine);
        fclose(cmdLine);
        if (strstr(cmdLineBuf, KEYWORD_TO_AUTOTEST))
            stPanelInfo->ucOutTimingMode       = (MAPI_APIPNL_OUT_TIMING_MODE)1;//iniparser_getint(PanelInfo, "panel:m_ucOutTimingMode", -1);
        else
            stPanelInfo->ucOutTimingMode       = (MAPI_APIPNL_OUT_TIMING_MODE)iniparser_getint(pPanelini, "panel:m_ucOutTimingMode", 0);
    }
    else
    {
        stPanelInfo->ucOutTimingMode       = (MAPI_APIPNL_OUT_TIMING_MODE)iniparser_getint(pPanelini, "panel:m_ucOutTimingMode", 0);
        printf("\nAUTO_TEST is Enable, but read cmdline FAIL!!\n\n");
    }
#else
    stPanelInfo->ucOutTimingMode       = (MAPI_APIPNL_OUT_TIMING_MODE)iniparser_getint(pPanelini, "panel:m_ucOutTimingMode", 0);
#endif

#if (STB_ENABLE == 1) || (CONNECTTV_BOX == 1) || (A3_STB_ENABLE == 1)
    stPanelInfo->bPanelNoiseDith       = iniparser_getboolean(pPanelini, "panel:m_bPanelNoiseDith", 0);
#else
    //stPanelInfo->bPanelNoiseDith       = iniparser_getboolean(pPanelini, "panel:m_bPanelNoiseDith", -1);
#endif

    // Extend data.
    stPanelInfo->u16PanelMaxDCLK        = (MAPI_U16)iniparser_getint(pPanelini, "panel:m_dwPanelMaxDCLK", 0);
    stPanelInfo->u16PanelDCLK           = (MAPI_U16)iniparser_getint(pPanelini, "panel:m_dwPanelDCLK", 0);
    stPanelInfo->u16PanelMinDCLK        = (MAPI_U16)iniparser_getint(pPanelini, "panel:m_dwPanelMinDCLK", 0);
    stPanelInfo->u16PanelLinkExtType    = (MAPI_U16)iniparser_getint(pPanelini, "panel:m_ePanelLinkExtType", E_MAPI_LINK_EXT);
    stPanelInfo->dPWMSim3DLRScale       = (double)iniparser_getdouble(pPanelini, (char *)"panel:dPWMSim3DLRScale", 1.0);

    return TRUE;
}

MAPI_BOOL SystemInfo::ParserGammaTable(GAMMA_TABLE_t *pGAMMA_TABLE_t, const char *pparameter, MAPI_U8 which_rgb)
{
    if (pparameter == NULL)
    {
        ASSERT(0);
    }

    vector<string*> strArray;
    ParserStringToArray(pparameter, strArray);

    if (strArray.size() > GammaArrayMAXSize)
    {
        printf("WRONG GAMMA TABLE FORMAT FROM INI FILE\n\n");
        ASSERT(0);
    }

    for (unsigned int gamma_table_index = 0; gamma_table_index < strArray.size(); gamma_table_index++)
    {
        if (strArray[gamma_table_index] == NULL)
        {
            printf("WRONG GAMMA TABLE FORMAT FROM INI FILE\n\n");
            ASSERT(0);
        }

        int temp_value = 0;
        int filledResult = sscanf(strArray[gamma_table_index]->c_str(), "%x", &temp_value);

        if (filledResult <= 0)
        {
            printf("WRONG GAMMA TABLE FORMAT FROM INI FILE\n\n");
            ASSERT(0);
        }

        if ((temp_value < 0x00) || (temp_value > 0xFF))
        {
            printf("WRONG GAMMA TABLE FORMAT FROM INI FILE\n\n");
            ASSERT(0);
        }

        if(which_rgb=='r')
        {
            pGAMMA_TABLE_t->NormalGammaR[gamma_table_index] = static_cast<MAPI_U8>(temp_value);
        }
        else if(which_rgb=='g')
        {
            pGAMMA_TABLE_t->NormalGammaG[gamma_table_index] = static_cast<MAPI_U8>(temp_value);
        }
        else if(which_rgb=='b')
        {
            pGAMMA_TABLE_t->NormalGammaB[gamma_table_index] = static_cast<MAPI_U8>(temp_value);
        }

        delete strArray[gamma_table_index];
    }

    return TRUE;
}

MAPI_BOOL SystemInfo::SetGammaTable(int iGammaIdx)
{
    char GammaTableSectionItemName[] = "gamma_table_1:parameter_r";
    const MAPI_U8 u8MaxGammaTable = 10; // maximum number of Gamma table
    GAMMA_TABLE_t   *Board_GammaTableInfo[u8MaxGammaTable] = {NULL};
    const char *pparameter;
    unsigned char u8Cnt;

    if((iGammaIdx>u8MaxGammaTable) || (iGammaIdx<0))
    {
        printf("WRONG GAMMA TABLE NO FROM INI FILE\n");
        ASSERT(0);
    }

    ASSERT(m_pPanelini);

    SYSTEM_INFO_DBG("%s:::::::::: %u\n", __FILE__, __LINE__);

    for (u8Cnt = 0; u8Cnt < u8MaxGammaTable; u8Cnt++)
    {
        SYSTEM_INFO_DBG("Parsing GAMMA[%d]\n", u8Cnt);
    //table no.
        GammaTableSectionItemName[12]= '0' + u8Cnt;

        GammaTableSectionItemName[24]= 'r';
        pparameter           = iniparser_getstr(m_pPanelini, GammaTableSectionItemName);
        if (pparameter == NULL)
        {
            break;
        }
        SysIniBlock.TotalGammaTableNo++;
        Board_GammaTableInfo[u8Cnt] = (GAMMA_TABLE_t *)malloc(sizeof(GAMMA_TABLE_t));
        ASSERT(Board_GammaTableInfo[u8Cnt]);

        ParserGammaTable(Board_GammaTableInfo[u8Cnt], pparameter, 'r');

    GammaTableSectionItemName[24]= 'g';
    pparameter           = iniparser_getstr(m_pPanelini, GammaTableSectionItemName);
        ParserGammaTable(Board_GammaTableInfo[u8Cnt], pparameter, 'g');

    GammaTableSectionItemName[24]= 'b';
    pparameter           = iniparser_getstr(m_pPanelini, GammaTableSectionItemName);
        ParserGammaTable(Board_GammaTableInfo[u8Cnt], pparameter, 'b');
    }

    SetGammaTableCfg(Board_GammaTableInfo, u8Cnt, iGammaIdx);

    for (unsigned char i = 0; i < u8Cnt; i++)
    {
        free(Board_GammaTableInfo[i]);
    }
    return TRUE;

}

MAPI_BOOL SystemInfo::SetTVParam(void)
{
    memcpy(&(m_TV_info_table), &(BOARD_TV_PARAM), sizeof(S_TV_TYPE_INFO));

#if (SYSINFO_DEBUG == 1)
    printf("--------------------TVInfo Start ------------------\n");
    printf("TV_info_table = (0x%x, 0x%x, 0x%x, 0x%x, 0x%x)\n", \
           m_TV_info_table.eTV_type, m_TV_info_table.eATV_type, \
           m_TV_info_table.eDTV_type, m_TV_info_table.eAUDIO_type, \
           /*m_TV_info_table.eSTB_type,*/ m_TV_info_table.eIPEnable_type);
    printf("Route[0] = %u , Route[1] = %u\n", m_TV_info_table.u8RoutePath[0], m_TV_info_table.u8RoutePath[1]);
    printf("Route[2] = %u , Route[3] = %u\n", m_TV_info_table.u8RoutePath[2], m_TV_info_table.u8RoutePath[3]);
    printf("--------------------TVInfo End--------------------\n\n");
#endif
    return TRUE;
}

MAPI_BOOL SystemInfo::SetInputMux(void)
{
    m_MuxConf.nSize = BOARD_INPUTMUX_NUM;
    if(m_pU32MuxInfo != NULL)
    {
        delete[] ((MAPI_VIDEO_INPUTSRCTABLE *)m_pU32MuxInfo);
        m_pU32MuxInfo = NULL;
    }

    if(m_MuxConf.nSize != 0)
    {
        m_pU32MuxInfo = new (std::nothrow) MAPI_VIDEO_INPUTSRCTABLE[m_MuxConf.nSize];
        ASSERT(m_pU32MuxInfo);
        memcpy(m_pU32MuxInfo, &(Board_Input_Mux_Table), sizeof(MAPI_VIDEO_INPUTSRCTABLE)*m_MuxConf.nSize);

    }

#if (SYSINFO_DEBUG == 1)
    {
        int i;
        MAPI_VIDEO_INPUTSRCTABLE *ptr = (MAPI_VIDEO_INPUTSRCTABLE*)m_pU32MuxInfo;
        if(ptr != NULL)
        {
            printf("------------------InputMuxCfg Start-2--------------------\n");
            printf("InputMuxTable Size = %d\n", m_MuxConf.nSize);
            for(i = 0; i < (int)m_MuxConf.nSize; i++)
                printf("MuxInfo[%d] = (%u, %u, %u)\n", i, ptr[i].u32EnablePort, \
                       ptr[i].u32Port[0], ptr[i].u32Port[1]);
            printf("------------------InputMuxCfg End----------------------\n\n");
        }
    }
#endif
    return TRUE;
}

unsigned char SystemInfo::SetGPIOCfg(void)
{
    m_gpioconfig.n_GPIO = BOARD_GPIO_NUM ;

    if(m_pGPIOColl != NULL)
    {
        delete [] m_pGPIOColl;
        m_pGPIOColl = NULL;
    }

    if(m_gpioconfig.n_GPIO != 0)
    {
        m_pGPIOColl = new (std::nothrow) GPIOInfo_t[m_gpioconfig.n_GPIO];
        ASSERT(m_pGPIOColl);
        memcpy(m_pGPIOColl, &(Board_GPIO_Setting), sizeof(GPIOInfo_t)*m_gpioconfig.n_GPIO);
    }

#if (SYSINFO_DEBUG == 1)
    printf("--------------------GPIOCfg Start------------------\n");
    printf("GPIO Device List\n");
    printf("===================\n");
    for(unsigned char i = 0; i < m_gpioconfig.n_GPIO; i++)
    {
        printf("GPIO Device[%d] ID = %u Inverter = %s, PadNo = %u\n", i, \
               m_pGPIOColl[i].gID, (TRUE==m_pGPIOColl[i].gInvertor) ? "True" : "False", \
               m_pGPIOColl[i].gPadNo);
    }

    printf("--------------------GPIOCfg End--------------------\n\n");
#endif
    return TRUE;
}

MAPI_BOOL SystemInfo::SetI2CCfg(void)
{
    int busnum = 0;

#if (STB_ENABLE == 1)

    m_i2cconfig.n_i2c_bus = BOARD_I2C_HWBUS_NUM + BOARD_I2C_SWBUS_NUM;

    m_i2cconfig.n_i2c_device = BOARD_I2C_DEVICE_NUM;

    if(m_pI2CBuses != NULL)
    {
        delete [] m_pI2CBuses;
        m_pI2CBuses = NULL;
    }

    if(m_pI2CDevices != NULL)
    {
        delete [] m_pI2CDevices;
        m_pI2CDevices = NULL;
    }

    if(m_i2cconfig.n_i2c_bus != 0)
    {
        m_pI2CBuses = new (std::nothrow) I2CBus_t[m_i2cconfig.n_i2c_bus];
        ASSERT(m_pI2CBuses);
        for(unsigned char i = 0; i < BOARD_I2C_SWBUS_NUM ; i ++)
        {
            m_pI2CBuses[busnum].eBus_type = EN_I2C_TYPE_SW;
            m_pI2CBuses[busnum].sw = BOARD_I2C_SWBUS[i];
            busnum ++;
        }
        for(unsigned char i = 0; i < BOARD_I2C_HWBUS_NUM; i ++)
        {
            m_pI2CBuses[busnum].eBus_type = EN_I2C_TYPE_HW;
            m_pI2CBuses[busnum].hw = BOARD_I2C_HWBUS[i];
            busnum ++;
        }
    }

    if(m_i2cconfig.n_i2c_device != 0)
    {
        m_pI2CDevices = new (std::nothrow) I2CDeviceInfo_t[m_i2cconfig.n_i2c_device];
        ASSERT(m_pI2CDevices);
        memcpy(m_pI2CDevices, &(Board_I2C_Dev), sizeof(I2CDeviceInfo_s)*m_i2cconfig.n_i2c_device);

    }

#else

    int tmpBusNum = 0, maxHWBusNum = 0, minHWBusNum = 0;
    int maxHWIndex = 0, minHWIndex = 0;

    // get max HW I2C bus port number to decide the array size
    if (BOARD_I2C_HWBUS_NUM == 0)
    {
        m_i2cconfig.n_i2c_bus = BOARD_I2C_SWBUS_NUM;
    }
    else
    {
          minHWBusNum = BOARD_I2C_HWBUS[0].ePort / 8 + 1;
        for(unsigned char i = 0; i < BOARD_I2C_HWBUS_NUM; i ++)
        {
            tmpBusNum = BOARD_I2C_HWBUS[i].ePort / 8 + 1;
            if(tmpBusNum > maxHWBusNum)
            {
                maxHWBusNum = tmpBusNum;
                maxHWIndex = i;
            }

            if(tmpBusNum < minHWBusNum)
            {
                minHWBusNum = tmpBusNum;
                minHWIndex = i;
            }
        }
        // prevent max and min HW I2C port use old and new port at the same time
        if((BOARD_I2C_HWBUS[minHWIndex].ePort < HW_I2C_PORT_VER_2(EN_HWI2C_PORT_0)) && (BOARD_I2C_HWBUS[maxHWIndex].ePort >= HW_I2C_PORT_VER_2(EN_HWI2C_PORT_0)))
        {
            printf("ERROR!!!!!! Please check the port setting in HW I2C. Do not use old and new port enum at the same time.\n");
            ASSERT(0);
        }

        // use new mechanism, HW I2C would put in I2C bus array in order, number of array = HW bus num + SW bus num
        if(BOARD_I2C_HWBUS[maxHWIndex].ePort >= HW_I2C_PORT_VER_2(EN_HWI2C_PORT_0))
        {
            m_i2cconfig.n_i2c_bus = BOARD_I2C_HWBUS_NUM + BOARD_I2C_SWBUS_NUM;
        }
        else    // use original mechanism, HW I2C would put in I2C bus array by port number, number of array = max of SWbus num and HW bus num
        {
            if(maxHWBusNum > BOARD_I2C_SWBUS_NUM)
            {
                m_i2cconfig.n_i2c_bus = maxHWBusNum;
            }
            else
            {
                m_i2cconfig.n_i2c_bus = BOARD_I2C_SWBUS_NUM;
            }
        }
    }

    m_i2cconfig.n_i2c_device = BOARD_I2C_DEVICE_NUM;

    if(m_pI2CBuses != NULL)
    {
        delete [] m_pI2CBuses;
        m_pI2CBuses = NULL;
    }

    if(m_pI2CDevices != NULL)
    {
        delete [] m_pI2CDevices;
        m_pI2CDevices = NULL;
    }

    if(m_i2cconfig.n_i2c_bus != 0)
    {
        m_pI2CBuses = new (std::nothrow) I2CBus_t[m_i2cconfig.n_i2c_bus];
        ASSERT(m_pI2CBuses);

        // initialize I2C bus array, including setting eBus_type = EN_I2C_TYPE_NONE
        memset(m_pI2CBuses, 0, sizeof(I2CBus_t)*m_i2cconfig.n_i2c_bus);

        // put SW I2C in I2C bus array in order
        for(unsigned char i = 0; i < BOARD_I2C_SWBUS_NUM ; i ++)
        {
            m_pI2CBuses[busnum].eBus_type = EN_I2C_TYPE_SW;
            m_pI2CBuses[busnum].sw = BOARD_I2C_SWBUS[i];
            busnum ++;
        }
        for(unsigned char i = 0; i < BOARD_I2C_HWBUS_NUM; i ++)
        {
            // use new mechanism, put HW I2C in I2C bus array in order
            if(BOARD_I2C_HWBUS[maxHWIndex].ePort >= HW_I2C_PORT_VER_2(EN_HWI2C_PORT_0))
            {
                m_pI2CBuses[busnum].eBus_type = EN_I2C_TYPE_HW;
                m_pI2CBuses[busnum].hw = BOARD_I2C_HWBUS[i];
                m_pI2CBuses[busnum].hw.ePort = (EN_HWI2C_PORT)(BOARD_I2C_HWBUS[i].ePort - HW_I2C_PORT_VER2_BASE);
                busnum ++;
            }
            else    // use new mechanism, put HW I2C in I2C bus array by port number
            {
                //for HW I2C setting usage 20110915
                busnum = (BOARD_I2C_HWBUS[i].ePort)/8;
                //printf(" \33[0;31m <<<<<111>>>>>>mapi_system::SetI2CCfg busnum = %d\n \33[m",busnum);

                // check whether HW I2C bus and SW I2C bus conflict at the same index in I2C bus array
                if(busnum < BOARD_I2C_SWBUS_NUM)
                {
                    printf("ERROR!!!!!! Please check the setting in HW & SW I2C. HW & SW I2C conflict at the same index in I2C bus array.\n");
                    ASSERT(0);
                }

                m_pI2CBuses[busnum].eBus_type = EN_I2C_TYPE_HW;
                m_pI2CBuses[busnum].hw = BOARD_I2C_HWBUS[i];
            }
        }
    }

    if(m_i2cconfig.n_i2c_device != 0)
    {
        m_pI2CDevices = new (std::nothrow) I2CDeviceInfo_t[m_i2cconfig.n_i2c_device];
        ASSERT(m_pI2CDevices);

        // check whether the setting of i2c_bus over the range of I2C bus array
        for(unsigned char i = 0; i < m_i2cconfig.n_i2c_device; i ++)
        {
            if(Board_I2C_Dev[i].i2c_bus >= m_i2cconfig.n_i2c_bus)
            {
                printf("ERROR!!!!!! Please check the i2c_bus setting of Board_I2C_Dev in board define.\n");
                ASSERT(0);
            }
        }

        memcpy(m_pI2CDevices, &(Board_I2C_Dev), sizeof(I2CDeviceInfo_s)*m_i2cconfig.n_i2c_device);

    }

#endif

#if (SYSINFO_DEBUG == 1)
    printf("--------------------I2CCfg Start------------------\n");
    printf("I2C Bus List\n");
    printf("===================\n");
    for(unsigned char i = 0; i < m_i2cconfig.n_i2c_bus ; i++)
    {
        if(m_pI2CBuses[i].eBus_type == EN_I2C_TYPE_SW)
        {
            printf("SWBus[%d] SCL = %u SDA = %u, Delay = %u\n", i, \
                   m_pI2CBuses[i].sw.scl_pad, m_pI2CBuses[i].sw.sda_pad, \
                   m_pI2CBuses[i].sw.delay);
        }
        else if(m_pI2CBuses[i].eBus_type == EN_I2C_TYPE_HW)
        {
            printf("HWBus[%d] Reg = %u BPos = %u, bEn = %u, eSpeed = %d, \
                ePort = %d\n", i, m_pI2CBuses[i].hw.sI2CPin.u32Reg, \
                   m_pI2CBuses[i].hw.sI2CPin.u8BitPos, \
                   m_pI2CBuses[i].hw.sI2CPin.bEnable , \
                   m_pI2CBuses[i].hw.eSpeed, \
                   m_pI2CBuses[i].hw.ePort);
        }
    }

    printf("I2C Device List\n");
    printf("===================\n");
    for(unsigned long i = 0; i < m_i2cconfig.n_i2c_device; i++)
    {
        printf("I2C Device[%d] ID = %u SlaveId = %u, Bus = %u\n", i, \
               m_pI2CDevices[i].gID, m_pI2CDevices[i].slave_id, \
               m_pI2CDevices[i].i2c_bus);
    }

    printf("--------------------I2CCfg End--------------------\n\n");
#endif

    return TRUE;
}

MAPI_BOOL SystemInfo::SetWDTCfg(void)
{
    memcpy(&m_WDTInfo, &(BOARD_WDTInfo), sizeof(WDTInfo_t));

    if(m_WDTInfo.u32WdtTimer == 0)  // if wdt timer = 0 sec , we will force it to be 1 second
        m_WDTInfo.u32WdtTimer = 1;

#if (SYSINFO_DEBUG == 1)
    printf("------------------WDTCfg Start-----------------\n");
    printf("WdtEnable = %s, WdtTimerRegisterAddr = 0x%08lx, WdtTimer = %u\n", \
           m_WDTInfo.bWdtEnable ? "True" : "False", m_WDTInfo.u32WdtTimerReg, \
           m_WDTInfo.u32WdtTimer);
    printf("------------------WDTCfg End-------------------\n\n");
#endif
    return TRUE;
}

MAPI_BOOL SystemInfo::SetAudioInputMuxCfg(void)
{
    unsigned char nTablesize = 0, nTablesize1 = 0, nTablesize2 = 0;

    // Someone will use the MAPI_AUDIO_INPUT_SOURCE_TYPE enum to access the array,
    // so the array size need not less than enum number
    if (BOARD_AUDIO_INPUT_SOURCE_TYPE_SIZE < MAPI_AUDIO_SOURC_NUM)
    {
        printf("ERROR: Audio Mux Info Size is too small\n");
        ASSERT(0);
    }

    // Someone will use the MAPI_AUDIO_PATH_TYPE enum to access the array,
    // so the array size need not less than enum number
    if (BOARD_AUDIO_PATH_TYPE_SIZE < MAPI_AUDIO_PATH_NUM)
    {
        printf("ERROR: Audio Path Info Size is too small\n");
        ASSERT(0);
    }

    // Someone will use the MAPI_AUDIO_OUTPUT_TYPE enum to access the array,
    // so the array size need not less than enum number
    if (BOARD_AUDIO_OUTPUT_TYPE_SIZE < MAPI_AUDIO_OUTPUT_NUM)
    {
        printf("ERROR: Audio Output Type Info Size is too small\n");
        ASSERT(0);
    }

    if(m_pAudioMuxInfo != NULL)
    {
        delete [] m_pAudioMuxInfo;
        m_pAudioMuxInfo = NULL;
    }

    if(m_pAudioPathInfo != NULL)
    {
        delete [] m_pAudioPathInfo;
        m_pAudioPathInfo = NULL;
    }

    if(m_pAudioOutputTypeInfo != NULL)
    {
        delete [] m_pAudioOutputTypeInfo;
        m_pAudioOutputTypeInfo = NULL;
    }


    nTablesize = BOARD_AUDIO_INPUT_SOURCE_TYPE_SIZE;
    if(nTablesize != 0)
    {
        m_pAudioMuxInfo = new (std::nothrow) AudioMux_t[nTablesize];
        ASSERT(m_pAudioMuxInfo);
        memcpy(m_pAudioMuxInfo, &(BOARD_AudioMux_t), sizeof(AudioMux_t)*nTablesize);
    }

    nTablesize1 = BOARD_AUDIO_PATH_TYPE_SIZE;
    if(nTablesize1 != 0)
    {
        m_pAudioPathInfo = new (std::nothrow) AudioPath_t[nTablesize1];
        ASSERT(m_pAudioPathInfo);
        memcpy(m_pAudioPathInfo, &(BOARD_AudioPath_t), sizeof(AudioPath_t)*nTablesize1);
    }


    nTablesize2 = BOARD_AUDIO_OUTPUT_TYPE_SIZE;
    if(nTablesize2 != 0)
    {
        m_pAudioOutputTypeInfo = new (std::nothrow) AudioOutputType_t[nTablesize2];
        ASSERT(m_pAudioOutputTypeInfo);
        memcpy(m_pAudioOutputTypeInfo, &(BOARD_AudioOutputType_t), sizeof(AudioOutputType_t)*nTablesize2);
    }
    memcpy(&m_AudioDefaultInit, &(BOARD_AudioDefaultInit_t), sizeof(AudioDefualtInit_t));

#if (SYSINFO_DEBUG == 1)
    printf("-------------------AudioInputMuxCfg Start-------------------\n");
    if(m_pAudioMuxInfo != NULL)
    {
        printf("AudioInputMux Size = %u\n", nTablesize);
        for(unsigned char i = 0; i < nTablesize; i++)
        {
            printf("AudioMuxInfo[%d] = 0x%02lx\n", i, m_pAudioMuxInfo[i].u32Port);
        }
    }

    printf("\n");
    if(m_pAudioOutputTypeInfo != NULL)
    {
        printf("AudioPathInfo Size = %u\n", nTablesize1);
        for(unsigned char i = 0; i < nTablesize1; i++)
        {
            printf("AudioPathInfo[%d] = 0x%02lx\n", i, m_pAudioPathInfo[i].u32Path);
        }
    }

    printf("\n");
    if(m_pAudioOutputTypeInfo != NULL)
    {
        printf("AudioOutputInfo Size = %u\n", nTablesize2);
        for(unsigned char i = 0; i < nTablesize2; i++)
        {
            printf("AudioOutputTypeInfo[%d] = 0x%02lx\n", i, m_pAudioOutputTypeInfo[i].u32Output);
        }
    }

    printf("Default audio Src = %d, Path = %d, Output = %d\n", m_AudioDeafultInit.eAudioSrc, m_AudioDeafultInit.eAudioPath, m_AudioDeafultInit.eAudioPath);
    printf("-------------------AudioInputMuxCfg End==-------------------\n\n");
#endif

    return TRUE;
}

MAPI_BOOL SystemInfo::SetVideoInfo(void)
{

#if (AUTO_TEST == 1)
    #define CMDLINE_PATH "/proc/cmdline"
    #define CMDLINE_SIZE 2048
    #define KEYWORD_TO_AUTOTEST "autotest=true"

    FILE *cmdLine;
    char cmdLineBuf[CMDLINE_SIZE];

    cmdLine=fopen(CMDLINE_PATH, "r");
    if(cmdLine != NULL)
    {
        fgets(cmdLineBuf, CMDLINE_SIZE, cmdLine);
        fclose(cmdLine);

        if (strstr(cmdLineBuf, KEYWORD_TO_AUTOTEST))
        {
            SystemInfo::SetVideoInfoCfg(E_DTV, DTV_WIN_INFO_RES_NUM, &stDTVVideoWinInfo_MHEG5_DTG_Test[0][0], HOT_PLUG_INVERSE);
        }
        else
        {
            SystemInfo::SetVideoInfoCfg(E_DTV, DTV_WIN_INFO_RES_NUM, &stDTVVideoWinInfo[0][0], HOT_PLUG_INVERSE);
        }
    }
    else
    {
        printf("\nAUTO_TEST is Enable, but read cmdline FAIL!!\n\n");
    }
#else
     SystemInfo::SetVideoInfoCfg(E_DTV, DTV_WIN_INFO_RES_NUM, &stDTVVideoWinInfo[0][0], HOT_PLUG_INVERSE);
#endif

#ifdef HOT_5V_DETECT_GPIO_SELECT
    SystemInfo::SetVideoInfoCfg(E_HDMI, HDMI_WIN_INFO_RES_NUM, &stHDMIVideoWinInfo[0][0], HOT_PLUG_INVERSE, SysIniBlock.bDotByDotAble , HOT_5V_DETECT_GPIO_SELECT);
#else
    SystemInfo::SetVideoInfoCfg(E_HDMI, HDMI_WIN_INFO_RES_NUM, &stHDMIVideoWinInfo[0][0], HOT_PLUG_INVERSE, SysIniBlock.bDotByDotAble);
#endif

    SystemInfo::SetVideoInfoCfg(E_YPbPr, YPBPR_WIN_INFO_RES_NUM, &stYPBPRVideoWinInfo[0][0], HOT_PLUG_INVERSE);
    SystemInfo::SetVideoInfoCfg(E_CVBS, VD_WIN_INFO_RES_NUM, &stVDVideoWinInfo[0][0], HOT_PLUG_INVERSE);
#if (RVU_ENABLE == 1)
    SystemInfo::SetVideoInfoCfg(E_RVU, RVU_WIN_INFO_RES_NUM, &stRVUVideoWinInfo[0][0], HOT_PLUG_INVERSE);
#endif
    SystemInfo::SetVDCaptureWinMode(BOARD_VD_CAP_WIN_MODE);

    return TRUE;
}


MAPI_BOOL SystemInfo::LoadVGAEDIDInfo(VGA_EDID_Info_t *pVgaEdidInfo, void *pDict)
{
    char *pVgaEdidFile;
    dictionary *pEdidIni = (dictionary *)pDict;

    ASSERT(pEdidIni);

    pVgaEdidInfo->bEnabled = iniparser_getboolean(pEdidIni, "VGA_EDID:bEDIDEnabled", -1);
    pVgaEdidInfo->bUseDefaultValue = iniparser_getboolean(pEdidIni, "VGA_EDID:bUseDefaultValue", -1);
    pVgaEdidFile = iniparser_getstring(pEdidIni, "VGA_EDID:VGA_EDID_File", NULL);

    /* check the VGA EDID file */
    if((pVgaEdidFile == NULL) || (strlen(pVgaEdidFile) == 0))
    {
        SYSTEM_INFO_ERR("Can't open VGA EDID file!\n");
        return FALSE;
    }

    MAPI_U8 u8Buf[VGA_EDID_LEN];

    /* reset the string and buffer */
    memset(u8Buf, 0, VGA_EDID_LEN);

    /* open the VGA_EDID.bin */
    FILE *pBinFile = fopen((const char*)pVgaEdidFile, "r");
    if(pBinFile == NULL)
    {
        SYSTEM_INFO_ERR("BIN file open error!\n");
        return FALSE;
    }

    /* read the bin file (128 bytes) */
    size_t szBufLen = fread (u8Buf, 1, VGA_EDID_LEN, pBinFile);
    if(szBufLen == VGA_EDID_LEN)
    {
        /* correct! copy the bin file buffer to EDID[] */
        memcpy(pVgaEdidInfo->edid, u8Buf, VGA_EDID_LEN);
    }
    else
    {
        SYSTEM_INFO_ERR("Read an error length in the Bin file! (VGA EDID)\n");
    }

    /* close the bin file*/
    fclose(pBinFile);

    return TRUE;
}


MAPI_BOOL SystemInfo::SetVGAEDIDInfo(void)
{
    VGA_EDID_Info_t stVgaEdidInfo;

    ASSERT(m_pCustomerini);

    /* reset stVgaEdidInfo structure */
    memset(&stVgaEdidInfo, 0, sizeof(VGA_EDID_Info_t));

    /* load VGA EDID Info from INI file. */
    if(SystemInfo::LoadVGAEDIDInfo(&stVgaEdidInfo, m_pCustomerini) == FALSE)
    {
        SYSTEM_INFO_ERR("Load VGA EDID ifno failed!\n");
    }

    memcpy(&m_VgaEdidInfo, &(stVgaEdidInfo), sizeof(VGA_EDID_Info_t));

#if (SYSINFO_DEBUG == 1)
    printf("--------------------VGAEDIDInfo Start------------------\n");
    printf("VgaEdidInfo Enable = %s, VgaEdidInfo UsedefaultValue = %s\n", \
           (TRUE==m_VgaEdidInfo.bEnabled) ? "True" : "False", (TRUE==m_VgaEdidInfo.bUseDefaultValue) ? "True" : "False");
    if(m_VgaEdidInfo.bEnabled)
    {
        for(int i = 0; i < 128 ; i++)
            printf("VGAEdid[%d] = 0x%02x, ", i, m_VgaEdidInfo.edid[i]);
    }
    printf("\n");
    printf("--------------------VGAEDIDInfo End--------------------\n\n");
#endif
    return TRUE;
}


MAPI_BOOL SystemInfo::LoadHDMIEDIDInfo(HDMI_EDID_Info_t *pHdmiEdidInfo, void *pDict, int iHdmiNum)
{
    char *pHdmiEdidFile = NULL;
    dictionary *pEdidIni = (dictionary *)pDict;

    ASSERT(pEdidIni);

    switch(iHdmiNum)
    {
    case 0:
        /* HDMI EDID #1 */
        pHdmiEdidInfo->bEDIDEnabled = iniparser_getboolean(pEdidIni, "HDMI_EDID_1:bEDIDEnabled", -1);
        pHdmiEdidInfo->bUseDefaultValue = iniparser_getboolean(pEdidIni, "HDMI_EDID_1:bUseDefaultValue", -1);
        pHdmiEdidInfo->bCECEnabled = iniparser_getboolean(pEdidIni, "HDMI_EDID_1:bCECEnabled", -1);
        pHdmiEdidInfo->u16CECPhyAddr = iniparser_getint(pEdidIni, "HDMI_EDID_1:u16CECPhyAddr", -1);
        pHdmiEdidInfo->u8CECPhyAddrIdxL = iniparser_getint(pEdidIni, "HDMI_EDID_1:u8CECPhyAddrIdxL", -1);
        pHdmiEdidInfo->u8CECPhyAddrIdxH = iniparser_getint(pEdidIni, "HDMI_EDID_1:u8CECPhyAddrIdxH", -1);
        pHdmiEdidFile = iniparser_getstring(pEdidIni, "HDMI_EDID_1:HDMI_EDID_File", NULL);
        break;
    case 1:
        /* HDMI EDID #2 */
        pHdmiEdidInfo->bEDIDEnabled = iniparser_getboolean(pEdidIni, "HDMI_EDID_2:bEDIDEnabled", -1);
        pHdmiEdidInfo->bUseDefaultValue = iniparser_getboolean(pEdidIni, "HDMI_EDID_2:bUseDefaultValue", -1);
        pHdmiEdidInfo->bCECEnabled = iniparser_getboolean(pEdidIni, "HDMI_EDID_2:bCECEnabled", -1);
        pHdmiEdidInfo->u16CECPhyAddr = iniparser_getint(pEdidIni, "HDMI_EDID_2:u16CECPhyAddr", -1);
        pHdmiEdidInfo->u8CECPhyAddrIdxL = iniparser_getint(pEdidIni, "HDMI_EDID_2:u8CECPhyAddrIdxL", -1);
        pHdmiEdidInfo->u8CECPhyAddrIdxH = iniparser_getint(pEdidIni, "HDMI_EDID_2:u8CECPhyAddrIdxH", -1);
        pHdmiEdidFile = iniparser_getstring(pEdidIni, "HDMI_EDID_2:HDMI_EDID_File", NULL);
        break;
    case 2:
        /* HDMI EDID #3 */
        pHdmiEdidInfo->bEDIDEnabled = iniparser_getboolean(pEdidIni, "HDMI_EDID_3:bEDIDEnabled", -1);
        pHdmiEdidInfo->bUseDefaultValue = iniparser_getboolean(pEdidIni, "HDMI_EDID_3:bUseDefaultValue", -1);
        pHdmiEdidInfo->bCECEnabled = iniparser_getboolean(pEdidIni, "HDMI_EDID_3:bCECEnabled", -1);
        pHdmiEdidInfo->u16CECPhyAddr = iniparser_getint(pEdidIni, "HDMI_EDID_3:u16CECPhyAddr", -1);
        pHdmiEdidInfo->u8CECPhyAddrIdxL = iniparser_getint(pEdidIni, "HDMI_EDID_3:u8CECPhyAddrIdxL", -1);
        pHdmiEdidInfo->u8CECPhyAddrIdxH = iniparser_getint(pEdidIni, "HDMI_EDID_3:u8CECPhyAddrIdxH", -1);
        pHdmiEdidFile = iniparser_getstring(pEdidIni, "HDMI_EDID_3:HDMI_EDID_File", NULL);
        break;
    case 3:
        /* HDMI EDID #4 */
        pHdmiEdidInfo->bEDIDEnabled = iniparser_getboolean(pEdidIni, "HDMI_EDID_4:bEDIDEnabled", -1);
        pHdmiEdidInfo->bUseDefaultValue = iniparser_getboolean(pEdidIni, "HDMI_EDID_4:bUseDefaultValue", -1);
        pHdmiEdidInfo->bCECEnabled = iniparser_getboolean(pEdidIni, "HDMI_EDID_4:bCECEnabled", -1);
        pHdmiEdidInfo->u16CECPhyAddr = iniparser_getint(pEdidIni, "HDMI_EDID_4:u16CECPhyAddr", -1);
        pHdmiEdidInfo->u8CECPhyAddrIdxL = iniparser_getint(pEdidIni, "HDMI_EDID_4:u8CECPhyAddrIdxL", -1);
        pHdmiEdidInfo->u8CECPhyAddrIdxH = iniparser_getint(pEdidIni, "HDMI_EDID_4:u8CECPhyAddrIdxH", -1);
        pHdmiEdidFile = iniparser_getstring(pEdidIni, "HDMI_EDID_4:HDMI_EDID_File", NULL);
        break;
    default:
        SYSTEM_INFO_ERR("Out of range!\n");
        return FALSE;
    }

    /* check the HDMI EDID file */
    if((pHdmiEdidFile == NULL) || (strlen(pHdmiEdidFile) == 0))
    {
        SYSTEM_INFO_ERR("Can't open HDMI EDID %d File!\n", iHdmiNum + 1);
        return FALSE;
    }

    /* open the HDMI_EDID.bin */
    FILE *pBinFile = fopen((const char*) pHdmiEdidFile, "r");
    if(pBinFile == NULL)
    {
        SYSTEM_INFO_ERR("BIN file open error!\n");
        return FALSE;
    }

    MAPI_U8 u8Buf[HDMI_EDID_LEN];

    /* reset the buffer */
    memset(u8Buf, 0, HDMI_EDID_LEN);

    /* read the bin file (256 bytes) */
    size_t szBufLen = fread(u8Buf, 1, HDMI_EDID_LEN, pBinFile);
    if(szBufLen == HDMI_EDID_LEN)
    {
        /* correct! copy the bin file buffer to EDID[] */
        memcpy(pHdmiEdidInfo->edid, u8Buf, HDMI_EDID_LEN);
    }
    else
    {
        SYSTEM_INFO_ERR("Read an error length in the Bin file! (HDMI EDID %d)\n", iHdmiNum + 1);
    }

    /* close the bin file*/
    fclose(pBinFile);

    return TRUE;
}


MAPI_BOOL SystemInfo::SetHDMIEDIDInfoSet(void)
{
#if ((STB_ENABLE == 0) || (ENABLE_HDMI_RX == 1))
    HDMI_EDID_Info_t stHdmiEdidInfo[BOARD_HDMI_EDID_InfoCount];

    ASSERT(m_pCustomerini);

    /* start to load HDMI EDID Info for all HDMI ports */
    for(int i = 0; i < BOARD_HDMI_EDID_InfoCount; i++)
    {
        /* reset the stHdmiEdidInfo[i] data */
        memset(&stHdmiEdidInfo[i], 0, sizeof(HDMI_EDID_Info_t));

        /* load HDMI EDID Info from INI file. */
        if(SystemInfo::LoadHDMIEDIDInfo(&stHdmiEdidInfo[i], m_pCustomerini, i) == FALSE)
        {
            SYSTEM_INFO_ERR("Load HDMI EDID %d Failed!\n", i + 1);
        }
    }

    m_HdmiEdidInfoSet.u8HDMI_EDID_InfoCount = BOARD_HDMI_EDID_InfoCount;
    memcpy(m_HdmiEdidInfoSet.pstHDMIEDIDInfos, &(stHdmiEdidInfo), sizeof(HDMI_EDID_Info_t)*BOARD_HDMI_EDID_InfoCount);

#if (SYSINFO_DEBUG == 1)
    printf("-------------------HDMIEDIDInfoSet Start-------------------\n");
    printf("HdmiEdidInfoSet Size = %u\n", m_HdmiEdidInfoSet.u8HDMI_EDID_InfoCount);
    for(int i = 0; i < m_HdmiEdidInfoSet.u8HDMI_EDID_InfoCount ; i++)
    {
        printf("HdmiEdidInfoSet[%d] = (%s, %s, %s, %u, %u, %u )\n", \
               i, (TRUE==m_HdmiEdidInfoSet.pstHDMIEDIDInfos[i].bEDIDEnabled) ? "True" : "False", \
               (TRUE==m_HdmiEdidInfoSet.pstHDMIEDIDInfos[i].bUseDefaultValue) ? "True" : "False", \
               (TRUE==m_HdmiEdidInfoSet.pstHDMIEDIDInfos[i].bCECEnabled) ? "True" : "False", \
               m_HdmiEdidInfoSet.pstHDMIEDIDInfos[i].u16CECPhyAddr, \
               m_HdmiEdidInfoSet.pstHDMIEDIDInfos[i].u8CECPhyAddrIdxL, \
               m_HdmiEdidInfoSet.pstHDMIEDIDInfos[i].u8CECPhyAddrIdxH);

        for(int j = 0; j < 256; j++)
        {
            printf("0x%02x, ", m_HdmiEdidInfoSet.pstHDMIEDIDInfos[i].edid[j]);
            if((j + 1) % 16 == 0)
                printf("\n");
        }
        printf("\n");
    }
    printf("-------------------HDMIEDIDInfoSet End---------------------\n\n");
#endif
#endif
    return TRUE;
}

MAPI_BOOL SystemInfo::SetHDMITxAnalogInfo(void)
{
#if (STB_ENABLE == 1)
    const MAPI_U8 u8Size = sizeof(astHdmiAnalogCfg)/sizeof(astHdmiAnalogCfg[0]);

    HDMITx_Analog_Info_t* pHdmiTxAnalogTbl = new (std::nothrow) HDMITx_Analog_Info_t[u8Size];
    ASSERT(pHdmiTxAnalogTbl);
    for (int i=0; i<u8Size; i++)
    {
        pHdmiTxAnalogTbl[i].HDMITx_Attr = (char*)&(astHdmiAnalogCfg[i]);  // save each address of Hdmitx analog cfg
    }

    if(m_pHdmiTxAnalogInfo != NULL)
    {
        delete [] m_pHdmiTxAnalogInfo;
        m_pHdmiTxAnalogInfo = NULL;
    }

    m_pHdmiTxAnalogInfo = new (std::nothrow) HDMITx_Analog_Info_t[u8Size]; // add a new space in heap
    ASSERT(m_pHdmiTxAnalogInfo);

    for (int i=0; i<u8Size; i++)
    {
        HDMITx_Analog_Param_t *pSrcCfg = (HDMITx_Analog_Param_t*)(pHdmiTxAnalogTbl[i].HDMITx_Attr);
        ASSERT(pSrcCfg);

        HDMITx_Analog_Param_t *pDstCfg = new (std::nothrow) HDMITx_Analog_Param_t;
        ASSERT(pDstCfg);

        m_pHdmiTxAnalogInfo[i].HDMITx_Attr = (char*)pDstCfg; // record heap address

        memcpy(pDstCfg, pSrcCfg, sizeof(HDMITx_Analog_Param_t));
    }

#if (SYSINFO_DEBUG == 1)
    HDMITx_Analog_Param_t *pInfo = NULL;
    printf("\033[36m--------------------HDMITxAnalogInfo Start------------------\033[m\n");
    pInfo = (HDMITx_Analog_Param_t*)m_pHdmiTxAnalogInfo[0].HDMITx_Attr;
    printf("\033[36mHDMITxAnalogInfo (HD): %x %x %x %x %x %x\033[m\n", pInfo->tm_txcurrent, pInfo->tm_pren2, pInfo->tm_precon,
                pInfo->tm_pren, pInfo->tm_tenpre, pInfo->tm_ten);
    pInfo = (HDMITx_Analog_Param_t*)m_pHdmiTxAnalogInfo[1].HDMITx_Attr;
    printf("\033[36mHDMITxAnalogInfo (Deep HD): %x %x %x %x %x %x\033[m\n", pInfo->tm_txcurrent, pInfo->tm_pren2, pInfo->tm_precon,
                pInfo->tm_pren, pInfo->tm_tenpre, pInfo->tm_ten);
    printf("\033[36m--------------------HDMITxAnalogInfo End------------------\033[m\n");
#endif

    delete [] pHdmiTxAnalogTbl;
    pHdmiTxAnalogTbl = NULL;
#endif
    return TRUE;
}

MAPI_BOOL SystemInfo::SetATVExtDemodInfo(void)
{
    memcpy(&m_ATVExtDemod, &(BOARD_ExtDemodInfo), sizeof(ATVExtDemodInfo_t));

#if (SYSINFO_DEBUG == 1)
    printf("--------------------ATVExtDemodInfo Start------------------\n");
    printf("ATVExtDemodInfo_t : %u, %u\n", m_ATVExtDemod.bATVExtDemod, m_ATVExtDemod.u32ATVExtVideoSrc);
    printf("--------------------ATVExtDemodInfoEnd------------------\n\n");
#endif
    return TRUE;
}


MAPI_BOOL SystemInfo::SetScartInfo(void)
{
    memcpy(&m_ScartInfo, &(BOARD_ScarInfo), sizeof(ScartInfo_t));

#if (SYSINFO_DEBUG == 1)
    printf("--------------------SetScartInfo Start------------------\n");
    printf("SetScartInfo: %u %u, %u, %u, %u\n",
           m_ScartInfo.u32SCART1_FB, m_ScartInfo.u32SCART2_FB , m_ScartInfo.u32SCARTOUT_MODE,
           m_ScartInfo.u32SCART1_Pin8, m_ScartInfo.u32SCART2_Pin8);
    printf("--------------------SetScartInfo------------------\n\n");
#endif

    BOARD_ScarInfo.bSCART_PIPE_DELAY_ENABLE=SCART_RGB_PIPE_DELAY_ENABLE;
    if(BOARD_ScarInfo.bSCART_PIPE_DELAY_ENABLE==FALSE)
    {
        BOARD_ScarInfo.u16SCART_PIPE_DELAY_PAL_BGHI=PIPE_DELAY_PAL_BGHI;
        BOARD_ScarInfo.u16SCART_PIPE_DELAY_NTSC_M=PIPE_DELAY_NTSC_M;
        BOARD_ScarInfo.u16SCART_PIPE_DELAY_SECAM=PIPE_DELAY_SECAM;
        BOARD_ScarInfo.u16SCART_PIPE_DELAY_NTSC_44=PIPE_DELAY_NTSC_44;
        BOARD_ScarInfo.u16SCART_PIPE_DELAY_PAL_M=PIPE_DELAY_PAL_M;
        BOARD_ScarInfo.u16SCART_PIPE_DELAY_PAL_N=PIPE_DELAY_PAL_N;
        BOARD_ScarInfo.u16SCART_PIPE_DELAY_PAL_60=PIPE_DELAY_PAL_60;
    }

    return TRUE;
}

MAPI_BOOL SystemInfo::SetDemuxInfo(void)
{

#if (TWIN_TUNER == 1)
    memcpy(&m_DMXRoute[0], &(BOARD_DMXConf_Table), sizeof(DMXConf_t)*2);
#else
    memcpy(&m_DMXRoute[0], &(BOARD_DMXConf_Table), sizeof(DMXConf_t));
#endif

#if (SYSINFO_DEBUG == 1)
    printf("--------------------DemuxInfo Start------------------\n");
    for(int i = 0; i < MAXROUTECOUNT ; i++)
    {
        printf("Route[%d]:DMX Table \n", i);
        printf("(With CI card )Demux Info (%d, %d, %d, %d)\n", m_DMXRoute[i].m_ParalleDMX.u8DMX_Flow_input, \
               m_DMXRoute[i].m_ParalleDMX.bClkInv, m_DMXRoute[i].m_ParalleDMX.bExtSync, \
               m_DMXRoute[i].m_ParalleDMX.bParallel);
        printf("(Without CI card)Demux Info (%d, %d, %d, %d)\n", m_DMXRoute[i].m_SerialDMX.u8DMX_Flow_input, \
               m_DMXRoute[i].m_SerialDMX.bClkInv, m_DMXRoute[i].m_SerialDMX.bExtSync, \
               m_DMXRoute[i].m_SerialDMX.bParallel);
        printf("\n");
    }
    printf("--------------------DemuxInfo End------------------\n\n");
#endif
    return TRUE;
}

MAPI_BOOL SystemInfo::ParserDLCCurve(MAPI_XC_DLC_init *pBoard_DLC_init, const char *pparameter, E_MAPI_DLC_PURE_INIT_CURVE whichcurve)
{
    ASSERT(MAPI_DLC_HISTOGRAM_LIMIT_CURVE_ARRARY_NUM == DLC_HISTOGRAM_LIMIT_CURVE_ARRARY_NUM);
    const unsigned int curve_arrary_num = (whichcurve == E_MAPI_DLC_INIT_HISTOGRAM_LIMIT_CURVE ? MAPI_DLC_HISTOGRAM_LIMIT_CURVE_ARRARY_NUM : 16);

    if (pparameter==NULL)
    {
        ASSERT(0);
    }

    vector<string*> strArray;
    ParserStringToArray(pparameter, strArray);

    if (strArray.size() > curve_arrary_num)
    {
        printf("WRONG DCL TABLE FORMAT FROM INI FILE\n\n");
        ASSERT(0);
    }

    for (unsigned int dlc_table_index = 0; dlc_table_index < strArray.size(); dlc_table_index++)
    {
        if (strArray[dlc_table_index] == NULL)
        {
            printf("WRONG GAMMA TABLE FORMAT FROM INI FILE\n\n");
            ASSERT(0);
        }

        int temp_value = 0;
        int filledResult = sscanf(strArray[dlc_table_index]->c_str(), "%x", &temp_value);

        if (filledResult <= 0)
        {
            printf("WRONG GAMMA TABLE FORMAT FROM INI FILE\n\n");
            ASSERT(0);
        }

        if ((temp_value < 0x00) || (temp_value > 0xFF))
        {
            printf("WRONG GAMMA TABLE FORMAT FROM INI FILE\n\n");
            ASSERT(0);
        }

        if(whichcurve==E_MAPI_DLC_INIT_LUMA_CURVE)
        {
            pBoard_DLC_init->DLC_MFinit_Ex.ucLumaCurve[dlc_table_index] = static_cast<MAPI_U8>(temp_value);
        }
        else if(whichcurve==E_MAPI_DLC_INIT_LUMA_CURVE2_A)
        {
            pBoard_DLC_init->DLC_MFinit_Ex.ucLumaCurve2_a[dlc_table_index] = static_cast<MAPI_U8>(temp_value);
        }
        else if(whichcurve==E_MAPI_DLC_INIT_LUMA_CURVE2_B)
        {
            pBoard_DLC_init->DLC_MFinit_Ex.ucLumaCurve2_b[dlc_table_index] = static_cast<MAPI_U8>(temp_value);
        }
        else if(whichcurve==E_MAPI_DLC_INIT_HISTOGRAM_LIMIT_CURVE)
        {
            pBoard_DLC_init->DLC_MFinit_Ex.ucDlcHistogramLimitCurve[dlc_table_index] = temp_value;
        }

        delete strArray[dlc_table_index];
    }

    return TRUE;
}

MAPI_BOOL SystemInfo::ParserColorCorrectionMatrix(MAPI_S16 *pS16Matrix, const char *pparameter, MAPI_U8 u8ItemCnt)
{

    if (pparameter==NULL)
    {
        ASSERT(0);
    }

    vector<string*> strArray;
    ParserStringToArray(pparameter, strArray);

    if (strArray.size() > u8ItemCnt)
    {
        printf("WRONG ColorCorrectionMatrix TABLE FORMAT FROM INI FILE\n\n");
        ASSERT(0);
    }

    for (unsigned int index = 0; index < strArray.size(); index++)
    {
        if (strArray[index] == NULL)
        {
            printf("WRONG ColorCorrectionMatrix TABLE FORMAT FROM INI FILE\n\n");
            ASSERT(0);
        }

        int temp_value = 0;
        int filledResult = sscanf(strArray[index]->c_str(), "%x", &temp_value);

        if (filledResult <= 0)
        {
            printf("WRONG ColorCorrectionMatrix TABLE FORMAT FROM INI FILE\n\n");
            ASSERT(0);
        }

        if ((temp_value < -32768) || (temp_value > 32767))
        {
            printf("WRONG ColorCorrectionMatrix TABLE FORMAT FROM INI FILE\n\n");
            ASSERT(0);
        }

        pS16Matrix[index] = static_cast<MAPI_S16>(temp_value);

        delete strArray[index];
    }

    return TRUE;
}

#if (SQL_DB_ENABLE)

char* SystemInfo::GetCustomerDBPath(EN_SYSINFO_CUSTOMER_DBPATH_TABLENAME_TYPE enCustomerDBPath)
{
    char* SQL_DB_Path = NULL;
    switch(enCustomerDBPath)
    {
        case E_CUSTOMER_3D_VIDEO_ROUTER_PATH:
            SQL_DB_Path = m_p3DVideoRouterPath;
            break;

        case E_CUSTOMER_3DTo2D_VIDEO_ROUTER_PATH:
            SQL_DB_Path = m_p3DTo2DVideoRouterPath;
            break;

        case E_CUSTOMER_DISPLAY_MODE_ROUTER_PATH:
            SQL_DB_Path = m_pDisplayModeRouterPath;
            break;

        case E_CUSTOMER_4K2K_3D_VIDEO_ROUTER_PATH:
            SQL_DB_Path = m_p4K2K3DVideoRouterPath;
            break;

        case E_CUSTOMER_4K2K_60HZ_3D_VIDEO_ROUTER_PATH:
            SQL_DB_Path = m_p4K2K60Hz3DVideoRouterPath;
            break;

        case E_CUSTOMER_FACTORY_ADC_ADJUST_PATH:
            SQL_DB_Path = m_pFactoryADCAdjustPath;
            break;

        case E_CUSTOMER_FACTORY_COLOR_TEMP_PATH:
            SQL_DB_Path = m_pFactoryColorTempPath;
            break;

        case E_CUSTOMER_FACTORY_COLOR_TEMP_EX_PATH:
            SQL_DB_Path = m_pFactoryColorTempExPath;
            break;
        case E_CUSTOMER_NON_LINEAR_ADJUST_EX_PATH:
            SQL_DB_Path = m_pNonLinearAdjustPath;
            break;
        case E_CUSTOMER_NON_LINEAR_ADJUST_3D_EX_PATH:
            SQL_DB_Path = m_pNonLinearAdjust3DPath;
            break;
        case E_CUSTOMER_FACTORY_COLOR_TEMP_EX_3D_PATH:
            SQL_DB_Path = m_pFactoryColorTempEx3DPath;
            break;
        default:
            break;
    }

    if (SQL_DB_Path == NULL)
    {
        ASSERT(0);
    }

    return SQL_DB_Path;
}

char* SystemInfo::GetCustomerDBTableName(EN_SYSINFO_CUSTOMER_DBPATH_TABLENAME_TYPE enCustomerDBTablename)
{
    char* SQL_DB_TableName = NULL;
    switch(enCustomerDBTablename)
    {
        case E_CUSTOMER_3D_VIDEO_ROUTER_TABLENAME:
            SQL_DB_TableName = m_p3DVideoRouterTableName;
            break;
        case E_CUSTOMER_3DTo2D_VIDEO_ROUTER_TABLENAME:
            SQL_DB_TableName = m_p3DTo2DVideoRouterTableName;
            break;
        case E_CUSTOMER_DISPLAY_MODE_ROUTER_TABLENAME:
            SQL_DB_TableName = m_pDisplayModeRouterTableName;
            break;
        case E_CUSTOMER_4K2K_3D_VIDEO_ROUTER_TABLENAME:
            SQL_DB_TableName = m_p4K2K3DVideoRouterTableName;
            break;
        case E_CUSTOMER_4K2K_60HZ_3D_VIDEO_ROUTER_TABLENAME:
            SQL_DB_TableName = m_p4K2K60Hz3DVideoRouterTableName;
            break;
        case E_CUSTOMER_FACTORY_ADC_ADJUST_TABLENAME:
            SQL_DB_TableName = m_pFactoryADCAdjustTableName;
            break;
        case E_CUSTOMER_FACTORY_COLOR_TEMP_TABLENAME:
            SQL_DB_TableName = m_pFactoryColorTempTableName;
            break;
        case E_CUSTOMER_FACTORY_COLOR_TEMP_EX_TABLENAME:
            SQL_DB_TableName = m_pFactoryColorTempExTableName;
            break;
        case E_CUSTOMER_NON_LINEAR_ADJUST_EX_TABLENAME:
            SQL_DB_TableName = m_pNonLinearAdjustTableName;
            break;
        case E_CUSTOMER_NON_LINEAR_ADJUST_3D_EX_TABLENAME:
            SQL_DB_TableName = m_pNonLinearAdjust3DTableName;
            break;
        case E_CUSTOMER_FACTORY_COLOR_TEMP_EX_3D_TABLENAME:
            SQL_DB_TableName = m_pFactoryColorTempEx3DTableName;
            break;
        default:
            break;
    }

    if (SQL_DB_TableName == NULL)
    {
        ASSERT(0);
    }

    return SQL_DB_TableName;
}

MAPI_BOOL SystemInfo::SetSQLDBdata(void)
{
    char *pTmp=NULL;

    if(true==m_bIsSqlDbSet)
    {
        return TRUE;
    }
    pTmp = iniparser_getstr(m_pDBIni, "DBPath_1:SQL_DB_3DVideoRouterPath");
    if(NULL!=pTmp)
    {
        m_p3DVideoRouterPath=(char*)malloc(strlen(pTmp)+1);
        memset((void*)m_p3DVideoRouterPath, 0 ,strlen(pTmp)+1);
        memcpy((void*)m_p3DVideoRouterPath, (void*)pTmp, strlen(pTmp));
    }

    pTmp=NULL;
    pTmp = iniparser_getstr(m_pDBIni, "DBPath_1:SQL_DB_3DVideoRouterTableName");
    if(NULL!=pTmp)
    {
        m_p3DVideoRouterTableName=(char*)malloc(strlen(pTmp)+1);
        memset((void*)m_p3DVideoRouterTableName, 0 ,strlen(pTmp)+1);
        memcpy((void*)m_p3DVideoRouterTableName, (void*)pTmp, strlen(pTmp));
    }

    pTmp=NULL;
    pTmp = iniparser_getstr(m_pDBIni, "DBPath_1:SQL_DB_3DTo2DVideoRouterPath");
    if(NULL!=pTmp)
    {
        m_p3DTo2DVideoRouterPath=(char*)malloc(strlen(pTmp)+1);
        memset((void*)m_p3DTo2DVideoRouterPath, 0 ,strlen(pTmp)+1);
        memcpy((void*)m_p3DTo2DVideoRouterPath, (void*)pTmp, strlen(pTmp));
    }

    pTmp=NULL;
    pTmp = iniparser_getstr(m_pDBIni, "DBPath_1:SQL_DB_3DTo2DVideoRouterTableName");
    if(NULL!=pTmp)
    {
        m_p3DTo2DVideoRouterTableName=(char*)malloc(strlen(pTmp)+1);
        memset((void*)m_p3DTo2DVideoRouterTableName, 0 ,strlen(pTmp)+1);
        memcpy((void*)m_p3DTo2DVideoRouterTableName, (void*)pTmp, strlen(pTmp));
    }

    pTmp=NULL;
    pTmp = iniparser_getstr(m_pDBIni, "DBPath_1:SQL_DB_4K2K3DVideoRouterPath");
    if(NULL!=pTmp)
    {
        m_p4K2K3DVideoRouterPath=(char*)malloc(strlen(pTmp)+1);
        memset((void*)m_p4K2K3DVideoRouterPath, 0 ,strlen(pTmp)+1);
        memcpy((void*)m_p4K2K3DVideoRouterPath, (void*)pTmp, strlen(pTmp));
    }

    pTmp=NULL;
    pTmp = iniparser_getstr(m_pDBIni, "DBPath_1:SQL_DB_4K2K3DVideoRouterTableName");
    if(NULL!=pTmp)
    {
        m_p4K2K3DVideoRouterTableName=(char*)malloc(strlen(pTmp)+1);
        memset((void*)m_p4K2K3DVideoRouterTableName, 0 ,strlen(pTmp)+1);
        memcpy((void*)m_p4K2K3DVideoRouterTableName, (void*)pTmp, strlen(pTmp));
    }

    pTmp=NULL;
    pTmp = iniparser_getstr(m_pDBIni, "DBPath_1:SQL_DB_4K2K60Hz3DVideoRouterPath");
    if(NULL!=pTmp)
    {
        m_p4K2K60Hz3DVideoRouterPath=(char*)malloc(strlen(pTmp)+1);
        memset((void*)m_p4K2K60Hz3DVideoRouterPath, 0 ,strlen(pTmp)+1);
        memcpy((void*)m_p4K2K60Hz3DVideoRouterPath, (void*)pTmp, strlen(pTmp));
    }

    pTmp=NULL;
    pTmp = iniparser_getstr(m_pDBIni, "DBPath_1:SQL_DB_4K2K60Hz3DVideoRouterTableName");
    if(NULL!=pTmp)
    {
        m_p4K2K60Hz3DVideoRouterTableName=(char*)malloc(strlen(pTmp)+1);
        memset((void*)m_p4K2K60Hz3DVideoRouterTableName, 0 ,strlen(pTmp)+1);
        memcpy((void*)m_p4K2K60Hz3DVideoRouterTableName, (void*)pTmp, strlen(pTmp));
    }

    pTmp=NULL;
    pTmp = iniparser_getstr(m_pDBIni, "DBPath_1:SQL_DB_DisplayModeRouterPath");
    if(NULL!=pTmp)
    {
        m_pDisplayModeRouterPath=(char*)malloc(strlen(pTmp)+1);
        memset((void*)m_pDisplayModeRouterPath, 0 ,strlen(pTmp)+1);
        memcpy((void*)m_pDisplayModeRouterPath, (void*)pTmp, strlen(pTmp));
    }

    pTmp=NULL;
    pTmp = iniparser_getstr(m_pDBIni, "DBPath_1:SQL_DB_DisplayModeRouterTableName");
    if(NULL!=pTmp)
    {
        m_pDisplayModeRouterTableName=(char*)malloc(strlen(pTmp)+1);
        memset((void*)m_pDisplayModeRouterTableName, 0 ,strlen(pTmp)+1);
        memcpy((void*)m_pDisplayModeRouterTableName, (void*)pTmp, strlen(pTmp));
    }

    pTmp=NULL;
    pTmp = iniparser_getstr(m_pDBIni, "DBPath_1:SQL_DB_FactoryADCAdjustPath");
    if(NULL!=pTmp)
    {
        m_pFactoryADCAdjustPath=(char*)malloc(strlen(pTmp)+1);
        memset((void*)m_pFactoryADCAdjustPath, 0 ,strlen(pTmp)+1);
        memcpy((void*)m_pFactoryADCAdjustPath, (void*)pTmp, strlen(pTmp));
    }

    pTmp=NULL;
    pTmp = iniparser_getstr(m_pDBIni, "DBPath_1:SQL_DB_FactoryADCAdjustTableName");
    if(NULL!=pTmp)
    {
        m_pFactoryADCAdjustTableName=(char*)malloc(strlen(pTmp)+1);
        memset((void*)m_pFactoryADCAdjustTableName, 0 ,strlen(pTmp)+1);
        memcpy((void*)m_pFactoryADCAdjustTableName, (void*)pTmp, strlen(pTmp));
    }

    pTmp=NULL;
    pTmp = iniparser_getstr(m_pDBIni, "DBPath_1:SQL_DB_FactoryColorTempPath");
    if(NULL!=pTmp)
    {
        m_pFactoryColorTempPath=(char*)malloc(strlen(pTmp)+1);
        memset((void*)m_pFactoryColorTempPath, 0 ,strlen(pTmp)+1);
        memcpy((void*)m_pFactoryColorTempPath, (void*)pTmp, strlen(pTmp));
    }

    pTmp=NULL;
    pTmp = iniparser_getstr(m_pDBIni, "DBPath_1:SQL_DB_FactoryColorTempTableName");
    if(NULL!=pTmp)
    {
        m_pFactoryColorTempTableName=(char*)malloc(strlen(pTmp)+1);
        memset((void*)m_pFactoryColorTempTableName, 0 ,strlen(pTmp)+1);
        memcpy((void*)m_pFactoryColorTempTableName, (void*)pTmp, strlen(pTmp));
    }

    pTmp=NULL;
    pTmp = iniparser_getstr(m_pDBIni, "DBPath_1:SQL_DB_FactoryColorTempExPath");
    if(NULL!=pTmp)
    {
        m_pFactoryColorTempExPath=(char*)malloc(strlen(pTmp)+1);
        memset((void*)m_pFactoryColorTempExPath, 0 ,strlen(pTmp)+1);
        memcpy((void*)m_pFactoryColorTempExPath, (void*)pTmp, strlen(pTmp));
    }

    pTmp=NULL;
    pTmp = iniparser_getstr(m_pDBIni, "DBPath_1:SQL_DB_FactoryColorTempExTableName");
    if(NULL!=pTmp)
    {
        m_pFactoryColorTempExTableName=(char*)malloc(strlen(pTmp)+1);
        memset((void*)m_pFactoryColorTempExTableName, 0 ,strlen(pTmp)+1);
        memcpy((void*)m_pFactoryColorTempExTableName, (void*)pTmp, strlen(pTmp));
    }

    pTmp=NULL;
    pTmp = iniparser_getstr(m_pDBIni, "DBPath_1:SQL_DB_NonLinearAdjustPath");
    if(NULL!=pTmp)
    {
        m_pNonLinearAdjustPath=(char*)malloc(strlen(pTmp)+1);
        memset((void*)m_pNonLinearAdjustPath, 0 ,strlen(pTmp)+1);
        memcpy((void*)m_pNonLinearAdjustPath, (void*)pTmp, strlen(pTmp));
    }

    pTmp=NULL;
    pTmp = iniparser_getstr(m_pDBIni, "DBPath_1:SQL_DB_NonLinearAdjustTableName");
    if(NULL!=pTmp)
    {
        m_pNonLinearAdjustTableName=(char*)malloc(strlen(pTmp)+1);
        memset((void*)m_pNonLinearAdjustTableName, 0 ,strlen(pTmp)+1);
        memcpy((void*)m_pNonLinearAdjustTableName, (void*)pTmp, strlen(pTmp));
    }

    pTmp=NULL;
    pTmp = iniparser_getstr(m_pDBIni, "DBPath_1:SQL_DB_NonLinearAdjust3DPath");
    if(NULL!=pTmp)
    {
        m_pNonLinearAdjust3DPath=(char*)malloc(strlen(pTmp)+1);
        memset((void*)m_pNonLinearAdjust3DPath, 0 ,strlen(pTmp)+1);
        memcpy((void*)m_pNonLinearAdjust3DPath, (void*)pTmp, strlen(pTmp));
    }

    pTmp=NULL;
    pTmp = iniparser_getstr(m_pDBIni, "DBPath_1:SQL_DB_NonLinearAdjust3DTableName");
    if(NULL!=pTmp)
    {
        m_pNonLinearAdjust3DTableName=(char*)malloc(strlen(pTmp)+1);
        memset((void*)m_pNonLinearAdjust3DTableName, 0 ,strlen(pTmp)+1);
        memcpy((void*)m_pNonLinearAdjust3DTableName, (void*)pTmp, strlen(pTmp));
    }

    pTmp=NULL;
    pTmp = iniparser_getstr(m_pDBIni, "DBPath_1:SQL_DB_FactoryColorTempEx3DPath");
    if(NULL!=pTmp)
    {
        m_pFactoryColorTempEx3DPath=(char*)malloc(strlen(pTmp)+1);
        memset((void*)m_pFactoryColorTempEx3DPath, 0 ,strlen(pTmp)+1);
        memcpy((void*)m_pFactoryColorTempEx3DPath, (void*)pTmp, strlen(pTmp));
    }

    pTmp=NULL;
    pTmp = iniparser_getstr(m_pDBIni, "DBPath_1:SQL_DB_FactoryColorTempEx3DTableName");
    if(NULL!=pTmp)
    {
        m_pFactoryColorTempEx3DTableName=(char*)malloc(strlen(pTmp)+1);
        memset((void*)m_pFactoryColorTempEx3DTableName, 0 ,strlen(pTmp)+1);
        memcpy((void*)m_pFactoryColorTempEx3DTableName, (void*)pTmp, strlen(pTmp));
    }
    return TRUE;
}
#endif


MAPI_BOOL SystemInfo::SetDLCInfo(void)
{
    MAPI_XC_DLC_init *Board_DLC_init[DLC_TABLE_COUNT_MAX] = {NULL};
    const char *pparameter = NULL;

    const MAPI_U8 u8bufSize = 50; // number of DLC.ini member name length
    char DLCTableSectionItemName[u8bufSize];
    MAPI_U8 u8Cnt;

    ASSERT(m_pDCLIni != NULL);
    for (u8Cnt = 0; u8Cnt < DLC_TABLE_COUNT_MAX; u8Cnt++)
    {
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:tLumaCurve", u8Cnt);
        pparameter = iniparser_getstr(m_pDCLIni, DLCTableSectionItemName);
        if (pparameter == NULL)
        {
            break;
        }
        Board_DLC_init[u8Cnt] = (MAPI_XC_DLC_init *)malloc(sizeof(MAPI_XC_DLC_init));
        ASSERT(Board_DLC_init[u8Cnt] != NULL);
        ParserDLCCurve(Board_DLC_init[u8Cnt], pparameter, E_MAPI_DLC_INIT_LUMA_CURVE);

        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:tLumaCurve2_a", u8Cnt);
        pparameter = iniparser_getstr(m_pDCLIni, DLCTableSectionItemName);
        ParserDLCCurve(Board_DLC_init[u8Cnt], pparameter, E_MAPI_DLC_INIT_LUMA_CURVE2_A);

        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:tLumaCurve2_b", u8Cnt);
        pparameter = iniparser_getstr(m_pDCLIni, DLCTableSectionItemName);
        ParserDLCCurve(Board_DLC_init[u8Cnt], pparameter, E_MAPI_DLC_INIT_LUMA_CURVE2_B);

        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:tDlcHistogramLimitCurve", u8Cnt);
        pparameter = iniparser_getstr(m_pDCLIni, DLCTableSectionItemName);
        ParserDLCCurve(Board_DLC_init[u8Cnt], pparameter, E_MAPI_DLC_INIT_HISTOGRAM_LIMIT_CURVE);

        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.u32DLC_MFinit_Ex_Version= DLC_MFINIT_EX_VERSION;

        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.u8_L_L_U", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.u8_L_L_U               = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,3);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.u8_L_L_D", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.u8_L_L_D               = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,0);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.u8_L_H_U", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.u8_L_H_U               = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,3);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.u8_L_H_D", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.u8_L_H_D               = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,3);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.u8_S_L_U", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.u8_S_L_U               = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,128);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.u8_S_L_D", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.u8_S_L_D               = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,128);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.u8_S_H_U", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.u8_S_H_U               = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,128);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.u8_S_H_D", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.u8_S_H_D               = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,128);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucDlcPureImageMode", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucDlcPureImageMode     = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,2);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucDlcLevelLimit", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucDlcLevelLimit        = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,0);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucDlcAvgDelta", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucDlcAvgDelta          = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,15);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucDlcAvgDeltaStill", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucDlcAvgDeltaStill     = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,0);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucDlcFastAlphaBlending", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucDlcFastAlphaBlending = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,31);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucDlcYAvgThresholdL", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucDlcYAvgThresholdL    = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,5);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucDlcYAvgThresholdH", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucDlcYAvgThresholdH    = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,200);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucDlcBLEPoint", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucDlcBLEPoint          = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,48);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucDlcWLEPoint", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucDlcWLEPoint          = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,48);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.bEnableBLE", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.bEnableBLE             = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,0);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.bEnableWLE", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.bEnableWLE             = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,0);

        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucDlcYAvgThresholdM", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucDlcYAvgThresholdM    = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,70);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucDlcCurveMode", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucDlcCurveMode         = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,2);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucDlcCurveModeMixAlpha", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucDlcCurveModeMixAlpha = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,80);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucDlcAlgorithmMode", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucDlcAlgorithmMode     = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,0);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucDlcSepPointH", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucDlcSepPointH         = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,188);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucDlcSepPointL", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucDlcSepPointL         = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,80);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.uwDlcBleStartPointTH", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.uwDlcBleStartPointTH   = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,640);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.uwDlcBleEndPointTH", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.uwDlcBleEndPointTH     = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,256);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucDlcCurveDiff_L_TH", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucDlcCurveDiff_L_TH    = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,56);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucDlcCurveDiff_H_TH", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucDlcCurveDiff_H_TH    = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,148);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.uwDlcBLESlopPoint_1", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.uwDlcBLESlopPoint_1    = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,1028);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.uwDlcBLESlopPoint_2", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.uwDlcBLESlopPoint_2    = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,1168);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.uwDlcBLESlopPoint_3", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.uwDlcBLESlopPoint_3    = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,1260);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.uwDlcBLESlopPoint_4", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.uwDlcBLESlopPoint_4    = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,1370);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.uwDlcBLESlopPoint_5", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.uwDlcBLESlopPoint_5    = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,1440);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.uwDlcDark_BLE_Slop_Min", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.uwDlcDark_BLE_Slop_Min = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,1200);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucDlcCurveDiffCoringTH", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucDlcCurveDiffCoringTH = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,2);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucDlcAlphaBlendingMin", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucDlcAlphaBlendingMin  = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,1);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucDlcAlphaBlendingMax", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucDlcAlphaBlendingMax  = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,128);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucDlcFlicker_alpha", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucDlcFlicker_alpha     = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,96);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucDlcYAVG_L_TH", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucDlcYAVG_L_TH         = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,56);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucDlcYAVG_H_TH", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucDlcYAVG_H_TH         = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,136);

        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucDlcDiffBase_L", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucDlcDiffBase_L        = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,4);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucDlcDiffBase_M", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucDlcDiffBase_M        = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,14);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucDlcDiffBase_H", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucDlcDiffBase_H        = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,20);

        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.bCGCCGainCtrl", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.bCGCCGainCtrl          = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,0);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucCGCCGain_offset", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucCGCCGain_offset      = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,0);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucCGCYCslope", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucCGCYCslope           = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,0);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucCGCChroma_GainLimitH", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucCGCChroma_GainLimitH = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,0);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucCGCChroma_GainLimitL", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucCGCChroma_GainLimitL = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,1);
        snprintf(DLCTableSectionItemName, u8bufSize, "DLC_%d:g_DlcParameters.ucCGCYth", u8Cnt);
        Board_DLC_init[u8Cnt]->DLC_MFinit_Ex.ucCGCYth               = iniparser_getint(m_pDCLIni, DLCTableSectionItemName,8);
    }

    m_u8DLCTableCount= u8Cnt;

    if(m_psXC_DLC_InitData == NULL)
    {
        m_psXC_DLC_InitData = new (std::nothrow) MAPI_XC_DLC_init[m_u8DLCTableCount];
        ASSERT(m_psXC_DLC_InitData != NULL);
        memset(m_psXC_DLC_InitData, 0, sizeof(MAPI_XC_DLC_init) * m_u8DLCTableCount);

        for (MAPI_U8 i = 0; i < m_u8DLCTableCount; i++)
        {
            memcpy(&m_psXC_DLC_InitData[i], Board_DLC_init[i], sizeof(MAPI_XC_DLC_init));
        }
    }

    for (MAPI_U8 i = 0; i < u8Cnt; i++)
    {
        free(Board_DLC_init[i]);
        Board_DLC_init[i] = NULL;
    }

    return TRUE;
}

MAPI_XC_DLC_init* SystemInfo::GetDLCInfo(unsigned char index)
{
    return &m_psXC_DLC_InitData[index];
}

unsigned char SystemInfo::GetDLCTableCount(void)
{
    return m_u8DLCTableCount;
}

unsigned char SystemInfo::SetDLCCurveInfo(unsigned char index, unsigned short u16CurveHStart, unsigned short u16CurveHEnd, unsigned short u16CurveVStart, unsigned short u16CurveVEnd)
{
    m_psXC_DLC_InitData[index].u16CurveHStart = u16CurveHStart;
    m_psXC_DLC_InitData[index].u16CurveHEnd = u16CurveHEnd;
    m_psXC_DLC_InitData[index].u16CurveVStart = u16CurveVStart;
    m_psXC_DLC_InitData[index].u16CurveVEnd = u16CurveVEnd;

    return TRUE;
}

MAPI_BOOL SystemInfo::SetColorMatrix(void)
{
    const char *pparameter=NULL;
    MAPI_S16 s16tmpMatrix[32];

    ASSERT(m_pMatrixIni);

    memset(s16tmpMatrix, 0, sizeof(s16tmpMatrix));

    m_pColorMatrix = new (std::nothrow) MAPI_COLOR_MATRIX;
    ASSERT(m_pColorMatrix);

    pparameter = iniparser_getstr(m_pMatrixIni, "ColorSpace:tSDTVYuv2rgb");

    if (pparameter != NULL)
    {
        SYSTEM_INFO_DBG("Parsing tSDTVYuv2rgb\n");
        ParserColorCorrectionMatrix(s16tmpMatrix, pparameter, 9);
        memcpy(m_pColorMatrix->s16SDYuv2Rgb, s16tmpMatrix, sizeof(m_pColorMatrix->s16SDYuv2Rgb));
    }
    else
    {
        ASSERT(0);
    }

    pparameter = iniparser_getstr(m_pMatrixIni, "ColorSpace:tHDTVYuv2rgb");
    if (pparameter != NULL)
    {
        SYSTEM_INFO_DBG("Parsing tSDTVYuv2rgb\n");
        ParserColorCorrectionMatrix(s16tmpMatrix, pparameter, 9);
        memcpy(m_pColorMatrix->s16HDYuv2Rgb, s16tmpMatrix, sizeof(m_pColorMatrix->s16HDYuv2Rgb));
    }
    else
    {
        ASSERT(0);
    }

    pparameter = iniparser_getstr(m_pMatrixIni, "ColorCorrection:tDefaultColorCorrectionMatrix");
    if (pparameter != NULL)
    {
        ParserColorCorrectionMatrix(s16tmpMatrix, pparameter, 32);
        memcpy(m_pColorMatrix->s16Default, s16tmpMatrix, sizeof(s16tmpMatrix));
    }
    else
    {
        ASSERT(0);
    }

    pparameter = iniparser_getstr(m_pMatrixIni, "ColorCorrection:tHDTVColorCorrectionMatrix");
    if (pparameter != NULL)
    {
        ParserColorCorrectionMatrix(s16tmpMatrix, pparameter, 32);
        memcpy(m_pColorMatrix->s16HDTV, s16tmpMatrix, sizeof(s16tmpMatrix));
    }
    else
    {
        memcpy(m_pColorMatrix->s16HDTV, m_pColorMatrix->s16Default, sizeof(m_pColorMatrix->s16Default));
    }

    pparameter = iniparser_getstr(m_pMatrixIni, "ColorCorrection:tSDTVColorCorrectionMatrix");
    if (pparameter != NULL)
    {
        ParserColorCorrectionMatrix(s16tmpMatrix, pparameter, 32);
        memcpy(m_pColorMatrix->s16SDTV, s16tmpMatrix, sizeof(s16tmpMatrix));
    }
    else
    {
        memcpy(m_pColorMatrix->s16SDTV, m_pColorMatrix->s16Default, sizeof(m_pColorMatrix->s16Default));
    }

    pparameter = iniparser_getstr(m_pMatrixIni, "ColorCorrection:tATVColorCorrectionMatrix");
    if (pparameter != NULL)
    {
        ParserColorCorrectionMatrix(s16tmpMatrix, pparameter, 32);
        memcpy(m_pColorMatrix->s16ATV, s16tmpMatrix, sizeof(s16tmpMatrix));
    }
    else
    {
        memcpy(m_pColorMatrix->s16ATV, m_pColorMatrix->s16Default, sizeof(m_pColorMatrix->s16Default));
    }

    pparameter = iniparser_getstr(m_pMatrixIni, "ColorCorrection:tSDYPbPrColorCorrectionMatrix");
    if (pparameter != NULL)
    {
        ParserColorCorrectionMatrix(s16tmpMatrix, pparameter, 32);
        memcpy(m_pColorMatrix->s16SdYPbPr, s16tmpMatrix, sizeof(s16tmpMatrix));
    }
    else
    {
        memcpy(m_pColorMatrix->s16SdYPbPr, m_pColorMatrix->s16Default, sizeof(m_pColorMatrix->s16Default));
    }

    pparameter = iniparser_getstr(m_pMatrixIni, "ColorCorrection:tHDYPbPrColorCorrectionMatrix");
    if (pparameter != NULL)
    {
        ParserColorCorrectionMatrix(s16tmpMatrix, pparameter, 32);
        memcpy(m_pColorMatrix->s16HdYPbPr, s16tmpMatrix, sizeof(s16tmpMatrix));
    }
    else
    {
        memcpy(m_pColorMatrix->s16HdYPbPr, m_pColorMatrix->s16Default, sizeof(m_pColorMatrix->s16Default));
    }

    pparameter = iniparser_getstr(m_pMatrixIni, "ColorCorrection:tHDHdmiColorCorrectionMatrix");
    if (pparameter != NULL)
    {
        ParserColorCorrectionMatrix(s16tmpMatrix, pparameter, 32);
        memcpy(m_pColorMatrix->s16HdHdmi, s16tmpMatrix, sizeof(s16tmpMatrix));
    }
    else
    {
        memcpy(m_pColorMatrix->s16HdHdmi, m_pColorMatrix->s16Default, sizeof(m_pColorMatrix->s16Default));
    }

    pparameter = iniparser_getstr(m_pMatrixIni, "ColorCorrection:tSDHdmiColorCorrectionMatrix");
    if (pparameter != NULL)
    {
        ParserColorCorrectionMatrix(s16tmpMatrix, pparameter, 32);
        memcpy(m_pColorMatrix->s16SdHdmi, s16tmpMatrix, sizeof(s16tmpMatrix));
    }
    else
    {
        memcpy(m_pColorMatrix->s16SdHdmi, m_pColorMatrix->s16Default, sizeof(m_pColorMatrix->s16Default));
    }

    pparameter = iniparser_getstr(m_pMatrixIni, "ColorCorrection:tAVColorCorrectionMatrix");
    if (pparameter != NULL)
    {
        ParserColorCorrectionMatrix(s16tmpMatrix, pparameter, 32);
        memcpy(m_pColorMatrix->s16AV, s16tmpMatrix, sizeof(s16tmpMatrix));
    }
    else
    {
        memcpy(m_pColorMatrix->s16AV, m_pColorMatrix->s16Default, sizeof(m_pColorMatrix->s16Default));
    }

    pparameter = iniparser_getstr(m_pMatrixIni, "ColorCorrection:tSVColorCorrectionMatrix");
    if (pparameter != NULL)
    {
        ParserColorCorrectionMatrix(s16tmpMatrix, pparameter, 32);
        memcpy(m_pColorMatrix->s16SV, s16tmpMatrix, sizeof(s16tmpMatrix));
    }
    else
    {
        memcpy(m_pColorMatrix->s16SV, m_pColorMatrix->s16Default, sizeof(m_pColorMatrix->s16Default));
    }

    pparameter = iniparser_getstr(m_pMatrixIni, "ColorCorrection:tVgaColorCorrectionMatrix");
    if (pparameter != NULL)
    {
        ParserColorCorrectionMatrix(s16tmpMatrix, pparameter, 32);
        memcpy(m_pColorMatrix->s16Vga, s16tmpMatrix, sizeof(s16tmpMatrix));
    }
    else
    {
        memcpy(m_pColorMatrix->s16Vga, m_pColorMatrix->s16Default, sizeof(m_pColorMatrix->s16Default));
    }

    return TRUE;
}

MAPI_COLOR_MATRIX* SystemInfo::GetColorMatrix()
{
    return m_pColorMatrix;
}

std::string SystemInfo::GetTunerSelect(MAPI_U8 u8TunerNo)
{
    // if tuner select no read from ini file is wrong data, set 0 as default value.
    return m_sTunerSelect[u8TunerNo];

}

MAPI_S16 SystemInfo::GetChinaDescramblerBoxDelayOffset(void)
{
    return ChinaDescramblerBoxDelayOffset;
}

SawArchitecture SystemInfo::GetSAWType(void)
{
#if (STB_ENABLE == 0)
        return enSawType;
#else
        return SAW_NUMS;
#endif
}

SarChannel SystemInfo::GetSARChannel(void)
{
#if (STB_ENABLE == 0)
        return enSarChannel;
#else
        return E_SAR_NC;
#endif
}

MAPI_U8  SystemInfo::GetPQEnableDefault(void)
{
    return  SysIniBlock.PQBinDefault;
}

MAPI_U8 * SystemInfo::GetPQPathName(void)
{
    return  SysIniBlock.PQPathName;
}

MAPI_BOOL SystemInfo::SetPQAutoNRParam(void)
{
    if(m_pCustomerini == NULL)
    {
        SYSTEM_INFO_ERR("ERROR: Load %s error.\n", SysIniBlock.ModelName);
        ASSERT(0);
    }

    MAPI_AUTO_NR_INIT_PARAM stPara;
    stPara.u8DebugLevel =(unsigned char)iniparser_getint(m_pCustomerini, "MISC_PQ_NR_CFG:DEBUG_LEVEL", 0);
    stPara.u16AutoNr_L2M_Thr = (unsigned short)iniparser_getint(m_pCustomerini, "MISC_PQ_NR_CFG:LOW2MID_THR", 0);
    stPara.u16AutoNr_M2L_Thr = (unsigned short)iniparser_getint(m_pCustomerini, "MISC_PQ_NR_CFG:MID2LOW_THR", 0);
    stPara.u16AutoNr_M2H_Thr = (unsigned short)iniparser_getint(m_pCustomerini, "MISC_PQ_NR_CFG:MID2HIGH_THR", 0);
    stPara.u16AutoNr_H2M_Thr = (unsigned short)iniparser_getint(m_pCustomerini, "MISC_PQ_NR_CFG:HIGH2MID_THR", 0);
    memcpy(&m_AutoNrParam, &stPara, sizeof(MAPI_AUTO_NR_INIT_PARAM));
    return TRUE;
}

MAPI_BOOL SystemInfo::SetSAWType(void)
{
#if (STB_ENABLE == 0)
    SawArchitecture SawType;
    SawType = BOARD_SAW_TYPE;

    if(SAW_NUMS>SawType)
    {
        enSawType = SawType;
    }
    else
    {
        ASSERT(0);
    }
#endif

    return  TRUE;
}

MAPI_BOOL SystemInfo::SetSARChannel(void)
{
#if (STB_ENABLE == 0)
    if ((E_SAR_MAX_NUMS>BOARD_SAR_CHANNEL) ||  (E_SAR_NC==BOARD_SAR_CHANNEL))
    {
        enSarChannel = (SarChannel)BOARD_SAR_CHANNEL;
    }
    else
    {
        ASSERT(0);
    }
#endif
    return  TRUE;
}

MAPI_BOOL SystemInfo::SetDemodConfig(void)
{
#if (STB_ENABLE == 0)
    // set DVBT config
    if (sizeof(u8BOARD_DVBT_DSPRegInitExt)>0)
    {
        u8BOARD_DVBT_DSPRegInitExt[2]=(unsigned char)((sizeof(u8BOARD_DVBT_DSPRegInitExt)-4)/4);
        u8BOARD_DVBT_DSPRegInitExt[3]=(unsigned char)(((sizeof(u8BOARD_DVBT_DSPRegInitExt)-4)/4)>>8);
        m_u8BOARD_DVBT_DSPRegInitExt=u8BOARD_DVBT_DSPRegInitExt;
        m_u8BOARD_DMD_DVBT_InitExt=u8BOARD_DMD_DVBT_InitExt;
    }
    else
    {
        m_u8BOARD_DVBT_DSPRegInitExt=NULL;
        m_u8BOARD_DMD_DVBT_InitExt=u8BOARD_DMD_DVBT_InitExt;
    }
    // set DVBC config
    if (sizeof(u8BOARD_DVBC_DSPRegInitExt)>0)
    {
        u8BOARD_DVBC_DSPRegInitExt[2]=(unsigned char)((sizeof(u8BOARD_DVBC_DSPRegInitExt)-4)/4);
        u8BOARD_DVBC_DSPRegInitExt[3]=(unsigned char)(((sizeof(u8BOARD_DVBC_DSPRegInitExt)-4)/4)>>8);
        m_u8BOARD_DVBC_DSPRegInitExt=u8BOARD_DVBC_DSPRegInitExt;
        m_u8BOARD_DMD_DVBC_InitExt=u8BOARD_DMD_DVBC_InitExt;
    }
    else
    {
        m_u8BOARD_DVBC_DSPRegInitExt=NULL;
        m_u8BOARD_DMD_DVBC_InitExt=u8BOARD_DMD_DVBC_InitExt;
    }

#if (ATSC_SYSTEM_ENABLE == 1)
    // set ATSC config
    m_u8BOARD_ATSC_DSPRegInitExt=NULL;
    m_u8BOARD_DMD_ATSC_InitExt=u8BOARD_DMD_ATSC_InitConf;
#endif
#endif
    return TRUE;
}

MAPI_BOOL SystemInfo::SetPanelModPvddInfo(void)
{
    m_PanelModPvddPowerInfo.bEnabled=BOARD_PANEL_MOD_CONFIG_BY_USER_ENABLE;
    m_PanelModPvddPowerInfo.bPanelModPvddPowerType=BOARD_PANEL_MOD_TYPE;
    return  TRUE;
}

#if (ENABLE_BACKEND == 1)
MAPI_BOOL SystemInfo::SetUrsaEnable(void)
{
    m_u8UrsaEnable = SysIniBlock.UrsaEanble;
    return  TRUE;
}

MAPI_BOOL SystemInfo::SetUrsaSelect(void)
{
    m_u8UrsaSelect = SysIniBlock.UrsaSelect;
    return  TRUE;
}

MAPI_BOOL SystemInfo::SetMEMCPanelEnable(void)
{
    m_u8MEMCPanelEnable= SysIniBlock.MEMCPanelEnable;
    return  TRUE;
}

MAPI_BOOL SystemInfo::SetMEMCPanelSelect(void)
{
    m_u8MEMCPanelSelect = SysIniBlock.MEMCPanelSelect;
    return  TRUE;
}
#endif
MAPI_BOOL SystemInfo::updateOSDLpllType(int width, int height, int timing)
{
    MAPI_BOOL bEnable_OSDC = MAPI_FALSE;
    SystemInfo::GetModuleParameter_bool("M_BACKEND:F_BACKEND_ENABLE_OSDC", &bEnable_OSDC);
    if( bEnable_OSDC == MAPI_TRUE )
    {
        SYSTEM_INFO_DBG("\nUpdate OSDC\n");
        SYSTEM_INFO_DBG("\nWidth: %d\n", width);
        SYSTEM_INFO_DBG("\nHeight: %d\n",height);
        SYSTEM_INFO_DBG("\nTiming: %d\n",timing);

#if (STB_ENABLE == 0) //wait for K2 utopia release
        // 1. accounting to W/H/T parameter to change OSDC setting.
        if(IS2K1K(width,height,20) && (getOSDLpllType() != LINK_VBY1_10BIT_2LANE))
        {
            setOSDLpllType(LINK_VBY1_10BIT_2LANE);
        }
        else if((IS4K2K(width,height,20) || IS4K1K(width,height,20)) &&
                (getOSDLpllType() != LINK_VBY1_10BIT_4LANE))
        {
            setOSDLpllType(LINK_VBY1_10BIT_4LANE);
        }else{
            SYSTEM_INFO_ERR("condition is not match. Don't change\n");
            return FALSE;
        }
#endif
        printf("[%s:%d] OSD type change MUST Notify Backend.\n",__FUNCTION__,__LINE__);
        return TRUE;
    }
    return FALSE;
}
MAPI_BOOL SystemInfo::setOSDLpllType(MAPI_U32 type)
{
    MAPI_BOOL bEnable_OSDC = MAPI_FALSE;
    SystemInfo::GetModuleParameter_bool("M_BACKEND:F_BACKEND_ENABLE_OSDC", &bEnable_OSDC);
    if( bEnable_OSDC == MAPI_TRUE )
    {
        SysIniBlock.OsdcLpllType = type;
        return  TRUE;
    }
    return FALSE;
}
MAPI_U32 SystemInfo::getOSDLpllType(void)
{
    MAPI_BOOL bEnable_OSDC = MAPI_FALSE;
    SystemInfo::GetModuleParameter_bool("M_BACKEND:F_BACKEND_ENABLE_OSDC", &bEnable_OSDC);
    if( bEnable_OSDC == MAPI_TRUE )
    {
        return  SysIniBlock.OsdcLpllType;
    }
    return 0;
}
MAPI_BOOL SystemInfo::GetOSDCInfo(MAPI_OSDCType *info)
{
    MAPI_BOOL bEnable_OSDC = MAPI_FALSE;
    SystemInfo::GetModuleParameter_bool("M_BACKEND:F_BACKEND_ENABLE_OSDC", &bEnable_OSDC);
    if( bEnable_OSDC == MAPI_TRUE )
    {
        info->OC_ClK_En                 = SysIniBlock.clk_en;
        info->OC_Mixer_Bypass_En        = SysIniBlock.bypass_en;
        info->OC_Mixer_InvAlpha_En      = SysIniBlock.invAlpha;
        info->OC_Mixer_Hsync_Vfde_Out   = SysIniBlock.hsync_vfde;
        info->OC_Mixer_Hfde_Vfde_Out    = SysIniBlock.hfde_vfde;
        info->OC_Mixer_u16OC_Lpll_type  = SysIniBlock.OsdcLpllType;
        int eUrsaType = E_URSA_NONE;
        SystemInfo::GetInstance()->GetModuleParameter_int("M_URSA:F_URSA_URSA_TYPE", &eUrsaType, 0);
        if (eUrsaType == E_URSA_7)
        {
            info->OC_OutputFormat = 1;
        }
        else
        {
            info->OC_OutputFormat = 0;
        }

        SYSTEM_INFO_DBG("   >> OC_ClK_En                =%d\n",info->OC_ClK_En);
        SYSTEM_INFO_DBG("   >> OC_Mixer_Bypass_En       =%d\n",info->OC_Mixer_Bypass_En);
        SYSTEM_INFO_DBG("   >> OC_Mixer_InvAlpha_En     =%d\n",info->OC_Mixer_InvAlpha_En);
        SYSTEM_INFO_DBG("   >> OC_Mixer_Hsync_Vfde_Out  =%d\n",info->OC_Mixer_Hsync_Vfde_Out);
        SYSTEM_INFO_DBG("   >> OC_Mixer_Hfde_Vfde_Out   =%d\n",info->OC_Mixer_Hfde_Vfde_Out);
        SYSTEM_INFO_DBG("   >> OC_Mixer_u16OC_Lpll_type =%d\n",info->OC_Mixer_u16OC_Lpll_type);

        return  TRUE;
    }
    return FALSE;
}
MAPI_BOOL SystemInfo::LoadOSDCInfo(void)
{
    MAPI_BOOL bEnable_OSDC = MAPI_FALSE;
    SystemInfo::GetModuleParameter_bool("M_BACKEND:F_BACKEND_ENABLE_OSDC", &bEnable_OSDC);
    if( bEnable_OSDC == MAPI_TRUE )
    {
        SysIniBlock.clk_en          = iniparser_getint(m_pCustomerini, "OSDC:OC_ClK_En", 0);
        SysIniBlock.bypass_en       = iniparser_getint(m_pCustomerini, "OSDC:OC_Mixer_Bypass_En", 0);
        SysIniBlock.invAlpha        = iniparser_getint(m_pCustomerini, "OSDC:OC_Mixer_InvAlpha_En", 0);
        SysIniBlock.hsync_vfde      = iniparser_getint(m_pCustomerini, "OSDC:OC_Mixer_Hsync_Vfde_Out", 0);
        SysIniBlock.hfde_vfde       = iniparser_getint(m_pCustomerini, "OSDC:OC_Mixer_Hfde_Vfde_Out", 0);
        SysIniBlock.OsdcLpllType    = iniparser_getint(m_pCustomerini, "OSDC:OC_Mixer_u16OC_Lpll_type", 0);

        SYSTEM_INFO_DBG("   >> OC_ClK_En                =%d\n",SysIniBlock.clk_en);
        SYSTEM_INFO_DBG("   >> OC_Mixer_Bypass_En       =%d\n",SysIniBlock.bypass_en);
        SYSTEM_INFO_DBG("   >> OC_Mixer_InvAlpha_En     =%d\n",SysIniBlock.invAlpha);
        SYSTEM_INFO_DBG("   >> OC_Mixer_Hsync_Vfde_Out  =%d\n",SysIniBlock.hsync_vfde);
        SYSTEM_INFO_DBG("   >> OC_Mixer_Hfde_Vfde_Out   =%d\n",SysIniBlock.hfde_vfde);
        SYSTEM_INFO_DBG("   >> OC_Mixer_u16OC_Lpll_type =%d\n",SysIniBlock.OsdcLpllType );

        return  TRUE;
    }
    return FALSE;
}
MAPI_BOOL SystemInfo::SetTunerSelect(void)
{
    char* sTunerSelect = NULL;
    sTunerSelect = iniparser_getstring(m_pCustomerini, "Tuner:TunerSelectNo1", NULL);
    if(sTunerSelect != NULL)
    {
        m_sTunerSelect.push_back(sTunerSelect);
    }
#if (TWIN_TUNER, 1)
    sTunerSelect = iniparser_getstring(m_pCustomerini, "Tuner:TunerSelectNo2", NULL);
    if(sTunerSelect != NULL)
    {
        m_sTunerSelect.push_back(sTunerSelect);
    }
#endif
    return  TRUE;
}
MAPI_BOOL SystemInfo::SetChinaDescramblerBoxDelayOffset(void)
{
    ChinaDescramblerBoxDelayOffset = iniparser_getint(m_pCustomerini,"Tuner:ChinaDescramblerBoxDelayOffset",0);
    return TRUE;
}
MAPI_BOOL SystemInfo::LoadTunerPWMInfo(TunerPWMInfo* pTunerPWMInfo)
{
    if(m_pCustomerini == NULL)
    {
        SYSTEM_INFO_ERR("ERROR: Load %s error.\n", SysIniBlock.ModelName);
        ASSERT(0);
    }

    pTunerPWMInfo->bPolPWM        = iniparser_getboolean(m_pCustomerini, "Tuner:bPolPWM", FALSE);
    pTunerPWMInfo->u16DivPWM      = iniparser_getint(m_pCustomerini, "Tuner:u16DivPWM", 0);
    pTunerPWMInfo->u16PWMvalue    = iniparser_getint(m_pCustomerini, "Tuner:u16PWMvalue", 0);
    pTunerPWMInfo->u32PeriodPWM   = iniparser_getint(m_pCustomerini, "Tuner:u32PeriodPWM", 0);
    pTunerPWMInfo->u32DutyPWM     = iniparser_getint(m_pCustomerini, "Tuner:u32DutyPWM", 0);

    return TRUE;
}


MAPI_BOOL SystemInfo::SetTunerPWMInfo(void)
{
#ifdef BOARD_TUNER_PWM_PORT
    TunerPWMInfo stTunerPWMInfo;
    memset(&stTunerPWMInfo, 0x00, sizeof(stTunerPWMInfo));
    stTunerPWMInfo.u8PWMPort = BOARD_TUNER_PWM_PORT;
    if(stTunerPWMInfo.u8PWMPort == 0xFF)
    {
        return FALSE;
    }
    else
    {
        LoadTunerPWMInfo(&stTunerPWMInfo);

        m_TunerPWMInfo.bPolPWM        = stTunerPWMInfo.bPolPWM;
        m_TunerPWMInfo.u16DivPWM      = stTunerPWMInfo.u16DivPWM;
        m_TunerPWMInfo.u16PWMvalue    = stTunerPWMInfo.u16PWMvalue;
        m_TunerPWMInfo.u32PeriodPWM   = stTunerPWMInfo.u32PeriodPWM;
        m_TunerPWMInfo.u32DutyPWM     = stTunerPWMInfo.u32DutyPWM;
        m_TunerPWMInfo.u8PWMPort      = stTunerPWMInfo.u8PWMPort;

        SYSTEM_INFO_DBG("   >> m_TunerPWMInfo.u8PWMPort     =%d\n",m_TunerPWMInfo.u8PWMPort);
        SYSTEM_INFO_DBG("   >> m_TunerPWMInfo.bPolPWM       =%d\n",m_TunerPWMInfo.bPolPWM);
        SYSTEM_INFO_DBG("   >> m_TunerPWMInfo.u16DivPWM     =%d\n",m_TunerPWMInfo.u16DivPWM);
        SYSTEM_INFO_DBG("   >> m_TunerPWMInfo.u16PWMvalue   =%d\n",m_TunerPWMInfo.u16PWMvalue);
        SYSTEM_INFO_DBG("   >> m_TunerPWMInfo.u32PeriodPWM  =%d\n",m_TunerPWMInfo.u32PeriodPWM);
        SYSTEM_INFO_DBG("   >> m_TunerPWMInfo.u32DutyPWM    =%d\n",m_TunerPWMInfo.u32DutyPWM);
        return TRUE;
    }
#else
    return FALSE;
#endif
}

MAPI_BOOL SystemInfo::SetAudioAmpSelect(void)
{
    m_u8AudioAmpSelect = SysIniBlock.AudioAmpSelect;
    return  TRUE;
}

MAPI_BOOL SystemInfo::SetVolumeCompensation(void)
{
    MAPI_BOOL bEnableVolumeCom;

    bEnableVolumeCom = iniparser_getboolean(m_pCustomerini, "VolumeCompensation:bEnabled",0);
    SystemInfo::SetVolumeCompensationFlag(bEnableVolumeCom);

    return TRUE;
}

void SystemInfo::SetVolumeCompensationFlag(MAPI_BOOL bEnableVolCom)
{
    m_bEnableVolumeCom = bEnableVolCom;
}

void SystemInfo::SetVolumeCurveCfgBlock(const VolumeCurve_t* const pVolumeCurveBlock)
{
    memcpy(&m_VolumeCurve, pVolumeCurveBlock, sizeof(VolumeCurve_t));
}

#if (STB_ENABLE == 0)
MAPI_BOOL SystemInfo::SetVolumeCurve(void)
{
    VolumeCurve_t Board_VolumeCurveBlock;
    const MAPI_U32 size = 101;

    ASSERT(m_pCustomerini);

    Board_VolumeCurveBlock.bEnabled=iniparser_getboolean(m_pCustomerini, "VolumeCurve:bEnabled",-1);
    iniparser_getU8array(m_pCustomerini, "VolumeCurve:u8Volume_Int[101]", size, Board_VolumeCurveBlock.u8Volume_Int);
    iniparser_getU8array(m_pCustomerini, "VolumeCurve:u8Volume_Fra[101]", size, Board_VolumeCurveBlock.u8Volume_Fra);

    SystemInfo::SetVolumeCurveCfgBlock(&Board_VolumeCurveBlock);

    return  TRUE;
}

MAPI_BOOL SystemInfo::SetPictureModeUseFacCurveFlag(void)
{
    m_bEnablePictureModeUseFacCurve = iniparser_getboolean(m_pPanelini, "picture_mode:bPictureModeUseFacCurveFlag", 0);
    return TRUE;
}

//
// Set five kinds of curve, contrast cuvre, brightness curve, saturation curve, sharpness curve and hue curve
// each kind of curve is only one curve and is NOT depended on input source.
//
MAPI_BOOL SystemInfo::SetPictureModeCurve(void)
{
    const MAPI_U32 size = 101;
    PictureModeCurve_t  Board_PictureModeCurveBlock;

    if (m_pPanelini == NULL)
    {
        ASSERT(0);
    }

    // Contrast
    iniparser_getU8array(m_pPanelini, "picture_mode:contrast_curve[101]", size, Board_PictureModeCurveBlock.u8ContrastCurve);

    // Brightness
    iniparser_getU8array(m_pPanelini, "picture_mode:brightness_curve[101]", size, Board_PictureModeCurveBlock.u8BrightnessCurve);

    // Saturation
    iniparser_getU8array(m_pPanelini, "picture_mode:saturation_curve[101]", size, Board_PictureModeCurveBlock.u8SaturationCurve);

    // Sharpness
    iniparser_getU8array(m_pPanelini, "picture_mode:sharpness_curve[101]", size, Board_PictureModeCurveBlock.u8SharpnessCurve);

     // Hue
    iniparser_getU8array(m_pPanelini, "picture_mode:hue_curve[101]", size, Board_PictureModeCurveBlock.u8HueCurve);

    memcpy(&m_PictureModeCurve, &(Board_PictureModeCurveBlock), sizeof(PictureModeCurve_t));

    return  TRUE;
}

MAPI_BOOL SystemInfo::SetPcModeTimingTable(void)
{
    char sec_key[MAX_BUFFER+1]="";
    MAPI_S32 s32res;
    MAPI_U8 u8cnt;
    const MAPI_U8 u8TotalModeCount = iniparser_getint(m_pPcModeIni, "TotalModeCount:Count", -1);
    PcModeTimingTable_t *pPcModeTimingTable = NULL;

    if (u8TotalModeCount > MAXPCMODETIMINGTABLE)
    {
        SYSTEM_INFO_ERR("ERROR: too many pc mode table error.\n");
        ASSERT(0);
    }
    pPcModeTimingTable = new (std::nothrow) PcModeTimingTable_t[u8TotalModeCount];
    ASSERT(pPcModeTimingTable);
    memset(pPcModeTimingTable, 0, u8TotalModeCount * sizeof(PcModeTimingTable_t));
    SYSTEM_INFO_DBG("\n\n\n");
    for (u8cnt = 0; u8cnt < u8TotalModeCount; u8cnt++) {
        snprintf(sec_key, MAX_BUFFER, "%d:resolution", u8cnt);
        s32res = iniparser_getint(m_pPcModeIni, sec_key, -1);
        if ((s32res < 0) || (s32res >= MAPI_RES_MAXIMUM))
        {
            SYSTEM_INFO_ERR("ERROR: ini resolution error.\n");
            ASSERT(0);
        }
        pPcModeTimingTable[u8cnt].m_enResolutionIndex = (MAPI_RESOLUTION_TYPE)s32res;
        snprintf(sec_key, MAX_BUFFER, "%d:HFreq", u8cnt);
        pPcModeTimingTable[u8cnt].m_u16HorizontalFrequency = iniparser_getint(m_pPcModeIni, sec_key, -1);
        snprintf(sec_key, MAX_BUFFER, "%d:VFreq", u8cnt);
        pPcModeTimingTable[u8cnt].m_u16VerticalFrequency = iniparser_getint(m_pPcModeIni, sec_key, -1);
        snprintf(sec_key, MAX_BUFFER, "%d:HStart", u8cnt);
        pPcModeTimingTable[u8cnt].m_u16HorizontalStart = iniparser_getint(m_pPcModeIni, sec_key, -1);
        snprintf(sec_key, MAX_BUFFER, "%d:VStart", u8cnt);
        pPcModeTimingTable[u8cnt].m_u16VerticalStart = iniparser_getint(m_pPcModeIni, sec_key, -1);
        snprintf(sec_key, MAX_BUFFER, "%d:HTotal", u8cnt);
        pPcModeTimingTable[u8cnt].m_u16HorizontalTotal = iniparser_getint(m_pPcModeIni, sec_key, -1);
        snprintf(sec_key, MAX_BUFFER, "%d:VTotal", u8cnt);
        pPcModeTimingTable[u8cnt].m_u16VerticalTotal = iniparser_getint(m_pPcModeIni, sec_key, -1);
        snprintf(sec_key, MAX_BUFFER, "%d:VTotalTorance", u8cnt);
        pPcModeTimingTable[u8cnt].m_u16VTotalTolerance = iniparser_getint(m_pPcModeIni, sec_key, -1);
        snprintf(sec_key, MAX_BUFFER, "%d:ADC_phase", u8cnt);
        pPcModeTimingTable[u8cnt].m_u16AdcPhase = iniparser_getint(m_pPcModeIni, sec_key, -1);
        snprintf(sec_key, MAX_BUFFER, "%d:u8StatusFlag", u8cnt);
        pPcModeTimingTable[u8cnt].m_u8StatusFlag = iniparser_getint(m_pPcModeIni, sec_key, -1);

        SYSTEM_INFO_DBG("ResolutionIndex %d\n",pPcModeTimingTable[u8cnt].m_enResolutionIndex);
        SYSTEM_INFO_DBG("HorizontalFrequency %d\n",pPcModeTimingTable[u8cnt].m_u16HorizontalFrequency);
        SYSTEM_INFO_DBG("VerticalFrequency %d\n",pPcModeTimingTable[u8cnt].m_u16VerticalFrequency);
        SYSTEM_INFO_DBG("HorizontalStart %d\n",pPcModeTimingTable[u8cnt].m_u16HorizontalStart);
        SYSTEM_INFO_DBG("VerticalStart %d\n",pPcModeTimingTable[u8cnt].m_u16VerticalStart);
        SYSTEM_INFO_DBG("HorizontalTotal %d\n",pPcModeTimingTable[u8cnt].m_u16HorizontalTotal);
        SYSTEM_INFO_DBG("VerticalTotal %d\n",pPcModeTimingTable[u8cnt].m_u16VerticalTotal);
        SYSTEM_INFO_DBG("VTotalTolerance %d\n",pPcModeTimingTable[u8cnt].m_u16VTotalTolerance);
        SYSTEM_INFO_DBG("AdcPhase %d\n",pPcModeTimingTable[u8cnt].m_u16AdcPhase);
        SYSTEM_INFO_DBG("StatusFlag %d\n",pPcModeTimingTable[u8cnt].m_u8StatusFlag);
    }
    SYSTEM_INFO_DBG("\n\n\n");

    SystemInfo::SetPcModeTimingTableCfgBlock(pPcModeTimingTable, u8TotalModeCount);
    if (pPcModeTimingTable)
    {
        delete[] pPcModeTimingTable;
        pPcModeTimingTable = NULL;
    }
    return TRUE;
}
#endif

MAPI_BOOL SystemInfo::SetAVSyncDelay(void)
{
    m_u16AvSyncDelay = SysIniBlock.u16AVSyncDelay;
    return TRUE;
}
MAPI_BOOL SystemInfo::SetAVSyncDelay(MAPI_U32 delay)
{
    m_u16AvSyncDelay = delay;
    SYSTEM_INFO_DBG("AV Sync delay is %d\n", m_u16AvSyncDelay);
    return TRUE;
}
MAPI_BOOL SystemInfo::SetMirrorFlag(void)
{
    m_bMirrorVideo=SysIniBlock.bMirrorVideo;

    if(SysIniBlock.bMirrorVideo)
    {
        if( (SysIniBlock.u8MirrorType==MIRROR_NORMAL) || (SysIniBlock.u8MirrorType>=MIRROR_MAX) )
        {
            m_u8MirrorMode= MIRROR_HV;      //default Mirror mode is MIRROR_HV
            printf("Warning!!! Mirror Type is illegal.\n\n");
        }
        else
        {
            m_u8MirrorMode= SysIniBlock.u8MirrorType;
        }
    }
    else
    {
        m_u8MirrorMode= MIRROR_NORMAL;
        if(SysIniBlock.u8MirrorType != MIRROR_NORMAL)    // Mirror disable  but Mirror mode not set MIRROR_NORMAL
        {
            printf("Warning!!! Mirror Type is not NORMAL, but Mirror is DISABLE.\n\n");
        }
    }

    return TRUE;
}

MAPI_BOOL SystemInfo::Set3DOverScanEnable(void)
{
    m_bEnable3DOverScan= SysIniBlock.bEnable3DOverScan;
    return TRUE;
}

MAPI_BOOL SystemInfo::SetHbbtvDelayInitFlag(void)
{
    m_bHbbtvDelayInitFlag= SysIniBlock.bHbbtvDelayInitFlag;
    return TRUE;
}

//for storage hdcp config
MAPI_BOOL SystemInfo::SetStorageHDCPCfg(void)
{
#if (MSTAR_TVOS == 1)
    SystemInfo::SetUseNandHdcpFlag(SysIniBlock.bNandHdcpEnable);

    SystemInfo::SetUseSPIHdcpFlag(SysIniBlock.bSPIHdcpEnable);

    SystemInfo::SetHdcpSPIBank(SysIniBlock.u8HdcpSPIBank);

    SystemInfo::setHdcpSPIOffSet(SysIniBlock.u16HdcpSPIOffset);

    SystemInfo::SetUseEEPROMFlag(SysIniBlock.bEEPROMHdcpEnable);

    SystemInfo::SetHdcpEEPROMAddr(SysIniBlock.u8HdcpEEPROMAddr);
 #endif

    SystemInfo::SetHDCPKeyFileName(SysIniBlock.pHDCPKeyFileName);
    return TRUE;
}


#if (LOCAL_DIMMING == 1)
//for storage mac config
MAPI_BOOL SystemInfo::SetStorageMACCfg(void)
{
    SystemInfo::SetUseSPIMacFlag(SysIniBlock.bSPIMacEnable);

    SystemInfo::SetMacSPIBank(SysIniBlock.u8MacSPIBank);

    SystemInfo::setMacSPIOffSet(SysIniBlock.u16MacSPIOffset);

    return TRUE;
}

//for local dimming config
MAPI_BOOL SystemInfo::SetLocalDIMMINGCfg(void)
{
    SystemInfo::SetLocalDIMMINGFlag(SysIniBlock.bLocalDIMMINGEnable);

    SystemInfo::SetLocalDIMMINGPanelSelect(SysIniBlock.u8LocalDIMMINGPanelSelect);
    return TRUE;
}
#endif

//set pc, component, HDMI source detect count
MAPI_BOOL SystemInfo::SetModeDectectCount(void)
{
    stSignalDetectCount stPcDetectSetting = {5,10};  //{stable count, unstable count}
    stSignalDetectCount stHDMIDetectSetting = {10,12};  //{stable count, unstable count}
    stSignalDetectCount stCompDetectSetting = {5,10};  //{stable count, unstable count}

    stPcDetectSetting.u8StableCount = iniparser_getint(m_pCustomerini, "ModeDetect:PcStableCount", 5);
    stPcDetectSetting.u8UnstableCount = iniparser_getint(m_pCustomerini, "ModeDetect:PcUnstableCount", 10);
    stHDMIDetectSetting.u8StableCount = iniparser_getint(m_pCustomerini, "ModeDetect:HDMIStableCount", 10);
    stHDMIDetectSetting.u8UnstableCount = iniparser_getint(m_pCustomerini, "ModeDetect:HDMIUnstableCount", 12);
    stCompDetectSetting.u8StableCount = iniparser_getint(m_pCustomerini, "ModeDetect:CompStableCount", 5);
    stCompDetectSetting.u8UnstableCount = iniparser_getint(m_pCustomerini, "ModeDetect:CompUnstableCount", 10);

    SystemInfo::SetPcDetectModeCount(&stPcDetectSetting);
    SystemInfo::SetHDMIDetectModeCount(&stHDMIDetectSetting);
    SystemInfo::SetCompDetectModeCount(&stCompDetectSetting);

    return TRUE;
}

#if(MULTI_DEMOD==1)
//set demod config
MAPI_BOOL SystemInfo::SetMultiDemodCfg(void)
{
    if(m_pCustomerini == NULL)
    {
        printf("ERROR: Load %s error.\n", SysIniBlock.ModelName);
    }

    stMultiExtendDemodCfg stMultiDemodSetting = {0,0};  //{stable count, unstable count}
    stMultiDemodSetting.u8DemodNumber = iniparser_getint(m_pCustomerini, "MultiDemod:DemodNumber", 1);
    stMultiDemodSetting.u8CurrentDemod = iniparser_getint(m_pCustomerini, "MultiDemod:CurrentDemodIndex", 0);

    //printf("\033[44m\033[33m[%s::%s::%d]\n\033[0m ,stMultiDemodSetting.u8DemodNumber=%d \n", __FILE__, __PRETTY_FUNCTION__, __LINE__,stMultiDemodSetting.u8DemodNumber);
    //printf("\033[44m\033[33m[%s::%s::%d]\n\033[0m ,stMultiDemodSetting.u8CurrentDemod=%d \n", __FILE__, __PRETTY_FUNCTION__, __LINE__,stMultiDemodSetting.u8CurrentDemod);

    memcpy(&m_MultiDemodParam, &stMultiDemodSetting, sizeof(stMultiExtendDemodCfg));
    return TRUE;
}
#endif
//
// callback function : Customer section in customer.ini
//

MAPI_U16    SystemInfo::IniGetInt(const char * pkey)
{
    MAPI_U16    u16Outdata;

    if(pkey==NULL)
        return -1;


    dictionary *pSysteminiGetData = iniparser_load(SYS_INI_PATH_FILENAME);
    if (pSysteminiGetData == NULL)
    {
        ASSERT(0);
    }

    char *pModelIniName =iniparser_getstr(pSysteminiGetData, "model:gModelName");

    dictionary *pCustomeriniGetData = iniparser_load(pModelIniName);
    ASSERT(pCustomeriniGetData);

    u16Outdata = iniparser_getint(pCustomeriniGetData, pkey, -1);

    iniparser_freedict(pCustomeriniGetData);
    iniparser_freedict(pSysteminiGetData);

    return  u16Outdata;
}

MAPI_U16 SystemInfo::IniGetBool(const char * pkey)
{
    MAPI_U16    u16Outdata;

    if(pkey==NULL)
        return -1;
    dictionary *pSysteminiGetData = iniparser_load(SYS_INI_PATH_FILENAME);
    if (pSysteminiGetData == NULL)
    {
        ASSERT(0);
    }

    char *pModelIniName =iniparser_getstr(pSysteminiGetData, "model:gModelName");

    dictionary *pCustomeriniGetData = iniparser_load(pModelIniName);
    ASSERT(pCustomeriniGetData);

    u16Outdata = iniparser_getboolean(pCustomeriniGetData, pkey, -1);

    iniparser_freedict(pSysteminiGetData);
    iniparser_freedict(pCustomeriniGetData);
    return  u16Outdata;
}

MAPI_BOOL   SystemInfo::IniGetStr(const char * pkey,  MAPI_U16 u16OutDataLen, char * pOutDataVal)
{
    if((pkey==NULL) || (pOutDataVal ==NULL))
        return FALSE;

    dictionary *pSysteminiGetData = iniparser_load(SYS_INI_PATH_FILENAME);
    if (pSysteminiGetData == NULL)
    {
        ASSERT(0);
    }

    char *pModelIniName =iniparser_getstr(pSysteminiGetData, "model:gModelName");

    dictionary *pCustomeriniGetData = iniparser_load(pModelIniName);
    ASSERT(pCustomeriniGetData);

    char *pStr = iniparser_getstr(pCustomeriniGetData, pkey);

    if(pStr == NULL)
    {
        SYSTEM_INFO_ERR("ERROR: The name in Customer INI is empty.\n");
        return FALSE;
    }

    int iStrLen = strlen(pStr) + 1;

    if(iStrLen>u16OutDataLen)
    {
        SYSTEM_INFO_ERR("IniGetStrCallBack : the length of output string is not enough.\n");
        return FALSE;
    }
    else
    {
        memset(pOutDataVal, 0, u16OutDataLen);
        memcpy(pOutDataVal, pStr, iStrLen);
    }
    iniparser_freedict(pCustomeriniGetData);
    iniparser_freedict(pSysteminiGetData);

    return TRUE;

}


MAPI_BOOL SystemInfo::CheckIniCustomerSectionExist(void)
{

    dictionary *pSysteminiGetData = iniparser_load(SYS_INI_PATH_FILENAME);
    if (pSysteminiGetData == NULL)
    {
        ASSERT(0);
    }

    char *pModelIniName =iniparser_getstr(pSysteminiGetData, "model:gModelName");
    dictionary *pCustomeriniGetData = iniparser_load(pModelIniName);
    ASSERT(pCustomeriniGetData);

    for(int  i = 0 ; i < pCustomeriniGetData->size ; i++)
    {
        if(pCustomeriniGetData->key[i]==NULL)
        {
            iniparser_freedict(pSysteminiGetData);
            iniparser_freedict(pCustomeriniGetData);
            return FALSE;
        }

        if(strncmp(pCustomeriniGetData->key[i], CUSTOMER_SECTION_NAME, strlen(CUSTOMER_SECTION_NAME)) == 0)
        {
            break;
        }
    }
    iniparser_freedict(pSysteminiGetData);
    iniparser_freedict(pCustomeriniGetData);

    return TRUE;
}

MAPI_BOOL SystemInfo::IniSetStr(char * IniFilePath, char * pKey, char * pValue)
{
    if((IniFilePath==NULL) || (pKey ==NULL) || (pValue==NULL))
    {
        SYSTEM_INFO_ERR("ERROR: the parameter of IniSetStr fuction is NULL\n");
        return FALSE;
    }

    dictionary *pIni = NULL;

    //Check IniFilePath exist?
    pIni = iniparser_load(IniFilePath);
    if (pIni == NULL)
    {
        SYSTEM_INFO_ERR("ERROR: Load %s error.\n", IniFilePath);
        return FALSE;
    }

    //Check pKey exist in ini file?
    if (iniparser_getstr(pIni, pKey) == NULL)
    {
        SYSTEM_INFO_ERR("ERROR: The key is empty.\n");
        iniparser_freedict(pIni);
        return FALSE;
    }

    if(iniparser_setstring(pIni, pKey, pValue) !=0)
    {
        SYSTEM_INFO_ERR("ERROR: The value is not set.\n");
        iniparser_freedict(pIni);
        return FALSE;
    }

    FILE *fp;
    fp = fopen(IniFilePath, "w");
    if (fp == NULL)
    {
        SYSTEM_INFO_ERR("ERROR: Can not open %s. \n", IniFilePath);
        iniparser_freedict(pIni);
        return FALSE;
    }
    iniparser_dump_ini(pIni, fp);
    fclose(fp);
    iniparser_freedict(pIni);


    return TRUE;

}

MAPI_BOOL SystemInfo::IniUpdateModelName(char * pModelName)
{
    MAPI_BOOL bret = FALSE;

    if((pModelName==NULL))
    {
        SYSTEM_INFO_ERR("ERROR: the parameter of IniUpdateModelName fuction is NULL.\n");
        return FALSE;
    }

    dictionary *pSysteminiUpdateData = iniparser_load(SYS_INI_PATH_FILENAME);
    if(pSysteminiUpdateData == NULL)
    {
        SYSTEM_INFO_ERR("ERROR: Load %s error.\n", SYS_INI_PATH_FILENAME);
        return FALSE;
    }

    MAPI_BOOL bSelectModelViaProjectID = iniparser_getboolean(pSysteminiUpdateData, "select_model_via_project_id:bEnabled",0);
    char TempBuffer[MAX_BUFFER+1]={0};

    if(bSelectModelViaProjectID == TRUE)
    {
        if((m_u16SpiProjectID == 0x0) || (m_u16SpiProjectID == 0xFFFF))
        {
            SYSTEM_INFO_ERR("ERROR: The project id is wrong.\n");
            iniparser_freedict(pSysteminiUpdateData);
            return FALSE;
        }
        /* Read the model name */
        snprintf(TempBuffer,MAX_BUFFER,"select_model_via_project_id:Model_%d",m_u16SpiProjectID);

        SYSTEM_INFO_IFO("SystemInfo::IniUpdateModelName, pModelName = %s \n", pModelName);
        SYSTEM_INFO_IFO("SystemInfo::IniUpdateModelName, TempBuffer = %s \n", TempBuffer);

        bret = IniSetStr((char *) SYS_INI_PATH_FILENAME, TempBuffer, pModelName);
    }
    else
    {
        SYSTEM_INFO_IFO("SystemInfo::IniUpdateModelName, pModelName = %s \n", pModelName);

        bret = IniSetStr((char *) SYS_INI_PATH_FILENAME, (char *) "model:gModelName", pModelName);
    }

    if(bret)
    {
        iniparser_UpdateCS(SYS_INI_PATH_FILENAME);
        sync();
    }
    else
    {
        SYSTEM_INFO_ERR("ERROR: Can Not set string into ini file\n");
        iniparser_freedict(pSysteminiUpdateData);
        return FALSE;
    }

    iniparser_freedict(pSysteminiUpdateData);
    return TRUE;
}

MAPI_BOOL SystemInfo::IniUpdatePanelName(char * pPanelName)
{

    if((pPanelName==NULL))
    {
        SYSTEM_INFO_ERR("ERROR: the parameter of IniUpdatePanelName fuction is NULL.\n");
        return FALSE;
    }


    dictionary *pSysteminiUpdateData = iniparser_load(SYS_INI_PATH_FILENAME);
    if(pSysteminiUpdateData == NULL)
    {
        SYSTEM_INFO_ERR("ERROR: Load %s error.\n", SYS_INI_PATH_FILENAME);
        return FALSE;
    }

    char pModelName[MAX_BUFFER+1]= {0};
    if(GetModelName(pSysteminiUpdateData, pModelName, MAX_BUFFER)  == FALSE)
    {
        iniparser_freedict(pSysteminiUpdateData);
        return FALSE;
    }
    SYSTEM_INFO_IFO("SystemInfo::IniUpdatePanelName, pModelName = %s \n", pModelName);
    if(IniSetStr(pModelName, (char *) "panel:m_pPanelName", pPanelName))
    {
        iniparser_UpdateCS(pModelName);
        sync();
    }

    iniparser_freedict(pSysteminiUpdateData);

    return TRUE;
}

MAPI_BOOL SystemInfo::IniUpdateCustomerini(char * pKeycode,char * pKeyvalue)
{

    if((pKeycode==NULL)||(pKeyvalue==NULL))
    {
        SYSTEM_INFO_ERR("ERROR: the parameter of IniUpdatePanelName fuction is NULL.\n");
        return FALSE;
    }


    dictionary *pSysteminiUpdateData = iniparser_load(SYS_INI_PATH_FILENAME);
    if(pSysteminiUpdateData == NULL)
    {
        SYSTEM_INFO_ERR("ERROR: Load %s error.\n", SYS_INI_PATH_FILENAME);
        return FALSE;
    }

    char pModelName[MAX_BUFFER+1]= {0};
    if(GetModelName(pSysteminiUpdateData, pModelName, MAX_BUFFER) == FALSE)
    {
        iniparser_freedict(pSysteminiUpdateData);
        return FALSE;
    }
    SYSTEM_INFO_IFO("SystemInfo::IniUpdateCustomerini, pModelName = %s \n", pModelName);
    if(IniSetStr(pModelName, pKeycode, pKeyvalue))
    {
        iniparser_UpdateCS(pModelName);
        sync();
    }

    iniparser_freedict(pSysteminiUpdateData);

    return TRUE;
}

MAPI_BOOL SystemInfo::IniUpdatePanelini(char * pKeycode,char * pKeyvalue)
{

    if((pKeycode==NULL)||(pKeyvalue==NULL))
    {
        SYSTEM_INFO_ERR("ERROR: the parameter of IniUpdatePanelName fuction is NULL.\n");
        return FALSE;
    }


    dictionary *pSysteminiUpdateData = iniparser_load(SYS_INI_PATH_FILENAME);
    if(pSysteminiUpdateData == NULL)
    {
        SYSTEM_INFO_ERR("ERROR: Load %s error.\n", SYS_INI_PATH_FILENAME);
        return FALSE;
    }

    char pModelName[MAX_BUFFER+1]= {0};
    if(GetModelName(pSysteminiUpdateData, pModelName, MAX_BUFFER) == FALSE)
    {
        iniparser_freedict(pSysteminiUpdateData);
        return FALSE;
    }
    SYSTEM_INFO_IFO("SystemInfo::IniUpdatePanelini, pModelName = %s \n", pModelName);
    dictionary *pCustomeriniUpdateData = iniparser_load(pModelName);
    if(pCustomeriniUpdateData == NULL)
    {
        SYSTEM_INFO_ERR("ERROR: Load %s error.\n",pModelName);
        iniparser_freedict(pSysteminiUpdateData);
        return FALSE;
    }

    char *pPanellName = iniparser_getstr(pCustomeriniUpdateData, "panel:m_pPanelName");
    if(pPanellName == NULL)
    {
        SYSTEM_INFO_ERR("ERROR: The model name is empty.\n");
        iniparser_freedict(pSysteminiUpdateData);
        iniparser_freedict(pCustomeriniUpdateData);
        return FALSE;
    }

    if(IniSetStr(pPanellName, pKeycode, pKeyvalue))
    {
        iniparser_UpdateCS(pPanellName);
        sync();
    }

    iniparser_freedict(pSysteminiUpdateData);
    iniparser_freedict(pCustomeriniUpdateData);

    return TRUE;
}


MAPI_BOOL SystemInfo::LoadGammaBinInfo()
{
    char GammaBinIniName[MAX_BUFFER + 1] ="";
    MAPI_U8  gammabinfileTotle;
    MAPI_U8  gammabinfileIndex;
    char *pGammaBinIniName =NULL;
    FILE *fp;
    int filelen;

    gammabinfileTotle = iniparser_getint(m_pCustomerini, "GAMMA_BIN:gammabinfileTotle",-1);
    gammabinfileIndex = iniparser_getint(m_pCustomerini, "GAMMA_BIN:gammabinfileIndex",-1);
    printf("\r\n  gammabinfileTotle = %d",gammabinfileTotle);
    printf("\r\n  gammabinfileIndex = %d",gammabinfileIndex);

    if(gammabinfileIndex> gammabinfileTotle)
    {
        SYSTEM_INFO_ERR("Can't open Gamma file!\n");
        return FALSE;
    }
    snprintf(GammaBinIniName, MAX_BUFFER, "GAMMA_BIN:GAMMA_FILE_%d", gammabinfileIndex);
    pGammaBinIniName = iniparser_getstring(m_pCustomerini, GammaBinIniName, NULL);
    printf("pGammaBinIniName = %s",pGammaBinIniName);

    /* check the Gamma file */
    if((pGammaBinIniName == NULL) || (strlen(pGammaBinIniName) == 0))
    {
        SYSTEM_INFO_ERR("Can't open Gamma bin file!\n");
        return FALSE;
    }

    GAMMA_TABLE_t   *Board_GammaTableInfo[1];
    Board_GammaTableInfo[0] = (GAMMA_TABLE_t *) malloc(sizeof(GAMMA_TABLE_t));
    ASSERT(Board_GammaTableInfo[0]);
    memset(Board_GammaTableInfo[0], 0, sizeof(GAMMA_TABLE_t));

    fp = fopen(pGammaBinIniName, "r");
    if (fp == NULL)
    {
        printf("Gamma Bin file open error\n");
    }
    else
    {
        fseek(fp,0,SEEK_END);
        filelen = ftell(fp);
        if (filelen != (GammaArrayMAXSize * 3))
        {
            printf("Gamma Bin file length error\n");
        }
        else
        {
            fseek(fp,0,SEEK_SET);
            filelen = fread((char *)Board_GammaTableInfo[0]->NormalGammaR,sizeof(char),GammaArrayMAXSize,fp);
            if (filelen == GammaArrayMAXSize)
            {
                fseek(fp,GammaArrayMAXSize,SEEK_SET);
                filelen = fread((char *)Board_GammaTableInfo[0]->NormalGammaG,sizeof(char),GammaArrayMAXSize,fp);
                if (filelen == GammaArrayMAXSize)
                {
                    fseek(fp,(GammaArrayMAXSize * 2),SEEK_SET);
                    filelen = fread((char *)Board_GammaTableInfo[0]->NormalGammaB,sizeof(char),GammaArrayMAXSize,fp);
                }
            }
        }
        fclose(fp);

    }
    SystemInfo::SetGammaTableCfg(Board_GammaTableInfo, 1, 0);
    return TRUE;

}

MAPI_BOOL SystemInfo::SetVideoFileName()
{
    m_u8VideoFileName = iniparser_getstr(m_pCustomerini, "VideoFilePath:VideoFileName");

    return TRUE;
}

char * SystemInfo::GetVideoFileName(void)
{
    return  m_u8VideoFileName;
}


//EosTek Patch Begin
MAPI_BOOL SystemInfo::SetVideoFileNameAlternative()
{
    m_u8VideoFileNameAlternative = iniparser_getstr(m_pCustomerini, "VideoFilePathAlternative:VideoFileName");

    return TRUE;
}

char * SystemInfo::GetVideoFileNameAlternative(void)
{
    return  m_u8VideoFileNameAlternative;
}
//EosTek Patch End

#if (DYNAMIC_I2C_ENABLE == 1)
MAPI_BOOL SystemInfo::LoadI2CBinInfo()
{
    FILE *fp;
    int filelen;

    I2CDeviceInfo_t *I2CInfo;
    I2CInfo = new I2CDeviceInfo_t;
    memset(I2CInfo, 0, sizeof(I2CDeviceInfo_t));

    fp = fopen(I2C_BIN_PATH, "r");

    if (fp == NULL)
    {
        printf("I2C Bin file open error\n");
        delete I2CInfo;
        return FALSE;
    }
    else
    {
        fseek(fp,0,SEEK_END);
        filelen = ftell(fp);
        if ((filelen % (sizeof(I2CDeviceInfo_t))) != 0)
        {
            printf("Tuner Bin file length error\n");
            return FALSE;
        }
        else
        {
            fseek(fp,0,SEEK_SET);
            while(fread(I2CInfo,sizeof(char),sizeof(I2CDeviceInfo_t),fp))
            {
                SystemInfo::ModifyI2CDevCfg(I2CInfo->gID, I2CInfo->i2c_bus, I2CInfo->slave_id);
            }
        }

        fclose(fp);
    }

    delete I2CInfo;

    return TRUE;
}
#endif

char * SystemInfo::GetBoardName(void)
{
    return  SysIniBlock.BoardName;
}

char * SystemInfo::GetSoftWareVer(void)
{
    return  SysIniBlock.SoftWareVer;
}

char * SystemInfo::GetSystemPanelName(void)
{
    return  SysIniBlock.SysPanelName;

}

MAPI_U16 SystemInfo::GetCurrentGammaTableNo(void)
{
    return (MAPI_U16)SysIniBlock.GammaTableNo;
}

MAPI_U16 SystemInfo::GetTotalGammaTableNo(void)
{
    return (MAPI_U16)SysIniBlock.TotalGammaTableNo;
}

#if (STEREO_3D_ENABLE == 1)
MAPI_PanelType* SystemInfo::GetGlobalPanelInfo()
{
    return &m_stGlobalPanelInfo;
}
#endif

MAPI_BOOL SystemInfo::UpdatePQParameterViaUsbKey(void)
{
#if (STB_ENABLE == 0)

    char pModelName[MAX_BUFFER+1]= {0};
    char *pDLCName = NULL;
    char *pMatrixName = NULL;
    char *pPQBinPathName = NULL;
    //MAPI_BOOL bPQUseDefaultValue = 0;
    MAPI_BOOL bMount = FALSE;
    const MAPI_U16 u16StrBufferSize = 128;
    char strCommand[u16StrBufferSize];

    // sys
    if(m_pSystemini == NULL)
    {
        m_pSystemini = iniparser_load(SYS_INI_PATH_FILENAME);
        ASSERT(m_pSystemini);
    }

    //customer
    if(GetModelName(m_pSystemini, pModelName, MAX_BUFFER) == FALSE)
    {
        ASSERT(0);
    }
    SYSTEM_INFO_IFO("SystemInfo::UpdatePQParameterViaUsbKey, pModelName = %s \n", pModelName);
    if(m_pCustomerini == NULL)
    {
        m_pCustomerini = iniparser_load(pModelName);
        ASSERT(m_pCustomerini);
    }

    //DLC
    pDLCName  =iniparser_getstr(m_pCustomerini, "DLC:m_pDLCName");
    ASSERT(pDLCName);

    //color matrix
    pMatrixName  =iniparser_getstr(m_pCustomerini , "ColorMatrix:MatrixName");
    ASSERT(pMatrixName);

    //PQBinPathName
    pPQBinPathName  =iniparser_getstr(m_pCustomerini , "panel:PQBinPathName");
    ASSERT(pPQBinPathName);

    SYSTEM_INFO_FLOW("Set PQ parameter - Start \n");

    // mount USB
    SYSTEM_INFO_FLOW("Set PQ parameter - Mount USB Key \n");
    snprintf(strCommand, u16StrBufferSize, "mkdir %s", MOUNT_FOLDER);
    SystemCmd(strCommand);
    snprintf(strCommand, u16StrBufferSize, "mount /dev/sda1 %s", MOUNT_FOLDER);
    SystemCmd(strCommand);

    snprintf(strCommand, u16StrBufferSize, "mount | grep \"/dev/sda1\" > %s", UPDATE_PQ_LOG);
    SystemCmd(strCommand);
    FILE *pFile = fopen(UPDATE_PQ_LOG, "r");
    if(pFile==NULL)
        return FALSE;

    char temp[256];
    memset(temp, 0, 256);
    while(fgets(temp,256,pFile))
    {
        temp[255] = 0;

        if(strstr(temp, "/dev/sda1 on /Customer/UpdatePQSetting"))
        {
            bMount = TRUE;
        }

        memset(temp, 0, sizeof(temp));
    }

    fclose(pFile);

    if(bMount == TRUE)
    {
        // Update DLC
        SYSTEM_INFO_FLOW("Set PQ parameter - Update DLC.ini \n");
        snprintf(strCommand, u16StrBufferSize, "cp -vf %s %s", DLC_UPDATE_SOURCE, pDLCName);
        SystemCmd(strCommand);;
        UpdateCSandBackupIni(pDLCName, CusBackupDLCMode);

        // Update ColorMatrix
        SYSTEM_INFO_FLOW("Set PQ parameter - Update ColorMatric \n");
        snprintf(strCommand, u16StrBufferSize, "cp -vf %s %s", COLORMATRIX_UPDATE_SOURCE, pMatrixName);
        SystemCmd(strCommand);
        UpdateCSandBackupIni(pMatrixName, CusBackupColorMatrixMode);

        //Update PQ table
        SYSTEM_INFO_FLOW("Set PQ parameter - Update PQ table \n");
        snprintf(strCommand, u16StrBufferSize, "cp -vf %s/%s %s/%s", MOUNT_FOLDER, BANDWIDTH_REG_TABLE_FILE, pPQBinPathName, BANDWIDTH_REG_TABLE_FILE);
        SystemCmd(strCommand);
        snprintf(strCommand, u16StrBufferSize, "cp -vf %s/%s %s/%s", MOUNT_FOLDER, PQ_MAIN_FILE, pPQBinPathName, PQ_MAIN_FILE);
        SystemCmd(strCommand);
        snprintf(strCommand, u16StrBufferSize, "cp -vf %s/%s %s/%s", MOUNT_FOLDER, PQ_MAIN_TEXT_FILE, pPQBinPathName, PQ_MAIN_TEXT_FILE);
        SystemCmd(strCommand);
        snprintf(strCommand, u16StrBufferSize, "cp -vf %s/%s %s/%s", MOUNT_FOLDER, PQ_MAIN_EX_FILE, pPQBinPathName, PQ_MAIN_EX_FILE);
        SystemCmd(strCommand);
        snprintf(strCommand, u16StrBufferSize, "cp -vf %s/%s %s/%s", MOUNT_FOLDER, PQ_MAIN_EX_TEXT_FILE, pPQBinPathName, PQ_MAIN_EX_TEXT_FILE);
        SystemCmd(strCommand);
        snprintf(strCommand, u16StrBufferSize, "cp -vf %s/%s %s/%s", MOUNT_FOLDER, PQ_SUB_FILE, pPQBinPathName, PQ_SUB_FILE);
        SystemCmd(strCommand);
        snprintf(strCommand, u16StrBufferSize, "cp -vf %s/%s %s/%s", MOUNT_FOLDER, PQ_SUB_TEXT_FILE, pPQBinPathName, PQ_SUB_TEXT_FILE);
        SystemCmd(strCommand);
        snprintf(strCommand, u16StrBufferSize, "cp -vf %s/%s %s/%s", MOUNT_FOLDER, PQ_SUB_EX_FILE, pPQBinPathName, PQ_SUB_EX_FILE);
        SystemCmd(strCommand);
        snprintf(strCommand, u16StrBufferSize, "cp -vf %s/%s %s/%s", MOUNT_FOLDER, PQ_SUB_EX_TEXT_FILE, pPQBinPathName, PQ_SUB_EX_TEXT_FILE);
        SystemCmd(strCommand);
        IniUpdateCustomerini((char *)"panel:bPQUseDefaultValue",(char *) "0");

        //Update Gamma
        SYSTEM_INFO_FLOW("Set PQ parameter - Update Gamma table \n");
        dictionary *pGammaIni = NULL;
        char *pGammaTableValue = NULL;
        pGammaIni = iniparser_load(GAMMA0_UPDATE_SOURCE);
        if(pGammaIni)
        {
            pGammaTableValue = iniparser_getstr(pGammaIni, "gamma_table_0:parameter_r");
            IniUpdatePanelini((char *)"gamma_table_0:parameter_r", pGammaTableValue);
            pGammaTableValue = iniparser_getstr(pGammaIni, "gamma_table_0:parameter_g");
            IniUpdatePanelini((char *)"gamma_table_0:parameter_g", pGammaTableValue);
            pGammaTableValue = iniparser_getstr(pGammaIni, "gamma_table_0:parameter_b");
            IniUpdatePanelini((char *)"gamma_table_0:parameter_b", pGammaTableValue);
            iniparser_freedict(pGammaIni);
        }

        sync();
        snprintf(strCommand, u16StrBufferSize, "umount %s", MOUNT_FOLDER);
        SystemCmd(strCommand);
        snprintf(strCommand, u16StrBufferSize, "rm -rf %s", MOUNT_FOLDER);
        SystemCmd(strCommand);

    }
    else
    {
        SYSTEM_INFO_FLOW("Set PQ parameter - Mount Fail \n");
    }

    SYSTEM_INFO_FLOW("Set PQ parameter - End \n");

    if(m_pSystemini)
    {
        iniparser_freedict(m_pSystemini);
        m_pSystemini=NULL;
    }
    if(m_pCustomerini)
    {
        iniparser_freedict(m_pCustomerini);
        m_pCustomerini=NULL;
    }

    if(bMount == FALSE)
    {
        return FALSE;
    }
#endif


    return TRUE;

}

MAPI_BOOL SystemInfo::GetUpdatePQFilePath(char pFilePath[MAX_BUFFER], const EN_PQ_UPDATE_FILE enPQFile)
{
    MAPI_BOOL bRet = TRUE;

#if (STB_ENABLE == 0)

    char pModelName[MAX_BUFFER + 1] = {0};
    char *pDLCName = NULL;
    char *pMatrixName = NULL;
    char *pPQBinPathName = NULL;

    // sys
    if(m_pSystemini == NULL)
    {
        m_pSystemini = iniparser_load(SYS_INI_PATH_FILENAME);
        ASSERT(m_pSystemini != NULL);
    }

    // customer
    if(GetModelName(m_pSystemini, pModelName, MAX_BUFFER) == FALSE)
    {
        ASSERT(0);
    }
    if(m_pCustomerini == NULL)
    {
        m_pCustomerini = iniparser_load(pModelName);
        ASSERT(m_pCustomerini != NULL);
    }

    // PQBinPathName
    pPQBinPathName  =iniparser_getstr(m_pCustomerini , "panel:PQBinPathName");
    ASSERT(pPQBinPathName);

    switch (enPQFile)
    {
        case E_DLC_FILE:
        {
            // DLC File
            pDLCName  =iniparser_getstr(m_pCustomerini, "DLC:m_pDLCName");
            ASSERT(pDLCName != NULL);
            snprintf(pFilePath, MAX_BUFFER, "%s", pDLCName);
            break;
        }
        case E_COLOR_MATRiX_FILE:
        {
            // ColorMatrix File
            pMatrixName  =iniparser_getstr(m_pCustomerini , "ColorMatrix:MatrixName");
            ASSERT(pMatrixName != NULL);
            snprintf(pFilePath, MAX_BUFFER, "%s", pMatrixName);
            break;
        }
        case E_BANDWIDTH_REG_TABLE_FILE:
        {
            // PQ Files
            snprintf(pFilePath, MAX_BUFFER, "%s%s", pPQBinPathName, BANDWIDTH_REG_TABLE_FILE);
            break;
        }
        case E_PQ_MAIN_FILE:
        {
            // PQ Files
            snprintf(pFilePath, MAX_BUFFER, "%s%s", pPQBinPathName, PQ_MAIN_FILE);
            break;
        }
        case E_PQ_MAIN_TEXT_FILE:
        {
            // PQ Files
            snprintf(pFilePath, MAX_BUFFER, "%s%s", pPQBinPathName, PQ_MAIN_TEXT_FILE);
            break;
        }
        case E_PQ_MAIN_EX_FILE:
        {
            // PQ Files
            snprintf(pFilePath, MAX_BUFFER, "%s%s", pPQBinPathName, PQ_MAIN_EX_FILE);
            break;
        }
        case E_PQ_MAIN_EX_TEXT_FILE:
        {
            // PQ Files
            snprintf(pFilePath, MAX_BUFFER, "%s%s", pPQBinPathName, PQ_MAIN_EX_TEXT_FILE);
            break;
        }
        case E_PQ_SUB_FILE:
        {
            // PQ Files
            snprintf(pFilePath, MAX_BUFFER, "%s%s", pPQBinPathName, PQ_SUB_FILE);
            break;
        }
        case E_PQ_SUB_TEXT_FILE:
        {
            // PQ Files
            snprintf(pFilePath, MAX_BUFFER, "%s%s", pPQBinPathName, PQ_SUB_TEXT_FILE);
            break;
        }
        case E_PQ_SUB_EX_FILE:
        {
            // PQ Files
            snprintf(pFilePath, MAX_BUFFER, "%s%s", pPQBinPathName, PQ_SUB_EX_FILE);
            break;
        }
        case E_PQ_SUB_EX_TEXT_FILE:
        {
            // PQ Files
            snprintf(pFilePath, MAX_BUFFER, "%s%s", pPQBinPathName, PQ_SUB_EX_TEXT_FILE);
            break;
        }
        case E_GAMMA0_FILE:
        {
            // Gamma Fils
            snprintf(pFilePath, MAX_BUFFER, "%s", GAMMA0_UPDATE_SOURCE);
            break;
        }
        default:
        {
            bRet = FALSE;
            break;
        }
    }

    if(m_pSystemini)
    {
        iniparser_freedict(m_pSystemini);
        m_pSystemini = NULL;
    }
    if(m_pCustomerini)
    {
        iniparser_freedict(m_pCustomerini);
        m_pCustomerini = NULL;
    }

#endif

    return bRet;
}

void SystemInfo::UpdatePQiniFiles()
{
    char pModelName[MAX_BUFFER + 1] = {0};
    char *pDLCName = NULL;
    char *pMatrixName = NULL;
    dictionary *pGammaIni = NULL;
    char *pGammaTableValue = NULL;

    // sys
    if(m_pSystemini == NULL)
    {
        m_pSystemini = iniparser_load(SYS_INI_PATH_FILENAME);
        ASSERT(m_pSystemini != NULL);
    }

    // customer
    if(GetModelName(m_pSystemini, pModelName, MAX_BUFFER) == FALSE)
    {
        ASSERT(0);
    }
    if(m_pCustomerini == NULL)
    {
        m_pCustomerini = iniparser_load(pModelName);
        ASSERT(m_pCustomerini != NULL);
    }

    // DLC
    pDLCName  =iniparser_getstr(m_pCustomerini, "DLC:m_pDLCName");
    ASSERT(pDLCName != NULL);
    UpdateCSandBackupIni(pDLCName, CusBackupDLCMode);

    // ColorMatrix
    pMatrixName  =iniparser_getstr(m_pCustomerini , "ColorMatrix:MatrixName");
    ASSERT(pMatrixName != NULL);
    UpdateCSandBackupIni(pMatrixName, CusBackupColorMatrixMode);

    // PQ
    IniUpdateCustomerini((char *)"panel:bPQUseDefaultValue",(char *)"0");

    // Gamma0
    pGammaIni = iniparser_load(GAMMA0_UPDATE_SOURCE);
    if(pGammaIni != NULL)
    {
        pGammaTableValue = iniparser_getstr(pGammaIni, "gamma_table_0:parameter_r");
        IniUpdatePanelini((char *)"gamma_table_0:parameter_r", pGammaTableValue);
        pGammaTableValue = iniparser_getstr(pGammaIni, "gamma_table_0:parameter_g");
        IniUpdatePanelini((char *)"gamma_table_0:parameter_g", pGammaTableValue);
        pGammaTableValue = iniparser_getstr(pGammaIni, "gamma_table_0:parameter_b");
        IniUpdatePanelini((char *)"gamma_table_0:parameter_b", pGammaTableValue);
        iniparser_freedict(pGammaIni);
    }

    if(m_pSystemini)
    {
        iniparser_freedict(m_pSystemini);
        m_pSystemini = NULL;
    }
    if(m_pCustomerini)
    {
        iniparser_freedict(m_pCustomerini);
        m_pCustomerini = NULL;
    }

    sync();
}

#if (TRAVELING_ENABLE == 1)
MAPI_BOOL SystemInfo::GetTravelingPairInfo(MAPI_INPUT_SOURCE_TYPE enMainInputSrc, MAPI_INPUT_SOURCE_TYPE enSubInputSrc)
{
#if (PIP_ENABLE == 1)
    return SysIniBlock.bTravelingPairInfo[enMainInputSrc][enSubInputSrc];
#else
    return FALSE;
#endif
}
#endif

#if (PIP_ENABLE == 1)
MAPI_BOOL SystemInfo::GetPipPairInfo(MAPI_INPUT_SOURCE_TYPE enMainInputSrc, MAPI_INPUT_SOURCE_TYPE enSubInputSrc)
{
    if( (enMainInputSrc<0) || (enMainInputSrc>=MAPI_INPUT_SOURCE_NUM) )
    {
        SYSTEM_INFO_IFO("SystemInfo: GetPipPairInfo, enMainInputSrc(%d) error\n", (int)enMainInputSrc);
        ASSERT(0);
    }

    if( (enSubInputSrc<0) || (enSubInputSrc>=MAPI_INPUT_SOURCE_NUM) )
    {
        SYSTEM_INFO_IFO("SystemInfo: GetPipPairInfo, enSubInputSrc(%d) error\n", (int)enSubInputSrc);
        ASSERT(0);
    }

    return SysIniBlock.bPipPairInfo[enMainInputSrc][enSubInputSrc];
}

MAPI_BOOL SystemInfo::GetPopPairInfo(MAPI_INPUT_SOURCE_TYPE enMainInputSrc, MAPI_INPUT_SOURCE_TYPE enSubInputSrc)
{
    if( (enMainInputSrc<0) || (enMainInputSrc>=MAPI_INPUT_SOURCE_NUM) )
    {
        SYSTEM_INFO_IFO("SystemInfo: GetPipPairInfo, enMainInputSrc(%d) error\n", (int)enMainInputSrc);
        ASSERT(0);
    }

    if( (enSubInputSrc<0) || (enSubInputSrc>=MAPI_INPUT_SOURCE_NUM) )
    {
        SYSTEM_INFO_IFO("SystemInfo: GetPipPairInfo, enSubInputSrc(%d) error\n", (int)enSubInputSrc);
        ASSERT(0);
    }

    return SysIniBlock.bPopPairInfo[enMainInputSrc][enSubInputSrc];
}


MAPI_BOOL SystemInfo::SetPipInfoSet(void)
{
    char *pPipModeName = NULL;

    if(m_pPipModeIni==NULL)
    {
        pPipModeName = iniparser_getstr(m_pCustomerini, "Pip:PipTableName");
        m_pPipModeIni = iniparser_load(pPipModeName);
    }

    //DTV
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pPipModeIni, "DTVMainInput:bDTVSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pPipModeIni, "DTVMainInput:bSTORAGESubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pPipModeIni, "DTVMainInput:bATVSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pPipModeIni, "DTVMainInput:bCVBSSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_CVBS2] = iniparser_getboolean(m_pPipModeIni, "DTVMainInput:bCVBS2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pPipModeIni, "DTVMainInput:bYPBPRSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pPipModeIni, "DTVMainInput:bVGASubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_VGA2] = iniparser_getboolean(m_pPipModeIni, "DTVMainInput:bVGA2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_VGA3] = iniparser_getboolean(m_pPipModeIni, "DTVMainInput:bVGA3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pPipModeIni, "DTVMainInput:bHDMISubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pPipModeIni, "DTVMainInput:bHDMI2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pPipModeIni, "DTVMainInput:bHDMI3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pPipModeIni, "DTVMainInput:bHDMI4SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_SVIDEO] = iniparser_getboolean(m_pPipModeIni, "DTVMainInput:bSVIDEOSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_SCART] = iniparser_getboolean(m_pPipModeIni, "DTVMainInput:bSCARTSubInput", 0);
    //STORAGE
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pPipModeIni, "STORAGEMainInput:bDTVSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pPipModeIni, "STORAGEMainInput:bSTORAGESubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pPipModeIni, "STORAGEMainInput:bATVSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pPipModeIni, "STORAGEMainInput:bCVBSSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_CVBS2] = iniparser_getboolean(m_pPipModeIni, "STORAGEMainInput:bCVBS2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pPipModeIni, "STORAGEMainInput:bYPBPRSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pPipModeIni, "STORAGEMainInput:bVGASubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_VGA2] = iniparser_getboolean(m_pPipModeIni, "STORAGEMainInput:bVGA2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_VGA3] = iniparser_getboolean(m_pPipModeIni, "STORAGEMainInput:bVGA3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pPipModeIni, "STORAGEMainInput:bHDMISubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pPipModeIni, "STORAGEMainInput:bHDMI2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pPipModeIni, "STORAGEMainInput:bHDMI3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pPipModeIni, "STORAGEMainInput:bHDMI4SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_SVIDEO] = iniparser_getboolean(m_pPipModeIni, "STORAGEMainInput:bSVIDEOSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_SCART] = iniparser_getboolean(m_pPipModeIni, "STORAGEMainInput:bSCARTSubInput", 0);
    //ATV
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pPipModeIni, "ATVMainInput:bDTVSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pPipModeIni, "ATVMainInput:bSTORAGESubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pPipModeIni, "ATVMainInput:bATVSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pPipModeIni, "ATVMainInput:bCVBSSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_CVBS2] = iniparser_getboolean(m_pPipModeIni, "ATVMainInput:bCVBS2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pPipModeIni, "ATVMainInput:bYPBPRSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pPipModeIni, "ATVMainInput:bVGASubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_VGA2] = iniparser_getboolean(m_pPipModeIni, "ATVMainInput:bVGA2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_VGA3] = iniparser_getboolean(m_pPipModeIni, "ATVMainInput:bVGA3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pPipModeIni, "ATVMainInput:bHDMISubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pPipModeIni, "ATVMainInput:bHDMI2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pPipModeIni, "ATVMainInput:bHDMI3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pPipModeIni, "ATVMainInput:bHDMI4SubInput", 0);
    //CVBS
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pPipModeIni, "CVBSMainInput:bDTVSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pPipModeIni, "CVBSMainInput:bSTORAGESubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pPipModeIni, "CVBSMainInput:bATVSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pPipModeIni, "CVBSMainInput:bCVBSSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_CVBS2] = iniparser_getboolean(m_pPipModeIni, "CVBSMainInput:bCVBS2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pPipModeIni, "CVBSMainInput:bYPBPRSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pPipModeIni, "CVBSMainInput:bVGASubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_VGA2] = iniparser_getboolean(m_pPipModeIni, "CVBSMainInput:bVGA2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_VGA3] = iniparser_getboolean(m_pPipModeIni, "CVBSMainInput:bVGA3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pPipModeIni, "CVBSMainInput:bHDMISubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pPipModeIni, "CVBSMainInput:bHDMI2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pPipModeIni, "CVBSMainInput:bHDMI3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pPipModeIni, "CVBSMainInput:bHDMI4SubInput", 0);
    //CVBS2
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_CVBS2][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pPipModeIni, "CVBS2MainInput:bDTVSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_CVBS2][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pPipModeIni, "CVBS2MainInput:bSTORAGESubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_CVBS2][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pPipModeIni, "CVBS2MainInput:bATVSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_CVBS2][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pPipModeIni, "CVBS2MainInput:bCVBSSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_CVBS2][MAPI_INPUT_SOURCE_CVBS2] = iniparser_getboolean(m_pPipModeIni, "CVBS2MainInput:bCVBS2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_CVBS2][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pPipModeIni, "CVBS2MainInput:bYPBPRSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_CVBS2][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pPipModeIni, "CVBS2MainInput:bVGASubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_CVBS2][MAPI_INPUT_SOURCE_VGA2] = iniparser_getboolean(m_pPipModeIni, "CVBS2MainInput:bVGA2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_CVBS2][MAPI_INPUT_SOURCE_VGA3] = iniparser_getboolean(m_pPipModeIni, "CVBS2MainInput:bVGA3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_CVBS2][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pPipModeIni, "CVBS2MainInput:bHDMISubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_CVBS2][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pPipModeIni, "CVBS2MainInput:bHDMI2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_CVBS2][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pPipModeIni, "CVBS2MainInput:bHDMI3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_CVBS2][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pPipModeIni, "CVBS2MainInput:bHDMI4SubInput", 0);
    //YPbPr
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pPipModeIni, "YPBPRMainInput:bDTVSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pPipModeIni, "YPBPRMainInput:bSTORAGESubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pPipModeIni, "YPBPRMainInput:bATVSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pPipModeIni, "YPBPRMainInput:bCVBSSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_CVBS2] = iniparser_getboolean(m_pPipModeIni, "YPBPRMainInput:bCVBS2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pPipModeIni, "YPBPRMainInput:bYPBPRSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pPipModeIni, "YPBPRMainInput:bVGASubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_VGA2] = iniparser_getboolean(m_pPipModeIni, "YPBPRMainInput:bVGA2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_VGA3] = iniparser_getboolean(m_pPipModeIni, "YPBPRMainInput:bVGA3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pPipModeIni, "YPBPRMainInput:bHDMISubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pPipModeIni, "YPBPRMainInput:bHDMI2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pPipModeIni, "YPBPRMainInput:bHDMI3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pPipModeIni, "YPBPRMainInput:bHDMI4SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_SVIDEO] = iniparser_getboolean(m_pPipModeIni, "YPBPRMainInput:bSVIDEOSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_SCART] = iniparser_getboolean(m_pPipModeIni, "YPBPRMainInput:bSCARTSubInput", 0);
    //VGA
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pPipModeIni, "VGAMainInput:bDTVSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pPipModeIni, "VGAMainInput:bSTORAGESubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pPipModeIni, "VGAMainInput:bATVSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pPipModeIni, "VGAMainInput:bCVBSSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_CVBS2] = iniparser_getboolean(m_pPipModeIni, "VGAMainInput:bCVBS2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pPipModeIni, "VGAMainInput:bYPBPRSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pPipModeIni, "VGAMainInput:bVGASubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_VGA2] = iniparser_getboolean(m_pPipModeIni, "VGAMainInput:bVGA2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_VGA3] = iniparser_getboolean(m_pPipModeIni, "VGAMainInput:bVGA3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pPipModeIni, "VGAMainInput:bHDMISubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pPipModeIni, "VGAMainInput:bHDMI2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pPipModeIni, "VGAMainInput:bHDMI3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pPipModeIni, "VGAMainInput:bHDMI4SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_SVIDEO] = iniparser_getboolean(m_pPipModeIni, "VGAMainInput:bSVIDEOSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_SCART] = iniparser_getboolean(m_pPipModeIni, "VGAMainInput:bSCARTSubInput", 0);
    //VGA2
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA2][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pPipModeIni, "VGA2MainInput:bDTVSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA2][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pPipModeIni, "VGA2MainInput:bSTORAGESubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA2][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pPipModeIni, "VGA2MainInput:bATVSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA2][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pPipModeIni, "VGA2MainInput:bCVBSSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA2][MAPI_INPUT_SOURCE_CVBS2] = iniparser_getboolean(m_pPipModeIni, "VGA2MainInput:bCVBS2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA2][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pPipModeIni, "VGA2MainInput:bYPBPRSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA2][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pPipModeIni, "VGA2MainInput:bVGASubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA2][MAPI_INPUT_SOURCE_VGA3] = iniparser_getboolean(m_pPipModeIni, "VGA2MainInput:bVGA3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA2][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pPipModeIni, "VGA2MainInput:bHDMISubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA2][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pPipModeIni, "VGA2MainInput:bHDMI2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA2][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pPipModeIni, "VGA2MainInput:bHDMI3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA2][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pPipModeIni, "VGA2MainInput:bHDMI4SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA2][MAPI_INPUT_SOURCE_SVIDEO] = iniparser_getboolean(m_pPipModeIni, "VGA2MainInput:bSVIDEOSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA2][MAPI_INPUT_SOURCE_SCART] = iniparser_getboolean(m_pPipModeIni, "VGA2MainInput:bSCARTSubInput", 0);
    //VGA3
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pPipModeIni, "VGA3MainInput:bDTVSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pPipModeIni, "VGA3MainInput:bSTORAGESubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pPipModeIni, "VGA3MainInput:bATVSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pPipModeIni, "VGA3MainInput:bCVBSSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_CVBS2] = iniparser_getboolean(m_pPipModeIni, "VGA3MainInput:bCVBS2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pPipModeIni, "VGA3MainInput:bYPBPRSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pPipModeIni, "VGA3MainInput:bVGASubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_VGA2] = iniparser_getboolean(m_pPipModeIni, "VGA3MainInput:bVGA2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_VGA3] = iniparser_getboolean(m_pPipModeIni, "VGA3MainInput:bVGA3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pPipModeIni, "VGA3MainInput:bHDMISubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pPipModeIni, "VGA3MainInput:bHDMI2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pPipModeIni, "VGA3MainInput:bHDMI3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pPipModeIni, "VGA3MainInput:bHDMI4SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_SVIDEO] = iniparser_getboolean(m_pPipModeIni, "VGA3MainInput:bSVIDEOSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_SCART] = iniparser_getboolean(m_pPipModeIni, "VGA3MainInput:bSCARTSubInput", 0);
    //HDMI
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pPipModeIni, "HDMIMainInput:bDTVSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pPipModeIni, "HDMIMainInput:bSTORAGESubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pPipModeIni, "HDMIMainInput:bATVSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pPipModeIni, "HDMIMainInput:bCVBSSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_CVBS2] = iniparser_getboolean(m_pPipModeIni, "HDMIMainInput:bCVBS2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pPipModeIni, "HDMIMainInput:bYPBPRSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pPipModeIni, "HDMIMainInput:bVGASubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_VGA2] = iniparser_getboolean(m_pPipModeIni, "HDMIMainInput:bVGA2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_VGA3] = iniparser_getboolean(m_pPipModeIni, "HDMIMainInput:bVGA3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pPipModeIni, "HDMIMainInput:bHDMISubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pPipModeIni, "HDMIMainInput:bHDMI2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pPipModeIni, "HDMIMainInput:bHDMI3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pPipModeIni, "HDMIMainInput:bHDMI4SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_SVIDEO] = iniparser_getboolean(m_pPipModeIni, "HDMIMainInput:bSVIDEOSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_SCART] = iniparser_getboolean(m_pPipModeIni, "HDMIMainInput:bSCARTSubInput", 0);
    //HDMI2
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pPipModeIni, "HDMI2MainInput:bDTVSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pPipModeIni, "HDMI2MainInput:bSTORAGESubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pPipModeIni, "HDMI2MainInput:bATVSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pPipModeIni, "HDMI2MainInput:bCVBSSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pPipModeIni, "HDMI2MainInput:bCVBS2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pPipModeIni, "HDMI2MainInput:bYPBPRSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pPipModeIni, "HDMI2MainInput:bVGASubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_VGA2] = iniparser_getboolean(m_pPipModeIni, "HDMI2MainInput:bVGA2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_VGA3] = iniparser_getboolean(m_pPipModeIni, "HDMI2MainInput:bVGA3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pPipModeIni, "HDMI2MainInput:bHDMISubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pPipModeIni, "HDMI2MainInput:bHDMI2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pPipModeIni, "HDMI2MainInput:bHDMI3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pPipModeIni, "HDMI2MainInput:bHDMI4SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_SVIDEO] = iniparser_getboolean(m_pPipModeIni, "HDMI2MainInput:bSVIDEOSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_SCART] = iniparser_getboolean(m_pPipModeIni, "HDMI2MainInput:bSCARTSubInput", 0);
    //HDMI3
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pPipModeIni, "HDMI3MainInput:bDTVSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pPipModeIni, "HDMI3MainInput:bSTORAGESubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pPipModeIni, "HDMI3MainInput:bATVSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pPipModeIni, "HDMI3MainInput:bCVBSSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_CVBS2] = iniparser_getboolean(m_pPipModeIni, "HDMI3MainInput:bCVBS2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pPipModeIni, "HDMI3MainInput:bYPBPRSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pPipModeIni, "HDMI3MainInput:bVGASubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_VGA2] = iniparser_getboolean(m_pPipModeIni, "HDMI3MainInput:bVGA2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_VGA3] = iniparser_getboolean(m_pPipModeIni, "HDMI3MainInput:bVGA3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pPipModeIni, "HDMI3MainInput:bHDMISubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pPipModeIni, "HDMI3MainInput:bHDMI2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pPipModeIni, "HDMI3MainInput:bHDMI3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pPipModeIni, "HDMI3MainInput:bHDMI4SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_SVIDEO] = iniparser_getboolean(m_pPipModeIni, "HDMI3MainInput:bSVIDEOSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_SCART] = iniparser_getboolean(m_pPipModeIni, "HDMI3MainInput:bSCARTSubInput", 0);
    //HDMI4
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pPipModeIni, "HDMI4MainInput:bDTVSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pPipModeIni, "HDMI4MainInput:bSTORAGESubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pPipModeIni, "HDMI4MainInput:bATVSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pPipModeIni, "HDMI4MainInput:bCVBSSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_CVBS2] = iniparser_getboolean(m_pPipModeIni, "HDMI4MainInput:bCVBS2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pPipModeIni, "HDMI4MainInput:bYPBPRSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pPipModeIni, "HDMI4MainInput:bVGASubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_VGA2] = iniparser_getboolean(m_pPipModeIni, "HDMI4MainInput:bVGA2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_VGA3] = iniparser_getboolean(m_pPipModeIni, "HDMI4MainInput:bVGA3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pPipModeIni, "HDMI4MainInput:bHDMISubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pPipModeIni, "HDMI4MainInput:bHDMI2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pPipModeIni, "HDMI4MainInput:bHDMI3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pPipModeIni, "HDMI4MainInput:bHDMI4SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_SVIDEO] = iniparser_getboolean(m_pPipModeIni, "HDMI4MainInput:bSVIDEOSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_SCART] = iniparser_getboolean(m_pPipModeIni, "HDMI4MainInput:bSCARTSubInput", 0);
    //SVIDEO
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_SVIDEO][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pPipModeIni, "SVIDEOMainInput:bDTVSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_SVIDEO][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pPipModeIni, "SVIDEOMainInput:bYPBPRSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_SVIDEO][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pPipModeIni, "SVIDEOMainInput:bVGASubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_SVIDEO][MAPI_INPUT_SOURCE_VGA2] = iniparser_getboolean(m_pPipModeIni, "SVIDEOMainInput:bVGA2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_SVIDEO][MAPI_INPUT_SOURCE_VGA3] = iniparser_getboolean(m_pPipModeIni, "SVIDEOMainInput:bVGA3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_SVIDEO][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pPipModeIni, "SVIDEOMainInput:bHDMISubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_SVIDEO][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pPipModeIni, "SVIDEOMainInput:bHDMI2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_SVIDEO][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pPipModeIni, "SVIDEOMainInput:bHDMI3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_SVIDEO][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pPipModeIni, "SVIDEOMainInput:bHDMI4SubInput", 0);
    //SCART
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_SCART][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pPipModeIni, "SCARTMainInput:bDTVSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_SCART][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pPipModeIni, "SCARTMainInput:bYPBPRSubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_SCART][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pPipModeIni, "SCARTMainInput:bVGASubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_SCART][MAPI_INPUT_SOURCE_VGA2] = iniparser_getboolean(m_pPipModeIni, "SCARTMainInput:bVGA2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_SCART][MAPI_INPUT_SOURCE_VGA3] = iniparser_getboolean(m_pPipModeIni, "SCARTMainInput:bVGA3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_SCART][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pPipModeIni, "SCARTMainInput:bHDMISubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_SCART][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pPipModeIni, "SCARTMainInput:bHDMI2SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_SCART][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pPipModeIni, "SCARTMainInput:bHDMI3SubInput", 0);
    SysIniBlock.bPipPairInfo[MAPI_INPUT_SOURCE_SCART][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pPipModeIni, "SCARTMainInput:bHDMI4SubInput", 0);

    if(m_pPipModeIni!=NULL)
    {
        iniparser_freedict(m_pPipModeIni);
        m_pPipModeIni = NULL;
    }

    return TRUE;
}

MAPI_BOOL SystemInfo::SetPopInfoSet(void)
{

    char *pPopModeName = NULL;

    if(pPopModeName==NULL)
    {
        pPopModeName = iniparser_getstr(m_pCustomerini, "Pip:PopTableName");
        m_pPopModeIni = iniparser_load(pPopModeName);
    }

    //DTV
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pPopModeIni, "DTVMainInput:bDTVSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pPopModeIni, "DTVMainInput:bSTORAGESubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pPopModeIni, "DTVMainInput:bATVSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pPopModeIni, "DTVMainInput:bCVBSSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_CVBS2] = iniparser_getboolean(m_pPopModeIni, "DTVMainInput:bCVBS2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pPopModeIni, "DTVMainInput:bYPBPRSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pPopModeIni, "DTVMainInput:bVGASubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_VGA2] = iniparser_getboolean(m_pPopModeIni, "DTVMainInput:bVGA2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_VGA3] = iniparser_getboolean(m_pPopModeIni, "DTVMainInput:bVGA3SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pPopModeIni, "DTVMainInput:bHDMISubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pPopModeIni, "DTVMainInput:bHDMI2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pPopModeIni, "DTVMainInput:bHDMI3SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pPopModeIni, "DTVMainInput:bHDMI4SubInput", 0);
    //STORAGE
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pPopModeIni, "STORAGEMainInput:bDTVSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pPopModeIni, "STORAGEMainInput:bSTORAGESubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pPopModeIni, "STORAGEMainInput:bATVSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pPopModeIni, "STORAGEMainInput:bCVBSSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_CVBS2] = iniparser_getboolean(m_pPopModeIni, "STORAGEMainInput:bCVBS2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pPopModeIni, "STORAGEMainInput:bYPBPRSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pPopModeIni, "STORAGEMainInput:bVGASubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_VGA2] = iniparser_getboolean(m_pPopModeIni, "STORAGEMainInput:bVGA2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_VGA3] = iniparser_getboolean(m_pPopModeIni, "STORAGEMainInput:bVGA3SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pPopModeIni, "STORAGEMainInput:bHDMISubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pPopModeIni, "STORAGEMainInput:bHDMI2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pPopModeIni, "STORAGEMainInput:bHDMI3SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pPopModeIni, "STORAGEMainInput:bHDMI4SubInput", 0);
    //ATV
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pPopModeIni, "ATVMainInput:bDTVSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pPopModeIni, "ATVMainInput:bSTORAGESubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pPopModeIni, "ATVMainInput:bATVSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pPopModeIni, "ATVMainInput:bCVBSSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_CVBS2] = iniparser_getboolean(m_pPopModeIni, "ATVMainInput:bCVBS2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pPopModeIni, "ATVMainInput:bYPBPRSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pPopModeIni, "ATVMainInput:bVGASubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_VGA2] = iniparser_getboolean(m_pPopModeIni, "ATVMainInput:bVGA2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_VGA3] = iniparser_getboolean(m_pPopModeIni, "ATVMainInput:bVGA3SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pPopModeIni, "ATVMainInput:bHDMISubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pPopModeIni, "ATVMainInput:bHDMI2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pPopModeIni, "ATVMainInput:bHDMI3SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pPopModeIni, "ATVMainInput:bHDMI4SubInput", 0);
    //CVBS
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pPopModeIni, "CVBSMainInput:bDTVSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pPopModeIni, "CVBSMainInput:bSTORAGESubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pPopModeIni, "CVBSMainInput:bATVSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pPopModeIni, "CVBSMainInput:bCVBSSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_CVBS2] = iniparser_getboolean(m_pPopModeIni, "CVBSMainInput:bCVBS2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pPopModeIni, "CVBSMainInput:bYPBPRSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pPopModeIni, "CVBSMainInput:bVGASubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_VGA2] = iniparser_getboolean(m_pPopModeIni, "CVBSMainInput:bVGA2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_VGA3] = iniparser_getboolean(m_pPopModeIni, "CVBSMainInput:bVGA3SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pPopModeIni, "CVBSMainInput:bHDMISubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pPopModeIni, "CVBSMainInput:bHDMI2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pPopModeIni, "CVBSMainInput:bHDMI3SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pPopModeIni, "CVBSMainInput:bHDMI4SubInput", 0);
    //CVBS2
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_CVBS2][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pPopModeIni, "CVBS2MainInput:bDTVSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_CVBS2][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pPopModeIni, "CVBS2MainInput:bSTORAGESubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_CVBS2][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pPopModeIni, "CVBS2MainInput:bATVSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_CVBS2][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pPopModeIni, "CVBS2MainInput:bCVBSSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_CVBS2][MAPI_INPUT_SOURCE_CVBS2] = iniparser_getboolean(m_pPopModeIni, "CVBS2MainInput:bCVBS2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_CVBS2][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pPopModeIni, "CVBS2MainInput:bYPBPRSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_CVBS2][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pPopModeIni, "CVBS2MainInput:bVGASubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_CVBS2][MAPI_INPUT_SOURCE_VGA2] = iniparser_getboolean(m_pPopModeIni, "CVBS2MainInput:bVGA2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_CVBS2][MAPI_INPUT_SOURCE_VGA3] = iniparser_getboolean(m_pPopModeIni, "CVBS2MainInput:bVGA3SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_CVBS2][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pPopModeIni, "CVBS2MainInput:bHDMISubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_CVBS2][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pPopModeIni, "CVBS2MainInput:bHDMI2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_CVBS2][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pPopModeIni, "CVBS2MainInput:bHDMI3SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_CVBS2][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pPopModeIni, "CVBS2MainInput:bHDMI4SubInput", 0);
    //YPbPr
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pPopModeIni, "YPBPRMainInput:bDTVSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pPopModeIni, "YPBPRMainInput:bSTORAGESubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pPopModeIni, "YPBPRMainInput:bATVSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pPopModeIni, "YPBPRMainInput:bCVBSSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_CVBS2] = iniparser_getboolean(m_pPopModeIni, "YPBPRMainInput:bCVBS2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pPopModeIni, "YPBPRMainInput:bYPBPRSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pPopModeIni, "YPBPRMainInput:bVGASubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_VGA2] = iniparser_getboolean(m_pPopModeIni, "YPBPRMainInput:bVGA2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_VGA3] = iniparser_getboolean(m_pPopModeIni, "YPBPRMainInput:bVGA3SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pPopModeIni, "YPBPRMainInput:bHDMISubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pPopModeIni, "YPBPRMainInput:bHDMI2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pPopModeIni, "YPBPRMainInput:bHDMI3SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pPopModeIni, "YPBPRMainInput:bHDMI4SubInput", 0);
    //VGA
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pPopModeIni, "VGAMainInput:bDTVSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pPopModeIni, "VGAMainInput:bSTORAGESubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pPopModeIni, "VGAMainInput:bATVSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pPopModeIni, "VGAMainInput:bCVBSSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_CVBS2] = iniparser_getboolean(m_pPopModeIni, "VGAMainInput:bCVBS2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pPopModeIni, "VGAMainInput:bYPBPRSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pPopModeIni, "VGAMainInput:bVGASubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_VGA2] = iniparser_getboolean(m_pPopModeIni, "VGAMainInput:bVGA2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_VGA3] = iniparser_getboolean(m_pPopModeIni, "VGAMainInput:bVGA3SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pPopModeIni, "VGAMainInput:bHDMISubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pPopModeIni, "VGAMainInput:bHDMI2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pPopModeIni, "VGAMainInput:bHDMI3SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pPopModeIni, "VGAMainInput:bHDMI4SubInput", 0);
    //VGA2
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA2][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pPopModeIni, "VGA2MainInput:bDTVSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA2][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pPopModeIni, "VGA2MainInput:bSTORAGESubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA2][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pPopModeIni, "VGA2MainInput:bATVSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA2][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pPopModeIni, "VGA2MainInput:bCVBSSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA2][MAPI_INPUT_SOURCE_CVBS2] = iniparser_getboolean(m_pPopModeIni, "VGA2MainInput:bCVBS2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA2][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pPopModeIni, "VGA2MainInput:bYPBPRSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA2][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pPopModeIni, "VGA2MainInput:bVGASubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA2][MAPI_INPUT_SOURCE_VGA2] = iniparser_getboolean(m_pPopModeIni, "VGA2MainInput:bVGA2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_VGA3] = iniparser_getboolean(m_pPopModeIni, "VGA2MainInput:bVGA3SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA2][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pPopModeIni, "VGA2MainInput:bHDMISubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA2][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pPopModeIni, "VGA2MainInput:bHDMI2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA2][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pPopModeIni, "VGA2MainInput:bHDMI3SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA2][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pPopModeIni, "VGA2MainInput:bHDMI4SubInput", 0);
    //VGA3
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pPopModeIni, "VGA3MainInput:bDTVSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pPopModeIni, "VGA3MainInput:bSTORAGESubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pPopModeIni, "VGA3MainInput:bATVSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pPopModeIni, "VGA3MainInput:bCVBSSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_CVBS2] = iniparser_getboolean(m_pPopModeIni, "VGA3MainInput:bCVBS2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pPopModeIni, "VGA3MainInput:bYPBPRSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pPopModeIni, "VGA3MainInput:bVGASubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_VGA2] = iniparser_getboolean(m_pPopModeIni, "VGA3MainInput:bVGA2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_VGA3] = iniparser_getboolean(m_pPopModeIni, "VGA3MainInput:bVGA3SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pPopModeIni, "VGA3MainInput:bHDMISubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pPopModeIni, "VGA3MainInput:bHDMI2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pPopModeIni, "VGA3MainInput:bHDMI3SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_VGA3][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pPopModeIni, "VGA3MainInput:bHDMI4SubInput", 0);
    //HDMI
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pPopModeIni, "HDMIMainInput:bDTVSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pPopModeIni, "HDMIMainInput:bSTORAGESubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pPopModeIni, "HDMIMainInput:bATVSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pPopModeIni, "HDMIMainInput:bCVBSSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_CVBS2] = iniparser_getboolean(m_pPopModeIni, "HDMIMainInput:bCVBS2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pPopModeIni, "HDMIMainInput:bYPBPRSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pPopModeIni, "HDMIMainInput:bVGASubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_VGA2] = iniparser_getboolean(m_pPopModeIni, "HDMIMainInput:bVGA2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_VGA3] = iniparser_getboolean(m_pPopModeIni, "HDMIMainInput:bVGA3SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pPopModeIni, "HDMIMainInput:bHDMISubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pPopModeIni, "HDMIMainInput:bHDMI2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pPopModeIni, "HDMIMainInput:bHDMI3SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pPopModeIni, "HDMIMainInput:bHDMI4SubInput", 0);
    //HDMI2
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pPopModeIni, "HDMI2MainInput:bDTVSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pPopModeIni, "HDMI2MainInput:bSTORAGESubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pPopModeIni, "HDMI2MainInput:bATVSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pPopModeIni, "HDMI2MainInput:bCVBSSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_CVBS2] = iniparser_getboolean(m_pPopModeIni, "HDMI2MainInput:bCVBS2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pPopModeIni, "HDMI2MainInput:bYPBPRSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pPopModeIni, "HDMI2MainInput:bVGASubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_VGA2] = iniparser_getboolean(m_pPopModeIni, "HDMI2MainInput:bVGA2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_VGA3] = iniparser_getboolean(m_pPopModeIni, "HDMI2MainInput:bVGA3SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pPopModeIni, "HDMI2MainInput:bHDMISubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pPopModeIni, "HDMI2MainInput:bHDMI2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pPopModeIni, "HDMI2MainInput:bHDMI3SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pPopModeIni, "HDMI2MainInput:bHDMI4SubInput", 0);
    //HDMI3
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pPopModeIni, "HDMI3MainInput:bDTVSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pPopModeIni, "HDMI3MainInput:bSTORAGESubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pPopModeIni, "HDMI3MainInput:bATVSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pPopModeIni, "HDMI3MainInput:bCVBSSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_CVBS2] = iniparser_getboolean(m_pPopModeIni, "HDMI3MainInput:bCVBS2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pPopModeIni, "HDMI3MainInput:bYPBPRSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pPopModeIni, "HDMI3MainInput:bVGASubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_VGA2] = iniparser_getboolean(m_pPopModeIni, "HDMI3MainInput:bVGA2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_VGA3] = iniparser_getboolean(m_pPopModeIni, "HDMI3MainInput:bVGA3SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pPopModeIni, "HDMI3MainInput:bHDMISubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pPopModeIni, "HDMI3MainInput:bHDMI2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pPopModeIni, "HDMI3MainInput:bHDMI3SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pPopModeIni, "HDMI3MainInput:bHDMI4SubInput", 0);
    //HDMI4
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pPopModeIni, "HDMI4MainInput:bDTVSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pPopModeIni, "HDMI4MainInput:bSTORAGESubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pPopModeIni, "HDMI4MainInput:bATVSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pPopModeIni, "HDMI4MainInput:bCVBSSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_CVBS2] = iniparser_getboolean(m_pPopModeIni, "HDMI4MainInput:bCVBS2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pPopModeIni, "HDMI4MainInput:bYPBPRSubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pPopModeIni, "HDMI4MainInput:bVGASubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_VGA2] = iniparser_getboolean(m_pPopModeIni, "HDMI4MainInput:bVGA2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_VGA3] = iniparser_getboolean(m_pPopModeIni, "HDMI4MainInput:bVGA3SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pPopModeIni, "HDMI4MainInput:bHDMISubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pPopModeIni, "HDMI4MainInput:bHDMI2SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pPopModeIni, "HDMI4MainInput:bHDMI3SubInput", 0);
    SysIniBlock.bPopPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pPopModeIni, "HDMI4MainInput:bHDMI4SubInput", 0);

    if(m_pPopModeIni!=NULL)
    {
        iniparser_freedict(m_pPopModeIni);
        m_pPopModeIni = NULL;
    }

    return TRUE;
}

#if (TRAVELING_ENABLE == 1)
MAPI_BOOL SystemInfo::SetTravelingInfoSet(void)
{
    //DTV
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pTravelingModeIni, "DTVMainInput:bDTVSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pTravelingModeIni, "DTVMainInput:bSTORAGESubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pTravelingModeIni, "DTVMainInput:bATVSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pTravelingModeIni, "DTVMainInput:bCVBSSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pTravelingModeIni, "DTVMainInput:bYPBPRSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pTravelingModeIni, "DTVMainInput:bVGASubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pTravelingModeIni, "DTVMainInput:bHDMISubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pTravelingModeIni, "DTVMainInput:bHDMI2SubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pTravelingModeIni, "DTVMainInput:bHDMI3SubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_DTV][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pTravelingModeIni, "DTVMainInput:bHDMI4SubInput", 0);
    //STORAGE
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pTravelingModeIni, "STORAGEMainInput:bDTVSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pTravelingModeIni, "STORAGEMainInput:bSTORAGESubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pTravelingModeIni, "STORAGEMainInput:bATVSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pTravelingModeIni, "STORAGEMainInput:bCVBSSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pTravelingModeIni, "STORAGEMainInput:bYPBPRSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pTravelingModeIni, "STORAGEMainInput:bVGASubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pTravelingModeIni, "STORAGEMainInput:bHDMISubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pTravelingModeIni, "STORAGEMainInput:bHDMI2SubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pTravelingModeIni, "STORAGEMainInput:bHDMI3SubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_STORAGE][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pTravelingModeIni, "STORAGEMainInput:bHDMI4SubInput", 0);
    //ATV
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pTravelingModeIni, "ATVMainInput:bDTVSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pTravelingModeIni, "ATVMainInput:bSTORAGESubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pTravelingModeIni, "ATVMainInput:bATVSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pTravelingModeIni, "ATVMainInput:bCVBSSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pTravelingModeIni, "ATVMainInput:bYPBPRSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pTravelingModeIni, "ATVMainInput:bVGASubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pTravelingModeIni, "ATVMainInput:bHDMISubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pTravelingModeIni, "ATVMainInput:bHDMI2SubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pTravelingModeIni, "ATVMainInput:bHDMI3SubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_ATV][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pTravelingModeIni, "ATVMainInput:bHDMI4SubInput", 0);
    //CVBS
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pTravelingModeIni, "CVBSMainInput:bDTVSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pTravelingModeIni, "CVBSMainInput:bSTORAGESubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pTravelingModeIni, "CVBSMainInput:bATVSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pTravelingModeIni, "CVBSMainInput:bCVBSSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pTravelingModeIni, "CVBSMainInput:bYPBPRSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pTravelingModeIni, "CVBSMainInput:bVGASubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pTravelingModeIni, "CVBSMainInput:bHDMISubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pTravelingModeIni, "CVBSMainInput:bHDMI2SubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pTravelingModeIni, "CVBSMainInput:bHDMI3SubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_CVBS][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pTravelingModeIni, "CVBSMainInput:bHDMI4SubInput", 0);
    //YPbPr
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pTravelingModeIni, "YPBPRMainInput:bDTVSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pTravelingModeIni, "YPBPRMainInput:bSTORAGESubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pTravelingModeIni, "YPBPRMainInput:bATVSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pTravelingModeIni, "YPBPRMainInput:bCVBSSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pTravelingModeIni, "YPBPRMainInput:bYPBPRSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pTravelingModeIni, "YPBPRMainInput:bVGASubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pTravelingModeIni, "YPBPRMainInput:bHDMISubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pTravelingModeIni, "YPBPRMainInput:bHDMI2SubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pTravelingModeIni, "YPBPRMainInput:bHDMI3SubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_YPBPR][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pTravelingModeIni, "YPBPRMainInput:bHDMI4SubInput", 0);
    //VGA
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pTravelingModeIni, "VGAMainInput:bDTVSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pTravelingModeIni, "VGAMainInput:bSTORAGESubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pTravelingModeIni, "VGAMainInput:bATVSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pTravelingModeIni, "VGAMainInput:bCVBSSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pTravelingModeIni, "VGAMainInput:bYPBPRSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pTravelingModeIni, "VGAMainInput:bVGASubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pTravelingModeIni, "VGAMainInput:bHDMISubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pTravelingModeIni, "VGAMainInput:bHDMI2SubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pTravelingModeIni, "VGAMainInput:bHDMI3SubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_VGA][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pTravelingModeIni, "VGAMainInput:bHDMI4SubInput", 0);
    //HDMI
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pTravelingModeIni, "HDMIMainInput:bDTVSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pTravelingModeIni, "HDMIMainInput:bSTORAGESubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pTravelingModeIni, "HDMIMainInput:bATVSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pTravelingModeIni, "HDMIMainInput:bCVBSSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pTravelingModeIni, "HDMIMainInput:bYPBPRSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pTravelingModeIni, "HDMIMainInput:bVGASubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pTravelingModeIni, "HDMIMainInput:bHDMISubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pTravelingModeIni, "HDMIMainInput:bHDMI2SubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pTravelingModeIni, "HDMIMainInput:bHDMI3SubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pTravelingModeIni, "HDMIMainInput:bHDMI4SubInput", 0);
    //HDMI2
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pTravelingModeIni, "HDMI2MainInput:bDTVSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pTravelingModeIni, "HDMI2MainInput:bSTORAGESubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pTravelingModeIni, "HDMI2MainInput:bATVSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pTravelingModeIni, "HDMI2MainInput:bCVBSSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pTravelingModeIni, "HDMI2MainInput:bYPBPRSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pTravelingModeIni, "HDMI2MainInput:bVGASubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pTravelingModeIni, "HDMI2MainInput:bHDMISubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pTravelingModeIni, "HDMI2MainInput:bHDMI2SubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pTravelingModeIni, "HDMI2MainInput:bHDMI3SubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI2][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pTravelingModeIni, "HDMI2MainInput:bHDMI4SubInput", 0);
    //HDMI3
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pTravelingModeIni, "HDMI3MainInput:bDTVSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pTravelingModeIni, "HDMI3MainInput:bSTORAGESubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pTravelingModeIni, "HDMI3MainInput:bATVSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pTravelingModeIni, "HDMI3MainInput:bCVBSSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pTravelingModeIni, "HDMI3MainInput:bYPBPRSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pTravelingModeIni, "HDMI3MainInput:bVGASubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pTravelingModeIni, "HDMI3MainInput:bHDMISubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pTravelingModeIni, "HDMI3MainInput:bHDMI2SubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pTravelingModeIni, "HDMI3MainInput:bHDMI3SubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI3][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pTravelingModeIni, "HDMI3MainInput:bHDMI4SubInput", 0);
    //HDMI4
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_DTV2] = iniparser_getboolean(m_pTravelingModeIni, "HDMI4MainInput:bDTVSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_STORAGE] = iniparser_getboolean(m_pTravelingModeIni, "HDMI4MainInput:bSTORAGESubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_ATV] = iniparser_getboolean(m_pTravelingModeIni, "HDMI4MainInput:bATVSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_CVBS] = iniparser_getboolean(m_pTravelingModeIni, "HDMI4MainInput:bCVBSSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_YPBPR] = iniparser_getboolean(m_pTravelingModeIni, "HDMI4MainInput:bYPBPRSubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_VGA] = iniparser_getboolean(m_pTravelingModeIni, "HDMI4MainInput:bVGASubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_HDMI] = iniparser_getboolean(m_pTravelingModeIni, "HDMI4MainInput:bHDMISubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_HDMI2] = iniparser_getboolean(m_pTravelingModeIni, "HDMI4MainInput:bHDMI2SubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_HDMI3] = iniparser_getboolean(m_pTravelingModeIni, "HDMI4MainInput:bHDMI3SubInput", 0);
    SysIniBlock.bTravelingPairInfo[MAPI_INPUT_SOURCE_HDMI4][MAPI_INPUT_SOURCE_HDMI4] = iniparser_getboolean(m_pTravelingModeIni, "HDMI4MainInput:bHDMI4SubInput", 0);
    return TRUE;
}
#endif //Traveling
#endif //PIP

MAPI_BOOL SystemInfo::ReadTunerModeTable(void)
{
    if (m_pTunerModeIni == NULL)
    {
        //Default support
        vector<string> pushArray;

#if (STB_ENABLE == 0)
        pushArray.push_back("ATV");
#endif

        if ((SystemInfo::IsSupportTheDTVSystemType(DVBT_ENABLE) == TRUE) ||
            (SystemInfo::IsSupportTheDTVSystemType(DVBT2_ENABLE) == TRUE))
        {
            pushArray.push_back("DVBT");
            pushArray.push_back("DVBT2");
        }
        if ((SystemInfo::IsSupportTheDTVSystemType(DVBS_ENABLE) == TRUE) ||
            (SystemInfo::IsSupportTheDTVSystemType(DVBS2_ENABLE) == TRUE))
        {
            pushArray.push_back("DVBS");
            pushArray.push_back("DVBS2");
        }
        if (SystemInfo::IsSupportTheDTVSystemType(DVBC_ENABLE) == TRUE)
        {
            pushArray.push_back("DVBC");
        }
        if (SystemInfo::IsSupportTheDTVSystemType(ISDB_ENABLE) == TRUE)
        {
            pushArray.push_back("ISDB");
        }
        if (SystemInfo::IsSupportTheDTVSystemType(DTMB_ENABLE) == TRUE)
        {
            pushArray.push_back("DTMB");
        }
        if (SystemInfo::IsSupportTheDTVSystemType(ATSC_ENABLE) == TRUE)
        {
            pushArray.push_back("ATSC");
        }

        m_TunerModelTable.push_back(pushArray);
        return TRUE;
    }

#if (TWIN_TUNER == 1)
    int tunerNum = iniparser_getint(m_pTunerModeIni, "TwinTunerMode:TunerNum", 0);
#else
    int tunerNum = iniparser_getint(m_pTunerModeIni, "TunerMode:TunerNum", 0);
#endif

    for (int i = 1; i <= tunerNum; i++)
    {
        char tempNum[10];
        sprintf(tempNum, "%d", i);
        string numString(tempNum);
#if (TWIN_TUNER == 1)
        string tunerName = "TwinTunerMode:Tuner" + numString;
#else
        string tunerName = "TunerMode:Tuner" + numString;
#endif
        const char *pString = iniparser_getstr(m_pTunerModeIni, tunerName.c_str());
        vector<string*> strArray;
        ParserStringToArray(pString, strArray);

        if (strArray.size() == 0)
        {
            continue;
        }

        vector<string> pushArray;

        for (unsigned int j = 0; j < strArray.size(); j++)
        {
            pushArray.push_back(*strArray[j]);
            delete strArray[j];
        }

        m_TunerModelTable.push_back(pushArray);
    }
    return TRUE;
}

MAPI_U8 SystemInfo::GetTunersCount(void) const
{
    return m_TunerModelTable.size();
}

vector< vector<string> > SystemInfo::GetTunerModeTable(void) const
{
    return m_TunerModelTable;
}

MAPI_BOOL SystemInfo::IsDTVRouteConflict(vector<EN_DTV_TYPE> dtvRouteSet) const
{
    vector<MAPI_BOOL> tunerConflictTable;
    tunerConflictTable.resize(m_TunerModelTable.size());

    for (unsigned int i = 0; i < tunerConflictTable.size(); i++)
    {
        tunerConflictTable[i] = FALSE;
    }

    for (unsigned int i = 0; i < dtvRouteSet.size(); i++)
    {
        MAPI_BOOL findedSupportTuner = FALSE;
        for (unsigned int j = 0; j < m_TunerModelTable.size(); j++)
        {
            if (tunerConflictTable[j] == TRUE)
            {
                continue;
            }

            for (unsigned int k = 0; k < m_TunerModelTable[j].size(); k++)
            {
                //ATV skip out
                if (m_TunerModelTable[j][k] == "ATV")
                {
                    continue;
                }

                if (dtvRouteSet[i] == GetDTVRouteEnumByString(m_TunerModelTable[j][k]))
                {
                    findedSupportTuner = TRUE;
                    tunerConflictTable[j] = TRUE;
                    break;
                }
            }

            if (findedSupportTuner == TRUE)
            {
                break;
            }
        }

        if (findedSupportTuner == FALSE)
        {
            return TRUE;
        }
    }
    return FALSE;
}

EN_DTV_TYPE SystemInfo::GetDTVRouteEnumByString(string dtvRouteString) const
{
    if (dtvRouteString == "DVBT")
    {
        return DTV_TYPE_DVBT;
    }
    else if (dtvRouteString == "DVBC")
    {
        return DTV_TYPE_DVBC;
    }
    else if (dtvRouteString == "DVBS")
    {
        return DTV_TYPE_DVBS;
    }
    else if (dtvRouteString == "DVBT2")
    {
        return DTV_TYPE_DVBT2;
    }
    else if (dtvRouteString == "DVBS2")
    {
        return DTV_TYPE_DVBS2;
    }
    else if (dtvRouteString == "DTMB")
    {
        return DTV_TYPE_DTMB;
    }
    else if (dtvRouteString == "ATSC")
    {
        return DTV_TYPE_ATSC;
    }
    else if (dtvRouteString == "ISDB")
    {
        return DTV_TYPE_ISDB;
    }
    else
    {
        ASSERT(0);
    }

    return DTV_TYPE_UNKNOW;
}

EN_DTV_TYPE SystemInfo::GetDTVRouteEnumByRouteIndex(MAPI_U8 u8RouteIndex) const
{
    switch (SystemInfo::GetInstance()->GetRouteTVMode(u8RouteIndex))
    {
        case E_ROUTE_DVBT:
            return DTV_TYPE_DVBT;
            break;
        case E_ROUTE_DVBC:
            return DTV_TYPE_DVBC;
            break;
        case E_ROUTE_DVBS:
            return DTV_TYPE_DVBS;
            break;
        case E_ROUTE_DVBT2:
            return DTV_TYPE_DVBT2;
            break;
        case E_ROUTE_DVBS2:
            return DTV_TYPE_DVBS2;
            break;
        case E_ROUTE_DTMB:
            return DTV_TYPE_DTMB;
            break;
        case E_ROUTE_ATSC:
            return DTV_TYPE_ATSC;
            break;
        case E_ROUTE_ISDB:
            return DTV_TYPE_ISDB;
            break;
        default:
            break;
    }
    return DTV_TYPE_UNKNOW;
}

void SystemInfo::LoadIniFile(void)
{
#if(PIP_ENABLE == 1)
    char *pTravelingModeName = NULL;
#endif
#if (SQL_DB_ENABLE == 1)
    char *pDBName=NULL;
#endif
#if (STB_ENABLE == 0)
    char *pPcModeName=NULL;
#endif
    char *pDLCName=NULL;
    char *pMatrixName=NULL;
    char *pBoardName = NULL;
#if ( INTEL_WIDI_ENABLE == 1 )
    char *pWidiName = NULL;
#endif

#if ( INTEL_WIDI_ENABLE == 1 )
    pWidiName = iniparser_getstr(m_pCustomerini, "widi:m_pWidiName");
    ASSERT(pWidiName);
    m_pWidiini = iniparser_load(pWidiName);
    ASSERT(m_pWidiini);
#endif

    // Default panel
    SYSTEM_INFO_FLOW("Load default panel.\n");
    char *pPanelName =iniparser_getstr(m_pCustomerini, "panel:m_pPanelName");
    SYSTEM_INFO_IFO("Default panel file is %s.\n", pPanelName);
    ASSERT(pPanelName);
    m_pPanelini = iniparser_load(pPanelName);
    ASSERT(m_pPanelini);

    m_enActivePanelTiming = E_MAPI_TIMING_DEFAULT;

    for (int i = 0 ; i<PANEL_NAME_LIST_SIZE ; i++)
    {
        if (iniparser_getstr(m_pCustomerini, g_PanelPath[i].cName) != NULL)
        {
            SYSTEM_INFO_FLOW("Load timing %x, panel ini is %s.\n", g_PanelPath[i].enTiming, g_PanelPath[i].cName);

            m_PanelPathMap[g_PanelPath[i].enTiming] = iniparser_getstr(m_pCustomerini, g_PanelPath[i].cName);
        }
    }

    CreateEmptyPanelInfo(m_PanelPathMap.size()+1);

    SYSTEM_INFO_IFO("Total panel count is %d. (Exception default panel)", m_PanelPathMap.size());

    //board
    pBoardName =iniparser_getstr(m_pCustomerini, "board:m_pBoardName");
    ASSERT(pBoardName);
    m_pBoardini = iniparser_load(pBoardName);
    ASSERT(m_pBoardini);

    //Keep Board ini file name
    m_strBoardIniFileName = pBoardName;

    //DLC
    m_pDCLIni = iniparser_load((char *)DLC_INI_UPDATE_PATH);
    if(m_pDCLIni == NULL)
    {
        pDLCName  =iniparser_getstr(m_pCustomerini, "DLC:m_pDLCName");
        ASSERT(pDLCName);
        m_pDCLIni = iniparser_load(pDLCName);
        ASSERT(m_pDCLIni);
    }

    //color matrix
    m_pMatrixIni = iniparser_load((char *)COLORMATRIX_INI_UPDATE_PATH);
    if(m_pMatrixIni == NULL)
    {
        pMatrixName  =iniparser_getstr(m_pCustomerini , "ColorMatrix:MatrixName");
        ASSERT(pMatrixName);
        m_pMatrixIni = iniparser_load(pMatrixName);
        ASSERT(m_pMatrixIni);
    }

#if (STB_ENABLE == 0)
    // pc mode table
#if ENABLE_RGB_SUPPORT_85HZ
    pPcModeName = iniparser_getstr(m_pCustomerini, "PcModeSupport85HZ:m_pPcModeTable");
#else
    pPcModeName = iniparser_getstr(m_pCustomerini, "PcModeTable:m_pPcModeTable");
#endif
    ASSERT(pPcModeName);
    m_pPcModeIni = iniparser_load(pPcModeName);
    ASSERT(m_pPcModeIni);
#endif    //#if (STB_ENABLE == 0)

#if (PIP_ENABLE == 1)
    pTravelingModeName = iniparser_getstr(m_pCustomerini, "Pip:TravelingTableName");
    m_pTravelingModeIni = iniparser_load(pTravelingModeName);
#if (TRAVELING_ENABLE == 1)
    SetTravelingInfoSet();
#endif
#endif

#if (SQL_DB_ENABLE == 1)
    pDBName  =iniparser_getstr(m_pCustomerini, "DB:m_pDBName");
    ASSERT(pDBName);
    m_pDBIni = iniparser_load(pDBName);
    ASSERT(m_pDBIni);
    SystemInfo::SetSQLDBdata();
#endif

#if 0
    //wait SDK CL 568471 release
    MAPI_SECTION_FILTER_CONFIG demuxSectionFilterCfg = {0};
    demuxSectionFilterCfg.u16Section1kFilterNumber = iniparser_getunsignedint(m_pCustomerini, "TSP:Section1kFilterNumber", 0);
    demuxSectionFilterCfg.u16Section4kFilterNumber = iniparser_getunsignedint(m_pCustomerini, "TSP:Section4kFilterNumber", 0);
    demuxSectionFilterCfg.u16Section64kFilterNumber = iniparser_getunsignedint(m_pCustomerini, "TSP:Section64kFilterNumber", 0);
    SystemInfo::SetTspSectionFilterConfig(demuxSectionFilterCfg);
#endif
}

void SystemInfo::FreeIniFile(void)
{

#if (STB_ENABLE == 0)
    if(m_pSystemini!=NULL)
    {
        iniparser_freedict(m_pSystemini);
        m_pSystemini=NULL;
    }

    if(m_pPanelini!=NULL)
    {
        iniparser_freedict(m_pPanelini);
        m_pPanelini=NULL;
    }

    if(m_pBoardini!=NULL)
    {
        iniparser_freedict(m_pBoardini);
        m_pBoardini=NULL;
    }

    if(m_pDCLIni!=NULL)
    {
        iniparser_freedict(m_pDCLIni);
        m_pDCLIni=NULL;
    }

    if(m_pMatrixIni!=NULL)
    {
        iniparser_freedict(m_pMatrixIni);
        m_pMatrixIni=NULL;
    }
    if(m_pPcModeIni!=NULL)
    {
        iniparser_freedict(m_pPcModeIni);
        m_pPcModeIni=NULL;
    }
#endif

#if (PIP_ENABLE == 1)
    if(m_pPipModeIni!=NULL)
    {
        iniparser_freedict(m_pPipModeIni);
        m_pPipModeIni = NULL;
    }
    if(m_pPopModeIni!=NULL)
    {
        iniparser_freedict(m_pPopModeIni);
        m_pPopModeIni = NULL;
    }
    if(m_pTravelingModeIni!=NULL)
    {
        iniparser_freedict(m_pTravelingModeIni);
        m_pTravelingModeIni = NULL;
    }
#endif

    if (m_pTunerModeIni != NULL)
    {
        iniparser_freedict(m_pTunerModeIni);
        m_pTunerModeIni = NULL;
    }

#if (SQL_DB_ENABLE == 1)
    if(m_pDBIni!=NULL)
    {
        iniparser_freedict(m_pDBIni);
        m_pDBIni = NULL;
    }
#endif
}

MAPI_BOOL SystemInfo::GetModelIni(dictionary * pSysteminiUpdateData, char * pRetModelName, const MAPI_U16 u16ModelNameLen)
{
    MAPI_BOOL bSelectModelViaProjectID = FALSE;
    char TempBuffer[MAX_BUFFER+1]={0};
    char * pModelName =NULL;
    bSelectModelViaProjectID = iniparser_getboolean(pSysteminiUpdateData, "select_model_via_project_id:bEnabled",0);

    if(bSelectModelViaProjectID == TRUE)
    {
        if((m_u16SpiProjectID == 0x0) || (m_u16SpiProjectID == 0xFFFF))
        {
            SYSTEM_INFO_ERR("ERROR: The project id is wrong.\n");
            return FALSE;
        }
        /* Read the model name */
        snprintf(TempBuffer,MAX_BUFFER,"select_model_via_project_id:Model_%d",m_u16SpiProjectID);
        pModelName = iniparser_getstr(pSysteminiUpdateData, TempBuffer);
        if(pModelName == NULL)
        {
            SYSTEM_INFO_ERR("ERROR: The model name is empty.\n");
            return FALSE;
        }
    }
    else
    {
        /* Read the model name */
        pModelName = iniparser_getstr(pSysteminiUpdateData, "model:gModelName");
        if(pModelName == NULL)
        {
            SYSTEM_INFO_ERR("ERROR: The model name is empty.\n");
            return FALSE;
        }
    }

    memset(pRetModelName ,0 ,u16ModelNameLen);
    strncpy(pRetModelName, pModelName,strlen(pModelName));

    SYSTEM_INFO_IFO("SystemInfo::GetModelName, pModelName =%s\n", pModelName);

    return TRUE;
}

MAPI_BOOL SystemInfo::GetModelName(dictionary * pSysteminiUpdateData, char * pRetModelName, const MAPI_U16 u16ModelNameLen)
{
    printf("%s: Warning: Please use 'GetModelIni()' instead of 'GetModelName()', at %d\n", __func__, __LINE__);
    return GetModelIni(pSysteminiUpdateData, pRetModelName, u16ModelNameLen);
}

MAPI_BOOL SystemInfo::GetModelName(MAPI_U16 u16ProjectId, char * pRetModelName, const MAPI_U16 u16ModelNameLen)
{
    return _GetCustomerModelFeature("Name", u16ProjectId, pRetModelName, u16ModelNameLen);
}

MAPI_BOOL SystemInfo::GetModelDescription(MAPI_U16 u16ProjectId, char * pModelDes, const MAPI_U16 pModelDesLen)
{
    return _GetCustomerModelFeature("Description", u16ProjectId, pModelDes, pModelDesLen);
}

MAPI_BOOL SystemInfo::_GetCustomerModelFeature(const char* pStr, MAPI_U16 u16ProjectId, char * pRetStr, const MAPI_U16 u16StrLen)
{
    char TempBuffer[MAX_BUFFER+1]={0};
    char *pModelIni = NULL;
    char *pModelName = NULL;
    dictionary *pCustomerini=NULL;
    dictionary *pSystemini=NULL;
    MAPI_BOOL bSelectModelViaProjectID = FALSE;
    MAPI_BOOL bRet = FALSE;

    if((u16ProjectId == 0x0) || (u16ProjectId == 0xFFFF))
    {
        SYSTEM_INFO_ERR("ERROR: The project id(0x%04X) is wrong.\n", u16ProjectId);
        goto END;
    }

    if(NULL == pStr)
    {
        SYSTEM_INFO_ERR("ERROR: pStr is empty.\n");
        goto END;
    }

    pSystemini = iniparser_load(SYS_INI_PATH_FILENAME);
    ASSERT(pSystemini != NULL);

    /* Judge which customer ini to use */
    bSelectModelViaProjectID = (MAPI_BOOL)Get_bSelectModelViaProjectID();
    if(TRUE == bSelectModelViaProjectID)
    {
        snprintf(TempBuffer,MAX_BUFFER,"select_model_via_project_id:Model_%d", u16ProjectId);
    }
    else
    {
        snprintf(TempBuffer,MAX_BUFFER,"model:gModelName");
    }

    /* Read the path of customer ini */
    pModelIni = iniparser_getstr(pSystemini, TempBuffer);
    if(pModelIni == NULL)
    {
        SYSTEM_INFO_ERR("ERROR: The model name is empty.\n");
        goto END;
    }

    /* Load the customer ini */
    if(pCustomerini == NULL)
    {
        pCustomerini = iniparser_load(pModelIni);
        ASSERT(pCustomerini != NULL);
        /* Read the model name in customer ini */
        memset(TempBuffer, 0, sizeof(TempBuffer));
        snprintf(TempBuffer,MAX_BUFFER,"Model:%s", pStr);
        pModelName = iniparser_getstr(pCustomerini, TempBuffer);
        if(pModelName == NULL)
        {
            SYSTEM_INFO_ERR("ERROR: The '%s' in Model is empty.\n", pStr);
            goto END;
        }
        memset(pRetStr ,0 ,u16StrLen);
        strncpy(pRetStr, pModelName, strlen(pModelName));
        bRet = TRUE;
    }

END:
    if(NULL != pCustomerini)
    {
        iniparser_freedict(pCustomerini);
        pCustomerini = NULL;
    }

    if(NULL != pSystemini)
    {
        iniparser_freedict(pSystemini);
        pSystemini = NULL;
    }
    return bRet;
}

MAPI_BOOL SystemInfo::SetAMPInfo(void)
{
    char *AmpBinPath = NULL;
    AmpBinPath = iniparser_getstring(m_pCustomerini, "AudioAmp:AmpBinPath", NULL);
    /* check the Gamma file */
    if(AmpBinPath != NULL)
    {
        SYSTEM_INFO_IFO("AmpBinPath = %s",AmpBinPath);
        pAmpInitBinPath = (char*)AmpBinPath;
    }
    return TRUE;

}
MAPI_BOOL SystemInfo::SetVideoZoomInfo(void)
{
    SystemInfo::SetVideoZoomInfo(MAPI_INPUT_SOURCE_NONE, stDefaultZoomInfo);
    SystemInfo::SetVideoZoomInfo(MAPI_INPUT_SOURCE_CVBS, stCvbsZoomInfo);
    SystemInfo::SetVideoZoomInfo(MAPI_INPUT_SOURCE_YPBPR, stYpbprZoomInfo);
    SystemInfo::SetVideoZoomInfo(MAPI_INPUT_SOURCE_HDMI, stHdmiZoomInfo);
    SystemInfo::SetVideoZoomInfo(MAPI_INPUT_SOURCE_DTV, stDtvZoomInfo);
    return TRUE;
}

EN_MODULE_PARAMETER_STATUS SystemInfo::GetModuleParameter_bool(const char * feature, MAPI_BOOL *enable)
{
    int bRetInCustomize = 0;
    int bRetInDefault = 0;

    if(feature == NULL)
        return E_FAIL_TO_FIND;

    if(m_bLoadIniStatus == TRUE)
        return E_INI_NOT_READY;

    pthread_mutex_lock(&m_moduleParameter_mutex);

    if((m_pModuleIni == NULL) || (m_pModuleDefaultIni == NULL))
    {
        LoadModuleIniFile();
    }

    bRetInCustomize = iniparser_getboolean(m_pModuleIni, feature, -1);

    if(bRetInCustomize == -1)
    {
        bRetInDefault = iniparser_getboolean(m_pModuleDefaultIni, feature, -1);

        if(bRetInDefault == -1)
        {
            *enable = MAPI_FALSE;
            pthread_mutex_unlock(&m_moduleParameter_mutex);
            return E_FAIL_TO_FIND;
        }
        else
        {
            if (bRetInDefault == 1)
            {
                *enable = MAPI_TRUE;
            }
            else
            {
                *enable = MAPI_FALSE;
            }
            pthread_mutex_unlock(&m_moduleParameter_mutex);
            return E_FIND_IN_DEFAULT;
        }
    }
    else
    {
        if (bRetInCustomize == 1)
        {
            *enable = MAPI_TRUE;
        }
        else
        {
            *enable = MAPI_FALSE;
        }
        pthread_mutex_unlock(&m_moduleParameter_mutex);
        return E_FIND_IN_CUSTOMIZE;
    }
}

EN_MODULE_PARAMETER_STATUS SystemInfo::GetModuleParameter_int(const char * feature, int *value, int notfound)
{
    int retInCustomize = 0;
    int retInDefault = 0;

    if(feature == NULL)
        return E_FAIL_TO_FIND;

    if(m_bLoadIniStatus == TRUE)
        return E_INI_NOT_READY;

    pthread_mutex_lock(&m_moduleParameter_mutex);

    if((m_pModuleIni == NULL) || (m_pModuleDefaultIni == NULL))
    {
        LoadModuleIniFile();
    }

    retInCustomize = iniparser_getint(m_pModuleIni, feature, -1);

    if(retInCustomize == -1)
    {
        retInDefault = iniparser_getint(m_pModuleDefaultIni, feature, -1);

        if(retInDefault == -1)
        {
            *value = notfound;
            pthread_mutex_unlock(&m_moduleParameter_mutex);
            return E_FAIL_TO_FIND;
        }
        else
        {
            *value = retInDefault;
            pthread_mutex_unlock(&m_moduleParameter_mutex);
            return E_FIND_IN_DEFAULT;
        }
    }
    else
    {
        *value = retInCustomize;
        pthread_mutex_unlock(&m_moduleParameter_mutex);
        return E_FIND_IN_CUSTOMIZE;
    }
}

EN_MODULE_PARAMETER_STATUS SystemInfo::GetModuleParameter_string(const char * feature, char *strValue, const MAPI_U16 strLenth)
{
    char* retInCustomize = NULL;
    char* retInDefault = NULL;

    if(feature == NULL)
        return E_FAIL_TO_FIND;

    if(m_bLoadIniStatus == TRUE)
        return E_INI_NOT_READY;

    pthread_mutex_lock(&m_moduleParameter_mutex);

    if((m_pModuleIni == NULL) || (m_pModuleDefaultIni == NULL))
    {
        LoadModuleIniFile();
    }

    retInCustomize = iniparser_getstr(m_pModuleIni, feature);

    if(retInCustomize == NULL)
    {
        retInDefault = iniparser_getstr(m_pModuleDefaultIni, feature);

        if(retInDefault == NULL)
        {
            strValue = NULL;
            pthread_mutex_unlock(&m_moduleParameter_mutex);
            return E_FAIL_TO_FIND;
        }
        else
        {
            memset(strValue ,0 , strLenth);
            strncpy(strValue, retInDefault, strLenth);
            pthread_mutex_unlock(&m_moduleParameter_mutex);
            return E_FIND_IN_DEFAULT;
        }
    }
    else
    {
        memset(strValue ,0 , strLenth);
        strncpy(strValue, retInCustomize, strLenth);
        pthread_mutex_unlock(&m_moduleParameter_mutex);
        return E_FIND_IN_CUSTOMIZE;
    }
}

EN_MODULE_PARAMETER_STATUS SystemInfo::GetModuleParameter_U8array(const char * feature, MAPI_U8 *dataArray, const MAPI_U16 arraySize)
{
    MAPI_BOOL retInCustomize = FALSE;
    MAPI_BOOL retInDefault = FALSE;

    if(feature == NULL)
        return E_FAIL_TO_FIND;

    if(m_bLoadIniStatus == TRUE)
        return E_INI_NOT_READY;

    pthread_mutex_lock(&m_moduleParameter_mutex);

    if((m_pModuleIni == NULL) || (m_pModuleDefaultIni == NULL))
    {
        LoadModuleIniFile();
    }

    retInCustomize = iniparser_getU8array(m_pModuleIni, feature, arraySize, dataArray);

    if(retInCustomize == FALSE)
    {
        retInDefault = iniparser_getU8array(m_pModuleDefaultIni, feature, arraySize, dataArray);

        if(retInDefault == FALSE)
        {
            pthread_mutex_unlock(&m_moduleParameter_mutex);
            return E_FAIL_TO_FIND;
        }
        else
        {
            pthread_mutex_unlock(&m_moduleParameter_mutex);
            return E_FIND_IN_DEFAULT;
        }
    }
    else
    {
        pthread_mutex_unlock(&m_moduleParameter_mutex);
        return E_FIND_IN_CUSTOMIZE;
    }
}

void SystemInfo::LoadModuleIniFile(void)
{
    char *pModuleDefaultName = NULL;
    char *pModuleName = NULL;

    m_bLoadIniStatus = TRUE;

    if(m_pCustomerini == NULL)
    {
        m_pSystemini = iniparser_load(SYS_INI_PATH_FILENAME);
        ASSERT(m_pSystemini);
        unsigned char bSelectModelViaProjectID = FALSE;
        bSelectModelViaProjectID = iniparser_getboolean(m_pSystemini, "select_model_via_project_id:bEnabled",0);
        if(bSelectModelViaProjectID == TRUE)
        {
            m_u16SpiProjectID = GetProjectId();
            Set_SpiProjectID(m_u16SpiProjectID);
        }
        else
        {
            //customer
            m_pModelName = iniparser_getstr(m_pSystemini, "model:gModelName");
            ASSERT(m_pModelName);
            m_pCustomerini = iniparser_load(m_pModelName);
            ASSERT(m_pCustomerini);
        }
    }

    //default module parameter
    pModuleDefaultName  = iniparser_getstr(m_pCustomerini , "module:m_pModuleDefaultName");
    ASSERT(pModuleDefaultName);
    m_pModuleDefaultIni = iniparser_load(pModuleDefaultName);
    ASSERT(m_pModuleDefaultIni);

    //module parameter
    pModuleName  = iniparser_getstr(m_pCustomerini , "module:m_pModuleName");
    ASSERT(pModuleName);
    m_pModuleIni = iniparser_load(pModuleName);
    ASSERT(m_pModuleIni);

    m_bLoadIniStatus = FALSE;
}

MAPI_BOOL SystemInfo::SetAdditionalModeInfo(EN_SupportPanelType ePanelType)
{
    SYSTEM_INFO_ERR("This function doesn't support any more");
    return MAPI_FALSE;
}

MAPI_BOOL SystemInfo::IsPanel4K2KModeNeedCmd(void)
{
    MAPI_BOOL bNeed = FALSE;

    bNeed = SysIniBlock.bPanel4K2KModeNeedCmd;
    return bNeed;
}

void SystemInfo::ParserStringToArray(const char *inputStr, vector<string*> &outputStrArray)
{
    int leftIndex = -1, rightIndex = -2;
    unsigned int i;

    for (i = 0; i < strlen(inputStr); i++)
    {
        if (*(inputStr+i) == '{')
        {
            leftIndex = i + 1;
            break;
        }
    }

    for (i = strlen(inputStr)-1; i >= 0; i--)
    {
        if (*(inputStr+i) == '}')
        {
            rightIndex = i - 1;
            break;
        }
    }

    if (leftIndex > rightIndex)
    {
        return;
    }

    int len = rightIndex - leftIndex + 1;
    char *str = new char[len + 1];
    strncpy(str, inputStr + leftIndex, len);
    str[len] = '\0';

    char *pch = strtok(str, " ,");
    while (pch != NULL)
    {
        string *tmpStr = new string(pch);

        if (tmpStr == NULL)
        {
            ASSERT(0);
        }

        outputStrArray.push_back(tmpStr);
        pch = strtok(NULL, " ,");
    }

    delete[] str;
}

#if (MSTAR_TVOS == 1)
MAPI_BOOL SystemInfo::SetHDMI_HdcpEnable(void)
{
    MAPI_BOOL bEnableHdcp[4] = {MAPI_FALSE,MAPI_FALSE,MAPI_FALSE,MAPI_FALSE};
    if(m_pCustomerini == NULL)
    {
        SYSTEM_INFO_ERR("ERROR: Load %s error.\n", SysIniBlock.ModelName);
        ASSERT(0);
        return FALSE;
    }

    bEnableHdcp[0] = iniparser_getboolean(m_pCustomerini, "StorageHDCP:Hdcp_Hdmi1_Enable", TRUE);
    bEnableHdcp[1] = iniparser_getboolean(m_pCustomerini, "StorageHDCP:Hdcp_Hdmi2_Enable", TRUE);
    bEnableHdcp[2] = iniparser_getboolean(m_pCustomerini, "StorageHDCP:Hdcp_Hdmi3_Enable", TRUE);
    bEnableHdcp[3] = iniparser_getboolean(m_pCustomerini, "StorageHDCP:Hdcp_Hdmi4_Enable", TRUE);

    for(int Idx = 0;Idx < HDMI_PORT_MAX; Idx++)
    {
        m_bEnableHdcp[Idx] = bEnableHdcp[Idx];
    }
    return TRUE;
}
#endif

void _SDK2DriverPanelTypeInfoTrans(const MAPI_PanelType* const pApiPanelType, PanelType* pDriverPanelType)
{
    // MAPI_APIPNL_LINK_TYPE V.S. APIPNL_LINK_TYPE
    STATIC_ASSERT((int)E_MAPI_LINK_TTL == (int)LINK_TTL);
    STATIC_ASSERT((int)E_MAPI_LINK_LVDS == (int)LINK_LVDS);
    STATIC_ASSERT((int)E_MAPI_LINK_RSDS == (int)LINK_RSDS);
    STATIC_ASSERT((int)E_MAPI_LINK_MINILVDS == (int)LINK_MINILVDS);
    STATIC_ASSERT((int)E_MAPI_LINK_ANALOG_MINILVDS == (int)LINK_ANALOG_MINILVDS);
    STATIC_ASSERT((int)E_MAPI_LINK_DIGITAL_MINILVDS == (int)LINK_DIGITAL_MINILVDS);
    STATIC_ASSERT((int)E_MAPI_LINK_MFC == (int)LINK_MFC);
    STATIC_ASSERT((int)E_MAPI_LINK_DAC_I == (int)LINK_DAC_I);
    STATIC_ASSERT((int)E_MAPI_LINK_DAC_P == (int)LINK_DAC_P);
    STATIC_ASSERT((int)E_MAPI_LINK_PDPLVDS == (int)LINK_PDPLVDS);
    STATIC_ASSERT((int)E_MAPI_LINK_EXT == (int)LINK_EXT);

    // MAPI_PNL_ASPECT_RATIO V.S. E_PNL_ASPECT_RATIO
    STATIC_ASSERT((int)E_MAPI_PNL_ASPECT_RATIO_4_3 == (int)E_PNL_ASPECT_RATIO_4_3);
    STATIC_ASSERT((int)E_MAPI_PNL_ASPECT_RATIO_WIDE == (int)E_PNL_ASPECT_RATIO_WIDE);
    STATIC_ASSERT((int)E_MAPI_PNL_ASPECT_RATIO_OTHER == (int)E_PNL_ASPECT_RATIO_OTHER);

    // MAPI_APIPNL_TIBITMODE V.S. APIPNL_TIBITMODE
    STATIC_ASSERT((int)E_MAPI_TI_10BIT_MODE == (int)TI_10BIT_MODE);
    STATIC_ASSERT((int)E_MAPI_TI_8BIT_MODE == (int)TI_8BIT_MODE);
    STATIC_ASSERT((int)E_MAPI_TI_6BIT_MODE == (int)TI_6BIT_MODE);

    if ((NULL != pApiPanelType) && (NULL != pDriverPanelType))
    {
        pDriverPanelType->m_bPanelDither      = pApiPanelType->bPanelDither;

        // need change later
        pDriverPanelType->m_ePanelLinkType    = (APIPNL_LINK_TYPE)pApiPanelType->ePanelLinkType;

        pDriverPanelType->m_bPanelDualPort    = pApiPanelType->bPanelDualPort;
        pDriverPanelType->m_bPanelSwapPort    = pApiPanelType->bPanelSwapPort;
        pDriverPanelType->m_bPanelSwapOdd_ML  = pApiPanelType->bPanelSwapOdd_ML;
        pDriverPanelType->m_bPanelSwapEven_ML = pApiPanelType->bPanelSwapEven_ML;
        pDriverPanelType->m_bPanelSwapOdd_RB  = pApiPanelType->bPanelSwapOdd_RB;
        pDriverPanelType->m_bPanelSwapEven_RB = pApiPanelType->bPanelSwapEven_RB;

        pDriverPanelType->m_bPanelSwapLVDS_POL = pApiPanelType->bPanelSwapLVDS_POL;
        pDriverPanelType->m_bPanelSwapLVDS_CH = pApiPanelType->bPanelSwapLVDS_CH;
        pDriverPanelType->m_bPanelPDP10BIT    = pApiPanelType->bPanelPDP10BIT;
        pDriverPanelType->m_bPanelLVDS_TI_MODE = pApiPanelType->bPanelLVDS_TI_MODE;

        pDriverPanelType->m_ucPanelDCLKDelay  = pApiPanelType->ucPanelDCLKDelay;
        pDriverPanelType->m_bPanelInvDCLK     = pApiPanelType->bPanelInvDCLK;
        pDriverPanelType->m_bPanelInvDE       = pApiPanelType->bPanelInvDE;
        pDriverPanelType->m_bPanelInvHSync    = pApiPanelType->bPanelInvHSync;
        pDriverPanelType->m_bPanelInvVSync    = pApiPanelType->bPanelInvVSync;

        pDriverPanelType->m_ucPanelDCKLCurrent = pApiPanelType->ucPanelDCKLCurrent;
        pDriverPanelType->m_ucPanelDECurrent  = pApiPanelType->ucPanelDECurrent;
        pDriverPanelType->m_ucPanelODDDataCurrent = pApiPanelType->ucPanelODDDataCurrent;
        pDriverPanelType->m_ucPanelEvenDataCurrent = pApiPanelType->ucPanelEvenDataCurrent;

        pDriverPanelType->m_wPanelOnTiming1   = pApiPanelType->wPanelOnTiming1;
        pDriverPanelType->m_wPanelOnTiming2   = pApiPanelType->wPanelOnTiming2;
        pDriverPanelType->m_wPanelOffTiming1  = pApiPanelType->wPanelOffTiming1;
        pDriverPanelType->m_wPanelOffTiming2  = pApiPanelType->wPanelOffTiming2;

        pDriverPanelType->m_ucPanelHSyncWidth = pApiPanelType->ucPanelHSyncWidth;
        pDriverPanelType->m_ucPanelHSyncBackPorch = pApiPanelType->ucPanelHSyncBackPorch;

        pDriverPanelType->m_ucPanelVSyncWidth = pApiPanelType->ucPanelVSyncWidth;
        pDriverPanelType->m_ucPanelVBackPorch = pApiPanelType->ucPanelVBackPorch;

        pDriverPanelType->m_wPanelHStart      = pApiPanelType->wPanelHStart;
        pDriverPanelType->m_wPanelVStart      = pApiPanelType->wPanelVStart;
        pDriverPanelType->m_wPanelWidth       = pApiPanelType->wPanelWidth;
        pDriverPanelType->m_wPanelHeight      = pApiPanelType->wPanelHeight;


        pDriverPanelType->m_wPanelMaxHTotal   = pApiPanelType->wPanelMaxHTotal;
        pDriverPanelType->m_wPanelHTotal      = pApiPanelType->wPanelHTotal;
        pDriverPanelType->m_wPanelMinHTotal   = pApiPanelType->wPanelMinHTotal;

        pDriverPanelType->m_wPanelMaxVTotal   = pApiPanelType->wPanelMaxVTotal;
        pDriverPanelType->m_wPanelVTotal      = pApiPanelType->wPanelVTotal;
        pDriverPanelType->m_wPanelMinVTotal   = pApiPanelType->wPanelMinVTotal;

        pDriverPanelType->m_dwPanelMaxDCLK    = pApiPanelType->dwPanelMaxDCLK;
        pDriverPanelType->m_dwPanelDCLK       = pApiPanelType->dwPanelDCLK;
        pDriverPanelType->m_dwPanelMinDCLK    = pApiPanelType->dwPanelMinDCLK;

        pDriverPanelType->m_wSpreadSpectrumStep   = pApiPanelType->wSpreadSpectrumStep;
        pDriverPanelType->m_wSpreadSpectrumSpan   = pApiPanelType->wSpreadSpectrumSpan;

        pDriverPanelType->m_ucDimmingCtl      = pApiPanelType->ucDimmingCtl;
        pDriverPanelType->m_ucMaxPWMVal       = pApiPanelType->ucMaxPWMVal;
        pDriverPanelType->m_ucMinPWMVal       = pApiPanelType->ucMinPWMVal;

        pDriverPanelType->m_bPanelDeinterMode = pApiPanelType->bPanelDeinterMode;

        // need change later
        pDriverPanelType->m_ucPanelAspectRatio = (E_PNL_ASPECT_RATIO)pApiPanelType->ucPanelAspectRatio;

        pDriverPanelType->m_u16LVDSTxSwapValue = pApiPanelType->u16LVDSTxSwapValue;

        // need change later
        pDriverPanelType->m_ucTiBitMode       = (APIPNL_TIBITMODE)pApiPanelType->ucTiBitMode;

        // need change later
        pDriverPanelType->m_ucOutputFormatBitMode = (APIPNL_OUTPUTFORMAT_BITMODE)pApiPanelType->ucOutputFormatBitMode;

        pDriverPanelType->m_bPanelSwapOdd_RG  = pApiPanelType->bPanelSwapOdd_RG;
        pDriverPanelType->m_bPanelSwapEven_RG = pApiPanelType->bPanelSwapEven_RG;
        pDriverPanelType->m_bPanelSwapOdd_GB  = pApiPanelType->bPanelSwapOdd_GB;
        pDriverPanelType->m_bPanelSwapEven_GB = pApiPanelType->bPanelSwapEven_GB;

        pDriverPanelType->m_bPanelDoubleClk   = pApiPanelType->bPanelDoubleClk;
        pDriverPanelType->m_dwPanelMaxSET     = pApiPanelType->dwPanelMaxSET;
        pDriverPanelType->m_dwPanelMinSET     = pApiPanelType->dwPanelMinSET;

        // need change later
        pDriverPanelType->m_ucOutTimingMode   = (APIPNL_OUT_TIMING_MODE)pApiPanelType->ucOutTimingMode;

#if (STB_ENABLE == 1) || (CONNECTTV_BOX == 1) || (A3_STB_ENABLE == 1)
        pDriverPanelType->m_bPanelNoiseDith = pApiPanelType->bPanelNoiseDith;
#endif

    }
}

const MAPI_VIDEO_INPUTSRCTABLE* SystemInfo::GetInputMuxInfo()
{
    if(m_pU32MuxInfo != NULL)
    {
        return (MAPI_VIDEO_INPUTSRCTABLE*)m_pU32MuxInfo;
    }

    ASSERT(0);
}

const S_TV_TYPE_INFO &SystemInfo::GetTVInfo(void)
{
    return (m_TV_info_table);
}

unsigned char SystemInfo::GetRouteTVMode(unsigned char u8RouteIndex)
{
    if (u8RouteIndex >= MAXROUTECOUNT)
    {
        printf("Rout Path index is %d larger than MAXROUTECOUNT:%d\n", u8RouteIndex, MAXROUTECOUNT);
        u8RouteIndex = 0;

        ASSERT(0);
    }

    return m_TV_info_table.u8RoutePath[u8RouteIndex];
}

void SystemInfo::SetCISlotCount(unsigned char u8CISlotCount)
{
    m_u8CISlotCount = u8CISlotCount;
}

unsigned char SystemInfo::GetCISlotCount()
{
    return m_u8CISlotCount;
}

unsigned short SystemInfo::get_IPEnableType()
{
    return m_TV_info_table.eIPEnable_type;
}

EN_ATV_SYSTEM_TYPE SystemInfo::get_ATVSystemType()
{
    return m_TV_info_table.eATV_type;
}

unsigned char SystemInfo::get_DTV_SystemType()
{
    return m_TV_info_table.eDTV_type;
}

unsigned char SystemInfo::get_STB_SystemType()
{
    return m_TV_info_table.eSTB_type;
}

EN_AUDIO_SYSTEM_TYPE SystemInfo::get_AUDIOSystemType()
{
    return m_TV_info_table.eAUDIO_type;
}

unsigned char SystemInfo::IsSupportTheDTVSystemType(unsigned char u8Type)
{
    unsigned char bRet = FALSE;

    if ((m_TV_info_table.eDTV_type&u8Type) == u8Type)
    {
        bRet = TRUE;
    }

    return bRet;
}

#if (STB_ENABLE == 1)
const HDMITx_Analog_Info_t* SystemInfo::GetHDMITxAnalogInfo(unsigned char u8Index)
{
    ASSERT(m_pHdmiTxAnalogInfo);

#if (SYSINFO_DEBUG == 1)
    HDMITx_Analog_Param_t *pInfo = NULL;
    pInfo = (HDMITx_Analog_Param_t*)(m_pHdmiTxAnalogInfo + u8Index)->HDMITx_Attr;
    printf("\033[36mHDMITxAnalogInfo (HD): %x %x %x %x %x %x\033[m\n", pInfo->tm_txcurrent, pInfo->tm_pren2, pInfo->tm_precon,
                pInfo->tm_pren, pInfo->tm_tenpre, pInfo->tm_ten);
#endif
    return (m_pHdmiTxAnalogInfo + u8Index);
}
#endif

const ScartInfo_t& SystemInfo::GetScartInfo()
{
    return m_ScartInfo;
}

const WDTInfo_t& SystemInfo::GetWDTCfg()
{
    return m_WDTInfo;
}

unsigned char SystemInfo::GetWDTCfgEnableFlag()
{
    return m_WDTInfo.bWdtEnable;
}

unsigned char SystemInfo::GetHbbtvDelayInitFlag()
{
    return m_bHbbtvDelayInitFlag;
}

const GPIOConfig_t& SystemInfo::GetGPIOConfig()
{
    return m_gpioconfig;
}

const GPIOInfo_t* SystemInfo::GetGPIOInfo()
{
    return m_pGPIOColl;
}

const I2CConfig_t& SystemInfo::GetI2CConfig()
{
    return m_i2cconfig;
}

const I2CBus_t* SystemInfo::GetI2CBus()
{
    return m_pI2CBuses;
}

I2CDeviceInfo_t* SystemInfo::GetI2CDeviceInfo()
{
    return m_pI2CDevices;
}

void SystemInfo::SetMSPIPadInfo(void)
{

    m_mspiconfig.n_mspi_devNum = BOARD_MSPI_DEVICE_NUM;
    if(!BOARD_MSPI_DEVICE_NUM)
        return;

    m_pMSPIPadInfo = new (std::nothrow) MSPI_pad_info_s[m_mspiconfig.n_mspi_devNum];
    ASSERT(m_pMSPIPadInfo);


    memcpy(m_pMSPIPadInfo, &(Board_MSPI_Dev), sizeof(MSPI_pad_info_s)*BOARD_MSPI_DEVICE_NUM);
}

const MSPI_pad_info_t* SystemInfo::GetMSPIPadInfo(void)
{
    return m_pMSPIPadInfo;
}

void SystemInfo::SetMSPICfg(MSPIConfig_s *pMSPIDev, unsigned char HWNum, unsigned char DEVNum)
{
    m_mspiconfig.n_mspi_devNum = DEVNum;
    m_mspiconfig.n_mspi_HWNum = HWNum;

    if(m_pMSPIDevices != NULL)
    {
        delete [] m_pMSPIDevices;
        m_pMSPIDevices = NULL;
    }

    if(m_mspiconfig.n_mspi_devNum != 0)
    {
        m_pMSPIDevices = new (std::nothrow) MSPIConfig_t[m_mspiconfig.n_mspi_devNum];
        ASSERT(m_pMSPIDevices);
        ASSERT(pMSPIDev);
        memcpy(m_pMSPIDevices, pMSPIDev, sizeof(MSPIConfig_s)*m_mspiconfig.n_mspi_devNum);
    }
}

MSPIConfig_t* SystemInfo::GetMSPIDeviceInfo()
{
    return m_pMSPIDevices;
}

const MSPI_config_t& SystemInfo::GetMSPIConfig()
{
    return m_mspiconfig;
}

EN_VD_CAPTURE_WINDOW_MODE SystemInfo::GetVDCaptureWinMode()
{
    return m_VDCaptureWinMode;
}

void SystemInfo::SetVDCaptureWinMode(EN_VD_CAPTURE_WINDOW_MODE  enVDCaptureWinMode)
{
    m_VDCaptureWinMode = enVDCaptureWinMode;

#if (SYSINFO_DEBUG == 1)
    printf("VDCaptureWinMode = %s\n", m_VDCaptureWinMode ? "Mode_1135" : "Mode1135_1P5");
#endif
}

const DemuxInfo_t* SystemInfo::GetSerialDMXInfo(unsigned char index)
{
    return &m_DMXRoute[index].m_SerialDMX;
}

const DemuxInfo_t* SystemInfo::GetParallelDMXInfo(unsigned char index)
{
    return &m_DMXRoute[index].m_ParalleDMX;
}

const AudioDefualtInit_t& SystemInfo::GetAudioDefaultInit()
{
    return m_AudioDefaultInit;
}

const AudioMux_t* SystemInfo::GetAudioInputMuxInfo()
{
    if(m_pAudioMuxInfo != NULL)
    {
        return m_pAudioMuxInfo;
    }

    ASSERT(0);
}

const AudioPath_t* SystemInfo::GetAudioPathInfo()
{
    if(m_pAudioPathInfo != NULL)
    {
        return m_pAudioPathInfo;
    }

    ASSERT(0);
}

const AudioOutputType_t* SystemInfo::GetAudioOutputTypeInfo()
{
    if(m_pAudioOutputTypeInfo != NULL)
    {
        return m_pAudioOutputTypeInfo;
    }

    ASSERT(0);
}

const VolumeCurve_t* SystemInfo::GetVolumeCurve()
{
    return &m_VolumeCurve;
}

unsigned char SystemInfo::GetPictureModeUseFacCurveFlag()
{
    return m_bEnablePictureModeUseFacCurve;
}

const PictureModeCurve_t* SystemInfo::GetPictureModeCurve()
{
    return &m_PictureModeCurve;
}

void SystemInfo::SetGammaTableCfg(GAMMA_TABLE_t* pGammaTableInfo[], unsigned char u8TblCnt, unsigned char u8Default)
{
    if(m_pGammaTableInfo != NULL)
    {
        delete [] m_pGammaTableInfo;
        m_pGammaTableInfo = NULL;
    }

    m_GammaConf.nGammaTableSize = u8TblCnt;
    m_pGammaTableInfo = new (std::nothrow) GAMMA_TABLE_t[m_GammaConf.nGammaTableSize];

    ASSERT(m_pGammaTableInfo);
    ASSERT(pGammaTableInfo);

    for (unsigned char i = 0; i < u8TblCnt; i++)
    {
        memcpy(&m_pGammaTableInfo[i],  pGammaTableInfo[i], sizeof(GAMMA_TABLE_t));
    }

    m_u8DefaultGammaIdx = u8Default;


#if (SYSINFO_DEBUG == 1)
    printf("--------------------GammaTableCfg Start------------------\n");
    printf("Gamma Table size = %u\n", m_GammaConf.nGammaTableSize);
    if(m_pGammaTableInfo != NULL)
    {
        for(int j = 0 ; j < m_GammaConf.nGammaTableSize ; j++)
        {
            for(unsigned short i = 0; i < GammaArrayMAXSize; i++)
            {
                printf("GammaTableInfo_R[%d] = 0x%02x \n", i, m_pGammaTableInfo[j].NormalGammaR[i]);
            }
            printf("\n");
            for(unsigned short i = 0; i < GammaArrayMAXSize; i++)
            {
                printf("GammaTableInfo_G[%d] = 0x%02x  \n", i, m_pGammaTableInfo[j].NormalGammaG[i]);
            }
            printf("\n");
            for(unsigned short i = 0; i < GammaArrayMAXSize; i++)
            {
                printf("GammaTableInfo_B[%d] = 0x%02x  \n", i, m_pGammaTableInfo[j].NormalGammaB[i]);
            }
            printf("\n");
        }
    }
    printf("--------------------GammaTableCfg End--------------------\n\n");
#endif
}

unsigned char SystemInfo::GetDefaultGammaIdx()
{
    return m_u8DefaultGammaIdx;
}

GAMMA_TABLE_t* SystemInfo::GetGammaTableInfo(unsigned char u8Idx)
{
    if(m_pGammaTableInfo != NULL)
    {
        return &m_pGammaTableInfo[u8Idx];
    }

    ASSERT(0);
}

unsigned char SystemInfo::GetVolumeCompensationFlag()
{
    return m_bEnableVolumeCom;
}

void SystemInfo::SetPcDetectModeCount(const stSignalDetectCount* const pPcdetectCount)
{
    ASSERT(pPcdetectCount);
    memcpy(&stPcDetectCountInfo, pPcdetectCount, sizeof(stSignalDetectCount));

#if (SYSINFO_DEBUG == 1)
    printf("------------------PC Detect Count Cfg Start-----------------\n");
    printf("Stable count = %d, unStable count = %d, \n", \
           stPcDetectCountInfo.u8StableCount, stPcDetectCountInfo.u8UnstableCount);
    printf("------------------PC Detect Count Cfg End-------------------\n\n");
#endif
}

void SystemInfo::SetHDMIDetectModeCount(const stSignalDetectCount* const pHdmiDetectCount)
{
    ASSERT(pHdmiDetectCount);
    memcpy(&stHDMIDetectCountInfo, pHdmiDetectCount, sizeof(stSignalDetectCount));

#if (SYSINFO_DEBUG == 1)
    printf("------------------HDMI Detect Count Cfg Start-----------------\n");
    printf("Stable count = %d, unStable count = %d, \n", \
           stHDMIDetectCountInfo.u8StableCount, stHDMIDetectCountInfo.u8UnstableCount);
    printf("------------------HDMI Detect Count Cfg End-------------------\n\n");
#endif
}

void SystemInfo::SetCompDetectModeCount(const stSignalDetectCount* const pCompDetectCount)
{
    ASSERT(pCompDetectCount);
    memcpy(&stCompDetectCountInfo, pCompDetectCount, sizeof(stSignalDetectCount));

#if (SYSINFO_DEBUG == 1)
    printf("------------------COMP Detect Count Cfg Start-----------------\n");
    printf("Stable count = %d, unStable count = %d, \n", \
           stCompDetectCountInfo.u8StableCount, stCompDetectCountInfo.u8UnstableCount);
    printf("------------------COMP Detect Count Cfg End-------------------\n\n");
#endif
}

const stSignalDetectCount& SystemInfo::GetPcDetectCount(void)
{
    return stPcDetectCountInfo;
}

const stSignalDetectCount& SystemInfo::GetHdmiDetectCount(void)
{
    return stHDMIDetectCountInfo;
}

const stSignalDetectCount& SystemInfo::GetCompDetectCount(void)
{
    return stCompDetectCountInfo;
}

void SystemInfo::SetDefaultPanelInfoCfg(MAPI_PanelType *pstPanelInfo, MAPI_U16 u16LVDS_Output_type, PanelBacklightPWMInfo *ptPanelBacklightPWMInfo)
{
    m_u16LVDS_Output_type = u16LVDS_Output_type;

    printf("************************Set default panel start*****************************\n");

#if 0  // wait utopia CL986058 release
    if ((*pstPanelInfo).u16PanelDCLK > MApi_PNL_GetSupportMaxDclk())
    {
        // This panel info is illegal, skip this panel info.
        continue;
    }
#endif

    char *pName = NULL;

    if ((*pstPanelInfo).pPanelName != NULL)
    {
        int len = strlen((*pstPanelInfo).pPanelName);
        printf("panel name len:%d\n", len);

        pName = new (std::nothrow) char[len+1];
        ASSERT(pName);

        memset(pName, 0 , len + 1);
        memcpy(pName, (*pstPanelInfo).pPanelName, len);
    }

    PanelType *ptr = new (std::nothrow) PanelType;
    ASSERT(ptr);

    _SDK2DriverPanelTypeInfoTrans(pstPanelInfo, ptr);
    ptr->m_pPanelName = pName;

    m_pPanelInfo[m_u16PanelSize].PanelAttr = (char*)ptr;
    m_pPanelInfo[m_u16PanelSize].u16PanelMaxDCLK = (*pstPanelInfo).u16PanelMaxDCLK;
    m_pPanelInfo[m_u16PanelSize].u16PanelDCLK = (*pstPanelInfo).u16PanelDCLK;
    m_pPanelInfo[m_u16PanelSize].u16PanelMinDCLK = (*pstPanelInfo).u16PanelMinDCLK;
    m_pPanelInfo[m_u16PanelSize].u16PanelLinkExtType = (*pstPanelInfo).u16PanelLinkExtType;
    m_pPanelInfo[m_u16PanelSize].dPWMSim3DLRScale = (*pstPanelInfo).dPWMSim3DLRScale;

    m_pPanelInfo[m_u16PanelSize].u16tRx = (*pstPanelInfo).u16tRx; // target Rx
    m_pPanelInfo[m_u16PanelSize].u16tRy = (*pstPanelInfo).u16tRy; // target Ry
    m_pPanelInfo[m_u16PanelSize].u16tGx = (*pstPanelInfo).u16tGx; // target Gx
    m_pPanelInfo[m_u16PanelSize].u16tGy = (*pstPanelInfo).u16tGy; // target Gy
    m_pPanelInfo[m_u16PanelSize].u16tBx = (*pstPanelInfo).u16tBx; // target Bx
    m_pPanelInfo[m_u16PanelSize].u16tBy = (*pstPanelInfo).u16tBy; // target By
    m_pPanelInfo[m_u16PanelSize].u16tWx = (*pstPanelInfo).u16tWx; // target Wx
    m_pPanelInfo[m_u16PanelSize].u16tWy = (*pstPanelInfo).u16tWy; // target Wy

    printf("%s\n", ptr->m_pPanelName);

    m_PanelMap[E_MAPI_TIMING_DEFAULT] = &m_pPanelInfo[m_u16PanelSize];

    m_u16PanelSize++;

    SYSTEM_INFO_FLOW("Panel size is (%d, %d).\n", ptr->m_wPanelWidth, ptr->m_wPanelHeight);

    printf("************************Set default panel end*******************************\n");

    if (ptPanelBacklightPWMInfo != NULL)
    {
        ASSERT(ptPanelBacklightPWMInfo);
        memcpy(&(m_PanelBacklightPWMInfo), ptPanelBacklightPWMInfo, sizeof(PanelBacklightPWMInfo));
    }

#if (SYSINFO_DEBUG == 1)
    printf("------------------SetPanelBlacklightPWM Start--------------------\n");
    printf(" PWMPort = %d,\n u32PeriodPWM = %d,\n u32DutyPWM = %d,\n u16DivPWM = %d,\n bPolPWM = %d,\n u8MaxPWMvalue = %d,\n u8MinPWMvalue = %d,\n ", \
                m_PanelBacklightPWMInfo.u8PWMPort, m_PanelBacklightPWMInfo.u32PeriodPWM, m_PanelBacklightPWMInfo.u32DutyPWM, m_PanelBacklightPWMInfo.u16DivPWM, m_PanelBacklightPWMInfo.bPolPWM, m_PanelBacklightPWMInfo.u16MaxPWMvalue, m_PanelBacklightPWMInfo.u16MinPWMvalue,m_PanelBacklightPWMInfo.bBakclightFreq2Vfreq);
    printf("------------------SetPanelBlacklightPWM End----------------------\n\n");
#endif

    m_enActivePanelTiming = E_MAPI_TIMING_DEFAULT;

#if (SYSINFO_DEBUG == 1)
    printf("--------------------PanelCfg Start-------------------\n");
    printf("Current Panel = %s\n", pName);
    for(int i = 0; i < 1 ; i++)
    {
        if (m_pPanelInfo[i].PanelAttr == NULL)
        {
            continue;
        }

        PanelType *ptr = (PanelType*)m_pPanelInfo[i].PanelAttr;
        printf("Table Index[%d] = (%s, %u, %d, %u, %u, %u, %u, %u, %u, %u, %u, %u, %u, %u, %u, %u, %u, %u, ", i, ptr->m_pPanelName, ptr->bPanelDither, ptr->m_ePanelLinkType\
               , ptr->m_bPanelDualPort, ptr->m_bPanelSwapPort, ptr->m_bPanelSwapOdd_ML, ptr->m_bPanelSwapEven_ML, \
               ptr->m_bPanelSwapOdd_RB, ptr->m_bPanelSwapEven_RB, ptr->m_bPanelSwapLVDS_POL, ptr->m_bPanelSwapLVDS_CH, \
               ptr->m_bPanelPDP10BIT, ptr->m_bPanelLVDS_TI_MODE, ptr->m_ucPanelDCLKDelay, ptr->m_bPanelInvDCLK, \
               ptr->m_bPanelInvDE, ptr->m_bPanelInvHSync, ptr->m_bPanelInvVSync);
        printf("%u, %u, %u, %u, %u, %u, %u, %u, %u, %u, %u, %u, %u, %u, %u, %u, %u, %u, %u, %u, %u, %u, %u, %u, %u, %u, %u, %u, %u, %u, %u, %d, %u, %d, %d, %u, %u, %u, %u, %u, 0x%x, 0x%x, %d)\n", \
               ptr->m_ucPanelDCKLCurrent, ptr->m_ucPanelDECurrent, ptr->m_ucPanelODDDataCurrent, \
               ptr->m_ucPanelEvenDataCurrent, ptr->m_wPanelOnTiming1, ptr->m_wPanelOnTiming2, ptr->m_wPanelOffTiming1, ptr->m_wPanelOffTiming2, \
               ptr->m_ucPanelHSyncWidth, ptr->m_ucPanelHSyncBackPorch, ptr->m_ucPanelVSyncWidth, ptr->m_ucPanelVBackPorch, \
               ptr->m_wPanelHStart, ptr->m_wPanelVStart, ptr->m_wPanelWidth, ptr->m_wPanelHeight, \
               ptr->m_wPanelMaxHTotal, ptr->m_wPanelHTotal, ptr->m_wPanelMinHTotal, \
               ptr->m_wPanelMaxVTotal, ptr->m_wPanelVTotal, ptr->m_wPanelMinVTotal, \
               ptr->m_dwPanelMaxDCLK, ptr->m_dwPanelDCLK, ptr->m_dwPanelMinDCLK, \
               ptr->m_wSpreadSpectrumStep, ptr->m_wSpreadSpectrumSpan, \
               ptr->m_ucDimmingCtl, ptr->m_ucMaxPWMVal, ptr->m_ucMinPWMVal, \
               ptr->m_bPanelDeinterMode, ptr->m_ucPanelAspectRatio, \
               ptr->m_u16LVDSTxSwapValue, ptr->m_ucTiBitMode, ptr->m_ucOutputFormatBitMode, \
               ptr->m_bPanelSwapOdd_RG, ptr->m_bPanelSwapEven_RG, ptr->m_bPanelSwapOdd_GB, ptr->m_bPanelSwapEven_GB, \
               ptr->m_bPanelDoubleClk, ptr->m_dwPanelMaxSET, ptr->m_dwPanelMinSET, ptr->m_ucOutTimingMode);
    }
    printf("--------------------PanelCfg End---------------------\n\n");
#endif
}

void SystemInfo::SetPanelInfoCfg(EN_MAPI_TIMING enTiming, MAPI_PanelType *pstPanelInfo)
{
    if (m_u16PanelSize >= m_u16MaxPanelSize)
    {
        SYSTEM_INFO_DBG("Current panel size is %d, max panel size is %d.\n", m_u16PanelSize, m_u16MaxPanelSize);
        ASSERT(0);
        return ;
    }

    printf("************************Set %s panel start*****************************\n", (*pstPanelInfo).pPanelName);

#if 0  // wait utopia CL986058 release
    if ((*pstPanelInfo).u16PanelDCLK > MApi_PNL_GetSupportMaxDclk())
    {
        // This panel info is illegal, skip this panel info.
        continue;
    }
#endif

    char *pName = NULL;

    if ((*pstPanelInfo).pPanelName != NULL)
    {
        int len = strlen((*pstPanelInfo).pPanelName);
        printf("panel name len:%d\n", len);

        pName = new (std::nothrow) char[len+1];
        ASSERT(pName);

        memset(pName, 0 , len + 1);
        memcpy(pName, (*pstPanelInfo).pPanelName, len);
    }

    PanelType *ptr = new (std::nothrow) PanelType;
    ASSERT(ptr);

    _SDK2DriverPanelTypeInfoTrans(pstPanelInfo, ptr);
    ptr->m_pPanelName = pName;

    m_pPanelInfo[m_u16PanelSize].PanelAttr = (char*)ptr;
    m_pPanelInfo[m_u16PanelSize].u16PanelMaxDCLK = (*pstPanelInfo).u16PanelMaxDCLK;
    m_pPanelInfo[m_u16PanelSize].u16PanelDCLK = (*pstPanelInfo).u16PanelDCLK;
    m_pPanelInfo[m_u16PanelSize].u16PanelMinDCLK = (*pstPanelInfo).u16PanelMinDCLK;
    m_pPanelInfo[m_u16PanelSize].u16PanelLinkExtType = (*pstPanelInfo).u16PanelLinkExtType;
    m_pPanelInfo[m_u16PanelSize].dPWMSim3DLRScale = (*pstPanelInfo).dPWMSim3DLRScale;

    m_pPanelInfo[m_u16PanelSize].u16tRx = (*pstPanelInfo).u16tRx; // target Rx
    m_pPanelInfo[m_u16PanelSize].u16tRy = (*pstPanelInfo).u16tRy; // target Ry
    m_pPanelInfo[m_u16PanelSize].u16tGx = (*pstPanelInfo).u16tGx; // target Gx
    m_pPanelInfo[m_u16PanelSize].u16tGy = (*pstPanelInfo).u16tGy; // target Gy
    m_pPanelInfo[m_u16PanelSize].u16tBx = (*pstPanelInfo).u16tBx; // target Bx
    m_pPanelInfo[m_u16PanelSize].u16tBy = (*pstPanelInfo).u16tBy; // target By
    m_pPanelInfo[m_u16PanelSize].u16tWx = (*pstPanelInfo).u16tWx; // target Wx
    m_pPanelInfo[m_u16PanelSize].u16tWy = (*pstPanelInfo).u16tWy; // target Wy

    // Get panel timing.
    EN_MAPI_TIMING enTiming1 = GetPanelTiming(m_u16PanelSize);
    // Check timing base.
    if ((enTiming1 & 0xff00) != (enTiming & 0xff00))
    {
        ASSERT(0);
        return ;
    }

    SYSTEM_INFO_FLOW("Load panel timing is %x, panel name is %s.\n", enTiming, ptr->m_pPanelName);
    m_PanelMap[enTiming] = &m_pPanelInfo[m_u16PanelSize];
    m_PanelMap[enTiming1] = &m_pPanelInfo[m_u16PanelSize];
    if ((enTiming1 & 0xff00) == E_MAPI_TIMING_4K2K)
    {
        if (enTiming1 == E_MAPI_TIMING_4K2KP_30)
        {
            SYSTEM_INFO_FLOW("Also set panel to timing %x.\n", E_MAPI_TIMING_4K2K);
            m_PanelMap[E_MAPI_TIMING_4K2K] = &m_pPanelInfo[m_u16PanelSize];
        }
    }
    else
    {
        if ((enTiming1 & 0x00ff) == 0x01)
        {
            enTiming1 = (EN_MAPI_TIMING)(enTiming1&0xff00);
            SYSTEM_INFO_FLOW("Also set panel to timing %x.\n", enTiming1);
            m_PanelMap[enTiming1] = &m_pPanelInfo[m_u16PanelSize];
        }
    }

    m_u16PanelSize++;

    SYSTEM_INFO_FLOW("panel size is (%d, %d).\n", ptr->m_wPanelWidth, ptr->m_wPanelHeight);

    printf("************************Set panel end*******************************\n");
}

#if ( INTEL_WIDI_ENABLE == 1 )
const WidiInfo_t* SystemInfo::GetWidiInfo()
{
    if (m_WidiInfo.checksum != WIDI_INI_CHECKSUM)
    {
        int  strLength = 0;

        m_WidiInfo.sigma = iniparser_getint(m_pWidiini, "SIGMA:sigma", -1);
        if ( m_WidiInfo.sigma != 0 )
        {
            char *pUsbPath = NULL;
            char *pScriptPath = NULL;

            pUsbPath = iniparser_getstr(m_pWidiini, "SIGMA:usb_path");
            if(pUsbPath == NULL)
            {
                SYSTEM_INFO_DBG("\n\033[1;33;40m >>>>Debug<<<<  SIGMA:usb_path == NULL \033[0m\n");
                ASSERT(pUsbPath);
                return NULL;
            }

            //safestrcpy(&m_WidiInfo.usb_path[0], pUsbPath);
            strLength = strlen(pUsbPath);
            if( (strLength+1) > MAX_WIDI_STRING_BUFFER_SIZE )
            {
                SYSTEM_INFO_DBG("\n\033[1;33;40m >>>>Debug<<<<  SIGMA:usb_path >= 256 \033[0m\n");
                ASSERT(0);
                return NULL;
            }
            memcpy(&m_WidiInfo.usb_path[0], pUsbPath, (strLength+1));

            pScriptPath = iniparser_getstr(m_pWidiini, "SIGMA:p2p_script_file_path");
            if(pScriptPath == NULL)
            {
                SYSTEM_INFO_DBG("\n\033[1;33;40m >>>>Debug<<<<  SIGMA:p2p_script_file_path == NULL \033[0m\n");
                ASSERT(pScriptPath);
                return NULL;
            }

            //safestrcpy(&m_WidiInfo.usb_path[0], pUsbPath);
            strLength = strlen(pScriptPath);
            if( (strLength+1) > MAX_WIDI_STRING_BUFFER_SIZE )
            {
                SYSTEM_INFO_DBG("\n\033[1;33;40m >>>>Debug<<<<  SIGMA:p2p_script_file_path >= 256 \033[0m\n");
                ASSERT(0);
                return NULL;
            }
            memcpy(&m_WidiInfo.p2p_script_file_path[0], pScriptPath, (strLength+1));

            pScriptPath = iniparser_getstr(m_pWidiini, "SIGMA:wfd_script_file_path");
            if(pScriptPath == NULL)
            {
                SYSTEM_INFO_DBG("\n\033[1;33;40m >>>>Debug<<<<  SIGMA:wfd_script_file_path == NULL \033[0m\n");
                ASSERT(pScriptPath);
                return NULL;
            }

            //safestrcpy(&m_WidiInfo.usb_path[0], pUsbPath);
            strLength = strlen(pScriptPath);
            if( (strLength+1) > MAX_WIDI_STRING_BUFFER_SIZE )
            {
                SYSTEM_INFO_DBG("\n\033[1;33;40m >>>>Debug<<<<  SIGMA:wfd_script_file_path >= 256 \033[0m\n");
                ASSERT(0);
                return NULL;
            }
            memcpy(&m_WidiInfo.wfd_script_file_path[0], pScriptPath, (strLength+1));
        }

        m_WidiInfo.widi_debug_level = iniparser_getint(m_pWidiini, "WIDI:debug_level", -1);

        m_WidiInfo.tsplayer_debug_level = iniparser_getint(m_pWidiini, "TSPLAYER:debug_level", -1);
        m_WidiInfo.tsplayer_audio_buffer_threshold = iniparser_getint(m_pWidiini, "TSPLAYER:audio_buffer_threshold", -1);
        m_WidiInfo.tsplayer_debug_flag = iniparser_getint(m_pWidiini, "TSPLAYER:debug_flag", -1);

        m_WidiInfo.ts_filein_enable = iniparser_getint(m_pWidiini, "TSPLAYER:ts_filein_enable", -1);
        if ( m_WidiInfo.ts_filein_enable != 0 )
        {
            char *pTsFilePath = NULL;
            m_WidiInfo.ts_filein_size_count = iniparser_getint(m_pWidiini, "TSPLAYER:ts_filein_size_count", -1);
            m_WidiInfo.ts_filein_sleep_time_us = iniparser_getint(m_pWidiini, "TSPLAYER:ts_filein_sleep_time_us", -1);
            pTsFilePath = iniparser_getstr(m_pWidiini, "TSPLAYER:ts_filein_file_path");
            if(pTsFilePath == NULL)
            {
                SYSTEM_INFO_DBG("\n\033[1;33;40m >>>>Debug<<<<  TSPLAYER:ts_filein_file_path == NULL \033[0m\n");
                ASSERT(pTsFilePath);
                return NULL;
            }
            //safestrcpy(&m_WidiInfo.ts_filein_file_path[0], pTsFilePath);

            strLength = strlen(pTsFilePath);
            if( (strLength+1) >= MAX_WIDI_STRING_BUFFER_SIZE )
            {
                SYSTEM_INFO_DBG("\n\033[1;33;40m >>>>Debug<<<<  TSPLAYER:ts_filein_file_path >= 256 \033[0m\n");
                ASSERT(0);
                return FALSE;
            }
            memcpy(&m_WidiInfo.ts_filein_file_path[0], pTsFilePath, (strLength+1));
        }

        m_WidiInfo.checksum = WIDI_INI_CHECKSUM;
    }
    return &m_WidiInfo;
}
#endif

#if (ENABLE_LITE_SN == 0)
const PanelInfo_t* SystemInfo::Get4K2KModeInfo()
{
    return m_p4K2KModelInfo;
}
#endif

const PanelBacklightPWMInfo* SystemInfo::GetPanelBacklightPWMInfo()
{
    return &m_PanelBacklightPWMInfo;
}

const PanelModPvddPowerInfo* SystemInfo::GetPanelModPvddPowerInfo()
{
    return &m_PanelModPvddPowerInfo;
}

unsigned short SystemInfo::GetLVDSOutputType()
{
    return m_u16LVDS_Output_type;
}

bool SystemInfo::IsPanelExists(EN_MAPI_TIMING ePanelTiming)
{
    if (GetPanel(ePanelTiming) == NULL)
    {
        return false;
    }
    else
    {
        return true;
    }
}

EN_MAPI_TIMING SystemInfo::GetPanelTiming(int panelIdx)
{
    SYSTEM_INFO_FLOW("Specify panel index %d.\n", panelIdx);
    if (panelIdx < 0 && panelIdx >= m_u16MaxPanelSize)
    {
        // idx need large than 0.
        return E_MAPI_TIMING_UNDEFINED;
    }

    if (m_pPanelInfo[panelIdx].PanelAttr == NULL)
    {
        // No panel attribute exists.
        return E_MAPI_TIMING_UNDEFINED;
    }

    PanelType *pPnl = (PanelType*)m_pPanelInfo[panelIdx].PanelAttr;
    float frameRate = ((float)m_pPanelInfo[panelIdx].u16PanelDCLK * 1000000.0) / pPnl->m_wPanelHTotal / pPnl->m_wPanelVTotal;

    SYSTEM_INFO_FLOW("Specify panel name is %s.\n", pPnl->m_pPanelName);
    SYSTEM_INFO_FLOW("Specify panel width is %d, height is %d, framerate is %.2f.\n", pPnl->m_wPanelWidth, pPnl->m_wPanelHeight, frameRate);

    if ((pPnl->m_wPanelWidth == 720) && (pPnl->m_wPanelHeight == 480))
    {
        if (pPnl->m_ePanelLinkType == LINK_DAC_I)
        {
            return E_MAPI_TIMING_480I_60;
        }
        else
        {
            return E_MAPI_TIMING_480P_60;
        }
    }
    else if ((pPnl->m_wPanelWidth == 720) && (pPnl->m_wPanelHeight == 240))
    {
        return E_MAPI_TIMING_480I_60;
    }
    else if ((pPnl->m_wPanelWidth == 720) && (pPnl->m_wPanelHeight == 576))
    {
        if (pPnl->m_ePanelLinkType == LINK_DAC_I)
        {
            return E_MAPI_TIMING_576I_50;
        }
        else
        {
            return E_MAPI_TIMING_576P_50;
        }
    }
    else if ((pPnl->m_wPanelWidth == 720) && (pPnl->m_wPanelHeight == 288))
    {
        return E_MAPI_TIMING_576I_50;
    }
    else if ((pPnl->m_wPanelWidth == 1280) && (pPnl->m_wPanelHeight == 720) && (frameRate >= 55.0) && (frameRate < 65.0))
    {
        return E_MAPI_TIMING_720P_60;
    }
    else if ((pPnl->m_wPanelWidth == 1280) && (pPnl->m_wPanelHeight == 720) && (frameRate >= 45.0) && (frameRate < 55.0))
    {
        return E_MAPI_TIMING_720P_50;
    }
    else if ((pPnl->m_wPanelWidth == 1280) && (pPnl->m_wPanelHeight == 1440))
    {
        return E_MAPI_TIMING_1440P_50;
    }
    else if ((pPnl->m_wPanelWidth == 1280) && (pPnl->m_wPanelHeight == 1470) && (frameRate >= 55.0) && (frameRate < 65.0))
    {
        return E_MAPI_TIMING_1470P_60;
    }
    else if ((pPnl->m_wPanelWidth == 1280) && (pPnl->m_wPanelHeight == 1470) && (frameRate >= 25.0) && (frameRate < 35.0))
    {
        return E_MAPI_TIMING_1470P_30;
    }
    else if ((pPnl->m_wPanelWidth == 1280) && (pPnl->m_wPanelHeight == 1470) && (frameRate >= 23.0) && (frameRate < 25.0))
    {
        return E_MAPI_TIMING_1470P_24;
    }
    else if ((pPnl->m_wPanelWidth == 1920) && (pPnl->m_wPanelHeight == 1080) && (frameRate >= 55.0) && (frameRate < 65.0))
    {
        if (pPnl->m_ePanelLinkType == LINK_DAC_I)
        {
            return E_MAPI_TIMING_2K1KI_60;
        }
        else
        {
            return E_MAPI_TIMING_2K1KP_60;
        }
    }
    else if ((pPnl->m_wPanelWidth == 1920) && (pPnl->m_wPanelHeight == 540) && (frameRate >= 55.0) && (frameRate < 65.0))
    {
        return E_MAPI_TIMING_2K1KI_60;
    }
    else if ((pPnl->m_wPanelWidth == 1920) && (pPnl->m_wPanelHeight == 1080) && (frameRate >= 45.0) && (frameRate < 55.0))
    {
        if (pPnl->m_ePanelLinkType == LINK_DAC_I)
        {
            return E_MAPI_TIMING_2K1KI_50;
        }
        else
        {
            return E_MAPI_TIMING_2K1KP_50;
        }
    }
    else if ((pPnl->m_wPanelWidth == 1920) && (pPnl->m_wPanelHeight == 540) && (frameRate >= 45.0) && (frameRate < 55.0))
    {
        return E_MAPI_TIMING_2K1KI_50;
    }
    else if ((pPnl->m_wPanelWidth == 1920) && (pPnl->m_wPanelHeight == 1080) && (frameRate >= 27.5) && (frameRate < 35.0))
    {
        return E_MAPI_TIMING_2K1KP_30;
    }
    else if ((pPnl->m_wPanelWidth == 1920) && (pPnl->m_wPanelHeight == 1080) && (frameRate >= 24.5) && (frameRate < 27.5))
    {
        return E_MAPI_TIMING_2K1KP_25;
    }
    else if ((pPnl->m_wPanelWidth == 1920) && (pPnl->m_wPanelHeight == 1080) && (frameRate < 24.5))
    {
        return E_MAPI_TIMING_2K1KP_24;
    }
    else if ((pPnl->m_wPanelWidth == 1920) && (pPnl->m_wPanelHeight == 2160) && (frameRate >= 55.0) && (frameRate < 65.0))
    {
        return E_MAPI_TIMING_2K2KP_60;
    }
    else if ((pPnl->m_wPanelWidth == 1920) && (pPnl->m_wPanelHeight == 2160) && (frameRate >= 26.0) && (frameRate < 35.0))
    {
        return E_MAPI_TIMING_2K2KP_30;
    }
    else if ((pPnl->m_wPanelWidth == 1920) && (pPnl->m_wPanelHeight == 2160) && (frameRate >= 24.5) && (frameRate < 26.0))
    {
        return E_MAPI_TIMING_2K2KP_25;
    }
    else if ((pPnl->m_wPanelWidth == 1920) && (pPnl->m_wPanelHeight == 2160) && (frameRate >= 23.0) && (frameRate < 24.5))
    {
        return E_MAPI_TIMING_2K2KP_24;
    }
    else if ((pPnl->m_wPanelWidth == 1920) && (pPnl->m_wPanelHeight == 2205) && (frameRate >= 23.0) && (frameRate < 24.5))
    {
        return E_MAPI_TIMING_2K2KP_24;
    }
    else if ((pPnl->m_wPanelWidth == 3840) && (pPnl->m_wPanelHeight == 540) && (frameRate >= 230.0) && (frameRate < 250.0))
    {
        return E_MAPI_TIMING_4K540P_240;
    }
    else if ((pPnl->m_wPanelWidth == 3840) && (pPnl->m_wPanelHeight == 1080) && (frameRate >= 110.0) && (frameRate < 130.0))
    {
        return E_MAPI_TIMING_4K1KP_120;
    }
    else if ((pPnl->m_wPanelWidth == 3840) && (pPnl->m_wPanelHeight == 1080) && (frameRate >= 50.0) && (frameRate < 70.0))
    {
        return E_MAPI_TIMING_4K1KP_60;
    }
    else if ((pPnl->m_wPanelWidth == 3840) && (pPnl->m_wPanelHeight == 1080) && (frameRate >= 25.0) && (frameRate < 35.0))
    {
        return E_MAPI_TIMING_4K1KP_30;
    }
    else if ((pPnl->m_wPanelWidth == 3840) && (pPnl->m_wPanelHeight == 2160) && (frameRate >= 55.0) && (frameRate < 65.0))
    {
        return E_MAPI_TIMING_4K2KP_60;
    }
    else if ((pPnl->m_wPanelWidth == 3840) && (pPnl->m_wPanelHeight == 2160) && (frameRate >= 45.0) && (frameRate < 55.0))
    {
        return E_MAPI_TIMING_4K2KP_50;
    }
    else if ((pPnl->m_wPanelWidth == 3840) && (pPnl->m_wPanelHeight == 2160) && (frameRate >= 27.5) && (frameRate < 35.0))
    {
        return E_MAPI_TIMING_4K2KP_30;
    }
    else if ((pPnl->m_wPanelWidth == 3840) && (pPnl->m_wPanelHeight == 2160) && (frameRate >= 24.5) && (frameRate < 27.5))
    {
        return E_MAPI_TIMING_4K2KP_25;
    }
    else if ((pPnl->m_wPanelWidth == 3840) && (pPnl->m_wPanelHeight == 2160) && (frameRate >= 23.0) && (frameRate < 24.5))
    {
        return E_MAPI_TIMING_4K2KP_24;
    }
    else if ((pPnl->m_wPanelWidth == 4096) && (pPnl->m_wPanelHeight == 2160) && (frameRate >= 23.0) && (frameRate < 24.5))
    {
        return E_MAPI_TIMING_4096x2160P_24;
    }

    return E_MAPI_TIMING_UNDEFINED;

}

MAPI_AUTO_NR_INIT_PARAM* SystemInfo::GetPQAutoNRParam()
{
    return &m_AutoNrParam;
}

#if(MULTI_DEMOD==1)
stMultiExtendDemodCfg& SystemInfo::GetMultiDemodCfg()
{
    return m_MultiDemodParam;
}
#endif

void SystemInfo::SetCustomerPQCfg(char * pPQCustomerBinFilePath, MAPI_PQ_WIN enWinType)
{
    if (NULL == pPQCustomerBinFilePath)
    {
        return;
    }

    if ( enWinType == MAPI_PQ_MAIN_WINDOW )
    {
        pMainPQPath = pPQCustomerBinFilePath;
    }
    else if ( enWinType == MAPI_PQ_SUB_WINDOW )
    {
        pSubPQPath = pPQCustomerBinFilePath;
    }
}

std::string  SystemInfo::GetCustomerPQCfg(MAPI_PQ_WIN enWinType)
{
    if ( enWinType == MAPI_PQ_MAIN_WINDOW )
    {
        return pMainPQPath;
    }
    else if ( enWinType == MAPI_PQ_SUB_WINDOW )
    {
        return pSubPQPath;
    }

    return pMainPQPath;
}

unsigned char SystemInfo::GetDemodConfig(unsigned char u8DemodMode, unsigned char** u8BOARD_DSPRegInitExt, unsigned char** u8BOARD_DMD_InitExt )
{
#if (STB_ENABLE == 0)
    if (u8DemodMode==0) // DVBT
    {
        *u8BOARD_DSPRegInitExt=m_u8BOARD_DVBT_DSPRegInitExt;
        *u8BOARD_DMD_InitExt=m_u8BOARD_DMD_DVBT_InitExt;
    }
    else if (u8DemodMode==1) // DVBC
    {
        *u8BOARD_DSPRegInitExt=m_u8BOARD_DVBC_DSPRegInitExt;
        *u8BOARD_DMD_InitExt=m_u8BOARD_DMD_DVBC_InitExt;
    }
    else if (u8DemodMode==2) // ATSC
    {
        *u8BOARD_DSPRegInitExt=m_u8BOARD_ATSC_DSPRegInitExt;
        *u8BOARD_DMD_InitExt=m_u8BOARD_DMD_ATSC_InitExt;
    }
    else
    {
        *u8BOARD_DSPRegInitExt=NULL;
        *u8BOARD_DMD_InitExt=NULL;
        return FALSE;
    }
    return TRUE;
#else
    *u8BOARD_DSPRegInitExt=NULL;
    *u8BOARD_DMD_InitExt=NULL;
    return FALSE;
#endif
}

void SystemInfo::SetPcModeTimingTableCfgBlock(const PcModeTimingTable_t* const pPcModeTimingTable, const unsigned char u8TableCount)
{
    if (!m_pPcModeTimingTable)
    {
        if (u8TableCount > MAXPCMODETIMINGTABLE)
        {
            printf("ERROR: too many pc mode table error.\n");
            ASSERT(0);
        }
        m_u8PcModeTimingTableCount = u8TableCount;
        m_pPcModeTimingTable = new (std::nothrow) PcModeTimingTable_t[u8TableCount];
        ASSERT(m_pPcModeTimingTable);
        memcpy(m_pPcModeTimingTable, pPcModeTimingTable, u8TableCount * sizeof(PcModeTimingTable_t));
    }
    else
    {
        printf("ERROR: PcMode has been allocted...\n");
        ASSERT(0);
    }
}



const PcModeTimingTable_t* SystemInfo::GetPcModeTimingTable()
{
    return m_pPcModeTimingTable;
}

unsigned char SystemInfo::GetPcModeTimingTableCount()
{
    return m_u8PcModeTimingTableCount;
}

VGA_EDID_Info_t* SystemInfo::GetVGAEDIDInfo()
{
    return &m_VgaEdidInfo;
}

HDMI_EDID_InfoSet_t* SystemInfo::GetHDMIEDIDInfoSet()
{
    return &m_HdmiEdidInfoSet;
}

void SystemInfo::SetVideoMirrorCfg(unsigned char bMirrorEnable,unsigned char u8MirrorMode)
{
    m_bMirrorVideo=bMirrorEnable;

    if(bMirrorEnable)
    {
        if( (u8MirrorMode==MIRROR_NORMAL) || (u8MirrorMode>=MIRROR_MAX) )
        {
            m_u8MirrorMode= MIRROR_HV;      //default Mirror mode is MIRROR_HV
            printf("Warning!!! Mirror Type is illegal.\n\n");
        }
        else
        {
            m_u8MirrorMode= u8MirrorMode;
        }
    }
    else
    {
        m_u8MirrorMode= MIRROR_NORMAL;
        if(u8MirrorMode != MIRROR_NORMAL)    // Mirror disable  but Mirror mode not set MIRROR_NORMAL
        {
            printf("Warning!!! Mirror Type is not NORMAL, but Mirror is DISABLE.\n\n");
        }
    }
}

unsigned char SystemInfo::getMirrorVideoMode()
{
    return m_u8MirrorMode;
}

#if (HDMITX_ENABLE == 1)
EN_LTH_HDMITX_AUDIO_TYPE SystemInfo::GetHdmitxAudioType()
{
    return ROCKET_AUDIO_INPUT_MODE;
}
#endif

unsigned char SystemInfo::GetMirrorVideoFlag()
{
    return m_bMirrorVideo;
}

unsigned char SystemInfo::Get3DOverScanFlag()
{
    return m_bEnable3DOverScan;
}
#if (ENABLE_LITE_SN == 0)
void SystemInfo:: SetSwingLevel(unsigned short u16SwingLevel)
{
#if (MSTAR_TVOS ==1 )
    m_u16SwingLevel = u16SwingLevel;
#endif
}

unsigned short SystemInfo::GetSwingLevel()
{
#if (MSTAR_TVOS ==1 )
    return m_u16SwingLevel;
#else
    return 0;
#endif
}
#endif

void SystemInfo::Set3DPanelLRInverseCfg(unsigned char bEnable)
{
    m_bEnable3DPanelLRInverse = bEnable;
}

unsigned char SystemInfo::Get3DPanelLRInverseFlag()
{
    return m_bEnable3DPanelLRInverse;
}

void SystemInfo::SetFreerunCfg(unsigned char b3D, unsigned char bEnable)
{
    m_bEnableFreerun[b3D] = bEnable;
}

unsigned char SystemInfo::GetFreerunFlag(unsigned char b3D)
{
    return m_bEnableFreerun[b3D];
}

void SystemInfo::SetSGPanelCfg(unsigned char bEnable)
{
    m_bIsSGPanel= bEnable;
}

unsigned char SystemInfo::GetSGPanelFlag()
{
    return m_bIsSGPanel;
}

void SystemInfo::SetXCOutput120hzSGPanelCfg(unsigned char bEnable)
{
    m_bIsXCOutput120hzSGPanel = bEnable;
}

unsigned char SystemInfo::GetXCOutput120hzSGPanelFlag()
{
    return m_bIsXCOutput120hzSGPanel;
}

void SystemInfo::SetFBLModeThreshold(MAPI_U32 u32Threshold)
{
    m_u32FBLThreshold = u32Threshold;
}

MAPI_U32 SystemInfo::GetFBLModeThreshold()
{
    return m_u32FBLThreshold;
}


void SystemInfo::SetVideoInfoCfg(VideoInfo_s enVideoNum, unsigned char u8ResNum, const ST_MAPI_VIDEO_WINDOW_INFO* pstVideoInfo, MAPI_U32 u32HotPlugInverse, unsigned char bDotbydotAble , MAPI_U32 u32Hdmi5vDetectGpioSelect)
{
    int i = 0, j = 0;

    ST_MAPI_VIDEO_WINDOW_INFO **vptr_vd = NULL;
    vptr_vd = new (std::nothrow) ST_MAPI_VIDEO_WINDOW_INFO *[u8ResNum];
    ASSERT(vptr_vd);

    for(i = 0 ; i < u8ResNum ; i++)
    {
        vptr_vd[i] = new (std::nothrow) ST_MAPI_VIDEO_WINDOW_INFO[E_AR_MAX];
        ASSERT(vptr_vd[i]);
        for(j = 0; j < E_AR_MAX ; j++)
        {
            ASSERT(pstVideoInfo);
            memcpy((char*)&vptr_vd[i][j], (const char*)pstVideoInfo, sizeof(ST_MAPI_VIDEO_WINDOW_INFO));
            pstVideoInfo++;
        }
    }

    m_ResoSize[enVideoNum].nResolutionSize = u8ResNum;
    m_pVideoWinInfo[enVideoNum] = (void**)vptr_vd;
    if(enVideoNum == E_HDMI)
    {
        m_u32HotPlugInverse = u32HotPlugInverse;
        m_u32Hdmi5vDetectGpioSelect = u32Hdmi5vDetectGpioSelect;
        m_bDotByDotAble = bDotbydotAble;
        printf("HotPlug  = 0x%x\n", m_u32HotPlugInverse);
        printf("Hdmi5VDetectGpioSelect = 0x%x\n",m_u32Hdmi5vDetectGpioSelect);
        printf("DotbyDot = %d\n", m_bDotByDotAble);
    }

#if (SYSINFO_DEBUG == 1)
    printf("------------------VideoInfoCfg Start--------------------\n");
    ST_MAPI_VIDEO_WINDOW_INFO** ptr = (ST_MAPI_VIDEO_WINDOW_INFO**)m_pVideoWinInfo[enVideoNum];
    if(ptr != NULL)
    {
        switch(enVideoNum)
        {
            case E_DTV:
                printf("DTV Video Info Table\n");
                break;
            case E_HDMI:
                printf("HDMI Video Info Table\n");
                break;
            case E_YPbPr:
                printf("YPbPr(Component) Video Info Table\n");
                break;
            case E_CVBS:
                printf("CVBS Video Info Table\n");
                break;
            case E_RVU:
                printf("RVU Video Info Table\n");
                break;
            default:
                break;
        }

        for(i = 0 ; i < m_ResoSize[enVideoNum].nResolutionSize ; i++)
        {
            printf("//Resolution[%d]:\n", i);
            printf("//******************\n");
            for(j = 0; j < E_AR_MAX ; j++)
            {
                printf("(0x%02x, 0x%02x, %u, %u, %u, %u)//", ptr[i][j].u16H_CapStart, ptr[i][j].u16V_CapStart\
                       , ptr[i][j].u8HCrop_Left, ptr[i][j].u8HCrop_Right, ptr[i][j].u8VCrop_Up, ptr[i][j].u8VCrop_Down);
                switch(j)
                {
                    case E_AR_DEFAULT:
                        printf("Default \n");
                        break;
                    case E_AR_16x9:
                        printf("16:9 \n");
                        break;
                    case E_AR_4x3:
                        printf("4:3 \n");
                        break;
                    case E_AR_AUTO:
                        printf("Auto \n");
                        break;
                    case E_AR_Panorama:
                        printf("Panorama \n");
                        break;
                    case E_AR_JustScan:
                        printf("JustScan \n");
                        break;
                    case E_AR_Zoom1:
                        printf("Zoom1 \n");
                        break;
                    case E_AR_Zoom2:
                        printf("Zoom2 \n");
                        break;
#if (STB_ENABLE == 1)
                    case E_AR_4x3_PanScan:
                        printf("4x3_PanScan \n");
                        break;
                    case E_AR_4x3_LetterBox:
                        printf("4x3_LetterBox \n");
                        break;
                    case E_AR_16x9_PillarBox:
                        printf("16x9_PillarBox \n");
                        break;
#endif
                    default:
                        break;
                }

            }
            printf("//******************\n\n");
        }
    }
    printf("======================================================\n");
#endif

}

const ResolutionInfoSize* SystemInfo::GetResolutionInfo()
{
    return m_ResoSize;
}

MAPI_U32 SystemInfo::GetHotPlugInverse()
{
    return m_u32HotPlugInverse;
}

MAPI_U32 SystemInfo::GetHdmi5vGpioSelect()
{
    return m_u32Hdmi5vDetectGpioSelect;
}


unsigned char SystemInfo::GetDotByDotable()
{
    return m_bDotByDotAble;
}

const ST_MAPI_VIDEO_WINDOW_INFO** SystemInfo::GetVideoWinInfo(VideoInfo_t E_VideoInfo)
{
    if(E_VideoInfo == E_DTV)
    {
        if(m_pVideoWinInfo[E_DTV] != NULL)
        {
            return (const ST_MAPI_VIDEO_WINDOW_INFO**)m_pVideoWinInfo[E_DTV];
        }
        else
        {
            ASSERT(0);
        }
    }
    else if(E_VideoInfo == E_HDMI)
    {
        if(m_pVideoWinInfo[E_HDMI] != NULL)
        {
            return (const ST_MAPI_VIDEO_WINDOW_INFO**)m_pVideoWinInfo[E_HDMI];
        }
        else
        {
            ASSERT(0);
        }
    }
    else if(E_VideoInfo == E_YPbPr)
    {
        if(m_pVideoWinInfo[E_YPbPr] != NULL)
        {
            return (const ST_MAPI_VIDEO_WINDOW_INFO**)m_pVideoWinInfo[E_YPbPr];
        }
        else
        {
            ASSERT(0);
        }
    }
    else if(E_VideoInfo == E_CVBS)
    {
        if(m_pVideoWinInfo[E_CVBS] != NULL)
        {
            return (const ST_MAPI_VIDEO_WINDOW_INFO**)m_pVideoWinInfo[E_CVBS];
        }
        else
        {
            ASSERT(0);
        }
    }
    else if(E_VideoInfo == E_RVU)
    {
        if(m_pVideoWinInfo[E_RVU] != NULL)
        {
            return (const ST_MAPI_VIDEO_WINDOW_INFO**)m_pVideoWinInfo[E_RVU];
        }
        else
        {
            ASSERT(0);
        }
    }
    ASSERT(0);
}

#if (ENABLE_BACKEND == 1)
unsigned char SystemInfo::SetUrsaInfoCfg(MAPI_UrsaType* pUrsaInfo)
{
    m_UrsaInfo.PanelBitNums = pUrsaInfo->PanelBitNums;
    m_UrsaInfo.bTIMode = pUrsaInfo->bTIMode;
    m_UrsaInfo.bSwapPol = pUrsaInfo->bSwapPol;
    m_UrsaInfo.bShiftPair = pUrsaInfo->bShiftPair;
    m_UrsaInfo.bSwapPair = pUrsaInfo->bSwapPair;
    m_UrsaInfo.ucSwap = pUrsaInfo->ucSwap;
    m_UrsaInfo.ucSwing = pUrsaInfo->ucSwing;
    return TRUE;
}

MAPI_UrsaType* SystemInfo::GetUrsaInfo(void)
{
    return &m_UrsaInfo;
}

unsigned char SystemInfo::GetUrsaEnable()
{
    return m_u8UrsaEnable;
}

unsigned char SystemInfo::GetUrsaSelect()
{
    return m_u8UrsaSelect;
}

unsigned char SystemInfo::GetMEMCPanelEnable()
{
    return m_u8MEMCPanelEnable;
}

unsigned char SystemInfo::GetMEMCPanelSelect()
{
    return m_u8MEMCPanelSelect;
}
#endif

const TunerPWMInfo* SystemInfo::GetTunerPWMInfo()
{
    return &m_TunerPWMInfo;
}

unsigned char SystemInfo::GetAudioAmpSelect()
{
    return m_u8AudioAmpSelect;
}

unsigned char SystemInfo::Set_Eeprom_Type(unsigned char u8TypeID)
{
    if(u8TypeID >= RM_TYPE_NUM)
    {
        printf("Set EEPROM type error, the input EEPROM type is invalid!\n");
        return FALSE;
    }

    m_u8EepromType = u8TypeID;
    return TRUE;
}

unsigned char SystemInfo::Get_Eeprom_Type(void)
{
    if(m_u8EepromType >= RM_TYPE_NUM)
    {
        m_u8EepromType = RM_DEFAULT_TYPE;
        printf("Get EEPROM type error, the EEPROM type is invalid and use default!\n");
    }

    return m_u8EepromType;
}

unsigned char SystemInfo::SetHDCPKeyFileName(const char* strHDCPKeyFileName)
{
    if(strHDCPKeyFileName != NULL)
    {
        m_bHDCPKeyFileName.assign(strHDCPKeyFileName);
        return TRUE;
    }
    else
    {
        return FALSE;
    }

}

std::string SystemInfo::GetHDCPKeyFileName()
{
    return m_bHDCPKeyFileName;
}

#if (ENABLE_LITE_SN == 0)

MAPI_U16 SystemInfo::GetUseCustomerAVSyncDelay()
{
    return m_u16AvSyncDelay;
}
//for storage hdcp config
unsigned char SystemInfo::SetUseNandHdcpFlag(unsigned char bNandHdcpEnable)
{
#if (MSTAR_TVOS ==1 )
    m_bNandHdcpEnable = bNandHdcpEnable;
    return TRUE;
#else
    return FALSE;
#endif
}

unsigned char SystemInfo::GetUseNandHdcpFlag()
{
#if (MSTAR_TVOS ==1 )
    return m_bNandHdcpEnable;
#else
    return FALSE;
#endif
}

unsigned char SystemInfo::SetUseSPIHdcpFlag(unsigned char bSPIHdcpEnable)
{
#if (MSTAR_TVOS ==1 )
    m_bSPIHdcpEnable = bSPIHdcpEnable;
    return TRUE;
#else
    return FALSE;
#endif
}

unsigned char SystemInfo::GetUseSPIHdcpFlag()
{
#if (MSTAR_TVOS ==1 )
    return m_bSPIHdcpEnable;
#else
    return FALSE;
#endif
}

unsigned char SystemInfo::SetHdcpSPIBank(unsigned char u8HdcpSPIBank)
{
#if (MSTAR_TVOS ==1 )
        m_u8HdcpSPIBank = u8HdcpSPIBank;
        return TRUE;
#else
        return FALSE;
#endif
}

unsigned char SystemInfo::GetHdcpSPIBank()
{
#if (MSTAR_TVOS ==1 )
        return m_u8HdcpSPIBank;
#else
        return 0;
#endif
}


unsigned char SystemInfo::setHdcpSPIOffSet(unsigned short u16HdcpSPIOffset)
{
#if (MSTAR_TVOS ==1 )
        m_u16HdcpSPIOffset = u16HdcpSPIOffset;
        return TRUE;
#else
        return FALSE;
#endif
}

unsigned short SystemInfo::getHdcpSPIOffset()
{
#if (MSTAR_TVOS ==1 )
        return m_u16HdcpSPIOffset;
#else
        return 0;
#endif
}

unsigned char SystemInfo::SetUseEEPROMFlag(unsigned char bEEPROMHdcpEnable)
{
#if (MSTAR_TVOS ==1 )
        m_bEEPROMHdcpEnable = bEEPROMHdcpEnable;
        return TRUE;
#else
        return FALSE;
#endif
}

unsigned char SystemInfo::GetUseEEPROMFlag()
{
#if (MSTAR_TVOS ==1 )
        return m_bEEPROMHdcpEnable;
#else
        return FALSE;
#endif
}

unsigned char SystemInfo::SetHdcpEEPROMAddr(unsigned char u8HdcpEEPROMAddr)
{
#if (MSTAR_TVOS ==1 )
        m_u8HdcpEEPROMAddr = u8HdcpEEPROMAddr;
        return TRUE;
#else
        return FALSE;
#endif
}

unsigned char SystemInfo::GetHdcpEEPROMAddr()
{
#if (MSTAR_TVOS ==1 )
        return m_u8HdcpEEPROMAddr;
#else
        return 0;
#endif
}



unsigned char SystemInfo::SetUseSPIMacFlag(unsigned char bSPIMacEnable)
{
#if (MSTAR_TVOS ==1 )
        m_bSPIMacEnable = bSPIMacEnable;
        return TRUE;
#else
        return FALSE;
#endif
}



unsigned char SystemInfo::GetUseSPIMacFlag()
{
#if (MSTAR_TVOS ==1 )
        return m_bSPIMacEnable;
#else
        return FALSE;
#endif
}


unsigned char SystemInfo::SetMacSPIBank(unsigned char u8MacSPIBank)
{
#if (MSTAR_TVOS ==1 )
        m_u8MacSPIBank = u8MacSPIBank;
        return TRUE;
#else
        return FALSE;
#endif
}



unsigned char SystemInfo::GetMacSPIBank()
{
#if (MSTAR_TVOS ==1 )
        return m_u8MacSPIBank;
#else
        return 0;
#endif
}


unsigned char SystemInfo::setMacSPIOffSet(unsigned short u16MACSPIOffset)
{
#if (MSTAR_TVOS ==1 )
        m_u16MacSPIOffset = u16MACSPIOffset;
        return TRUE;
#else
        return FALSE;
#endif
}


unsigned short SystemInfo::getMacSPIOffset()
{
#if (MSTAR_TVOS ==1 )
        return m_u16MacSPIOffset;
#else
        return 0;
#endif
}


unsigned char SystemInfo::SetLocalDIMMINGFlag(unsigned char bLocalDIMMINGEnable)
{
#if (LOCAL_DIMMING ==1 )
        m_bLocalDIMMINGEnable = bLocalDIMMINGEnable;
        return TRUE;
#else
        return FALSE;
#endif
}

unsigned char SystemInfo::GetLocalDIMMINGFlag()
{
#if (LOCAL_DIMMING ==1 )
        return m_bLocalDIMMINGEnable;
#else
        return FALSE;
#endif
}

unsigned char SystemInfo::SetLocalDIMMINGPanelSelect(unsigned char u8LocalDIMMINGPanelSelect)
{
#if (LOCAL_DIMMING ==1 )
        m_u8LocalDIMMINGPanelSelect = u8LocalDIMMINGPanelSelect;
        return TRUE;
#else
        return FALSE;
#endif
}

unsigned char SystemInfo::GetLocalDIMMINGPanelSelect()
{
#if (LOCAL_DIMMING ==1 )
        return m_u8LocalDIMMINGPanelSelect;
#else
        return 0;
#endif
}
#endif

std::string SystemInfo::GetAMPBinPath()
{
    return pAmpInitBinPath;
}

unsigned char SystemInfo::SetVideoZoomInfo(MAPI_INPUT_SOURCE_TYPE enInputSrc, ST_MAPI_VIDEO_ZOOM_INFO *stZoomInfo)
{
    if(IsSrcAV(enInputSrc) == TRUE)
    {
        m_stCvbsZoomInfo[0].u8HCrop_Left = stZoomInfo[0].u8HCrop_Left;
        m_stCvbsZoomInfo[0].u8HCrop_Right = stZoomInfo[0].u8HCrop_Right;
        m_stCvbsZoomInfo[0].u8VCrop_Up = stZoomInfo[0].u8VCrop_Up;
        m_stCvbsZoomInfo[0].u8VCrop_Down = stZoomInfo[0].u8VCrop_Down;

        m_stCvbsZoomInfo[1].u8HCrop_Left = stZoomInfo[1].u8HCrop_Left;
        m_stCvbsZoomInfo[1].u8HCrop_Right = stZoomInfo[1].u8HCrop_Right;
        m_stCvbsZoomInfo[1].u8VCrop_Up = stZoomInfo[1].u8VCrop_Up;
        m_stCvbsZoomInfo[1].u8VCrop_Down = stZoomInfo[1].u8VCrop_Down;
    }
    else if(IsSrcComp(enInputSrc) == TRUE)
    {
        m_stYpbprZoomInfo[0].u8HCrop_Left = stZoomInfo[0].u8HCrop_Left;
        m_stYpbprZoomInfo[0].u8HCrop_Right = stZoomInfo[0].u8HCrop_Right;
        m_stYpbprZoomInfo[0].u8VCrop_Up = stZoomInfo[0].u8VCrop_Up;
        m_stYpbprZoomInfo[0].u8VCrop_Down = stZoomInfo[0].u8VCrop_Down;

        m_stYpbprZoomInfo[1].u8HCrop_Left = stZoomInfo[1].u8HCrop_Left;
        m_stYpbprZoomInfo[1].u8HCrop_Right = stZoomInfo[1].u8HCrop_Right;
        m_stYpbprZoomInfo[1].u8VCrop_Up = stZoomInfo[1].u8VCrop_Up;
        m_stYpbprZoomInfo[1].u8VCrop_Down = stZoomInfo[1].u8VCrop_Down;
    }
    else if(IsSrcHDMI(enInputSrc) == TRUE)
    {
        m_stHdmiZoomInfo[0].u8HCrop_Left = stZoomInfo[0].u8HCrop_Left;
        m_stHdmiZoomInfo[0].u8HCrop_Right = stZoomInfo[0].u8HCrop_Right;
        m_stHdmiZoomInfo[0].u8VCrop_Up = stZoomInfo[0].u8VCrop_Up;
        m_stHdmiZoomInfo[0].u8VCrop_Down = stZoomInfo[0].u8VCrop_Down;

        m_stHdmiZoomInfo[1].u8HCrop_Left = stZoomInfo[1].u8HCrop_Left;
        m_stHdmiZoomInfo[1].u8HCrop_Right = stZoomInfo[1].u8HCrop_Right;
        m_stHdmiZoomInfo[1].u8VCrop_Up = stZoomInfo[1].u8VCrop_Up;
        m_stHdmiZoomInfo[1].u8VCrop_Down = stZoomInfo[1].u8VCrop_Down;
    }
    else if(IsSrcDTV(enInputSrc) == TRUE)
    {
        m_stDtvZoomInfo[0].u8HCrop_Left = stZoomInfo[0].u8HCrop_Left;
        m_stDtvZoomInfo[0].u8HCrop_Right = stZoomInfo[0].u8HCrop_Right;
        m_stDtvZoomInfo[0].u8VCrop_Up = stZoomInfo[0].u8VCrop_Up;
        m_stDtvZoomInfo[0].u8VCrop_Down = stZoomInfo[0].u8VCrop_Down;

        m_stDtvZoomInfo[1].u8HCrop_Left = stZoomInfo[1].u8HCrop_Left;
        m_stDtvZoomInfo[1].u8HCrop_Right = stZoomInfo[1].u8HCrop_Right;
        m_stDtvZoomInfo[1].u8VCrop_Up = stZoomInfo[1].u8VCrop_Up;
        m_stDtvZoomInfo[1].u8VCrop_Down = stZoomInfo[1].u8VCrop_Down;
    }
    else if(enInputSrc == MAPI_INPUT_SOURCE_NONE)
    {
        m_stDefaultZoomInfo[0].u8HCrop_Left = stZoomInfo[0].u8HCrop_Left;
        m_stDefaultZoomInfo[0].u8HCrop_Right = stZoomInfo[0].u8HCrop_Right;
        m_stDefaultZoomInfo[0].u8VCrop_Up = stZoomInfo[0].u8VCrop_Up;
        m_stDefaultZoomInfo[0].u8VCrop_Down = stZoomInfo[0].u8VCrop_Down;

        m_stDefaultZoomInfo[1].u8HCrop_Left = stZoomInfo[1].u8HCrop_Left;
        m_stDefaultZoomInfo[1].u8HCrop_Right = stZoomInfo[1].u8HCrop_Right;
        m_stDefaultZoomInfo[1].u8VCrop_Up = stZoomInfo[1].u8VCrop_Up;
        m_stDefaultZoomInfo[1].u8VCrop_Down = stZoomInfo[1].u8VCrop_Down;
    }
    else
    {
        printf("SystemInfo::SetVideoZoomInfo(), input source is not found!!!\n");
        return FALSE;
    }
    return TRUE;
}

unsigned char SystemInfo::GetVideoZoomInfo(MAPI_INPUT_SOURCE_TYPE enInputSrc, ST_MAPI_VIDEO_ZOOM_INFO *stZoomInfo)
{
    if(IsSrcAV(enInputSrc) == TRUE)
    {
        stZoomInfo[0].u8HCrop_Left = m_stCvbsZoomInfo[0].u8HCrop_Left;
        stZoomInfo[0].u8HCrop_Right= m_stCvbsZoomInfo[0].u8HCrop_Right;
        stZoomInfo[0].u8VCrop_Up = m_stCvbsZoomInfo[0].u8VCrop_Up;
        stZoomInfo[0].u8VCrop_Down = m_stCvbsZoomInfo[0].u8VCrop_Down;

        stZoomInfo[1].u8HCrop_Left = m_stCvbsZoomInfo[1].u8HCrop_Left;
        stZoomInfo[1].u8HCrop_Right = m_stCvbsZoomInfo[1].u8HCrop_Right;
        stZoomInfo[1].u8VCrop_Up = m_stCvbsZoomInfo[1].u8VCrop_Up;
        stZoomInfo[1].u8VCrop_Down = m_stCvbsZoomInfo[1].u8VCrop_Down;
    }
    else if(IsSrcComp(enInputSrc) == TRUE)
    {
        stZoomInfo[0].u8HCrop_Left = m_stYpbprZoomInfo[0].u8HCrop_Left;
        stZoomInfo[0].u8HCrop_Right= m_stYpbprZoomInfo[0].u8HCrop_Right;
        stZoomInfo[0].u8VCrop_Up = m_stYpbprZoomInfo[0].u8VCrop_Up;
        stZoomInfo[0].u8VCrop_Down = m_stYpbprZoomInfo[0].u8VCrop_Down;

        stZoomInfo[1].u8HCrop_Left = m_stYpbprZoomInfo[1].u8HCrop_Left;
        stZoomInfo[1].u8HCrop_Right = m_stYpbprZoomInfo[1].u8HCrop_Right;
        stZoomInfo[1].u8VCrop_Up = m_stYpbprZoomInfo[1].u8VCrop_Up;
        stZoomInfo[1].u8VCrop_Down = m_stYpbprZoomInfo[1].u8VCrop_Down;

    }
    else if(IsSrcHDMI(enInputSrc) == TRUE)
    {
        stZoomInfo[0].u8HCrop_Left = m_stHdmiZoomInfo[0].u8HCrop_Left;
        stZoomInfo[0].u8HCrop_Right= m_stHdmiZoomInfo[0].u8HCrop_Right;
        stZoomInfo[0].u8VCrop_Up = m_stHdmiZoomInfo[0].u8VCrop_Up;
        stZoomInfo[0].u8VCrop_Down = m_stHdmiZoomInfo[0].u8VCrop_Down;

        stZoomInfo[1].u8HCrop_Left = m_stHdmiZoomInfo[1].u8HCrop_Left;
        stZoomInfo[1].u8HCrop_Right = m_stHdmiZoomInfo[1].u8HCrop_Right;
        stZoomInfo[1].u8VCrop_Up = m_stHdmiZoomInfo[1].u8VCrop_Up;
        stZoomInfo[1].u8VCrop_Down = m_stHdmiZoomInfo[1].u8VCrop_Down;

    }
    else if(IsSrcDTV(enInputSrc) == TRUE)
    {
        stZoomInfo[0].u8HCrop_Left = m_stDtvZoomInfo[0].u8HCrop_Left;
        stZoomInfo[0].u8HCrop_Right= m_stDtvZoomInfo[0].u8HCrop_Right;
        stZoomInfo[0].u8VCrop_Up = m_stDtvZoomInfo[0].u8VCrop_Up;
        stZoomInfo[0].u8VCrop_Down = m_stDtvZoomInfo[0].u8VCrop_Down;

        stZoomInfo[1].u8HCrop_Left = m_stDtvZoomInfo[1].u8HCrop_Left;
        stZoomInfo[1].u8HCrop_Right = m_stDtvZoomInfo[1].u8HCrop_Right;
        stZoomInfo[1].u8VCrop_Up = m_stDtvZoomInfo[1].u8VCrop_Up;
        stZoomInfo[1].u8VCrop_Down = m_stDtvZoomInfo[1].u8VCrop_Down;
    }
    else
    {
        printf("SystemInfo::GetVideoZoomInfo(), input source is not found. Use default!!!\n");
        stZoomInfo[0].u8HCrop_Left = m_stDefaultZoomInfo[0].u8HCrop_Left;
        stZoomInfo[0].u8HCrop_Right= m_stDefaultZoomInfo[0].u8HCrop_Right;
        stZoomInfo[0].u8VCrop_Up = m_stDefaultZoomInfo[0].u8VCrop_Up;
        stZoomInfo[0].u8VCrop_Down = m_stDefaultZoomInfo[0].u8VCrop_Down;

        stZoomInfo[1].u8HCrop_Left = m_stDefaultZoomInfo[1].u8HCrop_Left;
        stZoomInfo[1].u8HCrop_Right = m_stDefaultZoomInfo[1].u8HCrop_Right;
        stZoomInfo[1].u8VCrop_Up = m_stDefaultZoomInfo[1].u8VCrop_Up;
        stZoomInfo[1].u8VCrop_Down = m_stDefaultZoomInfo[1].u8VCrop_Down;
        return FALSE;
    }
    return TRUE;
}

unsigned char SystemInfo::GetHdcpKeyEnable(MAPI_INPUT_SOURCE_TYPE enInputSrc)
{
    if(enInputSrc < MAPI_INPUT_SOURCE_HDMI || enInputSrc >= MAPI_INPUT_SOURCE_HDMI_MAX)
    {
        printf("\t Only HDMI channel can get hdcp key enable\n");
        ASSERT(0);
        return FALSE;
    }
    unsigned char u8HdmiIdx = enInputSrc - MAPI_INPUT_SOURCE_HDMI;
    return m_bEnableHdcp[u8HdmiIdx];
}

void SystemInfo::ModifyI2CDevCfg(MAPI_U32 u32gID, unsigned char u8i2c_bus, unsigned char u8slave_id)
{
    if(m_i2cconfig.n_i2c_device != 0)
    {
        for(MAPI_U32 i = 0; i < m_i2cconfig.n_i2c_device; i++)
        {
            if(i == u32gID)
            {
                printf("Orig I2C Device[%u] ID = %u SlaveId = %u, Bus = %u\n", i, \
                m_pI2CDevices[i].gID, m_pI2CDevices[i].slave_id, \
                m_pI2CDevices[i].i2c_bus);

                printf("==>\n");
                m_pI2CDevices[i].slave_id = u8slave_id;
                m_pI2CDevices[i].i2c_bus = u8i2c_bus;

                printf("Orig I2C Device[%u] ID = %u SlaveId = %u, Bus = %u\n", i, \
                m_pI2CDevices[i].gID, m_pI2CDevices[i].slave_id, \
                m_pI2CDevices[i].i2c_bus);
            }
        }
    }
}

unsigned char SystemInfo::SetTspSectionFilterConfig(MAPI_SECTION_FILTER_CONFIG& rCfg)
{
    m_stTspSectionFilterCfg = rCfg;
    return TRUE;
}

const MAPI_SECTION_FILTER_CONFIG& SystemInfo::GetTspSectionFilterConfig()
{
    return m_stTspSectionFilterCfg;
}

MAPI_BOOL SystemInfo::SetPesFilterNumber(MAPI_U32 u32PesFilterNumber)
{
    m_u32PesFilterNumber = u32PesFilterNumber;
    return MAPI_TRUE;
}

const MAPI_U32 SystemInfo::GetPesFilterNumber()
{
    return m_u32PesFilterNumber;
}

void SystemInfo::SetPowerOnNetflixKey(MAPI_U8 u8PowerOnNetflixKey)
{
    m_u8PowerOnNetflixKey = u8PowerOnNetflixKey;
}

MAPI_U8 SystemInfo::GetPowerOnNetflixKey()
{
    return m_u8PowerOnNetflixKey;
}

MAPI_BOOL SystemInfo::GetFrcMode()
{
    MAPI_BOOL bEnable_4k2k_FRC = MAPI_FALSE;
    SystemInfo::GetInstance()->GetModuleParameter_bool("M_BACKEND:F_BACKEND_ENABLE_4K2K_FRC", &bEnable_4k2k_FRC);
    if(bEnable_4k2k_FRC == MAPI_TRUE)
    {
        return TRUE;
    }
        return FALSE;
}

#if (HDMITX_ENABLE == 1)
MAPI_U8 SystemInfo::GetPanelResolutionNum(void)
{
    dictionary *pPanelini = NULL;
    IEnvManager* pEnvMan = IEnvManager::Instance();
    if(pEnvMan)
    {
        pPanelini = iniparser_load(pEnvMan->GetEnv("panel_path"));
    }
    if (pPanelini == NULL)
    {
        pPanelini = iniparser_load("/config/panel/FullHD_CMO216_H1L01_Rocket.ini");
    }
    ASSERT(pPanelini);
    MAPI_U8 u8ResolutionNum = (MAPI_U8)iniparser_getint(pPanelini, "hdmi_tx:m_ResolutionNum", 0);
    iniparser_freedict(pPanelini);
    return u8ResolutionNum;
}
#endif
#if (STB_ENABLE == 1)
MAPI_U8 SystemInfo::GetPanelResolutionNum(void)
{
    dictionary *pPanelini = NULL;
    IEnvManager* pEnvMan = IEnvManager::Instance();
    if(pEnvMan)
    {
        pPanelini = iniparser_load(pEnvMan->GetEnv("panel_path"));
    }
    if (pPanelini == NULL)
    {
        printf("Get panel ini failed\n");
        ASSERT(pPanelini);
    }
    ASSERT(pPanelini);
    MAPI_U8 u8ResolutionNum = (MAPI_U8)iniparser_getint(pPanelini, "panel:m_ResolutionNum", 0);
    iniparser_freedict(pPanelini);
    return u8ResolutionNum;
}
#endif

#if (NETFLIX_ENABLE==1)
//set Netflix info
MAPI_BOOL SystemInfo::SetNetflixInfo(void)
{
    MAPI_U8 u8PowerOnNetflixKey = 0x00;

    u8PowerOnNetflixKey = iniparser_getint(m_pCustomerini, "Netflix:PowerOnNetflixKey", 0);

    SetPowerOnNetflixKey(u8PowerOnNetflixKey);

    return TRUE;
}
#endif

MAPI_BOOL SystemInfo::CmdLineParser(char *str,const char* keyword_string)
{
    FILE *cmdLine;
    char cmdLineBuf[CMD_LINE_SIZE];
    char *buf;
    char* pTypeStart = 0;
    char* pTypeEnd = 0;
    cmdLine=fopen(CMD_LINE_PATH, "r");
    if(cmdLine != NULL)
    {
        if (fgets(cmdLineBuf, CMD_LINE_SIZE, cmdLine) != NULL)
        {
            fclose(cmdLine);
            buf = strstr(cmdLineBuf, keyword_string);
            if (buf)
            {
                pTypeStart = strchr(buf,'=');
            }
            else
            {
                return FALSE;
            }
            if (pTypeStart)
            {
                pTypeEnd = strchr(buf,' ');
            }
            else
            {
                return FALSE;
            }
            if (pTypeEnd)
            {
                *pTypeEnd = 0;
            }
            else
            {
                return FALSE;
            }
            ++pTypeStart;
            memcpy(str,pTypeStart,strlen(pTypeStart)+1);
            return TRUE;
        }
        else
        {
            fclose(cmdLine);
            return FALSE;
        }
    }
    return FALSE;
}

MAPI_U32 SystemInfo::getOADCustomerOUI(void)
{
    return iniparser_getint(m_pCustomerini, "OAD:CUSTOMER_OUI", 0);
}

MAPI_U8 SystemInfo::getOADHWModel(void)
{
    return iniparser_getint(m_pCustomerini, "OAD:HW_MODEL", 0);
}

MAPI_U8 SystemInfo::getOADHWVersion(void)
{
    return iniparser_getint(m_pCustomerini, "OAD:HW_VERSION", 0);
}

MAPI_U16 SystemInfo::getOADAPSWModel(void)
{
    return iniparser_getint(m_pCustomerini, "OAD:AP_SW_MODEL", 0);
}

MAPI_U16 SystemInfo::getOADAPSWVersion(void)
{
    return iniparser_getint(m_pCustomerini, "OAD:AP_SW_VERSION", 0);
}

MAPI_U8 SystemInfo::getOADCustomerMakerID(void)
{
    return iniparser_getint(m_pCustomerini, "OAD:CUSTOMER_MAKER_ID", 0);
}

MAPI_U8 SystemInfo::getOADCustomerModelID(void)
{
    return iniparser_getint(m_pCustomerini, "OAD:CUSTOMER_MODEL_ID", 0);
}

MAPI_BOOL SystemInfo::CheckEnvironmentVariable(const char* name)
{
    MAPI_BOOL bRtpmFlag = FALSE;
    IEnvManager* pEnvMan = IEnvManager::Instance();
    if(pEnvMan != NULL)
    {
        if(pEnvMan->Initialize() == TRUE)
        {
            IEnvManager_scope_lock block(pEnvMan);
            MAPI_U32 size = pEnvMan->QueryLength(name);
            char tmp[size+1];
            memset(tmp, 0, size+1);
            MAPI_BOOL bResult = pEnvMan->GetEnv_Protect(name,tmp,size);
            if(bResult == TRUE && (strncmp(tmp, "1", 1) == 0))
            {
                bRtpmFlag = TRUE;
            }
        }
    }
    else
    {
        printf("---> IEnvManager Init Fail\n");
    }
    return bRtpmFlag;
}

void SystemInfo::CreateEmptyPanelInfo(MAPI_U16 u16Size)
{
    SYSTEM_INFO_FLOW("Allocate %d panel info memory.\n", u16Size);

    DestoryPanelInfo(m_u16MaxPanelSize);

    m_pPanelInfo = new (std::nothrow)PanelInfo_t[u16Size];
    for (MAPI_U16 i = 0 ; i < u16Size ; i++)
    {
        m_pPanelInfo[i].PanelAttr = NULL;
        m_pPanelInfo[i].u16PanelMinDCLK = 0;
        m_pPanelInfo[i].u16PanelDCLK = 0;
        m_pPanelInfo[i].u16PanelMaxDCLK = 0;
        m_pPanelInfo[i].u16PanelLinkExtType = 0;
        m_pPanelInfo[i].dPWMSim3DLRScale = 1.0;
    }
    m_u16PanelSize = 0;
    m_u16MaxPanelSize = u16Size;
    m_PanelMap.clear();
}

void SystemInfo::DestoryPanelInfo(MAPI_U16 u16Size)
{
    SYSTEM_INFO_FLOW("Free %d panel info memory.\n", u16Size);

    if (m_pPanelInfo != NULL)
    {
        for (MAPI_U16 i = 0 ; i < u16Size ; i++)
        {
            if (m_pPanelInfo[i].PanelAttr != NULL)
            {
                PanelType *ptr1 = (PanelType *)m_pPanelInfo[i].PanelAttr;
                if (ptr1->m_pPanelName != NULL)
                {
                    delete [] ptr1->m_pPanelName;
                    ptr1->m_pPanelName = NULL;
                }

                delete ptr1;
                ptr1 = NULL;
            }
            m_pPanelInfo[i].PanelAttr = NULL;
        }

        delete [] m_pPanelInfo;
        m_pPanelInfo = NULL;

    }

    m_u16PanelSize = 0;
    m_u16MaxPanelSize = 0;
    m_PanelMap.clear();
}

MAPI_BOOL SystemInfo::SetUseAudioDelayOffset(void)
{
    /* Read and assign the AudioDelay Offset*/
    SysIniBlock.u8AudioDelayOffset[MAPI_AudioDelay_SOURCE_DTV] = iniparser_getint(m_pCustomerini, "MISC:DTVAudioDelayOffset", 0);
    SysIniBlock.u8AudioDelayOffset[MAPI_AudioDelay_SOURCE_ATV] = iniparser_getint(m_pCustomerini, "MISC:ATVAudioDelayOffset", 0);
    SysIniBlock.u8AudioDelayOffset[MAPI_AudioDelay_SOURCE_CVBS] = iniparser_getint(m_pCustomerini, "MISC:CVBSAudioDelayOffset", 0);
    SysIniBlock.u8AudioDelayOffset[MAPI_AudioDelay_SOURCE_SVIDEO] = iniparser_getint(m_pCustomerini, "MISC:SVIDEOAudioDelayOffset", 0);
    SysIniBlock.u8AudioDelayOffset[MAPI_AudioDelay_SOURCE_SCART] = iniparser_getint(m_pCustomerini, "MISC:SCARTAudioDelayOffset", 0);
    SysIniBlock.u8AudioDelayOffset[MAPI_AudioDelay_SOURCE_YPBPR] = iniparser_getint(m_pCustomerini, "MISC:YPBPRAudioDelayOffset", 0);
    SysIniBlock.u8AudioDelayOffset[MAPI_AudioDelay_SOURCE_VGA] = iniparser_getint(m_pCustomerini, "MISC:VGAAudioDelayOffset", 0);
    SysIniBlock.u8AudioDelayOffset[MAPI_AudioDelay_SOURCE_HDMI] = iniparser_getint(m_pCustomerini, "MISC:HDMIAudioDelayOffset", 0);
    SysIniBlock.u8AudioDelayOffset[MAPI_AudioDelay_SOURCE_STORAGE] = iniparser_getint(m_pCustomerini, "MISC:STORAGEAudioDelayOffset", 0);
    return TRUE;
}

MAPI_U8 SystemInfo::GetUseAudioDelayOffset(MAPI_INPUT_SOURCE_TYPE enInputSrc)
{
    switch(enInputSrc)
    {
        case MAPI_INPUT_SOURCE_DTV:
            m_u8UseAudioDelayOffset = SysIniBlock.u8AudioDelayOffset[MAPI_AudioDelay_SOURCE_DTV];
            break;
        case MAPI_INPUT_SOURCE_ATV:
            m_u8UseAudioDelayOffset = SysIniBlock.u8AudioDelayOffset[MAPI_AudioDelay_SOURCE_ATV];
            break;
        case MAPI_INPUT_SOURCE_CVBS:
        case MAPI_INPUT_SOURCE_CVBS2:
            m_u8UseAudioDelayOffset = SysIniBlock.u8AudioDelayOffset[MAPI_AudioDelay_SOURCE_CVBS];
            break;
        case MAPI_INPUT_SOURCE_SVIDEO:
            m_u8UseAudioDelayOffset = SysIniBlock.u8AudioDelayOffset[MAPI_AudioDelay_SOURCE_SVIDEO];
            break;
        case MAPI_INPUT_SOURCE_SCART:
        case MAPI_INPUT_SOURCE_SCART2:
            m_u8UseAudioDelayOffset = SysIniBlock.u8AudioDelayOffset[MAPI_AudioDelay_SOURCE_SCART];
            break;
        case MAPI_INPUT_SOURCE_YPBPR:
        case MAPI_INPUT_SOURCE_YPBPR2:
            m_u8UseAudioDelayOffset = SysIniBlock.u8AudioDelayOffset[MAPI_AudioDelay_SOURCE_YPBPR];
            break;
        case MAPI_INPUT_SOURCE_VGA:
        case MAPI_INPUT_SOURCE_DVI:
            m_u8UseAudioDelayOffset = SysIniBlock.u8AudioDelayOffset[MAPI_AudioDelay_SOURCE_VGA];
            break;
        case MAPI_INPUT_SOURCE_HDMI:
        case MAPI_INPUT_SOURCE_HDMI2:
        case MAPI_INPUT_SOURCE_HDMI3:
        case MAPI_INPUT_SOURCE_HDMI4:
            m_u8UseAudioDelayOffset = SysIniBlock.u8AudioDelayOffset[MAPI_AudioDelay_SOURCE_HDMI];
            break;
        case MAPI_INPUT_SOURCE_STORAGE:
        case MAPI_INPUT_SOURCE_STORAGE2:
        case MAPI_INPUT_SOURCE_KTV:
            m_u8UseAudioDelayOffset = SysIniBlock.u8AudioDelayOffset[MAPI_AudioDelay_SOURCE_STORAGE];
            break;
        default:
            break;
    }
    return m_u8UseAudioDelayOffset;
}

MAPI_BOOL SystemInfo::SetUseSPDIFDelayOffset(void)
{
    /* Read and assign the SPDIFDelay Offset*/
    SysIniBlock.u8SPDIFDelayOffset[MAPI_AudioDelay_SOURCE_DTV] = iniparser_getint(m_pCustomerini, "MISC:DTVSPDIFDelayOffset", 0);
    SysIniBlock.u8SPDIFDelayOffset[MAPI_AudioDelay_SOURCE_ATV] = iniparser_getint(m_pCustomerini, "MISC:ATVSPDIFDelayOffset", 0);
    SysIniBlock.u8SPDIFDelayOffset[MAPI_AudioDelay_SOURCE_CVBS] = iniparser_getint(m_pCustomerini, "MISC:CVBSSPDIFDelayOffset", 0);
    SysIniBlock.u8SPDIFDelayOffset[MAPI_AudioDelay_SOURCE_SVIDEO] = iniparser_getint(m_pCustomerini, "MISC:SVIDEOSPDIFDelayOffset", 0);
    SysIniBlock.u8SPDIFDelayOffset[MAPI_AudioDelay_SOURCE_SCART] = iniparser_getint(m_pCustomerini, "MISC:SCARTSPDIFDelayOffset", 0);
    SysIniBlock.u8SPDIFDelayOffset[MAPI_AudioDelay_SOURCE_YPBPR] = iniparser_getint(m_pCustomerini, "MISC:YPBPRSPDIFDelayOffset", 0);
    SysIniBlock.u8SPDIFDelayOffset[MAPI_AudioDelay_SOURCE_VGA] = iniparser_getint(m_pCustomerini, "MISC:VGASPDIFDelayOffset", 0);
    SysIniBlock.u8SPDIFDelayOffset[MAPI_AudioDelay_SOURCE_HDMI] = iniparser_getint(m_pCustomerini, "MISC:HDMISPDIFDelayOffset", 0);
    SysIniBlock.u8SPDIFDelayOffset[MAPI_AudioDelay_SOURCE_STORAGE] = iniparser_getint(m_pCustomerini, "MISC:STORAGESPDIFDelayOffset", 0);
    return TRUE;
}

MAPI_U8 SystemInfo::GetUseSPDIFDelayOffset(MAPI_INPUT_SOURCE_TYPE enInputSrc)
{
    switch(enInputSrc)
    {
        case MAPI_INPUT_SOURCE_DTV:
            m_u8UseSPDIFDelayOffset = SysIniBlock.u8SPDIFDelayOffset[MAPI_AudioDelay_SOURCE_DTV];
            break;
        case MAPI_INPUT_SOURCE_ATV:
            m_u8UseSPDIFDelayOffset = SysIniBlock.u8SPDIFDelayOffset[MAPI_AudioDelay_SOURCE_ATV];
            break;
        case MAPI_INPUT_SOURCE_CVBS:
        case MAPI_INPUT_SOURCE_CVBS2:
            m_u8UseSPDIFDelayOffset = SysIniBlock.u8SPDIFDelayOffset[MAPI_AudioDelay_SOURCE_CVBS];
            break;
        case MAPI_INPUT_SOURCE_SVIDEO:
            m_u8UseSPDIFDelayOffset = SysIniBlock.u8SPDIFDelayOffset[MAPI_AudioDelay_SOURCE_SVIDEO];
            break;
        case MAPI_INPUT_SOURCE_SCART:
        case MAPI_INPUT_SOURCE_SCART2:
            m_u8UseSPDIFDelayOffset = SysIniBlock.u8SPDIFDelayOffset[MAPI_AudioDelay_SOURCE_SCART];
            break;
        case MAPI_INPUT_SOURCE_YPBPR:
        case MAPI_INPUT_SOURCE_YPBPR2:
            m_u8UseSPDIFDelayOffset = SysIniBlock.u8SPDIFDelayOffset[MAPI_AudioDelay_SOURCE_YPBPR];
            break;
        case MAPI_INPUT_SOURCE_VGA:
        case MAPI_INPUT_SOURCE_VGA2:
        case MAPI_INPUT_SOURCE_VGA3:
        case MAPI_INPUT_SOURCE_DVI:
            m_u8UseSPDIFDelayOffset = SysIniBlock.u8SPDIFDelayOffset[MAPI_AudioDelay_SOURCE_VGA];
            break;
        case MAPI_INPUT_SOURCE_HDMI:
        case MAPI_INPUT_SOURCE_HDMI2:
        case MAPI_INPUT_SOURCE_HDMI3:
        case MAPI_INPUT_SOURCE_HDMI4:
            m_u8UseSPDIFDelayOffset = SysIniBlock.u8SPDIFDelayOffset[MAPI_AudioDelay_SOURCE_HDMI];
            break;
        case MAPI_INPUT_SOURCE_STORAGE:
        case MAPI_INPUT_SOURCE_STORAGE2:
        case MAPI_INPUT_SOURCE_KTV:
            m_u8UseSPDIFDelayOffset = SysIniBlock.u8SPDIFDelayOffset[MAPI_AudioDelay_SOURCE_STORAGE];
            break;
        default:
            break;
    }
    return m_u8UseSPDIFDelayOffset;
}

MAPI_U8 SystemInfo::GetScart1SarChannel()
{
    return SCART_ID1_SAR_CHANNEL;
}

MAPI_U8 SystemInfo::GetScart2SarChannel()
{
    return SCART_ID2_SAR_CHANNEL;
}

MAPI_U8 SystemInfo::GetCECPortSelect()
{
#ifdef CEC1_PORT_SELECT
    return CEC1_PORT_SELECT;
#else
    return 0x00;
#endif
}

MAPI_U16 SystemInfo::GetSupportedTimingList(EN_MAPI_TIMING *pTimingList, MAPI_U16 u16ListSize)
{
    MAPI_U16 u16Count = (MAPI_U16)m_PanelPathMap.size();

    if ((pTimingList == NULL) || (u16ListSize == 0))
    {
        // If pTimingList is NULL, return supported timing.
        return u16Count;
    }

    if (u16ListSize < u16Count)
    {
        // Allocate EN_MAPI_TIMING array (pTimingList) is less than supported timing,  it returns 0.
        return 0;
    }

    u16Count = 0;
    std::map<EN_MAPI_TIMING, std::string>::iterator iter = m_PanelPathMap.begin();
    while (iter != m_PanelPathMap.end())
    {
        pTimingList[u16Count++] = iter->first;
        iter++;
    }

    return u16Count;
}

MAPI_BOOL SystemInfo::SetActivePanel(EN_MAPI_TIMING enTiming)
{
    SYSTEM_INFO_FLOW("Specify active panel timing is %x.\n", enTiming);

    const PanelInfo_t* const pPanelInfo = GetPanel(enTiming);

    if (pPanelInfo == NULL)
    {
        ASSERT(0);
        return MAPI_FALSE;
    }

    m_enActivePanelTiming = enTiming;
    return MAPI_TRUE;
}

EN_MAPI_TIMING SystemInfo::GetActivePanelTiming()
{
    SYSTEM_INFO_FLOW("Active timing is  %x.\n", m_enActivePanelTiming);
    return m_enActivePanelTiming;
}

std::string SystemInfo::GetActivePanelName()
{
    SYSTEM_INFO_FLOW("Active timing is  %x.\n", m_enActivePanelTiming);
    return GetPanelName(m_enActivePanelTiming);
}

const PanelInfo_t* SystemInfo::GetActivePanel()
{
    SYSTEM_INFO_FLOW("Active timing is  %x.\n", m_enActivePanelTiming);
    return GetPanel(m_enActivePanelTiming);
}

std::string SystemInfo::GetPanelName(EN_MAPI_TIMING enTiming)
{
    SYSTEM_INFO_FLOW("Specified panel timing is  %x.\n", enTiming);

    const PanelInfo_t* const pPanelInfo = GetPanel(enTiming);

    if (pPanelInfo == NULL)
    {
        ASSERT(0);
        return "";
    }

    PanelType *pPnl = (PanelType*)(*pPanelInfo).PanelAttr;
    return std::string(pPnl->m_pPanelName);
}

const PanelInfo_t* SystemInfo::GetPanel(EN_MAPI_TIMING enTiming)
{
    SYSTEM_INFO_FLOW("Specified panel timing is  %x.\n", enTiming);
    std::map<EN_MAPI_TIMING, PanelInfo_t *>::iterator iter = m_PanelMap.find(enTiming);
    if (iter != m_PanelMap.end())
    {
        if (iter->second == NULL)
        {
            // No specified panel exists.
            ASSERT(0);
            return NULL;
        }
        else
        {
            return iter->second;
        }
    }
    else
    {
        // Check panel is specified in m_PanelPathMap.
        std::map<EN_MAPI_TIMING, std::string>::iterator iter1 = m_PanelPathMap.find(enTiming);
        if (iter1 == m_PanelPathMap.end())
        {
            // Panel doesn't specify in m_PanelPathMap.
            SYSTEM_INFO_ERR("Specified panel %x doesn't exist in m_PanelPathMap.\n", enTiming);
            m_PanelMap[enTiming] = NULL;
            ASSERT(0);
            return NULL;
        }
        else
        {
            // Load panel which is specified in m_PanelPathMap.
            if (SetPanelInfo(enTiming) == MAPI_FALSE)
            {
                SYSTEM_INFO_ERR("Load panel %x fail.\n", enTiming);
                m_PanelMap[enTiming] = NULL;
                ASSERT(0);
                return NULL;
            }
        }
    }

    iter = m_PanelMap.find(enTiming);
    if (iter == m_PanelMap.end())
    {
        SYSTEM_INFO_ERR("Specified panel %x doesn't exist.\n", enTiming);
        ASSERT(0);
        return NULL;
    }

    if (iter->second == NULL)
    {
        SYSTEM_INFO_ERR("Specified panel %x doesn't exist.\n", enTiming);
        ASSERT(0);
        return NULL;
    }

    return iter->second;
}

MAPI_BOOL SystemInfo::SetHDMIEDIDVersionList(void)
{
    const char *pHdmiEdidVersion[E_HDMI_EDID_NUM] =
    {
        "HDMI_EDID_VERSION:bHDMI_EDID_DEFAULT",
        "HDMI_EDID_VERSION:bHDMI_EDID_1.4",
        "HDMI_EDID_VERSION:bHDMI_EDID_2.0"
    };

    if(m_pU32HdmiEdidVersionList != NULL)
    {
        delete []m_pU32HdmiEdidVersionList;
        m_pU32HdmiEdidVersionList = NULL;
    }

    m_pU32HdmiEdidVersionList = new (std::nothrow) MAPI_U32[E_HDMI_EDID_NUM];
    ASSERT(m_pU32HdmiEdidVersionList);
    for (int i = 0; i < E_HDMI_EDID_NUM; i++)
    {
        m_pU32HdmiEdidVersionList[i] = iniparser_getboolean(m_pCustomerini, pHdmiEdidVersion[i], 0);
    }

    return TRUE;
}

const MAPI_U32* SystemInfo::GetHDMIEDIDVersionList()
{
    if(m_pU32HdmiEdidVersionList != NULL)
    {
        return m_pU32HdmiEdidVersionList;
    }

    ASSERT(0);
}

MAPI_BOOL SystemInfo::UpdataHDMIEDIDInfoSet(EN_HDMI_EDID_VERSION eHdmiEdidVersion)
{
#if ((STB_ENABLE == 0) || (ENABLE_HDMI_RX == 1))
    HDMI_EDID_Info_t stHdmiEdidInfo[BOARD_HDMI_EDID_InfoCount];

    ASSERT(m_pCustomerini);

    /* start to load HDMI EDID Info for all HDMI ports */
    for(int i = 0; i < BOARD_HDMI_EDID_InfoCount; i++)
    {
        /* reset the stHdmiEdidInfo[i] data */
        memset(&stHdmiEdidInfo[i], 0, sizeof(HDMI_EDID_Info_t));

        /* load HDMI EDID Info from INI file. */
        if(SystemInfo::LoadHDMIEDIDInfo(&stHdmiEdidInfo[i], m_pCustomerini, i, eHdmiEdidVersion) == FALSE)
        {
            SYSTEM_INFO_ERR("Load HDMI EDID %d Failed!\n", i + 1);
        }
    }

    m_HdmiEdidInfoSet.u8HDMI_EDID_InfoCount = BOARD_HDMI_EDID_InfoCount;
    memcpy(m_HdmiEdidInfoSet.pstHDMIEDIDInfos, &(stHdmiEdidInfo), sizeof(HDMI_EDID_Info_t)*BOARD_HDMI_EDID_InfoCount);

#if (SYSINFO_DEBUG == 1)
    printf("-------------------HDMIEDIDInfoSet Start-------------------\n");
    printf("HdmiEdidInfoSet Size = %u\n", m_HdmiEdidInfoSet.u8HDMI_EDID_InfoCount);
    for(int i = 0; i < m_HdmiEdidInfoSet.u8HDMI_EDID_InfoCount ; i++)
    {
        printf("HdmiEdidInfoSet[%d] = (%s, %s, %s, %u, %u, %u )\n", \
               i, (TRUE==m_HdmiEdidInfoSet.pstHDMIEDIDInfos[i].bEDIDEnabled) ? "True" : "False", \
               (TRUE==m_HdmiEdidInfoSet.pstHDMIEDIDInfos[i].bUseDefaultValue) ? "True" : "False", \
               (TRUE==m_HdmiEdidInfoSet.pstHDMIEDIDInfos[i].bCECEnabled) ? "True" : "False", \
               m_HdmiEdidInfoSet.pstHDMIEDIDInfos[i].u16CECPhyAddr, \
               m_HdmiEdidInfoSet.pstHDMIEDIDInfos[i].u8CECPhyAddrIdxL, \
               m_HdmiEdidInfoSet.pstHDMIEDIDInfos[i].u8CECPhyAddrIdxH);

        for(int j = 0; j < 256; j++)
        {
            printf("0x%02x, ", m_HdmiEdidInfoSet.pstHDMIEDIDInfos[i].edid[j]);
            if((j + 1) % 16 == 0)
                printf("\n");
        }
        printf("\n");
    }
    printf("-------------------HDMIEDIDInfoSet End---------------------\n\n");
#endif
#endif
    return TRUE;
}

MAPI_BOOL SystemInfo::UpdataHDMIEDIDInfoSet(int iHdimEdidIndex, EN_HDMI_EDID_VERSION eHdmiEdidVersion)
{
#if ((STB_ENABLE == 0) || (ENABLE_HDMI_RX == 1))
    if(iHdimEdidIndex >= BOARD_HDMI_EDID_InfoCount)
    {
        ASSERT(0);
    }

    ASSERT(m_pCustomerini);

    HDMI_EDID_Info_t stHdmiEdidInfo;

    /* start to load HDMI EDID Info */

    /* reset the stHdmiEdidInfo[i] data */
    memset(&stHdmiEdidInfo, 0, sizeof(HDMI_EDID_Info_t));

    /* load HDMI EDID Info from INI file. */
    if(SystemInfo::LoadHDMIEDIDInfo(&stHdmiEdidInfo, m_pCustomerini, iHdimEdidIndex, eHdmiEdidVersion) == FALSE)
    {
        SYSTEM_INFO_ERR("Load HDMI EDID %d Failed!\n", iHdimEdidIndex + 1);
        return FALSE;
    }

    m_HdmiEdidInfoSet.u8HDMI_EDID_InfoCount = BOARD_HDMI_EDID_InfoCount;
    memcpy(&m_HdmiEdidInfoSet.pstHDMIEDIDInfos[iHdimEdidIndex], &(stHdmiEdidInfo), sizeof(HDMI_EDID_Info_t));

#if (SYSINFO_DEBUG == 1)
    printf("-------------------HDMIEDIDInfoSet Start-------------------\n");
    printf("HdmiEdidVersion = %d\n", eHdmiEdidVersion);
    printf("HdmiEdidInfoSet Size = %u\n", m_HdmiEdidInfoSet.u8HDMI_EDID_InfoCount);

    for(int i = 0; i < m_HdmiEdidInfoSet.u8HDMI_EDID_InfoCount ; i++)
    {
        printf("HdmiEdidInfoSet[%d] = (%s, %s, %s, %u, %u, %u )\n", \
               i, (TRUE==m_HdmiEdidInfoSet.pstHDMIEDIDInfos[i].bEDIDEnabled) ? "True" : "False", \
               (TRUE==m_HdmiEdidInfoSet.pstHDMIEDIDInfos[i].bUseDefaultValue) ? "True" : "False", \
               (TRUE==m_HdmiEdidInfoSet.pstHDMIEDIDInfos[i].bCECEnabled) ? "True" : "False", \
               m_HdmiEdidInfoSet.pstHDMIEDIDInfos[i].u16CECPhyAddr, \
               m_HdmiEdidInfoSet.pstHDMIEDIDInfos[i].u8CECPhyAddrIdxL, \
               m_HdmiEdidInfoSet.pstHDMIEDIDInfos[i].u8CECPhyAddrIdxH);

        for(int j = 0; j < 256; j++)
        {
            printf("0x%02x, ", m_HdmiEdidInfoSet.pstHDMIEDIDInfos[i].edid[j]);
            if((j + 1) % 16 == 0)
                printf("\n");
        }
        printf("\n");
    }
    printf("-------------------HDMIEDIDInfoSet End---------------------\n\n");
#endif
#endif
    return TRUE;
}

MAPI_BOOL SystemInfo::LoadHDMIEDIDInfo(HDMI_EDID_Info_t *pHdmiEdidInfo, void *pDict, int iHdmiNum, EN_HDMI_EDID_VERSION eHdmiEdidVersion)
{
    char *pHdmiEdidFile = NULL;
    dictionary *pEdidIni = (dictionary *)pDict;

    ASSERT(pEdidIni);

    switch(iHdmiNum)
    {
    case 0:
        /* HDMI EDID #1 */
        pHdmiEdidInfo->bEDIDEnabled = iniparser_getboolean(pEdidIni, "HDMI_EDID_1:bEDIDEnabled", -1);
        pHdmiEdidInfo->bUseDefaultValue = iniparser_getboolean(pEdidIni, "HDMI_EDID_1:bUseDefaultValue", -1);
        pHdmiEdidInfo->bCECEnabled = iniparser_getboolean(pEdidIni, "HDMI_EDID_1:bCECEnabled", -1);
        pHdmiEdidInfo->u16CECPhyAddr = iniparser_getint(pEdidIni, "HDMI_EDID_1:u16CECPhyAddr", -1);
        pHdmiEdidInfo->u8CECPhyAddrIdxL = iniparser_getint(pEdidIni, "HDMI_EDID_1:u8CECPhyAddrIdxL", -1);
        pHdmiEdidInfo->u8CECPhyAddrIdxH = iniparser_getint(pEdidIni, "HDMI_EDID_1:u8CECPhyAddrIdxH", -1);
        switch(eHdmiEdidVersion)
        {
            case E_HDMI_EDID_1_4:
                pHdmiEdidFile = iniparser_getstring(pEdidIni, "HDMI_EDID_1:HDMI_EDID_File_1_4", NULL);
                break;
            case E_HDMI_EDID_2_0:
                pHdmiEdidFile = iniparser_getstring(pEdidIni, "HDMI_EDID_1:HDMI_EDID_File_2_0", NULL);
                break;
            default:
                pHdmiEdidFile = iniparser_getstring(pEdidIni, "HDMI_EDID_1:HDMI_EDID_File", NULL);
                break;
        }
        break;
    case 1:
        /* HDMI EDID #2 */
        pHdmiEdidInfo->bEDIDEnabled = iniparser_getboolean(pEdidIni, "HDMI_EDID_2:bEDIDEnabled", -1);
        pHdmiEdidInfo->bUseDefaultValue = iniparser_getboolean(pEdidIni, "HDMI_EDID_2:bUseDefaultValue", -1);
        pHdmiEdidInfo->bCECEnabled = iniparser_getboolean(pEdidIni, "HDMI_EDID_2:bCECEnabled", -1);
        pHdmiEdidInfo->u16CECPhyAddr = iniparser_getint(pEdidIni, "HDMI_EDID_2:u16CECPhyAddr", -1);
        pHdmiEdidInfo->u8CECPhyAddrIdxL = iniparser_getint(pEdidIni, "HDMI_EDID_2:u8CECPhyAddrIdxL", -1);
        pHdmiEdidInfo->u8CECPhyAddrIdxH = iniparser_getint(pEdidIni, "HDMI_EDID_2:u8CECPhyAddrIdxH", -1);
        switch(eHdmiEdidVersion)
        {
            case E_HDMI_EDID_1_4:
                pHdmiEdidFile = iniparser_getstring(pEdidIni, "HDMI_EDID_2:HDMI_EDID_File_1_4", NULL);
                break;
            case E_HDMI_EDID_2_0:
                pHdmiEdidFile = iniparser_getstring(pEdidIni, "HDMI_EDID_2:HDMI_EDID_File_2_0", NULL);
                break;
            default:
                pHdmiEdidFile = iniparser_getstring(pEdidIni, "HDMI_EDID_2:HDMI_EDID_File", NULL);
                break;
        }
        break;
    case 2:
        /* HDMI EDID #3 */
        pHdmiEdidInfo->bEDIDEnabled = iniparser_getboolean(pEdidIni, "HDMI_EDID_3:bEDIDEnabled", -1);
        pHdmiEdidInfo->bUseDefaultValue = iniparser_getboolean(pEdidIni, "HDMI_EDID_3:bUseDefaultValue", -1);
        pHdmiEdidInfo->bCECEnabled = iniparser_getboolean(pEdidIni, "HDMI_EDID_3:bCECEnabled", -1);
        pHdmiEdidInfo->u16CECPhyAddr = iniparser_getint(pEdidIni, "HDMI_EDID_3:u16CECPhyAddr", -1);
        pHdmiEdidInfo->u8CECPhyAddrIdxL = iniparser_getint(pEdidIni, "HDMI_EDID_3:u8CECPhyAddrIdxL", -1);
        pHdmiEdidInfo->u8CECPhyAddrIdxH = iniparser_getint(pEdidIni, "HDMI_EDID_3:u8CECPhyAddrIdxH", -1);
        switch(eHdmiEdidVersion)
        {
            case E_HDMI_EDID_1_4:
                pHdmiEdidFile = iniparser_getstring(pEdidIni, "HDMI_EDID_3:HDMI_EDID_File_1_4", NULL);
                break;
            case E_HDMI_EDID_2_0:
                pHdmiEdidFile = iniparser_getstring(pEdidIni, "HDMI_EDID_3:HDMI_EDID_File_2_0", NULL);
                break;
            default:
                pHdmiEdidFile = iniparser_getstring(pEdidIni, "HDMI_EDID_3:HDMI_EDID_File", NULL);
                break;
        }
        break;
    case 3:
        /* HDMI EDID #4 */
        pHdmiEdidInfo->bEDIDEnabled = iniparser_getboolean(pEdidIni, "HDMI_EDID_4:bEDIDEnabled", -1);
        pHdmiEdidInfo->bUseDefaultValue = iniparser_getboolean(pEdidIni, "HDMI_EDID_4:bUseDefaultValue", -1);
        pHdmiEdidInfo->bCECEnabled = iniparser_getboolean(pEdidIni, "HDMI_EDID_4:bCECEnabled", -1);
        pHdmiEdidInfo->u16CECPhyAddr = iniparser_getint(pEdidIni, "HDMI_EDID_4:u16CECPhyAddr", -1);
        pHdmiEdidInfo->u8CECPhyAddrIdxL = iniparser_getint(pEdidIni, "HDMI_EDID_4:u8CECPhyAddrIdxL", -1);
        pHdmiEdidInfo->u8CECPhyAddrIdxH = iniparser_getint(pEdidIni, "HDMI_EDID_4:u8CECPhyAddrIdxH", -1);
        switch(eHdmiEdidVersion)
        {
            case E_HDMI_EDID_1_4:
                pHdmiEdidFile = iniparser_getstring(pEdidIni, "HDMI_EDID_4:HDMI_EDID_File_1_4", NULL);
                break;
            case E_HDMI_EDID_2_0:
                pHdmiEdidFile = iniparser_getstring(pEdidIni, "HDMI_EDID_4:HDMI_EDID_File_2_0", NULL);
                break;
            default:
                pHdmiEdidFile = iniparser_getstring(pEdidIni, "HDMI_EDID_4:HDMI_EDID_File", NULL);
                break;
        }
        break;
    default:
        SYSTEM_INFO_ERR("Out of range!\n");
        return FALSE;
    }

    /* check the HDMI EDID file */
    if((pHdmiEdidFile == NULL) || (strlen(pHdmiEdidFile) == 0))
    {
        SYSTEM_INFO_ERR("Can't open HDMI EDID %d File!\n", iHdmiNum + 1);
        return FALSE;
    }

    /* open the HDMI_EDID.bin */
    FILE *pBinFile = fopen((const char*) pHdmiEdidFile, "r");
    if(pBinFile == NULL)
    {
        SYSTEM_INFO_ERR("BIN file open error!\n");
        return FALSE;
    }

    MAPI_U8 u8Buf[HDMI_EDID_LEN];

    /* reset the buffer */
    memset(u8Buf, 0, HDMI_EDID_LEN);

    /* read the bin file (256 bytes) */
    size_t szBufLen = fread(u8Buf, 1, HDMI_EDID_LEN, pBinFile);
    if(szBufLen == HDMI_EDID_LEN)
    {
        /* correct! copy the bin file buffer to EDID[] */
        memcpy(pHdmiEdidInfo->edid, u8Buf, HDMI_EDID_LEN);
    }
    else
    {
        SYSTEM_INFO_ERR("Read an error length in the Bin file! (HDMI EDID %d)\n", iHdmiNum + 1);
    }

    /* close the bin file*/
    fclose(pBinFile);

    return TRUE;
}

MAPI_U8 SystemInfo::GetHDMIEDIDInfoCount()
{
#if ((STB_ENABLE == 0) || (ENABLE_HDMI_RX == 1))
    return BOARD_HDMI_EDID_InfoCount;
#else
    return 0;
#endif
}

void SystemInfo::ParseVb1ChannelOrder(dictionary *pCustomerIni, const char *pKey, MAPI_U16 u16Order[4])
{
    memset(u16Order, 0, sizeof(MAPI_U16)*4);

    MAPI_U8 channelOrder[7];
    memset(channelOrder, 0, sizeof(channelOrder));

    if (iniparser_getU8array(pCustomerIni, pKey, 7, channelOrder) == MAPI_TRUE)
    {
        u16Order[0] = (u16Order[0] << 8) + channelOrder[0];
        u16Order[0] = (u16Order[0] << 8) + channelOrder[1];
        u16Order[1] = (u16Order[1] << 8) + channelOrder[2];
        u16Order[1] = (u16Order[1] << 8) + channelOrder[3];
        u16Order[2] = (u16Order[2] << 8) + channelOrder[4];
        u16Order[2] = (u16Order[2] << 8) + channelOrder[5];
        u16Order[3] = (u16Order[3] << 8) + channelOrder[6];
    }
}

MAPI_BOOL SystemInfo::GetVB1ChannelOrder(EN_MAPI_TIMING enVideo, EN_MAPI_TIMING enOsd, MAPI_U16 u16Order[4])
{
    memset(u16Order, 0, sizeof(MAPI_U16)*4);

    if (enVideo == E_MAPI_TIMING_DEFAULT)
    {
        enVideo = GetPanelTiming(0);
    }

    if (enVideo >= E_MAPI_TIMING_2K1K && enVideo < E_MAPI_TIMING_2K1K_MAX)
    {
        u16Order[0] |= m_u16Vb12VChannelOrder[0];
        u16Order[1] |= m_u16Vb12VChannelOrder[1];
        u16Order[2] |= m_u16Vb12VChannelOrder[2];
        u16Order[3] |= m_u16Vb12VChannelOrder[3];
    }
    else if (enVideo == E_MAPI_TIMING_4K2K || (enVideo >= E_MAPI_TIMING_4K2KP_30 && enVideo <= E_MAPI_TIMING_4K2KP_24))
    {
        u16Order[0] |= m_u16Vb14VChannelOrder[0];
        u16Order[1] |= m_u16Vb14VChannelOrder[1];
        u16Order[2] |= m_u16Vb14VChannelOrder[2];
        u16Order[3] |= m_u16Vb14VChannelOrder[3];
    }
    else if (enVideo >= E_MAPI_TIMING_4K2KP_60 && enVideo <= E_MAPI_TIMING_4K2KP_50)
    {
        u16Order[0] |= m_u16Vb18VChannelOrder[0];
        u16Order[1] |= m_u16Vb18VChannelOrder[1];
        u16Order[2] |= m_u16Vb18VChannelOrder[2];
        u16Order[3] |= m_u16Vb18VChannelOrder[3];
    }
    else if (enVideo == E_MAPI_TIMING_4096x2160P_24)
    {
        u16Order[0] |= m_u16Vb14VChannelOrder[0];
        u16Order[1] |= m_u16Vb14VChannelOrder[1];
        u16Order[2] |= m_u16Vb14VChannelOrder[2];
        u16Order[3] |= m_u16Vb14VChannelOrder[3];
    }

    if (enOsd >= E_MAPI_TIMING_2K1K && enOsd < E_MAPI_TIMING_2K1K_MAX)
    {
        u16Order[0] |= m_u16Vb12OChannelOrder[0];
        u16Order[1] |= m_u16Vb12OChannelOrder[1];
        u16Order[2] |= m_u16Vb12OChannelOrder[2];
        u16Order[3] |= m_u16Vb12OChannelOrder[3];
    }
    else if (enOsd == E_MAPI_TIMING_4K2K || (enOsd >= E_MAPI_TIMING_4K2KP_30 && enOsd <= E_MAPI_TIMING_4K2KP_24))
    {
        u16Order[0] |= m_u16Vb14OChannelOrder[0];
        u16Order[1] |= m_u16Vb14OChannelOrder[1];
        u16Order[2] |= m_u16Vb14OChannelOrder[2];
        u16Order[3] |= m_u16Vb14OChannelOrder[3];
    }
    else if (enOsd == E_MAPI_TIMING_4096x2160P_24)
    {
        u16Order[0] |= m_u16Vb14OChannelOrder[0];
        u16Order[1] |= m_u16Vb14OChannelOrder[1];
        u16Order[2] |= m_u16Vb14OChannelOrder[2];
        u16Order[3] |= m_u16Vb14OChannelOrder[3];
    }

    if ((u16Order[0] == 0) && (u16Order[1] == 0) && (u16Order[2] == 0) && (u16Order[3] == 0))
    {
        return MAPI_FALSE;
    }

    return MAPI_TRUE;
}

MAPI_BOOL SystemInfo::GetHdrLevelAttribues(E_MAPI_XC_HDR_LEVEL enHdrLevel, MAPI_XC_HDR_LEVEL_ATTRIBUTES *pAttribues)
{
    if (enHdrLevel >= E_MAPI_XC_HDR_LOW && enHdrLevel < E_MAPI_XC_HDR_MAX)
    {
        memcpy(pAttribues, &m_stHdrLevelAttributes[enHdrLevel], sizeof(MAPI_XC_HDR_LEVEL_ATTRIBUTES));
        return MAPI_TRUE;
    }
    return MAPI_FALSE;
}

MAPI_BOOL SystemInfo::getColorBankFromIni()
{
    MAPI_BOOL ret = MAPI_FALSE;
    char *pRegini = NULL;
    dictionary *pSystemini = iniparser_load(SYS_INI_PATH_FILENAME);
    if (pSystemini == NULL)
    {
        SYSTEM_INFO_ERR("Can't loading %s\n",SYS_INI_PATH_FILENAME);
        return MAPI_FALSE;
    }
    pRegini = iniparser_getstr(pSystemini, "troubleshooting:gSettingFile");

    dictionary *pReginiGetData = iniparser_load(pRegini);
    if (pReginiGetData == NULL)
    {
        SYSTEM_INFO_ERR("Can't loading %s\n",pRegini);
        return MAPI_FALSE;
    }
    ret = iniparser_getboolean(pReginiGetData, "reg:colorbank",0);
    iniparser_freedict(pReginiGetData);
    return ret;
}
unsigned int * SystemInfo::getRegsFromIni(unsigned int &n)
{
    char *pRegini = NULL;
    dictionary *pSystemini = iniparser_load(SYS_INI_PATH_FILENAME);
    if (pSystemini == NULL)
    {
        SYSTEM_INFO_ERR("Can't loading %s\n",SYS_INI_PATH_FILENAME);
        return NULL;
    }
    pRegini = iniparser_getstr(pSystemini, "troubleshooting:gSettingFile");
    dictionary *pReginiGetData = iniparser_load(pRegini);
    unsigned int *retstr = NULL;
    if (pReginiGetData == NULL)
    {
        SYSTEM_INFO_ERR("Can't loading %s\n",pRegini);
        return NULL;
    }
    retstr = iniparser_getU32array(pReginiGetData, "reg:banks", n);
    iniparser_freedict(pReginiGetData);
    return retstr;
}
char* SystemInfo::getTargetPathFromIni(void)
{
    char *pRegini = NULL;
    char *retstr = NULL;
    char *str = NULL;
    dictionary *pSystemini = iniparser_load(SYS_INI_PATH_FILENAME);
    if (pSystemini == NULL)
    {
        SYSTEM_INFO_ERR("Can't loading %s\n",SYS_INI_PATH_FILENAME);
        return NULL;
    }
    pRegini = iniparser_getstr(pSystemini, "troubleshooting:gSettingFile");
    dictionary *pReginiGetData = iniparser_load(pRegini);

    if (pReginiGetData == NULL)
    {
        SYSTEM_INFO_ERR("Can't loading %s\n",pRegini);
        return NULL;
    }
    str = iniparser_getstr(pReginiGetData, "output:Target");
    if(str != NULL)
    {
        retstr = (char *)malloc(sizeof(char )* strlen(str)+1);
        memcpy(retstr,str,sizeof(char )* strlen(str)+1);
    }
    iniparser_freedict(pReginiGetData);
    return retstr;
}
int SystemInfo::getDumpLogFromIni(void)
{
    char *pRegini = NULL;
    int ret = 0;
    dictionary *pSystemini = iniparser_load(SYS_INI_PATH_FILENAME);
    if (pSystemini == NULL)
    {
        SYSTEM_INFO_ERR("Can't loading %s\n",SYS_INI_PATH_FILENAME);
        return MAPI_FALSE;
    }
    pRegini = iniparser_getstr(pSystemini, "troubleshooting:gSettingFile");
    dictionary *pReginiGetData = iniparser_load(pRegini);
    if (pReginiGetData == NULL)
    {
        SYSTEM_INFO_ERR("Can't loading %s\n",pRegini);
        return MAPI_FALSE;
    }
    ret = iniparser_getint(pReginiGetData, "log:dump",-1);
    iniparser_freedict(pReginiGetData);
    return ret;
}
unsigned int* iniparser_getU32array(dictionary * pdic, const char * pkey,unsigned int &count)
{
    char *pIniString = NULL;
    char *pch = NULL;
    const char *delim = ",";
    int i = 0;
    unsigned int *list = NULL;
    count = 0;
    pIniString = iniparser_getstr(pdic, pkey);
    if(pIniString == NULL)
    {
        return 0;
    }
    SYSTEM_INFO_DBG("IniString[%s]\n",pIniString);
    pch = pIniString;
    while((*pch != '\0') && (*pch != ';'))
    {
        if(*pch == ',')
            count++;
        pch++;
    }
    count+=1;
    list = (unsigned int *)malloc(sizeof(unsigned int )* count);
    memset(list,0,sizeof(unsigned int )* count);
    pch = strtok(pIniString, delim);
    while(pch != NULL)
    {
        list[i] = strtoul(pch,NULL,16);
        pch = strtok(NULL,delim);
        i++;
    }
    return list;
}

void SystemInfo::LoadHdrLevelAttributes(dictionary *pCustomerIni, MAPI_XC_HDR_LEVEL_ATTRIBUTES stHdrLevelAttributes[E_MAPI_XC_HDR_MAX])
{
    stHdrLevelAttributes[E_MAPI_XC_HDR_LOW].u16Smin = iniparser_getint(pCustomerIni, "Hdr:low_Smin", 17);
    stHdrLevelAttributes[E_MAPI_XC_HDR_LOW].u16Smed = iniparser_getint(pCustomerIni, "Hdr:low_Smed", 410);
    stHdrLevelAttributes[E_MAPI_XC_HDR_LOW].u16Smax = iniparser_getint(pCustomerIni, "Hdr:low_Smax", 920);
    stHdrLevelAttributes[E_MAPI_XC_HDR_LOW].u16Tmin = iniparser_getint(pCustomerIni, "Hdr:low_Tmin", 64);
    stHdrLevelAttributes[E_MAPI_XC_HDR_LOW].u16Tmed = iniparser_getint(pCustomerIni, "Hdr:low_Tmed", 420);
    stHdrLevelAttributes[E_MAPI_XC_HDR_LOW].u16Tmax = iniparser_getint(pCustomerIni, "Hdr:low_Tmax", 712);
    stHdrLevelAttributes[E_MAPI_XC_HDR_LOW].u16MidSourceOffset = iniparser_getint(pCustomerIni, "Hdr:low_MidSourceOffset", 410);
    stHdrLevelAttributes[E_MAPI_XC_HDR_LOW].u16MidTargetOffset = iniparser_getint(pCustomerIni, "Hdr:low_MidTargetOffset", 420);
    stHdrLevelAttributes[E_MAPI_XC_HDR_LOW].u16MidSourceRatio = iniparser_getint(pCustomerIni, "Hdr:low_MidSourceRatio", 300);
    stHdrLevelAttributes[E_MAPI_XC_HDR_LOW].u16MidTargetRatio = iniparser_getint(pCustomerIni, "Hdr:low_MidTargetRatio", 500);

    stHdrLevelAttributes[E_MAPI_XC_HDR_MIDDLE].u16Smin = iniparser_getint(pCustomerIni, "Hdr:middle_Smin", 17);
    stHdrLevelAttributes[E_MAPI_XC_HDR_MIDDLE].u16Smed = iniparser_getint(pCustomerIni, "Hdr:middle_Smed", 438);
    stHdrLevelAttributes[E_MAPI_XC_HDR_MIDDLE].u16Smax = iniparser_getint(pCustomerIni, "Hdr:middle_Smax", 920);
    stHdrLevelAttributes[E_MAPI_XC_HDR_MIDDLE].u16Tmin = iniparser_getint(pCustomerIni, "Hdr:middle_Tmin", 54);
    stHdrLevelAttributes[E_MAPI_XC_HDR_MIDDLE].u16Tmed = iniparser_getint(pCustomerIni, "Hdr:middle_Tmed", 485);
    stHdrLevelAttributes[E_MAPI_XC_HDR_MIDDLE].u16Tmax = iniparser_getint(pCustomerIni, "Hdr:middle_Tmax", 712);
    stHdrLevelAttributes[E_MAPI_XC_HDR_MIDDLE].u16MidSourceOffset = iniparser_getint(pCustomerIni, "Hdr:middle_MidSourceOffset", 430);
    stHdrLevelAttributes[E_MAPI_XC_HDR_MIDDLE].u16MidTargetOffset = iniparser_getint(pCustomerIni, "Hdr:middle_MidTargetOffset", 480);
    stHdrLevelAttributes[E_MAPI_XC_HDR_MIDDLE].u16MidSourceRatio = iniparser_getint(pCustomerIni, "Hdr:middle_MidSourceRatio", 400);
    stHdrLevelAttributes[E_MAPI_XC_HDR_MIDDLE].u16MidTargetRatio = iniparser_getint(pCustomerIni, "Hdr:middle_MidTargetRatio", 400);

    stHdrLevelAttributes[E_MAPI_XC_HDR_HIGH].u16Smin = iniparser_getint(pCustomerIni, "Hdr:high_Smin", 17);
    stHdrLevelAttributes[E_MAPI_XC_HDR_HIGH].u16Smed = iniparser_getint(pCustomerIni, "Hdr:high_Smed", 418);
    stHdrLevelAttributes[E_MAPI_XC_HDR_HIGH].u16Smax = iniparser_getint(pCustomerIni, "Hdr:high_Smax", 920);
    stHdrLevelAttributes[E_MAPI_XC_HDR_HIGH].u16Tmin = iniparser_getint(pCustomerIni, "Hdr:high_Tmin", 54);
    stHdrLevelAttributes[E_MAPI_XC_HDR_HIGH].u16Tmed = iniparser_getint(pCustomerIni, "Hdr:high_Tmed", 510);
    stHdrLevelAttributes[E_MAPI_XC_HDR_HIGH].u16Tmax = iniparser_getint(pCustomerIni, "Hdr:high_Tmax", 712);
    stHdrLevelAttributes[E_MAPI_XC_HDR_HIGH].u16MidSourceOffset = iniparser_getint(pCustomerIni, "Hdr:high_MidSourceOffset", 418);
    stHdrLevelAttributes[E_MAPI_XC_HDR_HIGH].u16MidTargetOffset = iniparser_getint(pCustomerIni, "Hdr:high_MidTargetOffset", 510);
    stHdrLevelAttributes[E_MAPI_XC_HDR_HIGH].u16MidSourceRatio = iniparser_getint(pCustomerIni, "Hdr:high_MidSourceRatio", 200);
    stHdrLevelAttributes[E_MAPI_XC_HDR_HIGH].u16MidTargetRatio = iniparser_getint(pCustomerIni, "Hdr:high_MidTargetRatio", 500);

}

MAPI_U8 SystemInfo::Get3DLRPWM(void)
{
    return D_BOARD_PWM_CH;
}

#if (MHL_ENABLE == 1)
MAPI_INPUT_SOURCE_TYPE SystemInfo::GetMHLSource(void)
{
#ifdef HDMI_PORT_FOR_MHL
    if ((HDMI_PORT_FOR_MHL >= MAPI_INPUT_SOURCE_HDMI) && (HDMI_PORT_FOR_MHL < MAPI_INPUT_SOURCE_HDMI_MAX))
    {
        return HDMI_PORT_FOR_MHL;
    }
    else
    {
        printf("Warning!!! %s, %s, %d: HDMI_PORT_FOR_MHL(%d) is not support!!!\n", __FILE__, __FUNCTION__, __LINE__, HDMI_PORT_FOR_MHL);
        return MAPI_INPUT_SOURCE_HDMI3;
    }
#else
    printf("Warning!!! %s, %s, %d: HDMI_PORT_FOR_MHL is not define!!!\n", __FILE__, __FUNCTION__, __LINE__);
    return MAPI_INPUT_SOURCE_HDMI3;
#endif
}
#endif
