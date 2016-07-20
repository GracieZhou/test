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
///////////////////////////////////////////////////////////////////////////////////////////////////
/// @file MSrv_ATV_Player.h
/// @brief \b Introduction: ATV Player Description:
///
///         It is the ATV player class.
///
///         All of the ATV player related functions are implemented here.
///
/// \b How_to_use:
///
///   <1>. Auto Tuning
///     - User can call SetAutoTuningStar to star Auto Tuning.
///     - User can call SetAutoTuningPause to pause Auto Tuning.
///     - User can call SetAutoTuningResume to restart Auto Tuning.
///     - User can call SetAutoTuningEnd to end Auto Tuning.
///
/// @image html AutoTuning.JPG "Use Case - Auto Tuning"
///
///   <2>. Change Channel
///     - User can call SetToNextChannel to change program to Next Channel
///     - User can call SetToPreChannel to change program to Prev Channel
///
/// @image html SetChannel.JPG "Use Case - Change Channel"
///
/// @author MStar Semiconductor Inc.
///
/// \b Features:
/// - Support the auto tuning / manual tuning for ATV
/// - Support the channel change function for ATV
/// - Support the upper layer to get the channel information for ATV
///
///////////////////////////////////////////////////////////////////////////////////////////////////

/*@ <IncludeGuard> @*/
#ifndef MSrv_ATV_Player_H
#define MSrv_ATV_Player_H
/*@ </IncludeGuard> @*/

#include "mapi_tuner.h"

#include "MSrv_TV_Player.h"
#include "MSrv_ATV_Database.h"
#include "mapi_vd.h"

#if(ISDB_CC_ENABLE == 1)
#include "mapi_closedcaption_brazil.h"
#elif((ATSC_CC_ENABLE == 1)||(NTSC_CC_ENABLE == 1))
#include "mapi_closedcaption.h"
#endif

#if (HBBTV_ENABLE == 1)
#include "../../dvb/base/middleware/hbbtv/inc/MW_HBBTV.CommandHandlerBase.h"
#endif

class mapi_demodulator;
class MW_ATV_Scan;
class MW_DTV_Scan_ATSC;
class MW_ATV_Util;
#if (TTX_ENABLE == 1)
class MW_TTX;
#endif

/*@ <Definitions> @*/

/// the main loop interval in millisecond
#define MAIN_LOOP_INTERVAL          10              // ms
/// wait N milliseconds
#define WAIT_N_ms(N)        ((N)/MAIN_LOOP_INTERVAL)

/// the CNI customization flag
#ifndef ENABLE_CUSTOMER_ATS_TABLE
#define ENABLE_CUSTOMER_ATS_TABLE 0
#endif

/// the switch of ATV Shift Patch mechanism
#ifdef TV_FREQ_SHIFT_CLOCK
#undef TV_FREQ_SHIFT_CLOCK
#endif

// The disturbance issue has been fixed by HW, so disable shift clock function by default.
#define TV_FREQ_SHIFT_CLOCK 0

#if( TV_FREQ_SHIFT_CLOCK )
/// define the mode enum of frequency
typedef enum
{
    SHIFT_CLK_ORIGIN_43d2M,
    SHIFT_CLK_TYPE1_42M,
    SHIFT_CLK_TYPE2_44d4M
} TV_FREQ_SHIFT_MODE;
#endif

/// The class for MSrv ATV Player
/// @code
///
///    if(FALSE == SetAutoTuningStart())
///    {
///        // there is problem to start auto tuning, do error handling here
///    }
///
///    if(FALSE == SetAutoTuningPause())
///    {
///        // there is problem to pause auto tuning, do error handling here
///    }
///
///	   if(FALSE == SetAutoTuningResume())
///    {
///        // there is problem to resume auto tuning, do error handling here
///    }
///
/// 	  if(FALSE == SetAutoTuningEnd())
///    {
///        // there is problem to stop auto tuning, do error handling here
///    }
/// @endcode
class MSrv_ATV_Player : public MSrv_TV_Player
#if (HBBTV_ENABLE == 1)
        ,public MW_HBBTV_CommandHandlerBase
#endif
{
// ****************************************************************
// Public
// ****************************************************************
public:
    friend class MW_ATV_Scan;
    friend class MW_ATV_Scan_NTSC;
#if (ISDB_SYSTEM_ENABLE == 1)
    friend class MW_ATV_Scan_Brazil;
#endif
    friend class MW_ATV_Scan_EU;
    friend class MW_ATV_Scan_AsiaChina;
#if (ESASIA_NTSC_SYSTEM_ENABLE == 1)
    friend class MW_ATV_Scan_ESAsia_NTSC;  //Add for ES Asia/TW ATV tuing 20140526EL
#endif
    friend class MW_DTV_Scan_ATSC;

    // ------------------------------------------------------------
    // Enum Define
    // ------------------------------------------------------------

    /// Define the Channel Search type
    typedef enum
    {
        /// Search Left
        E_CHANNEL_SEARCH_LEFT,
        /// Search Right
        E_CHANNEL_SEARCH_RIGHT,
        ///No Search Type
        E_CHANNEL_SEARCH_NONE,
    } EN_CHANNEL_SEARCH_TYPE;


    /// Current ATV Tunning Type
    typedef enum
    {
        /// Auto Tunning
        E_SCAN_AUTO_TUNING,
        /// Manual Tunning
        E_SCAN_MANUAL_TUNING,
    } eScanMode;

    /// Scan State
    typedef enum
    {
        /// Runing Scan State
        E_SCAN_STATE_RUNNING,
        /// Pause Scan State
        E_SCAN_STATE_PAUSE,
        /// Stop Scan State
        E_SCAN_STATE_STOP,
     } eScanState;


    /// Tuning Frequency Signal Lock Status
    typedef enum
    {
        /// Tuning status good
        E_TUNING_STATUS_GOOD,
        /// Tuning frequnecy over
        E_TUNING_STATUS_OVER,
        /// Tuning frequency under
        E_TUNING_STATUS_UNDER,
        /// Tuning frequnecy over a lot
        E_TUNING_STATUS_OVER_MORE,
        /// Tuning frequnecy under a lot
        E_TUNING_STATUS_UNDER_MORE,
        /// Tuning frequnecy is out of lock window
        E_TUNING_STATUS_OUT_OF_AFCWIN,
    } TUNING_STATUS;

    /// Tuning Search Direction
    typedef enum
    {
        /// Search Direction Up
        DIRECTION_UP,
        /// Search Direction Down
        DIRECTION_DOWN
    } DIRECTION;

    /// Auto Scan State /////// is this flag still in use?
    typedef enum
    {
        /// normal auto scan
        E_NORMAL_AUTO_SCAN,
        /// NTSC auto scan all
        E_AUTO_SCAN_ALL,
    } eAutoScanState;

#if (MSTAR_TVOS == 1)
    ///channel volume compensation state
    typedef enum
    {
        /// current channel volume compensation
        E_CURRENT_CHNNEL_VOLUME_COMPENSATION,
        /// next channel volume compensation
        E_NEXT_CHNNEL_VOLUME_COMPENSATION,
        /// previous channel volume compensation
        E_PRE_CHNNEL_VOLUME_COMPENSATION,
        /// select channel volume compensation
        E_SEL_CHNNEL_VOLUME_COMPENSATION

    } EN_CHANNEL_VOLUME_COM_STATE;
#endif

    // ------------------------------------------------------------
    // Other Define
    // ------------------------------------------------------------
    //#define EV_ATV_AUTO_TUNING_SCAN_INFO        EV_END+1  //move to muf
    //#define EV_ATV_MANUAL_TUNING_SCAN_INFO      EV_END+2  //move to muf

#ifndef  ENABLE_V_RANGE_HANDLE_ATV
///To Enable V Range Handle (VD)
#define ENABLE_V_RANGE_HANDLE_ATV    1   //wjq20100128
#endif

// AFT internal states
/// AFT State: idle state
#define AFT_IDLE                        0x10
///AFT default
#define AFT_OFFSET_0                    0x80

    // In the AFT_SHOWTIME2 State, sometimes it will goto Sync-Unloc
    // Here we create the configurations for user to adjust
    //<1>.Configuration-A: 60% LOCK = PASS
    //<2>.Configuration-B: 20% LOCK = PASS
    //<3>.Configuration-C: ALL PASS
    //PS: Before tye this configuration, please try the VD-Sensitivit first
    //     in the drvVD.c #define HSEN_NORMAL_...
#if (ATSC_SYSTEM_ENABLE == 1)
/// For ATSC about check signal period
#define THREAD_MONITOR_CHECK_SIGNAL_STATUS_SEC  (1*120)
#endif


    /// AFT external Steps
    typedef U32 eAFTSTEP;

/// Check bit about hsync locked
#define VD_CHECK_HSYNC_LOCKED             BIT14

    // ------------------------------------------------------------
    // Structure Define
    // ------------------------------------------------------------

    /// Define the channel scan information for ATV
    typedef struct
    {
        /// the interval of sending event to UI
        U32 u32EventInterval;
        /// the start frequency of scan
        U32 u32StartFrequency;
        /// the end frequency of scan
        U32 u32EndFrequency;
        /// the scan type
        U8  u8ScanType;
        /// the scan state
        eScanState  u8ScanState;
        /// the total scanned channel number
        U16 u16TotalChannelNum;
        /// the manual tuning mode
        eAtvManualTuneMode aATVMannualTuneMode;
    } ST_ATV_SCAN_PARAMETER;

    /// Define struct to store frequency and channel mapping table
    typedef struct
    {
        /// frequency
        U16 wFrequency;
        /// channel
        U16 wChannel;
    } ST_FREQ_CHANNEL;

    /// Define the channel info for ATV
    typedef struct
    {
        /// the frequency in kHz
        U32 u32FrequencyKhz;
        /// the program number
        U8  u8ProgramNum;
        /// the channel attribute
        ST_ATV_MISC stMisc;
        /// the channel name
        U8 au8Name[MAX_STATION_NAME];
    } ST_MSRV_CHANNEL_INFO;

    /// Define the scan even timer
    typedef struct
    {
        /// timer switch , TRUE: on ;FALSE:off
        BOOL timerSwitch;
        /// start time
        U32 startTime;
    }ST_ATV_EVENT_TIMER;

    // ------------------------------------------------------------
    // Variable Define
    // ------------------------------------------------------------

    // ------------------------------------------------------------
    // Function Define
    // ------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------
    /// Constructor of MSrv_ATV_Player.
    /// @return  None
    //-------------------------------------------------------------------------------------------------
    MSrv_ATV_Player();

    //-------------------------------------------------------------------------------------------------
    /// Destructor of MSrv_ATV_Player.
    /// @return  None
    //-------------------------------------------------------------------------------------------------
    virtual ~MSrv_ATV_Player();

#if (VE_ENABLE == 1 || CVBSOUT_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Initialize Video out
    /// @param  eInputType      \b IN: Identification of specific input source.
    /// @param eWin             \b IN: output window
    /// @return                 \b OUT: True, or False
    /// @code
    ///
    ///      //when VE ENABLE this method is used to init video out.
    ///
    ///     InitVideoOut(MAPI_INPUT_SOURCE_ATV, MAPI_MAIN_WINDOW);
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual BOOL InitVideoOut(MAPI_INPUT_SOURCE_TYPE eInputType, MAPI_SCALER_WIN eWin);

    //-------------------------------------------------------------------------------------------------
    /// Finalize Video out
    /// @return                 \b OUT: True, or False
    //-------------------------------------------------------------------------------------------------
    virtual BOOL FinalizeVideoOut();
#endif
  // EosTek Patch Begin
    //-------------------------------------------------------------------------------------------------
    /// This function is get tuner status.
    /// @return                 \b OUT: True, or False
    //-------------------------------------------------------------------------------------------------
    BOOL TunerStatus(void);
  // EosTek Patch End

    //------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------
    /// To get IsDiffVideoStandByChannelAndVD while  ATV  channel change
    /// @return                 \b OUT: True, or False
    //-------------------------------------------------------------------------------------------------
    virtual BOOL IsDiffVideoStandByChannelAndVD(void);

    //-------------------------------------------------------------------------------------------------
    /// Get the information of current video, h-resolution v-resolution, etc.....
    /// @param pVideoInfo         \b OUT: video information
    /// @return None
    /// @code
    ///
    ///      GetVideoInfo(&Video_Info);
    ///
    ///      printf("Video_Info.u16HResolution = %d\n",Video_Info.u16HResolution);
    ///      printf("Video_Info.u16VResolution = %d\n",Video_Info.u16VResolution);
    ///      printf("Video_Info.u16FrameRate = %d\n",Video_Info.u16FrameRate);
    ///      printf("Video_Info.u8ModeIndex = %d\n",Video_Info.u8ModeIndex);
    ///      printf("Video_Info.enScanType = %d\n",Video_Info.enScanType);
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual void GetVideoInfo(ST_VIDEO_INFO *pVideoInfo);

    //-------------------------------------------------------------------------------------------------
    /// To start the ATV auto scan
    /// @param u32EventIntervalMs   \b IN: interval to send the event to update the scan status OSD
    /// @param u32FrequencyStart    \b IN: start frequency of auto scan
    /// @param u32FrequencyEnd      \b IN: end freqency of auto scan
    /// @param eScanState       \b IN: auto tuning type
    /// @return                     \b OUT: True, or False
    /// @code
    ///
    ///     SetAutoTuningStart(AUTO_TUNING_RECEIVE_EVENT_INTERVAL, AUTO_TUNING_FREQ_START, AUTO_TUNING_FREQ_END);
    ///
    ///     if(FALSE == SetAutoTuningPause())
    ///     {
    ///         //pause fail error handling
    ///     }
    ///     if(FALSE == SetAutoTuningResume())
    ///     {
    ///         //resume fail error handling
    ///     }
    ///     if(FALSE == SetAutoTuningEnd())
    ///     {
    ///         //stop fail error handling
    ///     }
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual BOOL SetAutoTuningStart(U32 u32EventIntervalMs, U32 u32FrequencyStart, U32 u32FrequencyEnd, eAutoScanState eScanState = E_NORMAL_AUTO_SCAN);

#if ((ATSC_SYSTEM_ENABLE == 1)||(ISDB_SYSTEM_ENABLE == 1)||(ESASIA_NTSC_SYSTEM_ENABLE == 1))
    //-------------------------------------------------------------------------------------------------
    /// To start the Direct Tune for backward compatible reserve, will remove in the future
    /// @param u16MajorNum   \b IN: major number
    /// @param u16MinorNum    \b IN: minor number
    /// @return  None.
    /// @code
    ///
    ///     //start direct tune for NTSC.
    ///
    ///     NTSCStartDirectTune(u16MajorNum, u16MinorNum);
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual void NTSCStartDirectTune(U16 u16MajorNum,U16 u16MinorNum);
#endif

    //-------------------------------------------------------------------------------------------------
    /// To pause the ATV auto scan
    /// @return                 \b OUT: True, or False
    //-------------------------------------------------------------------------------------------------
    virtual BOOL SetAutoTuningPause();

    //-------------------------------------------------------------------------------------------------
    /// To resume the ATV auto scan
    /// @return                 \b OUT: True, or False
    //-------------------------------------------------------------------------------------------------
    virtual BOOL SetAutoTuningResume();

    //-------------------------------------------------------------------------------------------------
    /// To stop the ATV auto scan
    /// @return                 \b OUT: True, or False
    //-------------------------------------------------------------------------------------------------
    virtual BOOL SetAutoTuningEnd();

    //-------------------------------------------------------------------------------------------------
    /// To set current program block or not
    /// @param set   \b IN:  True or False
    ///@return  None.
    /// @code
    ///
    ///     //block the current program.
    ///
    ///     SetCurrentProgramBlock(TRUE);
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
   virtual void SetCurrentProgramBlock(BOOL set);

    //-------------------------------------------------------------------------------------------------
    /// To get current program block state (true if  program been locked)
    /// @return                     \b OUT: True, or False
    //-------------------------------------------------------------------------------------------------
   virtual BOOL GetCurrentProgramBlock(void);

    //-------------------------------------------------------------------------------------------------
    /// To start the ATV manual tuning
    /// @param u32EventIntervalMs   \b IN: interval to send the event to update the scan status OSD
    /// @param u32Frequency    \b IN: the frequency to do the manual searxh
    /// @param eMode                \b IN: manual tuning mode
    /// @return                     \b OUT: True, or False
    /// @code
    ///
    ///     SetChannelSearchType(E_CHANNEL_SEARCH_LEFT);
    ///     AddEventRecipient(this);
    ///     SetManualTuningStart(u32EventIntervalMs, u32Frequency, E_MANUAL_TUNE_MODE_SEARCH_ONE_TO_DOWN)
    ///     .
    ///     .
    ///     .
    ///     SetManualTuningEnd();
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual BOOL SetManualTuningStart(U32 u32EventIntervalMs, U32 u32Frequency, eAtvManualTuneMode  eMode);

    //-------------------------------------------------------------------------------------------------
    /// To stop the ATV manual tuning
    /// @return  None.
    //-------------------------------------------------------------------------------------------------
    virtual void SetManualTuningEnd();

    //-------------------------------------------------------------------------------------------------
    /// To get the frequency of current channel
    /// @param pu32Frequency        \b OUT: current channel's frequency
    /// @return                     \b OUT: True, or False
    //-------------------------------------------------------------------------------------------------
    virtual BOOL GetCurrentFrequency(U32 * pu32Frequency);

    //-------------------------------------------------------------------------------------------------
    /// Set the specific frequency to tuner
    /// @param u32Frequency        \b IN: channel's frequency to set
    /// @return                    \b OUT: True, or False
    //-------------------------------------------------------------------------------------------------
    virtual BOOL SetFrequency(U32 u32Frequency);

    //-------------------------------------------------------------------------------------------------
    /// Get the current channel number
    /// @param pu16ChannelNum        \b OUT: current channel number
    /// @return                      \b OUT: True, or False
    /// @code
    ///
    ///     U16 u16TotalNumver = 0;
    ///
    ///     GetTotalChannelNumber(&u16TotalNumver);
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual BOOL GetCurrentChannelNumber(U16 *  pu16ChannelNum);

    //-------------------------------------------------------------------------------------------------
    /// Get total channel number
    /// @param pu16TotalChannelNum   \b OUT: total channel number
    /// @return                      \b OUT: True, or False
    //-------------------------------------------------------------------------------------------------
    virtual BOOL GetTotalChannelNumber(U16 *  pu16TotalChannelNum);

    //-------------------------------------------------------------------------------------------------
    /// Get the program information
    /// @param stChannelInfo                \b OUT: program information
    /// @param u8Index                           \b IN: channel index number
    /// @return                                            \b OUT: True, or False
    /// @code
    ///
    ///     ST_DTV_PROGRAM_INFO progInfo;
    ///
    ///     if(FALSE == GetProgramInfoByIndex(progInfo, u8Index))
    ///     {
    ///         //handling
    ///     }
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual BOOL GetProgramInfoByIndex(ST_MSRV_CHANNEL_INFO &stChannelInfo, U8 u8Index);

    //-------------------------------------------------------------------------------------------------
    /// Get the current program information
    /// @param stChannelInfo                 \b OUT: current program information
    /// @return                                           \b OUT: True, or False
    /// @code
    ///
    ///     ST_MSRV_CHANNEL_INFO stChannelInfo;
    ///     ST_CM_PROGRAM_INFO *pResult=NULL;
    ///     memset(&stChannelInfo, 0, sizeof(MSrv_ATV_Player::ST_MSRV_CHANNEL_INFO));
    ///
    ///     if(MSrv_Control::GetMSrvAtv()->GetCurrentProgramInfo(stChannelInfo) == FALSE)
    ///     {
    ///
    ///         pResult->unProgNumber.u32Number     =  stChannelInfo.u8ProgramNum;
    ///         pResult->u8Favorite   =  stChannelInfo.stMisc.u8Favorite;
    ///         pResult->bIsLock       =  stChannelInfo.stMisc.bIsLock;
    ///         pResult->bIsSkip       =  stChannelInfo.stMisc.bSkip;
    ///         pResult->bIsScramble   =  FALSE;
    ///         pResult->bIsDelete     =  FALSE;
    ///         pResult->bIsVisible    = TRUE;
    ///         pResult->bIsSkip       =  stChannelInfo.stMisc.bSkip;
    ///         pResult->bIsHide       =  stChannelInfo.stMisc.bHide;
    ///         pResult->u8ServiceType =  E_SERVICETYPE_ATV;
    ///         pResult->sServiceName.assign((char *)stChannelInfo.au8Name);
    ///
    ///     }
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual BOOL GetCurrentProgramInfo(ST_MSRV_CHANNEL_INFO &stChannelInfo);

    //-------------------------------------------------------------------------------------------------
    /// Set channel to specific channel number
    /// @param u16ChannelNum        \b IN: specific channel number
    /// @param bCheckBlock          \b IN: block(True) or non-block(False)
    /// @return                     \b OUT: E_SET_CHANNEL_SUCCESS or E_SET_CHANNEL_FAIL or E_SET_CHANNEL_BLOCK
    /// @code
    ///
    ///     EN_SET_CHANNEL_ERROR_CODE RetCode = SetChannel(u16ChannelNum, TRUE);
    ///
    ///     if(RetCode==E_SET_CHANNEL_SUCCESS)
    ///     {
    ///          //sucess handling here.
    ///     }
    ///     else if(RetCode==E_SET_CHANNEL_FAIL)
    ///     {
    ///          //error handling here.
    ///     }
    ///     else
    ///     {
    ///          //program block handling here.
    ///     }
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual EN_SET_CHANNEL_ERROR_CODE SetChannel(U16 u16ChannelNum, BOOL bCheckBlock);

    //-------------------------------------------------------------------------------------------------
    /// Set to the previous channel
    /// @param bIncludeSkipped      \b IN: Include skipped program or not
    /// @param bCheckBlock          \b IN: block or non-block
    /// @return                     \b OUT: E_SET_CHANNEL_SUCCESS or E_SET_CHANNEL_FAIL or E_SET_CHANNEL_BLOCK
    /// @code
    ///
    ///     EN_SET_CHANNEL_ERROR_CODE RetCode = SetToPreChannel(FALSE, TRUE);
    ///
    ///     if(RetCode==E_SET_CHANNEL_SUCCESS)
    ///     {
    ///          //sucess handling here.
    ///     }
    ///     else if(RetCode==E_SET_CHANNEL_FAIL)
    ///     {
    ///          //error handling here.
    ///     }
    ///     else
    ///     {
    ///          //program block handling here.
    ///     }
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual EN_SET_CHANNEL_ERROR_CODE SetToPreChannel(BOOL bIncludeSkipped, BOOL bCheckBlock);

    //-------------------------------------------------------------------------------------------------
    /// Set to the next channel
    /// @param bIncludeSkipped      \b IN: Include skipped program or not
    /// @param bCheckBlock          \b IN: block or non-block
    /// @return                     \b OUT: E_SET_CHANNEL_SUCCESS or E_SET_CHANNEL_FAIL or E_SET_CHANNEL_BLOCK
    /// @code
    ///
    ///     EN_SET_CHANNEL_ERROR_CODE RetCode = SetToNextChannel(FALSE, TRUE);
    ///
    ///     if(RetCode==E_SET_CHANNEL_SUCCESS)
    ///     {
    ///          //sucess handling here.
    ///     }
    ///     else if(RetCode==E_SET_CHANNEL_FAIL)
    ///     {
    ///          //error handling here.
    ///     }
    ///     else
    ///     {
    ///          //program block handling here.
    ///     }
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual EN_SET_CHANNEL_ERROR_CODE SetToNextChannel(BOOL bIncludeSkipped, BOOL bCheckBlock);

    //-------------------------------------------------------------------------------------------------
    /// Return to the previous channel that you watch
    /// @param bCheckBlock          \b IN: block or non-block
    /// @return                     \b OUT: E_SET_CHANNEL_SUCCESS or E_SET_CHANNEL_FAIL or E_SET_CHANNEL_BLOCK
    /// @code
    ///
    ///     EN_SET_CHANNEL_ERROR_CODE RetCode = SetToRtnChannel(TRUE);
    ///
    ///     if(RetCode==E_SET_CHANNEL_SUCCESS)
    ///     {
    ///          //sucess handling here.
    ///     }
    ///     else if(RetCode==E_SET_CHANNEL_FAIL)
    ///     {
    ///          //error handling here.
    ///     }
    ///     else
    ///     {
    ///          //program block handling here.
    ///     }
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual EN_SET_CHANNEL_ERROR_CODE SetToRtnChannel(BOOL bCheckBlock);

    //-------------------------------------------------------------------------
    /// Set the screen to snowflake
    /// @return None
    //---------------------------------------------------------------------------
    void SetToSnowflakeScreen(void);

    //-------------------------------------------------------------------------------------------------
    /// Get Sound System
    /// @return                     \b OUT: EN_MSRV_UI_SOUND_SYSTEM
    //-------------------------------------------------------------------------------------------------
    virtual EN_MSRV_UI_SOUND_SYSTEM GetSoundSystem(void);

    //-------------------------------------------------------------------------------------------------
    /// Force Set to Sound System
    /// @param eForceSoundSystem    \b IN: Input the Audio Sound System
    /// @return                     \b OUT: True or False
    /// @code
    ///
    ///     if(FALSE == SetForceSoundSystem(E_SYSTEM_MODE_BG))
    ///     {
    ///          //error handling here.
    ///     }
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual BOOL SetForceSoundSystem(EN_MSRV_UI_SOUND_SYSTEM eForceSoundSystem);

#if (ISDB_SYSTEM_ENABLE == 1)
    /******************************************************************************/
    ///- This function will set video standard for brazil
    /// @param eForceVideoSystem \b IN: video standard detected
    /// @return                     \b OUT: True or False
    /// @code
    ///
    ///     if(FALSE == SetForceBrazilVideoStandardSystem(E_VIDEOSTANDARD_BRAZIL_NTSC_M))
    ///     {
    ///          //error handling here.
    ///     }
    ///
    /// @endcode
    /******************************************************************************/
    virtual BOOL SetForceBrazilVideoStandardSystem(EN_MSRV_VIDEOSTANDARD_BRAZIL_TYPE eForceVideoSystem);

#endif
    /******************************************************************************/
    ///- This function will force set video standard
    /// @param eForceVideoSystem \b IN: video standard detected
    /// @return                     \b OUT: True or False
    /// @code
    ///
    ///    if(FALSE == SetForceVideoStandardSystem(E_ATV_VIDEOSTANDARD_PAL_M))
    ///    {
    ///        //error handling here.
    ///    }
    ///
    /// @endcode
    /******************************************************************************/
    virtual BOOL SetForceVideoStandardSystem(EN_MSRV_ATV_VIDEOSTANDARD_TYPE eForceVideoSystem);

    //-------------------------------------------------------------------------------------------------
    /// Detect ATV Player Current Setting Video Standard
    /// (for UI Query information, plz use MSrv_ATV_Database::ATVGetVideoStandardOfProgram instead)
    /// @return                     \b OUT: EN_MSRV_ATV_VIDEOSTANDARD_TYPE
    /// @code
    ///
    ///     EN_MSRV_ATV_VIDEOSTANDARD_TYPE videostandard;
    ///
    ///     videostandard = DetectVideoStardSystem();
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual EN_MSRV_ATV_VIDEOSTANDARD_TYPE DetectVideoStardSystem();

    //-------------------------------------------------------------------------------------------------
    /// Get Channel Search Type
    /// @return                     \b OUT: EN_CHANNEL_SEARCH_TYPE
    //-------------------------------------------------------------------------------------------------
    virtual EN_CHANNEL_SEARCH_TYPE GetChannelSearchType(void);

    //-------------------------------------------------------------------------------------------------
    /// Set Channel Search Type
    /// @param enType               \b IN: Input the Channel Search Type
    /// @return                     \b OUT: True or False
    /// @code
    ///
    ///     if(FALSE == SetChannelSearchType(enType))
    ///     {
    ///         //error handliing here.
    ///     }
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual BOOL SetChannelSearchType(EN_CHANNEL_SEARCH_TYPE enType);

    //-------------------------------------------------------------------------------------------------
    /// The interface for MSrv_ATV_Database using.
    //-------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------
    /// Check need to do AFT or not
    /// @return                                        \b OUT: True or False
    /// @code
    ///
    ///     if(TRUE == IsAFTEnabled())
    ///     {
    ///         //handling here.
    ///     }
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual BOOLEAN IsAFTEnabled(void);

    //-------------------------------------------------------------------------------------------------
    /// Check need to do AFT or not //for back compaitbility , going to remove
    /// @return                                        \b OUT: True or False
    /// @code
    ///
    ///     if(TRUE == TunerIsAFTNeeded())
    ///     {
    ///         //handling here.
    ///     }
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual BOOLEAN TunerIsAFTNeeded(void); //for back compaitbility , going to remove

    //-------------------------------------------------------------------------------------------------
    /// Enable AFT or not
    /// @param bAFTNeed \b  IN: AFT needed flag
    /// @return None
    /// @code
    ///
    ///      EnableAFT(TRUE);
    ///
    ///      InitAtvDemodTuner();
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual void EnableAFT(BOOLEAN bAFTNeed);

    //-------------------------------------------------------------------------------------------------
    /// Set AFT flag //for back compaitbility , going to remove
    /// @param bAFTNeed \b  IN: AFT needed flag
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void SetTunerAFTNeeded(BOOLEAN bAFTNeed);//for back compaitbility , going to remove
#if 0 //no more use
    //-------------------------------------------------------------------------------------------------
    /// This function is valid after finetune and will return the station name in char* sName
    /// @param sName \b  IN: station name
    /// @return none
    //-------------------------------------------------------------------------------------------------
    virtual void TunerGetCurrentStationName(U8 *sName);
#endif
#if 0
    //-------------------------------------------------------------------------------------------------
    /// This function is called to get current channel number. it is not valid
    /// @return U8: channel number.
    //-------------------------------------------------------------------------------------------------
    virtual U8 TunerGetChannelNumber(void);
#endif
    //-------------------------------------------------------------------------------------------------
    /// Check whether signal stable or not
    /// @return                                   \b OUT: True or False
    /// @code
    ///
    ///     if(TRUE == IsSignalStable())
    ///     {
    ///          //if signal is stable, handling here.
    ///     }
    ///     else
    ///     {
    ///         //if signal is not stable, handling here.
    ///     }
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual BOOL IsSignalStable(void);

    //------------------------------------------------------------------------------
    /// -This function will save current freq program to program number u8CurrentProgramNumber
    /// @param u8CurrentProgramNumber \b IN: program number
    /// @return None
    /// @code
    ///
    ///     ATVSaveProgram(u8CurrentProgramNumber);
    ///
    /// @endcode
    //------------------------------------------------------------------------------
    virtual void ATVSaveProgram(U8 u8CurrentProgramNumber);

    //------------------------------------------------------------------------------
    /// -This function will init the scart out
    /// @return                 \b OUT: True, or False
    //------------------------------------------------------------------------------
    virtual BOOL InitAtvDemodTuner(void);

    //------------------------------------------------------------------------------
    /// -This function will Finalize the scart out
    /// @return                 \b OUT: True, or False
    /// @code
    ///
    ///     if(TRUE == FinalizeAtvDemodTuner())
    ///     {
    ///         //handling here.
    ///     }
    ///
    /// @endcode
    //------------------------------------------------------------------------------
    virtual BOOL FinalizeAtvDemodTuner(void);

#if ((ATSC_CC_ENABLE == 1)||(NTSC_CC_ENABLE == 1)||(ISDB_CC_ENABLE == 1))
    //------------------------------------------------------------------------------
    /// -Has  CC or  not
    /// @return TRUE or FALSE
    //------------------------------------------------------------------------------
    BOOL IsCCExist();
#endif

#if (ATSC_SYSTEM_ENABLE == 1 ||ISDB_SYSTEM_ENABLE == 1)
    //------------------------------------------------------------------------------
    /// -Set NTSC Antenna Type Cable or Air
    /// @param eAntenna \b In: Air or Cable
    /// @return                 \b OUT: True, or False
    /// @code
    ///
    ///     if(TRUE == SetNTSCAntenna())
    ///     {
    ///         //handling here.
    ///     }
    ///
    /// @endcode
    //------------------------------------------------------------------------------
    virtual BOOL SetNTSCAntenna(MSrv_ATV_Database::MEDIUM eAntenna);
//------------------------------------------------------------------------------
/// -Set Antenna Type Cable or Air
/// @param eAntenna \b In: Air or Cable
/// @return TRUE or FALSE
//------------------------------------------------------------------------------
    virtual BOOL SetAntennaType(MSrv_ATV_Database::MEDIUM eAntenna);
    //------------------------------------------------------------------------------
    /// -Get NTSC Antenna Type
    /// @return \b OUT: MSrv_ATV_Database::MEDIUM (cable or air)
    /// @code
    ///
    ///     MEDIUM AntennaType = GetNTSCAntenna();
    ///
    /// @endcode
    //------------------------------------------------------------------------------
    virtual MSrv_ATV_Database::MEDIUM GetNTSCAntenna();
#endif
#if (ATSC_SYSTEM_ENABLE == 1)
    //------------------------------------------------------------------------------
    /// -Get ATV Start Channel Number
    /// @return U8 \b OUT: channel number
    /// @code
    ///
    ///     U8 u8StartChannelNumber = msAPI_ATVGetStartChannelNumber();
    ///     if(0==u8StartChannelNumber)
    ///     {
    ///         //handling
    ///     }
    ///     else
    ///     {
    ///         //handling
    ///     }
    ///
    /// @endcode
    //------------------------------------------------------------------------------
    virtual U8 msAPI_ATVGetStartChannelNumber(void);

    //------------------------------------------------------------------------------
    /// -Check If Is ATV Manual Scan
    /// @return                 \b OUT: True, or False
    /// @code
    ///
    ///     if(TRUE == msAPI_ATVCheckIsManualScan())
    ///     {
    ///         //handling here.
    ///     }
    ///
    /// @endcode
    //------------------------------------------------------------------------------
    virtual BOOL msAPI_ATVCheckIsManualScan(void);
#endif

    //------------------------------------------------------------------------------
    /// -This function will Update VIF parameter for factory menu
    /// @return None
    //------------------------------------------------------------------------------
    virtual void InitATVVIF(void);

    //------------------------------------------------------------------------------
    /// -This function will Update VIF parameter for factory menu //for back compaitbility , going to remove
    /// @return None
    //------------------------------------------------------------------------------
    virtual void UpdateVIFSetting(void);//for back compaitbility , going to remove

    //-------------------------------------------------------------------------------------------------
    /// set aspec ratio info
    /// @param     stVideoARCInfo \b IN: aspect ratio info
    /// @return  None
    /// @code
    ///
    ///     ST_MAPI_VIDEO_ARC_INFO stVideoARCInfo;
    ///
    ///     stVideoARCInfo.s16Adj_ARC_Left = 0;
    ///     stVideoARCInfo.s16Adj_ARC_Right = 0;
    ///     stVideoARCInfo.s16Adj_ARC_Up = 0;
    ///     stVideoARCInfo.s16Adj_ARC_Down = 0;
    ///     stVideoARCInfo.bSetCusWin = MAPI_FALSE;
    ///
    ///     SetAspectRatio(stVideoARCInfo)
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual void SetAspectRatio(const mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO &stVideoARCInfo);

    //-------------------------------------------------------------------------------------------------
    /// Get video aspect ratio.
    /// @param stVideoARCInfo  \b IN:  The structure for the video ARC info define
    /// @return None
    /// @code
    ///
    ///     //when in other source except atv and dtv, you can use it to init atv tuner,demod...
    ///     mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO stVideoARCInfo;
    ///
    ///     GetAspectRatio(stVideoARCInfo);
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual void GetAspectRatio(mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO &stVideoARCInfo);

    //-------------------------------------------------------------------------------------------------
    /// Set Finetune Overscan window.
    /// @param     stVideoARCInfo \b IN: aspect ratio info
    /// @return  None
    /// @code
    ///
    ///     mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO stVideoARCInfo;
    ///
    ///     GetAspectRatio(stVideoARCInfo);
    ///
    ///     FinetuneOverscan(&stVideoARCInfo);
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual void FinetuneOverscan(const mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO &stVideoARCInfo);

    //---------------------------------------------------------------------------
    /// To initialize ATV tuner ,demod...for other source except DTV.
    /// IN: void
    /// @return                 \b OUT: True, or False
    /// @code
    ///
    ///     //when in other source except atv and dtv, you can use it to init atv tuner,demod...
    ///
    ///     InitATVForOtherSource();
    ///
    /// @endcode
    //-----------------------------------------------------------------------
    virtual BOOL InitATVForOtherSource(void);

    //----------------------------------------------------------------------------
    /// get the flag of smart scan mode
    /// @param pbSmartScanMode \b OUT: *pbSmartScanMode=TRUE: smart scan mode enable; *pbSmartScanMode=FALSE:smart scan mode disable
    /// @return None
    //----------------------------------------------------------------------------------
    virtual void GetSmartScanMode(BOOL* const pbSmartScanMode);

    //----------------------------------------------------------------------------
    /// set the flag of smart scan mode
    /// @param bSmartScanMode \b IN: bSmartScanMode=TRUE: smart scan mode enable; bSmartScanMode=FALSE:smart scan mode disable
    /// @return None
    /// @code
    ///
    ///     //close the smart scan.
    ///
    ///     SetSmartScanMode(FALSE);
    ///
    /// @endcode
    //----------------------------------------------------------------------------------
    virtual void SetSmartScanMode(BOOL const bSmartScanMode);

    //----------------------------------------------------------------------------
    /// start even timer
    /// @return None
    /// @code
    ///
    ///     ST_ATV_EVENT_TIMER timer;
    ///
    ///     stAtvEventScan      m_stAtvScannedInfo;
    ///
    ///     StartEvenTimer();
    ///
    ///     GetEvenTimer(&timer);
    ///
    ///     StopEvenTimer();
    ///
    ///     PostEvent(0, EV_ATV_AUTO_TUNING_SCAN_INFO, (U32)m_stAtvScannedInfo);
    ///
    /// @endcode
    //----------------------------------------------------------------------------------
    virtual void StartEvenTimer();

    //----------------------------------------------------------------------------
    ///Get even timer
    ///@param pTimer \b OUT: *pTimer to recieve the timer
    ///@return None
    //----------------------------------------------------------------------------------
    virtual void GetEvenTimer(ST_ATV_EVENT_TIMER* const pTimer);

    //----------------------------------------------------------------------------
    ///stop even timer
    ///@return None
    //----------------------------------------------------------------------------------
    virtual void StopEvenTimer();

    //----------------------------------------------------------------------------
    ///get atv scan param
    ///@param pstAtvScanParam \b OUT:*pstAtvScanParam to recieve the Paramerter
    ///@return None
    /// @code
    ///
    ///     ST_ATV_SCAN_PARAMETER stScnParam;
    ///
    ///     GetAtvScanParam(&stScnParam);
    ///
    ///     printf("stScnParam.u32EventInterval = %d\n",stScnParam.u32EventInterval);
    ///     printf("stScnParam.u32StartFrequency = %d\n",stScnParam.u32StartFrequency);
    ///     printf("stScnParam.u32EndFrequency = %d\n",stScnParam.u32EndFrequency);
    ///     printf("stScnParam.u8ScanType = %d\n",stScnParam.u8ScanType);
    ///     printf("stScnParam.u8ScanState = %d\n",stScnParam.u8ScanState);
    ///     printf("stScnParam.u16TotalChannelNum = %d\n",stScnParam.u16TotalChannelNum);
    ///     printf("stScnParam.aATVMannualTuneMode = %d\n",stScnParam.aATVMannualTuneMode);
    ///
    /// @endcode
    //----------------------------------------------------------------------------------
    virtual void GetAtvScanParam(ST_ATV_SCAN_PARAMETER* const pstAtvScanParam);

    //------------------------------------------------------------------------------
    /// -This function will Stop Channel Display
    /// @return                 \b OUT: True, or False
    /// @code
    ///
    ///     if(TRUE == DisableChannel())
    ///     {
    ///         //handling here.
    ///     }
    ///
    /// @endcode
    //------------------------------------------------------------------------------
    virtual BOOL DisableChannel(void);

    //------------------------------------------------------------------------------
    /// -This function will enable Channel Display
    /// @return                 \b OUT: True, or False
    /// @code
    ///
    ///     if(TRUE == EnableChannel())
    ///     {
    ///         //handling here.
    ///     }
    ///
    /// @endcode
    //------------------------------------------------------------------------------
    virtual BOOL EnableChannel(void);

#if (MSTAR_TVOS == 1)
    //------------------------------------------------------------------------------
    /// -This function will Set Country
    /// @param enCountry \b IN: Country enum
    /// @return None
    /// @code
    ///
    ///     SetCountry(E_UK);
    ///
    /// @endcode
    //------------------------------------------------------------------------------
    virtual void SetCountry(MEMBER_COUNTRY enCountry);

    //------------------------------------------------------------------------------
    /// -This function will Set Factory Atv Program Data
    /// @param u8AtvProgramIndex \b IN: Program index
    /// @param u32FrequencyKHz \b IN: Frequency in KHz
    /// @param u8AudioStandard \b IN: AudioStandard Type
    /// @param u8VideoStandard \b IN: VideoStandard Type
    /// @return None
    /// @code
    ///
    ///     //reset atv database data to factory mode.
    ///
    ///     ResetFactoryAtvProgramData(u8AtvProgramIndex, u32FrequencyKHz, u8AudioStandard, u8VideoStandard);
    ///
    /// @endcode
    //------------------------------------------------------------------------------
    virtual void ResetFactoryAtvProgramData(U8 u8AtvProgramIndex, U32 u32FrequencyKHz, U8 u8AudioStandard, U8 u8VideoStandard);

    //-------------------------------------------------------------------------------------------------
    /// Set each Channel Volume Compensation
    /// @param eChlVolumeComState   \b IN: channel volume compensation state
    /// @param u16ProgramNumber    \b IN: program number
    /// @return  None.
    /// @code
    ///
    ///     SetChannelVolumeCompensation(E_CURRENT_CHNNEL_VOLUME_COMPENSATION);
    ///
    ///     SetChannelVolumeCompensation(E_NEXT_CHNNEL_VOLUME_COMPENSATION);
    ///
    ///     SetChannelVolumeCompensation(E_PRE_CHNNEL_VOLUME_COMPENSATION);
    ///
    ///     SetChannelVolumeCompensation(E_SEL_CHNNEL_VOLUME_COMPENSATION);
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual void SetChannelVolumeCompensation(EN_CHANNEL_VOLUME_COM_STATE eChlVolumeComState, U16 u16ProgramNumber = 0xFFFF);
#endif

    //-------------------------------------------------------------------------------------------------
    /// API to set m_bIsDoSetMTSMode True or False.
    /// If user press MTS key, m_bIsDoSetMTSMode should be true
    /// until MTS mode set to database.
    /// @param bFlag   \b IN: Dose user press MTS key to shift MTS mode or not
    /// @return  None.
    //-------------------------------------------------------------------------------------------------
    virtual void SetMTSModeFlag(BOOL bFlag);

#if ENABLE_CUSTOMER_ATS_TABLE
    //-------------------------------------------------------------------------------------------------
    /// CNI Call back function.
    /// @return U16 \b OUT:cni ExtTable pointer
    /// @code
    ///
    ///     mapi_cni* pCniInstance = mapi_interface::Get_mapi_cni();
    ///
    ///     if(pCniInstance)
    ///     {
    ///              pCniInstance->InstallCallback_CNI_Cus_GetExtATSTable(CNI_Cus_GetExtATSTableCallBack);
    ///     }
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    static U16 *CNI_Cus_GetExtATSTableCallBack(void);
#endif

#if (HBBTV_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// HbbTV Service Request command handler.
    /// @param  hbbtvCommand              \b IN: Object pointer
    /// @return                 \b OUT: True, or False
    //-------------------------------------------------------------------------------------------------
    virtual bool HBBTV_ServiceRequestHandler(void*);
#endif

#if( TV_FREQ_SHIFT_CLOCK )
    //-------------------------------------------------------------------------------------------------
    /// API to switch Tuner Shift Clock on/off
    /// @param bEnable   \b IN: enable frequency shift or not
    /// @return  None.
    //-------------------------------------------------------------------------------------------------
    void msAPI_Tuner_Patch_TVShiftClk(BOOL bEnable);
#endif
#if (CHANNEL_CHANGE_FREEZE_IMAGE_BYDFB_ENBALE == 1)
    void ChannelChangeFreezeImage(BOOL bEnable);
#endif

    //-------------------------------------------------------------------------------------------------
    /// Refresh window.
    /// @return                 \b <OUT> : NONE
    //-------------------------------------------------------------------------------------------------
    void RefreshWindow();

#if (INPUT_SOURCE_LOCK_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Set middleware enable/disable
    /// @param bEnable: middleware status, MAPI_TRUE: enable, MAPI_FALSE: disable
    /// @return MAPI_TRUE: set middleware status success
    /// @return MAPI_FALSE: set middleware status fail
    //-------------------------------------------------------------------------------------------------
    virtual MAPI_BOOL EnableMiddleware(MAPI_BOOL bEnable);
#endif

// ****************************************************************
// Protected
// ****************************************************************

protected:
    //-------------------------------------------------------------------------------------------------
    /// get tuning status
    /// @return MSrv_ATV_Player::TUNING_STATUS
    //-------------------------------------------------------------------------------------------------
    virtual MSrv_ATV_Player::TUNING_STATUS _GetTuningStatus(void);

#if ( TV_FREQ_SHIFT_CLOCK )
    //-------------------------------------------------------------------------------------------------
    /// Shift clock of audio, vif, and vd driver for the assigned shift mode
    /// @param u8Mode   \b IN: frequency mode to be switched
    /// @return  None.
    //-------------------------------------------------------------------------------------------------
    void _TVShiftClk(MAPI_AVD_ATV_CLK_TYPE u8Mode);

    //-------------------------------------------------------------------------------------------------
    /// Get the shift mode according to input frequency
    /// @param u32Freq   \b IN: atv frequency
    /// @return  Shift Mode
    //-------------------------------------------------------------------------------------------------
    MAPI_AVD_ATV_CLK_TYPE _Get_Shift_Mode(U32 u32Freq);

    //-------------------------------------------------------------------------------------------------
    /// Set Shift mode accroding to input frequency for particular tuners
    /// @param u32Freq   \b IN: atv frequency
    /// @return  None.
    //-------------------------------------------------------------------------------------------------
    void _Set_Shift_Freq(U32 u32Freq);
#endif

    /// member demodulator
    mapi_demodulator  *m_pDemodulator;
    /// is pre program disabled
    BOOL             m_bIsPreProgramDisabled;

// ****************************************************************
// Private
// ****************************************************************
private:
    #if (ISDB_SYSTEM_ENABLE == 1)
    /// Auto Choose AudioMode
    BOOL  bForceCheckAudioMode;
    #endif
    #if (ATSC_SYSTEM_ENABLE == 1)
    U16                 m_u16ScreenSaverCounter;
    BOOL                m_bIsMTSMonitorEnabled;
    #endif
    #if (ESASIA_NTSC_SYSTEM_ENABLE == 1)//Add for MTS detection issue: 0660220 20140710EL
    BOOL                m_bIsMTSMonitorEnabled;
    #endif

    MSrv_ATV_Database::MEDIUM m_Antenna;
    BOOL                m_bFlagThreadSignalMonitor_Active;
    ST_ATV_SCAN_PARAMETER  m_stAtvScanParam;
    pthread_mutex_t     m_mutex_ProgramChangeProcess;
    EN_CHANNEL_SEARCH_TYPE m_eChannelSearchType;
    BOOL                m_bIsAFTNeeded;
    BOOL                m_bIsLSearch;
    U16                 m_u16UhfMaxPll;
    U16                 m_u16UhfMinPll;
    U16                 m_u16VhfLowMinPll;
    U16                 m_u16VhfHighMinPll;
    U16                 m_u16DefaultPll;

#if (TTX_ENABLE == 1)
    MW_TTX             *m_pcTTX;
#endif

    // ------------------------------------------------------------
    // Variable Define
    // ------------------------------------------------------------

    mapi_tuner  *m_pTuner;

    pthread_t m_threadSignalMonitor_id;
    pthread_t m_threadSendScanInfo_id;

    eAFTSTEP    m_eCurrentTuningState;
    MSrv_ATV_Database::MEDIUM      m_eMedium;
    U8          m_u8ChannelNumber;
    U16         m_u16TunerPLL;
    U16         m_u16IdleTimer;
    U32         m_u32IdleTimer;
    U8          m_au8CurrentStationName[MAX_STATION_NAME];
    U8          m_u8AftOffset;
    U8          m_u8AftOffset_pre;
    U8          m_u8AftOffset_saved;
    U8          m_u8AftStep;
    U8          m_u8StartChangeProgram;
    U8          m_ManualScanRtUpdateVDandScaler;
    U8          m_u8ChangeProgramvifreset;
    BOOLEAN     m_bCurrentProgramBlock;
    U32         m_u32StartTime;
    U16         m_u16IfFreqPre;
    BOOL        m_bProgInfoChg;
    U8          m_u8MtsStatus;
    BOOL        m_bInitAtvDemodTuner;
    U32         m_u32PostEventIntervalTime;
    U32         m_u32UserMtsSettingIntervalTime;
    U32         m_u32DelayAudioUnmuteTime;
    BOOL        m_bIsDoSetMTSMode;

    // FIXME: this variable should be refined in new scan architecture
    BOOLEAN     m_TV_SCAN_PAL_SECAM_ONCE;

    //Smart Scan flag
    BOOL        m_bSmartScan;

    // Timer for even interval
    ST_ATV_EVENT_TIMER m_stAtvEvenTimer;
    U8          m_u8ScreenModeStatus;//avoid postevent too much
    /// re-send flag
    BOOL m_bReSendEvent;

    /// before first set channel when switch from other
    BOOL m_bBeforeFirstSetChannel;

    /// need reload PQ
    BOOL m_bNeedReloadPQ;

    /// the scan event information
    stAtvEventScan      m_stAtvScannedInfo;

    /// the mutex for scan
    pthread_mutex_t     m_mutex_Scan;
    MAPI_AVD_VideoStandardType m_CurVideoStandard;

    // ------------------------------------------------------------
    // Function Define
    // ------------------------------------------------------------
    virtual void _SetDefaultStationName(U8 *sStationName);
    virtual BOOLEAN _IsLPrime(void);
    virtual BOOLEAN _SetVifIfFreq(void);
    virtual void _SetTunerPLL(U16 u16PLL);
    virtual void _DetectStationName(void);
    virtual void msAPI_FrontEnd_Init(void);
    virtual BOOLEAN msAPI_Tuner_IsTuningProcessorBusy(void);
    virtual void msAPI_Tuner_AdjustUnlimitedFineTune(DIRECTION eDirection);
    virtual void msAPI_Tuner_SetIF(void);
    virtual void msAPI_Tuner_ConvertMediumAndChannelNumberToString(MSrv_ATV_Database::MEDIUM eMedium, U8 u8ChannelNumber, U8 * sStationName);
    virtual void msAPI_Tuning_IsScanL(BOOLEAN bEnable);


    virtual void CheckSaveAftOffsetValue(void);
    virtual void _msAPI_Tuning_AutoFineTuning(void);
    virtual void msAPI_CFT_GetMinMaxChannel(MSrv_ATV_Database::MEDIUM eMedium, U8 * pcMin, U8 * pcMax);
    virtual U32 ConvertPLLtoFrequencyKHZ(U16 wPLL);
    virtual U16 msAPI_CFT_ConvertPLLtoIntegerOfFrequency(U16 wPLL);
    virtual U16 msAPI_CFT_ConvertPLLtoFractionOfFrequency(U16 wPLL);
    virtual U16 ConvertFrequncyHzToPLL(U32 u32FreqHz);
    virtual U8 GetFreqChannelTable(ST_FREQ_CHANNEL ** ppFreqChannel);
    virtual U16 ConvertPLLtoCompressedFrequency(U16 wPLLData);
    static void* threadSignalMonitor(void *arg);
    static void* threadSendScanInfo(void *arg);
    virtual void ProgramChangeProcess(void);

    virtual BOOL CheckAudioStandardChange(void);
    virtual void CalWSSWin(mapi_vd_datatype::ASPECT_RATIO_TYPE enWSSARCType, mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *ptWinType, mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO *ptARCInfo);
    virtual void UpdateVDandScaler(void);
    virtual void ATVProc_Handler(void);
    virtual void UpdateMediumStatus(void);
    #if (ATSC_SYSTEM_ENABLE == 1)
    //void ScreenSaverMonitor(void);
    #if (VCHIP_ENABLE == 1)
    virtual BOOL IsTVSourceBlock(void);
    #endif
    virtual void DefaultMTSSelection(void);
    virtual void CurrentMTSMonitor(void);
    #endif
    #if (ESASIA_NTSC_SYSTEM_ENABLE == 1 || ISDB_SYSTEM_ENABLE == 1)//Add for MTS detection issue: 0660220 20140710EL
    virtual void CurrentMTSMonitor(void);
    #endif
    #if (ISDB_SYSTEM_ENABLE == 1)
    //------------------------------------------------------------------------------
    /// -User changes MTS by himslef for Brazil
    /// @return \none
    //------------------------------------------------------------------------------
    virtual void DefaultMTSSelection_Brazil(void);

    #endif

    //------------------------------------------------------------------------------
    /// -Enable Scaler
    /// @return \none
    //------------------------------------------------------------------------------
    virtual void EnableScaler(void);

    virtual void RecoverUserMtsSetting(void);
    virtual EN_SET_CHANNEL_ERROR_CODE StartChangeChannel(U8 u8ChannelNumber, BOOL bCheckBlock);

    virtual void ATVGetMediumAndChannelNumber(U8 u8ProgramNumber, MSrv_ATV_Database::MEDIUM * peMedium, U8 * pu8ChannelNumber);
    virtual void ATVSetMediumAndChannelNumber(U8 u8ProgramNumber, MSrv_ATV_Database::MEDIUM eMedium, U8 u8ChannelNumber);

 #if ENABLE_V_RANGE_HANDLE_ATV
    virtual void  MApp_VD_RangeReset(void);
    virtual void  MApp_VD_SyncRangeHandler(void);
    virtual void  MApp_VD_StartRangeHandle(void);
    //void  MApp_XC_check_crop_win( XC_SETWIN_INFO *pstXC_SetWin_Info );
    //void  MApp_Scaler_GetWinInfo(XC_SETWIN_INFO* pWindowInfo, SCALER_WIN eWindow);
    //void  MApp_Scaler_SetCustomerWindow(MS_WINDOW_TYPE *ptSrcWin, MS_WINDOW_TYPE *ptCropWin, MS_WINDOW_TYPE *ptDstWin, SCALER_WIN eWindow);
 #endif

    MW_ATV_Scan *mScan;

    ST_MAPI_VIDEO_WINDOW_INFO **m_pVptr;
    virtual void SetOverscanFromDB(ST_MAPI_VIDEO_WINDOW_INFO **vptr, BOOL bSetAllDataFromDB = TRUE);

    //-------------------------------------------------------------------------------------------------
    /// To initialize the video of ATV player
    /// @param eInputType   \b IN: input source type
    /// @param eWin                \b IN: the output display window
    /// @return                 \b OUT: True, or False
    //-------------------------------------------------------------------------------------------------
    virtual BOOL DoVideoInit(MAPI_INPUT_SOURCE_TYPE eInputType, MAPI_SCALER_WIN eWin=MAPI_MAIN_WINDOW);

    //-------------------------------------------------------------------------------------------------
    /// post-video init for the player
    /// @return  \b TRUE: init OK, FALSE: otherwise
    //-------------------------------------------------------------------------------------------------
    virtual BOOL _PostVideoInit();

    //-------------------------------------------------------------------------------------------------
    /// To finalize the ATV player
    /// @return                 \b OUT: True, or False
    //-------------------------------------------------------------------------------------------------
    virtual BOOL _Finalize();

    //-------------------------------------------------------------------------------------------------
    /// This function will check is medium and channel valid
    /// @param eMedium \b IN: Medium
    /// @param cChannelNumber \b IN: Channel Number
    /// @return                     \b OUT: True or False
    //-------------------------------------------------------------------------------------------------
    virtual BOOLEAN CFTIsValidMediumAndChannel(MSrv_ATV_Database::MEDIUM eMedium, U8 cChannelNumber);

    //-------------------------------------------------------------------------------------------------
    /// This function will Get Medium type
    /// @param wChannelPLLData \b IN: Channel PLL Data
    /// @return MEDIUM: Medium type
    //-------------------------------------------------------------------------------------------------
    virtual MSrv_ATV_Database::MEDIUM CFTGetMedium(U16 wChannelPLLData);

    //-------------------------------------------------------------------------------------------------
    /// This function is called to get current channel PLL.
    /// @return U16: current PLL value of tuner.
    //-------------------------------------------------------------------------------------------------
    virtual U16 TunerGetCurrentChannelPLL(void);

    //-------------------------------------------------------------------------------------------------
    /// This function is called to get current tuning interface.
    /// @return MEDIUM: MEDIUM_CABLE or MEDIUM_AIR.
    //-------------------------------------------------------------------------------------------------
    virtual MSrv_ATV_Database::MEDIUM TunerGetMedium(void);
#if 0
    //-------------------------------------------------------------------------------------------------
    /// This function will Get Channel Number
    /// @param wChannelPLLData \b IN: wChannelPLLData
    /// @return U8: Channel Number
    //-------------------------------------------------------------------------------------------------
    //U8 CFTGetChannelNumber(U16 wChannelPLLData);
    //------------------------------------------------------------------------------
    /// get the band type of the PLL
    /// @param u16PLL \b In: PLL value
    /// @return Band
    //------------------------------------------------------------------------------
    virtual RFBAND GetBand(U16 u16PLL);
#endif
    //-------------------------------------------------------------------------------------------------
    /// Check whether the current play frequency channel is same to the stored frequency channel.
    /// @return                                         \b OUT: True or False
    //-------------------------------------------------------------------------------------------------
    virtual BOOLEAN TunerIsCurrentChannelAndSavedChannelSame(void);

};
/*@ </Definitions> @*/







/*@ <IncludeGuardEnd> @*/
#endif
/*@ </IncludeGuardEnd> @*/
