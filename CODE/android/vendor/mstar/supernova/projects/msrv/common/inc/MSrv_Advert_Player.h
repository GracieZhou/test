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
/// @file MSrv_Advert_Player.h
/// @brief\b Provide a simple streaming mode player.
/// @author MStar Semiconductor Inc.
///
/// Application use MSrv_Advertise_Player to play a streaming media.
///
/// Features:
/// - Provide a simple streaming mode player.
///////////////////////////////////////////////////////////////////////////////////////////////////

/*@ <IncludeGuard> @*/
#ifndef _MSRV_ADVERT_PLAYER_H_
#define _MSRV_ADVERT_PLAYER_H_
/*@ <IncludeGuard> @*/

/*@ <Include> @*/
#include "MSrv.h"
#include "MSrv_MM_PlayerInterface.h"

/// Advert player status
typedef enum
{
    /// advert player does not initial
    EN_NOT_INITIAL,
    /// advert player initial ok
    EN_INITIAL_OK,
    /// advert player initial fail
    EN_INITIAL_FAIL,
    /// advert player decode ok
    EN_DECODE_OK,
    /// advert player decode fail 
    EN_DECODE_FAIL,
    /// advert player decode done
    E_DECODE_DONE,
    /// advert player max enum number
    EN_ADVERT_PLAYER_STATUS_MAX
} EN_ADVERT_PLAYER_STATUS;

/// MediaFile Info for Advert player
typedef struct
{
    /// file pointer
    FILE * fp_mminterface;
    U64 u64FilePos;
    U64 u64FileLength;
    U64 u64StartPos;
} ST_MEDIAFILE_INFO;

// Enumerate
/// Define the media type.
typedef enum
{
    /// Media item first file index.
    E_MEDIAFILE_ID_FIRST                = 0,
    /// Media item second file index. Use on video media subtitle data file read process. If the application can't support the seconad file index, please set this argument with 0.
    E_MEDIAFILE_ID_SECOND          = 1,
    /// Media item third file index. Use on video media audio data file read process. If the application can't suppor the third file index, please set thie argument with 0.
    E_MEDIAFILE_ID_THIRD               = 2,
    /// Media item fourth file index. Use on video media DIVX attachment file read process. If the application can't suppor the fourth file index, please set thie argument with 0.
    E_MEDIAFILE_ID_FOURTH           = 3,
    /// Number of Media item ID.
    E_MEDIAFILE_ID_NUM,
}EN_MEDIAFILE_ID_TYPE;


/// The class for MSrv_Advert_Player middleware
/// @code
///
///     if(FALSE == AdvPlayerInit()) 
///     {
///         // there is problem to initial Advert_Player, do error handling here
///     }
///
///     if(FALSE == AdvPlayerPlay())
///     {
///         // there is problem to play video, do error handling here
///     }
///
///     if(FALSE == AdvPlayerStop())
///     {
///         // there is problem to stop player, do error handling here
///     }
///
///     if(FALSE == AdvPlayerExit())
///     {
///         // there is problem to remove media player, do error handling here
///     }
/// @endcode
class MSrv_Advert_Player : public MSrv
{
//       DECLARE_EVENT_MAP();

// ****************************************************************
// Public
// ****************************************************************
public:

    // ------------------------------------------------------------
    // Variable Define
    // ------------------------------------------------------------


    // ------------------------------------------------------------
    // Function Define
    // ------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------
    /// GetInstance of MSrv_Advert_Player
    /// @return  None
    //-------------------------------------------------------------------------------------------------
    static MSrv_Advert_Player* GetInstance();

    //-------------------------------------------------------------------------------------------------
    /// Destroy this instance
    /// @return  None
    //-------------------------------------------------------------------------------------------------
    static void DestroyInstance();

    //-------------------------------------------------------------------------------------------------
    /// Constructor of MSrv_Advert_Player.
    /// @return  None
    //-------------------------------------------------------------------------------------------------
    MSrv_Advert_Player();

    //-------------------------------------------------------------------------------------------------
    /// Destructor of MSrv_Advert_Player.
    /// @return  None
    //-------------------------------------------------------------------------------------------------
    ~MSrv_Advert_Player();

    //-------------------------------------------------------------------------------------------------
    /// Initialize of MSrv_Advert_Player
    /// 1. to read media file and change input source to USB device.
    /// 2. to initialize a mm player interface.
    /// @return         \b TRUE: initialize successfully, FALSE: initializse failed.
    //-------------------------------------------------------------------------------------------------
    BOOL AdvPlayerInit(void);

     //-------------------------------------------------------------------------------------------------
    /// Play of MSrv_Advert_Player
    /// Decode video stream and play.
    /// @return         \b TRUE: play successfully, FALSE: play failed.
    //-------------------------------------------------------------------------------------------------
    BOOL AdvPlayerPlay(void);

    //-------------------------------------------------------------------------------------------------
    /// Stop of MSrv_Advert_Player
    /// Stop playing.
    /// @return         \b TRUE: stop successfully, FALSE: stop failed.
    //-------------------------------------------------------------------------------------------------
    BOOL AdvPlayerStop(void);

    //-------------------------------------------------------------------------------------------------
    /// Exit of MSrv_Advert_Player
    /// Exit Advert Player, after to stop Advert Player
    /// @return         \b TRUE: exit successfully, FALSE: exit failed.
    //-------------------------------------------------------------------------------------------------
    BOOL AdvPlayerExit(void);

    //-------------------------------------------------------------------------------------------------
    /// GetAdvPlayerStatus of MSrv_Advert_Player
    //  Make sure the player initialization.
    /// @return         \b TRUE: have been initialized, FALSE: dose not initialize.
    //-------------------------------------------------------------------------------------------------
    BOOL GetAdvPlayerStatus(void);

    //-------------------------------------------------------------------------------------------------
    /// Finalize
    /// @return         \b TRUE: yes, FALSE: no.
    //-------------------------------------------------------------------------------------------------
    BOOL Finalize(void);

    //-------------------------------------------------------------------------------------------------
    /// Get the playing status
    /// @return         \b the player status: EN_ADVERT_PLAYER_STATUS.
    //-------------------------------------------------------------------------------------------------
    EN_ADVERT_PLAYER_STATUS GetAdvPlayerPlayStatus(void);

// ****************************************************************
// Private
// ****************************************************************

private:

    // ------------------------------------------------------------
    // Variable Define
    // ------------------------------------------------------------
    static MSrv_MM_PlayerInterface * MM_interface;
    static MSrv_Advert_Player* m_instance;
    static U32   u32Item;
    ST_MEDIA_INFO stMedia;
    static BOOL mm_init_flag;
    static BOOL mm_init_fail;
    static BOOL mm_decode_flag;
    static BOOL mm_decode_fail;
    static BOOL bAdvert_player;
    static ST_MEDIAFILE_INFO * m_pMediaFile1st;
    static ST_MEDIAFILE_INFO * m_pMediaFile2nd;
    static ST_MEDIAFILE_INFO * m_pMediaFile3rd;
    static ST_MEDIAFILE_INFO * m_pMediaFile4th;
    ST_MEDIAFILE_INFO m_stMediaFileInfo[E_MEDIAFILE_ID_NUM];
    static BOOL m_bRestore3DdetectFlag;
    // ------------------------------------------------------------
    // Function Define
    // ------------------------------------------------------------
    static BOOL EventHandler(void *arg1, void *arg2, void *arg3);    
    BOOL Open(const char* const pString, ST_MEDIAFILE_INFO * pMediaFileInfo);
    BOOL OpenFile(const char* const pString);
    BOOL Close(ST_MEDIAFILE_INFO * pMediaFileInfo);
    BOOL CloseFile(void);


};

#endif
