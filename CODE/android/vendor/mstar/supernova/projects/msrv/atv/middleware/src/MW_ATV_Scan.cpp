
// headers of itself
#include "MW_ATV_Scan.h"

// headers of standard C libs
#include <unistd.h>
#include <limits.h>
#include <sys/prctl.h>

// headers of standard C++ libs

// headers of the same layer's
#include "MSrv_ATV_Player.h"
#include "MSrv_System_Database.h"
#include "MSrv_Control.h"
#include "MEvent.h"

#if (ATSC_SYSTEM_ENABLE == 1)
#include "MSrv_ChannelManager_ATSC.h"
#else
#include "MSrv_ChannelManager.h"
#endif


#if (TTX_ENABLE == 1)
#include "MW_TTX.h"
#endif

// headers of underlying layer's
#include "mapi_vif.h"
#include "mapi_utility.h"
#include "mapi_pcb.h"
#include "mapi_vd.h"
#include "mapi_video_vd_cfg.h"
#include "mapi_interface.h"
#include "mapi_cni.h"
#include "SystemInfo.h"
#if (STR_ENABLE == 1)
#include "mapi_str.h"
#endif

//Debug Message
#if 0
#define ATV_Scan_DBG(fmt, arg...)           printf((char *)fmt, ##arg)
#define ATV_Scan_LOG(file, fmt, arg...)     //fprintf(file,(char *)fmt, ##arg)
#define ATV_Scan_LOG_FILE(x)               //x
#define ATV_Scan_FLOW_DBG(fmt, arg...)      printf((char *)fmt, ##arg)
#define ATV_Scan_IFO(fmt, arg...)           printf((char *)fmt, ##arg)
#else
#define ATV_Scan_DBG(fmt, arg...)
#define ATV_Scan_LOG(file, fmt, arg...)
#define ATV_Scan_LOG_FILE(x)
#define ATV_Scan_FLOW_DBG(fmt, arg...)
#define ATV_Scan_IFO(fmt, arg...)
#endif

#define SCAN_DEBUG_ENABLE "/Customer/scan_debug"
BOOL MW_ATV_Scan::m_bDebugEnable = FALSE;
U32 MW_ATV_Scan::m_U32TimeOnSleep = 0;
U32 MW_ATV_Scan::m_U32TimeOnScan = 0;
U32 MW_ATV_Scan::m_U32TimeOnI2C = 0;
U32 MW_ATV_Scan::m_U32NumOfScan = 0;
U32 MW_ATV_Scan::m_U32ATVTimerTotal = 0;
U32 MW_ATV_Scan::m_U32ATVTimerMAX[] = {0,};
U32 MW_ATV_Scan::m_U32ATVTimerEachTotal[] = {0,};
U32 MW_ATV_Scan::m_U32ATVTimer[] = {0,};

#define ATV_Scan_PRINT(fmt, arg...)           if(TRUE == MW_ATV_Scan::m_bDebugEnable){printf((char *)fmt, ##arg);}

void MW_ATV_Scan::_ClearAllTimer()
{
    int idx=0;
    ATV_Scan_PRINT("%s: at %d\n", __func__, __LINE__);
    for(idx=0; idx<(int)SCAN_TIMER_MAX; idx++)
    {
        m_U32ATVTimer[idx] = 0;
        m_U32ATVTimerMAX[idx] = 0;
        m_U32ATVTimerEachTotal[idx] = 0;
    }
    m_U32ATVTimerTotal = 0;
}

void MW_ATV_Scan::_DisplayAllTimer()
{
    int idx=0;
    ATV_Scan_PRINT("%s: at %d\n", __func__, __LINE__);
    for(idx=0; idx<(int)SCAN_TIMER_MAX; idx++)
    {
        if(m_U32ATVTimerMAX[idx] < m_U32ATVTimer[idx])
        {
            m_U32ATVTimerMAX[idx] = m_U32ATVTimer[idx];
        }
        m_U32ATVTimerTotal += m_U32ATVTimer[idx];
        m_U32ATVTimerEachTotal[idx] += m_U32ATVTimer[idx];
        printf("time(%02u): %04u / %04u, (%08u)\n", idx, m_U32ATVTimer[idx], m_U32ATVTimerMAX[idx], m_U32ATVTimerEachTotal[idx]);
        m_U32ATVTimer[idx] = 0;
    }
    ATV_Scan_PRINT("%s: m_U32ATVTimerTotal: %u, at %d\n", __func__, m_U32ATVTimerTotal, __LINE__);
}

void MW_ATV_Scan::TunerSetIF(U8 u8ATVScanSoundSystemType)//;?????;;It's Sound System, need to change name?
{
    AUDIOSTANDARD_TYPE_ eAudioStandard;

    if(u8ATVScanSoundSystemType == TV_SOUND_LL)
    {
        //;;?????         SECAM_L_PRIME_ON(); // SAW GPIO config
        m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_LP_);
    }
    else if(u8ATVScanSoundSystemType == TV_SOUND_L)
    {
        m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_L_);
    }
    else
    {
        //;;?????          SECAM_L_PRIME_OFF();
        eAudioStandard = mapi_interface::Get_mapi_audio()->SIF_GetAudioStandard();
#if (ISDB_SYSTEM_ENABLE == 1)
        m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_);
        UNUSED(eAudioStandard);
       // ATV_PLAYER_DBG("\n    IF_FREQ_MN\n");
#else
    switch(eAudioStandard)
    {
        case E_AUDIOSTANDARD_BG_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_BG_);
            break;

        case E_AUDIOSTANDARD_NOTSTANDARD_:
        case E_AUDIOSTANDARD_BG_A2_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_BG_A2_);
            break;
        case E_AUDIOSTANDARD_BG_NICAM_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_BG_NICAM_);
            break;
        case E_AUDIOSTANDARD_I_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_I_);
            break;
        case E_AUDIOSTANDARD_DK_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_DK_);
            break;
        case E_AUDIOSTANDARD_DK1_A2_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_DK1_A2_);
            break;
        case E_AUDIOSTANDARD_DK2_A2_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_DK2_A2_);
            break;
        case E_AUDIOSTANDARD_DK3_A2_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_DK3_A2_);
            break;
        case E_AUDIOSTANDARD_DK_NICAM_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_DK_NICAM_);
            break;
        case E_AUDIOSTANDARD_L_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_L_);
            break;
        case E_AUDIOSTANDARD_M_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_);
            break;
        case E_AUDIOSTANDARD_M_BTSC_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_BTSC_);
            break;
        case E_AUDIOSTANDARD_M_A2_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_A2_);
            break;
        case E_AUDIOSTANDARD_M_EIA_J_:
            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_EIA_J_);
            break;
        }

#endif
    }
}

void MW_ATV_Scan::VifSetFrequency(U32 u32Freq_KHz, AUDIOSTANDARD_TYPE_ OtherMode)
{
    U32 tunerPLL;
    RFBAND band;
    EN_FREQ_STEP eFreqStep = E_FREQ_STEP_INVALD;

    //HERE convert freq to PLL
    tunerPLL = u32Freq_KHz;
    //update m_u16TunerPLL
    MSrv_Control::GetMSrvAtv()->m_u16TunerPLL = MW_ATV_Util::GetInstance()->ConvertFrequncyHzToPLL(u32Freq_KHz * 1000);

    m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_FREQ_STEP, 0, 0, &eFreqStep);

    switch(eFreqStep)
    {
        case E_FREQ_STEP_31_25KHz:
            tunerPLL = (tunerPLL * 100) / 3125;
            break;
        case E_FREQ_STEP_50KHz:
            tunerPLL = tunerPLL / 50;
            break;
        case E_FREQ_STEP_62_5KHz:
            tunerPLL = (tunerPLL * 10) / 625;
            break;
        default:
            ASSERT(0);
            break;
    }


    band = MW_ATV_Util::GetInstance()->GetBand(tunerPLL);

    EN_TUNER_MODE eTunerMode = E_TUNER_INVALID;
    if(SystemInfo::GetInstance()->get_ATVSystemType() == E_NTSC_ENABLE)
    {
            eTunerMode = E_TUNER_ATV_NTSC_MODE;
    }
    else
    {
        if(m_bIsLLSearch)
        {
            eTunerMode = E_TUNER_ATV_SECAM_L_PRIME_MODE;
        }
        else
        {
            eTunerMode = E_TUNER_ATV_PAL_MODE;
        }
    }

    mapi_interface::Get_mapi_pcb()->EnableTunerI2cPath(TRUE);
    if(OtherMode == E_AUDIOSTANDARD_NOTSTANDARD_)
    {
#if ( ATSC_SYSTEM_ENABLE == 1 || ESASIA_NTSC_SYSTEM_ENABLE == 1 || ISDB_SYSTEM_ENABLE == 1 )
        // NTSC default audio standard: M BTSC
        m_pTuner->ATV_SetTune(u32Freq_KHz, band, eTunerMode, E_AUDIOSTANDARD_M_BTSC_);
#else
        // PAL/SECAM default audio standard: DK
        m_pTuner->ATV_SetTune(u32Freq_KHz, band, eTunerMode, E_AUDIOSTANDARD_DK_);
#endif
    }
    else
    {
        m_pTuner->ATV_SetTune(u32Freq_KHz, band, eTunerMode, OtherMode);
    }
    mapi_interface::Get_mapi_pcb()->EnableTunerI2cPath(FALSE);

    //Sync with yellow flag: CL823423. it's for getting satble FOE value per VIF team's suggestion.
    //It depends on each project's need to choose enable or disable, and the delay value can be fine-tuned.
    #if 0
        usleep(1000*100);  //waiting for AFC stable, this value is needed to fine-tune with different platform
    #endif
    //msAPI_Tuner_SetIF();
}

void MW_ATV_Scan::VifSetSoundStandard(U8 u8ATVScanSoundSystemType)
{
    U16 u16IFFreqKHz = 0;

    if(u8ATVScanSoundSystemType == TV_SOUND_LL)
    {
        m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_L_PRIME_IF_FREQ, 0, 0, &u16IFFreqKHz);
        ATV_Scan_DBG("\r\n IFFreqKHz (SECAM LL)= %u", u16IFFreqKHz);
//        printf("\r\n IFFreqKHz (SECAM LL)= %u", u16IFFreqKHz);
        m_pDemodulator->ATV_SetVIF_IfFreq(u16IFFreqKHz);
        usleep(10 * 1000);
    }
    else
    {
        m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_IF_FREQ, 0, 0, &u16IFFreqKHz);
        ATV_Scan_DBG("\r\n IFFreqKHz = %u", u16IFFreqKHz);
//        printf("\r\n IFFreqKHz = %u", u16IFFreqKHz);
        m_pDemodulator->ATV_SetVIF_IfFreq(u16IFFreqKHz);
        usleep(10 * 1000);
    }

    TunerSetIF(u8ATVScanSoundSystemType);
}

void MW_ATV_Scan::SetSoundSystem(AUDIOSTANDARD_TYPE_ eStandard)
{
   U8 u8ATVScanSoundSystemType = TV_SOUND_DK;

   mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard(eStandard);
    switch(mapi_interface::Get_mapi_audio()->SIF_ConvertToBasicAudioStandard(eStandard))
   {
    case E_AUDIOSTANDARD_BG_:
            u8ATVScanSoundSystemType = TV_SOUND_BG;
            break;

    case E_AUDIOSTANDARD_I_:
            u8ATVScanSoundSystemType = TV_SOUND_I;
            break;

    case E_AUDIOSTANDARD_DK_:
            u8ATVScanSoundSystemType = TV_SOUND_DK;
            break;

    case E_AUDIOSTANDARD_L_:
            u8ATVScanSoundSystemType = TV_SOUND_L;
            break;

    case E_AUDIOSTANDARD_M_:
            u8ATVScanSoundSystemType = TV_SOUND_M;
            break;

    default:
        break;
   }

    VifSetSoundStandard(u8ATVScanSoundSystemType);

}

void MW_ATV_Scan::SetMediumAndChannelNumber(U8 u8ProgramNumber, ATV_UTIL_MEDIUM eMedium, U8 cChannelNumber)
{
    ST_ATV_MISC Misc;

    if(u8ProgramNumber > MSrv_Control::GetMSrvAtvDatabase()->ATVGetChannelMax())
    {
        return;
    }

    if(true == MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_MISC , u8ProgramNumber , 0 , (U8 *)&Misc))
    {
        if(eMedium == ATV_UTIL_MEDIUM_CABLE)
        {
            Misc.eMedium = MSrv_ATV_Database::MEDIUM_CABLE;
        }
        else if(eMedium == ATV_UTIL_MEDIUM_AIR)
        {
            Misc.eMedium = MSrv_ATV_Database::MEDIUM_AIR;
        }
        else
        {
            ASSERT(0);
        }
        Misc.u8ChannelNumber = cChannelNumber;

        if(TRUE != MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_MISC , u8ProgramNumber , 0 , (U8 *)&Misc))
        {
            ASSERT(0);
        }
    }
}

void MW_ATV_Scan::TunerConvertMediumAndChannelNumberToString(ATV_UTIL_MEDIUM eMedium, U8 u8ChannelNumber, U8 * sStationName)
{
#if (ATSC_SYSTEM_ENABLE == 0)//atv name is not necessary when ATSC
    if(eMedium == ATV_UTIL_MEDIUM_AIR)
    {
        sStationName[0] = 'C';    // Air
    }
    else
    {
        sStationName[0] = 'S';    // Cable
    }

    sStationName[1] = '-';
    sStationName[2] = (u8ChannelNumber / 10) + '0';
    sStationName[3] = (u8ChannelNumber % 10) + '0';
    sStationName[4] = ' ';
    sStationName[5] = '\0';
#endif
}


//-----------------------------------------------------------------------------
MW_ATV_Scan::MW_ATV_Scan()
{
    m_pDemodulator = mapi_interface::Get_mapi_pcb()->GetAtvDemod(0);
    ASSERT(m_pDemodulator != NULL);
    m_pTuner = mapi_interface::Get_mapi_pcb()->GetAtvTuner(0);
    ASSERT(m_pTuner != NULL);

    m_eMedium = ATV_UTIL_MEDIUM_CABLE;

    memset(&m_AtvScanThread , 0, sizeof(pthread_t));
    m_bATVScanThreadEnable = FALSE;
    memset(m_au8CurrentStationName, 0 , sizeof(m_au8CurrentStationName));
    m_bIsLLSearch = FALSE;
    m_bScanContinue = FALSE;
    //m_u16TunerPLL = 0;
    m_u32StartFreq = 0;
    m_IsLocked = FALSE;
    m_bIsFranceSearch = FALSE;
    m_u8SortingPriority = LOWEST_SORTING_PRIORITY;
    m_bCNIStatus = FALSE;
    m_bStartJoinThread = FALSE;
    m_u32AutoScanFreq = 0;
    m_u32PrevScanFreq = 0;
    m_u8ChannelCount = CHANNEL_MIN_NUM;
    m_AutoScanState = AUTO_SCAN_ATV;

    m_IsManualScan = FALSE;
    m_bIsFranceSearch = FALSE;
    m_manualSearchType = SearchUp;

    m_eAirCableSelect = MSrv_ATV_Database::MEDIUM_AIR;
    m_eNTSCCableType = NTSC_CABLE_STD;
    m_u8AutoDebounce = 0;
    m_u8RFDebounce = 0;
    m_u8AutoScanChannel  = 0;
    m_u8AutoScanChannelEnd  = 0;
    m_u8AutoScanChannelStart = 0;
    m_u8AutoStep = 0;
    m_u8TunerOffset = 0;
    m_u8TunerSignal = 0;
    MW_ATV_Util::GetInstance()->Init();

    m_u8RFShiftStep = 0;
    m_ScanDetectedAudioType = E_AUDIOSTANDARD_BG_;
    m_ScanDetectedVideoType = 0;
    m_u32EndFreq = 0;
    m_u8OrgRFFreq = 0;
#if (MSTAR_TVOS == 1)
    m_u32BreakPointFreq = 0;
#endif
    m_bDebugEnable = FALSE;

    // Get min. step of Tuner freq.
    EN_FREQ_STEP eFreqStep = E_FREQ_STEP_INVALD;
    m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_FREQ_STEP, 0, 0, &eFreqStep);
    switch(eFreqStep)
    {
        case E_FREQ_STEP_31_25KHz:
            m_u8TunerMinDistance = 31;
            break;
        case E_FREQ_STEP_50KHz:
            m_u8TunerMinDistance = 50;
            break;
        case E_FREQ_STEP_62_5KHz:
            m_u8TunerMinDistance = 62;
            break;
        default:
            m_u8TunerMinDistance = 62;
            printf("\nTuner min distance is invald, use default distance 62KHz.\n");
            break;
    }

    //assign small step depend on tuner min distance
    if(m_pDemodulator->ATV_IsInternalVIF())
    {
        m_u32FreqSmallStep = m_u8TunerMinDistance * TUNER_TWO_STEP;
    }
    else
    {
        m_u32FreqSmallStep = m_u8TunerMinDistance * TUNER_FOUR_STEP;
    }

    m_U32TimeOnSleep=0;
    m_U32TimeOnScan=0;
    m_U32TimeOnI2C=0;
    m_U32NumOfScan=0;

    memset(m_U32ATVTimerMAX, 0, sizeof(m_U32ATVTimerMAX));
    memset(m_U32ATVTimerEachTotal, 0, sizeof(m_U32ATVTimerMAX));
    memset(m_U32ATVTimer, 0, sizeof(m_U32ATVTimerMAX));
    m_U32ATVTimerTotal = 0;
}

MW_ATV_Scan::~MW_ATV_Scan()
{
    Stop();
}

BOOL MW_ATV_Scan::Init(void)
{
    return DoInit();
}

U8 MW_ATV_Scan::CheckAfcWinPass(mapi_demodulator_datatype::AFC wValue)
{
    U8 ucRet = 0;

    if(wValue <= mapi_demodulator_datatype::E_AFC_BELOW_MINUS_187p5KHz)  // Signed bit: Minus
    {
        // < Target Freq, need increase
        if((wValue <= mapi_demodulator_datatype::E_AFC_MINUS_62p5KHz) && (wValue >= mapi_demodulator_datatype::E_AFC_MINUS_12p5KHz))  // -50 / -62.5k
        {
            ucRet = AFC_GOOD;
        }
        else if(wValue < mapi_demodulator_datatype::E_AFC_BELOW_MINUS_187p5KHz)  // -187.5k
        {
            ucRet = AFC_INCREASE;
        }
        else
        {
            ucRet = AFC_BIG_STEP_INC;
        }
    }
    else
    {
        // > Target Freq, need decrease
        if((wValue <= mapi_demodulator_datatype::E_AFC_PLUS_12p5KHz) && (wValue >= mapi_demodulator_datatype::E_AFC_PLUS_62p5KHz))  // 50 / 62.5k
        {
            ucRet = AFC_GOOD;
        }
        else if(wValue >mapi_demodulator_datatype::E_AFC_ABOVE_PLUS_187p5KHz)  // 187.5k
        {
            ucRet = AFC_DECREASE;
        }
        else
        {
            ucRet = AFC_BIG_STEP_DEC;
        }
    }

    return ucRet;
}

U32 MW_ATV_Scan::GetCurrentFreq()
{
    return m_u32AutoScanFreq;
}
void MW_ATV_Scan::SetupAutoScanPara(AUDIOSTANDARD_TYPE_ ucSoundSystem)
{
    mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard((AUDIOSTANDARD_TYPE_)ucSoundSystem);
    m_u32AutoScanFreq = PAL_FREQ_MIN;
#if (MSTAR_TVOS == 1)
    m_u32BreakPointFreq = PAL_FREQ_MIN;
#endif

}

//-----------------------------------------------------------------------------

U8 MW_ATV_Scan::NowChannel()
{
    return m_u8AutoScanChannel;
}
#if (ATSC_SYSTEM_ENABLE == 1)
U8 MW_ATV_Scan::IsManualScan()
{
    return (m_IsManualScan&&m_bATVScanThreadEnable);
}
U8 MW_ATV_Scan::StartChannel()
{
    return m_u8AutoScanChannelStart;
}
#endif
///////////////////////////////////////////////////////////////////////////////

BOOL MW_ATV_Scan::_IsScanDebugEnable()
{
    FILE *fp=NULL;
    fp = fopen(SCAN_DEBUG_ENABLE, "r");
    if(fp !=NULL)
    {
        fclose(fp);
        return TRUE;
    }
    return FALSE;
}

void * MW_ATV_Scan::ScanThreadFunc(void *arg)
{
    //U32 u32ProgramCount = 0;

    //MW_DTV_SCAN_FUNCTION("MW_DTV_Scan::ScanThreadFunc \n");
    prctl(PR_SET_NAME, (unsigned long)"MW ATV SCAN Task");
#if (STR_ENABLE == 1)
    mapi_str::AutoRegister _R;
#endif
    ATV_Scan_DBG("Enter ThreadFunc\n");

#if (AUTO_TEST == 1)
    printf("[AT][SN][ATV_AutoScanCmd START][%u]\n", mapi_time_utility::GetTime0());
#endif

    MW_ATV_Scan *_this = (MW_ATV_Scan *) arg;
    ASSERT(_this != NULL);
    m_bDebugEnable = _IsScanDebugEnable();
    MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u16ScannedChannelNum = MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_NON_SKIP_PROGRAM_COUNT, 0, 0, NULL); //@FIXME I think total non skip count should not re-calculte during scan
    _this->DoScan();

    // for update program count
    //MSrv_Control_common::GetMSrvChannelManager()->GetProgramCount(&u32ProgramCount);


    // After Auto-scan, set current program number to ATV_FIRST_PR_NUM

#if (ISDB_SYSTEM_ENABLE == 1)
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER , MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CHANNEL_MIN , 0, 0, NULL) - 1  , 0 , NULL);
#elif (ESASIA_NTSC_SYSTEM_ENABLE == 1)
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER , MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_FIRST_PROGRAM_NUMBER , 0, 0, NULL)  , 0 , NULL);
#else
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER , ATV_FIRST_PR_NUM , 0 , NULL);
#endif

#if (AUTO_TEST == 1)
    printf("[AT][SN][ATV_AutoScanCmd END][%u]\n", mapi_time_utility::GetTime0());
#endif

    if(! _this->m_bStartJoinThread)
    {
        ATV_Scan_FLOW_DBG("pthread_detach.....\n");
        pthread_detach(pthread_self());
    }

    pthread_exit(NULL);
}

void MW_ATV_Scan::Stop(void)
{
    //    ATC_scan_DEBUGINFO(printf("mbrg_ATV_scan_Stop\n");)
    if(m_bATVScanThreadEnable)
    {
        m_bStartJoinThread = TRUE;
        m_bATVScanThreadEnable = FALSE;
        void *thread_result;
        int intPTHChk;
        ATV_Scan_FLOW_DBG("pthread start to join.....\n");

        intPTHChk = PTH_RET_CHK(pthread_join(m_AtvScanThread, &thread_result));
        if(intPTHChk != 0)
        {
            perror("thread Atv Scan Thread join failed");
            ASSERT(0);
        }
        else
        {
            ATV_Scan_IFO("Exit Atv Scan Thread Success.\n");
        }

        //pthread_kill(m_AtvScanThread,0);
        //Thread_callback(Thread_handle,ICE_Scan_Finished,&tuned_params,Thread_user_data);
        m_AtvScanThread = 0;
        m_bStartJoinThread = FALSE;
        MSrv_Control::GetMSrvAtv()->StopEvenTimer();
    }
}

void MW_ATV_Scan::ChannelSortByPri(U8 ucRange)   // including NTSC check
{
    U8 i, j;
    //U8 chIdx1, chIdx2;
    U8 SortPriCh1, SortPriCh2;
    ST_ATV_MISC Misc;
    //U8 chIdx10,k;
    //U8 SortPriCh10;

    if(ucRange == 0)
    {
        return;
    }

    ucRange += CHANNEL_MIN_NUM;

    for(i = CHANNEL_MIN_NUM; i < (ucRange - 1); ++ i)
    {
        //chIdx1 = PAL_LoadChannelIdx( i );
        //chIdx1 = MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_CHANNEL_INDEX, i , NULL , (U8 *)&Misc);
        for(j = i + 1; j < ucRange; ++ j)
        {
            //chIdx2 = PAL_LoadChannelIdx( j );
            //chIdx2 = MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_CHANNEL_INDEX, j , NULL , (U8 *)&Misc);

            SortPriCh1 = MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_SORTING_PRIORITY, i , 0 , (U8 *)&Misc);
            SortPriCh2 = MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_SORTING_PRIORITY , j , 0 , (U8 *)&Misc);
            if(SortPriCh1 > SortPriCh2)
            {
                MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SWAP_PROGRAM , i, j, NULL);
                //chIdx1 = MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_CHANNEL_INDEX, i , NULL , (U8 *)&Misc);
                //chIdx1 = chIdx2;
            }
            else if(SortPriCh1 == SortPriCh2)
            {
                char name1[MAX_STATION_NAME] = {0};
                char name2[MAX_STATION_NAME] = {0};
                MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_STATION_NAME, i, 0, name1);
                MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_STATION_NAME, j, 0, name2);
                if(strcmp(name1, name2) > 0)
                {
                    MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SWAP_PROGRAM , i, j, NULL);
                }
            }
        }
    }

    /*
        for(k=0;k<ucRange;k++
    )
        {
            //MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl( SET_CURRENT_PROGRAM_NUMBER , k , NULL , NULL );
            chIdx10 = MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_CHANNEL_INDEX, k , NULL , (U8 *)&Misc);
            SortPriCh10 = MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_SORTING_PRIORITY, k , NULL , (U8 *)&Misc);
            printf("\r\n  --- k=%d   chIdx:%d   Prioryty:%d",k,chIdx10,SortPriCh10);
        }
    */
}

BOOL MW_ATV_Scan::Pause(void)
{
    // ATC_scan_DEBUGINFO(printf("mbrg_ATV_scan_Stop\n");)
    m_bScanContinue = FALSE;
    return TRUE;
}

BOOL MW_ATV_Scan::Resume(void)
{
    // ATC_scan_DEBUGINFO(printf("mbrg_ATV_scan_Stop\n");)
    m_bScanContinue = TRUE;
    return TRUE;
}

void * MW_ATV_Scan::ManualScanThreadFunc(void *arg)
{
    //MW_DTV_SCAN_FUNCTION("MW_DTV_Scan::ScanThreadFunc \n");
    prctl(PR_SET_NAME, (unsigned long)"MW ATV Manual SCAN Task");
    ATV_Scan_DBG("Enter Manual ThreadFunc\n");
    MW_ATV_Scan *_this = (MW_ATV_Scan *) arg;
    ASSERT(_this != NULL);

    m_bDebugEnable = _IsScanDebugEnable();
    _this->DoScan();

    if(! _this->m_bStartJoinThread)
    {
        ATV_Scan_FLOW_DBG("pthread_detach.....\n");
        pthread_detach(pthread_self());
    }

    pthread_exit(NULL);
}

BOOL MW_ATV_Scan::IsScanning()
{
    return m_bATVScanThreadEnable;
}

/*
 ********************************************
 FUNCTION   : GetScanProgressPercent
 USAGE      :
 INPUT      : None
 OUTPUT     : None
 ********************************************
*/
U8 MW_ATV_Scan::GetScanProgressPercent(void)
{
    U8 u8Percent;
    U32 u32UhfMaxFreq;
    u8Percent = 0;

    mapi_tuner* pTuner;
    pTuner = mapi_interface::Get_mapi_pcb()->GetAtvTuner(0);
    ASSERT(pTuner);

    pTuner->ExtendCommand(
        mapi_tuner_datatype::E_TUNER_SUBCMD_GET_UHF_MAX_FREQ,
        0,
        0,
        &u32UhfMaxFreq);

    if((m_IsManualScan == TRUE) && (IsSearched()))
    {
        return 100;
    }

    if(m_IsManualScan == TRUE)
    {
        if(m_manualSearchType == SearchUp)
        {
            if((m_u32StartFreq ) <= 48250 )
            {
                if((GetCurrentFreq() ) == (u32UhfMaxFreq ))
                {
                    printf("%s() LINE=%d\n",__func__,__LINE__);
                    return 100;
                }
            }
            else
            {
                if((GetCurrentFreq()/1000 ) == (m_u32StartFreq /1000 - 1))
                {
                    printf("%s() LINE=%d m_u32StartFreq =%d , Curent=%d\n",__func__,__LINE__,(int)m_u32StartFreq,(int)GetCurrentFreq());
                    return 100;
                }
            }
        }
        else
        {
            if((m_u32StartFreq ) >= (u32UhfMaxFreq ))
            {
                if((GetCurrentFreq() ) <= 48250 )
                {
                    printf("%s() LINE=%d\n",__func__,__LINE__);
                    return 100;
                }
            }
            else
            {
                if((GetCurrentFreq()/1000 ) == (m_u32StartFreq/1000 + 1))
                {
                    printf("%s() LINE=%d\n",__func__,__LINE__);
                    return 100;
                }
            }
        }
        return 0;
    }

    //AUTO SCAN Case

    u8Percent = (U8)((((GetCurrentFreq() - m_u32StartFreq) + 1) * 100) / ((m_u32EndFreq + 4000) - m_u32StartFreq)) ;

    if((m_bIsFranceSearch == TRUE) && (MSrv_Control::GetMSrvAtv()->m_TV_SCAN_PAL_SECAM_ONCE == FALSE))
    {
        if(mapi_interface::Get_mapi_audio()->SIF_GetAudioStandard() == E_AUDIOSTANDARD_L_)
        {
            u8Percent = u8Percent / 2;
        }
        else
        {
            u8Percent = 50 + (u8Percent / 2);
        }
    }
    return u8Percent;
}


//-----------------------------------------------------------------------------

#if (ISDB_SYSTEM_ENABLE == 1)
MW_ATV_Scan_Brazil::~MW_ATV_Scan_Brazil()
{
}
#endif

MW_ATV_Scan_EU::~MW_ATV_Scan_EU()
{
}

MW_ATV_Scan_AsiaChina::~MW_ATV_Scan_AsiaChina()
{
}

#if (ESASIA_NTSC_SYSTEM_ENABLE == 1)  //Add for ES Asia/TW ATV tuing 20140526EL
MW_ATV_Scan_ESAsia_NTSC::~MW_ATV_Scan_ESAsia_NTSC()
{
}
#endif



//****************************************************************************
// Private attributes of this file.
//****************************************************************************
#define AFEC_REG_BASE           0x3500  // 0x3500 - 0x35FF
#define COMB_REG_BASE           0x3600  // 0x3600 - 0x36FF
#define BK_AFEC_C4              (AFEC_REG_BASE+0xC4)
BOOL msCheckHsyncEdge(void)
{
    U8 i;
    U8 ucHsyncEdgeValue, ucPrevHsyncEdgeValue, ucMaxHsyncEdgeValue, ucMinHsyncEdgeValue, ucChangeCount;

    ucMaxHsyncEdgeValue = 0;
    ucMinHsyncEdgeValue = 0xFF;
    ucChangeCount = 0;
    ucPrevHsyncEdgeValue = 0;

    for(i = 0; i <= 50; i++)
    {
        ucHsyncEdgeValue = mapi_interface::Get_mapi_vd()->GetHsyncEdge(); //msReadByte(REG_3503);

        if(ucHsyncEdgeValue > ucMaxHsyncEdgeValue)
        {
            ucMaxHsyncEdgeValue = ucHsyncEdgeValue ;
        }

        if(ucHsyncEdgeValue < ucMinHsyncEdgeValue)
        {
            ucMinHsyncEdgeValue = ucHsyncEdgeValue ;
        }

        if(ucPrevHsyncEdgeValue != ucHsyncEdgeValue)
        {
            ucChangeCount++;
        }

        ucPrevHsyncEdgeValue = ucHsyncEdgeValue;

        ATV_Scan_DBG(" \r\n ucHsyncEdgeValue = %x", ucHsyncEdgeValue);
        usleep(1 * 1000);
    }
    ATV_Scan_DBG(" \r\n MaxHsyncEdgeValue = %x", ucMaxHsyncEdgeValue);
    ATV_Scan_DBG(" \r\n MinHsyncEdgeValue = %x", ucMinHsyncEdgeValue);
    ATV_Scan_DBG(" \r\n DifferentValue = %x", (ucMaxHsyncEdgeValue - ucMinHsyncEdgeValue));
    ATV_Scan_DBG(" \r\n ChangeCount = %x", ucChangeCount);

    if(((ucMaxHsyncEdgeValue - ucMinHsyncEdgeValue) >= 0x10) && (ucChangeCount > 20))
    {
        return TRUE;
    }
    else
    {
        return FALSE; // It's should be SECAM-l
    }
}

//-----------------------------------------------------------------------------
////
///     Temp Patch to fix Secam L error lock issue
///
///
//for china scan,vd and vif lock use threshold
#define PAL_SCAN_VIF_LOCK_CHECK_THR      2
#define PAL_SCAN_VD_LOCK_CHECK_THR      3

//-----------------------------------------------------------------------------


void MW_ATV_Scan_EU::MemorizeProg(U32 FreqKHz, BOOL bSkip)
{
    U16 u16TunerPLL;
    EN_FREQ_STEP eFreqStep = E_FREQ_STEP_INVALD;
    U8 u8CurrentProgramNumber;
    U16 wTmpVd;

    u8CurrentProgramNumber = (U8)MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL);
    m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_FREQ_STEP, 0, 0, &eFreqStep);

    switch(eFreqStep)
    {
        case E_FREQ_STEP_31_25KHz:
            u16TunerPLL = (((FreqKHz * 1000) / 312) + 5) / 10;
            break;
        case E_FREQ_STEP_50KHz:
            u16TunerPLL = FreqKHz / 50;
            break;
        case E_FREQ_STEP_62_5KHz:
            u16TunerPLL = (((FreqKHz * 100) / 625) + 5) / 10;
            break;
        default:
            ASSERT(0);
            break;
    }

    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_PROGRAM_PLL_DATA , u8CurrentProgramNumber, (U16)u16TunerPLL, NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_AUDIO_STANDARD , u8CurrentProgramNumber, (U16)mapi_interface::Get_mapi_audio()->SIF_GetAudioStandard(), NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SKIP_PROGRAM , u8CurrentProgramNumber, FALSE, NULL);

    MSrv_Control::GetMSrvAtvDatabase()->SetFavoriteProgram(SET_FAVORITE_PROGRAM, u8CurrentProgramNumber, (U16)FALSE, NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(LOCK_PROGRAM , u8CurrentProgramNumber, FALSE, NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(NEED_AFT , u8CurrentProgramNumber, TRUE, NULL);
    MSrv_Control::GetMSrvAtv()->EnableAFT(TRUE);//update atv_proc ATF flag
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(ENABLE_REALTIME_AUDIO_DETECTION , u8CurrentProgramNumber, TRUE, NULL);

    wTmpVd = mapi_interface::Get_mapi_vd()->GetVideoStandardDetection(&wTmpVd);

    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_VIDEO_STANDARD_OF_PROGRAM, u8CurrentProgramNumber, wTmpVd, NULL);

    U8 m_u8ChannelNumber = 0;
    if(m_bCNIStatus == FALSE)
    {
        m_u8ChannelNumber = MW_ATV_Util::GetInstance()->CFTGetChannelNumber(u16TunerPLL);
        m_eMedium = MW_ATV_Util::GetInstance()->CFTGetMedium(u16TunerPLL);
        TunerConvertMediumAndChannelNumberToString(m_eMedium, m_u8ChannelNumber, m_au8CurrentStationName);
        m_u8SortingPriority = LOWEST_SORTING_PRIORITY;
    }

    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_STATION_NAME , u8CurrentProgramNumber , 0 , m_au8CurrentStationName);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_SORTING_PRIORITY, u8CurrentProgramNumber , m_u8SortingPriority , NULL);
    SetMediumAndChannelNumber(u8CurrentProgramNumber, m_eMedium, m_u8ChannelNumber);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER, u8CurrentProgramNumber, 0, NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(INC_CURRENT_PROGRAM_NUMBER, 0, 0, NULL);
}

#if (ISDB_SYSTEM_ENABLE == 1)
void MW_ATV_Scan_Brazil::MemorizeProg(U32 FreqKHz, BOOL bSkip)
{
    U16 u16TunerPLL;
    EN_FREQ_STEP eFreqStep = E_FREQ_STEP_INVALD;
    U8 u8CurrentProgramNumber;

    u8CurrentProgramNumber = (U8)MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL);
    m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_FREQ_STEP, 0, 0, &eFreqStep);

    switch(eFreqStep)
    {
        case E_FREQ_STEP_31_25KHz:
            u16TunerPLL = (((FreqKHz * 1000) / 312) + 5) / 10;
            break;
        case E_FREQ_STEP_50KHz:
            u16TunerPLL = FreqKHz / 50;
            break;
        case E_FREQ_STEP_62_5KHz:
            u16TunerPLL = (((FreqKHz * 100) / 625) + 5) / 10;
            break;
        default:
            ASSERT(0);
            break;
    }


    if(!bSkip)
    {
        MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u16ScannedChannelNum++  ;
    }

    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_PROGRAM_PLL_DATA , u8CurrentProgramNumber, (U16)u16TunerPLL, NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_AUDIO_STANDARD , u8CurrentProgramNumber, (U16)mapi_interface::Get_mapi_audio()->SIF_GetAudioStandard(), NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SKIP_PROGRAM , u8CurrentProgramNumber, bSkip, NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(HIDE_PROGRAM , u8CurrentProgramNumber, bSkip, NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_DIRECT_TUNED , u8CurrentProgramNumber, !bSkip, NULL);

    MSrv_Control::GetMSrvAtvDatabase()->SetFavoriteProgram(SET_FAVORITE_PROGRAM, u8CurrentProgramNumber, (U16)FALSE, NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(LOCK_PROGRAM , u8CurrentProgramNumber, FALSE, NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(NEED_AFT , u8CurrentProgramNumber, TRUE, NULL);
    MSrv_Control::GetMSrvAtv()->EnableAFT(TRUE);//update atv_proc ATF flag
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(ENABLE_REALTIME_AUDIO_DETECTION , u8CurrentProgramNumber, TRUE, NULL);

    mapi_video_vd_cfg *pVDData = new(std::nothrow) mapi_video_vd_cfg;
    ASSERT(pVDData);

    MAPI_INPUT_SOURCE_TYPE enInputSrc = MAPI_INPUT_SOURCE_ATV;
    MSrv_Control::GetMSrvSystemDatabase()->GetLastVideoStandard(&(pVDData->enVideoStandard), &enInputSrc);

    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_VIDEO_STANDARD_OF_PROGRAM, u8CurrentProgramNumber, pVDData->enVideoStandard, NULL);

    if(pVDData != NULL)
    {
        delete pVDData;
        pVDData = NULL;
    }

    U8 m_u8ChannelNumber = 0;
    if(m_bCNIStatus == FALSE)
    {
        m_u8ChannelNumber = MW_ATV_Util::GetInstance()->CFTGetChannelNumber(u16TunerPLL);
        m_eMedium = MW_ATV_Util::GetInstance()->CFTGetMedium(u16TunerPLL);
        TunerConvertMediumAndChannelNumberToString(m_eMedium, m_u8ChannelNumber, m_au8CurrentStationName);
        m_u8SortingPriority = LOWEST_SORTING_PRIORITY;
    }

    //MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_STATION_NAME , u8CurrentProgramNumber , NULL , m_au8CurrentStationName);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_SORTING_PRIORITY, u8CurrentProgramNumber , m_u8SortingPriority , NULL);
    SetMediumAndChannelNumber(u8CurrentProgramNumber, m_eMedium, m_u8ChannelNumber);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER , u8CurrentProgramNumber, 0, NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(INC_CURRENT_PROGRAM_NUMBER, 0, 0, NULL);
}

//-------------------------------------------------------------------------------------------------
/// ATV_InitProgramPLLData
//-------------------------------------------------------------------------------------------------
void MW_ATV_Scan_Brazil::ATV_InitProgramPLLData(void)
{
    U8 _u8Buf0;
    U8 _u8Buf1 = MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CHANNEL_MIN, 0, 0, NULL);
    U8 _u8Buf2 = MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CHANNEL_MAX, 0, 0, NULL);
    U16 wPLL;

    if(TRUE != MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(HIDE_PROGRAM , 0, TRUE, NULL))
    {
        ASSERT(0);
    }

    for(_u8Buf0 = _u8Buf1; _u8Buf0 <= _u8Buf2 ; _u8Buf0++)
    {
        //printf("<1> [%bu][%bu] active = %bu\n", (_u8Buf0-1), _GetPRIndexTable(_u8Buf0-1), _IsPREntityActive(_GetPRIndexTable(_u8Buf0-1)));
        wPLL = MSrv_Control::GetMSrvAtvDatabase()->ATV_MapChanToFreq(_u8Buf0);
        if(TRUE != MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_PROGRAM_PLL_DATA , _u8Buf0 - 1, (U16)wPLL, NULL))
        {
            ASSERT(0);
        }
        if(TRUE != MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(HIDE_PROGRAM , _u8Buf0 - 1, TRUE, NULL))
        {
            ASSERT(0);
        }
        if(TRUE != MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_ATV_AUTOCOLOR , _u8Buf0 - 1, TRUE, NULL))
        {
            ASSERT(0);
        }
        //printf("<2> [%bu][%bu] active = %bu\n", (_u8Buf0-1), _GetPRIndexTable(_u8Buf0-1), _IsPREntityActive(_GetPRIndexTable(_u8Buf0-1)));
    }

}
#endif

void MW_ATV_Scan_AsiaChina::MemorizeProg(U32 FreqKHz, BOOL bSkip)
{
    U16 u16TunerPLL;
    EN_FREQ_STEP eFreqStep = E_FREQ_STEP_INVALD;
    U8 u8CurrentProgramNumber;
    U16 wTmpVd = 0;

    u8CurrentProgramNumber = (U8)MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL);
    m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_FREQ_STEP, 0, 0, &eFreqStep);

    switch(eFreqStep)
    {
        case E_FREQ_STEP_31_25KHz:
            u16TunerPLL = (((FreqKHz * 1000) / 312) + 5) / 10;
            break;
        case E_FREQ_STEP_50KHz:
            u16TunerPLL = FreqKHz / 50;
            break;
        case E_FREQ_STEP_62_5KHz:
            u16TunerPLL = (((FreqKHz * 100) / 625) + 5) / 10;
            break;
        default:
            ASSERT(0);
            break;
    }

    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_PROGRAM_PLL_DATA , u8CurrentProgramNumber, (U16)u16TunerPLL, NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_AUDIO_STANDARD , u8CurrentProgramNumber, (U16)m_ScanDetectedAudioType, NULL);
    //MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_AUDIO_STANDARD , u8CurrentProgramNumber, (U16)mapi_interface::Get_mapi_audio()->SIF_GetAudioStandard(), NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SKIP_PROGRAM , u8CurrentProgramNumber, FALSE, NULL);

    MSrv_Control::GetMSrvAtvDatabase()->SetFavoriteProgram(SET_FAVORITE_PROGRAM, u8CurrentProgramNumber, (U16)FALSE, NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(LOCK_PROGRAM , u8CurrentProgramNumber, FALSE, NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(NEED_AFT , u8CurrentProgramNumber, TRUE, NULL);
    MSrv_Control::GetMSrvAtv()->EnableAFT(TRUE);//update atv_proc ATF flag
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(ENABLE_REALTIME_AUDIO_DETECTION , u8CurrentProgramNumber, TRUE, NULL);

    //   mapi_video_vd_cfg *pVDData = new(std::nothrow) mapi_video_vd_cfg;
    //   ASSERT(pVDData);

    //    MAPI_INPUT_SOURCE_TYPE enInputSrc = MAPI_INPUT_SOURCE_ATV;
    //   MSrv_Control::GetMSrvSystemDatabase()->GetLastVideoStandard(&(pVDData->enVideoStandard), &enInputSrc);

    //MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_VIDEO_STANDARD_OF_PROGRAM, u8CurrentProgramNumber, pVDData->enVideoStandard, NULL);

    //  delete pVDData;
    // pVDData = NULL;
    wTmpVd = mapi_interface::Get_mapi_vd()->GetVideoStandardDetection(&wTmpVd);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_VIDEO_STANDARD_OF_PROGRAM, u8CurrentProgramNumber, wTmpVd, NULL);

    U8 m_u8ChannelNumber = 0;
    if(m_bCNIStatus == FALSE)
    {
        m_u8ChannelNumber = MW_ATV_Util::GetInstance()->CFTGetChannelNumber(u16TunerPLL);
        m_eMedium = MW_ATV_Util::GetInstance()->CFTGetMedium(u16TunerPLL);
        TunerConvertMediumAndChannelNumberToString(m_eMedium, m_u8ChannelNumber, m_au8CurrentStationName);
        m_u8SortingPriority = LOWEST_SORTING_PRIORITY;
    }

    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_STATION_NAME , u8CurrentProgramNumber , 0 , m_au8CurrentStationName);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_SORTING_PRIORITY, u8CurrentProgramNumber , m_u8SortingPriority , NULL);
    SetMediumAndChannelNumber(u8CurrentProgramNumber, m_eMedium, m_u8ChannelNumber);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER, u8CurrentProgramNumber, 0, NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(INC_CURRENT_PROGRAM_NUMBER, 0, 0, NULL);
}

//-----------------------------------------------------------------------------


#define CNI_FORMAT_VPS      _BIT0
#define CNI_FORMAT_830_1    _BIT1
#define CNI_FORMAT_830_2    _BIT2

#define PAL_SCAN_VIF_HALF_POLLING_LOOP ((MW_ATV_Scan_EU::PAL_SCAN_SIGNAL_CHECK_BY_VIF - MW_ATV_Scan_EU::PAL_SCAN_NOP_0) / 2)
#define PAL_SCAN_VD_HALF_POLLING_LOOP ((MW_ATV_Scan_EU::PAL_SCAN_SIGNAL_CHECK_BY_VD - MW_ATV_Scan_EU::PAL_SCAN_NOP_1) / 2)
#define PAL_SCAN_VIF_HALF_POLLING_LOOP_CHINA ((MW_ATV_Scan_AsiaChina::PAL_SCAN_SIGNAL_CHECK_BY_VIF - MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_0) / 2)
#define PAL_SCAN_VD_HALF_POLLING_LOOP_CHINA ((MW_ATV_Scan_AsiaChina::PAL_SCAN_SIGNAL_CHECK_BY_VD - MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_1) / 2)


///////////////////////////////////////////////////////////////////////
// VIF
///////////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////////////////////////////////////
//
// NTSC Scan
//
////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////
// Tuner PLL frequency code
////////////////////////////////////////////////
//#define TN_FREQ_IF          45.75// MHz, NTSC
//#define TN_FREQ_TR          (2000/TN_FREQ_SS)
//#define TN_N(Frf)       ((UINT)(((Frf + TN_FREQ_IF) * 1000) / 62.5)) //TN_FREQ_SS))
#define TN_N(Frf)       (Frf*1000) //KHz

#if 0 //(AUDIO_SYSTEM_SEL==AUDIO_SYSTEM_EIAJ)
// Air TV
code UINT tFreqCode_AirTV[] =
{
//    0                1                2                3                4          (Channel)
    TN_N(91.25), TN_N(91.25), TN_N(97.25), TN_N(103.25), TN_N(171.25),    // 000 - 004
    TN_N(177.25), TN_N(183.25), TN_N(189.25), TN_N(193.25), TN_N(199.25), // 005 - 009
    TN_N(205.25), TN_N(211.25), TN_N(217.25), TN_N(471.25), TN_N(477.25), // 010 - 014
    TN_N(483.25), TN_N(489.25), TN_N(495.25), TN_N(501.25), TN_N(507.25), // 015 - 019
    TN_N(513.25), TN_N(519.25), TN_N(525.25), TN_N(531.25), TN_N(537.25), // 020 - 024
    TN_N(543.25), TN_N(549.25), TN_N(555.25), TN_N(561.25), TN_N(567.25), // 025 - 029
    TN_N(573.25), TN_N(579.25), TN_N(585.25), TN_N(591.25), TN_N(597.25), // 030 - 034
    TN_N(603.25), TN_N(609.25), TN_N(615.25), TN_N(621.25), TN_N(627.25), // 035 - 039
    TN_N(633.25), TN_N(639.25), TN_N(645.25), TN_N(651.25), TN_N(657.25), // 040 - 044
    TN_N(663.25), TN_N(669.25), TN_N(675.25), TN_N(681.25), TN_N(687.25), // 045 - 049
    TN_N(693.25), TN_N(699.25), TN_N(705.25), TN_N(711.25), TN_N(717.25), // 050 - 054
    TN_N(723.25), TN_N(729.25), TN_N(735.25), TN_N(741.25), TN_N(747.25), // 055 - 059
    TN_N(753.25), TN_N(759.25), TN_N(765.25),                                   // 060 - 062
};

// CATV
code UINT tFreqCode_CATV[] =
{
//    0                1                2                3                4          (Channel)
    TN_N(91.25), TN_N(91.25), TN_N(97.25), TN_N(103.25), TN_N(171.25),    // 000 - 004
    TN_N(177.25), TN_N(183.25), TN_N(189.25), TN_N(193.25), TN_N(199.25), // 005 - 009
    TN_N(205.25), TN_N(211.25), TN_N(217.25), TN_N(109.25), TN_N(115.25), // 010 - 014
    TN_N(121.25), TN_N(127.25), TN_N(133.25), TN_N(139.25), TN_N(145.25), // 015 - 019
    TN_N(151.25), TN_N(157.25), TN_N(165.25), TN_N(223.25), TN_N(231.25), // 020 - 024
    TN_N(237.25), TN_N(243.25), TN_N(249.25), TN_N(253.25), TN_N(259.25), // 025 - 029
    TN_N(265.25), TN_N(271.25), TN_N(277.25), TN_N(283.25), TN_N(289.25), // 030 - 034
    TN_N(295.25), TN_N(301.25), TN_N(307.25), TN_N(313.25), TN_N(319.25), // 035 - 039
    TN_N(325.25), TN_N(331.25), TN_N(337.25), TN_N(343.25), TN_N(349.25), // 040 - 044
    TN_N(355.25), TN_N(361.25), TN_N(367.25), TN_N(373.25), TN_N(379.25), // 045 - 049
    TN_N(385.25), TN_N(391.25), TN_N(397.25), TN_N(403.25), TN_N(409.25), // 050 - 054
    TN_N(415.25), TN_N(421.25), TN_N(427.25), TN_N(433.25), TN_N(439.25), // 055 - 059
    TN_N(445.25), TN_N(451.25), TN_N(457.25), TN_N(463.25),                  // 060 - 063
};
#elif (ISDB_SYSTEM_ENABLE == 1)
// Air TV
float tFreqCode_AirTV[] =
{
//      x0            x1            x2            x3           x4            <Channel>
    TN_N(55.25), TN_N(55.25), TN_N(55.25), TN_N(61.25), TN_N(67.25),      // 000 - 004
    TN_N(77.25), TN_N(83.25), TN_N(175.25), TN_N(181.25), TN_N(187.25),   // 005 - 009
    TN_N(193.25), TN_N(199.25), TN_N(205.25), TN_N(211.25), TN_N(471.25), // 010 - 014
    TN_N(477.25), TN_N(483.25), TN_N(489.25), TN_N(495.25), TN_N(501.25), // 015 - 019
    TN_N(507.25), TN_N(513.25), TN_N(519.25), TN_N(525.25), TN_N(531.25), // 020 - 024
    TN_N(537.25), TN_N(543.25), TN_N(549.25), TN_N(555.25), TN_N(561.25), // 025 - 029
    TN_N(567.25), TN_N(573.25), TN_N(579.25), TN_N(585.25), TN_N(591.25), // 030 - 034
    TN_N(597.25), TN_N(603.25), TN_N(609.25), TN_N(615.25), TN_N(621.25), // 035 - 039
    TN_N(627.25), TN_N(633.25), TN_N(639.25), TN_N(645.25), TN_N(651.25), // 040 - 044
    TN_N(657.25), TN_N(663.25), TN_N(669.25), TN_N(675.25), TN_N(681.25), // 045 - 049
    TN_N(687.25), TN_N(693.25), TN_N(699.25), TN_N(705.25), TN_N(711.25), // 050 - 054
    TN_N(717.25), TN_N(723.25), TN_N(729.25), TN_N(735.25), TN_N(741.25), // 055 - 059
    TN_N(747.25), TN_N(753.25), TN_N(759.25), TN_N(765.25), TN_N(771.25), // 060 - 064
    TN_N(777.25), TN_N(783.25), TN_N(789.25), TN_N(795.25), TN_N(801.25), // 065 - 069
};

float tFreqCode_CATV[NTSC_CABLE_NUM][126] =
{
    {
//STD   x0            x1            x2            x3           x4            <Channel>
        TN_N(73.25), TN_N(73.25), TN_N(55.25), TN_N(61.25), TN_N(67.25),      // 000 - 004
        TN_N(77.25), TN_N(83.25), TN_N(175.25), TN_N(181.25), TN_N(187.25),   // 005 - 009
        TN_N(193.25), TN_N(199.25), TN_N(205.25), TN_N(211.25), TN_N(121.25), // 010 - 014
        TN_N(127.25), TN_N(133.25), TN_N(139.25), TN_N(145.25), TN_N(151.25), // 015 - 019
        TN_N(157.25), TN_N(163.25), TN_N(169.25), TN_N(217.25), TN_N(223.25), // 020 - 024
        TN_N(229.25), TN_N(235.25), TN_N(241.25), TN_N(247.25), TN_N(253.25), // 025 - 029
        TN_N(259.25), TN_N(265.25), TN_N(271.25), TN_N(277.25), TN_N(283.25), // 030 - 034
        TN_N(289.25), TN_N(295.25), TN_N(301.25), TN_N(307.25), TN_N(313.25), // 035 - 039
        TN_N(319.25), TN_N(325.25), TN_N(331.25), TN_N(337.25), TN_N(343.25), // 040 - 044
        TN_N(349.25), TN_N(355.25), TN_N(361.25), TN_N(367.25), TN_N(373.25), // 045 - 049
        TN_N(379.25), TN_N(385.25), TN_N(391.25), TN_N(397.25), TN_N(403.25), // 050 - 054
        TN_N(409.25), TN_N(415.25), TN_N(421.25), TN_N(427.25), TN_N(433.25), // 055 - 059
        TN_N(439.25), TN_N(445.25), TN_N(451.25), TN_N(457.25), TN_N(463.25), // 060 - 064
        TN_N(469.25), TN_N(475.25), TN_N(481.25), TN_N(487.25), TN_N(493.25), // 065 - 069
        TN_N(499.25), TN_N(505.25), TN_N(511.25), TN_N(517.25), TN_N(523.25), // 070 - 074
        TN_N(529.25), TN_N(535.25), TN_N(541.25), TN_N(547.25), TN_N(553.25), // 075 - 079
        TN_N(559.25), TN_N(565.25), TN_N(571.25), TN_N(577.25), TN_N(583.25), // 080 - 084
        TN_N(589.25), TN_N(595.25), TN_N(601.25), TN_N(607.25), TN_N(613.25), // 085 - 089
        TN_N(619.25), TN_N(625.25), TN_N(631.25), TN_N(637.25), TN_N(643.25), // 090 - 094
        TN_N(91.25), TN_N(97.25), TN_N(103.25), TN_N(109.25), TN_N(115.25),   // 095 - 000
        TN_N(649.25), TN_N(655.25), TN_N(661.25), TN_N(667.25), TN_N(673.25), // 100 - 104
        TN_N(679.25), TN_N(685.25), TN_N(691.25), TN_N(697.25), TN_N(703.25), // 105 - 109
        TN_N(709.25), TN_N(715.25), TN_N(721.25), TN_N(727.25), TN_N(733.25), // 110 - 114
        TN_N(739.25), TN_N(745.25), TN_N(751.25), TN_N(757.25), TN_N(763.25), // 115 - 119
        TN_N(769.25), TN_N(775.25), TN_N(781.25), TN_N(787.25), TN_N(793.25), // 120 - 124
        TN_N(799.25)                                                          // 125
    },
    {
//HRC   x0            x1            x2            x3           x4            <Channel>
        TN_N(73.25), TN_N(73.25), TN_N(54.00), TN_N(60.00), TN_N(66.00),      // 000 - 004
        TN_N(78.00), TN_N(84.00), TN_N(174.00), TN_N(180.00), TN_N(186.00),   // 005 - 009
        TN_N(192.00), TN_N(198.00), TN_N(204.01), TN_N(210.01), TN_N(120.00), // 010 - 014
        TN_N(126.00), TN_N(132.00), TN_N(138.00), TN_N(144.00), TN_N(150.00), // 015 - 019
        TN_N(156.00), TN_N(162.00), TN_N(168.00), TN_N(216.01), TN_N(222.01), // 020 - 024
        TN_N(228.01), TN_N(234.01), TN_N(240.01), TN_N(246.01), TN_N(252.01), // 025 - 029
        TN_N(258.01), TN_N(264.01), TN_N(270.01), TN_N(276.01), TN_N(282.01), // 030 - 034
        TN_N(288.01), TN_N(294.01), TN_N(300.01), TN_N(306.01), TN_N(312.01), // 035 - 039
        TN_N(318.01), TN_N(324.01), TN_N(330.01), TN_N(336.01), TN_N(342.01), // 040 - 044
        TN_N(348.01), TN_N(354.01), TN_N(360.01), TN_N(366.01), TN_N(372.01), // 045 - 049
        TN_N(378.01), TN_N(384.01), TN_N(390.01), TN_N(396.01), TN_N(402.02), // 050 - 054
        TN_N(408.02), TN_N(414.02), TN_N(420.02), TN_N(426.02), TN_N(432.02), // 055 - 059
        TN_N(438.02), TN_N(444.02), TN_N(450.02), TN_N(456.02), TN_N(462.02), // 060 - 064
        TN_N(468.02), TN_N(474.02), TN_N(480.02), TN_N(486.02), TN_N(492.02), // 065 - 069
        TN_N(498.02), TN_N(504.02), TN_N(510.02), TN_N(516.02), TN_N(522.02), // 070 - 074
        TN_N(528.02), TN_N(534.02), TN_N(540.02), TN_N(546.02), TN_N(552.02), // 075 - 079
        TN_N(558.02), TN_N(564.02), TN_N(570.02), TN_N(576.02), TN_N(582.02), // 080 - 084
        TN_N(588.02), TN_N(594.02), TN_N(600.03), TN_N(606.03), TN_N(612.03), // 085 - 089
        TN_N(618.03), TN_N(624.03), TN_N(630.03), TN_N(636.03), TN_N(642.03), // 090 - 094
        TN_N(90.00), TN_N(96.00), TN_N(102.00), TN_N(108.02), TN_N(114.02),   // 095 - 000
        TN_N(648.03), TN_N(654.03), TN_N(660.03), TN_N(666.03), TN_N(672.03), // 100 - 104
        TN_N(678.03), TN_N(684.03), TN_N(690.03), TN_N(696.03), TN_N(702.03), // 105 - 109
        TN_N(708.03), TN_N(714.03), TN_N(720.03), TN_N(726.03), TN_N(732.03), // 110 - 114
        TN_N(738.03), TN_N(744.03), TN_N(750.03), TN_N(756.03), TN_N(762.03), // 115 - 119
        TN_N(768.03), TN_N(774.03), TN_N(780.03), TN_N(786.03), TN_N(792.03), // 120 - 124
        TN_N(798.03),                                                     // 125
    },
    {
//IRC   x0            x1            x2            x3           x4            <Channel>
        TN_N(73.25), TN_N(73.25), TN_N(55.26), TN_N(61.26), TN_N(67.26),      // 000 - 004
        TN_N(79.26), TN_N(85.26), TN_N(175.26), TN_N(181.26), TN_N(187.26),   // 005 - 009
        TN_N(193.26), TN_N(199.26), TN_N(205.26), TN_N(211.26), TN_N(121.26), // 010 - 014
        TN_N(127.26), TN_N(133.26), TN_N(139.26), TN_N(145.26), TN_N(151.26), // 015 - 019
        TN_N(157.26), TN_N(163.26), TN_N(169.26), TN_N(217.26), TN_N(223.26), // 020 - 024
        TN_N(229.26), TN_N(235.26), TN_N(241.26), TN_N(247.26), TN_N(253.26), // 025 - 029
        TN_N(259.26), TN_N(265.26), TN_N(271.26), TN_N(277.26), TN_N(283.26), // 030 - 034
        TN_N(289.26), TN_N(295.26), TN_N(301.26), TN_N(307.26), TN_N(313.26), // 035 - 039
        TN_N(319.26), TN_N(325.26), TN_N(331.27), TN_N(337.26), TN_N(343.26), // 040 - 044
        TN_N(349.26), TN_N(355.26), TN_N(361.26), TN_N(367.26), TN_N(373.26), // 045 - 049
        TN_N(379.26), TN_N(385.26), TN_N(391.26), TN_N(397.26), TN_N(403.26), // 050 - 054
        TN_N(409.26), TN_N(415.26), TN_N(421.26), TN_N(427.26), TN_N(433.26), // 055 - 059
        TN_N(439.26), TN_N(445.26), TN_N(451.26), TN_N(457.26), TN_N(463.26), // 060 - 064
        TN_N(469.26), TN_N(475.26), TN_N(481.26), TN_N(487.26), TN_N(493.26), // 065 - 069
        TN_N(499.26), TN_N(505.26), TN_N(511.26), TN_N(517.26), TN_N(523.26), // 070 - 074
        TN_N(529.26), TN_N(535.26), TN_N(541.26), TN_N(547.26), TN_N(553.26), // 075 - 079
        TN_N(559.26), TN_N(565.26), TN_N(571.26), TN_N(577.26), TN_N(583.26), // 080 - 084
        TN_N(589.26), TN_N(595.26), TN_N(601.26), TN_N(607.26), TN_N(613.26), // 085 - 089
        TN_N(619.26), TN_N(625.26), TN_N(631.26), TN_N(637.26), TN_N(643.26), // 090 - 094
        TN_N(91.26), TN_N(97.26), TN_N(103.26), TN_N(109.27), TN_N(115.27),   // 095 - 000
        TN_N(649.26), TN_N(655.26), TN_N(661.26), TN_N(667.26), TN_N(673.26), // 100 - 104
        TN_N(679.26), TN_N(685.26), TN_N(691.26), TN_N(697.26), TN_N(703.26), // 105 - 109
        TN_N(709.26), TN_N(715.26), TN_N(721.26), TN_N(727.26), TN_N(733.26), // 110 - 114
        TN_N(739.26), TN_N(745.26), TN_N(751.26), TN_N(757.26), TN_N(763.26), // 115 - 119
        TN_N(769.26), TN_N(775.26), TN_N(781.26), TN_N(787.26), TN_N(793.26), // 120 - 124
        TN_N(799.26),                                                         // 125
    }
};
#elif (ATSC_SYSTEM_ENABLE == 1)
// Air TV
float tFreqCode_AirTV[] =
{
//      x0            x1            x2            x3           x4            <Channel>
    TN_N(55.25), TN_N(55.25), TN_N(55.25), TN_N(61.25), TN_N(67.25),      // 000 - 004
    TN_N(77.25), TN_N(83.25), TN_N(175.25), TN_N(181.25), TN_N(187.25),   // 005 - 009
    TN_N(193.25), TN_N(199.25), TN_N(205.25), TN_N(211.25), TN_N(471.25), // 010 - 014
    TN_N(477.25), TN_N(483.25), TN_N(489.25), TN_N(495.25), TN_N(501.25), // 015 - 019
    TN_N(507.25), TN_N(513.25), TN_N(519.25), TN_N(525.25), TN_N(531.25), // 020 - 024
    TN_N(537.25), TN_N(543.25), TN_N(549.25), TN_N(555.25), TN_N(561.25), // 025 - 029
    TN_N(567.25), TN_N(573.25), TN_N(579.25), TN_N(585.25), TN_N(591.25), // 030 - 034
    TN_N(597.25), TN_N(603.25), TN_N(609.25), TN_N(615.25), TN_N(621.25), // 035 - 039
    TN_N(627.25), TN_N(633.25), TN_N(639.25), TN_N(645.25), TN_N(651.25), // 040 - 044
    TN_N(657.25), TN_N(663.25), TN_N(669.25), TN_N(675.25), TN_N(681.25), // 045 - 049
    TN_N(687.25), TN_N(693.25), TN_N(699.25), TN_N(705.25), TN_N(711.25), // 050 - 054
    TN_N(717.25), TN_N(723.25), TN_N(729.25), TN_N(735.25), TN_N(741.25), // 055 - 059
    TN_N(747.25), TN_N(753.25), TN_N(759.25), TN_N(765.25), TN_N(771.25), // 060 - 064
    TN_N(777.25), TN_N(783.25), TN_N(789.25), TN_N(795.25), TN_N(801.25), // 065 - 069
};

float tFreqCode_CATV[NTSC_CABLE_NUM][136] =
{
    {
//STD   x0            x1            x2            x3           x4            <Channel>
        TN_N(73.25), TN_N(73.25), TN_N(55.25), TN_N(61.25), TN_N(67.25),      // 000 - 004
        TN_N(77.25), TN_N(83.25), TN_N(175.25), TN_N(181.25), TN_N(187.25),   // 005 - 009
        TN_N(193.25), TN_N(199.25), TN_N(205.25), TN_N(211.25), TN_N(121.25), // 010 - 014
        TN_N(127.25), TN_N(133.25), TN_N(139.25), TN_N(145.25), TN_N(151.25), // 015 - 019
        TN_N(157.25), TN_N(163.25), TN_N(169.25), TN_N(217.25), TN_N(223.25), // 020 - 024
        TN_N(229.25), TN_N(235.25), TN_N(241.25), TN_N(247.25), TN_N(253.25), // 025 - 029
        TN_N(259.25), TN_N(265.25), TN_N(271.25), TN_N(277.25), TN_N(283.25), // 030 - 034
        TN_N(289.25), TN_N(295.25), TN_N(301.25), TN_N(307.25), TN_N(313.25), // 035 - 039
        TN_N(319.25), TN_N(325.25), TN_N(331.25), TN_N(337.25), TN_N(343.25), // 040 - 044
        TN_N(349.25), TN_N(355.25), TN_N(361.25), TN_N(367.25), TN_N(373.25), // 045 - 049
        TN_N(379.25), TN_N(385.25), TN_N(391.25), TN_N(397.25), TN_N(403.25), // 050 - 054
        TN_N(409.25), TN_N(415.25), TN_N(421.25), TN_N(427.25), TN_N(433.25), // 055 - 059
        TN_N(439.25), TN_N(445.25), TN_N(451.25), TN_N(457.25), TN_N(463.25), // 060 - 064
        TN_N(469.25), TN_N(475.25), TN_N(481.25), TN_N(487.25), TN_N(493.25), // 065 - 069
        TN_N(499.25), TN_N(505.25), TN_N(511.25), TN_N(517.25), TN_N(523.25), // 070 - 074
        TN_N(529.25), TN_N(535.25), TN_N(541.25), TN_N(547.25), TN_N(553.25), // 075 - 079
        TN_N(559.25), TN_N(565.25), TN_N(571.25), TN_N(577.25), TN_N(583.25), // 080 - 084
        TN_N(589.25), TN_N(595.25), TN_N(601.25), TN_N(607.25), TN_N(613.25), // 085 - 089
        TN_N(619.25), TN_N(625.25), TN_N(631.25), TN_N(637.25), TN_N(643.25), // 090 - 094
        TN_N(91.25), TN_N(97.25), TN_N(103.25), TN_N(109.25), TN_N(115.25),   // 095 - 000
        TN_N(649.25), TN_N(655.25), TN_N(661.25), TN_N(667.25), TN_N(673.25), // 100 - 104
        TN_N(679.25), TN_N(685.25), TN_N(691.25), TN_N(697.25), TN_N(703.25), // 105 - 109
        TN_N(709.25), TN_N(715.25), TN_N(721.25), TN_N(727.25), TN_N(733.25), // 110 - 114
        TN_N(739.25), TN_N(745.25), TN_N(751.25), TN_N(757.25), TN_N(763.25), // 115 - 119
        TN_N(769.25), TN_N(775.25), TN_N(781.25), TN_N(787.25), TN_N(793.25), // 120 - 124
        TN_N(799.25), TN_N(805.25), TN_N(811.25), TN_N(817.25), TN_N(823.25), // 125 - 129
        TN_N(829.25), TN_N(835.25), TN_N(841.25), TN_N(847.25), TN_N(853.25), // 130 - 134
        TN_N(859.25)                                                              // 135
    },
    {
//HRC   x0            x1            x2            x3           x4            <Channel>
        TN_N(73.25), TN_N(73.25), TN_N(54.00), TN_N(60.00), TN_N(66.00),      // 000 - 004
        TN_N(78.00), TN_N(84.00), TN_N(174.00), TN_N(180.00), TN_N(186.00),   // 005 - 009
        TN_N(192.00), TN_N(198.00), TN_N(204.01), TN_N(210.01), TN_N(120.00), // 010 - 014
        TN_N(126.00), TN_N(132.00), TN_N(138.00), TN_N(144.00), TN_N(150.00), // 015 - 019
        TN_N(156.00), TN_N(162.00), TN_N(168.00), TN_N(216.01), TN_N(222.01), // 020 - 024
        TN_N(228.01), TN_N(234.01), TN_N(240.01), TN_N(246.01), TN_N(252.01), // 025 - 029
        TN_N(258.01), TN_N(264.01), TN_N(270.01), TN_N(276.01), TN_N(282.01), // 030 - 034
        TN_N(288.01), TN_N(294.01), TN_N(300.01), TN_N(306.01), TN_N(312.01), // 035 - 039
        TN_N(318.01), TN_N(324.01), TN_N(330.01), TN_N(336.01), TN_N(342.01), // 040 - 044
        TN_N(348.01), TN_N(354.01), TN_N(360.01), TN_N(366.01), TN_N(372.01), // 045 - 049
        TN_N(378.01), TN_N(384.01), TN_N(390.01), TN_N(396.01), TN_N(402.02), // 050 - 054
        TN_N(408.02), TN_N(414.02), TN_N(420.02), TN_N(426.02), TN_N(432.02), // 055 - 059
        TN_N(438.02), TN_N(444.02), TN_N(450.02), TN_N(456.02), TN_N(462.02), // 060 - 064
        TN_N(468.02), TN_N(474.02), TN_N(480.02), TN_N(486.02), TN_N(492.02), // 065 - 069
        TN_N(498.02), TN_N(504.02), TN_N(510.02), TN_N(516.02), TN_N(522.02), // 070 - 074
        TN_N(528.02), TN_N(534.02), TN_N(540.02), TN_N(546.02), TN_N(552.02), // 075 - 079
        TN_N(558.02), TN_N(564.02), TN_N(570.02), TN_N(576.02), TN_N(582.02), // 080 - 084
        TN_N(588.02), TN_N(594.02), TN_N(600.03), TN_N(606.03), TN_N(612.03), // 085 - 089
        TN_N(618.03), TN_N(624.03), TN_N(630.03), TN_N(636.03), TN_N(642.03), // 090 - 094
        TN_N(90.00), TN_N(96.00), TN_N(102.00), TN_N(108.02), TN_N(114.02),   // 095 - 000
        TN_N(648.03), TN_N(654.03), TN_N(660.03), TN_N(666.03), TN_N(672.03), // 100 - 104
        TN_N(678.03), TN_N(684.03), TN_N(690.03), TN_N(696.03), TN_N(702.03), // 105 - 109
        TN_N(708.03), TN_N(714.03), TN_N(720.03), TN_N(726.03), TN_N(732.03), // 110 - 114
        TN_N(738.03), TN_N(744.03), TN_N(750.03), TN_N(756.03), TN_N(762.03), // 115 - 119
        TN_N(768.03), TN_N(774.03), TN_N(780.03), TN_N(786.03), TN_N(792.03), // 120 - 124
        TN_N(798.03), TN_N(804.03), TN_N(810.03), TN_N(816.03), TN_N(822.03), // 125 - 129
        TN_N(828.03), TN_N(834.03), TN_N(840.03), TN_N(846.03), TN_N(852.03), // 130 - 134
        TN_N(858.03)                                                              // 135
    },
    {
//IRC   x0            x1            x2            x3           x4            <Channel>
        TN_N(73.25), TN_N(73.25), TN_N(55.26), TN_N(61.26), TN_N(67.26),      // 000 - 004
        TN_N(79.26), TN_N(85.26), TN_N(175.26), TN_N(181.26), TN_N(187.26),   // 005 - 009
        TN_N(193.26), TN_N(199.26), TN_N(205.26), TN_N(211.26), TN_N(121.26), // 010 - 014
        TN_N(127.26), TN_N(133.26), TN_N(139.26), TN_N(145.26), TN_N(151.26), // 015 - 019
        TN_N(157.26), TN_N(163.26), TN_N(169.26), TN_N(217.26), TN_N(223.26), // 020 - 024
        TN_N(229.26), TN_N(235.26), TN_N(241.26), TN_N(247.26), TN_N(253.26), // 025 - 029
        TN_N(259.26), TN_N(265.26), TN_N(271.26), TN_N(277.26), TN_N(283.26), // 030 - 034
        TN_N(289.26), TN_N(295.26), TN_N(301.26), TN_N(307.26), TN_N(313.26), // 035 - 039
        TN_N(319.26), TN_N(325.26), TN_N(331.27), TN_N(337.26), TN_N(343.26), // 040 - 044
        TN_N(349.26), TN_N(355.26), TN_N(361.26), TN_N(367.26), TN_N(373.26), // 045 - 049
        TN_N(379.26), TN_N(385.26), TN_N(391.26), TN_N(397.26), TN_N(403.26), // 050 - 054
        TN_N(409.26), TN_N(415.26), TN_N(421.26), TN_N(427.26), TN_N(433.26), // 055 - 059
        TN_N(439.26), TN_N(445.26), TN_N(451.26), TN_N(457.26), TN_N(463.26), // 060 - 064
        TN_N(469.26), TN_N(475.26), TN_N(481.26), TN_N(487.26), TN_N(493.26), // 065 - 069
        TN_N(499.26), TN_N(505.26), TN_N(511.26), TN_N(517.26), TN_N(523.26), // 070 - 074
        TN_N(529.26), TN_N(535.26), TN_N(541.26), TN_N(547.26), TN_N(553.26), // 075 - 079
        TN_N(559.26), TN_N(565.26), TN_N(571.26), TN_N(577.26), TN_N(583.26), // 080 - 084
        TN_N(589.26), TN_N(595.26), TN_N(601.26), TN_N(607.26), TN_N(613.26), // 085 - 089
        TN_N(619.26), TN_N(625.26), TN_N(631.26), TN_N(637.26), TN_N(643.26), // 090 - 094
        TN_N(91.26), TN_N(97.26), TN_N(103.26), TN_N(109.27), TN_N(115.27),   // 095 - 000
        TN_N(649.26), TN_N(655.26), TN_N(661.26), TN_N(667.26), TN_N(673.26), // 100 - 104
        TN_N(679.26), TN_N(685.26), TN_N(691.26), TN_N(697.26), TN_N(703.26), // 105 - 109
        TN_N(709.26), TN_N(715.26), TN_N(721.26), TN_N(727.26), TN_N(733.26), // 110 - 114
        TN_N(739.26), TN_N(745.26), TN_N(751.26), TN_N(757.26), TN_N(763.26), // 115 - 119
        TN_N(769.26), TN_N(775.26), TN_N(781.26), TN_N(787.26), TN_N(793.26), // 120 - 124
        TN_N(799.26), TN_N(805.26), TN_N(811.26), TN_N(817.26), TN_N(823.26), // 125 - 129
        TN_N(829.26), TN_N(835.26), TN_N(841.26), TN_N(847.26), TN_N(853.26), // 130 - 134
        TN_N(859.26)                                                              // 135
    }
};
#else
// Air TV
float tFreqCode_AirTV[] =
{
//      x0            x1            x2            x3           x4            <Channel>
    TN_N(55.25), TN_N(55.25), TN_N(55.25), TN_N(61.25), TN_N(67.25),      // 000 - 004
    TN_N(77.25), TN_N(83.25), TN_N(175.25), TN_N(181.25), TN_N(187.25),   // 005 - 009
    TN_N(193.25), TN_N(199.25), TN_N(205.25), TN_N(211.25), TN_N(471.25), // 010 - 014
    TN_N(477.25), TN_N(483.25), TN_N(489.25), TN_N(495.25), TN_N(501.25), // 015 - 019
    TN_N(507.25), TN_N(513.25), TN_N(519.25), TN_N(525.25), TN_N(531.25), // 020 - 024
    TN_N(537.25), TN_N(543.25), TN_N(549.25), TN_N(555.25), TN_N(561.25), // 025 - 029
    TN_N(567.25), TN_N(573.25), TN_N(579.25), TN_N(585.25), TN_N(591.25), // 030 - 034
    TN_N(597.25), TN_N(603.25), TN_N(609.25), TN_N(615.25), TN_N(621.25), // 035 - 039
    TN_N(627.25), TN_N(633.25), TN_N(639.25), TN_N(645.25), TN_N(651.25), // 040 - 044
    TN_N(657.25), TN_N(663.25), TN_N(669.25), TN_N(675.25), TN_N(681.25), // 045 - 049
    TN_N(687.25), TN_N(693.25), TN_N(699.25), TN_N(705.25), TN_N(711.25), // 050 - 054
    TN_N(717.25), TN_N(723.25), TN_N(729.25), TN_N(735.25), TN_N(741.25), // 055 - 059
    TN_N(747.25), TN_N(753.25), TN_N(759.25), TN_N(765.25), TN_N(771.25), // 060 - 064
    TN_N(777.25), TN_N(783.25), TN_N(789.25), TN_N(795.25), TN_N(801.25), // 065 - 069
};

float tFreqCode_CATV[NTSC_CABLE_NUM][126] =
{
    {
//STD   x0            x1            x2            x3           x4            <Channel>
        TN_N(73.25), TN_N(73.25), TN_N(55.25), TN_N(61.25), TN_N(67.25),      // 000 - 004
        TN_N(77.25), TN_N(83.25), TN_N(175.25), TN_N(181.25), TN_N(187.25),   // 005 - 009
        TN_N(193.25), TN_N(199.25), TN_N(205.25), TN_N(211.25), TN_N(121.25), // 010 - 014
        TN_N(127.25), TN_N(133.25), TN_N(139.25), TN_N(145.25), TN_N(151.25), // 015 - 019
        TN_N(157.25), TN_N(163.25), TN_N(169.25), TN_N(217.25), TN_N(223.25), // 020 - 024
        TN_N(229.25), TN_N(235.25), TN_N(241.25), TN_N(247.25), TN_N(253.25), // 025 - 029
        TN_N(259.25), TN_N(265.25), TN_N(271.25), TN_N(277.25), TN_N(283.25), // 030 - 034
        TN_N(289.25), TN_N(295.25), TN_N(301.25), TN_N(307.25), TN_N(313.25), // 035 - 039
        TN_N(319.25), TN_N(325.25), TN_N(331.25), TN_N(337.25), TN_N(343.25), // 040 - 044
        TN_N(349.25), TN_N(355.25), TN_N(361.25), TN_N(367.25), TN_N(373.25), // 045 - 049
        TN_N(379.25), TN_N(385.25), TN_N(391.25), TN_N(397.25), TN_N(403.25), // 050 - 054
        TN_N(409.25), TN_N(415.25), TN_N(421.25), TN_N(427.25), TN_N(433.25), // 055 - 059
        TN_N(439.25), TN_N(445.25), TN_N(451.25), TN_N(457.25), TN_N(463.25), // 060 - 064
        TN_N(469.25), TN_N(475.25), TN_N(481.25), TN_N(487.25), TN_N(493.25), // 065 - 069
        TN_N(499.25), TN_N(505.25), TN_N(511.25), TN_N(517.25), TN_N(523.25), // 070 - 074
        TN_N(529.25), TN_N(535.25), TN_N(541.25), TN_N(547.25), TN_N(553.25), // 075 - 079
        TN_N(559.25), TN_N(565.25), TN_N(571.25), TN_N(577.25), TN_N(583.25), // 080 - 084
        TN_N(589.25), TN_N(595.25), TN_N(601.25), TN_N(607.25), TN_N(613.25), // 085 - 089
        TN_N(619.25), TN_N(625.25), TN_N(631.25), TN_N(637.25), TN_N(643.25), // 090 - 094
        TN_N(91.25), TN_N(97.25), TN_N(103.25), TN_N(109.25), TN_N(115.25),   // 095 - 000
        TN_N(649.25), TN_N(655.25), TN_N(661.25), TN_N(667.25), TN_N(673.25), // 100 - 104
        TN_N(679.25), TN_N(685.25), TN_N(691.25), TN_N(697.25), TN_N(703.25), // 105 - 109
        TN_N(709.25), TN_N(715.25), TN_N(721.25), TN_N(727.25), TN_N(733.25), // 110 - 114
        TN_N(739.25), TN_N(745.25), TN_N(751.25), TN_N(757.25), TN_N(763.25), // 115 - 119
        TN_N(769.25), TN_N(775.25), TN_N(781.25), TN_N(787.25), TN_N(793.25), // 120 - 124
        TN_N(799.25)                                                          // 125
    },
    {
//HRC   x0            x1            x2            x3           x4            <Channel>
        TN_N(73.25), TN_N(73.25), TN_N(54.00), TN_N(60.00), TN_N(66.00),      // 000 - 004
        TN_N(78.00), TN_N(84.00), TN_N(174.00), TN_N(180.00), TN_N(186.00),   // 005 - 009
        TN_N(192.00), TN_N(198.00), TN_N(204.01), TN_N(210.01), TN_N(120.00), // 010 - 014
        TN_N(126.00), TN_N(132.00), TN_N(138.00), TN_N(144.00), TN_N(150.00), // 015 - 019
        TN_N(156.00), TN_N(162.00), TN_N(168.00), TN_N(216.01), TN_N(222.01), // 020 - 024
        TN_N(228.01), TN_N(234.01), TN_N(240.01), TN_N(246.01), TN_N(252.01), // 025 - 029
        TN_N(258.01), TN_N(264.01), TN_N(270.01), TN_N(276.01), TN_N(282.01), // 030 - 034
        TN_N(288.01), TN_N(294.01), TN_N(300.01), TN_N(306.01), TN_N(312.01), // 035 - 039
        TN_N(318.01), TN_N(324.01), TN_N(330.01), TN_N(336.01), TN_N(342.01), // 040 - 044
        TN_N(348.01), TN_N(354.01), TN_N(360.01), TN_N(366.01), TN_N(372.01), // 045 - 049
        TN_N(378.01), TN_N(384.01), TN_N(390.01), TN_N(396.01), TN_N(402.02), // 050 - 054
        TN_N(408.02), TN_N(414.02), TN_N(420.02), TN_N(426.02), TN_N(432.02), // 055 - 059
        TN_N(438.02), TN_N(444.02), TN_N(450.02), TN_N(456.02), TN_N(462.02), // 060 - 064
        TN_N(468.02), TN_N(474.02), TN_N(480.02), TN_N(486.02), TN_N(492.02), // 065 - 069
        TN_N(498.02), TN_N(504.02), TN_N(510.02), TN_N(516.02), TN_N(522.02), // 070 - 074
        TN_N(528.02), TN_N(534.02), TN_N(540.02), TN_N(546.02), TN_N(552.02), // 075 - 079
        TN_N(558.02), TN_N(564.02), TN_N(570.02), TN_N(576.02), TN_N(582.02), // 080 - 084
        TN_N(588.02), TN_N(594.02), TN_N(600.03), TN_N(606.03), TN_N(612.03), // 085 - 089
        TN_N(618.03), TN_N(624.03), TN_N(630.03), TN_N(636.03), TN_N(642.03), // 090 - 094
        TN_N(90.00), TN_N(96.00), TN_N(102.00), TN_N(108.02), TN_N(114.02),   // 095 - 000
        TN_N(648.03), TN_N(654.03), TN_N(660.03), TN_N(666.03), TN_N(672.03), // 100 - 104
        TN_N(678.03), TN_N(684.03), TN_N(690.03), TN_N(696.03), TN_N(702.03), // 105 - 109
        TN_N(708.03), TN_N(714.03), TN_N(720.03), TN_N(726.03), TN_N(732.03), // 110 - 114
        TN_N(738.03), TN_N(744.03), TN_N(750.03), TN_N(756.03), TN_N(762.03), // 115 - 119
        TN_N(768.03), TN_N(774.03), TN_N(780.03), TN_N(786.03), TN_N(792.03), // 120 - 124
        TN_N(798.03)                                                     // 125
    },
    {
//IRC   x0            x1            x2            x3           x4            <Channel>
        TN_N(73.25), TN_N(73.25), TN_N(55.26), TN_N(61.26), TN_N(67.26),      // 000 - 004
        TN_N(79.26), TN_N(85.26), TN_N(175.26), TN_N(181.26), TN_N(187.26),   // 005 - 009
        TN_N(193.26), TN_N(199.26), TN_N(205.26), TN_N(211.26), TN_N(121.26), // 010 - 014
        TN_N(127.26), TN_N(133.26), TN_N(139.26), TN_N(145.26), TN_N(151.26), // 015 - 019
        TN_N(157.26), TN_N(163.26), TN_N(169.26), TN_N(217.26), TN_N(223.26), // 020 - 024
        TN_N(229.26), TN_N(235.26), TN_N(241.26), TN_N(247.26), TN_N(253.26), // 025 - 029
        TN_N(259.26), TN_N(265.26), TN_N(271.26), TN_N(277.26), TN_N(283.26), // 030 - 034
        TN_N(289.26), TN_N(295.26), TN_N(301.26), TN_N(307.26), TN_N(313.26), // 035 - 039
        TN_N(319.26), TN_N(325.26), TN_N(331.27), TN_N(337.26), TN_N(343.26), // 040 - 044
        TN_N(349.26), TN_N(355.26), TN_N(361.26), TN_N(367.26), TN_N(373.26), // 045 - 049
        TN_N(379.26), TN_N(385.26), TN_N(391.26), TN_N(397.26), TN_N(403.26), // 050 - 054
        TN_N(409.26), TN_N(415.26), TN_N(421.26), TN_N(427.26), TN_N(433.26), // 055 - 059
        TN_N(439.26), TN_N(445.26), TN_N(451.26), TN_N(457.26), TN_N(463.26), // 060 - 064
        TN_N(469.26), TN_N(475.26), TN_N(481.26), TN_N(487.26), TN_N(493.26), // 065 - 069
        TN_N(499.26), TN_N(505.26), TN_N(511.26), TN_N(517.26), TN_N(523.26), // 070 - 074
        TN_N(529.26), TN_N(535.26), TN_N(541.26), TN_N(547.26), TN_N(553.26), // 075 - 079
        TN_N(559.26), TN_N(565.26), TN_N(571.26), TN_N(577.26), TN_N(583.26), // 080 - 084
        TN_N(589.26), TN_N(595.26), TN_N(601.26), TN_N(607.26), TN_N(613.26), // 085 - 089
        TN_N(619.26), TN_N(625.26), TN_N(631.26), TN_N(637.26), TN_N(643.26), // 090 - 094
        TN_N(91.26), TN_N(97.26), TN_N(103.26), TN_N(109.27), TN_N(115.27),   // 095 - 000
        TN_N(649.26), TN_N(655.26), TN_N(661.26), TN_N(667.26), TN_N(673.26), // 100 - 104
        TN_N(679.26), TN_N(685.26), TN_N(691.26), TN_N(697.26), TN_N(703.26), // 105 - 109
        TN_N(709.26), TN_N(715.26), TN_N(721.26), TN_N(727.26), TN_N(733.26), // 110 - 114
        TN_N(739.26), TN_N(745.26), TN_N(751.26), TN_N(757.26), TN_N(763.26), // 115 - 119
        TN_N(769.26), TN_N(775.26), TN_N(781.26), TN_N(787.26), TN_N(793.26), // 120 - 124
        TN_N(799.26)                                                         // 125
    }
};
#endif

#if 0 //(AUDIO_SYSTEM_SEL==AUDIO_SYSTEM_EIAJ)
#define CHANNEL_AIR_MIN     1
#define CHANNEL_AIR_MAX     62

#define CHANNEL_CATV_MIN    1
#define CHANNEL_CATV_MAX    63
#elif (ISDB_SYSTEM_ENABLE == 1)
#define CHANNEL_AIR_MIN     2
#define CHANNEL_AIR_MAX     69

#define CHANNEL_CATV_MIN    1
#define CHANNEL_CATV_MAX    125
#elif (ATSC_SYSTEM_ENABLE == 1)
#define CHANNEL_AIR_MIN     2
#define CHANNEL_AIR_MAX     69

#define CHANNEL_CATV_MIN    1
#define CHANNEL_CATV_MAX    135
#define DEFAULT_FREQUENCY   867875
#else // Taiwan NTSC channel range
#define CHANNEL_AIR_MIN     2
#define CHANNEL_AIR_MAX     69

#define CHANNEL_CATV_MIN    1
#define CHANNEL_CATV_MAX    125
#endif

#define MAX_FINE_TUNE_FREQ    500 //kHz

typedef enum _TVAutoScanStatusNTSC
{
    NTSC_SCAN_START,
    NTSC_SCAN_RF_RECHECK,
    NTSC_SCAN_RF_CHECK,
    NTSC_SCAN_RF_SHIFT_FREQ,
    NTSC_SCAN_RECHECK,
    NTSC_SCAN_SIGNAL_CHECK = NTSC_SCAN_RECHECK + 2,
#if (NTSC_CABLE_HRC_IRC_AUTO)
    NTSC_SCAN_CHECK_HRC_IRC,
#endif
    NTSC_SCAN_NOP_STEP,
    NTSC_SCAN_CH_FINE_TUNE,
    NTSC_SCAN_CH_VIEW,
    NTSC_SCAN_SAVE_DATA,
    NTSC_SCAN_NUMS
} TVAutoScanStatusNTSC;

#define DEBOUNCE_DEFAULT    3//6
#if (ATSC_SYSTEM_ENABLE == 1 || ESASIA_NTSC_SYSTEM_ENABLE == 1)  //Add for ES Asia/TW ATV tuing 20140526EL
#define SIGANL_VALID        (DEBOUNCE_DEFAULT+10)
#else
#define SIGANL_VALID        (DEBOUNCE_DEFAULT+3)
#endif
#define SIGNAL_INVALID      0

#define RF_DEBOUNCE_DEFAULT    6

//----------------------------------------------------------------------------


#if (ISDB_SYSTEM_ENABLE == 1)
void MW_ATV_Scan_Brazil::Start(U32 u32FrequencyStart, U32 u32FrequencyEnd)//,ScanCallback callback)
{
    // Delete Timing Monitor thread if exist.
    //ATC_scan_DEBUGINFO(printf("mbrg_ATV_scan_Start  , m_bATVScanThreadEnable = %d \n",(int)m_bATVScanThreadEnable);)
    ATV_Scan_DBG("mbrg_ATV_scan_Start  , u32StartFreq = %d\n", u32FrequencyStart);
    m_u32StartFreq = u32FrequencyStart;
    m_u32EndFreq =u32FrequencyEnd;
    ATV_Scan_DBG("mbrg_ATV_scan_Start  , u32StartFreq = %d\n", m_u32StartFreq);

    m_bScanContinue = TRUE;
    m_manualSearchType = SearchUp;
    m_IsManualScan = FALSE;

    // Get min. step of Tuner freq.
    EN_FREQ_STEP eFreqStep = E_FREQ_STEP_INVALD;
    m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_FREQ_STEP, 0, 0, &eFreqStep);

    switch(eFreqStep)
    {
        case E_FREQ_STEP_31_25KHz:
            m_u8TunerMinDistance = 31;
            break;
        case E_FREQ_STEP_50KHz:
            m_u8TunerMinDistance = 50;
            break;
        case E_FREQ_STEP_62_5KHz:
            m_u8TunerMinDistance = 62;
            break;
        default:
            ASSERT(0);
            break;
    }

    DoStart();

    //l]wWv
    VifSetFrequency(m_u32AutoScanFreq);
    m_u32PrevScanFreq = m_u32AutoScanFreq;
    SendScanInfo();

    //////////////////////////////////////////////////////////////////////////////////////////////////////

    if(!m_bATVScanThreadEnable)
    {
        m_bATVScanThreadEnable = TRUE;
        int intPTHChk;

        // To Init ATV DataBase
        MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER , MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CHANNEL_MIN , 0, 0, NULL) - 1, 0, NULL);
        MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(INIT_ALL_CHANNEL_DATA, 0, 0, NULL);
        // ATV_InitProgramPLLData();

        pthread_attr_t attr;
        pthread_attr_init(&attr);
        pthread_attr_setstacksize(&attr, PTHREAD_STACK_SIZE);
        intPTHChk = PTH_RET_CHK(pthread_create(&m_AtvScanThread, &attr, ScanThreadFunc, this));
        if(intPTHChk != 0)
        {
            ASSERT(0);
            //return FALSE;
        }
        m_bStartJoinThread = FALSE;
    }
    else
    {
        m_bStartJoinThread = TRUE;
        m_bATVScanThreadEnable = FALSE;
        void *thread_result;
        int intPTHChk;
        intPTHChk = PTH_RET_CHK(pthread_join(m_AtvScanThread, &thread_result));
        if(intPTHChk != 0)
        {
            perror("thread Atv Scan Thread join failed");
            ASSERT(0);
        }
        else
        {
            ATV_Scan_IFO("Exit Atv Scan Thread Success.\n");
        }

        // To Init ATV DataBase
        MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER , MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CHANNEL_MIN , 0, 0, NULL) - 1, 0, NULL);

        MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(RESET_CHANNEL_DATA , 0, 0, NULL);

        //pthread_kill(m_AtvScanThread,0);
        m_AtvScanThread = 0;
        m_bStartJoinThread = FALSE;
        m_bATVScanThreadEnable = TRUE;

        pthread_attr_t attr;
        pthread_attr_init(&attr);
        pthread_attr_setstacksize(&attr, PTHREAD_STACK_SIZE);
        PTH_RET_CHK(pthread_create(&m_AtvScanThread, &attr, ScanThreadFunc, this));
    }
}
#endif


void MW_ATV_Scan_EU::Start(U32 u32FrequencyStart, U32 u32FrequencyEnd)//,ScanCallback callback)
{
    // Delete Timing Monitor thread if exist.
    //ATC_scan_DEBUGINFO(printf("mbrg_ATV_scan_Start  , m_bATVScanThreadEnable = %d \n",(int)m_bATVScanThreadEnable);)
    ATV_Scan_DBG("mbrg_ATV_scan_Start  , u32StartFreq = %d\n", u32FrequencyStart);
    m_u32StartFreq = u32FrequencyStart;
    m_u32EndFreq =u32FrequencyEnd;
    ATV_Scan_DBG("mbrg_ATV_scan_Start  , u32StartFreq = %d\n", m_u32StartFreq);
    m_bScanContinue = TRUE;
    m_manualSearchType = SearchUp;
    m_IsManualScan = FALSE;

    // Get min. step of Tuner freq.
    EN_FREQ_STEP eFreqStep = E_FREQ_STEP_INVALD;
    m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_FREQ_STEP, 0, 0, &eFreqStep);
    switch(eFreqStep)
    {
        case E_FREQ_STEP_31_25KHz:
            m_u8TunerMinDistance = 31;
            break;
        case E_FREQ_STEP_50KHz:
            m_u8TunerMinDistance = 50;
            break;
        case E_FREQ_STEP_62_5KHz:
            m_u8TunerMinDistance = 62;
            break;
        default:
            ASSERT(0);
            break;
    }

    DoStart();

    //l]wWv
    VifSetFrequency(m_u32AutoScanFreq);
    m_u32PrevScanFreq = m_u32AutoScanFreq;
    SendScanInfo();

    //////////////////////////////////////////////////////////////////////////////////////////////////////

    if(!m_bATVScanThreadEnable)
    {
        m_bATVScanThreadEnable = TRUE;
        int intPTHChk;

        // To Init ATV DataBase
        MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER, ATV_FIRST_PR_NUM, 0, NULL);
        MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(RESET_CHANNEL_DATA, 0, 0, NULL);

        pthread_attr_t attr;
        pthread_attr_init(&attr);
        pthread_attr_setstacksize(&attr, PTHREAD_STACK_SIZE);
        intPTHChk = PTH_RET_CHK(pthread_create(&m_AtvScanThread, &attr, ScanThreadFunc, this));
        if(intPTHChk != 0)
        {
            ASSERT(0);
            //return FALSE;
        }
        m_bStartJoinThread = FALSE;
    }
    else
    {
        m_bStartJoinThread = TRUE;
        m_bATVScanThreadEnable = FALSE;
        void *thread_result;
        int intPTHChk;
        intPTHChk = PTH_RET_CHK(pthread_join(m_AtvScanThread, &thread_result));
        if(intPTHChk != 0)
        {
            perror("thread Atv Scan Thread join failed");
            ASSERT(0);
        }
        else
        {
            ATV_Scan_IFO("Exit Atv Scan Thread Success.\n");
        }

        // To Init ATV DataBase
        MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER , ATV_FIRST_PR_NUM, 0, NULL);
        MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(RESET_CHANNEL_DATA, 0, 0, NULL);

        //pthread_kill(m_AtvScanThread,0);
        m_AtvScanThread = 0;
        m_bStartJoinThread = FALSE;
        m_bATVScanThreadEnable = TRUE;

        pthread_attr_t attr;
        pthread_attr_init(&attr);
        pthread_attr_setstacksize(&attr, PTHREAD_STACK_SIZE);
        PTH_RET_CHK(pthread_create(&m_AtvScanThread, &attr, ScanThreadFunc, this));
    }
}


void MW_ATV_Scan_AsiaChina::Start(U32 u32FrequencyStart, U32 u32FrequencyEnd)//,ScanCallback callback)
{
    // Delete Timing Monitor thread if exist.
    //ATC_scan_DEBUGINFO(printf("mbrg_ATV_scan_Start  , m_bATVScanThreadEnable = %d \n",(int)m_bATVScanThreadEnable);)
    ATV_Scan_DBG("mbrg_ATV_scan_Start  , u32StartFreq = %d\n", u32FrequencyStart);
    m_u32StartFreq = u32FrequencyStart;
    m_u32EndFreq =u32FrequencyEnd;
    ATV_Scan_DBG("mbrg_ATV_scan_Start  , u32StartFreq = %d\n", m_u32StartFreq);
    m_bScanContinue = TRUE;
    m_manualSearchType = SearchUp;
    m_IsManualScan = FALSE;

    // Get min. step of Tuner freq.
    EN_FREQ_STEP eFreqStep = E_FREQ_STEP_INVALD;
    m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_FREQ_STEP, 0, 0, &eFreqStep);
    switch(eFreqStep)
    {
        case E_FREQ_STEP_31_25KHz:
            m_u8TunerMinDistance = 31;
            break;
        case E_FREQ_STEP_50KHz:
            m_u8TunerMinDistance = 50;
            break;
        case E_FREQ_STEP_62_5KHz:
            m_u8TunerMinDistance = 62;
            break;
        default:
            ASSERT(0);
            break;
    }

    DoStart();

    //l]wWv
    VifSetFrequency(m_u32AutoScanFreq);
    m_u32PrevScanFreq = m_u32AutoScanFreq;
    SendScanInfo();

    //////////////////////////////////////////////////////////////////////////////////////////////////////

    if(!m_bATVScanThreadEnable)
    {
        m_bATVScanThreadEnable = TRUE;
        int intPTHChk;

        // To Init ATV DataBase
        BOOL bSmartScan;
        MSrv_Control::GetMSrvAtv()->GetSmartScanMode(&bSmartScan);
        if(FALSE==bSmartScan)
        {
            MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER , ATV_FIRST_PR_NUM, 0, NULL);
            MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(RESET_CHANNEL_DATA, 0, 0, NULL);
        }
        else
        {
            MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER , MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_ACTIVE_PROGRAM_COUNT, 0, 0, NULL), 0, NULL);
        }
        pthread_attr_t attr;
        pthread_attr_init(&attr);
        pthread_attr_setstacksize(&attr, PTHREAD_STACK_SIZE);
        intPTHChk = PTH_RET_CHK(pthread_create(&m_AtvScanThread, &attr, ScanThreadFunc, this));
        if(intPTHChk != 0)
        {
            ASSERT(0);
            //return FALSE;
        }
        m_bStartJoinThread = FALSE;
    }
    else
    {
        m_bStartJoinThread = TRUE;
        m_bATVScanThreadEnable = FALSE;
        void *thread_result;
        int intPTHChk;
        intPTHChk = PTH_RET_CHK(pthread_join(m_AtvScanThread, &thread_result));
        if(intPTHChk != 0)
        {
            perror("thread Atv Scan Thread join failed");
            ASSERT(0);
        }
        else
        {
            ATV_Scan_IFO("Exit Atv Scan Thread Success.\n");
        }

        BOOL bSmartScan;
        MSrv_Control::GetMSrvAtv()->GetSmartScanMode(&bSmartScan);
        if(FALSE==bSmartScan)
        {
            MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER , ATV_FIRST_PR_NUM, 0, NULL);
            MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(RESET_CHANNEL_DATA, 0, 0, NULL);
        }
        else
        {
            MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER , MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_ACTIVE_PROGRAM_COUNT, 0, 0, NULL), 0 , NULL);
        }

        //pthread_kill(m_AtvScanThread,0);
        m_AtvScanThread = 0;
        m_bStartJoinThread = FALSE;
        m_bATVScanThreadEnable = TRUE;

        pthread_attr_t attr;
        pthread_attr_init(&attr);
        pthread_attr_setstacksize(&attr, PTHREAD_STACK_SIZE);
        PTH_RET_CHK(pthread_create(&m_AtvScanThread, &attr, ScanThreadFunc, this));
    }
}

//-----------------------------------------------------------------------------

#if (ISDB_SYSTEM_ENABLE == 1)
void MW_ATV_Scan_Brazil::StartManualScan(U32 u32FrequencyStart, SearchDirection mode)
{
    m_u32StartFreq = u32FrequencyStart;
    m_IsLocked = FALSE;
    m_bScanContinue = TRUE;
    m_IsManualScan = TRUE;
    m_manualSearchType = mode;
    m_u32PrevScanFreq = 0;
    m_u32AutoScanFreq = m_u32StartFreq;

    if(m_pDemodulator->ATV_IsInternalVIF())  // MStar VIF
    {
        m_u32FreqSmallStep = STEP_125K;//STEP_187d5K;
    }
    else
    {
        m_u32FreqSmallStep = STEP_250K;
    }

    // Get min. step of Tuner freq.
    EN_FREQ_STEP eFreqStep = E_FREQ_STEP_INVALD;
    m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_FREQ_STEP, 0, 0, &eFreqStep);
    switch(eFreqStep)
    {
        case E_FREQ_STEP_31_25KHz:
            m_u8TunerMinDistance = 31;
            break;
        case E_FREQ_STEP_50KHz:
            m_u8TunerMinDistance = 50;
            break;
        case E_FREQ_STEP_62_5KHz:
            m_u8TunerMinDistance = 62;
            break;
        default:
            ASSERT(0);
            break;
    }

#if (ISDB_SYSTEM_ENABLE == 1) // NTSC
    m_eAirCableSelect = MSrv_Control::GetMSrvAtv()->GetNTSCAntenna();
#if (NTSC_CABLE_HRC_IRC_AUTO)
    m_eNTSCCableType = NTSC_CABLE_AUTO;
#else
    m_eNTSCCableType = NTSC_CABLE_STD;
#endif

//    m_u8AutoScanChannelStart = GetMinChannelNO();
//    m_u8AutoScanChannelEnd = GetMaxChannelNO();
//    m_u8AutoScanChannel = m_u8AutoScanChannelStart;

    mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard((AUDIOSTANDARD_TYPE_)E_AUDIOSTANDARD_M_BTSC);
    m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_BTSC_);
    VifSetSoundStandard(TV_SOUND_M);
#endif


    if(!m_bATVScanThreadEnable)
    {
        m_bATVScanThreadEnable = TRUE;
        int intPTHChk;
        pthread_attr_t attr;
        pthread_attr_init(&attr);
        pthread_attr_setstacksize(&attr, PTHREAD_STACK_SIZE);
        intPTHChk = PTH_RET_CHK(pthread_create(&m_AtvScanThread, &attr, ManualScanThreadFunc, this));
        if(intPTHChk != 0)
        {
            ASSERT(0);
            //return FALSE;
        }
        m_bStartJoinThread = FALSE;
    }
    else
    {
        m_bStartJoinThread = TRUE;
        m_bATVScanThreadEnable = FALSE;
        void *thread_result;
        int intPTHChk;
        intPTHChk = PTH_RET_CHK(pthread_join(m_AtvScanThread, &thread_result));
        if(intPTHChk != 0)
        {
            perror("thread Atv Scan Thread join failed");
            ASSERT(0);
        }
        else
        {
            ATV_Scan_IFO("Exit Atv Scan Thread Success.\n");
        }
        m_AtvScanThread = 0;
        m_bATVScanThreadEnable = TRUE;
        m_bStartJoinThread = FALSE;
        pthread_attr_t attr;
        pthread_attr_init(&attr);
        pthread_attr_setstacksize(&attr, PTHREAD_STACK_SIZE);
        PTH_RET_CHK(pthread_create(&m_AtvScanThread, &attr, ManualScanThreadFunc, this));
    }
}
#endif


void MW_ATV_Scan_EU::StartManualScan(U32 u32FrequencyStart, SearchDirection mode)
{
    m_u32StartFreq = u32FrequencyStart;
    m_IsLocked = FALSE;
    m_bScanContinue = TRUE;
    m_IsManualScan = TRUE;
    m_manualSearchType = mode;
    m_u32AutoScanFreq = m_u32StartFreq;
    if(TRUE == mapi_interface::Get_mapi_vd()->IsSyncLocked())
    {
        if(mode == SearchUp)
        {
            m_u32AutoScanFreq += 5250;
        }
        else
        {
            m_u32AutoScanFreq -= 5250;
        }
    }

    //m_u32AutoScanFreq = m_u32StartFreq;

    VifSetFrequency(m_u32AutoScanFreq);

    m_u32PrevScanFreq = m_u32AutoScanFreq;
    if(m_pDemodulator->ATV_IsInternalVIF())  // MStar VIF
    {
        m_u32FreqSmallStep = STEP_125K;//STEP_187d5K;
    }
    else
    {
        m_u32FreqSmallStep = STEP_250K;
    }

    // Get min. step of Tuner freq.
    EN_FREQ_STEP eFreqStep = E_FREQ_STEP_INVALD;
    m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_FREQ_STEP, 0, 0, &eFreqStep);
    switch(eFreqStep)
    {
        case E_FREQ_STEP_31_25KHz:
            m_u8TunerMinDistance = 31;
            break;
        case E_FREQ_STEP_50KHz:
            m_u8TunerMinDistance = 50;
            break;
        case E_FREQ_STEP_62_5KHz:
            m_u8TunerMinDistance = 62;
            break;
        default:
            ASSERT(0);
            break;
    }

    if(!m_bATVScanThreadEnable)
    {
        m_bATVScanThreadEnable = TRUE;
        int intPTHChk;
        pthread_attr_t attr;
        pthread_attr_init(&attr);
        pthread_attr_setstacksize(&attr, PTHREAD_STACK_SIZE);
        intPTHChk = PTH_RET_CHK(pthread_create(&m_AtvScanThread, &attr, ManualScanThreadFunc, this));
        if(intPTHChk != 0)
        {
            ASSERT(0);
            //return FALSE;
        }
        m_bStartJoinThread = FALSE;
    }
    else
    {
        m_bStartJoinThread = TRUE;
        m_bATVScanThreadEnable = FALSE;
        void *thread_result;
        int intPTHChk;
        intPTHChk = PTH_RET_CHK(pthread_join(m_AtvScanThread, &thread_result));
        if(intPTHChk != 0)
        {
            perror("thread Atv Scan Thread join failed");
            ASSERT(0);
        }
        else
        {
            ATV_Scan_IFO("Exit Atv Scan Thread Success.\n");
        }
        m_AtvScanThread = 0;
        m_bATVScanThreadEnable = TRUE;
        m_bStartJoinThread = FALSE;
        pthread_attr_t attr;
        pthread_attr_init(&attr);
        pthread_attr_setstacksize(&attr, PTHREAD_STACK_SIZE);
        PTH_RET_CHK(pthread_create(&m_AtvScanThread, &attr, ManualScanThreadFunc, this));
    }
}

void MW_ATV_Scan_AsiaChina::StartManualScan(U32 u32FrequencyStart, SearchDirection mode)
{
    m_u32StartFreq = u32FrequencyStart;
    m_IsLocked = FALSE;
    m_bScanContinue = TRUE;
    m_IsManualScan = TRUE;
    m_manualSearchType = mode;
    if(TRUE == mapi_interface::Get_mapi_vd()->IsSyncLocked())
    {
        if(mode == SearchUp)
        {
            m_u32StartFreq += 5250;
        }
        else
        {
            m_u32StartFreq -= 5250;
        }
    }

    m_u32AutoScanFreq = m_u32StartFreq;

    VifSetFrequency(m_u32AutoScanFreq);

    m_u32PrevScanFreq = m_u32AutoScanFreq;
    if(m_pDemodulator->ATV_IsInternalVIF())  // MStar VIF
    {
        m_u32FreqSmallStep = STEP_125K;//STEP_187d5K;
    }
    else
    {
        m_u32FreqSmallStep = STEP_250K;
    }

    // Get min. step of Tuner freq.
    EN_FREQ_STEP eFreqStep = E_FREQ_STEP_INVALD;
    m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_FREQ_STEP, 0, 0, &eFreqStep);
    switch(eFreqStep)
    {
        case E_FREQ_STEP_31_25KHz:
            m_u8TunerMinDistance = 31;
            break;
        case E_FREQ_STEP_50KHz:
            m_u8TunerMinDistance = 50;
            break;
        case E_FREQ_STEP_62_5KHz:
            m_u8TunerMinDistance = 62;
            break;
        default:
            ASSERT(0);
            break;
    }

    if(!m_bATVScanThreadEnable)
    {
        m_bATVScanThreadEnable = TRUE;
        int intPTHChk;
        pthread_attr_t attr;
        pthread_attr_init(&attr);
        pthread_attr_setstacksize(&attr, PTHREAD_STACK_SIZE);
        intPTHChk = PTH_RET_CHK(pthread_create(&m_AtvScanThread, &attr, ManualScanThreadFunc, this));
        if(intPTHChk != 0)
        {
            ASSERT(0);
            //return FALSE;
        }
        m_bStartJoinThread = FALSE;
    }
    else
    {
        m_bStartJoinThread = TRUE;
        m_bATVScanThreadEnable = FALSE;
        void *thread_result;
        int intPTHChk;
        intPTHChk = PTH_RET_CHK(pthread_join(m_AtvScanThread, &thread_result));
        if(intPTHChk != 0)
        {
            perror("thread Atv Scan Thread join failed");
            ASSERT(0);
        }
        else
        {
            ATV_Scan_IFO("Exit Atv Scan Thread Success.\n");
        }
        m_AtvScanThread = 0;
        m_bATVScanThreadEnable = TRUE;
        m_bStartJoinThread = FALSE;
        pthread_attr_t attr;
        pthread_attr_init(&attr);
        pthread_attr_setstacksize(&attr, PTHREAD_STACK_SIZE);
        PTH_RET_CHK(pthread_create(&m_AtvScanThread, &attr, ManualScanThreadFunc, this));
    }
}

//-----------------------------------------------------------------------------

#if (ISDB_SYSTEM_ENABLE == 1)
void MW_ATV_Scan_Brazil::SendScanInfo()
{
    U32 u32FreqKHz = 0;

    u32FreqKHz = GetCurrentFreq();
    MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u32FrequencyKHz = u32FreqKHz;
    MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.bIsScaningEnable = m_bATVScanThreadEnable;
    if(m_IsManualScan == FALSE)
    {
        //For Auto Tuning
        MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u8Percent = GetScanProgressPercent();
        MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u16CurScannedChannel = m_u8AutoScanChannel; //MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, NULL, NULL, NULL);
        MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u16MaxScannedChannel = m_u8AutoScanChannelEnd;
        ATV_Scan_IFO("BBB: p=%d, Freq=%d, Num=%d \n, Thread_freq=%d"
                     , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u8Percent
                     , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u32FrequencyKHz
                     , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u16ScannedChannelNum
                     , (int)m_u32AutoScanFreq);
                     //, (int)MSrv_Control::GetMSrvAtv()->mScan->m_u32StartFreq);
        ATV_Scan_IFO(">>>>>>>>> EV_ATV_AUTO_TUNING_SCAN_INFO EVENT SEND>>>>>>>>>>>\n");


        MSrv_Control::GetMSrvAtv()->PostEvent(0, EV_ATV_AUTO_TUNING_SCAN_INFO, (U32)&MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo);

        if(MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u8Percent >= 100)
        {
            ATV_Scan_IFO("---> Auto Scan Complete...(Percent=100)\n");
        }

    }
    else
    {
        MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u8Percent = GetScanProgressPercent();

        //U8 u8Percent;
        //u8Percent = MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u8Percent;
        MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.bIsScaningEnable = m_bATVScanThreadEnable;
        if(MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.bIsScaningEnable == FALSE)
        {
            ATV_Scan_IFO("BBB-2: p=%d, Freq=%d, Num=%d \n"
                         , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u8Percent
                         , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u32FrequencyKHz
                         , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u16ScannedChannelNum);

            //MSrv_Control::GetMSrvAtv()->PostEvent(0, EV_ATV_MANUAL_TUNING_SCAN_INFO, (U32)&MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo);
            ATV_Scan_IFO("---> Manual Scan Complete...\n");
            ATV_Scan_IFO(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> EV_ATV_MANUAL_TUNING_SCAN_INFO EVENT SEND..\n");
            MSrv_Control::GetMSrvAtv()->PostEvent(0, EV_ATV_MANUAL_TUNING_SCAN_INFO, (U32)&MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo);
        }
    }

}
#endif

void MW_ATV_Scan_EU::SendScanInfo()
{
    U32 u32FreqKHz = 0;

    u32FreqKHz = GetCurrentFreq();
    MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u32FrequencyKHz = u32FreqKHz;
    if(m_IsManualScan == FALSE)
    {
        //For Auto Tuning
        MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u8Percent = GetScanProgressPercent();
        MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u16ScannedChannelNum = MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL);
        ATV_Scan_IFO("BBB: p=%d, Freq=%d, Num=%d \n, Thread_freq=%d"
                     , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u8Percent
                     , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u32FrequencyKHz
                     , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u16ScannedChannelNum
                     , (int)m_u32AutoScanFreq);
                  //   , (int)MSrv_Control::GetMSrvAtv()->mScan->m_u32StartFreq);
        ATV_Scan_IFO(">>>>>>>>> EV_ATV_AUTO_TUNING_SCAN_INFO EVENT SEND>>>>>>>\n");


        MSrv_Control::GetMSrvAtv()->PostEvent(0, EV_ATV_AUTO_TUNING_SCAN_INFO, (U32)&MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo);


        if(MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u8Percent >= 100)
        {
            ATV_Scan_IFO("---> Auto Scan Complete...(Percent=100)\n");
        }

    }
    else
    {
        MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u8Percent = GetScanProgressPercent();

        U8 u8Percent;
        u8Percent = MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u8Percent;
        if(u8Percent >= 100)
        {
            //Have program => Stop it
            MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u16ScannedChannelNum = 1;

            ATV_Scan_IFO("BBB-2: p=%d, Freq=%d, Num=%d \n"
                         , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u8Percent
                         , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u32FrequencyKHz
                         , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u16ScannedChannelNum);


            //MSrv_Control::GetMSrvAtv()->PostEvent(0, EV_ATV_MANUAL_TUNING_SCAN_INFO, (U32)&MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo);


            //Have program => Stop it
            MSrv_Control::GetMSrvAtv()->m_u16TunerPLL = MW_ATV_Util::GetInstance()->ConvertFrequncyHzToPLL(u32FreqKHz * 1000);
            ATV_Scan_IFO("---> Manual Scan Complete...(Percent=100)\n");
        }
        ATV_Scan_IFO(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> EV_ATV_MANUAL_TUNING_SCAN_INFO EVENT SEND..\n");
        MSrv_Control::GetMSrvAtv()->PostEvent(0, EV_ATV_MANUAL_TUNING_SCAN_INFO, (U32)&MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo);
    }

}

void MW_ATV_Scan_AsiaChina::SendScanInfo()
{
    U32 u32FreqKHz = 0;

    u32FreqKHz = GetCurrentFreq();
    MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u32FrequencyKHz = u32FreqKHz;
    if(m_IsManualScan == FALSE)
    {
        //For Auto Tuning
        MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u8Percent = GetScanProgressPercent();
        MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u16ScannedChannelNum = MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL);
        ATV_Scan_IFO("BBB: p=%d, Freq=%d, Num=%d \n, Thread_freq=%d"
                     , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u8Percent
                     , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u32FrequencyKHz
                     , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u16ScannedChannelNum
                     , (int)m_u32AutoScanFreq);
                    // , (int)MSrv_Control::GetMSrvAtv()->mScan->m_u32StartFreq);
        ATV_Scan_IFO(">>>>>>>>> EV_ATV_AUTO_TUNING_SCAN_INFO EVENT SEND>>>>>>>\n");


        MSrv_Control::GetMSrvAtv()->PostEvent(0, EV_ATV_AUTO_TUNING_SCAN_INFO, (U32)&MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo);

        if(MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u8Percent >= 100)
        {
            ATV_Scan_IFO("---> Auto Scan Complete...(Percent=100)\n");
        }

    }
    else
    {
        MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u8Percent = GetScanProgressPercent();

        U8 u8Percent;
        u8Percent = MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u8Percent;
        if(u8Percent >= 100)
        {
            //Have program => Stop it
            MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u16ScannedChannelNum = 1;

            ATV_Scan_IFO("BBB-2: p=%d, Freq=%d, Num=%d \n"
                         , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u8Percent
                         , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u32FrequencyKHz
                         , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u16ScannedChannelNum);


            //MSrv_Control::GetMSrvAtv()->PostEvent(0, EV_ATV_MANUAL_TUNING_SCAN_INFO, (U32)&MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo);

            //Have program => Stop it
            MSrv_Control::GetMSrvAtv()->m_u16TunerPLL = MW_ATV_Util::GetInstance()->ConvertFrequncyHzToPLL(u32FreqKHz * 1000);
            ATV_Scan_IFO("---> Manual Scan Complete...(Percent=100)\n");
        }
        ATV_Scan_IFO(">>>> EV_ATV_MANUAL_TUNING_SCAN_INFO EVENT SEND>>>>>>>\n");
        MSrv_Control::GetMSrvAtv()->PostEvent(0, EV_ATV_MANUAL_TUNING_SCAN_INFO, (U32)&MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo);
    }
}

//-----------------------------------------------------------------------------

#if (ATSC_SYSTEM_ENABLE == 1)

MW_ATV_Scan_NTSC::~MW_ATV_Scan_NTSC()
{
}


void MW_ATV_Scan_NTSC::Start(U32 u32FrequencyStart, U32 u32FrequencyEnd)//,ScanCallback callback)
{
    // Delete Timing Monitor thread if exist.
    //ATC_scan_DEBUGINFO(printf("mbrg_ATV_scan_Start  , m_bATVScanThreadEnable = %d \n",(int)m_bATVScanThreadEnable);)
    ATV_Scan_DBG("mbrg_ATV_scan_Start  , u32StartFreq = %d\n", u32FrequencyStart);
    m_u32StartFreq = u32FrequencyStart;
    m_u32EndFreq =u32FrequencyEnd;
    ATV_Scan_DBG("mbrg_ATV_scan_Start  , u32StartFreq = %d\n", m_u32StartFreq);
    m_bScanContinue = TRUE;
    m_manualSearchType = SearchUp;
    m_IsManualScan = FALSE;

    // Get min. step of Tuner freq.
    EN_FREQ_STEP eFreqStep = E_FREQ_STEP_INVALD;
    m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_FREQ_STEP, 0, 0, &eFreqStep);
    switch(eFreqStep)
    {
        case E_FREQ_STEP_31_25KHz:
            m_u8TunerMinDistance = 31;
            break;
        case E_FREQ_STEP_50KHz:
            m_u8TunerMinDistance = 50;
            break;
        case E_FREQ_STEP_62_5KHz:
            m_u8TunerMinDistance = 62;
            break;
        default:
            ASSERT(0);
            break;
    }

    DoStart();

    //l]wWv
    VifSetFrequency(m_u32AutoScanFreq);
    m_u32PrevScanFreq = m_u32AutoScanFreq;
    //SendScanInfo();

    //////////////////////////////////////////////////////////////////////////////////////////////////////

    if(!m_bATVScanThreadEnable)
    {
        m_bATVScanThreadEnable = TRUE;
        int intPTHChk;

        // To Init ATV DataBase
        MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER , ATV_FIRST_PR_NUM, 0, NULL);
        MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(INIT_ALL_CHANNEL_DATA, 0, 0, NULL);

        pthread_attr_t attr;
        pthread_attr_init(&attr);
        pthread_attr_setstacksize(&attr, PTHREAD_STACK_SIZE);
        intPTHChk = PTH_RET_CHK(pthread_create(&m_AtvScanThread, &attr, ScanThreadFunc, this));
        if(intPTHChk != 0)
        {
            ASSERT(0);
            //return FALSE;
        }
        m_bStartJoinThread = FALSE;
    }
    else
    {
        m_bStartJoinThread = TRUE;
        m_bATVScanThreadEnable = FALSE;
        void *thread_result;
        int intPTHChk;
        intPTHChk = PTH_RET_CHK(pthread_join(m_AtvScanThread, &thread_result));
        if(intPTHChk != 0)
        {
            perror("thread Atv Scan Thread join failed");
            ASSERT(0);
        }
        else
        {
            ATV_Scan_IFO("Exit Atv Scan Thread Success.\n");
        }

        // To Init ATV DataBase
        MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER , ATV_FIRST_PR_NUM, 0, NULL);
        MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(RESET_CHANNEL_DATA, 0, 0, NULL);

        //pthread_kill(m_AtvScanThread,0);
        m_AtvScanThread = 0;
        m_bStartJoinThread = FALSE;
        m_bATVScanThreadEnable = TRUE;

        pthread_attr_t attr;
        pthread_attr_init(&attr);
        pthread_attr_setstacksize(&attr, PTHREAD_STACK_SIZE);
        PTH_RET_CHK(pthread_create(&m_AtvScanThread, &attr, ScanThreadFunc, this));
    }
}


void MW_ATV_Scan_NTSC::StartManualScan(U32 u32FrequencyStart, SearchDirection mode)
{
    m_u32StartFreq = u32FrequencyStart;
    m_IsLocked = FALSE;
    m_bScanContinue = TRUE;
    m_IsManualScan = TRUE;
    m_manualSearchType = mode;
    m_u32PrevScanFreq = 0;
    m_u32AutoScanFreq = m_u32StartFreq;

    if(m_pDemodulator->ATV_IsInternalVIF())  // MStar VIF
    {
        m_u32FreqSmallStep = STEP_125K;//STEP_187d5K;
    }
    else
    {
        m_u32FreqSmallStep = STEP_250K;
    }

    // Get min. step of Tuner freq.
    EN_FREQ_STEP eFreqStep = E_FREQ_STEP_INVALD;
    m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_FREQ_STEP, 0, 0, &eFreqStep);
    switch(eFreqStep)
    {
        case E_FREQ_STEP_31_25KHz:
            m_u8TunerMinDistance = 31;
            break;
        case E_FREQ_STEP_50KHz:
            m_u8TunerMinDistance = 50;
            break;
        case E_FREQ_STEP_62_5KHz:
            m_u8TunerMinDistance = 62;
            break;
        default:
            ASSERT(0);
            break;
    }

#if (ATSC_SYSTEM_ENABLE == 1) // NTSC
    m_eAirCableSelect = MSrv_Control::GetMSrvAtv()->GetNTSCAntenna();
#if (NTSC_CABLE_HRC_IRC_AUTO)
    m_eNTSCCableType = NTSC_CABLE_AUTO;
#else
    m_eNTSCCableType = NTSC_CABLE_STD;
#endif

//    m_u8AutoScanChannelStart = GetMinChannelNO();
//    m_u8AutoScanChannelEnd = GetMaxChannelNO();
//    m_u8AutoScanChannel = m_u8AutoScanChannelStart;

    mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard((AUDIOSTANDARD_TYPE_)E_AUDIOSTANDARD_M_BTSC);
    m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_BTSC_);
    VifSetSoundStandard(TV_SOUND_M);
#endif


    if(!m_bATVScanThreadEnable)
    {
        m_bATVScanThreadEnable = TRUE;
        int intPTHChk;
        pthread_attr_t attr;
        pthread_attr_init(&attr);
        pthread_attr_setstacksize(&attr, PTHREAD_STACK_SIZE);
        intPTHChk = PTH_RET_CHK(pthread_create(&m_AtvScanThread, &attr, ManualScanThreadFunc, this));
        if(intPTHChk != 0)
        {
            ASSERT(0);
            //return FALSE;
        }
        m_bStartJoinThread = FALSE;
    }
    else
    {
        m_bStartJoinThread = TRUE;
        m_bATVScanThreadEnable = FALSE;
        void *thread_result;
        int intPTHChk;
        intPTHChk = PTH_RET_CHK(pthread_join(m_AtvScanThread, &thread_result));
        if(intPTHChk != 0)
        {
            perror("thread Atv Scan Thread join failed");
            ASSERT(0);
        }
        else
        {
            ATV_Scan_IFO("Exit Atv Scan Thread Success.\n");
        }
        m_AtvScanThread = 0;
        m_bATVScanThreadEnable = TRUE;
        m_bStartJoinThread = FALSE;
        pthread_attr_t attr;
        pthread_attr_init(&attr);
        pthread_attr_setstacksize(&attr, PTHREAD_STACK_SIZE);
        PTH_RET_CHK(pthread_create(&m_AtvScanThread, &attr, ManualScanThreadFunc, this));
    }
}


void MW_ATV_Scan_NTSC::MemorizeProg(U32 FreqKHz, BOOL bSkip)
{
    U16 u16TunerPLL;
    EN_FREQ_STEP eFreqStep = E_FREQ_STEP_INVALD;
    U8 u8CurrentProgramNumber;

    u8CurrentProgramNumber = (U8)MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL);
    m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_FREQ_STEP, 0, 0, &eFreqStep);

    switch(eFreqStep)
    {
        case E_FREQ_STEP_31_25KHz:
            u16TunerPLL = (((FreqKHz * 1000) / 312) + 5) / 10;
            break;
        case E_FREQ_STEP_50KHz:
            u16TunerPLL = FreqKHz / 50;
            break;
        case E_FREQ_STEP_62_5KHz:
            u16TunerPLL = (((FreqKHz * 100) / 625) + 5) / 10;
            break;
        default:
            ASSERT(0);
            break;
    }

#if (ATSC_SYSTEM_ENABLE == 1)
    if(!bSkip)
    {
        MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u16ScannedChannelNum++  ; //@FIXME I think total non skip count should Update on Change
    }
#endif
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_PROGRAM_PLL_DATA , u8CurrentProgramNumber, (U16)u16TunerPLL, NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_AUDIO_STANDARD , u8CurrentProgramNumber, (U16)mapi_interface::Get_mapi_audio()->SIF_GetAudioStandard(), NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SKIP_PROGRAM , u8CurrentProgramNumber, bSkip, NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(HIDE_PROGRAM , u8CurrentProgramNumber, bSkip, NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_DIRECT_TUNED , u8CurrentProgramNumber, !bSkip, NULL);

    MSrv_Control::GetMSrvAtvDatabase()->SetFavoriteProgram(SET_FAVORITE_PROGRAM, u8CurrentProgramNumber, (U16)FALSE, NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(LOCK_PROGRAM , u8CurrentProgramNumber, FALSE, NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(NEED_AFT , u8CurrentProgramNumber, TRUE, NULL);
    MSrv_Control::GetMSrvAtv()->EnableAFT(TRUE);//update atv_proc ATF flag
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(ENABLE_REALTIME_AUDIO_DETECTION , u8CurrentProgramNumber, TRUE, NULL);


    U16 wTmpVd = 0;
    wTmpVd = mapi_interface::Get_mapi_vd()->GetVideoStandardDetection(&wTmpVd);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_VIDEO_STANDARD_OF_PROGRAM, u8CurrentProgramNumber, wTmpVd, NULL);
    U8 m_u8ChannelNumber = 0;
    if(m_bCNIStatus == FALSE)
    {
        m_u8ChannelNumber = MW_ATV_Util::GetInstance()->CFTGetChannelNumber(u16TunerPLL);
        m_eMedium = MW_ATV_Util::GetInstance()->CFTGetMedium(u16TunerPLL);
        TunerConvertMediumAndChannelNumberToString(m_eMedium, m_u8ChannelNumber, m_au8CurrentStationName);
        m_u8SortingPriority = LOWEST_SORTING_PRIORITY;
    }

    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_STATION_NAME , u8CurrentProgramNumber , 0 , m_au8CurrentStationName);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_SORTING_PRIORITY, u8CurrentProgramNumber , m_u8SortingPriority , NULL);
    SetMediumAndChannelNumber(u8CurrentProgramNumber, m_eMedium, m_u8ChannelNumber);
    #if (ATSC_SYSTEM_ENABLE == 1)
    MSrv_ChannelManager_ATSC *pCMCtrl = dynamic_cast<MSrv_ChannelManager_ATSC *>(MSrv_Control_common::GetMSrvChannelManager());
    pCMCtrl->GenMainList(FALSE);
    #endif
//    MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(INC_CURRENT_PROGRAM_NUMBER , NULL , NULL , NULL);
}

void MW_ATV_Scan_NTSC::SendScanInfo()
{
    U32 u32FreqKHz = 0;

    u32FreqKHz = GetCurrentFreq();
    MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u32FrequencyKHz = u32FreqKHz;
    if(m_IsManualScan == FALSE)
    {
        //For Auto Tuning
        MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u8Percent = GetScanProgressPercent();
        if(m_u8AutoScanChannel>m_u8AutoScanChannelEnd)
        {
            MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u16CurScannedChannel = m_u8AutoScanChannelEnd;
        }
        else
        {
            MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u16CurScannedChannel = m_u8AutoScanChannel;
        }
        MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.bIsScaningEnable = m_bATVScanThreadEnable;
        ATV_Scan_IFO("BBB: p=%d, Freq=%d, Num=%d \n, Thread_freq=%d"
                     , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u8Percent
                     , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u32FrequencyKHz
                     , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u16ScannedChannelNum
                     , (int)m_u32AutoScanFreq);
                    // , (int)MSrv_Control::GetMSrvAtv()->mScan->m_u32StartFreq);
        ATV_Scan_IFO(">>>>>>>>> EV_ATV_AUTO_TUNING_SCAN_INFO EVENT SEND>>>>>>>\n");


        MSrv_Control::GetMSrvAtv()->PostEvent(0, EV_ATV_AUTO_TUNING_SCAN_INFO, (U32)&MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo);


        if(MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.bIsScaningEnable == FALSE)
        {
            ATV_Scan_IFO("---> Auto Scan Complete...(Percent=100)\n");
        }

    }
    else
    {
        MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u8Percent = GetScanProgressPercent();
        MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.bIsScaningEnable = m_bATVScanThreadEnable;
        if(MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.bIsScaningEnable == FALSE)
        {
            //Have program => Stop it
            MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u16ScannedChannelNum = 1;
            MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u16CurScannedChannel = m_u8AutoScanChannelEnd;

            ATV_Scan_IFO("BBB-2: p=%d, Freq=%d, Num=%d \n"
                         , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u8Percent
                         , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u32FrequencyKHz
                         , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u16ScannedChannelNum);

            ATV_Scan_IFO("---> Manual Scan Complete...\n");
            ATV_Scan_IFO(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> EV_ATV_MANUAL_TUNING_SCAN_INFO EVENT SEND..\n");
            MSrv_Control::GetMSrvAtv()->PostEvent(0, EV_ATV_MANUAL_TUNING_SCAN_INFO, (U32)&MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo);
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
U8 MW_ATV_Scan_NTSC::GetMinChannelNO(void)
{
    if(m_eAirCableSelect == MSrv_ATV_Database::MEDIUM_AIR)
    {
        return CHANNEL_AIR_MIN;
    }
    else
    {
        return CHANNEL_CATV_MIN;
    }
}

///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
U8 MW_ATV_Scan_NTSC::GetMaxChannelNO(void)
{
    if(m_eAirCableSelect == MSrv_ATV_Database::MEDIUM_AIR)
    {
        return CHANNEL_AIR_MAX;
    }
    else
    {
        return CHANNEL_CATV_MAX;
    }
}

//////////////////
U8 MW_ATV_Scan_NTSC::GetCurrentChannel()
{
    return m_u8AutoScanChannel;
}

//////////////////
U32 MW_ATV_Scan_NTSC::GetScanShiftFreq(U32 u32RFFreqKhz,U8 u8RFShiftStep)
{
    switch(u8RFShiftStep)
    {
        case 0://+0
            return u32RFFreqKhz;

        case 1:// -1M
            return (u32RFFreqKhz-1000);

        case 2:// 1M
            return (u32RFFreqKhz+1000);

        case 3:// 2M
            return (u32RFFreqKhz+2000);

        case 4:// -2M
            return (u32RFFreqKhz-2000);

        default:
            return u32RFFreqKhz;
    }

    return u32RFFreqKhz;
}

///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
void MW_ATV_Scan_NTSC::DoScan() // US/TW NTSC
{
    //////////////////////////////////////////////////////////////////////////////
    int u8TVScanStep = NTSC_SCAN_START;

    mapi_demodulator_datatype::AFC wTmpIf;
    U8 ucAFCOffset;
    U16 wTmpVd = 0;
#if (NTSC_CABLE_HRC_IRC_AUTO)
    TV_NTSC_CABLE_TYPE ucCheckCableHRCIRC = NTSC_CABLE_STD;
#endif
    U32 m_u32StartTime = mapi_time_utility::GetTime0();
    U32 u32OffsetFreq = 0;
    U32 u32RfShiftFreq = 0;
#if (NTSC_SCAN_MAX_SHIFT_STEP == 2)
    BOOL bCheck56IRC = TRUE;
#endif
    //////////////////////////////////////////////////////////////////////////////
    BOOL bDebugEnable = m_bDebugEnable;
    m_U32TimeOnSleep = 0 ;
    m_U32TimeOnScan = mapi_time_utility::GetTime0();
    m_U32TimeOnI2C = 0;
    //U8 waitTime;
    //char buf[10];
    ATV_Scan_LOG_FILE(FILE * pFile;)
    ATV_Scan_LOG_FILE(pFile = fopen("/Customer/scan_history.txt", "w");)
    ATV_Scan_LOG_FILE(fclose(pFile);)
    ATV_Scan_LOG_FILE(pFile = fopen("/Customer/vd_lock_history.txt", "w");)
    ATV_Scan_LOG_FILE(fclose(pFile);)
    ATV_Scan_DBG("=======================================================\nEnter Thread Body (NTSC)\n==================================================\n");
    mapi_interface::Get_mapi_vd()->StartAutoStandardDetection();
    mapi_interface::Get_mapi_vd()->SetHsyncDetectionForTuning(TRUE);
    //pthread_mutex_lock(&MSrv_Control::GetMSrvAtv()->m_mutex_Scan);
    mapi_scope_lock(scopeLock, &MSrv_Control::GetMSrvAtv()->m_mutex_Scan);

    //set the height of window size to 480 for avoiding showing garbage (ex. source:480 window: 576 => garbage displayed on the bottom )
    mapi_video_vd_cfg *pVDData = new(std::nothrow) mapi_video_vd_cfg;
    ASSERT(pVDData);
    pVDData->enVideoStandard = E_MAPI_VIDEOSTANDARD_PAL_M;
    mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetMode(pVDData);
    mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO stVideoARCInfo;
    memset(&stVideoARCInfo, 0, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO));
    mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetWindow(NULL, NULL, &stVideoARCInfo);

    while(m_bATVScanThreadEnable)
    {
        if(m_bScanContinue)
        {
            if(mapi_time_utility::TimeDiffFromNow0(m_u32StartTime) > 20)
            {
                m_u32StartTime = mapi_time_utility::GetTime0();
                ATV_Scan_DBG("\033[0;32;34m allal ATV Tuning Loop StateTime=%d  \033[m \n", (int)m_u32StartTime);
                ATV_Scan_DBG("now state=%d\n", u8TVScanStep);

                ATV_Scan_FLOW_DBG("\r\n[S]:now state=%d", u8TVScanStep);

                switch(u8TVScanStep)
                {
                    case NTSC_SCAN_START:
                        ATV_Scan_FLOW_DBG("\r\n[S]:NTSC_SCAN_START");
                        ATV_Scan_DBG("\r\n==========CH=======%d ", m_u8AutoScanChannel);
                        m_U32NumOfScan += 1;
                        m_u8TunerOffset = 0;
                        m_u8TunerSignal = 0;
                        m_u8RFShiftStep = 0;
                        m_u8AutoDebounce = DEBOUNCE_DEFAULT;
                        m_u8RFDebounce = RF_DEBOUNCE_DEFAULT;

                        if(m_eAirCableSelect == MSrv_ATV_Database::MEDIUM_AIR)  // Air TV
                        {
                            m_u32AutoScanFreq = tFreqCode_AirTV[m_u8AutoScanChannel];
                        }
                        else // Cable
                        {
#if (NTSC_CABLE_HRC_IRC_AUTO)
                            ucCheckCableHRCIRC = NTSC_CABLE_STD;

                            if(m_eNTSCCableType == NTSC_CABLE_AUTO)
                            {
                                m_u32AutoScanFreq = tFreqCode_CATV[ucCheckCableHRCIRC][m_u8AutoScanChannel];
                            }
                            else
#endif
                            {
                                m_u32AutoScanFreq = tFreqCode_CATV[m_eNTSCCableType][m_u8AutoScanChannel];
                            }
                        }
                        u32RfShiftFreq = m_u32AutoScanFreq; //use for fine tune fail
                        m_u8OrgRFFreq = m_u32AutoScanFreq;
                        VifSetFrequency(m_u32AutoScanFreq);
                        u8TVScanStep++;

                        //bypass scan dtv RF channel  (Manual scan and atv scan did not bypass)
                        if( (m_IsManualScan == FALSE) && (m_AutoScanState == AUTO_SCAN_ALL))
                        {
                            if(MSrv_Control_common::GetMSrvChannelManager()->IsDtvHaveThisRfChannelNo(m_u8AutoScanChannel))
                            {
                                u8TVScanStep = NTSC_SCAN_SAVE_DATA;
                            }
                        }
                        break;
                   case NTSC_SCAN_RF_CHECK:
                       ATV_Scan_FLOW_DBG("\r\n[S]:NTSC_SCAN_RF_CHECK");
                       wTmpIf = m_pDemodulator->ATV_GetAFC_Distance();
                       if(wTmpIf != mapi_demodulator_datatype::E_AFC_OUT_OF_AFCWIN)
                       {
                           ATV_Scan_DBG("\nVIF LOCK..");
                           u8TVScanStep = NTSC_SCAN_SIGNAL_CHECK;
                       }
                       else
                       {
                           ATV_Scan_DBG("\nVIF UNLOCK..");
                           if(m_u8RFDebounce==0)
                           {
#if(NTSC_SHIFT_SCAN_ENABLE == 1)
                               u8TVScanStep = NTSC_SCAN_RF_SHIFT_FREQ;
#else
                                u8TVScanStep = NTSC_SCAN_SAVE_DATA;
#endif
                           }
                           else
                           {
                               u8TVScanStep = NTSC_SCAN_RF_RECHECK;
                               m_u8RFDebounce--;
                           }
                       }
                       break;

                    case NTSC_SCAN_RF_SHIFT_FREQ:
#if(NTSC_SHIFT_SCAN_ENABLE == 1)
                        ATV_Scan_FLOW_DBG("\r\n[S]:NTSC_SCAN_RF_SHIFT_CHECK");
                        if(m_u8RFShiftStep < NTSC_SCAN_MAX_SHIFT_STEP)
                        {
                            //set next shift freq
                            m_u8RFShiftStep++;
                            m_u32AutoScanFreq = GetScanShiftFreq(m_u8OrgRFFreq,m_u8RFShiftStep);
                            u32RfShiftFreq = m_u32AutoScanFreq; //use for fine tune fail
                            VifSetFrequency(m_u32AutoScanFreq);
                            m_u8AutoDebounce = DEBOUNCE_DEFAULT;
                            m_u8RFDebounce = RF_DEBOUNCE_DEFAULT;
                            u8TVScanStep = NTSC_SCAN_RF_RECHECK;
                            //printf("\n m_u8RFShiftStep:%d",(U16)m_u8RFShiftStep);
                        }
                        else
                        {
#if (NTSC_SCAN_MAX_SHIFT_STEP == 2)
                           if(((m_u8AutoScanChannel == 5) || (m_u8AutoScanChannel == 6))
                                && bCheck56IRC == TRUE
                                && m_eAirCableSelect == MSrv_ATV_Database::MEDIUM_CABLE)// special for CH5 & CH6
                           {
                               m_u32AutoScanFreq = tFreqCode_CATV[NTSC_CABLE_IRC][m_u8AutoScanChannel];
                               VifSetFrequency(m_u32AutoScanFreq);
                               u8TVScanStep = NTSC_SCAN_RF_RECHECK;
                               m_u8AutoDebounce = DEBOUNCE_DEFAULT;
                               m_u8RFDebounce = RF_DEBOUNCE_DEFAULT;
                               bCheck56IRC = FALSE;
                           }
                           else
                           {
                            //save original freq
                            m_u32AutoScanFreq = GetScanShiftFreq(m_u8OrgRFFreq,0);
                            u32RfShiftFreq = m_u32AutoScanFreq; //use for fine tune fail
                            u8TVScanStep = NTSC_SCAN_SAVE_DATA;
                               bCheck56IRC = TRUE;
                           }
#else
                            //save original freq
                            m_u32AutoScanFreq = GetScanShiftFreq(m_u8OrgRFFreq,0);
                            u32RfShiftFreq = m_u32AutoScanFreq; //use for fine tune fail
                            u8TVScanStep = NTSC_SCAN_SAVE_DATA;
#endif
                        }
#endif
                        break;

                    case NTSC_SCAN_SIGNAL_CHECK:
                        ATV_Scan_FLOW_DBG("\r\n[S]:NTSC_SCAN_SIGNAL_CHECK");
                        wTmpVd = mapi_interface::Get_mapi_vd()->CheckStatusLoop();
                        wTmpIf = m_pDemodulator->ATV_GetAFC_Distance();
                        ATV_Scan_DBG("VD:%x..", wTmpVd);
                        ATV_Scan_DBG("VIF:%x..", wTmpIf);
#if 0 //(IF_SELECT==IF_R2S10401SP)
                        if(wTmpVd & VD_CHECK_HSYNC_LOCKED)
#else
                        if((wTmpVd & VD_CHECK_HSYNC_LOCKED) && (wTmpIf != mapi_demodulator_datatype::E_AFC_OUT_OF_AFCWIN))
#endif
                        {
                            ATV_Scan_DBG("VD&VIF LOCK..");
                            m_u8AutoDebounce++;
                            if(m_u8AutoDebounce == SIGANL_VALID)
                            {
                                m_u8TunerSignal = 1;
                                ucAFCOffset = CheckAfcWinPass(wTmpIf);
                                if(ucAFCOffset == AFC_GOOD)
                                {
                                    ATV_Scan_DBG("\r\nGOOD AFC");
                                    u8TVScanStep = NTSC_SCAN_CH_VIEW;

#if (NTSC_CABLE_HRC_IRC_AUTO)
                                    if((m_eAirCableSelect == MSrv_ATV_Database::MEDIUM_CABLE) && (m_eNTSCCableType == NTSC_CABLE_AUTO))
                                    {
                                        if(ucCheckCableHRCIRC == NTSC_CABLE_HRC)
                                        {
                                            ATV_Scan_DBG("\r\n!! Force Cable Std : HRC");
                                            m_eNTSCCableType = NTSC_CABLE_HRC;
                                        }
                                        else if(ucCheckCableHRCIRC == NTSC_CABLE_IRC)
                                        {
                                            ATV_Scan_DBG("\r\n!! Force Cable Std : IRC");
                                            m_eNTSCCableType = NTSC_CABLE_IRC;
                                        }
                                        else if(m_u8AutoScanChannel >= 7)
                                        {
                                            ATV_Scan_DBG("\r\n!! Force Cable Std : STD");
                                            m_eNTSCCableType = NTSC_CABLE_STD;
                                        }
                                    }
#endif
                                }
                                else
                                {
                                    u8TVScanStep = NTSC_SCAN_NOP_STEP;
                                    if((ucAFCOffset == AFC_INCREASE)||(ucAFCOffset == AFC_BIG_STEP_INC))
                                    {
                                        m_u8TunerOffset++;
                                        m_u32AutoScanFreq += m_u8TunerMinDistance;
                                    }
                                    else
                                    {
                                        m_u8TunerOffset--;
                                        m_u32AutoScanFreq -= m_u8TunerMinDistance;
                                    }

                                    VifSetFrequency(m_u32AutoScanFreq);
                                }
                            }
                            else
                            {
                                u8TVScanStep = NTSC_SCAN_RECHECK;
                            }
                        }
                        else
                        {
                            ATV_Scan_DBG("NO LOCK..");
                            m_u8AutoDebounce--;
                            if(m_u8AutoDebounce == SIGNAL_INVALID)
                            {
#if (NTSC_CABLE_HRC_IRC_AUTO)
                                if((m_eAirCableSelect == MSrv_ATV_Database::MEDIUM_CABLE) && (m_eNTSCCableType == NTSC_CABLE_AUTO))
                                {
                                    u8TVScanStep = NTSC_SCAN_CHECK_HRC_IRC;
                                }
                                else // Air or Cable has standard
                                {
                                    u8TVScanStep = NTSC_SCAN_SAVE_DATA;
                                }
#elif (NTSC_SHIFT_SCAN_ENABLE == 1)
                u8TVScanStep = NTSC_SCAN_RF_SHIFT_FREQ;
#else
                                u8TVScanStep = NTSC_SCAN_SAVE_DATA;
#endif
                            }
                            else
                            {
                                u8TVScanStep = NTSC_SCAN_RECHECK;
                            }
                        }
                        break;

#if (NTSC_CABLE_HRC_IRC_AUTO)
                    case NTSC_SCAN_CHECK_HRC_IRC:
                        ATV_Scan_FLOW_DBG("\r\n[S]:NTSC_SCAN_CHECK_HRC_IRC");
                        ATV_Scan_DBG("\r\nucCheckCableHRCIRC:%d..", ucCheckCableHRCIRC);
                        m_u8TunerOffset = 0;
                        m_u8TunerSignal = 0;

                        m_u8AutoDebounce = DEBOUNCE_DEFAULT;
                        m_u8RFDebounce = RF_DEBOUNCE_DEFAULT;

                        if((m_u8AutoScanChannel == 5) || (m_u8AutoScanChannel == 6))  // special for CH5 & CH6
                        {
                            if(ucCheckCableHRCIRC == NTSC_CABLE_STD)
                            {
                                ucCheckCableHRCIRC = NTSC_CABLE_HRC;
                                m_u32AutoScanFreq = tFreqCode_CATV[ucCheckCableHRCIRC][m_u8AutoScanChannel];
                                ATV_Scan_DBG("\r\nswitch to (ch5_6) HRC:%uKHz", m_u32AutoScanFreq);
                                VifSetFrequency(m_u32AutoScanFreq);
                                u8TVScanStep = NTSC_SCAN_RECHECK;
                            }
                            else if(ucCheckCableHRCIRC == NTSC_CABLE_HRC)
                            {
                                ucCheckCableHRCIRC = NTSC_CABLE_IRC;
                                m_u32AutoScanFreq = tFreqCode_CATV[ucCheckCableHRCIRC][m_u8AutoScanChannel];
                                ATV_Scan_DBG("\r\nswitch to (ch5_6) IRC:%uKHz", m_u32AutoScanFreq);
                                VifSetFrequency(m_u32AutoScanFreq);
                                u8TVScanStep = NTSC_SCAN_RECHECK;
                            }
                            else
                            {
                                u8TVScanStep = NTSC_SCAN_SAVE_DATA;
                            }
                        }
                        else
                        {
                            if(ucCheckCableHRCIRC == NTSC_CABLE_STD)
                            {
                                ucCheckCableHRCIRC = NTSC_CABLE_HRC;
                                m_u32AutoScanFreq = tFreqCode_CATV[ucCheckCableHRCIRC][m_u8AutoScanChannel];
                                ATV_Scan_DBG("\r\nswitch to HRC:%uKHz", m_u32AutoScanFreq);
                                VifSetFrequency(m_u32AutoScanFreq);
                                u8TVScanStep = NTSC_SCAN_RECHECK;
                            }
                            else
                            {
                                u8TVScanStep = NTSC_SCAN_SAVE_DATA;
                            }
                        }
                        break;
#endif

                    case NTSC_SCAN_CH_FINE_TUNE:
                    {
                        ATV_Scan_FLOW_DBG("\r\n[S]:NTSC_SCAN_CH_FINE_TUNE");
                        wTmpIf = m_pDemodulator->ATV_GetAFC_Distance();
                        ucAFCOffset = CheckAfcWinPass(wTmpIf);
                        u32OffsetFreq = (u32RfShiftFreq >= m_u32AutoScanFreq)? (u32RfShiftFreq - m_u32AutoScanFreq):(m_u32AutoScanFreq - u32RfShiftFreq);
                        ATV_Scan_FLOW_DBG("\r\n    - Offeset freq by fine tune: %d kHz",u32OffsetFreq);
                        if(ucAFCOffset == AFC_GOOD)
                        {
                            u8TVScanStep = NTSC_SCAN_CH_VIEW;
                        }
                        else if(u32OffsetFreq > MAX_FINE_TUNE_FREQ) // check fine tune offset range
                        {
                            m_u32AutoScanFreq = u32RfShiftFreq;
                            u8TVScanStep = NTSC_SCAN_CH_VIEW;
                        }
                        else
                        {
                            u8TVScanStep = NTSC_SCAN_NOP_STEP;
                            if((ucAFCOffset == AFC_INCREASE) || (ucAFCOffset == AFC_BIG_STEP_INC))
                            {
                                m_u8TunerOffset++;
                                m_u32AutoScanFreq += m_u8TunerMinDistance;
                            }
                            else
                            {
                                m_u8TunerOffset--;
                                m_u32AutoScanFreq -= m_u8TunerMinDistance;
                            }

                            VifSetFrequency(m_u32AutoScanFreq);
                        }
                        break;
                    }
                    case NTSC_SCAN_SAVE_DATA:
                        ATV_Scan_FLOW_DBG("\r\n[S]:NTSC_SCAN_SAVE_DATA");
                        ATV_Scan_DBG("\r\n  Signal=%x", m_u8TunerSignal);
                        ATV_Scan_DBG("\r\n  Offset=%x", m_u8TunerOffset);

                        //mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard((AUDIOSTANDARD_TYPE_)E_AUDIOSTANDARD_M_BTSC_ );
                        //m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_BTSC_);

                        //save for manual search no signal
                        if((m_IsManualScan == TRUE) && (!m_u8TunerSignal))
                        {
                            //set default freq
                            if(m_eAirCableSelect == MSrv_ATV_Database::MEDIUM_AIR)    // Air TV
                            {
                                m_u32AutoScanFreq = tFreqCode_AirTV[m_u8AutoScanChannel];
                            }
                            else // Cable
                            {
                                m_u32AutoScanFreq = tFreqCode_CATV[NTSC_CABLE_STD][m_u8AutoScanChannel];
                            }

                            //save manual tuning channel
                            m_u8TunerSignal = TRUE;
                        }

                        // Real Channel
                        //if(m_u8TunerSignal)
                        {
                            ATV_Scan_FLOW_DBG("\r\n[CH]:%d have signal", m_u8AutoScanChannel);
                            MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER, m_u8AutoScanChannel, 0, NULL);
                            MemorizeProg(m_u32AutoScanFreq, !m_u8TunerSignal);
                        }

                        mapi_interface::Get_mapi_vif()->BypassDBBAudioFilter(FALSE);

                        m_u8AutoScanChannel++; // next
                        u8TVScanStep = NTSC_SCAN_START;
                        break;
                    default:
                        u8TVScanStep++;
                        break;
                }
            }
            else
            {
                U32 m_u32TmpTime = mapi_time_utility::GetTime0();
                U32 sleepTime =  20 - mapi_time_utility::TimeDiffFromNow0(m_u32StartTime);
                //if(sleepTime < 20 && sleepTime>=0)
                if(sleepTime < 20)
                {
                    ATV_Scan_DBG("time out for %u", sleepTime * 1000);
                    usleep(sleepTime * 1000);
                }
                m_U32TimeOnSleep += mapi_time_utility::TimeDiffFromNow0(m_u32TmpTime);
            }

            m_pDemodulator->ATV_VIF_Handler(TRUE);

            {
#if ( ENABLE_SKIP_SAME_FREQ )
                if(m_u32PrevScanFreq != m_u32AutoScanFreq)
#endif
                {
                    // For OSD update // NTSC ӬOq Ch. No. ......
                    switch(m_u8TunerMinDistance)
                    {
                        case 31:
                        {
                            U16 u16TunerPLL = (((m_u32AutoScanFreq * 1000) / 3125) + 5) / 10;
                            m_u32AutoScanFreq = (u16TunerPLL * 3125) / 100;
                            break;
                        }
                        case 62:
                        {
                            U16 u16TunerPLL = (((m_u32AutoScanFreq * 100) / 625) + 5) / 10;
                            m_u32AutoScanFreq = (u16TunerPLL * 625) / 10;
                            break;
                        }
                        default:
                            break;
                    }

                    //VifSetFrequency( m_u32AutoScanFreq );
                    m_u32PrevScanFreq = m_u32AutoScanFreq;
                    SendScanInfo();
                }
            }

            if(m_u8AutoScanChannel > m_u8AutoScanChannelEnd)  // end tuning
            {
                m_bATVScanThreadEnable = FALSE;
                m_AtvScanThread = 0;
                SendScanInfo();

                ATV_Scan_DBG("\nScan is Ending...");
            }

            //pthread_mutex_unlock(&MSrv_Control::GetMSrvAtv()->m_mutex_Scan);
            ATV_Scan_LOG_FILE(fclose(pFile);)
        }
        else
        {
            usleep(20 * 1000);
        }
    }

    //Init VIF
    //m_pDemodulator->ATV_VIF_Init();
    if(TRUE == bDebugEnable)
    {
        m_U32TimeOnScan = mapi_time_utility::TimeDiffFromNow0(m_U32TimeOnScan);
        ATV_Scan_PRINT("\n_ATV_Scan_Thread Exit Success\n m_U32TimeOnScan=%u, at %d\n", m_U32TimeOnScan, __LINE__);
        ATV_Scan_PRINT("\n_ATV_Scan_Thread Exit Success\n m_U32TimeOnI2C=%u\n", m_U32TimeOnI2C);
        ATV_Scan_PRINT("\n_ATV_Scan_Thread Exit Success\n m_U32TimeOnSleep=%u\n", m_U32TimeOnSleep);
    }
    //#endif
    //pthread_mutex_unlock(&MSrv_Control::GetMSrvAtv()->m_mutex_Scan);
    mapi_interface::Get_mapi_vd()->SetHsyncDetectionForTuning(FALSE);
    if(pVDData != NULL)
    {
        delete pVDData;
        pVDData = NULL;
    }
    ATV_Scan_DBG("\n_ATV_Scan_Thread Exit Success\n Freq=%d, at %d\n", m_u32StartFreq, __LINE__);
}

BOOL MW_ATV_Scan_NTSC::DoInit(void)
{
    // For NTSC
    m_u8AutoScanChannelStart = 0;
    m_u8AutoScanChannelEnd = 0;
    m_u8AutoScanChannel = 0;
    m_u8TunerOffset = 0;
    m_u8TunerSignal = 0;
    m_u8AutoDebounce = 0;
    m_eAirCableSelect = MSrv_ATV_Database::MEDIUM_AIR;
    m_eNTSCCableType = NTSC_CABLE_STD;

    return TRUE;
}

void MW_ATV_Scan_NTSC::DoStart(void)
{
    ST_MEDIUM_SETTING MediumSetting;
    m_u32AutoScanFreq = DEFAULT_FREQUENCY;    // set the default VIF frequency for mantis 0650388
    MSrv_Control::GetMSrvSystemDatabase()->GetMediumSetting(&MediumSetting);
    if(MediumSetting.AntennaType == E_ANTENNA_TYPE_AIR)
    {
        MSrv_Control::GetMSrvAtv()->SetNTSCAntenna(MSrv_ATV_Database::MEDIUM_AIR);
    }
    else if(MediumSetting.AntennaType == E_ANTENNA_TYPE_CABLE)
    {
        MSrv_Control::GetMSrvAtv()->SetNTSCAntenna(MSrv_ATV_Database::MEDIUM_CABLE);
    }
    else
    {
        // error here
        ATV_Scan_DBG("!!!!! Error Medium Type\n");
        MSrv_Control::GetMSrvAtv()->SetNTSCAntenna(MSrv_ATV_Database::MEDIUM_CABLE);
    }

    m_eAirCableSelect = MSrv_Control::GetMSrvAtv()->GetNTSCAntenna();
#if (NTSC_CABLE_HRC_IRC_AUTO)
    m_eNTSCCableType = NTSC_CABLE_AUTO;
#else
    m_eNTSCCableType = NTSC_CABLE_STD;
#endif
    m_u8AutoScanChannelStart = GetMinChannelNO();
    m_u8AutoScanChannelEnd = GetMaxChannelNO();
    m_u8AutoScanChannel = m_u8AutoScanChannelStart;
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER, (U16)m_u8AutoScanChannel, 0, NULL);

    mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard((AUDIOSTANDARD_TYPE_)E_AUDIOSTANDARD_M_BTSC);
    m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_BTSC_);
    VifSetSoundStandard(TV_SOUND_M);
}

U8 MW_ATV_Scan_NTSC::GetScanProgressPercent(void)
{
    U8 u8Percent;
    U32 u32UhfMaxFreq;
    u8Percent = 0;

    mapi_tuner* pTuner;
    pTuner = mapi_interface::Get_mapi_pcb()->GetAtvTuner(0);
    ASSERT(pTuner);

    pTuner->ExtendCommand(
        mapi_tuner_datatype::E_TUNER_SUBCMD_GET_UHF_MAX_FREQ,
        0,
        0,
        &u32UhfMaxFreq);

    if(((m_IsManualScan == TRUE) && (IsSearched())) || (FALSE == m_bATVScanThreadEnable) )
    {
        return 100;
    }

    if(m_IsManualScan == TRUE)
    {
        if(m_manualSearchType == SearchUp)
        {
            if((m_u32StartFreq ) <= 48250 )
            {
                if((GetCurrentFreq() ) == (u32UhfMaxFreq ))
                {
                    printf("%s() LINE=%d\n",__func__,__LINE__);
                    return 100;
                }
            }
            else
            {
                if((GetCurrentFreq()/1000 ) == (m_u32StartFreq /1000 - 1))
                {
                    printf("%s() LINE=%d m_u32StartFreq =%d , Curent=%d\n",__func__,__LINE__,(int)m_u32StartFreq,(int)GetCurrentFreq());
                    return 100;
                }
            }
        }
        else
        {
            if((m_u32StartFreq ) >= (u32UhfMaxFreq ))
            {
                if((GetCurrentFreq() ) <= 48250 )
                {
                    printf("%s() LINE=%d\n",__func__,__LINE__);
                    return 100;
                }
            }
            else
            {
                if((GetCurrentFreq()/1000 ) == (m_u32StartFreq/1000 + 1))
                {
                    printf("%s() LINE=%d\n",__func__,__LINE__);
                    return 100;
                }
            }
        }
        return 0;
    }

    u8Percent = (U8)(((NowChannel() - GetMinChannelNO()) * 100) / (GetMaxChannelNO() - GetMinChannelNO())) ;
    printf("u8Percent=%d \tMin=%d \t Max=%d\n", u8Percent, GetMinChannelNO(), GetMaxChannelNO());

    return u8Percent;
}

#endif

///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
#if (ISDB_SYSTEM_ENABLE == 1)

U8 MW_ATV_Scan_Brazil::GetMinChannelNO(void)
{
    if(m_eAirCableSelect == MSrv_ATV_Database::MEDIUM_AIR)
    {
        return CHANNEL_AIR_MIN;
    }
    else
    {
        return CHANNEL_CATV_MIN;
    }
}

///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
U8 MW_ATV_Scan_Brazil::GetMaxChannelNO(void)
{
    if(m_eAirCableSelect == MSrv_ATV_Database::MEDIUM_AIR)
    {
        return CHANNEL_AIR_MAX;
    }
    else
    {
        return CHANNEL_CATV_MAX;
    }
}

//////////////////
U8 MW_ATV_Scan_Brazil::GetCurrentChannel()
{
    return m_u8AutoScanChannel;
}

///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
void MW_ATV_Scan_Brazil::DoScan() // Brazil PAL-M
{
    //////////////////////////////////////////////////////////////////////////////
    int u8TVScanStep = NTSC_SCAN_START;

    mapi_demodulator_datatype::AFC wTmpIf;
    U8 ucAFCOffset;
    U16 wTmpVd;
#if (NTSC_CABLE_HRC_IRC_AUTO)
    TV_NTSC_CABLE_TYPE ucCheckCableHRCIRC = NTSC_CABLE_STD;
#endif
    U32 m_u32StartTime = mapi_time_utility::GetTime0();
    //////////////////////////////////////////////////////////////////////////////
    BOOL bDebugEnable = m_bDebugEnable;
    m_U32TimeOnSleep = 0 ;
    m_U32TimeOnScan = mapi_time_utility::GetTime0();
    m_U32TimeOnI2C = 0;
    //Init VIF
    // m_pDemodulator->ATV_VIF_Init();

    //U8 waitTime;
    //char buf[10];
    ATV_Scan_LOG_FILE(FILE * pFile;)
    ATV_Scan_LOG_FILE(pFile = fopen("/Customer/scan_history.txt", "w");)
    ATV_Scan_LOG_FILE(fclose(pFile);)
    ATV_Scan_LOG_FILE(pFile = fopen("/Customer/vd_lock_history.txt", "w");)
    ATV_Scan_LOG_FILE(fclose(pFile);)
    ATV_Scan_DBG("=======================================================\nEnter Thread Body (Brazil)\n==================================================\n");
    mapi_interface::Get_mapi_vd()->StartAutoStandardDetection();
    mapi_interface::Get_mapi_vd()->SetHsyncDetectionForTuning(TRUE);
    //pthread_mutex_lock(&MSrv_Control::GetMSrvAtv()->m_mutex_Scan);
    mapi_scope_lock(scopeLock, &MSrv_Control::GetMSrvAtv()->m_mutex_Scan);

    //set the height of window size to 480 for avoiding showing garbage (ex. source:480 window: 576 => garbage displayed on the bottom )
    mapi_video_vd_cfg *pVDData = new(std::nothrow) mapi_video_vd_cfg;
    ASSERT(pVDData);
    pVDData->enVideoStandard = E_MAPI_VIDEOSTANDARD_PAL_M;
    mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetMode(pVDData);
    mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO stVideoARCInfo;
    memset(&stVideoARCInfo, 0, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO));
    mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetWindow(NULL, NULL, &stVideoARCInfo);

    if(m_IsManualScan == TRUE)
        MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER , (U16) (m_u8AutoScanChannel-1) , 0 , NULL);

    while(m_bATVScanThreadEnable)
    {
        if(m_bScanContinue)
        {
            if(mapi_time_utility::TimeDiffFromNow0(m_u32StartTime) > 30)
            {
                m_u32StartTime = mapi_time_utility::GetTime0();
                ATV_Scan_DBG("\033[0;32;34m allal ATV Tuning Loop StateTime=%d  \033[m \n", (int)m_u32StartTime);
                ATV_Scan_DBG("now state=%d\n", u8TVScanStep);

                ATV_Scan_FLOW_DBG("\r\n[S]:now state=%d", u8TVScanStep);

                switch(u8TVScanStep)
                {
                    case NTSC_SCAN_START:
                        ATV_Scan_FLOW_DBG("\r\n[S]:NTSC_SCAN_START");
                        ATV_Scan_DBG("\r\n==========CH=======%d ", m_u8AutoScanChannel);
                        m_U32NumOfScan += 1;
                        m_u8TunerOffset = 0;
                        m_u8TunerSignal = 0;
                        m_u8AutoDebounce = DEBOUNCE_DEFAULT;

                        if(m_eAirCableSelect == MSrv_ATV_Database::MEDIUM_AIR)  // Air TV
                        {
                            m_u32AutoScanFreq = tFreqCode_AirTV[m_u8AutoScanChannel];
                        }
                        else // Cable
                        {
#if (NTSC_CABLE_HRC_IRC_AUTO)
                            ucCheckCableHRCIRC = NTSC_CABLE_STD;

                            if(m_eNTSCCableType == NTSC_CABLE_AUTO)
                            {
                                m_u32AutoScanFreq = tFreqCode_CATV[ucCheckCableHRCIRC][m_u8AutoScanChannel];

                            }
                            else
#endif
                            {
                                m_u32AutoScanFreq = tFreqCode_CATV[m_eNTSCCableType][m_u8AutoScanChannel];

                            }
                        }

                        VifSetFrequency(m_u32AutoScanFreq);
                        u8TVScanStep++;
                        break;
                    case NTSC_SCAN_SIGNAL_CHECK:
                        ATV_Scan_FLOW_DBG("\r\n[S]:NTSC_SCAN_SIGNAL_CHECK");
                        wTmpVd = mapi_interface::Get_mapi_vd()->CheckStatusLoop();
                        wTmpIf = m_pDemodulator->ATV_GetAFC_Distance();
                        ATV_Scan_DBG("VD:%x..", wTmpVd);
                        ATV_Scan_DBG("VIF:%x..", wTmpIf);
#if 0 //(IF_SELECT==IF_R2S10401SP)
                        if(wTmpVd & VD_CHECK_HSYNC_LOCKED)
#else
                        if((wTmpVd & VD_CHECK_HSYNC_LOCKED) && (wTmpIf != mapi_demodulator_datatype::E_AFC_OUT_OF_AFCWIN))
#endif
                        {
                            ATV_Scan_DBG("VD&VIF LOCK..");
                            m_u8AutoDebounce++;
                            if(m_u8AutoDebounce == SIGANL_VALID)
                            {
                                m_u8TunerSignal = 1;
                                ucAFCOffset = CheckAfcWinPass(wTmpIf);
                                if(ucAFCOffset == AFC_GOOD)
                                {
                                    ATV_Scan_DBG("\r\nGOOD AFC");
                                    u8TVScanStep = NTSC_SCAN_CH_VIEW;

#if (NTSC_CABLE_HRC_IRC_AUTO)
                                    if((m_eAirCableSelect == MSrv_ATV_Database::MEDIUM_CABLE) && (m_eNTSCCableType == NTSC_CABLE_AUTO))
                                    {
                                        if(ucCheckCableHRCIRC == NTSC_CABLE_HRC)
                                        {
                                            ATV_Scan_DBG("\r\n!! Force Cable Std : HRC");
                                            m_eNTSCCableType = NTSC_CABLE_HRC;
                                        }
                                        else if(ucCheckCableHRCIRC == NTSC_CABLE_IRC)
                                        {
                                            ATV_Scan_DBG("\r\n!! Force Cable Std : IRC");
                                            m_eNTSCCableType = NTSC_CABLE_IRC;
                                        }
                                        else if(m_u8AutoScanChannel >= 7)
                                        {
                                            ATV_Scan_DBG("\r\n!! Force Cable Std : STD");
                                            m_eNTSCCableType = NTSC_CABLE_STD;
                                        }
                                    }
#endif
                                }
                                else
                                {
                                    u8TVScanStep = NTSC_SCAN_NOP_STEP;
                                    if((ucAFCOffset == AFC_INCREASE)||(ucAFCOffset == AFC_BIG_STEP_INC))
                                    {
                                        m_u8TunerOffset++;
                                        m_u32AutoScanFreq += m_u8TunerMinDistance;
                                    }
                                    else
                                    {
                                        m_u8TunerOffset--;
                                        m_u32AutoScanFreq -= m_u8TunerMinDistance;
                                    }
                                    VifSetFrequency(m_u32AutoScanFreq);
                                }
                            }
                            else
                            {
                                u8TVScanStep = NTSC_SCAN_RECHECK;
                            }
                        }
                        else
                        {
                            ATV_Scan_DBG("NO LOCK..");
                            m_u8AutoDebounce--;
                            if(m_u8AutoDebounce == SIGNAL_INVALID)
                            {
#if (NTSC_CABLE_HRC_IRC_AUTO)
                                if((m_eAirCableSelect == MSrv_ATV_Database::MEDIUM_CABLE) && (m_eNTSCCableType == NTSC_CABLE_AUTO))
                                {
                                    u8TVScanStep = NTSC_SCAN_CHECK_HRC_IRC;
                                }
                                else // Air or Cable has standard
                                {
                                    u8TVScanStep = NTSC_SCAN_SAVE_DATA;
                                }
#else
                                u8TVScanStep = NTSC_SCAN_SAVE_DATA;
#endif
                            }
                            else
                            {
                                u8TVScanStep = NTSC_SCAN_RECHECK;
                            }
                        }
                        break;

#if (NTSC_CABLE_HRC_IRC_AUTO)
                    case NTSC_SCAN_CHECK_HRC_IRC:
                        ATV_Scan_FLOW_DBG("\r\n[S]:NTSC_SCAN_CHECK_HRC_IRC");
                        ATV_Scan_DBG("\r\nucCheckCableHRCIRC:%d..", ucCheckCableHRCIRC);
                        m_u8TunerOffset = 0;
                        m_u8TunerSignal = 0;

                        m_u8AutoDebounce = DEBOUNCE_DEFAULT;

                        if((m_u8AutoScanChannel == 5) || (m_u8AutoScanChannel == 6))  // special for CH5 & CH6
                        {
                            if(ucCheckCableHRCIRC == NTSC_CABLE_STD)
                            {
                                ucCheckCableHRCIRC = NTSC_CABLE_HRC;
                                m_u32AutoScanFreq = tFreqCode_CATV[ucCheckCableHRCIRC][m_u8AutoScanChannel];
                                ATV_Scan_DBG("\r\nswitch to (ch5_6) HRC:%uKHz", m_u32AutoScanFreq);
                                VifSetFrequency(m_u32AutoScanFreq);
                                u8TVScanStep = NTSC_SCAN_RECHECK;
                            }
                            else if(ucCheckCableHRCIRC == NTSC_CABLE_HRC)
                            {
                                ucCheckCableHRCIRC = NTSC_CABLE_IRC;
                                m_u32AutoScanFreq = tFreqCode_CATV[ucCheckCableHRCIRC][m_u8AutoScanChannel];
                                ATV_Scan_DBG("\r\nswitch to (ch5_6) IRC:%uKHz", m_u32AutoScanFreq);
                                VifSetFrequency(m_u32AutoScanFreq);
                                u8TVScanStep = NTSC_SCAN_RECHECK;
                            }
                            else
                            {
                                u8TVScanStep = NTSC_SCAN_SAVE_DATA;
                            }
                        }
                        else
                        {
                            if(ucCheckCableHRCIRC == NTSC_CABLE_STD)
                            {
                                ucCheckCableHRCIRC = NTSC_CABLE_HRC;
                                m_u32AutoScanFreq = tFreqCode_CATV[ucCheckCableHRCIRC][m_u8AutoScanChannel];
                                ATV_Scan_DBG("\r\nswitch to HRC:%uKHz", m_u32AutoScanFreq);
                                VifSetFrequency(m_u32AutoScanFreq);
                                u8TVScanStep = NTSC_SCAN_RECHECK;
                            }
                            else
                            {
                                u8TVScanStep = NTSC_SCAN_SAVE_DATA;
                            }
                        }
                        break;
#endif

                    case NTSC_SCAN_CH_FINE_TUNE:
                        ATV_Scan_FLOW_DBG("\r\n[S]:NTSC_SCAN_CH_FINE_TUNE");
                        wTmpVd = mapi_interface::Get_mapi_vd()->CheckStatusLoop();
                        wTmpIf = m_pDemodulator->ATV_GetAFC_Distance();

                        if((wTmpVd & VD_CHECK_HSYNC_LOCKED) && (wTmpIf != mapi_demodulator_datatype::E_AFC_OUT_OF_AFCWIN))
                        {
                            ucAFCOffset = CheckAfcWinPass(wTmpIf);
                            if(ucAFCOffset == AFC_GOOD)
                            {
                                u8TVScanStep = NTSC_SCAN_CH_VIEW;
                            }
                            else
                            {
                                u8TVScanStep = NTSC_SCAN_NOP_STEP;
                                if((ucAFCOffset == AFC_INCREASE)||(ucAFCOffset == AFC_BIG_STEP_INC))
                                {
                                    m_u8TunerOffset++;
                                    m_u32AutoScanFreq += m_u8TunerMinDistance;
                                }
                                else
                                {
                                    m_u8TunerOffset--;
                                    m_u32AutoScanFreq -= m_u8TunerMinDistance;
                                }
                                VifSetFrequency(m_u32AutoScanFreq);
                            }
                        }
                        else
                        {
                            m_u8AutoDebounce--;
                            if(m_u8AutoDebounce == SIGNAL_INVALID)
                            {
#if (NTSC_CABLE_HRC_IRC_AUTO)
                                if((m_eAirCableSelect == MSrv_ATV_Database::MEDIUM_CABLE) && (m_eNTSCCableType == NTSC_CABLE_AUTO))
                                {
                                    u8TVScanStep = NTSC_SCAN_CHECK_HRC_IRC;
                                }
                                else // Air or Cable has standard
                                {
                                    u8TVScanStep = NTSC_SCAN_SAVE_DATA;
                                }
#else
                                u8TVScanStep = NTSC_SCAN_SAVE_DATA;
#endif
                            }
                            else
                            {
                                u8TVScanStep = NTSC_SCAN_RECHECK;
                            }
                        }
                        break;
                    case NTSC_SCAN_SAVE_DATA:
                        ATV_Scan_FLOW_DBG("\r\n[S]:NTSC_SCAN_SAVE_DATA");
                        ATV_Scan_DBG("\r\n  Signal=%x", m_u8TunerSignal);
                        ATV_Scan_DBG("\r\n  Offset=%x", m_u8TunerOffset);

                        //mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard((AUDIOSTANDARD_TYPE_)E_AUDIOSTANDARD_M_BTSC_ );
                        //m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_BTSC_);

                        // Real Channel
                        //if(m_u8TunerSignal)
                        {
                            ATV_Scan_FLOW_DBG("\r\n[CH]:%d have signal", m_u8AutoScanChannel);
                            MemorizeProg(m_u32AutoScanFreq, !m_u8TunerSignal);
                        }

                        mapi_interface::Get_mapi_vif()->BypassDBBAudioFilter(FALSE);

                        m_u8AutoScanChannel++; // next
                        u8TVScanStep = NTSC_SCAN_START;
                        break;
                    default:
                        u8TVScanStep++;
                        break;
                }
            }
            else
            {
                U32 m_u32TmpTime = mapi_time_utility::GetTime0();
                U32 sleepTime =  20 - mapi_time_utility::TimeDiffFromNow0(m_u32StartTime);
                //if(sleepTime < 20 && sleepTime>=0)
                if(sleepTime < 20)
                {
                    ATV_Scan_DBG("time out for %u", sleepTime * 1000);
                    usleep(sleepTime * 1000);
                }
                m_U32TimeOnSleep += mapi_time_utility::TimeDiffFromNow0(m_u32TmpTime);
            }

            m_pDemodulator->ATV_VIF_Handler(TRUE);

            {
#if ( ENABLE_SKIP_SAME_FREQ )
                if(m_u32PrevScanFreq != m_u32AutoScanFreq)
#endif
                {
                    // For OSD update // NTSC ӬOq Ch. No. ......
                    switch(m_u8TunerMinDistance)
                    {
                        case 31:
                        {
                            U16 u16TunerPLL = (((m_u32AutoScanFreq * 1000) / 3125) + 5) / 10;
                            m_u32AutoScanFreq = (u16TunerPLL * 3125) / 100;
                            break;
                        }
                        case 62:
                        {
                            U16 u16TunerPLL = (((m_u32AutoScanFreq * 100) / 625) + 5) / 10;
                            m_u32AutoScanFreq = (u16TunerPLL * 625) / 10;
                            break;
                        }
                        default:
                            break;
                    }

                    //VifSetFrequency( m_u32AutoScanFreq );
                    m_u32PrevScanFreq = m_u32AutoScanFreq;
                    SendScanInfo();
                }
            }

            if(m_u8AutoScanChannel > m_u8AutoScanChannelEnd)  // end tuning
            {
                m_u8AutoScanChannel = m_u8AutoScanChannelEnd;
                m_bATVScanThreadEnable = FALSE;
                m_AtvScanThread = 0;
                SendScanInfo();
                ATV_Scan_DBG("\nScan is Ending...");
            }

            //pthread_mutex_unlock(&MSrv_Control::GetMSrvAtv()->m_mutex_Scan);
            ATV_Scan_LOG_FILE(fclose(pFile);)
        }
        else
        {
            usleep(20 * 1000);
        }
    }

    //Init VIF
    //m_pDemodulator->ATV_VIF_Init();

    //#endif
    //pthread_mutex_unlock(&MSrv_Control::GetMSrvAtv()->m_mutex_Scan);
    if(TRUE == bDebugEnable)
    {
        m_U32TimeOnScan = mapi_time_utility::TimeDiffFromNow0(m_U32TimeOnScan);
        ATV_Scan_PRINT("\n_ATV_Scan_Thread Exit Success\n m_U32TimeOnScan=%u, at %d\n", m_U32TimeOnScan, __LINE__);
        ATV_Scan_PRINT("\n_ATV_Scan_Thread Exit Success\n m_U32TimeOnI2C=%u\n", m_U32TimeOnI2C);
        ATV_Scan_PRINT("\n_ATV_Scan_Thread Exit Success\n m_U32TimeOnSleep=%u\n", m_U32TimeOnSleep);
    }

    mapi_interface::Get_mapi_vd()->SetHsyncDetectionForTuning(FALSE);
    if(pVDData != NULL)
    {
        delete pVDData;
        pVDData = NULL;
    }
    ATV_Scan_DBG("\n_ATV_Scan_Thread Exit Success\n Freq=%d, at %d\n", m_u32StartFreq, __LINE__);
}
//------------------------------------------------------------------------------

BOOL MW_ATV_Scan_Brazil::DoInit(void)
{
    // For NTSC
    m_u8AutoScanChannelStart = 0;
    m_u8AutoScanChannelEnd = 0;
    m_u8AutoScanChannel = 0;
    m_u8TunerOffset = 0;
    m_u8TunerSignal = 0;
    m_u8AutoDebounce = 0;
    m_eAirCableSelect = MSrv_ATV_Database::MEDIUM_AIR;
    m_eNTSCCableType = NTSC_CABLE_STD;

    return TRUE;
}

void MW_ATV_Scan_Brazil::DoStart(void)
{
    ST_MEDIUM_SETTING MediumSetting;
    MSrv_Control::GetMSrvSystemDatabase()->GetMediumSetting(&MediumSetting);

    SetupAutoScanPara(E_AUDIOSTANDARD_M_BTSC_);
    if(MediumSetting.AntennaType == E_ANTENNA_TYPE_AIR)
    {
        MSrv_Control::GetMSrvAtv()->SetNTSCAntenna(MSrv_ATV_Database::MEDIUM_AIR);
    }
    else if(MediumSetting.AntennaType == E_ANTENNA_TYPE_CABLE)
    {
        MSrv_Control::GetMSrvAtv()->SetNTSCAntenna(MSrv_ATV_Database::MEDIUM_CABLE);
    }
    else
    {
        // error here
        ATV_Scan_DBG("!!!!! Error Medium Type\n");
        MSrv_Control::GetMSrvAtv()->SetNTSCAntenna(MSrv_ATV_Database::MEDIUM_CABLE);
    }

    m_eAirCableSelect = MSrv_Control::GetMSrvAtv()->GetNTSCAntenna();
#if (NTSC_CABLE_HRC_IRC_AUTO)
    m_eNTSCCableType = NTSC_CABLE_AUTO;
#else
    m_eNTSCCableType = NTSC_CABLE_STD;
#endif

    m_u8AutoScanChannelStart = GetMinChannelNO();
    m_u8AutoScanChannelEnd = GetMaxChannelNO();
    m_u8AutoScanChannel = m_u8AutoScanChannelStart;
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER, (U16)m_u8AutoScanChannelStart, 0, NULL);

    mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard((AUDIOSTANDARD_TYPE_)E_AUDIOSTANDARD_M_BTSC);
    m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_BTSC_);
    VifSetSoundStandard(TV_SOUND_M);
}
#endif



void MW_ATV_Scan_EU::DoScan()
{
    //////////////////////////////////////////////////////////////////////////////
    int u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_START;
    U8 u8ATVScanSoundSystemType = 0;
    U32 u32FineTuneFreq = 0;
    U8 u8AudioDetectCount = 0;
    BOOLEAN bPollingVifLock = 0;
    BOOLEAN bPollingVdLock = 0;

    mapi_demodulator_datatype::AFC wTmpIf;
    U8 ucAFCOffset;
    U8 ucTmpSoundSystem;
    U16 wTmpVd;
    U32 m_u32StartTime = mapi_time_utility::GetTime0();
    BOOL bDebugEnable = m_bDebugEnable;
    m_U32TimeOnSleep = 0 ;
    m_U32TimeOnScan = mapi_time_utility::GetTime0();
    m_U32TimeOnI2C = 0;
    m_U32NumOfScan = 0;
    //////////////////////////////////////////////////////////////////////////////

    //U8 waitTime;
    //char buf[10];
    ATV_Scan_LOG_FILE(FILE * pFile;)
    ATV_Scan_LOG_FILE(pFile = fopen("/Customer/scan_history.txt", "w");)
    ATV_Scan_LOG_FILE(fclose(pFile);)
    ATV_Scan_LOG_FILE(pFile = fopen("/Customer/vd_lock_history.txt", "w");)
    ATV_Scan_LOG_FILE(fclose(pFile);)
    ATV_Scan_DBG("=======================================================\nEnter Thread Body (EU)\n==================================================\n");
    mapi_interface::Get_mapi_vd()->StartAutoStandardDetection();
    mapi_interface::Get_mapi_vd()->SetHsyncDetectionForTuning(TRUE);
    //pthread_mutex_lock(&MSrv_Control::GetMSrvAtv()->m_mutex_Scan);
    mapi_scope_lock(scopeLock, &MSrv_Control::GetMSrvAtv()->m_mutex_Scan);

    MSrv_Control::GetInstance()->SetVideoMute(TRUE);
    //set the height of window size to 480 for avoiding showing garbage (ex. source:480 window: 576 => garbage displayed on the bottom )
    mapi_video_vd_cfg *pVDData = new(std::nothrow) mapi_video_vd_cfg;
    ASSERT(pVDData);
    pVDData->enVideoStandard = E_MAPI_VIDEOSTANDARD_PAL_M;
    mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetMode(pVDData);
    mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO stVideoARCInfo;
    memset(&stVideoARCInfo, 0, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO));
    mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetWindow(NULL, NULL, &stVideoARCInfo);
    MSrv_Control::GetInstance()->SetVideoMute(FALSE);

//    printf("\r\n ===================> Do Scan =================");

    while(m_bATVScanThreadEnable)
    {
        if(m_bScanContinue)
        {
            if(mapi_time_utility::TimeDiffFromNow0(m_u32StartTime) > 20)
            {
                m_u32StartTime = mapi_time_utility::GetTime0();
                ATV_Scan_DBG("\033[0;32;34m allal ATV Tuning Loop StateTime=%d  \033[m \n", (int)m_u32StartTime);
                ATV_Scan_DBG("now state=%d\n", u8TVScanStep);

                ATV_Scan_FLOW_DBG("\r\n[S]:now state=%d", u8TVScanStep);

                switch(u8TVScanStep)
                {
                    case MW_ATV_Scan_EU::PAL_SCAN_START:
                    case MW_ATV_Scan_EU::PAL_SCAN_START_SECAM:
                        ATV_Scan_FLOW_DBG("\r\n[S]:SCAN_START");
                        m_U32NumOfScan += 1;

                        if(u8ATVScanSoundSystemType != TV_SOUND_DK)
                            mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard((AUDIOSTANDARD_TYPE_)E_AUDIOSTANDARD_DK);

                        if(m_bIsFranceSearch == true) //pGOSDk
                        {
                            ATV_Scan_DBG("\r\n<<Secam Start>>");
//                            printf("\r\n<<Secam Start>>");
                            if(m_u32AutoScanFreq < PAL_FREQ_SECAM_L)
                            {
                                u8ATVScanSoundSystemType = TV_SOUND_LL;
                                m_bIsLLSearch = true;
                                VifSetSoundStandard(u8ATVScanSoundSystemType);
                            }
                            else
                            {
                                u8ATVScanSoundSystemType = TV_SOUND_L;
                                m_bIsLLSearch = false;
                                VifSetSoundStandard(u8ATVScanSoundSystemType);
                            }

                            u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_NOP_0;
                            //wTmpIf = STEP_250K;
                            //m_u32AutoScanFreq -= (m_u32AutoScanFreq%wTmpIf); // Normalize
                        }
                        else
                        {
                            u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_START_PAL;
                        }
                        break;

                    case MW_ATV_Scan_EU::PAL_SCAN_START_PAL:
                        ATV_Scan_DBG("\r\n<<Pal Start>>");
//                         printf("\r\n<<Pal Start>>");

                        m_bIsLLSearch = false;
                        if(u8ATVScanSoundSystemType != TV_SOUND_DK)
                        {
                            u8ATVScanSoundSystemType = TV_SOUND_DK;
                            VifSetSoundStandard(u8ATVScanSoundSystemType);
                        }

                        if(m_bIsFranceSearch == true) //pGOSDk
                            u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_NOP_0_PAL - 1; // speed up a little bit than PAL_SCAN_START_SECAM
                        else
                            u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_NOP_0_PAL; // speed up a little bit than PAL_SCAN_START_SECAM
                        //g_ucCNIScanFormatCurrent = 0;
                        //g_ucCNIScanFormatDone = 0;

                        //wTmpIf = STEP_250K;
                        //m_u32AutoScanFreq -= (m_u32AutoScanFreq%wTmpIf); // Normalize
                        break;

                    case MW_ATV_Scan_EU::PAL_SCAN_NOP_0:
                    case MW_ATV_Scan_EU::PAL_SCAN_NOP_0_PAL:
                        u8TVScanStep++;
                        bPollingVifLock = 0;
                        break;

                    case MW_ATV_Scan_EU::PAL_SCAN_SIGNAL_CHECK_BY_VIF:
                        ATV_Scan_FLOW_DBG("\r\n[S]:SCAN_SIGNAL_CHECK_BY_VIF");
                        ATV_Scan_FLOW_DBG("\r\n<< %u KHz  ", m_u32AutoScanFreq);
//                        printf("\r\n[S]:SCAN_SIGNAL_CHECK_BY_VIF");
//                        printf("\r\n<< %u KHz  ", m_u32AutoScanFreq);
                        ATV_Scan_DBG("\r\n[%uKHz ", m_u32AutoScanFreq);
                            wTmpIf = m_pDemodulator->ATV_GetAFC_Distance();
                            ATV_Scan_DBG("VIF:%x..", wTmpIf);
                        if((wTmpIf != mapi_demodulator_datatype::E_AFC_OUT_OF_AFCWIN) || bPollingVifLock) // VIF Lock
                        {
                            u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_NOP_1;
                            ATV_Scan_DBG("VIF LOCK..");
//                            printf("\r\n VIF LOCK..");
                        }
                        else // No Lock
                        {
//                            ATV_Scan_DBG("no lock]");
//                            printf(" \r\n VIF no lock]");
                            if(m_bIsFranceSearch == true) //pGOSDk
                            {
                                if((u8ATVScanSoundSystemType == TV_SOUND_LL)
                                        || (u8ATVScanSoundSystemType == TV_SOUND_L))
                                {
                                    u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_START_PAL;
                                    break;
                                }
                            }

                            if(m_manualSearchType == SearchUp) // Auto search or Manual up search
                            {
                                m_u32AutoScanFreq += BIG_STEP;
                            }
                            else
                            {
                                m_u32AutoScanFreq -= BIG_STEP;
                            }

                            u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_START;
                        }
                        break;

                    case MW_ATV_Scan_EU::PAL_SCAN_NOP_1:
                        ATV_Scan_FLOW_DBG("\r\n[S]:SCAN_NOP_1");
                        u8TVScanStep++;
                        bPollingVdLock = 0;
                        break;

                    case MW_ATV_Scan_EU::PAL_SCAN_SIGNAL_CHECK_BY_VD:
                        ATV_Scan_FLOW_DBG("\r\n[S]:SCAN_SIGNAL_CHECK_BY_VD");
//                        printf("\r\n [S]:SCAN_SIGNAL_CHECK_BY_VD");
                        wTmpVd = mapi_interface::Get_mapi_vd()->CheckStatusLoop();
                        //wTmpVd = mapi_interface::Get_mapi_vd()->CheckStatusLoop();
                        ATV_Scan_DBG("VD:%x..", wTmpVd);
                        if((wTmpVd & VD_CHECK_HSYNC_LOCKED) || bPollingVdLock)  // VD Lock
                        {
                            u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_NOP_2;
                            ATV_Scan_DBG("VD LOCK!]");
//                            printf("VD LOCK!]");
                        }
                        else
                        {
                            u8TVScanStep =  MW_ATV_Scan_EU::PAL_SCAN_SIGNAL_WEAK;
                        }
                        break;

                    case MW_ATV_Scan_EU::PAL_SCAN_SIGNAL_WEAK:
                        ATV_Scan_FLOW_DBG("\r\n[S]:SCAN_SIGNAL_WEAK");
                        ATV_Scan_DBG("no lock]");

                        if(m_bIsFranceSearch == true) //pGOSDk
                        {
                            if((u8ATVScanSoundSystemType == TV_SOUND_LL)
                                    || (u8ATVScanSoundSystemType == TV_SOUND_L))
                            {
                                u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_START_PAL;
                                break;
                            }
                        }

                        #if (MSTAR_TVOS == 1)
                        if(m_manualSearchType == SearchUp) // Auto search or Manual up search
                        {
                            m_u32AutoScanFreq += STEP_500K;
                        }
                        else
                        {
                            m_u32AutoScanFreq -= STEP_500K;
                        }
                        #else
                        if(m_manualSearchType == SearchUp)
                        {
                            m_u32AutoScanFreq += BIG_STEP;
                        }
                        else
                        {
                            m_u32AutoScanFreq -= BIG_STEP;
                        }
                        #endif

                        u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_START;
                        break;

                    case MW_ATV_Scan_EU::PAL_SCAN_NOP_2:
                        ATV_Scan_FLOW_DBG("\r\n[S]:SCAN_NOP_2");
                        u32FineTuneFreq = STEP_1d5M;//STEP_500K
                        u8TVScanStep++;
                        break;

                    case MW_ATV_Scan_EU::PAL_SCAN_FIND_GOOD_AFC:
                        ATV_Scan_FLOW_DBG("\r\n[S]:SCAN_FIND_GOOD_AFC");

                        wTmpVd = mapi_interface::Get_mapi_vd()->CheckStatusLoop();
                        ATV_Scan_DBG("VD:%x..", wTmpVd);
                        if(wTmpVd & MAPI_VD_HSYNC_LOCKED)
                        {
                                wTmpIf = m_pDemodulator->ATV_GetAFC_Distance();
                                ATV_Scan_DBG("\r\nATV_GetAFC_Distance = %x", wTmpIf);
                            if(wTmpIf != mapi_demodulator_datatype::E_AFC_OUT_OF_AFCWIN)
                            {
                                ucAFCOffset = CheckAfcWinPass(wTmpIf);
                            }
                            else
                            {
                                if(m_manualSearchType == SearchUp)
                                {
                                    ucAFCOffset = AFC_BIG_STEP_INC;
                                }
                                else
                                {
                                    ucAFCOffset = AFC_BIG_STEP_DEC;
                                }
                            }
                            //ATV_Scan_DBG("VD Lock");

                            if(ucAFCOffset == AFC_GOOD)
                            {
                                ATV_Scan_DBG("\r\nGOOD AFC");
                                u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_NOP_5; //SCAN_CNI_START; //bSaveFlag = TRUE;
                            }
                            else if(ucAFCOffset == AFC_INCREASE)
                            {
                                ATV_Scan_DBG("\r\nINC AFC ");
                                m_u32AutoScanFreq += m_u8TunerMinDistance;
                                u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_NOP_3;
                            }
                            else if(ucAFCOffset == AFC_DECREASE)
                            {
                                ATV_Scan_DBG("\r\nDEC AFC ");
                                m_u32AutoScanFreq -= m_u8TunerMinDistance;
                                u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_NOP_4;
                            }
                            else if(ucAFCOffset == AFC_BIG_STEP_INC)
                            {
                                ATV_Scan_DBG("\r\nBIG INC ");
                                m_u32AutoScanFreq += SMALL_STEP;

                                u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_START;
                            }
                            else
                            {
                                ATV_Scan_DBG("\r\nBIG DEC ");
                                m_u32AutoScanFreq -= SMALL_STEP;

                                u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_START;
                            }
                        }
                        else
                        {
                            u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_NOP_0_PAL;//MW_ATV_Scan_EU::PAL_SCAN_SIGNAL_CHECK_BY_VIF;
                        }
                        break;

                    case MW_ATV_Scan_EU::PAL_SCAN_NOP_3:
                        ATV_Scan_FLOW_DBG("\r\n[S]:SCAN_NOP_3");
                        u8TVScanStep++;
                        break;

                    case PAL_SCAN_FINE_INCREASE:
                        ATV_Scan_FLOW_DBG("\r\n[S]:SCAN_FINE_INCREASE");
                        wTmpVd = mapi_interface::Get_mapi_vd()->CheckStatusLoop();
                            wTmpIf = m_pDemodulator->ATV_GetAFC_Distance();
                        ATV_Scan_DBG("\r\nINC[%u]KHz", m_u32AutoScanFreq);
                        ATV_Scan_DBG(" VD[%x]", wTmpVd & MAPI_VD_HSYNC_LOCKED);
                        ATV_Scan_DBG(" AFC[%x]", wTmpIf);
                        if((wTmpVd & VD_CHECK_HSYNC_LOCKED) && (wTmpIf != mapi_demodulator_datatype::E_AFC_OUT_OF_AFCWIN))
                        {
                            ucAFCOffset = CheckAfcWinPass(wTmpIf);

                            if(ucAFCOffset == AFC_GOOD)
                            {
                                u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_NOP_5; //SCAN_CNI_START; //bSaveFlag = TRUE;
                            }
                            else
                            {
                                if((ucAFCOffset == AFC_INCREASE)||(ucAFCOffset == AFC_BIG_STEP_INC))
                                {
                                    u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_NOP_3;
                                    m_u32AutoScanFreq += m_u8TunerMinDistance;
                                }
                                else
                                {
                                    u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_NOP_4;
                                    m_u32AutoScanFreq -= m_u8TunerMinDistance;
                                }

                                if(u32FineTuneFreq)  //avoid increase & decrease
                                {
                                    if(u32FineTuneFreq > m_u8TunerMinDistance)
                                    {
                                        u32FineTuneFreq -= m_u8TunerMinDistance;
                                    }
                                    else
                                    {
                                        u32FineTuneFreq = 0;
                                    }
                                }
                                else
                                {
                                    ATV_Scan_DBG("\r\nFinetune fail");
                                    u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_SIGNAL_WEAK;
                                }
                            }
                        }
                        else
                        {
                            u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_SIGNAL_WEAK;
                        }
                        break;

                    case MW_ATV_Scan_EU::PAL_SCAN_NOP_4:
                        u8TVScanStep++;
                        break;

                    case MW_ATV_Scan_EU::PAL_SCAN_FINE_DECREASE:
                        ATV_Scan_FLOW_DBG("\r\n[S]:SCAN_FINE_DECREASE");
                        wTmpVd = mapi_interface::Get_mapi_vd()->CheckStatusLoop();
                        wTmpIf = m_pDemodulator->ATV_GetAFC_Distance();
                        ATV_Scan_DBG("\r\nDEC[%u]KHz", m_u32AutoScanFreq);
                        ATV_Scan_DBG(" VD[%x]", wTmpVd & VD_CHECK_HSYNC_LOCKED);
                        ATV_Scan_DBG(" AFC[%x]", wTmpIf);
                        if((wTmpVd & VD_CHECK_HSYNC_LOCKED) && (wTmpIf != mapi_demodulator_datatype::E_AFC_OUT_OF_AFCWIN))
                        {
                            ucAFCOffset = CheckAfcWinPass(wTmpIf);

                            if(ucAFCOffset == AFC_GOOD)
                            {
                                u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_NOP_5; //SCAN_CNI_START; //bSaveFlag = TRUE;
                            }
                            else
                            {
                                if((ucAFCOffset == AFC_INCREASE)||(ucAFCOffset == AFC_BIG_STEP_INC))
                                {
                                    u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_NOP_3;
                                    m_u32AutoScanFreq += m_u8TunerMinDistance;
                                }
                                else
                                {
                                    u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_NOP_4;
                                    m_u32AutoScanFreq -= m_u8TunerMinDistance;
                                }

                                if(u32FineTuneFreq)  //avoid increase & decrease
                                {
                                    if(u32FineTuneFreq > m_u8TunerMinDistance)
                                    {
                                        u32FineTuneFreq -= m_u8TunerMinDistance;
                                    }
                                    else
                                    {
                                        u32FineTuneFreq = 0;
                                    }
                                }
                                else
                                {
                                    ATV_Scan_DBG("\r\nFinetune fail");
                                    u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_SIGNAL_WEAK;
                                }
                            }
                        }
                        else
                        {
                            u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_SIGNAL_WEAK;
                        }
                        break;

                    case MW_ATV_Scan_EU::PAL_SCAN_NOP_5:
                        u8AudioDetectCount = 0;
                        if(m_bIsFranceSearch == true) //pGOSDk
                        {
                            ucTmpSoundSystem = u8ATVScanSoundSystemType;
                            // double check SECAM_LL
                            if(m_u32AutoScanFreq < PAL_FREQ_SECAM_L)
                            {
                                ATV_Scan_DBG("\r\nSECAM_LL check");
                                m_bIsLLSearch = true;
                                u8ATVScanSoundSystemType = TV_SOUND_LL;
                            }
                            else
                            {
                                ATV_Scan_DBG("\r\nSECAM_L check");
                                m_bIsLLSearch = false;
                                u8ATVScanSoundSystemType = TV_SOUND_L;
                            }

                            //wTmpVd = mapi_interface::Get_mapi_vd()->GetVideoStandardDetection(&wTmpVd);
                            //ATV_Scan_DBG("\r\nVD Std. Test = %d", wTmpVd);

                            if(ucTmpSoundSystem != u8ATVScanSoundSystemType)
                            {
                                VifSetSoundStandard(u8ATVScanSoundSystemType);
                                VifSetFrequency(m_u32AutoScanFreq);
                            }
                            /*
                            if (wTmpVd == E_MAPI_VIDEOSTANDARD_SECAM)
                            {
                                ATV_Scan_DBG("\r\nCheckHsyncEdge");
                                if (msCheckHsyncEdge())
                                {
                                    wTmpVd = E_MAPI_VIDEOSTANDARD_PAL_BGHI;
                                }
                            }

                            if (E_MAPI_VIDEOSTANDARD_SECAM == wTmpVd)
                            {
                                // STORE TO DATABASE
                                //g_PalCurChannelData.ucColorSystem = TV_COLOR_SECAM;
                                //g_PalCurChannelData.ucSoundSystem = u8ATVScanSoundSystemType;
                                //g_PalCurChannelData.ucAutoSound = devAudioGetAutoStatus(TV_COLOR_SECAM);//u8ATVScanSoundSystemType;
                                u8TVScanStep = PAL_SCAN_CNI_START;
                                break;
                            }
                            else
                            {
                            */
                            u8TVScanStep++;
                            //}
                        }
                        else
                        {
                            //printf(" \33[0;31m <><Add Checking Mechjanism for channel lost in Germany Filed Test><> \n \33[m");

                            MEMBER_COUNTRY eCountry;
                            eCountry = MSrv_Control::GetMSrvSystemDatabase()->GetSystemCountry();
                            if(eCountry == E_GERMANY)
                            {
                                //printf(" \33[0;32m Now Country = E_GERMANY \n \33[m");

                                if(mapi_interface::Get_mapi_vd()->IsLockAudioCarrier()&&(m_u32AutoScanFreq <= (m_u32PrevScanFreq + PAL_NEXT_CHANNEL_BOUND)))
                                {
                                    printf(" \33[0;31m >>>abandan this audio carrier Freq = %u KHz !!!! \n \33[m",m_u32AutoScanFreq);
                                    if(m_manualSearchType == SearchUp)
                                    {
                                        m_u32AutoScanFreq += BIG_STEP;
                                    }
                                    else
                                    {
                                        m_u32AutoScanFreq -= BIG_STEP;
                                    }

                                    u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_START;
                                    break;
                                }

                            }



                            //u8TVScanStep = PAL_SCAN_DETECT_AUDIO_SYSTEM;
                            u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_NOP_6;
                        }
                        break;

                    case MW_ATV_Scan_EU::PAL_SCAN_DETECT_VIDEO_SYSTEM:
//                        ATV_Scan_FLOW_DBG("\r\n[S]:SCAN_DETECT_VIDEO_SYSTEM");
//                        printf("\r\n[S]:SCAN_DETECT_VIDEO_SYSTEM");
                        wTmpVd = mapi_interface::Get_mapi_vd()->GetVideoStandardDetection(&wTmpVd);
                        ATV_Scan_DBG("\r\nVD Std. Test = %d", wTmpVd);
//                         printf("\r\n VD Std. Test = %d\r\n ", wTmpVd);
                        if(m_bIsFranceSearch == true) //pGOSDk
                        {
                            ucTmpSoundSystem = u8ATVScanSoundSystemType;
//                          printf("\r\n u8ATVScanSoundSystemType = %d\r\n ", u8ATVScanSoundSystemType);

                            if(wTmpVd == E_MAPI_VIDEOSTANDARD_SECAM)
                            {
                                ATV_Scan_DBG("\r\nCheckHsyncEdge");
//                                printf("\r\nCheckHsyncEdge");
                                if(msCheckHsyncEdge())
                                {
                                    wTmpVd = E_MAPI_VIDEOSTANDARD_PAL_BGHI;
//                                    printf("\r\n wTmpVd = %d",wTmpVd);
                                }
                            }

                            if(wTmpVd != E_MAPI_VIDEOSTANDARD_SECAM)  // PAL detect
                            {
                                if(u8AudioDetectCount < 10)
                                {
                                    u8AudioDetectCount++;
                                    u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_NOP_5 + 1;
                                    //VifSetSoundStandard(u8ATVScanSoundSystemType);
                                    //VifSetFrequency(m_u32AutoScanFreq);
                                    break;
                                }
                                else
                                {
                                    ATV_Scan_DBG("\r\n It's PAL");
//                                     printf("\r\n It's PAL \r\n ");
                                    m_bIsLLSearch = false;
                                    u8ATVScanSoundSystemType = TV_SOUND_DK;
                                }
                            }
                            else // SECAM_LL detect // SECAM_L detect
                            {
                                if(m_u32AutoScanFreq < PAL_FREQ_SECAM_L)
                                {
                                    ATV_Scan_DBG("\r\nIt's SECAM_LL");
//                                    printf("\r\nIt's SECAM_LL");
                                    m_bIsLLSearch = true;
                                    u8ATVScanSoundSystemType = TV_SOUND_LL;
                                }
                                else
                                {
                                    ATV_Scan_DBG("\r\nIt's SECAM_L");
//                                    printf("\r\nIt's SECAM_L");
                                    m_bIsLLSearch = false;
                                    u8ATVScanSoundSystemType = TV_SOUND_L;
                                }
                                mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard((AUDIOSTANDARD_TYPE_)E_AUDIOSTANDARD_L_);
                                // STORE TO DATABASE
                                //g_PalCurChannelData.ucColorSystem = TV_COLOR_SECAM;
                                //g_PalCurChannelData.ucSoundSystem = u8ATVScanSoundSystemType;
                                //g_PalCurChannelData.ucAutoSound = devAudioGetAutoStatus(TV_COLOR_SECAM);//u8ATVScanSoundSystemType;
                                u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_CNI_START; //bSaveFlag = TRUE;
                                break;
                            }

                            if(ucTmpSoundSystem != u8ATVScanSoundSystemType)
                            {
                                VifSetSoundStandard(u8ATVScanSoundSystemType);
                                VifSetFrequency(m_u32AutoScanFreq);
                            }

                            u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_NOP_6;
                        }
                        else
                        {
                            u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_NOP_6;
                        }
                        break;

                    case MW_ATV_Scan_EU::PAL_SCAN_NOP_6:
                        mapi_interface::Get_mapi_vif()->BypassDBBAudioFilter(TRUE);
                        mapi_interface::Get_mapi_audio()->SIF_StartAutoStandardDetection();
                        u8AudioDetectCount = 0;
                        u8TVScanStep++;
                        break;

                    case MW_ATV_Scan_EU::PAL_SCAN_DETECT_AUDIO_SYSTEM:
                        ATV_Scan_FLOW_DBG("\r\n[S]:SCAN_DETECT_AUDIO_SYSTEM");
                        {
#if 1
                            AUDIOSTANDARD_TYPE_ detectedAudioType = mapi_interface::Get_mapi_audio()->SIF_GetResultOfAutoStandardDetection();

                            ATV_Scan_DBG("\r\nAUDIO_SYSTEM = [%x],\n", detectedAudioType);
//                            printf("\r\n AUDIO_SYSTEM = [%x],\n", detectedAudioType);
                            //AUDIOSTANDARD_TYPE_ audioType = mapi_interface::Get_mapi_audio()->SIF_GetAudioStandard();

                            if(mapi_interface::Get_mapi_audio()->SIF_GetAudioStandard() != E_AUDIOSTANDARD_L_)
                            {
                                AUDIOSTANDARD_TYPE_ AudioType = detectedAudioType;
                                if(detectedAudioType == E_AUDIOSTANDARD_NOTSTANDARD_)
                                {
                                    AudioType = E_AUDIOSTANDARD_BG_;
                                }

                                mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard(AudioType);
                                if(m_IsManualScan == TRUE)
                                {
                                    if(detectedAudioType == E_AUDIOSTANDARD_BG_)
                                    {
                                        VifSetSoundStandard(TV_SOUND_BG);
                                        // It depends on the picture carrier of each tuner driver. ex. tuner NXP_TDA18275 (since CL769556), tuner SI_2158
                                        // Because the distance of picture carrier between DK and BG is too far, tuner AFT needs more time to shift frequency.
                                        // VIF would be unlocked until picture carrier shift to BG.
                                        // For the suggestion from VIF team, do SetTune again will let picture carrier shift faster
                                        VifSetFrequency(m_u32AutoScanFreq, E_AUDIOSTANDARD_BG_);
                                    }
                                    else if(detectedAudioType == E_AUDIOSTANDARD_I_)
                                    {
                                        VifSetSoundStandard(TV_SOUND_I);
                                    }
                                }

                            }
                            ATV_Scan_DBG("\r\nAUDIO_SYSTEM = [%x],\n", detectedAudioType);
                            //g_PalCurChannelData.ucColorSystem = TV_COLOR_AUTO;
                            //g_PalCurChannelData.ucSoundSystem = TV_SOUND_AUTO;
                            //g_PalCurChannelData.ucAutoSound = devAudioStdDetect(TV_COLOR_AUTO) & 0x0F;
                            if(detectedAudioType == E_AUDIOSTANDARD_NOTSTANDARD_)
                            {
                                if(u8AudioDetectCount < 1)
                                {
                                    u8AudioDetectCount++;
                                    u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_NOP_6 + 10;
                                }
                                else
                                {
                                    // force clk shift to VD lock
                                    if(m_pDemodulator->ATV_IsInternalVIF())  // MStar VIF
                                    {
                                        VifSetFrequency(m_u32AutoScanFreq + STEP_250K);
                                    }
                                    else
                                    {
                                        VifSetFrequency(m_u32AutoScanFreq + STEP_500K);
                                    }

                                    u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_NOP_7;
                                }
                            }
                            else
#endif
                            {
                                //wTmpIf = devAudioCntStd(g_PalCurChannelData.ucAutoSound);
                                //devTunerSetSubSys( GetTunerSoundSystem(wTmpIf) );
                                //devTunerSetFreq( m_u32AutoScanFreq );
                                u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_CNI_START; //bSaveFlag = TRUE;
                            }
                            break;
                        }
                    case MW_ATV_Scan_EU::PAL_SCAN_NOP_7:
                        u8AudioDetectCount = 0;
                        u8TVScanStep++;
                        break;

                    case MW_ATV_Scan_EU::PAL_SCAN_CHECK_FALSE_CHANNEL:
                        ATV_Scan_FLOW_DBG("\r\n[S]:SCAN_CHECK_FALSE_CHANNEL");
                        if(u8AudioDetectCount < 6)
                        {
                            u8AudioDetectCount++;
                            u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_NOP_7 + 1;
                        }
                        else
                        {
                            wTmpVd = mapi_interface::Get_mapi_vd()->CheckStatusLoop();
                            wTmpIf = m_pDemodulator->ATV_GetAFC_Distance();
                            ATV_Scan_DBG("\r\nVD[%x]", wTmpVd);
//                            printf("\r\n VD(MW_ATV_Scan_EU::PAL_SCAN_CHECK_FALSE_CHANNEL)[%x]", wTmpVd);
                            ATV_Scan_DBG(" TN[%x]", wTmpIf);
                            if((wTmpVd & VD_CHECK_HSYNC_LOCKED) && (wTmpIf != mapi_demodulator_datatype::E_AFC_OUT_OF_AFCWIN))
                            {
                                //VifSetSoundStandard(u8ATVScanSoundSystemType);
                                VifSetFrequency(m_u32AutoScanFreq);
                                u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_CNI_START; //bSaveFlag = TRUE;
                                ATV_Scan_DBG("\r\n< Audio fail >");
//                                printf("\r\n< Audio fail >");
                            }
                            else
                            {
                                if(m_manualSearchType == SearchUp) // Auto search or Manual up search
                                {
                                    m_u32AutoScanFreq += BIG_STEP;
                                }
                                else
                                {
                                    m_u32AutoScanFreq -= BIG_STEP;
                                }

                                u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_START;
                                ATV_Scan_DBG("\r\n< False Channel >");
                            }
                        }
                        break;

                    default:
                        if((u8TVScanStep > (MW_ATV_Scan_EU::PAL_SCAN_NOP_0 + PAL_SCAN_VIF_HALF_POLLING_LOOP))
                                && (u8TVScanStep < MW_ATV_Scan_EU::PAL_SCAN_SIGNAL_CHECK_BY_VIF))//polling check VIF lock
                        {
                            wTmpIf = m_pDemodulator->ATV_GetAFC_Distance();
                            if(wTmpIf != mapi_demodulator_datatype::E_AFC_OUT_OF_AFCWIN)
                            {
                                bPollingVifLock = 1;
                                u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_SIGNAL_CHECK_BY_VIF;
                                break;
                            }
                        }
                        else if((u8TVScanStep > (MW_ATV_Scan_EU::PAL_SCAN_NOP_1 + PAL_SCAN_VD_HALF_POLLING_LOOP))
                                && (u8TVScanStep < MW_ATV_Scan_EU::PAL_SCAN_SIGNAL_CHECK_BY_VD))//polling check VD lock
                        {
                            wTmpVd = mapi_interface::Get_mapi_vd()->CheckStatusLoop();
                            if(wTmpVd & VD_CHECK_HSYNC_LOCKED)   // VD Lock
                            {
                                bPollingVdLock = 1;
                                u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_SIGNAL_CHECK_BY_VD;
                                break;
                            }
                        }
                        else if((u8TVScanStep > MW_ATV_Scan_EU::PAL_SCAN_CNI_START) && (u8TVScanStep < MW_ATV_Scan_EU::PAL_SCAN_CNI_END))
                        {
#if (TTX_ENABLE == 1)
                            //if(MW_TTX::GetInstance()->DoesHaveTTXSignal())
                            {
                                if(mapi_interface::Get_mapi_cni()->GetStationName(m_au8CurrentStationName, MAX_STATION_NAME, &m_u8SortingPriority))
                                {
                                    m_bCNIStatus = TRUE;
                                    u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_CNI_END;
                                    break;
                                }
                            }
#endif
                        }
                        u8TVScanStep++;
                        break;
                    case MW_ATV_Scan_EU::PAL_SCAN_CNI_START:
                        // For Secam/PAL TTX format different
                        wTmpVd = mapi_interface::Get_mapi_vd()->GetVideoStandardDetection(&wTmpVd);
                        ATV_Scan_DBG("\r\nVD Std. Test = %d", wTmpVd);
//                        printf("\r\nVD Std. Test(1) = %d\r\n ", wTmpVd);
                        if(wTmpVd == E_MAPI_VIDEOSTANDARD_SECAM)
                        {
                            mapi_interface::Get_mapi_vd()->SetVideoStandard(E_MAPI_VIDEOSTANDARD_SECAM);
#if 1
                            if(m_bIsFranceSearch == true) //pGOSDk
                            {
                                ucTmpSoundSystem = u8ATVScanSoundSystemType;

                                if(m_u32AutoScanFreq < PAL_FREQ_SECAM_L)
                                {
                                    ATV_Scan_DBG("\r\nIt's SECAM_LL");
                                    printf("\r\n It's SECAM_LL\r\n");
                                    m_bIsLLSearch = true;
                                    u8ATVScanSoundSystemType = TV_SOUND_LL;
                                }
                                else
                                {
                                    ATV_Scan_DBG("\r\nIt's SECAM_L");
                                    printf("\r\nIt's SECAM_L\r\n ");
                                    m_bIsLLSearch = false;
                                    u8ATVScanSoundSystemType = TV_SOUND_L;
                                }
                                mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard((AUDIOSTANDARD_TYPE_)E_AUDIOSTANDARD_L_);

                                if(ucTmpSoundSystem != u8ATVScanSoundSystemType)
                                {
                                    VifSetSoundStandard(u8ATVScanSoundSystemType);
                                    VifSetFrequency(m_u32AutoScanFreq);
                                }

                            }
#endif
                        }
                        else
                        {
                            mapi_interface::Get_mapi_vd()->SetVideoStandard(E_MAPI_VIDEOSTANDARD_PAL_BGHI);
                        }

#if (TTX_ENABLE == 1)
                        MW_TTX::GetInstance()->Reset();
#endif

                        memset(m_au8CurrentStationName, 0 , sizeof(m_au8CurrentStationName));
                        m_u8SortingPriority = LOWEST_SORTING_PRIORITY;
                        m_bCNIStatus = FALSE;
                        u8TVScanStep++;
                        break;
                    case MW_ATV_Scan_EU::PAL_SCAN_CNI_END:
                        ATV_Scan_FLOW_DBG("\r\n[S]:SCAN_CNI_END");
                        printf("\r\n<< %u KHz  ", m_u32AutoScanFreq); // force to print message
                        printf("Ch[%d]", (int)m_u8ChannelCount);
                        //ATV_Scan_DBG(">>\r\n", m_u8ChannelCount);

                        if(m_IsManualScan == TRUE)
                        {
                            U16 u16VdStatus = 0;
                            //MemorizeProg(m_u32AutoScanFreq);
                            ATV_Scan_LOG_FILE(fclose(pFile);)

                            m_bATVScanThreadEnable = FALSE;
                            m_IsLocked = TRUE;
                            //m_u32StartFreq = m_u32AutoScanFreq;
                            u16VdStatus = mapi_interface::Get_mapi_vd()->GetVideoStandardDetection(&u16VdStatus);
                            mapi_interface::Get_mapi_vd()->SetVideoStandard((MAPI_AVD_VideoStandardType)u16VdStatus, MAPI_FALSE);

                            if( (m_bIsFranceSearch == true) && (m_u32AutoScanFreq < PAL_FREQ_SECAM_L) && (mapi_interface::Get_mapi_audio()->SIF_GetAudioStandard() == E_AUDIOSTANDARD_L_) )
                            {   // for SECAM L' in France
                                mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard(mapi_interface::Get_mapi_audio()->SIF_GetAudioStandard());
                                VifSetSoundStandard(TV_SOUND_LL);
                            }
                            else
                            {
                                SetSoundSystem(mapi_interface::Get_mapi_audio()->SIF_GetAudioStandard());
                            }

                            // It depends on the picture carrier of each tuner driver.
                            // For the suggestion from VIF team, do SetTune again will let picture carrier shift faster for all case in manual tuning.
                            #if (MSTAR_TVOS == 0)
                                AUDIOSTANDARD_TYPE_ AudioType = mapi_interface::Get_mapi_audio()->SIF_GetAudioStandard();
                                VifSetFrequency(m_u32AutoScanFreq, mapi_interface::Get_mapi_audio()->SIF_ConvertToBasicAudioStandard(AudioType));
                            #endif

                            SendScanInfo();
                            m_IsManualScan = FALSE;
                            //MSrv_Control::GetMSrvAtv()->_SetTunerPLL(((m_u32AutoScanFreq * 100 / 625)+5)/10);
                            //MSrv_Control::GetMSrvAtv()->_DetectStationName();
                            break;
                        }
                        else
                        {
                            // xs AFT Info
                            //g_PalCurChannelData.ucAftMode = AFT_MODE_ON | AFT_OFFSET_0;
//                            printf("\r\n =========== Store to DATABASE and EEPROM ===========");
                            // STORE TO DATABASE & EEPROM
                            MemorizeProg(m_u32AutoScanFreq, FALSE);

                            mapi_interface::Get_mapi_vif()->BypassDBBAudioFilter(FALSE);

                            if((u8ATVScanSoundSystemType == TV_SOUND_LL)
                                    || (u8ATVScanSoundSystemType == TV_SOUND_L)
                                    || (u8ATVScanSoundSystemType == TV_SOUND_M))
                            {
                                m_u32AutoScanFreq += SECAM_NEXT_STEP; // Add 4M Hz
                            }
                            else
                            {
                                m_u32AutoScanFreq += PAL_NEXT_STEP; // Add 6M Hz
                            }
                        }

                        m_u8ChannelCount++;
                        u8TVScanStep = MW_ATV_Scan_EU::PAL_SCAN_START;
                        break;
                }
            }
            else
            {
                U32 m_u32TmpTime = mapi_time_utility::GetTime0();
                U32 sleepTime =  20 - mapi_time_utility::TimeDiffFromNow0(m_u32StartTime);
                //if(sleepTime < 20 && sleepTime>=0)
                if(sleepTime < 20)
                {
                    ATV_Scan_DBG("time out for %u", sleepTime * 1000);
                    usleep(sleepTime * 1000);
                }
                m_U32TimeOnSleep += mapi_time_utility::TimeDiffFromNow0(m_u32TmpTime);
            }

            m_pDemodulator->ATV_VIF_Handler(TRUE);

            {
                static BOOL lastLLSearch = FALSE;

                if(m_IsManualScan == TRUE)
                {
                    if(m_manualSearchType == SearchUp)
                    {
                        if(m_u32AutoScanFreq > PAL_FREQ_MAX)
                        {
                            m_u32AutoScanFreq = PAL_FREQ_MIN;
                        }
                    }
                    else
                    {
                        if(m_u32AutoScanFreq < PAL_FREQ_MIN)
                        {
                            m_u32AutoScanFreq = PAL_FREQ_MAX;
                        }
                    }
                }

#if ( ENABLE_SKIP_SAME_FREQ )
                if((m_u32PrevScanFreq != m_u32AutoScanFreq) || (lastLLSearch != m_bIsLLSearch))
#endif
                {
                    // For OSD update
                    switch(m_u8TunerMinDistance)
                    {
                        case 31:
                        {
                            U16 u16TunerPLL = (((m_u32AutoScanFreq * 1000) / 3125) + 5) / 10;
                            m_u32AutoScanFreq = (u16TunerPLL * 3125) / 100;
                            break;
                        }
                        case 62:
                        {
                            U16 u16TunerPLL = (((m_u32AutoScanFreq * 100) / 625) + 5) / 10;
                            m_u32AutoScanFreq = (u16TunerPLL * 625) / 10;
                            break;
                        }
                        default:
                            break;
                    }

                    U32 m_u32TmpTime = mapi_time_utility::GetTime0();
                    VifSetFrequency(m_u32AutoScanFreq);
                    m_u32PrevScanFreq = m_u32AutoScanFreq;
                    m_U32TimeOnI2C += mapi_time_utility::TimeDiffFromNow0(m_u32TmpTime);
                    SendScanInfo();
                    if((lastLLSearch != m_bIsLLSearch))
                    {
                        lastLLSearch = m_bIsLLSearch;
                    }
                }
            }

            //pthread_mutex_unlock(&MSrv_Control::GetMSrvAtv()->m_mutex_Scan);
            ATV_Scan_LOG_FILE(fclose(pFile);)
        }
        else
        {
            usleep(20 * 1000);
        }
    }

    //Channel Sorting
    ChannelSortByPri(MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL));

    //Init VIF
    //m_pDemodulator->ATV_VIF_Init();

    //#endif
    //pthread_mutex_unlock(&MSrv_Control::GetMSrvAtv()->m_mutex_Scan);
    if(TRUE == bDebugEnable)
    {
        m_U32TimeOnScan = mapi_time_utility::TimeDiffFromNow0(m_U32TimeOnScan);
        ATV_Scan_PRINT("\n_ATV_Scan_Thread Exit Success\n m_U32NumOfScan=%u, at %d\n", m_U32NumOfScan, __LINE__);
        ATV_Scan_PRINT("\n_ATV_Scan_Thread Exit Success\n m_U32TimeOnScan=%u, at %d\n", m_U32TimeOnScan, __LINE__);
        ATV_Scan_PRINT("\n_ATV_Scan_Thread Exit Success\n m_U32TimeOnI2C=%u\n", m_U32TimeOnI2C);
        ATV_Scan_PRINT("\n_ATV_Scan_Thread Exit Success\n m_U32TimeOnSleep=%u\n", m_U32TimeOnSleep);
    }
    mapi_interface::Get_mapi_vd()->SetHsyncDetectionForTuning(FALSE);
    if(pVDData != NULL)
    {
        delete pVDData;
        pVDData = NULL;
    }
    ATV_Scan_DBG("\n_ATV_Scan_Thread Exit Success\n Freq=%d, at %d\n", m_u32StartFreq, __LINE__);
}

BOOL MW_ATV_Scan_EU::DoInit(void)
{
    return TRUE;
}

void MW_ATV_Scan_EU::DoStart(void)
{
    SetupAutoScanPara(E_AUDIOSTANDARD_DK_);

    if(m_pDemodulator->ATV_IsInternalVIF())  // MStar VIF
    {
        m_u32FreqSmallStep = STEP_125K;//STEP_187d5K;
    }
    else
    {
        m_u32FreqSmallStep = STEP_250K;
    }

    m_u8ChannelCount = CHANNEL_MIN_NUM;
}

void MW_ATV_Scan_AsiaChina::DoScan() // including NTSC check
{
    //////////////////////////////////////////////////////////////////////////////
    int u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_START;
    U8 u8ATVScanSoundSystemType = 0;
    U32 u32FineTuneFreq = 0;
    U8 u8AudioDetectCount = 0;
    U8 bPollingVifLock = 0;
    U8 bPollingVdLock = 0;
    U8 u8ProtectCount = 0;

    mapi_demodulator_datatype::AFC wTmpIf;
    U8 ucAFCOffset;
    U16 wTmpVd;
    U32 m_u32StartTime = mapi_time_utility::GetTime0();
    #if (MSTAR_TVOS == 1)
    U32 u32BreakPointStartTime = mapi_time_utility::GetTime0();
    #endif
    U32 u32TmpTime = 0;
    U32 u32TmpTimeAlternate = 0;
    BOOL bDebugEnable = m_bDebugEnable;
    AUDIOSTANDARD_TYPE_ ePrevAudioType = E_AUDIOSTANDARD_NOTSTANDARD_;

    //////////////////////////////////////////////////////////////////////////////

    //U8 waitTime;
    //char buf[10];
    ATV_Scan_LOG_FILE(FILE * pFile;)
    ATV_Scan_LOG_FILE(pFile = fopen("/Customer/scan_history.txt", "w");)
    ATV_Scan_LOG_FILE(fclose(pFile);)
    ATV_Scan_LOG_FILE(pFile = fopen("/Customer/vd_lock_history.txt", "w");)
    ATV_Scan_LOG_FILE(fclose(pFile);)
    ATV_Scan_DBG("=======================================================\nEnter Thread Body (Asean_China)\n==================================================\n");
    mapi_interface::Get_mapi_vd()->StartAutoStandardDetection();
    mapi_interface::Get_mapi_vd()->SetHsyncDetectionForTuning(TRUE);
    //pthread_mutex_lock(&MSrv_Control::GetMSrvAtv()->m_mutex_Scan);
    mapi_scope_lock(scopeLock, &MSrv_Control::GetMSrvAtv()->m_mutex_Scan);

    MSrv_Control::GetInstance()->SetVideoMute(TRUE);
    //set the height of window size to 480 for avoiding showing garbage (ex. source:480 window: 576 => garbage displayed on the bottom )
    mapi_video_vd_cfg *pVDData = new(std::nothrow) mapi_video_vd_cfg;
    ASSERT(pVDData);
    pVDData->enVideoStandard = E_MAPI_VIDEOSTANDARD_PAL_M;
    mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetMode(pVDData);
    mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO stVideoARCInfo;
    memset(&stVideoARCInfo, 0, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO));
    mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetWindow(NULL, NULL, &stVideoARCInfo);
    MSrv_Control::GetInstance()->SetVideoMute(FALSE);

    if(TRUE == bDebugEnable)
    {
        m_U32TimeOnSleep = 0;
        m_U32TimeOnScan = mapi_time_utility::GetTime0();
        m_U32TimeOnI2C = 0;
        m_U32NumOfScan = 0;
        m_U32ATVTimerTotal = 0;

        _ClearAllTimer();
        u32TmpTime = mapi_time_utility::GetTime0();
    }
    while(m_bATVScanThreadEnable)
    {
        if(m_bScanContinue)
        {
            #if (MSTAR_TVOS == 1)
            if(mapi_time_utility::TimeDiffFromNow0(u32BreakPointStartTime) > 60*1000)
            {
                U32 DiffFreq = 0;

                if(m_u32BreakPointFreq >= m_u32AutoScanFreq)
                {
                    DiffFreq = m_u32BreakPointFreq - m_u32AutoScanFreq;
                }
                else
                {
                    DiffFreq = m_u32AutoScanFreq - m_u32BreakPointFreq;
                }

                u32BreakPointStartTime = mapi_time_utility::GetTime0();

                if(DiffFreq < BIG_STEP)
                {
                    printf("\n more than 30s in +/- 1M,so jump out!\n");
                    if(m_IsManualScan == TRUE)
                    {
                        if(m_manualSearchType == SearchUp)
                        {
                            if((m_u32AutoScanFreq + 2*BIG_STEP) > PAL_FREQ_MAX)
                            {
                                m_u32AutoScanFreq = PAL_FREQ_MAX - BIG_STEP;
                            }
                            else
                            {
                                m_u32AutoScanFreq += 2*BIG_STEP;
                            }
                        }
                        else
                        {
                            if(m_u32AutoScanFreq < (PAL_FREQ_MIN + 2*BIG_STEP))
                            {
                                m_u32AutoScanFreq = PAL_FREQ_MIN + BIG_STEP;
                            }
                            else
                            {
                                m_u32AutoScanFreq -= 2*BIG_STEP;
                            }
                        }
                    }
                    else
                    {
                        if((m_u32AutoScanFreq + 2*BIG_STEP) > PAL_FREQ_MAX)
                        {
                            m_u32AutoScanFreq = PAL_FREQ_MAX - BIG_STEP;
                        }
                        else
                        {
                            m_u32AutoScanFreq += 2*BIG_STEP;
                        }
                    }
                }
                m_u32BreakPointFreq = m_u32AutoScanFreq;
            }
            #endif


            if(mapi_time_utility::TimeDiffFromNow0(m_u32StartTime) > 20)
            {
                m_u32StartTime = mapi_time_utility::GetTime0();
                ATV_Scan_DBG("\033[0;32;34m allal ATV Tuning Loop StateTime=%d  \033[m \n", (int)m_u32StartTime);
                ATV_Scan_DBG("now state=%d\n", u8TVScanStep);

                ATV_Scan_FLOW_DBG("\r\n[S]:now state=%d", u8TVScanStep);

                switch(u8TVScanStep)
                {
                    case MW_ATV_Scan_AsiaChina::PAL_SCAN_START:
                    case MW_ATV_Scan_AsiaChina::PAL_SCAN_START_SECAM:
                    case MW_ATV_Scan_AsiaChina::PAL_SCAN_START_PAL:
                    {
                        ATV_Scan_DBG("\r\n<<Pal Start>>");
                        m_U32NumOfScan += 1;
                        u8ATVScanSoundSystemType = TV_SOUND_DK;
                        if(ePrevAudioType != E_AUDIOSTANDARD_DK_)
                        {
                            mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard((AUDIOSTANDARD_TYPE_)E_AUDIOSTANDARD_DK_);
                            ePrevAudioType = E_AUDIOSTANDARD_DK_;
                            VifSetSoundStandard(u8ATVScanSoundSystemType);
                        }
                        u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_0_PAL; // speed up a little bit than PAL_SCAN_START_SECAM
                        //g_ucCNIScanFormatCurrent = 0;
                        //g_ucCNIScanFormatDone = 0;
                        u8ProtectCount = 0;

                        //wTmpIf = STEP_250K;
                        //m_u32AutoScanFreq -= (m_u32AutoScanFreq%wTmpIf); // Normalize
                    }
                    if(TRUE == bDebugEnable)
                    {
                        m_U32ATVTimer[SCAN_TIMER_START] = mapi_time_utility::TimeDiffFromNow0(u32TmpTime);
                        u32TmpTime = mapi_time_utility::GetTime0();
                    }
                    break;

                    case MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_0:
                    case MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_0_PAL:
                        u8TVScanStep++;
                        bPollingVifLock = 0;
                        break;

                    case MW_ATV_Scan_AsiaChina::PAL_SCAN_SIGNAL_CHECK_BY_VIF:
                        ATV_Scan_FLOW_DBG("\r\n[S]:SCAN_SIGNAL_CHECK_BY_VIF");
                        ATV_Scan_FLOW_DBG("\r\n<< %u KHz  ", m_u32AutoScanFreq);
                        ATV_Scan_DBG("\r\n[%uKHz ", m_u32AutoScanFreq);
                        wTmpIf = m_pDemodulator->ATV_GetAFC_Distance();
                        ATV_Scan_DBG("VIF:%x..", wTmpIf);
                        if(TRUE == bDebugEnable)
                        {
                            ATV_Scan_PRINT("\r\n[%uKHz ", m_u32AutoScanFreq);
                            ATV_Scan_PRINT("VIF:%x..", wTmpIf);
                        }
                        #if (MSTAR_TVOS == 1)
                        if((wTmpIf != mapi_demodulator_datatype::E_AFC_OUT_OF_AFCWIN) || (bPollingVifLock>=PAL_SCAN_VIF_LOCK_CHECK_THR)) // VIF Lock
                        #else
                        if((wTmpIf != mapi_demodulator_datatype::E_AFC_OUT_OF_AFCWIN) && (bPollingVifLock>=PAL_SCAN_VIF_LOCK_CHECK_THR)) // VIF Lock
                        #endif
                        {
                            u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_1;
                            ATV_Scan_DBG("VIF LOCK..");
                            if(TRUE == bDebugEnable)
                            {
                                ATV_Scan_PRINT("VIF LOCK..");
                                m_U32ATVTimer[SCAN_TIMER_VIF] = mapi_time_utility::TimeDiffFromNow0(u32TmpTime);
                                u32TmpTime = mapi_time_utility::GetTime0();
                            }
                        }
                        else // No Lock
                        {
                            ATV_Scan_DBG("no lock]");
                            if(TRUE == bDebugEnable)
                            {
                                ATV_Scan_PRINT("no lock]");
                            }

                            if(m_manualSearchType == SearchUp) // Auto search or Manual up search
                            {
                                m_u32AutoScanFreq += BIG_STEP;
                            }
                            else
                            {
                                m_u32AutoScanFreq -= BIG_STEP;
                            }

                            u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_START;
                            if(TRUE == bDebugEnable)
                            {
                                m_U32ATVTimer[SCAN_TIMER_VIF] = mapi_time_utility::TimeDiffFromNow0(u32TmpTime);
                                u32TmpTime = mapi_time_utility::GetTime0();
                            }
                        }
                        break;

                    case MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_1:
                        ATV_Scan_FLOW_DBG("\r\n[S]:SCAN_NOP_1");
                        u8TVScanStep++;
                        bPollingVdLock = 0;
                        break;

                    case MW_ATV_Scan_AsiaChina::PAL_SCAN_SIGNAL_CHECK_BY_VD:
                         m_ScanDetectedAudioType = E_AUDIOSTANDARD_NOTSTANDARD_;
                         m_ScanDetectedVideoType = 0x07;

                        ATV_Scan_FLOW_DBG("\r\n[S]:SCAN_SIGNAL_CHECK_BY_VD");
                        wTmpVd = mapi_interface::Get_mapi_vd()->CheckStatusLoop();
                        //wTmpVd = mapi_interface::Get_mapi_vd()->CheckStatusLoop();
                        ATV_Scan_DBG("VD:%x..", wTmpVd);
                        if(TRUE == bDebugEnable)
                        {
                            ATV_Scan_PRINT("VD:%x..", wTmpVd);
                        }
                        if((wTmpVd & VD_CHECK_HSYNC_LOCKED) && (bPollingVdLock >= PAL_SCAN_VD_LOCK_CHECK_THR)) // VD Lock
                        {
                            u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_2;
                            ATV_Scan_DBG("VD LOCK!]");
                            if(TRUE == bDebugEnable)
                            {
                                ATV_Scan_PRINT("VD LOCK!]");
                            }
                        }
                        else
                        {
                            u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_SIGNAL_WEAK;
                        }
                        if(TRUE == bDebugEnable)
                        {
                            m_U32ATVTimer[SCAN_TIMER_VD] = mapi_time_utility::TimeDiffFromNow0(u32TmpTime);
                            u32TmpTime = mapi_time_utility::GetTime0();
                        }
                        break;

                    case MW_ATV_Scan_AsiaChina::PAL_SCAN_SIGNAL_WEAK:
                        ATV_Scan_FLOW_DBG("\r\n[S]:SCAN_SIGNAL_WEAK");
                        ATV_Scan_DBG("no lock]");
                        if(TRUE == bDebugEnable)
                        {
                            ATV_Scan_PRINT("no lock]");
                        }
                        #if (MSTAR_TVOS == 1)
                        if(m_manualSearchType == SearchUp) // Auto search or Manual up search
                        {
                            m_u32AutoScanFreq += STEP_500K;
                        }
                        else
                        {
                            m_u32AutoScanFreq -= STEP_500K;
                        }
                        #else
                        if(m_manualSearchType == SearchUp) // Auto search or Manual up search
                        {
                            m_u32AutoScanFreq += BIG_STEP;
                        }
                        else
                        {
                            m_u32AutoScanFreq -= BIG_STEP;
                        }
                        #endif

                        u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_START;
                        if(TRUE == bDebugEnable)
                        {
                            m_U32ATVTimer[SCAN_TIMER_AFT] = mapi_time_utility::TimeDiffFromNow0(u32TmpTime);
                            u32TmpTime = mapi_time_utility::GetTime0();
                        }
                        break;

                    case MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_2:
                        ATV_Scan_FLOW_DBG("\r\n[S]:SCAN_NOP_2");
                        u32FineTuneFreq = STEP_500K;
                        u8TVScanStep++;
                        break;

                    case MW_ATV_Scan_AsiaChina::PAL_SCAN_FIND_GOOD_AFC:
                        ATV_Scan_FLOW_DBG("\r\n[S]:SCAN_FIND_GOOD_AFC");
                        u8ProtectCount++;
                        if(u8ProtectCount>15)
                        {
                            u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_SIGNAL_WEAK;
                            printf("\nProtect !!!!---->goto PAL_SCAN_SIGNAL_WEAK\n");
                            break;
                        }

                        wTmpVd = mapi_interface::Get_mapi_vd()->CheckStatusLoop();
                        ATV_Scan_DBG("VD:%x..", wTmpVd);
                        if(TRUE == bDebugEnable)
                        {
                            ATV_Scan_PRINT("VD:%x..", wTmpVd);
                        }
                        if(wTmpVd & MAPI_VD_HSYNC_LOCKED)
                        {
                            wTmpIf = m_pDemodulator->ATV_GetAFC_Distance();
                            ATV_Scan_DBG("\r\nATV_GetAFC_Distance = %x", wTmpIf);
                            if(TRUE == bDebugEnable)
                            {
                                ATV_Scan_PRINT("\r\nATV_GetAFC_Distance = %x", wTmpIf);
                            }
                            if(wTmpIf != mapi_demodulator_datatype::E_AFC_OUT_OF_AFCWIN)
                            {
                                ucAFCOffset = CheckAfcWinPass(wTmpIf);
                            }
                            else
                            {
                                if(m_manualSearchType == SearchUp) // Auto search or Manual up search
                                {
                                    ucAFCOffset = AFC_BIG_STEP_INC;
                                }
                                else
                                {
                                    ucAFCOffset = AFC_BIG_STEP_DEC;
                                }
                            }

                            //ATV_Scan_DBG("VD Lock");
                            if(ucAFCOffset == AFC_GOOD)
                            {
                                ATV_Scan_DBG("\r\nGOOD AFC");
                                if(TRUE == bDebugEnable)
                                {
                                    ATV_Scan_PRINT("\r\nGOOD AFC");
                                }
                                u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_5; //SCAN_CNI_START; //bSaveFlag = TRUE;
                            }
                            else if(ucAFCOffset == AFC_INCREASE)
                            {
                                ATV_Scan_DBG("\r\nINC AFC ");
                                if(TRUE == bDebugEnable)
                                {
                                    ATV_Scan_PRINT("\r\nINC AFC ");
                                }
                                m_u32AutoScanFreq += m_u8TunerMinDistance;
                                u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_3;
                            }
                            else if(ucAFCOffset == AFC_DECREASE)
                            {
                                ATV_Scan_DBG("\r\nDEC AFC ");
                                if(TRUE == bDebugEnable)
                                {
                                    ATV_Scan_PRINT("\r\nDEC AFC ");
                                }
                                m_u32AutoScanFreq -= m_u8TunerMinDistance;
                                u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_4;
                            }
                            else if(ucAFCOffset == AFC_BIG_STEP_INC)
                            {
                                ATV_Scan_DBG("\r\nBIG INC ");
                                if(TRUE == bDebugEnable)
                                {
                                    ATV_Scan_PRINT("\r\nBIG INC ");
                                }
                                m_u32AutoScanFreq += SMALL_STEP;
                                u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_START;
                            }
                            else
                            {
                                ATV_Scan_DBG("\r\nBIG DEC ");
                                if(TRUE == bDebugEnable)
                                {
                                    ATV_Scan_PRINT("\r\nBIG DEC ");
                                }
                                m_u32AutoScanFreq -= SMALL_STEP;
                                u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_START;
                            }
                        }
                        else
                        {
                            u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_0_PAL;//PAL_SCAN_SIGNAL_CHECK_BY_VIF;
                        }
                        break;

                    case MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_3:
                        ATV_Scan_FLOW_DBG("\r\n[S]:SCAN_NOP_3");
                        u8TVScanStep++;
                        break;

                    case PAL_SCAN_FINE_INCREASE:
                        ATV_Scan_FLOW_DBG("\r\n[S]:SCAN_FINE_INCREASE");
                        wTmpVd = mapi_interface::Get_mapi_vd()->CheckStatusLoop();
                        wTmpIf = m_pDemodulator->ATV_GetAFC_Distance();
                        ATV_Scan_DBG("\r\nINC[%u]KHz", m_u32AutoScanFreq);
                        ATV_Scan_DBG(" VD[%x]", wTmpVd & MAPI_VD_HSYNC_LOCKED);
                        ATV_Scan_DBG(" AFC[%x]", wTmpIf);
                        if((wTmpVd & VD_CHECK_HSYNC_LOCKED) && (wTmpIf != mapi_demodulator_datatype::E_AFC_OUT_OF_AFCWIN))
                        {
                            ucAFCOffset = CheckAfcWinPass(wTmpIf);

                            if(ucAFCOffset == AFC_GOOD)
                            {
                                u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_5; //SCAN_CNI_START; //bSaveFlag = TRUE;
                            }
                            else
                            {
                                if((ucAFCOffset == AFC_INCREASE)||(ucAFCOffset == AFC_BIG_STEP_INC))
                                {
                                    u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_3;
                                    m_u32AutoScanFreq += m_u8TunerMinDistance;
                                }
                                else
                                {
                                    u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_4;
                                    m_u32AutoScanFreq -= m_u8TunerMinDistance;
                                }

                                if(u32FineTuneFreq)  //avoid increase & decrease
                                {
                                    if(u32FineTuneFreq > m_u8TunerMinDistance)
                                    {
                                        u32FineTuneFreq -= m_u8TunerMinDistance;
                                    }
                                    else
                                    {
                                        u32FineTuneFreq = 0;
                                    }
                                }
                                else
                                {
                                    ATV_Scan_DBG("\r\nFinetune fail");
                                    u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_SIGNAL_WEAK;
                                }
                            }
                        }
                        else
                        {
                            u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_SIGNAL_WEAK;
                        }
                        break;

                    case MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_4:
                        u8TVScanStep++;
                        break;

                    case MW_ATV_Scan_AsiaChina::PAL_SCAN_FINE_DECREASE:
                        ATV_Scan_FLOW_DBG("\r\n[S]:SCAN_FINE_DECREASE");
                        wTmpVd = mapi_interface::Get_mapi_vd()->CheckStatusLoop();
                        wTmpIf = m_pDemodulator->ATV_GetAFC_Distance();
                        ATV_Scan_DBG("\r\nDEC[%u]KHz", m_u32AutoScanFreq);
                        ATV_Scan_DBG(" VD[%x]", wTmpVd & VD_CHECK_HSYNC_LOCKED);
                        ATV_Scan_DBG(" AFC[%x]", wTmpIf);
                        if((wTmpVd & VD_CHECK_HSYNC_LOCKED) && (wTmpIf != mapi_demodulator_datatype::E_AFC_OUT_OF_AFCWIN))
                        {
                            ucAFCOffset = CheckAfcWinPass(wTmpIf);

                            if(ucAFCOffset == AFC_GOOD)
                            {
                                u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_5; //SCAN_CNI_START; //bSaveFlag = TRUE;
                            }
                            else
                            {
                                if((ucAFCOffset == AFC_INCREASE)||(ucAFCOffset == AFC_BIG_STEP_INC))
                                {
                                    u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_3;
                                    m_u32AutoScanFreq += m_u8TunerMinDistance;
                                }
                                else
                                {
                                    u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_4;
                                    m_u32AutoScanFreq -= m_u8TunerMinDistance;
                                }

                                if(u32FineTuneFreq)  //avoid increase & decrease
                                {
                                    if(u32FineTuneFreq > m_u8TunerMinDistance)
                                    {
                                        u32FineTuneFreq -= m_u8TunerMinDistance;
                                    }
                                    else
                                    {
                                        u32FineTuneFreq = 0;
                                    }
                                }
                                else
                                {
                                    ATV_Scan_DBG("\r\nFinetune fail");
                                    u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_SIGNAL_WEAK;
                                }
                            }
                        }
                        else
                        {
                            u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_SIGNAL_WEAK;
                        }
                        break;

                    case MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_5:
                        u8AudioDetectCount = 0;
                        u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_DETECT_VIDEO_SYSTEM;
                        if(TRUE == bDebugEnable)
                        {
                            m_U32ATVTimer[SCAN_TIMER_AFT] = mapi_time_utility::TimeDiffFromNow0(u32TmpTime);
                            u32TmpTime = mapi_time_utility::GetTime0();
                        }
                #if (ENABLE_ATV_NOSINGAL_BLACKSCREEN==1)
                        MSrv_Control::GetInstance()->SetVideoMute(FALSE);
                #endif
                        break;

                    case MW_ATV_Scan_AsiaChina::PAL_SCAN_DETECT_VIDEO_SYSTEM:
                        ATV_Scan_FLOW_DBG("\r\n[S]:SCAN_DETECT_VIDEO_SYSTEM");

                        // wait VD vsync ready
                        {
                            U32 iTimeoutStart = 0;
                            U32 iTimeoutEnd = 0;

                            iTimeoutStart = mapi_time_utility::GetTime0();
                            while(!(mapi_interface::Get_mapi_vd()->GetStatus() & 0x01))  // Bit 1
                            {
                                iTimeoutEnd = mapi_time_utility::GetTime0();

                                if(iTimeoutEnd > iTimeoutStart)
                                {
                                    if((iTimeoutEnd - iTimeoutStart) > 1000)
                                    {
                                        break;
                                    }
                                }
                                else
                                {
                                    iTimeoutStart = iTimeoutEnd;
                                }
                            }

                            #if (MSTAR_TVOS == 1)
                            iTimeoutStart = mapi_time_utility::GetTime0();
                            while(mapi_interface::Get_mapi_vd()->GetVerticalFreq() == E_MAPI_VIDEO_FQ_NOSIGNAL)
                            {
                                if(mapi_time_utility::TimeDiffFromNow0(iTimeoutStart) > 1000)
                                {
                                    break;
                                }
                            }
                            #endif
                        }

                        wTmpVd = mapi_interface::Get_mapi_vd()->GetVideoStandardDetection(&wTmpVd);
                        ATV_Scan_DBG("\r\nVD Std. Test = %d", wTmpVd);
                        if(TRUE == bDebugEnable)
                        {
                            ATV_Scan_PRINT("\r\nVD Std. Test = %d", wTmpVd);
                        }
                         m_ScanDetectedVideoType = wTmpVd;

                        // 60Hz(NTSC) detect
                        if(mapi_interface::Get_mapi_vd()->GetVerticalFreq() == 60)
                        {
                            u8ATVScanSoundSystemType = TV_SOUND_M;
                            m_ScanDetectedAudioType = (AUDIOSTANDARD_TYPE_)E_AUDIOSTANDARD_M_;
                            if(ePrevAudioType != E_AUDIOSTANDARD_M_)
                            {
                                mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard((AUDIOSTANDARD_TYPE_)E_AUDIOSTANDARD_M_);
                                ePrevAudioType = E_AUDIOSTANDARD_M_;
                            }
                            m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_);
                            u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_CNI_END;
                            if(TRUE == bDebugEnable)
                            {
                                m_U32ATVTimer[SCAN_TIMER_VIDEO] = mapi_time_utility::TimeDiffFromNow0(u32TmpTime);
                                u32TmpTime = mapi_time_utility::GetTime0();
                            }
                        }
                        //// Color NTSC detect
                        //else if ((wTmpVd == E_MAPI_VIDEOSTANDARD_NTSC_M) || (wTmpVd == E_MAPI_VIDEOSTANDARD_NTSC_44))
                        //{
                        //    u8ATVScanSoundSystemType = TV_SOUND_M;
                        //    mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard( E_AUDIOSTANDARD_M_ );
                        //    m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_);
                        //    u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_CNI_END;
                        //}
                        else
                        {
                            u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_6;
                            if(TRUE == bDebugEnable)
                            {
                                m_U32ATVTimer[SCAN_TIMER_VIDEO] = mapi_time_utility::TimeDiffFromNow0(u32TmpTime);
                                u32TmpTime = mapi_time_utility::GetTime0();
                            }
                        }
                        break;

                    case MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_6:
                        mapi_interface::Get_mapi_vif()->BypassDBBAudioFilter(TRUE);
                        mapi_interface::Get_mapi_audio()->SIF_StartAutoStandardDetection();
                        u8AudioDetectCount = 0;
                        u8TVScanStep++;
                        break;

                    case MW_ATV_Scan_AsiaChina::PAL_SCAN_DETECT_AUDIO_SYSTEM:
                        ATV_Scan_FLOW_DBG("\r\n[S]:SCAN_DETECT_AUDIO_SYSTEM");
                        {
#if 1
                            AUDIOSTANDARD_TYPE_ detectedAudioType = mapi_interface::Get_mapi_audio()->SIF_GetResultOfAutoStandardDetection();

                            ATV_Scan_DBG("\r\nAUDIO_SYSTEM = [%x],\n", detectedAudioType);
                            //AUDIOSTANDARD_TYPE_ audioType = mapi_interface::Get_mapi_audio()->SIF_GetAudioStandard();

                            if(mapi_interface::Get_mapi_audio()->SIF_GetAudioStandard() != E_AUDIOSTANDARD_L_)
                            {
                                AUDIOSTANDARD_TYPE_ AudioType = detectedAudioType;

                                if(detectedAudioType == E_AUDIOSTANDARD_NOTSTANDARD_)
                                {
                                   #if (CHINA_ENABLE == 1)
                                   AudioType = E_AUDIOSTANDARD_DK_;
                                   #else
                                   AudioType = E_AUDIOSTANDARD_BG_;
                                   #endif
                                }

                                if(ePrevAudioType != AudioType)
                                {
                                    mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard((AUDIOSTANDARD_TYPE_)AudioType);
                                    ePrevAudioType = AudioType;
                                }
                                if(m_IsManualScan == TRUE)
                                {
                                    if(detectedAudioType == E_AUDIOSTANDARD_BG_)
                                    {
                                        VifSetSoundStandard(TV_SOUND_BG);
                                        // It depends on the picture carrier of each tuner driver. ex. tuner NXP_TDA18275 (since CL769556), tuner SI_2158
                                        // Because the distance of picture carrier between DK and BG is too far, tuner AFT needs more time to shift frequency.
                                        // VIF would be unlocked until picture carrier shift to BG.
                                        // For the suggestion from VIF team, do SetTune again will let picture carrier shift faster
                                        VifSetFrequency(m_u32AutoScanFreq, E_AUDIOSTANDARD_BG_);
                                    }
                                    else if(detectedAudioType == E_AUDIOSTANDARD_I_)
                                    {
                                        VifSetSoundStandard(TV_SOUND_I);
                                    }
                                }
                            }

                            ATV_Scan_DBG("\r\nAUDIO_SYSTEM = [%x],\n", detectedAudioType);
                            if(TRUE == bDebugEnable)
                            {
                                ATV_Scan_PRINT("\r\nAUDIO_SYSTEM = [%x],\n", detectedAudioType);
                            }
                            //g_PalCurChannelData.ucColorSystem = TV_COLOR_AUTO;
                            //g_PalCurChannelData.ucSoundSystem = TV_SOUND_AUTO;
                            //g_PalCurChannelData.ucAutoSound = devAudioStdDetect(TV_COLOR_AUTO) & 0x0F;
                            if(detectedAudioType == E_AUDIOSTANDARD_NOTSTANDARD_)
                            {
                                if(u8AudioDetectCount < 1)
                                {
                                    u8AudioDetectCount++;
                                    u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_6 + 10;
                                }
                                else
                                {
                                    // force clk shift to VD lock
                                    if(m_pDemodulator->ATV_IsInternalVIF())  // MStar VIF
                                    {
                                        VifSetFrequency(m_u32AutoScanFreq + STEP_250K);
                                    }
                                    else
                                    {
                                        VifSetFrequency(m_u32AutoScanFreq + STEP_500K);
                                    }

                                    u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_7;
                                    if(TRUE == bDebugEnable)
                                    {
                                        m_U32ATVTimer[SCAN_TIMER_AUDIO] = mapi_time_utility::TimeDiffFromNow0(u32TmpTime);
                                        u32TmpTime = mapi_time_utility::GetTime0();
                                    }
                                }
                            }
                            else
#endif
                            {
                                //wTmpIf = devAudioCntStd(g_PalCurChannelData.ucAutoSound);
                                //devTunerSetSubSys( GetTunerSoundSystem(wTmpIf) );
                                //devTunerSetFreq( m_u32AutoScanFreq );
                                u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_CNI_START; //bSaveFlag = TRUE;
                                if(TRUE == bDebugEnable)
                                {
                                    m_U32ATVTimer[SCAN_TIMER_AUDIO] = mapi_time_utility::TimeDiffFromNow0(u32TmpTime);
                                    u32TmpTime = mapi_time_utility::GetTime0();
                                }
                            }
                            m_ScanDetectedAudioType = detectedAudioType;
                            break;
                        }
                    case MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_7:
                        u8AudioDetectCount = 0;
                        bPollingVdLock = 0;
                        bPollingVifLock = 0;
                        u8TVScanStep++;
                        break;

                    case MW_ATV_Scan_AsiaChina::PAL_SCAN_CHECK_FALSE_CHANNEL:
                        ATV_Scan_FLOW_DBG("\r\n[S]:SCAN_CHECK_FALSE_CHANNEL");
                        if(u8AudioDetectCount < 6)
                        {
                            u8AudioDetectCount++;
                            u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_7 + 1;
                        }
                        else
                        {
                            wTmpVd = mapi_interface::Get_mapi_vd()->CheckStatusLoop();
                            wTmpIf = m_pDemodulator->ATV_GetAFC_Distance();
                            ATV_Scan_DBG("\r\nVD[%x]", wTmpVd);
                            ATV_Scan_DBG(" TN[%x]", wTmpIf);
                            if(((wTmpVd & VD_CHECK_HSYNC_LOCKED) && (bPollingVdLock>= PAL_SCAN_VD_LOCK_CHECK_THR))
                                && ((wTmpIf != mapi_demodulator_datatype::E_AFC_OUT_OF_AFCWIN) && (bPollingVifLock >= PAL_SCAN_VIF_LOCK_CHECK_THR)))
                            {
                                //VifSetSoundStandard(u8ATVScanSoundSystemType);
                                VifSetFrequency(m_u32AutoScanFreq);
                                u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_CNI_START; //bSaveFlag = TRUE;
                                if(TRUE == bDebugEnable)
                                {
                                    m_U32ATVTimer[SCAN_TIMER_FALSE_CHANNEL] = mapi_time_utility::TimeDiffFromNow0(u32TmpTime);
                                    u32TmpTime = mapi_time_utility::GetTime0();
                                }
                                   #if (CHINA_ENABLE == 1)
                                   m_ScanDetectedAudioType = E_AUDIOSTANDARD_DK_;
                                   #else
                                   m_ScanDetectedAudioType = E_AUDIOSTANDARD_BG_;
                                   #endif
                                  ATV_Scan_DBG("\r\n< Audio fail >");
                            }
                            else
                            {
                                if(m_manualSearchType == SearchUp) // Auto search or Manual up search
                                {
                                    m_u32AutoScanFreq += BIG_STEP;
                                }
                                else
                                {
                                    m_u32AutoScanFreq -= BIG_STEP;
                                }

                                u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_START;
                                if(TRUE == bDebugEnable)
                                {
                                    m_U32ATVTimer[SCAN_TIMER_FALSE_CHANNEL] = mapi_time_utility::TimeDiffFromNow0(u32TmpTime);
                                    u32TmpTime = mapi_time_utility::GetTime0();
                                }
                                ATV_Scan_DBG("\r\n< False Channel >");
                            }
                        }
                        break;

                    default:
                        if((u8TVScanStep > (MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_0 + PAL_SCAN_VIF_HALF_POLLING_LOOP_CHINA))
                                && (u8TVScanStep < MW_ATV_Scan_AsiaChina::PAL_SCAN_SIGNAL_CHECK_BY_VIF))//polling check VIF lock
                        {
                            wTmpIf = m_pDemodulator->ATV_GetAFC_Distance();
                            if(wTmpIf != mapi_demodulator_datatype::E_AFC_OUT_OF_AFCWIN)
                            {
                                bPollingVifLock++;
                                if(bPollingVifLock>= PAL_SCAN_VIF_LOCK_CHECK_THR)
                                    u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_SIGNAL_CHECK_BY_VIF;
                                break;
                            }
                        }
                        else if((u8TVScanStep > (MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_1 + PAL_SCAN_VD_HALF_POLLING_LOOP_CHINA))
                                && (u8TVScanStep < MW_ATV_Scan_AsiaChina::PAL_SCAN_SIGNAL_CHECK_BY_VD))//polling check VD lock
                        {
                            wTmpVd = mapi_interface::Get_mapi_vd()->CheckStatusLoop();
                            if(wTmpVd & VD_CHECK_HSYNC_LOCKED)   // VD Lock
                            {
                                bPollingVdLock ++;
                                if(bPollingVdLock>= PAL_SCAN_VD_LOCK_CHECK_THR)
                                    u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_SIGNAL_CHECK_BY_VD;
                                break;
                            }
                        }
                        else if((u8TVScanStep > (MW_ATV_Scan_AsiaChina::PAL_SCAN_NOP_7 + PAL_SCAN_VD_HALF_POLLING_LOOP_CHINA))
                                && (u8TVScanStep < MW_ATV_Scan_AsiaChina::PAL_SCAN_CHECK_FALSE_CHANNEL))
                        {
                            wTmpVd = mapi_interface::Get_mapi_vd()->CheckStatusLoop();
                            wTmpIf = m_pDemodulator->ATV_GetAFC_Distance();
                            if(wTmpVd & VD_CHECK_HSYNC_LOCKED)   // VD Lock
                            {
                                bPollingVdLock ++;
                            }
                            if(wTmpIf != mapi_demodulator_datatype::E_AFC_OUT_OF_AFCWIN)
                            {
                                bPollingVifLock++;
                            }
                            if((bPollingVdLock >= PAL_SCAN_VD_LOCK_CHECK_THR) && (bPollingVifLock >= PAL_SCAN_VIF_LOCK_CHECK_THR))
                            {
                                u8TVScanStep = PAL_SCAN_CHECK_FALSE_CHANNEL;
                                break;
                            }
                        }
                        u8TVScanStep++;
                        break;

                    case MW_ATV_Scan_AsiaChina::PAL_SCAN_CNI_START:
                    case MW_ATV_Scan_AsiaChina::PAL_SCAN_CNI_END:
                        ATV_Scan_FLOW_DBG("\r\n[S]:SCAN_CNI_END");
                        printf("\r\n<< %u KHz  ", m_u32AutoScanFreq); // force to print message
                        printf("Ch[%d]", (int)m_u8ChannelCount);
                        //ATV_Scan_DBG(">>\r\n", m_u8ChannelCount);

                        if(m_IsManualScan == TRUE)
                        {
                            //MemorizeProg(m_u32AutoScanFreq);
                            ATV_Scan_LOG_FILE(fclose(pFile);)

                            m_bATVScanThreadEnable = FALSE;
                            m_IsLocked = TRUE;
                            //m_u32StartFreq = m_u32AutoScanFreq;
                            mapi_interface::Get_mapi_vd()->SetVideoStandard((MAPI_AVD_VideoStandardType)m_ScanDetectedVideoType, MAPI_FALSE);
                            SetSoundSystem(m_ScanDetectedAudioType);
                            // It depends on the picture carrier of each tuner driver.
                            // For the suggestion from VIF team, do SetTune again will let picture carrier shift faster for all case in manual tuning.
                            AUDIOSTANDARD_TYPE_ AudioType = mapi_interface::Get_mapi_audio()->SIF_GetAudioStandard();
                            VifSetFrequency(m_u32AutoScanFreq, mapi_interface::Get_mapi_audio()->SIF_ConvertToBasicAudioStandard(AudioType));

                            SendScanInfo();
                            m_IsManualScan = FALSE;
                            //MSrv_Control::GetMSrvAtv()->_SetTunerPLL(((m_u32AutoScanFreq * 100 / 625)+5)/10);
                            //MSrv_Control::GetMSrvAtv()->_DetectStationName();
                            break;
                        }
                        else
                        {
                            // xs AFT Info
                            //g_PalCurChannelData.ucAftMode = AFT_MODE_ON | AFT_OFFSET_0;

                            // STORE TO DATABASE & EEPROM
                            BOOL bSmartScan;
                            MSrv_Control::GetMSrvAtv()->GetSmartScanMode(&bSmartScan);
                            if(FALSE == bSmartScan)
                            {
                                MemorizeProg(m_u32AutoScanFreq, FALSE);
                            }
                            else
                            {
                                //whether this frequency is obtain or not
                                U16 u16CurrTunerPLL = 0;
                                U16 u16SavedTunerPLL = 0;
                                U16 u16PLLof1MFrequency = -1;
                                EN_FREQ_STEP eFreqStep = E_FREQ_STEP_INVALD;
                                m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_FREQ_STEP, 0, 0, &eFreqStep);
                                switch(eFreqStep)
                                {
                                    case E_FREQ_STEP_31_25KHz:
                                        u16CurrTunerPLL = (((m_u32AutoScanFreq * 1000) / 312) + 5) / 10;
                                        u16PLLof1MFrequency = (((1000 * 1000) / 312) + 5) / 10;
                                        break;
                                    case E_FREQ_STEP_50KHz:
                                        u16CurrTunerPLL = m_u32AutoScanFreq / 50;
                                        u16PLLof1MFrequency = 1000/50;
                                        break;
                                    case E_FREQ_STEP_62_5KHz:
                                        u16CurrTunerPLL = (((m_u32AutoScanFreq * 100) / 625) + 5) / 10;
                                        u16PLLof1MFrequency = (((1000 * 100) / 625) + 5) / 10;
                                        break;
                                    default:
                                        ASSERT(0);
                                        break;
                                }
                                //printf("\n\n###########################u16CurrTunerPLL =%d###########################\n\n",u16CurrTunerPLL);
                                int ChannelCounter = 0;
                                int ActiveMaxChannel = 0;

                                ChannelCounter= MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CHANNEL_MIN, 0, 0, NULL);
                                ActiveMaxChannel = MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_ACTIVE_PROGRAM_COUNT, 0, 0, NULL);
                                //printf("\n\n###########################ChannelCounter = %d###########################\n###########################ActiveMaxChannel = %d###########################\n\n",ChannelCounter,ActiveMaxChannel);
                                while(ChannelCounter <= ActiveMaxChannel)
                                {
                                    //printf("\n\n###########################in loop, now ChannelCounter =%d###########################\n",ChannelCounter);
                                    u16SavedTunerPLL=MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(GET_PROGRAM_PLL_DATA,ChannelCounter,0,NULL);
                                    //printf("\n\n###########################in loop, now u16SavedTunerPLL =%d###########################\n",u16SavedTunerPLL);
                                    if(abs(u16CurrTunerPLL - u16SavedTunerPLL) <= u16PLLof1MFrequency)
                                    {
                                        //printf("\n\n###########################loop break###########################\n\n");
                                        break;
                                    }
                                    else
                                    {
                                        ChannelCounter++;
                                        //printf("\n\n###########################in loop, increase ChannelCounter =%d###########################\n\n",ChannelCounter);
                                    }
                                }
                                if(abs(u16CurrTunerPLL - u16SavedTunerPLL) > u16PLLof1MFrequency)
                                {
                                    MemorizeProg(m_u32AutoScanFreq, FALSE);
                                    //printf("\n\n###########################smart scan save program###########################\n\n");
                                }
                            }


                            mapi_interface::Get_mapi_vif()->BypassDBBAudioFilter(FALSE);

                            if(u8ATVScanSoundSystemType == TV_SOUND_M)
                            {
                                m_u32AutoScanFreq += SECAM_NEXT_STEP; // Add 4M Hz
                            }
                            else
                            {
                                m_u32AutoScanFreq += PAL_NEXT_STEP_CHINA; // Add 2M Hz
                            }
                        }
                        m_u8ChannelCount++;
                        u8TVScanStep = MW_ATV_Scan_AsiaChina::PAL_SCAN_START;
                        if(TRUE == bDebugEnable)
                        {
                            m_U32ATVTimer[SCAN_TIMER_CNI] = mapi_time_utility::TimeDiffFromNow0(u32TmpTime);
                            u32TmpTime = mapi_time_utility::GetTime0();
                        }
                    #if (ENABLE_ATV_NOSINGAL_BLACKSCREEN==1)
                        MSrv_Control::GetInstance()->SetVideoMute(TRUE);
                    #endif
                        break;
                }
            }
            else
            {
                U32 sleepTime =  20 - mapi_time_utility::TimeDiffFromNow0(m_u32StartTime);
                //if(sleepTime < 20 && sleepTime>=0)
                if(sleepTime < 20)
                {
                    ATV_Scan_DBG("time out for %u", sleepTime * 1000);
                    usleep(sleepTime * 1000);
                }
                if(TRUE == bDebugEnable)
                {
                    m_U32TimeOnSleep += mapi_time_utility::TimeDiffFromNow0(u32TmpTimeAlternate);
                }
            }

            m_pDemodulator->ATV_VIF_Handler(TRUE);

            {
                if(m_IsManualScan == TRUE)
                {
                    if(m_manualSearchType == SearchUp)
                    {
                        if(m_u32AutoScanFreq > PAL_FREQ_MAX)
                        {
                            m_u32AutoScanFreq = PAL_FREQ_MIN;
                        }
                    }
                    else
                    {
                        if(m_u32AutoScanFreq < PAL_FREQ_MIN)
                        {
                            m_u32AutoScanFreq = PAL_FREQ_MAX;
                        }
                    }
                }

#if ( ENABLE_SKIP_SAME_FREQ )
                if(m_u32PrevScanFreq != m_u32AutoScanFreq)
#endif
                {
                    // For OSD update
                    switch(m_u8TunerMinDistance)
                    {
                        case 31:
                        {
                            U16 u16TunerPLL = (((m_u32AutoScanFreq * 1000) / 3125) + 5) / 10;
                            m_u32AutoScanFreq = (u16TunerPLL * 3125) / 100;
                            break;
                        }
                        case 62:
                        {
                            U16 u16TunerPLL = (((m_u32AutoScanFreq * 100) / 625) + 5) / 10;
                            m_u32AutoScanFreq = (u16TunerPLL * 625) / 10;
                            break;
                        }
                        default:
                            break;
                    }
                    if(TRUE == bDebugEnable)
                    {
                        u32TmpTimeAlternate = mapi_time_utility::GetTime0();
                        ATV_Scan_PRINT("\n%s [F: %u][V: 0x%02X][A: 0x%02X], at %d\n", __func__, m_u32PrevScanFreq, m_ScanDetectedVideoType, m_ScanDetectedAudioType, __LINE__);
                        ATV_Scan_PRINT("[S: %02u][T]: %u, at %d\n", u8TVScanStep, mapi_time_utility::GetTime0(), __LINE__);
                    }
                    VifSetFrequency(m_u32AutoScanFreq);
                    m_u32PrevScanFreq = m_u32AutoScanFreq;
                    if(TRUE == bDebugEnable)
                    {
                        m_U32TimeOnI2C += mapi_time_utility::TimeDiffFromNow0(u32TmpTimeAlternate);
                        ATV_Scan_PRINT("[S: %02u][T]: %u, at %d\n", u8TVScanStep, mapi_time_utility::GetTime0(), __LINE__);
                    }
                    SendScanInfo();
                    if(TRUE == bDebugEnable)
                    {
                        _DisplayAllTimer();
                        m_U32ATVTimer[SCAN_TIMER_TUNER] = mapi_time_utility::TimeDiffFromNow0(u32TmpTime);
                        u32TmpTime = mapi_time_utility::GetTime0();
                    }
                }
            }

            //pthread_mutex_unlock(&MSrv_Control::GetMSrvAtv()->m_mutex_Scan);
            ATV_Scan_LOG_FILE(fclose(pFile);)
        }
        else
        {
            usleep(20 * 1000);
        }
    }

    //Init VIF
    //m_pDemodulator->ATV_VIF_Init();

    //#endif
    //pthread_mutex_unlock(&MSrv_Control::GetMSrvAtv()->m_mutex_Scan);
    if(TRUE == bDebugEnable)
    {
        m_U32TimeOnScan = mapi_time_utility::TimeDiffFromNow0(m_U32TimeOnScan);
        ATV_Scan_PRINT("\n_ATV_Scan_Thread Exit Success\n m_U32NumOfScan=%u, at %d\n", m_U32NumOfScan, __LINE__);
        ATV_Scan_PRINT("\n_ATV_Scan_Thread Exit Success\n m_U32TimeOnScan=%u, at %d\n", m_U32TimeOnScan, __LINE__);
        ATV_Scan_PRINT("\n_ATV_Scan_Thread Exit Success\n m_U32TimeOnI2C=%u\n", m_U32TimeOnI2C);
        ATV_Scan_PRINT("\n_ATV_Scan_Thread Exit Success\n m_U32TimeOnSleep=%u\n", m_U32TimeOnSleep);
    }
    mapi_interface::Get_mapi_vd()->SetHsyncDetectionForTuning(FALSE);
    MSrv_Control::GetMSrvAtv()->SetSmartScanMode(FALSE);
    if(pVDData != NULL)
    {
        delete pVDData;
        pVDData = NULL;
    }
    ATV_Scan_DBG("\n_ATV_Scan_Thread Exit Success\n Freq=%d, at %d\n", m_u32StartFreq, __LINE__);
}

BOOL MW_ATV_Scan_AsiaChina::DoInit(void)
{
    return TRUE;
}

void MW_ATV_Scan_AsiaChina::DoStart(void)
{
    SetupAutoScanPara(E_AUDIOSTANDARD_DK_);

    if(m_pDemodulator->ATV_IsInternalVIF())  // MStar VIF
    {
        m_u32FreqSmallStep = STEP_125K;//STEP_187d5K;
    }
    else
    {
        m_u32FreqSmallStep = STEP_250K;
    }

    m_u8ChannelCount = CHANNEL_MIN_NUM;
}

//-----------------------------------------------------------------------------
#if (ISDB_SYSTEM_ENABLE == 1)
U8 MW_ATV_Scan_Brazil::GetScanProgressPercent(void)
{
    U8 u8Percent;
    U32 u32UhfMaxFreq;
    u8Percent = 0;

    mapi_tuner* pTuner;
    pTuner = mapi_interface::Get_mapi_pcb()->GetAtvTuner(0);
    ASSERT(pTuner);

    pTuner->ExtendCommand(
        mapi_tuner_datatype::E_TUNER_SUBCMD_GET_UHF_MAX_FREQ,
        0,
        0,
        &u32UhfMaxFreq);


    U8 u8CurrentProgramNumber;
    u8CurrentProgramNumber = (U8)MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL);

    if((m_IsManualScan == TRUE) && (IsSearched()))
    {
        return 100;
    }

    if(m_IsManualScan == TRUE)
    {
        if(m_manualSearchType == SearchUp)
        {
            if((m_u32StartFreq ) <= 48250 )
            {
                if((GetCurrentFreq() ) == (u32UhfMaxFreq ))
                {
                    printf("%s() LINE=%d\n",__func__,__LINE__);
                    return 100;
                }
            }
            else
            {
                if((GetCurrentFreq()/1000 ) == (m_u32StartFreq /1000 - 1))
                {
                    printf("%s() LINE=%d m_u32StartFreq =%d , Curent=%d\n",__func__,__LINE__,(int)m_u32StartFreq,(int)GetCurrentFreq());
                    return 100;
                }
            }
        }
        else
        {
            if((m_u32StartFreq ) >= (u32UhfMaxFreq ))
            {
                if((GetCurrentFreq() ) <= 48250 )
                {
                    printf("%s() LINE=%d\n",__func__,__LINE__);
                    return 100;
                }
            }
            else
            {
                if((GetCurrentFreq()/1000 ) == (m_u32StartFreq/1000 + 1))
                {
                    printf("%s() LINE=%d\n",__func__,__LINE__);
                    return 100;
                }
            }
        }
        return 0;
    }

    u8Percent = (U8)(((u8CurrentProgramNumber - GetMinChannelNO()) * 100) / (GetMaxChannelNO() - GetMinChannelNO())) ;
    printf("u8Percent=%d \tMin=%d \t Max=%d\n", u8Percent, GetMinChannelNO(), GetMaxChannelNO());
    if(u8CurrentProgramNumber <= GetMinChannelNO())
    {
        u8Percent=0;
    }
    else if(((u8CurrentProgramNumber+1)<GetMaxChannelNO())&&(u8Percent==100))
    {
    }

    return u8Percent;
}
#endif

#if (ESASIA_NTSC_SYSTEM_ENABLE == 1)  //Add for ES Asia/TW ATV tuing 20140526EL
void MW_ATV_Scan_ESAsia_NTSC::Start(U32 u32FrequencyStart, U32 u32FrequencyEnd)//,ScanCallback callback)
{
    // Delete Timing Monitor thread if exist.
    //ATC_scan_DEBUGINFO(printf("mbrg_ATV_scan_Start  , m_bATVScanThreadEnable = %d \n",(int)m_bATVScanThreadEnable);)
    ATV_Scan_DBG("mbrg_ATV_scan_Start  , u32StartFreq = %d\n", u32FrequencyStart);
    m_u32StartFreq = u32FrequencyStart;
    m_u32EndFreq =u32FrequencyEnd;
    ATV_Scan_DBG("mbrg_ATV_scan_Start  , u32StartFreq = %d\n", m_u32StartFreq);
    m_bScanContinue = TRUE;
    m_manualSearchType = SearchUp;
    m_IsManualScan = FALSE;

    // Get min. step of Tuner freq.
    EN_FREQ_STEP eFreqStep = E_FREQ_STEP_INVALD;
    m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_FREQ_STEP, 0, 0, &eFreqStep);
    switch(eFreqStep)
    {
        case E_FREQ_STEP_31_25KHz:
            m_u8TunerMinDistance = 31;
            break;
        case E_FREQ_STEP_50KHz:
            m_u8TunerMinDistance = 50;
            break;
        case E_FREQ_STEP_62_5KHz:
            m_u8TunerMinDistance = 62;
            break;
        default:
            ASSERT(0);
            break;
    }

    DoStart();

    //l]wWv
    VifSetFrequency(m_u32AutoScanFreq);
    m_u32PrevScanFreq = m_u32AutoScanFreq;
    SendScanInfo();

    //////////////////////////////////////////////////////////////////////////////////////////////////////

    if(!m_bATVScanThreadEnable)
    {
        m_bATVScanThreadEnable = TRUE;
        int intPTHChk;

        // To Init ATV DataBase
        MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER, ATV_FIRST_PR_NUM, 0, NULL);
        MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(RESET_CHANNEL_DATA, 0, 0, NULL);

        pthread_attr_t attr;
        pthread_attr_init(&attr);
        pthread_attr_setstacksize(&attr, PTHREAD_STACK_SIZE);
        intPTHChk = PTH_RET_CHK(pthread_create(&m_AtvScanThread, &attr, ScanThreadFunc, this));
        if(intPTHChk != 0)
        {
            ASSERT(0);
            //return FALSE;
        }
        m_bStartJoinThread = FALSE;
    }
    else
    {
        m_bStartJoinThread = TRUE;
        m_bATVScanThreadEnable = FALSE;
        void *thread_result;
        int intPTHChk;
        intPTHChk = PTH_RET_CHK(pthread_join(m_AtvScanThread, &thread_result));
        if(intPTHChk != 0)
        {
            perror("thread Atv Scan Thread join failed");
            ASSERT(0);
        }
        else
        {
            ATV_Scan_IFO("Exit Atv Scan Thread Success.\n");
        }

        // To Init ATV DataBase
        MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER , ATV_FIRST_PR_NUM, 0, NULL);
        MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(RESET_CHANNEL_DATA, 0, 0, NULL);

        //pthread_kill(m_AtvScanThread,0);
        m_AtvScanThread = 0;
        m_bStartJoinThread = FALSE;
        m_bATVScanThreadEnable = TRUE;

        pthread_attr_t attr;
        pthread_attr_init(&attr);
        pthread_attr_setstacksize(&attr, PTHREAD_STACK_SIZE);
        PTH_RET_CHK(pthread_create(&m_AtvScanThread, &attr, ScanThreadFunc, this));
    }
}

void MW_ATV_Scan_ESAsia_NTSC::StartManualScan(U32 u32FrequencyStart, SearchDirection mode)
{
    m_u32StartFreq = u32FrequencyStart;
    m_IsLocked = FALSE;
    m_bScanContinue = TRUE;
    m_IsManualScan = TRUE;
    m_manualSearchType = mode;
    m_u32PrevScanFreq = 0;
    m_u32AutoScanFreq = m_u32StartFreq;

    if(m_pDemodulator->ATV_IsInternalVIF())  // MStar VIF
    {
        m_u32FreqSmallStep = STEP_125K;//STEP_187d5K;
    }
    else
    {
        m_u32FreqSmallStep = STEP_250K;
    }

    // Get min. step of Tuner freq.
    EN_FREQ_STEP eFreqStep = E_FREQ_STEP_INVALD;
    m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_FREQ_STEP, 0, 0, &eFreqStep);
    switch(eFreqStep)
    {
        case E_FREQ_STEP_31_25KHz:
            m_u8TunerMinDistance = 31;
            break;
        case E_FREQ_STEP_50KHz:
            m_u8TunerMinDistance = 50;
            break;
        case E_FREQ_STEP_62_5KHz:
            m_u8TunerMinDistance = 62;
            break;
        default:
            ASSERT(0);
            break;
    }

    m_eNTSCCableType = NTSC_CABLE_STD;

    mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard((AUDIOSTANDARD_TYPE_)E_AUDIOSTANDARD_M_BTSC);
    m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_BTSC_);
    VifSetSoundStandard(TV_SOUND_M);

    if(!m_bATVScanThreadEnable)
    {
        m_bATVScanThreadEnable = TRUE;
        int intPTHChk;
        pthread_attr_t attr;
        pthread_attr_init(&attr);
        pthread_attr_setstacksize(&attr, PTHREAD_STACK_SIZE);
        intPTHChk = PTH_RET_CHK(pthread_create(&m_AtvScanThread, &attr, ManualScanThreadFunc, this));
        if(intPTHChk != 0)
        {
            ASSERT(0);
            //return FALSE;
        }
        m_bStartJoinThread = FALSE;
    }
    else
    {
        m_bStartJoinThread = TRUE;
        m_bATVScanThreadEnable = FALSE;
        void *thread_result;
        int intPTHChk;
        intPTHChk = PTH_RET_CHK(pthread_join(m_AtvScanThread, &thread_result));
        if(intPTHChk != 0)
        {
            perror("thread Atv Scan Thread join failed");
            ASSERT(0);
        }
        else
        {
            ATV_Scan_IFO("Exit Atv Scan Thread Success.\n");
        }
        m_AtvScanThread = 0;
        m_bATVScanThreadEnable = TRUE;
        m_bStartJoinThread = FALSE;
        pthread_attr_t attr;
        pthread_attr_init(&attr);
        pthread_attr_setstacksize(&attr, PTHREAD_STACK_SIZE);
        PTH_RET_CHK(pthread_create(&m_AtvScanThread, &attr, ManualScanThreadFunc, this));
    }
}

void MW_ATV_Scan_ESAsia_NTSC::MemorizeProg(U32 FreqKHz, BOOL bSkip)
{
    U16 u16TunerPLL;
    EN_FREQ_STEP eFreqStep = E_FREQ_STEP_INVALD;
    U8 u8CurrentProgramNumber;

    u8CurrentProgramNumber = (U8)MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL);
    m_pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_FREQ_STEP, 0, 0, &eFreqStep);

    switch(eFreqStep)
    {
        case E_FREQ_STEP_31_25KHz:
            u16TunerPLL = (((FreqKHz * 1000) / 312) + 5) / 10;
            break;
        case E_FREQ_STEP_50KHz:
            u16TunerPLL = FreqKHz / 50;
            break;
        case E_FREQ_STEP_62_5KHz:
            u16TunerPLL = (((FreqKHz * 100) / 625) + 5) / 10;
            break;
        default:
            ASSERT(0);
            break;
    }

    if(!bSkip)
    {
        MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u16ScannedChannelNum++  ; //@FIXME I think total non skip count should Update on Change
    }

    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_PROGRAM_PLL_DATA , u8CurrentProgramNumber, (U16)u16TunerPLL, NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_AUDIO_STANDARD , u8CurrentProgramNumber, (U16)mapi_interface::Get_mapi_audio()->SIF_GetAudioStandard(), NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SKIP_PROGRAM , u8CurrentProgramNumber, bSkip, NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(HIDE_PROGRAM , u8CurrentProgramNumber, bSkip, NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_DIRECT_TUNED , u8CurrentProgramNumber, !bSkip, NULL);

    MSrv_Control::GetMSrvAtvDatabase()->SetFavoriteProgram(SET_FAVORITE_PROGRAM, u8CurrentProgramNumber, (U16)FALSE, NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(LOCK_PROGRAM , u8CurrentProgramNumber, FALSE, NULL);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(NEED_AFT , u8CurrentProgramNumber, TRUE, NULL);
    MSrv_Control::GetMSrvAtv()->EnableAFT(TRUE);//update atv_proc ATF flag
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(ENABLE_REALTIME_AUDIO_DETECTION , u8CurrentProgramNumber, TRUE, NULL);


    U16 wTmpVd = 0;
    wTmpVd = mapi_interface::Get_mapi_vd()->GetVideoStandardDetection(&wTmpVd);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_VIDEO_STANDARD_OF_PROGRAM, u8CurrentProgramNumber, wTmpVd, NULL);
    U8 m_u8ChannelNumber = 0;
    if(m_bCNIStatus == FALSE)
    {
        m_u8ChannelNumber = MW_ATV_Util::GetInstance()->CFTGetChannelNumber(u16TunerPLL);
        m_eMedium = MW_ATV_Util::GetInstance()->CFTGetMedium(u16TunerPLL);
        TunerConvertMediumAndChannelNumberToString(m_eMedium, m_u8ChannelNumber, m_au8CurrentStationName);
        m_u8SortingPriority = LOWEST_SORTING_PRIORITY;
    }

    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_STATION_NAME , u8CurrentProgramNumber , 0 , m_au8CurrentStationName);
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramInfo(SET_SORTING_PRIORITY, u8CurrentProgramNumber , m_u8SortingPriority , NULL);
    SetMediumAndChannelNumber(u8CurrentProgramNumber, m_eMedium, m_u8ChannelNumber);

//    MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(INC_CURRENT_PROGRAM_NUMBER , NULL , NULL , NULL);
}


///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
U8 MW_ATV_Scan_ESAsia_NTSC::GetMinChannelNO(void)
{
    return CHANNEL_CATV_MIN;
}

///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
U8 MW_ATV_Scan_ESAsia_NTSC::GetMaxChannelNO(void)
{
    return CHANNEL_CATV_MAX;
}

//////////////////
U32 MW_ATV_Scan_ESAsia_NTSC::GetScanShiftFreq(U32 u32RFFreqKhz,U8 u8RFShiftStep)
{
    switch(u8RFShiftStep)
    {
        case 0://+0
            return u32RFFreqKhz;

        case 1:// -1M
            return (u32RFFreqKhz-1000);

        case 2:// 1M
            return (u32RFFreqKhz+1000);

        case 3:// 2M
            return (u32RFFreqKhz+2000);

        case 4:// -2M
            return (u32RFFreqKhz-2000);

        default:
            return u32RFFreqKhz;
    }

    return u32RFFreqKhz;
}

U8 MW_ATV_Scan_ESAsia_NTSC::GetScanProgressPercent(void)
{
    U8 u8Percent;
    U32 u32UhfMaxFreq;
    u8Percent = 0;

    mapi_tuner* pTuner;
    pTuner = mapi_interface::Get_mapi_pcb()->GetAtvTuner(0);
    ASSERT(pTuner);

    pTuner->ExtendCommand(
        mapi_tuner_datatype::E_TUNER_SUBCMD_GET_UHF_MAX_FREQ,
        0,
        0,
        &u32UhfMaxFreq);

    if((m_IsManualScan == TRUE) && (IsSearched()))
    {
        return 100;
    }

    if(m_IsManualScan == TRUE)
    {
        if(m_manualSearchType == SearchUp)
        {
            if((m_u32StartFreq ) <= 48250 )
            {
                if((GetCurrentFreq() ) == (u32UhfMaxFreq ))
                {
                    printf("%s() LINE=%d\n",__func__,__LINE__);
                    return 100;
                }
            }
            else
            {
                if((GetCurrentFreq()/1000 ) == (m_u32StartFreq /1000 - 1))
                {
                    printf("%s() LINE=%d m_u32StartFreq =%d , Curent=%d\n",__func__,__LINE__,(int)m_u32StartFreq,(int)GetCurrentFreq());
                    return 100;
                }
            }
        }
        else
        {
            if((m_u32StartFreq ) >= (u32UhfMaxFreq ))
            {
                if((GetCurrentFreq() ) <= 48250 )
                {
                    printf("%s() LINE=%d\n",__func__,__LINE__);
                    return 100;
                }
            }
            else
            {
                if((GetCurrentFreq()/1000 ) == (m_u32StartFreq/1000 + 1))
                {
                    printf("%s() LINE=%d\n",__func__,__LINE__);
                    return 100;
                }
            }
        }
        return 0;
    }

    u8Percent = (U8)(((NowChannel() - GetMinChannelNO()) * 100) / (GetMaxChannelNO() - GetMinChannelNO())) ;
    printf("u8Percent=%d \tMin=%d \t Max=%d\n", u8Percent, GetMinChannelNO(), GetMaxChannelNO());

    return u8Percent;
}


void MW_ATV_Scan_ESAsia_NTSC::SendScanInfo()
{
    U32 u32FreqKHz = 0;

    u32FreqKHz = GetCurrentFreq();
    MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u32FrequencyKHz = u32FreqKHz;
    if(m_IsManualScan == FALSE)
    {
        //For Auto Tuning
        MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u8Percent = GetScanProgressPercent();
        MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u16CurScannedChannel = m_u8AutoScanChannel; //MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, NULL, NULL, NULL);
        MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.bIsScaningEnable = m_bATVScanThreadEnable;
        ATV_Scan_IFO("BBB: p=%d, Freq=%d, Num=%d \n, Thread_freq=%d"
                     , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u8Percent
                     , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u32FrequencyKHz
                     , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u16ScannedChannelNum
                     , (int)m_u32AutoScanFreq);
                  //   , (int)MSrv_Control::GetMSrvAtv()->mScan->m_u32StartFreq);
        ATV_Scan_IFO(">>>>>>>>> EV_ATV_AUTO_TUNING_SCAN_INFO EVENT SEND>>>>>>>\n");


        MSrv_Control::GetMSrvAtv()->PostEvent(0, EV_ATV_AUTO_TUNING_SCAN_INFO, (U32)&MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo);


        if(MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.bIsScaningEnable == FALSE)
        {
            ATV_Scan_IFO("---> Auto Scan Complete...(Percent=100)\n");
        }

    }
    else
    {
        MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u8Percent = GetScanProgressPercent();

        //U8 u8Percent;
        //u8Percent = MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u8Percent;
        MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.bIsScaningEnable = m_bATVScanThreadEnable;
        if(MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.bIsScaningEnable == FALSE)
        {
            //Have program => Stop it
            MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u16ScannedChannelNum = 1;

            ATV_Scan_IFO("BBB-2: p=%d, Freq=%d, Num=%d \n"
                         , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u8Percent
                         , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u32FrequencyKHz
                         , (int)MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo.u16ScannedChannelNum);


            //MSrv_Control::GetMSrvAtv()->PostEvent(0, EV_ATV_MANUAL_TUNING_SCAN_INFO, (U32)&MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo);

        }

        ATV_Scan_IFO("---> Manual Scan Complete...\n");
        ATV_Scan_IFO(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> EV_ATV_MANUAL_TUNING_SCAN_INFO EVENT SEND..\n");
        MSrv_Control::GetMSrvAtv()->PostEvent(0, EV_ATV_MANUAL_TUNING_SCAN_INFO, (U32)&MSrv_Control::GetMSrvAtv()->m_stAtvScannedInfo);
    }

}

BOOL MW_ATV_Scan_ESAsia_NTSC::DoInit(void)
{
    // For NTSC
    m_u8AutoScanChannelStart = 0;
    m_u8AutoScanChannelEnd = 0;
    m_u8AutoScanChannel = 0;
    m_u8TunerOffset = 0;
    m_u8TunerSignal = 0;
    m_u8AutoDebounce = 0;

    m_eNTSCCableType = NTSC_CABLE_STD;

    return TRUE;
}

///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
void MW_ATV_Scan_ESAsia_NTSC::DoScan() // US/TW NTSC
{
    //////////////////////////////////////////////////////////////////////////////
    int u8TVScanStep = NTSC_SCAN_START;

    mapi_demodulator_datatype::AFC wTmpIf;
    U8 ucAFCOffset;
    U16 wTmpVd = 0;

    U32 m_u32StartTime = mapi_time_utility::GetTime0();
    U32 u32OffsetFreq = 0;
    U32 u32RfShiftFreq = 0;
#if (NTSC_SCAN_MAX_SHIFT_STEP == 2)
    BOOL bCheck56IRC = TRUE;
#endif
    //////////////////////////////////////////////////////////////////////////////
    BOOL bDebugEnable = m_bDebugEnable;
    m_U32TimeOnSleep = 0 ;
    m_U32TimeOnScan = mapi_time_utility::GetTime0();
    m_U32TimeOnI2C = 0;
    //U8 waitTime;
    //char buf[10];
    ATV_Scan_LOG_FILE(FILE * pFile;)
    ATV_Scan_LOG_FILE(pFile = fopen("/Customer/scan_history.txt", "w");)
    ATV_Scan_LOG_FILE(fclose(pFile);)
    ATV_Scan_LOG_FILE(pFile = fopen("/Customer/vd_lock_history.txt", "w");)
    ATV_Scan_LOG_FILE(fclose(pFile);)
    ATV_Scan_DBG("=======================================================\nEnter Thread Body (NTSC)\n==================================================\n");
    mapi_interface::Get_mapi_vd()->StartAutoStandardDetection();
    mapi_interface::Get_mapi_vd()->SetHsyncDetectionForTuning(TRUE);
    //pthread_mutex_lock(&MSrv_Control::GetMSrvAtv()->m_mutex_Scan);
    mapi_scope_lock(scopeLock, &MSrv_Control::GetMSrvAtv()->m_mutex_Scan);

    //set the height of window size to 480 for avoiding showing garbage (ex. source:480 window: 576 => garbage displayed on the bottom )
    mapi_video_vd_cfg *pVDData = new(std::nothrow) mapi_video_vd_cfg;
    ASSERT(pVDData);
    pVDData->enVideoStandard = E_MAPI_VIDEOSTANDARD_PAL_M;
    mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetMode(pVDData);
    mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO stVideoARCInfo;
    memset(&stVideoARCInfo, 0, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO));
    mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_ATV)->SetWindow(NULL, NULL, &stVideoARCInfo);

    while(m_bATVScanThreadEnable)
    {
        if(m_bScanContinue)
        {
            if(mapi_time_utility::TimeDiffFromNow0(m_u32StartTime) > 20)
            {
                m_u32StartTime = mapi_time_utility::GetTime0();
                ATV_Scan_DBG("\033[0;32;34m allal ATV Tuning Loop StateTime=%d  \033[m \n", (int)m_u32StartTime);
                ATV_Scan_DBG("now state=%d\n", u8TVScanStep);

                ATV_Scan_FLOW_DBG("\r\n[S]:now state=%d", u8TVScanStep);

                switch(u8TVScanStep)
                {
                    case NTSC_SCAN_START:
                        ATV_Scan_FLOW_DBG("\r\n[S]:NTSC_SCAN_START");
                        ATV_Scan_DBG("\r\n==========CH=======%d ", m_u8AutoScanChannel);
                        m_U32NumOfScan += 1;
                        m_u8TunerOffset = 0;
                        m_u8TunerSignal = 0;
                        m_u8RFShiftStep = 0;
                        m_u8AutoDebounce = DEBOUNCE_DEFAULT;
                        m_u8RFDebounce = RF_DEBOUNCE_DEFAULT;

                        m_u32AutoScanFreq = tFreqCode_CATV[m_eNTSCCableType][m_u8AutoScanChannel];

                        u32RfShiftFreq = m_u32AutoScanFreq; //use for fine tune fail
                        m_u8OrgRFFreq = m_u32AutoScanFreq;
                        VifSetFrequency(m_u32AutoScanFreq);
                        u8TVScanStep++;

                        break;
                   case NTSC_SCAN_RF_CHECK:
                       ATV_Scan_FLOW_DBG("\r\n[S]:NTSC_SCAN_RF_CHECK");
                       wTmpIf = m_pDemodulator->ATV_GetAFC_Distance();
                       if(wTmpIf != mapi_demodulator_datatype::E_AFC_OUT_OF_AFCWIN)
                       {
                           ATV_Scan_DBG("\nVIF LOCK..");
                           u8TVScanStep = NTSC_SCAN_SIGNAL_CHECK;
                       }
                       else
                       {
                           ATV_Scan_DBG("\nVIF UNLOCK..");
                           if(m_u8RFDebounce==0)
                           {
#if(NTSC_SHIFT_SCAN_ENABLE == 1)
                               u8TVScanStep = NTSC_SCAN_RF_SHIFT_FREQ;
#else
                                u8TVScanStep = NTSC_SCAN_SAVE_DATA;
#endif
                           }
                           else
                           {
                               u8TVScanStep = NTSC_SCAN_RF_RECHECK;
                               m_u8RFDebounce--;
                           }
                       }
                       break;

                    case NTSC_SCAN_RF_SHIFT_FREQ:
#if(NTSC_SHIFT_SCAN_ENABLE == 1)
                        ATV_Scan_FLOW_DBG("\r\n[S]:NTSC_SCAN_RF_SHIFT_CHECK");
                        if(m_u8RFShiftStep < NTSC_SCAN_MAX_SHIFT_STEP)
                        {
                            //set next shift freq
                            m_u8RFShiftStep++;
                            m_u32AutoScanFreq = GetScanShiftFreq(m_u8OrgRFFreq,m_u8RFShiftStep);
                            u32RfShiftFreq = m_u32AutoScanFreq; //use for fine tune fail
                            VifSetFrequency(m_u32AutoScanFreq);
                            m_u8AutoDebounce = DEBOUNCE_DEFAULT;
                            m_u8RFDebounce = RF_DEBOUNCE_DEFAULT;
                            u8TVScanStep = NTSC_SCAN_RF_RECHECK;
                            //printf("\n m_u8RFShiftStep:%d",(U16)m_u8RFShiftStep);
                        }
                        else
                        {
#if (NTSC_SCAN_MAX_SHIFT_STEP == 2)
                           if(((m_u8AutoScanChannel == 5) || (m_u8AutoScanChannel == 6))
                                && bCheck56IRC == TRUE)// special for CH5 & CH6
                           {
                               m_u32AutoScanFreq = tFreqCode_CATV[NTSC_CABLE_IRC][m_u8AutoScanChannel];
                               VifSetFrequency(m_u32AutoScanFreq);
                               u8TVScanStep = NTSC_SCAN_RF_RECHECK;
                               m_u8AutoDebounce = DEBOUNCE_DEFAULT;
                               m_u8RFDebounce = RF_DEBOUNCE_DEFAULT;
                               bCheck56IRC = FALSE;
                           }
                           else
                           {
                            //save original freq
                            m_u32AutoScanFreq = GetScanShiftFreq(m_u8OrgRFFreq,0);
                            u32RfShiftFreq = m_u32AutoScanFreq; //use for fine tune fail
                            u8TVScanStep = NTSC_SCAN_SAVE_DATA;
                               bCheck56IRC = TRUE;
                           }
#else
                            //save original freq
                            m_u32AutoScanFreq = GetScanShiftFreq(m_u8OrgRFFreq,0);
                            u32RfShiftFreq = m_u32AutoScanFreq; //use for fine tune fail
                            u8TVScanStep = NTSC_SCAN_SAVE_DATA;
#endif
                        }
#endif
                        break;

                    case NTSC_SCAN_SIGNAL_CHECK:
                        ATV_Scan_FLOW_DBG("\r\n[S]:NTSC_SCAN_SIGNAL_CHECK");
                        wTmpVd = mapi_interface::Get_mapi_vd()->CheckStatusLoop();
                        wTmpIf = m_pDemodulator->ATV_GetAFC_Distance();
                        ATV_Scan_DBG("VD:%x..", wTmpVd);
                        ATV_Scan_DBG("VIF:%x..", wTmpIf);
#if 0 //(IF_SELECT==IF_R2S10401SP)
                        if(wTmpVd & VD_CHECK_HSYNC_LOCKED)
#else
                        if((wTmpVd & VD_CHECK_HSYNC_LOCKED) && (wTmpIf != mapi_demodulator_datatype::E_AFC_OUT_OF_AFCWIN))
#endif
                        {
                            ATV_Scan_DBG("VD&VIF LOCK..");
                            m_u8AutoDebounce++;
                            if(m_u8AutoDebounce == SIGANL_VALID)
                            {
                                m_u8TunerSignal = 1;
                                ucAFCOffset = CheckAfcWinPass(wTmpIf);
                                if(ucAFCOffset == AFC_GOOD)
                                {
                                    ATV_Scan_DBG("\r\nGOOD AFC");
                                    u8TVScanStep = NTSC_SCAN_CH_VIEW;
                                }
                                else
                                {
                                    u8TVScanStep = NTSC_SCAN_NOP_STEP;
                                    if((ucAFCOffset == AFC_INCREASE)||(ucAFCOffset == AFC_BIG_STEP_INC))
                                    {
                                        m_u8TunerOffset++;
                                        m_u32AutoScanFreq += m_u8TunerMinDistance;
                                    }
                                    else
                                    {
                                        m_u8TunerOffset--;
                                        m_u32AutoScanFreq -= m_u8TunerMinDistance;
                                    }

                                    VifSetFrequency(m_u32AutoScanFreq);
                                }
                            }
                            else
                            {
                                u8TVScanStep = NTSC_SCAN_RECHECK;
                            }
                        }
                        else
                        {
                            ATV_Scan_DBG("NO LOCK..");
                            m_u8AutoDebounce--;
                            if(m_u8AutoDebounce == SIGNAL_INVALID)
                            {

#if (NTSC_SHIFT_SCAN_ENABLE == 1)
                                u8TVScanStep = NTSC_SCAN_RF_SHIFT_FREQ;
#else
                                u8TVScanStep = NTSC_SCAN_SAVE_DATA;
#endif
                            }
                            else
                            {
                                u8TVScanStep = NTSC_SCAN_RECHECK;
                            }
                        }
                        break;

                    case NTSC_SCAN_CH_FINE_TUNE:
                    {
                        ATV_Scan_FLOW_DBG("\r\n[S]:NTSC_SCAN_CH_FINE_TUNE");
                        wTmpIf = m_pDemodulator->ATV_GetAFC_Distance();
                        ucAFCOffset = CheckAfcWinPass(wTmpIf);
                        u32OffsetFreq = (u32RfShiftFreq >= m_u32AutoScanFreq)? (u32RfShiftFreq - m_u32AutoScanFreq):(m_u32AutoScanFreq - u32RfShiftFreq);
                        ATV_Scan_FLOW_DBG("\r\n    - Offeset freq by fine tune: %d kHz",u32OffsetFreq);
                        if(ucAFCOffset == AFC_GOOD)
                        {
                            u8TVScanStep = NTSC_SCAN_CH_VIEW;
                        }
                        else if(u32OffsetFreq > MAX_FINE_TUNE_FREQ) // check fine tune offset range
                        {
                            m_u32AutoScanFreq = u32RfShiftFreq;
                            u8TVScanStep = NTSC_SCAN_CH_VIEW;
                        }
                        else
                        {
                            u8TVScanStep = NTSC_SCAN_NOP_STEP;
                            if((ucAFCOffset == AFC_INCREASE) || (ucAFCOffset == AFC_BIG_STEP_INC))
                            {
                                m_u8TunerOffset++;
                                m_u32AutoScanFreq += m_u8TunerMinDistance;
                            }
                            else
                            {
                                m_u8TunerOffset--;
                                m_u32AutoScanFreq -= m_u8TunerMinDistance;
                            }

                            VifSetFrequency(m_u32AutoScanFreq);
                        }
                        break;
                    }
                    case NTSC_SCAN_SAVE_DATA:
                        ATV_Scan_FLOW_DBG("\r\n[S]:NTSC_SCAN_SAVE_DATA");
                        ATV_Scan_DBG("\r\n  Signal=%x", m_u8TunerSignal);
                        ATV_Scan_DBG("\r\n  Offset=%x", m_u8TunerOffset);

                        //mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard((AUDIOSTANDARD_TYPE_)E_AUDIOSTANDARD_M_BTSC_ );
                        //m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_BTSC_);

                        //save for menual search no signal
                        if((m_IsManualScan == TRUE) && (!m_u8TunerSignal))
                        {
                            //set default freq
                            m_u32AutoScanFreq = tFreqCode_CATV[NTSC_CABLE_STD][m_u8AutoScanChannel];

                            //set save flag
                            #if (MSTAR_TVOS == 1)
                            //m_u8TunerSignal = TRUE; //Do not save channel data if thers is no channel scanned.
                            #else
                            m_u8TunerSignal = TRUE;
                            #endif

                        }
                        // Real Channel
                        if(m_u8TunerSignal)  //Do not save channel data if thers is no channel scanned.
                        {
                            ATV_Scan_FLOW_DBG("\r\n[CH]:%d have signal", m_u8AutoScanChannel);
							// EosTek Patch Begin
							//follow 828 modify about ATV number 
                            MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER, m_u8AutoScanChannel - 1, 0, NULL);    // For displaying correct channel number, passing "channel number - 1" to upper layer (mantis 0694026, 0721891)
                            // EosTek Patch End
							MemorizeProg(m_u32AutoScanFreq, !m_u8TunerSignal);

                        }

                        mapi_interface::Get_mapi_vif()->BypassDBBAudioFilter(FALSE);

                        m_u8AutoScanChannel++; // next
                        u8TVScanStep = NTSC_SCAN_START;
                        break;
                    default:
                        u8TVScanStep++;
                        break;
                }
            }
            else
            {
                U32 m_u32TmpTime = mapi_time_utility::GetTime0();
                U32 sleepTime =  20 - mapi_time_utility::TimeDiffFromNow0(m_u32StartTime);
                //if(sleepTime < 20 && sleepTime>=0)
                if(sleepTime < 20)
                {
                    ATV_Scan_DBG("time out for %u", sleepTime * 1000);
                    usleep(sleepTime * 1000);
                }
                m_U32TimeOnSleep += mapi_time_utility::TimeDiffFromNow0(m_u32TmpTime);
            }

            m_pDemodulator->ATV_VIF_Handler(TRUE);

            {
#if ( ENABLE_SKIP_SAME_FREQ )
                if(m_u32PrevScanFreq != m_u32AutoScanFreq)
#endif
                {
                    // For OSD update // NTSC ӬOq Ch. No. ......
                    switch(m_u8TunerMinDistance)
                    {
                        case 31:
                        {
                            U16 u16TunerPLL = (((m_u32AutoScanFreq * 1000) / 3125) + 5) / 10;
                            m_u32AutoScanFreq = (u16TunerPLL * 3125) / 100;
                            break;
                        }
                        case 62:
                        {
                            U16 u16TunerPLL = (((m_u32AutoScanFreq * 100) / 625) + 5) / 10;
                            m_u32AutoScanFreq = (u16TunerPLL * 625) / 10;
                            break;
                        }
                        default:
                            break;
                    }

                    //VifSetFrequency( m_u32AutoScanFreq );
                    m_u32PrevScanFreq = m_u32AutoScanFreq;
                    SendScanInfo();
                }
            }

            if(m_u8AutoScanChannel > m_u8AutoScanChannelEnd)  // end tuning
            {
                m_bATVScanThreadEnable = FALSE;
                m_AtvScanThread = 0;
                SendScanInfo();

                ATV_Scan_DBG("\nScan is Ending...");
            }

            //pthread_mutex_unlock(&MSrv_Control::GetMSrvAtv()->m_mutex_Scan);
            ATV_Scan_LOG_FILE(fclose(pFile);)
        }
        else
        {
            usleep(20 * 1000);
        }
    }

    //Init VIF
    //m_pDemodulator->ATV_VIF_Init();
    if(TRUE == bDebugEnable)
    {
        m_U32TimeOnScan = mapi_time_utility::TimeDiffFromNow0(m_U32TimeOnScan);
        ATV_Scan_PRINT("\n_ATV_Scan_Thread Exit Success\n m_U32TimeOnScan=%u, at %d\n", m_U32TimeOnScan, __LINE__);
        ATV_Scan_PRINT("\n_ATV_Scan_Thread Exit Success\n m_U32TimeOnI2C=%u\n", m_U32TimeOnI2C);
        ATV_Scan_PRINT("\n_ATV_Scan_Thread Exit Success\n m_U32TimeOnSleep=%u\n", m_U32TimeOnSleep);
    }
    //#endif
    //pthread_mutex_unlock(&MSrv_Control::GetMSrvAtv()->m_mutex_Scan);
    mapi_interface::Get_mapi_vd()->SetHsyncDetectionForTuning(FALSE);
    if(pVDData != NULL)
    {
        delete pVDData;
        pVDData = NULL;
    }
    ATV_Scan_DBG("\n_ATV_Scan_Thread Exit Success\n Freq=%d, at %d\n", m_u32StartFreq, __LINE__);
}

void MW_ATV_Scan_ESAsia_NTSC::DoStart(void)
{
#if (NTSC_CABLE_HRC_IRC_AUTO)
        m_eNTSCCableType = NTSC_CABLE_AUTO;
#else
        m_eNTSCCableType = NTSC_CABLE_STD;
#endif

    m_u8AutoScanChannelStart = GetMinChannelNO();
    m_u8AutoScanChannelEnd = GetMaxChannelNO();
    m_u8AutoScanChannel = m_u8AutoScanChannelStart;
    MSrv_Control::GetMSrvAtvDatabase()->SetProgramCtrl(SET_CURRENT_PROGRAM_NUMBER, (U16)m_u8AutoScanChannel, 0, NULL);

    mapi_interface::Get_mapi_audio()->SIF_SetAudioStandard((AUDIOSTANDARD_TYPE_)E_AUDIOSTANDARD_M_BTSC);
    m_pDemodulator->ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_BTSC_);
    VifSetSoundStandard(TV_SOUND_M);
}


#endif
