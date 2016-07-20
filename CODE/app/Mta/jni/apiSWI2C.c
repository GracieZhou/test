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
#define _API_SWI2C_C_

#include "MsCommon.h"
#include "MsVersion.h"
#include "apiSWI2C.h"
#include "drvGPIO.h"
#include "drvMMIO.h"
//#include "drvCPU.h"

//-------------------------------------------------------------------------------------------------
//  Local Defines
//-------------------------------------------------------------------------------------------------
#define __I2C_BUS(scl, sda, dly)    scl, sda, dly
#define I2C_BUS( bus )              __I2C_BUS( bus )
#define COUNTOF( array )            (sizeof(array) / sizeof((array)[0]))
#define IIC_BUS_MAX                 16

#define PULL_HIGH                   1
#define PULL_LOW                    0
#define BIT0                    0x0001
#define SWI2C_READ              0
#define SWI2C_WRITE             1
#define I2C_CHECK_PIN_DUMMY     100
#define I2C_ACKNOWLEDGE         PULL_LOW
#define I2C_NON_ACKNOWLEDGE     PULL_HIGH
#define I2C_ACCESS_DUMMY_TIME   3

#define HIBYTE(value)  ((MS_U8)((value) / 0x100))
#define LOBYTE(value)  ((MS_U8)(value))

static MS_S32 g_s32SWI2CMutex[IIC_BUS_MAX] = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
const char gu8SWI2CMutexName[IIC_BUS_MAX][13] = { \
    "SWI2CMTXBUS0","SWI2CMTXBUS1","SWI2CMTXBUS2","SWI2CMTXBUS3","SWI2CMTXBUS4","SWI2CMTXBUS5","SWI2CMTXBUS6","SWI2CMTXBUS7", \
    "SWI2CMTXBUS8","SWI2CMTXBUS9","SWI2CMTXBUSA","SWI2CMTXBUSB","SWI2CMTXBUSC","SWI2CMTXBUSD","SWI2CMTXBUSE","SWI2CMTXBUSF" };
#define IIC_MUTEX_CREATE(_P_)      g_s32SWI2CMutex[_P_] = MsOS_CreateMutex(E_MSOS_FIFO, (char*)gu8SWI2CMutexName[_P_] , MSOS_PROCESS_SHARED)
#define IIC_MUTEX_LOCK(_P_)        OS_OBTAIN_MUTEX(g_s32SWI2CMutex[_P_],MSOS_WAIT_FOREVER)
#define IIC_MUTEX_UNLOCK(_P_)      OS_RELEASE_MUTEX(g_s32SWI2CMutex[_P_])
#define IIC_MUTEX_DELETE(_P_)      OS_DELETE_MUTEX(g_s32SWI2CMutex[_P_])
//For ISP Programming
static MS_S32 g_s32I2CMutexS = -1;
#define IIC_MUTEX_S_CREATE()       g_s32I2CMutexS = MsOS_CreateMutex(E_MSOS_FIFO, (char*)"OS_SWI2C_Mutex", MSOS_PROCESS_SHARED)
#define IIC_MUTEX_S_LOCK()         OS_OBTAIN_MUTEX(g_s32I2CMutexS,MSOS_WAIT_FOREVER)
#define IIC_MUTEX_S_UNLOCK()       OS_RELEASE_MUTEX(g_s32I2CMutexS)
#define IIC_MUTEX_S_DELETE()       OS_DELETE_MUTEX(g_s32I2CMutexS)

//-------------------------------------------------------------------------------------------------
//  Local Structures
//-------------------------------------------------------------------------------------------------


//-------------------------------------------------------------------------------------------------
//  Global Variables
//-------------------------------------------------------------------------------------------------


//-------------------------------------------------------------------------------------------------
//  Local Variables
//-------------------------------------------------------------------------------------------------
static SWI2C_DbgLvl _gSWI2CDbgLevel = E_SWI2C_DBGLVL_ERROR;
static MSIF_Version _api_swi2c_version = {
    .DDI = { SWI2C_API_VERSION },
};

static SWI2C_BusCfg g_I2CBus[IIC_BUS_MAX];
static MS_U8 u8BusAllocNum = 0;
static MS_U8 u8BusSel = 0;
static SWI2C_ReadMode g_I2CReadMode[IIC_BUS_MAX];

static MS_U32 u32DelayCount[IIC_BUS_MAX];
static MS_U32 u32FactorDelay = 50400UL;
static MS_U32 u32FactorAdjust = 11040UL;
static MS_U32 u32ParamBase1 = 130UL;
static MS_U32 u32Parameter1 = 130UL;
static MS_U32 u32Parameter2 = 440UL;
static MS_U32 u32CpuSpeedMHz;
static MS_U32 u32AdjParam;
#define DELAY_CNT(SpeedKHz)  ((u32FactorDelay/(SpeedKHz))-((u32Parameter1+u32AdjParam)-((SpeedKHz)/u32AdjParam))+((1<<((u32Parameter2-SpeedKHz)/40))))

//-------------------------------------------------------------------------------------------------
//  Debug Functions
//-------------------------------------------------------------------------------------------------
#define SWI2C_DBG_FUNC()               //if (_gSWI2CDbgLevel >= E_SWI2C_DBGLVL_ALL) \
                                        //{MS_DEBUG_MSG(printf("\t====   %s   ====\n", __FUNCTION__));}
#define SWI2C_DBG_INFO(x, args...)     //if (_gSWI2CDbgLevel >= E_SWI2C_DBGLVL_INFO ) \
                                        //{MS_DEBUG_MSG(printf("[%s]: ", __FUNCTION__); printf(x, ##args));}
#define SWI2C_DBG_ERR(x, args...)      //if (_gSWI2CDbgLevel >= E_SWI2C_DBGLVL_ERROR) \
                                        //{MS_DEBUG_MSG(printf("[%s]: ", __FUNCTION__); printf(x, ##args);)}
#define SWI2C_DBG_WARN(x, args...)     //if (_gSWI2CDbgLevel >= E_SWI2C_DBGLVL_WARNING) \
                                        //{MS_DEBUG_MSG(printf("[%s]: ", __FUNCTION__); printf(x, ##args);)}

//-------------------------------------------------------------------------------------------------
//  Local Functions
//-------------------------------------------------------------------------------------------------
static void iic_delay(MS_U8 u8BusNum)
{
    MS_U32 volatile u32Loop=u32DelayCount[u8BusNum];
    
    while(u32Loop--)
    {
        #ifdef __mips__
        __asm__ __volatile__ ("nop");
        #endif
        
        #ifdef __AEONR2__
        __asm__ __volatile__ ("l.nop");
        #endif

        #ifdef __arm__
        __asm__ __volatile__ ("mov r0, r0");
        #endif
    }
}

static void pin_scl_set_input(MS_U8 u8BusNum)
{
    mdrv_gpio_set_input( g_I2CBus[u8BusNum].padSCL );
}

static void pin_scl_set_high(MS_U8 u8BusNum)
{
     mdrv_gpio_set_input( g_I2CBus[u8BusNum].padSCL );
}

static void pin_scl_set_low(MS_U8 u8BusNum)
{
    mdrv_gpio_set_low( g_I2CBus[u8BusNum].padSCL );
}

static int pin_scl_get_level(MS_U8 u8BusNum)
{
    return mdrv_gpio_get_level( g_I2CBus[u8BusNum].padSCL ) ? PULL_HIGH : PULL_LOW;
}

static void pin_scl_check_high(MS_U8 u8BusNum)
{
    MS_U8 u8Dummy;

    pin_scl_set_high(u8BusNum);
    u8Dummy = I2C_CHECK_PIN_DUMMY;
    while (u8Dummy--)
    {
        if(pin_scl_get_level(u8BusNum) == PULL_HIGH)
            break;
    }
}

static void pin_sda_set_input(MS_U8 u8BusNum)
{
    mdrv_gpio_set_input( g_I2CBus[u8BusNum].padSDA );
}

static void pin_sda_set_high(MS_U8 u8BusNum)
{
    mdrv_gpio_set_input( g_I2CBus[u8BusNum].padSDA );
}

static void pin_sda_set_low(MS_U8 u8BusNum)
{
    mdrv_gpio_set_low( g_I2CBus[u8BusNum].padSDA );
}

static int pin_sda_get_level(MS_U8 u8BusNum)
{
    return mdrv_gpio_get_level( g_I2CBus[u8BusNum].padSDA ) ? PULL_HIGH : PULL_LOW;
}

static void pin_sda_check_high(MS_U8 u8BusNum)
{
    MS_U8 u8Dummy;

    pin_sda_set_high(u8BusNum);
    u8Dummy = I2C_CHECK_PIN_DUMMY;
    while (u8Dummy--)
    {
        if(pin_sda_get_level(u8BusNum) == PULL_HIGH)
            break;
    }
}

static void pin_scl_check_low(MS_U8 u8BusNum)
{
    MS_U16 u16Dummy = I2C_CHECK_PIN_DUMMY;

    pin_scl_set_low(u8BusNum);
    while (u16Dummy--)
    {
        if(pin_scl_get_level(u8BusNum) == PULL_LOW)
            break;
    }
}

/******************************************************************************/
////////////////////////////////////////////////////////////////////////////////
// I2C start signal.
// <comment>
//  SCL ________
//              \_________
//  SDA _____
//           \____________
//
// Return value: None
////////////////////////////////////////////////////////////////////////////////
/******************************************************************************/
static MS_BOOL IIC_Start(MS_U8 u8BusNum)
{
    MS_BOOL bStatus = TRUE;    // success status

    pin_sda_check_high(u8BusNum);
    iic_delay(u8BusNum);

    pin_scl_check_high(u8BusNum);
    iic_delay(u8BusNum);

    // check pin error
    pin_scl_set_input(u8BusNum);
    pin_sda_set_input(u8BusNum);

    if ((pin_scl_get_level(u8BusNum) == PULL_LOW) || (pin_sda_get_level(u8BusNum) == PULL_LOW))
    {
        pin_scl_set_high(u8BusNum);
        pin_sda_set_high(u8BusNum);
        bStatus = FALSE;
    }
    else // success
    {
        pin_sda_set_low(u8BusNum);
        iic_delay(u8BusNum);
        pin_scl_set_low(u8BusNum);
    }

    return bStatus;     //vain
}

////////////////////////////////////////////////////////////////////////////////
// I2C stop signal.
// <comment>
//              ____________
//  SCL _______/
//                 _________
//  SDA __________/
////////////////////////////////////////////////////////////////////////////////
static void IIC_Stop(MS_U8 u8BusNum)
{
    pin_scl_set_low(u8BusNum);
    iic_delay(u8BusNum);
    pin_sda_set_low(u8BusNum);

    iic_delay(u8BusNum);
    pin_scl_set_input(u8BusNum);
    iic_delay(u8BusNum);
    pin_sda_set_input(u8BusNum);
    iic_delay(u8BusNum);
}

/******************************************************************************/
///Send 1 bytes data
///@param u8dat \b IN: 1 byte data to send
/******************************************************************************/
static MS_BOOL SendByte(MS_U8 u8BusNum, MS_U8 u8dat)   // Be used int IIC_SendByte
{
    MS_U8    u8Mask = 0x80;
    int bAck; // acknowledge bit

    while ( u8Mask )
    {
        if (u8dat & u8Mask)
        {
            pin_sda_check_high(u8BusNum);
        }
        else
        {
            pin_sda_set_low(u8BusNum);
        }

        iic_delay(u8BusNum);
        pin_scl_check_high(u8BusNum);
        iic_delay(u8BusNum);
        pin_scl_set_low(u8BusNum);

        u8Mask >>= 1; // next
    }

    // recieve acknowledge
    pin_sda_set_input(u8BusNum);
    iic_delay(u8BusNum);
    pin_scl_check_high(u8BusNum);

    iic_delay(u8BusNum);
    bAck = pin_sda_get_level(u8BusNum); // recieve acknowlege
    pin_scl_set_low(u8BusNum);

    iic_delay(u8BusNum);

    //for I2c waveform sharp
    if (bAck)
        pin_sda_set_high(u8BusNum);
    else
        pin_sda_set_low(u8BusNum);

    pin_sda_set_input(u8BusNum);

    iic_delay(u8BusNum);
    iic_delay(u8BusNum);
    iic_delay(u8BusNum);
    iic_delay(u8BusNum);

    return (bAck)? TRUE: FALSE;
}

/******************************************************************************/
///Send 1 bytes data, this function will retry 5 times until success.
///@param u8dat \b IN: 1 byte data to send
///@return MS_BOOL:
///- TRUE: Success
///- FALSE: Fail
/******************************************************************************/
static MS_BOOL IIC_SendByte(MS_U8 u8BusNum, MS_U8 u8dat)
{
    MS_U8 i;

    for(i=0;i<5;i++)
    {
        if (SendByte(u8BusNum,u8dat)==I2C_ACKNOWLEDGE)
            return TRUE;
    }

    SWI2C_DBG_INFO("IIC[%d] write byte 0x%x fail!!\n",u8BusNum, u8dat);
    return FALSE;
}

////////////////////////////////////////////////////////////////////////////////
// I2C access start.
//
// Arguments: u8SlaveID - Slave ID (Address)
//            trans_t - I2C_TRANS_WRITE/I2C_TRANS_READ
////////////////////////////////////////////////////////////////////////////////
static MS_BOOL IIC_AccessStart(MS_U8 u8BusNum, MS_U8 u8SlaveID, MS_U8 trans_t)
{
    MS_U8 u8Dummy; // loop dummy

    SWI2C_DBG_FUNC();

    if (trans_t == SWI2C_READ) // check i2c read or write
    {
        u8SlaveID |= BIT0;
    }
    else
    {
        u8SlaveID &= ~BIT0;
    }

    u8Dummy = I2C_ACCESS_DUMMY_TIME;

    while (u8Dummy--)
    {
        if ( IIC_Start(u8BusNum) == FALSE)
        {
            continue;
        }

        if ( IIC_SendByte(u8BusNum,u8SlaveID) == TRUE )  // check acknowledge
        {
            return TRUE;
        }

        IIC_Stop(u8BusNum);
    }

    return FALSE;
}
/******************************************************************************/
///Get 1 bytes data, this function will retry 5 times until success.
///@param *u8dat \b IN: pointer to 1 byte data buffer for getting data
///@return MS_BOOL:
///- TRUE: Success
///- FALSE: Fail
/******************************************************************************///
//static MS_BOOL IIC_GetByte(MS_U8* pu8data)    // Auto generate ACK
static MS_U8 IIC_GetByte (MS_U8 u8BusNum, MS_U16  bAck)
{
    MS_U8 ucReceive = 0;
    MS_U8 ucMask = 0x80;

    pin_sda_set_input(u8BusNum);

    while ( ucMask )
    {
        iic_delay(u8BusNum);
        pin_scl_check_high(u8BusNum);
        iic_delay(u8BusNum);

        if (pin_sda_get_level(u8BusNum) == PULL_HIGH)
        {
            ucReceive |= ucMask;
        }
        ucMask >>= 1; // next

        pin_scl_set_low(u8BusNum);
        pin_scl_check_low(u8BusNum);
    }
    if (bAck)
    {
        pin_sda_set_low(u8BusNum);     // acknowledge
    }
    else
    {
        pin_sda_check_high(u8BusNum);  // non-acknowledge
    }
    iic_delay(u8BusNum);
    pin_scl_check_high(u8BusNum);
    iic_delay(u8BusNum);
    pin_scl_set_low(u8BusNum);
    iic_delay(u8BusNum);
    iic_delay(u8BusNum);
    iic_delay(u8BusNum);
    return ucReceive;
}

static void IIC_CfgSpeedParam(MS_U8 u8BusNum, MS_U32 u32Speed_K)
{
    //(1) assign primary parameters
    u32FactorDelay = u32CpuSpeedMHz * 100;
    u32FactorAdjust = (u32CpuSpeedMHz>=312)? 10000UL :13000UL;
    u32AdjParam = u32FactorAdjust/u32CpuSpeedMHz;
    u32Parameter2 = 440UL;
    //(2) assign base for parameter 1
    if(u32CpuSpeedMHz>=1000) u32ParamBase1 = 150UL;
    else if(u32CpuSpeedMHz>=900) u32ParamBase1 = 140UL;
    else if(u32CpuSpeedMHz>=780) u32ParamBase1 = 135UL;
    else if(u32CpuSpeedMHz>=720) u32ParamBase1 = 130UL;
    else if(u32CpuSpeedMHz>=650) u32ParamBase1 = 125UL;
    else if(u32CpuSpeedMHz>=600) u32ParamBase1 = 110UL;
    else if(u32CpuSpeedMHz>=560) u32ParamBase1 = 100UL;
    else if(u32CpuSpeedMHz>=530) u32ParamBase1 = 95UL;
    else if(u32CpuSpeedMHz>=500) u32ParamBase1 = 90UL;
    else if(u32CpuSpeedMHz>=480) u32ParamBase1 = 85UL;
    else if(u32CpuSpeedMHz>=430) u32ParamBase1 = 80UL;
    else if(u32CpuSpeedMHz>=400) u32ParamBase1 = 75UL;
    else if(u32CpuSpeedMHz>=384) u32ParamBase1 = 70UL;
    else if(u32CpuSpeedMHz>=360) u32ParamBase1 = 65UL;
    else if(u32CpuSpeedMHz>=336) u32ParamBase1 = 60UL;
    else if(u32CpuSpeedMHz>=312) u32ParamBase1 = 40UL;
    else if(u32CpuSpeedMHz>=240) u32ParamBase1 = 10UL;
    else if(u32CpuSpeedMHz>=216) u32ParamBase1 = 0UL;
    else u32ParamBase1 = 0UL;
    //(3) compute parameter 1 by base
    if(u32Speed_K>=350) u32Parameter1 = u32ParamBase1;  //400K level
    else if(u32Speed_K>=250) u32Parameter1 = u32ParamBase1 + 10; //300K evel
    else if(u32Speed_K>=150) u32Parameter1 = u32ParamBase1 + 60; //200K level
    else if(u32Speed_K>=75) u32Parameter1 = u32ParamBase1 + 250; //100K level //160
    else u32Parameter1 = u32ParamBase1 + 560; //50K level
    //(4) compute delay counts
    u32DelayCount[u8BusNum] = DELAY_CNT(u32Speed_K);

}

/******************************************************************************/
///I2C Initialize: set I2C Clock and enable I2C
/******************************************************************************/
static MS_BOOL IIC_ConfigBus(MS_U8 u8BusNum,SWI2C_BusCfg* pSWI2CBusCfg)
{
    SWI2C_DBG_FUNC();

    //check resources
    if((u8BusAllocNum>IIC_BUS_MAX)||(u8BusNum>=IIC_BUS_MAX))
        return FALSE;
    if(!pSWI2CBusCfg)
        return FALSE;
    //config IIC bus settings
    g_I2CBus[u8BusNum].padSCL = pSWI2CBusCfg->padSCL;
    g_I2CBus[u8BusNum].padSDA = pSWI2CBusCfg->padSDA;
    g_I2CBus[u8BusNum].defDelay = pSWI2CBusCfg->defDelay;
    u8BusAllocNum++;
    SWI2C_DBG_INFO("[IIC_ConfigBus]: g_I2CBus[%d].padSCL   = %d\n",u8BusNum, g_I2CBus[u8BusNum].padSCL);
    SWI2C_DBG_INFO("[IIC_ConfigBus]: g_I2CBus[%d].padSDA   = %d\n",u8BusNum, g_I2CBus[u8BusNum].padSDA);
    SWI2C_DBG_INFO("[IIC_ConfigBus]: g_I2CBus[%d].defDelay = %d\n",u8BusNum, g_I2CBus[u8BusNum].defDelay);
    SWI2C_DBG_INFO("[IIC_ConfigBus]: u8BusAllocNum = %d\n",u8BusAllocNum);
    return TRUE;
}

static void IIC_UseBus( MS_U8 u8BusNum )
{
    if ( u8BusNum < COUNTOF( g_I2CBus ) )
    {
        u8BusSel = u8BusNum;
        IIC_CfgSpeedParam(u8BusNum, g_I2CBus[u8BusNum].defDelay);
        SWI2C_DBG_INFO("[IIC_UseBus]: u8BusSel = %d\n",u8BusSel);
    }
}

static void IIC_UnuseBus( MS_U8 u8BusNum )
{
    u8BusNum = u8BusNum;
}

//-------------------------------------------------------------------------------------------------
//  Global Functions
//-------------------------------------------------------------------------------------------------
MS_U8 MApi_SWI2C_GetMaxBuses(void)
{
    SWI2C_DBG_FUNC();
    return (MS_U8)IIC_BUS_MAX;
}

MS_U32 MApi_SWI2C_Speed_Setting(MS_U8 u8BusNum, MS_U32 u32Speed_K)
{
    MS_U32 u32OriginalValue;

    SWI2C_DBG_FUNC();

    u32OriginalValue = g_I2CBus[u8BusNum].defDelay;
    g_I2CBus[u8BusNum].defDelay = u32Speed_K;
    IIC_UseBus(u8BusNum);
    SWI2C_DBG_INFO("[MApi_SWI2C_Speed_Setting]: u8BusNum = %d, u32Speed_K = %ld\n",u8BusNum,u32Speed_K);
    return u32OriginalValue;
}

MS_BOOL MApi_SWI2C_SetReadMode(SWI2C_ReadMode eReadMode)
{
    SWI2C_DBG_FUNC();

    if(eReadMode>=E_SWI2C_READ_MODE_MAX)
        return FALSE;
    g_I2CReadMode[u8BusSel] = eReadMode;
    return TRUE;
}

MS_BOOL MApi_SWI2C_SetBusReadMode(MS_U8 u8BusNum, SWI2C_ReadMode eReadMode)
{
    SWI2C_DBG_FUNC();

    if(eReadMode>=E_SWI2C_READ_MODE_MAX)
        return FALSE;
    g_I2CReadMode[u8BusNum] = eReadMode;
    return TRUE;
}

void MApi_SWI2C_Init(SWI2C_BusCfg SWI2CCBusCfg[],MS_U8 u8CfgBusNum)
{
    MS_U8 u8Bus;

    SWI2C_DBG_FUNC();

    u8BusAllocNum = 0;

    //Get CPU clock & delay parameters
    //MDrv_COPRO_GetBase();
    //u32CpuSpeedMHz = (MS_U32)(MDrv_CPU_QueryClock()/1000000UL);
	u32CpuSpeedMHz = 900UL;
    printf("@@@@@@ u32CpuSpeedMHz= %d MHz\n",(int)u32CpuSpeedMHz);

    //config iic buses
    for(u8Bus=0;u8Bus<u8CfgBusNum;u8Bus++)
    {
        IIC_ConfigBus(u8Bus,&SWI2CCBusCfg[u8Bus]);
        MApi_SWI2C_Speed_Setting(u8Bus,SWI2CCBusCfg[u8Bus].defDelay);
        MApi_SWI2C_SetBusReadMode(u8Bus,E_SWI2C_READ_MODE_DIRECTION_CHANGE);
        IIC_MUTEX_CREATE(u8Bus);
    }
    // create mutex to protect access specially for ISP Programming
    IIC_MUTEX_S_CREATE();

}

/******************************************************************************/
///Write bytes, be able to write 1 byte or several bytes to several register offsets in same slave address.
///@param u16BusNumSlaveID \b IN: Bus Number (high byte) and Slave ID (Address) (low byte)
///@param u8addrcount \b IN:  register NO to write, this parameter is the NO of register offsets in pu8addr buffer,
///it should be 0 when *pu8addr = NULL.
///@param *pu8addr \b IN: pointer to a buffer containing target register offsets to write
///@param u16size \b IN: Data length (in byte) to write
///@param *pu8data \b IN: pointer to the data buffer for write
///@return MS_BOOL:
///- TRUE: Success
///- FALSE: Fail
/******************************************************************************/
MS_BOOL MApi_SWI2C_WriteBytes(MS_U16 u16BusNumSlaveID, MS_U8 AddrCnt, MS_U8* pu8addr, MS_U16 u16size, MS_U8* pBuf)
{
    MS_U8 u8Dummy = 1; //I2C_ACCESS_DUMMY_TIME; // loop dummy
    MS_U8 u8BusNum = HIBYTE(u16BusNumSlaveID);
    MS_U8 u8SlaveID = LOBYTE(u16BusNumSlaveID);
    MS_BOOL bRet = FALSE;

    SWI2C_DBG_FUNC();

    if(u8BusAllocNum==0)
        return FALSE;

    IIC_MUTEX_LOCK(u8BusNum);
    IIC_UseBus(u8BusNum);

    while (u8Dummy--)
    {
        if (IIC_AccessStart(u8BusNum, u8SlaveID, SWI2C_WRITE) == FALSE)
        {
            if( u8Dummy )
                continue;
            else
                goto fail;
        }

        while (AddrCnt)
        {
            AddrCnt--;
            if (IIC_SendByte(u8BusNum,*pu8addr) == FALSE)
            {
                goto fail;
            }
            pu8addr++;
        }
        while (u16size) // loop of writting data
        {
            u16size-- ;
            if (IIC_SendByte(u8BusNum,*pBuf) == FALSE)
            {
                goto fail;
            }
            pBuf++; // next byte pointer
        }

        break;
    }
    bRet = TRUE;

fail:

    IIC_Stop(u8BusNum);
    IIC_UnuseBus(u8BusNum);
    IIC_MUTEX_UNLOCK(u8BusNum);

    return bRet;
}

 /******************************************************************************/
 ///Write bytes with Stop control
 ///@param u16BusNumSlaveID \b IN: Bus Number (high byte) and Slave ID (Address) (low byte)
 ///@param AddrCnt \b IN:  register NO to write, this parameter is the NO of register offsets in pu8addr buffer,
 ///it should be 0 when *pu8addr = NULL.
 ///@param *pu8addr \b IN: pointer to a buffer containing target register offsets to write
 ///@param u16size \b IN: Data length (in byte) to write
 ///@param *pu8data \b IN: pointer to the data buffer for write
 ///@param bGenStop \b IN: control stop to be generated by result
 ///@return MS_BOOL:
 ///- TRUE: Success
 ///- FALSE: Fail
 /******************************************************************************/
 MS_BOOL MApi_SWI2C_WriteBytesStop(MS_U16 u16BusNumSlaveID, MS_U8 AddrCnt, MS_U8* pu8addr, MS_U16 u16size, MS_U8* pBuf,MS_BOOL bGenStop)
 {
     MS_U8 u8Dummy = 1; //I2C_ACCESS_DUMMY_TIME; // loop dummy
     MS_U8 u8BusNum = HIBYTE(u16BusNumSlaveID);
     MS_U8 u8SlaveID = LOBYTE(u16BusNumSlaveID);
     MS_BOOL bRet = FALSE;
 
     SWI2C_DBG_FUNC();
 
     if(u8BusAllocNum==0)
         return FALSE;
 
     IIC_MUTEX_LOCK(u8BusNum);
     IIC_UseBus(u8BusNum);
 
     while (u8Dummy--)
     {
         if (IIC_AccessStart(u8BusNum, u8SlaveID, SWI2C_WRITE) == FALSE)
         {
             if( u8Dummy )
                 continue;
             else
                 goto fail;
         }
 
         while (AddrCnt)
         {
             AddrCnt--;
             if (IIC_SendByte(u8BusNum,*pu8addr) == FALSE)
             {
                 goto fail;
             }
             pu8addr++;
         }
         while (u16size) // loop of writting data
         {
             u16size-- ;
             if (IIC_SendByte(u8BusNum,*pBuf) == FALSE)
             {
                 goto fail;
             }
             pBuf++; // next byte pointer
         }
 
         break;
     }
     bRet = TRUE;
 
 fail:
 
     if (bRet==TRUE)
     {
         if (bGenStop == TRUE)
         {
             IIC_Stop(u8BusNum);
         }
     }
     else
     {
         IIC_Stop(u8BusNum);
     }
     IIC_UnuseBus(u8BusNum);
     IIC_MUTEX_UNLOCK(u8BusNum);
 
     return bRet;
 }


 /******************************************************************************/
///Read bytes, be able to read 1 byte or several bytes in thru mode, like ISDB demod TC90527
///@param u16BusNumSlaveID \b IN: Bus Number (high byte) and Slave ID (Address) (low byte)
///@param u8AddrNum \b IN:  register NO to read, this parameter is the NO of register offsets in pu8addr buffer,
///it should be 0 when *paddr = NULL.
///@param *paddr \b IN: pointer to a buffer containing target register offsets to read
///@param u16size \b IN: Data length (in byte) to read
///@param *pu8data \b IN: pointer to retun data buffer.
///@return MS_BOOL:
///- TRUE: Success
///- FALSE: Fail
/******************************************************************************/
MS_BOOL MApi_SWI2C_ReadBytes_ThruMode(MS_U16 u16BusNumSlaveID, MS_U8 ucSubAdr, MS_U8* paddr, MS_U16 ucBufLen, MS_U8* pBuf)
{
    MS_U8 u8Dummy = I2C_ACCESS_DUMMY_TIME; // loop dummy
    MS_U8 u8BusNum = HIBYTE(u16BusNumSlaveID);
    MS_U8 u8SlaveID = LOBYTE(u16BusNumSlaveID);
    MS_BOOL bRet = FALSE;
    MS_U8 *pAddr_bk = paddr;

    SWI2C_DBG_FUNC();

    if(u8BusAllocNum==0)
        return FALSE;

    IIC_MUTEX_LOCK(u8BusNum);
    IIC_UseBus(u8BusNum);

    while (u8Dummy--)
    {
        // step1
        if((g_I2CReadMode[u8BusNum]!=E_SWI2C_READ_MODE_DIRECT) && (ucSubAdr>0) && (paddr))
        {
            if (IIC_AccessStart(u8BusNum, u8SlaveID, SWI2C_WRITE) == FALSE)
            {
                if( u8Dummy )
                    continue;
                else
                    goto fail;
            }

            while (ucSubAdr)
            {
                ucSubAdr--;
                if (IIC_SendByte(u8BusNum,*paddr) == FALSE)
                {
                    goto fail;
                }
                paddr++;
            }

            if(g_I2CReadMode[u8BusNum]==E_SWI2C_READ_MODE_DIRECTION_CHANGE_STOP_START)
            {
                IIC_Stop(u8BusNum);
            }
        }
        
        //step 2
        if (IIC_AccessStart(u8BusNum, u8SlaveID, SWI2C_WRITE) == FALSE)
        {
            if( u8Dummy )
                continue;
            else
                goto fail;
        }
        if (IIC_SendByte(u8BusNum,pAddr_bk[0]) == FALSE)
        {
            goto fail;
        }
        if (IIC_SendByte(u8BusNum,pAddr_bk[1]+1) == FALSE)
        {
            goto fail;
        }
        
        //step3
        if (IIC_AccessStart(u8BusNum, u8SlaveID, SWI2C_READ) == FALSE)
        {
            if( u8Dummy )
                continue;
            else
                goto fail;
        }
        while (ucBufLen--) // loop to burst read
        {
            *pBuf = IIC_GetByte(u8BusNum,ucBufLen); // receive byte
            pBuf++; // next byte pointer
        }
        break;
    }
    bRet = TRUE;

fail:

    IIC_Stop(u8BusNum);
    IIC_UnuseBus(u8BusNum);
    IIC_MUTEX_UNLOCK(u8BusNum);

    return bRet;
}
 /******************************************************************************/
///Read bytes, be able to read 1 byte or several bytes from several register offsets in same slave address.
///@param u16BusNumSlaveID \b IN: Bus Number (high byte) and Slave ID (Address) (low byte)
///@param u8AddrNum \b IN:  register NO to read, this parameter is the NO of register offsets in pu8addr buffer,
///it should be 0 when *paddr = NULL.
///@param *paddr \b IN: pointer to a buffer containing target register offsets to read
///@param u16size \b IN: Data length (in byte) to read
///@param *pu8data \b IN: pointer to retun data buffer.
///@return MS_BOOL:
///- TRUE: Success
///- FALSE: Fail
/******************************************************************************/
MS_BOOL MApi_SWI2C_ReadBytes(MS_U16 u16BusNumSlaveID, MS_U8 ucSubAdr, MS_U8* paddr, MS_U16 ucBufLen, MS_U8* pBuf)
{
     MS_U8 u8Dummy = I2C_ACCESS_DUMMY_TIME; // loop dummy
     MS_U8 u8BusNum = HIBYTE(u16BusNumSlaveID);
     MS_U8 u8SlaveID = LOBYTE(u16BusNumSlaveID);
     MS_BOOL bRet = FALSE;

    SWI2C_DBG_FUNC();

    if(u8BusAllocNum==0)
        return FALSE;

    IIC_MUTEX_LOCK(u8BusNum);
    IIC_UseBus(u8BusNum);

    while (u8Dummy--)
    {
        if((g_I2CReadMode[u8BusNum]!=E_SWI2C_READ_MODE_DIRECT) && (ucSubAdr>0) && (paddr))
        {
            if (IIC_AccessStart(u8BusNum, u8SlaveID, SWI2C_WRITE) == FALSE)
            {
                if( u8Dummy )
                    continue;
                else
                    goto fail;
            }

            while (ucSubAdr)
            {
                ucSubAdr--;
                if (IIC_SendByte(u8BusNum,*paddr) == FALSE)
                {
                    goto fail;
                }
                paddr++;
            }

            if(g_I2CReadMode[u8BusNum]==E_SWI2C_READ_MODE_DIRECTION_CHANGE_STOP_START)
            {
                IIC_Stop(u8BusNum);
            }
        }

        if (IIC_AccessStart(u8BusNum, u8SlaveID, SWI2C_READ) == FALSE)
        {
            if( u8Dummy )
                continue;
            else
                goto fail;
        }

        while (ucBufLen--) // loop to burst read
        {
            *pBuf = IIC_GetByte(u8BusNum,ucBufLen); // receive byte
            pBuf++; // next byte pointer
        }

        break;
    }
    bRet = TRUE;

fail:

    IIC_Stop(u8BusNum);
    IIC_UnuseBus(u8BusNum);
    IIC_MUTEX_UNLOCK(u8BusNum);

    return bRet;
}

/******************************************************************************/
///Read 1 byte through IIC
///@param u16BusNumSlaveID \b IN: Bus Number (high byte) and Slave ID (Address) (low byte)
///@param u8RegAddr \b IN: Target register offset to read
///@param *pu8Data \b IN: pointer to 1 byte return data.
///@return MS_BOOL:
///- TRUE: Success
///- FALSE: Fail
/******************************************************************************/
MS_BOOL MApi_SWI2C_ReadByte(MS_U16 u16BusNumSlaveID, MS_U8 u8RegAddr, MS_U8 *pu8Data)
{
    MS_BOOL Result;

    SWI2C_DBG_FUNC();

    Result=MApi_SWI2C_ReadBytes(u16BusNumSlaveID, 1, &u8RegAddr,1, pu8Data);
    return Result;
}

/******************************************************************************/
///Write 1 byte through IIC
///@param u16BusNumSlaveID \b IN: Bus Number (high byte) and Slave ID (Address) (low byte)
///@param u8RegAddr \b IN: Target register offset to write
///@param u8Data \b IN: 1 byte data to write
///@return MS_BOOL:
///- TRUE: Success
///- FALSE: Fail
/******************************************************************************/
MS_BOOL MApi_SWI2C_WriteByte(MS_U16 u16BusNumSlaveID, MS_U8 u8RegAddr, MS_U8 u8Data)
{
    SWI2C_DBG_FUNC();

    return( MApi_SWI2C_WriteBytes(u16BusNumSlaveID, 1, &u8RegAddr, 1, &u8Data) );
}
//------------------------------------------------------------------------
MS_BOOL MApi_SWI2C_Write2Bytes(MS_U16 u16BusNumSlaveID, MS_U8 u8addr, MS_U16 u16data)
{
    MS_U8 u8Data[2];

    SWI2C_DBG_FUNC();

    u8Data[0] = (u16data>>8) & 0xFF;
    u8Data[1] = (u16data) & 0xFF;
    return (MApi_SWI2C_WriteBytes(u16BusNumSlaveID, 1, &u8addr, 2, u8Data));
}

MS_U16 MApi_SWI2C_Read2Bytes(MS_U16 u16BusNumSlaveID, MS_U8 u8addr)
{
    SWI2C_DBG_FUNC();

    MS_U8 u8Data[2]={0,0};
    MApi_SWI2C_ReadBytes(u16BusNumSlaveID, 1, &u8addr, 2, u8Data);
    return ( (((MS_U16)u8Data[0])<<8)|u8Data[1] );
}

MS_BOOL MApi_SWI2C_WriteByteDirectly(MS_U16 u16BusNumSlaveID, MS_U8 u8Data)
{
    MS_U8 u8BusNum = HIBYTE(u16BusNumSlaveID);
    MS_U8 u8SlaveID = LOBYTE(u16BusNumSlaveID);
    MS_BOOL bRet = FALSE;

    SWI2C_DBG_FUNC();

    if(u8BusAllocNum==0)
        return FALSE;

    IIC_MUTEX_LOCK(u8BusNum);
    IIC_UseBus(u8BusNum);

    IIC_Start(u8BusNum);
    if (IIC_SendByte(u8BusNum,(u8SlaveID&~BIT0)) == FALSE)
        goto fail;
    if (IIC_SendByte(u8BusNum,u8Data)==FALSE)
        goto fail;
    bRet = TRUE;
    
fail:

    IIC_Stop(u8BusNum);
    IIC_UnuseBus(u8BusNum);
    IIC_MUTEX_UNLOCK(u8BusNum);

    return bRet;
}


MS_BOOL MApi_SWI2C_Write4Bytes(MS_U16 u16BusNumSlaveID, MS_U32 u32Data, MS_U8 u8EndData)
{
    MS_U8 u8BusNum = HIBYTE(u16BusNumSlaveID);
    MS_U8 u8SlaveID = LOBYTE(u16BusNumSlaveID);
    MS_BOOL bRet = FALSE;

    SWI2C_DBG_FUNC();

    if(u8BusAllocNum==0)
        return FALSE;

    IIC_MUTEX_LOCK(u8BusNum);
    IIC_UseBus(u8BusNum);
    
    IIC_Start(u8BusNum);
    if(IIC_SendByte(u8BusNum,(u8SlaveID&~BIT0))==FALSE)
        goto fail;
    if(IIC_SendByte(u8BusNum,(MS_U8)(((MS_U32)u32Data)>>24) ) == FALSE )
        goto fail;
    if(IIC_SendByte(u8BusNum,(MS_U8)(((MS_U32)u32Data)>>16) ) == FALSE )
        goto fail;
    if(IIC_SendByte(u8BusNum,(MS_U8)(((MS_U32)u32Data)>>8) ) == FALSE )
        goto fail;
    if(IIC_SendByte(u8BusNum,(MS_U8)(((MS_U32)u32Data)>>0) ) == FALSE )
        goto fail;
    if(IIC_SendByte(u8BusNum,u8EndData)==FALSE)
        goto fail;
    bRet = TRUE;

fail:

    IIC_Stop(u8BusNum);
    IIC_UnuseBus(u8BusNum);
    IIC_MUTEX_UNLOCK(u8BusNum);

    return bRet;
}

MS_BOOL MApi_SWI2C_WriteGroupBytes(MS_U16 u16BusNumSlaveID, MS_U8 u8SubGroup, MS_U16 u16Addr, MS_U16 u16Data)
{
    MS_U8 u8BusNum = HIBYTE(u16BusNumSlaveID);
    MS_U8 u8SlaveID = LOBYTE(u16BusNumSlaveID);
    MS_BOOL bRet = FALSE;

    SWI2C_DBG_FUNC();

    if(u8BusAllocNum==0)
        return FALSE;

    IIC_MUTEX_LOCK(u8BusNum);
    IIC_UseBus(u8BusNum);
    
    IIC_Start(u8BusNum);
    if(IIC_SendByte(u8BusNum,(u8SlaveID&~BIT0))==FALSE)
        goto fail;
    if(IIC_SendByte(u8BusNum,u8SubGroup)==FALSE)
        goto fail;
    if(IIC_SendByte(u8BusNum,(u16Addr>>8)&0xFF)==FALSE)
        goto fail;
    if(IIC_SendByte(u8BusNum,u16Addr&0xFF)==FALSE)
        goto fail;
    if(IIC_SendByte(u8BusNum,(u16Data>>8)&0xFF)==FALSE)
        goto fail;
    if(IIC_SendByte(u8BusNum,u16Data&0xFF)==FALSE)
        goto fail;
    bRet = TRUE;
    
fail:

    IIC_Stop(u8BusNum);
    IIC_UnuseBus(u8BusNum);
    IIC_MUTEX_UNLOCK(u8BusNum);

    return bRet;
}

MS_U16 MApi_SWI2C_ReadGroupBytes(MS_U16 u16BusNumSlaveID, MS_U8 u8SubGroup, MS_U16 u16Addr)
{
    MS_U16 u16Data = 0; // Modified it by coverity_507
    MS_U8 u8Address[3];

    SWI2C_DBG_FUNC();

    u8Address[0] = u8SubGroup;
    u8Address[1] = (u16Addr>>8)&0xFF;
    u8Address[2] = u16Addr&0xFF;

    MApi_SWI2C_ReadBytes(u16BusNumSlaveID, 3, u8Address, 2, (MS_U8 *)&u16Data);

    return u16Data;
}

MS_BOOL MApi_SWI2C_GetLibVer(const MSIF_Version **ppVersion)
{
    SWI2C_DBG_FUNC();

    if (!ppVersion)
    {
        return FALSE;
    }

    *ppVersion = &_api_swi2c_version;
    return TRUE;
}

//-------------------------------------------------------------------------------------------------
/// Set SWI2C debug function level.
/// @param eLevel \b IN: E_SWI2C_DBGLVL_NONE/E_SWI2C_DBGLVL_WARNING/E_SWI2C_DBGLVL_ERROR/E_SWI2C_DBGLVL_INFO/E_SWI2C_DBGLVL_ALL
/// @return E_SWI2C_OK: Success
/// @return E_SWI2C_FAIL or other values: Failure
//-------------------------------------------------------------------------------------------------
SWI2C_Result MApi_SWI2C_SetDbgLevel(SWI2C_DbgLvl eLevel)
{
    SWI2C_DBG_INFO("Debug level: %u\n", eLevel);

    _gSWI2CDbgLevel = eLevel;
    return E_SWI2C_OK;
}

/******************************************************************************/
///
/// The following API functions are packed for special usage in ISP programming
///
/******************************************************************************/

////////////////////////////////////////////////////////////////////////////////
/// Packed I2C MUTEX Lock.
///
/// Arguments: 
/// @ None
/// return: 
/// @ None:
////////////////////////////////////////////////////////////////////////////////
void MApi_SWI2C_MutexLock(void)
{
    IIC_MUTEX_S_LOCK();
}

////////////////////////////////////////////////////////////////////////////////
/// Packed I2C MUTEX Unlock.
///
/// Arguments: 
/// @ None
/// return: 
/// @ None:
////////////////////////////////////////////////////////////////////////////////
void MApi_SWI2C_MutexUnlock(void)
{
    IIC_MUTEX_S_UNLOCK();
}

////////////////////////////////////////////////////////////////////////////////
/// Packed I2C Bus Use.
///
/// Arguments: 
/// @ None
/// return: 
/// @ None:
////////////////////////////////////////////////////////////////////////////////
void MApi_SWI2C_UseBus( MS_U8 u8BusChn )
{
    IIC_UseBus(u8BusChn);
}

////////////////////////////////////////////////////////////////////////////////
/// Packed I2C Bus Unuse.
///
/// Arguments: 
/// @ None
/// return: 
/// @ None:
////////////////////////////////////////////////////////////////////////////////
void MApi_SWI2C_UnuseBus(void)
{
     IIC_UnuseBus(u8BusSel);
}

////////////////////////////////////////////////////////////////////////////////
/// Packed I2C access start.
///
/// Arguments: 
/// @u8SlaveID - Slave ID (Address)
/// @trans_t - I2C_TRANS_WRITE/I2C_TRANS_READ
/// @return MS_BOOL:
/// - TRUE: Success
/// - FALSE: Fail
////////////////////////////////////////////////////////////////////////////////
MS_BOOL MApi_SWI2C_AccessStart(MS_U8 u8SlaveID, MS_U8 trans_t)
{
    MS_BOOL bRet;
    bRet = IIC_AccessStart(u8BusSel, u8SlaveID, trans_t);
    return bRet;
}

////////////////////////////////////////////////////////////////////////////////
/// Packed I2C access stop.
///
/// Arguments: 
/// @ None
/// return:
/// @ None
////////////////////////////////////////////////////////////////////////////////
void MApi_SWI2C_Stop(void)
{
    IIC_Stop(u8BusSel);
}

////////////////////////////////////////////////////////////////////////////////
/// Packed I2C access start.
///
/// Arguments: 
/// @ None
/// return:
/// @ MS_BOOL:
/// - TRUE: Success
/// - FALSE: Fail
////////////////////////////////////////////////////////////////////////////////
MS_BOOL MApi_SWI2C_Start(void)
{
    return IIC_Start(u8BusSel);
}

////////////////////////////////////////////////////////////////////////////////
/// Packed I2C send byte.
///
/// Arguments: 
/// @ u8Data : one-byte data for sending
/// @return MS_BOOL:
/// - TRUE: Success
/// - FALSE: Fail
////////////////////////////////////////////////////////////////////////////////
MS_BOOL MApi_SWI2C_SendByte(MS_U8 u8Data)
{
    MS_BOOL bRet;

    bRet = IIC_SendByte(u8BusSel, u8Data);
    return bRet;
}

////////////////////////////////////////////////////////////////////////////////
/// Packed I2C send byte.
///
/// Arguments: 
/// @ u16Ack : 
///     None 0 : get next byte and request next byte from slave device, 
///             0 : get next byte and inform of slave device not to send data anymore.  
/// @return MS_U8: 
///     return one-byte data from slave device 
////////////////////////////////////////////////////////////////////////////////
MS_U8 MApi_SWI2C_GetByte(MS_U16 u16Ack)
{
    MS_U8 u8Ret;

    u8Ret = IIC_GetByte(u8BusSel, u16Ack);
    return u8Ret;
}

#undef _API_SWI2C_C_

