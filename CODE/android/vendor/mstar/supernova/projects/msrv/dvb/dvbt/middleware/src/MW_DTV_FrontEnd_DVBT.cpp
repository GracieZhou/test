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
#include "MW_DTV_FrontEnd_DVBT.h"

// headers of standard C libs
#include <pthread.h>
#include <unistd.h>
#include <sys/prctl.h>
#include <math.h>

// headers of standard C++ libs

// headers of the same layer's
#include "MSrv_System_Database.h"
#include "MSrv_Control.h"

// headers of underlying layer's
#include "debug.h"
#include "mapi_types.h"
#include "mapi_pcb.h"
#include "mapi_tuner.h"
#include "mapi_interface.h"

#define MW_DTV_FRONTEND_FATAL(fmt, arg...)     printf((char *)fmt, ##arg)
#define MW_DTV_FRONTEND_ERROR(fmt, arg...)     printf((char *)fmt, ##arg)
#define MW_DTV_FRONTEND_WARNING(fmt, arg...)   printf((char *)fmt, ##arg)
#define MW_DTV_FRONTEND_INFO(fmt, arg...)      //printf((char *)fmt, ##arg)
#define MW_DTV_FRONTEND_FUNCTION(fmt, arg...)  //printf((char *)fmt, ##arg)
#define MW_DTV_FRONTEND_DBG(fmt, arg...)            //printf((char *)fmt, ##arg)

#ifndef GetStatusInterval
#define GetStatusInterval 200
#endif

MW_DTV_FrontEnd_DVBT::MW_DTV_FrontEnd_DVBT(BOOLEAN bT2Enable, mapi_demodulator *pTDemod, mapi_demodulator *pT2Demod, mapi_tuner *pTuner, mapi_demux *pDemux, U8 u8PathIdx)
{

    ASSERT(pTDemod);
    ASSERT(pTuner);
    m_init = FALSE;
    m_pcDemod = m_pcTDemod = pTDemod;
    m_pcT2Demod = NULL;
    m_pcTuner = pTuner;
    m_pcDemux = pDemux;

    m_u8FrontendPathIdx = u8PathIdx;

    if (MAPI_FALSE == m_pcDemod->Connect(mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T))
    {
        printf("[Wrong] DVBT Demod connect failed.\n");
    }
    if (MAPI_FALSE == m_pcDemod->Power_On_Initialization())
    {
        m_bDemodUnready = TRUE;
        return;
    }

    m_bT2Enabled = bT2Enable;
    if (bT2Enable == TRUE)
    {
        ASSERT(pT2Demod);
        if(pTDemod != pT2Demod)
        {
            m_pcT2Demod = pT2Demod;
            if (MAPI_FALSE == m_pcT2Demod->Connect(mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2))
            {
                printf("[Wrong] DVBT2 Demod connect failed.\n");
            }
            if (MAPI_FALSE == m_pcT2Demod->Power_On_Initialization())
            {
                m_bDemodUnready = TRUE;
            }
        }
        else
        {
            m_pcT2Demod = pT2Demod;
        }
    }
    if (MAPI_FALSE == SetDemodulatorType(mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T))
    {
        m_bDemodUnready = TRUE;
    }

    if (m_bDemodUnready == TRUE)
    {
        for (int i = 0; i < 10; i++)
        {
            printf("[Wrong] Demod initial failed, the DTV functional can't work. [file:%s, line:%d]\n", __FILE__, __LINE__);
        }
    }
    
    mapi_interface::Get_mapi_pcb()->SwitchToDvbT(u8PathIdx, m_pcDemux);

    m_eLockStatus = E_STATUS_UNLOCK;
    m_u32CurrTimeStamp = 0;

    m_enCurrentBandWidth = E_RF_CH_BAND_INVALID;
    m_u32CurrentFrequency = 0;
    m_u8CurrentPlpID = 0;
    m_enCurrentDemodType = mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T;
    m_bSeLLP=FALSE;
#if (AUTO_TEST == 1)
    bIsFirstLock = FALSE;
#endif
}

//-------------------------------------------------------------------------
//
//-------------------------------------------------------------------------
MW_DTV_FrontEnd_DVBT::~MW_DTV_FrontEnd_DVBT() //sttest
{
    MW_DTV_FRONTEND_FUNCTION("MW_DTV_FrontEnd_DVBT::~MW_DTV_FrontEnd_DVBT() \n ");
    ASSERT(m_pcDemod);
    m_pcTDemod->Disconnect();
    if ((m_bT2Enabled == TRUE) && (m_pcT2Demod != NULL))
    {
        m_pcT2Demod->Disconnect();
    }
}


//-------------------------------------------------------------------------
//for dvbt2 &dvbt use
//-------------------------------------------------------------------------
BOOL MW_DTV_FrontEnd_DVBT::Set(U32 u32Frequency, RF_CHANNEL_BANDWIDTH enBandWidth, U16 u16PlpID, BOOLEAN bLPsel)
{
    if (m_bDemodUnready == TRUE)
    {
        printf("[Error] Demod initial failed, can't set frequency, please check target board and software config.\n");
        return FALSE;
    }

    MW_DTV_FRONTEND_FUNCTION("MW_DTV_FrontEnd_DVBT::Set(%d,%d,%d) \n", u32Frequency, enBandWidth + 5, u16PlpID);

    BOOL bRet = TRUE;
    BOOL bPalBG;
/*
    if (m_bT2Enabled == FALSE)
    {
        return FALSE;
    }
*/
    if (enBandWidth == E_RF_CH_BAND_INVALID)
    {
        m_eLockStatus = E_STATUS_UNSUPPORT;
        return FALSE;
    }

    //printf("u32Frequency..............................%d %d\n",u32Frequency,bLPsel);

    if (MSrv_Control::GetMSrvSystemDatabase()->GetSystemCountry() == E_UK)
    {
        bPalBG = FALSE;
    }
    else
    {
        bPalBG = TRUE;
    }


    if ((m_init==FALSE) || (u32Frequency != m_u32CurrentFrequency) || (enBandWidth != m_enCurrentBandWidth))
    {
        bRet = mapi_interface::Get_mapi_pcb()->EnableTunerI2cPath(TRUE);
        if(bRet == FALSE)
        {
            MW_DTV_FRONTEND_ERROR("MW_DTV_FrontEnd_DVBT::DoSet_DVBT() IIC_Bypass_Mode(1) Error");
            return FALSE;
        }
#if (AUTO_TEST == 1)
        printf("\033[1;31m[AUTO_TEST][channel change]: Tuner lock Begin [PIU][%u]\033[0m\n", mapi_time_utility::GetPiuTimer1());
#endif
        if(m_pcDemod->GetCurrentDemodulatorType() == mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2)
            bRet = m_pcTuner->DTV_SetTune((double)u32Frequency / 1000, enBandWidth, E_TUNER_DTV_DVB_T2_MODE);
        else
            bRet = m_pcTuner->DTV_SetTune((double)u32Frequency / 1000, enBandWidth, E_TUNER_DTV_DVB_T_MODE);
        if(bRet == FALSE)
        {
            MW_DTV_FRONTEND_ERROR("MW_DTV_FrontEnd_DVBT::DoSet_DVBT() DTV_SetTune Error\n");
            return FALSE;
        }
        bRet = mapi_interface::Get_mapi_pcb()->EnableTunerI2cPath(FALSE);
        if(bRet == FALSE)
        {
            MW_DTV_FRONTEND_ERROR("MW_DTV_FrontEnd_DVBT::DoSet_DVBT() IIC_Bypass_Mode(0) Error \n");
            return FALSE;
        }
        if(m_pcDemod->GetCurrentDemodulatorType() == mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2)
        {
            bRet = m_pcDemod->DTV_DVB_T2_SetPlpGroupID((U8) (u16PlpID & DVB_T2_PLP_ID_MASK), 0xff);
        }
#if (AUTO_TEST == 1)
        printf("\033[1;31m[AUTO_TEST][channel change]: Demod lock Begin [PIU][%u]\033[0m\n", mapi_time_utility::GetPiuTimer1());
#endif
        bRet = m_pcDemod->DTV_SetFrequency(u32Frequency, enBandWidth, bPalBG, bLPsel);
        if(bRet == FALSE)
        {
            MW_DTV_FRONTEND_ERROR("MW_DTV_FrontEnd_DVBT::DoSet_DVBT() DTV_SetFrequency(%d,%d) Error", u32Frequency, enBandWidth);
            return FALSE;
        }
#if (FAST_CHANNEL_ALGORITHM_ENABLE == 1)
        m_pcDemux->resetFCA();
#endif
        //DVBT2 FIXME: Add Set u8PlpID to Demod or Tuner Here
    }
    else if ((bLPsel != m_bSeLLP) || (m_u8CurrentPlpID != (U8)(u16PlpID & DVB_T2_PLP_ID_MASK)) )
    {
        // In some case, it's not necessary to reset tuner. only reset demodulator.
        // DVBT,  high/low priority switch
        // DVBT2, plpid switch
        if(m_pcDemod->GetCurrentDemodulatorType() == mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2)
        {
            bRet = m_pcDemod->DTV_DVB_T2_SetPlpGroupID((U8) (u16PlpID & DVB_T2_PLP_ID_MASK), 0xff);
        }
        bRet = m_pcDemod->DTV_SetFrequency(u32Frequency, enBandWidth, bPalBG, bLPsel);
        if(bRet == FALSE)
        {
            MW_DTV_FRONTEND_ERROR("MW_DTV_FrontEnd_DVBT::DoSet_DVBT() DTV_SetFrequency(%d,%d) Error", u32Frequency, enBandWidth);
            return FALSE;
        }
    }
#if (AUTO_TEST == 1)
    bIsFirstLock = FALSE;
#endif
    m_bSeLLP=bLPsel;
    // chnage DVBT2 SW flow, plpid and groupid is pair each other. Just assign plpid is enough.
#if 0
    if((u16PlpID != DVB_T2_PLP_ID_INVALID) &&
        ((u32Frequency != m_u32CurrentFrequency) ||
        (enBandWidth != m_enCurrentBandWidth) ||
        ((U8) (u16PlpID & DVB_T2_PLP_ID_MASK) != m_u8CurrentPlpID)))
    {
        MAPI_U8 u8GroupId = 0;
        MAPI_U8 u8PlpId = (U8) (u16PlpID & DVB_T2_PLP_ID_MASK);

        if(m_pcDemod->GetCurrentDemodulatorType() == mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2)
        {
            MAPI_U16 u16Retry = 0;
            bRet = m_pcDemod->DTV_DVB_T2_GetPlpGroupID(u8PlpId, &u8GroupId);
            while((bRet == FALSE) && (u16Retry < 60))
            {
                u16Retry++;
                printf("DoSet_DVBT2 get groupid retry %d \n", u16Retry);
                usleep(100 * 1000);
                bRet = m_pcDemod->DTV_DVB_T2_GetPlpGroupID(u8PlpId, &u8GroupId);
            }

            if(bRet == FALSE)
            {
                m_u32CurrentFrequency = u32Frequency;
                m_enCurrentBandWidth = enBandWidth;
                MW_DTV_FRONTEND_ERROR("MW_DTV_FrontEnd_DVBT::DoSet_DVBT2() DTV_DVB_T2_GetPlpGroupID(%d) Error \n", u16PlpID);
                return TRUE;
            }

            bRet = m_pcDemod->DTV_DVB_T2_SetPlpGroupID(u8PlpId, u8GroupId);
            if(bRet == FALSE)
            {
                MW_DTV_FRONTEND_ERROR("MW_DTV_FrontEnd_DVBT::DoSet_DVBT2() DTV_DVB_T2_SetPlpGroupID(%d,%d) Error", u16PlpID, u8GroupId);
                return FALSE;
            }
        }
    }
#endif
    if(bRet)
    {
        m_init = TRUE;
        m_eLockStatus = E_STATUS_CHECKING;
        m_u32CurrentFrequency = u32Frequency;
        m_enCurrentBandWidth = enBandWidth;
        m_u8CurrentPlpID = (U8) (u16PlpID & DVB_T2_PLP_ID_MASK);
    }


    m_u32CurrTimeStamp = (U32) mapi_time_utility::GetTime0();
    m_eLockStatus = E_STATUS_CHECKING;
    return TRUE;
}

BOOL MW_DTV_FrontEnd_DVBT::SetPlpID(U8 u8PlpID)
{
    return Set(m_u32CurrentFrequency, m_enCurrentBandWidth, u8PlpID, FALSE);
}

//-------------------------------------------------------------------------
//
//-------------------------------------------------------------------------
BOOL MW_DTV_FrontEnd_DVBT::Stop(void)
{
    m_eLockStatus = E_STATUS_UNLOCK;
    m_u32CurrTimeStamp = (U32) mapi_time_utility::GetTime0();
    m_init = FALSE;
    return TRUE;
}
//-------------------------------------------------------------------------
//
//-------------------------------------------------------------------------
BOOL MW_DTV_FrontEnd_DVBT::Reset(void)
{
    m_eLockStatus = E_STATUS_UNLOCK;
    m_init = FALSE;
    m_u32CurrTimeStamp = (U32) mapi_time_utility::GetTime0();
    return TRUE;
}

//-------------------------------------------------------------------------
//
//-------------------------------------------------------------------------
MW_DTV_FrontEnd_DVB::EN_FE_STATUS MW_DTV_FrontEnd_DVBT::GetStatus(void)
{
    if((mapi_time_utility::TimeDiffFromNow0(m_u32CurrTimeStamp) > GetStatusInterval) && (m_init != FALSE) )
    {
        mapi_demodulator_datatype::EN_LOCK_STATUS enLockStatus;

        enLockStatus = m_pcDemod->DTV_GetLockStatus();
        switch(enLockStatus)
        {
            case mapi_demodulator_datatype::E_DEMOD_LOCK:
                m_eLockStatus = E_STATUS_LOCK;
                #if (AUTO_TEST == 1)
                if (FALSE == bIsFirstLock)
                {
                    bIsFirstLock = TRUE;
                    printf("\033[1;31m[AUTO_TEST][channel change]: Demod lock Done [PIU][%u]\033[0m\n", mapi_time_utility::GetPiuTimer1());
                }
                #endif
                break;
            case mapi_demodulator_datatype::E_DEMOD_CHECKING:
            case mapi_demodulator_datatype::E_DEMOD_CHECKEND:
                m_eLockStatus = E_STATUS_CHECKING;
                break;
            case mapi_demodulator_datatype::E_DEMOD_UNLOCK:
                m_eLockStatus = E_STATUS_UNLOCK;
                break;
            default:
                m_eLockStatus = E_STATUS_UNSUPPORT;
                break;
        }

        m_u32CurrTimeStamp = (U32) mapi_time_utility::GetTime0();
    }
    return m_eLockStatus;
}
//-------------------------------------------------------------------------
//
//-------------------------------------------------------------------------
U16 MW_DTV_FrontEnd_DVBT::GetSignalQuality(void)
{
    return m_pcDemod->DTV_GetSignalQuality();
}
//-------------------------------------------------------------------------
//
//-------------------------------------------------------------------------
U16 MW_DTV_FrontEnd_DVBT::GetSignalStrength(void)
{
    return m_pcDemod->DTV_GetSignalStrength();
}
//-------------------------------------------------------------------------
//
//-------------------------------------------------------------------------
U16 MW_DTV_FrontEnd_DVBT::GetCellID(void)
{
    return m_pcDemod->DTV_GetCellID();
}
BOOL MW_DTV_FrontEnd_DVBT::IsHierarchyOn(void)
{

    return m_pcDemod->DTV_IsHierarchyOn();
}
BOOL MW_DTV_FrontEnd_DVBT::IsHPCoding(void)
{
    return (m_bSeLLP == TRUE) ? FALSE : TRUE;
}

//-------------------------------------------------------------------------
//
//-------------------------------------------------------------------------
vector<U8 > MW_DTV_FrontEnd_DVBT::GetPlpIDList(void)
{
    vector<U8> PlpIDList;
    MAPI_BOOL bRet;
    MAPI_U8 u8PlpBitMap[32];

    MW_DTV_FRONTEND_FUNCTION("MW_DTV_FrontEnd_DVBT::GetPlpIDList() \n");
    memset(u8PlpBitMap, 0, sizeof(u8PlpBitMap));

    bRet = m_pcDemod->DTV_DVB_T2_GetPlpBitMap(u8PlpBitMap);

    if (bRet == MAPI_FALSE)
        return PlpIDList;

    for(int i = 0; i < 32; i++)
    {
        for(int j = 0; j < 8; j++)
            if((u8PlpBitMap[i] >> j) & 1)
                PlpIDList.push_back(i * 8 + j);
    }

    return PlpIDList;
}

//-------------------------------------------------------------------------
//
//-------------------------------------------------------------------------
mapi_demodulator *MW_DTV_FrontEnd_DVBT::GetCurrentDemodulator(void)
{
    return m_pcDemod;
}

//-------------------------------------------------------------------------
//
//-------------------------------------------------------------------------
mapi_demodulator_datatype::EN_DEVICE_DEMOD_TYPE MW_DTV_FrontEnd_DVBT::GetDemodulatorType(void)
{
    return m_pcDemod->GetCurrentDemodulatorType();
}

//-------------------------------------------------------------------------
//
//-------------------------------------------------------------------------
MAPI_BOOL MW_DTV_FrontEnd_DVBT::SetDemodulatorType(mapi_demodulator_datatype::EN_DEVICE_DEMOD_TYPE enDemodType)
{
    if (enDemodType == mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2)
    {
        ASSERT(m_bT2Enabled);
        m_pcDemod = m_pcT2Demod;
        mapi_interface::Get_mapi_pcb()->SwitchToDvbT2(m_u8FrontendPathIdx, m_pcDemux);
    }
    else
    {
        m_pcDemod = m_pcTDemod;
        mapi_interface::Get_mapi_pcb()->SwitchToDvbT(m_u8FrontendPathIdx, m_pcDemux);
    }
    m_init = FALSE;
    m_enCurrentDemodType = enDemodType;
    return m_pcDemod->SetCurrentDemodulatorType(enDemodType);
}

