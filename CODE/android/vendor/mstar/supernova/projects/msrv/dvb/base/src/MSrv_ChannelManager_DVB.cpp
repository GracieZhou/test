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
#include "MSrv_ChannelManager_DVB.h"

// headers of standard C libs
#include <string.h>

// headers of standard C++ libs

// headers of the same layer's
#include "MSrv_ATV_Player.h"
#include "MSrv_DTV_Player.h"
#include "MSrv_DTV_Player_DVB.h"
#include "MSrv_System_Database.h"
#include "MSrv_Control.h"
#include "MSrv_Timer.h"
#include "MSrv_SSSound.h"
#if (HBBTV_ENABLE)//temp solution for streaming go back to live in
#include "MW_HBBTV.h"
#endif
#include "SystemInfo.h"

// headers of underlying layer's
#include "debug.h"
#include "mapi_audio.h"
#include "mapi_interface.h"
#include "mapi_utility.h"
#include "SystemInfo.h"
#include "mapi_system.h"

#define MSRV_CHANNELMANAGER_DVB_INFO(dbgID, fmt, arg...)          //printf((char *)fmt, ##arg)

static BOOL CopyDTVProgramToCMProgram(const ST_DTV_PROGRAM_INFO& rDTVProg, ST_CM_PROGRAM_INFO& rCMProg);
static BOOL SetInputSource(MAPI_INPUT_SOURCE_TYPE eInputSrc, MAPI_SCALER_WIN eWin, BOOL bForceSet)
{
    return MSrv_Control::GetInstance()->SetInputSource(eInputSrc, TRUE, FALSE, FALSE, eWin, bForceSet);
}

MSrv_ChannelManager_DVB::MSrv_ChannelManager_DVB()
    :m_TempRTNProg(), m_TempPipSubRTNProg()
{
    m_RTNProg.u32LastChannelNumber    = 0;
    m_PipSubRTNProg.u32LastChannelNumber    = 0;
#if (A3_STB_ENABLE == 1)
    m_RTNProg.u8LastServiceType       = E_SERVICETYPE_DTV;
    m_PipSubRTNProg.u8LastServiceType = E_SERVICETYPE_DTV;
#else
    m_RTNProg.u8LastServiceType       = 0;
    m_PipSubRTNProg.u8LastServiceType = 0;
#endif

    memset((void *)u32TotalNumber,0,sizeof(U32)*E_COUNT_TYPE_MAX);

    m_ATVonly   =   FALSE;
    m_DTVonly   =   FALSE;
    m_ATVDTVboth    =   FALSE;

    const MAPI_VIDEO_INPUTSRCTABLE *pInputSrcTable = NULL;
    pInputSrcTable = MSrv_Control::GetInstance()->GetSourceList();
    if(pInputSrcTable[MAPI_INPUT_SOURCE_DTV].u32EnablePort && pInputSrcTable[MAPI_INPUT_SOURCE_ATV].u32EnablePort)
    {
        m_ATVDTVboth = TRUE;
    }
    else if(pInputSrcTable[MAPI_INPUT_SOURCE_DTV].u32EnablePort)
    {
        m_DTVonly = TRUE;
    }
    else if(pInputSrcTable[MAPI_INPUT_SOURCE_ATV].u32EnablePort)
    {
        m_ATVonly = TRUE;
    }
    else
    {
       ASSERT(0);
    }
    MS_CM_GET_SERVICE_INFO stQueryInfo = {};
    if(GetCurrProgramInfo(&stQueryInfo))
    {
        m_RTNProg.u32LastChannelNumber = stQueryInfo.u32Number;
        m_RTNProg.u8LastServiceType = stQueryInfo.u32Number;

#if (STB_ENABLE == 1)
        if(stQueryInfo.u8ServiceType == E_SERVICETYPE_RADIO)
        {
            m_eLoopType = E_LOOP_RADIO;
        }
        else
        {
            m_eLoopType = E_LOOP_DTV_DATA;
        }

        for(int i = 0; i< E_LOOP_TYPE_MAX; i++)
        {
            m_LastChannel[i].u32LastChannelNumber = 0;
            m_LastChannel[i].u8LastServiceType = 0;
        }
#else
        m_eLoopType = E_LOOP_TYPE_MAX;
#endif
    }
    else
    {
#if (STB_ENABLE == 1)
            m_eLoopType = E_LOOP_DTV_DATA;
#else
            m_eLoopType = E_LOOP_TYPE_MAX;
#endif
            m_RTNProg.u32LastChannelNumber = 0;
            m_RTNProg.u8LastServiceType = 0;
    }
    UpdateProgramCount();

}

MSrv_ChannelManager_DVB::~MSrv_ChannelManager_DVB()
{

}


#if (STB_ENABLE == 0)
#if (VE_ENABLE == 1)
BOOL MSrv_ChannelManager_DVB::IsEnableChannelChangeForScart(void)
{
    if((m_ATVDTVboth) || (m_ATVonly))
    {
        if(u32TotalNumber[E_COUNT_ATV] > 1)
        {
            MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "IsEnableChannelChangeForScart TRUE  \n");
            return TRUE;
        }
        else
        {
            MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "IsEnableChannelChangeForScart FALSE  \n");
            return FALSE;
        }
    }
    else if(m_DTVonly)
    {
        return FALSE;
    }

    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "IsEnableChannelChangeForScart FALSE  \n");
    return FALSE;
}
#endif
#endif

BOOL MSrv_ChannelManager_DVB::IsEnableChannelChangeForTV(void)
{
    MAPI_INPUT_SOURCE_TYPE enInputSrc = MSrv_Control::GetInstance()->GetCurrentInputSource();

    if(m_ATVDTVboth)
    {
        if(((enInputSrc == MAPI_INPUT_SOURCE_DTV) || (enInputSrc == MAPI_INPUT_SOURCE_DTV2)) ||
           (enInputSrc == MAPI_INPUT_SOURCE_ATV))
        {
            if((((u32TotalNumber[E_COUNT_DTV] - u32TotalNumber[E_COUNT_DTV_DELETE]) - u32TotalNumber[E_COUNT_DTV_NOT_VISIABLE]) + u32TotalNumber[E_COUNT_ATV]) > 1)
            {
                MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "IsEnableChannelChangeForTV TRUE  \n");
                return TRUE;
            }
            else
            {
                MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "IsEnableChannelChangeForTV FALSE  \n");
                return FALSE;
            }
        }
    }
    else if(m_DTVonly)
    {
        if((enInputSrc == MAPI_INPUT_SOURCE_DTV) || (enInputSrc == MAPI_INPUT_SOURCE_DTV2))
        {
            if(((u32TotalNumber[E_COUNT_DTV] - u32TotalNumber[E_COUNT_DTV_DELETE]) - u32TotalNumber[E_COUNT_DTV_NOT_VISIABLE]) > 1)
            {
                MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "IsEnableChannelChangeForTV TRUE  \n");

                return TRUE;
            }
            else
            {
                MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "IsEnableChannelChangeForTV FALSE  \n");
                return FALSE;
            }
        }
    }
    else if(m_ATVonly)
    {
        if(enInputSrc == MAPI_INPUT_SOURCE_ATV)
        {
            if(u32TotalNumber[E_COUNT_ATV] > 1)
            {
                MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "IsEnableChannelChangeForTV TRUE  \n");
                return TRUE;
            }
            else
            {
                MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "IsEnableChannelChangeForTV FALSE  \n");
                return FALSE;
            }
        }
    }
    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "IsEnableChannelChangeForTV FALSE  \n");
    return FALSE;
}

BOOL MSrv_ChannelManager_DVB::SetLastTVorRadioProgram(void)
{
    MS_CM_GET_SERVICE_INFO stQueryInfo;
    if(GetCurrProgramInfo(&stQueryInfo))
    {
        switch(stQueryInfo.u8ServiceType)
        {
            case E_SERVICETYPE_DTV:
            case E_SERVICETYPE_DATA:
            {
                m_LastChannel[E_LOOP_DTV_DATA].u32LastChannelNumber = stQueryInfo.u32Number;
                m_LastChannel[E_LOOP_DTV_DATA].u8LastServiceType = stQueryInfo.u8ServiceType;
            }
            break;
            case E_SERVICETYPE_RADIO:
            {
                m_LastChannel[E_LOOP_RADIO].u32LastChannelNumber = stQueryInfo.u32Number;
                m_LastChannel[E_LOOP_RADIO].u8LastServiceType = stQueryInfo.u8ServiceType;
            }
            break;

            default:
                break;
        }
        return true;
    }
    else
    {
        return false;
    }

}

BOOL MSrv_ChannelManager_DVB::GetLastTVorRadioProgram(MS_CM_RTN_PROGRAM *proInfo)
{
    if(proInfo == NULL)
    {
        return false;
    }

    if( m_LastChannel[m_eLoopType].u8LastServiceType == E_SERVICETYPE_INVALID )
    {
        return false;
    }

    proInfo->u32LastChannelNumber = m_LastChannel[m_eLoopType].u32LastChannelNumber;
    proInfo->u8LastServiceType = m_LastChannel[m_eLoopType].u8LastServiceType;

    return true;
}

BOOL MSrv_ChannelManager_DVB::ChangeToFirstProgram(EN_FIRST_PROG_TYPE eProgramType , EN_FIRST_PROG_CHANGE_TYPE eFirstChangeType)
{
    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "ChangeToFirstProgram  eFirstChangeType=%d\n", eFirstChangeType);

#if (CI_PLUS_ENABLE == 1)
    MAPI_INPUT_SOURCE_TYPE enCurrentInputType;
    enCurrentInputType = MSrv_Control::GetInstance()->GetCurrentInputSource();
    if (MAPI_INPUT_SOURCE_DTV == enCurrentInputType)
    {
        BOOL bisCiOccupiedTuner = FALSE;
        MSrv_DTV_Player_DVB *pMsrvDtv = (MSrv_DTV_Player_DVB*)(MSrv_Control::GetMSrvDtv());
        bisCiOccupiedTuner = pMsrvDtv->IsCiOccupiedTuner(TRUE);
        if (TRUE == bisCiOccupiedTuner)
        {
            return FALSE;
        }
    }
#endif

    UpdateProgramCount(); //keeping program count correctly

    if(m_DTVonly)
    {
        switch(eProgramType)
        {
            case E_FIRST_PROG_ALL:
            case E_FIRST_PROG_DTV:
            {
                GetProgramCount(E_COUNT_ATV_DTV);

                if(eFirstChangeType == E_ON_TIME_BOOT_TYPE)
                {
                    MSrv_Timer* pTimer;
                    MSrv_Timer::ST_time stTime;
                    MSrv_Timer::EN_Timer_Period enState;
                    MSrv_Timer::ST_OnTime_TVDes stDes;
                    EN_TIMER_BOOT_TYPE enBootMode;

                    pTimer = MSrv_Control::GetMSrvTimer();
                    pTimer->GetOnTime(stTime, enState, stDes, enBootMode);

                    if(enBootMode == EN_TIMER_BOOT_ON_TIMER)
                    {
                    #if (STB_ENABLE == 1 && MSTAR_TVOS == 0)
                        MS_USER_SOUND_SETTING stAudioSetting;

                        MSrv_Control::GetMSrvSystemDatabase()->GetAudioSetting(&stAudioSetting);
					    //Database range (0~100) mapping to UI/OnTime_TVDes.Vol range (0~30)
                        //need convert UI range to DB range ,according to u8VolumeLevelInit table in VolumeControlFrame
                        if((stDes.u8Vol>=0) && (stDes.u8Vol<=15))
                        {
                            stAudioSetting.Volume = stDes.u8Vol * 4;
                        }
                        else if((stDes.u8Vol>15) && (stDes.u8Vol<=23))
                        {
                            stAudioSetting.Volume =  60+ (stDes.u8Vol-15) *3; //(15*4)
                        }
                        else
                        {
                            stAudioSetting.Volume =  84+ (stDes.u8Vol-23) *2; //(15*4) + ((23-15)*3)
                        }

                        MSrv_Control::GetMSrvSystemDatabase()->SetAudioSetting(&stAudioSetting);
                     #endif

                        switch(stDes.enTVSrc)
                        {
                            case MSrv_Timer::EN_Time_OnTimer_Source_DTV:
                                ProgramSel((U32)stDes.u16ChNo, E_SERVICETYPE_DTV,0);
                                break;
                            case MSrv_Timer::EN_Time_OnTimer_Source_DATA:
                                ProgramSel((U32)stDes.u16ChNo, E_SERVICETYPE_DATA,0);
                                break;
                            case MSrv_Timer::EN_Time_OnTimer_Source_RADIO:
                                ProgramSel((U32)stDes.u16ChNo, E_SERVICETYPE_RADIO,0);
                                break;
                            default:
                                break;
                        }
                    }
                    else if((enBootMode == EN_TIMER_BOOT_REMINDER) || (enBootMode == EN_TIMER_BOOT_RECORDER))
                    {
                        ST_EPG_EVENT_TIMER_INFO stCurTimerInfo;
                        memset(&stCurTimerInfo, 0, sizeof(ST_EPG_EVENT_TIMER_INFO));

                        if(MSrv_Control::GetMSrvTimer()->GetEPGTimerEventByIndex(stCurTimerInfo, 0))
                        {
                            _GetFocusMSrvDtv()->GetOffsetTime(stCurTimerInfo.u32StartTime, MAPI_TRUE);
                            ProgramSel((U32)stCurTimerInfo.u16ServiceNumber, stCurTimerInfo.u8ServiceType,0);
                        }
                        else
                        {
                            _GetFocusMSrvDtv()->PlayCurrentProgram();
                        }
                    }
                }
                else
                {
                    _GetFocusMSrvDtv()->PlayCurrentProgram();
                }

                return TRUE;
            }
            break;
            default:
                return FALSE;
                break;
        }
    }
#if (STB_ENABLE == 0)
    else if(m_ATVDTVboth)
    {
		// EosTek Patch Begin
        if (E_AUTO_SCAN_TYPE == eFirstChangeType)
        {
            if(E_FIRST_PROG_ATV == eProgramType)
            {
                ProgramSel((U32)PROGRAM_NUMBER_AFTER_AUTOSCAN, E_SERVICETYPE_ATV,0);
            }
            return TRUE;
        }
		// EosTek Patch End
        if(eFirstChangeType == E_ON_TIME_BOOT_TYPE)
        {
            MSrv_Timer* pTimer;
            MSrv_Timer::ST_time stTime;
            MSrv_Timer::EN_Timer_Period enState;
            MSrv_Timer::ST_OnTime_TVDes stDes;
            EN_TIMER_BOOT_TYPE enBootMode;
            MAPI_U32 u32Time = 0;

            pTimer = MSrv_Control::GetMSrvTimer();
            pTimer->GetOnTime(stTime, enState, stDes, enBootMode);

            if(eProgramType == E_FIRST_PROG_DTV)
            {
                switch(stDes.enTVSrc)
                {
                    case MSrv_Timer::EN_Time_OnTimer_Source_DTV:
                        ProgramSel((U32)stDes.u16ChNo, E_SERVICETYPE_DTV,0);
                        break;
                    case MSrv_Timer::EN_Time_OnTimer_Source_DATA:
                        ProgramSel((U32)stDes.u16ChNo, E_SERVICETYPE_DATA,0);
                        break;
                    case MSrv_Timer::EN_Time_OnTimer_Source_RADIO:
                        ProgramSel((U32)stDes.u16ChNo, E_SERVICETYPE_RADIO,0);
                        break;
                    default:
                        break;
                }
            }
            else
            {
                if(stDes.u16ChNo>=1)
                    stDes.u16ChNo = stDes.u16ChNo-1;
                ProgramSel((U32)stDes.u16ChNo, E_SERVICETYPE_ATV,0);
            }
            u32Time = mapi_interface::Get_mapi_system()->RTCGetCLK() + 1;
            pTimer->ReconfigTimerFromList(u32Time, TRUE);
            return TRUE;
        }

        switch(eProgramType)
        {
            case E_FIRST_PROG_ALL:
            {
                GetProgramCount(E_COUNT_ATV_DTV);

                //MS_CM_GET_SERVICE_INFO stQueryInfo;
                //stQueryInfo.u32QueryIndex = 0;  //Query item index.
                //if(!GetProgramInfo(&stQueryInfo))

                ST_CM_PROGRAM_INFO stQueryInfo;
                stQueryInfo.u32QueryIndex = 0;  //Query item index.
                if(!GetProgramInfo(E_INFO_DATABASE_INDEX,&stQueryInfo))
                {
                    return FALSE;
                }
                ProgramSel(stQueryInfo.unProgNumber.u32Number, stQueryInfo.u8ServiceType,0);

                return TRUE;
            }
            break;

            case E_FIRST_PROG_DTV:
            {
                MSrv_Control::GetInstance()->SetInputSource(MAPI_INPUT_SOURCE_DTV);
                _GetFocusMSrvDtv()->PlayCurrentProgram();
                return TRUE;
            }
            break;

            case E_FIRST_PROG_ATV:
            {
                U16 CurrentProgramNumber = MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL);
                ProgramSel((U32)CurrentProgramNumber, E_SERVICETYPE_ATV,0);
                return TRUE;
            }
            break;

            default:
                break;
        }
    }
    else if(m_ATVonly)
    {
        switch(eProgramType)
        {
            case E_FIRST_PROG_ALL:
            case E_FIRST_PROG_ATV:
            {
                if(eFirstChangeType == E_ON_TIME_BOOT_TYPE)
                {
                    MSrv_Timer* pTimer;
                    MSrv_Timer::ST_time stTime;
                    MSrv_Timer::EN_Timer_Period enState;
                    MSrv_Timer::ST_OnTime_TVDes stDes;
                    EN_TIMER_BOOT_TYPE enBootMode;

                    pTimer = MSrv_Control::GetMSrvTimer();
                    pTimer->GetOnTime(stTime, enState, stDes, enBootMode);
                    ProgramSel((U32)stDes.u16ChNo, E_SERVICETYPE_ATV,0);
                }
                else
                {
                    U16 CurrentProgramNumber = MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL);
                    ProgramSel(CurrentProgramNumber, E_SERVICETYPE_ATV,0);
                }
                return TRUE;
            }
            break;
            default:
                break;
        }
    }
#endif

    return FALSE;
}

EN_LoopServiceType MSrv_ChannelManager_DVB::ProgramLoopTypeReMap(EN_PROG_LOOP_TYPE eProgramLoopType)
{
    switch(eProgramLoopType){
        case E_PROG_LOOP_ALL:
        case E_PROG_LOOP_ALL_NO_CYCLE:
            return E_LOOP_ALL;
            break;
        case E_PROG_LOOP_ATV:
        case E_PROG_LOOP_ATV_NO_CYCLE:
            return E_LOOP_TYPE_MAX;
            break;
        case E_PROG_LOOP_DTV:
        case E_PROG_LOOP_DTV_NO_CYCLE:
        case E_PROG_LOOP_DTV_TV:
        case E_PROG_LOOP_DTV_RADIO:
        case E_PROG_LOOP_DTV_DATA:
            return E_LOOP_DTV;
            break;
        default:
            return E_LOOP_TYPE_MAX;
            break;
    }
}

BOOL MSrv_ChannelManager_DVB::ProgramCycleTypeReMap(EN_PROG_LOOP_TYPE eProgramLoopType)
{
    switch(eProgramLoopType){
        case E_PROG_LOOP_DTV:
        case E_PROG_LOOP_DTV_TV:
        case E_PROG_LOOP_DTV_RADIO:
        case E_PROG_LOOP_DTV_DATA:
        case E_PROG_LOOP_ALL:
            return TRUE;
            break;
        case E_PROG_LOOP_ALL_NO_CYCLE:
        case E_PROG_LOOP_DTV_NO_CYCLE:
        case E_PROG_LOOP_ATV_NO_CYCLE:
            return FALSE;
            break;
       default:
            return FALSE;
            break;
    }

}

BOOL MSrv_ChannelManager_DVB::ProgramUp(EN_PROG_LOOP_TYPE eProgramLoopType)
{
#if (AUTO_TEST == 1)
    printf("\033[1;31m[AUTO_TEST][channel change]: ProgramUp [PIU][%u]\033[0m\n", mapi_time_utility::GetPiuTimer1());
#endif
    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "ProgramUp  \n");
    BOOL bRet = FALSE;

#if ((HBBTV_ENABLE ==1) && (!NEW_VOD))

        if(MW_HBBTV::GetCheckedInstance())
        {
            if(MW_HBBTV::GetCheckedInstance()->IsStreaming())
            {
                return FALSE;
            }
        }
#endif

#if (STB_ENABLE == 0)
#if (VE_ENABLE == 1)
    MAPI_INPUT_SOURCE_TYPE enInputSrc = MSrv_Control::GetInstance()->GetCurrentInputSource();
    if((enInputSrc >= MAPI_INPUT_SOURCE_SCART)
            && (enInputSrc < MAPI_INPUT_SOURCE_SCART_MAX))
    {
        bRet = ProgramUpForScart();
    }
    else
#endif
#endif
    {
        bRet = ProgramUpForTV(eProgramLoopType);
    }

    MSrv_Player *pMsrvPlayer = MSrv_Control::GetInstance()->GetMSrvPlayer(MSrv_Control::GetInstance()->GetCurrentInputSource());
    if((pMsrvPlayer != NULL) && (bRet == TRUE))
    {
        pMsrvPlayer->ResetSignalCount();
    }

    return bRet;
}


BOOL MSrv_ChannelManager_DVB::ProgramUpForTV(EN_PROG_LOOP_TYPE eProgramLoopTyp)
{
    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "ProgramUpForTV  \n");
    MAPI_INPUT_SOURCE_TYPE enInputSrc = MSrv_Control::GetInstance()->GetCurrentInputSource();

    BOOL bCycle=TRUE;
    EN_LoopServiceType eLoopType=E_LOOP_TYPE_MAX;

    if(IsEnableChannelChangeForTV() == FALSE)
    {
        return FALSE;
    }

#if (MSTAR_TVOS == 1)
    if(SystemInfo::GetInstance()->GetVolumeCompensationFlag() == TRUE)
    {
#if (STB_ENABLE == 0)
        if(enInputSrc == MAPI_INPUT_SOURCE_ATV)
        {
            MSrv_Control::GetMSrvAtv()->SetChannelVolumeCompensation(MSrv_ATV_Player::E_NEXT_CHNNEL_VOLUME_COMPENSATION);
        }
#endif
    }
#endif
    MSrv_Control::GetMSrvSSSound()->SetAbsoluteVolume();

#if (STB_ENABLE == 1 && TWIN_TUNER == 1)
    if (SetProgramReturn() == FALSE)
    {
        return FALSE;
    }
#else
    SetProgramReturn();
#endif

    if(E_LOOP_TYPE_MAX==m_eLoopType)
    {
        eLoopType=ProgramLoopTypeReMap(eProgramLoopTyp);
        if(eLoopType==E_LOOP_TYPE_MAX)
        {
            return FALSE;
        }
        bCycle=ProgramCycleTypeReMap(eProgramLoopTyp);
    }
    else
    {
        eLoopType=m_eLoopType;
    }

    if(m_DTVonly)
    {
        if((enInputSrc == MAPI_INPUT_SOURCE_DTV) || (enInputSrc == MAPI_INPUT_SOURCE_DTV2))
        {
            _GetFocusMSrvDtv()->ProgramUp(bCycle, eLoopType);
        }
    }
#if (STB_ENABLE == 0)
    else if(m_ATVDTVboth)
    {
        if((enInputSrc == MAPI_INPUT_SOURCE_DTV) || (enInputSrc == MAPI_INPUT_SOURCE_DTV2))
        {
             _GetFocusMSrvDtv()->ProgramUp(bCycle, eLoopType);
        }
        else if(enInputSrc == MAPI_INPUT_SOURCE_ATV)
        {
            MSrv_Control::GetMSrvAtv()->SetToNextChannel(0, 1);
        }
    }
    else if(m_ATVonly)
    {
        if(enInputSrc == MAPI_INPUT_SOURCE_ATV)
        {
            MSrv_Control::GetMSrvAtv()->SetToNextChannel(0, 1);
        }
    }
#endif

    return TRUE;
}

#if (STB_ENABLE == 0)
#if (VE_ENABLE == 1)
//This Function is for Scart Canal+, we can program up/down during Source = Scart
BOOL MSrv_ChannelManager_DVB::ProgramUpForScart(void)
{
    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "ProgramUpForScart  \n");
    MAPI_SCALER_WIN eWin = MAPI_MAIN_WINDOW;
    mapi_video_out *pTVVideoOut = NULL;

    if(IsEnableChannelChangeForScart() == FALSE)
    {
        return FALSE;
    }

    MSrv_Control::GetMSrvSSSound()->SetAbsoluteVolume();

    SetProgramReturn();

    if(IsVideoOutTVModeFreeToUse() == TRUE)
    {
        pTVVideoOut = mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_TV_MODE);
        if(MAPI_TRUE == MSrv_Control::GetInstance()->IsFocusOnSubSource())
        {
            eWin = MAPI_SUB_WINDOW;
        }
        if(pTVVideoOut != NULL)
        {
            if(pTVVideoOut->IsDestTypeExistent(eWin))
            {
                pTVVideoOut->SetVideoMute(TRUE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, eWin);
            }
        }
    }
    MSrv_Control::GetInstance()->SetInputSource(MAPI_INPUT_SOURCE_ATV);
    U16  u16ChannelNum;
    u16ChannelNum = MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER , 0, 0, NULL);
    u16ChannelNum++;
    if((u16ChannelNum) >= u32TotalNumber[E_COUNT_ATV])
    {
        u16ChannelNum = 0;
    }

    MSrv_Control::GetMSrvAtv()->SetChannel(u16ChannelNum, 1);
    if(pTVVideoOut != NULL)
    {
        if(pTVVideoOut->IsDestTypeExistent(eWin))
        {
            pTVVideoOut->SetVideoMute(FALSE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, eWin);
        }
    }

    return TRUE;
}
#endif
#endif

BOOL MSrv_ChannelManager_DVB::ProgramDown(EN_PROG_LOOP_TYPE eProgramLoopType)
{
#if (AUTO_TEST == 1)
    printf("\033[1;31m[AUTO_TEST][channel change]: ProgramDown [PIU][%u]\033[0m\n", mapi_time_utility::GetPiuTimer1());
#endif
    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "ProgramDown  \n");
    BOOL bRet = FALSE;

#if ((HBBTV_ENABLE ==1) && (!NEW_VOD))


    if(MW_HBBTV::GetCheckedInstance())
    {
        if(MW_HBBTV::GetCheckedInstance()->IsStreaming())
        {
            return FALSE;
        }
    }
#endif

#if (STB_ENABLE == 0)
#if (VE_ENABLE == 1)
    MAPI_INPUT_SOURCE_TYPE enInputSrc = MSrv_Control::GetInstance()->GetCurrentInputSource();
    if((enInputSrc >= MAPI_INPUT_SOURCE_SCART)
            && (enInputSrc < MAPI_INPUT_SOURCE_SCART_MAX))
    {
        bRet = ProgramDownForScart();
    }
    else
#endif
#endif
    {
        bRet = ProgramDownForTV(eProgramLoopType);
    }

    MSrv_Player *pMsrvPlayer = MSrv_Control::GetInstance()->GetMSrvPlayer(MSrv_Control::GetInstance()->GetCurrentInputSource());
    if((pMsrvPlayer != NULL) && (bRet == TRUE))
    {
        pMsrvPlayer->ResetSignalCount();
    }

    return bRet;

}


BOOL MSrv_ChannelManager_DVB::ProgramDownForTV(EN_PROG_LOOP_TYPE eProgramLoopTyp)
{
    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "ProgramDownForTV \n");
    MAPI_INPUT_SOURCE_TYPE enInputSrc = MSrv_Control::GetInstance()->GetCurrentInputSource();
    BOOL bCycle=TRUE;
    EN_LoopServiceType eLoopType=E_LOOP_TYPE_MAX;

    if(IsEnableChannelChangeForTV() == FALSE)
    {
        return FALSE;
    }

    #if (MSTAR_TVOS == 1)
    if(SystemInfo::GetInstance()->GetVolumeCompensationFlag() == TRUE)
    {
    #if (STB_ENABLE == 0)
        if(enInputSrc == MAPI_INPUT_SOURCE_ATV)
        {
            MSrv_Control::GetMSrvAtv()->SetChannelVolumeCompensation(MSrv_ATV_Player::E_PRE_CHNNEL_VOLUME_COMPENSATION);
        }
    #endif
    }
    #endif
    MSrv_Control::GetMSrvSSSound()->SetAbsoluteVolume();

#if (STB_ENABLE == 1 && TWIN_TUNER == 1)
    if (SetProgramReturn() == FALSE)
    {
        return FALSE;
    }
#else
    SetProgramReturn();
#endif

    if(E_LOOP_TYPE_MAX==m_eLoopType)
    {
        eLoopType=ProgramLoopTypeReMap(eProgramLoopTyp);
        if(eLoopType==E_LOOP_TYPE_MAX)
        {
            return FALSE;
        }
        bCycle=ProgramCycleTypeReMap(eProgramLoopTyp);
     }
     else
     {
        eLoopType=m_eLoopType;
     }

    if(m_DTVonly)
    {
        if((enInputSrc == MAPI_INPUT_SOURCE_DTV) || (enInputSrc == MAPI_INPUT_SOURCE_DTV2))
        {
            _GetFocusMSrvDtv()->ProgramDown(bCycle, eLoopType);
        }
    }
#if (STB_ENABLE == 0)
    else if(m_ATVDTVboth)
    {
        if((enInputSrc == MAPI_INPUT_SOURCE_DTV) || (enInputSrc == MAPI_INPUT_SOURCE_DTV2))
        {
            BOOL bRet = FALSE;
        #if (PIP_ENABLE == 1)
            //Focus on sub window
            if(TRUE == MSrv_Control::GetInstance()->IsFocusOnSubSource())
            {
                bRet = MSrv_Control::GetInstance()->SetInputSource(MAPI_INPUT_SOURCE_DTV2, TRUE, FALSE,FALSE, MAPI_SUB_WINDOW);
            }
            else
            {
                bRet = MSrv_Control::GetInstance()->SetInputSource(MAPI_INPUT_SOURCE_DTV);
            }
        #else
            bRet = MSrv_Control::GetInstance()->SetInputSource(MAPI_INPUT_SOURCE_DTV);
        #endif
            if(TRUE == bRet)//if source change success
            {
                _GetFocusMSrvDtv()->ProgramDown(bCycle, eLoopType);
            }
        }
        else if(enInputSrc == MAPI_INPUT_SOURCE_ATV)
        {
            MSrv_Control::GetMSrvAtv()->SetToPreChannel(0, 1);
        }
    }
    else if(m_ATVonly)
    {
        if(enInputSrc == MAPI_INPUT_SOURCE_ATV)
        {
            MSrv_Control::GetMSrvAtv()->SetToPreChannel(0, 1);
        }
    }
#endif

    return TRUE;

}

#if (STB_ENABLE == 0)
#if (VE_ENABLE == 1)
//This Function is for Scart Canal+, we can program up/down during Source = Scart
BOOL MSrv_ChannelManager_DVB::ProgramDownForScart(void)
{
    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "ProgramDown ForScart \n");
    MAPI_SCALER_WIN eWin = MAPI_MAIN_WINDOW;
    mapi_video_out *pTVVideoOut = NULL;
    if(IsEnableChannelChangeForScart() == FALSE)
    {
        return FALSE;
    }

    MSrv_Control::GetMSrvSSSound()->SetAbsoluteVolume();

    SetProgramReturn();

    if(IsVideoOutTVModeFreeToUse() == TRUE)
    {
        pTVVideoOut = mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_TV_MODE);
        if(MAPI_TRUE == MSrv_Control::GetInstance()->IsFocusOnSubSource())
        {
            eWin = MAPI_SUB_WINDOW;
        }
        if(pTVVideoOut != NULL)
        {
            if(pTVVideoOut->IsDestTypeExistent(eWin))
            {
                pTVVideoOut->SetVideoMute(TRUE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, eWin);
            }
        }
    }

    MSrv_Control::GetInstance()->SetInputSource(MAPI_INPUT_SOURCE_ATV);
    U16  u16ChannelNum;
    u16ChannelNum = MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER , 0, 0, NULL);
    if(u16ChannelNum == 0)
    {
        u16ChannelNum = u32TotalNumber[E_COUNT_ATV] - 1;
    }
    else
    {
        u16ChannelNum--;
    }

    MSrv_Control::GetMSrvAtv()->SetChannel(u16ChannelNum, 1);

    if(pTVVideoOut != NULL)
    {
        if(pTVVideoOut->IsDestTypeExistent(eWin))
        {
            pTVVideoOut->SetVideoMute(FALSE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, eWin);
        }
    }

    return TRUE;
}
#endif
#endif

//TVOS: will be deleted
BOOL MSrv_ChannelManager_DVB::ProgramSel(U32 u32Number, U8 u8ServiceType)
{
    return ProgramSel(u32Number,u8ServiceType,0);
}

BOOL MSrv_ChannelManager_DVB::ProgramSel(U32 u32ProgNumber, U8 u8ProgType ,U16 u16ProgID, BOOL bForceSet)
{
#if (AUTO_TEST == 1)
    printf("\033[1;31m[AUTO_TEST][channel change]: ProgramSel [PIU][%u]\033[0m\n", mapi_time_utility::GetPiuTimer1());
#endif
    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "ProgramSel Number[%d] Type[%d] ID[%d]\n", u32ProgNumber, u8ProgType,u16ProgID);

    BOOL bRet = FALSE;
#if ((HBBTV_ENABLE ==1) && (!NEW_VOD))


    if(MW_HBBTV::GetCheckedInstance())
    {
        if(MW_HBBTV::GetCheckedInstance()->IsStreaming())
        {
            //printf("\033[44m\033[33mskip ProgramSel\n\033[0m");

            return FALSE;
        }
    }

#endif

#if (STB_ENABLE == 0)
#if (VE_ENABLE == 1)
    MAPI_INPUT_SOURCE_TYPE enInputSrc = MSrv_Control::GetInstance()->GetCurrentInputSource();
    if((enInputSrc >= MAPI_INPUT_SOURCE_SCART)
            && (enInputSrc < MAPI_INPUT_SOURCE_SCART_MAX))
    {
        bRet = ProgramSelForScart(u32ProgNumber, u8ProgType);
    }
    else
#endif
#endif
    {
        bRet = ProgramSelForTV(u32ProgNumber, u8ProgType, bForceSet);
    }

    MSrv_Player *pMsrvPlayer = MSrv_Control::GetInstance()->GetMSrvPlayer(MSrv_Control::GetInstance()->GetCurrentInputSource());
    if((pMsrvPlayer != NULL) && (bRet == TRUE))
    {
        pMsrvPlayer->ResetSignalCount();
    }

    return bRet;
}

BOOL MSrv_ChannelManager_DVB::ProgramSelForTV(U32 u32Number, U8 u8ServiceType, BOOL bForceSet)
{
    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "ProgramSelForTV Number[%d] Type[%d] \n", u32Number, u8ServiceType);

/*Due to including non-visible prog. so, DO NOT check for this case!
issued by number of programs are 2 which one is visible and the other is non-visible*/
#if 0
    if(IsEnableChannelChangeForTV() == FALSE)
    {
        return FALSE;
    }
#endif

#if (MSTAR_TVOS == 1)
#if (STB_ENABLE == 0)
    MAPI_INPUT_SOURCE_TYPE enInputSrc = MSrv_Control::GetInstance()->GetCurrentInputSource();
    if(SystemInfo::GetInstance()->GetVolumeCompensationFlag() == TRUE)
    {

        if(enInputSrc == MAPI_INPUT_SOURCE_ATV)
        {
            MSrv_Control::GetMSrvAtv()->SetChannelVolumeCompensation(MSrv_ATV_Player::E_SEL_CHNNEL_VOLUME_COMPENSATION, (U16)u32Number);
        }

    }
#endif
    #endif
    MSrv_Control::GetMSrvSSSound()->SetAbsoluteVolume();

#if (STB_ENABLE == 1 && TWIN_TUNER == 1)
    if (SetProgramReturn(u8ServiceType) == FALSE)
    {
        return FALSE;
    }
#else
    SetProgramReturn(u8ServiceType);
#endif

    if(m_DTVonly)
    {
        BOOL bRet = FALSE;
    #if (PIP_ENABLE == 1)
        //Focus on sub window
        if(TRUE == MSrv_Control::GetInstance()->IsFocusOnSubSource())
        {
            bRet = SetInputSource(MAPI_INPUT_SOURCE_DTV2, MAPI_SUB_WINDOW, bForceSet);
        }
        else
        {
            bRet = SetInputSource(MAPI_INPUT_SOURCE_DTV, MAPI_MAIN_WINDOW, bForceSet);
        }
    #else
        bRet = SetInputSource(MAPI_INPUT_SOURCE_DTV, MAPI_MAIN_WINDOW, bForceSet);
    #endif
        if(TRUE == bRet)//if source change success
        {
            _GetFocusMSrvDtv()->ProgramSel(u32Number, u8ServiceType, TRUE, FALSE, bForceSet);
        }
    }
#if (STB_ENABLE == 0)
    else if(m_ATVDTVboth)
    {
        if(u8ServiceType > E_SERVICETYPE_ATV)
        {
            BOOL bRet = FALSE;
        #if (PIP_ENABLE == 1)
            //Focus on sub window
            if(TRUE == MSrv_Control::GetInstance()->IsFocusOnSubSource())
            {
                bRet = SetInputSource(MAPI_INPUT_SOURCE_DTV2, MAPI_SUB_WINDOW, bForceSet);
            }
            else
            {
                bRet = SetInputSource(MAPI_INPUT_SOURCE_DTV, MAPI_MAIN_WINDOW, bForceSet);
            }
        #else
            bRet = SetInputSource(MAPI_INPUT_SOURCE_DTV, MAPI_MAIN_WINDOW, bForceSet);
        #endif
            if(TRUE == bRet)//if source change success
            {
                _GetFocusMSrvDtv()->ProgramSel(u32Number, u8ServiceType, TRUE, FALSE, bForceSet);
            }
        }
        else
        {
            BOOL bRet=FALSE;
#if (PIP_ENABLE == 1)
            if(TRUE == MSrv_Control::GetInstance()->IsPipModeEnable())
            {
                MAPI_INPUT_SOURCE_TYPE enCurrentInputSource=MSrv_Control::GetInstance()->GetCurrentInputSource();

                if(enCurrentInputSource == MSrv_Control::GetInstance()->GetCurrentSubInputSource())//Focus on sub window
                {
                    bRet = SetInputSource(MAPI_INPUT_SOURCE_ATV, MAPI_SUB_WINDOW, bForceSet);
                }
                else//focus on main window, keep doing original flow
                {
                    bRet = SetInputSource(MAPI_INPUT_SOURCE_ATV, MAPI_MAIN_WINDOW, bForceSet);
                }

                if(TRUE == bRet)//if source change success
                {
                    MSrv_Control::GetMSrvAtv()->SetChannel((U8)u32Number, 1);
                }
            }
            else
            {
                bRet = SetInputSource(MAPI_INPUT_SOURCE_ATV, MAPI_MAIN_WINDOW, bForceSet);
                if(TRUE == bRet)//if source change success
                {
                    MSrv_Control::GetMSrvAtv()->SetChannel((U8)u32Number, 1);
                }
            }
#else
            bRet = SetInputSource(MAPI_INPUT_SOURCE_ATV, MAPI_MAIN_WINDOW, bForceSet);
            if(TRUE == bRet)//if source change success
            {
                MSrv_Control::GetMSrvAtv()->SetChannel((U8)u32Number, 1);
            }
#endif
        }
    }
    else if(m_ATVonly)
    {
        BOOL bRet=FALSE;
#if (PIP_ENABLE == 1)
        if(TRUE == MSrv_Control::GetInstance()->IsPipModeEnable())
        {
            MAPI_INPUT_SOURCE_TYPE enCurrentInputSource=MSrv_Control::GetInstance()->GetCurrentInputSource();

            if(enCurrentInputSource == MSrv_Control::GetInstance()->GetCurrentSubInputSource())//Focus on sub window
            {
                bRet = SetInputSource(MAPI_INPUT_SOURCE_ATV, MAPI_SUB_WINDOW, bForceSet);
            }
            else//focus on main window, keep doing original flow
            {
                bRet = SetInputSource(MAPI_INPUT_SOURCE_ATV, MAPI_MAIN_WINDOW, bForceSet);
            }

            if(TRUE == bRet)//if source change success
            {
                MSrv_Control::GetMSrvAtv()->SetChannel((U8)u32Number, 1);
            }
        }
        else
        {
            bRet = SetInputSource(MAPI_INPUT_SOURCE_ATV, MAPI_MAIN_WINDOW, bForceSet);
            if(TRUE == bRet)//if source change success
            {
                MSrv_Control::GetMSrvAtv()->SetChannel((U8)u32Number, 1);
            }
        }
#else
        bRet = SetInputSource(MAPI_INPUT_SOURCE_ATV, MAPI_MAIN_WINDOW, bForceSet);
        if(TRUE == bRet)//if source change success
        {
            MSrv_Control::GetMSrvAtv()->SetChannel((U8)u32Number, 1);
        }
#endif

    }
#endif

    return TRUE;
}

#if (STB_ENABLE == 0)
#if (VE_ENABLE == 1)
//This Function is for Scart Canal+, we can program Select during Source = Scart
BOOL MSrv_ChannelManager_DVB::ProgramSelForScart(U32 u32Number, U8 u8ServiceType)
{
    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "ProgramSelForScart Number[%d] Type[%d] \n", u32Number, u8ServiceType);
    MAPI_SCALER_WIN eWin = MAPI_MAIN_WINDOW;
    mapi_video_out *pTVVideoOut = NULL;

    if(IsEnableChannelChangeForScart() == FALSE)
    {
        return FALSE;
    }

    MSrv_Control::GetMSrvSSSound()->SetAbsoluteVolume();

    SetProgramReturn(u8ServiceType);

    if(IsVideoOutTVModeFreeToUse() == TRUE)
    {
        pTVVideoOut = mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_TV_MODE);
        if(MAPI_TRUE == MSrv_Control::GetInstance()->IsFocusOnSubSource())
        {
            eWin = MAPI_SUB_WINDOW;
        }
        if(pTVVideoOut != NULL)
        {
            if(pTVVideoOut->IsDestTypeExistent(eWin))
            {
                pTVVideoOut->SetVideoMute(TRUE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, eWin);
            }
        }
    }

    MSrv_Control::GetInstance()->SetInputSource(MAPI_INPUT_SOURCE_ATV);
    MSrv_Control::GetMSrvAtv()->SetChannel((U8)u32Number, 1);
    if(pTVVideoOut != NULL)
    {
        if(pTVVideoOut->IsDestTypeExistent(eWin))
        {
            pTVVideoOut->SetVideoMute(FALSE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, eWin);
        }
    }

    return TRUE;
}
#endif
#endif


BOOL MSrv_ChannelManager_DVB::ReturnToPreviousProgram(void)
{
    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "ReturnToPreviousProgram\n");

    BOOL bRet = FALSE;

#if (STB_ENABLE == 0)
#if (VE_ENABLE == 1)
    MAPI_INPUT_SOURCE_TYPE enInputSrc = MSrv_Control::GetInstance()->GetCurrentInputSource();
    if((enInputSrc >= MAPI_INPUT_SOURCE_SCART)
            && (enInputSrc < MAPI_INPUT_SOURCE_SCART_MAX))
    {
        bRet = ProgramReturnForScart();
    }
    else
#endif
#endif
    {
        bRet = ProgramReturnForTV();
    }

    return bRet;
}


BOOL MSrv_ChannelManager_DVB::ProgramReturnForTV(void)
{
    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "ProgramReturnForTV  \n");

    if(IsEnableChannelChangeForTV() == FALSE)
    {
        return FALSE;
    }

    #if (MSTAR_TVOS == 1)
    #if (STB_ENABLE == 0)
    MAPI_INPUT_SOURCE_TYPE enInputSrc = MSrv_Control::GetInstance()->GetCurrentInputSource();
    if(SystemInfo::GetInstance()->GetVolumeCompensationFlag() == TRUE)
    {

        if(enInputSrc == MAPI_INPUT_SOURCE_ATV)
        {
            MSrv_Control::GetMSrvAtv()->SetChannelVolumeCompensation(MSrv_ATV_Player::E_SEL_CHNNEL_VOLUME_COMPENSATION, (U16)_GetProgramReturn().u32LastChannelNumber);
        }
    }
    #endif
    #endif
    MSrv_Control::GetMSrvSSSound()->SetAbsoluteVolume();

    U32 u32tmpChannelNumber = _GetProgramReturn().u32LastChannelNumber;
    U8 u8tmpServiceType = _GetProgramReturn().u8LastServiceType;

#if (STB_ENABLE == 1 && TWIN_TUNER == 1)
    if (SetProgramReturn() == FALSE)
    {
        return FALSE;
    }
#else
    SetProgramReturn();
#endif

    ProgramSel(u32tmpChannelNumber, u8tmpServiceType,0);

    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "ProgramReturnForTV LastNumber[%d] Type[%d] \n", _GetProgramReturn().u32LastChannelNumber, _GetProgramReturn().u8LastServiceType);
    return TRUE;

}

#if (STB_ENABLE == 0)
#if (VE_ENABLE == 1)
//This Function is for Scart Canal+, we can program Return during Source = Scart
BOOL MSrv_ChannelManager_DVB::ProgramReturnForScart(void)
{
    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "ProgramReturnForScart  \n");

    if(IsEnableChannelChangeForScart() == FALSE)
    {
        return FALSE;
    }

    U32 u32tmpChannelNumber = _GetProgramReturn().u32LastChannelNumber;
    U8 u8tmpServiceType = _GetProgramReturn().u8LastServiceType;
    if(u8tmpServiceType == E_SERVICETYPE_ATV)
    {
        MSrv_Control::GetMSrvSSSound()->SetAbsoluteVolume();
        SetProgramReturn();
        ProgramSel(u32tmpChannelNumber, u8tmpServiceType,0);
    }
    else
    {
        return FALSE;
    }

    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "ProgramReturnForScart LastNumber[%d] Type[%d] \n", _GetProgramReturn().u32LastChannelNumber, _GetProgramReturn().u8LastServiceType);
    return TRUE;
}
#endif
#endif

#if (STB_ENABLE == 1) || (CONNECTTV_BOX == 1)
BOOL MSrv_ChannelManager_DVB::ProgramSwitchTVRADIO(void)
{
    ST_DTV_PROGRAM_COUNT stProgCnt;
    MSrv_Control::GetMSrvDtv()->GetProgramCount(E_EXCLUDE_NOT_VISIBLE_SKIPPED_AND_DELETED,stProgCnt);

    if(m_eLoopType == E_LOOP_DTV_DATA)
    {
        if(stProgCnt.m_u16NumOfRadioProg > 0)
        {
            m_eLoopType = E_LOOP_RADIO;
        }
        else
        {
            return FALSE;
        }
    }
    else
    {
        if((stProgCnt.m_u16NumOfDTVProg + stProgCnt.m_u16NumOfDataProg) > 0)
        {
            m_eLoopType = E_LOOP_DTV_DATA;
        }
        else
        {
            return FALSE;
        }
    }

    return TRUE;
}
#endif

BOOL MSrv_ChannelManager_DVB::SetProgramFavorite(U32 u32ProgNumber, U8 u8ProgType, U16 u16ProgID, U8 u8FavoriteList)
{
    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "SetProgramFavorite Number[%d] Type[%d] ID[%d] FavoriteList[%d]\n", u32ProgNumber, u8ProgType, u16ProgID,u8FavoriteList);

    if(m_DTVonly)
    {
        MSrv_Control::GetMSrvDtv()->SetProgramFavorite(u8ProgType, u32ProgNumber, u8FavoriteList);
    }
#if (STB_ENABLE == 0)
    else if(m_ATVDTVboth)
    {
        if(u8ProgType > E_SERVICETYPE_ATV)
        {
            MSrv_Control::GetMSrvDtv()->SetProgramFavorite(u8ProgType, u32ProgNumber, u8FavoriteList);
        }
        else
        {
            MSrv_Control::GetMSrvAtvDatabase()->SetFavoriteProgram(SET_FAVORITE_PROGRAM, u32ProgNumber, u8FavoriteList, NULL);
        }
    }
    else if(m_ATVonly)
    {
        MSrv_Control::GetMSrvAtvDatabase()->SetFavoriteProgram(SET_FAVORITE_PROGRAM, u32ProgNumber, u8FavoriteList, NULL);
    }
#endif

    return TRUE;
}

//TVOS: will be deleted
BOOL MSrv_ChannelManager_DVB::SetProgramFavorite(U32 u32Number, U8 u8ServiceType, U8 u8Favorite)
{
    return SetProgramFavorite(u32Number,u8ServiceType,0,u8Favorite);
}

//TVOS: will be moved to private
BOOL MSrv_ChannelManager_DVB::SetProgramLock(U32 u32Number, U8 u8ServiceType, BOOL bIsLock)
{
    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID,"SetProgramLock Number[%d] Type[%d] bool[%d]\n", u32Number, u8ServiceType, bIsLock);
    if(m_ATVDTVboth || m_DTVonly)
    {
        if(u8ServiceType > E_SERVICETYPE_ATV)
        {
            MSrv_Control::GetMSrvDtv()->SetProgramLock(u8ServiceType, u32Number, bIsLock);
            //Query current channel.
            //MS_CM_GET_SERVICE_INFO stQueryInfo;
            //if(GetCurrProgramInfo(&stQueryInfo))
            ST_CM_PROGRAM_INFO stQueryInfo;
            if(GetProgramInfo(E_INFO_CURRENT,&stQueryInfo))
            {
                if((u32Number == stQueryInfo.unProgNumber.u32Number) && (u8ServiceType == stQueryInfo.u8ServiceType))
                {
                    MSrv_Control::GetMSrvDtv()->PlayCurrentProgram();
                }
            }

        }
#if (STB_ENABLE == 0)
        else
        {

        // MS_CM_GET_SERVICE_INFO stQueryInfo;
        //if(FALSE==MSrv_Control_common::GetMSrvChannelManager()->GetCurrProgramInfo(&stQueryInfo))
        ST_CM_PROGRAM_INFO stQueryInfo;
        if(FALSE==GetProgramInfo(E_INFO_CURRENT,&stQueryInfo))
         {
             //printf("GetCurrProgramInfo FALSE!\n");
             return FALSE;
         }

         if(stQueryInfo.unProgNumber.u32Number == u32Number)
         {
             if(bIsLock == TRUE)
             {
                   mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_BYBLOCK_MUTEON_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
                   MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_BYBLOCK_MUTEON_);
                   MSrv_Control::GetInstance()->SetVideoMute(TRUE);

                   MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(LOCK_PROGRAM , u32Number, bIsLock, NULL);
                   MSrv_Control::GetMSrvAtv()->SetChannel(u32Number,TRUE);//for ATV block CH pop up password dialog
                   #if (VE_ENABLE == 1 || CVBSOUT_ENABLE==1)
                   if(IsVideoOutTVModeFreeToUse() == TRUE)
                   {
                       MAPI_SCALER_WIN eWin = MAPI_MAIN_WINDOW;
                       mapi_video_out *pTVVideoOut = NULL;

                       pTVVideoOut = mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_TV_MODE);
                       if(MAPI_TRUE == MSrv_Control::GetInstance()->IsFocusOnSubSource())
                       {
                           eWin = MAPI_SUB_WINDOW;
                       }
                       if(pTVVideoOut != NULL)
                       {
                           if(pTVVideoOut->IsDestTypeExistent(eWin))
                           {
                               pTVVideoOut->SetVideoMute(TRUE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, eWin);
                           }
                       }
                   }
                   #endif
                   return TRUE;
             }
             else
             {
                     MS_USER_SYSTEM_SETTING stGetSystemSetting;
                     MSrv_Control::GetMSrvSystemDatabase()->GetUserSystemSetting(&stGetSystemSetting);
                     mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_BYBLOCK_MUTEOFF_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
                     MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_BYBLOCK_MUTEOFF_);
                     mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_SCAN_MUTEOFF_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
                     MSrv_Control::GetMSrvAtv()->SetCurrentProgramBlock(FALSE);
                     #if (VE_ENABLE == 1 || CVBSOUT_ENABLE==1)
                     if(IsVideoOutTVModeFreeToUse() == TRUE)
                     {
                         MAPI_SCALER_WIN eWin = MAPI_MAIN_WINDOW;
                         mapi_video_out *pTVVideoOut = NULL;

                         pTVVideoOut = mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_TV_MODE);
                         if(MAPI_TRUE == MSrv_Control::GetInstance()->IsFocusOnSubSource())
                         {
                             eWin = MAPI_SUB_WINDOW;
                         }
                         if(pTVVideoOut != NULL)
                         {
                             if(pTVVideoOut->IsDestTypeExistent(eWin))
                             {
                                 pTVVideoOut->SetVideoMute(FALSE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, eWin);
                             }
                         }
                     }
                     #endif
                     if(stGetSystemSetting.AudioOnly == FALSE)
                           MSrv_Control::GetInstance()->SetVideoMute(FALSE);
             }
         }
         else
         {
            // printf("Not curr program, not do mute/unmute action!\n");
         }
           MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(LOCK_PROGRAM , u32Number, bIsLock, NULL);
        }
#endif
    }
#if (STB_ENABLE == 0)
    else if(m_ATVonly)
    {
             if(bIsLock == TRUE)
             {
                   mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_BYBLOCK_MUTEON_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
                   MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_BYBLOCK_MUTEON_);
                   MSrv_Control::GetInstance()->SetVideoMute(TRUE);
             }
             else
             {
                   MS_USER_SYSTEM_SETTING stGetSystemSetting;
                   MSrv_Control::GetMSrvSystemDatabase()->GetUserSystemSetting(&stGetSystemSetting);
                   mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_BYBLOCK_MUTEOFF_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
                   MSrv_Control::GetInstance()->SetAudioMute(E_AUDIO_BYBLOCK_MUTEOFF_);

                   if(stGetSystemSetting.AudioOnly == FALSE)
                        MSrv_Control::GetInstance()->SetVideoMute(FALSE);
             }
        MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(LOCK_PROGRAM , u32Number, bIsLock, NULL);
    }
#endif

    return TRUE;
}

//TVOS: will be moved to private
BOOL MSrv_ChannelManager_DVB::SetProgramSkip(U32 u32Number, U8 u8ServiceType, BOOL bIsSkip)
{
    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "SetProgramSkip Number[%d] Type[%d] bool[%d]\n", u32Number, u8ServiceType, bIsSkip);

    if(m_DTVonly)
    {
        MSrv_Control::GetMSrvDtv()->SetProgramSkip(u8ServiceType, u32Number, bIsSkip);
    }
#if (STB_ENABLE == 0)
    else if(m_ATVDTVboth)
    {
        if(u8ServiceType > E_SERVICETYPE_ATV)
        {
            MSrv_Control::GetMSrvDtv()->SetProgramSkip(u8ServiceType, u32Number, bIsSkip);
        }
        else
        {
            (MSrv_Control::GetMSrvAtvDatabase())->SetProgramInfo(SKIP_PROGRAM , u32Number, bIsSkip, NULL);
        }
    }
    else if(m_ATVonly)
    {
        (MSrv_Control::GetMSrvAtvDatabase())->SetProgramInfo(SKIP_PROGRAM , u32Number, bIsSkip, NULL);
    }
#endif

    return TRUE;
}

//TVOS: will be moved to private
BOOL MSrv_ChannelManager_DVB::SetProgramDel(U32 u32Number, U8 u8ServiceType)
{
    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "SetProgramDel Number[%d] Type[%d]\n", u32Number, u8ServiceType);

    if(m_DTVonly)
    {
        MSrv_Control::GetMSrvDtv()->SetProgramDelete(u8ServiceType, u32Number, TRUE);
        //MS_CM_GET_SERVICE_INFO stQueryInfo;
        //if(GetCurrProgramInfo(&stQueryInfo))
        ST_CM_PROGRAM_INFO stQueryInfo;
        if(GetProgramInfo(E_INFO_CURRENT,&stQueryInfo))
        {
            if((stQueryInfo.unProgNumber.u32Number == u32Number) && (stQueryInfo.u8ServiceType == u8ServiceType))
            {
                MSrv_Control::GetMSrvDtv()->PlayCurrentProgram();
            }
        }
    }
#if (STB_ENABLE == 0)
    else if(m_ATVDTVboth)
    {
        if(u8ServiceType > E_SERVICETYPE_ATV)
        {
            MSrv_Control::GetMSrvDtv()->SetProgramDelete(u8ServiceType, u32Number, TRUE);

            //MS_CM_GET_SERVICE_INFO stQueryInfo;
            //if(GetCurrProgramInfo(&stQueryInfo))
            ST_CM_PROGRAM_INFO stQueryInfo;
            if(GetProgramInfo(E_INFO_CURRENT,&stQueryInfo))
            {
            	#if (MSTAR_TVOS == 1)
				if(stQueryInfo.u8ServiceType > E_SERVICETYPE_ATV)
				#else
                if((stQueryInfo.unProgNumber.u32Number == u32Number) && (stQueryInfo.u8ServiceType == u8ServiceType))
				#endif
                {
                    MSrv_Control::GetMSrvDtv()->PlayCurrentProgram();
                }
            }
        }
        else if(u8ServiceType == E_SERVICETYPE_ATV)
        {
            MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(DELETE_PROGRAM , u32Number , 0, NULL);
        }
    }
    else if(m_ATVonly)
    {
        MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(DELETE_PROGRAM , u32Number , 0, NULL);
    }
#endif

    MSrv_Control::GetMSrvTimer()->DelEPGTimerByProg(u8ServiceType, u32Number);
    UpdateProgramCount();
    return TRUE;
}

//TVOS: will be moved to private
BOOL MSrv_ChannelManager_DVB::SetProgramHide(U32 u32Number, U8 u8ServiceType, BOOL bIsHide)
{
    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "SetProgramHidep Number[%d] Type[%d] bool[%d]\n", u32Number, u8ServiceType, bIsHide);

    if(m_DTVonly)
    {
        MSrv_Control::GetMSrvDtv()->SetProgramHide(u8ServiceType, u32Number, bIsHide);
    }
#if (STB_ENABLE == 0)
    else if(m_ATVDTVboth)
    {
        if(u8ServiceType > E_SERVICETYPE_ATV)
        {
            MSrv_Control::GetMSrvDtv()->SetProgramHide(u8ServiceType, u32Number, bIsHide);
        }
        else
        {
            (MSrv_Control::GetMSrvAtvDatabase())->SetProgramInfo(HIDE_PROGRAM , u32Number, bIsHide, NULL);
        }
    }
    else if(m_ATVonly)
    {
        (MSrv_Control::GetMSrvAtvDatabase())->SetProgramInfo(HIDE_PROGRAM , u32Number, bIsHide, NULL);
    }
#endif

    return TRUE;
}

BOOL MSrv_ChannelManager_DVB::SetProgramAttributes(EN_PROG_ATTRIBUTE_TYPE  eProgAttributeType ,U32 u32ProgNumber , U8 u8ProgramType, U16 u8ProgramID, BOOL bAttributeValue)
{
    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "SetProgramAttributes Number[%d] Type[%d] value[%d]\n", u32ProgNumber, u8ProgramType, bAttributeValue);

    int ret = 0;

    switch(eProgAttributeType){
        case E_ATTRIBUTE_DELETE:
            ret = SetProgramDel(u32ProgNumber, u8ProgramType);
            break;
        case E_ATTRIBUTE_LOCK:
            ret = SetProgramLock(u32ProgNumber,u8ProgramType,bAttributeValue);
            break;
        case E_ATTRIBUTE_SKIP:
            ret = SetProgramSkip(u32ProgNumber,u8ProgramType,bAttributeValue);
            break;
        case E_ATTRIBUTE_HIDE:
            ret = SetProgramHide(u32ProgNumber,u8ProgramType,bAttributeValue);
            break;
        default:
            return FALSE;
            break;
    }
    return ret;
}

BOOL MSrv_ChannelManager_DVB::GetProgramAttributes(EN_PROG_ATTRIBUTE_TYPE  eProgAttributeType ,U32 u32ProgNumber , U8 u8ProgramType, U16 u8ProgramID, BOOL *pbAttributeValue)
{
    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "GetProgramAttributes Number[%d] Type[%d]\n", u32ProgNumber, u8ProgramType);

    ST_CM_PROGRAM_INFO progInfo;
    BOOL ret=FALSE;
    if((pbAttributeValue==NULL)||(eProgAttributeType>=E_ATTRIBUTE_TYPE_MAX))
        return FALSE;

    progInfo.unProgNumber.u32Number=u32ProgNumber;
    progInfo.u8ServiceType=u8ProgramType;

    ret=GetProgramInfo(E_INFO_PROGRAM_NUMBER,&progInfo);
    if(ret==TRUE)
    {
        switch(eProgAttributeType)
        {
           case E_ATTRIBUTE_DELETE:
                *pbAttributeValue=progInfo.bIsDelete;
            break;
           case E_ATTRIBUTE_LOCK:
               *pbAttributeValue=progInfo.bIsLock;
            break;
           case E_ATTRIBUTE_SKIP:
               *pbAttributeValue=progInfo.bIsSkip;
            break;
           case E_ATTRIBUTE_HIDE:
               *pbAttributeValue=progInfo.bIsHide;
            break;
           default:
            break;
        }
    }

    return ret;

}

BOOL MSrv_ChannelManager_DVB::GetProgramName(U32 u32ProgNumber, U8 u8ProgType, U16 u16ProgID, string &sProgName)
{
    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "GetProgramName Number[%d] Type[%d] ID[%d]\n", u32ProgNumber, u8ProgType,u16ProgID);
    ST_CM_PROGRAM_INFO progInfo;
    BOOL ret=FALSE;
    progInfo.unProgNumber.u32Number=u32ProgNumber;
    progInfo.u8ServiceType=u8ProgType;

    ret=GetProgramInfo(E_INFO_PROGRAM_NUMBER,&progInfo);
    if(ret==TRUE){
        sProgName=progInfo.sServiceName;
    }

    return ret;
}


BOOL MSrv_ChannelManager_DVB::SetProgramName(U32 u32ProgNumber, U8 u8ProgType, U16 u16ProgID, const string &sProgName)
{
    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "SetProgramName Number[%d] Type[%d] ID[%d]\n", u32ProgNumber, u8ProgType,u16ProgID);


#if (STB_ENABLE == 1)
    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "SetProgramName Number[%d] Type[%d] ID[%d]\n", u32ProgNumber, u8ProgType,u16ProgID);
    if(m_DTVonly)
    {
        if(u8ProgType <= E_SERVICETYPE_ATV)
        {
            MSrv_DTV_Player *pMsrvDtv = MSrv_Control::GetMSrvDtv();
            pMsrvDtv->SetProgramName(u8ProgType,u32ProgNumber,sProgName);
        }
    }
    return TRUE;

#else
    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "SetProgramName Number[%d] Type[%d] ID[%d]\n", u32ProgNumber, u8ProgType,u16ProgID);
    if(m_ATVDTVboth | m_ATVonly)
    {
        if(u8ProgType == E_SERVICETYPE_ATV)
        {
            MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_STATION_NAME, u32ProgNumber, 0, (const void*)sProgName.substr(0, MAX_STATION_NAME - 1).c_str());
        }
    }
    return TRUE;
#endif
}
//TVOS: will be deleted
BOOL MSrv_ChannelManager_DVB::SetProgramName(U32 u32Number, U8 u8ServiceType, const string &sProgName)
{
    return SetProgramName(u32Number,u8ServiceType,0,sProgName);
}
BOOL MSrv_ChannelManager_DVB::ProgramsSwitch(U32 u32ProgPos1, U32 u32ProgPos2)
{
    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "ProgramSwitch src[%d] dst[%d] \n", u32ProgPos1, u32ProgPos2);

    ST_CM_PROGRAM_INFO programInfo;
#if (STB_ENABLE == 0)
    U8 u8ServiceType=0;
#endif

    programInfo.u32QueryIndex=u32ProgPos1;
    if(GetProgramInfo(E_INFO_DATABASE_INDEX,&programInfo)==FALSE)
        return FALSE;

#if (STB_ENABLE == 0)
    u8ServiceType=programInfo.u8ServiceType;
#endif

    if(m_DTVonly)
    {
        MSrv_Control::GetMSrvDtv()->ProgramSwitch(u32ProgPos1, u32ProgPos2);
    }
#if (STB_ENABLE == 0)
    else if(m_ATVDTVboth)
    {
        if(u8ServiceType == E_SERVICETYPE_ATV)
        {
            MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SWAP_PROGRAM , u32ProgPos1 - u32TotalNumber[E_COUNT_DTV], u32ProgPos2 - u32TotalNumber[E_COUNT_DTV], NULL);
        }
        else
        {
            MSrv_Control::GetMSrvDtv()->ProgramSwitch(u32ProgPos1, u32ProgPos2);
        }
    }
    else if(m_ATVonly) //ATV only
    {
        MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SWAP_PROGRAM , u32ProgPos1, u32ProgPos2, NULL);
    }
#endif

    return TRUE;
}

BOOL MSrv_ChannelManager_DVB::ProgramMove(U32 u32ProgSourcePos, U32 u32ProgTargetPos)
{
    MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "ProgramMove src[%d] dst[%d] \n", u32ProgSourcePos, u32ProgTargetPos);

    ST_CM_PROGRAM_INFO programInfo;
#if (STB_ENABLE == 0)
    U8 u8ServiceType=0;
#endif

    programInfo.u32QueryIndex=u32ProgSourcePos;
    if(GetProgramInfo(E_INFO_DATABASE_INDEX,&programInfo)==FALSE)
        return FALSE;

#if (STB_ENABLE == 0)
    u8ServiceType=programInfo.u8ServiceType;
#endif

    if(m_DTVonly)
    {
        MSrv_Control::GetMSrvDtv()->ProgramMove(u32ProgSourcePos, u32ProgTargetPos);
    }
#if (STB_ENABLE == 0)
    else if(m_ATVDTVboth)
    {
        if(u8ServiceType == E_SERVICETYPE_ATV)
        {
            MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(MOVE_PROGRAM_BY_CH_LIST , u32ProgSourcePos - u32TotalNumber[E_COUNT_DTV], u32ProgTargetPos - u32TotalNumber[E_COUNT_DTV], NULL);
        }
        else
        {
            MSrv_Control::GetMSrvDtv()->ProgramMove(u32ProgSourcePos, u32ProgTargetPos);
        }
    }
    else if(m_ATVonly) //ATV only
    {
        MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(MOVE_PROGRAM_BY_CH_LIST , u32ProgSourcePos, u32ProgTargetPos, NULL);
    }
#endif

    return TRUE;
}

//TVOS: will be deleted
BOOL MSrv_ChannelManager_DVB::SetProgramMove(U32 u32Source, U32 u32Target, U8 u8ServiceType)
{
    return ProgramMove(u32Source,u32Target);
}

BOOL MSrv_ChannelManager_DVB::SetProgramReturn(U8 u8ServiceTypeForProgSel)
{
    ST_CM_PROGRAM_INFO progInfo;
    if(GetProgramInfo(E_INFO_CURRENT,&progInfo)==FALSE)
        return FALSE;

    MS_CM_RTN_PROGRAM* aChRnTarget[2][2] = {{&m_TempRTNProg, &m_TempPipSubRTNProg}, {&m_RTNProg, &m_PipSubRTNProg}};
    U8 u8TuneToServiceType = progInfo.u8ServiceType;
    if(u8ServiceTypeForProgSel != E_SERVICETYPE_INVALID)
    {
        u8TuneToServiceType = u8ServiceTypeForProgSel;
    }
    U8 u8TypeIdx  = (u8TuneToServiceType == E_SERVICETYPE_ATV)?1:0;
    U8 u8PipIdx  = (TRUE == MSrv_Control::GetInstance()->IsFocusOnSubSource())?1:0;
    aChRnTarget[u8TypeIdx][u8PipIdx]->u32LastChannelNumber = progInfo.unProgNumber.u32Number;
    aChRnTarget[u8TypeIdx][u8PipIdx]->u8LastServiceType = progInfo.u8ServiceType;

#if (STB_ENABLE == 1 && TWIN_TUNER == 1)
    MSrv_DTV_Player_DVB* pDVBPlayer = dynamic_cast<MSrv_DTV_Player_DVB*>(MSrv_Control::GetMSrvDtv(MAPI_MAIN_WINDOW));
    ASSERT(pDVBPlayer);
    if ((pDVBPlayer->IsRecording() == TRUE) &&
        (MSrv_Control::GetInstance()->IsFocusOnSubSource() == FALSE))
    {
        U8 u8CurrentRouteIndex = MSrv_Control::GetInstance()->GetCurrentDtvRoute(MAPI_MAIN_WINDOW);
        EN_DTV_TYPE enCurrentDtvType = SystemInfo::GetInstance()->GetDTVRouteEnumByRouteIndex(u8CurrentRouteIndex);
        U8 u8TempRouteIndex;
        for (u8TempRouteIndex = 0; u8TempRouteIndex < MAXROUTECOUNT; u8TempRouteIndex++)
        {
            if (u8TempRouteIndex == u8CurrentRouteIndex)
            {
                continue;
            }
            if (enCurrentDtvType == SystemInfo::GetInstance()->GetDTVRouteEnumByRouteIndex(u8TempRouteIndex))
            {
                MSrv_DTV_Player_DVB* pNextDVBPlayer = dynamic_cast<MSrv_DTV_Player_DVB*>(MSrv_Control::GetInstance()->GetMSrvDtvByIndex(u8TempRouteIndex));
                ASSERT(pNextDVBPlayer);
                if (pNextDVBPlayer->IsRecording() == FALSE)
                {
                    MSrv_Control::GetInstance()->SwitchMSrvDtvRoute(u8TempRouteIndex);
                    PostEvent(0, EV_DTV_CHANNEL_CHANGE, 0);
                    break;
                    //channel change;
                }
            }
        }
        if (u8TempRouteIndex == MAXROUTECOUNT)
        {
            //Not find can support channel change route, post event yes/no stop record
            PostEvent(0, EV_PVR_NOTIFY_CHANNEL_CHANGE_UNSUPPORT, 0);
            return FALSE;
        }
    }
#endif

    if(progInfo.u8ServiceType != E_SERVICETYPE_ATV) //DTV
    {
         _GetFocusMSrvDtv()->SetChannelChangeNotifier(ChannelChangeDoneNotify, (void*)(this));
    }
    return TRUE;
    #if 0
    MS_CM_GET_SERVICE_INFO stQueryInfo;
    if(GetCurrProgramInfo(&stQueryInfo))
    {
        m_RTNProg.u32LastChannelNumber = stQueryInfo.u32Number;
        m_RTNProg.u8LastServiceType = stQueryInfo.u8ServiceType;
    }
    return TRUE;
    #endif
}

BOOL MSrv_ChannelManager_DVB::_GetCurrProgramInfo(ST_CM_PROGRAM_INFO *pResult)
{
    MAPI_INPUT_SOURCE_TYPE enInputSrc = MSrv_Control::GetInstance()->GetCurrentInputSource();
    U8 u8DTV=TRUE;
    U8 u8Tmp = 0;

    if(pResult == NULL)
    {
        return FALSE;
    }

    if (m_DTVonly == TRUE)
    {
        u8Tmp++;
        u8DTV = TRUE;
    }

    if (m_ATVDTVboth == TRUE)
    {
        u8Tmp++;
        if ((enInputSrc == MAPI_INPUT_SOURCE_DTV) || (enInputSrc == MAPI_INPUT_SOURCE_DTV2))
        {
           u8DTV = TRUE;
        }
        else
        {
           u8DTV = FALSE;
        }
    }

    if (m_ATVonly == TRUE)
    {
        u8Tmp++;
        u8DTV = FALSE;
    }


    if (u8Tmp != 1)
    {
        return FALSE;
    }


    if(u8DTV==TRUE)
    {
        ST_DTV_PROGRAM_INFO stQueryInfo;
        if(!_GetFocusMSrvDtv()->GetCurrentProgramInfo(stQueryInfo))
        {
            MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "CM Query fail *****\n");
            return FALSE;
        }

        MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "(%d)\tF(%d)\tL(%d)\tS(%d)\tD(%d)\tV(%d)\tT(%d)\t(%s)\n",
                                     stQueryInfo.m_u16Number, stQueryInfo.m_u8Favorite, stQueryInfo.m_bIsLock, stQueryInfo.m_bIsSkip,
                                     stQueryInfo.m_bIsDelete, stQueryInfo.m_bIsVisible, stQueryInfo.m_u8ServiceType, stQueryInfo.m_sServiceName.c_str());
        return CopyDTVProgramToCMProgram(stQueryInfo, *pResult);
    }
#if (STB_ENABLE == 0)
    else
    {
        MSrv_ATV_Player::ST_MSRV_CHANNEL_INFO stChannelInfo;
        memset(&stChannelInfo, 0, sizeof(MSrv_ATV_Player::ST_MSRV_CHANNEL_INFO));
        if(MSrv_Control::GetMSrvAtv()->GetCurrentProgramInfo(stChannelInfo) == FALSE)
        {
            MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "CM Query fail *****\n");
            pResult->unProgNumber.u32Number     =  stChannelInfo.u8ProgramNum;
            pResult->u8Favorite   =  stChannelInfo.stMisc.u8Favorite;
            pResult->bIsLock       =  stChannelInfo.stMisc.bIsLock;
            pResult->bIsSkip       =  stChannelInfo.stMisc.bSkip;
            pResult->bIsScramble   =  FALSE;
            pResult->bIsDelete     =  FALSE;
            pResult->bIsVisible    = TRUE;
            pResult->bIsSkip       =  stChannelInfo.stMisc.bSkip;
            pResult->bIsHide       =  stChannelInfo.stMisc.bHide;
            pResult->u8ServiceType =  E_SERVICETYPE_ATV;
            pResult->sServiceName.assign((char *)stChannelInfo.au8Name);
            return FALSE;
        }

        pResult->unProgNumber.u32Number     =  stChannelInfo.u8ProgramNum;
        pResult->u8Favorite   =  stChannelInfo.stMisc.u8Favorite;
        pResult->bIsLock       =  stChannelInfo.stMisc.bIsLock;
        pResult->bIsSkip       =  stChannelInfo.stMisc.bSkip;
        pResult->bIsScramble   =  FALSE;
        pResult->bIsDelete     =  FALSE;
        pResult->bIsVisible    = TRUE;
        pResult->bIsSkip       =  stChannelInfo.stMisc.bSkip;
        pResult->bIsHide       =  stChannelInfo.stMisc.bHide;
        pResult->u8ServiceType =  E_SERVICETYPE_ATV;
        pResult->sServiceName.assign((char *)stChannelInfo.au8Name);
        return TRUE;
    }
#endif
    return FALSE;
}

BOOL MSrv_ChannelManager_DVB::_GetNextProgramInfo(ST_CM_PROGRAM_INFO *pResult)
{
    MAPI_INPUT_SOURCE_TYPE enInputSrc = MSrv_Control::GetInstance()->GetCurrentInputSource();
    U8 u8DTV=TRUE;
    U8 u8Tmp=0;

    if(pResult == NULL)
    {
        return FALSE;
    }

    if (m_DTVonly == TRUE)
    {
        u8Tmp++;
        u8DTV = TRUE;
    }

    if (m_ATVDTVboth == TRUE)
    {
        u8Tmp++;
        if ((enInputSrc == MAPI_INPUT_SOURCE_DTV) || (enInputSrc == MAPI_INPUT_SOURCE_DTV2))
        {
           u8DTV = TRUE;
        }
        else
        {
           u8DTV = FALSE;
        }
    }

    if (m_ATVonly == TRUE)
    {
        u8Tmp++;
        u8DTV = FALSE;
    }


    if (u8Tmp != 1)
    {
        return FALSE;
    }

    if(u8DTV==TRUE)
    {
        ST_DTV_PROGRAM_INFO stQueryInfo;
    #if 0
        if(!MSrv_Control::GetMSrvDtv()->GetCurrentProgramInfo(stQueryInfo))
        {
            MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "CM Query fail *****\n");
            return FALSE;
        }

        if(!MSrv_Control::GetMSrvDtv()->GetNextProgramInfo(stQueryInfo.m_u8ServiceType,stQueryInfo.m_u16Number,stQueryInfo))
        {
            MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "CM Query fail *****\n");
            return FALSE;
        }
    #else
        if(!_GetFocusMSrvDtv()->GetNextProgramInfo(pResult->u8ServiceType,(U16)pResult->unProgNumber.u32Number,stQueryInfo))
        {
               MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "CM Query fail *****\n");
               return FALSE;
        }
    #endif

        MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "(%d)\tF(%d)\tL(%d)\tS(%d)\tD(%d)\tV(%d)\tT(%d)\t(%s)\n",
                                     stQueryInfo.m_u16Number, stQueryInfo.m_u8Favorite, stQueryInfo.m_bIsLock, stQueryInfo.m_bIsSkip,
                                     stQueryInfo.m_bIsDelete, stQueryInfo.m_bIsVisible, stQueryInfo.m_u8ServiceType, stQueryInfo.m_sServiceName.c_str());
        #if 0
        printf("\033[0;34m %s (%d)\tF(%d)\tL(%d)\tS(%d)\tD(%d)\tV(%d)\tT(%d)\t(%s)\033[0m\n",
                                      __FUNCTION__,(unsigned int)stQueryInfo.m_u16Number, (unsigned int)stQueryInfo.m_u8Favorite, (unsigned int)stQueryInfo.m_bIsLock, (unsigned int)stQueryInfo.m_bIsSkip,
                                     (unsigned int)stQueryInfo.m_bIsDelete, (unsigned int)stQueryInfo.m_bIsVisible, (unsigned int)stQueryInfo.m_u8ServiceType, stQueryInfo.m_sServiceName.c_str());
        #endif
        return CopyDTVProgramToCMProgram(stQueryInfo, *pResult);
    }

     return FALSE;
}

BOOL MSrv_ChannelManager_DVB::_GetPrevProgramInfo(ST_CM_PROGRAM_INFO *pResult)
{
    MAPI_INPUT_SOURCE_TYPE enInputSrc = MSrv_Control::GetInstance()->GetCurrentInputSource();
    U8 u8DTV=TRUE;
    U8 u8Tmp=0;

    if(pResult == NULL)
    {
        return FALSE;
    }

    if (m_DTVonly == TRUE)
    {
        u8Tmp++;
        u8DTV = TRUE;
    }

    if (m_ATVDTVboth == TRUE)
    {
        u8Tmp++;
        if ((enInputSrc == MAPI_INPUT_SOURCE_DTV) || (enInputSrc == MAPI_INPUT_SOURCE_DTV2))
        {
           u8DTV = TRUE;
        }
        else
        {
           u8DTV = FALSE;
        }
    }

    if (m_ATVonly == TRUE)
    {
        u8Tmp++;
        u8DTV = FALSE;
    }


    if (u8Tmp != 1)
    {
        return FALSE;
    }

    if(u8DTV==TRUE)
    {
        ST_DTV_PROGRAM_INFO stQueryInfo;
    #if 0
        if(!MSrv_Control::GetMSrvDtv()->GetCurrentProgramInfo(stQueryInfo))
        {
            MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "CM Query fail *****\n");
            return FALSE;
        }

        if(!MSrv_Control::GetMSrvDtv()->GetPrevProgramInfo(stQueryInfo.m_u8ServiceType,stQueryInfo.m_u16Number,stQueryInfo))
        {
            MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "CM Query fail *****\n");
            return FALSE;
        }
    #else
        if(!_GetFocusMSrvDtv()->GetPrevProgramInfo(pResult->u8ServiceType,(U16)pResult->unProgNumber.u32Number,stQueryInfo))
        {
               MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "CM Query fail *****\n");
               return FALSE;
        }
    #endif


        MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "(%d)\tF(%d)\tL(%d)\tS(%d)\tD(%d)\tV(%d)\tT(%d)\t(%s)\n",
                                     stQueryInfo.m_u16Number, stQueryInfo.m_u8Favorite, stQueryInfo.m_bIsLock, stQueryInfo.m_bIsSkip,
                                     stQueryInfo.m_bIsDelete, stQueryInfo.m_bIsVisible, stQueryInfo.m_u8ServiceType, stQueryInfo.m_sServiceName.c_str());
        #if 0
        printf("\033[0;34m %s (%d)\tF(%d)\tL(%d)\tS(%d)\tD(%d)\tV(%d)\tT(%d)\t(%s)\033[0m\n",
                                      __FUNCTION__,(unsigned int)stQueryInfo.m_u16Number, (unsigned int)stQueryInfo.m_u8Favorite, (unsigned int)stQueryInfo.m_bIsLock, (unsigned int)stQueryInfo.m_bIsSkip,
                                     (unsigned int)stQueryInfo.m_bIsDelete, (unsigned int)stQueryInfo.m_bIsVisible, (unsigned int)stQueryInfo.m_u8ServiceType, stQueryInfo.m_sServiceName.c_str());
        #endif
        return CopyDTVProgramToCMProgram(stQueryInfo, *pResult);
    }

     return FALSE;
}



BOOL MSrv_ChannelManager_DVB::_GetProgramInfoByIndex(ST_CM_PROGRAM_INFO *pResult)
{
    U8 u8DTV=TRUE;
    U8 u8Tmp=0;
    if(pResult == NULL)
    {
        return FALSE;
    }

    if (m_DTVonly == TRUE)
    {
        u8Tmp++;
        u8DTV = TRUE;
    }

    if (m_ATVDTVboth == TRUE)
    {
        u8Tmp++;
        if (pResult->u32QueryIndex < u32TotalNumber[E_COUNT_DTV])
        {
           u8DTV = TRUE;
        }
        else
        {
           u8DTV = FALSE;
        }
    }

    if (m_ATVonly == TRUE)
    {
        u8Tmp++;
        u8DTV = FALSE;
    }


    if (u8Tmp != 1)
    {
        return FALSE;
    }

    if(u8DTV==TRUE)
    {
        ST_DTV_PROGRAM_INFO stQueryInfo;
        stQueryInfo.m_u16Number = pResult->u32QueryIndex;  //Query DTV channel index
        if(!_GetFocusMSrvDtv()->GetProgramInfoByIndex(pResult->u32QueryIndex, stQueryInfo))
        {
            MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "CM Query fail *****\n");
            return FALSE;
        }

        MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "(%d)\tF(%d)\tL(%d)\tS(%d)\tD(%d)\tV(%d)\tT(%d)\t(%s)\n",
                                     stQueryInfo.m_u16Number, stQueryInfo.m_u8Favorite, stQueryInfo.m_bIsLock, stQueryInfo.m_bIsSkip,
                                     stQueryInfo.m_bIsDelete, stQueryInfo.m_bIsVisible, stQueryInfo.m_u8ServiceType, stQueryInfo.m_sServiceName.c_str());

        return CopyDTVProgramToCMProgram(stQueryInfo, *pResult);
    }
#if (STB_ENABLE == 0)
    else
    {
        MSrv_ATV_Player::ST_MSRV_CHANNEL_INFO stChannelInfo;
        U8 u8ProgramIndex;

        if(m_ATVDTVboth == TRUE)
        {
            u8ProgramIndex = MSrv_Control::GetMSrvAtvDatabase()->CommondCmd(CONVERT_ORDINAL_NUMBER_TO_PROGRAM_NUMBER, ((U16)pResult->u32QueryIndex - u32TotalNumber[E_COUNT_DTV]), 0, NULL);
        }
        else
        {
            u8ProgramIndex = MSrv_Control::GetMSrvAtvDatabase()->CommondCmd(CONVERT_ORDINAL_NUMBER_TO_PROGRAM_NUMBER, (U16)pResult->u32QueryIndex, 0, NULL);
        }

        if(MSrv_Control::GetMSrvAtv()->GetProgramInfoByIndex(stChannelInfo, u8ProgramIndex) == FALSE)
        {
            MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "CM Query fail *****\n");
            return FALSE;
        }

        pResult->unProgNumber.u32Number     =  stChannelInfo.u8ProgramNum;
        pResult->u8Favorite   =  stChannelInfo.stMisc.u8Favorite;
        pResult->bIsLock       =  stChannelInfo.stMisc.bIsLock;
        pResult->bIsSkip       =  stChannelInfo.stMisc.bSkip;
        pResult->bIsScramble   =  FALSE;
        pResult->bIsDelete     =  FALSE;
        pResult->bIsVisible    = TRUE;
        pResult->bIsSkip       =  stChannelInfo.stMisc.bSkip;
        pResult->bIsHide       =  stChannelInfo.stMisc.bHide;
        pResult->u8ServiceType =  E_SERVICETYPE_ATV;
        pResult->sServiceName.assign((char *)stChannelInfo.au8Name);


        return TRUE;
    }
#endif
    return FALSE;
}

BOOL MSrv_ChannelManager_DVB::_GetProgramInfoByNumber(ST_CM_PROGRAM_INFO *pResult)
{
   if(pResult == NULL)
    {
        return FALSE;
    }

    if((pResult->u8ServiceType==E_SERVICETYPE_DTV)||(pResult->u8ServiceType==E_SERVICETYPE_RADIO)||(pResult->u8ServiceType==E_SERVICETYPE_DATA)||(pResult->u8ServiceType==E_SERVICETYPE_UNITED_TV))
    {
        ST_DTV_PROGRAM_INFO stQueryInfo;

        if(!_GetFocusMSrvDtv()->GetProgramInfoByNumber(pResult->unProgNumber.u32Number, pResult->u8ServiceType,stQueryInfo))
        {
            MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "CM Query fail *****\n");
            return FALSE;
        }

        MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "(%d)\tF(%d)\tL(%d)\tS(%d)\tD(%d)\tV(%d)\tT(%d)\t(%s)\n",
                                     stQueryInfo.m_u16Number, stQueryInfo.m_u8Favorite, stQueryInfo.m_bIsLock, stQueryInfo.m_bIsSkip,
                                     stQueryInfo.m_bIsDelete, stQueryInfo.m_bIsVisible, stQueryInfo.m_u8ServiceType, stQueryInfo.m_sServiceName.c_str());

        return CopyDTVProgramToCMProgram(stQueryInfo, *pResult);
    }
#if (STB_ENABLE == 0)
    else if(pResult->u8ServiceType==E_SERVICETYPE_ATV)
    {
        MSrv_ATV_Player::ST_MSRV_CHANNEL_INFO stChannelInfo;

        //U8 u8ProgramIndex = MSrv_Control::GetMSrvAtvDatabase()->CommondCmd(CONVERT_ORDINAL_NUMBER_TO_PROGRAM_NUMBER, (U16)pResult->u32QueryIndex, 0, NULL);
        if(MSrv_Control::GetMSrvAtv()->GetProgramInfoByIndex(stChannelInfo, (U8)pResult->unProgNumber.u32Number) == FALSE)
        {
            MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "CM Query fail *****\n");
            return FALSE;
        }

        pResult->unProgNumber.u32Number     =  stChannelInfo.u8ProgramNum;
        pResult->u8Favorite   =  stChannelInfo.stMisc.u8Favorite;
        pResult->bIsLock       =  stChannelInfo.stMisc.bIsLock;
        pResult->bIsSkip       =  stChannelInfo.stMisc.bSkip;
        pResult->bIsScramble   =  FALSE;
        pResult->bIsDelete     =  FALSE;
        pResult->bIsVisible    = TRUE;
        pResult->bIsSkip       =  stChannelInfo.stMisc.bSkip;
        pResult->bIsHide       =  stChannelInfo.stMisc.bHide;
        pResult->u8ServiceType =  E_SERVICETYPE_ATV;
        pResult->sServiceName.assign((char *)stChannelInfo.au8Name);


        return TRUE;
    }
#endif
    else
        return FALSE;

}

BOOL MSrv_ChannelManager_DVB::_GetProgramInfoByTripleID(ST_CM_PROGRAM_INFO *pResult)
{
   if(pResult == NULL)
    {
        return FALSE;
    }

    MSrv_DTV_Player_DVB* pPlayer = _GetFocusMSrvDtv();
    if(NULL != pPlayer)
    {
        ST_DTV_PROGRAM_INFO stQueryInfo;

        if(FALSE == pPlayer->GetProgramInfoByTripleID(pResult->u16OriginalNetwork_ID, pResult->u16TransportStream_ID, pResult->u16ServiceID, stQueryInfo))
        {
            MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "CM Query fail *****\n");
            return FALSE;
        }

        MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "(%d)\tF(%d)\tL(%d)\tS(%d)\tD(%d)\tV(%d)\tT(%d)\t(%s)\n",
                                     stQueryInfo.m_u16Number, stQueryInfo.m_u8Favorite, stQueryInfo.m_bIsLock, stQueryInfo.m_bIsSkip,
                                     stQueryInfo.m_bIsDelete, stQueryInfo.m_bIsVisible, stQueryInfo.m_u8ServiceType, stQueryInfo.m_sServiceName.c_str());

        return CopyDTVProgramToCMProgram(stQueryInfo, *pResult);
    }
    else
        return FALSE;
}

BOOL MSrv_ChannelManager_DVB::GetProgramInfo(EN_PROG_INFO_TYPE eProgInfoType,ST_CM_PROGRAM_INFO *pResult)
{
    BOOL ret=FALSE;
    switch(eProgInfoType)
    {
        case E_INFO_CURRENT:
            ret= _GetCurrProgramInfo(pResult);
            break;
        case E_INFO_DATABASE_INDEX:
            ret= _GetProgramInfoByIndex(pResult);
            break;
        case E_INFO_PROGRAM_NUMBER:
            ret= _GetProgramInfoByNumber(pResult);
            break;
        case E_INFO_PREVIOUS:
            ret = _GetPrevProgramInfo(pResult);
            break;
        case E_INFO_NEXT:
            ret = _GetNextProgramInfo(pResult);
            break;
        case E_INFO_PREVIOUS_BY_NUMBER:
            ret = _GetPrevProgramInfo(pResult);
            break;
        case E_INFO_NEXT_BY_NUMBER:
            ret = _GetNextProgramInfo(pResult);
            break;
        case E_INFO_BY_CHANNEL_TRIPLET:
            ret = _GetProgramInfoByTripleID(pResult);
            break;
        default:
            break;
    }

    return ret;
}



//TVOS: will be deleted
BOOL MSrv_ChannelManager_DVB::GetProgramInfo(MS_CM_GET_SERVICE_INFO *pResult)
{
    ST_CM_PROGRAM_INFO Result;
    BOOL ret=FALSE;
    Result.u32QueryIndex=pResult->u32QueryIndex;
    Result.unProgNumber.u32Number=pResult->u32Number;
    Result.u32ProgID=pResult->u32ProgID;
    Result.u8Favorite=pResult->u8Favorite;
    Result.bIsLock=pResult->bIsLock;
    Result.bIsSkip=pResult->bIsSkip;
    Result.bIsScramble=pResult->bIsScramble;
    Result.bIsDelete=pResult->bIsDelete;
    Result.bIsVisible=pResult->bIsVisible;
    Result.bIsHide=pResult->bIsHide;
    Result.u8ServiceType=pResult->u8ServiceType;
    Result.sServiceName=pResult->sServiceName;

    ret=GetProgramInfo(E_INFO_DATABASE_INDEX,&Result);

    pResult->u32QueryIndex=Result.u32QueryIndex;
    pResult->u32Number=Result.unProgNumber.u32Number;
    pResult->u32ProgID=Result.u32ProgID;
    pResult->u8Favorite=Result.u8Favorite;
    pResult->bIsLock=Result.bIsLock;
    pResult->bIsSkip=Result.bIsSkip;
    pResult->bIsScramble=Result.bIsScramble;
    pResult->bIsDelete=Result.bIsDelete;
    pResult->bIsVisible=Result.bIsVisible;
    pResult->bIsHide=Result.bIsHide;
    pResult->u8ServiceType=Result.u8ServiceType;
    pResult->sServiceName=Result.sServiceName;
    return ret;
}

//TVOS: will be deleted
BOOL MSrv_ChannelManager_DVB::GetCurrProgramInfo(MS_CM_GET_SERVICE_INFO *pResult)
{
    ST_CM_PROGRAM_INFO Result;
    BOOL ret=FALSE;
    Result.u32QueryIndex=pResult->u32QueryIndex;
    Result.unProgNumber.u32Number=pResult->u32Number;
    Result.u32ProgID=pResult->u32ProgID;
    Result.u8Favorite=pResult->u8Favorite;
    Result.bIsLock=pResult->bIsLock;
    Result.bIsSkip=pResult->bIsSkip;
    Result.bIsScramble=pResult->bIsScramble;
    Result.bIsDelete=pResult->bIsDelete;
    Result.bIsVisible=pResult->bIsVisible;
    Result.bIsHide=pResult->bIsHide;
    Result.u8ServiceType=pResult->u8ServiceType;
    Result.sServiceName=pResult->sServiceName;
    ret=GetProgramInfo(E_INFO_CURRENT,&Result);
    if(ret==FALSE)
    {
        return ret;
    }
    pResult->u32QueryIndex=Result.u32QueryIndex;
    pResult->u32Number=Result.unProgNumber.u32Number;
    pResult->u32ProgID=Result.u32ProgID;
    pResult->u8Favorite=Result.u8Favorite;
    pResult->bIsLock=Result.bIsLock;
    pResult->bIsSkip=Result.bIsSkip;
    pResult->bIsScramble=Result.bIsScramble;
    pResult->bIsDelete=Result.bIsDelete;
    pResult->bIsVisible=Result.bIsVisible;
    pResult->bIsHide=Result.bIsHide;
    pResult->u8ServiceType=Result.u8ServiceType;
    pResult->sServiceName=Result.sServiceName;
    return ret;
}

BOOL MSrv_ChannelManager_DVB::GetCurrChannelNumber(U32 *progNum)
{
    ST_CM_PROGRAM_INFO Result;
    BOOL ret=FALSE;

    if(progNum==NULL)
        return ret;

    ret=GetProgramInfo(E_INFO_CURRENT,&Result);
    if(ret==TRUE)
        *progNum=Result.unProgNumber.u32Number;

    return ret;
}
U32 MSrv_ChannelManager_DVB::GetProgramCount(EN_PROG_COUNT_TYPE eProgCountType)
{
    U32  u32Number=0;

    UpdateProgramCount();
    if(eProgCountType >= E_COUNT_TYPE_MAX)
    {
        return 0;
    }
    if(eProgCountType==E_COUNT_ATV_DTV)
    {
        u32Number=u32TotalNumber[E_COUNT_DTV] + u32TotalNumber[E_COUNT_ATV];
    }
    else
    {
        u32Number=u32TotalNumber[eProgCountType];
    }
    return u32Number;
}

//TVOS: will be deleted
BOOL MSrv_ChannelManager_DVB::GetProgramCount(U32 *m_u32Number)
{
     *m_u32Number=GetProgramCount(E_COUNT_ATV_DTV);
      return TRUE;
}
//TVOS: will be deleted
BOOL MSrv_ChannelManager_DVB::GetATVProgramCount(U32 *m_u32Number)
{
   *m_u32Number=GetProgramCount(E_COUNT_ATV);
    return TRUE;
}
//TVOS: will be deleted
BOOL MSrv_ChannelManager_DVB::GetDTVProgramCount(U32 *m_u32Number)
{
   *m_u32Number=GetProgramCount(E_COUNT_DTV);
    return TRUE;
}


BOOL MSrv_ChannelManager_DVB::UpdateProgramCount()
{
    ST_DTV_PROGRAM_COUNT stQueryTotal;

    if(m_DTVonly)
    {
        u32TotalNumber[E_COUNT_DTV] = 0;

        MSrv_DTV_Player *pMsrvDtv = _GetFocusMSrvDtv();
        if(pMsrvDtv->GetProgramCount(E_INCLUDE_ALL, stQueryTotal))
        {
            u32TotalNumber[E_COUNT_DTV] = stQueryTotal.m_u16NumOfTotalProg;
            u32TotalNumber[E_COUNT_DTV_RADIO] = stQueryTotal.m_u16NumOfRadioProg;
            u32TotalNumber[E_COUNT_DTV_DATA] = stQueryTotal.m_u16NumOfDataProg;
            u32TotalNumber[E_COUNT_DTV_TV] = stQueryTotal.m_u16NumOfDTVProg;
#if(NVOD_ENABLE == 1)
            u32TotalNumber[E_COUNT_DTV_NVOD] = stQueryTotal.m_u16NumofNvodProg;
#endif
        }

        if(pMsrvDtv->GetProgramCount(E_DELETED, stQueryTotal))
        {
            u32TotalNumber[E_COUNT_DTV_DELETE] = stQueryTotal.m_u16NumOfTotalProg;
            u32TotalNumber[E_COUNT_DTV_RADIO_DELETE] = stQueryTotal.m_u16NumOfRadioProg;
            u32TotalNumber[E_COUNT_DTV_DATA_DELETE] = stQueryTotal.m_u16NumOfDataProg;
            u32TotalNumber[E_COUNT_DTV_TV_DELETE] = stQueryTotal.m_u16NumOfDTVProg;
        }

        if(pMsrvDtv->GetProgramCount(E_NOT_VISIBLE, stQueryTotal))
        {

            u32TotalNumber[E_COUNT_DTV_NOT_VISIABLE] = stQueryTotal.m_u16NumOfTotalProg;
            u32TotalNumber[E_COUNT_DTV_RADIO_NOT_VISIABLE] = stQueryTotal.m_u16NumOfRadioProg;
            u32TotalNumber[E_COUNT_DTV_DATA_NOT_VISIABLE] = stQueryTotal.m_u16NumOfDataProg;
            u32TotalNumber[E_COUNT_DTV_TV_NOT_VISIABLE] = stQueryTotal.m_u16NumOfDTVProg;
        }

         if(pMsrvDtv->GetProgramCount(E_EXCLUDE_NOT_VISIBLE_AND_DELETED, stQueryTotal))
        {

            u32TotalNumber[E_COUNT_DTV_EXCLUDE_NOT_VISIABLE_AND_DELETE] = stQueryTotal.m_u16NumOfTotalProg;
            u32TotalNumber[E_COUNT_DTV_RADIO_EXCLUDE_NOT_VISIABLE_AND_DELETE] = stQueryTotal.m_u16NumOfRadioProg;
            u32TotalNumber[E_COUNT_DTV_DATA_EXCLUDE_NOT_VISIABLE_AND_DELETE] = stQueryTotal.m_u16NumOfDataProg;
            u32TotalNumber[E_COUNT_DTV_TV_EXCLUDE_NOT_VISIABLE_AND_DELETE] = stQueryTotal.m_u16NumOfDTVProg;
        }

        if(pMsrvDtv->GetProgramCount(E_INCLUDE_NOT_VISIBLE_EXCLUDE_DELETED, stQueryTotal))
        {

            u32TotalNumber[E_COUNT_DTV_INCLUDE_NOT_VISIABLE_EXCLUDE_DELETE] = stQueryTotal.m_u16NumOfTotalProg;
            u32TotalNumber[E_COUNT_DTV_RADIO_INCLUDE_NOT_VISIABLE_EXCLUDE_DELETE] = stQueryTotal.m_u16NumOfRadioProg;
            u32TotalNumber[E_COUNT_DTV_DATA_INCLUDE_NOT_VISIABLE_EXCLUDE_DELETE] = stQueryTotal.m_u16NumOfDataProg;
            u32TotalNumber[E_COUNT_DTV_TV_INCLUDE_NOT_VISIABLE_EXCLUDE_DELETE] = stQueryTotal.m_u16NumOfDTVProg;
        }

        if(pMsrvDtv->GetProgramCount(E_EXCLUDE_NOT_VISIBLE_INCLUDE_DELETED, stQueryTotal))
        {
            u32TotalNumber[E_COUNT_DTV_EXCLUDE_NOT_VISIABLE_INCLUDE_DELETE] = stQueryTotal.m_u16NumOfTotalProg;
            u32TotalNumber[E_COUNT_DTV_RADIO_EXCLUDE_NOT_VISIABLE_INCLUDE_DELETE] = stQueryTotal.m_u16NumOfRadioProg;
            u32TotalNumber[E_COUNT_DTV_DATA_EXCLUDE_NOT_VISIABLE_INCLUDE_DELETE] = stQueryTotal.m_u16NumOfDataProg;
            u32TotalNumber[E_COUNT_DTV_TV_EXCLUDE_NOT_VISIABLE_INCLUDE_DELETE] = stQueryTotal.m_u16NumOfDTVProg;
        }

         if(pMsrvDtv->GetProgramCount(E_EXCLUDE_NOT_VISIBLE_SKIPPED_AND_DELETED, stQueryTotal))
        {
            u32TotalNumber[E_COUNT_DTV_EXCLUDE_NOT_VISIABLE_SKIPPD_AND_DELETE] = stQueryTotal.m_u16NumOfTotalProg;
            u32TotalNumber[E_COUNT_DTV_RADIO_EXCLUDE_NOT_VISIABLE_SKIPPD_AND_DELETE] = stQueryTotal.m_u16NumOfRadioProg;
            u32TotalNumber[E_COUNT_DTV_DATA_EXCLUDE_NOT_VISIABLE_SKIPPD_AND_DELETE] = stQueryTotal.m_u16NumOfDataProg;
            u32TotalNumber[E_COUNT_DTV_TV_EXCLUDE_NOT_VISIABLE_SKIPPD_AND_DELETE] = stQueryTotal.m_u16NumOfDTVProg;
        }

        MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "UpdateProgramCount [DTV] = %d\n", u32TotalNumber[E_COUNT_DTV]);
        MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "DelNum [DTV] = %d\n", u32TotalNumber[E_COUNT_DTV_DELETE]);

    }
#if (STB_ENABLE == 0)
    else if(m_ATVDTVboth)
    {

        u32TotalNumber[E_COUNT_DTV] = 0;

        MSrv_DTV_Player *pMsrvDtv = _GetFocusMSrvDtv();
        if(pMsrvDtv->GetProgramCount(E_INCLUDE_ALL, stQueryTotal))
        {
            u32TotalNumber[E_COUNT_DTV] = stQueryTotal.m_u16NumOfTotalProg;
            u32TotalNumber[E_COUNT_DTV_RADIO] = stQueryTotal.m_u16NumOfRadioProg;
            u32TotalNumber[E_COUNT_DTV_DATA] = stQueryTotal.m_u16NumOfDataProg;
            u32TotalNumber[E_COUNT_DTV_TV] = stQueryTotal.m_u16NumOfDTVProg;
        }

        if(pMsrvDtv->GetProgramCount(E_DELETED, stQueryTotal))
        {
            u32TotalNumber[E_COUNT_DTV_DELETE] = stQueryTotal.m_u16NumOfTotalProg;
            u32TotalNumber[E_COUNT_DTV_RADIO_DELETE] = stQueryTotal.m_u16NumOfRadioProg;
            u32TotalNumber[E_COUNT_DTV_DATA_DELETE] = stQueryTotal.m_u16NumOfDataProg;
            u32TotalNumber[E_COUNT_DTV_TV_DELETE] = stQueryTotal.m_u16NumOfDTVProg;
        }

        if(pMsrvDtv->GetProgramCount(E_NOT_VISIBLE, stQueryTotal))
        {
            u32TotalNumber[E_COUNT_DTV_NOT_VISIABLE] = stQueryTotal.m_u16NumOfTotalProg;
            u32TotalNumber[E_COUNT_DTV_RADIO_NOT_VISIABLE] = stQueryTotal.m_u16NumOfRadioProg;
            u32TotalNumber[E_COUNT_DTV_DATA_NOT_VISIABLE] = stQueryTotal.m_u16NumOfDataProg;
            u32TotalNumber[E_COUNT_DTV_TV_NOT_VISIABLE] = stQueryTotal.m_u16NumOfDTVProg;
        }

         if(pMsrvDtv->GetProgramCount(E_EXCLUDE_NOT_VISIBLE_AND_DELETED, stQueryTotal))
        {
            u32TotalNumber[E_COUNT_DTV_EXCLUDE_NOT_VISIABLE_AND_DELETE] = stQueryTotal.m_u16NumOfTotalProg;
            u32TotalNumber[E_COUNT_DTV_RADIO_EXCLUDE_NOT_VISIABLE_AND_DELETE] = stQueryTotal.m_u16NumOfRadioProg;
            u32TotalNumber[E_COUNT_DTV_DATA_EXCLUDE_NOT_VISIABLE_AND_DELETE] = stQueryTotal.m_u16NumOfDataProg;
            u32TotalNumber[E_COUNT_DTV_TV_EXCLUDE_NOT_VISIABLE_AND_DELETE] = stQueryTotal.m_u16NumOfDTVProg;
        }

        if(pMsrvDtv->GetProgramCount(E_INCLUDE_NOT_VISIBLE_EXCLUDE_DELETED, stQueryTotal))
        {
            u32TotalNumber[E_COUNT_DTV_INCLUDE_NOT_VISIABLE_EXCLUDE_DELETE] = stQueryTotal.m_u16NumOfTotalProg;
            u32TotalNumber[E_COUNT_DTV_RADIO_INCLUDE_NOT_VISIABLE_EXCLUDE_DELETE] = stQueryTotal.m_u16NumOfRadioProg;
            u32TotalNumber[E_COUNT_DTV_DATA_INCLUDE_NOT_VISIABLE_EXCLUDE_DELETE] = stQueryTotal.m_u16NumOfDataProg;
            u32TotalNumber[E_COUNT_DTV_TV_INCLUDE_NOT_VISIABLE_EXCLUDE_DELETE] = stQueryTotal.m_u16NumOfDTVProg;
        }

        if(pMsrvDtv->GetProgramCount(E_EXCLUDE_NOT_VISIBLE_INCLUDE_DELETED, stQueryTotal))
        {
            u32TotalNumber[E_COUNT_DTV_EXCLUDE_NOT_VISIABLE_INCLUDE_DELETE] = stQueryTotal.m_u16NumOfTotalProg;
            u32TotalNumber[E_COUNT_DTV_RADIO_EXCLUDE_NOT_VISIABLE_INCLUDE_DELETE] = stQueryTotal.m_u16NumOfRadioProg;
            u32TotalNumber[E_COUNT_DTV_DATA_EXCLUDE_NOT_VISIABLE_INCLUDE_DELETE] = stQueryTotal.m_u16NumOfDataProg;
            u32TotalNumber[E_COUNT_DTV_TV_EXCLUDE_NOT_VISIABLE_INCLUDE_DELETE] = stQueryTotal.m_u16NumOfDTVProg;
        }

         if(pMsrvDtv->GetProgramCount(E_EXCLUDE_NOT_VISIBLE_SKIPPED_AND_DELETED, stQueryTotal))
        {
            u32TotalNumber[E_COUNT_DTV_EXCLUDE_NOT_VISIABLE_SKIPPD_AND_DELETE] = stQueryTotal.m_u16NumOfTotalProg;
            u32TotalNumber[E_COUNT_DTV_RADIO_EXCLUDE_NOT_VISIABLE_SKIPPD_AND_DELETE] = stQueryTotal.m_u16NumOfRadioProg;
            u32TotalNumber[E_COUNT_DTV_DATA_EXCLUDE_NOT_VISIABLE_SKIPPD_AND_DELETE] = stQueryTotal.m_u16NumOfDataProg;
            u32TotalNumber[E_COUNT_DTV_TV_EXCLUDE_NOT_VISIABLE_SKIPPD_AND_DELETE] = stQueryTotal.m_u16NumOfDTVProg;
        }



        U16 u16TotalNumver = 0;
        MSrv_Control::GetMSrvAtv()->GetTotalChannelNumber(&u16TotalNumver);
        u32TotalNumber[E_COUNT_ATV] = u16TotalNumver;

        MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "UpdateProgramCount [ATV] = %d\n", u32TotalNumber[E_COUNT_ATV]);
        MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "UpdateProgramCount [DTV] = %d\n", u32TotalNumber[E_COUNT_DTV]);
        MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "DelNum [DTV] = %d\n", u32TotalNumber[E_COUNT_DTV_DELETE]);
    }
    else if(m_ATVonly)
    {
        U16 u16TotalNumver = 0;
        MSrv_Control::GetMSrvAtv()->GetTotalChannelNumber(&u16TotalNumver);
        u32TotalNumber[E_COUNT_ATV] = u16TotalNumver;
        MSRV_CHANNELMANAGER_DVB_INFO(m_DBGInfoID, "UpdateProgramCount [ATV] = %d\n", u32TotalNumber[E_COUNT_ATV]);
    }
#endif

    return TRUE;
}
#if(NVOD_ENABLE==1)
U16 MSrv_ChannelManager_DVB::GetNvodReferenceServicesCount(void)
{
    return _GetFocusMSrvDtv()->GetNvodReferenceServicesCount();
}
U16 MSrv_ChannelManager_DVB::GetNvodReferenceServicesInfo( ST_CM_PROGRAM_INFO*pstProgBufferInfo, const U16 u16MaxProgNum)
{
    ST_DTV_PROGRAM_INFO* ProgBuf=new(std::nothrow)ST_DTV_PROGRAM_INFO[u16MaxProgNum];
    if(ProgBuf == NULL)
    {
        return 0;
    }
    U16 ProgramCount = _GetFocusMSrvDtv()->GetNvodReferenceServicesInfo(ProgBuf, u16MaxProgNum);
    U16 ProgCount=0;
    if(ProgramCount<u16MaxProgNum)
    {
        ProgCount=ProgramCount;
    }
    else
    {
        ProgCount=u16MaxProgNum;
    }

    for(U16 i=0;i<ProgCount;i++)
    {
        CopyDTVProgramToCMProgram(ProgBuf[i], pstProgBufferInfo[i]);
    }
    if(ProgBuf != NULL)
    {
        delete[] ProgBuf;
        ProgBuf = NULL;
    }
    return ProgramCount;
}
#endif
BOOL CopyDTVProgramToCMProgram(const ST_DTV_PROGRAM_INFO& rDTVProg, ST_CM_PROGRAM_INFO& rCMProg)
{
    rCMProg.unProgNumber.u32Number     =  rDTVProg.m_u16Number;
    rCMProg.u8Favorite   =  rDTVProg.m_u8Favorite;
    rCMProg.bIsScramble   =  rDTVProg.m_bIsScramble;
    rCMProg.bIsLock       =  rDTVProg.m_bIsLock;
    rCMProg.bIsSkip       =  rDTVProg.m_bIsSkip;
    rCMProg.bIsDelete     =  rDTVProg.m_bIsDelete;
    rCMProg.bIsHide       =  rDTVProg.m_bIsHide;
    rCMProg.u8ServiceType =  rDTVProg.m_u8ServiceType;
    rCMProg.u16TransportStream_ID = rDTVProg.m_u16TransportStream_ID;
    rCMProg.u16ServiceID = rDTVProg.m_u16ServiceID;
    rCMProg.u16OriginalNetwork_ID= rDTVProg.u16OriginalNetwork_ID;
    rCMProg.bIsVisible    =  rDTVProg.m_bIsVisible;
    rCMProg.sServiceName  =  rDTVProg.m_sServiceName;
    rCMProg.u32Frequency = rDTVProg.m_u32Frequency;
#if(NVOD_ENABLE==1)
    rCMProg.u8NvodTimeShiftServiceNum=rDTVProg.u8NvodRealSrvNum;
    for(int i=0;i<rDTVProg.u8NvodRealSrvNum;i++)
    {
        rCMProg.stTimeShiftedServices[i].u16OnId=rDTVProg.stNvodRealSrv[i].u16OnId;
        rCMProg.stTimeShiftedServices[i].u16TsId=rDTVProg.stNvodRealSrv[i].u16TsId;
        rCMProg.stTimeShiftedServices[i].u16SrvId=rDTVProg.stNvodRealSrv[i].u16SrvId;
    }
#endif
    return TRUE;
}

MSrv_DTV_Player_DVB* MSrv_ChannelManager_DVB::_GetFocusMSrvDtv(void)
{
    return MSrv_Control::GetInstance()->GetFocusMSrvDtv();
}

MSrv_ChannelManager_DVB::MS_CM_RTN_PROGRAM& MSrv_ChannelManager_DVB::_GetProgramReturn()
{
#if (PIP_ENABLE == 1)
    if(TRUE == MSrv_Control::GetInstance()->IsFocusOnSubSource())
    {
        return m_PipSubRTNProg;
    }
#endif
    return m_RTNProg;
}
struct ST_CH_CHG_INFO
{
    MSrv_ChannelManager_DVB* pCM;
    ST_CM_PROGRAM_INFO stPrevProg;
};

BOOL MSrv_ChannelManager_DVB::ChannelChangeDoneNotify(void* pParam, BOOL bResult)
{
    MSrv_ChannelManager_DVB* pCM = static_cast<MSrv_ChannelManager_DVB*>(pParam);
    if(pCM)
    {
        pCM->DoChannelChangeDoneNotify(bResult);
    }
    return TRUE;
}

BOOL MSrv_ChannelManager_DVB::DoChannelChangeDoneNotify(BOOL bResult)
{
    if(bResult)
    {
        if(TRUE == MSrv_Control::GetInstance()->IsFocusOnSubSource())
        {
            m_PipSubRTNProg = m_TempPipSubRTNProg;
        }
        else
        {
            m_RTNProg = m_TempRTNProg;
        }
    }
    _GetFocusMSrvDtv()->SetChannelChangeNotifier(NULL, NULL);
    return TRUE;
}

