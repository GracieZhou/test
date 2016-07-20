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
// Copyright (c) 2006-2009 MStar Semiconductor, Inc.
// All rights reserved.
//
// Unless otherwise stipulated in writing, any and all information contained
// herein regardless in any format shall remain the sole proprietary of
// MStar Semiconductor Inc. and be kept in strict confidence
// (¡§MStar Confidential Information¡¨) by the recipient.
// Any unauthorized act including without limitation unauthorized disclosure,
// copying, use, reproduction, sale, distribution, modification, disassembling,
// reverse engineering and compiling of the contents of MStar Confidential
// Information is unlawful and strictly prohibited. MStar hereby reserves the
// rights to any and all damages, losses, costs and expenses resulting therefrom.
//
////////////////////////////////////////////////////////////////////////////////

#ifndef __MSRV_UARTDEBUG_H
#define __MSRV_UARTDEBUG_H

#include "MSrv.h"
#include <pthread.h>
#include "mapi_uartdebug_datatype.h"
// EosTek Patch Begin
//ashton: for wb adjust
#include "mapi_types.h"
#include "MSrv_System_Database.h"
// EosTek Patch End



//=============================ysw=============================
struct structPageWBAdjustT
{
	U16 u8CurMode;
	U16 u8CurTemperature;
	U16 u8CurRGain;
	U16 u8CurGGain;
	U16 u8CurBGain;
	U16 u8CurROffset;
	U16 u8CurGOffset;
	U16 u8CurBOffset;
} ;



/// MSrv_UartDebug
//==================================ysw================================
class MSrv_UartDebug : public MSrv
{
	public:
		//-------------------------------------------------------------------------------------------------
		/// Get the instance of MSrv_UartDebug
		/// @return  the pointer to the instance
		//-------------------------------------------------------------------------------------------------
		static MSrv_UartDebug* GetInstance();

		//-------------------------------------------------------------------------------------------------
		/// destroy the instance of MSrv_UartDebug
		/// @return None
		//-------------------------------------------------------------------------------------------------
		static void DestroyInstance();
		//-------------------------------------------------------------------------------------------------
		/// Start uart debug, launch Thread to monitor input command
		/// @param eMode        \b  : UARTDBG_MODE_STD,             => data with attached protocol data for uart debug
		///                                       : UARTDBG_MODE_RAW,           => pure data without attached protocol data for uart debug
		///                                       : UARTDBG_MODE_STD_UART1,   => same with UARTDBG_MODE_STD, but for UART1
		///                                       : UARTDBG_MODE_CUS,            => for Customized 
		/// @return        \b Start Uart debug successful or not
		//-------------------------------------------------------------------------------------------------
		MAPI_BOOL Start(UartDebugMode eMode);
		//-------------------------------------------------------------------------------------------------
		/// Exit uart debug mode, stop minitor thread
		/// @return None
		//-------------------------------------------------------------------------------------------------
		void Exit(void);
		//-------------------------------------------------------------------------------------------------
		/// Customer Uart Debug function,customer add special use case here
		/// As uart debug mode is UARTDBG_MODE_CUS, monitor thread run this process
		/// @return None
		//-------------------------------------------------------------------------------------------------
		virtual void CusUartDebug_Start(void);

		//-------------------------------------------------------------------------------------------------
		/// Constructor
		//-------------------------------------------------------------------------------------------------
		MSrv_UartDebug();                          //ysw move from private to public 0331

		//-------------------------------------------------------------------------------------------------
		/// Destructor
		//-------------------------------------------------------------------------------------------------
		~MSrv_UartDebug();                        //  ysw move from private to public 0331              

		//====================ysw add start================================

		// EosTek Patch Begin
              //ashton: for wb adjust 
		MAPI_BOOL asGetUartSate(void);	

		void asDebugMsg(char msg[], U8 size);
		
		// EosTek Patch End


		// static void* InitThread(void *arg);
		//====================ysw add end=================================
	private:
			// EosTek Patch Begin
			//ashton: for wb adjust
            	MAPI_BOOL setpicture(int data,int command);
		MAPI_BOOL setADC_AGC_ADJ(int data, int command);
		void putcharbnum(const U8 *Byte, U8 num);
		void DecodeExtCommand(void);
		void DecodeNormalCommand(void);
		UartCommadType m_UartCommand; // Uart command struct
		void DecodeWhiteBalanceAutoAdjustCommand(void);
		void FactoryUart_InputCommandAction(void);   //20150421
		MAPI_INPUT_SOURCE_TYPE TransMsInputSoutrceTypeMapiInputSourceTo(EN_MS_INPUT_SOURCE_TYPE enMsInputSourceType); 
		MAPI_BOOL as_bIsUartStar;
		// EosTek Patch End
		
		void Uart_Reset(void); //longchaofeng add for skyworith
		void SaveCurrentTempColor(void);
		void Uart_RecvHandler(void);

		pthread_t m_threadUartDebugMode;

		static MSrv_UartDebug* m_pInstance;

		static void* InitThread(void *arg);

		static UartDebugMode m_UartDebugMode;



};

#endif /* __MSRV_UARTDEBUG_H  */

