/*@ <FileComment ID=1246257763274> @*/
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
* File  : device_audio_amp_MSH9010
**********************************************************************/
/*@ </FileComment ID=1246257763274> @*/

/*@ <Include> @*/
#include "debug.h"
#include "device_audio_amp_MSH9010.h"
#include "mapi_i2c_devTable.h"

#include "mapi_i2c.h"
#include "mapi_interface.h"
#include "drvGPIO.h"
#include "mapi_gpio.h"
#include "mapi_gpio_devTable.h"
#if (KARAOKE_ENABLE==1)
#include"MSrv_Control_common.h"
#include <unistd.h>
#endif
//-------------------------------------------------------------------------------------------------
/// Constructor
/// @param  None
/// @return None
//-------------------------------------------------------------------------------------------------
device_audio_amp_MSH9010::device_audio_amp_MSH9010(void)
{
    return;
}

//-------------------------------------------------------------------------------------------------
/// De-constructor
/// @param  None
/// @return None
//-------------------------------------------------------------------------------------------------
device_audio_amp_MSH9010::~device_audio_amp_MSH9010(void)
{
    return;
}

//-------------------------------------------------------------------------------------------------
/// Init this Audio Amplifier
/// @param None
/// @return             \b OUT: MAPI_TRUE or MAPI_FALSE
//-------------------------------------------------------------------------------------------------
MAPI_BOOL device_audio_amp_MSH9010::Init(void)
{
    mapi_gpio* gptr = mapi_gpio::GetGPIO_Dev(Audio_Amplifier);
    if(gptr != NULL)
    {
        gptr->SetOn();
    }

    return MAPI_TRUE;
}

//-------------------------------------------------------------------------------------------------
/// Finalize this Audio Amplifier
/// @param None
/// @return             \b OUT: MAPI_TRUE or MAPI_FALSE
//-------------------------------------------------------------------------------------------------
MAPI_BOOL device_audio_amp_MSH9010::Finalize(void)
{
    mapi_gpio* gptr = mapi_gpio::GetGPIO_Dev(Audio_Amplifier);
    if(gptr != NULL)
    {
        gptr->SetOff();
    }
    return MAPI_TRUE;
}



//-------------------------------------------------------------------------------------------------
/// Mute this Audio Amplifier
/// @param bMute		\b IN: TRUE for Mute; FALSE for unMute
/// @return 			\b OUT: MAPI_TRUE or MAPI_FALSE
//-------------------------------------------------------------------------------------------------
MAPI_BOOL device_audio_amp_MSH9010::Mute(MAPI_BOOL bMute)
{
    mapi_gpio *gptr = mapi_gpio::GetGPIO_Dev(MUTE);
    if(gptr != NULL)
    {
      if (bMute == MAPI_TRUE)
      {
          gptr->SetOn();
      }
      else
      {
          gptr->SetOff();
      }
    }
    return MAPI_TRUE;
}
#if (KARAOKE_ENABLE == 1)
//这是M-62429的音量表格
/*
const unsigned char   TAB_M62429_VOL[41]={
0x00,0x00,0x03,0x21,0x62,0x10,0x13,0x51,0x32,0x70,0x73,
0x09,0x4a,0x28,0x2b,0x69,0x1a,0x58,0x5b,0x7a,0x7a,
0x7b,0x06,0x07,0x46,0x47,0x26,0x25,0x27,0x64,0x66,
0x65,0x67,0x14,0x16,0x15,0x17,0x54,0x56,0x55,0x57,
};
*/
const unsigned char   TAB_M62429_VOL[21] =
{
    0x00, 0x1a, 0x58, 0x5b, 0x7a, 0x7b, 0x06, 0x07, 0x46,
    0x47, 0x26, 0x25, 0x27, 0x64, 0x65, 0x67, 0x14, 0x16,
    0x15, 0x17, 0x17,
};


MAPI_U8 device_audio_amp_MSH9010::Write_M62429(MAPI_U8 data , MAPI_U8 sum)
{
    MAPI_U8 send_data;
    MAPI_U8 i;
    send_data = data;
    for (i = 0; i < sum; i++)
    {
        MSrv_Control_common::SetGpioDeviceStatus(M62429_DATA, 0);
        usleep(2);
        MSrv_Control_common::SetGpioDeviceStatus(M62429_CLK, 0);
        usleep(2);
        if (send_data & 0x80)			// falling edge comes on the next pass
        {
            MSrv_Control_common::SetGpioDeviceStatus(M62429_DATA, 1);  //Set SDA = output mode and pull high
        }
        else
        {
            MSrv_Control_common::SetGpioDeviceStatus(M62429_DATA, 0);  //Set SDA = output mode and pull high
        }
        usleep(2);
        MSrv_Control_common::SetGpioDeviceStatus(M62429_CLK, 1); //Set SCL = output mode and pull high
        usleep(2);
        send_data = send_data << 1;
    }

    return MAPI_TRUE;
}



void device_audio_amp_MSH9010::MicVol_Set(MAPI_U8 VolValue)
{
    MAPI_U8 Val;

    if(VolValue > 100)
    {
        VolValue = 100;
    }

    VolValue = 20 * VolValue / 100;
    Val = TAB_M62429_VOL[VolValue];
    //printf("--*****--YYYYYYYYY=%x-****-.\n",Val);
    Val = Val << 1; //声音控制位为7位，TAB中数据为8位，故要做此处理。

    Write_M62429(0xC0, 2); //11** ****先写入01,1通道加减音量
    Write_M62429(Val, 7);   //写入volume
    Write_M62429(0xff, 2); //写入11
    MSrv_Control_common::SetGpioDeviceStatus(M62429_DATA, 1);
    usleep(2);
    MSrv_Control_common::SetGpioDeviceStatus(M62429_CLK, 0);
    usleep(2);
    MSrv_Control_common::SetGpioDeviceStatus(M62429_DATA, 0);
    //printf("--*****--YYYYYYYYY--****-.\n");
}

void device_audio_amp_MSH9010::MicEcho_Set(MAPI_U8 EchoValue)
{
    MAPI_U8 Val;

    if(EchoValue > 40)
    {
        EchoValue = 40;
    }

    //EchoValue=40*EchoValue/100;
    Val = TAB_M62429_VOL[EchoValue];
    Val = Val << 1; //声音控制位为7位，TAB中数据为8位，故要做此处理。
    Write_M62429(0x40, 2); //11** ****先写入01,1通道加减音量
    Write_M62429(Val, 7);   //写入volume
    Write_M62429(0xff, 2); //写入11
    MSrv_Control_common::SetGpioDeviceStatus(M62429_DATA, 1);
    usleep(2);
    MSrv_Control_common::SetGpioDeviceStatus(M62429_CLK, 0);
    usleep(2);
    MSrv_Control_common::SetGpioDeviceStatus(M62429_DATA, 0);
}

#endif
