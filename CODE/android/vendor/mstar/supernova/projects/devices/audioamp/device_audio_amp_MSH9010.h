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

* Class : device_audio_amp_MSH9010
* File  : device_audio_amp_MSH9010.h
**********************************************************************/
#ifndef device_audio_amp_MSH9010_H
#define device_audio_amp_MSH9010_H

/*@ </IncludeGuard> @*/
#include "mapi_audio_amp.h"

class device_audio_amp_MSH9010 : public mapi_audio_amp
{
public:
    // ------------------------------------------------------------
    // public operations
    // ------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------
    /// Constructor
    /// @param  None
    /// @return None
    //-------------------------------------------------------------------------------------------------
    device_audio_amp_MSH9010(void);

    //-------------------------------------------------------------------------------------------------
    /// De-constructor
    /// @param  None
    /// @return None
    //-------------------------------------------------------------------------------------------------
    ~device_audio_amp_MSH9010(void);

    //-------------------------------------------------------------------------------------------------
    /// Init this Audio Amplifier
    /// @param None
    /// @return             \b OUT: MAPI_TRUE or MAPI_FALSE
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL Init(void);

    //-------------------------------------------------------------------------------------------------
    /// Finalize this Audio Amplifier
    /// @param None
    /// @return             \b OUT: MAPI_TRUE or MAPI_FALSE
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL Finalize(void);


    //-------------------------------------------------------------------------------------------------
    /// Mute this Audio Amplifier
    /// @param bMute        \b IN: TRUE for Mute; FALSE for unMute
    /// @return             \b OUT: MAPI_TRUE or MAPI_FALSE
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL Mute(MAPI_BOOL bMute);
 #if (KARAOKE_ENABLE == 1)
    MAPI_U8 Write_M62429(MAPI_U8 data , MAPI_U8 sum);
    void MicVol_Set(MAPI_U8 VolValue);
    void MicEcho_Set(MAPI_U8 EchoValue);
#endif
};


#endif // device_audio_amp_msh9010_H

