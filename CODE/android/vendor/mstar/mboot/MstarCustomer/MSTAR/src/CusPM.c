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

//-------------------------------------------------------------------------------------------------
// Includes
//-------------------------------------------------------------------------------------------------
#include <command.h>
#include <common.h>
#include <ShareType.h>
#include <msAPI_Power.h>
#include <drvWDT.h>
#include <MsSystem.h>
#include <MsEnvironment.h>
#include <CusConfig.h>
#include <MsVfs.h>
#include <MsDebug.h>
#include <drvBDMA.h>
#include <MsApiPM.h>
#include <MsSysUtility.h>

#if (ENABLE_MSTAR_NUGGET==1)
#include <apiCEC.h>
#endif
#include <drvPM.h>
#include <MsUboot.h>
#include <MsMmap.h>
#include <MsDrvCache.h>
#if (ENABLE_MSTAR_NIKON==1) || (ENABLE_MSTAR_MILAN==1)
#ifndef  CUS_IR_HEAD_FILE//Please define it in board file for customization
#include "IR_MSTAR_DTV.h"
#else
#include CUS_IR_HEAD_FILE
#endif
#endif


//-------------------------------------------------------------------------------------------------
//  Debug Functions
//-------------------------------------------------------------------------------------------------

//-------------------------------------------------------------------------------------------------
//  Local Defines
//-------------------------------------------------------------------------------------------------

//-------------------------------------------------------------------------------------------------
//  Global Variables
//-------------------------------------------------------------------------------------------------

//-------------------------------------------------------------------------------------------------
//  Local Variables
//-------------------------------------------------------------------------------------------------

//-------------------------------------------------------------------------------------------------
//  Extern Functions
//-------------------------------------------------------------------------------------------------
extern int snprintf(char *str, size_t size, const char *fmt, ...);

//-------------------------------------------------------------------------------------------------
//  Private Functions
//-------------------------------------------------------------------------------------------------

#if (ENABLE_MSTAR_NIKON==1) || (ENABLE_MSTAR_MILAN==1)
static int SetPm2L1(void);
#else
static int SetPm2Sram(void);
#endif
BOOLEAN get_poweroff_flag(void);
#if (CONFIG_WDT_RESET_BY_ESD)
static void set_poweroff_flag(BOOLEAN bEnable);
#endif
static int If_Boot_To_PM(void);
static BOOLEAN check_pm_standby(void);
static int PM51_PowerDown(void);
static unsigned int get_pm51_program_counter(void);

//-------------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------------

static int PM51_PowerDown(void)
{
    char *s = NULL;
    int iStrCrc = 0;
        
    UBOOT_TRACE("IN\n");
    if(IsHouseKeepingBootingMode()==TRUE)
    {
        // bring up 51
        if( IsBootingFromMaskRom() == TRUE )
        {
            #if (ENABLE_MSTAR_EDISON==1)
                UBOOT_DEBUG("=== PATCH FOR EDISON GTV ROMBOOT BUT PM IN SPI ===\n");
                msAPI_SetSPIOffsetForMCU();
            #elif (ENABLE_MSTAR_NIKON==1)
                UBOOT_DEBUG("=== NiKon ROMBOOT BUT PM IN L1 ===\n");
                return SetPm2L1();
            #elif (ENABLE_MSTAR_MILAN==1)
                UBOOT_DEBUG("=== Milan ROMBOOT BUT PM IN L1 ===\n");
                return SetPm2L1();
            #else
            UBOOT_DEBUG("== SetPm2Sram ==\n");
            if(SetPm2Sram()!=0)
            {
                UBOOT_ERROR("SetPm2Sram Fail!!\n");
                return -1;
            }
            #endif
        }
        else
        {
            msAPI_SetSPIOffsetForMCU();
        }
    }
    #if (CONFIG_MSTAR_RT_PM_IN_SPI)
    MDrv_PM_STR_CheckFactoryPowerOnModePassword();
    #endif

    s = getenv("factory_poweron_mode");
    if((NULL != s) && (strcmp(s, "power_test") == 0))
    {   
        s = NULL;
        s = getenv("str_crc");
        if(NULL != s)
        {
            iStrCrc = (int) simple_strtoul (s, NULL, 10);
            msAPI_Power_SetStrConfig(iStrCrc);
        }
        else
        {
            msAPI_Power_SetStrConfig(1);
        }
    }
    else
    {
        msAPI_Power_SetStrConfig(1);
    }

    //msAPI_Power_SetStrConfig( (strcmp(getenv("str_crc"), "0")==0) ? FALSE : TRUE ) ;
    msAPI_Power_PowerDown_EXEC();
    UBOOT_TRACE("OK\n");
    return 0;
}

#if (ENABLE_MSTAR_NIKON==1) || (ENABLE_MSTAR_MILAN==1)
extern BOOLEAN msAPI_KeyPad_Initialize(void);

static int SetPm2L1(void)
{
    U32 u32Addr=0;
    U32 u32AddrVA=0;
    U32 u32AddrNVA=0;
    char PMPath[CMD_BUF]="\0";
    UBOOT_TRACE("IN\n");
    //*(volatile U32*)(0xBF203DC4) = 0x0002;
#if (CONFIG_KEYPAD)
    msAPI_KeyPad_Initialize();
#endif
    static PM_WakeCfg PmWakeCfg =
        {
            .bPmWakeEnableIR = TRUE,
            .bPmWakeEnableSAR = TRUE,
            .bPmWakeEnableGPIO0 = FALSE,
            .bPmWakeEnableGPIO1 = FALSE,
            .bPmWakeEnableUART1 = FALSE,
            .bPmWakeEnableSYNC = FALSE,
            .bPmWakeEnableESYNC = FALSE,

            .bPmWakeEnableRTC0 = TRUE,
            .bPmWakeEnableRTC1 = TRUE,
            .bPmWakeEnableDVI0 = FALSE,
            .bPmWakeEnableDVI2 = FALSE,
            .bPmWakeEnableCEC = FALSE,
            .bPmWakeEnableAVLINK = FALSE,

            .u8PmWakeIR =
            {   //IR wake-up key define
                IRKEY_POWER, 0xFF, 0xFF, 0xFF,
                0xFF, 0xFF, 0xFF, 0xFF,
                0xFF, 0xFF, 0xFF, 0xFF,
                0xFF, 0xFF, 0xFF, 0xFF,
                0xFF, 0xFF, 0xFF, 0xFF,
                0xFF, 0xFF, 0xFF, 0xFF,
                0xFF, 0xFF, 0xFF, 0xFF,
                0xFF, 0xFF, 0xFF, 0xFF
            },

            .u8PmWakeIR2 =
            {   //IR wake-up key define
                IRKEY_POWER, 0xFF, 0xFF, 0xFF,
                0xFF, 0xFF, 0xFF, 0xFF,
                0xFF, 0xFF, 0xFF, 0xFF,
                0xFF, 0xFF, 0xFF, 0xFF
            }
        };
    char* s = getenv(POWER_KEY_NAME);
    if (s)
    {
        PmWakeCfg.u8PmWakeIR[0] = simple_strtoul(s, NULL, 16);
    }

    flush_cache((MS_U32)&PmWakeCfg, sizeof(PM_WakeCfg));
    if(MDrv_PM_Init((PM_WakeCfg*)VA2PA((MS_U32) &PmWakeCfg))==0)
    {
        UBOOT_ERROR("MDrv_PM_Init fail !!!\n");
    }

    if(get_value_from_env(E_MMAP_ID_PM51_USAGE_MEM_ADR, NO_DEFAULT_MMAP_VALUE,&u32Addr)!=0)
    {
	    UBOOT_ERROR("get %s mmap fail\n",E_MMAP_ID_PM51_USAGE_MEM_ADR);
        return -1;
    }    

    if(vfs_mount(CONFIG)!= 0)
        {
            UBOOT_ERROR("vfs_mount fail\n");
            return -1;
        }
    snprintf(PMPath,sizeof(PMPath),"%s/PM.bin",CONFIG_PATH);
    u32AddrVA=PA2VA(u32Addr);
    u32AddrNVA=PA2NVA(u32Addr);

    if(vfs_read((void*)u32AddrVA,PMPath,0,0x10000) != 0)
    {
        UBOOT_ERROR("vfs_read fail !!! \n");
        return -1;
    }
    udelay(50);


    UBOOT_DEBUG("Load PM To DRAM PA[0x%x]VA[0x%x]NVA[0x%x] \n",u32Addr,u32AddrVA,u32AddrNVA);

    //MsOS_DisableAllInterrupts();
    flush_cache(u32AddrVA,0x10000);
    Chip_Flush_Memory();

    enable_cache(0);

    ric_fill_icache((void *)(u32AddrNVA), 0x8000);
    ric_fill_dcache((void *)(u32AddrNVA+0x8000), 0x8000);
    enable_cache(1);

    printf("[%s][%s] Start PM at :0x%x \n\n",__FILE__,__FUNCTION__, (u32AddrVA|0x400));
    console_disable();
    udelay(50);

    asm volatile (
            "move $9, %[i0]\n\t" \
            "j $9\n\t" \
            "nop\n\t" \
            : \
            : [i0] "r"(u32AddrVA|0x400) \
            : "memory" \
    );
    while(1);

    UBOOT_TRACE("OK\n");
    return 0;
}

#else
static int SetPm2Sram(void)
{
    U32 u32Addr=0;
#if (ENABLE_MSTAR_NUGGET == 1 || ENABLE_MSTAR_NIKE == 1 || \
	 ENABLE_MSTAR_MADISON == 1 || ENABLE_MSTAR_MIAMI == 1 || \
	 ENABLE_MSTAR_NADAL == 1 || ENABLE_MSTAR_EINSTEIN == 1 || \
	 ENABLE_MSTAR_NAPOLI == 1 || ENABLE_MSTAR_MONACO == 1 || \
	 ENABLE_MSTAR_MUJI == 1 || ENABLE_MSTAR_CELTICS == 1 || \
     ENABLE_MSTAR_CLIPPERS == 1 || (ENABLE_MSTAR_MONET == 1) || \
	 ENABLE_MSTAR_MUNICH == 1 || ENABLE_MSTAR_MANHATTAN == 1 ||\
	 ENABLE_MSTAR_KANO==1 || (ENABLE_MSTAR_MESSI == 1))
#define PM_SIZE 0x5FFF
#else
#define PM_SIZE 0x47FF
#endif
    char PMPath[CMD_BUF]="\0";
    UBOOT_TRACE("IN\n");

    if(get_value_from_env(E_MMAP_ID_PM51_USAGE_MEM_ADR, NO_DEFAULT_MMAP_VALUE,&u32Addr)!=0)
    {
	    UBOOT_ERROR("get %s mmap fail\n",E_MMAP_ID_PM51_USAGE_MEM_ADR);
        return -1;
    }

    if(vfs_mount(CONFIG)!= 0)
    {
        UBOOT_ERROR("vfs_mount fail\n");
        return -1;
    }

    snprintf(PMPath,sizeof(PMPath),"%s/PM.bin",CONFIG_PATH);
    if(vfs_read((void*)PA2NVA(u32Addr),PMPath,0,PM_SIZE) != 0)
    {
        UBOOT_ERROR("vfs_read fail\n");
        return -1;
    }
    if(MDrv_BDMA_CopyHnd(u32Addr, 0x0, PM_SIZE, E_BDMA_SDRAM2SRAM1K_HK51, 0) != TRUE )
    {
        UBOOT_ERROR("MDrv_BDMA_CopyHnd fail\n");
        return -1;
    }
    MDrv_PM_SetSRAMOffsetForMCU();
    UBOOT_TRACE("OK\n");
    return 0;
}
#endif



#if (CONFIG_WDT_RESET_BY_ESD)
static void set_poweroff_flag(BOOLEAN bEnable)///ac power off also use this flag -> dc_poweroff
{
    UBOOT_TRACE("IN\n");

#if (ENABLE_ENV_IN_NAND == 1)
    ////Do nothing because vfs_write is not implement now
#else
    BOOLEAN bOrgValue = FALSE;
    if (strcmp(getenv("dc_poweroff"), "1") == 0)
    {
        bOrgValue = TRUE;
    }

    if (bEnable != bOrgValue)
    {
        if(bEnable)
        {
            setenv("dc_poweroff", "1");
        }
        else
        {
            setenv("dc_poweroff", "0");
        }
        saveenv();
    }
#endif
    UBOOT_TRACE("OK\n");
}
#endif

static BOOLEAN check_pm_standby(void)
{
    BOOLEAN ret = FALSE;
    UBOOT_TRACE("IN\n");
    
    if((getenv("force_onetime_standby")!=NULL) && (strcmp(getenv("force_onetime_standby"), "1") == 0))
    {
         setenv("force_onetime_standby",NULL);
         setenv("MstarUpgrade_complete","1");
         saveenv();
         ret = TRUE;
    }       
    else if(strcmp(getenv("factory_poweron_mode"), "power_test") == 0)
    {
         ret = TRUE;
    }
    else if(strcmp(getenv("factory_poweron_mode"), "secondary") == 0) //Secondary mode, AC on will always enter standby mode.
    {
        #if (ENABLE_MSTAR_NUGGET==1)
        MApi_CEC_ConfigWakeUp();
		#endif
        ret = TRUE;
    }
    else if(strcmp(getenv("factory_poweron_mode"), "memory") == 0) //Memory, DC off -> AC off -> AC on -> Standby
    {
        #if (CONFIG_MSTAR_RT_PM_IN_SPI)
        if(MDrv_PM_STR_CheckFactoryPowerOnMode_Second(1)!= 1)
        {
            return FALSE;
        }
        #endif
        ret = get_poweroff_flag();
    }

    UBOOT_TRACE("OK\n");
    return ret;
}

static unsigned int get_pm51_program_counter(void)
{
    unsigned int u32ProgramCounter = 0;
    UBOOT_TRACE("IN\n");
    u32ProgramCounter = *(volatile U32*)(MS_RIU_MAP+(0x10fe<<1));
    u32ProgramCounter = (u32ProgramCounter&0xff)<<16;
    u32ProgramCounter |= *(volatile U32*)(MS_RIU_MAP+(0x10fc<<1));
    UBOOT_TRACE("OK\n");
    return u32ProgramCounter;
}

static int If_Boot_To_PM(void)
{
    EN_POWER_ON_MODE ePowerMode=EN_POWER_DC_BOOT;
    UBOOT_TRACE("IN\n");

#if (WDT_STANDBY_MODE==1)
    MDrv_WDT_Init(E_WDT_DBGLV_ALL);

    if(MDrv_WDT_IsReset() && get_poweroff_flag())
    {

        MDrv_WDT_ClearRstFlag();
        return 0;
    }
#endif
    ePowerMode=msAPI_Power_QueryPowerOnMode();

    if ( EN_POWER_AC_BOOT == ePowerMode)
    {
        
        if(getenv("factory_poweron_mode") == NULL)  //first boot on has no data
        {
            setenv("factory_poweron_mode", "direct");//default is direct mode (never standby)
// EosTek Patch Begin
            setenv("factory_poweron_mode", "secondary"); //HTV default is secondary mode
// EosTek Patch End
            saveenv();
        }
        if(getenv("dc_poweroff") == NULL)
        {
            setenv("dc_poweroff", "0");
            saveenv();
        }
        
// EosTek Patch Begin
#if (ENABLE_MSTAR_BD_MST082B_10AJQ_MONET_HTV)
        // HTV default is secondary mode after recovery
        char *p_str = NULL;
        p_str = getenv("htvstandbystate");
        if (NULL != p_str && strcmp(getenv("factory_poweron_mode"), "direct") == 0 )
        {
            if (0 == strcmp(p_str, "1"))
            {
                setenv("htvstandbystate", "2");
                saveenv();
            }
            else if (0 == strcmp(p_str, "2"))
            {
                setenv("factory_poweron_mode", "secondary");
                setenv("htvstandbystate", "0");
                saveenv();
            }
        }

#if 0
        //Fenoss.hu modify 2014/11/06 HTV default is UARTOnOff off
        char *m_uartStr = NULL;
        m_uartStr = getenv("htvuartstate");
        printf("\nhtvuartstate %s\n",m_uartStr);
        if ((NULL == m_uartStr) || (strcmp(m_uartStr, "1")))
        {
            printf("\nhtvuartstate %s\n",m_uartStr);
            setenv("htvuartstate", "0");
            setenv("UARTOnOff", "off");
            saveenv();
        }
#endif
#endif
// EosTek Patch End
#if (CONFIG_WDT_RESET_BY_ESD)
        MS_BOOL bWDTResetToPowerOff = FALSE;
    #if (WDT_STANDBY_MODE == 0)
        MDrv_WDT_Init(E_WDT_DBGLV_ALL);
    #endif
        if(MDrv_WDT_IsReset())      // when we running ESD test, system will reset by wdt
        {
            UBOOT_DEBUG("System is reset by watchdog !!!\n");
            if(!get_poweroff_flag())
            {
                UBOOT_DEBUG("The last status is power on, system will not enter in standby mode\n");
                return 0;
            }
            else
            {
                UBOOT_DEBUG("The last status is power down, system will enter in standby mode\n");
                bWDTResetToPowerOff = TRUE;
            }
        }
#endif

#if (CONFIG_WDT_RESET_BY_ESD)
        if(bWDTResetToPowerOff || check_pm_standby())
#else
        if(check_pm_standby())
#endif
        {
#if (CONFIG_WDT_RESET_BY_ESD)
            if (!bWDTResetToPowerOff)
            {
                set_poweroff_flag(TRUE);
            }
#endif
            //PM51_PowerDown();
            run_command("pm51 standby",0);
        }
       
        else
        { //to do: patch need to remove
            #if(ENABLE_MSTAR_KENYA_BD_MST188A_D02A==1)
                WriteByte(0x1422,0x09);
                WriteByte(0x1423,0x09);
                WriteByte(0x1424,0x02);
            #endif
        }
    }
    
    else
    {//to do: patch need to remove
        #if(ENABLE_MSTAR_KENYA_BD_MST188A_D02A==1)
            WriteByte(0x1422,0x09);
            WriteByte(0x1423,0x09);
            WriteByte(0x1424,0x02);
        #endif
    }
    UBOOT_TRACE("OK\n");
    return 0;
}

int do_if_boot_to_pm( cmd_tbl_t *cmdtp, int flag, int argc, char * const argv[])
{
    If_Boot_To_PM();
// EosTek Patch Begin
    LED_GREEN_ON();
    LED_RED_OFF();
// EosTek Patch End
    return 0;
}

int do_pm51( cmd_tbl_t *cmdtp, int flag, int argc, char * const argv[])
{
    int ret =0;
    char * u8Command = NULL;
    UBOOT_TRACE("IN\n");
    if (argc < 2 || argv[1]==NULL)
    {
        cmd_usage(cmdtp);
        return 0;
    }
    u8Command = argv[1];

    if(strncmp(u8Command, "stop", 4) == 0)
    {
        UBOOT_INFO("Stop PM51!\n");
        *(volatile U32*)(MS_RIU_MAP+(0x1018<<1))=0x0000;
    }
    else if(strncmp(u8Command, "start", 6) == 0)
    {
        UBOOT_INFO("Start PM51!\n");
        *(volatile U32*)(MS_RIU_MAP+(0x1018<<1))=0x000e;
    }
    else if(strncmp(u8Command, "readpc", 6) == 0)
    {
        UBOOT_INFO("PM51[PC]=0x%x\n",get_pm51_program_counter());
    }
    else if(strncmp(u8Command, "standby", 7) == 0)
    {
         ret = PM51_PowerDown();
    }
    UBOOT_TRACE("OK\n");
    return ret;
}


