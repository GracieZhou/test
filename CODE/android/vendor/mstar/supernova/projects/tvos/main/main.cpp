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
#include <string.h>
#include <stdio.h>
#include <sys/prctl.h>
#include <sys/resource.h>
#include <sys/types.h>
#include <sys/wait.h>

#include "mthread.h"
//MStarSDK header files
#include "mapi_interface.h"
#include "mapi_utility.h"

//Msrv header files
#include "MSystem.h"
#include "MSrv_Control.h"
#include "MSrv_System_Database.h"
#include "MSrv_ChannelManager.h"
#include "MSrv_ATV_Player.h"
#include "MSrv_DTV_Player_DVB.h"
#include "MSrv_DTV_Player_DVBT.h"
#include "MSrv_AV_Player.h"
#include "MSrv_SV_Player.h"
#include "MSrv_PC_Player.h"
#include "MSrv_COMP_Player.h"
#include "MSrv_SCART_Player.h"
#include "MSrv_HDMI_Player.h"
#include "MSrv_Storage_Player.h"
#include "MSrv_CIMMI.h"
#include "MSrv_SSSound.h"
#include "MSrv_Advert_Player.h"
#include "MSrv_MHL.h"
#if (CEC_ENABLE == 1)
#include "MSrv_CEC.h"
#endif
#include "MSrv_Video.h"
#if (ENABLE_BACKEND == 1)
#include "MSrv_Backend.h"
#endif
#include "SystemInfo.h"

#if (MSTAR_TVOS_MDMPPLAYER == 1)
#include "MMServiceInterface.h"
#endif
#if (MSTAR_IPC == 1)
#if (APP_IPC_SERVICE_ENABLE == 1)
#include "AppIpcServiceInterface.h"
#endif
#if (MM_IPC_SERVICE_ENABLE == 1)
#include "MMServiceInterface.h"
#endif
#endif
//tvos Service header files
#include <common/TVOS_Common.h>
#include <playerimpl/PlayerImplService.h>
#include <scanmanagerimpl/ScanManagerImplService.h>
#include <channelmanager/ChannelManagerService.h>
//#include <sample/SampleService.h>
#include <audiomanager/AudioManagerService.h>
#include <tvmanager/TvManagerService.h>
#include <picturemanager/PictureManagerService.h>
#include <timermanager/TimerManagerService.h>
#include <databasemanager/DatabaseManagerService.h>
#include <factorymanager/FactoryManagerService.h>
#include <threedimensionmanager/ThreeDimensionManagerService.h>
#include <thirdpartytvmanager/ThirdPartyTvManagerService.h>
#include <subtitlemanager/SubtitleManagerService.h>
#include <pvrmanager/PvrManagerService.h>
#include <oadmanager/OadManagerService.h>
#if(EPG_ENABLE == 1)
#include <epgmanager/EpgManagerService.h>
#endif
#include <logomanager/LogoManagerService.h>
#include <videoset/VideoSetService.h>
#if(MCAST_ENABLE == 1)
#include <mcast/MCastService.h>
#endif
#if (CEC_ENABLE == 1)

#include <cecmanager/CecManagerService.h>
#endif
#include <mhlmanager/MhlManagerService.h>
#include <cimanager/CiManagerService.h>
#include <parentalcontrolmanager/ParentalcontrolManagerService.h>

#include <pipmanager/PipManagerService.h>

#include <dmxmanager/DmxManagerService.h>
#include <scmanager/ScManagerService.h>
#include <pcmciamanager/PcmciaManagerService.h>

#if (PLATFORM_TYPE == MSTAR_PURESN)
#include <networkmanager/NetworkManagerService.h>
#include <usbmassstoragemanager/UsbMassStorageManagerService.h>
#endif

#if(TVOS_AN_VERSION_MAJOR==5)
#define _REALLY_INCLUDE_SYS__SYSTEM_PROPERTIES_H_
#include <sys/_system_properties.h>
#elif (TVOS_AN_VERSION_MAJOR==4)
#include <sys/system_properties.h>
#elif (TVOS_AN_VERSION_MAJOR==2)
#else
#error "not support"
#endif

#include <cutils/properties.h>
#include "mapi_env_manager.h"
#include "mapi_gpio_devTable.h"

#if(CA_ENABLE == 1)
#include "MSrv_CA.h"
#endif
#include <camanager/CaManagerService.h>

#if (STB_ENABLE == 1)
#include "mdrv_dac_tbl.h"
#endif

#if (ADVERT_BOOTING_ENABLE == 1)
#include "apiGOP.h"
#endif
#define lock(m_FuncLock) lock(m_FuncLock,(char*)(__FILE__),(char *)(__FUNCTION__),__LINE__)
extern Mutex m_FuncLock; //Mutex with TVOS services

#if (HDMITX_ENABLE == 1)
#include "MSrv_HDMITX.h"
#endif

#if (ADVERT_BOOTING_ENABLE == 1)
#include "MSrv_Player.h"
#include "MSrv_Video.h"
#define WIDTH_4K2K         3840
#define HEIGHT_4K2K        2160
#define OFFSET_4K2K     (10)
#endif

#if (DVBS_SYSTEM_ENABLE == 1)
#include "MSrv_DTV_Player_DVBS.h"
#endif
#if defined(ENABLE_MBACKTRACE)  && (ENABLE_MBACKTRACE == 1)
#include "MBacktrace.h"
#endif
/**
* Tv System Define to fit Android Api definition
* for backward compatible, we keep the original define.
* the content style means : { "old value", "new value" }
* old value for backward, new value for fitting route define in mapi_syscfg_table.h
*/
#define TV_SYSTEM_DVBT   1
#define TV_SYSTEM_DVBC   2
#define TV_SYSTEM_DVBS   3
#define TV_SYSTEM_DVBT2  4
#define TV_SYSTEM_DVBS2  5
#define TV_SYSTEM_DTMB   6
#define TV_SYSTEM_ATSC   7
#define TV_SYSTEM_ISDB   8
#define TV_SYSTEM_NUM    TV_SYSTEM_ISDB + 1

static string tvsystem[TV_SYSTEM_NUM][2] = {
    { "0",   "0" }, //null
    { "1",   "1" }, //dvbt
    { "2",   "2" }, //dvbc
    { "3",   "4" }, //dvbs
    { "4",  "32" }, //dvbt2
    { "5",  "64" }, //dvbs2
    { "6", "128" }, //dtmb
    { "7",   "8" }, //atsc
    { "8",  "16" }, //isdb
};

void ThirdPartyDtvSystemInit()
{
    MS_USER_SYSTEM_SETTING stGetSystemSetting;

    MSrv_Control::GetMSrvSystemDatabase()->GetUserSystemSetting(&stGetSystemSetting);
    MS_USER_SYSTEM_SETTING stOrgSystemSetting = stGetSystemSetting;
    printf("stGetSystemSetting.enInputSourceType :%d \n",stGetSystemSetting.enInputSourceType);
    if(stGetSystemSetting.enInputSourceType >= MAPI_INPUT_SOURCE_STORAGE)
    {
        stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_DTV;
        MSrv_Control::GetMSrvSystemDatabase()->SetUserSystemSetting(&stGetSystemSetting, &stOrgSystemSetting);
    }
    if(stGetSystemSetting.enInputSourceType != MAPI_INPUT_SOURCE_DTV)
    {
        mapi_scope_lock(scopeLock, (pthread_mutex_t *)&m_FuncLock);
        MSrv_Control::GetInstance()->SetInputSource(stGetSystemSetting.enInputSourceType);
        return;
    }
    MSrv_Control::GetMSrvDtv()->SetDTVFunctionOnOff(FALSE);
    MSrv_Control::GetInstance()->SetInputSource(MAPI_INPUT_SOURCE_DTV,TRUE,FALSE,FALSE);
    printf("ThirdPartyDtvSystemInit ok \n");
}

void SetInputSourceAfterPowerOn(void)
{
    IEnvManager* pEnvMan = IEnvManager::Instance();//colin@20130418 add for thirdparty dtv
    U32 thirdpartydtv = 0;
#if (ADVERT_BOOTING_ENABLE == 1)
       U8 bootlogo_gopidx = 0;
       if (pEnvMan)
       {
          const char* pbootlogo_gop = NULL;
          pbootlogo_gop =pEnvMan->GetEnv("bootlogo_gopidx");
          if(pbootlogo_gop != NULL)
          {
             bootlogo_gopidx =(MAPI_U32) strtoul(pbootlogo_gop, 0, 10);
          }
          printf("get bootlogo_gopidx:%d \n",bootlogo_gopidx);

          int i=0;
          int j=0;
          static MS_U8 u8GwinNum=0;
          for(i=0;i<bootlogo_gopidx;i++)
          {
             u8GwinNum+=MApi_GOP_GWIN_GetGwinNum(i);
          }
          for(j=u8GwinNum;j<u8GwinNum+MApi_GOP_GWIN_GetGwinNum(bootlogo_gopidx);j++)
          {
             if(MApi_GOP_GWIN_IsGWINEnabled(j)==TRUE)
             {
                MApi_GOP_GWIN_Enable(j,FALSE);
             }
          }
       }

#if (PLATFORM_TYPE == MSTAR_ANDROID)
       property_set("mstar.videoadvert.finished", "1");
#endif
#endif

    if (pEnvMan)
    {
        const char* pInfo = NULL;
        pInfo =pEnvMan->GetEnv("thirdpartydtv");
        if(pInfo != NULL)
        {
            thirdpartydtv =(MAPI_U32) strtoul(pInfo, 0, 10);
        }
        printf("get thirdpartydtv env:%s,thirdpartydtv:%d \n",pInfo,(unsigned int)thirdpartydtv);
        if(thirdpartydtv != 0)
        {
            ThirdPartyDtvSystemInit();
#if (ADVERT_BOOTING_ENABLE == 1)
#if (PLATFORM_TYPE == MSTAR_ANDROID)
            property_set("mstar.videoadvert.finished", "1");
#endif
#endif
            return ;
        }
    }

    MS_USER_SYSTEM_SETTING stGetSystemSetting;
    U32 u32Num;


    MSrv_Control::GetMSrvSystemDatabase()->GetUserSystemSetting(&stGetSystemSetting);
    MS_USER_SYSTEM_SETTING stOrgSystemSetting = stGetSystemSetting;

    if(stGetSystemSetting.enInputSourceType >= MAPI_INPUT_SOURCE_STORAGE)
    {
        MSrv_Control_common::GetMSrvChannelManager()->GetDTVProgramCount(&u32Num);
        if(u32Num)
        {
            stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_DTV;
        }
        else
        {
        #if (STB_ENABLE == 0)
            MSrv_Control_common::GetMSrvChannelManager()->GetATVProgramCount(&u32Num);
            if(u32Num)
            {
                stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_ATV;
            }
            else
        #endif
            {
                stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_DTV;
            }
        }

        MSrv_Control::GetMSrvSystemDatabase()->SetUserSystemSetting(&stGetSystemSetting, &stOrgSystemSetting);
    }
     if(MSrv_Control::GetMSrvAtv()->TunerStatus())
    {
  //  stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_ATV;
    }
   else
    {
    if ((stGetSystemSetting.enInputSourceType == MAPI_INPUT_SOURCE_DTV)||(stGetSystemSetting.enInputSourceType == MAPI_INPUT_SOURCE_ATV))
    stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_HDMI;
    }
    //set input source and first channel
    //CHECK On time up or DC AC UP
    MSrv_Timer* pTimer;
    struct tm timeinfo;
    MSrv_Timer::ST_time stTime;
    MSrv_Timer::EN_Timer_Period enState;
    MSrv_Timer::ST_OnTime_TVDes stDes;
    EN_TIMER_BOOT_TYPE enBootMode;

    pTimer = MSrv_Control::GetMSrvTimer();
    pTimer->GetOnTime(stTime, enState, stDes, enBootMode);
    pTimer->GetCLKTime(timeinfo);

    if( pTimer->IsDaylightSaving() )
        timeinfo.tm_hour+=1;

    if(MSrv_Control::GetInstance()->IsWakeUpByRTC()&&(((enBootMode == EN_TIMER_BOOT_REMINDER) || (enBootMode == EN_TIMER_BOOT_RECORDER))))
    {
        stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_DTV;
    }

    //Add for Boot On Timer switch to preset Input Source
    if((MSrv_Control::GetInstance()->QueryPowerOnMode() == EN_POWER_DC_BOOT) &&
    (enBootMode == EN_TIMER_BOOT_ON_TIMER) &&
    (enState != MSrv_Timer::EN_Timer_Off) &&
    (stTime.u8Hour == (U8) timeinfo.tm_hour) &&
    (stTime.u8Minute == (U8)timeinfo.tm_min))
    {
#if (ISDB_SYSTEM_ENABLE == 1)
        if(pTimer->GetTVSrc(stDes.enTVSrc)==MAPI_INPUT_SOURCE_DTV)
        {
            ST_MEDIUM_SETTING MediumSetting;
            MSrv_Control::GetMSrvSystemDatabase()->GetMediumSetting(&MediumSetting);
            MediumSetting.AntennaType = E_ANTENNA_TYPE_AIR;
            MSrv_Control::GetMSrvSystemDatabase()->SetMediumSetting(&MediumSetting);
            MSrv_ATV_Database *pAtvDb = dynamic_cast<MSrv_ATV_Database *>(MSrv_Control::GetMSrvAtvDatabase());
            ASSERT(pAtvDb);
            pAtvDb->ConnectDatabase(MSrv_ATV_Database::DVBT_DB);
            if((stDes.u16ChNo & 0x0000ff00)>> 8)
            {
                stGetSystemSetting.enInputSourceType =MAPI_INPUT_SOURCE_DTV;
            }
            else
            {
                stGetSystemSetting.enInputSourceType =MAPI_INPUT_SOURCE_ATV;
            }
        }
        else if(pTimer->GetTVSrc(stDes.enTVSrc)==MAPI_INPUT_SOURCE_ATV)
        {
            ST_MEDIUM_SETTING MediumSetting;
            MSrv_Control::GetMSrvSystemDatabase()->GetMediumSetting(&MediumSetting);
            MediumSetting.AntennaType = E_ANTENNA_TYPE_CABLE;
            MSrv_Control::GetMSrvSystemDatabase()->SetMediumSetting(&MediumSetting);
            MSrv_ATV_Database *pAtvDb = dynamic_cast<MSrv_ATV_Database *>(MSrv_Control::GetMSrvAtvDatabase());
            ASSERT(pAtvDb);
            pAtvDb->ConnectDatabase(MSrv_ATV_Database::DVBC_DB);
            stGetSystemSetting.enInputSourceType =MAPI_INPUT_SOURCE_ATV;
        }
        else
        {
            stGetSystemSetting.enInputSourceType = pTimer->GetTVSrc(stDes.enTVSrc);
        }
#else
        stGetSystemSetting.enInputSourceType = pTimer->GetTVSrc(stDes.enTVSrc);
#endif
        printf("BootOnTime:switch to preset input src : %d\n",stGetSystemSetting.enInputSourceType);
    }

#if 0
    MSrv_Control::GetInstance()->SetInputSource(MAPI_INPUT_SOURCE_STORAGE, FALSE);
#else
    MSrv_Timer::ST_time stEPGTimerStartTime;
    ST_EPG_EVENT_TIMER_INFO stCurTimerInfo={0};
    {//Alert: Do not remove this bracket for mutex auto release after set input source
        mapi_scope_lock(scopeLock, (pthread_mutex_t *)&m_FuncLock);
        MSrv_Control::GetInstance()->SetInputSource(stGetSystemSetting.enInputSourceType);
    }

    if((enBootMode == EN_TIMER_BOOT_ON_TIMER) && (enState != MSrv_Timer::EN_Timer_Off) && (stTime.u8Hour == (U8) timeinfo.tm_hour) && (stTime.u8Minute == (U8)timeinfo.tm_min))
    {
        if(enState == MSrv_Timer::EN_Timer_Once)
        {
            pTimer->SetOnTime(stTime, MSrv_Timer::EN_Timer_Off, stDes, TRUE);
        }

        if(stGetSystemSetting.enInputSourceType == MAPI_INPUT_SOURCE_DTV)
        {
            MSrv_Control_common::GetMSrvChannelManager()->ChangeToFirstProgram(E_FIRST_PROG_DTV, E_ON_TIME_BOOT_TYPE);
        }
    #if (STB_ENABLE == 0)
        else if(stGetSystemSetting.enInputSourceType == MAPI_INPUT_SOURCE_ATV)
        {
            MSrv_Control_common::GetMSrvChannelManager()->ChangeToFirstProgram(E_FIRST_PROG_ATV, E_ON_TIME_BOOT_TYPE);
        }
    #endif
    }
    else if(((enBootMode == EN_TIMER_BOOT_REMINDER) || (enBootMode == EN_TIMER_BOOT_RECORDER)) && (stGetSystemSetting.enInputSourceType == MAPI_INPUT_SOURCE_DTV))
    {
        pTimer->GetEPGTimerEventByIndex(stCurTimerInfo, 0);
        MSrv_DTV_Player_DVB* pPlayer = dynamic_cast<MSrv_DTV_Player_DVB*>(MSrv_Control::GetMSrvDtv());
        if (pPlayer)
        {
            pPlayer->GetOffsetTime(stCurTimerInfo.u32StartTime, MAPI_TRUE);
        }
        pTimer->ConvertSeconds2StTime(stCurTimerInfo.u32StartTime, &stEPGTimerStartTime);
        if(MSrv_Control::GetInstance()->IsWakeUpByRTC())
        {
            MSrv_Control_common::GetMSrvChannelManager()->ChangeToFirstProgram(E_FIRST_PROG_DTV, E_ON_TIME_BOOT_TYPE);
        }
        else
        {
            MSrv_Control_common::GetMSrvChannelManager()->ChangeToFirstProgram(E_FIRST_PROG_DTV, E_AC_DC_BOOT_TYPE);
        }
    }
    else
    {
#if (STB_ENABLE == 0)
        if(!MSrv_Control::GetInstance()->HasAnyScartConnect())
        {
            if(stGetSystemSetting.enInputSourceType == MAPI_INPUT_SOURCE_DTV)
            {
                MSrv_Control_common::GetMSrvChannelManager()->ChangeToFirstProgram(E_FIRST_PROG_DTV, E_AC_DC_BOOT_TYPE);
            }
            else if(stGetSystemSetting.enInputSourceType == MAPI_INPUT_SOURCE_ATV)
            {
                MSrv_Control_common::GetMSrvChannelManager()->ChangeToFirstProgram(E_FIRST_PROG_ATV, E_AC_DC_BOOT_TYPE);
            }
        }
#else
        if(stGetSystemSetting.enInputSourceType == MAPI_INPUT_SOURCE_DTV)
        {
            MSrv_Control_common::GetMSrvChannelManager()->ChangeToFirstProgram(E_FIRST_PROG_DTV, E_AC_DC_BOOT_TYPE);
        }
#endif
    }
#endif

    MAPI_BOOL bEnable_4k2k_FRC = MAPI_FALSE;
    MAPI_BOOL bEnable_4k2k_Napoli = MAPI_FALSE;
    int eUrsaType= E_URSA_NONE;
    SystemInfo::GetInstance()->GetModuleParameter_bool("M_BACKEND:F_BACKEND_ENABLE_4K2K_NAPOLI", &bEnable_4k2k_Napoli);
    SystemInfo::GetInstance()->GetModuleParameter_bool("M_BACKEND:F_BACKEND_ENABLE_4K2K_FRC", &bEnable_4k2k_FRC);
    SystemInfo::GetInstance()->GetModuleParameter_int("M_URSA:F_URSA_URSA_TYPE", &eUrsaType, 0);
    if ((bEnable_4k2k_FRC == MAPI_TRUE) || ((bEnable_4k2k_Napoli == MAPI_TRUE) && (eUrsaType != E_URSA_8)))
    {
        /* for all source, mute once, avoid twice, or more times */
        /* 1. no signal will be fhd */
        /* 2. when input souce is 4K source, let mute time expand, important is ONCE MUTE */
        MSrv_Control::GetMSrvVideo()->SetOutputTiming(E_TIMING_2K1K, E_TIMING_DEFAULT);
    }
}

U32 GetMiniSecondCounter()
{
    struct timespec ts;
    U32 ms;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    ms = (ts.tv_sec * 1000) + (ts.tv_nsec / 1000000);
    if(ms == 0)
    {
        ms = 1;
    }
    return ms;
}


#define PROPERTY_BOOTANIM_LOG  "/var/property_bootanim.log"
#define ADVERT_PLAYER_TIMEOUT 90000  //ms
#define ADVERT_PLAYER_WAIT_INPUTSOURCE_TIMEOUT 5000  //ms

BOOL bStopAdvertPlayerFlag = FALSE;

#if(AUTO_TEST == 1)
void StartAutotest()
{
    #define CMDLINE_PATH "/proc/cmdline"
    #define KEYWORD_TO_AUTOTEST "autotest=true"
    FILE *cmdLine;
    char cmdLineBuf[1024];

    cmdLine = fopen(CMDLINE_PATH, "r");
    if(cmdLine != NULL)
    {
        fgets(cmdLineBuf, 1024, cmdLine);
        fclose(cmdLine);

        if(strstr(cmdLineBuf, KEYWORD_TO_AUTOTEST))
        {
           //start autotest
           char const* args[] = { "start", "autotest_client", NULL };
           execv("/system/bin/start", (char * const *)args);
        }
    }
    else
    {
        printf("\nAUTO_TEST is Enable, but read cmdline FAIL!!\n\n");
    }

}

void* AutotestThread(void*)
{
    FILE * pFile = NULL;
    char strtemp[128];

    // check boot anim = stopped
    pFile = fopen(PROPERTY_BOOTANIM_LOG, "r");
    if(pFile == NULL)
    {

    }
    else
    {
        while( fgets(strtemp,128,pFile) )
        {
            strtemp[127] = 0;
            if(strstr(strtemp, "stopped"))
            {
                StartAutotest();
                break;
            }
        }

        fclose(pFile);
        pFile = NULL;
    }

    pthread_exit(NULL);

}

void StartAutotestThread()
{
    pthread_t m_AutotestThread;

    int ret = pthread_create(&m_AutotestThread, NULL, AutotestThread, NULL);

    if (ret)
    {
        printf("tvos main, create StartAutotestThread failed\n");
        ASSERT(0);
    }

}
#endif

void* AdvertPlayerThread(void*)
{
    U32 u32StartTime = 0;
    U32 u32CurrentTime=0;
    U32 timeout =0;
    #if (ADVERT_BOOTING_ENABLE == 0)
    FILE * pFile = NULL;
    char strtemp[128];
    #endif
    BOOL bAdvPlayerPlayFlag = FALSE;

    u32StartTime=GetMiniSecondCounter();
    while((MSrv_Control::GetInstance()->GetCurrentInputSource() != MAPI_INPUT_SOURCE_STORAGE) && (bStopAdvertPlayerFlag == FALSE))
    {
        //check time out
        u32CurrentTime = GetMiniSecondCounter();
        if(u32CurrentTime > u32StartTime)
        {
            timeout = (u32CurrentTime-u32StartTime);
        }
        else
        {
            timeout = (u32StartTime-u32CurrentTime+1);
        }
        if(timeout > ADVERT_PLAYER_WAIT_INPUTSOURCE_TIMEOUT)
        {
            bStopAdvertPlayerFlag = TRUE;
        }
        usleep(100000);
    }
    bAdvPlayerPlayFlag = MSrv_Control::GetMSrvAdvertPlayer()->AdvPlayerPlay();
    if(bAdvPlayerPlayFlag == FALSE)
    {
        bStopAdvertPlayerFlag = TRUE;
    }
#if (ADVERT_BOOTING_ENABLE == 1)
     BOOL bStopAnim = FALSE;
#endif
    u32StartTime=GetMiniSecondCounter();
	//EosTek Patch Begin
    MS_USER_SOUND_SETTING stAudioSetting;
    int BOOTVIDEO_VOLUME_MAX = 5;
    MSrv_Control::GetMSrvSystemDatabase()->GetAudioSetting(&stAudioSetting);
    //if (stAudioSetting.Volume > BOOTVIDEO_VOLUME_MAX) {
        //printf("BootAnimation:set audio volume to %d instead of %d\n", BOOTVIDEO_VOLUME_MAX, stAudioSetting.Volume);
        MSrv_Control::GetMSrvSSSound()->SetVolume(BOOTVIDEO_VOLUME_MAX);
    //}
	// EosTek Patch End
    while(bStopAdvertPlayerFlag == FALSE)
    {
        // check advert play, decode done
        if(MSrv_Control::GetMSrvAdvertPlayer()->GetAdvPlayerPlayStatus() == E_DECODE_DONE)
        {
            bStopAdvertPlayerFlag = TRUE;
        }

        // check advert play, decode fail
        if(MSrv_Control::GetMSrvAdvertPlayer()->GetAdvPlayerPlayStatus() == EN_DECODE_FAIL)
        {
            bStopAdvertPlayerFlag = TRUE;

        }

        // check advert play, time done
        if(MSrv_Control::GetMSrvAdvertPlayer()->GetAdvPlayerPlayStatus() == EN_ADVERT_PLAYER_STATUS_MAX)
        {
            bStopAdvertPlayerFlag = TRUE;
        }
        // check current input source = storage
        if(MSrv_Control::GetInstance()->GetCurrentInputSource() != MAPI_INPUT_SOURCE_STORAGE)
        {
            bStopAdvertPlayerFlag = TRUE;
        }

        //check time out
        u32CurrentTime = GetMiniSecondCounter();
        if(u32CurrentTime > u32StartTime)
        {
            timeout = (u32CurrentTime-u32StartTime);
        }
        else
        {
            timeout = (u32StartTime-u32CurrentTime+1);
        }
        if(timeout > ADVERT_PLAYER_TIMEOUT)
        {
            bStopAdvertPlayerFlag = TRUE;
        }
#if (ADVERT_BOOTING_ENABLE == 1)
        mapi_video * pMapiVideo = mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_STORAGE);
        MS_BOOL  bXCReady = FALSE;
        if(MSrv_Control::GetMSrvAdvertPlayer()->GetAdvPlayerPlayStatus() == E_DECODE_DONE)
        {

            MAPI_BOOL bEnable_4k2k_Nikeu = MAPI_FALSE, bEnable_4k2k_Napoli = MAPI_FALSE;
            SystemInfo::GetInstance()->GetModuleParameter_bool("M_BACKEND:F_BACKEND_ENABLE_4K2K_NIKEU", &bEnable_4k2k_Nikeu);
            SystemInfo::GetInstance()->GetModuleParameter_bool("M_BACKEND:F_BACKEND_ENABLE_4K2K_NAPOLI", &bEnable_4k2k_Napoli);
            if((bEnable_4k2k_Nikeu == MAPI_FALSE) && (bEnable_4k2k_Napoli == MAPI_FALSE))
            {
                MSrv_Player *pMsrvPlayer = MSrv_Control::GetInstance()->GetMSrvPlayer(MSrv_Control::GetInstance()->GetCurrentInputSource());
                pMsrvPlayer->ShowFreezeImageByDFB(TRUE);
            }
            break;
        }
        else
        {
          if((MAPI_FALSE == pMapiVideo->IsBlueBlackScreen(&bXCReady)) && bXCReady&&!bStopAnim)
          {
            bStopAnim = TRUE;

#if (ENABLE_BACKEND == 1)
            MAPI_BOOL bEnable_4k2k_Napoli = MAPI_FALSE;
            int eUrsaType= E_URSA_NONE;
            SystemInfo::GetInstance()->GetModuleParameter_bool("M_BACKEND:F_BACKEND_ENABLE_4K2K_NAPOLI", &bEnable_4k2k_Napoli);
            SystemInfo::GetInstance()->GetModuleParameter_int("M_URSA:F_URSA_URSA_TYPE", &eUrsaType, 0);
            if ((bEnable_4k2k_Napoli == MAPI_TRUE) && (eUrsaType != E_URSA_8))
            {
                ST_VIDEO_INFO stVideoInfo;
                memset(&stVideoInfo, 0, sizeof(ST_VIDEO_INFO));
                MSrv_Player *pMainMsrvPlayer = MSrv_Control::GetInstance()->GetMSrvPlayer(MSrv_Control::GetInstance()->GetCurrentMainInputSource());
                pMainMsrvPlayer->GetVideoInfo(&stVideoInfo);
                printf("stVideoInfo.u16HResolution==%d,stVideoInfo.u16VResolution==%d\n",stVideoInfo.u16HResolution,stVideoInfo.u16VResolution);
                if(((stVideoInfo.u16HResolution > (WIDTH_4K2K  - OFFSET_4K2K)) && (stVideoInfo.u16HResolution < (WIDTH_4K2K + OFFSET_4K2K)))
                    || ((stVideoInfo.u16VResolution > (HEIGHT_4K2K - OFFSET_4K2K)) && (stVideoInfo.u16VResolution < (HEIGHT_4K2K + OFFSET_4K2K))))
                {
                    MSrv_Control::GetMSrvVideo()->SetOutputTiming(E_TIMING_4K2K, E_TIMING_DEFAULT);
                    MSrv_Control::GetMSrvVideo()->SelectWindow(MAPI_MAIN_WINDOW);   //FIXME : if setdispwindow is called before keepScalerOutput4k2k,video size will be wrong
                    mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stDispInfo;
                    stDispInfo.x  = 0;
                    stDispInfo.y  = 0;
                    stDispInfo.width  = WIDTH_4K2K;
                    stDispInfo.height = HEIGHT_4K2K;
                    MSrv_Control::GetMSrvVideo()->SetDisplayWindow(&stDispInfo);
                    MSrv_Control::GetMSrvVideo()->ScaleWindow();
                }
            }
#endif
            IEnvManager* pEnvMan = IEnvManager::Instance();
            U8 bootlogo_gopidx = 0;
            if (pEnvMan)
            {
               const char* pbootlogo_gop = NULL;
               pbootlogo_gop =pEnvMan->GetEnv("bootlogo_gopidx");
               if(pbootlogo_gop != NULL)
               {
                 bootlogo_gopidx =(MAPI_U32) strtoul(pbootlogo_gop, 0, 10);
               }
               printf("get bootlogo_gopidx:%d \n",bootlogo_gopidx);
               int i=0;
               int j=0;
               static MS_U8 u8GwinNum=0;
               for(i=0;i<bootlogo_gopidx;i++)
               {
                  u8GwinNum+=MApi_GOP_GWIN_GetGwinNum(i);
               }
               for(j=u8GwinNum;j<u8GwinNum+MApi_GOP_GWIN_GetGwinNum(bootlogo_gopidx);j++)
               {
                  if(MApi_GOP_GWIN_IsGWINEnabled(j)==TRUE)
                  {
                    MApi_GOP_GWIN_Enable(j,FALSE);
                  }
               }
            }
          }
        }
#else
     // check boot anim = stopped
        pFile = fopen(PROPERTY_BOOTANIM_LOG, "r");
        if(pFile == NULL)
        {

        }
        else
        {
            while( fgets(strtemp,128,pFile) )
            {
                strtemp[127] = 0;
                if(strstr(strtemp, "stopped"))
                {
                    bStopAdvertPlayerFlag = TRUE;
                    break;
                }
            }
            fclose(pFile);
            pFile = NULL;
        }
#endif
     usleep(300000);
    }
    MSrv_Control::GetMSrvAdvertPlayer()->AdvPlayerStop();
    MSrv_Control::GetMSrvAdvertPlayer()->AdvPlayerExit();
    SetInputSourceAfterPowerOn();
    pthread_exit(NULL);

}

#if (HBBTV_MWB_ENABLE == 1)
void* HbbTVLauncherThread(void*)
{
    MSrv_Control::GetInstance()->WaitBootComplete();
    struct stat st = {0};
    if (stat("/dev/shm", &st) == -1)
    {
        mkdir("/dev/shm", 0700);
    }
    if (!system("ps | grep MWB | grep -v grep > /dev/null"))
    {
        printf("<<%s>> <<%d>> MWB exists, skip fork", __PRETTY_FUNCTION__, __LINE__);
    }
    else
    {
        pid_t pid;
        pid = fork();
        if (pid < 0)
        {
            printf("<<%s>> <<%d>> fork failed! pid=%d", __PRETTY_FUNCTION__, __LINE__, pid);
        }
        else if (!pid)
        {
            printf("\033[31m<<***** MWB START FROM FORK ***** %s %s >>%d \033[m\r\n", __FILE__, __func__, __LINE__);
            const char path[] = "/applications/MWB/MWB";
            char const *argv[] = {path, "-qws", "-display", "directfb:layerid=0:debug", "-scale-window", "-frameless-window", "-nomouse", 0};
            char const *env[] = {"DFB_LAYER=0", "WINDOW_STACK=upper", "MSTAR_DEVICE_INPUT=0",
                                "LD_LIBRARY_PATH=/applications/appdata/:/applications/MWB/lib:/mslib:/mslib/utopia:/mslib/directfb-1.4-0/inputdrivers/:/config",
                                "MWB_CONFIG_PATH=/applications/MWB/config.xml", "QT_PLUGIN_PATH=/mslib/qt/plugins",
                                "GST_PLUGIN_PATH=/mslib/gstreamer-0.10", "GST_PLUGIN_SCANNER=/mslib/gstreamer-0.10/bin/gst-plugin-scanner", "GST_REGISTRY_FORK=no",
                                "CERT_CA_BUNDLE_PATH=/applications/MWB/certificates", "CERT_KEY_PATH=/applications/MWB/certificates/key.pem",
                                "CERT_CERT_PATH=/applications/MWB/certificates/cert.pem", "MSTAR_QT_KEYMAP=/applications/MWB/mstarkeymap.csv",
                                "QWS_NO_SHARE_FONTS=true", "FONTCONFIG_PATH=/applications/etc/fonts", "WEBKIT_APM_DISABLED=1", 0
                                };
            execve(path, const_cast<char **>(argv), const_cast<char **>(env));
        }
        else
        {
            printf("<<%s>> <<%d>> fork pid=%d", __PRETTY_FUNCTION__, __LINE__, pid);
        }
    }
    pthread_exit(NULL);
}
#endif

void StartMsrv(void)
{
    BOOL bAdvPlayerInitFlag=FALSE;
    MS_USER_SYSTEM_SETTING stGetSystemSetting;
//interalSpeaker mute
    IEnvManager* pEnvMan = IEnvManager::Instance();
    IEnvManager* pEnvMan_1 = IEnvManager::Instance();
    IEnvManager_scope_lock block(pEnvMan);
    IEnvManager_scope_lock block_1(pEnvMan_1);
    int size=pEnvMan->QueryLength("interalSpeaker");
    int size_1=pEnvMan_1->QueryLength("EarPhone");
    char tmp[size+1];
    char tmp_1[size_1+1];
    memset(tmp,0,size+1);
    memset(tmp_1,0,size_1+1);
    pEnvMan->GetEnv_Protect("interalSpeaker",tmp,size);
    pEnvMan_1->GetEnv_Protect("EarPhone",tmp_1,size_1);
    if(tmp[0] == 0)
    	{
        pEnvMan->SetEnv_Protect("interalSpeaker", "on");
        pEnvMan->SaveEnv();
        MSrv_Control_common::SetGpioDeviceStatus(MUTE, FALSE);
    	}
    if(tmp_1[0] == 0)
    	{
        pEnvMan_1->SetEnv_Protect("EarPhone", "on");
        pEnvMan_1->SaveEnv();
        MSrv_Control::GetInstance()->SetEarPhoneByOnOff(TRUE);
    	}

	if(strncmp(tmp,"on",2) == 0)
	        MSrv_Control_common::SetGpioDeviceStatus(MUTE, FALSE);
	else
		        MSrv_Control_common::SetGpioDeviceStatus(MUTE, TRUE);
	if(strncmp(tmp_1,"on",2) == 0)
		MSrv_Control::GetInstance()->SetEarPhoneByOnOff(TRUE);
	else
		MSrv_Control::GetInstance()->SetEarPhoneByOnOff(FALSE);

	// EarPhone Mute off
	//MSrv_Control::GetInstance()->SetEarPhoneByOnOff(TRUE);
    //MSrv_Control::GetInstance()->SetIRLedByOnOff(TRUE);
    MSrv_Control::GetInstance()->SetIRLedByOnOff(FALSE);

#if (STB_ENABLE == 1)
    printf("Insert DAC table to MstarSDK %p (%d)\n", DACMAP_Main, DAC_PANEL_NUMS);
    mapi_display::setDacTable((void*)DACMAP_Main, (size_t)DAC_PANEL_NUMS);

    T_MS_VIDEO dispInfo;

    MSrv_Control::GetMSrvSystemDatabase()->GetUserSystemSetting(&stGetSystemSetting);

    if(stGetSystemSetting.enInputSourceType >= MAPI_INPUT_SOURCE_STORAGE)
    {
        stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_DTV;
    }
    MSrv_Control::GetMSrvSystemDatabase()->GetVideoSetting(&dispInfo, &stGetSystemSetting.enInputSourceType);
#endif
#if (ADVERT_BOOTING_ENABLE == 0)
#if (PLATFORM_TYPE == MSTAR_ANDROID)
    property_set("mstar.videoadvert.finished", "1");
#endif
#endif

    bAdvPlayerInitFlag = MSrv_Control::GetMSrvAdvertPlayer()->AdvPlayerInit();

    if(bAdvPlayerInitFlag == TRUE)
    {
        pthread_t m_AdvertPlayerThread;
        int ret = pthread_create(&m_AdvertPlayerThread, NULL, AdvertPlayerThread, NULL);
        if (ret)
        {
            printf("tvos main, create AdvertPlayerThread failed\n");
            ASSERT(0);
        }
    }
    else
    {
        bStopAdvertPlayerFlag = TRUE;
        SetInputSourceAfterPowerOn();
    }
    MSrv_Control::GetInstance()->StartThreadMonitor();

    ////////////////////////////////////////////////////////////////
    // Reset DcPowerOffMode Flag //
    //MS_USER_SYSTEM_SETTING stGetSystemSetting;
    MSrv_Control::GetMSrvSystemDatabase()->GetUserSystemSetting(&stGetSystemSetting);
    if(stGetSystemSetting.fDcPowerOFFMode != 0)
    {
        U8  DcOffMode = FALSE;
        MSrv_Control::GetMSrvSystemDatabase()->SetDcPoweroffMode(DcOffMode);
    }

}

static PlayerImplService *pPlayerImplServ = NULL;
static ScanManagerImplService *pScanManagerImplServ = NULL;
static ChannelManagerService *pChannelManager = NULL;
static AudioManagerService *pAudioManager=NULL;
static TvManagerService *pTvManager=NULL;
//static SampleService *pSampleService = NULL;
static PictureManagerService *pPictureManager=NULL;
static TimerManagerService *pTimerManager=NULL;
static DatabaseManagerService *pDatabaseManager=NULL;
static FactoryManagerService *pFactoryManager=NULL;
static ThreeDimensionManagerService *pThreeDimensionManger=NULL;
static ThirdPartyTvManagerService *pThirdPartyTvManager=NULL;
static SubtitleManagerService *pSubtitleManager=NULL;
static PvrManagerService  *pPvrManager=NULL;
static OadManagerService   *pOadManager=NULL;
#if(EPG_ENABLE ==1)
static EpgManagerService   *pEpgManager=NULL;
#endif
#if (STR_ENABLE == 0)
static LogoManagerService  *pLogoManager=NULL;
#endif
#if (CEC_ENABLE == 1)
static CecManagerService *pCecManager=NULL;
#endif
static MhlManagerService *pMhlManager=NULL;
static ParentalcontrolManagerService *pParentalcontrolManager=NULL;
static CiManagerService *pCiManager=NULL;
static DmxManagerService *pDmxManager=NULL;
static ScManagerService *pScManager=NULL;
static PcmciaManagerService *pPcmciaManager=NULL;
static PipManagerService *pPipManager=NULL;
static CaManagerService *pCaManager=NULL;
#if (PLATFORM_TYPE == MSTAR_PURESN)
static NetworkManagerService *pNetworkManager=NULL;
static UsbMassStorageManagerService *pUsbMassStorageManager=NULL;
#endif

#define DEVICE "/dev/ir"

void * tvos_ir_thread(void * pData)
{
    IDirectFB *dfb = MSystem::GetInstance()->dfb;
    IDirectFBEventBuffer *input_events = NULL;
    if (DFB_OK != dfb->CreateInputEventBuffer(dfb, DICAPS_KEYS, DFB_TRUE, &input_events))
    {
        printf("tvos_ir_thread cannot create event buffer.\n");
        ASSERT(0);
    }

    while(1)
    {

        if (bStopAdvertPlayerFlag && !MSystem::IsTvosIr())
        {
#if (ENABLE_4K2K_EDISON_MFC_DEMO == 0) && (ENABLE_4K2K_EDISON_SR_DEMO == 0)
            break;
#endif
        }

        input_events->WaitForEventWithTimeout(input_events, 0, 300);

        if (DFB_OK == input_events->HasEvent(input_events))
        {
            printf("tvos has key\n");
            DFBInputEvent evt;
            if (input_events->GetEvent(input_events, (DFBEvent*)&evt) == DFB_OK)
            {
                printf("tvos get key\n");
                if (evt.type == DIET_KEYPRESS)
                {
                    printf("evt.key_code:%d, evt.key_id:%d, evt.key_symbol:%d\n", evt.key_code, evt.key_id, evt.key_symbol);
                    switch (evt.key_code)
                    {
                    // EosTek Patch Begin
                    /*
                        case 0x1F://IRKEY_CHANNEL_PLUS:
                        {
                            printf("KEY CH+\n");
                            MAPI_INPUT_SOURCE_TYPE enCurrentInputType;
                            enCurrentInputType = MSrv_Control::GetInstance()->GetCurrentInputSource();
                        #if (STB_ENABLE == 0)
                            if((enCurrentInputType == MAPI_INPUT_SOURCE_ATV) || (enCurrentInputType == MAPI_INPUT_SOURCE_DTV))
                        #else
                            if(enCurrentInputType == MAPI_INPUT_SOURCE_DTV)
                        #endif
                            {
                                BOOL bRet;
                                bRet = MSrv_Control_common::GetMSrvChannelManager()->ProgramUp(E_PROG_LOOP_ALL);

                                if(bRet == TRUE)
                                {
                                    printf("Channel Change OK\n");
                                }
                                else
                                {
                                    printf("Channel Change FAIL\n");
                                }
                            }
                        }
                        break;
                        case 0x19://IRKEY_CHANNEL_MINUS:
                        {
                            printf("KEY CH-\n");
                            MAPI_INPUT_SOURCE_TYPE enCurrentInputType;
                            enCurrentInputType = MSrv_Control::GetInstance()->GetCurrentInputSource();
                        #if (STB_ENABLE == 0)
                            if((enCurrentInputType == MAPI_INPUT_SOURCE_ATV) || (enCurrentInputType == MAPI_INPUT_SOURCE_DTV))
                        #else
                            if(enCurrentInputType == MAPI_INPUT_SOURCE_DTV)
                        #endif
                            {
                                BOOL bRet;
                                bRet = MSrv_Control_common::GetMSrvChannelManager()->ProgramDown(E_PROG_LOOP_ALL);

                                if(bRet == TRUE)
                                {
                                    printf("Channel Change OK\n");
                                }
                                else
                                {
                                    printf("Channel Change FAIL\n");
                                }
                            }
                        }
                        break;
                         */
                         // EosTek Patch End 
                         	// EosTek Patch Begin
						/*
                        case 0x16://IRKEY_VOLUME_PLUS:
                        {
                            printf("KEY VOL+\n");
                            MS_USER_SOUND_SETTING stAudioSetting;
                            MSrv_Control::GetMSrvSystemDatabase()->GetAudioSetting(&stAudioSetting);

                            if (stAudioSetting.Volume != 255)
                                stAudioSetting.Volume += 1;

                            MSrv_Control::GetMSrvSSSound()->SetVolume(stAudioSetting.Volume);
                            MSrv_Control::GetMSrvSystemDatabase()->SetAudioSetting(&stAudioSetting);
                            MSrv_Control::GetMSrvSSSound()->SetMuteStatus(MUTE_BYUSER, FALSE);
                        }
                        break;
                        case 0x15://IRKEY_VOLUME_MINUS:
                        {
                            printf("KEY VOL-\n");
                            MS_USER_SOUND_SETTING stAudioSetting;
                            MSrv_Control::GetMSrvSystemDatabase()->GetAudioSetting(&stAudioSetting);

                            if (stAudioSetting.Volume != 0)
                                stAudioSetting.Volume -= 1;

                            MSrv_Control::GetMSrvSSSound()->SetVolume(stAudioSetting.Volume);
                            MSrv_Control::GetMSrvSystemDatabase()->SetAudioSetting(&stAudioSetting);
                            MSrv_Control::GetMSrvSSSound()->SetMuteStatus(MUTE_BYUSER, FALSE);
                        }
                        break;                
                        case 0x5A://IRKEY_MUTE:
                        {
                            printf("MUTE\n");
                            bool currentMute = MSrv_Control::GetInstance()->GetMuteFlag();
                            MSrv_Control::GetMSrvSSSound()->SetMuteStatus(MUTE_BYUSER, !currentMute);
                        }
                        break;
                        case 0x1B://IRKEY_EXIT:
                        {
                            printf("exit\n");
                            MSystem::DisableTvosIr();
                            bStopAdvertPlayerFlag = TRUE;
                        }
                        break;
                        */
                       // EosTek Patch End 
#if (ENABLE_4K2K_EDISON_MFC_DEMO == 1 && ENABLE_BACKEND == 1)
                        case 0x43://IRKEY_SIZE
                        {
                            printf("enable MFC_LR demo\n");
                            MSrv_Control::GetMSrvBackend()->SetMfcDemoMode(MSrv_Picture::E_MFC_DEMO_LR);
                        }
                        break;
                        case 0x5F://IRKEY_CLOCK
                        {
                            printf("disable MFC demo\n");
                            MSrv_Control::GetMSrvBackend()->SetMfcDemoMode(MSrv_Picture::E_MFC_DEMO_OFF);
                        }
                        break;
#endif
#if (ENABLE_4K2K_EDISON_SR_DEMO == 1 && ENABLE_BACKEND == 1)
                        case 0x00://IR_HOLD
                        {
                            printf("disable SR demo\n");
                            MSrv_Control::GetMSrvBackend()->SetSRDemoMode(MSrv_Picture::E_SR_DEMO_OFF);
                        }
                        break;
                        case 0x0C://IR_UPDATE
                        {
                            printf("enable SR_Low demo\n");
                            MSrv_Control::GetMSrvBackend()->SetSRDemoMode(MSrv_Picture::E_SR_DEMO_LOW);
                        }
                        break;
                        case 0x4F://IR_REVEAL
                        {
                            printf("disable SR_Middle demo\n");
                            MSrv_Control::GetMSrvBackend()->SetSRDemoMode(MSrv_Picture::E_SR_DEMO_MIDDLE);
                        }
                        break;
                        case 0x5E://IR_SUBCODE
                        {
                            printf("disable SR_High demo\n");
                            MSrv_Control::GetMSrvBackend()->SetSRDemoMode(MSrv_Picture::E_SR_DEMO_HIGH);
                        }
                        break;
#endif
                        default:
                            break;
                    }
                }
            }
        }
    }

    input_events->Release(input_events);

    return NULL;
}

static bool bAnServiceInitDone = false;

void * AndroidServiceInitThread(void * pData)
    {
    EventManager *em = EventManager::GetInstance();

    printf("\nevent manager init\n");
    em->Init();


    pParentalcontrolManager = ParentalcontrolManagerService::instantiate();
    pScanManagerImplServ = ScanManagerImplService::instantiate();
    pPlayerImplServ = PlayerImplService::instantiate();
    const MAPI_VIDEO_INPUTSRCTABLE *pSrcTable;
    pSrcTable = MSrv_Control::GetInstance()->GetSourceList();

#if (STB_ENABLE == 0)
    if(pSrcTable[MAPI_INPUT_SOURCE_ATV].u32EnablePort)
    {
      MSrv_Control_TV::GetMSrvAtv()->RegisterService(pPlayerImplServ);
    }
#endif

    if(pSrcTable[MAPI_INPUT_SOURCE_DTV].u32EnablePort)
    {
      //Register playerImpl to all DTVPlayers.
      for(U8 u8RouteIndex = 0; u8RouteIndex < MSrv_Control::GetInstance()->GetDtvRouteCount(); u8RouteIndex++)
      {
          MSrv_DTV_Player *player = MSrv_Control::GetInstance()->GetMSrvDtvByIndex(u8RouteIndex);
          player->RegisterService(pPlayerImplServ);
          printf("dtv route[%d]=0x%x\n", u8RouteIndex, (int)player);
      }
    }

#if (STB_ENABLE == 0)
    if(pSrcTable[MAPI_INPUT_SOURCE_CVBS].u32EnablePort)
    {
      MSrv_Control_TV::GetMSrvAv()->RegisterService(pPlayerImplServ);
    }
    if(pSrcTable[MAPI_INPUT_SOURCE_SVIDEO].u32EnablePort)
    {
      MSrv_Control_TV::GetMSrvSv()->RegisterService(pPlayerImplServ);
    }
    if(pSrcTable[MAPI_INPUT_SOURCE_YPBPR].u32EnablePort)
    {
      MSrv_Control_TV::GetMSrvComp()->RegisterService(pPlayerImplServ);
    }
    if(pSrcTable[MAPI_INPUT_SOURCE_HDMI].u32EnablePort)
    {
      MSrv_Control_TV::GetMSrvHdmi()->RegisterService(pPlayerImplServ);
    }
    if(pSrcTable[MAPI_INPUT_SOURCE_STORAGE].u32EnablePort)
    {
      MSrv_Control_TV::GetMSrvStorage()->RegisterService(pPlayerImplServ);
    }
    if(pSrcTable[MAPI_INPUT_SOURCE_VGA].u32EnablePort)
    {
      MSrv_Control_TV::GetMSrvVga()->RegisterService(pPlayerImplServ);
    }
    if(pSrcTable[MAPI_INPUT_SOURCE_SCART].u32EnablePort)
    {
      MSrv_Control::GetMSrvScart()->RegisterService(pPlayerImplServ);
    }
#else
    if(pSrcTable[MAPI_INPUT_SOURCE_HDMI].u32EnablePort)
    {
#if (ENABLE_HDMI_RX == 1)
      MSrv_Control_STB::GetMSrvHdmi()->RegisterService(pPlayerImplServ);
#endif
    }
    if(pSrcTable[MAPI_INPUT_SOURCE_STORAGE].u32EnablePort)
    {
      MSrv_Control_STB::GetMSrvStorage()->RegisterService(pPlayerImplServ);
    }

#endif
    if(pSrcTable[MAPI_INPUT_SOURCE_DTV].u32EnablePort ||pSrcTable[MAPI_INPUT_SOURCE_ATV].u32EnablePort)
    {
        pChannelManager = ChannelManagerService::instantiate();
    }

    pAudioManager = AudioManagerService::instantiate();
    pTvManager = TvManagerService::instantiate();
    MSrv_Control::GetInstance()->RegisterService(pTvManager);
    MSrv_Control::GetMSrvVideo()->RegisterService(pTvManager);

    pTvManager->RegisterToEM(E_TVMANAGER);
    pPlayerImplServ->RegisterToEM(E_PLAYERIMPL);


   // pSampleService = SampleService::instantiate();
    pPictureManager = PictureManagerService::instantiate();
    MAPI_BOOL bEnable_4k2k_Nikeu = MAPI_FALSE, bEnable_4k2k_Napoli = MAPI_FALSE;
    SystemInfo::GetInstance()->GetModuleParameter_bool("M_BACKEND:F_BACKEND_ENABLE_4K2K_NIKEU", &bEnable_4k2k_Nikeu);
    SystemInfo::GetInstance()->GetModuleParameter_bool("M_BACKEND:F_BACKEND_ENABLE_4K2K_NAPOLI", &bEnable_4k2k_Napoli);
    if((bEnable_4k2k_Nikeu == MAPI_TRUE) || (bEnable_4k2k_Napoli == MAPI_TRUE))
    {
        MSrv_Control::GetMSrvPicture()->RegisterService(pPictureManager);
        pPictureManager->RegisterToEM(E_PICTUREMANAGER);
    }
    else
        pPictureManager->RegisterToEM(E_PICTUREMANAGER);
    pTimerManager= TimerManagerService::instantiate();
    MSrv_Control::GetMSrvTimer()->RegisterService(pTimerManager);
    pTimerManager->RegisterToEM(E_TIMERMANAGER);

    pDatabaseManager = DatabaseManagerService::instantiate();

    pFactoryManager =  FactoryManagerService::instantiate();


    pThreeDimensionManger = ThreeDimensionManagerService::instantiate();
#if (STEREO_3D_ENABLE == 1)
    MSrv_Control::GetMSrv3DManager()->RegisterService(pThreeDimensionManger);
    pThreeDimensionManger->RegisterToEM(E_3DMANAGER);
#endif

    pSubtitleManager = SubtitleManagerService::instantiate();

    pPvrManager = PvrManagerService::instantiate();
    pOadManager = OadManagerService::instantiate();
#if(EPG_ENABLE ==1)
    pEpgManager  =  EpgManagerService::instantiate();
#endif
#if (STR_ENABLE == 0)
    pLogoManager =  LogoManagerService::instantiate();
#endif
#if (CEC_ENABLE == 1)
    pCecManager= CecManagerService::instantiate();
    MSrv_Control::GetMSrvCEC()->RegisterService(pCecManager);
    pCecManager->RegisterToEM(E_CECMANAGER);
#endif
    pMhlManager= MhlManagerService::instantiate();
#if (MHL_ENABLE == 1)
    MSrv_Control::GetMSrvMHL()->RegisterService(pMhlManager);
    pMhlManager->RegisterToEM(E_MHLMANAGER);
#endif

    pCiManager=  CiManagerService::instantiate();
#if(CI_ENABLE == 1)
    MSrv_Control::GetMSrvCIMMI()->RegisterService(pCiManager);
    pCiManager->RegisterToEM(E_CIMANAGER);
#endif
    pThirdPartyTvManager = ThirdPartyTvManagerService::instantiate();

    pPipManager = PipManagerService::instantiate();
    pPipManager->RegisterToEM(E_PIPMANAGER);
    pDmxManager = DmxManagerService::instantiate();
    pScManager = ScManagerService::instantiate();
    pPcmciaManager = PcmciaManagerService::instantiate();

    pCaManager = CaManagerService::instantiate();

#if(CA_ENABLE == 1)
    MSrv_Control_DVB::GetMSrvCA()->RegisterService(pCaManager);
    pCaManager->RegisterToEM(E_CAMANAGER);
#endif
    VideoSetService::instantiate();

#if (PLATFORM_TYPE == MSTAR_PURESN)
    pNetworkManager = NetworkManagerService::instantiate();

    pUsbMassStorageManager = UsbMassStorageManagerService::instantiate();
#endif

#if (MSTAR_TVOS_MDMPPLAYER == 1)
    MMService_Instantiate();
#endif
#if (MSTAR_IPC == 1)
#if (APP_IPC_SERVICE_ENABLE == 1)
    AppIpcService_Instantiate();
#endif
#if (MM_IPC_SERVICE_ENABLE == 1)
    MMService_Instantiate();
#endif
#endif
#if(MCAST_ENABLE == 1)
    MCastService::instantiate();
#endif
    printf("\neventmanager start thread pool\n");
    em->StartPostThreadPool();
#if (PLATFORM_TYPE == MSTAR_ANDROID)
    bAnServiceInitDone = true;

    char value[8] = {};
    printf("\nstart wait boot complete\n");
    while(1) {
        sleep(1);

        property_get("sys.boot_completed",value,"0");

        if(strcmp("1",value)== 0) {
            MSrv_Control::GetInstance()->SetBootComplete(TRUE);
            break;
        }
    }
#else
    MSrv_Control::GetInstance()->SetBootComplete(TRUE);
#endif
    printf("\n boot complete\n");
    pthread_exit(0);

}
void handler(int nSignal, siginfo_t* si, void* arg)
{
    printf("[SIGNAL CATCH]\n");
    printf("PID:%d, ERRNO:%d\n", si->si_pid, si->si_errno);
    printf("dump stack==========>\n");
    MSrv_Control::GetInstance()->SetWatchdogTimer(60);
#if defined(ENABLE_MBACKTRACE)  && (ENABLE_MBACKTRACE == 1)
    enableBacktrace(MAPI_TRUE);
    printBacktrace(0);
    enableBacktrace(MAPI_FALSE);
#endif
    printf("==============================\n");
    kill(si->si_pid, nSignal);
}
int main(int argc, char **argv)
{
    struct timespec start_ts;
    MAPI_U32 start_ms;
    clock_gettime(CLOCK_MONOTONIC, &start_ts);
    start_ms = (start_ts.tv_sec * 1000) + (start_ts.tv_nsec / 1000000);

    printf("TVOS SN Start Check(need add timer gap)%u\n",(unsigned int)start_ms);

    bAnServiceInitDone = false;
    struct rlimit limit;
    int resource;
    resource = RLIMIT_CORE;
    limit.rlim_cur = RLIM_INFINITY;
    limit.rlim_max = RLIM_INFINITY;
    setrlimit(resource, &limit);

    // Restore android service permission
    umask(0);

    /* Init property */
#if (PLATFORM_TYPE == MSTAR_ANDROID)
    __system_properties_init();
#endif
    struct sigaction sa = {};
    memset(&sa, 0, sizeof(struct sigaction));
    sigemptyset(&sa.sa_mask);
    sa.sa_sigaction = handler;
    //sa.sa_flags = SA_SIGINFO|SA_ONSTACK;
    sa.sa_flags = SA_SIGINFO|SA_RESETHAND;  // Reset signal handler to system default after signal triggered
    sigaction(SIGILL, &sa, NULL);
    sigaction(SIGABRT, &sa, NULL);
    sigaction(SIGFPE, &sa, NULL);
    sigaction(SIGKILL, &sa, NULL);
    sigaction(SIGSEGV, &sa, NULL);
    sigaction(SIGBUS, &sa, NULL);

    int retChdir;
    retChdir = chdir("/applications/bin");
    if (retChdir)
        printf("change directory to /applications/bin  fail:%s",strerror(errno));

#if(AUTO_TEST == 1)
        printf("\033[1;31m  sn init-chk#0 \033[0m\n");
#endif
    //set hw init done property
#if (PLATFORM_TYPE == MSTAR_ANDROID)
    property_set("mstar.hw.init", "1");
#endif

    MAPI_BOOL bEnable_4k2k_Nikeu = MAPI_FALSE, bEnable_4k2k_Napoli = MAPI_FALSE;
    int eUrsaType = E_URSA_NONE;
    SystemInfo::GetInstance()->GetModuleParameter_int("M_URSA:F_URSA_URSA_TYPE", &eUrsaType, 0);
    SystemInfo::GetInstance()->GetModuleParameter_bool("M_BACKEND:F_BACKEND_ENABLE_4K2K_NIKEU", &bEnable_4k2k_Nikeu);
    SystemInfo::GetInstance()->GetModuleParameter_bool("M_BACKEND:F_BACKEND_ENABLE_4K2K_NAPOLI", &bEnable_4k2k_Napoli);
    if((bEnable_4k2k_Nikeu == MAPI_TRUE) || (bEnable_4k2k_Napoli == MAPI_TRUE))
    {
#if (PLATFORM_TYPE == MSTAR_ANDROID)
        property_set("mstar.4k2k.photo", "1");
#endif
    }
    if((bEnable_4k2k_Napoli == MAPI_TRUE) && (eUrsaType == E_URSA_8))
    {
#if (PLATFORM_TYPE == MSTAR_ANDROID)
        property_set("mstar.4k2k.enable", "1");
#endif
    }


    MSrv_Control::Build();
    MSrv_Control::GetInstance()->SetBootComplete(FALSE);

#if( AUTO_TEST == 1 )
        printf("\033[1;31m  sn init-chk#1 \033[0m\n");
#endif
    //mapi_interface::Get_mapi_display()->OnOff(TRUE);
#if( AUTO_TEST == 1 )
    printf("\033[1;31m  sn init-chk#2 \033[0m\n");
#endif

    if (MSrv_Control::GetInstance()->IsSupportTheDTVSystemType(ATSC_ENABLE))
    {
        printf("[WARN]!!![WARN]!!![WARN]!!![WARN]!!!\n");
        printf("This is DVB main function, SHOULD NOT fall into ATSC System case!!!\n");
        ASSERT(0);
    }
    else if (MSrv_Control::GetInstance()->IsSupportTheDTVSystemType(ISDB_ENABLE))
    {
#if (PLATFORM_TYPE == MSTAR_ANDROID)
        property_set("mstar.tvsystem", tvsystem[TV_SYSTEM_ISDB][0].c_str());
        property_set("mstar.tvsystem2", tvsystem[TV_SYSTEM_ISDB][1].c_str());
#endif
    }
    else if (MSrv_Control::GetInstance()->IsSupportTheDTVSystemType(DTMB_ENABLE))
    {
#if (PLATFORM_TYPE == MSTAR_ANDROID)
        property_set("mstar.tvsystem", tvsystem[TV_SYSTEM_DTMB][0].c_str());
        property_set("mstar.tvsystem2", tvsystem[TV_SYSTEM_DTMB][1].c_str());
#endif
    }
    else if (MSrv_Control::GetInstance()->IsSupportTheDTVSystemType(DVBT_ENABLE)
            || MSrv_Control::GetInstance()->IsSupportTheDTVSystemType(DVBC_ENABLE)
            || MSrv_Control::GetInstance()->IsSupportTheDTVSystemType(DVBS_ENABLE))
    {
#if (PLATFORM_TYPE == MSTAR_ANDROID)
        property_set("mstar.tvsystem", tvsystem[TV_SYSTEM_DVBT][0].c_str());
        property_set("mstar.tvsystem2", tvsystem[TV_SYSTEM_DTMB][1].c_str());
#endif
    }
    else
    {
        printf("No specified Tv System, Default fall into DVBT System\n");
#if (PLATFORM_TYPE == MSTAR_ANDROID)
        property_set("mstar.tvsystem", tvsystem[TV_SYSTEM_DVBT][0].c_str());
        property_set("mstar.tvsystem2", tvsystem[TV_SYSTEM_DTMB][1].c_str());
#endif
    }

    mapi_interface::SetGopForceWrite(true);
    MSystem::Initialize(argc, argv, false);
#if (ISDB_SYSTEM_ENABLE == 1)
    MSystem::OSD_RESOLUTION_WIDTH = 1280;
    MSystem::OSD_RESOLUTION_HEIGHT = 720;
#endif
    MSystem::InitializeDFBLayer();
    mapi_interface::SetGopForceWrite(false);
#if(AUTO_TEST == 1)
    printf("\033[1;31m sn init-chk#3 \033[0m\n");
#endif
    MSrv_Control::GetInstance()->Initialize();

    //set audio init done property
#if (PLATFORM_TYPE == MSTAR_ANDROID)
    property_set("mstar.audio.init", "1");
#endif


#if(GINGA_ENABLE == 1)
{
#if (PLATFORM_TYPE == MSTAR_ANDROID)
    property_set("mstar.ginga", "1");
#endif

    int ret = setenv("LD_LIBRARY_PATH","$LD_LIBRARY_PATH:/mslib/ginga/jvm/lib:/config",1);
#if defined(ONE_GOP_ENABLE) && (ONE_GOP_ENABLE == 1)
    ret |= setenv("GINGA_DFB_LAYER","0",1);
#else
    ret |= setenv("GINGA_DFB_LAYER","1",1);
#endif
    ret |= setenv("GINGA_DFB_LAYER_WIDTH","1280",1);
    ret |= setenv("GINGA_DFB_LAYER_HEIGHT","720",1);
    ret |= setenv("GINGA_DFB_BLITAUX_SUPPORT","0",1);
    ret |= setenv("GINGA_PROFILE","B",1);
    ret |= setenv("GINGA_MSLIB_PATH","/mslib",1);
    ret |= setenv("FONTCONFIG_PATH","/applications/etc/fonts",1);

    if(0!=ret)
    {
         printf("\nOpen state_entering fail in STATE_ENTERING_BREAK~~\n");
         ASSERT(0);
    }
}
#endif

    //disable Layer 0
    MSystem::GetInstance()->SetGOPOnOffbyLayer(0, false);
#if( AUTO_TEST == 1)
    printf("\033[1;31m sn init-chk#4 \033[0m\n");
#endif

    pthread_attr_t ASthr_attr;
    pthread_attr_init(&ASthr_attr);
    pthread_attr_setstacksize(&ASthr_attr, PTHREAD_STACK_SIZE);
    pthread_attr_setdetachstate(&ASthr_attr, PTHREAD_CREATE_DETACHED);

    pthread_t ASthread_id;

    ProcessState::self();
    int ret = pthread_create(&ASthread_id, &ASthr_attr, AndroidServiceInitThread, NULL);
    if (ret)
    {
            printf("tvos main, create android service init thread failed\n");
            ASSERT(0);
    }
    StartMsrv();

#if (HBBTV_MWB_ENABLE == 1)
    pthread_attr_t m_HbbtvThr_attr;
    pthread_attr_init(&m_HbbtvThr_attr);
    pthread_attr_setstacksize(&m_HbbtvThr_attr, PTHREAD_STACK_SIZE);
    pthread_attr_setdetachstate(&m_HbbtvThr_attr, PTHREAD_CREATE_DETACHED);
    pthread_t m_HbbtvLauncherThread;
    ret = pthread_create(&m_HbbtvLauncherThread, &m_HbbtvThr_attr, HbbTVLauncherThread, NULL);
    if (ret)
    {
        printf("tvos main, create HbbtvLauncherThread failed\n");
        ASSERT(0);
    }
#endif

#if (DVBS_SYSTEM_ENABLE==1)
    BOOL InitFlag;
    MSrv_Control::GetMSrvSystemDatabase()->IsInstallationGuideEnabled(&InitFlag);
    if (true == InitFlag)
    {
        U8 u8DvbsRoute = 0;

        for (U8 i = 0; i < MAXROUTECOUNT; i++)
        {
            if ((MSrv_Control::GetRouteTVMode(i) == E_ROUTE_DVBS) || (MSrv_Control::GetRouteTVMode(i) == E_ROUTE_DVBS2))
            {
                u8DvbsRoute= i;
                break;
            }
        }
        if(MSrv_Control::IsSupportTheDTVSystemType(DVBS2_ENABLE))
        {
            MSrv_DTV_Player_DVBS * pDtv = NULL;
            pDtv = dynamic_cast<MSrv_DTV_Player_DVBS*>(MSrv_Control::GetInstance()->GetMSrvDtvByIndex(u8DvbsRoute));
            ASSERT(pDtv);
            pDtv->ReSetSatelliteDB();
        }
        InitFlag = false;
        MSrv_Control::GetMSrvSystemDatabase()->SetInstallationguideEnabled(&InitFlag);
    }
#endif
#if( AUTO_TEST == 1)
    printf("\033[1;31m sn init-chk#5 \033[0m\n");
#endif

#if (PLATFORM_TYPE == MSTAR_ANDROID)
    while(!bAnServiceInitDone)
        usleep(100*1000);
#endif
    ProcessState::self()->startThreadPool();

#if (STR_ENABLE == 1)
    MSrv_Control::GetInstance()->InitializeStr();
#endif

    MSystem::EnableTvosIr();

    if (MSystem::IsTvosIr())
    {
        pthread_attr_t thr_attr;
        pthread_attr_init(&thr_attr);
        pthread_attr_setstacksize(&thr_attr, PTHREAD_STACK_SIZE);

        pthread_t thread_id;

        int ret = pthread_create(&thread_id, &thr_attr, tvos_ir_thread, NULL);
        if (ret)
        {
            printf("tvos main, create ir thread failed\n");
            ASSERT(0);
        }
    }

    printf("\033[41;32m ####### Final Check Ponint ##### \033[0m\n");
    IPCThreadState::self()->joinThreadPool();
    delete (MSrv_Control::GetInstance()); // parasoft-suppress MRM-09 "because the pointer will be destroyed immediately"
    EventManager::Destroy();

    return 0;
}
