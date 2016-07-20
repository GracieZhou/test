/*
 * (C) Copyright 2002-2006
 * Wolfgang Denk, DENX Software Engineering, wd@denx.de.
 *
 * (C) Copyright 2002
 * Sysgo Real-Time Solutions, GmbH <www.elinos.com>
 * Marius Groeger <mgroeger@sysgo.de>
 *
 * See file CREDITS for list of people who contributed to this
 * project.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307 USA
 */

/*
 * To match the U-Boot user interface on ARM platforms to the U-Boot
 * standard (as on PPC platforms), some messages with debug character
 * are removed from the default U-Boot build.
 *
 * Define DEBUG here if you want additional info as shown below
 * printed upon startup:
 *
 * U-Boot code: 00F00000 -> 00F3C774  BSS: -> 00FC3274
 * IRQ Stack: 00ebff7c
 * FIQ Stack: 00ebef7c
 */

#include <common.h>
#include <command.h>
#include <malloc.h>
#include <stdio_dev.h>
#include <timestamp.h>
#include <version.h>
#include <net.h>
#include <serial.h>
#include <nand.h>
#include <onenand_uboot.h>
#include <mmc.h>
// MSTAR start
#include <MsInit.h>
#include <ShareType.h>
#include <MsSystem.h>

// MSTAR end

#ifdef CONFIG_BITBANGMII
#include <miiphy.h>
#endif

#ifdef CONFIG_DRIVER_SMC91111
#include "../drivers/net/smc91111.h"
#endif
#ifdef CONFIG_DRIVER_LAN91C96
#include "../drivers/net/lan91c96.h"
#endif

/* initialize gd to make sure it in data section*/
gd_t *gd =(gd_t *)0xbabebabe;

ulong monitor_flash_len;

#ifdef CONFIG_HAS_DATAFLASH
extern int  AT91F_DataflashInit(void);
extern void dataflash_print_info(void);
#endif

#ifndef CONFIG_IDENT_STRING
#define CONFIG_IDENT_STRING ""
#endif

const char version_string[] =
	U_BOOT_VERSION" (" U_BOOT_DATE " - " U_BOOT_TIME ")"CONFIG_IDENT_STRING;

#ifdef CONFIG_DRIVER_RTL8019
extern void rtl8019_get_enetaddr (uchar * addr);
#endif

#if defined(CONFIG_HARD_I2C) || \
    defined(CONFIG_SOFT_I2C)
#include <i2c.h>
#endif

//#define DYNAMIC_RELOCATE_BIST 1


/************************************************************************
 * Coloured LED functionality
 ************************************************************************
 * May be supplied by boards if desired
 */
void inline __coloured_LED_init (void) {};
void coloured_LED_init (void) __attribute__((weak, alias("__coloured_LED_init")));
void inline __red_LED_on (void) {}
void red_LED_on (void) __attribute__((weak, alias("__red_LED_on")));
void inline __red_LED_off(void) {}
void red_LED_off(void) __attribute__((weak, alias("__red_LED_off")));
void inline __green_LED_on(void) {}
void green_LED_on(void) __attribute__((weak, alias("__green_LED_on")));
void inline __green_LED_off(void) {}
void green_LED_off(void) __attribute__((weak, alias("__green_LED_off")));
void inline __yellow_LED_on(void) {}
void yellow_LED_on(void) __attribute__((weak, alias("__yellow_LED_on")));
void inline __yellow_LED_off(void) {}
void yellow_LED_off(void) __attribute__((weak, alias("__yellow_LED_off")));
void inline __blue_LED_on(void) {}
void blue_LED_on(void) __attribute__((weak, alias("__blue_LED_on")));
void inline __blue_LED_off(void) {}
void blue_LED_off(void) __attribute__((weak, alias("__blue_LED_off")));

#if defined (CONFIG_POINTS_HANDLER_ENABLE)
extern int snprintf(char *str, size_t size, const char *fmt, ...);
#endif

/************************************************************************
 * Init Utilities							*
 ************************************************************************
 * Some of this code should be moved into the core functions,
 * or dropped completely,
 * but let's get it working (again) first...
 */

#if defined(CONFIG_ARM_DCC) && !defined(CONFIG_BAUDRATE)
#define CONFIG_BAUDRATE 115200
#endif
static int init_baudrate (void)
{
	char tmp[64];	/* long enough for environment variables */
	int i = getenv_f("baudrate", tmp, sizeof (tmp));

	gd->baudrate = (i > 0)
			? (int) simple_strtoul (tmp, NULL, 10)
			: CONFIG_BAUDRATE;

	return (0);
}

// this code is more prior, so we can't use array to extern. it will be cleared
#if defined (CONFIG_POINTS_HANDLER_ENABLE)
#define CMD_BUF                 128
char aPrePoints[CMD_BUF] = "MB_start_ub";
unsigned int start_ub_time = 9;   // value: only in data section
#endif
static int display_banner (void)
{
    /* checkpoint for autotest boottime, plz dont remove it */
    extern unsigned long G_MS_BOOTTIME_SBOOT; // global variable for storing the boot time used in sboot (ms)
    unsigned int PiuTick = *(volatile unsigned int *)(0x1f006090);
    PiuTick |= (*(volatile unsigned int *)(0x1f006094)) << 16;
    unsigned int PiuTime = PiuTick / 12000;
    G_MS_BOOTTIME_SBOOT = (unsigned long)PiuTime;
    printf("[AT][MB][start ub][%u]", PiuTime);
    #if defined (CONFIG_POINTS_HANDLER_ENABLE)
    start_ub_time = PiuTime;
    #endif

	printf ("\n\n%s  %s\n\n", version_string,MBOOT_CL);
#ifdef CONFIG_MSTAR_STR_MINISIZE
    debug ("U-Boot code: %08lX -> %08lX  BSS: -> %08lX\n",
	       _TEXT_BASE,
	       _bss_start_ofs+uboot2_ofs+_TEXT_BASE, _bss_end_ofs+uboot2_ofs+_TEXT_BASE);
#else
	debug ("U-Boot code: %08lX -> %08lX  BSS: -> %08lX\n",
	       _TEXT_BASE,
	       _bss_start_ofs+_TEXT_BASE, _bss_end_ofs+_TEXT_BASE);
#endif

#ifdef CONFIG_MODEM_SUPPORT
	debug ("Modem Support enabled\n");
#endif
#ifdef CONFIG_USE_IRQ
	debug ("IRQ Stack: %08lx\n", IRQ_STACK_START);
	debug ("FIQ Stack: %08lx\n", FIQ_STACK_START);
#endif

	return (0);
}

/*
 * WARNING: this code looks "cleaner" than the PowerPC version, but
 * has the disadvantage that you either get nothing, or everything.
 * On PowerPC, you might see "DRAM: " before the system hangs - which
 * gives a simple yet clear indication which part of the
 * initialization if failing.
 */
static int display_dram_config (void)
{
	int i;

#ifdef DEBUG
	puts ("RAM Configuration:\n");

	for(i=0; i<CONFIG_NR_DRAM_BANKS; i++) {
		printf ("Bank #%d: %08lx ", i, gd->bd->bi_dram[i].start);
		print_size (gd->bd->bi_dram[i].size, "\n");
	}
#else
	ulong size = 0;

	for (i=0; i<CONFIG_NR_DRAM_BANKS; i++) {
		size += gd->bd->bi_dram[i].size;
	}
	puts("DRAM:  ");
	print_size(size, "\n");
#endif

	return (0);
}

#define MACADDR_STR                     "macaddr"
#define SYSTEM_Ver_STR                  "EOSTEK_SYSTEM_Ver"
#define MTA_Check_STR                   "EOSTEK_MTA_Check"
#define MTA_Reset_STR                   "EOSTEK_MTA_Reset"
#define XShuaiUFO_300LM                 "XShuaiUFO_300LM"
#define PQUPGRADE_Ver_STR               "EOSTEK_PQUPGRADE_Ver"

#define EOSTEK_MACADDR_STR              "EOSTEK_MAC:"
#define EOSTEK_SYSTEM_Ver_STR           "EOSTEK_SYSTEM_Ver:"
#define EOSTEK_MTA_Check_STR            "EOSTEK_MTA_Check:"
#define EOSTEK_MTA_Reset_STR            "EOSTEK_MTA_Reset:"
#define EOSTEK_PQUPGRADE_Ver_STR        "EOSTEK_PQUPGRADE_Ver:"
#define EOSTEK_XShuaiUFO_300LM          "EOSTEK_XShuaiUFO_300LM:"
#define EOSTEK_PQUPGRADE_CLEAR_STR      "EOSTEK_PQUPGRADE_CLEAR"
static int display_EOS_Info (void)
{
    char *tmp = "";
    
    printf("\r\n====================================================\r\n");
    printf("\r\n==============  EOSTEK PRODUCE INFO  ===============\r\n");
    printf("\r\n====================================================\r\n");
    
    
    printf("\r\n"EOSTEK_PQUPGRADE_CLEAR_STR"\r\n");
    
	uchar mac[]={0xff,0xff,0xff,0xff,0xff,0xff};
	if (getenv (MACADDR_STR)) 
    {
		eth_getenv_enetaddr(MACADDR_STR, mac);
    }
    printf("\r\n"EOSTEK_MACADDR_STR"%02x%02x%02x%02x%02x%02x\r\n",mac[0],mac[1],mac[2],mac[3],mac[4],mac[5]);
    
    tmp = getenv(SYSTEM_Ver_STR);
    printf("\r\n"EOSTEK_SYSTEM_Ver_STR"%s\r\n",tmp);
    
    tmp = getenv(MTA_Check_STR);
    printf("\r\n"EOSTEK_MTA_Check_STR"%s\r\n",tmp);
    
    tmp = getenv(MTA_Reset_STR);
    printf("\r\n"EOSTEK_MTA_Reset_STR"%s\r\n",tmp);

    tmp = getenv(XShuaiUFO_300LM);
    printf("\r\n"EOSTEK_XShuaiUFO_300LM"%s\r\n",tmp);

    tmp = getenv(PQUPGRADE_Ver_STR);
    printf("\r\n"EOSTEK_PQUPGRADE_Ver_STR"%s\r\n",tmp);

    printf("\r\n====================================================\r\n");
    return 0;
}


#if defined(CONFIG_HARD_I2C) || defined(CONFIG_SOFT_I2C)
static int init_func_i2c (void)
{
	puts ("I2C:   ");
	i2c_init (CONFIG_SYS_I2C_SPEED, CONFIG_SYS_I2C_SLAVE);
	puts ("ready\n");
	return (0);
}
#endif

#if defined(CONFIG_CMD_PCI) || defined (CONFIG_PCI)
#include <pci.h>
static int arm_pci_init(void)
{
	pci_init();
	return 0;
}
#endif /* CONFIG_CMD_PCI || CONFIG_PCI */

/*
 * Breathe some life into the board...
 *
 * Initialize a serial port as console, and carry out some hardware
 * tests.
 *
 * The first part of initialization is running from Flash memory;
 * its main purpose is to initialize the RAM so that we
 * can relocate the monitor code to RAM.
 */

/*
 * All attempts to come up with a "common" initialization sequence
 * that works for all boards and architectures failed: some of the
 * requirements are just _too_ different. To get rid of the resulting
 * mess of board dependent #ifdef'ed code we now make the whole
 * initialization sequence configurable to the user.
 *
 * The requirements for any new initalization function is simple: it
 * receives a pointer to the "global data" structure as it's only
 * argument, and returns an integer return code, where 0 means
 * "continue" and != 0 means "fatal error, hang the system".
 */
typedef int (init_fnc_t) (void);

int print_cpuinfo (void);

void __dram_init_banksize(void)
{
	gd->bd->bi_dram[0].start = CONFIG_SYS_SDRAM_BASE;
	gd->bd->bi_dram[0].size =  gd->ram_size;
}
void dram_init_banksize(void)
	__attribute__((weak, alias("__dram_init_banksize")));

init_fnc_t *init_sequence[] = {
#if defined(CONFIG_ARCH_CPU_INIT)
	arch_cpu_init,		/* basic arch cpu dependent setup */
#endif
#if defined(CONFIG_BOARD_EARLY_INIT_F)
	board_early_init_f,
#endif
	timer_init,		/* initialize timer */
#ifdef CONFIG_FSL_ESDHC
	get_clocks,
#endif
	env_init,		/* initialize environment */
	init_baudrate,		/* initialze baudrate settings */
	serial_init,		/* serial communications setup */
	console_init_f,		/* stage 1 init of console */
#if defined(CONFIG_HARD_I2C) || defined(CONFIG_SOFT_I2C)
	init_func_i2c,
#endif
	dram_init,		/* configure available RAM banks */
	NULL,
};

#if defined(CONFIG_MULTICORES_PLATFORM)
#include <multicores/multicores.h>
#include <asm/multicores_platform.h>

#include <asm/io.h>
//#include <MsOS.h>

int magic_number_cleaned = 0;

extern void init_secondary_cpu(void);
void secondary_start_uboot(void)
{
	printf("%s() init_secondary_cpu = %x\n", __func__, (unsigned int)init_secondary_cpu);
#if 0
	*((volatile unsigned int *)(0x16000000 + 0x1000 + 0x0000)) = 0x00000000;
	*((volatile unsigned int *)(0x16000000 + 0x2000 + 0x0000)) = 0x00000000;
	*((volatile unsigned int *)(0x16000000 + 0x2000 + 0x0004)) = 0x000000f0;
	*((volatile unsigned int *)(0x16000000 + 0x1000 + 0x0180)) = 0xffff0000;
	*((volatile unsigned int *)(0x16000000 + 0x1000 + 0x0100)) = 0x0000ffff;
	*((volatile unsigned int *)(0x16000000 + 0x1000 + 0x0F00)) = 0x000e0001;
	*((volatile unsigned int *)(0x16000000 + 0x2000 + 0x0000)) = 0x00000001;
	*((volatile unsigned int *)(0x16000000 + 0x1000 + 0x0000)) = 0x00000001;
#endif
	writel((unsigned int)init_secondary_cpu, CONFIG_KERNEL_START_ADDRESS + SMP_DUMMY_PA_START);
	writel(0xbabe, CONFIG_KERNEL_START_ADDRESS + SMP_DUMMY_MAGIC);
	flush_cache_all();
#if 1
	printf("REG_GIC_DIST_BASE_ADDRESS + 0x0000 => %x\n",  *((volatile unsigned int *)(0x16000000 + 0x1000 + 0x0000)));
	printf("REG_GIC_CPU_BASE_ADDRESS + 0x0000 => %x\n",  *((volatile unsigned int *)(0x16000000 + 0x2000 + 0x0004)));
	printf("REG_GIC_DIST_BASE_ADDRESS + 0x0180 => %x\n",  *((volatile unsigned int *)(0x16000000 + 0x1000 + 0x0180)));
	printf("REG_GIC_DIST_BASE_ADDRESS + 0x0100 => %x\n",  *((volatile unsigned int *)(0x16000000 + 0x1000 + 0x0100)));
	printf("REG_GIC_CPU_BASE_ADDRESS + 0x0000 => %x\n",  *((volatile unsigned int *)(0x16000000 + 0x2000 + 0x0000)));
	printf("REG_GIC_DIST_BASE_ADDRESS + 0x0F00 => %x\n",  *((volatile unsigned int *)(0x16000000 + 0x1000 + 0x0F00)));
#endif
	return;
}

void secondary_start_uboot_cleanup(void)
{
	printf("%s\n", __func__);
	writel(0x0, CONFIG_KERNEL_START_ADDRESS + SMP_DUMMY_PA_START);
	writel(0x0, CONFIG_KERNEL_START_ADDRESS + SMP_DUMMY_MAGIC);
	magic_number_cleaned = 1;
	flush_cache_all();
	return;
}

#endif //CONFIG_MULTICORES_PLATFORM

void board_init_f (ulong bootflag)
{
	bd_t *bd;
	init_fnc_t **init_fnc_ptr;
	gd_t *id;
	ulong addr, addr_sp, relocaddr;
    extern void set_irq_sp(ulong);

	/* Pointer is writable since we allocated a register for it */
#ifdef CONFIG_DYNAMIC_RELOCATE
	ulong sp_addr;
    u32 *dram_size = (u32 *)RELOCATION_INFO_ADDRESS;
    sp_addr = CONFIG_SYS_SDRAM_BASE + (*dram_size -1)*0x100000;
	gd = (gd_t *) (sp_addr);
#else
    gd = (gd_t *) ((CONFIG_SYS_INIT_SP_ADDR) & ~0x07);
#endif

	/* compiler optimization barrier needed for GCC >= 3.4 */
	__asm__ __volatile__("": : :"memory");

	memset ((void*)gd, 0, sizeof (gd_t));

#ifdef CONFIG_MSTAR_STR_MINISIZE
	gd->mon_len = uboot2_ofs+_bss_end_ofs;
#else
    gd->mon_len = _bss_end_ofs;
#endif

	for (init_fnc_ptr = init_sequence; *init_fnc_ptr; ++init_fnc_ptr) {
		if ((*init_fnc_ptr)() != 0) {
			hang ();
		}
	}

        //printf("\n\rHello U-Boot\n");
	debug ("monitor len: %08lX\n", gd->mon_len);

	/*
	 * Ram is setup, size stored in gd !!
	 */
	debug ("ramsize: %08lX\n", gd->ram_size);
#if defined(CONFIG_SYS_MEM_TOP_HIDE)
	/*
	 * Subtract specified amount of memory to hide so that it won't
	 * get "touched" at all by U-Boot. By fixing up gd->ram_size
	 * the Linux kernel should now get passed the now "corrected"
	 * memory size and won't touch it either. This should work
	 * for arch/ppc and arch/powerpc. Only Linux board ports in
	 * arch/powerpc with bootwrapper support, that recalculate the
	 * memory size from the SDRAM controller setup will have to
	 * get fixed.
	 */
	gd->ram_size -= CONFIG_SYS_MEM_TOP_HIDE;
#endif

	addr = CONFIG_SYS_SDRAM_BASE + gd->ram_size;

#ifdef CONFIG_LOGBUFFER
#ifndef CONFIG_ALT_LB_ADDR
	/* reserve kernel log buffer */
	addr -= (LOGBUFF_RESERVE);
	debug ("Reserving %dk for kernel logbuffer at %08lx\n", LOGBUFF_LEN, addr);
#endif
#endif

#ifdef CONFIG_PRAM
	/*
	 * reserve protected RAM
	 */
	i = getenv_r ("pram", (char *)tmp, sizeof (tmp));
	reg = (i > 0) ? simple_strtoul ((const char *)tmp, NULL, 10) : CONFIG_PRAM;
	addr -= (reg << 10);		/* size is in kB */
	debug ("Reserving %ldk for protected RAM at %08lx\n", reg, addr);
#endif /* CONFIG_PRAM */

#if !(defined(CONFIG_SYS_NO_ICACHE) && defined(CONFIG_SYS_NO_DCACHE))
	/* reserve TLB table */
	addr -= (4096 * 4);

	/* round down to next 64 kB limit */
	addr &= ~(0x10000 - 1);

	gd->tlb_addr = addr;
	debug ("TLB table at: %08lx\n", addr);
	/*Setup page table, and enable MMU*/
	relocaddr = addr;
	relocaddr &= ~(4096 - 1);
	relocaddr -= gd->mon_len;
	relocaddr &= ~(0x100000 - 1);
	enable_mmu(gd->tlb_addr, relocaddr);
#endif

	/* round down to next 4 kB limit */
	addr &= ~(4096 - 1);
	debug ("Top of RAM usable for U-Boot at: %08lx\n", addr);

#ifdef CONFIG_LCD
#ifdef CONFIG_FB_ADDR
	gd->fb_base = CONFIG_FB_ADDR;
#else
	/* reserve memory for LCD display (always full pages) */
	addr = lcd_setmem (addr);
	gd->fb_base = addr;
#endif /* CONFIG_FB_ADDR */
#endif /* CONFIG_LCD */

	/*
	 * reserve memory for U-Boot code, data & bss
	 * round down to next 1 MB limit
	 */
	addr -= gd->mon_len;
	addr &= ~(0x100000 - 1);

	debug ("Reserving %ldk for U-Boot at: %08lx\n", gd->mon_len >> 10, addr);

#ifndef CONFIG_PRELOADER
	/*
	 * reserve memory for malloc() arena
	 */
	addr_sp = addr - TOTAL_MALLOC_LEN;
	debug ("Reserving %dk for malloc() at: %08lx\n",
			TOTAL_MALLOC_LEN >> 10, addr_sp);
	/*
	 * (permanently) allocate a Board Info struct
	 * and a permanent copy of the "global" data
	 */
	addr_sp -= sizeof (bd_t);
	bd = (bd_t *) addr_sp;
	gd->bd = bd;
	debug ("Reserving %zu Bytes for Board Info at: %08lx\n",
			sizeof (bd_t), addr_sp);
	addr_sp -= sizeof (gd_t);
	id = (gd_t *) addr_sp;
	debug ("Reserving %zu Bytes for Global Data at: %08lx\n",
			sizeof (gd_t), addr_sp);

	/* setup stackpointer for exeptions */
	gd->irq_sp = addr_sp;
	set_irq_sp(gd->irq_sp);
#ifdef CONFIG_USE_IRQ
	addr_sp -= (CONFIG_STACKSIZE_IRQ+CONFIG_STACKSIZE_FIQ);
	debug ("Reserving %zu Bytes for IRQ stack at: %08lx\n",
		CONFIG_STACKSIZE_IRQ+CONFIG_STACKSIZE_FIQ, addr_sp);
#endif
	/* leave 3 words for abort-stack    */
	addr_sp -= 12;

	/* 8-byte alignment for ABI compliance */
	addr_sp &= ~0x07;
#else
	addr_sp += 128;	/* leave 32 words for abort-stack   */
	gd->irq_sp = addr_sp;
#endif

	debug ("New Stack Pointer is: %08lx\n", addr_sp);

#ifdef CONFIG_POST
	post_bootmode_init();
	post_run (NULL, POST_ROM | post_bootmode_get(0));
#endif

	gd->bd->bi_baudrate = gd->baudrate;
	gd->relocaddr = addr;
	gd->start_addr_sp = addr_sp;
	gd->reloc_off = addr - _TEXT_BASE;
	printf ("\nRelocation Offset is: %08lx\n", gd->reloc_off);
	memcpy (id, (void *)gd, sizeof (gd_t));

#if defined(CONFIG_MULTICORES_PLATFORM)
	writel(gd->tlb_addr, CONFIG_KERNEL_START_ADDRESS+SMP_DUMMY_TTB0);
#endif

#ifdef CONFIG_MSTAR_STR_MINISIZE
    relocate_code (addr_sp, id, addr, (ulong)board_init_r);
#else
	relocate_code (addr_sp, id, addr);
#endif
	/* NOTREACHED - relocate_code() does not return */
}

#if !defined(CONFIG_SYS_NO_FLASH)
static char *failed = "*** failed ***\n";
#endif

#ifdef CONFIG_MSTAR_STR_MINISIZE
void init_cmd_tbl(void)
{
    u_boot_cmd_tbl_start=&__u_boot_cmd_start;
    u_boot_cmd_tbl_end=&__u_boot_cmd_end;
}
#endif
/************************************************************************
 *
 * This is the next part if the initialization sequence: we are now
 * running from RAM and have a "normal" C environment, i. e. global
 * data can be written, BSS has been cleared, the stack size in not
 * that critical any more, etc.
 *
 ************************************************************************
 */

#if defined(CONFIG_MULTICORES_PLATFORM)
#define SCU_CTRL        0x00
#define SCU_CONFIG      0x04
#define SCU_CPU_STATUS      0x08

void scu_enable(void *scu_base)
{
	u32 scu_ctrl;
	u32 ucpuid;

	ucpuid = get_cpu_id();
	//scu_ctrl = __raw_readl(scu_base + SCU_CTRL);
	scu_ctrl = (*(volatile unsigned int *)(scu_base + SCU_CTRL));

	/* already enabled? */
	if (scu_ctrl & 1)
		return;

	scu_ctrl |= 1;
	//__raw_writel(scu_ctrl, scu_base + SCU_CTRL);
	*(volatile unsigned int *)(scu_base + SCU_CTRL) = scu_ctrl;
	//polll[ucpuid+5] = *(volatile unsigned int *)(scu_base + SCU_CTRL);
	/*
	 * Ensure that the data accessed by CPU0 before the SCU was
	 * initialised is visible to the other CPUs.
	 */
	flush_cache_all();
}

typedef void (*CoreWakeupFunc ) (void);
volatile unsigned int GlobalActive_Area[NR_CPUS] = {0,};
volatile CoreWakeupFunc ActiveFuncPointer[NR_CPUS] = {0,};

extern void __init_dic(void);
extern void __init_interrupts(void);
extern void set_irq_sp(ulong);

void secondary_init_r(void)
{
	u32 cpuID = get_cpu_id();
	unsigned int sp_addr;

	printf("%s(): cpu[%d] %d\n", __func__, cpuID, __LINE__);

	if (cpuID >= NR_CPUS) {
		printf("Error!!, Incorrect CPUID %d\n", cpuID);
		while(1);
	}

	scu_enable((void *)(CHIP_MPCORE_SCU)); // SCU PA = 0x16004000
	writel(SMP_DBG_IN_C_CODE, SMP_RIU_BASE + (SMP_DUMMY_BANK<<1) + cpuID*4);

	sp_addr = *((unsigned int *)(CONFIG_KERNEL_START_ADDRESS+SMP_DUMMY_SP));
	sp_addr = sp_addr - (2+cpuID)*0x4000;
	set_irq_sp(sp_addr);

	__init_dic();
	__init_interrupts();
	//cyli remove
	//    CHIP_interrupt_unmask(E_INTERRUPT_IRQ);

	// enable Timer
	//cyli remove
	//    MAsm_CPU_TimerInit();
	//    MAsm_CPU_TimerStart();

	flush_cache_all();

	enable_interrupts();
	while(1) {
		if((GlobalActive_Area[cpuID] == 1) && (ActiveFuncPointer[cpuID] != 0))
			ActiveFuncPointer[cpuID]();
	}
}

void Core_Wakeup(CoreWakeupFunc __Addr, u32 CoreID)
{
	if (CoreID >= NR_CPUS) {
		printf("Error!! in Core_Wakeup, Not correct CPUID %d\n", CoreID);
		while(1);
	}

	if (GlobalActive_Area[CoreID] == 1) {
		printf("Core %d is active\n", CoreID);
		return;
	}

	//printf("Wake UP Core %d, PC %x\n", CoreID, (unsigned int)__Addr);
	//delayms(100);  // Wait printf finish (Critical Section problem. Need multi core lock)
	ActiveFuncPointer[CoreID] =  __Addr;
	GlobalActive_Area[CoreID] = 1;
	flush_cache_all();
}

void multicores_init(void)
{
	int perCPU;

	for(perCPU = 1; perCPU < NR_CPUS; perCPU++)
		Core_Wakeup(multicores_handle_cmd, perCPU);
}

void multicores_release_non_boot_cores(void)
{
#define SMP_WAIT_NON_BOOT_CPU_RELEASE_COUNT 10000
//	unsigned int i = 0;
	unsigned int perCPU;

	printf("%s(): check all cmd done... ", __func__);
	for(perCPU = 1; perCPU < NR_CPUS; perCPU++)
		while (!multicores_check_all_cmd_done(perCPU))
			;
	printf("done!\n");

	secondary_start_uboot_cleanup();

	multicores_release_all_smp_cpu();

//	printf("%s(): check smp cpu released... ", __func__);
	while(!multicores_check_smp_cpu_released())
	{
#if 0
		if (i > SMP_WAIT_NON_BOOT_CPU_RELEASE_COUNT) {
			printf("%s(): timeout!\n", __func__);
			do_reset(NULL, 0, 0, NULL);  //just in case, should never be here!
		}
		i++;
#endif
	}
//	printf("done!\n");
}
#endif //CONFIG_MULTICORES_PLATFORM

unsigned int u32UbootStart = 0;
unsigned int u32UbootEnd = 0;
void board_init_r (gd_t *id, ulong dest_addr)
{
	char *s;
	bd_t *bd;
	ulong malloc_start;
#if !defined(CONFIG_SYS_NO_FLASH)
	ulong flash_size;
#endif

    gd = id;
	bd = gd->bd;

	gd->flags |= GD_FLG_RELOC;	/* tell others: relocation done */
#ifdef CONFIG_MSTAR_STR_MINISIZE
    init_cmd_tbl();
#endif
    display_banner();		/* say that we are here */

	/* The Malloc area is immediately below the monitor copy in DRAM */
	malloc_start = dest_addr - TOTAL_MALLOC_LEN;
#if defined(CONFIG_MULTICORES_PLATFORM)
	const unsigned int non_boot_core_svc_irq_num = (NR_CPUS-1) * 2;
	ulong malloc_end;
	unsigned int core_num=0;

	mem_malloc_init (malloc_start, TOTAL_MALLOC_LEN-(0x4000*non_boot_core_svc_irq_num));

	//printf("Reserve all cores stack by digging malloc: TOTAL_MALLOC_LEN after = %x\n", TOTAL_MALLOC_LEN-0x4000*non_boot_core_svc_irq_num);
	//printf("The end of the malloc addr = %x\n", malloc_start+TOTAL_MALLOC_LEN-0x4000*non_boot_core_svc_irq_num);

	malloc_end = malloc_start+TOTAL_MALLOC_LEN;

	/*Keep the stack address for other cores*/
	writel(malloc_end, CONFIG_KERNEL_START_ADDRESS+SMP_DUMMY_SP);

	/*
	   Here we put a specific number to the end of the stack.
	       badbeef1: at the end of sp_svc for core1
	       badbeef2: at the end of sp_svc for core2
	       badbeef3: at the end of sp_svc for core3
	       badbeef4: at the end of sp_irq for core1
	       badbeef5: at the end of sp_irq for core2
	       badbeef6: at the end of sp_irq for core3
	 */
	for (core_num=1; core_num<=non_boot_core_svc_irq_num; core_num++)
	{
		malloc_end-=(0x4000);
		*((unsigned int *)(malloc_end)) = 0xbadbeef0|core_num;
		//printf("Put boundary badbeef to = %x\n", malloc_end);
	}
#if 0
	unsigned int ttb_check;

	asm volatile ("mrc p15, 0, %0, c2, c0, 0"
			: "=r" (ttb_check)
			:
			: "memory");
	printf("check TLB %x gd TlB %x\n", ttb_check, gd->tlb_addr);
#endif

#else
	mem_malloc_init (malloc_start, TOTAL_MALLOC_LEN);
#endif //CONFIG_MULTICORES_PLATFORM

    /*MSTAR start*/
    MstarSysInit();
#if defined(CONFIG_MULTICORES_PLATFORM)
	scu_enable((void *)(CHIP_MPCORE_SCU));
	secondary_start_uboot();
	multicores_init();
#endif //CONFIG_MULTICORES_PLATFORM
    u32UbootStart = id->start_addr_sp;
    u32UbootEnd = CONFIG_SYS_SDRAM_BASE+id->ram_size;
    printf("\nuboot held at [%08x~%08x],size=%08x\n",
            u32UbootStart,
            u32UbootEnd,
            u32UbootEnd-u32UbootStart);
    /* MSTAR end*/
#ifdef CONFIG_MSTAR_STR_MINISIZE
    monitor_flash_len = uboot2_ofs+_end_ofs;
#else
	monitor_flash_len = _end_ofs;
#endif

#if defined(CONFIG_DISPLAY_CPUINFO)
	print_cpuinfo();		/* display cpu info (and speed) */
#endif
#if defined(CONFIG_DISPLAY_BOARDINFO)
	checkboard();		/* display board info */
#endif

    /* Ram ist board specific, so move it to board code ... */
	dram_init_banksize();
	display_dram_config();	/* and display it */
	debug ("monitor flash len: %08lX\n", monitor_flash_len);
	board_init();	/* Setup chipselects */

#ifdef CONFIG_SERIAL_MULTI
	serial_initialize();
#endif

	printf ("Now running in RAM - U-Boot at: %08lx\n", dest_addr);

#ifdef CONFIG_LOGBUFFER
	logbuff_init_ptrs ();
#endif
#ifdef CONFIG_POST
	post_output_backlog ();
#endif

#if !defined(CONFIG_SYS_NO_FLASH)
	puts ("Flash: ");

	if ((flash_size = flash_init ()) > 0) {
# ifdef CONFIG_SYS_FLASH_CHECKSUM
		print_size (flash_size, "");
		/*
		 * Compute and print flash CRC if flashchecksum is set to 'y'
		 *
		 * NOTE: Maybe we should add some WATCHDOG_RESET()? XXX
		 */
		s = getenv ("flashchecksum");
		if (s && (*s == 'y')) {
			printf ("  CRC: %08X",
				crc32 (0, (const unsigned char *) CONFIG_SYS_FLASH_BASE, flash_size)
			);
		}
		putc ('\n');
# else	/* !CONFIG_SYS_FLASH_CHECKSUM */
		print_size (flash_size, "\n");
# endif /* CONFIG_SYS_FLASH_CHECKSUM */
	} else {
		puts (failed);
		hang ();
	}
#endif

#if defined(CONFIG_CMD_NAND)
	puts ("NAND:  ");
	nand_init();		/* go init the NAND */
#endif

#if defined(CONFIG_CMD_ONENAND)
	onenand_init();
#endif

#ifdef CONFIG_HAS_DATAFLASH
	AT91F_DataflashInit();
	dataflash_print_info();
#endif
#if defined(CONFIG_GENERIC_MMC) && defined(CONFIG_ENV_IS_IN_MMC)
	//puts("MMC:	");
	mmc_initialize(bd);
#endif

	/* initialize environment */
	env_relocate ();

#if defined(CONFIG_GENERIC_MMC) && defined(CONFIG_ENV_IS_IN_SPI_FLASH)
	//puts("MMC:	");
	mmc_initialize(bd);
#endif

#if defined(CONFIG_SD)
	printf("sd_initialize [ start ]\n");
	sd_initialize(bd);
	printf("sd_initialize [  end  ]\n");
#endif

#if defined(CONFIG_CMD_PCI) || defined(CONFIG_PCI)
	arm_pci_init();
#endif

	/* IP Address */
	gd->bd->bi_ip_addr = getenv_IPaddr ("ipaddr");

	stdio_init ();	/* get the devices list going. */

	jumptable_init ();

#if defined(CONFIG_API)
	/* Initialize API */
	api_init ();
#endif

    // EosTek Patch Begin
    // Display info for produce check
    display_EOS_Info();
    // EosTek Patch End

	console_init_r ();	/* fully init console as a device */

#if defined(CONFIG_ARCH_MISC_INIT)
	/* miscellaneous arch dependent initialisations */
	arch_misc_init ();
#endif
#if defined(CONFIG_MISC_INIT_R)
	/* miscellaneous platform dependent initialisations */
	misc_init_r ();
#endif

	/* set up exceptions */
	interrupt_init ();
	/* enable exceptions */
	enable_interrupts ();

	/* Perform network card initialisation if necessary */
#if defined(CONFIG_DRIVER_SMC91111) || defined (CONFIG_DRIVER_LAN91C96)
	/* XXX: this needs to be moved to board init */
	if (getenv ("ethaddr")) {
		uchar enetaddr[6];
		eth_getenv_enetaddr("ethaddr", enetaddr);
		smc_set_mac_addr(enetaddr);
	}
#endif /* CONFIG_DRIVER_SMC91111 || CONFIG_DRIVER_LAN91C96 */

	/* Initialize from environment */
	if ((s = getenv ("loadaddr")) != NULL) {
		load_addr = simple_strtoul (s, NULL, 16);
	}
#if defined(CONFIG_CMD_NET)
	if ((s = getenv ("bootfile")) != NULL) {
		copy_filename (BootFile, s, sizeof (BootFile));
	}
#endif

#ifdef BOARD_LATE_INIT
	board_late_init ();
#endif

#ifdef CONFIG_BITBANGMII
	bb_miiphy_init();
#endif
#if defined(CONFIG_CMD_NET)
#if defined(CONFIG_NET_MULTI)
	puts ("Net:   ");
#endif
	eth_initialize(gd->bd);
#if defined(CONFIG_RESET_PHY_R)
	debug ("Reset Ethernet PHY\n");
	reset_phy();
#endif
#endif

#ifdef CONFIG_POST
	post_run (NULL, POST_RAM | post_bootmode_get(0));
#endif

#if defined(CONFIG_PRAM) || defined(CONFIG_LOGBUFFER)
	/*
	 * Export available size of memory for Linux,
	 * taking into account the protected RAM at top of memory
	 */
	{
		ulong pram;
		uchar memsz[32];
#ifdef CONFIG_PRAM
		char *s;

		if ((s = getenv ("pram")) != NULL) {
			pram = simple_strtoul (s, NULL, 10);
		} else {
			pram = CONFIG_PRAM;
		}
#else
		pram=0;
#endif
#ifdef CONFIG_LOGBUFFER
#ifndef CONFIG_ALT_LB_ADDR
		/* Also take the logbuffer into account (pram is in kB) */
		pram += (LOGBUFF_LEN+LOGBUFF_OVERHEAD)/1024;
#endif
#endif
		sprintf ((char *)memsz, "%ldk", (bd->bi_memsize / 1024) - pram);
		setenv ("mem", (char *)memsz);
	}
#endif

    if(-1==MstarDrvInit())
    {
        printf("Error: MstarInit() \n");
    }
    printf("[AT][MBoot][Driver Init][%lu]\n", MsSystemGetBootTime());

	/* main_loop() can return to retry autoboot, if so just run it again. */

#ifdef CONFIG_DYNAMIC_RELOCATE
    #ifdef DYNAMIC_RELOCATE_BIST
        u32 *dram_size = (u32 *)(RELOCATION_INFO_ADDRESS + 0x20000000);
        *dram_size = 151;
        Chip_Flush_Memory();
    #endif
#endif

#if (ENABLE_MODULE_NAND_FLASH == 1)
	extern U32 nand_MTDMarkBad(U32 u32_GoodBlkIdx);
	extern int nand_ShiftBlock_Recover(U32 (*nand_markbad)(U32));
	nand_ShiftBlock_Recover(nand_MTDMarkBad);
#endif

	for (;;) {
		main_loop ();
	}

	/* NOTREACHED - no way out of command loop except booting */
}

void hang (void)
{
	puts ("### ERROR ### Please RESET the board ###\n");
	for (;;);
}
