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
#include "MSrv_Timer.h"

// headers of standard C libs
#include "mthread.h"
#include "MSystem.h"
#include <unistd.h>
#include <sys/prctl.h>
#include <math.h>
#include <limits.h>
// headers of standard C++ libs
#include <sstream>
// headers of the same layer's
#include "MSrv_Control.h"
#include "MSrv_System_Database.h"
#include "MSrv_ChannelManager.h"
//EosTek Patch Begin
#include "MSrv_Factory_Mode.h"
//EosTek Patch End
#if (MSTAR_TVOS == 0)
#include "MSrv_Ntp_Entity.h"
#endif
#if (ATSC_SYSTEM_ENABLE == 0)
#include "MSrv_DTV_Player_DVB.h"
#include "mapi_dvb_utility.h"
#else
#include "MSrv_DTV_Player.h"
#endif

#if (DVB_ENABLE==1)
#include "MSrv_System_Database_DVB.h"
#endif

#if (STB_ENABLE == 0)
#include "MSrv_ATV_Player.h"
#endif
#if (MSTAR_TVOS == 0)
#include "MSrv_Network_Control.h"
#endif
// headers of underlying layer's
#include "debug.h"
#include "mapi_interface.h"
#include "mapi_types.h"
#include "mapi_system.h"
#include "mapi_utility.h"
#if (STR_ENABLE == 1)
#include "mapi_str.h"
#endif

#if (MWB_LAUNCHER_ENABLE == 1)
#include "MstarLauncherAgent.h"
#endif

#ifdef TARGET_BUILD
#if (MSTAR_IPC == 1)
#include "apm.h"
#endif //end of MSTAR_IPC
#endif //end of TARGET_BUILD
// EosTek Patch Begin
#define SleepThreshold 60 //1 min
//#define SleepThreshold 300 //5 min
// EosTek Patch End
#define TIMER_REMINDER_CONFLICT_OFFSET 26
#define TIMEZONE_8Point45 ((SECONDS_PER_HOUR*8)+(SECONDS_PER_QUARTER * 3))
#define TIMEZONE_5Point45 ((SECONDS_PER_HOUR*5)+(SECONDS_PER_QUARTER * 3))
#define TIMEZONE_12Point45 ((SECONDS_PER_HOUR*12)+(SECONDS_PER_QUARTER * 3))
BEGIN_EVENT_MAP(MSrv_Timer, MSrv)
#if (MSTAR_TVOS == 0)
ON_EVENT(EV_NETWORK_CONTROL, &MSrv_Timer::OnNetworkControlEvent)
#endif
END_EVENT_MAP();

const static U8  SleepTimeCoef[11] = {0, 1, 2, 3, 6, 9, 12, 18, 24,36,48};
const static U8  SolarCal[12] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
const static U16 SolarDays[28] = { 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365, 396, 0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335, 366, 397};

// Add start/end paddings to timer record info.
static void _AddPaddingsToEpgEventTimerInfo(ST_EPG_EVENT_TIMER_INFO& stEpgTimerInfo);
// Remove start/end paddings from timer record info.
static void _RemovePaddingsFromEpgEventTimerInfo(ST_EPG_EVENT_TIMER_INFO& stEpgTimerInfo);

BOOL _IsRepeatDays(U8 u8Repeat)
{
    if(((u8Repeat & EPG_REPEAT_NONE_DAYS_FLAG) == 0) && ((u8Repeat & EPG_REPEAT_DAILY) != 0))
        return TRUE;
    else
        return FALSE;
}

MSrv_Timer* MSrv_Timer::m_instance = NULL;

MSrv_Timer::MSrv_Timer()
{
    MTIMER_IFO("%s::Constructor...\n", __func__);
#if (STR_ENABLE == 1)
    /* initialize thread parameters and binding calling functions */
    int intPTHChk;

    pthread_condattr_t attr;
    pthread_condattr_init(&attr);
    intPTHChk = PTH_RET_CHK(pthread_mutex_init(&m_mutex_offtimer, NULL));
    ASSERT(intPTHChk == 0);
    ASSERT((pthread_cond_init(&m_cond_offtimer, &attr)) == 0);


    PTH_RET_CHK(pthread_mutex_init(&m_mutex_poll, NULL));
    ASSERT(intPTHChk == 0);
    PTH_RET_CHK(pthread_cond_init(&m_cond_poll, NULL));
    ASSERT(intPTHChk == 0);

    PTH_RET_CHK(pthread_mutexattr_init(&m_EpgTimerMutexAttr));
    PTH_RET_CHK(pthread_mutexattr_settype(&m_EpgTimerMutexAttr, PTHREAD_MUTEX_RECURSIVE));
    PTH_RET_CHK(pthread_mutex_init(&m_EpgTimerMutex, &m_EpgTimerMutexAttr));

    PTH_RET_CHK(pthread_mutexattr_init(&m_CRIDTimerMutexAttr));
    PTH_RET_CHK(pthread_mutexattr_settype(&m_CRIDTimerMutexAttr, PTHREAD_MUTEX_RECURSIVE));
    PTH_RET_CHK(pthread_mutex_init(&m_CRIDTimerMutex, &m_CRIDTimerMutexAttr));
#endif
#if (MSTAR_TVOS == 0)
    MSrv_Network_Control::GetInstance()->AddEventRecipient(this);
#endif
    Init();

}

#if 0
void DumpTimerList(pthread_mutex_t& m_EpgTimerMutex)
{
    ST_EPG_EVENT_TIMER_INFO *pEventList = NULL;
    U32 listSize = 0;
    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);
    {
        mapi_scope_lock(scopeLock, &m_EpgTimerMutex);
        printf("================  DumpTimerList  ============\n");
        pSysDB->UseEpgTimerList(&pEventList, &listSize);
        for(int LoopIdx = 0; LoopIdx < EPG_TIMER_MAX_NUM ; LoopIdx++)
        {
            if((pEventList[LoopIdx].enTimerType == EPG_EVENT_NONE)
                    || (pEventList[LoopIdx].enTimerType >= EPG_EVENT_MAX))
            {
                break;
            }
            printf("%d\n",pEventList[LoopIdx].u16EventID);
        }
    }
}
#endif

#if (STR_ENABLE == 1)
void MSrv_Timer::Suspend(void)
{
    m_bCountdown = FALSE;
    m_bPoll = FALSE;
    if (m_bOfftimer == TRUE)
    {
        m_bOfftimer = FALSE;
        PostOfftimerCondSignal();
        if (m_t_offtimer != 0)
        {
            PTH_RET_CHK(pthread_join(m_t_offtimer, NULL));
        }
    }
}

void MSrv_Timer::Resume(void)
{
    Init();
    InitThreads();
}
#endif

BOOL MSrv_Timer::Init()
{
    /* local variables for recording timer infor */
    memset(&m_stClkTime, 0, sizeof(ST_time));
    memset(&m_stOnTime, 0, sizeof(ST_time));
    memset(&m_stOffTime, 0, sizeof(ST_time));
    memset(&m_stOnTimeDes, 0, sizeof(ST_OnTime_TVDes));

    //m_bRTCInitFlag = FALSE;
    m_bOnTimeFlag = FALSE;
    m_bOffTimeFlag = FALSE;
    m_bAutoSleepFlag = FALSE;
    m_bAutoSyncFlag = FALSE;
    m_bClockAlarmFlag = FALSE;
    m_bSignal = FALSE;
    m_bIs12Hour = FALSE;
    m_bResetTimer = TRUE;
    m_u32SleepTimebase = 0;
    m_u32SleepTimeDur = 0;
    m_u32AutoSleepTimebase = 0;
    m_u32AutoSleepTimeDur = 0;
    m_u32NextOnTime = SEC_THREADING_PENDING;
    m_u32SleepRemain = SEC_THREADING_PENDING;
    m_u32AutoSleepRemain = SEC_THREADING_PENDING;
    m_u32OffTimesecs = SEC_THREADING_PENDING;
    m_u32SleepTimesecs = SEC_THREADING_PENDING;
    m_u32AutoSleepTimesecs = SEC_THREADING_PENDING;
    m_u32OffRemain = SEC_THREADING_PENDING;
    m_enSleepState = STATE_SLEEP_OFF;
    m_enOffTimerState = EN_Timer_Off;
    m_enOnTimerState = EN_Timer_Off;
    m_enTimeZone = TIMEZONE_GMT_0_START;
    m_u32PoweroffTime = SEC_THREADING_PENDING;
    m_OffMode = OFF_MODE_INVALID_ID;
    m_u32EPGTimerActionTime = INVALID_EPG_TIME;
    m_u32EPGTimerRecordDuration = INVALID_EPG_TIME;
    m_EPGTimerActionMode = EN_EPGTIMER_ACT_NONE;
    m_EPGTimerRecordStatus = EN_EPGTIMER_RECORDER_IDLE;
    m_EPGTimerRecordLeadingTime = 0;
    m_TimerBootMode = EN_TIMER_BOOT_NONE;
    m_u32OADTimeRemain = SEC_THREADING_PENDING;
    m_u32OADTimesecs = SEC_THREADING_PENDING;
    m_bOADTimeFlag = FALSE;
#if (STR_ENABLE == 1)
    m_bTVPowerOffFlag = FALSE;
    m_u32TVPowerOffTimeSecs = SEC_THREADING_PENDING;
    m_u32TVPowerOffTimeRemain = SEC_THREADING_PENDING;
    m_bIsTVReallyPowerDown = FALSE;
#endif
    m_bIsRiksTVSleepTimerTrigger = FALSE;
    m_bIsRiksTVSleepTimerCountDownMessageShow = FALSE;
    m_bIsRiksTVSleepTimerMonitorThreadCreate = FALSE;
    memset(&m_RecordingProgram,0,sizeof(ST_EPG_EVENT_TIMER_INFO));
    memset(&m_NextB2BRecordingProgram,0,sizeof(m_NextB2BRecordingProgram));
    m_bIsRunawayRecording = FALSE;
    MS_TIME stTimeDb;
    (MSrv_Control::GetMSrvSystemDatabase())->GetOsdTime(&stTimeDb);

    m_enLinuxTimeSource = stTimeDb.enLinuxTimeSource;
#if (MSTAR_TVOS == 0)
    m_Ntp_Entity_Obj = new(std::nothrow)Ntp_Entity;
    ASSERT(m_Ntp_Entity_Obj);
#endif
    if(MSrv_Control::GetInstance()->QueryPowerOnMode() == EN_POWER_DC_BOOT)
    {
        if(m_enLinuxTimeSource == E_LINUX_TIME_FROM_NTP)
        {
            time_t timep;
            timep = stTimeDb.u32NtpTime + (mapi_interface::Get_mapi_system()->RTCGetCLK()-stTimeDb.u32OldRtc);
            stime(&timep);
        }
    }

    if(m_enLinuxTimeSource == E_LINUX_TIME_FROM_DTV)
    {
        time_t timep;
        timep = mapi_interface::Get_mapi_system()->RTCGetCLK();
        stime(&timep);
    }


#if (ATSC_SYSTEM_ENABLE == 1)
    m_bIsDaylightsaving = stTimeDb.bIsDaylightsaving; //the restore function is commited too late, so I have to load it before all
    m_u32StreamTime = 0;
#else
    m_bIsDaylightsaving = FALSE;
#endif
    m_u32OffTimebase = SEC_THREADING_PENDING;

    m_s32OffsetTime = GetTimeZoneOffsetTime(GetTimeZone2SpeciedTimeOffset(m_enTimeZone));

    mapi_interface::Get_mapi_system()->SetClockOffset(m_s32OffsetTime);
    MTIMER_DBG("%s:s32OffsetTime = %d\n", __func__, m_s32OffsetTime);

    if(MSrv_Control::IsSupportTheDTVSystemType(ATSC_ENABLE))
    {
        MS_TIME stTimeDb;
        MSrv_Control::GetMSrvSystemDatabase()->GetOsdTime(&stTimeDb);
#if (ATSC_SYSTEM_ENABLE == 1)
        m_bAutoSyncFlag = stTimeDb.bIsAutoSync;
#endif
        SetAutoSyncState(m_bAutoSyncFlag);
    }

    /* initialize thread parameters and binding calling functions */
#if (STR_ENABLE == 0)
    int intPTHChk;

    pthread_condattr_t attr;
    pthread_condattr_init(&attr);
    intPTHChk = PTH_RET_CHK(pthread_mutex_init(&m_mutex_offtimer, NULL));
    ASSERT(intPTHChk == 0);
    ASSERT((pthread_cond_init(&m_cond_offtimer, &attr)) == 0);


    PTH_RET_CHK(pthread_mutex_init(&m_mutex_poll, NULL));
    ASSERT(intPTHChk == 0);
    PTH_RET_CHK(pthread_cond_init(&m_cond_poll, NULL));
    ASSERT(intPTHChk == 0);

    PTH_RET_CHK(pthread_mutexattr_init(&m_EpgTimerMutexAttr));
    PTH_RET_CHK(pthread_mutexattr_settype(&m_EpgTimerMutexAttr, PTHREAD_MUTEX_RECURSIVE));
    PTH_RET_CHK(pthread_mutex_init(&m_EpgTimerMutex, &m_EpgTimerMutexAttr));

    PTH_RET_CHK(pthread_mutexattr_init(&m_CRIDTimerMutexAttr));
    PTH_RET_CHK(pthread_mutexattr_settype(&m_CRIDTimerMutexAttr, PTHREAD_MUTEX_RECURSIVE));
    PTH_RET_CHK(pthread_mutex_init(&m_CRIDTimerMutex, &m_CRIDTimerMutexAttr));
#endif
    m_u32CRIDCheckTriggerCount = 0;

    /* Restore TimerDB */
    RestoreTimerDB();
    return TRUE;
}

BOOL MSrv_Timer::InitThreads()
{
    int intPTHChk;

    /* Create power-off timer monitor */
    pthread_attr_t thr_attr;
    pthread_attr_init(&thr_attr);
    pthread_attr_setstacksize(&thr_attr, PTHREAD_STACK_SIZE);

    m_bOfftimer = TRUE;
    intPTHChk = PTH_RET_CHK(pthread_create(&m_t_offtimer, &thr_attr, MonitorOffModeTimer, this));
    ASSERT(intPTHChk == 0);

#if (STR_ENABLE == 1)
    pthread_attr_setdetachstate(&thr_attr, PTHREAD_CREATE_DETACHED);
#endif

    /* Create thread for updating timer page*/
    m_bPoll = TRUE;
    intPTHChk = PTH_RET_CHK(pthread_create(&m_t_poll, &thr_attr, PollingCurrTime , this));
    ASSERT(intPTHChk == 0);

    /* Thread for polling countdown timer */
    m_bCountdown = TRUE;
    intPTHChk = PTH_RET_CHK(pthread_create(&m_t_countdown, &thr_attr, CountDownTime, this));
    ASSERT(intPTHChk == 0);

#if (STR_ENABLE == 1)
    pthread_attr_destroy(&thr_attr);
#endif
    return TRUE;
}

BOOL MSrv_Timer::Finalize()
{
    PTH_RET_CHK(pthread_mutexattr_destroy(&m_EpgTimerMutexAttr));
    PTH_RET_CHK(pthread_mutex_destroy(&m_EpgTimerMutex));

    PTH_RET_CHK(pthread_mutexattr_destroy(&m_CRIDTimerMutexAttr));
    PTH_RET_CHK(pthread_mutex_destroy(&m_CRIDTimerMutex));
#if (MSTAR_TVOS == 0)
    DELETE(m_Ntp_Entity_Obj);
#endif
    return TRUE;

}
MSrv_Timer::~MSrv_Timer()
{
    MTIMER_IFO("%s::Destructor...\n", __func__);

#if (MSTAR_TVOS == 0)
    MSrv_Network_Control::GetInstance()->RemoveEventRecipient(this);
#endif
    ASSERT((pthread_mutex_destroy(&m_mutex_poll)) == 0);
    ASSERT((pthread_cond_destroy(&m_cond_poll)) == 0);
    ASSERT((pthread_mutex_destroy(&m_mutex_offtimer)) == 0);
    ASSERT((pthread_cond_destroy(&m_cond_offtimer) == 0));
}

void MSrv_Timer::StoreTimerDB()
{
    MTIMER_DBG(">>>>>>>%s:Backup Timer DB.....<<<<<<<\n" , __func__);
    /* For storing timer relative informations */
    MSrv_System_Database *dbptr = MSrv_Control::GetMSrvSystemDatabase();
    MS_TIME stTimeDb;

    dbptr->GetOsdTime(&stTimeDb);

    stTimeDb.bOffTimeFlag = m_bOffTimeFlag;
    stTimeDb.bOnTimeFlag = m_bOnTimeFlag;
    stTimeDb.enOffTimeState = m_enOffTimerState;
    stTimeDb.enOnTimeState = m_enOnTimerState;
    stTimeDb.u8OffTimer_Info_Hour = m_stOffTime.u8Hour;
    stTimeDb.u8OffTimer_Info_Min = m_stOffTime.u8Minute;
    stTimeDb.u8OnTimer_Info_Hour = m_stOnTime.u8Hour;
    stTimeDb.u8OnTimer_Info_Min = m_stOnTime.u8Minute;
    stTimeDb.cOnTimeTVSrc = (U8)(m_stOnTimeDes.enTVSrc);
#if (ISDB_SYSTEM_ENABLE == 1)
    stTimeDb.cOnTimeAntennaType = (U8)(m_stOnTimeDes.enAntennaType);
#endif
    stTimeDb.cOnTimerChannel = m_stOnTimeDes.u16ChNo;
    stTimeDb.cOnTimerVolume = m_stOnTimeDes.u8Vol;
    stTimeDb.eTimeZoneInfo = (U8)m_enTimeZone;
    stTimeDb.bIs12Hour = m_bIs12Hour;
    stTimeDb.bClockMode = m_bAutoSyncFlag;
    stTimeDb.bAutoSleepFlag = m_bAutoSleepFlag;
    stTimeDb.enTimerBootMode = m_TimerBootMode;
#if (ATSC_SYSTEM_ENABLE == 1)
    stTimeDb.bIsAutoSync = m_bAutoSyncFlag;
#endif
    stTimeDb.bIsDaylightsaving = m_bIsDaylightsaving;
    stTimeDb.s32OffsetTime=m_s32OffsetTime;
    stTimeDb.enLinuxTimeSource = m_enLinuxTimeSource;
    stTimeDb.u32NtpTime = time(NULL);
    stTimeDb.u32OldRtc = mapi_interface::Get_mapi_system()->RTCGetCLK();
    dbptr->SetOsdTime(&stTimeDb);

}

void MSrv_Timer::RestoreTimerDB()
{
    MTIMER_IFO(">>>>>>>%s:Restore Timer DB Start.....<<<<<<<\n", __func__);
    MSrv_System_Database *dbptr = MSrv_Control::GetMSrvSystemDatabase();
    MS_TIME stTimeDb;
    ST_time _stOffTime, _stOntime, _stCurr;
    MS_USER_SOUND_SETTING stAudioSetting;
    MS_USER_SYSTEM_SETTING  stGetSystemSetting;

    memset(&stTimeDb, 0 , sizeof(MS_TIME));
    memset(&_stOffTime, 0, sizeof(ST_time));
    memset(&_stOntime, 0, sizeof(ST_time));
    memset(&_stCurr, 0, sizeof(ST_time));

    MTIMER_IFO("Before dbptr->GetSystemDatabase\n");
    (MSrv_Control::GetMSrvSystemDatabase())->GetOsdTime(&stTimeDb);
    dbptr->GetOsdTime(&stTimeDb);
    MTIMER_IFO("After dbptr->GetSystemDatabase\n");
    DumpDB(stTimeDb);

    /* Initial system clock time */
    m_enTimeZone = (EN_TIMEZONE)stTimeDb.eTimeZoneInfo;
    SetTimeZone(m_enTimeZone, FALSE);

    m_bOffTimeFlag = stTimeDb.bOffTimeFlag;
    m_bOnTimeFlag = stTimeDb.bOnTimeFlag;
    m_enOffTimerState = (EN_Timer_Period)stTimeDb.enOffTimeState;

    _stOffTime.u8Hour = stTimeDb.u8OffTimer_Info_Hour;
    _stOffTime.u8Minute = stTimeDb.u8OffTimer_Info_Min;
    SetOffModeStatus(_stOffTime, m_enOffTimerState, FALSE);

    m_enOnTimerState = (EN_Timer_Period)stTimeDb.enOnTimeState;
    _stOntime.u8Hour = stTimeDb.u8OnTimer_Info_Hour;
    _stOntime.u8Minute = stTimeDb.u8OnTimer_Info_Min;
    m_stOnTimeDes.enTVSrc = (EN_TIME_OnTimer_Source)stTimeDb.cOnTimeTVSrc;
#if (ISDB_SYSTEM_ENABLE == 1)
    m_stOnTimeDes.enAntennaType=(EN_TIME_OnTimer_AntennaType)stTimeDb.cOnTimeAntennaType;
#endif
    m_stOnTimeDes.u16ChNo = stTimeDb.cOnTimerChannel;
    m_stOnTimeDes.u8Vol = stTimeDb.cOnTimerVolume;
    SetOnTime(_stOntime, m_enOnTimerState, m_stOnTimeDes, FALSE);

    m_bIs12Hour = stTimeDb.bIs12Hour;
#if (ATSC_SYSTEM_ENABLE == 1)
    m_bAutoSyncFlag = stTimeDb.bIsAutoSync;
#else
    m_bAutoSyncFlag = stTimeDb.bClockMode;
#endif
    m_bAutoSleepFlag = stTimeDb.bAutoSleepFlag;
    m_TimerBootMode = stTimeDb.enTimerBootMode;
    m_bIsDaylightsaving = stTimeDb.bIsDaylightsaving;
    m_enLinuxTimeSource = stTimeDb.enLinuxTimeSource;

    //EPG timer initial from boot.
    //if(MSrv_Control::GetInstance()->IsWakeUpByRTC())
            ReconfigTimerFromList();

    struct tm timeinfo;
    memset(&timeinfo, 0, sizeof(struct tm));
    GetCLKTime(timeinfo);
    Convertlocaltime2StTime(&timeinfo, &_stCurr);
#if (ISDB_SYSTEM_ENABLE == 1)
    ST_MEDIUM_SETTING MediumSetting;
#endif
    /* Set Ontime TV information */
    MTIMER_DBG("%s:IfTodayInTimerTerm = %s , TimeDiff = %d\n", __func__, IfTodayInTimerTerm(m_enOnTimerState) ? "True" : "False", abs((int)(ConvertStTime2Seconds(&m_stOnTime) - ConvertStTime2Seconds(&_stCurr))));
    if(IfTodayInTimerTerm(m_enOnTimerState))
    {
        if(abs((int)(ConvertStTime2Seconds(&m_stOnTime) - ConvertStTime2Seconds(&_stCurr))) < (2 * SECONDS_PER_MIN))
        {
            MSrv_Control::GetMSrvSystemDatabase()->GetUserSystemSetting(&stGetSystemSetting);
            switch(m_stOnTimeDes.enTVSrc)
            {
                case EN_Time_OnTimer_Source_DTV:
                case EN_Time_OnTimer_Source_RADIO:
                case EN_Time_OnTimer_Source_DATA:
               #if (ISDB_SYSTEM_ENABLE == 1)
                    MediumSetting.AntennaType = E_ANTENNA_TYPE_AIR;
                    MSrv_Control::GetMSrvSystemDatabase()->SetMediumSetting(&MediumSetting);
#if (STB_ENABLE == 0)
                    MSrv_Control::GetMSrvAtv()->SetNTSCAntenna(MSrv_ATV_Database::MEDIUM_AIR);
#endif
                    if((m_stOnTimeDes.u16ChNo&0X0000ff00)>>8)//Air DTV CH
                    {
                        stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_DTV;
                    }
                    else
                    {
                        stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_ATV;
                    }
              #else
                    stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_DTV;
              #endif
                    break;
                case EN_Time_OnTimer_Source_ATV:
#if (ISDB_SYSTEM_ENABLE == 1)
                    MediumSetting.AntennaType = E_ANTENNA_TYPE_CABLE;
                    MSrv_Control::GetMSrvSystemDatabase()->SetMediumSetting(&MediumSetting);
#if (STB_ENABLE == 0)
                    MSrv_Control::GetMSrvAtv()->SetNTSCAntenna(MSrv_ATV_Database::MEDIUM_CABLE);
#endif
#endif
                    stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_ATV;
                    break;
                case EN_Time_OnTimer_Source_SCART:
                    stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_SCART;
                    break;
                case EN_Time_OnTimer_Source_SCART2:
                    stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_SCART2;
                    break;
                case EN_Time_OnTimer_Source_COMPONENT:
                    stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_YPBPR;
                    break;
                case EN_Time_OnTimer_Source_COMPONENT2:
                    stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_YPBPR2;
                    break;
                case EN_Time_OnTimer_Source_RGB:
                    stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_VGA;
                    break;
                case EN_Time_OnTimer_Source_RGB2:
                    stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_VGA2;
                    break;
                case EN_Time_OnTimer_Source_RGB3:
                    stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_VGA3;
                    break;
                case EN_Time_OnTimer_Source_HDMI:
                    stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_HDMI;
                    break;
                case EN_Time_OnTimer_Source_HDMI2:
                    stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_HDMI2;
                    break;
                case EN_Time_OnTimer_Source_HDMI3:
                    stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_HDMI3;
                    break;
                case EN_Time_OnTimer_Source_HDMI4:
                    stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_HDMI4;
                    break;
                case EN_Time_OnTimer_Source_AV:
                    stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_CVBS;
                    break;
                case EN_Time_OnTimer_Source_AV2:
                    stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_CVBS2;
                    break;
                case EN_Time_OnTimer_Source_SVIDEO:
                    stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_SVIDEO;
                    break;
                case EN_Time_OnTimer_Source_SVIDEO2:
                    stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_SVIDEO2;
                    break;
                default:
                    break;
            }

            MSrv_Control::GetMSrvSystemDatabase()->SetUserSystemSetting(&stGetSystemSetting);
            MSrv_Control::GetMSrvSystemDatabase()->GetAudioSetting(&stAudioSetting);
            stAudioSetting.Volume = m_stOnTimeDes.u8Vol;
            MSrv_Control::GetMSrvSystemDatabase()->SetAudioSetting(&stAudioSetting);
        }
    }

    MTIMER_IFO(">>>>>>>%s:Restore Timer DB End.....<<<<<<<<<\n", __func__);
}

void MSrv_Timer::DumpDB(MS_TIME &stTimeDb)
{
    MTIMER_IFO("**************************DumpDB*********************************************************\n");
    MTIMER_IFO("bOffTimeFlag = %d , bOnTimeFlag = %d, enOffTimeState = %d, enOffTimeState = %d\n", stTimeDb.bOffTimeFlag, stTimeDb.bOnTimeFlag, stTimeDb.enOffTimeState, stTimeDb.enOnTimeState);
    MTIMER_IFO("u8OffTimer_Info_Hour = %d ,u8OffTimer_Info_Min = %d ,u8OnTimer_Info_Hour = %d, u8OnTimer_Info_Min= %d\n", stTimeDb.u8OffTimer_Info_Hour, stTimeDb.u8OffTimer_Info_Min, stTimeDb.u8OnTimer_Info_Hour, stTimeDb.u8OnTimer_Info_Min);
    MTIMER_IFO("cOnTimeTVSrc = %d, cOnTimerChannel = %d, cOnTimerVolume = %d, eTimeZoneInfo= %d\n", stTimeDb.cOnTimeTVSrc, stTimeDb.cOnTimerChannel, stTimeDb.cOnTimerVolume, stTimeDb.eTimeZoneInfo);
    MTIMER_IFO("*****************************************************************************************\n");
}

void MSrv_Timer::GetCLKTime(struct tm &timeinfo)
{
    U32 u32Second;
    ST_time stTime;
    u32Second = mapi_interface::Get_mapi_system()->RTCGetCLK();
    u32Second += mapi_interface::Get_mapi_system()->GetClockOffset();

    ConvertSeconds2StTime(u32Second, &stTime);
    ConvertStTime2Localtime(&stTime, &timeinfo);
}

void MSrv_Timer::SetCLKTime(const ST_time &stTime, BOOL bflag)
{
    MAPI_S32 s32OffsetTime;
    s32OffsetTime = mapi_interface::Get_mapi_system()->GetClockOffset();

    if(m_enLinuxTimeSource == E_LINUX_TIME_FROM_DTV)
    {
        struct timeval tv;
        struct timezone tz;
        memset(&tv, 0 , sizeof(struct timeval));
        memset(&tz, 0 , sizeof(struct timezone));

        tv.tv_sec = (time_t)ConvertStTime2Seconds(&stTime);
        tv.tv_usec = 0;
        tz.tz_minuteswest = 0 - (s32OffsetTime / SECONDS_PER_MIN);
        tz.tz_dsttime = 0;  // to be implentment iff DST consideration

        int ret;
        ret = settimeofday(&tv, &tz);
        if(ret != 0)
        {
            printf("settimeofday fail");
        }

        #if (MSTAR_TVOS == 1)
        usleep(1000);
        #endif
    }
    U32 RTCsecs = ConvertStTime2Seconds(&stTime) - s32OffsetTime;
    MTIMER_IFO("%s:timezoneSec = %u,RTCsecs = %u\n", __func__, ConvertStTime2Seconds(&stTime), RTCsecs);
    RTCSetCLK(ConvertStTime2Seconds(&stTime) - s32OffsetTime);

    if(bflag)
    {
        StoreTimerDB();
    }

    if(m_bOffTimeFlag)
    {
        m_u32OffTimesecs = GetNextTimer(m_stOffTime, m_enOffTimerState);
        m_u32OffRemain = GetNextTimer(m_stOffTime, m_enOffTimerState) - (mapi_interface::Get_mapi_system()->RTCGetCLK() + mapi_interface::Get_mapi_system()->GetClockOffset());

        if(m_u32OffTimesecs == 0)
        {
            m_u32OffTimesecs = SEC_THREADING_PENDING;
        }
        /*Reset counting down window flag*/
        m_bClockAlarmFlag = FALSE;
        MTIMER_DBG(">>>>>>m_u32OffTimesecs = %u, m_u32OffRemain = %u\n", m_u32OffTimesecs, m_u32OffRemain);
    }
    int intPTHChk;
    intPTHChk = PTH_RET_CHK(pthread_cond_signal(&m_cond_offtimer));
    ASSERT(intPTHChk == 0);

    //reset on time
    SetOnTime(m_stOnTime, m_enOnTimerState, m_stOnTimeDes, TRUE);

    //reset EPG timer
    ReconfigTimerFromList();

}

void MSrv_Timer::GetOffModeStatus(ST_time &stTime,  EN_Timer_Period &enTimeOffTimer)
{
    enTimeOffTimer = m_enOffTimerState;
    stTime = m_stOffTime;
}

void MSrv_Timer::SetOffModeStatus(const ST_time &stTime, EN_Timer_Period enTimeOffTimer, BOOL bflag)
{
    struct tm timeinfo;
    GetCLKTime(timeinfo);
    Convertlocaltime2StTime(&timeinfo, &m_stOffTime);

    m_enOffTimerState = enTimeOffTimer;
    m_stOffTime.u8Hour = stTime.u8Hour;
    m_stOffTime.u8Minute = stTime.u8Minute;
    m_stOffTime.u8Second = 0;


    //MTIMER_IFO("m_enOffTimerState = %d, m_stOffTime.u8Hour = %d, m_stOffTime.u8Minute = %d\n", m_enOffTimerState, m_stOffTime.u8Hour, m_stOffTime.u8Minute);

    m_u32OffTimesecs = GetNextTimer(m_stOffTime, m_enOffTimerState);
    printf("mapi_interface::Get_mapi_system()->RTCGetCLK() = %d\n",mapi_interface::Get_mapi_system()->RTCGetCLK());
    printf("mapi_interface::Get_mapi_system()->GetClockOffset() = %d\n", mapi_interface::Get_mapi_system()->GetClockOffset());

    m_u32OffRemain = GetNextTimer(m_stOffTime, m_enOffTimerState) - (mapi_interface::Get_mapi_system()->RTCGetCLK() + mapi_interface::Get_mapi_system()->GetClockOffset());

    if(m_u32OffTimesecs == 0)
    {
        m_u32OffTimesecs = SEC_THREADING_PENDING;
        m_u32OffRemain = SEC_THREADING_PENDING;
    }

    MTIMER_DBG(">>>>>>, m_u32OffTimesecs = %u, m_u32OffRemain = %u\n", m_u32OffTimesecs, m_u32OffRemain);

    if(m_enOffTimerState != EN_Timer_Off)
    {
        m_bOffTimeFlag = TRUE;
    }
    else
    {
        m_bOffTimeFlag = FALSE;
        m_u32OffTimesecs = SEC_THREADING_PENDING;
        m_u32OffRemain = SEC_THREADING_PENDING;
    }

    /*Reset counting down window flag*/
    m_bClockAlarmFlag = FALSE;

    if(bflag)
    {
        StoreTimerDB();
    }

    /* signal monitor thread to check iff time is out */
    int intPTHChk;
    intPTHChk = PTH_RET_CHK(pthread_cond_signal(&m_cond_offtimer));
    ASSERT(intPTHChk == 0);
}

void MSrv_Timer::SetSleepModeTime(EN_SLEEP_TIME_STATE enState)
{
    m_enSleepState = enState;

    if(enState != STATE_SLEEP_OFF)
    {
        m_bSleepFlag = TRUE;
        m_u32SleepTimebase = mapi_time_utility::GetTime0() / SECOND_TO_MS;
        //m_u32SleepTimeDur =60;
        m_u32SleepTimeDur = (SleepTimeCoef[m_enSleepState] * SLEEP_TIMER_TIMEBASE) / SECOND_TO_MS;
        m_u32SleepTimesecs = m_u32SleepTimebase + m_u32SleepTimeDur;
        m_u32SleepRemain = m_u32SleepTimesecs - m_u32SleepTimebase;

    }
    else
    {
        m_bSleepFlag = FALSE;
        m_u32SleepTimebase = 0;
        m_u32SleepTimeDur = 0;
        m_u32SleepTimesecs = SEC_THREADING_PENDING;
        m_u32SleepRemain = SEC_THREADING_PENDING;
    }

    MTIMER_IFO("m_u32SleepTimebase =%u (secs), m_u32SleepTimeDur = %u (secs) , m_u32SleepRemain = %u\n", m_u32SleepTimebase, m_u32SleepTimeDur, m_u32SleepRemain);

    /*Reset counting down window flag*/
    m_bClockAlarmFlag = FALSE;

    int intPTHChk;
    intPTHChk = PTH_RET_CHK(pthread_cond_signal(&m_cond_offtimer));
    ASSERT(intPTHChk == 0);
}

void MSrv_Timer::SetSleepModeTime(MAPI_U8 u8Minutes)
{
    if(u8Minutes)
    {
        m_bSleepFlag = TRUE;
        m_u32SleepTimebase = mapi_time_utility::GetTime0() / SECOND_TO_MS;
        //m_u32SleepTimeDur =60;
        m_u32SleepTimeDur = (u8Minutes * MINUTE_TO_MS) / SECOND_TO_MS;
        m_u32SleepTimesecs = m_u32SleepTimebase + m_u32SleepTimeDur;
        m_u32SleepRemain = m_u32SleepTimesecs - m_u32SleepTimebase;

    }
    else
    {
        m_bSleepFlag = FALSE;
        m_u32SleepTimebase = 0;
        m_u32SleepTimeDur = 0;
        m_u32SleepTimesecs = SEC_THREADING_PENDING;
        m_u32SleepRemain = SEC_THREADING_PENDING;
    }

    MTIMER_IFO("m_u32SleepTimebase =%u (secs), m_u32SleepTimeDur = %u (secs) , m_u32SleepRemain = %u\n", m_u32SleepTimebase, m_u32SleepTimeDur, m_u32SleepRemain);

    /*Reset counting down window flag*/
    m_bClockAlarmFlag = FALSE;

    int intPTHChk;
    intPTHChk = PTH_RET_CHK(pthread_cond_signal(&m_cond_offtimer));
    ASSERT(intPTHChk == 0);
}

BOOL MSrv_Timer::GetAutoSleepState()
{
    return m_bAutoSleepFlag;
}

void MSrv_Timer::SetAutoSleepState(BOOL bflag)
{
    m_bAutoSleepFlag = bflag;
}
#if (OAD_ENABLE == 1)
void MSrv_Timer::SetOADScanTime(U32 u32Secs)
{
    U32 timebase;
    // 0xFFFFFFFF mean close the OAD SCAN Timer
    if(u32Secs == 0xFFFFFFFF)
    {
        m_bOADTimeFlag = FALSE;
        m_u32OADTimesecs = SEC_THREADING_PENDING;
        m_u32OADTimeRemain = SEC_THREADING_PENDING;
    }
    else
    {
        m_bOADTimeFlag = TRUE;
        timebase = mapi_time_utility::GetTime0() / SECOND_TO_MS;
        m_u32OADTimesecs = timebase + u32Secs;
        m_u32OADTimeRemain = m_u32OADTimesecs - timebase;
        {
            MSrv_Timer* pTimer;
            struct tm timeinfo;
            MSrv_System_Database *dbptr = MSrv_Control::GetMSrvSystemDatabase();
            MW_OAD_WAKEUP_INFORMATION stOadWakeUpInfo;

            pTimer = MSrv_Control::GetMSrvTimer();
            pTimer->GetCLKTime(timeinfo);
            {
                stOadWakeUpInfo.u8ScheduleOn = 1;
                stOadWakeUpInfo.u32WakeUpTime = mapi_interface::Get_mapi_system()->RTCGetCLK();
                //stOadWakeUpInfo.u32WakeUpTime += mapi_interface::Get_mapi_system()->GetClockOffset();
                stOadWakeUpInfo.u32WakeUpTime += u32Secs;
            }
            dbptr->SetOADWakeUpInfo((U8*)&stOadWakeUpInfo);
        }
    }

    int intPTHChk;
    intPTHChk = PTH_RET_CHK(pthread_cond_signal(&m_cond_offtimer));
    ASSERT(intPTHChk == 0);
}
#endif
void MSrv_Timer::GetOnTime(ST_time &stTime, EN_Timer_Period &enTimeOnTimer, ST_OnTime_TVDes &stTvDes, EN_TIMER_BOOT_TYPE &enBootMode)
{
    enTimeOnTimer = m_enOnTimerState;
    stTime = m_stOnTime;
    stTvDes = m_stOnTimeDes;
    enBootMode = m_TimerBootMode;
}

void MSrv_Timer::SetOnTime(const ST_time &stTime, EN_Timer_Period enState, const ST_OnTime_TVDes &stTvDes, BOOL bflag, BOOL boot_setup , EN_EPG_TIMER_ACT_TYPE enEPGTimerActionMode)
{
    MTIMER_IFO("SetOnTime([time %2d %2d:%2d][Period:%d][tv: %d, %d, %d][save:%d][boot:%d])\n"
               , stTime.u8Day, stTime.u8Hour, stTime.u8Minute, enState, stTvDes.enTVSrc, stTvDes.u16ChNo, stTvDes.u8Vol, bflag, enEPGTimerActionMode);
    struct tm timeinfo;
    ST_time temp_ontime;
    GetCLKTime(timeinfo);
    Convertlocaltime2StTime(&timeinfo, &temp_ontime);

    MS_TIME stTimeDb;
    (MSrv_Control::GetMSrvSystemDatabase())->GetOsdTime(&stTimeDb);
    if(bflag && (enState != EN_Timer_Off) && (boot_setup == FALSE) && (E_DAYLIGHT_SAVING_AUTO == stTimeDb.enDaylightSavingMode))
    {
        if(mapi_interface::Get_mapi_system()->GetClockOffset() !=  GetTimeZoneOffsetTime(GetTimeZone2SpeciedTimeOffset(GetTimeZone())))
        {
            m_bIsDaylightsaving = TRUE;
        }
        else
        {
            m_bIsDaylightsaving = FALSE;
        }
    }

    if(enEPGTimerActionMode == EN_EPGTIMER_ACT_NONE)
    {
        //default for on time
        m_bOnTimeFlag = TRUE;
        m_enOnTimerState = enState;

        m_stOnTime.u16Year = temp_ontime.u16Year;
        m_stOnTime.u8Month = temp_ontime.u8Month;
        m_stOnTime.u8Day = temp_ontime.u8Day;
        m_stOnTime.u8Hour = stTime.u8Hour;
        m_stOnTime.u8Minute = stTime.u8Minute;
        m_stOnTime.u8Second = 0;
        m_stOnTimeDes = stTvDes;
        m_TimerBootMode = EN_TIMER_BOOT_ON_TIMER;

        if(bflag)
        {
            StoreTimerDB();
        }
    }
    MTIMER_IFO("m_enOnTimerState = %d, m_stOnTime.u8Hour = %d, m_stOnTime.u8Minute = %d , m_stOnTimeDes.enTVSrc = %d, m_stOnTimeDes.u16ChNo = %d, m_stOnTimeDes.u8Vol = %d\n", m_enOnTimerState, m_stOnTime.u8Hour, m_stOnTime.u8Minute, m_stOnTimeDes.enTVSrc, m_stOnTimeDes.u16ChNo, m_stOnTimeDes.u8Vol);

    m_u32NextOnTime = GetNextTimer(m_stOnTime, m_enOnTimerState);
    MAPI_S32 s32OffsetTime;
    s32OffsetTime = mapi_interface::Get_mapi_system()->GetClockOffset();
    if(m_u32NextOnTime != SEC_THREADING_PENDING)
    {
        m_u32NextOnTime -= s32OffsetTime;
    }
    U32 timeNow = mapi_interface::Get_mapi_system()->RTCGetCLK();
    if((m_u32EPGTimerActionTime != INVALID_EPG_TIME) && (m_u32EPGTimerActionTime > timeNow)
            && ((enEPGTimerActionMode == EN_EPGTIMER_ACT_CI_OP_REFRESH) || (enEPGTimerActionMode == EN_EPGTIMER_ACT_REMINDER) || (enEPGTimerActionMode == EN_EPGTIMER_ACT_RECORDER_START)))
    {
        // if record timer is early than power on time, then set power on state
        if(((m_u32NextOnTime > timeNow) && ((m_u32EPGTimerActionTime - (2*SECONDS_PER_MIN)) < m_u32NextOnTime))
                || (m_u32NextOnTime <= timeNow))
        {
            if(((m_u32EPGTimerActionTime - (2*SECONDS_PER_MIN)) > timeNow)&&(enEPGTimerActionMode != EN_EPGTIMER_ACT_REMINDER))
            {
                m_u32NextOnTime = (m_u32EPGTimerActionTime - (2*SECONDS_PER_MIN));
            }
            else if((m_u32EPGTimerActionTime > (timeNow+10)) && (enEPGTimerActionMode == EN_EPGTIMER_ACT_RECORDER_START))
            {
                m_u32NextOnTime = (timeNow+10);
            }
            else
            {
                m_u32NextOnTime = m_u32EPGTimerActionTime;
            }
            m_stOnTime.u8Hour = stTime.u8Hour;
            m_stOnTime.u8Minute = stTime.u8Minute;
            m_stOnTime.u8Second = 0;
            m_stOnTimeDes = stTvDes;
            switch(enEPGTimerActionMode)
            {
                case EN_EPGTIMER_ACT_REMINDER:
                    m_TimerBootMode = EN_TIMER_BOOT_REMINDER;
                    break;
                case EN_TIMER_BOOT_RECORDER:
                    m_TimerBootMode = EN_TIMER_BOOT_RECORDER;
                    break;
                case EN_EPGTIMER_ACT_CI_OP_REFRESH:
                    m_TimerBootMode = EN_TIMER_BOOT_CI_OP_REFRESH;
                    break;
                default:
                    ASSERT(0);
                    break;
            }
            StoreTimerDB();
        }
    }
    MTIMER_IFO("RTC sec = %u , Wakeuptime = %u\n", mapi_interface::Get_mapi_system()->RTCGetCLK(), m_u32NextOnTime);

    mapi_interface::Get_mapi_system()->RTCSetMatchTime(m_u32NextOnTime);
    mapi_interface::Get_mapi_system()->RTCEnableInterrupt(TRUE);
}

#if (OAD_ENABLE == 1)
void MSrv_Timer::GetOADWakeUpTime(MAPI_U32 &u32Seconds)
{
    MW_OAD_WAKEUP_INFORMATION stOadWakeUpInfo;
    MSrv_System_Database *dbptr = MSrv_Control::GetMSrvSystemDatabase();
    dbptr->GetOADWakeUpInfo((U8*)&stOadWakeUpInfo);
    u32Seconds = stOadWakeUpInfo.u32WakeUpTime;
}

void MSrv_Timer::SetOADWakeUpTime(MAPI_U32 u32Seconds)
{
    m_TimerBootMode = EN_TIMER_BOOT_OAD_DOWNLOAD;
    StoreTimerDB();
    mapi_interface::Get_mapi_system()->RTCSetMatchTime(u32Seconds);
    mapi_interface::Get_mapi_system()->RTCEnableInterrupt(TRUE);
}
#endif

BOOL MSrv_Timer::IfTodayInTimerTerm(EN_Timer_Period enState)
{
    ST_time st_cur;
    EN_DayofWeek eToday;
    struct tm timeinfo;

    GetCLKTime(timeinfo);
    Convertlocaltime2StTime(&timeinfo, &st_cur);

    eToday = (EN_DayofWeek)timeinfo.tm_wday;
    MTIMER_DBG("%s:timeinfo.tm_wday = %d\n", __func__, timeinfo.tm_wday);
    switch(enState)
    {
        case EN_Timer_Once:
        case EN_Timer_Everyday:
            return TRUE;
            break;
        case EN_Timer_Mon2Fri:
            if((eToday >= MON) && (eToday <= FRI))
            {
                return TRUE;
            }
            break;
        case EN_Timer_Mon2Sat:
            if((eToday >= MON) && (eToday <= SAT))
            {
                return TRUE;
            }
            break;
        case EN_Timer_Sat2Sun:
            if((eToday == SAT) || (eToday == SUN))
            {
                return TRUE;
            }
            break;
        case EN_Timer_Sun:
            if(eToday == SUN)
            {
                return TRUE;
            }
            break;
        default:
            return FALSE;
    }
    return FALSE;
}

void  MSrv_Timer::DisableOffMode(EN_OffTimerMode enOffMode)
{
    int intPTHChk;

    //MTIMER_DBG(">>>>>>%s:bPoweroffMode = %d\n", __func__, enOffMode);
    switch(enOffMode)
    {
        case OFF_MODE_TIMER:
            m_bOffTimeFlag = FALSE;
            m_u32OffTimesecs = SEC_THREADING_PENDING;
            m_u32OffRemain = SEC_THREADING_PENDING;
            m_u32OffTimebase = SEC_THREADING_PENDING;
            if(EN_Timer_Once == m_enOffTimerState)
            {
                m_enOffTimerState = EN_Timer_Off;
                StoreTimerDB();
                sleep(1);
            }
            intPTHChk = PTH_RET_CHK(pthread_cond_signal(&m_cond_offtimer));
            ASSERT(intPTHChk == 0);
            break;
        case OFF_MODE_SLEEP:
            m_bSleepFlag = FALSE;
            m_u32SleepTimebase = 0;
            m_u32SleepTimeDur = 0;
            m_u32SleepTimesecs = SEC_THREADING_PENDING;
            m_u32SleepRemain = SEC_THREADING_PENDING;
            intPTHChk = PTH_RET_CHK(pthread_cond_signal(&m_cond_offtimer));
            ASSERT(intPTHChk == 0);
            m_enSleepState = STATE_SLEEP_OFF;
            m_bIsRiksTVSleepTimerTrigger = FALSE;
            break;
        case OFF_MODE_AUTOSLEEP:
            if(m_bAutoSleepFlag == TRUE)
            {
                m_bResetTimer = TRUE;
                m_u32AutoSleepTimebase = 0;
                m_u32AutoSleepTimeDur = 0;
                m_u32AutoSleepTimesecs = SEC_THREADING_PENDING;
                m_u32AutoSleepRemain = SEC_THREADING_PENDING;
            }
            intPTHChk = PTH_RET_CHK(pthread_cond_signal(&m_cond_offtimer));
            ASSERT(intPTHChk == 0);
            break;
        default:
            MTIMER_ERR("Wrong OFF Timer mode!\n");
            break;
    }
}

void MSrv_Timer::PostOfftimerCondSignal(void)
{
    int intPTHChk;
    intPTHChk = PTH_RET_CHK(pthread_cond_signal(&m_cond_offtimer));
    ASSERT(intPTHChk == 0);
}

void *MSrv_Timer::MonitorOffModeTimer(void *arg)
{
    U8 name[32];
    memset(name, 0, 32);
    prctl(PR_SET_NAME, (unsigned long)"PowerDownOffTimer");
    prctl(PR_GET_NAME, (unsigned long)name);
#if (STB_ENABLE == 0)
    const MAPI_VIDEO_INPUTSRCTABLE *pSrcTable= MSrv_Control::GetInstance()->GetSourceList();
#endif
#if (STR_ENABLE == 1)
    mapi_str::AutoRegister _R;
#endif
    MTIMER_IFO("Thread name: %s.\n", name);

    struct timespec ts;
    U32 u32CurrTime, u32TimeNow;
    S32 ret;

    MSrv_Timer * pTimer = (MSrv_Timer *)arg;

    ts.tv_sec = SEC_THREADING_PENDING;
    ts.tv_nsec = 0;
#if (STR_ENABLE == 1)
    while(pTimer->m_bOfftimer == TRUE)
#else
    while(1)
#endif
    {
        /* lock m_mutex_offtimer */
        int intPTHChk = PTH_RET_CHK(pthread_mutex_lock(&pTimer->m_mutex_offtimer));
        ASSERT(intPTHChk == 0);
        /* waiting signal or timeout */
        ret = pthread_cond_timedwait(&pTimer->m_cond_offtimer, &pTimer->m_mutex_offtimer, &ts);
        u32CurrTime = mapi_time_utility::GetTime0() ;

#if (STR_ENABLE == 1)
        if (pTimer->m_bOfftimer == FALSE)
        {
            intPTHChk = PTH_RET_CHK(pthread_mutex_unlock(&pTimer->m_mutex_offtimer));
            ASSERT(intPTHChk == 0);
            break;
        }
#endif


        u32TimeNow = mapi_interface::Get_mapi_system()->RTCGetCLK();

        pTimer->GetOffModeMinTime();
        //MTIMER_DBG("%s:u32PoweroffTime = %u , OffMode = %d\n", __func__, pTimer->m_u32PoweroffTime, pTimer->m_OffMode);

        if((ret != ETIMEDOUT) && (pTimer->m_bSignal == TRUE) && (pTimer->m_OffMode == OFF_MODE_AUTOSLEEP))
        {
            ts.tv_sec = SEC_THREADING_PENDING;
            ts.tv_nsec = 0;
            pTimer->m_bClockAlarmFlag = FALSE;
            pTimer->m_OffMode = OFF_MODE_INVALID_ID;

            intPTHChk = PTH_RET_CHK(pthread_mutex_unlock(&pTimer->m_mutex_offtimer));
            ASSERT(intPTHChk == 0);
            continue;
        }

        if((ret != ETIMEDOUT) && (pTimer->m_bClockAlarmFlag == TRUE) && (0 != pTimer->m_u32PoweroffTime))
        {
            ts.tv_sec = SEC_THREADING_PENDING;
            ts.tv_nsec = 0;
            pTimer->m_OffMode = OFF_MODE_INVALID_ID;

            intPTHChk = PTH_RET_CHK(pthread_mutex_unlock(&pTimer->m_mutex_offtimer));
            ASSERT(intPTHChk == 0);
            continue;
        }

        ts.tv_sec = 0; /* set tv_sec by m_OffMode */
        ts.tv_nsec = 0;
        switch(pTimer->m_OffMode)
        {
            case OFF_MODE_TIMER:
                if((pTimer->m_u32PoweroffTime == 0) && pTimer->IfTodayInTimerTerm(pTimer->m_enOffTimerState) && (MSrv_Control::GetMSrvDtv()->IsScanning() == FALSE)
#if (STB_ENABLE == 0)
                        && (pSrcTable[MAPI_INPUT_SOURCE_ATV].u32EnablePort == 1) && (MSrv_Control::GetMSrvAtv()->IsScanning() == FALSE)
#endif
                  )
                {
                    if(pTimer->m_enOffTimerState == EN_Timer_Once)
                    {
                        pTimer->m_bOffTimeFlag = FALSE;
                        pTimer->m_enOffTimerState = EN_Timer_Off;
                        pTimer->StoreTimerDB();
                        sleep(1);
                    }

                    MTIMER_IFO("######Off Timer PowerOff!\n");
                    MTIMER_IFO("u32CurrTime = %u , m_u32OffTimesecs = %u\n", u32CurrTime, pTimer->m_u32OffTimesecs);

#if (MSTAR_TVOS == 1)
                    pTimer->PostEvent(NULL, EV_TIMER_POWOER_EVENT, 0, 0);
                    ts.tv_sec = SEC_THREADING_PENDING;
                    break;
#else
#if (PVR_ENABLE == 1 && ACTIVE_STANDBY_MODE_ENABLE == 1)
                    MSrv_DTV_Player_DVB *pDVBPlayer = dynamic_cast<MSrv_DTV_Player_DVB*>(MSrv_Control::GetMSrvDtv());
                    ASSERT(pDVBPlayer);
                    if((pDVBPlayer->IsRecording()))
                    {
                        MSrv_Control_common::SetActiveStandbyMode(TRUE);
                    }
                    else
                    {
                        MSrv_Control::GetInstance()->EnterSleepMode();
                    }
#else
                    MSrv_Control::GetInstance()->EnterSleepMode();
#endif
#endif
                }
                break;
            case OFF_MODE_SLEEP:
                if((pTimer->m_u32PoweroffTime == 0) && ((u32CurrTime / SECOND_TO_MS)>= pTimer->m_u32SleepTimesecs)
                        && (pTimer->m_bSleepFlag == TRUE)
                        && (MSrv_Control::GetMSrvDtv()->IsScanning() == FALSE)
#if (STB_ENABLE == 0)
                        && (pSrcTable[MAPI_INPUT_SOURCE_ATV].u32EnablePort == 1) && (MSrv_Control::GetMSrvAtv()->IsScanning() == FALSE)
#endif
                  )
                {
                    MTIMER_IFO("u32CurrTime = %u , SleeperTime = %u\n", u32CurrTime, (pTimer->m_u32SleepTimebase + pTimer->m_u32SleepTimeDur));
                    MTIMER_IFO("Sleep Timer PowerOff!!!\n");

#if (MSTAR_TVOS == 1)
                    pTimer->PostEvent(NULL, EV_TIMER_POWOER_EVENT, 0, 0);
                    ts.tv_sec = SEC_THREADING_PENDING;
                    break;
#else
                    MSrv_Control::GetInstance()->EnterSleepMode();
#endif
                }
                break;
            case OFF_MODE_AUTOSLEEP:
                if((pTimer->m_u32PoweroffTime == 0) && ((u32CurrTime / SECOND_TO_MS) == (pTimer->m_u32AutoSleepTimesecs))
                        && (pTimer->m_bAutoSleepFlag == TRUE)
                        && (pTimer->m_bSignal == FALSE))
                {
                    MTIMER_IFO("u32CurrTime = %u , AutoSleeperTime = %u\n", u32CurrTime, (pTimer->m_u32AutoSleepTimebase + pTimer->m_u32AutoSleepTimeDur));
                    MTIMER_IFO("AutoSleep Timer PowerOff!!!\n");

#if (MSTAR_TVOS == 1)
                    pTimer->PostEvent(NULL, EV_TIMER_POWOER_EVENT, 0, 0);
                    ts.tv_sec = SEC_THREADING_PENDING;
                    break;
#else
                    MSrv_Control::GetInstance()->EnterSleepMode(TRUE,TRUE);
#endif
                }
                if((pTimer->m_bAutoSleepFlag == FALSE) && (ret == ETIMEDOUT))
                {
                    ts.tv_sec = SEC_THREADING_PENDING;
                }
                break;
            case OFF_MODE_INVALID_ID:
            default:
                MTIMER_IFO("No power-off timer be trigger!!!\n");
                ts.tv_sec = SEC_THREADING_PENDING;
                break;
        }
        /* set tv_sec by AutoSleepTime */
        if (ts.tv_sec == 0)
        {
            U32 u32difftime = mapi_time_utility::TimeDiffFromNow0(u32CurrTime)/ SECOND_TO_MS;
            U64 tmp_sec = (u32TimeNow - u32difftime) + pTimer->m_u32AutoSleepTimesecs;
            ts.tv_sec = (tmp_sec > SEC_THREADING_PENDING) ? SEC_THREADING_PENDING : tmp_sec;
        }
        /* unlock m_mutex_offtimer */
        intPTHChk = PTH_RET_CHK(pthread_mutex_unlock(&pTimer->m_mutex_offtimer));
        ASSERT(intPTHChk == 0);
        MTIMER_DBG("%s:ts.tv_sec = %ld\n", __func__,ts.tv_sec);
    }
    return NULL;
}

#define COUNT_DOWN_SLEEP_NS         (500000)        //0.5 second
void *MSrv_Timer::CountDownTime(void *arg)
{
    U8 name[32];
    memset(name, 0, 32);
    prctl(PR_SET_NAME, (unsigned long)"CountDownTime");
    prctl(PR_GET_NAME, (unsigned long)name);
#if (STB_ENABLE == 0)
    const MAPI_VIDEO_INPUTSRCTABLE *pSrcTable= MSrv_Control::GetInstance()->GetSourceList();
#endif
#if (STR_ENABLE == 1)
    mapi_str::AutoRegister _R;
#endif
    MTIMER_IFO("Thread name: %s.\n", name);

    U32 timeCurr , timeNow;
    MSrv_Timer * pTimer = (MSrv_Timer *)arg;

    pTimer->ReconfigTimerFromList(0, TRUE);
#if (STR_ENABLE == 1)
    while(pTimer->m_bCountdown == TRUE)
#else
    while(1)
#endif
    {
        timeCurr = mapi_time_utility::GetTime0() / SECOND_TO_MS;

        timeNow = mapi_interface::Get_mapi_system()->RTCGetCLK();

#if (STR_ENABLE == 1)
if(pTimer->m_bTVPowerOffFlag == TRUE)
{
    if(timeCurr <= pTimer->m_u32TVPowerOffTimeSecs)
    {
        pTimer->m_u32TVPowerOffTimeRemain = pTimer->m_u32TVPowerOffTimeSecs - timeCurr;
        //printf(" timeCur=%u, setTime=%u, remainTime=%u\n", timeCurr, pTimer->m_u32TVPowerOffTimeSecs, pTimer->m_u32TVPowerOffTimeRemain);
        if(pTimer->m_u32TVPowerOffTimeRemain == 0)
        {
            printf("\033[1;31m Himiro Test Timeout!!!:%s:%s[%d]\033[0m\n", __FILE__, __FUNCTION__, __LINE__);
            pTimer->PostEvent(NULL, EV_NOTIFY_POWER_OFF, 0, 0);
        }
        else
        {
            static U32 u32TimeTmp = 0;
            if(pTimer->m_u32TVPowerOffTimeRemain != u32TimeTmp)
            {
                printf(" [TV PW Timer] Remain %u secs, cnt=%u\n", (U16)pTimer->m_u32TVPowerOffTimeRemain, (U16)pTimer->m_bIsTVReallyPowerDown);
                u32TimeTmp = pTimer->m_u32TVPowerOffTimeRemain;

                BOOL bIsWIFI = 0;
                BOOL bEnable = 0;
                U8 u8TempReg = 0;
                //MSrv_Control::GetInstance()->GetMSrvFactoryDefault()->GetFactoryData(DATA_EEPROM_NETWORK_TYPE_ADDRESS, &bIsWIFI);
                //MSrv_Control::GetInstance()->GetMSrvFactoryDefault()->GetFactoryData(DATA_EEPROM_WAKEUP_ON_LAN_ADDRESS, &bEnable);

                if (bEnable == TRUE)
                {
                    if (bIsWIFI == TRUE)
                    {
                        // lerry : wakeup to normal mode on WoW
                        u8TempReg = MApi_XC_ReadByte(0x1425);
                        //printf("u8TempReg 0x%x\n", u8TempReg);
                        // magic packet will pull low GPIO 44
                        if ((u8TempReg & 0x08) == 0)
                        {
                            //MSrv_Control::GetInstance()->GetMSrvFactoryCmd()->WakeupSystemfromWoLCommand();
                        }
                    }
                    else if (bIsWIFI == FALSE)
                    {
                        // lerry : wakeup to normal mode on WoL
                        u8TempReg = MApi_XC_ReadByte(0x1043);
                        //printf("u8TempReg 0x%x\n", u8TempReg);
                        // magic packet will set dummy register
                        if ((u8TempReg & 0x10) == 1)
                        {
                            //MSrv_Control::GetInstance()->GetMSrvFactoryCmd()->WakeupSystemfromWoLCommand();
                        }
                    }
                }
            }
        }
    }
    else
        pTimer->SetTVPowerDoneTimer(0xFFFFFFFF);
}
#endif

        if(pTimer->m_bAutoSleepFlag && (!pTimer->m_bSignal))
        {
            if(timeCurr <= pTimer->m_u32AutoSleepTimesecs)
            {
                pTimer->m_u32AutoSleepRemain = pTimer->m_u32AutoSleepTimesecs - timeCurr;
                // if(pTimer->m_u32AutoSleepTimesecs >= timeCurr) do not need check
                {
                    if((pTimer->m_u32AutoSleepRemain <= 60) && (pTimer->m_bClockAlarmFlag == FALSE))
                    {
                        MTIMER_IFO("%s:AutoSleep Timer Postevent to IdleAPP\n", __func__);
                        pTimer->PostEvent(NULL, EV_LASTMINUTE_WARN, 0, 0);
                        pTimer->m_bClockAlarmFlag = TRUE; //need to be reset iff power-down be cancled
                        MTIMER_DBG("%s:AutoSleep time Remain = %u\n", __func__, pTimer->m_u32AutoSleepRemain);
                    }
                }
            }
        }

        if(pTimer->m_bSleepFlag)
        {
            if(pTimer->m_u32SleepTimesecs >= timeCurr)
            {
                pTimer->m_u32SleepRemain = pTimer->m_u32SleepTimesecs - timeCurr;
                if((pTimer->m_u32SleepRemain <= SleepThreshold) && (pTimer->m_bClockAlarmFlag == FALSE) && (MSrv_Control::GetMSrvDtv()->IsScanning() == FALSE)
#if (STB_ENABLE == 0)
                        && (pSrcTable[MAPI_INPUT_SOURCE_ATV].u32EnablePort == 1) && (MSrv_Control::GetMSrvAtv()->IsScanning() == FALSE)
#endif
                  )
                {
                    if(pTimer->IsRiksTVSleepModeTimerTrigger() == TRUE)
                    {
                        if(pTimer->IsRiksTVMode() == TRUE)
                        {
                            MTIMER_IFO("%s:Sleep Timer Postevent to IdleAPP\n", __func__);
                            pTimer->m_bIsRiksTVSleepTimerCountDownMessageShow = TRUE;
                            pTimer->PostEvent(NULL, EV_LASTMINUTE_WARN, 0, 0);
                            pTimer->m_bClockAlarmFlag = TRUE; //need to be reset iff power-down be cancled
                            MTIMER_DBG("%s:Sleep time Remain = %u\n", __func__, pTimer->m_u32SleepRemain);
                        }
                        else
                        {
                            pTimer->DisableOffMode(OFF_MODE_SLEEP);
                        }
                    }
                    else
                    {
                        MTIMER_IFO("%s:Sleep Timer Postevent to IdleAPP\n", __func__);
                        pTimer->PostEvent(NULL, EV_LASTMINUTE_WARN, 0, 0);
                        pTimer->m_bClockAlarmFlag = TRUE; //need to be reset iff power-down be cancled
                        MTIMER_DBG("%s:Sleep time Remain = %u\n", __func__, pTimer->m_u32SleepRemain);
                    }
                }
            }
        }

        if(pTimer->m_bOADTimeFlag)
        {
            if(pTimer->m_u32OADTimesecs >= timeCurr)
            {
                pTimer->m_u32OADTimeRemain = pTimer->m_u32OADTimesecs - timeCurr;

                if(pTimer->m_u32OADTimeRemain <= 5)
                {
                    MTIMER_IFO("%s:OAD Timer Postevent to IdleAPP\n", __func__);
                    pTimer->PostEvent(NULL, EV_OAD_TIMESCAN, 0, 0);
                    //pTimer->m_bClockAlarmFlag = TRUE; //need to be reset iff power-down be cancled
                    MTIMER_DBG("%s:OAD time Remain = %u\n", __func__, pTimer->m_u32OADTimeRemain);
                    pTimer->m_bOADTimeFlag = FALSE;
                }
            }
        }

        if(pTimer->m_bOffTimeFlag)
        {
            if(pTimer->m_u32OffTimesecs >= (timeNow + mapi_interface::Get_mapi_system()->GetClockOffset()))
            {
                if(pTimer->m_u32OffTimebase != SEC_THREADING_PENDING)
                {
                    // SH@ for mantis:304053, off-timer issue. The SN tip code will be added, too.
                    if(pTimer->m_u32OffTimebase >= timeCurr)
                    {
                        pTimer->m_u32OffRemain = pTimer->m_u32OffTimebase - timeCurr;
                    }
                }
                else
                {
                    pTimer->m_u32OffRemain = pTimer->m_u32OffTimesecs - (timeNow + mapi_interface::Get_mapi_system()->GetClockOffset());
                }

                if((pTimer->m_u32OffRemain <= 60) && (pTimer->m_bClockAlarmFlag == FALSE) && (MSrv_Control::GetMSrvDtv()->IsScanning() == FALSE)
#if (STB_ENABLE == 0)
                        && (pSrcTable[MAPI_INPUT_SOURCE_ATV].u32EnablePort == 1) && (MSrv_Control::GetMSrvAtv()->IsScanning() == FALSE)
#endif
                  )
                {
                    MTIMER_IFO("%s:Off Timer Postevent to IdleAPP\n", __func__);
                    pTimer->PostEvent(NULL, EV_LASTMINUTE_WARN, 0, 0);
                    pTimer->m_bClockAlarmFlag = TRUE;
                    pTimer->m_u32OffTimebase = timeCurr + pTimer->m_u32OffRemain;
                    MTIMER_DBG("%s:Off time Remain = %u\n", __func__, pTimer->m_u32OffRemain);
                }
            }
            else  // for mantis 0386234 about sometimes stopping countdown, because the timeNow doesnot increase continuously
            {
                if(pTimer->m_u32OffTimebase != SEC_THREADING_PENDING)
                {
                    if(pTimer->m_u32OffTimebase >= timeCurr)
                    {
                        pTimer->m_u32OffRemain = pTimer->m_u32OffTimebase - timeCurr;
                    }
                }
                MTIMER_IFO("%s: @@@m_u32OffTimebase = %u, timeCurr = %u\n", __func__, pTimer->m_u32OffTimebase, timeCurr);
            }
        }

        //Post event to notify UI to pop-up count-down window with
        if((pTimer->m_bClockAlarmFlag == TRUE) &&
                ((pTimer->m_bAutoSleepFlag == TRUE) || (pTimer->m_bOffTimeFlag == TRUE) || (pTimer->m_bSleepFlag == TRUE)))
        {

            pTimer->PostEvent(NULL, EV_UPDATE_LASTMINUTE, pTimer->GetOffModeMinTime(), (U32)pTimer->m_OffMode);
            if(0 == pTimer->GetOffModeMinTime())
            {
                pTimer->PostOfftimerCondSignal();
            }
        }

//printf("RecordStartTime=%x current=%x\n",pTimer->m_u32EPGTimerActionTime,mapi_interface::Get_mapi_system()->RTCGetCLK());
        //EPG timer
        U32 current = mapi_interface::Get_mapi_system()->RTCGetCLK();

        const MAPI_VIDEO_INPUTSRCTABLE *pSrcTable;
        pSrcTable = MSrv_Control::GetInstance()->GetSourceList();
        if (pSrcTable[MAPI_INPUT_SOURCE_DTV].u32EnablePort)
        {
#if (EPG_ENABLE == 1 && PVR_ENABLE == 1 && ATSC_SYSTEM_ENABLE == 0)
            pTimer->_TrackScheChanging();
#endif
        }
        if(EN_EPGTIMER_RECORDER_TIMEUP == pTimer->m_EPGTimerRecordStatus)
        {
            pTimer->m_EPGTimerRecordStatus = EN_EPGTIMER_RECORDER_TIMEUP_TO_REC;
            pTimer->m_EPGTimerRecordLeadingTime = mapi_interface::Get_mapi_system()->RTCGetCLK();
        }
        else if(EN_EPGTIMER_RECORDER_TIMEUP_TO_REC == pTimer->m_EPGTimerRecordStatus)
        {
            if(current  >= (pTimer->m_EPGTimerRecordLeadingTime + EPGTIMER_RECORDER_LEADING_TIME_S))
            {
                if (!pTimer->GetEPGTimerEventByIndex(pTimer->m_RecordingProgram, 0))
                {
                    memset(&pTimer->m_RecordingProgram, 0, sizeof(pTimer->m_RecordingProgram));
                }

                if((pTimer->m_RecordingProgram.enRepeatMode != EPG_EVENT_RECORDER_EVENT_ID) ||
                   ((pTimer->m_RecordingProgram.enRepeatMode == EPG_EVENT_RECORDER_EVENT_ID) && (pTimer->_IsEventRecordProgramReady() == TRUE)))
                {
                    //Start Record
                    _AddPaddingsToEpgEventTimerInfo(pTimer->m_RecordingProgram);
                    pTimer->PostEvent(NULL, EV_EPGTIMER_RECORD_START, pTimer->m_RecordingProgram.u8DtvRoute);
                    pTimer->m_EPGTimerRecordLeadingTime = 0;
                    pTimer->m_u32EPGTimerActionTime = INVALID_EPG_TIME;
                    pTimer->m_EPGTimerRecordStatus = EN_EPGTIMER_RECORDER_TIMEUP_RECORDING;
                }
                else
                {
                    memset(&pTimer->m_RecordingProgram, 0, sizeof(pTimer->m_RecordingProgram));
                    pTimer->m_EPGTimerRecordStatus = EN_EPGTIMER_RECORDER_IDLE;
                }
            }
        }
        else if(EN_EPGTIMER_RECORDER_TIMEUP_RECORDING == pTimer->GetEPGTimerRecordStatus())
        {
            if(pTimer->m_NextB2BRecordingProgram.u32StartTime>0)
            {
                //printf("Has B2B event\n");
                U32 timeUp = pTimer->m_NextB2BRecordingProgram.u32StartTime - (current+EPGTIMER_RECORDER_LEADING_TIME_S);
                if((0 < timeUp) && (timeUp <= EPGTIMER_COUNTDOWN_LEADING_TIME_S))
                {
                    pTimer->PostEvent(NULL, EV_EPGTIMER_COUNTDOWN, timeUp, EN_EPGTIMER_ACT_RECORDER_START);
                }
                else if(timeUp<=0)
                {
                    //pTimer->ResetStopppedTimerListItem(0);
                    pTimer->PostEvent(NULL, EV_EPGTIMER_COUNTDOWN, 0, EN_EPGTIMER_ACT_RECORDER_START);
                    pTimer->PostEvent(NULL, EV_PVR_NOTIFY_SCHEDULED_STOP, pTimer->m_RecordingProgram.u8DtvRoute);
                    //pTimer->m_EPGTimerRecordStatus = EN_EPGTIMER_RECORDER_IDLE;
                }
            }
            //if(current >= (pTimer->m_RecordingProgram.u32StartTime+pTimer->m_RecordingProgram.u32DurationTime))
            /*
             * Freeview+ DTR 2.11.TEST 3 selects the last event in stream (Event 12 in AltInst_Ch23C.mpg)
             * to record, and the record do not stop since playcard loops.
             * Checking less-than start time is a workaround.
             */
            else if((current >= (pTimer->m_RecordingProgram.u32StartTime+pTimer->m_RecordingProgram.u32DurationTime))
                    || (current < pTimer->m_RecordingProgram.u32StartTime))
            {
                pTimer->m_bIsRunawayRecording = FALSE;
                #if (EPG_ENABLE == 1 && PVR_ENABLE == 1 && ATSC_SYSTEM_ENABLE == 0)
                MSrv_DTV_Player_DVB* pDVBPlayer = dynamic_cast<MSrv_DTV_Player_DVB*>(MSrv_Control::GetMSrvDtv());
                ASSERT(pDVBPlayer);
                ST_EPG_EVENT_INFO  stEPGEvent;
                if(EPG_EVENT_RECORDER_EVENT_ID == pTimer->m_RecordingProgram.enRepeatMode)
                {
                    if(!pDVBPlayer->GetPresentEvent(pTimer->m_RecordingProgram.u8ServiceType, pTimer->m_RecordingProgram.u16ServiceNumber,EPG_NONE_DESCRIPTION, stEPGEvent))
                    {
                        pTimer->m_bIsRunawayRecording = TRUE;
                        MTIMER_DBG("RunAway recording (No PF)!!!\n");
                    }
                    else if(stEPGEvent.u16eventId == pTimer->m_RecordingProgram.u16EventID)
                    {
                        pTimer->m_bIsRunawayRecording = TRUE;
                        MTIMER_DBG("RunAway recording (Same Event ID %d)!!!\n", stEPGEvent.u16eventId);
                    }
                }
                #endif

#if (ATSC_SYSTEM_ENABLE == 0)
                if((!pTimer->m_bIsRunawayRecording) || (pTimer->m_bIsRunawayRecording
                    && (current >= GET_REAL_UTC_TIME_BY_COUNTRY(MSrv_Control::GetMSrvSystemDatabase()->GetSystemCountry(),(pTimer->m_RecordingProgram.u32StartTime+pTimer->m_RecordingProgram.u32DurationTime)))))
#else
                if((!pTimer->m_bIsRunawayRecording) || (pTimer->m_bIsRunawayRecording
                    && (current >= (pTimer->m_RecordingProgram.u32StartTime+pTimer->m_RecordingProgram.u32DurationTime))))
#endif
                {
                    pTimer->PostEvent(NULL, EV_PVR_NOTIFY_SCHEDULED_STOP, pTimer->m_RecordingProgram.u8DtvRoute);
                    pTimer->m_EPGTimerRecordStatus = EN_EPGTIMER_RECORDER_IDLE;
                }
            }
        }
        else
        {
            if(pTimer->m_u32EPGTimerActionTime != INVALID_EPG_TIME)
            {
                if(((pTimer->m_u32EPGTimerActionTime <= (current - EPGTIMER_CI_OP_REFRESH_LEADING_TIME_S)) && (pTimer->m_EPGTimerActionMode == EN_EPGTIMER_ACT_CI_OP_REFRESH)) || \
                    ((pTimer->m_u32EPGTimerActionTime <= (current - EPGTIMER_REMINDER_LEADING_TIME_S)) && (pTimer->m_EPGTimerActionMode == EN_EPGTIMER_ACT_REMINDER)) || \
                    ((pTimer->m_u32EPGTimerActionTime <= (current + EPGTIMER_RECORDER_LEADING_TIME_S)) && (pTimer->m_EPGTimerActionMode == EN_EPGTIMER_ACT_RECORDER_START)))
                {
                    pTimer->PostEvent(NULL, EV_EPGTIMER_COUNTDOWN, 0, EN_EPGTIMER_ACT_RECORDER_START);
                }
                else if(pTimer->m_EPGTimerActionMode == EN_EPGTIMER_ACT_CI_OP_REFRESH)
                {
                    U32 timeUp = pTimer->m_u32EPGTimerActionTime - current;
                    if((0 < timeUp) && (timeUp <= EPGTIMER_COUNTDOWN_LEADING_TIME_S))
                    {
                        pTimer->PostEvent(NULL, EV_EPGTIMER_COUNTDOWN, timeUp, EN_EPGTIMER_ACT_CI_OP_REFRESH);
                    }
                }
                else if(pTimer->m_EPGTimerActionMode == EN_EPGTIMER_ACT_REMINDER)
                {
                    U32 timeUp = pTimer->m_u32EPGTimerActionTime - current;
                    if((0 < timeUp) && (timeUp <= EPGTIMER_COUNTDOWN_LEADING_TIME_S))
                    {
                        //If  Current source is dtv and current channel is the same with channel which set up  in reminder, the count down UI will be ignored
                        MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
                        MAPI_INPUT_SOURCE_TYPE eInputSrc;
                        eInputSrc = MSrv_Control::GetInstance()->GetCurrentInputSource();
                        ASSERT(pSysDB);
                        ST_EPG_EVENT_TIMER_INFO *pEventList = NULL;
                        U32 listSize = 0;
                        pSysDB->UseEpgTimerList(&pEventList, &listSize);
                        U16 actionServiceNumber = pEventList[0].u16ServiceNumber;

                        if (eInputSrc == MAPI_INPUT_SOURCE_DTV)
                        {
                            MS_CM_GET_SERVICE_INFO pCcurrProgramInfo;
                            MSrv_Control_common::GetMSrvChannelManager()->GetCurrProgramInfo(&pCcurrProgramInfo);
                            if(pCcurrProgramInfo.u32Number == actionServiceNumber)
                            {
                                //same program case
                            }
                            else
                            {
                                pTimer->PostEvent(NULL, EV_EPGTIMER_COUNTDOWN, timeUp, EN_EPGTIMER_ACT_REMINDER);
                            }
                        }
                        else
                        {
                            pTimer->PostEvent(NULL, EV_EPGTIMER_COUNTDOWN, timeUp, EN_EPGTIMER_ACT_REMINDER);
                        }
                    }
                }
                else if(pTimer->m_EPGTimerActionMode == EN_EPGTIMER_ACT_RECORDER_START)
                {
                    U32 timeUp = pTimer->m_u32EPGTimerActionTime - (current+EPGTIMER_RECORDER_LEADING_TIME_S);
                    if((0 < timeUp) && (timeUp <= EPGTIMER_COUNTDOWN_LEADING_TIME_S))
                    {
                        //If Current source is dtv and current channel is the same with channel which set up  in recorder, the count down UI will be ignored
                        MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
                        MAPI_INPUT_SOURCE_TYPE eInputSrc;
                        eInputSrc = MSrv_Control::GetInstance()->GetCurrentInputSource();
                        ASSERT(pSysDB);
                        ST_EPG_EVENT_TIMER_INFO *pEventList = NULL;
                        U32 listSize = 0;
                        pSysDB->UseEpgTimerList(&pEventList, &listSize);
                        U16 actionServiceNumber = pEventList[0].u16ServiceNumber;

                        if (eInputSrc == MAPI_INPUT_SOURCE_DTV)
                        {
                            MS_CM_GET_SERVICE_INFO pCcurrProgramInfo;
                            MSrv_Control_common::GetMSrvChannelManager()->GetCurrProgramInfo(&pCcurrProgramInfo);
                            if(pCcurrProgramInfo.u32Number == actionServiceNumber)
                            {
                                //same program case
                            }
                            else
                            {
                                pTimer->PostEvent(NULL, EV_EPGTIMER_COUNTDOWN, timeUp, EN_EPGTIMER_ACT_RECORDER_START);
                            }
                        }
                        else
                        {
                            pTimer->PostEvent(NULL, EV_EPGTIMER_COUNTDOWN, timeUp, EN_EPGTIMER_ACT_RECORDER_START);
                        }
                    }
                }
                /*
                    else if((pTimer->m_EPGTimerActionMode == EN_EPGTIMER_ACT_REMINDER) || (pTimer->m_EPGTimerActionMode == EN_EPGTIMER_ACT_RECORDER_START))
                    {
                        U32 timeUp = pTimer->m_u32EPGTimerActionTime - (current + EPGTIMER_RECORDER_LEADING_TIME_S);
                        if(timeUp <= EPGTIMER_COUNTDOWN_LEADING_TIME_S)
                        {
                            pTimer->PostEvent(NULL, EV_EPGTIMER_COUNTDOWN, timeUp, 0);
                        }
                    }
                */
            }
        }

        usleep(COUNT_DOWN_SLEEP_NS);
    }
    return NULL;
}

void MSrv_Timer::StopEPGRecord()
{
    m_u32EPGTimerRecordDuration = INVALID_EPG_TIME;
    m_EPGTimerRecordStatus = EN_EPGTIMER_RECORDER_IDLE;
    ST_EPG_EVENT_TIMER_INFO stTimerInfo;
    if(GetEPGTimerEventByIndex(stTimerInfo, 0) == TRUE)
    {
        _AddPaddingsToEpgEventTimerInfo(stTimerInfo);
        if((m_RecordingProgram.u8ServiceType == stTimerInfo.u8ServiceType)
            && (m_RecordingProgram.u16ServiceNumber == stTimerInfo.u16ServiceNumber)
            && (m_RecordingProgram.u32StartTime == stTimerInfo.u32StartTime))
        {
            ResetStopppedTimerListItem(0); // remove timer event if record stop earlier
        }
    }
    ReconfigTimerFromList(mapi_interface::Get_mapi_system()->RTCGetCLK() + 1);
    memset(&m_RecordingProgram,0,sizeof(m_RecordingProgram));
}

U32 MSrv_Timer::GetEPGRecordDuration()
{
    return m_u32EPGTimerRecordDuration;
}

U32 MSrv_Timer::GetEPGTimerRecordStatus()
{
    return m_EPGTimerRecordStatus;
}
BOOL MSrv_Timer::GetEPGTimerRecordingProgram(ST_EPG_EVENT_TIMER_INFO& stTimerInfo) const
{
    if((EN_EPGTIMER_RECORDER_TIMEUP_TO_REC == m_EPGTimerRecordStatus)
            || (EN_EPGTIMER_RECORDER_TIMEUP_RECORDING == m_EPGTimerRecordStatus))
    {
        ASSERT(EPG_EVENT_RECORDER == m_RecordingProgram.enTimerType);
        stTimerInfo = m_RecordingProgram;
        return TRUE;
    }
    return FALSE;
}

#define CHECK_SYSTEM_CLK_CHG_INTERVAL 30 //unit:second
void *MSrv_Timer::PollingCurrTime(void *arg)
{
    U8 name[32];
    U32 u32CurTime = 0;
    U32 u32PreCurTime = 0;
    memset(name, 0, 32);
    prctl(PR_SET_NAME, (unsigned long)"PollingCurrTime");
    prctl(PR_GET_NAME, (unsigned long)name);
#if (STR_ENABLE == 1)
    mapi_str::AutoRegister _R;
#endif
    MTIMER_IFO("Thread name: %s.\n", name);

// EosTek Patch Begin
    MSrv_Factory_Mode *factory = MSrv_Control::GetMSrvFactoryMode();
    if (NULL != factory)
    {
        BOOL bRet = factory->EosUpdateHDCPKey();
        printf("EosUpdateHDCPKey ret = %d\n", bRet);
#if (HDMI_HDCP22_ENABLE == 1)
        bRet = factory->EosUpdateHDCPKey(true);
        printf("EosUpdateHDCPKey2.2 ret = %d\n", bRet);
#endif

        bRet = factory->EosUpdateWB();
        printf("EosUpdateWB ret = %d\n", bRet);
    }
// EosTek Patch End

    MSrv_Timer * pTimer = (MSrv_Timer *)arg;

    u32CurTime = mapi_interface::Get_mapi_system()->RTCGetCLK();
    u32PreCurTime = u32CurTime;

#if (STR_ENABLE == 1)
    while(pTimer->m_bPoll == TRUE)
#else
    while(1)
#endif
    {
        u32CurTime = mapi_interface::Get_mapi_system()->RTCGetCLK();
        if(((u32CurTime > u32PreCurTime) && (u32CurTime - u32PreCurTime >= CHECK_SYSTEM_CLK_CHG_INTERVAL))
            || (u32PreCurTime > u32CurTime))
        {
            MTIMER_FLOW("\t ----->>System clock Change<<-------\n");
            pTimer->PostEvent(NULL, EV_TIMER_SYSTEM_CLK_CHG, 0, 0);
        }

        u32PreCurTime = u32CurTime;
#if(MSTAR_TVOS == 0)
        pTimer->m_Ntp_Entity_Obj->NtpProcessMonitor();
#endif
        pTimer->PostEvent(NULL, EV_ONESECOND_BEAT, 0, 0);
#if (STR_ENABLE == 1)
        for(int i=0;i<10;i++)
        {
            if(pTimer->m_bPoll != TRUE)break;
            usleep(100000);
        }
#else
        sleep(1);
#endif

    }
    return NULL;
}

U32 MSrv_Timer::GetNextTimer(const ST_time &stTime, EN_Timer_Period enState)
{

    EN_DayofWeek _Today = SUN;
    U32 _u32NextDay = 0;
    U32 NextTime, CurrTime;
    struct tm timeinfo;

    GetCLKTime(timeinfo);
    MTIMER_IFO("%s\n", asctime(&timeinfo));
    if(enState == EN_Timer_Off)
    {
        return 0;
    }
    else
    {
        _Today = (EN_DayofWeek)timeinfo.tm_wday;
        NextTime = ConvertStTime2Seconds(&stTime);

        CurrTime = (U32)mapi_interface::Get_mapi_system()->RTCGetCLK() + mapi_interface::Get_mapi_system()->GetClockOffset();

        MTIMER_IFO("NextTime = %u, CurrTime = %u\n", NextTime, CurrTime);
        if(NextTime < CurrTime) //the time pass, find next day
        {
            switch(enState)
            {
                default :
                case EN_Timer_Off:
                    return 0;

                case EN_Timer_Once:
                case EN_Timer_Everyday:
                    _u32NextDay = SECONDS_PER_DAY;
                    break;

                case EN_Timer_Mon2Fri:
                    switch(_Today)
                    {
                        case FRI:
                            _u32NextDay += SECONDS_PER_DAY * 3;
                            break;
                        case SAT:
                            _u32NextDay += SECONDS_PER_DAY * 2;
                            break;
                        default:
                            _u32NextDay += SECONDS_PER_DAY;
                            break;
                    }
                    break;

                case EN_Timer_Mon2Sat:
                    switch(_Today)
                    {
                        case SAT:
                            _u32NextDay += SECONDS_PER_DAY * 2;
                            break;
                        default:
                            _u32NextDay += SECONDS_PER_DAY;
                            break;
                    }
                    break;

                case EN_Timer_Sat2Sun:
                    switch(_Today)
                    {
                        case SUN:
                            _u32NextDay += SECONDS_PER_DAY * 6;
                            break;
                        case MON:
                            _u32NextDay += SECONDS_PER_DAY * 5;
                            break;
                        case TUE:
                            _u32NextDay += SECONDS_PER_DAY * 4;
                            break;
                        case WED:
                            _u32NextDay += SECONDS_PER_DAY * 3;
                            break;
                        case THU:
                            _u32NextDay += SECONDS_PER_DAY * 2;
                            break;
                        case FRI:
                        case SAT:
                            _u32NextDay += SECONDS_PER_DAY;
                            break;
                        default:
                            break;
                    }
                    break;

                case EN_Timer_Sun:
                    switch(_Today)
                    {
                        case SUN:
                            _u32NextDay += SECONDS_PER_DAY * 7;
                            break;
                        case MON:
                            _u32NextDay += SECONDS_PER_DAY * 6;
                            break;
                        case TUE:
                            _u32NextDay += SECONDS_PER_DAY * 5;
                            break;
                        case WED:
                            _u32NextDay += SECONDS_PER_DAY * 4;
                            break;
                        case THU:
                            _u32NextDay += SECONDS_PER_DAY * 3;
                            break;
                        case FRI:
                            _u32NextDay += SECONDS_PER_DAY * 2;
                            break;
                        case SAT:
                            _u32NextDay += SECONDS_PER_DAY;
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }
        else
        {
            switch(enState)
            {
                default :
                case EN_Timer_Off:
                    return 0;

                case EN_Timer_Once:
                case EN_Timer_Everyday:
                    if(NextTime - CurrTime >= SECONDS_PER_DAY)
                        _u32NextDay = -SECONDS_PER_DAY;
                    else
                        _u32NextDay = 0;
                    break;

                case EN_Timer_Mon2Fri:
                    switch(_Today)
                    {
                        case SAT:
                            _u32NextDay += SECONDS_PER_DAY * 2;
                            break;
                        case SUN:
                            _u32NextDay += SECONDS_PER_DAY;
                            break;
                        default:
                            break;
                    }
                    break;

                case EN_Timer_Mon2Sat:
                    if(_Today == SUN)
                        _u32NextDay += SECONDS_PER_DAY;
                    break;

                case EN_Timer_Sat2Sun:
                    switch(_Today)
                    {
                        case MON:
                            _u32NextDay += SECONDS_PER_DAY * 5;
                            break;
                        case TUE:
                            _u32NextDay += SECONDS_PER_DAY * 4;
                            break;
                        case WED:
                            _u32NextDay += SECONDS_PER_DAY * 3;
                            break;
                        case THU:
                            _u32NextDay += SECONDS_PER_DAY * 2;
                            break;
                        case FRI:
                            _u32NextDay += SECONDS_PER_DAY;
                            break;
                        default:
                            break;
                    }
                    break;

                case EN_Timer_Sun:
                    switch(_Today)
                    {
                        case MON:
                            _u32NextDay += SECONDS_PER_DAY * 6;
                            break;
                        case TUE:
                            _u32NextDay += SECONDS_PER_DAY * 5;
                            break;
                        case WED:
                            _u32NextDay += SECONDS_PER_DAY * 4;
                            break;
                        case THU:
                            _u32NextDay += SECONDS_PER_DAY * 3;
                            break;
                        case FRI:
                            _u32NextDay += SECONDS_PER_DAY * 2;
                            break;
                        case SAT:
                            _u32NextDay += SECONDS_PER_DAY;
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }
        return (NextTime + _u32NextDay);
    }
}

/* Return seconds */
U8 MSrv_Timer::GetSleepModeTime()
{
    U32 timeRemain, timeCurr;
    timeCurr = mapi_time_utility::GetTime0() / SECOND_TO_MS;

    if(timeCurr <= (m_u32SleepTimesecs))
    {
        timeRemain = (m_u32SleepTimesecs) - timeCurr;
    }
    else
    {
        timeRemain = 0;
    }

    if((timeRemain % SECONDS_PER_MIN) == 0)
    {
        return (timeRemain / SECONDS_PER_MIN);
    }
    else
    {
        return (timeRemain / SECONDS_PER_MIN) + 1;
    }
}

U32 MSrv_Timer::GetOffModeMinTime()
{
    //MTIMER_DBG("%s:m_u32OffRemain = %u, m_u32SleepRemain = %u,m_u32AutoSleepRemain = %u\n", __func__, m_u32OffRemain, m_u32SleepRemain, m_u32AutoSleepRemain);

    U32 timer_remain_list[OFF_MODE_NUM];
    EN_OffTimerMode timer_remain_id[OFF_MODE_NUM];

    timer_remain_list[OFF_MODE_TIMER] = m_u32OffRemain;
    timer_remain_list[OFF_MODE_SLEEP] = m_u32SleepRemain;
    timer_remain_list[OFF_MODE_AUTOSLEEP] = m_u32AutoSleepRemain;

    timer_remain_id[OFF_MODE_TIMER] = OFF_MODE_TIMER;
    timer_remain_id[OFF_MODE_SLEEP] = OFF_MODE_SLEEP;
    timer_remain_id[OFF_MODE_AUTOSLEEP] = OFF_MODE_AUTOSLEEP;

    // bubble sort.  small -> large
    // get the smallest one
    for(int i = 0;  i < OFF_MODE_NUM; i++)
    {
        for(int j = i + 1; j < OFF_MODE_NUM; j++)
        {
            if(timer_remain_list[i] > timer_remain_list[j])
            {
                U32 tmp;
                EN_OffTimerMode tmp_id;

                tmp = timer_remain_list[i];
                timer_remain_list[i] = timer_remain_list[j];
                timer_remain_list[j] = tmp;

                tmp_id = timer_remain_id[i];
                timer_remain_id[i] = timer_remain_id[j];
                timer_remain_id[j] = tmp_id;
            }
        }
    }

    if(timer_remain_list[0] == SEC_THREADING_PENDING)
    {
        m_OffMode = OFF_MODE_INVALID_ID;
        m_u32PoweroffTime = SEC_THREADING_PENDING;
    }
    else
    {
        m_OffMode = timer_remain_id[0];
        m_u32PoweroffTime = timer_remain_list[0];
    }

    return m_u32PoweroffTime;
}

BOOL MSrv_Timer::GetAutoSyncState()
{
    return m_bAutoSyncFlag;
}

void MSrv_Timer::SetAutoSyncState(BOOL bflag)
{
    m_bAutoSyncFlag = bflag;
}

BOOL  MSrv_Timer::GetDaylightSavingState()
{
    return m_bIsDaylightsaving;
}

void MSrv_Timer::SetDaylightSavingState(BOOL bflag)
{
    m_bIsDaylightsaving = bflag;
    SetTimeZone(GetTimeZone(), TRUE);
}

void MSrv_Timer::GetZoneInfoPath(char * tzPath)
{
    strcpy(tzPath, "/usr/share/zoneinfo");

    switch(m_enTimeZone)
    {
        // GMT
        case TIMEZONE_CANARY:
            strcat(tzPath, "/Atlantic/Canary");
            break;
        case TIMEZONE_DUBLIN:
            strcat(tzPath, "/Europe/Dublin");
            break;
        case TIMEZONE_LISBON:
            strcat(tzPath, "/Europe/Lisbon");
            break;
        case TIMEZONE_LONDON:
            strcat(tzPath, "/Europe/London");
            break;

        // GMT + 1
        case TIMEZONE_AMSTERDAM:
            strcat(tzPath, "/Europe/Amsterdam");
            break;
        case TIMEZONE_BEOGRAD:
            strcat(tzPath, "/Europe/Belgrade");
            break;
        case TIMEZONE_BERLIN:
            strcat(tzPath, "/Europe/Berlin");
            break;

        case TIMEZONE_BRUSSELS:
            strcat(tzPath, "/Europe/Brussels");
            break;
        case TIMEZONE_BUDAPEST:
            strcat(tzPath, "/Europe/Budapest");
            break;
        case TIMEZONE_COPENHAGEN:
            strcat(tzPath, "/Europe/Copenhagen");
            break;
        case TIMEZONE_LIUBLJANA:
            strcat(tzPath, "/Europe/Ljubljana"); // It's Ljubljana, not Liubljana.
            break;
        case TIMEZONE_LUXEMBOURG:
            strcat(tzPath, "/Europe/Luxembourg");
            break;
        case TIMEZONE_MADRID:
            strcat(tzPath, "/Europe/Madrid");
            break;
        case TIMEZONE_OSLO:
            strcat(tzPath, "/Europe/Oslo");
            break;
        case TIMEZONE_PARIS:
            strcat(tzPath, "/Europe/Paris");
            break;
        case TIMEZONE_PRAGUE:
            strcat(tzPath, "/Europe/Prague");
            break;
        case TIMEZONE_ROME:
            strcat(tzPath, "/Europe/Rome");
            break;
        case TIMEZONE_STOCKHOLM:
            strcat(tzPath, "/Europe/Stockholm");
            break;
        case TIMEZONE_WARSAW:
            strcat(tzPath, "/Europe/Warsaw");
            break;
        case TIMEZONE_VIENNA:
            strcat(tzPath, "/Europe/Vienna");
            break;
        case TIMEZONE_ZAGREB:
            strcat(tzPath, "/Europe/Zagreb");
            break;

        // GMT + 2
        case TIMEZONE_ATHENS:
            strcat(tzPath, "/Europe/Athens");
            break;
        case TIMEZONE_BUCURESTI:
            strcat(tzPath, "/Europe/Bucharest"); // Bucuresti = Bucharest
            break;
        case TIMEZONE_HELSINKI:
            strcat(tzPath, "/Europe/Helsinki");
            break;
        case TIMEZONE_ISTANBUL:
            strcat(tzPath, "/Europe/Istanbul");
            break;
        case TIMEZONE_SOFIA:
            strcat(tzPath, "/Europe/Sofia");
            break;
        case TIMEZONE_TALLINN:
            strcat(tzPath, "/Europe/Tallinn");
            break;
        case TIMEZONE_VILNIUS:
            strcat(tzPath, "/Europe/Vilnius");
            break;

        // GMT + 3
        case TIMEZONE_MOSCOW:
            strcat(tzPath, "/Europe/Moscow");
            break;

        //GMT + 8
        case TIMEZONE_WA:
            strcat(tzPath, "/Australia/West");
            break;

        //GMT + 9.5
        case TIMEZONE_SA:
            strcat(tzPath, "/Australia/South");
            break;
        case TIMEZONE_NT:
            strcat(tzPath, "/Australia/South");
            break;

        //GMT + 10
        case TIMEZONE_NSW:
            strcat(tzPath, "/Australia/NSW");
            break;
        case TIMEZONE_VIC:
            strcat(tzPath, "/Australia/Victoria");
            break;
        case TIMEZONE_QLD:
            strcat(tzPath, "/Australia/Queensland");
            break;
        case TIMEZONE_TAS:
            strcat(tzPath, "/Australia/Tasmania");
            break;

        //GMT +  12
        case TIMEZONE_NZST:
            strcat(tzPath, "/NZ");
            break;

        //GMT - 10
        case TIMEZONE_NORTH_AMERICA_HAWAIIAN:
            strcat(tzPath, "/US/Hawaii");
            break;

        //GMT - 9
        case TIMEZONE_NORTH_AMERICA_ALASKAN:
            strcat(tzPath, "/US/Alaska");
            break;

        //GMT - 8
        case TIMEZONE_NORTH_AMERICA_PACIFIC:
            strcat(tzPath, "/US/Pacific");
            break;

        //GMT - 7
        case TIMEZONE_NORTH_AMERICA_MOUNTAIN:
            strcat(tzPath, "/US/Mountain");
            break;

        //GMT - 6
        case TIMEZONE_NORTH_AMERICA_CENTRAL:
            strcat(tzPath, "/US/Central");
            break;

        //GMT - 5
        case TIMEZONE_AM_WEST:
            strcat(tzPath, "/America/Eirunepe");
            break;
        case TIMEZONE_ACRE:
            strcat(tzPath, "/America/Rio_Branco");
            break;
        case TIMEZONE_NORTH_AMERICA_EASTERN:
            strcat(tzPath, "/US/Eastern");
            break;

        //GMT - 4
        case TIMEZONE_M_GROSSO:
            strcat(tzPath, "/America/Cuiaba");
            break;
        case TIMEZONE_NORTH:
            strcat(tzPath, "/America/Boa_Vista");
            break;
        case TIMEZONE_NORTH_AMERICA_ATLANTIC:
            strcat(tzPath, "/Canada/Atlantic");
            break;

        //GMT - 3
        case TIMEZONE_BRASILIA:
        case TIMEZONE_NORTHEAST:
            strcat(tzPath, "/Brazil/East");
            break;

        //GMT - 2
        case TIMEZONE_F_NORONHA:
            strcat(tzPath, "/America/Noronha");
            break;

        default:
            if(m_enTimeZone>=TIMEZONE_GMT_0_START && m_enTimeZone<=TIMEZONE_GMT_0_END)
            {
                strcat(tzPath, "/Etc/GMT");
                break;
            }
            else if(m_enTimeZone>=TIMEZONE_GMT_1_START && m_enTimeZone<=TIMEZONE_GMT_1_END)
            {
                strcat(tzPath, "/Etc/GMT+1");
                break;
            }
            else if(m_enTimeZone>=TIMEZONE_GMT_2_START && m_enTimeZone<=TIMEZONE_GMT_2_END)
            {
                strcat(tzPath, "/Etc/GMT+2");
                break;
            }
            else if(m_enTimeZone>=TIMEZONE_GMT_3_START && m_enTimeZone<=TIMEZONE_GMT_3_END)
            {
                strcat(tzPath, "/Etc/GMT+3");
                break;
            }
            else if(m_enTimeZone>=TIMEZONE_GMT_8_START && m_enTimeZone<=TIMEZONE_GMT_8_END)
            {
                strcat(tzPath, "/Etc/GMT+8");
                break;
            }
            else if(m_enTimeZone>=TIMEZONE_GMT_10_START && m_enTimeZone<=TIMEZONE_GMT_10_END)
            {
                strcat(tzPath, "/Etc/GMT+10");
                break;
            }
            else if(m_enTimeZone>=TIMEZONE_GMT_12_START && m_enTimeZone<=TIMEZONE_GMT_12_END)
            {
                strcat(tzPath, "/Etc/GMT+12");
                break;
            }
            else if(m_enTimeZone>=TIMEZONE_GMT_Minus10_START && m_enTimeZone<=TIMEZONE_GMT_Minus10_END)
            {
                strcat(tzPath, "/Etc/GMT-10");
                break;
            }
            else if(m_enTimeZone>=TIMEZONE_GMT_Minus9_START && m_enTimeZone<=TIMEZONE_GMT_Minus9_END)
            {
                strcat(tzPath, "/Etc/GMT-9");
                break;
            }
            else if(m_enTimeZone>=TIMEZONE_GMT_Minus8_START && m_enTimeZone<=TIMEZONE_GMT_Minus8_END)
            {
                strcat(tzPath, "/Etc/GMT-8");
                break;
            }
            else if(m_enTimeZone>=TIMEZONE_GMT_Minus7_START && m_enTimeZone<=TIMEZONE_GMT_Minus7_END)
            {
                strcat(tzPath, "/Etc/GMT-7");
                break;
            }
            else if(m_enTimeZone>=TIMEZONE_GMT_Minus6_START && m_enTimeZone<=TIMEZONE_GMT_Minus6_END)
            {
                strcat(tzPath, "/Etc/GMT-6");
                break;
            }
            else if(m_enTimeZone>=TIMEZONE_GMT_Minus5_START && m_enTimeZone<=TIMEZONE_GMT_Minus5_END)
            {
                strcat(tzPath, "/Etc/GMT-5");
                break;
            }
            else if(m_enTimeZone>=TIMEZONE_GMT_Minus4_START && m_enTimeZone<=TIMEZONE_GMT_Minus4_END)
            {
                strcat(tzPath, "/Etc/GMT-4");
                break;
            }
            else if(m_enTimeZone>=TIMEZONE_GMT_Minus3_START && m_enTimeZone<=TIMEZONE_GMT_Minus3_END)
            {
                strcat(tzPath, "/Etc/GMT-3");
                break;
            }
            else if(m_enTimeZone>=TIMEZONE_GMT_Minus2_START && m_enTimeZone<=TIMEZONE_GMT_Minus2_END)
            {
                strcat(tzPath, "/Etc/GMT-2");
                break;
            }
            else
            {
                strcat(tzPath, "/Etc/GMT");
                break;
            }
    }

}

EN_TIMEZONE MSrv_Timer::GetTimeZone()
{
    return m_enTimeZone;
}
void MSrv_Timer::SetTimeZone(EN_TIMEZONE entimezone, BOOL bflag)
{

    if(m_enLinuxTimeSource == E_LINUX_TIME_FROM_NTP)
    {
        /* For TVOS, Android will set the system time, so we don't do Set Clock operation in SetTimeZone. */

        m_enTimeZone = entimezone;
        m_s32OffsetTime = GetTimeZoneOffsetTime(GetTimeZone2SpeciedTimeOffset(m_enTimeZone));
    }
    else
    {
        int ret;
        struct timeval tv;
        struct timezone tz;
        memset(&tv, 0 , sizeof(struct timeval));
        memset(&tz, 0 , sizeof(struct timezone));

        U32 u32RtcSec = mapi_interface::Get_mapi_system()->RTCGetCLK();

#if (ATSC_SYSTEM_ENABLE == 1)
        //ATSC system time need to late than 1980/01/06
        if(u32RtcSec < ATSC_TIME_DIFFERENCE)
            u32RtcSec = ATSC_TIME_DIFFERENCE;
#endif

        m_enTimeZone = entimezone;
        if(bflag == TRUE)
        {
            m_s32OffsetTime = GetTimeZoneOffsetTime(GetTimeZone2SpeciedTimeOffset(m_enTimeZone));
        }
        else
        {
            MS_TIME stTimeDb;
            (MSrv_Control::GetMSrvSystemDatabase())->GetOsdTime(&stTimeDb);
            m_s32OffsetTime=stTimeDb.s32OffsetTime;
        }

        // for all minus timezone region which is earlier than in UK(GMT0),
        // we have to preserve a time shift, or the time will become minus(underflow)
        if(((S32)u32RtcSec + m_s32OffsetTime) < 0)
        {
            u32RtcSec = abs(m_s32OffsetTime);
        }

        MTIMER_IFO("%s:u32RtcSec = %u, s32OffsetTime = %d\n", __func__, u32RtcSec, m_s32OffsetTime);
        tv.tv_sec = (time_t)(u32RtcSec + m_s32OffsetTime);
        tv.tv_usec = 0;
        tz.tz_minuteswest = 0 - (m_s32OffsetTime / SECONDS_PER_MIN);
        tz.tz_dsttime = 0;  // to be implentment iff DST consideration
        ConvertSeconds2StTime((U32)(u32RtcSec + m_s32OffsetTime), &m_stClkTime);
        ret = settimeofday(&tv, &tz);
        if(ret != 0)
        {
            printf("settimeofday fail");
        }

        mapi_interface::Get_mapi_system()->RTCSetCLK(ConvertStTime2Seconds(&m_stClkTime) - m_s32OffsetTime);
    }

    mapi_interface::Get_mapi_system()->SetClockOffset(m_s32OffsetTime);
    if(bflag == TRUE)
    {
        StoreTimerDB();
    }
#if (MWB_LAUNCHER_ENABLE ==1)
    string strTimeZoneAbbreviation = GetTimeZoneAbbreviation(entimezone);
    MstarLauncherAgent::GetInstance().onTimezoneChange((const char*)strTimeZoneAbbreviation.c_str());
#endif
}

BOOL MSrv_Timer::GetTimeOffsetFromCurrentTimeZone(int *timeOffset)
{
    EN_Clock_TimeZone currentClockTImeZone;
    BOOL rv=TRUE;
    if(NULL==timeOffset)
    {
        rv=FALSE;
    }
    else
    {
        currentClockTImeZone = GetTimeZone2SpeciedTimeOffset(m_enTimeZone);
        *timeOffset = GetTimeZoneOffsetTime(currentClockTImeZone);
         rv=TRUE;
    }

    return rv;
}
BOOL MSrv_Timer::GetTimeFormat()
{
    return m_bIs12Hour;
}

void MSrv_Timer::SetTimeFormat(BOOL bflag)
{
    m_bIs12Hour = bflag;
    StoreTimerDB();
}

U32 MSrv_Timer::ConvertGPSTime2UTC(U32 u32GPSSec)
{
    U16 u16Year = UTC_BASE_YEAR;
    U32 u32TotalSeconds = 0;

    while(u16Year < GPS_BASE_YEAR)
    u32TotalSeconds += GetDaysOfThisYear(u16Year++) * SECONDS_PER_DAY;

    u32GPSSec += u32TotalSeconds + (GPS_BASE_DAY-1) * SECONDS_PER_DAY;  //1980.1.6
    return u32GPSSec;

}
U32 MSrv_Timer::ConvertUTCTime2GPS(U32 u32UTCSec)
{
    U16 u16Year = UTC_BASE_YEAR;
    U32 u32TotalSeconds = 0;

    while(u16Year < GPS_BASE_YEAR)
        u32TotalSeconds += GetDaysOfThisYear(u16Year++) * SECONDS_PER_DAY;

    u32UTCSec -= u32TotalSeconds + (GPS_BASE_DAY-1)  * SECONDS_PER_DAY;
    return u32UTCSec;
}
#if (ATSC_SYSTEM_ENABLE == 1)
void MSrv_Timer::SetStreamTime(U32 u32StreamTime)
{
    m_u32StreamTime = u32StreamTime;
}
U32 MSrv_Timer::GetStreamTime(void)
{
    return m_u32StreamTime;
}
#endif

/* the base time starts from DEFAULT_YEAR/DEFAULT_MONTH/DEFAULT_DAY DEFAULT_HOUR:DEFAULT_MIN:DEFAULT_SEC*/
U32 MSrv_Timer::ConvertStTime2Seconds(const ST_time * const stTime)
{
    U32 u32TotalSeconds;
    U16 u16YearCalc;

    u32TotalSeconds = 0;

    /* sec */
    u32TotalSeconds += stTime->u8Second;

    /* min */
    u32TotalSeconds += stTime->u8Minute * SECONDS_PER_MIN;

    /* hour */
    u32TotalSeconds += stTime->u8Hour * SECONDS_PER_HOUR;

    /* day */
    u32TotalSeconds += (stTime->u8Day - 1) * SECONDS_PER_DAY;

    /* month */
    u32TotalSeconds += SolarDays[GetLeap(stTime->u16Year) * 14 + stTime->u8Month - 1] * SECONDS_PER_DAY;

    /* year */
    u16YearCalc = stTime->u16Year;
    while(u16YearCalc > DEFAULT_BASE_YEAR)
    {
        u16YearCalc--;
        u32TotalSeconds += GetDaysOfThisYear(u16YearCalc) * SECONDS_PER_DAY;
    }

    return u32TotalSeconds;
}

/* the base time starts from DEFAULT_YEAR/DEFAULT_MONTH/DEFAULT_DAY DEFAULT_HOUR:DEFAULT_MIN:DEFAULT_SEC*/
void MSrv_Timer::ConvertSeconds2StTime(U32 u32SystemTime, ST_time* stTime) const
{
    U16 u16TotalDays, u16Days;

    /* set to base date */
    SetToDefaultSystemTime(stTime);

    /* u32SystemTime = total accumulative seconds from base date */
    if(u32SystemTime > 0)
    {
        /* sec */
        stTime->u8Second = u32SystemTime % SECONDS_PER_MIN;
        u32SystemTime -= stTime->u8Second;

        /* min */
        stTime->u8Minute = (u32SystemTime / SECONDS_PER_MIN) % MINS_PER_HOUR;
        u32SystemTime -= stTime->u8Minute * SECONDS_PER_MIN;

        /* hour */
        stTime->u8Hour = (u32SystemTime / SECONDS_PER_HOUR) % HOURS_PER_DAY;
        u32SystemTime -= stTime->u8Hour * SECONDS_PER_HOUR;

        /* days */
        u16TotalDays = u32SystemTime / SECONDS_PER_DAY;

        /* year */
        u16Days = GetDaysOfThisYear(stTime->u16Year);
        while(u16TotalDays >= u16Days)
        {
            u16TotalDays -= u16Days;
            stTime->u16Year++;
            u16Days = GetDaysOfThisYear(stTime->u16Year);
        }
        /* month */
        u16Days = GetDaysOfThisMonth(stTime->u16Year, stTime->u8Month);
        while(u16TotalDays >= u16Days)
        {
            u16TotalDays -= u16Days;
            stTime->u8Month++;
            u16Days = GetDaysOfThisMonth(stTime->u16Year, stTime->u8Month);
        }

        /* day */
        stTime->u8Day += (U8) u16TotalDays;
    }
}

void MSrv_Timer::SetToDefaultSystemTime(ST_time *pstTime) const
{
    pstTime->u16Year = DEFAULT_BASE_YEAR;
    pstTime->u8Month = DEFAULT_MONTH;
    pstTime->u8Day = DEFAULT_DAY;
    pstTime->u8Hour = DEFAULT_HOUR;
    pstTime->u8Minute = DEFAULT_MIN;
    pstTime->u8Second = DEFAULT_SEC;
}

U16 MSrv_Timer::GetDaysOfThisYear(U16 u16year) const
{
    return (GetLeap(u16year) != 0) ? 366 : 365;
}

U8 MSrv_Timer::GetLeap(U16 u16year) const
{
    if((u16year % 400) == 0)
    {
        return 1;
    }
    else if((u16year % 100) == 0)
    {
        return 0;
    }
    else if((u16year % 4) == 0)
    {
        return 1;
    }

    return 0;
}

U8 MSrv_Timer::GetDaysOfThisMonth(U16 u16Year, U8 u8Month) const
{
    if((u8Month >= 1) && (u8Month <= 12))
    {
        return ((GetLeap(u16Year) && (u8Month == 2)) ? 29 : SolarCal[u8Month - 1]);
    }
    else
    {
        return 0;
    }
}

// Get day of week
U8 MSrv_Timer::GetDayOfWeek(U16 u16Year, U8 u8Month, U8 u8Day)
{
    U8 i;
    U16 u16days = 0;
    U32 u32sum;

    for(i = 1; i <= (u8Month - 1); i++)
    {
        u16days += SolarCal[i - 1];
    }

    if(GetLeap(u16Year) && (u8Month > 2))
        u16days += 1;

    u16days += u8Day;

    u32sum = u16Year - 1 + ((u16Year - 1) / 4) - ((u16Year - 1) / 100) + ((u16Year - 1) / 400) + u16days;

    return (U8)(u32sum % 7);
}

/* Return Clock time zone for specified Time zone*/
EN_Clock_TimeZone MSrv_Timer::GetTimeZone2SpeciedTimeOffset(EN_TIMEZONE enTZ)
{
#if 0//(ISDB_SYSTEM_ENABLE == 1)
    if((enTZ >= TIMEZONE_GMT_Minus5_START) && (enTZ <= TIMEZONE_GMT_Minus5_END))
        return EN_Clock_TimeZone_20;
    else if((enTZ >= TIMEZONE_GMT_Minus4_START) && (enTZ <= TIMEZONE_GMT_Minus4_END))
        return EN_Clock_TimeZone_22;
    else if((enTZ >= TIMEZONE_GMT_Minus3_START) && (enTZ <= TIMEZONE_GMT_Minus3_END))
        return EN_Clock_TimeZone_24;
    else if((enTZ >= TIMEZONE_GMT_Minus2_START) && (enTZ <= TIMEZONE_GMT_Minus2_END))
        return EN_Clock_TimeZone_26;
    else
    {
        return EN_Clock_TimeZone_24;
    }
#else

    if((enTZ >= TIMEZONE_GMT_0_START) && (enTZ <= TIMEZONE_GMT_0_END))
    {
        return EN_Clock_TimeZone_24;
    }
    else if((enTZ >= TIMEZONE_GMT_1_START) && (enTZ <= TIMEZONE_GMT_1_END))
    {
        return EN_Clock_TimeZone_26;
    }
    else if((enTZ >= TIMEZONE_GMT_2_START) && (enTZ <= TIMEZONE_GMT_2_END))
    {
        return EN_Clock_TimeZone_28;
    }
    else if((enTZ >= TIMEZONE_GMT_3_START) && (enTZ <= TIMEZONE_GMT_3_END))
    {
        return EN_Clock_TimeZone_30;
    }
    else if((enTZ >= TIMEZONE_GMT_3Point5_START) && (enTZ <= TIMEZONE_GMT_3Point5_END))
    {
        return EN_Clock_TimeZone_31;
    }
    else if((enTZ >= TIMEZONE_GMT_4_START) && (enTZ <= TIMEZONE_GMT_4_END))
    {
        return EN_Clock_TimeZone_32;
    }
    else if((enTZ >= TIMEZONE_GMT_4Point5_START) && (enTZ <= TIMEZONE_GMT_4Point5_END))
    {
        return EN_Clock_TimeZone_33;
    }
    else if((enTZ >= TIMEZONE_GMT_5_START) && (enTZ <= TIMEZONE_GMT_5_END))
    {
        return EN_Clock_TimeZone_34;
    }
    else if((enTZ >= TIMEZONE_GMT_5Point5_START) && (enTZ <= TIMEZONE_GMT_5Point5_END))
    {
        return EN_Clock_TimeZone_35;
    }
    else if((enTZ >= TIMEZONE_GMT_5Point45_START) && (enTZ <= TIMEZONE_GMT_5Point45_END))
    {
        return EN_Clock_TimeZone_51_Point45;
    }
    else if((enTZ >= TIMEZONE_GMT_6_START) && (enTZ <= TIMEZONE_GMT_6_END))
    {
        return EN_Clock_TimeZone_36;
    }
    else if((enTZ >= TIMEZONE_GMT_6Point5_START) && (enTZ <= TIMEZONE_GMT_6Point5_END))
    {
        return EN_Clock_TimeZone_37;
    }
    else if((enTZ >= TIMEZONE_GMT_7_START) && (enTZ <= TIMEZONE_GMT_7_END))
    {
        return EN_Clock_TimeZone_38;
    }
    else if((enTZ >= TIMEZONE_GMT_8_START) && (enTZ <= TIMEZONE_GMT_8_END))
    {
        return EN_Clock_TimeZone_40;
    }
    else if((enTZ >= TIMEZONE_GMT_8Point45_START) && (enTZ <= TIMEZONE_GMT_8Point45_END))
    {
        return EN_Clock_TimeZone_52_Point45;
    }
    else if((enTZ >= TIMEZONE_GMT_9_START) && (enTZ <= TIMEZONE_GMT_9_END))
    {
        return EN_Clock_TimeZone_42;
    }
    else if((enTZ >= TIMEZONE_GMT_9Point5_START) && (enTZ <= TIMEZONE_GMT_9Point5_END))
    {
        return EN_Clock_TimeZone_43;
    }
    else if((enTZ >= TIMEZONE_GMT_10_START) && (enTZ <= TIMEZONE_GMT_10_END))
    {
        return EN_Clock_TimeZone_44;
    }
    else if((enTZ >= TIMEZONE_GMT_10Point5_START) && (enTZ <= TIMEZONE_GMT_10Point5_END))
    {
        return EN_Clock_TimeZone_45;
    }
    else if((enTZ >= TIMEZONE_GMT_11_START) && (enTZ <= TIMEZONE_GMT_11_END))
    {
        return EN_Clock_TimeZone_46;
    }
    else if((enTZ >= TIMEZONE_GMT_12_START) && (enTZ <= TIMEZONE_GMT_12_END))
    {
        return EN_Clock_TimeZone_48;
    }
    else if((enTZ >= TIMEZONE_GMT_12Point45_START) && (enTZ <= TIMEZONE_GMT_12Point45_END))
    {
        return EN_Clock_TimeZone_53_Point45;
    }
    else if((enTZ >= TIMEZONE_GMT_13_START) && (enTZ <= TIMEZONE_GMT_13_END))
    {
        return EN_Clock_TimeZone_50;
    }
    /* North America Time Zone*/
    else if((enTZ >= TIMEZONE_GMT_Minus11_START) && (enTZ <= TIMEZONE_GMT_Minus11_END))
    {
        return EN_Clock_TimeZone_2;
    }
    else if((enTZ >= TIMEZONE_GMT_Minus10_START) && (enTZ <= TIMEZONE_GMT_Minus10_END))
    {
        return EN_Clock_TimeZone_4;
    }
    else if((enTZ >= TIMEZONE_GMT_Minus9_START) && (enTZ <= TIMEZONE_GMT_Minus9_END))
    {
        return EN_Clock_TimeZone_6;
    }
    else if((enTZ >= TIMEZONE_GMT_Minus8_START) && (enTZ <= TIMEZONE_GMT_Minus8_END))
    {
        return EN_Clock_TimeZone_8;
    }
    else if((enTZ >= TIMEZONE_GMT_Minus7_START) && (enTZ <= TIMEZONE_GMT_Minus7_END))
    {
        return EN_Clock_TimeZone_10;
    }
    else if((enTZ >= TIMEZONE_GMT_Minus6_START) && (enTZ <= TIMEZONE_GMT_Minus6_END))
    {
        return EN_Clock_TimeZone_12;
    }
    else if((enTZ >= TIMEZONE_GMT_Minus5_START) && (enTZ <= TIMEZONE_GMT_Minus5_END))
    {
        return EN_Clock_TimeZone_14;
    }
    else if((enTZ >= TIMEZONE_GMT_Minus4_5_START) && (enTZ <= TIMEZONE_GMT_Minus4_5_END))
    {
        return EN_Clock_TimeZone_15;
    }
    else if((enTZ >= TIMEZONE_GMT_Minus4_START) && (enTZ <= TIMEZONE_GMT_Minus4_END))
    {
        return EN_Clock_TimeZone_16;
    }
    else if((enTZ >= TIMEZONE_GMT_Minus3_5_START) && (enTZ <= TIMEZONE_GMT_Minus3_5_END))
    {
        return EN_Clock_TimeZone_17;
    }
    else if((enTZ >= TIMEZONE_GMT_Minus3_START) && (enTZ <= TIMEZONE_GMT_Minus3_END))
    {
        return EN_Clock_TimeZone_18;
    }
    else if((enTZ >= TIMEZONE_GMT_Minus2_5_START) && (enTZ <= TIMEZONE_GMT_Minus2_5_END))
    {
        return EN_Clock_TimeZone_19;
    }
    else if((enTZ >= TIMEZONE_GMT_Minus2_START) && (enTZ <= TIMEZONE_GMT_Minus2_END))
    {
        return EN_Clock_TimeZone_20;
    }
    else if((enTZ >= TIMEZONE_GMT_Minus1_START) && (enTZ <= TIMEZONE_GMT_Minus1_END))
    {
        return EN_Clock_TimeZone_22;
    }
    else
    {
        ASSERT(0);
    }
#endif
}

/* Return value in seconds */
S32 MSrv_Timer::GetTimeZoneOffsetTime(EN_Clock_TimeZone u8TimeZone)
{
    S32 s32TempTime = 0;
    switch(u8TimeZone)
    {
        case EN_Clock_TimeZone_51_Point45:
        {
            s32TempTime = TIMEZONE_5Point45;
            break;
        }
        case EN_Clock_TimeZone_52_Point45:
        {
            s32TempTime = TIMEZONE_8Point45;
            break;
        }
        case EN_Clock_TimeZone_53_Point45:
        {
            s32TempTime = TIMEZONE_12Point45;
            break;
        }
        default:
        {
            s32TempTime = (u8TimeZone - (S32)EN_Clock_TimeZone_24) * SECONDS_PER_HALF_HOUR;
            break;
        }
    }

    if(m_bIsDaylightsaving)
        s32TempTime = s32TempTime + SECONDS_PER_HOUR;

    return s32TempTime;
}

void MSrv_Timer::Convertlocaltime2StTime(struct tm *ti, ST_time *ptime)
{
    ptime->u16Year = ti->tm_year + TM_YEAR_BASE;
    ptime->u8Month = ti->tm_mon + 1;
    ptime->u8Day = ti->tm_mday;
    ptime->u8Hour = ti->tm_hour;
    ptime->u8Minute = ti->tm_min;
    ptime->u8Second = ti->tm_sec;
    MTIMER_DBG("tm_year = %d, tm_mon = %d, tm_mday = %d, tm_hour = %d,tm_min= %d,  tm_sec = %d\n", ti->tm_year + TM_YEAR_BASE, ti->tm_mon + 1, ti->tm_mday,  ti->tm_hour, ti->tm_min, ti->tm_sec);
}
U32 MSrv_Timer::Convertlocaltime2Second(struct tm * ti)
{
    if(ti!=NULL)
    {
        ST_time st_time;
        memset(&st_time,0,sizeof(ST_time));
        Convertlocaltime2StTime(ti,&st_time);
        return ConvertStTime2Seconds(&st_time);
    }
    return 0;
}
void MSrv_Timer::ConvertStTime2Localtime(ST_time *ptime, struct tm *ti)
{
    ti->tm_year = ptime->u16Year - TM_YEAR_BASE;
    ti->tm_mon = ptime->u8Month - 1;
    ti->tm_mday = ptime->u8Day;
    ti->tm_hour = ptime->u8Hour;
    ti->tm_min = ptime->u8Minute;
    ti->tm_sec = ptime->u8Second;
    ti->tm_wday = GetDayOfWeek(ptime->u16Year, ptime->u8Month, ptime->u8Day);
}

void MSrv_Timer::ConvertStTime2MJDUTC(struct tm *ptm, MAPI_U8 *pau8TDTData)
{
    ST_time stTime;
    double L, p1, p2, p3, julian;
    MAPI_U16 mjd;
    //Array should be allocated outside.
    if(!pau8TDTData)
    {
        return;
    }

    stTime.u8Day = (u8)ptm->tm_mday;
    stTime.u8Month = (u8)ptm->tm_mon + 1;
    stTime.u16Year = (u16)ptm->tm_year + TM_YEAR_BASE;
    stTime.u8Hour = (u8)ptm->tm_hour;
    stTime.u8Minute = (u8)ptm->tm_min;
    stTime.u8Second = (u8)ptm->tm_sec;

    // In leap years, -1 for Jan, Feb, else 0
    L = ceil(((double)stTime.u8Month - 14.0) / 12.0);
    p1 = (double)stTime.u8Day - 32075.0 + floor(1461.0 * ((double)stTime.u16Year + 4800.0 + L) / 4.0);

    p2 =  floor(367.0 * ((double)stTime.u8Month - 2.0 - L * 12.0) / 12.0);
    p3 = 3.0 *  floor(floor(((double)stTime.u16Year + 4900.0 + L) / 100.0) / 4.0);

    julian = p1 + p2 - p3 - 0.5;
    mjd = (MAPI_U16)(julian - 2400000.5);

    pau8TDTData[0] = HIGHBYTE(mjd);
    pau8TDTData[1] = LOWBYTE(mjd);
    pau8TDTData[2] = DEC2BCD(stTime.u8Hour);
    pau8TDTData[3] = DEC2BCD(stTime.u8Minute);
    pau8TDTData[4] = DEC2BCD(stTime.u8Second);
}

MAPI_INPUT_SOURCE_TYPE MSrv_Timer::GetTVSrc(EN_TIME_OnTimer_Source etvsrc)
{
    switch(etvsrc)
    {
        case EN_Time_OnTimer_Source_DTV:
            return MAPI_INPUT_SOURCE_DTV;
        case EN_Time_OnTimer_Source_ATV:
            return MAPI_INPUT_SOURCE_ATV;
        case EN_Time_OnTimer_Source_SCART:
            return MAPI_INPUT_SOURCE_SCART;
        case EN_Time_OnTimer_Source_SCART2:
            return MAPI_INPUT_SOURCE_SCART2;
        case EN_Time_OnTimer_Source_COMPONENT:
            return MAPI_INPUT_SOURCE_YPBPR;
        case EN_Time_OnTimer_Source_COMPONENT2:
            return MAPI_INPUT_SOURCE_YPBPR2;
        case EN_Time_OnTimer_Source_HDMI:
            return MAPI_INPUT_SOURCE_HDMI;
        case EN_Time_OnTimer_Source_HDMI2:
            return MAPI_INPUT_SOURCE_HDMI2;
        case EN_Time_OnTimer_Source_HDMI3:
            return MAPI_INPUT_SOURCE_HDMI3;
        case EN_Time_OnTimer_Source_HDMI4:
            return MAPI_INPUT_SOURCE_HDMI4;
        case EN_Time_OnTimer_Source_AV:
            return MAPI_INPUT_SOURCE_CVBS;
        case EN_Time_OnTimer_Source_AV2:
            return MAPI_INPUT_SOURCE_CVBS2;
        case EN_Time_OnTimer_Source_AV3:
            return MAPI_INPUT_SOURCE_CVBS3;
        case EN_Time_OnTimer_Source_SVIDEO:
            return MAPI_INPUT_SOURCE_SVIDEO;
        case EN_Time_OnTimer_Source_SVIDEO2:
            return MAPI_INPUT_SOURCE_SVIDEO2;
        case EN_Time_OnTimer_Source_RGB:
            return MAPI_INPUT_SOURCE_VGA;
        case EN_Time_OnTimer_Source_RGB2:
            return MAPI_INPUT_SOURCE_VGA2;
        case EN_Time_OnTimer_Source_RGB3:
            return MAPI_INPUT_SOURCE_VGA3;
        case EN_Time_OnTimer_Source_MPLAYER:
        case EN_Time_OnTimer_Source_DLNA:
        case EN_Time_OnTimer_Source_RADIO:   // do not find respect item in OSD_DB , FIXME
        default:
            return MAPI_INPUT_SOURCE_ATV;
    }
}

void MSrv_Timer::GetNextNDayCLKTime(struct tm &timeinfo, U8 u8Nday)
{
    U32 u32Second;
    ST_time stTime;
    u32Second = mapi_interface::Get_mapi_system()->RTCGetCLK();
    u32Second += u8Nday * SECONDS_PER_DAY;
    u32Second += mapi_interface::Get_mapi_system()->GetClockOffsetByUtcTime(u32Second);
    ConvertSeconds2StTime(u32Second, &stTime);
    ConvertStTime2Localtime(&stTime, &timeinfo);
}

U32 MSrv_Timer::GetNextNDayCLKTimeInSec(U8 u8Nday)
{
    U32 u32Second;

    u32Second = mapi_interface::Get_mapi_system()->RTCGetCLK();
    u32Second += u8Nday * SECONDS_PER_DAY;
    u32Second += mapi_interface::Get_mapi_system()->GetClockOffsetByUtcTime(u32Second);
    return u32Second;
}

U32 MSrv_Timer::GetNextNDayCLKUtcTimeInSec(U8 u8Nday)
{
    U32 u32Second;

    u32Second = mapi_interface::Get_mapi_system()->RTCGetCLK();
    u32Second += u8Nday * SECONDS_PER_DAY;
    return u32Second;
}

S32 MSrv_Timer::GetClockOffsetByUtcTime(const U32 u32UtcTime)
{
    return mapi_interface::Get_mapi_system()->GetClockOffsetByUtcTime(u32UtcTime);
}

MSrv_Timer::ST_time MSrv_Timer::GetstClkTime(void)
{
    return(m_stClkTime);
}
MSrv_Timer::ST_time MSrv_Timer::GetstOnTime(void)
{
    return(m_stOnTime);
}
MSrv_Timer::ST_time MSrv_Timer::GetstOffTime(void)
{
    return(m_stOffTime);
}

void MSrv_Timer::AutoSleepModeSignalSet(BOOL bHaveSignal)
{
    m_bSignal = bHaveSignal;

    if(m_bAutoSleepFlag == TRUE)
    {
        if((m_bSignal == FALSE) && (m_bResetTimer == TRUE))
        {
            MAPI_INPUT_SOURCE_TYPE enCurrentInputType;
            enCurrentInputType = MSrv_Control::GetInstance()->GetCurrentInputSource();

            m_u32AutoSleepTimebase = mapi_time_utility::GetTime0() / SECOND_TO_MS;
            if(IsSrcVga(enCurrentInputType))
                m_u32AutoSleepTimeDur = 120;  //VGA default auto sleep timer 2 mins
            else
                m_u32AutoSleepTimeDur = SLEEP_TIMER_TIMEBASE / SECOND_TO_MS;  //default auto sleep timer 10 mins
            //m_u32AutoSleepTimeDur = 60;
            m_u32AutoSleepTimesecs = m_u32AutoSleepTimebase + m_u32AutoSleepTimeDur;
            m_u32AutoSleepRemain = m_u32AutoSleepTimesecs - m_u32AutoSleepTimebase;
            MTIMER_IFO("m_u32AutoSleepTimebase =%u (secs), m_u32AutoSleepTimeDur = %u (secs), m_u32AutoSleepRemain = %u\n", m_u32AutoSleepTimebase, m_u32AutoSleepTimeDur, m_u32AutoSleepRemain);

            /*Reset counting down window flag*/
            m_bClockAlarmFlag = FALSE;
            m_bResetTimer = FALSE;
            int intPTHChk;
            intPTHChk = PTH_RET_CHK(pthread_cond_signal(&m_cond_offtimer));
            ASSERT(intPTHChk == 0);
        }
        else if((m_bSignal == TRUE) && (m_bResetTimer == FALSE))
        {
            MTIMER_DBG("%s:AutoSleep Timer Postevent(EV_SIGNAL_LOCK) to IdleAPP cuz Signal recover!\n", __func__);
            m_u32AutoSleepTimebase = mapi_time_utility::GetTime0() / SECOND_TO_MS;
            m_u32AutoSleepTimeDur = SLEEP_TIMER_TIMEBASE / SECOND_TO_MS;  //default auto sleep timer 10 mins
            //m_u32AutoSleepTimeDur = 60;
            m_u32AutoSleepTimesecs = m_u32AutoSleepTimebase + m_u32AutoSleepTimeDur;
            m_u32AutoSleepRemain = m_u32AutoSleepTimesecs - m_u32AutoSleepTimebase;
            MTIMER_IFO("m_u32AutoSleepTimebase =%u (secs), m_u32AutoSleepTimeDur = %u (secs), m_u32AutoSleepRemain = %u\n", m_u32AutoSleepTimebase, m_u32AutoSleepTimeDur, m_u32AutoSleepRemain);

            PostEvent(NULL, EV_SIGNAL_LOCK, 0, 0);
        }
    }
    else
    {
        m_bResetTimer = TRUE;
        m_u32AutoSleepTimebase = 0;
        m_u32AutoSleepTimeDur = 0;
        m_u32AutoSleepTimesecs = SEC_THREADING_PENDING;
        m_u32AutoSleepRemain = SEC_THREADING_PENDING;
    }
    //MTIMER_DBG("%s:m_bAutoSleepFlag = %d , m_bSignal = %d, m_bResetTimer = %d\n",__func__, m_bAutoSleepFlag, m_bSignal, m_bResetTimer );
}

BOOL MSrv_Timer::GetEPGTimerEventByIndex(ST_EPG_EVENT_TIMER_INFO &stTimerInfo, U32 u32Index)
{
    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);

    ST_EPG_EVENT_TIMER_INFO stTimerDB[EPG_TIMER_MAX_NUM];

    /* load EPG timer DB */
    mapi_scope_lock(scopeLock, &m_EpgTimerMutex);
    pSysDB->GetEpgTimer(stTimerDB);

    for(U32 i = 0; i < EPG_TIMER_MAX_NUM; i++)
    {
        if(stTimerDB[i].enTimerType == EPG_EVENT_NONE)
        {
            break;
        }

        if(i == u32Index)
        {
            stTimerInfo = stTimerDB[i];
            _RemovePaddingsFromEpgEventTimerInfo(stTimerInfo);
            MTIMER_IFO("GetEPGTimerEventByIndex idx[%d] INFO:type[%d],mode[%d],service:[%d] \n", u32Index, stTimerInfo.enTimerType, stTimerInfo.enRepeatMode, stTimerInfo.u16ServiceNumber);
            return TRUE;
        }
    }

    MTIMER_IFO("GetEPGTimerEventByIndex idx[%d] \n", u32Index);
    return FALSE;

}

BOOL MSrv_Timer::GetEPGTimerIndexEventById(U8 &u8Index, const U32 u32TimerId)
{
    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);

    ST_EPG_EVENT_TIMER_INFO stTimerDB[EPG_TIMER_MAX_NUM];

    mapi_scope_lock(scopeLock, &m_EpgTimerMutex);
    pSysDB->GetEpgTimer(stTimerDB);

    for(U32 i = 0; i < EPG_TIMER_MAX_NUM; i++)
    {
        if(stTimerDB[i].enTimerType == EPG_EVENT_NONE)
        {
            break;
        }

        if(u32TimerId == stTimerDB[i].u32TimerId)
        {
            u8Index = i;
            return TRUE;
        }
    }

    MTIMER_IFO("GetEPGTimerEventById u32TimerId[%d] \n", u32TimerId);
    return FALSE;
}



BOOL MSrv_Timer::GetEPGTimerEventById(ST_EPG_EVENT_TIMER_INFO &stTimerInfo, const U32 u32TimerId)
{
    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);

    ST_EPG_EVENT_TIMER_INFO stTimerDB[EPG_TIMER_MAX_NUM];

    /* load EPG timer DB */
    mapi_scope_lock(scopeLock, &m_EpgTimerMutex);
    pSysDB->GetEpgTimer(stTimerDB);

    for(U32 i = 0; i < EPG_TIMER_MAX_NUM; i++)
    {
        if(stTimerDB[i].enTimerType == EPG_EVENT_NONE)
        {
            break;
        }

        if(u32TimerId == stTimerDB[i].u32TimerId)
        {
            stTimerInfo = stTimerDB[i];
            _RemovePaddingsFromEpgEventTimerInfo(stTimerInfo);
            return TRUE;
        }
    }

    MTIMER_IFO("GetEPGTimerEventById u32TimerId[%d] \n", u32TimerId);
    return FALSE;
}

U16 MSrv_Timer::GetEPGTimerEventCount(void)
{
    U16 LoopIdx = 0;

    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);

    ST_EPG_EVENT_TIMER_INFO *pEventList = NULL;
    U32 listSize = 0;

    mapi_scope_lock(scopeLock, &m_EpgTimerMutex);
    pSysDB->UseEpgTimerList(&pEventList, &listSize);

    for(LoopIdx = 0; LoopIdx < EPG_TIMER_MAX_NUM ; LoopIdx++)
    {
        if((pEventList[LoopIdx].enTimerType == EPG_EVENT_NONE)
                || (pEventList[LoopIdx].enTimerType >= EPG_EVENT_MAX))
        {
            break;
        }
    }

    return LoopIdx;
}

void MSrv_Timer::ResetStopppedTimerListItem(U16 ItemIndex)
{
    U16 itemTotal = GetEPGTimerEventCount();
    if((ItemIndex >= itemTotal) || (itemTotal == 0))
    {
        return;
    }

    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);

    ST_EPG_EVENT_TIMER_INFO *pEventList = NULL;
    U32 listSize = 0;

    mapi_scope_lock(scopeLock, &m_EpgTimerMutex);
    pSysDB->UseEpgTimerList(&pEventList, &listSize);


#if 0   //for recorder queue
    if(pEventList[ItemIndex].enTimerType == EPG_EVENT_RECORDER)
    {
        for(U16 idx = 0; idx < EPGTIMER_REC_QUEUE_NUMBER; idx++)
        {
            if(m_u8EpgTimerRecQueue[i].u8TimerIndex == ItemIndex) //do not delete recording item
            {
                return;
            }
        }
    }
#endif

    if(_IsRepeatDays(pEventList[ItemIndex].enRepeatMode))
    {
        //reset to next
        vector<U32> vu32Time;
        U32 u32CurTime = mapi_interface::Get_mapi_system()->RTCGetCLK();
        U8 u8Repeat = pEventList[ItemIndex].enRepeatMode;
        _GetNextWeekStartTime(pEventList[ItemIndex].u32StartTime, u8Repeat, vu32Time);
        if(vu32Time.size() == 0)
        {
            return;
        }
        for(U8 idx = 0; idx < vu32Time.size(); ++idx)
        {
            if(vu32Time[idx] > u32CurTime)
            {
                pEventList[ItemIndex].u32StartTime = vu32Time[idx];
                return;
            }
        }
        // whole week in vu32Time is past, use the last one (although past)
        pEventList[ItemIndex].u32StartTime = vu32Time.back();
        return;
    }

    switch(pEventList[ItemIndex].enRepeatMode)
    {
        case EPG_REPEAT_AUTO:
            break;
        case EPG_REPEAT_ONCE:
        case EPG_EVENT_RECORDER_EVENT_ID:
        {
            //erase
            U16 curLastValid = itemTotal - 1;
            pEventList[ItemIndex] = pEventList[curLastValid];
            memset(&pEventList[curLastValid], 0x00, sizeof(ST_EPG_EVENT_TIMER_INFO));
            pEventList[curLastValid].enTimerType = EPG_EVENT_NONE;

            //TIMER_DBG(("Delete Item=%d\n",ItemIndex));
#if 0   //for recorder queue
            {
                U8 idx;
                for(idx = 0; idx < EPGTIMER_REC_QUEUE_NUMBER; idx++)
                {
                    if(m_u8EpgTimerRecQueue[idx].u8TimerIndex == ItemIndex)
                    {
                        m_u8EpgTimerRecQueue[idx].u8TimerIndex = EPGTIMER_INDEX_NULL;//item deleted
                        TIMER_DBG(("m_u8EpgTimerRecQueue[%d].u8TimerIndex = %d\n", idx, m_u8EpgTimerRecQueue[idx].u8TimerIndex));
                    }
                    else if(m_u8EpgTimerRecQueue[idx].u8TimerIndex == curLastValid)
                    {
                        m_u8EpgTimerRecQueue[idx].u8TimerIndex = ItemIndex;
                        TIMER_DBG(("m_u8EpgTimerRecQueue[%d].u8TimerIndex = %d\n", idx, m_u8EpgTimerRecQueue[idx].u8TimerIndex));
                    }
                }
            }
#endif

#if 0   //for PVR
            if(m_u8EpgNextStartUpIdx == ItemIndex)
            {
                m_u8EpgNextStartUpIdx = EPGTIMER_INDEX_NULL; //item deleted
                TIMER_DBG(("m_u8EpgNextStartUpIdx=%d\n", m_u8EpgNextStartUpIdx));
            }
            else if(m_u8EpgNextStartUpIdx == curLastValid)
            {
                m_u8EpgNextStartUpIdx = ItemIndex;
                TIMER_DBG(("m_u8EpgNextStartUpIdx=%d\n", m_u8EpgNextStartUpIdx));
            }


            if(m_u8EpgStartingIdx == ItemIndex)
            {
                m_u8EpgStartingIdx = EPGTIMER_INDEX_NULL; //item deleted
                TIMER_DBG(("m_u8EpgStartingIdx=%d\n", EPGTIMER_INDEX_NULL));
            }
            else if(m_u8EpgStartingIdx == curLastValid)
            {
                m_u8EpgStartingIdx = ItemIndex;
                TIMER_DBG(("m_u8EpgStartingIdx=%d\n", m_u8EpgStartingIdx));
            }
#endif
        }
        break;
        case EPG_REPEAT_DAILY:
            //fall through
        case EPG_REPEAT_WEEKLY:
        {
            //reset to next
            U32 u32CurTime = mapi_interface::Get_mapi_system()->RTCGetCLK();
            U32 u32TimeDiff = 0;
            if(u32CurTime > pEventList[ItemIndex].u32StartTime)
            {
                u32TimeDiff = u32CurTime - pEventList[ItemIndex].u32StartTime;
            }
            else
            {
                // If the event is not past, assume it is past and reset to next cycle.
                u32TimeDiff = 1;
            }

#if 0   //to fix ZUI
            if(u32CurTime <= pEventList[ItemIndex].u32StartTime && !MApp_ZUI_API_IsSuccessor(HWND_EPG_COUNTDOWN_PANE, MApp_ZUI_API_GetFocus()))
            {
                break;
            }
#endif

            if(EPG_REPEAT_DAILY == pEventList[ItemIndex].enRepeatMode)
            {
                U32 u32DiffStep = u32TimeDiff / SECONDS_PER_DAY;

                if((u32TimeDiff % SECONDS_PER_DAY) == 0)
                {
                    u32TimeDiff = u32DiffStep * SECONDS_PER_DAY;
                }
                else
                {
                    u32TimeDiff = (u32DiffStep + 1) * SECONDS_PER_DAY;
                }

                pEventList[ItemIndex].u32StartTime += u32TimeDiff;
            }
            else if(EPG_REPEAT_WEEKLY == pEventList[ItemIndex].enRepeatMode)
            {
                U32 u32DiffStep = u32TimeDiff / (SECONDS_PER_DAY * 7);

                if((u32TimeDiff % (SECONDS_PER_DAY * 7)) == 0)
                {
                    u32TimeDiff = u32DiffStep * (SECONDS_PER_DAY * 7);
                }
                else
                {
                    u32TimeDiff = (u32DiffStep + 1) * (SECONDS_PER_DAY * 7);
                }

                pEventList[ItemIndex].u32StartTime += u32TimeDiff;
            }
            //TIMER_DBG(("Item Reset\n"));
        }
        break;
        default:
            break;
    }
}

BOOL MSrv_Timer::CheckEPGItemOverlap(const ST_EPG_EVENT_TIMER_INFO &stEventInfoA, const ST_EPG_EVENT_TIMER_INFO &stEventInfoB)
{
    if(((stEventInfoA.enRepeatMode == EPG_REPEAT_ONCE)||(stEventInfoA.enRepeatMode == EPG_EVENT_RECORDER_EVENT_ID))
            && ((stEventInfoB.enRepeatMode == EPG_REPEAT_ONCE)||(stEventInfoB.enRepeatMode == EPG_EVENT_RECORDER_EVENT_ID)))
    {
        return CompareEPGItemOverlap(stEventInfoA.u32StartTime, stEventInfoA.u32DurationTime, \
                                     stEventInfoB.u32StartTime, stEventInfoB.u32DurationTime);
    }
    else if((stEventInfoA.enRepeatMode == EPG_REPEAT_ONCE)||(stEventInfoA.enRepeatMode == EPG_EVENT_RECORDER_EVENT_ID))
    {
        return _CheckEPGItemCycleToOnce(
            stEventInfoB.u32StartTime, stEventInfoB.u32DurationTime, stEventInfoB.enRepeatMode,
            stEventInfoA.u32StartTime, stEventInfoA.u32DurationTime, stEventInfoA.enRepeatMode);
    }
    else if((stEventInfoB.enRepeatMode == EPG_REPEAT_ONCE)||(stEventInfoB.enRepeatMode == EPG_EVENT_RECORDER_EVENT_ID))
    {
        return _CheckEPGItemCycleToOnce(
            stEventInfoA.u32StartTime, stEventInfoA.u32DurationTime, stEventInfoA.enRepeatMode,
            stEventInfoB.u32StartTime, stEventInfoB.u32DurationTime, stEventInfoB.enRepeatMode);
    }
    else
    {
        if((stEventInfoA.enRepeatMode == EPG_REPEAT_DAILY)
                || (stEventInfoB.enRepeatMode == EPG_REPEAT_DAILY))
        {
            return CheckEPGItemCycle(stEventInfoA.u32StartTime, stEventInfoA.u32DurationTime, \
                                     stEventInfoB.u32StartTime, stEventInfoB.u32DurationTime, \
                                     SECONDS_PER_DAY);
        }
        else
        {
            vector<U32> vu32A;
            vector<U32> vu32B;
            _GetNextWeekStartTime(stEventInfoA.u32StartTime, stEventInfoA.enRepeatMode, vu32A);
            _GetNextWeekStartTime(stEventInfoB.u32StartTime, stEventInfoB.enRepeatMode, vu32B);
            for(vector<U32>::iterator itrA = vu32A.begin(); itrA < vu32A.end(); ++itrA)
            {
                for(vector<U32>::iterator itrB = vu32B.begin(); itrB < vu32B.end(); ++itrB)
                {
                    if(CheckEPGItemCycle(*itrA, stEventInfoA.u32DurationTime, \
                                         *itrB, stEventInfoB.u32DurationTime, SECONDS_PER_WEEK))
                    {
                        return TRUE;
                    }
                }
            }
        }

    }
    return FALSE;
}

BOOL MSrv_Timer::CompareEPGItemOverlap(const U32& u32StartTimeA, const U32& u32DurationTimeA, const U32& u32StartTimeB, const U32& u32DurationTimeB) const
{
    if(u32StartTimeA==u32StartTimeB)
        return TRUE;
    else if(u32StartTimeA+u32DurationTimeA<=u32StartTimeB)
        return FALSE;
    else if(u32StartTimeB+u32DurationTimeB<=u32StartTimeA)
        return FALSE;
    return TRUE;
}

void MSrv_Timer::_GetNextWeekStartTime(U32 u32CurStartTime, U8 u8Repeat, vector<U32>& vu32Out) const
{
    time_t timeCurStartTime = (time_t)u32CurStartTime;
    struct tm* pTm = gmtime(&timeCurStartTime);
    ASSERT(pTm);

    U8 u8WeekDay = pTm->tm_wday;
    if(EPG_REPEAT_WEEKLY == u8Repeat)
    {
        u8Repeat = 0x01 << u8WeekDay;
    }
    else if((0 == u8Repeat) || (EPG_REPEAT_DAILY < u8Repeat))
    {
        ASSERT(0);
    }

    U32 u32RepeatDaysConcat = u8Repeat | (u8Repeat << 7);
    u32RepeatDaysConcat >>= u8WeekDay;
    u32RepeatDaysConcat >>= 1;
    for(U8 idx = 1; idx <= DAYS_PER_WEEK; ++idx)
    {
        if((u32RepeatDaysConcat & 0x01) != 0)
        {
            vu32Out.push_back(u32CurStartTime + idx * SECONDS_PER_DAY);
        }
        u32RepeatDaysConcat >>= 1;
    }
}

BOOL MSrv_Timer::_CheckEPGItemCycleToOnce(U32 u32StartA, U32 u32DurA, U8 u8RptA,
        U32 u32StartB, U32 u32DurB, U8 u8RptB) const
{
    ASSERT(_IsRepeatDays(u8RptA) || (EPG_REPEAT_WEEKLY == u8RptA));
    ASSERT((EPG_REPEAT_ONCE == u8RptB) || (EPG_EVENT_RECORDER_EVENT_ID == u8RptB));
    if(u32StartB + u32DurB < u32StartA)
    {
        return FALSE;
    }
    if(EPG_REPEAT_DAILY == u8RptA)
    {
        return CheckEPGItemCycle(u32StartA, u32DurA, u32StartB, u32DurB, SECONDS_PER_DAY);
    }
    if(EPG_REPEAT_WEEKLY == u8RptA)
    {
        return CheckEPGItemCycle(u32StartA, u32DurA, u32StartB, u32DurB, SECONDS_PER_WEEK);
    }
    vector<U32> vu32StartA;
    _GetNextWeekStartTime(u32StartA, u8RptA, vu32StartA);
    vu32StartA.insert(vu32StartA.begin(), u32StartA);
    for(vector<U32>::iterator itr = vu32StartA.begin(); itr < vu32StartA.end(); ++itr)
    {
        if(CheckEPGItemCycle(*itr, u32DurA, u32StartB, u32DurB, SECONDS_PER_WEEK))
        {
            return TRUE;
        }
    }
    return FALSE;
}

BOOL MSrv_Timer::CheckEPGItemCycle(const U32& u32StartTimeA, const U32& u32DurationTimeA, const U32& u32StartTimeB, const U32& u32DurationTimeB, U32 u32SecondsPERmode) const
{
    U32 DailyStartTimeA = 0, DailyStartTimeB = 0;
    DailyStartTimeA = u32StartTimeA % u32SecondsPERmode;
    DailyStartTimeB = u32StartTimeB % u32SecondsPERmode;

    if(((DailyStartTimeA + u32DurationTimeA) > u32SecondsPERmode)
            && ((DailyStartTimeB + u32DurationTimeB) <= DailyStartTimeA))
    {
        DailyStartTimeB += u32SecondsPERmode;
    }

    if(((DailyStartTimeB + u32DurationTimeB) > u32SecondsPERmode)
            && ((DailyStartTimeA + u32DurationTimeA) <= DailyStartTimeB))
    {
        DailyStartTimeA += u32SecondsPERmode;
    }
    return CompareEPGItemOverlap(DailyStartTimeA, u32DurationTimeA, \
                                 DailyStartTimeB, u32DurationTimeB);
}

void MSrv_Timer::DelEPGTimerByProg(U8 u8ServiceType, U16 u16ServiceNumber)
{
    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);

    ST_EPG_EVENT_TIMER_INFO *pEventList = NULL;
    U32 listSize = 0;

    mapi_scope_lock(scopeLock, &m_EpgTimerMutex);
    pSysDB->UseEpgTimerList(&pEventList, &listSize);

    U16 u16EPGTimerEventCount = GetEPGTimerEventCount();

    for(U16 i = 0 ; i < u16EPGTimerEventCount ; i++)
    {
        if((EPG_EVENT_REMIDER == pEventList[i].enTimerType)
                || (EPG_EVENT_RECORDER == pEventList[i].enTimerType))
        {
            if((u8ServiceType == pEventList[i].u8ServiceType) \
                    && (u16ServiceNumber == pEventList[i].u16ServiceNumber))
            {
                DelEPGEvent(i);

                pSysDB->UseEpgTimerList(&pEventList, &listSize);
                u16EPGTimerEventCount = GetEPGTimerEventCount();

                i = 0;
                continue;
            }
        }
    }
}

void MSrv_Timer::DelPastTimeItems(U32 u32TimeActing, BOOL bCheckEndTime)
{
    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);

    ST_EPG_EVENT_TIMER_INFO *pEventList = NULL;
    U32 listSize = 0;

    mapi_scope_lock(scopeLock, &m_EpgTimerMutex);
    pSysDB->UseEpgTimerList(&pEventList, &listSize);

    U16 u16EPGTimerEventCount = GetEPGTimerEventCount();
    if(u16EPGTimerEventCount == 0)
    {
        return;
    }

    for(; u16EPGTimerEventCount > 0; u16EPGTimerEventCount--)  //from last to first
    {
        ST_EPG_EVENT_TIMER_INFO stTimer = pEventList[u16EPGTimerEventCount - 1];
        if(
            ((stTimer.u32StartTime > u32TimeActing) && ((EPG_EVENT_CI_OP_REFRESH == stTimer.enTimerType)||(EPG_EVENT_REMIDER == stTimer.enTimerType)))
            || ((!bCheckEndTime) && (EPG_EVENT_RECORDER == stTimer.enTimerType) && ((stTimer.u32StartTime) > u32TimeActing))
            || (bCheckEndTime && (EPG_EVENT_RECORDER == stTimer.enTimerType) && ((stTimer.u32StartTime+ stTimer.u32DurationTime)> u32TimeActing))
            )

        {
            continue;
        }
        ResetStopppedTimerListItem(u16EPGTimerEventCount - 1);
    }

}
void MSrv_Timer::SortTimerList(void)
{
    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);

    ST_EPG_EVENT_TIMER_INFO *pEventList = NULL;
    U32 listSize = 0;

    mapi_scope_lock(scopeLock, &m_EpgTimerMutex);
    pSysDB->UseEpgTimerList(&pEventList, &listSize);
    U16 iItemCount = GetEPGTimerEventCount();

    for(U16 i = 0 ; i < (iItemCount - 1) ; i++)
    {
        for(U16 j = i + 1 ; j < iItemCount ; j++)
        {
            if((pEventList[i].u32StartTime > pEventList[j].u32StartTime)
                    || ((pEventList[i].u32StartTime == pEventList[j].u32StartTime) && (pEventList[i].u32DurationTime > pEventList[j].u32DurationTime)))
            {
                ST_EPG_EVENT_TIMER_INFO temp = pEventList[j];
                pEventList[j] = pEventList[i];
                pEventList[i] = temp;
            }
        }
    }
}

void MSrv_Timer::ReconfigTimerFromList(U32 u32TimeActing, BOOL bCheckEndTime)
{
    U32 timeAct = ((u32TimeActing == 0) ? mapi_interface::Get_mapi_system()->RTCGetCLK() : u32TimeActing);
    DelPastTimeItems(timeAct, bCheckEndTime);
    SortTimerList();
    UpdateEPGTimerMoniter(timeAct);

    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();

    mapi_scope_lock(scopeLock, &m_EpgTimerMutex);
    //DumpTimerList(m_EpgTimerMutex);
    pSysDB->SaveEPGTimerSetting();

}

void MSrv_Timer::CancelEPGTimerEvent(U32 u32TimeActing, BOOL bCheckEndTime)
{
    ReconfigTimerFromList(u32TimeActing, bCheckEndTime);
}

static BOOL IsBack2BackTimeGap(const ST_EPG_EVENT_TIMER_INFO& rEPGTimerEventFirst, const ST_EPG_EVENT_TIMER_INFO& rEPGTimerEventSecond)
{
        U32 u32TimeGap = (rEPGTimerEventSecond.u32StartTime - (rEPGTimerEventFirst.u32StartTime + rEPGTimerEventFirst.u32DurationTime));
        if(u32TimeGap <= (MSrv_Timer::EPGTIMER_RECORDER_LEADING_TIME_S+MSrv_Timer::EPGTIMER_COUNTDOWN_LEADING_TIME_S))
            return TRUE;
        return FALSE;
}

#if 0
EN_OVERLAP_B2B_SHIFT_TYPE MSrv_Timer::_ProcessBack2BackEvent(ST_EPG_EVENT_TIMER_INFO& rEPGTimerEventTarget,const ST_EPG_EVENT_TIMER_INFO& rEPGTimerEventCompared)
{
    U32 u32TimeGap = (MSrv_Timer::EPGTIMER_RECORDER_LEADING_TIME_S+MSrv_Timer::EPGTIMER_COUNTDOWN_LEADING_TIME_S);
    if(rEPGTimerEventTarget.u32StartTime >= rEPGTimerEventCompared.u32StartTime)
{
        if((rEPGTimerEventTarget.u32StartTime - rEPGTimerEventCompared.u32StartTime) < u32TimeGap)
    {
            return E_SHIFT_TYPE_START_PLUS;
        }
    }
    else if(rEPGTimerEventTarget.u32StartTime < rEPGTimerEventCompared.u32StartTime)
        {
        if((rEPGTimerEventCompared.u32StartTime - rEPGTimerEventTarget.u32StartTime) < u32TimeGap)
            {
            return E_SHIFT_TYPE_START_MINUS;
        }
            }

    return E_SHIFT_TYPE_NONE;

}

#endif

static U32 _GetTimerId()
{
    MS_USER_SYSTEM_SETTING stSystemSetting;
    MSrv_System_Database* pSysDb = MSrv_Control::GetMSrvSystemDatabase();
    ASSERT(pSysDb);
    pSysDb->GetUserSystemSetting(&stSystemSetting);
    U32 counter = ++stSystemSetting.u32MsrvTimerCounter;
    pSysDb->SetUserSystemSetting(&stSystemSetting);
    return counter;
}

BOOL MSrv_Timer::UpdateEPGTimerMoniter(U32 u32TimeActing)
{
    if(m_EPGTimerRecordStatus == EN_EPGTIMER_RECORDER_TIMEUP_RECORDING)
        return TRUE;
    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);

    ST_EPG_EVENT_TIMER_INFO *pEventList = NULL;
    U32 listSize = 0;
    memset(&m_NextB2BRecordingProgram,0,sizeof(m_NextB2BRecordingProgram));
    mapi_scope_lock(scopeLock, &m_EpgTimerMutex);
    pSysDB->UseEpgTimerList(&pEventList, &listSize);

    ST_EPG_EVENT_TIMER_INFO stTimer = pEventList[0]; //if the sorting is changing, the nextStartUpIdx will not different.
    m_u32EPGTimerActionTime = INVALID_EPG_TIME;
    {
        //Get nearest timer action
        if(stTimer.enTimerType == EPG_EVENT_REMIDER)
        {
            if(stTimer.u32StartTime > u32TimeActing)
            {
                m_EPGTimerActionMode = EN_EPGTIMER_ACT_REMINDER;
                m_u32EPGTimerActionTime = stTimer.u32StartTime;
            }
        }
        else if(stTimer.enTimerType == EPG_EVENT_CI_OP_REFRESH)
        {
            if(stTimer.u32StartTime > u32TimeActing)
            {
                m_EPGTimerActionMode = EN_EPGTIMER_ACT_CI_OP_REFRESH;
                m_u32EPGTimerActionTime = stTimer.u32StartTime;
            }
        }
        else if(stTimer.enTimerType == EPG_EVENT_RECORDER)
        {
            if(pEventList[1].enTimerType == EPG_EVENT_RECORDER)
            {
                if(IsBack2BackTimeGap(pEventList[0],pEventList[1]))
                {
                    memcpy(&m_NextB2BRecordingProgram, &pEventList[1], sizeof(m_NextB2BRecordingProgram));
                }
            }
            if(stTimer.u32StartTime > u32TimeActing)
            {
                m_EPGTimerActionMode = EN_EPGTIMER_ACT_RECORDER_START;
                m_u32EPGTimerActionTime = stTimer.u32StartTime;
                m_u32EPGTimerRecordDuration = stTimer.u32DurationTime;
            }
            else if((stTimer.u32StartTime + stTimer.u32DurationTime) > u32TimeActing)
            {
                m_EPGTimerActionMode = EN_EPGTIMER_ACT_RECORDER_START;
                m_u32EPGTimerActionTime = u32TimeActing+ EPGTIMER_RECORDER_LEADING_TIME_S+EPGTIMER_COUNTDOWN_LEADING_TIME_S;
                m_u32EPGTimerRecordDuration = stTimer.u32StartTime + stTimer.u32DurationTime - m_u32EPGTimerActionTime;

            }
            /*
            if((stTimer.u32StartTime + stTimer.u32DurationTime) > u32TimeActing)
            {
                m_EPGTimerActionMode = EN_EPGTIMER_ACT_RECORDER_STOP;
                m_u32EPGTimerActionTime = (stTimer.u32StartTime + stTimer.u32DurationTime);
            }
            */
        }
        else
        {
            m_EPGTimerActionMode = EN_EPGTIMER_ACT_NONE;
        }
    }
#if (OAD_ENABLE == 1)
    MW_OAD_INFORMATION Oad_Info;
    MSrv_Control::GetMSrvSystemDatabase()->GetOADInfo((U8*)&Oad_Info);
#endif
    if(((m_u32EPGTimerActionTime != INVALID_EPG_TIME) && (m_u32EPGTimerActionTime > u32TimeActing))
            && ((m_EPGTimerActionMode == EN_EPGTIMER_ACT_REMINDER) ||(m_EPGTimerActionMode == EN_EPGTIMER_ACT_CI_OP_REFRESH) || (m_EPGTimerActionMode == EN_EPGTIMER_ACT_RECORDER_START)))
    {
        ST_time stTime;
        ConvertSeconds2StTime(m_u32EPGTimerActionTime, &stTime);
        ST_OnTime_TVDes eTVdest;
        eTVdest.enTVSrc = EN_Time_OnTimer_Source_DTV;
        eTVdest.u16ChNo = pEventList[0].u16ServiceNumber;
        eTVdest.u8Vol = 0;
        SetOnTime(stTime, EN_Timer_Off, eTVdest, TRUE, TRUE, m_EPGTimerActionMode);
    }
    else if((m_enOnTimerState != EN_Timer_Off) &&
        (m_EPGTimerActionMode == EN_EPGTIMER_ACT_NONE))
    {
            SetOnTime(m_stOnTime, m_enOnTimerState, m_stOnTimeDes, TRUE, TRUE);
    }
    else
    {
        #if (OAD_ENABLE == 1)
        if(Oad_Info.CheckStatus.u8ScheduleOn == FALSE)
        #endif
        {
            mapi_interface::Get_mapi_system()->RTCEnableInterrupt(FALSE);
        }
    }

    return TRUE;
}

BOOL MSrv_Timer::DeletePastEPGTimer()
{
    DelPastTimeItems(mapi_interface::Get_mapi_system()->RTCGetCLK());
    return TRUE;
}

BOOL MSrv_Timer::IsEPGTimerInList(const ST_EPG_EVENT_TIMER_INFO &stTimerInfo)
{
    U32 u32NumofEvents = 0;

    u32NumofEvents = GetEPGTimerEventCount();

    if(u32NumofEvents > 0)
    {
        MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
        ASSERT(pSysDB);

        ST_EPG_EVENT_TIMER_INFO stTimerDB[EPG_TIMER_MAX_NUM];
        memset(stTimerDB, 0, sizeof(stTimerDB));
        /* load EPG timer DB */
        mapi_scope_lock(scopeLock, &m_EpgTimerMutex);
        pSysDB->GetEpgTimer(stTimerDB);

        for(U32 i = 0; i < EPG_TIMER_MAX_NUM; i++)
        {
            //printf("sttest IsEPGTimerInList %d,%d,%d,%d \n",i,stTimerDB[i].enTimerType,stTimerDB[i].u16EventID,stTimerInfo.u16EventID);
            if(stTimerDB[i].enTimerType == EPG_EVENT_NONE)
            {
                break;
            }
            if((stTimerInfo.u16EventID == stTimerDB[i].u16EventID)
                &&(stTimerInfo.enTimerType == stTimerDB[i].enTimerType))
            {
                return TRUE;
            }
        }
    }
    return FALSE;
}

EPG_TIMER_CHECK MSrv_Timer::IsEPGTimerSettingValid(const ST_EPG_EVENT_TIMER_INFO &stTimerInfo)
{
    U32 timeNow = 0;
    U32 u32NumofEvents = 0;

    //Get System Time Now
    timeNow = mapi_interface::Get_mapi_system()->RTCGetCLK();

    u32NumofEvents = GetEPGTimerEventCount();

    // [1] Check if start timer is past && has empty space
    if(((EPG_EVENT_CI_OP_REFRESH == stTimerInfo.enTimerType) && (timeNow >= stTimerInfo.u32StartTime))
        || ((EPG_EVENT_REMIDER == stTimerInfo.enTimerType) && (timeNow >= stTimerInfo.u32StartTime))
        || ((EPG_EVENT_RECORDER == stTimerInfo.enTimerType) && (timeNow >= stTimerInfo.u32StartTime + stTimerInfo.u32DurationTime)))
    {
        return EPG_TIMER_CHECK_PAST;
    }
    if(u32NumofEvents > EPG_TIMER_MAX_NUM)
    {
        return EPG_TIMER_CHECK_FULL;
    }
    if(stTimerInfo.bIsEndTimeBeforeStart)
    {
        printf("-----> bIsEndTimeBeforeStart\n");
        return EPG_TIMER_CHECK_ENDTIME_BEFORE_START;
    }

    // [2] Check if new timer is overlapping the existed timer list
    if(u32NumofEvents > 0)
    {
        MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
        ASSERT(pSysDB);

        ST_EPG_EVENT_TIMER_INFO stTimerDB[EPG_TIMER_MAX_NUM];

        /* load EPG timer DB */
        mapi_scope_lock(scopeLock, &m_EpgTimerMutex);
        pSysDB->GetEpgTimer(stTimerDB);

        for(U32 i = 0; i < EPG_TIMER_MAX_NUM; i++)
        {
            if(stTimerDB[i].enTimerType == EPG_EVENT_NONE)
            {
                break;
            }

            if(stTimerInfo.u32StartTime == stTimerDB[i].u32StartTime)
            {
                return EPG_TIMER_CHECK_OVERLAY;
            }

            //if((stTimerInfo.enTimerType == EPG_EVENT_RECORDER) && (stTimerDB[i].enTimerType == EPG_EVENT_RECORDER))
            {
                if(CheckEPGItemOverlap(stTimerDB[i], stTimerInfo))
                {
                    return EPG_TIMER_CHECK_OVERLAY;
                }
            }
        }
    }

    return EPG_TIMER_CHECK_SUCCESS;
}

EPG_TIMER_CHECK MSrv_Timer::AddEPGEvent(const ST_EPG_EVENT_TIMER_INFO& stEPGEvent)
{
    U32 u32TimerId = 0;
    return AddEPGEvent(stEPGEvent, u32TimerId);
}

EPG_TIMER_CHECK MSrv_Timer::AddEPGEvent(const ST_EPG_EVENT_TIMER_INFO& stEPGEvent, U32& u32TimerId)
{
//   stEPGEvent.u32StartTime -= mapi_si_dvb_parser::GetOffsetTime(stEPGEvent.u32StartTime, NULL, NULL,  MAPI_TRUE);
    ST_EPG_EVENT_TIMER_INFO stEPGEventWithPaddings = stEPGEvent;
    _AddPaddingsToEpgEventTimerInfo(stEPGEventWithPaddings);

    EPG_TIMER_CHECK rtnCheck = IsEPGTimerSettingValid(stEPGEventWithPaddings);
    if(rtnCheck != EPG_TIMER_CHECK_SUCCESS)
    {
        return rtnCheck;
    }

    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);


    ST_EPG_EVENT_TIMER_INFO *pEventList = NULL;
    U32 listSize = 0;

    mapi_scope_lock(scopeLock, &m_EpgTimerMutex);
    pSysDB->UseEpgTimerList(&pEventList, &listSize);

    U16 iItemCount = GetEPGTimerEventCount();
    if(iItemCount >= EPG_TIMER_MAX_NUM)
    {
        return EPG_TIMER_CHECK_FULL;
    }
    memcpy(&(pEventList[iItemCount]), &stEPGEventWithPaddings, sizeof(ST_EPG_EVENT_TIMER_INFO));
    pEventList[iItemCount].u32TimerId = u32TimerId = _GetTimerId();
    if((iItemCount + 1) < EPG_TIMER_MAX_NUM)
    {
        pEventList[iItemCount+1].enTimerType = EPG_EVENT_NONE;
    }

    ReconfigTimerFromList();
    return EPG_TIMER_CHECK_SUCCESS;
}

BOOL MSrv_Timer::DelEPGEvent(U16 index)
{
    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);

    ST_EPG_EVENT_TIMER_INFO *pEventList = NULL;
    U32 listSize = 0;

    mapi_scope_lock(scopeLock, &m_EpgTimerMutex);
    pSysDB->UseEpgTimerList(&pEventList, &listSize);

    U16 iItemCount = GetEPGTimerEventCount();
    if((iItemCount == 0) || (index >= iItemCount))
    {
        return FALSE;
    }

    if(index < (iItemCount - 1))
    {
        memcpy(&(pEventList[index]), &(pEventList[iItemCount - 1]), sizeof(ST_EPG_EVENT_TIMER_INFO));
    }
    pEventList[iItemCount - 1].enTimerType = EPG_EVENT_NONE;
    ReconfigTimerFromList();
    return TRUE;
}

BOOL MSrv_Timer::DelEPGEventById(const U32 u32TimerId)
{
    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);

    ST_EPG_EVENT_TIMER_INFO *pEventList = NULL;
    U32 listSize = 0;

    mapi_scope_lock(scopeLock, &m_EpgTimerMutex);
    pSysDB->UseEpgTimerList(&pEventList, &listSize);

    U16 iItemCount = GetEPGTimerEventCount();
    for(U16 idx = 0; idx < iItemCount; ++idx)
    {
        if(u32TimerId == pEventList[idx].u32TimerId)
        {
            return this->DelEPGEvent(idx);
        }
    }

    return FALSE;
}

//Check Auto channel update continue or not ,if EPG remider or recorder exists within input value seconds..
BOOL MSrv_Timer::IsEPGScheduleRecordRemiderExist(U32 u32SecFromNow)
{
    BOOL bEpgRecordRemiderExist = FALSE;

    U32 u32SystemTimeNow = 0;
    U32 u32NumofEpgEvents = 0;

    u32SystemTimeNow = mapi_interface::Get_mapi_system()->RTCGetCLK();

    ST_EPG_EVENT_TIMER_INFO *pEventList = NULL;

    mapi_scope_lock(scopeLock, &m_EpgTimerMutex);
    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);

    pSysDB->UseEpgTimerList(&pEventList, &u32NumofEpgEvents);

    if(u32NumofEpgEvents > 0)
    {
        for(U32 i = 0; i < EPG_TIMER_MAX_NUM; i++)
        {
            if(pEventList[i].enTimerType == EPG_EVENT_NONE)
            {
                break;
            }
            else if(pEventList[i].enTimerType == EPG_EVENT_REMIDER)
            {
                 if(pEventList[i].u32StartTime <= u32SystemTimeNow + u32SecFromNow)
                {
                    bEpgRecordRemiderExist = TRUE;
                    break;
                }
            }
            else if(pEventList[i].enTimerType == EPG_EVENT_RECORDER)
            {
                if(pEventList[i].u32StartTime <= u32SystemTimeNow + u32SecFromNow)
                {
                    bEpgRecordRemiderExist = TRUE;
                    break;
                }
            }
        }
    }

    return bEpgRecordRemiderExist;
}

#if (ACTIVE_STANDBY_MODE_ENABLE == 1)
BOOL MSrv_Timer::IsActiveStandbyModeNeedTrigger(void)
{
#if (PVR_ENABLE == 1)
    ST_EPG_EVENT_TIMER_INFO *pEventList = NULL;
    U32 listSize = 0;
    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);
    mapi_scope_lock(scopeLock, &m_EpgTimerMutex);
    pSysDB->UseEpgTimerList(&pEventList, &listSize);
    if(pEventList != NULL)
    {
        if((EPG_EVENT_RECORDER == pEventList[0].enTimerType) ||
            (EPG_EVENT_CI_OP_REFRESH == pEventList[0].enTimerType))
        {
            return TRUE;
        }
    }
#endif
    return FALSE;
}
#endif
BOOL MSrv_Timer::GetCurrentScheduleCachedPinCode(U16& u16CachedPinCode)
{
    BOOL bPinCodeCached=FALSE;
    u16CachedPinCode = INVALID_PIN_CODE;
#if (ATSC_SYSTEM_ENABLE == 0)
#if (CI_PLUS_ENABLE == 1)
#if (PVR_ENABLE == 1)
    ST_EPG_EVENT_TIMER_INFO *pEventList = NULL;
    U32 listSize = 0;
    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);
    mapi_scope_lock(scopeLock, &m_EpgTimerMutex);
    pSysDB->UseEpgTimerList(&pEventList, &listSize);
    if(pEventList != NULL)
    {
        bPinCodeCached = pEventList[0].bPinCodeCached;
        u16CachedPinCode = pEventList[0].u16CachedPinCode;
    }
#endif
#endif
#endif
    return bPinCodeCached;
}

BOOL MSrv_Timer::EPGTimerExecTimerAction()
{
    MTIMER_IFO("EPGTimerExecTimerAction()\n");
    if(GetEPGTimerEventCount() == 0)
    {
        return FALSE;
    }

    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);


    ST_EPG_EVENT_TIMER_INFO *pEventList = NULL;
    U32 listSize = 0;
    mapi_scope_lock(scopeLock, &m_EpgTimerMutex);
    pSysDB->UseEpgTimerList(&pEventList, &listSize);
    if(pEventList == NULL)
    {
        return FALSE;
    }

#if (ATSC_SYSTEM_ENABLE == 0)
    MSrv_DTV_Player_DVB* pPlayer = ((MSrv_DTV_Player_DVB*)(MSrv_Control::GetInstance()->GetMSrvDtvByIndex(pEventList[0].u8DtvRoute)));
    ASSERT(pPlayer);
#endif

    #if (ATSC_SYSTEM_ENABLE == 1)
        U16 actionMajorNumber = pEventList[0].u16MajorNumber;
        U16 actionMinorNumber = pEventList[0].u16MinorNumber;
    #else
        U16 actionServiceNumber = pEventList[0].u16ServiceNumber;
        U8 actionServiceType = pEventList[0].u8ServiceType;
    #endif

    switch(m_EPGTimerActionMode)
    {
        case EN_EPGTIMER_ACT_REMINDER:
        {

#ifdef TARGET_BUILD
#if (MSTAR_IPC == 1)
            char appName[16];
            int ret=0;
            ret=APM_GetFocusApp(appName, sizeof(appName));
            if( (ret==0) && (strncmp(appName, "SN", strlen("SN"))) && (strncmp(appName, "CommonUI", strlen("CommonUI"))) )
            {
                APM_RequestToExit((char *)appName); //EPG_TIME_UP event exit focused AP
            }
#endif //end of MSTAR_IPC
#endif //end of TARGET_BUILD

            SendEvent(NULL, EV_DMP_QUIT, 0, 0); //EPG_TIME_UP event kills DMPlayerApp
#if (ISDB_SYSTEM_ENABLE == 1)
            ST_MEDIUM_SETTING MediumSetting;
            MSrv_Control::GetMSrvSystemDatabase()->GetMediumSetting(&MediumSetting);
            if (MediumSetting.AntennaType != E_ANTENNA_TYPE_AIR)
            {
                MediumSetting.AntennaType = E_ANTENNA_TYPE_AIR;
                MSrv_Control::GetMSrvSystemDatabase()->SetMediumSetting(&MediumSetting);
#if (STB_ENABLE == 0)
                MSrv_Control::GetMSrvAtv()->SetNTSCAntenna(MSrv_ATV_Database::MEDIUM_AIR);
                MSrv_ATV_Database *pAtvDb = dynamic_cast<MSrv_ATV_Database *>(MSrv_Control::GetMSrvAtvDatabase());
                ASSERT(pAtvDb );
                pAtvDb->ConnectDatabase(MSrv_ATV_Database::DVBT_DB);
#endif
                MSrv_Control::GetInstance()->SetInputSource(MAPI_INPUT_SOURCE_DTV);
                MSrv_Control_common::GetMSrvChannelManager()->ChangeToFirstProgram(E_FIRST_PROG_DTV, E_ON_TIME_BOOT_TYPE);
            }
            else
            {
                MSrv_Control::GetInstance()->SetInputSource(MAPI_INPUT_SOURCE_DTV);
                MSrv_Control_common::GetMSrvChannelManager()->ProgramSel((U32)actionServiceNumber, actionServiceType);
            }
#else
            if(MAPI_INPUT_SOURCE_DTV != MSrv_Control::GetInstance()->GetCurrentInputSource())
            {
                MSrv_Control::GetInstance()->SetInputSource(MAPI_INPUT_SOURCE_DTV);
            }
    #if (ATSC_SYSTEM_ENABLE == 1)
            MSrv_Control_common::GetMSrvChannelManager()->ProgramSel(actionMajorNumber, actionMinorNumber);
    #else
            MSrv_Control_common::GetMSrvChannelManager()->ProgramSel((U32)actionServiceNumber, actionServiceType);
    #endif
#endif
            U32 u32EPGTimerActionTime = m_u32EPGTimerActionTime;
            m_u32EPGTimerActionTime = INVALID_EPG_TIME;
            ReconfigTimerFromList(u32EPGTimerActionTime + 1);
        }
            break;
        case EN_EPGTIMER_ACT_CI_OP_REFRESH:
        {
#if (ATSC_SYSTEM_ENABLE == 0)
#if (CI_PLUS_ENABLE == 1)
            pPlayer->NotifyCiOpRefreshApproval(TRUE);
            ReconfigTimerFromList(mapi_interface::Get_mapi_system()->RTCGetCLK() + 1);
#endif
#endif
        }
            break;
        case EN_EPGTIMER_ACT_RECORDER_START:
        {
#ifdef TARGET_BUILD
#if (MSTAR_IPC == 1)
            char appName[16];
            int ret=0;
            ret=APM_GetFocusApp(appName, sizeof(appName));
            if( (ret==0) && (strncmp(appName, "SN", strlen("SN"))) && (strncmp(appName, "CommonUI", strlen("CommonUI"))) )
            {
                APM_RequestToExit((char *)appName); //EPG_TIME_UP event exit focused AP
            }
#endif //end of MSTAR_IPC
#endif //end of TARGET_BUILD

#if (ISDB_SYSTEM_ENABLE == 1)
            ST_MEDIUM_SETTING MediumSetting;
            MSrv_Control::GetMSrvSystemDatabase()->GetMediumSetting(&MediumSetting);
            if (MediumSetting.AntennaType != E_ANTENNA_TYPE_AIR)
            {
                MediumSetting.AntennaType = E_ANTENNA_TYPE_AIR;
                MSrv_Control::GetMSrvSystemDatabase()->SetMediumSetting(&MediumSetting);
#if (STB_ENABLE == 0)
                MSrv_Control::GetMSrvAtv()->SetNTSCAntenna(MSrv_ATV_Database::MEDIUM_AIR);
                MSrv_ATV_Database *pAtvDb = dynamic_cast<MSrv_ATV_Database *>(MSrv_Control::GetMSrvAtvDatabase());
                ASSERT(pAtvDb );
                pAtvDb->ConnectDatabase(MSrv_ATV_Database::DVBT_DB);
#endif
                MSrv_Control::GetInstance()->SetInputSource(MAPI_INPUT_SOURCE_DTV);
#if (PVR_ENABLE == 1 && ATSC_SYSTEM_ENABLE == 0)
            // Scheduled recording has higher priority than normal record (One-Touch-Recording)
                pPlayer->PVRStopAll();
#endif

                MSrv_Control_common::GetMSrvChannelManager()->ChangeToFirstProgram(E_FIRST_PROG_DTV, E_ON_TIME_BOOT_TYPE);
            }
            else
            {
                MSrv_Control::GetInstance()->SetInputSource(MAPI_INPUT_SOURCE_DTV);
#if (PVR_ENABLE == 1 && ATSC_SYSTEM_ENABLE == 0)
            // Scheduled recording has higher priority than normal record (One-Touch-Recording)
                pPlayer->PVRStopAll();
#endif
                MSrv_Control_common::GetMSrvChannelManager()->ProgramSel((U32)actionServiceNumber, actionServiceType);
            }

#else

#if (PVR_ENABLE == 1 && ATSC_SYSTEM_ENABLE == 0)

            // Scheduled recording has higher priority than normal record (One-Touch-Recording),
            // But DTV has already modify this flow inside PVR check before channel change
#if (STB_ENABLE == 1)
            pPlayer->PVRStopAll();
#endif




            //Leading time up
#if (PIP_ENABLE == 1)
            ST_TRIPLE_ID stService;
            BOOL bRet = FALSE;
            stService.u16OnId  = pEventList[0].u16Onid;
            stService.u16TsId  = pEventList[0].u16Tsid;
            stService.u16SrvId = pEventList[0].u16Sid;
            bRet = MSrv_Control::GetInstance()->SetRecordServiceByRoute(stService, pEventList[0].u8DtvRoute, TRUE);
            if(!bRet)
            {
                DelEPGEventById(pEventList[0].u32TimerId);
                m_EPGTimerRecordStatus = EN_EPGTIMER_RECORDER_IDLE;
                break;
            }
#else
            MSrv_Control::GetInstance()->SwitchMSrvDtvRoute(pEventList[0].u8DtvRoute, MAPI_MAIN_WINDOW, TRUE);
            MSrv_Control_common::GetMSrvChannelManager()->ProgramSel((U32)actionServiceNumber, actionServiceType);
#endif
#endif
#endif
            U32 current = mapi_interface::Get_mapi_system()->RTCGetCLK();
            if(!((m_u32EPGTimerActionTime != INVALID_EPG_TIME) &&
                ((current+EPGTIMER_RECORDER_LEADING_TIME_S) < m_u32EPGTimerActionTime)))
            {
                m_EPGTimerRecordStatus = EN_EPGTIMER_RECORDER_TIMEUP;
            }
        }
            break;
        case EN_EPGTIMER_ACT_RECORDER_STOP:
            PostEvent(NULL, EV_PVR_NOTIFY_SCHEDULED_STOP, pEventList[0].u8DtvRoute);
            m_EPGTimerRecordStatus = EN_EPGTIMER_RECORDER_IDLE;
            break;
        default:
            break;
    }
    return TRUE;
}

BOOL  MSrv_Timer::IsDaylightSaving()
{
    return m_bIsDaylightsaving;
}

BOOL MSrv_Timer::DelALLEPGEvent(void)
{
    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();

    ST_EPG_EVENT_TIMER_INFO *pEventList = NULL;
    U32 u32listSize = 0;
    U16 u16ItemCount = 0;

    mapi_scope_lock(scopeLock, &m_EpgTimerMutex);

    pSysDB->UseEpgTimerList(&pEventList, &u32listSize);

    u16ItemCount = GetEPGTimerEventCount();

    if(u16ItemCount == 0)
    {
        return FALSE;
    }
    pSysDB->InitEPGTimerSetting();

    ReconfigTimerFromList();

    return TRUE;
}

BOOL MSrv_Timer::_IsEventRecordProgramReady()
{
#if (EPG_ENABLE == 1 && PVR_ENABLE == 1 && ATSC_SYSTEM_ENABLE == 0)
    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);
    ST_EPG_EVENT_TIMER_INFO *pEventList = NULL;
    U32 listSize = 0;

    mapi_scope_lock(scopeLock, &m_EpgTimerMutex);
    pSysDB->UseEpgTimerList(&pEventList, &listSize);

    if(listSize == 0)
    {
        printf("Error: Check schedule record with event queue is empty!!\n");
        return FALSE;
    }

    if(pEventList[0].enRepeatMode != EPG_EVENT_RECORDER_EVENT_ID)
    {
        printf("Error: Check schedule record with not event base!!\n");
        return FALSE;
    }

    MSrv_DTV_Player_DVB *pDVBPlayer = dynamic_cast<MSrv_DTV_Player_DVB*>(MSrv_Control::GetInstance()->GetMSrvDtvByIndex(pEventList[0].u8DtvRoute));
    if(pDVBPlayer->isActive() != TRUE)
    {
        return FALSE;
    }

    ST_DTV_SPECIFIC_PROGINFO CurrProg_Info;
    ST_EPG_EVENT_INFO stEventInfo;
    U32 u32StartTime;
    pDVBPlayer->GetCurrentProgramSpecificInfo(CurrProg_Info);
    u32StartTime = MSrv_Control::GetMSrvTimer()->GetNextNDayCLKUtcTimeInSec(0);

    if((pDVBPlayer->GetEventInfoByTime(CurrProg_Info.m_eServiceType, CurrProg_Info.m_u32Number, u32StartTime, stEventInfo) == TRUE) &&
       (pEventList[0].u16EventID != stEventInfo.u16eventId))
    {
        return FALSE;
    }
    else
    {
        return TRUE;
    }
#else
    return FALSE;
#endif
}

#if (EPG_ENABLE == 1 && PVR_ENABLE == 1 && ATSC_SYSTEM_ENABLE == 0)
static void EPGTimerMoveLastItemTo(ST_EPG_EVENT_TIMER_INFO *pEventList, U32& u8LastIdx, U32 u8TargetIdx)
{
    pEventList[u8TargetIdx] = pEventList[u8LastIdx];
    //memset(&pEventList[u8LastIdx], 0, sizeof(pEventList[u8LastIdx]));
    pEventList[u8LastIdx].enTimerType = EPG_EVENT_NONE;
    u8LastIdx--;
}

static BOOL FindEventByTimeAndEventID(MSrv_DTV_Player_DVB* pPlayer , U8 u8ServiceType, U16 u16ServiceNum, U32 u32StartTime, U16 u16EventId, ST_EPG_EVENT_INFO& stEPGEvent)
{
    if(pPlayer->GetEventInfoByTime(u8ServiceType, u16ServiceNum, u32StartTime, stEPGEvent) && (u16EventId == stEPGEvent.u16eventId))
        return TRUE;
    if(pPlayer->GetEventInfoByID(u8ServiceType, u16ServiceNum, u16EventId, stEPGEvent))
        return TRUE;
    return FALSE;
}

struct ChangedEvent_S
{
    ST_EPG_EVENT_TIMER_INFO EPGTimertInfo;
    U32 TimerIdx;
};

#define TIME_PERIOD_TO_TRACK_EPG 5
void MSrv_Timer::_TrackScheChanging()
{
    static U32 preTime = mapi_interface::Get_mapi_system()->RTCGetCLK();
    U32 curTime = mapi_interface::Get_mapi_system()->RTCGetCLK();

    _TrackCRIDUpdate();

    if(curTime - preTime < TIME_PERIOD_TO_TRACK_EPG)
        return;
    preTime = curTime;
/*
code flow: prevent locking epg timer db and epg db simultaneously from causing dead lock.
1. Clone epg timer DB.
2. Find and record  schedule chnaged events in epg DB.
3. update the schedule chnaged event back to epg timer db.
*/
    //printf("_TrackScheChanging\n");
    MSrv_DTV_Player_DVB* pPlayer = dynamic_cast<MSrv_DTV_Player_DVB*>(MSrv_Control::GetMSrvDtv());
    if(pPlayer == NULL)
    {
        MTIMER_ERR("Warning! %s:%d, not getting DTV_Player\n", __FUNCTION__, __LINE__);
        return;
    }

    U32 u32CurrentTime = mapi_interface::Get_mapi_system()->RTCGetCLK();
    u32 u32TimeOffset = mapi_interface::Get_mapi_system()->GetClockOffset();
    //u32 u32CurrentTimeLocal = u32CurrentTime+u32TimeOffset;

    ST_EPG_EVENT_INFO stEPGEvent;
    if(pPlayer->IsRecording() && (m_RecordingProgram.u16EventID>0) && (m_RecordingProgram.enRepeatMode == EPG_EVENT_RECORDER_EVENT_ID) )
    {
        if(pPlayer->GetEventInfoByTime(m_RecordingProgram.u8ServiceType, m_RecordingProgram.u16ServiceNumber, u32CurrentTime, stEPGEvent))
        {
            if((stEPGEvent.u16eventId == m_RecordingProgram.u16EventID )
                && (((m_RecordingProgram.u32StartTime+m_RecordingProgram.u32DurationTime)!=(stEPGEvent.u32UtcStartTime+stEPGEvent.u32DurationTime)))
            )
            {
                m_RecordingProgram.u32DurationTime = stEPGEvent.u32UtcStartTime+stEPGEvent.u32DurationTime - m_RecordingProgram.u32StartTime;
                m_u32EPGTimerRecordDuration = stEPGEvent.u32UtcStartTime+stEPGEvent.u32DurationTime - m_u32EPGTimerActionTime;
                //printf("Sche Change: %s [%s - %s](%d) \n", stEPGEvent.sName.c_str(), stTimeStr.c_str(), edTimeStr.c_str(), dur.u8Minute);
            }
        }
    }

    if((m_NextB2BRecordingProgram.u32StartTime>0) && FindEventByTimeAndEventID(pPlayer, m_NextB2BRecordingProgram.u8ServiceType, m_NextB2BRecordingProgram.u16ServiceNumber, m_NextB2BRecordingProgram.u32StartTime, m_NextB2BRecordingProgram.u16EventID, stEPGEvent))
    {
        if((m_NextB2BRecordingProgram.u32StartTime!= stEPGEvent.u32UtcStartTime)||(m_NextB2BRecordingProgram.u32DurationTime!= stEPGEvent.u32DurationTime))
        {
            //printf("==> sche change!!\n");
            m_NextB2BRecordingProgram.u32StartTime = stEPGEvent.u32UtcStartTime;
            m_NextB2BRecordingProgram.u32DurationTime = stEPGEvent.u32DurationTime;
            if(!IsBack2BackTimeGap(m_RecordingProgram, m_NextB2BRecordingProgram))
            {
                memset(&m_NextB2BRecordingProgram, 0, sizeof(m_NextB2BRecordingProgram));
            }
            else
            {
                MSrv_Timer::ST_time stTime;
                ConvertSeconds2StTime(m_NextB2BRecordingProgram.u32StartTime+u32TimeOffset, &stTime);
                //printf("New StartTime: %d::%d::%d\n", stTime.u8Hour, stTime.u8Minute, stTime.u8Second);
            }
        }
    }

    std::list<ChangedEvent_S> ScheChangedEventList;
    ST_EPG_EVENT_TIMER_INFO *pEventList = NULL;
    U32 listSize = 0;
    u32 u32eventCount = 0;
    ST_EPG_EVENT_TIMER_INFO clonedEPGTinerList[EPG_TIMER_MAX_NUM];
    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);
    {
        mapi_scope_lock(scopeLock, &m_EpgTimerMutex);
        pSysDB->UseEpgTimerList(&pEventList, &listSize);
        u32eventCount =  MSrv_Control::GetMSrvTimer()->GetEPGTimerEventCount();
        if(u32eventCount ==0 )
        {
            return;
        }
        memcpy(clonedEPGTinerList, pEventList, sizeof(ST_EPG_EVENT_TIMER_INFO)*u32eventCount);
        pEventList = clonedEPGTinerList;
    }

    BOOL bHasProgramUpdated = FALSE;

    for(U32 i = 0 ; i < u32eventCount; i++)
    {
        ST_EPG_EVENT_INFO stEPGEvent={0};
        if(pEventList[i].enRepeatMode != EPG_EVENT_RECORDER_EVENT_ID)
            continue;

        if(pEventList[i].enTimerType != EPG_EVENT_RECORDER)
            continue;

        if(!FindEventByTimeAndEventID(pPlayer, pEventList[i].u8ServiceType, pEventList[i].u16ServiceNumber, pEventList[i].u32StartTime+u32TimeOffset, pEventList[i].u16EventID, stEPGEvent))
        {
            //Event not found, skip it.
            continue;
        }

        if((stEPGEvent.u32UtcStartTime!= pEventList[i].u32StartTime) || (stEPGEvent.u32DurationTime!=pEventList[i].u32DurationTime))
        {
            ChangedEvent_S changedEvent;
            changedEvent.EPGTimertInfo = pEventList[i];
            changedEvent.TimerIdx = i;
            changedEvent.EPGTimertInfo.u32StartTime = stEPGEvent.u32UtcStartTime;
            changedEvent.EPGTimertInfo.u32DurationTime = stEPGEvent.u32DurationTime;
            ScheChangedEventList.push_back(changedEvent);
            bHasProgramUpdated = TRUE;
        }
    }

    if(!bHasProgramUpdated)
    {
        return;
    }

    //update EPG timer DB
    mapi_scope_lock(scopeLock, &m_EpgTimerMutex);

    pSysDB->UseEpgTimerList(&pEventList, &listSize);
    u32eventCount =  MSrv_Control::GetMSrvTimer()->GetEPGTimerEventCount();
    U32 u32LastItemIdx = u32eventCount-1;
    std::list<ChangedEvent_S>::reverse_iterator rit;
    rit = ScheChangedEventList.rbegin();
    while(rit!=ScheChangedEventList.rend())
    {
        ChangedEvent_S& chnagedEventInfo = *rit;
        ST_EPG_EVENT_TIMER_INFO& rEPGTimerEvent = chnagedEventInfo.EPGTimertInfo;
        u32 idx = chnagedEventInfo.TimerIdx;
        if((idx<=u32LastItemIdx) && (pEventList[idx].u8ServiceType == rEPGTimerEvent.u8ServiceType)
            && (pEventList[idx].u16ServiceNumber == rEPGTimerEvent.u16ServiceNumber)
            && (pEventList[idx].u16EventID == rEPGTimerEvent.u16EventID))
        {
            EPGTimerMoveLastItemTo(pEventList, u32LastItemIdx, idx);
            rit++;
        }
        else
        {
            std::list<ChangedEvent_S>::iterator it = ScheChangedEventList.erase((++rit).base());
            std::list<ChangedEvent_S>::reverse_iterator new_rit(it);
            rit = new_rit;
        }
    }

    ReconfigTimerFromList();
    std::list<ChangedEvent_S>::iterator it;
    for(it = ScheChangedEventList.begin();it != ScheChangedEventList.end(); it++)
    {
        ChangedEvent_S& chnagedEventInfo = *it;
        ST_EPG_EVENT_TIMER_INFO& rEPGTimerEvent = chnagedEventInfo.EPGTimertInfo;
        AddEPGEvent(rEPGTimerEvent);
    }
}

EPG_TIMER_CHECK MSrv_Timer::FixReminderConflict(const ST_EPG_EVENT_TIMER_INFO& stEPGEvent)
{
    EPG_TIMER_CHECK rtnCheck = EPG_TIMER_CHECK_NONE;
    ST_EPG_EVENT_TIMER_INFO stTmpEPGEvent;
    MAPI_U32 u32TmpTime = 0;
    MAPI_U8 u8TmpTimeType = 0;
    memcpy(&(stTmpEPGEvent), &stEPGEvent, sizeof(ST_EPG_EVENT_TIMER_INFO));

    if (EPG_EVENT_RECORDER != stTmpEPGEvent.enTimerType)
    {
        return EPG_TIMER_CHECK_NONE;
    }

    u32TmpTime    = stTmpEPGEvent.u32StartTime;
    u8TmpTimeType = stTmpEPGEvent.enTimerType;

    stTmpEPGEvent.u32StartTime += TIMER_REMINDER_CONFLICT_OFFSET;
    rtnCheck = IsEPGTimerSettingValid(stTmpEPGEvent);
    if(EPG_TIMER_CHECK_DB_REMINDER_CONFLICT != rtnCheck)
    {
        return EPG_TIMER_CHECK_OVERLAY;
    }


    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);


    ST_EPG_EVENT_TIMER_INFO *pEventList = NULL;
    U32 listSize = 0;

    mapi_scope_lock(scopeLock, &m_EpgTimerMutex);
    pSysDB->UseEpgTimerList(&pEventList, &listSize);

    U16 iItemCount = GetEPGTimerEventCount();
    printf("sttest AddEPGEvent iItemCount = %d \n",iItemCount);
    if(iItemCount >= EPG_TIMER_MAX_NUM)
    {
        return EPG_TIMER_CHECK_FULL;
    }

    stTmpEPGEvent.u32StartTime = u32TmpTime;
    stTmpEPGEvent.enTimerType  = u8TmpTimeType;
    for(U32 i = 0; i < EPG_TIMER_MAX_NUM; i++)
    {
        if(pEventList[i].enTimerType == EPG_EVENT_NONE)
        {
            break;
        }

        if(CheckEPGItemOverlap(pEventList[i], stTmpEPGEvent))
        {
            if ((EPG_EVENT_REMIDER == pEventList[i].enTimerType)
                && (pEventList[i].u32StartTime == stTmpEPGEvent.u32StartTime))
            {
                pEventList[i].u32StartTime += TIMER_REMINDER_CONFLICT_OFFSET;
                break;
            }
        }
    }

    ReconfigTimerFromList();
    return EPG_TIMER_CHECK_SUCCESS;
}

void MSrv_Timer::_TrackCRIDUpdate()
{
    BOOL bRet = MAPI_TRUE;
    U16 u16CRIDEventCount = 0;
    U32 u32size = 0;
    ST_EPG_EVENT_TIMER_INFO stEPGTimerEvent;
    ST_CRID_TIMER_INFO *pstCRID = NULL;
    EPGCridEventList_t rCRIDEventInfoList;
    //ST_CRID_TIMER_INFO astCRID[CRID_TIMER_MAX_NUM];  //25
    MSrv_DTV_Player_DVB* pPlayer = dynamic_cast<MSrv_DTV_Player_DVB*>(MSrv_Control::GetMSrvDtv());
    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();

    memset(&stEPGTimerEvent, 0, sizeof(stEPGTimerEvent));
    //memset(astCRID, 0, sizeof(astCRID));
    //pSysDB->GetCRIDTimerList(astCRID);

    u16CRIDEventCount = GetCRIDEventCount();
    pSysDB->UseCRIDTimerList(&pstCRID, &u32size);

    if (1000 < m_u32CRIDCheckTriggerCount)
    {
        ++m_u32CRIDCheckTriggerCount;
        return;
    }
    else
    {
        m_u32CRIDCheckTriggerCount = 0;
    }

    DelPastCRIDItems();

    for (U16 i = 0; i< u16CRIDEventCount; i++)
    {
        if (MAPI_FALSE == pstCRID[i].bValidFlag)
        {
            continue;
        }
        bRet = pPlayer->GetEventInfoByCRID(
                            pstCRID[i],
                            rCRIDEventInfoList);
        if (MAPI_FALSE == bRet)
        {
            continue;
        }
        EPGCridEventList_t::iterator itr = rCRIDEventInfoList.begin();
        for (U16 Index = 0; itr != rCRIDEventInfoList.end(); itr++,Index++)
        {
            stEPGTimerEvent.enTimerType           = EPG_EVENT_RECORDER;
            stEPGTimerEvent.enRepeatMode          = EPG_REPEAT_ONCE;
            stEPGTimerEvent.u32StartTime          = itr->m_EventInfo.u32UtcStartTime;
            stEPGTimerEvent.u32DurationTime       = itr->m_EventInfo.u32DurationTime;
            stEPGTimerEvent.u8ServiceType         = itr->m_u8ServiceType;
            stEPGTimerEvent.u16ServiceNumber      = itr->m_u16ServiceNumber;
            stEPGTimerEvent.u16EventID            = itr->m_EventInfo.u16eventId;
            stEPGTimerEvent.bIsEndTimeBeforeStart = FALSE;
            AddEPGEvent(stEPGTimerEvent);
            DelCRIDEvent(Index);
            PostEvent(0, EV_RCT_REFLESH, 0);
        }
    }
}

BOOL MSrv_Timer::GetEPGTimerNonRecordingFirstEvent(ST_EPG_EVENT_TIMER_INFO &stTimerInfo)
{
    ST_EPG_EVENT_TIMER_INFO *pEventList = NULL;
    U32 listSize = 0;
    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);
    mapi_scope_lock(scopeLock, &m_EpgTimerMutex);
    pSysDB->UseEpgTimerList(&pEventList, &listSize);
    for(U8 i =0; i< listSize ; i++)
    {
        BOOL bFound = FALSE;
        if(pEventList[i].enTimerType == EPG_EVENT_RECORDER)
        {
            if((pEventList[i].u32TimerId == m_RecordingProgram.u32TimerId)&&
                (m_EPGTimerRecordStatus == EN_EPGTIMER_RECORDER_TIMEUP_RECORDING))
                {
                    bFound = TRUE;
            }
        }
        else if((pEventList[i].enTimerType ==EPG_EVENT_NONE) ||(pEventList[i].enTimerType >=EPG_EVENT_MAX))
        {
            return FALSE;
        }

        if(bFound == FALSE)
        {
            stTimerInfo = pEventList[i];
            return TRUE;
        }
    }

    return FALSE;
}

typedef std::list<ST_DTV_EPG_CRID_EVENT_INFO> EPGCridEventList_t;

EPG_TIMER_CHECK MSrv_Timer::CheckCRIDInTimerList(const char *pcCRID)
{
    U32 u32NumofEvents = 0;

    if (NULL == pcCRID)
    {
        return  EPG_TIMER_CHECK_NONE;
    }

    u32NumofEvents = GetCRIDEventCount();

    // [1] Check if start timer is past && has empty space
    if(u32NumofEvents > CRID_TIMER_MAX_NUM)
    {
        return EPG_TIMER_CHECK_FULL;
    }

    // [2] Check if new timer is overlapping the existed timer list
    if (u32NumofEvents > 0)
    {
        MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
        ASSERT(pSysDB);
        ST_CRID_TIMER_INFO stCRIDTimerDB[CRID_TIMER_MAX_NUM];

        /* load EPG timer DB */
        mapi_scope_lock(scopeLock, &m_CRIDTimerMutex);
        pSysDB->GetCRIDTimerList(stCRIDTimerDB);

        for(U16 i = 0; i < u32NumofEvents; i++)
        {
            //check CRID conflict :
            if ((MAPI_TRUE == stCRIDTimerDB[i].bValidFlag)
                && (0 == memcmp(stCRIDTimerDB[i].au8CRID, pcCRID, 64))
            )
            {
                return EPG_TIMER_CHECK_SUCCESS;
            }
        }
    }
    else
    {
        return EPG_TIMER_CHECK_NONE;
    }

    return EPG_TIMER_CHECK_NONE;
}

BOOL MSrv_Timer::DelAllEventByMux(U8 u8RfNum)
{
    DelAllCRIDEvent();
    {
        MSrv_DTV_Player_DVB* pDVBPlayer = dynamic_cast<MSrv_DTV_Player_DVB*>(MSrv_Control::GetMSrvDtv());
        ASSERT(pDVBPlayer);
        MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
        ASSERT(pSysDB);

        ST_EPG_EVENT_TIMER_INFO *pEventList = NULL;
        U32 listSize = 0;

        mapi_scope_lock(scopeLock, &m_EpgTimerMutex);
        pSysDB->UseEpgTimerList(&pEventList, &listSize);

        U16 u16EPGTimerEventCount = GetEPGTimerEventCount();

        for(U16 i = 0 ; i < u16EPGTimerEventCount ; )
        {
            ST_DVB_MUX_INFO stMux;
            if((EPG_EVENT_REMIDER == pEventList[i].enTimerType)
                    || (EPG_EVENT_RECORDER == pEventList[i].enTimerType))
            {
                printf("sttest aaaaaaaaaaaaaaaaaaaaaaa \n\n");
                if(pDVBPlayer->GetMuxInfoByProgramNumber(pEventList[i].u16ServiceNumber, pEventList[i].u8ServiceType, stMux) == FALSE)
                {
                    i++;
                    continue;
                }

                printf("sttest u8RfNum %d ,%d \n",u8RfNum,stMux.u8RfNumber);

                if(u8RfNum == stMux.u8RfNumber )
                {
                    DelEPGEvent(i);
                    pSysDB->UseEpgTimerList(&pEventList, &listSize);
                    u16EPGTimerEventCount = GetEPGTimerEventCount();
                    i = 0;
                    continue;
                }
            }
            i++;
        }
    }

    ReconfigTimerFromList();
    return TRUE;
}

BOOL MSrv_Timer::DelAllCRIDEvent()
{
    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);
    U16 u16Index;
    ST_CRID_TIMER_INFO *pEventList = NULL;
    U32 listSize = 0;

    mapi_scope_lock(scopeLock, &m_CRIDTimerMutex);
    U16 iCRIDItemCount  = GetCRIDEventCount();
    pSysDB->UseCRIDTimerList(&pEventList, &listSize);

    if(iCRIDItemCount == 0)
    {
        return FALSE;
    }

    for(u16Index = 0 ; u16Index<iCRIDItemCount ; u16Index++)
    {
        memset(&(pEventList[u16Index]),0,sizeof(ST_CRID_TIMER_INFO));
        pEventList[u16Index].bValidFlag = MAPI_FALSE;
    }

    ReconfigCRIDFromList();
    return TRUE;
}

BOOL MSrv_Timer::DelCRIDEvent(U16 index)
{
    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);

    ST_CRID_TIMER_INFO *pEventList = NULL;
    U32 listSize = 0;

    mapi_scope_lock(scopeLock, &m_CRIDTimerMutex);
    U16 iCRIDItemCount  = GetCRIDEventCount();
    pSysDB->UseCRIDTimerList(&pEventList, &listSize);

    if((iCRIDItemCount == 0) || (index >= iCRIDItemCount))
    {
        return FALSE;
    }

    if(index < iCRIDItemCount)
    {
        memcpy(&(pEventList[index]), &(pEventList[iCRIDItemCount - 1]), sizeof(ST_CRID_TIMER_INFO));
    }
    pEventList[iCRIDItemCount - 1].bValidFlag = MAPI_FALSE;
    ReconfigCRIDFromList();

   // printf("[%s %d] pEventList[%u].bValidFlag=%u \n",__FUNCTION__,__LINE__,index,pEventList[index].bValidFlag);
   // printf("[%s %d] pEventList[%u - 1].bValidFlag=%u \n",__FUNCTION__,__LINE__,iCRIDItemCount,pEventList[iCRIDItemCount - 1].bValidFlag);

    return TRUE;
}

BOOL MSrv_Timer::GetCRIDTimerEventByIndex(ST_CRID_TIMER_INFO &stTimerInfo, U32 u32Index)
{
    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);

    ST_CRID_TIMER_INFO stTimerDB[CRID_TIMER_MAX_NUM];

    /* load EPG timer DB */
    mapi_scope_lock(scopeLock, &m_CRIDTimerMutex);
    pSysDB->GetCRIDTimerList(stTimerDB);

    for(U32 i = 0; i < CRID_TIMER_MAX_NUM; i++)
    {
        if(MAPI_FALSE == stTimerDB[i].bValidFlag)
        {
            continue;
        }
        if(i == u32Index)
        {
            stTimerInfo = stTimerDB[i];
            return TRUE;
        }
    }

    MTIMER_IFO("GetCRIDTimerEventByIndex idx[%d] \n", u32Index);
    return FALSE;
}

EPG_TIMER_CHECK MSrv_Timer::IsCRIDSettingValid(const ST_CRID_TIMER_INFO *stCRIDTimerInfo)
{
    U32 timeNow = 0;
    U32 u32ValidTime = 0;
    U32 u32NumofEvents = 0;

    //Get System Time Now
    timeNow = mapi_interface::Get_mapi_system()->RTCGetCLK();

    u32NumofEvents = GetCRIDEventCount();

    // [1] Check if start timer is past && has empty space
    if(u32NumofEvents > CRID_TIMER_MAX_NUM)
    {
        return EPG_TIMER_CHECK_FULL;
    }

    // [2] Check if new timer is overlapping the existed timer list
    if (u32NumofEvents > 0)
    {
        MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
        ASSERT(pSysDB);
        ST_CRID_TIMER_INFO *pEventList = NULL;
        U32 listSize = 0;

        /* load EPG timer DB */
        mapi_scope_lock(scopeLock, &m_CRIDTimerMutex);
        pSysDB->UseCRIDTimerList(&pEventList, &listSize);

        for(U16 i = 0; i < u32NumofEvents; i++)
        {
            //check CRID conflict :
            if ((MAPI_TRUE == pEventList[i].bValidFlag)
                && (0 == memcmp(pEventList[i].au8CRID, stCRIDTimerInfo->au8CRID, 64))
            )
            {
                u32ValidTime = pEventList[i].u32StartTime + SECONDS_PER_HALF_MIN;
                if (u32ValidTime > timeNow)
                {
                    pEventList[i].u32StartTime = stCRIDTimerInfo->u32StartTime;
                    pSysDB->SaveCRIDTimerSetting();
                }
                return EPG_TIMER_CHECK_OVERLAY;
            }
        }
    }

    return EPG_TIMER_CHECK_SUCCESS;
}

U16 MSrv_Timer::GetCRIDEventCount(void)
{
    U16 LoopIdx = 0;

    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);

    ST_CRID_TIMER_INFO *pEventList = NULL;
    U32 listSize = 0;

    mapi_scope_lock(scopeLock, &m_CRIDTimerMutex);
    pSysDB->UseCRIDTimerList(&pEventList, &listSize);

    for(LoopIdx = 0; LoopIdx < CRID_TIMER_MAX_NUM ; LoopIdx++)
    {
        if (MAPI_FALSE == pEventList[LoopIdx].bValidFlag)
        {
            break;
        }
    }

    return LoopIdx;
}

EPG_TIMER_CHECK MSrv_Timer::AddEPGEventByCRID(const ST_CRID_TIMER_INFO *pstCRIDTimerInfo)
{
    if (NULL == pstCRIDTimerInfo)
    {
        return EPG_TIMER_CHECK_NONE;
    }

//    pstCRIDTimerInfo->u32StartTime = mapi_interface::Get_mapi_system()->RTCGetCLK();
    EPG_TIMER_CHECK rtnCheck = IsCRIDSettingValid(pstCRIDTimerInfo);
    if(rtnCheck == EPG_TIMER_CHECK_OVERLAY)
    {
        return EPG_TIMER_CHECK_OVERLAY;
    }
    else if(rtnCheck != EPG_TIMER_CHECK_SUCCESS)
    {
        return rtnCheck;
    }

    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);


    ST_CRID_TIMER_INFO *pEventList = NULL;
    U32 listSize = 0;

    mapi_scope_lock(scopeLock, &m_CRIDTimerMutex);
    pSysDB->UseCRIDTimerList(&pEventList, &listSize);

    U16 iItemCount = GetCRIDEventCount();
    if(iItemCount >= CRID_TIMER_MAX_NUM)
    {
        return EPG_TIMER_CHECK_FULL;
    }
#if 0
    printf("[%s %d] pstCRIDTimerInfo->bValidFlag   = %u\n",__FUNCTION__,__LINE__,pstCRIDTimerInfo->bValidFlag);
    printf("[%s %d] pstCRIDTimerInfo->u16CRIDType  = %u\n",__FUNCTION__,__LINE__,pstCRIDTimerInfo->u16CRIDType);
    printf("[%s %d] pstCRIDTimerInfo->u32StartTime = %u\n",__FUNCTION__,__LINE__,pstCRIDTimerInfo->u32StartTime);
    printf("[%s %d] pstCRIDTimerInfo->au8CRID      = %s\n",__FUNCTION__,__LINE__,pstCRIDTimerInfo->au8CRID);
    printf("[%s %d] iItemCount      = %u \n",__FUNCTION__,__LINE__,iItemCount);
#endif

    //pEventList[iItemCount] = *pstCRIDTimerInfo;
    memcpy(&(pEventList[iItemCount]), pstCRIDTimerInfo, sizeof(ST_CRID_TIMER_INFO));

#if 0
    printf("[%s %d] pEventList[%u].bValidFlag   = %u\n",__FUNCTION__,__LINE__,iItemCount,pEventList[iItemCount].bValidFlag);
    printf("[%s %d] pEventList[%u].u16CRIDType  = %u\n",__FUNCTION__,__LINE__,iItemCount,pEventList[iItemCount].u16CRIDType);
    printf("[%s %d] pEventList[%u].u32StartTime = %u\n",__FUNCTION__,__LINE__,iItemCount,pEventList[iItemCount].u32StartTime);
    printf("[%s %d] pEventList[%u].au8CRID      = %s\n",__FUNCTION__,__LINE__,iItemCount,pEventList[iItemCount].au8CRID);
#endif

    if((iItemCount + 1) < CRID_TIMER_MAX_NUM)
    {
        pEventList[iItemCount+1].bValidFlag = MAPI_FALSE;
    }

    ReconfigCRIDFromList();

#if 0
    pSysDB->UseCRIDTimerList(&pEventList, &listSize);
    printf("\n[%s %d] pEventList[%u].bValidFlag   = %u\n",__FUNCTION__,__LINE__,iItemCount,pEventList[iItemCount].bValidFlag);
    printf("[%s %d] pEventList[%u].u16CRIDType  = %u\n",__FUNCTION__,__LINE__,iItemCount,pEventList[iItemCount].u16CRIDType);
    printf("[%s %d] pEventList[%u].u32StartTime = %u\n",__FUNCTION__,__LINE__,iItemCount,pEventList[iItemCount].u32StartTime);
    printf("[%s %d] pEventList[%u].au8CRID      = %s\n",__FUNCTION__,__LINE__,iItemCount,pEventList[iItemCount].au8CRID);
#endif
    return EPG_TIMER_CHECK_SUCCESS;
}

void MSrv_Timer::ReconfigCRIDFromList(U32 u32TimeActing, BOOL bCheckEndTime)
{
    U32 timeAct = ((u32TimeActing == 0) ? mapi_interface::Get_mapi_system()->RTCGetCLK() : u32TimeActing);
    DelPastCRIDItems(timeAct, bCheckEndTime);

    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    mapi_scope_lock(scopeLock, &m_CRIDTimerMutex);
    //DumpTimerList(m_EpgTimerMutex);
    pSysDB->SaveCRIDTimerSetting();
}

void MSrv_Timer::DelPastCRIDItems(U32 u32TimeActing, BOOL bCheckEndTime)
{
    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);

    ST_CRID_TIMER_INFO *pEventList = NULL;
    U32 listSize = 0;
    U32  timeNow = mapi_interface::Get_mapi_system()->RTCGetCLK();
    U32  u32ValidTime = 0;

    mapi_scope_lock(scopeLock, &m_CRIDTimerMutex);
    pSysDB->UseCRIDTimerList(&pEventList, &listSize);

    U16 u16CRIDTimerEventCount = GetCRIDEventCount();
    if(u16CRIDTimerEventCount == 0)
    {
        return;
    }

    for(; u16CRIDTimerEventCount > 0; u16CRIDTimerEventCount--)  //from last to first
    {
        u32ValidTime = pEventList[u16CRIDTimerEventCount - 1].u32StartTime + CRID_TIMER_VALID_DAY;
        if( (MAPI_TRUE == pEventList[u16CRIDTimerEventCount - 1].bValidFlag)
           && (u32ValidTime > timeNow )
         )
        {
            continue;
        }

        ResetCRIDListItem(u16CRIDTimerEventCount - 1);
    }
}

void MSrv_Timer::ResetCRIDListItem(U16 ItemIndex)
{
    U16 itemTotal = GetCRIDEventCount();
    if((ItemIndex >= itemTotal) || (itemTotal == 0))
    {
        return;
    }

    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);

    ST_CRID_TIMER_INFO *pEventList = NULL;
    U32 listSize = 0;

    mapi_scope_lock(scopeLock, &m_CRIDTimerMutex);
    pSysDB->UseCRIDTimerList(&pEventList, &listSize);

    //erase
    U16 curLastValid = itemTotal - 1;

    //pEventList[ItemIndex] = pEventList[curLastValid];
    memcpy(&(pEventList[ItemIndex]), &(pEventList[curLastValid]), sizeof(ST_CRID_TIMER_INFO));
    memset(&pEventList[curLastValid], 0x00, sizeof(ST_CRID_TIMER_INFO));
}

#endif

#if (PVR_ENABLE == 1 && ATSC_SYSTEM_ENABLE == 0)
BOOL MSrv_Timer::EPGTimerExecRecordStart(EN_PVR_STATUS *pRtnPvrResult)
{
    if(pRtnPvrResult == NULL)
    {
        return FALSE;
    }

    U16 u16CachedPinCode = INVALID_PIN_CODE;
    GetCurrentScheduleCachedPinCode(u16CachedPinCode);
    *pRtnPvrResult = ((MSrv_DTV_Player_DVB*)(MSrv_Control::GetMSrvDtv()))->RecordStart(u16CachedPinCode);
    if(*pRtnPvrResult == E_PVR_SUCCESS)
    {
        m_EPGTimerRecordStatus = EN_EPGTIMER_RECORDER_TIMEUP_RECORDING;
    }
    else
    {
        m_EPGTimerRecordStatus = EN_EPGTIMER_RECORDER_IDLE;
        StopEPGRecord();
    }

    return TRUE;
}
#endif

void _AddPaddingsToEpgEventTimerInfo(ST_EPG_EVENT_TIMER_INFO& stEpgTimerInfo)
{
#if (DVB_ENABLE==1)
    MS_PVR_SETTING stPvrSetting;
    MSrv_System_Database_DVB* pSysDB =
        dynamic_cast<MSrv_System_Database_DVB*>(MSrv_Control::GetInstance()->GetMSrvSystemDatabase());
    if(NULL == pSysDB)
    {
        return;
    }
    memset(&stPvrSetting, 0, sizeof(stPvrSetting));
    pSysDB->GetPvrSetting(&stPvrSetting);
    if(EPG_EVENT_RECORDER != stEpgTimerInfo.enTimerType)
    {
        return;
    }
    U32 u32StartPadding = stPvrSetting.u16ScheduledRecordStartPadding * SECONDS_PER_MIN;
    U32 u32BothPadding = u32StartPadding + stPvrSetting.u16ScheduledRecordEndPadding * SECONDS_PER_MIN;
    if(stEpgTimerInfo.u32StartTime >= u32StartPadding)
    {
        stEpgTimerInfo.u32StartTime -= u32StartPadding;
        stEpgTimerInfo.u32DurationTime += u32BothPadding;
    }
    else
    {
        printf("Error: can not add paddings: u32StartTime too small.\n");
    }
#endif
}

void _RemovePaddingsFromEpgEventTimerInfo(ST_EPG_EVENT_TIMER_INFO& stEpgTimerInfo)
{
#if (DVB_ENABLE==1)
    MS_PVR_SETTING stPvrSetting;
    MSrv_System_Database_DVB* pSysDB =
        dynamic_cast<MSrv_System_Database_DVB*>(MSrv_Control::GetInstance()->GetMSrvSystemDatabase());
    if(NULL == pSysDB)
    {
        return;
    }
    memset(&stPvrSetting, 0, sizeof(stPvrSetting));
    pSysDB->GetPvrSetting(&stPvrSetting);
    if(EPG_EVENT_RECORDER != stEpgTimerInfo.enTimerType)
    {
        return;
    }
    U32 u32StartPadding = stPvrSetting.u16ScheduledRecordStartPadding * SECONDS_PER_MIN;
    U32 u32BothPadding = u32StartPadding + stPvrSetting.u16ScheduledRecordEndPadding * SECONDS_PER_MIN;
    if(stEpgTimerInfo.u32DurationTime >= u32BothPadding)
    {
        stEpgTimerInfo.u32StartTime += u32StartPadding;
        stEpgTimerInfo.u32DurationTime -= u32BothPadding;
    }
    else
    {
        printf("Error: can not remove paddings: u32DurationTime too small.\n");
    }
#endif
}

BOOL MSrv_Timer::IsRiksTVMode(void)
{
#if (ATSC_SYSTEM_ENABLE == 0)
     if ((MSrv_Control::GetMSrvSystemDatabase()->GetSystemCountry() == E_NORWAY)
        &&  (MSrv_Control::GetInstance()->GetCurrentInputSource() == MAPI_INPUT_SOURCE_DTV))
     {
         MSrv_DTV_Player_DVB *pDVBPlayer = dynamic_cast<MSrv_DTV_Player_DVB*>(MSrv_Control::GetMSrvDtv());
         ASSERT(pDVBPlayer);
#if (PVR_ENABLE == 1)
         if((pDVBPlayer->IsRecording() == FALSE)
#if (OAD_ENABLE == 1)
                &&(pDVBPlayer->IsOadDownloading() == FALSE)
#endif

                )
        {
            return true;
        }
        else
#endif
        {
            return false;
        }
     }
     else
#endif
     {
        return false;
     }
}

BOOL MSrv_Timer::IsRiksTVSleepModeTimerTrigger(void)
{
    return m_bIsRiksTVSleepTimerTrigger;
}
void MSrv_Timer::StartRiksTVSleepModeTimerThreadMonitor(BOOL bOnOff)
{
    m_bIsRiksTVSleepTimerTrigger = bOnOff;

    //If RiksTV Sleep Timer is triggered, start to monitor key event
    if(m_bIsRiksTVSleepTimerTrigger == TRUE)
    {
        if (m_bIsRiksTVSleepTimerMonitorThreadCreate == FALSE)
        {
            pthread_t thread_id;
            pthread_attr_t thr_attr;
            pthread_attr_init(&thr_attr);
            pthread_attr_setstacksize(&thr_attr, PTHREAD_STACK_SIZE);
            int ret = pthread_create(&thread_id, &thr_attr, RiksTVSleepModeTimerThreadMonitor, this);
            if (ret)
            {
                ASSERT(0);
            }
            m_bIsRiksTVSleepTimerMonitorThreadCreate = TRUE;
        }
    }
}

void* MSrv_Timer::RiksTVSleepModeTimerThreadMonitor(void* ptr)
{
    MSrv_Timer * m_pTimer = (MSrv_Timer *)ptr;
    IDirectFBEventBuffer *input_events;
    IDirectFB *dfb = MSystem::GetInstance()->dfb;

    if (DFB_OK != dfb->CreateInputEventBuffer(dfb, DICAPS_KEYS, DFB_TRUE, &input_events))
    {
        printf("MWindowManager::Start() cannot create event buffer.\n");
        ASSERT(0);
    }

    while(m_pTimer->m_bIsRiksTVSleepTimerTrigger == TRUE)
    {
        m_pTimer = MSrv_Control::GetMSrvTimer();
        if(m_pTimer->m_bIsRiksTVSleepTimerCountDownMessageShow != TRUE)
        {
            input_events->WaitForEventWithTimeout(input_events, 0, 3000);
            if (DFB_OK == input_events->HasEvent(input_events))
            {
                //Reset timer
                MSrv_Timer::EN_SLEEP_TIME_STATE sleep;
                sleep = m_pTimer->GetSleeperState();
                m_pTimer->SetSleepModeTime(sleep);
                //clear key event
                input_events->Reset(input_events);
            }
        }
        else
        {
            break;
        }
    }
    m_pTimer->m_bIsRiksTVSleepTimerMonitorThreadCreate = FALSE;
    pthread_exit(NULL);
}

BOOL MSrv_Timer::IsTimerRecordingStartInMin(U32 u32Min)
{
    if(GetEPGTimerEventCount() == 0)
    {
        return FALSE;
    }

    MSrv_System_Database *pSysDB = MSrv_Control::GetInstance()->GetMSrvSystemDatabase();
    ASSERT(pSysDB);
    ST_EPG_EVENT_TIMER_INFO *pEventList = NULL;
    U32 listSize = 0;
    mapi_scope_lock(scopeLock, &m_EpgTimerMutex);
    pSysDB->UseEpgTimerList(&pEventList, &listSize);
    if(pEventList == NULL)
    {
        return FALSE;
    }

    if(EPG_EVENT_RECORDER == pEventList[0].enTimerType)
    {
        U32 U32CurrentTime = mapi_interface::Get_mapi_system()->RTCGetCLK();
        if(pEventList[0].u32StartTime <= U32CurrentTime + u32Min*60)
        {
            return TRUE;
        }
        else
        {
            return FALSE;
        }
    }
    else
    {
        return FALSE;
    }

    return FALSE;
}

#if (STR_ENABLE == 1)
void MSrv_Timer::SetIsTVReallyPowerOff(BOOL bReallyOff)
{
    m_bIsTVReallyPowerDown = bReallyOff;
    printf(" [TV PW Timer] Set really pwoff = %u\n", m_bIsTVReallyPowerDown);
}

BOOL MSrv_Timer::GetIsTVReallyPowerOff(void)
{
    printf(" [TV PW Timer] really pwoff = %u\n", m_bIsTVReallyPowerDown);
    return m_bIsTVReallyPowerDown;
}

BOOL MSrv_Timer::GetTVPowerOffFlag(void)
{
    return m_bTVPowerOffFlag;
}

void MSrv_Timer::SetTVPowerDoneTimer(U32 u32Secs)
{
    U32 timebase;
    if(u32Secs == 0xFFFFFFFF)
    {
        printf(" [TV PW Timer] Reset timer\n");
        m_bIsTVReallyPowerDown = FALSE;
        m_bTVPowerOffFlag = FALSE;
        m_u32TVPowerOffTimeRemain = SEC_THREADING_PENDING;
        m_u32TVPowerOffTimeSecs = SEC_THREADING_PENDING;
    }
    else
    {
        printf(" [TV PW Timer] Start to count down 1min\n");
        timebase = mapi_time_utility::GetTime0() / SECOND_TO_MS;
        m_u32TVPowerOffTimeSecs = timebase + u32Secs;
        m_bTVPowerOffFlag = TRUE;
    }
}
#endif
#if (MSTAR_TVOS == 0)
BOOL MSrv_Timer::OnNetworkControlEvent(void* arg1, void* arg2, void* arg3)
{
    int netCtrlEvt=(int)arg2;

    if(netCtrlEvt == E_MSRV_NETWORK_CONTROL_EVENT_WAN_READY)
    {
        if(m_enLinuxTimeSource == E_LINUX_TIME_FROM_NTP)
        {
            m_Ntp_Entity_Obj->UpdateSysTimeViaNetwork();
        }
    }
    return TRUE;
}
#endif

BOOL MSrv_Timer::SetLinuxTimeSource(const EN_LINUX_TIME_SOURCE eLinuxTimeSource)
{
    if(eLinuxTimeSource == E_LINUX_TIME_FROM_NTP)
    {
#if(MSTAR_TVOS == 0)
        m_Ntp_Entity_Obj->UpdateSysTimeViaNetwork();
#endif
    }
    else
    {
        time_t timep;
        timep = mapi_interface::Get_mapi_system()->RTCGetCLK();
        stime(&timep);
    }

    m_enLinuxTimeSource = eLinuxTimeSource;
    StoreTimerDB();
    return TRUE;
}
EN_LINUX_TIME_SOURCE MSrv_Timer::GetLinuxTimeSource(void)
{
    return m_enLinuxTimeSource;
}

void MSrv_Timer::RTCSetCLK(const U32 u32secs)
{
    mapi_system * system = mapi_interface::Get_mapi_system();

    if(system)
    {
        system->RTCSetCLK(u32secs);

        if(m_enLinuxTimeSource == E_LINUX_TIME_FROM_DTV)
        {
            time_t timep;
            timep = u32secs;
            stime(&timep);
        }
    }
}
