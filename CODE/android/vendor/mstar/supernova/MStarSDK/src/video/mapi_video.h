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

///////////////////////////////////////////////////////////////////////////////////////////////////
/// @file mapi_video.h
/// @brief \b Introduction: mapi_video module Description.
///
///   It provides easy interface for customer to set the Scaler.
///
///   For example: SetMode, SetWindow
///   and it also provides some basic Scaler functions.
///
/// @image html Scaler_Video_1.JPG "AV Player access mapi_video class"
///   For example: FreezeImage, SetVideoMute
/// @author MStar Semiconductor Inc.
///
/// Features:
/// - To set the Scaler with interfaces SetMode and SetWindow.
/// - Provide FreezeImage function.
/// - Provide set mute screen function
///
///////////////////////////////////////////////////////////////////////////////////////////////////



#ifndef _MAPI_VIDEO_H_
#define _MAPI_VIDEO_H_

#include <pthread.h>
#include "mapi_base.h"
//#include "mapi_pql.h"
#include "mapi_syscfg_table.h"
#include "mapi_video_datatype.h"
#include "mapi_timing_datatype.h"
#include "MsTypes.h"
#include "drvXC_IOPort.h"
#include "apiXC.h"

#include "UFO.h"


// Forward Declaration
class mapi_video_cfg_data;
class mapi_pql;
struct MAPI_PQL_CALIBRATION_DATA;

/// class: The functionality of mapi_video is about "video setting(scaler)".
class DLL_PUBLIC mapi_video : public mapi_base
{
public:
    //------------------------------------------------------------------------------
    /// Constructor
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC mapi_video();

    //------------------------------------------------------------------------------
    /// Destructor
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual ~mapi_video();

    /// FRC 3D PANEL TYPE
    typedef enum
    {
        ///3D Panel none
        E_3D_PANEL_NONE,
        ///240hz panel, which can process updown, leftright,vertical or horizontal line weave
        E_3D_PANEL_SHUTTER,
        ///120hz panel, which can only process horizontal line weave
        E_3D_PANEL_PELLICLE,
        //120hz 4K1K panel, which can process updown, leftright,vertical or horizontal line weave
        E_3D_PANEL_4K1K_SHUTTER,
        ///3D Panel count
        E_3D_PANEL_MAX,
    } E_3D_PANEL_TYPE;

    //------------------------------------------------------------------------------
    /// Set the Display window info, and lock it
    /// @param bEnable                  \b IN:TRUE/FALSE
    /// @param pstDispWin                    \b IN: the select window size
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void LockDispWindowSize(const MAPI_BOOL bEnable, const mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstDispWin)=0;

    //------------------------------------------------------------------------------
    /// Lock display window forever
    /// @param bEnable                  \b IN:TRUE/FALSE
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL LockDispWindowForever(const MAPI_BOOL bEnable)=0;

    //------------------------------------------------------------------------------
    /// Set panel timing
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void SetPanelTiming(void)=0;

    //------------------------------------------------------------------------------
    /// Set freerun info for do panel timing using
    /// @param pstVideoStatus              \b IN: the timing info
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void SetFreerunInfo(mapi_video_datatype::ST_MAPI_VIDEO_STATUS *pstVideoStatus) = 0;

    //------------------------------------------------------------------------------
    /// To initiate this module
    /// @param enInputSourceType    \b IN: input source type
    /// @param enDstWin             \b IN: output window
    /// @param ptDispWin            \b IN: output window info
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void Initialize(MAPI_INPUT_SOURCE_TYPE enInputSourceType, MAPI_SCALER_WIN enDstWin, mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *ptDispWin=NULL) = 0;

    //------------------------------------------------------------------------------
    /// To configure video data
    /// @param pData                \b IN: video data
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL SetMode(mapi_video_cfg_data *pData) = 0;

    //------------------------------------------------------------------------------
    /// keep op timing as 4k2k
    /// @param bEnable \b IN: whether set op timing 4k2k
    /// @return        \b MAPI_TRUE:set Op timing 4k2k, MAPI_FALSE: not supported or failed
    //------------------------------------------------------------------------------
    DLL_PUBLIC static MAPI_BOOL KeepOpTiming4k2k(const MAPI_BOOL bEnable);
#if (STB_ENABLE == 0)
    //------------------------------------------------------------------------------
    /// set OP timing & FRC output timing 2k1k/4k2k
    /// @param enFRC_InputTiming \b IN: 2k1k/4k2k
    /// @return        \b MAPI_TRUE:set Op timing 4k2k, MAPI_FALSE: not supported or failed
    //------------------------------------------------------------------------------
    DLL_PUBLIC static MAPI_BOOL SetFRCInputTiming(E_XC_FRC_InputTiming enFRC_InputTiming);
#endif

    //-------------------------------------------------------------------------------------------------
    /// Set FRC Window
    /// @param  e3dInputMode               \b IN: @ref E_XC_3D_INPUT_MODE
    /// @param  e3dOutputMode               \b IN: @ref E_XC_3D_OUTPUT_MODE
    /// @param  e3dPanelType               \b IN: @ref E_XC_3D_PANEL_TYPE
    //-------------------------------------------------------------------------------------------------
    DLL_PUBLIC static void SetFRCWindow(E_XC_3D_INPUT_MODE e3dInputMode, E_XC_3D_OUTPUT_MODE e3dOutputMode, E_XC_3D_PANEL_TYPE e3dPanelType);

    //------------------------------------------------------------------------------
    /// Query if currently pip mode or not
    /// @return   \b MAPI_TRUE:Pip Mode, MAPI_FALSE: not Pip Mode
    //------------------------------------------------------------------------------
    DLL_PUBLIC static MAPI_BOOL IsPipMode();

    //------------------------------------------------------------------------------
    /// To configure PIP Mode
    /// @param ePipMode                \b IN: Pip Mode
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC static void SetPipMode(EN_MAPI_PIP_MODES ePipMode);

    //------------------------------------------------------------------------------
    /// To set main and sub input source type for pip on case use
    /// @param eMainInputSrc                \b IN: main input source type
    /// @param eSubInputSrc                \b IN: sub input source type
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC static void SetPipMainSubInputSourceType(MAPI_INPUT_SOURCE_TYPE eMainInputSrc, MAPI_INPUT_SOURCE_TYPE eSubInputSrc);

    //------------------------------------------------------------------------------
    /// To get main and sub input source type according scaler win for pip on case use.
    /// @param eWin                \b IN: main or sub xc win
    /// @return MAPI_INPUT_SOURCE_TYPE          \b OUT: detected 3d format
    //------------------------------------------------------------------------------
    DLL_PUBLIC static MAPI_INPUT_SOURCE_TYPE GetPipInputSourceType(MAPI_SCALER_WIN eWin);

    //-------------------------------------------------------------------------------------------------
    /// Query the capability of the 'enEngineType'
    /// @param  enEngineType      \b IN: which engine's caps you want to get
    /// @param  pstTravelingCaps  \b OUT: engine's caps
    /// @return                   \b EN_TRAVELING_RETURN
    //-------------------------------------------------------------------------------------------------
    DLL_PUBLIC static EN_TRAVELING_RETURN GetTravelingEngineCaps(ST_TRAVELING_ENGINE_CAPS *pstTravelingCaps, EN_TRAVELING_ENGINE_TYPE enEngineType);

#if (TRAVELING_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Set Traveling mode with main/MM inputsource
    /// The MM will transmit to the remote cellphone/PAD
    /// @param pstTravelInfo  \b IN: The information structure for Traveing mode setting
    /// @return                 \b MAPI_BOOL
    //-------------------------------------------------------------------------------------------------
    DLL_PUBLIC virtual EN_TRAVELING_RETURN InitTravelingMode(ST_TRAVELING_MODE_INFO *pstTravelInfo, EN_TRAVELING_ENGINE_TYPE enEngineType) = 0;

    //-------------------------------------------------------------------------------------------------
    /// start to capture, when one frame is done, call back function will be called
    /// @param pTravelCallback  \b OUT: The information structure for Traveing mode setting
    /// @return                 \b MAPI_BOOL
    //-------------------------------------------------------------------------------------------------
    DLL_PUBLIC virtual EN_TRAVELING_RETURN StartTravelingMode(TRAVELMODEDATACALLBACK pTravelDataCallback, TRAVELMODEEVENTCALLBACK pTravelEventCallback, EN_TRAVELING_ENGINE_TYPE enEngineType=E_TRAVELING_ENGINE_TYPE_HD0) = 0;

    //-------------------------------------------------------------------------------------------------
    /// stop the travelling mode
    /// @return                 \b MAPI_BOOL
    //-------------------------------------------------------------------------------------------------
    DLL_PUBLIC virtual EN_TRAVELING_RETURN StopTravelingMode(EN_TRAVELING_ENGINE_TYPE enEngineType=E_TRAVELING_ENGINE_TYPE_HD0) = 0;

    //-------------------------------------------------------------------------------------------------
    /// Clear the frame that APP just waited on
    /// if clear, means APP has finish copying the data, and the memory will be writed on new coming data
    /// @return                 \b MAPI_BOOL
    //-------------------------------------------------------------------------------------------------
    DLL_PUBLIC virtual EN_TRAVELING_RETURN FrameProcessDone(MAPI_U32 u32Index, EN_TRAVELING_ENGINE_TYPE enEngineType) = 0;


    //------------------------------------------------------------------------------
    /// Set capture window and aspec ration
    /// @param stCapWin            \b IN: the setting of capture window
    /// @param ptARCInfo            \b IN: the aspect ration information
    /// @return                 \b MAPI_BOOL
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual EN_TRAVELING_RETURN SetTravelingWindow(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *stCapWin, const mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO *ptARCInfo, EN_TRAVELING_ENGINE_TYPE enEngineType=E_TRAVELING_ENGINE_TYPE_HD0) = 0;

    //----------------------------------------------------------------------------------------
    /// Open API of MuteTraveling function for MAPI_VIDEO, MSRV will use this function
    /// @param bEnable             \b IN: TRUE: Set Traveling off, FALSE: Set Traveing on
    /// @param enEngineType        \b IN: Traveling Engine Type
    /// @return void
    //----------------------------------------------------------------------------------------
    DLL_PUBLIC virtual void SetTravelingMute(MAPI_BOOL bEnable, EN_TRAVELING_ENGINE_TYPE enEngineType=E_TRAVELING_ENGINE_TYPE_HD0) = 0;

    //-------------------------------------------------------------------------------------------------
    /// Finalize the travelling mode
    /// @return                 \b MAPI_BOOL
    //-------------------------------------------------------------------------------------------------
    DLL_PUBLIC virtual EN_TRAVELING_RETURN FinalizeTravelingMode(EN_TRAVELING_ENGINE_TYPE enEngineType=E_TRAVELING_ENGINE_TYPE_HD0) = 0;
    //------------------------------------------------------------------------------
    /// Set Memory Traveling mode with info in ST_MEMORY_TRAVELING_INFO
    /// @param pstMemInfo          \b IN: memory info for traveling
    /// @param enEngineType        \b IN: engine type for traveling
    /// @return                    \b MAPI_BOOL
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual EN_TRAVELING_RETURN SetMemoryTravelingConfig(ST_MEMORY_TRAVELING_INFO *pstMemInfo,EN_TRAVELING_ENGINE_TYPE enEngineType=E_TRAVELING_ENGINE_TYPE_HD0) = 0;
#endif //(TRAVELING_ENABLE == 1)

    //-------------------------------------------------------------------------------------------------
    /// Init Dualview related XC settings
    /// @param enInputSourceType          \b IN: input source type
    /// @param pstCapWin                   \b IN: pointer to capture window
    /// @param pstDispWin                  \b IN: pointer to display window
    /// @return                 \b MAPI_BOOL
    //-------------------------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL InitDualViewWindow(MAPI_INPUT_SOURCE_TYPE enInputSourceType,
                                      mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstCapWin,
                                      mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstDispWin) = 0;

    //------------------------------------------------------------------------------
    /// To set mirror
    /// @param bEnable                  \b IN: enable mirror mode or not
    /// @param stARCInfo              \b IN: ARC data
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC void SetMirror(MAPI_BOOL bEnable ,mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO *stARCInfo);
    //------------------------------------------------------------------------------
    /// Set OSDC
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC void ResetOSDC(void);
#if 0//(XC_LOW_DELAY == 1)
    //------------------------------------------------------------------------------
    /// Set PQ info
    /// @param ptARCInfo            \b IN: the aspect ration information
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void PreSetPQInfo(const mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO *ptARCInfo, MAPI_INPUT_SOURCE_TYPE enInputSourceType, SCALER_WIN eWindow) = 0;
#endif

    //------------------------------------------------------------------------------
    /// Set window and aspec ration
    /// @param ptCropWin            \b IN: the setting of crop window
    /// @param ptDispWin            \b IN: the setting of display
    /// @param ptARCInfo            \b IN: the aspect ration information
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL SetWindow(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *ptCropWin, mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *ptDispWin, const mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO *ptARCInfo) = 0;

    ///------------------------------------------------------------------------------
    /// Set Pause then Zoom Patch
    /// When Pause, fd_mask is Enable, it will cause Zoom out function fail
    /// Patch method : 1. force full motion
    ///                2. force OPM read bank to original read bank
    ///                3. switch RFBL/FB mode
    ///                4. crop by mvop & IP=OP base & force mvop field
    ///                5. force clear STB_DC fb_mask
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void SetPauseZoomPatch(void) = 0;

    //------------------------------------------------------------------------------
    /// Set Motion Enable/Disable (need match with scaler MADi mode)
    /// When Pause  Zoom  Play , need disble motion to avoid garbage
    /// @param bEnable             \b IN: enable/disable  motion
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void SetMotionPatch(MAPI_BOOL bEnable) = 0 ;

    //------------------------------------------------------------------------------
    /// Get window info. capture, crop, display window.
    /// Provide developer access to current low-level window information.
    /// This helps developer implemnt customized overscan, preview-like window.
    /// @param pCapWin             \b IN: the setting of capture window
    /// @param pCropWin            \b IN: the setting of crop window
    /// @param pDispWin            \b IN: the setting of display
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void GetWindowInfo(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pCapWin, mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pCropWin, mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pDispWin) = 0;

    //------------------------------------------------------------------------------
    /// Set window info. capture, crop, display window.
    /// Provide developer access to current low-level window information.
    /// This helps developer implemnt customized overscan, preview-like window.
    /// @param pCapWin             \b IN: the setting of capture window
    /// @param pCropWin            \b IN: the setting of crop window
    /// @param pDispWin            \b IN: the setting of display
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void SetWindowInfo(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pCapWin, mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pCropWin, mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pDispWin) = 0;

    //------------------------------------------------------------------------------
    /// To finalize this module
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void Finalize(void) = 0;

    //------------------------------------------------------------------------------
    /// To freeze image
    /// @param bEnable              \b IN: enable/disable freeze image
    /// @param bIsTimeShiftSeamlessMode              \b IN: bIsTimeShiftSeamlessMode, default value: MAPI_FALSE
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void FreezeImage(MAPI_BOOL bEnable, MAPI_BOOL bIsTimeShiftSeamlessMode = MAPI_FALSE) = 0;

    //------------------------------------------------------------------------------
    /// Check image freeze or Not
    /// @return MAPI_BOOL
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL  IsFreezeImage(void) = 0;

    //------------------------------------------------------------------------------
    /// Wrapper function of ResolutionRemapping at Mapi_comp_video
    /// @param pu8Res            \b    OUT:    the return value of ResolutionRemapping
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void GetYPbPrResolutionRemapping(MAPI_U8* pu8Res) = 0;

    //------------------------------------------------------------------------------
    /// Wrapper function of ResolutionRemapping at Mapi_hdmi_video
    /// @param pu8Res            \b    OUT:    the return value of ResolutionRemapping
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void GetHDMIResolutionRemapping(MAPI_U8* pu8Res) = 0;

    //------------------------------------------------------------------------------
    /// Wrapper function of GetHdmiInputFreq at Mapi_hdmi_video
    /// @return MAPI_U16
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_U16 GetHdmiInputFreq() = 0;

    //------------------------------------------------------------------------------
    /// SetHprescalingPermission for bandwidth
    /// @param bEnable
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void SetHprescalingPermission(MAPI_BOOL bEnable) = 0;
    //------------------------------------------------------------------------------
    /// Get satus of prescalingPermission
    /// @return MAPI_BOOL
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL GetHprescalingPermission(void) = 0;


#if (STB_ENABLE == 1)
//------------------------------------------------------------------------------
/// Set Scaler Y8M4 mode
/// if Samba mode is ture, it is must in Y8M4 mode for bandwidth issue.
/// @param bEnable  \b IN: enable/disable Y8M4 mode
/// @return None
//------------------------------------------------------------------------------
    DLL_PUBLIC virtual void  SetY8M4Mode(MAPI_BOOL bEnable) = 0;

//------------------------------------------------------------------------------
/// Check PQ is Y8M4 mode or Not
/// When scaler buf < 9M , Y8M4 is true
/// @return MAPI_BOOL   \b TRUE : success , FALSE : Failed
//------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL  IsY8M4mode(void) = 0;
#endif

    //------------------------------------------------------------------------------
    /// set mute color
    /// @param pEngine                                      \b IN: mute engine
    /// @param u32MuteColor                                 \b IN: mute color
    /// @return MAPI_BOOL                                   \b OUT: return error code
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL setMuteColor(mapi_video_datatype::MAPI_VIDEO_Screen_Mute_Color u32MuteColor, E_MUTE_ENGINE pEngine = MUTE_ENGINE_XC) = 0;
    //------------------------------------------------------------------------------
    /// set frame color
    /// @param u32MuteFrameColor                            \b IN: frame color
    /// @param pEngine                                      \b IN: mute engine
    /// @return MAPI_BOOL                                   \b OUT: return error code
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL setFrameColor(mapi_video_datatype::MAPI_VIDEO_Screen_Mute_Color u32MuteFrameColor,E_MUTE_ENGINE pEngine = MUTE_ENGINE_XC) = 0;

    //------------------------------------------------------------------------------
    /// Open API of MuteScreen function for MAPI_VIDEO, MSRV will use this function
    /// @param bEnable             \b IN: TRUE: Set Screen off, FALSE: Set Screen on
    /// @param u16WaitToDoTime      \b IN: delay time, video mute on/off will delay u16VideoUnMuteTime ms.
    /// @param engine               \b IN: Mute engine
    /// @return \b MAPI_BOOL
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL SetVideoMute(MAPI_BOOL bEnable, MAPI_U32 u16WaitToDoTime = 0, E_MUTE_ENGINE engine = MUTE_ENGINE_XC) = 0;

    //------------------------------------------------------------------------------
    /// To enable video discontinous mode
    /// @param bEnable                  \b IN: enable/disable video discontinous mode
    /// @param enWin                    \b IN: MAIN or SUB WIN
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void EnableVideoDiscontinousMode(MAPI_BOOL bEnable, MAPI_SCALER_WIN enWin) = 0;

    //------------------------------------------------------------------------------
    /// get Mute flag or count
    /// @param engine                          \b IN: mute engine
    /// @param enScalerWinType                  \b IN: main window or sub
    /// @return MAPI_U32                        \b OUT: return count
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_U32 getMuteFlag(E_MUTE_ENGINE engine, MAPI_SCALER_WIN enScalerWinType) = 0;

    //------------------------------------------------------------------------------
    /// Set video mute on/off
    /// @param bEnable              \b IN: enable/disable video mute
    /// @param enColor              \b IN: mute color
    /// @param u16VideoUnMuteTime  \b IN: delay time, video mute on/off will delay u16VideoUnMuteTime ms.
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void SetVideoMute(MAPI_BOOL bEnable, mapi_video_datatype::MAPI_VIDEO_Screen_Mute_Color enColor, MAPI_U16 u16VideoUnMuteTime) = 0;

    //------------------------------------------------------------------------------
    /// Lock or Unlock Mute status
    /// @param bEnable                  \b IN: TRUE: lock Mute status
    /// @param type                     \b IN: Lock type
    /// @return NONE
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL LockMuteStatus(const MAPI_BOOL bEnable, E_MUTE_LOCK type) = 0;
    //------------------------------------------------------------------------------
    /// set Wait ms to fill xc buffer
    /// @param u16WaitMs                          \b IN: wait time
    /// @return none
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void setWaitMs2fillXCBuffer(MAPI_U16 u16WaitMs) = 0;
    //------------------------------------------------------------------------------
    /// change internal setup
    /// @param bInternal                          \b IN: true: follow Internal setup
    /// @return none
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void setInternalFlow(MAPI_BOOL bInternal) = 0;
    //------------------------------------------------------------------------------
    /// get Wait ms to fill xc buffer
    /// @return MAPI_U8                        \b OUT: return value
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_U16 getWaitMs2fillXCBuffer(void) = 0;
    //-------------------------------------------------------------------------------------------------
    /// Query whether Screen is black video enabled or not
    /// @return @ref MS_BOOL
    //-------------------------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL IsVideoMute(void) = 0;

    //-------------------------------------------------------------------------------------------------
    /// Query whether Screen is black video enabled or not
    /// @param engine                           \b IN: Mute engine
    /// @param eWIN                             \b IN: Main or sub window
    /// @return @ref MS_BOOL
    //-------------------------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL IsVideoMute(E_MUTE_ENGINE engine, MAPI_SCALER_WIN eWIN) = 0;

    //------------------------------------------------------------------------------
    /// Show current mute state (include Lock status)
    /// @return NONE
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void PrintCurrentMuteState(void) = 0 ;
//-------------------------------------------------------------------------------------------------
/// Capture live screec by DWIN
/// @param u8Type \b IN : 0 UV7Y8, 1 UV8Y8, 2 ARGB8888, 3 RGB565.
/// @param u32BufAddr \b IN : buffer address for capture useage, must be physical address and in MIU0!!!
/// @param u32BufSize \b IN : buffer size for capture useage
/// @param pu32Width \b OUT : capture width
/// @param pu32Height \b OUT : capture height
/// @return MAPI_BOOL   \b TRUE : success , FALSE : Failed
//-------------------------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL CaptureScreen(MAPI_U8 u8Type, MAPI_U32 u32BufAddr, MAPI_U32 u32BufSize, MAPI_U32 * pu32Width, MAPI_U32 *pu32Height);

//-------------------------------------------------------------------------------------------------
/// Capture live screec by DWIN and save it as file .
/// @param logo_name \b IN : Logo file name.
/// @return MAPI_BOOL   \b TRUE : success , FALSE : Failed
//-------------------------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL CaptureScreenToJpeg(char *logo_name);

    /// Capture mvop output by DWIN
    /// @param efmt \b IN : 0 UV7Y8, 1 UV8Y8, 2 ARGB8888, 3 RGB565.
    /// @param bInterlaced \b IN : input is interlaced or not
    /// @param u32BufAddr \b IN : buffer address for capture useage
    /// @param u32BufSize \b IN : buffer size for capture useage
    /// @param pstDwinWindow \b OUT : capture window
    /// @return MAPI_BOOL   \b TRUE : success , FALSE : Failed
    DLL_PUBLIC MAPI_BOOL CaptureMVopOutput(const mapi_video_datatype::EN_MAPI_GOP_DWIN_DATA_FMT efmt, const MAPI_BOOL bInterlaced, const MAPI_U32 u32BufAddr, const MAPI_U32 u32BufSize, mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE* const pstDwinWindow);

    //------------------------------------------------------------------------------
    /// To GetDisplayInfo
    /// @return MAPI_U16
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_U16 GetDisplayInfo(void) = 0;

    //------------------------------------------------------------------------------
    /// Wait numbers of v-sync, this function will block
    /// @param u8NumVSyncs          \b IN: numbers of v-sync to wait
    /// @param u16Timeout           \b IN: timeout counter
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void WaitOutputVSync(MAPI_U8 u8NumVSyncs, MAPI_U16 u16Timeout) = 0;

    //------------------------------------------------------------------------------
    /// Wait numbers of v-sync, this function will block
    /// @param u8NumVSyncs          \b IN: numbers of v-sync to wait
    /// @param u16Timeout           \b IN: timeout counter
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void WaitInputVSync(MAPI_U8 u8NumVSyncs, MAPI_U16 u16Timeout) = 0;

    //------------------------------------------------------------------------------
    /// Set the color of background
    /// @param u32Color             \b IN: background color, [7,0] color B, [15,8] color G, [23, 16] color B.
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void SetBackgroundColor(MAPI_U32 u32Color) = 0;

    //------------------------------------------------------------------------------
    /// To initiate hardware of this module.
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC static void InitHW(void);
     //------------------------------------------------------------------------------
     /// To Finalize hardware of this module. if has something to uninit,please add in this function
     /// @return None
     //------------------------------------------------------------------------------
     DLL_PUBLIC static void FinitHW(void);

    //------------------------------------------------------------------------------
    /// To create thread to do HDMI initialization.
    /// @param arg      \b IN: the argument for thread
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC static void* InitHDMIthread(void *arg);

    //------------------------------------------------------------------------------
    /// To Set Factory Mode to enable InitHW interface
    /// @param bEnable              \b IN: enable/disable bFactoryMode flag
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC static void SetFactoryMode(MAPI_BOOL bEnable);

    //------------------------------------------------------------------------------
    /// To Check current scaler blue/black screen setting is on or off
    /// @param bIsXCReady   \b OUT: is scaler ready or not
    /// @param eWindow      \b IN: scaler window
    /// @return Trur/False
    //------------------------------------------------------------------------------
    DLL_PUBLIC static MAPI_BOOL IsBlueBlackScreen(MAPI_BOOL * bIsXCReady, MAPI_SCALER_WIN eWindow=MAPI_MAIN_WINDOW);

    //------------------------------------------------------------------------------
    /// Set Frame Buffer Less
    /// @param u32Address      \b IN: the setting of Frane buffer address
    /// @param u32Size            \b IN: the setting of Freame buffer size
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void SetFrameBufferSize(MAPI_U32 u32Address, MAPI_U32 u32Size) = 0;

    //------------------------------------------------------------------------------
    /// Set Scaler Frame Rate Convert setting
    /// If Enable the FRC fun, it must also be called when timing change.
    /// If Disable the FRC fun, FRC be controled by setwindow flow
    /// @param bEnable            \b IN: enable/disable FRC control
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void SetFrameRateConvert(MAPI_BOOL bEnable) = 0;

    //------------------------------------------------------------------------------
    /// Get scaler info
    /// @param enType           \b IN: scaler parameter
    /// @param pBuf             \b OUT: the parameter value
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void GetScalerInfo(mapi_video_datatype::MAPI_VIDEO_SC_INFO enType, void *pBuf) = 0;

    //------------------------------------------------------------------------------
    /// Set Inputsource Disable on/off
    /// @param bEnable              \b IN: enable/disable inputsource disable
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void SetInputSourceDisable(MAPI_BOOL bEnable) = 0;

    //------------------------------------------------------------------------------
    /// Set HPD invert
    /// @param  u32Invert             \b IN: invert setting
    /// @return                     \b OUT: MAPI_TRUE  - the setting success
    ///                             \b OUT: MAPI_FALSE - the setting failed
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL SetHPDInvert(MAPI_U32 u32Invert) = 0;

    //------------------------------------------------------------------------------
    /// Request virtual box size
    /// @param  u16VBoxWidth              \b Out: virtual box width supported by XC
    /// @param  u16VBoxHeight             \b Out: virtual box height supported by XC
    /// @param  u8Interlace               \b IN: input is interlaced or not
    /// @param  u16FrameRate              \b IN: input source framerate
    /// @param  eWindow                   \b IN: the select window
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void RequestVirtualBoxSize(MAPI_U16* u16VBoxWidth, MAPI_U16* u16VBoxHeight, MAPI_U8 u8Interlace, MAPI_U16 u16FrameRate, MAPI_SCALER_WIN eWindow) = 0;

    //------------------------------------------------------------------------------
    /// Set virtual box
    /// @param  bEn                 \b IN: Enable/Disable Virtual Box
    /// @param  u16MVOP_width            \b IN: width of virtual box
    /// @param  u16MVOP_height           \b IN: height of virtual box
    /// @param  u16src_width            \b IN: width of source
    /// @param  u16src_height           \b IN: height of source
    /// @param  DispWin            \b IN: display window info
    /// @param  CropWin            \b IN: crop window info
    /// @return                     \b OUT: MAPI_TRUE  - the setting success
    ///                             \b OUT: MAPI_FALSE - the setting failed
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL SetVirtualBox(MAPI_BOOL bEn, MAPI_U16 u16MVOP_width, MAPI_U16 u16MVOP_height, MAPI_U16 u16src_width, MAPI_U16 u16src_height, const mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE &DispWin, const mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE &CropWin) = 0;

    //------------------------------------------------------------------------------
    /// Get width of current virtual box
    /// @param  eWindow                   \b IN: the select window
    /// @return MAPI_U16                  \b OUT: width of current virtual box
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_U16 GetCurrentVirtualBoxWidth(MAPI_SCALER_WIN eWindow) = 0;

    //------------------------------------------------------------------------------
    /// Get height of current virtual box
    /// @param  eWindow                   \b IN: the select window
    /// @return MAPI_U16                  \b OUT: height of current virtual box
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_U16 GetCurrentVirtualBoxHeight(MAPI_SCALER_WIN eWindow) = 0;

    //------------------------------------------------------------------------------
    /// ADC SW auto adjust gain offset or HW get fixed gain.
    /// @param CurrentMapiInputType           \b IN: inputsource
    /// @param peAdcIndex                     \b OUT: adc set index
    /// @param pstADCGainOffset               \b OUT: adc gain offset
    /// @param enCalibrationMode              \b IN: Calibration Mode hw or sw
    /// @return MAPI_BOOL                     \b: MAPI_TRUE - succeed, MAPI_FALSE - fail
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL AutoGainOffset(const MAPI_INPUT_SOURCE_TYPE CurrentMapiInputType, E_ADC_SET_INDEX *peAdcIndex, MAPI_PQL_CALIBRATION_DATA *pstADCGainOffset, const EN_MAPI_CALIBRATION_MODE enCalibrationMode = E_MAPI_CALIBRATION_MODE_SW);

    //------------------------------------------------------------------------------
    /// enable HW calibrating when changing source.
    /// @param bEnable                      \b IN: enable/disable HW calibration
    /// @return MAPI_BOOL                   \b: MAPI_TRUE - succeed, MAPI_FALSE - fail
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL EnableADCHWCalibration(const MAPI_BOOL bEnable);

    //------------------------------------------------------------------------------
    /// get HW fixed gain/offset.
    /// @param enADCIndex                   \b IN: ADC set index
    /// @param pstADCGainOffset         \b IN: ADC gain offset structure pointer
    /// @return MAPI_BOOL                   \b: MAPI_TRUE - succeed, MAPI_FALSE - fail
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL getHWFixedADCGainOffset(const E_ADC_SET_INDEX enADCIndex, MAPI_PQL_CALIBRATION_DATA* pstADCGainOffset);

    //------------------------------------------------------------------------------
    /// Set overscan information
    /// @param  pInfo               \b IN: oversca information
    /// @return                     \b OUT: MAPI_TRUE  - the setting success
    ///                             \b OUT: MAPI_FALSE - the setting failed
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL SetOverScanInfo(const ST_MAPI_VIDEO_WINDOW_INFO **pInfo) = 0;

    //------------------------------------------------------------------------------
    /// get OverScan Table for VGA/HDMI/YPbPr/VD input source
    /// @return                     \b OUT: OverScan Table for VGA/HDMI/YPbPr input source
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual const ST_MAPI_VIDEO_WINDOW_INFO** getOverScanTable(void) = 0;

    //------------------------------------------------------------------------------
    /// get resolution Table index for VGA/HDMI/YPbPr/VD input source
    /// @param pu8Res               \b IN : resolution Table index for VGA/HDMI/YPbPr/VD input source
    /// @return                     \b OUT: MAPI_TRUE  - the setting success
    ///                             \b OUT: MAPI_FALSE - the setting failed
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL getResolution_Info(MAPI_U8 *pu8Res) = 0;

    //------------------------------------------------------------------------------
    /// Video screen garbage checked
    /// @return                     \b OUT: MAPI_TRUE  - video check OK
    ///                             \b OUT: MAPI_FALSE - video check failed
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL XC_AutoTest(void)=0;

    //------------------------------------------------------------------------------
    /// Clear display buffer
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void ClearDisplayBuffer(void) = 0;

    //------------------------------------------------------------------------------
    /// Get the video status
    /// @param pDrvStatus                 \b OUT: the pointer of video status
    /// @param eWindow                    \b IN: the select window
    /// @return MAPI_BOOL
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL GetStatus(mapi_video_datatype::ST_MAPI_VIDEO_STATUS *pDrvStatus, MAPI_SCALER_WIN eWindow) = 0;

    //------------------------------------------------------------------------------
    /// Get the panel type
    /// @return EN_MAPI_PNL_MODE
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual EN_MAPI_PNL_MODE GetMirrorModeType(void) = 0;

    //------------------------------------------------------------------------------
    /// Set Vstart of capture window
    /// @param u16Vstart    \b IN: Vstart
    /// @param eWindow      \b IN: the scaler window
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void SetCaptureWindowVstart(MAPI_U16 u16Vstart , MAPI_SCALER_WIN eWindow) = 0;

    //------------------------------------------------------------------------------
    /// Set Video as SetMode status
    /// @return  None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void SetModeStart(void) = 0;

    //------------------------------------------------------------------------------
    /// Set Video as NonSetMode status
    /// @return  None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void SetModeEnd(void) = 0;

    //------------------------------------------------------------------------------
    /// To Enable or disable 3D effect
    /// @param bEnable              \b IN: enable/disable 3D effect
    /// @param enInMode             \b IN: set 3D input mode
    /// @param enOutMode            \b IN: set 3D output mode
    /// @param ptARCInfo            \b IN: set the ARC info type
    /// @return      \b OUT: True or False
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL Enable3D(MAPI_BOOL bEnable, mapi_video_datatype::EN_3D_INPUT_TYPE enInMode, mapi_video_datatype::EN_3D_OUTPUT_TYPE enOutMode,
                            mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO *ptARCInfo);

#if (STB_ENABLE == 1)
    //------------------------------------------------------------------------------
    /// To Enable or disable 3D UI for stb
    /// @param bEnable              \b IN: enable/disable 3D effect
    /// @return      \b OUT: True or False
    //------------------------------------------------------------------------------

    DLL_PUBLIC MAPI_BOOL SetHDMITX_Enable3D(MAPI_BOOL bEnable);
    //------------------------------------------------------------------------------
    /// To Enable or disable 3D UI for stb
    /// @param bEnable              \b IN: enable/disable 3D effect
    /// @return      \b OUT: True or False
    //------------------------------------------------------------------------------

    DLL_PUBLIC MAPI_BOOL SetHDMITX_3DMode(MAPI_U16 u16_3DMode);
#endif
    //------------------------------------------------------------------------------
    /// To set 3D effect info
    /// @param bEnable              \b IN: enable/disable 3D effect
    /// @param enInMode             \b IN: set 3D input mode
    /// @param enOutMode            \b IN: set 3D output mode
    /// @return      \b OUT: True or False
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL Set3DInfo(MAPI_BOOL bEnable, mapi_video_datatype::EN_3D_INPUT_TYPE enInMode, mapi_video_datatype::EN_3D_OUTPUT_TYPE enOutMode);

    //------------------------------------------------------------------------------
    /// To get 3D effect info
    /// @param mapi_3D_info      \b OUT: get 3D info
    /// @return      \b OUT: True or False
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL Get3DInfo(mapi_video_datatype::ST_MAPI_3D_INFO &mapi_3D_info);

    //------------------------------------------------------------------------------
    /// Set 3D L/R switch
    /// @param bEnable              \b IN: enable/disable L/R switch
    /// @return                     \b OUT: MAPI_TRUE  - switch done
    ///                             \b OUT: MAPI_FALSE - switch failed
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL Set3DLRSwitch(MAPI_BOOL bEnable);

    //------------------------------------------------------------------------------
    /// get main or sub window as the first show one
    /// @return                     \b OUT: MAPI_TRUE  - switched
    ///                             \b OUT: MAPI_FALSE - not switched
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL Get3DLRSwitch();

    //------------------------------------------------------------------------------
    /// get main or sub window as the first show one
    /// @return                     DLL_PUBLIC MAPI_U16
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_U16 Get3DHShiftStatus();

    //------------------------------------------------------------------------------
    /// Set 3D H Shift for 2d->3d effect
    /// @param u163DH      \b OUT: the 3D H Shift value
    /// @return      \b OUT: True or False
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL Set3DHShift(MAPI_U16 u163DH);

    //------------------------------------------------------------------------------
    /// Set OSDC freq
    /// @param u16OsdFreq      \b OUT: the OSDC freq
    /// @return   None
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL setOsdcFreq(MAPI_U16 u16OsdFreq);

    //------------------------------------------------------------------------------
    /// Set Para for hw 2d to 3d
    /// @param pstHw2DTo3DPara           \b IN: pointer to mapi_video_datatype::ST_MAPI_3D_HW2DTO3DPARA
    /// @return                          \b OUT: True or False
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL SetHW2DTo3DParameters(mapi_video_datatype::ST_MAPI_3D_HW2DTO3DPARA *pstHw2DTo3DPara);

    //------------------------------------------------------------------------------
    /// Get Para for hw 2d to 3d
    /// @param pstHw2DTo3DPara              \b IN: pointer to mapi_video_datatype::ST_MAPI_3D_HW2DTO3DPARA
    /// @return                             \b OUT: True or False
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL GetHW2DTo3DParameters(mapi_video_datatype::ST_MAPI_3D_HW2DTO3DPARA *pstHw2DTo3DPara);

    //------------------------------------------------------------------------------
    /// Is Supported HW 2D To 3D
    /// @return                             \b OUT: True or False
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL IsSupportedHW2DTo3D();

    //------------------------------------------------------------------------------
    /// Set Detect 3D Format Para
    /// @param pstDetect3DFormatPara        \b IN: pointer to mapi_video_datatype::ST_MAPI_3D_DETECT3DFORMATPARA
    /// @return                             \b OUT: True or False
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL SetDetect3DFormatParameters(mapi_video_datatype::ST_MAPI_3D_DETECT3DFORMATPARA *pstDetect3DFormatPara);

    //------------------------------------------------------------------------------
    /// Get Detect 3D Format Para
    /// @param pstDetect3DFormatPara        \b IN: pointer to mapi_video_datatype::ST_MAPI_3D_DETECT3DFORMATPARA
    /// @return                             \b OUT: True or False
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL GetDetect3DFormatParameters(mapi_video_datatype::ST_MAPI_3D_DETECT3DFORMATPARA *pstDetect3DFormatPara);

    //------------------------------------------------------------------------------
    /// Detect 3d Format By Content
    /// @param  eWindow                     \b IN: which window view we are going to detect
    /// @return E_XC_3D_INPUT_MODE          \b OUT: detected 3d format
    //------------------------------------------------------------------------------
    DLL_PUBLIC mapi_video_datatype::EN_3D_INPUT_TYPE Detect3DFormatByContent(MAPI_SCALER_WIN eWindow);

    //------------------------------------------------------------------------------
    /// Enable Auto Detect 3D and Assign Detect Method
    /// @param bEnable                      \b IN: enable auto detect 3d or not
    /// @param enDetectMethod               \b IN: detect method
    /// @return                             \b OUT: True or False
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL EnableAutoDetect3D(MAPI_BOOL bEnable, mapi_video_datatype::EN_3D_AUTODETECT_METHOD enDetectMethod);

    //------------------------------------------------------------------------------
    /// Get Auto Detect 3D Flag
    /// @param pbEnable                     \b IN: whether enable auto detect 3d or not
    /// @param penDetectMethod               \b IN: which detect method
    /// @return                             \b OUT: True or False
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL GetAutoDetect3DFlag(MAPI_BOOL *pbEnable, mapi_video_datatype::EN_3D_AUTODETECT_METHOD *penDetectMethod);

    //------------------------------------------------------------------------------
    /// Get the 3d format is supported by our chips or not information
    /// @param enInMode                     \b IN: 3d input mode
    /// @param enOutMode                    \b IN: 3d output mode
    /// @return                             \b OUT: True: support False: not support
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL Is3DFormatSupported(mapi_video_datatype::EN_3D_INPUT_TYPE enInMode, mapi_video_datatype::EN_3D_OUTPUT_TYPE enOutMode);

    //------------------------------------------------------------------------------
    /// Set Pause Flag for 3D
    /// @param bEnable                     \b IN: whether enable pause or not
    /// @return                             \b OUT: True or False
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL Set3DPauseFlag(MAPI_BOOL bEnable) = 0;

    //------------------------------------------------------------------------------
    /// Enable 3D By MVOP
    /// @param bEnable              \b IN: enable/disable 3D
    /// @param enInMode             \b IN: set 3D input mode
    /// @param enOutMode            \b IN: set 3D output mode
    /// @return                     \b OUT: True or False
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL Enable3DByMVOP(MAPI_BOOL bEnable, mapi_video_datatype::EN_3D_INPUT_TYPE enInMode, mapi_video_datatype::EN_3D_OUTPUT_TYPE enOutMode) = 0;

    //------------------------------------------------------------------------------
    /// Query which 3d format need mvop output
    /// @return                             \b OUT: output 3d type
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual mapi_video_datatype::EN_3D_OUTPUT_TYPE NeedMVOPOutput3DType(void) = 0;
////////////////////////////////////////////////////////////////////////////////////////////////
////The following 3D function is obsolete, we will remove later!!!
////////////////////////////////////////////////////////////////////////////////////////////////

    //------------------------------------------------------------------------------
    /// Do 3D LR view switch
    /// @return                     \b OUT: MAPI_TRUE  - switch done
    ///                             \b OUT: MAPI_FALSE - switch failed
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL Set3DLRSwitch();

    //------------------------------------------------------------------------------
    /// set 3D LR view switch flag
    /// @param bSet              \b IN: set to true of false
    /// @return      \b OUT: True or False
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL Set3DLRSwitchFlag(MAPI_BOOL bSet);

    //------------------------------------------------------------------------------
    /// get main or sub window as the first show one
    /// @return                     \b OUT: MAPI_TRUE  - switched
    ///                             \b OUT: MAPI_FALSE - not switched
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL Get3DLRSwitchStatus();

////////////////////////////////////////////////////////////////////////////////////////////////
////The above 3D function is obsolete, we will remove later!!!
////////////////////////////////////////////////////////////////////////////////////////////////

    //------------------------------------------------------------------------------
    /// Set the dynamic scaling info
    /// @param pstDSInfo                  \b IN: pointer to dynamic scaling info
    /// @param u32DSInfoLen               \b IN: the size of the info
    /// @param eWindow                    \b IN: the select window
    /// @return MAPI_BOOL
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL SetDynamicScaling(const mapi_video_datatype::ST_MAPI_DYNAMICSCALING_INFO* const pstDSInfo, const MAPI_U32 u32DSInfoLen,const MAPI_SCALER_WIN eWindow);

    //------------------------------------------------------------------------------
    /// Get the dynamic scaling info
    /// @param bDS_Status                 \b Out: Get DS on/off info
    /// @param u16CurrentFrameHSize       \b Out: the current frame horizontal size
    /// @param u16CurrentFrameVSize       \b Out: the current frame vertical size
    /// @param u16NextFrameHSize          \b Out: the Next frame horizontal size
    /// @param u16NextFrameVSize          \b Out: the Next frame vertical size
    /// @param u16VsyncCNT                \b Out: the vsync count
    /// @param stCapWin                \b Out: the capture window
    /// @param stCropWin                \b Out: the corp window
    /// @param stDispWin                \b Out: the display window
    /// @param eWin                       \b IN: the select window
    //------------------------------------------------------------------------------
    DLL_PUBLIC void DS_GetVideoStatusFromFirmware(
        MAPI_BOOL &bDS_Status,
        MAPI_U16 &u16CurrentFrameHSize,
        MAPI_U16 &u16CurrentFrameVSize,
        MAPI_U16 &u16NextFrameHSize,
        MAPI_U16 &u16NextFrameVSize,
        MAPI_U16 &u16VsyncCNT,
        mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE &stCapWin,
        mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE &stCropWin,
        mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE &stDispWin,
        const MAPI_SCALER_WIN eWin
        );
#if (STB_ENABLE == 1)
    //------------------------------------------------------------------------------
    /// Set the XC1 dynamic scaling  info
    /// @param pstDSInfo                  \b IN: pointer to dynamic scaling info
    /// @param u32DSInfoLen               \b IN: the size of the info
    /// @param eWindow                    \b IN: the select window
    /// @return MAPI_BOOL
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL SetDynamicScalingEx(const mapi_video_datatype::ST_MAPI_DYNAMICSCALING_INFO* const pstDSInfo,
                                                                                           const MAPI_U32 u32DSInfoLen,const MAPI_SCALER_WIN eWindow);
#endif
    //------------------------------------------------------------------------------
    /// Set the dynamic scaling PQ on or off
    /// @param bEnable                    \b IN: to on or off the PQ DS
    /// @return none
    //------------------------------------------------------------------------------
    DLL_PUBLIC void PQ_SetDS_OnOFF(const MAPI_BOOL bEnable);

    //------------------------------------------------------------------------------
    /// move view window for dynamic scaling
    /// @param u32DSAddr        \b IN: physical address of dynamic scaling info
    /// @param pstCropWin       \b IN: corp window
    /// @param pstDispWin       \b IN: display window
    /// @param u32Height        \b IN: height
    /// @param u32Width         \b IN: width
    /// @return Success
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL DS_MoveViewWindow(
        const MAPI_U32 u32DSAddr,
        mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstCropWin,
        mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstDispWin,
        const MAPI_U32 u32Height,
        const MAPI_U32 u32Width
        );

    //------------------------------------------------------------------------------
    /// Set the Display window info
    /// @param pstDspwin                  \b IN: pointer to display window info
    /// @param eWindow                    \b IN: the select window
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC void SetDispWinToDriver(const mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE* const pstDspwin,const MAPI_SCALER_WIN eWindow);

    //------------------------------------------------------------------------------
    /// Get the Display window info
    /// @param pstDspwin                  \b IN: pointer to display window info
    /// @param eWindow                    \b IN: the select window
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC void GetDispWinFromDriver(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE* const pstDspwin,const MAPI_SCALER_WIN eWindow);

    //------------------------------------------------------------------------------
    /// Get XC H Duplicate
    /// @param eWindow                    \b IN: the select window
    /// @return TRUE : for MVD, YPbPr, indicate input double sampled
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL GetHDuplicate( const MAPI_SCALER_WIN eWindow);

    ///////////////////////////////////////////////
    //////////////// Pixel Shift //////////////////////
    //////////////////////////////////////////////
    //-------------------------------------------------------------------------------------------------
    /// Shift pixel.
    /// @return        \b OUT: If shift pixel success, it returns MAPI_TRUE, otherwise it returns MAPI_FALSE.
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL ShiftPixel(void) = 0;

    //-------------------------------------------------------------------------------------------------
    /// Set HDR metadata
    /// @param pstMetadata             \b IN: HDR metadata
    /// @param enWin                     \b IN: Set Main window or Sub window.
    /// @return      \b OUT: If operation success, it returns MAPI_TRUE, otherwise it returns MAPI_FALSE.
    //-------------------------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL SetHdrMetadata(ST_MAPI_HDR_METADATA *pstMetadata, MAPI_SCALER_WIN enWin) = 0;

    //-------------------------------------------------------------------------------------------------
    /// Get HDR attributes.
    /// @param enAttribueType       \b IN: Specify HDR attributes enum
    /// @param pAttributes             \b OUT: Return attributes specify by enAttribueType.
    /// @param u16AttributesSize  \b IN: Attributes size.
    /// @return      \b OUT: If functiion success, it returns MAPI_TRUE, otherwise it returns MAPI_FALSE.
    //-------------------------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL GetHdrAttributes(EN_MAPI_HDR_ATTRIBUTES enAttribueType, void *pAttributes, MAPI_U16 u16AttributesSize) = 0;

    //-------------------------------------------------------------------------------------------------
    /// Set HDR attributes.
    /// @param enAttribueType       \b IN: Specify HDR attributes enum
    /// @param pAttributes             \b IN: Specify attributes that you want to set.
    /// @param u16AttributesSize  \b IN: Attributes size.
    /// @return      \b OUT: If functiion success, it returns MAPI_TRUE, otherwise it returns MAPI_FALSE.
    //-------------------------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL SetHdrAttributes(EN_MAPI_HDR_ATTRIBUTES enAttribueType, void *pAttributes, MAPI_U16 u16AttributesSize) = 0;


    //-------------------------------------------------------------------------------------------------
    /// Is HDR enable
    /// @param enWin                     \b IN: Set Main window or Sub window.
    /// @return      \b OUT: Return HDR is enable or disable.
    //-------------------------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL IsHdrEnable(MAPI_SCALER_WIN enWin) = 0;

    //-------------------------------------------------------------------------------------------------
    /// Get auto detect HDR level.
    /// @param enWin                     \b IN: Set Main window or Sub window.
    /// @return                          \b OUT: MAPI_TRUE or MAPI_FALSE
    //-------------------------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL GetAutoDetectHdrLevel(MAPI_SCALER_WIN enWin);

    //-------------------------------------------------------------------------------------------------
    /// Set auto detect HDR level.
    /// @param bAuto                  \b IN: Specify auto or not.
    /// @param enWin                 \b IN: Set Main window or Sub window.
    /// @param enAutoHdrLevel  \b IN: Specify auto HDR level.
    /// @return                            \b OUT: MAPI_TRUE or MAPI_FALSE
    //-------------------------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL SetAutoDetectHdrLevel(MAPI_BOOL bAuto, MAPI_SCALER_WIN enWin, E_MAPI_XC_HDR_LEVEL enAutoHdrLevel = E_MAPI_XC_HDR_MAX);

    //-------------------------------------------------------------------------------------------------
    /// Get HDR level.
    /// @param enWin                     \b IN: Set Main window or Sub window.
    /// @return                        \b OUT: return HDR level.
    //-------------------------------------------------------------------------------------------------
    DLL_PUBLIC E_MAPI_XC_HDR_LEVEL GetHdrLevel(MAPI_SCALER_WIN enWin);

    //-------------------------------------------------------------------------------------------------
    /// Set HDR level.
    /// @param enHdrLevel       \b IN: HDR level.
    /// @param enWin                     \b IN: Set Main window or Sub window.
    /// @return                          \b OUT: MAPI_TRUE or MAPI_FALSE
    //-------------------------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL SetHdrLevel(E_MAPI_XC_HDR_LEVEL enHdrLevel, MAPI_SCALER_WIN enWin);

    //-------------------------------------------------------------------------------------------------
    /// Is driver support HDR function.
    /// @return                          \b OUT: MAPI_TRUE or MAPI_FALSE
    //-------------------------------------------------------------------------------------------------
    DLL_PUBLIC static MAPI_BOOL IsSupportHdr();

#if (H_LINEAR_SCALING_ENABLE == 1)
    //------------------------------------------------------------------------------
    /// Set H linear scaling
    /// @param bEnable                    \b IN: enable or disable h linear scaling
    /// @param bSign                      \b IN: the signed
    /// @param u16Delta                   \b IN: the delta
    /// @param eWindow                    \b IN: the select window
    /// @return Success
    //------------------------------------------------------------------------------
    DLL_PUBLIC MS_BOOL SetHLinearScaling(MS_BOOL bEnable, MS_BOOL bSign, MS_U16 u16Delta, const MAPI_SCALER_WIN eWindow);
#endif

    //-------------------------------------------------------------------------------------------------
    /// Get scaler memory data from memory buffer
    /// @param  eBufType               \b IN: get RGB 8 bits or 10 bits format data
    /// @param  pRect                  \b IN: the widnow for getting data area in memory, need to refer to pre-scaling
    /// @param  pRectBuf               \b IN: the buffer for getting data, the buffer typs must be the same as eBufType
    /// @param  eWindow                \b IN: get main or sub video data from memory
    /// @return Success
    //-------------------------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL GetScalerMemoryData(mapi_video_datatype::EN_MAPI_XC_OUTPUTDATA_TYPE eBufType, mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pRect, void *pRectBuf, MAPI_SCALER_WIN eWindow);

    //-------------------------------------------------------------------------------------------------
    /// Set specified data into scaler memory buffer
    /// @param  bEnable                 \b IN: ENABLE/DISABLE to set data on memory and show on screen
    /// @param  eBufType               \b IN: data source is RGB 8 bits or 10 bits or YUV 8 bits format
    /// @param  pRect                    \b IN: the widnow for source data size
    /// @param  pDataBuf               \b IN: the buffer for source data, the buffer typs must be the same as eBufType
    /// @param  eWindow                \b IN: get main or sub video data from memory
    /// @return Success
    //-------------------------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL SetDataOnScalerMemory(MAPI_BOOL bEnable, mapi_video_datatype::EN_MAPI_XC_INPUTDATA_TYPE eBufType, mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pRect, void *pDataBuf, MAPI_SCALER_WIN eWindow);

    //------------------------------------------------------------------------------
    /// view zooming for dynamic scaling
    /// @param u32DSAddr        \b IN: physical address of dynamic scaling info
    /// @param pstCropWin       \b IN: corp window
    /// @param pstDispWin       \b IN: display window
    /// @param u32CropBottom    \b IN: bottom
    /// @param u32Height        \b IN: height
    /// @param u32Width         \b IN: width
    /// @return Success
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL DS_ViewZooming(
        const MAPI_U32 u32DSAddr,
        mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstCropWin,
        mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstDispWin,
        const MAPI_U32 u32CropBottom,
        const MAPI_U32 u32Height,
        const MAPI_U32 u32Width
        );

    //------------------------------------------------------------------------------
    /// send XC status to firmware for dynamic scaling
    /// @param u32FM_Buf_Base   \b IN: physical address of dynamic scaling info
    /// @param eWin             \b IN: window type
    /// @return none
    //------------------------------------------------------------------------------
    DLL_PUBLIC void DS_SendXCStatus2Firmware(const MAPI_U32 u32FM_Buf_Base, const MAPI_SCALER_WIN eWin);

    //------------------------------------------------------------------------------
    /// view zooming for dynamic scaling
    /// @param u32FM_Buf_Base   \b IN: physical address of dynamic scaling info
    /// @param stNewCropWin     \b IN: corp window
    /// @param stNewDispWin     \b IN: display window
    /// @param eWin             \b IN: window type
    /// @return none
    //------------------------------------------------------------------------------
    DLL_PUBLIC void DS_SendZoomInfo2Firmware(const MAPI_U32 u32FM_Buf_Base, const mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE* const stNewCropWin, const mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE* const stNewDispWin, const MAPI_SCALER_WIN eWin);

    //------------------------------------------------------------------------------
    /// Set ADC SOG bandwidth
    /// @param u16value         \b IN: SOG bandwidth value
    /// @return MAPI_BOOL       \b TRUE : success , FALSE : Failed
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL ADC_Set_SOGBW(MAPI_U16 u16value);

    //------------------------------------------------------------------------------
    /// Get Scaler output Vertical frequency
    /// @return MAPI_U16        \b scaler output Vertical frequency
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_U16 GetOutputVFreq(void);

    //-------------------------------------------------------------------------------------------------
    /// SetVideoOnOSD
    /// @param  enlayer               \b IN: set video show on which osd layer
    /// @param  eWindow               \b IN: set main or sub video data to memory
    /// @return Success
    //-------------------------------------------------------------------------------------------------

    DLL_PUBLIC MAPI_BOOL SetVideoOnOSD(const mapi_video_datatype::EN_MAPI_VIDEO_ON_OSD_LAYER enlayer,  const MAPI_SCALER_WIN eWindow);

    //------------------------------------------------------------------------------
    /// WaitFPLLDone
    /// @return Mapi_TRUE
    /// @return Mapi_FALSE
    //------------------------------------------------------------------------------
    DLL_PUBLIC static MAPI_BOOL WaitFPLLDone(void);

    //------------------------------------------------------------------------------
    /// SetSourceType for video path. This must be involved after initialization
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void SetSourceType(void) = 0;

    //------------------------------------------------------------------------------
    /// if enable PWS
    /// @param bEnable             \b IN:TRUE/FALSE
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void EnablePWS(MAPI_BOOL bEnable) = 0;


    //------------------------------------------------------------------------------
    /// Check if is active
    /// @return MAPI_BOOL
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL  IsActive(void) = 0;

    //------------------------------------------------------------------------------
    /// Enable  FD MASK
    /// @param  bEnable                     \b IN: Slecet Enable or Disable
    /// @return MAPI_BOOL                        \b OUT: result.
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL SetFD_Mask(MAPI_BOOL bEnable) = 0;

    //-------------------------------------------------------------------------------------------------
    /// Get Video resolution size
    /// @param E_VideoInfo                  \b IN: The video type
    /// @return mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_INFO   \b OUT: The resolution size.
    //-------------------------------------------------------------------------------------------------
    DLL_PUBLIC static const ResolutionInfoSize GetVideoResSize(VideoInfo_t E_VideoInfo);

    //------------------------------------------------------------------------------
  /// Get the Black Video Enable or Disable
    /// @param eWindow                    \b IN: the select window
    /// @return MAPI_BOOL
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL IsBlackVideoEnable(MAPI_SCALER_WIN eWindow)=0;
    //------------------------------------------------------------------------------
    /// Get the video status
    /// @param bEnable                     \b IN: Slecet Enable or Disable
    /// @param eWindow                    \b IN: the select window
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void SetWindowEnable(MAPI_BOOL bEnable, MAPI_SCALER_WIN eWindow)=0;
    //------------------------------------------------------------------------------
    /// Enable 3D dual view
    /// @param bEnable                     \b IN: Slecet Enable or Disable
    /// @return MAPI_BOOL \b TRUE: success, FALSE: fail.
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL Enable3DDualView(MAPI_BOOL bEnable)=0;

    //------------------------------------------------------------------------------
    /// Is Lock Display Window
    /// @return MAPI_BOOL          \b TRUE : lock display window , FALSE : not lock display window
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL IsLockDispWindow(void)=0;

    //------------------------------------------------------------------------------
    /// Is Lock Display Window forever
    /// @return MAPI_BOOL          \b TRUE : lock display window , FALSE : not lock display window
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL IsLockDispWindowForever(void)=0;

    //------------------------------------------------------------------------------
    /// Scaler set window flag
    /// @param bSetMode            \b IN: Set Scaler window Flag
    /// @return MAPI_BOOL          \b TRUE : success , FALSE : Failed
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL SetMode2(MAPI_BOOL bSetMode)=0;

    //------------------------------------------------------------------------------
    /// Set ATV scanning or not
    /// @param  bEn                        \b IN   : ATV scanning or not
    /// @return MAPI_BOOL                  \b TRUE : success , FALSE : Failed
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL SetATVScanning(MAPI_BOOL bEn)=0;

    //------------------------------------------------------------------------------
    /// Set video action is ARC or not
    /// @param  bEn                        \b IN   : ARC action or not
    /// @return MAPI_BOOL                  \b TRUE : success , FALSE : Failed
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL SetARCstatus(MAPI_BOOL bEn)=0;

    //------------------------------------------------------------------------------
    /// Set scaler output display to free run
    /// @return MAPI_BOOL                  \b TRUE : success , FALSE : Failed
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL XCSetFreerun(void)=0;

    //------------------------------------------------------------------------------
    /// Enable scaler overscan feature
    /// @param  bEnable                    \b IN   : overscan action or not
    /// @return MAPI_BOOL                  \b TRUE : success , FALSE : Failed
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL EnableOverScan(MAPI_BOOL bEnable) = 0;

    static MAPI_U8 FRC_3D_PANEL_TYPE;
    // for ursa7 only
    //------------------------------------------------------------------------------
    /// Get HDCP key (for ursa7 only)
    /// @param u8HdcpKey    \b IN: HDCP key
    /// @return MAPI_BOOL   \b TRUE : success , FALSE : Failed
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL GetHdcpKey(MAPI_U8* u8HdcpKey);

#if (DTV_CHANNEL_CHANGE_FREEZE_IMAGE_ENBALE == 1)
    //------------------------------------------------------------------------------
    /// Is support Seamless Zapping
    /// @param  None
    /// @return MAPI_BOOL           \b TRUE : Supported , FALSE : Not supported
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL isSupportSeamlessZapping(void);

    //------------------------------------------------------------------------------
    /// Get Seamless Zapping enabled or disabled
    /// @param eWindow                  \b IN: the select window
    /// @return MAPI_BOOL               \b TRUE : Enabled , FALSE : Disabled
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL isEnableSeamlessZapping(MAPI_SCALER_WIN eWindow);

    //------------------------------------------------------------------------------
    /// Setup Seamless Zapping
    /// @param bEnable                      \b IN: Slecet Enable or Disable
    /// @param eWindow                      \b IN: the select window
    /// @return MAPI_BOOL           \b TRUE : Success , FALSE : Failed
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL setupSeamlessZapping(MAPI_BOOL bEnable, MAPI_SCALER_WIN eWindow);
#endif

    //-------------------------------------------------------------------------------------------------
    /// Get XC Status of specific window(Main/Sub) with version info
    /// @param  pDrvStatus                  \b OUT: store the status
    /// @param  eWindow                     \b IN: which window(Main/Sub) is going to get status
    /// @return @ref MAPI_BOOL return the structure size consistency
    //-------------------------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL getXCStatus(mapi_video_datatype::ST_MAPI_XC_API_STATUS *pDrvStatus, MAPI_SCALER_WIN eWindow) = 0;

    //-------------------------------------------------------------------------------------------------
    /// disable input source
    /// @param  bDisable                     \b IN: TRUE : Disable; FALSE: Enable
    /// @param  eWindow                     \b IN: which window(Main/Sub) is going to disable
    //-------------------------------------------------------------------------------------------------
    DLL_PUBLIC void disableInputSource(MAPI_BOOL bDisable, MAPI_SCALER_WIN eWindow);

    //------------------------------------------------------------------------------
    /// Get window is HD or not
    /// @param eWin                         \b IN: the select window
    /// @return MAPI_BOOL                        \b OUT: result.
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL GetSrcIsHD(MAPI_SCALER_WIN eWin)=0;
    //------------------------------------------------------------------------------
    /// Set HDMI channel EQ value
    /// @param enInputSrc           \b IN: input source
    /// @param u8EQValue            \b IN: EQ value
    /// @return                     \b OUT: TRUE success   FALSE failed
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL SetHdmiEQ(MAPI_INPUT_SOURCE_TYPE enInputSrc,MAPI_U8 u8EQValue) = 0;

#if (STB_ENABLE == 0)
    //-------------------------------------------------------------------------------------------------
    /// MakeOutputDeviceHandshake
    /// @return                     \b OUT: TRUE success   FALSE failed
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL MakeOutputDeviceHandshake(void);
#endif


#if (STB_ENABLE == 1)
#if (DUAL_XC_ENABLE == 1)
    //------------------------------------------------------------------------------
    /// Set XC1 Display window
    /// @param bEnable           \b IN: set XC1 display window is enable AA modle need disable
    /// @param pDispWin          \ST_MAPI_VIDEO_WINDOW_TYE IN: display window  position and  window size
    /// @return                  \MAPI_BOOL
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL SetXC1DisplayWindow(MAPI_BOOL bEnable, mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pDispWin) = 0;
#endif
#endif
    //------------------------------------------------------------------------------
    /// Set Picture Quality Call Back Function
    /// @param pFunc                         \b IN: the point of Set Picture Qulity call Function
    /// @return none
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual void SetPQCallBackFunction(pfnSetPictureQuality pFunc)=0;

    //------------------------------------------------------------------------------
    /// Get Picture Quality Call Back Function Flag
    /// @return MAPI_BOOL
    //------------------------------------------------------------------------------
    DLL_PUBLIC virtual MAPI_BOOL GetPQCallBackFunctionFlag(void)=0;

    //------------------------------------------------------------------------------
    /// Set Pcmode flag
    /// @param  bEnable                     \b IN: TRUE : Enable; FALSE: Disable
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC  void SetPCmode(MAPI_BOOL bEnable);

    //------------------------------------------------------------------------------
    /// Get PCmode flag
    /// @return MAPI_BOOL
    //------------------------------------------------------------------------------
    DLL_PUBLIC  MAPI_BOOL GetPCmode(void);

    //------------------------------------------------------------------------------
    /// Query wheather the input feature is supported.
    /// @param enFeature                                 \b IN:  Specify the feature to be queried.
    /// @param pParam                                    \b IN:  Pointer for private use of the specific feature.
    /// @return                                          \b OUT: BOOL TURE for supported feature,
    ///                                                          BOOL FALSE for unsurpported feature.
    //------------------------------------------------------------------------------
    DLL_PUBLIC MAPI_BOOL IsSupportedFeature(mapi_video_datatype::EN_MAPI_VIDEO_SUPPORTED_FEATURES enFeature, void *pParam);

    //------------------------------------------------------------------------------
    /// Set OSDC output timing
    /// @param enTiming                                 \b IN:  Specify OSDC output timing
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC void SetOSDCOutputTiming(EN_MAPI_TIMING enTiming);

    //------------------------------------------------------------------------------
    /// Set init state
    /// @param enInitType           \b IN:  Specify the feature to be edit.
    /// @param bInit                \b IN:  TRUE : Init; FALSE: unInit
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC static void SetVideoInitState(mapi_video_datatype::EN_MAPI_VIDEO_INIT_TYPE enInitType, MAPI_BOOL bInit);

    //------------------------------------------------------------------------------
    /// Get init state
    /// @param enInitType           \b IN:  Specify the type to be queried.
    /// @return                     \b OUT: BOOL TURE for init ,
    ///                                     BOOL FALSE for uninit.
    //------------------------------------------------------------------------------
    DLL_PUBLIC static MAPI_BOOL GetVideoInitState(mapi_video_datatype::EN_MAPI_VIDEO_INIT_TYPE enInitType);


    //-------------------------------------------------------------------------------------------------
    /// To generate designated test pattern,you should follow the following example
    /// Ex:
    /// SET_IPMUX_TESTPATTERN_t ipmux_test_pattern;
    /// ipmux_test_pattern.bEnable = TRUE;
    /// ipmux_test_pattern.u16R_CR_Data = xxx;
    /// ipmux_test_pattern.u16G_Y_Data  = xxx;
    /// ipmux_test_pattern.u16B_CB_Data = xxx;
    /// Generate_TestPattern(E_IPMUX_PATTERN_MODE,(void *)&ipmux_test_pattern,sizeof(XC_IPMUX_TESTPATTERN_t));
    /// Ex:
    /// EN_MVOP_Pattern mvop = E_MVOP_PATTERN_COLORBAR;
    /// Generate_TestPattern(E_MVOP_PATTERN_MODE, (void *)&mvop, sizeof(E_MVOP_PATTERN_MODE));
    /// ------------------------------------------------------------------------------------------------
    /// @param  ePatternMode \b IN:  pattern type
    /// @param  para         \b IN:  pattern related data
    /// @param  u32Length    \b IN:  data length
    //-------------------------------------------------------------------------------------------------
    DLL_PUBLIC  virtual void Generate_TestPattern(EN_TEST_PATTERN_MODE ePatternMode,void* para, MAPI_U32 u32Length) = 0;

// EosTek Patch Begin
    DLL_PUBLIC  static void ReloadHdcpkey(MAPI_BOOL bVer2);
// EosTek Patch End
    
protected:
    /// current pip modes
    static EN_MAPI_PIP_MODES m_ePipMode;
    static mapi_video_datatype::ST_MAPI_3D_INFO m_st3DInfo;
    static MAPI_INPUT_SOURCE_TYPE m_eMainInputSource;
    static MAPI_INPUT_SOURCE_TYPE m_eSubInputSource;
    MAPI_BOOL m_bEnablePCmode;

#ifdef UFO_XC_HDR
    static MAPI_BOOL m_bAutoDetectHdrLevel[2];
    static E_MAPI_XC_HDR_LEVEL m_ActiveHdrLevel[2];
    static E_MAPI_XC_HDR_LEVEL m_AutoHdrLevel[2];

    /// Current HDR metadata.
    ST_MAPI_HDR_METADATA m_HdrMetadata;
    /// Specify HDR have been initialized or not.
    MAPI_BOOL m_bHdrInitialized;
#endif

#if ((PIP_ENABLE == 1) && (TRAVELING_ENABLE == 1))
    //When traveling main source,reseting traveling window may be needed if pip mode changed.
    //Use m_bForceSetTravelingWin to confirm if reseting traveling window is necessary.
    static MAPI_BOOL m_bForceSetTravelingWin;
#endif
#if (STB_ENABLE == 1)
#if (DTV_CHANNEL_CHANGE_FREEZE_IMAGE_ENBALE == 1)
    static MAPI_BOOL m_bEnableSeamlessZapping;
#endif
#endif
    //------------------------------------------------------------------------------
    /// Transfer the SDK Display window to HW register format(Including add Panel DE start, mirror postion transform etc.)
    /// @param pstDispWin                  \b IN: pointer to display window info
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC void TransferSDKDispWinToReg(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE* const pstDispWin);

    //------------------------------------------------------------------------------
    /// Transfer the DispWin from HW register to SDK Display window format(Including minus Panel DE start, mirror postion transform etc.)
    /// @param pstDispWin                  \b IN: pointer to display window info
    /// @return None
    //------------------------------------------------------------------------------
    DLL_PUBLIC void TransferRegDispWinToSDK(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE* const pstDispWin);

    //------------------------------------------------------------------------------
    /// Calculate correct display window through MVOP mirror
    /// @param stDispWin                  \b IN: pointer to display window info
    /// @return stDispWin                   \b OUT: reference of display window info
    //------------------------------------------------------------------------------
    virtual void CalcMirror(MS_WINDOW_TYPE *stDispWin) = 0;

private:
    static MAPI_BOOL bHWInit;
    static MAPI_BOOL bFactoryMode;
    MAPI_BOOL m_bLRSwitch;
    MAPI_U16 m_u163DHShift;
    static MAPI_BOOL m_bSubXCVaild;
    static MAPI_U32 u32DS_Sharemem_Buf_Base;
    static MAPI_BOOL m_bEDIDInit;
    static MAPI_BOOL m_bHDMIInit;
    static void getOSDCinfo(MS_XC_OSDC_CTRL_INFO* osdc);
    static void SetOSDC(void);
    static void SysInitSYS(void);
    static void SysInitXC(void);
#if (STB_ENABLE == 1)
#if (DUAL_XC_ENABLE == 1)
    static void SysInitXCEx(void);
#endif
#endif
    static void SysInitACE(void);
    static void SysInitHDMI(void);
    static void SysSet5vDetectGpioSelect();
    static void SySInitCEC(void);
    static MAPI_BOOL LoadHDCP(void);
};

#endif

