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
///////////////////////////////////////////////////////////////////////////////
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
/// @file MSrv_Factory_Mode.h
/// @brief \b Introduction: Factory Mode Description
///
/// @author MStar Semiconductor Inc.
///
/// Features:
/// - Support Factory Mode Menu Operations
///////////////////////////////////////////////////////////////////////////////////////////////////

#ifndef MSrv_Factory_Mode_H
#define MSrv_Factory_Mode_H

#if (ENABLE_LITE_SN == 0)
#include "mapi_version.h"
#endif
#include "MSrv.h"
#include "MSrv_System_Database.h"

#include "MString.h"

using std::string;

///This is for update CI+ key from USB and return state;
typedef enum
{
    UPDATE_KEY_SUSECCE,
    UPDATE_KEY_NO_USB,
    UPDATE_KEY_MOUNT_FAIL,
    UPDATE_KEY_EMPTY,
    UPDATE_KEY_FAIL,
    E_USED_MAC_FINISHED,
    E_WRITE_MAC_TO_MEMORY_ERROR,
    E_POINT_TO_NEXT_MAC_ERROR,
    E_UPGRADE_OK,
    UPDATE_KEY_COUNT,
}UPDATE_KEY_TYPE;

/// This class is used for support Factory Mode Menu Operations
class MSrv_Factory_Mode : public MSrv
{
// ****************************************************************
// Public
// ****************************************************************

public:
    ///screen mute color
    typedef enum
    {
        ///screen mute off
        E_SCREEN_MUTE_OFF,
        ///screen mute white
        E_SCREEN_MUTE_WHITE,
        ///screen mute red
        E_SCREEN_MUTE_RED,
        ///screen mute green
        E_SCREEN_MUTE_GREEN,
        ///screen mute blue
        E_SCREEN_MUTE_BLUE,
        ///screen mute black
        E_SCREEN_MUTE_BLACK,
        E_SCREEN_MUTE_NUMBER,
    } Screen_Mute_Color;


    ///overscan command
    typedef enum
    {
        ///Get overscan table command
        E_OVERSCAN_GET,
        ///Set overscan table command
        E_OVERSCAN_SAVE
    }OVERSCAN_COMMAND;

    ///attribution of config partition
    typedef enum
    {
        ///read only
        MOUNT_CONFIG_RO,
        ///read write
        MOUNT_CONFIG_RW
    }MOUNT_CONFIG;

    /// Firmware type (as argument to GetFwVersion).
    typedef enum
    {
        /// MVD video firmware.
        VIDEO_FW_TYPE_MVD = 0,
        /// HVD video firmware.
        VIDEO_FW_TYPE_HVD,
        /// Audio firmware.
        AUDIO_FW_TYPE,
    } SN_FW_TYPE;

    /// Spi Write Protect Active Status
    typedef enum
    {
        /// Spi Write Protect Active Off
        EN_SPI_WP_ACTIVE_OFF = 0,
        /// Spi Write Protect Active On
        EN_SPI_WP_ACTIVE_ON,
        /// Spi Write Protect Active Done
        EN_SPI_WP_ACTIVE_DONE,
        /// Spi Write Protect Active N/A, max index
        EN_SPI_WP_ACTIVE_COUNT,
    } EN_SPI_WP_ACTIVE_STATUS;

    /// Struct to get/set PVR factory options.
    struct ST_PVR_FACTORY_OPTION
    {
        /// The level of PVR debug messages to print.
        U32 u32DebugLevel;
        /// Enable PVR record all or not.
        BOOL bIsRecordAllEnabled;
        /// Enable CAPVR path.
        BOOL bIsCaPvrEnabled;
        /// Enable PVR file encryption.
        BOOL bIsEncryptionEnabled;
    };

// ------------------------------------------------------------
// Variable Define
// ------------------------------------------------------------

// ------------------------------------------------------------
// Function Define
// ------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------
    /// To initialize the Factory Mode
    /// @return TURE: init successfully
    /// @return FALSE: if have been init already
    //-------------------------------------------------------------------------------------------------
    BOOL Init();

    //-------------------------------------------------------------------------------------------------
    /// To finalize the Factory Mode
    /// @return TURE: success
    /// @return FALSE: fail
    //-------------------------------------------------------------------------------------------------
    BOOL Finalize();

    //-------------------------------------------------------------------------------------------------
    /// To set SW Version. Attention!! Currently, You have to modify SW version from define.
    /// @param CL         \b IN: ChangeList for SW version
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetSWVersion(const MString &CL);

    //-------------------------------------------------------------------------------------------------
    /// To get SW Version.
    /// @param CL         \b IN: ChangeList for SW version
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void GetSWVersion(MString &CL);

    //-------------------------------------------------------------------------------------------------
    /// To get firmware version.
    /// @param eType         \b IN: Firmware type.
    /// @return U32: the version of specified firmware.
    //-------------------------------------------------------------------------------------------------
    U32 GetFwVersion(SN_FW_TYPE eType) const;

    //-------------------------------------------------------------------------------------------------
    /// To reset display resolution
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void ResetDisplayResolution();

    //-------------------------------------------------------------------------------------------------
    /// To get display resolution
    /// @param enDisplayRes         \b OUT: resolution
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void GetDisplayResolution(mapi_display_datatype::EN_DISPLAY_RES_TYPE &enDisplayRes);

    //-------------------------------------------------------------------------------------------------
    /// To set video test pattern
    /// @param enColor         \b IN: test pattern color
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetVideoTestPattern(Screen_Mute_Color enColor);

    //-------------------------------------------------------------------------------------------------
    /// To set picture quality ADC R/G/B Gain and offset values.
    /// @param enWin           \b IN: main of sub window in PIP
    /// @param eAdcIndex       \b IN: the ADC structure array index
    /// @param stADCGainOffset \b IN: the ADC R/G/B Gain and offset values
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetADCGainOffset(MAPI_SCALER_WIN enWin, E_ADC_SET_INDEX eAdcIndex, const MAPI_PQL_CALIBRATION_DATA &stADCGainOffset);

    //-------------------------------------------------------------------------------------------------
    /// To get picture quality ADC R/G/B Gain and offset values.
    /// @param enWin           \b IN: main of sub window in PIP
    /// @param eAdcIndex       \b IN: the ADC structure array index
    /// @param pstADCGainOffset \b OUT: the ADC R/G/B Gain and offset values
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void GetADCGainOffset(MAPI_SCALER_WIN enWin, E_ADC_SET_INDEX eAdcIndex, MAPI_PQL_CALIBRATION_DATA *pstADCGainOffset);

    //-------------------------------------------------------------------------------------------------
    /// To get nonlinear  param
    /// @param enInputSrc    \b IN
    /// @param eNLAIndex    \b IN
    /// @param pstNLA           \b OUT
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void GetNonLinear(MS_NLA_SET_INDEX eNLAIndex, MS_NLA_POINT *pstNLA, MAPI_INPUT_SOURCE_TYPE enInputSrc);

    //-------------------------------------------------------------------------------------------------
    /// To Set nonlinear  param
    /// @param enInputSrc       \b IN
    /// @param eNLAIndex        \b IN
    /// @param pstNLA               \b OUT
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetNonLinear(MS_NLA_SET_INDEX eNLAIndex, MS_NLA_POINT *pstNLA, MAPI_INPUT_SOURCE_TYPE enInputSrc);

    //-------------------------------------------------------------------------------------------------
    /// To do auto ADC R/G/B Gain and offset.
    /// @return TRUE: inputsource is right & set successfully.
    /// @return FALSE: Set Fail
    //-------------------------------------------------------------------------------------------------
    BOOL AutoADC();

    //-------------------------------------------------------------------------------------------------
    /// To adjust brightness value of video  (not in use now)
    /// @param u8SubBrightness   \b IN: Brightness value to set
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetBrightness(U8 u8SubBrightness);

    //-------------------------------------------------------------------------------------------------
    /// To adjust contrast value of video  (not in use now)
    /// @param u8SubContrast   \b IN: Contrast value to set
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetContrast(U8 u8SubContrast);

    //-------------------------------------------------------------------------------------------------
    /// To adjust saturation value of video  (not in use now)
    /// @param u8Saturation   \b IN: Saturation value to set
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetSaturation(U8 u8Saturation);

    //-------------------------------------------------------------------------------------------------
    /// To adjust Sharpness value of video  (not in use now)
    /// @param u8Sharpness   \b IN: Sharpness value to set
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetSharpness(U8 u8Sharpness);

    //-------------------------------------------------------------------------------------------------
    /// To Create enable_mount_partition_rw File in /Customer. The partition could be /config or /certificate
    /// @param mountPartitionPath   \b IN: Partition path on target board
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void CreateMountConfigFile(const char *mountPartitionPath);

    //-------------------------------------------------------------------------------------------------
    /// To Remove enable_mount_partition_rw File in /Customer. The partition could be /config or /certificate
    /// @param mountPartitionPath   \b IN: Partition path on target board
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void RemoveMountConfigFile(const char *mountPartitionPath);

    //-------------------------------------------------------------------------------------------------
    /// To Get enable_mount_partition_rw File Status in /Customer. The partition could be /config or /certificate
    /// @param mountPartitionPath   \b IN: Partition path on target board
    /// @return                 \b OUT: MOUNT_CONFIG_RW = read write, MOUNT_CONFIG_RO = read only
    //-------------------------------------------------------------------------------------------------
    U8 GetMountConfigFile(const char *mountPartitionPath);

    //-------------------------------------------------------------------------------------------------
    /// To adjust Hue value of video  (not in use now)
    /// @param u8Hue   \b IN: Hue value to set
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetHue(U8 u8Hue);

    //-------------------------------------------------------------------------------------------------
    /// To get picture mode Brightness,Contrast,Saturation,Sharpness,Hue values.
    /// @param u8Brightness   \b OUT: Brightness of Picture Mode
    /// @param u8Contrast     \b OUT: Contrast of Picture Mode
    /// @param u8Saturation   \b OUT: Saturation of Picture Mode
    /// @param u8Sharpness   \b OUT: Sharpness of Picture Mode
    /// @param u8Hue             \b OUT: Hue of Picture Mode
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void GetPictureModeValue(U8 &u8Brightness , U8 &u8Contrast , U8 &u8Saturation , U8 &u8Sharpness , U8 &u8Hue);

    //-------------------------------------------------------------------------------------------------
    /// Function not in use
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void CopySubColorDataToAllInput(void);

    //-------------------------------------------------------------------------------------------------
    /// To set white ballance R/G/B Gain and offset values in 8 bit
    /// @param eColorTemp      \b IN: Color temperature
    /// @param u8RedGain       \b IN: Red gain value
    /// @param u8GreenGain     \b IN: Green gain value
    /// @param u8BlueGain      \b IN: Blue gain value
    /// @param u8RedOffset     \b IN: Red offset value
    /// @param u8GreenOffset   \b IN: Green offset value
    /// @param u8BlueOffset    \b IN: Blue offset value
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetWBGainOffset(MSrv_Picture::EN_MS_COLOR_TEMP eColorTemp, U8 u8RedGain, U8 u8GreenGain, U8 u8BlueGain, U8 u8RedOffset, U8 u8GreenOffset, U8 u8BlueOffset);

    //-------------------------------------------------------------------------------------------------
    /// To set white ballance R/G/B Gain and offset values in 16bit
    /// @param eColorTemp      \b IN: Color temperature
    /// @param u16RedGain       \b IN: Red gain value
    /// @param u16GreenGain     \b IN: Green gain value
    /// @param u16BlueGain      \b IN: Blue gain value
    /// @param u16RedOffset     \b IN: Red offset value
    /// @param u16GreenOffset   \b IN: Green offset value
    /// @param u16BlueOffset    \b IN: Blue offset value
    /// @param enSrcType    \b IN: Input source type
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetWBGainOffsetEx(MSrv_Picture::EN_MS_COLOR_TEMP eColorTemp, U16 u16RedGain, U16 u16GreenGain, U16 u16BlueGain, U16 u16RedOffset, U16 u16GreenOffset, U16 u16BlueOffset, MAPI_INPUT_SOURCE_TYPE enSrcType);
	
    void as_SetWBGainOffsetEx(MSrv_Picture::EN_MS_COLOR_TEMP eColorTemp, U16 u16RedGain, U16 u16GreenGain, U16 u16BlueGain, U16 u16RedOffset, U16 u16GreenOffset, U16 u16BlueOffset, MAPI_INPUT_SOURCE_TYPE enSrcType);
    //-------------------------------------------------------------------------------------------------
    /// To get white ballance R/G/B Gain and offset values  in 8bit.
    /// @param eColorTemp      \b IN: Color temperature
    /// @param pu8RedGain       \b OUT: Red gain value
    /// @param pu8GreenGain     \b OUT: Green gain value
    /// @param pu8BlueGain      \b OUT: Blue gain value
    /// @param pu8RedOffset     \b OUT: Red offset value
    /// @param pu8GreenOffset   \b OUT: Green offset value
    /// @param pu8BlueOffset    \b OUT: Blue offset value
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void GetWBGainOffset(MSrv_Picture::EN_MS_COLOR_TEMP eColorTemp, U8 *pu8RedGain, U8 *pu8GreenGain, U8 *pu8BlueGain, U8 *pu8RedOffset, U8 *pu8GreenOffset, U8 *pu8BlueOffset);

    //-------------------------------------------------------------------------------------------------
    /// To get white ballance R/G/B Gain and offset values in 16bit.
    /// @param eColorTemp      \b IN: Color temperature
    /// @param pu16RedGain       \b OUT: Red gain value
    /// @param pu16GreenGain     \b OUT: Green gain value
    /// @param pu16BlueGain      \b OUT: Blue gain value
    /// @param pu16RedOffset     \b OUT: Red offset value
    /// @param pu16GreenOffset   \b OUT: Green offset value
    /// @param pu16BlueOffset    \b OUT: Blue offset value
    /// @param enSrcType    \b IN: Input source type
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void GetWBGainOffsetEx(MSrv_Picture::EN_MS_COLOR_TEMP eColorTemp, U16 *pu16RedGain, U16 *pu16GreenGain, U16 *pu16BlueGain, U16 *pu16RedOffset, U16 *pu16GreenOffset, U16 *pu16BlueOffset, MAPI_INPUT_SOURCE_TYPE enSrcType);

    //-------------------------------------------------------------------------------------------------
    /// Function not in use
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void CopyWhiteBalanceSettingToAllInput(void);

    //-------------------------------------------------------------------------------------------------
    /// To get QMAP IP number
    /// @return U16: IP number
    //-------------------------------------------------------------------------------------------------
    U16 GetQMAPIPNum(void);

    //-------------------------------------------------------------------------------------------------
    /// To get QMAP Table number
    /// @param u8IPIndex       \b IN: IP index
    /// @return U16: Table number
    //-------------------------------------------------------------------------------------------------
    U16 GetQMAPTableNum(U8 u8IPIndex);

    //-------------------------------------------------------------------------------------------------
    /// To get QMAP Current Table Index
    /// @param u8IPIndex       \b IN: IP index
    /// @return U16: Current table index
    //-------------------------------------------------------------------------------------------------
    U16 GetQMAPCurrentTableIdx(U8 u8IPIndex);

    //-------------------------------------------------------------------------------------------------
    /// To get QMAP IP Name
    /// @param IPName          \b OUT: IP name
    /// @param u8IPIndex       \b IN: IP index
    /// @return  None
    //-------------------------------------------------------------------------------------------------
    void GetQMAPIPName(MString &IPName, U8 u8IPIndex);

    //-------------------------------------------------------------------------------------------------
    /// To get QMAP Table Name
    /// @param TableName          \b OUT: Table name
    /// @param u8IPIndex       \b IN: IP index
    /// @param u8TableIndex       \b IN: Table index
    /// @return  None
    //-------------------------------------------------------------------------------------------------
    void GetQMAPTableName(MString &TableName, U8 u8IPIndex, U8 u8TableIndex);

    //-------------------------------------------------------------------------------------------------
    /// To get PQ Version
    /// @param MainWinVersion    \b OUT: Main window PQ version
    /// @param SubWinVersion     \b OUT: Sub window PQ version
    /// @return  None
    //-------------------------------------------------------------------------------------------------
    void GetPQVersion(MString &MainWinVersion, MString &SubWinVersion);

    //-------------------------------------------------------------------------------------------------
    /// To Load PQ table
    /// @param u16TableIndex          \b IN: Table index
    /// @param u16IPIndex               \b IN: IP index
    /// @return  None
    //-------------------------------------------------------------------------------------------------
    void PQ_LoadTable(U16 u16TableIndex, U16 u16IPIndex);

    //-------------------------------------------------------------------------------------------------
    /// To get watch dog status
    /// @return TRUE: WDT on.
    /// @return FALSE: WDT off.
    //-------------------------------------------------------------------------------------------------
    static BOOL GetWDT_ONOFF();

    //-------------------------------------------------------------------------------------------------
    /// To set watch dog status
    /// @param bOnOff                 \b IN: on/off
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetWDT_ONOFF(BOOL bOnOff);

    //-------------------------------------------------------------------------------------------------
    /// To get AgingMode status
    /// @return TRUE: AgingMode on.
    /// @return FALSE: AgingMode off.
    //-------------------------------------------------------------------------------------------------
    BOOL GetAgingMode_ONOFF();

#if (ENABLE_LITE_SN == 0)
    //-------------------------------------------------------------------------------------------------
    /// To get Uart Bus status
    /// @return TRUE: Uart on.
    /// @return FALSE: Uart off.
    //-------------------------------------------------------------------------------------------------
    BOOL GetUart_ONOFF();

    //-------------------------------------------------------------------------------------------------
    /// To set Uart status
    /// @param bOnOff                 \b IN: on/off
    /// @return TRUE: success.
    /// @return FALSE: fail.
    //-------------------------------------------------------------------------------------------------
    BOOL SetUart_ONOFF(BOOL bOnOff);
#endif

    //-------------------------------------------------------------------------------------------------
    /// To Get PVR Record All on/off status
    /// @return TRUE: Record All is on.
    /// @return FALSE: Record All is off.
    //-------------------------------------------------------------------------------------------------
    BOOL GetPVRRecordAll_ONOFF();

    //-------------------------------------------------------------------------------------------------
    /// To set PVR Record All on/off status
    /// @param bOnOff                 \b IN: on/off
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetPVRRecordAll_ONOFF(U32 bOnOff);

    //-------------------------------------------------------------------------------------------------
    /// To get PVR options from storage.
    /// If storage read fail, default settings are returned.
    /// @param  stPvrOption           \b OUT: output struct of PVR options.
    /// @return None.
    //-------------------------------------------------------------------------------------------------
    void GetPvrOptions(ST_PVR_FACTORY_OPTION& stPvrOption);

    //-------------------------------------------------------------------------------------------------
    /// To set PVR options to storage.
    /// @param  stPvrOption           \b IN: input struct of PVR options.
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetPvrOptions(const ST_PVR_FACTORY_OPTION& stPvrOption);

#if (DEPRECATED_CODE == 0)
    //-------------------------------------------------------------------------------------------------
    /// To get MAPI version
    /// @return MAPI_Version : version of MAPI
    //-------------------------------------------------------------------------------------------------
    const MAPI_Version* FuncGetMAPIVersion();

    //-------------------------------------------------------------------------------------------------
    /// To get Utopia Bsp Version
    /// @param pSoftwareVersionInfo  \b IN:SW_VERSION_INFO
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void FuncGetUtopiaBspVersion(MAPI_DRIVER_SW_VERSION_INFO *pSoftwareVersionInfo);

#endif
    //-------------------------------------------------------------------------------------------------
    /// To backup database to USB
    /// @return None
    //-------------------------------------------------------------------------------------------------
	void BeckupDBToUSB();

    //-------------------------------------------------------------------------------------------------
    /// To restore database from USB
    /// @return None
    //-------------------------------------------------------------------------------------------------
	void RestoreDBFromUSB();

    //-------------------------------------------------------------------------------------------------
    /// To set MBoot UART on/off env
    /// @param bOnOff                 \b IN: on/off
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetUartEnv(BOOL bOnOff);


    //-------------------------------------------------------------------------------------------------
    /// To set SN message on/off env
    /// @param bOnOff                 \b IN: on/off
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetSNPrintf_ONOFF(BOOL bOnOff);

    //-------------------------------------------------------------------------------------------------
    /// To set Kernel message on/off env
    /// @param bOnOff                 \b IN: on/off
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetKernelQuiet_ONOFF(BOOL bOnOff);

    //-------------------------------------------------------------------------------------------------
    /// To determine that wether the Kernel Printk debug message is printed
    /// @return TRUE:  enable Kernel Printk debug message.
    /// @return FALSE: disable Kernel Printk debug message.
    //-------------------------------------------------------------------------------------------------
    BOOL GetKernelQuiet_ONOFF();

    //-------------------------------------------------------------------------------------------------
    /// To determine that wether the SN printf debug message is printed
    /// @return TRUE:  enable SN Printf debug message.
    /// @return FALSE: disable SN Printf debug message.
    //-------------------------------------------------------------------------------------------------
    BOOL  GetSNPrintf_ONOFF();

    //-------------------------------------------------------------------------------------------------
    /// To determine that wether the uart debug message is printed by setting environment parameter of MBoot.
    /// @return TRUE:  enable uart debug message.
    /// @return FALSE: disable uart debug message.
    //-------------------------------------------------------------------------------------------------
    BOOL GetUartEnv();

    //-------------------------------------------------------------------------------------------------
    /// To set MBoot Spi Write Protect on/off env
    /// @param bOnOff                 \b IN: on/off
    /// @return TRUE:  success.
    /// @return FALSE: fail.
    //-------------------------------------------------------------------------------------------------
    BOOL SetSpiWriteProtectActive(BOOL bOnOff);

    //-------------------------------------------------------------------------------------------------
    /// To get MBoot Spi Write Protect on/off env
    /// @param enSpiWPActiveStatus      \b OUT: status
    /// @return TRUE:  success.
    /// @return FALSE: fail.
    //-------------------------------------------------------------------------------------------------
    BOOL GetSpiWriteProtectActive(EN_SPI_WP_ACTIVE_STATUS& enSpiWPActiveStatus);

    //-------------------------------------------------------------------------------------------------
    /// To check MBoot Spi Write Protect process on/off
    /// @return TRUE:  on.
    /// @return FALSE: off.
    //-------------------------------------------------------------------------------------------------
    BOOL IsWriteProtectRestrict();

    //-------------------------------------------------------------------------------------------------
    /// To set VD parameters for non-standard signal in factory menu.
    /// @param pFactoryParaData                 \b IN: VD Non Stardard factory parameter
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetFactoryVDParameter(MS_Factory_NS_VD_SET *pFactoryParaData);

    //-------------------------------------------------------------------------------------------------
    /// To set VD parameter AFEC_CF[2] for non-standard signal in factory menu.
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetFactoryVDParameterAFECCF();

    //-------------------------------------------------------------------------------------------------
    /// To set AVD parameters for AFEC Register in factory menu.
    /// @param enParaReg                 \b IN: MAPI_AVD_FactoryPara
    /// @param u8value                   \b IN: u8value
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetFactoryAVDRegValue(MAPI_AVD_FactoryPara  enParaReg, U8 u8value);

    //-------------------------------------------------------------------------------------------------
    /// To set initial VD parameters for non-standard signal in factory menu.
    /// @param pFactoryParaData                 \b IN: VD Non Stardard factory parameter
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetFactoryVDInitParameter(MS_Factory_NS_VD_SET *pFactoryParaData);

    //-------------------------------------------------------------------------------------------------
    /// To get AVD FINE GAIN
    /// @return U8: AVD FINE Gain
    //-------------------------------------------------------------------------------------------------
	U8 GetAVDGainValue(void);

    //-------------------------------------------------------------------------------------------------
    /// To get AVD FINE GAIN and will be a base value for FIX GAIN,
    /// @return U8: AVD Gain For Reference
    //-------------------------------------------------------------------------------------------------
	U8 GetAVDGainForRef(void);

    //-------------------------------------------------------------------------------------------------
    /// To get AVD DSP Version
    /// @return U16: AVDDSPVersion
    //-------------------------------------------------------------------------------------------------
	U16 GetAVDDSPVersion(void);

    //-------------------------------------------------------------------------------------------------
    /// To set AVD GAIN Type
    /// @param bEnable                 \b IN: TRUE/FALSE
    /// @return None
    //-------------------------------------------------------------------------------------------------
	void SetAVDGainAutoTune(BOOL bEnable);

    //-------------------------------------------------------------------------------------------------
    /// To set AVD PLL TRACK Type
    /// @param enPLLTrackType                 \b IN: EN_AVD_PLL_TRACK_TYPE
    /// @return TRUE:  Sucessful
    /// @return FALSE: Fail
    //-------------------------------------------------------------------------------------------------
	BOOL SetAVDPLLTrackType(EN_AVD_PLL_TRACK_TYPE enPLLTrackType);

    //-------------------------------------------------------------------------------------------------
    /// To set AVD Force Slice Type
    /// @param enSliceType                 \b IN: EN_AVD_FORCE_SLICE_TYPE
    /// @return TRUE:  Sucessful
    /// @return FALSE: Fail
    //-------------------------------------------------------------------------------------------------
	BOOL SetAVDForceSliceType(EN_AVD_FORCE_SLICE_TYPE enSliceType);

    //-------------------------------------------------------------------------------------------------
    /// To set AVD H Force Slice Level
    /// @param u8HSliceLevel           \b IN: u8HSliceLevel
    /// @return None
    //-------------------------------------------------------------------------------------------------
	void SetAVDHForceSliceLevel(U8 u8HSliceLevel);

    //-------------------------------------------------------------------------------------------------
    /// To set AVD V Force Slice Level
    /// @param u8VSliceLevel           \b IN: u8VSliceLevel
    /// @return None
    //-------------------------------------------------------------------------------------------------
	void SetAVDVForceSliceLevel(U8 u8VSliceLevel);


    //-------------------------------------------------------------------------------------------------
    /// Read/Write overscan from database
    /// @param  eOSCommand         \b IN: Save/Get
    /// @param  m_VideoWinInfo  \b IN: point of struct ST_MAPI_VIDEO_WINDOW_INFO
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void OverscanDBCommand(OVERSCAN_COMMAND eOSCommand, ST_MAPI_VIDEO_WINDOW_INFO *m_VideoWinInfo);

    //-------------------------------------------------------------------------------------------------
    /// Get input source resolution
    /// @param  peIndex         \b IN(OUT): The resolution
    /// @param  enCurrentInputType  \b IN: Current Input Type
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void GetResolutionMapping(U8* peIndex, MAPI_INPUT_SOURCE_TYPE enCurrentInputType);

    //-------------------------------------------------------------------------------------------------
    /// Get input source detail resolution only for comp hdmi
    /// @param  ResolutionInfo         \b IN(OUT): The resolution
    /// @param  enCurrentInputType  \b IN: Current Input Type
    /// @return TRUE: sucess
    /// @return FALSE: fail
    //-------------------------------------------------------------------------------------------------
    BOOL GetResolutionInfo(U8* ResolutionInfo, MAPI_INPUT_SOURCE_TYPE enCurrentInputType);

    //-------------------------------------------------------------------------------------------------
    /// The wrapper of SetWindow function of all input source type
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetWindowWrapper(void);


    //-------------------------------------------------------------------------------------------------
    /// Set PQ parameter via USB key
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetPQParameterViaUsbKey(void);

    //-------------------------------------------------------------------------------------------------
    /// Set HDCP Key via USB key
    /// @return U32: UPDATE_KEY_SUSECCE for Susecce.
    /// @return U32: UPDATE_KEY_NO_USB for Mount No USB.
    /// @return U32: UPDATE_KEY_MOUNT_FAIL for Mount fail.
    /// @return U32: UPDATE_KEY_EMPTY for empty USB
    //-------------------------------------------------------------------------------------------------
    U32 UpdateHDCPKeyViaUsbKey(void);

    //-------------------------------------------------------------------------------------------------
    /// Set HDCP Key via USB key
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetHDCPKeyViaUsbKey(void);

    //-------------------------------------------------------------------------------------------------
    /// Set CIPlus Key via USB key
    /// @return U32: UPDATE_KEY_SUSECCE for Susecce.
    /// @return U32: UPDATE_KEY_NO_USB for Mount No USB.
    /// @return U32: UPDATE_KEY_MOUNT_FAIL for Mount fail.
    /// @return U32: UPDATE_KEY_EMPTY for empty USB
    //-------------------------------------------------------------------------------------------------
    U32 UpdateCIPlusKeyViaUsbKey(void);

    //------------------------------------------------------------------------------------------------------------
    /// Set Mac Address Via Usb
    /// @param  bErase         \b IN(OUT): IS Erase nand flash
    /// @return U32: UPDATE_KEY_SUSECCE for Susecce.
    /// @return U32: UPDATE_KEY_NO_USB for Mount No USB.
    /// @return U32: UPDATE_KEY_MOUNT_FAIL for Mount fail.
    /// @return U32: UPDATE_KEY_EMPTY for empty USB
    //-----------------------------------------------------------------------------------------------------------
    U32 UpdateMACAddrViaUsbKey(MAPI_BOOL bErase);

    //-------------------------------------------------------------------------------------------------
    /// Set CIPlus Key via USB key
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetCIPlusKeyViaUsbKey(void);

    //----------------------------------------------------------------------------------------------------
    ///Set MAC Address Via USB key
    ///@return
    //----------------------------------------------------------------------------------------------------
    void SetMACAddrViaUsbKey(void);

    //-------------------------------------------------------
    /// Get Mac addr String
    /// @param  MacString         \b IN: Get MAC tmp buffer
    /// @return
    //-----------------------------------------------------------
    void GetMACAddrString(string &MacString);

#if (MSTAR_TVOS == 1)
    //-------------------------------------------------------------------------------------------------
    /// Restore atv program to factory default value.
    /// @param u8CityIndex         \b IN: Specify the index of city
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreFactoryAtvProgramTable(U8 u8CityIndex);

    //-------------------------------------------------------------------------------------------------
    /// Restore dtv program to factory default value.
    /// @param u8CityIndex         \b IN: Specify the index of city
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void RestoreFactoryDtvProgramTable(U8 u8CityIndex);
    //-------------------------------------------------------------------------------------------------
    /// Restore dtv program to factory default value.
    /// @param u8CityIndex         \b IN: Specify the index of city
    /// @param u8dtvRouteMode         \b IN: dtv route mode
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void RestoreFactoryDtvProgramTable(U8 u8CityIndex, U8 u8dtvRouteMode);
#endif

    //-------------------------------------------------------------------------------------------------
    /// Get ursa version information
    /// @param u16Version         \b OUT: the ursa version
    /// @param u32Changelist     \b OUT: the ursa changelist
    /// @return TRUE: sucess
    /// @return FALSE: fail
    //-------------------------------------------------------------------------------------------------
    BOOL GetUrsaVersionInfo(U16*u16Version, U32 *u32Changelist);

#if (WOL_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Set WOLEnable status
    /// @param flag                 \b IN: on/off
    /// @return None
    //-------------------------------------------------------------------------------------------------
    void SetWOLEnableStatus(bool flag);

    //-------------------------------------------------------------------------------------------------
    /// Get WOLEnable Status
    /// @return TRUE: enable
    /// @return FALSE: disable
    //-------------------------------------------------------------------------------------------------
    BOOL GetWOLEnableStatus(void);
#endif

#if (STR_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Get STR Power Mode
    /// @return 0: always DC
    /// @return 1: always AC
    /// @return >1: how many times of DC before AC
    //-------------------------------------------------------------------------------------------------
    U32 GetStrPowerMode(void);
#endif
//------------------------------------------------------------------------------------
    /// get CVBS/Tuner auto fine gain
    /// @return byte range: 0~255
    U8 GetAutoFineGain();

    /// set CVBS/Tuner auto fine gain
    /// @param fineGain      \b IN: byte range: 0~255
    /// @return TRUE: sucess
    /// @return FALSE: fail
    bool SetFixedFineGain(U8 fineGain);

    /// get RF auto adjust gain value
    /// @return byte range: 0~255
    U8 GetAutoRFGain();

    /// get CVBS auto adjustment result
    /// @param rfGain      \b IN: byte range: 0~255
    /// @return byte range: 0~255
    bool SetRFGain(U8 rfGain);

// EosTek Patch Begin
    /// set HDCP Key to ENV
    /// @param u8Key        \b IN: array
    /// @param u32Key_len    \b IN: int length
    /// @param bVer2        \n IN: bool HDCP2.x
    /// @return TRUE: sucess
    /// @return FALSE: fail
    BOOL EosSetHDCPKey(const U8 *u8Key, U16 u32Key_len, BOOL bVer2 = false);

    /// get HDCP Key from ENV
    /// @param u8Key        \b OUT: array
    /// @param u32Key_len    \b OUT: int length
    /// @param bVer2        \n IN: bool HDCP2.x
    /// @return TRUE: sucess
    /// @return FALSE: fail
    BOOL EosGetHDCPKey(U8 *u8Key, U16 u32Key_len, BOOL bVer2 = false);

    /// update HDCP Key to system
    /// @return TRUE: sucess
    /// @return FALSE: fail
    BOOL EosUpdateHDCPKey(BOOL bVer2 = false);

    /// set WB to ENV
    /// @param warm, nature, cool        \b IN: array
    /// @return TRUE: sucess
    /// @return FALSE: fail
    BOOL EosSetWB(mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA warm, mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA nature, mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA cool);

    /// get WB from ENV
    /// @param warm, nature, cool        \b OUT: array
    /// @return TRUE: sucess
    /// @return FALSE: fail
    BOOL EosGetWB(mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA *warm, mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA *nature, mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA *cool);

    /// update WB to system
    /// @return TRUE: sucess
    /// @return FALSE: fail
    BOOL EosUpdateWB(void);

// EosTek Patch End
//------------------------------------------------------------------------------------


// ****************************************************************
// Private
// ****************************************************************

private:
    // ------------------------------------------------------------
    // Variable Define
    // ------------------------------------------------------------
    BOOL        gDeviceBusy;
    MString     SWchangelist;
    U8        m_u8AFEC_CFbit2_BK_ATV;
    U8        m_u8AFEC_CFbit2_BK_AV;
    MAPI_U64    u64MacAddr;

    static BOOL        m_bFlagEnableWDT;
    // ------------------------------------------------------------
    // Function Define
    // ------------------------------------------------------------
};


#endif // MSrv_Factory_Mode_H
