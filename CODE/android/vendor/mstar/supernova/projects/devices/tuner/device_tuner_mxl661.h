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

* Class : device_tuner_MXL661
* File  : device_tuner_MXL661.h
**********************************************************************/

#ifndef DEVICE_TUNER_MXL661
#define DEVICE_TUNER_MXL661

#include "mapi_tuner.h"
#include "MxL661/MxL661_TunerApi.h"

// ------------------------------------------------------------
// Other Define
// ------------------------------------------------------------

class device_tuner_MXL661 : public mapi_tuner
{
private:

    MAPI_BOOL m_bDeviceBusy;
    MXL661_COMMAND_T apiCmd;
    MXL661_SIGNAL_MODE_E curSignalMode;
    MAPI_BOOL m_bInATVScanMode;
    MAPI_BOOL m_bLastInATVScanMode;
    MAPI_BOOL m_bNeedResetAGCMode;
    MAPI_BOOL m_bInATVFinetuneMode;//Add to fix fineTune garbage(Mantis 0586666)
    MAPI_U32 u32GlobalATVRfFreqKHz;//Add to fix fineTune garbage(Mantis 0586666)
    MAPI_U32 u32chantuneFreq;	// record the frequency set by ChannelTune command
    EN_TUNER_MODE m_eLastTunerMode;
    MAPI_U8 m_u8LastOtherMode;
    pthread_mutex_t m_mutex_SetTune;    // the mutex for SetTune
    MXL661_AGC_ID_E AGC_Pin_Internal;
    MXL661_AGC_ID_E AGC_Pin_External;

public:



    // ------------------------------------------------------------
    // public operations
    // ------------------------------------------------------------
    device_tuner_MXL661(void);

    ~device_tuner_MXL661(void);

    //-------------------------------------------------------------------------------------------------
    /// To increase Tuner Init
    /// @param None
    /// @return             \b OUT: MAPI_TRUE or MAPI_FALSE
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL TunerInit(void);
#if (STR_ENABLE == 1)
        //-------------------------------------------------------------------------------------------------
        /// Tuner Reset
        /// @param None
        /// @return             \b OUT: MAPI_TRUE or MAPI_FALSE
        //-------------------------------------------------------------------------------------------------
        MAPI_BOOL TunerReset(void);
#endif

    //-------------------------------------------------------------------------------------------------
    /// To connect this tuner module
    /// @param None
    /// @return             \b OUT: MAPI_TRUE or MAPI_FALSE
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL Connect(void);

    //-------------------------------------------------------------------------------------------------
    /// To disconnect this tuner module
    /// @param None
    /// @return             \b OUT: MAPI_TRUE or MAPI_FALSE
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL Disconnect(void);
	
  // EosTek Patch Begin
    //-------------------------------------------------------------------------------------------------
    /// To disconnect this tuner module
    /// @param None
    /// @return             \b OUT: MAPI_TRUE or MAPI_FALSE
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL TunerStatus(void);
// EosTek Patch End

    //------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------
    /// To set tuner AFC range. When do channel sacn in analog TV mode,
    //   MXL661_AFC_30KHZ or MXL661_AFC_50KHZ is recommended to use.
    //  When exit channel scan procedure, MXL661_AFC_DEFAULT shall be set.
    /// @param u32FreqOffsetKHz: AFC range in kHz unit
    /// @return             \b OUT: MAPI_TRUE or MAPI_FALSE
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL Tuner_AFC_RangeSet(MXL661_AFC_RANGE_E u32FreqOffsetKHz);

    //-------------------------------------------------------------------------------------------------
    /// Set the frequency to the tuner module for ATV
    /// @param u32FreqKHz   \b IN: Input the frequency with the unit = KHz
    /// @param eBand        \b IN: Input the band (E_RFBAND_VHF_LOW, E_RFBAND_VHF_HIGH, or E_RFBAND_UHF)
    /// @return             \b OUT: MAPI_TRUE or MAPI_FALSE
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL ATV_SetTune(MAPI_U32 u32FreqKHz, RFBAND eBand, EN_TUNER_MODE eMode,MAPI_U8 u8OtherMode = 0xff);

    //-------------------------------------------------------------------------------------------------
    /// Set the frequency to the tuner module for DTV
    /// @param Freq         \b IN: Input the frequency with the unit = KHz
    /// @param eBandWidth   \b IN: Input the BandWidth (E_RF_CH_BAND_6MHz E_RF_CH_BAND_7MHz, or E_RF_CH_BAND_8MHz)
    /// @return             \b OUT: MAPI_TRUE or MAPI_FALSE
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL DTV_SetTune(double Freq, RF_CHANNEL_BANDWIDTH eBandWidth, EN_TUNER_MODE eMode);

    //-------------------------------------------------------------------------------------------------
    /// Reserve extend command for customer. If you don't need it, you skip it.
    /// @param u8SubCmd     \b IN: Commad defined by the customer.
    /// @param u32Param1    \b IN: Defined by the customer.
    /// @param u32Param2    \b IN: Defined by the customer.
    /// @param u32Param3    \b IN: Defined by the customer.
    /// @return             \b OUT: MAPI_TRUE or MAPI_FALSE
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL ExtendCommand(MAPI_U8 u8SubCmd, MAPI_U32 u32Param1, MAPI_U32 u32Param2, void* pvoidParam3);

    //-------------------------------------------------------------------------------------------------
    /// For Config AGC mode
    /// @param eMode        \b IN: the tuner mode(TV system)
    /// @return             \b OUT: MAPI_TRUE or MAPI_FALSE
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL ConfigAGCMode(EN_TUNER_MODE eMode);

    //-------------------------------------------------------------------------------------------------
    /// For Tuner in scan mode or normal mode
    /// @param bScan        \b IN: true for scan mode,false for normal mode
    /// @return             \b OUT: MAPI_TRUE or MAPI_FALSE
    //-------------------------------------------------------------------------------------------------
    virtual MAPI_BOOL SetTunerInScanMode(MAPI_BOOL bScan);

    //-------------------------------------------------------------------------------------------------
    /// For Tuner in Finetune mode or normal mode
    /// @param bFinetune        \b IN: true for Finetune mode,false for normal mode
    /// @return             \b OUT: MAPI_TRUE or MAPI_FALSE
    //-------------------------------------------------------------------------------------------------
    virtual MAPI_BOOL SetTunerInFinetuneMode(MAPI_BOOL bFinetune);

    //-------------------------------------------------------------------------------------------------
    /// to convert IF freq
    /// @param u16IfFreqKHz        \b IN: IF freq number in KHz
    /// @return MXL_IF_FREQ_E      \b OUT: member of enum MXL_IF_FREQ_E
    //-------------------------------------------------------------------------------------------------
    MXL661_IF_FREQ_E  IfFreqConversion(MAPI_U16 u16IfFreqKHz);

    //-------------------------------------------------------------------------------------------------
    /// check the frequency is in fine-tune range or not
    /// @param u32FreqKHz   \b IN: Input the frequency with the unit = KHz
    /// @return             \b OUT: MAPI_TRUE or MAPI_FALSE
    //-------------------------------------------------------------------------------------------------
    MAPI_BOOL CheckFineTuneRange(MAPI_U32 u32FreqKHz);
   // EosTek Patch Begin
    MAPI_BOOL m_bTunerStatus;
// EosTek Patch End
};


#endif //device_tuner_MXL661
