
/**********************************************************************
 Copyright (c) 2006-2009 MStar Semiconductor, Inc.
 All rights reserved.

 Unless otherwise stipulated in writing, any and all information contained
 herein regardless in any format shall remain the sole proprietary of
 MStar Semiconductor Inc. and be kept in strict confidence
 (MStar Confidential Information) by the recipient.
 Any unauthorized act including without limitation unauthorized disclosure,
 copying, use, reproduction, sale, distribution, modification, disassembling,
 reverse engineering and compiling of the contents of MStar Confidential
 Information is unlawful and strictly prohibited. MStar hereby reserves the
 rights to any and all damages, losses, costs and expenses resulting therefrom.

* Class : MSrv_SSSound
* File  : MSrv_SSSound.h
**********************************************************************/

///////////////////////////////////////////////////////////////////////////////////////////////////
/// @file MSrv_SSSound.h
/// @brief\b Sound related interface.
/// @author MStar Semiconductor Inc.
///
/// Sound related interface is used to change certain kind of sound property.
///
/// Features:
/// - Provide sound related functions.
///////////////////////////////////////////////////////////////////////////////////////////////////

/*@ <Include> @*/
#include "mapi_types.h"
#include "mapi_audio_datatype.h"
#include "mapi_audio_amp.h"
#include "MSrv.h"
#include "mapi_syscfg_table.h"
#include "MSrv_MountNotifier.h"
/*@ </Include> @*/

/*@ <Definitions> @*/

/// Audio output port source setting
typedef struct
{
    MAPI_AUDIO_PROCESSOR_TYPE SpeakerOut; ///<  for Speaker
    MAPI_AUDIO_PROCESSOR_TYPE HpOut;          ///<  for HP
    MAPI_AUDIO_PROCESSOR_TYPE MonitorOut; ///<  for Monitor out
    MAPI_AUDIO_PROCESSOR_TYPE ScartOut;     ///<  for Scart out
    MAPI_AUDIO_PROCESSOR_TYPE SpdifOut;     ///<  for SPDIF out
}MSRV_AUDIO_OUTPORT_SOURCE_INFO;





/// Define Audio debug level
typedef enum
{
    AUDIO_DEBUG_LEVEL_OFF = 0x0,
    AUDIO_DEBUG_LEVEL_AVSYNC = 0x1,
    AUDIO_DEBUG_LEVEL_DEC1 = 0x2,
    AUDIO_DEBUG_LEVEL_IOINFO = 0x4,
    AUDIO_DEBUG_LEVEL_ALL = AUDIO_DEBUG_LEVEL_AVSYNC | AUDIO_DEBUG_LEVEL_DEC1 | AUDIO_DEBUG_LEVEL_IOINFO,
} E_AUDIO_DBGLVL;

/// Define Surround mode type
typedef enum
{
    ///Surround mode type is OFF
    E_SURROUND_MODE_OFF,
    ///Surround mode type is ON
    E_SURROUND_MODE_ON,
    ///Surround mode type is SRS TSXT
    E_SURROUND_MODE_SRS_TSXT,
#if (AU_SUPPORT_DBX == 1)
    ///Surround mode type is DBX
    E_SURROUND_MODE_DBX,
#endif
} SOUND_SURROUND_MODE;

/// Define sif Hidev filter bw level.
typedef enum
{
    ///Hidev filter off
    E_SOUND_HIDEV_OFF,
    ///Hidev filter bw level 1
    E_SOUND_HIDEV_BW_L1,
    ///Hidev filter bw level 2
    E_SOUND_HIDEV_BW_L2,
    ///Hidev filter bw level 3
    E_SOUND_HIDEV_BW_L3,
    ///Hidev filter bw level numbers
    E_SOUND_HIDEV_BW_MAX
} SOUND_HIDEV_INDEX;

/// Define Sound mute type.
typedef enum
{
    ///Set sound mute type for permanent
    MUTE_PERMANENT          = BIT0,
    ///Set sound mute type for moment
    MUTE_MOMENT             = BIT1,
    ///Set sound mute type for by user
    MUTE_BYUSER             = BIT2,
    ///Set sound mute type for by sync
    MUTE_BYSYNC             = BIT3,
    ///Set sound mute type for by chip
    MUTE_BYVCHIP            = BIT4,
    ///Set sound mute type for by block
    MUTE_BYBLOCK            = BIT5,
    ///Set sound mute type for interanl1
    MUTE_INTERNAL1          = BIT6,
    ///Set sound mute type for interanl2
    MUTE_INTERNAL2          = BIT7,
    ///Set sound mute type for interanl3
    MUTE_INTERNAL3          = BIT8,
    ///Set sound mute type for during limited time
    MUTE_DURING_LIMITED_TIME = BIT9,
    ///Set sound mute type for mhegap
    MUTE_MHEGAP             = BIT10,
    ///Set sound mute type for CI
    MUTE_CI                 = BIT11,
    ///Set sound mute type for Scan
    MUTE_SCAN               = BIT12,
    ///Set sound mute type for source switch
    MUTE_SOURCESWITCH       = BIT13,
    ///Set sound mute type for user speaker
    MUTE_USER_SPEAKER       = BIT14,
    ///Set sound mute type for user hp
    MUTE_USER_HP            = BIT15,
    ///Set sound mute type for user spdif
    MUTE_USER_SPDIF         = BIT16,
    ///Set sound mute type for user scart1
    MUTE_USER_SCART1        = BIT17,
    ///Set sound mute type for user scart2
    MUTE_USER_SCART2        = BIT18,
    ///Set all sound mute type
    MUTE_ALL                = BIT19,
    ///Set sound mute type for data input (From Alsa)
    MUTE_USER_DATA_IN       = BIT20,
    ///Set sound mute type for capture1
    MUTE_USER_PCM_CAPTURE1  = BIT21,
    ///Set sound mute type for capture2
    MUTE_USER_PCM_CAPTURE2  = BIT22,
    ///Set sound mute by APP
    MUTE_BY_APP             = BIT23,
    ///Set sound mute type for input source lock
    MUTE_INPUT_SOURCEL_LOCK = BIT24,
}  SSSOUND_MUTE_TYPE;


/// Define port for mute/volume control (MSrv layer)
typedef enum
{
    ///Access Main Speaker out
    PORT_SPEAKER,
    ///Access HP out
    PORT_HP,
    ///Access Monitor out
    PORT_MONITOR_OUT,
    ///Access scart out
    PORT_SCART,
    ///Access S/PDIF out
    PORT_SPDIF,
    ///Access HDMI ARC out
    PORT_ARC,
    ///Access main channel port (Mixer)
    PORT_MAIN_CHANNEL,
    ///Access 2nd channel port (Mixer)
    PORT_SECOND_CHANNEL,
    ///Access data reader
    PORT_DATA_READER,
    ///Access user defined port 1
    PORT_USER_DEFINE1,
    ///Access user defined port 2
    PORT_USER_DEFINE2,
    ///Access user defined port 3
    PORT_USER_DEFINE3,
    ///Max internal port Number
    PORT_MAX,
} AUDIO_BASIC_PORT_TYPE;


///Define the max number of sound mute type
#define MAX_MUTE_TYPE_NUM 24

#if (MSTAR_TVOS == 1)
///Define the default channel volume compensation value
#define DEFAULT_CHANNEL_VOLUME_COMPENSATION 10
#endif

/// Define Audio Output Path type.
typedef enum
{
    ///Set Audio Output path type is MainSpeaker
    PATH_MAIN_SPEAKER,
    ///Set Audio Output path type is HeadPhone
    PATH_HP,
    ///Set Audio Output path type is LineOut
    PATH_LINEOUT,
    ///Set Audio Output path type is SCART-1
    PATH_SCART1,
    ///Set Audio Output path type is SCART-2
    PATH_SCART2,
} SSSOUND_PATH_TYPE;

/// Define Audio Processor(For Main/Sub/Scart sound)  type.
typedef enum
{
    MSRV_AUDIO_PROCESSOR_MAIN,            ///< 0: For Main window
    MSRV_AUDIO_PROCESSOR_SUB,              ///< 1: For Sub window
    MSRV_AUDIO_PROCESSOR_SCART,          ///< 2: For Scart out
    MSRV_AUDIO_PROCESSOR_MAX,              ///< audio processor max. number
} MSRV_AUDIO_PROCESSOR_TYPE;      // for PIP application

/// Audio Data Capture Device Selection
typedef enum
{
    /// 1st audio device
    MSRV_CAPTURE_DEVICE_TYPE_DEVICE0,
    /// 2nd audio device
    MSRV_CAPTURE_DEVICE_TYPE_DEVICE1,
    /// 3rd audio device
    MSRV_CAPTURE_DEVICE_TYPE_DEVICE2,
    /// 4th audio device
    MSRV_CAPTURE_DEVICE_TYPE_DEVICE3,
    /// 5th audio device
    MSRV_CAPTURE_DEVICE_TYPE_DEVICE4,
    /// 6th audio device
    MSRV_CAPTURE_DEVICE_TYPE_DEVICE5,
} MSRV_AUDIO_CAPTURE_DEVICE_TYPE;


/// Audio Data Capture Source Selection
typedef enum
{
    /// Capure main audio sound data
    MSRV_CAPTURE_MAIN_SOUND,
    /// Capure main audio sound data
    MSRV_CAPTURE_SUB_SOUND,
    /// Capure sub audio sound data
    MSRV_CAPTURE_MICROPHONE_SOUND,
    /// Capure mixed audio sound data
    MSRV_CAPTURE_MIXED_SOUND,
    /// Capure user defined sound source1
    MSRV_CAPTURE_USER_DEFINE1 ,
    /// Capure user defined sound source2
    MSRV_CAPTURE_USER_DEFINE2,
    /// Max. capture source Number
    MSRV_CAPTURE_SOURCE_MAX,
} MSRV_AUDIO_CAPTURE_SOURCE;

///////////////////////////////////////////////////////////////////////////////////////////////////
/// Audio Sys & Sound related enum & structure is used to Java kind of sound property.
///////////////////////////////////////////////////////////////////////////////////////////////////
/// Define Audio Output Path type.
typedef enum
{
    ///Set Audio volume source type is MainSpeaker
    VOL_SOURCE_SPEAKER_OUT,
    ///Set Audio volume source type is HeadPhone
    VOL_SOURCE_HP_OUT,
    ///Set Audio volume source type is LineOut
    VOL_SOURCE_LINE_OUT,
    ///Set Audio volume source type is SCART-1
    VOL_SOURCE_SCART1_OUT,
    ///Set Audio volume source type is SCART-2
    VOL_SOURCE_SCART2_OUT,
    ///Set Audio volume source type is SPDIF
    VOL_SOURCE_SPDIF_OUT,
    ///Set Audio volume source type is capture1
    VOL_SOURCE_PCM_CAPTURE1,
    ///Set Audio volume source type is capture2
    VOL_SOURCE_PCM_CAPTURE2,
#if (MSTAR_TVOS == 1)
    ///Set Audio volume source type is Volume Compensation
    VOL_SOURCE_COMPENSATION,
#endif
} AUDIO_VOL_SOURCE_TYPE;

/// Define Audio Input Level Source Path type.
typedef enum
{
    ///Set Audio input level source type is Audio Description
    VOL_SOURCE_AUDIO_DESCRIPTION_IN,
    ///Set Audio input level type is Pre-Mixed KTV MP3
    VOL_SOURCE_PREMIXER_KTV_MP3_IN,
    ///Set Audio input level type is Pre-Mixed Mic
    VOL_SOURCE_PREMIXER_KTV_MIC_IN,
    ///Set Audio input level type is Pre-Mixed Game-1
    VOL_SOURCE_PREMIXER_GAME1_IN,
    ///Set Audio input level type is Pre-Mixed Game-2
    VOL_SOURCE_PREMIXER_GAME2_IN,
    ///Set Audio input level type is Pre-Mixed Echo-1
    VOL_SOURCE_PREMIXER_ECHO1_IN,
    ///Set Audio input level type is Pre-Mixed Echo-2
    VOL_SOURCE_PREMIXER_ECHO2_IN,
    ///Set Audio input level type is Pre-Mixed Alsa
    VOL_SOURCE_PREMIXER_ALSA_IN,
} AUDIO_INPUT_LEVEL_SOURCE_TYPE;

/// Define MSrv_SSSound Return type.
typedef enum
{
    ///Return not Okay
    RETURN_NOTOK,
    ///Return  Okay
    RETURN_OK,
    ///Return  unsupport format
    RETURN_UNSUPPORT,
} MSRV_SSSND_RET;


/// Define Audio Baisc Effect Type.
typedef enum
{
    ///PreScale
    BSND_PRESCALE,
    ///Treble
    BSND_TREBLE,
    ///Bass
    BSND_BASS,
    ///Balance
    BSND_BALANCE,
    ///EQ
    BSND_EQ,
    ///PEQ
    BSND_PEQ,
    ///AVC(AVL)
    BSND_AVC,
    ///MStar Surround
    BSND_Surround,
    ///Sound DRC
    BSND_DRC,
    /// Noise Reduction
    BSND_NR,
    /// ECHO
    BSND_ECHO,
} BSOUND_EFFECT_TYPE;

/**********************************************************************/
// EQ, PEQ
/**********************************************************************/
/// Define Basic Audio Parameter of EQ.
typedef struct ST_BSND_PARAMETER_EQ
{
    ///EQ Level
    MAPI_U16    BSND_PARAM_EQ_LEVEL;
} ST_BSND_PARAMETER_EQ;

/// Define Basic Audio Parameter of PEQ.
typedef struct ST_BSND_PARAMETER_PEQ
{
    ///PEQ GAIN
    MAPI_U16     BSND_PARAM_PEQ_GAIN;
    ///PEQ Center Freq for Each Band
    MAPI_U16    BSND_PARAM_PEQ_FC;
    ///PEQ Q Value for Each Band
    MAPI_U16    BSND_PARAM_PEQ_QVALUE;
} ST_BSND_PARAMETER_PEQ;

///Define Max EQ band number
#define MAX_EQ_BAND_NUM 5

///Define Max PEQ band number
#define MAX_PEQ_BAND_NUM 5

/// Define Audio Set Baisc Effect Parameter.
typedef struct BSND_PARAMETER
{
    ///PreScale Vaule
    MAPI_U16    BSND_PARAM_PRESCALE;
    ///Treble Vaule
    MAPI_U16    BSND_PARAM_TREBLE;
    ///Bass Value
    MAPI_U16    BSND_PARAM_BASS;
    ///Balance Value
    MAPI_U16    BSND_PARAM_TYPE_BALANCE;
    ///EQ Band Number
    MAPI_U8     BSND_PARAM_EQ_BAND_NUM;
    ///PEQ Band Number
    MAPI_U8     BSND_PARAM_PEQ_BAND_NUM;
    ///EQ
    ST_BSND_PARAMETER_EQ BSND_PARAM_EQ[MAX_EQ_BAND_NUM];
    ///PEQ
    ST_BSND_PARAMETER_PEQ BSND_PARAM_PEQ[MAX_PEQ_BAND_NUM];
    ///AVC(AVL) Threshold
    MAPI_U16    BSND_PARAM_AVC_THRESHOLD;
    ///AVC(AVL) Attach Time
    MAPI_U16    BSND_PARAM_AVC_AT;
    ///AVC(AVL) Release Time
    MAPI_U16    BSND_PARAM_AVC_RT;
    ///MStar Surround XA Value
    MAPI_U16    BSND_PARAM_MSURR_XA;
    ///MStar Surround XB Value
    MAPI_U16    BSND_PARAM_MSURR_XB;
    ///MStar Surround XK Value
    MAPI_U16    BSND_PARAM_MSURR_XK;
    ///MStar Surround LPFGAIN value
    MAPI_U16    BSND_PARAM_MSURR_LPFGAIN;
    ///Sound DRC Threshold
    MAPI_U16    BSND_PARAM_DRC_THRESHOLD;
    ///Noise Reduction Threshold
    MAPI_U16    BSND_PARAM_NR_THRESHOLD;
    /// ECHO Time(ms)
    MAPI_U16    BSND_PARAM_ECHO_TIME;
} BSND_PARAMETER;

/// Define Audio Get Baisc Effect Parameter.
typedef enum
{
    ///PreScale Vaule
    BSND_GET_PRESCALE,
    ///Treble Vaule
    BSND_GET_TREBLE,
    ///Bass Value
    BSND_GET_BASS,
    ///Balance Value
    BSND_GET_BALANCE,
    ///EQ Level
    BSND_GET_EQ_BAND0_LEVEL,
    ///EQ Level
    BSND_GET_EQ_BAND1_LEVEL,
    ///EQ Level
    BSND_GET_EQ_BAND2_LEVEL,
    ///EQ Level
    BSND_GET_EQ_BAND3_LEVEL,
    ///EQ Level
    BSND_GET_EQ_BAND4_LEVEL,
    ///EQ Level
    BSND_GET_EQ_BAND5_LEVEL,
    ///EQ Level
    BSND_GET_EQ_BAND6_LEVEL,
    ///EQ Level
    BSND_GET_EQ_BAND7_LEVEL,
    ///PEQ GAIN
    BSND_GET_PEQ_BAND0_GAIN,
    ///PEQ GAIN
    BSND_GET_PEQ_BAND1_GAIN,
    ///PEQ GAIN
    BSND_GET_PEQ_BAND2_GAIN,
    ///PEQ GAIN
    BSND_GET_PEQ_BAND3_GAIN,
    ///PEQ GAIN
    BSND_GET_PEQ_BAND4_GAIN,
    ///PEQ GAIN
    BSND_GET_PEQ_BAND5_GAIN,
    ///PEQ GAIN
    BSND_GET_PEQ_BAND6_GAIN,
    ///PEQ GAIN
    BSND_GET_PEQ_BAND7_GAIN,
    ///PEQ Center Freq for Each Band
    BSND_GET_PEQ_BAND0_FC,
    ///PEQ Center Freq for Each Band
    BSND_GET_PEQ_BAND1_FC,
    ///PEQ Center Freq for Each Band
    BSND_GET_PEQ_BAND2_FC,
    ///PEQ Center Freq for Each Band
    BSND_GET_PEQ_BAND3_FC,
    ///PEQ Center Freq for Each Band
    BSND_GET_PEQ_BAND4_FC,
    ///PEQ Center Freq for Each Band
    BSND_GET_PEQ_BAND5_FC,
    ///PEQ Center Freq for Each Band
    BSND_GET_PEQ_BAND6_FC,
    ///PEQ Center Freq for Each Band
    BSND_GET_PEQ_BAND7_FC,
    ///PEQ Q Value for Each Band
    BSND_GET_PEQ_BAND0_QVALUE,
    ///PEQ Q Value for Each Band
    BSND_GET_PEQ_BAND1_QVALUE,
    ///PEQ Q Value for Each Band
    BSND_GET_PEQ_BAND2_QVALUE,
    ///PEQ Q Value for Each Band
    BSND_GET_PEQ_BAND3_QVALUE,
    ///PEQ Q Value for Each Band
    BSND_GET_PEQ_BAND4_QVALUE,
    ///PEQ Q Value for Each Band
    BSND_GET_PEQ_BAND5_QVALUE,
    ///PEQ Q Value for Each Band
    BSND_GET_PEQ_BAND6_QVALUE,
    ///PEQ Q Value for Each Band
    BSND_GET_PEQ_BAND7_QVALUE,
    ///AVC(AVL) Enable Flag
    BSND_GET_AVC_ONOFF,
    ///AVC(AVL) Threshold
    BSND_GET_AVC_THRESHOLD,
    ///AVC(AVL) Attach Time
    BSND_GET_AVC_AT,
    ///AVC(AVL) Release Time
    BSND_GET_AVC_RT,
    ///MStar Surround XA Value
    BSND_GET_MSURR_XA,
    ///MStar Surround XB Value
    BSND_GET_MSURR_XB,
    ///MStar Surround XK Value
    BSND_GET_MSURR_XK,
    ///MStar Surround LPFGAIN Value
    BSND_GET_MSURR_LPFGAIN,
    ///Sound DRC Threshold
    BSND_GET_DRC_THRESHOLD,
    ///Noise Reduction Threshold
    BSND_GET_NR_THRESHOLD,
    /// ECHO Time(ms)
    BSND_GET_ECHO_TIME,
} BSND_GET_PARAMETER_TYPE;

/// Define AQ upgrade file name
#define AUDIO_UPGRADE_FILE "audio_param_update.txt"

/// Advance Sound Main Function Type
typedef enum
{
    /// Dolby Prologic2 + Virtual Dolby Surr
    ADVSND_DOLBY_PL2VDS,
    /// Dolby Prologic2 + Dolby Virtual Speaker
    ADVSND_DOLBY_PL2VDPK,
    /// BBE
    ADVSND_BBE,
    /// SRS TrueSurr XT
    ADVSND_SRS_TSXT,
    /// SRS TrueSurr HD
    ADVSND_SRS_TSHD,
    /// SRS Theater Sound
    ADVSND_SRS_THEATERSOUND,
    /// DTS Ultra-TV
    ADVSND_DTS_ULTRATV,
    /// Audyssey
    ADVSND_AUDYSSEY,
    /// MStar Super Voice
    ADVSND_SUPER_VOICE,
    /// DBX
    ADVSND_DBX,
    /// SRS_PURESOUND
    ADVSND_SRS_PURESOUND,
    /// SRS SS3D
    ADVSND_SRS_THEATERSOUND3D,
    /// Reserve
    ADVSND_RESERVE4,
    /// Reserve
    ADVSND_RESERVE5,
    /// Reserve
    ADVSND_RESERVE6,
    /// NONE
    ADVSND_NONE,
} ADVANCESND_TYPE;


/// Define Audio Advacne Sound SubProcess TYPE enable/disable.
typedef enum
{
    /// SRS_TrueSurround-XT Subfunction:TrueBass ON
    SRS_TSXT_TRUEBASS_ON,
    /// SRS_TrueSurround-XT Subfunction:TrueBass OFF
    SRS_TSXT_TRUEBASS_OFF,
    /// SRS_TrueSurround-XT Subfunction:Dynamic Clarity ON
    SRS_TSXT_DYNAMIC_CLARITY_ON,
    /// SRS_TrueSurround-XT Subfunction:Dynamic Clarity OFF
    SRS_TSXT_DYNAMIC_CLARITY_OFF,

    // SRS_TrueSurround-HD Subfunction:TrueBass ON
    SRS_TSHD_TRUEBASS_ON,
    // SRS_TrueSurround-HD Subfunction:TrueBass OFF
    SRS_TSHD_TRUEBASS_OFF,
    /// SRS_TrueSurround-HD Subfunction:Dynamic Clarity ON
    SRS_TSHD_DYNAMIC_CLARITY_ON,
    /// SRS_TrueSurround-HD Subfunction:Dynamic Clarity OFF
    SRS_TSHD_DYNAMIC_CLARITY_OFF,
    /// SRS_TrueSurround-HD Subfunction:Definition ON
    SRS_TSHD_DEFINITION_ON,
    /// SRS_TrueSurround-HD Subfunction:Definition OFF
    SRS_TSHD_DEFINITION_OFF,
    /// SRS_TrueSurround-HD Subfunction:3D ON
    SRS_TSHD_SRS3D_ON,
    /// SRS_TrueSurround-HD Subfunction:3D OFF
    SRS_TSHD_SRS3D_OFF,

    ///SRS_THEATERSOUND Subfunction:TrueSurround-HD ON
    SRS_THEATERSOUND_TSHD_ON,
    ///SRS_THEATERSOUND Subfunction:TrueSurround-HD OFF
    SRS_THEATERSOUND_TSHD_OFF,
    // SRS_THEATERSOUND Subfunction:TrueBass ON
    SRS_THEATERSOUND_TRUEBASS_ON,
    // SRS_THEATERSOUND Subfunction:TrueBass OFF
    SRS_THEATERSOUND_TRUEBASS_OFF,
    /// SRS_THEATERSOUND Subfunction:Dynamic Clarity ON
    SRS_THEATERSOUND_DYNAMIC_CLARITY_ON,
    /// SRS_THEATERSOUND Subfunction:Dynamic Clarity OFF
    SRS_THEATERSOUND_DYNAMIC_CLARITY_OFF,
    /// SRS_THEATERSOUND Subfunction:Definition ON
    SRS_THEATERSOUND_DEFINITION_ON,
    /// SRS_THEATERSOUND Subfunction:Definition OFF
    SRS_THEATERSOUND_DEFINITION_OFF,
    /// SRS_THEATERSOUND Subfunction:TrueVolume ON
    SRS_THEATERSOUND_TRUEVOLUME_ON,
    /// SRS_THEATERSOUND Subfunction:TrueVolume OFF
    SRS_THEATERSOUND_TRUEVOLUME_OFF,
    /// SRS_THEATERSOUND Subfunction:HardLimter ON
    SRS_THEATERSOUND_HARDLIMITER_ON,
    /// SRS_THEATERSOUND Subfunction:HardLimter OFF
    SRS_THEATERSOUND_HARDLIMITER_OFF,
    /// SRS_THEATERSOUND Subfunction:HiPassFilter ON
    SRS_THEATERSOUND_HPF_ON,
    /// SRS_THEATERSOUND Subfunction:HiPassFilter OFF
    SRS_THEATERSOUND_HPF_OFF,
    /// SRS_THEATERSOUND Subfunction:TrueEQ ON
    SRS_THEATERSOUND_TRUEQ_ON,
    /// SRS_THEATERSOUND Subfunction:TrueEQ OFF
    SRS_THEATERSOUND_TRUEQ_OFF,
    /// SRS_THEATERSOUND Subfunction:NoiseManager ON
    SRS_THEATERSOUND_TRUVOLUME_NOISE_MANAGER_ON,
    /// SRS_THEATERSOUND Subfunction:NoiseManager OFF
    SRS_THEATERSOUND_TRUVOLUME_NOISE_MANAGER_OFF,

    ///DTS_Surround Envelo ON
    DTS_ULTRATV_ENVELO_ON,
    ///DTS_Surround Envelo OFF
    DTS_ULTRATV_ENVELO_OFF,
    ///DTS Symmetry ON
    DTS_ULTRATV_SYM_ON,
    ///DTS Symmetry OFF
    DTS_ULTRATV_SYM_OFF,

    ///AUDYSSEY Subfunction:Dynamic Volume ON
    AUDYSSEY_DYNAMIC_VOL_ON,
    ///AUDYSSEY Subfunction:Dynamic Volume OFF
    AUDYSSEY_DYNAMIC_VOL_OFF,
    ///AUDYSSEY Subfunction:Dynamic EQ ON
    AUDYSSEY_DYNAMIC_EQ_ON,
    ///AUDYSSEY Subfunction:Dynamic EQ OFF
    AUDYSSEY_DYNAMIC_EQ_OFF,
    ///AUDYSSEY Subfunction:PEQ ON
    AUDYSSEY_PEQ_ON,
    ///AUDYSSEY Subfunction:PEQ OFF
    AUDYSSEY_PEQ_OFF,
    ///AUDYSSEY Subfunction:ABX ON
    AUDYSSEY_ABX_ON,
    ///AUDYSSEY Subfunction:ABX OFF
    AUDYSSEY_ABX_OFF,

    ///SRS_THEATERSOUND Subfunction:TSHD SURROUND ON
    SRS_THEATERSOUND_TSHD_SURR_ON,
    ///SRS_THEATERSOUND Subfunction:TSHD SURROUND OFF
    SRS_THEATERSOUND_TSHD_SURR_OFF,
    ///SRS_THEATERSOUND Subfunction:TRUBASS_LEVEL_INDPENDANT ON
    SRS_THEATERSOUND_TRUBASS_LEVEL_INDP_ON,
    ///SRS_THEATERSOUND Subfunction:TRUBASS_LEVEL_INDPENDANT OFF
    SRS_THEATERSOUND_TRUBASS_LEVEL_INDP_OFF,
    ///SRS_THEATERSOUND Subfunction:CS ON
    SRS_THEATERSOUND_CS_ON,
    ///SRS_THEATERSOUND Subfunction:CS OFF
    SRS_THEATERSOUND_CS_OFF,
    ///SRS_THEATERSOUND Subfunction:TRUBASS_LEVEL_INDPENDANT ON
    SRS_THEATERSOUND_TRUDIALOG_ON,
    ///SRS_THEATERSOUND Subfunction:TRUBASS_LEVEL_INDPENDANT OFF
    SRS_THEATERSOUND_TRUDIALOG_OFF,
    /// SRS_THEATERSOUND Subfunction:NORMALIZER ON
    SRS_THEATERSOUND_TRUVOLUME_NORMALIZER_ON,
    /// SRS_THEATERSOUND Subfunction:NORMALIZER OFF
    SRS_THEATERSOUND_TRUVOLUME_NORMALIZER_OFF,
    /// SRS_THEATERSOUND Subfunction:SMOOTH ON
    SRS_THEATERSOUND_TRUVOLUME_SMOOTH_ON,
    /// SRS_THEATERSOUND Subfunction:SMOOTH OFF
    SRS_THEATERSOUND_TRUVOLUME_SMOOTH_OFF,
    /// SRS_THEATERSOUND Subfunction:HiPassFilterEnd ON
    SRS_THEATERSOUND_HPF_END_ON,
    /// SRS_THEATERSOUND Subfunction:HiPassFilterEnd OFF
    SRS_THEATERSOUND_HPF_END_OFF,
    /// SRS_THEATERSOUND3D :TS3D_ON
    SRS_THEATERSOUND3D_ON,
    /// SRS_THEATERSOUND3D :TS3D_OFF
    SRS_THEATERSOUND3D_OFF,
    /// SRS_THEATERSOUND3D Subfunction:CC3D_ON
    SRS_THEATERSOUND3D_CC3D_ON,
    /// SRS_THEATERSOUND3D Subfunction:CC3D_OFF
    SRS_THEATERSOUND3D_CC3D_OFF,
    /// SRS_THEATERSOUND3D Subfunction:DEPTH_PROCESS_ON
    SRS_THEATERSOUND3D_CC3D_DEPTH_PROCESS_ON,
    /// SRS_THEATERSOUND3D Subfunction:DEPTH_PROCESS_OFF
    SRS_THEATERSOUND3D_CC3D_DEPTH_PROCESS_OFF,
    /// SRS_THEATERSOUND3D Subfunction:3D_SURR_BOOST_ON
    SRS_THEATERSOUND3D_CC3D_3D_SURR_BOOST_ON,
    /// SRS_THEATERSOUND3D Subfunction:3D_SURR_BOOST_OFF
    SRS_THEATERSOUND3D_CC3D_3D_SURR_BOOST_OFF,
    /// SRS_THEATERSOUND3D Subfunction:FADE_ON
    SRS_THEATERSOUND3D_CC3D_FADE_ON,
    /// SRS_THEATERSOUND3D Subfunction:FADE_OFF
    SRS_THEATERSOUND3D_CC3D_FADE_OFF,
    /// SRS_THEATERSOUND3D Subfunction:TSHD_MIX_ON
    SRS_THEATERSOUND3D_CC3D_TSHD_MIX_ON,
    /// SRS_THEATERSOUND3D Subfunction:TSHD_MIX_OFF
    SRS_THEATERSOUND3D_CC3D_TSHD_MIX_OFF,
    /// SRS_THEATERSOUND3D Subfunction:TBHDX_ON
    SRS_THEATERSOUND3D_CC3D_TBHDX_ON,
    /// SRS_THEATERSOUND3D Subfunction:TBHDX_OFF
    SRS_THEATERSOUND3D_CC3D_TBHDX_OFF,
    /// SRS_THEATERSOUND3D Subfunction:GEQ_ON
    SRS_THEATERSOUND3D_GEQ_ON,
    /// SRS_THEATERSOUND3D Subfunction:GEQ_OFF
    SRS_THEATERSOUND3D_GEQ_OFF,

    /// SRS_PURESND:ON
    SRS_PURESND_ON,
    /// SRS_PURESND:OFF
    SRS_PURESND_OFF,
    /// SRS_PURESND:HardLimiter_ON
    SRS_PURESOUND_HL_ON,
    /// SRS_PURESND:HardLimiter_OFF
    SRS_PURESOUND_HL_OFF,
    /// SRS_PURESND:ActiveEQ_ON
    SRS_PURESOUND_AEQ_ON,
    /// SRS_PURESND:ActiveEQ_OFF
    SRS_PURESOUND_AEQ_OFF,
    /// SRS_PURESND:HPF_ON
    SRS_PURESOUND_HPF_ON,
    /// SRS_PURESND:HPF_OFF
    SRS_PURESOUND_HPF_OFF,
    /// SRS_PURESND:TBHD_ON
    SRS_PURESOUND_TBHD_ON,
    /// SRS_PURESND:TBHD_OFF
    SRS_PURESOUND_TBHD_OFF,

    ///No SubProc, Don't care
    NO_SUBPROC,
} ADVSND_SUBPROC;

/// Define AEQ coefficient number
#define DTS_AEQ_TABLE_NUM 121

/// Advance Sound Effect Parameter TYPE
typedef enum
{
    ///DOLBY PL2 + VSPK SMOD Setting
    ADVSND_DOLBY_PL2VDPK_SMOD,
    ///DOLBY PL2 + VSPK WMOD Setting
    ADVSND_DOLBY_PL2VDPK_WMOD,
    ///SRS TSXT PARAM:INPUT_GAIN Setting
    ADVSND_SRS_TSXT_SET_INPUT_GAIN,
    ///SRS TSXT PARAM:DC_GAIN Setting
    ADVSND_SRS_TSXT_SET_DC_GAIN,
    ///SRS TSXT PARAM:TRUBASS_GAIN Setting
    ADVSND_SRS_TSXT_SET_TRUBASS_GAIN,
    ///SRS TSXT PARAM:SPEAKERSIZE Setting
    ADVSND_SRS_TSXT_SET_SPEAKERSIZE,
    ///SRS TSXT PARAM:INPUT_MODE Setting
    ADVSND_SRS_TSXT_SET_INPUT_MODE,
    ///SRS TSXT PARAM:OUTPUT_GAIN Setting
    ADVSND_SRS_TSXT_SET_OUTPUT_GAIN,

    ///SRS TSHD Sub-Process Setting Parameter
    ///SRS TSHD PARAM:INPUT_MODE Setting
    ADVSND_SRS_TSHD_SET_INPUT_MODE,
    ///SRS TSHD PARAM:OUTPUT_MODE Setting
    ADVSND_SRS_TSHD_SET_OUTPUT_MODE,
    ///SRS TSHD PARAM:SPEAKERSIZE Setting
    ADVSND_SRS_TSHD_SET_SPEAKERSIZE,
    ///SRS TSHD PARAM:TRUBASS_CONTROL Setting
    ADVSND_SRS_TSHD_SET_TRUBASS_CONTROL,
    ///SRS TSHD PARAM:DEFINITION_CONTROL Setting
    ADVSND_SRS_TSHD_SET_DEFINITION_CONTROL,
    ///SRS TSHD PARAM:DC_CONTROL Setting
    ADVSND_SRS_TSHD_SET_DC_CONTROL,
    ///SRS TSHD PARAM:SURROUND_LEVEL Setting
    ADVSND_SRS_TSHD_SET_SURROUND_LEVEL,
    ///SRS TSHD PARAM:INPUT_GAIN Setting
    ADVSND_SRS_TSHD_SET_INPUT_GAIN,
    ///SRS TSHD PARAM:WOWSPACE_CONTROL Setting
    ADVSND_SRS_TSHD_SET_WOWSPACE_CONTROL,
    ///SRS TSHD PARAM:WOWCENTER_CONTROL Setting
    ADVSND_SRS_TSHD_SET_WOWCENTER_CONTROL,
    ///SRS TSHD PARAM:WOWHDSRS3DMODE Setting
    ADVSND_SRS_TSHD_SET_WOWHDSRS3DMODE,
    ///SRS TSHD PARAM:LIMITERCONTROL Setting
    ADVSND_SRS_TSHD_SET_LIMITERCONTROL,
    ///SRS TSHD PARAM:OUTPUT_GAIN Setting
    ADVSND_SRS_TSHD_SET_OUTPUT_GAIN,

    ///SRS_THEATERSOUND Sub-Process Setting Parameter
    ///SRS THEATERSOUND PARAM:INPUT_GAIN Setting
    ADVSND_SRS_THEATERSOUND_INPUT_GAIN,
    ///SRS THEATERSOUND PARAM:DEFINITION_CONTROL Setting
    ADVSND_SRS_THEATERSOUND_DEFINITION_CONTROL,
    ///SRS THEATERSOUND PARAM:DC_CONTROL Setting
    ADVSND_SRS_THEATERSOUND_DC_CONTROL,
    ///SRS THEATERSOUND PARAM:TRUBASS_CONTROL Setting
    ADVSND_SRS_THEATERSOUND_TRUBASS_CONTROL,
    ///SRS THEATERSOUND PARAM:SPEAKERSIZE Setting
    ADVSND_SRS_THEATERSOUND_SPEAKERSIZE,
    ///SRS THEATERSOUND PARAM:HARDLIMITER_LEVEL Setting
    ADVSND_SRS_THEATERSOUND_HARDLIMITER_LEVEL,
    ///SRS THEATERSOUND PARAM:HARDLIMITER_BOOST_GAIN Setting
    ADVSND_SRS_THEATERSOUND_HARDLIMITER_BOOST_GAIN,
    ///SRS THEATERSOUND PARAM:HEADROOM_GAIN Setting
    ADVSND_SRS_THEATERSOUND_HEADROOM_GAIN,
    ///SRS THEATERSOUND PARAM:TRUVOLUME_MODE Setting
    ADVSND_SRS_THEATERSOUND_TRUVOLUME_MODE,
    ///SRS THEATERSOUND PARAM:TRUVOLUME_REF_LEVEL Setting
    ADVSND_SRS_THEATERSOUND_TRUVOLUME_REF_LEVEL,
    ///SRS THEATERSOUND PARAM:TRUVOLUME_MAX_GAIN Setting
    ADVSND_SRS_THEATERSOUND_TRUVOLUME_MAX_GAIN,
    ///SRS THEATERSOUND PARAM:TRUVOLUME_NOISE_MNGR_THLD Setting
    ADVSND_SRS_THEATERSOUND_TRUVOLUME_NOISE_MNGR_THLD,
    ///SRS THEATERSOUND PARAM:TRUVOLUME_CALIBRATE Setting
    ADVSND_SRS_THEATERSOUND_TRUVOLUME_CALIBRATE,
    ///SRS THEATERSOUND PARAM:TRUVOLUME_INPUT_GAIN Setting
    ADVSND_SRS_THEATERSOUND_TRUVOLUME_INPUT_GAIN,
    ///SRS THEATERSOUND PARAM:TRUVOLUME_OUTPUT_GAIN Setting
    ADVSND_SRS_THEATERSOUND_TRUVOLUME_OUTPUT_GAIN,
    ///SRS THEATERSOUND PARAM:HPF_FC Setting
    ADVSND_SRS_THEATERSOUND_HPF_FC,

    ///DTS_ULTRATV Sub-Process Setting Parameter
    ///DTS_ULTRATV PARAM:EVO_MONOINPUT Setting
    ADVSND_DTS_ULTRATV_EVO_MONOINPUT,
    ///DTS_ULTRATV PARAM:EVO_WIDENINGON Setting
    ADVSND_DTS_ULTRATV_EVO_WIDENINGON,
    ///DTS_ULTRATV PARAM:EVO_ADD3DBON Setting
    ADVSND_DTS_ULTRATV_EVO_ADD3DBON,
    ///DTS_ULTRATV PARAM:EVO_PCELEVEL Setting
    ADVSND_DTS_ULTRATV_EVO_PCELEVEL,
    ///DTS_ULTRATV PARAM:EVO_VLFELEVEL Setting
    ADVSND_DTS_ULTRATV_EVO_VLFELEVEL,
    ///DTS_ULTRATV PARAM:SYM_DEFAULT Setting
    ADVSND_DTS_ULTRATV_SYM_DEFAULT,
    ///DTS_ULTRATV PARAM:SYM_MODE Setting
    ADVSND_DTS_ULTRATV_SYM_MODE,
    ///DTS_ULTRATV PARAM:SYM_LEVEL Setting
    ADVSND_DTS_ULTRATV_SYM_LEVEL,
    ///DTS_ULTRATV PARAM:SYM_RESET Setting
    ADVSND_DTS_ULTRATV_SYM_RESET,

    ///AUDYSSEY Sub-Process Setting Parameter
    ///AUDYSSEY PARAM:DYNAMICVOL_COMPRESS_MODE Setting
    ADVSND_AUDYSSEY_DYNAMICVOL_COMPRESS_MODE,
    ///AUDYSSEY PARAM:DYNAMICVOL_GC Setting
    ADVSND_AUDYSSEY_DYNAMICVOL_GC,
    ///AUDYSSEY PARAM:DYNAMICVOL_VOLSETTING Setting
    ADVSND_AUDYSSEY_DYNAMICVOL_VOLSETTING,
    ///AUDYSSEY PARAM:DYNAMICEQ_EQOFFSET Setting
    ADVSND_AUDYSSEY_DYNAMICEQ_EQOFFSET,
    ///AUDYSSEY PARAM:ABX_GWET Setting
    ADVSND_AUDYSSEY_ABX_GWET,
    ///AUDYSSEY PARAM:ABX_GDRY Setting
    ADVSND_AUDYSSEY_ABX_GDRY,
    ///AUDYSSEY PARAM:ABX_FILSET Setting
    ADVSND_AUDYSSEY_ABX_FILSET,

    ///SRS_THEATERSOUND Sub-Process Setting Parameter
    ///SRS THEATERSOUND PARAM:TSHD_INPUT_GAIN Setting
    ADVSND_SRS_THEATERSOUND_TSHD_INPUT_GAIN,
    ///SRS THEATERSOUND PARAM:TSHD_OUTPUT_GAIN Setting
    ADVSND_SRS_THEATERSOUND_TSHD_OUTPUT_GAIN,
    ///SRS THEATERSOUND PARAM:SURR_LEVEL_CONTROL Setting
    ADVSND_SRS_THEATERSOUND_SURR_LEVEL_CONTROL,
    ///SRS THEATERSOUND PARAM:TRUBASS_COMPRESSOR_CONTROL Setting
    ADVSND_SRS_THEATERSOUND_TRUBASS_COMPRESSOR_CONTROL,
    ///SRS THEATERSOUND PARAM:TRUBASS_PROCESS_MODE Setting
    ADVSND_SRS_THEATERSOUND_TRUBASS_PROCESS_MODE,
    ///SRS THEATERSOUND PARAM:TRUBASS_SPEAKER_AUDIO Setting
    ADVSND_SRS_THEATERSOUND_TRUBASS_SPEAKER_AUDIO,
    ///SRS THEATERSOUND PARAM:SPEAKER_ANALYSIS Setting
    ADVSND_SRS_THEATERSOUND_SPEAKER_ANALYSIS,
    ///SRS THEATERSOUND PARAM:OUTPUT_GAIN Setting
    ADVSND_SRS_THEATERSOUND_OUTPUT_GAIN,
    ///SRS THEATERSOUND PARAM:BYPASS_GAIN Setting
    ADVSND_SRS_THEATERSOUND_BYPASS_GAIN,
    ///SRS THEATERSOUND PARAM:INPUT_MODE Setting
    ADVSND_SRS_THEATERSOUND_INPUT_MODE,
    ///SRS THEATERSOUND PARAM:TVOLHD_BYPASS_GAIN
    ADVSND_SRS_THEATERSOUND_TVOLHD_BYPASS_GAIN,
    ///SRS THEATERSOUND PARAM:CS_INPUT_GAIN
    ADVSND_SRS_THEATERSOUND_CS_INPUT_GAIN,
    ///SRS THEATERSOUND PARAM:CS_PROCESS_MODE
    ADVSND_SRS_THEATERSOUND_CS_PROCESS_MODE,
    ///SRS THEATERSOUND PARAM:CS_LR_OUTPUT_GAIN Setting
    ADVSND_SRS_THEATERSOUND_CS_LR_OUTPUT_GAIN,
    ///SRS THEATERSOUND PARAM:CS_LSRS_OUTPUT_GAIN Setting
    ADVSND_SRS_THEATERSOUND_CS_LSRS_OUTPUT_GAIN,
    ///SRS THEATERSOUND PARAM:CS_CENTER_OUTPUT_GAIN Setting
    ADVSND_SRS_THEATERSOUND_CS_CENTER_OUTPUT_GAIN,
    ///SRS THEATERSOUND PARAM:TRUDIALOG_INPUT_GAIN
    ADVSND_SRS_THEATERSOUND_TRUDIALOG_INPUT_GAIN,
    ///SRS THEATERSOUND PARAM:TRUDIALOG_OUTPUT_GAIN Setting
    ADVSND_SRS_THEATERSOUND_TRUDIALOG_OUTPUT_GAIN,
    ///SRS THEATERSOUND PARAM:TRUDIALOG_BYPASS_GAIN Setting
    ADVSND_SRS_THEATERSOUND_TRUDIALOG_BYPASS_GAIN,
    ///SRS THEATERSOUND PARAM:TRUDIALOG_PROCESS_GAIN Setting
    ADVSND_SRS_THEATERSOUND_TRUDIALOG_PROCESS_GAIN,
    ///SRS THEATERSOUND PARAM:TRUDIALOG_CLARITY_GAIN Setting
    ADVSND_SRS_THEATERSOUND_TRUDIALOG_CLARITY_GAIN,
    ///SRS THEATERSOUND PARAM:TRUVOLUME_NORMALIZE_THRESH Setting
    ADVSND_SRS_THEATERSOUND_TRUVOLUME_NORMALIZE_THRESH,
    ///SRS THEATERSOUND3D PARAM:CC3D_INPUT_GAIN Setting
    ADVSND_SRS_THEATERSOUND3D_CC3D_INPUT_GAIN,
    ///SRS THEATERSOUND3D PARAM:CC3D_OUTPUT_GAIN Setting
    ADVSND_SRS_THEATERSOUND3D_CC3D_OUTPUT_GAIN,
    ///SRS THEATERSOUND3D PARAM:CC3D_BYPASS_GAIN Setting
    ADVSND_SRS_THEATERSOUND3D_CC3D_BYPASS_GAIN,
    ///SRS THEATERSOUND3D PARAM:CC3D_APERTURE Setting
    ADVSND_SRS_THEATERSOUND3D_CC3D_APERTURE,
    ///SRS THEATERSOUND3D PARAM:CC3D_GAINLIMIT Setting
    ADVSND_SRS_THEATERSOUND3D_CC3D_GAINLIMIT,
    ///SRS THEATERSOUND3D PARAM:CC3D_FF_DEPTH Setting
    ADVSND_SRS_THEATERSOUND3D_CC3D_FF_DEPTH,
    ///SRS THEATERSOUND3D PARAM:CC3D_NF_DEPTH Setting
    ADVSND_SRS_THEATERSOUND3D_CC3D_NF_DEPTH,
    ///SRS THEATERSOUND3D PARAM:TSHD_MIX_FADE_CTRL Setting
    ADVSND_SRS_THEATERSOUND3D_CC3D_TSHD_MIX_FADE_CTRL,
    ///SRS THEATERSOUND3D PARAM:CC3D_TBHDX_INPUT_GAIN Setting
    ADVSND_SRS_THEATERSOUND3D_CC3D_TBHDX_INPUT_GAIN,
    ///SRS THEATERSOUND3D PARAM:CC3D_TBHDX_BASSLEVEL Setting
    ADVSND_SRS_THEATERSOUND3D_CC3D_TBHDX_BASSLEVEL,
    ///SRS THEATERSOUND3D PARAM:CC3D_TBHDX_SPEAKERSIZE Setting
    ADVSND_SRS_THEATERSOUND3D_CC3D_TBHDX_SPEAKERSIZE,
    ///SRS THEATERSOUND3D PARAM:CC3D_TBHDX_MODE Setting
    ADVSND_SRS_THEATERSOUND3D_CC3D_TBHDX_MODE,
    ///SRS THEATERSOUND3D PARAM:CC3D_TBHDX_DYNAMICS Setting
    ADVSND_SRS_THEATERSOUND3D_CC3D_TBHDX_DYNAMICS,
    ///SRS THEATERSOUND3D PARAM:CC3D_TBHDX_HP_ORDER Setting
    ADVSND_SRS_THEATERSOUND3D_CC3D_TBHDX_HP_ORDER,
    ///SRS THEATERSOUND3D PARAM:CC3D_TBHDX_CUSTOM_FILTER Setting
    ADVSND_SRS_THEATERSOUND3D_CC3D_TBHDX_CUSTOM_FILTER,
    ///SRS THEATERSOUND3D PARAM:GEQ_INPUT_GAIN Setting
    ADVSND_SRS_THEATERSOUND3D_GEQ_INPUT_GAIN,
    ///SRS THEATERSOUND3D PARAM:GEQ_BAND0_GAIN Setting
    ADVSND_SRS_THEATERSOUND3D_GEQ_BAND0_GAIN,
    ///SRS THEATERSOUND3D PARAM:GEQ_BAND1_GAIN Setting
    ADVSND_SRS_THEATERSOUND3D_GEQ_BAND1_GAIN,
    ///SRS THEATERSOUND3D PARAM:GEQ_BAND2_GAIN Setting
    ADVSND_SRS_THEATERSOUND3D_GEQ_BAND2_GAIN,
    ///SRS THEATERSOUND3D PARAM:GEQ_BAND3_GAIN Setting
    ADVSND_SRS_THEATERSOUND3D_GEQ_BAND3_GAIN,
    ///SRS THEATERSOUND3D PARAM:GEQ_BAND4_GAIN Setting
    ADVSND_SRS_THEATERSOUND3D_GEQ_BAND4_GAIN,
    ///SRS THEATERSOUND3D PARAM:CC3D_PROCESS_PATH Setting
    ADVSND_SRS_THEATERSOUND3D_CC3D_PROCESS_PATH,

    ///SRS PURESOUND PARAM:HL_INPUT_GAIN Setting
    ADVSND_SRS_PURESOUND_HL_INPUT_GAIN,
    ///SRS PURESOUND PARAM:HL_OUTPUT_GAIN Setting
    ADVSND_SRS_PURESOUND_HL_OUTPUT_GAIN,
    ///SRS PURESOUND PARAM:HL_BYPASS_GAIN Setting
    ADVSND_SRS_PURESOUND_HL_BYPASS_GAIN,
    ///SRS PURESOUND PARAM:HL_LIMITERBOOST Setting
    ADVSND_SRS_PURESOUND_HL_LIMITERBOOST,
     ///SRS PURESOUND PARAM:HL_HARDLIMIT Setting
    ADVSND_SRS_PURESOUND_HL_HARDLIMIT,
    ///SRS PURESOUND PARAM:HL_DELAYLEN Setting
    ADVSND_SRS_PURESOUND_HL_DELAYLEN,
    ///SRS PURESOUND PARAM:AEQ_INPUT_GAIN Setting
    ADVSND_SRS_PURESOUND_AEQ_INPUT_GAIN,
    ///SRS PURESOUND PARAM:AEQ_OUTPUT_GAIN Setting
    ADVSND_SRS_PURESOUND_AEQ_OUTPUT_GAIN,
    ///SRS PURESOUND PARAM:AEQ_BYPASS_GAIN Setting
    ADVSND_SRS_PURESOUND_AEQ_BYPASS_GAIN,
    ///SRS PURESOUND PARAM:HPF_FREQUENCY Setting
    ADVSND_SRS_PURESOUND_HPF_FREQUENCY,
    ///SRS PURESOUND PARAM:TBHD_TRUBASS_LEVEL Setting
    ADVSND_SRS_PURESOUND_TBHD_TRUBASS_LEVEL,
    ///SRS PURESOUND PARAM:TBHD_SPEAKER_SIZE Setting
    ADVSND_SRS_PURESOUND_TBHD_SPEAKER_SIZE,
    ///SRS PURESOUND PARAM:TBHD_LEVEL_INDEPENDENT_EN Setting
    ADVSND_SRS_PURESOUND_TBHD_LEVEL_INDEPENDENT_EN,
    ///SRS PURESOUND PARAM:TBHD_COMPRESSOR_LEVEL Setting
    ADVSND_SRS_PURESOUND_TBHD_COMPRESSOR_LEVEL,
    ///SRS PURESOUND PARAM:TBHD_MODE Setting
    ADVSND_SRS_PURESOUND_TBHD_MODE,
    ///SRS PURESOUND PARAM:TBHD_SPEAKER_AUDIO Setting
    ADVSND_SRS_PURESOUND_TBHD_SPEAKER_AUDIO,
    ///SRS PURESOUND PARAM:TBHD_SPEAKER_ANALYSIS Setting
    ADVSND_SRS_PURESOUND_TBHD_SPEAKER_ANALYSIS,
    ///SRS PURESOUND PARAM:INPUT_GAIN Setting
    ADVSND_SRS_PURESOUND_INPUT_GAIN,
    ///SRS PURESOUND PARAM:OUTPUT_GAIN Setting
    ADVSND_SRS_PURESOUND_OUTPUT_GAIN,

} ADVSND_PARAM_TYPE;


/// Define Audio Advance Baisc Effect Parameter.
typedef struct ST_ADVSND_PARAMETER
{
    ///DOLBY PL2 + VSPK SMOD Setting
    MAPI_U16    PARAM_DOLBY_PL2VDPK_SMOD;
    ///DOLBY PL2 + VSPK WMOD Setting
    MAPI_U16    PARAM_DOLBY_PL2VDPK_WMOD;
    ///SRS TSXT PARAM:INPUT_GAIN Setting
    MAPI_U16    PARAM_SRS_TSXT_SET_INPUT_GAIN;
    ///SRS TSXT PARAM:DC_GAIN Setting
    MAPI_U16    PARAM_SRS_TSXT_SET_DC_GAIN;
    ///SRS TSXT PARAM:TRUBASS_GAIN Setting
    MAPI_U16    PARAM_SRS_TSXT_SET_TRUBASS_GAIN;
    ///SRS TSXT PARAM:SPEAKERSIZE Setting
    MAPI_U16    PARAM_SRS_TSXT_SET_SPEAKERSIZE;
    ///SRS TSXT PARAM:INPUT_MODE Setting
    MAPI_U16    PARAM_SRS_TSXT_SET_INPUT_MODE;
    ///SRS TSXT PARAM:OUTPUT_GAIN Setting
    MAPI_U16    PARAM_SRS_TSXT_SET_OUTPUT_GAIN;

    ///SRS TSHD Sub-Process Setting Parameter
    ///SRS TSHD PARAM:INPUT_MODE Setting
    MAPI_U16    PARAM_SRS_TSHD_SET_INPUT_MODE;
    ///SRS TSHD PARAM:OUTPUT_MODE Setting
    MAPI_U16    PARAM_SRS_TSHD_SET_OUTPUT_MODE;
    ///SRS TSHD PARAM:SPEAKERSIZE Setting
    MAPI_U16    PARAM_SRS_TSHD_SET_SPEAKERSIZE;
    ///SRS TSHD PARAM:TRUBASS_CONTROL Setting
    MAPI_U16    PARAM_SRS_TSHD_SET_TRUBASS_CONTROL;
    ///SRS TSHD PARAM:DEFINITION_CONTROL Setting
    MAPI_U16    PARAM_SRS_TSHD_SET_DEFINITION_CONTROL;
    ///SRS TSHD PARAM:DC_CONTROL Setting
    MAPI_U16    PARAM_SRS_TSHD_SET_DC_CONTROL;
    ///SRS TSHD PARAM:SURROUND_LEVEL Setting
    MAPI_U16    PARAM_SRS_TSHD_SET_SURROUND_LEVEL;
    ///SRS TSHD PARAM:INPUT_GAIN Setting
    MAPI_U16    PARAM_SRS_TSHD_SET_INPUT_GAIN;
    ///SRS TSHD PARAM:WOWSPACE_CONTROL Setting
    MAPI_U16    PARAM_SRS_TSHD_SET_WOWSPACE_CONTROL;
    ///SRS TSHD PARAM:WOWCENTER_CONTROL Setting
    MAPI_U16    PARAM_SRS_TSHD_SET_WOWCENTER_CONTROL;
    ///SRS TSHD PARAM:WOWHDSRS3DMODE Setting
    MAPI_U16    PARAM_SRS_TSHD_SET_WOWHDSRS3DMODE;
    ///SRS TSHD PARAM:LIMITERCONTROL Setting
    MAPI_U16    PARAM_SRS_TSHD_SET_LIMITERCONTROL;
    ///SRS TSHD PARAM:OUTPUT_GAIN Setting
    MAPI_U16    PARAM_SRS_TSHD_SET_OUTPUT_GAIN;

    ///SRS_THEATERSOUND Sub-Process Setting Parameter
    ///SRS THEATERSOUND PARAM:INPUT_GAIN Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND_INPUT_GAIN;
    ///SRS THEATERSOUND PARAM:OUTPUT_GAIN Setting
    MAPI_U16 PARAM_SRS_THEATERSOUND_OUTPUT_GAIN;
    ///SRS THEATERSOUND PARAM:BYPASS_GAIN Setting
    MAPI_U16 PARAM_SRS_THEATERSOUND_BYPASS_GAIN;
    ///SRS THEATERSOUND PARAM:HEADROOM_GAIN Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND_HEADROOM_GAIN;
    ///SRS THEATERSOUND PARAM:INPUT_MODE Setting
    MAPI_U16 PARAM_SRS_THEATERSOUND_INPUT_MODE;

    ///SRS THEATERSOUND PARAM:DEFINITION_CONTROL Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND_DEFINITION_CONTROL;
    ///SRS THEATERSOUND PARAM:DC_CONTROL Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND_DC_CONTROL;
    ///SRS THEATERSOUND PARAM:TRUBASS_CONTROL Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND_TRUBASS_CONTROL;
    ///SRS THEATERSOUND PARAM:SPEAKERSIZE Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND_SPEAKERSIZE;
    ///SRS THEATERSOUND PARAM:HARDLIMITER_LEVEL Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND_HARDLIMITER_LEVEL;
    ///SRS THEATERSOUND PARAM:HARDLIMITER_BOOST_GAIN Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND_HARDLIMITER_BOOST_GAIN;
    ///SRS THEATERSOUND PARAM:TRUVOLUME_MODE Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND_TRUVOLUME_MODE;
    ///SRS THEATERSOUND PARAM:TRUVOLUME_REF_LEVEL Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND_TRUVOLUME_REF_LEVEL;
    ///SRS THEATERSOUND PARAM:TRUVOLUME_MAX_GAIN Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND_TRUVOLUME_MAX_GAIN;
    ///SRS THEATERSOUND PARAM:TRUVOLUME_NOISE_MNGR_THLD Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND_TRUVOLUME_NOISE_MNGR_THLD;
    ///SRS THEATERSOUND PARAM:TRUVOLUME_CALIBRATE Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND_TRUVOLUME_CALIBRATE;
    ///SRS THEATERSOUND PARAM:TRUVOLUME_INPUT_GAIN Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND_TRUVOLUME_INPUT_GAIN;
    ///SRS THEATERSOUND PARAM:TRUVOLUME_OUTPUT_GAIN Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND_TRUVOLUME_OUTPUT_GAIN;
    ///SRS THEATERSOUND PARAM:TRUVOLUME_BYPASS_GAIN Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND_TRUVOLUME_BYPASS_GAIN;
    ///SRS THEATERSOUND PARAM:TRUVOLUME_MAXGAIN_CONTROL Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND_TVOLHD_MAXGAIN_CONTROL;
    ///SRS THEATERSOUND PARAM:TRUVOLUME_NORMAL_THRESH Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND_TVOLHD_NORMAL_THRESH;
    ///SRS_THEATERSOUND Sub-Process Setting Parameter
    ///SRS THEATERSOUND PARAM:TSHD_INPUT_GAIN Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND_TSHD_INPUT_GAIN;
    ///SRS THEATERSOUND PARAM:TSHD_OUTPUT_GAIN Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND_TSHD_OUTPUT_GAIN;
    ///SRS THEATERSOUND PARAM:SURR_LEVEL_CONTROL Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND_SURR_LEVEL_CONTROL;
    ///SRS THEATERSOUND PARAM:TRUBASS_COMPRESSOR_CONTROL Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND_TRUBASS_COMPRESSOR_CONTROL;
    ///SRS THEATERSOUND PARAM:TRUBASS_PROCESS_MODE Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND_TRUBASS_PROCESS_MODE;
    ///SRS THEATERSOUND PARAM:TRUBASS_SPEAKER_AUDIO Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND_TRUBASS_SPEAKER_AUDIO;
    ///SRS THEATERSOUND PARAM:TRUBASS_SPEAKER_ANALYSIS Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND_TRUBASS_SPEAKER_ANALYSIS;
    ///SRS THEATERSOUND PARAM:CS_INPUT_GAIN Setting
    MAPI_U16 PARAM_SRS_THEATERSOUND_CS_INPUT_GAIN;
    ///SRS THEATERSOUND PARAM:CS_PROCESS_MODE Setting
    MAPI_U16 PARAM_SRS_THEATERSOUND_CS_PROCESS_MODE;
    ///SRS THEATERSOUND PARAM:CS_LR_OUTPUT_GAIN Setting
    MAPI_U16 PARAM_SRS_THEATERSOUND_CS_LR_OUTPUT_GAIN;
    ///SRS THEATERSOUND PARAM:CS_LSRS_OUTPUT_GAIN Setting
    MAPI_U16 PARAM_SRS_THEATERSOUND_CS_LSRS_OUTPUT_GAIN;
    ///SRS THEATERSOUND PARAM:CS_CENTER_OUTPUT_GAIN Setting
    MAPI_U16 PARAM_SRS_THEATERSOUND_CS_CENTER_OUTPUT_GAIN;
    ///SRS THEATERSOUND PARAM:TRUDIALOG_INPUT_GAIN Setting
    MAPI_U16 PARAM_SRS_THEATERSOUND_TRUDIALOG_INPUT_GAIN;
    ///SRS THEATERSOUND PARAM:TRUDIALOG_OUTPUT_GAIN Setting
    MAPI_U16 PARAM_SRS_THEATERSOUND_TRUDIALOG_OUTPUT_GAIN;
    ///SRS THEATERSOUND PARAM:TRUDIALOG_BYPASS_GAIN Setting
    MAPI_U16 PARAM_SRS_THEATERSOUND_TRUDIALOG_BYPASS_GAIN;
    ///SRS THEATERSOUND PARAM:TRUDIALOG_PROCESS_GAIN Setting
    MAPI_U16 PARAM_SRS_THEATERSOUND_TRUDIALOG_PROCESS_GAIN;
    ///SRS THEATERSOUND PARAM:TRUDIALOG_CLARITY_GAIN Setting
    MAPI_U16 PARAM_SRS_THEATERSOUND_TRUDIALOG_CLARITY_GAIN;
    ///SRS THEATERSOUND PARAM:NORMALIZER_THRESH Setting
    MAPI_U16 PARAM_SRS_THEATERSOUND_NORMALIZER_THRESH;
    ///SRS THEATERSOUND3D PARAM:CC3D_INPUT_GAIN Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND3D_CC3D_INPUT_GAIN;
    ///SRS THEATERSOUND3D PARAM:CC3D_OUTPUT_GAIN Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND3D_CC3D_OUTPUT_GAIN;
    ///SRS THEATERSOUND3D PARAM:CC3D_BYPASS_GAIN Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND3D_CC3D_BYPASS_GAIN;
    ///SRS THEATERSOUND3D PARAM:CC3D_APERTURE Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND3D_CC3D_APERTURE;
    ///SRS THEATERSOUND3D PARAM:CC3D_GAINLIMIT Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND3D_CC3D_GAINLIMIT;
    ///SRS THEATERSOUND3D PARAM:CC3D_FF_DEPTH Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND3D_CC3D_FF_DEPTH;
    ///SRS THEATERSOUND3D PARAM:CC3D_NF_DEPTH Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND3D_CC3D_NF_DEPTH;
    ///SRS THEATERSOUND3D PARAM:TSHD_MIX_FADE_CTRL Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND3D_CC3D_TSHD_MIX_FADE_CTRL;
    ///SRS THEATERSOUND3D PARAM:CC3D_TBHDX_INPUT_GAIN Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND3D_CC3D_TBHDX_INPUT_GAIN;
    ///SRS THEATERSOUND3D PARAM:CC3D_TBHDX_BASSLEVEL Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND3D_CC3D_TBHDX_BASSLEVEL;
    ///SRS THEATERSOUND3D PARAM:CC3D_TBHDX_SPEAKERSIZE Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND3D_CC3D_TBHDX_SPEAKERSIZE;
    ///SRS THEATERSOUND3D PARAM:CC3D_TBHDX_MODE Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND3D_CC3D_TBHDX_MODE;
    ///SRS THEATERSOUND3D PARAM:CC3D_TBHDX_DYNAMICS Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND3D_CC3D_TBHDX_DYNAMICS;
    ///SRS THEATERSOUND3D PARAM:CC3D_TBHDX_HP_ORDER Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND3D_CC3D_TBHDX_HP_ORDER;
    ///SRS THEATERSOUND3D PARAM:CC3D_TBHDX_CUSTOM_FILTER Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND3D_CC3D_TBHDX_CUSTOM_FILTER;
    ///SRS THEATERSOUND3D PARAM:GEQ_INPUT_GAIN Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND3D_GEQ_INPUT_GAIN;
    ///SRS THEATERSOUND3D PARAM:GEQ_BAND0_GAIN Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND3D_GEQ_BAND0_GAIN;
    ///SRS THEATERSOUND3D PARAM:GEQ_BAND1_GAIN Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND3D_GEQ_BAND1_GAIN;
    ///SRS THEATERSOUND3D PARAM:GEQ_BAND2_GAIN Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND3D_GEQ_BAND2_GAIN;
    ///SRS THEATERSOUND3D PARAM:GEQ_BAND3_GAIN Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND3D_GEQ_BAND3_GAIN;
    ///SRS THEATERSOUND3D PARAM:GEQ_BAND4_GAIN Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND3D_GEQ_BAND4_GAIN;
    ///SRS THEATERSOUND3D PARAM:CC3D_PROCESS_PATH Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND3D_CC3D_PROCESS_PATH;
    ///SRS THEATERSOUND PARAM:HPF_FC Setting
    MAPI_U16    PARAM_SRS_THEATERSOUND_HPF_FC;
    ///DTS_ULTRATV Sub-Process Setting Parameter
    ///DTS_ULTRATV PARAM:EVO_MONOINPUT Setting
    MAPI_U16    PARAM_DTS_ULTRATV_EVO_MONOINPUT;
    ///DTS_ULTRATV PARAM:EVO_WIDENINGON Setting
    MAPI_U16    PARAM_DTS_ULTRATV_EVO_WIDENINGON;
    ///DTS_ULTRATV PARAM:EVO_ADD3DBON Setting
    MAPI_U16    PARAM_DTS_ULTRATV_EVO_ADD3DBON;
    ///DTS_ULTRATV PARAM:EVO_PCELEVEL Setting
    MAPI_U16    PARAM_DTS_ULTRATV_EVO_PCELEVEL;
    ///DTS_ULTRATV PARAM:EVO_VLFELEVEL Setting
    MAPI_U16    PARAM_DTS_ULTRATV_EVO_VLFELEVEL;
    ///DTS_ULTRATV PARAM:SYM_DEFAULT Setting
    MAPI_U16    PARAM_DTS_ULTRATV_SYM_DEFAULT;
    ///DTS_ULTRATV PARAM:SYM_MODE Setting
    MAPI_U16    PARAM_DTS_ULTRATV_SYM_MODE;
    ///DTS_ULTRATV PARAM:SYM_LEVEL Setting
    MAPI_U16    PARAM_DTS_ULTRATV_SYM_LEVEL;
    ///DTS_ULTRATV PARAM:SYM_RESET Setting
    MAPI_U16    PARAM_DTS_ULTRATV_SYM_RESET;

    ///AUDYSSEY Sub-Process Setting Parameter
    ///AUDYSSEY PARAM:DYNAMICVOL_COMPRESS_MODE Setting
    MAPI_U16    PARAM_AUDYSSEY_DYNAMICVOL_COMPRESS_MODE;
    ///AUDYSSEY PARAM:DYNAMICVOL_GC Setting
    MAPI_U16    PARAM_AUDYSSEY_DYNAMICVOL_GC;
    ///AUDYSSEY PARAM:DYNAMICVOL_VOLSETTING Setting
    MAPI_U16    PARAM_AUDYSSEY_DYNAMICVOL_VOLSETTING;
    ///AUDYSSEY PARAM:DYNAMICEQ_EQOFFSET Setting
    MAPI_U16    PARAM_AUDYSSEY_DYNAMICEQ_EQOFFSET;
    ///AUDYSSEY PARAM:ABX_GWET Setting
    MAPI_U16    PARAM_AUDYSSEY_ABX_GWET;
    ///AUDYSSEY PARAM:ABX_GDRY Setting
    MAPI_U16    PARAM_AUDYSSEY_ABX_GDRY;
    ///AUDYSSEY PARAM:ABX_FILSET Setting
    MAPI_U16    PARAM_AUDYSSEY_ABX_FILSET;

    ///SRS PURESOUND PARAM:HL_INPUT_GAIN Setting
    MAPI_U16    PARAM_SRS_PURESOUND_HL_INPUT_GAIN;
    ///SRS PURESOUND PARAM:HL_OUTPUT_GAIN Setting
    MAPI_U16    PARAM_SRS_PURESOUND_HL_OUTPUT_GAIN;
    ///SRS PURESOUND PARAM:HL_BYPASS_GAIN Setting
    MAPI_U16    PARAM_SRS_PURESOUND_HL_BYPASS_GAIN;
    ///SRS PURESOUND PARAM:HL_LIMITERBOOST Setting
    MAPI_U16    PARAM_SRS_PURESOUND_HL_LIMITERBOOST;
    ///SRS PURESOUND PARAM:HL_HARDLIMIT Setting
    MAPI_U16    PARAM_SRS_PURESOUND_HL_HARDLIMIT;
    ///SRS PURESOUND PARAM:HL_DELAYLEN Setting
    MAPI_U16    PARAM_SRS_PURESOUND_HL_DELAYLEN;
    ///SRS PURESOUND PARAM:AEQ_INPUT_GAIN Setting
    MAPI_U16    PARAM_SRS_PURESOUND_AEQ_INPUT_GAIN;
    ///SRS PURESOUND PARAM:AEQ_OUTPUT_GAIN Setting
    MAPI_U16    PARAM_SRS_PURESOUND_AEQ_OUTPUT_GAIN;
    ///SRS PURESOUND PARAM:AEQ_BYPASS_GAIN Setting
    MAPI_U16    PARAM_SRS_PURESOUND_AEQ_BYPASS_GAIN;
    ///SRS PURESOUND PARAM:HPF_FREQUENCY Setting
    MAPI_U16    PARAM_SRS_PURESOUND_HPF_FREQUENCY;
    ///SRS PURESOUND PARAM:TBHD_TRUBASS_LEVEL Setting
    MAPI_U16    PARAM_SRS_PURESOUND_TBHD_TRUBASS_LEVEL;
    ///SRS PURESOUND PARAM:TBHD_SPEAKER_SIZE Setting
    MAPI_U16    PARAM_SRS_PURESOUND_TBHD_SPEAKER_SIZE;
    ///SRS PURESOUND PARAM:TBHD_LEVEL_INDEPENDENT_EN Setting
    MAPI_U16    PARAM_SRS_PURESOUND_TBHD_LEVEL_INDEPENDENT_EN;
    ///SRS PURESOUND PARAM:TBHD_COMPRESSOR_LEVEL Setting
    MAPI_U16    PARAM_SRS_PURESOUND_TBHD_COMPRESSOR_LEVEL;
    ///SRS PURESOUND PARAM:TBHD_MODE Setting
    MAPI_U16    PARAM_SRS_PURESOUND_TBHD_MODE;
    ///SRS PURESOUND PARAM:TBHD_SPEAKER_AUDIO Setting
    MAPI_U16    PARAM_SRS_PURESOUND_TBHD_SPEAKER_AUDIO;
    ///SRS PURESOUND PARAM:TBHD_SPEAKER_ANALYSIS Setting
    MAPI_U16    PARAM_SRS_PURESOUND_TBHD_SPEAKER_ANALYSIS;
    ///SRS PURESOUND PARAM:INPUT_GAIN Setting
    MAPI_U16    PARAM_SRS_PURESOUND_INPUT_GAIN;
    ///SRS PURESOUND PARAM:OUTPUT_GAIN Setting
    MAPI_U16    PARAM_SRS_PURESOUND_OUTPUT_GAIN;
} ST_ADVSND_PARAMETER;


/// Define Audio Output TYPE.
typedef enum
{
    /// Delay in Speaker
    DELAY_SPEAKER,
    /// Delay in SPDIF
    DELAY_SPDIF,
    /// DigitalOut type SPDIF type change in UI(OSD)
    DIGITALOUT_SPDIF_UI_CONFIG,
    /// DigitalOut type is SPDIF Common Active
    DIGITALOUT_SPDIF_COMMON_CONFIG,
} AUDIO_OUT_TYPE;


/// Define Audio Output TYPE Parameter
typedef struct AUDIO_OUT_PARAMETER
{
    /// Speaker Delay Time(ms)
    MAPI_U32    SPEAKER_DELAY_TIME;
    /// SPDIF Delay Time(ms)
    MAPI_U32    SPDIF_DELAY_TIME;
    /// DigitalOut type SPDIF Out mode change in UI(OSD)
    SPDIF_TYPE_ SPDIF_OUTMOD_IN_UI;
    /// DigitalOut type SPDIF Out mode change in Active
    SPDIF_TYPE_ SPDIF_OUTMOD_ACTIVE;

} AUDIO_OUT_PARAMETER;

///////////////////////////////////////////////////////////////////////////////////////////////////
/// ATV related enum & structure is used to Java kind of sound property.
///////////////////////////////////////////////////////////////////////////////////////////////////

/// Define ATV Information Type
typedef enum
{
    ///ATV_HIDEV_MODE
    ATV_HIDEV_INFO,
}  ATV_INFO_TYPE;

/// Define ATV Parameter
typedef enum ATV_INFO_MODE
{
    ///Hidev filter off
    ATV_HIDEV_OFF,
    ///Hidev filter bw level 1
    ATV_HIDEV_BW_LV1,
    ///Hidev filter bw level 2
    ATV_HIDEV_BW_LV2,
    ///Hidev filter bw level 3
    ATV_HIDEV_BW_LV3,
    ///Hidev filter bw level numbers
    ATV_HIDEV_BW_MAX,
} ATV_INFO_MODE;

/// Define ATV Information Type
typedef enum
{
    ///ATV_SYSTEM_STANDARDS_BG
    ATV_SYSTEM_STANDARDS_BG = 0x0,
    ///ATV_SYSTEM_STANDARDS_BG
    ATV_SYSTEM_STANDARDS_DK = 0x4,
    ///ATV_SYSTEM_STANDARDS_BG
    ATV_SYSTEM_STANDARDS_I = 0x3,
    ///ATV_SYSTEM_STANDARDS_BG
    ATV_SYSTEM_STANDARDS_L = 0x9,
    ///ATV_SYSTEM_STANDARDS_BG
    ATV_SYSTEM_STANDARDS_M = 0xA,
}  ATV_SYSTEM_STANDARDS;

/// Define ATV Mode Type
typedef enum
{
    ///< Audio Mode Invalid
    ATV_AUDIOMODE_INVALID      = 0x00,
    ///< Audio Mode MONO
    ATV_AUDIOMODE_MONO         = 0x01,
    ///< Audio Mode Forced MONO
    ATV_AUDIOMODE_FORCED_MONO  = 0x02,
    ///< Audio Mode G Stereo
    ATV_AUDIOMODE_G_STEREO     = 0x03,
    ///< Audio Mode K Stereo
    ATV_AUDIOMODE_K_STEREO     = 0x04,
    ///< Audio Mode Mono SAP
    ATV_AUDIOMODE_MONO_SAP     = 0x05,
    ///< Audio Mode Stereo SAP
    ATV_AUDIOMODE_STEREO_SAP   = 0x06,
    ///< Audio Mode Dual A
    ATV_AUDIOMODE_DUAL_A       = 0x07,
    ///< Audio Mode Dual B
    ATV_AUDIOMODE_DUAL_B       = 0x08,
    ///< Audio Mode Dual AB
    ATV_AUDIOMODE_DUAL_AB      = 0x09,
    ///< Audio Mode NICAM MONO
    ATV_AUDIOMODE_NICAM_MONO   = 0x0A,
    ///< Audio Mode NICAM Stereo
    ATV_AUDIOMODE_NICAM_STEREO = 0x0B,
    ///< Audio Mode NICAM DUAL A
    ATV_AUDIOMODE_NICAM_DUAL_A = 0x0C,
    ///< Audio Mode NICAM DUAL B
    ATV_AUDIOMODE_NICAM_DUAL_B = 0x0D,
    ///< Audio Mode NICAM DUAL AB
    ATV_AUDIOMODE_NICAM_DUAL_AB = 0x0E,
    ///< Audio Mode HIDEV MONO
    ATV_AUDIOMODE_HIDEV_MONO   = 0x0F,
    ///< Audio Mode left left
    ATV_AUDIOMODE_LEFT_LEFT    = 0x10,
    ///< Audio Mode right right
    ATV_AUDIOMODE_RIGHT_RIGHT  = 0x11,
    ///< Audio Mode left right
    ATV_AUDIOMODE_LEFT_RIGHT   = 0x12,
} ATV_AUDIOMODE_TYPE;

///////////////////////////////////////////////////////////////////////////////////////////////////
/// DTV related enum & structure is used to Java kind of sound property.
///////////////////////////////////////////////////////////////////////////////////////////////////
/// Audio Output Mode Type Select
typedef enum
{
    /// Audio output mode is stereo mode
    DTV_DMP_DEC_SOUNDMODE_STEREO,
    /// Audio output mode is left mode
    DTV_DMP_DEC_SOUNDMODE_LEFT,
    /// Audio output mode is right mode
    DTV_DMP_DEC_SOUNDMODE_RIGHT,
    /// Audio output mode is mixed mode
    DTV_DMP_DEC_SOUNDMODE_MIXED,
} DTV_DMP_AUDIO_DEC_SOUNDMOD_TYPE;

/// MMA Decoder Command Type
typedef enum
{
    /// MM audio decoder command is stop
    DTV_DMP_DEC_STOP                       = 0x0,
    /// MM audio decoder command is play
    DTV_DMP_DEC_PLAY                       = 0x1,
    /// MM audio decoder command is play in file hand-shake
    DTV_DMP_DEC_PLAY_TS_FILE               = 0x2,
    /// MM audio decoder command is re-sync
    DTV_DMP_DEC_RESYNC                     = 0x3,
    DTV_DMP_DEC_PLAY_FILE                  = 0x4,
    DTV_DMP_DEC_BROWSE                     = 0x5,
    DTV_DMP_DEC_PAUSE                      = 0x6,
} DTV_DMP_AUDIO_DEC_CTRLCMD_TYPE;

/// Define DTV INFO TYPE
typedef enum
{
    ///DTV and DMP Decoder control
    DTV_DMP_DECODER_CTRL,
    ///DTV dualtype information
    DTV_DMP_DECODER_SOUNDMODE_CTRL,
    ///DTV mpeg info
    DTV_DMP_INFOTYPE_MPEG,
} DTV_DMP_INFO_TYPE;

/// Define DTV_DMP Audio Decoder ID Number
typedef enum
{
    /// Audio Decoder ID invalid
    DTVDMP_DEC_INVALID                      = -1,
    /// Audio 1-Decoder ID
    DTVDMP_DEC_ID1                          = 0,
    /// Audio 2-Decoder ID
    DTVDMP_DEC_ID2                          = 1,
    /// Audio 3-Decoder ID
    DTVDMP_DEC_ID3                          = 2,
    /// Audio Max-Decoder
    DTVDMP_DEC_MAX                          = 3
} DTV_DMP_AUDIO_DEC_ID;

/// Define DTV Parameter
typedef struct DTV_DMP_INFO_CFG1
{
    ///DTV and DMP Decoder command
    DTV_DMP_AUDIO_DEC_CTRLCMD_TYPE  DTV_DMP_DECODER_COMMAND;
    ///DTV Dual type
    DTV_DMP_AUDIO_DEC_SOUNDMOD_TYPE DTV_DECOCDER_SOUNDMODE;
    ///DTV SND MPEG infotype
    Audio_MPEG_infoType_  DTV_MPEG_INFO;
} DTV_DMP_INFO_CFG1;

/// Define DTV Parameter
typedef struct DTV_DMP_INFO_CFG2
{
    ///Parameter
    MAPI_BOOL      DTVDMP_CFG2_NOT_NEED;
    ///Parameter
    const MAPI_U32 DTVDMP_PARAM1;
    ///Parameter
    const MAPI_U32 DTVDMP_PARAM2;
} DTV_DMP_INFO_CFG2;

/// Define DTV Audio Channel Mode
typedef enum
{
    /// Acmod from decoder is not ready
    DTV_AUDIO_DEC_ACMODE_NOTREADY           = 0x0,
    /// Acmod from decoder is dual mono
    DTV_AUDIO_DEC_ACMODE_DUALMONO1          = 0x1,
    /// Acmod from decoder is mono
    DTV_AUDIO_DEC_ACMODE_MONO               = 0x2,
    /// Acmod from decoder is stereo
    DTV_AUDIO_DEC_ACMODE_STEREO             = 0x3,
    /// Acmod from decoder is multi-channel
    DTV_AUDIO_DEC_ACMODE_MULTICH            = 0x4,
    /// Acmod from decoder is dual mono2
    DTV_AUDIO_DEC_ACMODE_DUALMONO2          = 0x5,
} DTV_AUDIO_DEC_CHMOD_Type;

/// Define DTV Audio Sound Mode
typedef enum
{
    /// LL
    KTV_AUD_MPEG_SOUNDMODE_LL,
    /// RR
    KTV_AUD_MPEG_SOUNDMODE_RR,
    /// LR
    KTV_AUD_MPEG_SOUNDMODE_LR,
    /// MIX LR
    KTV_AUD_MPEG_SOUNDMODE_MIX_LR
} KTV_AUDIO_MPEG_SOUNDMODE;

/// MSRV_SSSOUND_AUTOTEST_DEBUG_LEVEL
typedef enum
{
    /// level 0
    MSRV_SSSOUND_LEVEL0_EMERG = 0,    ///< 0: level 0
    /// level 1
    MSRV_SSSOUND_LEVEL1_ALERT,        ///< 1: level 1
    /// level 2
    MSRV_SSSOUND_LEVEL2_CRIT,         ///< 2: level 2
    /// level 3
    MSRV_SSSOUND_LEVEL3_ERR,          ///< 3: level 3
    /// level 4
    MSRV_SSSOUND_LEVEL4_WARNING,      ///< 4: level 4
    /// level 5
    MSRV_SSSOUND_LEVEL5_NOTICE,       ///< 5: level 5
    /// level 6
    MSRV_SSSOUND_LEVEL6_INFO,         ///< 6: level 6
    /// level 7
    MSRV_SSSOUND_LEVEL7_DEBUG,        ///< 7: level 7
    /// level max
    MSRV_SSSOUND_LEVEL_MAX            ///< max: dont print debug msg
} MSRV_SSSOUND_DEBUG_LEVEL;

///S/PDIF out delay
typedef struct ST_SPDIF_Delay
{
    MAPI_U8 DTV_SPDIF_DELAY;
    MAPI_U8 ATV_SPDIF_DELAY;
    MAPI_U8 AV_SPDIF_DELAY;
    MAPI_U8 HDMI_SPDIF_DELAY;
    MAPI_U8 DVI_SPDIF_DELAY;
    MAPI_U8 MM_SPDIF_DELAY;

} ST_SPDIF_Delay;

///SPEAKER out delay
typedef struct ST_SPEAKER_Delay
{
    MAPI_U8 DTV_SPEAKER_DELAY;
    MAPI_U8 ATV_SPEAKER_DELAY;
    MAPI_U8 AV_SPEAKER_DELAY;
    MAPI_U8 HDMI_SPEAKER_DELAY;
    MAPI_U8 DVI_SPEAKER_DELAY;
    MAPI_U8 MM_SPEAKER_DELAY;
} ST_SPEAKER_Delay;

///AVC threshold struct
typedef struct ST_AVC_Threshold
{
    MAPI_U8 u8Avc_Threshold;
    MAPI_U8 u8Avc_AT;
    MAPI_U8 u8Avc_RT;
} ST_AVC_Threshold;

///DRC threshold struct
typedef struct ST_DRC_Threshold
{
    MAPI_U8 u8Avc_Threshold;
    MAPI_U8 u8Avc_AT;
    MAPI_U8 u8Avc_RT;
} ST_DRC_Threshold;

/// Define MSrv_SSSound class.
class MSrv_SSSound : public MSrv
{
    DECLARE_EVENT_MAP();

public:

    // ------------------------------------------------------------
    // public constructors
    // ------------------------------------------------------------
    /// No-liner adjust of picture items
    typedef struct
    {
        /// ponint 0
        U32 u32OSD_V0;
        /// ponint 25
        U32 u32OSD_V25;
        /// point 50
        U32 u32OSD_V50;
        /// point 75
        U32 u32OSD_V75;
        /// point 100
        U32 u32OSD_V100;
    } MS_SND_NLA_POINT;

    /// volume limit
    enum
    {
        ///minimum
        VOLUME_SCALE_MIN = 0,
        ///maximum
        VOLUME_SCALE_MAX = 100
    };
//=============================================================
//  AUDIO SSound RELATIONAL FUNCTION
//=============================================================
    //-------------------------------------------------------------------------------------------------
    /// Class Constructor
    //-------------------------------------------------------------------------------------------------
    MSrv_SSSound();

    // ------------------------------------------------------------
    // public destructor
    // ------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------
    /// Class Destructor
    //-------------------------------------------------------------------------------------------------
    ~MSrv_SSSound();

    // ------------------------------------------------------------
    // public operations
    // ------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------
    /// Audio Initialize Function in MSrv_SSound, including Audio
    /// @return None
    /// This should be the very first that AUDIO need process.
    /// It should already initialize it that can only call other audio function.
    //-------------------------------------------------------------------------------------------------
    void Initialize();

#if (STR_ENABLE == 1)
    // ------------------------------------------------------------
    /// Audio Suspend Function in MSrv_SSound
    /// @return MAPI_BOOL       \b IN: 1 is on, 0 is Off
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL Suspend();

    // ------------------------------------------------------------
    /// Audio Resume Function in MSrv_SSound
    /// @return MAPI_BOOL       \b IN: 1 is on, 0 is Off
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL Resume();
#endif

    //-------------------------------------------------------------------------------------------------
    /// Set Audio Path PreScale.
    /// @param  u8PreScale  \b IN :PreScale value
    /// @return SSSOUND_OK: Success
    /// This can setting the prescale before the sound effect for each input source. It may different for different input source.
    /// Now, it is one of switch case in MSrv_SSSound::SetBasicSoundEffect for case BSND_PRESCALE.
    /// for example, you can call by
    /// @code
    /// pSSSound->SetBasicSoundEffect((BSOUND_EFFECT_TYPE)BSND_PRESCALE, &stSndParameter);
    /// @endcode
    /// The stSndParameter has stored the pre-scale that want to applied.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetPreScale( MAPI_U8 u8PreScale );

    //-------------------------------------------------------------------------------------------------
    /// Set Audio Speaker volume.
    /// @param u8Vol        \b IN: Volume value range from 0~100
    /// @return SSSOUND_OK: Success
    /// This can setting the main speaker and Headphone volume use the u8Vol
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetVolume(MAPI_U8 u8Vol);

    /*add by owen.qin begin*/
    //-------------------------------------------------------------------------------------------------
    /// Set Audio AD status.
    /// @param enable        \b IN: Volume value true or false
    /// @return SSSOUND_OK: Success
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetADEnable(MAPI_BOOL enable);
    /*add by owen.qin end*/

    /*add by owen.qin begin*/
    //-------------------------------------------------------------------------------------------------
    /// Set Hearing of Impaired (HOH) status
    ///	@param enable
    /// @return SSSOUND_OK Success
    MAPI_U8 SetAutoHOHEnable(MAPI_BOOL enable);
	/*add by owen.qin end*/

    //-------------------------------------------------------------------------------------------------
    /// Setting the particular output path volume by the u8Vol
    /// Set Audio volume by different output.
    /// @param u8Vol                \b IN: Volume value range from 0~100
    /// @param enAudioPathType      \b IN: Different output path type
    /// @return SSSOUND_OK: Success
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetVolume(MAPI_U8 u8Vol, MAPI_AUDIO_PATH_TYPE enAudioPathType);

    //-------------------------------------------------------------------------------------------------
    ///Get the volume setting by the UI
    /// Get Audio Volume.
    /// @return MAPI_U8     \b OUT: Volume value range from 0~100
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetVolume();

    // ------------------------------------------------------------
    // public operations
    // ------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------
    /// Function same with SetVolume but only on headphone path
    /// Set Audio HeadPhone Volume.
    /// @param u8Vol        \b IN: Volume value range from 0~100
    /// @return SSSOUND_OK: Success
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetHPVolume(MAPI_U8 u8Vol);

    //-------------------------------------------------------------------------------------------------
    /// Get the headphone volume setting
    /// Get Audio HeadPhone Volume
    /// @return MAPI_U8     \b OUT: Volume value range from 0~100
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetHPVolume();

    //-------------------------------------------------------------------------------------------------
    /// Control the Audio Description volume
    /// Set AD (Audio Description) volume
    /// @param u8Vol        \b IN: Volume value range from 0~100
    /// @return SSSOUND_OK: Success
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetADVolume(MAPI_U8 u8Vol);

    //-------------------------------------------------------------------------------------------------
    /// Get the Audio Description volume setting
    /// Get AD (Audio Description) volume value
    /// @return MAPI_U8     \b OUT: AD volume value in system database
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetADVolume();

    //-------------------------------------------------------------------------------------------------
    /// Enable or disable the extern audio amplifier
    /// Set Audio Amplifier ON or OFF.
    /// @param EnAmp        \b IN: Enable: 1, Disable: 0
    /// @return SSSOUND_OK: Success
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 EnableAmplifier(MAPI_BOOL EnAmp);

    //-------------------------------------------------------------------------------------------------
    /// Get the which kind of mute type as following:
    /// Mute_Status_bIsAudioModeChanged,
    /// Mute_Status_bPermanentAudioMute,
    /// Mute_Status_bMomentAudioMute,
    /// Mute_Status_bByUserAudioMute,
    /// Mute_Status_bBySyncAudioMute,
    /// Mute_Status_bByVChipAudioMute,
    /// Mute_Status_bByBlockAudioMute,
    /// Mute_Status_bInternal1AudioMute,
    /// Mute_Status_bInternal2AudioMute,
    /// Mute_Status_bInternal3AudioMute,
    /// Mute_Status_bInternal4AudioMute,
    /// Mute_Status_bByDuringLimitedTimeAudioMute,
    /// Mute_Status_bByScanInOutchgCHchg,
    /// Mute_Status_bMHEGApMute,
    /// Mute_Status_bCIAudioMute,
    /// Mute_Status_SourceSwitchAudioMute,
    /// Mute_Status_bUsrSpkrAudioMute,
    /// Mute_Status_bUsrHpAudioMute,
    /// Mute_Status_bUsrSpdifAudioMute,
    /// Mute_Status_bUsrScart1AudioMute,
    /// Mute_Status_bUsrScart2AudioMute,
    /// Mute_Status_bUsrDataInAudioMute,
    /// Mute_Status_bPowerOnMute,
    /// Get the audio mute status by typing status type
    /// @param mute_status_type \b IN: mute status type
    /// @return MAPI_BOOL       \b IN: 1 is on, 0 is Off
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL GetMuteStatus(const MAPI_SOUND_MUTE_STATUS_TYPE mute_status_type);

    //-------------------------------------------------------------------------------------------------
    /// Set mute ON/OFF in the following kind of type:
    /// Mute_Status_bIsAudioModeChanged,
    /// Mute_Status_bPermanentAudioMute,
    /// Mute_Status_bMomentAudioMute,
    /// Mute_Status_bByUserAudioMute,
    /// Mute_Status_bBySyncAudioMute,
    /// Mute_Status_bByVChipAudioMute,
    /// Mute_Status_bByBlockAudioMute,
    /// Mute_Status_bInternal1AudioMute,
    /// Mute_Status_bInternal2AudioMute,
    /// Mute_Status_bInternal3AudioMute,
    /// Mute_Status_bInternal4AudioMute,
    /// Mute_Status_bByDuringLimitedTimeAudioMute,
    /// Mute_Status_bByScanInOutchgCHchg,
    /// Mute_Status_bMHEGApMute,
    /// Mute_Status_bCIAudioMute,
    /// Mute_Status_SourceSwitchAudioMute,
    /// Mute_Status_bUsrSpkrAudioMute,
    /// Mute_Status_bUsrHpAudioMute,
    /// Mute_Status_bUsrSpdifAudioMute,
    /// Mute_Status_bUsrScart1AudioMute,
    /// Mute_Status_bUsrScart2AudioMute,
    /// Mute_Status_bUsrDataInAudioMute,
    /// Mute_Status_bPowerOnMute,
    /// Set audio mute status
    /// @param muteType         \b IN: mute type
    /// @param onOff            \b IN: On: 1, Off: 0
    /// @param eType    \b IN: For Main /SUB / SCART
    /// @return SSSOUND_OK: Success
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetMuteStatus(SSSOUND_MUTE_TYPE muteType, MAPI_BOOL onOff, MSRV_AUDIO_PROCESSOR_TYPE eType=MSRV_AUDIO_PROCESSOR_MAIN);

    //-------------------------------------------------------------------------------------------------
    /// Set Audio ATV MTS mode
    /// The u8SifSoundMode setting has the following kind:
    /// E_AUDIOMODE_INVALID_
    /// E_AUDIOMODE_MONO_
    /// E_AUDIOMODE_FORCED_MONO_
    /// E_AUDIOMODE_G_STEREO_
    /// E_AUDIOMODE_K_STEREO_
    /// E_AUDIOMODE_MONO_SAP_
    /// E_AUDIOMODE_STEREO_SAP_
    /// E_AUDIOMODE_DUAL_A_
    /// E_AUDIOMODE_DUAL_B_
    /// E_AUDIOMODE_DUAL_AB_
    /// E_AUDIOMODE_NICAM_MONO_
    /// E_AUDIOMODE_NICAM_STEREO_
    /// E_AUDIOMODE_NICAM_DUAL_A_
    /// E_AUDIOMODE_NICAM_DUAL_B_
    /// E_AUDIOMODE_NICAM_DUAL_AB_
    /// E_AUDIOMODE_HIDEV_MONO_
    /// E_AUDIOMODE_LEFT_LEFT_
    /// E_AUDIOMODE_RIGHT_RIGHT_
    /// E_AUDIOMODE_LEFT_RIGHT_
    /// @param u8SifSoundMode    \b IN: MTS mode (Mono/Stereo/Dual)
    /// @return SSSOUND_OK: Success
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetMtsMode(MAPI_U8 u8SifSoundMode) ;

    //-------------------------------------------------------------------------------------------------
    /// Get Audio ATV MTS mode by the UI setting
    /// The return value has the following mode:
    /// E_AUDIOMODE_INVALID_
    /// E_AUDIOMODE_MONO_
    /// E_AUDIOMODE_FORCED_MONO_
    /// E_AUDIOMODE_G_STEREO_
    /// E_AUDIOMODE_K_STEREO_
    /// E_AUDIOMODE_MONO_SAP_
    /// E_AUDIOMODE_STEREO_SAP_
    /// E_AUDIOMODE_DUAL_A_
    /// E_AUDIOMODE_DUAL_B_
    /// E_AUDIOMODE_DUAL_AB_
    /// E_AUDIOMODE_NICAM_MONO_
    /// E_AUDIOMODE_NICAM_STEREO_
    /// E_AUDIOMODE_NICAM_DUAL_A_
    /// E_AUDIOMODE_NICAM_DUAL_B_
    /// E_AUDIOMODE_NICAM_DUAL_AB_
    /// E_AUDIOMODE_HIDEV_MONO_
    /// E_AUDIOMODE_LEFT_LEFT_
    /// E_AUDIOMODE_RIGHT_RIGHT_
    /// E_AUDIOMODE_LEFT_RIGHT_
    /// @return MAPI_U8         \b IN: MTS mode (Mono/Stereo/Dual)
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetMtsMode();

    //-------------------------------------------------------------------------------------------------
    /// According the return of GetMtsMode to setting the next MTS mode need to set
    /// It depend on the GETMstMode value to decide which kind of part to use (Stereo,A2_Dual,NICAM_Stereo ...)
    /// The next mode will be choosen in circularlly in that part.
    /// MAPI_U8 Stereo[] =
    /// {
    ///     E_AUDIOMODE_FORCED_MONO_,
    ///     E_AUDIOMODE_G_STEREO_,
    /// };
    ///
    /// MAPI_U8 A2_Dual[] =
    /// {
    ///     E_AUDIOMODE_DUAL_A_,
    ///     E_AUDIOMODE_DUAL_B_,
    ///     E_AUDIOMODE_DUAL_AB_,
    /// };
    ///
    /// MAPI_U8 NICAM_Stereo[] =
    /// {
    ///     E_AUDIOMODE_FORCED_MONO_,
    ///     E_AUDIOMODE_NICAM_STEREO_,
    /// };
    ///
    /// MAPI_U8 NICAM_Dual[] =
    /// {
    ///     E_AUDIOMODE_FORCED_MONO_,
    ///     E_AUDIOMODE_NICAM_DUAL_A_,
    ///     E_AUDIOMODE_NICAM_DUAL_B_,
    ///     E_AUDIOMODE_NICAM_DUAL_AB_,
    /// };
    ///
    /// MAPI_U8 NICAM_Mono[] =
    /// {
    ///     E_AUDIOMODE_FORCED_MONO_,
    ///     E_AUDIOMODE_NICAM_MONO_ ,
    /// };
    ///
    /// MAPI_U8 BTSC_Mono_Sap[] =
    /// {
    ///     E_AUDIOMODE_MONO_,
    ///     E_AUDIOMODE_MONO_SAP_ ,
    /// };
    ///
    /// MAPI_U8 BTSC_Stereo_Sap[] =
    /// {
    ///     E_AUDIOMODE_MONO_,
    ///     E_AUDIOMODE_G_STEREO_ ,
    ///     E_AUDIOMODE_STEREO_SAP_ ,
    /// };
    /// Set Audio ATV Next MTS mode
    /// @return SSSOUND_OK: Success
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetToNextMtsMode(void);;

    //-------------------------------------------------------------------------------------------------
    /// Get the available ATV Sound mode (Mono/Stereo/Dual) from the DSP report
    /// The return value has the following mode:
    /// E_AUDIOMODE_INVALID_
    /// E_AUDIOMODE_MONO_
    /// E_AUDIOMODE_FORCED_MONO_
    /// E_AUDIOMODE_G_STEREO_
    /// E_AUDIOMODE_K_STEREO_
    /// E_AUDIOMODE_MONO_SAP_
    /// E_AUDIOMODE_STEREO_SAP_
    /// E_AUDIOMODE_DUAL_A_
    /// E_AUDIOMODE_DUAL_B_
    /// E_AUDIOMODE_DUAL_AB_
    /// E_AUDIOMODE_NICAM_MONO_
    /// E_AUDIOMODE_NICAM_STEREO_
    /// E_AUDIOMODE_NICAM_DUAL_A_
    /// E_AUDIOMODE_NICAM_DUAL_B_
    /// E_AUDIOMODE_NICAM_DUAL_AB_
    /// E_AUDIOMODE_HIDEV_MONO_
    /// E_AUDIOMODE_LEFT_LEFT_
    /// E_AUDIOMODE_RIGHT_RIGHT_
    /// E_AUDIOMODE_LEFT_RIGHT_
    /// @return MAPI_U8         \b IN: MTS mode (Mono/Stereo/Dual)
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetSoundMode();

    //-------------------------------------------------------------------------------------------------
    /// Set main speaker output to  Stereo/LL/RR/Mixed.
    /// It will set the mode both in the decoder output and the sound effect set
    /// @param  mode            \b IN : Stereo/LL/RR/Mixed LR
    /// @return SSSOUND_OK: Success
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 DECODER_SetOutputMode(En_DVB_soundModeType_ mode);

    //-------------------------------------------------------------------------------------------------
    ///  Set sound effect treble setting.
    /// According the UI to set the Treble gain. Treble u8Treble (0~100) mapping to -16~+15dB.
    /// Pseudo code:
    ///    value = get Treble value from UI
    ///    SND_SetTreble(value);
    /// @param u8Treble         \b IN: treble setting ( 0 ~ 100 percent )
    /// @return SSSOUND_OK: Success
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SND_SetTreble(MAPI_U8 u8Treble);

    //-------------------------------------------------------------------------------------------------
    /// Get sound effect treble setting value by the UI set
    /// @return MAPI_U8         \b IN: Current treble setting value( 0 ~ 100 percent
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SND_GetTreble();

    //-------------------------------------------------------------------------------------------------
    /// Set bass sound
    /// According the UI to set the Bass gain. Bass u8Bass (0~100) mapping to -16~+15dB.
    /// Pseudo code:
    /// @code
    ///    value = get Bass value from UI
    ///    SND_SetBass(value);
    /// @endcode
    /// @param u8Bass           \b IN: Bass value (0 ~ 100)
    /// @return SSSOUND_OK: Success
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SND_SetBass(MAPI_U8 u8Bass);

    //-------------------------------------------------------------------------------------------------
    /// Get bass value by the UI set
    /// @return MAPI_U8         \b IN: Current bass value, 0 ~ 100
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SND_GetBass(void);

    //-------------------------------------------------------------------------------------------------
    /// Set balance sound
    /// @param u8Balance        \b IN: Balance value (0 ~ 100)
    ///                                - 0   :  R channel is muted ...
    ///                                - 50  :  L & R channel output the same level...
    ///                                - 100 :  L channel is muted .
    /// Pseudo code:
    /// @code
    ///    value = get balance value from UI
    ///    SND_SetBalance(value);
    /// @endcode
    /// @return SSSOUND_OK: Success
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SND_SetBalance(MAPI_U8 u8Balance);

    //-------------------------------------------------------------------------------------------------
    /// Get balance value
    ///
    /// @return MAPI_U8: Current balance value, 0 ~ 100
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SND_GetBalance();

    //-------------------------------------------------------------------------------------------------
    /// Enable/Disable auto volume
    /// The auto volume threshold level may set first, than use SetAutoVolume(MAPI_BOOL enAutoVol) to Enable/Disable
    /// The threshold leve set can use SetBasicSoundEffect(BSND_AVC, BSND_PARAMETER *bsnd_param);
    /// Pseudo code:
    /// @code
    ///    SetBasicSoundEffect(BSND_AVC, BSND_PARAMETER *bsnd_param);  setting the threshold leve of auto volume
    ///    SetAutoVolume(FALSE); to disable auto volume
    ///    SetAutoVolume(TRUE);  to enable auto volume
    /// @endcode
    /// @param enAutoVol         \b IN: On: 1, Off: 0
    ///
    /// @return SSSOUND_OK: Success
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetAutoVolume(MAPI_BOOL enAutoVol);

    //-------------------------------------------------------------------------------------------------
    /// Set current volume value in system database
    /// Set the path of main speaker volume get from the database
    /// @return SSSOUND_OK: Success
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetAbsoluteVolume(void);

    //-------------------------------------------------------------------------------------------------
    /// Set current volume value in system database
    /// Set the path of main speaker volume get from the database
	/// @param eWin 		\b IN: set main or sub
    /// @return SSSOUND_OK: Success
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetAbsoluteVolume(MAPI_SCALER_WIN eWin);

    //-------------------------------------------------------------------------------------------------
    /// Get status of Auto volume
    ///
    /// @return True: Enable Auto volume
    /// @return False: Disable Auto volume
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL GetAutoVolume(void);

    //-------------------------------------------------------------------------------------------------
    /// Set surround sound effect to Off/On/SRS_TSXT, the mode can set as:
    /// E_SURROUND_MODE_OFF,
    /// E_SURROUND_MODE_ON,
    /// E_SURROUND_MODE_SRS_TSXT,
    /// @param mode             \b IN: surround sound mode  Off: 0, On: 1, SRS_TSXT:2
    /// @return SSSOUND_OK: Success
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetSurroundSound(SOUND_SURROUND_MODE mode);

    //-------------------------------------------------------------------------------------------------
    /// Get surround sound mode that SetSurroundSound aready set
    ///
    /// @return SOUND_SURROUND_MODE: Current surround mode  Off: 0, On: 1, SRS_TSXT:2
    //-------------------------------------------------------------------------------------------------
    SOUND_SURROUND_MODE GetSurroundSound(void);

    //-------------------------------------------------------------------------------------------------
    /// Set the specific band level of 5-band EQ
    /// The level (0~100) mapping to -12dB ~ +12dB
    /// Pseudo code: If we want set Bank1 to Bank 5 to max (+12dB)
    /// @code
    ///   SND_SetEq(0, 100);
    ///   SND_SetEq(1, 100);
    ///   SND_SetEq(2, 100);
    ///   SND_SetEq(3, 100);
    ///   SND_SetEq(4, 100);
    /// @endcode
    /// @param u8band                     \b IN: the band to set, 0 ~ 4
    /// @param u8level                     \b IN: the level set to the band (0 ~ 100)
    /// @return SSSOUND_OK: Success
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SND_SetEq(MAPI_U8 u8band, MAPI_U8 u8level);

#if (ENABLE_LITE_SN != 1)
    //-------------------------------------------------------------------------------------------------
    /// SND_SetSubWooferVolume:To Set sub woofer volume and mute of Amplifier
    /// @param bMute    \b IN: TRUE for Mute enable; FALSE for Mute disable.
    /// @param u8Val    \b IN: Volume Value(0~100), set 0 is mute.
    /// @return       \b OUT: MAPI_TRUE or MAPI_FALSE
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SND_SetSubWooferVolume(MAPI_BOOL bMute, MAPI_U8 u8Val);


    //-------------------------------------------------------------------------------------------------
    /// Reserve extend command for customer. If you don't need it, you skip it.
    /// @param u8SubCmd     \b IN: Commad defined by the customer.
    /// @param u32Param1    \b IN: Defined by the customer.
    /// @param u32Param2    \b IN: Defined by the customer.
    /// @param pvoidParam3    \b IN: Defined by the customer.
    /// @return             \b OUT: MAPI_TRUE or MAPI_FALSE
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SND_AmpExtendCmd(MAPI_U8 u8SubCmd,MAPI_U32 u32Param1,MAPI_U32 u32Param2,void * pvoidParam3);
#endif


    /*********** KTV Function ***********/
    //-------------------------------------------------------------------------------------------------
    /// Set each volume in KTV mode
    /// The AUDIO_MIX_VOL_TYPE_ can choice which volume need to control in KTV mix mode (PCM_VOL_,MIC_VOL_, MP3_VOL_ ..)
    /// Pseudo code:
    /// For example, if Microphone want setting volume to -2.125dB:
    /// @code
    ///   KTV_SetMixModeVolume(MIC_VOL_, 0x0E, 0x01); should be applied
    /// @endcode
    /// @param VolType          \b IN: Audio volume type
    /// @param u8Vol1            \b IN: MSB 7-bit register value of 10-bit u8Volume (0x00 ~ 0x7E, gain: +12db to   -114db (-1 db per step))
    /// @param u8Vol2            \b IN: LSB 3-bit register value of 10-bit u8Volume (0x00 ~ 0x07, gain:  -0db to -0.875db (-0.125 db per step))
    ///
    /// @return SSSOUND_OK: Success
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 KTV_SetMixModeVolume(AUDIO_MIX_VOL_TYPE_ VolType, MAPI_U8 u8Vol1, MAPI_U8 u8Vol2);

    //-------------------------------------------------------------------------------------------------
    /// Enable/Disable each volume in KTV mode to mute
    /// The AUDIO_MIX_VOL_TYPE_ can choice which mute need to set in KTV mix mode (PCM_VOL_,MIC_VOL_, MP3_VOL_ ..)
    /// Pseudo code:
    /// For example, if Microphone want set UnMute
    /// @code
    ///   KTV_SetMixModeMute(MIC_VOL_, Disable); should be applied
    /// @endcode
    /// @param VolType          \b IN: Audio volume type
    /// @param EnMute           \b IN: Enable: 1, Disable: 0
    ///
    /// @return SSSOUND_OK: Success
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 KTV_SetMixModeMute(AUDIO_MIX_VOL_TYPE_ VolType, MAPI_BOOL EnMute);

    //-------------------------------------------------------------------------------------------------
    /// This routine is used to set S/PDIF output mode
    /// According the UI to set the S/PDIF mode.
    /// Pseudo code:
    /// @code
    ///    spdif_mode = get Treble value from UI
    ///    SetSPDIFmode(spdif_mode);
    /// @endcode
    /// @param mode            \b IN: PCM mode: 0, SPDIF off: 1, nonPCM mode:2
    ///
    /// @return SSSOUND_OK: Success.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetSPDIFmode(SPDIF_TYPE_ mode);

    //-------------------------------------------------------------------------------------------------
    /// This routine is used to set SPDIF delay time.
    ///    If the spdif need delay 50ms to sync video
    /// Pseudo code:
    /// @code
    ///    SetSPDIFDelay(50);
    /// @endcode
    /// @param  delay       \b IN: Buffer Process Value, 0 ~ 250 (unit:ms).
    /// @return SSSOUND_OK: Success.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetSPDIFDelay(U32 delay);

    //-------------------------------------------------------------------------------------------------
    /// This routine is used to set SPDIF hardware enable.
    /// @param  eHWMode       \b IN: enable/disable SPDIF hareware
    /// @return SSSOUND_OK: Success.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetSPDIFHWMode(SPDIF_HW_MODE_ eHWMode);

    //-------------------------------------------------------------------------------------------------
    /// This routine is used to  set S/PDIF mute.
    /// @param bMute            \b IN: SPDIF On: 0, SPDIF off: 1
    /// @return SSSOUND_OK: Success.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetSPDIFMute(MAPI_BOOL bMute);

    //-------------------------------------------------------------------------------------------------
    /// This routine is used to set HDMI Tx mode.
    /// @param  eHDMITxMode       \b IN:MSAPI_HDMI_MODE_PCM, MSAPI_HDMI_MODE_RAW, MSAPI_HDMI_MODE_UNKNOWN
    /// @return SSSOUND_OK: Success.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetHDMITxMode(HDMI_TYPE_ eHDMITxMode);

    //-------------------------------------------------------------------------------------------------
    /// This routine is used to set Hidev mode
    /// @param eMode            \b IN: Hidev mode: 0,  off: 1:  bw 250Hz ,2: bw 280Hz, 3: bw 320Hz
    ///
    /// @return SSSOUND_OK: Success.
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SoundSetHidevMode(SOUND_HIDEV_INDEX eMode);

    //-------------------------------------------------------------------------------------------------
    /// This routine is used to set the Audysessy Dynamic Volume.
    /// @param  bOnoff      \b IN: Enable: 1, Disable: 0.
    /// @return SSSOUND_OK: Success.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetAudysessyDynaVol(MAPI_BOOL bOnoff);

    //-------------------------------------------------------------------------------------------------
    /// This routine is used to set PEQ enable or disable.
    /// @param  bOnoff      \b IN: Enable: 1, Disable: 0.
    /// @return SSSOUND_OK: Success.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetPEQ(MAPI_BOOL bOnoff);

    //-------------------------------------------------------------------------------------------------
    /// This routine is used to set SRS enable or disable.(Please note:This api will be removed later, please use SetSoundSound instead)
    /// @param  bOnoff      \b IN: Enable: 1, Disable: 0.
    /// @return SSSOUND_OK: Success.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetSRS(MAPI_BOOL bOnoff);

    //-------------------------------------------------------------------------------------------------
    /// This routine is used to get SRS sound mode.
    /// @return MAPI_BOOL: \b IN: Enable: 1, Disable: 0.
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL GetSRSMode(void);

    //-------------------------------------------------------------------------------------------------
    /// This routine is used to set SND Speaker delay time.
    ///    If the main speaker need delay 50ms to sync video
    /// Pseudo code:
    /// @code
    ///    SetSNDSpeakerDelay(50);
    /// @endcode
    /// @param  delay       \b IN: Buffer Process Value, 0 ~ max.ms (unit:ms). max depend on chip resource (200~ )
    /// @return SSSOUND_OK: Success.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetSNDSpeakerDelay(U32 delay);

    //------------------------------------------------------------------------------------------
    /// The routine provide MPEG audio decoder setting. The type has:
    /// {
    ///   Audio_MPEG_infoType_DecStatus_,         //R         //0:unlock  1:lock
    ///   Audio_MPEG_infoType_SampleRate_,        //R         //
    ///   Audio_MPEG_infoType_BitRate_,           //R         //
    ///   Audio_MPEG_infoType_FrameNum_,          //R         //
    ///   Audio_MPEG_infoType_SoundMode_,         //W         //0: LR / 1:LL / 2:RR
    ///   Audio_MPEG_infoType_stereoMode_,        //R         //0: Stereo / 1: / 2:RR
    ///   Audio_MPEG_infoType_Layer_,             //R         //1 / 2 / 3
    ///   Audio_MPEG_infoType_Header_,            //R
    ///   Audio_MPEG_infoType_FFSpeed_,
    ///   Audio_MPEG_infoType_MMFileSize_         //W         //enum FILE_SIZE
    /// }
    /// For example, if user want to set decoder sound mode to LL
    ///  Pseudo code may like:
    /// @code
    ///    SetSNDMpegInfo(Audio_MPEG_infoType_SoundMode_, 1, 0);
    /// @endcode
    /// @brief \b Function \b Name: SND_SetMpegInfo(Audio_MPEG_infoType_ infoType, MAPI_U32 param1, MAPI_U32 param2);
    /// @brief \b Function \b Description:
    /// @param  infoType (Please check "Audio_MPEG_infoType_")
    /// @param  param1 : depend on infoType
    /// @param  param2 : depend on infoType
    /// @return TRUE(ok) or FALSE(fail)
    //------------------------------------------------------------------------------------------
    MAPI_U8 SetSNDMpegInfo(const Audio_MPEG_infoType_ infoType,const MAPI_U32 param1, const MAPI_U32 param2);

    //------------------------------------------------------------------------------------------
    /// The routine can get some audio setting in current stage. The type has
    /// {
    ///   Sound_GET_PARAM_PreScale_,
    ///   Sound_GET_PARAM_Balance_L_,
    ///   Sound_GET_PARAM_Balance_R_,
    ///   Sound_GET_PARAM_EQ_,
    ///   Sound_GET_PARAM_Surround_XA_,
    ///   Sound_GET_PARAM_Surround_XB_,
    ///   Sound_GET_PARAM_Surround_XK_,
    ///   Sound_GET_PARAM_Surround_LPFGAIN_,
    ///   Sound_GET_PARAM_Treble_,
    ///   Sound_GET_PARAM_Bass_,
    ///   Sound_GET_PARAM_Avc_Mode_,
    ///   Sound_GET_PARAM_Avc_Threshold_,
    ///   Sound_GET_PARAM_Avc_AT_,
    ///   Sound_GET_PARAM_Avc_RT_,
    ///   Sound_GET_PARAM_AudioDelay_,
    ///   Sound_GET_PARAM_DCOffet_,
    ///   Sound_GET_PARAM_NR_Threshold_,
    ///   Sound_GET_PARAM_NR_Status_,
    ///   Sound_GET_PARAM_getSignal_Energy_,
    ///   Sound_GET_PARAM_EQ_Status_,
    ///   Sound_GET_PARAM_PEQ_Status_,
    ///   Sound_GET_PARAM_Tone_Status_,
    ///   Sound_GET_PARAM_AutoVolume_Status_,
    ///   Sound_GET_PARAM_Surround_Status_,
    ///   Sound_GET_PARAM_HPF_Status_,
    ///   Sound_GET_PARAM_Drc_Threshold_,
    ///   Sound_GET_PARAM_DMAReader_Buffer_Addr_,
    /// }
    /// For example, if user want to know the audio auto volume (AVC) threshold value.
    ///  Pseudo code may like:
    /// @code
    ///    value = SND_GetParam(Sound_GET_PARAM_Avc_Threshold_, 0); (It only need set param1, param2 don't care)
    /// @endcode
    ///    value is return the AVC threshold value
    /// @brief \b Function \b Name: SND_GetParam(Sound_SET_PARAM_Type_ Type, MAPI_S16 param1);
    /// @brief \b Function \b Description:
    ///@param  Type       \b IN: Which sound type info you want to get
    ///@param param1 \b IN: sound type special paraml
    /// @return MAPI_BOOL: sound mode type.
    //------------------------------------------------------------------------------------------
    MAPI_S16 SND_GetParam(const Sound_GET_PARAM_Type_ Type,const MAPI_S16 param1);

    //------------------------------------------------------------------------------------------
    /// The routine can get some audio setting in current stage. The type has
    /// {
    /// Sound_SET_PARAM_PreScale_,
    /// Sound_SET_PARAM_Balance_,
    /// Sound_SET_PARAM_EQ_,
    /// Sound_SET_PARAM_Surround_XA_,
    /// Sound_SET_PARAM_Surround_XB_,
    /// Sound_SET_PARAM_Surround_XK_,
    /// Sound_SET_PARAM_Surround_LPFGAIN_,
    /// Sound_SET_PARAM_Treble_,
    /// Sound_SET_PARAM_Bass_,
    /// Sound_SET_PARAM_AbsoluteBass_,
    /// Sound_SET_PARAM_Avc_Mode_,
    /// Sound_SET_PARAM_NR_Threshold_,
    /// Sound_SET_PARAM_Avc_Threshold_,
    /// Sound_SET_PARAM_Avc_AT_,
    /// Sound_SET_PARAM_Avc_RT_,
    /// Sound_SET_PARAM_AudioDelay_,
    /// Sound_SET_PARAM_DCOffet_,
    /// Sound_SET_PARAM_PEQ_48K_A0_,
    /// Sound_SET_PARAM_PEQ_48K_A1_,
    /// Sound_SET_PARAM_PEQ_48K_A2_,
    /// Sound_SET_PARAM_PEQ_48K_B1_,
    /// Sound_SET_PARAM_PEQ_48K_B2_,
    /// Sound_SET_PARAM_PEQ_32K_A0_,
    /// Sound_SET_PARAM_PEQ_32K_A1_,
    /// Sound_SET_PARAM_PEQ_32K_A2_,
    /// Sound_SET_PARAM_PEQ_32K_B1_,
    /// Sound_SET_PARAM_PEQ_32K_B2_,
    /// Sound_SET_PARAM_AbsoluteEQ_,
    /// Sound_SET_PARAM_Drc_Threshold_,
    /// Sound_SET_PARAM_DMAReader_,
    /// }
    /// For example, if user want to set the audio auto volume (AVC) threshold level to -16dB
    ///  Pseudo code may like:
    /// @code
    ///    SND_SetParam(Sound_SET_PARAM_Avc_Threshold_, 0x20, 0);  (It only need set param1, param2 don't care)
    /// @endcode
    /// @brief \b Function  \b Name: SND_SetParam(Sound_SET_PARAM_Type_ Type, MAPI_S16 param1, MAPI_S16 param2);
    /// @brief \b Function  \b Description:
    /// @param  Type        \b  Sound_SET_PARAM_Type_
    /// @param  param1      \b Sound Effect Process First Parameter
    /// @param  param2      \b Sound Effect Process Second Parameter
    /// @return SSSOUND_OK: Success.
    //------------------------------------------------------------------------------------------
    MAPI_U8 SND_SetParam(const Sound_SET_PARAM_Type_ Type, const MAPI_S16 param1,const MAPI_S16 param2);

    //-------------------------------------------------------------------------------------------------
    /// Select Main/Sub/Scart  source
    ///  If user want to set change the Main channel to DTV and Sub channel to VGA
    ///  Pseudo code may like:
    /// @code
    ///  SetAudioSource(MAPI_INPUT_SOURCE_DTV, MSRV_AUDIO_PROCESSOR_MAIN);
    ///  SetAudioSource(MAPI_INPUT_SOURCE_VGA, MSRV_AUDIO_PROCESSOR_SUB);
    /// @endcode
    /// @param eInputSrc    \b IN: MAPI_INPUT_SOURCE_TYPE Pointer
    /// @param eType    \b IN: For Main /SUB / SCART
    /// @return MSRV_SSSND_RET: RETURN_OK
    //-------------------------------------------------------------------------------------------------
    MSRV_SSSND_RET  SetAudioSource(MAPI_INPUT_SOURCE_TYPE eInputSrc, MSRV_AUDIO_PROCESSOR_TYPE eType);

    //-------------------------------------------------------------------------------------------------
    ///  Set Primary Audio Language
    ///
    /// @code
    ///  SetAudioLanguage1(int);
    /// @endcode
    /// @param enLanguage    \b IN: Language Index
    /// @return MSRV_SSSND_RET: RETURN_OK
    //-------------------------------------------------------------------------------------------------
    MSRV_SSSND_RET  SetAudioLanguage1(int enLanguage);

    //-------------------------------------------------------------------------------------------------
    ///  Set Secondary Audio Language
    ///
    /// @code
    ///  SetAudioLanguage2(int);
    /// @endcode
    /// @param enLanguage    \b IN: Language Index
    /// @return MSRV_SSSND_RET: RETURN_OK
    //-------------------------------------------------------------------------------------------------
    MSRV_SSSND_RET  SetAudioLanguage2(int enLanguage);

    //-------------------------------------------------------------------------------------------------
    ///  Get Primary Audio Language
    ///
    /// @code
    ///  GetAudioLanguage1();
    /// @endcode
    /// @return int: audio language 1
    //-------------------------------------------------------------------------------------------------
    int  GetAudioLanguage1();

    //-------------------------------------------------------------------------------------------------
    ///  Get Secondary Audio Language
    ///
    /// @code
    ///  GetAudioLanguage2();
    /// @endcode
    /// @return int: audio language 2
    //-------------------------------------------------------------------------------------------------
    int  GetAudioLanguage2();

    //-------------------------------------------------------------------------------------------------
    /// The rountine to decide use which data capture ID to capture the eSource path
    /// For example, if use want use first capture ID(MSRV_CAPTURE_DEVICE_TYPE_DEVICE0) to get the Microphone path.
    /// Pseudo code may like:
    /// @code
    ///    SetAudioCaptureSource(MSRV_CAPTURE_DEVICE_TYPE_DEVICE0,MSRV_CAPTURE_MICROPHONE_SOUND);
    /// @endcode
    /// MSRV_AUDIO_CAPTURE_DEVICE_TYPE has the following, now only use MSRV_CAPTURE_DEVICE_TYPE_DEVICE0 and MSRV_CAPTURE_DEVICE_TYPE_DEVICE1
    /// {
    ///   MSRV_CAPTURE_DEVICE_TYPE_DEVICE0,
    ///   MSRV_CAPTURE_DEVICE_TYPE_DEVICE1,
    ///   MSRV_CAPTURE_DEVICE_TYPE_DEVICE2,
    ///   MSRV_CAPTURE_DEVICE_TYPE_DEVICE3,
    ///   MSRV_CAPTURE_DEVICE_TYPE_DEVICE4,
    /// }
    /// MSRV_AUDIO_CAPTURE_SOURCE has the following kind:
    /// {
    ///   MSRV_CAPTURE_MAIN_SOUND,
    ///   MSRV_CAPTURE_SUB_SOUND,
    ///   MSRV_CAPTURE_MICROPHONE_SOUND,
    ///   MSRV_CAPTURE_MIXED_SOUND,
    ///   MSRV_CAPTURE_USER_DEFINE1 ,
    ///   MSRV_CAPTURE_USER_DEFINE2,
    ///   MSRV_CAPTURE_SOURCE_MAX,
    /// }
    /// @brief  \b Function  \b Name: SetAudioCaptueSource()
    /// @brief  \b Function  \b Description: Set Audio data Capture Source
    /// @param  eAudioDeviceType    \b : audio device type
    /// @param  eSource    \b : data source type
    /// @return \b MAPI_BOOL    : True(success)/False(fail)
    //-------------------------------------------------------------------------------------------------
    MSRV_SSSND_RET SetAudioCaptureSource(MSRV_AUDIO_CAPTURE_DEVICE_TYPE eAudioDeviceType, MSRV_AUDIO_CAPTURE_SOURCE eSource);


	//-------------------------------------------------------------------------------------------------
	/// @brief \b Function \b Name: SetOutputSourceInfo(AUDIO_VOL_SOURCE_TYPE eAudioPath,MSRV_AUDIO_CAPTURE_SOURCE eSource)
	/// @brief \b Function \b Description: Get audio output port source information
	/// @param eAudioPath \b  :	audio path
	/// @param    eSource     \b  : audio output port source information
	/// @return \b  MAPI_BOOL    : True(success)/False(fail)
	//-------------------------------------------------------------------------------------------------
	MSRV_SSSND_RET SetOutputSourceInfo(AUDIO_VOL_SOURCE_TYPE eAudioPath, MSRV_AUDIO_PROCESSOR_TYPE eSource);

    ////////////////////////////////////////////////////////////////////////////////
    /// The rountine can get information in KTV mode, the infomation type are:
    /// {
    ///   KTV_GETINFO_PCM_BUFLEVEL
    ///   KTV_GETINFO_DEC_TimeStamp,
    /// }
    /// For example, if user need monitor the PCM buffer level:
    ///  Pseudo code may like:
    /// @code
    ///  PCMlevel = SND_GetKTVInfo(KTV_GETINFO_PCM_BUFLEVEL);
    /// @endcode
    /// KTV_GetInfo: This function get KTV information by modelType
    /// @param  infoType:  AUD_KTV_infoType1_,
    ///                    AUD_KTV_infoType2_,
    ///                    AUD_KTV_infoType3_,
    ///                    ....
    /// @return MAPI_U64   return KTV value
    ////////////////////////////////////////////////////////////////////////////////
    MAPI_U64 SND_GetKTVInfo(const KTV_GET_INFO_TYPE_ infoType);

    ////////////////////////////////////////////////////////////////////////////////
    /// The rountine can set feature in KTV mode, the feature type are:
    /// {
    ///  KTV_SETINFO_MODEL
    ///  KTV_SETINFO_ADC_GAIN,
    ///  KTV_SETINFO_DEC_MUTE,
    ///  KTV_SETINFO_DEC_PLAY_WO_OUT,
    ///  KTV_SETINFO_ADC1_GAIN,
    ///  KTV_SETINFO_MIC_SOUNDMODE,
    ///  KTV_SETINFO_BG_MUSIC_SOUNDMODE,
    /// }
    /// KTV_SetInfo: This function set KTV parameter by modelType
    /// @param  infoType  \b KTV_SET_INFO_TYPE_
    /// @param  param1 \b depend on ktv set info type
    /// @param  param2  \b depend on ktv set info type
    /// @return SSSOUND_OK: Success.
    ////////////////////////////////////////////////////////////////////////////////
    MAPI_U8 SND_KTV_SetInfo(const KTV_SET_INFO_TYPE_ infoType,const MAPI_U32 param1,const MAPI_U32 param2);

    //-------------------------------------------------------------------------------------------------
    /// Config the DMA reader for KTV mode or others
    ///  If the source input type is E_AUDIO_INFO_KTV_IN_ or E_AUDIO_INFO_GAME_IN_, this routine can assing samping rate
    ///  Other input source the DMA reader sampling rate always keep 48KHz
    /// @brief \b Function \b Name: SND_DmaReader_Init()
    /// @brief \b Function \b Description:
    /// @param sampleRate        \b IN : sample Rate
    /// @param sourceInfo       \b IN:    sourceInfo
    /// @return SSSOUND_OK: Success.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SND_DmaReader_Init(const SAMPLE_RATE_ sampleRate,const AUDIO_SOURCE_INFO_TYPE_ sourceInfo);

    //-------------------------------------------------------------------------------------------------
    /// This rountine to Start or Stop the DMA reader, make sure the SND_DmaReader_Init already setting.
    /// Procedure of Pseudo code:
    /// @code
    ///   //set KTV mode and in sampling rate of 44.1KHz
    ///   if (SND_DmaReader_Init(SAMPLE_RATE_44100_, E_AUDIO_INFO_KTV_IN_) == TRUE);
    ///   {
    ///           SND_DmaReader_SetCommand(DMA_START_);
    ///   }
    ///   else
    ///   {
    ///           /// DMA Reader init fail
    ///   }
    /// @endcode
    /// @brief \b Function \b Name: SND_DmaReader_SetCommand()
    /// @brief \b Function \b Description: This routine is used to control DMA reader Buffer
    /// @param DMAcommand        \b IN : STOP / Start
    /// @return SSSOUND_OK: Success.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SND_DmaReader_SetCommand(const DMA_COMMAND_ DMAcommand);

    //-------------------------------------------------------------------------------------------------
    /// The system prepare PCM data than feed to audio DSP to play out.
    /// Pseudo code Step:
    /// @code
    ///  ///Step 1: make sure SND_DmaReader_Init, SND_DmaReader_SetCommand already run
    ///   if (SND_DmaReader_Init(SAMPLE_RATE_44100_, E_AUDIO_INFO_KTV_IN_) == TRUE);
    ///   {
    ///       SND_DmaReader_SetCommand(DMA_START_);
    ///   }
    ///   else
    ///   {
    ///         //there is problem for init DMA reader,  put your error handling codes here or ASSERT it.
    ///         ASSERT(0);
    ///         return;    //break or return, no need process step2 and step3
    ///   }
    ///  ///Step 2: System collect PCM data to BUFFER from internet, internal, ......
    ///  ///Step 3: Every constant period of time feed the data to DSP
    ///    SND_DmaReader_WritePCM(BUFFER, size);
    /// @endcode
    /// @brief \b Function \b Name: SND_DmaReader_WritePCM()
    /// @brief \b Function \b Description: This routine is used to write PCM data into DMA reader Buffer
    /// @param buffer       \b IN   : buffer address
    /// @param bytes        \b IN   : buffer size (must be multiple of 8 Byte)
    ///@return MAPI_BOOL: true or false
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SND_DmaReader_WritePCM(void* const buffer,const MAPI_U32 bytes);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: SND_AUDIO_IO_PCMUploadCtrl
    /// @brief \b Function \b Description: Set STOP/START to PCM upload function
    /// @param up_control       \b IN  UPLAOD_STOP_ / UPLOAD_START_
    ///@return SSSOUND_OK: Success.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SND_AUDIO_IO_PCMUploadCtrl(const PCMUPLOAD_COMMAND_ up_control);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: SND_AUDIO_IO_CheckPCMUploadRequest
    /// @brief \b Function \b Description: Check if pcm ready for upload to MIPS.
    /// @param pU32WrtAddr \b IN : If PCM upload ready, this func will set the address info in this variable
    /// @param pU32WrtBytes   \b IN :  If PCM upload ready, this func will set the size info in this variable
    /// @param index       \b IN :
    /// @return MAPI_BOOL: true or false
    /// index will increase one every time the PCM upload request is ready
    /// @code
    /// //start PCM upload process
    /// MAPI_BOOL ready;
    /// SND_AUDIO_IO_PCMUploadCtrl(UPLOAD_START_);
    ///
    /// ready = SND_AUDIO_IO_CheckPCMUploadRequest(*WrtAddr, *WrtBytes, * index);
    ///
    /// if (ready == 1 && index == pre_inedx+1 ){
    ///    // Data is ready from address of WrtAddr,and length is WrtBytes bytes.
    ///    // memory copy the pcm data to other application, like blue tooth device
    ///    pre_index = index;
    /// }
    /// @endcode
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SND_AUDIO_IO_CheckPCMUploadRequest(MAPI_U32*  const pU32WrtAddr, MAPI_U32* const pU32WrtBytes, MAPI_U32* const index);

    //-------------------------------------------------------------------------------------------------
    /// Audio andvanced sound effect (SRS, AUDYSSEY, ...) sub functions enable/disable.
    /// The sub function type are:
    /// {
    ///  SRS_THEATERSOUND_TSHD_,
    ///  SRS_THEATERSOUND_TRUBASS_,
    ///  SRS_THEATERSOUND_DC_,
    ///  SRS_THEATERSOUND_DEFINITION_,
    ///  SRS_THEATERSOUND_TRUVOLUME_,
    ///  SRS_THEATERSOUND_HARDLIMITER_,
    ///  SRS_THEATERSOUND_HPF_,
    ///  SRS_THEATERSOUND_TRUEQ_,
    ///  SRS_THEATERSOUND_TRUVOLUME_NOISE_MNGR_,
    ///  ....
    ///  }
    ///
    /// SND_ADV_SubProcessEnable: Set ADVANCE Sub-Process Type.
    /// @param  proc  : Sub-Process Type
    /// @param  enable : enable or not
    /// @return FALSE : No Sub-Process Type or Disable
    ///         TRUE  : Sub-Process Type Enable
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SND_ADV_SubProcessEnable(const ADVFUNC_ proc,const MAPI_BOOL enable);

    //-------------------------------------------------------------------------------------------------
    /// Audio andvanced sound effect (SRS, AUDYSSEY, ...) parameters setging rountine
    /// The meaning of u16value1 and u16value2 are different for different case type
    /// Please check the application note for different if need it
    ///{
    ///  SRS_TSXT_SET_INPUT_GAIN_,
    ///  SRS_TSXT_SET_DC_GAIN_,
    ///  SRS_TSXT_SET_TRUBASS_GAIN_,
    ///  SRS_TSXT_SET_SPEAKERSIZE_,
    ///  SRS_TSXT_SET_INPUT_MODE_,
    ///  SRS_TSXT_SET_OUTPUT_GAIN_,
    ///  ....
    ///}
    /// @brief \b Function \b Name: SND_ADV_SetParam()
    /// @brief \b Function \b Description: Set ADVANCE Parameter.
    /// @param   param        \b IN param : Advance Sound Parameter Type
    /// @param   u16value1   \b IN :Parameter value1
    /// @param   u16value2   \b IN :Parameter value2
    /// @return  MAPI_BOOL    \b FALSE : Setting Fail
    ///                       \b TRUE  : Setting OK
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SND_ADV_SetParam(const ADVSND_PARAM_ param,const MAPI_U16 u16value1,const MAPI_U16 u16value2);

    //-------------------------------------------------------------------------------------------------
    /// SND_Key_SetInfo: This function is for setting MENU or KEY sound init configuration.
    /// @return SSSOUND_OK: Success.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SND_Key_SetInfo(void);

    //-------------------------------------------------------------------------------------------------
    /// SND_Key_Start: This function start to feeding MENU or KEY PCM to DDR for DSP.
    /// @param  keyinfo   \b IN :pointer to structure for setting DDR start/end/size setting for DSP
    /// @param  FileSrc   \b IN :file malloc address
    /// @param  FileSize   \b IN :file length
    /// @return SSSOUND_OK: Success.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SND_Key_Start(AUDIO_KEY_INFO *keyinfo, MAPI_U8* FileSrc, MAPI_U32 FileSize);

    //-------------------------------------------------------------------------------------------------
    ///  Set Sound Noise Reduce Threshold.
    ///  If the value of Threshold is not equal zero, that means the audio noise reduction will auto eable
    /// @param value         \b IN: Threshold setting ( 0 ~ 100  )
    /// @return SSSOUND_OK: Success
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SND_SetNrThreshold(const MAPI_U32 value);

    //-------------------------------------------------------------------------------------------------
    /// Sys & Sound related interface function is used to Java kind of sound property.
    //-------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------
    /// Java Audio Initialize
    /// @return MSRV_SSSND_RET: RETURN_OK
    //-------------------------------------------------------------------------------------------------
    MSRV_SSSND_RET  SetInit(void);

    //-------------------------------------------------------------------------------------------------
    /// Set Audio internal/ouput port gain .
    /// Pseudo code:
    /// @code
    ///    ///set scart output gain keep to +3dB, set Headphone output gain keep to +4dB
    ///     U8 S_Gain; HP_Gain;
    ///     S_Gain = 3*8, HP_Gain = 4*8;
    ///
    ///     SetOutputGain(PORT_SCART, S_Gain);
    ///     SetOutputGain(PORT_HP, HP_Gain);
    /// @endcode
    /// @param ePort   \b IN: volume source type
    /// @param sGainStep        \b IN: Gain range from -114*8 ~ +12*8 (-114dB~+12db) ; 0.125dB/step
    /// @return MSRV_SSSND_RET: RETURN_OK, RETURN_NOTOK, RETURN_NOTSUPPORT
    //-------------------------------------------------------------------------------------------------
    MSRV_SSSND_RET  SetOutputGain(AUDIO_BASIC_PORT_TYPE ePort, int sGainStep);

    //-------------------------------------------------------------------------------------------------
    /// Setting the particular output path volume by the u8Vol
    /// Set Audio volume by different output.
    /// Set Audio Volume (for Java use)
    /// @param volSrcType   \b IN: volume source type
    /// @param u8Vol        \b IN: Volume value range from 0~100
    ///  @return MSRV_SSSND_RET: RETURN_OK, RETURN_NOTOK, RETURN_NOTSUPPORT
    //-------------------------------------------------------------------------------------------------
    MSRV_SSSND_RET  SetAudioVolume(AUDIO_VOL_SOURCE_TYPE volSrcType,MAPI_U8 u8Vol);

    //-------------------------------------------------------------------------------------------------
    /// Get particular path of Audio Volume (for Java use)
    /// @param volSrcType   \b IN: volume source type
    /// @return u8Vol       \b OUT: return volume value range from 0~100
    //-------------------------------------------------------------------------------------------------
    MAPI_U8  GetAudioVolume(AUDIO_VOL_SOURCE_TYPE volSrcType);

    //-------------------------------------------------------------------------------------------------
    /// Set Audio Input Level before mix.
    /// @param inputLvlSrcType   \b IN: input level source type
    /// @param u8Vol        \b IN: volume value range from 0~100
    /// @return MSRV_SSSND_RET: RETURN_OK, RETURN_NOTOK, RETURN_NOTSUPPORT
    //-------------------------------------------------------------------------------------------------
    MSRV_SSSND_RET SetAudioInputLevel(AUDIO_INPUT_LEVEL_SOURCE_TYPE inputLvlSrcType, MAPI_U32 u8Vol);

    //-------------------------------------------------------------------------------------------------
    /// Get Audio Input Level that ever set.
    /// For example, in KTV mode, there are mp3 and microphone input. User can set the different level for mp3 or microphone by SetAudioInputLevel
    /// And use GetAudioInputLevel to get the level that ever set.
    /// @code
    ///   // set mp3 input to max.(100) and microphone input to 50 in KTV mode
    ///   MAPI_U8 A_Gain, B_Gain;
    ///
    ///   SetAudioInputLevel(VOL_SOURCE_PREMIXER_KTV_MP3_IN, 100);
    ///   SetAudioInputLevel(VOL_SOURCE_PREMIXER_KTV_MIC_IN, 50;
    ///   // Use GetAudioInputLevel to get the level of mp3 and microphone
    ///   A_Gain = GetAudioInputLevel(VOL_SOURCE_PREMIXER_KTV_MP3_IN);
    ///   B_Gain = GetAudioInputLevel(VOL_SOURCE_PREMIXER_KTV_MIC_IN);
    ////  //A_Gain should be 100 and B_Gain should be 50
    ///
    /// @endcode
    /// @param inputLvlSrcType   \b IN: volume source type
    /// @return u8Vol       \b OUT: return level value range from 0~100
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetAudioInputLevel(AUDIO_INPUT_LEVEL_SOURCE_TYPE inputLvlSrcType);

    //-------------------------------------------------------------------------------------------------
    /// Enable/Disable Basic Sound Effect Type
    /// There are some types of basic audio sound effect for user to enable/disable
    /// BSND_PRESCALE,
    /// BSND_TREBLE,
    /// BSND_BASS,
    /// BSND_BALANCE,
    /// BSND_EQ,
    /// BSND_PEQ,
    /// BSND_AVC,
    /// BSND_Surround,
    /// BSND_DRC,
    /// BSND_NR,
    /// BSND_ECHO,
    /// @code
    ///   //enable noise reduction and disable echo function
    ///   EnableBasicSoundEffect(BSND_NR, Enable);
    ///   EnableBasicSoundEffect(BSND_ECHO, Disable);
    /// @endcode
    /// @param BSndType     \b IN: BSOUND_EFFECT_TYPE
    /// @param BSOUND_OnOff \b IN: TRUE is ON, FALSE is OFF
    /// @return MSRV_SSSND_RET: RETURN_OK, RETURN_NOTOK, RETURN_NOTSUPPORT
    //-------------------------------------------------------------------------------------------------
    MSRV_SSSND_RET EnableBasicSoundEffect(BSOUND_EFFECT_TYPE BSndType, MAPI_BOOL BSOUND_OnOff);

    //-------------------------------------------------------------------------------------------------
    /// Set Basic Sound Effect Parameter.
    /// There are some types of basic audio sound effect for user to set their sub parameters
    /// BSND_PRESCALE,
    /// BSND_TREBLE,
    /// BSND_BASS,
    /// BSND_BALANCE,
    /// BSND_EQ,
    /// BSND_PEQ,
    /// BSND_AVC,
    /// BSND_Surround,
    /// BSND_DRC,
    /// BSND_NR,
    /// BSND_ECHO,
    /// @param BSndType     \b IN: BSOUND_EFFECT_TYPE
    /// @param bsnd_param   \b IN: Parameter
    /// @return MSRV_SSSND_RET: RETURN_OK, RETURN_NOTOK, RETURN_NOTSUPPORT
    //-------------------------------------------------------------------------------------------------
    MSRV_SSSND_RET  SetBasicSoundEffect(BSOUND_EFFECT_TYPE BSndType, BSND_PARAMETER *bsnd_param);

    //-------------------------------------------------------------------------------------------------
    /// Set PreScale Table for different input type.
    /// @param enCurrentInputType     \b IN: Current Input Type
    /// @param u8PreScale   \b IN: PreScale
    /// @return NONE
    //-------------------------------------------------------------------------------------------------
    void SetPreScaleTable(MAPI_INPUT_SOURCE_TYPE enCurrentInputType, MAPI_U8 u8PreScale = 0x0);

    //-------------------------------------------------------------------------------------------------
    /// Get Basic Sound Effect Parameter that ever set by SetBasicSoundEffect
    /// @param bsnd_param     \b IN: BSND_GET_PARAMETER_TYPE
    /// @return MAPI_U16 : BSND_PARAMETER
    /// @code
    /// MAPI_U16 prescale;
    ///  BSND_PARAMETER param;
    ///  param.BSND_PARAM_PRESCALE = 100;
    ///  // current source prescale to 100
    ///  SetBasicSoundEffect(BSND_PRESCALE, &param);
    ///  // get the current source prescale
    ///  prescale = GetBasicSoundEffect(BSND_PRESCALE);    //prescale should be 100
    /// @endcode
    /// There are some types of basic audio sound effect for user to set/get their sub parameters
    /// BSND_PRESCALE,
    /// BSND_TREBLE,
    /// BSND_BASS,
    /// BSND_BALANCE,
    /// BSND_EQ,
    /// BSND_PEQ,
    /// BSND_AVC,
    /// BSND_Surround,
    /// BSND_DRC,
    /// BSND_NR,
    /// BSND_ECHO,
    //-------------------------------------------------------------------------------------------------
    MAPI_U16  GetBasicSoundEffect(BSND_GET_PARAMETER_TYPE bsnd_param);

    //-------------------------------------------------------------------------------------------------
    /// Enable Advanced Sound Effect Type
    /// Now the advanced souned effect has the following type:
    /// ADVSND_DOLBY_PL2VDS,
    /// ADVSND_DOLBY_PL2VDPK,
    /// ADVSND_BBE,
    /// ADVSND_SRS_TSXT,
    /// ADVSND_SRS_TSHD,
    /// ADVSND_SRS_THEATERSOUND,
    /// ADVSND_DTS_ULTRATV,
    /// ADVSND_AUDYSSEY,
    /// ADVSND_SUPER_VOICE,
    /// ADVSND_DBX,
    /// @param AdvSndType   \b IN: ADVANCESND_TYPE
    /// @param AdvSubProc   \b IN: ADVSND_SUBPROC
    /// @return MSRV_SSSND_RET: RETURN_OK, RETURN_NOTOK, RETURN_NOTSUPPORT
    //-------------------------------------------------------------------------------------------------
    MSRV_SSSND_RET EnableAdvancedSoundEffect(ADVANCESND_TYPE AdvSndType, ADVSND_SUBPROC AdvSubProc);

    //-------------------------------------------------------------------------------------------------
    /// Set Advance Sound Effect Parameter.
    /// @code
    ///   ST_ADVSND_PARAMETER param;
    ///   param.PARAM_SRS_TSXT_SET_DC_GAIN = 5;
    ///   //set SRS tsxt DC gain to 5
    ///   SetBasicSoundEffect(ADVSND_SRS_THEATERSOUND_DC_CONTROL, &param);
    /// @endcode
    /// @param advsnd_param_type     \b IN: ADVSND_PARAM_TYPE
    /// @param *advsnd_param   \b IN: ST_ADVSND_PARAMETER Parameter Structure Pointer
    /// @return MSRV_SSSND_RET: RETURN_OK, RETURN_NOTOK, RETURN_NOTSUPPORT
    //-------------------------------------------------------------------------------------------------
    MSRV_SSSND_RET  SetAdvancedSoundEffect(ADVSND_PARAM_TYPE advsnd_param_type, ST_ADVSND_PARAMETER *advsnd_param);

    //-------------------------------------------------------------------------------------------------
    /// Get Advance Sound Effect Parameter that ever set.
    /// @param advsnd_param_type     \b IN: Advacne Sound Parameter Type
    /// @return ADVBSND_PARAMETER:
    //-------------------------------------------------------------------------------------------------
    MAPI_U16  GetAdvancedSoundEffect(ADVSND_PARAM_TYPE advsnd_param_type);

    //-------------------------------------------------------------------------------------------------
    /// Set Audio Output .
    /// @param type   \b IN: AUDIO_OUT_TYPE
    /// @param *audout_param    \b IN: AUDIO_OUT_PARAMETER Pointer
    /// @return MSRV_SSSND_RET: RETURN_OK, RETURN_NOTOK, RETURN_NOTSUPPORT
    //-------------------------------------------------------------------------------------------------
    MSRV_SSSND_RET  SetAudioOutput(AUDIO_OUT_TYPE type, AUDIO_OUT_PARAMETER *audout_param);

    //-------------------------------------------------------------------------------------------------
    /// Set Audio InputSource Switch.
    /// @param eInputSrc    \b IN: MAPI_INPUT_SOURCE_TYPE Pointer
    /// @return MSRV_SSSND_RET: RETURN_OK
    //-------------------------------------------------------------------------------------------------
    MSRV_SSSND_RET  SetInputSource(MAPI_INPUT_SOURCE_TYPE eInputSrc);

    //-------------------------------------------------------------------------------------------------
    /// Get Audio InputSource Switch that ever set.
    /// @return MSRV_SSSND_RET: MAPI_INPUT_SOURCE_TYPE
    //-------------------------------------------------------------------------------------------------
    MAPI_INPUT_SOURCE_TYPE  GetInputSource(void);

    //-------------------------------------------------------------------------------------------------
    /// Get the audio output port mute status(for Java use)    
    /// @param ePort \b IN: audio port type 
    /// @return MAPI_BOOL       \b IN: 1 is Mute , 0 is unmute
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL GetOutputMuteStatus(AUDIO_BASIC_PORT_TYPE ePort);

    //-------------------------------------------------------------------------------------------------
    /// Get the audio mute status by typing status type (for Java use)
    /// Audio Mute/unMute may control by different type of case. This rountine can get specific type of mute
    /// is assert or not.
    /// @param mute_status_type \b IN: mute status type
    /// @return MAPI_BOOL       \b IN: 1 is on, 0 is Off
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL GetMute(const MAPI_SOUND_MUTE_STATUS_TYPE mute_status_type);

    //-------------------------------------------------------------------------------------------------
    /// Set audio mute status (for Java use)
    /// Set mute ON/OFF in the following kind of type:
    /// Mute_Status_bIsAudioModeChanged,
    /// Mute_Status_bPermanentAudioMute,
    /// Mute_Status_bMomentAudioMute,
    /// Mute_Status_bByUserAudioMute,
    /// Mute_Status_bBySyncAudioMute,
    /// Mute_Status_bByVChipAudioMute,
    /// Mute_Status_bByBlockAudioMute,
    /// Mute_Status_bInternal1AudioMute,
    /// Mute_Status_bInternal2AudioMute,
    /// Mute_Status_bInternal3AudioMute,
    /// Mute_Status_bInternal4AudioMute,
    /// Mute_Status_bByDuringLimitedTimeAudioMute,
    /// Mute_Status_bByScanInOutchgCHchg,
    /// Mute_Status_bMHEGApMute,
    /// Mute_Status_bCIAudioMute,
    /// Mute_Status_SourceSwitchAudioMute,
    /// Mute_Status_bUsrSpkrAudioMute,
    /// Mute_Status_bUsrHpAudioMute,
    /// Mute_Status_bUsrSpdifAudioMute,
    /// Mute_Status_bUsrScart1AudioMute,
    /// Mute_Status_bUsrScart2AudioMute,
    /// Mute_Status_bUsrDataInAudioMute,
    /// Mute_Status_bPowerOnMute,
    /// @param muteType         \b IN: mute type
    /// @param onOff            \b IN: On: 1, Off: 0
    /// @return SSSOUND_OK: Success
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetMute(SSSOUND_MUTE_TYPE muteType, MAPI_BOOL onOff);


//-------------------------------------------------------------------------------------------------
/// ATV related interface function is used to Java kind of sound property.
//-------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------
    /// Set ATV Info .
    /// Set ATV Hideviation mode:
    /// ATV_HIDEV_OFF,
    /// ATV_HIDEV_BW_LV1,
    /// ATV_HIDEV_BW_LV2,
    /// ATV_HIDEV_BW_LV3,
    /// ATV_HIDEV_BW_MAX,
    /// @param infotype \b IN: ATV Info
    /// @param Info_mode \b IN: ATV Info mode
    /// @return MSRV_SSSND_RET: RETURN_OK
    //-------------------------------------------------------------------------------------------------
    MSRV_SSSND_RET SetATVInfo(ATV_INFO_TYPE infotype, ATV_INFO_MODE Info_mode);

    //-------------------------------------------------------------------------------------------------
    /// Get ATV Info that ever set.
    /// @param infotype \b IN: ATV Info
    /// @return MSRV_SSSND_RET: RETURN_OK
    //-------------------------------------------------------------------------------------------------
    ATV_INFO_MODE GetATVInfo(ATV_INFO_TYPE infotype);

    //-------------------------------------------------------------------------------------------------
    /// Get ATV sound system .
    /// The ATVinfo returned will the following type:
    /// ATV_SYSTEM_STANDARDS_BG
    /// ATV_SYSTEM_STANDARDS_DK
    /// ATV_SYSTEM_STANDARDS_I
    /// ATV_SYSTEM_STANDARDS_L
    /// ATV_SYSTEM_STANDARDS_M
    /// @return ATVInfo
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetATVSoundSystem(void);

    //-------------------------------------------------------------------------------------------------
    /// Set ATV sound system .
    /// @param standard     \b IN: ATV system standard info
    /// @return ATVInfo
    //-------------------------------------------------------------------------------------------------
    MSRV_SSSND_RET SetATVSoundSystem(ATV_SYSTEM_STANDARDS standard);

    //-------------------------------------------------------------------------------------------------
    /// The routine is something like monitor for ATV stanadard check and set to audio DSP.
    /// Check ATV sound system .
    /// @return ATVInfo
    //-------------------------------------------------------------------------------------------------
    MSRV_SSSND_RET CheckATVSoundSystem(void);

    //-------------------------------------------------------------------------------------------------
    /// Get the available ATV MTS mode .
    /// The mode can set as:
    /// E_AUDIOMODE_INVALID
    /// E_AUDIOMODE_MONO
    /// E_AUDIOMODE_FORCED_MONO
    /// E_AUDIOMODE_G_STEREO
    /// E_AUDIOMODE_K_STEREO
    /// E_AUDIOMODE_MONO_SAP
    /// E_AUDIOMODE_STEREO_SAP
    /// E_AUDIOMODE_DUAL_A
    /// E_AUDIOMODE_DUAL_B
    /// E_AUDIOMODE_DUAL_AB
    /// E_AUDIOMODE_NICAM_MONO
    /// E_AUDIOMODE_NICAM_STEREO
    /// E_AUDIOMODE_NICAM_DUAL_A
    /// E_AUDIOMODE_NICAM_DUAL_B
    /// E_AUDIOMODE_NICAM_DUAL_AB
    /// E_AUDIOMODE_HIDEV_MONO
    /// E_AUDIOMODE_LEFT_LEFT
    /// E_AUDIOMODE_RIGHT_RIGHT
    /// E_AUDIOMODE_LEFT_RIGHT
    /// @param mode     \b IN: ATV MTS Info
    /// @return <OUT>      \b MTS mode (Mono/Stereo/Dual)
    //-------------------------------------------------------------------------------------------------
    MSRV_SSSND_RET SetATVMtsMode(MAPI_U8 mode);

    //-------------------------------------------------------------------------------------------------
    /// Set to next ATV MTS Info .
    /// The rountine same with SetMtsMode
    /// According the return of GetMtsMode to change to the next MTS mode
    /// It depend on the GETMstMode value to decide which kind of part to use (Stereo,A2_Dual,NICAM_Stereo ...)
    /// The next mode will be choice in circularlly in that part.
    /// MAPI_U8 Stereo[] =
    /// {
    ///     E_AUDIOMODE_FORCED_MONO_,
    ///     E_AUDIOMODE_G_STEREO_,
    /// };
    ///
    /// MAPI_U8 A2_Dual[] =
    /// {
    ///     E_AUDIOMODE_DUAL_A_,
    ///     E_AUDIOMODE_DUAL_B_,
    ///     E_AUDIOMODE_DUAL_AB_,
    /// };
    ///
    /// MAPI_U8 NICAM_Stereo[] =
    /// {
    ///     E_AUDIOMODE_FORCED_MONO_,
    ///     E_AUDIOMODE_NICAM_STEREO_,
    /// };
    ///
    /// MAPI_U8 NICAM_Dual[] =
    /// {
    ///     E_AUDIOMODE_FORCED_MONO_,
    ///     E_AUDIOMODE_NICAM_DUAL_A_,
    ///     E_AUDIOMODE_NICAM_DUAL_B_,
    ///     E_AUDIOMODE_NICAM_DUAL_AB_,
    /// };
    ///
    /// MAPI_U8 NICAM_Mono[] =
    /// {
    ///     E_AUDIOMODE_FORCED_MONO_,
    ///     E_AUDIOMODE_NICAM_MONO_ ,
    /// };
    ///
    /// MAPI_U8 BTSC_Mono_Sap[] =
    /// {
    ///     E_AUDIOMODE_MONO_,
    ///     E_AUDIOMODE_MONO_SAP_ ,
    /// };
    ///
    /// MAPI_U8 BTSC_Stereo_Sap[] =
    /// {
    ///     E_AUDIOMODE_MONO_,
    ///     E_AUDIOMODE_G_STEREO_ ,
    ///     E_AUDIOMODE_STEREO_SAP_ ,
    /// };
    /// @return MSRV_SSSND_RET: RETURN_OK
    //-------------------------------------------------------------------------------------------------
    MSRV_SSSND_RET SetToNextATVMtsMode(void);

    //-------------------------------------------------------------------------------------------------
    /// Get the MTS mode
    /// The rountine same with GetMtsMode()
    /// Get Audio ATV MTS mode by the UI setting
    /// The return value has the following mode:
    /// E_AUDIOMODE_INVALID_
    /// E_AUDIOMODE_MONO_
    /// E_AUDIOMODE_FORCED_MONO_
    /// E_AUDIOMODE_G_STEREO_
    /// E_AUDIOMODE_K_STEREO_
    /// E_AUDIOMODE_MONO_SAP_
    /// E_AUDIOMODE_STEREO_SAP_
    /// E_AUDIOMODE_DUAL_A_
    /// E_AUDIOMODE_DUAL_B_
    /// E_AUDIOMODE_DUAL_AB_
    /// E_AUDIOMODE_NICAM_MONO_
    /// E_AUDIOMODE_NICAM_STEREO_
    /// E_AUDIOMODE_NICAM_DUAL_A_
    /// E_AUDIOMODE_NICAM_DUAL_B_
    /// E_AUDIOMODE_NICAM_DUAL_AB_
    /// E_AUDIOMODE_HIDEV_MONO_
    /// E_AUDIOMODE_LEFT_LEFT_
    /// E_AUDIOMODE_RIGHT_RIGHT_
    /// E_AUDIOMODE_LEFT_RIGHT_
    /// @return <OUT>      \b MTS mode (Mono/Stereo/Dual)
    //-------------------------------------------------------------------------------------------------
    ATV_AUDIOMODE_TYPE GetATVMtsMode(void);

    //-------------------------------------------------------------------------------------------------
    /// Get the available ATV Sound mode (Mono/Stereo/Dual) from the DSP report
    /// The rountine same with GetSoundMode()
    /// The return value has the following mode:
    /// E_AUDIOMODE_INVALID_
    /// E_AUDIOMODE_MONO_
    /// E_AUDIOMODE_FORCED_MONO_
    /// E_AUDIOMODE_G_STEREO_
    /// E_AUDIOMODE_K_STEREO_
    /// E_AUDIOMODE_MONO_SAP_
    /// E_AUDIOMODE_STEREO_SAP_
    /// E_AUDIOMODE_DUAL_A_
    /// E_AUDIOMODE_DUAL_B_
    /// E_AUDIOMODE_DUAL_AB_
    /// E_AUDIOMODE_NICAM_MONO_
    /// E_AUDIOMODE_NICAM_STEREO_
    /// E_AUDIOMODE_NICAM_DUAL_A_
    /// E_AUDIOMODE_NICAM_DUAL_B_
    /// E_AUDIOMODE_NICAM_DUAL_AB_
    /// E_AUDIOMODE_HIDEV_MONO_
    /// E_AUDIOMODE_LEFT_LEFT_
    /// E_AUDIOMODE_RIGHT_RIGHT_
    /// E_AUDIOMODE_LEFT_RIGHT_
    /// @return <OUT>      \b MTS mode (Mono/Stereo/Dual)
    //-------------------------------------------------------------------------------------------------
    ATV_AUDIOMODE_TYPE GetATVSoundMode(void);

//-------------------------------------------------------------------------------------------------
/// DTV related interface function is used to Java kind of sound property.
//-------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------
    /// Set DTV / DMP(Digital MultiMedia Player) Info .
    ///DTV and DMP Decoder control
    /// The control type are:
    /// DTV_DMP_DECODER_CTRL,
    /// DTV_DMP_DECODER_SOUNDMODE_CTRL,
    /// DTV_DMP_INFOTYPE_MPEG,
    /// For example, if user need set play command to DEC_ID 1
    /// @code
    ///  DTV_DMP_INFO_CFG1 info_config1;
    ///
    ///  DTV_DMP_INFO_CFG2 info_config2;   ///(Don't care now)
    ///
    ///  info_config1.DTV_DMP_DECODER_COMMAND = DTV_DMP_DEC_PLAY;
    ///
    ///  SetDTVInfoDMPInfo(DTV_DMP_DECODER_CTRL, DTVDMP_DEC_ID1, &info_config1, &info_config2)
    /// @endcode
    /// @param info_type        \b IN: DTV_DMP Info
    /// @param dec_id           \b IN: DTV_DMP Decoder ID
    /// @param *Info_config1    \b IN: DTV_DMP Info config-1 structure
    /// @param *Info_config2    \b IN: DTV_DMP Info config-2 structure
    /// @return MSRV_SSSND_RET: RETURN_OK
    //-------------------------------------------------------------------------------------------------
    MSRV_SSSND_RET  SetDTVInfoDMPInfo(DTV_DMP_INFO_TYPE info_type, DTV_DMP_AUDIO_DEC_ID dec_id, DTV_DMP_INFO_CFG1 *Info_config1, DTV_DMP_INFO_CFG2 *Info_config2);

    //-------------------------------------------------------------------------------------------------
    /// Set sound track(Digital MultiMedia Player) Info .
    /// @param enSoundMode        \b IN: DTV Audio Sound Mode (LL, RR, LR...)
    /// @return MSRV_SSSND_RET: RETURN_OK
    //-------------------------------------------------------------------------------------------------
    MSRV_SSSND_RET  SetKTVSoundTrack(KTV_AUDIO_MPEG_SOUNDMODE enSoundMode);

    //-------------------------------------------------------------------------------------------------
    /// Get main speaker output to Stereo/LL/RR/Mixed that ever set by DECODER_SetOutputMode(mode);
    /// @return DTV_DMP_AUDIO_DEC_SOUNDMOD_TYPE: output mode Stereo: 0, LL: 1, RR: 2, Mixed: 3
    //-------------------------------------------------------------------------------------------------
    DTV_DMP_AUDIO_DEC_SOUNDMOD_TYPE GetDTVInfoDMPInfo_DECODER_SoundMode();

    //-------------------------------------------------------------------------------------------------
    /// Get DTV or DMP Decoder Control Command that ever set by SetDTVInfoDMPInfo(DTV_DMP_DECODER_CTRL, xx, xx, xx);
    /// @return DTV_DMP_AUDIO_DEC_CTRLCMD_TYPE: MMA_STOP_, MMA_PLAY_,MMA_PLAY_FILETSP_, MMA_RESYNC_, MMA_PLAY_FILE_,
    ///                             MMA_BROWSE_, MMA_PAUSE_
    //-------------------------------------------------------------------------------------------------
    DTV_DMP_AUDIO_DEC_CTRLCMD_TYPE GetDTVInfoDMPInfo_DECODER_CtrlCmd();

    //-------------------------------------------------------------------------------------------------
    /// Get DTV or DMP Decoder Channel Mode
    /// @return DTV_AUDIO_DEC_CHMOD_Type: AUDIO_DEC_ACMODE_NOTREADY, AUDIO_DEC_ACMODE_DUALMONO1, AUDIO_DEC_ACMODE_STEREO ...etc
    //-------------------------------------------------------------------------------------------------
    DTV_AUDIO_DEC_CHMOD_Type GetDTVInfoDMPInfo_DECODER_ChannelMode();

    //-------------------------------------------------------------------------------------------------
    /// SetAmplifierMute: To Set Amplifier Mute
    /// @param bMute        \b IN: TRUE for Mute; FALSE for unMute
    /// @return RESULT
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetAmplifierMute(MAPI_BOOL bMute);

    //-------------------------------------------------------------------------------------------------
    /// SetAmplifierEQByMode: To Set EQ By Mode
    /// @param Mode      \b IN: desktop mode or hungup mode.
    /// @return NONE
    //-------------------------------------------------------------------------------------------------
    void SetAmplifierEQByMode(ENUM_SET_EQ_BY_MODE_TYPE Mode);

    //-------------------------------------------------------------------------------------------------
    /// Set volume fro external system
    /// Set volume to value that normalized from (VOLUME_SCALE_MIN = 0, VOLUME_SCALE_MAX = 100) to (min, max)
    /// @param value   \b IN: the value needs to transform
    /// @param min     \n IN: minimal value in the source domain
    /// @param max     \n IN: maximal value in the source domain
    /// @return none
    //-------------------------------------------------------------------------------------------------
    void SetVolumeWithExternalScale(MAPI_S32 value, MAPI_S32 min, MAPI_S32 max);

    //-------------------------------------------------------------------------------------------------
    /// @brief  \b Function  \b Name: SetDebugMsgLevel()
    /// @brief  \b Function  \b Description: Set msrv sssound debug message level
    /// @param  eDebugLevel    \b DebugLevel , total 8 level
    /// @return MSRV_SSSND_RET: RETURN_OK
    //-------------------------------------------------------------------------------------------------
    MSRV_SSSND_RET SetDebugMsgLevel(MSRV_SSSOUND_DEBUG_LEVEL eDebugLevel);

    //-------------------------------------------------------------------------------------------------
    /// Init Sound Effect setting wile entering skype mode
    /// @return MSRV_SSSND_RET: RETURN_OK
    //-------------------------------------------------------------------------------------------------
    MSRV_SSSND_RET  EnterSkypeSoundSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// Restore Sound Effect setting wile exiting skype mode
    /// @return MSRV_SSSND_RET: RETURN_OK
    //-------------------------------------------------------------------------------------------------
    MSRV_SSSND_RET  ExitSkypeSoundSetting(void);

    //-------------------------------------------------------------------------------------------------
    /// Get Skype Sound Setting
    /// @param u8Option   \b IN: sound option
    /// @return MAPI_U32: The value of Skype sound seeting
    //-------------------------------------------------------------------------------------------------
    MAPI_U32  GetSkypeSoundSetting(MAPI_U8 u8Option);

    //-------------------------------------------------------------------------------------------------
    /// The routine include many kinds of audio command/parameters setting.
    /// pseudo code
    /// @code
    ///  //use SetCommAudioInfo to set audio noise reduction threshold to 0xAA
    ///   if(SetCommAudioInfo(Audio_Comm_infoType_setNR_Threshold_, 0xAA, 0))
    ///   {
    ///   else
    ///   {
    ///    //there is problem set audio noise reduction threshold, put your error handling codes here or ASSERT it.
    ///      ASSERT(0);
    ///   }
    /// @endcode
    /// @brief  \b Function  \b Name: SetCommAudioInfo()
    /// @brief  \b Function  \b Description: Set audio info
    /// @param  eInfoType    \b : audio type
    /// @param  param1    \b :  MMA audio type
    /// @param  param2    \b : write mask byte condition
    /// @return \b AUDIOMUTESOURCE_TYPE_    : True(success)/False(fail)
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetCommAudioInfo(Audio_COMM_infoType_ eInfoType, MAPI_S32 param1, MAPI_S32 param2 );

    //-------------------------------------------------------------------------------------------------
    /// Set audio debug level
    /// @param eDbgLvl   \b IN: audio system debug level
    /// @return MAPI_U8: SSSOUND_OK
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL SetAudioDebugLevel(E_AUDIO_DBGLVL eDbgLvl);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: SetDolbyBulletin11()
    /// @brief \b Function \b Description: Enable/disable Dolby bulletin-11.
    /// @param   bEnable        \b IN param : TRUE or FALSE
    /// @return  SSSOUND_OK: Success.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetDolbyBulletin11(MAPI_BOOL bEnable);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: SetDDPlusHDMITxByPass()
    /// @brief \b Function \b Description: Enable/disable Dolby HDMI Tx bypass. If disable, AC3P will be transferred to AC3, and the pass to HDMI Tx.
    /// @param   bEnable        \b IN param : TRUE or FALSE
    /// @return  SSSOUND_OK: Success.
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetDDPlusHDMITxByPass(MAPI_BOOL bEnable);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: SetSNDDTSInfo()
    /// @brief \b Function \b Description: This routine set configuration to DTS decoder
    /// @param infoType    \b Audio_DTS_infoType_	 : DTS control type
    /// @param param1   \b MAPI_U32	 : param1:
    /// @param param2   \b MAPI_U32	 : param2:
    /// @return 	\b MAPI_U8 :	0 ( SUCCESS ) / OTHER (FAIL)
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetSNDDTSInfo(const Audio_DTS_infoType_ infoType,MAPI_U32 param1,MAPI_U32 param2);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: GetSNDAACInfo()
    /// @brief  \b Function \b Description: Get AAC Plus Decoder information
    /// @param <infoType>        \b Audio_AAC_infoType_:  Please see enum "Audio_AAC_infoType_"
    /// @return MAPI_U32     \b Audio_AAC_infoType_ info values
    //-------------------------------------------------------------------------------------------------
    MAPI_U32 GetSNDAACInfo(Audio_AAC_infoType_ infoType);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: GetSNDAACInfo()
    /// @brief \b Function \b Description: Set AAC Decoder information
    /// @param  <infoType>        \b infoType: AAC Decoder information
    /// @param param1   \b MAPI_U32	 : param1:
    /// @param param2   \b MAPI_U32	 : param2:
    /// @return   MAPI_U8      \b ZERO(OK) / OTHER(Fail)
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetSNDAACInfo(const Audio_AAC_infoType_ infoType,MAPI_U32 param1,MAPI_U32 param2);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: GetSNDAC3Info()
    /// @brief \b Function \b Description: Get AC3 Plus Decoder information
    /// @param <infoType>        \b Audio_AC3_infoType_:  Please see enum "Audio_AC3P_infoType_"
    /// @return MAPI_U32       \b Audio_AC3P_infoType_ info values
    //-------------------------------------------------------------------------------------------------
    MAPI_U32 GetSNDAC3Info(Audio_AC3_infoType_ infoType);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: SetSNDAC3Info()
    /// @brief \b Function \b Description: Set AC3 Plus Decoder information
    /// @param  <infoType>        \b Audio_AC3_infoType_: AC3P Decoder information
    /// @param param1   \b MAPI_U32	 : param1:
    /// @param param2   \b MAPI_U32	 : param2:
    /// @return MAPI_U8     \b ZERO(OK) / OTHER(Fail)
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetSNDAC3Info(const Audio_AC3_infoType_ infoType,MAPI_U32 param1,MAPI_U32 param2);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: GetSNDAC3PInfo()
    /// @brief \b Function \b Description: Get AC3 Plus Decoder information
    /// @param <infoType>        \b Audio_AC3P_infoType_:  Please see enum "Audio_AC3P_infoType_"
    /// @return MAPI_U32       \b Audio_AC3P_infoType_ info values
    //-------------------------------------------------------------------------------------------------
    MAPI_U32 GetSNDAC3PInfo(Audio_AC3P_infoType_ infoType);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: SetSNDAC3PInfo()
    /// @brief \b Function \b Description: Set AC3 Plus Decoder information
    /// @param  <infoType>        \b Audio_AC3P_infoType_: AC3P Decoder information
    /// @param param1   \b MAPI_U32	 : param1:
    /// @param param2   \b MAPI_U32	 : param2:
    /// @return MAPI_U8     \b ZERO(OK) / OTHER(Fail)
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetSNDAC3PInfo(const Audio_AC3P_infoType_ infoType,MAPI_U32 param1,MAPI_U32 param2);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: GetSNDMpegInfo()
    /// @brief \b Function \b Description: Get MPEG Decoder information
    /// @param <infoType>        \b infoType:  Please see enum "Audio_MPEG_infoType_"
    /// @return MAPI_U32      \b Audio_MPEG_infoType_ info values
    //-------------------------------------------------------------------------------------------------
    MAPI_U32 GetSNDMpegInfo(Audio_MPEG_infoType_ infoType);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: SetSNDWMAInfo()
    /// @brief \b Function \b Description: Set WMA Decoder information
    /// @param  <infoType>        \b Audio_WMA_infoType_: WMA Decoder information
    /// @param param1   \b MAPI_U32	 :WMA Decoder information param1
    /// @param param2   \b MAPI_U32	 : WMA Decoder information param2
    /// @return MAPI_U8       \b ZERO(OK) / OTHER(Fail)
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 SetSNDWMAInfo( const Audio_WMA_infoType_ infoType,MAPI_U32 param1,MAPI_U32 param2);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: GetMpegFrameCnt()
    /// @brief \b Function \b Description: Get current mepg frame count
    /// @return MAPI_U32       \b Total number of current mepg frame count
    //-------------------------------------------------------------------------------------------------
    MAPI_U32 GetMpegFrameCnt(void);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: GetAudioCommInfo()
    /// @brief \b Function \b Description: This routine will return the 64bit Common info according to info type
    /// @param  <infoType>        \b MAPI_U32: Auido command value
    /// @return  MAPI_U64    \b  MAPI_U64: 64bit info
    //-------------------------------------------------------------------------------------------------
    MAPI_U64 GetAudioCommInfo(MAPI_U32 infoType);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: CheckInputRequest()
    /// @brief \b Function \b Description: If MCU receive DSP interrupt, this function will return MAPI_TRUE
    /// @param pU32WrtAddr \b MAPI_U32: pU32WrtAddr
    /// @param pU32WrtBytes \b MAPI_U32: pU32WrtBytes
    /// @return  MAPI_U8    \b MAPI_U8    : request result
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 CheckInputRequest(MAPI_U32 *pU32WrtAddr, MAPI_U32 *pU32WrtBytes) ;

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: SetInput()
    /// @brief \b Function \b Description: This function will inform DSP that MCU already write data to ES buffer.
    /// @return none
    //-------------------------------------------------------------------------------------------------
    void SetInput(void);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: StartDecode()
    /// @brief \b Function \b Description:
    /// @return none
    //-------------------------------------------------------------------------------------------------
    void StartDecode(void);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: StopDecode()
    /// @brief \b Function \b Description:
    /// @return none
    //-------------------------------------------------------------------------------------------------
    void StopDecode(void);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: SwitchAudioDSPSystem()
    /// @brief \b Function \b Description: Audio Switch Decoder System Function,
    /// @param <eAudioDSPSystem> 	   \b eAudioDSPSystem : Audio DSP System Type
    /// 									  : E_AUDIO_DSP_SIF_,
    /// 									  : E_AUDIO_DSP_MPEG_,
    /// 									  : E_AUDIO_DSP_AC3_,
    /// 									  : E_AUDIO_DSP_AC3P_,
    /// 									  : E_AUDIO_DSP_AACP_,
    /// 									  : E_AUDIO_DSP_MPEG_AD_, ...etc
    /// 									  : see "enum AUDIO_DSP_SYSTEM_"
    /// @return none
    //-------------------------------------------------------------------------------------------------
    void SwitchAudioDSPSystem(AUDIO_DSP_SYSTEM_ eAudioDSPSystem);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: SetAudioSpidifOutPut()
    /// @brief \b Function \b Description : Audio SPDIF Set Mode
    /// @param eSpidif_output       \b SPDIF_TYPE_ : MSAPI_AUD_SPDIF_PCM_,
    ///                                   : MSAPI_AUD_SPDIF_NONPCM_,
    /// @return  none
    //-------------------------------------------------------------------------------------------------
    void SetAudioSpidifOutPut(SPDIF_TYPE_ eSpidif_output);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: SetAudioHDMIOutPut()
    /// @brief \b Function \b Description : Audio HDMI Set Mode
    /// @param eHdmi_putput \b IN: HDMI_TYPE_
    /// @return none
    //-------------------------------------------------------------------------------------------------
    void SetAudioHDMIOutPut(HDMI_TYPE_ eHdmi_putput);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: SetAQParamFromUSB()
    /// @brief \b Function \b Description: Read advance sound effect table from USB and set it to DSP
    /// @param  sFilePath  \b : USB file path
    /// @return : none
    //-------------------------------------------------------------------------------------------------
    void SetAQParamFromUSB(string sFilePath);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: SetAudioHDMItx_HDBypass()
    /// @param   bEnable        \b IN param : TRUE or FALSE
    /// @return none
    //-------------------------------------------------------------------------------------------------
    void SetAudioHDMItx_HDBypass(MAPI_BOOL bEnable);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: GetAudioHDMItx_HDBypass()
    /// @brief \b Function \b Description:
    /// @return RESULT
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL GetAudioHDMItx_HDBypass(void);

    //-------------------------------------------------------------------------------------------------
    /// @brief \b Function \b Name: GetAudioHDMItx_HDBypass_Capability()
    /// @brief \b Function \b Description:
    /// @return RESULT
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL GetAudioHDMItx_HDBypass_Capability(void);
 #if (KARAOKE_ENABLE == 1)
    void  SetMicVol(MAPI_U8 Value);
    void  SetMicEcho(MAPI_U8 Value);
    MAPI_U8 GetMicVol(void);
    MAPI_U8 GetMicEcho(void);
#endif
private:
    //MAPI_U8     m_obj_name;
    //MAPI_U8     m_obj_index;
    //MAPI_U8     m_source;
    //MAPI_U8     m_window;
    ST_SIF_Prescale_Offset st_SIF_Prescale_Offset ;
    SOUND_SURROUND_MODE m_surrMode;
    MAPI_BOOL m_bAudyessySRSMode;
    MAPI_BOOL m_bNRenable;
    //MAPI_INPUT_SOURCE_TYPE m_enCurrInputSrc;

    MAPI_BOOL IsAVCFlag;

    MAPI_U8 m_SpeakerVolumeValue;
    MAPI_U8 m_HPVolumeValue;
    MAPI_U8 m_LINE_OUT_VolumeValue;
    MAPI_U8 m_SCART1_OUT_VolumeValue;
    MAPI_U8 m_SCART2_OUT_VolumeValue;
    MAPI_U8 m_SPDIF_OUT_VolumeValue;
    MAPI_U8 m_AD_VolumeValue;
    MAPI_U8 m_Premixer_KTV_MP3_VolumeValue;
    MAPI_U8 m_Premixer_KTV_MIC_VolumeValue;
    MAPI_U8 m_Premixer_GAME1_VolumeValue;
    MAPI_U8 m_Premixer_GAME2_VolumeValue;
    MAPI_U8 m_Premixer_ECHO1_VolumeValue;
    MAPI_U8 m_Premixer_ECHO2_VolumeValue;
    MAPI_U8 m_Premixer_ALSA_VolumeValue;
    MAPI_U8 m_PcmCapture1_VolumeValue;
    MAPI_U8 m_PcmCapture2_VolumeValue;
#if (MSTAR_TVOS == 1)
    ///Must Get m_VolumeCompensation from ATV database,before Calling SetVolume()
    MAPI_U8 m_VolumeCompensation;
#endif

    Sound_PRESCALE_TBL_ PrescaleTable;

    MAPI_S8 spkr_prescale;
    MAPI_S8 hp_prescale;
    MAPI_S8 sc1_prescale;
    MAPI_S8 sc2_prescale;
    MAPI_S8 spdif_prescale;

    /*******************************************/
    // For Get Basic Sound Effect Parameter
    /*******************************************/
    MAPI_U16    m_BSND_PARAM_PRESCALE;
    ///Treble Vaule
    MAPI_U16    m_BSND_PARAM_TREBLE;
    ///Bass Value
    MAPI_U16    m_BSND_PARAM_BASS;
    ///Balance Value
    MAPI_U16    m_BSND_PARAM_TYPE_BALANCE;
    ///EQ Band Number
    MAPI_U8     m_BSND_PARAM_EQ_BAND_NUM;
    ///PEQ Band Number
    MAPI_U8     m_BSND_PARAM_PEQ_BAND_NUM;
    ///EQ Level
    MAPI_U16    m_BSND_PARAM_EQ_LEVEL[MAX_EQ_BAND_NUM];
    ///PEQ GAIN
    MAPI_U16    m_BSND_PARAM_PEQ_GAIN[MAX_PEQ_BAND_NUM];
    ///PEQ Center Freq for Each Band
    MAPI_U16    m_BSND_PARAM_PEQ_FC[MAX_PEQ_BAND_NUM];
    ///PEQ Q Value for Each Band
    MAPI_U16    m_BSND_PARAM_PEQ_QVALUE[MAX_PEQ_BAND_NUM];
    ///AVC(AVL) Threshold
    MAPI_U16    m_BSND_PARAM_AVC_THRESHOLD;
    ///AVC(AVL) Attach Time
    MAPI_U16    m_BSND_PARAM_AVC_AT;
    ///AVC(AVL) Release Time
    MAPI_U16    m_BSND_PARAM_AVC_RT;
    ///MStar Surround XA Value
    MAPI_U16    m_BSND_PARAM_MSURR_XA;
    ///MStar Surround XB Value
    MAPI_U16    m_BSND_PARAM_MSURR_XB;
    ///MStar Surround XK Value
    MAPI_U16    m_BSND_PARAM_MSURR_XK;
    ///MStar Surround LPFGAIN Value
    MAPI_U16    m_BSND_PARAM_MSURR_LPFGAIN;
    ///Sound DRC Threshold
    MAPI_U16    m_BSND_PARAM_DRC_THRESHOLD;
    ///Noise Reduction Threshold
    MAPI_U16    m_BSND_PARAM_NR_THRESHOLD;
    /// ECHO Time(ms)
    MAPI_U16    m_BSND_PARAM_ECHO_TIME;

    /*******************************************/
    // For Get Advance Sound Effect Parameter
    /*******************************************/
    ///DOLBY PL2 + VSPK SMOD Param Value
    MAPI_U16    m_ADVSND_DOLBY_PL2VDPK_SMOD;
    ///DOLBY PL2 + VSPK WMOD Param Value
    MAPI_U16    m_ADVSND_DOLBY_PL2VDPK_WMOD;
    ///SRS TSXT PARAM:INPUT_GAIN Param Value
    MAPI_U16    m_ADVSND_SRS_TSXT_SET_INPUT_GAIN;
    ///SRS TSXT PARAM:DC_GAIN Param Value
    MAPI_U16    m_ADVSND_SRS_TSXT_SET_DC_GAIN;
    ///SRS TSXT PARAM:TRUBASS_GAIN Param Value
    MAPI_U16    m_ADVSND_SRS_TSXT_SET_TRUBASS_GAIN;
    ///SRS TSXT PARAM:SPEAKERSIZE Param Value
    MAPI_U16    m_ADVSND_SRS_TSXT_SET_SPEAKERSIZE;
    ///SRS TSXT PARAM:INPUT_MODE Param Value
    MAPI_U16    m_ADVSND_SRS_TSXT_SET_INPUT_MODE;
    ///SRS TSXT PARAM:OUTPUT_GAIN Param Value
    MAPI_U16    m_ADVSND_SRS_TSXT_SET_OUTPUT_GAIN;
    ///SRS TSHD PARAM:INPUT_MODE Param Value
    MAPI_U16    m_ADVSND_SRS_TSHD_SET_INPUT_MODE;
    ///SRS TSHD PARAM:OUTPUT_MODE Param Value
    MAPI_U16    m_ADVSND_SRS_TSHD_SET_OUTPUT_MODE;
    ///SRS TSHD PARAM:SPEAKERSIZE Param Value
    MAPI_U16    m_ADVSND_SRS_TSHD_SET_SPEAKERSIZE;
    ///SRS TSHD PARAM:TRUBASS_CONTROL Param Value
    MAPI_U16    m_ADVSND_SRS_TSHD_SET_TRUBASS_CONTROL;
    ///SRS TSHD PARAM:DEFINITION_CONTROL Param Value
    MAPI_U16    m_ADVSND_SRS_TSHD_SET_DEFINITION_CONTROL;
    ///SRS TSHD PARAM:DC_CONTROL Param Value
    MAPI_U16    m_ADVSND_SRS_TSHD_SET_DC_CONTROL;
    ///SRS TSHD PARAM:SURROUND_LEVEL Param Value
    MAPI_U16    m_ADVSND_SRS_TSHD_SET_SURROUND_LEVEL;
    ///SRS TSHD PARAM:INPUT_GAIN Param Value
    MAPI_U16    m_ADVSND_SRS_TSHD_SET_INPUT_GAIN;
    ///SRS TSHD PARAM:WOWSPACE_CONTROL Param Value
    MAPI_U16    m_ADVSND_SRS_TSHD_SET_WOWSPACE_CONTROL;
    ///SRS TSHD PARAM:WOWCENTER_CONTROL Param Value
    MAPI_U16    m_ADVSND_SRS_TSHD_SET_WOWCENTER_CONTROL;
    ///SRS TSHD PARAM:WOWHDSRS3DMODE Param Value
    MAPI_U16    m_ADVSND_SRS_TSHD_SET_WOWHDSRS3DMODE;
    ///SRS TSHD PARAM:LIMITERCONTROL Param Value
    MAPI_U16    m_ADVSND_SRS_TSHD_SET_LIMITERCONTROL;
    ///SRS TSHD PARAM:OUTPUT_GAIN Param Value
    MAPI_U16    m_ADVSND_SRS_TSHD_SET_OUTPUT_GAIN;
    ///SRS THEATERSOUND PARAM:INPUT_GAIN Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_INPUT_GAIN;
    ///SRS THEATERSOUND PARAM:OUTPUT_GAIN Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_OUTPUT_GAIN;
    ///SRS THEATERSOUND PARAM:BYPASS_GAIN Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_BYPASS_GAIN;
    ///SRS THEATERSOUND PARAM:HEADROOM_GAIN Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_HEADROOM_GAIN;
    ///SRS THEATERSOUND PARAM:INPUT_MODE Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_INPUT_MODE;

    ///SRS THEATERSOUND PARAM:DEFINITION_CONTROL Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_DEFINITION_CONTROL;
    ///SRS THEATERSOUND PARAM:DC_CONTROL Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_DC_CONTROL;
    ///SRS THEATERSOUND PARAM:TRUBASS_CONTROL Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_TRUBASS_CONTROL;
    ///SRS THEATERSOUND PARAM:SPEAKERSIZE Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_SPEAKERSIZE;
    ///SRS THEATERSOUND PARAM:HARDLIMITER_LEVEL Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_HARDLIMITER_LEVEL;
    ///SRS THEATERSOUND PARAM:HARDLIMITER_BOOST_GAIN Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_HARDLIMITER_BOOST_GAIN;
    ///SRS THEATERSOUND PARAM:TRUVOLUME_MODE Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_MODE;
    ///SRS THEATERSOUND PARAM:TRUVOLUME_REF_LEVEL Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_REF_LEVEL;
    ///SRS THEATERSOUND PARAM:TRUVOLUME_MAX_GAIN Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_MAX_GAIN;
    ///SRS THEATERSOUND PARAM:TRUVOLUME_NOISE_MNGR_THLD Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_NOISE_MNGR_THLD;
    ///SRS THEATERSOUND PARAM:TRUVOLUME_CALIBRATE Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_CALIBRATE;
    ///SRS THEATERSOUND PARAM:TRUVOLUME_INPUT_GAIN Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_INPUT_GAIN;
    ///SRS THEATERSOUND PARAM:TRUVOLUME_OUTPUT_GAIN Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_OUTPUT_GAIN;
    ///SRS THEATERSOUND PARAM:TRUVOLUME_BYPASS_GAIN Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_TRUVOLUME_BYPASS_GAIN;
    ///SRS THEATERSOUND PARAM:HPF_FC Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_HPF_FC;
    ///SRS THEATERSOUND PARAM:TSHD_INPUT_GAIN Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_TSHD_INPUT_GAIN;
    ///SRS THEATERSOUND PARAM:TSHD_OUTPUT_GAIN Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_TSHD_OUTPUT_GAIN;
    ///SRS THEATERSOUND PARAM:SURR_LEVEL_CONTROL Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_SURR_LEVEL_CONTROL;
    ///SRS THEATERSOUND PARAM:TRUBASS_COMPRESSOR_CONTROL Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_TRUBASS_COMPRESSOR_CONTROL;
    ///SRS THEATERSOUND PARAM:TRUBASS_PROCESS_MODE Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_TRUBASS_PROCESS_MODE;
    ///SRS THEATERSOUND PARAM:TRUBASS_SPEAKER_AUDIO Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_TRUBASS_SPEAKER_AUDIO;
    ///SRS THEATERSOUND PARAM:TRUBASS_SPEAKER_ANALYSIS Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_TRUBASS_SPEAKER_ANALYSIS;
    ///SRS THEATERSOUND PARAM:CS_INPUT_GAIN Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_CS_INPUT_GAIN;
    ///SRS THEATERSOUND PARAM:CS_PROCESS_MODE Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_CS_PROCESS_MODE;
    ///SRS THEATERSOUND PARAM:CS_LR_OUTPUT_GAIN Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_CS_LR_OUTPUT_GAIN;
    ///SRS THEATERSOUND PARAM:CS_LSRS_OUTPUT_GAIN Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_CS_LSRS_OUTPUT_GAIN;
    ///SRS THEATERSOUND PARAM:CS_CENTER_OUTPUT_GAIN Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_CS_CENTER_OUTPUT_GAIN;
    ///SRS THEATERSOUND PARAM:TRUDIALOG_INPUT_GAIN Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_TRUDIALOG_INPUT_GAIN;
    ///SRS THEATERSOUND PARAM:TRUDIALOG_OUTPUT_GAIN Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_TRUDIALOG_OUTPUT_GAIN;
    ///SRS THEATERSOUND PARAM:TRUDIALOG_BYPASS_GAIN Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_TRUDIALOG_BYPASS_GAIN;
    ///SRS THEATERSOUND PARAM:TRUDIALOG_PROCESS_GAIN Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_TRUDIALOG_PROCESS_GAIN;
    ///SRS THEATERSOUND PARAM:TRUDIALOG_CLARITY_GAIN Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_TRUDIALOG_CLARITY_GAIN;
    ///SRS THEATERSOUND PARAM:NORMALIZER_THRESH Param Value
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND_NORMALIZER_THRESH;
    ///SRS THEATERSOUND3D PARAM:CC3D_INPUT_GAIN Setting
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND3D_CC3D_INPUT_GAIN;
    ///SRS THEATERSOUND3D PARAM:CC3D_OUTPUT_GAIN Setting
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND3D_CC3D_OUTPUT_GAIN;
    ///SRS THEATERSOUND3D PARAM:CC3D_BYPASS_GAIN Setting
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND3D_CC3D_BYPASS_GAIN;
    ///SRS THEATERSOUND3D PARAM:CC3D_APERTURE Setting
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND3D_CC3D_APERTURE;
    ///SRS THEATERSOUND3D PARAM:CC3D_GAINLIMIT Setting
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND3D_CC3D_GAINLIMIT;
    ///SRS THEATERSOUND3D PARAM:CC3D_FF_DEPTH Setting
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND3D_CC3D_FF_DEPTH;
    ///SRS THEATERSOUND3D PARAM:CC3D_NF_DEPTH Setting
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND3D_CC3D_NF_DEPTH;
    ///SRS THEATERSOUND3D PARAM:TSHD_MIX_FADE_CTRL Setting
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND3D_CC3D_TSHD_MIX_FADE_CTRL;
    ///SRS THEATERSOUND3D PARAM:CC3D_TBHDX_INPUT_GAIN Setting
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND3D_CC3D_TBHDX_INPUT_GAIN;
    ///SRS THEATERSOUND3D PARAM:CC3D_TBHDX_BASSLEVEL Setting
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND3D_CC3D_TBHDX_BASSLEVEL;
    ///SRS THEATERSOUND3D PARAM:CC3D_TBHDX_SPEAKERSIZE Setting
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND3D_CC3D_TBHDX_SPEAKERSIZE;
    ///SRS THEATERSOUND3D PARAM:CC3D_TBHDX_MODE Setting
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND3D_CC3D_TBHDX_MODE;
    ///SRS THEATERSOUND3D PARAM:CC3D_TBHDX_DYNAMICS Setting
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND3D_CC3D_TBHDX_DYNAMICS;
    ///SRS THEATERSOUND3D PARAM:CC3D_TBHDX_HP_ORDER Setting
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND3D_CC3D_TBHDX_HP_ORDER;
    ///SRS THEATERSOUND3D PARAM:CC3D_TBHDX_CUSTOM_FILTER Setting
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND3D_CC3D_TBHDX_CUSTOM_FILTER;
    ///SRS THEATERSOUND3D PARAM:GEQ_INPUT_GAIN Setting
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND3D_GEQ_INPUT_GAIN;
    ///SRS THEATERSOUND3D PARAM:GEQ_BAND0_GAIN Setting
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND3D_GEQ_BAND0_GAIN;
    ///SRS THEATERSOUND3D PARAM:GEQ_BAND1_GAIN Setting
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND3D_GEQ_BAND1_GAIN;
    ///SRS THEATERSOUND3D PARAM:GEQ_BAND2_GAIN Setting
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND3D_GEQ_BAND2_GAIN;
    ///SRS THEATERSOUND3D PARAM:GEQ_BAND3_GAIN Setting
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND3D_GEQ_BAND3_GAIN;
    ///SRS THEATERSOUND3D PARAM:GEQ_BAND4_GAIN Setting
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND3D_GEQ_BAND4_GAIN;
    ///SRS THEATERSOUND3D PARAM:CC3D_PROCESS_PATH Setting
    MAPI_U16    m_ADVSND_SRS_THEATERSOUND3D_CC3D_PROCESS_PATH;
    ///DTS_ULTRATV PARAM:EVO_MONOINPUT Param Value
    MAPI_U16    m_ADVSND_DTS_ULTRATV_EVO_MONOINPUT;
    ///DTS_ULTRATV PARAM:EVO_WIDENINGON Param Value
    MAPI_U16    m_ADVSND_DTS_ULTRATV_EVO_WIDENINGON;
    ///DTS_ULTRATV PARAM:EVO_ADD3DBON Param Value
    MAPI_U16    m_ADVSND_DTS_ULTRATV_EVO_ADD3DBON;
    ///DTS_ULTRATV PARAM:EVO_PCELEVEL Param Value
    MAPI_U16    m_ADVSND_DTS_ULTRATV_EVO_PCELEVEL;
    ///DTS_ULTRATV PARAM:EVO_VLFELEVEL Param Value
    MAPI_U16    m_ADVSND_DTS_ULTRATV_EVO_VLFELEVEL;
    ///DTS_ULTRATV PARAM:SYM_DEFAULT Param Value
    MAPI_U16    m_ADVSND_DTS_ULTRATV_SYM_DEFAULT;
    ///DTS_ULTRATV PARAM:SYM_MODE Param Value
    MAPI_U16    m_ADVSND_DTS_ULTRATV_SYM_MODE;
    ///DTS_ULTRATV PARAM:SYM_LEVEL Param Value
    MAPI_U16    m_ADVSND_DTS_ULTRATV_SYM_LEVEL;
    ///DTS_ULTRATV PARAM:SYM_RESET Param Value
    MAPI_U16    m_ADVSND_DTS_ULTRATV_SYM_RESET;
    ///AUDYSSEY PARAM:DYNAMICVOL_COMPRESS_MODE Param Value
    MAPI_U16    m_ADVSND_AUDYSSEY_DYNAMICVOL_COMPRESS_MODE;
    ///AUDYSSEY PARAM:DYNAMICVOL_GC Param Value
    MAPI_U16    m_ADVSND_AUDYSSEY_DYNAMICVOL_GC;
    ///AUDYSSEY PARAM:DYNAMICVOL_VOLParam Value Param Value
    MAPI_U16    m_ADVSND_AUDYSSEY_DYNAMICVOL_VOLSETTING;
    ///AUDYSSEY PARAM:DYNAMICEQ_EQOFFSET Param Value
    MAPI_U16    m_ADVSND_AUDYSSEY_DYNAMICEQ_EQOFFSET;
    ///AUDYSSEY PARAM:ABX_GWET Param Value
    MAPI_U16    m_ADVSND_AUDYSSEY_ABX_GWET;
    ///AUDYSSEY PARAM:ABX_GDRY Param Value
    MAPI_U16    m_ADVSND_AUDYSSEY_ABX_GDRY;
    ///AUDYSSEY PARAM:ABX_FILSET Param Value
    MAPI_U16    m_ADVSND_AUDYSSEY_ABX_FILSET;

    ///SRS PURESOUND PARAM:HL_INPUT_GAIN Setting
    MAPI_U16    m_ADVSND_SRS_PURESOUND_HL_INPUT_GAIN;
    ///SRS PURESOUND PARAM:HL_OUTPUT_GAIN Setting
    MAPI_U16    m_ADVSND_SRS_PURESOUND_HL_OUTPUT_GAIN;
    ///SRS PURESOUND PARAM:HL_BYPASS_GAIN Setting
    MAPI_U16    m_ADVSND_SRS_PURESOUND_HL_BYPASS_GAIN;
    ///SRS PURESOUND PARAM:HL_LIMITERBOOST Setting
    MAPI_U16    m_ADVSND_SRS_PURESOUND_HL_LIMITERBOOST;
    ///SRS PURESOUND PARAM:HL_HARDLIMIT Setting
    MAPI_U16    m_ADVSND_SRS_PURESOUND_HL_HARDLIMIT;
    ///SRS PURESOUND PARAM:HL_DELAYLEN Setting
    MAPI_U16    m_ADVSND_SRS_PURESOUND_HL_DELAYLEN;
    ///SRS PURESOUND PARAM:AEQ_INPUT_GAIN Setting
    MAPI_U16    m_ADVSND_SRS_PURESOUND_AEQ_INPUT_GAIN;
    ///SRS PURESOUND PARAM:AEQ_OUTPUT_GAIN Setting
    MAPI_U16    m_ADVSND_SRS_PURESOUND_AEQ_OUTPUT_GAIN;
    ///SRS PURESOUND PARAM:AEQ_BYPASS_GAIN Setting
    MAPI_U16    m_ADVSND_SRS_PURESOUND_AEQ_BYPASS_GAIN;
    ///SRS PURESOUND PARAM:HPF_FREQUENCY Setting
    MAPI_U16    m_ADVSND_SRS_PURESOUND_HPF_FREQUENCY;
    ///SRS PURESOUND PARAM:TBHD_TRUBASS_LEVEL Setting
    MAPI_U16    m_ADVSND_SRS_PURESOUND_TBHD_TRUBASS_LEVEL;
    ///SRS PURESOUND PARAM:TBHD_SPEAKER_SIZE Setting
    MAPI_U16    m_ADVSND_SRS_PURESOUND_TBHD_SPEAKER_SIZE;
    ///SRS PURESOUND PARAM:TBHD_LEVEL_INDEPENDENT_EN Setting
    MAPI_U16    m_ADVSND_SRS_PURESOUND_TBHD_LEVEL_INDEPENDENT_EN;
    ///SRS PURESOUND PARAM:TBHD_COMPRESSOR_LEVEL Setting
    MAPI_U16    m_ADVSND_SRS_PURESOUND_TBHD_COMPRESSOR_LEVEL;
    ///SRS PURESOUND PARAM:TBHD_MODE Setting
    MAPI_U16    m_ADVSND_SRS_PURESOUND_TBHD_MODE;
    ///SRS PURESOUND PARAM:TBHD_SPEAKER_AUDIO Setting
    MAPI_U16    m_ADVSND_SRS_PURESOUND_TBHD_SPEAKER_AUDIO;
    ///SRS PURESOUND PARAM:TBHD_SPEAKER_ANALYSIS Setting
    MAPI_U16    m_ADVSND_SRS_PURESOUND_TBHD_SPEAKER_ANALYSIS;
    ///SRS PURESOUND PARAM:INPUT_GAIN Setting
    MAPI_U16    m_ADVSND_SRS_PURESOUND_INPUT_GAIN;
    ///SRS PURESOUND PARAM:OUTPUT_GAIN Setting
    MAPI_U16    m_ADVSND_SRS_PURESOUND_OUTPUT_GAIN;
    ///SRS PURESOUND PARAM:AudioDelayOffset Setting
    MAPI_U8 m_u8AudioDelayOffset;
    MAPI_U8 m_u8SPDIFDelayOffset;
    
    ///DTV or DMP Decoder Control Command
    DTV_DMP_AUDIO_DEC_CTRLCMD_TYPE m_DTV_DMP_DECODER_CTRL_CMD;
    ///DTV Decoder Sound Mode
    DTV_DMP_AUDIO_DEC_SOUNDMOD_TYPE m_DTV_DECOCDER_SOUNDMODE;
    ///Msrv_SSSound Debug Level
    MSRV_SSSOUND_DEBUG_LEVEL m_msrv_sssound_debug_level;
    //-------------------------------------------------------------------------------------------------
    /// To Calculate sound nonlinear
    /// @param pNonLinearCurve             \b IN: curve of the item
    /// @param AdjustValue             \b IN: user value
    /// @return U8
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 Sound_NonLinearCalculate_100(MS_SND_NLA_POINT * pNonLinearCurve, U8 AdjustValue);

    //-------------------------------------------------------------------------------------------------
    /// Transform value to the system domain
    /// Get the volume value that normalized from (VOLUME_SCALE_MIN = 0, VOLUME_SCALE_MAX = 100) to (min, max)
    /// @param value   \b IN: the value needs to transform
    /// @param min     \n IN: minimal value in the source domain
    /// @param max     \n IN: maximal value in the source domain
    /// @return normalized value
    //-------------------------------------------------------------------------------------------------
    MAPI_U8 GetNormalizedVolume(MAPI_S32 value, MAPI_S32 min, MAPI_S32 max);
    void ProcessEvent(void * arg1, void * arg2, void * arg3);

    Sound_Hidev_mode Current_Hidev_Mode;


    MSrv_MountNotifier * m_pMountNotifier;
    void *m_pUpdateInfo;

//protected :
};

/// OK
#define SSSOUND_OK                  (0x00000000UL)


