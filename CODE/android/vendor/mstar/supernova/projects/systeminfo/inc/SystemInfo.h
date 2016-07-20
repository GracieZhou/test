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

#ifndef _SYSTEM_INFO_H_
#define _SYSTEM_INFO_H_

// headers of standard C++ libs
#include <vector>
#include <string>
#include <map>

#ifndef IniType
#include <VersionControl.h>
#endif

#include "mapi_types.h"
#include "mapi_syscfg_table.h"
#include "mapi_syscfg_fetch.h"


#define MAX_BUFFER 63

typedef struct _dictionary_ dictionary;

/// PQ Update file
typedef enum
{
    /// DLC file
    E_DLC_FILE,
    /// Color Matrix file
    E_COLOR_MATRiX_FILE,
    /// Bandwidth Table file
    E_BANDWIDTH_REG_TABLE_FILE,
    /// PQ Main file
    E_PQ_MAIN_FILE,
    /// PQ Main Text file
    E_PQ_MAIN_TEXT_FILE,
    /// PQ Main Ex file
    E_PQ_MAIN_EX_FILE,
    /// PQ Main Ex Text file
    E_PQ_MAIN_EX_TEXT_FILE,
    /// PQ Sub file
    E_PQ_SUB_FILE,
    /// PQ Sub Text file
    E_PQ_SUB_TEXT_FILE,
    /// PQ Sub Ex file
    E_PQ_SUB_EX_FILE,
    /// PQ Sub Ex Text file
    E_PQ_SUB_EX_TEXT_FILE,
    /// Gamma0 file
    E_GAMMA0_FILE,
} EN_PQ_UPDATE_FILE;


/// enum for input AudioDelay source type
typedef enum
{
    MAPI_AudioDelay_SOURCE_DTV,          ///<DTV input              0
    MAPI_AudioDelay_SOURCE_ATV,          ///<TV input               1
    MAPI_AudioDelay_SOURCE_CVBS,         ///<AV                     2
    MAPI_AudioDelay_SOURCE_SVIDEO,       ///<SVIDEO                 3
    MAPI_AudioDelay_SOURCE_SCART,        ///<SCART                  4
    MAPI_AudioDelay_SOURCE_YPBPR,        ///<YPBPR                  5
    MAPI_AudioDelay_SOURCE_VGA,          ///<VGA                    6
    MAPI_AudioDelay_SOURCE_HDMI,         ///<HDMI                   7
    MAPI_AudioDelay_SOURCE_STORAGE,      ///<STORAGE                8
    MAPI_AudioDelay_SOURCE_NUM           ///<number of the source   9
} MAPI_AudioDelay_Source_TYPE;


class SystemInfo : public mapi_syscfg_fetch
{
public:
    // ------------------------------------------------------------
    // public operations
    // ------------------------------------------------------------
    SystemInfo();
    //-------------------------------------------------------------------------------------------------
    /// Destructor of MSrv_Control.
    /// @param  None
    /// @return  None
    //-------------------------------------------------------------------------------------------------
    ~SystemInfo();


    static SystemInfo* GetInstance(void);
    MAPI_BOOL Init(void);
    MAPI_BOOL Finalize(void);
    MAPI_BOOL SetSystemInfo(void);

    //-------------------------------------------------------------------------------------------------
    /// Get_bSelectModelViaProjectID
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 Get_bSelectModelViaProjectID(void);

    //-------------------------------------------------------------------------------------------------
    /// Get_MaxProjectID
    /// @return                 \b OUT: MaxProjectID
    //-------------------------------------------------------------------------------------------------
    MAPI_U16 Get_MaxProjectID(void);

    //-------------------------------------------------------------------------------------------------
    /// Get_u32ProjectIdSpiAddr
    /// @return                 \b OUT: ProjectIdSpiAddr
    //-------------------------------------------------------------------------------------------------
    MAPI_U32 Get_u32ProjectIdSpiAddr(void);

    //-------------------------------------------------------------------------------------------------
    /// Get_u32ProjectIdBackupSpiAddr
    /// @return                 \b OUT: ProjectIdBackupSpiAddr
    //-------------------------------------------------------------------------------------------------
    MAPI_U32 Get_u32ProjectIdBackupSpiAddr(void);
    //-------------------------------------------------------------------------------------------------
    /// Get_u16ProjectId
    /// @return                 \b OUT: ProjectId
    //-------------------------------------------------------------------------------------------------
    MAPI_U16 GetProjectId(void);

    //-------------------------------------------------------------------------------------------------
    /// Set SpiProjectID
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 Set_SpiProjectID(MAPI_U16 m_u16SpiProjectID);


    //-------------------------------------------------------------------------------------------------
    /// Check Customer Section in the Customer.ini
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL CheckIniCustomerSectionExist(void);

    //-------------------------------------------------------------------------------------------------
    /// Provide function, based on key name get int value from ini file's customer section
    /// @param  pkey   ini parser key name "section:item", for example "customer:item123"
    /// @return                 \b OUT: the value from ini file based on key name
    //-------------------------------------------------------------------------------------------------
    MAPI_U16    IniGetInt(const char * pkey);

    //-------------------------------------------------------------------------------------------------
    /// Provide function, based on key name get MAPI_BOOLean value from ini file's customer section
    /// @param  pkey   ini parser key name "section:item", for example "customer:item123"
    /// @return                 \b OUT: the value from ini file based on key name
    //-------------------------------------------------------------------------------------------------
    MAPI_U16    IniGetBool(const char * pkey);

    //-------------------------------------------------------------------------------------------------
    /// Provide function, based on key name get string value from ini file's customer section
    /// @param  pkey :   ini parser key name "section:item", for example "customer:item123"
    /// @param  u16OutDataLen : the length of parameter pOutDataVal
    /// @param  pOutDataVal : Get the string and save to pOutDataVal
    /// @return                 \b OUT:  False or True
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL   IniGetStr(const char * pkey,  MAPI_U16 u16OutDataLen, char * pOutDataVal);

    //-------------------------------------------------------------------------------------------------
    /// Set model name into sys.ini file
    /// @param  pModelName :   the full path of model ini file, for example "/Customer/model/Customer_1.ini"
    /// @return                 \b OUT:  False or True
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL   IniUpdateModelName(char * pModelName);

    //-------------------------------------------------------------------------------------------------
    /// Set panel name into customer_x.ini file
    /// @param  pPanelName :   the full path of panel ini file, for example "/Customer/panel/FullHD_CMO216_H1L01.ini"
    /// @return                 \b OUT:  False or True
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL IniUpdatePanelName(char * pPanelName);

    //-------------------------------------------------------------------------------------------------
    /// Set keyvalue according to key code  into customer_x.ini file
    /// @param  pKeycode :   ini file key code , for example "panel:m_pPanelNamei"
    /// @param  pKeyvalue :   the full path of panel ini file, for example "/Customer/panel/FullHD_CMO216_H1L01.ini"
    /// @return                 \b OUT:  False or True
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL IniUpdateCustomerini(char * pKeycode,char * pKeyvalue);

    //-------------------------------------------------------------------------------------------------
    /// Set keyvalue according to key code  into panel ini file
    /// @param  pKeycode :   ini file key code , for example "panel:m_pPanelNamei"
    /// @param  pKeyvalue :   the full path of panel ini file, for example "/Customer/panel/FullHD_CMO216_H1L01.ini"
    /// @return                 \b OUT:  False or True
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL IniUpdatePanelini(char * pKeycode,char * pKeyvalue);

    //-------------------------------------------------------------------------------------------------
    /// To get Local_Storage_Path key of MWBLauncher.ini
    /// @return                 \b OUT: local storage path
    //-------------------------------------------------------------------------------------------------
    char * getLocalStoragePathOfMWBLauncherIni(void);

    //-------------------------------------------------------------------------------------------------
    /// To video.ts name to VideoFilePath:VideoFileName ini file
    /// @return                 \b OUT: VideoFileName
    //-------------------------------------------------------------------------------------------------
    char * GetVideoFileName(void);
	//EosTek Patch Begin
	char * GetVideoFileNameAlternative(void);
	//EosTek Patch End

    //-------------------------------------------------------------------------------------------------
    /// To board name for board def
    /// @return                 \b OUT: board name
    //-------------------------------------------------------------------------------------------------
    char * GetBoardName(void);

    //-------------------------------------------------------------------------------------------------
    /// To SoftWare Ver for board def
    /// @return                 \b OUT: SoftWare Ver name
    //-------------------------------------------------------------------------------------------------
    char * GetSoftWareVer(void);
    //-------------------------------------------------------------------------------------------------
    /// To panel name from ini
    /// @return                 \b OUT: panel name
    //-------------------------------------------------------------------------------------------------
    char * GetSystemPanelName(void);

    //-------------------------------------------------------------------------------------------------
    /// Get current number of gamma table
    /// @return                 \b OUT: current gamma table No
    //-------------------------------------------------------------------------------------------------
    MAPI_U16 GetCurrentGammaTableNo(void);

    //-------------------------------------------------------------------------------------------------
    /// Get total number of gamma table
    /// @return                 \b OUT: total gamma table No
    //-------------------------------------------------------------------------------------------------
    MAPI_U16 GetTotalGammaTableNo(void);

#if (STEREO_3D_ENABLE == 1)
    //------------------------------------------------------------------------------
    /// Get global information
    /// @return                 \b OUT: panel infomation
    //------------------------------------------------------------------------------
    MAPI_PanelType* GetGlobalPanelInfo();
#endif

    //-------------------------------------------------------------------------------------------------
    /// Update PQ parameter via USB key
    /// @return                 \b OUT: total gamma table No
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL UpdatePQParameterViaUsbKey(void);

    //-------------------------------------------------------------------------------------------------
    /// Get Update PQ File Path
    /// @param  pFilePath      \b OUT: file path buffer array[63]
    /// @param  enPQFile      \b IN: PQ file
    /// @return                 \b TRUE: success, or FALSE: failure.
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL GetUpdatePQFilePath(char pFilePath[MAX_BUFFER], const EN_PQ_UPDATE_FILE enPQFile);

    //-------------------------------------------------------------------------------------------------
    /// Update PQ ini Files
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void UpdatePQiniFiles();

    //-------------------------------------------------------------------------------------------------
    /// Get Pip main/sub pair Info
    /// @param  enMainInputSrc     main inputsource of PIP
    /// @param  enSubInputSrc      sub inputsource of PIP
    /// @return                 \b OUT: TRUE:Support or FALSE:Not Support
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL GetPipPairInfo(MAPI_INPUT_SOURCE_TYPE enMainInputSrc, MAPI_INPUT_SOURCE_TYPE enSubInputSrc);

    //-------------------------------------------------------------------------------------------------
    /// Get Pop main/sub pair Info
    /// @param  enMainInputSrc     main inputsource of PIP
    /// @param  enSubInputSrc      sub inputsource of PIP
    /// @return                 \b OUT: TRUE:Support or FALSE:Not Support
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL GetPopPairInfo(MAPI_INPUT_SOURCE_TYPE enMainInputSrc, MAPI_INPUT_SOURCE_TYPE enSubInputSrc);

#if (TRAVELING_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Get Traveling main/sub pair Info
    /// @param  enMainInputSrc     main inputsource of PIP
    /// @param  enSubInputSrc      sub inputsource of PIP
    /// @return                 \b OUT: TRUE:Support or FALSE:Not Support
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL GetTravelingPairInfo(MAPI_INPUT_SOURCE_TYPE enMainInputSrc, MAPI_INPUT_SOURCE_TYPE enSubInputSrc);
#endif
    //-------------------------------------------------------------------------------------------------
    /// To set AVSync Delay value from input parameter
    /// @param  delay           AVSync Delay
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetAVSyncDelay(MAPI_U32 delay);

#if (SQL_DB_ENABLE)

    /// Define customer db path and table name
    typedef enum
    {
        /// Default
        E_CUSTOMER_DBPATH_TABLENAME_DEFAULT = 0,
        /// Customer DB 3D VIDEO ROUTER PATH
        E_CUSTOMER_3D_VIDEO_ROUTER_PATH,
        /// Customer DB 3D VIDEO ROUTER TABLENAME
        E_CUSTOMER_3D_VIDEO_ROUTER_TABLENAME,
        /// Customer DB 3D To 2D VIDEO ROUTER PATH
        E_CUSTOMER_3DTo2D_VIDEO_ROUTER_PATH,
        /// Customer DB 3D To 2D VIDEO ROUTER TABLENAME
        E_CUSTOMER_3DTo2D_VIDEO_ROUTER_TABLENAME,
        /// display mode ROUTER PATH
        E_CUSTOMER_DISPLAY_MODE_ROUTER_PATH,
        /// display mode ROUTE TABLENAME
        E_CUSTOMER_DISPLAY_MODE_ROUTER_TABLENAME,
        /// Customer DB FACTORY ADC ADJUST PATH
        E_CUSTOMER_FACTORY_ADC_ADJUST_PATH,
        /// Customer DB FACTORY ADC ADJUST TABLENAME
        E_CUSTOMER_FACTORY_ADC_ADJUST_TABLENAME,
        /// Customer DB FACTORY COLOR TEMP PATH
        E_CUSTOMER_FACTORY_COLOR_TEMP_PATH,
        /// Customer DB FACTORY COLOR TEMP TABLENAME
        E_CUSTOMER_FACTORY_COLOR_TEMP_TABLENAME,
        /// Customer DB FACTORY COLOR TEMP EX PATH
        E_CUSTOMER_FACTORY_COLOR_TEMP_EX_PATH,
        /// Customer DB FACTORY COLOR TEMP EX TABLENAME
        E_CUSTOMER_FACTORY_COLOR_TEMP_EX_TABLENAME,
        /// Customer DB NON LINEAR ADJUST EX PATH
        E_CUSTOMER_NON_LINEAR_ADJUST_EX_PATH,
        /// Customer DB NON LINEAR ADJUST EX TABLENAME
        E_CUSTOMER_NON_LINEAR_ADJUST_EX_TABLENAME,
        /// Customer DB NON LINEAR ADJUST EX 3D PATH
        E_CUSTOMER_NON_LINEAR_ADJUST_3D_EX_PATH,
        /// Customer DB NON LINEAR ADJUST EX 3D TABLENAME
        E_CUSTOMER_NON_LINEAR_ADJUST_3D_EX_TABLENAME,
        /// Customer DB FACTORY COLOR TEMP EX 3D PATH
        E_CUSTOMER_FACTORY_COLOR_TEMP_EX_3D_PATH,
        /// Customer DB FACTORY COLOR TEMP EX 3D TABLENAME
        E_CUSTOMER_FACTORY_COLOR_TEMP_EX_3D_TABLENAME,
        /// Customer DB 4K2K 3D VIDEO ROUTER PATH
        E_CUSTOMER_4K2K_3D_VIDEO_ROUTER_PATH,
        /// Customer DB 4K2K 3D VIDEO ROUTER TABLENAME
        E_CUSTOMER_4K2K_3D_VIDEO_ROUTER_TABLENAME,
        /// Customer DB 4K2K 60HZ 3D VIDEO ROUTER PATH
        E_CUSTOMER_4K2K_60HZ_3D_VIDEO_ROUTER_PATH,
        /// Customer DB 4K2K 60HZ 3D VIDEO ROUTER TABLENAME
        E_CUSTOMER_4K2K_60HZ_3D_VIDEO_ROUTER_TABLENAME,
        /// Maximum value of this enum
        E_CUSTOMER_DBPATH_TABLENAME_MAX,
    } EN_SYSINFO_CUSTOMER_DBPATH_TABLENAME_TYPE;
    //-------------------------------------------------------------------------------------------------
    /// Get Customer DB Path
    /// @param enCustomerDBPath        DB PATH TYPE
    /// @return Customer DB Path
    //-------------------------------------------------------------------------------------------------
    char* GetCustomerDBPath(EN_SYSINFO_CUSTOMER_DBPATH_TABLENAME_TYPE enCustomerDBPath);

    //-------------------------------------------------------------------------------------------------
    /// Get Customer TableName
    /// @param enCustomerDBTablename    DB TABLE TYPE
    /// @return Customer TableName
    //-------------------------------------------------------------------------------------------------
    char* GetCustomerDBTableName(EN_SYSINFO_CUSTOMER_DBPATH_TABLENAME_TYPE enCustomerDBTablename);

    //-------------------------------------------------------------------------------------------------
    /// Set SQL DB data
    /// @return                 \b OUT: TRUE or FALSE
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetSQLDBdata(void);
#endif

    //-------------------------------------------------------------------------------------------------
    /// Get module feature enable/disable information from module ini file
    /// @param  feature     module feature in module ini file, for example "M_PVR:F_PVR_XXXX"
    /// @param  enable      \b OUT: feature enable or not
    /// @return             \b OUT: find status
    //-------------------------------------------------------------------------------------------------
    EN_MODULE_PARAMETER_STATUS GetModuleParameter_bool(const char * feature, MAPI_BOOL *enable);

    //-------------------------------------------------------------------------------------------------
    /// Get module feature information of type integer from module ini file
    /// @param  feature     module feature in module ini file, for example "M_PVR:F_PVR_XXXX"
    /// @param  value      \b OUT: feature value
    /// @param  notfound   \b OUT: value of value if feature not found
    /// @return                 \b OUT: find status
    //-------------------------------------------------------------------------------------------------
    EN_MODULE_PARAMETER_STATUS GetModuleParameter_int(const char * feature, int *value, int notfound);

    //-------------------------------------------------------------------------------------------------
    /// Get module feature information of type string from module ini file
    /// @param  feature     module feature in module ini file, for example "M_PVR:F_PVR_XXXX"
    /// @param  value      \b OUT: feature value
    /// @return                 \b OUT: find status
    //-------------------------------------------------------------------------------------------------
    EN_MODULE_PARAMETER_STATUS GetModuleParameter_string(const char * feature, char *strValue, const MAPI_U16 strLenth);

    //-------------------------------------------------------------------------------------------------
    /// Get module feature information of type array from module ini file
    /// @param  feature     module feature in module ini file, for example "M_PVR:F_PVR_XXXX"
    /// @param  value      \b OUT: feature value
    /// @return                 \b OUT: find status
    //-------------------------------------------------------------------------------------------------
    EN_MODULE_PARAMETER_STATUS GetModuleParameter_U8array(const char * feature, MAPI_U8 *dataArray, const MAPI_U16 arraySize);

    //-------------------------------------------------------------------------------------------------
    /// set additional panel parameters for 4k1k/4k2k mode. no more used.
    /// @param  ePanelType      \b IN: Panel resolution
    /// @return                 \b OUT: TRUE or FALSE
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetAdditionalModeInfo(EN_SupportPanelType ePanelType);

    //-------------------------------------------------------------------------------------------------
    /// check whether set panel as 4k2k mode by cmd or not
    /// @return                 \b OUT: TRUE or FALSE
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL IsPanel4K2KModeNeedCmd(void);

    //-------------------------------------------------------------------------------------------------
    /// update OSDC information from INI
    /// @return                 \b OUT: TRUE or FALSE
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL LoadOSDCInfo(void);

    //-------------------------------------------------------------------------------------------------
    /// get OSDC information and save to info
    /// @param info             \b IN: OSDC information
    /// @return                 \b OUT: TRUE or FALSE
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL GetOSDCInfo(MAPI_OSDCType *info);

    //-------------------------------------------------------------------------------------------------
    /// setup Lpll type
    /// @param type             \b IN: Lpll type
    /// @return                 \b OUT: TRUE or FALSE
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL setOSDLpllType(MAPI_U32 type);
    //-------------------------------------------------------------------------------------------------
    /// get the OSD lpll type
    /// @return                 \b OUT: lpll type
    //-------------------------------------------------------------------------------------------------
    MAPI_U32 getOSDLpllType(void);
    //-------------------------------------------------------------------------------------------------
    /// get the OSD lpll type
    /// @param width                \b IN: OSD width
    /// @param height               \b IN: OSD height
    /// @param timing               \b IN: OSD timing
    /// @return                     \b OUT: TRUE or FALSE
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL updateOSDLpllType(int width, int height, int timing);
#if (MSTAR_TVOS == 1)
    //-------------------------------------------------------------------------------------------------
    /// set hdmi port hdcp key enable or disable
    /// @return                 \b OUT: TRUE or FALSE
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetHDMI_HdcpEnable(void);
#endif

    //-------------------------------------------------------------------------------------------------
    /// Get tuner mode table from module ini file
    /// @return                 \b OUT: tuner mode table
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetTunersCount(void) const;
    std::vector< std::vector<std::string> > GetTunerModeTable(void) const;

    MAPI_BOOL IsDTVRouteConflict(std::vector<EN_DTV_TYPE> dtvRouteSet) const;

    EN_DTV_TYPE GetDTVRouteEnumByString(std::string dtvRouteString) const;
    EN_DTV_TYPE GetDTVRouteEnumByRouteIndex(MAPI_U8 u8RouteIndex) const;

#if (HDMITX_ENABLE == 1) || (STB_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Get panel resolution num for HDMI TX
    /// @return                 \b OUT:  Panel resolution num
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetPanelResolutionNum(void);
#endif

    //-------------------------------------------------------------------------------------------------
    /// To get SAW type from board define
    /// @return                 \b OUT: SAW type
    //-------------------------------------------------------------------------------------------------
    SawArchitecture GetSAWType(void);

    //-------------------------------------------------------------------------------------------------
    /// To get SAR channel from board define
    /// @return                 \b OUT: SAW type
    //-------------------------------------------------------------------------------------------------
    SarChannel GetSARChannel(void);

    //-------------------------------------------------------------------------------------------------
    /// To get PQ binary file's path  from customer.ini
    /// @return                 \b OUT: PQ binary file's path
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 * GetPQPathName(void);

    //-------------------------------------------------------------------------------------------------
    /// Get Input source mux table
    /// @return pointer to input source mux table
    //-------------------------------------------------------------------------------------------------
    const MAPI_VIDEO_INPUTSRCTABLE* GetInputMuxInfo();

    //-------------------------------------------------------------------------------------------------
    /// Get TV system configuration
    /// @return pointer to TV system configuration
    //-------------------------------------------------------------------------------------------------
    const S_TV_TYPE_INFO& GetTVInfo(void);

    //-------------------------------------------------------------------------------------------------
    /// Get Dtv type of route path
    /// @param u8RouteIndex \b IN: index of route (0~(MAXROUTECOUNT-1))
    /// @return MAPI_U8 bitmapping of DTV type
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetRouteTVMode(MAPI_U8 u8RouteIndex);

    //-------------------------------------------------------------------------------------------------
    /// Get CI slot count
    /// @return MAPI_U8 CI slot count
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetCISlotCount(void);

    //-------------------------------------------------------------------------------------------------
    /// Get IP enable bits
    /// @return MAPI_U16 bitmap of IP enable or not
    //-------------------------------------------------------------------------------------------------
    MAPI_U16 get_IPEnableType();

    //-------------------------------------------------------------------------------------------------
    /// Get ATV system type
    /// @return ATV system type
    //-------------------------------------------------------------------------------------------------
    EN_ATV_SYSTEM_TYPE get_ATVSystemType();

    //-------------------------------------------------------------------------------------------------
    /// Get DTV system type
    /// @return DTV system type
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 get_DTV_SystemType();

    //-------------------------------------------------------------------------------------------------
    /// Get STB system type
    /// @return STB system type
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 get_STB_SystemType();

    //-------------------------------------------------------------------------------------------------
    /// Get audio system type
    /// @return audio system type
    //-------------------------------------------------------------------------------------------------
    EN_AUDIO_SYSTEM_TYPE get_AUDIOSystemType();

    //-------------------------------------------------------------------------------------------------
    /// Is support the DTV system type
    /// @param u8Type \b IN: DTV system type
    /// @return TRUE: support, FALSE: not support
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 IsSupportTheDTVSystemType(MAPI_U8 u8Type);

#if (STB_ENABLE == 1)

    //-------------------------------------------------------------------------------------------------
    /// Get HDMITx analog info
    /// @return HDMITx analog info
    //-------------------------------------------------------------------------------------------------
    const HDMITx_Analog_Info_t* GetHDMITxAnalogInfo(MAPI_U8 u8Index);
#endif

    //-------------------------------------------------------------------------------------------------
    /// Get scart info
    /// @return pointer to scart info
    //-------------------------------------------------------------------------------------------------
    const ScartInfo_t& GetScartInfo();

    //-------------------------------------------------------------------------------------------------
    /// Get watchdog configuration
    /// @return pointer to watchdog configuration
    //-------------------------------------------------------------------------------------------------
    const WDTInfo_t& GetWDTCfg();

    //-------------------------------------------------------------------------------------------------
    /// Get watchdog configuration enable flag
    /// @return TRUE: watchdog is enable, FALSE: watchdog is disable
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetWDTCfgEnableFlag();

    //-------------------------------------------------------------------------------------------------
    /// Get HbbtvDelayInit Flag
    /// @return TRUE: HbbTV init after Video is shown, FALSE: HbbTV init before Video is shown
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetHbbtvDelayInitFlag();

    //-------------------------------------------------------------------------------------------------
    /// Get GPIO configuration
    /// @return pointer to GPIO configuration
    //-------------------------------------------------------------------------------------------------
    const GPIOConfig_t& GetGPIOConfig();

    //-------------------------------------------------------------------------------------------------
    /// Get GPIO info
    /// @return pointer to GPIO info
    //-------------------------------------------------------------------------------------------------
    const GPIOInfo_t* GetGPIOInfo();

    //-------------------------------------------------------------------------------------------------
    /// Modify customized I2C configuration
    /// @param u32gID \b IN: I2C device ID
    /// @param u8i2c_bus \b IN: hardware I2C bus
    /// @param u8slave_id \b IN: software I2C slave_id
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void ModifyI2CDevCfg(MAPI_U32 u32gID, MAPI_U8 u8i2c_bus, MAPI_U8 u8slave_id);

    //-------------------------------------------------------------------------------------------------
    /// Get I2C configuration
    /// @return pointer to I2C configuration
    //-------------------------------------------------------------------------------------------------
    const I2CConfig_t& GetI2CConfig();

    //-------------------------------------------------------------------------------------------------
    /// Get I2C bus configuration
    /// @return pointer to I2C bus configuration
    //-------------------------------------------------------------------------------------------------
    const I2CBus_t* GetI2CBus();

    //-------------------------------------------------------------------------------------------------
    /// Get I2C device info
    /// @return pointer to I2C device info
    //-------------------------------------------------------------------------------------------------
    I2CDeviceInfo_t* GetI2CDeviceInfo();

    //-------------------------------------------------------------------------------------------------
    /// Get MSPI Pad information
    /// @return pointer to MSPI Pad info
    //-------------------------------------------------------------------------------------------------
    const MSPI_pad_info_t* GetMSPIPadInfo(void);

    //-------------------------------------------------------------------------------------------------
    /// Get MSPI configuration
    /// @return pointer to MSPI configuration
    //-------------------------------------------------------------------------------------------------
    const MSPI_config_t& GetMSPIConfig();

    //-------------------------------------------------------------------------------------------------
    /// Get MSPI device info
    /// @return pointer to MSPI device info
    //-------------------------------------------------------------------------------------------------
    MSPIConfig_t* GetMSPIDeviceInfo();

    //-------------------------------------------------------------------------------------------------
    /// To get VD capture window mode
    /// @return                 \b OUT: VD capture window mode
    //-------------------------------------------------------------------------------------------------
    EN_VD_CAPTURE_WINDOW_MODE GetVDCaptureWinMode();

    //-------------------------------------------------------------------------------------------------
    /// Get serial DMX table
    /// @param index  \b index of route path
    /// @return pointer to serial DMX table
    //-------------------------------------------------------------------------------------------------
    const DemuxInfo_t* GetSerialDMXInfo(MAPI_U8 index);

    //-------------------------------------------------------------------------------------------------
    /// Get parallel DMX table
    /// @param index  \b index of route path
    /// @return pointer to parallel DMX table
    //-------------------------------------------------------------------------------------------------
    const DemuxInfo_t* GetParallelDMXInfo(MAPI_U8 index);

    //-------------------------------------------------------------------------------------------------
    /// Get audio default init value
    /// @return pointer to audio default init value
    //-------------------------------------------------------------------------------------------------
    const AudioDefualtInit_t& GetAudioDefaultInit();

    //-------------------------------------------------------------------------------------------------
    /// Get audio input source mux table
    /// @return pointer to audio input source mux table
    //-------------------------------------------------------------------------------------------------
    const AudioMux_t* GetAudioInputMuxInfo();

    //-------------------------------------------------------------------------------------------------
    /// Get audio path mux table
    /// @return pointer to udio path mux table
    //-------------------------------------------------------------------------------------------------
    const AudioPath_t* GetAudioPathInfo();

    //-------------------------------------------------------------------------------------------------
    /// Get audio output path mux table
    /// @return pointer to audio output path mux table
    //-------------------------------------------------------------------------------------------------
    const AudioOutputType_t* GetAudioOutputTypeInfo();

    //-------------------------------------------------------------------------------------------------
    /// Get audio volume curve table
    /// @return pointer to audio volume curve table
    //-------------------------------------------------------------------------------------------------
    const VolumeCurve_t* GetVolumeCurve();

    //-------------------------------------------------------------------------------------------------
    /// Get Picture Mode Use Factory Curve Flag
    /// @return Picture Mode Use Factory Curve Flag
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetPictureModeUseFacCurveFlag();

    //-------------------------------------------------------------------------------------------------
    /// Get picture mode curve table
    /// @return pointer to picture mode curve table
    //-------------------------------------------------------------------------------------------------
    const PictureModeCurve_t* GetPictureModeCurve();

    //-------------------------------------------------------------------------------------------------
    /// Get default gamma table index
    /// @return default gamma table index
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetDefaultGammaIdx();

    //-------------------------------------------------------------------------------------------------
    /// Get gamma table
    /// @param u8Idx                   IN: Gamma table index
    /// @return pointer to gamma table
    //-------------------------------------------------------------------------------------------------
    GAMMA_TABLE_t* GetGammaTableInfo(MAPI_U8 u8Idx);

    //-------------------------------------------------------------------------------------------------
    /// Get Volume Compensation
    /// @return Volume Compensation
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetVolumeCompensationFlag();

    //-------------------------------------------------------------------------------------------------
    /// Get PC source signal detected count
    /// @return detected count
    //-------------------------------------------------------------------------------------------------
    const stSignalDetectCount& GetPcDetectCount(void);

    //-------------------------------------------------------------------------------------------------
    /// Get HDMI source signal detected count
    /// @return detected count
    //-------------------------------------------------------------------------------------------------
    const stSignalDetectCount& GetHdmiDetectCount(void);

    //-------------------------------------------------------------------------------------------------
    /// Get COMP source signal detected count
    /// @return detected count
    //-------------------------------------------------------------------------------------------------
    const stSignalDetectCount& GetCompDetectCount(void);

    //-------------------------------------------------------------------------------------------------
    ///  Get Customer PQC fg
    /// @param enWinType : windows type
    /// @return current PQ path name
    //-------------------------------------------------------------------------------------------------
    std::string  GetCustomerPQCfg(MAPI_PQ_WIN enWinType);

#if ( INTEL_WIDI_ENABLE == 1 )
    //-------------------------------------------------------------------------------------------------
    /// Get widi info
    /// @return pointer to widi info
    //-------------------------------------------------------------------------------------------------
    const WidiInfo_t* GetWidiInfo();
#endif

#if (ENABLE_LITE_SN == 0)
    //-------------------------------------------------------------------------------------------------
    /// Get 4k-2k mode info
    /// @return pointer to 4k-2k mode info
    //-------------------------------------------------------------------------------------------------
    const PanelInfo_t* Get4K2KModeInfo();
#endif

    //-------------------------------------------------------------------------------------------------
    /// Get LVDS output type
    /// @return LVDS output type
    //-------------------------------------------------------------------------------------------------
    MAPI_U16 GetLVDSOutputType();

    //-------------------------------------------------------------------------------------------------
    /// Get PQ Auto NR Param
    /// @return pointer to PQ Auto NR Param
    //-------------------------------------------------------------------------------------------------
    MAPI_AUTO_NR_INIT_PARAM* GetPQAutoNRParam();

#if(MULTI_DEMOD==1)
    stMultiExtendDemodCfg& GetMultiDemodCfg(void);
#endif
    //-------------------------------------------------------------------------------------------------
    /// Get demod config from board define
    /// @param  u8DemodMode               \b IN: 0 for DVBT, 1 for DVBC, 2 for ATSC
    /// @param  u8BOARD_DSPRegInitExt               \b IN: pointer of dsp register initial setting
    /// @param  u8BOARD_DMD_InitExt               \b IN: pointer of standard initial setting
    /// @return TRUE: get success, FALSE: get fail
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetDemodConfig(MAPI_U8 u8DemodMode, MAPI_U8** u8BOARD_DSPRegInitExt, MAPI_U8** u8BOARD_DMD_InitExt );

    //-------------------------------------------------------------------------------------------------
    /// Get pc mode timing table
    /// @return pointer to pc mode timing table
    //-------------------------------------------------------------------------------------------------
    const PcModeTimingTable_t* GetPcModeTimingTable();

    //-------------------------------------------------------------------------------------------------
    /// Get pc mode timing table count
    /// @return pc mode timing table count
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetPcModeTimingTableCount();

    //-------------------------------------------------------------------------------------------------
    /// Get HDMI EDID table
    /// @return pointer to HDMI EDID table
    //-------------------------------------------------------------------------------------------------
    HDMI_EDID_InfoSet_t* GetHDMIEDIDInfoSet();

    //-------------------------------------------------------------------------------------------------
    /// Get VGA EDID table
    /// @return pointer to VGA EDID table
    //-------------------------------------------------------------------------------------------------
    VGA_EDID_Info_t* GetVGAEDIDInfo();

    //-------------------------------------------------------------------------------------------------
    /// Set Video Mirror mode flag
    /// @param bMirrorEnable     \b: IN: enable mirror mode or not
    /// @param u8MirrorType     \b: IN: Mirror type 0:MIRROR_NORMAL, 1:MIRROR_H_ONLY, 2:MIRROR_V_ONLY, 3:MIRROR_HV,
    /// @return none
    //-------------------------------------------------------------------------------------------------
    void SetVideoMirrorCfg(MAPI_U8 bMirrorEnable ,MAPI_U8 u8MirrorType = 3 );

    //-------------------------------------------------------------------------------------------------
    /// Get Video Mirror Flag
    /// @return the Video Mirror Flag (0:MIRROR_DISABLE, 1:MIRROR_ENABLE)
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetMirrorVideoFlag();

    //-------------------------------------------------------------------------------------------------
    /// Get Video Mirror Mode
    /// @return the Video Mirror Mode (0:MIRROR_NORMAL, 1:MIRROR_H_ONLY, 2:MIRROR_V_ONLY, 3:MIRROR_HV)
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 getMirrorVideoMode();

    //-------------------------------------------------------------------------------------------------
    /// Get 3D over scan flag
    /// @return the 3D over scan flag
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 Get3DOverScanFlag();

#if (HDMITX_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Get HDMI TX audoi type
    /// @return the HDMI TX audoi type
    //-------------------------------------------------------------------------------------------------
    EN_LTH_HDMITX_AUDIO_TYPE GetHdmitxAudioType();
#endif

    //-------------------------------------------------------------------------------------------------
    /// Get 3D Panel LR Inverse Flag
    /// @return the 3D Panel LR Inverse Flag
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 Get3DPanelLRInverseFlag();

    //-------------------------------------------------------------------------------------------------
    /// Set Freerun Config
    /// @param b3D         \b: IN: 3D or 2D
    /// @param bEnable     \b: IN: Enable Freerun or not
    /// @return none
    //-------------------------------------------------------------------------------------------------
    void SetFreerunCfg(MAPI_U8 b3D, MAPI_U8 bEnable);

    //-------------------------------------------------------------------------------------------------
    /// Get Freerun Flag
    /// @param b3D         \b: IN: 3D or 2D
    /// @return the Freerun Flag
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetFreerunFlag(MAPI_U8 b3D);

    //-------------------------------------------------------------------------------------------------
    /// Get SG Panel Flag
    /// @return SG Panel Flag
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetSGPanelFlag();

    //-------------------------------------------------------------------------------------------------
    /// Get scaler direct output 120hz sg panel config flag
    /// @return this Flag
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetXCOutput120hzSGPanelFlag();

    //-------------------------------------------------------------------------------------------------
    /// Get FBL mode's threshold
    /// @return threshold
    //-------------------------------------------------------------------------------------------------
    MAPI_U32 GetFBLModeThreshold();

    //-------------------------------------------------------------------------------------------------
    /// get resolution info table size
    /// @return pointer to resolution info
    //-------------------------------------------------------------------------------------------------
    const ResolutionInfoSize* GetResolutionInfo();

    //-------------------------------------------------------------------------------------------------
    /// Get HotPlugInverse flags
    /// @return m_u32HotPlugInverse flags
    //-------------------------------------------------------------------------------------------------
    MAPI_U32 GetHotPlugInverse();


    //-------------------------------------------------------------------------------------------------
    /// Get HDMI 5V Detect GPIO Select flags
    /// @return m_u32Hdmi5vDetectGpioSelect flags
    //-------------------------------------------------------------------------------------------------
    MAPI_U32 GetHdmi5vGpioSelect();

    //-------------------------------------------------------------------------------------------------
    /// If m_bDotByDotAble enable
    /// @return MAPI_BOOL to tell if m_bDotByDotAble is true.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetDotByDotable();

    //-------------------------------------------------------------------------------------------------
    /// Get Video windows table infor
    /// @param E_VideoInfo \b video type of  window
    /// @return ST_MAPI_VIDEO_WINDOW_INFO  table of video window infor
    //-------------------------------------------------------------------------------------------------
    const ST_MAPI_VIDEO_WINDOW_INFO** GetVideoWinInfo(VideoInfo_t E_VideoInfo);

    //-------------------------------------------------------------------------------------------------
    /// Parse Regs list form INI
    /// @param n                    \b list length
    /// @return Regs list           \b OUT
    //-------------------------------------------------------------------------------------------------
    unsigned int * getRegsFromIni(unsigned int &n);

    //-------------------------------------------------------------------------------------------------
    /// get ColorBank From INI
    /// @return MAPI_BOOL           \b OUT
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL getColorBankFromIni(void);

    //-------------------------------------------------------------------------------------------------
    /// get the output path from INI
    /// @return char*           \b OUT  target file path
    //-------------------------------------------------------------------------------------------------
    char* getTargetPathFromIni(void);

    //-------------------------------------------------------------------------------------------------
    /// get the dump log from INI
    /// @return int           \b OUT
    //-------------------------------------------------------------------------------------------------
    int getDumpLogFromIni(void);

#if (ENABLE_BACKEND == 1)
    //-------------------------------------------------------------------------------------------------
    /// Get Ursa init data
    /// @return pointer to ursa info
    //-------------------------------------------------------------------------------------------------
    MAPI_UrsaType* GetUrsaInfo(void);

    //-------------------------------------------------------------------------------------------------
    /// Get Ursa enable
    /// @return the Ursa enable
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetUrsaEnable();

    //-------------------------------------------------------------------------------------------------
    /// Get Ursa select number
    /// @return the Ursa number
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetUrsaSelect();

    //-------------------------------------------------------------------------------------------------
    /// Get MEMC Panel enable
    /// @return the MEMC Panel enable
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetMEMCPanelEnable();

    //-------------------------------------------------------------------------------------------------
    /// Get MEMC Panel select number
    /// @return the MEMC Panel number
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetMEMCPanelSelect();
#endif

    //-------------------------------------------------------------------------------------------------
    /// Get Tuner select number
    /// @param             \b IN: MAPI_U8 tuner No.
    /// @return the Tuner number
    //-------------------------------------------------------------------------------------------------
    std::string GetTunerSelect(MAPI_U8 u8TunerNo);

    //-------------------------------------------------------------------------------------------------
    /// Get China Descrambler Box Delay Offset
    /// @return the Tuner number
    //-------------------------------------------------------------------------------------------------
    MAPI_S16 GetChinaDescramblerBoxDelayOffset(void);

    //-------------------------------------------------------------------------------------------------
    /// Get panel backlight PWM table
    /// @return pointer to panel backlight PWM table
    //-------------------------------------------------------------------------------------------------
    const TunerPWMInfo* GetTunerPWMInfo();

    //-------------------------------------------------------------------------------------------------
    /// Get Audio AMP select number
    /// @return the Audio AMP number
    //-------------------------------------------------------------------------------------------------
     MAPI_U8 GetAudioAmpSelect();

    //-------------------------------------------------------------------------------------------------
    /// Get eeprom_type
    /// @return           \b OUT: eeprom_type
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 Get_Eeprom_Type(void);

    //-------------------------------------------------------------------------------------------------
    /// Get HDCP Key filename path
    /// @return string to locate it path.
    //-------------------------------------------------------------------------------------------------
    std::string GetHDCPKeyFileName();

#if (ENABLE_LITE_SN == 0)
    //-------------------------------------------------------------------------------------------------
    /// Get Swing Level
    /// @return the swing level's value
    //-------------------------------------------------------------------------------------------------
    MAPI_U16  GetSwingLevel();

    //-------------------------------------------------------------------------------------------------
    /// Get Use Customer AVSync Delay
    /// @return AVSync Delay
    //-------------------------------------------------------------------------------------------------
    MAPI_U16 GetUseCustomerAVSyncDelay();

    //-------------------------------------------------------------------------------------------------
    /// Get Nand Hdcp Flag
    /// @return MAPI_BOOL to tell if bIsNandHdcpEnable is true.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetUseNandHdcpFlag();

    //-------------------------------------------------------------------------------------------------
    /// Get Nand Hdcp Flag
    /// @return MAPI_BOOL to tell if bIsNandHdcpEnable is true.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetUseSPIHdcpFlag();

    //-------------------------------------------------------------------------------------------------
    /// Get Hdcp SPI Bank
    /// @return Hdcp SPI Bank.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetHdcpSPIBank();

    //-------------------------------------------------------------------------------------------------
    /// Get EEPROM Flag
    /// @return MAPI_BOOL to tell if bIsEEPROMHdcpenable is true.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetUseEEPROMFlag();

    //-------------------------------------------------------------------------------------------------
    /// Get EEPROM Flag
    /// @return MAPI_BOOL to tell if bIsEEPROMHdcpenable is true.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetHdcpEEPROMAddr();

    //-------------------------------------------------------------------------------------------------
    /// Get MAC is get from SPI Flag
    /// @return MAPI_BOOL to tell if bIsNandMACEnable is true.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetUseSPIMacFlag();

    //-------------------------------------------------------------------------------------------------
    /// Get MAC SPI Bank
    /// @return MAC SPI Bank.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetMacSPIBank();

    //-------------------------------------------------------------------------------------------------
    /// Get Local DIMMING Flag
    /// @return MAPI_BOOL to tell if bIsEEPROMHdcpenable is true.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetLocalDIMMINGFlag();

    //-------------------------------------------------------------------------------------------------
    /// Get Local DIMMING Panel
    /// @return MAPI_BOOL to tell if bIsEEPROMHdcpenable is true.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetLocalDIMMINGPanelSelect();
#endif
    //-------------------------------------------------------------------------------------------------
    /// Get Hdcp SPI Offset
    /// @return Hdcp SPI Offset.
    //-------------------------------------------------------------------------------------------------
    MAPI_U16 getHdcpSPIOffset();

    //-------------------------------------------------------------------------------------------------
    /// Get MAC SPI Offset
    /// @return MAC SPI Offset.
    //-------------------------------------------------------------------------------------------------
    MAPI_U16 getMacSPIOffset();

    //-------------------------------------------------------------------------------------------------
    /// Get AMP init bin path
    /// @return           \b OUT: AMP init bin path
    //-------------------------------------------------------------------------------------------------
    std::string GetAMPBinPath();

    //-------------------------------------------------------------------------------------------------
    /// Set video zoom info
    /// @param enInputSrc \b IN: input source
    /// @param stZoomInfo \b IN: return zoom info
    /// @return TRUE: setting is ok, FALSE: setting is failed
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetVideoZoomInfo(MAPI_INPUT_SOURCE_TYPE enInputSrc, ST_MAPI_VIDEO_ZOOM_INFO *stZoomInfo);

    //-------------------------------------------------------------------------------------------------
    /// Set HdcpKeyEnable
    /// @param enInputSrc \b IN: input source
    /// @return TRUE: get hdcp key cfg success, FALSE: get hdcp key cfg failed
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetHdcpKeyEnable(MAPI_INPUT_SOURCE_TYPE enInputSrc);

    //-------------------------------------------------------------------------------------------------
    /// GetTspSectionFilterConfig
    /// @return : config for section filter number
    //-------------------------------------------------------------------------------------------------
    const MAPI_SECTION_FILTER_CONFIG& GetTspSectionFilterConfig();

    //-------------------------------------------------------------------------------------------------
    /// Get DLC info table
    /// @param index \b IN: DLC info table index
    /// @return : DLC info table
    //-------------------------------------------------------------------------------------------------
    MAPI_XC_DLC_init* GetDLCInfo(MAPI_U8 index);

    //-------------------------------------------------------------------------------------------------
    /// Get DLC info table count
    /// @return : DLC info table count
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetDLCTableCount(void);

    //-------------------------------------------------------------------------------------------------
    /// Set DLC Curve Configuration
    /// @param  index           DLC table index
    /// @param  u16CurveHStart  DLC curve H start
    /// @param  u16CurveHEnd    DLC curve H end
    /// @param  u16CurveVStart  DLC curve V start
    /// @param  u16CurveVEnd    DLC curve V end
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetDLCCurveInfo(MAPI_U8 index, MAPI_U16 u16CurveHStart, MAPI_U16 u16CurveHEnd, MAPI_U16 u16CurveVStart, MAPI_U16 u16CurveVEnd);

    //-------------------------------------------------------------------------------------------------
    /// Get Color Matrix
    /// @return : Color Matrix
    //-------------------------------------------------------------------------------------------------
    MAPI_COLOR_MATRIX* GetColorMatrix();

    //-------------------------------------------------------------------------------------------------
    /// Set Local DIMMING Flag
    /// @param bLocalDIMMINGEnable \b IN: Local DIMMING enable
    /// @return MAPI_BOOL to tell if SetUseEEPROMFlag is true
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetLocalDIMMINGFlag(MAPI_U8 bLocalDIMMINGEnable);

    //-------------------------------------------------------------------------------------------------
    /// Set audio volume curve table
    /// @param pVolumeCurveBlock    \b IN: pointer to audio volume curve table
    //-------------------------------------------------------------------------------------------------
    void SetVolumeCurveCfgBlock(const VolumeCurve_t* const pVolumeCurveBlock);
    //------------------------------------------------------------------------------
    /// Set Volume Compensation    /// @param bEnableVolCom \b IN: Enable Volume Compensation
    //------------------------------------------------------------------------------
    void SetVolumeCompensationFlag(MAPI_BOOL bEnableVolCom);

       //-------------------------------------------------------------------------------------------------
    /// Set power on launch Netflix key
    /// @param u8PowerOnNetflixKey \b IN: power on launch Netflix key
    /// @return  None
    //-------------------------------------------------------------------------------------------------
    void SetPowerOnNetflixKey(MAPI_U8 u8PowerOnNetflixKey);

    //-------------------------------------------------------------------------------------------------
    /// Get power on launch Netflix key
    /// @return : power on launch Netflix key
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetPowerOnNetflixKey();

    //-------------------------------------------------------------------------------------------------
    /// get OAD customer oui
    /// @return                 \b OUT: customer oui
    //-------------------------------------------------------------------------------------------------
    MAPI_U32 getOADCustomerOUI(void);

    //-------------------------------------------------------------------------------------------------
    /// get OAD hw model
    /// @return                 \b OUT: hw model
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 getOADHWModel(void);

    //-------------------------------------------------------------------------------------------------
    /// get OAD hw version
    /// @return                 \b OUT: hw version
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 getOADHWVersion(void);

    //-------------------------------------------------------------------------------------------------
    /// get OAD ap sw model
    /// @return                 \b OUT: ap sw model
    //-------------------------------------------------------------------------------------------------
    MAPI_U16 getOADAPSWModel(void);

    //-------------------------------------------------------------------------------------------------
    /// get OAD ap sw version
    /// @return                 \b OUT: ap sw version
    //-------------------------------------------------------------------------------------------------
    MAPI_U16 getOADAPSWVersion(void);

    //-------------------------------------------------------------------------------------------------
    /// get SDTT OAD customer maker id
    /// @return                 \b OUT: maker id
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 getOADCustomerMakerID(void);

    //-------------------------------------------------------------------------------------------------
    /// get SDTT OAD customer model id
    /// @return                 \b OUT: model id
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 getOADCustomerModelID(void);

    //-------------------------------------------------------------------------------------------------
    /// Check Environment Variable
    /// @param   name   \b IN: variable name
    /// @return   	    \b OUT: TRUE(Variable exists and value equals one) or
    ///                 \b      FALSE(Variable does not exist or value equals zero)
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL CheckEnvironmentVariable(const char* name);

    //-------------------------------------------------------------------------------------------------
    /// Set UseAudio Delay Offset
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetUseAudioDelayOffset(void);

    //-------------------------------------------------------------------------------------------------
    /// Get UseAudio Delay Offset.
    /// @param enInputSrc \b IN: input source
    /// @return                 \b OUT: Delay Offset
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetUseAudioDelayOffset(MAPI_INPUT_SOURCE_TYPE enInputSrc);

    //-------------------------------------------------------------------------------------------------
    /// Set UseSPDIF Delay Offset
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetUseSPDIFDelayOffset(void);

    //-------------------------------------------------------------------------------------------------
    /// Get UseSPDIF Delay Offset.
    /// @param enInputSrc \b IN: input source
    /// @return                 \b OUT: Delay Offset
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetUseSPDIFDelayOffset(MAPI_INPUT_SOURCE_TYPE enInputSrc);


    ///////////////////////////////////////////////////
    // About panel
    //////////////////////////////////////////////////
    //-------------------------------------------------------------------------------------------------
    /// Set active panel
    /// @param enTiming  \b IN: Active panel timing.
    /// @return \b OUT: If the function succeeds, it returns MAPI_TRUE. Otherwise it returns MAPI_FALSE.
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetActivePanel(EN_MAPI_TIMING enTiming);

    //-------------------------------------------------------------------------------------------------
    /// Get active panel timing.
    /// @return active panel timing
    //-------------------------------------------------------------------------------------------------
    EN_MAPI_TIMING GetActivePanelTiming();

    //-------------------------------------------------------------------------------------------------
    /// Get active panel name
    /// @return active panel name
    //-------------------------------------------------------------------------------------------------
    std::string GetActivePanelName();

    //-------------------------------------------------------------------------------------------------
    /// Get active panel
    /// @return pointer to panel info
    //-------------------------------------------------------------------------------------------------
    const PanelInfo_t* GetActivePanel();

    //-------------------------------------------------------------------------------------------------
    /// Get specified panel name
    /// @param enTiming   \b IN: Specified panel timing.
    /// @return active panel name
    //-------------------------------------------------------------------------------------------------
    std::string GetPanelName(EN_MAPI_TIMING enTiming);

    //-------------------------------------------------------------------------------------------------
    /// Get specified panel
    /// @param enTiming   \b IN: Specified panel timing.
    /// @return pointer to panel info
    //-------------------------------------------------------------------------------------------------
    const PanelInfo_t* GetPanel(EN_MAPI_TIMING enTiming);

    //-------------------------------------------------------------------------------------------------
    /// Determine specified panel is exist or not.
    /// @param ePanelTiming   \b IN: Specified panel timing.
    /// @return                         \b OUT: If exist return true, otherwise return false.
    //-------------------------------------------------------------------------------------------------
    bool IsPanelExists(EN_MAPI_TIMING ePanelTiming);

    //-------------------------------------------------------------------------------------------------
    /// Get panel timing.
    /// @param  panelIdx     \b IN:  Specify panel index.
    /// @return                     \b OUT: Current panel timing.
    //-------------------------------------------------------------------------------------------------
    EN_MAPI_TIMING GetPanelTiming(int panelIdx);

    //-------------------------------------------------------------------------------------------------
    /// Get panel backlight PWM table
    /// @return pointer to panel backlight PWM table
    //-------------------------------------------------------------------------------------------------
    const PanelBacklightPWMInfo* GetPanelBacklightPWMInfo();

    //-------------------------------------------------------------------------------------------------
    /// Get panel MOD PVDD Info
    /// @return pointer to panel MOD PVDD Info table
    //-------------------------------------------------------------------------------------------------
    const PanelModPvddPowerInfo* GetPanelModPvddPowerInfo();

    //------------------------------------------------------------------------------
    /// Get supported output timing list.
    /// @param pTimingList                    \b OUT: Output timing list.
    /// @param u16ListSize                   \b IN: Output timing list size.
    /// @return                                      \b OUT: If the function succeeds, it returns the number of output timing in list.
    ///                                                                 If the pTimingList is NULL or u16ListSize is 0, it returns the size of pTimingList you should allocate.
    ///                                                                 If the u16ListSize less than supported output timing count, it returns 0.
    //------------------------------------------------------------------------------
    MAPI_U16 GetSupportedTimingList(EN_MAPI_TIMING *pTimingList, MAPI_U16 u16ListSize);

    //-------------------------------------------------------------------------------------------------
    /// Get Model name from Customer_*.ini
    /// @param  u16ProjectId : Project ID
    /// @param  pModelName : return model name
    /// @param  u16ModelNameLen : pModeName length
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL GetModelName(MAPI_U16 u16ProjectId, char * pModelName, const MAPI_U16 u16ModelNameLen);

    //-------------------------------------------------------------------------------------------------
    /// Get Model description from Customer_*.ini
    /// @param  u16ProjectId : Project ID
    /// @param  pModelDes : return model description
    /// @param  pModelDesLen : pModelDes length
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL GetModelDescription(MAPI_U16 u16ProjectId, char * pModelDes, const MAPI_U16 pModelDesLen);

#if (PIP_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Set PIP configuration into mapi
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetPipInfoSet(void);

    //-------------------------------------------------------------------------------------------------
    /// Set POP configuration into mapi
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetPopInfoSet(void);
#endif

    //-------------------------------------------------------------------------------------------------
    /// Get HDMI EDID Version information
    /// @return pointer to HDMI EDID support version list
    //-------------------------------------------------------------------------------------------------
    const MAPI_U32* GetHDMIEDIDVersionList();

    //-------------------------------------------------------------------------------------------------
    /// Set current HDMI EDID version.
    /// @param  eHdmiEdidVersion      \b IN: Hdmi Edid Version
    /// @return                       \b OUT: TRUE or FALSE
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetHDMIEDIDVersion(EN_HDMI_EDID_VERSION eHdmiEdidVersion);

       //-------------------------------------------------------------------------------------------------
    /// Read HDMI EDID. It is called by UpdataHDMIEDIDInfoSet
    /// @param  pHdmiEdidInfo     HDMI EDID configuration table
    /// @param  pDict   INI parser pointer
    /// @param  eHdmiEdidVersion  input HDMI EDID version
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL LoadHDMIEDIDInfo(HDMI_EDID_Info_t *pHdmiEdidInfo, void *pDict, int iHdmiNum, EN_HDMI_EDID_VERSION eHdmiEdidVersion);

    //-------------------------------------------------------------------------------------------------
    /// Updata HDMI EDID configuration into mapi
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL UpdataHDMIEDIDInfoSet(EN_HDMI_EDID_VERSION eHdmiEdidVersion);

    //-------------------------------------------------------------------------------------------------
    /// Updata HDMI EDID configuration into mapi
    /// @param  iHdimEdidIndex    input HDMI EDID index
    /// @param  eHdmiEdidVersion  input HDMI EDID version
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL UpdataHDMIEDIDInfoSet(int iHdimEdidIndex, EN_HDMI_EDID_VERSION eHdmiEdidVersion);

    //-------------------------------------------------------------------------------------------------
    /// Get BOARD_HDMI_EDID_InfoCount.
    /// @return                        \b OUT: BOARD_HDMI_EDID_InfoCount
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetHDMIEDIDInfoCount();

    //-------------------------------------------------------------------------------------------------
    /// Get VB1 channel order.
    /// @param enVideo                 \b IN: Video timing.
    /// @param enOsd                   \b IN: OSD timing
    /// @param u16Order                \b OUT: Channel order
    /// @return                        \b OUT: MAPI_TRUE or MAPI_FALSE
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL GetVB1ChannelOrder(EN_MAPI_TIMING enVideo, EN_MAPI_TIMING enOsd, MAPI_U16 u16Order[4]);

    //-------------------------------------------------------------------------------------------------
    /// Get HDR level attributes by specified HDR level.
    /// @param enHdrLevel       \b IN: HDR level.
    /// @param pAttribues        \b IN: HDR attribute.
    /// @return                          \b OUT: MAPI_TRUE or MAPI_FALSE
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL GetHdrLevelAttribues(E_MAPI_XC_HDR_LEVEL enHdrLevel, MAPI_XC_HDR_LEVEL_ATTRIBUTES *pAttribues);

#if (MHL_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// get HDMI input source type for MHL
    /// @return                 \b OUT: Input Source Type, HDMI only
    //-------------------------------------------------------------------------------------------------
    MAPI_INPUT_SOURCE_TYPE GetMHLSource(void);
#endif

protected:
    /// Define aspect ratio type
    typedef enum
    {
        /// Default
        E_AR_DEFAULT = 0,
        /// 16x9
        E_AR_16x9,
        /// 4x3
        E_AR_4x3,
        /// Auto
        E_AR_AUTO,
        /// Panorama
        E_AR_Panorama,
        /// Just Scan
        E_AR_JustScan,
        /// Zoom 1
        E_AR_Zoom1,
        /// Zoom 2
        E_AR_Zoom2,
        E_AR_14x9,
        /// point to point
        E_AR_DotByDot,
         /// Subtitle
        E_AR_Subtitle,
        /// movie
        E_AR_Movie,
        /// Personal
        E_AR_Personal,
        /// 4x3 Panorama
        E_AR_4x3_PanScan,
        /// 4x3 Letter Box
        E_AR_4x3_LetterBox,
        /// 16x9 PillarBox
        E_AR_16x9_PillarBox,
        /// 16x9 PanScan
        E_AR_16x9_PanScan,
        /// 4x3 Combind
        E_AR_4x3_Combind,
        /// 16x9  Combind
        E_AR_16x9_Combind,
        /// Zoom 2X
        E_AR_Zoom_2x,
        /// Zoom 3X
        E_AR_Zoom_3x,
        /// Zoom 4X
        E_AR_Zoom_4x,
        /// In front of E_AR_CUS is Supernova area and the customization area at the back of E_AR_CUS.
        E_AR_CUS =0x20,
        /// Maximum value of this enum
        E_AR_MAX=0x40,
    } MAPI_VIDEO_ARC_Type;

    //-------------------------------------------------------------------------------------------------
    /// Get PWM CH selection for 3D LR
    /// @return                          \b OUT: PWM value
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 Get3DLRPWM(void);

private:

    dictionary *m_pSystemini;
    dictionary *m_pMWBLauncherini;
    dictionary *m_pCustomerini;
    dictionary *m_pPanelini;
#if ( INTEL_WIDI_ENABLE == 1 )
    dictionary *m_pWidiini;
#endif

    dictionary *m_pBoardini;
    dictionary *m_pDCLIni;
    dictionary *m_pDBIni;
    dictionary *m_pMatrixIni;
    dictionary *m_pPcModeIni;
    dictionary *m_pModuleDefaultIni;
    dictionary *m_pModuleIni;

#if (PIP_ENABLE == 1)
    dictionary *m_pPipModeIni;
    dictionary *m_pPopModeIni;
    dictionary *m_pTravelingModeIni;
#endif

    dictionary *m_pTunerModeIni;

    MAPI_U16 m_u16SpiProjectID;
    char * m_pModelName;

#if (STEREO_3D_ENABLE == 1)
    MAPI_PanelType m_stGlobalPanelInfo;
#endif

#if (SQL_DB_ENABLE==1)
    MAPI_BOOL m_bIsSqlDbSet;
    char *m_p3DVideoRouterPath;
    char *m_p3DVideoRouterTableName;
    char *m_p3DTo2DVideoRouterPath;
    char *m_p3DTo2DVideoRouterTableName;
    char *m_pDisplayModeRouterPath;
    char *m_pDisplayModeRouterTableName;
    char *m_p4K2K3DVideoRouterPath;
    char *m_p4K2K3DVideoRouterTableName;
    char *m_p4K2K60Hz3DVideoRouterPath;
    char *m_p4K2K60Hz3DVideoRouterTableName;
    char *m_pFactoryADCAdjustPath;
    char *m_pFactoryADCAdjustTableName;
    char *m_pFactoryColorTempPath;
    char *m_pFactoryColorTempTableName;
    char *m_pFactoryColorTempExPath;
    char *m_pFactoryColorTempExTableName;
    char *m_pNonLinearAdjustPath;
    char *m_pNonLinearAdjustTableName;
    char *m_pNonLinearAdjust3DPath;
    char *m_pNonLinearAdjust3DTableName;
    char *m_pFactoryColorTempEx3DPath;
    char *m_pFactoryColorTempEx3DTableName;
#endif

    MAPI_BOOL m_bLoadIniStatus;

    static pthread_mutex_t  m_moduleParameter_mutex;

    // Panel timing and panel path map.
    std::map<EN_MAPI_TIMING, std::string> m_PanelPathMap;
    // Panel timing and panel info object map
    std::map<EN_MAPI_TIMING, PanelInfo_t *> m_PanelMap;
    // Active panel timing
    EN_MAPI_TIMING m_enActivePanelTiming;
    //Panel info
    static PanelInfo_t *m_pPanelInfo;
    // Specify current panel size which had loaded.
    MAPI_U16 m_u16PanelSize;
    // Specify expect panel size.
    MAPI_U16 m_u16MaxPanelSize;


//
//=== INI files checksum ===//
//
    MAPI_BOOL CheckPattern(MAPI_U8 *pu8Buffer, MAPI_U32 u32ReadSize);
    MAPI_U32 CalculateSimpleCS(MAPI_U8 *pu8Buffer, MAPI_S32 s32Filelength);
    MAPI_U8 iniparser_UpdateCS(const char * ininame);
    MAPI_U8 iniparser_CheckCS(const char * ininame);
    MAPI_BOOL CustomerBackupPathFilename (const char * pPanelPathFileName, char * pBkpPanelFileFullName, int iSize,int mode);
    MAPI_BOOL CheckIniCS(void);
    MAPI_BOOL UpdateCSandBackupIni(const char * pPathFileName, MAPI_U8 u8Mode );


//
//=== Version Control ===//
//
    //-------------------------------------------------------------------------------------------------
    /// Set System ini default value
    /// @param  ver     the version of ini file
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    void SetSystemIniDefaultValue(int ver);

    //-------------------------------------------------------------------------------------------------
    /// Set Panel ini default value
    /// @param  ver     the version of ini file
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    void SetPanelIniDefaultValue(int ver);

    //-------------------------------------------------------------------------------------------------
    /// Set DLC ini default value
    /// @param  ver     the version of ini file
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    void SetDCLIniDefaultValue(int ver);

    //-------------------------------------------------------------------------------------------------
    /// Fill new structure default value
    /// @param  type     00: System INI File, 01: Panel INI File, 02: DLC INI File
    /// @param  ver     the version of ini file
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    void FillNewStructDefaultValue(IniType type, int ver);

    //-------------------------------------------------------------------------------------------------
    /// Check the version of ini file, if version is not the same, system will set default values.
    /// @param  type     00: System INI File, 01: Panel INI File, 02: DLC INI File
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    void CheckVersionAndSetDefaultValue(IniType type);


//
//=== Original System Configuration ===//
//
    //-----------------------------------------------------------------------------
    /// Load INI File
    /// @return                 none
    //-----------------------------------------------------------------------------
    void LoadIniFile(void);

    //-----------------------------------------------------------------------------
    /// Free INI File (except module INI file)
    /// @return                 none
    //-----------------------------------------------------------------------------
    void FreeIniFile(void);

    //-----------------------------------------------------------------------------
    /// Pre-load the INI File (Customer.ini) to SysIniBlock
    /// @return                 \b OUT: true or false
    //-----------------------------------------------------------------------------
    MAPI_BOOL PreLoadSystemIni(void);

    //-------------------------------------------------------------------------------------------------
    /// Load Panle Configuration from panel ini file
    /// @param  pPanelini   input panel ini dictionary
    /// @param  stPanelInfo   output panel info
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL LoadPanelInfo(dictionary* pPanelini, MAPI_PanelType* stPanelInfo);

#if ( INTEL_WIDI_ENABLE == 1 )
    //-------------------------------------------------------------------------------------------------
    /// Set Widi Configuration from customer.ini and widi ini file
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetWidiInfo(void);
#endif

    //-------------------------------------------------------------------------------------------------
    /// Set Panle Configuration from customer.ini and panel ini file
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetPanelInfo(void);

    //-------------------------------------------------------------------------------------------------
    /// Set specified panel timing configuration from m_PanelPathMap.
    /// @param  enTiming     \b IN: Specified panel timing
    /// @return                     \b OUT: If the function success, it returns MAPI_TURE. Otherwise it return MAPI_FALSE.
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetPanelInfo(EN_MAPI_TIMING enTiming);

    //-------------------------------------------------------------------------------------------------
    /// Parser panel ini file and set into gamma configuration table. It is called by SetGammaTableCfg
    /// @param  pGAMMA_TABLE_t     Gamma configuration table
    /// @param  pparameter   input string from DLC.ini
    /// @param  which_rgb   select which rgb
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL ParserGammaTable(GAMMA_TABLE_t *pGAMMA_TABLE_t, const char *pparameter, MAPI_U8 which_rgb);

    //-------------------------------------------------------------------------------------------------
    /// Set Gamma table Configuration from panel ini file
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetGammaTable(int iGammaIdx);

    //-------------------------------------------------------------------------------------------------
    /// Parser DLC.ini and set into DLC configuration table. It is called by SetDLCInfo
    /// @param  pBoard_DLC_init     DLC configuration table
    /// @param  pparameter   input string from DLC.ini
    /// @param  whichcurve   select which curve
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL ParserDLCCurve(MAPI_XC_DLC_init *pBoard_DLC_init, const char *pparameter, E_MAPI_DLC_PURE_INIT_CURVE whichcurve);

    MAPI_BOOL ParserColorCorrectionMatrix(MAPI_S16 *pS16Matrix, const char *pparameter, MAPI_U8 u8ItemCnt);

    //-------------------------------------------------------------------------------------------------
    /// Set DLC Curve Configuration from DCL.ini
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetDLCInfo(void);

    MAPI_BOOL SetColorMatrix(void);

    //-------------------------------------------------------------------------------------------------
    /// Set TV Parameters
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetTVParam(void);


    //-------------------------------------------------------------------------------------------------
    /// Set I2C Configuration
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetI2CCfg(void);

    //-------------------------------------------------------------------------------------------------
    /// Set GPIO Configuration
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetGPIOCfg(void);

    //-------------------------------------------------------------------------------------------------
    /// Set Input Mux information.
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetInputMux(void);

    //-------------------------------------------------------------------------------------------------
    /// Set ATV Externam Demod  information.
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetATVExtDemodInfo(void);

    //-------------------------------------------------------------------------------------------------
    /// Set Scart  information.
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetScartInfo(void);

    //-------------------------------------------------------------------------------------------------
    /// Set Overscan information.
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetVideoInfo(void);

    //-------------------------------------------------------------------------------------------------
    /// Set Audio Input Mux Configuration into mapi
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetWDTCfg(void);


    //-------------------------------------------------------------------------------------------------
    /// Set Audio Input Mux Configuration into mapi
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetAudioInputMuxCfg(void);

    //-------------------------------------------------------------------------------------------------
    /// Read HDMI EDID. It is called by SetHDMIEDIDInfoSet
    /// @param  pHdmiEdidInfo     HDMI EDID configuration table
    /// @param  pDict   INI parser pointer
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL LoadHDMIEDIDInfo(HDMI_EDID_Info_t *pHdmiEdidInfo, void *pDict, int iHdmiNum);

    //-------------------------------------------------------------------------------------------------
    /// Set VGA EDID configuration into mapi
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetHDMIEDIDInfoSet(void);

    //-------------------------------------------------------------------------------------------------
    /// Set HDMI Analog configuration into mapi
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetHDMITxAnalogInfo(void);

    //-------------------------------------------------------------------------------------------------
    /// Read VGA EDID. It is called by SetVGAEDIDInfo
    /// @param  pVgaEdidInfo     VGA EDID configuration table
    /// @param  pDict   INI parser pointer
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL LoadVGAEDIDInfo(VGA_EDID_Info_t *pVgaEdidInfo, void *pDict);

    //-------------------------------------------------------------------------------------------------
    /// Set VGA EDID configuration into mapi
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetVGAEDIDInfo(void);

    //-------------------------------------------------------------------------------------------------
    /// Set Demux configuration into mapi from board define
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetDemuxInfo(void);


//
//=== Set other common information from board define or ini files ===//
//
    //-------------------------------------------------------------------------------------------------
    /// Set Ursa Configuration
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetUrsaCfg(void);

    //-------------------------------------------------------------------------------------------------
    /// To set PQ Auto NR param from customer.ini
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetPQAutoNRParam(void);

    //-------------------------------------------------------------------------------------------------
    /// To set SAW Type from customer.ini
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetSAWType(void);

    //-------------------------------------------------------------------------------------------------
    /// To set SAR channel from customer.ini
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetSARChannel(void);

    //-------------------------------------------------------------------------------------------------
    /// To set demod configuration from board define
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetDemodConfig(void);

    //-------------------------------------------------------------------------------------------------
    /// To set Panel MOD PVDD from customer.ini
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetPanelModPvddInfo(void);

#if (ENABLE_BACKEND == 1)
    //-------------------------------------------------------------------------------------------------
    /// To set Ursa chip enable from customer.ini
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetUrsaEnable(void);

    //-------------------------------------------------------------------------------------------------
    /// To set Ursa chip type from customer.ini
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetUrsaSelect(void);

    //-------------------------------------------------------------------------------------------------
    /// To set MEMC Panel Enable from customer.ini
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetMEMCPanelEnable(void);

    //-------------------------------------------------------------------------------------------------
    /// To set MEMC Panel type from customer.ini
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetMEMCPanelSelect(void);
#endif

    //-------------------------------------------------------------------------------------------------
    /// To set Tuner chip from customer.ini
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetTunerSelect(void);

    //-------------------------------------------------------------------------------------------------
    /// To set China Descramble rBox Delay Offset from customer.ini
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------

    MAPI_BOOL SetChinaDescramblerBoxDelayOffset(void);

    //-------------------------------------------------------------------------------------------------
    /// To set Tuner PWM Setting from customer.ini
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetTunerPWMInfo(void);

    //-------------------------------------------------------------------------------------------------
    /// To Load Tuner PWM Setting from customer.ini to pTunerPWMInfo
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL LoadTunerPWMInfo(TunerPWMInfo * pTunerPWMInfo);

    //-------------------------------------------------------------------------------------------------
    /// To set Audio AMP number from customer.ini
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetAudioAmpSelect(void);

    //-------------------------------------------------------------------------------------------------
    /// To set Volume Compensation from customer.ini
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetVolumeCompensation(void);

#if (STB_ENABLE == 0)
    //-------------------------------------------------------------------------------------------------
    /// To set Volume Curve from customer.ini
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetVolumeCurve(void);
#endif
    //-------------------------------------------------------------------------------------------------
    /// To set AVSync Delay value from customer.ini
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetAVSyncDelay(void);


    //-------------------------------------------------------------------------------------------------
    /// To set Picture Mode Use Factory Curve flag from customer.ini
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetPictureModeUseFacCurveFlag(void);

    //-------------------------------------------------------------------------------------------------
    /// To set Picture Mode Curve from customer.ini
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetPictureModeCurve(void);

    //-------------------------------------------------------------------------------------------------
    /// To set Pc Mode Timing Table and count from PcModeTimingTable.ini
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetPcModeTimingTable(void);

    //-------------------------------------------------------------------------------------------------
    /// To set osd and video mirror flag from customer.ini
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetMirrorFlag(void);

    //-------------------------------------------------------------------------------------------------
    /// To set 3D OverScanEnable from customer.ini
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL Set3DOverScanEnable(void);


    //-------------------------------------------------------------------------------------------------
    /// To set HbbtvDelayInit flag from customer.ini
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetHbbtvDelayInitFlag(void);

    //for storage hdcp config
    //-------------------------------------------------------------------------------------------------
    /// To set Storage HDCP Cfg from customer.ini
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetStorageHDCPCfg(void);

#if (LOCAL_DIMMING == 1)
    //for storage mac config
    //-------------------------------------------------------------------------------------------------
    /// To set Storage MAC Cfg from customer.ini
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetStorageMACCfg(void);

    //for local dimming config
    //-------------------------------------------------------------------------------------------------
    /// To set Local DIMMING Cfg from customer.ini
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetLocalDIMMINGCfg(void);
#endif

    //-------------------------------------------------------------------------------------------------
    /// To set mode detect count from customer.ini
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetModeDectectCount(void);

#if(MULTI_DEMOD==1)
    //-------------------------------------------------------------------------------------------------
    /// To set demod config from customer.ini
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetMultiDemodCfg(void);
#endif
//
//=== Get  information ===//
//
    //-------------------------------------------------------------------------------------------------
    /// To get the flag that PQ binary file use the default value or customer's binary file
    /// @return                 \b OUT: PQ binary use the default or not.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8  GetPQEnableDefault(void);

#if (PIP_ENABLE == 1)
#if (TRAVELING_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Set Traveling configuration into mapi
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetTravelingInfoSet(void);
#endif

#endif

    //-------------------------------------------------------------------------------------------------
    /// Read tuner mode table from ini file
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL ReadTunerModeTable(void);

    std::vector< std::vector<std::string> > m_TunerModelTable;


    //-------------------------------------------------------------------------------------------------
    /// Get Model name from sys.ini
    /// @param  pSysteminiUpdateData : the ini parser pointer of sys.ini file
    /// @param  pModelName : return model name
    /// @param  u16ModelNameLen : pModeName length
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL GetModelName(dictionary * pSysteminiUpdateData, char * pModelName, const MAPI_U16 u16ModelNameLen);

    //-------------------------------------------------------------------------------------------------
    /// Get the path of Customer ini from sys.ini
    /// @param  pSysteminiUpdateData : the ini parser pointer of sys.ini file
    /// @param  pModelName : return model name
    /// @param  u16ModelNameLen : pModeName length
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL GetModelIni(dictionary * pSysteminiUpdateData, char * pModelName, const MAPI_U16 u16ModelNameLen);

    //-------------------------------------------------------------------------------------------------
    /// Get the string of 'Model' from Customer_*.ini
    /// @param  pStr : The 'feature' of model to get. e.g. 'Name', 'Description'
    /// @param  u16ProjectId : Project ID
    /// @param  pRetStr : return feature string
    /// @param  u16StrLen : the length of feature string
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL _GetCustomerModelFeature(const char* pStr, MAPI_U16 u16ProjectId, char * pRetModelName, const MAPI_U16 u16ModelNameLen);

//
//=== Set Ini === //
//
    //-------------------------------------------------------------------------------------------------
    /// Set String into Ini file
    /// @param  IniFilePath :  the full path of ini file, for example "/Customer/sys.ini"
    /// @param  pKey : ini parser key name "section:item", for example "model:gModelName"
    /// @param  pValue : the value will be update into ini file
    /// @return                 \b OUT:  False or True
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL   IniSetStr(char * IniFilePath, char * pKey, char * pValue);

#if (DYNAMIC_I2C_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// To get tuner binary file's setting
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL LoadI2CBinInfo();
#endif

    //-------------------------------------------------------------------------------------------------
    /// To get gamma binary file's path  from customer.ini
    /// @return                 \b OUT: gamma table value
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL LoadGammaBinInfo();

    //-------------------------------------------------------------------------------------------------
    /// Set AMP regs init bin path into mapi
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetAMPInfo(void);
    //-------------------------------------------------------------------------------------------------
    /// Set video zoom info
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetVideoZoomInfo(void);

    //-------------------------------------------------------------------------------------------------
    /// load module ini files
    //-------------------------------------------------------------------------------------------------
    void LoadModuleIniFile(void);

    //-------------------------------------------------------------------------------------------------
    /// Parser "{ str0, str1, ..., strn }" string to string array
    /// @param  inputStr :  The input string format like "{ str0, str1, ..., strn }".
    /// @param  outputStrArray : The parsed result array like { "str0", "str1", ..., "strn" }.
    //-------------------------------------------------------------------------------------------------
    void ParserStringToArray(const char *inputStr, std::vector<std::string*> &outputStrArray);

    //-------------------------------------------------------------------------------------------------
    /// Set CI slot count
    /// @param u8CISlotCount   \b IN: CI slot count
    //-------------------------------------------------------------------------------------------------
    void SetCISlotCount(MAPI_U8 u8CISlotCount);

    //-------------------------------------------------------------------------------------------------
    ///  set MSPI configuration
    /// @param pMSPIDev \b IN: MSPI device information
    /// @param HWNum \b IN: hardware MSPI number
    /// @param DEVNum \b IN: MSPI interface device number
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetMSPICfg(MSPIConfig_s *pMSPIDev, MAPI_U8 HWNum, MAPI_U8 DEVNum);

    //-------------------------------------------------------------------------------------------------
    /// To Set VD capture window mode
    /// @param enVDCaptureWinMode \b IN: VD capture window mode
    /// @return                 \b OUT: none
    //-------------------------------------------------------------------------------------------------
    void SetVDCaptureWinMode(EN_VD_CAPTURE_WINDOW_MODE  enVDCaptureWinMode);

    //------------------------------------------------------------------------------
    /// Set gamma table configuration
    /// @param pGammaTableInfo \b IN: pointer to gamma table information
    /// @param u8TblCnt   \b IN: number of gamma table configurations
    /// @param u8Default  \b IN: default gamma table index
    //------------------------------------------------------------------------------
    void SetGammaTableCfg(GAMMA_TABLE_t* pGammaTableInfo[], MAPI_U8 u8TblCnt, MAPI_U8 u8Default);

    //------------------------------------------------------------------------------
    /// Set PC source signal detected count
    /// @param pPcdetectCount \b IN: stable and unstable detected count
    //------------------------------------------------------------------------------
    void SetPcDetectModeCount(const stSignalDetectCount* const pPcdetectCount);

    //------------------------------------------------------------------------------
    /// Set HDMI source signal detected count
    /// @param pHdmiDetectCount \b IN: stable and unstable detected count
    //------------------------------------------------------------------------------
    void SetHDMIDetectModeCount(const stSignalDetectCount* const pHdmiDetectCount);

        //------------------------------------------------------------------------------
    /// Set COMP source signal detected count
    /// @param pCompDetectCount \b IN: stable and unstable detected count
    //------------------------------------------------------------------------------
    void SetCompDetectModeCount(const stSignalDetectCount* const pCompDetectCount);

    //------------------------------------------------------------------------------
    /// Set default panel configuration
    /// @param pstPanelInfo \b IN: pointer to panel information
    /// @param u16LVDS_Output_type  \b IN: LVDS output type
    /// @param ptPanelBacklightPWMInfo  \b IN: pointer to PanelBacklightPWMInfo
    //------------------------------------------------------------------------------
    void SetDefaultPanelInfoCfg(MAPI_PanelType *pstPanelInfo, MAPI_U16 u16LVDS_Output_type, PanelBacklightPWMInfo *ptPanelBacklightPWMInfo);

    //------------------------------------------------------------------------------
    /// Set panel configuration
    /// @param enTiming     \b IN: panel timing
    /// @param pstPanelInfo \b IN: pointer to panel information
    //------------------------------------------------------------------------------
    void SetPanelInfoCfg(EN_MAPI_TIMING enTiming, MAPI_PanelType *pstPanelInfo);

    //-------------------------------------------------------------------------------------------------
    ///  Set Customer PQC fg
    /// @param pPQCustomerBinFilePath : PQ Customer Bin File Path
    /// @param enWinType : windows type
    //-------------------------------------------------------------------------------------------------
    void SetCustomerPQCfg(char * pPQCustomerBinFilePath, MAPI_PQ_WIN enWinType);

    //-------------------------------------------------------------------------------------------------
    /// Set pc mode timing table
    /// @param pPcModeTimingTable  \b IN: pointer to PC mode timing table
    /// @param u8TableCount  \b IN: number configurations in timing table
    /// @return NONE
    //-------------------------------------------------------------------------------------------------
    void SetPcModeTimingTableCfgBlock(const PcModeTimingTable_t* const pPcModeTimingTable, const MAPI_U8 u8TableCount);

    //-------------------------------------------------------------------------------------------------
    /// Set 3D Panel LR Inverse Config
    /// @param bEnable     \b: IN: Enable 3D Panel LR Inverse or not
    /// @return none
    //-------------------------------------------------------------------------------------------------
    void Set3DPanelLRInverseCfg(MAPI_BOOL bEnable);

    //-------------------------------------------------------------------------------------------------
    /// Set SG Panel Config
    /// @param bEnable     \b: IN: Enable SG panel or not
    /// @return none
    //-------------------------------------------------------------------------------------------------
    void SetSGPanelCfg(MAPI_BOOL bEnable);

    //-------------------------------------------------------------------------------------------------
    /// Set scaler direct output 120hz sg panel config
    /// @param bEnable     \b: IN: Enable or not
    /// @return none
    //-------------------------------------------------------------------------------------------------
    void SetXCOutput120hzSGPanelCfg(MAPI_BOOL bEnable);

    //-------------------------------------------------------------------------------------------------
    /// Set FBL mode's threshold. if vde*hde*framerate >= u32Threshold, the video will go fbl mode
    /// @param u32Threshold     \b: IN: FBL mode's threshold
    /// @return none
    //-------------------------------------------------------------------------------------------------
    void SetFBLModeThreshold(MAPI_U32 u32Threshold);

    //-------------------------------------------------------------------------------------------------
    /// Set Video information configuration
    /// @param enVideoNum   \b IN: video source type
    /// @param u8ResNum     \b IN: number of video info
    /// @param pstVideoInfo     \b IN: pointer to Video info
    /// @param u32HotPlugInverse  \b IN: hot plug inverse flag
    /// @param bDotbydotAble
    /// @param u32HDMI5VDetectGPIOSelect   \b IN: HDMI 5V Detect GPIO Select
    /// @return none
    //-------------------------------------------------------------------------------------------------
    void SetVideoInfoCfg(VideoInfo_s enVideoNum, MAPI_U8 u8ResNum, const ST_MAPI_VIDEO_WINDOW_INFO *pstVideoInfo, MAPI_U32 u32HotPlugInverse, MAPI_BOOL bDotbydotAble=MAPI_FALSE , MAPI_U32 u32Hdmi5vDetectGpioSelect=0x0000);

#if (ENABLE_LITE_SN == 0)
    //-------------------------------------------------------------------------------------------------
    /// Set Ursa init data
    /// @param pUrsaInfo
    /// @return none
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetUrsaInfoCfg(MAPI_UrsaType* pUrsaInfo);
#endif

    //-------------------------------------------------------------------------------------------------
    /// Set Eeprom_type
    /// @param  u8TypeID               \b IN: EEPROM type ID
    /// @return           \b OUT: TRUE:  success
    /// @return           \b OUT: FALSE: failure
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL Set_Eeprom_Type(MAPI_U8 u8TypeID);

    //-------------------------------------------------------------------------------------------------
    /// Set HDCP Key filename path
    /// @param strHDCPKeyFileName \b IN: HDCP key filename
    /// @return MAPI_BOOLean to make sure it work.
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetHDCPKeyFileName(const char* strHDCPKeyFileName);


#if (ENABLE_LITE_SN == 0)
    //-------------------------------------------------------------------------------------------------
    /// Set Swing Level
    /// @param u16SwingLevel
    /// @return none
    //-------------------------------------------------------------------------------------------------
    void SetSwingLevel(MAPI_U16 u16SwingLevel);

    //for storage hdcp config
    //-------------------------------------------------------------------------------------------------
    /// Set Nand Hdcp Flag
    /// @param bNandHdcpEnable \b IN: Nand HDCP enable
    /// @return MAPI_BOOL to tell if SetUseNandHdcpFlag is true
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetUseNandHdcpFlag(MAPI_BOOL bNandHdcpEnable);

    //-------------------------------------------------------------------------------------------------
    /// Set SPI Hdcp Flag
    /// @param bSPIHdcpEnable \b IN: SPI HDCP enable
    /// @return MAPI_BOOL to tell if SetUseSPIHdcpFlag is true
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetUseSPIHdcpFlag(MAPI_BOOL bSPIHdcpEnable);

    //-------------------------------------------------------------------------------------------------
    /// Set SPI Hdcp Bank
    /// @param u8HdcpSPIBank \b IN: HDCP SPI bank
    /// @return MAPI_BOOL to tell if SetHdcpSPIBank is true
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetHdcpSPIBank(MAPI_U8 u8HdcpSPIBank);

    //-------------------------------------------------------------------------------------------------
    /// Set EEPROM Flag
    /// @param bEEPROMHdcpEnable \b IN: EEPROM HDCP enable
    /// @return MAPI_BOOL to tell if SetUseEEPROMFlag is true
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetUseEEPROMFlag(MAPI_BOOL bEEPROMHdcpEnable);

    //-------------------------------------------------------------------------------------------------
    /// Set EEPROM Flag
    /// @param u8HdcpEEPROMAddr \b IN: HDCP EEPROM addr
    /// @return MAPI_BOOL to tell if SetUseEEPROMFlag is true
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetHdcpEEPROMAddr(MAPI_U8 u8HdcpEEPROMAddr);

    //-------------------------------------------------------------------------------------------------
    /// Set MAC is get from SPI Flag
    /// @param bSPIMacEnable \b IN: SPI Mac enable
    /// @return MAPI_BOOL to tell if SetUseSPIMacFlag is true
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetUseSPIMacFlag(MAPI_BOOL bSPIMacEnable);

    //-------------------------------------------------------------------------------------------------
    /// Set SPI MAC Bank
    /// @param u8MacSPIBank \b IN: Mac SPI bank
    /// @return MAPI_BOOL to tell if SetMacSPIBank is true
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetMacSPIBank(MAPI_U8 u8MacSPIBank);

    //-------------------------------------------------------------------------------------------------
    /// Set Local DIMMING Panel Select
    /// @param u8LocalDIMMINGPanelSelect
    /// @return MAPI_BOOL to tell if SetUseEEPROMFlag is true
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetLocalDIMMINGPanelSelect(MAPI_U8 u8LocalDIMMINGPanelSelect);

#endif
    //-------------------------------------------------------------------------------------------------
    /// Set SPI Hdcp Offset
    /// @param u16HdcpSPIOffset \b IN: HDCP SPI offset
    /// @return MAPI_BOOL to tell if SetHdcpSPIOffset is true
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL setHdcpSPIOffSet(MAPI_U16 u16HdcpSPIOffset);

    //-------------------------------------------------------------------------------------------------
    /// Set MAC SPI Offset
    /// @param u16MACSPIOffset \b IN: Mac SPI offset
    /// @return MAPI_BOOL to tell if SetMACSPIOffset is true
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL setMacSPIOffSet(MAPI_U16 u16MACSPIOffset);

    //-------------------------------------------------------------------------------------------------
    /// Set video zoom info
    /// @param enInputSrc \b IN: input source
    /// @param stZoomInfo \b IN: set to zoom info
    /// @return TRUE: setting is ok, FALSE: setting is failed
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetVideoZoomInfo(MAPI_INPUT_SOURCE_TYPE enInputSrc, ST_MAPI_VIDEO_ZOOM_INFO *stZoomInfo);

    //-------------------------------------------------------------------------------------------------
    /// SetTspSectionFilterConfig
    /// @param rCfg \b IN: config for section filter number
    /// @return TRUE: setting is ok, FALSE: setting is failed
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetTspSectionFilterConfig(MAPI_SECTION_FILTER_CONFIG& rCfg);

    //-------------------------------------------------------------------------------------------------
    /// SetPesFilterNumber
    /// @param u32PesFilterNumber \b IN: config for pes filter number
    /// @return TRUE: setting is ok, FALSE: setting is failed
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetPesFilterNumber(MAPI_U32 u32PesFilterNumber);

    //-------------------------------------------------------------------------------------------------
    /// Set MSPI Pad info
    /// @return N/A
    //-------------------------------------------------------------------------------------------------
    void SetMSPIPadInfo(void);

    //-------------------------------------------------------------------------------------------------
    /// GetPesFilterNumber
    /// @return : config for pes filter number
    //-------------------------------------------------------------------------------------------------
    const MAPI_U32 GetPesFilterNumber();

    //-------------------------------------------------------------------------------------------------
    /// Get FRC mode
    /// @return int \b OUT: return value of FRC mode TRUE:enable FALSE:disable
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL GetFrcMode();

#if (NETFLIX_ENABLE==1)
    //-------------------------------------------------------------------------------------------------
    /// set Netflix info
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetNetflixInfo(void);
#endif

    MAPI_BOOL CmdLineParser(char *str,const char* keyword_string);

    //-------------------------------------------------------------------------------------------------
    /// Get SCART1's SAR channel.
    /// @return \b OUT: return SAR channel.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetScart1SarChannel();

    //-------------------------------------------------------------------------------------------------
    /// Get SCART2's SAR channel.
    /// @return \b OUT: return SAR channel.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetScart2SarChannel();

    //-------------------------------------------------------------------------------------------------
    /// Get CEC multi-port select.
    /// @return \b OUT: return which port support CEC.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetCECPortSelect();

    //-------------------------------------------------------------------------------------------------
    /// Set boot Video FileName path
    /// @return MAPI_BOOL to make sure it work.
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetVideoFileName();
	
	//EosTek Patch Begin
	MAPI_BOOL SetVideoFileNameAlternative();
	//EosTek Patch End

    //-------------------------------------------------------------------------------------------------
    /// Set HDMI EDID support version list.
    /// @return                 \b OUT: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetHDMIEDIDVersionList(void);

    ///TV info table
    static S_TV_TYPE_INFO m_TV_info_table;

    //input mux info
    static MuxSize m_MuxConf;
    static MAPI_VIDEO_INPUTSRCTABLE* m_pU32MuxInfo;

    ///external atv demod. info.
    static ATVExtDemodInfo_t m_ATVExtDemod;
    /// scart
    static ScartInfo_t m_ScartInfo;

    ///WDT info
    static WDTInfo_t m_WDTInfo;

    ///Delay HBBTV init or not
    static MAPI_BOOL m_bHbbtvDelayInitFlag;

    // GPIO
    static GPIOConfig_t m_gpioconfig;
    static GPIOInfo_t *m_pGPIOColl;

    // I2C
    static I2CConfig_t m_i2cconfig;
    static I2CBus_t *m_pI2CBuses;
    static I2CDeviceInfo_t *m_pI2CDevices;

    // MSPI
    static MSPI_pad_info_t* m_pMSPIPadInfo;
    static MSPI_config_t m_mspiconfig;
    static MSPIConfig_t *m_pMSPIDevices;

    /// VD capture window mode
    static EN_VD_CAPTURE_WINDOW_MODE m_VDCaptureWinMode;

    ///demux setting
    static DMXConf_t m_DMXRoute[MAXROUTECOUNT];

    //audio mux info
    static AudioMux_t* m_pAudioMuxInfo;
    static AudioPath_t* m_pAudioPathInfo;
    static AudioOutputType_t* m_pAudioOutputTypeInfo;
    static AudioDefualtInit_t m_AudioDefaultInit;

#if (STB_ENABLE == 1)
    // HDMI analog param info
    static HDMITx_Analog_Info_t *m_pHdmiTxAnalogInfo;
#endif
    ///volume mapping curve for audio
    static VolumeCurve_t m_VolumeCurve;

    ///Enable Picture Mode Use Fac Curve
    static MAPI_BOOL m_bEnablePictureModeUseFacCurve;

    ///picture mode curve
    static PictureModeCurve_t m_PictureModeCurve;

    //gamma table info
    static GammaTableSize_t m_GammaConf;
    static GAMMA_TABLE_t* m_pGammaTableInfo;
    static MAPI_U8 m_u8DefaultGammaIdx;



#if ( INTEL_WIDI_ENABLE == 1 )
    // widi info
    static WidiInfo_t m_WidiInfo;
#endif

#if (ENABLE_LITE_SN == 0)
    static PanelInfo_t* m_p4K2KModelInfo;
#endif
    //ursa info
    static MAPI_UrsaType m_UrsaInfo;

    ///Panel Backlight PWM
    static  PanelBacklightPWMInfo  m_PanelBacklightPWMInfo;

    ///Panel MOD PVDD Power Type
    static  PanelModPvddPowerInfo  m_PanelModPvddPowerInfo;

    /// LVDS output type
    static MAPI_U16 m_u16LVDS_Output_type;

    /// Panel link extern type
    static MAPI_U16 m_u16PanelLinkExtType;

    /// Auto NR Param
    static MAPI_AUTO_NR_INIT_PARAM m_AutoNrParam;
#if (MULTI_DEMOD == 1)
    static stMultiExtendDemodCfg m_MultiDemodParam;
#endif
    /// SAW
    static SawArchitecture enSawType;

    /// SAR
    static SarChannel enSarChannel;

    // demod info
    static MAPI_U8* m_u8BOARD_DVBT_DSPRegInitExt;
    static MAPI_U8* m_u8BOARD_DMD_DVBT_InitExt;
    static MAPI_U8* m_u8BOARD_DVBC_DSPRegInitExt;
    static MAPI_U8* m_u8BOARD_DMD_DVBC_InitExt;
    static MAPI_U8* m_u8BOARD_ATSC_DSPRegInitExt;
    static MAPI_U8* m_u8BOARD_DMD_ATSC_InitExt;

    //CustomerPQ
    static std::string pMainPQPath;
    static std::string pSubPQPath;

    /// Pc Mode Timing Table
    static PcModeTimingTable_t *m_pPcModeTimingTable;

    ///Pc Mode Timing Table Count
    static MAPI_U8 m_u8PcModeTimingTableCount;

    //VGA and HDMI EDID info.
    static VGA_EDID_Info_t m_VgaEdidInfo;
    static HDMI_EDID_InfoSet_t m_HdmiEdidInfoSet;

    //HDMI EDID support list
    static MAPI_U32 *m_pU32HdmiEdidVersionList;

    ///mirror config
    static MAPI_BOOL m_bMirrorVideo;
    ///mirror mode
    static MAPI_U8 m_u8MirrorMode;

    ///Enable 3D OverScan
    static MAPI_BOOL m_bEnable3DOverScan;
#if (ENABLE_LITE_SN == 0)
    ///Swing Level
    static MAPI_U16 m_u16SwingLevel;
#endif
    //video info
    static ResolutionInfoSize m_ResoSize[5];
    static void **m_pVideoWinInfo[5];
    static MAPI_U32 m_u32HotPlugInverse;
    static MAPI_U32 m_u32Hdmi5vDetectGpioSelect;
    static MAPI_BOOL m_bDotByDotAble;

    ///Ursa enable.
    static  MAPI_U8  m_u8UrsaEnable;
    ///Ursa type.
    static  MAPI_U8  m_u8UrsaSelect;
    ///Memc Panel enable.
    static  MAPI_U8  m_u8MEMCPanelEnable;
    ///Memc Panel type.
    static  MAPI_U8  m_u8MEMCPanelSelect;
    ///Tuner type.
    static  std::vector<std::string> m_sTunerSelect;
    ///Tuner PWM
    static  TunerPWMInfo m_TunerPWMInfo;
    ///Audio AMP type.
    static  MAPI_U8  m_u8AudioAmpSelect;
    ///Enable 3D Panel LR Inverse
    static MAPI_BOOL m_bEnable3DPanelLRInverse;
    ///Enable Freerun
    static MAPI_BOOL m_bEnableFreerun[2];
    ///Enable 3D SG Panel
    static MAPI_BOOL m_bIsSGPanel;
    ///Is scaler output 120hz sg panel
    static MAPI_BOOL m_bIsXCOutput120hzSGPanel;
    ///Enable Volume Compensation
    static MAPI_BOOL m_bEnableVolumeCom;
    ///eeprom type.
    static  MAPI_U8  m_u8EepromType;
    ///FBL's Threshold
    static  MAPI_U32  m_u32FBLThreshold;

#if (SQL_DB_ENABLE == 1)
    ///DB 3D Video Router Path
    static char* SQL_DB_3DVideoRouterPath;
    ///DB 3D Video Router Table Name
    static char* SQL_DB_3DVideoRouterTableName;
    ///DB Display Mode Router Path
    static char* SQL_DB_DisplayModeRouterPath;
    ///DB Display Mode Router Table Name
    static char* SQL_DB_DisplayModeRouterTableName;
    ///DB Factory Adc Adjust Path
    static char* SQL_DB_FactoryADCAdjustPath;
    ///DB Factory Adc Adjust Table Name
    static char* SQL_DB_FactoryADCAdjustTableName;
    ///DB Factory Color Temp Path
    static char* SQL_DB_FactoryColorTempPath;
    ///DB Factory Color Temp Table Name
    static char* SQL_DB_FactoryColorTempTableName;
    ///DB Factory Color Temp Ex Path
    static char* SQL_DB_FactoryColorTempExPath;
    ///DB Factory Color Temp Ex Table Name
    static char* SQL_DB_FactoryColorTempExTableName;
    ///DB Non Linear Adjust Ex Path
    static char* SQL_DB_NonLinearAdjustExPath;
    ///DB Non Linear Adjust Table Name
    static char* SQL_DB_NonLinearAdjustTableName;
    ///DB Non Linear Adjust 3D Ex Path
    static char* SQL_DB_NonLinearAdjust3DExPath;
    ///DB Non Linear Adjust Table 3D Name
    static char* SQL_DB_NonLinearAdjust3DTableName;
    ///DB Factory Color Temp Ex 3D Path
    static char* SQL_DB_FactoryColorTempEx3DPath;
    ///DB Factory Color Temp Ex 3D Table Name
    static char* SQL_DB_FactoryColorTempEx3DTableName;
#endif
    static MAPI_U16 m_u16AvSyncDelay;
    ///Audio Delay Offset
    static MAPI_U8 m_u8UseAudioDelayOffset;
    ///SPDIF Delay Offset
    static MAPI_U8 m_u8UseSPDIFDelayOffset;

    ///China Descrambler Box Delay Offset
    static MAPI_S16 ChinaDescramblerBoxDelayOffset;

#if (ENABLE_LITE_SN == 0)

    //for storage hdcp config
    ///Nand HDCP Flag
    static MAPI_BOOL m_bNandHdcpEnable;
    ///SPI HDCP Flag
    static MAPI_BOOL m_bSPIHdcpEnable;
    ///HDCP SPI Bank
    static MAPI_U8 m_u8HdcpSPIBank;
    ///HDCP SPI Offset
    static MAPI_U16 m_u16HdcpSPIOffset;
    ///EEPROM HDCP Flag
    static MAPI_BOOL m_bEEPROMHdcpEnable;
    ///HDCP EEPROM Addr
    static MAPI_U8 m_u8HdcpEEPROMAddr;

    //for storage MAC Address config
    ///SPI MAC Flag
    static MAPI_BOOL m_bSPIMacEnable;
    ///MAC SPI Bank
    static MAPI_U8 m_u8MacSPIBank;
    ///MAC SPI Offset
    static MAPI_U16 m_u16MacSPIOffset;
    ///Local DIMMING Flag
    static MAPI_BOOL m_bLocalDIMMINGEnable;
    ///Local DIIMING Panel Select
    static MAPI_U8 m_u8LocalDIMMINGPanelSelect;
#endif

        ///pc mode detect
    static stSignalDetectCount stPcDetectCountInfo;
        ///HDMI mode detect
    static stSignalDetectCount stHDMIDetectCountInfo;
        ///Comp mode detect
    static stSignalDetectCount stCompDetectCountInfo;

    static std::string pAmpInitBinPath;
    //HDCP Key File Name
    static std::string m_bHDCPKeyFileName;

    ///Enable Hdcp key
    static MAPI_BOOL m_bEnableHdcp[HDMI_PORT_MAX];

    static char* m_u8VideoFileName;
	
	
	//EosTek Patch Begin
	static char* m_u8VideoFileNameAlternative;
	//EosTek Patch End
	
	
	
    /// zoom info: zoom1 & zoom2
    /// default zoom info
    static ST_MAPI_VIDEO_ZOOM_INFO m_stDefaultZoomInfo[2];
    /// CVBS zoom info
    static ST_MAPI_VIDEO_ZOOM_INFO m_stCvbsZoomInfo[2];
    /// YPbPr zoom info
    static ST_MAPI_VIDEO_ZOOM_INFO m_stYpbprZoomInfo[2];
    /// HDMI zoom info
    static ST_MAPI_VIDEO_ZOOM_INFO m_stHdmiZoomInfo[2];
    /// DTV zoom info
    static ST_MAPI_VIDEO_ZOOM_INFO m_stDtvZoomInfo[2];
    /// CI slot count
    static MAPI_U8 m_u8CISlotCount;
    /// section filter number config
    static MAPI_SECTION_FILTER_CONFIG m_stTspSectionFilterCfg;
    /// pes filter number
    static MAPI_U32 m_u32PesFilterNumber;

    /// DLC table count
    static MAPI_U8 m_u8DLCTableCount;
    /// DLC table info
    static MAPI_XC_DLC_init *m_psXC_DLC_InitData;
    /// color Matrix
    static MAPI_COLOR_MATRIX *m_pColorMatrix;

    /// Power on launch Netflix key
    static MAPI_U8 m_u8PowerOnNetflixKey;

    // OSD Lpll type
    static  MAPI_U32  m_OsdLpllType;

    // Board ini file name
    std::string m_strBoardIniFileName;

    // 8V channel order
    MAPI_U16 m_u16Vb18VChannelOrder[4];
    // 4V channel order
    MAPI_U16 m_u16Vb14VChannelOrder[4];
    // 2V channel order
    MAPI_U16 m_u16Vb12VChannelOrder[4];
    // 4O channel order
    MAPI_U16 m_u16Vb14OChannelOrder[4];
    // 2O channel order
    MAPI_U16 m_u16Vb12OChannelOrder[4];


    static MAPI_XC_HDR_LEVEL_ATTRIBUTES m_stHdrLevelAttributes[E_MAPI_XC_HDR_MAX];

    //-------------------------------------------------------------------------------------------------
    /// Parse VB1 channel order from ini file (Customer_x.ini).
    /// @param pCustomerIni            \b IN: ini file.
    /// @param pKey                    \b IN: Key name.
    /// @param u16Order                \b OUT: Channel order
    //-------------------------------------------------------------------------------------------------
    void ParseVb1ChannelOrder(dictionary *pCustomerIni, const char *pKey, MAPI_U16 u16Order[4]);

    //-------------------------------------------------------------------------------------------------
    /// Parse HDR level attributes from ini file (Customer_x.ini).
    /// @param pCustomerIni                   \b IN: ini file.
    /// @param stHdrLevelAttributes        \b OUT: HDR level attributes
    //-------------------------------------------------------------------------------------------------
    void LoadHdrLevelAttributes(dictionary *pCustomerIni, MAPI_XC_HDR_LEVEL_ATTRIBUTES stHdrLevelAttributes[E_MAPI_XC_HDR_MAX]);



    void CreateEmptyPanelInfo(MAPI_U16 u16Size);
    void DestoryPanelInfo(MAPI_U16 u16Size);
};
#endif // _SYSTEM_INFO_H_

