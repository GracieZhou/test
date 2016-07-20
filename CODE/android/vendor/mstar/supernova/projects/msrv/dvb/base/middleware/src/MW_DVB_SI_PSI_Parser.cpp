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

// headers of itself
#include "MW_DVB_SI_PSI_Parser.h"

// headers of standard C libs
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <signal.h>
#include <sys/time.h>
#include <time.h>
#include <typeinfo>
#include <sys/prctl.h>
#include <limits.h>

// headers of standard C++ libs

// headers of the same layer's
#include "MSrv_Control.h"
#include "MW_DTV_Program_DVB.h"
#include "MW_DTV_CM_DB.h"
#include "MW_DVB_HuffmanTable.h"
#if ((ISDB_SYSTEM_ENABLE == 1) || (CI_PLUS_ENABLE == 1))
#include "MW_DTV_FreqTable_DVB.h"
#endif

// headers of underlying layer's
#include "mapi_types.h"
#include "mapi_utility.h"
#include "mapi_interface.h"
#include "mapi_system.h"
#include "mapi_demux.h"
#include "mapi_si_dvb.h"
#include "mapi_si_ait.h"
#include "mapi_dvb_utility.h"
#include "mapi_si_ait_parser.h"
#if (CA_ENABLE == 1)
#include "MSrv_CA.h"
#endif
#if (STR_ENABLE == 1)
#include "mapi_str.h"
#endif


#if (OAD_ENABLE == 1)
#include "mapi_oad_parser.h"
#include "MW_OAD_Parser.h"
#endif //(OAD_ENABLE == 1)

#include "SystemInfo.h"


#define EIT_BUFFER_DBG 0


#define MW_SI_PARSER_FATAL(fmt, arg...)          //printf((char *)fmt, ##arg)
#define MW_SI_PARSER_MESSAGE(fmt, arg...)         // printf((char *)fmt, ##arg)
#define MW_SI_PARSER_ERROR(fmt, arg...)          //printf((char *)fmt, ##arg)
#define MW_SI_PARSER_WARNING(fmt, arg...)          //printf((char *)fmt, ##arg)
#define MW_SI_PARSER_UPDATE(fmt, arg...)        //printf((char *)fmt, ##arg)
#define MW_SI_PARSER_SCAN(fmt, arg...)           //printf((char *)fmt, ##arg)
#define MW_SI_TARGET_REGION_DBG(fmt, arg...)          // printf((char *)fmt, ##arg)
#define MW_SI_NIT_AUTOUPDATE_DBG(fmt, arg...)          //printf((char *)fmt, ##arg)
#define MW_SI_PARSER_QUICK_SCAN(fmt, arg...)           //printf((char *)fmt, ##arg)
#define MW_SI_CIPLUS_SERVICE_DBG(fmt, arg...)          //printf((char *)fmt, ##arg)
#define MW_SI_SGT_SCAN_DEBUG(fmt,arg...)               ///printf((char *)fmt, ##arg)

#define IS_DEFAULT_BROADCAST_MIX_AD_PROVIDER(c)            ((EN_CABLE_OPERATORS)c==EN_CABLEOP_CABLEREADY)
#define IS_TABLE_VERSION_VALID(table) (table.u8Version != INVALID_PSI_SI_VERSION)
#define ITER_EACH(iter, iterable) for(typeof((iterable).begin()) iter = (iterable).begin(); iter != (iterable).end(); iter++)

#define MONITOR_NOTIFY(a,b,c,d)    \
    if(NULL != _pfMonitorNotify)  \
    {   \
        _pfMonitorNotify(a,b,c,d);    \
    }

#if EIT_BUFFER_DBG
static MAPI_U32 _u32BufferAllocCnt = 0;
static MAPI_U32 _u32BufferFreeCnt = 0;
#endif

#define RESET_ALL   0xFF
#define RESET_INFO   0x01
#define RESET_EIT   0x02
#define RESET_RCT   0x08
#define RESET_SDT_OTHER   0x04

#define HBBTV_APP_TYPE 0x0010

#define SI_SECONDS_PER_HOUR 3600L
#define SI_SECONDS_PER_MIN  60L


#define SI_PSI_FORCE_UPDATE_VER (INVALID_PSI_SI_VERSION-1)
#define INVALID_TIME 0
#define INVALID_PMT_PID 0
#define EIT_PF_CONTINUE_MODE 1
typedef enum
{
    EN_DVB_SCAN_START,
    EN_DVB_MONITOR_START,
    EN_DVB_OAD_SCAN_START,
    EN_DVB_PARSER_STOP,
    EN_DVB_PARSER_STOP_ALL,
    EN_DVB_PARSER_STOP_CLOCK,
    EN_DVB_SCAN_FINISH,
    EN_DVB_RESET_AIT,
    EN_DVB_QUICK_SCAN_START,
    EN_DVB_EPG_BARKERCHANNEL_START,
    EN_DVB_OAD_GO_DOWNLOAD,
    EN_DVB_FILE_IN_MONITOR_START,
    EN_DVB_FILE_IN_MONITOR_STOP,
    EN_DVB_EPG_UPDATE_START,
    EN_DVB_EPG_UPDATE_STOP,
    EN_DVB_SGT_SCAN_START,
} EN_DVB_PARSER_EVENT;


typedef enum
{
    EN_VER_SDT,
    EN_VER_PAT,
    EN_VER_PMT,
    EN_VER_NIT,
    EN_VER_BAT,
    EN_VER_SGT,
} EN_VER_TYPE;
typedef enum
{
    EN_ID_SID,
    EN_ID_ONID,
    EN_ID_TSID,
    EN_ID_NID,
    EN_ID_PMT,
} EN_ID_TYPE;
#define DATA_BC_ID_SSU              0x000A // DVB-SSU
#define DATA_BC_ID_UK_EC        0x0111 // UK Engineering Channel

#define BCD2Dec(x)      ((((x) >> 4) * 10) + ((x) & 0x0F))

#define TSGetU16(ptr)                       ((((const MAPI_U8 *)ptr)[0] << 8) + ((const MAPI_U8 *)ptr)[1])

#define CLEAR_TRIGGER_EVENT(a,b)   \
    MW_SI_PARSER_MESSAGE("clear trigger event start\n");    \
    while(a->Wait(b,0) == 0);   \
    MW_SI_PARSER_MESSAGE("clear trigger event end\n");


#define DATA_READY(func,a,b,c,d) func((DVB_PROG*)a,(DVB_CM*)b,(DVB_MUX*)c,(DVB_NETWORK*)d)

#define GET_CUR_PROG(prog,cm)   \
    {   \
        MW_DTV_CM_DB_scope_lock lock((DVB_CM *)m_pCMDB);   \
        prog = ((DVB_CM *)cm)->GetCurr();  \
    }

#define IS_PROG_EXIST(TSID,ONID,SID,bExist) \
    {   \
        MW_DTV_CM_DB_scope_lock lock((DVB_CM *)m_pCMDB);   \
        bExist = (NULL != ((DVB_CM *)m_pCMDB)->GetByID(TSID, ONID, SID)) ? MAPI_TRUE : MAPI_FALSE;  \
    }
#define IS_ONE_SEG_SERVICE(a,b) (((a==0x1FC8) && ((b & 0x07)==0)) || \
    ((a==0x1FC9) && ((b & 0x07)==1)) || \
    ((a==0x1FCA) && ((b & 0x07)==2)) || \
    ((a==0x1FCB) && ((b & 0x07)==3)) || \
    ((a==0x1FCC) && ((b & 0x07)==4)) || \
    ((a==0x1FCD) && ((b & 0x07)==5)) || \
    ((a==0x1FCE) && ((b & 0x07)==6)) || \
    ((a==0x1FCF) && ((b & 0x07)==7)))

#if (DVBS_SYSTEM_ENABLE ==1 )
#define GETSAT(a,b) ((m_eParserType != MW_DVB_S_PARSER) ? NULL:__GetSat((DVB_MUX*)a,(DVB_CM *)b))

MAPI_U16  const AutoUpdateSatellite[]=
{
    /// For Canal Digital auto update
    192,
    235,
    282,
    3592,
    /// for test
    //130,
};
#endif

/// Start NID for KDG NIT scan
#define KDG_START_NID    0xF001
/// End NID for KDG NIT scan
#define KDG_END_NID    0xF01F

static void _FreeCIProtectInfo(MAPI_SI_TABLE_SDT* pSdt);
static void _FreeLogoInfo(MAPI_SI_TABLE_SDT* pSdt);
static void _FreeRegionInfo(MAPI_SI_TABLE_SDT* pSdt = NULL, MAPI_SI_TABLE_NIT* pNit= NULL);
static void _FreeLCNv2Info(MAPI_SI_TABLE_NIT* pNit= NULL);

#if(CA_ENABLE == 1)
MSrv_CA * MW_DVB_SI_PSI_Parser::m_pCaClient = NULL;
#endif
#if (OAD_ENABLE == 1)
//TODO, only check NorDig country now
static MAPI_BOOL __IsONIDMatchCountry(MAPI_U16 u16ONID, MEMBER_COUNTRY eCountry)
{
    MAPI_BOOL bRet=MAPI_FALSE;
    switch(eCountry)
    {
        case E_FINLAND:
            if(u16ONID == FINLAND_ONID)
            {
                bRet = MAPI_TRUE;
            }
            break;
        case E_SWEDEN:
            if(u16ONID == SWEDEN_ONID)
            {
                bRet = MAPI_TRUE;
            }
            break;
        case E_DENMARK:
            if(u16ONID == DENMARK_ONID)
            {
                bRet = MAPI_TRUE;
            }
            break;
        case E_NORWAY:
            if(u16ONID == NORWAY_ONID)
            {
                bRet = MAPI_TRUE;
            }
            break;
        case E_IRELAND:
            if(u16ONID == IRELAND_ONID)
            {
                bRet = MAPI_TRUE;
            }
            break;
        default:
            MW_SI_PARSER_ERROR("TODO: only check NorDig Country\n");
            break;
    }
    return bRet;
}
#endif


static MAPI_U8 __GetVer(DVB_PROG* pProg, EN_VER_TYPE eType)
{
    MAPI_U8 u8Ver = INVALID_PSI_SI_VERSION;
    ASSERT(pProg);
    switch(eType)
    {
        case EN_VER_SDT:
            u8Ver = pProg->stPSI_SI_Version.u8SDTVer;
            break;
        case EN_VER_PAT:
            u8Ver = pProg->stPSI_SI_Version.u8PATVer;
            break;
        case EN_VER_PMT:
            u8Ver = pProg->stPSI_SI_Version.u8PMTVer;
            break;
        case EN_VER_NIT:
            u8Ver = pProg->stPSI_SI_Version.u8NITVer;
            break;
        case EN_VER_BAT:
            u8Ver = pProg->stPSI_SI_Version.u8BATVer;
            break;
        case EN_VER_SGT:
            u8Ver = pProg->stPSI_SI_Version.u8SGTVer;
            break;
    }
    return u8Ver;
}
static void __SetVer(DVB_PROG* pProg, EN_VER_TYPE eType, MAPI_U8 u8Ver)
{
    ASSERT(pProg);
    switch(eType)
    {
        case EN_VER_SDT:
            pProg->stPSI_SI_Version.u8SDTVer = u8Ver;
            break;
        case EN_VER_PAT:
            pProg->stPSI_SI_Version.u8PATVer = u8Ver;
            break;
        case EN_VER_PMT:
            pProg->stPSI_SI_Version.u8PMTVer = u8Ver;
            break;
        case EN_VER_NIT:
            pProg->stPSI_SI_Version.u8NITVer = u8Ver;
            break;
        case EN_VER_BAT:
            pProg->stPSI_SI_Version.u8BATVer = u8Ver;
            break;
        case EN_VER_SGT:
            pProg->stPSI_SI_Version.u8SGTVer = u8Ver;
            break;
    }
}
static MAPI_U16 __GetID(DVB_PROG* pProg, DVB_MUX *pMux, DVB_NETWORK *pNetwork, EN_ID_TYPE eType)
{
    MAPI_U16 u16ID = 0;

    if (NULL != pProg)
    {
        switch(eType)
        {
            case EN_ID_SID:
                u16ID = pProg->u16ServiceID;
                break;
            case EN_ID_PMT:
                u16ID = pProg->u16PmtPID;
                break;
            case EN_ID_ONID:
                u16ID = pMux->u16OriginalNetwork_ID;
                break;
            case EN_ID_TSID:
                u16ID = pMux->u16TransportStream_ID;
                break;
            case EN_ID_NID:
                u16ID = pNetwork->m_u16NetworkID;
                break;
        }
    }
    return u16ID;
}
 void __SetID(DVB_PROG* pProg, DVB_MUX *pMux, DVB_NETWORK *pNetwork, EN_ID_TYPE eType, MAPI_U16 u16ID)
{
    ASSERT(pProg);

    switch(eType)
    {
        case EN_ID_SID:
            pProg->u16ServiceID = u16ID;
            break;
        case EN_ID_PMT:
            pProg->u16PmtPID = u16ID;
            break;
        case EN_ID_ONID:
            pMux->u16OriginalNetwork_ID = u16ID;
            break;
        case EN_ID_TSID:
            pMux->u16TransportStream_ID = u16ID;
            break;
        case EN_ID_NID:
            pNetwork->m_u16NetworkID = u16ID;
            break;
    }
}
 DVB_MUX* __GetMux(DVB_PROG* pProg, DVB_CM* pCM)
{
    if((pProg == NULL) || (pCM == NULL))
    {
        return NULL;
    }
    return  pCM->GetMux(pProg->m_u16MuxTableID);
}


 DVB_NETWORK* __GetNetwork(DVB_MUX* pMux, DVB_CM* pCM)
{
    if((pMux == NULL) || (pCM == NULL))
    {
        return NULL;
    }
    return  pCM->GetNetwork(pMux->m_u16NetworkTableID);
}

#if (DVBS_SYSTEM_ENABLE == 1)
static DVB_SAT* __GetSat(DVB_MUX* pMux, DVB_CM* pCM)
{
    if((pMux == NULL) || (pCM == NULL))
    {
        return NULL;
    }
    return  pCM->GetSat(pMux->m_u16SatTableID);
}
#endif


 static MAPI_BOOL __UpdateService(DVB_PROG* pProg, MW_DVB_SI_ServicelInfo* pNewProg, DVB_CM* pCMDB, MAPI_BOOL bSave)
{
    //The following data comes from PMT, therefore we should check if pmt table is ready
    if(pNewProg->u8PmtVersion == INVALID_PSI_SI_VERSION)
    {
        return FALSE;
    }

    MAPI_BOOL bChange = MAPI_FALSE;
    ASSERT(pProg);
    if(pProg->u16PCRPID != pNewProg->wPCRPid)
    {
        pProg->u16PCRPID = pNewProg->wPCRPid;
        bChange = MAPI_TRUE;
        MW_SI_PARSER_UPDATE("PCR change\n");
    }
    if(pProg->u16VideoPID != pNewProg->stVideoInfo.wVideoPID)
    {
        pProg->u16VideoPID = pNewProg->stVideoInfo.wVideoPID;
        bChange = MAPI_TRUE;
        MW_SI_PARSER_UPDATE("video change\n");
    }
    if(pProg->stCHAttribute.u8VideoType != pNewProg->stVideoInfo.bVideoType)
    {
        pProg->stCHAttribute.u8VideoType = pNewProg->stVideoInfo.bVideoType;
        bChange = MAPI_TRUE;
        MW_SI_PARSER_UPDATE("video type change\n");
    }
    for(int i=0;i<MAX_AUD_LANG_NUM;i++)
    {
        if(memcmp(&pProg->stAudInfo[i], &pNewProg->stAudInfo[i], sizeof(AUD_INFO)))
        {
            if(!((pProg->stAudInfo[i].u16AudPID == pNewProg->stAudInfo[i].u16AudPID)
                &&(pProg->stAudInfo[i].u8AudType == pNewProg->stAudInfo[i].u8AudType)
                &&(pProg->stAudInfo[i].aISOLangInfo.u8AudType == pNewProg->stAudInfo[i].aISOLangInfo.u8AudType)
                &&(pProg->stAudInfo[i].bBroadcastMixAD == pNewProg->stAudInfo[i].bBroadcastMixAD)
                &&(pProg->stAudInfo[i].u8AACType == pNewProg->stAudInfo[i].u8AACType)
                &&(pProg->stAudInfo[i].u8AACProfileAndLevel == pNewProg->stAudInfo[i].u8AACProfileAndLevel)
                &&(pProg->stAudInfo[i].aISOLangInfo.u8IsValid == MAPI_TRUE)
                &&(pProg->stAudInfo[i].bInValid == pNewProg->stAudInfo[i].bInValid)
                &&(pNewProg->stAudInfo[i].aISOLangInfo.u8IsValid == MAPI_FALSE)))
            {
                memcpy(pProg->stAudInfo, pNewProg->stAudInfo, sizeof(AUD_INFO)*MAX_AUD_LANG_NUM);
                bChange = MAPI_TRUE;
                MW_SI_PARSER_UPDATE("audio change\n");
                break;
            }
        }
    }
    if(pProg->stCHAttribute.u8IsServiceIdOnly != pNewProg->bIsSIDOnly)
    {
        pProg->stCHAttribute.u8IsServiceIdOnly = pNewProg->bIsSIDOnly;
        bChange = MAPI_TRUE;
        MW_SI_PARSER_UPDATE("SID only change\n");
    }
    if(pProg->stCHAttribute.u8IsScramble != pNewProg->bIsCAExist)
    {
        pProg->stCHAttribute.u8IsScramble = pNewProg->bIsCAExist;
        bChange = MAPI_TRUE;
        MW_SI_PARSER_UPDATE("CA Exist change\n");
    }
    if (pProg->stCHAttribute.u8IsMHEGIncluded != pNewProg->bMHEG5Service)
    {
        pProg->stCHAttribute.u8IsMHEGIncluded = pNewProg->bMHEG5Service;
        bChange = MAPI_TRUE;
        MW_SI_PARSER_UPDATE("MHEG5 or Gina service change\n");
    }
    if(bChange)
    {
        MW_SI_PARSER_UPDATE("program change %s\n", bSave ? "save" : "no save");
        if(bSave)
        {
            MW_DTV_CM_DB_scope_lock lock(pCMDB);
            pCMDB->Update(pProg);
        }
    }
    else
    {
        MW_SI_PARSER_MESSAGE("program not change\n");
    }
    return bChange;
}
void MW_DVB_SI_PSI_Parser::__UpdateProviderName(DVB_PROG* pProg, MAPI_U8* pName, DVB_CM* pCMDB)
{
    ASSERT(pProg);
    if(memcmp(pProg->u8ServiceProviderName, pName, min(MAPI_SI_MAX_PROVIDER_NAME , MAX_PROVIDER_NAME)))
    {
        MW_SI_PARSER_UPDATE("service privoder name change  %.*s => %.*s\n", MAX_PROVIDER_NAME, pProg->u8ServiceProviderName, MAPI_SI_MAX_PROVIDER_NAME, pName);
        memcpy(pProg->u8ServiceProviderName, pName, min(MAPI_SI_MAX_PROVIDER_NAME , MAX_PROVIDER_NAME));
        MW_DTV_CM_DB_scope_lock lock(pCMDB);
        pCMDB->Update(pProg);
    }
}
void MW_DVB_SI_PSI_Parser::__UpdateName(DVB_PROG* pProg, MAPI_U8* pName, DVB_CM* pCMDB)
{
    ASSERT(pProg);
    if(memcmp(pProg->u8ServiceName, pName, (MAPI_SI_MAX_SERVICE_NAME > MAX_SERVICE_NAME) ? MAX_SERVICE_NAME : MAPI_SI_MAX_SERVICE_NAME) && (pProg->stCHAttribute.u8IsReName != TRUE))
    {
        MW_SI_PARSER_UPDATE("service name change  %.*s => %.*s\n", MAX_SERVICE_NAME, pProg->u8ServiceName, MAPI_SI_MAX_SERVICE_NAME, pName);
        memcpy(pProg->u8ServiceName, pName, (MAPI_SI_MAX_SERVICE_NAME > MAX_SERVICE_NAME) ? MAX_SERVICE_NAME : MAPI_SI_MAX_SERVICE_NAME);
        MW_DTV_CM_DB_scope_lock lock(pCMDB);
        pCMDB->Update(pProg);
    }
}

#if (MULTIPLE_SERVICE_NAME_ENABLE == 1)
static void __UpdateMultiName(DVB_PROG* pProg, MAPI_U8 (*pName)[MAPI_SI_MAX_SERVICE_NAME], MEMBER_LANGUAGE* pLangIndex, MAPI_U8 u8MultiServiceNameCnt, DVB_CM* pCMDB)
{
    ASSERT(pProg);
    MAPI_BOOL bUpdate = MAPI_FALSE;

    if(u8MultiServiceNameCnt != pProg->u8MultiServiceNameCnt)
    {
        bUpdate = MAPI_TRUE;
    }
    pProg->u8MultiServiceNameCnt = (u8MultiServiceNameCnt > MAX_MULTILINGUAL_SERVICE_NAME) ? MAX_MULTILINGUAL_SERVICE_NAME : u8MultiServiceNameCnt;

    for(MAPI_U8 i=0; i<pProg->u8MultiServiceNameCnt; i++)
    {
        if((pLangIndex[i] != pProg->aeLangIndex[i]) || (memcmp(pProg->u8MultiServiceName, pName[i], (MAPI_SI_MAX_SERVICE_NAME > MAX_SERVICE_NAME) ? MAX_SERVICE_NAME : MAPI_SI_MAX_SERVICE_NAME)))
        {
            pProg->aeLangIndex[i] = pLangIndex[i];
            memcpy(pProg->u8MultiServiceName[i], pName[i], (MAPI_SI_MAX_SERVICE_NAME > MAX_SERVICE_NAME) ? MAX_SERVICE_NAME : MAPI_SI_MAX_SERVICE_NAME);
            bUpdate = MAPI_TRUE;
        }
    }

    if(bUpdate == MAPI_TRUE)
    {
        MW_DTV_CM_DB_scope_lock lock(pCMDB);
        pCMDB->Update(pProg);
    }
}
#endif

static MAPI_BOOL __UpdateReplacement(DVB_PROG* pProg,  DVB_CM* pCMDB)
{
    ASSERT(pProg);
    MW_SI_PARSER_UPDATE("__UpdateReplacement\n");
    if(pProg->stCHAttribute.u8IsReplaceDel)
    {
        MW_DTV_CM_DB_scope_lock lock(pCMDB);
        pProg->stCHAttribute.u8IsDelete = MAPI_FALSE;
        pProg->stCHAttribute.u8IsReplaceDel = MAPI_FALSE;
        pCMDB->Update(pProg);
        return MAPI_TRUE;
    }
    return MAPI_FALSE;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::_UpdateProgram(DVB_PROG *pService, MW_DVB_PROGRAM_INFO *pServiceInfo)
{
    pService->u16PCRPID = pServiceInfo->u16PCRPid;
    pService->u16LCN = pServiceInfo->u16LCN;
    pService->u16PmtPID = pServiceInfo->u16PmtPID;
    pService->u16ServiceID = pServiceInfo->u16ServiceID;
    pService->u16SimuLCN = pServiceInfo->u16SimuLCN;

    pService->stPSI_SI_Version.u8NITVer = pServiceInfo->u8NitVer;
    pService->stPSI_SI_Version.u8PATVer = pServiceInfo->u8PatVer;
    pService->stPSI_SI_Version.u8PMTVer = pServiceInfo->u8PmtVer;
    pService->stPSI_SI_Version.u8SDTVer = pServiceInfo->u8SdtVer;

#if (ASTRA_SGT_ENABLE == 1)
    pService->stPSI_SI_Version.u8SGTVer = pServiceInfo->u8SgtVer;
#endif
    //pstChnInfo->m_stProgramInfo.stCHAttribute.u16SignalStrength = 0xFFFE;
    pService->stCHAttribute.u8VisibleServiceFlag = pServiceInfo->bIsVisible;
    pService->stCHAttribute.u8NumericSelectionFlag = pServiceInfo->bIsSelectable;
    //pstChnInfo->m_stProgramInfo.stCHAttribute.u8IsDelete = DEFAULT_IS_DELETED;
    //pstChnInfo->m_stProgramInfo.stCHAttribute.u8IsMove = DEFAULT_IS_MOVED;
    pService->stCHAttribute.u8IsScramble = pServiceInfo->bIsCAExist;
    //pstChnInfo->m_stProgramInfo.stCHAttribute.u8IsSkipped = DEFAULT_IS_SKIPPED;
    //pstChnInfo->m_stProgramInfo.stCHAttribute.u8IsLock = DEFAULT_IS_LOCKED;
    pService->stCHAttribute.u8IsStillPicture = pServiceInfo->stVideoInfo.bStillPic;
    pService->stCHAttribute.u8IsMHEGIncluded = pServiceInfo->bIsDataBroadcastService;


    pService->stCHAttribute.u8IsServiceIdOnly = pServiceInfo->bIsServiceIDOnly;
    pService->stCHAttribute.u8IsDataServiceAvailable = pServiceInfo->bIsDataService;
    //pstChnInfo->m_stProgramInfo.stCHAttribute.u8IsReplaceDel = MAPI_FALSE;
    pService->stCHAttribute.u8ServiceType = pServiceInfo->u8ServiceType;
    pService->stCHAttribute.u8ServiceTypePrio = pServiceInfo->u8ServiceTypePrio;
    pService->u16VideoPID = pServiceInfo->stVideoInfo.wVideoPID;
    pService->stCHAttribute.u8VideoType = pServiceInfo->stVideoInfo.bVideoType;
    pService->stCHAttribute.u8IsSpecialSrv = pServiceInfo->bIsSpecialSrv;
    //pstChnInfo->m_stProgramInfo.stCHAttribute.u8IsFavorite = DEFAULT_IS_FAVORITE;

    memcpy(pService->stAudInfo, pServiceInfo->stAudInfo, MAX_AUD_LANG_NUM * sizeof(AUD_INFO));
    //pstChnInfo->stCHAttribute.u8InvalidCell = pDTVProgramData->stCHAttribute.bInvalidCell;
    //pstChnInfo->stCHAttribute.u8UnconfirmedService = pDTVProgramData->stCHAttribute.bUnconfirmedService;

    memcpy(pService->u8ServiceName, pServiceInfo->au8ServiceName, (MAPI_SI_MAX_SERVICE_NAME > MAX_SERVICE_NAME) ? MAX_SERVICE_NAME : MAPI_SI_MAX_SERVICE_NAME);
    memcpy(pService->u8ServiceProviderName, pServiceInfo->au8ServiceProviderName, min(MAPI_SI_MAX_PROVIDER_NAME, MAX_PROVIDER_NAME));
#if (MULTIPLE_SERVICE_NAME_ENABLE == 1)
    pService->u8MultiServiceNameCnt = (pServiceInfo->u8MultiServiceNameCnt > MAX_MULTILINGUAL_SERVICE_NAME) ? MAX_MULTILINGUAL_SERVICE_NAME : pServiceInfo->u8MultiServiceNameCnt;
    for(U8 i=0; i<pServiceInfo->u8MultiServiceNameCnt; i++)
    {
        pService->aeLangIndex[i] = pServiceInfo->aeLangIndex[i];
        memcpy(pService->u8MultiServiceName[i], pServiceInfo->u8MultiServiceName[i], (MAPI_SI_MAX_SERVICE_NAME > MAX_SERVICE_NAME) ? MAX_SERVICE_NAME : MAPI_SI_MAX_SERVICE_NAME);
    }
#endif
    return TRUE;
}

void* MW_DVB_SI_PSI_Parser::_AddProgram(MW_DVB_TS_INFO *pTsInfo, MW_DVB_PROGRAM_INFO *pServiceInfo, MAPI_BOOL bCheckInfo)
{
    DVB_PROG stChnInfoDVB;
    DVB_MUX cMux;
    DVB_NETWORK cNetwork;
    DVB_SAT cSat;

    memset(&stChnInfoDVB, 0, sizeof(DVB_PROG));
    memcpy(&cMux, m_pCurMux, sizeof(DVB_MUX));
    cMux.u16TransportStream_ID = pTsInfo->u16TSID;
    cMux.u16OriginalNetwork_ID = pTsInfo->u16ONID;
    cMux.u32LossSignal_Frequency = 0;
    cMux.u32LossSignal_StartTime = 0;
    cMux.u16Network_ID = pTsInfo->u16NID;
    memcpy(&cNetwork, m_pCurNetwork, sizeof(DVB_NETWORK));
    cNetwork.m_u16NetworkID = pTsInfo->u16NID;
    if(m_pCurSat)
    {
        memcpy(&cSat, m_pCurSat, sizeof(DVB_SAT));
    }

    _AddCommonProgramInfo(stChnInfoDVB, pServiceInfo);
    stChnInfoDVB.stCHAttribute.u8Region = m_pCurProg->stCHAttribute.u8Region;
    memcpy(stChnInfoDVB.u8ServiceProviderName, pServiceInfo->au8ServiceProviderName, min(MAPI_SI_MAX_PROVIDER_NAME, MAX_PROVIDER_NAME));
#if (MULTIPLE_SERVICE_NAME_ENABLE == 1)
    stChnInfoDVB.u8MultiServiceNameCnt = (pServiceInfo->u8MultiServiceNameCnt > MAX_MULTILINGUAL_SERVICE_NAME) ? MAX_MULTILINGUAL_SERVICE_NAME : pServiceInfo->u8MultiServiceNameCnt;
    for(MAPI_U8 i=0; i<stChnInfoDVB.u8MultiServiceNameCnt; i++)
    {
        memcpy(stChnInfoDVB.u8MultiServiceName[i], pServiceInfo->u8MultiServiceName[i], (MAPI_SI_MAX_SERVICE_NAME > MAX_SERVICE_NAME) ? MAX_SERVICE_NAME : MAPI_SI_MAX_SERVICE_NAME);
        stChnInfoDVB.aeLangIndex[i] = pServiceInfo->aeLangIndex[i];
    }
#endif

    return ((m_pCurSat !=  NULL) ? (void*)m_pCMDB->Add(TRUE, stChnInfoDVB, cMux, cNetwork, cSat) : (void*)m_pCMDB->Add(TRUE, stChnInfoDVB, cMux, cNetwork));


}
void* MW_DVB_SI_PSI_Parser::_AddSdtOtherProgram(void *pMuxInfo, MW_DVB_PROGRAM_INFO *pServiceInfo)
{
    DVB_PROG stChnInfoDVB;
    DVB_MUX cMux;
    DVB_NETWORK cNetwork;
    DVB_SAT cSat;
    memset(&stChnInfoDVB, 0, sizeof(DVB_PROG));
    //stChnInfoDVB.m_pMuxInfo = &cProgIdDVB;
    //memcpy(stChnInfoDVB.m_pMuxInfo,pMuxInfo,sizeof(_MUX));
    memcpy(&cMux, pMuxInfo, sizeof(DVB_MUX));
    memcpy(&cNetwork, m_pCurNetwork, sizeof(DVB_NETWORK));
    if(NULL != m_pCurSat)
    {
        memcpy(&cSat, m_pCurSat, sizeof(DVB_SAT));
    }
    cMux.u32LossSignal_Frequency = 0;
    cMux.u32LossSignal_StartTime = 0;
    stChnInfoDVB.u16PCRPID = INVALID_PID;
    // pstChnInfo->u16VideoPID = pServiceInfo->u16VideoPid;
    stChnInfoDVB.u16LCN = pServiceInfo->u16LCN;
    stChnInfoDVB.u16Number = pServiceInfo->u16LCN;
    stChnInfoDVB.u16PmtPID = INVALID_PID;
    stChnInfoDVB.u16ServiceID = pServiceInfo->u16ServiceID;
    stChnInfoDVB.u16SimuLCN = pServiceInfo->u16SimuLCN;

    stChnInfoDVB.stPSI_SI_Version.u8NITVer = INVALID_PSI_SI_VERSION;
    stChnInfoDVB.stPSI_SI_Version.u8PATVer = INVALID_PSI_SI_VERSION;
    stChnInfoDVB.stPSI_SI_Version.u8PMTVer = INVALID_PSI_SI_VERSION;
    stChnInfoDVB.stPSI_SI_Version.u8SDTVer = INVALID_PSI_SI_VERSION;

    stChnInfoDVB.stCHAttribute.u8Region = _FindRegionByMux(cMux.u16TransportStream_ID, cMux.u16OriginalNetwork_ID, cMux.u16Network_ID);
    stChnInfoDVB.stCHAttribute.u16SignalStrength = 0xFFFC;
    stChnInfoDVB.stCHAttribute.u8VisibleServiceFlag = pServiceInfo->bIsVisible;
    stChnInfoDVB.stCHAttribute.u8NumericSelectionFlag = pServiceInfo->bIsSelectable;
    stChnInfoDVB.stCHAttribute.u8IsDelete = DEFAULT_IS_DELETED;
    stChnInfoDVB.stCHAttribute.u8IsMove = DEFAULT_IS_MOVED;
    stChnInfoDVB.stCHAttribute.u8IsScramble = MAPI_FALSE;
    stChnInfoDVB.stCHAttribute.u8IsSkipped = DEFAULT_IS_SKIPPED;
    stChnInfoDVB.stCHAttribute.u8IsLock = DEFAULT_IS_LOCKED;
    stChnInfoDVB.stCHAttribute.u8IsReName = DEFAULT_IS_RENAME;
    stChnInfoDVB.stCHAttribute.u8IsStillPicture = MAPI_FALSE;
    stChnInfoDVB.stCHAttribute.u8IsMHEGIncluded = MAPI_FALSE;


    stChnInfoDVB.stCHAttribute.u8IsServiceIdOnly = MAPI_TRUE;
    stChnInfoDVB.stCHAttribute.u8IsDataServiceAvailable = MAPI_FALSE;
    stChnInfoDVB.stCHAttribute.u8IsReplaceDel = MAPI_FALSE;
    stChnInfoDVB.stCHAttribute.u8ServiceType = pServiceInfo->u8ServiceType;
    stChnInfoDVB.stCHAttribute.u8ServiceTypePrio = pServiceInfo->u8ServiceTypePrio;
    stChnInfoDVB.stCHAttribute.u8IsSpecialSrv = pServiceInfo->bIsSpecialSrv;

    /*
    printf("%s....rf %d  sid %x lcn %d simulcn %d\n",__FUNCTION__,
        stChnInfoDVB.m_pMuxInfo->u32Frequency,stChnInfoDVB.u16ServiceID,
        stChnInfoDVB.u16LCN ,stChnInfoDVB.u16SimuLCN);


    printf("%s....onid %x tsid %x \n",__FUNCTION__,
        stChnInfoDVB.m_pMuxInfo->u16OriginalNetwork_ID,stChnInfoDVB.m_pMuxInfo->u16TransportStream_ID);
    */

    stChnInfoDVB.u16VideoPID = INVALID_PID;
    stChnInfoDVB.stCHAttribute.u8VideoType = E_VIDEOTYPE_NONE;
    stChnInfoDVB.stCHAttribute.u8Favorite = DEFAULT_FAVORITE;
    for(int i = 0; i < MAX_AUD_LANG_NUM; i++)
    {
        stChnInfoDVB.stAudInfo[i].u16AudPID = INVALID_PID;
    }
    //pstChnInfo->stCHAttribute.u8InvalidCell = pDTVProgramData->stCHAttribute.bInvalidCell;
    //pstChnInfo->stCHAttribute.u8UnconfirmedService = pDTVProgramData->stCHAttribute.bUnconfirmedService;

    memcpy(stChnInfoDVB.u8ServiceName, pServiceInfo->au8ServiceName, (MAPI_SI_MAX_SERVICE_NAME > MAX_SERVICE_NAME) ? MAX_SERVICE_NAME : MAPI_SI_MAX_SERVICE_NAME);
    memcpy(stChnInfoDVB.u8ServiceProviderName, pServiceInfo->au8ServiceProviderName, min(MAPI_SI_MAX_PROVIDER_NAME, MAX_PROVIDER_NAME));
#if (MULTIPLE_SERVICE_NAME_ENABLE == 1)
    stChnInfoDVB.u8MultiServiceNameCnt = (pServiceInfo->u8MultiServiceNameCnt > MAX_MULTILINGUAL_SERVICE_NAME) ? MAX_MULTILINGUAL_SERVICE_NAME : pServiceInfo->u8MultiServiceNameCnt;
    for(MAPI_U8 i=0; i<stChnInfoDVB.u8MultiServiceNameCnt; i++)
    {
        memcpy(stChnInfoDVB.u8MultiServiceName[i], pServiceInfo->u8MultiServiceName[i], (MAPI_SI_MAX_SERVICE_NAME > MAX_SERVICE_NAME) ? MAX_SERVICE_NAME : MAPI_SI_MAX_SERVICE_NAME);
        stChnInfoDVB.aeLangIndex[i] = pServiceInfo->aeLangIndex[i];
    }
#endif

    return ((m_pCurSat !=  NULL) ? (void*)m_pCMDB->Add(TRUE, stChnInfoDVB, cMux, cNetwork, cSat) : (void*)m_pCMDB->Add(TRUE, stChnInfoDVB, cMux, cNetwork));


}






#define RELEASE_REGION  0x1
#define RELEASE_LOGO    0x2
#define RELEASE_CIPROTECTION    0x4
#define RELEASE_CABLE_DEL    0x8
#define RELEASE_SAT_DEL    0x10
#define RELEASE_PARTIAL_RECEPTION    0x20
#define RELEASE_LCNV2    0x40
#define RELEASE_ALL    0xFFFFFFFF

///for release malloc data in table
class table_release
{
public:
    /// Define table type
    typedef enum
    {
        /// SDT
        E_TABLE_SDT,
        /// NIT
        E_TABLE_NIT,
        ///max
        E_TABLE_MAX,
    } TABLE_TYPE;

    //-------------------------------------------------------------------------------------------------
    /// constructor
    /// @param pTable                 \b IN: pointer to table
    /// @param eType    \b IN: table type
    /// @return  None
    //-------------------------------------------------------------------------------------------------
    explicit table_release(void* pTable, TABLE_TYPE eType)
    {
        ASSERT(pTable);
        m_pTable = pTable;
        m_eTableType = eType;
        m_u32ReleaseType = 0;
    }
    //-------------------------------------------------------------------------------------------------
    /// destructor
    /// @return  None
    //-------------------------------------------------------------------------------------------------
    ~table_release(void)
    {
        switch(m_eTableType)
        {
            case E_TABLE_SDT:
            {
                if(RELEASE_REGION == (m_u32ReleaseType&RELEASE_REGION))
                {
                    //printf("sdt release region\n");
                    _FreeRegionInfo((MAPI_SI_TABLE_SDT*)m_pTable);
                }
                if(RELEASE_LOGO == (m_u32ReleaseType&RELEASE_LOGO))
                {
                    //printf("sdt release logo\n");
                    _FreeLogoInfo((MAPI_SI_TABLE_SDT*)m_pTable);
                }
                if(RELEASE_CIPROTECTION == (m_u32ReleaseType&RELEASE_CIPROTECTION))
                {
                    //printf("sdt release ci\n");
                    _FreeCIProtectInfo((MAPI_SI_TABLE_SDT*)m_pTable);
                }
                break;
            }
            case E_TABLE_NIT:
            {
                MAPI_SI_TABLE_NIT *pNit=(MAPI_SI_TABLE_NIT*)m_pTable;
                if(RELEASE_REGION == (m_u32ReleaseType&RELEASE_REGION))
                {
                    //printf("nit release region\n");
                    _FreeRegionInfo(NULL, pNit);
                }
                if(pNit != NULL)
                {
                    if(RELEASE_CABLE_DEL == (m_u32ReleaseType&RELEASE_CABLE_DEL))
                    {
                        //printf("nit release cable del\n");
                        mapi_utility::freeList(&pNit->pCableDeliveryInfo);
                    }
                    if(RELEASE_SAT_DEL == (m_u32ReleaseType&RELEASE_SAT_DEL))
                    {
                        //printf("nit release sat del\n");
                        mapi_utility::freeList(&pNit->pSatelliteDeliveryInfo);
                    }
                    if(RELEASE_PARTIAL_RECEPTION == (m_u32ReleaseType&RELEASE_PARTIAL_RECEPTION))
                    {
                        //printf("nit release partial\n");
                        FREE(pNit->pstPartialReception);
                    }
                    if(RELEASE_LCNV2 == (m_u32ReleaseType&RELEASE_LCNV2))
                    {
                        //printf("nit release lcnv2\n");
                        _FreeLCNv2Info(pNit);
                    }
                }
                break;
            }
            default:
                break;

        }
    }
    //-------------------------------------------------------------------------------------------------
    /// setReleaseType
    /// @param u32Type    \b IN: release type
    /// @return  None
    //-------------------------------------------------------------------------------------------------
    void setReleaseType(MAPI_U32 u32Type)
    {
        m_u32ReleaseType |= u32Type;
    }
    //-------------------------------------------------------------------------------------------------
    /// unSetReleaseType
    /// @param u32Type    \b IN: release type
    /// @return  None
    //-------------------------------------------------------------------------------------------------
    void unSetReleaseType(MAPI_U32 u32Type)
    {
        m_u32ReleaseType &= ~u32Type;
    }
private:
    TABLE_TYPE m_eTableType;
    void* m_pTable;
    MAPI_U32 m_u32ReleaseType;
};



void * MW_DVB_SI_PSI_Parser::_ParserMonitor_Task(void *arg)
{
    prctl(PR_SET_NAME, (unsigned long)"SI ParserMonitor Task");
#if (STR_ENABLE == 1)
    mapi_str::AutoRegister _R;
#endif
    MW_DVB_SI_PSI_Parser *pObj = (MW_DVB_SI_PSI_Parser *) arg;
    pObj->_ParserMonitorMain();
    MW_SI_PARSER_MESSAGE("exit _ParserMonitor_Task\n");
    pthread_exit(0);
}

void MW_DVB_SI_PSI_Parser::_TablesProcess(void)
{
    mapi_si_psi_event cEvt;

    if(0 == m_pSiEvent->Wait(&cEvt, 20))
    {
        //printf("\n\nSI_Scan: Got .....%d\n", (unsigned int)cEvt.u32Event);
        mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
        switch(cEvt.u32Event)
        {
            case EN_SI_PSI_EVENT_DATA_ERROR:
            case EN_SI_PSI_EVENT_DATA_OVERFLOW:
            {
                MAPI_BOOL bPatError = MAPI_FALSE;
                if(cEvt.u32Event == EN_SI_PSI_EVENT_DATA_ERROR)
                {
                    MW_SI_PARSER_WARNING("EN_SI_PSI_EVENT_DATA_ERROR \n");
                }
                else
                {
                    MW_SI_PARSER_WARNING("EN_SI_PSI_EVENT_DATA_OVERFLOW \n");
                }
                if((MAPI_U32)m_pPatParser == cEvt.u32Param1)
                {
                    bPatError = MAPI_TRUE;
                }
                _DeleteParser(cEvt.u32Param1, MAPI_TRUE);
                if(bPatError && (E_DVB_SCAN == m_eParserMode))
                {
                    _ScanError(E_DVB_SCAN_DATA_ERROR);
                }
            }
            break;
            case EN_SI_PSI_EVENT_TIMEOUT:
            case EN_SI_PSI_EVENT_SAME_VERSION:
            {
                MAPI_BOOL bPatError = MAPI_FALSE;
                if(cEvt.u32Event == EN_SI_PSI_EVENT_TIMEOUT)
                {
                    MW_SI_PARSER_WARNING("EN_SI_PSI_EVENT_TIMEOUT \n");
                    if(((MAPI_U32)m_pSdtParser == cEvt.u32Param1) && (E_DVB_MONITOR == m_eParserMode) && (m_eParserBaseType == MW_DVB_SDT_PAT_BASE))
                    {
                        MW_SI_PARSER_WARNING("SDT monitoring parser time out\n");
                        m_bSdtParserTimeOut = MAPI_TRUE;
                        mapi_si_PAT_parser::Reset(m_stPat);
                    }
                }
                else
                {
                    if((MAPI_U32)m_pBatParser == cEvt.u32Param1)
                    {
                        MW_SI_PARSER_MESSAGE("BAT EN_SI_PSI_EVENT_SAME_VERSION\n");
                    }
                    else if ((MAPI_U32)m_pEitPFParser == cEvt.u32Param1)
                    {
                        if ((_GetRating() != m_u8CurRatingChkValue)||(_GetContent() !=m_u8CurContentChkValue))
                        {
                            MONITOR_NOTIFY(E_DVB_PARENTAL_RATING, NULL, m_pMonitorNotifyUsrParam, NULL);
                        }
                    }
                }
                if((cEvt.u32Event == EN_SI_PSI_EVENT_TIMEOUT) && ((MAPI_U32)m_pPatParser == cEvt.u32Param1))
                {
                    bPatError = MAPI_TRUE;
                }
#if (ASTRA_SGT_ENABLE == 1)
                /// to get service lists for SGT section first loop
                if((cEvt.u32Event == EN_SI_PSI_EVENT_TIMEOUT) && ((MAPI_U32)m_pSgtParser == cEvt.u32Param1))
                {
                    if(m_pSgtParser->GetBasicServiceListInfo(m_ServiceListBasicInfo)==MAPI_FALSE)
                    {
                        _ScanError(E_DVB_SCAN_TIMEOUT);
                    }
                    else
                    {
                        MW_SI_SGT_SCAN_DEBUG(" %s Basic Service list Not Zero\n",__FUNCTION__);
                    }

                }
#endif
                if(EIT_PF_CONTINUE_MODE &&
                    (cEvt.u32Param1 == (U32)m_pEitPFParser) &&
                    (cEvt.u32Event == EN_SI_PSI_EVENT_SAME_VERSION))
                {
                    ///do nothing
                }
                else
                {
                    _DeleteParser(cEvt.u32Param1, (cEvt.u32Event == EN_SI_PSI_EVENT_TIMEOUT) ? MAPI_TRUE : MAPI_FALSE);
                }

#if (OAD_ENABLE == 1)
                if((cEvt.u32Event == EN_SI_PSI_EVENT_TIMEOUT) || (cEvt.u32Event == EN_SI_PSI_EVENT_SAME_VERSION))
                {
                    if(((E_DVB_MONITOR == m_eParserMode) || (E_DVB_OAD_SCAN == m_eParserMode)) && MW_DVB_SI_PSI_Parser::m_OADParser)
                    {
                        MAPI_BOOL bAllPmtReady = MAPI_TRUE;

                        MW_DVB_SI_PSI_Parser::m_OADParser->SetPmtReady();

                        for(int i = 0; i < MAX_CHANNEL_IN_MUX; i++)
                        {
                            if(m_aPMTWaitFilter[i] == MAPI_TRUE)
                            {
                                bAllPmtReady = MAPI_FALSE;
                                break;
                            }
                        }
                        if(bAllPmtReady)
                        {
                            for(int i = 0; i < MAX_CHANNEL_IN_MUX; i++)
                            {
                                if(m_pAllPmtParser[i])
                                {
                                    bAllPmtReady = MAPI_FALSE;
                                    break;
                                }
                            }
                        }
                        if(bAllPmtReady)
                        {
                            MW_DVB_SI_PSI_Parser::m_OADParser->SetAllPmtReady();
                        }
                    }
                }
#endif //(OAD_ENABLE == 1)
                if(bPatError && (E_DVB_SCAN == m_eParserMode))
                {
                    _ScanError(E_DVB_SCAN_TIMEOUT);
                }
            }
            break;
            case EN_SI_PSI_EVENT_PAT_READY:
            {
                //for Filein SI parsing
                if((MAPI_U32)m_pFileinPatParser == cEvt.u32Param1)
                {
                    DATA_READY(_FileinPatReady, m_pCurProg, m_pCMDB, m_pCurMux, m_pCurNetwork);
                }

                if((MAPI_U32)m_pPatParser != cEvt.u32Param1)
                {
                    if(NULL != m_pPatParser)
                    {
                        MW_SI_PARSER_WARNING("old pat data ready \n");
                        //ASSERT(0);
                    }
                    break;
                }
#if (OAD_ENABLE == 1)
                if(E_DVB_OAD_SCAN == m_eParserMode)
                {
                    _PatReady_OAD();
                }
                else
                {
                    DATA_READY(_PatReady, m_pCurProg, m_pCMDB, m_pCurMux, m_pCurNetwork);
                }
#else
                DATA_READY(_PatReady, m_pCurProg, m_pCMDB, m_pCurMux, m_pCurNetwork);
#endif

            }
            break;
            case EN_SI_PSI_EVENT_PMT_READY:
            {
#if (OAD_ENABLE == 1)
                if(E_DVB_OAD_SCAN == m_eParserMode)
                {
                    _PmtReady_OAD(cEvt.u32Param1);
                }
                else
                {
                    _PmtReady(m_pCurProg, m_pCMDB, m_pCurMux, m_pCurNetwork, cEvt.u32Param1);
                }
#else
                _PmtReady(m_pCurProg, m_pCMDB, m_pCurMux, m_pCurNetwork, cEvt.u32Param1);
#endif
            }
#if (OAD_ENABLE == 1)
            if(((E_DVB_MONITOR == m_eParserMode) || (E_DVB_OAD_SCAN == m_eParserMode)) && MW_DVB_SI_PSI_Parser::m_OADParser)
            {
                MAPI_BOOL bAllPmtReady = MAPI_TRUE;

                MW_DVB_SI_PSI_Parser::m_OADParser->SetPmtReady();

                for(int i = 0; i < MAX_CHANNEL_IN_MUX; i++)
                {
                    if(m_aPMTWaitFilter[i] == MAPI_TRUE)
                    {
                        bAllPmtReady = MAPI_FALSE;
                        break;
                    }
                }
                if(bAllPmtReady)
                {
                    for(int i = 0; i < MAX_CHANNEL_IN_MUX; i++)
                    {
                        if(m_pAllPmtParser[i])
                        {
                            bAllPmtReady = MAPI_FALSE;
                            break;
                        }
                    }
                }
                if(bAllPmtReady)
                {
                    MW_DVB_SI_PSI_Parser::m_OADParser->SetAllPmtReady();
                }
            }
#endif //(OAD_ENABLE == 1)
            break;
            case EN_SI_PSI_EVENT_NIT_READY:
            {
                if((m_eParserMode == E_DVB_QUICK_SCAN) && (m_eParserType == MW_DVB_C_PARSER))
                {
                    std::list<mapi_si_NIT_parser *>::iterator it;
                    for(it=m_pNitParserList.begin(); it!=m_pNitParserList.end(); ++it)
                    {
                        if((*it != NULL) && ((MAPI_U32)*it == cEvt.u32Param1))
                        {
                            m_pNitParser = *it;
                            break;
                        }
                    }
                }
                if((MAPI_U32)m_pNitParser != cEvt.u32Param1)
                {
                    if(NULL != m_pNitParser)
                    {
                        MW_SI_PARSER_WARNING("old nit data ready \n");
                        //ASSERT(0);
                    }
                    break;
                }
                DATA_READY(_NitReady, m_pCurProg, m_pCMDB, m_pCurMux, m_pCurNetwork);
#if (OAD_ENABLE == 1)
                if(((E_DVB_MONITOR == m_eParserMode) || (E_DVB_OAD_SCAN == m_eParserMode)) && MW_DVB_SI_PSI_Parser::m_OADParser)
                {
                    MW_DVB_SI_PSI_Parser::m_OADParser->SetNitReady();
                }
#endif //(OAD_ENABLE == 1)
            }
            break;
            case EN_SI_PSI_EVENT_SDT_READY:
            {
                if((MAPI_U32)m_pSdtParser != cEvt.u32Param1)
                {
                    if(NULL != m_pSdtParser)
                    {
                        MW_SI_PARSER_WARNING("old sdt data ready \n");
                        //ASSERT(0);
                    }
                    break;
                }
                DATA_READY(_SdtReady, m_pCurProg, m_pCMDB, m_pCurMux, m_pCurNetwork);
#if (OAD_ENABLE == 1)
                if((E_DVB_OAD_SCAN == m_eParserMode) && MW_DVB_SI_PSI_Parser::m_OADParser)
                {
                    MW_DVB_SI_PSI_Parser::m_OADParser->SetSdtReady();
                }
#endif
            }
            break;
            case EN_SI_PSI_EVENT_SDT_OTHER_READY:
            {
                if ((m_eParserMode == E_DVB_QUICK_SCAN) && (m_eParserType == MW_DVB_C_PARSER))
                {
                    for(int j = 0; j < MAPI_SI_MAX_TS_IN_NETWORK; j++)
                    {
                        if(m_pAllSdtParser[j] && ((MAPI_U32)m_pAllSdtParser[j] == cEvt.u32Param1))
                        {
                            m_pSdtOtherParser = m_pAllSdtParser[j];
                            m_pAllSdtParser[j] = NULL;
                            break;
                        }
                    }
                }
                if((MAPI_U32)m_pSdtOtherParser != cEvt.u32Param1)
                {
                    if(NULL != m_pSdtOtherParser)
                    {
                        MW_SI_PARSER_WARNING("old sdt other data ready \n");
                        //ASSERT(0);
                    }
                    break;
                }
                DATA_READY(_SdtOtherReady, m_pCurProg, m_pCMDB, m_pCurMux, m_pCurNetwork);
            }
            break;
            case EN_SI_PSI_EVENT_TDT_READY:
            {
                if((MAPI_U32)m_pTdtParser != cEvt.u32Param1)
                {
                    if(NULL != m_pTdtParser)
                    {
                        MW_SI_PARSER_WARNING("old tdt data ready \n");
                        //ASSERT(0);
                    }
                    break;
                }
                _TdtReady();
#if (OAD_ENABLE == 1)
                if(((E_DVB_MONITOR == m_eParserMode) || (E_DVB_OAD_SCAN == m_eParserMode)) && MW_DVB_SI_PSI_Parser::m_OADParser)
                {
                    MW_DVB_SI_PSI_Parser::m_OADParser->SetTdtReady();
                }
#endif
            }
            break;

            case EN_SI_PSI_EVENT_TOT_READY:
            {
                if((MAPI_U32)m_pTotParser != cEvt.u32Param1)
                {
                    if(NULL != m_pTotParser)
                    {
                        MW_SI_PARSER_WARNING("old tot data ready \n");
                        //ASSERT(0);
                    }
                    break;
                }
                _TotReady();
#if (OAD_ENABLE == 1)
                if(((E_DVB_MONITOR == m_eParserMode) || (E_DVB_OAD_SCAN == m_eParserMode)) && MW_DVB_SI_PSI_Parser::m_OADParser)
                {
                    MW_DVB_SI_PSI_Parser::m_OADParser->SetTotReady();
                }
#endif
            }
            break;
            case EN_SI_PSI_EVENT_EIT_READY:
            {
                if((MAPI_U32)m_pEitPFParser != cEvt.u32Param1)
                {
                    if(NULL != m_pEitPFParser)
                    {
                        MW_SI_PARSER_WARNING("old PF data ready \n");
                        //ASSERT(0);
                    }
                    break;
                }
                _EitPFReady();
            }
            break;
            case EN_SI_PSI_EVENT_RCT_READY:
            {
                if((MAPI_U32)m_pRctParser != cEvt.u32Param1)
                {
                    if(NULL != m_pRctParser)
                    {
                        MW_SI_PARSER_WARNING("old RCT data ready \n");
                        //ASSERT(0);
                    }
                    break;
                }
                _RctReady();
            }
            break;
            case EN_SI_PSI_EVENT_AIT_READY:
            {
                if((MAPI_U32)m_pAitParser != cEvt.u32Param1)
                {
                    if(NULL != m_pAitParser)
                    {
                        MW_SI_PARSER_WARNING("old AIT data ready \n");
                        //ASSERT(0);
                    }
                    break;
                }
                _AitReady();
            }
            break;
            case EN_SI_PSI_EVENT_BAT_READY:
            {
                if((MAPI_U32)m_pBatParser != cEvt.u32Param1)
                {
                    if(NULL != m_pBatParser)
                    {
                        MW_SI_PARSER_WARNING("old bat data ready \n");
                        //ASSERT(0);
                    }
                    break;
                }
                DATA_READY(_BatReady, m_pCurProg, m_pCMDB, m_pCurMux, m_pCurNetwork);
            }
            break;
#if (ISDB_SYSTEM_ENABLE == 1)
#if 1//(ISDB_CHANNELLOGO_ENABLE == 1)
            case EN_SI_PSI_EVENT_CDT_READY:
            {  printf("EN_SI_PSI_EVENT_CDT_READY \n");

                if((MAPI_U32)m_pCdtParser != cEvt.u32Param1)
                {
                    if(NULL != m_pCdtParser)
                    {
                        printf("old CDT data ready \n");
                        //ASSERT(0);
                    }
                    break;
                }
                _CdtReady();
            }
            break;
#endif
#endif
#if (ASTRA_SGT_ENABLE == 1)
            case EN_SI_PSI_EVENT_SGT_READY:
            {
                if((MAPI_U32)m_pSgtParser != cEvt.u32Param1)
                {
                    if(NULL != m_pSgtParser)
                    {
                        MW_SI_PARSER_WARNING("old Sgt data ready \n");
                        //ASSERT(0);
                    }
                    break;
                }
                _SgtReady();

            }
            break;
#endif
#if ((OAD_ENABLE == 1) && (SDTT_OAD_ENABLE==1))
            case EN_SI_PSI_EVENT_SDTT_READY:
            {
                _SdttReady();

            }
            break;
#endif
            default:
                break;
        }
    }
}
void MW_DVB_SI_PSI_Parser::_UpdateServiceCheck(void)
{
#if (NCD_ENABLE == 1)
    std::list<ST_MW_RELOCATED_SERVICE_INFO>::iterator it;

    for(it=m_stRelocatedServiceInfoList.begin(); it!=m_stRelocatedServiceInfoList.end();)
    {
        if(it->enState == E_RELOCATED_SERVICE_STATE_WAIT_ADD)
        {
            if((it->stTripleId.u16TsId == __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_TSID)) && (it->stTripleId.u16OnId == __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_ONID)))
            {
                MW_SI_PARSER_UPDATE("[NCD] %s: add SID %u in TSID %u\n", __FUNCTION__, it->stTripleId.u16SrvId, it->stTripleId.u16TsId);
                m_au16NewProg2Add[m_u16NewAddChnNum] = it->stTripleId.u16SrvId;
                m_u16NewAddChnNum++;
                MW_SI_PARSER_UPDATE("[NCD] %s: erase SID %u in TSID %u from RelocatedServiceInfo(size = %u)\n", __FUNCTION__, \
                                it->stTripleId.u16SrvId, it->stTripleId.u16TsId, m_stRelocatedServiceInfoList.size());
                it = m_stRelocatedServiceInfoList.erase(it);
            }
            else
            {
                MW_SI_PARSER_UPDATE("[NCD] %s: add SID %u in TSID %u\n", __FUNCTION__, it->stTripleId.u16SrvId, it->stTripleId.u16TsId);
                MW_DVB_PROGRAM_INFO stProgInfo;
                _BuildChannelInfoOther(it->stTripleId.u16TsId, &it->stServiceInfo, stProgInfo);
                _UpdateTSOther(&it->stTripleId, &stProgInfo,(DVB_CM *)m_pCMDB);
                MW_SI_PARSER_UPDATE("[NCD] %s: erase SID %u in TSID %u from RelocatedServiceInfo(size = %u)\n", __FUNCTION__, \
                                it->stTripleId.u16SrvId, it->stTripleId.u16TsId, m_stRelocatedServiceInfoList.size());
                it = m_stRelocatedServiceInfoList.erase(it);
            }
        }
        else
        {
            it++;
        }
    }
#endif

    if(m_u16NewAddChnNum > 0)
    {
        if((m_stPat.u8Version != INVALID_PSI_SI_VERSION) && (((m_stSdt.u8Version != INVALID_PSI_SI_VERSION)  && (m_stNit.u8Version != INVALID_PSI_SI_VERSION)) || (_Timer_DiffTimeFromNow(m_u32StartUpdateTime) > MAX_SI_NIT_WAIT_TABLES_TIME)))
        {
            MAPI_BOOL bFinishUpdate = MAPI_TRUE;
            if(m_eParserBaseType != MW_DVB_PAT_BASE)
            {
                __SetVer(m_pCurProg, EN_VER_SDT, m_u8CurSdtVer);
            }
            else
            {
                __SetVer(m_pCurProg, EN_VER_PAT, m_u8CurPatVer);
            }
#if (ISDB_SYSTEM_ENABLE == 0)
            if((m_eParserBaseType != MW_DVB_PAT_BASE) && (m_stPat.u16TsId != m_stSdt.wTransportStream_ID))
            {
                m_u16NewAddChnNum = 0;
            }
            else
#endif
            {
                if(_Timer_DiffTimeFromNow(m_u32StartUpdateTime) < MAX_SI_NIT_WAIT_TABLES_TIME)
                {
                    for(int i = 0; i < MAX_CHANNEL_IN_MUX; i++)
                    {
                        if(m_aPMTWaitFilter[i] == MAPI_TRUE)
                        {
                            bFinishUpdate = MAPI_FALSE;
                            break;
                        }
                    }
                    if(bFinishUpdate)
                    {
                        for(int i = 0; i < MAX_CHANNEL_IN_MUX; i++)
                        {
                            if(NULL != m_pAllPmtParser[i])
                            {
                                bFinishUpdate = MAPI_FALSE;
                                break;
                            }
                        }
                    }
                }
                if(bFinishUpdate)
                {
                    MW_SI_PARSER_UPDATE("start update TS %s %d\n", __FUNCTION__, __LINE__);
                    _BuildChannelInfo();
                    if (m_TsInfo.u16ServiceCount != 0)
                    {
                        _UpdateTS(m_pCurProg, m_pCMDB, m_pCurMux, m_pCurNetwork);
                    }
                    m_u16NewAddChnNum = 0;
                }
            }
        }
    }
}
void MW_DVB_SI_PSI_Parser::_ScanFinishCheck(void)
{
    if(m_s16OpenFilter <= 0)//bSDTFinish && bPatFinish)
    {
        MW_SI_PARSER_SCAN("finish scan\n");
        m_bRunning = MAPI_FALSE;
        if(m_stPat.u8Version == INVALID_PSI_SI_VERSION)
        {
            MW_SI_PARSER_ERROR("invalid PAT\n");
            _FreeDeliveryInfo(m_stNit, MAPI_FALSE);
            _FreeRegionInfo(&m_stSdt, &m_stNit);
            if(NULL != _pfScanResultNotify)
            {
                _pfScanResultNotify(E_DVB_SCAN_DATA_ERROR, m_u32ScanResultNotifyParam1, NULL, NULL);
            }
        }
        else
        {
            /*if( (m_eParserBaseType != MW_DVB_PAT_BASE) && ((m_stSdt.u8Version != INVALID_PSI_SI_VERSION) && (m_stPat.u16TsId != m_stSdt.wTransportStream_ID)))
            {
                MW_SI_PARSER_ERROR("SDT/PAT tsid  not match\n");
                _FreeDeliveryInfo(m_stNit, MAPI_FALSE);
                if(_pfScanResultNotify)
                {
                    _pfScanResultNotify(E_DVB_SCAN_DATA_ERROR,m_u32ScanResultNotifyParam1,NULL,NULL);
                }
            }
            else*/
            {
                MAPI_BOOL bSuccess = MAPI_TRUE;
                if(m_bEnableNetworkFilter && (m_eParserType != MW_DVB_T_PARSER))
                {
                    if((m_stNit.u8Version == INVALID_PSI_SI_VERSION) || (m_stNit.u16NetworkID != m_u16NetworkID))
                    {
                        bSuccess = MAPI_FALSE;
                        _FreeDeliveryInfo(m_stNit, MAPI_FALSE);
                        _FreeRegionInfo(&m_stSdt, &m_stNit);
                        if(NULL != _pfScanResultNotify)
                        {
                            _pfScanResultNotify(E_DVB_SCAN_DATA_ERROR, m_u32ScanResultNotifyParam1, NULL, NULL);
                        }
                    }
                }
                if(bSuccess)
                {
                    _BuildNitInfo();
                    _BuildDeliveryInfo(m_stNit);
                    _BuildChannelInfo();
                    if(NULL != _pfScanResultNotify)
                    {
                        _pfScanResultNotify(E_DVB_SCAN_FINISH, m_u32ScanResultNotifyParam1, &m_TsInfo, m_ProgramInfo);
                    }
                }
            }
        }
    }
}
void MW_DVB_SI_PSI_Parser::_ParserMonitorMain(void)
{
    while(1)
    {
        if(m_bExitParser)
        {
            _StopAllFilter(MAPI_TRUE);
            return;
        }
        // Process event trigger from Player
        if (MAPI_FALSE == _ProcessTriggerEvent())
        {
            //for Filein SI parsing
            //_FileinPMT_Monitor();
            if(MAPI_TRUE == m_bFileinParserRunning)
            {
                _FileinPAT_Monitor();
                _FileinPMT_Monitor();
                _TablesProcess();
            }
            //<
            if (m_bRunning)
            {
                if ((E_DVB_IDLE != m_eParserMode)
                    || (MAPI_TRUE == m_bFileinParserRunning))//for Filein SI parsing
                {
                    PTH_RET_CHK(pthread_mutex_lock(&m_pParseMonitorMutex));
                    if (E_DVB_MONITOR == m_eParserMode)
                    {
                        _Table_Monitor();
                        _UpdateServiceCheck();
                        _CiEitNortifyMonitor();
                    }
                    else if (E_DVB_SCAN == m_eParserMode)
                    {
                        _ScanFinishCheck();
                    }
#if (OAD_ENABLE == 1)
                    else if((E_DVB_OAD_SCAN == m_eParserMode) || (E_DVB_OAD_DOWNLOAD == m_eParserMode))
                    {
                        _Table_Monitor_OadScan();
                    }
#endif
#if (DVBC_SYSTEM_ENABLE == 1)
                    else if (E_DVB_QUICK_SCAN == m_eParserMode)
                    {
                        _QuickScan();
                    }
#endif
#if (ASTRA_SGT_ENABLE == 1)
                    else if(E_DVB_SGT_SCAN == m_eParserMode)
                    {
                        _SGTScan();
                    }
#endif
                    else if(E_DVB_EPG_UPDATE == m_eParserMode)
                    {
                        _EPG_Update_Monitor();
                    }

                    PTH_RET_CHK(pthread_mutex_unlock(&m_pParseMonitorMutex));
                    _TablesProcess();
                }
                else
                {
                    usleep(20000);
                }
            }
        }
    }
}

#if EIT_BUFFER_USE_QUEUE
void * MW_DVB_SI_PSI_Parser::_EitParserMonitor_Task(void *arg)
{
    prctl(PR_SET_NAME, (unsigned long)"EIT ParserMonitor Task");
#if (STR_ENABLE == 1)
    mapi_str::AutoRegister _R;
#endif
    MW_DVB_SI_PSI_Parser *pObj = (MW_DVB_SI_PSI_Parser *) arg;
    pObj->_EitParserMonitorMain();
    MW_SI_PARSER_MESSAGE("exit EIT _ParserMonitor_Task\n");
    pthread_exit(0);
}


void MW_DVB_SI_PSI_Parser::_EitParserMonitorMain(void)
{
    mapi_si_psi_event cEvt;
    while(1)
    {
        int Result;//=m_pEitEvent->Wait(&cEvt,20);
        if(m_bExitParser || m_bResetEitParser)
        {
            while(m_pEitEvent->Wait(&cEvt, 0) == 0)
            {
                ASSERT(cEvt.u32Param2);
                free((void*)cEvt.u32Param2);
                m_s16EITBufferAllocCnt--;
#if EIT_BUFFER_DBG
                _u32BufferFreeCnt++;
#endif
            }
            m_bResetEitParser = MAPI_FALSE;
#if EIT_BUFFER_DBG
            ASSERT(_u32BufferAllocCnt == _u32BufferFreeCnt);
#endif
            if(m_bExitParser)
            {
                return;
            }
        }
        Result = m_pEitEvent->Wait(&cEvt, 20);
        if(Result == 0)
        {
            switch(cEvt.u32Event)
            {
                case EN_SI_PSI_EVENT_EIT_SCHE_ALL_PF_READY:
                    ASSERT(cEvt.u32Param2);
#if (EPG_ENABLE == 1)

                    if(!m_bExitParser)
                    {
                        m_ParserCallBackInfo.EventHandler(E_DVB_SI_EIT_SECTION_IN,(MAPI_U32)m_ParserCallBackInfo.pCallbackReference,cEvt.u32Param2);

                    }
#endif

                    free((void*)cEvt.u32Param2);
                    m_s16EITBufferAllocCnt--;
#if EIT_BUFFER_DBG
                    _u32BufferFreeCnt++;

#endif
                    break;
                default:
                    break;
            }

        }
    }



}

#endif


template<class _DELIVERINFO>
MAPI_BOOL MW_DVB_SI_PSI_Parser::_AddDeliveryInfo(_DELIVERINFO *pSrc, _DELIVERINFO *pNew)
{
    _DELIVERINFO *pInfo, *pTail, *pAdd;


    if(pNew->u16TSID == m_stPat.u16TsId)
    {
        pNew->bUsed = MAPI_TRUE;
    }

    pInfo = pTail = pSrc;
    while(NULL != pInfo)
    {
        pTail = pInfo;
        if(pInfo->u16TSID == pNew->u16TSID)
        {
            return MAPI_FALSE;
        }
        pInfo = pInfo->next;
    }
    pAdd = (_DELIVERINFO*)malloc(sizeof(_DELIVERINFO));
    if(NULL != pAdd)
    {
        memcpy(pAdd, pNew, sizeof(_DELIVERINFO));
        pTail->next = pAdd;
        pAdd->next = NULL;
        return MAPI_TRUE;
    }
    MW_SI_PARSER_ERROR("_AddDeliveryInfo allocate failed\n");
    return MAPI_FALSE;
}




void MW_DVB_SI_PSI_Parser::_BuildDeliveryInfo(MAPI_SI_TABLE_NIT & stNit, BOOL bExcludeCurTS)
{
    if(NULL != stNit.pCableDeliveryInfo)
    {
        if(NULL == m_pCableDeliveryInfo)
        {
            CABLE_DESC_DEL_SYS_DATA *pTmp;
            pTmp = stNit.pCableDeliveryInfo;
            while(NULL != pTmp)
            {
                MW_SI_PARSER_MESSAGE("m_stNit.pCableDeliveryInfo ts %x frequency %d sym %d mod %d\n", pTmp->u16TSID,
                                     pTmp->stCDS.u32CentreFreq, pTmp->stCDS.u32Symbol_rate, pTmp->stCDS.u8Modulation);
                if((bExcludeCurTS)&&(pTmp->u16TSID == m_stPat.u16TsId))
                {
                    pTmp->bUsed = MAPI_TRUE;
                }
                pTmp = pTmp->next;
            }
            m_pCableDeliveryInfo = stNit.pCableDeliveryInfo;//coverity check bug(missing lock), no change need
            stNit.pCableDeliveryInfo = NULL;
        }
        else
        {
            CABLE_DESC_DEL_SYS_DATA *pNew;
            pNew = stNit.pCableDeliveryInfo;
            while(NULL != pNew)
            {
                _AddDeliveryInfo(m_pCableDeliveryInfo, pNew);
                pNew = pNew->next;
            }
            mapi_utility::freeList(&stNit.pCableDeliveryInfo);
        }
    }

    if(NULL != stNit.pSatelliteDeliveryInfo)
    {
        if(NULL == m_pSatelliteDeliveryInfo)
        {
            SATELLITE_DESC_DEL_SYS_DATA *pTmp;
            pTmp = stNit.pSatelliteDeliveryInfo;
            while(NULL != pTmp)
            {
                MW_SI_PARSER_MESSAGE("m_stNit.pSatelliteDeliveryInfo ts %x frequency %d sym %d mod type %d mod sys %d pol %d fec %d\n", pTmp->u16TSID,
                                     pTmp->stSDS.u32Freq, pTmp->stSDS.u32Symbol_rate, pTmp->stSDS.u8Modulation_type, pTmp->stSDS.u8modulation_system, pTmp->stSDS.u8polarization, pTmp->stSDS.u8FEC_inner);
                if((bExcludeCurTS)&&(pTmp->u16TSID == m_stPat.u16TsId))
                {
                    pTmp->bUsed = MAPI_TRUE;
                }
                pTmp = pTmp->next;
            }
            m_pSatelliteDeliveryInfo = stNit.pSatelliteDeliveryInfo;
            stNit.pSatelliteDeliveryInfo = NULL;
        }
        else
        {
            SATELLITE_DESC_DEL_SYS_DATA *pNew;
            pNew = stNit.pSatelliteDeliveryInfo;
            while(NULL != pNew)
            {
                _AddDeliveryInfo(m_pSatelliteDeliveryInfo, pNew);
                pNew = pNew->next;
            }
            mapi_utility::freeList(&stNit.pSatelliteDeliveryInfo);
        }
    }

}

void MW_DVB_SI_PSI_Parser::_FreeDeliveryInfo(MAPI_SI_TABLE_NIT & stNit, MAPI_BOOL bResetAll)
{
    if(NULL != stNit.pCableDeliveryInfo)
    {
        mapi_utility::freeList(&stNit.pCableDeliveryInfo);
    }
    if(bResetAll && (NULL != m_pCableDeliveryInfo))
    {
        mapi_utility::freeList(&m_pCableDeliveryInfo);
    }
    if(NULL != stNit.pSatelliteDeliveryInfo)
    {
        mapi_utility::freeList(&stNit.pSatelliteDeliveryInfo);
    }
    if(bResetAll && (NULL != m_pSatelliteDeliveryInfo))
    {
        mapi_utility::freeList(&m_pSatelliteDeliveryInfo);
    }
}

#if (DVBC_SYSTEM_ENABLE ==1)
MAPI_BOOL MW_DVB_SI_PSI_Parser::GetNextCableDeliveryInfo(MS_CABLE_PARAMETER  &stInfo, MAPI_U16 &u16RemainMux, BOOL bExcludeCurTS)
{
    CABLE_DESC_DEL_SYS_DATA *pCableInfo;
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    pCableInfo = m_pCableDeliveryInfo;
    u16RemainMux = 0;
    while(NULL != pCableInfo)
    {
        if((bExcludeCurTS)&&(pCableInfo->u16TSID == m_stPat.u16TsId))
        {
            pCableInfo->bUsed = TRUE;
        }
        if(pCableInfo->bUsed == FALSE)
        {
            u16RemainMux += 1;
        }
        pCableInfo = pCableInfo->next;
    }
    pCableInfo = m_pCableDeliveryInfo;
    while(NULL != pCableInfo)
    {
        if(pCableInfo->bUsed == MAPI_FALSE)
        {
            pCableInfo->bUsed = MAPI_TRUE;
            stInfo.u32CentreFreq = pCableInfo->stCDS.u32CentreFreq;
            stInfo.u32Symbol_rate = pCableInfo->stCDS.u32Symbol_rate;
            stInfo.u8Modulation = pCableInfo->stCDS.u8Modulation;
            return MAPI_TRUE;
        }
        pCableInfo = pCableInfo->next;
    }
    return MAPI_FALSE;
}
#endif

#if (DVBS_SYSTEM_ENABLE ==1)
MAPI_BOOL MW_DVB_SI_PSI_Parser::GetNextSatelliteDeliveryInfo(MS_SATELLITE_PARAMETER  &stInfo, MAPI_U16 &u16RemainMux)
{
    SATELLITE_DESC_DEL_SYS_DATA *pSatelliteInfo;
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    pSatelliteInfo = m_pSatelliteDeliveryInfo;
    u16RemainMux = 0;
    while(NULL != pSatelliteInfo)
    {
        if(pSatelliteInfo->u16TSID == m_stPat.u16TsId)
        {
            pSatelliteInfo->bUsed = TRUE;
        }
        if(pSatelliteInfo->bUsed == FALSE)
        {
            u16RemainMux += 1;
        }
        pSatelliteInfo = pSatelliteInfo->next;
    }
    pSatelliteInfo = m_pSatelliteDeliveryInfo;
    while(NULL != pSatelliteInfo)
    {
        if(pSatelliteInfo->bUsed == MAPI_FALSE)
        {
            pSatelliteInfo->bUsed = MAPI_TRUE;
            stInfo.u32Freq = pSatelliteInfo->stSDS.u32Freq;
            stInfo.u32Symbol_rate = pSatelliteInfo->stSDS.u32Symbol_rate;
            stInfo.u8Modulation_type = pSatelliteInfo->stSDS.u8Modulation_type;
            stInfo.u8modulation_system = pSatelliteInfo->stSDS.u8modulation_system;
            stInfo.u8FEC_inner = pSatelliteInfo->stSDS.u8FEC_inner;
            stInfo.u8polarization = pSatelliteInfo->stSDS.u8polarization;
            return MAPI_TRUE;
        }
        pSatelliteInfo = pSatelliteInfo->next;
    }
    return MAPI_FALSE;
}
MAPI_BOOL MW_DVB_SI_PSI_Parser::IsSatelliteTpExistInNIT(MS_SATELLITE_PARAMETER desc)
{
    SATELLITE_DESC_DEL_SYS_DATA *pSatelliteInfo;
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    pSatelliteInfo = m_pSatelliteDeliveryInfo;
    while(pSatelliteInfo != NULL)
    {
        if((desc.u32Freq == pSatelliteInfo->stSDS.u32Freq)&&
            (desc.u32Symbol_rate == pSatelliteInfo->stSDS.u32Symbol_rate)&&
            (desc.u8Modulation_type == pSatelliteInfo->stSDS.u8Modulation_type)&&
            (desc.u8polarization == pSatelliteInfo->stSDS.u8polarization))
        {
            return TRUE;
        }
        pSatelliteInfo = pSatelliteInfo->next;
    }
    return MAPI_FALSE;
}
#endif

#if (CI_PLUS_ENABLE == 1)
#if (DVBC_SYSTEM_ENABLE == 1)
MAPI_BOOL MW_DVB_SI_PSI_Parser::GetCableDeliveryInfoByID(CABLE_DESC_DEL_SYS_DATA * pCableDeliveryInfo, MS_CABLE_PARAMETER  &stInfo, U16 u16TSID)
{
    CABLE_DESC_DEL_SYS_DATA *pCableInfo;
    pCableInfo = pCableDeliveryInfo;

    while(NULL != pCableInfo)
    {
        if(pCableInfo->u16TSID == u16TSID)
        {
            stInfo.u32CentreFreq = pCableInfo->stCDS.u32CentreFreq;
            stInfo.u32Symbol_rate = pCableInfo->stCDS.u32Symbol_rate;
            stInfo.u8Modulation = pCableInfo->stCDS.u8Modulation;
            return MAPI_TRUE;
        }
        pCableInfo = pCableInfo->next;
    }
    return MAPI_FALSE;
}
#endif

#endif

#if (DVBS_SYSTEM_ENABLE == 1)
MAPI_BOOL MW_DVB_SI_PSI_Parser::GetSatelliteDeliveryInfoByID(SATELLITE_DESC_DEL_SYS_DATA * pSatelliteDeliveryInfo, MS_SATELLITE_PARAMETER  &stInfo, U16 u16TSID)
{
    SATELLITE_DESC_DEL_SYS_DATA *pSatelliteInfo;
    pSatelliteInfo = pSatelliteDeliveryInfo;

    while(NULL != pSatelliteInfo)
    {
        if(pSatelliteInfo->u16TSID == u16TSID)
        {
            stInfo.u32Freq = pSatelliteInfo->stSDS.u32Freq;
            stInfo.u32Symbol_rate = pSatelliteInfo->stSDS.u32Symbol_rate;
            stInfo.u8Modulation_type = pSatelliteInfo->stSDS.u8Modulation_type;
            stInfo.u8modulation_system = pSatelliteInfo->stSDS.u8modulation_system;
            stInfo.u8FEC_inner = pSatelliteInfo->stSDS.u8FEC_inner;
            stInfo.u8polarization = pSatelliteInfo->stSDS.u8polarization;

            return MAPI_TRUE;
        }
        pSatelliteInfo = pSatelliteInfo->next;
    }
    return MAPI_FALSE;
}
#endif
MW_DVB_SI_PSI_Parser::MW_DVB_SI_PSI_Parser()
{
    pthread_mutexattr_t attr;
    MW_SI_PARSER_MESSAGE("MW_DVB_SI_PSI_Parser::MW_DVB_SI_PSI_Parser\n");
    m_u8PmtMonitorTimerMultiple = 1;
    m_u16NewAddChnNum = 0;
    m_u32StartUpdateTime = INVALID_TIME;
    //for Filein SI parsing
    m_pFileinPatParser = NULL;
    m_bFileinParserRunning = MAPI_FALSE;
    memset(&m_stFileinPat,0,sizeof(m_stFileinPat));
    m_stFileinPat.u8Version= INVALID_PSI_SI_VERSION;
    memset(&m_stFileinCurPmt,0,sizeof(m_stFileinCurPmt));
    m_stFileinCurPmt.u8Version = INVALID_PSI_SI_VERSION;
    memset(m_pFileinAllPmtParser, 0, MAX_CHANNEL_IN_MUX * sizeof(mapi_si_PMT_parser *));
    memset(&m_FileinCurServiceInfo, 0, sizeof(m_FileinCurServiceInfo));
    //<
    m_u32CurPMTCRC32 = 0;
    m_pCableDeliveryInfo = NULL;
    m_pSatelliteDeliveryInfo = NULL;
    m_pCMDB = NULL;
    m_pPatParser = NULL;
    m_pSdtParser = NULL;
    m_pSdtOtherParser = NULL;
    m_pNitParser = NULL;
    m_pBatParser = NULL;
    m_pCurPmtParser = NULL;
    m_pCurMux = NULL;
    m_pCurNetwork = NULL;
    m_pCurSat = NULL;
    m_pSi = NULL;
#if (OAD_ENABLE == 1)
    m_OADParser = NULL;
    m_bOADONIDMatch = MAPI_TRUE;
#endif //(OAD_ENABLE == 1)
    m_pOtherPmtParser = NULL;
    m_pTdtParser = NULL;
    m_pTotParser = NULL;
    m_pAitParser = NULL;
    m_pEitSch1Parser = NULL;
    m_pEitSch2Parser = NULL;
    m_pEitPFParser = NULL;
    m_pEitPfAllParser = NULL;
    m_pRctParser = NULL;
#if (ASTRA_SGT_ENABLE == 1)
    m_pSgtParser = NULL;
#endif
#if ((OAD_ENABLE == 1) && (SDTT_OAD_ENABLE==1))
    m_pSdttParser = NULL;
    m_SDTT_PID = INVALID_PID;
#endif
    m_pCurProg = NULL;
    m_pDemux = NULL;
#if (ISDB_SYSTEM_ENABLE == 1)
    m_pCdtParser = NULL;
    m_bEnableCdtMonitorFlag = FALSE;
    memset(&m_CurServiceLogoInfo, 0, sizeof(DESC_LOGO_TRANSMISSION));
    m_CurServiceLogoInfo.pu8LogoData = NULL;
    m_u8CurRFChannel = MAX_PHYSICAL_CHANNEL_NUM;
    m_bDisableOneSegProgFilter = FALSE;
#endif

#if (CA_ENABLE == 1)
    m_pCaClient = NULL;
#endif

#if EIT_BUFFER_USE_QUEUE
    m_s16EITBufferAllocCnt = 0;
#endif

    _pfMonitorNotify = NULL;
    m_pMonitorNotifyUsrParam = NULL;
    _pfScanResultNotify = NULL;
    m_bEnableNetworkFilter = MAPI_FALSE;
    m_bEnableBouquetFilter = MAPI_FALSE;
    m_bGotBatInScan = MAPI_FALSE;
    m_u16NetworkID = INVALID_NID;
    m_u16BouquetID = 0;
    m_u32CurCRCValue = 0;
    m_u32PostSendCiProtectionEventTime = 0;
    memset(m_pAllPmtParser, 0, MAX_CHANNEL_IN_MUX * sizeof(mapi_si_PMT_parser *));
    memset(m_aPMTWaitFilter, MAPI_FALSE, MAX_CHANNEL_IN_MUX * sizeof(MAPI_BOOL));

    memset(&m_stBat, 0, sizeof(m_stBat));
    m_stBat.u8Version = INVALID_PSI_SI_VERSION;
    memset(&m_stNit, 0, sizeof(m_stNit));
#if (ASTRA_SGT_ENABLE == 1 )
    memset(&m_stSgt, 0, sizeof(MAPI_SI_TABLE_SGT));
#endif
    //_ResetInfo(RESET_ALL);
    //_ResetStatus();
    memset(&m_stTargetRegionInfo, 0, sizeof(MW_DVB_TARGET_REGION_INFO));
    memset(&m_stAllServiceTargetRegionInfo, 0, sizeof(MW_DVB_ALL_SERVICE_REGION_INFO));
    memset(&m_CurRPServiceInfo, 0, sizeof(MW_DVB_RP_SERVICE_INFO));
    memset(&m_ReplacedServiceInfo,0,sizeof(MW_DVB_RP_SERVICE_INFO));
    memset(m_au16NewProg2Add,0,sizeof(m_au16NewProg2Add));
    memset(&m_CurServiceInfo,0,sizeof(MW_DVB_SI_ServicelInfo));
    memset(&m_stNit_UpdateInfo,0,sizeof(MW_SI_NIT_AUTOUPDATE_INFO));

    m_u16OldSID = INVALID_SERVICE_ID;
    m_u16OldPMTPID = INVALID_PMT_PID;
    m_u32WaitPMTTimer = INVALID_TIME;
    m_bGotTDT_Ready = FALSE;
    m_bGotTOT_Ready = FALSE;
    m_u8TOTIndex = 0xFF;
    m_u32OldNextTimeOffset = 0;
    m_s32OffsetTime = 0;
    m_u8SummerTimeOffset = 0;
    m_u8CurRatingChkValue = 0;
    m_u32MonitorUserData = 0;
    m_bNitUpdate2Rescan = MAPI_FALSE;
    m_enMonitorMode = EN_MW_SI_MONITOR_MODE_NORMAL;

    m_s16OpenFilter = 0;
    m_eParserMode = E_DVB_IDLE;
    m_bExitParser = MAPI_FALSE;
    m_bInit = MAPI_FALSE;
    m_bRunning = MAPI_FALSE;
#if (CI_PLUS_ENABLE == 1)
    m_bIsOpMode = MAPI_FALSE;
    for(int i=0; i<MAPI_SI_MAX_CI_CONTENT_LABEL; i++)
    {
        memset(&m_stContentLabel[i], 0 ,sizeof(MW_SI_CIPLUS_CONTENT_LABEL));
    }
#endif
#if (DVBS_SYSTEM_ENABLE == 1)
    m_DvbsScanTpMap.clear();
#endif
#if (ASTRA_SGT_ENABLE == 1 )
    m_ServiceListBasicInfo.clear();
    m_enSgtSource = E_SGT_TP_NUM;
    m_u16SateScanTsid = INVALID_TS_ID;
    m_u16ServiceListId = INVALID_SERVICE_LIST_ID;
    m_enSgtFilterType = E_SGT_FITLER_TYPE_NUM;
#endif
    memset(&m_stTot, 0, sizeof(MAPI_SI_TABLE_TOT));
    mapi_si_PAT_parser::Reset(m_stPat);
    memset(&m_stNit, 0, sizeof(MAPI_SI_TABLE_NIT));
    memset(&m_stSdt, 0, sizeof(MAPI_SI_TABLE_SDT));
    memset(&m_stBat, 0, sizeof(MAPI_SI_TABLE_BAT));
    memset(&m_stCurPmt, 0, sizeof(MAPI_SI_TABLE_PMT));
    memset(m_astAllPmt, 0, sizeof(m_astAllPmt));
    memset(&m_stTot, 0, sizeof(MAPI_SI_TABLE_TOT));
    memset(&m_TsInfo, 0, sizeof(MW_DVB_TS_INFO));
    memset(m_ProgramInfo, 0, sizeof(m_ProgramInfo));
    memset(m_EitPfInfo, 0, sizeof(m_EitPfInfo));
    memset(m_PFComponentInfo, 0, sizeof(m_PFComponentInfo));
    memset(&m_stService_CMG, 0, sizeof(MW_DVB_CMG_INFO));
    memset(&m_stScanRemoveRFInfo, 0, sizeof(MW_DVB_SCAN_REMOVE_RF_INFO));
    m_enCableOperator=EN_CABLEOP_OTHER;
    m_enSatellitePlatform=EN_SATEPF_OTHER;
    memset(&m_SpecifyMheg5Service, 0, sizeof(MAPI_SI_TABLE_PMT));

    m_bEPGBarkerChannelWorking = MAPI_FALSE;
    memset(&m_NitLinkageEPG, 0x00, sizeof(MAPI_SI_DESC_LINKAGE_INFO));
    //Create the crid db as an singleton object
    mapi_epg_criddb::GetInstance();
#if EIT_BUFFER_USE_QUEUE
    m_bResetEitParser = MAPI_FALSE;
#endif
    m_pSiEvent = new (std::nothrow) mapi_event<mapi_si_psi_event>;
    ASSERT(m_pSiEvent);
    m_ParserCallBackInfo.pSiEvent = m_pSiEvent;

#if EIT_BUFFER_USE_QUEUE
    m_pEitEvent = new (std::nothrow) mapi_event<mapi_si_psi_event>;
    ASSERT(m_pEitEvent);
    m_ParserCallBackInfo.pEitEvent = m_pEitEvent;
#endif

    m_pParserTriggerEvent = new (std::nothrow) mapi_event<mapi_si_psi_event>;
    ASSERT(m_pParserTriggerEvent);

    m_bIsSIAutoUpdateOff = MAPI_FALSE;
    m_bIsSIDynmaicReScanOff = MAPI_FALSE;
    m_bforceNetworkCheck = MAPI_TRUE;
    m_enQuickScanState = EN_QUICK_SCAN_STATE_IDLE;
    m_bForceRetrievePfEIT = MAPI_FALSE;
    memset(m_pAllSdtParser, 0, MAPI_SI_MAX_TS_IN_NETWORK*sizeof(mapi_si_SDT_parser *));
    memset(&m_stCableParam, 0, sizeof(MS_CABLE_PARAMETER));
    m_pstAllSdt = NULL;
    memset(&m_stUpdateStatus, 0, sizeof(m_stUpdateStatus));

    m_u32StreamUTCTime = 0;
    m_u32StreamUTCTime0 = 0;
    mapi_si_dvb_parser::InitParser();

    //Correct the init squence
    _ResetInfo(RESET_ALL);
    _ResetStatus();

    int intPTHChk;
    PTH_RET_CHK(pthread_mutexattr_init(&attr));
    PTH_RET_CHK(pthread_mutexattr_settype(&attr, PTHREAD_MUTEX_RECURSIVE));

    intPTHChk = PTH_RET_CHK(pthread_mutex_init(&m_pParseMonitorMutex, &attr));
    if(intPTHChk != 0)
    {
        ASSERT(0);
        return;
    }
    pthread_attr_t thr_attr;
    pthread_attr_init(&thr_attr);
    pthread_attr_setstacksize(&thr_attr, PTHREAD_STACK_SIZE);
    intPTHChk = PTH_RET_CHK(pthread_create(&m_ParserMonitorThread, &thr_attr, _ParserMonitor_Task, this));
    if(intPTHChk != 0)
    {
        m_bExitParser = MAPI_TRUE;
        pthread_mutex_destroy(&m_pParseMonitorMutex);
        ASSERT(0);
        return;
    }

#if EIT_BUFFER_USE_QUEUE
    intPTHChk = PTH_RET_CHK(pthread_create(&m_EitParserMonitorThread, &thr_attr, _EitParserMonitor_Task, this));
    if(intPTHChk != 0)
    {

        intPTHChk = PTH_RET_CHK(pthread_join(m_ParserMonitorThread, NULL));
        ASSERT(intPTHChk == 0);

        m_bExitParser = MAPI_TRUE;
        pthread_mutex_destroy(&m_pParseMonitorMutex);
        ASSERT(0);
        return;
    }
#endif
#if (NCD_ENABLE == 1)
    m_stTripleIdBeforeNC.u16TsId = INVALID_TS_ID;
    m_stTripleIdBeforeNC.u16OnId = INVALID_ON_ID;
    m_stTripleIdBeforeNC.u16SrvId = INVALID_SERVICE_ID;
    m_stRelocatedServiceInfoList.clear();
#endif
    for(int i = 0; i < MAX_SPECIAL_SERVICE_NUM; i++)
    {
        m_astSpecialService[i].u16OnId = INVALID_ON_ID;
        m_astSpecialService[i].u16SrvId = INVALID_SERVICE_ID;
        m_astSpecialService[i].u16TsId = INVALID_TS_ID;
        m_pSpecialServicesParser[i] = NULL;
    }
#if (OAD_ENABLE == 1)
    m_bOadStopEit = FALSE;
#endif
    m_vPatCRC32.clear();
    m_s8RegionalListID = -1;
}

MW_DVB_SI_PSI_Parser::~MW_DVB_SI_PSI_Parser()
{
    m_ListRPServiceInfo.clear();
#if (NCD_ENABLE == 1)
    m_stNCDInfoList.clear();
    m_stRelocatedServiceInfoList.clear();
#endif
    m_u16NIDList.clear();
    m_pNitParserList.clear();
    m_bExitParser = MAPI_TRUE;
    int intPTHChk;
#if EIT_BUFFER_USE_QUEUE
    intPTHChk = PTH_RET_CHK(pthread_join(m_EitParserMonitorThread, NULL));
    ASSERT(intPTHChk == 0);
#endif
    intPTHChk = PTH_RET_CHK(pthread_join(m_ParserMonitorThread, NULL));
    ASSERT(intPTHChk == 0);

    if(NULL != m_pSiEvent)
    {
        delete m_pSiEvent;
        m_pSiEvent = NULL;
    }

#if EIT_BUFFER_USE_QUEUE
    if(NULL != m_pEitEvent)
    {
        mapi_si_psi_event cEvt;
        while(m_pEitEvent->Wait(&cEvt, 0) == 0)
        {
            ASSERT(cEvt.u32Param2);
            free((void*)cEvt.u32Param2);
            m_s16EITBufferAllocCnt--;
    #if EIT_BUFFER_DBG
            _u32BufferFreeCnt++;
    #endif
        }
    #if EIT_BUFFER_DBG
        ASSERT(_u32BufferAllocCnt == _u32BufferFreeCnt);
    #endif

        delete m_pEitEvent;
        m_pEitEvent = NULL;
    }
#endif

    if(NULL != m_pParserTriggerEvent)
    {
        delete m_pParserTriggerEvent;
        m_pParserTriggerEvent = NULL;
    }


    mapi_epg_criddb::GetInstance()->DelInstance();
    _FreeDeliveryInfo(m_stNit, MAPI_TRUE);
    if(m_bEnableBouquetFilter)
    {
        mapi_utility::freeList(&m_stBat.pstTSInfoList);
    }
    _free_SI_TABLE_NIT(m_stNit);
#if (ISDB_SYSTEM_ENABLE == 1)
    FREE(m_CurServiceLogoInfo.pu8LogoData);
#endif
    pthread_mutex_destroy(&m_pParseMonitorMutex);
}

void MW_DVB_SI_PSI_Parser::Init(mapi_si_psi * const pSi, void * const pCMDB, void * const pCallbackReference, funCallbackPmtNotify pPmtNotify, void (*PMTNotifyCA)(MAPI_U8 * pu8Section), funCallbackCiProtectNotify pCiProtectNotify, void (*SI_EventHandler)(MAPI_U32 u32Cmd, MAPI_U32 param1, MAPI_U32 param2), mapi_demux* pDemux)
{
    ASSERT(pSi);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    if(m_bInit)
    {
        ASSERT(0);
    }
    m_pSi = pSi;
    m_pSi->SetHuffmanTable(_DecodeTblType1,_DecodeTblType2);



    m_pCMDB = (DVB_CM*)pCMDB;
    m_ParserCallBackInfo.pCallbackReference = pCallbackReference;
    m_ParserCallBackInfo.PMTSectionNotify = pPmtNotify;
    m_ParserCallBackInfo.PMTSectionNotifyCA = PMTNotifyCA;
    m_ParserCallBackInfo.CIProtectionNotify = pCiProtectNotify;
    m_ParserCallBackInfo.EventHandler = SI_EventHandler;
    m_ParserCallBackInfo.pDvbSiPsiParser = this;

    m_eCountry = E_UK;
    m_eLanguage = E_LANGUAGE_ENGLISH;
    m_bInit = MAPI_TRUE;
    m_eClockMode = MW_DVB_CLOCK_AUTO;
    m_eParserBaseType = MW_DVB_SDT_PAT_BASE;
    m_bServiceIDUnique = MAPI_FALSE;
    m_bGotTDT_Ready = FALSE;
    m_bGotTOT_Ready = FALSE;
    m_enCableOperator = EN_CABLEOP_OTHER;
    m_enSatellitePlatform = EN_SATEPF_OTHER;
    memset(&m_stNit, 0, sizeof(MAPI_SI_TABLE_NIT));
    memset(&m_stBat, 0, sizeof(m_stBat));
    m_stBat.u8Version = INVALID_PSI_SI_VERSION;
    mapi_epg_criddb::GetInstance()->Clear();
    m_pDemux = (pDemux != NULL)?pDemux:mapi_interface::Get_mapi_demux();
    ASSERT(m_pDemux);
    m_u16FileInSrvId = INVALID_SERVICE_ID;
#if (ISDB_SYSTEM_ENABLE == 1)
    m_eSIMonitorDebugMode = E_ALL_MONITOR_ON;
#endif
    m_s8RegionalListID = -1;
    //Set(MW_DVB_T_PARSER,SI_COUNTRY_UK,SI_TIMEZONE_LONDON,EN_SI_Clock_TimeZone_0,MAPI_FALSE, 1970,E_LANGUAGE_ENGLISH);
}

void MW_DVB_SI_PSI_Parser::SetVariable(MEMBER_COUNTRY eCountry, EN_TIMEZONE enTimeZone,
    MAPI_BOOL bEnableManualCalculateOffsettime, MAPI_U16 u16DefaultYear,
    MEMBER_LANGUAGE eLanguage)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    if (m_eCountry != eCountry)
    {
        m_bGotTDT_Ready = MAPI_FALSE;
        m_bGotTOT_Ready = MAPI_FALSE;
        m_u8TOTIndex = 0xFF;
        m_u32OldNextTimeOffset = 0;
        m_s32OffsetTime = 0;
        memset(&m_stTot, 0, sizeof(MAPI_SI_TABLE_TOT));
    }
    if (m_enTimeZone != enTimeZone)
    {
        MAPI_S32 s32TempTime;
        mapi_system * system = mapi_interface::Get_mapi_system();
        ASSERT(system);

        s32TempTime = mapi_dvb_utility::SI_GetTimeZoneOffset(mapi_dvb_utility::SI_GetTimeOffset(m_enTimeZone));
        s32TempTime += m_u8SummerTimeOffset * SI_SECONDS_PER_HOUR;
        //system->SetTimeOfChange(0);
        //system->SetNextTimeOffset(0);
        _SetTimeOfChange(0);
        _SetNextTimeOffset(0);
        //system->SetClockOffset(s32TempTime);
    }
    m_eCountry = eCountry;
    m_eLanguage = eLanguage;
    m_enTimeZone = enTimeZone;
    m_bEnableManualCalculateOffsettime = bEnableManualCalculateOffsettime;
    m_u16DefaultYear = u16DefaultYear;
    mapi_si_dvb_parser::Set(m_eCountry, m_eLanguage, m_enCableOperator, m_enSatellitePlatform);

}
void MW_DVB_SI_PSI_Parser::SetLanguage(MEMBER_LANGUAGE eLanguage)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    m_eLanguage = eLanguage;
    mapi_si_dvb_parser::Set(m_eCountry, m_eLanguage, m_enCableOperator, m_enSatellitePlatform);
    _ResetInfo(RESET_EIT);
}
void MW_DVB_SI_PSI_Parser::SetCountry(MEMBER_COUNTRY eCountry)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    if (m_eCountry != eCountry)
    {
        m_bGotTDT_Ready = MAPI_FALSE;
        m_bGotTOT_Ready = MAPI_FALSE;
        m_u8TOTIndex = 0xFF;
        m_u32OldNextTimeOffset = 0;
        m_s32OffsetTime = 0;
        memset(&m_stTot, 0, sizeof(MAPI_SI_TABLE_TOT));
    }
    m_eCountry = eCountry;
    mapi_si_dvb_parser::Set(m_eCountry, m_eLanguage, m_enCableOperator, m_enSatellitePlatform);
}

void MW_DVB_SI_PSI_Parser::SetTimeZone(EN_TIMEZONE enTimeZone)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    if (m_enTimeZone != enTimeZone)
    {
        MAPI_S32 s32TempTime;
        mapi_system * system = mapi_interface::Get_mapi_system();
        ASSERT(system);

        s32TempTime = mapi_dvb_utility::SI_GetTimeZoneOffset(mapi_dvb_utility::SI_GetTimeOffset(m_enTimeZone));
        s32TempTime += m_u8SummerTimeOffset * SI_SECONDS_PER_HOUR;
        //system->SetTimeOfChange(0);
        //system->SetNextTimeOffset(0);
        _SetTimeOfChange(0);
        _SetNextTimeOffset(0);
        //system->SetClockOffset(s32TempTime);
    }
    m_enTimeZone = enTimeZone;
}
void MW_DVB_SI_PSI_Parser::SetParserType(MW_DVB_PARSER_TYPE eParserType)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    m_eParserType = eParserType;
}

void MW_DVB_SI_PSI_Parser::SetMonitorNotify(void (*MonitorNotify)(MW_DVB_SI_MONITOR_EVENT eEvent, void *pInfo, void *pUsrParam1, void *pUsrParam2), void *pUsrParam)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    _pfMonitorNotify = MonitorNotify;
    m_pMonitorNotifyUsrParam = pUsrParam;
}
void MW_DVB_SI_PSI_Parser::SetClockMode(MW_DVB_CLOCK_MODE eClockMode)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    m_eClockMode = eClockMode;
#if (ISDB_SYSTEM_ENABLE == 0)
    if(m_eClockMode == MW_DVB_CLOCK_MANUAL)
    {
        mapi_si_psi_event cEvt;
        cEvt.u32Event = (MAPI_U32) EN_DVB_PARSER_STOP_CLOCK;
        m_pParserTriggerEvent->Send(cEvt);
    }
#endif
}
void MW_DVB_SI_PSI_Parser::SetParserBaseType(MW_DVB_PARSER_BASE_TYPE etype)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    m_eParserBaseType = etype;
    if((m_eParserBaseType == MW_DVB_PAT_BASE) && m_bServiceIDUnique)
    {
        MW_SI_PARSER_ERROR("can not support unique service id in PAT base\n");
        ASSERT(0);
    }
}

#if (DVBC_SYSTEM_ENABLE ==1)
void MW_DVB_SI_PSI_Parser::SetOperator(EN_CABLE_OPERATORS enOperator)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    m_enCableOperator = enOperator;
    mapi_si_dvb_parser::Set(m_eCountry, m_eLanguage, m_enCableOperator, m_enSatellitePlatform);
}
#endif

void MW_DVB_SI_PSI_Parser::SetUniqueServiceID(MAPI_BOOL bSIDUnique)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    m_bServiceIDUnique = bSIDUnique;
    if((m_eParserBaseType == MW_DVB_PAT_BASE) && m_bServiceIDUnique)
    {
        MW_SI_PARSER_ERROR("can not support unique service id in PAT base\n");
        ASSERT(0);
    }
}

#if (DVBS_SYSTEM_ENABLE == 1)
void MW_DVB_SI_PSI_Parser::SetSatePlatform(EN_SATELLITE_PLATFORM enSatePlatform)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    m_enSatellitePlatform = enSatePlatform;
    mapi_si_dvb_parser::Set(m_eCountry, m_eLanguage, m_enCableOperator, m_enSatellitePlatform);
}
#endif

void MW_DVB_SI_PSI_Parser::SetRegionalListID(MAPI_S8 s8RegionalListID)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    m_s8RegionalListID = s8RegionalListID;
}

void MW_DVB_SI_PSI_Parser::StartScan(void (*pfSIScanResultNotify)(MW_DVB_SCAN_RESULT enEvent, MAPI_U32 u32Param1, MW_DVB_TS_INFO *pTsInfo, MW_DVB_PROGRAM_INFO *pServiceInfo), MAPI_U32  u32ScanResultNotifyParam1)
{
    ASSERT(m_bInit);

    m_u32ScanResultNotifyParam1 = u32ScanResultNotifyParam1;
    _pfScanResultNotify = pfSIScanResultNotify;
    mapi_si_psi_event cEvt;
    cEvt.u32Event = (MAPI_U32) EN_DVB_SCAN_START;
    m_pParserTriggerEvent->Send(cEvt);
}

#if (DVBC_SYSTEM_ENABLE == 1)
void MW_DVB_SI_PSI_Parser::StartQuickScan(void (*pfSIScanResultNotify)(MW_DVB_SCAN_RESULT enEvent, MAPI_U32 u32Param1, MW_DVB_TS_INFO *pTsInfo, MW_DVB_PROGRAM_INFO *pServiceInfo),
                                                MAPI_U32  u32ScanResultNotifyParam1, const MS_CABLE_PARAMETER &stCableParam)
{
    ASSERT(m_bInit);

    m_u32ScanResultNotifyParam1 = u32ScanResultNotifyParam1;
    _pfScanResultNotify = pfSIScanResultNotify;
    m_stCableParam = stCableParam;
    mapi_si_psi_event cEvt;
    cEvt.u32Event = (MAPI_U32) EN_DVB_QUICK_SCAN_START;
    m_pParserTriggerEvent->Send(cEvt);
}
#endif

void MW_DVB_SI_PSI_Parser::StopScan()
{
    ASSERT(m_bInit);
    mapi_si_psi_event cEvt;
    cEvt.u32Event = (MAPI_U32) EN_DVB_PARSER_STOP_ALL;
    m_pParserTriggerEvent->Send(cEvt);
}
void MW_DVB_SI_PSI_Parser::FinishScan()
{
    ASSERT(m_bInit);
    mapi_si_psi_event cEvt;
    cEvt.u32Event = (MAPI_U32) EN_DVB_SCAN_FINISH;
    m_pParserTriggerEvent->Send(cEvt);
}

void MW_DVB_SI_PSI_Parser::StartMonitor(MAPI_U32 u32UserData, EN_MW_SI_MONITOR_MODE enMonitorMode)
{
    ASSERT(m_bInit);
    mapi_si_psi_event cEvt;
    cEvt.u32Event = (MAPI_U32) EN_DVB_MONITOR_START;
    cEvt.u32Param1 = u32UserData;
    cEvt.u32Param2 = enMonitorMode;
    m_pParserTriggerEvent->Send(cEvt);
}
#if (OAD_ENABLE == 1)
void MW_DVB_SI_PSI_Parser::StartOadScan()
{
    ASSERT(m_bInit);
    mapi_si_psi_event cEvt;
    cEvt.u32Event = (MAPI_U32) EN_DVB_OAD_SCAN_START;
    m_pParserTriggerEvent->Send(cEvt);
}

void MW_DVB_SI_PSI_Parser::FinishOadScan()
{
    ASSERT(m_bInit);
    mapi_si_psi_event cEvt;
    cEvt.u32Event = (MAPI_U32) EN_DVB_OAD_GO_DOWNLOAD;
    m_pParserTriggerEvent->Send(cEvt);
}
void MW_DVB_SI_PSI_Parser::StopOadScanMonitor()
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    if(m_eParserMode == E_DVB_OAD_DOWNLOAD)
    {
        m_bRunning = MAPI_FALSE;
        m_eParserMode = E_DVB_IDLE;
    }
}

#endif
//for Filein SI parsing
void MW_DVB_SI_PSI_Parser::StartFileinMonitor(MAPI_U16 u16SrvId)
{
    ASSERT(m_bInit);
    if (u16SrvId == INVALID_SERVICE_ID)
    {
        printf("[%s:%s][%d] Invalid service ID !!! \033[0m\n",__FILE__,__FUNCTION__,__LINE__);
        return;
    }
    m_u16FileInSrvId = u16SrvId;
    mapi_si_psi_event cEvt;
    cEvt.u32Event = (MAPI_U32) EN_DVB_FILE_IN_MONITOR_START;
    m_pParserTriggerEvent->Send(cEvt);
}
void MW_DVB_SI_PSI_Parser::StopFileinMonitor()
{
    ASSERT(m_bInit);
    m_u16FileInSrvId = INVALID_SERVICE_ID;
    mapi_si_psi_event cEvt;
    cEvt.u32Event = (MAPI_U32) EN_DVB_FILE_IN_MONITOR_STOP;
    m_pParserTriggerEvent->Send(cEvt);
}
//<

void MW_DVB_SI_PSI_Parser::StopMonitor()
{
    ASSERT(m_bInit);
    //mapi_scope_lock(m_pParseMonitorMutex);
    mapi_si_psi_event cEvt;
    CLEAR_TRIGGER_EVENT(m_pParserTriggerEvent, &cEvt);
    cEvt.u32Event = (MAPI_U32) EN_DVB_PARSER_STOP;
    m_pParserTriggerEvent->Send(cEvt);
    MW_SI_PARSER_MESSAGE("StopMonitor wait start\n");
    while(m_eParserMode != E_DVB_IDLE)
    {
        sleep(0);
    }
    MW_SI_PARSER_MESSAGE("StopMonitor wait end\n");
}
void MW_DVB_SI_PSI_Parser::StopParser()
{
    ASSERT(m_bInit);
    //mapi_scope_lock(m_pParseMonitorMutex);
    mapi_si_psi_event cEvt;
    CLEAR_TRIGGER_EVENT(m_pParserTriggerEvent, &cEvt);
    cEvt.u32Event = (MAPI_U32) EN_DVB_PARSER_STOP_ALL;
    m_pParserTriggerEvent->Send(cEvt);
    MW_SI_PARSER_MESSAGE("StopParser wait start\n");
    while(m_eParserMode != E_DVB_IDLE)
    {
        sleep(0);
    }
    MW_SI_PARSER_MESSAGE("StopParser wait end\n");
}

void MW_DVB_SI_PSI_Parser::StartEPGBarkerChannel(void)
{
    ASSERT(m_bInit);
    mapi_si_psi_event cEvt;
    cEvt.u32Event = (MAPI_U32) EN_DVB_EPG_BARKERCHANNEL_START;
    m_pParserTriggerEvent->Send(cEvt);
}

void MW_DVB_SI_PSI_Parser::SetNetwokScan(MAPI_BOOL bEnable, MAPI_U16 u16NID)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    m_bEnableNetworkFilter = bEnable;
    if(m_bEnableNetworkFilter)
    {
        m_u16NetworkID = u16NID;
    }
}
void MW_DVB_SI_PSI_Parser::SetBouquet_Info(MAPI_BOOL bEnable, MAPI_U16 u16BID)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    m_bEnableBouquetFilter = bEnable;
    m_u16BouquetID = u16BID;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::IsServiceChange(MW_DVB_SI_ServicelInfo *pServiceInfo)
{
    ASSERT(m_bInit);
    ASSERT(pServiceInfo);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    if(m_CurServiceInfo.eChangeType == MW_SERVICE_NO_CHANGE)
    {
        return MAPI_FALSE;
    }
    memcpy(pServiceInfo, &m_CurServiceInfo, sizeof(MW_DVB_SI_ServicelInfo));
    m_CurServiceInfo.eChangeType = MW_SERVICE_NO_CHANGE;
    return MAPI_TRUE;
}


MAPI_BOOL MW_DVB_SI_PSI_Parser::GetSatInfo(MAPI_U16 &u16Orbital_position, MAPI_U8 &u8West_east_flag)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    u16Orbital_position=0;
    u8West_east_flag=0;
    if(NULL != m_pSatelliteDeliveryInfo)
    {
       u16Orbital_position = m_pSatelliteDeliveryInfo->stSDS.u16Orbital_position;
       u8West_east_flag = m_pSatelliteDeliveryInfo->stSDS.u8West_east_flag;
       return TRUE;
    }

    return FALSE;
}


MAPI_BOOL MW_DVB_SI_PSI_Parser::GetServiceInfo(MW_DVB_SI_ServicelInfo *pServiceInfo)
{
    ASSERT(m_bInit);
    ASSERT(pServiceInfo);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    memcpy(pServiceInfo, &m_CurServiceInfo, sizeof(MW_DVB_SI_ServicelInfo));
    if((m_stCurPmt.u8Version == INVALID_PSI_SI_VERSION) ||
            (m_stCurPmt.u8Version == SI_PSI_FORCE_UPDATE_VER))
    {
        return MAPI_FALSE;
    }
    return MAPI_TRUE;
}
MAPI_BOOL MW_DVB_SI_PSI_Parser::IsPFInfoAvaiIable()
{
    if((m_stSdt.u8Version != INVALID_PSI_SI_VERSION) && (m_CurServiceInfo.bEit_pf_flag == MAPI_FALSE))
        return MAPI_FALSE;
    return MAPI_TRUE;
}
MAPI_BOOL MW_DVB_SI_PSI_Parser::GetEitPfInfo(MAPI_EIT_CUR_EVENT_PF *pEitPFInfo, MAPI_BOOL bPresent)
{
    MAPI_U8 index;
    ASSERT(m_bInit);
    ASSERT(pEitPFInfo);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    index = (bPresent == MAPI_TRUE) ? 0 : 1;
    if((m_EitPfInfo[index].version_number == INVALID_PSI_SI_VERSION)
            || (MAPI_FALSE == IsPFInfoAvaiIable()))
    {
        memset(pEitPFInfo, 0, sizeof(MAPI_EIT_CUR_EVENT_PF));
        return MAPI_FALSE;
    }
    memcpy(pEitPFInfo, &m_EitPfInfo[index], sizeof(MAPI_EIT_CUR_EVENT_PF));

    return MAPI_TRUE;
}

MAPI_U8 MW_DVB_SI_PSI_Parser::GetParentalRating(BOOL bPresent)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
#if (ISDB_SYSTEM_ENABLE == 1)
    if( m_u8PmtParentalControl != INVALID_PARENTAL_RATING )
    {
        MW_SI_PARSER_MESSAGE("GetParentalRating from PMT table %d\n",m_u8PmtParentalControl);
        return m_u8PmtParentalControl;
    }
#endif

    if(((m_EitPfInfo[0].version_number == INVALID_PSI_SI_VERSION) && bPresent)
        ||((m_EitPfInfo[1].version_number == INVALID_PSI_SI_VERSION) && (!bPresent))
        || (MAPI_FALSE == IsPFInfoAvaiIable()))
    {
        return 0;
    }
    MAPI_U8 u8ParentalRate;
    if(bPresent)
    {
        u8ParentalRate =  m_EitPfInfo[0].u8Parental_Control;
    }
    else
    {
        u8ParentalRate =  m_EitPfInfo[1].u8Parental_Control;
    }
    MW_SI_PARSER_MESSAGE("GetParentalRating from EIT table %d\n", u8ParentalRate);
    return u8ParentalRate;
}

MAPI_U8 MW_DVB_SI_PSI_Parser::_GetRating(void)
{
    ASSERT(m_bInit);
#if (ISDB_SYSTEM_ENABLE == 1)
    if( m_u8PmtParentalControl != INVALID_PARENTAL_RATING )
    {
        MW_SI_PARSER_MESSAGE("GetParentalRating from PMT table %d\n",m_u8PmtParentalControl);
        return m_u8PmtParentalControl;
    }
#endif
    MAPI_U8 u8ParentalCtl = 0;
    MAPI_U32 u32EvtStartTime;
    MAPI_U32 u32EvtEndTime;
    mapi_system * system = mapi_interface::Get_mapi_system();
    ASSERT(system);

    if (m_EitPfInfo[0].version_number != INVALID_PSI_SI_VERSION)
    {
        u32EvtEndTime = mapi_dvb_utility::SI_MJDUTC2Seconds(m_EitPfInfo[0].start_time, NULL);
        #if (ISDB_SYSTEM_ENABLE == 1)
        u32EvtEndTime = GET_REAL_UTC_TIME_BY_COUNTRY(m_eCountry, u32EvtEndTime);
        #endif
        u32EvtEndTime += mapi_dvb_utility::SI_UTC2Seconds(m_EitPfInfo[0].duration);
        if (GetUtcTimeForCheckingEitValidation() > u32EvtEndTime)
        {
            if (m_EitPfInfo[1].version_number != INVALID_PSI_SI_VERSION)
            {
                // if current system time is great than event end time, following event is use instead.
                u8ParentalCtl =  m_EitPfInfo[1].u8Parental_Control;
            }
            else
            {
                u8ParentalCtl =  m_EitPfInfo[0].u8Parental_Control;
            }
        }
        else
        {
            u8ParentalCtl =  m_EitPfInfo[0].u8Parental_Control;
        }
    }
    else
    {
        if (m_EitPfInfo[1].version_number != INVALID_PSI_SI_VERSION)
        {
            u32EvtStartTime = mapi_dvb_utility::SI_MJDUTC2Seconds(m_EitPfInfo[1].start_time, NULL);
            #if (ISDB_SYSTEM_ENABLE == 1)
            u32EvtStartTime = GET_REAL_UTC_TIME_BY_COUNTRY(m_eCountry, u32EvtStartTime);
            #endif
            u32EvtEndTime = u32EvtStartTime + mapi_dvb_utility::SI_UTC2Seconds(m_EitPfInfo[1].duration);
            if ((GetUtcTimeForCheckingEitValidation()>=u32EvtStartTime) && (GetUtcTimeForCheckingEitValidation()<=u32EvtEndTime))
            {
               u8ParentalCtl =  m_EitPfInfo[1].u8Parental_Control;
            }
            else
            {
                u8ParentalCtl = 0;
            }
        }
        else
        {
            u8ParentalCtl = 0;
        }
    }
    MW_SI_PARSER_MESSAGE("GetParentalRating from EIT table %d\n",u8ParentalCtl);
    return u8ParentalCtl;
}

MAPI_U8 MW_DVB_SI_PSI_Parser::_GetContent(void)
{
    ASSERT(m_bInit);
    MAPI_U8 u8ParentalCtl = 0;
    MAPI_U32 u32EvtStartTime;
    MAPI_U32 u32EvtEndTime;
    mapi_system * system = mapi_interface::Get_mapi_system();
    ASSERT(system);

    if (m_EitPfInfo[0].version_number != INVALID_PSI_SI_VERSION)
    {
        u32EvtEndTime = mapi_dvb_utility::SI_MJDUTC2Seconds(m_EitPfInfo[0].start_time, NULL);
        #if (ISDB_SYSTEM_ENABLE == 1)
        u32EvtEndTime = GET_REAL_UTC_TIME_BY_COUNTRY(m_eCountry, u32EvtEndTime);
        #endif
        u32EvtEndTime += mapi_dvb_utility::SI_UTC2Seconds(m_EitPfInfo[0].duration);
        if (GetUtcTimeForCheckingEitValidation()>u32EvtEndTime)
        {
            if (m_EitPfInfo[1].version_number != INVALID_PSI_SI_VERSION)
            {
                // if current system time is great than event end time, following event is use instead.
                u8ParentalCtl =  m_EitPfInfo[1].u8Parental_ObjectiveContent;
            }
            else
            {
                u8ParentalCtl =  m_EitPfInfo[0].u8Parental_ObjectiveContent;
            }
        }
        else
        {
            u8ParentalCtl =  m_EitPfInfo[0].u8Parental_ObjectiveContent;
        }
    }
    else
    {
        if (m_EitPfInfo[1].version_number != INVALID_PSI_SI_VERSION)
        {
            u32EvtStartTime = mapi_dvb_utility::SI_MJDUTC2Seconds(m_EitPfInfo[1].start_time, NULL);
            #if (ISDB_SYSTEM_ENABLE == 1)
            u32EvtStartTime = GET_REAL_UTC_TIME_BY_COUNTRY(m_eCountry, u32EvtStartTime);
            #endif
            u32EvtEndTime = u32EvtStartTime + mapi_dvb_utility::SI_UTC2Seconds(m_EitPfInfo[1].duration);
            if ((GetUtcTimeForCheckingEitValidation()>=u32EvtStartTime) && (GetUtcTimeForCheckingEitValidation()<=u32EvtEndTime))
            {
               u8ParentalCtl =  m_EitPfInfo[1].u8Parental_ObjectiveContent;
            }
            else
            {
                u8ParentalCtl = 0;
            }
        }
        else
        {
            u8ParentalCtl = 0;
        }
    }
    MW_SI_PARSER_MESSAGE("_GetContent from EIT table %d\n",u8ParentalCtl);
    return u8ParentalCtl;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::IsServiceRunning(void)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    if(m_stSdt.u8Version == INVALID_PSI_SI_VERSION)
    {
        return MAPI_TRUE;
    }
    if(m_eCountry == E_ITALY)
    {
        if(m_CurRPServiceInfo.eRunningStatus == MW_SI_SDT_RUN_NOT_RUN)
        {
            return MAPI_FALSE;
        }
    }
    else if(IS_DTG_COUNTRY(m_eCountry))
    {
        if((m_CurRPServiceInfo.eRunningStatus == MW_SI_SDT_RUN_NOT_RUN)
                || (m_CurRPServiceInfo.eRunningStatus == MW_SI_SDT_RUN_PAUSE)
                || (m_CurRPServiceInfo.eRunningStatus == MW_SI_SDT_RUN_START_SOON)
                || (m_CurRPServiceInfo.eRunningStatus == MW_SI_SDT_SERVICE_AIR_OFF))
        {
            return MAPI_FALSE;
        }
    }
    else if (_IsZiggoOperator())
    {
        if((m_CurRPServiceInfo.eRunningStatus == MW_SI_SDT_RUN_UNDEFINED)
                || (m_CurRPServiceInfo.eRunningStatus == MW_SI_SDT_RUN_NOT_RUN)
                || (m_CurRPServiceInfo.eRunningStatus == MW_SI_SDT_RUN_START_SOON)
                || (m_CurRPServiceInfo.eRunningStatus == MW_SI_SDT_RUN_PAUSE))
        {
            return MAPI_FALSE;
        }
    }
    else
    {
        if((m_CurRPServiceInfo.eRunningStatus == MW_SI_SDT_RUN_NOT_RUN)
                || (m_CurRPServiceInfo.eRunningStatus == MW_SI_SDT_RUN_PAUSE)
                || (m_CurRPServiceInfo.eRunningStatus == MW_SI_SDT_RUN_START_SOON))
        {
            return MAPI_FALSE;
        }
    }
    return MAPI_TRUE;
}


MAPI_BOOL MW_DVB_SI_PSI_Parser::GetNetworkName(MAPI_U8 *pNetworkName, MAPI_U8 *pu8Len, MAPI_U8 u8MaxLen)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    ASSERT(pNetworkName);
    ASSERT(pu8Len);
    if(m_stNit.u8NetWrokNameLen > 0)
    {
        *pu8Len = (m_stNit.u8NetWrokNameLen > u8MaxLen) ? u8MaxLen : m_stNit.u8NetWrokNameLen;
        memcpy(pNetworkName, m_stNit.au8NetWorkName, *pu8Len);
        return MAPI_TRUE;
    }
    *pu8Len = 0;
    return MAPI_FALSE;
}

MAPI_S32 MW_DVB_SI_PSI_Parser::GetOffsetTime(MAPI_U32 u32Seconds, MAPI_BOOL bIsStartTime, MAPI_BOOL *pbDayLightTime)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    MAPI_U8 u8Hour, u8Min;
    mapi_system * system = mapi_interface::Get_mapi_system();
    MAPI_S32 s32OffseTime, s32OffseTime1, s32OffseTime2, s32AnotherOffsetTime;
    ASSERT(m_bInit);

    ASSERT(system);
    s32OffseTime = system->GetClockOffset();
    if(NULL != pbDayLightTime)
    {
        *pbDayLightTime = MAPI_FALSE;
    }

    if(m_bGotTOT_Ready && (m_u8TOTIndex != 0xFF) && (m_u8TOTIndex < MAPI_SI_MAX_LTO_REGION_NUM))
    {
        MAPI_U32 TimeOfChange = mapi_dvb_utility::SI_MJDUTC2Seconds(m_stTot.stTotLto.aLTOInfo[m_u8TOTIndex].au8TimeOfChange, NULL);
        if(TimeOfChange)
        {
            #if (ISDB_SYSTEM_ENABLE == 1)
            TimeOfChange = GET_REAL_UTC_TIME_BY_COUNTRY(m_eCountry, TimeOfChange);
            #endif
            u8Hour = BCD2Dec(m_stTot.stTotLto.aLTOInfo[m_u8TOTIndex].au8LTO[0]);
            u8Min  = BCD2Dec(m_stTot.stTotLto.aLTOInfo[m_u8TOTIndex].au8LTO[1]);
            if(m_stTot.stTotLto.aLTOInfo[m_u8TOTIndex].u8LTOPolarity)
            {
                s32OffseTime1 = (MAPI_S32)(-((u8Hour * SI_SECONDS_PER_HOUR) + (u8Min * SI_SECONDS_PER_MIN)));
            }
            else
            {
                s32OffseTime1 = (MAPI_S32)((u8Hour * SI_SECONDS_PER_HOUR) + (u8Min * SI_SECONDS_PER_MIN));
            }
            #if (ISDB_SYSTEM_ENABLE == 1)
            s32OffseTime1 = GET_REAL_OFFSET_TIME_BY_COUNTRY(m_eCountry, s32OffseTime1);
            #endif
            u8Hour = BCD2Dec(m_stTot.stTotLto.aLTOInfo[m_u8TOTIndex].au8NextTimeOffset[0]);
            u8Min  = BCD2Dec(m_stTot.stTotLto.aLTOInfo[m_u8TOTIndex].au8NextTimeOffset[1]);
            if(m_stTot.stTotLto.aLTOInfo[m_u8TOTIndex].u8LTOPolarity)
            {
                s32OffseTime2 = (MAPI_S32)(-((u8Hour * SI_SECONDS_PER_HOUR) + (u8Min * SI_SECONDS_PER_MIN)));
            }
            else
            {
                s32OffseTime2 = (MAPI_S32)((u8Hour * SI_SECONDS_PER_HOUR) + (u8Min * SI_SECONDS_PER_MIN));
            }
            #if (ISDB_SYSTEM_ENABLE == 1)
            s32OffseTime2 = GET_REAL_OFFSET_TIME_BY_COUNTRY(m_eCountry, s32OffseTime2);
            #endif
            if((TimeOfChange < u32Seconds)
                || (((m_eCountry == E_UK) || (m_eCountry == E_NEWZEALAND))
                    && (bIsStartTime == MAPI_FALSE) && (TimeOfChange == u32Seconds))
                || ((bIsStartTime == MAPI_TRUE) && (TimeOfChange == u32Seconds))) //add check incorrect time_of_change case
            {
                s32OffseTime = s32OffseTime2;
                s32AnotherOffsetTime = s32OffseTime1;
            }
            else
            {
                s32OffseTime = s32OffseTime1;
                s32AnotherOffsetTime = s32OffseTime2;
            }
            if(m_stTot.stTotLto.aLTOInfo[m_u8TOTIndex].u8LTOPolarity)
            {
                if(s32OffseTime < s32AnotherOffsetTime)
                {
                    if(NULL != pbDayLightTime)*pbDayLightTime = MAPI_TRUE;
                }
            }
            else if(s32OffseTime > s32AnotherOffsetTime)
            {
                if(NULL != pbDayLightTime)*pbDayLightTime = MAPI_TRUE;
            }
        }
    }
    return s32OffseTime;
}

void MW_DVB_SI_PSI_Parser::_ParserPMT(MAPI_U8 *pu8SectionData)
{
    MAPI_U8 *pu8LoopPosition, *pu8Descriptor;
    MAPI_U8 u8Tag;
    MAPI_U16 u16TotalDescriptorLength, u16TotalParseLength
    , u16DescriptorLength, u16SectionLength, u16DbID;

#if (OAD_ENABLE == 1)
    MAPI_U16 u16ServiceID, u16DSMCC_PID;
    u16ServiceID = (pu8SectionData[3] << 8) | pu8SectionData[4];
#endif
    u16TotalDescriptorLength = ((pu8SectionData[10] & 0x0f) << 8) | pu8SectionData[11];
    pu8LoopPosition = pu8SectionData + 12 + u16TotalDescriptorLength;
    u16SectionLength = ((pu8SectionData[1] & 0x0f) << 8) | pu8SectionData[2];
    u16SectionLength += 3;
    //== Second loop ==/
    while((MAPI_U32) pu8LoopPosition < (((MAPI_U32)pu8SectionData + u16SectionLength) - 4))
    {
        u16TotalDescriptorLength = ((pu8LoopPosition[3] & 0xf) << 8) | pu8LoopPosition[4];
        pu8Descriptor = pu8LoopPosition + 5;
        u16TotalParseLength = 0;
        switch(pu8LoopPosition[0]) //stream type
        {
            case 0x0A://ST_DSMCC_DATA_TYPE_A:
            case 0x0B://ST_DSMCC_DATA_TYPE_B:
            case 0x0C://ST_DSMCC_DATA_TYPE_C:
            case 0x0D://ST_DSMCC_DATA_TYPE_D:
            case 0x0E://ST_DSMCC_DATA_TYPE_E:
            case 0x80://USER_PRIVATE:
            {
#if (OAD_ENABLE == 1)
                u16DSMCC_PID = ((pu8LoopPosition[1] & 0x1F) << 8) | pu8LoopPosition[2];
                //printf("u16DSMCC_PID %x\n",u16DSMCC_PID);
#endif
                u16TotalParseLength = 0;
                while(u16TotalParseLength < u16TotalDescriptorLength)
                {
                    u8Tag = pu8Descriptor[0];  //descripter Tag
                    //printf("u8Tag %x\n",u8Tag);
                    switch(u8Tag)
                    {
                        case TAG_DBID:
                        {
                            u16DbID = ((pu8Descriptor[2] << 8) | pu8Descriptor[3]);
                            if(((pu8Descriptor[1] - 2) > 0) && (u16DbID == DATA_BC_ID_SSU))
                            {
                                // Send OAD info & Update Status to OAD module.
#if (OAD_ENABLE == 1)
                                if(NULL != m_OADParser)
                                {
                                    m_OADParser->SetPmtSignal(u16DSMCC_PID, u16DbID, &pu8Descriptor[4] , TAG_DBID, u16ServiceID);
                                }
#endif //(OAD_ENABLE == 1)
                            }
                            else if(((pu8Descriptor[1] - 2) >= 0)  && (u16DbID == DATA_BC_ID_UK_EC))
                            {
                                // Send OAD info & Update Status to OAD module.
#if (OAD_ENABLE == 1)
                                if(NULL != m_OADParser)
                                {
                                    m_OADParser->SetPmtSignal(u16DSMCC_PID, DATA_BC_ID_UK_EC, &pu8Descriptor[4] , TAG_DBID, u16ServiceID);
                                }
#endif //(OAD_ENABLE == 1)
                            }
                            break;
                        }

                        case TAG_SID:
                        {
#if (OAD_ENABLE == 1)
                            if(NULL != m_OADParser)
                            {
                                m_OADParser->SetPmtSignal(u16DSMCC_PID, DATA_BC_ID_SSU, &pu8Descriptor[1] , TAG_SID, u16ServiceID);
                            }
#endif //(OAD_ENABLE == 1)
                            break;
                        }
#if ((OAD_ENABLE == 1) && (SDTT_OAD_ENABLE==1))
                        case TAG_CID:
                            {
                                //printf("u8Tag == TAG_CID (carousel_id_descriptor)\n");
                                if((pu8Descriptor[1] > 0) && pu8Descriptor[6] == 0) //FormatId value should be set zero for SDTT OAD.
                                {
                                    if(NULL != m_OADParser)
                                    {
                                        m_OADParser->SetPmtSignal(u16DSMCC_PID, DATA_BC_ID_SSU, &pu8Descriptor[1] , TAG_CID, u16ServiceID);
                                    }
                                }

                                break;
                            }
                        case TAG_ATD:
                            {
                                //printf("u8Tag == 0x14 association_tag_descriptor \n");
                                if(pu8Descriptor[1] > 0)
                                {
                                    MAPI_U16 u16Use = (pu8Descriptor[4]<<8 | pu8Descriptor[5]);
                                    if(u16Use == 0) //use value should be set zero for SDTT OAD.
                                    {
                                        if(NULL != m_OADParser)
                                        {
                                            m_OADParser->SetPmtSignal(u16DSMCC_PID, DATA_BC_ID_SSU, &pu8Descriptor[1] , TAG_ATD, u16ServiceID);
                                        }
                                    }
                                }
                            }
                             break;
#endif
                        default:
                            break;
                    }
                    /* move to next descriptor */
                    u16DescriptorLength = pu8Descriptor[1] + 2;
                    u16TotalParseLength += u16DescriptorLength;
                    pu8Descriptor += u16DescriptorLength;
                }
            }
            break;
            default:
                break;
        }
        pu8LoopPosition += (5 + u16TotalDescriptorLength);
    }
}

void MW_DVB_SI_PSI_Parser::_ParserNIT(MAPI_U8 *pu8SectionData)
{
    MAPI_U16 u16DescriptorLength;
    MAPI_U16 u16TotalParseLength;
    MAPI_U16       u16TotalDescriptorLength;
    MAPI_U8 u8Tag;
    MAPI_U8                       *pu8Descriptor;
#if (OAD_ENABLE == 1)
    MAPI_U16  wTSId, wONId, wServiceId;
#endif
    //pu8Descriptor = MApp_SI_FindLoopHead(pu8Section,EN_NIT,EN_FIRSTLOOP);
    u16TotalDescriptorLength = ((pu8SectionData[8] & 0xf) << 8) | pu8SectionData[9]; //TSGetBitsFromU16(&pu8Section[8],0,0x0fff);
    if(u16TotalDescriptorLength)
    {
        pu8Descriptor = pu8SectionData + 10;
    }
    else
    {
        pu8Descriptor = NULL;
    }
    if(pu8Descriptor != NULL)
    {
        /* get total descriptor length */
        //u16TotalDescriptorLength = TSGetBitsFromU16(&pu8Section[8], 0, 0x0FFF);
        u16TotalParseLength = 0;
        while(u16TotalParseLength < u16TotalDescriptorLength)
        {
            u8Tag = pu8Descriptor[0];  //descripter Tag
            switch(u8Tag)
            {
                case TAG_LD:
                {
                    if((pu8Descriptor[1] - 7) > 0)
                    {
                        if(pu8Descriptor[8] == MAPI_SI_LINKAGE_SSU_SERVICE)
                        {
#if (OAD_ENABLE == 1)
                            wTSId = (pu8Descriptor[2] << 8) | pu8Descriptor[3]; //TSGetU16(&pu8Descriptor[2]);
                            wONId = (pu8Descriptor[4] << 8) | pu8Descriptor[5]; //TSGetU16(&pu8Descriptor[4]);
                            wServiceId = (pu8Descriptor[6] << 8) | pu8Descriptor[7]; //TSGetU16(&pu8Descriptor[6]);
                            if(((_ShouldCheckOnidForOAD() == false)
                                    || __IsONIDMatchCountry(wONId, mapi_si_dvb_parser::GetCountry())))
                            {
                                if(NULL != m_OADParser)
                                {
                                    m_OADParser->SetNitSignal(wTSId, wONId, wServiceId, &pu8Descriptor[9]);
                                }
                            }
#endif //(OAD_ENABLE == 1)
                        }
                    }
                }
                break;

                default:
                    break;
            }
            /* move to next descriptor */
            u16DescriptorLength = pu8Descriptor[1] + 2;
            u16TotalParseLength += u16DescriptorLength;
            pu8Descriptor += u16DescriptorLength;
        }
    }
}

void MW_DVB_SI_PSI_Parser::_ParserBAT(MAPI_U8 *pu8SectionData)
{
    MAPI_U16 u16BouquetDescriptorLength;
    MAPI_U16 u16DescriptorLength;
    MAPI_U16 u16TotalParseLength;
    MAPI_U8 *pu8Descriptor;

    if(pu8SectionData == NULL)
    {
        return;
    }

    u16BouquetDescriptorLength = ((pu8SectionData[8] & 0xf) << 8) | pu8SectionData[9];

    if(u16BouquetDescriptorLength > 0)
    {
        pu8Descriptor = pu8SectionData + 10;
    }
    else
    {
        pu8Descriptor = NULL;
    }

    if(pu8Descriptor != NULL)
    {
        u16TotalParseLength = 0;

        while(u16TotalParseLength < u16BouquetDescriptorLength)
        {
            switch(pu8Descriptor[0])
            {
                case TAG_LD:
                {
                    if((pu8Descriptor[1] - 7) > 0)
                    {
                        if(pu8Descriptor[8] == MAPI_SI_LINKAGE_SSU_SERVICE)
                        {
#if (OAD_ENABLE == 1)
                            MAPI_U16 u16BouquetId = pu8SectionData[3] << 8 | pu8SectionData[4];
                            const MAPI_U16 u16KDGOadBouquetId = 0xFF00;

                            if((m_enCableOperator == EN_CABLEOP_KDG) && (u16BouquetId != u16KDGOadBouquetId))
                            {
                                break;
                            }

                            if(m_OADParser != NULL)
                            {
                                MAPI_U16 u16TSId, u16ONId, u16ServiceId;

                                u16TSId = (pu8Descriptor[2] << 8) | pu8Descriptor[3];
                                u16ONId = (pu8Descriptor[4] << 8) | pu8Descriptor[5];
                                u16ServiceId = (pu8Descriptor[6] << 8) | pu8Descriptor[7];
                                m_OADParser->SetNitSignal(u16TSId, u16ONId, u16ServiceId, &pu8Descriptor[9]);
                            }
#endif
                        }
                    }
                }
                break;

                default:
                    break;
            }

            /* move to next descriptor */
            u16DescriptorLength = pu8Descriptor[1] + 2;
            u16TotalParseLength += u16DescriptorLength;
            pu8Descriptor += u16DescriptorLength;
        }
    }
}

void MW_DVB_SI_PSI_Parser::_ParserCallback(MAPI_U8 u8Event, mapi_si_parser* pParser, MAPI_U32 u32Param1, MAPI_U8 * pu8SectionData, MAPI_U8 u8SecCnt, MAPI_BOOL &bStop)
{
    ASSERT(u32Param1);
    MW_DVB_PARSER_CALLBACK_INFO *pParseCallbackInfo = (MW_DVB_PARSER_CALLBACK_INFO*)u32Param1;
    if((pParseCallbackInfo != NULL) && (pParseCallbackInfo->pDvbSiPsiParser != NULL))
    {
        pParseCallbackInfo->pDvbSiPsiParser->_DoParserCallback(u8Event, pParser, u32Param1, pu8SectionData, u8SecCnt, bStop);
    }
}

void MW_DVB_SI_PSI_Parser::_DoParserCallback(MAPI_U8 u8Event, mapi_si_parser* pParser, MAPI_U32 u32Param1, MAPI_U8 * pu8SectionData, MAPI_U8 u8SecCnt, MAPI_BOOL &bStop)
{
    mapi_si_psi_event cEvt;
    MW_DVB_PARSER_CALLBACK_INFO *pParseCallbackInfo = (MW_DVB_PARSER_CALLBACK_INFO*)u32Param1;
    ASSERT(pParser);
    ASSERT(u32Param1);


    if(EN_SI_PSI_EVENT_EIT_SCHE_ALL_PF_READY == u8Event)
    {
#if (EPG_ENABLE == 1)
#if EIT_BUFFER_USE_QUEUE
        if(pParseCallbackInfo->pEitEvent && pParser)
        {
            MAPI_U16 u16SecLen;
            MAPI_U8 *pData = NULL;
            if(m_s16EITBufferAllocCnt >= MAX_EIT_SECTION_DATA_NUM)
            {
                return;
            }
            ASSERT(pu8SectionData);
            if((m_eParserMode != E_DVB_MONITOR) && (m_eParserMode != E_DVB_EPG_UPDATE))
            {
                return;
            }
            u16SecLen  = (((pu8SectionData[1]) & 0x0f) << 8);
            u16SecLen |= (((pu8SectionData[2]) & 0xff) << 0);
            if(u16SecLen <= 4)
            {
                return;
            }
            u16SecLen = u16SecLen + 3; // plus before section length fields
            pData = (MAPI_U8*)malloc(u16SecLen);
            if(pData == NULL)
            {
                ASSERT(0);
                return;
            }
            memcpy(pData, pu8SectionData, u16SecLen);
            cEvt.u32Param2 = (MAPI_U32) pData;
            m_s16EITBufferAllocCnt++;
#if EIT_BUFFER_DBG
            _u32BufferAllocCnt++;
#endif


            cEvt.u32Event = (MAPI_U32) u8Event;
            cEvt.u32Param1 = (MAPI_U32) pParser;


            if(pParseCallbackInfo->pEitEvent->Send(cEvt))
            {
                ASSERT(cEvt.u32Param2);
                free((void*)cEvt.u32Param2);
                m_s16EITBufferAllocCnt--;
#if EIT_BUFFER_DBG
                _u32BufferAllocCnt--;
#endif
            }
        }
#else
        pParser->m_ParserCallBackInfo.EventHandler(E_DVB_SI_EIT_SECTION_IN, (MAPI_U32)pParser->m_ParserCallBackInfo.pCallbackReference, (MAPI_U32)pu8SectionData);
#endif
#endif
        return;
    }

    if((NULL != pParser)
    &&((m_pCurPmtParser == pParser)||(m_pOtherPmtParser == pParser))
    &&(EN_SI_PSI_EVENT_PMT_READY == u8Event)
    &&(NULL != pParseCallbackInfo->PMTSectionNotify))
    {
        ASSERT(pu8SectionData);
        if((m_enMonitorMode != EN_MW_SI_MONITOR_MODE_AIT_ONLY)&&(m_eParserMode == E_DVB_MONITOR))
        {
            U16 u16ONID = ((ST_DVB_MUX_INFO*)m_pCurMux)->u16OriginalNetwork_ID;
            U16 u16TSID = ((ST_DVB_MUX_INFO*)m_pCurMux)->u16TransportStream_ID;
            U16 u16SID = TSGetU16(&pu8SectionData[3]);
            pParseCallbackInfo->PMTSectionNotify(pParseCallbackInfo->pCallbackReference, pu8SectionData, (3 + (((pu8SectionData[1] & 0xF) << 8) | pu8SectionData[2])), u16ONID, u16TSID, u16SID);
        }
    }

    if((NULL != pParser) && (EN_SI_PSI_EVENT_PMT_READY == u8Event) && (m_pCurPmtParser == pParser) && (NULL != pParseCallbackInfo->PMTSectionNotifyCA))
    {
        ASSERT(pu8SectionData);
        if(m_enMonitorMode != EN_MW_SI_MONITOR_MODE_AIT_ONLY)
        {
            pParseCallbackInfo->PMTSectionNotifyCA(pu8SectionData);
        }
    }

#if (OAD_ENABLE == 1 || CA_ENABLE == 1)
    if(pParser && (EN_SI_PSI_EVENT_PMT_READY == u8Event) && ((m_eParserMode == E_DVB_MONITOR) || (m_eParserMode == E_DVB_OAD_SCAN)))
    {
        MW_SI_PARSER_MESSAGE("EN_SI_PSI_EVENT_PMT_READY %s\n", m_pCurPmtParser == pParser ? "current" : "other");
        MAPI_BOOL bParse=MAPI_TRUE;
        if((m_eParserMode == E_DVB_MONITOR) && (m_bOADONIDMatch == MAPI_FALSE))
        {
            bParse = MAPI_FALSE;
        }
        if(bParse)
        {
            _ParserPMT(pu8SectionData);
        }
    }
    else if (pParser && (EN_SI_PSI_EVENT_NIT_READY == u8Event)
        && ((m_eParserMode == E_DVB_MONITOR) || (m_eParserMode == E_DVB_OAD_SCAN)))
    {
        MAPI_U8* pData = pu8SectionData;
        MAPI_U16 u16SecLengthCount;
        //printf("u16SecLengthCount...%d\n",u16SecLengthCount);
        for(int i = 0; i < u8SecCnt; i++)
        {
            _ParserNIT(pData);
            u16SecLengthCount = (((pData[1] << 8) | pData[2]) & 0x0fff) + 3;
#if (CA_ENABLE == 1)
            if(MW_DVB_SI_PSI_Parser::m_pCaClient)
            {
                MW_DVB_SI_PSI_Parser::m_pCaClient->NotifyNITUpdate(i, u8SecCnt, (MAPI_U32)pData);
            }
#endif
            pData += u16SecLengthCount;
        }
    }
    else if(pParser && (EN_SI_PSI_EVENT_BAT_READY == u8Event)
        && ((m_eParserMode == E_DVB_MONITOR) || (m_eParserMode == E_DVB_OAD_SCAN)))
    {
        MAPI_U8* pData = pu8SectionData;
        MAPI_U16 u16SecLengthCount;

        for(int i=0; i<u8SecCnt; i++)
        {
            _ParserBAT(pData);
            u16SecLengthCount = (((pData[1] << 8) | pData[2]) & 0x0fff) + 3;
            pData += u16SecLengthCount;
        }
    }
#endif
#if (CA_ENABLE == 1)
    else if(pParser && (EN_SI_PSI_EVENT_SDT_READY == u8Event) && (m_eParserMode == E_DVB_MONITOR))
    {
        MAPI_U8* pData = pu8SectionData;
        MAPI_U16 u16SecLengthCount = 0;
        for(int i = 0; i < u8SecCnt; i++)
        {
            u16SecLengthCount = (((pData[1] << 8) | pData[2]) & 0x0fff) + 3;
            if(MW_DVB_SI_PSI_Parser::m_pCaClient)
            {
                MW_DVB_SI_PSI_Parser::m_pCaClient->NotifySDTUpdate(i, u8SecCnt, pData);
            }
            pData += u16SecLengthCount;
        }
    }
#endif
    if((EN_SI_PSI_EVENT_EIT_SCHE_ALL_PF_READY != u8Event) &&
        (EN_SI_PSI_EVENT_EIT_READY != u8Event))
    {
        if(!(EIT_PF_CONTINUE_MODE &&(pParser == m_pEitPFParser)))
        {
            bStop=MAPI_TRUE;
        }
    }
    //printf("_ParserCallback = %u %x\n", u8Event,(MAPI_U32)pParser);
    cEvt.u32Event = (MAPI_U32) u8Event;
    cEvt.u32Param1 = (MAPI_U32) pParser;
    if(NULL != pParseCallbackInfo->pSiEvent)
    {
        pParseCallbackInfo->pSiEvent->Send(cEvt);
    }
}

void MW_DVB_SI_PSI_Parser::_StopAllFilter(MAPI_BOOL bIncludeEIT)
{
    if(m_pSi == NULL)
    {
        return;
    }
    if(bIncludeEIT)
    {
        if(m_pEitPfAllParser)
        {
            _DeleteParser((MAPI_U32)m_pEitPfAllParser, MAPI_FALSE);
        }
        if(m_pEitSch1Parser)
        {
            _DeleteParser((MAPI_U32)m_pEitSch1Parser, MAPI_FALSE);
        }
        if(m_pEitSch2Parser)
        {
            _DeleteParser((MAPI_U32)m_pEitSch2Parser, MAPI_FALSE);
        }

    }
    if(m_pBatParser)
    {
        _DeleteParser((MAPI_U32)m_pBatParser, MAPI_FALSE);
    }
    if(m_pEitPFParser)
    {
        _DeleteParser((MAPI_U32)m_pEitPFParser, MAPI_FALSE);
    }
    if(m_pPatParser)
    {
        _DeleteParser((MAPI_U32)m_pPatParser, MAPI_FALSE);
    }
    if(m_pSdtParser)
    {
        _DeleteParser((MAPI_U32)m_pSdtParser, MAPI_FALSE);
    }
    if(m_pSdtOtherParser)
    {
        _DeleteParser((MAPI_U32)m_pSdtOtherParser, MAPI_FALSE);
    }
    if(m_pNitParser)
    {
        _DeleteParser((MAPI_U32)m_pNitParser, MAPI_FALSE);
    }
    if(m_pCurPmtParser)
    {
        _DeleteParser((MAPI_U32)m_pCurPmtParser, MAPI_FALSE);
    }
    if(m_pOtherPmtParser)
    {
        _DeleteParser((MAPI_U32)m_pOtherPmtParser, MAPI_FALSE);
    }

    if(m_pTdtParser)
    {
        _DeleteParser((MAPI_U32)m_pTdtParser, MAPI_FALSE);
    }
    if(m_pTotParser)
    {
        _DeleteParser((MAPI_U32)m_pTotParser, MAPI_FALSE);
    }
    if(m_pAitParser)
    {
        _DeleteParser((MAPI_U32)m_pAitParser, MAPI_FALSE);
    }
    if(m_pRctParser)
    {
        _DeleteParser((MAPI_U32)m_pRctParser, MAPI_FALSE);
    }
    for(int j = 0; j < MAX_CHANNEL_IN_MUX; j++)
    {
        if(m_pAllPmtParser[j])
        {
            _DeleteParser((MAPI_U32)m_pAllPmtParser[j], MAPI_FALSE);
        }
    }
#if (ISDB_SYSTEM_ENABLE == 1)
#if 1//(ISDB_CHANNELLOGO_ENABLE == 1)
    if(m_pCdtParser)
    {
        _DeleteParser((MAPI_U32)m_pCdtParser, MAPI_FALSE);
    }
#endif
#endif
    for(int i = 0; i < MAPI_SI_MAX_TS_IN_NETWORK; i++)
    {
        if(m_pAllSdtParser[i])
        {
            _DeleteParser((MAPI_U32)m_pAllSdtParser[i] , MAPI_FALSE);
        }
    }
    if (m_pFileinPatParser)
    {
        _DeleteParser((MAPI_U32)m_pFileinPatParser, MAPI_FALSE);
    }
    for(int j = 0; j < MAX_CHANNEL_IN_MUX; j++)
    {
        if(m_pFileinAllPmtParser[j])
        {
            _DeleteParser((MAPI_U32)m_pFileinAllPmtParser[j], MAPI_FALSE);
        }
    }
    for(int i = 0; i < MAX_SPECIAL_SERVICE_NUM; i++)
    {
        if(m_pSpecialServicesParser[i])
        {
            _DeleteParser((MAPI_U32)m_pSpecialServicesParser[i], MAPI_FALSE);
        }
    }
#if ((OAD_ENABLE == 1) && (SDTT_OAD_ENABLE==1))
    if(m_pSdttParser)
    {
        _DeleteParser((MAPI_U32)m_pSdttParser, MAPI_FALSE);
    }
#endif

    std::list<mapi_si_NIT_parser *> pTempList = m_pNitParserList;
    for(std::list<mapi_si_NIT_parser *>::iterator it=pTempList.begin(); it!=pTempList.end(); ++it)
    {
        if(*it != NULL)
        {
            _DeleteParser((MAPI_U32)*it, MAPI_FALSE);
        }
    }
}

void MW_DVB_SI_PSI_Parser::_UpdateVideo(MAPI_SI_VIDEO_INFO *pNewVideoInfo, MAPI_SI_VIDEO_INFO *pVideoInfo)
{
    MAPI_U8 i;
    MAPI_U8 bStillPicture = pVideoInfo[0].bStillPic;
    MAPI_U8 u8VideoType = pVideoInfo[0].bVideoType;
    MAPI_U8 u8AVCLevel = pVideoInfo[0].u8AVCLevelIDC;
    MAPI_U16 u16VideoPid = pVideoInfo[0].wVideoPID;
    pNewVideoInfo->bVideoType = E_VIDEOTYPE_NONE;
    if(u16VideoPid == 0)
    {
        pNewVideoInfo->wVideoPID = INVALID_PID;
        return;
    }
    if ((m_eCountry == E_ITALY) || (m_eCountry == E_SPAIN))
    {
        for(i = 1; i < MAPI_SI_MAX_VIDEO; i++)
        {
             if((pVideoInfo[i].wVideoPID < u16VideoPid) &&
               (pVideoInfo[i].wVideoPID != INVALID_PID) &&
               (pVideoInfo[i].wVideoPID != 0) )
            {
                u8VideoType = pVideoInfo[i].bVideoType;
                u16VideoPid = pVideoInfo[i].wVideoPID;
                bStillPicture= pVideoInfo[i].bStillPic;
            }
        }
    }
    else
    {
        for(i = 0; i < MAPI_SI_MAX_VIDEO; i++)
        {
            //Select the avc video with highest AVC Profile
            if(((u8VideoType != MAPI_ST_AVCH264_VID) || (u8AVCLevel < pVideoInfo[i].u8AVCLevelIDC)) &&
               (pVideoInfo[i].bVideoType == MAPI_ST_AVCH264_VID) &&
               (pVideoInfo[i].wVideoPID != 0))
            {
                u8VideoType = pVideoInfo[i].bVideoType;
                u16VideoPid = pVideoInfo[i].wVideoPID;
                u8AVCLevel = pVideoInfo[i].u8AVCLevelIDC;
                bStillPicture= pVideoInfo[i].bStillPic;
            }
        }
    }
    pNewVideoInfo->bStillPic = bStillPicture;
    pNewVideoInfo->wVideoPID = u16VideoPid;
    switch(u8VideoType)
    {
        case MAPI_ST_AVS_VID:
            pNewVideoInfo->bVideoType = E_VIDEOTYPE_AVS;//mapi_video_dtv_cfg_datatype::CODEC_TYPE_AVS;
            break;
        case MAPI_ST_VC1_VID:
            pNewVideoInfo->bVideoType = E_VIDEOTYPE_VC1;//mapi_video_dtv_cfg_datatype::CODEC_TYPE_VC1;
            break;
        case MAPI_ST_AVCH264_VID:
            pNewVideoInfo->bVideoType = E_VIDEOTYPE_H264;//mapi_video_dtv_cfg_datatype::CODEC_TYPE_H264;
            break;
        case MAPI_ST_MPEG1_VID:    // MPEG-1 video
        case MAPI_ST_MPEG2_VID:    // MPEG-2 video
            pNewVideoInfo->bVideoType = E_VIDEOTYPE_MPEG;//mapi_video_dtv_cfg_datatype::CODEC_TYPE_MPEG2;
            break;
        case MAPI_ST_HEVC_VID:
            pNewVideoInfo->bVideoType = E_VIDEOTYPE_HEVC;//mapi_video_dtv_cfg_datatype::CODEC_TYPE_HEVC;
            break;
    }
    //printf("pNewVideoInfo->bVideoType %x\n",pNewVideoInfo->bVideoType);
}

MAPI_U8 MW_DVB_SI_PSI_Parser::_UpdateAudio(AUD_INFO *pNewAudioInfo, MAPI_SI_AUD_INFO *pAudioInfo)
{
    MAPI_U8 u8Index = 0;
    memset(pNewAudioInfo, 0, sizeof(AUD_INFO)*MAX_AUD_LANG_NUM);
    for(int k = 0; k < MAX_AUD_LANG_NUM; k++)
    {
        pNewAudioInfo[k].u16AudPID = INVALID_PID;
    }
    for(int k = 0; k < MAX_AUD_LANG_NUM; k++)
    {
        if((pAudioInfo[k].wAudPID>0x000F)&&(pAudioInfo[k].wAudPID<=0x1FFF))
        {
            pNewAudioInfo[u8Index].u16AudPID = pAudioInfo[k].wAudPID;
            if ((m_eCountry == E_CHINA) && (pAudioInfo[k].stISOLangInfo.bIsValid == MAPI_FALSE))
            {
                pAudioInfo[k].stISOLangInfo.bIsValid = MAPI_TRUE;
                pAudioInfo[k].stISOLangInfo.u8AudioMode = E_SI_AUDIOMODE_STEREO;
                pAudioInfo[k].stISOLangInfo.bAudType = E_SI_AUDIO_UNDEFINED;
                pAudioInfo[k].stISOLangInfo.aISOLangInfo[0] = 'C';
                pAudioInfo[k].stISOLangInfo.aISOLangInfo[1] = 'H';
                pAudioInfo[k].stISOLangInfo.aISOLangInfo[2] = 'I';
            }
            pNewAudioInfo[u8Index].aISOLangInfo.u8IsValid = pAudioInfo[k].stISOLangInfo.bIsValid;
            memcpy(pNewAudioInfo[u8Index].aISOLangInfo.u8ISOLangInfo, pAudioInfo[k].stISOLangInfo.aISOLangInfo, 3);
            pNewAudioInfo[u8Index].bBroadcastMixAD = pAudioInfo[k].bBroadcastMixAD;
            if(_IsDefaultBroadcastMixAD())
            {
                if((pAudioInfo[k].stISOLangInfo.bAudType == E_SI_AUDIO_VISUAL_IMPAIRED)
                    && (pAudioInfo[k].bGetSuppleMentaryAudioDesc == MAPI_FALSE))
                {
                    pNewAudioInfo[u8Index].bBroadcastMixAD=MAPI_TRUE;
                }
            }
            pNewAudioInfo[u8Index].u8AACType = pAudioInfo[k].u8AACType;
            pNewAudioInfo[u8Index].u8AACProfileAndLevel = pAudioInfo[k].u8AACProfileAndLevel;
            pNewAudioInfo[u8Index].bInValid = pAudioInfo[k].bInValid;
            switch(pAudioInfo[k].u8AudType)
            {
                case MAPI_ST_MPEG1_AUD:
                case MAPI_ST_MPEG2_AUD:
                    pNewAudioInfo[u8Index].u8AudType = E_AUDIOTYPE_MPEG;
                    break;
                case MAPI_ST_AC3_AUD:
                    pNewAudioInfo[u8Index].u8AudType = E_AUDIOTYPE_AC3;
                    break;
                case MAPI_ST_MPEG4_AUD:
                    pNewAudioInfo[u8Index].u8AudType = E_AUDIOTYPE_AAC;//E_AUDIOTYPE_MPEG4;
                    break;
                case MAPI_ST_AAC_AUD:
                    pNewAudioInfo[u8Index].u8AudType = E_AUDIOTYPE_AAC;
                    break;
                case MAPI_ST_AC3P_AUD:
                    pNewAudioInfo[u8Index].u8AudType = E_AUDIOTYPE_AC3P;
                    break;
                case MAPI_ST_DRA1_AUD:
                    pNewAudioInfo[u8Index].u8AudType = E_AUDIOTYPE_DRA ;
                    break;
            }
            if (pAudioInfo[k].stISOLangInfo.bIsValid)
            {
                switch(pAudioInfo[k].stISOLangInfo.bAudType)
                {

                    case E_SI_AUDIO_CLEAN_EFFECTS:
                        pNewAudioInfo[u8Index].aISOLangInfo.u8AudType = E_AUDIO_ISO639TYPE_CLEAN_EFFECTS;
                        break;
                    case E_SI_AUDIO_VISUAL_IMPAIRED:
                        pNewAudioInfo[u8Index].aISOLangInfo.u8AudType = E_AUDIO_ISO639TYPE_VISUAL_IMPAIRED;
                        break;
                    case E_SI_AUDIO_HEARING_IMPAIRED:
                        pNewAudioInfo[u8Index].aISOLangInfo.u8AudType = E_AUDIO_ISO639TYPE_HEARING_IMPAIRED;
                        break;
                    case E_SI_AUDIO_UNDEFINED:
                        pNewAudioInfo[u8Index].aISOLangInfo.u8AudType = E_AUDIO_ISO639TYPE_UNDEFINED;
                        break;
                    default:
                        pNewAudioInfo[u8Index].aISOLangInfo.u8AudType = E_AUDIO_ISO639TYPE_UNKNOW;
                        break;
                }
            }
            else
            {
                pNewAudioInfo[u8Index].aISOLangInfo.u8AudType = E_AUDIO_ISO639TYPE_UNKNOW;
            }
            switch(pAudioInfo[k].stISOLangInfo.u8AudioMode)
            {
                case E_SI_AUDIOMODE_LL:
                    if((m_eParserType == MW_DVB_C_PARSER) &&  (  (m_enCableOperator == EN_CABLEOP_COMHEM)  || (m_enCableOperator == EN_CABLEOP_CDCABLE) ||(m_enCableOperator == EN_CABLEOP_CDSMATV)))
                    {
                        if((pNewAudioInfo[u8Index].u8AudType ==  E_AUDIOTYPE_AC3P) || (pNewAudioInfo[u8Index].u8AudType ==  E_AUDIOTYPE_AC3))
                        {
                            pNewAudioInfo[u8Index].aISOLangInfo.u8AudMode = E_AUDIOMODE_STEREO;
                        }
                    }
                    else
                    {
                        pNewAudioInfo[u8Index].aISOLangInfo.u8AudMode = E_AUDIOMODE_LL;
                    }
                    break;
                case E_SI_AUDIOMODE_RR:
                    if((m_eParserType == MW_DVB_C_PARSER) &&  (  (m_enCableOperator == EN_CABLEOP_COMHEM)  || (m_enCableOperator == EN_CABLEOP_CDCABLE) ||(m_enCableOperator == EN_CABLEOP_CDSMATV)))
                    {
                        if((pNewAudioInfo[u8Index].u8AudType ==  E_AUDIOTYPE_AC3P) || (pNewAudioInfo[u8Index].u8AudType ==  E_AUDIOTYPE_AC3))
                        {
                            pNewAudioInfo[u8Index].aISOLangInfo.u8AudMode = E_AUDIOMODE_STEREO;
                        }
                    }
                    else
                    {
                        pNewAudioInfo[u8Index].aISOLangInfo.u8AudMode = E_AUDIOMODE_RR;
                    }
                    break;
                case E_SI_AUDIOMODE_STEREO:
                default:
                    pNewAudioInfo[u8Index].aISOLangInfo.u8AudMode = E_AUDIOMODE_STEREO;
                    break;
            }
            u8Index++;
        }
    }
    return u8Index;
}


void MW_DVB_SI_PSI_Parser::_UpdateServiceType(MAPI_U8 *pServiceType, MAPI_U8 *pServicePriority, MAPI_U8 u8ServiceType)
{

    MAPI_U8 u8NewType;
    if(_IsSpecificSupport(E_DVB_SPECIFIC_SERVICE_TYPE_SUPPORT,&u8ServiceType,&u8NewType))
    {
        u8ServiceType=u8NewType;
    }
    else
    {
        u8ServiceType=E_TYPE_INVALID;
    }

    *pServicePriority = E_SERVICETYPE_PRIORITY_LOW;
    switch(u8ServiceType)
    {
        case E_TYPE_AC_RADIO:
            *pServicePriority = E_SERVICETYPE_PRIORITY_HIGH;
            *pServiceType = E_SERVICETYPE_RADIO;
            break;
        case E_TYPE_RADIO:
            *pServiceType = E_SERVICETYPE_RADIO;
            break;
        case E_TYPE_TTX:
            if(m_eCountry == E_UK)
            {
                *pServiceType = E_SERVICETYPE_INVALID;
            }
            else
            {
                *pServiceType = E_SERVICETYPE_DATA;
            }
            break;
        case E_TYPE_DATA:
        case E_TYPE_MHP:
            *pServiceType = E_SERVICETYPE_DATA;
            break;
        case E_TYPE_ACSD_DTV:
            *pServicePriority = E_SERVICETYPE_PRIORITY_MIDDLE;
            *pServiceType = E_SERVICETYPE_DTV;
            break;
        case E_TYPE_HD_DTV://For ziggo simulcast requirement
        case E_TYPE_ACHD_DTV:
            *pServicePriority = E_SERVICETYPE_PRIORITY_HIGH;
            *pServiceType = E_SERVICETYPE_DTV;
            break;
        case E_TYPE_DTV:
            *pServiceType = E_SERVICETYPE_DTV;
            break;
#if (NVOD_ENABLE == 1)
        case E_TYPE_NVODRD:
            *pServiceType= E_SERVICETYPE_NVODREF;
            break;
        case E_TYPE_NVODTS:
            *pServiceType=E_SERVICETYPE_NVODTS;
            break;
#endif
        default:
            *pServiceType = E_SERVICETYPE_INVALID;
            break;
    }
}
void MW_DVB_SI_PSI_Parser::_UpdateServiceInfo(MW_DVB_SERVICE_CHANGE_TYPE eType, MAPI_SI_TABLE_PMT &pmt,MW_DVB_SI_ServicelInfo &info)
{
    info.u8PmtVersion = pmt.u8Version;
    if(pmt.bIsServiceMove)
    {
        info.eChangeType = MW_SERVICE_MOVE;
        memcpy(&info.stServiceMoveInfo, &pmt.stServiceMove, sizeof(MAPI_SI_SERVICE_MOVE));
    }
    else
    {
        info.eChangeType = eType;
    }

    info.bMHEG5Service = MAPI_FALSE;
    info.bDataService = MAPI_FALSE;
    for(int i = 0; i < MAPI_SI_MAX_DATA; i++)
    {
        if(pmt.sDataBroadcastInfo[i].u16PID)
        {
            if((pmt.sDataBroadcastInfo[i].u16DBId == MAPI_DATA_BC_ID_MHEG5) \
                    || (pmt.sDataBroadcastInfo[i].u16DBId == MAPI_DATA_BC_ID_MHP))
            {
                info.bDataService = MAPI_TRUE;
            }
            if(pmt.sDataBroadcastInfo[i].u16DBId == MAPI_DATA_BC_ID_MHEG5)
            {
                if((pmt.sDataBroadcastInfo[i].u16AppType == MAPI_APPTYPE_LAUNCH_PROFILE)
                        || (pmt.sDataBroadcastInfo[i].u16AppType == MAPI_APPTYPE_LAUNCH_PROFILE2)
                        || (pmt.sDataBroadcastInfo[i].u16AppType == MAPI_NZ_APPLICATION_TYPE_CODE))
                {
                    info.bMHEG5Service = MAPI_TRUE;
                    break;
                }
            }
        }
    }
#if (ISDB_SYSTEM_ENABLE == 1)
    for(int k=0; k<MAPI_SI_MAX_APP_SIGNALLING_NUM; k++)
    {
        if (pmt.stAppSignalInfo.astAppSignalling[k].u16AppType != MAPI_INVALID_APPLICATION_TYPE)
        {
            info.bMHEG5Service = MAPI_TRUE;
            break;
        }
    }
#endif

    info.bIsPMTCAExist = pmt.bIsCAExist;
    if(MAPI_TRUE == _IsSpecificSupport(E_DVB_CHECK_SDT_FREE_CA_MODE,&m_eCountry, NULL))
    {
        info.bIsCAExist = info.bIsSDTCAExist | info.bIsPMTCAExist;
    }
    else
    {
        info.bIsCAExist = info.bIsPMTCAExist;
    }
    _UpdateVideo(&info.stVideoInfo, pmt.sVideoInfo);
    info.bIsSIDOnly = pmt.bIsSIDOnly;
    info.wPCRPid = pmt.wPCRPid;
    info.wCCPid = (0!=pmt.u16CCPid) ? pmt.u16CCPid : INVALID_PID;
    info.u8AudStreamNum = _UpdateAudio(info.stAudInfo, pmt.stAudInfo);
    for(int i = 0; i < MAX_AUD_LANG_NUM; i++)
    {
        info.au8AudioComponentTag[i] = pmt.stAudInfo[i].u8ComponentTag;
    }
    memcpy(info.astTXTInfo, pmt.astTXTInfo, MAPI_SI_MAX_TTXINFO_NUM * sizeof(MAPI_SI_TELETEXT_INFO));
    info.u8TTXNum = 0;
    for(int i = 0; i < MAPI_SI_MAX_TTXINFO_NUM; i++)
    {
        if(info.astTXTInfo[i].u16TTX_Pid)
        {
            info.u8TTXNum++;
        }
    }
    memcpy(info.astEBUSubtInfo, pmt.astEBUSubtInfo, sizeof(info.astEBUSubtInfo));
    info.u8EBUSubtitleNum = 0;
    for(int i = 0; i < MAPI_SI_MAX_SUBTITLEINFO_NUM; i++)
    {
        if(info.astEBUSubtInfo[i].u16TTX_Pid)
        {
            info.u8EBUSubtitleNum++;
        }
    }
    memcpy(info.astDVBSubtInfo, pmt.astDVBSubtInfo, sizeof(info.astDVBSubtInfo));
    info.u8DVBSubtitleNum = 0;
    for(int i = 0; i < MAPI_SI_MAX_SUBTITLEINFO_NUM; i++)
    {
        if(0 != info.astDVBSubtInfo[i].u16Sub_Pid)
        {
            info.u8DVBSubtitleNum++;
        }
    }
}

void MW_DVB_SI_PSI_Parser::_DeleteParser(MAPI_U32 u32Address, MAPI_BOOL bDbg)
{
    if((void *)u32Address == NULL)
    {
        return;
    }

    if((MAPI_U32)m_pPatParser == u32Address)
    {
        delete m_pPatParser;
        m_pPatParser = NULL;
        if(bDbg)
        {
            MW_SI_PARSER_WARNING("\n\n#######PAT data err\n\n");
            //mapi_system * system = mapi_interface::Get_mapi_system();
            //printf("time %d\n",system->GetSystemTime());

        }
        ASSERT(m_s16OpenFilter);
        m_s16OpenFilter--;
        return;
    }
    else if((MAPI_U32)m_pNitParser == u32Address)
    {
        ASSERT(m_s16OpenFilter);
        m_s16OpenFilter--;


        delete m_pNitParser;
        m_pNitParser = NULL;
        if(bDbg)
        {
            MW_SI_PARSER_WARNING("\n\n#######NIT data err\n\n");
            //mapi_system * system = mapi_interface::Get_mapi_system();
            //printf("time %d\n",system->GetSystemTime());
        }
        return;
    }
    else if((MAPI_U32)m_pSdtParser == u32Address)
    {
        ASSERT(m_s16OpenFilter);
        m_s16OpenFilter--;

        delete m_pSdtParser;
        m_pSdtParser = NULL;
        if(bDbg)
        {
            MW_SI_PARSER_WARNING("\n\n#######sdt data err\n\n");
            //mapi_system * system = mapi_interface::Get_mapi_system();
            //printf("time %d\n",system->GetSystemTime());
        }
        //printf("\n\n#######SDT data err\n\n");
        return;
    }
    else if((MAPI_U32)m_pSdtOtherParser == u32Address)
    {
        ASSERT(m_s16OpenFilter);
        m_s16OpenFilter--;

        delete m_pSdtOtherParser;
        m_pSdtOtherParser = NULL;
        if(bDbg)
        {
            MW_SI_PARSER_WARNING("\n\n#######sdt other data err\n\n");
            //mapi_system * system = mapi_interface::Get_mapi_system();
            //printf("time %d\n",system->GetSystemTime());
        }
        //printf("\n\n#######SDT data err\n\n");
        return;
    }
    else if((MAPI_U32)m_pCurPmtParser == u32Address)
    {
        ASSERT(m_s16OpenFilter);
        m_s16OpenFilter--;

        delete m_pCurPmtParser;
        m_pCurPmtParser = NULL;
        if(bDbg)
        {
            MW_SI_PARSER_WARNING("\n\n#######cur pmt data err\n\n");
            //mapi_system * system = mapi_interface::Get_mapi_system();
            //printf("time %d\n",system->GetSystemTime());
        }
        //printf("\n\n#######cur pmt  data err\n\n");
        return;
    }
    else if((MAPI_U32)m_pOtherPmtParser == u32Address)
    {
        ASSERT(m_s16OpenFilter);
        m_s16OpenFilter--;

        delete m_pOtherPmtParser;
        m_pOtherPmtParser = NULL;
        if(bDbg)
        {
            MW_SI_PARSER_WARNING("\n\n#######other pmt data err\n\n");
            //mapi_system * system = mapi_interface::Get_mapi_system();
            //printf("time %d\n",system->GetSystemTime());
        }
        //printf("\n\n#######cur pmt  data err\n\n");
        return;
    }
    else if((MAPI_U32)m_pTdtParser == u32Address)
    {
        ASSERT(m_s16OpenFilter);
        m_s16OpenFilter--;

        delete m_pTdtParser;
        m_pTdtParser = NULL;
        if(bDbg)
        {
            MW_SI_PARSER_WARNING("\n\n#######tdt data err\n\n");
            //mapi_system * system = mapi_interface::Get_mapi_system();
            //printf("time %d\n",system->GetSystemTime());
        }
        //printf("\n\n#######tdt  data err\n\n");
        return;
    }
    else if((MAPI_U32)m_pTotParser == u32Address)
    {
        ASSERT(m_s16OpenFilter);
        m_s16OpenFilter--;

        delete m_pTotParser;
        m_pTotParser = NULL;
        if(bDbg)
        {
            MW_SI_PARSER_WARNING("\n\n#######tot data err\n\n");
            //mapi_system * system = mapi_interface::Get_mapi_system();
            //printf("time %d\n",system->GetSystemTime());
        }
        //printf("\n\n#######tot  data err\n\n");
        return;
    }
    else if((MAPI_U32)m_pEitPfAllParser == u32Address)
    {
        ASSERT(m_s16OpenFilter);
        m_s16OpenFilter--;
        delete m_pEitPfAllParser;
        m_pEitPfAllParser = NULL;
        if(bDbg)
        {
            MW_SI_PARSER_WARNING("\n\n#######eit pf all data err\n\n");
            //mapi_system * system = mapi_interface::Get_mapi_system();
            //printf("time %d\n",system->GetSystemTime());
        }
        return;
    }
    else if((MAPI_U32)m_pEitSch1Parser == u32Address)
    {
        ASSERT(m_s16OpenFilter);
        m_s16OpenFilter--;
        delete m_pEitSch1Parser;
        m_pEitSch1Parser = NULL;
        if(bDbg)
        {
            MW_SI_PARSER_WARNING("\n\n#######eit sch1 data err\n\n");
            //mapi_system * system = mapi_interface::Get_mapi_system();
            //printf("time %d\n",system->GetSystemTime());
        }
        return;
    }
    else if((MAPI_U32)m_pEitSch2Parser == u32Address)
    {
        ASSERT(m_s16OpenFilter);
        m_s16OpenFilter--;
        delete m_pEitSch2Parser;
        m_pEitSch2Parser = NULL;
        if(bDbg)
        {
            MW_SI_PARSER_WARNING("\n\n#######eit sch2 data err\n\n");
            //mapi_system * system = mapi_interface::Get_mapi_system();
            //printf("time %d\n",system->GetSystemTime());
        }
        return;
    }
    else if((MAPI_U32)m_pEitPFParser == u32Address)
    {
        ASSERT(m_s16OpenFilter);
        m_s16OpenFilter--;
        delete m_pEitPFParser;
        m_pEitPFParser = NULL;
        if(bDbg)
        {
            MW_SI_PARSER_WARNING("\n\n#######eit pf data err\n\n");
            //mapi_system * system = mapi_interface::Get_mapi_system();
            //printf("time %d\n",system->GetSystemTime());
        }
        return;
    }
    else if((MAPI_U32)m_pAitParser == u32Address)
    {
        ASSERT(m_s16OpenFilter);
        m_s16OpenFilter--;
        delete m_pAitParser;
        m_pAitParser = NULL;
        if(bDbg)
        {
            MW_SI_PARSER_WARNING("\n\n#######ait data err\n\n");
            //mapi_system * system = mapi_interface::Get_mapi_system();
            //printf("time %d\n",system->GetSystemTime());
        }
        return;
    }
    else if((MAPI_U32)m_pBatParser == u32Address)
    {
        ASSERT(m_s16OpenFilter);
        m_s16OpenFilter--;


        delete m_pBatParser;
        m_pBatParser = NULL;
        if(bDbg)
        {
            MW_SI_PARSER_WARNING("\n\n#######BAT data err\n\n");
            //mapi_system * system = mapi_interface::Get_mapi_system();
            //printf("time %d\n",system->GetSystemTime());
        }
        return;
    }
    else if((MAPI_U32)m_pRctParser == u32Address)
    {
        ASSERT(m_s16OpenFilter);
        m_s16OpenFilter--;

        delete m_pRctParser;
        m_pRctParser = NULL;
        if(bDbg)
        {
            MW_SI_PARSER_WARNING("\n\n#######rct data err\n\n");
            //mapi_system * system = mapi_interface::Get_mapi_system();
            //printf("time %d\n",system->GetSystemTime());
        }
        //printf("\n\n#######RCT data err\n\n");
        return;
    }
#if (ISDB_SYSTEM_ENABLE == 1)
#if 1//(ISDB_CHANNELLOGO_ENABLE == 1)
    else if((MAPI_U32)m_pCdtParser == u32Address)
    {
        ASSERT(m_s16OpenFilter);
        m_s16OpenFilter--;

        delete m_pCdtParser;
        m_pCdtParser = NULL;
        if(bDbg)
        {
            MW_SI_PARSER_WARNING("\n\n#######cdt data err\n\n");
        }
        return;
    }
#endif
#endif
//for Filein SI parsing
    else if((MAPI_U32)m_pFileinPatParser == u32Address)
    {
        delete m_pFileinPatParser;
        m_pFileinPatParser = NULL;
        //printf("delete Filein PAT parser\n");
        if(bDbg)
        {
            MW_SI_PARSER_WARNING("\n\n#######PAT data err\n\n");
            //mapi_system * system = mapi_interface::Get_mapi_system();
            //printf("time %d\n",system->GetSystemTime());

        }
        ASSERT(m_s16OpenFilter);
        m_s16OpenFilter--;
        return;
    }
//<
#if (ASTRA_SGT_ENABLE == 1)
    else if((MAPI_U32)m_pSgtParser == u32Address)
    {
        DELETE(m_pSgtParser);
        //printf("delete Sgt parser\n");
        if(bDbg)
        {
            MW_SI_PARSER_WARNING("\n\n#######Sgt data err\n\n");
            //mapi_system * system = mapi_interface::Get_mapi_system();
            //printf("time %d\n",system->GetSystemTime());

        }
        ASSERT(m_s16OpenFilter);
        m_s16OpenFilter--;
        return;
    }
#endif
#if ((OAD_ENABLE == 1) && (SDTT_OAD_ENABLE==1))
    else if((MAPI_U32)m_pSdttParser == u32Address)
    {
        delete m_pSdttParser;
        m_pSdttParser = NULL;
        if(bDbg)
        {
            MW_SI_PARSER_WARNING("\n\n####### SDTT data err\n\n");
        }
        ASSERT(m_s16OpenFilter);
        m_s16OpenFilter--;
        return;
    }
#endif
    else
    {
        MAPI_U16 j;
        for(j = 0; j < MAX_CHANNEL_IN_MUX; j++)
        {
            if(m_pAllPmtParser[j] && ((MAPI_U32)m_pAllPmtParser[j] == u32Address))
            {
                ASSERT(m_s16OpenFilter);
                m_s16OpenFilter--;

                delete m_pAllPmtParser[j];
                m_pAllPmtParser[j] = NULL;
                m_aPMTWaitFilter[j] = MAPI_FALSE;
                //printf("\n\n####### pmt  data err\n\n");
                if(bDbg)
                {
                    MW_SI_PARSER_WARNING("\n\n#######pmt data err\n\n");
                    //mapi_system * system = mapi_interface::Get_mapi_system();
                    //printf("time %d\n",system->GetSystemTime());
                }
                return;
            }
        }
        //for Filein SI parsing
        for(j = 0; j < MAX_CHANNEL_IN_MUX; j++)
        {
            if(m_pFileinAllPmtParser[j] && ((MAPI_U32)m_pFileinAllPmtParser[j] == u32Address))
            {
                ASSERT(m_s16OpenFilter);
                m_s16OpenFilter--;

                delete m_pFileinAllPmtParser[j];
                m_pFileinAllPmtParser[j] = NULL;
                //printf("\n\n####### pmt  data err\n\n");
                if(bDbg)
                {
                    MW_SI_PARSER_WARNING("\n\n#######pmt data err\n\n");
                    //mapi_system * system = mapi_interface::Get_mapi_system();
                    //printf("time %d\n",system->GetSystemTime());
                }
                return;
            }
        }
        //<
        for (j = 0; j < MAPI_SI_MAX_TS_IN_NETWORK; j++)
        {
            if(m_pAllSdtParser[j] && ((MAPI_U32)m_pAllSdtParser[j] == u32Address))
            {
                ASSERT(m_s16OpenFilter);
                m_s16OpenFilter--;

                delete m_pAllSdtParser[j];
                m_pAllSdtParser[j] = NULL;
                if(bDbg)
                {
                    MW_SI_PARSER_WARNING("\n\n#######all sdt data\n\n");
                }
                return;
            }
        }
        for(j = 0; j < MAX_SPECIAL_SERVICE_NUM; j++)
        {
            if(m_pSpecialServicesParser[j] && ((MAPI_U32)m_pSpecialServicesParser[j] == u32Address))
            {
                ASSERT(m_s16OpenFilter);
                m_s16OpenFilter--;

                delete m_pSpecialServicesParser[j];
                m_pSpecialServicesParser[j] = NULL;
                if(bDbg)
                {
                    printf("\n\n####### special service pmt data err\n\n");
                }
                return;
            }
        }

        for(std::list<mapi_si_NIT_parser *>::iterator it=m_pNitParserList.begin(); it!=m_pNitParserList.end(); ++it)
        {
            if((*it != NULL) && ((MAPI_U32)*it == u32Address))
            {
                ASSERT(m_s16OpenFilter);
                m_s16OpenFilter--;

                DELETE(*it);
                m_pNitParserList.erase(it);

                if(bDbg)
                {
                    printf("\n\n####### nit data err\n\n");
                }

                return;
            }
        }
    }
    //ASSERT(0);
}

void MW_DVB_SI_PSI_Parser::_AddExtraSSUService()
{
    if(IS_NORDIC_COUNTRY(m_eCountry) && IS_TABLE_VERSION_VALID(m_stNit) &&
        (m_stNit.pNitLinkageInfo != NULL) && m_stNit.pNitLinkageInfo->size() > 0)
    {
        MW_DTV_CM_DB_scope_lock cmdbLock((MW_DTV_CM_DB<ST_DVB_PROGRAMINFO, ST_DVB_MUX_INFO, ST_DVB_SAT_INFO, ST_DVB_NETWORK_INFO>*)m_pCMDB);
        ITER_EACH(it, (*m_stNit.pNitLinkageInfo))
        {
            if(it->enLinkageType != MAPI_SI_LINKAGE_SSU_SERVICE)
            {
                continue;
            }

            if(!((m_TsInfo.u16TSID == it->u16TSId) &&(m_TsInfo.u16ONID == it->u16ONId))) // Linkage service not in cur mux
            {
                continue;
            }

            U16 i = 0;
            for(; i<m_TsInfo.u16ServiceCount;i++)
            {
                if((m_ProgramInfo[i].u16ServiceID == it->u16ServiceId) &&
                    (m_TsInfo.u16TSID == it->u16TSId) &&
                    (m_TsInfo.u16ONID == it->u16ONId))
                {
                    break;
                }
            }
            if( i >= m_TsInfo.u16ServiceCount) //linkage service not in m_ProgramInfo
            {
                if(((MW_DTV_CM_DB<ST_DVB_PROGRAMINFO, ST_DVB_MUX_INFO, ST_DVB_SAT_INFO, ST_DVB_NETWORK_INFO>*)m_pCMDB)->GetByID(it->u16TSId, it->u16ONId, it->u16ServiceId)) // already in CMDB
                {
                    continue;
                }

                //add service
                _ResetServiceInfo(m_ProgramInfo[m_TsInfo.u16ServiceCount]);
                m_ProgramInfo[m_TsInfo.u16ServiceCount].u8NitVer = m_stNit.u8Version;
                m_ProgramInfo[m_TsInfo.u16ServiceCount].u16ServiceID = it->u16ServiceId;
                m_ProgramInfo[m_TsInfo.u16ServiceCount].u8RealServiceType = E_TYPE_DATA;
                m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceType = E_SERVICETYPE_DATA;
                m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsSelectable = MAPI_FALSE;
                m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsVisible = MAPI_FALSE;
                m_TsInfo.u16ServiceCount++;
            }
        }
    }
}

void MW_DVB_SI_PSI_Parser::_BuildChannelInfo(void)
{
    MAPI_U16 u16TotalChannel = MAX_CHANNEL_IN_MUX;
    MAPI_U16 u16CellID = INVALID_CELLID;
    MAPI_BOOL bHaveValidService=MAPI_FALSE;
    MAPI_SI_BAT_TS_INFO *pstTsInfo = NULL;
    //printf("%s.............%d\n",__FUNCTION__,__LINE__);
    table_release sdt(&m_stSdt, table_release::E_TABLE_SDT);
    sdt.setReleaseType(RELEASE_ALL);
    table_release nit(&m_stNit, table_release::E_TABLE_NIT);
    nit.setReleaseType(RELEASE_REGION|RELEASE_PARTIAL_RECEPTION|RELEASE_LCNV2);

    if((m_eParserBaseType == MW_DVB_SDT_BASE) ||
        (IS_SDT_BASE_COUNTRY(m_eCountry) && (m_eParserType == MW_DVB_T_PARSER))
        /*||((E_GERMANY == m_eCountry)&& (m_eParserBaseType != MW_DVB_PAT_BASE))*/ //cannot find spec. so, remove it.
      )
    {
        if(m_stSdt.u8Version == INVALID_PSI_SI_VERSION)
        {
            m_TsInfo.u16ServiceCount = 0;
            return;
        }
    }

    if(m_bEnableNetworkFilter && (m_eParserBaseType == MW_DVB_NIT_BASE))
    {
        if(m_stNit.u8Version == INVALID_PSI_SI_VERSION )
        {
            m_TsInfo.u16ServiceCount = 0;
            return;
        }
    }

    if(m_eParserBaseType == MW_DVB_PAT_BASE)
    {
        m_TsInfo.u16TSID  = m_stPat.u16TsId;
    }
    else if(m_stSdt.u8Version != INVALID_PSI_SI_VERSION)
    {
        m_TsInfo.u16TSID  = m_stSdt.wTransportStream_ID;
    }
    else
    {
        m_TsInfo.u16TSID  = m_stPat.u16TsId;
    }

    if(m_stSdt.u8Version != INVALID_PSI_SI_VERSION)
    {
        m_TsInfo.u16ONID = m_stSdt.wOriginalNetwork_ID;
    }
    else if(m_stNit.u8Version != INVALID_PSI_SI_VERSION)
    {
        m_TsInfo.u16ONID = INVALID_ON_ID;//If no found match TSID in NIT table , ONID set Invalid

        for(U8 i = 0; i < m_stNit.u16TSNumber; i++)
        {
            if( m_TsInfo.u16TSID == m_stNit.pstTSInfo[i].wTransportStream_ID )
            {
                m_TsInfo.u16ONID = m_stNit.pstTSInfo[i].u16ONID;
            }
        }
    }
    else
    {
         m_TsInfo.u16ONID = INVALID_ON_ID;
    }

    if((m_eParserType == MW_DVB_C_PARSER )&& ((m_enCableOperator == EN_CABLEOP_CDCABLE) || (m_enCableOperator == EN_CABLEOP_CDSMATV)))
    {
        if(m_stSdt.wOriginalNetwork_ID != CANALDIGITAL_ONID)
        {
            m_TsInfo.u16ServiceCount = 0;
            return;
        }
    }
    if(m_stNit.u8Version != INVALID_PSI_SI_VERSION)
    {
        if(IS_NORDIC_COUNTRY(m_eCountry))
        {
            if(m_stNit.u16NetworkID >= 0xFF01)
            {
                m_TsInfo.u16ServiceCount = 0;
                m_u16NewAddChnNum = 0;
                return;
            }
            else if ((m_TsInfo.u16ONID >= 0xFF00)
                || ((m_stSdt.u8Version != INVALID_PSI_SI_VERSION) && (m_stSdt.wOriginalNetwork_ID >= 0xFF00))
                ||_IsOnidInNitInvalid())
            {
                m_TsInfo.u16ServiceCount = 0;
                m_u16NewAddChnNum = 0;
                return;
            }
        }
        else if(MAPI_TRUE== _IsSpecificSupport(E_DVB_CHECK_NON_NORDIG_TEST_NETWORK,&m_eCountry, NULL))
        {
            if ((m_stNit.u16NetworkID >= 0xFF00)
                || ((m_TsInfo.u16ONID >= 0xFF00)
                || ((m_stSdt.u8Version != INVALID_PSI_SI_VERSION) && (m_stSdt.wOriginalNetwork_ID >= 0xFF00))))
            {
                m_TsInfo.u16ServiceCount = 0;
                m_u16NewAddChnNum = 0;
                return;
            }
        }
        m_TsInfo.u16NID = m_stNit.u16NetworkID;
        m_TsInfo.u8NetWrokNameLen = m_stNit.u8NetWrokNameLen;
        memcpy(m_TsInfo.au8NetWorkName, m_stNit.au8NetWorkName , m_TsInfo.u8NetWrokNameLen);
    }

    if (m_bEnableBouquetFilter && (m_stBat.u8Version != INVALID_PSI_SI_VERSION))
    {
        pstTsInfo = m_stBat.pstTSInfoList;
        while(pstTsInfo)
        {
            if(m_TsInfo.u16TSID == pstTsInfo->u16TransportStream_ID)
            {
                m_TsInfo.u16TSID = pstTsInfo->u16TransportStream_ID;
                MW_SI_PARSER_SCAN("BAT m_TsInfo.u16TSID   0x%x\n",m_TsInfo.u16TSID);
                break;
            }
            pstTsInfo = pstTsInfo->next;
        }
    }
    m_TsInfo.u16ServiceCount = 0;
    if(m_eParserMode == E_DVB_MONITOR)
    {
        u16TotalChannel = m_u16NewAddChnNum;
    }

    if (m_eParserMode == E_DVB_SCAN)
    {
        _CollectTargetRegionInfo();
        _CollectChannelListInfo();
    }

    if((E_NEWZEALAND == m_eCountry) && (MW_SI_ONID_NEWZEALAND == m_TsInfo.u16ONID))
    {
        m_ParserCallBackInfo.EventHandler(E_DVB_SI_GET_CELLID,(MAPI_U32)m_ParserCallBackInfo.pCallbackReference,(MAPI_U32)&u16CellID);
    }
    else
    {
        u16CellID = INVALID_CELLID;
    }

    for(int i = 0; i < u16TotalChannel; i++)
    {
        MAPI_U16 u16ServiceID;
	    MAPI_U16 uTransportStream_ID=0xFFFF;
        MAPI_BOOL bFreeCAMode = MAPI_FALSE;
        if(m_eParserMode == E_DVB_MONITOR)
        {
            uTransportStream_ID=m_stPat.u16TsId;
            u16ServiceID = m_au16NewProg2Add[i];
        }
        else if((m_eParserBaseType != MW_DVB_PAT_BASE) && (m_stSdt.u8Version != INVALID_PSI_SI_VERSION))
        {
            uTransportStream_ID=m_stSdt.wTransportStream_ID;
            u16ServiceID = m_stSdt.astServiceInfo[i].u16ServiceID;
            bFreeCAMode = m_stSdt.astServiceInfo[i].u8FreeCAMode;
        }
        else
        {
            uTransportStream_ID=m_stPat.u16TsId;
            u16ServiceID = m_stPat.ServiceIDInfo[i].u16ServiceID;
        }

        if(u16ServiceID && (MAPI_TRUE == _CheckSrvInAvailCell(&m_stSdt, u16CellID, u16ServiceID)))
        {
            MAPI_BOOL bSkip = MAPI_FALSE; //for for skip no av and no data(MHEG5/MHP/TTX/sub) program
            _ResetServiceInfo(m_ProgramInfo[m_TsInfo.u16ServiceCount]);
            //update PAT
            m_ProgramInfo[m_TsInfo.u16ServiceCount].u16ServiceID = u16ServiceID;
            m_ProgramInfo[m_TsInfo.u16ServiceCount].u8PatVer = m_stPat.u8Version;
            //CA Exist shout consider both SDT free_CA_mode and PMT CA_descriptor
            if(MAPI_TRUE == _IsSpecificSupport(E_DVB_CHECK_SDT_FREE_CA_MODE,&m_eCountry, NULL))
            {
                m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsCAExist = bFreeCAMode;
            }

            for(int j = 0; j < MAX_CHANNEL_IN_MUX; j++)
            {
                if(u16ServiceID == m_stPat.ServiceIDInfo[j].u16ServiceID)
                {
                    m_ProgramInfo[m_TsInfo.u16ServiceCount].u16PmtPID = m_stPat.ServiceIDInfo[j].u16PmtPID;
                    break;
                }
            }
            //update SDT
            m_ProgramInfo[m_TsInfo.u16ServiceCount].u8SdtVer = m_stSdt.u8Version;
            m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceType = E_SERVICETYPE_INVALID;
            for(int j = 0; j < MAX_CHANNEL_IN_MUX; j++)
            {
                if(m_stSdt.astServiceInfo[j].u16ServiceID && (u16ServiceID == m_stSdt.astServiceInfo[j].u16ServiceID)&&
                    ((uTransportStream_ID==m_stSdt.wTransportStream_ID)||
                    (m_eParserBaseType == MW_DVB_PAT_BASE)) //For AVS streamAvenue_1920x1080_5Mbps.ts, its SDT tsid is zero. We shall still can use SDT info when pat base
                )
                {
                    memcpy(m_ProgramInfo[m_TsInfo.u16ServiceCount].au8ServiceName, m_stSdt.astServiceInfo[j].u8ServiceName, MAPI_SI_MAX_SERVICE_NAME);
                    memcpy(m_ProgramInfo[m_TsInfo.u16ServiceCount].au8ServiceProviderName, m_stSdt.astServiceInfo[j].u8ServiceProviderName, MAPI_SI_MAX_PROVIDER_NAME);

                    _UpdateServiceType(&m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceType, &m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceTypePrio, m_stSdt.astServiceInfo[j].u8ServiceType);
                    m_ProgramInfo[m_TsInfo.u16ServiceCount].u8RealServiceType = m_stSdt.astServiceInfo[j].u8ServiceType;
#if(NVOD_ENABLE == 1)
                    if((m_stSdt.astServiceInfo[j].u16NvodRefSrvID != INVALID_SERVICE_ID)&&(m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceType == E_SERVICETYPE_INVALID))
                    {
                        m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceType = E_SERVICETYPE_NVODTS;
                        m_ProgramInfo[m_TsInfo.u16ServiceCount].u8RealServiceType = E_TYPE_NVODTS;

                    }
                    if(m_stSdt.astServiceInfo[j].u8NvodRealSrvNum!=0)
                    {
                        m_ProgramInfo[m_TsInfo.u16ServiceCount].u8NvodRealSrvNum=m_stSdt.astServiceInfo[j].u8NvodRealSrvNum;
                        for(int k=0;k<m_ProgramInfo[m_TsInfo.u16ServiceCount].u8NvodRealSrvNum;k++)
                        {
                            m_ProgramInfo[m_TsInfo.u16ServiceCount].stNvodRealSrv[k].u16TsId=m_stSdt.astServiceInfo[j].stNvodRealSrv[k].u16TsId;
                            m_ProgramInfo[m_TsInfo.u16ServiceCount].stNvodRealSrv[k].u16OnId=m_stSdt.astServiceInfo[j].stNvodRealSrv[k].u16OnId;
                            m_ProgramInfo[m_TsInfo.u16ServiceCount].stNvodRealSrv[k].u16SrvId=m_stSdt.astServiceInfo[j].stNvodRealSrv[k].u16SrvId;
                        }
                    }
                    m_ProgramInfo[m_TsInfo.u16ServiceCount].u16NvodRefSrvID=m_stSdt.astServiceInfo[j].u16NvodRefSrvID;

#endif
#if (MULTIPLE_SERVICE_NAME_ENABLE == 1)
                    m_ProgramInfo[m_TsInfo.u16ServiceCount].u8MultiServiceNameCnt = (m_stSdt.astServiceInfo[j].u8MultiServiceNameCnt > MAX_MULTILINGUAL_SERVICE_NAME) ? MAX_MULTILINGUAL_SERVICE_NAME : m_stSdt.astServiceInfo[j].u8MultiServiceNameCnt;
                    for(MAPI_U8 u8Index = 0; u8Index < m_ProgramInfo[m_TsInfo.u16ServiceCount].u8MultiServiceNameCnt; u8Index++)
                    {
                        memcpy(m_ProgramInfo[m_TsInfo.u16ServiceCount].u8MultiServiceName[u8Index], m_stSdt.astServiceInfo[j].u8MultiServiceName[u8Index], MAPI_SI_MAX_SERVICE_NAME);
                        m_ProgramInfo[m_TsInfo.u16ServiceCount].aeLangIndex[u8Index] = m_stSdt.astServiceInfo[j].aeLangIndex[u8Index];
                    }
#endif
                    break;
                }
            }
            if((m_stSdt.astServiceInfo[i].stLinkageInfo.enLinkageType)&&(IS_NORDIC_COUNTRY(m_eCountry)))
            {
                if (m_stSdt.astServiceInfo[i].stLinkageInfo.enLinkageType == MAPI_SI_LINKAGE_NORDIG_SIMULCAST_REPLACEMENT_SERVICE)
                {
                    MW_SI_PARSER_SCAN("Nordig simulcast Replacement:build service:%s\n",m_stSdt.astServiceInfo[i].u8ServiceName);
                    MW_DVB_RP_SERVICE_INFO RPServiceInfo;
					memset(&RPServiceInfo, 0, sizeof(MW_DVB_RP_SERVICE_INFO));
                    RPServiceInfo.enLinkageType = m_stSdt.astServiceInfo[i].stLinkageInfo.enLinkageType;
                    RPServiceInfo.bSer_Replacement = m_stSdt.astServiceInfo[i].stLinkageInfo.u8Ser_Replacement;
                    RPServiceInfo.u16ONId = m_stSdt.astServiceInfo[i].stLinkageInfo.u16ONId;
                    RPServiceInfo.u16TSId = m_stSdt.astServiceInfo[i].stLinkageInfo.u16TSId;
                    RPServiceInfo.u16ServiceId = m_stSdt.astServiceInfo[i].stLinkageInfo.u16ServiceId;
                    RPServiceInfo.u16RPS_ONId = m_stSdt.wOriginalNetwork_ID;
                    RPServiceInfo.u16RPS_TSId = m_stSdt.wTransportStream_ID;
                    RPServiceInfo.u16RPS_ServiceId = m_stSdt.astServiceInfo[i].u16ServiceID;
                    RPServiceInfo.bCurService = MAPI_FALSE;
                    RPServiceInfo.u8FreeCAMode = m_stSdt.astServiceInfo[i].u8FreeCAMode;
                    RPServiceInfo.eRunningStatus =  (MW_SI_SDT_RUNNINGSTATUS)m_stSdt.astServiceInfo[i].u8RunningStatus;
                    m_ListRPServiceInfo.push_back(RPServiceInfo);
                }
            }

            //update PMT
            for(int j = 0; j < MAX_CHANNEL_IN_MUX; j++)
            {
                if(u16ServiceID == m_astAllPmt[j].wServiceID)
                {
                    m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsServiceIDOnly = m_astAllPmt[j].bIsSIDOnly;
                    m_ProgramInfo[m_TsInfo.u16ServiceCount].u16ServiceID = u16ServiceID;
                    //CA Exist shout consider both SDT free_CA_mode and PMT CA_descriptor
                    m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsCAExist |= m_astAllPmt[j].bIsCAExist;
                    m_ProgramInfo[m_TsInfo.u16ServiceCount].u8PmtVer = m_astAllPmt[j].u8Version;
                    //m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsDataService = m_astPmt[j].u8IsDataService;
                    m_ProgramInfo[m_TsInfo.u16ServiceCount].u16PCRPid = m_astAllPmt[j].wPCRPid;

                    _UpdateVideo(&m_ProgramInfo[m_TsInfo.u16ServiceCount].stVideoInfo, m_astAllPmt[j].sVideoInfo);

                    for(int k = 0; k < MAPI_SI_MAX_DATA; k++)
                    {
                        if((m_astAllPmt[j].sDataBroadcastInfo[k].u16DBId == MAPI_DATA_BC_ID_MHEG5) || (m_astAllPmt[j].sDataBroadcastInfo[k].u16DBId == MAPI_DATA_BC_ID_MHP))
                        {
                            m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsDataBroadcastService = MAPI_TRUE;
                            break;
                        }
                    }
#if (ISDB_SYSTEM_ENABLE == 1)
                    for(int k=0; k<MAPI_SI_MAX_APP_SIGNALLING_NUM; k++)
                    {
                        if (m_astAllPmt[j].stAppSignalInfo.astAppSignalling[k].u16AppType != MAPI_INVALID_APPLICATION_TYPE)
                        {
                            m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsDataBroadcastService = MAPI_TRUE;
                            break;
                        }
                    }
#endif
                    if((0 != m_astAllPmt[j].astDVBSubtInfo[0].u16Sub_Pid) ||
                            (((0 != m_astAllPmt[j].astEBUSubtInfo[0].u16TTX_Pid) || (0 != m_astAllPmt[j].astTXTInfo[0].u16TTX_Pid)) && ((m_eCountry != E_UK))))
                    {
                        m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsDataService = MAPI_TRUE;
                    }
                    _UpdateAudio(m_ProgramInfo[m_TsInfo.u16ServiceCount].stAudInfo, m_astAllPmt[j].stAudInfo);
                    /*
                    if((m_ProgramInfo[m_TsInfo.u16ServiceCount].stVideoInfo.wVideoPID == INVALID_PID) &&
                        (m_ProgramInfo[m_TsInfo.u16ServiceCount].stAudInfo[0].u16AudPID == INVALID_PID) &&
                        (m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsDataService == MAPI_FALSE) &&
                        (m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsDataBroadcastService == MAPI_FALSE) &&
                        (m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsServiceIDOnly == MAPI_FALSE))
                    {
                        bSkip=MAPI_TRUE;
                    }*/
                    break;
                }
            }

            if(m_eCountry != E_CHINA)
            {
                if((m_eParserBaseType == MW_DVB_PAT_BASE)||((m_eParserBaseType == MW_DVB_SDT_PAT_BASE)&&(m_stSdt.u8Version == INVALID_PSI_SI_VERSION)))
                {
                    if(m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceType == E_SERVICETYPE_INVALID)
                    {
                        if(m_ProgramInfo[m_TsInfo.u16ServiceCount].stVideoInfo.wVideoPID != INVALID_PID)
                        {
                            m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceType = E_SERVICETYPE_DTV;
                        }
                        else if((m_ProgramInfo[m_TsInfo.u16ServiceCount].stVideoInfo.wVideoPID == INVALID_PID) &&
                                (m_ProgramInfo[m_TsInfo.u16ServiceCount].stAudInfo[0].u16AudPID != INVALID_PID))
                        {
                            m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceType = E_SERVICETYPE_RADIO;
                        }
                    }
                }
                if(m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceType == E_SERVICETYPE_INVALID)
                {
                    bSkip = TRUE;
                }
            }
            else //if(m_eCountry == E_CHINA)
            {
                if((m_ProgramInfo[m_TsInfo.u16ServiceCount].stVideoInfo.wVideoPID == INVALID_PID)
                && (m_ProgramInfo[m_TsInfo.u16ServiceCount].stAudInfo[0].u16AudPID == INVALID_PID)
                && (m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsDataBroadcastService == MAPI_FALSE))
                {
                      bSkip = MAPI_TRUE;
                }

                if(m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceType == E_SERVICETYPE_INVALID)
                {
                      if(m_ProgramInfo[m_TsInfo.u16ServiceCount].stVideoInfo.wVideoPID != INVALID_PID&&
                               (m_ProgramInfo[m_TsInfo.u16ServiceCount].stAudInfo[0].u16AudPID != INVALID_PID))
                      {
                          m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceType = E_SERVICETYPE_DTV;
                      }
                      else if(m_ProgramInfo[m_TsInfo.u16ServiceCount].stAudInfo[0].u16AudPID != INVALID_PID)
                      {
                          m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceType = E_SERVICETYPE_RADIO;
                      }
                      else
                      {
                          bSkip = MAPI_TRUE;
                      }
                }
                else if(m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceType  == E_SERVICETYPE_DTV)
                {
                    if(m_ProgramInfo[m_TsInfo.u16ServiceCount].stVideoInfo.wVideoPID == INVALID_PID)
                    {
                        if(m_ProgramInfo[m_TsInfo.u16ServiceCount].stAudInfo[0].u16AudPID != INVALID_PID)
                        {
                            m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceType = E_SERVICETYPE_RADIO;
                        }
                        else if(m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsDataBroadcastService == MAPI_FALSE)
                        {
                            bSkip = MAPI_TRUE;
                        }
                    }
                    else if(m_ProgramInfo[m_TsInfo.u16ServiceCount].stAudInfo[0].u16AudPID == INVALID_PID)
                    {
                        //bSkip = MAPI_TRUE;
                    }
                }
                else if(m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceType == E_SERVICETYPE_RADIO)
                {
                    if(m_ProgramInfo[m_TsInfo.u16ServiceCount].stAudInfo[0].u16AudPID  == INVALID_PID)
                    {
                        bSkip = MAPI_TRUE;
                    }
                }
                else if(m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceType == E_SERVICETYPE_UNITED_TV || m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceType == E_SERVICETYPE_DATA)
                {
                     bSkip = MAPI_TRUE;
                }
            }
            if(!bSkip && (MAPI_TRUE == _IsSpecificSupport(E_DVB_USER_DEFINE_SERVICE_NAME_SUPPORT,&m_eCountry, NULL)))
            {
                U8 u8NameLen = strlen((char*)m_ProgramInfo[m_TsInfo.u16ServiceCount].au8ServiceName);
                if(u8NameLen == 0)
                {
                    sprintf((char*)m_ProgramInfo[m_TsInfo.u16ServiceCount].au8ServiceName, "ch%d", m_ProgramInfo[m_TsInfo.u16ServiceCount].u16ServiceID);
                }
            }

            //update NIT
            m_ProgramInfo[m_TsInfo.u16ServiceCount].u8NitVer = m_stNit.u8Version;

            if(m_stNit.u8Version != INVALID_PSI_SI_VERSION)
            {
                for(int j = 0; j < m_stNit.u16TSNumber; j++)
                {
                    if(m_TsInfo.u16TSID == m_stNit.pstTSInfo[j].wTransportStream_ID)
                    {
                        for(int k = 0; k < MAX_CHANNEL_IN_MUX; k++)
                        {
                            if((u16ServiceID == m_stNit.pstTSInfo[j].astLcnInfo[k].u16ServiceID)&&(!m_bEnableBouquetFilter))
                            {
                                if ((m_eParserType == MW_DVB_T_PARSER) && (m_eCountry == E_FINLAND)
                                    && (m_ProgramInfo[m_TsInfo.u16ServiceCount].u16LCN != INVALID_LOGICAL_CHANNEL_NUMBER))
                                {
                                    // it shall install all services with same service but different LCN for Antenna Ready HD
                                    if ((m_TsInfo.u16ServiceCount+1)>=MAX_CHANNEL_IN_MUX)
                                    {
                                        break;
                                    }
                                    else
                                    {
                                        memcpy(&m_ProgramInfo[m_TsInfo.u16ServiceCount+1], &m_ProgramInfo[m_TsInfo.u16ServiceCount],sizeof(MW_DVB_PROGRAM_INFO));
                                        m_TsInfo.u16ServiceCount++;
                                        m_ProgramInfo[m_TsInfo.u16ServiceCount].u16LCN = m_stNit.pstTSInfo[j].astLcnInfo[k].u16LCNNumber;
                                    }
                                }
                                else
                                {
                                    m_ProgramInfo[m_TsInfo.u16ServiceCount].u16LCN = m_stNit.pstTSInfo[j].astLcnInfo[k].u16LCNNumber;
                                    m_ProgramInfo[m_TsInfo.u16ServiceCount].u16SimuLCN = m_stNit.pstTSInfo[j].astLcnInfo[k].u16SimuLCNNumber;
                                    if((m_ProgramInfo[m_TsInfo.u16ServiceCount].u16LCN == 0) && (!IS_DTG_COUNTRY(m_eCountry) /*&& !IS_NORDIC_COUNTRY(m_eCountry)*/))
                                    {
                                        m_stNit.pstTSInfo[j].astLcnInfo[k].bIsSelectable = MAPI_FALSE;
                                        m_stNit.pstTSInfo[j].astLcnInfo[k].bIsVisable = MAPI_FALSE;
                                    }
                                    m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsSelectable = m_stNit.pstTSInfo[j].astLcnInfo[k].bIsSelectable;
                                    m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsVisible = m_stNit.pstTSInfo[j].astLcnInfo[k].bIsVisable;
                                    m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsSpecialSrv = m_stNit.pstTSInfo[j].astLcnInfo[k].bIsSpecialSrv;
                                }
                                // it shall install all services with same service but different LCN for Antenna Ready HD
                                if ((m_eParserType == MW_DVB_T_PARSER) && (m_eCountry == E_FINLAND))
                                {
                                    continue;
                                }
                                else
                                {
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            }
            if((pstTsInfo)&&(m_bEnableBouquetFilter))
            {
                for(int m = 0; m < pstTsInfo->u16LCNNumber; m++)
                {
                    if(u16ServiceID == pstTsInfo->astLcnInfo[m].u16ServiceID)
                    {
                        m_ProgramInfo[m_TsInfo.u16ServiceCount].u16LCN = pstTsInfo->astLcnInfo[m].u16LCNNumber;
                        MW_SI_TARGET_REGION_DBG("SID=%x,lcn=%d\n",m_ProgramInfo[m_TsInfo.u16ServiceCount].u16ServiceID,m_ProgramInfo[m_TsInfo.u16ServiceCount].u16LCN);
                    }
                }
            }
            E_CHINA_DVBCREGION eRegion = E_CN_NUM;
            if (m_ParserCallBackInfo.EventHandler)
            {
                m_ParserCallBackInfo.EventHandler(E_DVB_SI_GET_CHINA_DVBCREGION,(MAPI_U32)m_ParserCallBackInfo.pCallbackReference,(MAPI_U32)&eRegion);
            }
            if(E_CN_YANGZHOU_CABLE == eRegion)
            {
                for(int n = 0; n < m_stSdt.wServiceNumber; n++)
                {
                    if((u16ServiceID == m_stSdt.astServiceInfo[n].stLcnInfo.u16ServiceID )
                    && (u16ServiceID > 0))
                    {
                        m_ProgramInfo[m_TsInfo.u16ServiceCount].u16LCN = m_stSdt.astServiceInfo[n].stLcnInfo.u16LCNNumber;

                    }
                }
            }
#if (ISDB_SYSTEM_ENABLE == 1)
            if( m_eParserType == MW_ISDB_T_PARSER )
            {
                if(( m_bDisableOneSegProgFilter == FALSE ) && (NULL != m_stNit.pstPartialReception))
                {  //remove one-segment service
                    for(int j=0;(j<m_stNit.u16TSNumber) && !bSkip;j++)
                    {
                        if(m_stNit.pstTSInfo[j].wTransportStream_ID == m_TsInfo.u16TSID)
                        {
                            for( int k = 0; k < m_stNit.pstPartialReception[j].u8PRServiceCount; k++ )
                            {
                                if(u16ServiceID == m_stNit.pstPartialReception[j].au16PRServiceId[k] )
                                {
                                    bSkip = MAPI_TRUE;
                                    break;
                                }
                            }
                        }
                    }
                    if( bSkip == MAPI_FALSE )
                    {
                        for(int j=0;j<m_stPat.u16ServiceCount;j++)
                        {
                           if(u16ServiceID == m_stPat.ServiceIDInfo[j].u16ServiceID)
                           {
                               if( IS_ONE_SEG_SERVICE(m_stPat.ServiceIDInfo[j].u16PmtPID,m_stPat.ServiceIDInfo[j].u16ServiceID) == MAPI_TRUE )
                               {
                                    bSkip = MAPI_TRUE;
                                    break;
                               }
                           }
                        }
                    }
                }
            }
#endif

            //Check not show data service behind SDT and NIT update.
            if ((m_ProgramInfo[m_TsInfo.u16ServiceCount].u8RealServiceType ==  E_TYPE_DATA) || (m_ProgramInfo[m_TsInfo.u16ServiceCount].u8RealServiceType == E_TYPE_MHP))
            {
                if(MAPI_FALSE == _IsSpecificSupport(E_DVB_DATA_SERVICE_SUPPORT,&m_eCountry, NULL))
                {
                    m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsSelectable = MAPI_FALSE;
                    m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsVisible = MAPI_FALSE;
                }
            }

            MW_SI_PARSER_SCAN("sid %x name %s lcn %d simulcn %d visible %x selectable %x\n", u16ServiceID, m_ProgramInfo[m_TsInfo.u16ServiceCount].au8ServiceName, m_ProgramInfo[m_TsInfo.u16ServiceCount].u16LCN, m_ProgramInfo[m_TsInfo.u16ServiceCount].u16SimuLCN,
                              m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsVisible, m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsSelectable);

            if(!((0x0001 <= m_ProgramInfo[m_TsInfo.u16ServiceCount].u16PmtPID) && (m_ProgramInfo[m_TsInfo.u16ServiceCount].u16PmtPID <= 0x1FFF)))
            {
                bSkip = MAPI_TRUE;
            }
            else if(!((m_ProgramInfo[m_TsInfo.u16ServiceCount].u16PCRPid <= 0x0001) || ((0x0010 <= m_ProgramInfo[m_TsInfo.u16ServiceCount].u16PCRPid) && (m_ProgramInfo[m_TsInfo.u16ServiceCount].u16PCRPid <= 0x1FFF))))
            {
                bSkip = MAPI_TRUE;
            }
            else if((m_ProgramInfo[m_TsInfo.u16ServiceCount].stVideoInfo.wVideoPID <= 0x000F) || (0x1FFF < m_ProgramInfo[m_TsInfo.u16ServiceCount].stVideoInfo.wVideoPID))
            {
                bSkip = MAPI_TRUE;
            }
            else if((m_eCountry == E_CHINA) && (m_ProgramInfo[m_TsInfo.u16ServiceCount].stAudInfo[0].u16AudPID == 0))
            {
                bSkip=MAPI_TRUE;
            }

            #if (MSTAR_TVOS == 1)
            if(m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceType == E_SERVICETYPE_INVALID)
            {
                bSkip = TRUE;
            }
            #endif

            if(MAPI_TRUE==_IsSpecificSupport(E_DVB_SKIP_NONE_PMT_PID,&m_ProgramInfo[m_TsInfo.u16ServiceCount].u16PmtPID,NULL))
            {
                bSkip=MAPI_TRUE;
            }

            #if 0
            {
                U8 i;
                printf("\n/*******************************/\n");
                printf("Service Name: %s", m_ProgramInfo[m_TsInfo.u16ServiceCount].au8ServiceName);
                printf("\n");
                printf("LCN %u\n",m_ProgramInfo[m_TsInfo.u16ServiceCount].u16LCN);
                printf("simuLCN %u\n",m_ProgramInfo[m_TsInfo.u16ServiceCount].u16SimuLCN);
                printf("Vid Pid: 0x%04x\n",m_ProgramInfo[m_TsInfo.u16ServiceCount].stVideoInfo.wVideoPID);
                printf("video type: %d\n",m_ProgramInfo[m_TsInfo.u16ServiceCount].stVideoInfo.bVideoType);
                printf("PMT Pid: 0x%04x\n",m_ProgramInfo[m_TsInfo.u16ServiceCount].u16PmtPID);
                printf("PCR Pid: 0x%04x\n",m_ProgramInfo[m_TsInfo.u16ServiceCount].u16PCRPid);
                printf("Service ID: 0x%04x\n",m_ProgramInfo[m_TsInfo.u16ServiceCount].u16ServiceID);
                printf("RealService type:0x%04x\n", m_ProgramInfo[m_TsInfo.u16ServiceCount].u8RealServiceType);
                printf("Service type:0x%04x\n", m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceType);
                printf("Service type Prio:0x%04x\n", m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceTypePrio);
                printf("Visible: %s\n",(m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsVisible)?"TRUE":"FALSE");
                printf("CAExist: %s\n",(m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsCAExist)?"TRUE":"FALSE");
                printf("Service ID only: %s\n",(m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsServiceIDOnly)?"TRUE":"FALSE");
                printf("Data service: %s\n",(m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsDataService)?"TRUE":"FALSE");
                printf("MHEG5/MHP: %s\n",(m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsDataBroadcastService)?"TRUE":"FALSE");
                printf("Data service: %s\n",(m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsDataService)?"TRUE":"FALSE");

                for(i = 0; i < MAX_AUD_LANG_NUM; i++)
                {
                    if(m_ProgramInfo[m_TsInfo.u16ServiceCount].stAudInfo[i].u16AudPID == INVALID_PID)
                    break;

                    printf("aud[%u] type[%d],", i, m_ProgramInfo[m_TsInfo.u16ServiceCount].stAudInfo[i].u8AudType);

                    printf("ISO[%s]  Pid[0x%x]\n", m_ProgramInfo[m_TsInfo.u16ServiceCount].stAudInfo[i].aISOLangInfo.u8ISOLangInfo, m_ProgramInfo[m_TsInfo.u16ServiceCount].stAudInfo[i].u16AudPID);
                }
                printf("Skip the prog: %s\n",(bSkip==MAPI_TRUE)?"YES":"NO");
                printf("\n/*******************************/\n");
            }
            #endif
#if 0   //mark first for evora pass
            if(IS_UPC(m_enCableOperator) && m_eParserType == MW_DVB_C_PARSER)/*Operator checkings only for DVBC mode*/
            {
                if(m_ProgramInfo[m_TsInfo.u16ServiceCount].u16LCN > 999)
                {
                   bSkip = MAPI_TRUE;
                }
            }
#endif
            /*Ziggo/UPC/Telenet NIT search implementation*/
            if((m_eParserType == MW_DVB_C_PARSER)
                && ((m_enCableOperator == EN_CABLEOP_ZIGGO) || IS_UPC(m_enCableOperator)
                    || (m_enCableOperator == EN_CABLEOP_TELENET)))
            {
                if((m_ProgramInfo[m_TsInfo.u16ServiceCount].u16LCN == DEFAULT_LCN) ||( m_ProgramInfo[m_TsInfo.u16ServiceCount].u16LCN == 0))
                {
                   bSkip = MAPI_TRUE;
                }
            }

            if(bSkip)
            {
                m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceType = E_SERVICETYPE_INVALID;
                m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsSelectable = MAPI_FALSE;
                m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsVisible = MAPI_FALSE;
                m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsSpecialSrv = MAPI_FALSE;

            }
            else
            {
                bHaveValidService=MAPI_TRUE;
            }

            //if(!bSkip)
            {
                m_TsInfo.u16ServiceCount++;
            }
        }
    }
    if(bHaveValidService == MAPI_FALSE)
    {
        m_TsInfo.u16ServiceCount=0;
    }
    _AddExtraSSUService();
}

#if (NCD_ENABLE == 1)
void MW_DVB_SI_PSI_Parser::_BuildChannelInfoOther(MAPI_U16 u16TSID, MAPI_SI_SDT_SERVICE_INFO *pstServiceInfo, MW_DVB_PROGRAM_INFO& stProgInfo)
{
    int i, j;

    memset(&stProgInfo, 0, sizeof(MW_DVB_PROGRAM_INFO));
    stProgInfo.u16LCN = DEFAULT_LCN;
    stProgInfo.u16SimuLCN = DEFAULT_SIMU_LCN;
    stProgInfo.bIsVisible = MAPI_TRUE;
    stProgInfo.bIsSelectable = MAPI_TRUE;

    if(m_stNit.u8Version != INVALID_PSI_SI_VERSION)
    {
        MAPI_BOOL bGot = MAPI_FALSE;
        for(i=0; i<m_stNit.u16TSNumber; i++)
        {
            if(m_stNit.pstTSInfo[i].wTransportStream_ID == u16TSID)
            {
                for(j=0; j<MAX_CHANNEL_IN_MUX; j++)
                {
                    if(m_stNit.pstTSInfo[i].astLcnInfo[j].u16ServiceID == pstServiceInfo->u16ServiceID)
                    {
                        stProgInfo.u16LCN = m_stNit.pstTSInfo[i].astLcnInfo[j].u16LCNNumber;
                        stProgInfo.u16SimuLCN = m_stNit.pstTSInfo[i].astLcnInfo[j].u16SimuLCNNumber;
                        stProgInfo.bIsVisible = m_stNit.pstTSInfo[i].astLcnInfo[j].bIsVisable;
                        stProgInfo.bIsSelectable = m_stNit.pstTSInfo[i].astLcnInfo[j].bIsSelectable;
                        stProgInfo.bIsSpecialSrv = m_stNit.pstTSInfo[i].astLcnInfo[j].bIsSpecialSrv;
                        bGot = MAPI_TRUE;
                        break;
                    }
                }
            }
            if(bGot)
            {
                break;
            }
        }
    }

    stProgInfo.u16ServiceID = pstServiceInfo->u16ServiceID;
    stProgInfo.u8ServiceType = pstServiceInfo->u8ServiceType;
    _UpdateServiceType(&stProgInfo.u8ServiceType, &stProgInfo.u8ServiceTypePrio, pstServiceInfo->u8ServiceType);
    _UpdateProgInfoByServiceType(pstServiceInfo->u8ServiceType, NULL, &stProgInfo);
    stProgInfo.u8RealServiceType = pstServiceInfo->u8ServiceType;

    if((m_eParserBaseType == MW_DVB_NIT_BASE)&&IS_UPC(m_enCableOperator)
        &&((stProgInfo.u16LCN == DEFAULT_LCN)||(stProgInfo.u16LCN == 0)))
    {
        stProgInfo.bIsSelectable = MAPI_FALSE;
        stProgInfo.bIsVisible = MAPI_FALSE;
    }

    memcpy(stProgInfo.au8ServiceName, pstServiceInfo->u8ServiceName, MAPI_SI_MAX_SERVICE_NAME);
    memcpy(stProgInfo.au8ServiceProviderName, pstServiceInfo->u8ServiceProviderName, MAPI_SI_MAX_PROVIDER_NAME);

#if (MULTIPLE_SERVICE_NAME_ENABLE == 1)
    stProgInfo.u8MultiServiceNameCnt = (pstServiceInfo->u8MultiServiceNameCnt > MAX_MULTILINGUAL_SERVICE_NAME) ? MAX_MULTILINGUAL_SERVICE_NAME : pstServiceInfo->u8MultiServiceNameCnt;
    for(i=0; i<stProgInfo.u8MultiServiceNameCnt; i++)
    {
        stProgInfo.aeLangIndex[i] = pstServiceInfo->aeLangIndex[i];
        memcpy(stProgInfo.u8MultiServiceName[i], pstServiceInfo->u8MultiServiceName[i], MAPI_SI_MAX_SERVICE_NAME);
    }
#endif
}
#endif

MAPI_BOOL MW_DVB_SI_PSI_Parser::_EnBATFilter(MAPI_U16 u16PID, MAPI_U16 u16BouquetID)
{
    MAPI_U8 u8Ver = (m_eParserMode==E_DVB_SCAN) ? INVALID_PSI_SI_VERSION : m_stBat.u8Version;
    MAPI_U16 u16Timeout = (m_eParserMode==E_DVB_SCAN) ? BAT_SCAN_TIMEOUT : BAT_MONITOR_TIMEOUT;

    if ((m_eParserMode==E_DVB_SCAN) && (m_pBatParser))
    {
        _DeleteParser((MAPI_U32)m_pBatParser, MAPI_FALSE);
    }
#if (OAD_ENABLE == 1)
    if((m_bOadStopEit == MAPI_TRUE)&&(m_eParserMode!=E_DVB_SCAN))
    {
        return MAPI_FALSE;
    }
#endif
    if(m_pBatParser == NULL)
    {
        if((m_u32BatMonitorTimer == 0) || (_Timer_DiffTimeFromNow(m_u32BatMonitorTimer) > BAT_MONITOR_PERIOD) ||\
            (m_eParserMode==E_DVB_SCAN))
        {
            MAPI_U16 u16TunnelledPID = u16PID;//(u16PID == INVALID_PID) ? FreesatGetTunnelledPID(EN_FS_TUNNEL_TYPE_BAT) : u16PID;
            if (u16TunnelledPID == INVALID_PID)
            {
                return MAPI_FALSE;
            }
            m_pBatParser = new (std::nothrow) mapi_si_BAT_parser(m_pSi, m_pDemux);
            if(m_pBatParser)
            {
                MAPI_U16 u16BID = u16BouquetID;//(u16BouquetID == INVALID_BID) ? m_stPCTInfo.u16BouquetID : u16BouquetID;
                if(m_pBatParser->Init(0x4000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_NORMAL, u8Ver)
                        && m_pBatParser->Start(u16Timeout, u16BID, MAPI_FALSE, u16TunnelledPID))
                {
                    //printf("%s >>PID:0x%x, BID:0x%x, RegionId:0x%x,  Version:%d\n",__FUNCTION__, u16TunnelledPID, u16BID, m_stPCTInfo.u16RegionID, u8Ver);
                    MW_SI_PARSER_MESSAGE("%s >>PID:0x%x, BID:0x%x,  Version:%d\n",__FUNCTION__, u16TunnelledPID, u16BID, u8Ver);
                    if (m_eParserMode==E_DVB_MONITOR)
                    {
                        m_u32BatMonitorTimer = _GetTime0();
                    }
                    m_s16OpenFilter++;
                }
                else
                {
                    delete m_pBatParser;
                    m_pBatParser = NULL;
                    return MAPI_FALSE;

                }
            }
            else
            {
                printf("%s>>new Tunnelled BAT parser FAILE...\n", __FUNCTION__);
                ASSERT(0);
                return MAPI_FALSE;
            }
        }
    }
    return MAPI_TRUE;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::_ScanStart(void)
{

    MW_SI_PARSER_SCAN("%s %d\n", __FUNCTION__, __LINE__);
    _StopAllFilter(MAPI_TRUE);
#if EIT_BUFFER_USE_QUEUE
    m_bResetEitParser = MAPI_TRUE;
#endif
    _FreeDeliveryInfo(m_stNit, MAPI_FALSE);
    m_bRunning = MAPI_FALSE;
    m_eParserMode = E_DVB_IDLE;
    m_u16NewAddChnNum = 0;
    m_u32StartUpdateTime = INVALID_TIME;
    m_bNitUpdate2Rescan = MAPI_FALSE;
    //if(m_bRunning == MAPI_FALSE)
    {
        MW_SI_PARSER_SCAN("EN_DVB_SCAN_START\n");
        MW_SI_PARSER_SCAN("EN_DVB_SCAN_START clear old event OK\n");


        _ResetInfo(RESET_ALL);
        _ResetStatus();

        m_pSdtParser = new (std::nothrow) mapi_si_SDT_parser(m_pSi, m_pDemux);
        if(m_pSdtParser)
        {
            if(m_pSdtParser->Init(0x2000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_NORMAL, INVALID_PSI_SI_VERSION)
                    && m_pSdtParser->Start(SDT_SCAN_TIMEOUT))
            {
                m_s16OpenFilter++;
            }
            else
            {
                delete m_pSdtParser;
                m_pSdtParser = NULL;
                ASSERT(0);
                return MAPI_FALSE;
            }
        }
        else
        {
            ASSERT(0);
            return MAPI_FALSE;
        }

        if(!((E_CHINA == m_eCountry) && (m_eParserType == MW_DVB_T_PARSER))) // This means CHINA DTMB do not support LCN, therefore it do not need to acquire NIT
        {
            m_pNitParser = new (std::nothrow) mapi_si_NIT_parser(m_pSi, m_pDemux);
            if(m_pNitParser)
            {
                if(m_pNitParser->Init(0x2000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_NORMAL, INVALID_PSI_SI_VERSION)
                        && m_pNitParser->Start(NIT_SCAN_TIMEOUT, m_bEnableNetworkFilter && (m_eParserType != MW_DVB_T_PARSER), m_u16NetworkID))
                {
                    m_s16OpenFilter++;

                }
                else
                {
                    delete m_pNitParser;
                    m_pNitParser = NULL;
                    delete m_pSdtParser;
                    m_pSdtParser = NULL;
                    ASSERT(0);
                    return MAPI_FALSE;
                }
            }
            else
            {
                delete m_pSdtParser;
                m_pSdtParser = NULL;
                ASSERT(0);
                return MAPI_FALSE;
            }
        }

        m_pPatParser = new (std::nothrow) mapi_si_PAT_parser(m_pSi, m_pDemux);
        if(m_pPatParser)
        {
            if(m_pPatParser->Init(0x2000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_NORMAL, INVALID_PSI_SI_VERSION)
                    && m_pPatParser->Start(PAT_SCAN_TIMEOUT))
            {
                m_s16OpenFilter++;


            }
            else
            {
                delete m_pPatParser;
                m_pPatParser = NULL;
                delete m_pNitParser;
                m_pNitParser = NULL;
                delete m_pSdtParser;
                m_pSdtParser = NULL;
                ASSERT(0);
                return MAPI_FALSE;
            }
        }
        else
        {
            delete m_pSdtParser;
            m_pSdtParser = NULL;
            delete m_pNitParser;
            m_pNitParser = NULL;
            ASSERT(0);
            return MAPI_FALSE;
        }
        m_eParserMode = E_DVB_SCAN;
        m_bRunning = MAPI_TRUE;
    }

    if((m_bEnableBouquetFilter)&&(!m_bGotBatInScan))
    {
        _EnBATFilter(PID_SDT_BAT,m_u16BouquetID);
    }
    return MAPI_TRUE;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::EPGUpdateStart()
{
    ASSERT(m_bInit);
    mapi_si_psi_event cEvt;
    cEvt.u32Event = (MAPI_U32) EN_DVB_EPG_UPDATE_START;
    m_pParserTriggerEvent->Send(cEvt);
    return MAPI_TRUE;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::EPGUpdateStop()
{
    ASSERT(m_bInit);
    if(E_DVB_EPG_UPDATE != m_eParserMode)
    {
        return MAPI_TRUE;
    }
    //mapi_scope_lock scopeLock(&m_pParseMonitorMutex);
    mapi_si_psi_event cEvt;
    CLEAR_TRIGGER_EVENT(m_pParserTriggerEvent, &cEvt);
    cEvt.u32Event = (MAPI_U32) EN_DVB_EPG_UPDATE_STOP;
    m_pParserTriggerEvent->Send(cEvt);
    MW_SI_PARSER_MESSAGE("EPGUpdateStop wait start\n");
    while(m_eParserMode != E_DVB_IDLE)
    {
        sleep(0);
    }
    MW_SI_PARSER_MESSAGE("EPGUpdateStop wait end\n");
    return MAPI_TRUE;
}

void MW_DVB_SI_PSI_Parser::_EPGUpdateStart()
{
    MW_SI_PARSER_SCAN("%s %d\n", __FUNCTION__, __LINE__);
    _StopAllFilter(MAPI_TRUE);
    m_bRunning = MAPI_FALSE;
    m_eParserMode = E_DVB_IDLE;
    m_u32EitPfAllMonitorTimer = 0;
    m_u32EitSch1MonitorTimer = 0;
    m_u32EitSch2MonitorTimer = 0;
    m_eParserMode = E_DVB_EPG_UPDATE;
    m_bRunning = MAPI_TRUE;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::_MonitorStart(void)
{
//    mapi_si_psi_event cEvt;
    MAPI_BOOL bIsNetworkChg = MAPI_FALSE;
    m_u16NewAddChnNum = 0;
    m_u32StartUpdateTime = INVALID_TIME;
    m_u32ServiceMoveTimer = INVALID_TIME;
    m_u32ServiceRelocTimer = INVALID_TIME;
    if(m_bRunning)
    {
        //m_bRunning = MAPI_FALSE;
        _StopAllFilter(MAPI_FALSE);
    }
    if (m_bNitUpdate2Rescan == TRUE)
    {
        _FreeDeliveryInfo(m_stNit, MAPI_FALSE);
    }
    else
    {
        _FreeDeliveryInfo(m_stNit, MAPI_TRUE);
    }
    _SetCMG(0, E_CMG_NONE);
    m_bRunning = MAPI_FALSE;
    m_eParserMode = E_DVB_IDLE;
#if (OAD_ENABLE == 1)
    m_bOADONIDMatch = MAPI_TRUE;
#endif
    //if(m_bRunning == MAPI_FALSE)
    {

        MW_SI_PARSER_SCAN("EN_DVB_MONITOR_START\n");

        MW_SI_PARSER_SCAN("EN_DVB_MONITOR_START clear old event OK\n");

        if((m_enMonitorMode == EN_MW_SI_MONITOR_MODE_CI_SPECIFY_PMT) || (m_enMonitorMode == EN_MW_SI_MONITOR_MODE_MHEG5_SPECIFY_PMT))
        {
            m_stNit.u8NetWrokNameLen = 0;
            m_u8CurPatVer = INVALID_PSI_SI_VERSION;
            m_u8CurPmtVer = INVALID_PSI_SI_VERSION;
            m_u8CurSdtVer = INVALID_PSI_SI_VERSION;
            m_u8CurNitVer = INVALID_PSI_SI_VERSION;
            m_bforceNetworkCheck = MAPI_FALSE;
            m_u8CurBatVer = INVALID_PSI_SI_VERSION;
        }
        else
        {
            if(m_pCMDB == NULL)
            {
                ASSERT(0);
                return MAPI_FALSE;
            }
            GET_CUR_PROG(m_pCurProg, m_pCMDB);

            if(m_pCurProg == NULL)
            {
                MW_SI_PARSER_ERROR("current program not exist\n");
                ASSERT(0);
                return MAPI_FALSE;
            }


            m_pCurMux = __GetMux(m_pCurProg, m_pCMDB);
            if(m_pCurMux == NULL)
            {
                MW_SI_PARSER_ERROR("get mux failed\n");
                ASSERT(0);
                return MAPI_FALSE;
            }
            m_pCurNetwork = __GetNetwork(m_pCurMux, m_pCMDB);
            if(m_pCurNetwork == NULL)
            {
                MW_SI_PARSER_ERROR("get network failed\n");
                ASSERT(0);
                return MAPI_FALSE;
            }

#if (DVBS_SYSTEM_ENABLE == 1)
            m_pCurSat = GETSAT(m_pCurMux, m_pCMDB);
#endif

            if(m_stNit.u16NetworkID != __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_NID))
            {
                m_stNit.u8NetWrokNameLen = 0;
            }


            m_u8CurPatVer = __GetVer(m_pCurProg, EN_VER_PAT);
            m_u8CurPmtVer = __GetVer(m_pCurProg, EN_VER_PMT);
            bIsNetworkChg = _IsNetworkChange();
            if((MAPI_TRUE == m_bforceNetworkCheck) && ((E_FRANCE== m_eCountry)
                || (E_NEWZEALAND == m_eCountry)
                || ((MAPI_TRUE == bIsNetworkChg) && (IS_NORDIC_COUNTRY(m_eCountry)))
                || ((MAPI_TRUE == bIsNetworkChg) && (E_THAILAND == m_eCountry))
                || ((MAPI_TRUE == bIsNetworkChg) && (E_MALAYSIA == m_eCountry))))
            {
                m_u8CurSdtVer = INVALID_PSI_SI_VERSION;
                m_u8CurNitVer = INVALID_PSI_SI_VERSION;
                m_bforceNetworkCheck = MAPI_FALSE;
            }
            else
            {
                m_u8CurSdtVer = __GetVer(m_pCurProg, EN_VER_SDT);
                m_u8CurNitVer = __GetVer(m_pCurProg, EN_VER_NIT);
            }

            if(MAPI_TRUE == m_bEnableBouquetFilter)
            {
                m_u8CurBatVer = __GetVer(m_pCurProg, EN_VER_BAT);
            }

#if (OAD_ENABLE == 1)
            if(_ShouldCheckOnidForOAD())
            {
                if(!__IsONIDMatchCountry(__GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_ONID), m_eCountry))
                {
                    m_bOADONIDMatch = MAPI_FALSE;
                }
            }
#endif
#if ((OAD_ENABLE == 1) && (SDTT_OAD_ENABLE==1))
            m_SDTT_PID = INVALID_PID;
#endif
        }
        m_u32SdtMonitorTimer = 0;
        m_u32SdtOtherMonitorTimer = 0;
        m_u32PatMonitorTimer = 0;
        m_u32PmtMonitorTimer = 0;
        m_u32PmtOtherMonitorTimer = 0;
        m_u32NitMonitorTimer = 0;
        m_u32TdtMonitorTimer = 0;
        m_u32TotMonitorTimer = 0;
        m_u32OtherPmtMonitorTimer = 0;
        m_u32SdtOtherMonitorTimer = 0;
        m_u32TotMonitorTimer = 0;
        m_u32TdtMonitorTimer = 0;
        m_u32EitPfAllMonitorTimer = 0;
        m_u32EitPfMonitorTimer = 0;
        m_u8CurRatingChkValue = 0;
        m_u8CurContentChkValue = 0;
        m_u32EitSch1MonitorTimer = 0;
        m_u32EitSch2MonitorTimer = 0;
        m_u32AitMonitorTimer = 0;
        m_u32RctMonitorTimer = 0;
        m_u32SgtMonitorTimer = 0;
        m_u32SpecialService_MonitorTimer = 0;
#if ((OAD_ENABLE == 1) && (SDTT_OAD_ENABLE==1))
        m_u32SdttMonitorTimer = 0;
#endif
        MAPI_U8 u8ResetFlag = RESET_INFO | RESET_EIT;

        if(m_enMonitorMode == EN_MW_SI_MONITOR_MODE_CI_SPECIFY_PMT)
        {
            u8ResetFlag |= RESET_SDT_OTHER;
        }
        else
        {
            if((m_stSdtOtherInfo.bValid == MAPI_FALSE) || (m_stSdtOtherInfo.u16NID != __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_NID)))
            {
                u8ResetFlag |= RESET_SDT_OTHER;
            }
        }
        m_bGotBatInScan = MAPI_FALSE;
        _ResetInfo(u8ResetFlag);
        _ResetStatus();
        if(m_enMonitorMode == EN_MW_SI_MONITOR_MODE_NORMAL)
        {
            _BuildSdtOtherInfo(m_pCurProg, m_pCMDB, m_pCurMux, m_pCurNetwork);
        }
        m_eParserMode = E_DVB_MONITOR;
        m_bRunning = MAPI_TRUE;
    }


    return MAPI_TRUE;
}
#if (OAD_ENABLE == 1)
MAPI_BOOL MW_DVB_SI_PSI_Parser::_OadScanStart(void)
{
    m_bOADONIDMatch = MAPI_FALSE;
    m_u16NewAddChnNum = 0;
    if(m_bRunning)
    {
        _StopAllFilter(MAPI_FALSE);
    }
    m_bRunning = MAPI_FALSE;
    m_eParserMode = E_DVB_IDLE;
#if 0
    if(m_pCMDB == NULL)
    {
        ASSERT(0);
        return MAPI_FALSE;
    }
    GET_CUR_PROG(m_pCurProg, m_pCMDB);

    if(m_pCurProg == NULL)
    {
        MW_SI_PARSER_ERROR("current program not exist\n");
        ASSERT(0);
        return MAPI_FALSE;
    }
#endif
    m_u8CurPatVer = INVALID_PSI_SI_VERSION;
    m_u8CurPmtVer = INVALID_PSI_SI_VERSION;
    m_u8CurSdtVer = INVALID_PSI_SI_VERSION;
    m_u32PatMonitorTimer = 0;
    m_u32PmtMonitorTimer = 0;
    m_u32PmtOtherMonitorTimer = 0;
    m_u32TdtMonitorTimer = 0;
    m_u32TotMonitorTimer = 0;
    m_u32OtherPmtMonitorTimer = 0;
    _ResetStatus();
    m_eParserMode = E_DVB_OAD_SCAN;
    m_bRunning = MAPI_TRUE;
    m_u32SpecialService_MonitorTimer = 0;

    return MAPI_TRUE;
}
#endif
void MW_DVB_SI_PSI_Parser::_ScanError(MW_DVB_SCAN_RESULT eResult)
{
    _StopAllFilter(MAPI_TRUE);
    if(_pfScanResultNotify)
    {
        _pfScanResultNotify(eResult, m_u32ScanResultNotifyParam1, NULL, NULL);
    }
}
//for Filein SI parsing
void MW_DVB_SI_PSI_Parser::_FileinPatReady(DVB_PROG *pCurProg, DVB_CM *pCMDB, DVB_MUX *pMux, DVB_NETWORK *pNetwork)
{
    if(NULL == m_pFileinPatParser)
    {
        return;
    }
    //get PAT table information
    if (MAPI_FALSE ==m_pFileinPatParser->GetTable(m_stFileinPat))
    {
        _DeleteParser((MAPI_U32)m_pFileinPatParser, MAPI_FALSE);
    }
    return;
    //<


}
//<


void MW_DVB_SI_PSI_Parser::_PatReady(DVB_PROG *pCurProg, DVB_CM *pCMDB, DVB_MUX *pMux, DVB_NETWORK *pNetwork)
{
    DVB_PROG* pProg;
    MAPI_BOOL bNewService;
    MAPI_BOOL bCurSrvChg = MAPI_FALSE;
    if (MAPI_FALSE == m_pPatParser->GetTable(m_stPat))
    {
        _DeleteParser((MAPI_U32)m_pPatParser, MAPI_FALSE);
        return;
    }
    MW_SI_PARSER_MESSAGE("get PAT ver %x\n", m_stPat.u8Version);
    _DeleteParser((MAPI_U32)m_pPatParser, MAPI_FALSE);
    if(E_DVB_MONITOR == m_eParserMode)
    {
        if((m_enMonitorMode == EN_MW_SI_MONITOR_MODE_CI_SPECIFY_PMT) || (m_enMonitorMode == EN_MW_SI_MONITOR_MODE_MHEG5_SPECIFY_PMT))
        {
            for(MAPI_U16 i=0;i<m_stPat.u16ServiceCount;i++)
            {
                if((MAPI_U16)m_u32MonitorUserData == m_stPat.ServiceIDInfo[i].u16ServiceID)
                {
                    ST_TRIPLE_ID tripleID;
                    memset(&tripleID, 0, sizeof(ST_TRIPLE_ID));
                    m_u32MonitorUserData &=~(0xFFFF<<16);
                    m_u32MonitorUserData |= (m_stPat.ServiceIDInfo[i].u16PmtPID<<16);
                    MW_SI_PARSER_MESSAGE("PAT ready:CI Specify m_u32MonitorUserData=0x%x\n",m_u32MonitorUserData);
                    //notify PMT needs to use current TsId
                    pMux->u16TransportStream_ID = m_stPat.u16TsId;
                    tripleID.u16OnId = __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_ONID);/// use previous channel for ONID
                    tripleID.u16TsId = m_stPat.u16TsId;
                    tripleID.u16SrvId = m_stPat.ServiceIDInfo[i].u16ServiceID;
                    MONITOR_NOTIFY(E_DVB_CI_HC_TUNE_SERVICE_UPDATE_TRIPLE_ID, &tripleID, m_pMonitorNotifyUsrParam, NULL);
                    return;
                }
            }
        }
        if(m_bIsSIDynmaicReScanOff==MAPI_TRUE)
        {
            m_u16NewAddChnNum =0;
        }
        else
        {
            if((m_eParserBaseType == MW_DVB_PAT_BASE) && ((m_u8CurPatVer != m_stPat.u8Version)
                    || (_IsPatCRCChange() == MAPI_TRUE)))
            {
                MW_SI_PARSER_UPDATE("PAT change ver %x=>%x tsid %x=>%x m_stPat.u16ServiceCount %d\n",
                                    m_u8CurPatVer, m_stPat.u8Version, __GetID(pCurProg, pMux, pNetwork,EN_ID_TSID) , m_stPat.u16TsId, m_stPat.u16ServiceCount);
                m_u8CurPatVer = m_stPat.u8Version;
                m_u16NewAddChnNum = 0;
                _ResetInfo(RESET_INFO);
                m_vPatCRC32.clear();
                for (CRC32_t::iterator it = m_stPat.vPatCRC.begin(); it != m_stPat.vPatCRC.end(); it++)
                {
                    m_vPatCRC32.push_back((*it));
                }
                if(m_stPat.u16ServiceCount == 0)
                {
                    return;
                }

                if(__GetID(pCurProg, pMux, pNetwork, EN_ID_TSID) != m_stPat.u16TsId)
                {
                    for(int i = 0; i < MAX_CHANNEL_IN_MUX; i++)
                    {
                        if(m_stPat.ServiceIDInfo[i].u16ServiceID)
                        {
                            m_au16NewProg2Add[m_u16NewAddChnNum] = m_stPat.ServiceIDInfo[i].u16ServiceID;
                            m_u16NewAddChnNum++;
                        }
                    }
                }
                else
                {
                    MW_DTV_CM_DB_scope_lock lock(pCMDB);
                    for(int i = 0; i < MAX_CHANNEL_IN_MUX; i++)
                    {
                        bNewService = MAPI_TRUE;
                        if(m_stPat.ServiceIDInfo[i].u16ServiceID)
                        {
                            pProg = pCMDB->GetByIndex(0);
                            while(pProg)
                            {
                                if(__GetID(pProg, pMux, pNetwork, EN_ID_TSID) == m_stPat.u16TsId)
                                {
                                    if(m_stPat.ServiceIDInfo[i].u16ServiceID == __GetID(pProg, pMux, pNetwork, EN_ID_SID))
                                    {
                                        bNewService = MAPI_FALSE;
                                        break;
                                    }
                                }
                                pProg = pCMDB->GetNext(pProg);
                            }
                            if(bNewService)
                            {
                                m_au16NewProg2Add[m_u16NewAddChnNum] = m_stPat.ServiceIDInfo[i].u16ServiceID;
                                m_u16NewAddChnNum++;
                            }
                        }
                    }
                }
                if(m_u16NewAddChnNum == 0)
                {
                    MAPI_BOOL bUdate = MAPI_FALSE;
                    MAPI_U16 onid,tsid,sid;
                    MW_DTV_CM_DB_scope_lock lock(pCMDB);
                    onid=__GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_ONID);
                    tsid=__GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_TSID);
                    sid=__GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_SID);
                    if(_RemoveMismatchCH(pCurProg, pCMDB, m_stPat.ServiceIDInfo, pMux, pNetwork))
                    {
                        pCMDB->ReArrangeNumber();
                        _UpdateCurrentProgram();
                        ASSERT(m_pCurProg);
                        if((sid != __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_SID)) ||
                            (tsid != __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_TSID)) ||
                            (onid != __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_ONID)))
                        {
                            bCurSrvChg = MAPI_TRUE;
                        }
                        //m_pCurProg = pCMDB->GetCurr();
                        //ASSERT(m_pCurProg);
                        bUdate = MAPI_TRUE;
                    }
                    if(!bCurSrvChg)
                    {
                        __SetVer(m_pCurProg, EN_VER_PAT, m_u8CurPatVer);
                        pCMDB->Update(pCurProg);
                    }
                    if(bUdate && _pfMonitorNotify)
                    {
                        _pfMonitorNotify(E_DVB_TS_CHANGE, &bCurSrvChg, m_pMonitorNotifyUsrParam, NULL);
                    }

                }
                else
                {
                    m_stSdt.u8Version = m_u8CurSdtVer = INVALID_PSI_SI_VERSION;
                    m_stNit.u8Version = m_u8CurNitVer = INVALID_PSI_SI_VERSION;
                    m_u32SdtMonitorTimer = m_u32NitMonitorTimer = 0;
                    if (m_bEnableBouquetFilter)
                    {
                        m_stBat.u8Version = INVALID_PSI_SI_VERSION;
                        m_u32BatMonitorTimer = 0;
                    }
                    m_u32StartUpdateTime = _GetTime0();
                }
            }
            else if((m_u8CurPatVer != m_stPat.u8Version) || (_IsPatCRCChange() == MAPI_TRUE))
            {
                m_u8CurPatVer = m_stPat.u8Version;
                m_vPatCRC32.clear();
                for (CRC32_t::iterator it = m_stPat.vPatCRC.begin(); it != m_stPat.vPatCRC.end(); it++)
                {
                    m_vPatCRC32.push_back((*it));
                }
                __SetVer(pCurProg, EN_VER_PAT, m_u8CurPatVer);
                pCMDB->Update(pCurProg);
            }
        }
        for(int j = 0; j < MAX_CHANNEL_IN_MUX; j++)
        {
            if(m_stPat.ServiceIDInfo[j].u16ServiceID ==  __GetID(pCurProg, pMux, pNetwork, EN_ID_SID))
            {
                if(__GetID(pCurProg, pMux, pNetwork, EN_ID_PMT) != m_stPat.ServiceIDInfo[j].u16PmtPID)
                {
                    __SetID(pCurProg, pMux, pNetwork, EN_ID_PMT, m_stPat.ServiceIDInfo[j].u16PmtPID);
                    pCMDB->Update(pCurProg);
                    m_u32PmtMonitorTimer = 0;
                    if(m_pCurPmtParser)
                    {
                        _DeleteParser((MAPI_U32)m_pCurPmtParser, MAPI_FALSE);
                    }
                }
            }
        }

        if(m_u16NewAddChnNum == 0)
        {
            return;
        }
    }
    for(int j = 0; j < MAX_CHANNEL_IN_MUX; j++)
    {
        if(m_pAllPmtParser[j])
        {
            _DeleteParser((MAPI_U32)m_pAllPmtParser[j] , MAPI_FALSE);
            m_aPMTWaitFilter[j] = MAPI_FALSE;
        }
    }

    //printf("MS_SI_Scan_Task --> TSID = %u\n", m_stPat.u16TsId);
    for(int j = 0; j < MAX_CHANNEL_IN_MUX; j++)
    {
        if(m_stPat.ServiceIDInfo[j].u16ServiceID)
        {
            if(E_DVB_MONITOR == m_eParserMode)
            {
                MAPI_BOOL bNewProgram = MAPI_FALSE;
                for(int i = 0; i < m_u16NewAddChnNum; i++)
                {
                    if(m_stPat.ServiceIDInfo[j].u16ServiceID == m_au16NewProg2Add[i])
                    {
                        bNewProgram = MAPI_TRUE;
                        break;
                    }
                }
                if(bNewProgram == MAPI_FALSE)
                {
                    continue;
                }
            }
            m_pAllPmtParser[j] = new (std::nothrow) mapi_si_PMT_parser(m_pSi, m_pDemux);
            if(m_pAllPmtParser[j])
            {
                if(m_pAllPmtParser[j]->Init(0x400, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo, EN_SI_PSI_PARSER_NORMAL, INVALID_PSI_SI_VERSION)
                        && m_pAllPmtParser[j]->Start(PMT_SCAN_TIMEOUT, m_stPat.ServiceIDInfo[j].u16ServiceID, m_stPat.ServiceIDInfo[j].u16PmtPID))
                {
                    m_s16OpenFilter++;

                }
                else
                {
                    delete m_pAllPmtParser[j];
                    m_pAllPmtParser[j] = NULL;
                    m_aPMTWaitFilter[j] = MAPI_TRUE;
                }
            }
            else
            {
                m_aPMTWaitFilter[j] = MAPI_TRUE;
            }

        }
    }
    //printf("s16OpenFilter %d\n",m_s16OpenFilter);
}

void MW_DVB_SI_PSI_Parser::_PmtReady(DVB_PROG *pCurProg, DVB_CM *pCMDB, DVB_MUX *pMux, DVB_NETWORK *pNetwork, MAPI_U32 u32Address)
{

    //for Filein SI parsing
    for(int j = 0; j < m_stFileinPat.u16ServiceCount; j++)
    {
        if((MAPI_U32)m_pFileinAllPmtParser[j] == u32Address)
        {
            //printf("___Filein _PmtReady ");
            if(FALSE == m_pFileinAllPmtParser[j]->GetTable(m_stFileinCurPmt))
            {
                for(int k = 0; k < m_stFileinPat.u16ServiceCount; k++)
                {
                    if(m_pFileinAllPmtParser[k])
                    {
                        _DeleteParser((MAPI_U32)m_pFileinAllPmtParser[k], MAPI_FALSE);
                    }
                }
                return;
            }
            m_FileinCurServiceInfo.wPmtPID = m_stFileinPat.ServiceIDInfo[j].u16PmtPID;
            _UpdateServiceInfo(MW_SERVICE_PMT_VER_CHANGE_AV_N_CHANGE,m_stFileinCurPmt,m_FileinCurServiceInfo);
            MONITOR_NOTIFY(E_DVB_FILEIN_PMT_RECIEVE, &m_FileinCurServiceInfo, m_pMonitorNotifyUsrParam, NULL);

            for(int k = 0; k < m_stFileinPat.u16ServiceCount; k++)
            {
                //Dynamic PMT
                if(m_pFileinAllPmtParser[k])
                    _DeleteParser((MAPI_U32)m_pFileinAllPmtParser[k], MAPI_FALSE);
            }

            m_stFileinPat.u16ServiceCount = 1;
            m_stFileinPat.ServiceIDInfo[0].u16ServiceID = m_stFileinPat.ServiceIDInfo[j].u16ServiceID;
            m_stFileinPat.ServiceIDInfo[0].u16PmtPID =  m_stFileinPat.ServiceIDInfo[j].u16PmtPID;

            //printf(" ___Filein _PmtReady  u16ServiceID [0x%X]\n",m_stFileinPat.ServiceIDInfo[j].u16ServiceID);
            //printf(" ___Filein _PmtReady  u16PmtPID [0x%X]\n",m_stFileinPat.ServiceIDInfo[j].u16PmtPID);
            //printf(" ___Filein _PmtReady  V_PID [0x%X]\n",m_stFileinCurPmt.sVideoInfo[0].wVideoPID);

            return;
        }
    }
    //<

    //for special service pmt
    for(int j = 0; j < MAX_SPECIAL_SERVICE_NUM; j++)
    {
        if((MAPI_U32)m_pSpecialServicesParser[j] == u32Address)
        {
            MAPI_SI_TABLE_PMT stSpecialPmt;
            DVB_PROG *pProg = NULL;

            if(FALSE == m_pSpecialServicesParser[j]->GetTable(stSpecialPmt))
            {
                _DeleteParser((MAPI_U32)m_pSpecialServicesParser[j], MAPI_FALSE);
                return;
            }
            _DeleteParser((MAPI_U32)m_pSpecialServicesParser[j], MAPI_FALSE);
            if((stSpecialPmt.wServiceID == m_astSpecialService[j].u16SrvId) &&
                (m_stPat.u16TsId == m_astSpecialService[j].u16TsId))
            {
                MW_DTV_CM_DB_scope_lock lock(pCMDB);
                pProg = pCMDB->GetByID(m_astSpecialService[j].u16TsId, m_astSpecialService[j].u16OnId, m_astSpecialService[j].u16SrvId);
                if(pProg)
                {
                    _UpdateSpecialServicePmtInfo(pProg, pCMDB, pMux, stSpecialPmt);
                }
            }
            return;
        }
    }

    if(m_pOtherPmtParser && ((MAPI_U32)m_pOtherPmtParser == u32Address))
    {
        MAPI_SI_TABLE_PMT stOtherPmt;
        MAPI_U16 wPmtPID = INVALID_PID;
        MW_DVB_SI_ServicelInfo stServiceInfo;
        MAPI_U16 wTSID, wONID;
        DVB_PROG *pProg = NULL;
        MAPI_U8 u8Version;

        if (MAPI_FALSE == m_pOtherPmtParser->GetTable(stOtherPmt))
        {
            _DeleteParser((MAPI_U32)m_pOtherPmtParser, MAPI_FALSE);
            return;
        }
        _DeleteParser((MAPI_U32)m_pOtherPmtParser, MAPI_FALSE);
        wTSID = __GetID(pCurProg, pMux, pNetwork, EN_ID_TSID);
        wONID = __GetID(pCurProg, pMux, pNetwork, EN_ID_ONID);
        MW_DTV_CM_DB_scope_lock lock(pCMDB);
        pProg = pCMDB->GetByID(wTSID, wONID, stOtherPmt.wServiceID);
        if(pProg == NULL)
        {
            return;
        }
        u8Version = __GetVer(pProg, EN_VER_PMT);
        if(stOtherPmt.u8Version != u8Version)
        {
            __SetVer(pProg, EN_VER_PMT, stOtherPmt.u8Version);
            for(int j = 0; j < m_stPat.u16ServiceCount; j++)
            {
                if((m_stPat.ServiceIDInfo[j].u16ServiceID != INVALID_SERVICE_ID)&&
                    (stOtherPmt.wServiceID == m_stPat.ServiceIDInfo[j].u16ServiceID))
                {
                    wPmtPID = m_stPat.ServiceIDInfo[j].u16PmtPID;
                    break;
                }
            }
            memset(&stServiceInfo, 0, sizeof(MW_DVB_SI_ServicelInfo));
            stServiceInfo.wPmtPID = wPmtPID;
            _UpdateServiceInfo(MW_SERVICE_PMT_CHANGED, stOtherPmt, stServiceInfo);
            if(!__UpdateService(pProg, &stServiceInfo, pCMDB, MAPI_TRUE))
            {
                MW_SI_PARSER_UPDATE("other service update program fail\n");
            }
        }
    }
    else if(m_pCurPmtParser && ((MAPI_U32)m_pCurPmtParser == u32Address))
    {
        MAPI_U32 u32OldCRC32 = m_stCurPmt.u32CRC32;
        MAPI_U8 u8OldVer = m_stCurPmt.u8Version;
        MAPI_SI_TABLE_PMT m_stBackupPmt;
#if (HBBTV_ENABLE)
        MAPI_BOOL bGotHBBTVAPP = MAPI_FALSE, bGotNoneHBBTVAPP = MAPI_FALSE;
#endif
#if (GINGA_ENABLE == 1)
        MAPI_BOOL bGotGingaNclApp=MAPI_FALSE, bGotGingaJApp=MAPI_FALSE, bGotGingaUnknownApp=MAPI_FALSE, bGotNoneGingaApp=MAPI_FALSE;
#endif
        MAPI_BOOL bIsChanged;
        m_stBackupPmt = m_stCurPmt;
        if (MAPI_FALSE == m_pCurPmtParser->GetTable(m_stCurPmt))
        {
            _DeleteParser((MAPI_U32)m_pCurPmtParser, MAPI_FALSE);
            return;
        }
        _DeleteParser((MAPI_U32)m_pCurPmtParser, MAPI_FALSE);

        if(m_enMonitorMode == EN_MW_SI_MONITOR_MODE_CI_SPECIFY_PMT)
        {
            MW_SI_PARSER_MESSAGE("Not run _PmtReady in m_enMonitorMode=EN_MW_SI_MONITOR_MODE_CI_SPECIFY_PMT\n");
            return;
        }
        if(m_enMonitorMode == EN_MW_SI_MONITOR_MODE_MHEG5_SPECIFY_PMT)
        {
            memcpy(&m_SpecifyMheg5Service,&m_stCurPmt, sizeof(MAPI_SI_TABLE_PMT));
            _pfMonitorNotify(E_DVB_MHEG5_SPECIFY_PMT, NULL, m_pMonitorNotifyUsrParam, NULL);
            return;
        }
        // fix the version is diff but content is the same for freeview SD si02
        if (MAPI_FALSE == _IsPmtContentDiff(m_stBackupPmt, m_stCurPmt))
        {
            return;
        }
        // END - fix the version is diff but content is the same for freeview SD si02

#if (HBBTV_ENABLE)
        if((m_u8CurPmtVer != m_stCurPmt.u8Version) || (u32OldCRC32 != m_stCurPmt.u32CRC32))
        {
            if(m_stCurPmt.stAppSignalInfo.u16AitPID != INVALID_PID)
            {
                for(int i = 0; i < MAPI_SI_MAX_APP_SIGNALLING_NUM; i++)
                {
                    if(m_stCurPmt.stAppSignalInfo.astAppSignalling[i].u16AppType == HBBTV_APP_TYPE)
                    {
                        bGotHBBTVAPP = MAPI_TRUE;
                        break;

                    }
                    else if(m_stCurPmt.stAppSignalInfo.astAppSignalling[i].u16AppType != MAPI_INVALID_APPLICATION_TYPE)
                    {
                        bGotNoneHBBTVAPP = MAPI_TRUE;
                    }
                }
                if((bGotHBBTVAPP == MAPI_FALSE) && bGotNoneHBBTVAPP)
                {
                    m_stCurPmt.stAppSignalInfo.u16AitPID = INVALID_PID;
                }
            }
            if(m_stCurPmt.stAppSignalInfo.u16AitPID == INVALID_PID)
            {
                {
                    m_stAit.m_u16AppType = 0xFFFF;
                    m_stAit.m_u8AppNum = 0;

                    U16 u16SID=__GetID(pCurProg,pMux,pNetwork,EN_ID_SID);
                    MONITOR_NOTIFY(E_DVB_AIT_SIGNAL, static_cast<mapi_ait_apps*>(&m_stAit), m_pMonitorNotifyUsrParam,(void*)&u16SID);

                    //m_pcHBBTV->AITSignal(__GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_SID), m_stAit);
                }
            }
        }
#elif (GINGA_ENABLE == 1)
        if((m_u8CurPmtVer != m_stCurPmt.u8Version) || (u32OldCRC32 != m_stCurPmt.u32CRC32))
        {
            if(m_stCurPmt.stAppSignalInfo.u16AitPID != INVALID_PID)
            {
                for(int i=0;i<MAPI_SI_MAX_APP_SIGNALLING_NUM;i++)
                {
                    if(m_stCurPmt.stAppSignalInfo.astAppSignalling[i].u16AppType == APP_TYPE_GINGA_NCL)
                    {
                        bGotGingaNclApp=MAPI_TRUE;
                        break;
                    }
                    else if(m_stCurPmt.stAppSignalInfo.astAppSignalling[i].u16AppType == APP_TYPE_GINGA_J)
                    {
                        bGotGingaJApp=MAPI_TRUE;
                        break;
                    }
                    else if(m_stCurPmt.stAppSignalInfo.astAppSignalling[i].u16AppType == APP_TYPE_GINGA_UNKNOWN)
                    {
                        bGotGingaUnknownApp=MAPI_TRUE;
                        break;
                    }
                    else if(m_stCurPmt.stAppSignalInfo.astAppSignalling[i].u16AppType != MAPI_INVALID_APPLICATION_TYPE)
                    {
                        bGotNoneGingaApp=MAPI_TRUE;
                    }
                }
                if((bGotGingaNclApp==MAPI_FALSE) && (bGotGingaJApp==MAPI_FALSE) && (bGotGingaUnknownApp==MAPI_FALSE) && (bGotNoneGingaApp == MAPI_TRUE))
                {
                    m_stCurPmt.stAppSignalInfo.u16AitPID = INVALID_PID;
                }
            }
            if(m_stCurPmt.stAppSignalInfo.u16AitPID == INVALID_PID)
            {
                {
                    m_stAit.m_u16AppType=0xFFFF;
                    m_stAit.m_u8AppNum=0;
                    U16 u16SID=__GetID(pCurProg,pMux,pNetwork,EN_ID_SID);
                    MONITOR_NOTIFY(E_DVB_AIT_SIGNAL, static_cast<mapi_ait_apps*>(&m_stAit), m_pMonitorNotifyUsrParam,(void*)&u16SID);
                    //m_pcGinga->AITSignal(__GetID(m_pCurProg,m_pCurMux,m_pCurNetwork,EN_ID_SID),m_stAit);
                }
            }
        }
#endif

        if(m_enMonitorMode == EN_MW_SI_MONITOR_MODE_AIT_ONLY)
        {
            return;
        }

        m_u16OldSID = INVALID_SERVICE_ID;
        m_u16OldPMTPID = INVALID_PMT_PID;
        m_u32WaitPMTTimer = INVALID_TIME;
        MW_SI_PARSER_MESSAGE("get PMT ver (%x) %x crc %x %x\n", m_u8CurPmtVer, m_stCurPmt.u8Version, u32OldCRC32, m_stCurPmt.u32CRC32);
        m_CurServiceInfo.wPmtPID = __GetID(pCurProg,pMux,pNetwork,EN_ID_PMT);
        _UpdateServiceInfo(((m_u8CurPmtVer != m_stCurPmt.u8Version) || (u32OldCRC32 != m_stCurPmt.u32CRC32)) ? MW_SERVICE_PMT_CHANGED : MW_SERVICE_NO_CHANGE,m_stCurPmt,m_CurServiceInfo);

        if((m_u8CurPmtVer != m_stCurPmt.u8Version) || (u32OldCRC32 != m_stCurPmt.u32CRC32))
        {
            if(m_u8CurPmtVer != m_stCurPmt.u8Version)
            {
                MW_SI_PARSER_UPDATE("PMT change %x => %x\n", m_u8CurPmtVer, m_stCurPmt.u8Version);
                m_u8CurPmtVer = m_stCurPmt.u8Version;
                __SetVer(m_pCurProg, EN_VER_PMT, m_u8CurPmtVer);
            }
#if (ISDB_SYSTEM_ENABLE == 1)

            MAPI_U8  u8LoopIdx = 0,u8PmtParentalControlBackup=0,u8TempRate = 0;\

            if( m_eParserType == MW_ISDB_T_PARSER )
            {
                for(u8LoopIdx = 0; u8LoopIdx < MAPI_SI_MAX_PARENTAL_RATING_NUM; u8LoopIdx++)
                {
                    if( (m_stCurPmt.astParentalRatingInfo[u8LoopIdx].Country_code[0] == 0) &&
                        (m_stCurPmt.astParentalRatingInfo[u8LoopIdx].Country_code[1] == 0) &&
                        (m_stCurPmt.astParentalRatingInfo[u8LoopIdx].Country_code[2] == 0))
                    {
                        MW_SI_PARSER_MESSAGE("Current program has %d country code in PMT Parental Rating\n",u8LoopIdx);
                        break;
                    }
                    u8PmtParentalControlBackup = m_u8PmtParentalControl;
                    u8TempRate = m_stCurPmt.astParentalRatingInfo[u8LoopIdx].Rating;

                    if(u8TempRate > 0x0f)
                    {
                        u8TempRate = 0; //skip broadcast define
                    }
                    if(m_eCountry == mapi_dvb_utility::SI_GetCountryIndex(m_stCurPmt.astParentalRatingInfo[u8LoopIdx].Country_code))
                    {
                        if(u8TempRate <= 0)
                        {
                            break;
                        }

                        m_u8PmtParentalControl = u8TempRate;
                        break;
                    }
                    else
                    {
                        if((m_u8PmtParentalControl == 0) || (m_u8PmtParentalControl > u8TempRate))
                        {
                            m_u8PmtParentalControl = u8TempRate;
                        }
                    }
                }
                if( m_u8PmtParentalControl != INVALID_PARENTAL_RATING )
                {

                    m_u8PmtParentalControl = (m_u8PmtParentalControl & 0x0F);

                    switch(m_u8PmtParentalControl)
                    {
                        case 2:
                            m_u8PmtParentalControl = 10;
                            break;
                        case 3:
                            m_u8PmtParentalControl = 12;
                            break;
                        case 4:
                            m_u8PmtParentalControl = 14;
                            break;
                        case 5:
                            m_u8PmtParentalControl = 16;
                            break;
                        case 6:
                            m_u8PmtParentalControl = 18;
                            break;

                        default:
                            m_u8PmtParentalControl = 0;
                            break;
                    }

                    if( (E_DVB_MONITOR == m_eParserMode) &&
                       (u8PmtParentalControlBackup != m_u8PmtParentalControl) )
                    {
                        MONITOR_NOTIFY(E_DVB_PARENTAL_RATING, NULL, m_pMonitorNotifyUsrParam, NULL);
                    }

                }

            }


#endif
            if(m_CurServiceInfo.bIsCAExist != pCurProg->stCHAttribute.u8IsScramble)
            {
                bIsChanged = MAPI_TRUE;
            }

            if((!__UpdateService(pCurProg, &m_CurServiceInfo, pCMDB, MAPI_TRUE)) && (m_CurServiceInfo.eChangeType == MW_SERVICE_PMT_CHANGED))
            {
                if(u8OldVer != SI_PSI_FORCE_UPDATE_VER)
                {
                    m_CurServiceInfo.eChangeType = MW_SERVICE_PMT_VER_CHANGE_AV_N_CHANGE;
                }
            }

#if (ISDB_SYSTEM_ENABLE == 1)

            if((m_eParserType == MW_ISDB_T_PARSER) && (INVALID_PSI_SI_VERSION != m_EitPfInfo[0].version_number))
            {
                BOOL bUpdate=FALSE;
                for(MAPI_U8 i=0;i<m_stCurPmt.u8AudioNumber;i++)
                {
                    MW_SI_PARSER_MESSAGE("m_EitPfInfo[0].u8AudioTag %d\n",m_EitPfInfo[0].u8AudioTag);
                    MW_SI_PARSER_MESSAGE("m_CurServiceInfo.au8AudioComponentTag[%d] %d\n",i,m_CurServiceInfo.au8AudioComponentTag[i]);
                    MW_SI_PARSER_MESSAGE("m_CurServiceInfo.stAudInfo[%d].aISOLangInfo.u8IsValid %d\n",i,m_CurServiceInfo.stAudInfo[i].aISOLangInfo.u8IsValid);
                    for(int j=0;j<m_EitPfInfo[0].u8AudioNumber;j++)
                    {
                        if( (m_CurServiceInfo.au8AudioComponentTag[i] == m_EitPfInfo[0].au8AudioTag[j] )&&
                            (m_CurServiceInfo.stAudInfo[i].aISOLangInfo.u8IsValid != TRUE) )
                        {
                            m_CurServiceInfo.stAudInfo[i].aISOLangInfo.u8IsValid = TRUE;
                            m_CurServiceInfo.stAudInfo[i].aISOLangInfo.u8AudType = m_EitPfInfo[0].astISOLangInfo[j].u8AudType;
                            m_CurServiceInfo.stAudInfo[i].aISOLangInfo.u8AudMode = m_EitPfInfo[0].astISOLangInfo[j].u8AudMode;
                            memcpy(m_CurServiceInfo.stAudInfo[i].aISOLangInfo.u8ISOLangInfo,m_EitPfInfo[0].astISOLangInfo[j].u8ISOLangInfo, 3);
                            bUpdate=TRUE;
                            break;
                        }
                    }
                }
                if(bUpdate)
                {
                    if(__UpdateService(pCurProg, &m_CurServiceInfo, pCMDB, MAPI_FALSE))
                    {
                        MONITOR_NOTIFY(E_DVB_AUDIO_UPDATE, &m_CurServiceInfo, m_pMonitorNotifyUsrParam, NULL);
                    }

                }
            }
#endif


            //printf("%s....current....type %d\n",__FUNCTION__,m_CurServiceInfo.eChangeType);
            m_u32ServiceMoveTimer = INVALID_TIME;
            if(m_CurServiceInfo.eChangeType == MW_SERVICE_MOVE)
            {
                MAPI_BOOL bProgExist = MAPI_FALSE;

                IS_PROG_EXIST(m_stCurPmt.stServiceMove.u16NewTSId, m_stCurPmt.stServiceMove.u16NewONId, m_stCurPmt.stServiceMove.u16NewServiceId, bProgExist);

                if(bProgExist)
                {
                    MONITOR_NOTIFY(E_DVB_PMT_SERVICE_CHANGE, &m_CurServiceInfo, m_pMonitorNotifyUsrParam, NULL);
                    m_CurServiceInfo.eChangeType = MW_SERVICE_NO_CHANGE;
                }
                else
                {
                    U16 index;
                    if(m_stSdtOtherInfo.stSdtOtherTS.size())
                    {
                        for(index = 0; index < m_stSdtOtherInfo.stSdtOtherTS.size(); index++)
                        {
                            if(m_stSdtOtherInfo.stSdtOtherTS.at(index).u16TSID == m_stCurPmt.stServiceMove.u16NewTSId)
                            {
                                break;
                            }
                        }
                        if(index < m_stSdtOtherInfo.stSdtOtherTS.size())
                        {
                            U8 NewIndex = index + 1;
                            if(NewIndex >= m_stSdtOtherInfo.stSdtOtherTS.size()) NewIndex = 0;
                            if(m_pSdtOtherParser != NULL)
                            {
                                if(NewIndex != m_stSdtOtherInfo.u8SdtOtherIndex)//del different other
                                {
                                    _DeleteParser((U32)m_pSdtOtherParser, MAPI_FALSE);
                                }

                            }
                            m_u32SdtOtherMonitorTimer = 0;
                        }
                        m_u32ServiceMoveTimer = _GetTime0();
                        m_u8CurPmtVer = SI_PSI_FORCE_UPDATE_VER;
                        m_stCurPmt.u8Version = SI_PSI_FORCE_UPDATE_VER;


                        MW_SI_PARSER_UPDATE("got service move onid %x tsid %x sid %x\n", m_stCurPmt.stServiceMove.u16NewONId,
                                            m_stCurPmt.stServiceMove.u16NewTSId, m_stCurPmt.stServiceMove.u16NewServiceId);

                    }
                }
            }
            else if((m_CurServiceInfo.eChangeType != MW_SERVICE_NO_CHANGE))
            {
                MAPI_U16 u16SID = __GetID(pCurProg, pMux, pNetwork,EN_ID_SID);

                for(int j = 0; j < MAX_SPECIAL_SERVICE_NUM; j++)
                {
                    if((u16SID == m_astSpecialService[j].u16SrvId) &&
                        (m_stPat.u16TsId == m_astSpecialService[j].u16TsId))
                    {
                        MW_DVB_SI_SpecialService_Info info;

                        memcpy(&info.stServiceInfo, &m_CurServiceInfo, sizeof(MW_DVB_SI_ServicelInfo));
                        info.u16TSID = m_astSpecialService[j].u16TsId;
                        info.u16ONID = m_astSpecialService[j].u16OnId;
                        info.u16SID = m_astSpecialService[j].u16SrvId;
                        MONITOR_NOTIFY(E_DVB_SPECIAL_SERVICE_UPDATE, &info, m_pMonitorNotifyUsrParam, &bIsChanged);
                        break;
                    }
                }
                if(u8OldVer == SI_PSI_FORCE_UPDATE_VER)
                {
                    MONITOR_NOTIFY(E_DVB_PMT_SERVICE_CHANGE, &m_CurServiceInfo, m_pMonitorNotifyUsrParam, NULL);
                }
                else
                {
                    MONITOR_NOTIFY((m_u32CurPMTCRC32 == m_stCurPmt.u32CRC32) ? E_DVB_PMT_CHANGE : E_DVB_PMT_REALLY_CHANGE, &m_CurServiceInfo, m_pMonitorNotifyUsrParam, &bIsChanged);
                }
                m_u32CurPMTCRC32=m_stCurPmt.u32CRC32;
                m_CurServiceInfo.eChangeType = MW_SERVICE_NO_CHANGE;
            }
        }
        // for retrieve EIT PF
        m_bForceRetrievePfEIT = MAPI_TRUE;
        return;
    }


    for(int j = 0; j < MAX_CHANNEL_IN_MUX; j++)
    {
        if(m_pAllPmtParser[j] && ((MAPI_U32)m_pAllPmtParser[j] == u32Address) && m_pAllPmtParser[j]->GetTable(m_astAllPmt[j]))
        {
            _DeleteParser((MAPI_U32)m_pAllPmtParser[j] , MAPI_FALSE);
            m_aPMTWaitFilter[j] = MAPI_FALSE;
            //printf("pmt sid %x\n",m_astPmt[j].wServiceID);
            break;
        }
    }
    for(int j = 0; j < MAX_CHANNEL_IN_MUX; j++)
    {
        if((m_aPMTWaitFilter[j] == MAPI_TRUE) && (m_pAllPmtParser[j] == NULL))
        {
            m_pAllPmtParser[j] = new (std::nothrow) mapi_si_PMT_parser(m_pSi, m_pDemux);
            if(m_pAllPmtParser[j])
            {
                if(m_pAllPmtParser[j]->Init(0x400, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_NORMAL, INVALID_PSI_SI_VERSION)
                        && m_pAllPmtParser[j]->Start(PMT_SCAN_TIMEOUT, m_stPat.ServiceIDInfo[j].u16ServiceID, m_stPat.ServiceIDInfo[j].u16PmtPID))
                {

                    m_s16OpenFilter++;

                    m_aPMTWaitFilter[j] = MAPI_FALSE;
                }
                else
                {
                    delete m_pAllPmtParser[j];
                    m_pAllPmtParser[j] = NULL;
                }

            }
            else
            {
                break;
            }
        }
    }
    MW_SI_PARSER_MESSAGE("s16OpenFilter %d\n", m_s16OpenFilter);
}

#if (OAD_ENABLE == 1)
void MW_DVB_SI_PSI_Parser::_PatReady_OAD()
{
    if (MAPI_FALSE == m_pPatParser->GetTable(m_stPat))
    {
        _DeleteParser((MAPI_U32)m_pPatParser, MAPI_FALSE);
        return;
    }
    MW_SI_PARSER_MESSAGE("get PAT ver %x\n", m_stPat.u8Version);
    _DeleteParser((MAPI_U32)m_pPatParser, MAPI_FALSE);

    if(m_u8CurPatVer != m_stPat.u8Version)
    {
        m_u8CurPatVer = m_stPat.u8Version;
    }

    for(int j = 0; j < MAX_CHANNEL_IN_MUX; j++)
    {
        if(m_pAllPmtParser[j])
        {
            _DeleteParser((MAPI_U32)m_pAllPmtParser[j] , MAPI_FALSE);
            m_aPMTWaitFilter[j] = MAPI_FALSE;
        }
    }


    for(int j = 0; j < MAX_CHANNEL_IN_MUX; j++)
    {
        if(m_stPat.ServiceIDInfo[j].u16ServiceID)
        {
            m_pAllPmtParser[j] = new (std::nothrow) mapi_si_PMT_parser(m_pSi, m_pDemux);
            if(m_pAllPmtParser[j])
            {
                if(m_pAllPmtParser[j]->Init(0x400, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo, EN_SI_PSI_PARSER_NORMAL, INVALID_PSI_SI_VERSION)
                        && m_pAllPmtParser[j]->Start(PMT_SCAN_TIMEOUT, m_stPat.ServiceIDInfo[j].u16ServiceID, m_stPat.ServiceIDInfo[j].u16PmtPID))
                {
                    m_s16OpenFilter++;
                }
                else
                {
                    delete m_pAllPmtParser[j];
                    m_pAllPmtParser[j] = NULL;
                    m_aPMTWaitFilter[j] = MAPI_TRUE;
                }
            }
            else
            {
                m_aPMTWaitFilter[j] = MAPI_TRUE;
            }

        }
    }
}

void MW_DVB_SI_PSI_Parser::_PmtReady_OAD(MAPI_U32 u32Address)
{
    if(m_pOtherPmtParser && ((MAPI_U32)m_pOtherPmtParser == u32Address))
    {
        _DeleteParser((MAPI_U32)m_pOtherPmtParser, MAPI_FALSE);
    }

    else if(m_pCurPmtParser && ((MAPI_U32)m_pCurPmtParser == u32Address))
    {
        MAPI_U32 u32OldCRC32 = m_stCurPmt.u32CRC32;
        if (MAPI_FALSE == m_pCurPmtParser->GetTable(m_stCurPmt))
        {
            _DeleteParser((MAPI_U32)m_pCurPmtParser, MAPI_FALSE);
            return;
        }
        _DeleteParser((MAPI_U32)m_pCurPmtParser, MAPI_FALSE);
        m_u32WaitPMTTimer = INVALID_TIME;
        MW_SI_PARSER_MESSAGE("get PMT ver %x crc %x %x\n", m_stCurPmt.u8Version, u32OldCRC32, m_stCurPmt.u32CRC32);
        if((m_u8CurPmtVer != m_stCurPmt.u8Version) || (u32OldCRC32 != m_stCurPmt.u32CRC32))
        {
            if(m_u8CurPmtVer != m_stCurPmt.u8Version)
            {
                m_u8CurPmtVer = m_stCurPmt.u8Version;
            }
        }
        return;
    }
    for(int j = 0; j < MAX_CHANNEL_IN_MUX; j++)
    {
        if(m_pAllPmtParser[j] && ((MAPI_U32)m_pAllPmtParser[j] == u32Address) && m_pAllPmtParser[j]->GetTable(m_astAllPmt[j]))
        {
            _DeleteParser((MAPI_U32)m_pAllPmtParser[j] , MAPI_FALSE);
            m_aPMTWaitFilter[j] = MAPI_FALSE;
            //printf("pmt sid %x\n",m_astPmt[j].wServiceID);
            break;
        }
    }
    for(int j = 0; j < MAX_CHANNEL_IN_MUX; j++)
    {
        if((m_aPMTWaitFilter[j] == MAPI_TRUE) && (m_pAllPmtParser[j] == NULL))
        {
            m_pAllPmtParser[j] = new (std::nothrow) mapi_si_PMT_parser(m_pSi, m_pDemux);
            if(m_pAllPmtParser[j])
            {
                if(m_pAllPmtParser[j]->Init(0x400, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_NORMAL, INVALID_PSI_SI_VERSION)
                        && m_pAllPmtParser[j]->Start(PMT_SCAN_TIMEOUT, m_stPat.ServiceIDInfo[j].u16ServiceID, m_stPat.ServiceIDInfo[j].u16PmtPID))
                {

                    m_s16OpenFilter++;

                    m_aPMTWaitFilter[j] = MAPI_FALSE;
                }
                else
                {
                    delete m_pAllPmtParser[j];
                    m_pAllPmtParser[j] = NULL;
                }

            }
            else
            {
                break;
            }
        }
    }
}
#endif
MAPI_BOOL MW_DVB_SI_PSI_Parser::_IsServiceLCNMatchBAT(MAPI_U16 u16TSID, MAPI_U16 u16ONID, MAPI_U16 u16ServiceID, MAPI_U16 u16LCN)
{
    MAPI_SI_BAT_TS_INFO *pstTsInfo = m_stBat.pstTSInfoList;

    while(pstTsInfo)
    {
        if((pstTsInfo->u16TransportStream_ID == u16TSID) &&
            (pstTsInfo->u16OriginalNetwork_ID == u16ONID))
        {
            for(int j = 0; j < pstTsInfo->u16LCNNumber; j++)
            {
                if(u16ServiceID && (u16ServiceID == pstTsInfo->astLcnInfo[j].u16ServiceID) &&
                    (u16LCN == pstTsInfo->astLcnInfo[j].u16LCNNumber))
                {
                    return MAPI_TRUE;
                }
            }
        }
        pstTsInfo = pstTsInfo->next;
    }

    return MAPI_FALSE;
}

void MW_DVB_SI_PSI_Parser::_BatReady(DVB_PROG *pCurProg, DVB_CM *pCMDB, DVB_MUX *pMux, DVB_NETWORK *pNetwork)
{
    DVB_PROG *pProg = NULL, *pTmpProg = NULL;
    DVB_MUX* pMux_Info;
    MAPI_BOOL bNotify = MAPI_FALSE;
    MAPI_SI_BAT_TS_INFO *pstTsInfo = NULL;
    MAPI_U16 u16ServiceId,u16LCNNumber;
    if (MAPI_FALSE == m_pBatParser->GetTable(m_stBat))
    {
        _DeleteParser((MAPI_U32)m_pBatParser, MAPI_FALSE);
        return;
    }
    MW_SI_PARSER_MESSAGE("get BAT ver %x\n", m_stBat.u8Version);
    _DeleteParser((MAPI_U32)m_pBatParser, MAPI_FALSE);
    MW_SI_PARSER_MESSAGE("s16OpenFilter %d\n", m_s16OpenFilter);
    m_bBatParserTimeOut = MW_SI_DEFAULT_BID_BAT_STATE_OK;
    if(E_DVB_SCAN== m_eParserMode)
    {
        m_bGotBatInScan = MAPI_TRUE;
    }
    else if(E_DVB_MONITOR == m_eParserMode)
    {
        if(m_u8CurBatVer != m_stBat.u8Version)
        {
            MAPI_BOOL bUpdate = MAPI_FALSE;
            MAPI_BOOL bLCNUpdate = MAPI_FALSE;
            MAPI_BOOL bUpdateMovedService = MAPI_FALSE;
            //printf("bat ver change %d=>%d\n", m_u8CurBatVer, m_stBat.u8Version);
            m_u8CurBatVer = m_stBat.u8Version;
            MW_DTV_CM_DB_scope_lock lock(pCMDB);
            __SetVer(pCurProg, EN_VER_BAT, m_u8CurBatVer);
            pCMDB->Update(pCurProg);
            pstTsInfo = m_stBat.pstTSInfoList;
            while(pstTsInfo)
            {
                for(int j = 0; j < pstTsInfo->u16LCNNumber; j++)
                {
                    u16ServiceId = pstTsInfo->astLcnInfo[j].u16ServiceID;
                    u16LCNNumber = pstTsInfo->astLcnInfo[j].u16LCNNumber;
                    pProg = pTmpProg = NULL;
                    for(MAPI_U16 m = 0; m < pCMDB->Size(); m++)
                    {
                        pTmpProg = pCMDB->GetByIndex(m);
                        if(pTmpProg == NULL)
                        {
                            break;
                        }
                        pMux_Info = pCMDB->GetMux(pTmpProg->m_u16MuxTableID);
                        if (pMux_Info!=NULL)
                        {
                            if ((pMux_Info->u16TransportStream_ID == pstTsInfo->u16TransportStream_ID) &&
                                (pMux_Info->u16OriginalNetwork_ID == pstTsInfo->u16OriginalNetwork_ID) &&
                                (pTmpProg->u16ServiceID == u16ServiceId))
                            {
                                if(pTmpProg->u16LCN == u16LCNNumber)
                                {
                                    pProg = pTmpProg;
                                    break;
                                }
                                else
                                {
                                    if(pProg == NULL)
                                    {
                                        if(MAPI_FALSE == _IsServiceLCNMatchBAT(pMux_Info->u16TransportStream_ID, pMux_Info->u16OriginalNetwork_ID,
                                            pTmpProg->u16ServiceID,pTmpProg->u16LCN))
                                        {
                                            pProg = pTmpProg;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(pProg)
                    {
                        if(pProg->u16LCN != u16LCNNumber)
                        {
                            MW_SI_PARSER_UPDATE("LCN change %s %d=>%d\n", pProg->u8ServiceName,
                                                pProg->u16LCN, u16LCNNumber);
                            pProg->u16LCN = u16LCNNumber;
                            if(pProg->stCHAttribute.u8IsMove == TRUE)
                            {
                                bUpdateMovedService = MAPI_TRUE;
                            }
                            bLCNUpdate = bUpdate = MAPI_TRUE;
                        }
                        if(bUpdate)
                        {
                            bNotify = MAPI_TRUE;
                            bUpdate = MAPI_FALSE;
                            pCMDB->Update(pProg);
                        }
                    }
                }
                pstTsInfo = pstTsInfo->next;
            }
            for(int i = 0; i < (int)pCMDB->Size(); i++)
            {
                pProg = pCMDB->GetByIndex(i);
                if (pProg != NULL)
                {
                    if(pMux == __GetMux(pProg, m_pCMDB))//pMux must be m_pCurMux
                    {
                        MAPI_BOOL bCmdbMatchNit = MAPI_TRUE;
                        pstTsInfo = m_stBat.pstTSInfoList;
                        for(int j = 0; j < m_stBat.u8TSNumber; j++)
                        {
                            if(pstTsInfo->u16TransportStream_ID == __GetID(pProg, pMux, pNetwork, EN_ID_TSID))
                            {
                                 int k = 0;
                                 for(k = 0; k < pstTsInfo->u16LCNNumber; k++)
                                 {
                                    if(__GetID(pProg, pMux, pNetwork, EN_ID_SID) == pstTsInfo->astLcnInfo[k].u16ServiceID)
                                    {

                                        break;
                                    }
                                 }

                                 if(k >= pstTsInfo->u16LCNNumber)
                                 {
                                     bCmdbMatchNit = MAPI_FALSE;
                                     break;
                                 }
                            }
                            pstTsInfo = pstTsInfo->next;
                        }

                        if(bCmdbMatchNit == MAPI_FALSE)
                        {
                            MW_SI_PARSER_UPDATE("CMDB service not found in BAT service:MuxTableID %d,Number %4d, LCN %4d, NAME %s\n",
                            pProg->m_u16MuxTableID,pProg->u16Number, pProg->u16LCN,pProg->u8ServiceName);

                            pProg->u16LCN = INVALID_LOGICAL_CHANNEL_NUMBER;
                            pProg->u16SimuLCN = INVALID_LOGICAL_CHANNEL_NUMBER;
                            if(pProg->stCHAttribute.u8IsMove == TRUE)
                            {
                                bUpdateMovedService = MAPI_TRUE;
                            }
                            bLCNUpdate = MAPI_TRUE;
                            bNotify = MAPI_TRUE;
                            pCMDB->Update(pProg);
                        }
                    }
                }
            }

            if((m_enCableOperator == EN_CABLEOP_KDG) && (bUpdateMovedService == MAPI_TRUE))
            {
                MONITOR_NOTIFY(E_DVB_KDG_LCN_UPDATE_MESSAGE_DISPLAY, NULL, m_pMonitorNotifyUsrParam, NULL);
            }
            else
            {
                if(bLCNUpdate)
                {
                    pCMDB->ReArrangeNumber();
                    _UpdateCurrentProgram();
                }

                if(bNotify)
                {
                    MAPI_BOOL bCurSrvChg = MAPI_FALSE;
                    MONITOR_NOTIFY(E_DVB_TS_CHANGE, &bCurSrvChg, m_pMonitorNotifyUsrParam, NULL);
                }
            }
        }
    }
}


void MW_DVB_SI_PSI_Parser::_NitReady(DVB_PROG *pCurProg, DVB_CM *pCMDB, DVB_MUX *pMux, DVB_NETWORK *pNetwork)
{
    DVB_PROG* pProg;
    MAPI_BOOL bNotify = MAPI_FALSE;
    MAPI_U8 u8CurTsIndex = 0xFF;
    MAPI_BOOL bCurSrvChg = MAPI_FALSE;

    //printf("%s.............%d\n",__FUNCTION__,__LINE__);
    _free_SI_TABLE_NIT(m_stNit);
    if (MAPI_FALSE == m_pNitParser->GetTable(m_stNit))
    {
        _DeleteParser((MAPI_U32)m_pNitParser, MAPI_FALSE);
        return;
    }

    table_release nit(&m_stNit, table_release::E_TABLE_NIT);

    if(E_DVB_SCAN != m_eParserMode)
    {
        //not normal scan, release all in default
        nit.setReleaseType(RELEASE_ALL);
    }

    if(E_DVB_MONITOR== m_eParserMode)
    {
        //unset partial(used in add new service)
        //unset cable in cable parser(_CheckNITAutoUpdateForDVBC)
        nit.unSetReleaseType(RELEASE_PARTIAL_RECEPTION);
        if(m_eParserType == MW_DVB_C_PARSER)
        {
            nit.unSetReleaseType(RELEASE_CABLE_DEL);
        }
        if(m_eParserType == MW_DVB_S_PARSER)
        {
            nit.unSetReleaseType(RELEASE_SAT_DEL);
        }
    }
    else if(E_DVB_QUICK_SCAN == m_eParserMode)
    {
        if(m_enCableOperator == EN_CABLEOP_KDG)
        {
            if(m_stNit.bKDG_PDSD_ID_Exist == MAPI_TRUE)
            {
                m_enQuickScanState = EN_QUICK_SCAN_STATE_GET_SDT;
                std::list<mapi_si_NIT_parser *> pTempList = m_pNitParserList;
                for(std::list<mapi_si_NIT_parser *>::iterator it=pTempList.begin(); it!=pTempList.end(); ++it)
                {
                    if(*it != NULL)
                    {
                        _DeleteParser((MAPI_U32)*it, MAPI_FALSE);
                    }
                }
                nit.unSetReleaseType(RELEASE_CABLE_DEL);
            }
            else
            {
                _free_SI_TABLE_NIT(m_stNit);
            }

            _DeleteParser((MAPI_U32)m_pNitParser, MAPI_FALSE);
            return;
        }
        //unset cable(cable quick scan)
        nit.unSetReleaseType(RELEASE_CABLE_DEL);
    }
#if (OAD_ENABLE == 1)
    else if (E_DVB_OAD_SCAN == m_eParserMode)
    {
         _DeleteParser((MAPI_U32)m_pNitParser, MAPI_FALSE);
        _free_SI_TABLE_NIT(m_stNit);
        return;
    }
#endif
#if (ASTRA_SGT_ENABLE == 1)
    else if(E_DVB_SGT_SCAN == m_eParserMode)
    {
        //unset satellite(satellite sgt scan)
        nit.unSetReleaseType(RELEASE_SAT_DEL);
    }
#endif

    MW_SI_PARSER_MESSAGE("get NIT ver %x,m_u8CurNitVer %x\n", m_stNit.u8Version,m_u8CurNitVer);
    _DeleteParser((MAPI_U32)m_pNitParser, MAPI_FALSE);
    MW_SI_PARSER_MESSAGE("s16OpenFilter %d\n", m_s16OpenFilter);

    if(E_DVB_MONITOR == m_eParserMode)
    {
#if (CI_PLUS_ENABLE == 1)
        if(m_enMonitorMode == EN_MW_SI_MONITOR_MODE_CI_SPECIFY_PMT)
        {
            if (m_u8CurNitVer != m_stNit.u8Version)
            {
                m_u8CurNitVer = m_stNit.u8Version;
                U16 u16ServiceID = (m_u32MonitorUserData & 0xFFFF);
                BOOL bVisibleflag = TRUE;
                for(int i = 0; i < m_stNit.u16TSNumber; i++)
                {
                    if(m_stPat.u16TsId == m_stNit.pstTSInfo[i].wTransportStream_ID)
                    {
                        U16 loopcnt =(m_stNit.pstTSInfo[i].u16LcnInfoNum > MAX_CHANNEL_IN_MUX)? MAX_CHANNEL_IN_MUX:m_stNit.pstTSInfo[i].u16LcnInfoNum;
                        for(U16 j = 0; j < loopcnt; j++)
                        {
                            if(u16ServiceID == m_stNit.pstTSInfo[i].astLcnInfo[j].u16ServiceID)
                            {
                                bVisibleflag = m_stNit.pstTSInfo[i].astLcnInfo[j].bIsVisable;
                                break;
                            }
                        }
                        break;
                    }
                }
                MONITOR_NOTIFY(E_DVB_CI_HC_TUNE_SERVICE_VISIBLE_FLAG, &bVisibleflag, m_pMonitorNotifyUsrParam, NULL);
            }
            _free_SI_TABLE_NIT(m_stNit);
            return;
        }
#endif
        BOOL bIsOriCurProgNumSelFlag = m_pCurProg->stCHAttribute.u8NumericSelectionFlag;
        /// Nit turn into to collect multi linkage descriptors
        if(m_stNit.pNitLinkageInfo!=NULL)
        {
            for(LinkageInfo_t::iterator it = m_stNit.pNitLinkageInfo->begin();it!= m_stNit.pNitLinkageInfo->end();++it)
            {
                if((*it).enLinkageType == EN_SI_LINKAGE_TS_CONTAIN_COMPLETE_NETWORK)
                {
                    memcpy(&m_NitLinkageEPG,&(*it) ,sizeof(MAPI_SI_DESC_LINKAGE_INFO));
                    break;
                }
            }
        }
        /// version without change need to set Cmg from NIT when dc off/on and switch channel
        for(U8 i = 0; i < m_stNit.u16TSNumber; i++)
        {
            //update CMG for TS level
            if(m_stNit.pstTSInfo[i].wTransportStream_ID == __GetID(pCurProg, pMux, pNetwork, EN_ID_TSID))
            {
                u8CurTsIndex = i;
                break;
            }
        }
        if((u8CurTsIndex != 0xFF) && (MAPI_TRUE == m_stNit.pstTSInfo[u8CurTsIndex].m_TsCMG.bExist))
        {
            _SetServiceCMG(m_stNit.pstTSInfo[u8CurTsIndex].m_TsCMG, E_CMG_FROM_NIT);
        }
        else
        {
            //update CMG for network level
            _SetServiceCMG(m_stNit.m_NetworkCMG, E_CMG_FROM_NIT);
        }

#if (NCD_ENABLE == 1)
        _UpdateNCDInfo();
#endif

        if(m_bIsSIDynmaicReScanOff == MAPI_TRUE)
        {
            return;
        }
        if (m_u8CurNitVer != m_stNit.u8Version)
        {
            MAPI_BOOL bUpdate = MAPI_FALSE;
            MAPI_BOOL bRearrange = MAPI_FALSE;
            MW_SI_PARSER_UPDATE("nit ver change %d=>%d\n", m_u8CurNitVer, m_stNit.u8Version);
            m_u8CurNitVer = m_stNit.u8Version;
            // check NIT update
            _CheckNITAutoUpdate(pCurProg, pCMDB, pMux, pNetwork);
#if (ASTRA_SGT_ENABLE == 1)
            if (m_eParserBaseType == MW_DVB_SGT_BASE)
            {
                _CatLinkageInfo(E_LINKAGE_SGT);
                if((m_stPat.u8Version != INVALID_PSI_SI_VERSION)&&(_BuildFullLinkage(E_LINKAGE_SGT)==E_LINKAGE_SGT_CUR))
                {
                    for(NIT_LINKAGE_t::iterator it =m_SGTLinkageInfo.begin();it!=m_SGTLinkageInfo.end();++it)
                    {
                        if(it->bIsMatchCurTs==TRUE)
                        {
                            _CreatePmtFilterBySrvID(it->stTargetCh.u16SrvId);
                            break;
                        }
                    }
                }
                //The following codes update channel info by NIT, SGT base should base on SGT.
                return ;
            }
#endif
            MW_DTV_CM_DB_scope_lock lock(pCMDB);
            __SetVer(pCurProg, EN_VER_NIT, m_u8CurNitVer);
            pCMDB->Update(pCurProg);

            for(int i = 0; i < m_stNit.u16TSNumber; i++)
            {
                if((m_eCountry == E_MALAYSIA)&&(m_stNit.pstTSInfo != NULL)&&(m_stNit.pstTSInfo[i].u16ONID == MALAYSIA_ONID))
                {
                    for(int Idx = 0; Idx < m_stNit.pstTSInfo[i].u8ChannelListNumber; Idx++)
                    {
                        if(m_s8RegionalListID == m_stNit.pstTSInfo[i].astLcnV2Info[Idx].u8ChannelListID)
                        {
                            memcpy(m_stNit.pstTSInfo[i].astLcnInfo, m_stNit.pstTSInfo[i].astLcnV2Info[Idx].pLCNInfo,
                                        m_stNit.pstTSInfo[i].astLcnV2Info[Idx].u8ServicesNumber * sizeof(MAPI_SI_LCN_INFO));
                            m_stNit.pstTSInfo[i].u16LcnInfoNum = m_stNit.pstTSInfo[i].astLcnV2Info[Idx].u8ServicesNumber;
                        }
                    }
                }
                //update CMG for TS level
                if(m_stNit.pstTSInfo[i].wTransportStream_ID == __GetID(pCurProg, pMux, pNetwork, EN_ID_TSID))
                {
                    u8CurTsIndex = i;
                }
                U16 loopcnt =(m_stNit.pstTSInfo[i].u16LcnInfoNum > MAX_CHANNEL_IN_MUX)? MAX_CHANNEL_IN_MUX:m_stNit.pstTSInfo[i].u16LcnInfoNum;
                for(U16 j = 0; j < loopcnt; j++)
                {
                    if ((m_eParserType == MW_DVB_T_PARSER) && (m_eCountry == E_FINLAND))
                    {
                        // it shall install all services with same service but different LCN for Antenna Ready HD
                        pProg = pCMDB->GetByNumID(m_stNit.pstTSInfo[i].astLcnInfo[j].u16LCNNumber,
                                                            m_stNit.pstTSInfo[i].wTransportStream_ID, m_stNit.pstTSInfo[i].u16ONID,
                                                            m_stNit.pstTSInfo[i].astLcnInfo[j].u16ServiceID);
                        if (pProg == NULL)
                        {
                            pProg = pCMDB->GetByID(m_stNit.pstTSInfo[i].wTransportStream_ID, m_stNit.pstTSInfo[i].u16ONID,
                                                                m_stNit.pstTSInfo[i].astLcnInfo[j].u16ServiceID);
                        }
                    }
                    else
                    {
                        pProg = pCMDB->GetByID(m_stNit.pstTSInfo[i].wTransportStream_ID, m_stNit.pstTSInfo[i].u16ONID,
                                                            m_stNit.pstTSInfo[i].astLcnInfo[j].u16ServiceID);
                    }

                    if(m_stNit.pstTSInfo[i].astLcnInfo[j].u16ServiceID &&
                            (pProg != NULL) &&
                            (pProg->stCHAttribute.u8ServiceType != E_SERVICETYPE_INVALID))
                    {
                        if((pProg->stCHAttribute.u8ServiceType == E_SERVICETYPE_DATA) && (m_eCountry == E_NORWAY))
                        {
                            m_stNit.pstTSInfo[i].astLcnInfo[j].bIsVisable = m_stNit.pstTSInfo[i].astLcnInfo[j].bIsSelectable = MAPI_FALSE;
                            m_stNit.pstTSInfo[i].astLcnInfo[j].u16LCNNumber = INVALID_LOGICAL_CHANNEL_NUMBER;
                        }
                        if((pProg->u16LCN != m_stNit.pstTSInfo[i].astLcnInfo[j].u16LCNNumber)&&(!m_bEnableBouquetFilter))
                        {
                            MW_SI_PARSER_UPDATE("LCN change %s %d=>%d\n", pProg->u8ServiceName,
                                                pProg->u16LCN, m_stNit.pstTSInfo[i].astLcnInfo[j].u16LCNNumber);
                            pProg->u16LCN = m_stNit.pstTSInfo[i].astLcnInfo[j].u16LCNNumber;
                            bRearrange = bUpdate = MAPI_TRUE;
                        }
                        if(pProg->u16SimuLCN != m_stNit.pstTSInfo[i].astLcnInfo[j].u16SimuLCNNumber)
                        {
                            MW_SI_PARSER_UPDATE("simu LCN change %s %d=>%d\n", pProg->u8ServiceName,
                                                pProg->u16SimuLCN, m_stNit.pstTSInfo[i].astLcnInfo[j].u16SimuLCNNumber);
                            pProg->u16SimuLCN = m_stNit.pstTSInfo[i].astLcnInfo[j].u16SimuLCNNumber;
                            bRearrange = bUpdate = MAPI_TRUE;
                        }
                        if(pProg->stCHAttribute.u8VisibleServiceFlag != m_stNit.pstTSInfo[i].astLcnInfo[j].bIsVisable)
                        {
                            //printf("pProg->stCHAttribute.u8ServiceType...%x(%x)\n",pProg->stCHAttribute.u8ServiceType,m_stNit.astTSInfo[i].astLcnInfo[j].u16ServiceID);
                            MW_SI_PARSER_UPDATE("visible change %s %d=>%d\n", pProg->u8ServiceName,
                                                pProg->stCHAttribute.u8VisibleServiceFlag, m_stNit.pstTSInfo[i].astLcnInfo[j].bIsVisable);
                            pProg->stCHAttribute.u8VisibleServiceFlag = m_stNit.pstTSInfo[i].astLcnInfo[j].bIsVisable;

                            //-------------------------------------------------//
                            // For ZIggo test script 1.91 NIT_0009
                            // The service became invisible due to it has a HD simulcast service
                            // Now it is updated to be visible, we need to check if it still t has a HD simulcast service.
                            // So, rearrange it again.
                            if (_IsZiggoOperator()
                                    && (m_stNit.pstTSInfo[i].astLcnInfo[j].bIsVisable)) //become visible
                            {
                                bRearrange = MAPI_TRUE;
                            }
                            //-------------------------------------------------//

                            bUpdate = MAPI_TRUE;
                        }
                        if(pProg->stCHAttribute.u8NumericSelectionFlag != m_stNit.pstTSInfo[i].astLcnInfo[j].bIsSelectable)
                        {
                            MW_SI_PARSER_UPDATE("selectable change %s %d=>%d\n", pProg->u8ServiceName,
                                                pProg->stCHAttribute.u8NumericSelectionFlag, m_stNit.pstTSInfo[i].astLcnInfo[j].bIsSelectable);
                            pProg->stCHAttribute.u8NumericSelectionFlag = m_stNit.pstTSInfo[i].astLcnInfo[j].bIsSelectable;

                            bUpdate = MAPI_TRUE;
                        }

                        if ((pProg->stCHAttribute.u8RealServiceType ==  E_TYPE_DATA)
                            || (pProg->stCHAttribute.u8RealServiceType == E_TYPE_MHP))
                        {
                            if(MAPI_FALSE == _IsSpecificSupport(E_DVB_DATA_SERVICE_SUPPORT,&m_eCountry, NULL))
                            {
                                pProg->stCHAttribute.u8NumericSelectionFlag = MAPI_FALSE;
                                pProg->stCHAttribute.u8VisibleServiceFlag = MAPI_FALSE;
                                bUpdate = MAPI_TRUE;
                            }
                        }

                        if((pProg->u16LCN == 0) && (!IS_DTG_COUNTRY(m_eCountry)))
                        {
                            pProg->stCHAttribute.u8VisibleServiceFlag = MAPI_FALSE;
                            pProg->stCHAttribute.u8NumericSelectionFlag = MAPI_FALSE;
                            bUpdate = MAPI_TRUE;
                        }

                        // service should be skipped when LCN equal to zero or not carried for Ziggo/UPC/Telenet
                        if((m_eParserType == MW_DVB_C_PARSER)
                            && ((m_enCableOperator == EN_CABLEOP_ZIGGO) || IS_UPC(m_enCableOperator)
                                || (m_enCableOperator == EN_CABLEOP_TELENET)))
                        {
                            if((pProg->u16LCN == INVALID_LOGICAL_CHANNEL_NUMBER) ||(pProg->u16LCN == 0))
                            {
                                pProg->stCHAttribute.u8VisibleServiceFlag = MAPI_FALSE;
                                pProg->stCHAttribute.u8NumericSelectionFlag = MAPI_FALSE;
                                bUpdate = MAPI_TRUE;
                            }
                        }
                        if(bUpdate)
                        {
                            bNotify = MAPI_TRUE;
                            bUpdate = MAPI_FALSE;
                            pCMDB->Update(pProg);
                        }
                    }
                }
                // check service type changed or not
                if (m_eParserBaseType == MW_DVB_NIT_BASE)
                {
                    MAPI_SI_SERVICETYPE enOldRealSrvType = E_TYPE_INVALID;
                    MEMBER_SERVICETYPE enOldSrvType = E_SERVICETYPE_INVALID;
                    loopcnt =(m_stNit.pstTSInfo[i].u16ServiceNum > MAX_CHANNEL_IN_MUX)? MAX_CHANNEL_IN_MUX:m_stNit.pstTSInfo[i].u16ServiceNum;
                    for(U16 j = 0; j < loopcnt; j++)
                    {
                        pProg = pCMDB->GetByID(m_stNit.pstTSInfo[i].wTransportStream_ID, m_stNit.pstTSInfo[i].u16ONID, m_stNit.pstTSInfo[i].astServiceInfo[j].u16ServiceID);
                        if (m_stNit.pstTSInfo[i].astServiceInfo[j].u16ServiceID && (pProg != NULL))
                        {
                            do
                            {
                                enOldRealSrvType = (MAPI_SI_SERVICETYPE)pProg->stCHAttribute.u8RealServiceType;
                                enOldSrvType = (MEMBER_SERVICETYPE)pProg->stCHAttribute.u8ServiceType;
                                _UpdateServiceType(&pProg->stCHAttribute.u8ServiceType, &pProg->stCHAttribute.u8ServiceTypePrio,
                                                    m_stNit.pstTSInfo[i].astServiceInfo[j].u8ServiceType);
                                if ((m_stNit.pstTSInfo[i].astServiceInfo[j].u8ServiceType ==  E_TYPE_DATA) || (m_stNit.pstTSInfo[i].astServiceInfo[j].u8ServiceType == E_TYPE_MHP))
                                {
                                    if(MAPI_FALSE == _IsSpecificSupport(E_DVB_DATA_SERVICE_SUPPORT,&m_eCountry, NULL))
                                    {
                                        pProg->stCHAttribute.u8NumericSelectionFlag = MAPI_FALSE;
                                        pProg->stCHAttribute.u8VisibleServiceFlag = MAPI_FALSE;
                                    }
                                }
                                pProg->stCHAttribute.u8RealServiceType = m_stNit.pstTSInfo[i].astServiceInfo[j].u8ServiceType;
                                if ((enOldRealSrvType != pProg->stCHAttribute.u8RealServiceType)
                                    || (enOldSrvType != pProg->stCHAttribute.u8ServiceType))
                                {
                                    bNotify = MAPI_TRUE;
                                    pCMDB->Update(pProg);
                                    if (enOldSrvType != pProg->stCHAttribute.u8ServiceType)
                                    {
                                        // service type change, need rearrange the programs
                                        bRearrange = MAPI_TRUE;
                                    }
                                }
                                pProg = pCMDB->GetByIDNext(pProg, m_stNit.pstTSInfo[i].wTransportStream_ID, m_stNit.pstTSInfo[i].u16ONID,
                                                                        m_stNit.pstTSInfo[i].astServiceInfo[j].u16ServiceID);
                            }while((pProg!=NULL) && (m_eParserType == MW_DVB_T_PARSER)
                                                && (m_eCountry == E_FINLAND));
                        }
                    }
                }
                // check service type changed or not - END
            }

            // check service carried LCN or not
            for(int i = 0; i < (int)pCMDB->Size(); i++)
            {
                pProg = pCMDB->GetByIndex(i);
                //Prog has LCN, and the following code will check whether its LCN is gone or not.
                if ((pProg != NULL) && ((pProg->u16LCN != INVALID_LOGICAL_CHANNEL_NUMBER) || (pProg->u16SimuLCN != INVALID_LOGICAL_CHANNEL_NUMBER)))
                {
                    if(pMux == __GetMux(pProg, m_pCMDB))//pMux must be m_pCurMux
                    {
                        MAPI_BOOL bCmdbMatchNit = MAPI_TRUE;
                        for(int j = 0; j < m_stNit.u16TSNumber; j++)
                        {
                            if(m_stNit.pstTSInfo[j].wTransportStream_ID == __GetID(pProg, pMux, pNetwork, EN_ID_TSID))
                            {
                                int k = 0;
                                for(k = 0; k < MAX_CHANNEL_IN_MUX; k++)
                                {
                                    if(__GetID(pProg, pMux, pNetwork, EN_ID_SID) == m_stNit.pstTSInfo[j].astLcnInfo[k].u16ServiceID)
                                    {
                                        break;
                                    }
                                }

                                if(k >= MAX_CHANNEL_IN_MUX)
                                {
                                    bCmdbMatchNit = MAPI_FALSE;
                                    break;
                                }
                            }
                        }

                        if(bCmdbMatchNit == MAPI_FALSE)
                        {
                            MW_SI_PARSER_UPDATE("CMDB service not found in NIT service:MuxTableID %d,Number %4d, LCN %4d, NAME %s\n",
                            pProg->m_u16MuxTableID,pProg->u16Number, pProg->u16LCN,pProg->u8ServiceName);

                            pProg->u16LCN = INVALID_LOGICAL_CHANNEL_NUMBER;
                            pProg->u16SimuLCN = INVALID_LOGICAL_CHANNEL_NUMBER;
                            // service should be skipped when LCN equal to zero or not carried for Ziggo/UPC/Telenet
                            if((m_eParserType == MW_DVB_C_PARSER)
                                && ((m_enCableOperator == EN_CABLEOP_ZIGGO)
                                    || (IS_UPC(m_enCableOperator))
                                    || (m_enCableOperator == EN_CABLEOP_TELENET)))
                            {
                                pProg->stCHAttribute.u8VisibleServiceFlag = MAPI_FALSE;
                                pProg->stCHAttribute.u8NumericSelectionFlag = MAPI_FALSE;
                            }
                            bRearrange = MAPI_TRUE;
                            bNotify = MAPI_TRUE;
                            pCMDB->Update(pProg);
                        }
                    }
                }
            }
            // check service carried LCN or not - END

            if(bRearrange)
            {
                pCMDB->ReArrangeNumber();
                _UpdateCurrentProgram();
            }

            // check if current program is changed to non-selectable, change to other channel playing
            if ((bIsOriCurProgNumSelFlag == TRUE) && (m_pCurProg->stCHAttribute.u8NumericSelectionFlag == MAPI_FALSE))
            {
                DVB_PROG *pNextProg = NULL;
                DVB_PROG *pstNonVisibleProg = NULL;

                pNextProg = pCMDB->GetByIndex(0);
                while(pNextProg)
                {
                    if (__GetMux(pNextProg, pCMDB) == pMux)
                    {
                        if (pNextProg->stCHAttribute.u8NumericSelectionFlag == MAPI_TRUE)
                        {
                            if (pNextProg->stCHAttribute.u8VisibleServiceFlag == MAPI_TRUE)
                            {
                                break;
                            }
                            else if (pstNonVisibleProg == NULL)
                            {
                                pstNonVisibleProg = pNextProg;
                            }
                        }
                    }
                    pNextProg = pCMDB->GetNext(pNextProg);
                }
                if (pNextProg)
                {
                    // found visible and selectable service with sam mux
                    pCMDB->SetCurr(pNextProg);
                }
                else if (pstNonVisibleProg)
                {
                    /* if current mux has no visible service,
                        change to first nonvisible service with same mux */
                    pCMDB->SetCurr(pstNonVisibleProg);
                }
                else
                {
                    /* if current mux has no selectable and visible service,
                        change to first program */
                    DVB_PROG* pProgDVB = (DVB_PROG *)pCMDB->GetByIndex(0);
                    if (pProgDVB != NULL)
                    {
                        pCMDB->SetCurr(pProgDVB);
                    }
                    else
                    {
                        ASSERT(0);
                    }
                }
                _UpdateCurrentProgram();
                bCurSrvChg = MAPI_TRUE;
            }
            // check if current program is changed to non-selectable, change the other channel to play - END

            //Check update network name,network name should be updated if current TS belongs to the same network
            if((pNetwork != NULL) && (pMux != NULL))
            {
                for(int i = 0; i < m_stNit.u16TSNumber; i++)
                {
                    if((m_stNit.pstTSInfo[i].u16ONID == __GetID(pCurProg, pMux, pNetwork, EN_ID_ONID)) &&
                       (m_stNit.u16NetworkID == __GetID(pCurProg, pMux, pNetwork, EN_ID_NID)))
                    {
                        if( memcmp(m_stNit.au8NetWorkName,pNetwork->aNetworkName,(MAPI_SI_MAX_NETWORK_NAME > MAX_NETWORK_NAME) ? MAX_NETWORK_NAME : MAPI_SI_MAX_NETWORK_NAME) )
                        {
                            memcpy(pNetwork->aNetworkName, m_stNit.au8NetWorkName ,(MAPI_SI_MAX_NETWORK_NAME > MAX_NETWORK_NAME) ? MAX_NETWORK_NAME : MAPI_SI_MAX_NETWORK_NAME);
                            pCMDB->UpdateNetwork(pNetwork);
                            MW_SI_PARSER_UPDATE("[%s:%d]Network Name Updated\n",__FUNCTION__,__LINE__);
                        }
                    }
                }
            }
            if(bNotify)
            {
                MONITOR_NOTIFY(E_DVB_TS_CHANGE, &bCurSrvChg, m_pMonitorNotifyUsrParam, NULL);
            }
        }//if(m_u8CurNitVer != m_stNit.u8Version)

    }//if(E_DVB_MONITOR == m_eParserMode)

}

#if (CI_PLUS_ENABLE == 1)
MAPI_BOOL MW_DVB_SI_PSI_Parser::_IsSdtServiceInfoExist(MAPI_U32 u32SID)
{
    for(int i = 0; i < m_stSdt.wServiceNumber; i++)
    {
        if(m_stSdt.astServiceInfo[i].u16ServiceID == u32SID)
        {
            if(m_stSdt.astServiceInfo[i].pu8CI_protect_info)
                return TRUE;
            else
                return FALSE;
        }
    }

    return FALSE;
}
#endif

void MW_DVB_SI_PSI_Parser::_SdtReady(DVB_PROG *pCurProg, DVB_CM *pCMDB, DVB_MUX *pMux, DVB_NETWORK *pNetwork)
{
    DVB_PROG* pProgDVB;
    MAPI_BOOL bNewService = MAPI_FALSE;
    MAPI_BOOL bCurSrvChg = MAPI_FALSE;
    MAPI_U16 u16CurSrvIndex = 0xFFFF;
    MAPI_BOOL bUpdateTS = MAPI_FALSE;
    MAPI_BOOL bUpdate = MAPI_FALSE;
    MAPI_BOOL bRearrange = MAPI_FALSE;
    //printf("%s.............%d\n",__FUNCTION__,__LINE__);
#if (OAD_ENABLE == 1)
    if(m_eParserMode == E_DVB_OAD_SCAN)
    {
        MAPI_SI_TABLE_SDT SdtTmp;
        memset(&SdtTmp, 0, sizeof(MAPI_SI_TABLE_SDT));
        if ((m_pSdtParser != NULL) && (MAPI_TRUE == m_pSdtParser->GetTable(SdtTmp)))
        {
            table_release sdt(&SdtTmp, table_release::E_TABLE_SDT);
            sdt.setReleaseType(RELEASE_ALL);

            if(_ShouldCheckOnidForOAD())
            {
                if(__IsONIDMatchCountry(SdtTmp.wOriginalNetwork_ID, m_eCountry))
                {
                    m_bOADONIDMatch = MAPI_TRUE;
                }
                else
                {
                    ASSERT(m_OADParser);
                    m_OADParser->SkipCurrentScan();
                    m_eParserMode = E_DVB_IDLE;
                }
            }
        }
        _DeleteParser((MAPI_U32)m_pSdtParser , MAPI_FALSE);
        return;
    }
#endif
    if ((m_pSdtParser != NULL) && (MAPI_FALSE == m_pSdtParser->GetTable(m_stSdt)))
    {
        _DeleteParser((MAPI_U32)m_pSdtParser , MAPI_FALSE);
        return;
    }
    table_release sdt(&m_stSdt, table_release::E_TABLE_SDT);

    sdt.setReleaseType(RELEASE_ALL);
    if(E_DVB_SCAN == m_eParserMode)
    {
        sdt.unSetReleaseType(RELEASE_REGION);
    }

    MW_SI_PARSER_MESSAGE("get SDT ver 0x%x\n", m_stSdt.u8Version);
    //printf("sdt tsid %x\n",m_stSdt.wTransportStream_ID);

    _DeleteParser((MAPI_U32)m_pSdtParser , MAPI_FALSE);
    MW_SI_PARSER_MESSAGE("s16OpenFilter %d\n", m_s16OpenFilter);
    if(E_DVB_MONITOR == m_eParserMode)
    {
        m_ListRPServiceInfo.clear();
        m_CurRPServiceInfo.bSer_Replacement = MAPI_FALSE;
        MW_SI_PARSER_UPDATE("cur onid %x tsid %x sid %x\n", __GetID(pCurProg, pMux, pNetwork, EN_ID_ONID),
                            __GetID(pCurProg, pMux, pNetwork, EN_ID_TSID), __GetID(pCurProg, pMux, pNetwork, EN_ID_SID));
#if (CI_PLUS_ENABLE == 1)
        if(m_ParserCallBackInfo.CIProtectionNotify)
        {
            for(int i = 0; i < m_stPat.u16ServiceCount; i++)
            {
                if (_IsSdtServiceInfoExist(m_stPat.ServiceIDInfo[i].u16ServiceID))
                {
                    m_ParserCallBackInfo.CIProtectionNotify(m_ParserCallBackInfo.pCallbackReference, m_stSdt.astServiceInfo[i].pu8CI_protect_info,
                                                            m_stSdt.wOriginalNetwork_ID, m_stPat.u16TsId, m_stPat.ServiceIDInfo[i].u16ServiceID);
                }
                else
                {
                    m_ParserCallBackInfo.CIProtectionNotify(m_ParserCallBackInfo.pCallbackReference, NULL,
                                                            m_stSdt.wOriginalNetwork_ID, m_stPat.u16TsId, m_stPat.ServiceIDInfo[i].u16ServiceID);
                }
            }

            if(m_stSdt.wServiceNumber)
            {
                MW_SI_PARSER_MESSAGE("EitPF ready, send CI protection event!\n");
                MONITOR_NOTIFY(E_DVB_SDT_CI_PROTECTION_FINISH, NULL, m_pMonitorNotifyUsrParam, NULL);
            }

        }

        if(m_enMonitorMode == EN_MW_SI_MONITOR_MODE_CI_SPECIFY_PMT)
        {
            return;
        }

        if(m_bIsOpMode == MAPI_TRUE)
        {
            return;
        }
#endif

        // update onid directly when onid in cmdb is invalid
        if (INVALID_ON_ID == __GetID(pCurProg, pMux, pNetwork, EN_ID_ONID))
        {
            MW_DTV_CM_DB_scope_lock lock(pCMDB);
            pMux->u16OriginalNetwork_ID = m_stSdt.wOriginalNetwork_ID;
            pCMDB->UpdateMux(pMux);
        }
        // update onid directly when onid in cmdb is invalid - END

        if((__GetID(pCurProg, pMux, pNetwork, EN_ID_ONID) == m_stSdt.wOriginalNetwork_ID)
                && (__GetID(pCurProg, pMux, pNetwork, EN_ID_TSID) == m_stSdt.wTransportStream_ID))
        {
            MW_DTV_CM_DB_scope_lock lock(pCMDB);
            for(int i = 0; i < m_stSdt.wServiceNumber; i++)
            {
                // check EPG sort id changed or not
                E_CHINA_DVBCREGION eRegion = E_CN_NUM;
                if (m_ParserCallBackInfo.EventHandler)
                {
                    m_ParserCallBackInfo.EventHandler(E_DVB_SI_GET_CHINA_DVBCREGION,(MAPI_U32)m_ParserCallBackInfo.pCallbackReference,(MAPI_U32)&eRegion);
                }
                if((E_CN_YANGZHOU_CABLE == eRegion)
                    && (m_stSdt.astServiceInfo[i].stLcnInfo.u16ServiceID > 0))
                {
                    pProgDVB = pCMDB->GetByID(m_stSdt.wTransportStream_ID, m_stSdt.wOriginalNetwork_ID,
                        m_stSdt.astServiceInfo[i].u16ServiceID);
                    if((pProgDVB != NULL)
                        && (pProgDVB->u16ServiceID == m_stSdt.astServiceInfo[i].stLcnInfo.u16ServiceID)
                        && (pProgDVB->u16LCN != m_stSdt.astServiceInfo[i].stLcnInfo.u16LCNNumber))
                    {
                        pProgDVB->u16LCN = m_stSdt.astServiceInfo[i].stLcnInfo.u16LCNNumber;
                        bRearrange = MAPI_TRUE;
                        pCMDB->Update(pProgDVB);
                    }
                }
                // check EPG sort id changed or not - END
                // check service type changed or not
                if ((m_bIsSIDynmaicReScanOff ==MAPI_FALSE)&&((m_eParserBaseType == MW_DVB_SDT_PAT_BASE) ||(m_eParserBaseType == MW_DVB_SDT_BASE)
                                                              ||((m_eParserBaseType == MW_DVB_NIT_BASE)&&IS_UPC(m_enCableOperator))))
                {
                    MAPI_SI_SERVICETYPE enOldRealSrvType = E_TYPE_INVALID;
                    MEMBER_SERVICETYPE enOldSrvType = E_SERVICETYPE_INVALID;
                    pProgDVB = pCMDB->GetByID(m_stSdt.wTransportStream_ID, m_stSdt.wOriginalNetwork_ID,
                        m_stSdt.astServiceInfo[i].u16ServiceID);
                    if (m_stSdt.astServiceInfo[i].u16ServiceID && (pProgDVB != NULL)&& (m_eParserBaseType != MW_DVB_SGT_BASE))
                    {
                        do
                        {
                            enOldRealSrvType = (MAPI_SI_SERVICETYPE)pProgDVB->stCHAttribute.u8RealServiceType;
                            enOldSrvType = (MEMBER_SERVICETYPE)pProgDVB->stCHAttribute.u8ServiceType;
                            _UpdateServiceType(&pProgDVB->stCHAttribute.u8ServiceType, &pProgDVB->stCHAttribute.u8ServiceTypePrio,
                                                m_stSdt.astServiceInfo[i].u8ServiceType);
                            if ((m_stSdt.astServiceInfo[i].u8ServiceType ==  E_TYPE_DATA) || (m_stSdt.astServiceInfo[i].u8ServiceType == E_TYPE_MHP))
                            {
                                if(MAPI_FALSE == _IsSpecificSupport(E_DVB_DATA_SERVICE_SUPPORT,&m_eCountry, NULL))
                                {
                                    pProgDVB->stCHAttribute.u8NumericSelectionFlag = MAPI_FALSE;
                                    pProgDVB->stCHAttribute.u8VisibleServiceFlag = MAPI_FALSE;
                                }
                            }
                            pProgDVB->stCHAttribute.u8RealServiceType = m_stSdt.astServiceInfo[i].u8ServiceType;
                            if ((enOldRealSrvType != pProgDVB->stCHAttribute.u8RealServiceType)
                                || (enOldSrvType != pProgDVB->stCHAttribute.u8ServiceType))
                            {
                                bUpdate = MAPI_TRUE;
                                if(pProgDVB->stCHAttribute.u8ServiceType >= E_SERVICETYPE_INVALID)
                                {
                                    pCMDB->Delete(pProgDVB);
                                    bRearrange = MAPI_TRUE;
                                }
                                else
                                {
                                    pCMDB->Update(pProgDVB);
                                    if (enOldSrvType != pProgDVB->stCHAttribute.u8ServiceType)
                                    {
                                        // service type change, need rearrange the programs
                                        bRearrange = MAPI_TRUE;
                                    }
                                }

                            }
                            pProgDVB = pCMDB->GetByIDNext(pProgDVB, m_stSdt.wTransportStream_ID, m_stSdt.wOriginalNetwork_ID,
                                                                    m_stSdt.astServiceInfo[i].u16ServiceID);
                        }while((pProgDVB!=NULL) && (m_eParserType == MW_DVB_T_PARSER)
                                            && (m_eCountry == E_FINLAND));
                    }
                }
                // check service type changed or not - END

                if(__GetID(pCurProg, pMux, pNetwork, EN_ID_SID) == m_stSdt.astServiceInfo[i].u16ServiceID)
                {
                    m_CurServiceInfo.bEit_pf_flag = m_stSdt.astServiceInfo[i].u8NowNextFlag;
                    if((!m_CurServiceInfo.bEit_pf_flag) && (!_IsSpecificSupport(E_DVB_CHECK_PF_PRESENT_IN_SDT,&m_eCountry,NULL)))
                    {
                        m_CurServiceInfo.bEit_pf_flag=MAPI_TRUE;
                    }
                    m_CurServiceInfo.bEit_schedule_flag = m_stSdt.astServiceInfo[i].u8EITSchFlag;

                    m_CurServiceInfo.bIsSDTCAExist = m_stSdt.astServiceInfo[i].u8FreeCAMode;
                    if(MAPI_TRUE == _IsSpecificSupport(E_DVB_CHECK_SDT_FREE_CA_MODE,&m_eCountry, NULL))
                    {
                        m_CurServiceInfo.bIsCAExist = m_CurServiceInfo.bIsSDTCAExist | m_CurServiceInfo.bIsPMTCAExist;
                    }
                    else
                    {
                        m_CurServiceInfo.bIsCAExist = m_CurServiceInfo.bIsPMTCAExist;
                    }
                    if(m_bIsSIDynmaicReScanOff ==MAPI_FALSE)
                    {
                        __UpdateService(m_pCurProg, &m_CurServiceInfo, m_pCMDB, MAPI_TRUE);
                        if(m_eParserBaseType != MW_DVB_SGT_BASE)
                        {
                            __UpdateName(pCurProg, m_stSdt.astServiceInfo[i].u8ServiceName, pCMDB);
                            __UpdateProviderName(pCurProg, m_stSdt.astServiceInfo[i].u8ServiceProviderName, pCMDB);
#if (MULTIPLE_SERVICE_NAME_ENABLE == 1)
                            __UpdateMultiName(pCurProg, m_stSdt.astServiceInfo[i].u8MultiServiceName, m_stSdt.astServiceInfo[i].aeLangIndex, m_stSdt.astServiceInfo[i].u8MultiServiceNameCnt, pCMDB);
#endif
                            pCurProg->stCHAttribute.u8RealServiceType = m_stSdt.astServiceInfo[i].u8ServiceType;
                        }
                    }

                    m_CurRPServiceInfo.eRunningStatus = (MW_SI_SDT_RUNNINGSTATUS)m_stSdt.astServiceInfo[i].u8RunningStatus;
                    m_CurRPServiceInfo.u8FreeCAMode = m_stSdt.astServiceInfo[i].u8FreeCAMode;
                    if(m_stSdt.astServiceInfo[i].stLinkageInfo.enLinkageType)
                    {
                        m_CurRPServiceInfo.enLinkageType = m_stSdt.astServiceInfo[i].stLinkageInfo.enLinkageType;
                        m_CurRPServiceInfo.bSer_Replacement = m_stSdt.astServiceInfo[i].stLinkageInfo.u8Ser_Replacement;
                        m_CurRPServiceInfo.u16ONId = m_stSdt.astServiceInfo[i].stLinkageInfo.u16ONId;
                        m_CurRPServiceInfo.u16TSId = m_stSdt.astServiceInfo[i].stLinkageInfo.u16TSId;
                        m_CurRPServiceInfo.u16ServiceId = m_stSdt.astServiceInfo[i].stLinkageInfo.u16ServiceId;
                        m_CurRPServiceInfo.u16RPS_ONId = m_stSdt.wOriginalNetwork_ID;
                        m_CurRPServiceInfo.u16RPS_TSId = m_stSdt.wTransportStream_ID;
                        m_CurRPServiceInfo.u16RPS_ServiceId = m_stSdt.astServiceInfo[i].u16ServiceID;
                        m_CurRPServiceInfo.bCurService = MAPI_TRUE;
                    }

#if (GUIDANCE_ENABLE == 1)
                    _AddGuidanceInfoFromSdt(m_stSdt.wOriginalNetwork_ID,m_stSdt.wTransportStream_ID, m_stSdt.astServiceInfo[i].u16ServiceID, m_stSdt.astServiceInfo[i].pu8ServiceGuidance);
#endif

                    u16CurSrvIndex = i;
                    //break;
                    if(m_CurServiceInfo.bEit_pf_flag == MAPI_FALSE)
                    {
                        MONITOR_NOTIFY(E_DVB_PARENTAL_RATING, NULL, m_pMonitorNotifyUsrParam, NULL);
                    }
#if (ISDB_SYSTEM_ENABLE == 1)
#if 1//(ISDB_CHANNELLOGO_ENABLE == 1)
                    if( m_stSdt.astServiceInfo[i].stLogoTransmission.u8logo_transmission_type == LOGO_TRANSMISSION_TYPE_1 )

                    {
                        m_CurServiceLogoInfo.u16logo_version = m_stSdt.astServiceInfo[i].stLogoTransmission.u16logo_version;
                        m_CurServiceLogoInfo.u8logo_transmission_type = m_stSdt.astServiceInfo[i].stLogoTransmission.u8logo_transmission_type;
                        m_CurServiceLogoInfo.u16logo_id = m_stSdt.astServiceInfo[i].stLogoTransmission.u16logo_id ;
                        m_CurServiceLogoInfo.u16download_data_id = m_stSdt.astServiceInfo[i].stLogoTransmission.u16download_data_id;
                        m_bEnableCdtMonitorFlag = MAPI_TRUE;
                        MW_SI_PARSER_MESSAGE("m_CurServiceLogoInfo update logo type:%d,id:%d,version:%d,data_id:%d\n",
                                m_CurServiceLogoInfo.u8logo_transmission_type,m_CurServiceLogoInfo.u16logo_id,m_CurServiceLogoInfo.u16logo_version,
                                m_CurServiceLogoInfo.u16download_data_id);
                    }
                    else if(m_stSdt.astServiceInfo[i].stLogoTransmission.u8logo_transmission_type == LOGO_TRANSMISSION_TYPE_2)
                    {
                        m_CurServiceLogoInfo.u8logo_transmission_type = m_stSdt.astServiceInfo[i].stLogoTransmission.u8logo_transmission_type;
                        m_CurServiceLogoInfo.u16logo_id = m_stSdt.astServiceInfo[i].stLogoTransmission.u16logo_id ;

                        m_bEnableCdtMonitorFlag = MAPI_TRUE;
                        MW_SI_PARSER_MESSAGE("m_CurServiceLogoInfo update logo type:%d,id:%d\n",
                                m_CurServiceLogoInfo.u8logo_transmission_type,m_CurServiceLogoInfo.u16logo_id);
                    }
                    else if(m_stSdt.astServiceInfo[i].stLogoTransmission.u8logo_transmission_type == LOGO_TRANSMISSION_TYPE_3)
                    {
                        m_CurServiceLogoInfo.u8logo_transmission_type = m_stSdt.astServiceInfo[i].stLogoTransmission.u8logo_transmission_type;
                        m_CurServiceLogoInfo.u16LogoDataLength = m_stSdt.astServiceInfo[i].stLogoTransmission.u16LogoDataLength;
                        m_bEnableCdtMonitorFlag = MAPI_FALSE;
                        FREE(m_CurServiceLogoInfo.pu8LogoData);//should free first, may version change case
                        m_CurServiceLogoInfo.pu8LogoData = m_stSdt.astServiceInfo[i].stLogoTransmission.pu8LogoData;
                        m_stSdt.astServiceInfo[i].stLogoTransmission.pu8LogoData = NULL;
                        m_stSdt.astServiceInfo[i].stLogoTransmission.u16LogoDataLength = 0;
                        MW_SI_PARSER_MESSAGE("m_CurServiceLogoInfo update logo type:%d,Logo char length:%d\n",m_CurServiceLogoInfo.u8logo_transmission_type,m_CurServiceLogoInfo.u16LogoDataLength);
                        MW_SI_PARSER_MESSAGE("logochar is \n");
                        for(MAPI_U8 i=0;i<m_CurServiceLogoInfo.u16LogoDataLength;i++)
                            MW_SI_PARSER_MESSAGE("0x%x",m_CurServiceLogoInfo.pu8LogoData[i]);
                        MW_SI_PARSER_MESSAGE("\n\n");
                    }
#endif
#endif
                }
                else if(INVALID_SERVICE_ID != m_stSdt.astServiceInfo[i].u16ServiceID)
                {
                    pProgDVB = pCMDB->GetByIndex(0);
                    while(pProgDVB)
                    {
                        //The same MUX,ONID and TSID must the same.
                        if((__GetMux(pProgDVB, pCMDB) == __GetMux(pCurProg, pCMDB)) &&
                           (__GetID(pProgDVB, pMux, pNetwork, EN_ID_SID)  == m_stSdt.astServiceInfo[i].u16ServiceID))
                        {
                            if((m_bIsSIDynmaicReScanOff ==MAPI_FALSE)&& (m_eParserBaseType != MW_DVB_SGT_BASE))
                            {
                                __UpdateName(pProgDVB, m_stSdt.astServiceInfo[i].u8ServiceName, pCMDB);
                                __UpdateProviderName(pProgDVB, m_stSdt.astServiceInfo[i].u8ServiceProviderName, pCMDB);
#if (MULTIPLE_SERVICE_NAME_ENABLE == 1)
                                __UpdateMultiName(pProgDVB, m_stSdt.astServiceInfo[i].u8MultiServiceName, m_stSdt.astServiceInfo[i].aeLangIndex, m_stSdt.astServiceInfo[i].u8MultiServiceNameCnt, pCMDB);
#endif
                                pProgDVB->stCHAttribute.u8RealServiceType = m_stSdt.astServiceInfo[i].u8ServiceType;
                            }
#if (GUIDANCE_ENABLE == 1)
                            _AddGuidanceInfoFromSdt(m_stSdt.wOriginalNetwork_ID,m_stSdt.wTransportStream_ID, m_stSdt.astServiceInfo[i].u16ServiceID, m_stSdt.astServiceInfo[i].pu8ServiceGuidance);
#endif
                            if(m_stSdt.astServiceInfo[i].stLinkageInfo.u8Ser_Replacement == MAPI_FALSE)
                            {
                                if (MAPI_TRUE == __UpdateReplacement(pProgDVB, pCMDB))
                                {
                                    bUpdateTS = MAPI_TRUE;
                                }
                            }
                            break;
                        }
                        pProgDVB = pCMDB->GetNext(pProgDVB);
                    }
                    _ReplacementSwitchBackMonitor(&m_stSdt);
                    _CheckNordigSimulRplSrv(&m_stSdt.astServiceInfo[i],m_stSdt.wOriginalNetwork_ID,m_stSdt.wTransportStream_ID);
                }
            }
            if(0xFFFF != u16CurSrvIndex)
            {
                _SetServiceCMG(m_stSdt.astServiceInfo[u16CurSrvIndex].m_CMG, E_CMG_FROM_SDT);
            }
        }
        if (bUpdateTS)
        {
            MAPI_BOOL bCurSrvChg = MAPI_FALSE;
            MONITOR_NOTIFY(E_DVB_TS_CHANGE, &bCurSrvChg, m_pMonitorNotifyUsrParam, NULL);
        }
        if(m_CurRPServiceInfo.bSer_Replacement)
        {
            MONITOR_NOTIFY(E_DVB_SDT_REPLACEMENT, &m_CurRPServiceInfo, m_pMonitorNotifyUsrParam, NULL);
        }
        if(!m_ListRPServiceInfo.empty())
        {
            while (!m_ListRPServiceInfo.empty())
            {
                MW_DVB_RP_SERVICE_INFO RPServiceInfo;
                RPServiceInfo = m_ListRPServiceInfo.front();
                m_ListRPServiceInfo.pop_front();
                MONITOR_NOTIFY(E_DVB_SDT_REPLACEMENT, &RPServiceInfo, m_pMonitorNotifyUsrParam, NULL);
            }
            MONITOR_NOTIFY(E_DVB_TS_CHANGE, NULL, m_pMonitorNotifyUsrParam, NULL);
        }
        if(((m_eParserBaseType == MW_DVB_SDT_PAT_BASE) ||(m_eParserBaseType == MW_DVB_SDT_BASE)
            ||((m_eParserBaseType == MW_DVB_NIT_BASE)&&IS_UPC(m_enCableOperator)))
                && ((m_u8CurSdtVer != m_stSdt.u8Version)
                || (__GetID(pCurProg, pMux, pNetwork, EN_ID_ONID) != m_stSdt.wOriginalNetwork_ID)
#if (STB_ENABLE == 0)
                || (__GetID(pCurProg, pMux, pNetwork, EN_ID_TSID) != m_stSdt.wTransportStream_ID)
                ||(m_u32CurCRCValue != m_stSdt.u32CRCValue)))
        {
            m_u32CurCRCValue = m_stSdt.u32CRCValue;
#else
                || (__GetID(pCurProg, pMux, pNetwork, EN_ID_TSID) != m_stSdt.wTransportStream_ID)))
        {
#endif
            if (m_bIsSIDynmaicReScanOff == MAPI_FALSE)
            {
                m_u8CurSdtVer = m_stSdt.u8Version;
            }
            m_u16NewAddChnNum = 0;
            _ResetInfo(RESET_INFO);
            MW_SI_PARSER_MESSAGE("m_stSdt.wServiceNumber %d\n", m_stSdt.wServiceNumber);
            if(m_stSdt.wServiceNumber == 0)
            {
                return;
            }
            /*
                        printf("onid %x %x tsid %x %x\n",m_pCurProg->m_stProgramInfo.pcProgMux->u16OriginalNetwork_ID,
                            m_stSdt.wOriginalNetwork_ID,m_pCurProg->m_stProgramInfo.pcProgMux->u16TransportStream_ID,
                            m_stSdt.wTransportStream_ID);
            */

#if (HBBTV_ENABLE == 1)
            MW_SI_PARSER_UPDATE("%s ONID %x %x tsid %x %x\n", __FUNCTION__, __GetID(pCurProg, pMux, pNetwork, EN_ID_ONID),
                                m_stSdt.wOriginalNetwork_ID,
                                __GetID(pCurProg, pMux, pNetwork, EN_ID_TSID),
                                m_stSdt.wTransportStream_ID);
#endif
            if((__GetID(pCurProg, pMux, pNetwork, EN_ID_ONID) != m_stSdt.wOriginalNetwork_ID)
                    || (__GetID(pCurProg, pMux, pNetwork, EN_ID_TSID) != m_stSdt.wTransportStream_ID))
            {
                for(int i = 0; i < MAX_CHANNEL_IN_MUX; i++)
                {
                    if(m_stSdt.astServiceInfo[i].u16ServiceID)
                    {
                        m_au16NewProg2Add[m_u16NewAddChnNum] = m_stSdt.astServiceInfo[i].u16ServiceID;
                        m_u16NewAddChnNum++;
                    }
                }
            }
            else
            {
                MW_DTV_CM_DB_scope_lock lock(pCMDB);
                for(int i = 0; i < MAX_CHANNEL_IN_MUX; i++)
                {
                    bNewService = MAPI_TRUE;
                    if(m_stSdt.astServiceInfo[i].u16ServiceID)
                    {
                        pProgDVB = pCMDB->GetByIndex(0);
                        while(pProgDVB)
                        {
                            DVB_MUX *_pMux = __GetMux(pProgDVB,pCMDB);
                            DVB_NETWORK *_pNetwork = __GetNetwork(_pMux,pCMDB);
                            if((__GetID(pProgDVB, _pMux, _pNetwork, EN_ID_ONID) == m_stSdt.wOriginalNetwork_ID)
                                    && (m_bServiceIDUnique || (__GetID(pProgDVB, _pMux, _pNetwork, EN_ID_TSID) == m_stSdt.wTransportStream_ID)))
                            {
                                if(m_stSdt.astServiceInfo[i].u16ServiceID == __GetID(pProgDVB, _pMux, _pNetwork, EN_ID_SID))
                                {
                                    if (pMux == _pMux) // check whether service move from different mux
                                    {
                                        bNewService = MAPI_FALSE;
                                    }
                                    break;
                                }
                            }
                            pProgDVB = pCMDB->GetNext(pProgDVB);
                        }
                        if(bNewService)
                        {
 #if (NCD_ENABLE == 1)
                            if(_AddRelocatedServiceToList(&m_stSdt, i, pCMDB) == MAPI_FALSE)  // not add new service to prog list immediately and append it to relocated list if needed
#endif
                            {
                                m_au16NewProg2Add[m_u16NewAddChnNum] = m_stSdt.astServiceInfo[i].u16ServiceID;
                                m_u16NewAddChnNum++;
                            }
                        }
                    }
#if(GUIDANCE_ENABLE ==1)
                    ST_TRIPLE_ID tripleid;
                    tripleid.u16OnId =m_stSdt.wOriginalNetwork_ID;
                    tripleid.u16TsId =m_stSdt.wTransportStream_ID;
                    tripleid.u16SrvId=m_stSdt.astServiceInfo[i].u16ServiceID;
                    _ResetGuidanceInfoByTripleIds(tripleid,m_stSdt.astServiceInfo[i].pu8ServiceGuidance);
#endif

                }
            }

            if((m_bIsSIDynmaicReScanOff ==MAPI_TRUE)||(MAPI_TRUE == m_bIsSIAutoUpdateOff) ||(MAPI_TRUE == m_stUpdateStatus.bDisableAutoUpdate))
            {
                m_u16NewAddChnNum = 0;
                return;
            }

            if(m_u16NewAddChnNum == 0)
            {
                MAPI_U16 u16SID = __GetID(m_pCurProg, pMux, pNetwork, EN_ID_SID);
                MAPI_U16 u16PMTPID = __GetID(m_pCurProg, pMux, pNetwork, EN_ID_PMT);
                MAPI_U16 onid,tsid,sid;
                MW_DTV_CM_DB_scope_lock lock(pCMDB);
                onid=__GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_ONID);
                tsid=__GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_TSID);
                sid=__GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_SID);
                if (_RemoveMismatchCH(pCurProg, pCMDB, m_stSdt.astServiceInfo, pMux, pNetwork))
                {
                    pCMDB->ReArrangeNumber();
                    _UpdateCurrentProgram();
                    if((sid != __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_SID)) ||
                        (tsid != __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_TSID)) ||
                        (onid != __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_ONID)))
                    {
                        bCurSrvChg = MAPI_TRUE;
                    }
                    bUpdate = MAPI_TRUE;
                    if(m_u32ServiceMoveTimer == INVALID_TIME)
                    {
                        if(u16SID != __GetID(m_pCurProg, pMux, pNetwork, EN_ID_SID))
                        {
                            m_u16OldSID = u16SID;
                            m_u16OldPMTPID = u16PMTPID;
                            m_u32WaitPMTTimer = _GetTime0();
                        }
                    }
                }
                else if (MAPI_TRUE == bRearrange)
                {
                    pCMDB->ReArrangeNumber();
                    _UpdateCurrentProgram();
                }
                __SetVer(m_pCurProg, EN_VER_SDT, m_u8CurSdtVer);
                if(bUpdate)
                {
                    MONITOR_NOTIFY(E_DVB_TS_CHANGE, &bCurSrvChg, m_pMonitorNotifyUsrParam, NULL);
                }

            }
            else
            {
                m_stPat.u8Version = m_u8CurPatVer = INVALID_PSI_SI_VERSION;
                m_stNit.u8Version = m_u8CurNitVer = INVALID_PSI_SI_VERSION;
                m_u32PatMonitorTimer = m_u32NitMonitorTimer = 0;
                if (m_bEnableBouquetFilter)
                {
                    m_stBat.u8Version = INVALID_PSI_SI_VERSION;
                    m_u32BatMonitorTimer = 0;
                }
                m_u32StartUpdateTime = _GetTime0();
            }
        }
    }


}


void MW_DVB_SI_PSI_Parser::_SdtOtherReady(DVB_PROG *pCurProg, DVB_CM *pCMDB, DVB_MUX *pMux, DVB_NETWORK *pNetwork)
{
    MAPI_SI_TABLE_SDT stSdtOtherTmp;
    MAPI_SI_TABLE_SDT *pstSdtOther=NULL;
    DVB_PROG* pProgDVB;
    DVB_PROG* pProgDVBTmp = NULL;
    MAPI_BOOL bRemove = MAPI_FALSE;
    MAPI_BOOL bUpdate = MAPI_FALSE;
    MAPI_BOOL bRearrange = MAPI_FALSE;
    MW_SI_PARSER_UPDATE("%s m_u32ServiceRelocTimer %d\n", __FUNCTION__, m_u32ServiceRelocTimer);
    memset(&stSdtOtherTmp, 0, sizeof(stSdtOtherTmp));
    if ((m_eParserMode == E_DVB_QUICK_SCAN) && (m_eParserType == MW_DVB_C_PARSER))
    {
        MAPI_U16 u16TsNum = m_stNit.u16TSNumber;
        for (MAPI_U16 u16TsIndex=0;u16TsIndex<u16TsNum;u16TsIndex++)
        {
            if ((m_pstAllSdt[u16TsIndex].wOriginalNetwork_ID == INVALID_ON_ID) &&
                (m_pstAllSdt[u16TsIndex].wTransportStream_ID == INVALID_TS_ID))
            {
                pstSdtOther = &m_pstAllSdt[u16TsIndex];
                break;
            }
        }
    }
    else
    {
        pstSdtOther = &stSdtOtherTmp;
    }
    if(NULL == pstSdtOther)
    {
        MW_SI_PARSER_ERROR("_SdtOtherReady malloc failed\n");
        _DeleteParser((MAPI_U32)m_pSdtOtherParser , MAPI_FALSE);
        return;
    }

    if (MAPI_FALSE == m_pSdtOtherParser->GetTable(*pstSdtOther))
    {
        _DeleteParser((MAPI_U32)m_pSdtOtherParser , MAPI_FALSE);
        return;
    }
    table_release sdt(pstSdtOther, table_release::E_TABLE_SDT);
    sdt.setReleaseType(RELEASE_ALL);


    //printf("pstSdtOther->wTransportStream_ID....%x\n",pstSdtOther->wTransportStream_ID);
    for(MAPI_U16 i = 0; i < m_stSdtOtherInfo.stSdtOtherTS.size(); i++)
    {
        if(m_stSdtOtherInfo.stSdtOtherTS.at(i).u16TSID == pstSdtOther->wTransportStream_ID)
        {
            if ( m_bIsSIDynmaicReScanOff == MAPI_FALSE)
            {
                m_stSdtOtherInfo.stSdtOtherTS.at(i).u8SdtVer = pstSdtOther->u8Version;
            }
            //printf("update sdt other ver %x\n",stSdtOther.u8Version);
            break;
        }
    }
    MW_SI_PARSER_MESSAGE("get SDT other ver ts %x %x\n", pstSdtOther->wTransportStream_ID, pstSdtOther->u8Version);
    //printf("sdt tsid %x\n",m_stSdt.wTransportStream_ID);
    _DeleteParser((MAPI_U32)m_pSdtOtherParser , MAPI_FALSE);
    MW_SI_PARSER_MESSAGE("s16OpenFilter %d\n", m_s16OpenFilter);

#if (CI_PLUS_ENABLE == 1)
    if(m_bIsOpMode == MAPI_TRUE)
    {
        return;
    }
#endif
    if(E_DVB_MONITOR == m_eParserMode)
    {
        BOOL bIsZiggo = _IsZiggoOperator();
        DVB_MUX* _pMux;

        // update onid directly when onid in cmdb is invalid
        MAPI_U16 u16CmdbTsPos;
        MW_DTV_CM_DB_scope_lock lock(pCMDB);
        for (u16CmdbTsPos=0; u16CmdbTsPos<pCMDB->GetMaxMuxNum(); u16CmdbTsPos++)
        {
            _pMux = pCMDB->GetValidMux(u16CmdbTsPos);
            if (_pMux)
            {
                if (INVALID_ON_ID == _pMux->u16OriginalNetwork_ID)
                {
                    if (pstSdtOther->wTransportStream_ID == _pMux->u16TransportStream_ID)
                    {
                        _pMux->u16OriginalNetwork_ID = pstSdtOther->wOriginalNetwork_ID;
                        pCMDB->UpdateMux(_pMux);
                        break;
                    }
                }
            }
        }
        // update onid directly when onid in cmdb is invalid - END

        _pMux = NULL;
        for(int i = 0; i < pstSdtOther->wServiceNumber; i++)
        {
            if(pstSdtOther->astServiceInfo[i].u16ServiceID)
            {
                pProgDVB = pCMDB->GetByID(pstSdtOther->wTransportStream_ID, pstSdtOther->wOriginalNetwork_ID, pstSdtOther->astServiceInfo[i].u16ServiceID);
                if(pProgDVB)
                {
                    if(pProgDVBTmp == NULL)pProgDVBTmp = pProgDVB;
                    _pMux = __GetMux(pProgDVB, pCMDB);
                    break;
                }
            }
        }
        _ReplacementSwitchBackMonitor(pstSdtOther);

        if(_pMux == NULL)
        {
            _pMux = pCMDB->GetMux(pstSdtOther->wTransportStream_ID, pstSdtOther->wOriginalNetwork_ID);
        }

        if(NULL != _pMux)
        {
            MAPI_U16 addCnt = 0;
            MAPI_U16 u16CellID = _pMux->u16CellID;

            for(int i = 0; i < pstSdtOther->wServiceNumber; i++)
            {
                if(pstSdtOther->astServiceInfo[i].u16ServiceID && (MAPI_TRUE == _CheckSrvInAvailCell(pstSdtOther,u16CellID, pstSdtOther->astServiceInfo[i].u16ServiceID)))
                {
                    pProgDVB = pCMDB->GetByID(pstSdtOther->wTransportStream_ID, pstSdtOther->wOriginalNetwork_ID, pstSdtOther->astServiceInfo[i].u16ServiceID);

                    MW_SI_PARSER_UPDATE("%s  sid %x relocated %x\n", __FUNCTION__, pstSdtOther->astServiceInfo[i].u16ServiceID, pstSdtOther->astServiceInfo[i].bSrvRelocated);
                    if((INVALID_TIME != m_u32ServiceRelocTimer) && pstSdtOther->astServiceInfo[i].bSrvRelocated)
                    {
                        if((m_CurServiceInfo.stServiceRelocInfo.u16OldOnID == pstSdtOther->astServiceInfo[i].stServiceRelocated.u16OldOnID) &&
                                (m_CurServiceInfo.stServiceRelocInfo.u16OldTsID == pstSdtOther->astServiceInfo[i].stServiceRelocated.u16OldTsID) &&
                                (m_CurServiceInfo.stServiceRelocInfo.u16OldSrvID == pstSdtOther->astServiceInfo[i].stServiceRelocated.u16OldSrvID))
                        {
                            MW_SI_PARSER_UPDATE("got relocated service.......\n");
                            m_CurServiceInfo.stServiceRelocInfo.u16OldOnID = pstSdtOther->wOriginalNetwork_ID;
                            m_CurServiceInfo.stServiceRelocInfo.u16OldTsID = pstSdtOther->wTransportStream_ID;
                            m_CurServiceInfo.stServiceRelocInfo.u16OldSrvID = pstSdtOther->astServiceInfo[i].u16ServiceID;
                        }
                    }

                    if(pProgDVB)
                    {
#if (GUIDANCE_ENABLE == 1)
                        _AddGuidanceInfoFromSdt(pstSdtOther->wOriginalNetwork_ID,pstSdtOther->wTransportStream_ID, pstSdtOther->astServiceInfo[i].u16ServiceID, pstSdtOther->astServiceInfo[i].pu8ServiceGuidance);
#endif
                        if (MAPI_FALSE == m_bIsSIDynmaicReScanOff )
                        {
                            DVB_PROG* pTempProgDVB = NULL;
                            pTempProgDVB = pProgDVB;
                            do
                            {
                                if ((m_eParserBaseType == MW_DVB_SDT_PAT_BASE) ||(m_eParserBaseType == MW_DVB_SDT_BASE)
                                ||((m_eParserBaseType == MW_DVB_NIT_BASE) && (!(bIsZiggo && (pProgDVB->u8ServiceName[0] !='\0'))||IS_UPC(m_enCableOperator))))
                                {
                                    MAPI_SI_SERVICETYPE enOldRealSrvType = E_TYPE_INVALID;
                                    MEMBER_SERVICETYPE enOldSrvType = E_SERVICETYPE_INVALID;
                                    enOldRealSrvType = (MAPI_SI_SERVICETYPE)pTempProgDVB->stCHAttribute.u8RealServiceType;
                                    enOldSrvType = (MEMBER_SERVICETYPE)pTempProgDVB->stCHAttribute.u8ServiceType;
                                    _UpdateServiceType(&pTempProgDVB->stCHAttribute.u8ServiceType, &pTempProgDVB->stCHAttribute.u8ServiceTypePrio,
                                                        pstSdtOther->astServiceInfo[i].u8ServiceType);
                                    if ((pstSdtOther->astServiceInfo[i].u8ServiceType ==  E_TYPE_DATA) || (pstSdtOther->astServiceInfo[i].u8ServiceType == E_TYPE_MHP))
                                    {
                                        if(MAPI_FALSE == _IsSpecificSupport(E_DVB_DATA_SERVICE_SUPPORT,&m_eCountry, NULL))
                                        {
                                            pTempProgDVB->stCHAttribute.u8NumericSelectionFlag = MAPI_FALSE;
                                            pTempProgDVB->stCHAttribute.u8VisibleServiceFlag = MAPI_FALSE;
                                        }
                                    }
                                    pTempProgDVB->stCHAttribute.u8RealServiceType = pstSdtOther->astServiceInfo[i].u8ServiceType;
                                    if ((enOldRealSrvType != pTempProgDVB->stCHAttribute.u8RealServiceType)
                                        || (enOldSrvType != pTempProgDVB->stCHAttribute.u8ServiceType))
                                    {
                                        bUpdate = MAPI_TRUE;
                                        pCMDB->Update(pTempProgDVB);
                                        if (enOldSrvType != pTempProgDVB->stCHAttribute.u8ServiceType)
                                        {
                                            // service type change, need rearrange the programs
                                            bRearrange = MAPI_TRUE;
                                        }
                                    }
                                }

                                //According to additional Notes with regard to Ziggo requirements v1 40.pdf
                                //we cannot use SDT_Other to update service name.
                                if(!(bIsZiggo && (pProgDVB->u8ServiceName[0] !='\0')))
                                {
                                    __UpdateName(pTempProgDVB, pstSdtOther->astServiceInfo[i].u8ServiceName, pCMDB);
                                    __UpdateProviderName(pTempProgDVB, pstSdtOther->astServiceInfo[i].u8ServiceProviderName, pCMDB);
#if (MULTIPLE_SERVICE_NAME_ENABLE == 1)
                                    __UpdateMultiName(pTempProgDVB, pstSdtOther->astServiceInfo[i].u8MultiServiceName,
                                                    pstSdtOther->astServiceInfo[i].aeLangIndex,
                                                    pstSdtOther->astServiceInfo[i].u8MultiServiceNameCnt, pCMDB);
#endif
                                }
                                pTempProgDVB = pCMDB->GetByIDNext(pTempProgDVB, pstSdtOther->wTransportStream_ID,
                                                                        pstSdtOther->wOriginalNetwork_ID,
                                                                        pstSdtOther->astServiceInfo[i].u16ServiceID);
                            }while((pTempProgDVB!=NULL) && (m_eParserType == MW_DVB_T_PARSER)
                                                && (m_eCountry == E_FINLAND));
                        }
                        // check service type changed or not - END

                        if(pstSdtOther->astServiceInfo[i].stLinkageInfo.u8Ser_Replacement == MAPI_FALSE)
                        {
                            if (MAPI_TRUE == __UpdateReplacement(pProgDVB, pCMDB))
                            {
                                bUpdate = MAPI_TRUE;
                            }
                        }
                    }
                    else if ((MAPI_FALSE == m_bIsSIDynmaicReScanOff )&&((m_eParserBaseType == MW_DVB_SDT_PAT_BASE) ||(m_eParserBaseType == MW_DVB_SDT_BASE)
                                                                         ||((m_eParserBaseType == MW_DVB_NIT_BASE)&&IS_UPC(m_enCableOperator))))
                    {
#if (NCD_ENABLE == 1)
                        if(_AddRelocatedServiceToList(pstSdtOther, i, pCMDB) == MAPI_FALSE)  // not add new service to prog list immediately and append it to relocated list if needed
#endif
                        {
                            DVB_PROG* pNew = NULL;
                            MW_DVB_PROGRAM_INFO ServiceInfo;
                            memset(&ServiceInfo,0,sizeof(MW_DVB_PROGRAM_INFO));
                            ServiceInfo.u16LCN = DEFAULT_LCN;
                            ServiceInfo.u16SimuLCN = DEFAULT_SIMU_LCN;
                            ServiceInfo.bIsVisible = MAPI_TRUE;
                            ServiceInfo.bIsSelectable = MAPI_TRUE;

                            if(m_stNit.u8Version != INVALID_PSI_SI_VERSION)
                            {
                                MAPI_BOOL bGot = MAPI_FALSE;
                                for(int j = 0; j < m_stNit.u16TSNumber; j++)
                                {
                                    if(m_stNit.pstTSInfo[j].wTransportStream_ID == pstSdtOther->wTransportStream_ID)
                                    {
                                        for(int k = 0; k < MAX_CHANNEL_IN_MUX; k++)
                                        {
                                            if(m_stNit.pstTSInfo[j].astLcnInfo[k].u16ServiceID == pstSdtOther->astServiceInfo[i].u16ServiceID)
                                            {
                                                ServiceInfo.u16LCN = m_stNit.pstTSInfo[j].astLcnInfo[k].u16LCNNumber;
                                                ServiceInfo.u16SimuLCN = m_stNit.pstTSInfo[j].astLcnInfo[k].u16SimuLCNNumber;
                                                ServiceInfo.bIsVisible = m_stNit.pstTSInfo[j].astLcnInfo[k].bIsVisable;
                                                ServiceInfo.bIsSelectable = m_stNit.pstTSInfo[j].astLcnInfo[k].bIsSelectable;
                                                ServiceInfo.bIsSpecialSrv = m_stNit.pstTSInfo[j].astLcnInfo[k].bIsSpecialSrv;
                                                bGot = MAPI_TRUE;
                                                break;
                                            }
                                        }
                                    }
                                    if(bGot)
                                    {
                                        break;
                                    }
                                }
                            }
                            MW_SI_PARSER_UPDATE("sdt other Add\n");
                            ServiceInfo.u16ServiceID = pstSdtOther->astServiceInfo[i].u16ServiceID;
                            ServiceInfo.u8ServiceType = pstSdtOther->astServiceInfo[i].u8ServiceType;
                            _UpdateServiceType(&ServiceInfo.u8ServiceType, &ServiceInfo.u8ServiceTypePrio, pstSdtOther->astServiceInfo[i].u8ServiceType);
                            _UpdateProgInfoByServiceType(pstSdtOther->astServiceInfo[i].u8ServiceType, NULL, &ServiceInfo);
                            ServiceInfo.u8RealServiceType = pstSdtOther->astServiceInfo[i].u8ServiceType;

                            if((m_eParserBaseType == MW_DVB_NIT_BASE)&&IS_UPC(m_enCableOperator)
                                &&((ServiceInfo.u16LCN ==DEFAULT_LCN)||(ServiceInfo.u16LCN ==0)))
                            {
                                ServiceInfo.bIsSelectable = MAPI_FALSE;
                                ServiceInfo.bIsVisible = MAPI_FALSE;
                            }

                            MW_SI_PARSER_UPDATE("add service.....sid %x\n", ServiceInfo.u16ServiceID);
                            memcpy(ServiceInfo.au8ServiceName, pstSdtOther->astServiceInfo[i].u8ServiceName, MAPI_SI_MAX_SERVICE_NAME);
                            memcpy(ServiceInfo.au8ServiceProviderName, pstSdtOther->astServiceInfo[i].u8ServiceProviderName, MAPI_SI_MAX_PROVIDER_NAME);
#if (MULTIPLE_SERVICE_NAME_ENABLE == 1)
                            ServiceInfo.u8MultiServiceNameCnt = (pstSdtOther->astServiceInfo[i].u8MultiServiceNameCnt > MAX_MULTILINGUAL_SERVICE_NAME) ? MAX_MULTILINGUAL_SERVICE_NAME : pstSdtOther->astServiceInfo[i].u8MultiServiceNameCnt;
                            for(int j=0; j<ServiceInfo.u8MultiServiceNameCnt; j++)
                            {
                                ServiceInfo.aeLangIndex[j] = pstSdtOther->astServiceInfo[i].aeLangIndex[j];
                                memcpy(ServiceInfo.u8MultiServiceName[j], pstSdtOther->astServiceInfo[i].u8MultiServiceName[j], MAPI_SI_MAX_SERVICE_NAME);
                            }
#endif
                            //CableReady Manual scanning shall set SI updates to "OFF" on all physical channels.
                            if ((MAPI_FALSE == m_bIsSIAutoUpdateOff ) && (MAPI_FALSE == m_stUpdateStatus.bDisableAutoUpdate))
                            {
                                pNew = (DVB_PROG*)_AddSdtOtherProgram(_pMux, &ServiceInfo);
                                if(pNew)addCnt++;
                            }
                        }
                        //printf("sdt other Add....%d\n",addCnt);
                    }
                }
                _CheckNordigSimulRplSrv(&pstSdtOther->astServiceInfo[i],pstSdtOther->wOriginalNetwork_ID,pstSdtOther->wTransportStream_ID);
            }
            if(!m_ListRPServiceInfo.empty())
            {
                while (!m_ListRPServiceInfo.empty())
                {
                    MW_DVB_RP_SERVICE_INFO RPServiceInfo;
                    RPServiceInfo = m_ListRPServiceInfo.front();
                    m_ListRPServiceInfo.pop_front();
                    MONITOR_NOTIFY(E_DVB_SDT_REPLACEMENT, &RPServiceInfo, m_pMonitorNotifyUsrParam, NULL);
                }
                MONITOR_NOTIFY(E_DVB_TS_CHANGE, NULL, m_pMonitorNotifyUsrParam, NULL);

            }

            if ((m_eParserBaseType == MW_DVB_SDT_PAT_BASE) ||(m_eParserBaseType == MW_DVB_SDT_BASE)
                ||((m_eParserBaseType == MW_DVB_NIT_BASE)&&IS_UPC(m_enCableOperator)))
            {
                //CableReady Manual scanning shall set SI updates to "OFF" on all physical channels.
                if ((MAPI_FALSE == m_bIsSIDynmaicReScanOff )&& (MAPI_FALSE == m_bIsSIAutoUpdateOff ) && (MAPI_FALSE == m_stUpdateStatus.bDisableAutoUpdate))
                {
                    if(pProgDVBTmp != NULL)
                    {
                        bRemove = _RemoveMismatchCH(pProgDVBTmp, pCMDB, pstSdtOther->astServiceInfo, _pMux, pNetwork);
                    }

                    if(addCnt > 0 || bRemove == MAPI_TRUE)
                    {
                        pCMDB->ReArrangeNumber();
                        _UpdateCurrentProgram();
                        bUpdate = MAPI_TRUE;
                    }
                    else if (MAPI_TRUE == bRearrange)
                    {
                        pCMDB->ReArrangeNumber();
                        _UpdateCurrentProgram();
                    }
                }
            }
        }
    }

    if(bUpdate)
    {
        MAPI_BOOL bCurSrvChg = MAPI_FALSE;
        MONITOR_NOTIFY(E_DVB_TS_CHANGE, &bCurSrvChg, m_pMonitorNotifyUsrParam, NULL);
    }

    if(INVALID_TIME != m_u32ServiceMoveTimer)
    {
        MW_SI_PARSER_UPDATE("notify service move type cost time %d %d onid %x tsid %x sid %x\n", _Timer_DiffTimeFromNow(m_u32ServiceMoveTimer), m_CurServiceInfo.eChangeType , m_stCurPmt.stServiceMove.u16NewONId,
                            m_stCurPmt.stServiceMove.u16NewTSId, m_stCurPmt.stServiceMove.u16NewServiceId);

        pProgDVB = pCMDB->GetByID(m_stCurPmt.stServiceMove.u16NewTSId, m_stCurPmt.stServiceMove.u16NewONId, m_stCurPmt.stServiceMove.u16NewServiceId);
        if(pProgDVB != NULL)
        {
            //printf("add onid %x tsid %x sid %x\n",m_stCurPmt.stServiceMove.u16NewONId,m_stCurPmt.stServiceMove.u16NewTSId,m_stCurPmt.stServiceMove.u16NewServiceId);
            //pCMDB->SetCurr(pProgDVB);
            //m_pCurProg=pProgDVB;

            m_CurServiceInfo.eChangeType = MW_SERVICE_MOVE;
            MONITOR_NOTIFY(E_DVB_PMT_SERVICE_CHANGE, &m_CurServiceInfo, m_pMonitorNotifyUsrParam, NULL);

            m_CurServiceInfo.eChangeType = MW_SERVICE_NO_CHANGE;

            m_u32ServiceMoveTimer = INVALID_TIME;
        }
    }
    else if(INVALID_TIME != m_u32ServiceRelocTimer)
    {
        MW_SI_PARSER_UPDATE("notify service relocate type cost time %d %d onid %x tsid %x sid %x\n", _Timer_DiffTimeFromNow(m_u32ServiceRelocTimer),
                            m_CurServiceInfo.eChangeType , m_CurServiceInfo.stServiceRelocInfo.u16OldOnID, m_CurServiceInfo.stServiceRelocInfo.u16OldTsID,
                            m_CurServiceInfo.stServiceRelocInfo.u16OldSrvID);

        pProgDVB = pCMDB->GetByID(m_CurServiceInfo.stServiceRelocInfo.u16OldTsID, m_CurServiceInfo.stServiceRelocInfo.u16OldOnID, m_CurServiceInfo.stServiceRelocInfo.u16OldSrvID);
        if(pProgDVB != NULL)
        {
            //pCMDB->SetCurr(pProgDVB);
            //m_pCurProg=pProgDVB;
            m_CurServiceInfo.eChangeType = MW_SERVICE_RELOCATE;
            MONITOR_NOTIFY(E_DVB_PMT_SERVICE_CHANGE, &m_CurServiceInfo, m_pMonitorNotifyUsrParam, NULL);
            m_CurServiceInfo.eChangeType = MW_SERVICE_NO_CHANGE;
            m_u32ServiceRelocTimer = INVALID_TIME;
        }
    }
}


void MW_DVB_SI_PSI_Parser::_UpdateTS(DVB_PROG* pCurProg, DVB_CM *pCMDB , DVB_MUX *pMux, DVB_NETWORK *pNetwork)
{
    MAPI_BOOL bAddCH;
    DVB_PROG *pProg, *pNewCH = NULL;
    MAPI_BOOL bDifferentTS = MAPI_FALSE;
    //void* pMux=__GetMux(pCurProg,pCMDB);
    MAPI_U16 u16AddCnt = 0;
    MAPI_S32 s32CurrentIndex = -1;
    MAPI_BOOL bCHRemoved = MAPI_FALSE;
    MAPI_BOOL bCurSrvChg = MAPI_FALSE;
#if (MSTAR_TVOS != 1)
    MAPI_U16 u16NewONID = 0, u16NewTSID = 0, u16NewSID = 0;
#endif
    MAPI_U16 onid,tsid,sid;
    onid=__GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_ONID);
    tsid=__GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_TSID);
    sid=__GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_SID);
    if(m_pCurPmtParser)//don't del parser in cm lock mode
    {
        _DeleteParser((MAPI_U32)m_pCurPmtParser, MAPI_FALSE);
    }
    if(m_stNit.u8Version == INVALID_PSI_SI_VERSION)
    {
        m_TsInfo.u16NID = __GetID(pCurProg, pMux, pNetwork, EN_ID_NID);
    }
    if((m_stSdt.u8Version == INVALID_PSI_SI_VERSION)
            && (m_stNit.u8Version == INVALID_PSI_SI_VERSION))
    {
        m_TsInfo.u16ONID = __GetID(pCurProg, pMux, pNetwork, EN_ID_ONID);
    }

    if((__GetID(pCurProg, pMux, pNetwork, EN_ID_NID) != m_TsInfo.u16NID) ||
            (__GetID(pCurProg, pMux, pNetwork, EN_ID_TSID) != m_TsInfo.u16TSID) ||
            (__GetID(pCurProg, pMux, pNetwork, EN_ID_ONID) != m_TsInfo.u16ONID))
    {
        bDifferentTS = MAPI_TRUE;
    }
    //SETID(pCurProg,EN_ID_NID,m_TsInfo.u16NID);
    //SETID(pCurProg,EN_ID_TSID,m_TsInfo.u16TSID);
    //SETID(pCurProg,EN_ID_ONID,m_TsInfo.u16ONID);
    MW_SI_PARSER_UPDATE("MW_DVB_SI_PSI_Parser::_UpdateTS m_u16NewAddChnNum %d\n", m_u16NewAddChnNum);

    //pCMDB->Update(pCurProg);
    MW_DTV_CM_DB_scope_lock lock(pCMDB);

    for(int i = 0; i < m_u16NewAddChnNum; i++)
    {
        if(bDifferentTS == MAPI_TRUE)
        {
            DVB_PROG* pNew;
            pNew = (DVB_PROG*)_AddProgram(&m_TsInfo, &m_ProgramInfo[i]);
#if 0
            if((pNewCH == NULL) && pNew)
            {
                pNewCH = pNew;
                u16NewONID = m_TsInfo.u16ONID;
                u16NewTSID = m_TsInfo.u16TSID;
                u16NewSID = m_ProgramInfo[i].u16ServiceID;
            }
#else
            MW_SI_PARSER_UPDATE("add service=>>point %d type %d\n",(MAPI_U32)pNew, m_ProgramInfo[i].u8ServiceType);
            if((s32CurrentIndex == -1) && pNew && (m_ProgramInfo[i].u8ServiceType != E_SERVICETYPE_INVALID))
            {
                s32CurrentIndex = pCMDB->Size() - 1;
                #if (MSTAR_TVOS != 1)
                u16NewONID = m_TsInfo.u16ONID;
                u16NewTSID = m_TsInfo.u16TSID;
                u16NewSID = m_ProgramInfo[i].u16ServiceID;
                #endif
            }
#endif
            continue;
        }
        bAddCH = MAPI_TRUE;
        pProg = pCMDB->GetByIndex(0);
        while(pProg)
        {
            if((__GetMux(pProg, pCMDB) == pMux) && (__GetID(pProg, pMux, pNetwork, EN_ID_SID) == m_au16NewProg2Add[i]))
            {
                MW_SI_PARSER_UPDATE("update\n");
                _UpdateProgram(pProg, &m_ProgramInfo[i]);
                pCMDB->Update(pProg);
                bAddCH = MAPI_FALSE;
                break;
            }
            pProg = pCMDB->GetNext(pProg);
        }
        if(bAddCH)
        {
            MW_SI_PARSER_UPDATE("Add\n");
            if(_AddProgram(&m_TsInfo, &m_ProgramInfo[i]))
            {
                u16AddCnt++;
            }
        }
    }


    if(bDifferentTS == MAPI_TRUE)
    {
        if(s32CurrentIndex == -1)
        {
            s32CurrentIndex = pCMDB->Size() - 1;
        }
        if(s32CurrentIndex >= 0)
        {
            MAPI_S32 s32MatchIdx = pCMDB->Size()-1;
            ASSERT(s32MatchIdx>=0);
            do
            {
                pProg = pCMDB->GetByIndex(s32MatchIdx);
                ASSERT(pProg);
                if(__GetMux(pProg, pCMDB) == pMux)
                {
                    MW_SI_PARSER_UPDATE("delete service ====>%s\n",pProg->u8ServiceName);
                    pCMDB->Delete(pProg);
                    s32CurrentIndex--;
                }
                s32MatchIdx--;
            }while((pProg != NULL) && (s32MatchIdx >= 0));

            //error handling
            if (s32CurrentIndex < 0)
            {
                s32CurrentIndex = 0;
            }
            pNewCH = pCMDB->GetByIndex((U16)s32CurrentIndex);
            //ASSERT(pNewCH);
            if(pNewCH)
            {
                pCMDB->SetCurr(pNewCH);
                m_pCurProg = pCMDB->GetCurr();
                m_pCurMux = __GetMux(m_pCurProg, m_pCMDB);
                if(m_pCurMux == NULL)
                {
                    MW_SI_PARSER_ERROR("get mux failed\n");
                    ASSERT(0);
                }
                m_pCurNetwork = __GetNetwork(m_pCurMux, m_pCMDB);
                if(m_pCurNetwork == NULL)
                {
                    MW_SI_PARSER_ERROR("get network failed\n");
                    ASSERT(0);
                }
                m_stCurPmt.u8Version = SI_PSI_FORCE_UPDATE_VER;
                m_u32PmtMonitorTimer = 0;
            }
            /*if(m_pCurPmtParser)//don't del parser in cm lock mode
            {
                _DeleteParser((MAPI_U32)m_pCurPmtParser,MAPI_FALSE);
            }*/

            //_ResetInfo(RESET_EIT);

        }
        else
        {
            MW_SI_PARSER_ERROR("TS change, but no support service \n");
            //ASSERT(0);
        }
    }
    else
    {
        if(m_eParserBaseType != MW_DVB_PAT_BASE)
        {
            bCHRemoved = _RemoveMismatchCH(pCurProg, pCMDB, m_stSdt.astServiceInfo, pMux, pNetwork);
        }
        else
        {
            bCHRemoved = _RemoveMismatchCH(pCurProg, pCMDB, m_stPat.ServiceIDInfo, pMux, pNetwork);
        }
    }
    if(bDifferentTS || bCHRemoved || u16AddCnt)
    {
        MAPI_S32 s32Idx = pCMDB->Size();
        if(s32Idx)
        {
            pCMDB->ReArrangeNumber();
            _UpdateCurrentProgram();
            if(bDifferentTS && pNewCH)
            {
                #if (MSTAR_TVOS == 1)
                m_pCurProg = pCMDB->GetByIndex(0);
                #else
                m_pCurProg = pCMDB->GetByID(u16NewTSID, u16NewONID, u16NewSID);
                if(m_pCurProg == NULL)
                {
                    m_pCurProg = pCMDB->GetByID(u16NewONID, u16NewSID);
                    if(m_pCurProg == NULL)
                    {
                        m_pCurProg = pCMDB->GetByIndex(0);
                    }
                }
                #endif
                bCurSrvChg = MAPI_TRUE;
                ASSERT(m_pCurProg);
                pCMDB->SetCurr(m_pCurProg);
                _UpdateCurrentProgram();
            }
            else if((sid != __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_SID)) ||
                (tsid != __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_TSID)) ||
                (onid != __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_ONID)))
            {

                bCurSrvChg = MAPI_TRUE;
            }
        }
        else
        {
            bCurSrvChg = MAPI_TRUE;
        }

        MONITOR_NOTIFY(E_DVB_TS_CHANGE, &bCurSrvChg, m_pMonitorNotifyUsrParam, NULL);
    }


    if(m_u32PmtMonitorTimer == 0) //don't del parser in cm lock mode
    {
        _ResetInfo(RESET_EIT);
    }

}

#if (NCD_ENABLE == 1)
void MW_DVB_SI_PSI_Parser::_UpdateTSOther(ST_TRIPLE_ID *pstTripleId, MW_DVB_PROGRAM_INFO *pstProgInfo, DVB_CM *pCMDB)
{
    if((m_bIsSIAutoUpdateOff == MAPI_FALSE) && (m_stUpdateStatus.bDisableAutoUpdate == MAPI_FALSE))
    {
        DVB_CM* pMux;
        MW_DTV_CM_DB_scope_lock lock(pCMDB);
        pMux = (DVB_CM*)pCMDB->GetMux(pstTripleId->u16TsId, pstTripleId->u16OnId);
        if(_AddSdtOtherProgram(pMux, pstProgInfo) != NULL)
        {
            if((m_eParserBaseType == MW_DVB_SDT_PAT_BASE) ||(m_eParserBaseType == MW_DVB_SDT_BASE) \
                ||((m_eParserBaseType == MW_DVB_NIT_BASE) && IS_UPC(m_enCableOperator)))
            {
                if (MAPI_FALSE == m_bIsSIDynmaicReScanOff)
                {
                    MAPI_BOOL bCurSrvChg = MAPI_FALSE;
                    pCMDB->ReArrangeNumber();
                    _UpdateCurrentProgram();
                    MONITOR_NOTIFY(E_DVB_TS_CHANGE, &bCurSrvChg, m_pMonitorNotifyUsrParam, NULL);
                }
            }
        }
    }
}
#endif

template<class _SERVICE>
MAPI_BOOL MW_DVB_SI_PSI_Parser::_RemoveMismatchCH(DVB_PROG *pCurProg, DVB_CM *pCMDB, _SERVICE *pService, DVB_MUX *pMux, DVB_NETWORK *pNetwork)
{
    MAPI_BOOL bCurChExist = MAPI_FALSE;
    MAPI_BOOL bRemove = MAPI_FALSE;
    //void* pMux=__GetMux(pCurProg,pCMDB);
    //_PROGRAM* pProg;
    DVB_PROG* pFirstProg = NULL;
    DVB_PROG* pNextProg;
    MAPI_U16 u16RemoveCnt = 0;
    MAPI_U16 u16OldONID, u16OldTSID, u16OldSID, u16NewSID=INVALID_SERVICE_ID;

    u16OldONID = __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_ONID);
    u16OldTSID = __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_TSID);
    u16OldSID = __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_SID);

    if (pMux == m_pCurMux)
    {
        for(int i = 0; i < MAX_CHANNEL_IN_MUX; i++)
        {
            if(pService[i].u16ServiceID == __GetID(pCurProg, pMux, pNetwork, EN_ID_SID))
            {
                bCurChExist = MAPI_TRUE;
                MW_SI_PARSER_MESSAGE("cur service exist\n");
                break;
            }
        }
    }
    else
    {
        bCurChExist = MAPI_TRUE;
    }
    MAPI_S32 total=pCMDB->Size();
#if (NCD_ENABLE == 1)
    MAPI_U16 u16ServiceID, u16TSID;
#endif
    //printf("total %d\n",total);
    while(total > 0)
    {
        pNextProg = pCMDB->GetByIndex(total-1);
        if(pNextProg)
        {
            if(__GetMux(pNextProg, pCMDB) == pMux)
            {
                bRemove = MAPI_TRUE;
#if (NCD_ENABLE == 1)
                u16ServiceID = __GetID(pNextProg, pMux, pNetwork, EN_ID_SID);
                u16TSID = __GetID(pNextProg, pMux, pNetwork, EN_ID_TSID);
#endif
                for(int i = 0; i < MAX_CHANNEL_IN_MUX; i++)
                {
                    if(INVALID_SERVICE_ID != pService[i].u16ServiceID)
                    {
                        if(pService[i].u16ServiceID == __GetID(pNextProg, pMux, pNetwork, EN_ID_SID))
                        {
                            u16NewSID = pService[i].u16ServiceID;
                            bRemove = MAPI_FALSE;
                            break;
                        }
                    }
                }

                if(bRemove && IS_NORDIC_COUNTRY(m_eCountry))
                {
#if (OAD_ENABLE == 1)
                    U16 u16TS_ID = 0, u16ON_ID = 0, u16Srv_ID = 0;
                    if(m_OADParser)
                        m_OADParser->GetOADServiceIDs(&u16TS_ID, &u16ON_ID, &u16Srv_ID);
                    if((__GetID(pNextProg, pMux, pNetwork, EN_ID_SID)  == u16Srv_ID) &&
                       (__GetID(pNextProg,pMux,pNetwork,EN_ID_TSID) == u16TS_ID) &&
                       (__GetID(pNextProg,pMux,pNetwork,EN_ID_ONID) == u16ON_ID))
                    {
                        bRemove = FALSE;
                    }
#endif
                    if((__GetVer(pNextProg, EN_VER_SDT) == INVALID_PSI_SI_VERSION) &&
                        (pNextProg->stCHAttribute.u8ServiceType == E_SERVICETYPE_DATA))
                    {

                        bRemove = FALSE;
                    }
                    if(!bRemove && (pCurProg == pNextProg))
                    {
                        bCurChExist = TRUE;
                    }
                }

                if(bRemove)
                {
                    //printf("del %s\n",pNextProg->u8ServiceName);
                    pCMDB->Delete(pNextProg);
                    u16RemoveCnt++;
                }

#if (NCD_ENABLE == 1)
                std::list<ST_MW_RELOCATED_SERVICE_INFO>::iterator it;
                for(it=m_stRelocatedServiceInfoList.begin(); it!=m_stRelocatedServiceInfoList.end(); ++it)
                {
                    if((it->stTripleId.u16SrvId == u16ServiceID) && (it->u16TSIDHasSameService == u16TSID))
                    {
                        if(bRemove == MAPI_TRUE)
                        {
                            MW_SI_PARSER_UPDATE("[NCD] %s: set SID %u in TSID %u to WAIT_ADD\n", __FUNCTION__, it->stTripleId.u16SrvId, it->stTripleId.u16TsId);
                            it->enState = E_RELOCATED_SERVICE_STATE_WAIT_ADD;

                        }
                        else
                        {
                            MW_SI_PARSER_UPDATE("[NCD] %s: erase SID %u in TSID %u(size = %u)\n", __FUNCTION__, it->stTripleId.u16SrvId, it->stTripleId.u16TsId, m_stRelocatedServiceInfoList.size());
                            m_stRelocatedServiceInfoList.erase(it);
                        }
                        break;
                    }
                }
#endif
            }
        }
        total--;
    }

    if(bCurChExist == MAPI_FALSE)
    {
        MW_SI_PARSER_UPDATE("remove .............................cur ch\n");
        MW_SI_PARSER_MESSAGE("update current CH\n");
        if (u16NewSID != INVALID_SERVICE_ID)
        {
            pFirstProg = pCMDB->GetByID(u16OldTSID, u16OldONID, u16NewSID);
        }
        else
        {
            pFirstProg = pCMDB->GetByIndex(0);
        }
        ASSERT(pFirstProg);
        if(pCMDB->SetCurr(pFirstProg) == MAPI_FALSE)
        {
            ASSERT(0);
        }
        m_pCurProg = pCMDB->GetCurr();
        m_pCurMux = __GetMux(m_pCurProg, m_pCMDB);
        if(m_pCurMux == NULL)
        {
            MW_SI_PARSER_ERROR("get mux failed\n");
            ASSERT(0);
        }
        m_pCurNetwork = __GetNetwork(m_pCurMux, m_pCMDB);
        if(m_pCurNetwork == NULL)
        {
            MW_SI_PARSER_ERROR("get network failed\n");
            ASSERT(0);
        }
        m_stCurPmt.u8Version = SI_PSI_FORCE_UPDATE_VER;
        m_u32PmtMonitorTimer = 0;

        //reset sdt_other version with specific service moving TSID
        for(MAPI_U16 i = 0; i < m_stSdtOtherInfo.stSdtOtherTS.size(); i++)
        {
            if(m_stSdtOtherInfo.stSdtOtherTS.at(i).u16TSID == m_stCurPmt.stServiceMove.u16NewTSId)
            {
                m_stSdtOtherInfo.stSdtOtherTS.at(i).u8SdtVer = INVALID_PSI_SI_VERSION;
                break;
            }
        }
        U16 index;
        if(m_stSdtOtherInfo.stSdtOtherTS.size())
        {
            for(index = 0; index < m_stSdtOtherInfo.stSdtOtherTS.size(); index++)
            {
                if(m_stSdtOtherInfo.stSdtOtherTS.at(index).u16TSID == m_stCurPmt.stServiceMove.u16NewTSId)
                {
                    break;
                }
            }
            if(index < m_stSdtOtherInfo.stSdtOtherTS.size())
            {
                U8 NewIndex = index + 1;
                if(NewIndex >= m_stSdtOtherInfo.stSdtOtherTS.size()) NewIndex = 0;
                if(m_pSdtOtherParser != NULL)
                {
                    if(NewIndex != m_stSdtOtherInfo.u8SdtOtherIndex)//del different other
                    {
                        _DeleteParser((U32)m_pSdtOtherParser, MAPI_FALSE);
                    }
                }
                m_u32SdtOtherMonitorTimer = 0;
            }
        }

        //start timer to check if got service relocate descriptor
        m_u32SdtOtherMonitorTimer = 0;
        //m_CurServiceInfo.eChangeType = MW_SERVICE_RELOCATE;
        m_CurServiceInfo.stServiceRelocInfo.u16OldOnID = u16OldONID;
        m_CurServiceInfo.stServiceRelocInfo.u16OldTsID = u16OldTSID;
        m_CurServiceInfo.stServiceRelocInfo.u16OldSrvID = u16OldSID;
        m_u32ServiceRelocTimer = _GetTime0();
        /*if(m_pCurPmtParser)//don't del parser in cm lock mode
        {
            _DeleteParser((MAPI_U32)m_pCurPmtParser,MAPI_FALSE);
        }*/
        //_ResetInfo(RESET_EIT);
    }
    else
    {
        _UpdateCurrentProgram();
    }
    return u16RemoveCnt ? MAPI_TRUE : MAPI_FALSE;
}

BOOL MW_DVB_SI_PSI_Parser::_IsTDTMajor()
{
    if((m_eCountry == E_FRANCE) && (m_eParserType == MW_DVB_C_PARSER))
    {
        return FALSE;
    }
    return TRUE;
}

BOOL MW_DVB_SI_PSI_Parser::_ShouldSetTimeFromTOT()
{
    if(_IsTDTMajor() == FALSE)
    {
        return TRUE;
    }
    return (m_bGotTDT_Ready == FALSE);
}

BOOL MW_DVB_SI_PSI_Parser::_ShouldSetTimeFromTDT()
{
    if(_IsTDTMajor() == TRUE)
    {
        return TRUE;
    }
    return (m_bGotTOT_Ready == FALSE);
}

void MW_DVB_SI_PSI_Parser::_TdtReady(void)
{
    MW_SI_PARSER_MESSAGE("\n\n%s.....\n", __PRETTY_FUNCTION__);
    MAPI_U8 au8UTC[5];
    MAPI_U32 u32Seconds;
    MAPI_SI_TIME m_stTime;
    mapi_system * system = mapi_interface::Get_mapi_system();
    ASSERT(system);
    MAPI_S32 s32OldOffsetTime=system->GetClockOffset();

    memset(au8UTC, 0, sizeof(au8UTC));

    if (m_pTdtParser->GetUTC(au8UTC))
    {
        u32Seconds = mapi_dvb_utility::SI_MJDUTC2Seconds(au8UTC, &m_stTime);
#if (ISDB_SYSTEM_ENABLE == 1)
        _SetStreamUTCTime(u32Seconds);
        if(m_eClockMode != MW_DVB_CLOCK_AUTO)
        {
            return;
        }
#endif
        if(u32Seconds)  //stGenSetting.g_Time.en_ClockMode == EN_ClockMode_Auto )
        {
            MAPI_S32 s32TempTime = 0;
            //system->RTCSetCLK(u32Seconds);
            if(_ShouldSetTimeFromTDT())
            {
                _RTCSetCLK(u32Seconds);
            }

            if(!m_bGotTOT_Ready)//use UTC of TDT
            {
                MAPI_BOOL bDaylightSavingAuto;
                m_ParserCallBackInfo.EventHandler(E_DVB_SI_CHECK_DAYLIGHT_SAVING_AUTO, (MAPI_U32)m_ParserCallBackInfo.pCallbackReference, (MAPI_U32)&bDaylightSavingAuto);
                if(MAPI_TRUE == bDaylightSavingAuto)
                {
                    s32TempTime = mapi_dvb_utility::SI_GetTimeZoneOffset(mapi_dvb_utility::SI_GetTimeOffset(m_enTimeZone));
                    if(m_bEnableManualCalculateOffsettime)
                    {
                        m_u8SummerTimeOffset = _CalSummerTimeOffset(au8UTC);
                        s32TempTime += m_u8SummerTimeOffset * SI_SECONDS_PER_HOUR;
                        //system->SetClockOffset(s32TempTime);
                        _SetClockOffset(s32TempTime, MAPI_FALSE);
                    }
                    
                }
                if(!m_bGotTDT_Ready)
                {
                    _pfMonitorNotify(E_DVB_TDT_TOT_RECEIVED, NULL, m_pMonitorNotifyUsrParam, NULL);
                }
            }
            m_bGotTDT_Ready = TRUE;
        }
    }
    _DeleteParser((MAPI_U32)m_pTdtParser , MAPI_FALSE);
    if(s32OldOffsetTime != system->GetClockOffset())
    {
        MONITOR_NOTIFY(E_DVB_OFFSET_TIME_CHANGE, NULL, m_pMonitorNotifyUsrParam, NULL);
    }
}

void MW_DVB_SI_PSI_Parser::_TotReady(void)
{
    MW_SI_PARSER_MESSAGE("\n\n%s.....\n", __PRETTY_FUNCTION__);
    MAPI_U32 u32OldNxt = 0;
    mapi_system * system = mapi_interface::Get_mapi_system();
    ASSERT(system);
    MAPI_S32 s32OldOffsetTime=system->GetClockOffset();
    if(0xFF != m_u8TOTIndex)
    {
        u32OldNxt = mapi_dvb_utility::SI_MJDUTC2Seconds(m_stTot.stTotLto.aLTOInfo[m_u8TOTIndex].au8TimeOfChange, NULL);
    }
    if (m_pTotParser != NULL)
    {
        if (m_pTotParser->GetTable(m_stTot))
        {
#if (ISDB_SYSTEM_ENABLE == 1)
            _SetStreamUTCTime(mapi_dvb_utility::SI_MJDUTC2Seconds(m_stTot.au8UTCTime, NULL));
            if(m_eClockMode != MW_DVB_CLOCK_AUTO)
            {
                return;
            }
#endif
            if(0xFF != m_u8TOTIndex)
            {
                if(u32OldNxt != mapi_dvb_utility::SI_MJDUTC2Seconds(m_stTot.stTotLto.aLTOInfo[m_u8TOTIndex].au8TimeOfChange, NULL))
                {
                    m_u32OldNextTimeOffset = u32OldNxt;
                }
            }
            MAPI_SI_TIME stTime;
            MAPI_U32 u32Seconds;
            u32Seconds = mapi_dvb_utility::SI_MJDUTC2Seconds(m_stTot.au8UTCTime, &stTime);
            m_u8TOTIndex = 0xFF;
            if(u32Seconds)  //stGenSetting.g_Time.en_ClockMode == EN_ClockMode_Auto )
            {
                if(_ShouldSetTimeFromTOT())
                {
                    ASSERT(system);
                    //system->RTCSetCLK(u32Seconds);
                    _RTCSetCLK(u32Seconds);
                    if(!m_bGotTOT_Ready)
                    {
                        _pfMonitorNotify(E_DVB_TDT_TOT_RECEIVED, NULL, m_pMonitorNotifyUsrParam, NULL);
                    }
                }

                MAPI_BOOL bDaylightSavingAuto;
                m_ParserCallBackInfo.EventHandler(E_DVB_SI_CHECK_DAYLIGHT_SAVING_AUTO, (MAPI_U32)m_ParserCallBackInfo.pCallbackReference, (MAPI_U32)&bDaylightSavingAuto);
                if(MAPI_TRUE == bDaylightSavingAuto)
                {
                    /* Can not get the LTO, use defalut. */
                    if(MAPI_FALSE == _SI_Update_OffsetTime(u32Seconds, m_u8TOTIndex))
                    {
                        MAPI_S32 s32TempTime = 0;
                        //TODO
                        //msAPI_Timer_SetOffsetTime(0);
                        //system->SetTimeOfChange(0);
                        //system->SetNextTimeOffset(0);
                        _SetTimeOfChange(0);
                        _SetNextTimeOffset(0);
                        m_u8TOTIndex = 0xFF;
                        s32TempTime = mapi_dvb_utility::SI_GetTimeZoneOffset(mapi_dvb_utility::SI_GetTimeOffset(m_enTimeZone));
                        if(m_bEnableManualCalculateOffsettime)
                        {
                            m_u8SummerTimeOffset = _CalSummerTimeOffset(m_stTot.au8UTCTime);
                            s32TempTime += m_u8SummerTimeOffset * SI_SECONDS_PER_HOUR;
                        }
                        //system->SetClockOffset(s32TempTime);
                        // msAPI_Timer_SetOffsetTime(s32TempTime);
                        _SetClockOffset(s32TempTime, MAPI_FALSE);
                        m_s32OffsetTime = s32TempTime;
                    }
                }
                m_bGotTOT_Ready = TRUE;
            }
        }
    }
    _DeleteParser((MAPI_U32)m_pTotParser , MAPI_FALSE);
    if(s32OldOffsetTime != system->GetClockOffset())
    {
        MONITOR_NOTIFY(E_DVB_OFFSET_TIME_CHANGE, NULL, m_pMonitorNotifyUsrParam, NULL);
    }
}
void MW_DVB_SI_PSI_Parser::_RctReady(void)
{

    mapi_si_RCT_Table* pRCT =  mapi_epg_criddb::GetInstance()->GetRCT();
    if(pRCT!=NULL)
    {
        MAPI_DB_SCOPE_LOCK<mapi_si_RCT_Table> scopeLock(*pRCT);
        if(pRCT->GetLinkCount()!=0)
        {
            MONITOR_NOTIFY(E_DVB_RCT_PRESENCE, NULL, m_pMonitorNotifyUsrParam, NULL);
        }
        else
        {
            MONITOR_NOTIFY(E_DVB_RCT_ABSENCE, NULL, m_pMonitorNotifyUsrParam, NULL);
        }
    }
    if(m_pRctParser != NULL)
    {
        _DeleteParser((MAPI_U32)m_pRctParser, MAPI_FALSE);
    }
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::_SI_Update_OffsetTime(MAPI_U32 time, MAPI_U8 & u8Index)
{
    MEMBER_COUNTRY eCountry = mapi_dvb_utility::SI_GetCountryByTimeZone(m_enTimeZone);
    MAPI_U8 __attribute__((unused)) u8Region = 0;
    MAPI_U8 u8Loop = 0;
//    MAPI_U16 u16Offset;
    MAPI_BOOL result = FALSE;
    //mapi_system * system = mapi_interface::Get_mapi_system();

#if ((MSTAR_TVOS == 1)&&(ISDB_SYSTEM_ENABLE == 1))
    //Android system set timezone can not set the time zone carefully.
    if(((m_eCountry == E_BRAZIL)&&(((m_enTimeZone>=TIMEZONE_GMT_Minus2_START)&&(m_enTimeZone<=TIMEZONE_GMT_Minus2_END))
        ||((m_enTimeZone>=TIMEZONE_GMT_Minus3_START)&&(m_enTimeZone<=TIMEZONE_GMT_Minus3_END))
        ||((m_enTimeZone>=TIMEZONE_GMT_Minus4_START)&&(m_enTimeZone<=TIMEZONE_GMT_Minus4_END))
        ||((m_enTimeZone>=TIMEZONE_GMT_Minus5_START)&&(m_enTimeZone<=TIMEZONE_GMT_Minus5_END))))
        // BRAZIL  TimeZone  -2 -3 -4 -5
        ||(((m_eCountry != E_BRAZIL)&&(m_eCountry != E_ARGENTINA))&&(eCountry == m_eCountry)))
        // For Other Country
#else
    if(eCountry == m_eCountry)
#endif
    {
        // Only the following timezones have region id other than zero.
        if(m_enTimeZone == TIMEZONE_CANARY)
            u8Region = 2;
        else if(m_enTimeZone == TIMEZONE_AZORES)
            u8Region = 2;
        else if(m_enTimeZone == TIMEZONE_LISBON)
            u8Region = 1;
        else if(m_enTimeZone == TIMEZONE_MADRID)
            u8Region = 1;
        else if(m_enTimeZone == TIMEZONE_NSW)
            u8Region = 2;
        else if(m_enTimeZone == TIMEZONE_VIC)
            u8Region = 3;
        else if(m_enTimeZone == TIMEZONE_QLD)
            u8Region = 4;
        else if(m_enTimeZone == TIMEZONE_SA)
            u8Region = 5;
        else if(m_enTimeZone == TIMEZONE_WA)
            u8Region = 6;
        else if(m_enTimeZone == TIMEZONE_TAS)
            u8Region = 7;
        else if(m_enTimeZone == TIMEZONE_NT)
            u8Region = 8;
// ISDB System
        else if(m_enTimeZone == TIMEZONE_AM_WEST)
            u8Region = 7;
        else if(m_enTimeZone == TIMEZONE_ACRE)
            u8Region = 6;
        else if(m_enTimeZone == TIMEZONE_M_GROSSO)
            u8Region = 5;
        else if(m_enTimeZone == TIMEZONE_NORTH)
            u8Region = 4;
        else if(m_enTimeZone == TIMEZONE_BRASILIA)
            u8Region = 3;
        else if(m_enTimeZone == TIMEZONE_NORTHEAST)
            u8Region = 2;
        else if(m_enTimeZone == TIMEZONE_F_NORONHA)
            u8Region = 1;
// ISDB System - END

#define LTO m_stTot.stTotLto
        for(u8Loop = 0; u8Loop < LTO.u8NumofRegion; u8Loop++)
        {
            //printf("Country[%bu] = %bu, RegionId[%bu:%bu]\n",u8Loop,LTO.aLTOInfo[u8Loop].eCountryIdx,LTO.aLTOInfo[u8Loop].u8RegionId,u8Region);
#if ((MSTAR_TVOS == 1)&&(ISDB_SYSTEM_ENABLE == 1))
            if((m_eCountry == E_BRAZIL)&&(m_eCountry == mapi_dvb_utility::SI_GetCountryIndex(LTO.aLTOInfo[u8Loop].aCountryInfo)))
#else
            if((m_eCountry == mapi_dvb_utility::SI_GetCountryIndex(LTO.aLTOInfo[u8Loop].aCountryInfo)) &&
                    //LTO.aLTOInfo[u8Loop].aCountryInfo == SI_COUNTRY_SETTING &&
                    ((LTO.aLTOInfo[u8Loop].u8RegionId == 0)//no time zone
                     ||(LTO.aLTOInfo[u8Loop].u8RegionId == u8Region)
                     || ((!IS_SOUTHAMERICA_COUNTRY(m_eCountry) && (m_eCountry != E_AUSTRALIA))/*(LTO.aLTOInfo[u8Loop].u8RegionId == 0)*/ && (LTO.u8NumofRegion == 1))) //for the case that the broadcast(Madrid) didn't give region ID
                    )
#endif
            {
                //MAPI_U8 *pu8LTO;
                MAPI_U8 u8Hour, u8Min;
                MAPI_U32 TimeOfChange = mapi_dvb_utility::SI_MJDUTC2Seconds(LTO.aLTOInfo[u8Loop].au8TimeOfChange, NULL);
                //printf("TimeOfChange=%u UTCTime=%u >>", MApp_MJDUTC2Seconds(LTO.aLTOInfo[u8Loop].au8TimeOfChange), u32Seconds);
                //m_u8TOTIndex=u8Loop;
                u8Index = u8Loop;

#if  ((MSTAR_TVOS == 1)&&(ISDB_SYSTEM_ENABLE == 1))
                MONITOR_NOTIFY(E_DVB_TOT_REGION_ID_UPDATE, reinterpret_cast<void*>(LTO.aLTOInfo[u8Loop].u8RegionId), m_pMonitorNotifyUsrParam, NULL);
#endif
                //remove, let code check TimeOfChange to select offset for stream loop case
/*
                if(TimeOfChange && (TimeOfChange < time))//add check incorrect time_of_change case
                {
                    //printf(" using NextTimeOffset, %u\n",LTO.aLTOInfo[u8Loop].u16NextTimeOffset);
                    //u16Offset=LTO.aLTOInfo[u8Loop].au8NextTimeOffset;//BE2ME16(LTO.aLTOInfo[u8Loop].u16NextTimeOffset);
                    //pu8LTO = (MAPI_U8*)&u16Offset;

                    u8Hour = BCD2Dec(LTO.aLTOInfo[u8Loop].au8NextTimeOffset[0]);
                    u8Min  = BCD2Dec(LTO.aLTOInfo[u8Loop].au8NextTimeOffset[1]);

                }
                else
*/
                {
                    //printf(" using LTO, %u\n",LTO.aLTOInfo[u8Loop].u16LTO);
                    //u16Offset=LTO.aLTOInfo[u8Loop].u16LTO;//BE2ME16(LTO.aLTOInfo[u8Loop].u16LTO);
                    //pu8LTO = (MAPI_U8*)&u16Offset;

                    u8Hour = BCD2Dec(LTO.aLTOInfo[u8Loop].au8LTO[0]);
                    u8Min  = BCD2Dec(LTO.aLTOInfo[u8Loop].au8LTO[1]);
                }
                //printf("\n\nindex %bu, polarity %bu, hr %bu, MIN %bu\n",u8Loop,LTO.aLTOInfo[u8Loop].u8LTOPolarity,u8Hour,u8Min);
                if(LTO.aLTOInfo[u8Loop].u8LTOPolarity)
                {
                    //TODO
                    //printf("offset time - %d hour\n",u8Hour);
                    //msAPI_Timer_SetOffsetTime( (S32)(-((u8Hour * SI_SECONDS_PER_HOUR) + (u8Min * SI_SECONDS_PER_MIN))));
                    m_s32OffsetTime = (MAPI_S32)(-((u8Hour * SI_SECONDS_PER_HOUR) + (u8Min * SI_SECONDS_PER_MIN)));
                    //system->SetClockOffset(m_s32OffsetTime);
                    _SetClockOffset(m_s32OffsetTime);
                }
                else
                {
                    //TODO
                    //printf("offset time + %d hour\n",u8Hour);
                    //msAPI_Timer_SetOffsetTime((S32)((u8Hour * SI_SECONDS_PER_HOUR )+ (u8Min * SI_SECONDS_PER_MIN)));
                    m_s32OffsetTime = (MAPI_S32)((u8Hour * SI_SECONDS_PER_HOUR) + (u8Min * SI_SECONDS_PER_MIN));
                    //system->SetClockOffset(m_s32OffsetTime);
                    _SetClockOffset(m_s32OffsetTime);
                }

                //system->SetTimeOfChange(TimeOfChange);
                _SetTimeOfChange(TimeOfChange);
                u8Hour = BCD2Dec(LTO.aLTOInfo[u8Loop].au8NextTimeOffset[0]);
                u8Min  = BCD2Dec(LTO.aLTOInfo[u8Loop].au8NextTimeOffset[1]);
                if(LTO.aLTOInfo[u8Loop].u8LTOPolarity)
                {
                    //system->SetNextTimeOffset((MAPI_S32)(-((u8Hour * SI_SECONDS_PER_HOUR) + (u8Min * SI_SECONDS_PER_MIN))));
                    _SetNextTimeOffset((MAPI_S32)(-((u8Hour * SI_SECONDS_PER_HOUR) + (u8Min * SI_SECONDS_PER_MIN))));
                }
                else
                {
                    //system->SetNextTimeOffset((MAPI_S32)((u8Hour * SI_SECONDS_PER_HOUR) + (u8Min * SI_SECONDS_PER_MIN)));
                    _SetNextTimeOffset((MAPI_S32)((u8Hour * SI_SECONDS_PER_HOUR) + (u8Min * SI_SECONDS_PER_MIN)));
                }

                result = TRUE;
                break;
            }
        }
    }
#undef LTO
    return result;
}
void MW_DVB_SI_PSI_Parser::_UpdateComponent(void)
{
    MAPI_U8 i, j, u8total;
    if(m_PFComponentInfo[0].u8AudioComponentNumber)
    {
        u8total = 0;
        for(i = 0; i < MAX_AUD_LANG_NUM; i++)
        {
            if(m_CurServiceInfo.stAudInfo[i].u16AudPID != INVALID_PID)
            {
                u8total++;
            }
        }

        for(i = 0; i < u8total; i++)
        {
            for(j = 0; j < m_PFComponentInfo[0].u8AudioComponentNumber; j++)
            {
                if(m_CurServiceInfo.au8AudioComponentTag[i] == m_PFComponentInfo[0].aAudioComponentInfo[j].u8ComponentTag)
                {
                    if(m_CurServiceInfo.stAudInfo[i].bInValid)
                    {
                        m_CurServiceInfo.eChangeType = MW_SERVICE_COMPONENT_CHANGED;
                        m_CurServiceInfo.stAudInfo[i].bInValid = MAPI_FALSE;
                    }
                    break;
                }
            }
            if(j >= m_PFComponentInfo[0].u8AudioComponentNumber)
            {
                MW_SI_PARSER_MESSAGE("audio component change\n");
                #if 0 // remove the feature for v2.1.0 not mandatory and test case confused
                m_CurServiceInfo.stAudInfo[i].bInValid = MAPI_TRUE;
                #endif
                m_CurServiceInfo.eChangeType = MW_SERVICE_COMPONENT_CHANGED;
            }
        }
    }

    if(m_PFComponentInfo[0].u8DvbSubComponentNumber)
    {
        u8total = 0;

        for(i = 0; i < MAPI_SI_MAX_SUBTITLEINFO_NUM; i++)
        {
            if(0 != m_CurServiceInfo.astDVBSubtInfo[i].u16Sub_Pid)
            {
                u8total++;
            }
        }

        for(i = 0; i < u8total; i++)
        {
            for(j = 0; j < m_PFComponentInfo[0].u8DvbSubComponentNumber; j++)
            {
                if(m_CurServiceInfo.astDVBSubtInfo[i].u8ComponentTag == m_PFComponentInfo[0].aDvbSubtitleComponentInfo[j].u8ComponentTag)
                {
                    MW_SI_PARSER_MESSAGE("same dvb\n");
                    if(m_CurServiceInfo.astDVBSubtInfo[i].bInValid)
                    {
                        m_CurServiceInfo.eChangeType = MW_SERVICE_COMPONENT_CHANGED;
                        m_CurServiceInfo.astDVBSubtInfo[i].bInValid = MAPI_FALSE;
                    }
                    break;
                }
            }
            if(j >= m_PFComponentInfo[0].u8DvbSubComponentNumber)
            {
                MW_SI_PARSER_MESSAGE("dvb subtitle component change\n");
                #if 0 // remove the feature for v2.1.0 not mandatory and test case confused
                m_CurServiceInfo.astDVBSubtInfo[i].bInValid = MAPI_TRUE;
                #endif
                m_CurServiceInfo.eChangeType = MW_SERVICE_COMPONENT_CHANGED;
            }
        }
    }

    if(m_PFComponentInfo[0].u8EbuSubComponentNumber)
    {
        u8total = 0;
        for(i = 0; i < MAPI_SI_MAX_SUBTITLEINFO_NUM; i++)
        {
            if(0 != m_CurServiceInfo.astEBUSubtInfo[i].u16TTX_Pid)
            {
                u8total++;
            }
        }

        for(i = 0; i < u8total; i++)
        {
            for(j = 0; j < m_PFComponentInfo[0].u8EbuSubComponentNumber; j++)
            {
                if(m_CurServiceInfo.astEBUSubtInfo[i].u8ComponentTag == m_PFComponentInfo[0].aEbuSubtitleComponentInfo[j].u8ComponentTag)
                {
                    MW_SI_PARSER_MESSAGE("same ebu\n");
                    if(m_CurServiceInfo.astEBUSubtInfo[i].bInValid)
                    {
                        m_CurServiceInfo.eChangeType = MW_SERVICE_COMPONENT_CHANGED;
                        m_CurServiceInfo.astEBUSubtInfo[i].bInValid = MAPI_FALSE;
                    }
                    break;
                }
            }
            if(j >= m_PFComponentInfo[0].u8EbuSubComponentNumber)
            {
                MW_SI_PARSER_MESSAGE("ebu subtitle component change\n");
                #if 0 // remove the feature for v2.1.0 not mandatory and test case confused
                m_CurServiceInfo.astEBUSubtInfo[i].bInValid = MAPI_TRUE;
                #endif
                m_CurServiceInfo.eChangeType = MW_SERVICE_COMPONENT_CHANGED;
            }
        }
    }
    if(m_CurServiceInfo.eChangeType == MW_SERVICE_COMPONENT_CHANGED)
    {
        if ( m_bIsSIDynmaicReScanOff == MAPI_FALSE)
        {
            __UpdateService(m_pCurProg, &m_CurServiceInfo, m_pCMDB, MAPI_FALSE);
        }
        MONITOR_NOTIFY(E_DVB_COMPONENT_CHANGE, &m_CurServiceInfo, m_pMonitorNotifyUsrParam, NULL);
        m_CurServiceInfo.eChangeType = MW_SERVICE_NO_CHANGE;
    }
}
void MW_DVB_SI_PSI_Parser::_EitPFReady(void)
{
    // MAPI_EIT_CUR_EVENT_PF is a large structure more than 4KB
    // and its member is mixed with raw data and extracted data.
    // m_pEitPFParser->GetTable may get present section, following section, or both.
    // Once GetTable finish, the previous data is overwritten (including extraced data),
    //  we cannot juge if new received table is new version or not.
    // Therefore, we backup version, CRC. parental rating, content objective (ISDB)
    // before m_pEitPFParser->GetTable, and then recover those data after GetTable.
    // Then check if version change.
    // the best way is to backup whole table, but it is too huge to consume memory/time.

    MAPI_U8 au8OldVer[MAX_CUR_PF_SEC];
    MAPI_U32 au32CRC32[MAX_CUR_PF_SEC];
    MAPI_U8 au8BkParentalCtl[MAX_CUR_PF_SEC];
    MAPI_U8 au8BkParentalContent[MAX_CUR_PF_SEC];
    au8BkParentalCtl[PRESENT_SEC] = m_EitPfInfo[PRESENT_SEC].u8Parental_Control;
    au8BkParentalCtl[FOLLOW_SEC] = m_EitPfInfo[FOLLOW_SEC].u8Parental_Control;
    au8BkParentalContent[PRESENT_SEC] = m_EitPfInfo[PRESENT_SEC].u8Parental_ObjectiveContent;
    au8BkParentalContent[FOLLOW_SEC] = m_EitPfInfo[FOLLOW_SEC].u8Parental_ObjectiveContent;

    au8OldVer[PRESENT_SEC] = m_EitPfInfo[PRESENT_SEC].version_number;
    au32CRC32[PRESENT_SEC] = m_EitPfInfo[PRESENT_SEC].u32CRC32;
    au8OldVer[FOLLOW_SEC] = m_EitPfInfo[FOLLOW_SEC].version_number;
    au32CRC32[FOLLOW_SEC] = m_EitPfInfo[FOLLOW_SEC].u32CRC32;

    if(m_pEitPFParser != NULL)
    {
        if (MAPI_FALSE == m_pEitPFParser->GetTable(m_EitPfInfo))
        {
            if((m_EitPfInfo[PRESENT_SEC].version_number != INVALID_PSI_SI_VERSION) &&
                    (m_EitPfInfo[FOLLOW_SEC].version_number != INVALID_PSI_SI_VERSION))
            {
                _DeleteParser((MAPI_U32)m_pEitPFParser , MAPI_FALSE);
            }
            return;
        }
        //printf("m_EitPfInfo %x %x %x %x\n",m_EitPfInfo[0].version_number,m_EitPfInfo[0].u32CRC32
        //   ,m_EitPfInfo[1].version_number,m_EitPfInfo[1].u32CRC32);
        //present change
        m_EitPfInfo[PRESENT_SEC].u8Parental_Control = au8BkParentalCtl[PRESENT_SEC];
        m_EitPfInfo[FOLLOW_SEC].u8Parental_Control = au8BkParentalCtl[FOLLOW_SEC];
        m_EitPfInfo[PRESENT_SEC].u8Parental_ObjectiveContent = au8BkParentalContent[PRESENT_SEC];
        m_EitPfInfo[FOLLOW_SEC].u8Parental_ObjectiveContent = au8BkParentalContent[FOLLOW_SEC];


        if((au8OldVer[PRESENT_SEC] != m_EitPfInfo[PRESENT_SEC].version_number)
                || (au32CRC32[PRESENT_SEC] != m_EitPfInfo[PRESENT_SEC].u32CRC32)
                || (au8OldVer[FOLLOW_SEC] != m_EitPfInfo[FOLLOW_SEC].version_number)
                || (au32CRC32[FOLLOW_SEC] != m_EitPfInfo[FOLLOW_SEC].u32CRC32))
        {
            m_pEitPFParser->GetPFComponentInfo(m_PFComponentInfo);
        }
    }
#if 0 //(FREEVIEW_AU_ENABLE==0) // remove the feature for v2.1.0 not mandatory and test case confused
    if(m_EitPfInfo[0].version_number != INVALID_PSI_SI_VERSION)
    {
        if((u8OldVer != m_EitPfInfo[0].version_number)
                || (u32CRC32 != m_EitPfInfo[0].u32CRC32))
        {
            if(m_CurServiceInfo.u8PmtVersion != INVALID_PSI_SI_VERSION)
            {
                if((m_eCountry == E_NEWZEALAND) && //IS_DTG_COUNTRY(m_eCountry) &&
                        ((__GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_ONID) == UK_ONID)
                         || (__GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_ONID) == NZ_ONID)))
                {

                    _UpdateComponent();
                }
            }
        }
    }
#endif
    MAPI_EIT_CUR_EVENT_PF* pEvent=NULL;
    MAPI_U8* pu8parentalRate;
    for(MAPI_U8 i = 0; i < MAX_CUR_PF_SEC; i++)
    {
        pEvent = NULL;
        if (((m_EitPfInfo[i].version_number != INVALID_PSI_SI_VERSION) && (m_EitPfInfo[i].u32CRC32 !=0))
            && ((au8OldVer[i] != m_EitPfInfo[i].version_number) || (au32CRC32[i] != m_EitPfInfo[i].u32CRC32)))
        {
            pEvent=&m_EitPfInfo[i];
            pEvent->u8Parental_Control = 0;
            //printf("au8OldVer[i] = %d,  m_EitPfInfo[i].version_number = %d  Changed\n",i,au8OldVer[i],i,m_EitPfInfo[i].version_number);
        }

        if((NULL != pEvent)&&(NULL != pEvent->pu8ParentalRawData))
        {
            pu8parentalRate = &pEvent->u8Parental_Control;

            MAPI_U8* pLangCode;
            MAPI_U8* pu8Src;
            MAPI_U8 u8LoopIdx,u8StrCount=0,u8TempRate;
            MAPI_BOOL bRatingValid = MAPI_FALSE;
            UNUSED(bRatingValid);
            pu8Src = pEvent->pu8ParentalRawData;
            u8StrCount = pEvent->u8ParentalRateCount;
            for(u8LoopIdx = 0; u8LoopIdx < u8StrCount; u8LoopIdx++)
            {
                pLangCode=pu8Src + (4 * u8LoopIdx);
                u8TempRate = *(pu8Src + ((4 * u8LoopIdx) + 3));
                if(m_eCountry== mapi_dvb_utility::SI_GetCountryIndex(pLangCode))
                {
                    //printf("u8TempRate=%d\n",u8TempRate);
                    //printf("m_u8parentalRatel =%d,u8TempRate=%d\n",*pu8parentalRate,u8TempRate);
                    *pu8parentalRate = u8TempRate;
                    bRatingValid = MAPI_TRUE;
                    break;
                }
                else if((m_eCountry != E_THAILAND)&&(m_enCableOperator != EN_CABLEOP_ZIGGO)&&(!IS_UPC(m_enCableOperator)))
                {
                    if( (*pu8parentalRate == 0) || (*pu8parentalRate > u8TempRate))
                    {   //Only record rateing here, change to age in parser layer
                        //printf("m_u8parentalRatel =%d,u8TempRate=%d\n",*pu8parentalRate,u8TempRate);
                        *pu8parentalRate = u8TempRate;
                        bRatingValid = MAPI_TRUE;
                    }
                }
            }
#if (ISDB_SYSTEM_ENABLE == 1)
            if((au8OldVer[i] != m_EitPfInfo[i].version_number))
            {
                m_EitPfInfo[i].u8Parental_ObjectiveContent = m_EitPfInfo[i].u8Parental_Control >> 4;
                m_EitPfInfo[i].u8Parental_Control = (m_EitPfInfo[i].u8Parental_Control & 0x0F);
                switch(m_EitPfInfo[i].u8Parental_Control)
                {
                    case 2:
                        m_EitPfInfo[i].u8Parental_Control = 10;
                        break;
                    case 3:
                        m_EitPfInfo[i].u8Parental_Control = 12;
                        break;
                    case 4:
                        m_EitPfInfo[i].u8Parental_Control = 14;
                        break;
                    case 5:
                        m_EitPfInfo[i].u8Parental_Control = 16;
                        break;
                    case 6:
                        m_EitPfInfo[i].u8Parental_Control = 18;
                        break;

                    default:
                        m_EitPfInfo[i].u8Parental_Control = 0;
                        break;
                }
            MW_SI_PARSER_MESSAGE("------->m_EitPfInfo[%d].u8Parental_Control %d\n",i,m_EitPfInfo[i].u8Parental_Control);

            }

#else
            if (bRatingValid)
            {
                if( *pu8parentalRate > 0x1f )
                {
                    *pu8parentalRate= 0;
                }
                *pu8parentalRate += PARENTAL_RATE_MIN_AGE; // ETSI spec says that we need add "3"
                //printf("*pu8parentalRate = %d\n",*pu8parentalRate);
            }
#endif
            FREE(pEvent->pu8ParentalRawData);
        }
    }

    if(((m_EitPfInfo[PRESENT_SEC].version_number != INVALID_PSI_SI_VERSION)
        && (m_EitPfInfo[PRESENT_SEC].u32CRC32 !=0))
            && ((au8OldVer[PRESENT_SEC] != m_EitPfInfo[PRESENT_SEC].version_number)
            || (au32CRC32[PRESENT_SEC] != m_EitPfInfo[PRESENT_SEC].u32CRC32)
            || (_GetRating() != m_u8CurRatingChkValue)
#if (ISDB_SYSTEM_ENABLE == 1)
            || (_GetContent() != m_u8CurContentChkValue)
#endif
			))
    {
#if (ISDB_SYSTEM_ENABLE == 1)
        if(m_u8PmtParentalControl == INVALID_PARENTAL_RATING)
#endif
        {
            MONITOR_NOTIFY(E_DVB_PARENTAL_RATING, NULL, m_pMonitorNotifyUsrParam, NULL);
        }
    }
    if((au8OldVer[FOLLOW_SEC] != m_EitPfInfo[FOLLOW_SEC].version_number)
            || (au32CRC32[FOLLOW_SEC] != m_EitPfInfo[FOLLOW_SEC].u32CRC32))
    {
        MONITOR_NOTIFY(E_DVB_GOT_FOLLOW_INFO, NULL, m_pMonitorNotifyUsrParam, NULL);
    }

    if((au8OldVer[PRESENT_SEC] != m_EitPfInfo[PRESENT_SEC].version_number) || (au32CRC32[PRESENT_SEC] != m_EitPfInfo[PRESENT_SEC].u32CRC32))
    {
        _SetServiceCMG(m_EitPfInfo[PRESENT_SEC].m_CMG, E_CMG_FROM_EIT);
    }
    if((au8OldVer[PRESENT_SEC] != m_EitPfInfo[PRESENT_SEC].version_number)&&(MAPI_TRUE == m_EitPfInfo[PRESENT_SEC].stEitSimulcastinfo.bIsSimulcast))
    {
        ///printf("SD simulcast with HD\n\n");
        MONITOR_NOTIFY(E_DVB_SIMULCAST, NULL, m_pMonitorNotifyUsrParam,& m_EitPfInfo[PRESENT_SEC].stEitSimulcastinfo.stTargetTripleid);
    }

    if (EIT_PF_CONTINUE_MODE == 0)
    {
        if((m_EitPfInfo[PRESENT_SEC].version_number != INVALID_PSI_SI_VERSION) &&
                (m_EitPfInfo[FOLLOW_SEC].version_number != INVALID_PSI_SI_VERSION))
        {
            _DeleteParser((MAPI_U32)m_pEitPFParser , MAPI_FALSE);
            MW_SI_PARSER_MESSAGE("get eit pf\n");
            //printf("rating %d %d\n",m_EitPfInfo[0].u8Parental_Control,m_EitPfInfo[1].u8Parental_Control);
            //printf("del pf\n");
            //printf("s16OpenFilter %d\n",m_s16OpenFilter);
        }
    }

#if (ISDB_SYSTEM_ENABLE == 1)
    if( m_eParserType == MW_ISDB_T_PARSER )
    {
        //if( m_EitPfInfo[0].u8AudioTag != 0 )//EIT Get TAG_ACD
        BOOL bUpdate=FALSE;
        {
           if((au8OldVer[PRESENT_SEC] != m_EitPfInfo[PRESENT_SEC].version_number) ||
              (au32CRC32[PRESENT_SEC] != m_EitPfInfo[PRESENT_SEC].u32CRC32))
            {

                if(m_stCurPmt.u8Version != INVALID_PSI_SI_VERSION)
                {
                    for(MAPI_U8 i=0;i<m_stCurPmt.u8AudioNumber;i++)
                    {
                        MW_SI_PARSER_MESSAGE("m_EitPfInfo[PRESENT_SEC].u8AudioTag %d\n",m_EitPfInfo[PRESENT_SEC].u8AudioTag);
                        MW_SI_PARSER_MESSAGE("m_CurServiceInfo.au8AudioComponentTag[%d] %d\n",i,m_CurServiceInfo.au8AudioComponentTag[i]);
                        MW_SI_PARSER_MESSAGE("m_CurServiceInfo.stAudInfo[%d].aISOLangInfo.u8IsValid %d\n",i,m_CurServiceInfo.stAudInfo[i].aISOLangInfo.u8IsValid);
                        for(int j=0;j<m_EitPfInfo[PRESENT_SEC].u8AudioNumber;j++)
                        {
                            if( (m_CurServiceInfo.au8AudioComponentTag[i] == m_EitPfInfo[PRESENT_SEC].au8AudioTag[j]) &&
                                (m_CurServiceInfo.stAudInfo[i].aISOLangInfo.u8IsValid != TRUE) )
                            {
                                m_CurServiceInfo.stAudInfo[i].aISOLangInfo.u8IsValid = TRUE;
                                m_CurServiceInfo.stAudInfo[i].aISOLangInfo.u8AudType = m_EitPfInfo[PRESENT_SEC].astISOLangInfo[j].u8AudType;
                                m_CurServiceInfo.stAudInfo[i].aISOLangInfo.u8AudMode = m_EitPfInfo[PRESENT_SEC].astISOLangInfo[j].u8AudMode;
                                memcpy(&m_CurServiceInfo.stAudInfo[i].aISOLangInfo.u8ISOLangInfo[0],&m_EitPfInfo[PRESENT_SEC].astISOLangInfo[j].u8ISOLangInfo[0], 3);
                                bUpdate=TRUE;
                                break;
                            }
                        }
                    }
                }
            }
            if((TRUE == bUpdate) && (FALSE == m_bIsSIDynmaicReScanOff))
            {
                if(__UpdateService(m_pCurProg, &m_CurServiceInfo, m_pCMDB, MAPI_FALSE))
                {
                    MONITOR_NOTIFY(E_DVB_AUDIO_UPDATE, &m_CurServiceInfo, m_pMonitorNotifyUsrParam, NULL);
                }

            }
        }
    }
#endif
}

void MW_DVB_SI_PSI_Parser::_AitReady(void)
{
    if(E_DVB_MONITOR == m_eParserMode)
    {
        MAPI_U8 u8OldVersion = m_stAit.m_u8Version;
        if(m_pAitParser != NULL)
        {
            if (MAPI_FALSE == m_pAitParser->GetTable(m_stAit))
            {
                _DeleteParser((MAPI_U32)m_pAitParser , MAPI_FALSE);
                return;
            }
        }
        _DeleteParser((MAPI_U32)m_pAitParser , MAPI_FALSE);
        MW_SI_PARSER_MESSAGE("get AIT ver %x %x\n", u8OldVersion, m_stAit.m_u8Version);
        if(u8OldVersion != m_stAit.m_u8Version)
        {
            //1 TODO: notify AIT to OWB
            MW_SI_PARSER_MESSAGE("\nAIT Ready>>\n");
            //mapi_si_AIT_parser::DumpParseData(m_stAit);

#if (HBBTV_ENABLE ==1)
            {


                for(int i = 0; i < m_stAit.m_u8AppNum; i++)
                {
                    mapi_ait_app& app = m_stAit.m_app[i];
                    //if(app.m_u8CtrlCode != 0x01)continue;//non auto start
                    for(int j = 0; j < app.m_u8TrpNum; j++)
                    {
                        U8 base;
                        if(app.m_u8CtrlCode == 0x01)base = 5; //autuostart
                        else if(app.m_u8CtrlCode == 0x02)base = 4; //present
                        else if(app.m_u8CtrlCode == 0x04)base = 4; //kill
                        else if(app.m_u8CtrlCode == 0x07)base = 2; //disabled
                        else base = 1;
                        app.m_trp[j].m_u16Weight = base * 10000 + (app.m_u8Priority + 1) * 10 + app.m_trp[j].m_u16ProtoID;
                    }
                }

                U16 u16SID=__GetID(m_pCurProg,m_pCurMux,m_pCurNetwork,EN_ID_SID);
                MONITOR_NOTIFY(E_DVB_AIT_SIGNAL, static_cast<mapi_ait_apps*>(&m_stAit), m_pMonitorNotifyUsrParam,(void*)&u16SID);

                /*
                if(m_pcHBBTV->AITSignal(__GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_SID), static_cast<mapi_ait_apps&>(m_stAit)) == FALSE)
                {
                    m_stAit.m_u8Version = u8OldVersion;
                }*/
            }
            /*
                        if(NULL != m_pAitSignalling)
                        {
                            //Is a HbbTV application?
                            if(eAppType_HBBTV == static_cast<EN_AIT_APP_TYPE>(m_stAit.m_u16AppType))
                            {
                                if(NULL != m_pCurProg)
                                {
                                    m_pAitSignalling->m_impl->service_new_ait(__GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_SID), static_cast<mapi_ait_apps&>(m_stAit));
                                }
                            }
                        }
            */
#elif (GINGA_ENABLE ==1)
            {
                for(int i=0;i<m_stAit.m_u8AppNum;i++)
                {
                    mapi_ait_app& app = m_stAit.m_app[i];
                    //if(app.m_u8CtrlCode != 0x01)continue;//non auto start
                    for(int j=0;j<app.m_u8TrpNum;j++)
                    {
                        U8 base;
                        //printf("app.m_u8CtrlCode = 0x%x\n", app.m_u8CtrlCode);
                        if(app.m_u8CtrlCode==0x01)base=5;//autuostart
                        else if(app.m_u8CtrlCode==0x02)base=4;//present
                        else if(app.m_u8CtrlCode==0x04)base=4;//kill
                        else if(app.m_u8CtrlCode==0x07)base=2;//disabled
                        else base=1;
                        app.m_trp[j].m_u16Weight=base*10000+(app.m_u8Priority+1)*10+app.m_trp[j].m_u16ProtoID;
                    }
                }

                U16 u16SID=__GetID(m_pCurProg,m_pCurMux,m_pCurNetwork,EN_ID_SID);
                MONITOR_NOTIFY(E_DVB_AIT_SIGNAL, static_cast<mapi_ait_apps*>(&m_stAit), m_pMonitorNotifyUsrParam,(void*)&u16SID);

                /*
                if(m_pcGinga->AITSignal(__GetID(m_pCurProg,m_pCurMux,m_pCurNetwork,EN_ID_SID),static_cast<mapi_ait_apps&>(m_stAit)) == FALSE)
                {
                    m_stAit.m_u8Version=u8OldVersion;
                }
                */
            }
#endif
        }
    }
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::_ProcessTriggerEvent(void)
{
    int result;
    mapi_si_psi_event cEvt;
    result = m_pParserTriggerEvent->Wait(&cEvt, (m_bRunning == MAPI_TRUE) ? 0 : 100);
    if(result == 0)
    {
        mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
        MW_SI_PARSER_MESSAGE("\n\n%s.....event %u\n", __PRETTY_FUNCTION__, (unsigned int)cEvt.u32Event);

        switch(cEvt.u32Event)
        {
            case EN_DVB_SCAN_START:
                ResetEPGBarkerChannelInfo();
                _ScanStart();
                return MAPI_TRUE;
            case EN_DVB_MONITOR_START:
                if ((cEvt.u32Param1)
                    && (((m_eParserType == MW_DVB_C_PARSER) && (m_enCableOperator == EN_CABLEOP_CABLEREADY))
                        ||((m_eParserType == MW_DVB_T_PARSER) && (m_eCountry == E_FINLAND))))
                {
                    m_bIsSIAutoUpdateOff = TRUE;
                }
                else
                {
                    m_bIsSIAutoUpdateOff = FALSE;
                }
                m_u32MonitorUserData = cEvt.u32Param1;
                m_enMonitorMode = (EN_MW_SI_MONITOR_MODE)cEvt.u32Param2;
                if((m_enMonitorMode == EN_MW_SI_MONITOR_MODE_CI_SPECIFY_PMT) || (m_enMonitorMode == EN_MW_SI_MONITOR_MODE_MHEG5_SPECIFY_PMT))
                {
                    m_u32MonitorUserData &=~(0xFFFF<<16);
                    m_u32MonitorUserData |=(INVALID_PID<<16);
                }
                _MonitorStart();
                return MAPI_TRUE;
#if (OAD_ENABLE == 1)
            case EN_DVB_OAD_SCAN_START:
                _OadScanStart();
                return MAPI_TRUE;
#endif
            case EN_DVB_SCAN_FINISH:
            case EN_DVB_PARSER_STOP_ALL:
            case EN_DVB_PARSER_STOP:
            case EN_DVB_EPG_UPDATE_STOP:
            case EN_DVB_OAD_GO_DOWNLOAD:
                if(cEvt.u32Event == EN_DVB_SCAN_FINISH)
                {
                    // avoid the nit forcing update check after fist scan done
                    m_bforceNetworkCheck = MAPI_FALSE;
#if (PERSISTENT_NIT_CABLE_INFO_ENABLE == 1)
                    if (m_ParserCallBackInfo.EventHandler && (NULL != m_pCableDeliveryInfo))
                    {
                        m_ParserCallBackInfo.EventHandler(E_DVB_SI_SAVE_NIT_CABLE_DEL_INFO,
                                                                                (MAPI_U32)m_ParserCallBackInfo.pCallbackReference,(MAPI_U32)m_pCableDeliveryInfo);
                    }
#endif

                    _FreeDeliveryInfo(m_stNit, MAPI_TRUE);
                    _ResetTargetRegion();
                    _ResetRegionalChannelList();
                    if(IS_NORDIC_COUNTRY(m_eCountry))
                    {
                        _NordigSimulRplSrvDelet(m_pCMDB, m_pCurProg);
                        m_ListRPServiceInfo.clear();
                    }
                }
                if(cEvt.u32Event == EN_DVB_PARSER_STOP_ALL)
                {
                    _FreeDeliveryInfo(m_stNit, MAPI_TRUE);
                }
                if((m_enMonitorMode == EN_MW_SI_MONITOR_MODE_CI_SPECIFY_PMT) || (m_enMonitorMode == EN_MW_SI_MONITOR_MODE_MHEG5_SPECIFY_PMT))
                {
                    m_u32MonitorUserData = (INVALID_PID<<16);
                }
                else
                {
                    m_u32MonitorUserData=0;
                }
                m_enMonitorMode = EN_MW_SI_MONITOR_MODE_NORMAL;
                //if(m_bRunning)
                {
                    MW_SI_PARSER_MESSAGE("EN_DVB_SCAN_STOP\n");
                    _StopAllFilter(MAPI_TRUE);//cEvt.u32Event == EN_DVB_PARSER_STOP_ALL ? MAPI_TRUE : MAPI_FALSE);

#if EIT_BUFFER_USE_QUEUE
                    m_bResetEitParser = MAPI_TRUE;
#endif
                    if(cEvt.u32Event != EN_DVB_OAD_GO_DOWNLOAD)
                    {
                        m_bRunning = MAPI_FALSE;
                        m_eParserMode = E_DVB_IDLE;
                    }
                    else
                    {
                        m_eParserMode = E_DVB_OAD_DOWNLOAD;
                    }

                    m_u32ServiceMoveTimer = INVALID_TIME;
                    m_u32ServiceRelocTimer = INVALID_TIME;
                    m_u8CurRatingChkValue = 0;
                    m_bEnableBouquetFilter=MAPI_FALSE;
                    m_bGotBatInScan=MAPI_FALSE;

                    m_enQuickScanState = EN_QUICK_SCAN_STATE_IDLE;
                    FREE(m_pstAllSdt);

#if (ASTRA_SGT_ENABLE == 1)
                    m_enSgtScanState = E_SGT_SCAN_STATE_IDLE;

                    if(m_pSgtParser !=NULL)
                    {
                        _DeleteParser((MAPI_U32)m_pSgtParser, MAPI_FALSE);
                    }
                    if(MW_DVB_S_PARSER== m_eParserType)
                    {
                        _ResetSgtScanInfo();
                        m_DvbsScanTpMap.clear();
                    }
#endif

                }
                return MAPI_TRUE;
            case EN_DVB_PARSER_STOP_CLOCK:
            {
                if(m_pTotParser)
                {
                    _DeleteParser((MAPI_U32)m_pTotParser, MAPI_FALSE);
                }
                if(m_pTdtParser)
                {
                    _DeleteParser((MAPI_U32)m_pTdtParser, MAPI_FALSE);
                }
            }
            return MAPI_FALSE;
            case EN_DVB_RESET_AIT:
            {
                if(m_pAitParser)
                {
                    _DeleteParser((MAPI_U32)m_pAitParser, MAPI_FALSE);
                }
                m_stAit.m_u8Version = INVALID_PSI_SI_VERSION;
                m_u32AitMonitorTimer = _GetTime0();
                //m_u32AitMonitorTimer=m_u32AitMonitorTimer>2000?m_u32AitMonitorTimer-2000:0;//need wait HBBTV application
                //printf("%s......%d %d\n",__PRETTY_FUNCTION__,_GetTime0(),m_u32AitMonitorTimer);
            }
            return MAPI_FALSE;
#if (DVBC_SYSTEM_ENABLE == 1)
            case EN_DVB_QUICK_SCAN_START:
            {
                if (m_enQuickScanState == EN_QUICK_SCAN_STATE_IDLE)
                {
                    m_enQuickScanState = EN_QUICK_SCAN_STATE_INIT;
                }
                m_bRunning = MAPI_TRUE;
                _QuickScan();
                return MAPI_TRUE;
            }
#endif
            case EN_DVB_EPG_BARKERCHANNEL_START:
                _StartEPGBarkerChannel();
                return MAPI_TRUE;
            case EN_DVB_FILE_IN_MONITOR_START:
                if(MAPI_FALSE == m_bFileinParserRunning)
                {
                    m_bFileinParserRunning = MAPI_TRUE;
                }
                return MAPI_TRUE;
            case EN_DVB_FILE_IN_MONITOR_STOP:
                if(MAPI_TRUE == m_bFileinParserRunning)
                {
                    //<Dynamic PMT
                    //clean up all parser and related data
                    if(m_pFileinPatParser != NULL)
                        _DeleteParser((MAPI_U32)m_pFileinPatParser, FALSE);

                    for(int j = 0; j < m_stFileinPat.u16ServiceCount; j++)
                    {
                        if(m_pFileinAllPmtParser[j]!=NULL)
                           _DeleteParser((MAPI_U32)m_pFileinAllPmtParser[j], FALSE);
                    }

                    memset(&m_stFileinPat,0,sizeof(m_stFileinPat));
                    m_stFileinPat.u8Version= INVALID_PSI_SI_VERSION;

                    memset(&m_stFileinCurPmt,0,sizeof(m_stFileinCurPmt));
                    m_stFileinCurPmt.u8Version = INVALID_PSI_SI_VERSION;
                    m_bFileinParserRunning = MAPI_FALSE;
                    //>
                }
                return MAPI_TRUE;
#if (ASTRA_SGT_ENABLE == 1)
            case EN_DVB_SGT_SCAN_START:
            {
                if(m_enSgtScanState == E_SGT_SCAN_STATE_IDLE)
                {
                   _ResetSgtScanInfo();
                   m_enSgtScanState = E_SGT_SCAN_STATE_CREATE_PAT_NIT;
                }
                m_bRunning = MAPI_TRUE;
                _SGTScan();
                return MAPI_TRUE;
            }
#endif
            case EN_DVB_EPG_UPDATE_START:
                _EPGUpdateStart();
                return MAPI_TRUE;
        }
    }
    return MAPI_FALSE;
}

void MW_DVB_SI_PSI_Parser::_EPG_Update_Monitor()
{
#if (OAD_ENABLE == 1)
    //for oad download lost DDB and update fail issue
    if(m_bOadStopEit == MAPI_TRUE)
    {
        return;
    }
#endif
    _Create_EIT_All_PF_Filter();
    _Create_EIT_Schedule_Filter1();
    _Create_EIT_Schedule_Filter2();
}

void MW_DVB_SI_PSI_Parser::_Table_Monitor()
{
    if(m_enMonitorMode == EN_MW_SI_MONITOR_MODE_NORMAL)
    {
#if (ISDB_SYSTEM_ENABLE == 1)
        if(IsMonitorOFF() == MAPI_TRUE)
        {
            return ;
        }
#endif
        _TDT_Monitor();
        _TOT_Monitor();
        _EIT_Monitor();

        if( m_bEPGBarkerChannelWorking == MAPI_FALSE )
        {
            _PMT_Monitor();
            _PAT_Monitor();
            _SDT_Monitor();
#if ((OAD_ENABLE == 1) && (SDTT_OAD_ENABLE==1))
            _SDTT_Monitor();
#endif

#if (CI_PLUS_ENABLE == 1)
            if(m_bIsOpMode == MAPI_FALSE)
#endif
            {
                _NIT_Monitor();
            }
            _RCT_Monitor();
            _PMT_SpecialService_Monitor();
            _PMT_Other_Monitor();
            //SDT_Other shall not be supported in Canal Digital - SMATV
            if (!((EN_CABLEOP_CDSMATV == m_enCableOperator) && (MW_DVB_C_PARSER == m_eParserType)))
            {
                _SDT_Other_Monitor();
            }
            _AIT_Monitor();
            if(m_bEnableBouquetFilter)
            {
                _EnBATFilter(PID_SDT_BAT,m_u16BouquetID);
            }
#if (ISDB_SYSTEM_ENABLE == 1)
#if 1//(ISDB_CHANNELLOGO_ENABLE == 1)
            _CDT_Monitor();
#endif
#endif
#if (ASTRA_SGT_ENABLE == 1)
            if(MW_DVB_S_PARSER == m_eParserType)
            {
                _SGT_Monitor();
            }
#endif
        }


#if (OAD_ENABLE == 1)
        if(MW_DVB_SI_PSI_Parser::m_OADParser)
        {
            MW_DVB_SI_PSI_Parser::m_OADParser->SignalProcess();
            MW_DVB_SI_PSI_Parser::m_OADParser->Monitor();
            MW_DVB_SI_PSI_Parser::m_OADParser->Download();
        }
#endif //(OAD_ENABLE == 1)
    }
    else if((m_enMonitorMode == EN_MW_SI_MONITOR_MODE_CI_SPECIFY_PMT) || (m_enMonitorMode == EN_MW_SI_MONITOR_MODE_MHEG5_SPECIFY_PMT))
    {
        if(m_enMonitorMode == EN_MW_SI_MONITOR_MODE_MHEG5_SPECIFY_PMT)
        {
            _TDT_Monitor();
            _TOT_Monitor();
            _EIT_Monitor();
        }
        _PAT_Monitor();

         _PMT_Monitor();

        if(m_enMonitorMode == EN_MW_SI_MONITOR_MODE_CI_SPECIFY_PMT)
        {
            _SDT_Monitor();
#if (CI_PLUS_ENABLE == 1)
            if(m_bIsOpMode == MAPI_FALSE)
#endif
            {
                _NIT_Monitor();
            }
        }
    }
    else if(m_enMonitorMode == EN_MW_SI_MONITOR_MODE_AIT_ONLY)
    {

        _PMT_Monitor();

        _PAT_Monitor();
        _AIT_Monitor();
    }

}

#if (OAD_ENABLE == 1)
void MW_DVB_SI_PSI_Parser::_Table_Monitor_OadScan()
{
    if(m_eParserMode == E_DVB_OAD_SCAN)
    {
        MAPI_BOOL bSkip=MAPI_FALSE;
        if(IS_NORDIC_COUNTRY(m_eCountry))
        {
            _SDT_Monitor();
            if(m_bOADONIDMatch == MAPI_FALSE)
            {
                bSkip= MAPI_TRUE;
            }

        }

        if(bSkip == MAPI_FALSE)
        {
            _NIT_Monitor();
            _TDT_Monitor();
            _TOT_Monitor();
            _PAT_Monitor();
            //_PMT_Monitor();
            _PMT_Other_Monitor();

        }

    }

    if(MW_DVB_SI_PSI_Parser::m_OADParser)
    {
        MW_DVB_SI_PSI_Parser::m_OADParser->SignalProcess();
        MW_DVB_SI_PSI_Parser::m_OADParser->Monitor();
        MW_DVB_SI_PSI_Parser::m_OADParser->Download();
    }
}
#endif

void MW_DVB_SI_PSI_Parser::_SDT_Monitor()
{
#if (OAD_ENABLE == 1)
    if(m_bOadStopEit == MAPI_TRUE)
    {
        return ;
    }
#endif
#if (ISDB_SYSTEM_ENABLE == 1)
    if(IsMonitorOFF(E_SDT_MONITOR_OFF) == MAPI_TRUE)
    {
        return ;
    }
#endif

    if(m_pSdtParser == NULL)
    {
        if((m_u32SdtMonitorTimer == 0) || (_Timer_DiffTimeFromNow(m_u32SdtMonitorTimer) > SDT_MONITOR_PERIOD))
        {
            CRC32_t vSdtCrc(1, m_stSdt.u32CRCValue);
            m_pSdtParser = new (std::nothrow) mapi_si_SDT_parser(m_pSi, m_pDemux);
            if(m_pSdtParser)
            {
#if (ISDB_SYSTEM_ENABLE == 1)
                if(m_pSdtParser->Init(0x2000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_NORMAL, m_stSdt.u8Version)
                        && m_pSdtParser->Start(SDT_MONITOR_TIMEOUT, EN_TABLE_ACTUAL, m_stSdt.wTransportStream_ID, MAPI_TRUE,__GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_SID), &vSdtCrc))

#else
                if(m_pSdtParser->Init(0x2000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_NORMAL, m_stSdt.u8Version)
                        && m_pSdtParser->Start(SDT_MONITOR_TIMEOUT, EN_TABLE_ACTUAL, m_stSdt.wTransportStream_ID, MAPI_TRUE, 0, &vSdtCrc))
#endif
                {
#if (NCD_ENABLE == 1)
                    _UpdateRelocatedServiceList(m_stSdt.wTransportStream_ID);  // update item's state of the relocated service list
#endif
                    m_u32SdtMonitorTimer = _GetTime0();
                    m_s16OpenFilter++;

                }
                else
                {
                    delete m_pSdtParser;
                    m_pSdtParser = NULL;
                }
            }
        }
    }
}
void MW_DVB_SI_PSI_Parser::_SDT_Other_Monitor()
{
    if(!_IsSpecificSupport(E_DVB_SDT_OTHER_ENABLE,&m_eCountry,NULL))
    {
        return;
    }
#if (OAD_ENABLE == 1)
    if(m_bOadStopEit == MAPI_TRUE)
    {
        return;
    }
#endif
    if((m_pSdtOtherParser == NULL) && (m_stSdtOtherInfo.stSdtOtherTS.size() > 1))
    {
        if((m_u32SdtOtherMonitorTimer == 0) || (INVALID_TIME != m_u32ServiceRelocTimer) || (_Timer_DiffTimeFromNow(m_u32SdtOtherMonitorTimer) > SDT_OTHER_MONITOR_PERIOD))
        {

            if(m_stSdtOtherInfo.u8SdtOtherIndex >= m_stSdtOtherInfo.stSdtOtherTS.size())
            {
                m_stSdtOtherInfo.u8SdtOtherIndex = 0;
            }
            if(m_stSdtOtherInfo.stSdtOtherTS.at(m_stSdtOtherInfo.u8SdtOtherIndex).u16TSID == __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_TSID))
            {
                m_stSdtOtherInfo.u8SdtOtherIndex++;
                if(m_stSdtOtherInfo.u8SdtOtherIndex >= m_stSdtOtherInfo.stSdtOtherTS.size())
                {
                    m_stSdtOtherInfo.u8SdtOtherIndex = 0;
                }

            }


            m_pSdtOtherParser = new (std::nothrow) mapi_si_SDT_parser(m_pSi, m_pDemux);
            if(m_pSdtOtherParser)
            {
                if(m_u32ServiceMoveTimer != INVALID_TIME)
                {
                    for(MAPI_U16 i = 0; i < m_stSdtOtherInfo.stSdtOtherTS.size(); i++)
                    {
                        if(m_stSdtOtherInfo.stSdtOtherTS.at(i).u16TSID == m_stCurPmt.stServiceMove.u16NewTSId)
                        {
                            m_stSdtOtherInfo.u8SdtOtherIndex = i;
                            //printf("service mode sdt other monitor time %d\n",_Timer_DiffTimeFromNow(m_u32ServiceMoveTimer));
                            break;
                        }
                    }
                    //m_stSdtOtherInfo.stSdtOtherTS.at(m_stSdtOtherInfo.u8SdtOtherIndex).u8SdtVer = INVALID_PSI_SI_VERSION;
                }
#if (NCD_ENABLE == 1)
                _FindSdtOtherIndexForRelocate();  // find sdt other index to create filter for service relocation
#endif

                if(m_pSdtOtherParser->Init(0x2000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_NORMAL, m_stSdtOtherInfo.stSdtOtherTS.at(m_stSdtOtherInfo.u8SdtOtherIndex).u8SdtVer)
                        && m_pSdtOtherParser->Start(SDT_OTHER_MONITOR_TIMEOUT, EN_TABLE_OTHER,
                        m_stSdtOtherInfo.stSdtOtherTS.at(m_stSdtOtherInfo.u8SdtOtherIndex).u16TSID, MAPI_TRUE))
                {
                    MW_SI_PARSER_UPDATE("%s tsid %x\n", __FUNCTION__, m_stSdtOtherInfo.stSdtOtherTS.at(m_stSdtOtherInfo.u8SdtOtherIndex).u16TSID);
#if (NCD_ENABLE == 1)
                    _UpdateRelocatedServiceList(m_stSdtOtherInfo.stSdtOtherTS.at(m_stSdtOtherInfo.u8SdtOtherIndex).u16TSID);  // update item's state of the relocated service list
#endif
                    m_u32SdtOtherMonitorTimer = _GetTime0();
                    m_s16OpenFilter++;
                    m_stSdtOtherInfo.u8SdtOtherIndex++;
                    if(m_stSdtOtherInfo.u8SdtOtherIndex >= m_stSdtOtherInfo.stSdtOtherTS.size())
                    {
                        m_stSdtOtherInfo.u8SdtOtherIndex = 0;
                    }
                }
                else
                {
                    delete m_pSdtOtherParser;
                    m_pSdtOtherParser = NULL;
                }
            }
        }
    }
}


void MW_DVB_SI_PSI_Parser::_Create_EIT_All_PF_Filter()
{
    if(m_pEitPfAllParser == NULL)
    {
        if((m_u32EitPfAllMonitorTimer == 0) || (_Timer_DiffTimeFromNow(m_u32EitPfAllMonitorTimer) > EIT_MONITOR_PERIOD))
        {
            m_pEitPfAllParser = new (std::nothrow) mapi_si_EIT_parser(m_pSi, m_pDemux);

            if(m_pEitPfAllParser)
            {

                //mapi_system * system = mapi_interface::Get_mapi_system();
                //printf("start EIT %d\n",system->GetSystemTime());

                if(m_pEitPfAllParser->Init(0x1000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_NORMAL, INVALID_PSI_SI_VERSION)
                        && m_pEitPfAllParser->Start(EN_EIT_FILTER_TYPE_PF_ALL, NULL))
                {
                    m_u32EitPfAllMonitorTimer = _GetTime0();
                    m_s16OpenFilter++;

                }
                else
                {
                    delete m_pEitPfAllParser;
                    m_pEitPfAllParser = NULL;
                }
            }
        }
    }
}

void MW_DVB_SI_PSI_Parser::_Create_EIT_Schedule_Filter1()
{
    if(m_pEitSch1Parser == NULL)
    {
        if((m_u32EitSch1MonitorTimer == 0) || (_Timer_DiffTimeFromNow(m_u32EitSch1MonitorTimer) > EIT_MONITOR_PERIOD))
        {
            m_pEitSch1Parser = new (std::nothrow) mapi_si_EIT_parser(m_pSi, m_pDemux);


            if(m_pEitSch1Parser)
            {
                if(m_pEitSch1Parser->Init(0x1000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_NORMAL, INVALID_PSI_SI_VERSION)
                        && m_pEitSch1Parser->Start(EN_EIT_FILTER_TYPE_SCHE_1, NULL))
                {
                    m_u32EitSch1MonitorTimer = _GetTime0();
                    m_s16OpenFilter++;

                }
                else
                {
                    delete m_pEitSch1Parser;
                    m_pEitSch1Parser = NULL;
                }
            }
        }
    }
}

void MW_DVB_SI_PSI_Parser::_Create_EIT_Schedule_Filter2()
{
    if(m_pEitSch2Parser == NULL)
    {
        if((m_u32EitSch2MonitorTimer == 0) || (_Timer_DiffTimeFromNow(m_u32EitSch2MonitorTimer) > EIT_MONITOR_PERIOD))
        {
            m_pEitSch2Parser = new (std::nothrow) mapi_si_EIT_parser(m_pSi, m_pDemux);
            if(m_pEitSch2Parser)
            {
                if(m_pEitSch2Parser->Init(0x1000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_NORMAL, INVALID_PSI_SI_VERSION)
                        && m_pEitSch2Parser->Start(EN_EIT_FILTER_TYPE_SCHE_2, NULL))
                {
                    m_u32EitSch2MonitorTimer = _GetTime0();
                    m_s16OpenFilter++;

                }
                else
                {
                    delete m_pEitSch2Parser;
                    m_pEitSch2Parser = NULL;
                }
            }
        }
    }
}


void MW_DVB_SI_PSI_Parser::_Create_EIT_Current_PF_Filter()
{
    if(m_pEitPFParser == NULL)
    {
        if((m_u32EitPfMonitorTimer == 0) || (_Timer_DiffTimeFromNow(m_u32EitPfMonitorTimer) > EIT_MONITOR_PERIOD))
        {
            m_pEitPFParser = new (std::nothrow) mapi_si_EIT_parser(m_pSi, m_pDemux);
            if(m_pEitPFParser)
            {
                MAPI_SI_EIT_PF_PARAMETER EitPfVer;
                for(int i = 0; i < MAX_CUR_PF_SEC; i++)
                {
                    if (MAPI_TRUE == m_bForceRetrievePfEIT)
                    {
                        EitPfVer.u8Version[i] = m_EitPfInfo[i].version_number;
                        EitPfVer.u32CRC32[i] = 0;
                        m_bForceRetrievePfEIT = MAPI_FALSE;
                    }
                    else
                    {
                        EitPfVer.u8Version[i] = m_EitPfInfo[i].version_number;
                        EitPfVer.u32CRC32[i] = m_EitPfInfo[i].u32CRC32;
                    }
                }
                EitPfVer.u16TsID = __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_TSID);
                EitPfVer.u16ServiceID = __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_SID);
                if(m_pEitPFParser->Init(0x1000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_NORMAL, INVALID_PSI_SI_VERSION)
                        && m_pEitPFParser->Start(EN_EIT_FILTER_TYPE_PF_CURRENT, &EitPfVer))
                {
                    m_u32EitPfMonitorTimer = _GetTime0();
                    m_s16OpenFilter++;

                }
                else
                {
                    delete m_pEitPFParser;
                    m_pEitPFParser = NULL;
                }
            }
        }
    }
}

void MW_DVB_SI_PSI_Parser::_EIT_Monitor()
{
#if (OAD_ENABLE == 1)
    //for oad download lost DDB and update fail issue
    if(m_bOadStopEit == MAPI_TRUE)
    {
        return;
    }
#endif
#if (ISDB_SYSTEM_ENABLE == 1)
    if(IsMonitorOFF(E_EIT_MONITOR_OFF) == MAPI_TRUE)
    {
        return ;
    }
#endif

#if (EPG_ENABLE == 1)
    _Create_EIT_All_PF_Filter();
    _Create_EIT_Schedule_Filter1();
    _Create_EIT_Schedule_Filter2();
#endif

    _Create_EIT_Current_PF_Filter();
}

//for Filein SI parsing
void MW_DVB_SI_PSI_Parser::_FileinPAT_Monitor()
{

    if((m_pFileinPatParser == NULL) && (m_stFileinPat.u8Version == INVALID_PSI_SI_VERSION))
    {

        m_pFileinPatParser = new (std::nothrow) mapi_si_PAT_parser(m_pSi, m_pDemux);
        if(m_pFileinPatParser)
        {
            if(m_pFileinPatParser->Init(0x2000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_IGNORE_VERSION, INVALID_PSI_SI_VERSION,MAPI_TRUE)
                    && m_pFileinPatParser->Start(PAT_MONITOR_PERIOD))
            {
                m_s16OpenFilter++;
            }
            else
            {
                delete m_pFileinPatParser;
                m_pFileinPatParser = NULL;
            }
        }
    }
}

void MW_DVB_SI_PSI_Parser::_FileinPMT_Monitor()
{
    if(m_stFileinPat.u8Version != INVALID_PSI_SI_VERSION)
    {
        for(int j = 0; j < m_stFileinPat.u16ServiceCount; j++)
        {
            if((m_pFileinAllPmtParser[j] == NULL) && (m_stFileinPat.ServiceIDInfo[j].u16ServiceID == m_u16FileInSrvId))
            {
                m_pFileinAllPmtParser[j] = new (std::nothrow) mapi_si_PMT_parser(m_pSi, m_pDemux);
                if(m_pFileinAllPmtParser[j])
                {
                    if(m_pFileinAllPmtParser[j]->Init(0x2000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_VERSION_CHANGE, m_stFileinCurPmt.u8Version, MAPI_TRUE)
                            && m_pFileinAllPmtParser[j]->Start(PMT_MONITOR_TIMEOUT, m_stFileinPat.ServiceIDInfo[j].u16ServiceID, m_stFileinPat.ServiceIDInfo[j].u16PmtPID, m_stFileinCurPmt.u32CRC32))
                    {

                        m_s16OpenFilter++;

                    }
                    else
                    {
                       // printf("________________________fail to create PMT parser \n");
                        delete m_pFileinAllPmtParser[j];
                        m_pFileinAllPmtParser[j] = NULL;
                    }
                }
            }

        }
    }
}
//<



void MW_DVB_SI_PSI_Parser::_PAT_Monitor()
{
#if (OAD_ENABLE == 1)
    //for oad download lost DDB and update fail issue
    if(m_bOadStopEit == MAPI_TRUE)
    {
        return;
    }
#endif
#if (ISDB_SYSTEM_ENABLE == 1)
    if(IsMonitorOFF(E_PAT_MONITOR_OFF) == MAPI_TRUE)
    {
        return ;
    }
#endif

    if(m_pPatParser == NULL)
    {
        if((m_u32PatMonitorTimer == 0) || (_Timer_DiffTimeFromNow(m_u32PatMonitorTimer) > PAT_MONITOR_PERIOD))
        {
            m_pPatParser = new (std::nothrow) mapi_si_PAT_parser(m_pSi, m_pDemux);
            if(m_pPatParser)
            {
                EN_SI_PSI_PARSER_OPTION eMode = EN_SI_PSI_PARSER_NORMAL;
#if (OAD_ENABLE == 1)
                //for oad download lost DDB and update fail issue
                eMode = (m_bOadStopEit == MAPI_TRUE) ? EN_SI_PSI_PARSER_ONE_SHOT : EN_SI_PSI_PARSER_NORMAL;
#endif
                if(m_pPatParser->Init(0x2000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  eMode, m_stPat.u8Version)
                        && m_pPatParser->Start(PAT_MONITOR_PERIOD, &m_stPat.vPatCRC))
                {
                    m_u32PatMonitorTimer = _GetTime0();
                    m_s16OpenFilter++;

                }
                else
                {
                    delete m_pPatParser;
                    m_pPatParser = NULL;
                }
            }
        }
    }
}

void MW_DVB_SI_PSI_Parser::_PMT_Monitor()
{
#if (OAD_ENABLE == 1)
    //for oad download lost DDB and update fail issue
    if(m_bOadStopEit == MAPI_TRUE)
    {
        return;
    }
#endif
#if (ISDB_SYSTEM_ENABLE == 1)
    if(IsMonitorOFF(E_PMTa_MONITOR_OFF) == MAPI_TRUE)
    {
        return ;
    }
#endif

    if(m_pCurPmtParser == NULL)
    {
        if(m_u32ServiceMoveTimer != INVALID_TIME)
        {
            m_u32WaitPMTTimer = INVALID_TIME;
            m_u16OldSID = INVALID_SERVICE_ID;
            m_u16OldPMTPID = INVALID_PMT_PID;
            if(_Timer_DiffTimeFromNow(m_u32ServiceMoveTimer) < SERVICE_MOVE_TIMEOUT)
            {
                return;
            }
            m_u32ServiceMoveTimer = INVALID_TIME;
            MW_SI_PARSER_UPDATE("wait service move time out\n");
        }
        else if(INVALID_TIME != m_u32ServiceRelocTimer)
        {
            if(_Timer_DiffTimeFromNow(m_u32ServiceRelocTimer) < SERVICE_RELOC_TIMEOUT)
            {
                return;
            }
            MW_SI_PARSER_UPDATE("wait relocate time out\n");
            m_u32ServiceRelocTimer = INVALID_TIME;
        }
        else if((m_enMonitorMode == EN_MW_SI_MONITOR_MODE_CI_SPECIFY_PMT) || (m_enMonitorMode == EN_MW_SI_MONITOR_MODE_MHEG5_SPECIFY_PMT))
        {
            if((m_u32MonitorUserData>>16) == INVALID_PID)
            {
                return;
            }
        }

        if(INVALID_TIME != m_u32WaitPMTTimer)
        {
            if(_Timer_DiffTimeFromNow(m_u32WaitPMTTimer) > WAIT_PMT_TIMEOUT)
            {
                m_u32WaitPMTTimer = INVALID_TIME;
                m_u16OldSID = INVALID_SERVICE_ID;
                m_u16OldPMTPID = INVALID_PMT_PID;
            }
        }

        MAPI_U16 u16ServiceID,u16PmtPID;
        if((m_enMonitorMode == EN_MW_SI_MONITOR_MODE_CI_SPECIFY_PMT) || (m_enMonitorMode == EN_MW_SI_MONITOR_MODE_MHEG5_SPECIFY_PMT))
        {
            u16ServiceID = (MAPI_U16)m_u32MonitorUserData;
            u16PmtPID = (MAPI_U16)(m_u32MonitorUserData>>16);
            MW_SI_PARSER_MESSAGE("CI Specify PMT monitor:SID=0x%x,PMTPID=0x%x\n",u16ServiceID,u16PmtPID);
        }
        else
        {
            u16ServiceID = m_u16OldSID;
            u16PmtPID = m_u16OldPMTPID;
        }
        if (m_ReplacedServiceInfo.bSer_Replacement
            && (m_ReplacedServiceInfo.u16RPPmtPID != INVALID_PMT_PID)
            && (m_ReplacedServiceInfo.enLinkageType == MAPI_SI_LINKAGE_SERVICE_REPLACEMENT_SERVICE))
        {
            u16PmtPID = m_ReplacedServiceInfo.u16RPPmtPID;
        }
        if((m_u32PmtMonitorTimer == 0) || (_Timer_DiffTimeFromNow(m_u32PmtMonitorTimer) > ((MAPI_U32)m_u8PmtMonitorTimerMultiple*PMT_MONITOR_PERIOD)))
        {
            m_u8PmtMonitorTimerMultiple=1;
            m_pCurPmtParser = new (std::nothrow) mapi_si_PMT_parser(m_pSi, m_pDemux);
            if(m_pCurPmtParser)
            {
                EN_SI_PSI_PARSER_OPTION eMode = EN_SI_PSI_PARSER_NORMAL;
#if (OAD_ENABLE == 1)
                //for oad download lost DDB and update fail issue
                eMode = (m_bOadStopEit == MAPI_TRUE) ? EN_SI_PSI_PARSER_ONE_SHOT : EN_SI_PSI_PARSER_NORMAL;
#endif
                if(m_pCurPmtParser->Init(0x2000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  eMode, m_stCurPmt.u8Version)
                        && m_pCurPmtParser->Start((u16ServiceID != 0) ? PMT_MOVE_MONITOR_TIMEOUT : PMT_MONITOR_TIMEOUT, (u16ServiceID != INVALID_SERVICE_ID) ? u16ServiceID : __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_SID), (u16PmtPID != INVALID_PMT_PID) ? u16PmtPID : __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_PMT), m_stCurPmt.u32CRC32))
                {
                    //printf("pmt monitor...ONID %x TSID %x SID %x pmt %x\n",__GetID(m_pCurProg,EN_ID_ONID),__GetID(m_pCurProg,EN_ID_TSID),__GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_SID),
                    //__GetID(m_pCurProg,EN_ID_PMT));
                    m_u16OldSID = INVALID_SERVICE_ID;
                    m_u16OldPMTPID = INVALID_PMT_PID;
                    m_u32WaitPMTTimer = INVALID_TIME;
                    m_u32PmtMonitorTimer = _GetTime0();
                    m_s16OpenFilter++;

                }
                else
                {
                    delete m_pCurPmtParser;
                    m_pCurPmtParser = NULL;
                }
            }
        }
    }
}

void MW_DVB_SI_PSI_Parser::_PMT_Other_Monitor()
{
#if (OAD_ENABLE == 1)
    //for oad download lost DDB and update fail issue
    if(m_bOadStopEit == MAPI_TRUE)
    {
        return;
    }
#endif
#if (ISDB_SYSTEM_ENABLE == 1)
    if(IsMonitorOFF(E_PMTo_MONITOR_OFF) == MAPI_TRUE)
    {
        return ;
    }
#endif

    if((m_pOtherPmtParser == NULL) && (m_stPat.u8Version != INVALID_PSI_SI_VERSION))
    {
        if((m_u32PmtOtherMonitorTimer == 0) || (_Timer_DiffTimeFromNow(m_u32PmtOtherMonitorTimer) > PMT_OTHER_MONITOR_PERIOD))
        {
            if(m_u8PmtIndex >= m_stPat.u16ServiceCount)
            {
                m_u8PmtIndex = 0;
            }
            if(m_eParserMode != E_DVB_OAD_SCAN)
            {
                if(m_stPat.ServiceIDInfo[m_u8PmtIndex].u16ServiceID == __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_SID))
                {
                    m_u8PmtIndex++;
                    if(m_u8PmtIndex >= m_stPat.u16ServiceCount)
                    {
                        m_u8PmtIndex = 0;
                    }

                }
            }

            m_pOtherPmtParser = new (std::nothrow) mapi_si_PMT_parser(m_pSi, m_pDemux);
            if(m_pOtherPmtParser)
            {
                if(m_pOtherPmtParser->Init(0x2000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_NORMAL, 0xFF)
                        && m_pOtherPmtParser->Start(PMT_OTHER_MONITOR_TIMEOUT, m_stPat.ServiceIDInfo[m_u8PmtIndex].u16ServiceID, m_stPat.ServiceIDInfo[m_u8PmtIndex].u16PmtPID, 0))
                {
                    m_u32PmtOtherMonitorTimer = _GetTime0();
                    m_s16OpenFilter++;
                    m_u8PmtIndex++;
                }
                else
                {
                    delete m_pOtherPmtParser;
                    m_pOtherPmtParser = NULL;
                }
            }
        }
    }
}

void MW_DVB_SI_PSI_Parser::_NIT_Monitor()
{
#if (OAD_ENABLE == 1)
    if(m_bOadStopEit == MAPI_TRUE)
    {
        return;
    }
#endif
#if (ISDB_SYSTEM_ENABLE == 1)
    if(IsMonitorOFF(E_NIT_MONITOR_OFF) == MAPI_TRUE)
    {
        return ;
    }
#endif

    if(m_pNitParser == NULL)
    {
        if((m_u32NitMonitorTimer == 0) || (_Timer_DiffTimeFromNow(m_u32NitMonitorTimer) > NIT_MONITOR_PERIOD))
        {
            m_pNitParser = new (std::nothrow) mapi_si_NIT_parser(m_pSi, m_pDemux);
            if(m_pNitParser)
            {
                if(m_pNitParser->Init(0x2000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_NORMAL, m_stNit.u8Version)
                        && m_pNitParser->Start(MAPI_DMX_TIMEOUT_INFINITE, (m_eParserType == MW_DVB_C_PARSER), __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_NID)))
                {
                    m_u32NitMonitorTimer = _GetTime0();
                    m_s16OpenFilter++;

                }
                else
                {
                    delete m_pNitParser;
                    m_pNitParser = NULL;
                }
            }
        }
    }
}



void MW_DVB_SI_PSI_Parser::_TOT_Monitor()
{
#if (ISDB_SYSTEM_ENABLE == 0)
    if(m_eClockMode != MW_DVB_CLOCK_AUTO)
    {
        return;
    }
#endif
    if(m_pTotParser == NULL)
    {
        if((m_u32TotMonitorTimer == 0) || (_Timer_DiffTimeFromNow(m_u32TotMonitorTimer) > TOT_MONITOR_PERIOD))
        {
            m_pTotParser = new (std::nothrow) mapi_si_TOT_parser(m_pSi, m_pDemux);
            if(m_pTotParser)
            {
                if(m_pTotParser->Init(0, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_NORMAL, INVALID_PSI_SI_VERSION)
                        && m_pTotParser->Start())
                {
                    m_u32TotMonitorTimer = _GetTime0();
                    m_s16OpenFilter++;

                }
                else
                {
                    delete m_pTotParser;
                    m_pTotParser = NULL;
                }
            }
        }
    }
}
void MW_DVB_SI_PSI_Parser::_TDT_Monitor()
{
    if(m_eClockMode != MW_DVB_CLOCK_AUTO)
    {
        return;
    }
    if(m_pTdtParser == NULL)
    {
        if((m_u32TdtMonitorTimer == 0) || (_Timer_DiffTimeFromNow(m_u32TdtMonitorTimer) > TDT_MONITOR_PERIOD))
        {
            m_pTdtParser = new (std::nothrow) mapi_si_TDT_parser(m_pSi, m_pDemux);
            if(m_pTdtParser)
            {
                if(m_pTdtParser->Init(0, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_NORMAL, INVALID_PSI_SI_VERSION)
                        && m_pTdtParser->Start())
                {
                    m_u32TdtMonitorTimer = _GetTime0();
                    m_s16OpenFilter++;

                }
                else
                {
                    delete m_pTdtParser;
                    m_pTdtParser = NULL;
                }
            }
        }
    }
}

void MW_DVB_SI_PSI_Parser::_AIT_Monitor()
{
#if (OAD_ENABLE == 1)
    if(m_bOadStopEit == MAPI_TRUE)
    {
        return;
    }
#endif
#if (HBBTV_ENABLE ==1)

    if((m_eParserMode != E_DVB_MONITOR) || (m_stCurPmt.u8Version == INVALID_PSI_SI_VERSION) || (m_stCurPmt.stAppSignalInfo.u16AitPID == INVALID_PID))
    {
        return;
    }

    if(m_pAitParser == NULL)
    {
        if((m_u32AitMonitorTimer == 0) || (_Timer_DiffTimeFromNow(m_u32AitMonitorTimer) > AIT_MONITOR_PERIOD))
        {
            m_pAitParser = new (std::nothrow) mapi_si_AIT_parser(m_pSi, m_pDemux);
            if(m_pAitParser)
            {

                if(m_pAitParser->Init(0x2000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_NORMAL, m_stAit.m_u8Version)
                        && m_pAitParser->Start(AIT_MONITOR_TIMEOUT, m_stCurPmt.stAppSignalInfo.u16AitPID, HBBTV_APP_TYPE))
                {
                    MW_SI_PARSER_MESSAGE("AIT Monitor>>Started at [AIT PID:0x%x][SID:0x%x]\n", m_stCurPmt.stAppSignalInfo.u16AitPID, m_stCurPmt.wServiceID);
                    m_u32AitMonitorTimer = _GetTime0();
                    m_s16OpenFilter++;

                }
                else
                {
                    delete m_pAitParser;
                    m_pAitParser = NULL;
                }
            }
        }
    }
#elif (GINGA_ENABLE == 1)
    if((m_eParserMode != E_DVB_MONITOR) || (m_stCurPmt.u8Version == INVALID_PSI_SI_VERSION) || (m_stCurPmt.stAppSignalInfo.u16AitPID == INVALID_PID))
    {
        return;
    }

    if(m_pAitParser == NULL)
    {
        if((m_u32AitMonitorTimer == 0) || (_Timer_DiffTimeFromNow(m_u32AitMonitorTimer) > AIT_MONITOR_PERIOD))
        {
            m_pAitParser = new (std::nothrow) mapi_si_AIT_parser(m_pSi, m_pDemux);
            if(m_pAitParser)
            {
                if(m_pAitParser->Init(0x2000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_NORMAL, m_stAit.m_u8Version)
                    && m_pAitParser->Start(AIT_MONITOR_TIMEOUT, m_stCurPmt.stAppSignalInfo.u16AitPID, m_stCurPmt.stAppSignalInfo.astAppSignalling[0].u16AppType))
                {
                    MW_SI_PARSER_MESSAGE("AIT Monitor>>Started at [AIT PID:0x%x][SID:0x%x]\n", m_stCurPmt.stAppSignalInfo.u16AitPID, m_stCurPmt.wServiceID);
                    m_u32AitMonitorTimer = _GetTime0();
                    m_s16OpenFilter++;
                }
                else
                {
                    delete m_pAitParser;
                    m_pAitParser = NULL;
                }
            }
        }
    }
#endif
}
void MW_DVB_SI_PSI_Parser::GetFullCRID(string& sIO_CRID)
{
    BOOL bHasCRIDHeader = FALSE;
    if(!mapi_epg_criddb::IsCRIDHasAuthority(sIO_CRID.length(), (const U8*)(sIO_CRID.c_str()), bHasCRIDHeader))
    {
        string sOriCRID = sIO_CRID;
        sIO_CRID = "";
        if(!mapi_epg_criddb::GetInstance()->GetFullCRID(m_TsInfo.u16ONID, m_TsInfo.u16TSID, m_stCurPmt.wServiceID, sOriCRID, sIO_CRID))
        {
            U16 NID = __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_NID);
            if(NID!=0)
            {
                MAPI_U16 u16NetID = NID;
                mapi_epg_criddb::GetInstance()->GetFullCRID(u16NetID, sOriCRID, sIO_CRID);
                //printf("GetFull CRID: %s :%d\n", sIO_CRID.c_str(), __LINE__);
            }
            else
            {
                //printf("GetFull CRID fail (NID: %d): %d\n", NID,  __LINE__);
            }
        }
        else
        {
            //printf("GetFull CRID: %s :%d\n", sIO_CRID.c_str(), __LINE__);
        }
    }
    else if(bHasCRIDHeader)
    {
        mapi_epg_criddb::OmitCRIDHeader(sIO_CRID);
    }
    mapi_epg_criddb::ToLowerCase(sIO_CRID);
}
void MW_DVB_SI_PSI_Parser::GetFullCRID(std::string& sIO_CRID, U16 u16ONID, U16 u16TSID, U16 u16SID)
{
    BOOL bHasCRIDHeader = FALSE;
    if(!mapi_epg_criddb::IsCRIDHasAuthority(sIO_CRID.length(), (const U8*)(sIO_CRID.c_str()), bHasCRIDHeader))
    {
        std::string sOriCRID = sIO_CRID;
        sIO_CRID = "";
        //printf("[%s %d] (%u, %u, %u)\n",__FUNCTION__,__LINE__, ONID, TSID, SID);
        if(!mapi_epg_criddb::GetInstance()->GetFullCRID(u16ONID, u16TSID, u16SID, sOriCRID, sIO_CRID))
        {
            U16 NID = __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_NID);
            if(NID!=0)
            {
                MAPI_U16 u16NetID = NID;
                mapi_epg_criddb::GetInstance()->GetFullCRID(u16NetID, sOriCRID, sIO_CRID);
                //printf("GetFull CRID: %s :%d\n", sIO_CRID.c_str(), __LINE__);
            }
            else
            {
                //printf("GetFull CRID fail (NID: %d): %d\n", NID,  __LINE__);
            }
        }
        else
        {
            //printf("GetFull CRID: %s :%d\n", sIO_CRID.c_str(), __LINE__);
        }
    }
    else if(bHasCRIDHeader)
    {
        mapi_epg_criddb::OmitCRIDHeader(sIO_CRID);
    }
    mapi_epg_criddb::ToLowerCase(sIO_CRID);
    //printf("[%s %d] %s :%d\n",__FUNCTION__,__LINE__, sIO_CRID.c_str(), __LINE__);
}
void MW_DVB_SI_PSI_Parser::_RCT_Monitor()
{
    //Temporily disable the _RCT_Monitor since UI is not ready for trailer booking feature.
    #if 1
    if(INVALID_PSI_SI_VERSION == m_stCurPmt.u8Version)
    {
        if(NULL != m_pRctParser)
        {
            _DeleteParser((MAPI_U32)m_pRctParser, MAPI_FALSE);
        }
        return;
    }

    if(m_stCurPmt.u16RCTPid ==0)
    {
        mapi_si_RCT_Table* pRCT = mapi_epg_criddb::GetInstance()->GetRCT();
        if(NULL == pRCT)
        {
            return;
        }

        pRCT->Reset();
        return ;
    }

    U16 u16RCTVersionNumber =INVALID_PSI_SI_VERSION;
    mapi_si_RCT_Table* pRCT = mapi_epg_criddb::GetInstance()->GetRCT();
    if(NULL != pRCT)
    {
        u16RCTVersionNumber=pRCT->GetVersionNum();
    }

    if((m_pRctParser == NULL) || (m_pRctParser->GetProcessingPID()!=m_stCurPmt.u16RCTPid) || (m_pRctParser->GetProcessingSID()!=m_stCurPmt.wServiceID))
    {
        if(NULL != m_pRctParser)
        {
            _DeleteParser((MAPI_U32)m_pRctParser, MAPI_FALSE);
        }
        if((m_u32RctMonitorTimer == 0) || (_Timer_DiffTimeFromNow(m_u32RctMonitorTimer) > RCT_MONITOR_PERIOD))
        {
            m_pRctParser = new (std::nothrow) mapi_si_RCT_parser(m_pSi, m_pDemux);
            if(m_pRctParser)
            {
                if(m_pRctParser->Init(0x2000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_NORMAL, u16RCTVersionNumber)
                        && m_pRctParser->Start(m_stCurPmt.u16RCTPid, m_stCurPmt.wServiceID))
                {
                    m_u32RctMonitorTimer = _GetTime0();
                    m_s16OpenFilter++;
                }
                else
                {
                    delete m_pRctParser;
                    m_pRctParser = NULL;
                }
            }
        }
    }
    #endif
}
void MW_DVB_SI_PSI_Parser::_ResetStatus(void)
{
    mapi_si_PAT_parser::Reset(m_stPat);
    _free_SI_TABLE_NIT(m_stNit);
    memset(&m_stNit, 0, sizeof(m_stNit));
    m_stNit.u8Version = INVALID_PSI_SI_VERSION;
    memset(&m_stSdt, 0, sizeof(m_stSdt));
    m_stSdt.u8Version = INVALID_PSI_SI_VERSION;
    memset(&m_stCurPmt, 0, sizeof(m_stCurPmt));
    m_stCurPmt.u8Version = INVALID_PSI_SI_VERSION;
    m_stCurPmt.u32CRC32 = 0;
    m_u32CurPMTCRC32 = 0;
    mapi_si_RCT_Table* pRCT = mapi_epg_criddb::GetInstance()->GetRCT();
    if(NULL != pRCT)
    {
        pRCT->Reset();
    }

    m_stAit.m_u8Version = INVALID_PSI_SI_VERSION;
    if(!m_bGotBatInScan)
    {
        mapi_utility::freeList(&m_stBat.pstTSInfoList);
        memset(&m_stBat, 0, sizeof(m_stBat));
        m_stBat.u8Version = INVALID_PSI_SI_VERSION;
    }
    for(int i = 0; i < MAX_CHANNEL_IN_MUX; i++)
    {
        memset(&m_astAllPmt[i], 0, sizeof(m_astAllPmt[i]));
        m_astAllPmt[i].u8Version = INVALID_PSI_SI_VERSION;
    }
#if (ASTRA_SGT_ENABLE == 1)
    _free_SI_TABLE_SGT(m_stSgt);
    memset(&m_stSgt, 0, sizeof(m_stSgt));
    m_stSgt.u8Version = INVALID_PSI_SI_VERSION;
#endif
    memset(&m_CurServiceInfo, 0, sizeof(m_CurServiceInfo));
    m_CurServiceInfo.u8PmtVersion = INVALID_PSI_SI_VERSION;
    m_CurServiceInfo.wPCRPid = MAPI_DMX_INVALID_PID;
    m_CurServiceInfo.stVideoInfo.wVideoPID = MAPI_DMX_INVALID_PID;
    m_CurServiceInfo.bEit_pf_flag = MAPI_TRUE;//coverity check bug(missing lock), no change need
    m_CurServiceInfo.bEit_schedule_flag = MAPI_TRUE;
    m_CurServiceInfo.stVideoInfo.wVideoPID = INVALID_PID;
    m_CurServiceInfo.wCCPid = INVALID_PID;
    for(int i=0;i<MAX_AUD_LANG_NUM;i++)
    {
        m_CurServiceInfo.stAudInfo[i].u16AudPID = INVALID_PID;
    }
    //Don't memset all to ZERO. Keep replacement serive for switch back.
    //memset(&m_CurRPServiceInfo, 0, sizeof(MW_DVB_RP_SERVICE_INFO));
    m_CurRPServiceInfo.bSer_Replacement = MAPI_FALSE;

#if (ISDB_SYSTEM_ENABLE == 1)
    m_u8PmtParentalControl = INVALID_PARENTAL_RATING;

#if 1//(ISDB_CHANNELLOGO_ENABLE == 1)
    m_bEnableCdtMonitorFlag = MAPI_FALSE;
    FREE(m_CurServiceLogoInfo.pu8LogoData);
    memset(&m_CurServiceLogoInfo, 0, sizeof(DESC_LOGO_TRANSMISSION));
#endif
#endif
#if ((OAD_ENABLE == 1) && (SDTT_OAD_ENABLE==1))
    if(m_OADParser != NULL)
    {
        m_OADParser->ResetSDTT_Broadcast_Status();
    }
#endif
    m_u32PostSendCiProtectionEventTime = 0;
}
void MW_DVB_SI_PSI_Parser::_ResetInfo(MAPI_U8 u8Type)
{
    //m_s16OpenFilter = 0;
    if(u8Type & RESET_INFO)
    {
        m_u8PmtIndex = 0;
        //memset(m_pAllPmtParser, NULL, MAX_CHANNEL_IN_MUX * sizeof(mapi_si_PMT_parser *));
        //memset(m_aPMTWaitFilter, MAPI_FALSE, MAX_CHANNEL_IN_MUX * sizeof(MAPI_BOOL));
        for(int j = 0; j < MAX_CHANNEL_IN_MUX; j++)
        {
            if(m_pAllPmtParser[j])
            {
                _DeleteParser((MAPI_U32)m_pAllPmtParser[j] , MAPI_FALSE);
                m_aPMTWaitFilter[j] = MAPI_FALSE;
            }
        }
        memset(&m_TsInfo, 0, sizeof(m_TsInfo));
        memset(m_ProgramInfo, 0, sizeof(MW_DVB_PROGRAM_INFO)*MAX_CHANNEL_IN_MUX);

        for(int i = 0; i < MAX_CHANNEL_IN_MUX; i++)
        {
            m_ProgramInfo[i].bIsSelectable = MAPI_TRUE;
            m_ProgramInfo[i].bIsVisible = MAPI_TRUE;
            m_ProgramInfo[i].u16LCN = MAPI_SI_INVALID_LCN;
            m_ProgramInfo[i].u16SimuLCN = MAPI_SI_INVALID_LCN;
            m_ProgramInfo[i].u8PatVer = INVALID_PSI_SI_VERSION;
            m_ProgramInfo[i].u8PmtVer = INVALID_PSI_SI_VERSION;
            m_ProgramInfo[i].u8SdtVer = INVALID_PSI_SI_VERSION;
            m_ProgramInfo[i].u8NitVer = INVALID_PSI_SI_VERSION;
#if (NVOD_ENABLE==1)
            for(int j=0;j<MAX_NVOD_TIME_SHIFT_SRV_NUM;j++)
            {
                m_ProgramInfo[i].stNvodRealSrv[j].u16TsId=INVALID_TS_ID;
                m_ProgramInfo[i].stNvodRealSrv[j].u16OnId=INVALID_ON_ID;
                m_ProgramInfo[i].stNvodRealSrv[j].u16SrvId=INVALID_SERVICE_ID;
            }
            m_ProgramInfo[i].u16NvodRefSrvID=INVALID_SERVICE_ID;
#endif
#if (ASTRA_SGT_ENABLE == 1)
            m_ProgramInfo[i].u8SgtVer = INVALID_PSI_SI_VERSION;
#endif
        }
    }
    if(u8Type & RESET_EIT)
    {
        if(m_pEitPFParser)
        {
            //m_pSi->DeAttachParser(m_pNitParser);
            //m_pNitParser->Stop();
            m_s16OpenFilter--;
            delete m_pEitPFParser;
            m_pEitPFParser = NULL;
        }
        memset(m_EitPfInfo, 0, sizeof(MAPI_EIT_CUR_EVENT_PF) * 2);
        m_EitPfInfo[0].version_number = INVALID_PSI_SI_VERSION;
        m_EitPfInfo[1].version_number = INVALID_PSI_SI_VERSION;
        memset(m_PFComponentInfo,0,sizeof(MAPI_SI_COMPONENT_INFO)*2);
        m_bForceRetrievePfEIT = MAPI_FALSE;
    }
    if(u8Type & RESET_RCT)
    {
        if(m_pRctParser)
        {
            //m_pSi->DeAttachParser(m_pNitParser);
            //m_pNitParser->Stop();
            m_s16OpenFilter--;
            delete m_pRctParser;
            m_pRctParser = NULL;
        }
    }
    if(u8Type & RESET_SDT_OTHER)
    {
        m_stSdtOtherInfo.u8SdtOtherIndex = 0;
        m_stSdtOtherInfo.u16NID = INVALID_NID;
        m_stSdtOtherInfo.stSdtOtherTS.clear();
        m_stSdtOtherInfo.bValid = MAPI_FALSE;
    }
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::_AddSdtOther(MAPI_U16 u16TSID)
{
    vector<MW_SI_SDT_OTHER_TS>::iterator it;
    MAPI_U16 u16OldTSID = INVALID_TS_ID;

    if (m_stSdtOtherInfo.stSdtOtherTS.size()>0)
    {
        u16OldTSID = m_stSdtOtherInfo.stSdtOtherTS.at(m_stSdtOtherInfo.u8SdtOtherIndex).u16TSID;
    }
    for (it = m_stSdtOtherInfo.stSdtOtherTS.begin(); it != m_stSdtOtherInfo.stSdtOtherTS.end(); it++)
    {
        if(it->u16TSID == u16TSID)
        {
            return MAPI_TRUE;
        }
    }
    if (m_stSdtOtherInfo.stSdtOtherTS.size()<MAPI_SI_MAX_TS_IN_NETWORK)
    {
        MW_SI_SDT_OTHER_TS stSdtTS;
        stSdtTS.u16TSID = u16TSID;
        stSdtTS.u8SdtVer = INVALID_PSI_SI_VERSION;
        m_stSdtOtherInfo.stSdtOtherTS.push_back(stSdtTS);

        for (MAPI_U16 i=0; i<m_stSdtOtherInfo.stSdtOtherTS.size();i++)
        {
            if (m_stSdtOtherInfo.stSdtOtherTS.at(i).u16TSID == u16OldTSID)
            {
                m_stSdtOtherInfo.u8SdtOtherIndex = i;
                break;
            }
        }
        return MAPI_TRUE;
    }
    else
    {
        return MAPI_FALSE;
    }
}
MAPI_BOOL MW_DVB_SI_PSI_Parser::_ClearSdtOther(MAPI_U16 u16TSID)
{
    vector<MW_SI_SDT_OTHER_TS>::iterator it;
    MAPI_U16 u16Pos;
    MAPI_U16 u16OldTSID = m_stSdtOtherInfo.stSdtOtherTS.at(m_stSdtOtherInfo.u8SdtOtherIndex).u16TSID;
    for (it = m_stSdtOtherInfo.stSdtOtherTS.begin(); it != m_stSdtOtherInfo.stSdtOtherTS.end(); it++)
    {
        if(it->u16TSID == u16TSID)
        {
            m_stSdtOtherInfo.stSdtOtherTS.erase(it);

            for (u16Pos=0; u16Pos<m_stSdtOtherInfo.stSdtOtherTS.size();u16Pos++)
            {
                if (m_stSdtOtherInfo.stSdtOtherTS.at(u16Pos).u16TSID == u16OldTSID)
                {
                    m_stSdtOtherInfo.u8SdtOtherIndex = u16Pos;
                    break;
                }
            }
            if (u16Pos>=m_stSdtOtherInfo.stSdtOtherTS.size())
            {
                m_stSdtOtherInfo.u8SdtOtherIndex = 0;
            }
            return MAPI_TRUE;
        }
    }
    return MAPI_FALSE;
}

void MW_DVB_SI_PSI_Parser::_BuildSdtOtherInfo(DVB_PROG *pProg, DVB_CM *pCMDB, DVB_MUX *pMux, DVB_NETWORK *pNetwork)
{
    DVB_PROG* pNextProg;
    DVB_MUX *_pMux = NULL;
    DVB_NETWORK *_pNetwork = NULL;
    if(m_stSdtOtherInfo.u16NID == INVALID_NID)
    {
        m_stSdtOtherInfo.bValid = MAPI_TRUE;
        //MAPI_U32 u32startTime=_GetTime0();
        m_stSdtOtherInfo.stSdtOtherTS.clear();
        m_stSdtOtherInfo.u8SdtOtherIndex = 0;
        m_stSdtOtherInfo.u16NID = __GetID(pProg, pMux, pNetwork, EN_ID_NID);
        MW_DTV_CM_DB_scope_lock lock(pCMDB);
        pNextProg = pCMDB->GetByIndex(0);
        while(pNextProg)
        {
            _pMux = __GetMux(pNextProg, pCMDB);
            _pNetwork = __GetNetwork(pMux, pCMDB);
            if((NULL != _pMux) && (NULL != _pNetwork))
            {
                if(__GetID(pNextProg, _pMux, _pNetwork, EN_ID_NID) == m_stSdtOtherInfo.u16NID)
                {
                    if(_AddSdtOther(__GetID(pNextProg, _pMux, _pNetwork, EN_ID_TSID)) == MAPI_FALSE)
                    {
                        break;
                    }
                }
            }
            pNextProg = pCMDB->GetNext(pNextProg);
        }
        //printf("_BuildSdtOtherInfo use time %d ms\n",_Timer_DiffTimeFromNow(u32startTime) );
    }

}

MAPI_U32 MW_DVB_SI_PSI_Parser::_GetTime0(void)
{
    return mapi_time_utility::GetTime0();
}

MAPI_U32 MW_DVB_SI_PSI_Parser::_Timer_DiffTimeFromNow(MAPI_U32 u32TaskTimer) //unit = ms
{
    return mapi_time_utility::TimeDiffFromNow0(u32TaskTimer);
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::GetServiceCMG(MAPI_U8 *u8DoNotScramble, MAPI_U8 *u8CtrlRemoteAOI, MAPI_U8 *u8DoNotApplyRevococate)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    if(m_stService_CMG.eGotCmgTable != E_CMG_NONE)
    {
        *u8DoNotScramble = m_stService_CMG.u8Do_not_scramble;
        *u8CtrlRemoteAOI = m_stService_CMG.u8CtrlRemoteAccessOverInternet;
        *u8DoNotApplyRevococate = m_stService_CMG.u8Do_not_apply_revocation;
        return MAPI_TRUE;
    }
    else
    {
        *u8DoNotScramble = 0;
        *u8CtrlRemoteAOI = 0;
        *u8DoNotApplyRevococate = 0;
        return MAPI_FALSE;
    }
}
MAPI_BOOL MW_DVB_SI_PSI_Parser::_GetTsLevelCMG(MAPI_SI_CMG_INFO& TsCMG)
{
    MAPI_U16 u16Idx;
    MAPI_U16 u16TsID;
    MAPI_U8 bResult = MAPI_FALSE;

    u16TsID = __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_TSID);
    for(u16Idx = 0; u16Idx < m_stNit.u16TSNumber; u16Idx++)
    {
        if(m_stNit.pstTSInfo[u16Idx].wTransportStream_ID == u16TsID)
        {
            TsCMG = m_stNit.pstTSInfo[u16Idx].m_TsCMG;
            bResult = MAPI_TRUE;
            break;
        }
    }
    return bResult;
}
MAPI_BOOL MW_DVB_SI_PSI_Parser::_GetServiceLevelCMG(MAPI_SI_CMG_INFO& SrvCMG)
{
    MAPI_U16 u16Idx;
    MAPI_U16 u16SrvID;
    MAPI_U8 bResult = MAPI_FALSE;

    u16SrvID = __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_SID);
    for(u16Idx = 0; u16Idx < m_stSdt.wServiceNumber; u16Idx++)
    {
        if(m_stSdt.astServiceInfo[u16Idx].u16ServiceID == u16SrvID)
        {
            SrvCMG = m_stSdt.astServiceInfo[u16Idx].m_CMG;
            bResult = MAPI_TRUE;
            break;
        }
    }

    return bResult;
}
void MW_DVB_SI_PSI_Parser::_SetCMG(MAPI_U8 u8CmgData, E_CMG_TABLE eTable)
{
    m_stService_CMG.u8Do_not_scramble = ((u8CmgData >> 3) & 0x01);
    m_stService_CMG.u8CtrlRemoteAccessOverInternet = ((u8CmgData >> 1) & 0x03);
    m_stService_CMG.u8Do_not_apply_revocation = (u8CmgData & 0x01);
    m_stService_CMG.eGotCmgTable = eTable;
}
void MW_DVB_SI_PSI_Parser::_SetServiceCMG(const MAPI_SI_CMG_INFO& stCmgInfo, E_CMG_TABLE eTable)
{
    MAPI_SI_CMG_INFO stTsCMG, stSrvCMG;
    MAPI_BOOL bExistCurTs = MAPI_FALSE, bExistCurSrv = MAPI_FALSE;

    bExistCurSrv = _GetServiceLevelCMG(stSrvCMG);
    bExistCurTs = _GetTsLevelCMG(stTsCMG);
    switch(eTable)
    {
        case E_CMG_FROM_EIT:
            if(MAPI_TRUE == stCmgInfo.bExist)
            {
                _SetCMG(stCmgInfo.u8CmgData, E_CMG_FROM_EIT);
            }
            else if((MAPI_FALSE == stCmgInfo.bExist) && (m_stService_CMG.eGotCmgTable == E_CMG_FROM_EIT))
            {
                // init. CMG first
                _SetCMG(0, E_CMG_NONE);
                if((MAPI_TRUE == bExistCurSrv) && (MAPI_TRUE == stSrvCMG.bExist))
                {
                    //service level is second priority
                    _SetCMG(stSrvCMG.u8CmgData, E_CMG_FROM_SDT);
                }
                else if((MAPI_TRUE == bExistCurTs) && (MAPI_TRUE == stTsCMG.bExist))
                {
                    // TS level is third priority
                    _SetCMG(stTsCMG.u8CmgData, E_CMG_FROM_NIT);
                }
                else if(MAPI_TRUE == m_stNit.m_NetworkCMG.bExist)
                {
                    // network level is lowest priority
                    _SetCMG(m_stNit.m_NetworkCMG.u8CmgData, E_CMG_FROM_NIT);
                }
            }
            break;
        case E_CMG_FROM_SDT:
            if((MAPI_TRUE == stCmgInfo.bExist) && (MAPI_FALSE == m_EitPfInfo[0].m_CMG.bExist))
            {
                // check the priority of privious state is higher than service level
                _SetCMG(stCmgInfo.u8CmgData, E_CMG_FROM_EIT);
            }
            else if((MAPI_FALSE == stCmgInfo.bExist) && (m_stService_CMG.eGotCmgTable == E_CMG_FROM_SDT))
            {
                // init. CMG first
                _SetCMG(0, E_CMG_NONE);
                if((MAPI_TRUE == bExistCurTs) && (MAPI_TRUE == stTsCMG.bExist))
                {
                    // TS level is third priority
                    _SetCMG(stTsCMG.u8CmgData, E_CMG_FROM_NIT);
                }
                else if(MAPI_TRUE == m_stNit.m_NetworkCMG.bExist)
                {
                    // network level is lowest priority
                    _SetCMG(m_stNit.m_NetworkCMG.u8CmgData, E_CMG_FROM_NIT);
                }
            }
            break;
        case E_CMG_FROM_NIT:
            if((MAPI_TRUE == stCmgInfo.bExist) && (MAPI_FALSE == m_EitPfInfo[0].m_CMG.bExist) &&
                    ((MAPI_FALSE == bExistCurSrv) || (MAPI_FALSE == stSrvCMG.bExist)))
            {
                //there is no cmg data
                _SetCMG(stCmgInfo.u8CmgData, E_CMG_FROM_NIT);
            }
            else if((MAPI_FALSE == stCmgInfo.bExist) && (m_stService_CMG.eGotCmgTable == E_CMG_FROM_NIT))
            {
                // init. CMG first
                _SetCMG(0, E_CMG_NONE);
            }
            break;
        default:
            break;
    }
}
MAPI_S16 MW_DVB_SI_PSI_Parser::_AddTargetCountry(MW_DVB_TARGET_REGION_INFO & stTargetRegionInfo, MAPI_U8* pu8CountryCode)
{
    MAPI_U16 u16Index = stTargetRegionInfo.u8CountryNum;
    MW_DVB_COUNTRY_INFO* pOldInfo;
    for(int i = 0; i < u16Index; i++)
    {
        MW_SI_TARGET_REGION_DBG("%s..%d\n", __FUNCTION__, __LINE__);
        if(memcmp(stTargetRegionInfo.pCountryInfo[i].au8CountryCode, pu8CountryCode, 3) == 0)
        {
            MW_SI_TARGET_REGION_DBG("same country %.*s\n", 3, pu8CountryCode);
            return i;
        }
    }
    pOldInfo = stTargetRegionInfo.pCountryInfo;
    stTargetRegionInfo.pCountryInfo = (MW_DVB_COUNTRY_INFO*)malloc(sizeof(MW_DVB_COUNTRY_INFO) * (u16Index + 1));
    if(stTargetRegionInfo.pCountryInfo == NULL)
    {
        stTargetRegionInfo.pCountryInfo = pOldInfo;
        ASSERT(0);
        return -1;
    }
    if(pOldInfo)
    {
        memcpy(stTargetRegionInfo.pCountryInfo, pOldInfo, sizeof(MW_DVB_COUNTRY_INFO)*(u16Index));
        FREE(pOldInfo);
    }
    memcpy(stTargetRegionInfo.pCountryInfo[u16Index].au8CountryCode, pu8CountryCode, 3);
    stTargetRegionInfo.pCountryInfo[u16Index].u16PrimaryRegionNum = 0;
    stTargetRegionInfo.pCountryInfo[u16Index].pPrimaryRegionInfo = NULL;
    stTargetRegionInfo.u8CountryNum++;
    MW_SI_TARGET_REGION_DBG("add country %.*s total %d\n", 3, pu8CountryCode, stTargetRegionInfo.u8CountryNum);
    return u16Index;
}
MAPI_S16 MW_DVB_SI_PSI_Parser::_AddTargetPrimaryRegion(MW_DVB_TARGET_REGION_INFO & stTargetRegionInfo, MAPI_U16 u16CountryIndex, MAPI_U8 u8PimaryRegion)
{
    MAPI_BOOL bAdd = MAPI_FALSE;
    MAPI_U16 u16Index = stTargetRegionInfo.pCountryInfo[u16CountryIndex].u16PrimaryRegionNum;
    MW_DVB_PRIMARY_REGION_INFO* pOldInfo;
    MW_SI_TARGET_REGION_DBG("%s %d %d\n", __FUNCTION__, u16CountryIndex, u8PimaryRegion);
    for(int i = 0; i < u16Index; i++)
    {
        MW_SI_TARGET_REGION_DBG("%s..%d\n", __FUNCTION__, __LINE__);
        if(stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[i].u8Code == u8PimaryRegion)
        {
            MW_SI_TARGET_REGION_DBG("same primary region %d\n", u8PimaryRegion);
            return i;
        }
    }
    for(int i = 0; i < m_stNit.u8TargetRegionNumber; i++)
    {
        if(memcmp(m_stNit.pastTargetRegionNameInfo[i].u8countryCode, stTargetRegionInfo.pCountryInfo[u16CountryIndex].au8CountryCode, 3))
        {
            MW_SI_TARGET_REGION_DBG("%s..%d\n", __FUNCTION__, __LINE__);
            continue;
        }
        MW_SI_TARGET_REGION_DBG("%s..%d\n", __FUNCTION__, __LINE__);
        for(int j = 0; j < m_stNit.pastTargetRegionNameInfo[i].u8primary_region_num; j++)
        {
            MW_SI_TARGET_REGION_DBG("%s..%d %x %x\n", __FUNCTION__, __LINE__, m_stNit.pastTargetRegionNameInfo[i].stPrimary_region_info[j].u8primary_region_code,
                                    u8PimaryRegion);
            if(m_stNit.pastTargetRegionNameInfo[i].stPrimary_region_info[j].u8primary_region_code == u8PimaryRegion)
            {
                if(bAdd == MAPI_FALSE)
                {
                    pOldInfo = stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo;
                    stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo = (MW_DVB_PRIMARY_REGION_INFO*)malloc(sizeof(MW_DVB_PRIMARY_REGION_INFO) * (u16Index + 1));
                    if(stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo == NULL)
                    {
                        stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo = pOldInfo;
                        ASSERT(0);
                        return -1;
                    }
                    if(pOldInfo)
                    {
                        memcpy(stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo, pOldInfo, sizeof(MW_DVB_PRIMARY_REGION_INFO)*(u16Index));
                        FREE(pOldInfo);
                    }
                    stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16Index].u8Code = u8PimaryRegion;
                    memcpy(stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16Index].name, m_stNit.pastTargetRegionNameInfo[i].stPrimary_region_info[j].Name, MAPI_SI_MAX_REGION_NAME);
                    stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16Index].u16SecondaryRegionNum = 0;
                    stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16Index].pSecondaryRegionInfo = NULL;
                    MW_SI_TARGET_REGION_DBG("add primary region %x %s\n", stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16Index].u8Code,
                                            stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16Index].name);
                    stTargetRegionInfo.pCountryInfo[u16CountryIndex].u16PrimaryRegionNum++;
                }
                else if(m_eLanguage == mapi_dvb_utility::SI_GetLangIndex(m_stNit.pastTargetRegionNameInfo[i].u8langCode))
                {
                    memset(stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16Index].name, 0, MAPI_SI_MAX_REGION_NAME);
                    memcpy(stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16Index].name, m_stNit.pastTargetRegionNameInfo[i].stPrimary_region_info[j].Name, MAPI_SI_MAX_REGION_NAME);
                    MW_SI_TARGET_REGION_DBG("update primary region %xd %s\n", stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16Index].u8Code,
                                            stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16Index].name);
                    return u16Index;

                }
                if(m_eLanguage == mapi_dvb_utility::SI_GetLangIndex(m_stNit.pastTargetRegionNameInfo[i].u8langCode))
                {
                    return u16Index;
                }
                bAdd = MAPI_TRUE;
            }
        }
    }
    return -1;
}
MAPI_S16 MW_DVB_SI_PSI_Parser::_AddTargetSecondaryRegion(MW_DVB_TARGET_REGION_INFO & stTargetRegionInfo, MAPI_U16 u16CountryIndex, MAPI_U16 u16PrimaryIndex, MAPI_U8 u8SecondaryRegion)
{
    MAPI_BOOL bAdd = MAPI_FALSE;
    MAPI_U16 u16Index = stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].u16SecondaryRegionNum;
    MW_DVB_SECONDARY_REGION_INFO* pOldInfo;
    MW_SI_TARGET_REGION_DBG("%s %d %d %d\n", __FUNCTION__, u16CountryIndex, u16PrimaryIndex, u8SecondaryRegion);
    for(int i = 0; i < u16Index; i++)
    {
        MW_SI_TARGET_REGION_DBG("%s..%d\n", __FUNCTION__, __LINE__);
        if(stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[i].u8Code == u8SecondaryRegion)
        {
            MW_SI_TARGET_REGION_DBG("same secondary region %d\n", u8SecondaryRegion);
            return i;
        }
    }
    for(int i = 0; i < m_stNit.u8TargetRegionNumber; i++)
    {

        if(memcmp(m_stNit.pastTargetRegionNameInfo[i].u8countryCode, stTargetRegionInfo.pCountryInfo[u16CountryIndex].au8CountryCode, 3))
        {
            continue;
        }

        for(int j = 0; j < m_stNit.pastTargetRegionNameInfo[i].u8primary_region_num; j++)
        {

            if(m_stNit.pastTargetRegionNameInfo[i].stPrimary_region_info[j].u8primary_region_code
                    != stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].u8Code)
            {
                MW_SI_TARGET_REGION_DBG("continue\n");
                continue;
            }

            for(int k = 0; k < m_stNit.pastTargetRegionNameInfo[i].stPrimary_region_info[j].u8secondary_region_num; k++)
            {
                if(m_stNit.pastTargetRegionNameInfo[i].stPrimary_region_info[j].stSecondary_region_info[k].u8secondary_region_code == u8SecondaryRegion)
                {
                    if(bAdd == MAPI_FALSE)
                    {
                        pOldInfo = stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo;
                        stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo = (MW_DVB_SECONDARY_REGION_INFO*)malloc(sizeof(MW_DVB_SECONDARY_REGION_INFO) * (u16Index + 1));
                        if(stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo == NULL)
                        {
                            stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo = pOldInfo;
                            ASSERT(0);
                            return -1;
                        }
                        if(pOldInfo)
                        {
                            memcpy(stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo, pOldInfo, sizeof(MW_DVB_SECONDARY_REGION_INFO)*(u16Index + 1));
                            FREE(pOldInfo);
                        }
                        stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[u16Index].u8Code = u8SecondaryRegion;
                        memcpy(stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[u16Index].name, m_stNit.pastTargetRegionNameInfo[i].stPrimary_region_info[j].stSecondary_region_info[k].Name, MAPI_SI_MAX_REGION_NAME);
                        stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[u16Index].u16TertiaryRegionNum = 0;
                        stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[u16Index].pTertiaryRegionInfo = NULL;
                        MW_SI_TARGET_REGION_DBG("add secondary region %x %s\n", stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[u16Index].u8Code,
                                                stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[u16Index].name);
                        stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].u16SecondaryRegionNum++;
                    }
                    else if(m_eLanguage == mapi_dvb_utility::SI_GetLangIndex(m_stNit.pastTargetRegionNameInfo[i].u8langCode))
                    {
                        memset(stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[u16Index].name, 0, MAPI_SI_MAX_REGION_NAME);
                        memcpy(stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[u16Index].name, m_stNit.pastTargetRegionNameInfo[i].stPrimary_region_info[j].stSecondary_region_info[k].Name, MAPI_SI_MAX_REGION_NAME);
                        MW_SI_TARGET_REGION_DBG("update secondary region %x %s\n", stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[u16Index].u8Code,
                                                stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[u16Index].name);
                        return u16Index;

                    }
                    if(m_eLanguage == mapi_dvb_utility::SI_GetLangIndex(m_stNit.pastTargetRegionNameInfo[i].u8langCode))
                    {
                        return u16Index;
                    }
                    bAdd = MAPI_TRUE;
                }
            }
        }
    }
    return -1;
}
MAPI_S16 MW_DVB_SI_PSI_Parser::_AddTargetTertiaryRegion(MW_DVB_TARGET_REGION_INFO & stTargetRegionInfo, MAPI_U16 u16CountryIndex, MAPI_U16 u16PrimaryIndex, MAPI_U16 u16SecondaryIndex, MAPI_U16 u16TertiaryRegion)
{

    MAPI_BOOL bAdd = MAPI_FALSE;
    MAPI_U16 u16Index = stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[u16SecondaryIndex].u16TertiaryRegionNum;
    MW_DVB_TERTIARY_REGION_INFO* pOldInfo;

    MW_SI_TARGET_REGION_DBG("%s %x %x %x %x\n", __FUNCTION__, u16CountryIndex, u16PrimaryIndex, u16SecondaryIndex, u16TertiaryRegion);
    for(int i = 0; i < u16Index; i++)
    {
        MW_SI_TARGET_REGION_DBG("%s..%d\n", __FUNCTION__, __LINE__);
        if(stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[u16SecondaryIndex].pTertiaryRegionInfo[i].u16Code == u16TertiaryRegion)
        {
            MW_SI_TARGET_REGION_DBG("same tertiary region %d\n", u16TertiaryRegion);
            return i;
        }
    }
    for(int i = 0; i < m_stNit.u8TargetRegionNumber; i++)
    {

        if(memcmp(m_stNit.pastTargetRegionNameInfo[i].u8countryCode, stTargetRegionInfo.pCountryInfo[u16CountryIndex].au8CountryCode, 3))
        {
            MW_SI_TARGET_REGION_DBG("continue\n");
            continue;
        }
        MW_SI_TARGET_REGION_DBG("%s...%d\n", __FUNCTION__, __LINE__);
        for(int j = 0; j < m_stNit.pastTargetRegionNameInfo[i].u8primary_region_num; j++)
        {

            if(m_stNit.pastTargetRegionNameInfo[i].stPrimary_region_info[j].u8primary_region_code
                    != stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].u8Code)
            {
                MW_SI_TARGET_REGION_DBG("continue\n");
                continue;
            }

            MW_SI_TARGET_REGION_DBG("%s...%d\n", __FUNCTION__, __LINE__);
            for(int k = 0; k < m_stNit.pastTargetRegionNameInfo[i].stPrimary_region_info[j].u8secondary_region_num; k++)
            {


                if(m_stNit.pastTargetRegionNameInfo[i].stPrimary_region_info[j].stSecondary_region_info[k].u8secondary_region_code
                        != stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[u16SecondaryIndex].u8Code)
                {
                    MW_SI_TARGET_REGION_DBG("continue\n");
                    continue;
                }
                MW_SI_TARGET_REGION_DBG("%s...%d...%d\n", __FUNCTION__, __LINE__, m_stNit.pastTargetRegionNameInfo[i].stPrimary_region_info[j].stSecondary_region_info[k].u8tertiary_region_num);
                for(int l = 0; l < m_stNit.pastTargetRegionNameInfo[i].stPrimary_region_info[j].stSecondary_region_info[k].u8tertiary_region_num; l++)
                {
                    MW_SI_TARGET_REGION_DBG("u16tertiary_region_code %x \n", m_stNit.pastTargetRegionNameInfo[i].stPrimary_region_info[j].stSecondary_region_info[k].sttertiary_region_info[l].u16tertiary_region_code);
                    if(m_stNit.pastTargetRegionNameInfo[i].stPrimary_region_info[j].stSecondary_region_info[k].sttertiary_region_info[l].u16tertiary_region_code == u16TertiaryRegion)
                    {
                        if(bAdd == MAPI_FALSE)
                        {
                            pOldInfo = stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[u16SecondaryIndex].pTertiaryRegionInfo;
                            stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[u16SecondaryIndex].pTertiaryRegionInfo = (MW_DVB_TERTIARY_REGION_INFO*)malloc(sizeof(MW_DVB_TERTIARY_REGION_INFO) * (u16Index + 1));
                            //ASSERT(stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[u16SecondaryIndex].pTertiaryRegionInfo);
                            if(stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[u16SecondaryIndex].pTertiaryRegionInfo == NULL)
                            {
                                stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[u16SecondaryIndex].pTertiaryRegionInfo = pOldInfo;
                                return -1;
                            }
                            if(NULL != pOldInfo)
                            {
                                memcpy(stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[u16SecondaryIndex].pTertiaryRegionInfo, pOldInfo, sizeof(MW_DVB_TERTIARY_REGION_INFO)*(u16Index));
                                FREE(pOldInfo);
                            }
                            stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[u16SecondaryIndex].pTertiaryRegionInfo[u16Index].u16Code = u16TertiaryRegion;
                            memcpy(stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[u16SecondaryIndex].pTertiaryRegionInfo[u16Index].name, m_stNit.pastTargetRegionNameInfo[i].stPrimary_region_info[j].stSecondary_region_info[k].sttertiary_region_info[l].Name, MAPI_SI_MAX_REGION_NAME);
                            MW_SI_TARGET_REGION_DBG("add tertiary region %x %s\n", stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[u16SecondaryIndex].pTertiaryRegionInfo[u16Index].u16Code,
                                                    stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[u16SecondaryIndex].pTertiaryRegionInfo[u16Index].name);
                            stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[u16SecondaryIndex].u16TertiaryRegionNum++;
                        }
                        else if(m_eLanguage == mapi_dvb_utility::SI_GetLangIndex(m_stNit.pastTargetRegionNameInfo[i].u8langCode))
                        {
                            memset(stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[u16SecondaryIndex].pTertiaryRegionInfo[u16Index].name, 0, MAPI_SI_MAX_REGION_NAME);
                            memcpy(stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[u16SecondaryIndex].pTertiaryRegionInfo[u16Index].name, m_stNit.pastTargetRegionNameInfo[i].stPrimary_region_info[j].stSecondary_region_info[k].sttertiary_region_info[l].Name, MAPI_SI_MAX_REGION_NAME);
                            MW_SI_TARGET_REGION_DBG("update tertiary region %x %s\n", stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[u16SecondaryIndex].pTertiaryRegionInfo[u16Index].u16Code,
                                                    stTargetRegionInfo.pCountryInfo[u16CountryIndex].pPrimaryRegionInfo[u16PrimaryIndex].pSecondaryRegionInfo[u16SecondaryIndex].pTertiaryRegionInfo[u16Index].name);
                            return u16Index;

                        }
                        if(m_eLanguage == mapi_dvb_utility::SI_GetLangIndex(m_stNit.pastTargetRegionNameInfo[i].u8langCode))
                        {
                            return u16Index;
                        }
                        bAdd = MAPI_TRUE;
                    }
                }
            }
        }
    }
    return -1;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::GetTargetRegionInfo(MW_DVB_TARGET_REGION_INFO & info)
{
    info.pCountryInfo = NULL;
    info.u8CountryNum = 0;
    if(m_stTargetRegionInfo.pCountryInfo)
    {
        info = m_stTargetRegionInfo;
        MW_SI_TARGET_REGION_DBG("m_stTargetRegionInfo.u8CountryNum %d\n", m_stTargetRegionInfo.u8CountryNum);
        for(int i = 0; i < m_stTargetRegionInfo.u8CountryNum; i++)
        {
            MW_SI_TARGET_REGION_DBG("country %.*s\n", 3, m_stTargetRegionInfo.pCountryInfo[i].au8CountryCode);
            MW_SI_TARGET_REGION_DBG("m_stTargetRegionInfo.pCountryInfo[i].u16PrimaryRegionNum %d\n", m_stTargetRegionInfo.pCountryInfo[i].u16PrimaryRegionNum);

            for(int j = 0; j < m_stTargetRegionInfo.pCountryInfo[i].u16PrimaryRegionNum; j++)
            {
                MW_SI_TARGET_REGION_DBG("__primary %x %s\n", m_stTargetRegionInfo.pCountryInfo[i].pPrimaryRegionInfo[j].u8Code
                                        , m_stTargetRegionInfo.pCountryInfo[i].pPrimaryRegionInfo[j].name);
                MW_SI_TARGET_REGION_DBG("m_stTargetRegionInfo.pCountryInfo[i].pPrimaryRegionInfo[j].u16SecondaryRegionNum %d\n", m_stTargetRegionInfo.pCountryInfo[i].pPrimaryRegionInfo[j].u16SecondaryRegionNum);
                for(int k = 0; k < m_stTargetRegionInfo.pCountryInfo[i].pPrimaryRegionInfo[j].u16SecondaryRegionNum; k++)
                {
                    MW_SI_TARGET_REGION_DBG("____secondary %x %s\n", m_stTargetRegionInfo.pCountryInfo[i].pPrimaryRegionInfo[j].pSecondaryRegionInfo[k].u8Code
                                            , m_stTargetRegionInfo.pCountryInfo[i].pPrimaryRegionInfo[j].pSecondaryRegionInfo[k].name);
                    MW_SI_TARGET_REGION_DBG("m_stTargetRegionInfo.pCountryInfo[i].pPrimaryRegionInfo[j].pSecondaryRegionInfo[k].u16TertiaryRegionNum %d\n", m_stTargetRegionInfo.pCountryInfo[i].pPrimaryRegionInfo[j].pSecondaryRegionInfo[k].u16TertiaryRegionNum);
                    for(int l = 0; l < m_stTargetRegionInfo.pCountryInfo[i].pPrimaryRegionInfo[j].pSecondaryRegionInfo[k].u16TertiaryRegionNum; l++)
                    {
                        MW_SI_TARGET_REGION_DBG("______teritary %x %s\n", m_stTargetRegionInfo.pCountryInfo[i].pPrimaryRegionInfo[j].pSecondaryRegionInfo[k].pTertiaryRegionInfo[l].u16Code
                                                , m_stTargetRegionInfo.pCountryInfo[i].pPrimaryRegionInfo[j].pSecondaryRegionInfo[k].pTertiaryRegionInfo[l].name);
                    }
                }
            }
        }
        return MAPI_TRUE;
    }
    return MAPI_FALSE;
}


void MW_DVB_SI_PSI_Parser::_ResetTargetRegion(void)
{
    if(m_stTargetRegionInfo.pCountryInfo)
    {
        for(int i = 0; i < m_stTargetRegionInfo.u8CountryNum; i++)
        {
            for(int j = 0; j < m_stTargetRegionInfo.pCountryInfo[i].u16PrimaryRegionNum; j++)
            {
                for(int k = 0; k < m_stTargetRegionInfo.pCountryInfo[i].pPrimaryRegionInfo[j].u16SecondaryRegionNum; k++)
                {
                    if(m_stTargetRegionInfo.pCountryInfo[i].pPrimaryRegionInfo[j].pSecondaryRegionInfo[k].pTertiaryRegionInfo)
                    {
                        MW_SI_TARGET_REGION_DBG("free pTertiaryRegionInfo .....%x\n", (U32)m_stTargetRegionInfo.pCountryInfo[i].pPrimaryRegionInfo[j].pSecondaryRegionInfo[k].pTertiaryRegionInfo);
                        FREE(m_stTargetRegionInfo.pCountryInfo[i].pPrimaryRegionInfo[j].pSecondaryRegionInfo[k].pTertiaryRegionInfo);
                    }

                }
                if(m_stTargetRegionInfo.pCountryInfo[i].pPrimaryRegionInfo[j].pSecondaryRegionInfo)
                {
                    MW_SI_TARGET_REGION_DBG("free pSecondaryRegionInfo .....%x\n", (U32)m_stTargetRegionInfo.pCountryInfo[i].pPrimaryRegionInfo[j].pSecondaryRegionInfo);
                    FREE(m_stTargetRegionInfo.pCountryInfo[i].pPrimaryRegionInfo[j].pSecondaryRegionInfo);
                }
            }
            if(m_stTargetRegionInfo.pCountryInfo[i].pPrimaryRegionInfo)
            {
                MW_SI_TARGET_REGION_DBG("free pPrimaryRegionInfo .....%x\n", (U32)m_stTargetRegionInfo.pCountryInfo[i].pPrimaryRegionInfo);
                FREE(m_stTargetRegionInfo.pCountryInfo[i].pPrimaryRegionInfo);
            }
        }
        MW_SI_TARGET_REGION_DBG("free pCountryInfo.....%x\n", (U32)m_stTargetRegionInfo.pCountryInfo);
        FREE(m_stTargetRegionInfo.pCountryInfo);
    }
    memset(&m_stTargetRegionInfo, 0, sizeof(m_stTargetRegionInfo));

    if(m_stAllServiceTargetRegionInfo.pInfo)
    {
        for(int i = 0; i < m_stAllServiceTargetRegionInfo.u16ServiceNum; i++)
        {
            for(int j = 0; j < m_stAllServiceTargetRegionInfo.pInfo[i].stInfo.u8CountryNum; j++)
            {
                for(int k = 0; k < m_stAllServiceTargetRegionInfo.pInfo[i].stInfo.pCountryInfo[j].u16PrimaryRegionNum; k++)
                {
                    for(int l = 0; l < m_stAllServiceTargetRegionInfo.pInfo[i].stInfo.pCountryInfo[j].pPrimaryRegionInfo[k].u16SecondaryRegionNum; l++)
                    {
                        FREE(m_stAllServiceTargetRegionInfo.pInfo[i].stInfo.pCountryInfo[j].pPrimaryRegionInfo[k].pSecondaryRegionInfo[l].pTertiaryRegionInfo);
                    }
                    FREE(m_stAllServiceTargetRegionInfo.pInfo[i].stInfo.pCountryInfo[j].pPrimaryRegionInfo[k].pSecondaryRegionInfo);
                    }
                FREE(m_stAllServiceTargetRegionInfo.pInfo[i].stInfo.pCountryInfo[j].pPrimaryRegionInfo);
            }
            FREE(m_stAllServiceTargetRegionInfo.pInfo[i].stInfo.pCountryInfo);
                }
        FREE(m_stAllServiceTargetRegionInfo.pInfo);
    }
    memset(&m_stAllServiceTargetRegionInfo, 0, sizeof(MW_DVB_ALL_SERVICE_REGION_INFO));
}

#if (OAD_ENABLE == 1)
void MW_DVB_SI_PSI_Parser::HookOadParser(MW_OAD_Parser *p_parser)
{
   m_OADParser = p_parser;
}
#endif //(OAD_ENABLE == 1)

#if (CA_ENABLE == 1)
void MW_DVB_SI_PSI_Parser::SetCaClient(MSrv_CA *p_ca)
{
    MW_DVB_SI_PSI_Parser::m_pCaClient = p_ca;
}
#endif

void MW_DVB_SI_PSI_Parser::SetTargetRegion(MAPI_U8* pu8Country, MAPI_U8 u8PrimaryRegion, MAPI_U8 u8SecondaryRegion, MAPI_U16 u16Tertiary)
{
    MW_SI_TARGET_REGION_DBG("\ncountry %.*s,%x,%x,%x\n", 3, pu8Country, u8PrimaryRegion, u8SecondaryRegion, u16Tertiary);
    //NIT case
    for(int i = 0; i < m_stAllServiceTargetRegionInfo.u16ServiceNum; i++)
    {
        if(m_stAllServiceTargetRegionInfo.pInfo[i].u16SID)continue;
        for(int j = 0; j < m_stAllServiceTargetRegionInfo.pInfo[i].stInfo.u8CountryNum; j++)
        {
            if(memcmp(m_stAllServiceTargetRegionInfo.pInfo[i].stInfo.pCountryInfo[j].au8CountryCode, pu8Country, 3))
            {
                continue;
            }
            _SetTargetRegionProgram(m_pCurProg, m_pCMDB, m_pCurMux, m_pCurNetwork
                                   , m_stAllServiceTargetRegionInfo.pInfo[i].u16ONID, m_stAllServiceTargetRegionInfo.pInfo[i].u16TSID, m_stAllServiceTargetRegionInfo.pInfo[i].u16SID, 0x01);
            for(int k = 0; k < m_stAllServiceTargetRegionInfo.pInfo[i].stInfo.pCountryInfo[j].u16PrimaryRegionNum; k++)
            {
                if(m_stAllServiceTargetRegionInfo.pInfo[i].stInfo.pCountryInfo[j].pPrimaryRegionInfo[k].u8Code != u8PrimaryRegion)
                {
                    continue;
                }
                _SetTargetRegionProgram(m_pCurProg, m_pCMDB, m_pCurMux, m_pCurNetwork
                                       , m_stAllServiceTargetRegionInfo.pInfo[i].u16ONID, m_stAllServiceTargetRegionInfo.pInfo[i].u16TSID, m_stAllServiceTargetRegionInfo.pInfo[i].u16SID, 0x02);
                for(int l = 0; l < m_stAllServiceTargetRegionInfo.pInfo[i].stInfo.pCountryInfo[j].pPrimaryRegionInfo[k].u16SecondaryRegionNum; l++)
                {

                    if(m_stAllServiceTargetRegionInfo.pInfo[i].stInfo.pCountryInfo[j].pPrimaryRegionInfo[k].pSecondaryRegionInfo[l].u8Code != u8SecondaryRegion)
                    {
                        continue;
                    }
                    _SetTargetRegionProgram(m_pCurProg, m_pCMDB, m_pCurMux, m_pCurNetwork
                                           , m_stAllServiceTargetRegionInfo.pInfo[i].u16ONID, m_stAllServiceTargetRegionInfo.pInfo[i].u16TSID, m_stAllServiceTargetRegionInfo.pInfo[i].u16SID, 0x04);


                    for(int m = 0; m < m_stAllServiceTargetRegionInfo.pInfo[i].stInfo.pCountryInfo[j].pPrimaryRegionInfo[k].pSecondaryRegionInfo[l].u16TertiaryRegionNum; m++)
                    {

                        if(m_stAllServiceTargetRegionInfo.pInfo[i].stInfo.pCountryInfo[j].pPrimaryRegionInfo[k].pSecondaryRegionInfo[l].pTertiaryRegionInfo[m].u16Code != u16Tertiary)
                        {
                            continue;
                        }
                        _SetTargetRegionProgram(m_pCurProg, m_pCMDB, m_pCurMux, m_pCurNetwork
                                               , m_stAllServiceTargetRegionInfo.pInfo[i].u16ONID, m_stAllServiceTargetRegionInfo.pInfo[i].u16TSID, m_stAllServiceTargetRegionInfo.pInfo[i].u16SID, 0x08);

                    }
                }
            }
        }

    }




    //SDT case
    for(int i = 0; i < m_stAllServiceTargetRegionInfo.u16ServiceNum; i++)
    {
        if(m_stAllServiceTargetRegionInfo.pInfo[i].u16SID == 0)continue;
        for(int j = 0; j < m_stAllServiceTargetRegionInfo.pInfo[i].stInfo.u8CountryNum; j++)
        {
            if(memcmp(m_stAllServiceTargetRegionInfo.pInfo[i].stInfo.pCountryInfo[j].au8CountryCode, pu8Country, 3))
            {
                continue;
            }
            //clear region data that got from NIT
            _SetTargetRegionProgram(m_pCurProg, m_pCMDB, m_pCurMux, m_pCurNetwork
                                   , m_stAllServiceTargetRegionInfo.pInfo[i].u16ONID, m_stAllServiceTargetRegionInfo.pInfo[i].u16TSID, m_stAllServiceTargetRegionInfo.pInfo[i].u16SID, 0x00);

            _SetTargetRegionProgram(m_pCurProg, m_pCMDB, m_pCurMux, m_pCurNetwork
                                   , m_stAllServiceTargetRegionInfo.pInfo[i].u16ONID, m_stAllServiceTargetRegionInfo.pInfo[i].u16TSID, m_stAllServiceTargetRegionInfo.pInfo[i].u16SID, 0x01);
            for(int k = 0; k < m_stAllServiceTargetRegionInfo.pInfo[i].stInfo.pCountryInfo[j].u16PrimaryRegionNum; k++)
            {
                if(m_stAllServiceTargetRegionInfo.pInfo[i].stInfo.pCountryInfo[j].pPrimaryRegionInfo[k].u8Code != u8PrimaryRegion)
                {
                    continue;
                }
                _SetTargetRegionProgram(m_pCurProg, m_pCMDB, m_pCurMux, m_pCurNetwork
                                       , m_stAllServiceTargetRegionInfo.pInfo[i].u16ONID, m_stAllServiceTargetRegionInfo.pInfo[i].u16TSID, m_stAllServiceTargetRegionInfo.pInfo[i].u16SID, 0x02);
                for(int l = 0; l < m_stAllServiceTargetRegionInfo.pInfo[i].stInfo.pCountryInfo[j].pPrimaryRegionInfo[k].u16SecondaryRegionNum; l++)
                {

                    if(m_stAllServiceTargetRegionInfo.pInfo[i].stInfo.pCountryInfo[j].pPrimaryRegionInfo[k].pSecondaryRegionInfo[l].u8Code != u8SecondaryRegion)
                    {
                        continue;
                    }
                    _SetTargetRegionProgram(m_pCurProg, m_pCMDB, m_pCurMux, m_pCurNetwork
                                           , m_stAllServiceTargetRegionInfo.pInfo[i].u16ONID, m_stAllServiceTargetRegionInfo.pInfo[i].u16TSID, m_stAllServiceTargetRegionInfo.pInfo[i].u16SID, 0x04);


                    for(int m = 0; m < m_stAllServiceTargetRegionInfo.pInfo[i].stInfo.pCountryInfo[j].pPrimaryRegionInfo[k].pSecondaryRegionInfo[l].u16TertiaryRegionNum; m++)
                    {

                        if(m_stAllServiceTargetRegionInfo.pInfo[i].stInfo.pCountryInfo[j].pPrimaryRegionInfo[k].pSecondaryRegionInfo[l].pTertiaryRegionInfo[m].u16Code != u16Tertiary)
                        {
                            continue;
                        }
                        _SetTargetRegionProgram(m_pCurProg, m_pCMDB, m_pCurMux, m_pCurNetwork
                                               , m_stAllServiceTargetRegionInfo.pInfo[i].u16ONID, m_stAllServiceTargetRegionInfo.pInfo[i].u16TSID, m_stAllServiceTargetRegionInfo.pInfo[i].u16SID, 0x08);

                    }
                }
            }
        }

    }

    //printf("SetTargetRegion====END?>>>>>>\n");

}

void MW_DVB_SI_PSI_Parser::_SetTargetRegionProgram(DVB_PROG *pCurProg, DVB_CM *pCMDB, DVB_MUX *pMux , DVB_NETWORK *pNetwork, MAPI_U16 u16ONID, MAPI_U16 u16TSID, MAPI_U16 u16SID, MAPI_U8 u8Value)
{
    DVB_PROG* pNextProg;

    pNextProg = pCMDB->GetByIndex(0);
    while(pNextProg)
    {
#if 1 //Need Vincent's review
        pMux = (DVB_MUX*)__GetMux(pNextProg, pCMDB);
        ASSERT(pMux);
        pNetwork = (DVB_NETWORK*)__GetNetwork(pMux, pCMDB);
        ASSERT(pNetwork);
#endif
        if((__GetID(pNextProg, pMux, pNetwork, EN_ID_TSID) == u16TSID) && (__GetID(pNextProg, pMux, pNetwork, EN_ID_ONID) == u16ONID)
                && ((!u16SID) || (__GetID(pNextProg, pMux, pNetwork, EN_ID_SID) == u16SID))
                && (((pNextProg->stCHAttribute.u8Region & u8Value) != u8Value) || (!u8Value)))
        {
            MW_SI_TARGET_REGION_DBG("%s....onid %x tsid %x sid %x\n", __FUNCTION__, u16ONID, u16TSID, u16SID);
            if(u8Value)
            {
                pNextProg->stCHAttribute.u8Region |= u8Value;
            }
            else
            {
                pNextProg->stCHAttribute.u8Region = 0;
            }
            pCMDB->Update(pNextProg);
        }
        pNextProg = pCMDB->GetNext(pNextProg);
    }
}
static void _FreeRegionInfo(MAPI_SI_TABLE_SDT* pSdt, MAPI_SI_TABLE_NIT* pNit)
{
    if(pNit != NULL)
    {
        if((pNit->u8Version != INVALID_PSI_SI_VERSION) && (pNit->pstTSInfo))
        {
            FREE(pNit->pastTargetRegionNameInfo);
            for(int i = 0; i < pNit->u16TSNumber; i++)
            {
                FREE(pNit->pstTSInfo[i].pstTargetRegionInfo);
            }
        }
    }
    if(pSdt != NULL)
    {
        if(pSdt->u8Version != INVALID_PSI_SI_VERSION)
        {
            for(int i = 0; i < pSdt->wServiceNumber; i++)
            {
                FREE(pSdt->astServiceInfo[i].pstTargetRegionInfo);
            }
        }
    }


}
static void _FreeCIProtectInfo(MAPI_SI_TABLE_SDT* pSdt)
{
    if(pSdt != NULL)
    {
        for(int i = 0; i < pSdt->wServiceNumber; i++)
        {
            FREE(pSdt->astServiceInfo[i].pu8CI_protect_info);
        }
    }
}
static void _FreeLogoInfo(MAPI_SI_TABLE_SDT* pSdt)
{
    if(pSdt != NULL)
    {
        for(int i = 0; i < pSdt->wServiceNumber; i++)
        {
            FREE(pSdt->astServiceInfo[i].stLogoTransmission.pu8LogoData);
            pSdt->astServiceInfo[i].stLogoTransmission.u16LogoDataLength = 0;
        }
    }
}

void MW_DVB_SI_PSI_Parser::_FreeSDTInfo(MAPI_SI_TABLE_SDT* pSdt)
{
    _FreeCIProtectInfo(pSdt);
    _FreeRegionInfo(pSdt);
    _FreeLogoInfo(pSdt);
}

static void _FreeLCNv2Info(MAPI_SI_TABLE_NIT* pNit)
{
    if(pNit != NULL)
    {
        if((pNit->u8Version != INVALID_PSI_SI_VERSION) && (pNit->pstTSInfo))
        {
            for(int i = 0; i < pNit->u16TSNumber; i++)
            {
                for(int j = 0; j < MAPI_SI_MAX_LCD2_SUPPORT; j++)
                {
                    FREE(pNit->pstTSInfo[i].astLcnV2Info[j].pLCNInfo);
                    pNit->pstTSInfo[i].astLcnV2Info[j].u8ServicesNumber = 0;
                    pNit->pstTSInfo[i].astLcnV2Info[j].u8ChannelListID = 0;
                }
                pNit->pstTSInfo[i].u8ChannelListNumber = 0;
            }
        }
    }
}

void MW_DVB_SI_PSI_Parser::GetCurRpSrvInfo(MW_DVB_RP_SERVICE_INFO *info)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    memcpy(info, &m_CurRPServiceInfo, sizeof(MW_DVB_RP_SERVICE_INFO));
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::_CheckSrvInAvailCell(MAPI_SI_TABLE_SDT *pstSdt, MAPI_U16 u16CellID, MAPI_U16 u16ServiceID)
{
    MAPI_U16 i =0, j = 0;
    if((pstSdt->u8Version != INVALID_PSI_SI_VERSION) && (INVALID_CELLID != u16CellID) && (E_NEWZEALAND == m_eCountry) &&
        ( MW_SI_ONID_NEWZEALAND == pstSdt->wOriginalNetwork_ID))
    {
        for(j = 0; j < pstSdt->wServiceNumber; j++)
        {
            if(u16ServiceID == pstSdt->astServiceInfo[j].u16ServiceID)
            {
                for(i = 0; i < pstSdt->astServiceInfo[j].u8AvailableCellNumber; i++)
                {
                    if(u16CellID == pstSdt->astServiceInfo[j].a16AvailableCell[i])
                    {
                        MW_SI_NIT_AUTOUPDATE_DBG("CellID Match  %d (%d)\n",u16CellID,pstSdt->astServiceInfo[j].a16AvailableCell[i]);
                        return MAPI_TRUE;
                    }
                }

                for(i = 0; i < pstSdt->astServiceInfo[j].u8UnAvailableCellNumber; i++)
                {
                    if(u16CellID == pstSdt->astServiceInfo[j].a16UnAvailableCell[i])
                    {
                        MW_SI_NIT_AUTOUPDATE_DBG("CellID Not Match  %d (%d)\n",u16CellID,pstSdt->astServiceInfo[j].a16UnAvailableCell[i]);
                        return MAPI_FALSE;
                    }
                }

                if(pstSdt->astServiceInfo[j].u8UnAvailableCellNumber)
                {
                    MW_SI_NIT_AUTOUPDATE_DBG("CellID found In NIT other mux\n");
                    return MAPI_TRUE;
                }

                if(pstSdt->astServiceInfo[j].u8AvailableCellNumber)
                {
                    MW_SI_NIT_AUTOUPDATE_DBG("CellID does not found In NIT other mux\n");
                    return MAPI_FALSE;
                }
                break;
            }
        }

    }

    return MAPI_TRUE;
}
void MW_DVB_SI_PSI_Parser::_BuildNitInfo(void)
{
    MW_SI_NIT_AUTOUPDATE_DBG("###  %s  ###\n", __FUNCTION__);

    MAPI_U8 u8Index = MAPI_SI_MAX_NETWORK;
    BOOL bUpdate = MAPI_FALSE;
    if(m_stNit.pstTSInfo == NULL)
    {
        return;
    }

    if((m_eParserType == MW_DVB_T_PARSER) && (m_stNit.u8Version != INVALID_PSI_SI_VERSION))
    {
        // for Norway RiksTV & NewZealand Freeview & Finland Antenna Ready HD
        if (!(((m_eCountry == E_NORWAY) && (m_stNit.pstTSInfo[m_stNit.u16TSNumber-1].u16ONID == NORWAY_ONID))
            || ((m_eCountry == E_NEWZEALAND) && (m_stNit.pstTSInfo[m_stNit.u16TSNumber-1].u16ONID == NEWZEALAND_ONID))
            || (m_eCountry == E_FINLAND)
#if (NCD_ENABLE == 1)
            || (m_eCountry == E_UK)
#endif
            ))
        {
            return;
        }

        //search matched NID or first invalid index
        for(int i = 0; i < MAPI_SI_MAX_NETWORK; i++)
        {
            if(m_stNit_UpdateInfo.astNit_Info[i].u16NetworkID == m_stNit.u16NetworkID)
            {
                u8Index = i;
                break;
            }
            else if ((m_stNit_UpdateInfo.astNit_Info[i].u16NetworkID == INVALID_NID)
                && (u8Index == MAPI_SI_MAX_NETWORK))
            {
                u8Index = i;
            }
        }
        if (u8Index < MAPI_SI_MAX_NETWORK)
        {
            m_stNit_UpdateInfo.astNit_Info[u8Index].u16ONID = m_stNit.pstTSInfo[m_stNit.u16TSNumber-1].u16ONID;
            m_stNit_UpdateInfo.astNit_Info[u8Index].u16NetworkID = m_stNit.u16NetworkID;
            MAPI_U8 u8TsIndex;
            int i, j;

            for (i = 0; i<m_stNit.u16TSNumber; i++)
            {
                u8TsIndex = 0xFF;
                for (j = 0; j< MAPI_SI_NTV_MAX_TS_IN_NETWORK; j++)
                {
                    if (m_stNit_UpdateInfo.astNit_Info[u8Index].astTSInfo[j].wTransportStream_ID == m_stNit.pstTSInfo[i].wTransportStream_ID)
                    {
                        memcpy(m_stNit_UpdateInfo.astNit_Info[u8Index].astTSInfo[j].au32FrequencyList, m_stNit.pstTSInfo[i].au32FrequencyList,
                                        sizeof(MAPI_U32)*MAPI_SI_NTV_MAX_FREQUENCY);
                        _AddNewFrequency(&m_stNit.pstTSInfo[i], m_stNit_UpdateInfo.astNit_Info[u8Index].astTSInfo[j].au32FrequencyList);
                        bUpdate = MAPI_TRUE;
                        break;
                    }
                    else if ((u8TsIndex == 0xFF)
                        && (m_stNit_UpdateInfo.astNit_Info[u8Index].astTSInfo[j].wTransportStream_ID == INVALID_TS_ID))
                    {
                        u8TsIndex = j;
                    }
                }
                if ((j>=MAPI_SI_NTV_MAX_TS_IN_NETWORK) && (u8TsIndex != 0xFF))
                {
                    m_stNit_UpdateInfo.astNit_Info[u8Index].astTSInfo[u8TsIndex].wTransportStream_ID = m_stNit.pstTSInfo[i].wTransportStream_ID;
                    memcpy(m_stNit_UpdateInfo.astNit_Info[u8Index].astTSInfo[u8TsIndex].au32FrequencyList,
                                    m_stNit.pstTSInfo[i].au32FrequencyList, sizeof(MAPI_U32)*MAPI_SI_NTV_MAX_FREQUENCY);
                    m_stNit_UpdateInfo.astNit_Info[u8Index].u8TSNumber++;
                    _AddNewFrequency(&m_stNit.pstTSInfo[i], m_stNit_UpdateInfo.astNit_Info[u8Index].astTSInfo[u8TsIndex].au32FrequencyList);
                    bUpdate = MAPI_TRUE;

                    if (m_stNit_UpdateInfo.astNit_Info[u8Index].u8TSNumber >= MAPI_SI_NTV_MAX_TS_IN_NETWORK)
                    {
                        break;
                    }
                }
            }

            if (bUpdate == MAPI_TRUE)
            {
                if (m_ParserCallBackInfo.EventHandler)
                {
                    m_ParserCallBackInfo.EventHandler(E_DVB_SI_SAVE_NIT_INFO,
                                                                            (MAPI_U32)m_ParserCallBackInfo.pCallbackReference,(MAPI_U32)&m_stNit_UpdateInfo);
                }
            }
        }
        else
        {
            MW_SI_NIT_AUTOUPDATE_DBG("Build NIT info FAIL!\n");
        }
    }
}

void MW_DVB_SI_PSI_Parser::ResetNitInfoInit(void)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    MW_SI_NIT_AUTOUPDATE_DBG("###  %s  ###\n", __FUNCTION__);

    if ((m_eParserType == MW_DVB_T_PARSER) &&
        ((m_eCountry == E_NORWAY) ||(m_eCountry == E_FINLAND) || (m_eCountry == E_NEWZEALAND)
#if (NCD_ENABLE == 1)
        || (m_eCountry == E_UK)
#endif
        ))
    {
        memset(&m_stNit_UpdateInfo, 0x00, sizeof(MW_SI_NIT_AUTOUPDATE_INFO));

        for(int i = 0; i < MAPI_SI_MAX_NETWORK; i++)
        {
            m_stNit_UpdateInfo.astNit_Info[i].u16NetworkID = INVALID_NID;
            m_stNit_UpdateInfo.astNit_Info[i].u16ONID = INVALID_ON_ID;
            for(int j = 0; j < MAPI_SI_NTV_MAX_TS_IN_NETWORK; j++)
            {
                m_stNit_UpdateInfo.astNit_Info[i].astTSInfo[j].wTransportStream_ID = INVALID_TS_ID;
            }
        }
        if (m_ParserCallBackInfo.EventHandler)
        {
            m_ParserCallBackInfo.EventHandler(E_DVB_SI_SAVE_NIT_INFO,
                                                                    (MAPI_U32)m_ParserCallBackInfo.pCallbackReference,(MAPI_U32)&m_stNit_UpdateInfo);
        }
    }
}

void MW_DVB_SI_PSI_Parser::_AddScanRFInfo(MAPI_U16 u16TransportStream_ID, MAPI_U32 *pu32Frequency)
{
    MW_SI_NIT_AUTOUPDATE_DBG("###  %s    :>>> TSID 0x%x\n", __FUNCTION__, u16TransportStream_ID);

    if((m_eParserType == MW_DVB_T_PARSER) && (m_stNit.u8Version != INVALID_PSI_SI_VERSION))
    {
        for(int i = 0; i < MAPI_SI_NTV_MAX_TS_IN_NETWORK; i++) //max ts index MAPI_SI_NTV_MAX_TS_IN_NETWORK in a network for dvb-t
        {
            if ((m_stScanRemoveRFInfo.stScanRFList[i].u16TransportStream_ID == u16TransportStream_ID)
                || ((m_stScanRemoveRFInfo.stScanRFList[i].u16TransportStream_ID == INVALID_TS_ID)
                    && (m_stScanRemoveRFInfo.stScanRFList[i].au32Frequency[0] == 0)))
            {
                m_stScanRemoveRFInfo.stScanRFList[i].u16TransportStream_ID = u16TransportStream_ID;
                MW_SI_NIT_AUTOUPDATE_DBG("Add ScanRF List :>TSID[%d] 0x%x\n", i, m_stScanRemoveRFInfo.stScanRFList[i].u16TransportStream_ID);
                if (pu32Frequency != NULL)
                {
                    // add frequency list
                    for(int k = 0; k < MAPI_SI_NTV_MAX_FREQUENCY; k++) //max frequency list index MAPI_SI_NTV_MAX_FREQUENCY in a ts for dvb-t
                    {
                        if(pu32Frequency[k] == 0)
                        {
                            break;
                        }
                        for(int j = 0; j < MAPI_SI_NTV_MAX_FREQUENCY; j++)
                        {
                            if(m_stScanRemoveRFInfo.stScanRFList[i].au32Frequency[j] == 0)
                            {
                                m_stScanRemoveRFInfo.stScanRFList[i].au32Frequency[j] = pu32Frequency[k];
                                MW_SI_NIT_AUTOUPDATE_DBG("Add ScanRF List :>Frequency[%d] %u\n", j, m_stScanRemoveRFInfo.stScanRFList[i].au32Frequency[j]);
                                break;
                            }
                        }
                    }
                }

                // add center frequency of terrestrial delivery system & T2 delivery system
                for (int j=0; j<m_stNit.u16TSNumber; j++)
                {
                    if (m_stNit.pstTSInfo[j].wTransportStream_ID == u16TransportStream_ID)
                    {
                        _AddNewFrequency(&m_stNit.pstTSInfo[j], m_stScanRemoveRFInfo.stScanRFList[i].au32Frequency);
                        break;
                    }
                }

                break;
            }
        }
    }
}
U32 MW_DVB_SI_PSI_Parser::GetFirstScanFrequency(void)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    MW_SI_NIT_AUTOUPDATE_DBG("###  %s  ###\n", __FUNCTION__);
    for(int i = 0; i < MAPI_SI_NTV_MAX_TS_IN_NETWORK; i++)
    {
        for(int j = 0; j < MAPI_SI_NTV_MAX_FREQUENCY; j++)
        {
            if (m_stScanRemoveRFInfo.stScanRFList[i].au32Frequency[j] > 0)
            {
                MW_SI_NIT_AUTOUPDATE_DBG("ScanRF List:> first frequency   %u\n", m_stScanRemoveRFInfo.stScanRFList[i].au32Frequency[j] / 100);
                m_stScanRemoveRFInfo.stScanRFList[i].bFreqUsed[j] = MAPI_TRUE;
                return (m_stScanRemoveRFInfo.stScanRFList[i].au32Frequency[j] / 100);
            }
        }
    }
    return 0;
}
U32 MW_DVB_SI_PSI_Parser::GetNextScanFrequency(void)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    MW_SI_NIT_AUTOUPDATE_DBG("###  %s \n", __FUNCTION__);

    U32 u32Freq = 0;
    for(int i = 0; i < MAPI_SI_NTV_MAX_TS_IN_NETWORK; i++)
    {
        for(int j = 0; j < MAPI_SI_NTV_MAX_FREQUENCY; j++)
        {
            if ((m_stScanRemoveRFInfo.stScanRFList[i].au32Frequency[j] > 0)
                && (MAPI_FALSE == m_stScanRemoveRFInfo.stScanRFList[i].bFreqUsed[j]))
            {
                u32Freq = m_stScanRemoveRFInfo.stScanRFList[i].au32Frequency[j]/100;
                m_stScanRemoveRFInfo.stScanRFList[i].bFreqUsed[j] = MAPI_TRUE;
                MW_SI_NIT_AUTOUPDATE_DBG("ScanRF List:> next frequency   %u\n", u32Freq);
                return u32Freq;
            }
        }
    }
    MW_SI_NIT_AUTOUPDATE_DBG("ScanRF List:> next frequency fail !!!");
    return u32Freq;
}

#if (DVBT_SYSTEM_ENABLE == 1)
MAPI_BOOL MW_DVB_SI_PSI_Parser::_CheckNITAutoUpdateForDVBT(DVB_PROG *pCurProg, DVB_CM *pCMDB, DVB_MUX *pMux, DVB_NETWORK *pNetwork)
{
    int i, j, k;
    MAPI_U8 u8Index = 0;
    MAPI_U8 u8NetworkIndex;
    MAPI_U8 u8TSRemoveIndex[MAPI_SI_NTV_MAX_TS_IN_NETWORK];
    DVB_MUX *pstMuxInfo = NULL;

    if((m_eParserType != MW_DVB_T_PARSER)
        || (m_stNit.u8Version == INVALID_PSI_SI_VERSION)
#if (NCD_ENABLE == 1)
#else
        || (m_stUpdateStatus.bDisableAutoUpdate == TRUE)
#endif
        || (m_stNit.u16NetworkID == INVALID_NID)
        || (m_stNit.u16TSNumber == 0))
    {
        MW_SI_NIT_AUTOUPDATE_DBG("NIT Check Fail 1>>nit ver:%d, DisAutoUpdate:%d, NID:%d, TS num:%d\n",
            m_stNit.u8Version,m_stUpdateStatus.bDisableAutoUpdate,m_stNit.u16NetworkID,m_stNit.u16TSNumber);
        return FALSE;
    }

    // for Norway RiksTV & Finland Antenna Ready HD & NewZealand Freeview
    if (!(((m_eCountry == E_NORWAY) && (m_stNit.pstTSInfo[m_stNit.u16TSNumber-1].u16ONID == NORWAY_ONID))
        || ((m_eCountry == E_NEWZEALAND) && (m_stNit.pstTSInfo[m_stNit.u16TSNumber-1].u16ONID == NEWZEALAND_ONID))
        || ((m_eCountry == E_FINLAND) && (m_bIsSIAutoUpdateOff == FALSE))
#if (NCD_ENABLE == 1)
        || (m_eCountry == E_UK)
#endif
        || ((m_eCountry == E_THAILAND) && (m_stNit.pstTSInfo[m_stNit.u16TSNumber-1].u16ONID == THAILAND_ONID))
        || ((m_eCountry == E_MALAYSIA) && (m_stNit.pstTSInfo[m_stNit.u16TSNumber-1].u16ONID == MALAYSIA_ONID))
        ))
    {
        MW_SI_NIT_AUTOUPDATE_DBG("NIT Check Fail 2>> country:%d; ONID:%d; AutoUpdateOff:%d\n",
            m_eCountry, m_stNit.pstTSInfo[m_stNit.u16TSNumber-1].u16ONID, m_bIsSIAutoUpdateOff);
        return FALSE;
    }

    if((m_stUpdateStatus.bMuxAdd == TRUE)
            || (m_stUpdateStatus.bMuxRemove == TRUE)
            || (m_stUpdateStatus.bFreqChange == TRUE))
    {
        MW_SI_NIT_AUTOUPDATE_DBG("NIT Check Fail 3>> MuxAdd:%d; MuxRemove:%d; FreqChange:%d\n",
            m_stUpdateStatus.bMuxAdd, m_stUpdateStatus.bMuxRemove, m_stUpdateStatus.bFreqChange);
        return FALSE;
    }

    m_stScanRemoveRFInfo.stRemoveTS.u16TSNumber = 0;
    for(i = 0; i < MAPI_SI_NTV_MAX_TS_IN_NETWORK; i++)
    {
        memset(&m_stScanRemoveRFInfo.stScanRFList[i], 0x00, sizeof(MW_DVB_SCAN_RF_INFO));
        m_stScanRemoveRFInfo.stRemoveTS.au16RemoveTS[i] = INVALID_TS_ID;
        m_stScanRemoveRFInfo.stScanRFList[i].u16TransportStream_ID = INVALID_TS_ID;
    }

    // check Mux add and frequency change
    memset(u8TSRemoveIndex, 0x00, sizeof(u8TSRemoveIndex));
    for(i = 0; i < MAPI_SI_MAX_NETWORK; i++)
    {
        if((m_stNit_UpdateInfo.astNit_Info[i].u16NetworkID != INVALID_NID)
                && (m_stNit_UpdateInfo.astNit_Info[i].u16NetworkID == m_stNit.u16NetworkID))
        {
            memset(u8TSRemoveIndex, 0x01, (sizeof(MAPI_U8)*m_stNit_UpdateInfo.astNit_Info[i].u8TSNumber));

            for(j = 0; j < m_stNit.u16TSNumber; j++)
            {
                if(m_stNit.pstTSInfo[j].wTransportStream_ID == INVALID_TS_ID)
                {
                    continue;
                }

                for(k = 0; k < m_stNit_UpdateInfo.astNit_Info[i].u8TSNumber; k++)
                {
                    if(m_stNit.pstTSInfo[j].wTransportStream_ID == m_stNit_UpdateInfo.astNit_Info[i].astTSInfo[k].wTransportStream_ID)
                    {
                        MAPI_U8 u8NewRFIndex, u8OldRFIndex;

                        u8TSRemoveIndex[k] = 0;
                        u8NewRFIndex = 0;

                        while((u8NewRFIndex < MAPI_SI_NTV_MAX_FREQUENCY) && (m_stNit.pstTSInfo[j].au32FrequencyList[u8NewRFIndex] != 0))
                        {
                            u8OldRFIndex = 0;
                            while((u8OldRFIndex < MAPI_SI_NTV_MAX_FREQUENCY) && (m_stNit_UpdateInfo.astNit_Info[i].astTSInfo[k].au32FrequencyList[u8OldRFIndex] != 0))
                            {
                                if(m_stNit.pstTSInfo[j].au32FrequencyList[u8NewRFIndex] == m_stNit_UpdateInfo.astNit_Info[i].astTSInfo[k].au32FrequencyList[u8OldRFIndex])
                                {
                                    break;
                                }

                                u8OldRFIndex++;
                            }

                            if((u8OldRFIndex >= MAPI_SI_NTV_MAX_FREQUENCY) || (m_stNit_UpdateInfo.astNit_Info[i].astTSInfo[k].au32FrequencyList[u8OldRFIndex] == 0))
                            {
                                m_stUpdateStatus.bFreqChange = TRUE;
                                _AddScanRFInfo(m_stNit.pstTSInfo[j].wTransportStream_ID, m_stNit.pstTSInfo[j].au32FrequencyList);
                                break;
                            }

                            u8NewRFIndex++;
                        }

                        break;
                    }
                }

                if(k >= m_stNit_UpdateInfo.astNit_Info[i].u8TSNumber)
                {
                    if((m_eCountry != E_NEWZEALAND) || (m_stNit.pstTSInfo[j].u16ServiceNum > 0))
                    {
                        m_stUpdateStatus.bMuxAdd = TRUE;
                        _AddScanRFInfo(m_stNit.pstTSInfo[j].wTransportStream_ID, m_stNit.pstTSInfo[j].au32FrequencyList);
                    }
                }
            }

            u8Index = 0;
            for(k = 0; k < m_stNit_UpdateInfo.astNit_Info[i].u8TSNumber; k++)
            {
                if(u8TSRemoveIndex[k] == 1)
                {
                    DVB_PROG *pProgDVB = NULL;
                    BOOL bInvalidMux = TRUE;
                    MW_DTV_CM_DB_scope_lock lock(pCMDB);
                    pProgDVB = pCMDB->GetByIndex(0);
                    while(pProgDVB)
                    {
                        DVB_MUX *_pMux = __GetMux(pProgDVB,pCMDB);
                        DVB_NETWORK *_pNetwork = __GetNetwork(_pMux,pCMDB);
                        if((__GetID(pProgDVB, _pMux, _pNetwork, EN_ID_NID) == m_stNit_UpdateInfo.astNit_Info[i].u16NetworkID)&&
                            (__GetID(pProgDVB, _pMux, _pNetwork, EN_ID_ONID) == m_stNit_UpdateInfo.astNit_Info[i].u16ONID)&&
                            (__GetID(pProgDVB, _pMux, _pNetwork, EN_ID_TSID) == m_stNit_UpdateInfo.astNit_Info[i].astTSInfo[k].wTransportStream_ID))
                        {
                            bInvalidMux = FALSE;
                            break;
                        }
                        pProgDVB = pCMDB->GetNext(pProgDVB);
                    }
                    if(bInvalidMux == FALSE)
                    {
                        MW_SI_NIT_AUTOUPDATE_DBG("Found not exist TS 0x%x\n", m_stNit_UpdateInfo.astNit_Info[i].astTSInfo[k].wTransportStream_ID);
                        m_stScanRemoveRFInfo.stRemoveTS.au16RemoveTS[u8Index++] = m_stNit_UpdateInfo.astNit_Info[i].astTSInfo[k].wTransportStream_ID;
                        m_stScanRemoveRFInfo.stRemoveTS.u16TSNumber++;
                        _AddScanRFInfo(m_stNit_UpdateInfo.astNit_Info[i].astTSInfo[k].wTransportStream_ID, m_stNit_UpdateInfo.astNit_Info[i].astTSInfo[k].au32FrequencyList);
                    }
                }
            }
            break;
        }
    }

    u8NetworkIndex = i;
    if(m_stScanRemoveRFInfo.stRemoveTS.u16TSNumber > 0)
    {
        // re-scan all NIT TS when mux removed
        for(i = 0; i < m_stNit.u16TSNumber; i++)
        {
            _AddScanRFInfo(m_stNit.pstTSInfo[i].wTransportStream_ID, m_stNit.pstTSInfo[i].au32FrequencyList);
        }
        m_stUpdateStatus.bMuxRemove = TRUE;
    }

    // check mux center frequency change
    if (m_eCountry == E_FINLAND)
    {
        for(i = 0; i < m_stNit.u16TSNumber; i++)
        {
            for (MAPI_U16 u16Index = 0; u16Index < pCMDB->GetMaxMuxNum(); u16Index++)
            {
                pstMuxInfo = (DVB_MUX *)pCMDB->GetValidMux(u16Index);
                if(pstMuxInfo)
                {
                    if(m_stNit.pstTSInfo[i].wTransportStream_ID == pstMuxInfo->u16TransportStream_ID)
                    {
                        if (m_stNit.pstTSInfo[i].stTDS.u32CentreFreq != 0)
                        {
                            if ((m_stNit.pstTSInfo[i].stTDS.u32CentreFreq/100) != pstMuxInfo->u32Frequency)
                            {
                                MW_SI_NIT_AUTOUPDATE_DBG("CenterFreq Changed>>Nit freq:%d, db freq:%d\n",
                                                            m_stNit.pstTSInfo[i].stTDS.u32CentreFreq/100,pstMuxInfo->u32Frequency);
                                m_stUpdateStatus.bFreqChange = TRUE;
                                _AddScanRFInfo(m_stNit.pstTSInfo[i].wTransportStream_ID, m_stNit.pstTSInfo[i].au32FrequencyList);

                                if (u8Index < MAPI_SI_NTV_MAX_TS_IN_NETWORK)
                                {
                                    // if do re-scaning, remove original mux to avoid duplicate mux.
                                    m_stScanRemoveRFInfo.stRemoveTS.au16RemoveTS[u8Index++] = m_stNit.pstTSInfo[i].wTransportStream_ID;
                                    m_stScanRemoveRFInfo.stRemoveTS.u16TSNumber++;
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    // check mux center frequency change - END

    // update rescan frequenc of multiplex
    if ((TRUE == m_stUpdateStatus.bMuxAdd) || (TRUE == m_stUpdateStatus.bFreqChange)
        || (TRUE == m_stUpdateStatus.bMuxRemove))
    {
        MW_SI_NIT_AUTOUPDATE_DBG("NIT Info update:> bMuxAdd:%d, bFreqChange:%d, bMuxRemove:%d\n",
            m_stUpdateStatus.bMuxAdd, m_stUpdateStatus.bFreqChange, m_stUpdateStatus.bMuxRemove);
#if (NCD_ENABLE == 1)
        _BuildNitInfo();
#else

        m_stUpdateStatus.bDisableAutoUpdate = TRUE;

        if (u8NetworkIndex < MAPI_SI_MAX_NETWORK)
        {
            // recored the network change flag; when user cancel the rescanning, receiver should give this pop-up
            //at least after resuming from standby. use this flag to re-trigger NIT update check
            m_stNit_UpdateInfo.astNit_Info[u8NetworkIndex].u8NetworkChg = MAPI_TRUE;
            if (m_ParserCallBackInfo.EventHandler)
            {
                m_ParserCallBackInfo.EventHandler(E_DVB_SI_SAVE_NIT_INFO,
                                                                        (MAPI_U32)m_ParserCallBackInfo.pCallbackReference,(MAPI_U32)&m_stNit_UpdateInfo);
            }
        }

        //  rescan the whole frequencies band is must for Antenna Ready HD in task 8:30 / 8:31 / A3
        //  and for RiskTV in task 11:2 /addition task 1
        EN_DVB_UPDATE_TYPE eUpdateType = E_NIT_UPDATE_NONE;
        if (m_stUpdateStatus.bMuxAdd)
        {
            eUpdateType = E_NIT_UPDATE_MUX_ADD;
        }
        else if (m_stUpdateStatus.bMuxRemove)
        {
            eUpdateType = E_NIT_UPDATE_MUX_REMOVE;
        }
        else if (m_stUpdateStatus.bFreqChange)
        {
            eUpdateType = E_NIT_UPDATE_FREQ_CHANGE;
        }

        if (eUpdateType != E_NIT_UPDATE_NONE)
        {
            m_stUpdateStatus.bMuxAdd = FALSE;
            m_stUpdateStatus.bFreqChange = FALSE;
            m_stUpdateStatus.bMuxRemove = FALSE;
            MONITOR_NOTIFY(E_DVB_NIT_AUTO_UPDATE_SCAN, &eUpdateType, m_pMonitorNotifyUsrParam, NULL);
        }
#endif
        return TRUE;
    }

    return FALSE;
}
#endif

MAPI_BOOL MW_DVB_SI_PSI_Parser::_BuildChannelByNitInfo(MAPI_SI_TS_INFO *pstTSInfo, MAPI_SI_SERVICE_LIST *pstServiceInfo, DVB_PROG &stNewProgInfo)
{
    MAPI_U16 u16NitSrvPos;

    if ((pstTSInfo == NULL) || (pstServiceInfo == NULL))
    {
        MW_SI_NIT_AUTOUPDATE_DBG("%s %d>>TS info or service info is NULL\n",__FUNCTION__,__LINE__);
        return MAPI_FALSE;
    }

    if (m_eParserType != MW_DVB_C_PARSER)
    {
        MW_SI_NIT_AUTOUPDATE_DBG("%s %d>>It is not dvb-c system\n",__FUNCTION__,__LINE__);
        return MAPI_FALSE;
    }

    _SetProgramWithDeaultValue(stNewProgInfo);
    stNewProgInfo.u16ServiceID = pstServiceInfo->u16ServiceID;

    for (u16NitSrvPos = 0; u16NitSrvPos < pstTSInfo->u16LcnInfoNum; u16NitSrvPos++)
    {
        // check NIT LCN info
        if(pstTSInfo->astLcnInfo[u16NitSrvPos].u16ServiceID == pstServiceInfo->u16ServiceID)
        {
            stNewProgInfo.u16LCN = stNewProgInfo.u16Number = pstTSInfo->astLcnInfo[u16NitSrvPos].u16LCNNumber;
            stNewProgInfo.u16SimuLCN = pstTSInfo->astLcnInfo[u16NitSrvPos].u16SimuLCNNumber;
            stNewProgInfo.stCHAttribute.u8VisibleServiceFlag = pstTSInfo->astLcnInfo[u16NitSrvPos].bIsVisable;
            stNewProgInfo.stCHAttribute.u8NumericSelectionFlag = pstTSInfo->astLcnInfo[u16NitSrvPos].bIsSelectable;
            stNewProgInfo.stCHAttribute.u8IsSpecialSrv = pstTSInfo->astLcnInfo[u16NitSrvPos].bIsSpecialSrv;
            if ((stNewProgInfo.u16LCN == 0) && (!IS_DTG_COUNTRY(m_eCountry)))
            {
                stNewProgInfo.stCHAttribute.u8NumericSelectionFlag = MAPI_FALSE;
                stNewProgInfo.stCHAttribute.u8VisibleServiceFlag = MAPI_FALSE;
            }
            break;
        }
    }

    stNewProgInfo.stCHAttribute.u8RealServiceType = pstServiceInfo->u8ServiceType;
    _UpdateServiceType(&stNewProgInfo.stCHAttribute.u8ServiceType,
                            &stNewProgInfo.stCHAttribute.u8ServiceTypePrio,
                            pstServiceInfo->u8ServiceType);
    _UpdateProgInfoByServiceType(pstServiceInfo->u8ServiceType, &stNewProgInfo, NULL);

    if ((m_eParserType == MW_DVB_C_PARSER)
        && ((m_enCableOperator == EN_CABLEOP_NUMERICABLE) || (m_enCableOperator == EN_CABLEOP_ZIGGO)
            || (IS_UPC(m_enCableOperator))
            || (m_enCableOperator == EN_CABLEOP_TELENET)))
    {
        if (stNewProgInfo.u16LCN == INVALID_LOGICAL_CHANNEL_NUMBER)
        {
            stNewProgInfo.stCHAttribute.u8NumericSelectionFlag = MAPI_FALSE;
            stNewProgInfo.stCHAttribute.u8VisibleServiceFlag = MAPI_FALSE;
        }
    }

    return MAPI_TRUE;
}

BOOL MW_DVB_SI_PSI_Parser::_ServiceMoveCheckLcnEqualOrIgnore(DVB_PROG *pProg, MAPI_SI_TS_INFO *pstTSInfo, MAPI_U16 u16ServiceID)
{
    if(_IsZiggoOperator())
    {
        MAPI_U16 u16LCN = INVALID_LOGICAL_CHANNEL_NUMBER;

        if((pProg == NULL) || (pstTSInfo == NULL))
        {
            return FALSE;
        }

        for(int i=0; i<pstTSInfo->u16LcnInfoNum; i++)
        {
            if(pstTSInfo->astLcnInfo[i].u16ServiceID == u16ServiceID)
            {
                u16LCN = pstTSInfo->astLcnInfo[i].u16LCNNumber;
                break;
            }
        }

        return ((pProg->u16LCN == u16LCN) && (u16LCN !=INVALID_LOGICAL_CHANNEL_NUMBER));
    }

    return TRUE;
}

#if (DVBC_SYSTEM_ENABLE == 1)

MAPI_BOOL MW_DVB_SI_PSI_Parser::_CheckNITAutoUpdateForDVBC(DVB_PROG *pCurProg, DVB_CM *pCMDB, DVB_MUX *pMux, DVB_NETWORK *pNetwork)
{
    MAPI_U16 u16NitTsPos, u16CmdbTsPos, u16NitSrvPos, u16CmdbSrvPos;
    MAPI_U16 u16TSID;
    DESC_CABLE_DEL_SYS *pstCableSys;
    MAPI_BOOL bCurSrvChg = MAPI_FALSE;
    DVB_NETWORK*pstNetworkInfo;
    DVB_MUX *pstMuxInfo, *pstDbMuxInfo;
    DVB_PROG *pstProgInfo = NULL;
    DVB_PROG stNewProgInfo;
    MAPI_BOOL bIsDbChg = MAPI_FALSE;
    MAPI_CHANNEL_TRIPPLE_ID cMovedCurProgId;

    if (m_stNit.u8Version == INVALID_PSI_SI_VERSION)
    {
        MW_SI_NIT_AUTOUPDATE_DBG("%s %d>>Invalid NIT version\n",__FUNCTION__,__LINE__);
        return FALSE;
    }

    if (m_eParserType != MW_DVB_C_PARSER)
    {
        MW_SI_NIT_AUTOUPDATE_DBG("%s %d>>It is not dvb-c system\n",__FUNCTION__,__LINE__);
        return FALSE;
    }

    if( m_bIsSIAutoUpdateOff == MAPI_TRUE)
    {
        MW_SI_NIT_AUTOUPDATE_DBG("%s %d>>CableReady Manual scan already.Nit auto update off.\n",__FUNCTION__,__LINE__);
        return FALSE;
    }

    m_bNitUpdate2Rescan = MAPI_FALSE;
    mapi_utility::freeList(&m_pCableDeliveryInfo);
    _BuildDeliveryInfo(m_stNit, FALSE);
    MW_DTV_CM_DB_scope_lock lock(pCMDB);
    std::vector<U16> AddedTsId;
    // check transponder/service addition
    for (u16NitTsPos = 0; u16NitTsPos < m_stNit.u16TSNumber; u16NitTsPos++)
    {
        u16TSID = m_stNit.pstTSInfo[u16NitTsPos].wTransportStream_ID;
        pstCableSys = _FindCableParambyTSID(u16TSID);

        for (u16CmdbTsPos=0; u16CmdbTsPos<pCMDB->GetMaxMuxNum(); u16CmdbTsPos++)
        {
            pstMuxInfo = (DVB_MUX *)pCMDB->GetValidMux(u16CmdbTsPos);

            if (pstMuxInfo)
            {
                pstNetworkInfo = (DVB_NETWORK *)__GetNetwork(pstMuxInfo, pCMDB);
                ASSERT(pstNetworkInfo);

                if (u16TSID == pstMuxInfo->u16TransportStream_ID)
                {
                    if (pstCableSys != NULL)
                    {
                        MAPI_BOOL bMUXupdate=MAPI_FALSE;
                        if ((pstCableSys->u32CentreFreq/10) != pstMuxInfo->u32Frequency)
                        {
                            MW_SI_NIT_AUTOUPDATE_DBG("%s %d>>Cable param chg: Freq[%d %d]\n",
                                        __FUNCTION__, __LINE__, pstMuxInfo->u32Frequency, pstCableSys->u32CentreFreq/10);
                            pstMuxInfo->u32Frequency = pstCableSys->u32CentreFreq/10;
                            bMUXupdate=MAPI_TRUE;
                        }
                        if((pstCableSys->u32Symbol_rate/10) != pstMuxInfo->u32SymbRate)
                        {
                            MW_SI_NIT_AUTOUPDATE_DBG("%s %d>>Cable param chg: Sym:[%d %d]\n",
                                        __FUNCTION__, __LINE__, pstMuxInfo->u32SymbRate, pstCableSys->u32Symbol_rate/10);
                            pstMuxInfo->u32SymbRate = pstCableSys->u32Symbol_rate/10;
                            bMUXupdate=MAPI_TRUE;
                        }
                        if((pstCableSys->u8Modulation-1) != pstMuxInfo->u8ModulationMode)
                        {
                            MW_SI_NIT_AUTOUPDATE_DBG("%s %d>>Cable param chg: Mod:[%d %d]\n",
                                        __FUNCTION__, __LINE__, pstMuxInfo->u8ModulationMode, pstCableSys->u8Modulation-1);
                            pstMuxInfo->u8ModulationMode = pstCableSys->u8Modulation-1;
                            bMUXupdate=MAPI_TRUE;
                        }
                        if(bMUXupdate)
                        {
                            // cable parameters change
                            if (__GetMux(pCurProg, pCMDB) == pstMuxInfo)
                            {
                                // need re-tune
                                MW_SI_NIT_AUTOUPDATE_DBG("%s %d>>CDC change>>Return service\n", __FUNCTION__, __LINE__);
                                bCurSrvChg = MAPI_TRUE;
                                bIsDbChg = MAPI_TRUE;
                            }
                            pCMDB->UpdateMux(pstMuxInfo);
                        }
                    }

                    if(m_eParserBaseType == MW_DVB_NIT_BASE)
                    {
                        for (u16NitSrvPos = 0; u16NitSrvPos < m_stNit.pstTSInfo[u16NitTsPos].u16ServiceNum; u16NitSrvPos++)
                        {
                            // check service moving first
                            u16CmdbSrvPos = 0;
                            pstProgInfo = pCMDB->GetByIndex(u16CmdbSrvPos);

                            while(pstProgInfo)
                            {
                                pstDbMuxInfo = (DVB_MUX *)__GetMux(pstProgInfo, pCMDB);
                                ASSERT(pstDbMuxInfo);

                                if((pstProgInfo->u16ServiceID == m_stNit.pstTSInfo[u16NitTsPos].astServiceInfo[u16NitSrvPos].u16ServiceID)
                                    && (pstMuxInfo->u16OriginalNetwork_ID == pstDbMuxInfo->u16OriginalNetwork_ID))
                                {
                                    if(pstMuxInfo == pstDbMuxInfo) // Check if existing service
                                    {
                                        break;
                                    }
                                    else if((_ServiceMoveCheckLcnEqualOrIgnore(pstProgInfo, &m_stNit.pstTSInfo[u16NitTsPos], m_stNit.pstTSInfo[u16NitTsPos].astServiceInfo[u16NitSrvPos].u16ServiceID)) //for ziggo test spec v1.9, test vector "NIT_0012 Service Move"
                                                || (pstMuxInfo->u32Frequency != pstDbMuxInfo->u32Frequency))
                                    {
                                        //service moving
                                        MW_SI_NIT_AUTOUPDATE_DBG("%s %d>>Service moving: SID:0x%x; TSID:[0x%x 0x%x]\n",
                                                    __FUNCTION__, __LINE__, m_stNit.pstTSInfo[u16NitTsPos].astServiceInfo[u16NitSrvPos].u16ServiceID, pstMuxInfo->u16TransportStream_ID, pstDbMuxInfo->u16TransportStream_ID);

                                        if(pCMDB->Add(TRUE, *pstProgInfo, *pstMuxInfo, *pstNetworkInfo) != NULL)
                                        {
                                            bIsDbChg = MAPI_TRUE;
                                            if(pCurProg == pstProgInfo) // Moving program is current program, ZIggo 1.91 NIT_0012
                                            {
                                               cMovedCurProgId = MAPI_CHANNEL_TRIPPLE_ID(pstMuxInfo->u16OriginalNetwork_ID, u16TSID, m_stNit.pstTSInfo[u16NitTsPos].astServiceInfo[u16NitSrvPos].u16ServiceID);
                                            }
                                        }

                                        break;
                                    }
                                }

                                u16CmdbSrvPos++;
                                pstProgInfo = pCMDB->GetByIndex(u16CmdbSrvPos);
                            }

                            if ((NULL == pstProgInfo) && (!IS_UPC(m_enCableOperator))) //UPC should not rely on NIT service list
                            {
                                //new service
                                MW_SI_NIT_AUTOUPDATE_DBG("%s %d>>New Service: SID:0x%x; TSID:0x%x\n",
                                                        __FUNCTION__, __LINE__, m_stNit.pstTSInfo[u16NitTsPos].astServiceInfo[u16NitSrvPos].u16ServiceID, pstMuxInfo->u16TransportStream_ID);

                                memset(&stNewProgInfo, 0, sizeof(DVB_PROG));
                                if (_BuildChannelByNitInfo(&m_stNit.pstTSInfo[u16NitTsPos], &m_stNit.pstTSInfo[u16NitTsPos].astServiceInfo[u16NitSrvPos], stNewProgInfo))
                                {
                                    if (pCMDB->Add(TRUE, stNewProgInfo, *pstMuxInfo, *pstNetworkInfo))
                                    {
                                        // reset SDT_actual/_other version, parser
                                        if (u16TSID == __GetID(pCurProg, pMux, pNetwork, EN_ID_TSID))
                                        {
                                            m_stSdt.u8Version = INVALID_PSI_SI_VERSION;
                                            if (m_pSdtParser)
                                            {
                                                _DeleteParser((MAPI_U32)m_pSdtParser, MAPI_FALSE);
                                                m_u32SdtMonitorTimer = 0;
                                            }
                                        }

                                        _SetSdtOtherIndex(u16TSID);
                                        bIsDbChg = MAPI_TRUE;
                                    }
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }

        if (u16CmdbTsPos >= pCMDB->GetMaxMuxNum())
        {
            // new transponder
            MW_SI_NIT_AUTOUPDATE_DBG("new transponder,u16CmdbTsPos=%d,db maxMuxNum=%d\n",u16CmdbTsPos,pCMDB->GetMaxMuxNum());

            if ((m_enCableOperator == EN_CABLEOP_CABLEREADY) || (m_eParserBaseType != MW_DVB_NIT_BASE))
            {
                MONITOR_NOTIFY(E_DVB_NIT_AUTO_UPDATE_SCAN, NULL, m_pMonitorNotifyUsrParam, NULL);
                m_bNitUpdate2Rescan = MAPI_TRUE;
                return TRUE;
            }

            if(!IS_UPC(m_enCableOperator))
            {
                if (pstCableSys != NULL)
                {
                    DVB_MUX stMux;
                    DVB_NETWORK stNetwork;

                    MW_SI_NIT_AUTOUPDATE_DBG("%s %d>>New Mux: TSID:0x%x; Freq:%d; sym:%d; mod;%d\n",
                                        __FUNCTION__, __LINE__, u16TSID, pstCableSys->u32CentreFreq/10,
                                        pstCableSys->u32Symbol_rate/10, pstCableSys->u8Modulation-1);

                    memset(&stMux, 0, sizeof(DVB_MUX));
                    stMux.u32Frequency = pstCableSys->u32CentreFreq/10;
                    stMux.u32SymbRate = pstCableSys->u32Symbol_rate/10;
                    stMux.u8ModulationMode = pstCableSys->u8Modulation-1;
                    stMux.u16Network_ID = m_stNit.u16NetworkID;
                    stMux.u16OriginalNetwork_ID = m_stNit.pstTSInfo[u16NitTsPos].u16ONID;
                    stMux.u16TransportStream_ID = u16TSID;

                    memset(&stNetwork, 0, sizeof(DVB_NETWORK));
                    stNetwork.m_u16NetworkID = m_stNit.u16NetworkID;
                    memcpy(&stNetwork.aNetworkName, m_stNit.au8NetWorkName, (MAPI_SI_MAX_NETWORK_NAME > MAX_NETWORK_NAME) ? MAX_NETWORK_NAME : MAPI_SI_MAX_NETWORK_NAME);

                    for (u16NitSrvPos = 0; u16NitSrvPos < m_stNit.pstTSInfo[u16NitTsPos].u16ServiceNum; u16NitSrvPos++)
                    {
                        memset(&stNewProgInfo, 0, sizeof(DVB_PROG));
                        if (_BuildChannelByNitInfo(&m_stNit.pstTSInfo[u16NitTsPos], &m_stNit.pstTSInfo[u16NitTsPos].astServiceInfo[u16NitSrvPos], stNewProgInfo))
                        {
                            MW_SI_NIT_AUTOUPDATE_DBG("%s %d>>New Mux>>New Service: SID:0x%x; type:%d; lcn:%d; vis:%d; sel:%d\n",
                                                __FUNCTION__, __LINE__, m_stNit.pstTSInfo[u16NitTsPos].astServiceInfo[u16NitSrvPos].u16ServiceID, stNewProgInfo.stCHAttribute.u8RealServiceType,
                                                stNewProgInfo.u16LCN, stNewProgInfo.stCHAttribute.u8VisibleServiceFlag,
                                                stNewProgInfo.stCHAttribute.u8NumericSelectionFlag);
                            if (pCMDB->Add(TRUE, stNewProgInfo, stMux, stNetwork))
                            {
                                bIsDbChg = MAPI_TRUE;
                            }
                        }
                    }

                    /// When NIT without servicelist, add the mux into database with a default program;
                    if(m_stNit.pstTSInfo[u16NitTsPos].u16ServiceNum == 0)
                    {
                        _SetProgramWithDeaultValue(stNewProgInfo);
                        if (pCMDB->Add(TRUE, stNewProgInfo, stMux, stNetwork))
                        {
                            bIsDbChg = MAPI_TRUE;
                        }
                    }
                    _AddSdtOther(u16TSID);
                    AddedTsId.push_back(u16TSID);
                }
            }
        }
    }

    if(AddedTsId.size() > 0)
    {
        //0605906: [ComHem]TSID37 services (for example "New Age" (LCN 40), "Disc. World" (LCN45)) are not add to channel list
        //Prioritise the first new added TS when doing SDT_other monitor
        _SetSdtOtherIndex(AddedTsId.front());
    }

    // check transponder/service removal
    if(m_enCableOperator != EN_CABLEOP_OTHER) //only NIT_base rely on NIT ts list
    {
        for (u16CmdbTsPos=0; u16CmdbTsPos<pCMDB->GetMaxMuxNum(); u16CmdbTsPos++)
        {
            pstMuxInfo = pCMDB->GetValidMux(u16CmdbTsPos);
            if (pstMuxInfo)
            {
                for (u16NitTsPos = 0; u16NitTsPos < m_stNit.u16TSNumber; u16NitTsPos++)
                {
                    if (m_stNit.pstTSInfo[u16NitTsPos].wTransportStream_ID == pstMuxInfo->u16TransportStream_ID)
                    {
                        // remove non-exist services
                        if((m_eParserBaseType == MW_DVB_NIT_BASE) && (!IS_UPC(m_enCableOperator)))
                        {
                            MAPI_U16 u16OldONID, u16OldTSID, u16OldSID;
                            u16OldSID = __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_SID);
                            u16OldTSID = __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_TSID);
                            u16OldONID = __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_ONID);
                            if (_RemoveMismatchCH(pCurProg, pCMDB,
                                                m_stNit.pstTSInfo[u16NitTsPos].astServiceInfo, pstMuxInfo,
                                                (DVB_NETWORK *)__GetNetwork(pstMuxInfo, pCMDB)))
                            {
                                MW_SI_NIT_AUTOUPDATE_DBG("%s %d>>Remove Service: TSID:0x%x\n",
                                                        __FUNCTION__, __LINE__, pstMuxInfo->u16TransportStream_ID);

                                DVB_PROG *pstCurProInfo = pCMDB->GetCurr();
                                DVB_MUX *pstCurMux = NULL;
                                DVB_NETWORK *pstCurNetwork = NULL;

                                if (pstCurProInfo)
                                {
                                    pstCurMux = (DVB_MUX *)__GetMux(pstCurProInfo, pCMDB);
                                    pstCurNetwork = (DVB_NETWORK *)__GetNetwork(pstCurMux, pCMDB);
                                }

                                if ((u16OldSID != __GetID(pstCurProInfo, pstCurMux, pstCurNetwork, EN_ID_SID))
                                    || (u16OldTSID != __GetID(pstCurProInfo, pstCurMux, pstCurNetwork, EN_ID_TSID))
                                    || (u16OldONID != __GetID(pstCurProInfo, pstCurMux, pstCurNetwork, EN_ID_ONID)))
                                {
                                    bCurSrvChg = MAPI_TRUE;
                                    _UpdateCurrentProgram();
                                }

                                bIsDbChg = MAPI_TRUE;
                            }
                        }
                        break;
                    }
                }

                if (u16NitTsPos >= m_stNit.u16TSNumber)
                {
                    // remove transponder
                    if (NULL != m_pCableDeliveryInfo)
                    {
                        MW_SI_NIT_AUTOUPDATE_DBG("%s %d>>Remove Mux: TSID:0x%x; Freq:%d\n",
                                                __FUNCTION__, __LINE__, pstMuxInfo->u16TransportStream_ID, pstMuxInfo->u32Frequency);

                        if (pstMuxInfo == m_pCurMux)
                        {
                            bCurSrvChg = MAPI_TRUE;
                        }

                        _ClearSdtOther(pstMuxInfo->u16TransportStream_ID);

                        if (pCMDB->DeleteMux(pstMuxInfo))
                        {
                            if ((m_enCableOperator == EN_CABLEOP_CABLEREADY) || (m_eParserBaseType != MW_DVB_NIT_BASE))
                            {
                                MONITOR_NOTIFY(E_DVB_NIT_AUTO_UPDATE_SCAN, NULL, m_pMonitorNotifyUsrParam, NULL);
                                m_bNitUpdate2Rescan = MAPI_TRUE;
                                return TRUE;
                            }
                            bIsDbChg = MAPI_TRUE;
                        }
                    }
                }
            }
        }
    }

    if (bIsDbChg)
    {
        if(pCMDB->Size())
        {
            pCMDB->ReArrangeNumber();

            if(INVALID_ON_ID != cMovedCurProgId.u16OnId) // Moving program is current program, ZIggo 1.91 NIT_0012
            {
                DVB_PROG* pProg = NULL;
                MW_SI_NIT_AUTOUPDATE_DBG("cMovedCurProgId.u16TsId 0x%x, cMovedCurProgId.u16OnId 0x%x, cMovedCurProgId.u16SrvId 0x%x\n",cMovedCurProgId.u16TsId,cMovedCurProgId.u16OnId,cMovedCurProgId.u16SrvId);
                if(INVALID_TS_ID != cMovedCurProgId.u16TsId)
                {
                    pProg = pCMDB->GetByID(cMovedCurProgId.u16TsId, cMovedCurProgId.u16OnId, cMovedCurProgId.u16SrvId);
                }
                else
                {
                    pProg = pCMDB->GetByID(cMovedCurProgId.u16OnId, cMovedCurProgId.u16SrvId);
                }

                if(NULL != pProg)
                {
                    pCMDB->SetCurr(pProg);
                    bCurSrvChg = TRUE;
                }
            }
            _UpdateCurrentProgram();
        }

        MONITOR_NOTIFY(E_DVB_TS_CHANGE, &bCurSrvChg, m_pMonitorNotifyUsrParam, NULL);
    }

    return TRUE;
}
#endif

void MW_DVB_SI_PSI_Parser::_CheckNITAutoUpdate(DVB_PROG *pCurProg, DVB_CM *pCMDB, DVB_MUX *pMux, DVB_NETWORK *pNetwork)
{
    MW_SI_NIT_AUTOUPDATE_DBG("###  %s  ###\n", __FUNCTION__);

    if (m_eParserType == MW_DVB_T_PARSER)
    {
        _CheckNITAutoUpdateForDVBT(pCurProg, pCMDB, pMux, pNetwork);
    }
#if (DVBC_SYSTEM_ENABLE == 1)
    else if (m_eParserType == MW_DVB_C_PARSER)
    {
        _CheckNITAutoUpdateForDVBC(pCurProg, pCMDB, pMux, pNetwork);
    }
#endif
#if (DVBS_SYSTEM_ENABLE == 1)
    else if(m_eParserType == MW_DVB_S_PARSER)
    {
        _CheckNITAutoUpdateForDVBS(pCurProg, pCMDB, pMux, pNetwork);
    }
#endif
}

void MW_DVB_SI_PSI_Parser::GetUpdateStatus(MAPI_BOOL *bMuxAdd, MAPI_BOOL *bMuxRemove, MAPI_BOOL *bFreqChange)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    *bMuxAdd = m_stUpdateStatus.bMuxAdd;
    *bMuxRemove = m_stUpdateStatus.bMuxRemove;
    *bFreqChange = m_stUpdateStatus.bFreqChange;

    m_stUpdateStatus.bMuxAdd = FALSE;
    m_stUpdateStatus.bMuxRemove = FALSE;
    m_stUpdateStatus.bFreqChange = FALSE;
}
MAPI_BOOL MW_DVB_SI_PSI_Parser::CheckParentalRatingLock(MAPI_U8 u8DbSettingValue, MAPI_U8 &u8CurParentalRating, MAPI_BOOL &bLockChannel)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    /* Exceptions and default cases for parental rating lock mechanism arranged*/

    MAPI_BOOL bIsSpainDVBT = ((E_SPAIN == m_eCountry) && (MW_DVB_T_PARSER == m_eParserType));
    MAPI_BOOL bIsUPC = (IS_UPC(m_enCableOperator) && (MW_DVB_C_PARSER == m_eParserType));
    MAPI_BOOL bIsZiggo = _IsZiggoOperator();
    MAPI_BOOL bIsFVAU = ((E_AUSTRALIA== m_eCountry) && (MW_DVB_T_PARSER == m_eParserType));

    MAPI_U8 u8ParentalRatingDB = u8DbSettingValue;
    MAPI_U8 u8ParentalRatingSI = _GetRating();
    //printf("m_u8CurParentalRating: %d, u8ParentalRatingDB %d u8ParentalRatingSI %d \n",m_u8CurParentalRating, u8ParentalRatingDB, u8ParentalRatingSI);
    bLockChannel = FALSE;

    if( ((u8CurParentalRating != u8ParentalRatingSI) || (bIsZiggo && (u8CurParentalRating == 0)))
            && (u8ParentalRatingSI != 0xff) )
    {
        u8CurParentalRating = u8ParentalRatingSI;
        m_u8CurRatingChkValue = u8ParentalRatingSI;
        //MAPI_BOOL bLockChannel = FALSE;

        if (bIsZiggo)
        {//ZIGGO REQ: block if eit value is greater than db value or eit (15,19) range for dtv services
            if ((((u8ParentalRatingSI > u8ParentalRatingDB) && (u8ParentalRatingSI <= PARENTAL_RATE_BLK_REGULAR_ZIGGO) ) || //valid eit range
                    ((u8ParentalRatingSI >= PARENTAL_RATE_BLK_ALL_ZIGGO) && (u8ParentalRatingSI <= PARENTAL_RATE_MAX_ZIGGO)) || // valid eit range
                    ((u8ParentalRatingSI == PARENTAL_RATE_NONE)|| (u8ParentalRatingSI == PARENTAL_RATE_MIN_ZIGGO)) ||  // valid eit range
                    ((u8ParentalRatingDB == PARENTAL_RATE_MIN_AGE)/*lock_all*/ && (u8ParentalRatingSI <= PARENTAL_RATE_MAX_ZIGGO) )) && // valid eit range
                    ((u8ParentalRatingDB >= PARENTAL_RATE_MIN_AGE) && (u8ParentalRatingDB <= PARENTAL_RATE_MAX_AGE))) //valid db range
            {
                bLockChannel = TRUE;
                //Do not lock if service is not dtv:
                ST_DVB_PROGRAMINFO *pstDvbtProgInfo;
                GET_CUR_PROG(pstDvbtProgInfo,m_pCMDB);
                if (!pstDvbtProgInfo)
                    printf("[%s:%d] Can not get current service type\n",__FUNCTION__,__LINE__);
                else if (pstDvbtProgInfo->stCHAttribute.u8ServiceType != E_SERVICETYPE_DTV)
                    bLockChannel = FALSE;
            }
        }
        else if (bIsUPC)
        {//UPC REQ: block if eit value is greater than or equal to db value
            if (((u8ParentalRatingSI >= u8ParentalRatingDB) && (u8ParentalRatingSI <= PARENTAL_RATE_MAX_AGE)) && //valid eit range
                    ((u8ParentalRatingDB >= PARENTAL_RATE_MIN_AGE) && (u8ParentalRatingDB <= PARENTAL_RATE_MAX_AGE)))//valid db range
            {
                bLockChannel = TRUE;
            }
        }
        else if (bIsSpainDVBT)
        {//SPAIN REQ: block if eit value is greater than or equal to db value or 0x1f for age 19
            if (((u8ParentalRatingSI >= u8ParentalRatingDB) && //valid eit range
                    ((u8ParentalRatingSI <= PARENTAL_RATE_MAX_AGE) || (u8ParentalRatingSI == (PARENTAL_RATE_BLK_SPAINDVBT+3)))) && //valid eit range
                    ((u8ParentalRatingDB > PARENTAL_RATE_MIN_AGE_SPAINDVBT) && (u8ParentalRatingDB <= PARENTAL_RATE_MAX_AGE_SPAINDVBT))) //valid db range
            {
                bLockChannel = TRUE;
            }
        }
        else if(bIsFVAU)
        {
            if(((u8ParentalRatingSI >= u8ParentalRatingDB) && (u8ParentalRatingSI <= PARENTAL_RATE_MAX_AGE)) &&  //valid eit range
                (u8ParentalRatingDB > 0) && (u8ParentalRatingDB <= PARENTAL_RATE_MAX_AGE)) //valid db range
            {//Freeview AU: u8ParentalRatingDB > 0 means if u8ParentalRatingDB=1 for block all
                bLockChannel = TRUE;
            }
        }
        else if((m_eParserType == MW_DVB_C_PARSER) && (m_enCableOperator == EN_CABLEOP_TELENET))
        {
            if((u8ParentalRatingSI > u8ParentalRatingDB)
                && ((u8ParentalRatingSI > PARENTAL_RATE_MIN_AGE) && (u8ParentalRatingSI <= PARENTAL_RATE_MAX_AGE)) //valid eit range
                && ((u8ParentalRatingDB > PARENTAL_RATE_MIN_AGE) && (u8ParentalRatingDB <= PARENTAL_RATE_MAX_AGE))) //valid db range
            {
                bLockChannel = TRUE;
            }
        }
        else if (((u8ParentalRatingSI >= u8ParentalRatingDB) && (u8ParentalRatingSI <= PARENTAL_RATE_MAX_AGE)) &&  //valid eit range
                (u8ParentalRatingDB > PARENTAL_RATE_MIN_AGE) && (u8ParentalRatingDB <= PARENTAL_RATE_MAX_AGE)) //valid db range
        {//DEFAULT: Greater or equal to implementation @ caliber requirement
            bLockChannel = TRUE;
        }

        if(bLockChannel)
        {//block check
            printf("Age block ch> DB:%d - EIT:%d\n", u8ParentalRatingDB, u8ParentalRatingSI);
           // LockProg(MW_DTV_AVMON_MUTE_FLAG_RATING);/*Tugberkk | 15.03.2011 : Parental Check mechanism changed due to PVR parental control*/
        }
        else
        {//unblock check
            printf("Age unblock ch> DB:%d - EIT:%d)\n", u8ParentalRatingDB, u8ParentalRatingSI);
           // UnlockProg(MW_DTV_AVMON_MUTE_FLAG_RATING);/*Tugberkk | 15.03.2011 : Parental Check mechanism changed due to PVR parental control*/
        }
        return TRUE;
    }
    return FALSE;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::CheckParentalObjectiveContentLock(MAPI_U8 u8DbSettingValue, MAPI_U8 &u8CurParentalContent, MAPI_BOOL &bLockChannel)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);

    MAPI_U8 u8ParentalObjectiveContentDB = u8DbSettingValue;
    MAPI_U8 u8ParentalObjectiveContentSI = _GetContent();
    //printf("m_u8CurParentalRating: %d, u8ParentalObjectiveContentDB %d u8ParentalObjectiveContentSI %d \n",m_u8CurParentalRating, u8ParentalObjectiveContentDB, u8ParentalObjectiveContentSI);
    bLockChannel = FALSE;

    if( ((u8CurParentalContent != u8ParentalObjectiveContentSI))&& (u8ParentalObjectiveContentSI != 0xff) )
    {
        u8CurParentalContent = u8ParentalObjectiveContentSI;
        m_u8CurContentChkValue = u8ParentalObjectiveContentSI;

        if ((u8ParentalObjectiveContentSI >= PARENTAL_CONTENT_DRUGS) && (u8ParentalObjectiveContentSI <= PARENTAL_CONTENT_VIOLENCE_SEX_DRUGS) &&  //valid eit range
                (u8ParentalObjectiveContentDB >= PARENTAL_CONTENT_DRUGS) && (u8ParentalObjectiveContentDB <= PARENTAL_CONTENT_VIOLENCE_SEX_DRUGS)&&//valid db range
                ((u8ParentalObjectiveContentDB & u8ParentalObjectiveContentSI) !=0)) //User set check
        {
            bLockChannel = TRUE;
        }

        if(bLockChannel)
        {//block check
            printf("block ch> DB:%d - EIT:%d\n", u8ParentalObjectiveContentDB, u8ParentalObjectiveContentSI);
        }
        else
        {//unblock check
            printf("unblock ch> DB:%d - EIT:%d)\n", u8ParentalObjectiveContentDB, u8ParentalObjectiveContentSI);
        }
        return TRUE;
    }
    return FALSE;
}

void MW_DVB_SI_PSI_Parser::SetNIT_Info(MW_SI_NIT_AUTOUPDATE_INFO* pInfo)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    memcpy(&m_stNit_UpdateInfo,pInfo,sizeof(MW_SI_NIT_AUTOUPDATE_INFO));
}
void MW_DVB_SI_PSI_Parser::SetAutoUpdateDisable(MAPI_BOOL bDisable)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    MW_SI_NIT_AUTOUPDATE_DBG("###  %s    :>>  bDisable  %d\n", __FUNCTION__, bDisable);

    m_stUpdateStatus.bDisableAutoUpdate = bDisable;
}
MAPI_BOOL MW_DVB_SI_PSI_Parser::GetTDTorTOTReady(void)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    return (m_bGotTOT_Ready | m_bGotTDT_Ready);
}
#if (OAD_ENABLE == 1)
void MW_DVB_SI_PSI_Parser::ResetNITVersion(void)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    m_stNit.u8Version = INVALID_PSI_SI_VERSION;
}

void MW_DVB_SI_PSI_Parser::ResetPatVersion(void)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    m_stPat.u8Version = INVALID_PSI_SI_VERSION;
}


void MW_DVB_SI_PSI_Parser::ResetAllPmtVersion(void)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    m_stCurPmt.u8Version = INVALID_PSI_SI_VERSION;
    m_stCurPmt.u32CRC32 = 0;
    m_u8PmtMonitorTimerMultiple = 1;
    for(int i = 0; i < MAX_CHANNEL_IN_MUX; i++)
    {
        m_astAllPmt[i].u8Version = INVALID_PSI_SI_VERSION;
    }

}
#endif
void MW_DVB_SI_PSI_Parser::ResetCurPmtVersion(void)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    m_stCurPmt.u8Version = SI_PSI_FORCE_UPDATE_VER;///for let get service info still work
    m_stCurPmt.u32CRC32 = 0;
    m_u8PmtMonitorTimerMultiple = 20;//for playback case, don't parse PMT so busy, it will be reset to 1(default) when start monitor
}

void MW_DVB_SI_PSI_Parser::ResetCurPmtVersion_OAD(void)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    m_stCurPmt.u8Version = INVALID_PSI_SI_VERSION;///for let get service info still work
    m_u8PmtMonitorTimerMultiple = 1;
    m_stCurPmt.u32CRC32 = 0;
}

void MW_DVB_SI_PSI_Parser::SetReplacedSrvInfo(MW_DVB_RP_SERVICE_INFO *info)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    memcpy(&m_ReplacedServiceInfo, info, sizeof(MW_DVB_RP_SERVICE_INFO));
}

void MW_DVB_SI_PSI_Parser::GetReplacedSrvInfo(MW_DVB_RP_SERVICE_INFO *info)
{
    if(info != NULL)
    {
        ASSERT(m_bInit);
        mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
        memcpy(info, &m_ReplacedServiceInfo,  sizeof(MW_DVB_RP_SERVICE_INFO));
    }
}


void MW_DVB_SI_PSI_Parser::ResetAIT(void)
{
    ASSERT(m_bInit);
    mapi_si_psi_event cEvt;
    cEvt.u32Event = (MAPI_U32) EN_DVB_RESET_AIT;
    m_pParserTriggerEvent->Send(cEvt);

}



#if (ISDB_SYSTEM_ENABLE == 1)
void MW_DVB_SI_PSI_Parser::SetCurrentRFChannel(MAPI_U8 u8CurRfChannel)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    m_u8CurRFChannel = u8CurRfChannel;
    if(m_eParserType != MW_ISDB_T_PARSER)
    {
        MW_SI_PARSER_ERROR("can not set RF Channel Number in this parse type\n");
        ASSERT(0);
    }
}

void MW_DVB_SI_PSI_Parser::SetOneSegment(MAPI_BOOL bDisableOneSegProgFilter)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    m_bDisableOneSegProgFilter = bDisableOneSegProgFilter;
    if(m_eParserType != MW_ISDB_T_PARSER)
    {
        MW_SI_PARSER_ERROR("can not set one segment filter in this parse type\n");
        ASSERT(0);
    }
}


#if 1//(ISDB_CHANNELLOGO_ENABLE == 1)
void MW_DVB_SI_PSI_Parser::_CdtReady(void)
{
    m_bEnableCdtMonitorFlag = MAPI_FALSE;
    FREE(m_CurServiceLogoInfo.pu8LogoData);
    m_CurServiceLogoInfo.u16LogoDataLength= 0;

    m_pCdtParser->GetLogoData(&m_CurServiceLogoInfo.u16LogoDataLength,&m_CurServiceLogoInfo.pu8LogoData);
#if 0
    printf("\n***_CdtReady Logo Data Size 0x%x****\n",m_CurServiceLogoInfo.u16LogoDataLength);
    for(MAPI_U16 i=0;i<m_CurServiceLogoInfo.u16LogoDataLength;i++)
        printf("%02x ",m_CurServiceLogoInfo.pu8LogoData[i]);
    printf("\n\n");
#endif
    _DeleteParser((MAPI_U32)m_pCdtParser , MAPI_FALSE);
    MW_SI_PARSER_MESSAGE("\n\n%s.....\n", __PRETTY_FUNCTION__);
}

void MW_DVB_SI_PSI_Parser::_CDT_Monitor()
{
    if((m_pCdtParser == NULL) && (m_bEnableCdtMonitorFlag == MAPI_TRUE))
    {
        if((m_CurServiceLogoInfo.u8logo_transmission_type == LOGO_TRANSMISSION_TYPE_1) ||
           (m_CurServiceLogoInfo.u8logo_transmission_type == LOGO_TRANSMISSION_TYPE_2))
        {
            m_pCdtParser = new (std::nothrow) mapi_si_CDT_parser(m_pSi, m_pDemux);
            if(NULL != m_pCdtParser)
            {
                if(m_pCdtParser->Init(0x1000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_NORMAL,INVALID_PSI_SI_VERSION)
                        && m_pCdtParser->Start(m_CurServiceLogoInfo.u8logo_transmission_type,m_CurServiceLogoInfo.u16logo_id,m_CurServiceLogoInfo.u16logo_version))
                {
                    printf("mapi_si_CDT_parser Init and Start OK.%s:%d\n",__FUNCTION__,__LINE__);
                    m_s16OpenFilter++;
                }
                else
                {
                    delete m_pCdtParser;
                    m_pCdtParser = NULL;
                }
            }
            else
            {
                printf("new mapi_si_CDT_parser fail.%s:%d\n",__FUNCTION__,__LINE__);
            }
        }
    }
}

MAPI_U16 MW_DVB_SI_PSI_Parser::GetCurrentLogoData(MAPI_U8 * pu8CurLogoData,MAPI_U16 u16BufferSize)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);

    if( u16BufferSize >= m_CurServiceLogoInfo.u16LogoDataLength )
    {
        memcpy(pu8CurLogoData, m_CurServiceLogoInfo.pu8LogoData, m_CurServiceLogoInfo.u16LogoDataLength);
    }
    else
    {
        return 0;
    }

    return m_CurServiceLogoInfo.u16LogoDataLength;
}
#endif
#endif

MAPI_BOOL MW_DVB_SI_PSI_Parser::IsLinkageEPGExist(void)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);

    if((m_NitLinkageEPG.enLinkageType == EN_SI_LINKAGE_TS_CONTAIN_COMPLETE_NETWORK)
        || (m_NitLinkageEPG.enLinkageType == MAPI_SI_LINKAGE_EPG_SERVICE))
    {
        return MAPI_TRUE;
    }
    return MAPI_FALSE;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::GetEPGBarkerChannelInfo(U16* p_u16ONID, U16* p_u16TSID)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);

    if((m_NitLinkageEPG.enLinkageType == EN_SI_LINKAGE_TS_CONTAIN_COMPLETE_NETWORK)
        || (m_NitLinkageEPG.enLinkageType == MAPI_SI_LINKAGE_EPG_SERVICE))
    {
        *p_u16ONID = m_NitLinkageEPG.u16ONId;
        *p_u16TSID = m_NitLinkageEPG.u16TSId;
        return MAPI_TRUE;
    }

    return MAPI_FALSE;
}

void MW_DVB_SI_PSI_Parser::ResetEPGBarkerChannelInfo(void)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);

    memset(&m_NitLinkageEPG, 0x00, sizeof(MAPI_SI_DESC_LINKAGE_INFO));
    m_bEPGBarkerChannelWorking = MAPI_FALSE;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::IsEPGBarkerChannelWorking(void)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);

    return m_bEPGBarkerChannelWorking;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::StopEPGBarkerChannel(void)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);

    m_bEPGBarkerChannelWorking = MAPI_FALSE;
    return TRUE;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::_StartEPGBarkerChannel(void)
{
    if(( m_NitLinkageEPG.enLinkageType == EN_SI_LINKAGE_TS_CONTAIN_COMPLETE_NETWORK )
        || (m_NitLinkageEPG.enLinkageType == MAPI_SI_LINKAGE_EPG_SERVICE))
    {
        _StopAllFilter(MAPI_TRUE);
        m_bEPGBarkerChannelWorking = MAPI_TRUE;
        return MAPI_TRUE;
    }
    else
    {
        m_bEPGBarkerChannelWorking = MAPI_FALSE;
        return MAPI_FALSE;
    }
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::_IsSpecificSupport(MW_DVB_SPECIFIC_SUPPORT eType, void* param1, void* param2)
{
    ASSERT(param1);
    switch(eType)
    {
        case E_DVB_SPECIFIC_SERVICE_TYPE_SUPPORT:
        {
            MAPI_U8 u8ServiceType=*(MAPI_U8*)param1;
            ASSERT(param2);
            switch(u8ServiceType)
            {
                case E_TYPE_AC_RADIO:
                case E_TYPE_RADIO:
                case E_TYPE_TTX:
                case E_TYPE_DATA:
                case E_TYPE_MHP:
                case E_TYPE_ACHD_DTV:
                case E_TYPE_ACSD_DTV:
                case E_TYPE_DTV:
                case E_TYPE_HD_DTV:
#if (NVOD_ENABLE==1)
                case E_TYPE_NVODRD:
                case E_TYPE_NVODTS:
#endif
                {
                    if((m_eParserType == MW_DVB_C_PARSER)&&(m_enCableOperator ==EN_CABLEOP_STOFA))
                    {
                        if((u8ServiceType == E_TYPE_DTV)||(u8ServiceType == E_TYPE_RADIO))
                        {
                            *(MAPI_U8*)param2=u8ServiceType;
                            return MAPI_TRUE;
                        }
                        else
                        {
                            *(MAPI_U8*)param2=E_SERVICETYPE_INVALID;
                            return MAPI_FALSE;
                        }
                    }
                    else if((m_eParserType == MW_DVB_T_PARSER)&&(m_eCountry == E_SPAIN))
                    {
                        if((u8ServiceType == E_TYPE_DTV)||(u8ServiceType == E_TYPE_RADIO)||
                            (u8ServiceType == E_TYPE_AC_RADIO)||(u8ServiceType == E_TYPE_ACSD_DTV)||
                            (u8ServiceType == E_TYPE_ACHD_DTV))
                        {
                            *(MAPI_U8*)param2=u8ServiceType;
                            return MAPI_TRUE;
                        }
                        else
                        {
                            *(MAPI_U8*)param2 =E_SERVICETYPE_INVALID;
                            return MAPI_FALSE;
                        }
                    }
                    else
                    {
                        *(MAPI_U8*)param2=u8ServiceType;
                        return MAPI_TRUE;
                    }
                }
                case E_TYPE_BEIJING_HDTV:
                case E_TYPE_BEIJING_3DTV_H264:
                case E_TYPE_BEIJING_3DTV_AVS:
                case E_TYPE_BEIJING_3DTV:
                case E_TYPE_GUANGZHOU_HDTV:
                case E_TYPE_LEIMENG_HDTV:
                case E_TYPE_GUANGZHOUJIANGMENG_HDTV:
                case E_TYPE_SHANGHAI_HDTV:
                {
                    if(m_eCountry == E_CHINA)
                    {
                        *(MAPI_U8*)param2=E_SERVICETYPE_DTV;
                        return MAPI_TRUE;
                    }
                    else
                    {
                        *(MAPI_U8*)param2=E_SERVICETYPE_INVALID;
                        return MAPI_FALSE;
                    }
                }

                default:
                {
                    //printf(" \33[0;31m >>>>>>>>111  u8ServiceType = 0x%x \n \33[m",u8ServiceType);
#if (A3_STB_ENABLE == 1)
                        //user defined type
                        //should all user defined type be handled like this??
                        if(m_enCableOperator != EN_CABLEOP_STOFA)
                        {
                            if(u8ServiceType == 0xc0&&m_stCurPmt.sVideoInfo[0].wVideoPID!=INVALID_PID)
                            {
                                 *(MAPI_U8*)param2=E_SERVICETYPE_DTV;
                                 return MAPI_TRUE;
                            }
                            else if (u8ServiceType == 0xc0&&m_stCurPmt.stAudInfo[0].wAudPID!=INVALID_PID)
                            {
                                *(MAPI_U8*)param2=E_SERVICETYPE_RADIO;
                                return MAPI_TRUE;
                            }
                        }
#endif

                    *(MAPI_U8*)param2=E_SERVICETYPE_INVALID;
                    return MAPI_FALSE;
                }
            }
        }
        break;
        case E_DVB_SDT_OTHER_ENABLE:
        {
            MEMBER_COUNTRY eCountry=*(MEMBER_COUNTRY*)param1;
            if((m_eParserType == MW_DVB_C_PARSER)
                || (eCountry== E_UK)
                || (eCountry== E_NEWZEALAND)
                || (eCountry== E_NORWAY)
                || (eCountry== E_SWEDEN)
                || (eCountry== E_FINLAND)
                || (eCountry== E_DENMARK)
                )
            {
                return MAPI_TRUE;
            }
            return MAPI_FALSE;
        }
        break;
        case E_DVB_CHECK_PF_PRESENT_IN_SDT:
        {
            MEMBER_COUNTRY eCountry=*(MEMBER_COUNTRY*)param1;
            if((eCountry== E_UK)
                || (eCountry== E_NEWZEALAND)
                || _IsZiggoOperator())
            {
                return MAPI_TRUE;
            }
            return MAPI_FALSE;
        }
        break;
        case E_DVB_DATA_SERVICE_SUPPORT:
        {
            MEMBER_COUNTRY eCountry=*(MEMBER_COUNTRY*)param1;
            if (_IsZiggoOperator()||
                IS_UPC(m_enCableOperator) || // according to UPC CTO - PSI SI overview v1.0.pdf, service_descriptor, data service is not supported.
                ((m_eParserType == MW_DVB_C_PARSER) && (eCountry == E_SWEDEN))||
                 ((m_eParserType == MW_DVB_T_PARSER) && (eCountry == E_ITALY))||
                 ((m_eParserType == MW_DVB_T_PARSER) && (eCountry == E_NORWAY)))
            {
                return MAPI_FALSE;
            }
            else
            {
                return MAPI_TRUE;
            }
        }
        break;
        case E_DVB_SKIP_NONE_PMT_PID:
        {
            MAPI_U16 pmtpid=*(MAPI_U16*)param1;
            if(pmtpid==INVALID_PID)
            {
                if(m_eParserType==MW_ISDB_T_PARSER)
                {
                    return MAPI_TRUE;
                }
                else
                {
                    return MAPI_FALSE;
                }
            }
            else
            {
                return MAPI_FALSE;
            }
        }
        break;
        case E_DVB_CHECK_NON_NORDIG_TEST_NETWORK:
        {
            MEMBER_COUNTRY eCountry=*(MEMBER_COUNTRY*)param1;
            if(IS_DTG_COUNTRY(eCountry)||IS_SOUTHAMERICA_COUNTRY(eCountry)||(m_eCountry==E_CHINA))
            {
                return MAPI_FALSE;
            }
            else
            {
                return MAPI_FALSE;//To facilitate future customization
            }
        }
        break;
        case E_DVB_CHECK_SDT_FREE_CA_MODE:
        {
            MAPI_BOOL bIsZiggo = ((E_NETHERLANDS == m_eCountry) && (EN_CABLEOP_ZIGGO == m_enCableOperator) && (MW_DVB_C_PARSER == m_eParserType));
            if (MAPI_TRUE == bIsZiggo)
            {
                return MAPI_FALSE;
            }
            else
            {
                return MAPI_TRUE;
            }
        }
        break;
        case E_DVB_USER_DEFINE_SERVICE_NAME_SUPPORT:
        {
            if(m_eCountry == E_CHINA)
            {
                return MAPI_TRUE;
            }
            else
            {
                return MAPI_FALSE;
            }
        }
        break;
        default:
            ASSERT(0);
            return MAPI_FALSE;
    }
    return MAPI_FALSE;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::GetAudioComponentTypeByCurAudioPID(MAPI_U16 u16CurAudioPID, AUDIO_TYPE &eAudioType, MAPI_U8 &u8ComponentType)
{
    ASSERT(m_bInit);
    MAPI_BOOL bRet=MAPI_FALSE;
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    eAudioType=E_AUDIOTYPE_MPEG;
    u8ComponentType=0;
    if((m_stCurPmt.u8Version == INVALID_PSI_SI_VERSION)
        || (m_EitPfInfo[0].version_number == INVALID_PSI_SI_VERSION)
        )
    {
        return bRet;
    }
    for(int i=0;i<m_stCurPmt.u8AudioNumber;i++)
    {
        if(m_stCurPmt.stAudInfo[i].wAudPID == u16CurAudioPID)
        {
            for(int j=0;j<m_PFComponentInfo[0].u8AudioComponentNumber;j++)
            {
                if(m_stCurPmt.stAudInfo[i].u8ComponentTag == m_PFComponentInfo[0].aAudioComponentInfo[j].u8ComponentTag)
                {
                    bRet=MAPI_TRUE;
                    switch(m_PFComponentInfo[0].aAudioComponentInfo[j].eStreamContent)
                    {
                        case E_COMPONENT_MPEG_AUDIO:
                            eAudioType=E_AUDIOTYPE_MPEG;
                            u8ComponentType=m_PFComponentInfo[0].aAudioComponentInfo[j].u8ComponentType;
                            break;
                        case E_COMPONENT_AAC_AUDIO:
                            eAudioType=E_AUDIOTYPE_AAC;
                            u8ComponentType=m_PFComponentInfo[0].aAudioComponentInfo[j].u8ComponentType;
                            break;
                        default:
                            bRet=MAPI_FALSE;
                            break;
                    }

                    break;
                }
            }
            break;
        }
    }
    return bRet;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::GetPFComponentInfo(MAPI_SI_COMPONENT_INFO* pPFComponentInfo)
{
    ASSERT(pPFComponentInfo);
    if((m_EitPfInfo[0].version_number != INVALID_PSI_SI_VERSION)
        ||(m_EitPfInfo[1].version_number != INVALID_PSI_SI_VERSION))
    {
        memcpy(pPFComponentInfo,m_PFComponentInfo,sizeof(MAPI_SI_COMPONENT_INFO)*2);
        return MAPI_TRUE;
    }
    return MAPI_FALSE;
}

#if (DVBC_SYSTEM_ENABLE == 1)
DESC_CABLE_DEL_SYS *MW_DVB_SI_PSI_Parser::_FindCableParambyTSID(MAPI_U16 u16TSID)
{
    CABLE_DESC_DEL_SYS_DATA *pCableInfo;
    pCableInfo = m_pCableDeliveryInfo;
    while(pCableInfo)
    {
        if (pCableInfo->u16TSID == u16TSID)
        {
            return &pCableInfo->stCDS;
        }
        pCableInfo = pCableInfo->next;
    }
    return NULL;
}
#endif

#if (DVBS_SYSTEM_ENABLE == 1)
DESC_SATELLITE_DEL_SYS *MW_DVB_SI_PSI_Parser::_FindSatelliteParambyTSID(MAPI_U16 u16TSID)
{
    SATELLITE_DESC_DEL_SYS_DATA *pSatelliteInfo;
    pSatelliteInfo = m_pSatelliteDeliveryInfo;

    while(pSatelliteInfo)
    {
        if (pSatelliteInfo->u16TSID == u16TSID)
        {
            return &pSatelliteInfo->stSDS;
        }
        pSatelliteInfo = pSatelliteInfo->next;
    }
    return NULL;
}
#endif

#if (DVBC_SYSTEM_ENABLE == 1)
MAPI_BOOL MW_DVB_SI_PSI_Parser::_BuildNIDList(void)
{
    m_u16NIDList.clear();

    if(m_enCableOperator == EN_CABLEOP_KDG)
    {
        for(MAPI_U16 u16NID=KDG_START_NID; u16NID<=KDG_END_NID; u16NID++)
        {
            m_u16NIDList.push_back(u16NID);
        }
    }

    return MAPI_TRUE;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::_EnableAllNitFilter(void)
{
    while(m_u16NIDList.size() > 0)
    {
        mapi_si_NIT_parser *pNitParser = new (std::nothrow) mapi_si_NIT_parser(m_pSi, m_pDemux);

        if(pNitParser)
        {
            if(pNitParser->Init(0x2000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_NORMAL, INVALID_PSI_SI_VERSION)
                && pNitParser->Start(NIT_SCAN_TIMEOUT, m_bEnableNetworkFilter, m_u16NIDList.front()))
            {
                m_pNitParserList.push_back(pNitParser);
                m_s16OpenFilter++;
                m_u16NIDList.erase(m_u16NIDList.begin());
            }
            else
            {
                delete pNitParser;
                break;
            }
        }
        else
        {
            ASSERT(0);
            return MAPI_FALSE;
        }
    }

    return MAPI_TRUE;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::_EnableAllSdtFilter(void)
{
    MAPI_U16 u16SdtParserIndex=0;
    while(m_stSdtOtherInfo.stSdtOtherTS.size())
    {
        if (u16SdtParserIndex >= MAPI_SI_MAX_TS_IN_NETWORK)
        {
            m_stSdtOtherInfo.stSdtOtherTS.clear();
            break;
        }
        if(m_pAllSdtParser[u16SdtParserIndex] == NULL)
        {
            m_pAllSdtParser[u16SdtParserIndex] = new (std::nothrow) mapi_si_SDT_parser(m_pSi, m_pDemux);
            if(m_pAllSdtParser[u16SdtParserIndex])
            {
                if(m_pAllSdtParser[u16SdtParserIndex]->Init(0x2000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo, EN_SI_PSI_PARSER_NORMAL, INVALID_PSI_SI_VERSION)
                    && m_pAllSdtParser[u16SdtParserIndex]->Start(SDT_OTHER_SCAN_TIMEOUT, EN_TABLE_ALL, m_stSdtOtherInfo.stSdtOtherTS.back().u16TSID, MAPI_TRUE))
                {
                    m_s16OpenFilter++;
                    MW_SI_PARSER_QUICK_SCAN("All SDT parse[%d]=0x%x tsid =0x%x\n",u16SdtParserIndex,(U32)m_pAllSdtParser[u16SdtParserIndex],m_stSdtOtherInfo.stSdtOtherTS.back().u16TSID);
                    _ClearSdtOther(m_stSdtOtherInfo.stSdtOtherTS.back().u16TSID);
                }
                else
                {
                    delete m_pAllSdtParser[u16SdtParserIndex];
                    m_pAllSdtParser[u16SdtParserIndex] = NULL;
                    break;
                }
            }
            else
            {
                ASSERT(0);
                return MAPI_FALSE;
            }
        }
        u16SdtParserIndex++;
    }
    return MAPI_TRUE;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::_FindTsidByCableParam(MAPI_U16 &u16TSID)
{
    CABLE_DESC_DEL_SYS_DATA *pCableInfo;
    pCableInfo = m_pCableDeliveryInfo;
    while(pCableInfo)
    {
        if(((pCableInfo->stCDS.u32CentreFreq/10) == m_stCableParam.u32CentreFreq/10)
            && ((pCableInfo->stCDS.u32Symbol_rate) == m_stCableParam.u32Symbol_rate)
            && (pCableInfo->stCDS.u8Modulation == m_stCableParam.u8Modulation))
        {
            u16TSID = pCableInfo->u16TSID;
            return MAPI_TRUE;
        }
        pCableInfo = pCableInfo->next;
    }
    u16TSID = INVALID_TS_ID;
    return MAPI_FALSE;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::_QuickScanInit(void)
{
    MW_SI_PARSER_QUICK_SCAN("%s %d\n", __FUNCTION__, __LINE__);

    _StopAllFilter(MAPI_TRUE);
#if EIT_BUFFER_USE_QUEUE
    m_bResetEitParser = MAPI_TRUE;
#endif
    _FreeDeliveryInfo(m_stNit, MAPI_TRUE);
    m_bRunning = MAPI_FALSE;
    m_eParserMode = E_DVB_IDLE;
    m_u16NewAddChnNum = 0;
    m_u32StartUpdateTime = INVALID_TIME;
    MW_SI_PARSER_QUICK_SCAN("EN_DVB_QUICK_SCAN_START\n");
    MW_SI_PARSER_QUICK_SCAN("EN_DVB_QUICK_SCAN_START clear old event OK\n");
    _ResetInfo(RESET_ALL);
    _ResetStatus();
    m_eParserMode = E_DVB_QUICK_SCAN;
    m_enQuickScanState = EN_QUICK_SCAN_STATE_GET_NIT;
    m_bRunning = MAPI_TRUE;
    return MAPI_TRUE;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::_QuickScanGetNIT(void)
{
    MW_SI_PARSER_QUICK_SCAN("%s %d\n", __FUNCTION__, __LINE__);

    if(m_enCableOperator == EN_CABLEOP_KDG)
    {
        _BuildNIDList();
        if(_EnableAllNitFilter() == MAPI_FALSE)
        {
            m_u16NIDList.clear();
            return MAPI_FALSE;
        }

        if(m_u16NIDList.size() > 0)
        {
            m_enQuickScanState = EN_QUICK_SCAN_STATE_CONTINUE_GET_NIT;
        }
        else
        {
            m_enQuickScanState = EN_QUICK_SCAN_STATE_GET_SDT;
        }
    }
    else
    {
        m_pNitParser = new (std::nothrow) mapi_si_NIT_parser(m_pSi, m_pDemux);
        if(m_pNitParser)
        {
            if(m_pNitParser->Init(0x2000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_NORMAL, INVALID_PSI_SI_VERSION)
                    && m_pNitParser->Start(NIT_SCAN_TIMEOUT, m_bEnableNetworkFilter, m_u16NetworkID))
            {
                MW_SI_PARSER_QUICK_SCAN("%s %d>>NIT filter with NID:0x%x\n", __FUNCTION__,__LINE__,m_u16NetworkID);
                m_s16OpenFilter++;
            }
            else
            {
                delete m_pNitParser;
                m_pNitParser = NULL;
                ASSERT(0);
                return MAPI_FALSE;
            }
        }
        else
        {
            ASSERT(0);
            return MAPI_FALSE;
        }

        m_enQuickScanState = EN_QUICK_SCAN_STATE_GET_SDT;
    }

    return MAPI_TRUE;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::_QuickScanContinueGetNIT(void)
{
    if(m_s16OpenFilter <= 0)
    {
        MW_SI_PARSER_QUICK_SCAN("%s %d\n", __FUNCTION__, __LINE__);

        if(_EnableAllNitFilter() == MAPI_FALSE)
        {
            m_u16NIDList.clear();
            return MAPI_FALSE;
        }

        if(m_u16NIDList.size() > 0)
        {
            m_enQuickScanState = EN_QUICK_SCAN_STATE_CONTINUE_GET_NIT;
        }
        else
        {
            m_enQuickScanState = EN_QUICK_SCAN_STATE_GET_SDT;
        }
    }

    return MAPI_TRUE;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::_QuickScanGetSDT(void)
{
    if (m_s16OpenFilter<=0)
    {
        MAPI_U16 i;
        MW_SI_PARSER_QUICK_SCAN("%s %d\n", __FUNCTION__, __LINE__);

        if (m_stNit.u8Version == INVALID_PSI_SI_VERSION)
        {
            MW_SI_PARSER_QUICK_SCAN("NIT can't be retrived\n");
            return MAPI_FALSE;
        }
        if((m_eParserType == MW_DVB_C_PARSER) && (m_enCableOperator == EN_CABLEOP_KDG))
        {
            if((m_stNit.u16NetworkID < KDG_START_NID) || (m_stNit.u16NetworkID > KDG_END_NID))
            {
                return MAPI_FALSE;
            }

            m_u16NetworkID = m_stNit.u16NetworkID;
        }
        if (m_bEnableNetworkFilter && (m_stNit.u16NetworkID != m_u16NetworkID))
        {
            MW_SI_PARSER_QUICK_SCAN("NID is not match:[0x%x, 0x%x]\n",m_stNit.u16NetworkID, m_u16NetworkID);
            return MAPI_FALSE;
        }
        if (0 == m_stNit.u16TSNumber)
        {
            MW_SI_PARSER_QUICK_SCAN("No carried any TS in NIT\n");
            return MAPI_FALSE;
        }
        if((m_eParserType == MW_DVB_C_PARSER)
            && ((m_enCableOperator == EN_CABLEOP_CDCABLE) || (m_enCableOperator == EN_CABLEOP_CDSMATV)))
        {
            for(i=0;i<m_stNit.u16TSNumber;i++)
            {
                if(m_stNit.pstTSInfo[i].u16ONID != CANALDIGITAL_ONID)
                {
                    MW_SI_PARSER_QUICK_SCAN("The  m_stNit.astTSInfo[%d].u16ONID :0x%x is not match CanalDigital\n",i,m_stNit.pstTSInfo[i].u16ONID);
                    return MAPI_FALSE;
                }
            }
        }
        _BuildDeliveryInfo(m_stNit);
        if ((NULL == m_pCableDeliveryInfo) && (m_enCableOperator != EN_CABLEOP_KDG))
        {
            MW_SI_PARSER_QUICK_SCAN("Can't find the cable delivery system information\n");
            return MAPI_FALSE;
        }
        FREE(m_pstAllSdt);

        m_pstAllSdt = (MAPI_SI_TABLE_SDT *)calloc(1,m_stNit.u16TSNumber * sizeof(MAPI_SI_TABLE_SDT));
        if (NULL == m_pstAllSdt)
        {
            MW_SI_PARSER_QUICK_SCAN("%s>>All SDT malloc FAILE...\n", __FUNCTION__);
            ASSERT(0);
            return MAPI_FALSE;
        }
        else
        {
            //memset(m_pstAllSdt, 0, (m_stNit.u16TSNumber*sizeof(MAPI_SI_TABLE_SDT)));
            m_stSdtOtherInfo.u16NID = m_stNit.u16NetworkID;
            m_stSdtOtherInfo.u8SdtOtherIndex = 0;
            m_stSdtOtherInfo.bValid = MAPI_TRUE;
            for (i=0;i<m_stNit.u16TSNumber;i++)
            {
                _AddSdtOther(m_stNit.pstTSInfo[i].wTransportStream_ID);
                m_pstAllSdt[i].wTransportStream_ID = INVALID_TS_ID;
                m_pstAllSdt[i].wOriginalNetwork_ID = INVALID_ON_ID;
                m_pstAllSdt[i].u8Version = INVALID_PSI_SI_VERSION;
            }
        }

        if (MAPI_FALSE == _EnableAllSdtFilter())
        {
            m_stSdtOtherInfo.stSdtOtherTS.clear();
            return MAPI_FALSE;
        }
        if (m_stSdtOtherInfo.stSdtOtherTS.size()>0)
        {
            m_enQuickScanState = EN_QUICK_SCAN_STATE_CONTINUE_GET_SDT;
        }
        else
        {
            m_enQuickScanState = EN_QUICK_SCAN_STATE_WAIT_SDT_DONE;
        }
    }
    return MAPI_TRUE;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::_QuickScanContinueGetSDT(void)
{
    if (m_s16OpenFilter<=0)
    {
        MW_SI_PARSER_QUICK_SCAN("%s %d\n", __FUNCTION__, __LINE__);

        if (MAPI_FALSE == _EnableAllSdtFilter())
        {
            m_stSdtOtherInfo.stSdtOtherTS.clear();
            return MAPI_FALSE;
        }
        if (m_stSdtOtherInfo.stSdtOtherTS.size()>0)
        {
            m_enQuickScanState = EN_QUICK_SCAN_STATE_CONTINUE_GET_SDT;
        }
        else
        {
            m_enQuickScanState = EN_QUICK_SCAN_STATE_WAIT_SDT_DONE;
        }
    }
    return MAPI_TRUE;
}
MAPI_BOOL MW_DVB_SI_PSI_Parser::_QuickScanWaitSdtDone(void)
{
    if (m_s16OpenFilter<=0)
    {
        MW_SI_PARSER_QUICK_SCAN("%s %d\n", __FUNCTION__, __LINE__);
        m_stSdtOtherInfo.u16NID = INVALID_NID;
        m_stSdtOtherInfo.bValid = MAPI_FALSE;
        m_enQuickScanState = EN_QUICK_SCAN_STATE_BUILD_CH;
        m_bRunning = MAPI_FALSE;
        if(_pfScanResultNotify)
        {
            m_TsInfo.u16NID = m_stNit.u16NetworkID;
            _pfScanResultNotify(E_DVB_SCAN_FINISH, m_u32ScanResultNotifyParam1, &m_TsInfo, m_ProgramInfo);
        }
    }
    return MAPI_TRUE;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::_QuickScanBuildCh(void)
{
    if (m_s16OpenFilter<=0)
    {
        MW_SI_PARSER_QUICK_SCAN("%s %d\n", __FUNCTION__, __LINE__);
        MAPI_U16 u16TSID;
        MAPI_U16 u16NitTsPos = 0;
        MAPI_U16 u16SdtTsPos = 0;
        MAPI_U16 u16NitSrvPos = 0;
        MAPI_U16 u16SdtSrvPos = 0;
        MAPI_U16 u16SrvPos = 0;
        MAPI_U16 u16SrvNum;

        memset(&m_TsInfo, 0, sizeof(m_TsInfo));
        memset(m_ProgramInfo, 0, sizeof(MW_DVB_PROGRAM_INFO)*MAX_CHANNEL_IN_MUX);
        if (MAPI_TRUE == _FindTsidByCableParam(u16TSID))
        {
            for (u16NitTsPos = 0; u16NitTsPos < m_stNit.u16TSNumber; u16NitTsPos++)
            {
                if (m_stNit.pstTSInfo[u16NitTsPos].wTransportStream_ID == u16TSID)
                {
                    break;
                }
            }
            for (u16SdtTsPos = 0; u16SdtTsPos < m_stNit.u16TSNumber; u16SdtTsPos++)
            {
                if (m_pstAllSdt[u16SdtTsPos].wTransportStream_ID == u16TSID)
                {
                    break;
                }
            }
            if (u16NitTsPos<m_stNit.u16TSNumber)
            {
                // update ts info.
                m_TsInfo.u16NID = m_stNit.u16NetworkID;
                m_TsInfo.u16ONID = m_stNit.pstTSInfo[u16NitTsPos].u16ONID;
                m_TsInfo.u16TSID = m_stNit.pstTSInfo[u16NitTsPos].wTransportStream_ID;
                m_TsInfo.u16ServiceCount = 0;
                m_TsInfo.u8NetWrokNameLen = m_stNit.u8NetWrokNameLen;
                memcpy(m_TsInfo.au8NetWorkName, m_stNit.au8NetWorkName , m_TsInfo.u8NetWrokNameLen);
                MW_SI_PARSER_QUICK_SCAN("##############################################\n");
                MW_SI_PARSER_QUICK_SCAN("NID:0x%x; ONID:0x%x; TSID:0x%x\n",m_TsInfo.u16NID, m_TsInfo.u16ONID, m_TsInfo.u16TSID);
                MW_SI_PARSER_QUICK_SCAN("Network name:%s\n",m_TsInfo.au8NetWorkName);
                MW_SI_PARSER_QUICK_SCAN("##############################################\n");

                // get service number by NIT base or SDT base
                if (IS_UPC(m_enCableOperator) && (u16SdtTsPos< m_stNit.u16TSNumber))
                {
                    if (m_pstAllSdt[u16SdtTsPos].u8Version != INVALID_PSI_SI_VERSION)
                    {
                        u16SrvNum = m_pstAllSdt[u16SdtTsPos].wServiceNumber;
                    }
                    else
                    {
                        u16SrvNum = m_stNit.pstTSInfo[u16NitTsPos].u16ServiceNum;
                    }
                }
                else
                {
                    u16SrvNum = m_stNit.pstTSInfo[u16NitTsPos].u16ServiceNum;
                }

                // check test network
                if(IS_NORDIC_COUNTRY(m_eCountry))
                {
                    if (m_stNit.u16NetworkID >= 0xFF01)
                    {
                        u16SrvNum = 0;
                    }
                    else if (m_stNit.pstTSInfo[u16NitTsPos].u16ONID >= 0xFF00)
                    {
                        u16SrvNum = 0;
                    }
                    else if (u16SdtTsPos < m_stNit.u16TSNumber)
                    {
                        if ((m_pstAllSdt[u16SdtTsPos].u8Version != INVALID_PSI_SI_VERSION) &&
                            (m_pstAllSdt[u16SdtTsPos].wOriginalNetwork_ID >= 0xFF00))
                        {
                            u16SrvNum = 0;
                        }
                    }
                }
                else if (!(IS_DTG_COUNTRY(m_eCountry) || (m_eCountry == E_CHINA)))   //E-Book 9.4.2.4 test network
                {
                    if ((m_stNit.u16NetworkID >= 0xFF00) || (m_stNit.pstTSInfo[u16NitTsPos].u16ONID >= 0xFF00))
                    {
                        u16SrvNum = 0;
                    }
                    else if (u16SdtTsPos < m_stNit.u16TSNumber)
                    {
                        if ((m_pstAllSdt[u16SdtTsPos].u8Version != INVALID_PSI_SI_VERSION) &&
                            (m_pstAllSdt[u16SdtTsPos].wOriginalNetwork_ID >= 0xFF00))
                        {
                            u16SrvNum = 0;
                        }
                    }
                }
                // check test network - END

                // update services
                for (u16SrvPos = 0; u16SrvPos<u16SrvNum; u16SrvPos++)
                {
                    // default value
                    _ResetServiceInfo(m_ProgramInfo[m_TsInfo.u16ServiceCount]);

                    //update service id/type by NIT base or SDT base
                    if (IS_UPC(m_enCableOperator) && (u16SdtTsPos< m_stNit.u16TSNumber))
                    {
                        m_ProgramInfo[m_TsInfo.u16ServiceCount].u16ServiceID = m_pstAllSdt[u16SdtTsPos].astServiceInfo[u16SrvPos].u16ServiceID;
                        _UpdateServiceType(&m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceType,
                                            &m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceTypePrio,
                                            m_pstAllSdt[u16SdtTsPos].astServiceInfo[u16SrvPos].u8ServiceType);
                        m_ProgramInfo[m_TsInfo.u16ServiceCount].u8RealServiceType = m_pstAllSdt[u16SdtTsPos].astServiceInfo[u16SrvPos].u8ServiceType;
                    }
                    else
                    {
                        m_ProgramInfo[m_TsInfo.u16ServiceCount].u16ServiceID = m_stNit.pstTSInfo[u16NitTsPos].astServiceInfo[u16SrvPos].u16ServiceID;
                        _UpdateServiceType(&m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceType,
                                            &m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceTypePrio,
                                            m_stNit.pstTSInfo[u16NitTsPos].astServiceInfo[u16SrvPos].u8ServiceType);
                        m_ProgramInfo[m_TsInfo.u16ServiceCount].u8RealServiceType = m_stNit.pstTSInfo[u16NitTsPos].astServiceInfo[u16SrvPos].u8ServiceType;
                    }

                    // update NIT
                    m_ProgramInfo[m_TsInfo.u16ServiceCount].u8NitVer = m_stNit.u8Version;
                    for(u16NitSrvPos = 0; u16NitSrvPos < MAX_CHANNEL_IN_MUX; u16NitSrvPos++)
                    {
                        if(m_ProgramInfo[m_TsInfo.u16ServiceCount].u16ServiceID == m_stNit.pstTSInfo[u16NitTsPos].astLcnInfo[u16NitSrvPos].u16ServiceID)
                        {
                            m_ProgramInfo[m_TsInfo.u16ServiceCount].u16LCN = m_stNit.pstTSInfo[u16NitTsPos].astLcnInfo[u16NitSrvPos].u16LCNNumber;
                            m_ProgramInfo[m_TsInfo.u16ServiceCount].u16SimuLCN = m_stNit.pstTSInfo[u16NitTsPos].astLcnInfo[u16NitSrvPos].u16SimuLCNNumber;

                            m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsSelectable = m_stNit.pstTSInfo[u16NitTsPos].astLcnInfo[u16NitSrvPos].bIsSelectable;
                            m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsVisible = m_stNit.pstTSInfo[u16NitTsPos].astLcnInfo[u16NitSrvPos].bIsVisable;
                            m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsSpecialSrv = m_stNit.pstTSInfo[u16NitTsPos].astLcnInfo[u16NitSrvPos].bIsSpecialSrv;
                            if((m_ProgramInfo[m_TsInfo.u16ServiceCount].u16LCN == 0) && (!IS_DTG_COUNTRY(m_eCountry)))
                            {
                                m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsSelectable = MAPI_FALSE;
                                m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsVisible = MAPI_FALSE;
                            }
                            break;
                        }
                    }

                    if((u16NitSrvPos>=MAX_CHANNEL_IN_MUX)
                        && (m_eParserType == MW_DVB_C_PARSER)
                        && ((m_enCableOperator == EN_CABLEOP_NUMERICABLE) || (m_enCableOperator == EN_CABLEOP_ZIGGO)
                            || (IS_UPC(m_enCableOperator))
                            || (m_enCableOperator == EN_CABLEOP_TELENET)))
                    {
                        //ZIGGO/Telenet Spec,LCN 0 or no LCN can not show in program list
                        if(m_ProgramInfo[m_TsInfo.u16ServiceCount].u16LCN == INVALID_LOGICAL_CHANNEL_NUMBER)
                        {
                            m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsSelectable = MAPI_FALSE;
                            m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsVisible = MAPI_FALSE;
                        }
                    }

                    _UpdateProgInfoByServiceType(m_stNit.pstTSInfo[u16NitTsPos].astServiceInfo[u16SrvPos].u8ServiceType, NULL, &m_ProgramInfo[m_TsInfo.u16ServiceCount]);

                    if (u16SdtTsPos<m_stNit.u16TSNumber)
                    {
                        // SDT found
                        m_ProgramInfo[m_TsInfo.u16ServiceCount].u8SdtVer = m_pstAllSdt[u16SdtTsPos].u8Version;
                        if (IS_UPC(m_enCableOperator) && (u16SdtTsPos< m_stNit.u16TSNumber))
                        {
                            memcpy(m_ProgramInfo[m_TsInfo.u16ServiceCount].au8ServiceName, m_pstAllSdt[u16SdtTsPos].astServiceInfo[u16SrvPos].u8ServiceName, MAPI_SI_MAX_SERVICE_NAME);
                            memcpy(m_ProgramInfo[m_TsInfo.u16ServiceCount].au8ServiceProviderName, m_pstAllSdt[u16SdtTsPos].astServiceInfo[u16SrvPos].u8ServiceProviderName, MAPI_SI_MAX_PROVIDER_NAME);
#if (MULTIPLE_SERVICE_NAME_ENABLE == 1)
                            m_ProgramInfo[m_TsInfo.u16ServiceCount].u8MultiServiceNameCnt = m_pstAllSdt[u16SdtTsPos].astServiceInfo[u16SrvPos].u8MultiServiceNameCnt > MAX_MULTILINGUAL_SERVICE_NAME ? MAX_MULTILINGUAL_SERVICE_NAME : m_pstAllSdt[u16SdtTsPos].astServiceInfo[u16SrvPos].u8MultiServiceNameCnt;
                            for(MAPI_U8 i=0; i<m_ProgramInfo[m_TsInfo.u16ServiceCount].u8MultiServiceNameCnt; i++)
                            {
                                m_ProgramInfo[m_TsInfo.u16ServiceCount].aeLangIndex[i] = m_pstAllSdt[u16SdtTsPos].astServiceInfo[u16SrvPos].aeLangIndex[i];
                                memcpy(m_ProgramInfo[m_TsInfo.u16ServiceCount].u8MultiServiceName[i], m_pstAllSdt[u16SdtTsPos].astServiceInfo[u16SrvPos].u8MultiServiceName[i], MAPI_SI_MAX_SERVICE_NAME);
                            }
#endif
                        }
                        else
                        {
                            for (u16SdtSrvPos = 0; u16SdtSrvPos< m_pstAllSdt[u16SdtTsPos].wServiceNumber; u16SdtSrvPos++)
                            {
                                if (m_pstAllSdt[u16SdtTsPos].astServiceInfo[u16SdtSrvPos].u16ServiceID == m_stNit.pstTSInfo[u16NitTsPos].astServiceInfo[u16SrvPos].u16ServiceID)
                                {
                                    // match service in SDT
                                    memcpy(m_ProgramInfo[m_TsInfo.u16ServiceCount].au8ServiceName, m_pstAllSdt[u16SdtTsPos].astServiceInfo[u16SdtSrvPos].u8ServiceName, MAPI_SI_MAX_SERVICE_NAME);
                                    memcpy(m_ProgramInfo[m_TsInfo.u16ServiceCount].au8ServiceProviderName, m_pstAllSdt[u16SdtTsPos].astServiceInfo[u16SdtSrvPos].u8ServiceProviderName, MAPI_SI_MAX_PROVIDER_NAME);
#if (MULTIPLE_SERVICE_NAME_ENABLE == 1)
                                    m_ProgramInfo[m_TsInfo.u16ServiceCount].u8MultiServiceNameCnt = m_pstAllSdt[u16SdtTsPos].astServiceInfo[u16SdtSrvPos].u8MultiServiceNameCnt > MAX_MULTILINGUAL_SERVICE_NAME ? MAX_MULTILINGUAL_SERVICE_NAME : m_pstAllSdt[u16SdtTsPos].astServiceInfo[u16SdtSrvPos].u8MultiServiceNameCnt;
                                    for(MAPI_U8 i=0; i<m_ProgramInfo[m_TsInfo.u16ServiceCount].u8MultiServiceNameCnt; i++)
                                    {
                                        m_ProgramInfo[m_TsInfo.u16ServiceCount].aeLangIndex[i] = m_pstAllSdt[u16SdtTsPos].astServiceInfo[u16SdtSrvPos].aeLangIndex[i];
                                        memcpy(m_ProgramInfo[m_TsInfo.u16ServiceCount].u8MultiServiceName[i], m_pstAllSdt[u16SdtTsPos].astServiceInfo[u16SdtSrvPos].u8MultiServiceName[i], MAPI_SI_MAX_SERVICE_NAME);
                                    }
#endif
                                    break;
                                }
                            }
                        }
                    }

                    MW_SI_PARSER_QUICK_SCAN("==============================================\n");
                    MW_SI_PARSER_QUICK_SCAN("SID:0x%x; name:%s\n",m_ProgramInfo[m_TsInfo.u16ServiceCount].u16ServiceID,
                        m_ProgramInfo[m_TsInfo.u16ServiceCount].au8ServiceName);
                    MW_SI_PARSER_QUICK_SCAN("type:%d; real type:%d; priority:%d; nit ver:%d; sdt ver:%d\n",
                        m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceType, m_ProgramInfo[m_TsInfo.u16ServiceCount].u8RealServiceType,
                        m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceTypePrio, m_ProgramInfo[m_TsInfo.u16ServiceCount].u8NitVer,
                        m_ProgramInfo[m_TsInfo.u16ServiceCount].u8SdtVer);
                    MW_SI_PARSER_QUICK_SCAN("LCN:%d; simuLCN:%d; sel:%d; vis:%d\n",
                        m_ProgramInfo[m_TsInfo.u16ServiceCount].u16LCN, m_ProgramInfo[m_TsInfo.u16ServiceCount].u16SimuLCN,
                        m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsSelectable, m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsVisible);
                    MW_SI_PARSER_QUICK_SCAN("==============================================\n");
                    m_TsInfo.u16ServiceCount++;
                }
                _AddExtraSSUService();
            }
        }
        if(_pfScanResultNotify)
        {
            _pfScanResultNotify(E_DVB_SCAN_FINISH, m_u32ScanResultNotifyParam1, &m_TsInfo, m_ProgramInfo);
        }
        m_bRunning = MAPI_FALSE;
    }
    return MAPI_TRUE;
}

void MW_DVB_SI_PSI_Parser::_QuickScan(void)
{
    MAPI_BOOL bRet = MAPI_FALSE;
    switch(m_enQuickScanState)
    {
        case EN_QUICK_SCAN_STATE_INIT:
            bRet = _QuickScanInit();
            break;
        case EN_QUICK_SCAN_STATE_GET_NIT:
            bRet = _QuickScanGetNIT();
            break;
        case EN_QUICK_SCAN_STATE_CONTINUE_GET_NIT:
            bRet = _QuickScanContinueGetNIT();
            break;
        case EN_QUICK_SCAN_STATE_GET_SDT:
            bRet = _QuickScanGetSDT();
            break;
        case EN_QUICK_SCAN_STATE_CONTINUE_GET_SDT:
            bRet = _QuickScanContinueGetSDT();
            break;
        case EN_QUICK_SCAN_STATE_WAIT_SDT_DONE:
            bRet = _QuickScanWaitSdtDone();
            break;
        case EN_QUICK_SCAN_STATE_BUILD_CH:
            bRet = _QuickScanBuildCh();
            break;
        default:
            break;
    }
    if (bRet == MAPI_FALSE)
    {
        m_bRunning = MAPI_FALSE;
        _FreeDeliveryInfo(m_stNit, MAPI_TRUE);
        _ScanError(E_DVB_SCAN_DATA_ERROR);
        m_enQuickScanState = EN_QUICK_SCAN_STATE_IDLE;
    }
}
#endif

void MW_DVB_SI_PSI_Parser::_ReplacementSwitchBackMonitor(MAPI_SI_TABLE_SDT *pstSdtOther)
{
    /*monitor replacement service
     * for the other services in current ts*/
    /*check if there is any replacement service to monitor*/
    if (m_ReplacedServiceInfo.bSer_Replacement)
    {/*check triplet for replacement made service*/
        MW_SI_PARSER_MESSAGE("m:sid=0x%x,tsid=0x%x,onid=0x%x\n",m_ReplacedServiceInfo.u16ServiceId,m_ReplacedServiceInfo.u16TSId,m_ReplacedServiceInfo.u16ONId);
        for(int i = 0; i < pstSdtOther->wServiceNumber; i++)
        {
            MW_SI_PARSER_MESSAGE("Loop:sid=0x%x,tsid=0x%x,onid=0x%x\n",pstSdtOther->astServiceInfo[i].u16ServiceID,pstSdtOther->wTransportStream_ID,pstSdtOther->wOriginalNetwork_ID);
            bool replacement_updated = MAPI_FALSE;
            if ((pstSdtOther->astServiceInfo[i].u16ServiceID == m_ReplacedServiceInfo.u16ServiceId) &&
                (pstSdtOther->wOriginalNetwork_ID == m_ReplacedServiceInfo.u16ONId) &&
                (pstSdtOther->wTransportStream_ID == m_ReplacedServiceInfo.u16TSId) )
            {/*update monitored replacement struct*/
                MW_SI_PARSER_UPDATE("Monitoring replacement for sid:%d > eRunningStatus :%d u8FreeCAMode:%d\n"
                        ,m_ReplacedServiceInfo.u16ServiceId,(MW_SI_SDT_RUNNINGSTATUS)pstSdtOther->astServiceInfo[i].u8RunningStatus,
                        pstSdtOther->astServiceInfo[i].u8FreeCAMode);

                if (m_ReplacedServiceInfo.eRunningStatus != (MW_SI_SDT_RUNNINGSTATUS)pstSdtOther->astServiceInfo[i].u8RunningStatus){
                    m_ReplacedServiceInfo.eRunningStatus = (MW_SI_SDT_RUNNINGSTATUS)pstSdtOther->astServiceInfo[i].u8RunningStatus;
                    MW_SI_PARSER_UPDATE("Running status for monitor service changed\n");
                    replacement_updated = MAPI_TRUE;
                }
                if (m_ReplacedServiceInfo.u8FreeCAMode != pstSdtOther->astServiceInfo[i].u8FreeCAMode){
                    m_ReplacedServiceInfo.u8FreeCAMode = pstSdtOther->astServiceInfo[i].u8FreeCAMode;
                    MW_SI_PARSER_UPDATE("CA status for monitor service changed\n");
                    replacement_updated = MAPI_TRUE;
                }
                //check if bSer_Replacement updated
                if(pstSdtOther->astServiceInfo[i].stLinkageInfo.enLinkageType)
                {
                    m_ReplacedServiceInfo.enLinkageType = pstSdtOther->astServiceInfo[i].stLinkageInfo.enLinkageType;
                    m_ReplacedServiceInfo.bSer_Replacement = pstSdtOther->astServiceInfo[i].stLinkageInfo.u8Ser_Replacement;
                }
            }/*~update*/
            if (replacement_updated)
            {
                MW_SI_PARSER_UPDATE("SI:E_DVB_SDT_REPLACEMENT_SWITCH_BACK:sid=0x%x\n",m_ReplacedServiceInfo.u16ServiceId);
                MONITOR_NOTIFY(E_DVB_SDT_REPLACEMENT_SWITCH_BACK, &m_ReplacedServiceInfo, m_pMonitorNotifyUsrParam, NULL);
            }
        }
    }/*~check*/
}
void MW_DVB_SI_PSI_Parser::_ResetServiceInfo(MW_DVB_PROGRAM_INFO &Service)
{
    // default value
    Service.u16PmtPID = INVALID_PID;
    Service.stVideoInfo.wVideoPID = INVALID_PID;
    Service.u16PCRPid = INVALID_PID;
    Service.bIsServiceIDOnly = MAPI_TRUE;
    Service.bIsSpecialSrv = MAPI_FALSE;

    Service.bIsCAExist = MAPI_FALSE;

    Service.bIsDataService = MAPI_FALSE;
    Service.bIsDataBroadcastService = MAPI_FALSE;
    Service.u16LCN = INVALID_LOGICAL_CHANNEL_NUMBER;
    Service.u16SimuLCN = INVALID_LOGICAL_CHANNEL_NUMBER;
    Service.bIsSelectable = MAPI_TRUE;;
    Service.bIsVisible = MAPI_TRUE;

    for(int i = 0; i < MAX_AUD_LANG_NUM; i++)
    {
        Service.stAudInfo[i].u16AudPID = INVALID_PID;
    }
#if (NVOD_ENABLE==1)
    for(int j=0;j<MAX_NVOD_TIME_SHIFT_SRV_NUM;j++)
    {
        Service.stNvodRealSrv[j].u16TsId=INVALID_TS_ID;
        Service.stNvodRealSrv[j].u16OnId=INVALID_ON_ID;
        Service.stNvodRealSrv[j].u16SrvId=INVALID_SERVICE_ID;
    }
    Service.u16NvodRefSrvID=INVALID_SERVICE_ID;
#endif
    Service.u8PatVer = INVALID_PSI_SI_VERSION;
    Service.u8PmtVer = INVALID_PSI_SI_VERSION;
    Service.u8SdtVer = INVALID_PSI_SI_VERSION;
    Service.u8NitVer = INVALID_PSI_SI_VERSION;
    Service.u8ServiceType = E_SERVICETYPE_DTV;


}

void MW_DVB_SI_PSI_Parser::ParsePMTSection(MAPI_U8 *pu8Section, MW_DVB_SI_ServicelInfo &ServiceInfo)
{
    MAPI_SI_TABLE_PMT pmt;
    mapi_si_PMT_parser::ParseSection(pu8Section, pmt);
    _UpdateServiceInfo(MW_SERVICE_PMT_CHANGED,pmt,ServiceInfo);
}

#if (CI_PLUS_ENABLE == 1) // wait to do , update or build service list
MAPI_BOOL MW_DVB_SI_PSI_Parser::NotifyForOPParseNIT(MAPI_U8 *pu8Section, MAPI_U16 u16SecCount)
{
    MAPI_SI_TABLE_NIT stNit;
    table_release nit(&stNit, table_release::E_TABLE_NIT);
    nit.setReleaseType(RELEASE_ALL);

    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);

    if(m_bIsOpMode == MAPI_TRUE)
    {
        memset(&stNit, 0, sizeof(MAPI_SI_TABLE_NIT));
        for(int i=0; i<MAPI_SI_MAX_CI_CONTENT_LABEL; i++)
        {
            memset(&m_stContentLabel[i], 0 ,sizeof(MW_SI_CIPLUS_CONTENT_LABEL));
        }
        mapi_si_NIT_parser::ParseNITSection(pu8Section, stNit, u16SecCount);
        // ci plus content label
        for(int i=0 ; i<stNit.stCIContentLabel.u8CIContentNumber; i++)
        {
            memcpy(m_stContentLabel[i].au8Iso639Lang, stNit.stCIContentLabel.stContentLabel[i].au8Iso639Lang, 3);
            memcpy(m_stContentLabel[i].au8LabelChar, stNit.stCIContentLabel.stContentLabel[i].pu8LabelChar, (stNit.stCIContentLabel.stContentLabel[i].u8LabelLen > MAX_CIPLUS_LABEL_CHAR) ? MAX_CIPLUS_LABEL_CHAR : stNit.stCIContentLabel.stContentLabel[i].u8LabelLen);
            m_stContentLabel[i].u8ContentByteMax = stNit.stCIContentLabel.stContentLabel[i].u8ContentByteMax;
            m_stContentLabel[i].u8ContentByteMin = stNit.stCIContentLabel.stContentLabel[i].u8ContentByteMin;
        }
        /// Nit save multi linkage descriptors now;
        // ci plus baker channel linkage type 0x02
        if(stNit.pNitLinkageInfo!=NULL)
        {
            for(LinkageInfo_t::iterator it = stNit.pNitLinkageInfo->begin();it!= stNit.pNitLinkageInfo->end();++it)
            {
                if((*it).enLinkageType == MAPI_SI_LINKAGE_EPG_SERVICE)
                {
                    memcpy(&m_NitLinkageEPG,&(*it) ,sizeof(MAPI_SI_DESC_LINKAGE_INFO));
                    break;
                }
            }
        }

        GET_CUR_PROG(m_pCurProg, m_pCMDB);
        m_pCurMux = __GetMux(m_pCurProg, m_pCMDB);
        m_pCurNetwork = __GetNetwork(m_pCurMux, m_pCMDB);
#if (DVBS_SYSTEM_ENABLE == 1)
        m_pCurSat = GETSAT(m_pCurMux, m_pCMDB);
#endif
        GET_CUR_PROG(m_pCurProg, m_pCMDB);
        m_pCurMux = __GetMux(m_pCurProg, m_pCMDB);
        m_pCurNetwork = __GetNetwork(m_pCurMux, m_pCMDB);
        if(MAPI_FALSE == _BuildOpChannel(m_pCurProg, m_pCMDB,m_pCurMux, m_pCurNetwork, NULL, stNit))
        {
            MW_SI_CIPLUS_SERVICE_DBG("### update ciplus service fail ###\n");
        }

        _free_SI_TABLE_NIT(stNit);

        return MAPI_TRUE;
    }

    return MAPI_FALSE;

}

EN_DELIVERY_SYS_TYPE MW_DVB_SI_PSI_Parser::GetDeliveryTypeFromNIT(MAPI_U8 *pu8Section, MAPI_U16 u16SecCount)
{
    MAPI_SI_TABLE_NIT stNit;
    EN_DELIVERY_SYS_TYPE eType = E_DELIVERY_SYS_NONE;

    //mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);

    memset(&stNit, 0, sizeof(MAPI_SI_TABLE_NIT));

    mapi_si_NIT_parser::ParseNITDeliverySystem(pu8Section, stNit, u16SecCount);

    if(stNit.pCableDeliveryInfo != NULL)
    {
        eType = E_DELIVERY_SYS_CDSD;
    }
    else if(stNit.pSatelliteDeliveryInfo != NULL)
    {
        eType = E_DELIVERY_SYS_SDSD;
    }
    else
    {
        for(int i = 0; i< stNit.u16TSNumber; i++)
        {
            if(stNit.pstTSInfo[i].stTDS.u32CentreFreq > 0)
            {
                eType = E_DELIVERY_SYS_TDSD;
                break;
            }
        }
    }
    _free_SI_TABLE_NIT(stNit);

    return eType;

}

#endif

void MW_DVB_SI_PSI_Parser::_NordigSimulRplSrvDelet(DVB_CM *pCMDB,DVB_PROG *pCurProg)
{
    //ST_DVB_PROGRAMINFO *pProg;
    DVB_PROG* pProg=NULL;
    while (!m_ListRPServiceInfo.empty())
    {
        MAPI_BOOL bPrgExist=MAPI_FALSE;
        MW_DVB_RP_SERVICE_INFO RPServiceInfo;
        RPServiceInfo = m_ListRPServiceInfo.front();
        m_ListRPServiceInfo.pop_front();
        MW_DTV_CM_DB_scope_lock lock(pCMDB);
        bPrgExist = (NULL != pCMDB->GetByID(RPServiceInfo.u16TSId,
                                                                    RPServiceInfo.u16ONId,
                                                                    RPServiceInfo.u16ServiceId)) ? MAPI_TRUE : MAPI_FALSE;
        MW_SI_PARSER_SCAN("Nordig simulcast replacement:%x,%x,%x,%d\n",RPServiceInfo.u16TSId,RPServiceInfo.u16ONId,RPServiceInfo.u16ServiceId,bPrgExist);
        if(bPrgExist)
        {
            pProg = pCMDB->GetByID(RPServiceInfo.u16RPS_TSId, RPServiceInfo.u16RPS_ONId, RPServiceInfo.u16RPS_ServiceId);
            if(pProg)
            {
                pProg->stCHAttribute.u8IsDelete = MAPI_TRUE;
                pProg->stCHAttribute.u8IsReplaceDel = MAPI_TRUE;
                pCMDB->Update(pProg);
            }
        }
    }
}
void MW_DVB_SI_PSI_Parser::_CheckNordigSimulRplSrv(MAPI_SI_SDT_SERVICE_INFO *pstSdtSrvInfo,MAPI_U16 u16Onid,MAPI_U16 u16Tsid)
{
    if((pstSdtSrvInfo->stLinkageInfo.enLinkageType)&&(IS_NORDIC_COUNTRY(m_eCountry)))
    {
        if (pstSdtSrvInfo->stLinkageInfo.enLinkageType == MAPI_SI_LINKAGE_NORDIG_SIMULCAST_REPLACEMENT_SERVICE)
        {
            MAPI_BOOL bPrgExist=MAPI_FALSE;

            IS_PROG_EXIST(pstSdtSrvInfo->stLinkageInfo.u16TSId,pstSdtSrvInfo->stLinkageInfo.u16ONId,
                                       pstSdtSrvInfo->stLinkageInfo.u16ServiceId,bPrgExist);
            if(bPrgExist)
            {
                MW_DVB_RP_SERVICE_INFO RPServiceInfo;
                RPServiceInfo.enLinkageType = pstSdtSrvInfo->stLinkageInfo.enLinkageType;
                RPServiceInfo.bSer_Replacement = pstSdtSrvInfo->stLinkageInfo.u8Ser_Replacement;
                RPServiceInfo.u16ONId = pstSdtSrvInfo->stLinkageInfo.u16ONId;
                RPServiceInfo.u16TSId = pstSdtSrvInfo->stLinkageInfo.u16TSId;
                RPServiceInfo.u16ServiceId = pstSdtSrvInfo->stLinkageInfo.u16ServiceId;
                RPServiceInfo.u16RPS_ONId = u16Onid;//pstSdtOther->wOriginalNetwork_ID;
                RPServiceInfo.u16RPS_TSId = u16Tsid;//pstSdtOther->wTransportStream_ID;
                RPServiceInfo.u16RPS_ServiceId = pstSdtSrvInfo->u16ServiceID;
                RPServiceInfo.bCurService = MAPI_FALSE;
                RPServiceInfo.u16RPPmtPID = INVALID_PMT_PID;
                RPServiceInfo.u8FreeCAMode = pstSdtSrvInfo->u8FreeCAMode;
                RPServiceInfo.eRunningStatus =  (MW_SI_SDT_RUNNINGSTATUS)pstSdtSrvInfo->u8RunningStatus;
                m_ListRPServiceInfo.push_back(RPServiceInfo);
            }
        }
    }
}

MAPI_U8 MW_DVB_SI_PSI_Parser::_CalSummerTimeOffset(MAPI_U8 *pu8MJDUTC)
{
/* algorithm is referenced http://hi.baidu.com/tgs28/blog/item/76309913cb0eed806538dbf6.html */
    MAPI_U8 u8SummerTimeOffset = 0;
    MAPI_U8 u8WeekDay;
    MAPI_U32 u32CurDTSecond, u32StartDstSecond, u32EndDstSecond;
    MAPI_SI_TIME stCurDT, stStartDstDT, stEndDstDT;
    if (NULL == pu8MJDUTC)
    {
        return u8SummerTimeOffset;
    }
    memset(&stStartDstDT, 0, sizeof(MAPI_SI_TIME));
    memset(&stEndDstDT, 0, sizeof(MAPI_SI_TIME));
    memset(&stCurDT, 0, sizeof(MAPI_SI_TIME));
    u32CurDTSecond = mapi_dvb_utility::SI_MJDUTC2Seconds(pu8MJDUTC, NULL);
    mapi_dvb_utility::SI_Seconds2StTime(u32CurDTSecond, &stCurDT);

    // assign to this year
    stStartDstDT.u16Year = stCurDT.u16Year;
    stEndDstDT.u16Year = stCurDT.u16Year;
    if (IS_EUROPE_COUNTRY(m_eCountry))
    {
        // DST start from the last Sunday in March with transitions at UTC 1:00
        stStartDstDT.u8Month = 3;
        stStartDstDT.u8Day = (31-((5*stStartDstDT.u16Year/4+4)%7));
        stStartDstDT.u8Hour = 1;
        u32StartDstSecond = mapi_dvb_utility::SI_StTime2Seconds(&stStartDstDT);

        // DST end to the last Sunday in October with transitions at UTC 1:00
        stEndDstDT.u8Month = 10;
        stEndDstDT.u8Day = (31-((5*stEndDstDT.u16Year/4+1)%7));
        stEndDstDT.u8Hour = 1;
        u32EndDstSecond = mapi_dvb_utility::SI_StTime2Seconds(&stEndDstDT);

        if ((u32CurDTSecond>=u32StartDstSecond) && (u32CurDTSecond<u32EndDstSecond))
        {
            u8SummerTimeOffset = 1;
        }
        else
        {
            u8SummerTimeOffset = 0;
        }
    }
    else if (m_eCountry == E_AUSTRALIA)
    {
        if ((m_enTimeZone == TIMEZONE_QLD) ||(m_enTimeZone == TIMEZONE_NT))
        {
            u8SummerTimeOffset = 0;
        }
        else
        {
            u32CurDTSecond += mapi_dvb_utility::SI_GetTimeZoneOffset(mapi_dvb_utility::SI_GetTimeOffset(m_enTimeZone));
            // DST start from the first Sunday in October with transitions at local time 2:00
            stStartDstDT.u8Month = 10;
            u8WeekDay = (5*stStartDstDT.u16Year/4+27)%7;
            stStartDstDT.u8Day = (u8WeekDay != 0) ? (1+(7-u8WeekDay)) : 1;
            stStartDstDT.u8Hour = 2;
            u32StartDstSecond = mapi_dvb_utility::SI_StTime2Seconds(&stStartDstDT);

            // DST end to the first Sunday in April with transitions at local time 3:00
            stEndDstDT.u8Month = 4;
            u8WeekDay = (5*stEndDstDT.u16Year/4+12)%7;
            stEndDstDT.u8Day = (u8WeekDay != 0) ? (1+(7-u8WeekDay)) : 1;
            stEndDstDT.u8Hour = 3;
            u32EndDstSecond = mapi_dvb_utility::SI_StTime2Seconds(&stEndDstDT);

            if ((u32CurDTSecond>=u32StartDstSecond) || (u32CurDTSecond<u32EndDstSecond))
            {
                u8SummerTimeOffset = 1;
            }
            else
            {
                u8SummerTimeOffset = 0;
            }
        }
    }
    else if (m_eCountry == E_NEWZEALAND)
    {
        u32CurDTSecond += mapi_dvb_utility::SI_GetTimeZoneOffset(mapi_dvb_utility::SI_GetTimeOffset(m_enTimeZone));
        // DST start from the first Sunday in September with transitions at local time 2:00
        stStartDstDT.u8Month = 9;
        u8WeekDay = (5*stStartDstDT.u16Year/4+25)%7;
        stStartDstDT.u8Day = (u8WeekDay != 0) ? (1+(7-u8WeekDay)) : 1;
        stStartDstDT.u8Hour = 2;
        u32StartDstSecond = mapi_dvb_utility::SI_StTime2Seconds(&stStartDstDT);

        // DST start from the first Sunday in April with transitions at local time 3:00
        stEndDstDT.u8Month = 4;
        u8WeekDay = (5*stEndDstDT.u16Year/4+12)%7;
        stEndDstDT.u8Day = (u8WeekDay != 0) ? (1+(7-u8WeekDay)) : 1;
        stEndDstDT.u8Hour = 3;
        u32EndDstSecond = mapi_dvb_utility::SI_StTime2Seconds(&stEndDstDT);

        if ((u32CurDTSecond>=u32StartDstSecond) || (u32CurDTSecond<u32EndDstSecond))
        {
            u8SummerTimeOffset = 1;
        }
        else
        {
            u8SummerTimeOffset = 0;
        }
    }
    return u8SummerTimeOffset;
}

mapi_si_RCT_Table* MW_DVB_SI_PSI_Parser::GetRCT()
{

    return mapi_epg_criddb::GetInstance()->GetRCT();
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::GetRemoveTS(MW_DVB_REMOVE_RF_INFO& stRemoveTS)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    memcpy(&stRemoveTS, &m_stScanRemoveRFInfo.stRemoveTS, sizeof(MW_DVB_REMOVE_RF_INFO));

    return MAPI_TRUE;
}


void MW_DVB_SI_PSI_Parser::_FreeCIPlusServiceInfo(MAPI_SI_TABLE_NIT &stNit)
{
    mapi_utility::freeList(&stNit.pstCIPlusService);
}
void MW_DVB_SI_PSI_Parser::_free_SI_TABLE_NIT(MAPI_SI_TABLE_NIT& Info)
{
    mapi_utility::freeList(&Info.pCableDeliveryInfo);
    mapi_utility::freeList(&Info.pSatelliteDeliveryInfo);
    mapi_utility::freeList(&Info.pstCIPlusService);
#if (NCD_ENABLE == 1)
    mapi_utility::freeList(&Info.pstMSGD);
#endif
    FREE(Info.pstPartialReception);
    FREE(Info.pastTargetRegionNameInfo);
#if (NCD_ENABLE == 1)
    FREE(Info.pstNCND);
#endif
    if(NULL != Info.pstTSInfo)
    {
        _FreeCIPlusServiceInfo(Info);
        for(int i=0;i<Info.u16TSNumber;i++)
        {
            FREE(Info.pstTSInfo[i].pstTargetRegionInfo);
            for(int j = 0; j < MAPI_SI_MAX_LCD2_SUPPORT; j++)
            {
                FREE(Info.pstTSInfo[i].astLcnV2Info[j].pLCNInfo);
                Info.pstTSInfo[i].astLcnV2Info[j].u8ServicesNumber = 0;
                Info.pstTSInfo[i].astLcnV2Info[j].u8ChannelListID = 0;
            }
        }
        FREE(Info.pstTSInfo);
    }
    Info.u16TSNumber = 0;
    FREE(Info.pFirstLoopLCNInfo);
    Info.u16FirstLoopLCNInfoNum = 0;
    DELETE(Info.pNitLinkageInfo);
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::_IsNetworkChange(void)
{
    for (int i=0; i<MAPI_SI_MAX_NETWORK;i++)
    {
        if (m_stNit_UpdateInfo.astNit_Info[i].u8NetworkChg)
        {
            return MAPI_TRUE;
        }
    }
    return MAPI_FALSE;
}
MAPI_BOOL MW_DVB_SI_PSI_Parser::_IsOnidInNitInvalid(void)
{
    for(int i=0; i<m_stNit.u16TSNumber; i++)
    {
        if(m_stNit.pstTSInfo[i].wTransportStream_ID==m_stSdt.wTransportStream_ID)
        {
            if(m_stNit.pstTSInfo[i].u16ONID>= 0xFF00)
            {
                return MAPI_TRUE;
            }
        }
    }
    return MAPI_FALSE;
}

void MW_DVB_SI_PSI_Parser::GetSpecifyMheg5ServicePMTInfo(MAPI_SI_TABLE_PMT & SpecifyMheg5PMTInfo)
{
     SpecifyMheg5PMTInfo = m_SpecifyMheg5Service;
}
#if(GUIDANCE_ENABLE == 1)
MAPI_BOOL MW_DVB_SI_PSI_Parser:: _AddGuidanceInfoFromSdt(const MAPI_U16 u16OnId,const MAPI_U16 u16TsId,const MAPI_U16 u16SrvId,GUIDANCE_INFO* pGuidance)
{
    if(pGuidance == NULL)
    {
        return MAPI_FALSE;
    }
    ST_TRIPLE_ID tripleid;
    tripleid.u16OnId =u16OnId;
    tripleid.u16TsId =u16TsId;
    tripleid.u16SrvId =u16SrvId;
    Triple_Ids triple_id(tripleid);
    GUIDANCE_INFO Guidance_tmp;
    memset(&Guidance_tmp,0,sizeof(GUIDANCE_INFO));
    std::pair< std::map<Triple_Ids,GUIDANCE_INFO>::iterator , MAPI_BOOL> ret ;


    Guidance_tmp.u8GuidanceType = pGuidance->u8GuidanceType;
    Guidance_tmp.u8GuidanceMode = pGuidance->u8GuidanceMode;
    Guidance_tmp.u8GuidanceTextLength = pGuidance->u8GuidanceTextLength;

    if(Guidance_tmp.u8GuidanceTextLength> MAX_GUIDANCE_TEXT_LENGTH)
    {
        Guidance_tmp.u8GuidanceTextLength = MAX_GUIDANCE_TEXT_LENGTH;
    }

    memcpy(Guidance_tmp.au8GuidanceText,pGuidance->au8GuidanceText,Guidance_tmp.u8GuidanceTextLength);
    std::map<Triple_Ids,GUIDANCE_INFO>::iterator it = m_GuidanceMap.end();
    it = m_GuidanceMap.find(triple_id);

    if(it!=m_GuidanceMap.end())
    {
        memcpy(&(*it).second,&Guidance_tmp,sizeof(GUIDANCE_INFO));
        return MAPI_TRUE;
    }
    else
    {
        ret =m_GuidanceMap.insert(GuidanceInfo_t::value_type(triple_id,Guidance_tmp));
        return ret.second;
    }


}
MAPI_BOOL MW_DVB_SI_PSI_Parser::_ResetGuidanceInfoByTripleIds(const ST_TRIPLE_ID tripleid,GUIDANCE_INFO *pGuidanceInfo)
{
    m_GuidanceMap.erase(tripleid);
    return _AddGuidanceInfoFromSdt(tripleid.u16OnId,tripleid.u16TsId, tripleid.u16SrvId, pGuidanceInfo);
}
MAPI_BOOL MW_DVB_SI_PSI_Parser::GetGuidanceInfoByTripleIds(const ST_TRIPLE_ID tripleid,GUIDANCE_INFO &ResultGuidance)
{
    Triple_Ids triple_id(tripleid);
    std::map<Triple_Ids,GUIDANCE_INFO>::iterator it = m_GuidanceMap.end();
    it = m_GuidanceMap.find(triple_id);

    if(it == m_GuidanceMap.end())
    {
        return MAPI_FALSE;
    }
    else
    {
        ResultGuidance=(*it).second;
        return MAPI_TRUE;
    }
}
MW_DVB_SI_PSI_Parser::Triple_Ids::Triple_Ids(const ST_TRIPLE_ID tripleid)
    :m_tripleid(tripleid)
{
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::Triple_Ids:: operator<(const Triple_Ids &rhs)const
{
    if(m_tripleid.u16OnId==rhs.m_tripleid.u16OnId)
    {
        if(m_tripleid.u16TsId==rhs.m_tripleid.u16TsId)
        {
            return m_tripleid.u16SrvId<rhs.m_tripleid.u16SrvId;
        }
        else
        {
            return m_tripleid.u16TsId<rhs.m_tripleid.u16TsId;
        }
    }
    else
    {
        return m_tripleid.u16OnId<rhs.m_tripleid.u16OnId;
    }
}
MAPI_BOOL MW_DVB_SI_PSI_Parser::Triple_Ids:: operator>(const Triple_Ids &rhs)const
{
     if(m_tripleid.u16OnId==rhs.m_tripleid.u16OnId)
    {
        if(m_tripleid.u16TsId==rhs.m_tripleid.u16TsId)
        {
            return m_tripleid.u16SrvId>rhs.m_tripleid.u16SrvId;
        }
        else
        {
            return m_tripleid.u16TsId>rhs.m_tripleid.u16TsId;
        }
    }
    else
    {
        return m_tripleid.u16OnId>rhs.m_tripleid.u16OnId;
    }
}
#endif

#if (CI_PLUS_ENABLE == 1)
void MW_DVB_SI_PSI_Parser::SetOPVariableInit(MAPI_BOOL bOpMode, void * const pCMDB)
{
    MW_SI_CIPLUS_SERVICE_DBG("%s:%d, bOpMode %d,  m_eParserMode %d\n",__PRETTY_FUNCTION__,__LINE__,bOpMode, m_eParserMode);
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    if(E_DVB_IDLE == m_eParserMode)
    {
        m_bIsOpMode = bOpMode;
        m_pCMDB = (DVB_CM*)pCMDB;
        if(m_pCMDB != NULL)
        {
            GET_CUR_PROG(m_pCurProg, m_pCMDB);
            m_pCurMux = __GetMux(m_pCurProg, m_pCMDB);
            m_pCurNetwork = __GetNetwork(m_pCurMux, m_pCMDB);
#if (DVBS_SYSTEM_ENABLE == 1)
            m_pCurSat = GETSAT(m_pCurMux, m_pCMDB);
#endif
        }
    }
}
void* MW_DVB_SI_PSI_Parser::_AddOpCacheProgram(MW_DVB_PROGRAM_INFO *pServiceInfo, DVB_CM *pCMDB, DVB_MUX *pMux, DVB_NETWORK *pNetwork, DVB_SAT *pSat)
{
    MW_SI_CIPLUS_SERVICE_DBG("%s:%d\n",__PRETTY_FUNCTION__,__LINE__);
    DVB_PROG stChnInfoDVB;
    DVB_MUX cMux;
    DVB_NETWORK cNetwork;
    DVB_SAT cSat;

    memset(&stChnInfoDVB, 0, sizeof(DVB_PROG));
    memcpy(&cMux, pMux, sizeof(DVB_MUX));
    memcpy(&cNetwork, pNetwork, sizeof(DVB_NETWORK));
    if(pSat)
    {
        memcpy(&cSat, pSat, sizeof(DVB_SAT));
    }

    _AddCommonProgramInfo(stChnInfoDVB, pServiceInfo);
    stChnInfoDVB.stCHAttribute.u8Region = DEFAULT_REGION;

    return ((pSat !=  NULL) ? (void*)pCMDB->Add(TRUE, stChnInfoDVB, cMux, cNetwork, cSat) : (void*)pCMDB->Add(TRUE, stChnInfoDVB, cMux, cNetwork));
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::_BuildOpChannel(DVB_PROG *Prog, DVB_CM *pCMDB, DVB_MUX *pMux, DVB_NETWORK *pNetwork, DVB_SAT *pSat, MAPI_SI_TABLE_NIT &stNit)
{
    //printf("### %s  %d\n",__FUNCTION__,__LINE__);

    if(m_bIsOpMode == FALSE)
    {
        return FALSE;
    }
    else
    {
        DVB_MUX *pstMux;
        DVB_NETWORK *pstNetwork;
        DVB_PROG* pProg;
        DVB_MUX stMux;
        MAPI_BOOL bNotify = MAPI_FALSE;
        MAPI_U16 u16TSID;
        MAPI_U16 u16ONID;
        MAPI_U16 u16SID;
        MAPI_U8 u8ServiceType;
        MAPI_U8 u8ServiceTypePrio;
        MAPI_BOOL bUpdate = MAPI_FALSE;
        #if 0
        MAPI_BOOL bRearrange = MAPI_FALSE;
        #endif
        MAPI_U16 u16AddCnt = 0;
        MAPI_BOOL bCmdbMatchNit = MAPI_FALSE;
        MAPI_S32 s32Index;
        DESC_CABLE_DEL_SYS *pstCableSys = NULL;
#if (DVBS_SYSTEM_ENABLE == 1)
        DESC_SATELLITE_DEL_SYS *pstSatelliteSys;
#endif
        CIPLUS_SERVICE_DATA *pCiSrvData = NULL;
#if (DVBS_SYSTEM_ENABLE == 1)
        MAPI_U16 u16Orbital_position;
        MW_SI_SAT_PARAM stSatParam;
#endif
        MAPI_U8 u8SrvNameLen = (MAPI_SI_MAX_SERVICE_NAME > MAX_SERVICE_NAME) ? MAX_SERVICE_NAME : MAPI_SI_MAX_SERVICE_NAME;
        MAPI_U16 u16CurTSID = INVALID_TS_ID;
        MAPI_U16 u16CurONID = INVALID_ON_ID;
        MAPI_U16 u16CurSID = INVALID_SERVICE_ID;
        MAPI_U16 u16CurLCN = INVALID_LOGICAL_CHANNEL_NUMBER;
        MAPI_BOOL bCurSrvChg = MAPI_FALSE;

        // check NIT update
        MW_DTV_CM_DB_scope_lock lock(pCMDB);

        _BuildDeliveryInfo(stNit);
#if (DVBS_SYSTEM_ENABLE == 1)
        memset(&stSatParam, 0, sizeof(MW_SI_SAT_PARAM));
#endif

        for(int i = 0; i < stNit.u16TSNumber; i++)
        {
            u16TSID = stNit.pstTSInfo[i].wTransportStream_ID;
            u16ONID = stNit.pstTSInfo[i].u16ONID;

            memset(&stMux, 0, sizeof(DVB_MUX));

            stMux.u16TransportStream_ID = u16TSID;
            stMux.u16OriginalNetwork_ID = u16ONID;
            stMux.u16Network_ID = stNit.u16NetworkID;
#if (DVBC_SYSTEM_ENABLE == 1)
            pstCableSys = _FindCableParambyTSID(u16TSID);
#endif
#if (DVBS_SYSTEM_ENABLE == 1)
            pstSatelliteSys = _FindSatelliteParambyTSID(u16TSID);
#endif

            if(pstCableSys != NULL)
            {
                stMux.u32Frequency = pstCableSys->u32CentreFreq / 10;
                stMux.u8ModulationMode = pstCableSys->u8Modulation - 1;
                stMux.u32SymbRate = pstCableSys->u32Symbol_rate / 10;
            }
#if (DVBS_SYSTEM_ENABLE == 1)
            else if(pstSatelliteSys != NULL)
            {
                stMux.u32Frequency = pstSatelliteSys->u32Freq / 100;
                stMux.u32SymbRate = pstSatelliteSys->u32Symbol_rate / 10;
                stMux.bPolarity = (pstSatelliteSys->u8polarization != 0) ? 1 : 0;
                u16Orbital_position = pstSatelliteSys->u16Orbital_position;
                stSatParam.u16Orbital_position = u16Orbital_position;
                stSatParam.u32CurrentFreqency = stMux.u32Frequency;

                if((stSatParam.stSatInfo.u16HiLOF == 0) && (stSatParam.stSatInfo.u16LoLOF == 0))
                {
                    if (m_ParserCallBackInfo.EventHandler)
                    {
                        m_ParserCallBackInfo.EventHandler(E_DVB_SI_GET_SATELLITE_INFO,(MAPI_U32)m_ParserCallBackInfo.pCallbackReference,(MAPI_U32)&stSatParam);
                    }
                }

                if (m_ParserCallBackInfo.EventHandler)
                {
                    m_ParserCallBackInfo.EventHandler(E_DVB_SI_ADD_SATELLITE_TRANSPONDER,(MAPI_U32)m_ParserCallBackInfo.pCallbackReference,(MAPI_U32)pstSatelliteSys);
                }
            }
#endif
            else if(stNit.pstTSInfo[i].stT2DS.stT2CellCentreFreq[0].u32CentreFreq[0] > 0)
            {
                switch(stNit.pstTSInfo[i].stT2DS.u8BW)
                {
                    case E_SI_RF_CH_BAND_8MHz:
                        stMux.u8Bandwidth = E_RF_CH_BAND_8MHz;
                        break;
                    case E_SI_RF_CH_BAND_7MHz:
                        stMux.u8Bandwidth = E_RF_CH_BAND_7MHz;
                        break;
                    case E_SI_RF_CH_BAND_6MHz:
                        stMux.u8Bandwidth = E_RF_CH_BAND_6MHz;
                        break;
                    default:
                        stMux.u8Bandwidth = E_RF_CH_BAND_8MHz;
                        break;
                }
            }
            else
            {
                stMux.u32Frequency = (stNit.pstTSInfo[i].stTDS.u32CentreFreq / 100);
                stMux.u16PlpID = DVB_T2_PLP_ID_INVALID;
                switch(stNit.pstTSInfo[i].stTDS.u8BW)
                {
                    case E_SI_RF_CH_BAND_8MHz:
                        stMux.u8Bandwidth = E_RF_CH_BAND_8MHz;
                        break;
                    case E_SI_RF_CH_BAND_7MHz:
                        stMux.u8Bandwidth = E_RF_CH_BAND_7MHz;
                        break;
                    case E_SI_RF_CH_BAND_6MHz:
                        stMux.u8Bandwidth = E_RF_CH_BAND_6MHz;
                        break;
                    default:
                        stMux.u8Bandwidth = E_RF_CH_BAND_8MHz;
                        break;
                }
            }

            pCiSrvData = stNit.pstCIPlusService;
            while(pCiSrvData != NULL)
            {
                if((pCiSrvData->u16TSID == u16TSID) && (pCiSrvData->u16ONID == u16ONID))
                {
                    u16SID = pCiSrvData->u16ServiceID;
                    if(u16SID)
                    {
                        MW_SI_CIPLUS_SERVICE_DBG("u16TSID 0x%x, u16ONID 0x%x, u16SID 0x%x\n",u16TSID,u16ONID,u16SID);
                        pProg = pCMDB->GetByID(u16TSID, u16ONID, u16SID);
                        _UpdateServiceType(&u8ServiceType, &u8ServiceTypePrio, pCiSrvData->u8ServiceType);
                        if((pProg != NULL) && ((pProg->u16LCN == pCiSrvData->u16LCN) || (MAPI_FALSE == memcmp(pProg->u8ServiceName, pCiSrvData->au8ServiceName, u8SrvNameLen))))
                        {
                            if(pProg->stCHAttribute.u8RealServiceType != pCiSrvData->u8ServiceType)
                            {
                                pProg->stCHAttribute.u8RealServiceType = pCiSrvData->u8ServiceType;
                                bUpdate = MAPI_TRUE;
                            }
                            if(pProg->stCHAttribute.u8ServiceType != u8ServiceType)
                            {
                                pProg->stCHAttribute.u8ServiceType = u8ServiceType;
                                bUpdate = MAPI_TRUE;
                            }
                            if(pProg->stCHAttribute.u8ServiceTypePrio != u8ServiceTypePrio)
                            {
                                pProg->stCHAttribute.u8ServiceTypePrio = u8ServiceTypePrio;
                                bUpdate = MAPI_TRUE;
                            }

                            if((pProg->stCHAttribute.u8ServiceType == E_SERVICETYPE_DATA) && (m_eCountry == E_NORWAY))
                            {
                                pCiSrvData->bIsVisable = pCiSrvData->bIsSelectable = MAPI_FALSE;
                                pCiSrvData->u16LCN = INVALID_LOGICAL_CHANNEL_NUMBER;
                            }
                            //#if 0
                            if(pProg->u16LCN != pCiSrvData->u16LCN)
                            {
                                MW_SI_PARSER_UPDATE("LCN change %s %d=>%d\n", pProg->u8ServiceName,
                                                    pProg->u16LCN, pCiSrvData->u16LCN);
                                pProg->u16LCN = pCiSrvData->u16LCN;
                                bUpdate = MAPI_TRUE;
                            }
                            //#endif

                            if(pProg->stCHAttribute.u8VisibleServiceFlag != pCiSrvData->bIsVisable)
                            {
                                //printf("pProg->stCHAttribute.u8ServiceType...%x(%x)\n",pProg->stCHAttribute.u8ServiceType,m_stNit.astTSInfo[i].astLcnInfo[j].u16ServiceID);
                                MW_SI_PARSER_UPDATE("visible change %s %d=>%d\n", pProg->u8ServiceName,
                                                    pProg->stCHAttribute.u8VisibleServiceFlag, pCiSrvData->bIsVisable);
                                pProg->stCHAttribute.u8VisibleServiceFlag = pCiSrvData->bIsVisable;
                                bUpdate = MAPI_TRUE;
                            }
                            if(pProg->stCHAttribute.u8NumericSelectionFlag != pCiSrvData->bIsSelectable)
                            {
                                MW_SI_PARSER_UPDATE("selectable change %s %d=>%d\n", pProg->u8ServiceName,
                                                    pProg->stCHAttribute.u8NumericSelectionFlag, pCiSrvData->bIsSelectable);
                                pProg->stCHAttribute.u8NumericSelectionFlag = pCiSrvData->bIsSelectable;
                                bUpdate = MAPI_TRUE;
                            }
                            if(memcmp(pProg->u8ServiceName, pCiSrvData->au8ServiceName, u8SrvNameLen))
                            {
                                memcpy(pProg->u8ServiceName, pCiSrvData->au8ServiceName, u8SrvNameLen);
                                bUpdate = MAPI_TRUE;
                            }

                            if ((pProg->stCHAttribute.u8RealServiceType ==  E_TYPE_DATA)
                                || (pProg->stCHAttribute.u8RealServiceType == E_TYPE_MHP))
                            {
                                if(MAPI_FALSE == _IsSpecificSupport(E_DVB_DATA_SERVICE_SUPPORT,&m_eCountry, NULL))
                                {
                                    pProg->stCHAttribute.u8NumericSelectionFlag = MAPI_FALSE;
                                    pProg->stCHAttribute.u8VisibleServiceFlag = MAPI_FALSE;
                                    bUpdate = MAPI_TRUE;
                                }
                            }

                            if(bUpdate)
                            {
                                bNotify = MAPI_TRUE;
                                bUpdate = MAPI_FALSE;
                                pCMDB->Update(pProg);
                            }
                        }
                        else
                        {
                            DVB_PROG* pNew = NULL;
                            DVB_NETWORK stNetwork;
                            DVB_SAT stSatInfo;
                            MW_DVB_PROGRAM_INFO ServiceInfo;
                            MAPI_BOOL bSkip = MAPI_FALSE;
                            memset(&stNetwork, 0, sizeof(DVB_NETWORK));
                            memset(&ServiceInfo,0,sizeof(MW_DVB_PROGRAM_INFO));
                            memset(&stSatInfo, 0, sizeof(DVB_SAT));
                            stNetwork.m_u16NetworkID = stNit.u16NetworkID;
                            memcpy(stNetwork.aNetworkName, stNit.au8NetWorkName, (MAPI_SI_MAX_NETWORK_NAME > MAX_NETWORK_NAME) ? MAX_NETWORK_NAME : MAPI_SI_MAX_NETWORK_NAME);

                            ServiceInfo.u16LCN = pCiSrvData->u16LCN;
                            ServiceInfo.u16SimuLCN = DEFAULT_SIMU_LCN;
                            ServiceInfo.bIsVisible = pCiSrvData->bIsVisable;
                            ServiceInfo.bIsSelectable = pCiSrvData->bIsSelectable;
                            ServiceInfo.u16ServiceID = u16SID;
                            ServiceInfo.u8ServiceType = u8ServiceType;
                            ServiceInfo.u8RealServiceType = pCiSrvData->u8ServiceType;
                            ServiceInfo.u8PatVer = INVALID_PSI_SI_VERSION;
                            ServiceInfo.u8PmtVer = INVALID_PSI_SI_VERSION;
                            ServiceInfo.u8SdtVer = INVALID_PSI_SI_VERSION;
                            ServiceInfo.u8NitVer = stNit.u8Version;

#if (DVBS_SYSTEM_ENABLE == 1)
                            if(pstSatelliteSys != NULL)
                            {
                                memcpy(stSatInfo.aSatName, stSatParam.stSatInfo.aSatName, (MAX_SAT_NAME_LEN > (MAX_SATNAME_LEN+1)) ? (MAX_SATNAME_LEN+1) : MAX_SAT_NAME_LEN);
                                stSatInfo.e0V12VOnOff = stSatParam.stSatInfo.e0V12VOnOff;
                                stSatInfo.e22KOnOff = stSatParam.stSatInfo.e22KOnOff;
                                stSatInfo.eDiseqcLevel = stSatParam.stSatInfo.eDiseqcLevel;
                                stSatInfo.eLNBPwrOnOff = stSatParam.stSatInfo.eLNBPwrOnOff;
                                stSatInfo.eLNBType = stSatParam.stSatInfo.eLNBType;
                                stSatInfo.eLNBTypeReal = stSatParam.stSatInfo.eLNBTypeReal;
                                stSatInfo.eSwt10Port = stSatParam.stSatInfo.eSwt10Port;
                                stSatInfo.eSwt11Port = stSatParam.stSatInfo.eSwt11Port;
                                stSatInfo.eToneburstType = stSatParam.stSatInfo.eToneburstType;
                                stSatInfo.u16Angle = stSatParam.stSatInfo.u16Angle;
                                stSatInfo.U16BeginTransponder = stSatParam.stSatInfo.U16BeginTransponder;
                                stSatInfo.u16HiLOF = stSatParam.stSatInfo.u16HiLOF;
                                stSatInfo.u16LoLOF = stSatParam.stSatInfo.u16LoLOF;
                                stSatInfo.u16IFreq = stSatParam.stSatInfo.u16IFreq;
                                stSatInfo.u16NumberOfTP = stSatParam.stSatInfo.u16NumberOfTP;
                                stSatInfo.u8ChannelId = stSatParam.stSatInfo.u8ChannelId;
                                stSatInfo.u8Position = stSatParam.stSatInfo.u8Position;
                                stMux.u8SatID = stSatParam.stSatInfo.u8SatID;

                            }
#endif

                            for(U8 u8Loop = 0 ; u8Loop < MAX_AUD_LANG_NUM; u8Loop++)
                            {
                                ServiceInfo.stAudInfo[u8Loop].u16AudPID = INVALID_PID;
                            }
                            if ((ServiceInfo.u8RealServiceType ==  E_TYPE_DATA) || (ServiceInfo.u8RealServiceType == E_TYPE_MHP))
                            {
                                if(MAPI_FALSE == _IsSpecificSupport(E_DVB_DATA_SERVICE_SUPPORT,&m_eCountry, NULL))
                                {
                                    ServiceInfo.bIsSelectable = MAPI_FALSE;
                                    ServiceInfo.bIsVisible = MAPI_FALSE;
                                }
                            }
                            memcpy(ServiceInfo.au8ServiceName, pCiSrvData->au8ServiceName, MAPI_SI_MAX_SERVICE_NAME);
                            if(ServiceInfo.u8ServiceType == E_SERVICETYPE_INVALID)
                            {
                                bSkip = MAPI_TRUE;
                            }

                            if(ServiceInfo.u16LCN > MAX_SI_CI_PLUS_SERVICE_LCN)
                            {
                                ServiceInfo.bIsSelectable = MAPI_FALSE;
                                ServiceInfo.bIsVisible = MAPI_FALSE;
                            }

                            if(bSkip == MAPI_FALSE)
                            {
                                pNew = (DVB_PROG*)_AddOpCacheProgram(&ServiceInfo, pCMDB, &stMux, &stNetwork, &stSatInfo);

                                if(pNew)
                                {
                                    MW_SI_CIPLUS_SERVICE_DBG("SID  0x%x, LCN %d, selectable %d, visible %d\n"
    ,ServiceInfo.u16ServiceID, ServiceInfo.u16LCN, ServiceInfo.bIsSelectable, ServiceInfo.bIsVisible);
                                    u16AddCnt++;
                                }
                            }
                        }
                    }
                }
                pCiSrvData = pCiSrvData->next;
            }
        }
        // check service is op service or not

        if( pCMDB->Size() > 0)
        {
            s32Index = pCMDB->Size()-1;
            pProg = pCMDB->GetByIndex(s32Index);
            if(Prog != NULL)
            {
                u16CurTSID = __GetID(Prog, pMux, pNetwork, EN_ID_TSID);
                u16CurONID = __GetID(Prog, pMux, pNetwork, EN_ID_ONID);
                u16CurSID = __GetID(Prog, pMux, pNetwork, EN_ID_SID);
                u16CurLCN = Prog->u16LCN;
            }
            while((pProg != NULL) && (s32Index >= 0))
            {
                bCmdbMatchNit = FALSE;
                pstMux = pCMDB->GetMux(pProg->m_u16MuxTableID);
                ASSERT(pstMux);
                pstNetwork = pCMDB->GetNetwork(pstMux->m_u16NetworkTableID);;
                ASSERT(pstNetwork);

                pCiSrvData = stNit.pstCIPlusService;
                while(pCiSrvData != NULL)
                {
                    if((__GetID(pProg, pstMux, pstNetwork, EN_ID_SID) == pCiSrvData->u16ServiceID)
                        &&((pProg->u16LCN == pCiSrvData->u16LCN) || (MAPI_FALSE == memcmp(pProg->u8ServiceName, pCiSrvData->au8ServiceName, u8SrvNameLen)))
                        &&(__GetID(pProg, pstMux, pstNetwork, EN_ID_TSID) == pCiSrvData->u16TSID)
                        &&(__GetID(pProg, pstMux, pstNetwork, EN_ID_ONID) == pCiSrvData->u16ONID))
                    {
                        bCmdbMatchNit = TRUE;
                        break;
                    }
                    pCiSrvData = pCiSrvData->next;
                }

                if(bCmdbMatchNit == MAPI_FALSE)
                {
                    MW_SI_PARSER_UPDATE("delete service ====>%s\n",pProg->u8ServiceName);
                    if((__GetID(pProg, pstMux, pstNetwork, EN_ID_SID) == u16CurSID)
                        &&((pProg->u16LCN == u16CurLCN))
                        &&(__GetID(pProg, pstMux, pstNetwork, EN_ID_TSID) == u16CurTSID)
                        &&(__GetID(pProg, pstMux, pstNetwork, EN_ID_ONID) == u16CurONID)
                        &&(u16CurSID != INVALID_SERVICE_ID))
                    {
                        bCurSrvChg = TRUE;
                    }
                    pCMDB->Delete(pProg);
                    bNotify = MAPI_TRUE;
                }
                s32Index--;
                pProg = pCMDB->GetByIndex(s32Index);

            }
        }
        if((pCMDB->Size() > 0) && (bNotify || u16AddCnt))
        {
            pCMDB->ReArrangeNumber();
            _UpdateCurrentProgram();
            if(m_pCurProg != NULL)
            {
                // check if current program is changed to non-selectable, change to other channel playing
                if (m_pCurProg->stCHAttribute.u8VisibleServiceFlag == MAPI_FALSE)
                {
                    DVB_PROG *pNextProg = NULL;
                    DVB_PROG *pstNonVisibleProg = NULL;
                    pNextProg = pCMDB->GetByIndex(0);
                    while(pNextProg)
                    {
                        {
                            if (pNextProg->stCHAttribute.u8NumericSelectionFlag == MAPI_TRUE)
                            {
                                if (pNextProg->stCHAttribute.u8VisibleServiceFlag == MAPI_TRUE)
                                {
                                    break;
                                }
                                else if (pstNonVisibleProg == NULL)
                                {
                                    pstNonVisibleProg = pNextProg;
                                }
                            }
                        }
                        pNextProg = pCMDB->GetNext(pNextProg);
                    }
                    if (pNextProg)
                    {
                        // found visible and selectable service with sam mux
                        pCMDB->SetCurr(pNextProg);
                    }
                    else if (pstNonVisibleProg)
                    {
                        /* if current mux has no visible service,
                            change to first nonvisible service with same mux */
                        pCMDB->SetCurr(pstNonVisibleProg);
                    }
                    else
                    {
                        /* if current mux has no selectable and visible service,
                            change to first program */
                        DVB_PROG* pProgDVB = (DVB_PROG *)pCMDB->GetByIndex(0);
                        if (pProgDVB != NULL)
                        {
                            pCMDB->SetCurr(pProgDVB);
                        }
                        else
                        {
                            ASSERT(0);
                        }
                    }
                    _UpdateCurrentProgram();
                    bCurSrvChg = MAPI_TRUE;
                }
                // check if current program is changed to non-selectable, change the other channel to play - END
            }
            MONITOR_NOTIFY(E_DVB_TS_CHANGE, &bCurSrvChg, m_pMonitorNotifyUsrParam, NULL);
        }
    }

    return MAPI_TRUE;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::CompareCIContentByte(MAPI_U8 & u8ContentByte,
MAPI_U8* pu8LabelChar, MAPI_U8 u8LabelLen, MEMBER_LANGUAGE eLanguage)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    MAPI_BOOL bRet = MAPI_FALSE;

    if(pu8LabelChar == NULL)
    {
        return MAPI_FALSE;
    }
    for(int i=0 ; i<MAPI_SI_MAX_CI_CONTENT_LABEL; i++)
    {
        if((m_stContentLabel[i].u8ContentByteMin == 0) && (m_stContentLabel[i].u8ContentByteMax == 0))
        {
            break;
        }
        if((u8ContentByte >= m_stContentLabel[i].u8ContentByteMin) && (u8ContentByte <= m_stContentLabel[i].u8ContentByteMax))
        {
            memset(pu8LabelChar, 0, u8LabelLen);
            memcpy(pu8LabelChar, m_stContentLabel[i].au8LabelChar, (MAX_CIPLUS_LABEL_CHAR > u8LabelLen) ? u8LabelLen : MAX_CIPLUS_LABEL_CHAR);
            bRet = MAPI_TRUE;
            if(eLanguage == mapi_dvb_utility::SI_GetISO639LangCode(m_stContentLabel[i].au8Iso639Lang))
            {
                break;
            }
        }
    }

    return bRet;
}
#endif

void MW_DVB_SI_PSI_Parser::SetDynamicRescanOff(MAPI_BOOL bIsDynamicRescanOff)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    m_bIsSIDynmaicReScanOff= bIsDynamicRescanOff;

}

MAPI_BOOL MW_DVB_SI_PSI_Parser::ParseDsdInfo(const MAPI_U8 *pu8DSD, const MAPI_U8 u8MaxDsdLen, ST_MW_DVB_DSD_INFO &stDsdInfo)
{
    mapi_dvb_dsd DsdInfo;
    mapi_dvb_dsd::ST_DVB_DSD_DATA stDsdData;
    MAPI_BOOL bRet = MAPI_TRUE;
    if (NULL == pu8DSD)
    {
        return MAPI_FALSE;
    }

    if(MAPI_FALSE == DsdInfo.ParseDSD(pu8DSD, u8MaxDsdLen))
    {
        return MAPI_FALSE;
    }
    else
    {
        memset(&stDsdData, 0, sizeof(mapi_dvb_dsd::ST_DVB_DSD_DATA));
        if (MAPI_TRUE == DsdInfo.GetDsdInformation(stDsdData))
        {
            switch(stDsdData.eDsdType)
            {
                case mapi_dvb_dsd::E_DVB_DSD_T:
                    stDsdInfo.eDelSysType = E_DELIVERY_SYS_TDSD;
                    stDsdInfo.u32CenterFreq = stDsdData.DSD.stTDS.u32CentreFreq/100;
                    break;
                case mapi_dvb_dsd::E_DVB_DSD_C:
                    stDsdInfo.eDelSysType = E_DELIVERY_SYS_CDSD;
                    stDsdInfo.u32CenterFreq = stDsdData.DSD.stCDS.u32CentreFreq/10;
                    stDsdInfo.u32SymbolRate = stDsdData.DSD.stCDS.u32Symbol_rate/10;
                    break;
                case mapi_dvb_dsd::E_DVB_DSD_S:
                    stDsdInfo.eDelSysType = E_DELIVERY_SYS_SDSD;
                    stDsdInfo.u32CenterFreq = stDsdData.DSD.stSDS.u32Freq/100;
                    stDsdInfo.u32SymbolRate = stDsdData.DSD.stSDS.u32Symbol_rate/10;
                    stDsdInfo.u8Polarization = ((stDsdData.DSD.stSDS.u8polarization != 0) ? 1 : 0);
                    break;
                default:
                    bRet = MAPI_FALSE;
                    break;
            }
        }
        else
        {
            return MAPI_FALSE;
        }
    }
    return bRet;
}

void MW_DVB_SI_PSI_Parser::_RTCSetCLK(MAPI_U32 u32Seconds, MAPI_BOOL bTimeFromSI)
{
    mapi_system * system = mapi_interface::Get_mapi_system();
    if(system)
    {
        #if (ISDB_SYSTEM_ENABLE == 1)
        if(bTimeFromSI)
        {
            u32Seconds = GET_REAL_UTC_TIME_BY_COUNTRY(m_eCountry, u32Seconds);
        }
        #endif
        MONITOR_NOTIFY(E_DVB_RTC_SET_CLK,NULL,m_pMonitorNotifyUsrParam,&u32Seconds);
    }
}

void MW_DVB_SI_PSI_Parser::_SetClockOffset(MAPI_S32 s32OffsetTime, MAPI_BOOL bOffsetFromSI)
{
    mapi_system * system = mapi_interface::Get_mapi_system();
    if(system)
    {
        #if (ISDB_SYSTEM_ENABLE == 1)
        if(bOffsetFromSI)
        {
            s32OffsetTime = GET_REAL_OFFSET_TIME_BY_COUNTRY(m_eCountry, s32OffsetTime);
        }
        #endif
        system->SetClockOffset(s32OffsetTime);
    }
}

void MW_DVB_SI_PSI_Parser::_SetNextTimeOffset(MAPI_S32 s32OffsetTime, MAPI_BOOL bOffsetFromSI)
{
    mapi_system * system = mapi_interface::Get_mapi_system();
    if(system)
    {
        #if (ISDB_SYSTEM_ENABLE == 1)
        if(bOffsetFromSI)
        {
            s32OffsetTime = GET_REAL_OFFSET_TIME_BY_COUNTRY(m_eCountry, s32OffsetTime);
        }
        #endif
        system->SetNextTimeOffset(s32OffsetTime);
    }
}

void MW_DVB_SI_PSI_Parser::_SetTimeOfChange(MAPI_U32 u32TimeOfChg,  MAPI_BOOL bTimeFromSI)
{
    mapi_system * system = mapi_interface::Get_mapi_system();
    if(system)
    {
        #if (ISDB_SYSTEM_ENABLE == 1)
        if(u32TimeOfChg !=0 && bTimeFromSI)
        {
            u32TimeOfChg = GET_REAL_UTC_TIME_BY_COUNTRY(m_eCountry, u32TimeOfChg);
        }
        #endif
        system->SetTimeOfChange(u32TimeOfChg);
    }
}

void MW_DVB_SI_PSI_Parser:: _SetProgramWithDeaultValue(DVB_PROG &stNewProgInfo)
{
    memset(&stNewProgInfo,0,sizeof(DVB_PROG));

    stNewProgInfo.u16VideoPID = INVALID_PID;
    stNewProgInfo.u16PCRPID = INVALID_PID;
    for(int i = 0; i < MAX_AUD_LANG_NUM; i++)
    {
        stNewProgInfo.stAudInfo[i].u16AudPID = INVALID_PID;
    }
    stNewProgInfo.u16LCN = INVALID_LOGICAL_CHANNEL_NUMBER;
    stNewProgInfo.u16Number = INVALID_LOGICAL_CHANNEL_NUMBER;
    stNewProgInfo.u16PmtPID = INVALID_PID;
    stNewProgInfo.u16SimuLCN = INVALID_SIMULCAST_LOGICAL_CHANNEL_NUMBER;
    stNewProgInfo.enAudioUserLanguage = E_LANGUAGE_MAX;
    stNewProgInfo.enSubtitleUserLanguage = E_LANGUAGE_MAX;
    stNewProgInfo.u8AudioUserMode = DEFAULT_AUDIO_MODE;
    stNewProgInfo.stPSI_SI_Version.u8NITVer = m_stNit.u8Version;
    stNewProgInfo.stPSI_SI_Version.u8PATVer = INVALID_PSI_SI_VERSION;
    stNewProgInfo.stPSI_SI_Version.u8PMTVer = INVALID_PSI_SI_VERSION;
    stNewProgInfo.stPSI_SI_Version.u8SDTVer = INVALID_PSI_SI_VERSION;
    stNewProgInfo.stPSI_SI_Version.u8BATVer = INVALID_PSI_SI_VERSION;

    stNewProgInfo.stCHAttribute.u16SignalStrength = 0xFFFF;
    stNewProgInfo.stCHAttribute.u8Region = DEFAULT_REGION;
    stNewProgInfo.stCHAttribute.u8VisibleServiceFlag = DEFAULT_VISIBLE_SERVICE_FLAG;
    stNewProgInfo.stCHAttribute.u8NumericSelectionFlag = DEFAULT_VISIBLE_SERVICE_FLAG;
    stNewProgInfo.stCHAttribute.u8IsDelete = DEFAULT_IS_DELETED;
    stNewProgInfo.stCHAttribute.u8IsMove = DEFAULT_IS_MOVED;
    stNewProgInfo.stCHAttribute.u8IsReName = DEFAULT_IS_RENAME;
    stNewProgInfo.stCHAttribute.u8IsScramble = DEFAULT_IS_SCRAMBLED;
    stNewProgInfo.stCHAttribute.u8IsSkipped = DEFAULT_IS_SKIPPED;
    stNewProgInfo.stCHAttribute.u8IsLock = DEFAULT_IS_LOCKED;
    stNewProgInfo.stCHAttribute.u8IsStillPicture = DEFAULT_IS_STILL_PICTURE;
    stNewProgInfo.stCHAttribute.u8IsMHEGIncluded = DEFAULT_IS_MHEG_INCLUDED;
    stNewProgInfo.stCHAttribute.u8VideoType = DEFAULT_VIDEO_TYPE;
    stNewProgInfo.stCHAttribute.u8IsServiceIdOnly = DEFAULT_IS_SERVICE_ID_ONLY;
    stNewProgInfo.stCHAttribute.u8IsDataServiceAvailable = DEFAULT_IS_DATA_SERVICE_AVAILABLE;
    stNewProgInfo.stCHAttribute.u8IsReplaceDel = DEFAULT_IS_REPLACE_DEL;;
    stNewProgInfo.stCHAttribute.u8ServiceType = DEFAULT_SERVICE_TYPE;
    stNewProgInfo.stCHAttribute.u8Favorite = DEFAULT_FAVORITE;
    stNewProgInfo.stCHAttribute.u8ServiceTypePrio = DEFAULT_SERVICE_TYPE_PRIO;
    stNewProgInfo.stCHAttribute.u8InvalidCell = MAPI_FALSE;
    stNewProgInfo.stCHAttribute.u8UnconfirmedService = MAPI_FALSE;
    stNewProgInfo.stCHAttribute.u8IsSpecialSrv = DEFAULT_SPECIAL_SRV;
    stNewProgInfo.stCHAttribute.u8RealServiceType = E_TYPE_INVALID;
    stNewProgInfo.u16ServiceID = INVALID_SERVICE_ID;
}


MAPI_BOOL MW_DVB_SI_PSI_Parser::_IsPmtContentDiff(MAPI_SI_TABLE_PMT &stOldPmt, MAPI_SI_TABLE_PMT &stNewPmt)
{
    MAPI_U8 u8BkVersion = stNewPmt.u8Version;
    MAPI_U32 u32BkCrc = stNewPmt.u32CRC32;
    MAPI_BOOL bRet = MAPI_TRUE;
    if ((stOldPmt.u8Version == SI_PSI_FORCE_UPDATE_VER) || (stOldPmt.u8Version == INVALID_PSI_SI_VERSION))
    {
        return bRet;
    }
    stOldPmt.u8Version = INVALID_PSI_SI_VERSION;
    stNewPmt.u8Version = INVALID_PSI_SI_VERSION;
    stOldPmt.u32CRC32 = 0;
    stNewPmt.u32CRC32 = 0;
    if (!memcmp(&stOldPmt, &stNewPmt, sizeof(MAPI_SI_TABLE_PMT)))
    {
        bRet = MAPI_FALSE;
    }
    stNewPmt.u8Version = u8BkVersion;
    stNewPmt.u32CRC32 = u32BkCrc;
    return bRet;
}

void MW_DVB_SI_PSI_Parser::_CollectTargetRegionInfo()
{
    if((m_eParserMode == E_DVB_SCAN) && m_stNit.pastTargetRegionNameInfo)
    {
        MW_SI_TARGET_REGION_DBG("m_TsInfo.u16TSID..........................%x\n", m_TsInfo.u16TSID);
        for(int i = 0; i < m_stSdt.wServiceNumber; i++)
        {
            MAPI_BOOL bGotRegionInfo;
            MAPI_U16 u16Index = m_stAllServiceTargetRegionInfo.u16ServiceNum;
            MW_DVB_SERVICE_REGION_INFO* pOldInfo = m_stAllServiceTargetRegionInfo.pInfo;
            if(m_stSdt.astServiceInfo[i].pstTargetRegionInfo)
            {
                bGotRegionInfo = MAPI_FALSE;
                m_stAllServiceTargetRegionInfo.pInfo = (MW_DVB_SERVICE_REGION_INFO*)malloc(sizeof(MW_DVB_SERVICE_REGION_INFO) * (u16Index + 1));
                if(m_stAllServiceTargetRegionInfo.pInfo == NULL)
                {
                    m_stAllServiceTargetRegionInfo.pInfo = pOldInfo;
                    ASSERT(0);
                }
                else
                {
                    if(pOldInfo)
                    {
                        memcpy(m_stAllServiceTargetRegionInfo.pInfo, pOldInfo, sizeof(MW_DVB_SERVICE_REGION_INFO)*(u16Index));
                    }

                    MW_SI_TARGET_REGION_DBG("m_stNit.u16TSNumber...%d\n", m_stNit.u16TSNumber);
                    m_stAllServiceTargetRegionInfo.u16ServiceNum++;
                    m_stAllServiceTargetRegionInfo.pInfo[u16Index].u16ONID = m_TsInfo.u16ONID;
                    m_stAllServiceTargetRegionInfo.pInfo[u16Index].u16TSID = m_TsInfo.u16TSID;
                    m_stAllServiceTargetRegionInfo.pInfo[u16Index].u16SID = m_stSdt.astServiceInfo[i].u16ServiceID;
                    m_stAllServiceTargetRegionInfo.pInfo[u16Index].stInfo.u8CountryNum = 0;
                    m_stAllServiceTargetRegionInfo.pInfo[u16Index].stInfo.pCountryInfo = NULL;
                    //check SDT
                    MW_SI_TARGET_REGION_DBG(".....................................add SDT region info\n");


                    if(m_stSdt.astServiceInfo[i].pstTargetRegionInfo->u8country_num)
                    {
                        bGotRegionInfo = MAPI_TRUE;
                    }

                    _AddTargetRegionInfo(m_stAllServiceTargetRegionInfo.pInfo[u16Index].stInfo, m_stSdt.astServiceInfo[i].pstTargetRegionInfo);

                    MW_SI_TARGET_REGION_DBG(".........................................add region info end\n");
                    if(bGotRegionInfo)
                    {
                        MW_SI_TARGET_REGION_DBG("got region info\n");
                        FREE(pOldInfo);
                    }
                    else
                    {
                        MW_SI_TARGET_REGION_DBG("no got region info\n");
                        FREE(m_stAllServiceTargetRegionInfo.pInfo);
                        m_stAllServiceTargetRegionInfo.pInfo = pOldInfo;
                        m_stAllServiceTargetRegionInfo.u16ServiceNum--;
                    }
                }
            }
        }

        MAPI_BOOL bGotRegionInfo = MAPI_FALSE;
        MAPI_U16 u16Index = m_stAllServiceTargetRegionInfo.u16ServiceNum;
        MW_DVB_SERVICE_REGION_INFO* pOldInfo = m_stAllServiceTargetRegionInfo.pInfo;

        m_stAllServiceTargetRegionInfo.pInfo = (MW_DVB_SERVICE_REGION_INFO*)malloc(sizeof(MW_DVB_SERVICE_REGION_INFO) * (u16Index + 1));
        if(m_stAllServiceTargetRegionInfo.pInfo == NULL)
        {
            m_stAllServiceTargetRegionInfo.pInfo = pOldInfo;
            ASSERT(0);
        }
        else
        {
            if(pOldInfo)
            {
                memcpy(m_stAllServiceTargetRegionInfo.pInfo, pOldInfo, sizeof(MW_DVB_SERVICE_REGION_INFO)*(u16Index));
            }

            MW_SI_TARGET_REGION_DBG("m_stNit.u16TSNumber...%d\n", m_stNit.u16TSNumber);
            m_stAllServiceTargetRegionInfo.u16ServiceNum++;
            m_stAllServiceTargetRegionInfo.pInfo[u16Index].u16ONID = m_TsInfo.u16ONID;
            m_stAllServiceTargetRegionInfo.pInfo[u16Index].u16TSID = m_TsInfo.u16TSID;
            m_stAllServiceTargetRegionInfo.pInfo[u16Index].u16SID = 0;
            m_stAllServiceTargetRegionInfo.pInfo[u16Index].stInfo.u8CountryNum = 0;
            m_stAllServiceTargetRegionInfo.pInfo[u16Index].stInfo.pCountryInfo = NULL;

            //check NIT
            MW_SI_TARGET_REGION_DBG(".........................................add NIT region info\n");
            for(int i = 0; i < m_stNit.u16TSNumber; i++)
            {
                if(m_stNit.pstTSInfo[i].pstTargetRegionInfo)
                {
                    MW_SI_TARGET_REGION_DBG("NIT tsid...%x %x....%d\n", m_TsInfo.u16TSID, m_stNit.pstTSInfo[i].wTransportStream_ID, m_stNit.pstTSInfo[i].pstTargetRegionInfo->u8country_num);
                    if(m_TsInfo.u16TSID == m_stNit.pstTSInfo[i].wTransportStream_ID)
                    {
                        MW_SI_TARGET_REGION_DBG("NIT u8country_num...%d\n", m_stNit.pstTSInfo[i].pstTargetRegionInfo->u8country_num);

                        if(m_stNit.pstTSInfo[i].pstTargetRegionInfo->u8country_num)
                        {
                            bGotRegionInfo = MAPI_TRUE;
                        }

                        _AddTargetRegionInfo(m_stAllServiceTargetRegionInfo.pInfo[u16Index].stInfo, m_stNit.pstTSInfo[i].pstTargetRegionInfo);
                        break;
                    }
                }
            }
            MW_SI_TARGET_REGION_DBG(".........................................add region info end\n");
            if(bGotRegionInfo)
            {
                MW_SI_TARGET_REGION_DBG("got region info\n");
                FREE(pOldInfo);
            }
            else
            {
                MW_SI_TARGET_REGION_DBG("no got region info\n");
                FREE(m_stAllServiceTargetRegionInfo.pInfo);
                m_stAllServiceTargetRegionInfo.pInfo = pOldInfo;
                m_stAllServiceTargetRegionInfo.u16ServiceNum--;
            }
        }

        //add target region name info
        for(int i = 0; i < m_stNit.u16TSNumber; i++)
        {
            if(m_stNit.pstTSInfo[i].pstTargetRegionInfo)
            {
                MW_SI_TARGET_REGION_DBG("tsid...%x %x....%d\n", m_TsInfo.u16TSID, m_stNit.pstTSInfo[i].wTransportStream_ID, m_stNit.pstTSInfo[i].pstTargetRegionInfo->u8country_num);
                if(m_TsInfo.u16TSID == m_stNit.pstTSInfo[i].wTransportStream_ID)
                {
                    MW_SI_TARGET_REGION_DBG("u8country_num...%d\n", m_stNit.pstTSInfo[i].pstTargetRegionInfo->u8country_num);
                    _AddTargetRegionInfo(m_stTargetRegionInfo, m_stNit.pstTSInfo[i].pstTargetRegionInfo);
                    break;
                }
            }
        }

        // collect sdt level target region
        for(int i = 0; i < m_stSdt.wServiceNumber; i++)
        {
            if(m_stSdt.astServiceInfo[i].pstTargetRegionInfo)
            {
                _AddTargetRegionInfo(m_stTargetRegionInfo, m_stSdt.astServiceInfo[i].pstTargetRegionInfo);
            }
        }
    }
}
#if (DVBS_SYSTEM_ENABLE == 1)
MAPI_BOOL MW_DVB_SI_PSI_Parser::_CheckSatelliteAngleToAutoUpdate(DVB_SAT* pSat)
{
    if(pSat==NULL)
    {
        return FALSE;
    }

    for(MAPI_U8 i=0; i<sizeof(AutoUpdateSatellite)/sizeof(MAPI_U16); i++)
    {
        if(AutoUpdateSatellite[i]== pSat->u16Angle)
        {
                return TRUE;
        }
    }
    return FALSE;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::_CheckNITAutoUpdateForDVBS(DVB_PROG *pCurProg, DVB_CM *pCMDB, DVB_MUX *pMux, DVB_NETWORK *pNetwork)
{
    MAPI_U16 u16NitTsPos, u16CmdbTsPos, u16NitSrvPos, u16CmdbSrvPos;
    MAPI_U16 u16TSID, u16SID;
    DESC_SATELLITE_DEL_SYS *pstSatelliteSys;
    MAPI_BOOL bCurSrvChg = MAPI_FALSE;
    DVB_MUX *pstMuxInfo;
    DVB_PROG *pstProgInfo;
    MAPI_BOOL bIsDbChg = MAPI_FALSE;
    MAPI_BOOL bIsNeedtoRescan =MAPI_FALSE;

    MW_SI_NIT_AUTOUPDATE_DBG("%s %s %d\n",__FILE__,__FUNCTION__,__LINE__);
    if (m_stNit.u8Version == INVALID_PSI_SI_VERSION)
    {
        MW_SI_NIT_AUTOUPDATE_DBG("%s %d>>Invalid NIT version\n",__FUNCTION__,__LINE__);
        return FALSE;
    }

    if (m_eParserType != MW_DVB_S_PARSER)
    {
        MW_SI_NIT_AUTOUPDATE_DBG("%s %d>>It is not dvb-s system\n",__FUNCTION__,__LINE__);
        return FALSE;
    }

    if(_CheckSatelliteAngleToAutoUpdate((DVB_SAT*)m_pCurSat) == FALSE)
    {
        return FALSE;
    }

    if( m_bIsSIAutoUpdateOff== MAPI_TRUE)
    {
        MW_SI_NIT_AUTOUPDATE_DBG("%s %d>>CableReady Manual scan already.Nit auto update off.\n",__FUNCTION__,__LINE__);
        return FALSE;
    }

    m_bNitUpdate2Rescan = MAPI_FALSE;
    mapi_utility::freeList(&m_pSatelliteDeliveryInfo);
    _BuildDeliveryInfo(m_stNit, FALSE);
    m_DvbsScanTpMap.clear();
    MW_DTV_CM_DB_scope_lock lock(pCMDB);

    // check transponder/service addition
    MW_SI_NIT_AUTOUPDATE_DBG("%s %s TS Number %d\n",__FILE__,__FUNCTION__,m_stNit.u16TSNumber);
    for (u16NitTsPos = 0; u16NitTsPos < m_stNit.u16TSNumber; u16NitTsPos++)
    {
        u16TSID = m_stNit.pstTSInfo[u16NitTsPos].wTransportStream_ID;
        MW_SI_NIT_AUTOUPDATE_DBG(" TSID %d\n",u16TSID);
        pstSatelliteSys= _FindSatelliteParambyTSID(u16TSID);

        for (u16CmdbTsPos=0; u16CmdbTsPos<pCMDB->GetMaxMuxNum(); u16CmdbTsPos++)
        {
            pstMuxInfo = (DVB_MUX *)pCMDB->GetValidMux(u16CmdbTsPos);

            if (pstMuxInfo)
            {
                if (u16TSID == pstMuxInfo->u16TransportStream_ID)
                {
                    if (pstSatelliteSys != NULL)
                    {
                        MAPI_BOOL bMUXupdate=MAPI_FALSE;

                        if ((pstSatelliteSys->u32Freq/100) != pstMuxInfo->u32Frequency)
                        {
                            MW_SI_NIT_AUTOUPDATE_DBG("%s %d>>Satellite param chg: Freq[%d %d]\n",
                                        __FUNCTION__, __LINE__, pstMuxInfo->u32Frequency, pstSatelliteSys->u32Freq/100);
                            pstMuxInfo->u32Frequency =pstSatelliteSys->u32Freq/100;
                            bMUXupdate=MAPI_TRUE;
                        }

                        if((pstSatelliteSys->u32Symbol_rate/10) != pstMuxInfo->u32SymbRate)
                        {
                            MW_SI_NIT_AUTOUPDATE_DBG("%s %d>>Satellite param chg: Sym:[%d %d]\n",
                                        __FUNCTION__, __LINE__, pstMuxInfo->u32SymbRate, pstSatelliteSys->u32Symbol_rate/10);
                            pstMuxInfo->u32SymbRate = pstSatelliteSys->u32Symbol_rate/10;
                            bMUXupdate=MAPI_TRUE;
                        }

                        if((pstSatelliteSys->u8Modulation_type) != pstMuxInfo->u8ModulationMode)
                        {
                            MW_SI_NIT_AUTOUPDATE_DBG("%s %d>>Satellite param chg: Mod:[%d %d]\n",
                                        __FUNCTION__, __LINE__, pstMuxInfo->u8ModulationMode, pstSatelliteSys->u8Modulation_type);
                            pstMuxInfo->u8ModulationMode = pstSatelliteSys->u8Modulation_type;
                            bMUXupdate=MAPI_TRUE;
                        }

                        if(pstSatelliteSys->u8polarization != pstMuxInfo->bPolarity)
                        {
                            MW_SI_NIT_AUTOUPDATE_DBG("%s %d>>Satellite param chg: Polarity:[%d %d]\n",
                                        __FUNCTION__, __LINE__, pstMuxInfo->bPolarity, pstSatelliteSys->u8polarization);
                            pstMuxInfo->bPolarity =pstSatelliteSys->u8polarization;
                            bMUXupdate=MAPI_TRUE;
                        }

                        if(bMUXupdate)
                        {
                            // Satellite parameters change
                            if (__GetMux(pCurProg, pCMDB) == pstMuxInfo)
                            {
                                // need re-tune
                                MW_SI_NIT_AUTOUPDATE_DBG("%s %d>>CDC change>>Return service\n", __FUNCTION__, __LINE__);
                                bCurSrvChg = MAPI_TRUE;
                                bIsDbChg = MAPI_TRUE;
                            }
                            pCMDB->UpdateMux(pstMuxInfo);
                        }
                    }

                    MW_SI_NIT_AUTOUPDATE_DBG("%s %s the number Services %d\n ",__FILE__,__FUNCTION__,m_stNit.pstTSInfo[u16NitTsPos].u16ServiceNum);

                    for (u16NitSrvPos = 0; u16NitSrvPos < m_stNit.pstTSInfo[u16NitTsPos].u16ServiceNum; u16NitSrvPos++)
                    {
                        u16SID = m_stNit.pstTSInfo[u16NitTsPos].astServiceInfo[u16NitSrvPos].u16ServiceID;
                        MW_SI_NIT_AUTOUPDATE_DBG("%s %s Service ID %d\n",__FILE__,__FUNCTION__,u16SID);
                        for (u16CmdbSrvPos = 0; u16CmdbSrvPos < pCMDB->Size(); u16CmdbSrvPos++)
                        {
                            pstProgInfo = pCMDB->GetByIndex(u16CmdbSrvPos);
                            if (pstProgInfo)
                            {
                                if ((u16SID == pstProgInfo->u16ServiceID)&&(_IsCurSatelliteProgram(pstProgInfo,pCMDB,pMux)==MAPI_TRUE))
                                {
                                    break;
                                }
                            }
                        }

                        if (u16CmdbSrvPos >= pCMDB->Size())
                        {
                            if(pstSatelliteSys != NULL)
                            {
                                if(MAPI_TRUE == _AddDvbsScanTp(u16TSID, *pstSatelliteSys))
                                {
                                     bIsNeedtoRescan = MAPI_TRUE;
                                }
                                MW_SI_NIT_AUTOUPDATE_DBG("new Service ID %d\n",u16SID);
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }

        if(m_eParserBaseType == MW_DVB_SGT_BASE)
        {
            continue;
        }

        if (u16CmdbTsPos >= pCMDB->GetMaxMuxNum())
        {
            // new transponder
            if(pstSatelliteSys != NULL)
            {
                if(MAPI_TRUE == _AddDvbsScanTp(u16TSID, *pstSatelliteSys))
                {
                     bIsNeedtoRescan = MAPI_TRUE;
                }
                MW_SI_NIT_AUTOUPDATE_DBG("new transponder %d\n",u16TSID);
            }
        }
    }

    if(m_eParserBaseType == MW_DVB_SGT_BASE)
    {
        return MAPI_FALSE;
    }

    // check transponder/service removal
    for (u16CmdbTsPos=0; u16CmdbTsPos<pCMDB->GetMaxMuxNum(); u16CmdbTsPos++)
    {
        pstMuxInfo = pCMDB->GetValidMux(u16CmdbTsPos);

        if (pstMuxInfo)
        {
            for (u16NitTsPos = 0; u16NitTsPos < m_stNit.u16TSNumber; u16NitTsPos++)
            {
                if (m_stNit.pstTSInfo[u16NitTsPos].wTransportStream_ID == pstMuxInfo->u16TransportStream_ID)
                {
                    break;
                }
            }

            if (u16NitTsPos >= m_stNit.u16TSNumber)
            {
                // remove transponder
                if (NULL != m_pSatelliteDeliveryInfo)
                {
                    MW_SI_NIT_AUTOUPDATE_DBG("%s %d>>Remove Mux: TSID:0x%x; Freq:%d\n",
                                            __FUNCTION__, __LINE__, pstMuxInfo->u16TransportStream_ID, pstMuxInfo->u32Frequency);

                    if (pstMuxInfo == m_pCurMux)
                    {
                        bCurSrvChg = MAPI_TRUE;
                    }

                    _ClearSdtOther(pstMuxInfo->u16TransportStream_ID);

                    DESC_SATELLITE_DEL_SYS stSatelliteSys;
                    memset(&stSatelliteSys, 0, sizeof(DESC_SATELLITE_DEL_SYS));
                    stSatelliteSys.u32Freq = pstMuxInfo->u32Frequency;
                    stSatelliteSys.u32Symbol_rate = pstMuxInfo->u32SymbRate;
                    stSatelliteSys.u8Modulation_type = pstMuxInfo->u8ModulationMode;
                    stSatelliteSys.u8polarization = pstMuxInfo->bPolarity;

                    if(MAPI_TRUE == _AddDvbsScanTp(pstMuxInfo->u16TransportStream_ID, stSatelliteSys))
                    {
                         bIsNeedtoRescan = MAPI_TRUE;
                    }
                    MW_SI_NIT_AUTOUPDATE_DBG("======Transponder remove need to rescan=====\n");
                }
            }
        }
    }

    if(bIsNeedtoRescan == MAPI_TRUE)
    {
        MW_SI_NIT_AUTOUPDATE_DBG("Start Rescan**************\n");
        MONITOR_NOTIFY(E_DVB_NIT_AUTO_UPDATE_SCAN, NULL, m_pMonitorNotifyUsrParam, NULL);
        m_bNitUpdate2Rescan = MAPI_TRUE;
        return TRUE;
    }

    if (bIsDbChg)
    {
        if(pCMDB->Size())
        {
            pCMDB->ReArrangeNumber();
            _UpdateCurrentProgram();
        }
        MONITOR_NOTIFY(E_DVB_TS_CHANGE, &bCurSrvChg, m_pMonitorNotifyUsrParam, NULL);
    }

    return TRUE;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::_AddDvbsScanTp(MAPI_U16 u16TSID,DESC_SATELLITE_DEL_SYS &stSatelliteSys)
{
    ST_MW_DVBS_SCAN_PARAM stScan;

    std::pair< std::map<MAPI_U16,ST_MW_DVBS_SCAN_PARAM>::iterator , MAPI_BOOL> ret ;

    stScan.u32Symbol_rate=stSatelliteSys.u32Symbol_rate;
    stScan.u32Freq =stSatelliteSys.u32Freq;
    stScan.u8Modulation_type=stSatelliteSys.u8Modulation_type;
    stScan.u8polarization=stSatelliteSys.u8polarization;

    ret=m_DvbsScanTpMap.insert(Dvbs_ScanTp_t::value_type(u16TSID,stScan));
    return ret.second;
}


MAPI_BOOL MW_DVB_SI_PSI_Parser::_IsCurSatelliteProgram(DVB_PROG *pProg,DVB_CM *pCM,DVB_MUX *pMux)
{
    if(pProg==NULL)
    {
        return MAPI_FALSE;
    }
    DVB_MUX *pMuxDB =pCM->GetMux(pProg->m_u16MuxTableID);
    if(pMuxDB ==NULL)
    {
        return FALSE;
    }
    DVB_SAT *pSatDB = pCM->GetSat(pMuxDB->m_u16SatTableID);
    DVB_SAT *pSat=pCM->GetSat(pMux->m_u16SatTableID);

    if((pSatDB == NULL)||(pSat==NULL))
    {
        return FALSE;
    }

    if(pSatDB->u16Angle ==pSat->u16Angle)
    {
        return TRUE;
    }

    return FALSE;
}
MAPI_BOOL MW_DVB_SI_PSI_Parser::GetScanTpParam(MAPI_U16 &u16TSID,ST_MW_DVBS_SCAN_PARAM &stScanParam)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    Dvbs_ScanTp_t::iterator it;
    memset(&stScanParam,0,sizeof(ST_MW_DVBS_SCAN_PARAM));
    if(m_DvbsScanTpMap.size()==0)
    {
        return MAPI_FALSE;
    }
    if(u16TSID == INVALID_TS_ID)
    {
        it = m_DvbsScanTpMap.begin();
    }
    else
    {
        it= m_DvbsScanTpMap.find(u16TSID);
        if(it==m_DvbsScanTpMap.end())
        {
            return FALSE;
        }
        else
        {
            it++;
            if(it==m_DvbsScanTpMap.end())
            {
                return FALSE;
            }

        }
    }
    u16TSID =it->first;
    MW_SI_NIT_AUTOUPDATE_DBG("%s TSID %x \n",__FUNCTION__,u16TSID);
    memcpy(&stScanParam,&it->second,sizeof(ST_MW_DVBS_SCAN_PARAM));
    MW_SI_NIT_AUTOUPDATE_DBG("%s== Freq %d Symbol Rate %d====\n",__FUNCTION__,stScanParam.u32Freq,stScanParam.u32Symbol_rate);
    return TRUE;

}
MAPI_U16 MW_DVB_SI_PSI_Parser:: GetScanTpSize(void)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    return m_DvbsScanTpMap.size();
}
void MW_DVB_SI_PSI_Parser::_CreatePmtFilterBySrvID(MAPI_U16 u16ServiceId)
{
    if((E_DVB_MONITOR == m_eParserMode)&&(m_eParserBaseType!=MW_DVB_SGT_BASE))
    {
        return ;
    }

    for(int j = 0; j < MAX_CHANNEL_IN_MUX; j++)
    {
        if(m_stPat.ServiceIDInfo[j].u16ServiceID)
        {

            if(m_stPat.ServiceIDInfo[j].u16ServiceID!=u16ServiceId)
            {
                continue;
            }

            if(m_pAllPmtParser[j])
            {
                _DeleteParser((MAPI_U32)m_pAllPmtParser[j] , MAPI_FALSE);
                m_aPMTWaitFilter[j] = MAPI_FALSE;
            }
            m_pAllPmtParser[j] = new (std::nothrow) mapi_si_PMT_parser(m_pSi, m_pDemux);
            if(m_pAllPmtParser[j])
            {
                if(m_pAllPmtParser[j]->Init(0x400, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo, EN_SI_PSI_PARSER_NORMAL, INVALID_PSI_SI_VERSION)
                        && m_pAllPmtParser[j]->Start(PMT_SCAN_TIMEOUT, m_stPat.ServiceIDInfo[j].u16ServiceID, m_stPat.ServiceIDInfo[j].u16PmtPID))
                {
                    m_s16OpenFilter++;

                }
                else
                {
                    delete m_pAllPmtParser[j];
                    m_pAllPmtParser[j] = NULL;
                    m_aPMTWaitFilter[j] = MAPI_TRUE;
                }
            }
            else
            {
                m_aPMTWaitFilter[j] = MAPI_TRUE;
            }

        }
    }

}
MAPI_BOOL MW_DVB_SI_PSI_Parser:: _CreatePATNITParser(void)
{
    MAPI_BOOL bRet = MAPI_FALSE;
    if(m_pPatParser !=NULL)
    {
        _DeleteParser((MAPI_U32)m_pPatParser,MAPI_FALSE);
    }

    m_pPatParser = new (std::nothrow) mapi_si_PAT_parser(m_pSi, m_pDemux);
    if(m_pPatParser)
    {
        if(m_pPatParser->Init(0x2000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_NORMAL, INVALID_PSI_SI_VERSION)
                && m_pPatParser->Start(PAT_SCAN_TIMEOUT))
        {
            m_s16OpenFilter++;
            bRet = MAPI_TRUE;
        }
        else
        {
            delete m_pPatParser;
            m_pPatParser = NULL;

            ASSERT(0);
            return MAPI_FALSE;
        }
    }
    else
    {
        ASSERT(0);
        return MAPI_FALSE;
    }

    if(m_pNitParser !=NULL)
    {
        _DeleteParser((MAPI_U32)m_pNitParser,MAPI_FALSE);
    }

    m_pNitParser = new (std::nothrow) mapi_si_NIT_parser(m_pSi, m_pDemux);
    if(m_pNitParser)
    {
        if(m_pNitParser->Init(0x2000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_NORMAL, INVALID_PSI_SI_VERSION)
                && m_pNitParser->Start(NIT_SCAN_TIMEOUT, m_bEnableNetworkFilter && (m_eParserType != MW_DVB_T_PARSER), m_u16NetworkID))
        {
            m_s16OpenFilter++;
            bRet = MAPI_TRUE;
        }
        else
        {
            delete m_pNitParser;
            m_pNitParser = NULL;
            delete m_pPatParser;
            m_pPatParser = NULL;
            ASSERT(0);
            return MAPI_FALSE;
        }
    }
    else
    {
        delete m_pPatParser;
        m_pPatParser = NULL;
        ASSERT(0);
        return MAPI_FALSE;
    }
    return bRet;
}
MAPI_BOOL MW_DVB_SI_PSI_Parser::GetNextScanTp(ST_MW_DVBS_SCAN_PARAM & stDvbsScanParam)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    memset(&stDvbsScanParam,0,sizeof(ST_MW_DVBS_SCAN_PARAM));

    while(memcmp(&m_NextTpFromNit,&stDvbsScanParam,sizeof(ST_MW_DVBS_SCAN_PARAM))==0)
    {
        if((m_eParserBaseType == MW_DVB_SGT_BASE)&&(_IsMoreTpAvailable(E_LINKAGE_SGT)==E_LINKAGE_NONE))
        {
            return MAPI_FALSE;
        }
        else if((m_eParserBaseType != MW_DVB_SGT_BASE)&&(_IsMoreTpAvailable(E_LINKAGE_NIT)==E_LINKAGE_NONE))
        {
            return MAPI_FALSE;
        }
    }
    memcpy(&stDvbsScanParam,&m_NextTpFromNit.stMuxScanParam,sizeof(ST_MW_DVBS_SCAN_PARAM));

    memset(&m_NextTpFromNit,0,sizeof(ST_MW_NIT_LINKAGEINFO));

    MW_SI_SGT_SCAN_DEBUG("%s: Freq Target %d Source %d\n",__FUNCTION__,stDvbsScanParam.u32Freq,m_NextTpFromNit.stMuxScanParam.u32Freq);
    MW_SI_SGT_SCAN_DEBUG("%s: Symb Target %d Source %d\n",__FUNCTION__,stDvbsScanParam.u32Symbol_rate,m_NextTpFromNit.stMuxScanParam.u32Symbol_rate);
    MW_SI_SGT_SCAN_DEBUG("%s: Modu Target %d Source %d\n",__FUNCTION__,stDvbsScanParam.u8Modulation_type,m_NextTpFromNit.stMuxScanParam.u8Modulation_type);
    MW_SI_SGT_SCAN_DEBUG("%s: Pola Target %d Source %d\n",__FUNCTION__,stDvbsScanParam.u8polarization,m_NextTpFromNit.stMuxScanParam.u8polarization);

    return MAPI_TRUE;
}
EN_LINKAGE_TYPE MW_DVB_SI_PSI_Parser:: _BuildFullLinkage(EN_LINKAGE_TYPE eLinkageType)
{
    SATELLITE_DESC_DEL_SYS_DATA *pSatelliteInfo = m_pSatelliteDeliveryInfo;
    MAPI_BOOL bIsCurBarkerTp =MAPI_FALSE;
    EN_LINKAGE_TYPE eTypeRet = E_LINKAGE_NONE;

    if(pSatelliteInfo == NULL)
    {
        return eTypeRet;
    }

    if(eLinkageType == E_LINKAGE_NIT)
    {
        while(pSatelliteInfo!=NULL)
        {
            for(NIT_LINKAGE_t::iterator it=m_NitLinkageInfo.begin();it!=m_NitLinkageInfo.end();++it)
            {
                if(it->stTargetCh.u16TsId == pSatelliteInfo->u16TSID)
                {
                    it->stMuxScanParam.u32Freq =pSatelliteInfo->stSDS.u32Freq;
                    it->stMuxScanParam.u32Symbol_rate=pSatelliteInfo->stSDS.u32Symbol_rate;
                    it->stMuxScanParam.u8Modulation_type = pSatelliteInfo->stSDS.u8Modulation_type;
                    it->stMuxScanParam.u8polarization = pSatelliteInfo->stSDS.u8polarization;
                    if(pSatelliteInfo->u16TSID == m_stPat.u16TsId)
                    {
                        bIsCurBarkerTp =MAPI_TRUE;
                        it->bIsMatchCurTs =MAPI_TRUE;
                        it->bIsUsed = MAPI_TRUE;
                        if(m_eParserBaseType != MW_DVB_SGT_BASE)
                        {
                            eTypeRet = E_LINKAGE_BARKER_CUR;
                        }
                    }
                    if(eTypeRet!=E_LINKAGE_BARKER_CUR)
                    {
                        eTypeRet = E_LINKAGE_NIT;
                    }
                    break;
                }
            }
            pSatelliteInfo=pSatelliteInfo->next;
        }
    }

#if (ASTRA_SGT_ENABLE == 1)
    pSatelliteInfo = m_pSatelliteDeliveryInfo;

    if((bIsCurBarkerTp == MAPI_FALSE)&&(eLinkageType == E_LINKAGE_NIT))
    {
        m_SGTLinkageInfo.clear();
    }

    if((m_eParserBaseType == MW_DVB_SGT_BASE)&&(((bIsCurBarkerTp ==MAPI_TRUE)&&(eLinkageType == E_LINKAGE_NIT))||(eLinkageType == E_LINKAGE_SGT)))
    {
        while(pSatelliteInfo!=NULL)
        {
            for(NIT_LINKAGE_t::iterator it=m_SGTLinkageInfo.begin();it!=m_SGTLinkageInfo.end();++it)
            {
                if(it->stTargetCh.u16TsId == pSatelliteInfo->u16TSID)
                {
                    it->stMuxScanParam.u32Freq =pSatelliteInfo->stSDS.u32Freq;
                    it->stMuxScanParam.u32Symbol_rate=pSatelliteInfo->stSDS.u32Symbol_rate;
                    it->stMuxScanParam.u8Modulation_type = pSatelliteInfo->stSDS.u8Modulation_type;
                    it->stMuxScanParam.u8polarization = pSatelliteInfo->stSDS.u8polarization;

                    if(pSatelliteInfo->u16TSID == m_stPat.u16TsId)
                    {
                        it->bIsMatchCurTs =MAPI_TRUE;
                        it->bIsUsed = MAPI_TRUE;
                        eTypeRet =E_LINKAGE_SGT_CUR;
                    }
                    else if(eTypeRet!=E_LINKAGE_SGT_CUR)
                    {
                        eTypeRet = E_LINKAGE_SGT;
                    }
                    break;
                }
            }
            pSatelliteInfo=pSatelliteInfo->next;
        }
    }

#endif

    return eTypeRet;
}
MAPI_BOOL MW_DVB_SI_PSI_Parser:: _CatLinkageInfo(EN_LINKAGE_TYPE eLinkageType)
{
    MAPI_BOOL bRet = MAPI_FALSE;

    if(m_stNit.pNitLinkageInfo == NULL)
    {
        MW_SI_SGT_SCAN_DEBUG("%s None Linkage Descriptors \n",__FUNCTION__);
        return MAPI_FALSE;
    }

    if(eLinkageType == E_LINKAGE_NIT)
    {
        m_NitLinkageInfo.clear();
        MW_SI_SGT_SCAN_DEBUG("%s size of NIT %d  %d\n",__FUNCTION__,m_NitLinkageInfo.size(),m_stNit.pNitLinkageInfo->size());
        for(LinkageInfo_t::iterator it=m_stNit.pNitLinkageInfo->begin();it!=m_stNit.pNitLinkageInfo->end();++it)
        {
            if((*it).enLinkageType == EN_SI_LINKAGE_TS_CONTAIN_COMPLETE_NETWORK)
            {
                ST_MW_NIT_LINKAGEINFO stSgtLinkageInfo;
                _CovertNitLdToTp(stSgtLinkageInfo, (*it));
                m_NitLinkageInfo.push_back(stSgtLinkageInfo);
                bRet = MAPI_TRUE;
            }
        }
        ///MW_SI_SGT_SCAN_DEBUG("%s size of Linkage type 0x04 %d  the size of Linkage descriptors %d\n",__FUNCTION__,m_NitLinkageInfo.size(),m_stNit.pNitLinkageInfo->size());
   }

#if (ASTRA_SGT_ENABLE == 1)
   if((m_eParserBaseType == MW_DVB_SGT_BASE)&&((eLinkageType == E_LINKAGE_SGT)||(eLinkageType == E_LINKAGE_NIT)))
   {
        m_SGTLinkageInfo.clear();
        for(LinkageInfo_t::iterator it=m_stNit.pNitLinkageInfo->begin();it!=m_stNit.pNitLinkageInfo->end();++it)
        {
            if(((*it).enLinkageType == MAPI_SI_LINKAGE_HD_PLUS_LCN)||
                ((*it).enLinkageType == MAPI_SI_LINKAGE_AUSTRIA_LCN)||
                ((*it).enLinkageType == MAPI_SI_LINKAGE_INTERNATIONAL))
            {
                ST_MW_NIT_LINKAGEINFO stSgtLinkageInfo;
                _CovertNitLdToTp(stSgtLinkageInfo, (*it));
                m_SGTLinkageInfo.push_back(stSgtLinkageInfo);
                bRet = MAPI_TRUE;
            }
        }
        ///MW_SI_SGT_SCAN_DEBUG("%s size of Linkage type SGT  %d thes size of Linkage descriptors %d\n",__FUNCTION__,m_SGTLinkageInfo.size(),m_stNit.pNitLinkageInfo->size());
    }
#endif
    return bRet;
}
void MW_DVB_SI_PSI_Parser:: _CovertNitLdToTp(ST_MW_NIT_LINKAGEINFO &stLinkagetp,const MAPI_SI_DESC_LINKAGE_INFO stLinkageNit)
{
    stLinkagetp.stTargetCh.u16TsId = stLinkageNit.u16TSId;
    stLinkagetp.stTargetCh.u16OnId=stLinkageNit.u16ONId;
    stLinkagetp.stTargetCh.u16SrvId=stLinkageNit.u16ServiceId;
    stLinkagetp.bIsMatchCurTs =MAPI_FALSE;
    stLinkagetp.bIsUsed = MAPI_FALSE;
    memset(&stLinkagetp.stMuxScanParam,0,sizeof(stLinkagetp.stMuxScanParam));
}
EN_LINKAGE_TYPE MW_DVB_SI_PSI_Parser::_IsMoreTpAvailable(EN_LINKAGE_TYPE eLinkageType)
{
    MAPI_BOOL bCheckNitAlso= MAPI_FALSE;
    EN_LINKAGE_TYPE bRetType = E_LINKAGE_NONE;

    memset(&m_NextTpFromNit,0,sizeof(m_NextTpFromNit));
#if (ASTRA_SGT_ENABLE == 1)
    m_enSgtScanState = E_SGT_SCAN_STATE_IDLE;

    if(eLinkageType == E_LINKAGE_SGT)
    {
        NIT_LINKAGE_t::iterator it=m_SGTLinkageInfo.begin();

        for(;it!=m_SGTLinkageInfo.end();++it)
        {
            if(it->bIsUsed == MAPI_FALSE)
            {
                it->bIsUsed = MAPI_TRUE;
                bRetType = E_LINKAGE_SGT;

                m_enSgtScanState = E_SGT_SCAN_STATE_CREATE_PAT_NIT;
                m_enSgtSource = E_SGT_TP_SGT_LD;
                MW_SI_SGT_SCAN_DEBUG("%s: linkage type is SGT \n",__FUNCTION__);
                memcpy(&m_NextTpFromNit,&(*it),sizeof(m_NextTpFromNit));

                if(_pfScanResultNotify)
                {
                    _pfScanResultNotify(E_DVB_SCAN_TP_GET, m_u32ScanResultNotifyParam1, NULL, NULL);
                }
                break;
            }
        }
        if(it==m_SGTLinkageInfo.end())
        {
            bCheckNitAlso = MAPI_TRUE;
            m_SGTLinkageInfo.clear();
        }

    }
#endif
    if(((bCheckNitAlso ==MAPI_TRUE)&&(eLinkageType == E_LINKAGE_SGT))||(eLinkageType == E_LINKAGE_NIT))
    {
       NIT_LINKAGE_t::iterator it=m_NitLinkageInfo.begin();

        for(;it!=m_NitLinkageInfo.end();++it)
        {
            if(it->bIsUsed == MAPI_FALSE)
            {
                it->bIsUsed = MAPI_TRUE;
                bRetType = E_LINKAGE_NIT;
#if (ASTRA_SGT_ENABLE == 1)
                if(m_eParserBaseType == MW_DVB_SGT_BASE)
                {
                    m_enSgtSource = E_SGT_TP_NIT_LD;
                    m_enSgtScanState = E_SGT_SCAN_STATE_CREATE_PAT_NIT;
                }
#endif
                MW_SI_SGT_SCAN_DEBUG("%s: linkage type is 0x04 \n",__FUNCTION__);
                memcpy(&m_NextTpFromNit,&(*it),sizeof(m_NextTpFromNit));
                if((m_eParserBaseType == MW_DVB_SGT_BASE)&&(_pfScanResultNotify))
                {
                    _pfScanResultNotify(E_DVB_SCAN_TP_GET, m_u32ScanResultNotifyParam1, NULL, NULL);
                }
                break;
            }
        }
        if((m_eParserBaseType == MW_DVB_SGT_BASE)&&(it==m_NitLinkageInfo.end()))
        {
            m_NitLinkageInfo.clear();
        }
    }
    m_bRunning = MAPI_FALSE;
    return bRetType;
}
#endif
MAPI_BOOL MW_DVB_SI_PSI_Parser::_IsZiggoOperator(void)
{
    return  ((m_eParserType == MW_DVB_C_PARSER)
            && (m_eCountry == E_NETHERLANDS)
            && (m_enCableOperator == EN_CABLEOP_ZIGGO));
}

#if (NCD_ENABLE == 1)
void MW_DVB_SI_PSI_Parser::BuildNCDInfoList(ST_MW_TOTAL_NCD_INFO *pstTotalNCDInfo)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);

    if(pstTotalNCDInfo == NULL)
    {
        MW_SI_PARSER_ERROR("[NCD] %s: pstTotalNCDInfo == NULL\n", __FUNCTION__);
        return;
    }

    MW_SI_PARSER_MESSAGE("[NCD] %s(orig ncd number = %u)\n", __FUNCTION__, pstTotalNCDInfo->u8NCDInfoNumber);
    MW_SI_PARSER_MESSAGE("[NCD] LatestNCDInfo: %u %u %u %u %u\n", pstTotalNCDInfo->stLatestNCDInfo.u16CellId, pstTotalNCDInfo->stLatestNCDInfo.u8NetworkChangeId, \
                        pstTotalNCDInfo->stLatestNCDInfo.u8NetworkChangeVersion, pstTotalNCDInfo->stLatestNCDInfo.u8ChangeType, pstTotalNCDInfo->stLatestNCDInfo.enState);

    m_stNCDInfoList.clear();

    for(int i=0; i<pstTotalNCDInfo->u8NCDInfoNumber; i++)
    {
        MW_SI_PARSER_MESSAGE("[NCD] add nc to list (id %u)\n", pstTotalNCDInfo->astNCDInfo[i].u8NetworkChangeId);
        m_stNCDInfoList.push_back(pstTotalNCDInfo->astNCDInfo[i]);
    }

    memcpy(&m_stLatestNCDInfo, &pstTotalNCDInfo->stLatestNCDInfo, sizeof(ST_MW_NCD_INFO));
    if(m_stLatestNCDInfo.enState == E_NCD_STATE_WAIT_PROCESS)
    {
        m_stLatestNCDInfo.enState = E_NCD_STATE_UNKNOWN;
    }
}

EN_NCD_STATE MW_DVB_SI_PSI_Parser::GetLatestNCDInfo(ST_MW_NCD_INFO& stLatestNCDInfo)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    memcpy(&stLatestNCDInfo, &m_stLatestNCDInfo, sizeof(ST_MW_NCD_INFO));
    return stLatestNCDInfo.enState;
}

void MW_DVB_SI_PSI_Parser::SetLatestNCDProcessingState(void)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    m_stLatestNCDInfo.enState = E_NCD_STATE_PROCESSING;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::GetNetworkChangeInfo(MAPI_U8 *pu8StartTime, MAPI_U8 *pu8Duration, MAPI_U8 *pu8TextChar)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);

    if(pu8StartTime == NULL || pu8Duration == NULL || pu8TextChar == NULL)
    {
        return MAPI_FALSE;
    }

    if(m_stLatestNCDInfo.enState == E_NCD_STATE_PROCESSING)
    {
        memcpy(pu8StartTime, m_stLatestNCDInfo.au8StartTime, 5);
        memcpy(pu8Duration, m_stLatestNCDInfo.au8Duration, 3);
        memcpy(pu8TextChar, m_stLatestNCDInfo.au8TextChar, MAPI_SI_MAX_MSG_LENGTH+1);
        _UpdateNCDInfoAfterProcess();
        return MAPI_TRUE;
    }
    else
    {
        return MAPI_FALSE;
    }
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::_CheckT2Receiver(void)
{
    MAPI_BOOL bEnable;

    m_ParserCallBackInfo.EventHandler(E_DVB_SI_CHECK_SUPPORT_DVBT2_TYPE, (MAPI_U32)m_ParserCallBackInfo.pCallbackReference, (MAPI_U32)&bEnable);
    MW_SI_PARSER_MESSAGE("[NCD] %s: return %u\n", __FUNCTION__, bEnable);
    return bEnable;
}

void MW_DVB_SI_PSI_Parser::_UpdateNCDInfo(void)
{
    MAPI_BOOL bNeedUpdateDb = MAPI_FALSE;
    MAPI_BOOL bLatestNCDInfoUpdated = MAPI_FALSE;
    MAPI_BOOL bNeedToCheckCellId;
    MAPI_U16 u16CellId;
    int i, j, k;
    MAPI_U32 u32CurrentTime, u32StartTime, u32Oldest;
    ST_MW_TOTAL_NCD_INFO stTotalNCDInfo;
    std::list<ST_MW_NCD_INFO>::iterator it, itOldest;

    MW_SI_PARSER_MESSAGE("[NCD] %s\n", __FUNCTION__);

    if(m_eCountry != E_UK || m_eParserType != MW_DVB_T_PARSER)
    {
        MW_SI_PARSER_MESSAGE("[NCD] only uk support ncnd feature\n");
        return;
    }

    m_ParserCallBackInfo.EventHandler(E_DVB_SI_GET_CELLID, (MAPI_U32)m_ParserCallBackInfo.pCallbackReference, (MAPI_U32)&u16CellId);
    MW_SI_PARSER_MESSAGE("[NCD] current cell id = %u\n", u16CellId);

    u32CurrentTime = mapi_interface::Get_mapi_system()->RTCGetCLK();
    bNeedToCheckCellId = ((m_eParserType == MW_DVB_T_PARSER) && (u16CellId > DEFAULT_CELLID)) ? MAPI_TRUE : MAPI_FALSE;

    //update m_stLatestNCDInfo if the change is expired and not processed
    if(m_stLatestNCDInfo.enState == E_NCD_STATE_UNKNOWN)
    {
        if(mapi_dvb_utility::SI_MJDUTC2Seconds(m_stLatestNCDInfo.au8StartTime, NULL) < u32CurrentTime)
        {
            MAPI_U32 u32Last;
            std::list<ST_MW_NCD_INFO> stNCDList;
            std::list<ST_MW_NCD_INFO>::iterator itLast;
            for(it=m_stNCDInfoList.begin(); it!=m_stNCDInfoList.end(); ++it)
            {
                if(mapi_dvb_utility::SI_MJDUTC2Seconds(it->au8StartTime, NULL) < u32CurrentTime)
                {
                    if(bNeedToCheckCellId)
                    {
                        if((it->u16CellId == DEFAULT_CELLID) || (it->u16CellId == u16CellId))
                        {
                            MW_SI_PARSER_MESSAGE("[NCD] push ncd(id %u) to candidate list\n", it->u8NetworkChangeId);
                            stNCDList.push_back(*it);
                        }
                    }
                    else
                    {
                        MW_SI_PARSER_MESSAGE("[NCD] push ncd(id %u) to candidate list\n", it->u8NetworkChangeId);
                        stNCDList.push_back(*it);
                    }
                }
            }

            u32Last = 0;
            itLast = stNCDList.end();
            for(it=stNCDList.begin(); it!=stNCDList.end(); ++it)
            {
                u32StartTime = mapi_dvb_utility::SI_MJDUTC2Seconds(it->au8StartTime, NULL);
                if(u32StartTime > u32Last)
                {
                    u32Last = u32StartTime;
                    itLast = it;
                }
            }

            if((itLast != stNCDList.end()) && (itLast->u8NetworkChangeId != m_stLatestNCDInfo.u8NetworkChangeId))
            {
                MW_SI_PARSER_MESSAGE("[NCD] update m_stLatestNCDInfo to ncd with id %u\n", itLast->u8NetworkChangeId);
                memcpy(&m_stLatestNCDInfo, &(*itLast), sizeof(ST_MW_NCD_INFO));
                bNeedUpdateDb = MAPI_TRUE;
            }
        }

        m_stLatestNCDInfo.enState = E_NCD_STATE_WAIT_PROCESS;
    }

    //reset all ncds' state to unknown and wait for further check
    for(it=m_stNCDInfoList.begin(); it!=m_stNCDInfoList.end(); ++it)
    {
        if(it->u16NetworkID == m_stNit.u16NetworkID)
        {
            it->enState = E_NCD_STATE_UNKNOWN;
        }
    }

    if(m_stNit.u8NCNDNumber > 0)
    {
        MAPI_BOOL bExisted;
        ST_MSGD_DATA *pstMsgData;

        for(i=0; i<m_stNit.u8NCNDNumber; i++)
        {
            for(j=0; j<m_stNit.pstNCND[i].u8CellLoopNum; j++)
            {
                if((m_eParserType == MW_DVB_T_PARSER) && (u16CellId > DEFAULT_CELLID))
                {
                    if(m_stNit.pstNCND[i].astCellNetworkChange[j].u16CellId > DEFAULT_CELLID)
                    {
                        if(m_stNit.pstNCND[i].astCellNetworkChange[j].u16CellId != u16CellId)
                        {
                            MW_SI_PARSER_MESSAGE("[NCD] DVB_T or DVB_T2 but cell id not matched(%u %u)\n", u16CellId, m_stNit.pstNCND[i].astCellNetworkChange[j].u16CellId);
                            continue;
                        }
                        else
                        {
                            MW_SI_PARSER_MESSAGE("[NCD] DVB_T or DVB_T2 and cell id matched(%u)\n", m_stNit.pstNCND[i].astCellNetworkChange[j].u16CellId);
                        }
                    }
                }

                for(k=0; k<m_stNit.pstNCND[i].astCellNetworkChange[j].u8NCNum; k++)
                {
                    if((m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].u8ReceiverCategory == E_ALL_RECEIVER) || \
                        ((m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].u8ReceiverCategory == E_DVB_T2_S2_C2_ONLY) && (_CheckT2Receiver() == MAPI_TRUE))
                        )
                    {
                        MW_SI_PARSER_MESSAGE("[NCD] receiver category check ok\n");
                    }
                    else
                    {
                        MW_SI_PARSER_MESSAGE("[NCD] receiver category not match\n");
                        continue;
                    }

                    if((m_stLatestNCDInfo.enState == E_NCD_STATE_INVALID) && (m_stLatestNCDInfo.u16NetworkID == INVALID_NID) && \
                        (mapi_dvb_utility::SI_MJDUTC2Seconds(m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].au8StartTime, NULL) < u32CurrentTime)
                        )
                    {
                        MW_SI_PARSER_MESSAGE("[NCD] 1. expired ncd (id %u) would not add\n", m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].u8NetworkChangeId);
                        continue;
                    }

                    if((m_stLatestNCDInfo.enState != E_NCD_STATE_INVALID) && (m_stLatestNCDInfo.enState != E_NCD_STATE_UNKNOWN) && \
                        (mapi_dvb_utility::SI_MJDUTC2Seconds(m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].au8StartTime, NULL) < mapi_dvb_utility::SI_MJDUTC2Seconds(m_stLatestNCDInfo.au8StartTime, NULL)) && \
                        ((m_stNit.pstNCND[i].astCellNetworkChange[j].u16CellId != m_stLatestNCDInfo.u16CellId) || (m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].u8NetworkChangeId != m_stLatestNCDInfo.u8NetworkChangeId))
                        )
                    {
                        MW_SI_PARSER_MESSAGE("[NCD] 2. expired ncd (id %u) would not add\n", m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].u8NetworkChangeId);
                        continue;
                    }

                    if((m_stLatestNCDInfo.enState == E_NCD_STATE_EXPIRED) && \
                        (m_stNit.pstNCND[i].astCellNetworkChange[j].u16CellId == m_stLatestNCDInfo.u16CellId) && \
                        (m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].u8NetworkChangeId == m_stLatestNCDInfo.u8NetworkChangeId) && \
                        (m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].u8NetworkChangeVersion == m_stLatestNCDInfo.u8NetworkChangeVersion)
                        )
                    {
                        MW_SI_PARSER_MESSAGE("[NCD] 3. expired ncd (id %u) would not add\n", m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].u8NetworkChangeId);
                        continue;
                    }

                    MW_SI_PARSER_MESSAGE("[NCD] NIT with ncnd (id %u)\n", m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].u8NetworkChangeId);
                    bExisted = MAPI_FALSE;
                    //check if the ncnd existed in list already
                    for(it=m_stNCDInfoList.begin(); it!=m_stNCDInfoList.end(); ++it)
                    {
                        if((m_stNit.pstNCND[i].astCellNetworkChange[j].u16CellId == it->u16CellId) && \
                            (m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].u8NetworkChangeId == it->u8NetworkChangeId))
                        {
                            bExisted = MAPI_TRUE;
                            it->enState = E_NCD_STATE_WAIT_PROCESS;
                            //ncnd existed in list but need update
                            if(((it->u8NetworkChangeVersion < 0xFF) && (m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].u8NetworkChangeVersion == it->u8NetworkChangeVersion+1)) || \
                                ((it->u8NetworkChangeVersion == 0xFF) && (m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].u8NetworkChangeVersion == 0)) \
                                )
                            {
                                MW_SI_PARSER_MESSAGE("[NCD] ncd (id %u) is updated\n", it->u8NetworkChangeId);
                                it->u8NetworkChangeVersion = m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].u8NetworkChangeVersion;
                                memcpy(it->au8StartTime, m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].au8StartTime, 5);
                                memcpy(it->au8Duration, m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].au8Duration, 3);
                                it->u8ReceiverCategory = m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].u8ReceiverCategory;
                                it->u8InvariantTSPresent = m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].u8InvariantTSPresent;
                                it->u8ChangeType = m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].u8ChangeType;
                                it->u16InvariantTSId = m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].u16InvariantTSId;
                                it->u16InvariantONId = m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].u16InvariantONId;

                                pstMsgData = m_stNit.pstMSGD;
                                memset(it->au8TextChar, 0, MAPI_SI_MAX_MSG_LENGTH+1);
                                while(pstMsgData != NULL)
                                {
                                    if(pstMsgData->u8MessageId == m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].u8MessageId && \
                                        memcmp(pstMsgData->au8ISO639LanguageCode, "eng", 3) == 0)
                                    {
                                        memcpy(it->au8ISO639LanguageCode, pstMsgData->au8ISO639LanguageCode, 3);
                                        memcpy(it->au8TextChar, pstMsgData->au8TextChar, MAPI_SI_MAX_MSG_LENGTH+1);
                                        break;
                                    }
                                    pstMsgData = pstMsgData->next;
                                }
                                if(pstMsgData == NULL)
                                {
                                    const char StdStr[] = "No information available";
                                    memcpy(it->au8TextChar, StdStr, sizeof(StdStr));
                                }

                                bNeedUpdateDb = MAPI_TRUE;
                                if((it->u8NetworkChangeId == m_stLatestNCDInfo.u8NetworkChangeId) && (m_stLatestNCDInfo.enState == E_NCD_STATE_WAIT_PROCESS))
                                {
                                    bLatestNCDInfoUpdated = MAPI_TRUE;
                                }
                            }
                            break;
                        }
                    }

                    //the ncnd not existed in list and add it
                    if(bExisted == MAPI_FALSE)
                    {
                        ST_MW_NCD_INFO stNCDInfo;

                        MW_SI_PARSER_MESSAGE("[NCD] add new ncd (id %d) to list\n", m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].u8NetworkChangeId);

                        stNCDInfo.u16NetworkID = m_stNit.u16NetworkID;
                        stNCDInfo.enState = E_NCD_STATE_WAIT_PROCESS;
                        stNCDInfo.u16CellId = m_stNit.pstNCND[i].astCellNetworkChange[j].u16CellId;
                        stNCDInfo.u8NetworkChangeId = m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].u8NetworkChangeId;
                        stNCDInfo.u8NetworkChangeVersion = m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].u8NetworkChangeVersion;
                        memcpy(stNCDInfo.au8StartTime, m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].au8StartTime, 5);
                        memcpy(stNCDInfo.au8Duration, m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].au8Duration, 3);
                        stNCDInfo.u8ReceiverCategory = m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].u8ReceiverCategory;
                        stNCDInfo.u8InvariantTSPresent = m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].u8InvariantTSPresent;
                        stNCDInfo.u8ChangeType = m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].u8ChangeType;
                        stNCDInfo.u16InvariantTSId = m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].u16InvariantTSId;
                        stNCDInfo.u16InvariantONId = m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].u16InvariantONId;

                        pstMsgData = m_stNit.pstMSGD;
                        memset(stNCDInfo.au8TextChar, 0, MAPI_SI_MAX_MSG_LENGTH+1);
                        while(pstMsgData != NULL)
                        {
                            if(pstMsgData->u8MessageId == m_stNit.pstNCND[i].astCellNetworkChange[j].astNetworkChange[k].u8MessageId && \
                                memcmp(pstMsgData->au8ISO639LanguageCode, "eng", 3) == 0)
                            {
                                memcpy(stNCDInfo.au8ISO639LanguageCode, pstMsgData->au8ISO639LanguageCode, 3);
                                memcpy(stNCDInfo.au8TextChar, pstMsgData->au8TextChar, MAPI_SI_MAX_MSG_LENGTH+1);
                                break;
                            }
                            pstMsgData = pstMsgData->next;
                        }
                        if(pstMsgData == NULL)
                        {
                            const char StdStr[] = "No information available";
                            memcpy(stNCDInfo.au8TextChar, StdStr, sizeof(StdStr));
                        }
                        m_stNCDInfoList.push_back(stNCDInfo);
                        bNeedUpdateDb = MAPI_TRUE;
                    }
                }
            }
        }
    }
    else
    {
        // distinguish the situation of first time update ncd info after receiver reset
        if((m_stLatestNCDInfo.enState == E_NCD_STATE_INVALID) && (m_stLatestNCDInfo.u16NetworkID == INVALID_NID))
        {
            MW_SI_PARSER_MESSAGE("[NCD] assign valid value to m_stLatestNCDInfo.u16NetworkID\n");
            m_stLatestNCDInfo.u16NetworkID = m_stNit.u16NetworkID;
            bNeedUpdateDb = MAPI_TRUE;
        }
    }

    memset(&stTotalNCDInfo, 0, sizeof(ST_MW_TOTAL_NCD_INFO));
    memcpy(&stTotalNCDInfo.stLatestNCDInfo, &m_stLatestNCDInfo, sizeof(ST_MW_NCD_INFO));
    for(i=0; i<MAX_NCD_INFO_NUM; i++)
    {
        stTotalNCDInfo.astNCDInfo[i].u16NetworkID = INVALID_NID;
        stTotalNCDInfo.astNCDInfo[i].enState = E_NCD_STATE_INVALID;
    }

    i = 0;
    u32Oldest = 0xFFFFFFFF;
    itOldest = m_stNCDInfoList.end();

    //build up stTotalNCDInfo and remove expired ncd(s) from list
    for(it=m_stNCDInfoList.begin(); it!=m_stNCDInfoList.end();)
    {
        if(it->enState == E_NCD_STATE_UNKNOWN)
        {
            if((it->u16CellId == m_stLatestNCDInfo.u16CellId) && \
                (it->u8NetworkChangeId == m_stLatestNCDInfo.u8NetworkChangeId) && \
                (it->u8NetworkChangeVersion == m_stLatestNCDInfo.u8NetworkChangeVersion) && \
                (m_stLatestNCDInfo.enState == E_NCD_STATE_WAIT_PROCESS))
            {
                if(mapi_dvb_utility::SI_MJDUTC2Seconds(m_stLatestNCDInfo.au8StartTime, NULL) > u32CurrentTime)
                {
                    MW_SI_PARSER_MESSAGE("[NCD] future latest ncd not exist, change its state to expired\n");
                    m_stLatestNCDInfo.enState = E_NCD_STATE_EXPIRED;
                }
            }
            MW_SI_PARSER_MESSAGE("[NCD] remove unknown ncd (id %u) from list\n", it->u8NetworkChangeId);
            it = m_stNCDInfoList.erase(it);
            bNeedUpdateDb = MAPI_TRUE;
        }
        else
        {
            if(i == MAX_NCD_INFO_NUM)
            {
                MW_SI_PARSER_MESSAGE("[NCD] ncd number exceeded!!\n");
                break;
            }

            MW_SI_PARSER_MESSAGE("[NCD] ncd (id %u) in list\n", it->u8NetworkChangeId);
            memcpy(&stTotalNCDInfo.astNCDInfo[i++], &(*it), sizeof(ST_MW_NCD_INFO));

            if(bNeedToCheckCellId)
            {
                if((it->u16CellId == DEFAULT_CELLID) || (it->u16CellId == u16CellId))
                {
                    u32StartTime = mapi_dvb_utility::SI_MJDUTC2Seconds(it->au8StartTime, NULL);
                    if(u32StartTime < u32Oldest)
                    {
                        u32Oldest = u32StartTime;
                        itOldest = it;
                    }
                }
            }
            else
            {
                u32StartTime = mapi_dvb_utility::SI_MJDUTC2Seconds(it->au8StartTime, NULL);
                if(u32StartTime < u32Oldest)
                {
                    u32Oldest = u32StartTime;
                    itOldest = it;
                }
            }
            it++;
        }
    }

    stTotalNCDInfo.u8NCDInfoNumber = i;

    //update the latest ncd info
    if(itOldest != m_stNCDInfoList.end())
    {
        if((m_stLatestNCDInfo.enState == E_NCD_STATE_INVALID) || (m_stLatestNCDInfo.enState == E_NCD_STATE_UNKNOWN) || (m_stLatestNCDInfo.enState == E_NCD_STATE_EXPIRED) || \
            (bLatestNCDInfoUpdated == MAPI_TRUE) || \
            ((m_stLatestNCDInfo.u8NetworkChangeId != itOldest->u8NetworkChangeId) && (u32Oldest < mapi_dvb_utility::SI_MJDUTC2Seconds(m_stLatestNCDInfo.au8StartTime, NULL))) \
            )
        {
            memcpy(&m_stLatestNCDInfo, &(*itOldest), sizeof(ST_MW_NCD_INFO));
            MW_SI_PARSER_MESSAGE("[NCD] set m_stLatestNCDInfo: %u %u %u %u %u\n", m_stLatestNCDInfo.u16CellId, m_stLatestNCDInfo.u8NetworkChangeId, \
                            m_stLatestNCDInfo.u8NetworkChangeVersion, m_stLatestNCDInfo.u8ChangeType, m_stLatestNCDInfo.enState);
            bNeedUpdateDb = MAPI_TRUE;
        }
    }

    if(bNeedUpdateDb == MAPI_TRUE)
    {
        MW_SI_PARSER_MESSAGE("[NCD] update database\n");
        memcpy(&stTotalNCDInfo.stLatestNCDInfo, &m_stLatestNCDInfo, sizeof(ST_MW_NCD_INFO));
        m_ParserCallBackInfo.EventHandler(E_DVB_SI_SAVE_TOTAL_NCD_INFO, \
                                                                        (MAPI_U32)m_ParserCallBackInfo.pCallbackReference, (MAPI_U32)&stTotalNCDInfo);
    }
}

MAPI_U16 MW_DVB_SI_PSI_Parser::_FindFirstServiceIdInTS(MAPI_U16 u16TSId, MAPI_U16 u16ONId)
{
    int i;

    MW_SI_PARSER_MESSAGE("[NCD] %s: tsid = %u, onid = %u\n", __FUNCTION__, u16TSId, u16ONId);

    for(i=0; i<m_stNit.u16TSNumber; i++)
    {
        if(m_stNit.pstTSInfo[i].wTransportStream_ID == INVALID_TS_ID)
        {
            continue;
        }
        if((m_stNit.pstTSInfo[i].wTransportStream_ID == u16TSId) && (m_stNit.pstTSInfo[i].u16ONID == u16ONId))
        {
            break;
        }
    }

    if(i == m_stNit.u16TSNumber)
    {
        MW_SI_PARSER_MESSAGE("[NCD] cannot find match tsid and onid\n");
        return INVALID_SERVICE_ID;
    }
    else
    {
        if(m_stNit.pstTSInfo[i].u16ServiceNum > 0)
        {
            return m_stNit.pstTSInfo[i].astServiceInfo[0].u16ServiceID;
        }
        else
        {
            return INVALID_SERVICE_ID;
        }
    }
}

void MW_DVB_SI_PSI_Parser::StoreCurrentProgInfo(U16 u16TSId, U16 u16ONId, U16 u16SrvId)
{
    MW_SI_PARSER_MESSAGE("[NCD] %s: original tsid = %u, onid = %u, service id = %u\n", __FUNCTION__, u16TSId, u16ONId, u16SrvId);

    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    m_stTripleIdBeforeNC.u16TsId = u16TSId;
    m_stTripleIdBeforeNC.u16OnId = u16ONId;
    m_stTripleIdBeforeNC.u16SrvId = u16SrvId;
}

void MW_DVB_SI_PSI_Parser::PostNCProcess(void)
{
    //MAPI_U32 u32OffsetTime, u32StartTime;
    //MAPI_SI_TIME stStartTime;
    const U8 u8CheckIsMajor = 0x08;

    MW_SI_PARSER_MESSAGE("[NCD] %s\n", __FUNCTION__);

    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);

    if(m_eCountry != E_UK || m_eParserType != MW_DVB_T_PARSER)
    {
        MW_SI_PARSER_MESSAGE("[NCD] only uk support ncnd feature\n");
        return;
    }

    if(m_stLatestNCDInfo.enState == E_NCD_STATE_PROCESSING)
    {
        if(m_stLatestNCDInfo.u8InvariantTSPresent == 1)
        {
            MAPI_BOOL bProgExist = MAPI_FALSE;
            ST_TRIPLE_ID stTripleId;
            stTripleId.u16TsId = m_stLatestNCDInfo.u16InvariantTSId;
            stTripleId.u16OnId = m_stLatestNCDInfo.u16InvariantONId;
            stTripleId.u16SrvId = _FindFirstServiceIdInTS(stTripleId.u16TsId, stTripleId.u16OnId);

            IS_PROG_EXIST(stTripleId.u16TsId, stTripleId.u16OnId, stTripleId.u16SrvId, bProgExist);
            if(bProgExist == MAPI_TRUE)
            {
                MONITOR_NOTIFY(E_DVB_NC_TUNE_TO_SERVICE, &stTripleId, m_pMonitorNotifyUsrParam, NULL);
            }
            else
            {
                MONITOR_NOTIFY(E_DVB_NC_TUNE_TO_SERVICE, &m_stTripleIdBeforeNC, m_pMonitorNotifyUsrParam, NULL);
            }
        }
        else
        {
            if((m_stLatestNCDInfo.u8ChangeType & u8CheckIsMajor) || (m_stLatestNCDInfo.u8ChangeType == E_NC_TYPE_MUX_REMOVED))
            {
                MONITOR_NOTIFY(E_DVB_NC_TUNE_TO_SERVICE, &m_stTripleIdBeforeNC, m_pMonitorNotifyUsrParam, NULL);
            }
        }

        m_stTripleIdBeforeNC.u16TsId = INVALID_TS_ID;
        m_stTripleIdBeforeNC.u16OnId = INVALID_ON_ID;
        m_stTripleIdBeforeNC.u16SrvId = INVALID_SERVICE_ID;

        /*u32OffsetTime = mapi_interface::Get_mapi_system()->GetClockOffset();
        u32StartTime = mapi_dvb_utility::SI_MJDUTC2Seconds(m_stLatestNCDInfo.au8StartTime, NULL) + u32OffsetTime;
        mapi_dvb_utility::SI_Seconds2StTime(u32StartTime, &stStartTime);

        MW_SI_PARSER_MESSAGE("\n#############NC INFORMATION#############\n");
        MW_SI_PARSER_MESSAGE("[NCD] %u/%u/%u %02u:%02u:%02u for %02x:%02x:%02x\n", stStartTime.u16Year, stStartTime.u8Month, stStartTime.u8Day, \
                        stStartTime.u8Hour, stStartTime.u8Min, stStartTime.u8Sec, \
                        m_stLatestNCDInfo.au8Duration[0], m_stLatestNCDInfo.au8Duration[1], m_stLatestNCDInfo.au8Duration[2]);
        MW_SI_PARSER_MESSAGE("     Network change occurred!!\n");
        MW_SI_PARSER_MESSAGE("     %s\n", m_stLatestNCDInfo.au8TextChar);
        MW_SI_PARSER_MESSAGE("########################################\n\n");*/

        MONITOR_NOTIFY(E_DVB_NC_MESSAGE_DISPLAY, NULL, m_pMonitorNotifyUsrParam, NULL);
    }
}

void MW_DVB_SI_PSI_Parser::_UpdateNCDInfoAfterProcess(void)
{
    MAPI_BOOL bNeedToCheckCellId;
    MAPI_U16 u16CellId;
    MAPI_U32 u32StartTime, u32Oldest;
    int i;
    ST_MW_TOTAL_NCD_INFO stTotalNCDInfo;
    std::list<ST_MW_NCD_INFO>::iterator it, itOldest;

    MW_SI_PARSER_MESSAGE("[NCD] %s\n", __FUNCTION__);

    m_stLatestNCDInfo.enState = E_NCD_STATE_EXPIRED;
    m_ParserCallBackInfo.EventHandler(E_DVB_SI_GET_CELLID, (MAPI_U32)m_ParserCallBackInfo.pCallbackReference, (MAPI_U32)&u16CellId);

    memset(&stTotalNCDInfo, 0, sizeof(ST_MW_TOTAL_NCD_INFO));
    for(i=0; i<MAX_NCD_INFO_NUM; i++)
    {
        stTotalNCDInfo.astNCDInfo[i].u16NetworkID = INVALID_NID;
        stTotalNCDInfo.astNCDInfo[i].enState = E_NCD_STATE_INVALID;
    }

    i = 0;
    u32Oldest = 0xFFFFFFFF;
    itOldest = m_stNCDInfoList.end();
    bNeedToCheckCellId = ((m_eParserType == MW_DVB_T_PARSER) && (u16CellId > DEFAULT_CELLID)) ? MAPI_TRUE : MAPI_FALSE;

    //build up stTotalNCDInfo and remove expired ncd(s) from list
    for(it=m_stNCDInfoList.begin(); it!=m_stNCDInfoList.end();)
    {
        if((it->u16CellId == m_stLatestNCDInfo.u16CellId) && \
            (it->u8NetworkChangeId == m_stLatestNCDInfo.u8NetworkChangeId) && \
            (it->u8NetworkChangeVersion == m_stLatestNCDInfo.u8NetworkChangeVersion))
        {
            MW_SI_PARSER_MESSAGE("[NCD] remove expired latest ncd (id %u) from list\n", it->u8NetworkChangeId);
            it = m_stNCDInfoList.erase(it);
        }
        else
        {
            if(i == MAX_NCD_INFO_NUM)
            {
                MW_SI_PARSER_MESSAGE("[NCD] ncd number exceeded!!\n");
                break;
            }

            MW_SI_PARSER_MESSAGE("[NCD] ncd (id %u) in list\n", it->u8NetworkChangeId);
            memcpy(&stTotalNCDInfo.astNCDInfo[i++], &(*it), sizeof(ST_MW_NCD_INFO));

            if(bNeedToCheckCellId)
            {
                if((it->u16CellId == DEFAULT_CELLID) || (it->u16CellId == u16CellId))
                {
                    u32StartTime = mapi_dvb_utility::SI_MJDUTC2Seconds(it->au8StartTime, NULL);
                    if(u32StartTime < u32Oldest)
                    {
                        u32Oldest = u32StartTime;
                        itOldest = it;
                    }
                }
            }
            else
            {
                u32StartTime = mapi_dvb_utility::SI_MJDUTC2Seconds(it->au8StartTime, NULL);
                if(u32StartTime < u32Oldest)
                {
                    u32Oldest = u32StartTime;
                    itOldest = it;
                }
            }
            it++;
        }
    }

    stTotalNCDInfo.u8NCDInfoNumber = i;

    //update the latest ncd info
    if(itOldest != m_stNCDInfoList.end())
    {
        memcpy(&m_stLatestNCDInfo, &(*itOldest), sizeof(ST_MW_NCD_INFO));
        MW_SI_PARSER_MESSAGE("[NCD] set m_stLatestNCDInfo: %u %u %u %u %u\n", m_stLatestNCDInfo.u16CellId, m_stLatestNCDInfo.u8NetworkChangeId, \
                        m_stLatestNCDInfo.u8NetworkChangeVersion, m_stLatestNCDInfo.u8ChangeType, m_stLatestNCDInfo.enState);
    }
    else
    {
        MW_SI_PARSER_MESSAGE("[NCD] no need to update m_stLatestNCDInfo\n");
    }

    MW_SI_PARSER_MESSAGE("[NCD] update database\n");
    memcpy(&stTotalNCDInfo.stLatestNCDInfo, &m_stLatestNCDInfo, sizeof(ST_MW_NCD_INFO));
    m_ParserCallBackInfo.EventHandler(E_DVB_SI_SAVE_TOTAL_NCD_INFO, \
                                                                    (MAPI_U32)m_ParserCallBackInfo.pCallbackReference, (MAPI_U32)&stTotalNCDInfo);
}

void MW_DVB_SI_PSI_Parser::_FindSdtOtherIndexForRelocate(void)
{
    std::list<ST_MW_RELOCATED_SERVICE_INFO>::iterator it;
    MAPI_U16 i;

    for(it=m_stRelocatedServiceInfoList.begin(); it!=m_stRelocatedServiceInfoList.end();)
    {
        if(it->enState == E_RELOCATED_SERVICE_STATE_WAIT_CREATE_FILTER)
        {
            for(i=0; i<m_stSdtOtherInfo.stSdtOtherTS.size(); i++)
            {
                if(m_stSdtOtherInfo.stSdtOtherTS.at(i).u16TSID == it->u16TSIDHasSameService)
                {
                    m_stSdtOtherInfo.u8SdtOtherIndex = i;
                    MW_SI_PARSER_UPDATE("[NCD] %s: create filter for service relocated, m_stSdtOtherInfo.u8SdtOtherIndex = %u\n", __FUNCTION__, i);
                    break;
                }
            }

            if(i < m_stSdtOtherInfo.stSdtOtherTS.size())
            {
                break;
            }
            else
            {
                MW_SI_PARSER_UPDATE("[NCD] %s: not found TSID %u, erase SID %u in TSID %u(size = %u)\n", __FUNCTION__, it->u16TSIDHasSameService, \
                                it->stTripleId.u16SrvId, it->stTripleId.u16TsId, m_stRelocatedServiceInfoList.size());
                it = m_stRelocatedServiceInfoList.erase(it);
            }
        }
        else
        {
            it++;
        }
    }
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::_AddRelocatedServiceToList(MAPI_SI_TABLE_SDT *pstSdtTable, int index, DVB_CM *pCMDB)
{
    if(pstSdtTable == NULL)
    {
        return MAPI_FALSE;
    }

    DVB_PROG *pProgInfo = pCMDB->GetByID(pstSdtTable->wOriginalNetwork_ID, pstSdtTable->astServiceInfo[index].u16ServiceID);
    if(pProgInfo)
    {
        ST_TRIPLE_ID stTripleId;
        DVB_MUX *pMuxInfo = __GetMux(pProgInfo, pCMDB);
        DVB_NETWORK *pNetworkInfo = __GetNetwork(pMuxInfo, pCMDB);

        stTripleId.u16OnId = pstSdtTable->wOriginalNetwork_ID;
        stTripleId.u16TsId = pstSdtTable->wTransportStream_ID;
        stTripleId.u16SrvId = pstSdtTable->astServiceInfo[index].u16ServiceID;

        if(_CheckInRelocatedServiceList(&stTripleId) == MAPI_FALSE)
        {
            ST_MW_RELOCATED_SERVICE_INFO stRelocatedServiceInfo;
            stRelocatedServiceInfo.stTripleId.u16OnId = stTripleId.u16OnId;
            stRelocatedServiceInfo.stTripleId.u16TsId = stTripleId.u16TsId;
            stRelocatedServiceInfo.stTripleId.u16SrvId = stTripleId.u16SrvId;
            memcpy(&stRelocatedServiceInfo.stServiceInfo, &pstSdtTable->astServiceInfo[index], sizeof(MAPI_SI_SDT_SERVICE_INFO));
            stRelocatedServiceInfo.u16TSIDHasSameService = __GetID(pProgInfo, pMuxInfo, pNetworkInfo, EN_ID_TSID);
            stRelocatedServiceInfo.enState = E_RELOCATED_SERVICE_STATE_WAIT_CREATE_FILTER;
            m_stRelocatedServiceInfoList.push_back(stRelocatedServiceInfo);
            MW_SI_PARSER_UPDATE("[NCD] %s: find the same SID(%u) in other TS(%u) (size = %u)\n", __FUNCTION__, \
                            stTripleId.u16SrvId, stRelocatedServiceInfo.u16TSIDHasSameService, m_stRelocatedServiceInfoList.size());
        }
        return MAPI_TRUE;
    }
    else
    {
        return MAPI_FALSE;
    }
}

void MW_DVB_SI_PSI_Parser::_UpdateRelocatedServiceList(MAPI_U16 u16TSId)
{
    std::list<ST_MW_RELOCATED_SERVICE_INFO>::iterator it;

    for(it=m_stRelocatedServiceInfoList.begin(); it!=m_stRelocatedServiceInfoList.end();)
    {
        if(it->u16TSIDHasSameService == u16TSId)
        {
            if(it->enState == E_RELOCATED_SERVICE_STATE_WAIT_CREATE_FILTER)
            {
                MW_SI_PARSER_UPDATE("[NCD] %s: set SID %u in TSID %u to WAIT_DELETE\n", __FUNCTION__, it->stTripleId.u16SrvId, it->stTripleId.u16TsId);
                it->enState = E_RELOCATED_SERVICE_STATE_WAIT_DELETE_OTHER;
                it++;
            }
            else if(it->enState == E_RELOCATED_SERVICE_STATE_WAIT_DELETE_OTHER)
            {
                MW_SI_PARSER_UPDATE("[NCD] %s: SID %u in TSID %u is duplicate, erase(size = %u)\n", __FUNCTION__, \
                                it->stTripleId.u16SrvId, it->stTripleId.u16TsId, m_stRelocatedServiceInfoList.size());
                it = m_stRelocatedServiceInfoList.erase(it);
            }
            else
            {
                it++;
            }
        }
        else
        {
            it++;
        }
    }
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::_CheckInRelocatedServiceList(ST_TRIPLE_ID *pstTripleId)
{
    std::list<ST_MW_RELOCATED_SERVICE_INFO>::iterator it;

    if(pstTripleId == NULL)
    {
        return MAPI_FALSE;
    }

    for(it=m_stRelocatedServiceInfoList.begin(); it!=m_stRelocatedServiceInfoList.end(); ++it)
    {
        if((it->stTripleId.u16SrvId == pstTripleId->u16SrvId) && (it->stTripleId.u16TsId == pstTripleId->u16TsId) && \
            (it->stTripleId.u16OnId == pstTripleId->u16OnId))
        {
            MW_SI_PARSER_UPDATE("[NCD] %s: SID %u in TSID %u has existed in m_stRelocatedServiceInfoList\n", __FUNCTION__, \
                            it->stTripleId.u16SrvId, it->stTripleId.u16TsId);
            return MAPI_TRUE;
        }
    }

    return MAPI_FALSE;
}
#endif

#if(ASTRA_SGT_ENABLE ==1)
MAPI_BOOL MW_DVB_SI_PSI_Parser::GetSgtBasicServicelistInfo(const MAPI_U8 u8ServicelistNumber,MAPI_SI_SGT_BASIC_SERVICE_LIST_INFO *pStBasicSerivelistInfo)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    ASSERT(m_bInit);

    MAPI_BOOL bRet = MAPI_FALSE;

    if((m_ServiceListBasicInfo.size()!=0)&&(u8ServicelistNumber!=0))
    {
        MAPI_U8 i=0;
        for(ServiceListBasicInfo_t::iterator it =m_ServiceListBasicInfo.begin();it!=m_ServiceListBasicInfo.end();++it)
        {
            if(i>=u8ServicelistNumber)
            {
                break;
            }
            pStBasicSerivelistInfo[i]=it->second;
            ++i;
        }
        bRet = MAPI_TRUE;
    }
    return bRet;
}
static BOOL IsSameTripleId(const MAPI_SI_SGT_SERVICE_INFO& rSgtServiceInfo, const ST_DVB_PROGRAMINFO* pProg,  DVB_CM* pCMDB)
{
    ST_TRIPLE_ID cProgId;
    if(pCMDB->GetProgramTripleID((ST_DVB_PROGRAMINFO*)pProg, cProgId.u16OnId, cProgId.u16TsId, cProgId.u16SrvId) &&
      (cProgId.u16TsId ==rSgtServiceInfo.stTripleIds.u16TsId)&&
      (cProgId.u16SrvId ==rSgtServiceInfo.stTripleIds.u16SrvId)&&
      (cProgId.u16OnId ==rSgtServiceInfo.stTripleIds.u16OnId))
    {
        return TRUE;
    }
    return FALSE;
}
void MW_DVB_SI_PSI_Parser::StartSGTScan(void (*pfSIScanResultNotify)(MW_DVB_SCAN_RESULT enEvent, MAPI_U32 u32Param1, MW_DVB_TS_INFO *pTsInfo, MW_DVB_PROGRAM_INFO *pServiceInfo), MAPI_U32  u32ScanResultNotifyParam1,MAPI_U16 u16TSID)
{
    ASSERT(m_bInit);

    m_u32ScanResultNotifyParam1 = u32ScanResultNotifyParam1;
    _pfScanResultNotify = pfSIScanResultNotify;
    m_u16SateScanTsid = u16TSID;
    mapi_si_psi_event cEvt;
    cEvt.u32Event = (MAPI_U32) EN_DVB_SGT_SCAN_START;
    m_pParserTriggerEvent->Send(cEvt);
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::_SGT_PMT_Monitor()
{
    if(m_SGTLinkageInfo.size()==0)
    {
        MW_SI_SGT_SCAN_DEBUG("%s ==No SGT linkage info==\n",__FUNCTION__);
        return MAPI_FALSE;
    }
    MAPI_U16 u16Serviceid= INVALID_SERVICE_ID;
    for(NIT_LINKAGE_t::iterator it=m_SGTLinkageInfo.begin();it!=m_SGTLinkageInfo.end();++it)
    {
        if(it->bIsMatchCurTs == MAPI_TRUE)
        {
            u16Serviceid = it->stTargetCh.u16SrvId;
        }
    }
    if(u16Serviceid == INVALID_SERVICE_ID)
    {
        MW_SI_SGT_SCAN_DEBUG("%s==No Match Cur TSID==\n",__FUNCTION__);
        return MAPI_FALSE;
    }
    for(int j = 0; j < MAX_CHANNEL_IN_MUX; j++)
    {
        if(m_pAllPmtParser[j]!=NULL)
        {
            continue;
        }
        if((m_u32NitMonitorTimer == 0) || (_Timer_DiffTimeFromNow(m_u32NitMonitorTimer) > NIT_MONITOR_PERIOD))
        {
            _CreatePmtFilterBySrvID(u16Serviceid);

        }
    }
    return MAPI_TRUE;
}
MAPI_BOOL MW_DVB_SI_PSI_Parser::_SGTBuildCh(void)
{
    MW_SI_SGT_SCAN_DEBUG("SGT SCAN STATE : _SGTBuildCh \n");
    MAPI_U16 u16NitTsPos = 0;

    memset(&m_TsInfo, 0, sizeof(m_TsInfo));
    memset(m_ProgramInfo, 0, sizeof(MW_DVB_PROGRAM_INFO)*MAX_CHANNEL_IN_MUX);

    typedef std::list<MAPI_SI_SGT_SERVICE_INFO> ServicesInfo_t;

    ServicesInfo_t stServicesInfo;
    stServicesInfo.clear();

    if(((m_u16SateScanTsid == INVALID_TS_ID)&&(m_DvbsScanTpMap.size()==0))||(NULL==m_stSgt.pSGTServiceListInfo))
    {
        if(_pfScanResultNotify)
        {
            _pfScanResultNotify(E_DVB_SCAN_STOP, m_u32ScanResultNotifyParam1, NULL, NULL);
        }
        return MAPI_FALSE;
    }

    for(ServiceList_t::iterator it =m_stSgt.pSGTServiceListInfo->begin();it!=m_stSgt.pSGTServiceListInfo->end();++it)
    {
        if(it->second.stTripleIds.u16TsId ==m_u16SateScanTsid)
        {
            MW_SI_SGT_SCAN_DEBUG("%s %d %s\n",__FUNCTION__,__LINE__,it->second.au8ServiceName);
            stServicesInfo.push_back(it->second);
        }
    }

    for (u16NitTsPos = 0; u16NitTsPos < m_stNit.u16TSNumber; u16NitTsPos++)
    {
        if (m_stNit.pstTSInfo[u16NitTsPos].wTransportStream_ID == m_u16SateScanTsid)
        {
            break;
        }
    }
    if (u16NitTsPos<m_stNit.u16TSNumber)
    {
        // update ts info.
        m_TsInfo.u16NID = m_stNit.u16NetworkID;
        m_TsInfo.u16ONID = m_stNit.pstTSInfo[u16NitTsPos].u16ONID;
        m_TsInfo.u16TSID = m_stNit.pstTSInfo[u16NitTsPos].wTransportStream_ID;
        m_TsInfo.u16ServiceCount = 0;
        m_TsInfo.u8NetWrokNameLen = m_stNit.u8NetWrokNameLen;
        memcpy(m_TsInfo.au8NetWorkName, m_stNit.au8NetWorkName , m_TsInfo.u8NetWrokNameLen);
        for (ServicesInfo_t::iterator it =stServicesInfo.begin();it!=stServicesInfo.end();++it)
        {
            _ResetServiceInfo(m_ProgramInfo[m_TsInfo.u16ServiceCount]);

            m_ProgramInfo[m_TsInfo.u16ServiceCount].u16ServiceID = it->stTripleIds.u16SrvId;

            _UpdateServiceType(&m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceType,
                                &m_ProgramInfo[m_TsInfo.u16ServiceCount].u8ServiceTypePrio,it->u8ServiceType);

            m_ProgramInfo[m_TsInfo.u16ServiceCount].u8RealServiceType = it->u8ServiceType;
            memcpy(m_ProgramInfo[m_TsInfo.u16ServiceCount].au8ServiceName, it->au8ServiceName, MAPI_SI_MAX_SERVICE_NAME);
            ///m_ProgramInfo[m_TsInfo.u16ServiceCount].u8NitVer = m_stNit.u8Version;
            m_ProgramInfo[m_TsInfo.u16ServiceCount].u8SgtVer = m_stSgt.u8Version;
            m_ProgramInfo[m_TsInfo.u16ServiceCount].u16Servicelistid = m_stSgt.stBasicServiceListInfo.u16ServiceListID;
            m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsCAExist = it->bIsServiceScrambled;
            m_ProgramInfo[m_TsInfo.u16ServiceCount].u16VirtualServiceID = it->u16VirtualServiceID;
            m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsVisible = it->bIsVisble;
            m_ProgramInfo[m_TsInfo.u16ServiceCount].u16LCN = it->u16LCN;
            MW_SI_SGT_SCAN_DEBUG("Program name %s LCN %d visible %d\n",m_ProgramInfo[m_TsInfo.u16ServiceCount].au8ServiceName,m_ProgramInfo[m_TsInfo.u16ServiceCount].u16LCN,m_ProgramInfo[m_TsInfo.u16ServiceCount].bIsVisible);
            m_TsInfo.u16ServiceCount ++;
        }
    }
    m_bRunning = MAPI_FALSE;
    if(m_TsInfo.u16ServiceCount !=0)
    {
        m_SGTLinkageInfo.clear();
        m_NitLinkageInfo.clear();
    }
    if(_pfScanResultNotify)
    {
        _pfScanResultNotify(E_DVB_SCAN_FINISH, m_u32ScanResultNotifyParam1, &m_TsInfo, m_ProgramInfo);
    }
    return MAPI_TRUE;
}
void MW_DVB_SI_PSI_Parser::_SGTScan(void)
{
    MAPI_BOOL bRet = MAPI_FALSE;
    switch(m_enSgtScanState)
    {
        case E_SGT_SCAN_STATE_CREATE_PAT_NIT:
            bRet = _SGTScanCreateNitPatParser();
            break;
        case E_SGT_SCAN_STATE_WAIT_PAT_NIT_READY:
            bRet = _SGTScanWaitPatNitReady();
            break;
        case E_SGT_SCAN_STATE_CREATE_SGT_PARSER:
            bRet = _SGTScanSgtCreate();
            break;
        case E_SGT_SCAN_STATE_WAIT_LISTS:
            bRet = _SGTScanWaitLists();
            break;
        case E_SGT_SCAN_STATE_WAIT_TABLE:
            bRet = _SGTScanWaitTable();
            break;
        case E_SGT_SCAN_STATE_BUILD_CH:
            bRet = _SGTBuildCh();
            break;
        default:
            break;
    }

    if((bRet == MAPI_FALSE)&& (_IsMoreTpAvailable(E_LINKAGE_SGT)==E_LINKAGE_NONE))
    {
        m_bRunning = MAPI_FALSE;
        _FreeDeliveryInfo(m_stNit, MAPI_TRUE);
        _ScanError(E_DVB_SCAN_DATA_ERROR);
        m_enSgtScanState = E_SGT_SCAN_STATE_IDLE;
    }
}
MAPI_BOOL MW_DVB_SI_PSI_Parser:: _SGTScanCreateNitPatParser(void)
{
    MAPI_BOOL bRet =MAPI_FALSE;

    m_NitLinkageInfo.clear();
    if(m_enSgtSource == E_SGT_TP_SGT_LD)
    {
        m_SGTLinkageInfo.clear();
    }
    if(_CreatePATNITParser()== MAPI_FALSE)
    {
        return MAPI_FALSE;
    }
    MW_SI_SGT_SCAN_DEBUG("SGT SCAN STATE : _SGTScanCreateNitPatParser \n");
    m_enSgtScanState = E_SGT_SCAN_STATE_WAIT_PAT_NIT_READY;

    m_eParserMode = E_DVB_SGT_SCAN;

    bRet = MAPI_TRUE;

    return bRet;
}

EN_MW_DVB_SI_SGT_SCAN_STATE MW_DVB_SI_PSI_Parser::GetSgtScanState(void)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    return m_enSgtScanState;
}
MAPI_BOOL MW_DVB_SI_PSI_Parser:: _SGTScanWaitTable(void)
{
    if (m_s16OpenFilter<=0)
    {
        MAPI_BOOL bRet =MAPI_FALSE;
        if(m_stSgt.pSGTServiceListInfo== NULL)
        {
            MW_SI_SGT_SCAN_DEBUG("%s SGT Service List == NULL\n",__FUNCTION__);
            if(NULL != _pfScanResultNotify)
            {
                _pfScanResultNotify(E_DVB_SCAN_STOP, m_u32ScanResultNotifyParam1, NULL, NULL);
            }
            return MAPI_FALSE;
        }
        MW_SI_SGT_SCAN_DEBUG("SGT SCAN STATE : _SGTScanWaitTable \n");

        typedef std::list<ST_MW_SGT_TSID> SgtTsId_t;
        SgtTsId_t stSgtTsidlist;

        stSgtTsidlist.clear();
        m_DvbsScanTpMap.clear();

        for(ServiceList_t::iterator it=m_stSgt.pSGTServiceListInfo->begin();it!=m_stSgt.pSGTServiceListInfo->end();++it)
        {
            SgtTsId_t::iterator iter=stSgtTsidlist.begin();

            for(;iter!=stSgtTsidlist.end();++iter)
            {
                if(it->second.stTripleIds.u16TsId ==iter->u16TsId)
                {
                    MW_SI_SGT_SCAN_DEBUG("%s Service TSID %d Service List TSID %d\n",__FUNCTION__,it->second.stTripleIds.u16TsId,iter->u16TsId);
                    break;
                }
            }
            if(iter == stSgtTsidlist.end())
            {
                iter->u16TsId =it->second.stTripleIds.u16TsId;
                stSgtTsidlist.push_back(*iter);
            }
        }

        for(SgtTsId_t::iterator iter=stSgtTsidlist.begin();iter!=stSgtTsidlist.end();++iter)
        {
            MW_SI_SGT_SCAN_DEBUG("TSID %d\n",iter->u16TsId);

            SATELLITE_DESC_DEL_SYS_DATA *pSatelliteInfo = m_pSatelliteDeliveryInfo;
            if(pSatelliteInfo == NULL)
            {
                MW_SI_SGT_SCAN_DEBUG("%s Satellite Delivery Info is NULL\n",__FUNCTION__);
                return MAPI_FALSE;
            }
            while(pSatelliteInfo!=NULL)
            {
               if(pSatelliteInfo->u16TSID ==iter->u16TsId)
               {
                   break;
               }
               pSatelliteInfo= pSatelliteInfo->next;
            }

            if(pSatelliteInfo!=NULL)
            {
                if(pSatelliteInfo->u16TSID == m_stPat.u16TsId)
                {
                    m_u16SateScanTsid = pSatelliteInfo->u16TSID;
                    iter->bIsUsed = MAPI_TRUE;
                }
                else
                {
                    _AddDvbsScanTp(pSatelliteInfo->u16TSID,pSatelliteInfo->stSDS);
                }
                bRet = MAPI_TRUE;
            }
        }
        if(bRet == MAPI_TRUE)
        {
            m_enSgtScanState = E_SGT_SCAN_STATE_BUILD_CH;
        }
        else
        {
            if(NULL != _pfScanResultNotify)
            {
                _pfScanResultNotify(E_DVB_SCAN_STOP, m_u32ScanResultNotifyParam1, NULL, NULL);
            }
        }
        return bRet;
    }
    return MAPI_TRUE;
}
MAPI_BOOL MW_DVB_SI_PSI_Parser:: _SGTScanWaitLists(void)
{
     if (m_s16OpenFilter<=0)
     {
        if(m_ServiceListBasicInfo.size() ==0)
        {
            return MAPI_FALSE;
        }
        else
        {
#if 0
            /// List Basic Service Info;
            for(ServiceListBasicInfo_t::iterator it=m_ServiceListBasicInfo.begin();it!=m_ServiceListBasicInfo.end();++it)
            {
                MW_SI_SGT_SCAN_DEBUG("%d %s\n",it->second.u16ServiceListID,it->second.au8ServiceListName);
            }
#endif
            MW_SI_SGT_SCAN_DEBUG("SGT SCAN STATE : _SGTScanWaitLists \n");

            m_enSgtScanState=E_SGT_SCAN_STATE_CREATE_SGT_PARSER;
            m_enSgtFilterType = E_SGT_FITLER_TYPE_ALL;
            m_bRunning = MAPI_FALSE;

            if(_pfScanResultNotify)
            {
                _pfScanResultNotify(E_DVB_SCAN_LCN_LIST, m_u32ScanResultNotifyParam1, NULL, NULL);
            }
        }
     }
     return MAPI_TRUE;
}
MAPI_BOOL MW_DVB_SI_PSI_Parser:: _CreateSgtFilter(EN_SGT_FILTER_TYPE eSgtFilterType, U16 u16ServiceListId, U8 u8SgtVer)
{
    if(m_SGTLinkageInfo.size() ==0)
    {
        MW_SI_SGT_SCAN_DEBUG("%s No Linkage type 0x90,0x91,0x93 for Linkage descriptors \n",__FUNCTION__);
        return MAPI_FALSE;
    }
    if(eSgtFilterType == E_SGT_FITLER_TYPE_NUM)
    {
        MW_SI_SGT_SCAN_DEBUG("%s SGT parser create with wrong filter type\n",__FUNCTION__);
        return MAPI_FALSE;
    }
    for(NIT_LINKAGE_t::iterator it =m_SGTLinkageInfo.begin();it!= m_SGTLinkageInfo.end();++it)
    {
        for(int i=0;i<MAX_CHANNEL_IN_MUX;i++)
        {
            if((m_astAllPmt[i].wPrivatePID!=0)&&(m_astAllPmt[i].wServiceID == it->stTargetCh.u16SrvId))
            {
                MW_SI_SGT_SCAN_DEBUG("%s: PID  service id %d %d %d\n",__FUNCTION__,m_astAllPmt[i].wServiceID,it->stTargetCh.u16SrvId,m_astAllPmt[i].wPrivatePID);

                if(m_pSgtParser !=NULL)
                {
                    _DeleteParser((MAPI_U32)m_pSgtParser,MAPI_FALSE);
                }

                m_pSgtParser = new (std::nothrow) mapi_si_SGT_parser(m_pSi, m_pDemux);

                if(m_pSgtParser !=NULL)
                {
                    if(m_pSgtParser->Init(0x2000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_NORMAL, u8SgtVer)
                            && m_pSgtParser->Start(eSgtFilterType,SGT_SCAN_TIMEOUT,u16ServiceListId,m_astAllPmt[i].wPrivatePID))
                    {
                        m_s16OpenFilter++;
                        return MAPI_TRUE;
                    }
                    else
                    {
                        DELETE(m_pSgtParser);
                        ASSERT(0);
                        return MAPI_FALSE;
                    }
                }
                else
                {
                    ASSERT(0);
                    return MAPI_FALSE;
                }
            }
        }
    }
    return MAPI_FALSE;
}
MAPI_BOOL MW_DVB_SI_PSI_Parser:: _SGTScanSgtCreate(void)
{
    if(_CreateSgtFilter(m_enSgtFilterType, m_u16ServiceListId, INVALID_PSI_SI_VERSION))
    {
        if(m_enSgtFilterType == E_SGT_FITLER_TYPE_ALL)
        {
            m_enSgtScanState =E_SGT_SCAN_STATE_WAIT_TABLE;
        }
        else if(m_enSgtFilterType == E_SGT_FITLER_TYPE_SERVICE_LIST)
        {
            m_enSgtScanState = E_SGT_SCAN_STATE_WAIT_LISTS;
        }
        return MAPI_TRUE;
    }
    return MAPI_FALSE;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser:: _SGTScanWaitPatNitReady(void)
{
    if (m_s16OpenFilter<=0)
    {
        if(m_stNit.u8Version == INVALID_PSI_SI_VERSION)
        {
            MW_SI_SGT_SCAN_DEBUG("%s Nit Get Fail\n",__FUNCTION__);
            return MAPI_FALSE;
        }
        mapi_utility::freeList(&m_pSatelliteDeliveryInfo);
        _BuildDeliveryInfo(m_stNit, FALSE);

        MW_SI_SGT_SCAN_DEBUG("SGT SCAN STATE : _SGTScanWaitPatNitReady \n");
        if(m_pSatelliteDeliveryInfo == NULL)
        {
            MW_SI_SGT_SCAN_DEBUG("%s SatelliteDevliery Get  Fail \n",__FUNCTION__);
            return MAPI_FALSE;
        }

        if(m_stPat.u8Version == INVALID_PSI_SI_VERSION)
        {
            MW_SI_SGT_SCAN_DEBUG("%s PAT Get Fail\n",__FUNCTION__);

            return MAPI_FALSE;
        }
        if(m_enSgtSource == E_SGT_TP_SGT_LD)
        {
            m_enSgtScanState = E_SGT_SCAN_STATE_CREATE_SGT_PARSER;
            m_enSgtFilterType = E_SGT_FITLER_TYPE_SERVICE_LIST;
            return MAPI_TRUE;
        }
        EN_LINKAGE_TYPE eRetLinkType = E_LINKAGE_NONE;

        if(_CatLinkageInfo(E_LINKAGE_NIT)== MAPI_TRUE)
        {
            eRetLinkType=_BuildFullLinkage(E_LINKAGE_NIT);
            #if 0
            MW_SI_SGT_SCAN_DEBUG("Nit size %d SGT size  %d\n",m_NitLinkageInfo.size(),m_SGTLinkageInfo.size());
            for(NIT_LINKAGE_t::iterator it=m_NitLinkageInfo.begin();it!=m_NitLinkageInfo.end();++it)
            {
                MW_SI_SGT_SCAN_DEBUG("NIT ONID %d TSID %d Service ID %d ",it->stTargetCh.u16OnId,it->stTargetCh.u16TsId,it->stTargetCh.u16SrvId);
                MW_SI_SGT_SCAN_DEBUG("Freq %d\n",it->stMuxScanParam.u32Freq);
            }
            for(NIT_LINKAGE_t::iterator it=m_SGTLinkageInfo.begin();it!=m_SGTLinkageInfo.end();++it)
            {
                MW_SI_SGT_SCAN_DEBUG("SGT ONID %d TSID %d Service ID %d ",it->stTargetCh.u16OnId,it->stTargetCh.u16TsId,it->stTargetCh.u16SrvId);
                MW_SI_SGT_SCAN_DEBUG("Freq %d\n",it->stMuxScanParam.u32Freq);
            }
            #endif
            if(eRetLinkType == E_LINKAGE_SGT_CUR)
            {
                MW_SI_SGT_SCAN_DEBUG("%s Cur SGT!!!!\n",__FUNCTION__);

                m_enSgtScanState = E_SGT_SCAN_STATE_CREATE_SGT_PARSER;
                m_enSgtFilterType = E_SGT_FITLER_TYPE_SERVICE_LIST;
            }
            else
            {
                return MAPI_FALSE;
            }
        }
        else
        {
            return MAPI_FALSE;
        }

    }
    return MAPI_TRUE;
}

void MW_DVB_SI_PSI_Parser::_free_SI_TABLE_SGT(MAPI_SI_TABLE_SGT& Info)
{
    DELETE(Info.pSGTServiceListInfo);
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::_BuildChannelByNitAndSgt(const MAPI_SI_SGT_SERVICE_INFO& rSgtServiceInfo, DVB_PROG& stNewProgInfo)
{
    MAPI_U16 u16NitTsPos;
    if (m_stNit.u8Version == INVALID_PSI_SI_VERSION)
    {
        return MAPI_FALSE;
    }
    if (m_stSgt.u8Version == INVALID_PSI_SI_VERSION)
    {
        return MAPI_FALSE;
    }

    if (m_eParserBaseType != MW_DVB_SGT_BASE)
    {
        return MAPI_FALSE;
    }

    for (u16NitTsPos = 0; u16NitTsPos < m_stNit.u16TSNumber; u16NitTsPos++)
    {
        if (rSgtServiceInfo.stTripleIds.u16TsId == m_stNit.pstTSInfo[u16NitTsPos].wTransportStream_ID)
        {
            _SetProgramWithDeaultValue(stNewProgInfo);
            stNewProgInfo.u16ServiceID = rSgtServiceInfo.stTripleIds.u16SrvId;
            stNewProgInfo.stCHAttribute.u8NumericSelectionFlag = TRUE;
            stNewProgInfo.stCHAttribute.u8VisibleServiceFlag = rSgtServiceInfo.bIsVisble;
            stNewProgInfo.u16LCN = rSgtServiceInfo.u16LCN;
            stNewProgInfo.u16VirtualServiceID= rSgtServiceInfo.u16VirtualServiceID;
            stNewProgInfo.stCHAttribute.u8RealServiceType = rSgtServiceInfo.u8ServiceType;
            _UpdateServiceType(&stNewProgInfo.stCHAttribute.u8ServiceType,
                                    &stNewProgInfo.stCHAttribute.u8ServiceTypePrio,
                                    rSgtServiceInfo.u8ServiceType);
            __UpdateName(&stNewProgInfo, (MAPI_U8*)rSgtServiceInfo.au8ServiceName, m_pCMDB);

            _UpdateProgInfoByServiceType(rSgtServiceInfo.u8ServiceType, &stNewProgInfo, NULL);
            return MAPI_TRUE;
        }
    }
    return MAPI_FALSE;
}
void MW_DVB_SI_PSI_Parser::_UpdateServiceBySGTInfo(const MAPI_SI_SGT_SERVICE_INFO& rSgtServiceInfo, DVB_PROG* pProg)
{
    //if(m_bIsSIDynmaicReScanOff ==MAPI_FALSE)
    {
        pProg->stCHAttribute.u8IsScramble = rSgtServiceInfo.bIsServiceScrambled;
        __UpdateName(pProg, (MAPI_U8*)rSgtServiceInfo.au8ServiceName, m_pCMDB);
        pProg->stCHAttribute.u8RealServiceType = rSgtServiceInfo.u8ServiceType;
        if(pProg->u16Number != ASTRA_HD_LCN_CONFLICT_PROGRAM_NUMBER)
        {
            pProg->stCHAttribute.u8VisibleServiceFlag = rSgtServiceInfo.bIsVisble;
            pProg->stCHAttribute.u8NumericSelectionFlag = rSgtServiceInfo.bIsVisble;
        }
        _UpdateServiceType(&pProg->stCHAttribute.u8ServiceType, &pProg->stCHAttribute.u8ServiceTypePrio,
                                                    rSgtServiceInfo.u8ServiceType);

        m_pCMDB->Update(pProg);
    }
}
enum EN_SI_SERVICE_COMPARE_RESULT
{
    E_SAME_SERVICE,
    E_DIFF_SERVICE,
    E_MOVE_SERVICE_CANDIDATE
};


EN_SI_SERVICE_COMPARE_RESULT CompareService(const MAPI_SI_SGT_SERVICE_INFO& rSgtServiceInfo, const ST_DVB_PROGRAMINFO* pProg,  DVB_CM* pCMDB)
{
    if(pProg->u16VirtualServiceID != rSgtServiceInfo.u16VirtualServiceID)
    {
        return E_DIFF_SERVICE;
    }

    if(pProg->u16LCN == rSgtServiceInfo.u16LCN)
    {
        if(IsSameTripleId(rSgtServiceInfo, pProg, (DVB_CM *)pCMDB))
        {
            return E_SAME_SERVICE;
        }
        else
        {
            return E_MOVE_SERVICE_CANDIDATE;
        }
    }

    return E_DIFF_SERVICE;
}
static S32 _CheckSgtServiceAddOrMove(const MAPI_SI_SGT_SERVICE_INFO& rSgtServiceInfo, DVB_CM* pCMDB, BOOL& bNewService, list<ST_DVB_PROGRAMINFO*>& cServiceMoveList)
{
    ST_DVB_PROGRAMINFO* pProg = NULL;
    U32  cmdbIdx = 0;
    pProg = pCMDB->GetByIndex(cmdbIdx);
    while(pProg)
    {
        EN_SI_SERVICE_COMPARE_RESULT enSrvCmp = CompareService(rSgtServiceInfo, pProg,pCMDB);
        if(enSrvCmp == E_SAME_SERVICE)
        {
            return cmdbIdx;
        }
        else if(enSrvCmp == E_MOVE_SERVICE_CANDIDATE)
        {
            cServiceMoveList.push_back(pProg);
        }
        cmdbIdx++;
        pProg = pCMDB->GetByIndex(cmdbIdx);
    }
    bNewService = ((cServiceMoveList.size() ==0) && rSgtServiceInfo.bIsNewService);
    return -1;
}
void MW_DVB_SI_PSI_Parser::_SgtReady()
{
    _free_SI_TABLE_SGT(m_stSgt);
    BOOL bGetTable = m_pSgtParser->GetTable(m_stSgt);
    _DeleteParser((MAPI_U32)m_pSgtParser, MAPI_FALSE);

    if(bGetTable == FALSE)
    {
        return;
    }

    if(m_stSgt.u8Version == INVALID_PSI_SI_VERSION)
    {
        return;
    }

    if((E_DVB_MONITOR == m_eParserMode) && (m_eParserBaseType == MW_DVB_SGT_BASE))
    {
        if((m_stSgt.pSGTServiceListInfo == NULL) || m_stSgt.pSGTServiceListInfo->size() == 0)
        {
            return;
        }
        if(__GetVer(m_pCurProg, EN_VER_SGT) == m_stSgt.u8Version)
        {
            return;
        }

        MW_DTV_CM_DB_scope_lock lock(m_pCMDB);

        __SetVer(m_pCurProg, EN_VER_SGT, m_stSgt.u8Version) ;
        m_pCMDB->Update(m_pCurProg);

        ServiceList_t::iterator sgtSrvLstIt;
        MAPI_BOOL bIsChannelMove =MAPI_FALSE;
        for(sgtSrvLstIt = m_stSgt.pSGTServiceListInfo->begin(); sgtSrvLstIt != m_stSgt.pSGTServiceListInfo->end(); ++sgtSrvLstIt)
        {
            BOOL bIsNewService = FALSE;
            list<ST_DVB_PROGRAMINFO*> cServiceMoveCandidateList;
            S32 s32SameSrvIdx = _CheckSgtServiceAddOrMove(sgtSrvLstIt->second, m_pCMDB, bIsNewService, cServiceMoveCandidateList);
            if( s32SameSrvIdx >=0 ) // same service
            {
                _UpdateServiceBySGTInfo(sgtSrvLstIt->second, m_pCMDB->GetByIndex(s32SameSrvIdx));
            }
            else if(bIsNewService)
            {
                m_NewSGTServiceListInfo.insert(ServiceList_t::value_type(sgtSrvLstIt->second.u16VirtualServiceID,sgtSrvLstIt->second));
            }
            else if( cServiceMoveCandidateList.size() > 0)
            {
                _MoveChannelBySgtInfo(cServiceMoveCandidateList, sgtSrvLstIt->second);
                bIsChannelMove =MAPI_TRUE;

            }
        }
        if(bIsChannelMove== MAPI_TRUE)
        {
            m_pCMDB->Sort();
        }
        if(m_NewSGTServiceListInfo.size()>0)
        {
            MONITOR_NOTIFY(E_DVB_ASTRA_NEW_SERVICE, NULL, m_pMonitorNotifyUsrParam, NULL);
        }
    }
}
static void SetMuxInfoBySatDeliveryInfo(U16 u16ONID, U16 u16NID, U16 u16TSID, U8 u8SatID, const MS_SATELLITE_PARAMETER& stInfo, ST_DVB_MUX_INFO& stMuxInfo)
{
    memset(&stMuxInfo,0,sizeof(ST_DVB_MUX_INFO));
    stMuxInfo.u16TransportStream_ID = u16TSID;
    stMuxInfo.u16OriginalNetwork_ID = u16ONID;
    stMuxInfo.u16Network_ID = u16NID;
    //stMuxInfo.u8RfNumber = m_u8RFCh;
    stMuxInfo.u32Frequency = stInfo.u32Freq / 100;
    stMuxInfo.u8ModulationMode = (stInfo.u8Modulation_type& 0x3);
    stMuxInfo.u32SymbRate = stInfo.u32Symbol_rate / 10;
    stMuxInfo.bPolarity = ((stInfo.u8polarization != 0) ? 1 : 0);
    stMuxInfo.u8SatID = u8SatID;
}
BOOL MW_DVB_SI_PSI_Parser::_SetMuxBySatDelvieryInfo(U16 u16TSID, U16 u16ONID, DVB_MUX& stMuxInfo)
{
    memset(&stMuxInfo,0,sizeof(ST_DVB_MUX_INFO));
    SATELLITE_DESC_DEL_SYS_DATA* pSatDeliveryInfoList = NULL;
    if((m_stNit.u8Version != INVALID_PSI_SI_VERSION) && (NULL != m_stNit.pSatelliteDeliveryInfo))
    {
        pSatDeliveryInfoList = m_stNit.pSatelliteDeliveryInfo;
    }
    else if(NULL != m_pSatelliteDeliveryInfo)
    {
        pSatDeliveryInfoList = m_pSatelliteDeliveryInfo;
    }

    ST_DVB_SAT_INFO* pSat = m_pCMDB->GetSat(((ST_DVB_MUX_INFO*)m_pCurMux)->m_u16SatTableID);
    if((pSatDeliveryInfoList != NULL) && (pSat != NULL))
    {
        MS_SATELLITE_PARAMETER stInfo;
        if(this->GetSatelliteDeliveryInfoByID(pSatDeliveryInfoList, stInfo, u16TSID))
        {
            SetMuxInfoBySatDeliveryInfo(u16ONID, m_stNit.u16NetworkID, u16TSID, ((ST_DVB_MUX_INFO*)m_pCurMux)->u8SatID, stInfo, stMuxInfo);
            return TRUE;
        }
    }
    return FALSE;
}
void MW_DVB_SI_PSI_Parser::_MoveChannelBySgtInfo(std::list<DVB_PROG*>& MovingProgInfoList, const MAPI_SI_SGT_SERVICE_INFO& rSgtServiceInfo)
{
    if( MovingProgInfoList.size() > 0)
    {
        ST_DVB_PROGRAMINFO* pstProgInfo = MovingProgInfoList.front();
        if(pstProgInfo != NULL)
        {
            ST_DVB_PROGRAMINFO stNewProgInfo;
            memcpy(&stNewProgInfo, pstProgInfo, sizeof(ST_DVB_PROGRAMINFO));
            stNewProgInfo.u16ServiceID= rSgtServiceInfo.stTripleIds.u16SrvId;
            stNewProgInfo.stCHAttribute.u8VisibleServiceFlag = rSgtServiceInfo.bIsVisble;
            memcpy(&stNewProgInfo.u8ServiceName,&rSgtServiceInfo.au8ServiceName,sizeof(rSgtServiceInfo.au8ServiceName));
            _AddChannelBySgtInfo(stNewProgInfo, rSgtServiceInfo);
            list<ST_DVB_PROGRAMINFO*>::iterator it;
            for(it = MovingProgInfoList.begin(); it != MovingProgInfoList.end(); it++)
            {
                m_pCMDB->Delete(*it);
            }
        }
        MovingProgInfoList.clear();
    }
}
DVB_PROG* MW_DVB_SI_PSI_Parser::_AddChannelBySgtInfo(DVB_PROG& stNewProgInfo, const MAPI_SI_SGT_SERVICE_INFO& rSgtServiceInfo)
{
    ST_DVB_NETWORK_INFO stNetworkInfo;
    memset(&stNetworkInfo, 0, sizeof(ST_DVB_NETWORK_INFO));
    stNetworkInfo.m_u16NetworkID = m_stNit.u16NetworkID;

    ST_DVB_MUX_INFO* pMux = m_pCMDB->GetMux(rSgtServiceInfo.stTripleIds.u16TsId, rSgtServiceInfo.stTripleIds.u16OnId);
    if(pMux) //existent TP
    {
        ST_DVB_SAT_INFO* pSat = m_pCMDB->GetSat(pMux->m_u16SatTableID);
        return m_pCMDB->Add(TRUE, stNewProgInfo, *pMux, stNetworkInfo, *pSat);
    }
    else //New TP
    {
        ST_DVB_MUX_INFO stMuxInfo;
        if(_SetMuxBySatDelvieryInfo(rSgtServiceInfo.stTripleIds.u16TsId, rSgtServiceInfo.stTripleIds.u16OnId, stMuxInfo))
        {
            ST_DVB_SAT_INFO* pSat = m_pCMDB->GetSat(((ST_DVB_MUX_INFO*)m_pCurMux)->m_u16SatTableID);
            return m_pCMDB->Add(TRUE, stNewProgInfo, stMuxInfo, stNetworkInfo, *pSat);
        }
    }
    return NULL;
}

void MW_DVB_SI_PSI_Parser::_AddChannelsBySgtInfo()
{
    if( (m_pCurMux!=NULL))
    {
        MW_DTV_CM_DB_scope_lock lock(m_pCMDB);
        list< ST_DVB_PROGRAMINFO* > stlNewAstraServiceList;
        ServiceList_t::iterator it;
        for(it = m_NewSGTServiceListInfo.begin(); it != m_NewSGTServiceListInfo.end(); ++it)
        {
            ST_DVB_PROGRAMINFO stNewProgInfo;
            memset(&stNewProgInfo, 0, sizeof(ST_DVB_PROGRAMINFO));
            if (_BuildChannelByNitAndSgt(it->second, stNewProgInfo))
            {
                ST_DVB_PROGRAMINFO* pProg = _AddChannelBySgtInfo(stNewProgInfo, it->second);
                if(pProg != NULL)
                {
                    stlNewAstraServiceList.push_back(pProg);
                }
            }
        }
        if(stlNewAstraServiceList.size() > 0)
        {
            ArrangeNewAstraService(m_pCMDB, stlNewAstraServiceList);
            m_pCMDB->Sort();
        }

    }
    m_NewSGTServiceListInfo.clear();
}

void MW_DVB_SI_PSI_Parser:: _SGT_Monitor()
{
    if (m_eParserBaseType != MW_DVB_SGT_BASE)
    {
        return;
    }
#if (OAD_ENABLE == 1)
    if(m_bOadStopEit == MAPI_TRUE)
    {
        return;
    }
#endif
    if((_SGT_PMT_Monitor()== MAPI_TRUE)&&(m_pSgtParser == NULL) && (m_pCurProg != NULL) )
    {
        if((m_u32SgtMonitorTimer == 0) || (_Timer_DiffTimeFromNow(m_u32SgtMonitorTimer) > NIT_MONITOR_PERIOD))
        {
            if(_CreateSgtFilter(E_SGT_FITLER_TYPE_ALL, m_pCurProg->u16SgtServiceListId, m_stSgt.u8Version))
            {
                m_u32SgtMonitorTimer = _GetTime0();
            }
        }
    }
}
MAPI_U8 MW_DVB_SI_PSI_Parser::GetSgtBasicServicelistInfoSize(void)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    ASSERT(m_bInit);
    return m_ServiceListBasicInfo.size();
}
void MW_DVB_SI_PSI_Parser:: SetServiceListId(MAPI_U16 u16ServiceListId)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    ASSERT(m_bInit);

    m_u16ServiceListId =u16ServiceListId;
}
void MW_DVB_SI_PSI_Parser::ConfirmAstraNewServices(BOOL bAddService)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    ASSERT(m_bInit);
    if(bAddService)
    {
        _AddChannelsBySgtInfo();
    }
    m_NewSGTServiceListInfo.clear();
}
void MW_DVB_SI_PSI_Parser::_ResetSgtScanInfo(void)
{
    m_NitLinkageInfo.clear();
    m_SGTLinkageInfo.clear();
    m_enSgtSource = E_SGT_TP_NUM;
}
void MW_DVB_SI_PSI_Parser::_ReMoveSgtMisMatchChannel(void)
{
    for (MAPI_U16 u16CmdbTsPos=0; u16CmdbTsPos<m_pCMDB->GetMaxMuxNum(); u16CmdbTsPos++)
    {
        DVB_MUX *pstMuxInfo =NULL;
        pstMuxInfo = m_pCMDB->GetValidMux(u16CmdbTsPos);
        if (pstMuxInfo)
        {
            MAPI_SI_SERVICE_ID_INFO ServiceIDInfo[MAX_CHANNEL_IN_MUX];
            memset(&ServiceIDInfo, 0, sizeof(MAPI_SI_SERVICE_ID_INFO)*MAX_CHANNEL_IN_MUX);
            MAPI_U16 u16TSID =pstMuxInfo->u16TransportStream_ID;
            int i=0;
            ServiceList_t::iterator sgtSrvLstIt;
            for(sgtSrvLstIt = m_stSgt.pSGTServiceListInfo->begin(); sgtSrvLstIt != m_stSgt.pSGTServiceListInfo->end(); ++sgtSrvLstIt)
            {
                if(sgtSrvLstIt->second.stTripleIds.u16TsId ==u16TSID)
                {
                    ServiceIDInfo[i].u16ServiceID=sgtSrvLstIt->second.stTripleIds.u16SrvId;
                    i++;
                }
            }
            _RemoveMismatchCH(m_pCurProg, m_pCMDB,
                                            ServiceIDInfo, pstMuxInfo,
                                            m_pCurNetwork);
        }
    }
}
#endif

MW_DVB_PARSER_CALLBACK_INFO::MW_DVB_PARSER_CALLBACK_INFO()
{
    pCallbackReference = NULL;
    PMTSectionNotify = NULL;
    PMTSectionNotifyCA = NULL;
    CIProtectionNotify = NULL;
    EventHandler = NULL;
    pSiEvent = NULL;
    pEitEvent = NULL;
    pDvbSiPsiParser = NULL;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::AddRemoveSpecialServiceInfo(ST_TRIPLE_ID & stTripleid, MAPI_BOOL bAdd)
{
    MAPI_BOOL bRet = MAPI_FALSE;
    if((stTripleid.u16TsId != INVALID_TS_ID)&&(stTripleid.u16OnId != INVALID_ON_ID)&&(stTripleid.u16SrvId != INVALID_SERVICE_ID))
    {
        mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
        int iUnUsed_Index = MAX_SPECIAL_SERVICE_NUM, j = 0;

        for(j = 0; j < MAX_SPECIAL_SERVICE_NUM; j++)
        {
            if((iUnUsed_Index == MAX_SPECIAL_SERVICE_NUM)&&(m_astSpecialService[j].u16SrvId == INVALID_SERVICE_ID))
            {
                iUnUsed_Index = j;
            }
            if((m_astSpecialService[j].u16TsId == stTripleid.u16TsId)&&
                (m_astSpecialService[j].u16OnId == stTripleid.u16OnId)&&
                (m_astSpecialService[j].u16SrvId == stTripleid.u16SrvId))
            {
                break;
            }
        }
        if(j >= MAX_SPECIAL_SERVICE_NUM)
        {
            if(bAdd == MAPI_TRUE)
            {
                printf("Add SpecialService :> TSID : 0x%x  ONID : 0x%x  SID : 0x%x\n",stTripleid.u16TsId,stTripleid.u16OnId,stTripleid.u16SrvId);
                memcpy(&m_astSpecialService[iUnUsed_Index], &stTripleid, sizeof(ST_TRIPLE_ID));
                bRet = MAPI_TRUE;
            }
        }
        else
        {
            if(bAdd == MAPI_FALSE)
            {
                printf("Remove SpecialService :> TSID : 0x%x  ONID : 0x%x  SID : 0x%x\n",stTripleid.u16TsId,stTripleid.u16OnId,stTripleid.u16SrvId);
                _DeleteParser((MAPI_U32)m_pSpecialServicesParser[j], MAPI_FALSE);
                m_astSpecialService[j].u16TsId = INVALID_TS_ID;
                m_astSpecialService[j].u16OnId = INVALID_ON_ID;
                m_astSpecialService[j].u16SrvId = INVALID_SERVICE_ID;
                bRet = MAPI_TRUE;
            }
        }
    }

    return bRet;
}

void MW_DVB_SI_PSI_Parser::_UpdateSpecialServicePmtInfo(DVB_PROG *pProg, DVB_CM *pCMDB, DVB_MUX *pMux, MAPI_SI_TABLE_PMT & stPmt)
{
    if((pProg == NULL)||(pCMDB == NULL)||(pMux == NULL))
    {
        return;
    }
    if((m_stPat.u16ServiceCount > 0)&&(stPmt.u8Version != INVALID_PSI_SI_VERSION))
    {
        MW_DVB_SI_SpecialService_Info info;

        printf("special service Pmt version :> 0x%x(0x%x)\n",stPmt.u8Version,__GetVer(pProg, EN_VER_PMT));
        if(stPmt.u8Version != __GetVer(pProg, EN_VER_PMT))
        {
            __SetVer(pProg, EN_VER_PMT, stPmt.u8Version);
            MAPI_BOOL bIsChanged;
            memset(&info.stServiceInfo, 0, sizeof(MW_DVB_SI_ServicelInfo));
            for(int i = 0; i < m_stPat.u16ServiceCount; i++)
            {
                    if(stPmt.wServiceID == m_stPat.ServiceIDInfo[i].u16ServiceID)
                {
                    info.stServiceInfo.wPmtPID = m_stPat.ServiceIDInfo[i].u16PmtPID;
                    break;
                }
            }
            _UpdateServiceInfo(MW_SERVICE_PMT_CHANGED, stPmt, info.stServiceInfo);
            if(info.stServiceInfo.bIsCAExist != pProg->stCHAttribute.u8IsScramble)
            {
                bIsChanged = MAPI_TRUE;
            }
            if(!__UpdateService(pProg, &info.stServiceInfo, pCMDB, MAPI_TRUE))
            {
                printf("other service update program fail\n");
            }
            info.u16TSID = pMux->u16TransportStream_ID;
            info.u16ONID = pMux->u16OriginalNetwork_ID;
            info.u16SID = stPmt.wServiceID;
            MONITOR_NOTIFY(E_DVB_SPECIAL_SERVICE_UPDATE, &info, m_pMonitorNotifyUsrParam, &bIsChanged);
        }
    }
}

void MW_DVB_SI_PSI_Parser::_PMT_SpecialService_Monitor(void)
{
    if(E_DVB_MONITOR != m_eParserMode)
    {
        return;
    }

    if(m_stPat.u8Version != INVALID_PSI_SI_VERSION)
    {
        if((m_u32SpecialService_MonitorTimer == 0) || (_Timer_DiffTimeFromNow(m_u32SpecialService_MonitorTimer) > PMT_OTHER_MONITOR_PERIOD))
        {
            for(int i = 0; i < MAX_SPECIAL_SERVICE_NUM; i++)
            {
                MAPI_BOOL bIsSpecialService = MAPI_FALSE;
                MAPI_U16 u16PmtPID = INVALID_PID;
                for(int j = 0; j < m_stPat.u16ServiceCount; j++)
                {
                    if((m_stPat.ServiceIDInfo[j].u16ServiceID == m_astSpecialService[i].u16SrvId) &&
                        (m_stPat.u16TsId == m_astSpecialService[i].u16TsId) &&
                        (m_stPat.ServiceIDInfo[j].u16ServiceID != __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_SID)))
                    {
                            bIsSpecialService = MAPI_TRUE;
                            u16PmtPID = m_stPat.ServiceIDInfo[j].u16PmtPID;
                        break;
                    }
                }
                if(bIsSpecialService == MAPI_TRUE)
                {
                    if(m_pSpecialServicesParser[i] == NULL)
                    {
                        m_pSpecialServicesParser[i] = new (std::nothrow) mapi_si_PMT_parser(m_pSi, m_pDemux);
                        if(m_pSpecialServicesParser[i])
                        {
                            //version wait to do
                            if(m_pSpecialServicesParser[i]->Init(0x2000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_NORMAL, INVALID_PSI_SI_VERSION)
                                    && m_pSpecialServicesParser[i]->Start(PMT_MONITOR_TIMEOUT, m_astSpecialService[i].u16SrvId, u16PmtPID, 0))
                            {
                                printf("PVR Record Pmt filter :> PmtPID : 0x%x, SID 0x%x\n",u16PmtPID,m_astSpecialService[i].u16SrvId);
                                m_s16OpenFilter++;
                            }
                            else
                            {
                                delete m_pSpecialServicesParser[i];
                                m_pSpecialServicesParser[i] = NULL;
                            }
                        }
                    }
                }
            }
            m_u32SpecialService_MonitorTimer = _GetTime0();
        }
    }
}

//for oad download lost DDB and update fail issue
void MW_DVB_SI_PSI_Parser::SetStartEITMonitor(void)
{
#if (OAD_ENABLE == 1)
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    m_bOadStopEit = MAPI_FALSE;
#endif
}
void MW_DVB_SI_PSI_Parser::SetStopEITMonitor(void)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    m_bOadStopEit = MAPI_TRUE;
    if(m_pEitPFParser)
    {
        _DeleteParser((MAPI_U32)m_pEitPFParser , MAPI_FALSE);
    }
    if(m_pEitPfAllParser)
    {
        _DeleteParser((MAPI_U32)m_pEitPfAllParser , MAPI_FALSE);
    }
    if(m_pEitSch1Parser)
    {
        _DeleteParser((MAPI_U32)m_pEitSch1Parser , MAPI_FALSE);
    }
    if(m_pEitSch2Parser)
    {
        _DeleteParser((MAPI_U32)m_pEitSch2Parser , MAPI_FALSE);
    }
    if(m_pSdtParser)
    {
        _DeleteParser((MAPI_U32)m_pSdtParser , MAPI_FALSE);
    }
    if(m_pNitParser)
    {
        _DeleteParser((MAPI_U32)m_pNitParser , MAPI_FALSE);
    }
#if (ASTRA_SGT_ENABLE == 1)
    if(m_pSgtParser)
    {
        _DeleteParser((MAPI_U32)m_pSgtParser , MAPI_FALSE);
    }
#endif
    if(m_pBatParser)
    {
        _DeleteParser((MAPI_U32)m_pBatParser , MAPI_FALSE);
    }
    if(m_pSdtOtherParser)
    {
        _DeleteParser((MAPI_U32)m_pSdtOtherParser , MAPI_FALSE);
    }
    if(m_pAitParser)
    {
        _DeleteParser((MAPI_U32)m_pAitParser , MAPI_FALSE);
    }
    if(m_pPatParser)
    {
        _DeleteParser((MAPI_U32)m_pPatParser , MAPI_FALSE);
    }
    if(m_pCurPmtParser)
    {
        _DeleteParser((MAPI_U32)m_pCurPmtParser , MAPI_FALSE);
    }
    if(m_pOtherPmtParser)
    {
        _DeleteParser((MAPI_U32)m_pOtherPmtParser , MAPI_FALSE);
    }
#if ((OAD_ENABLE == 1) && (SDTT_OAD_ENABLE==1))
    if(m_pSdttParser)
    {
        _DeleteParser((MAPI_U32)m_pSdttParser , MAPI_FALSE);
    }
#endif
}
//for oad download lost DDB and update fail issue end

#if ((OAD_ENABLE == 1) && (SDTT_OAD_ENABLE==1))
void MW_DVB_SI_PSI_Parser::_SDTT_Monitor()
{

    if(m_pSdttParser == NULL)
    {
        if((m_u32SdttMonitorTimer == 0) || (_Timer_DiffTimeFromNow(m_u32SdttMonitorTimer) > SDTT_MONITOR_PERIOD))
        {
            m_pSdttParser = new(std::nothrow) mapi_si_SDTT_parser(m_pSi, m_pDemux);
            if(m_pSdttParser)
            {
                MAPI_U8 u8Version;
                m_SDTT_PID = (m_SDTT_PID == PID_SDTTA) ? PID_SDTTB : PID_SDTTA;
                u8Version = (m_SDTT_PID == PID_SDTTA) ? m_u8CurSdttAVer : m_u8CurSdttBVer;
                if(m_pSdttParser->Init(0x2000, _ParserCallback, (MAPI_U32)&m_ParserCallBackInfo,  EN_SI_PSI_PARSER_NORMAL, u8Version)
                        && m_pSdttParser->Start(m_SDTT_PID, SDTT_MONITOR_TIMEOUT))
                {
                    m_u32SdttMonitorTimer = _GetTime0();
                    m_s16OpenFilter++;
                }
                else
                {
                    delete m_pSdttParser;
                    m_pSdttParser = NULL;
                }
            }
        }
    }

}

void MW_DVB_SI_PSI_Parser::_SdttReady(void)
{
    //printf("EN_SI_PSI_EVENT_SDTT_SECTION_READY \n");

    MAPI_SI_TABLE_SDTT stSdtt = {};
    MW_OAD_SDTT_INFO stOadInfo = {};
    MAPI_BOOL bGot = MAPI_FALSE;
    MAPI_U8 u8TimeInfoCount = 0;
    MAPI_U32 u32Duration = 0;
    MAPI_U8 *pu8CurSdttVer = NULL;
    MAPI_U8 u8CustomerMakerID = INVALID_MAKER_ID, u8CustomerModelID = INVALID_MAKER_ID;
    MAPI_U16 u16APSWVersion = 0;

    memset(&stSdtt, 0, sizeof(MAPI_SI_TABLE_SDTT));
    if (MAPI_FALSE == m_pSdttParser->GetTable(stSdtt))
    {
        _DeleteParser((MAPI_U32)m_pSdttParser, MAPI_FALSE);
         MW_SI_PARSER_MESSAGE("_DeleteParser((MAPI_U32)m_pSdttParser, MAPI_FALSE); \n");
        return;
    }

    _DeleteParser((MAPI_U32)m_pSdttParser, MAPI_FALSE);

    u8CustomerMakerID = SystemInfo::GetInstance()->getOADCustomerMakerID();
    u8CustomerModelID = SystemInfo::GetInstance()->getOADCustomerModelID();
    u16APSWVersion = SystemInfo::GetInstance()->getOADAPSWVersion();

    if(E_DVB_MONITOR == m_eParserMode)
    {
        pu8CurSdttVer = (m_SDTT_PID == PID_SDTTA) ? &m_u8CurSdttAVer : &m_u8CurSdttBVer;
        if (*pu8CurSdttVer != stSdtt.u8Version)
        {
            *pu8CurSdttVer = stSdtt.u8Version;
            if((stSdtt.stTable_id_ext.u8Maker_id != INVALID_MAKER_ID) && (stSdtt.stTable_id_ext.u8Model_id != INVALID_MODEL_ID))
            {
                if((stSdtt.stTable_id_ext.u8Maker_id == u8CustomerMakerID) && (stSdtt.stTable_id_ext.u8Model_id == u8CustomerModelID))
                {
                    for(MAPI_U8 i = 0; i < stSdtt.u8NumberOfContent; i++)
                    {
                        if((u16APSWVersion < stSdtt.pstSdttContent[i].u16New_version)&&((stSdtt.pstSdttContent[i].u8Version_indicator == E_ALL_VERSIONS) ||
                            ((stSdtt.pstSdttContent[i].u8Version_indicator == E_LATER_TARGET_VERSION)&&(stSdtt.pstSdttContent[i].u16Target_version <= u16APSWVersion)) ||
                            ((stSdtt.pstSdttContent[i].u8Version_indicator == E_EARLIER_TARGET_VERSION)&&(stSdtt.pstSdttContent[i].u16Target_version >= u16APSWVersion)) ||
                            ((stSdtt.pstSdttContent[i].u8Version_indicator == E_SAME_TARGET_VERSION)&&(stSdtt.pstSdttContent[i].u16Target_version == u16APSWVersion))))
                        {
                            stOadInfo.u16TSID = stSdtt.u16Transport_stream_id;
                            stOadInfo.u16ONID = stSdtt.u16Original_network_id;
                            stOadInfo.u16SID = stSdtt.u16Service_id;
                            if(stSdtt.pstSdttContent[i].u8Download_level == 0)
                            {
                                stOadInfo.bCompulsory = MAPI_FALSE;
                            }
                            else
                            {
                                stOadInfo.bCompulsory = MAPI_TRUE;
                            }
                            stOadInfo.u8TimeShift = stSdtt.pstSdttContent[i].u8Schedule_time_shift_information;
                            u8TimeInfoCount = (MAX_SCHEDULE_TIME_INFO > MAPI_SI_MAX_SCHEDULE_TIME_INFO) ? MAPI_SI_MAX_SCHEDULE_TIME_INFO : MAX_SCHEDULE_TIME_INFO;
                            for(MAPI_U8 j = 0; j < stSdtt.pstSdttContent[i].u8TimeInfoCount; j++)
                            {
                                stOadInfo.stTime_info[j].u32StartTime = mapi_dvb_utility::SI_MJDUTC2Seconds(stSdtt.pstSdttContent[i].stTime_info[j].u8Start_time , NULL);
                                u32Duration = mapi_dvb_utility::SI_UTC2Seconds(stSdtt.pstSdttContent[i].stTime_info[j].u8Duration);
                                stOadInfo.stTime_info[j].u32EndTime = stOadInfo.stTime_info[j].u32StartTime+u32Duration;
                            }
                            bGot = MAPI_TRUE;
                            break;
                        }
                    }
                }
            }
            else //if((m_stSdtt->stTable_id_ext.u8Maker_id == INVALID_MAKER_ID) || (m_stSdtt->stTable_id_ext.u8Model_id == INVALID_MODEL_ID))
            {
                for(MAPI_U8 i = 0; i < stSdtt.u8NumberOfContent; i++)
                {
                    if(stSdtt.pstSdttContent[i].sdtt_download_content_descriptor.bCompatibility_flag)
                    {
                        int k;
                        for(k = 0; k < stSdtt.pstSdttContent[i].sdtt_download_content_descriptor.stCompatibilityDescriptor.u16DescriptorCount; k++)
                        {
                            if((stSdtt.pstSdttContent[i].sdtt_download_content_descriptor.stCompatibilityDescriptor.stData[k].u8SpecifierType != ARIB_SPECIFIER_TYPE) ||
                                ((stSdtt.pstSdttContent[i].sdtt_download_content_descriptor.stCompatibilityDescriptor.stData[k].u8SpecifierData[0] != ((ARIB_CODE >> 16) & 0xFF)) ||
                                (stSdtt.pstSdttContent[i].sdtt_download_content_descriptor.stCompatibilityDescriptor.stData[k].u8SpecifierData[1] != ((ARIB_CODE >> 8) & 0xFF)) ||
                                (stSdtt.pstSdttContent[i].sdtt_download_content_descriptor.stCompatibilityDescriptor.stData[k].u8SpecifierData[2] != (ARIB_CODE & 0xFF))))
                            {
                                break;
                            }

                            if(stSdtt.pstSdttContent[i].sdtt_download_content_descriptor.stCompatibilityDescriptor.stData[k].u16Model != ((u8CustomerMakerID << 8) | u8CustomerModelID))
                            {
                                break;
                            }
                        }
                        if(k >= stSdtt.pstSdttContent[i].sdtt_download_content_descriptor.stCompatibilityDescriptor.u16DescriptorCount)
                        {
                            stOadInfo.u16TSID = stSdtt.u16Transport_stream_id;
                            stOadInfo.u16ONID = stSdtt.u16Original_network_id;
                            stOadInfo.u16SID = stSdtt.u16Service_id;
                            if(stSdtt.pstSdttContent[i].u8Download_level == 0)
                            {
                                stOadInfo.bCompulsory = MAPI_FALSE;
                            }
                            else
                            {
                                stOadInfo.bCompulsory = MAPI_TRUE;
                            }
                            stOadInfo.u8TimeShift = stSdtt.pstSdttContent[i].u8Schedule_time_shift_information;
                            u8TimeInfoCount = (MAX_SCHEDULE_TIME_INFO > MAPI_SI_MAX_SCHEDULE_TIME_INFO) ? MAPI_SI_MAX_SCHEDULE_TIME_INFO : MAX_SCHEDULE_TIME_INFO;
                            for(MAPI_U8 j = 0; j < u8TimeInfoCount; j++)
                            {
                                stOadInfo.stTime_info[j].u32StartTime = mapi_dvb_utility::SI_MJDUTC2Seconds(stSdtt.pstSdttContent[i].stTime_info[j].u8Start_time , NULL);
                                u32Duration = mapi_dvb_utility::SI_UTC2Seconds(stSdtt.pstSdttContent[i].stTime_info[j].u8Duration);
                                stOadInfo.stTime_info[j].u32EndTime = stOadInfo.stTime_info[j].u32StartTime+u32Duration;
                            }
                            bGot = MAPI_TRUE;
                            break;
                        }
                    }

                }

            }
            if(bGot == MAPI_TRUE)
            {
                if(m_OADParser != NULL)
                {
                    m_OADParser->SetOadInfo(stOadInfo);
                }
            }
        }
    }
    if(stSdtt.pstSdttContent != NULL)
    {
        FREE(stSdtt.pstSdttContent);
    }

}

void MW_DVB_SI_PSI_Parser::ResetSdttVersion(void)
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    m_u8CurSdttAVer = INVALID_PSI_SI_VERSION;
    m_u8CurSdttBVer = INVALID_PSI_SI_VERSION;
}
#endif

void MW_DVB_SI_PSI_Parser::_AddTargetRegionInfo(MW_DVB_TARGET_REGION_INFO &stDvbTargetRegionInfo, TARGET_REGION_INFO *pstTargetRegionInfo)
{
    if(pstTargetRegionInfo == NULL)
    {
        return;
    }

    for(int i=0; i<pstTargetRegionInfo->u8country_num; i++)
    {
        MAPI_S16 s16CountryIndex = _AddTargetCountry(stDvbTargetRegionInfo, pstTargetRegionInfo->stCountryinfo[i].u8CountryCode);
        if(s16CountryIndex >= 0)
        {
            for(int j=0; j<pstTargetRegionInfo->stCountryinfo[i].u8primary_region_num; j++)
            {
                MAPI_S16 s16PrimaryIndex = _AddTargetPrimaryRegion(stDvbTargetRegionInfo, s16CountryIndex, pstTargetRegionInfo->stCountryinfo[i].stPrimary_region_info[j].u8primary_region_code);
                if(s16PrimaryIndex >= 0)
                {
                    for(int k=0; k<pstTargetRegionInfo->stCountryinfo[i].stPrimary_region_info[j].u8secondary_region_num; k++)
                    {
                        MAPI_S16 s16SecondaryIndex = _AddTargetSecondaryRegion(stDvbTargetRegionInfo, s16CountryIndex, s16PrimaryIndex, pstTargetRegionInfo->stCountryinfo[i].stPrimary_region_info[j].stSecondary_region_info[k].u8secondary_region_code);
                        if(s16SecondaryIndex >= 0)
                        {
                            for(int l=0; l<pstTargetRegionInfo->stCountryinfo[i].stPrimary_region_info[j].stSecondary_region_info[k].u8tertiary_region_num; l++)
                            {
                                _AddTargetTertiaryRegion(stDvbTargetRegionInfo, s16CountryIndex, s16PrimaryIndex, s16SecondaryIndex, pstTargetRegionInfo->stCountryinfo[i].stPrimary_region_info[j].stSecondary_region_info[k].sttertiary_region_info[l].u16tertiary_region_code);
                            }
                        }
                    }
                }
            }
        }
    }
}

void MW_DVB_SI_PSI_Parser::_AddCommonProgramInfo(DVB_PROG &stChnInfoDVB, MW_DVB_PROGRAM_INFO *pServiceInfo)
{
    if(pServiceInfo == NULL)
    {
        return;
    }

    stChnInfoDVB.u16PCRPID = pServiceInfo->u16PCRPid;
    stChnInfoDVB.u16VideoPID = pServiceInfo->stVideoInfo.wVideoPID;
    stChnInfoDVB.u16LCN = pServiceInfo->u16LCN;
    stChnInfoDVB.u16Number = pServiceInfo->u16LCN;
    stChnInfoDVB.u16PmtPID = pServiceInfo->u16PmtPID;
    stChnInfoDVB.u16ServiceID = pServiceInfo->u16ServiceID;
    stChnInfoDVB.u16SimuLCN = pServiceInfo->u16SimuLCN;

    stChnInfoDVB.stPSI_SI_Version.u8NITVer = pServiceInfo->u8NitVer;
    stChnInfoDVB.stPSI_SI_Version.u8PATVer = pServiceInfo->u8PatVer;
    stChnInfoDVB.stPSI_SI_Version.u8PMTVer = pServiceInfo->u8PmtVer;
    stChnInfoDVB.stPSI_SI_Version.u8SDTVer = pServiceInfo->u8SdtVer;

    stChnInfoDVB.stCHAttribute.u16SignalStrength = 0xFFFD;
    stChnInfoDVB.stCHAttribute.u8VisibleServiceFlag = pServiceInfo->bIsVisible;
    stChnInfoDVB.stCHAttribute.u8NumericSelectionFlag = pServiceInfo->bIsSelectable;
    stChnInfoDVB.stCHAttribute.u8IsDelete = DEFAULT_IS_DELETED;
    stChnInfoDVB.stCHAttribute.u8IsMove = DEFAULT_IS_MOVED;
    stChnInfoDVB.stCHAttribute.u8IsScramble = pServiceInfo->bIsCAExist;
    stChnInfoDVB.stCHAttribute.u8IsSkipped = DEFAULT_IS_SKIPPED;
    stChnInfoDVB.stCHAttribute.u8IsLock = DEFAULT_IS_LOCKED;
    stChnInfoDVB.stCHAttribute.u8IsReName = DEFAULT_IS_RENAME;
    stChnInfoDVB.stCHAttribute.u8IsStillPicture = pServiceInfo->stVideoInfo.bStillPic;
    stChnInfoDVB.stCHAttribute.u8IsMHEGIncluded = pServiceInfo->bIsDataBroadcastService;
    stChnInfoDVB.stCHAttribute.u8IsServiceIdOnly = pServiceInfo->bIsServiceIDOnly;
    stChnInfoDVB.stCHAttribute.u8IsDataServiceAvailable = pServiceInfo->bIsDataService;
    stChnInfoDVB.stCHAttribute.u8IsReplaceDel = MAPI_FALSE;
    stChnInfoDVB.stCHAttribute.u8ServiceType = pServiceInfo->u8ServiceType;
    stChnInfoDVB.stCHAttribute.u8ServiceTypePrio = pServiceInfo->u8ServiceTypePrio;
    stChnInfoDVB.stCHAttribute.u8VideoType = pServiceInfo->stVideoInfo.bVideoType;
    stChnInfoDVB.stCHAttribute.u8Favorite = DEFAULT_FAVORITE;
    stChnInfoDVB.stCHAttribute.u8IsSpecialSrv = pServiceInfo->bIsSpecialSrv;

    memcpy(stChnInfoDVB.stAudInfo, pServiceInfo->stAudInfo, MAX_AUD_LANG_NUM * sizeof(AUD_INFO));
    memcpy(stChnInfoDVB.u8ServiceName, pServiceInfo->au8ServiceName, (MAPI_SI_MAX_SERVICE_NAME > MAX_SERVICE_NAME) ? MAX_SERVICE_NAME : MAPI_SI_MAX_SERVICE_NAME);
}

void MW_DVB_SI_PSI_Parser::_UpdateProgInfoByServiceType(MAPI_U8 u8ServiceType, DVB_PROG *pstDvbProgInfo, MW_DVB_PROGRAM_INFO *pstProgInfo)
{
    if((u8ServiceType == E_TYPE_DATA) || (u8ServiceType == E_TYPE_MHP))
    {
        if(_IsSpecificSupport(E_DVB_DATA_SERVICE_SUPPORT, &m_eCountry, NULL) == MAPI_FALSE)
        {
            if(pstDvbProgInfo != NULL)
            {
                pstDvbProgInfo->stCHAttribute.u8NumericSelectionFlag = MAPI_FALSE;
                pstDvbProgInfo->stCHAttribute.u8VisibleServiceFlag = MAPI_FALSE;
            }
            if(pstProgInfo != NULL)
            {
                pstProgInfo->bIsSelectable = MAPI_FALSE;
                pstProgInfo->bIsVisible = MAPI_FALSE;
            }
        }
    }

    if((pstDvbProgInfo != NULL) && (pstDvbProgInfo->stCHAttribute.u8ServiceType == E_SERVICETYPE_INVALID))
    {
        pstDvbProgInfo->stCHAttribute.u8NumericSelectionFlag = MAPI_FALSE;
        pstDvbProgInfo->stCHAttribute.u8VisibleServiceFlag = MAPI_FALSE;
        pstDvbProgInfo->stCHAttribute.u8IsSpecialSrv = MAPI_FALSE;
    }

    if((pstProgInfo != NULL) && (pstProgInfo->u8ServiceType == E_SERVICETYPE_INVALID))
    {
        pstProgInfo->bIsSelectable = MAPI_FALSE;
        pstProgInfo->bIsVisible = MAPI_FALSE;
        pstProgInfo->bIsSpecialSrv = MAPI_FALSE;
    }
}

void MW_DVB_SI_PSI_Parser::_AddNewFrequency(MAPI_SI_TS_INFO *pstTSInfo, MAPI_U32 (&au32FreqArray)[MAPI_SI_NTV_MAX_FREQUENCY])
{
    int i, j, k;
    MAPI_U8 u8EmptyFreqIndex = 0xFF;

    if(pstTSInfo == NULL)
    {
        return;
    }

    if(pstTSInfo->stTDS.u32CentreFreq != 0)
    {
        u8EmptyFreqIndex = 0xFF;

        for(i=0; i<MAPI_SI_NTV_MAX_FREQUENCY; i++)
        {
            if(au32FreqArray[i] == pstTSInfo->stTDS.u32CentreFreq)
            {
                break;
            }
            else if((u8EmptyFreqIndex == 0xFF) && (au32FreqArray[i] == 0))
            {
                u8EmptyFreqIndex = i;
            }
        }

        if((i >= MAPI_SI_NTV_MAX_FREQUENCY) && (u8EmptyFreqIndex != 0xFF))
        {
            au32FreqArray[u8EmptyFreqIndex] = pstTSInfo->stTDS.u32CentreFreq;
        }
    }
    else
    {
        MAPI_BOOL bFound = MAPI_FALSE;
        MAPI_U16 u16CellId;

        m_ParserCallBackInfo.EventHandler(E_DVB_SI_GET_CELLID, (MAPI_U32)m_ParserCallBackInfo.pCallbackReference, (MAPI_U32)&u16CellId);
        if(pstTSInfo->stT2DS.u8TfsFlag != 0)
        {
            for(i=0; i<MAX_SI_T2_CELL_CENTREFREQ; i++)
            {
                if(pstTSInfo->stT2DS.stT2CellCentreFreq[i].u16Cell == u16CellId)
                {
                    for(j=0; j<MAX_SI_T2_CENTREFREQ; j++)
                    {
                        if(pstTSInfo->stT2DS.stT2CellCentreFreq[i].u32CentreFreq[j] != 0)
                        {
                            bFound = MAPI_TRUE;
                            break;
                        }
                    }
                }

                if(bFound == MAPI_TRUE)
                {
                    break;
                }
            }

            if(bFound == MAPI_TRUE)
            {
                u8EmptyFreqIndex = 0xFF;
                for(k=0; k<MAPI_SI_NTV_MAX_FREQUENCY; k++)
                {
                    if(au32FreqArray[k] == pstTSInfo->stT2DS.stT2CellCentreFreq[i].u32CentreFreq[j])
                    {
                        break;
                    }
                    else if((u8EmptyFreqIndex == 0xFF) && (au32FreqArray[k] == 0))
                    {
                        u8EmptyFreqIndex = k;
                    }
                }

                if((k >= MAPI_SI_NTV_MAX_FREQUENCY) && (u8EmptyFreqIndex != 0xFF))
                {
                    au32FreqArray[u8EmptyFreqIndex] = pstTSInfo->stT2DS.stT2CellCentreFreq[i].u32CentreFreq[j];
                }
            }
        }
        else
        {
            for(i=0; i<MAX_SI_T2_CELL_CENTREFREQ; i++)
            {
                if((pstTSInfo->stT2DS.stT2CellCentreFreq[i].u32CentreFreq[0] != 0) && (pstTSInfo->stT2DS.stT2CellCentreFreq[i].u16Cell == u16CellId))
                {
                    bFound = MAPI_TRUE;
                    break;
                }
            }

            if(bFound == MAPI_TRUE)
            {
                u8EmptyFreqIndex = 0xFF;
                for(k=0; k<MAPI_SI_NTV_MAX_FREQUENCY; k++)
                {
                    if(au32FreqArray[k] == pstTSInfo->stT2DS.stT2CellCentreFreq[i].u32CentreFreq[0])
                    {
                        break;
                    }
                    else if((u8EmptyFreqIndex == 0xFF) && (au32FreqArray[k] == 0))
                    {
                        u8EmptyFreqIndex = k;
                    }
                }

                if((k >= MAPI_SI_NTV_MAX_FREQUENCY) && (u8EmptyFreqIndex != 0xFF))
                {
                    au32FreqArray[u8EmptyFreqIndex] = pstTSInfo->stT2DS.stT2CellCentreFreq[i].u32CentreFreq[0];
                }
            }
        }
    }
}

#if (DVBC_SYSTEM_ENABLE == 1)
MAPI_BOOL MW_DVB_SI_PSI_Parser::CheckCableDeliveryInfoExist(void)
{
    return ((m_pCableDeliveryInfo == NULL) ? MAPI_FALSE : MAPI_TRUE);
}
#endif
#if (ISDB_SYSTEM_ENABLE == 1)
void MW_DVB_SI_PSI_Parser::SetSIMonitorDebugMode(EN_SI_MONITOR_DEBUG_MODE eDebugMode)
{
    ASSERT(m_bInit);
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
    m_eSIMonitorDebugMode = eDebugMode;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::IsMonitorOFF(EN_SI_MONITOR_DEBUG_MODE eDebugMode)
{
    if(((m_eSIMonitorDebugMode & eDebugMode) == E_ALL_MONITOR_OFF) || ((m_eSIMonitorDebugMode & eDebugMode)== MAPI_TRUE))
    {
        return MAPI_TRUE;
    }
    return MAPI_FALSE;
}
#endif

MAPI_BOOL MW_DVB_SI_PSI_Parser::_SetSdtOtherIndex(MAPI_U16 u16TSID)
{
    for(MAPI_U16 i = 0; i < m_stSdtOtherInfo.stSdtOtherTS.size(); i++)
    {
        if ((m_stSdtOtherInfo.stSdtOtherTS.at(i).u16TSID == u16TSID)
            && (u16TSID != __GetID(m_pCurProg, m_pCurMux, m_pCurNetwork, EN_ID_TSID)))
        {
            m_stSdtOtherInfo.stSdtOtherTS.at(i).u8SdtVer = INVALID_PSI_SI_VERSION;
            m_stSdtOtherInfo.u8SdtOtherIndex = i;
            if (m_pSdtOtherParser)
            {
                _DeleteParser((MAPI_U32)m_pSdtOtherParser, MAPI_FALSE);
                m_u32SdtOtherMonitorTimer = 0;
            }
            break;
        }
    }
    return MAPI_TRUE;
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::_IsPatCRCChange(void)
{
    if(m_vPatCRC32.size() != m_stPat.vPatCRC.size())
    {
        return MAPI_TRUE;
    }
    else
    {
        CRC32_t::iterator it;
        CRC32_t::iterator it2;
        MAPI_BOOL bChange = MAPI_TRUE;
        for (it = m_stPat.vPatCRC.begin(); it != m_stPat.vPatCRC.end(); it++)
        {
            bChange = MAPI_TRUE;
            for (it2 = m_vPatCRC32.begin(); it2 != m_vPatCRC32.end(); it2++)
            {
                if((*it) == (*it2))
                {
                    bChange = MAPI_FALSE;
                    break;
                }
            }
            if(bChange == MAPI_TRUE)
            {
                return MAPI_TRUE;
            }
        }
    }
    return MAPI_FALSE;
}

void MW_DVB_SI_PSI_Parser::_UpdateCurrentProgram(void)
{
    m_pCurProg = m_pCMDB->GetCurr();
    m_pCurMux = __GetMux(m_pCurProg, m_pCMDB);
    if(m_pCurMux == NULL)
    {
        MW_SI_PARSER_ERROR("get mux failed\n");
        ASSERT(0);
    }
#if (DVBS_SYSTEM_ENABLE == 1)
    m_pCurSat = GETSAT(m_pCurMux, m_pCMDB);
    if(m_pCurSat == NULL)
    {
        MW_SI_PARSER_ERROR("get sat failed\n");
    }
#endif
    m_pCurNetwork = __GetNetwork(m_pCurMux, m_pCMDB);
    if(m_pCurNetwork == NULL)
    {
        MW_SI_PARSER_ERROR("get network failed\n");
        ASSERT(0);
    }

}

void MW_DVB_SI_PSI_Parser::_CiEitNortifyMonitor()
{
#if (CI_PLUS_ENABLE == 1)
    if(m_u32PostSendCiProtectionEventTime > 0)
    {
        if(m_EitPfInfo[PRESENT_SEC].version_number != INVALID_PSI_SI_VERSION
        || _Timer_DiffTimeFromNow(m_u32PostSendCiProtectionEventTime) > MAX_CI_PROTECTION_WAIT_TIME)
        {
            MW_SI_PARSER_MESSAGE("EitPF ready or timeout %d second reached, send CI protection event!\n", MAX_CI_PROTECTION_WAIT_TIME / 1000);
            MONITOR_NOTIFY(E_DVB_SDT_CI_PROTECTION_FINISH, NULL, m_pMonitorNotifyUsrParam, NULL);
            m_u32PostSendCiProtectionEventTime = 0;
        }
    }
#endif
}

BOOL MW_DVB_SI_PSI_Parser::_IsDefaultBroadcastMixAD(void)
{
    if(((m_eParserType == MW_DVB_T_PARSER) && (m_eCountry == E_FRANCE))
        || ((m_eParserType == MW_DVB_C_PARSER) && IS_DEFAULT_BROADCAST_MIX_AD_PROVIDER(m_enCableOperator)))
    {
        return TRUE;
    }
    return FALSE;
}

MAPI_U8 MW_DVB_SI_PSI_Parser::_FindRegionByMux(MAPI_U16 u16TSID, MAPI_U16 u16ONID, MAPI_U16 u16NID)
{
    DVB_PROG *Prog;
    DVB_MUX *Mux;

    MW_DTV_CM_DB_scope_lock lock(m_pCMDB);
    for(MAPI_U32 i = 0; i < m_pCMDB->Size(); i++)
    {
        Prog = m_pCMDB->GetByIndex(i);
        if(Prog != NULL)
        {
            Mux = __GetMux(Prog, m_pCMDB);
            if((u16TSID == Mux->u16TransportStream_ID) && (u16ONID == Mux->u16OriginalNetwork_ID) && (u16NID == Mux->u16Network_ID))
            {
                return Prog->stCHAttribute.u8Region;
            }
        }
    }

    return DEFAULT_REGION;
}

void MW_DVB_SI_PSI_Parser::_SetStreamUTCTime(U32 u32Time)
{
    m_u32StreamUTCTime = u32Time;
    m_u32StreamUTCTime0 = _GetTime0();
}

U32 MW_DVB_SI_PSI_Parser::_GetStreamUTCTime()
{
    if(m_u32StreamUTCTime0 > 0)
    {
        return m_u32StreamUTCTime +_Timer_DiffTimeFromNow(m_u32StreamUTCTime0);
    }
    return m_u32StreamUTCTime;
}

U32 MW_DVB_SI_PSI_Parser::GetUtcTimeForCheckingEitValidation()
{
    mapi_scope_lock(scopeLock, &m_pParseMonitorMutex);
#if (ISDB_SYSTEM_ENABLE == 1)
    U32 u32CurrentStreamTime = _GetStreamUTCTime();
    if ((m_eClockMode != MW_DVB_CLOCK_AUTO)&&(u32CurrentStreamTime != 0))
    {
        return u32CurrentStreamTime;
    }
#endif
    return mapi_interface::Get_mapi_system()->RTCGetCLK();
}

void MW_DVB_SI_PSI_Parser::_CollectChannelListInfo(void)
{
    int Index = 0;
    MW_DVB_SERVICE_LCN_INFO stServiceLcnInfo = {0};
    if((m_eParserMode != E_DVB_SCAN) || (m_stNit.pstTSInfo == NULL))
    {
        return;
    }

    //_ResetRegionalChannelList();

    for(int i = 0; i < m_stNit.u16TSNumber; i++)
    {
        if(MALAYSIA_ONID == m_stNit.pstTSInfo[i].u16ONID)
        {
            for(int j = 0; j < MAPI_SI_MAX_LCD2_SUPPORT; j++)
            {
                if(m_stNit.pstTSInfo[i].astLcnV2Info[j].u8ServicesNumber > 0)
                {
                    for(Index = 0; Index < MAX_CHANNELLIST; Index++)
                    {
                        if((m_stNit.u16NetworkID == m_stAllLCDV2ChannelList[Index].u16NID)&&
                            (m_stAllLCDV2ChannelList[Index].u8ChannelListID == m_stNit.pstTSInfo[i].astLcnV2Info[j].u8ChannelListID)&&
                            (strcmp((const char *)m_stNit.pstTSInfo[i].astLcnV2Info[j].ChannelListName, (const char *)m_stAllLCDV2ChannelList[Index].ChannelListName) == 0)&&
                            (m_stAllLCDV2ChannelList[Index].vLcnInfo.size()))
                        {
                            for(int cnt = 0; cnt < m_stNit.pstTSInfo[i].astLcnV2Info[j].u8ServicesNumber; cnt++)
                            {
                                stServiceLcnInfo.u16TSID = m_stNit.pstTSInfo[i].wTransportStream_ID;
                                stServiceLcnInfo.u16ONID = m_stNit.pstTSInfo[i].u16ONID;
                                stServiceLcnInfo.u16ServiceID = m_stNit.pstTSInfo[i].astLcnV2Info[j].pLCNInfo[cnt].u16ServiceID;
                                stServiceLcnInfo.u16LCNNumber = m_stNit.pstTSInfo[i].astLcnV2Info[j].pLCNInfo[cnt].u16LCNNumber;
                                stServiceLcnInfo.u16SimuLCNNumber = m_stNit.pstTSInfo[i].astLcnV2Info[j].pLCNInfo[cnt].u16SimuLCNNumber;
                                stServiceLcnInfo.bIsVisable = m_stNit.pstTSInfo[i].astLcnV2Info[j].pLCNInfo[cnt].bIsVisable;
                                stServiceLcnInfo.bIsSelectable = m_stNit.pstTSInfo[i].astLcnV2Info[j].pLCNInfo[cnt].bIsSelectable;
                                stServiceLcnInfo.bIsSpecialSrv = m_stNit.pstTSInfo[i].astLcnV2Info[j].pLCNInfo[cnt].bIsSpecialSrv;
                                m_stAllLCDV2ChannelList[Index].vLcnInfo.push_back(stServiceLcnInfo);
                            }
                            break;
                        }
                        else if(m_stAllLCDV2ChannelList[Index].vLcnInfo.size() == 0)
                        {
                            m_stAllLCDV2ChannelList[Index].u16NID = m_stNit.u16NetworkID;
                            m_stAllLCDV2ChannelList[Index].u8ChannelListID = m_stNit.pstTSInfo[i].astLcnV2Info[j].u8ChannelListID;
                            memcpy(m_stAllLCDV2ChannelList[Index].ChannelListName, m_stNit.pstTSInfo[i].astLcnV2Info[j].ChannelListName, MAPI_SI_MAX_CHANNELLIST_NAME);
                            //printf("\x1b[37;41m TSID : %d\x1b[0m\n",m_stAllLCDV2ChannelList[Index].u16TSID);
                            //printf("\x1b[37;41m TSID : %d\x1b[0m\n",m_stAllLCDV2ChannelList[Index].u16ONID);
                            //printf("\x1b[37;41m u8ChannelListID : %d\x1b[0m\n",m_stAllLCDV2ChannelList[Index].u8ChannelListID);
                            //printf("\x1b[37;41m ChannelListName : %s\x1b[0m\n",m_stAllLCDV2ChannelList[Index].ChannelListName);
                            for(int cnt = 0; cnt < m_stNit.pstTSInfo[i].astLcnV2Info[j].u8ServicesNumber; cnt++)
                            {
                                stServiceLcnInfo.u16TSID = m_stNit.pstTSInfo[i].wTransportStream_ID;
                                stServiceLcnInfo.u16ONID = m_stNit.pstTSInfo[i].u16ONID;
                                stServiceLcnInfo.u16ServiceID = m_stNit.pstTSInfo[i].astLcnV2Info[j].pLCNInfo[cnt].u16ServiceID;
                                stServiceLcnInfo.u16LCNNumber = m_stNit.pstTSInfo[i].astLcnV2Info[j].pLCNInfo[cnt].u16LCNNumber;
                                stServiceLcnInfo.u16SimuLCNNumber = m_stNit.pstTSInfo[i].astLcnV2Info[j].pLCNInfo[cnt].u16SimuLCNNumber;
                                stServiceLcnInfo.bIsVisable = m_stNit.pstTSInfo[i].astLcnV2Info[j].pLCNInfo[cnt].bIsVisable;
                                stServiceLcnInfo.bIsSelectable = m_stNit.pstTSInfo[i].astLcnV2Info[j].pLCNInfo[cnt].bIsSelectable;
                                stServiceLcnInfo.bIsSpecialSrv = m_stNit.pstTSInfo[i].astLcnV2Info[j].pLCNInfo[cnt].bIsSpecialSrv;
                                m_stAllLCDV2ChannelList[Index].vLcnInfo.push_back(stServiceLcnInfo);
                            }
                            break;
                        }
                    }
                    if(Index >= MAX_CHANNELLIST)
                    {
                        return;
                    }
                }
            }
        }
    }
}

MAPI_BOOL MW_DVB_SI_PSI_Parser::GetRegionalChannelListNameInfo(std::vector<REGIONAL_CHANNELLIST_NAME_INFO>&vstChannelListNameInfo)
{
    vector<MW_DVB_SERVICE_LCN_INFO>::iterator itChList;
    REGIONAL_CHANNELLIST_NAME_INFO stListNameInfo;

    vstChannelListNameInfo.clear();

    for(int k = 0; k < MAX_CHANNELLIST; k++)
    {
        if(m_stAllLCDV2ChannelList[k].vLcnInfo.size() > 0)
        {
            stListNameInfo.u8ChannelListID = m_stAllLCDV2ChannelList[k].u8ChannelListID;
            memcpy(stListNameInfo.ChannelListName, m_stAllLCDV2ChannelList[k].ChannelListName, MAPI_SI_MAX_CHANNELLIST_NAME);
            vstChannelListNameInfo.push_back(stListNameInfo);
        }
    }
    return (vstChannelListNameInfo.size() > 0)?MAPI_TRUE:MAPI_FALSE;
}

void MW_DVB_SI_PSI_Parser::SetRegionalChannelList(MAPI_S8 s8RegionalID)
{
    if(s8RegionalID >= 0)
    {
        vector<MW_DVB_SERVICE_LCN_INFO>::iterator itLCN;
        for(int i = 0; i < MAX_CHANNELLIST; i++)
        {
            if(s8RegionalID == m_stAllLCDV2ChannelList[i].u8ChannelListID)
            {
                for(itLCN = m_stAllLCDV2ChannelList[i].vLcnInfo.begin(); itLCN != m_stAllLCDV2ChannelList[i].vLcnInfo.end(); itLCN++)
                {
                    _SetRegionalChannelList(m_pCurProg, m_pCMDB, m_pCurMux, m_pCurNetwork, (*itLCN));
                }
                break;
            }
        }
    }
}

void MW_DVB_SI_PSI_Parser::_SetRegionalChannelList(DVB_PROG *pCurProg, DVB_CM *pCMDB, DVB_MUX *pMux , DVB_NETWORK *pNetwork, MW_DVB_SERVICE_LCN_INFO & stLcnInfo)
{
    DVB_PROG* pNextProg;

    pNextProg = pCMDB->GetByIndex(0);
    while(pNextProg)
    {
        pMux = (DVB_MUX*)__GetMux(pNextProg, pCMDB);
        ASSERT(pMux);
        pNetwork = (DVB_NETWORK*)__GetNetwork(pMux, pCMDB);
        ASSERT(pNetwork);

        if((__GetID(pNextProg, pMux, pNetwork, EN_ID_TSID) == stLcnInfo.u16TSID) &&
            (__GetID(pNextProg, pMux, pNetwork, EN_ID_ONID) == stLcnInfo.u16ONID) &&
            (__GetID(pNextProg, pMux, pNetwork, EN_ID_SID) == stLcnInfo.u16ServiceID))
        {
            pNextProg->u16LCN = stLcnInfo.u16LCNNumber;
            pNextProg->u16SimuLCN = stLcnInfo.u16SimuLCNNumber;
            pNextProg->stCHAttribute.u8VisibleServiceFlag = stLcnInfo.bIsVisable;
            pNextProg->stCHAttribute.u8VisibleServiceFlag = stLcnInfo.bIsVisable;
            pNextProg->stCHAttribute.u8IsSpecialSrv = stLcnInfo.bIsSpecialSrv;
            break;
        }
        pNextProg = pCMDB->GetNext(pNextProg);
    }
}

void MW_DVB_SI_PSI_Parser::_ResetRegionalChannelList(void)
{
    for(int k = 0; k < MAX_CHANNELLIST; k++)
    {
        m_stAllLCDV2ChannelList[k] = MW_DVB_REGIONAL_CHANNELLIST();
    }
}

bool MW_DVB_SI_PSI_Parser::_ShouldCheckOnidForOAD()
{
    return (m_eParserType == MW_DVB_T_PARSER) && (IS_NORDIC_COUNTRY(m_eCountry));
}
MW_EWS_INFO::MW_EWS_INFO(MAPI_U8 u8TrdwVer, MAPI_U8 u8TcdwVer, MAPI_U8 u8TmdwVer)
{
    UNUSED(u8TrdwVer);
    UNUSED(u8TcdwVer);
    UNUSED(u8TmdwVer);
}
