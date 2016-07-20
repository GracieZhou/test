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
#include <exports.h>
#include <MsTypes.h>
#include <ShareType.h>
#include <MsBoot.h>
#include <MsUpgrade.h>
#include <uboot_mmap.h>
#include <MsDebug.h>
#if defined (CONFIG_URSA_6M40) && defined(CONFIG_SET_4K2K_MODE)
#include <CusSystem.h>
#endif

#include <MsRawIO.h>
#include <secure/MsSecureBoot.h>
#include <MsSystem.h>
#include <MsMmap.h>
#include <MsApiSpi.h>
#include <drvWDT.h>

#if defined (CONFIG_SECURITY_BOOT)
#include <crypto_auth.h>
#endif

#if CONFIG_RESCUE_ENV  && CONFIG_RESCUE_ENV_IR_TRIGGER
#include <MsSystem.h>
#endif

#if CONFIG_AUTO_USB_UPGRADE
#include <msAPI_Power.h>
#endif
#include <MsUtility.h>
#include <CusConfig.h>
#include <MsSysUtility.h>
#include <MsSetupTee.h>
#if (CONFIG_URSA_UNION)
#include <ursa/ursa_common.h>
#endif
//-------------------------------------------------------------------------------------------------
//  Debug Functions
//-------------------------------------------------------------------------------------------------

//-------------------------------------------------------------------------------------------------
//  Local Defines
//-------------------------------------------------------------------------------------------------
#define ENV_UPGRADE_COMPLETE        "MstarUpgrade_complete"
#define ENV_UPGRADE_MODE            "upgrade_mode"
#define ENV_FORCE_UPGRADE           "force_upgrade"

#define ENV_BOOTto2SubSystem               "SubSystem"
//-------------------------------------------------------------------------------------------------
//  Global Variables
//-------------------------------------------------------------------------------------------------

#if defined (CONFIG_SEC_SYSTEM)
int ENABLE_SECOND_SYSTEM;
#endif

//-------------------------------------------------------------------------------------------------
//  Local Variables
//-------------------------------------------------------------------------------------------------

static EN_BOOT_MODE stBootMode = EN_BOOT_MODE_UNKNOWN;

//-------------------------------------------------------------------------------------------------
//  Extern Functions
//-------------------------------------------------------------------------------------------------
extern int snprintf(char *str, size_t size, const char *fmt, ...);
extern void Chip_Flush_Memory(void);
extern void del_bootargs_CMA(MS_BOOL bDontSaveEnv);
//-------------------------------------------------------------------------------------------------
//  Private Functions
//-------------------------------------------------------------------------------------------------
static void _RecoveryDrvInit(void);
static void setBootToRecovery(void);
#if (ENABLE_MODULE_NETUPDATE == 1)
static void _do_NetUpgrade_mode(void);
#endif

//-------------------------------------------------------------------------------------------------
//  Public Functions
//-------------------------------------------------------------------------------------------------

int boot_mode_recovery(void)
{
    int ret = -1;
    char *s = NULL;
    char cmd[CMD_BUF]="\0";
    UBOOT_TRACE("IN\n");    
    setBootToRecovery();
    _RecoveryDrvInit();
    
    if(getenv("E_MMAP_ID_RECOVERY_BUFFER_ADR")!=NULL)  
    {
        del_bootargs_CMA(1);    
    }
    
    s = getenv("recoverycmd");
    if ((s == NULL) || (strncmp(s, BOOT_COMMAND, 4) != 0))
    {
        snprintf(cmd, sizeof(cmd)-1, BOOT_COMMAND);
    }
    else
    {
        snprintf(cmd, sizeof(cmd)-1, s);
    }

#if defined (CONFIG_URSA_6M40) && defined(CONFIG_SET_4K2K_MODE)
    Set_4K2K_OP0();
#endif
    ret = run_command(cmd, 0);
    if(ret!=0)
    {
        UBOOT_ERROR("recovery command fail!!\n");
    }
    else
    {
        UBOOT_TRACE("OK\n");
    }
    return ret;
}

#define ENABLE_MODULE_ENV_BOOT 1
#if (ENABLE_MODULE_ENV_BOOT == 1)
EN_BOOT_MODE get_boot_mode_from_env(void)
{
    char *buffer = NULL;
    char ret = EN_BOOT_MODE_NORMAL;
    char *cUpgradeComplete = getenv(ENV_UPGRADE_COMPLETE);
    char *cUpgradeMode = getenv(ENV_UPGRADE_MODE);
    ulong ulComplete = 0;
    UBOOT_TRACE("IN\n");

    buffer = (char*) malloc(CMD_BUF);
    if(buffer==NULL)
    {
        UBOOT_ERROR("malloc fail\n");
        return -1;
    }

    if(cUpgradeMode == NULL)
    {
        memset(buffer, 0 , CMD_BUF);
        snprintf(buffer, CMD_BUF, "setenv %s null;saveenv", ENV_UPGRADE_MODE);
        if(run_command(buffer, 0)!=0)
        {
          UBOOT_ERROR("cmd[%s] fail!!\n",buffer);
        }
    }

    if(cUpgradeComplete == NULL)
    {
        memset(buffer, 0 , CMD_BUF);
        snprintf(buffer, CMD_BUF, "setenv %s 0;saveenv", ENV_UPGRADE_COMPLETE);
        if(run_command(buffer, 0)!=0)
        {
          UBOOT_ERROR("cmd[%s] fail!!\n",buffer);
        }
    }

    if(cUpgradeComplete != NULL)
    ulComplete = simple_strtoul (cUpgradeComplete, NULL, 10);

    if(ulComplete == 0)
    {
        if(strncmp(cUpgradeMode, "usb",3) == 0)
        {
            ret = EN_BOOT_MODE_USB_UPGRADE;
        }
#if CONFIG_MINIUBOOT
#else
        else if(strncmp(cUpgradeMode, "oad",3) == 0)
        {
            ret = EN_BOOT_MODE_OAD_UPGRADE;
        }
        else if(strncmp(cUpgradeMode, "net",3) == 0)
        {
            ret = EN_BOOT_MODE_NET_UPGRADE;
        }
#endif
#if (ENABLE_MODULE_ENV_UPGRADE_FROM_BANK == 1)
        else if(strncmp(cUpgradeMode, "env",3) == 0)
        {
            ret = EN_BOOT_MODE_ENV_UPGRADE;
        }
#endif
    }
    free(buffer);
    UBOOT_TRACE("OK\n");
    return ret;
}

#endif

int do_upgradecompletecheck (cmd_tbl_t *cmdtp, int flag, int argc, char * const argv[])
{
    char *buffer = NULL;
    char *cUpgradeComplete = getenv(ENV_UPGRADE_COMPLETE);
    int ret = 0;    
    ulong ulComplete = 0;
    UBOOT_TRACE("IN\n");

    buffer = (char*) malloc(CMD_BUF);
    if(buffer==NULL)
    {
        UBOOT_ERROR("malloc fail\n");
        return -1;
    }

    if(cUpgradeComplete != NULL)
    ulComplete = simple_strtoul (cUpgradeComplete, NULL, 10);

    if(ulComplete == 0)
    {
        ret = 0;
    }
    else
    {
        memset(buffer, 0 , CMD_BUF);
        snprintf(buffer, CMD_BUF, "setenv %s 0", ENV_UPGRADE_COMPLETE);
        run_command(buffer, 0);  // run_command("setenv MstarUpgrade_complete 0");
        run_command("saveenv", 0);
        UBOOT_DEBUG("last upgrade complete\n");
        ret = 1;
    }
    free(buffer);
    UBOOT_TRACE("OK\n");
    return ret;

}

#if (ENABLE_MODULE_BOOT_IR == 1)
EN_BOOT_MODE get_boot_mode_from_ir(void)
{
    U8 IRKey=0xFF;
    U8 IRFlag;
#if CONFIG_RESCUE_ENV && CONFIG_RESCUE_ENV_IR_TRIGGER
    U8 u8IRKeyPrev=0, u8KeyCheckCnt=0;
    U32 u32FirstTime = 0;
    const U32 BRICK_TERMINATOR_IR_TIMEOUT = 3000;
    const U8 BRICK_TERMINATOR_IR_CHECK_CNT = IR_BRICK_TERMINATOR_RECOVERY_KEY_REPEAT_REQUIRED;
#endif
    EN_BOOT_MODE mode = EN_BOOT_MODE_UNKNOWN;
    UBOOT_TRACE("IN\n");
    //IRKey = MDrv_ReadByte(0x3DA9); // get IR code
    extern BOOLEAN msIR_GetIRKeyCode(U8 *pkey, U8 *pu8flag);
    msIR_GetIRKeyCode(&IRKey,&IRFlag);
#if CONFIG_RESCUE_ENV && CONFIG_RESCUE_ENV_IR_TRIGGER
    u8IRKeyPrev = IRKey;
    u32FirstTime = MsOS_GetSystemTime();
#endif
    printf("fore uup IRKey [0x%x]\n",IRKey);
    switch(IRKey) // NOTE: read IR Key to decide what boot mode to go
    {
#if CONFIG_RESCUE_ENV && CONFIG_RESCUE_ENV_IR_TRIGGER
        case IR_BRICK_TERMINATOR_RECOVERY_KEY1:
        case IR_BRICK_TERMINATOR_RECOVERY_KEY2:
            {
                while(1)
                {
                    u32FirstTime = MsOS_GetSystemTime();
                    msIR_GetIRKeyCode(&IRKey,&IRFlag);
                    if((0xFF!=IRKey) && (u8IRKeyPrev!=IRKey))
                    {
                        printf("fore uup IRKey [0x%x] cnt: %u, at %d\n", IRKey, u8KeyCheckCnt, __LINE__);
                        u8KeyCheckCnt+=1;
                        if(BRICK_TERMINATOR_IR_CHECK_CNT <= u8KeyCheckCnt)
                        {
                            mode = EN_BOOT_MODE_BRICK_TERMINATOR;
                            break;
                        }
                        u8IRKeyPrev = IRKey;
                    }
                    if(BRICK_TERMINATOR_IR_TIMEOUT < MsOS_Timer_DiffTimeFromNow(u32FirstTime))
                    {
                        printf("%s: TIMEOUT, at %d\n", __func__, __LINE__);
                        break;
                    }
                    udelay(5000);
                }
            }
            break;
#endif
        case IR_RECOVERY_KEY:
            mode = EN_BOOT_MODE_RECOVERY;
            break;
        case IR_UPGRADEUSBOTA_KEY:
            mode = EN_BOOT_MODE_OTA_UPGRADE;
            break;
        case IR_FORCEUGRADE_KEY:
            mode = EN_BOOT_MODE_USB_UPGRADE;
            break;
        case IR_UPGRADEBOOTLOADER_KEY:
            mode = EN_BOOT_MODE_USB_RECOVRY_UPGRADE;
            break;
        case IR_RECOVERYWIPEDATA_KEY:
            mode = EN_BOOT_MODE_RECOVRY_WIPE_DATA;
            break;
        case IR_RECOVERYWIPECACHE_KEY:
            mode = EN_BOOT_MODE_RECOVRY_WIPE_CACHE;
            break;
#if defined (CONFIG_SEC_SYSTEM)
        case IR_SECOND_SYSTEM_KEY:
            if (0 == strcmp(getenv(ENV_BOOTto2SubSystem),"1"))
            {
                mode = EN_BOOT_MODE_SECOND_SYSTEM;
                ENABLE_SECOND_SYSTEM=1;
            }
            break;
#endif
        default:
            mode = EN_BOOT_MODE_UNKNOWN;
            break;
    }
    UBOOT_TRACE("OK\n");
    return mode;
}
#endif

#if (ENABLE_MODULE_ANDROID_BOOT == 1 )

EN_BOOT_MODE get_boot_mode_from_mtd0(void)
{
    EN_BOOT_MODE mode = EN_BOOT_MODE_UNKNOWN;
    struct bootloader_message *p_msg = NULL;
    p_msg = (struct bootloader_message*)BOOT_MODE_TMP_BUF_ADDR;
    char cmd[CMD_BUF]="\0";
    UBOOT_TRACE("IN\n");
#if (ENABLE_MODULE_MMC == 1)
    snprintf(cmd, sizeof(cmd)-1, "mmc read.p 0x%08lX misc 32", (unsigned long)BOOT_MODE_TMP_BUF_ADDR);
#else
    snprintf(cmd, sizeof(cmd)-1, "nand read.e 0x%08lX misc 32", (unsigned long)BOOT_MODE_TMP_BUF_ADDR);
#endif
    if(-1 != run_command(cmd, 0))
    {

        if((strlen(p_msg->command) == strlen(BOOT_MODE_RECOVERY_STR)) && (0==strncmp(p_msg->command, BOOT_MODE_RECOVERY_STR, strlen(BOOT_MODE_RECOVERY_STR))))
        {
            mode = EN_BOOT_MODE_RECOVERY;
        }
        /*else {do nothing;}*/
    }
    else
    {
        printf("%s: '%s' fails, at %d\n", __func__, cmd, __LINE__);
    }
    UBOOT_TRACE("OK\n");
    return mode;
}

#endif


#if (ENABLE_MODULE_BOOT_KEYPAD == 1)
extern BOOLEAN msKeypad_GetKey(U8 *pkey, U8 *pflag);

EN_BOOT_MODE get_boot_mode_from_keypad(void)
{
     U8 u8KeyPad_KeyVal=0xFF;
     U8 u8KeyPad_RepFlag = 0;
     EN_BOOT_MODE mode = EN_BOOT_MODE_UNKNOWN;
     UBOOT_TRACE("IN\n");
     msKeypad_GetKey(&u8KeyPad_KeyVal,&u8KeyPad_RepFlag);
     UBOOT_DEBUG("fore uup u8KeyPad_KeyVal [0x%x]\n",u8KeyPad_KeyVal);
     switch(u8KeyPad_KeyVal) // NOTE: read IR Key to decide what boot mode to go
    {
        case KEYPAD_FORCEUGRADE_KEY:
            mode =EN_BOOT_MODE_USB_UPGRADE;
            break;
        case KEYPAD_UART_DEBUG_KEY:
            mode = EN_BOOT_MODE_UART_DEBUG;
            break;
        #if (ENABLE_MODULE_SYSTEM_RESTORE == 1)
        case KEYPAD_SYSTEM_RESTORE_KEY:
            mode = EN_BOOT_MODE_SYSTEM_RESTORE;
            break;
        #endif
        default:
            mode = EN_BOOT_MODE_UNKNOWN;
            break;
    }
     UBOOT_TRACE("OK\n");
     return mode;
}
#endif

// set boot to Recovery mode flag
static void setBootToRecovery(void)
{
    stBootMode = EN_BOOT_MODE_RECOVERY;
}

// check if boot to Recovery mode
// must call it after bootcheck command
int isBootToRecovery(void)
{
    if(EN_BOOT_MODE_RECOVERY == stBootMode)
    {
        UBOOT_DEBUG("boot to recovery mode\n");
        return 1;
    }
    else
    {
        UBOOT_DEBUG("boot to normal mode\n");
        return 0;
    }
}

#if ENABLE_STB_BOOT
#if defined(CONFIG_DISABLE_PM_PARTITION)
#define STB_UBOOT_INFO_ADDR     0x10000
#else
#define STB_UBOOT_INFO_ADDR     0x20000
#endif
#define STB_INFO_SIZE           0x400//1024B//0x20000

void Big2LittleEndian(U8* addr,U8* data)
{

    U8 * tmp = NULL;
    tmp = (U8 *)(data);
    *tmp = *(addr+3);
    *(tmp+1) = *(addr+2);
    *(tmp+2) = *(addr+1);
    *(tmp+3) = *(addr);
    UBOOT_DEBUG("\n*(addr) 0x%x\n",*addr);
}

int do_read_boot_info(cmd_tbl_t *cmdtp, int flag, int argc, char * const argv[])
{
    char buffer[256];
    U8 *pTemp;
    U32 u32AppMagicFlag;
    U32 u32AppFileLen;
    U32 u32AppDramAddr;
    U32 u32AppRestAddr;
    U32 u32AppSPIAddr;
    U32 u32LzmaBufAddr;
    U32 u32LzmaDecBufAddr;
    U32 u32UbootInfoAddr = 0;
    int ret;
    int ch = 0;

    if (tstc())
    {   /* we got a key press   */
        ch = getc();  /* consume input  */
        puts ("\b\b\b 0");
        if (ch == 0x1B)//when esc,upgrade flash_usb_auto.bin
        {
            if (-1 == appInitUsbDisk())
            {
               UBOOT_ERROR("FAIL : can not init usb!! \n");
               return -1;
            }

            sprintf(buffer, "ustar /flash_usb_auto.bin");
            UBOOT_DEBUG("cmd=%s\n", buffer);
            if(-1 == run_command(buffer, 0))
            {
                UBOOT_ERROR("USB auto upgrade fail!\n");
                return -1;
            }

            run_command("reset",0);
        }
    }

    memset(buffer, 0 , 256);
    sprintf(buffer,"spi_rdc 0x80B00000 0x%x 0x%x", STB_UBOOT_INFO_ADDR, STB_INFO_SIZE);
    UBOOT_INFO("%s\n", buffer);

    ret = run_command(buffer, 0);
    if (ret!=0)
    {
        UBOOT_ERROR("cmd %s failed\n", buffer);
        return FALSE;
    }
    else
    {
        UBOOT_DEBUG("cmd %s success\n", buffer);
    }

#if (CONFIG_DISPLAY_LOGO == 1)
    pTemp = (U8 *)((U32)0x80B00000);
    pTemp += 0x20;//uboot rom end addr stored here
    u32UbootInfoAddr = *((U32 *) pTemp);

    UBOOT_DEBUG("u32UbootInfoAddr: 0x%x\n", u32UbootInfoAddr);
    pTemp += 0x4;
    u32UbootInfoAddr +=*((U32 *) pTemp);

    UBOOT_DEBUG("u32UbootInfoAddr1: 0x%x\n", u32UbootInfoAddr);
#else
    pTemp = (U8 *)((U32)0x80B00000);
    pTemp += 0xc;//uboot rom end addr stored here

    UBOOT_DEBUG("u32UbootInfoAddr: 0x%x\n", u32UbootInfoAddr);
    u32UbootInfoAddr = *((U32 *) pTemp);
    UBOOT_DEBUG("u32UbootInfoAddr1: 0x%x\n", u32UbootInfoAddr);
    u32UbootInfoAddr &= 0x000fffff;//get offest of 0xBFCxxxxx
    UBOOT_DEBUG("u32UbootInfoAddr: 0x%x\n", u32UbootInfoAddr);
#endif

    u32UbootInfoAddr = u32UbootInfoAddr + 65536;
    u32UbootInfoAddr = u32UbootInfoAddr/65536;
    u32UbootInfoAddr = u32UbootInfoAddr*65536;

    UBOOT_DEBUG("BOOT_INFO_ADDR: 0x%x\n", u32UbootInfoAddr);

    memset(buffer, 0 , 256);
    sprintf(buffer,"spi_rdc 0x80B00000 0x%x 0x%x", u32UbootInfoAddr, STB_INFO_SIZE);
    UBOOT_INFO("%s\n", buffer);

    ret = run_command(buffer, 0);

    if (ret!=0)
    {
        UBOOT_ERROR("cmd %s failed\n", buffer);
        return FALSE;
    }
    else
    {
        UBOOT_DEBUG("cmd %s success\n", buffer);
    }

    pTemp = (U8 *)((U32)0x80B00000);

    if (*((U32*)pTemp) == 0xefbeadde)
    {
       u32AppMagicFlag = 0xDEADBEEF;
    }
    Big2LittleEndian(pTemp,(U8*)(&u32AppMagicFlag));

    if (u32AppMagicFlag != 0xDEADBEEF)
    {
        UBOOT_ERROR("AppMagicFlag ERROR !!\n");
        return FALSE;
    }
    else
    {
        UBOOT_DEBUG("AppMagicFlag Correct !!\n");

    }
    UBOOT_DEBUG("\n*(pTemp +4) 0x%x\n",*(U32*)(pTemp +4));//printf just print 4 bytes in order
    Big2LittleEndian((U8 *)(pTemp +4),(U8 *) &u32AppFileLen);
    UBOOT_DEBUG("\nu32AppFileLen 0x%x\n", u32AppFileLen);

    // u32AppRestAddr
    Big2LittleEndian((U8 * )(pTemp +12),(U8 *) &u32AppRestAddr);
    Big2LittleEndian((U8 * )(pTemp +8),(U8 *) &u32AppDramAddr);
    UBOOT_DEBUG("\nu32AppDramAddr 0x%x\n",u32AppDramAddr);

    // u32AppSPIAddr
    u32AppSPIAddr = 0;
    Big2LittleEndian((U8 *)(pTemp +16),(U8 *) &u32AppSPIAddr);
    UBOOT_DEBUG("\nu32AppSPIAddr 0x%x\n", u32AppSPIAddr);

    // u32LzmaBufAddr
    u32LzmaBufAddr = 0;
    Big2LittleEndian((U8 *)(pTemp +20),(U8 *) &u32LzmaBufAddr);
    UBOOT_DEBUG("\nu32AppFileLen 0x%x\n", u32LzmaBufAddr);

    // u32LzmaDecBufAddr
    u32LzmaDecBufAddr = 0;
    Big2LittleEndian((U8 *)(pTemp +24),(U8 *) &u32LzmaDecBufAddr);
    UBOOT_DEBUG("\nu32AppFileLen 0x%x\n", u32LzmaDecBufAddr);

    memset(buffer, 0 , 256);

    //u32AppFileLen = 0x300000;
    sprintf(buffer, "setenv bootcmd ' usb exit;spi_rdc 0x%x 0x%x 0x%x; LzmaDec 0x%x 0x%x 0x%x 0x%x; go 0x%x;",u32LzmaBufAddr,u32AppSPIAddr,u32AppFileLen,u32LzmaBufAddr,u32AppFileLen,u32AppDramAddr,u32LzmaDecBufAddr,u32AppRestAddr);
    UBOOT_INFO("%s\n", buffer);

    ret = run_command (buffer, 0);
    if (ret != 0)
    {
        UBOOT_ERROR("cmd %s failed\n", buffer);
        return FALSE;
    }
    else
    {
        UBOOT_DEBUG("cmd %s success\n", buffer);
    }

    memset(buffer, 0 , 256);
    sprintf(buffer, "saveenv");

    ret = run_command (buffer, 0);
    if (ret!=0)
    {
        UBOOT_ERROR("cmd %s failed\n", buffer);
        return FALSE;
    }
    else
    {
        UBOOT_DEBUG("cmd %s success\n", buffer);
    }

    return TRUE;
}
#endif

void _RecoveryDrvInit(void)
{
    run_command("setenv "ENV_UPGRADE_COMPLETE" 1;saveenv", 0);
    run_command("panel_pre_init",0);    
#if(ENABLE_XC_Init==1)
    #if (CONFIG_URSA_UNION)
    if ((MDrv_URSA_COM_Get_URSA_Type() == URSA_COMMON_U9) || (MDrv_URSA_COM_Get_URSA_Type() == URSA_COMMON_U11))
    #endif        
    {
        run_command("xc_init", 0);
    }
#endif //#if(ENABLE_XC_Init==1)
    
#if defined(CONFIG_ENABLE_4K2K_PANEL)
#if defined(CONFIG_INX_NOVA_VB1)
    run_command("inx_panel_set_init", 0);
#endif
#endif
#if(ENABLE_CVBS_OUT==1)
    run_command ("cvbs" , 0);
#endif
#if(ENABLE_HDMI_TX == 1)
    run_command("hdmi init",0);
#endif
#if (CONFIG_ENABLE_RTPM)
    run_command ("runtime_pm" , 0);
#endif
#if defined (CONFIG_URSA_6M40)
    run_command("ursa_lvds_on", 0);
#endif
#if defined (CONFIG_URSA_8)
    run_command("ursa8_lvds_on", 0);
#endif
    
#if defined (CONFIG_URSA6_VB1) || defined(CONFIG_URSA_UNION)
    #if (CONFIG_URSA_UNION)
    if (MDrv_URSA_COM_Get_URSA_Type() == URSA_COMMON_U6) 
    #endif
    {
        run_command("send_I2C_cmd_to_ursa6", 0);
    }
#endif // #if defined (CONFIG_URSA6_VB1) || defined(CONFIG_URSA_UNION)
    
    run_command("bootlogo 0 0 1 1 boot_recovery.jpg", 0);

#if defined(CONFIG_INX_VB1) && defined(CONFIG_BOOTLOGO_4K2K)
    run_command("panel_inx_vb1_init", 0);
#endif

}
#if (ENABLE_MODULE_NETUPDATE == 1)
void _do_NetUpgrade_mode(void)
{
#if defined (CONFIG_NETUPGRADE_IN_BOOT)  
    UBOOT_DEBUG("Jump to KL_RC\n");
    set_bootargs_cfg("root","root=ubi:recovery",1);
    boot_mode_recovery();
#else
    run_command("cnstar",0);//net upgrade
#endif
}
#endif
int do_bootcheck (cmd_tbl_t *cmdtp, int flag, int argc, char * const argv[])
{
    int ret=0;
    EN_BOOT_MODE BootMode;
    BOOLEAN BootFlag;
    BootMode = EN_BOOT_MODE_UNKNOWN;
    BootFlag=0; // decide the boot mode

    UBOOT_TRACE("IN\n");

    if(MDrv_WDT_IsReset())
    {
        printf("Boot Check: Reboot by WDT --------\n");
    }

#if (ENABLE_AUTO_USB_UPGRADE == 1)
    #if (ENABLE_AUTO_USB_UPGRADE_AC == 1)
        if (msAPI_Power_QueryPowerOnMode() == EN_POWER_AC_BOOT)
    #endif
        {
            char *cUpgradeComplete = getenv(ENV_UPGRADE_COMPLETE);
            if((cUpgradeComplete == NULL) || (0 == strcmp(cUpgradeComplete, "0")))
            {
                //bootmode via AC ON
                run_command("custar", 0);
            }
        }
#endif

#if (ENABLE_MODULE_BOOT_IR == 1)
     if(BootFlag == 0)
     {
        BootMode =get_boot_mode_from_ir();
        if(BootMode!=EN_BOOT_MODE_UNKNOWN)
        {
            //bootmode via IR
            BootFlag=1;
        }
     }
#endif

#if (ENABLE_MODULE_BOOT_KEYPAD == 1)
     if(BootFlag == 0)
     {
        BootMode =get_boot_mode_from_keypad();
        if(BootMode!=EN_BOOT_MODE_UNKNOWN)
        {
            //BootMode via KEYPAD
            BootFlag=1;
        }
     }
#endif

#if (ENABLE_MODULE_ANDROID_BOOT == 1)
    // NOTE: read BCB in mtd0 (misc) to decide what boot mode to go
    if(BootFlag == 0)
    {
        BootMode=get_boot_mode_from_mtd0();
        if(BootMode!=EN_BOOT_MODE_UNKNOWN)
        {
            //BootMode via MTD
            BootFlag=1;
        }
    }
#endif

#if (ENABLE_MODULE_ENV_BOOT == 1)
    if(BootFlag == 0)
    {
        BootMode=get_boot_mode_from_env();
    }

    char* force_upgrade = getenv(ENV_FORCE_UPGRADE);
    printf("====>>>force_upgrade [0x%s]\n",force_upgrade);
    if(force_upgrade)
    {
        int force_flag = simple_strtoul(force_upgrade,NULL,16);
        if((force_flag <= 0x1F)&&(force_flag > 0x00))
        {
           //last time force upgrade not finish,so continue upgrading
            BootMode = EN_BOOT_MODE_USB_UPGRADE;
        }
    }
#endif
#if (ENABLE_MODULE_ANDROID_BOOT == 1 ) || (ENABLE_MODULE_BOOT_IR == 1)
#if CONFIG_RESCUE_ENV && CONFIG_RESCUE_ENV_IR_TRIGGER
    int brick_terminator_recover_mode = 0;
#endif
#endif
    switch(BootMode)
    {
#if CONFIG_RESCUE_ENV && CONFIG_RESCUE_ENV_IR_TRIGGER
        case EN_BOOT_MODE_BRICK_TERMINATOR:
            brick_terminator_recover_mode = 1;
            break;
#endif
#if (ENABLE_MODULE_ANDROID_BOOT == 1 )
        case EN_BOOT_MODE_RECOVERY:
            boot_mode_recovery();
            break;
        case EN_BOOT_MODE_RECOVRY_WIPE_DATA:
            run_command("recovery_wipe_partition data",0);
            boot_mode_recovery();
            break;
        case EN_BOOT_MODE_RECOVRY_WIPE_CACHE:
            run_command("recovery_wipe_partition cache",0);
            boot_mode_recovery();
            break;
        #if (ENABLE_MODULE_SYSTEM_RESTORE == 1)
        case EN_BOOT_MODE_SYSTEM_RESTORE:
            ret = run_command("SystemRestore",0);
            if (ret != -1)
            {
                boot_mode_recovery();
            }
            break;
        #endif
#endif
        #if (ENABLE_MODULE_USB == 1)
        case EN_BOOT_MODE_USB_UPGRADE:
            #ifdef CONFIG_MSTAR_NUGGET
            console_init();   //Open Uart when upgrade
            #endif
            ret = run_command("custar",0);//usb upgrade
            run_command("setenv "ENV_UPGRADE_COMPLETE" 1;saveenv", 0);
            break;
#if (ENABLE_MODULE_BOOT_IR == 1)
        case EN_BOOT_MODE_OTA_UPGRADE:
            ret = run_command("ota_zip_check",0);//ota upgrade
            if (ret != -1)
            {
                boot_mode_recovery();
            }
            break;
        case EN_BOOT_MODE_USB_RECOVRY_UPGRADE:
            ret = run_command("usb_bin_check",0);//usb upgrade
            if(ret == 0)
            {
                run_command("setenv "ENV_UPGRADE_COMPLETE" 1;saveenv", 0);
                break;
            }
            ret = run_command("ota_zip_check",0);//ota upgrade
            if (ret != -1)
            {
                boot_mode_recovery();
            }
            break;
#endif
        #endif
        #if (ENABLE_MODULE_OAD == 1)
        case EN_BOOT_MODE_OAD_UPGRADE:
            ret = run_command("costar",0);//oad upgrade
            break;
        #endif
        #if (ENABLE_MODULE_ENV_UPGRADE_FROM_BANK == 1)
        case EN_BOOT_MODE_ENV_UPGRADE:
            ret = run_command("rstar",0);
        #endif
        #if (ENABLE_MODULE_NETUPDATE == 1)
        case EN_BOOT_MODE_NET_UPGRADE:
            _do_NetUpgrade_mode();
            break;
        #endif
        case EN_BOOT_MODE_UART_DEBUG:
            ret = run_command("setenv UARTOnOff on", 0);
            ret = run_command("saveenv", 0);
            printf("Opening UART now\n");
            break;
        case EN_BOOT_MODE_NORMAL:
            break;
        default:
            //normal booting according bootcmd in main.c
            UBOOT_DEBUG("non available case\n");
            break;
    }
    
#if CONFIG_RESCUE_ENV && CONFIG_RESCUE_ENV_IR_TRIGGER
    if(1 == brick_terminator_recover_mode)
    {
        run_command("BrickTerminator force_recover",0);
#if ((CONFIG_RESCUE_ENV_IR_TRIGGER) && (1 != CONFIG_RESCUE_ENV_AUTO_TRIGGER))
        setenv(BRICK_TERMINATOR_IR_TRIGGERED_STRING, "1");
        saveenv();
#endif
    }
#endif
    UBOOT_TRACE("OK\n");
    return ret;
}


#if defined(CONFIG_SEC_SYSTEM)
int do_Boot2secKernel (cmd_tbl_t *cmdtp, int flag, int argc, char * const argv[])
{
    char *bootcmd;
    UBOOT_TRACE("IN\n");
    if (ENABLE_SECOND_SYSTEM==1)
    {
        bootcmd=getenv("bootcmd");
        UBOOT_DEBUG("%s\n",bootcmd);
        if(bootcmd!=NULL)
        {       
            UBOOT_INFO("\033[0;34m*******************************\033[0m\n");
            UBOOT_INFO("\033[0;34m* BOOT to SECOND KERNEL !! *\033[0m\n");
            UBOOT_INFO("\033[0;34m*******************************\033[0m\n");
            run_command(bootcmd,0);
        }
    }
    UBOOT_TRACE("OK\n");
    return 0;
}
#endif
