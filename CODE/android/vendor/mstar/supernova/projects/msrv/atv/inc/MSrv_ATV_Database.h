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
/// @file MSrv_ATV_Database.h
/// @brief \b Introduction: ATV Database
///
/// @author MStar Semiconductor Inc.
///
/// Features:
/// - Support the Store/Get program information
////////////////////////////////////////////////////////////////////////////////



/*@ <IncludeGuard> @*/
#ifndef MSrv_ATV_Database_H
#define MSrv_ATV_Database_H
/*@ </IncludeGuard> @*/


#include "mapi_vd_datatype.h"
#include "mapi_audio_datatype.h"

/*@ <Include> @*/
#include "MSrv.h"
/*@ </Include> @*/

class mapi_storage;

// EosTek Patch Begin
//the default program number after the autoscan
#define PROGRAM_NUMBER_AFTER_AUTOSCAN       12
// EosTek Patch End

/// the maximum bytes of station name
#define MAX_STATION_NAME            30//16// 9

// ATV PROGRAM DATA STRUCTURE
/// For TTX, List Page
#define MAX_LISTPAGE                4
/// For TTX, about max list page size
#define MAX_LISTPAGE_SIZE           ((MAX_LISTPAGE*10)/8)

/// the maximum number of ATV programs
#define MAX_NUMBER_OF_ATV_PROGRAM    256
/// the maximum number of program table map, round up to the multiple of 8(bits) since we use one bit per program and the minimum size for storing is 1 byte
#define MAX_PRTABLEMAP              ((MAX_NUMBER_OF_ATV_PROGRAM + 7) / 8)

/// the mask for transmission medium
#define MEDIUM_MASK                 0xF000
/// medium type: air
#define _AIR                        0x8000
/// mediumtype: cable
#define _CABLE                      0x4000
/// default medium type
#define DEFAULT_MEDIUM                              MSrv_ATV_Database::MEDIUM_CABLE

/// default channel number
#define DEFAULT_CHANNELNUMBER                       47

/// the first program number
#if (ATSC_SYSTEM_ENABLE == 1)
#define ATV_FIRST_PR_NUM                            1
#else
#define ATV_FIRST_PR_NUM                            1
#endif
/// CNI sorting priority
#define LOWEST_SORTING_PRIORITY                     0xFF


/// RT_AFT enable or not
#define IS_RT_AFT_ENABLED                 TRUE // FALSE
/// AFT_SAVE enable or not
#define IS_AFT_SAVE_ENABLED             TRUE
/// CH_NAME enable or not
#define IS_CH_NAME_ENABLED              TRUE
/// SAVING_DUAL enable or not
#define IS_SAVING_DUAL_ENABLED          TRUE


/// AUDIO STANDARD
typedef enum
{
    /// Audio Standard BG
    E_AUDIOSTANDARD_BG          = 0x00,
    /// Audio Standard BG A2
    E_AUDIOSTANDARD_BG_A2       = 0x01,
    /// Audio Standard BG Nicam
    E_AUDIOSTANDARD_BG_NICAM    = 0x02,
    /// Audio Standard I
    E_AUDIOSTANDARD_I           = 0x03,
    /// Audio Standard DK
    E_AUDIOSTANDARD_DK          = 0x04,
    /// Audio Standard DK1
    E_AUDIOSTANDARD_DK1_A2      = 0x05,
    /// Audio Standard DK2
    E_AUDIOSTANDARD_DK2_A2      = 0x06,
    /// Audio Standard DK3
    E_AUDIOSTANDARD_DK3_A2      = 0x07,
    /// Audio Standard DK NICAM
    E_AUDIOSTANDARD_DK_NICAM    = 0x08,
    /// Audio Standard L
    E_AUDIOSTANDARD_L           = 0x09,
    /// Audio Standard M
    E_AUDIOSTANDARD_M           = 0x0A,
    /// Audio Standard M BTSC
    E_AUDIOSTANDARD_M_BTSC      = 0x0B,
    /// Audio Standard M A2
    E_AUDIOSTANDARD_M_A2        = 0x0C,
    /// Audio Standard M EIAJ
    E_AUDIOSTANDARD_M_EIA_J     = 0x0D,
    /// Audio Standard Nonstandard
    E_AUDIOSTANDARD_NOTSTANDARD = 0x0F
} EN_MSRV_AUDIOSTANDARD_TYPE;

// Operation method
///DUAL AUDIO SELECTION;
typedef enum
{
    /// MONO
    E_DUAL_MONO         = 0x00,
    /// AUDIO A
    E_DUAL_AUDIO_A      = 0x01,
    /// AUDIO B
    E_DUAL_AUDIO_B      = 0x02,
    /// AUDIO AB
    E_DUAL_AUDIO_AB     = 0x03,
} EN_DUAL_AUDIO_SELECTION;

///Picture Quality items
typedef enum
{
    /// Brightness
    E_BRIGHTNESS        = 0x00,
    /// Contrast
    E_CONTRAST          = 0x01,
    /// Color
    E_COLOR             = 0x02,
    /// HUE
    E_HUE               = 0x03,
    /// Sharpness
    E_SHARPNESS         = 0x04,
    /// Picture Quality Item Number
    E_MAX_ITEM          = 0x05
} EN_PICTURE_QUALITY_ITEMS;

/// the channel attributes
typedef struct
{
    /// the audio standard
    U16 eAudioStandard                         : 4; // This allocation is very dangerous and not good. But I should use this method in order to reduce the size of NVRAM. I'm so sorry.
    /// is skip or not
    U16 bSkip                                  : 1;
    /// is hide or not
    U16 bHide                                  : 1;
    /// the video standard
    U16 eVideoStandard                         : 4; // This allocation is very dangerous and not good. But I should use this method in order to reduce the size of NVRAM. I'm so sorry.
    /// is dual audio selected or not
    U16 bWasDualAudioSelected                  : 2;
    /// Volume compensation for every channel.
    U16 eVolumeComp                            : 4;
    /// the audio mode
    U16 eAudioMode                             : 4; // This allocation is very dangerous and not good. But I should use this method in order to reduce the size of NVRAM. I'm so sorry.
    /// is realtime audio standard detection or not
    U16 bIsRealtimeAudioDetectionEnabled       : 1;
    /// Favorite setting value
    U16 u8Favorite                             : 8;
    /// is Medium type Cable or Air
    U16 eMedium                                : 1;
    /// is lock or not
    U16 bIsLock                                : 1;
    /// the channel number
    U16 u8ChannelNumber                        : 8;
    /// is auto frequency tuning enable
    U16 bAFT                                   : 1;
    /// is Mode direct tune or manual tune?
    U16 bIsDirectTuned                         : 1;
    /// To record the offset of AFT
    U16 u8AftOffset                            : 8;
#if (ISDB_SYSTEM_ENABLE == 1)
    /// is Auto color or not
    U16 bIsAutoColorSystem              :1;
    /// is Auto color or not
    U16 Unused                                   :5;
#endif

} ST_ATV_MISC;

/// the structure of one program
typedef struct
{
    /// Tuner PLL Value
    U16 wPLL;
    /// Program misc info
    ST_ATV_MISC Misc;
    /// is program sort?
    U8 u8Sort;
    /// program fine tune frequency
    S8 s8FineTune;
    /// Program Name
    U8 sName[MAX_STATION_NAME];
    /// to save the list page of this program
    U8 u8ListPage[MAX_LISTPAGE_SIZE];
} ST_ATV_PROGRAM_DATA;

/// the structure of programs saved in database
typedef struct
{
    /// last program number
    U8            u8LastPRNumber;
    /// program index table
    U8            u8ATVPRIndexTable[MAX_NUMBER_OF_ATV_PROGRAM];
    /// program table map
    U8            u8ATVPRTableMap[MAX_PRTABLEMAP];
    /// the program data
    ST_ATV_PROGRAM_DATA  ATVProgramData[MAX_NUMBER_OF_ATV_PROGRAM];
#if (ATSC_SYSTEM_ENABLE == 1)
    /// CABLE program index table
    U8            u8CATVPRIndexTable[MAX_NUMBER_OF_ATV_PROGRAM];
    /// CABLE program table map
    U8            u8CATVPRTableMap[MAX_PRTABLEMAP];
    /// CABLE program data
    ST_ATV_PROGRAM_DATA  CATVProgramData[MAX_NUMBER_OF_ATV_PROGRAM];
#endif
    /// the tag for mark the DB has been initialized
    U8            u8DBInitTag;
} ST_ATV_PROGRAM_DATA_STRUCTURE;


//------------------------------------------------------------------------------
// Public attributes.
//------------------------------------------------------------------------------

///For Get Favorite Program Command
typedef enum
{
    ///get first favorite program
    GET_FIRST_FAVORITE_PROGRAM = 0,
    ///get last favorite program
    GET_LAST_FAVORITE_PROGRAM,
    ///get previous favorite program
    GET_PREVIOUS_FAVORITE_PROGRAM,
    ///get next favorite program
    GET_NEXT_FAVORITE_PROGRAM,
    ///get total favorite program
    GET_TOTAL_FAVORITE_PROGRAM,
    ///is favorite program?
    GET_STATUS_IS_FAVORITE_PROGRAM

} EN_GET_FAVORITE_CMD;
///For Set Favorite Program Command
typedef enum
{
    ///Set favorite program
    SET_FAVORITE_PROGRAM = 0

} EN_SET_FAVORITE_CMD;

///GET PROGRAM INFO;
typedef enum
{
    /// Get fine tune frequency
    GET_FINE_TUNE = 0,
    /// Get Program PLL number
    GET_PROGRAM_PLL_DATA,
    /// Get Program Audio Standard
    GET_AUDIO_STANDARD,
    /// Get Program Video Standard
    GET_VIDEO_STANDARD_OF_PROGRAM,
    /// Get Program DUAL AUDIO Selection
    GET_DUAL_AUDIO_SELECTED,
    /// Is Program need to skip
    IS_PROGRAM_SKIPPED,
    /// Is Program lock
    IS_PROGRAM_LOCKED,
    /// Is Program need to hide
    IS_PROGRAM_HIDE,
    /// Is Program need to AFT
    IS_AFT_NEED,
    /// Is Direct Tuned
    IS_DIRECT_TUNED,
    /// Get AFT Offset
    GET_AFT_OFFSET,
    /// Is realtime audio detection enable
    IS_REALTIME_AUDIO_DETECTION_ENABLE,
    /// Get station name
    GET_STATION_NAME,
    /// Get Sorting priority
    GET_SORTING_PRIORITY,
    /// Get Channel Index
    GET_CHANNEL_INDEX,
#if (ISDB_SYSTEM_ENABLE == 1)
    ///Get ATV auto color
    GET_ATV_AUTOCOLOR,
#endif
    /// Get MISC Information
    GET_MISC

} EN_GET_PROGRAM_INFO;

///Set Program Info
typedef enum
{
    ///Set program pll data
    SET_PROGRAM_PLL_DATA = 0,
    ///Set audio standard
    SET_AUDIO_STANDARD,
    ///Set video standard of program
    SET_VIDEO_STANDARD_OF_PROGRAM,
    ///Skip program
    SKIP_PROGRAM,
    ///Hide program
    HIDE_PROGRAM,
    ///Lock program
    LOCK_PROGRAM,
    ///Need aft
    NEED_AFT,
    ///Set direct tuned
    SET_DIRECT_TUNED,
    ///Set AFT offset
    SET_AFT_OFFSET,
    ///Enable realtime audio detection
    ENABLE_REALTIME_AUDIO_DETECTION,
    ///Set station name
    SET_STATION_NAME,
    ///Set sorting priority
    SET_SORTING_PRIORITY,
    ///Set channel index
    SET_CHANNEL_INDEX,
#if (ISDB_SYSTEM_ENABLE == 1)
    ///Get ATV auto color
    SET_ATV_AUTOCOLOR,
#endif
    ///Set misc
    SET_MISC

} EN_SET_PROGRAM_INFO;

/// Get Program Ctrl;
typedef enum
{
    ///Get current program number
    GET_CURRENT_PROGRAM_NUMBER = 0,
    ///Get first program number
    GET_FIRST_PROGRAM_NUMBER,
    ///Get next program number
    GET_NEXT_PROGRAM_NUMBER,
    ///Get prev program number
    GET_PREV_PROGRAM_NUMBER,
    ///Get past program number
    GET_PAST_PROGRAM_NUMBER,
    ///Is program number active
    IS_PROGRAM_NUMBER_ACTIVE,
    ///Is program empty
    IS_PROGRAM_EMPTY,
    ///Get active program count
    GET_ACTIVE_PROGRAM_COUNT,
    ///Get non skip program count
    GET_NON_SKIP_PROGRAM_COUNT,
    ///Get channel max
    GET_CHANNEL_MAX,
    ///Get channel min
    GET_CHANNEL_MIN

} EN_GET_RPOGRAM_CTRL;

/// Set Rpogram Ctrl
typedef enum
{
    /// Reset channel data
    RESET_CHANNEL_DATA = 0,
    /// Init all channel data
    INIT_ALL_CHANNEL_DATA,
    /// Set current program number
    SET_CURRENT_PROGRAM_NUMBER,
    /// Inc current program number
    INC_CURRENT_PROGRAM_NUMBER,
    /// Dec current program number
    DEC_CURRENT_PROGRAM_NUMBER,
    /// Delete program
    DELETE_PROGRAM,
    /// Move program
    MOVE_PROGRAM,
    /// Swap program
    SWAP_PROGRAM,
    /// Copy program
    COPY_PROGRAM,
    /// Move program By Channel List Index Number
    MOVE_PROGRAM_BY_CH_LIST

} EN_SET_RPOGRAM_CTRL;

/// COMMON COMMAND;
typedef enum
{
    /// Initial atv data manager
    INITIAL_ATV_DATA_MANAGER = 0,
    /// Reset atv data manager
    RESET_ATV_DATA_MANAGER,
    /// Sort program
    SORT_PROGRAM,
    /// Convert program number to ordinal number
    CONVERT_PROGRAM_NUMBER_TO_ORDINAL_NUMBER,
    /// Convert ordinal number to program number
    CONVERT_ORDINAL_NUMBER_TO_PROGRAM_NUMBER,
    /// Get audio mode
    GET_AUDIO_MODE,
    /// Set audio mode
    SET_AUDIO_MODE

} EN_COMMON_COMMAND;

/// Extend Command
typedef enum
{
    /// Get list page number
    GET_LIST_PAGE_NUMBER = 0,
    /// Set list page number
    SET_LIST_PAGE_NUMBER,

} EN_EXTEND_COMMAND;

/// ATV Database lock type
typedef enum
{
    /// read lock
    EN_ATV_DB_READ_LOCK = 0,
    ///  write lock
    EN_ATV_DB_WRITE_LOCK,
} EN_ATV_DB_LOCK_TYPE;

/*@ <Definitions> @*/

/// The class for MSrv ATV database
/// @code
///
///    Init();
///    
///    if(NULL == GetProgramInfo())
///    {
///        // there is problem to get program information, do error handling here
///    }
///
///	   if(NULL == SetProgramInfo())
///    {
///        // there is problem to set program information, do error handling here
///    }
///
///	   if(FALSE == Finalize())
///    {
///        // there is problem to destory atv database, do error handling here
///    }
///
/// @endcode  
class MSrv_ATV_Database : public MSrv
{
public:
    ///Picture Item
    typedef enum
    {
        /// Brightness
        PICTURE_ITEM_BRIGHTNESS = 0,
        /// Contrast
        PICTURE_ITEM_CONTRAST,
        /// Saturation
        PICTURE_ITEM_SATURATION,
        /// Sharpness
        PICTURE_ITEM_SHARPNESS,
        /// Hue
        PICTURE_ITEM_HUE,
        /// Temnum
        PICTURE_ITEM_NUM
    } PICTURE_ITEM;

    /// Medium type
    typedef enum
    {
        ///< Medium type Cable
        MEDIUM_CABLE,
        ///< Medium type Air
        MEDIUM_AIR
    } MEDIUM;

    /// Database Type
    typedef enum
    {
        /// ATV Database invalid
        DB_INVALID=-1,
        /// ATV Database for dvbt
        DVBT_DB=0,
        /// ATV Database for dvbc
        DVBC_DB
    } DB_TYPE;


public:
    // ------------------------------------------------------------
    // public operations
    // ------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------
    /// Constructor of MSrv_ATV_Database.
    /// @return  None
    //-------------------------------------------------------------------------------------------------
    MSrv_ATV_Database();
    //-------------------------------------------------------------------------------------------------
    /// Destructor of MSrv_ATV_Database.
    /// @return  None
    //-------------------------------------------------------------------------------------------------
    virtual ~MSrv_ATV_Database();

    //-------------------------------------------------------------------------------------------------
    /// To initialize the ATV Database
    /// @return None
    //-------------------------------------------------------------------------------------------------
    virtual void Init(void);

    //-------------------------------------------------------------------------------------------------
    /// To finalize the ATV Database
    /// @return                 \b OUT: True, or False
    //-------------------------------------------------------------------------------------------------
    virtual BOOL Finalize(void);

    //new interface  //must;;
    //-------------------------------------------------------------------------------------------------
    /// To get favorite program, to check is the given program number is favorite
    /// @param  Cmd             \b IN: see type EN_GET_FAVORITE_CMD. Get First Program, Get Last Program...
    /// @param  u16Program       \b IN: program number only used while using cmd GET_PREVIOUS_FAVORITE_PROGRAM, GET_NEXT_FAVORITE_PROGRAM,GET_STATUS_IS_FAVORITE_PROGRAM
    /// @param  u16Param3       \b IN: no use so far.
    /// @param  *pVoid          \b no use so far.
    /// @return                 \b OUT: True, or False
    /// @code
    ///
    ///
    ///    U8 u8FirstProgramNumber = GetFavoriteProgram(GET_FIRST_FAVORITE_PROGRAM, 0, 0, NULL);
    ///
    ///    U8 u8LastProgramNumber = GetFavoriteProgram(GET_LAST_FAVORITE_PROGRAM, 0, 0, NULL);
    ///
    ///    U8 u8PreviousProgramNumber = GetFavoriteProgram(GET_PREVIOUS_FAVORITE_PROGRAM, const U16 u16Program, 0, NULL);
    ///
    ///    if(FALSE != GetFavoriteProgram(GET_STATUS_IS_FAVORITE_PROGRAM, const U16 u16Program, 0, NULL))
    ///    {
    ///        //the given program number is favorite.
    ///    }
    ///     
    /// @endcode  
    //-------------------------------------------------------------------------------------------------
    virtual U8 GetFavoriteProgram(const EN_GET_FAVORITE_CMD Cmd, const U16 u16Program, const U16 u16Param3, const void *pVoid);

    //-------------------------------------------------------------------------------------------------
    /// To Set favorite program, Set the given program number to favorite
    /// @param  Cmd             \b IN: there's only one cmd: SET_FAVORITE_PROGRAM
    /// @param  u16Program       \b IN: program number
    /// @param  u8Favorite       \b IN: program favorite setting
    /// @param  *pVoid          \b no use so far.
    /// @return                 \b OUT: True, or False
    /// @code
    ///
    ///    if(FALSE == SetFavoriteProgram(SET_FAVORITE_PROGRAM, const U16 u16Program,U8 u8Favorite, NULL))
    ///    {
    ///        //set the given program number to favorate is fail, do error handling here
    ///    }
    ///
    /// @endcode  
    //-------------------------------------------------------------------------------------------------
    virtual U8 SetFavoriteProgram(EN_SET_FAVORITE_CMD Cmd, U16 u16Program, U8 u8Favorite, void *pVoid);

    //-------------------------------------------------------------------------------------------------
    /// To get program information
    /// @param  Cmd             \b IN: GET_PROGRAM_PLL_DATA, IS_AFT_NEED, GET_AUDIO_STANDARD, GET_VIDEO_STANDARD,...see EN_GET_PROGRAM_INFO
    /// @param  u16Program       \b IN: program number
    /// @param  u16Param3       \b IN: no use so far.
    /// @param  *pVoid          \b OUT: for GET_STATION_NAME and GET_MISC.
    /// @return                 \b OUT: True, or False
    /// @code
    ///
    ///    MAPI_AVD_VideoStandardType enVideoStandard = GetProgramInfo(GET_VIDEO_STANDARD_OF_PROGRAM, u8ChannelNumber, 0, NULL);
    ///    
    ///    if(FALSE==GetProgramInfo(GET_STATION_NAME , u8CurrentProgramNumber , NULL , m_au8CurrentStationName))
    ///    {
    ///        //there is problem when get the given channel number's station name,do error handling here.
    ///    }
    ///
    /// @endcode	
    //-------------------------------------------------------------------------------------------------
    virtual U16 GetProgramInfo(const EN_GET_PROGRAM_INFO Cmd, const U16 u16Program, const U16 u16Param3, void *pVoid);

    //-------------------------------------------------------------------------------------------------
    /// To set program information
    /// @param  Cmd             \b IN: SET_PROGRAM_PLL_DATA, AFT_NEED, SET_AUDIO_STANDARD, SET_VIDEO_STANDARD,...see EN_SET_PROGRAM_INFO
    /// @param  u16Program       \b IN: program number
    /// @param  u16Param3       \b IN: every cmd except "SET_STATION_NAME, SET_MISC" need pass this parameter to store the information
    /// @param  *pVoid          \b OUT: for SET_STATION_NAME and SET_MISC.
    /// @return                 \b OUT: True, or False
    /// @code
    ///
    ///    if(FALSE==SetProgramInfo(SET_AUDIO_STANDARD, u16Program, u8AudioStandard, NULL))
    ///    {
    ///        //there is problem when set the given channel number's audio standard,do error handling here.
    ///    }
    ///
    ///    if(FALSE==SetProgramInfo(LOCK_PROGRAM, u16Program, TRUE, NULL))
    ///    {
    ///        //there is problem when lock the given channel number ,do error handling here.
    ///    }
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual U16 SetProgramInfo(EN_SET_PROGRAM_INFO Cmd, U16 u16Program, U16 u16Param3, const void *pVoid);

    //-------------------------------------------------------------------------------------------------
    /// To get control program information
    /// @param  Cmd             \b IN: GET_CURRENT_PROGRAM_NUMBER, GET_FIRST_PROGRAM_NUMBER, GET_NEXT_PROGRAM_NUMBER, GET_PREV_PROGRAM_NUMBER, GET_PAST_PROGRAM_NUMBER, IS_PROGRAM_NUMBER_ACTIVE,IS_PROGRAM_EMPTY,  GET_ACTIVE_PROGRAM_COUNT, GET_CHANNEL_MAX, GET_CHANNEL_MIN
    /// @param  u16Program       \b IN: program number
    /// @param  u16Param3       \b IN:
    /// @param  *pVoid          \b OUT:
    /// @return                 \b OUT: True, or False
    /// @code
    ///
    ///     u16 u16CurrentChannel = GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL);
    ///
    ///     if(FALSE == GetProgramCtrl(IS_PROGRAM_NUMBER_ACTIVE , u8Index , 0 , NULL))
    ///     {
    ///         //the given index program in atv database is not active. 
    ///     }
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual U16 GetProgramCtrl(const EN_GET_RPOGRAM_CTRL Cmd, const U16 u16Program, const U16 u16Param3, const void *pVoid);

    //-------------------------------------------------------------------------------------------------
    /// To set control program information
    /// @param  Cmd             \b IN: RESET_CHANNEL_DATA,  SET_CURRENT_PROGRAM_NUMBER,INC_CURRENT_PROGRAM_NUMBER,DEC_CURRENT_PROGRAM_NUMBER,DELETE_PROGRAM,MOVE_PROGRAM,SWAP_PROGRAM,COPY_PROGRA
    /// @param  u16Program       \b IN: program number
    /// @param  u16Param3       \b IN: program number
    /// @param  *pVoid          \b OUT:
    /// @return                 \b OUT: True, or False
    /// @code
    ///
    ///     if(FALSE == SetProgramCtrl(DELETE_PROGRAM, u16Program, 0, NULL))
    ///     {
    ///         //delete given program fail, do error handling here
    ///     }
    ///
    ///     if(FALSE == SetProgramCtrl(RESET_CHANNEL_DATA, 0, 0,NULL))
    ///     {
    ///         //reset channel data fail, do error handling here. 
    ///     }
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual U16 SetProgramCtrl(const EN_SET_RPOGRAM_CTRL Cmd, const U16 u16Program, const U16 u16Param3, const void *pVoid);

    //-------------------------------------------------------------------------------------------------
    /// Common Comand to ATV Database
    /// @param  Cmd             \b IN:
    ///                             INITIAL_ATV_DATA_MANAGER
    ///                             RESET_ATV_DATA_MANAGER
    ///                             SORT_PROGRAM,
    ///                             CONVERT_PROGRAM_NUMBER_TO_ORDINAL_NUMBER,
    ///                             CONVERT_ORDINAL_NUMBER_TO_PROGRAM_NUMBER,
    ///                             GET_AUDIO_MODE,
    ///                             SET_AUDIO_MODE
    /// @param  u16Program       \b IN: program number
    /// @param  u16Param3       \b IN: program number
    /// @param  *pVoid          \b OUT:
    /// @return                 \b OUT: True, or False
    /// @code
    ///
    ///     if(FALSE == CommondCmd(SET_AUDIO_MODE, 0, (U16)E_AUDIOMODE_FORCED_MONO_, NULL))
    ///     {
    ///         //set audio mode fail, do error handling here
    ///     }
    ///
    ///     //init atv data manager
    ///     CommondCmd(INITIAL_ATV_DATA_MANAGER , 0 , 0 , NULL);
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual U16 CommondCmd(const EN_COMMON_COMMAND Cmd, const U16 u16Program, const U16 u16Param3, void *pVoid);

    //-------------------------------------------------------------------------------------------------
    /// Extend Command
    /// @param  Cmd             \b IN: GET_LIST_PAGE_NUMBER SET_LIST_PAGE_NUMBER
    /// @param  u16Program       \b IN: program number
    /// @param  u16Param3       \b IN: program number
    /// @param  *pVoid          \b OUT:
    /// @return                 \b OUT: True, or False
    /// @code
    ///
    ///     if(FALSE == ExtendCmd(GET_LIST_PAGE_NUMBER, u16Program, 0, void *pVoid))
    ///     {
    ///         //get list page number fail, do error handling here
    ///     }
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual U16 ExtendCmd(const EN_EXTEND_COMMAND Cmd, const U16 u16Program, const U16 u16Param3, void *pVoid);

#if 0
    // for check boundaries
    virtual U16 msAPI_CFT_UHFMaxPLL(void);
    virtual U16 msAPI_CFT_VHFLowMinPLL(void);
    virtual U16 msAPI_CFT_VHFHighMinPLL(void);
    virtual U16 msAPI_CFT_UHFMinPLL(void);

    virtual BOOLEAN MApp_ATVProc_IsThisATVOptionEnabled(THISATV_OPTION eOption);
#endif

#if (ATSC_SYSTEM_ENABLE == 1 || ISDB_SYSTEM_ENABLE == 1)
    //-------------------------------------------------------------------------------------------------
    /// Set NTSC Antenna (Only work when ATSC is turn on)
    /// @param  eAntenna         \b IN: MEDIUM_CABLE, MEDIUM_AIR
    /// @return                 \b OUT: True, or False
    /// @code
    ///
    ///    if(FALSE==SetNTSCAntenna(MEDIUM_CABLE))
    ///    {
    ///        //there is problem to set NTSC antenna, do error handling here
    ///    }
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual BOOL SetNTSCAntenna(MEDIUM eAntenna);

    //-------------------------------------------------------------------------------------------------
    /// Set  Antenna 
    /// @param  eAntenna         \b IN: MEDIUM
    /// @return                 \b OUT: True, or False
    //-------------------------------------------------------------------------------------------------
	virtual BOOL SetAntennaType(MEDIUM eAntenna);

    //-------------------------------------------------------------------------------------------------
    /// Get NTSC Antenna (Only work when ATSC is turn on)
    /// @return                     \b OUT: MEDIUM
    /// @code
    ///
    ///    MEDIUM AntennaType = GetNTSCAntenna();
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual MEDIUM GetNTSCAntenna();
#endif
	
    //-------------------------------------------------------------------------------------------------
    /// Connect Different Type Database: for DVBT or DVBC
    /// @param  DB       \b IN: DB_TYPE
    /// @return                 \b OUT:connect sucess True, or False
    /// @code
    ///
    ///     if(FALSE == ConnectDatabase(DVBT_DB))
    ///     {
    ///         //connect atv database fail, do error handling here
    ///     }
    ///
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    virtual BOOL ConnectDatabase(DB_TYPE DB);

   #if (ISDB_SYSTEM_ENABLE == 1)
   //------------------------------------------------------------------------------
   /// -This function will check if Connect ISDB Database is True
   /// @param DB \b IN: DB_TYPE
   /// @return BOOL \b OUT: ISDB database  connect or not
   /// @code
   ///
   ///     if(FALSE == ISDBCheckDatabase(DVBT_DB))
   ///     {
   ///         //connect ISDB atv database fail, do error handling here
   ///     }
   ///
   /// @endcode
   //------------------------------------------------------------------------------
   virtual BOOL ISDBCheckDatabase(DB_TYPE DB);
   
   //------------------------------------------------------------------------------
   /// -This function will check if the specific program is IsAutoColorSystem
   /// @param u8ProgramNumber \b IN: program number
   /// @return BOOL \b OUT:AUTO color system or manual color system
   /// - TRUE: AUTO
   /// - FALAS: Manual
   /// @code
   ///
   ///     if(FALSE==ATVIsProgramAutoColorSystem(u16Program))
   ///     {
   ///         //the specify program is not aoto color system.
   ///     }
   ///
   /// @endcode
   //------------------------------------------------------------------------------
   virtual BOOL ATVIsProgramAutoColorSystem(U8 u8ProgramNumber);

   //------------------------------------------------------------------------------
   /// -This function will set the specific program auto color system or not
   /// @param u8ProgramNumber \b IN: program number
   /// @param bIsAutoColorSystem \b IN:autoColorSystem or not
   /// @return None
   /// @code
   ///
   ///     ATVProgramAutoColorSystem(u8Program, TRUE);
   ///
   /// @endcode
   //------------------------------------------------------------------------------
   virtual void ATVProgramAutoColorSystem(U8 u8ProgramNumber, BOOL bIsAutoColorSystem);
   
    //------------------------------------------------------------------------------
    /// -This function will  ATV_MapChanToFreq
    /// @param u8ProgramNumber \b IN: CH
    /// @return U16 \b OUT: wPLL value
    /// @code
    ///
    ///    U16 wPLL = ATV_MapChanToFreq(U8 u8ProgramNumber);
    ///
    /// @endcode
    //------------------------------------------------------------------------------
    virtual U16 ATV_MapChanToFreq(U8 u8ProgramNumber);
   #endif
   //------------------------------------------------------------------------------
   /// -This function will get max channel numbers if you set different antenna.
   /// @return U8 \b OUT: max number of atv programs
   /// @code
   ///
   ///     U8 u8MaxChannels = ATVGetChannelMax();
   ///
   /// @endcode
   //------------------------------------------------------------------------------
   virtual U8 ATVGetChannelMax(void);
   
   //------------------------------------------------------------------------------
   /// -This function will get min channel numbers if you set different antenna.
   /// @return U8 \b OUT: min number of atv programs
   /// @code
   ///
   ///     U8 u8MaxChannels = ATVGetChannelMin();
   ///
   /// @endcode
   //------------------------------------------------------------------------------
   virtual U8 ATVGetChannelMin(void);

private:
    // for thread safe
    pthread_rwlock_t pthreadRWLock;
    pthread_t pthreadIdWrLock;

    U16 _u16Identification;

    // PROGRAM DATA
    U8  _u8ATVPRIndexTable[MAX_NUMBER_OF_ATV_PROGRAM];
    U8  _u8PRTableMap[MAX_PRTABLEMAP];
    U8  _u8CurrentProgramNumber;
    U8  _u8PastProgramNumber;
    U8  sNullStationName[MAX_STATION_NAME];
    U16 m_wDefaultListPageNumber[MAX_LISTPAGE];
    MEDIUM m_Antenna;

    //local RAM for cacheing database
    ST_ATV_PROGRAM_DATA_STRUCTURE stProgramData;

    U16 m_u16DefaultPll;

    virtual void _CorrectDuplication(void);
    virtual void _CorrectMapping(void);
    virtual BOOL _GetPRTable(U8 u8TBIndex, U8 * pu8Buffer, U8 u8Param);
    virtual BOOL _SetPRTable(U8 u8TBIndex, const U8 * pu8Buffer, U8 u8Param);
    virtual BOOL _CopyPRTable(U8 u8TBIndex);
    virtual BOOL _DeletePRTable(U8 u8TBIndex);
    virtual BOOL _MovePRTable(U8 u8FromTBIndex, U8 u8ToTBIndex);
    virtual BOOL _SwapPRTable(U8 u8TBIndex1, U8 u8TBIndex2);
    virtual U8 _GetPRIndexTable(U8 u8TBIndex);
    virtual void _SetPRIndexTable(U8 u8TBIndex, U8 u8PRIndex);
    virtual BOOL _IsPREntityActive(U8 u8PRIndex);
    virtual void _ActivePREntity(U8 u8PRIndex, BOOL bActive);
    virtual BOOL _IsIndexActive(U8 u8TBIndex);
    virtual U8 _GetEmptyPREntity(void);
    virtual void _FillPREntityWithDefault(U8 u8PRIndex);
    virtual U8 _LoadProgramNumber(void);
    virtual void _SaveProgramNumber(U8 u8ProgramNumber);
    //virtual BOOL _GetPercentOfPictureItem(EN_PICTURE_QUALITY_ITEMS ePictureItem, MAPI_AVD_InputSourceType eVideoSource, U8 *pu8Percent);
    //virtual BOOL _SetPercentOfPictureItem(EN_PICTURE_QUALITY_ITEMS ePictureItem, MAPI_AVD_InputSourceType eVideoSource, U8 u8Percent);
    virtual U16 _GetListPageNumber(U8 *pu8Buffer, U8 u8ListIndex);
    virtual void _SetListPageNumber(U8 *pu8Buffer, U8 u8ListIndex, U16 u16ListPageNumber);
    virtual BOOL _GetNVRAM(U32 u32Address, U8 * pu8Buffer, U16 u16Size);
    virtual BOOL _GetNVRAM(U32 u32Address, U8 * pu8Buffer, U16 u16Size, EN_ATV_DB_LOCK_TYPE eType);
    virtual BOOL _SetNVRAM(U32 u32Address, const U8 *pu8Buffer, U16 u16Size);
    virtual void _StringCopy(U8 *pu8Dst, U8 *pu8Src, U8 u8Size);


    // for Favorate Program
    virtual void ATVSetFavoriteProgram(U8 u8ProgramNumber, U8 u8Favorite);
    virtual U8 ATVGetFirstFavoriteProgramNumber(void);
    virtual U8 ATVGetLastFavoriteProgramNumber(void);
    virtual U8 ATVGetPreviousFavoriteProgramNumber(U8 u8BaseProgramNumber);
    virtual U8 ATVGetNextFavoriteProgramNumber(U8 u8BaseProgramNumber);
    virtual U8 ATVGetTotalFavoriteProgramCount(void);
    virtual BOOL ATVIsProgramFavorite(U8 u8ProgramNumber);

    //GetProgramInfo
    virtual S8 GetFineTune(U8 u8ProgramNumber);
    virtual U16 ATVGetProgramPLLData(U8 u8ProgramNumber);
    virtual EN_MSRV_AUDIOSTANDARD_TYPE ATVGetAudioStandard(U8 u8ProgramNumber);
    virtual MAPI_AVD_VideoStandardType ATVGetVideoStandardOfProgram(U8 u8ProgramNumber);
    virtual U8 ATVGetDualAudioSelected(void);
    virtual BOOL ATVIsProgramSkipped(U8 u8ProgramNumber);
    virtual BOOL ATVIsProgramLocked(U8 u8ProgramNumber);
    virtual BOOL ATVIsProgramHide(const U8 u8ProgramNumber);
    virtual BOOL ATVIsAFTNeeded(U8 u8ProgramNumber);
    virtual BOOL ATVIsDirectTuned(const U8 u8ProgramNumber);
    virtual BOOL ATVIsRealtimeAudioDetectionEnabled(U8 u8ProgramNumber);
    virtual BOOL ATVGetStationName(U8 u8ProgramNumber, U8 *sName);
    virtual U8 ATVGetSortingPriority(U8 u8ProgramNumber);
    virtual BOOL ATVGetMISC(U8 u8ProgramNumber, U8 *pu8Misc);
    virtual BOOL ATVGetPll(U8 u8ProgramNumber, U8 *pu8Pll);


    //SetProgramInfo
    virtual void ATVSetProgramPLLData(U8 u8ProgramNumber, U16 wPLL);
    virtual void ATVSetAudioStandard(U8 u8ProgramNumber, EN_MSRV_AUDIOSTANDARD_TYPE eStandard);
    virtual void ATVSetVideoStandardOfProgram(U8 u8ProgramNumber, MAPI_AVD_VideoStandardType eStandard);
    virtual void ATVSkipProgram(U8 u8ProgramNumber, BOOL bSkip);
    virtual U16 ATVGetNonSkipProgramCount(void);
    virtual void ATVHideProgram(U8 u8ProgramNumber, BOOL bHide);
    virtual void ATVLockProgram(U8 u8ProgramNumber, BOOL bIsLock);
    virtual void ATVNeedAFT(U8 u8ProgramNumber, BOOL bNeed);
    virtual void ATVSetDirectTune(const U8 u8ProgramNumber, const BOOL bSetDirectTune);
    virtual U8 ATVGetAftOffset(U8 u8ProgramNumber);
    virtual void ATVSetAftOffset(U8 u8ProgramNumber, U8 u8AftOffsetValue);
    virtual void ATVEnableRealtimeAudioDetection(U8 u8ProgramNumber, BOOL bIsRealtimeAudioDetectionEnabled);
    virtual BOOL ATVSetStationName(U8 u8ProgramNumber, const U8 *sName);
    virtual BOOL ATVSetSortingPriority(U8 u8ProgramNumber, U8 u8Priority);
    virtual BOOL ATVSetMISC(U8 u8ProgramNumber, const U8 *pu8Misc);

    //GetProgramCtrl
    virtual U8 ATVGetFirstProgramNumber(BOOL bIncludeSkipped);
    virtual U8 ATVGetNextProgramNumber(U8 u8BaseProgramNumber, BOOL bIncludeSkipped);
    virtual U8 ATVGetPrevProgramNumber(U8 u8BaseProgramNumber, BOOL bIncludeSkipped);
    virtual U8 ATVGetPastProgramNumber(void);
    virtual BOOL ATVIsProgramNumberActive(U8 u8ProgramNumber);
    virtual BOOL ATVIsProgramEmpty(void);
    virtual U8 ATVGetActiveProgramCount(void);
    virtual U8 ATVGetCurrentProgramNumber(void);

    //SetProgramCtrl
    virtual void InitailAllChannelData(void);
    virtual void ResetChannelData(void);
    virtual void ATVSetCurrentProgramNumber(U8 u8ProgramNumber);
    virtual BOOL ATVIncCurrentProgramNumber(void);
    virtual BOOL ATVDecCurrentProgramNumber(void);
    virtual BOOL ATVDeleteProgram(U8 u8ProgramNumber);
    virtual BOOL ATVMoveProgram(U8 u8Source, U8 u8Target);
    virtual BOOL ATVMoveProgramByCHListIndex(U8 u8SourceIndex, U8 u8TargetIndex);
    virtual BOOL ATVSwapProgram(U8 u8ProgramNumber1, U8 u8ProgramNumber2);
    virtual BOOL ATVCopyProgram(void);

    //CommondCmd
    virtual void InitATVDataManager(void);
    virtual void ResetATVDataManager(void);
    virtual void ATVSortProgram(U8 u8Start, U8 u8Stop);
    virtual U8 ATVConvertProgramNumberToOrdinalNumber(U8 u8ProgramNumber);
    virtual U8 ATVConvertOrdinalNumberToProgramNumber(U16 u16OrdinalNumber);
    virtual BOOL ATVGetAudioMode(AUDIOMODE_TYPE_ *peAudioMode);
    virtual BOOL ATVSetAudioMode(AUDIOMODE_TYPE_ eAudioMode);

    //ExtendCmd
    virtual BOOL ATVGetListPageNumber(U8 u8ListIndex, U16 *pwListPageNumber);
    virtual BOOL ATVSetListPageNumber(U8 u8ListIndex, U16 wListPageNumber);



    virtual BOOL DisConnectDatabase();
    DB_TYPE m_dbType;
    mapi_storage* m_pDbStorage;
    #if (MSTAR_TVOS == 1)
    mapi_storage* m_pDbStorage_backup;
    #endif
protected:
    //------------------------------------------------------------------------------
    /// Get Default Video Standard of Program
    /// @return MAPI_AVD_VideoStandardType : Default Video standard
    //------------------------------------------------------------------------------
    virtual MAPI_AVD_VideoStandardType _getDefaultVideoStandardOfProgram();
};
/*@ </Definitions> @*/


/*@ <IncludeGuardEnd> @*/
#endif // MSrv_ATV_Database_H
/*@ </IncludeGuardEnd> @*/
