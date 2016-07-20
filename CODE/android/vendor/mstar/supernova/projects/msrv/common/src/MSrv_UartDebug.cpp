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
// headers of itself
#include "MSrv_UartDebug.h"

// headers of standard C libs
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/ioctl.h>
#include <sys/time.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/prctl.h>
#include <linux/input.h>
#include <linux/uinput.h>
#include <time.h>
#include <sys/time.h>
#include <unistd.h>
#include <fcntl.h>
#include <termios.h>
#include <limits.h>
#include <unistd.h>

/////////////////ysw auto white add star ///////////////////////

#include "MSrv_System_Database.h"
#include "MSrv_Control.h"
#include "MSrv_Picture.h"
//#include "ScanManagerImplType.h"
#include "mapi_video_datatype.h"
//#include "MSrv_ATV_Database.h"
//#include "MSrv_Control_common.h"
#include "MSrv_Factory_Mode.h"
#include "mapi_interface.h"
//#include "mapi_demodulator_datatype.h"
#include "mapi_demodulator.h"

/////////////////ysw auto white add end///////////////////////

// headers of standard C++ libs
#include <new>

// headers of the same layer's
#include "debug.h"
#include "mthread.h"
#include "MTypes.h"

// headers of underlying layer's
#include "mapi_uartdebug.h"


#include "MsCommon.h" // this is a implicit inclusion needed by "apiXC.h"   //20150420
#define CMD_LINE_SIZE_WB  350

static FILE *output_fp = NULL;

//===================star=========//20150420
#define UART_TIMEOUT            2000        // 2000 ms
#define UART_PROTOCOL_DEBUG     0
#define ACE_REPORT_DEBUG 0

// ashton
#define SKYWORTH_ACKNUM  4


#if ACE_REPORT_DEBUG
#include "apiXC_Ace.h"
#endif

//===================end=========//
///////////////////////ysw add start//////////////////////
structPageWBAdjustT structPageWBAdjust; 
structPageWBAdjustT structPageWBAdjust_1; 
structPageWBAdjustT structPageWBAdjust_2; 

static int input_fd = -1;

// EosTek Patch Begin
//ashton: for wb adjust
static MSrv_Picture::EN_MS_COLOR_TEMP eCurColorType;
static mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA WarmColorTemp;
static mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA NormalColorTemp;
static mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA CoolColorTemp;
// EosTek Patch End



static MAPI_U8 _bIsExtMode = 0;
//static MAPI_U8 _bIsExtModeFac232 = 0;
static MAPI_BOOL _bStartSession = FALSE;
static MAPI_U32 _u32SessionStartTime = 0;   //20150420
static MAPI_U8 recv_buffer[1024];  //20150420
static MAPI_BOOL  u8FactoryUARTEnable = FALSE;   //20150420


const U8 SKYWORTHACK_EXC[SKYWORTH_ACKNUM] = {0x5B, 0x04, 0x0A, 0x69};
const U8 SKYWORTHACK_ERR_UNEXC[SKYWORTH_ACKNUM] = {0x5B, 0x04, 0x0C, 0x6B};
const U8 SKYWORTHACK_RESENT[SKYWORTH_ACKNUM] = {0x5B, 0x04, 0x0E, 0x6D};
static int countflsgs =0;



#define PHOENIX_DEBUG 1
#define CANOPUS_DEBUG 0
#define EEPROM_DEBUG 0
#define IIC_DEBUG 0
#define UART_TEST_DEBUG 1

#define Factory_UARTPutCmd_Num  4
#define Factory_UARTInputCmd_Num  5
const U8 g_FactoryUartPutCommandA[Factory_UARTPutCmd_Num] = {0x5b,0x04,0x0a,0x69};//success
const U8 g_FactoryUartPutCommandB[Factory_UARTPutCmd_Num] = {0x5b,0x04,0x0c,0x6b};//feifa
const U8 g_FactoryUartPutCommandC[Factory_UARTPutCmd_Num] = {0x5b,0x04,0x0e,0x6d};//check error

const U8 g_FactoryUartPutADCCommandPass[Factory_UARTInputCmd_Num] = {0x5b,0x05,0x62,0x01,0xc3};
const U8 g_FactoryUartPutADCCommandFail[Factory_UARTInputCmd_Num] = {0x5b,0x05,0x62,0x00,0xc2};

#define ReplyNum  3
const U8 ReplyData[ReplyNum] = {0x99,0x4F,0x4B};//success


U8   m_bUart0Detected; // decode command flag
U16  m_Uart0CheckTick;
////////////////////////ysw add  end//////////
// UART receive
static MAPI_U8 _u8ReceivedBytes = 0;
static MAPI_U8 _cmdLine[CMD_LINE_SIZE_WB];
static MAPI_BOOL _bExitUartDebug = FALSE;
static MAPI_U8 _u8CmdLen = 0;


UartDebugMode MSrv_UartDebug::m_UartDebugMode = UARTDBG_MODE_STD;
MSrv_UartDebug* MSrv_UartDebug::m_pInstance = NULL;



void MSrv_UartDebug::putcharbnum(const U8 *Byte, U8 num)
{
	U32 ret = 0;
	if (input_fd)
	{
		ret = write(input_fd,(const MAPI_U8 *)Byte, num);
		if(ret != num)
		{
			write(input_fd,"error", 5);
		}
		
		sync();
	}	
}


U16 NU255toU2047 ( U16 wValue, U16 wMinValue, U16 wMaxValue )
{
    double dfTemp;
    wMaxValue = wMaxValue - wMinValue;
    if(!wValue)
        wValue=wMinValue;
    else
    {
        dfTemp=(double)(((wValue)*wMaxValue)/255.0+wMinValue);
        wValue=((wValue)*wMaxValue)/255+wMinValue;

        if((double)(dfTemp-wValue)>0.4)
            wValue=wValue+1;
    }
    return(U16) wValue;
}





MSrv_UartDebug::MSrv_UartDebug()
{
	memset(&m_threadUartDebugMode, 0, sizeof(pthread_t));

	_bExitUartDebug = TRUE;

	m_UartDebugMode = UARTDBG_MODE_STD;

	   // EosTek Patch Begin
	   //ashton: for wb adjust
       as_bIsUartStar = FALSE; 
	// EosTek Patch End

}

MSrv_UartDebug::~MSrv_UartDebug()
{
	void *thread_result;
	int intPTHChk;

	intPTHChk = PTH_RET_CHK(pthread_join(m_threadUartDebugMode, &thread_result));
	if(intPTHChk != 0)
	{
		printf("threadSignalMonitor join failed");
	}
	else
	{
		printf("Exit threadSignalMonitor Success.\n");
	}
}


//======================ysw====================

MSrv_UartDebug* MSrv_UartDebug::GetInstance(void)
{
	if(m_pInstance == NULL)
	{
		m_pInstance = new (std::nothrow) MSrv_UartDebug;
		ASSERT(m_pInstance);
	}
	return m_pInstance;
}

void MSrv_UartDebug::DestroyInstance()
{
	if(m_pInstance != NULL)
	{
		delete m_pInstance;
		m_pInstance = NULL;
	}
}

void* MSrv_UartDebug::InitThread(void *arg)
{
	ASSERT(arg);

	prctl(PR_SET_NAME, (unsigned long)"MSrv_UartDebug Task");

	((MSrv_UartDebug*)arg)->CusUartDebug_Start();

	return NULL;
}





MAPI_BOOL MSrv_UartDebug::Start(UartDebugMode eMode)
{
	MAPI_BOOL bRetChk = MAPI_FALSE;
	printf("\r\nMSrv_UartDebug::Start %d\n", eMode);

	printf("\r\n======7777==========MSrv_UartDebug::Start %d\n", eMode);
	m_UartDebugMode = eMode;
	if(UARTDBG_MODE_CUS > m_UartDebugMode)
	{
		mapi_uartdebug* uartDebug = mapi_uartdebug::GetInstance();
		if(uartDebug != NULL)
		{
			_bExitUartDebug = FALSE;
			bRetChk = uartDebug->Start(m_UartDebugMode);
			return bRetChk;
		}
		else
		{
			return MAPI_FALSE;
		}
	}
	else if(UARTDBG_MODE_CUS == m_UartDebugMode)
	{
		int intPTHChk;

		pthread_attr_t attr;
		pthread_attr_init(&attr);
		pthread_attr_setstacksize(&attr, PTHREAD_STACK_SIZE);
		intPTHChk = PTH_RET_CHK(pthread_create(&m_threadUartDebugMode, &attr, InitThread, this));
		if(intPTHChk != 0)
		{
			return MAPI_FALSE;
		}
		else
		{
			return MAPI_TRUE;
		}
	}
	else
	{
		printf("\t =>[Invalid Uart Debug Mode,Please check]\n");
		return MAPI_FALSE;
	}


}

void MSrv_UartDebug::Exit(void)
{

	if(_bExitUartDebug == FALSE)
	{
		_bExitUartDebug = TRUE;

		if(UARTDBG_MODE_CUS > m_UartDebugMode)
		{
			mapi_uartdebug* uartDebug = mapi_uartdebug::GetInstance();
			if (uartDebug != NULL)
			{
				uartDebug->DestroyInstance();
			}
		}
		else if(UARTDBG_MODE_CUS == m_UartDebugMode)
		{
			void *thread_result;
			int intPTHChk;

			intPTHChk = PTH_RET_CHK(pthread_join(m_threadUartDebugMode, &thread_result));
			if(intPTHChk != 0)
			{
				printf("m_threadUartDebugStart join failed");
			}
			else
			{
				printf("Exit m_threadUartDebugStart Success.\n");
			}
		}
		else
		{
			printf("\t Invalid uart debug mode exit,do nothing\n");
		}
	}
}

inline static void putcharb(MS_U8 byte)
{
	fputc(byte, output_fp);
}



/////////////////auto white add star ///////////////////////

BOOL MSrv_UartDebug::setpicture(int data,int command)
{
	bool flag=false;
	MAPI_INPUT_SOURCE_TYPE enCurrentInputType;
	T_MS_VIDEO stVedioTemp;
	enCurrentInputType = MSrv_Control::GetInstance()->GetCurrentInputSource();
	MSrv_Control::GetMSrvSystemDatabase()->GetVideo(&stVedioTemp, &enCurrentInputType);

	MS_USER_SYSTEM_SETTING stGetSystemSetting;
	MSrv_Control::GetMSrvSystemDatabase()->GetUserSystemSetting(&stGetSystemSetting);	
	switch (command)
	{
		case 0x25:    //chang PictureMode
			{
				switch (data)
				{
					case 0x01:
						///stVedioTemp.ePicture=PICTURE_NATURAL;
						stVedioTemp.ePicture=PICTURE_NORMAL;
						printf("xhw_stVedioTemp.ePicture.PICTURE_NORMAL\n");
						break;
					case 0x02:
						stVedioTemp.ePicture=PICTURE_DYNAMIC;//PICTURE_DYNAMIC;
						printf("xhw_stVedioTemp.ePicture.PICTURE_VIVID\n");
						break;
					case 0x03:
						//stVedioTemp.ePicture=PICTURE_MILD;
						stVedioTemp.ePicture=PICTURE_MOVIE;
						printf("xhw_stVedioTemp.ePicture.PICTURE_MOVIE\n");
						break;
					case 0x04:
						stVedioTemp.ePicture=PICTURE_USER;
						printf("xhw_stVedioTemp.ePicture.PICTURE_USER\n");
						break;
					default:
						//xuhongwen hide
						// putcharbnum(g_FactoryUartPutCommandB,Factory_UARTPutCmd_Num);
						return true;
						break;
				}
				MSrv_Control::GetMSrvSystemDatabase()->SetVideo(&stVedioTemp, &enCurrentInputType);
				MSrv_Control::GetMSrvPicture()->SetPictureModeBrightness(stVedioTemp.astPicture[stVedioTemp.ePicture].u8Brightness);
				MSrv_Control::GetMSrvPicture()->SetPictureModeContrast(stVedioTemp.astPicture[stVedioTemp.ePicture].u8Contrast);
				MSrv_Control::GetMSrvPicture()->SetPictureModeColor(stVedioTemp.astPicture[stVedioTemp.ePicture].u8Saturation);
				MSrv_Control::GetMSrvPicture()->SetPictureModeSharpness(stVedioTemp.astPicture[stVedioTemp.ePicture].u8Sharpness);

				//    putcharbnum(g_FactoryUartPutCommandA,Factory_UARTPutCmd_Num);
				flag = true;

				putcharbnum(ReplyData,ReplyNum);	  
			}
			break;
		case 0x26:    //change OSD brightness,contrast or restore to OSD default
			{
				if (stVedioTemp.ePicture != PICTURE_USER)
				{
					stVedioTemp.ePicture=PICTURE_USER;
				}
				switch(data)
				{
					case 0x01:   //change OSD brightness,contrast to max value
						stVedioTemp.astPicture[stVedioTemp.ePicture].u8Brightness=100;
						stVedioTemp.astPicture[stVedioTemp.ePicture].u8Contrast=100;
						MSrv_Control::GetMSrvSystemDatabase()->SetVideo(&stVedioTemp, &enCurrentInputType);
						MSrv_Control::GetMSrvPicture()->SetPictureModeBrightness(stVedioTemp.astPicture[stVedioTemp.ePicture].u8Brightness);
						MSrv_Control::GetMSrvPicture()->SetPictureModeContrast(stVedioTemp.astPicture[stVedioTemp.ePicture].u8Contrast);
						break;
					case 0x02:     //change OSD brightness,contrast to min value
						stVedioTemp.astPicture[stVedioTemp.ePicture].u8Brightness=0;
						stVedioTemp.astPicture[stVedioTemp.ePicture].u8Contrast=0;
						MSrv_Control::GetMSrvSystemDatabase()->SetVideo(&stVedioTemp, &enCurrentInputType);
						MSrv_Control::GetMSrvPicture()->SetPictureModeBrightness(stVedioTemp.astPicture[stVedioTemp.ePicture].u8Brightness);
						MSrv_Control::GetMSrvPicture()->SetPictureModeContrast(stVedioTemp.astPicture[stVedioTemp.ePicture].u8Contrast);
						break;
					case 0x03:    //restore to OSD default
						

						break;
					default:
						return true;
						break;
				}

				flag = true;
				putcharbnum(ReplyData,ReplyNum);	  

			}
			break;
		case 0x34:
			{
				// U8 u8backlight = stVedioTemp.astPicture[stVedioTemp.ePicture].u8Backlight;
				switch(data)
				{
					case 0xFF:  //Max BackLight
						{
							stVedioTemp.astPicture[stVedioTemp.ePicture].u8Backlight=100;
							MSrv_Control::GetMSrvSystemDatabase()->SetVideo(&stVedioTemp, &enCurrentInputType);
							MSrv_Control::GetMSrvPicture()->SetBacklight(stVedioTemp.astPicture[stVedioTemp.ePicture].u8Backlight);
							flag = true;
							break;
						}  
					case 0x00:   //BackLight-1
						{
							if(stVedioTemp.astPicture[stVedioTemp.ePicture].u8Backlight >0)
							{
								stVedioTemp.astPicture[stVedioTemp.ePicture].u8Backlight --;
							}
							else
							{
								stVedioTemp.astPicture[stVedioTemp.ePicture].u8Backlight = 0;
							}
							MSrv_Control::GetMSrvSystemDatabase()->SetVideo(&stVedioTemp, &enCurrentInputType);
							MSrv_Control::GetMSrvPicture()->SetBacklight(stVedioTemp.astPicture[stVedioTemp.ePicture].u8Backlight);
							flag = true;
							break;
						}
					case 0x01:  //BackLight+1
						{
							if(stVedioTemp.astPicture[stVedioTemp.ePicture].u8Backlight < 100)
							{
								stVedioTemp.astPicture[stVedioTemp.ePicture].u8Backlight ++;
							}
							else
							{
								stVedioTemp.astPicture[stVedioTemp.ePicture].u8Backlight = 100;
							}
							MSrv_Control::GetMSrvSystemDatabase()->SetVideo(&stVedioTemp, &enCurrentInputType);
							MSrv_Control::GetMSrvPicture()->SetBacklight(stVedioTemp.astPicture[stVedioTemp.ePicture].u8Backlight);
							flag = true;
							break;
						}
					case 0x10:   //BackLight-10
						{
							if(stVedioTemp.astPicture[stVedioTemp.ePicture].u8Backlight >= 10)
							{
								stVedioTemp.astPicture[stVedioTemp.ePicture].u8Backlight = stVedioTemp.astPicture[stVedioTemp.ePicture].u8Backlight - 10;
							}
							else
							{
								stVedioTemp.astPicture[stVedioTemp.ePicture].u8Backlight = 0;
							}
							MSrv_Control::GetMSrvSystemDatabase()->SetVideo(&stVedioTemp, &enCurrentInputType);
							MSrv_Control::GetMSrvPicture()->SetBacklight(stVedioTemp.astPicture[stVedioTemp.ePicture].u8Backlight);
							flag = true;
							break;
						}	 
					case 0x11:    //BackLight+10
						{
							if(stVedioTemp.astPicture[stVedioTemp.ePicture].u8Backlight <= 90)
							{
								stVedioTemp.astPicture[stVedioTemp.ePicture].u8Backlight = stVedioTemp.astPicture[stVedioTemp.ePicture].u8Backlight + 10;
							}
							else
							{
								stVedioTemp.astPicture[stVedioTemp.ePicture].u8Backlight = 100;
							}
							MSrv_Control::GetMSrvSystemDatabase()->SetVideo(&stVedioTemp, &enCurrentInputType);
							MSrv_Control::GetMSrvPicture()->SetBacklight(stVedioTemp.astPicture[stVedioTemp.ePicture].u8Backlight);
							flag = true;
							break;
						}

					case 0x12:
						{
										
							// need do nothing, backlight already saved when set data >> SetVideo

							break;	
						}
					default:
						return false;
				}
			}
			putcharbnum(ReplyData,ReplyNum);	  
			break;

		default:
			return false;


			break;
	}

	return flag;
}



BOOL MSrv_UartDebug::setADC_AGC_ADJ(int data, int command)
{
	static int counttemp =0;
	bool flag=false;
	MAPI_INPUT_SOURCE_TYPE enCurrentInputType;
	enCurrentInputType = MSrv_Control::GetInstance()->GetCurrentInputSource();

	T_MS_VIDEO tmpVideo;
	MSrv_Control *pMSrvControl = MSrv_Control::GetInstance();
	MAPI_INPUT_SOURCE_TYPE enSrcType = MSrv_Control::GetInstance()->GetCurrentInputSource();
	pMSrvControl->GetMSrvSystemDatabase()->GetVideo(&tmpVideo, &enSrcType);
    MSrv_Factory_Mode *pMSrvFactory = MSrv_Control::GetMSrvFactoryMode();
    if (NULL == pMSrvFactory)
    {
        printf("get MSrv_Factory_Mode is null. \n");
        return FALSE;
    }
	MSrv_Picture::EN_MS_COLOR_TEMP eColorTemp;
	eColorTemp = tmpVideo.astPicture[tmpVideo.ePicture].eColorTemp;//use it ????
	//U8 u8RedGain, u8GreenGain, u8BlueGain, u8RedOffset, u8GreenOffset, u8BlueOffset;

	if( counttemp == 0 )
		pMSrvFactory->GetWBGainOffsetEx(eColorTemp,//ysw  MSrv_Controlchange to MSrv_Control_common
				&structPageWBAdjust.u8CurRGain, &structPageWBAdjust.u8CurGGain, &structPageWBAdjust.u8CurBGain,
				&structPageWBAdjust.u8CurROffset, &structPageWBAdjust.u8CurGOffset, &structPageWBAdjust.u8CurBOffset,
				enSrcType);
#if UART_PROTOCOL_DEBUG
	printf("-------------setADC_AGC_ADJ ------------\n");
	printf("____________eColorTemp = %d\n",eColorTemp);
	printf("____________tmpVideo.ePicture = %d\n",tmpVideo.ePicture);
	printf("____________command = %d\n",command);
	printf("____________data = %d\n",data);

	printf("****xhw 0****u8CurRGain = %d\n",structPageWBAdjust.u8CurRGain);
	printf("****xhw 0****u8CurGGain = %d\n",structPageWBAdjust.u8CurGGain);
	printf("****xhw 0****u8CurBGain = %d\n",structPageWBAdjust.u8CurBGain);
	printf("****xhw 0****u8CurROffGain = %d\n",structPageWBAdjust.u8CurROffset);
	printf("****xhw 0****u8CurGOffGain = %d\n",structPageWBAdjust.u8CurGOffset);
	printf("****xhw 0****u8CurBOffGain = %d\n",structPageWBAdjust.u8CurBOffset);
	printf("############setADC_AGC_ADJ ###########\n");
#endif

	const U16 u16InputVSize = mapi_interface::Get_mapi_pql(MAPI_MAIN_WINDOW)->GetVsize();


	switch (command)
	{

		case 0x24:  //change ColorTemp
			{

				switch (data)
				{
					case 0x02:
						//eColorTemp=MSrv_Picture::MS_COLOR_TEMP_MEDIUM;
						eColorTemp=MSrv_Picture::MS_COLOR_TEMP_NATURE;
#if UART_PROTOCOL_DEBUG
						printf("___________MSrv_Picture::MS_COLOR_TEMP_NATURE\n");
#endif
						break;
					case 0x03:
						eColorTemp=MSrv_Picture::MS_COLOR_TEMP_WARM;
#if UART_PROTOCOL_DEBUG
						printf("____________MSrv_Picture::MS_COLOR_TEMP_WARM\n");
#endif
						break;
					case 0x01:
						eColorTemp=MSrv_Picture::MS_COLOR_TEMP_COOL;
#if UART_PROTOCOL_DEBUG
						printf("\n____________MSrv_Picture::MS_COLOR_TEMP_COOL\n");
#endif
						break;
					default:

						//     putcharbnum(g_FactoryUartPutCommandB,Factory_UARTPutCmd_Num);

						return true;
						break;
				}
				//putcharbnum(g_FactoryUartPutCommandA,Factory_UARTPutCmd_Num);
				eCurColorType = eColorTemp;
				pMSrvFactory->GetWBGainOffsetEx(eColorTemp,
						&structPageWBAdjust_1.u8CurRGain, &structPageWBAdjust_1.u8CurGGain, &structPageWBAdjust_1.u8CurBGain,
						&structPageWBAdjust_1.u8CurROffset, &structPageWBAdjust_1.u8CurGOffset, &structPageWBAdjust_1.u8CurBOffset,
						enSrcType);
#if UART_PROTOCOL_DEBUG
				printf("****xhw 1****counttemp = %d\n",counttemp);
				printf("****xhw 1****u8CurRGain = %d\n",structPageWBAdjust_1.u8CurRGain);
				printf("****xhw 1****u8CurGGain = %d\n",structPageWBAdjust_1.u8CurGGain);
				printf("****xhw 1 ****u8CurBGain = %d\n",structPageWBAdjust_1.u8CurBGain);
				printf("****xhw 1 ****u8CurROffGain = %d\n",structPageWBAdjust_1.u8CurROffset);
				printf("****xhw 1****u8CurGOffGain = %d\n",structPageWBAdjust_1.u8CurGOffset);
				printf("****xhw 1****u8CurBOffGain = %d\n",structPageWBAdjust_1.u8CurBOffset);
#endif
				pMSrvFactory->SetWBGainOffsetEx(eCurColorType, structPageWBAdjust_1.u8CurRGain, structPageWBAdjust_1.u8CurGGain, structPageWBAdjust_1.u8CurBGain, structPageWBAdjust_1.u8CurROffset, structPageWBAdjust_1.u8CurGOffset, structPageWBAdjust_1.u8CurBOffset,enSrcType);
#if UART_PROTOCOL_DEBUG
				printf("****xhw****counttemp = %d\n",counttemp);
				printf("****xhw****u8CurRGain = %d\n",structPageWBAdjust.u8CurRGain);
				printf("****xhw****u8CurGGain = %d\n",structPageWBAdjust.u8CurGGain);
				printf("****xhw****u8CurBGain = %d\n",structPageWBAdjust.u8CurBGain);
				printf("****xhw****u8CurROffGain = %d\n",structPageWBAdjust.u8CurROffset);
				printf("****xhw****u8CurGOffGain = %d\n",structPageWBAdjust.u8CurGOffset);
				printf("****xhw****u8CurBOffGain = %d\n",structPageWBAdjust.u8CurBOffset);
#endif
				flag = true;

				putcharbnum(ReplyData,ReplyNum);	  		
			}
			break;
		case 0x27: //AutoADC
			{
				if(data == 0x01)		
				{
					{
						if (enCurrentInputType==MAPI_INPUT_SOURCE_VGA)
						{
							if (pMSrvFactory->AutoADC())
							{
								flag=true;
							} 
							else
							{
								flag=false;
							}
						} 
						else if (enCurrentInputType==MAPI_INPUT_SOURCE_YPBPR)
						{

							if (pMSrvFactory->AutoADC())
							{
								flag=true;
							}
							else
							{
								flag=false;
							}
						}
						else
						{
							flag=false;
						}
					}
					putcharbnum(ReplyData,ReplyNum);	  	
					break;
				}
				else if(data == 0x02)
				{
					MAPI_PQL_CALIBRATION_DATA tmp_AdcGainOffset;
					memset(&tmp_AdcGainOffset ,0, sizeof(MAPI_PQL_CALIBRATION_DATA));
					if(enCurrentInputType == MAPI_INPUT_SOURCE_VGA)
					{
						pMSrvFactory->GetADCGainOffset(MAPI_MAIN_WINDOW, ADC_SET_VGA, &tmp_AdcGainOffset);
						int chsum;
						chsum = tmp_AdcGainOffset.u16RedGain + tmp_AdcGainOffset.u16BlueGain + tmp_AdcGainOffset.u16BlueGain +tmp_AdcGainOffset.u16RedOffset + tmp_AdcGainOffset.u16GreenOffset + tmp_AdcGainOffset.u16BlueOffset;
						if (chsum | 0x00)
						{
							putcharb(0x01);
							flag=true;
						}
						else
						{
							putcharb(0x00);
							flag=false;
						}

					}
					else if(enCurrentInputType == MAPI_INPUT_SOURCE_YPBPR)
					{
						if(u16InputVSize <1080)
						{
							pMSrvFactory->GetADCGainOffset(MAPI_MAIN_WINDOW, ADC_SET_YPBPR_SD, &tmp_AdcGainOffset);
							int chsum;
							chsum = tmp_AdcGainOffset.u16RedGain + tmp_AdcGainOffset.u16BlueGain + tmp_AdcGainOffset.u16BlueGain +tmp_AdcGainOffset.u16RedOffset + tmp_AdcGainOffset.u16GreenOffset + tmp_AdcGainOffset.u16BlueOffset;
							if (chsum | 0x00)
							{
								putcharb(0x01);
								flag=true;
							}
							else
							{
								putcharb(0x00);
								flag=false;
							}

						}
						else
						{
							pMSrvFactory->GetADCGainOffset(MAPI_MAIN_WINDOW, ADC_SET_YPBPR_HD, &tmp_AdcGainOffset);
							int chsum;
							chsum = tmp_AdcGainOffset.u16RedGain + tmp_AdcGainOffset.u16BlueGain + tmp_AdcGainOffset.u16BlueGain +tmp_AdcGainOffset.u16RedOffset + tmp_AdcGainOffset.u16GreenOffset + tmp_AdcGainOffset.u16BlueOffset;
							if (chsum | 0x00)
							{
								putcharb(0x01);
								flag=true;
							}
							else
							{
								putcharb(0x00);
								flag=false;
							}

						}
					}
					putcharbnum(ReplyData,ReplyNum);	    
					break;
				}
				else
				{

					flag=false;
					putcharbnum(ReplyData,ReplyNum);	  
					break;
				}

			}
		case 0x28:
			{	   
				int datavalue;
				datavalue = data & 0xFF;
				data = data >>8;
				data = data & 0xFF;



				switch(data)
				{
					case 0x01:  // set Red Gain
						{
							counttemp++;
							// structPageWBAdjust.u8CurRGain = datavalue*8;
							structPageWBAdjust.u8CurRGain = NU255toU2047((U16)datavalue , 0, 2047); 
							mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA tmpColorTemp;
							tmpColorTemp.u16RedGain =         structPageWBAdjust.u8CurRGain;
							tmpColorTemp.u16GreenGain =     structPageWBAdjust.u8CurGGain;
							tmpColorTemp.u16BlueGain =         structPageWBAdjust.u8CurBGain;
							tmpColorTemp.u16RedOffset =     structPageWBAdjust.u8CurROffset;
							tmpColorTemp.u16GreenOffset =     structPageWBAdjust.u8CurGOffset;
							tmpColorTemp.u16BlueOffset =     structPageWBAdjust.u8CurBOffset;
							//    putcharbnum(g_FactoryUartPutCommandA,Factory_UARTPutCmd_Num);
							mapi_interface::Get_mapi_pql(MAPI_MAIN_WINDOW)->SetColorTemperatureEx(&tmpColorTemp);
							pMSrvFactory->as_SetWBGainOffsetEx(eColorTemp, structPageWBAdjust.u8CurRGain, structPageWBAdjust.u8CurGGain, structPageWBAdjust.u8CurBGain, structPageWBAdjust.u8CurROffset, structPageWBAdjust.u8CurGOffset, structPageWBAdjust.u8CurBOffset,enSrcType);
							flag = true;
#if UART_PROTOCOL_DEBUG
							printf("****xhw 2.1****eColorTemp = %d\n",eColorTemp);
							printf("****xhw 2.1****tmpColorTemp.u16GreenGain = %d\n",structPageWBAdjust.u8CurRGain);
							printf("****xhw 2.1****u8CurGGain = %d\n",structPageWBAdjust.u8CurGGain);
							printf("****xhw 2.1****u8CurBGain = %d\n",structPageWBAdjust.u8CurBGain);
							printf("****xhw 2.1****u8CurROffGain = %d\n",structPageWBAdjust.u8CurROffset);
							printf("****xhw 2.1****u8CurGOffGain = %d\n",structPageWBAdjust.u8CurGOffset);
							printf("****xhw 2.1****u8CurBOffGain = %d\n",structPageWBAdjust.u8CurBOffset);
							printf("___________0X64_structPageWBAdjust.u8CurRGain = %d\n",structPageWBAdjust.u8CurRGain);
#endif


							break;
						}
					case 0x02:   // set Green Gain
						{
							counttemp++;
							// structPageWBAdjust.u8CurGGain = datavalue*8;
							structPageWBAdjust.u8CurGGain = NU255toU2047((U16)datavalue , 0, 2047); 
							mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA tmpColorTemp;
							tmpColorTemp.u16RedGain =         structPageWBAdjust.u8CurRGain;
							tmpColorTemp.u16GreenGain =     structPageWBAdjust.u8CurGGain;
							tmpColorTemp.u16BlueGain =         structPageWBAdjust.u8CurBGain;
							tmpColorTemp.u16RedOffset =     structPageWBAdjust.u8CurROffset;
							tmpColorTemp.u16GreenOffset =     structPageWBAdjust.u8CurGOffset;
							tmpColorTemp.u16BlueOffset =     structPageWBAdjust.u8CurBOffset;
							mapi_interface::Get_mapi_pql(MAPI_MAIN_WINDOW)->SetColorTemperatureEx(&tmpColorTemp);
							pMSrvFactory->as_SetWBGainOffsetEx(eColorTemp, structPageWBAdjust.u8CurRGain, structPageWBAdjust.u8CurGGain, structPageWBAdjust.u8CurBGain, structPageWBAdjust.u8CurROffset, structPageWBAdjust.u8CurGOffset, structPageWBAdjust.u8CurBOffset,enSrcType);
							flag = true;
#if UART_PROTOCOL_DEBUG
							printf("****xhw 2.2****eColorTemp = %d\n",eColorTemp);
							printf("****xhw 2.2****tmpColorTemp.u16GreenGain = %d\n",structPageWBAdjust.u8CurRGain);
							printf("****xhw 2.2****u8CurGGain = %d\n",structPageWBAdjust.u8CurGGain);
							printf("****xhw 2.2****u8CurBGain = %d\n",structPageWBAdjust.u8CurBGain);
							printf("****xhw 2.2****u8CurROffGain = %d\n",structPageWBAdjust.u8CurROffset);
							printf("****xhw 2.2****u8CurGOffGain = %d\n",structPageWBAdjust.u8CurGOffset);
							printf("****xhw 2.2****u8CurBOffGain = %d\n",structPageWBAdjust.u8CurBOffset);
							printf("___________0X64_structPageWBAdjust.u8CurRGain = %d\n",structPageWBAdjust.u8CurGGain);
#endif

							break;
						}
					case 0x03:  // set Blue Gain
						{
							counttemp++;
							// structPageWBAdjust.u8CurBGain = datavalue*8;
							structPageWBAdjust.u8CurBGain = NU255toU2047((U16)datavalue , 0, 2047); 
							mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA tmpColorTemp;
							tmpColorTemp.u16RedGain =         structPageWBAdjust.u8CurRGain;
							tmpColorTemp.u16GreenGain =     structPageWBAdjust.u8CurGGain;
							tmpColorTemp.u16BlueGain =         structPageWBAdjust.u8CurBGain;
							tmpColorTemp.u16RedOffset =     structPageWBAdjust.u8CurROffset;
							tmpColorTemp.u16GreenOffset =     structPageWBAdjust.u8CurGOffset;
							tmpColorTemp.u16BlueOffset =     structPageWBAdjust.u8CurBOffset;
							mapi_interface::Get_mapi_pql(MAPI_MAIN_WINDOW)->SetColorTemperatureEx(&tmpColorTemp);			     
							pMSrvFactory->as_SetWBGainOffsetEx(eColorTemp, structPageWBAdjust.u8CurRGain, structPageWBAdjust.u8CurGGain, structPageWBAdjust.u8CurBGain, structPageWBAdjust.u8CurROffset, structPageWBAdjust.u8CurGOffset, structPageWBAdjust.u8CurBOffset,enSrcType);
							flag = true;
#if UART_PROTOCOL_DEBUG
							printf("****xhw 2.3****eColorTemp = %d\n",eColorTemp);
							printf("****xhw 2.3****tmpColorTemp.u16GreenGain = %d\n",structPageWBAdjust.u8CurRGain);
							printf("****xhw 2.3****u8CurGGain = %d\n",structPageWBAdjust.u8CurGGain);
							printf("****xhw 2.3****u8CurBGain = %d\n",structPageWBAdjust.u8CurBGain);
							printf("****xhw 2.3****u8CurROffGain = %d\n",structPageWBAdjust.u8CurROffset);
							printf("****xhw 2.3****u8CurGOffGain = %d\n",structPageWBAdjust.u8CurGOffset);
							printf("****xhw 2.3****u8CurBOffGain = %d\n",structPageWBAdjust.u8CurBOffset);
							printf("___________0X64_structPageWBAdjust.u8CurRGain = %d\n",structPageWBAdjust.u8CurBGain);
#endif


							break;
						}
					default:
						flag = false;
						break;

				}


				if(eCurColorType == MSrv_Picture::MS_COLOR_TEMP_WARM )
				{

					WarmColorTemp.u16RedGain    		=     structPageWBAdjust.u8CurRGain;
					WarmColorTemp.u16GreenGain 		=	structPageWBAdjust.u8CurGGain;
					WarmColorTemp.u16BlueGain    	 	=  	structPageWBAdjust.u8CurBGain;
					WarmColorTemp.u16RedOffset    	=	structPageWBAdjust.u8CurROffset;
					WarmColorTemp.u16GreenOffset		= 	structPageWBAdjust.u8CurGOffset;
					WarmColorTemp.u16BlueOffset    	=	structPageWBAdjust.u8CurBOffset;
				}
				else if(eCurColorType == MSrv_Picture::MS_COLOR_TEMP_NATURE )
				{
					NormalColorTemp.u16RedGain    	=      structPageWBAdjust.u8CurRGain;
					NormalColorTemp.u16GreenGain 	=	structPageWBAdjust.u8CurGGain;
					NormalColorTemp.u16BlueGain    	=  	structPageWBAdjust.u8CurBGain;
					NormalColorTemp.u16RedOffset    	=	structPageWBAdjust.u8CurROffset;
					NormalColorTemp.u16GreenOffset	= 	structPageWBAdjust.u8CurGOffset;
					NormalColorTemp.u16BlueOffset    	=	structPageWBAdjust.u8CurBOffset;

				}
				else if(eCurColorType == MSrv_Picture::MS_COLOR_TEMP_COOL)
				{
					CoolColorTemp.u16RedGain    	=     structPageWBAdjust.u8CurRGain;
					CoolColorTemp.u16GreenGain 	=	structPageWBAdjust.u8CurGGain;
					CoolColorTemp.u16BlueGain    	=  	structPageWBAdjust.u8CurBGain;
					CoolColorTemp.u16RedOffset    	=	structPageWBAdjust.u8CurROffset;
					CoolColorTemp.u16GreenOffset	= 	structPageWBAdjust.u8CurGOffset;
					CoolColorTemp.u16BlueOffset    	=	structPageWBAdjust.u8CurBOffset;

				}
				else
				{
					// should not be go here.
					// somethine wrong happen, do nothing
				}
				putcharbnum(ReplyData,ReplyNum);
			}
			break;

		case 0x29: // set Red offset
			{
				counttemp++;
				structPageWBAdjust.u8CurROffset = data*2;
				mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA tmpColorTemp;
				tmpColorTemp.u16RedGain =         structPageWBAdjust.u8CurRGain;
				tmpColorTemp.u16GreenGain =     structPageWBAdjust.u8CurGGain;
				tmpColorTemp.u16BlueGain =         structPageWBAdjust.u8CurBGain;
				tmpColorTemp.u16RedOffset =     structPageWBAdjust.u8CurROffset;
				tmpColorTemp.u16GreenOffset =     structPageWBAdjust.u8CurGOffset;
				tmpColorTemp.u16BlueOffset =     structPageWBAdjust.u8CurBOffset;
				//    putcharbnum(g_FactoryUartPutCommandA,Factory_UARTPutCmd_Num);
				mapi_interface::Get_mapi_pql(MAPI_MAIN_WINDOW)->SetColorTemperatureEx(&tmpColorTemp);
				pMSrvFactory->as_SetWBGainOffsetEx(eColorTemp, structPageWBAdjust.u8CurRGain, structPageWBAdjust.u8CurGGain, structPageWBAdjust.u8CurBGain, structPageWBAdjust.u8CurROffset, structPageWBAdjust.u8CurGOffset, structPageWBAdjust.u8CurBOffset,enSrcType);
				if(eCurColorType == MSrv_Picture::MS_COLOR_TEMP_WARM )
				{

					WarmColorTemp.u16RedGain    		=     structPageWBAdjust.u8CurRGain;
					WarmColorTemp.u16GreenGain 		=	structPageWBAdjust.u8CurGGain;
					WarmColorTemp.u16BlueGain    	 	=  	structPageWBAdjust.u8CurBGain;
					WarmColorTemp.u16RedOffset    	=	structPageWBAdjust.u8CurROffset;
					WarmColorTemp.u16GreenOffset		= 	structPageWBAdjust.u8CurGOffset;
					WarmColorTemp.u16BlueOffset    	=	structPageWBAdjust.u8CurBOffset;
				}
				else if(eCurColorType == MSrv_Picture::MS_COLOR_TEMP_NATURE )
				{
					NormalColorTemp.u16RedGain    	=     structPageWBAdjust.u8CurRGain;
					NormalColorTemp.u16GreenGain 	=	structPageWBAdjust.u8CurGGain;
					NormalColorTemp.u16BlueGain    	=  	structPageWBAdjust.u8CurBGain;
					NormalColorTemp.u16RedOffset    	=	structPageWBAdjust.u8CurROffset;
					NormalColorTemp.u16GreenOffset	= 	structPageWBAdjust.u8CurGOffset;
					NormalColorTemp.u16BlueOffset    	=	structPageWBAdjust.u8CurBOffset;

				}
				else if(eCurColorType == MSrv_Picture::MS_COLOR_TEMP_COOL)
				{
					CoolColorTemp.u16RedGain    	=      structPageWBAdjust.u8CurRGain;
					CoolColorTemp.u16GreenGain 	=	structPageWBAdjust.u8CurGGain;
					CoolColorTemp.u16BlueGain    	=  	structPageWBAdjust.u8CurBGain;
					CoolColorTemp.u16RedOffset    	=	structPageWBAdjust.u8CurROffset;
					CoolColorTemp.u16GreenOffset	= 	structPageWBAdjust.u8CurGOffset;
					CoolColorTemp.u16BlueOffset    	=	structPageWBAdjust.u8CurBOffset;

				}
				else
				{
					// should not be go here.
					// somethine wrong happen, do nothing
				}



				flag = true;
#if UART_PROTOCOL_DEBUG
				printf("__________0X67__structPageWBAdjust.u8CurROffset = %d\n",structPageWBAdjust.u8CurROffset);
#endif

				putcharbnum(ReplyData,ReplyNum);

			}
			break;
		case 0x30: // set Green offset
			{
				counttemp++;
				structPageWBAdjust.u8CurGOffset = data*2;
				mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA tmpColorTemp;
				tmpColorTemp.u16RedGain =         structPageWBAdjust.u8CurRGain;
				tmpColorTemp.u16GreenGain =     structPageWBAdjust.u8CurGGain;
				tmpColorTemp.u16BlueGain =         structPageWBAdjust.u8CurBGain;
				tmpColorTemp.u16RedOffset =     structPageWBAdjust.u8CurROffset;
				tmpColorTemp.u16GreenOffset =     structPageWBAdjust.u8CurGOffset;
				tmpColorTemp.u16BlueOffset =     structPageWBAdjust.u8CurBOffset;
				//    putcharbnum(g_FactoryUartPutCommandA,Factory_UARTPutCmd_Num);
				mapi_interface::Get_mapi_pql(MAPI_MAIN_WINDOW)->SetColorTemperatureEx(&tmpColorTemp);
				pMSrvFactory->as_SetWBGainOffsetEx(eColorTemp, structPageWBAdjust.u8CurRGain, structPageWBAdjust.u8CurGGain, structPageWBAdjust.u8CurBGain, structPageWBAdjust.u8CurROffset, structPageWBAdjust.u8CurGOffset, structPageWBAdjust.u8CurBOffset,enSrcType);
				if(eCurColorType == MSrv_Picture::MS_COLOR_TEMP_WARM )
				{

					WarmColorTemp.u16RedGain    		=     structPageWBAdjust.u8CurRGain;
					WarmColorTemp.u16GreenGain 		=	structPageWBAdjust.u8CurGGain;
					WarmColorTemp.u16BlueGain    	 	=  	structPageWBAdjust.u8CurBGain;
					WarmColorTemp.u16RedOffset    	=	structPageWBAdjust.u8CurROffset;
					WarmColorTemp.u16GreenOffset		= 	structPageWBAdjust.u8CurGOffset;
					WarmColorTemp.u16BlueOffset    	=	structPageWBAdjust.u8CurBOffset;
				}
				else if(eCurColorType == MSrv_Picture::MS_COLOR_TEMP_NATURE )
				{
					NormalColorTemp.u16RedGain    	=     structPageWBAdjust.u8CurRGain;
					NormalColorTemp.u16GreenGain 	=	structPageWBAdjust.u8CurGGain;
					NormalColorTemp.u16BlueGain    	=  	structPageWBAdjust.u8CurBGain;
					NormalColorTemp.u16RedOffset    	=	structPageWBAdjust.u8CurROffset;
					NormalColorTemp.u16GreenOffset	= 	structPageWBAdjust.u8CurGOffset;
					NormalColorTemp.u16BlueOffset    	=	structPageWBAdjust.u8CurBOffset;

				}
				else if(eCurColorType == MSrv_Picture::MS_COLOR_TEMP_COOL)
				{
					CoolColorTemp.u16RedGain    	=   	structPageWBAdjust.u8CurRGain;
					CoolColorTemp.u16GreenGain 	=	structPageWBAdjust.u8CurGGain;
					CoolColorTemp.u16BlueGain    	=  	structPageWBAdjust.u8CurBGain;
					CoolColorTemp.u16RedOffset    	=	structPageWBAdjust.u8CurROffset;
					CoolColorTemp.u16GreenOffset	= 	structPageWBAdjust.u8CurGOffset;
					CoolColorTemp.u16BlueOffset    	=	structPageWBAdjust.u8CurBOffset;

				}
				else
				{
					// should not be go here.
					// somethine wrong happen, do nothing
				}

				flag = true;
#if UART_PROTOCOL_DEBUG
				printf("__________0X68__structPageWBAdjust.u8CurGOffset = %d\n",structPageWBAdjust.u8CurGOffset);
#endif

				putcharbnum(ReplyData,ReplyNum);
			}
			break;
		case 0x31:  // set Blue offset
			{
				counttemp++;
				structPageWBAdjust.u8CurBOffset = data*2;
				mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA tmpColorTemp;
				tmpColorTemp.u16RedGain =         structPageWBAdjust.u8CurRGain;
				tmpColorTemp.u16GreenGain =     structPageWBAdjust.u8CurGGain;
				tmpColorTemp.u16BlueGain =         structPageWBAdjust.u8CurBGain;
				tmpColorTemp.u16RedOffset =     structPageWBAdjust.u8CurROffset;
				tmpColorTemp.u16GreenOffset =     structPageWBAdjust.u8CurGOffset;
				tmpColorTemp.u16BlueOffset =     structPageWBAdjust.u8CurBOffset;
				//    putcharbnum(g_FactoryUartPutCommandA,Factory_UARTPutCmd_Num);
				mapi_interface::Get_mapi_pql(MAPI_MAIN_WINDOW)->SetColorTemperatureEx(&tmpColorTemp);
				pMSrvFactory->as_SetWBGainOffsetEx(eColorTemp, structPageWBAdjust.u8CurRGain, structPageWBAdjust.u8CurGGain, structPageWBAdjust.u8CurBGain, structPageWBAdjust.u8CurROffset, structPageWBAdjust.u8CurGOffset, structPageWBAdjust.u8CurBOffset,enSrcType);
				if(eCurColorType == MSrv_Picture::MS_COLOR_TEMP_WARM )
				{

					WarmColorTemp.u16RedGain    		=      structPageWBAdjust.u8CurRGain;
					WarmColorTemp.u16GreenGain 		=	structPageWBAdjust.u8CurGGain;
					WarmColorTemp.u16BlueGain    	 	=  	structPageWBAdjust.u8CurBGain;
					WarmColorTemp.u16RedOffset    	=	structPageWBAdjust.u8CurROffset;
					WarmColorTemp.u16GreenOffset		= 	structPageWBAdjust.u8CurGOffset;
					WarmColorTemp.u16BlueOffset    	=	structPageWBAdjust.u8CurBOffset;
				}
				else if(eCurColorType == MSrv_Picture::MS_COLOR_TEMP_NATURE )
				{
					NormalColorTemp.u16RedGain    	=     structPageWBAdjust.u8CurRGain;
					NormalColorTemp.u16GreenGain 	=	structPageWBAdjust.u8CurGGain;
					NormalColorTemp.u16BlueGain    	=  	structPageWBAdjust.u8CurBGain;
					NormalColorTemp.u16RedOffset    	=	structPageWBAdjust.u8CurROffset;
					NormalColorTemp.u16GreenOffset	= 	structPageWBAdjust.u8CurGOffset;
					NormalColorTemp.u16BlueOffset    	=	structPageWBAdjust.u8CurBOffset;

				}
				else if(eCurColorType == MSrv_Picture::MS_COLOR_TEMP_COOL)
				{
					CoolColorTemp.u16RedGain    	=      structPageWBAdjust.u8CurRGain;
					CoolColorTemp.u16GreenGain 	=	structPageWBAdjust.u8CurGGain;
					CoolColorTemp.u16BlueGain    	=  	structPageWBAdjust.u8CurBGain;
					CoolColorTemp.u16RedOffset    	=	structPageWBAdjust.u8CurROffset;
					CoolColorTemp.u16GreenOffset	= 	structPageWBAdjust.u8CurGOffset;
					CoolColorTemp.u16BlueOffset    	=	structPageWBAdjust.u8CurBOffset;

				}
				else
				{
					// should not be go here.
					// somethine wrong happen, do nothing
				}

				flag = true;
				printf("__________0X69__structPageWBAdjust.u8CurBOffset = %d\n",structPageWBAdjust.u8CurBOffset);

				putcharbnum(ReplyData,ReplyNum);

			}
			break;

		case 0x32:    // save WB data
			{
				switch(data)
				{
					case 0x01:  // save ATV WB data
						{
							pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_WARM , WarmColorTemp.u16RedGain , WarmColorTemp.u16GreenGain, WarmColorTemp.u16BlueGain, WarmColorTemp.u16RedOffset, WarmColorTemp.u16GreenOffset, WarmColorTemp.u16BlueOffset,MAPI_INPUT_SOURCE_ATV);
							pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_NATURE , NormalColorTemp.u16RedGain, NormalColorTemp.u16GreenGain, NormalColorTemp.u16BlueGain, NormalColorTemp.u16RedOffset, NormalColorTemp.u16GreenOffset, NormalColorTemp.u16BlueOffset,MAPI_INPUT_SOURCE_ATV);
							pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_COOL, CoolColorTemp.u16RedGain, CoolColorTemp.u16GreenGain, CoolColorTemp.u16BlueGain, CoolColorTemp.u16RedOffset, CoolColorTemp.u16GreenOffset, CoolColorTemp.u16BlueOffset,MAPI_INPUT_SOURCE_ATV);

						}
						break;
					case 0x02:  // save DTV WB data
						{
							pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_WARM , WarmColorTemp.u16RedGain , WarmColorTemp.u16GreenGain, WarmColorTemp.u16BlueGain, WarmColorTemp.u16RedOffset, WarmColorTemp.u16GreenOffset, WarmColorTemp.u16BlueOffset,MAPI_INPUT_SOURCE_DTV);
							pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_NATURE , NormalColorTemp.u16RedGain, NormalColorTemp.u16GreenGain, NormalColorTemp.u16BlueGain, NormalColorTemp.u16RedOffset, NormalColorTemp.u16GreenOffset, NormalColorTemp.u16BlueOffset,MAPI_INPUT_SOURCE_DTV);
							pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_COOL, CoolColorTemp.u16RedGain, CoolColorTemp.u16GreenGain, CoolColorTemp.u16BlueGain, CoolColorTemp.u16RedOffset, CoolColorTemp.u16GreenOffset, CoolColorTemp.u16BlueOffset,MAPI_INPUT_SOURCE_DTV);

						}
						break;
					case 0x03:  // save AV WB data
						{
							pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_WARM , WarmColorTemp.u16RedGain , WarmColorTemp.u16GreenGain, WarmColorTemp.u16BlueGain, WarmColorTemp.u16RedOffset, WarmColorTemp.u16GreenOffset, WarmColorTemp.u16BlueOffset,MAPI_INPUT_SOURCE_CVBS);
							pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_NATURE , NormalColorTemp.u16RedGain, NormalColorTemp.u16GreenGain, NormalColorTemp.u16BlueGain, NormalColorTemp.u16RedOffset, NormalColorTemp.u16GreenOffset, NormalColorTemp.u16BlueOffset,MAPI_INPUT_SOURCE_CVBS);
							pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_COOL, CoolColorTemp.u16RedGain, CoolColorTemp.u16GreenGain, CoolColorTemp.u16BlueGain, CoolColorTemp.u16RedOffset, CoolColorTemp.u16GreenOffset, CoolColorTemp.u16BlueOffset,MAPI_INPUT_SOURCE_CVBS);


						}
						break;
					case 0x04:  // save YPbPr WB data
						{
							pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_WARM , WarmColorTemp.u16RedGain , WarmColorTemp.u16GreenGain, WarmColorTemp.u16BlueGain, WarmColorTemp.u16RedOffset, WarmColorTemp.u16GreenOffset, WarmColorTemp.u16BlueOffset,MAPI_INPUT_SOURCE_YPBPR);
							pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_NATURE , NormalColorTemp.u16RedGain, NormalColorTemp.u16GreenGain, NormalColorTemp.u16BlueGain, NormalColorTemp.u16RedOffset, NormalColorTemp.u16GreenOffset, NormalColorTemp.u16BlueOffset,MAPI_INPUT_SOURCE_YPBPR);
							pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_COOL, CoolColorTemp.u16RedGain, CoolColorTemp.u16GreenGain, CoolColorTemp.u16BlueGain, CoolColorTemp.u16RedOffset, CoolColorTemp.u16GreenOffset, CoolColorTemp.u16BlueOffset,MAPI_INPUT_SOURCE_YPBPR);


						}
						break;
					case 0x05:  // save PC WB data
						{
							pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_WARM , WarmColorTemp.u16RedGain , WarmColorTemp.u16GreenGain, WarmColorTemp.u16BlueGain, WarmColorTemp.u16RedOffset, WarmColorTemp.u16GreenOffset, WarmColorTemp.u16BlueOffset,MAPI_INPUT_SOURCE_VGA);
							pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_NATURE , NormalColorTemp.u16RedGain, NormalColorTemp.u16GreenGain, NormalColorTemp.u16BlueGain, NormalColorTemp.u16RedOffset, NormalColorTemp.u16GreenOffset, NormalColorTemp.u16BlueOffset,MAPI_INPUT_SOURCE_VGA);
							pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_COOL, CoolColorTemp.u16RedGain, CoolColorTemp.u16GreenGain, CoolColorTemp.u16BlueGain, CoolColorTemp.u16RedOffset, CoolColorTemp.u16GreenOffset, CoolColorTemp.u16BlueOffset,MAPI_INPUT_SOURCE_VGA);


						}

						break;
					case 0x06:  // save HDMI WB data
						{
							pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_WARM , WarmColorTemp.u16RedGain , WarmColorTemp.u16GreenGain, WarmColorTemp.u16BlueGain, WarmColorTemp.u16RedOffset, WarmColorTemp.u16GreenOffset, WarmColorTemp.u16BlueOffset,MAPI_INPUT_SOURCE_HDMI);
							pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_NATURE , NormalColorTemp.u16RedGain, NormalColorTemp.u16GreenGain, NormalColorTemp.u16BlueGain, NormalColorTemp.u16RedOffset, NormalColorTemp.u16GreenOffset, NormalColorTemp.u16BlueOffset,MAPI_INPUT_SOURCE_HDMI);
							pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_COOL, CoolColorTemp.u16RedGain, CoolColorTemp.u16GreenGain, CoolColorTemp.u16BlueGain, CoolColorTemp.u16RedOffset, CoolColorTemp.u16GreenOffset, CoolColorTemp.u16BlueOffset,MAPI_INPUT_SOURCE_HDMI);

							pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_WARM , WarmColorTemp.u16RedGain , WarmColorTemp.u16GreenGain, WarmColorTemp.u16BlueGain, WarmColorTemp.u16RedOffset, WarmColorTemp.u16GreenOffset, WarmColorTemp.u16BlueOffset,MAPI_INPUT_SOURCE_HDMI2);
							pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_NATURE , NormalColorTemp.u16RedGain, NormalColorTemp.u16GreenGain, NormalColorTemp.u16BlueGain, NormalColorTemp.u16RedOffset, NormalColorTemp.u16GreenOffset, NormalColorTemp.u16BlueOffset,MAPI_INPUT_SOURCE_HDMI2);
							pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_COOL, CoolColorTemp.u16RedGain, CoolColorTemp.u16GreenGain, CoolColorTemp.u16BlueGain, CoolColorTemp.u16RedOffset, CoolColorTemp.u16GreenOffset, CoolColorTemp.u16BlueOffset,MAPI_INPUT_SOURCE_HDMI2);

							pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_WARM , WarmColorTemp.u16RedGain , WarmColorTemp.u16GreenGain, WarmColorTemp.u16BlueGain, WarmColorTemp.u16RedOffset, WarmColorTemp.u16GreenOffset, WarmColorTemp.u16BlueOffset,MAPI_INPUT_SOURCE_HDMI3);
							pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_NATURE , NormalColorTemp.u16RedGain, NormalColorTemp.u16GreenGain, NormalColorTemp.u16BlueGain, NormalColorTemp.u16RedOffset, NormalColorTemp.u16GreenOffset, NormalColorTemp.u16BlueOffset,MAPI_INPUT_SOURCE_HDMI3);
							pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_COOL, CoolColorTemp.u16RedGain, CoolColorTemp.u16GreenGain, CoolColorTemp.u16BlueGain, CoolColorTemp.u16RedOffset, CoolColorTemp.u16GreenOffset, CoolColorTemp.u16BlueOffset,MAPI_INPUT_SOURCE_HDMI3);

						}
						break;
					case 0x07:  // save USB WB data
						{
							pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_WARM , WarmColorTemp.u16RedGain , WarmColorTemp.u16GreenGain, WarmColorTemp.u16BlueGain, WarmColorTemp.u16RedOffset, WarmColorTemp.u16GreenOffset, WarmColorTemp.u16BlueOffset,MAPI_INPUT_SOURCE_STORAGE);
							pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_NATURE , NormalColorTemp.u16RedGain, NormalColorTemp.u16GreenGain, NormalColorTemp.u16BlueGain, NormalColorTemp.u16RedOffset, NormalColorTemp.u16GreenOffset, NormalColorTemp.u16BlueOffset,MAPI_INPUT_SOURCE_STORAGE);
							pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_COOL, CoolColorTemp.u16RedGain, CoolColorTemp.u16GreenGain, CoolColorTemp.u16BlueGain, CoolColorTemp.u16RedOffset, CoolColorTemp.u16GreenOffset, CoolColorTemp.u16BlueOffset,MAPI_INPUT_SOURCE_STORAGE);

						}
						break;

					case 0x08:  // save alll WB data
						{
							for(int i = MS_INPUT_SOURCE_TYPE_VGA;  i < MS_INPUT_SOURCE_TYPE_OTHERS;  i++)
							{


								if(i ==MS_INPUT_SOURCE_TYPE_NONE || i == MS_INPUT_SOURCE_TYPE_SVIDEO || i == MS_INPUT_SOURCE_TYPE_SCART)
								{
									continue;
								}
								MAPI_INPUT_SOURCE_TYPE enSrcType = TransMsInputSoutrceTypeMapiInputSourceTo((EN_MS_INPUT_SOURCE_TYPE)i);

								pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_WARM , WarmColorTemp.u16RedGain , WarmColorTemp.u16GreenGain, WarmColorTemp.u16BlueGain, WarmColorTemp.u16RedOffset, WarmColorTemp.u16GreenOffset, WarmColorTemp.u16BlueOffset,enSrcType);
								pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_NATURE , NormalColorTemp.u16RedGain, NormalColorTemp.u16GreenGain, NormalColorTemp.u16BlueGain, NormalColorTemp.u16RedOffset, NormalColorTemp.u16GreenOffset, NormalColorTemp.u16BlueOffset,enSrcType);
								pMSrvFactory->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_COOL, CoolColorTemp.u16RedGain, CoolColorTemp.u16GreenGain, CoolColorTemp.u16BlueGain, CoolColorTemp.u16RedOffset, CoolColorTemp.u16GreenOffset, CoolColorTemp.u16BlueOffset,enSrcType);

                                // Save WB to Environment
                                pMSrvFactory->EosSetWB(WarmColorTemp, NormalColorTemp, CoolColorTemp);
							}
						}
						break;

					default:
						{
							break;
						}
				}
				
		              putcharbnum(ReplyData,ReplyNum);

				// exit WB adjust after save data
				// u8FactoryUARTEnable = FALSE;
				// _bExitUartDebug = TRUE;
			}
			break;

		default:
			printf("MSrv_UartDebug.cpp   setADC_AGC_ADJ  default 949");
			return false;
			break;
			printf("MSrv_UartDebug.cpp   setADC_AGC_ADJ  958"); 	
	}

	

	return flag;
}

// EosTek Patch Begin
//ashton: for wb adjust
MAPI_INPUT_SOURCE_TYPE MSrv_UartDebug::TransMsInputSoutrceTypeMapiInputSourceTo(EN_MS_INPUT_SOURCE_TYPE enMsInputSourceType)
{
	switch(enMsInputSourceType)
	{		
		case MS_INPUT_SOURCE_TYPE_VGA:   ///<VGA input
			return MAPI_INPUT_SOURCE_VGA;
			break;
		case MS_INPUT_SOURCE_TYPE_ATV:       ///<ATV input
			return MAPI_INPUT_SOURCE_ATV; 
			break;
		case MS_INPUT_SOURCE_TYPE_CVBS:         ///<AV
			return MAPI_INPUT_SOURCE_CVBS;
			break;
		case MS_INPUT_SOURCE_TYPE_SVIDEO:       ///<S-video
			return MAPI_INPUT_SOURCE_SVIDEO;
			break;
		case MS_INPUT_SOURCE_TYPE_YPBPR:      ///<Component 1
			return MAPI_INPUT_SOURCE_YPBPR;
			break;
		case MS_INPUT_SOURCE_TYPE_SCART:        ///<Scart
			return MAPI_INPUT_SOURCE_SCART;
			break;
		case MS_INPUT_SOURCE_TYPE_HDMI:        ///<HDMI
			return MAPI_INPUT_SOURCE_HDMI;
			break;
		case MS_INPUT_SOURCE_TYPE_DTV:       ///<DTV  <DTV2
			return MAPI_INPUT_SOURCE_DTV;
			break;
		case MS_INPUT_SOURCE_TYPE_OTHERS:          ///<DVI    <Storage    <KTV     <Storage2
			//case MS_INPUT_SOURCE_TYPE_MAX :
		case MS_INPUT_SOURCE_TYPE_NUM:      ///<number of the source
		default:
			return MAPI_INPUT_SOURCE_NONE; 

	}
}



//====================star===========//20150420
void MSrv_UartDebug::FactoryUart_InputCommandAction(void)
{
	U8 Command,Data[2];
	int  uniondata = 0x00;

	if (u8FactoryUARTEnable == FALSE)//recv over???? check the length
	{
		return;
	}	

	Command = recv_buffer[1];
	Data[0] = recv_buffer[2];
	Data[1] = recv_buffer[3];

	
	memset(recv_buffer, 0 , 1024);


	MS_USER_SYSTEM_SETTING stGetSystemSetting;
	MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->GetUserSystemSetting(&stGetSystemSetting);
	//T_MS_VIDEO tmpVideo;            
	MAPI_INPUT_SOURCE_TYPE enSrcType = MSrv_Control::GetInstance()->GetCurrentInputSource();
	switch (Command)
	{

		case 0x23:  //change source
			{
				//MS_USER_SYSTEM_SETTING stGetSystemSetting;
				//MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->GetUserSystemSetting(&stGetSystemSetting);
				//MAPI_INPUT_SOURCE_TYPE enSrcType = MSrv_Control::GetInstance()->GetCurrentInputSource();
				// MSrv_Control::GetInstance()->SetInputSource(stGetSystemSetting.enInputSourceType);
				switch(Data[0])
				{
					case 01: //ATV
						if(enSrcType == MAPI_INPUT_SOURCE_ATV)
						{break;}
						else
						{
							stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_ATV;
							MSrv_Control::GetInstance()->SetInputSource(stGetSystemSetting.enInputSourceType);
							break;
						}
					case 02: //DTV
						if(enSrcType == MAPI_INPUT_SOURCE_DTV)
						{break;}
						else
						{
							stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_DTV;
							MSrv_Control::GetInstance()->SetInputSource(stGetSystemSetting.enInputSourceType);
							break;
						}
					case 03: //AV1,AV2
						if(Data[1] == 0x01) //AV1
						{
							if(enSrcType == MAPI_INPUT_SOURCE_CVBS)
							{break;}
							else
							{
								stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_CVBS;
								MSrv_Control::GetInstance()->SetInputSource(stGetSystemSetting.enInputSourceType);
								break;
							}
						}
						else if(Data[1] == 0x02) //AV2
						{
							if(enSrcType == MAPI_INPUT_SOURCE_CVBS2)
							{break;}
							else
							{
								stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_CVBS2;
								MSrv_Control::GetInstance()->SetInputSource(stGetSystemSetting.enInputSourceType);
								break;
							}
						}
						else
						{
							break;
						}
					case 04: //Component 1,2
						if(Data[1] == 0x01) //Component 1
						{
							if(enSrcType == MAPI_INPUT_SOURCE_YPBPR)
							{break;}
							else
							{
								stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_YPBPR;
								MSrv_Control::GetInstance()->SetInputSource(stGetSystemSetting.enInputSourceType);
								break;
							}
						}
						else if(Data[1] == 0x02) //Component 2
						{
							if(enSrcType == MAPI_INPUT_SOURCE_YPBPR2)
							{break;}
							else
							{
								stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_YPBPR2;
								MSrv_Control::GetInstance()->SetInputSource(stGetSystemSetting.enInputSourceType);
								break;
							}
						}
						else
						{
							break;
						}
					case 05: //PC
						if(enSrcType == MAPI_INPUT_SOURCE_VGA)
						{break;}
						else
						{
							stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_VGA;
							MSrv_Control::GetInstance()->SetInputSource(stGetSystemSetting.enInputSourceType);
							break;
						}
					case 06: //HDMI1,2,3,4,5
						if(Data[1] == 0x01) //HDMI 1
						{
							if(enSrcType == MAPI_INPUT_SOURCE_HDMI)
							{break;}
							else
							{
								stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_HDMI;
								MSrv_Control::GetInstance()->SetInputSource(stGetSystemSetting.enInputSourceType);
								break;
							}
						}
						else if(Data[1] == 0x02) //HDMI 2
						{
							if(enSrcType == MAPI_INPUT_SOURCE_HDMI2)
							{break;}
							else
							{
								stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_HDMI2;
								MSrv_Control::GetInstance()->SetInputSource(stGetSystemSetting.enInputSourceType);
								break;
							}
						}
						else if(Data[1] == 0x03) //HDMI 3
						{
							if(enSrcType == MAPI_INPUT_SOURCE_HDMI3)
							{break;}
							else
							{
								stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_HDMI3;
								MSrv_Control::GetInstance()->SetInputSource(stGetSystemSetting.enInputSourceType);
								break;
							}
						}

						else if(Data[1] == 0x04) //HDMI 4
						{
							if(enSrcType == MAPI_INPUT_SOURCE_HDMI4)
							{break;}
							else
							{
								stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_HDMI4;
								MSrv_Control::GetInstance()->SetInputSource(stGetSystemSetting.enInputSourceType);
								break;
							}
						}

						/*	else if(Data[1] == 0x05) //HDMI 5
							{
							stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_HDMI2;
							MSrv_Control::GetInstance()->SetInputSource(stGetSystemSetting.enInputSourceType);
							break;
							}*/
						else
						{
							break;
						}

					case 07: //USB
						if(enSrcType == MAPI_INPUT_SOURCE_STORAGE)
						{break;}
						else
						{
							stGetSystemSetting.enInputSourceType = MAPI_INPUT_SOURCE_STORAGE;
							MSrv_Control::GetInstance()->SetInputSource(stGetSystemSetting.enInputSourceType);
							break;
						}
					default:
						break;

				}
			}
			putcharbnum(ReplyData,ReplyNum);	    		
			break;

		case 0x24:   //chang ColorTemp
			{
				if ((Data[0]==0x01) ||(Data[0]==0x02) ||(Data[0]==0x03) )
				{
					if (setADC_AGC_ADJ(Data[0], Command))
						return;
				}
				else
				{

					return;
				}
			}
			break;
		case 0x25:   //change PictureMode
			if ((Data[0]==0x01) ||(Data[0]==0x02) ||(Data[0]==0x03) ||(Data[0]==0x04))
			{
				if (setpicture(Data[0], Command))
					return;
			} 
			else
			{
				return;
			}
			break;
		case 0x26:    //change OSD brightness,contrast or restore to OSD default
			if ((Data[0]==0x01) ||(Data[0]==0x02) ||(Data[0]==0x03) )
			{
				if (setpicture(Data[0], Command))
					return;
			}
			else
			{
				return;
			}

			break;

		case 0x27:     //AutoADC
			if ((Data[0]==0x01) ||(Data[0]==0x02))
			{
				if (setADC_AGC_ADJ(Data[0], Command))
					return;
			}
			else
			{
				return;
			}
			break;
		case 0x28:    // Adjust   R,G,B Gain value
			{

				if(((Data[0]==0x01) ||(Data[0]==0x02) ||(Data[0]==0x03)) && ((Data[1]>=0x00) && (Data[1]<=0xFF)))
				{
					uniondata = uniondata | Data[0];
					uniondata = uniondata << 8;
					uniondata = uniondata | Data[1];
					if (setADC_AGC_ADJ(uniondata, Command))
					{ 
						uniondata = 0x00;
						return;
					}
				}
				else
				{ 
					uniondata = 0x00;
					return;
				}
			}
			break;
		case 0x29:    //Adjust R_offset
		case 0x30:   //Adjust G_offset
		case 0x31:    //Adjust B_offset
			{
				//uniondata = 0x00;
				uniondata = uniondata | Data[0];
				uniondata = uniondata << 8;
				uniondata = uniondata | Data[1];
#if 0
				//write(input_fd,"\n",1);	
				int i;
				for (i = 0; i< 4;i++)
				{
					char tmp[10],tmp1[10];
					tmp[0] = char(uniondata & 0xFF);
					tmp[1] = char((uniondata & 0xFF00)>>8);
					sprintf(tmp1," 0x%x",tmp[i]);
					write(input_fd,tmp1,strlen(tmp1));	
				}
				//write(input_fd,"\n",1);
#endif

				if (setADC_AGC_ADJ(uniondata, Command))
				{ 
					uniondata = 0x00;
					return;
				}
				else
				{ 
					uniondata = 0x00;
					return;
				}
			}
			break;
		case 0x32:     //store WB value
			if (setADC_AGC_ADJ(Data[0], Command))
			{
				return;
			}
			else
			{return;}
			break;
		case 0x33:     //restore to factory default
			putcharbnum(ReplyData,ReplyNum);
			break;
		case 0x34:      //Adjust BackLight
			{
				if (setpicture(Data[0], Command))
				{
					return;
				}
				else
				{return;}
			}
			break;

		default:
			break;
	}

	return;
}
//====================end=============//


void MSrv_UartDebug::Uart_Reset(void)
{
	// reset state machine
	_u8ReceivedBytes = 0;

	_bIsExtMode = FALSE;
	// _bIsExtModeFac232 = FALSE;
	_u8CmdLen = 0;
	memset(_cmdLine, 0, UART_CMDLINE_SIZE);

	_bStartSession = FALSE;
}


MAPI_BOOL MSrv_UartDebug::asGetUartSate(void)
{
	return as_bIsUartStar;
}

void MSrv_UartDebug::SaveCurrentTempColor(void)
{
	if(eCurColorType == MSrv_Picture::MS_COLOR_TEMP_WARM )
	{
		WarmColorTemp.u16RedGain    	=   structPageWBAdjust.u8CurRGain;
		WarmColorTemp.u16GreenGain 		=	structPageWBAdjust.u8CurGGain;
		WarmColorTemp.u16BlueGain    	=  	structPageWBAdjust.u8CurBGain;  
		WarmColorTemp.u16RedOffset    	=	structPageWBAdjust.u8CurROffset;
		WarmColorTemp.u16GreenOffset	= 	structPageWBAdjust.u8CurGOffset; 
		WarmColorTemp.u16BlueOffset    	=	structPageWBAdjust.u8CurBOffset;									
	}
	else if(eCurColorType == MSrv_Picture::MS_COLOR_TEMP_NATURE )
	{
		NormalColorTemp.u16RedGain    	=   structPageWBAdjust.u8CurRGain;
		NormalColorTemp.u16GreenGain 	=	structPageWBAdjust.u8CurGGain;
		NormalColorTemp.u16BlueGain    	=  	structPageWBAdjust.u8CurBGain;  
		NormalColorTemp.u16RedOffset    =	structPageWBAdjust.u8CurROffset;
		NormalColorTemp.u16GreenOffset	= 	structPageWBAdjust.u8CurGOffset; 
		NormalColorTemp.u16BlueOffset   =	structPageWBAdjust.u8CurBOffset;	

	}
	else if(eCurColorType == MSrv_Picture::MS_COLOR_TEMP_COOL)
	{
		CoolColorTemp.u16RedGain    	=   structPageWBAdjust.u8CurRGain;
		CoolColorTemp.u16GreenGain 		=	structPageWBAdjust.u8CurGGain;
		CoolColorTemp.u16BlueGain    	=  	structPageWBAdjust.u8CurBGain;  
		CoolColorTemp.u16RedOffset    	=	structPageWBAdjust.u8CurROffset;
		CoolColorTemp.u16GreenOffset	= 	structPageWBAdjust.u8CurGOffset; 
		CoolColorTemp.u16BlueOffset    	=	structPageWBAdjust.u8CurBOffset;		

	}
	else
	{
		// should not be go here.
		// somethine wrong happen, do nothing
	}
}


void MSrv_UartDebug::Uart_RecvHandler(void)
{		
	MAPI_INPUT_SOURCE_TYPE enSrcType = MSrv_Control::GetInstance()->GetCurrentInputSource();
	MSrv_Picture::EN_MS_COLOR_TEMP eColorTemp;
	if( countflsgs == 0 )
	{
		eColorTemp = MSrv_Picture::MS_COLOR_TEMP_NATURE;
		MSrv_Control::GetMSrvFactoryMode()->GetWBGainOffsetEx(eColorTemp,
					&structPageWBAdjust.u8CurRGain, &structPageWBAdjust.u8CurGGain, &structPageWBAdjust.u8CurBGain,
					&structPageWBAdjust.u8CurROffset, &structPageWBAdjust.u8CurGOffset, &structPageWBAdjust.u8CurBOffset,
					enSrcType);			
	}						
	switch(m_UartCommand.Buffer[_UART_CMD_INDEX2_])  
	{
		case 0x0D:   // do nothing
			{
			///putcharbnum(SKYWORTHACK_EXC,SKYWORTH_ACKNUM);		
		}
		break;
		case 0x63: // set colour temperature
			{
				
				switch (m_UartCommand.Buffer[_UART_CMD_INDEX3_])
				{
					case 0x00:							
						eCurColorType=MSrv_Picture::MS_COLOR_TEMP_NATURE;
						break;
					case 0x01:
						eCurColorType=MSrv_Picture::MS_COLOR_TEMP_WARM;
						break;
					case 0x02:
						eCurColorType=MSrv_Picture::MS_COLOR_TEMP_COOL;
						break;
					default:
						putcharbnum(SKYWORTHACK_ERR_UNEXC,SKYWORTH_ACKNUM);
						return;
				}
				MSrv_Control::GetMSrvFactoryMode()->GetWBGainOffsetEx(eColorTemp,
						&structPageWBAdjust_1.u8CurRGain, &structPageWBAdjust_1.u8CurGGain, &structPageWBAdjust_1.u8CurBGain,
						&structPageWBAdjust_1.u8CurROffset, &structPageWBAdjust_1.u8CurGOffset, &structPageWBAdjust_1.u8CurBOffset,
						enSrcType);

				MSrv_Control::GetMSrvFactoryMode()->SetWBGainOffsetEx(eCurColorType, structPageWBAdjust_1.u8CurRGain, structPageWBAdjust_1.u8CurGGain, structPageWBAdjust_1.u8CurBGain, structPageWBAdjust_1.u8CurROffset, structPageWBAdjust_1.u8CurGOffset, structPageWBAdjust_1.u8CurBOffset,enSrcType);
					  									
			}
			break;
		case 0x40: // set picture mode
			{					
				T_MS_VIDEO stVedioTemp;
				MSrv_Control::GetMSrvSystemDatabase()->GetVideo(&stVedioTemp, &enSrcType);
				switch (m_UartCommand.Buffer[_UART_CMD_INDEX3_])
					{
						case 0x00:
							stVedioTemp.ePicture=PICTURE_NORMAL;								
							break;
						case 0x01:
							stVedioTemp.ePicture=PICTURE_DYNAMIC;//PICTURE_DYNAMIC;
							break;
						case 0x02:
							stVedioTemp.ePicture=PICTURE_MOVIE;
							break;
						case 0x03:
							stVedioTemp.ePicture=PICTURE_USER;
							break;
						default:
							break;
					}
					MSrv_Control::GetMSrvSystemDatabase()->SetVideo(&stVedioTemp, &enSrcType);
					MSrv_Control::GetMSrvPicture()->SetPictureModeBrightness(stVedioTemp.astPicture[stVedioTemp.ePicture].u8Brightness);
					MSrv_Control::GetMSrvPicture()->SetPictureModeContrast(stVedioTemp.astPicture[stVedioTemp.ePicture].u8Contrast);
					MSrv_Control::GetMSrvPicture()->SetPictureModeColor(stVedioTemp.astPicture[stVedioTemp.ePicture].u8Saturation);
					MSrv_Control::GetMSrvPicture()->SetPictureModeSharpness(stVedioTemp.astPicture[stVedioTemp.ePicture].u8Sharpness);
					MSrv_Control::GetMSrvPicture()->SetPictureModeTint(stVedioTemp.astPicture[stVedioTemp.ePicture].u8Hue);
					MSrv_Control::GetMSrvPicture()->SetBacklight(stVedioTemp.astPicture[stVedioTemp.ePicture].u8Backlight);				
			}
			break;
		case 0x64:   // set Rgain
			{
				countflsgs++;
				structPageWBAdjust.u8CurRGain = NU255toU2047((U16)m_UartCommand.Buffer[_UART_CMD_INDEX4_] , 0, 2047); 
				mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA tmpColorTemp;
				tmpColorTemp.u16RedGain 	=    structPageWBAdjust.u8CurRGain;
				tmpColorTemp.u16GreenGain 	=    structPageWBAdjust.u8CurGGain;
				tmpColorTemp.u16BlueGain 	=    structPageWBAdjust.u8CurBGain;
				tmpColorTemp.u16RedOffset 	=    structPageWBAdjust.u8CurROffset;
				tmpColorTemp.u16GreenOffset =  	 structPageWBAdjust.u8CurGOffset;
				tmpColorTemp.u16BlueOffset 	=    structPageWBAdjust.u8CurBOffset;
				mapi_interface::Get_mapi_pql(MAPI_MAIN_WINDOW)->SetColorTemperatureEx(&tmpColorTemp);					
				SaveCurrentTempColor();
			}
			break;
		case 0x65:   // set Ggain
			{
				countflsgs++;	
				structPageWBAdjust.u8CurGGain = NU255toU2047((U16)m_UartCommand.Buffer[_UART_CMD_INDEX4_] , 0, 2047); 
				mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA tmpColorTemp;
				tmpColorTemp.u16RedGain	 	=     structPageWBAdjust.u8CurRGain;
				tmpColorTemp.u16GreenGain 	=     structPageWBAdjust.u8CurGGain;
				tmpColorTemp.u16BlueGain 	=     structPageWBAdjust.u8CurBGain;
				tmpColorTemp.u16RedOffset 	=     structPageWBAdjust.u8CurROffset;
				tmpColorTemp.u16GreenOffset =     structPageWBAdjust.u8CurGOffset;
				tmpColorTemp.u16BlueOffset 	=     structPageWBAdjust.u8CurBOffset;
				mapi_interface::Get_mapi_pql(MAPI_MAIN_WINDOW)->SetColorTemperatureEx(&tmpColorTemp);					
				SaveCurrentTempColor();					                                    					
			}
			break;
		case 0x66:  // set Bgain
			{
				countflsgs++;	
				structPageWBAdjust.u8CurBGain = NU255toU2047((U16)m_UartCommand.Buffer[_UART_CMD_INDEX4_] , 0, 2047); 
				mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA tmpColorTemp;
				tmpColorTemp.u16RedGain 	=     structPageWBAdjust.u8CurRGain;
				tmpColorTemp.u16GreenGain 	=     structPageWBAdjust.u8CurGGain;
				tmpColorTemp.u16BlueGain 	=     structPageWBAdjust.u8CurBGain;
				tmpColorTemp.u16RedOffset 	=     structPageWBAdjust.u8CurROffset;
				tmpColorTemp.u16GreenOffset =     structPageWBAdjust.u8CurGOffset;
				tmpColorTemp.u16BlueOffset 	=     structPageWBAdjust.u8CurBOffset;
				mapi_interface::Get_mapi_pql(MAPI_MAIN_WINDOW)->SetColorTemperatureEx(&tmpColorTemp);					
				SaveCurrentTempColor();					
			}
			break;
		case 0x6A:  // save all colour
			{					
				for(int i = MS_INPUT_SOURCE_TYPE_VGA;  i < MS_INPUT_SOURCE_TYPE_OTHERS;  i++)
				{

					
					if(i ==MS_INPUT_SOURCE_TYPE_NONE || i == MS_INPUT_SOURCE_TYPE_SVIDEO || i == MS_INPUT_SOURCE_TYPE_SCART)
					{
						continue;	
					}
					MAPI_INPUT_SOURCE_TYPE enSrcType = TransMsInputSoutrceTypeMapiInputSourceTo((EN_MS_INPUT_SOURCE_TYPE)i);				 
					
					MSrv_Control::GetMSrvFactoryMode()->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_WARM , WarmColorTemp.u16RedGain , WarmColorTemp.u16GreenGain, WarmColorTemp.u16BlueGain, WarmColorTemp.u16RedOffset, WarmColorTemp.u16GreenOffset, WarmColorTemp.u16BlueOffset,enSrcType);
					MSrv_Control::GetMSrvFactoryMode()->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_NATURE , NormalColorTemp.u16RedGain, NormalColorTemp.u16GreenGain, NormalColorTemp.u16BlueGain, NormalColorTemp.u16RedOffset, NormalColorTemp.u16GreenOffset, NormalColorTemp.u16BlueOffset,enSrcType);				
					MSrv_Control::GetMSrvFactoryMode()->as_SetWBGainOffsetEx(MSrv_Picture::MS_COLOR_TEMP_COOL, CoolColorTemp.u16RedGain, CoolColorTemp.u16GreenGain, CoolColorTemp.u16BlueGain, CoolColorTemp.u16RedOffset, CoolColorTemp.u16GreenOffset, CoolColorTemp.u16BlueOffset,enSrcType);

				}											
			}
			break;

		default:	
			putcharbnum(SKYWORTHACK_ERR_UNEXC,SKYWORTH_ACKNUM);
			Uart_Reset();
			return;

	}
	
	putcharbnum(SKYWORTHACK_EXC,SKYWORTH_ACKNUM);

	
	return;
}

/////////////////auto white add end ///////////////////////

void MSrv_UartDebug::CusUartDebug_Start(void)
{
#define SERIAL1_IN_DEVICE    "/dev/ttyS1"
#define SET_IO_SPEED 4098 //baudrate = 115200

	//
	//switch UART_SELECT_MUX from PIU_UART1 to UART0
	//
	struct termios new_options, old_options;
	fd_set r_fds;
	struct timeval timeout;

	//int input_fd = -1;
	int ret;
	
	//unsigned char k;
	memset(recv_buffer, 0 , 1024);
	/* get key, ctrl+c to exit */
	// Open UART for read
	//
	// Open /dev/ttyS1
	//

	Uart_Reset();	
	u8FactoryUARTEnable = FALSE;   //20150420
	_u32SessionStartTime = MsOS_GetSystemTime();
	input_fd = open(SERIAL1_IN_DEVICE, O_RDWR | O_NOCTTY | O_NDELAY );
	if(input_fd == -1)
	{
		printf("error open %s\n", SERIAL1_IN_DEVICE);
		return;
	}

	output_fp = fopen(SERIAL1_IN_DEVICE, "w");

	if(output_fp == NULL)
	{
		printf("error open %s\n", SERIAL1_IN_DEVICE);
		close(input_fd);
		return;
	}
	mapi_uartdebug* uartDebug = mapi_uartdebug::GetInstance();
	if (uartDebug != NULL)
	{
		uartDebug->InitUart(EN_UART_PIU_UART1, 115200);
		uartDebug->SwitchUart(EN_UART_PORT0,EN_UART_PIU_UART1);
	}
	_bExitUartDebug = FALSE;
	setvbuf(output_fp, (char *)NULL, _IONBF, 0);

	//
	// Set new setting to ttyS1
	//
	fcntl(input_fd, F_SETFL, FNDELAY);
	tcgetattr(input_fd, &old_options);
	// config ttyS0 not do local ECHO, turn off flow control
	tcgetattr(input_fd, &new_options);
	new_options.c_lflag &= ~(ICANON | ECHO | ECHOE | ECHOK | ECHONL | ECHOCTL | ECHOPRT | ECHOKE | ISIG);
	new_options.c_iflag  &= ~(INPCK | INLCR | ICRNL | IUCLC | IXON | IXOFF);
	new_options.c_oflag  &= ~OPOST;   // raw output
	speed_t setiospeed=SET_IO_SPEED;
	cfsetispeed(&new_options, setiospeed);
	cfsetospeed(&new_options, setiospeed);
	tcsetattr(input_fd, TCSANOW, &new_options);


	write(input_fd,"cus uart debug test loop start\n",32);

	//
	// Read data and Write data
	//

       as_bIsUartStar = TRUE; 
	
	while(!_bExitUartDebug)
	{			
		//set Time Out
		timeout.tv_sec = 0;
		timeout.tv_usec = 10 * 1000; // 10 ms
		FD_ZERO(&r_fds);
		FD_SET(input_fd, &r_fds);
		// select polling UART, if no data recieve Linux will set Thread sleep. TimeOut time is 10ms
		ret = ::select(input_fd + 1, &r_fds, NULL, NULL, &timeout);

		if(ret == 0)
		{
			continue;
		}
		else if(ret > 0 && FD_ISSET(input_fd, &r_fds))//else if(ret > 0)
		{
			/*Here is demo code for read/write serial data,please Add Customer
			  code here for special use case*/
			// data recieved m_u8CmdLine					
			ret = read(input_fd, &_cmdLine[_u8ReceivedBytes], CMD_LINE_SIZE_WB);
			if(ret <= 0)
			{
				continue;
			}

			_u8ReceivedBytes += ret;
		
#if  UART_PROTOCOL_DEBUG
					{
						MAPI_U8 i;
						char cmd[32]={0};
						for(i = 0; i < ret; i++)
						{
							//printf("_cmdLine[%d]=%x\n", _u8ReceivedBytes - ret + i, _cmdLine[_u8ReceivedBytes-ret+i]);
							snprintf(cmd, 32, "ret = %d, _cmdLine[%d]=%x\n", ret, _u8ReceivedBytes - ret + i, _cmdLine[_u8ReceivedBytes-ret+i]);
							write(input_fd,cmd,strlen(cmd));
						}
					}
#endif

			if(_bStartSession == FALSE)
			{
				_bStartSession = TRUE;
				_u32SessionStartTime = MsOS_GetSystemTime();

			}
		}

		if(_cmdLine[0]==0x5A)
		{
			_u8CmdLen = _cmdLine[1];
		}
		else if(_cmdLine[0]==0x96)
		{
			_u8CmdLen = 4;
		}
		else
		{		
			_u8CmdLen = 0;
		}

		//ashton msg: for debug exit loop 
		if(_cmdLine[0]==0xff && _cmdLine[1] == 0xff)
		{
		    _bExitUartDebug =  TRUE;			
		    continue;
		}			
		
		if((_cmdLine[0]==0x5A)&&(_u8ReceivedBytes ==_u8CmdLen))
		{	
			memcpy(m_UartCommand.Buffer,_cmdLine,_u8ReceivedBytes);
			Uart_RecvHandler();
			_u8ReceivedBytes = 0;
			memset(_cmdLine,0,sizeof(_cmdLine));
		}
		else if((_cmdLine[0]==0x96)&&(_u8ReceivedBytes ==_u8CmdLen))
		{	
			if((_cmdLine[1] == 0x00) && (_cmdLine[2] == 0x01) && (_cmdLine[3] == 0x02))
			{
				putcharbnum(ReplyData,ReplyNum);
				u8FactoryUARTEnable = TRUE;
				_u8ReceivedBytes = 0;
				memset(_cmdLine,0,sizeof(_cmdLine));
				continue;			
			}			
			else  if( u8FactoryUARTEnable == TRUE && _cmdLine[1] != 0x00)
			{
	   
                memcpy(recv_buffer,_cmdLine,_u8ReceivedBytes);
				FactoryUart_InputCommandAction();	
				_u8ReceivedBytes = 0;
				memset(_cmdLine,0,sizeof(_cmdLine));							
			}			
		}
		else
		{
			Uart_Reset();
		}
		
	}

	// Restore new setting to ttyS1
	tcsetattr(input_fd, TCSANOW, &old_options);
	close(input_fd);
	input_fd = -1;

	setvbuf(output_fp, (char *)NULL, _IOLBF, 0);    // change it to line buffered
	fclose(output_fp);
	output_fp = NULL;

	//switch PIU_UART0 to UART0
	uartDebug->SwitchUart(EN_UART_PORT0,EN_UART_PIU_UART0);

       // EosTek Patch Begin
      //ashton: for wb adjust
       as_bIsUartStar = FALSE; 
      // EosTek Patch End
	
	printf("Cus Uart1 Debug Exit!\n");
}

void MSrv_UartDebug::asDebugMsg(char msg[], U8 size)
{
     write(input_fd,msg, size);   
}


// EosTek Patch End


