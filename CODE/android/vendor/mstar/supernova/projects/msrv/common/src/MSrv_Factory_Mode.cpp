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

// headers of itself
#include "MSrv_Factory_Mode.h"

// headers of standard C libs
#include <stdlib.h>
#include <stdio.h>
#include <sstream>
#include <unistd.h>
#include <sys/reboot.h>
#include <sys/types.h>

// headers of standard C++ libs
#include <string>
#include <fstream>

// headers of the same layer's
#include "MSrv_Picture.h"
#include "MSrv_Control.h"
#include "MSrv_Player.h"
#include "MSrv_System_Database.h"
#include "MSrv_MountNotifier.h"


#include "MW_DTV_AVMonitor.h"


#if (MSTAR_TVOS == 1)
#include "MSrv_ATV_Database.h"
#include "MSrv_ATV_Player.h"
#include "MSrv_ChannelManager.h"
#include "MSrv_DTV_Player.h"
#endif
// headers of underlying layer's
#include "mapi_vd.h"
#include "mapi_video.h"
#include "mapi_video_out.h"
#include "mapi_interface.h"
#include "mapi_pql.h"
#include "mapi_display.h"
#include "mapi_system.h"
#if (ENABLE_LITE_SN != 1)
#include "mapi_uartdebug.h"
#endif
#include "mapi_env_manager.h"
#include "mapi_storage_spiflash.h"
#include "mapi_storage_factory_config.h"
#include "mapi_storage_eeprom.h"

#if (ENABLE_BACKEND == 1)
#include "mapi_pcb.h"
#include "mapi_ursa.h"
#endif

#include "iniparser.h" // for iniparser_load(), iniparser_freedict()

#define FACTORY_MODE_ERR(fmt, arg...)          printf((char *)fmt, ##arg)
#define FACTORY_MODE_DBG(fmt, arg...)          printf((char *)fmt, ##arg)
#define FACTORY_MODE_IFO(fmt, arg...)          printf((char *)fmt, ##arg)
#define FACTORY_MODE_FLOW(fmt, arg...)         printf((char *)fmt, ##arg)

/// the software change list number
#if(ATSC_SYSTEM_ENABLE)
#define SW_CL "419757"
#else
#define SW_CL "360821"
#endif

#define HDCP_KEY_NAME   "hdcp_key"
#define CI_PLUS_NAME    "ci_plus"
#define MAC_ADDR_NAME   "MAC"
#define MAC_ADDR_PATH_NAME "/Customer/UpdateMACAddrSetting/MAC.bin"

#define ENABLE_MAC_IN_EEP   0

#if (ENABLE_MAC_IN_EEP == 1)
#define EEPEOM_I2C_SLAVE_MACADRR    (0xA8)
#define EEPEOM_MACADRR_ADRR_START   (0x00)
#else
#define FLASH_BANK_MACADRR           (0x0D)
#define FLASH_MACADRR_ADRR_START     (0x00)
#endif
#define MACADRR_DATA_LEN             (8)

#define SYSTEM_BANK_SIZE            0x10000 // 64k

BOOL MSrv_Factory_Mode::m_bFlagEnableWDT = TRUE;

/*
 ********************************************
 FUNCTION   : Init
 USAGE      : To init the Factory_Mode
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
BOOL MSrv_Factory_Mode::Init(void)
{
#ifdef SIM_BUILD
    return TRUE;
#else
    FACTORY_MODE_IFO("---> MSrv_Factory_Mode::Init\n");
    if(gDeviceBusy == 1)
    {
        FACTORY_MODE_IFO("---> 2MSrv_Factory_Mode::Init\n");
        return FALSE;
    }

    gDeviceBusy = 1;
    string s1 = SW_CL;
    SWchangelist.Set(s1);
    FACTORY_MODE_IFO("---> MSrv_Factory_Mode::Init-exit\n");

    MS_USER_SYSTEM_SETTING stUserSetting;
    MSrv_Control::GetMSrvSystemDatabase()->GetUserSystemSetting(&stUserSetting);

    m_bFlagEnableWDT = stUserSetting.bEnableWDT;

    return TRUE;
#endif
}

/*
 ********************************************
 FUNCTION   : Finalize
 USAGE      : To Finalize the Factory_Mode
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
BOOL MSrv_Factory_Mode::Finalize()
{
#ifdef SIM_BUILD
    return TRUE;
#else
    gDeviceBusy = 0;

    return TRUE;
#endif
}

void MSrv_Factory_Mode::SetSWVersion(const MString& CL)
{
    if(!CL.str.empty())
    {
        SWchangelist.str = CL.str;
    }
}

void MSrv_Factory_Mode::GetSWVersion(MString& CL)
{
    CL.str = SWchangelist.str;
}

U32 MSrv_Factory_Mode::GetFwVersion(MSrv_Factory_Mode::SN_FW_TYPE eType) const
{
    switch(eType)
    {
        case VIDEO_FW_TYPE_MVD:
            return mapi_interface::Get_mapi_vdec()->GetFwVersion(mapi_vdec_datatype::E_MAPI_VDEC_FW_TYPE_MVD);
        case VIDEO_FW_TYPE_HVD:
            return mapi_interface::Get_mapi_vdec()->GetFwVersion(mapi_vdec_datatype::E_MAPI_VDEC_FW_TYPE_HVD);
        case AUDIO_FW_TYPE:
            return 0;
        default:
            ASSERT(0);
            return 0;
    }
}

/*
 ********************************************
 FUNCTION   : ResetDisplayResolution
 USAGE      : To set display resolution
 INPUT      : enDisplayRes: resolution
 OUTPUT     : None
 ********************************************
*/
void MSrv_Factory_Mode::ResetDisplayResolution()
{
    mapi_video::SetFactoryMode(MAPI_TRUE);

    //load input source
    MS_USER_SYSTEM_SETTING stGetSystemSetting;
    MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->GetUserSystemSetting(&stGetSystemSetting);

    U16 u16DisplayWidth, u16DisplayVTotal;

    mapi_interface::Get_mapi_display()->Initialize();
    mapi_interface::Get_mapi_display()->GetInfo(mapi_display_datatype::DISPLAY_INFO_WIDTH, &u16DisplayWidth);
    mapi_interface::Get_mapi_display()->GetInfo(mapi_display_datatype::DISPLAY_INFO_V_TOTAL, &u16DisplayVTotal);
    mapi_video::InitHW();
    mapi_interface::Get_mapi_pql(MAPI_MAIN_WINDOW)->Initialize(u16DisplayWidth, u16DisplayVTotal);
    mapi_interface::Get_mapi_pql(MAPI_SUB_WINDOW)->Initialize(u16DisplayWidth, u16DisplayVTotal);
    mapi_video_out::InitHW();

    FACTORY_MODE_IFO("Reopen InputSourceType:%d\n", (int)stGetSystemSetting.enInputSourceType);
    MSrv_Control::GetInstance()->SetInputSource(stGetSystemSetting.enInputSourceType);

    mapi_video::SetFactoryMode(MAPI_FALSE);
}

/*
 ********************************************
 FUNCTION   : GetDisplayResolution
 USAGE      : To get display resolution
 INPUT      : None
 OUTPUT     : enDisplayRes: resolution
 ********************************************
*/
void MSrv_Factory_Mode::GetDisplayResolution(mapi_display_datatype::EN_DISPLAY_RES_TYPE &enDisplayRes)
{
//To do
    printf("MSrv_Factory_Mode::GetDisplayResolution not implemented yet\n");
}

/*
 ********************************************
 FUNCTION   : SetVideoTestPattern
 USAGE      : To set video test pattern
 INPUT      : enColor: test pattern color
 OUTPUT     : None
 ********************************************
*/
void MSrv_Factory_Mode::SetVideoTestPattern(Screen_Mute_Color enColor)
{
#if 1
    mapi_video_datatype::MAPI_VIDEO_Screen_Mute_Color mapi_enColor;
    //initial value
    mapi_enColor = mapi_video_datatype::E_SCREEN_MUTE_BLACK;

    //mapping MSrv Mute Color enum to mapi Mute Color enum
    switch(enColor)
    {
        case E_SCREEN_MUTE_BLACK:
            mapi_enColor = mapi_video_datatype::E_SCREEN_MUTE_BLACK;
            break;
        case E_SCREEN_MUTE_WHITE:
            mapi_enColor = mapi_video_datatype::E_SCREEN_MUTE_WHITE;
            break;
        case E_SCREEN_MUTE_RED:
            mapi_enColor = mapi_video_datatype::E_SCREEN_MUTE_RED;
            break;
        case E_SCREEN_MUTE_BLUE:
            mapi_enColor = mapi_video_datatype::E_SCREEN_MUTE_BLUE;
            break;
        case E_SCREEN_MUTE_GREEN:
            mapi_enColor = mapi_video_datatype::E_SCREEN_MUTE_GREEN;
            break;
        case E_SCREEN_MUTE_OFF:
            mapi_enColor = mapi_video_datatype::E_SCREEN_MUTE_BLACK;
            break;
        default:
            break;
    }
#endif

    if(enColor != E_SCREEN_MUTE_OFF)
    {
        MSrv_Control::GetInstance()->setVideoMuteColor(mapi_enColor);
        MSrv_Control::GetInstance()->SetVideoMute(TRUE);
    }
    else
    {
        MAPI_INPUT_SOURCE_TYPE enInputSrc= MSrv_Control::GetInstance()->GetCurrentInputSource();
        MSrv_Player *player = MSrv_Control::GetInstance()->GetMSrvPlayer(enInputSrc);

        MSrv_Control::GetInstance()->setVideoMuteColor(mapi_enColor);
        MSrv_Control::GetInstance()->SetVideoMute(TRUE);

        if(player != NULL)
        {
            if(player->IsSignalStable())
            {
                MSrv_Control::GetInstance()->SetVideoMute(FALSE);
            }
        }
        else
        {
            FACTORY_MODE_ERR("Get MSrv_Player Instance Fail\n");
        }
    }
}

/*
 ********************************************
 FUNCTION   : SetADCGainOffset
 USAGE      : To set picture quality ADC R/G/B Gain and offset values.
 INPUT      : enWin: main of sub window in PIP
            : stADCGainOffset: the ADC R/G/B Gain and offset values
 OUTPUT     : None
 ********************************************
*/
void MSrv_Factory_Mode::SetADCGainOffset(MAPI_SCALER_WIN enWin, E_ADC_SET_INDEX eAdcIndex, const MAPI_PQL_CALIBRATION_DATA &stADCGainOffset)
{
    mapi_interface::Get_mapi_pql(enWin)->SetADCGainOffset(stADCGainOffset.u16RedGain, stADCGainOffset.u16GreenGain, stADCGainOffset.u16BlueGain, stADCGainOffset.u16RedOffset, stADCGainOffset.u16GreenOffset, stADCGainOffset.u16BlueOffset);
    MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->SetFactoryVideoADC(eAdcIndex, stADCGainOffset);
}

/*
 ********************************************
 FUNCTION   : GetADCGainOffset
 USAGE      : To get picture quality ADC R/G/B Gain and offset values.
 INPUT      : enWin: main of sub window in PIP
 OUTPUT     : stADCGainOffset: the ADC R/G/B Gain and offset values
 ********************************************
*/
void MSrv_Factory_Mode::GetADCGainOffset(MAPI_SCALER_WIN enWin, E_ADC_SET_INDEX eAdcIndex, MAPI_PQL_CALIBRATION_DATA *pstADCGainOffset)
{
    MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->GetVideoAdc(pstADCGainOffset, (void *)&eAdcIndex);
}

void MSrv_Factory_Mode::GetNonLinear(MS_NLA_SET_INDEX eNLAIndex, MS_NLA_POINT *pstNLA , MAPI_INPUT_SOURCE_TYPE enInputSrc)
{
    MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->GetNLASetting(pstNLA, eNLAIndex,enInputSrc);
}

void MSrv_Factory_Mode::SetNonLinear(MS_NLA_SET_INDEX eNLAIndex, MS_NLA_POINT *pstNLA , MAPI_INPUT_SOURCE_TYPE enInputSrc)
{
    MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->SetNLASetting(pstNLA, eNLAIndex,enInputSrc);
}

///This function only use SW calibration mode, because this function is consider being call by ui only.
BOOL MSrv_Factory_Mode::AutoADC()
{
    MAPI_PQL_CALIBRATION_DATA stADCGainOffset;
    E_ADC_SET_INDEX enADCIndex = ADC_SET_NUMS;
    MAPI_INPUT_SOURCE_TYPE enInputSrc= MSrv_Control::GetInstance()->GetCurrentInputSource();
    BOOL bRet = FALSE;

    memset(&stADCGainOffset, 0, sizeof(MAPI_PQL_CALIBRATION_DATA));

    if(IsSrcVga(enInputSrc)
            || (enInputSrc == MAPI_INPUT_SOURCE_YPBPR)
            || (enInputSrc == MAPI_INPUT_SOURCE_YPBPR2)
            || (enInputSrc == MAPI_INPUT_SOURCE_YPBPR3)
            || (enInputSrc == MAPI_INPUT_SOURCE_SCART)
            || (enInputSrc == MAPI_INPUT_SOURCE_SCART2))
    {

        if ( (enInputSrc == MAPI_INPUT_SOURCE_SCART)
          || (enInputSrc == MAPI_INPUT_SOURCE_SCART2))
        {
            ///This function only use SW calibration mode, because this function is consider being call by ui only.
            if(mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_VGA)->AutoGainOffset(MAPI_INPUT_SOURCE_SCART, &enADCIndex, &stADCGainOffset, E_MAPI_CALIBRATION_MODE_SW))
            {
                MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->SetFactoryVideoADC(enADCIndex, stADCGainOffset);
                bRet = TRUE;
            }
        }
        else
        {
            ///This function only use SW calibration mode, because this function is consider being call by ui only.
            if(mapi_interface::Get_mapi_video(enInputSrc)->AutoGainOffset(enInputSrc, &enADCIndex, &stADCGainOffset, E_MAPI_CALIBRATION_MODE_SW))
            {
                MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->SetFactoryVideoADC(enADCIndex, stADCGainOffset);
                bRet = TRUE;
            }
        }
        ///This function only use SW calibration mode, because this function is consider being call by ui only.
        MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->SetADCCalibrationMode(enADCIndex, E_MAPI_CALIBRATION_MODE_SW);
    }
    return bRet;
}

void MSrv_Factory_Mode::SetBrightness(U8 u8SubBrightness)
{
    T_MS_VIDEO stVideo;
    MAPI_INPUT_SOURCE_TYPE eInputType = MSrv_Control::GetInstance()->GetCurrentInputSource();

    MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->GetVideoSetting(&stVideo, &eInputType);
    stVideo.astPicture[stVideo.ePicture].u8Brightness = u8SubBrightness;
    MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->SetVideoSetting(&stVideo, &eInputType);
    MSrv_Control::GetInstance()->GetMSrvPicture()->SetPictureMode(eInputType, MSrv_Picture::PICTURE_ITEM_BRIGHTNESS, u8SubBrightness);
}

void MSrv_Factory_Mode::SetContrast(U8 u8SubContrast)
{
    T_MS_VIDEO stVideo;
    MAPI_INPUT_SOURCE_TYPE eInputType = MSrv_Control::GetInstance()->GetCurrentInputSource();

    MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->GetVideoSetting(&stVideo, &eInputType);
    stVideo.astPicture[stVideo.ePicture].u8Contrast = u8SubContrast;
    MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->SetVideoSetting(&stVideo, &eInputType);
    MSrv_Control::GetInstance()->GetMSrvPicture()->SetPictureMode(eInputType, MSrv_Picture::PICTURE_ITEM_CONTRAST, u8SubContrast);
}

void MSrv_Factory_Mode::SetSaturation(U8 u8Saturation)
{
    T_MS_VIDEO tmpVideo;
    MAPI_INPUT_SOURCE_TYPE eInputType = MSrv_Control::GetInstance()->GetCurrentInputSource();

    MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->GetVideoSetting(&tmpVideo, &eInputType);
    tmpVideo.astPicture[tmpVideo.ePicture].u8Saturation = u8Saturation;
    MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->SetVideoSetting(&tmpVideo, &eInputType);
    MSrv_Control::GetInstance()->GetMSrvPicture()->SetPictureMode(eInputType, MSrv_Picture::PICTURE_ITEM_SATURATION, u8Saturation);
}

void MSrv_Factory_Mode::SetSharpness(U8 u8Sharpness)
{
    T_MS_VIDEO tmpVideo;
    MAPI_INPUT_SOURCE_TYPE eInputType = MSrv_Control::GetInstance()->GetCurrentInputSource();

    MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->GetVideoSetting(&tmpVideo, &eInputType);
    tmpVideo.astPicture[tmpVideo.ePicture].u8Sharpness = u8Sharpness;
    MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->SetVideoSetting(&tmpVideo, &eInputType);
    MSrv_Control::GetInstance()->GetMSrvPicture()->SetPictureMode(eInputType, MSrv_Picture::PICTURE_ITEM_SHARPNESS, u8Sharpness);
}

void MSrv_Factory_Mode::SetHue(U8 u8Hue)
{
    T_MS_VIDEO tmpVideo;
    MAPI_INPUT_SOURCE_TYPE eInputType = MSrv_Control::GetInstance()->GetCurrentInputSource();

    MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->GetVideoSetting(&tmpVideo, &eInputType);
    tmpVideo.astPicture[tmpVideo.ePicture].u8Hue = u8Hue;
    MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->SetVideoSetting(&tmpVideo, &eInputType);
    MSrv_Control::GetInstance()->GetMSrvPicture()->SetPictureMode(eInputType, MSrv_Picture::PICTURE_ITEM_HUE, u8Hue);
}

void MSrv_Factory_Mode::GetPictureModeValue(U8 &u8Brightness , U8 &u8Contrast , U8 &u8Saturation , U8 &u8Sharpness , U8 &u8Hue)
{
    T_MS_VIDEO tmpVideo;
    MAPI_INPUT_SOURCE_TYPE eInputType = MSrv_Control::GetInstance()->GetCurrentInputSource();

    MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->GetVideoSetting(&tmpVideo, &eInputType);

    u8Brightness = tmpVideo.astPicture[tmpVideo.ePicture].u8Brightness;
    u8Contrast = tmpVideo.astPicture[tmpVideo.ePicture].u8Contrast;

    u8Saturation = tmpVideo.astPicture[tmpVideo.ePicture].u8Saturation;
    u8Sharpness = tmpVideo.astPicture[tmpVideo.ePicture].u8Sharpness;
    u8Hue = tmpVideo.astPicture[tmpVideo.ePicture].u8Hue;
}

void MSrv_Factory_Mode::CopySubColorDataToAllInput()
{
    // Andy: Remove it temporarily
    //MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->CopySubColorDataToAllInput();
}
/*
 ********************************************
 FUNCTION   : SetWBGainOffset
 USAGE        : To set white ballance R/G/B Gain and offset values.
 INPUT            : eColorTemp: Color temperature
                      : u8RedGain: Red gain value
                      : u8GreenGain: Green gain value
                      : u8BlueGain: Blue gain value
                      : u8RedOffset: Red offset value
                      : u8GreenOffset: Green offset value
                      : u8BlueOffset: Blue offset value
 OUTPUT     : None
 ********************************************
*/
void MSrv_Factory_Mode::SetWBGainOffset(MSrv_Picture::EN_MS_COLOR_TEMP eColorTemp, U8 u8RedGain, U8 u8GreenGain, U8 u8BlueGain, U8 u8RedOffset, U8 u8GreenOffset, U8 u8BlueOffset)
{
    T_MS_VIDEO tmpVideo;
    mapi_pql_datatype::MAPI_PQL_COLOR_TEMP_DATA tmpColorTemp;
    MAPI_INPUT_SOURCE_TYPE eInputType = MSrv_Control::GetInstance()->GetCurrentInputSource();

    //Set WB Gain and Offset
    MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->GetVideoSetting(&tmpVideo, &eInputType);
    tmpVideo.astPicture[tmpVideo.ePicture].eColorTemp = eColorTemp;
    MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->SetVideoSetting(&tmpVideo, &eInputType);

    //Set WB setting to database
    tmpColorTemp.u8RedGain = u8RedGain;
    tmpColorTemp.u8GreenGain = u8GreenGain;
    tmpColorTemp.u8BlueGain = u8BlueGain;
    tmpColorTemp.u8RedOffset = u8RedOffset;
    tmpColorTemp.u8GreenOffset = u8GreenOffset;
    tmpColorTemp.u8BlueOffset = u8BlueOffset;
    MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->SetFactoryColorTemp(eColorTemp, tmpColorTemp);

    //mapi_interface::Get_mapi_pql(MAPI_MAIN_WINDOW)->SetColorTemperature(&tmpColorTemp);
    //using MSrv_Control::GetMSrvPicture()->SetColorTemperature(&tmpColorTemp) for setUrsaColorTemperatureParam
    MSrv_Control::GetMSrvPicture()->SetColorTemperature(&tmpColorTemp);
}

//Dawn :color temp for each source type
void MSrv_Factory_Mode::SetWBGainOffsetEx(MSrv_Picture::EN_MS_COLOR_TEMP eColorTemp, U16 u16RedGain, U16 u16GreenGain, U16 u16BlueGain, U16 u16RedOffset, U16 u16GreenOffset, U16 u16BlueOffset, MAPI_INPUT_SOURCE_TYPE enSrcType)
{
    T_MS_VIDEO tmpVideo;
    mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA tmpColorTemp;
    EN_MS_INPUT_SOURCE_TYPE enMsInputSourceType = MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->TransMapiInputSourceToMsInputSoutrceType(enSrcType);

    //Set WB Gain and Offset
    MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->GetVideoSetting(&tmpVideo, &enSrcType);
    tmpVideo.astPicture[tmpVideo.ePicture].eColorTemp = eColorTemp;
    MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->SetVideoSetting(&tmpVideo, &enSrcType);

    //Set WB setting to database
    tmpColorTemp.u16RedGain = u16RedGain;
    tmpColorTemp.u16GreenGain = u16GreenGain;
    tmpColorTemp.u16BlueGain = u16BlueGain;
    tmpColorTemp.u16RedOffset = u16RedOffset;
    tmpColorTemp.u16GreenOffset = u16GreenOffset;
    tmpColorTemp.u16BlueOffset = u16BlueOffset;
#if (STEREO_3D_ENABLE == 1)
    if(MSrv_Control::GetInstance()->GetMSrvPicture()->Is3DPicMode())
    {
        MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->SetFactoryColorTempEx3D(eColorTemp, enMsInputSourceType, tmpColorTemp);
    }
    else
#endif
    {
        MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->SetFactoryColorTempEx(eColorTemp, enMsInputSourceType, tmpColorTemp);
    }
    //mapi_interface::Get_mapi_pql(MAPI_MAIN_WINDOW)->SetColorTemperatureEx(&tmpColorTemp);
    //using MSrv_Control::GetMSrvPicture()->SetColorTemperatureEx(&tmpColorTemp) for setUrsaColorTemperatureParam
    MSrv_Control::GetMSrvPicture()->SetColorTemperatureEx(&tmpColorTemp);
}

// EosTek Patch Begin
//ashton: for wb adjust
void MSrv_Factory_Mode::as_SetWBGainOffsetEx(MSrv_Picture::EN_MS_COLOR_TEMP eColorTemp, U16 u16RedGain, U16 u16GreenGain, U16 u16BlueGain, U16 u16RedOffset, U16 u16GreenOffset, U16 u16BlueOffset, MAPI_INPUT_SOURCE_TYPE enSrcType)
{
    mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA tmpColorTemp;
    EN_MS_INPUT_SOURCE_TYPE enMsInputSourceType = MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->TransMapiInputSourceToMsInputSoutrceType(enSrcType);

    //Set WB setting to database
    tmpColorTemp.u16RedGain = u16RedGain;
    tmpColorTemp.u16GreenGain = u16GreenGain;
    tmpColorTemp.u16BlueGain = u16BlueGain;
    tmpColorTemp.u16RedOffset = u16RedOffset;
    tmpColorTemp.u16GreenOffset = u16GreenOffset;
    tmpColorTemp.u16BlueOffset = u16BlueOffset;
#if (STEREO_3D_ENABLE == 1)
    if(MSrv_Control::GetInstance()->GetMSrvPicture()->Is3DPicMode())
    {
        MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->SetFactoryColorTempEx3D(eColorTemp, enMsInputSourceType, tmpColorTemp);
    }
    else
#endif
    {
        MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->SetFactoryColorTempEx(eColorTemp, enMsInputSourceType, tmpColorTemp);
    }
}
// EosTek Patch End

/*
 ********************************************
 FUNCTION   : GetWBGainOffset
 USAGE      : To get white ballance R/G/B Gain and offset values.
 INPUT      : currentInputSource:
                : eColorTemp: Color temperature
OUTPUT          : u8RedGain: Red gain value
                      : u8GreenGain: Green gain value
                      : u8BlueGain: Blue gain value
                      : u8RedOffset: Red offset value
                      : u8GreenOffset: Green offset value
                      : u8BlueOffset: Blue offset value
 ********************************************
*/
void MSrv_Factory_Mode::GetWBGainOffset(MSrv_Picture::EN_MS_COLOR_TEMP eColorTemp, U8 *pu8RedGain, U8 *pu8GreenGain, U8 *pu8BlueGain, U8 *pu8RedOffset, U8 *pu8GreenOffset, U8 *pu8BlueOffset)
{
    mapi_pql_datatype::MAPI_PQL_COLOR_TEMP_DATA stColorTemp;

    MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->GetColorTemp(&stColorTemp, (void *)&eColorTemp);
    *pu8RedGain    = stColorTemp.u8RedGain;
    *pu8GreenGain  = stColorTemp.u8GreenGain;
    *pu8BlueGain   = stColorTemp.u8BlueGain;
    *pu8RedOffset  = stColorTemp.u8RedOffset;
    *pu8GreenOffset = stColorTemp.u8GreenOffset;
    *pu8BlueOffset = stColorTemp.u8BlueOffset;
}

//Dawn :color temp for each source type
void MSrv_Factory_Mode::GetWBGainOffsetEx(MSrv_Picture::EN_MS_COLOR_TEMP eColorTemp, U16 *pu16RedGain, U16 *pu16GreenGain, U16 *pu16BlueGain, U16 *pu16RedOffset, U16 *pu16GreenOffset, U16 *pu16BlueOffset, MAPI_INPUT_SOURCE_TYPE enSrcType)
{
    mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA stColorTemp;
#if (STEREO_3D_ENABLE == 1)
    if(MSrv_Control::GetInstance()->GetMSrvPicture()->Is3DPicMode())
    {
        MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->GetColorTempEx3D(&stColorTemp, (void *)&eColorTemp, enSrcType);
    }
    else
#endif
    {
        MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->GetColorTempEx(&stColorTemp, (void *)&eColorTemp, enSrcType);
    }
    *pu16RedGain    = stColorTemp.u16RedGain;
    *pu16GreenGain  = stColorTemp.u16GreenGain;
    *pu16BlueGain   = stColorTemp.u16BlueGain;
    *pu16RedOffset  = stColorTemp.u16RedOffset;
    *pu16GreenOffset = stColorTemp.u16GreenOffset;
    *pu16BlueOffset = stColorTemp.u16BlueOffset;
}

void MSrv_Factory_Mode::CopyWhiteBalanceSettingToAllInput()
{
    // Andy: remove it temporarily
    //MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->CopyWhiteBalanceSettingToAllInput();
}

U16 MSrv_Factory_Mode::GetQMAPIPNum()
{
    return mapi_interface::Get_mapi_pql(MAPI_MAIN_WINDOW)->GetIPNum();
}

U16 MSrv_Factory_Mode::GetQMAPTableNum(U8 u8IPIndex)
{
    return mapi_interface::Get_mapi_pql(MAPI_MAIN_WINDOW)->GetTableNum(u8IPIndex);
}

U16 MSrv_Factory_Mode::GetQMAPCurrentTableIdx(U8 u8IPIndex)
{
    return mapi_interface::Get_mapi_pql(MAPI_MAIN_WINDOW)->GetCurrentTableIdx(u8IPIndex);
}

void MSrv_Factory_Mode::GetQMAPIPName(MString &IPName, U8 u8IPIndex)
{
    const char *pcstr = (const char *)(mapi_interface::Get_mapi_pql(MAPI_MAIN_WINDOW)->GetIPName(u8IPIndex));
    if(pcstr != NULL)
    {
        IPName.str.assign(pcstr);
    }
}

void MSrv_Factory_Mode::GetPQVersion(MString &MainWinVersion, MString &SubWinVersion)
{
    const char *pcstr = (const char *)(mapi_interface::Get_mapi_pql(MAPI_MAIN_WINDOW)->GetPQVersion(MAPI_PQ_MAIN_WINDOW));
    if(pcstr != NULL)
    {
        MainWinVersion.str.assign(pcstr);
    }

	pcstr = (const char *)(mapi_interface::Get_mapi_pql(MAPI_SUB_WINDOW)->GetPQVersion(MAPI_PQ_SUB_WINDOW));
    if(pcstr != NULL)
    {
        SubWinVersion.str.assign(pcstr);
    }

}

void MSrv_Factory_Mode::GetQMAPTableName(MString &TableName, U8 u8IPIndex, U8 u8TableIndex)
{
    const char *pcstr = (const char *)(mapi_interface::Get_mapi_pql(MAPI_MAIN_WINDOW)->GetTableName(u8IPIndex, u8TableIndex));
    if(pcstr != NULL)
    {
        TableName.str.assign(pcstr);
    }
}

void MSrv_Factory_Mode::PQ_LoadTable(U16 u16TableIndex, U16 u16IPIndex)
{
    mapi_interface::Get_mapi_pql(MAPI_MAIN_WINDOW)->SetQualityByIP_Table(u16TableIndex, u16IPIndex);
}

BOOL MSrv_Factory_Mode::GetWDT_ONOFF()
{
    return m_bFlagEnableWDT;
}

BOOL MSrv_Factory_Mode::GetAgingMode_ONOFF()
{
    MAPI_BOOL bAgingMode;
    MSrv_Control::GetMSrvSystemDatabase()->IsAgingModeEnabled(&bAgingMode);

    return bAgingMode;
}

#if (ENABLE_LITE_SN == 0)
BOOL MSrv_Factory_Mode::GetUart_ONOFF()
{
    MS_USER_SYSTEM_SETTING stUserSetting;
    MSrv_Control::GetMSrvSystemDatabase()->GetUserSystemSetting(&stUserSetting);

    return stUserSetting.bUartBus;
}

BOOL MSrv_Factory_Mode::SetUart_ONOFF(const BOOL bOnOff)
{
    MS_USER_SYSTEM_SETTING stUserSetting;
    MSrv_Control::GetMSrvSystemDatabase()->GetUserSystemSetting(&stUserSetting);

    stUserSetting.bUartBus= bOnOff;

    if(bOnOff ==0)
    {
        mapi_uartdebug* uartDebug = mapi_uartdebug::GetInstance();
        if (uartDebug != NULL)
        {
            uartDebug->DestroyInstance();
        }

    }
    else
    {
        mapi_uartdebug* uartDebug = mapi_uartdebug::GetInstance();
        if (!uartDebug)
        {
            return FALSE;
        }

        if (uartDebug->Start() == MAPI_FALSE)
        {
            return FALSE;
        }
    }

    MSrv_Control::GetMSrvSystemDatabase()->SetUserSystemSetting(&stUserSetting);
    return TRUE;
}
#endif

void MSrv_Factory_Mode::SetWDT_ONOFF(BOOL bOnOff)
{
    MS_USER_SYSTEM_SETTING stUserSetting;
    MSrv_Control::GetMSrvSystemDatabase()->GetUserSystemSetting(&stUserSetting);
    mapi_system * system = mapi_interface::Get_mapi_system();
    IEnvManager* pEnvMan = IEnvManager::Instance();

    m_bFlagEnableWDT = bOnOff;
    stUserSetting.bEnableWDT = bOnOff;
    if(system!=NULL)
    {
        if(bOnOff)
        {
            system->EnableWatchDog();
        }
        else
        {
            system->DisableWatchDog();
        }
    }

    MSrv_Control::GetMSrvSystemDatabase()->SetUserSystemSetting(&stUserSetting);

    if(pEnvMan)
    {
        if(bOnOff)
        {
            pEnvMan->SetEnv("WDT_ENABLE", "1");
        }
        else
        {
            pEnvMan->SetEnv("WDT_ENABLE", "0");
        }
        pEnvMan->SaveEnv();
    }
}

BOOL MSrv_Factory_Mode::GetPVRRecordAll_ONOFF()
{
    ST_PVR_FACTORY_OPTION stOption;
    GetPvrOptions(stOption);
    return stOption.bIsRecordAllEnabled;
}

void MSrv_Factory_Mode::SetPVRRecordAll_ONOFF(U32 bOnOff)
{
    ST_PVR_FACTORY_OPTION stOption;
    GetPvrOptions(stOption);
    if(0 == bOnOff)
    {
        stOption.bIsRecordAllEnabled = FALSE;
    }
    else
    {
        stOption.bIsRecordAllEnabled = TRUE;
    }

    SetPvrOptions(stOption);
}

#define PVR_DEBUG_OPTIONS_FILE_INI        "/Customer/pvr_debug.ini"
#define PVR_DEBUG_LEVEL_TEXT              "PVR_DEBUG_LEVEL"
#define PVR_DEBUG_LEVEL_DEFAULT           1
#define PVR_RECORD_ALL_ENABLED_TEXT       "PVR_RECORD_ALL_ENABLED"
#define PVR_RECORD_ALL_ENABLED_DEFAULT    FALSE
#define PVR_ENCRYPTION_ENABLED_TEXT       "PVR_ENCRYPTION_ENABLED"
#define PVR_ENCRYPTION_ENABLED_DEFAULT    TRUE
#define PVR_CAPVR_ENABLED_TEXT            "PVR_CAPVR_ENABLED"
#if (STB_ENABLE == 1)
#define PVR_CAPVR_ENABLED_DEFAULT         FALSE
#else
#if ((CI_PLUS_ENABLE == 1) && (CIPLUS_PVR_ENABLE == 1))
#define PVR_CAPVR_ENABLED_DEFAULT         TRUE
#else
#define PVR_CAPVR_ENABLED_DEFAULT         FALSE
#endif
#endif

void MSrv_Factory_Mode::GetPvrOptions(ST_PVR_FACTORY_OPTION& stPvrOption)
{
    ST_PVR_FACTORY_OPTION stOption;
    dictionary* pDict = iniparser_load(PVR_DEBUG_OPTIONS_FILE_INI);
    if(pDict)
    {
        stOption.u32DebugLevel =
            iniparser_getunsignedint(pDict, ":" PVR_DEBUG_LEVEL_TEXT, PVR_DEBUG_LEVEL_DEFAULT);
        stOption.bIsRecordAllEnabled =
            iniparser_getunsignedint(pDict, ":" PVR_RECORD_ALL_ENABLED_TEXT, PVR_RECORD_ALL_ENABLED_DEFAULT);
        stOption.bIsEncryptionEnabled =
            iniparser_getunsignedint(pDict, PVR_ENCRYPTION_ENABLED_TEXT, PVR_ENCRYPTION_ENABLED_DEFAULT);
        stOption.bIsCaPvrEnabled =
            iniparser_getunsignedint(pDict, PVR_CAPVR_ENABLED_TEXT, PVR_CAPVR_ENABLED_DEFAULT);
        iniparser_freedict(pDict);
    }
    else
    {
        stOption.u32DebugLevel = PVR_DEBUG_LEVEL_DEFAULT;
        stOption.bIsRecordAllEnabled = PVR_RECORD_ALL_ENABLED_DEFAULT;
        stOption.bIsEncryptionEnabled = PVR_ENCRYPTION_ENABLED_DEFAULT;
        stOption.bIsCaPvrEnabled = PVR_CAPVR_ENABLED_DEFAULT;
    }

    stPvrOption = stOption;
}

void MSrv_Factory_Mode::SetPvrOptions(const ST_PVR_FACTORY_OPTION& stPvrOption)
{
    // Do NOT use iniparser_dump_ini to write because it adding unecessary ':' in key string.
    // fprintf with fixed format [key] = [value], which can be loaded by iniparser_load().
    FILE* pFile = fopen(PVR_DEBUG_OPTIONS_FILE_INI, "w");
    if(NULL == pFile)
    {
        printf("%s, %d: fopen fail: %s", __FILE__, __LINE__, PVR_DEBUG_OPTIONS_FILE_INI);
        return;
    }

    fprintf(pFile, "%s = %u\n", PVR_DEBUG_LEVEL_TEXT, stPvrOption.u32DebugLevel);
    fprintf(pFile, "%s = %u\n", PVR_RECORD_ALL_ENABLED_TEXT, stPvrOption.bIsRecordAllEnabled);
    fprintf(pFile, "%s = %u\n", PVR_ENCRYPTION_ENABLED_TEXT, stPvrOption.bIsEncryptionEnabled);
    fprintf(pFile, "%s = %u\n", PVR_CAPVR_ENABLED_TEXT, stPvrOption.bIsCaPvrEnabled);
    fclose(pFile);
}

void MSrv_Factory_Mode::BeckupDBToUSB()
{
    SystemCmd("cp *.bin /usb/sda1");
    SystemCmd("umount /usb/sda1");
    sync();
    reboot(RB_AUTOBOOT);
}

void MSrv_Factory_Mode::RestoreDBFromUSB()
{
    SystemCmd("cp /usb/sda1/*.bin ./");
    SystemCmd("umount /usb/sda1");
    sync();
    reboot(RB_AUTOBOOT);
}

#if (DEPRECATED_CODE == 0)
const MAPI_Version* MSrv_Factory_Mode::FuncGetMAPIVersion()
{
    const MAPI_Version *mv;
    mv = GetMAPIVersion();
    return mv;
}


void MSrv_Factory_Mode::FuncGetUtopiaBspVersion(MAPI_DRIVER_SW_VERSION_INFO *pSoftwareVersionInfo)
{
    GetUtopiaBspVersion(pSoftwareVersionInfo);
}
#endif

void MSrv_Factory_Mode::SetUartEnv(BOOL bOnOff)
{
    IEnvManager* pEnvMan = IEnvManager::Instance();
    if(pEnvMan && pEnvMan->Initialize())
    {
        BOOL bRet = FALSE;
        if(bOnOff)
        {
            bRet = pEnvMan->SetEnv_Protect("UARTOnOff", "on");
        }
        else
        {
            bRet = pEnvMan->SetEnv_Protect("UARTOnOff", "off");
        }

        if (bRet == TRUE)
        {
            pEnvMan->SaveEnv();
        }
    }
    else
    {
        FACTORY_MODE_ERR("---> IEnvManager Init Fail\n");
    }
}
BOOL MSrv_Factory_Mode::GetKernelQuiet_ONOFF()
{
    BOOL ret = FALSE;
    char sSetQuietTxt[] = " quiet";
    IEnvManager* pEnvMan = IEnvManager::Instance();
    if(pEnvMan && pEnvMan->Initialize())
    {
        IEnvManager_scope_lock block(pEnvMan);
        int size=pEnvMan->QueryLength("bootargs");
        char tmp[size+1];
        memset(tmp,0,size+1);
        pEnvMan->GetEnv_Protect("bootargs",tmp,size);

        char *loc = strstr(tmp,sSetQuietTxt);
        if(!loc)
        {
            ret = TRUE;
        }
    }
    return ret;
}
void MSrv_Factory_Mode::SetKernelQuiet_ONOFF(BOOL bOnOff)
{
    BOOL bRet = FALSE;
    char sSetQuietTxt[] = " quiet";
    int iSetQuietTxtSize = sizeof(sSetQuietTxt)-1;

    IEnvManager* pEnvMan = IEnvManager::Instance();
    if(pEnvMan && pEnvMan->Initialize())
    {
        IEnvManager_scope_lock block(pEnvMan);
        int size=pEnvMan->QueryLength("bootargs");
        char tmp[size+1];
        memset(tmp,0,size+1);
        pEnvMan->GetEnv_Protect("bootargs",tmp,size);

        char *loc = strstr(tmp,sSetQuietTxt);
        if(bOnOff)
        {
            if(!loc)
            {
                bRet = pEnvMan->SetEnv_Protect("bootargs",strcat(tmp,sSetQuietTxt));
                if (bRet == TRUE)
                {
                    pEnvMan->SaveEnv();
                    printf("%s\n",tmp);
                }
            }
        }
        else
        {
            if(loc)
            {
                int i;
                for ( i = loc-tmp + iSetQuietTxtSize; tmp[i] != '\0'; i++ )
                tmp[i-iSetQuietTxtSize] = tmp[i];
                tmp[i-iSetQuietTxtSize] = '\0';


                bRet = pEnvMan->SetEnv_Protect("bootargs",tmp);
                if (bRet == TRUE)
                {
                    pEnvMan->SaveEnv();
                    printf("%s\n",tmp);
                }
            }
        }
    }
    else
    {
        FACTORY_MODE_ERR("---> IEnvManager Init Fail\n");
    }
}
BOOL MSrv_Factory_Mode::GetSNPrintf_ONOFF()
{
    BOOL ret = TRUE;
    string getLineString;
    fstream file;
    file.open("/Customer/SetSNPrintf_ONOFF", fstream::in | fstream::out);
    if(file.good())
    {
        while(getline(file, getLineString))
        {
            if(getLineString =="off")
            {
                ret = FALSE;
            }
        }
    }
    file.close();
    return ret;
}
void MSrv_Factory_Mode::SetSNPrintf_ONOFF(BOOL bOnOff)
{
    if(chdir("/Customer/"))
    {
        printf("***Change directory failed\n");
        ASSERT(0);
    }
    SystemCmd("pwd");
    if(bOnOff)
    {
#if (MSTAR_IPC == 0)
        SystemCmd("sed -i '$d' profile");
        SystemCmd("echo './autorun'>>profile");
#else
        SystemCmd("sed -i '$d' profile");
        SystemCmd("echo './bigbang ---path /applications/apm/appmgr ---argv -ed off -c /Customer/app.cfg'>>profile");
#endif
        SystemCmd("echo 'on'>SetSNPrintf_ONOFF");
        printf("SetSNPrintf_ONOFF = on\n");
    }
    else
    {
#if (MSTAR_IPC == 0)
        SystemCmd("sed -i '$d' profile");
        SystemCmd("echo './autorun>/dev/null 2>&1'>>profile");
#else
        SystemCmd("sed -i '$d' profile");
        SystemCmd("echo './bigbang ---path /applications/apm/appmgr ---argv -ed off -c /Customer/app.cfg>/dev/null 2>&1'>>profile");
#endif
        SystemCmd("echo 'off'>SetSNPrintf_ONOFF");
        printf("SetSNPrintf_ONOFF = off\n");
    }
    SystemCmd("sync");
}

BOOL MSrv_Factory_Mode::GetUartEnv()
{
    BOOL ret = FALSE;
    IEnvManager* pEnvMan = IEnvManager::Instance();
    if(pEnvMan && pEnvMan->Initialize())
    {
        IEnvManager_scope_lock block(pEnvMan);
        int size=pEnvMan->QueryLength("UARTOnOff");
        char tmp[size+1];
        memset(tmp,0,size+1);
        bool result=pEnvMan->GetEnv_Protect("UARTOnOff",tmp,size);
        if(result && (strncmp(tmp,"on",2) == 0))
        {
            ret = TRUE;
        }
    }
    else
    {
        FACTORY_MODE_ERR("---> IEnvManager Init Fail\n");
    }
    return ret;
}

BOOL MSrv_Factory_Mode::SetSpiWriteProtectActive(BOOL bOnOff)
{
    //force return if MBoot do not use new flow
    if(!IsWriteProtectRestrict())
        return FALSE;
    IEnvManager* pEnvMan = IEnvManager::Instance();
    EN_SPI_WP_ACTIVE_STATUS enSpiWPActiveStatus = EN_SPI_WP_ACTIVE_OFF;
    if(GetSpiWriteProtectActive(enSpiWPActiveStatus))
    {
        if(EN_SPI_WP_ACTIVE_DONE == enSpiWPActiveStatus)
        {
            return FALSE;
        }
    }
    else
    {
        return FALSE;
    }
    if(pEnvMan && pEnvMan->Initialize())
    {
        BOOL bRet = FALSE;
        if(bOnOff)
        {
            bRet = pEnvMan->SetEnv_Protect("active_spi_wp", "on");
        }
        else
        {
            bRet = pEnvMan->SetEnv_Protect("active_spi_wp", "off");
        }

        if (bRet == TRUE)
        {
            pEnvMan->SaveEnv();
        }
    }
    else
    {
        FACTORY_MODE_ERR("---> IEnvManager Init Fail\n");
        return FALSE;
    }
    return TRUE;
}

BOOL MSrv_Factory_Mode::GetSpiWriteProtectActive(EN_SPI_WP_ACTIVE_STATUS& enSpiWPActiveStatus)
{
    BOOL ret = FALSE;
    //force return if MBoot do not use new flow
    if(!IsWriteProtectRestrict())
        return ret;
    IEnvManager* pEnvMan = IEnvManager::Instance();
    if(pEnvMan && pEnvMan->Initialize())
    {
        IEnvManager_scope_lock block(pEnvMan);
        int size=pEnvMan->QueryLength("active_spi_wp");
        char tmp[size+1];
        memset(tmp,0,size+1);
        bool result=pEnvMan->GetEnv_Protect("active_spi_wp",tmp,size);
        if(result)
        {
            ret = TRUE;
            if(0 == strncmp(tmp,"off",3))
            {
                enSpiWPActiveStatus = EN_SPI_WP_ACTIVE_OFF;
            }
            else if(0 == strncmp(tmp,"on",2))
            {
                enSpiWPActiveStatus = EN_SPI_WP_ACTIVE_ON;
            }
            else if(0 == strncmp(tmp,"done",4))
            {
                enSpiWPActiveStatus = EN_SPI_WP_ACTIVE_DONE;
            }
            else
            {
                ret = FALSE;
            }
        }
        else
        {
            enSpiWPActiveStatus = EN_SPI_WP_ACTIVE_OFF;
        }
    }
    else
    {
        FACTORY_MODE_ERR("---> IEnvManager Init Fail\n");
    }
    return ret;
}

BOOL MSrv_Factory_Mode::IsWriteProtectRestrict()
{
    //return mapi_storage_spiflash::IsWriteProtectRestrict();
        Imapi_storage_factory_config * pfactoryFlash = Imapi_storage_factory_config::GetInstance();
        return pfactoryFlash->IsWriteProtectRestrict();
}

void MSrv_Factory_Mode::CreateMountConfigFile(const char *mountPartitionPath)
{
    char strCommand[128];

    memset(strCommand, 0, sizeof(strCommand));
    snprintf(strCommand, sizeof(strCommand), "touch %s", mountPartitionPath);
    SystemCmd(strCommand);
    sync();
}

void MSrv_Factory_Mode::RemoveMountConfigFile(const char *mountPartitionPath)
{
    char strCommand[128];

    memset(strCommand, 0, sizeof(strCommand));
    snprintf(strCommand, sizeof(strCommand), "rm %s", mountPartitionPath);
    SystemCmd(strCommand);
    sync();
}

U8 MSrv_Factory_Mode::GetMountConfigFile(const char *mountPartitionPath)
{
    FILE *fd = NULL;
    fd = fopen(mountPartitionPath, "r");
    if(fd == NULL)
    {
        return MOUNT_CONFIG_RO;
    }
    else
    {
        fclose(fd);
        return MOUNT_CONFIG_RW;
    }
}


#if (STB_ENABLE == 0)
void MSrv_Factory_Mode::OverscanDBCommand(OVERSCAN_COMMAND eOSCommand, ST_MAPI_VIDEO_WINDOW_INFO *m_VideoWinInfo)
{
    mapi_video_datatype::MAPI_VIDEO_ARC_Type m_eVideoARCType;
    MAPI_INPUT_SOURCE_TYPE eInputType = MSrv_Control::GetInstance()->GetCurrentInputSource();

    switch(MSrv_Control::GetInstance()->GetCurrentInputSource())
    {
        case MAPI_INPUT_SOURCE_ATV:
        case MAPI_INPUT_SOURCE_CVBS:
        case MAPI_INPUT_SOURCE_CVBS2:
        case MAPI_INPUT_SOURCE_CVBS3:
        case MAPI_INPUT_SOURCE_CVBS4:
        case MAPI_INPUT_SOURCE_CVBS5:
        case MAPI_INPUT_SOURCE_CVBS6:
        case MAPI_INPUT_SOURCE_CVBS7:
        case MAPI_INPUT_SOURCE_CVBS8:
        case MAPI_INPUT_SOURCE_SVIDEO:
        case MAPI_INPUT_SOURCE_SVIDEO2:
        case MAPI_INPUT_SOURCE_SVIDEO3:
        case MAPI_INPUT_SOURCE_SVIDEO4:
        case MAPI_INPUT_SOURCE_SCART:
        case MAPI_INPUT_SOURCE_SCART2:
        {
            EN_VD_SIGNALTYPE eVDSinType;

            switch((MAPI_AVD_VideoStandardType)mapi_interface::Get_mapi_vd()->GetVideoStandard())
            {
                case E_MAPI_VIDEOSTANDARD_PAL_BGHI:
                    eVDSinType = SIG_PAL;
                    break;
                case E_MAPI_VIDEOSTANDARD_NTSC_M:
                    eVDSinType = SIG_NTSC;
                    break;
                case E_MAPI_VIDEOSTANDARD_SECAM:
                    eVDSinType = SIG_SECAM;
                    break;
                case E_MAPI_VIDEOSTANDARD_NTSC_44:
                    eVDSinType = SIG_NTSC_443;
                    break;
                case E_MAPI_VIDEOSTANDARD_PAL_M:
                    eVDSinType = SIG_PAL_M;
                    break;
                case E_MAPI_VIDEOSTANDARD_PAL_N:
                    eVDSinType = SIG_PAL_NC;
                    break;
                case E_MAPI_VIDEOSTANDARD_PAL_60:
                    eVDSinType = SIG_NTSC_443;
                    break;
                default:
                    eVDSinType = SIG_PAL;
                    break;
            }

            //get VideoARCType
            MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->GetVideoArc(&m_eVideoARCType, &eInputType);

            if(eOSCommand == E_OVERSCAN_SAVE)
            {
                MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->SetOverscanSetting(m_VideoWinInfo, eVDSinType, m_eVideoARCType, eInputType);
                printf("VD Overscan saved!\n");
                return;
            }
            else
            {
                //get VD Overscan Setting
                MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->GetOverscanSetting(m_VideoWinInfo, eVDSinType, m_eVideoARCType, eInputType);
            }

            printf("VD Signal Type: %d , ARC_TYPE: %d\n", eVDSinType, m_eVideoARCType);
            printf("Database Current: Left: %d , Right: %d , Top: %d , Bottom: %d\n", m_VideoWinInfo->u8HCrop_Left, m_VideoWinInfo->u8HCrop_Right, m_VideoWinInfo->u8VCrop_Up, m_VideoWinInfo->u8VCrop_Down);
        }
        break;

        case MAPI_INPUT_SOURCE_DTV:
        {
            MAX_DTV_Resolution_Info eDTVRes = E_DTV480i_60;
            //get resolution
            MSrv_Control::GetInstance()->GetMSrvFactoryMode()->GetResolutionMapping((U8*)&eDTVRes, MAPI_INPUT_SOURCE_DTV);

            //get VideoARCType
            MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->GetVideoArc(&m_eVideoARCType, &eInputType);

            if(eOSCommand == E_OVERSCAN_SAVE)
            {
                MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->SetOverscanSetting(m_VideoWinInfo, eDTVRes, m_eVideoARCType, eInputType);
                printf("DTV Overscan saved!\n");
                return;
            }
            else
            {
                //get DTV Overscan Setting
                MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->GetOverscanSetting(m_VideoWinInfo, eDTVRes, m_eVideoARCType, eInputType);
                printf("DTV Overscan get!\n");
            }

            printf("DTV Resolution: %d , ARC_TYPE: %d\n", eDTVRes, m_eVideoARCType);
            printf("Left: %d , Right: %d , Top: %d , Bottom: %d\n", m_VideoWinInfo->u8HCrop_Left, m_VideoWinInfo->u8HCrop_Right, m_VideoWinInfo->u8VCrop_Up, m_VideoWinInfo->u8VCrop_Down);


        }
        break;

        case MAPI_INPUT_SOURCE_YPBPR:
        case MAPI_INPUT_SOURCE_YPBPR2:
        case MAPI_INPUT_SOURCE_YPBPR3:
        {
            mapi_video_datatype::MAX_YPbPr_Resolution_Info eYPbPrRes = mapi_video_datatype::E_YPbPr480i_60;   //must have value(for using pointer)
            //get resolution
            MSrv_Control::GetMSrvFactoryMode()->GetResolutionMapping((U8*)&eYPbPrRes, eInputType);

            //get VideoARCType
            MSrv_Control::GetMSrvSystemDatabase()->GetVideoArc(&m_eVideoARCType, &eInputType);

            printf("Current Resolution: %d\n", MSrv_Control::GetInstance()->GetCurrentInputSource());
            printf("YPbPr Resolution: %d , ARC_TYPE: %d\n", eYPbPrRes, m_eVideoARCType);
            printf("Left: %d , Right: %d , Top: %d , Bottom: %d\n", m_VideoWinInfo->u8HCrop_Left, m_VideoWinInfo->u8HCrop_Right, m_VideoWinInfo->u8VCrop_Up, m_VideoWinInfo->u8VCrop_Down);

            if(eOSCommand == E_OVERSCAN_SAVE)
            {
                MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->SetOverscanSetting(m_VideoWinInfo, eYPbPrRes, m_eVideoARCType, eInputType);
                printf("YPbPr Overscan saved!\n");
                return;
            }
            else
            {
                //get YPbPr Overscan Setting
                printf("going to GetYPbPrOverscanSetting\n");
                MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->GetOverscanSetting(m_VideoWinInfo, eYPbPrRes, m_eVideoARCType, eInputType);
            }
        }
        break;

        case MAPI_INPUT_SOURCE_HDMI:
        case MAPI_INPUT_SOURCE_HDMI2:
        case MAPI_INPUT_SOURCE_HDMI3:
        case MAPI_INPUT_SOURCE_HDMI4:
        case MAPI_INPUT_SOURCE_DVI:
        case MAPI_INPUT_SOURCE_DVI2:
        case MAPI_INPUT_SOURCE_DVI3:
        case MAPI_INPUT_SOURCE_DVI4:
        {
            mapi_video_datatype::MAX_HDMI_Resolution_Info eHDMIRes = mapi_video_datatype::E_HDMI480i_60;
            //get resolution
            MSrv_Control::GetMSrvFactoryMode()->GetResolutionMapping((U8*)&eHDMIRes, eInputType);
            //get VideoARCType
            MSrv_Control::GetMSrvSystemDatabase()->GetVideoArc(&m_eVideoARCType, &eInputType);

            if(eOSCommand == E_OVERSCAN_SAVE)
            {
                MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->SetOverscanSetting(m_VideoWinInfo, eHDMIRes, m_eVideoARCType, eInputType);
                printf("HDMI Overscan saved!\n");
                return;
            }
            else
            {
                //get HDMI Overscan Setting
                printf("going to GetHDMIOverscanSetting\n");
                MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->GetOverscanSetting(m_VideoWinInfo, eHDMIRes, m_eVideoARCType, eInputType);
            }
            printf("Current Resolution: %d\n", MSrv_Control::GetInstance()->GetCurrentInputSource());
            printf("HDMI Resolution: %d , ARC_TYPE: %d\n", eHDMIRes, m_eVideoARCType);
            printf("Left: %d , Right: %d , Top: %d , Bottom: %d\n", m_VideoWinInfo->u8HCrop_Left, m_VideoWinInfo->u8HCrop_Right, m_VideoWinInfo->u8VCrop_Up, m_VideoWinInfo->u8VCrop_Down);
        }
        break;

        default:
            //VGA
            printf("No need to overscan!\n");
            break;
    }
}
#endif
void MSrv_Factory_Mode::GetResolutionMapping(U8* peIndex, MAPI_INPUT_SOURCE_TYPE enCurrentInputType)
{
    switch(enCurrentInputType)
    {
        case MAPI_INPUT_SOURCE_DTV:     //dtv
        {
            printf("MSrv_Factory_Mode::GetResolutionMapping, case DTV\n");
            MW_DTV_AVMonitor::ResolutionRemappingWrapper(peIndex);
        }
        break;

        case MAPI_INPUT_SOURCE_YPBPR:       //comp
        case MAPI_INPUT_SOURCE_YPBPR2:
        case MAPI_INPUT_SOURCE_YPBPR3:
        {
            mapi_video_datatype::MAX_YPbPr_Resolution_Info enRes = mapi_video_datatype::E_YPbPr480i_60;
            mapi_interface::Get_mapi_video(enCurrentInputType)->GetYPbPrResolutionRemapping((U8*) &enRes);
            *peIndex = enRes;
        }
        break;

        case MAPI_INPUT_SOURCE_HDMI:    //hdmi
        case MAPI_INPUT_SOURCE_HDMI2:
        case MAPI_INPUT_SOURCE_HDMI3:
        case MAPI_INPUT_SOURCE_HDMI4:
        case MAPI_INPUT_SOURCE_DVI:
        case MAPI_INPUT_SOURCE_DVI2:
        case MAPI_INPUT_SOURCE_DVI3:
        case MAPI_INPUT_SOURCE_DVI4:
        {
            mapi_video_datatype::MAX_HDMI_Resolution_Info enRes = mapi_video_datatype::E_HDMI480i_60;
            mapi_interface::Get_mapi_video(enCurrentInputType)->GetHDMIResolutionRemapping((U8*) &enRes);
            *peIndex = enRes;
        }
        break;

        default:
            peIndex = NULL;
            break;
    }

    return;
}

BOOL MSrv_Factory_Mode::GetResolutionInfo(U8* ResolutionInfo, MAPI_INPUT_SOURCE_TYPE enCurrentInputType)
{
    BOOL bRet = TRUE;
    switch(enCurrentInputType)
    {
        case MAPI_INPUT_SOURCE_YPBPR:       //comp
        case MAPI_INPUT_SOURCE_YPBPR2:
        case MAPI_INPUT_SOURCE_YPBPR3:
        {
            mapi_video_datatype::MAX_YPbPr_Resolution_Info enRes = mapi_video_datatype::E_YPbPr480i_60;
            mapi_interface::Get_mapi_video(enCurrentInputType)->GetYPbPrResolutionRemapping((U8*) &enRes);
            *ResolutionInfo = enRes;
        }
        break;

        case MAPI_INPUT_SOURCE_HDMI:    //hdmi
        case MAPI_INPUT_SOURCE_HDMI2:
        case MAPI_INPUT_SOURCE_HDMI3:
        case MAPI_INPUT_SOURCE_HDMI4:
        case MAPI_INPUT_SOURCE_DVI:
        case MAPI_INPUT_SOURCE_DVI2:
        case MAPI_INPUT_SOURCE_DVI3:
        case MAPI_INPUT_SOURCE_DVI4:
        {
            mapi_video_datatype::MAX_HDMI_Resolution_Info enRes = mapi_video_datatype::E_HDMI480i_60;
            mapi_interface::Get_mapi_video(enCurrentInputType)->GetHDMIResolutionRemapping((U8*) &enRes);
            *ResolutionInfo = enRes;
        }
        break;

        default:
            printf("Input source type not match, can not get resolution Info!!!");
            bRet = FALSE;
            break;
    }

    return bRet;
}

void MSrv_Factory_Mode::SetWindowWrapper(void)
{
#if (STB_ENABLE == 0)
    BOOL callSetWindow = TRUE;
    MAPI_INPUT_SOURCE_TYPE eInputSrc = MSrv_Control::GetInstance()->GetCurrentInputSource();

    MSrv_Player *player =MSrv_Control::GetInstance()->GetMSrvPlayer(eInputSrc);

    if ( IsSrcVideo(eInputSrc) )
        {
        callSetWindow = TRUE;
        }
    else if ( IsSrcHDMI(eInputSrc) )
        {
            callSetWindow = player->IsSignalStable();//no signal not need to call SetAspectRatio
        }
    else
        {
            callSetWindow = FALSE;
            printf("No need to overscan\n");
        }

    //call SetAspectRatio
    if(callSetWindow)
    {
        mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO stVideoARCInfo;
        stVideoARCInfo.s16Adj_ARC_Left = 0;
        stVideoARCInfo.s16Adj_ARC_Right = 0;
        stVideoARCInfo.s16Adj_ARC_Up = 0;
        stVideoARCInfo.s16Adj_ARC_Down = 0;
        stVideoARCInfo.bSetCusWin = MAPI_FALSE;
        MSrv_Control::GetMSrvSystemDatabase()->GetVideoArc(&stVideoARCInfo.enARCType, &eInputSrc);
        player->FinetuneOverscan(stVideoARCInfo);
        printf("SetWindowWrapper Done!!\n");
    }
#endif
}
void MSrv_Factory_Mode::SetFactoryVDInitParameter(MS_Factory_NS_VD_SET  *pFactoryParaData)
{
#if (STB_ENABLE == 0)
    mapi_interface::Get_mapi_vd()->InitNonVDPara(E_MAPI_FACTORY_PARA_AFEC_D7_HIGH_BOUND,pFactoryParaData->u8AFEC_D7_HIGH_BOUND);
    mapi_interface::Get_mapi_vd()->InitNonVDPara(E_MAPI_FACTORY_PARA_AFEC_D7_LOW_BOUND,pFactoryParaData->u8AFEC_D7_LOW_BOUND);
    if(pFactoryParaData->u8AFEC_43 == 0 )
    {
        mapi_interface::Get_mapi_vd()->InitNonVDPara(E_MAPI_FACTORY_PARA_AFEC_43, VD_FACTORY_FIX_GAIN);
        mapi_interface::Get_mapi_vd()->InitNonVDPara(E_MAPI_FACTORY_PARA_AFEC_44,pFactoryParaData->u8AFEC_44);
    }
    else
    {
        mapi_interface::Get_mapi_vd()->InitNonVDPara(E_MAPI_FACTORY_PARA_AFEC_43, VD_FACTORY_AUTO_GAIN);
    }
#endif
}
void MSrv_Factory_Mode::SetFactoryVDParameter(MS_Factory_NS_VD_SET  *pFactoryParaData)
{
#if (STB_ENABLE == 0)
    MAPI_INPUT_SOURCE_TYPE enSrcType;
    enSrcType = MSrv_Control::GetInstance()->GetCurrentInputSource();

    mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_D4,pFactoryParaData->u8AFEC_D4);
    mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_D8,pFactoryParaData->u8AFEC_D8_Bit3210);
    mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_D5_BIT2,pFactoryParaData->u8AFEC_D5_Bit2);
    mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_D9_BIT0,pFactoryParaData->u8AFEC_D9_Bit0);
    mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_A0, pFactoryParaData->u8AFEC_A0);
    mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_A1, pFactoryParaData->u8AFEC_A1);
    mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_66_BIT67,pFactoryParaData->u8AFEC_66_Bit76);
    mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_6E_BIT7654,pFactoryParaData->u8AFEC_6E_Bit7654);
    mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_6E_BIT3210,pFactoryParaData->u8AFEC_6E_Bit3210);
    if(pFactoryParaData->u8AFEC_43 == 0 )
    {
        mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_43, VD_FACTORY_FIX_GAIN);
        mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_44,pFactoryParaData->u8AFEC_44);
    }
    else
    {
        mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_43, VD_FACTORY_AUTO_GAIN);
    }

    mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_CB,pFactoryParaData->u8AFEC_CB);

    if(enSrcType == MAPI_INPUT_SOURCE_ATV)
    {
        mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_CF_BIT2,pFactoryParaData->u8AFEC_CF_Bit2_ATV);
        m_u8AFEC_CFbit2_BK_ATV = pFactoryParaData->u8AFEC_CF_Bit2_ATV;
    }
    else
    {
        mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_CF_BIT2,pFactoryParaData->u8AFEC_CF_Bit2_AV);
        m_u8AFEC_CFbit2_BK_AV = pFactoryParaData->u8AFEC_CF_Bit2_AV;
    }
#endif
}

void MSrv_Factory_Mode::SetFactoryVDParameterAFECCF(void)
{
#if (STB_ENABLE == 0)
    MAPI_INPUT_SOURCE_TYPE enSrcType;
    enSrcType = MSrv_Control::GetInstance()->GetCurrentInputSource();

    if(enSrcType == MAPI_INPUT_SOURCE_ATV)
    {
        mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_CF_BIT2,m_u8AFEC_CFbit2_BK_ATV);
    }
    else
    {
        mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_CF_BIT2,m_u8AFEC_CFbit2_BK_AV);
    }
#endif
}

void MSrv_Factory_Mode::SetFactoryAVDRegValue(MAPI_AVD_FactoryPara  enParaReg, U8 u8value)
{
#if (STB_ENABLE == 0)
    mapi_interface::Get_mapi_vd()->SetFactoryPara(enParaReg,u8value);
#endif
}

void MSrv_Factory_Mode::SetPQParameterViaUsbKey(void)
{
#if (STB_ENABLE == 0)
    BOOL bUpdateStatus=FALSE;
    bUpdateStatus = MSrv_Control::GetInstance()->UpdatePQParameterViaUsbKey();
    PostEvent(0, EV_PQ_SETTING_VIA_USB_UPDATE, (U32)bUpdateStatus);
#endif
}

U32 MSrv_Factory_Mode::UpdateHDCPKeyViaUsbKey(void)
{
    char strCommand[128];
    U32 ret = UPDATE_KEY_FAIL;

    ///Check is there USB
    vector < DISK_INFO > drives_info = MSrv_MountNotifier::GetInstance()->GetMountedList();
    U32 TotalDriveNum = drives_info.size();
    if(0 == TotalDriveNum)
    {
        ret = UPDATE_KEY_NO_USB;
        return ret;
    }
    // mount USB
    snprintf(strCommand, 128, "mkdir %s", "/Customer/UpdateHDCPKeySetting");
    SystemCmd(strCommand);
    snprintf(strCommand, 128, "mount /dev/sda1 %s", "/Customer/UpdateHDCPKeySetting");
    if(SystemCmd(strCommand) !=0)
    {
        snprintf(strCommand, 128, "umount %s", "/Customer/UpdateHDCPKeySetting");
        SystemCmd(strCommand);
        snprintf(strCommand, 128, "rm -rf %s", "/Customer/UpdateHDCPKeySetting");
        SystemCmd(strCommand);
        ret = UPDATE_KEY_MOUNT_FAIL;
        return ret;
    }
    // Update HDCP Key
    //snprintf(strCommand, 128, "cp -vf %s %s", "/Customer/UpdateHDCPKeySetting/hdcp_key.bin", "/certificate/hdcp_key.bin");
    snprintf(strCommand, 128, "mv `ls /Customer/UpdateHDCPKeySetting/%s* |sort |awk '{if(NR==1) printf $1}'` %s", HDCP_KEY_NAME,"/certificate/hdcp_key.bin");
    if(SystemCmd(strCommand) !=0)
    {
        printf("hdcp_key.bin is empty!!\n");
        ret = UPDATE_KEY_EMPTY;
        snprintf(strCommand, 128, "umount %s", "/Customer/UpdateHDCPKeySetting");
        SystemCmd(strCommand);
        snprintf(strCommand, 128, "rm -rf %s", "/Customer/UpdateHDCPKeySetting");
        SystemCmd(strCommand);
        return ret;
    }
    //snprintf(strCommand, 128, "rm -rf %s", "/Customer/UpdateHDCPKeySetting/hdcp_key.bin");
    //SystemCmd(strCommand);
    sync();
    usleep(100*1000);
    snprintf(strCommand, 128, "umount %s", "/Customer/UpdateHDCPKeySetting");
    SystemCmd(strCommand);
    snprintf(strCommand, 128, "rm -rf %s", "/Customer/UpdateHDCPKeySetting");
    SystemCmd(strCommand);
    ret = UPDATE_KEY_SUSECCE;

    return ret;

}

void MSrv_Factory_Mode::SetHDCPKeyViaUsbKey(void)
{
#if (STB_ENABLE == 0)
    U32 bUpdateStatus = UPDATE_KEY_FAIL;
    bUpdateStatus = UpdateHDCPKeyViaUsbKey();
    PostEvent(0, EV_HDCP_KEY_VIA_USB_UPDATE, (U32)bUpdateStatus);
#endif
}

U32 MSrv_Factory_Mode::UpdateCIPlusKeyViaUsbKey(void)
{

    char strCommand[128];
    U32 ret = UPDATE_KEY_FAIL;

    ///Check is there USB?

    vector < DISK_INFO > drives_info = MSrv_MountNotifier::GetInstance()->GetMountedList();
    U32 TotalDriveNum = drives_info.size();
    if(0 == TotalDriveNum)
    {
        ret = UPDATE_KEY_NO_USB;
        return ret;
    }
    // mount USB
    printf("----------------[SystemInfo] Set CIPlus Key - Mount USB Key ------------- \n");
    snprintf(strCommand, 128, "mkdir %s", "/Customer/UpdateCIPlusKeySetting");
    SystemCmd(strCommand);
    snprintf(strCommand, 128, "mount /dev/sda1 %s", "/Customer/UpdateCIPlusKeySetting");
    if(SystemCmd(strCommand) !=0)
    {
        snprintf(strCommand, 128, "umount %s", "/Customer/UpdateCIPlusKeySetting");
        SystemCmd(strCommand);
        snprintf(strCommand, 128, "rm -rf %s", "/Customer/UpdateCIPlusKeySetting");
        SystemCmd(strCommand);
        ret = UPDATE_KEY_MOUNT_FAIL;
        return ret;
    }
    // Update CIPlus Key
    printf("[SystemInfo] Set CIPlus Key - Update ci_plus.bin\n");
    //snprintf(strCommand, 128, "cp -vf %s %s", "/Customer/UpdateCIPlusKeySetting/ci_plus.bin", "/certificate/ci_plus.bin");
    snprintf(strCommand, 128, "mv `ls /Customer/UpdateCIPlusKeySetting/%s* |sort |awk '{if(NR==1) printf $1}'` %s", CI_PLUS_NAME,"/certificate/ci_plus.bin");
    if(SystemCmd(strCommand) !=0)
    {
        printf("ci_plus.bin is empty!!\n");
        ret = UPDATE_KEY_EMPTY;
        snprintf(strCommand, 128, "umount %s", "/Customer/UpdateCIPlusKeySetting");
        SystemCmd(strCommand);
        snprintf(strCommand, 128, "rm -rf %s", "/Customer/UpdateCIPlusKeySetting");
        SystemCmd(strCommand);
        return ret;
    }
    //snprintf(strCommand, 128, "rm -rf %s", "/Customer/UpdateCIPlusKeySetting/ci_plus.bin");
    //SystemCmd(strCommand);
    sync();
    usleep(100*1000);
    snprintf(strCommand, 128, "umount %s", "/Customer/UpdateCIPlusKeySetting");
    SystemCmd(strCommand);
    snprintf(strCommand, 128, "rm -rf %s", "/Customer/UpdateCIPlusKeySetting");
    SystemCmd(strCommand);
    ret = UPDATE_KEY_SUSECCE;

    return ret;

}

void MSrv_Factory_Mode::SetCIPlusKeyViaUsbKey(void)
{
#if (STB_ENABLE == 0)
    U32 bUpdateStatus = UPDATE_KEY_FAIL;
    bUpdateStatus = UpdateCIPlusKeyViaUsbKey();
    PostEvent(0, EV_CIPLUS_KEY_VIA_USB_UPDATE, (U32)bUpdateStatus);
#endif
}

U8 MSrv_Factory_Mode::GetAVDGainValue(void)
{
#if (STB_ENABLE == 0)
    U8 u8FineGain = 0;
    u8FineGain = mapi_interface::Get_mapi_vd()->GetDSPFineGain();

    return u8FineGain;
#else
    return 0;
#endif
}

U8 MSrv_Factory_Mode::GetAVDGainForRef(void)
{
#if (STB_ENABLE == 0)
    U8 u8FineGain = 0;
    SetAVDGainAutoTune(TRUE);
    usleep(400000); // waiting for AVD DSP Ready; recommand by AVD Driver Team
    u8FineGain = GetAVDGainValue();
    usleep(10000);  // recommand by AVD Driver Team
    SetAVDGainAutoTune(FALSE);

    return u8FineGain;
#else
    return 0;
#endif
}

U16 MSrv_Factory_Mode::GetAVDDSPVersion(void)
{
#if (STB_ENABLE == 0)
    return mapi_interface::Get_mapi_vd()->GetDSPVersion();
#else
    return 0;
#endif
}

void MSrv_Factory_Mode::SetAVDGainAutoTune(BOOL bEnable)
{
#if (STB_ENABLE == 0)
    U8 u8GainType = 0;

    u8GainType = ((bEnable==TRUE)? VD_FACTORY_AUTO_GAIN : VD_FACTORY_FIX_GAIN);

    mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_43,u8GainType);
#endif
}

BOOL MSrv_Factory_Mode::SetAVDPLLTrackType(EN_AVD_PLL_TRACK_TYPE enPLLTrackType)
{
    BOOL bRet = TRUE;
#if (STB_ENABLE == 0)
    mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_D5_BIT2,VD_FACTORY_CHINA_RF_DPL_OFF);
    mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_A0,VD_FACTORY_FORCE_DPL_K1_OFF);
    mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_A1,VD_FACTORY_FORCE_DPL_K2_OFF);

    switch(enPLLTrackType)
    {
        case AVD_PLL_TRACK_SPECIAL:
            mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_D5_BIT2,VD_FACTORY_CHINA_RF_DPL_ON);
            break;
        case AVD_PLL_TRACK_LO:
            mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_A0,VD_FACTORY_FORCE_DPL_K1_LO);
            mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_A1,VD_FACTORY_FORCE_DPL_K2_LO);
            break;
        case AVD_PLL_TRACK_MID_LO:
            mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_A0,VD_FACTORY_FORCE_DPL_K1_MID_LO);
            mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_A1,VD_FACTORY_FORCE_DPL_K2_MID_LO);
            break;
        case AVD_PLL_TRACK_MID_HI:
            mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_A0,VD_FACTORY_FORCE_DPL_K1_MID_HI);
            mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_A1,VD_FACTORY_FORCE_DPL_K2_MID_HI);
            break;
        case AVD_PLL_TRACK_HI:
            mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_A0,VD_FACTORY_FORCE_DPL_K1_HI);
            mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_A1,VD_FACTORY_FORCE_DPL_K2_HI);
            break;
        case AVD_PLL_TRACK_OFF: // VD set PLL tracking speed by itself
            break;
        default:
            bRet = FALSE;
            break;
    }
#endif
    return bRet;
}

BOOL MSrv_Factory_Mode::SetAVDForceSliceType(EN_AVD_FORCE_SLICE_TYPE enSliceType)
{
    BOOL bRet = TRUE;
#if (STB_ENABLE == 0)
    switch(enSliceType)
    {
        case AVD_FORCE_SLICE_H:
            mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_66_BIT67,VD_FACTORY_FORCE_H_SLICE_LEVEL);
            break;
        case AVD_FORCE_SLICE_V:
            mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_66_BIT67,VD_FACTORY_FORCE_V_SLICE_LEVEL);
            break;
        case AVD_FORCE_SLICE_HV:
            mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_66_BIT67,VD_FACTORY_FORCE_HV_SLICE_LEVEL);
            break;
        case AVD_FORCE_SLICE_OFF:
            mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_66_BIT67,VD_FACTORY_FORCE_SLICE_LEVEL_OFF);
            break;
        default:
            bRet = FALSE;
            break;
    }
#endif
    return bRet;
}

void MSrv_Factory_Mode::SetAVDHForceSliceLevel(U8 u8HSliceLevel)
{
#if (STB_ENABLE == 0)
    mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_6E_BIT3210,(u8HSliceLevel&0x0f));
#endif
}

void MSrv_Factory_Mode::SetAVDVForceSliceLevel(U8 u8VSliceLevel)
{
#if (STB_ENABLE == 0)
    mapi_interface::Get_mapi_vd()->SetFactoryPara(E_MAPI_FACTORY_PARA_AFEC_6E_BIT7654,((u8VSliceLevel&0x0f)<<4));
#endif
}
U32 MSrv_Factory_Mode::UpdateMACAddrViaUsbKey(MAPI_BOOL bErase)
{
    char strCommand[128];
    U32 ret = UPDATE_KEY_FAIL;
    FILE *fp = NULL;
    U8 filebuffer[14] = {0};
    ///Check is there USB?
    vector < DISK_INFO > drives_info = MSrv_MountNotifier::GetInstance()->GetMountedList();
    U32 TotalDriveNum = drives_info.size();
    if(0 == TotalDriveNum)
    {
        ret = UPDATE_KEY_NO_USB;
        return ret;
    }
    // mount USB
    printf("----------------[SystemInfo] Set MAC ADDR Key - Mount USB Key ------------- \n");
    snprintf(strCommand, 128, "mkdir %s", "/Customer/UpdateMACAddrSetting");
    SystemCmd(strCommand);
    snprintf(strCommand, 128, "mount /dev/sda1 %s", "/Customer/UpdateMACAddrSetting");
    if(SystemCmd(strCommand) !=0)
    {
        snprintf(strCommand, 128, "umount %s", "/Customer/UpdateMACAddrSetting");
        SystemCmd(strCommand);
        snprintf(strCommand, 128, "rm -rf %s", "/Customer/UpdateMACAddrSetting");
        ret = UPDATE_KEY_MOUNT_FAIL;
        return ret;
    }

    /*
    //Search one file
    snprintf(strCommand, 128, "rm -f %s",MAC_ADDR_PATH_NAME);
    SystemCmd(strCommand);
    snprintf(strCommand, 128, "mv `ls /Customer/UpdateMACAddrSetting/%s?* |sort |awk '{if(NR==1) printf $1}'` %s", MAC_ADDR_NAME, MAC_ADDR_PATH_NAME);
    if(SystemCmd(strCommand) !=0)
    {
        printf("MAC.bin is empty!!\n");

        snprintf(strCommand, 128, "umount %s", "/Customer/UpdateMACAddrSetting");
        SystemCmd(strCommand);
        snprintf(strCommand, 128, "rm -rf %s", "/Customer/UpdateMACAddrSetting");

        ret = UPDATE_KEY_EMPTY;

        return ret;
    }
    */
    // Update Mac Address
    printf("[SystemInfo] Set MAC Address - Update MAC.bin\n");

    MAPI_U64 u64MacDate = 0;
    MAPI_U64 u64MaxMacDate = 0;
    MAPI_U64 u64TempMacDate = 0;
    int length,i,j;

    fp = fopen(MAC_ADDR_PATH_NAME, "rb+");

    if(fp == NULL)
    {
        printf("\033[32m Not find %s,Please check it!!!\033[0m\n", MAC_ADDR_PATH_NAME);
        //ret = E_SEARCH_MAC_ERROR;
        ret = UPDATE_KEY_EMPTY;

        snprintf(strCommand, 128, "umount %s", "/Customer/UpdateMACAddrSetting");
        SystemCmd(strCommand);
        snprintf(strCommand, 128, "rm -rf %s", "/Customer/UpdateMACAddrSetting");
        SystemCmd(strCommand);
        return ret;
    }
    else
    {
        printf("open ------------>%s!\n", MAC_ADDR_PATH_NAME);
    }

    fread(filebuffer, 14, 1, fp);
    printf(">>read mac addr is:%x,%x,%x,%x,%x,%x\n",filebuffer[0],filebuffer[1],filebuffer[2],filebuffer[3],filebuffer[4],filebuffer[5]);

    for(i=0;i<6;i++)
    {
        u64TempMacDate=filebuffer[i];
        j=(5-i)*8;
        u64TempMacDate=u64TempMacDate<<j;
        u64MacDate += u64TempMacDate;
    }

    for(i=6;i<12;i++)
    {
        u64TempMacDate=filebuffer[i];
        j=(11-i)*8;
        u64TempMacDate=u64TempMacDate<<j;
        u64MaxMacDate+=u64TempMacDate;
    }

    if(u64MacDate > u64MaxMacDate)
    {
        printf("\033[32mThe MacAddr has raised to the max Addr!Please check it!!!\033[0m\n");
        ret = E_USED_MAC_FINISHED;
        goto exit;
    }

    u64MacAddr = u64MacDate;

#if(ENABLE_MAC_IN_EEP == 1)
    printf("\033[33m\033[5mENABLE_MAC_IN_EEP == 1\033[0m\n");
    //mapi_interface::Get_mapi_eeprom()->SetDeviceEEPSlaveAddr(EEPEOM_I2C_SLAVE_MACADRR);
    if(mapi_interface::Get_mapi_eeprom()->Write(EEPEOM_MACADRR_ADRR_START, (MAPI_U8 *)filebuffer, MACADRR_DATA_LEN) == MAPI_FALSE)
    {
        printf("write mac date to eeprom error!\n");
        //ret = E_WRITE_MAC_TO_MEMORY_ERROR;
        ret = UPDATE_KEY_FAIL;
        goto exit;
    }
#else
    //Imapi_storage_factory_config * pfactoryFlash = Imapi_storage_factory_config::GetInstance();
    if(bErase == MAPI_TRUE)
    {
        //U8 buffer[SYSTEM_BANK_SIZE];
        U8* buffer =NULL;
        buffer = (U8*)malloc(SYSTEM_BANK_SIZE*sizeof(U8));
        if(buffer==NULL)
        {
            fclose(fp);
            ASSERT(0);
        }
        Imapi_storage_factory_config::GetInstance()->Read(FLASH_BANK_MACADRR, 0x00, (MAPI_U32)buffer, SYSTEM_BANK_SIZE);
        memcpy(buffer + FLASH_MACADRR_ADRR_START, filebuffer, MACADRR_DATA_LEN);
        if(Imapi_storage_factory_config::GetInstance()->Write(FLASH_BANK_MACADRR, 0x00, (MAPI_U32)buffer, SYSTEM_BANK_SIZE, bErase) == MAPI_FALSE)
        {
            printf("write mac date to flash error!\n");
            //ret = E_WRITE_MAC_TO_MEMORY_ERROR;
            ret = UPDATE_KEY_FAIL;
            free(buffer);
            buffer=NULL;
            goto exit;
        }
        else
        {
            Imapi_storage_factory_config::GetInstance()->Read(FLASH_BANK_MACADRR,FLASH_MACADRR_ADRR_START , (MAPI_U32)buffer, MACADRR_DATA_LEN);
            buffer[MACADRR_DATA_LEN-1] = '\0';
            printf("\033[33mWrite MacAddr with erasing success:-->[%.2x:%.2x:%.2x:%.2x:%.2x:%.2x]\033[0m\n",buffer[0],buffer[1],buffer[2],buffer[3],buffer[4],buffer[5]);
        }
        free(buffer);
        buffer=NULL;
    }
    else
    {
        if(Imapi_storage_factory_config::GetInstance()->Write(FLASH_BANK_MACADRR, FLASH_MACADRR_ADRR_START, (MAPI_U32)filebuffer, MACADRR_DATA_LEN, bErase) == MAPI_FALSE)
        {
            printf("write mac date to flash error!\n");
            //ret = E_WRITE_MAC_TO_MEMORY_ERROR;
            ret = UPDATE_KEY_FAIL;
            goto exit;
        }
      else
      {
        U8* buffer =NULL;
        buffer = (U8*)malloc(SYSTEM_BANK_SIZE*sizeof(U8));
        if(buffer==NULL)
        {
            fclose(fp);
            ASSERT(0);
        }
        //U8 buffer[MACADRR_DATA_LEN];
        Imapi_storage_factory_config::GetInstance()->Read(FLASH_BANK_MACADRR,FLASH_MACADRR_ADRR_START , (MAPI_U32)buffer, MACADRR_DATA_LEN);
        buffer[MACADRR_DATA_LEN-1] = '\0';
        printf("\033[33mWrite MacAddr success:-->[%.2x:%.2x:%.2x:%.2x:%.2x:%.2x]\033[0m\n",buffer[0],buffer[1],buffer[2],buffer[3],buffer[4],buffer[5]);
        free(buffer);
        buffer=NULL;

      }
    }
#endif

    ret = UPDATE_KEY_SUSECCE;

    fseek(fp, 0, SEEK_SET);
    u64MacDate++;
    for(i=0;i<6;i++)
    {
      j=(5-i)*8;
      filebuffer[i]=(u64MacDate>>j);
    }
    printf(">>write data into mac.bin in usb:%.2x:%.2x:%.2x:%.2x:%.2x:%.2x\n",filebuffer[0],filebuffer[1],filebuffer[2],
                   filebuffer[3],filebuffer[4],filebuffer[5]);
    length = fwrite(filebuffer, 8, 1, fp);
    if(length != 1)
    {
        printf("write %s error!\n",MAC_ADDR_PATH_NAME);
        ret = E_POINT_TO_NEXT_MAC_ERROR;
    }
    else
    {
        printf("write %s success!\n",MAC_ADDR_PATH_NAME);
        fflush(fp);
        int fd = fileno(fp);
        if(fsync(fd) != 0)
        {
            printf("sync %s error!\n",MAC_ADDR_PATH_NAME);
            ret = E_POINT_TO_NEXT_MAC_ERROR;
        }
        else
        {
            MAPI_U64 u64MacDateTest = 0;
            printf("sync %s success!\n",MAC_ADDR_PATH_NAME);
            fseek(fp, 0, SEEK_SET);
            fread(filebuffer, MACADRR_DATA_LEN, 1, fp);
            printf(">>read mac addr again:%x,%x,%x,%x,%x,%x\n",filebuffer[0],filebuffer[1],filebuffer[2],filebuffer[3],filebuffer[4],filebuffer[5]);
            u64MacDateTest = 0;
            for(i=0;i<6;i++)
            {
                u64TempMacDate=filebuffer[i];
                j=(5-i)*8;
                u64TempMacDate=u64TempMacDate<<j;
                u64MacDateTest+=u64TempMacDate;
            }
            if(u64MacDateTest == u64MacDate)
            {
              ret = E_UPGRADE_OK;
            }
            else
            {
                ret = E_POINT_TO_NEXT_MAC_ERROR;
            }
        }
    }

exit:
    fclose(fp);

    snprintf(strCommand, 128, "umount %s", "/Customer/UpdateMACAddrSetting");
    SystemCmd(strCommand);
    snprintf(strCommand, 128, "rm -rf %s", "/Customer/UpdateMACAddrSetting");
    SystemCmd(strCommand);

    return ret;
}

#if (MSTAR_TVOS == 1)
void MSrv_Factory_Mode::RestoreFactoryAtvProgramTable(U8 u8CityIndex)
{
#if (STB_ENABLE == 0)
    U32 u32Number = 0;

    if(MSrv_Control::GetInstance()->GetCurrentInputSource() != MAPI_INPUT_SOURCE_ATV)
    {
        MSrv_Control::GetInstance()->SetInputSource(MAPI_INPUT_SOURCE_ATV);
        usleep(10000);
    }

    MSrv_Control::GetInstance()->GetMSrvAtvDatabase()->SetProgramCtrl(RESET_CHANNEL_DATA, 0, 0,NULL);

    U8 u8ProgramCount = 0;
    MSrv_Control::GetMSrvSystemDatabase()->GetDefaultAtvProgramCounts(&u8ProgramCount,u8CityIndex);


    MS_FACTORY_DEFAULT_ATV_SETTING stAtvDefaultProgram;

    for(int i = 0; i < u8ProgramCount; i++)
    {
        MSrv_Control::GetMSrvSystemDatabase()->GetDefaultAtvProgram(&stAtvDefaultProgram, u8CityIndex, i);

        MSrv_Control::GetMSrvAtv()->ResetFactoryAtvProgramData(stAtvDefaultProgram.u8AtvProgramIndex, stAtvDefaultProgram.u32FrequencyKHz, stAtvDefaultProgram.u8AudioStandard, stAtvDefaultProgram.u8VideoStandard);
    }

    MSrv_Control_common::GetMSrvChannelManager()->GetATVProgramCount(&u32Number);

    if(u32Number > 0)
    {
        MSrv_Control_common::GetMSrvChannelManager()->ProgramSel((U32)0, E_SERVICETYPE_ATV);
    }
#endif
}


void MSrv_Factory_Mode::RestoreFactoryDtvProgramTable(U8 u8CityIndex)
{
    U32 u32Number = 0;
    U16 u16FirstServiceNumber = 0;
    U8 i = 0;

    if(MSrv_Control::GetInstance()->GetCurrentInputSource() != MAPI_INPUT_SOURCE_DTV)
    {
        MSrv_Control::GetInstance()->SetInputSource(MAPI_INPUT_SOURCE_DTV);
        usleep(10000);
    }

    while(i < MAXROUTECOUNT)
    {
        if(MSrv_Control::GetInstance()->GetRouteTVMode(i) == E_ROUTE_DVBC)
        {
            MSrv_Control::GetInstance()->SwitchMSrvDtvRoute(i);
            usleep(10000);
            break;
        }

        i++;

        if(i >= MAXROUTECOUNT)
        {
            return;
        }
    }

    MSrv_Control::GetMSrvDtv()->ProgramDbReset(FALSE);
    usleep(500*1000);

    U8 u8ProgramCount = 0;
    MSrv_Control::GetMSrvSystemDatabase()->GetDefaultDtvProgramCounts(&u8ProgramCount,u8CityIndex);

    MS_FACTORY_DEFAULT_DTV_SETTING stDtvDefaultProgram;

    for(int j = 0; j < u8ProgramCount; j++)
    {
        MSrv_Control::GetMSrvSystemDatabase()->GetDefaultDtvProgram(&stDtvDefaultProgram, u8CityIndex, j);

        if(stDtvDefaultProgram.u8DtvProgramIndex == 0)
        {
            u16FirstServiceNumber = stDtvDefaultProgram.u16ServiceID;
            printf("\nu16FirstServiceNumber %d\n",u16FirstServiceNumber);
        }

        MSrv_Control::GetMSrvDtv()->SetFactoryDtvProgramData(&stDtvDefaultProgram);
    }

    MSrv_Control_common::GetMSrvChannelManager()->GetDTVProgramCount(&u32Number);
    if(u32Number > 0)
    {
        MSrv_Control_common::GetMSrvChannelManager()->ProgramSel(u16FirstServiceNumber, E_SERVICETYPE_DTV);
        MSrv_Control::GetMSrvDtv()->PlayCurrentProgram();
    }
}
void MSrv_Factory_Mode::RestoreFactoryDtvProgramTable(U8 u8CityIndex, U8 u8dtvRouteMode)
{
    // TODO:
}
#endif

void MSrv_Factory_Mode::GetMACAddrString(string &MacString)
{
    U8 macbuf[6];
    U8 i,j;
    char buf[18];

    for(i=0;i<6;i++)
    {
      j=(5-i)*8;
      macbuf[i]=(u64MacAddr>>j);
    }

    snprintf(buf, 18, "%.2x:%.2x:%.2x:%.2x:%.2x:%.2x",macbuf[0],macbuf[1],macbuf[2],macbuf[3],macbuf[4],macbuf[5]);
    //macbuf[17] = '\0';
    //printf("mac:%s\n",buf);
    //memcpy(MacString, buf, 18);
    MacString = buf;

    return;

}

void MSrv_Factory_Mode::SetMACAddrViaUsbKey(void)
{
    U32 bUpdateStatus = UPDATE_KEY_FAIL;
    bUpdateStatus = UpdateMACAddrViaUsbKey(TRUE);
    PostEvent(0, EV_MACAddr_VIA_USB_UPDATE, (U32)bUpdateStatus);
}

BOOL MSrv_Factory_Mode::GetUrsaVersionInfo(U16*u16Version, U32 *u32Changelist)
{
    BOOL ret = FALSE;
#if (ENABLE_BACKEND == 1)
    mapi_ursa *pUrsa = mapi_interface::Get_mapi_pcb()->GetUrsa(0);
    if(NULL != pUrsa)
    {
        *u16Version = pUrsa->GetVersion();
        *u32Changelist = pUrsa->GetUrsaVersionInfo();
        ret = TRUE;
    }
#else
    *u16Version = 0xFF;
    *u32Changelist = 0xFFFF;
#endif
    return ret;
}

#if (WOL_ENABLE == 1)
void MSrv_Factory_Mode::SetWOLEnableStatus(bool flag)
{
    MS_USER_SYSTEM_SETTING stGetSystemSetting;
    MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->GetUserSystemSetting(&stGetSystemSetting);

    stGetSystemSetting.bWOLEnable = flag;
    MSrv_Control::GetMSrvSystemDatabase()->SetUserSystemSetting(&stGetSystemSetting);

}

BOOL MSrv_Factory_Mode::GetWOLEnableStatus()
{
    MS_USER_SYSTEM_SETTING stGetSystemSetting;
    MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->GetUserSystemSetting(&stGetSystemSetting);
    return stGetSystemSetting.bWOLEnable;
}
#endif

#if (STR_ENABLE == 1)
U32 MSrv_Factory_Mode::GetStrPowerMode()
{
    MS_USER_SYSTEM_SETTING stGetSystemSetting;
    MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->GetUserSystemSetting(&stGetSystemSetting);
    return stGetSystemSetting.u32StrPowerMode;
}
#endif

U8 MSrv_Factory_Mode::GetAutoFineGain()
{
    return 0;
}

bool MSrv_Factory_Mode::SetFixedFineGain(U8 fineGain)
{
    return true;
}

U8 MSrv_Factory_Mode::GetAutoRFGain()
{
    return 0;
}

bool MSrv_Factory_Mode::SetRFGain(U8 rfGain)
{
    return true;
}

// EosTek Patch Begin
#define HDCP_KEY_SIZE   289
#define HDCP_KEY_FLAGNAME   "HKEY"
#define EOS_HDCP_KEY_PATH   "/Customer/hdcp_key.bin"

#if (HDMI_HDCP22_ENABLE == 1)
#define HDCP2_KEY_SIZE          1044
#define HDCP2_KEY_FLAGNAME      "H2KEY"
#define EOS_HDCP2_KEY_PATH      "/Customer/hdcp2_key.bin"
#endif

#define EOS_WB_FLAGNAME   "EOS_WB"

BOOL EosHdcpKeyWritetoFile(const char *filename, const U8 *u8Key, U16 u32Key_len)
{
    BOOL bRet = MAPI_FALSE;
    if (NULL == filename || NULL == u8Key || u32Key_len < HDCP_KEY_SIZE)
    {
        printf("EosHdcpKeyWritetoFile, param error\n");
        return bRet;
    }

    FILE *fp = fopen(filename, "w+");
    if (NULL == fp)
    {
        printf("EosHdcpKeyWritetoFile, open %s fail\n", filename);
        return bRet;
    }

    int nWriteLen = fwrite(u8Key, 1, u32Key_len, fp);
    if (nWriteLen > 1)
    {
        bRet = MAPI_TRUE;
    }

    if (NULL != fp)
    {
        fclose(fp);
    }
    return bRet;
}

BOOL Eos_TransHexToStr(const U8 *input, U16 input_len, char *output, U16 max_out_len)
{
	if (input == NULL || output == NULL || max_out_len <= input_len * 2)
    {
		return MAPI_FALSE;
	}

	char sTemp[4] = {0};
	memset(output, 0, max_out_len);
	for (int i = 0; i < input_len; i++) {
		sprintf(sTemp, "%02X", input[i]);
		strcat(output, sTemp);
	}

	return MAPI_TRUE;
}

BOOL Eos_TransStrToHex(const char *input, U16 input_len, U8 *output, U16 max_out_len)
{
	if (input == NULL || output == NULL || max_out_len < (input_len>>1))
    {
		return MAPI_FALSE;
	}

    U8 u8Temp[input_len + 2];
    memset(u8Temp, 0, input_len + 2);
    int i = 0;
    for (i = 0; i < input_len; i++)
    {
        if (input[i]>='0' && input[i]<='9') //change num 0~9 to Hex
        {
            u8Temp[i] = (U8)(input[i] - '0');
        }
        else
        {
            u8Temp[i] = (U8)(toupper(input[i]) - 'A' + 10); //change A~F to Hex
        }
    }

    memset(output, 0, max_out_len);
    for (i = 0; i < max_out_len; i++)
    {
        output[i] = (u8Temp[2*i]<<4)|(u8Temp[2*i+1]);
    }

	return MAPI_TRUE;
}

BOOL Eos_GetKeyInfo(BOOL bVer2, U16 *size, char *name, char *path)
{
    BOOL bRet = MAPI_FALSE;
    if (NULL == name || NULL == path)
    {
        return bRet;
    }

    if (bVer2)
    {
        #if (HDMI_HDCP22_ENABLE == 1)
        *size = HDCP2_KEY_SIZE;
        memcpy(name, HDCP2_KEY_FLAGNAME, sizeof(HDCP2_KEY_FLAGNAME));
        memcpy(path, EOS_HDCP2_KEY_PATH, sizeof(EOS_HDCP2_KEY_PATH));
        bRet = MAPI_TRUE;
        #endif
    }
    else
    {
        *size = HDCP_KEY_SIZE;
        memcpy(name, HDCP_KEY_FLAGNAME, sizeof(HDCP_KEY_FLAGNAME));
        memcpy(path, EOS_HDCP_KEY_PATH, sizeof(EOS_HDCP_KEY_PATH));
        bRet = MAPI_TRUE;
    }
    return bRet;
}

BOOL MSrv_Factory_Mode::EosSetHDCPKey(const U8 *u8Key, U16 u32Key_len, BOOL bVer2)
{
    U16 KEY_size = 0;
    char KEY_name[32] = {0};
    char KEY_path[64] = {0};
    BOOL bRet = MAPI_FALSE;

    if (!Eos_GetKeyInfo(bVer2, &KEY_size, KEY_name, KEY_path))
    {
        FACTORY_MODE_DBG("Eos_GetKeyInfo is error bVer2 = %d\n", bVer2);
        return bRet;
    }

    FACTORY_MODE_DBG("EosSetHDCPKey, Key_size = %d, name = %s, path = %s\n", KEY_size, KEY_name, KEY_path);

    if (NULL == u8Key || u32Key_len < KEY_size)
    {
        FACTORY_MODE_DBG("(bVer2 = %d) u8Key is null or len = %d\n", bVer2, u32Key_len);
        return bRet;
    }
#if 0
    for (unsigned int i = 0; i < u32Key_len; ++i)
    {
        printf("0x%02X ", u8Key[i]);
        if((i+1)%16 == 0)
		{
			printf("i = %d\n\n", i);
        }
    }
#endif

    char strKey[2*KEY_size + 2];
    memset(strKey, 0, sizeof(strKey));

    Eos_TransHexToStr(u8Key, u32Key_len, strKey, sizeof(strKey));

    IEnvManager* pEnvMan = IEnvManager::Instance();
    if(NULL != pEnvMan)
    {
        IEnvManager_scope_lock block(pEnvMan);
        bool bret1 = pEnvMan->SetEnv_Protect(KEY_name, strKey);
        bool bret2 = pEnvMan->SaveEnv();

        FACTORY_MODE_DBG("pEnvMan (%d & %d)\n", bret1, bret2);
        bRet = (bret1 && bret2);
    }

    // write to file
    if (bRet)
    {
        bRet = EosHdcpKeyWritetoFile(KEY_path, u8Key, KEY_size);
        if (bRet)
        {
            mapi_video::ReloadHdcpkey(bVer2);
        }
    }

    return bRet;
}

BOOL MSrv_Factory_Mode::EosGetHDCPKey(U8 *u8Key, U16 u32Key_len, BOOL bVer2)
{
    U16 KEY_size = 0;
    char KEY_name[32] = {0};
    char KEY_path[64] = {0};
    BOOL bRet = MAPI_FALSE;

    if (!Eos_GetKeyInfo(bVer2, &KEY_size, KEY_name, KEY_path))
    {
        FACTORY_MODE_DBG("Eos_GetKeyInfo is error bVer2 = %d\n", bVer2);
        return bRet;
    }

    FACTORY_MODE_DBG("EosGetHDCPKey, Key_size = %d, name = %s, path = %s\n", KEY_size, KEY_name, KEY_path);


    if (NULL == u8Key || u32Key_len < KEY_size)
    {
        FACTORY_MODE_DBG("u8Key is null or len = %d\n", u32Key_len);
        return bRet;
    }

    IEnvManager* pEnvMan = IEnvManager::Instance();
    if(NULL != pEnvMan)
    {
        IEnvManager_scope_lock block(pEnvMan);
        int nLen = pEnvMan->QueryLength(KEY_name);

        if (nLen < 2*KEY_size)
        {
            FACTORY_MODE_DBG("get env key length = %d\n", nLen);
            return bRet;
        }

        char strKey[nLen + 2];
        memset(strKey, 0, sizeof(strKey));
        pEnvMan->GetEnv_Protect(KEY_name, strKey, nLen);

        bRet = Eos_TransStrToHex(strKey, nLen, u8Key, u32Key_len);
    }

#if 0
    printf("HdcpKey:\n");
    for (unsigned int i = 0; i < u32Key_len; ++i)
    {
        printf("0x%02X ", u8Key[i]);
        if((i+1)%16 == 0)
		{
			printf("i = %d\n\n", i);
        }
    }
#endif

    return bRet;
}

BOOL MSrv_Factory_Mode::EosUpdateHDCPKey(BOOL bVer2)
{
    FILE *fp = NULL;
    long lSize = 0;

    BOOL bRet = MAPI_FALSE;

    if (bVer2)
    {
#if (HDMI_HDCP22_ENABLE == 1)
        if (0 == access(EOS_HDCP2_KEY_PATH, F_OK))
        {
            fp = fopen(EOS_HDCP2_KEY_PATH, "rb");
            fseek (fp , 0 , SEEK_END);
            lSize = ftell (fp);
            fclose(fp);
            if (0 != lSize)
            {
                FACTORY_MODE_DBG("EosUpdateHDCPKey, file exist [%s]\n", EOS_HDCP2_KEY_PATH);
                return MAPI_TRUE;
            }
        }

        U8 u8Key2[HDCP2_KEY_SIZE+1];
        memset(u8Key2, 0x00, HDCP2_KEY_SIZE+1);
        if (EosGetHDCPKey(u8Key2, sizeof(u8Key2), MAPI_TRUE))
        {
            bRet = EosHdcpKeyWritetoFile(EOS_HDCP2_KEY_PATH, u8Key2, HDCP2_KEY_SIZE);
        }
#else
        FACTORY_MODE_DBG("EosUpdateHDCPKey, bVer2 = %d, not define HDMI_HDCP22_ENABLE\n", bVer2);
#endif
    }
    else
    {
        if (0 == access(EOS_HDCP_KEY_PATH, F_OK))
        {
            fp = fopen(EOS_HDCP_KEY_PATH, "rb");
            fseek (fp , 0 , SEEK_END);
            lSize = ftell (fp);
            fclose(fp);
            if (0 != lSize)
            {
                FACTORY_MODE_DBG("EosUpdateHDCPKey, file exist [%s]\n", EOS_HDCP_KEY_PATH);
                return MAPI_TRUE;
            }
        }

        U8 u8Key[HDCP_KEY_SIZE+1];
        memset(u8Key, 0x00, HDCP_KEY_SIZE+1);
        if (EosGetHDCPKey(u8Key, sizeof(u8Key), MAPI_FALSE))
        {
            bRet = EosHdcpKeyWritetoFile(EOS_HDCP_KEY_PATH, u8Key, HDCP_KEY_SIZE);
        }
    }

    if (bRet)
    {
        mapi_video::ReloadHdcpkey(bVer2);
    }

    return bRet;
}

BOOL MSrv_Factory_Mode::EosSetWB(mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA warm, mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA nature, mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA cool)
{
    BOOL bRet = MAPI_FALSE;
    char strWB[80] = {0};

    sprintf(strWB, "%04X%04X%04X%04X%04X%04X%04X%04X%04X%04X%04X%04X%04X%04X%04X%04X%04X%04X", \
        warm.u16RedGain, warm.u16GreenGain, warm.u16BlueGain, warm.u16RedOffset, warm.u16GreenOffset, warm.u16BlueOffset, \
        nature.u16RedGain, nature.u16GreenGain, nature.u16BlueGain, nature.u16RedOffset, nature.u16GreenOffset, nature.u16BlueOffset, \
        cool.u16RedGain, cool.u16GreenGain, cool.u16BlueGain, cool.u16RedOffset, cool.u16GreenOffset, cool.u16BlueOffset);
    printf("strWB = %s\n", strWB);

    IEnvManager *pEnvMan = IEnvManager::Instance();
    if((NULL != pEnvMan) && pEnvMan->Initialize())
    {
        IEnvManager_scope_lock block(pEnvMan);
        bool bret1 = pEnvMan->SetEnv_Protect(EOS_WB_FLAGNAME, strWB);
        bool bret2 = pEnvMan->SaveEnv();
        printf("pEnvMan (%d & %d)\n", bret1, bret2);
        bRet = (bret1 && bret2);
    }
    return bRet;
}

BOOL MSrv_Factory_Mode::EosGetWB(mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA *warm, mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA *nature, mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA *cool)
{
    BOOL bRet = MAPI_FALSE;
    if (NULL == warm || NULL == nature || NULL == cool)
    {
        printf("EosGetWB, param is null\n");
        return bRet;
    }

    U8 u8WB[40] = {0};

    IEnvManager *pEnvMan = IEnvManager::Instance();
    if((NULL != pEnvMan) && pEnvMan->Initialize())
    {
        IEnvManager_scope_lock block(pEnvMan);
        int nLen = pEnvMan->QueryLength(EOS_WB_FLAGNAME);
        if (nLen < 72)
        {
            printf("get env key length = %d\n", nLen);
            return bRet;
        }

        char strWB[80];
        memset(strWB, 0, 80);
        pEnvMan->GetEnv_Protect(EOS_WB_FLAGNAME, strWB, nLen);
        printf("EosGetWB, strWB = %s\n", strWB);
        bRet = Eos_TransStrToHex(strWB, nLen, u8WB, 40);
    }

    if (bRet)
    {
        mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA tmpcolor;
        for(int i=0; i<3; i++)
        {
            memset(&tmpcolor, 0, sizeof(mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA));
            tmpcolor.u16RedGain = u8WB[i*12+0];
            tmpcolor.u16RedGain = (tmpcolor.u16RedGain<<8) | u8WB[i*12+1];
            tmpcolor.u16GreenGain = u8WB[i*12+2];
            tmpcolor.u16GreenGain = (tmpcolor.u16GreenGain<<8) | u8WB[i*12+3];
            tmpcolor.u16BlueGain = u8WB[i*12+4];
            tmpcolor.u16BlueGain = (tmpcolor.u16BlueGain<<8) | u8WB[i*12+5];
            tmpcolor.u16RedOffset = u8WB[i*12+6];
            tmpcolor.u16RedOffset = (tmpcolor.u16RedOffset<<8) | u8WB[i*12+7];
            tmpcolor.u16GreenOffset = u8WB[i*12+8];
            tmpcolor.u16GreenOffset = (tmpcolor.u16GreenOffset<<8) | u8WB[i*12+9];
            tmpcolor.u16BlueOffset = u8WB[i*12+10];
            tmpcolor.u16BlueOffset = (tmpcolor.u16BlueOffset<<8) | u8WB[i*12+11];

            //printf("R = %d, G = %d, B = %d\n", tmpcolor.u16RedGain, tmpcolor.u16GreenGain, tmpcolor.u16BlueGain);
            //printf("Roffset = %d, Goffset = %d, Boffset = %d\n", tmpcolor.u16RedOffset, tmpcolor.u16GreenOffset, tmpcolor.u16BlueOffset);

            switch (i)
            {
                case 0:
                    memcpy(warm, &tmpcolor, sizeof(mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA));
                    break;

                case 1:
                    memcpy(nature, &tmpcolor, sizeof(mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA));
                    break;

                case 2:
                    memcpy(cool, &tmpcolor, sizeof(mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA));
                    break;
                default:
                    break;
            }
        }

    }

    return bRet;
}

BOOL MSrv_Factory_Mode::EosUpdateWB(void)
{
    BOOL bRet = MAPI_FALSE;
    mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA warm, nature, cool;
    memset(&warm, 0, sizeof(mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA));
    memset(&nature, 0, sizeof(mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA));
    memset(&cool, 0, sizeof(mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA));

    U16 u16RedGain = 0;
    U16 u16GreenGain = 0;
    U16 u16BlueGain = 0;
    U16 u16RedOffset = 0;
    U16 u16GreenOffset = 0;
    U16 u16BlueOffset = 0;

    BOOL bWarmFlag = MAPI_FALSE;
    BOOL bNatureFlag = MAPI_FALSE;
    BOOL bCoolFlag = MAPI_FALSE;

    bRet = EosGetWB(&warm, &nature, &cool);
    if ((0 == warm.u16RedGain || 0 == warm.u16GreenGain || 0 == warm.u16BlueGain) || \
        (0 == nature.u16RedGain || 0 == nature.u16GreenGain || 0 == nature.u16BlueGain) || \
        (0 == cool.u16RedGain || 0 == cool.u16GreenGain || 0 == cool.u16BlueGain))
    {
        printf("EosUpdateWB, get RGB gain is 0\n");
        bRet = MAPI_FALSE;
    }

    if (bRet)
    {
        GetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_WARM, &u16RedGain, &u16GreenGain, &u16BlueGain, &u16RedOffset, &u16GreenOffset, &u16BlueOffset, MAPI_INPUT_SOURCE_HDMI);
        if (u16RedGain != warm.u16RedGain || u16GreenGain != warm.u16GreenGain || u16BlueGain != warm.u16BlueGain)
        {
            bWarmFlag = MAPI_TRUE;
        }

        GetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_NATURE, &u16RedGain, &u16GreenGain, &u16BlueGain, &u16RedOffset, &u16GreenOffset, &u16BlueOffset, MAPI_INPUT_SOURCE_HDMI);
        if (u16RedGain != nature.u16RedGain || u16GreenGain != nature.u16GreenGain || u16BlueGain != nature.u16BlueGain)
        {
            bNatureFlag = MAPI_TRUE;
        }

        GetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_COOL, &u16RedGain, &u16GreenGain, &u16BlueGain, &u16RedOffset, &u16GreenOffset, &u16BlueOffset, MAPI_INPUT_SOURCE_HDMI);
        if (u16RedGain != cool.u16RedGain || u16GreenGain != cool.u16GreenGain || u16BlueGain != cool.u16BlueGain)
        {
            bCoolFlag = MAPI_TRUE;
        }

    	for(int i = MS_INPUT_SOURCE_TYPE_VGA;  i < MS_INPUT_SOURCE_TYPE_OTHERS;  i++)
    	{
    		if(i ==MS_INPUT_SOURCE_TYPE_NONE || i == MS_INPUT_SOURCE_TYPE_SVIDEO || i == MS_INPUT_SOURCE_TYPE_SCART)
    		{
    			continue;
    		}

    		if (bWarmFlag)
            {
                MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->SetFactoryColorTempEx(MSrv_Picture::MS_COLOR_TEMP_WARM, (EN_MS_INPUT_SOURCE_TYPE)i, warm);
            }
    		if (bNatureFlag)
            {
                MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->SetFactoryColorTempEx(MSrv_Picture::MS_COLOR_TEMP_NATURE, (EN_MS_INPUT_SOURCE_TYPE)i, nature);
            }
    		if (bCoolFlag)
            {
                MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->SetFactoryColorTempEx(MSrv_Picture::MS_COLOR_TEMP_COOL, (EN_MS_INPUT_SOURCE_TYPE)i, cool);
            }
    	}
    }
    return bRet;
}
// EosTek Patch End

