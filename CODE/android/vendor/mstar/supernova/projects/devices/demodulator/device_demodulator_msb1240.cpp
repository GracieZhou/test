/*@ <FileComment ID=1246257763790> @*/
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
 * Class : mapi_demodulator_msb1240
 * File  : mapi_demodulator_msb1240.cpp
 **********************************************************************/
/*@ </FileComment ID=1246257763790> @*/
/*@ <Include> @*/
#include <math.h>
#include <unistd.h>
#include "MsCommon.h"
#include "debug.h"
#include "drvGPIO.h"
//#include "apiSWI2C.h"
#include "mapi_tuner.h"
#include "mapi_demodulator.h"
#include "mapi_i2c.h"
#include "mapi_i2c_devTable.h"
#include "mapi_gpio.h"
#include "mapi_gpio_devTable.h"
#include "mapi_interface.h"
#if (STR_ENABLE == 1)
#include <sys/prctl.h>
#include "mapi_str.h"
#endif
#include "device_pcb.h"
#include "device_demodulator_extend.h"
#include "device_demodulator_msb1240.h"
MAPI_U8 MSB1240_LIB_U01[]={
#include "msb124x_dvbt2.dat"
};
#if (MSPI_ENABLE==1)
#include "mapi_mspi.h"
#include "mapi_mspi_devTable.h"
#endif
#include "drvMMIO.h"
#include "drvHWI2C.h"
#include "drvMSPI.h"
#include "drvSYS.h"
#include "mapi_demodulator_datatype.h"
#include "mapi_system.h"
#include "mapi_pcb.h"
#include "mapi_vif.h"
#include "mapi_system.h"
#include "mapi_syscfg_table.h"
#include "SystemInfo.h"
#include "drvDMD_VD_MBX.h"
//#include "device_atsc_t3.cpp"
#include "mapi_sar.h"
#include "drvSAR.h"

#include "mapi_panel.h"
#include "mapi_utility.h"
#if (DVBS_SYSTEM_ENABLE == 1)
#include "device_dish.h"
#include "mapi_dish.h"

static  MAPI_U32                      _u32CurrentFreq=0;
static  MAPI_U32                      _u32CurrentSR=0;
static  MAPI_U32                      _u32LocktimeStart=0;
static  MAPI_U8                       _u8LocktimeFlag=0;
static  MAPI_U8                       _u8ToneBurstFlag=0;



static  MAPI_U16                      _u16_packetError=0;
static  float                         _fPostBer=0;
static  float                         _f_DVBS2_CurrentSNR=0;

extern MAPI_BOOL MDrv_DVBS_Tuner_SetFreq(MAPI_U16 u16CenterFreq_MHz, MAPI_U32 u32SymbolRate_Ks);
extern MAPI_BOOL MDrv_DVBS_Tuner_Initial(void);
extern MAPI_BOOL MDrv_DVBS_Tuner_CheckLock(void);



// input IF_AGC,
// return dBm.
extern float MDrv_DVBS_Tuner_Get_RSSI(MAPI_U16 u16_gain, MAPI_U8 Dtype);

#define MSB1240_DVBS_TUNER_WAIT_TIMEOUT  (50)
#define MSB1240_DEMOD_WAIT_TIMEOUT  (5000)
#define MSB1240_DVBS_ADCPLL_IQ_SWAP          0 //1 is ADCPLL IQ swap enable, for customer issue.
static  MAPI_BOOL                     _bDemodType=MAPI_FALSE;


#define TR_TIME_OUT_WITH_FIX_SYMBOL_RATE 1000
#endif

#if (DVBS_SYSTEM_ENABLE == 1)
//blind scan
#define MSB124X_DEMOD_WAIT_TIMEOUT      (6000)
#define REG_FSM_EN_S                    0x0CB8
static  MAPI_U16                        _u16BlindScanStartFreq =0;
static  MAPI_U16                        _u16BlindScanEndFreq   =0;
static  MAPI_U16                        _u16TunerCenterFreq    =0;
static  MAPI_U16                        _u16ChannelInfoIndex    =0;
//Debug Only+
static  MAPI_U16                      _u16NextCenterFreq=0;
static  MAPI_U16                      _u16LockedSymbolRate=0;
static  MAPI_U16                      _u16LockedCenterFreq=0;
static  MAPI_U16                      _u16PreLockedHB=0;
static  MAPI_U16                      _u16PreLockedLB=0;
static  MAPI_U16                      _u16CurrentSymbolRate=0;
static  MAPI_S16                      _s16CurrentCFO=0;
static  MAPI_U16                      _u16CurrentStepSize=0;
static  MAPI_U16                      _u16ChannelInfoArray[2][1000];
mapi_demodulator_datatype::EN_BLINDSCAN_STATUS eBlindScanStatue = mapi_demodulator_datatype::E_BLINDSCAN_NOTREADY;
#endif

/*@ </Include> @*/
/*@ <Definitions> @*/
#define ERR_DEMOD_MSB(x)    x
#define DBG_DEMOD_MSB(x)       // x
#define DBG_DEMOD_FLOW(x)      // x
#define DBG_GET_SIGNAL(x)      // x
#define DBG_DEMOD_LOAD_I2C(x)  // x
#define DBG_DEMOD_CHECKSUM(x)  // x
#define DBG_FLASH_WP(x)        // x
#define DBG_DUMP_LOAD_DSP_TIME 0
#define MDrv_Timer_Delayms OS_DELAY_TASK
#define MDrv_Timer_GetTime0 OS_SYSTEM_TIME

#define TS_CLK_INV 1  
#define TS_DATA_SWAP 0
#if (TS_DATA_SERIAL_ENABLE == 1)
#define TS_DATA_SERIAL 1
#else
#define TS_DATA_SERIAL 0
#endif

#if (PRELOAD_DSP_CODE_FROM_MAIN_CHIP_I2C_ENABLE==1)
#define PRELOAD_DSP_CODE_FROM_MAIN_CHIP_I2C  1
#else
#define PRELOAD_DSP_CODE_FROM_MAIN_CHIP_I2C  0
#endif
#if (PRELOAD_DSP_CODE_FROM_MAIN_CHIP_I2C_ONLY_LOAD_T2_ENABLE==1)
#define PRELOAD_DSP_CODE_FROM_MAIN_CHIP_I2C_ONLY_LOAD_T2  1
#else
#define PRELOAD_DSP_CODE_FROM_MAIN_CHIP_I2C_ONLY_LOAD_T2  0
#endif
#if (LOAD_DSP_CODE_FROM_MAIN_CHIP_I2C_ENABLE==1)
#define LOAD_DSP_CODE_FROM_MAIN_CHIP_I2C 1
#else
#define LOAD_DSP_CODE_FROM_MAIN_CHIP_I2C 0
#endif
#if (MSPI_ENABLE==1)
#define USE_SPI_LOAD_TO_SDRAM  1
#define NEED_SWITCH_TSPAD_FOR_MSPI_USE  1
#else //I2C
#define USE_SPI_LOAD_TO_SDRAM  0
#define NEED_SWITCH_TSPAD_FOR_MSPI_USE  0
#endif
#if (STB_ENABLE == 1)
#define MSB1240_SELECT_IF_INPUT     1  // 0:dvbt_I, 1:dvbs_I
#else
#define MSB1240_SELECT_IF_INPUT     0  // 0:dvbt_I, 1:dvbs_I
#endif


#define SDRAM_DATA_CHECK                 0
#define LOAD_CODE_I2C_BLOCK_NUM          0x80
#define TIMING_VERIFICATION              0
#define SDRAM_BASE                       0x5000
#define SPI_DEVICE_BUFFER_SIZE           256

// msb1240 flash size: 1Mbits
#define MAX_MSB1240_LIB_LEN              262144//131072
#define FLASH_WP_ENABLE                  1

#define MSB1240_BOOT  0x01
#define MSB1240_DVBT2 0x02
#define MSB1240_DVBT  0x04
#define MSB1240_DVBC  0x08
#define MSB1240_DVBS  0x10
#define MSB1240_ALL   0x0F

#define MSB1240_BOOT_START_ADDR     0x00000
#define MSB1240_BOOT_END_ADDR       0x00FFF
#define MSB1240_DVBT2_P1_START_ADDR 0x01000
#define MSB1240_DVBT2_P1_END_ADDR   0x08FFF
#define MSB1240_DVBT2_P2_START_ADDR 0x09000
#define MSB1240_DVBT2_P2_END_ADDR   0x0FFFF
#define MSB1240_DVBT_START_ADDR     0x10000
#define MSB1240_DVBT_END_ADDR       0x17FFF
#define MSB1240_DVBC_START_ADDR     0x18000
#define MSB1240_DVBC_END_ADDR       0x1FFFF
#define MSB1240_DVBS_P1_START_ADDR  0x20000
#define MSB1240_DVBS_P1_END_ADDR    0x27FFF
#define MSB1240_DVBS_P2_START_ADDR  0x28000
#define MSB1240_DVBS_P2_END_ADDR    0x28FFF
#define MSB1240_TOP_WR_DBG_90_ADDR  0x0990
#define MSB1240_TOP_WR_DBG_92_ADDR  0x0992
#define MSB1240_TOP_WR_DBG_93_ADDR  0x0993
#define MSB1240_DIG_DBG_5_ADDR  0x0B55
#define MSB1240_DIG_DBG_6_ADDR  0x0B56


#define MSB1240_WINDOWS_BASE                0x100   // unit: 4K  // 0x100*4k base = 0x100000(1M)
#define MSB1240_BOOT_WINDOWS_OFFSET         MSB1240_WINDOWS_BASE    // 1M
#define MSB1240_DVBT2_P2_WINDOWS_OFFSET    (MSB1240_WINDOWS_BASE + 0x08)   // 1M+32k
#define MSB1240_DVBT2_P1_WINDOWS_OFFSET    (MSB1240_DVBT2_P2_WINDOWS_OFFSET + 0x08)   // 1M+64k
#define MSB1240_DVBT_WINDOWS_OFFSET        (MSB1240_DVBT2_P1_WINDOWS_OFFSET + 0x08)   // 1M+96k
#define MSB1240_DVBC_WINDOWS_OFFSET        (MSB1240_DVBT_WINDOWS_OFFSET + 0x08)       // 1M+128k
#define MSB1240_DVBS_P2_WINDOWS_OFFSET     (MSB1240_DVBC_WINDOWS_OFFSET + 0x08)       // 1M+128k+32k
#define MSB1240_DVBS_P1_WINDOWS_OFFSET     (MSB1240_DVBS_P2_WINDOWS_OFFSET + 0x08)    // 1M+128k+64k  

#define    MSB1240_MAX_FLASH_ON_RETRY_NUM 3

#define    MSB1240_SW_SLEEP_MODE_EN 0

// Mail box register address
#define REG_MB_CNTL     0x0C80
#define REG_MB_ADDR_L   0x0C84
#define REG_MB_ADDR_H   0x0C82
#define REG_MB_DATA     0x0C86
#define REG_FSM_EN       0x0CB8
#define MSB1240_MB_CNT_TH 0x7FF

///////////////////////////////////////////////////////
// [DEMOD BANK DEFINITIONS]
/////////////////////////////////////////////////////// 
#define DIG_DBG_BASE    0x0B00  


typedef enum
{
    DEV_MSB1240_FFT_2K      = 0x0,
    DEV_MSB1240_FFT_8K      = 0x1,
    DEV_MSB1240_FFT_4K      = 0x2,
    DEV_MSB1240_FFT_1K      = 0x3,
    DEV_MSB1240_FFT_16K      = 0x4,
    DEV_MSB1240_FFT_32K      = 0x5,
    DEV_MSB1240_FFT_8K_     = 0x6,
    DEV_MSB1240_FFT_32K_    = 0x7,
} DEV_MSB1240_FFT_VAL;

typedef struct
{
    MAPI_U16 u16_x_10cn;
    MAPI_U8 u8_y_sqi;
}S_SQI_TABLE;

#if 0
MAPI_BOOL device_demodulator_extend::MSB1240_demod_info(t2_mod_info* t2_mod_info)
{
    MAPI_U8 reg = 0;
    MAPI_U8 s1_siso_miso = 0;
    MAPI_U8 plp_number = 0;
    MAPI_U8 plp_id = 0;
    MAPI_U8 constellation_l1 = 0;
    MAPI_U8 coderate_l1 = 0; //5
    MAPI_U8 plp_constellation = 0;
    MAPI_U8 plp_rotation_constellation = 0;
    MAPI_U8 plp_coderate = 0;
    MAPI_U8 plp_fec = 0;
    MAPI_U8 plp_frame_il_length = 0; //10
    MAPI_U8 plp_type_time_il = 0;
    MAPI_U8 plp_length_time_il = 0;
    MAPI_U8 pilot_pattern = 0;
    MAPI_U8 mode_carrier = 0;
    MAPI_U8 fef_type = 0; //15
    MAPI_U32 fef_length = 0;
    MAPI_U32 fef_interval = 0;
    MAPI_U8 papr = 0;
    MAPI_U8 guard_interval = 0;

    ReadReg(0x2400+0x19*2,&reg); //TDP'S1
    s1_siso_miso = (reg>>4)&0x07;
//BIT 6:4
    t2_mod_info->s1_siso_miso = s1_siso_miso; //  (1)

    ReadReg(0x2700+0x42*2,&reg); //PARSER
    plp_number = reg; //BIT 7:0
    t2_mod_info->plp_number = plp_number; // (2)

    //ReadDspReg(MAPI_U16 u16Addr,MAPI_U8 * pData); //DSP

    ReadDspReg((MAPI_U16)E_T2_PLP_ID, &reg);
    ReadReg(0x2700+0x45*2,&reg); //PARSER
    plp_id = reg; //BIT 7:0
    t2_mod_info->plp_id = plp_id; // (3)

    ReadReg(0x2700+0x31*2 + 1,&reg); //PARSER
    constellation_l1 = reg&0x0f; //BIT 8:11
    t2_mod_info->constellation_l1 = constellation_l1; // (4)
    coderate_l1 = (reg>>4)&0x03; //BIT 13:12
    t2_mod_info->coderate_l1 = coderate_l1; //(5)

    ReadReg(0x2700+0x47*2,&reg); //PARSER
    plp_coderate = reg&0x07; //BIT 2:0
    t2_mod_info->plp_coderate = plp_coderate; //(8)
    plp_constellation = (reg>>3)&0x07; //BIT 5:3
    t2_mod_info->plp_constellation = plp_constellation; //(6)
    plp_rotation_constellation = (reg>>6)&0x01; //BIT 6:6
    t2_mod_info->plp_rotation_constellation = plp_rotation_constellation; //(7)

    plp_fec = (reg>>7)&0x01; // BIT 8:7
    ReadReg(0x2700+0x47*2+1,&reg); //PARSER
    plp_fec = plp_fec|((reg&0x01)<<1);
    t2_mod_info->plp_fec = plp_fec; //(9)

    ReadReg(0x2700+0x49*2,&reg); //PARSER
    plp_frame_il_length = reg; // BIT 7:0
    t2_mod_info->plp_frame_il_length = plp_frame_il_length; //(10)

    ReadReg(0x2700+0x49*2 + 1,&reg); //BIT 8:15
    plp_length_time_il = reg;
    t2_mod_info->plp_length_time_il = plp_length_time_il; //(11-2)
    ReadReg(0x2700+0x48*2 + 1,&reg);
    plp_type_time_il = (reg>>5)&0x01; //BIT 12:12
    t2_mod_info->plp_type_time_il = plp_type_time_il; //(11-1)

    ReadReg(0x2700+0x36*2,&reg); //PARSER
    pilot_pattern = reg&0x0f; //BIT 3:0
    t2_mod_info->pilot_pattern = pilot_pattern; //(12)

    ReadReg(0x2400+0x19*2,&reg); //TDP' S2
    mode_carrier = reg&0x0f;
//BIT 3:0
    t2_mod_info->mode_carrier = mode_carrier; //  (13)

    ReadReg(0x2700+0x42*2 + 1,&reg); //PARSER
    fef_type = (reg>>4)&0x0f; //BIT 15:12
    t2_mod_info->fef_type = fef_type; // (14-1)

    ReadReg(0x2700+0x44*2 ,&reg); //PARSER
    fef_length = (reg&0x3F);
    ReadReg(0x2700+0x43*2 + 1,&reg);
    fef_length = (fef_length<<8)|reg;
    ReadReg(0x2700+0x43*2,&reg);
    fef_length = (fef_length<<8)|reg;
    t2_mod_info->fef_length = fef_length; // (14-2)

    ReadReg(0x2700+0x44*2 + 1 ,&reg); //PARSER
    fef_interval = (reg>>0)&0xff; //BIT 15:8
    t2_mod_info->fef_interval = fef_interval; // (14-3)

    ReadReg(0x2700+0x31*2,&reg); //PARSER
    guard_interval = (reg>>1)&0x07; //BIT 3:1
    t2_mod_info->guard_interval = guard_interval; // (16)
    papr = (reg>>4)&0x0f;  //BIT 7:4
    t2_mod_info->papr = papr; // (15)
/*
    printf("-[msb1240][mod][1]s1_siso_miso=%d, plp_number=%d, plp_id=%d\n",s1_siso_miso, plp_number, plp_id);
    printf("-[msb1240][mod][2]constellation_l1=0x%x, coderate_l1=0x%x\n",constellation_l1, coderate_l1);
    printf("-[msb1240][mod][3]plp_constellation=0x%x, plp_rotation_constellation=%d, plp_coderate=%d\n", plp_constellation, plp_rotation_constellation, plp_coderate);
    printf("-[msb1240][mod][4]plp_fec=0x%x, plp_frame_il_length=0x%x, plp_type_time_il=0x%x, plp_length_time_il==0x%x\n", plp_fec, plp_frame_il_length, plp_type_time_il, plp_length_time_il);
    printf("-[msb1240][mod][5]pilot_pattern=%d, mode_carrier=0x%x\n", pilot_pattern, mode_carrier);
    printf("-[msb1240][mod][6]fef_type=%d, fef_length=%x, fef_interval=%x\n", fef_type, fef_length, fef_interval);
    printf("-[msb1240][mod][7]papr=0x%x, guard_interval=0x%x\n", papr, guard_interval);
*/
    return true;
}
#endif
/*@ </Definitions> @*/
MAPI_U8 device_demodulator_extend::u8DeviceBusy = 0;
//MAPI_U8 device_demodulator_extend::u8PowerOnInit = 1;

//MAPI_BOOL device_demodulator_extend::m_bSerialOut = MAPI_FALSE;
//MAPI_U8 device_demodulator_extend::gVifTop = VIF_TOP;
//MAPI_U8 device_demodulator_extend::gVifSoundSystem = (MAPI_U8)VIF_SOUND_DK2;
//MAPI_U8 device_demodulator_extend::gVifIfFreq = (MAPI_U8)IF_FREQ_3890;
MAPI_U8 device_demodulator_extend::u8MsbData[6] = {0};
MAPI_U8 device_demodulator_extend::gu8ChipRevId = 0;
//MAPI_U8 device_demodulator_extend::gCalIdacCh0 = 0;
//MAPI_U8 device_demodulator_extend::gCalIdacCh1 = 0;
//S_CMDPKTREG device_demodulator_extend::gsCmdPacket = {0, {0}};
//t2_mod_info device_demodulator_extend::st2_mod_info = {0};    //@@++-- 20120405 Arki


MAPI_U8 device_demodulator_extend::gu8DemoDynamicI2cAddress = DEMOD_DYNAMIC_SLAVE_ID_1; //0x32;//Default
MAPI_BOOL device_demodulator_extend::FECLock = MAPI_FALSE;
MAPI_BOOL device_demodulator_extend::gbTVAutoScanEn = MAPI_FALSE; //init value=MAPI_FALSE, follow with auto/manual scan
mapi_demodulator_datatype::EN_DEVICE_DEMOD_TYPE device_demodulator_extend::m_enCurrentDemodulator_Type = mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2;
MAPI_U8 device_demodulator_extend::use_twin_demod = 0;

static MAPI_U32  u32ChkScanTimeStart = 0;
static MAPI_BOOL bPower_init_en = MAPI_FALSE;
#if (PRELOAD_DSP_CODE_FROM_MAIN_CHIP_I2C_ONLY_LOAD_T2 == 1)
static MAPI_BOOL bPreload_T2_en = MAPI_FALSE;
static MAPI_BOOL bSTR_mode_en = MAPI_FALSE;
#endif
static MAPI_U32  g_u32Frequency = 0;
static MAPI_BOOL p1_ever_lock_flag = 0;
static MAPI_U8   g_u8_bw = 0;

// 0: Normal mode. 
// 1: 1)reg r/w ok. 2)ADC powerdown. 3)MCU rst.
// 2: sleep mode.
static MAPI_U8   g_u8_msb1240_sleep_mode_status = 1;

// 0: Normal mode, ADC on. 
// 1: ADC off.
static MAPI_U8  g_u8_msb1240_adc_mode_status = 0;

#if(TIMING_VERIFICATION == 1)
static MAPI_U32 tmm_1 = 0x00;
static MAPI_U32 tmm_2 = 0x00;
static MAPI_U32 tmm_3 = 0x00;
static MAPI_U32 tmm_4 = 0x00;
static MAPI_U32 tmm_5 = 0x00;
static MAPI_U32 tmm_6 = 0x00;
static MAPI_U32 tmm_7 = 0x00;
static MAPI_U32 tmm_8 = 0x00;
static MAPI_U32 tmm_9 = 0x00;
static MAPI_U32 tmm_10 = 0x00;
static MAPI_U32 tmm_11 = 0x00;
static MAPI_U32 tmm_12 = 0x00;
static MAPI_U32 tmm_13 = 0x00;
static MAPI_U32 tmm_14 = 0x00;
static MAPI_U32 tmm_15 = 0x00;
static MAPI_U32 tmm_16 = 0x00;
static MAPI_U32 tmm_17 = 0x00;
static MAPI_U32 tmm_18 = 0x00;
static MAPI_U32 tmm_19 = 0x00;
static MAPI_U32 tmm_20 = 0x00;

#define GIVE_ME_TIME MsOS_GetSystemTime();
#endif

static MAPI_BOOL bDoReset = FALSE;
MAPI_U32 u32StartTime = 0;
MAPI_U8 gQamVal;

#define resetDemodTime  50
#define waitFlashTime   50

static pthread_mutex_t m_MutexOuter;
static pthread_mutex_t m_MutexRWDspReg;
static pthread_mutex_t m_MutexRWReg;

#if (LOAD_DSP_CODE_FROM_MAIN_CHIP_I2C == 1)
static MAPI_U8 g_WO_SPI_FLASH = 1;
#else
static MAPI_U8 g_WO_SPI_FLASH = 0;
#endif

// ONLY wo flash procedure used.
static MAPI_U8 g_sdram_code = 0x0;
static MAPI_U8 g_sram_code  = 0x0;

static MAPI_U8 g_msb1240_plp_id   = 0xff;
static MAPI_U8 g_msb1240_group_id = 0xff;
static MAPI_U8 u8_g_pid0_timeout_flag = 0;

static float g_msb1240_fSNR = 0.0;

//configure
//--------------------------------------------------------------------- for DVB-T2
// BW: 0->1.7M, 1->5M, 2->6M, 3->7M, 4->8M, 5->10M
#define T2_BW_VAL               0x04

// FC: FC = FS = 5000 = 0x1388     (5.0MHz IF)
#define T2_FC_L_VAL            0x88    // 5.0M
#define T2_FC_H_VAL            0x13
#define T2_TS_SERIAL_VAL        TS_DATA_SERIAL
#define T2_TS_CLK_RATE_VAL      0x06
#define T2_TS_OUT_INV_VAL       TS_CLK_INV
#define T2_TS_DATA_SWAP_VAL     TS_DATA_SWAP
#define T2_TS_ERR_POL_VAL       0x00
#define T2_IF_AGC_INV_PWM_EN_VAL 0x00
#define T2_TS_SPREAD_SPAN          0 //Unit: kHz (0: disable; default: 40 kHz)
#define T2_TS_SPREAD_STEP_SIZE    0.0 //Unit: percent(%) (0: disable; default: 3.0 %)
static MAPI_U8   T2_TS_SERIAL = T2_TS_SERIAL_VAL;
static MAPI_U8   T2_TS_CLK_INV = T2_TS_OUT_INV_VAL;

//--------------------------------------------------------------------- for DVB-T
//operation

//configure
#define T_FC_L                    0xc0
#define T_FC_H                    0x12
#define T_FS_L                    0x80
#define T_FS_H                    0x70
#define T_BW                      0x03
#define T_IQ_SWAP                 0x00
#define T_SERIAL_TS               TS_DATA_SERIAL
#define T_TS_CLK_SEL              0x06
#define T_TS_OUT_INV              TS_CLK_INV
#define T_TS_DATA_SWAP            TS_DATA_SWAP
#define T_IF_INV_PWM_OUT_EN  0x00
#define T_TS_SPREAD_SPAN          0 //Unit: kHz (0: disable; default: 40 kHz)
#define T_TS_SPREAD_STEP_SIZE    0.0 //Unit: percent(%) (0: disable; default: 3.0 %)
static MAPI_U8   T_SERIAL_TS_VAL = T_SERIAL_TS;

MAPI_U8 MSB1240_DVBT_DSPREG_TABLE[] = 
{
    T_BW,  T_FC_L,  T_FC_H,  T_SERIAL_TS,  T_TS_CLK_SEL,  T_TS_OUT_INV,  T_TS_DATA_SWAP,
    T_IQ_SWAP,  T_IF_INV_PWM_OUT_EN
};

//--------------------------------------------------------------------- for DVB-C
#define     C_AUTO_SCAN_SYM_RATE    1
#define     C_AUTO_SCAN_QAM	        1    
#define     C_IF_INV_PWM_OUT_EN     0    
#define     C_ZIF                   0    
#define     C_FC_L                  0x88 
#define     C_FC_H                  0x13 
#define     C_FS_L                  0xC0 
#define     C_FS_H                  0x5D 
#define     C_BW_L                  0xDB 
#define     C_BW_H                  0x1A 
#define     C_BW1_L                 0xF4 
#define     C_BW1_H                 0x1A 
#define     C_BW2_L                 0xDB 
#define     C_BW2_H                 0x1A 
#define     C_BW3_L                 0x58 
#define     C_BW3_H                 0x1B 
#define     C_QAM                   2    
#define     C_CCI                   0    
#define     C_TS_SERIAL             TS_DATA_SERIAL    
#define     C_TS_CLK_RATE           6    
#define     C_TS_OUT_INV            TS_CLK_INV    
#define     C_TS_DATA_SWAP          TS_DATA_SWAP    
#define     C_IQ_SWAP               0    
#define     C_TS_SPREAD_SPAN          0 //Unit: kHz (0: disable; default: 40 kHz)
#define     C_TS_SPREAD_STEP_SIZE    0.0 //Unit: percent(%) (0: disable; default: 3.0 %)
//--------------------------------------------------------------------- for DVB-SS2

#define     S_TS_SPREAD_SPAN          0 //Unit: kHz (0: disable; default: 40 kHz)
#define     S_TS_SPREAD_STEP_SIZE    0.0 //Unit: percent(%) (0: disable; default: 3.0 %)
static MAPI_U8 S_TS_Output_VAL = TS_DATA_SERIAL; 
static MAPI_BOOL S_TS_DataSwap_VAL = TS_DATA_SWAP;
static MAPI_BOOL S_TS_clk_inv_VAL = TS_CLK_INV;

MAPI_U8 MSB1240_DVBC_DSPREG_TABLE[] =
{                                                                                                           
    C_AUTO_SCAN_SYM_RATE,  //0x20
    C_AUTO_SCAN_QAM,	   
    C_IF_INV_PWM_OUT_EN,
    C_ZIF,               
    C_FC_L,              
    C_FC_H,              
    C_FS_L,              
    C_FS_H,
    
    C_BW_L,             //0x28 
    C_BW_H,              
    C_BW1_L,             
    C_BW1_H,             
    C_BW2_L,            
    C_BW2_H,             
    C_BW3_L,             
    C_BW3_H,
    
    C_QAM,               //0x30
    C_CCI,               
    C_TS_SERIAL,         
    C_TS_CLK_RATE,       
    C_TS_OUT_INV,        
    C_TS_DATA_SWAP,      
    C_IQ_SWAP,                 
};

static S_DVBT2_SQI_CN_NORDIGP1 g_msb1240_dvbt2_sqi_cn_nordigp1[] =
{
    {_T2_QPSK, _T2_CR1Y2, 3.5},
    {_T2_QPSK, _T2_CR3Y5, 4.7},
    {_T2_QPSK, _T2_CR2Y3, 5.6},
    {_T2_QPSK, _T2_CR3Y4, 6.6},
    {_T2_QPSK, _T2_CR4Y5, 7.2},
    {_T2_QPSK, _T2_CR5Y6, 7.7},

    {_T2_16QAM, _T2_CR1Y2, 8.7},
    {_T2_16QAM, _T2_CR3Y5, 10.1},
    {_T2_16QAM, _T2_CR2Y3, 11.4},
    {_T2_16QAM, _T2_CR3Y4, 12.5},
    {_T2_16QAM, _T2_CR4Y5, 13.3},
    {_T2_16QAM, _T2_CR5Y6, 13.8},

    {_T2_64QAM, _T2_CR1Y2, 13.0},
    {_T2_64QAM, _T2_CR3Y5, 14.8},
    {_T2_64QAM, _T2_CR2Y3, 16.2},
    {_T2_64QAM, _T2_CR3Y4, 17.7},
    {_T2_64QAM, _T2_CR4Y5, 18.7},
    {_T2_64QAM, _T2_CR5Y6, 19.4},

    {_T2_256QAM, _T2_CR1Y2, 17.0},
    {_T2_256QAM, _T2_CR3Y5, 19.4},
    {_T2_256QAM, _T2_CR2Y3, 20.8},
    {_T2_256QAM, _T2_CR3Y4, 22.9},
    {_T2_256QAM, _T2_CR4Y5, 24.3},
    {_T2_256QAM, _T2_CR5Y6, 25.1},
    {_T2_QAM_UNKNOWN, _T2_CR_UNKNOWN, 0.0}
};

MAPI_U8 u8MSB1240ChipRev = 0;
MAPI_U8* MSB1240_LIB = MSB1240_LIB_U01;
MAPI_U32 u32SizeOfMSB1240_LIB = sizeof(MSB1240_LIB_U01);

//--------------------------------------------------
//DVBC-SSI,SQI
//--------------------------------------------------
//algorithm selection
#define formula_A  0
#define formula_B  1
#define formula_OTHERS 100
#define D_CUSTOMER formula_A

#if (DVBC_SYSTEM_ENABLE == 1)
#if (D_CUSTOMER == formula_A)
static float intern_dvb_c_qam_ref_1[] = {-81.5, -78.5, -75.5, -72.5, -69.5, 0.0}; //16q,32q,64q,128q,256q, and others
static float cn_nordig_p1_DVBC[] = {17.5, 20.5, 23.5, 26.5, 30.5, 0.0}; //16q,32q,64q,128q,256q, and others
#else
static float intern_dvb_c_qam_ref[] = {3.0, 0.0, 0.0, 0.0, 0.0, 80.0}; //16q,32q,64q,128q,256q, and others
#endif
#endif

static float g_min_ber = 5.19e-7; // [0.6/(128*188*8*6)]
static float g_previous_ber = 5.19e-7; // [0.6/(128*188*8*6)]

MAPI_BOOL msb1240_flash_mode_en(void);
MAPI_BOOL msb1240_flash_boot_ready_waiting(MAPI_U8 *ptimeout);

#if(TIMING_VERIFICATION == 1)
static void show_timer(void)
{
    printf("***************************\n");
    printf("[tmm1]t2-t1 = %d (%d - %d)\n",tmm_2-tmm_1,tmm_2,tmm_1);
    printf("[tmm2]t4-t3 = %d (%d - %d)\n",tmm_4-tmm_3,tmm_4,tmm_3);
    printf("[tmm3]t6-t5 = %d (%d - %d)\n",tmm_6-tmm_5,tmm_6,tmm_5);
    printf("[tmm4]t8-t7 = %d (%d - %d)\n",tmm_8-tmm_7,tmm_8,tmm_7);
    printf("[tmm5]t10-t9 = %d (%d - %d)\n",tmm_10-tmm_9,tmm_10,tmm_9);
    printf("[tmm6]t12-t11 = %d (%d - %d)\n",tmm_12-tmm_11,tmm_12,tmm_11);
    printf("[tmm7]t14-t13 = %d (%d - %d)\n",tmm_14-tmm_13,tmm_14,tmm_13);
    printf("[tmm8]t16-t15 = %d (%d - %d)\n",tmm_16-tmm_15,tmm_16,tmm_15);
    printf("[tmm9]t18-t17 = %d (%d - %d)\n",tmm_18-tmm_17,tmm_18,tmm_17);
    printf("[tmm10]t20-t19 = %d (%d - %d)\n",tmm_20-tmm_19,tmm_20,tmm_19);
    printf("[tmm11]lock_time# = %d (%d - %d)\n",tmm_14-u32StartTime,tmm_14,u32StartTime);
    printf("[tmm12]lock-setf = %d (%d - %d)\n",tmm_14-tmm_11,tmm_14,tmm_11);
    printf("[tmm13]lock-loaddsp = %d (%d - %d)\n",tmm_14-tmm_9,tmm_14,tmm_9);
    printf("***************************\n");
}
#endif

#if (USE_SPI_LOAD_TO_SDRAM ==1)
#if (NEED_SWITCH_TSPAD_FOR_MSPI_USE == 1)
MAPI_BOOL device_demodulator_extend::MSPI_PAD_Enable(MAPI_U8 u8TSIndex, MAPI_BOOL bOnOff)
{
    SYS_PAD_MUX_SET enTSIndex = E_PAD_SET_MAX;

    switch (u8TSIndex)
    {
        case 0:
            enTSIndex = E_TS0_PAD_SET;
            break;
        case 1:
            enTSIndex = E_TS1_PAD_SET;
            break;            
        default:
            return MAPI_FALSE;
    }

    MAPI_BOOL bRet = MAPI_TRUE;

    if (bOnOff == MAPI_TRUE)
    {
        // ------enable to use TS_PAD as SSPI_PAD
        // [0:0] reg_en_sspi_pad
        // [1:1] reg_ts_sspi_en, 1: use TS_PAD as SSPI_PAD
        bRet &= WriteReg2bytes(0x0900 + (0x3b) * 2, 0x0002);
        //Turn off all pad in
        bRet &= WriteReg2bytes(0x0900 + (0x28) * 2, 0x0000);
        //Transport Stream pad on
        bRet &= WriteReg2bytes(0x0900 + (0x2d) * 2, 0x00ff);

        MDrv_SYS_SetPadMux(enTSIndex, E_MSPI_PAD_ON);
    }
    else
    {
        // ------disable to use TS_PAD as SSPI_PAD after load code
        // [0:0] reg_en_sspi_pad
        // [1:1] reg_ts_sspi_en, 1: use TS_PAD as SSPI_PAD
        bRet &= WriteReg2bytes(0x0900 + (0x3b) * 2, 0x0001);
        //Transport Stream pad off
        bRet &= WriteReg2bytes(0x0900 + (0x2d) * 2, 0x0000);

        MDrv_SYS_SetPadMux(enTSIndex, E_PARALLEL_IN);
    }

    return bRet;
}
#endif
#endif

//##########################################################################################################
//##########################################################################################################
//###############################  Public:Common Function Implementation ###################################
//##########################################################################################################
//##########################################################################################################

static MAPI_BOOL IspCheckVer(MAPI_U8* pLibData, MAPI_BOOL* pMatch)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_U8  bReadData[VERSION_CODE_SIZE];
    MAPI_U32  indx = 0;
    *pMatch = true;

    MAPI_U8 bWriteData[5] = {0x4D, 0x53, 0x54, 0x41, 0x52};
    mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_2);
    iptr->WriteBytes(0, NULL, 5, bWriteData);

    MAPI_U8    bAddr[1], bError = true;
    //MAPI_U16   Count;

    memset(bReadData, 0 , sizeof(bReadData));

    bAddr[0] = 0x10;
    //dwStartAddr=0;

    bWriteData[0] = 0x03;
    bWriteData[1] = VERSION_CODE_ADDR >> 16;
    bWriteData[2] = VERSION_CODE_ADDR >> 8;
    bWriteData[3] = VERSION_CODE_ADDR & 0xFF;

    bError &=  iptr->WriteBytes(1, bAddr, 4, bWriteData);

    bAddr[0] = 0x11;
    bError &= iptr->ReadBytes(1, bAddr, VERSION_CODE_SIZE, bReadData);

    bWriteData[0] = 0x12;
    bError &= iptr->WriteBytes(0, NULL, 1,  bWriteData);

    if(MAPI_FALSE == bError)
    {
        bWriteData[0] = 0x24 ;
        iptr->WriteBytes(0, NULL, 1, bWriteData);

        return MAPI_FALSE;
    }

    bWriteData[0] = 0x24 ;
    iptr->WriteBytes(0, NULL, 1, bWriteData);

    printf("sttest version data = ");
    for(indx = 0; indx < (VERSION_CODE_SIZE); indx++)
    {
        printf(" %x ,", bReadData[indx]);
        if(pLibData[indx+VERSION_CODE_ADDR] != bReadData[indx])
        {
            *pMatch = false;
            //break;
        }
    }
    printf(" \n");

    return MAPI_TRUE;
}

static MAPI_BOOL dram_crc_check(MAPI_U16 chksum_lib, MAPI_BOOL* pMatch)
{
    MAPI_U16  chksum = 0;
    // MAPI_U16  chksum_lib = 0;
    MAPI_U16  u16Addr = 0;
    MAPI_U8   u8MsbData[5];
    MAPI_U8   reg = 0;
    MAPI_BOOL bRet;
    MAPI_U8   mcu_status = 0;

    *pMatch = false;

    // MAPI_U8 bWriteData[5]={0x4D, 0x53, 0x54, 0x41, 0x52};
    mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_1);
    // iptr->WriteBytes(0, NULL, 5, bWriteData);

/// crc H byte
    u16Addr = 0x0c00+0x5a*2;

    u8MsbData[0] = 0x10;
    u8MsbData[1] = 0x00;
    u8MsbData[2] = 0x00;
    u8MsbData[3] = (u16Addr >> 8) &0xff;
    u8MsbData[4] = u16Addr &0xff;

    u8MsbData[0] = 0x35;
    iptr->WriteBytes(0, NULL, 1, u8MsbData);

    u8MsbData[0] = 0x10;
    iptr->WriteBytes(0, NULL, 5, u8MsbData);
    iptr->ReadBytes(0, NULL, 1, &reg);

    u8MsbData[0] = 0x34;
    bRet=iptr->WriteBytes(0, NULL, 1, u8MsbData);


   chksum = reg;

/// crc L byte
    u16Addr = 0x0c00+0x5b*2;

    u8MsbData[0] = 0x10;
    u8MsbData[1] = 0x00;
    u8MsbData[2] = 0x00;
    u8MsbData[3] = (u16Addr >> 8) &0xff;
    u8MsbData[4] = u16Addr &0xff;

    u8MsbData[0] = 0x35;
    iptr->WriteBytes(0, NULL, 1, u8MsbData);

    u8MsbData[0] = 0x10;
    iptr->WriteBytes(0, NULL, 5, u8MsbData);
    iptr->ReadBytes(0, NULL, 1, &reg);

    u8MsbData[0] = 0x34;
    bRet=iptr->WriteBytes(0, NULL, 1, u8MsbData);


   chksum = (chksum<<8)|reg;

// get mcu status

    u16Addr = 0x0900+0x4f*2;

    u8MsbData[0] = 0x10;
    u8MsbData[1] = 0x00;
    u8MsbData[2] = 0x00;
    u8MsbData[3] = (u16Addr >> 8) &0xff;
    u8MsbData[4] = u16Addr &0xff;

    u8MsbData[0] = 0x35;
    iptr->WriteBytes(0, NULL, 1, u8MsbData);

    u8MsbData[0] = 0x10;
    iptr->WriteBytes(0, NULL, 5, u8MsbData);
    iptr->ReadBytes(0, NULL, 1, &reg);

    u8MsbData[0] = 0x34;
    bRet=iptr->WriteBytes(0, NULL, 1, u8MsbData);


    mcu_status = reg;


/// check the crc in dsp lib array

    if (mcu_status == 0xaa && ((chksum_lib&0xff00) == (chksum&0xff00)) )
      *pMatch = true;
    else if(chksum_lib == chksum)
      *pMatch = true;

    DBG_DEMOD_CHECKSUM(printf("[crc]chksum_lib=0x%x, chksum=0x%x, bRet=%d, Match=%d, mcu_status=0x%x\n",chksum_lib,chksum,bRet,*pMatch,mcu_status));

    return bRet;
}

#if (FLASH_WP_ENABLE == 1)
static MAPI_BOOL msb1240_flash_WP_reg_read(MAPI_U16 u16Addr, MAPI_U8 *pu8Data)
{
    MAPI_BOOL bRet;
    MAPI_U8   u8MsbData[5];
    mapi_scope_lock(scopeLock, &m_MutexRWReg);
    mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_1);

    u8MsbData[0] = 0x10;
    u8MsbData[1] = 0x00;
    u8MsbData[2] = 0x00;
    u8MsbData[3] = (u16Addr >> 8) &0xff;
    u8MsbData[4] = u16Addr &0xff;

    u8MsbData[0] = 0x35;
    iptr->WriteBytes(0, NULL, 1, u8MsbData);

    u8MsbData[0] = 0x10;
    iptr->WriteBytes(0, NULL, 5, u8MsbData);
    iptr->ReadBytes(0, NULL, 1, pu8Data);

    u8MsbData[0] = 0x34;
    bRet=iptr->WriteBytes(0, NULL, 1, u8MsbData);
    return bRet;
}

static MAPI_BOOL msb1240_flash_WP_reg_write(MAPI_U16 u16Addr, MAPI_U8 u8Data)
{
    MAPI_BOOL bRet = MAPI_TRUE;
    MAPI_U8   u8MsbData[6];
    mapi_scope_lock(scopeLock, &m_MutexRWReg);
    mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_1);
#if (PRELOAD_DSP_CODE_FROM_MAIN_CHIP_I2C_ONLY_LOAD_T2 == 1)
    bRet &= iptr->SetSpeed(350);
#endif

    u8MsbData[0] = 0x10;
    u8MsbData[1] = 0x00;
    u8MsbData[2] = 0x00;
    u8MsbData[3] = (u16Addr >> 8) &0xff;
    u8MsbData[4] = u16Addr &0xff;
    u8MsbData[5] = u8Data;

    u8MsbData[0] = 0x35;
    iptr->WriteBytes(0, NULL, 1, u8MsbData);

    u8MsbData[0] = 0x10;
    iptr->WriteBytes(0, NULL, 6, u8MsbData);

    u8MsbData[0] = 0x34;
    bRet=iptr->WriteBytes(0, NULL, 1, u8MsbData);
    return bRet;
}

static MAPI_BOOL msb1240_flash_WRSR(MAPI_U8 reg)
{
    MAPI_U8 bWriteData[5]={0x4D, 0x53, 0x54, 0x41, 0x52};
    MAPI_U8     bAddr[1];
    MAPI_BOOL   rbet = true;

    mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_2);
    iptr->WriteBytes(0, NULL, 5, bWriteData);

    // WREN
    bAddr[0] = 0x10;
    bWriteData[0] = 0x06;
    iptr->WriteBytes(1, bAddr, 1, bWriteData);

    bWriteData[0] = 0x12;
    iptr->WriteBytes(0, NULL, 1, bWriteData);

    // WRSR
    bAddr[0] = 0x10;
    bWriteData[0] = 0x01;
    bWriteData[1] = reg;
    iptr->WriteBytes(1, bAddr, 2, bWriteData);

    bWriteData[0] = 0x12;
    iptr->WriteBytes(0, NULL, 1, bWriteData);

    // WRDI
    bAddr[0] = 0x10;
    bWriteData[0] = 0x04;
    iptr->WriteBytes(1, bAddr, 1, bWriteData);

    bWriteData[0] = 0x12;
    iptr->WriteBytes(0, NULL, 1, bWriteData);

    // end
    bWriteData[0] = 0x24;
    iptr->WriteBytes(0, NULL, 1, bWriteData);

    DBG_FLASH_WP(printf("[wb]msb1240_flash_WRSR, reg=0x%x\n",reg);)

    return rbet;
}

static MAPI_BOOL msb1240_flash_SRSR(MAPI_U8 *p_reg)
{

  MAPI_U8 bWriteData[5]={0x4D, 0x53, 0x54, 0x41, 0x52};
  MAPI_U8     bAddr[1];
  MAPI_BOOL   bRet = true;

  mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_2);
  iptr->WriteBytes(0, NULL, 5, bWriteData);

  bAddr[0] = 0x10;
  bWriteData[0] = 0x05;
  iptr->WriteBytes(1, bAddr, 1, bWriteData);

  bAddr[0] = 0x11;
  iptr->ReadBytes(1, bAddr, 1, p_reg);

  bWriteData[0] = 0x12;
  iptr->WriteBytes(0, NULL, 1, bWriteData);

  // end
  bWriteData[0] = 0x24 ;
  iptr->WriteBytes(0, NULL, 1, bWriteData);

  DBG_FLASH_WP(printf("[wb]msb1240_flash_SRSR, reg=0x%x\n",*p_reg);)

  return bRet;
}

static MAPI_BOOL msb1240_flash_WP(MAPI_U8 enable)
{
    MAPI_U8 reg = 0;
    MAPI_BOOL bRet = true;
    MAPI_U8 u8_count = 0;

    DBG_FLASH_WP(printf("[wb]msb1240_flash_WP_Enable=%d\n",enable);)

    if (enable == 1)
    {
      u8_count = 20;
      do
      {
        msb1240_flash_SRSR(&reg);
        usleep(1*1000);
      }while(reg&0x01 && u8_count--);

      if (u8_count == 0)
      {
        bRet = false;
        DBG_FLASH_WP(printf("[wb]Err, flash_SRSR timeout!!!\n");)
        return bRet;
      }

      msb1240_flash_WRSR(reg|0x9c);

      u8_count = 20;
      do
      {
        msb1240_flash_SRSR(&reg);
        usleep(1*1000);
      }while(reg&0x01 && u8_count--);


      if (u8_count == 0)
      {
        bRet = false;
        DBG_FLASH_WP(printf("[wb]Err, flash_SRSR timeout!!!\n");)
        return bRet;
      }

      // active low
      // init gpio0
      bRet &= msb1240_flash_WP_reg_read(0x0900+0x2e*2, &reg);
      bRet &= msb1240_flash_WP_reg_write(0x0900+0x2e*2, reg&(~0x02));
      bRet &= msb1240_flash_WP_reg_read(0x0900+0x6b*2, &reg);
      bRet &= msb1240_flash_WP_reg_write(0x0900+0x6b*2, reg&(~0x30));

      // pull low
      bRet &= msb1240_flash_WP_reg_read(0x0900+0x63*2, &reg);
      bRet &= msb1240_flash_WP_reg_write(0x0900+0x63*2, reg&(~0x01));

      // gpio0 output enable
      bRet &= msb1240_flash_WP_reg_read(0x0900+0x64*2, &reg);
      bRet &= msb1240_flash_WP_reg_write(0x0900+0x64*2, reg&(~0x01));
    }
    else
    {
      // unactive high
      // init gpio0
      bRet &= msb1240_flash_WP_reg_read(0x0900+0x2e*2, &reg);
      bRet &= msb1240_flash_WP_reg_write(0x0900+0x2e*2, reg&(~0x02));
      bRet &= msb1240_flash_WP_reg_read(0x0900+0x6b*2, &reg);
      bRet &= msb1240_flash_WP_reg_write(0x0900+0x6b*2, reg&(~0x30));

      // pull high
      bRet &= msb1240_flash_WP_reg_read(0x0900+0x63*2, &reg);
      bRet &= msb1240_flash_WP_reg_write(0x0900+0x63*2, reg|0x01);

      // gpio0 output enable
      bRet &= msb1240_flash_WP_reg_read(0x0900+0x64*2, &reg);
      bRet &= msb1240_flash_WP_reg_write(0x0900+0x64*2, reg&(~0x01));

      u8_count = 20;
      do
      {
        msb1240_flash_SRSR(&reg);
        usleep(1*1000);
      }while(reg&0x01 && u8_count--);

      if (u8_count == 0)
      {
        bRet = false;
        DBG_FLASH_WP(printf("[wb]Err, flash_SRSR timeout!!!\n");)
        return bRet;
      }

      msb1240_flash_WRSR(reg&(~0xbc));

      u8_count = 20;
      do
      {
        msb1240_flash_SRSR(&reg);
        usleep(1*1000);
      }while(reg&0x01 && u8_count--);

      if (u8_count == 0)
      {
        bRet = false;
        DBG_FLASH_WP(printf("[wb]Err, flash_SRSR timeout!!!\n");)
        return bRet;
      }
    }
    return bRet;
}
#endif


MAPI_BOOL device_demodulator_extend::Turn_Off_ALL_Pad_In(MAPI_BOOL b_en)
{
    MAPI_BOOL bRet = true;
    MAPI_U8   u8_reg = 0;
    
    // Turn off all-pad-in function
    // [8:8] 1: all pad turn off except i2c.    
    bRet &= ReadReg(0x0900+(0x28)*2+1, &u8_reg);
    if (b_en == MAPI_TRUE)
        u8_reg &= (0xff-0x01);
    else
        u8_reg |= 0x01;    
    bRet &= WriteReg(0x0900+(0x28)*2+1, u8_reg);
/*
    MAPI_BOOL  retb = TRUE;
    MAPI_U8    data[5] = {0x53, 0x45, 0x52, 0x44, 0x42};
    MAPI_U8    u8MsbData[6] = {0};
    MAPI_U8    ch_num  = 3;
    MAPI_U8    u8Data  = 0;
    MAPI_U16   u16Addr = 0;

    DBG_DEMOD_LOAD_I2C(printf("[msb1240][beg]msb1240_flash_mode_en\n"));

    MsOS_ObtainMutex(_s32_Demod_DVBT2_RW_Mutex, MSOS_WAIT_FOREVER);
    mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_1);

        if (retry_num != MSB1240_MAX_FLASH_ON_RETRY_NUM)
        {
            ERR_DEMOD_MSB(printf("[msb1240][error]flash mode en fail.....retry=%d\n",retry_num);)
        }
        // retb = TRUE;
        // password
        // 8'hb2(SRID)->8,h53(PWD1)->8,h45(PWD2)->8,h52(PWD3)->8,h44(PWD4)->8,h42(PWD5)
        data[0] = 0x53;
        retb &= iptr->WriteBytes(0, NULL, 5, data);

        // 8'hb2(SRID)->8,h71(CMD)  //TV.n_iic_
        data[0] = 0x71;
        retb &= iptr->WriteBytes(0, NULL, 1, data);

        // 8'hb2(SRID)->8,h81(CMD)  //TV.n_iic_sel_b0
        data[0] = ((ch_num & 0x01) != 0)? 0x81 : 0x80;
        retb &= iptr->WriteBytes(0, NULL, 1, data);

        // 8'hb2(SRID)->8,h83(CMD)  //TV.n_iic_sel_b1
        data[0] = ((ch_num & 0x02) != 0)? 0x83 : 0x82;
        retb &= iptr->WriteBytes(0, NULL, 1, data);

        // 8'hb2(SRID)->8,h84(CMD)  //TV.n_iic_sel_b2
        data[0] = ((ch_num & 0x04) != 0)? 0x85 : 0x84;
        retb &= iptr->WriteBytes(0, NULL, 1, data);

        // 8'hb2(SRID)->8,h53(CMD)  //TV.n_iic_ad_byte_en2, 32bit read/write
        data[0] = 0x53;
        retb &= iptr->WriteBytes(0, NULL, 1, data);

        // 8'hb2(SRID)->8,h7f(CMD)  //TV.n_iic_sel_use_cfg
        data[0] = 0x7f;
        retb &= iptr->WriteBytes(0, NULL, 1, data);

        retb = TRUE;

        // Turn off all-pad-in function
        // beg write register
        u16Addr = 0x0900+(0x28<<1) + 1;
        u8Data = 0x0;

        u8MsbData[0] = 0x10;
        u8MsbData[1] = 0x00;
        u8MsbData[2] = 0x00;
        u8MsbData[3] = (u16Addr >> 8) &0xff;
        u8MsbData[4] = u16Addr &0xff;
        u8MsbData[5] = u8Data;

        u8MsbData[0] = 0x35;
        retb &= iptr->WriteBytes(0, NULL, 1, u8MsbData);

        u8MsbData[0] = 0x10;
        retb &= iptr->WriteBytes(0, NULL, 6, u8MsbData);

        u8MsbData[0] = 0x34;
        retb &= iptr->WriteBytes(0, NULL, 1, u8MsbData);
        // end write register
*/
    return bRet;
}

#if (MSPI_ENABLE)
MAPI_BOOL device_demodulator_extend::DTV_DVB_HW_init(void)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));

    MAPI_BOOL bRet = true;
    MAPI_U8 u8_tmp = 0;
    MAPI_U8 u8_timeout = 0;

    mapi_scope_lock(scopeLock, &m_MutexOuter);

    DBG_DEMOD_LOAD_I2C(printf("[msb1240][beg]MSB1240_HW_init\n"));

    // ASIC INIT for Windermere DVB-T2
    {
    // -------------------------------------------------------------------
    // Initialize DMD_ANA_MISC
    // -------------------------------------------------------------------
    // [0]reg_tst_ldo25i
    // [1]reg_tst_ldo25q
    // [5:4]reg_tst_ldo25i_selfb
    // [7:6]reg_tst_ldo25q_selfb
    // [8]reg_pd_dm2p5ldoi = 1'b0
    // [9]reg_pd_dm2p5ldoq = 1'b0
    bRet &= MDrv_SS_RIU_Write16(0x0A00+(0x4f)*2, 0x0000);

    // [0]reg_tst_ldo11_clk
    // [1]reg_tst_ldo26
    // [2]reg_tst_ldo11_cmp
    // [3]reg_pd_dm1p1ldo_clk = 1'b0
    // [4]reg_pd_dm1p1ldo_cmp = 1'b0
    // [6]reg_tst_ldo26_selfb
    // [7]reg_pd_dm2p6ldo = 1'b0
    // [9:8]reg_tst_ldo11_cmp_selfb
    // [11:10]reg_tst_ldo11_clk_selfb
    bRet &= MDrv_SS_RIU_Write16(0x0A00+(0x4e)*2, 0x0000);

    // [1:0]reg_mpll_loop_div_first       feedback divider 00:div by 1 01:div by 2 10:div by 4 11:div by 8
    // [15:8]reg_mpll_loop_div_second      feedback divider, div by binary data number
    bRet &= MDrv_SS_RIU_Write16(0x0A00+(0x33)*2, 0x1201);

    // [2:0]reg_mpll_ictrl    charge pump current control
    // [3]reg_mpll_in_sel    1.8V or 3.3V reference clock domain select (1'b0=0==>3.3 V reference clock domain)  
    // [4]reg_mpll_xtal2adc_sel    select the XTAL clock bypass to MPLL_ADC_CLK
    // [5]reg_mpll_xtal2next_pll_sel  crystal clock bypass to next PLL select
    // [6]reg_mpll_vco_offset       set VCO initial offset frequency
    // [7]reg_mpll_pd    gated reference clock and power down PLL analog_3v: 1=power down
    // [8]reg_xtal_en    XTAL enable register; 1: enable
    // [10:9]reg_xtal_sel    XTAL driven strength select.
    // [11]reg_mpll_porst    MPLL input  power on reset, connect to reg as MPLL_RESET
    // [12]reg_mpll_reset    PLL software reset; 1:reset
    // [13]reg_pd_dmpll_clk    XTAL to MPLL clock reference power down
    // [14]reg_pd_3p3_1    XTAL to CLK_24M_3P3_1 power down
    // [15]reg_pd_3p3_2    XTAL to CLK_24M_3P3_2 power down
    bRet &= MDrv_SS_RIU_Write16(0x0A00+(0x35)*2, 0x1803);
    bRet &= MDrv_SS_RIU_Write16(0x0A00+(0x35)*2, 0x0003);


    // [0]reg_mpll_clk_dp_pddummy
    // [1]reg_adc_clk_pdADC output clock power down
    // [2]reg_mpll_div2_pdMPLL_DIV2 power down
    // [3]reg_mpll_div3_pdMPLL_DIV3 power down
    // [4]reg_mpll_div4_pdMPLL_DIV4 power down
    // [5]reg_mpll_div8_pdMPLL_DIV8 power down
    // [6]reg_mpll_div10_pdMPLL_DIV10 power down
    // `RIU_W((`RIUBASE_ADCPLL>>1)+7'h30, 2'b11, 16'h2400);  // divide ADC clock to 24Mhz = 24*36/36
          bRet &= MDrv_SS_RIU_Write16(0x0A00+(0x30)*2, 0x2400);

    // $display("--------------------------------------");
    // $display("Initialize ADC I/Q");
    // $display("--------------------------------------");

    // [0]Q channel ADC power down
    // [1]I channel ADC power down
    // [2]Q channel clamp enable. 0:enable, 1:disable
    // [3]I channel clamp enable. 0:enable, 1:disable
    // [6:4]    I channel input mux control;  
    //3'b000=I channel ADC calibration mode input
    //3'b001=VIF signal from VIFPGA
    //3'b100=DVB or ATSC mode input from PAD_I(Q)P(M)
    //all the other combination are only for test mode, don't use without understanding.
    // [10:8]   Q channel input mux control;
    //3'b000=Q channel ADC calibration mode input
    //3'b001=VIF signal from VIFPGA 3'b010 = SSIF signal from PAD_SIFP(M)
    //3'b100=DVB or ATSC mode input from PAD_I(Q)P(M)
    //all the other combination are only for test mode, don't use without understanding.
    // [12]ADC I,Q swap enable; 1: swap
    // [13]ADC clock out select; 1: ADC_CLKQ
    // [14]ADC linear calibration bypass enable; 1:enable
    // [15]ADC internal 1.2v regulator control always 0 in T3
        bRet &= MDrv_SS_RIU_Write16(0x0A00+(0x01)*2, 0x0440);

    // [2:0]reg_imuxs_s
    // [6:4]reg_qmuxs_s
    // [9:8]reg_iclpstr_s
    // [13:12]reg_qclpstr_s
        bRet &= MDrv_SS_RIU_Write16(0x0A00+(0x45)*2, 0x0000);

#if(MSB1240_SELECT_IF_INPUT)
    //[0:0] reg_ana_setting_enable
    //[6:4] reg_ana_setting_sel
        bRet &= MDrv_SS_RIU_Write16(0x0A00+(0x51)*2, 0x0021);
#endif

    // [0]Channel I ADC power down: 1=power dwon
    // [1]Channel Q ADC power down: 1=power dwon
    // [2]power down clamp buffer for test mode
    // [3]change ADC reference voltage for SSIF
    // [6:4]    ADC source bias current control
    // [9:8]    XTAL receiver amp gain
        bRet &= MDrv_SS_RIU_Write16(0x0A00+(0x0c)*2, 0x0002);

    // [0]reg_linear_cal_start_q
    // [1]reg_linear_cal_mode_q
    // [2]reg_linear_cal_en_q
    // [3]reg_linear_cal_code0_oren_q
    // [6:4]reg_linear_cal_status_sel_q
    // [7]reg_pwdn_vcalbuf
      bRet &= MDrv_SS_RIU_Write16(0x0A00+(0x0f)*2, 0x0000);

    // [3:0]clamp voltage control
    //          3'b000 = 0.7v
    //          3'b001 = 0.75v
    //          3'b010 = 0.5v
    //          3'b011 = 0.4v
    //          3'b100 = 0.8v
    //          3'b101 = 0.9v
    //          3'b110 = 0.65v
    //          3'b111 = 0.60v
    // [4]REFERENCE power down
      bRet &= MDrv_SS_RIU_Write16(0x0A00+(0x20)*2, 0x0000);

    // Set ADC gain is 1
      bRet &= MDrv_SS_RIU_Write16(0x0A00+(0x0b)*2, 0x0909);

    // Disable ADC Sign bit
      bRet &= MDrv_SS_RIU_Write16(0x0A00+(0x2e)*2, 0x0000);

    // ADC I channel offset
      bRet &= MDrv_SS_RIU_Write16(0x0A00+(0x2a)*2, 0x0c00);

    // ADC Q channel offset
      bRet &= MDrv_SS_RIU_Write16(0x0A00+(0x2b)*2, 0x0c00);
    
    // [2:0]reg_acl_ref
    // [5:4]reg_acl_isel
    // [8]reg_xtal_pm_isel
    // [9]reg_bond_mode
    // [10]reg_clk_bond_mode
    // [11]reg_clk_usb_3p3_en
    // [13:12]reg_iq_ctrl   = 2'd1
    bRet &= MDrv_SS_RIU_Write16(0x0A00+(0x19)*2, 0x1e00);

    // [ 4:0]reg_ckg_bist[4:0]
    // [11:8]reg_ckg_adcd_d2[3:0]
    bRet &= MDrv_SS_RIU_Write16(0x0900+(0x1c)*2, 0x0000);

    // [ 4:0]reg_ckg_dvbtm_sram_t11x_t22x[4:0]
    // [12:8]reg_ckg_dvbtm_sram_t11x_t24x[4:0]
    bRet &= MDrv_SS_RIU_Write16(0x0900+(0x1e)*2, 0x0000);


    // -------------------------------------------------------------------
    // Release clock gating
    // -------------------------------------------------------------------

    // [0]reg_xtal_en
    // [9:8]reg_clk_pd_iic
    // [10]reg_clk_pd_all
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x09)*2, 0x0101);

    // [3:0]reg_ckg_adcd
    // [7:4]reg_ckg_sadc
    // [11:8]reg_ckg_iicm
    // [13:12]reg_ckg_sbus
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x0a)*2, 0x0000);

    // [5:0]reg_ckg_mcu
    // [6]reg_ckg_live
    // [11:8]reg_ckg_inner
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x0b)*2, 0x0030);
    //set MCU ckg to 108MHz by jason
    // reg_ckg_mcu[4:2] = 0x0 
    // reg_ckg_mcu[5] = 0x0
//      bRet &= WriteReg2bytes(0x0900+(0x0b)*2, 0x0020);
//      bRet &= WriteReg2bytes(0x0900+(0x0b)*2, 0x0000);
    // @0x0910
    // [3:0]reg_ckg_dvbtm_adc
    // [6:4]reg_ckg_dvbt_inner1x
    // [10:8]reg_ckg_dvbt_inner2x
    // [14:12]reg_ckg_dvbt_inner4x
    //DVBT2
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x10)*2, 0x1110);

    // @0x0911
    // [2:0]reg_ckg_dvbt_outer1x
    // [6:4]reg_ckg_dvbt_outer2x
    // [11:8]reg_ckg_dvbtc_outer2x
    //DVBT2
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x11)*2, 0x0111);

    // @0x0912
    // [3:0]reg_ckg_dvbtm_ts
    // [4]reg_dvbtm_ts_out_mode
    // [5]reg_dvbtm_ts_clk_pol
    // [15:8]reg_dvbtm_ts_clk_divnum
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x12)*2, 0x1418);

    // @0x0913
    // [5:0]reg_ckg_spi
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x13)*2, 0x0020);

    // @0x0914
    // [12:8]reg_ckg_dvbtm_sram_t1o2x_t22x
    //DVBT2
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x14)*2, 0x0000);

    // @0x0915
    // [3:0]reg_ckg_dvbc_inner
    // [6:4]reg_ckg_dvbc_eq
    // [10:8]reg_ckg_dvbc_eq8x
    //DVBT2
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x15)*2, 0x0111);

    // @0x0916
    // [8:4]reg_ckg_dvbtm_sram_adc_t22x
    //DVBT2
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x16)*2, 0x0001);

    // @0x0917
    // [4:0]reg_ckg_dvbtm_sram_t12x_t22x
    // [12:8]reg_ckg_dvbtm_sram_t12x_t24x
    //DVBT2
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x17)*2, 0x0000);

    // @0x0918
    // [4:0]reg_ckg_dvbtm_sram_t14x_t24x
    // [12:8]reg_ckg_dvbtm_ts_in
    //DVBT2
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x18)*2, 0x0400);

    // @0x0919
    // [2:0]reg_ckg_tdp_jl_inner1x
    // [6:4]reg_ckg_tdp_jl_inner4x
    // [15:8]reg_ckg_miu
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x19)*2, 0x3c00);

    // @0x091a
    // [6:4]reg_ckg_dvbt2_inner1x
    // [10:8]reg_ckg_dvbt2_inner2x
    // [14:12]reg_ckg_dvbt2_inner4x
     //DVBT2
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x1a)*2, 0x0000);

    // @0x090e
    // [3:0]reg_ckg_dvbs2_ldpc_inner_sram
    // [7:4]reg_ckg_dvbs_viterbi_sram
    //DVBT2
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x0e)*2, 0x0000);

    // @0x091b
    // [2:0]reg_ckg_dvbt2_outer1x
    // [6:4]reg_ckg_dvbt2_outer2x
    // [10:8]reg_ckg_syn_miu
    // [14:12]reg_ckg_syn_ts
    //DVBT2
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x1b)*2, 0x0000);

    // @0x091c
    // [4:0]reg_ckg_bist
    // [11:8]reg_ckg_adcd_d2
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x1c)*2, 0x0000);

    // @0x091d
    // [3:0]reg_ckg_dvbtm_adc_eq_1x
    // [7:4]reg_ckg_dvbtm_adc_eq_0p5x
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x1d)*2, 0x0000);

    // @0x0921
    // [2:0]reg_ckg_tdp_teq_inner1x
    // [14:12]reg_ckg_tdp_teq_inner2x
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x21)*2, 0x0000);

    // @0x0922
    // [3:0]reg_ckg_dvbt_t2_inner0p5x_dvbc_eq1x
    // [7:4]reg_ckg_dvbt_t2_inner2x_dvbc_eq4x
    // [11:8]reg_ckg_dvbt_t2_inner1x
    //DVBT2
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x22)*2, 0x0000);

    // [1:0]reg_iicm_pad_sel
    // [4]reg_i2c_sbpm_en
    // [12:8]reg_i2c_sbpm_idle_num
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x08)*2, 0x0a01);

    // [8]reg_turn_off_pad
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x28)*2, 0x0000);

    // @0x0970
    // [3:0]reg_ckg_dvbt_inner2x_srd0p5x
    // [7:4]reg_ckg_dvbt2_inner2x_srd0p5x
    // [12:8]reg_ckg_dvbtm_sram_t1outer1x_t24x
    //DVBT2
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x70)*2, 0x0000);

    // @0x0971
    // [4:0]reg_ckg_dvbtm_sram_t12x_t24x_srd1x
    // [12:8]reg_ckg_dvbtm_sram_t14x_t24x_srd1x
    //DVBT2
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x71)*2, 0x0000);

    // @0x0972
    // [6:0]reg_ckg_dvbt2_s2_bch_out
    // [12:8]reg_ckg_dvbt2_outer2x
    //DVBT2
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x72)*2, 0x0000);

    // @0x0973
    // [3:0]reg_ckg_dvbt2_inner4x_s2_inner
    //DVBT2
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x73)*2, 0x0000);

    // @0x0974
    // [4:0]reg_ckg_dvbtm_sram_t12x_t24x_s2inner
    // [12:8]reg_ckg_dvbtm_sram_t14x_t24x_s2inner
    //DVBT2
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x74)*2, 0x0000);

    // @0x0975
    // [4:0]reg_ckg_dvbtc_rs
    // [11:8]reg_ckg_dvbs_outer2x_dvbt_outer2x
    // [15:12]reg_ckg_dvbs_outer2x_dvbt_outer2x_miu
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x75)*2, 0xc101);

    // @0x0976
    // [4:0]reg_ckg_dvbs_outer2x_dvbt_outer2x_dvbt2_inner2x
    //DVBT2
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x76)*2, 0x000c);

    // @0x0977
    // [3:0]reg_ckg_dvbt2_inner4x_dvbtc_rs
    // [8:4]reg_ckg_dvbtm_sram_adc_t22x_dvbtc_rs
    //DVBT2
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x77)*2, 0x0000);

//    $display("--------------------------------------");
//    $display("Initialize Transport Stream synthesizer and APLL");
//    $display("--------------------------------------");

    // [15:0]reg_synth_set[15: 0]
    // [ 7:0]reg_synth_set[23:16]
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x51)*2, 0x0000);
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x52)*2, 0x0040);


    // [0]reg_synth_reset
    // [1]reg_synth_ssc_en
    // [2]reg_synth_ssc_mode
    // [4]reg_synth_sld
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x50)*2, 0x0010);

    // [1:0]reg_apll_loop_div_first
    // [15:8]reg_apll_loop_div_second
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x57)*2, 0x0000);

    // [0]reg_apll_pd
    // [1]reg_apll_reset
    // [2]reg_apll_porst
    // [3]reg_apll_vco_offset
    // [4]reg_apll_en_ts
    // [5]reg_apll_endcc
    // [6]reg_apll_clkin_sel
    // [8]reg_apll_ts_mode
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x55)*2, 0x0100);
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x55)*2, 0x0110);

    // [16:0]reg_apll_test
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x59)*2, 0x0000);

    // 0x0920
    // [3:0]reg_ckg_ts_apll_div[2:0]
      bRet &= MDrv_SS_RIU_Write16(0x0900+(0x20)*2, 0x0004);


    // -------------------------------------------------------------------
    // initialize MIU
    // -------------------------------------------------------------------
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x0f)*2, 0x0000);
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x0f)*2, 0x0000);
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x0f)*2, 0x0000);
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x0f)*2, 0x0c01);
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x0f)*2, 0x0c00);

    
// set frequence 133MHz
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x11)*2, 0x60cc);
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x11)*2, 0x00cc);
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x11)*2, 0x004c);
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x10)*2, 0x33f8);
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x12)*2, 0x0000);
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x13)*2, 0x0000);
    // yihao 20130925 for new apll model
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x16)*2, 0x0000);
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x1c)*2, 0x00b0);

    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x01)*2, 0x8100);
    // cke: [0]
    // reg_self_refresh: [1]
    // reg_dynamic_cke: [2]
    // reg_dynamic_ck_odt: [3]
    // reg_dram_bus: [5:4] 00: 16b, 01: 32b, 10: 64b
    // reg_dram_type: [7:6] 00: sdr, 01: ddr, 10: ddr2
    // reg_4ba: [8]    0: 2bk,  1: 4bk
    // reg_col_size: [10:9]
    // reg_cke_oenz: [12]
    // reg_dq_oenz: [13]
    // reg_adr_oenz: [14]
    // reg_cs_z: [15]
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x01)*2, 0xe100);
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x01)*2, 0x8100);
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x02)*2, 0x0371);
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x03)*2, 0x0030);
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x04)*2, 0x33c9);
    // reg_tRAS                      : [3:0]        9
    // reg_tRC                       : [7:4]        c
    // reg_tRCD                      : [11:8]       3
    // reg_tRP                       : [15:12]      3
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x05)*2, 0x4232);
    // reg_tRRD                      : [3:0]         2
    // tWR                           : [7:4]         3
    // reg_tMRD                      : [11:8]        2
    // reg_tRTP                       : [15:12]    4
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x06)*2, 0x5532);
    // reg_w2r_dly(tWTR)             : [3:0]         2
    // reg_w2r_oen_dly               : [7:4]         3
    // reg_r2w_dly(tRTW)             : [11:8]        5
    // reg_r2w_oen_dly               : [15:12]       5
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x07)*2, 0x400c);
    // tRFC                          : [5:0]         c
    // reg_tRAS[4]                   : [6]           0
    // reg_tRC[4]                    : [7]           0
    // reg_write_latency             : [10:8]        0
    // reg_tCCD                      : [15:14]       1
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x0e)*2, 0x1800);

    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x23)*2, 0x7ffe);

    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x20)*2, 0xc001);

    //delay 1
    usleep(1000);

    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x0f)*2, 0x0c01);
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x0f)*2, 0x0c00);
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x0f)*2, 0x0c01);
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x0f)*2, 0x0c00);

    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x01)*2, 0x010d);


// $display("--------Initial DRAM start here!!!");
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x00)*2, 0x0001);


    // wait(test_chip_top.dut.i_dig_top.miu_inst.miu_reg_0.init_done);
    // while( (getValue(0x1200) & 0x8000)!= 0x8000);
    u8_timeout = 0;
    bRet &= ReadReg(0x1201, &u8_tmp);
    DBG_DEMOD_LOAD_I2C(printf("[msb1240]MIU wait init done, u8_tmp=0x%x, bRet=0x%x\n",u8_tmp,bRet));
    while( (u8_tmp&0x80) != 0x80)
    {
        if(u8_timeout++>200)
        {
            ERR_DEMOD_MSB(printf("[msb1240][err]MIU init failure...\n"));
            return MAPI_FALSE;
        }
        // 10us delay
        usleep(10);
        bRet &= ReadReg(0x1201, &u8_tmp);
    }
    DBG_DEMOD_LOAD_I2C(printf("[msb1240]MIU init done, u8_tmp=0x%x, bRet=0x%x\n",u8_tmp,bRet));


    // $display("--------Initial Done");
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x08)*2, 0x0001);


    // $display("-------------------------");
    // $display("-- miu self test start --");
    // $display("-------------------------");
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x70)*2, 0x0000);
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x71)*2, 0x0000);
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x72)*2, 0x0010);
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x74)*2, 0x5aa5);
    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x70)*2, 0x0001);

    // #10000;
    //delay 1
    usleep(1000);
    
    //wait test_finish
    u8_timeout = 0;
    bRet &= ReadReg(0x12E1, &u8_tmp);
    DBG_DEMOD_LOAD_I2C(printf("[msb1240]MIU self test Phase1 beg, u8_tmp=0x%x, bRet=0x%x\n",u8_tmp,bRet));
    while( (u8_tmp&0x80) != 0x80)
    {
        if(u8_timeout++>200)
        {
            ERR_DEMOD_MSB(printf("[msb1240][err]MIU self test Phase1 failure...\n"));
            return MAPI_FALSE;
        }
        // 10us delay
        usleep(10);
        bRet &= ReadReg(0x12E1, &u8_tmp);
    }
    DBG_DEMOD_LOAD_I2C(printf("[msb1240]MIU self test Phase1 end, u8_tmp=0x%x, bRet=0x%x\n",u8_tmp,bRet));

     // #10000;
     //delay 1
     usleep(1000);

    // MIU self test FAIL let program stuck in this while loop
    u8_timeout = 0;
    bRet &= ReadReg(0x12E1, &u8_tmp);
    DBG_DEMOD_LOAD_I2C(printf("[msb1240]MIU self test Phase2 beg, u8_tmp=0x%x, bRet=0x%x\n",u8_tmp,bRet));
    while( (u8_tmp&0x40) != 0x00)
    {
        if(u8_timeout++>200)
        {
            ERR_DEMOD_MSB(printf("[msb1240][err]MIU self test Phase2 failure...\n"));
            return MAPI_FALSE;
        }
        // 10us delay
        usleep(10);
        bRet &= ReadReg(0x12E1, &u8_tmp);
    }
    DBG_DEMOD_LOAD_I2C(printf("[msb1240]MIU self test Phase2 end, u8_tmp=0x%x, bRet=0x%x\n",u8_tmp,bRet));

    bRet &= MDrv_SS_RIU_Write16(0x1200+(0x23)*2, 0x0000);

    // -------------------------------------------------------------------
    // initialize MIU  finish
    // -------------------------------------------------------------------

    // -------------------------------------------------------------------
    //  Turn on pads
    // -------------------------------------------------------------------

    // ------Turn off all pad in
    // [0] reg_set_pad_low
    // [1] reg_set_pad_high
    // [2] reg_set_i2cs_pad_low
    // [3] reg_set_i2cs_pad_high
    // [8] reg_turn_off_pad
     bRet &= MDrv_SS_RIU_Write16(0x0900+(0x28)*2, 0x0000);

    // ------I2CM pad on
    // [1:0]    reg_iicm_pad_sel[1:0]1:iicm enable 2:UART enable
    // [4]      reg_i2c_sbpm_en     1: enable I2CS bypass to I2CM function
    // [12:8]   reg_i2c_sbpm_idle_num[4:0]a: default
     bRet &= MDrv_SS_RIU_Write16(0x0900+(0x08)*2, 0x0a01);

    // ------Transport Stream pad on (except TS ERR pad)
    // [15:0]   reg_en_ts_pad[15:0] 0x00ff:normal TS location 0xff00:reverse TS location
     bRet &= MDrv_SS_RIU_Write16(0x0900+(0x2d)*2, 0x00ff);

    // ------Transport Stream pad on (TS ERR pad)
    // [0]      reg_en_ts_err_pad   1: enable
    // [4]      reg_ts_err_pol  1: inverse 0:normal
     bRet &= MDrv_SS_RIU_Write16(0x0900+(0x2e)*2, 0x0001);

    // ------AGC pad on  
    // [0] reg_ifagc_t_enable
    // [1] reg_ifagc_t_odmode
    // [2] reg_ifagc_t_data_sel
    // [4] reg_ifagc_t_force_enable
    // [5] reg_ifagc_t_force_value
    // [8] reg_ifagc_s_enable
    // [9] reg_ifagc_s_odmode
    // [10] reg_ifagc_s_data_sel
    // [12] reg_ifagc_s_force_enable
    // [13] reg_ifagc_s_force_value
    
    #if (MSB1240_SELECT_IF_INPUT)
    bRet &= MDrv_SS_RIU_Write16(0x0a00+(0x18)*2, 0x0100);
    #else
    bRet &= MDrv_SS_RIU_Write16(0x0a00+(0x18)*2, 0x0001);
    #endif
    }

#if(TIMING_VERIFICATION == 1)
        tmm_2 = GIVE_ME_TIME
#endif

    DBG_DEMOD_LOAD_I2C(printf("[msb1240][end]MSB1240_HW_init, bRet=0x%x\n",bRet));

    return bRet;

}
#else
// 144 ms roughly
MAPI_BOOL device_demodulator_extend::DTV_DVB_HW_init(void)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));

    MAPI_BOOL bRet = true;
    MAPI_U8 u8_tmp = 0;
    MAPI_U8 u8_timeout = 0;

    mapi_scope_lock(scopeLock, &m_MutexOuter);

    DBG_DEMOD_LOAD_I2C(printf("[msb1240][beg]MSB1240_HW_init\n"));

    // ASIC INIT for Windermere DVB-T2
    {
    // -------------------------------------------------------------------
    // Initialize DMD_ANA_MISC
    // -------------------------------------------------------------------
    // [0]reg_tst_ldo25i
    // [1]reg_tst_ldo25q
    // [5:4]reg_tst_ldo25i_selfb
    // [7:6]reg_tst_ldo25q_selfb
    // [8]reg_pd_dm2p5ldoi = 1'b0
    // [9]reg_pd_dm2p5ldoq = 1'b0
    bRet &= WriteReg2bytes(0x0A00+(0x4f)*2, 0x0000);

    // [0]reg_tst_ldo11_clk
    // [1]reg_tst_ldo26
    // [2]reg_tst_ldo11_cmp
    // [3]reg_pd_dm1p1ldo_clk = 1'b0
    // [4]reg_pd_dm1p1ldo_cmp = 1'b0
    // [6]reg_tst_ldo26_selfb
    // [7]reg_pd_dm2p6ldo = 1'b0
    // [9:8]reg_tst_ldo11_cmp_selfb
    // [11:10]reg_tst_ldo11_clk_selfb
    bRet &= WriteReg2bytes(0x0A00+(0x4e)*2, 0x0000);

    // [1:0]reg_mpll_loop_div_first       feedback divider 00:div by 1 01:div by 2 10:div by 4 11:div by 8
    // [15:8]reg_mpll_loop_div_second      feedback divider, div by binary data number
    bRet &= WriteReg2bytes(0x0A00+(0x33)*2, 0x1201);

    // [2:0]reg_mpll_ictrl    charge pump current control
    // [3]reg_mpll_in_sel    1.8V or 3.3V reference clock domain select (1'b0=0==>3.3 V reference clock domain)  
    // [4]reg_mpll_xtal2adc_sel    select the XTAL clock bypass to MPLL_ADC_CLK
    // [5]reg_mpll_xtal2next_pll_sel  crystal clock bypass to next PLL select
    // [6]reg_mpll_vco_offset	    set VCO initial offset frequency
    // [7]reg_mpll_pd    gated reference clock and power down PLL analog_3v: 1=power down
    // [8]reg_xtal_en    XTAL enable register; 1: enable
    // [10:9]reg_xtal_sel    XTAL driven strength select.
    // [11]reg_mpll_porst    MPLL input  power on reset, connect to reg as MPLL_RESET
    // [12]reg_mpll_reset    PLL software reset; 1:reset
    // [13]reg_pd_dmpll_clk    XTAL to MPLL clock reference power down
    // [14]reg_pd_3p3_1    XTAL to CLK_24M_3P3_1 power down
    // [15]reg_pd_3p3_2    XTAL to CLK_24M_3P3_2 power down
    bRet &= WriteReg2bytes(0x0A00+(0x35)*2, 0x1803);
    bRet &= WriteReg2bytes(0x0A00+(0x35)*2, 0x0003);


    // [0]reg_mpll_clk_dp_pddummy
    // [1]reg_adc_clk_pdADC output clock power down
    // [2]reg_mpll_div2_pdMPLL_DIV2 power down
    // [3]reg_mpll_div3_pdMPLL_DIV3 power down
    // [4]reg_mpll_div4_pdMPLL_DIV4 power down
    // [5]reg_mpll_div8_pdMPLL_DIV8 power down
    // [6]reg_mpll_div10_pdMPLL_DIV10 power down
    // `RIU_W((`RIUBASE_ADCPLL>>1)+7'h30, 2'b11, 16'h2400);  // divide ADC clock to 24Mhz = 24*36/36
          bRet &= WriteReg2bytes(0x0A00+(0x30)*2, 0x2400);

    // $display("--------------------------------------");
    // $display("Initialize ADC I/Q");
    // $display("--------------------------------------");

    // [0]Q channel ADC power down
    // [1]I channel ADC power down
    // [2]Q channel clamp enable. 0:enable, 1:disable
    // [3]I channel clamp enable. 0:enable, 1:disable
    // [6:4]    I channel input mux control;  
    //3'b000=I channel ADC calibration mode input
    //3'b001=VIF signal from VIFPGA
    //3'b100=DVB or ATSC mode input from PAD_I(Q)P(M)
    //all the other combination are only for test mode, don't use without understanding.
    // [10:8]   Q channel input mux control;
    //3'b000=Q channel ADC calibration mode input
    //3'b001=VIF signal from VIFPGA 3'b010 = SSIF signal from PAD_SIFP(M)
    //3'b100=DVB or ATSC mode input from PAD_I(Q)P(M)
    //all the other combination are only for test mode, don't use without understanding.
    // [12]ADC I,Q swap enable; 1: swap
    // [13]ADC clock out select; 1: ADC_CLKQ
    // [14]ADC linear calibration bypass enable; 1:enable
    // [15]ADC internal 1.2v regulator control always 0 in T3
        bRet &= WriteReg2bytes(0x0A00+(0x01)*2, 0x0440);

    // [2:0]reg_imuxs_s
    // [6:4]reg_qmuxs_s
    // [9:8]reg_iclpstr_s
    // [13:12]reg_qclpstr_s
        bRet &= WriteReg2bytes(0x0A00+(0x45)*2, 0x0000);

#if(MSB1240_SELECT_IF_INPUT)
    //[0:0] reg_ana_setting_enable
    //[6:4] reg_ana_setting_sel
        bRet &= WriteReg2bytes(0x0A00+(0x51)*2, 0x0021);
#endif

    // [0]Channel I ADC power down: 1=power dwon
    // [1]Channel Q ADC power down: 1=power dwon
    // [2]power down clamp buffer for test mode
    // [3]change ADC reference voltage for SSIF
    // [6:4]    ADC source bias current control
    // [9:8]    XTAL receiver amp gain
        bRet &= WriteReg2bytes(0x0A00+(0x0c)*2, 0x0002);

    // [0]reg_linear_cal_start_q
    // [1]reg_linear_cal_mode_q
    // [2]reg_linear_cal_en_q
    // [3]reg_linear_cal_code0_oren_q
    // [6:4]reg_linear_cal_status_sel_q
    // [7]reg_pwdn_vcalbuf
      bRet &= WriteReg2bytes(0x0A00+(0x0f)*2, 0x0000);

    // [3:0]clamp voltage control
    //          3'b000 = 0.7v
    //          3'b001 = 0.75v
    //          3'b010 = 0.5v
    //          3'b011 = 0.4v
    //          3'b100 = 0.8v
    //          3'b101 = 0.9v
    //          3'b110 = 0.65v
    //          3'b111 = 0.60v
    // [4]REFERENCE power down
      bRet &= WriteReg2bytes(0x0A00+(0x20)*2, 0x0000);

    // Set ADC gain is 1
      bRet &= WriteReg2bytes(0x0A00+(0x0b)*2, 0x0909);

    // Disable ADC Sign bit
      bRet &= WriteReg2bytes(0x0A00+(0x2e)*2, 0x0000);

    // ADC I channel offset
      bRet &= WriteReg2bytes(0x0A00+(0x2a)*2, 0x0c00);

    // ADC Q channel offset
      bRet &= WriteReg2bytes(0x0A00+(0x2b)*2, 0x0c00);
    
    // [2:0]reg_acl_ref
    // [5:4]reg_acl_isel
    // [8]reg_xtal_pm_isel
    // [9]reg_bond_mode
    // [10]reg_clk_bond_mode
    // [11]reg_clk_usb_3p3_en
    // [13:12]reg_iq_ctrl	= 2'd1
    bRet &= WriteReg2bytes(0x0A00+(0x19)*2, 0x1e00);

    // [ 4:0]reg_ckg_bist[4:0]
    // [11:8]reg_ckg_adcd_d2[3:0]
    bRet &= WriteReg2bytes(0x0900+(0x1c)*2, 0x0000);

    // [ 4:0]reg_ckg_dvbtm_sram_t11x_t22x[4:0]
    // [12:8]reg_ckg_dvbtm_sram_t11x_t24x[4:0]
    bRet &= WriteReg2bytes(0x0900+(0x1e)*2, 0x0000);


    // -------------------------------------------------------------------
    // Release clock gating
    // -------------------------------------------------------------------

    // [0]reg_xtal_en
    // [9:8]reg_clk_pd_iic
    // [10]reg_clk_pd_all
      bRet &= WriteReg2bytes(0x0900+(0x09)*2, 0x0101);

    // [3:0]reg_ckg_adcd
    // [7:4]reg_ckg_sadc
    // [11:8]reg_ckg_iicm
    // [13:12]reg_ckg_sbus
      bRet &= WriteReg2bytes(0x0900+(0x0a)*2, 0x0000);

    // [5:0]reg_ckg_mcu
    // [6]reg_ckg_live
    // [11:8]reg_ckg_inner
      bRet &= WriteReg2bytes(0x0900+(0x0b)*2, 0x0030);
    //set MCU ckg to 108MHz by jason
    // reg_ckg_mcu[4:2] = 0x0 
    // reg_ckg_mcu[5] = 0x0
//      bRet &= WriteReg2bytes(0x0900+(0x0b)*2, 0x0020);
//      bRet &= WriteReg2bytes(0x0900+(0x0b)*2, 0x0000);
    // @0x0910
    // [3:0]reg_ckg_dvbtm_adc
    // [6:4]reg_ckg_dvbt_inner1x
    // [10:8]reg_ckg_dvbt_inner2x
    // [14:12]reg_ckg_dvbt_inner4x
    //DVBT2
      bRet &= WriteReg2bytes(0x0900+(0x10)*2, 0x1110);

    // @0x0911
    // [2:0]reg_ckg_dvbt_outer1x
    // [6:4]reg_ckg_dvbt_outer2x
    // [11:8]reg_ckg_dvbtc_outer2x
    //DVBT2
      bRet &= WriteReg2bytes(0x0900+(0x11)*2, 0x0111);

    // @0x0912
    // [3:0]reg_ckg_dvbtm_ts
    // [4]reg_dvbtm_ts_out_mode
    // [5]reg_dvbtm_ts_clk_pol
    // [15:8]reg_dvbtm_ts_clk_divnum
      bRet &= WriteReg2bytes(0x0900+(0x12)*2, 0x1418);

    // @0x0913
    // [5:0]reg_ckg_spi
      bRet &= WriteReg2bytes(0x0900+(0x13)*2, 0x0020);

    // @0x0914
    // [12:8]reg_ckg_dvbtm_sram_t1o2x_t22x
    //DVBT2
      bRet &= WriteReg2bytes(0x0900+(0x14)*2, 0x0000);

    // @0x0915
    // [3:0]reg_ckg_dvbc_inner
    // [6:4]reg_ckg_dvbc_eq
    // [10:8]reg_ckg_dvbc_eq8x
    //DVBT2
      bRet &= WriteReg2bytes(0x0900+(0x15)*2, 0x0111);

    // @0x0916
    // [8:4]reg_ckg_dvbtm_sram_adc_t22x
    //DVBT2
      bRet &= WriteReg2bytes(0x0900+(0x16)*2, 0x0001);

    // @0x0917
    // [4:0]reg_ckg_dvbtm_sram_t12x_t22x
    // [12:8]reg_ckg_dvbtm_sram_t12x_t24x
    //DVBT2
      bRet &= WriteReg2bytes(0x0900+(0x17)*2, 0x0000);

    // @0x0918
    // [4:0]reg_ckg_dvbtm_sram_t14x_t24x
    // [12:8]reg_ckg_dvbtm_ts_in
    //DVBT2
      bRet &= WriteReg2bytes(0x0900+(0x18)*2, 0x0400);

    // @0x0919
    // [2:0]reg_ckg_tdp_jl_inner1x
    // [6:4]reg_ckg_tdp_jl_inner4x
    // [15:8]reg_ckg_miu
      bRet &= WriteReg2bytes(0x0900+(0x19)*2, 0x3c00);

    // @0x091a
    // [6:4]reg_ckg_dvbt2_inner1x
    // [10:8]reg_ckg_dvbt2_inner2x
    // [14:12]reg_ckg_dvbt2_inner4x
     //DVBT2
      bRet &= WriteReg2bytes(0x0900+(0x1a)*2, 0x0000);

    // @0x090e
    // [3:0]reg_ckg_dvbs2_ldpc_inner_sram
    // [7:4]reg_ckg_dvbs_viterbi_sram
    //DVBT2
      bRet &= WriteReg2bytes(0x0900+(0x0e)*2, 0x0000);

    // @0x091b
    // [2:0]reg_ckg_dvbt2_outer1x
    // [6:4]reg_ckg_dvbt2_outer2x
    // [10:8]reg_ckg_syn_miu
    // [14:12]reg_ckg_syn_ts
    //DVBT2
      bRet &= WriteReg2bytes(0x0900+(0x1b)*2, 0x0000);

    // @0x091c
    // [4:0]reg_ckg_bist
    // [11:8]reg_ckg_adcd_d2
      bRet &= WriteReg2bytes(0x0900+(0x1c)*2, 0x0000);

    // @0x091d
    // [3:0]reg_ckg_dvbtm_adc_eq_1x
    // [7:4]reg_ckg_dvbtm_adc_eq_0p5x
      bRet &= WriteReg2bytes(0x0900+(0x1d)*2, 0x0000);

    // @0x0921
    // [2:0]reg_ckg_tdp_teq_inner1x
    // [14:12]reg_ckg_tdp_teq_inner2x
      bRet &= WriteReg2bytes(0x0900+(0x21)*2, 0x0000);

    // @0x0922
    // [3:0]reg_ckg_dvbt_t2_inner0p5x_dvbc_eq1x
    // [7:4]reg_ckg_dvbt_t2_inner2x_dvbc_eq4x
    // [11:8]reg_ckg_dvbt_t2_inner1x
    //DVBT2
      bRet &= WriteReg2bytes(0x0900+(0x22)*2, 0x0000);

    // [1:0]reg_iicm_pad_sel
    // [4]reg_i2c_sbpm_en
    // [12:8]reg_i2c_sbpm_idle_num
      bRet &= WriteReg2bytes(0x0900+(0x08)*2, 0x0a01);

    // [8]reg_turn_off_pad
      bRet &= WriteReg2bytes(0x0900+(0x28)*2, 0x0000);

    // @0x0970
    // [3:0]reg_ckg_dvbt_inner2x_srd0p5x
    // [7:4]reg_ckg_dvbt2_inner2x_srd0p5x
    // [12:8]reg_ckg_dvbtm_sram_t1outer1x_t24x
    //DVBT2
      bRet &= WriteReg2bytes(0x0900+(0x70)*2, 0x0000);

    // @0x0971
    // [4:0]reg_ckg_dvbtm_sram_t12x_t24x_srd1x
    // [12:8]reg_ckg_dvbtm_sram_t14x_t24x_srd1x
    //DVBT2
      bRet &= WriteReg2bytes(0x0900+(0x71)*2, 0x0000);

    // @0x0972
    // [6:0]reg_ckg_dvbt2_s2_bch_out
    // [12:8]reg_ckg_dvbt2_outer2x
    //DVBT2
      bRet &= WriteReg2bytes(0x0900+(0x72)*2, 0x0000);

    // @0x0973
    // [3:0]reg_ckg_dvbt2_inner4x_s2_inner
    //DVBT2
      bRet &= WriteReg2bytes(0x0900+(0x73)*2, 0x0000);

    // @0x0974
    // [4:0]reg_ckg_dvbtm_sram_t12x_t24x_s2inner
    // [12:8]reg_ckg_dvbtm_sram_t14x_t24x_s2inner
    //DVBT2
      bRet &= WriteReg2bytes(0x0900+(0x74)*2, 0x0000);

    // @0x0975
    // [4:0]reg_ckg_dvbtc_rs
    // [11:8]reg_ckg_dvbs_outer2x_dvbt_outer2x
    // [15:12]reg_ckg_dvbs_outer2x_dvbt_outer2x_miu
      bRet &= WriteReg2bytes(0x0900+(0x75)*2, 0xc101);

    // @0x0976
    // [4:0]reg_ckg_dvbs_outer2x_dvbt_outer2x_dvbt2_inner2x
    //DVBT2
      bRet &= WriteReg2bytes(0x0900+(0x76)*2, 0x000c);

    // @0x0977
    // [3:0]reg_ckg_dvbt2_inner4x_dvbtc_rs
    // [8:4]reg_ckg_dvbtm_sram_adc_t22x_dvbtc_rs
    //DVBT2
      bRet &= WriteReg2bytes(0x0900+(0x77)*2, 0x0000);

//    $display("--------------------------------------");
//    $display("Initialize Transport Stream synthesizer and APLL");
//    $display("--------------------------------------");

    // [15:0]reg_synth_set[15: 0]
    // [ 7:0]reg_synth_set[23:16]
      bRet &= WriteReg2bytes(0x0900+(0x51)*2, 0x0000);
      bRet &= WriteReg2bytes(0x0900+(0x52)*2, 0x0040);


    // [0]reg_synth_reset
    // [1]reg_synth_ssc_en
    // [2]reg_synth_ssc_mode
    // [4]reg_synth_sld
      bRet &= WriteReg2bytes(0x0900+(0x50)*2, 0x0010);

    // [1:0]reg_apll_loop_div_first
    // [15:8]reg_apll_loop_div_second
      bRet &= WriteReg2bytes(0x0900+(0x57)*2, 0x0000);

    // [0]reg_apll_pd
    // [1]reg_apll_reset
    // [2]reg_apll_porst
    // [3]reg_apll_vco_offset
    // [4]reg_apll_en_ts
    // [5]reg_apll_endcc
    // [6]reg_apll_clkin_sel
    // [8]reg_apll_ts_mode
      bRet &= WriteReg2bytes(0x0900+(0x55)*2, 0x0100);
      bRet &= WriteReg2bytes(0x0900+(0x55)*2, 0x0110);

    // [16:0]reg_apll_test
      bRet &= WriteReg2bytes(0x0900+(0x59)*2, 0x0000);

    // 0x0920
    // [3:0]reg_ckg_ts_apll_div[2:0]
      bRet &= WriteReg2bytes(0x0900+(0x20)*2, 0x0004);


    // -------------------------------------------------------------------
    // initialize MIU
    // -------------------------------------------------------------------
    bRet &= WriteReg2bytes(0x1200+(0x0f)*2, 0x0000);
    bRet &= WriteReg2bytes(0x1200+(0x0f)*2, 0x0000);
    bRet &= WriteReg2bytes(0x1200+(0x0f)*2, 0x0000);
    bRet &= WriteReg2bytes(0x1200+(0x0f)*2, 0x0c01);
    bRet &= WriteReg2bytes(0x1200+(0x0f)*2, 0x0c00);

    
// set frequence 133MHz
    bRet &= WriteReg2bytes(0x1200+(0x11)*2, 0x60cc);
    bRet &= WriteReg2bytes(0x1200+(0x11)*2, 0x00cc);
    bRet &= WriteReg2bytes(0x1200+(0x11)*2, 0x004c);
    bRet &= WriteReg2bytes(0x1200+(0x10)*2, 0x33f8);
    bRet &= WriteReg2bytes(0x1200+(0x12)*2, 0x0000);
    bRet &= WriteReg2bytes(0x1200+(0x13)*2, 0x0000);
    // yihao 20130925 for new apll model
    bRet &= WriteReg2bytes(0x1200+(0x16)*2, 0x0000);
    bRet &= WriteReg2bytes(0x1200+(0x1c)*2, 0x00b0);

    bRet &= WriteReg2bytes(0x1200+(0x01)*2, 0x8100);
    // cke: [0]
    // reg_self_refresh: [1]
    // reg_dynamic_cke: [2]
    // reg_dynamic_ck_odt: [3]
    // reg_dram_bus: [5:4] 00: 16b, 01: 32b, 10: 64b
    // reg_dram_type: [7:6] 00: sdr, 01: ddr, 10: ddr2
    // reg_4ba: [8]    0: 2bk,  1: 4bk
    // reg_col_size: [10:9]
    // reg_cke_oenz: [12]
    // reg_dq_oenz: [13]
    // reg_adr_oenz: [14]
    // reg_cs_z: [15]
    bRet &= WriteReg2bytes(0x1200+(0x01)*2, 0xe100);
    bRet &= WriteReg2bytes(0x1200+(0x01)*2, 0x8100);
    bRet &= WriteReg2bytes(0x1200+(0x02)*2, 0x0371);
    bRet &= WriteReg2bytes(0x1200+(0x03)*2, 0x0030);
    bRet &= WriteReg2bytes(0x1200+(0x04)*2, 0x33c9);
    // reg_tRAS                      : [3:0]        9
    // reg_tRC                       : [7:4]        c
    // reg_tRCD                      : [11:8]       3
    // reg_tRP                       : [15:12]      3
    bRet &= WriteReg2bytes(0x1200+(0x05)*2, 0x4232);
    // reg_tRRD                      : [3:0]         2
    // tWR                           : [7:4]         3
    // reg_tMRD                      : [11:8]        2
    // reg_tRTP                       : [15:12]    4
    bRet &= WriteReg2bytes(0x1200+(0x06)*2, 0x5532);
    // reg_w2r_dly(tWTR)             : [3:0]         2
    // reg_w2r_oen_dly               : [7:4]         3
    // reg_r2w_dly(tRTW)             : [11:8]        5
    // reg_r2w_oen_dly               : [15:12]       5
    bRet &= WriteReg2bytes(0x1200+(0x07)*2, 0x400c);
    // tRFC                          : [5:0]         c
    // reg_tRAS[4]                   : [6]           0
    // reg_tRC[4]                    : [7]           0
    // reg_write_latency             : [10:8]        0
    // reg_tCCD                      : [15:14]       1
    bRet &= WriteReg2bytes(0x1200+(0x0e)*2, 0x1800);

    bRet &= WriteReg2bytes(0x1200+(0x23)*2, 0x7ffe);

    bRet &= WriteReg2bytes(0x1200+(0x20)*2, 0xc001);

    //delay 1
    usleep(1000);

    bRet &= WriteReg2bytes(0x1200+(0x0f)*2, 0x0c01);
    bRet &= WriteReg2bytes(0x1200+(0x0f)*2, 0x0c00);
    bRet &= WriteReg2bytes(0x1200+(0x0f)*2, 0x0c01);
    bRet &= WriteReg2bytes(0x1200+(0x0f)*2, 0x0c00);

    bRet &= WriteReg2bytes(0x1200+(0x01)*2, 0x010d);


// $display("--------Initial DRAM start here!!!");
    bRet &= WriteReg2bytes(0x1200+(0x00)*2, 0x0001);


    // wait(test_chip_top.dut.i_dig_top.miu_inst.miu_reg_0.init_done);
    // while( (getValue(0x1200) & 0x8000)!= 0x8000);
    u8_timeout = 0;
    bRet &= ReadReg(0x1201, &u8_tmp);
    DBG_DEMOD_LOAD_I2C(printf("[msb1240]MIU wait init done, u8_tmp=0x%x, bRet=0x%x\n",u8_tmp,bRet));
    while( (u8_tmp&0x80) != 0x80)
    {
        if(u8_timeout++>200)
        {
            ERR_DEMOD_MSB(printf("[msb1240][err]MIU init failure...\n"));
            return MAPI_FALSE;
        }
        // 10us delay
        usleep(10);
        bRet &= ReadReg(0x1201, &u8_tmp);
    }
    DBG_DEMOD_LOAD_I2C(printf("[msb1240]MIU init done, u8_tmp=0x%x, bRet=0x%x\n",u8_tmp,bRet));


    // $display("--------Initial Done");
    bRet &= WriteReg2bytes(0x1200+(0x08)*2, 0x0001);


    // $display("-------------------------");
    // $display("-- miu self test start --");
    // $display("-------------------------");
    bRet &= WriteReg2bytes(0x1200+(0x70)*2, 0x0000);
    bRet &= WriteReg2bytes(0x1200+(0x71)*2, 0x0000);
    bRet &= WriteReg2bytes(0x1200+(0x72)*2, 0x0010);
    bRet &= WriteReg2bytes(0x1200+(0x74)*2, 0x5aa5);
    bRet &= WriteReg2bytes(0x1200+(0x70)*2, 0x0001);

    // #10000;
    //delay 1
    usleep(1000);
    
    //wait test_finish
    u8_timeout = 0;
    bRet &= ReadReg(0x12E1, &u8_tmp);
    DBG_DEMOD_LOAD_I2C(printf("[msb1240]MIU self test Phase1 beg, u8_tmp=0x%x, bRet=0x%x\n",u8_tmp,bRet));
    while( (u8_tmp&0x80) != 0x80)
    {
        if(u8_timeout++>200)
        {
            ERR_DEMOD_MSB(printf("[msb1240][err]MIU self test Phase1 failure...\n"));
            return MAPI_FALSE;
        }
        // 10us delay
        usleep(10);
        bRet &= ReadReg(0x12E1, &u8_tmp);
    }
    DBG_DEMOD_LOAD_I2C(printf("[msb1240]MIU self test Phase1 end, u8_tmp=0x%x, bRet=0x%x\n",u8_tmp,bRet));

     // #10000;
     //delay 1
     usleep(1000);

    // MIU self test FAIL let program stuck in this while loop
    u8_timeout = 0;
    bRet &= ReadReg(0x12E1, &u8_tmp);
    DBG_DEMOD_LOAD_I2C(printf("[msb1240]MIU self test Phase2 beg, u8_tmp=0x%x, bRet=0x%x\n",u8_tmp,bRet));
    while( (u8_tmp&0x40) != 0x00)
    {
        if(u8_timeout++>200)
        {
            ERR_DEMOD_MSB(printf("[msb1240][err]MIU self test Phase2 failure...\n"));
            return MAPI_FALSE;
        }
        // 10us delay
        usleep(10);
        bRet &= ReadReg(0x12E1, &u8_tmp);
    }
    DBG_DEMOD_LOAD_I2C(printf("[msb1240]MIU self test Phase2 end, u8_tmp=0x%x, bRet=0x%x\n",u8_tmp,bRet));

    bRet &= WriteReg2bytes(0x1200+(0x23)*2, 0x0000);

    // -------------------------------------------------------------------
    // initialize MIU  finish
    // -------------------------------------------------------------------

    // -------------------------------------------------------------------
    //  Turn on pads
    // -------------------------------------------------------------------

    // ------Turn off all pad in
    // [0] reg_set_pad_low
    // [1] reg_set_pad_high
    // [2] reg_set_i2cs_pad_low
    // [3] reg_set_i2cs_pad_high
    // [8] reg_turn_off_pad
     bRet &= WriteReg2bytes(0x0900+(0x28)*2, 0x0000);

    // ------I2CM pad on
    // [1:0]    reg_iicm_pad_sel[1:0]1:iicm enable 2:UART enable
    // [4]	    reg_i2c_sbpm_en		1: enable I2CS bypass to I2CM function
    // [12:8]   reg_i2c_sbpm_idle_num[4:0]a: default
     bRet &= WriteReg2bytes(0x0900+(0x08)*2, 0x0a01);

    // ------Transport Stream pad on (except TS ERR pad)
    // [15:0]   reg_en_ts_pad[15:0]	0x00ff:normal TS location 0xff00:reverse TS location
     bRet &= WriteReg2bytes(0x0900+(0x2d)*2, 0x00ff);

    // ------Transport Stream pad on (TS ERR pad)
    // [0]	    reg_en_ts_err_pad	1: enable
    // [4]	    reg_ts_err_pol	1: inverse 0:normal
     bRet &= WriteReg2bytes(0x0900+(0x2e)*2, 0x0001);

    // ------AGC pad on  
    // [0] reg_ifagc_t_enable
    // [1] reg_ifagc_t_odmode
    // [2] reg_ifagc_t_data_sel
    // [4] reg_ifagc_t_force_enable
    // [5] reg_ifagc_t_force_value
    // [8] reg_ifagc_s_enable
    // [9] reg_ifagc_s_odmode
    // [10] reg_ifagc_s_data_sel
    // [12] reg_ifagc_s_force_enable
    // [13] reg_ifagc_s_force_value
#if (MSB1240_SELECT_IF_INPUT)
    bRet &= WriteReg2bytes(0x0a00+(0x18)*2, 0x0100);
#else
    bRet &= WriteReg2bytes(0x0a00+(0x18)*2, 0x0001);
#endif
    }

#if(TIMING_VERIFICATION == 1)
        tmm_2 = GIVE_ME_TIME
#endif

    DBG_DEMOD_LOAD_I2C(printf("[msb1240][end]MSB1240_HW_init, bRet=0x%x\n",bRet));

    return bRet;

}
#endif

// i2c 150Kb/s, average rate 110Kb/s, 32KB, 2436ms.
MAPI_BOOL device_demodulator_extend::Load2Sdram(MAPI_U8 *u8_ptr, MAPI_U16 data_length, MAPI_U16 sdram_win_offset_base)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_BOOL bRet = true;

    DBG_DEMOD_LOAD_I2C(printf("[msb1240][beg]Load2Sdram, len=0x%x, win_offset=0x%x\n",data_length,sdram_win_offset_base));

#if(TIMING_VERIFICATION == 1)
    tmm_3 = GIVE_ME_TIME;
#endif

    // mask miu access of fdp, tdi, djb
    bRet &= WriteReg(0x1200+(0x23)*2 + 1, 0x0f);
    bRet &= WriteReg(0x1200+(0x23)*2, 0xf0);

    // 10us delay
    usleep(10);

    // Disable MCU
    bRet &= WriteReg(0x0b00+(0x19)*2, 0x03);
    
#if USE_SPI_LOAD_TO_SDRAM
    MAPI_U32 u32Addr = 0;

    u32Addr = (MAPI_U32)sdram_win_offset_base * 0x1000; // unit: 4K
    bRet &= MDrv_SS_MIU_Writes(u32Addr, u8_ptr, data_length);
    printf("****SPI load2sdram****, sdram_addr=0x%x, data_length=0x%x\n", u32Addr, data_length);

#if (SDRAM_DATA_CHECK == 1)
    MAPI_U16 i = 0, j = 0, index = 0;
    MAPI_U8 buf[SPI_DEVICE_BUFFER_SIZE]={0};

    if((data_length % SPI_DEVICE_BUFFER_SIZE) == 0)
        index = data_length / SPI_DEVICE_BUFFER_SIZE;
    else
        index = data_length / SPI_DEVICE_BUFFER_SIZE + 1;

    for (i=0; i<index; i++)
    {
        memset(buf, 0x00, SPI_DEVICE_BUFFER_SIZE);

        bRet &= MDrv_SS_MIU_Reads(u32Addr+SPI_DEVICE_BUFFER_SIZE*i, buf, SPI_DEVICE_BUFFER_SIZE);
        
        for (j=0; j<SPI_DEVICE_BUFFER_SIZE; j++)
        {
            //printf("j=%d, buf=0x%x, data=0x%x", j, buf[j], u8_ptr[j]);
            if (buf[j] != u8_ptr[SPI_DEVICE_BUFFER_SIZE*i+j])
            {
                printf("spi miu write&read fail! idx=%x, y=0x%x, x=0x%x\n", (SPI_DEVICE_BUFFER_SIZE*i+j), buf[i], u8_ptr[SPI_DEVICE_BUFFER_SIZE*i+j]);
                //break;
            }
        }
    }
#endif
    
#else

    MAPI_U16  sdram_win_offset = sdram_win_offset_base;
    MAPI_U16  x_data_offset = 0;
    MAPI_U16  y_cir_addr = 0;
        
    if (I2C_CH_Reset(0) == MAPI_FALSE)
    {
        ERR_DEMOD_MSB(printf(">>>MSB1240 CH0 Reset:Fail\n"));
        return MAPI_FALSE;
    }
    else
    {
        // set xData map upper and low bound for 64k DRAM window
        bRet &= WriteReg2bytes(0x2B00+(0x63)*2, 0x3F24);

        // set xData map offset for 64k DRAM window, 64kbytes alignment
        bRet &= WriteReg2bytes(0x2B00+(0x64)*2, 0x0000);

        // set xData map upper and low bound for 4k DRAM window
        bRet &= WriteReg2bytes(0x2B00+(0x65)*2, 0x2014);

        // set xData map offset for 4k DRAM window, 4kbytes alignment
        bRet &= WriteReg2bytes(0x2B00+(0x66)*2, sdram_win_offset++);

        // enable xData map for DRAM
        bRet &= WriteReg2bytes(0x2B00+(0x62)*2, 0x0007);

        for(x_data_offset = 0, y_cir_addr = SDRAM_BASE; x_data_offset < data_length;)
        {
            if (y_cir_addr == 0x6000)
            {
                //set xData map offset for 4k DRAM window, 4kbytes alignment
                // 0x1000, 4096 bytes
                bRet &= WriteReg2bytes(0x2B00+(0x66)*2, sdram_win_offset++);
                y_cir_addr = SDRAM_BASE;
            }

            // max 0x200, error above.....

            if((data_length - x_data_offset) >= LOAD_CODE_I2C_BLOCK_NUM)
            {
                bRet &= WriteRegs(y_cir_addr, (u8_ptr + x_data_offset),LOAD_CODE_I2C_BLOCK_NUM);
                y_cir_addr += LOAD_CODE_I2C_BLOCK_NUM;
                x_data_offset += LOAD_CODE_I2C_BLOCK_NUM;
            }
            else
            {
                bRet &= WriteRegs(y_cir_addr, (u8_ptr + x_data_offset),data_length - x_data_offset);
                y_cir_addr += (data_length - x_data_offset);
                x_data_offset += (data_length - x_data_offset);
            }
        }
        DBG_DEMOD_LOAD_I2C(printf("[msb1240]x_data_offset=%d,y_cir_addr=%d,z_block_num=%d\n",x_data_offset,y_cir_addr,sdram_win_offset));

#if (SDRAM_DATA_CHECK == 1)
        // beg data check.
        DBG_DEMOD_LOAD_I2C(printf("[msb1240]SDRAM data check...\n"));

        sdram_win_offset = sdram_win_offset_base;

        // set xData map offset for 4k DRAM window, 4kbytes alignment
        bRet &= WriteReg2bytes(0x2B00+(0x66)*2, sdram_win_offset++);

        for(x_data_offset = 0, y_cir_addr = SDRAM_BASE; x_data_offset < data_length;)
        {
            MAPI_U8 u8_tmp;
            if (y_cir_addr == 0x6000)
            {
                //set xData map offset for 4k DRAM window, 4kbytes alignment
                // 0x1000, 4096 bytes
                bRet &= WriteReg2bytes(0x2B00+(0x66)*2, sdram_win_offset++);
                y_cir_addr = SDRAM_BASE;
            }

            bRet &= ReadReg(y_cir_addr++, &u8_tmp);
            if(u8_tmp != *(u8_ptr + x_data_offset++))
            {
                ERR_DEMOD_MSB(printf("[msb1240]error, idx=0x%x, y=0x%x, x=0x%x\n",y_cir_addr-1, u8_tmp, *(u8_ptr + x_data_offset-1)));
            }
        }

        DBG_DEMOD_LOAD_I2C(printf("[msb1240]SDRAM data check...Done\n"));

        // end data check
#endif
        //  Release xData map for SDRAM
        bRet &= WriteReg2bytes(0x2B00+(0x62)*2, 0x0000);

        // Channel changed from CH 0x00 to CH 0x03
        if (I2C_CH_Reset(3) == MAPI_FALSE)
        {
            ERR_DEMOD_MSB(printf(">>>MSB1240 CH3 Reset:Fail\n"));
            return MAPI_FALSE;
        }
    }
#endif

#if(TIMING_VERIFICATION == 1)
    tmm_4 = GIVE_ME_TIME
    show_timer();
#endif

    DBG_DEMOD_LOAD_I2C(printf("[msb1240][end]Load2Sdram, len=0x%x, win_offset=0x%x\n",data_length,sdram_win_offset_base));
    return bRet;
}

// 92~95ms roughly
MAPI_BOOL device_demodulator_extend::LoadSdram2Sram(MAPI_U8 CodeNum)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));

    MAPI_BOOL bRet = true;
    MAPI_U8   u8_data = 0;
    MAPI_U8   u8_timeout = 0xFF;

    DBG_DEMOD_LOAD_I2C(printf("[msb1240][beg]LoadSdram2Sram, g_sram_code=0x%x, codeNum=0x%x\n",g_sram_code,CodeNum));

#if(TIMING_VERIFICATION == 1)
    tmm_17 = GIVE_ME_TIME
#endif

    if(CodeNum == g_sram_code)
    {
        DBG_DEMOD_LOAD_I2C(printf("[msb1240]LoadSdram2Sram, code is available.\n"));
        return bRet;
    }

    // mask miu access of fdp, tdi, djb
    bRet &= WriteReg(0x1200+(0x23)*2 + 1,0x0f);
    bRet &= WriteReg(0x1200+(0x23)*2,0xf0);

    // 10us delay
    usleep(10);

    // Disable MCU
    bRet &= WriteReg(0x0b00+(0x19)*2, 0x03);

    // Use GDMA move code from SDRAM to SRAM
     bRet &= WriteReg2bytes(0x0300+(0x00)*2, 0x4254);     // rst
     bRet &= WriteReg2bytes(0x0300+(0x00)*2, 0x4257);    // cfg & trig
   
   switch(CodeNum)
   {
        case 0x02: //DVB-T2 @001000h ~ 008FFFh
            bRet &= WriteReg2bytes(0x0300+(0x01)*2, 0x0000); // Set src_addr
            bRet &= WriteReg2bytes(0x0300+(0x02)*2, 0x0011); // start from 1M+64k
            break;

        case 0x04: //DVB-T @010000h ~ 017FFFh
            bRet &= WriteReg2bytes(0x0300+(0x01)*2, 0x8000); // Set src_addr
            bRet &= WriteReg2bytes(0x0300+(0x02)*2, 0x0011); // start from 1M+96k
            break;

        case 0x08: // DVB-C
            bRet &= WriteReg2bytes(0x0300+(0x01)*2, 0x0000); // Set src_addr
            bRet &= WriteReg2bytes(0x0300+(0x02)*2, 0x0012); // start from 1M+128k
            break;
            
        case 0x10:// DVB-S2 
            bRet &= WriteReg2bytes(0x0300+(0x01)*2, 0x0000); // Set src_addr
            bRet &= WriteReg2bytes(0x0300+(0x02)*2, 0x0013); // start from 1M+192k
            break;
        default:
            bRet &= false;
            break;
   }

    // Set dst_addr
    bRet &= WriteReg2bytes(0x0300+(0x03)*2, 0x0000);
    bRet &= WriteReg2bytes(0x0300+(0x04)*2, 0x0000);
        
    // Set data_size
    bRet &= WriteReg2bytes(0x0300+(0x05)*2, 0x8000);
    bRet &= WriteReg2bytes(0x0300+(0x06)*2, 0x0000);

    bRet &= WriteReg(0x0300+(0x07)*2, 0x01); //start GDMA

    // Wait for GDMA
    do
    {
      usleep(10);
      bRet &= ReadReg(0x0300+(0x08)*2, &u8_data);
      u8_timeout--;
    }while(((u8_data&0x01) != 0x01) && (u8_timeout != 0x00));

    if(u8_data != 0x01)
    {
        printf("[msb1240]LoadSdram2Sram, GDMA move code fail!!\n");
        return false;
    }

    //if(CodeNum == 0x02)
    //    bRet &= MSB1240_MEM_switch(2);  // setting for rom code
    //else
        bRet &= MSB1240_MEM_switch(1);

    if(bRet == false)
    {
        g_sram_code = 0x00;
    }
    else
    {
        g_sram_code = CodeNum;
    }

#if(TIMING_VERIFICATION == 1)
    tmm_18 = GIVE_ME_TIME
#endif

    DBG_DEMOD_LOAD_I2C(printf("[msb1240][end]LoadSdram2Sram, codeNum=0x%x, g_sram_code=0x%x, bRet=0x%x\n",CodeNum,g_sram_code,bRet));

    return bRet;
}


MAPI_BOOL device_demodulator_extend::LoadDspCodeToSDRAM_Boot(void)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_BOOL bRet = true;
    MAPI_U16  code_size, win_offset;
    MAPI_U8   *data_ptr;

    DBG_DEMOD_LOAD_I2C(printf("[msb1240][beg]LoadDspCodeToSDRAM_Boot\n"));

    if(!(g_sdram_code&MSB1240_BOOT))
    {
        if(u32SizeOfMSB1240_LIB > MSB1240_BOOT_START_ADDR)
        {
            // boot code
            data_ptr = MSB1240_LIB + MSB1240_BOOT_START_ADDR;
            code_size = MSB1240_BOOT_END_ADDR - MSB1240_BOOT_START_ADDR + 1;
            win_offset = MSB1240_BOOT_WINDOWS_OFFSET;
            bRet &= Load2Sdram(data_ptr,code_size,win_offset);
            if(bRet == true)
            {
                g_sdram_code |= MSB1240_BOOT;
            }
        }
        else
        {
            printf("@msb1240, boot code is unavailable!!!\n");
        }
    }
    DBG_DEMOD_LOAD_I2C(printf("[msb1240][end]LoadDspCodeToSDRAM_Boot\n"));
    return bRet;
}

MAPI_BOOL device_demodulator_extend::LoadDspCodeToSDRAM_dvbt2(void)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_BOOL bRet = true;
    MAPI_U16  code_size, win_offset;
    MAPI_U8   *data_ptr;

    DBG_DEMOD_LOAD_I2C(printf("[msb1240][beg]LoadDspCodeToSDRAM_dvbt2\n"));

    if( !(g_sdram_code&MSB1240_DVBT2) )
    {
        if(u32SizeOfMSB1240_LIB > MSB1240_DVBT2_P1_START_ADDR)
        {
#if (PRELOAD_DSP_CODE_FROM_MAIN_CHIP_I2C_ONLY_LOAD_T2 == 1)
        if (bPreload_T2_en == MAPI_FALSE)
        {
                // dvbt2_p2
                data_ptr = MSB1240_LIB + MSB1240_DVBT2_P2_START_ADDR;
                code_size = MSB1240_DVBT2_P2_END_ADDR - MSB1240_DVBT2_P2_START_ADDR + 1;
                win_offset = MSB1240_DVBT2_P2_WINDOWS_OFFSET;
                bRet &= Load2Sdram(data_ptr,code_size,win_offset);
                if (bRet == true)
                {
                    bPreload_T2_en = MAPI_TRUE;
                }
                return bRet;
        }
        else
        {

            // dvbt2_p1
            data_ptr = MSB1240_LIB + MSB1240_DVBT2_P1_START_ADDR;
            code_size = MSB1240_DVBT2_P1_END_ADDR - MSB1240_DVBT2_P1_START_ADDR + 1;
            win_offset = MSB1240_DVBT2_P1_WINDOWS_OFFSET;
            bRet &= Load2Sdram(data_ptr,code_size,win_offset);

            if(bRet == true)
            {
                g_sdram_code |= MSB1240_DVBT2;
            }
        }
#else
            // dvbt2_p2
            data_ptr = MSB1240_LIB + MSB1240_DVBT2_P2_START_ADDR;
            code_size = MSB1240_DVBT2_P2_END_ADDR - MSB1240_DVBT2_P2_START_ADDR + 1;
            win_offset = MSB1240_DVBT2_P2_WINDOWS_OFFSET;
            bRet &= Load2Sdram(data_ptr,code_size,win_offset);

            // dvbt2_p1
            data_ptr = MSB1240_LIB + MSB1240_DVBT2_P1_START_ADDR;
            code_size = MSB1240_DVBT2_P1_END_ADDR - MSB1240_DVBT2_P1_START_ADDR + 1;
            win_offset = MSB1240_DVBT2_P1_WINDOWS_OFFSET;
            bRet &= Load2Sdram(data_ptr,code_size,win_offset);

            if(bRet == true)
            {
                g_sdram_code |= MSB1240_DVBT2;
            }
#endif
        }
        else
        {
            ERR_DEMOD_MSB(printf("@msb1240, dvbt2 code is unavailable!!!\n"));
        }
    }
    DBG_DEMOD_LOAD_I2C(printf("[msb1240][end]LoadDspCodeToSDRAM_dvbt2\n"));

    return bRet;
}

MAPI_BOOL device_demodulator_extend::LoadDspCodeToSDRAM_dvbt(void)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_BOOL bRet = true;
    MAPI_U16  code_size, win_offset;
    MAPI_U8   *data_ptr;

    DBG_DEMOD_LOAD_I2C(printf("[msb1240][beg]LoadDspCodeToSDRAM_dvbt\n"));

    if(!(g_sdram_code&MSB1240_DVBT))
    {
        // dvbt code
        if(u32SizeOfMSB1240_LIB > MSB1240_DVBT_START_ADDR)
        {
         data_ptr = MSB1240_LIB + MSB1240_DVBT_START_ADDR;
         code_size = MSB1240_DVBT_END_ADDR - MSB1240_DVBT_START_ADDR + 1;
         win_offset = MSB1240_DVBT_WINDOWS_OFFSET;
         bRet &= Load2Sdram(data_ptr,code_size,win_offset);
         if(bRet == true)
         {
             g_sdram_code |= MSB1240_DVBT;
         }
        }
        else
        {
         printf("@msb1240, dvbt code is unavailable!!!\n");
        }
    }
    DBG_DEMOD_LOAD_I2C(printf("[msb1240][end]LoadDspCodeToSDRAM_dvbt\n"));
    return bRet;
}

MAPI_BOOL device_demodulator_extend::LoadDspCodeToSDRAM_dvbc(void)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_BOOL bRet = true;
    MAPI_U16  code_size, win_offset;
    MAPI_U8   *data_ptr;

    DBG_DEMOD_LOAD_I2C(printf("[msb1240][beg]LoadDspCodeToSDRAM_dvbc\n"));
    if(!(g_sdram_code&MSB1240_DVBC))
    {
        // dvbc code
        if(u32SizeOfMSB1240_LIB > MSB1240_DVBC_START_ADDR)
        {
            data_ptr = MSB1240_LIB + MSB1240_DVBC_START_ADDR;
            code_size = MSB1240_DVBC_END_ADDR - MSB1240_DVBC_START_ADDR + 1;
            win_offset = MSB1240_DVBC_WINDOWS_OFFSET;
            bRet &= Load2Sdram(data_ptr,code_size,win_offset);
            if(bRet == true)
            {
               g_sdram_code |= MSB1240_DVBC;
            }
        }
        else
        {
            printf("@msb1240, dvbc code is unavailable!!!\n");
        }
    }
    DBG_DEMOD_LOAD_I2C(printf("[msb1240][end]LoadDspCodeToSDRAM_dvbc\n"));
    return bRet;
}

MAPI_BOOL device_demodulator_extend::LoadDspCodeToSDRAM_dvbs(void)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_BOOL bRet = true;
    MAPI_U16  code_size, win_offset;
    MAPI_U8   *data_ptr;

    DBG_DEMOD_LOAD_I2C(printf("[msb1240][beg]LoadDspCodeToSDRAM_dvbs\n"));
    if(!(g_sdram_code&MSB1240_DVBS))
    {
        // dvbs code 
        if(u32SizeOfMSB1240_LIB > MSB1240_DVBS_P1_START_ADDR)
        {
            // dvbs2_p2
            data_ptr = MSB1240_LIB + MSB1240_DVBS_P2_START_ADDR;
            code_size = MSB1240_DVBS_P2_END_ADDR - MSB1240_DVBS_P2_START_ADDR + 1;
            win_offset = MSB1240_DVBS_P2_WINDOWS_OFFSET;
            bRet &= Load2Sdram(data_ptr,code_size,win_offset);

            // dvbs2_p1
            data_ptr = MSB1240_LIB + MSB1240_DVBS_P1_START_ADDR;
            code_size = MSB1240_DVBS_P1_END_ADDR - MSB1240_DVBS_P1_START_ADDR + 1;
            win_offset = MSB1240_DVBS_P1_WINDOWS_OFFSET;
            bRet &= Load2Sdram(data_ptr,code_size,win_offset);
            if(bRet == true)
            {
                g_sdram_code |= MSB1240_DVBS;
            }
        }
        else
        {
            printf("@msb1240, dvbs code is unavailable!!!\n");
        }
    }
    DBG_DEMOD_LOAD_I2C(printf("[msb1240][end]LoadDspCodeToSDRAM_dvbs\n"));
    return bRet;
}


// [0] bootloader [1] dvbt2, [2] dvbt, [3] dvbc [4] all
MAPI_BOOL device_demodulator_extend::LoadDspCodeToSDRAM(MAPI_U8 code_n)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_BOOL bRet = true;

    DBG_DEMOD_LOAD_I2C(printf("[msb1240][beg]LoadDspCodeToSDRAM, code_n=0x%x\n",code_n));

#if(TIMING_VERIFICATION == 1)
    tmm_7 = GIVE_ME_TIME
#endif

    switch(code_n)
    {
        case MSB1240_BOOT:
            {
                // boot code
                bRet &= LoadDspCodeToSDRAM_Boot();
            }
        break;
        case MSB1240_DVBT2:
            {
                // dvbt2 code
                bRet &= LoadDspCodeToSDRAM_dvbt2();
            }
            break;
        case MSB1240_DVBT:
            {
                // dvbt
                bRet &= LoadDspCodeToSDRAM_dvbt();
            }
            break;
        case MSB1240_DVBC:
            {
                // dvbtc
                bRet &= LoadDspCodeToSDRAM_dvbc();
            }
            break;
        case MSB1240_DVBS:
            {
                // dvbts
                bRet &= LoadDspCodeToSDRAM_dvbs();
            }
            break;
        case MSB1240_ALL:
        default:
            {
                // boot+dvbt2+dvbt+dvbc+dvbs

                // boot code
                bRet &= LoadDspCodeToSDRAM_Boot();
#if (DVBT_SYSTEM_ENABLE == 1)
                // dvbt2
                bRet &= LoadDspCodeToSDRAM_dvbt2();
                // dvbt
                bRet &= LoadDspCodeToSDRAM_dvbt();
#endif
#if (DVBC_SYSTEM_ENABLE == 1)
                // dvbc
                bRet &= LoadDspCodeToSDRAM_dvbc();
#endif
#if (DVBS_SYSTEM_ENABLE == 1)
                // dvbs
                bRet &= LoadDspCodeToSDRAM_dvbs();
#endif
            }
            break;
    }

#if(TIMING_VERIFICATION == 1)
    tmm_8 = GIVE_ME_TIME
    show_timer();
#endif

    DBG_DEMOD_LOAD_I2C(printf("[msb1240][end]LoadDspCodeToSDRAM, code_n=0x%x, bRet=0x%x\n",code_n,bRet));
    return bRet;
}

// mem_type 0: dram, 1:dram+sram
// 28 ms roughly
MAPI_BOOL device_demodulator_extend::MSB1240_MEM_switch(MAPI_U8 mem_type)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));

    MAPI_BOOL bRet = true;
    MAPI_U8 u8_tmp = 0;
    MAPI_U16 timeout = 0;
    MAPI_U8 u8Data = 0;

    DBG_DEMOD_LOAD_I2C(printf("[msb1240][beg]MSB1240_MEM_switch, mem_type=0x%x\n",mem_type));

#if(TIMING_VERIFICATION == 1)
    tmm_15 = GIVE_ME_TIME
#endif

    switch(device_demodulator_extend::m_enCurrentDemodulator_Type)
    {
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2:
            u8Data=1;
            break;
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
            u8Data=2;
            break;
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_C:
            u8Data=3;
            break;
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_S:
            u8Data=4;
            break;
        default:
            u8Data=2;
            return MAPI_FALSE;
    }


    if(mem_type == 1)
    {
        // Enable SRAM+SDRAM memory map

        // SRAM_START_ADDR 0x0000
        bRet &= WriteReg2bytes(0x1000,0x0000);
        bRet &= WriteReg2bytes(0x1004,0x0000);

        // SRAM_END_ADDR 0x7FFF
        bRet &= WriteReg2bytes(0x1002,0x0000);
        bRet &= WriteReg2bytes(0x1006,0x7FFF);

        if (u8Data == 4)
        {

            bRet &= WriteReg(0x2B80,0x12);
            // DRAM_START_ADDR 1M+0x2*0x1000+0x8000
            bRet &= WriteReg2bytes(0x1008,0x0000);
            bRet &= WriteReg2bytes(0x100C,0x8000);


            // DRAM_END_ADDR   1M+0x2*0x1000+0xFFFF
            bRet &= WriteReg2bytes(0x100A,0x0000);
            bRet &= WriteReg2bytes(0x100E,0xFFFF);
        }
        else
        {
            bRet &= WriteReg(0x2B80,0x10);

            // DRAM_START_ADDR 0x8000
            bRet &= WriteReg2bytes(0x1008,0x0000);
            bRet &= WriteReg2bytes(0x100C,0x8000);


            // DRAM_END_ADDR    0xFFFF
            bRet &= WriteReg2bytes(0x100A,0x0000);
            bRet &= WriteReg2bytes(0x100E,0xFFFF);
        }
        // Enable SRAM&SDRAM memory map
        bRet &= WriteReg(0x1018,0x05);

        // Wait memory map to be enabled
        do
        {
            bRet &= ReadReg(0x1018,&u8_tmp);
            if(timeout++ > 500)
            {
                printf("@msb1240, D+S memory mapping failure.!!!\n");
                return MAPI_FALSE;
            }
        }
        while(u8_tmp != 0x05);
    }
    else if(mem_type == 0)
    {
        // Enable SDRAM memory map
        // Run bootloader from SDRAM 1M

        bRet &= WriteReg(0x2B80,0x10);

        // DRAM_START_ADDR 0x0000
        bRet &= WriteReg2bytes(0x1008,0x0000);
        bRet &= WriteReg2bytes(0x100C,0x0000);

        // DRAM_END_ADDR    0x7FFF
        bRet &= WriteReg2bytes(0x100A,0x0000);
        bRet &= WriteReg2bytes(0x100E,0x7FFF);

        // Enable SRAM&SDRAM memory map
        bRet &= WriteReg(0x1018,0x04);

        // Wait memory map to be enabled
        do
        {
            bRet &= ReadReg(0x1018,&u8_tmp);
            if(timeout++ > 500)
            {
                printf("@msb1240, D memory mapping failure.!!!\n");
                return MAPI_FALSE;
            }
        }
        while(u8_tmp != 0x04);
    }
    else
    {
       printf("@msb1240, invalid mem type mapping.\n");
       return MAPI_FALSE;
    }
#if(TIMING_VERIFICATION == 1)
    tmm_16 = GIVE_ME_TIME
#endif

    DBG_DEMOD_LOAD_I2C(printf("[msb1240][end]MSB1240_MEM_switch, , mem_type=0x%x, bRet=0x%x\n",mem_type,bRet));

    return bRet;
}

MAPI_BOOL device_demodulator_extend::LoadDSPCode(void)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));

    mapi_scope_lock(scopeLock, &m_MutexOuter);
    DBG_DEMOD_LOAD_I2C(printf("[msb1240][beg]LoadDspCode\n"));

    MAPI_U32        u32Timeout = 0;
    MAPI_U8         u8DoneFlag = 0;
#if (DVBS_SYSTEM_ENABLE == 1)
    MAPI_U8        FWversionH = 0xFF;
    MAPI_U8        FWversionL = 0xFF;
#endif
    MAPI_U8         u8Data = 0;
    MAPI_BOOL       bRet = true;
    MAPI_U8         u8_timeout=0xff;

    DBG_DEMOD_MSB(printf(">>>MSB1240: Load DSP...\n"));

    switch(device_demodulator_extend::m_enCurrentDemodulator_Type)
    {
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2:
            u8Data=1;
            break;
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
            u8Data=2;
            break;
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_C:
            u8Data=3;
            break;
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_S:
            u8Data=4;
            break;
        default:
            u8Data=2;
            return MAPI_FALSE;
    }
    
    printf("this is load LoadDSPCode \n");
    if(g_WO_SPI_FLASH == 1)
    {//I2C or MSPI path
        MAPI_U8 u8FirmwareType = MSB1240_DVBT;
        // mask miu access for all and mcu   
        bRet &= WriteReg(0x1200+(0x23)*2 + 1,0x7f);        
        bRet &= WriteReg(0x1200+(0x23)*2,0xfe);        
        // 10us delay        
        usleep(10);        
        // Disable MCU        
        bRet &= WriteReg(0x0b00+(0x19)*2, 0x03);

        switch (u8Data)
        {
            case 1:
                u8FirmwareType = MSB1240_DVBT2;
                break;
            case 2:
            default:
                u8FirmwareType = MSB1240_DVBT;
                break;
            case 3:
                u8FirmwareType = MSB1240_DVBC;
                break;
            case 4:
                u8FirmwareType = MSB1240_DVBS;
                break;
        }

        bRet &= LoadDspCodeToSDRAM(u8FirmwareType);

        bRet &= LoadSdram2Sram(u8FirmwareType);

        // enable miu access of mcu gdma
        bRet &= WriteReg(0x1200+(0x23)*2,0xf0);
        // 10us delay
        usleep(10);

        // Enable MCU
        bRet &= WriteReg(0x0b00+(0x19)*2, 0x00);

    }
    else
    {//flash path
#if(TIMING_VERIFICATION == 1)
        tmm_9 = GIVE_ME_TIME
#endif

        // mask miu access of fdp, tdi, djb
        printf("this is 2 \n");
        bRet &= WriteReg(0x1200+(0x23)*2 + 1,0x0f);
        bRet &= WriteReg(0x1200+(0x23)*2,0xf0);

        // 10us delay
        usleep(10);

        // Disable MCU
        bRet &= WriteReg(0x0b00+(0x19)*2, 0x03);

        // Use GDMA move code from SDRAM to SRAM
         bRet &= WriteReg2bytes(0x0300+(0x00)*2, 0x4254);     // rst
         bRet &= WriteReg2bytes(0x0300+(0x00)*2, 0x4257);    // cfg & trig
       
       switch(u8Data)
       {
            case 0x01: //DVB-T2 @001000h ~ 008FFFh
                bRet &= WriteReg2bytes(0x0300+(0x01)*2, 0x0000); // Set src_addr
                bRet &= WriteReg2bytes(0x0300+(0x02)*2, 0x0010); // start from 1M
                break;

            case 0x02: //DVB-T @010000h ~ 017FFFh
                bRet &= WriteReg2bytes(0x0300+(0x01)*2, 0x0000); // Set src_addr
                bRet &= WriteReg2bytes(0x0300+(0x02)*2, 0x0011); // start from 1M+64k
                break;

            case 0x03: //DVB-C @018000h ~ 01FFFFh
                bRet &= WriteReg2bytes(0x0300+(0x01)*2, 0x8000); // Set src_addr
                bRet &= WriteReg2bytes(0x0300+(0x02)*2, 0x0011); // start from 1M+96k
                break;
            case 0x04: //DVB-S @ 0x20000h ~ 0x27ff0h 
                bRet &= WriteReg2bytes(0x0300+(0x01)*2, 0x0000); // Set src_addr
                bRet &= WriteReg2bytes(0x0300+ (0x02)*2, 0x0012); // start from 1M+128k
                break;
                default:
                bRet &= false;
                break;
       }

        // Set dst_addr
        bRet &= WriteReg2bytes(0x0300+(0x03)*2, 0x0000);
        bRet &= WriteReg2bytes(0x0300+(0x04)*2, 0x0000);
            
        // Set data_size
        bRet &= WriteReg2bytes(0x0300+(0x05)*2, 0x8000);
        bRet &= WriteReg2bytes(0x0300+(0x06)*2, 0x0000);

        bRet &= WriteReg(0x0300+(0x07)*2, 0x01); //start GDMA

#if(TIMING_VERIFICATION == 1)
        tmm_10 = GIVE_ME_TIME
        printf("[tmm8]t10 - t9 = %d (%d - %d)\n",tmm_10-tmm_9,tmm_10,tmm_9);
#endif

        // Wait for GDMA
        do
        {
            bRet &= ReadReg(0x0300+(0x08)*2, &u8DoneFlag);

            if (u32Timeout++ > 500)
            {
                return MAPI_FALSE;
            }

            usleep(500);
        }while((u8DoneFlag&0x01) != 0x01);



        // Wait for GDMA
        do
        {
            MsOS_DelayTask(1);
            ReadReg(0x0300+ 0x10,&u8DoneFlag);
            u8_timeout--;
        }while(((u8DoneFlag&0x01) != 0x01) && (u8_timeout != 0x00));

        if((u8DoneFlag&0x01) != 0x01)
        {
            printf("Fail to LoadSdram2Sram!!");
            return MAPI_FALSE;
        }


        printf("this si 3 \n");
#if(TIMING_VERIFICATION == 1)
        tmm_11 = GIVE_ME_TIME
        printf("[tmm8]t11 - t10 = %d (%d - %d)\n",tmm_11-tmm_10,tmm_11,tmm_10);
#endif

        //if(u8Data == 0x01)
        //    bRet &= MSB1240_MEM_switch(2);  // dvbt2 setting for rom code
        //else
            bRet &= MSB1240_MEM_switch(1);
    
        printf("this is 4 \n");

        // enable miu access of mcu gdma
        bRet &= WriteReg(0x1200+(0x23)*2,0xf0);

        //Disable romA_en & romB_en
        bRet &=WriteReg2bytes(0x1000+ (0x1c)*2, 0x0000);

        // 10us delay
        usleep(10);

        // Enable MCU
        WriteReg(0x0b00+(0x19)*2, 0x00);
    }

#if(TIMING_VERIFICATION == 1)
    tmm_12 = GIVE_ME_TIME
    printf("[tmm8]t12 - t11 = %d (%d - %d), TYPE is %d \n",tmm_12-tmm_11,tmm_12,tmm_11,device_demodulator_extend::m_enCurrentDemodulator_Type);
#endif

    printf("1: %d, 2:%d, 3:%d \n",mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2,mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T,mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_C);
    switch(device_demodulator_extend::m_enCurrentDemodulator_Type)
    {
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2:
            printf("11111\n");
            DTV_DVBT2_DSPReg_Init();
            break;
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
            printf("2222\n");
            DTV_DVBT_DSPReg_Init();
            break;
#if (DVBC_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_C:
            printf("3333\n");
            DTV_DVBC_DSPReg_Init();
            break;
#endif
#if (DVBS_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_S:
            IIC_Bypass_Mode(MAPI_TRUE);
            bRet&=MDrv_DVBS_Tuner_Initial();
            IIC_Bypass_Mode(MAPI_FALSE);
            bRet &=ReadReg(0x0900+(0x44)*2, &FWversionL);
            bRet &=ReadReg(0x0900+(0x44)*2+0x0001, &FWversionH);	 
            printf("MSB1240_S FW version :  %X.%X\n",FWversionL,FWversionH);
            printf("4444\n");
            break;
#endif
        default:
            return MAPI_FALSE;
    }

#if(TIMING_VERIFICATION == 1)
    tmm_13 = GIVE_ME_TIME
    printf("[tmm8]t13 - t12 = %d (%d - %d)\n",tmm_13-tmm_12,tmm_13,tmm_12);
#endif

    DBG_DEMOD_LOAD_I2C(printf("[msb1240][end]LoadDspCode\n"));
    return bRet;
}

static MAPI_BOOL IspProcFlash(MAPI_U8* pLibArry, MAPI_U32 dwLibSize)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_U32    dwLoop, dwTimeOut;
    MAPI_U32    dwStartAddr, dwEndAddr;
    //MAPI_U16    wLoop;
    MAPI_U8     bError = false;//, bWriteData[PAGE_WRITE_SIZE];

    MAPI_U8 bWriteData[5]={0x4D, 0x53, 0x54, 0x41, 0x52};
    mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_2);
    iptr->WriteBytes(0, NULL, 5, bWriteData);

    dwStartAddr = (MAPI_U32) 0;
    dwEndAddr = dwLibSize;


    MAPI_U8     bAddr[1];
    MAPI_U8     bReadData=0;
    dwLoop = dwStartAddr;
    while(1)
    {
        dwTimeOut = 10000;
        while(dwTimeOut--)
        {
            bAddr[0] = 0x10;
            bWriteData[0] = 0x05;
            iptr->WriteBytes(1, bAddr, 1, bWriteData);

            bAddr[0] = 0x11;
            iptr->ReadBytes(1, bAddr, 1, &bReadData);

            bWriteData[0] = 0x12;
            iptr->WriteBytes(0, NULL, 1, bWriteData);

            if (!(bReadData & 0x01))
                break;

            if(dwTimeOut==1)
            {
                bError = 1;
                break;
            }
            usleep(100);
        }

        if (dwLoop >= dwEndAddr)
            break;

        if (bError)
        {
            printf("flash program timeout %d\n",__LINE__);
            break;
        }
        else
        {
            MAPI_U8    bAddr[5], bWriteData[1];
            MAPI_BOOL bError2 = TRUE;

            mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_2);

            bAddr[0] = 0x10;
            bWriteData[0] = 0x06;
            bError2 &= iptr->WriteBytes(1, bAddr, 1, bWriteData);

            bWriteData[0] = 0x12;
            bError2 &= iptr->WriteBytes(0, NULL, 1, bWriteData);

            // Page Program
            bAddr[0] = 0x10;
            bAddr[1] = 0x02;
            bAddr[2] = dwLoop >> 16;
            bAddr[3] = dwLoop >> 8;
            bAddr[4] = dwLoop;

            bError2 &= iptr->WriteBytes(5, bAddr, PAGE_WRITE_SIZE, (pLibArry+dwLoop));

            bWriteData[0] = 0x12;
            bError2 &=  iptr->WriteBytes(0, NULL, 1, bWriteData);

            bAddr[0] = 0x10;
            bWriteData[0] = 0x04;
            bError2 &=  iptr->WriteBytes(1, bAddr, 1, bWriteData);

            bWriteData[0] = 0x12;
                bError2 &=  iptr->WriteBytes(0, NULL, 1, bWriteData);

            if(bError2 == FALSE)
            {
                break;
            }
        }
        dwLoop += PAGE_WRITE_SIZE;
    }

    bWriteData[0]=0x24 ;
    iptr->WriteBytes(0, NULL, 1, bWriteData);

    if(bError==false)
        return MAPI_TRUE;
    else
        return MAPI_FALSE;

}

MAPI_BOOL device_demodulator_extend::DeviceDemodCreate()
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    mapi_demodulator *pMapiDemod;
    pMapiDemod = mapi_interface::Get_mapi_demod_extend();
    //Public:Common
    pMapiDemod->stMapiDemodulatorFun.Connect = Connect;
    pMapiDemod->stMapiDemodulatorFun.Disconnect = Disconnect;
    pMapiDemod->stMapiDemodulatorFun.Reset = Reset;
    pMapiDemod->stMapiDemodulatorFun.IIC_Bypass_Mode = IIC_Bypass_Mode;
    pMapiDemod->stMapiDemodulatorFun.Power_On_Initialization = Power_On_Initialization;
    pMapiDemod->stMapiDemodulatorFun.Set_PowerOn = Set_PowerOn;
    pMapiDemod->stMapiDemodulatorFun.Set_PowerOff = Set_PowerOff;
    pMapiDemod->stMapiDemodulatorFun.Active = Active;
    pMapiDemod->stMapiDemodulatorFun.GetCurrentDemodulatorType = GetCurrentDemodulatorType;
    pMapiDemod->stMapiDemodulatorFun.ExtendCmd = ExtendCmd;

    //Public:DTV
    pMapiDemod->stMapiDemodulatorFun.DTV_SetFrequency = DTV_SetFrequency;
    pMapiDemod->stMapiDemodulatorFun.DTV_GetSNR = DTV_GetSNR;
    pMapiDemod->stMapiDemodulatorFun.DTV_GetBER = DTV_GetBER;
    pMapiDemod->stMapiDemodulatorFun.DTV_GetPacketErr = DTV_GetPacketErr;
    pMapiDemod->stMapiDemodulatorFun.DTV_GetPostBER = DTV_GetPostBER;
    pMapiDemod->stMapiDemodulatorFun.DTV_GetSignalQuality = DTV_GetSignalQuality;
    pMapiDemod->stMapiDemodulatorFun.DTV_GetSignalStrength = DTV_GetSignalStrength;
    pMapiDemod->stMapiDemodulatorFun.DTV_Serial_Control = DTV_Serial_Control;
    pMapiDemod->stMapiDemodulatorFun.DTV_GetCellID = DTV_GetCellID;
    pMapiDemod->stMapiDemodulatorFun.DTV_IsHierarchyOn = DTV_IsHierarchyOn;
    //pMapiDemod->stMapiDemodulatorFun.msb1240_flash_check =msb1240_flash_check;

   

    //Public:DTV-DVB-T
    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_T_GetLockStatus = DTV_DVB_T_GetLockStatus;

#if (DVBC_SYSTEM_ENABLE == 1)
    //Public:DTV-DVB-C
    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_C_GetLockStatus = DTV_DVB_C_GetLockStatus;
    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_C_SetFrequency = DTV_DVB_C_SetFrequency;
    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_C_GetCurrentSymbolRate = DTV_DVB_C_GetCurrentSymbolRate;
    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_C_GetSignalModulation = DTV_DVB_C_GetSignalModulation;
#endif

    //CCI_Check( COFDM_CHECK_FLAG eFlag );
    //SetDvbcParam(MAPI_U8 constel);
    //GetDvbcInfo( MAPI_U32 * DVBC_parameter);
    //MSB1240_Lock(E_SYSTEM system, COFDM_LOCK_STATUS eStatus );
    //Set_Config_dvbc_atv_detector(MAPI_U8 bEnable);
    //MSB1240_Config_DVBC(MAPI_U16 SymbolRate, MAPI_U32 u32IFFreq, MAPI_U16 bSpecInv);

    //Public:DTV-DVB-T2
    pMapiDemod->stMapiDemodulatorFun.SetCurrentDemodulatorType = SetCurrentDemodulatorType;
    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_T2_GetPlpBitMap = DTV_GetPlpBitMap;
    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_T2_GetPlpGroupID = DTV_GetPlpGroupID;
    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_T2_SetPlpGroupID = DTV_SetPlpGroupID;

    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_T_GetSignalModulation = DTV_DVB_T_GetSignalModulation;
    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_T_GetSignalGuardInterval = DTV_DVB_T_GetSignalGuardInterval;
    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_T_GetSignalFFTValue = DTV_DVB_T_GetSignalFFTValue;
    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_T_GetSignalCodeRate = DTV_DVB_T_GetSignalCodeRate;

#if (DVBS_SYSTEM_ENABLE == 1)
    //Public:DTV-DVB-S
    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_S_SetFrequency = DTV_DVB_S_SetFrequency;
    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_S_GetLockStatus = DTV_DVB_S_GetLockStatus;

    //blind scan
    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_S_BlindScan_Init = DTV_DVB_S_BlindScan_Init;
    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_S_BlindScan_ScanNextFreq = DTV_DVB_S_BlindScan_ScanNextFreq;
    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_S_BlindScan_GetFoundTP = DTV_DVB_S_BlindScan_GetFoundTP;
    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_S_BlindScan_Cancel = DTV_DVB_S_BlindScan_Cancel;
    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_S_BlindScan_End = DTV_DVB_S_BlindScan_End;
    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_S_BlindScan_GetStatus = DTV_DVB_S_BlindScan_GetStatus;
    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_S_BlindScan_GetScanFreq = DTV_DVB_S_BlindScan_GetScanFreq;

    mapi_dish* pMapiDish;
    pMapiDish = mapi_interface::Get_mapi_dish();
     
    pMapiDish->stMapiDishFun.DTV_DVB_S_SetToneBurst = DTV_DVB_S_SetToneBurst;
    pMapiDish->stMapiDishFun.DTV_DVB_S_SendDiSEqCCmd = DTV_DVB_S_SendDiSEqCCmd;
    // pMapiDish->stMapiDishFun.DTV_DVB_S_SetLNBPower = DTV_DVB_S_SetLNBPower;

    device_dish::DeviceDishCreate();
    pMapiDish->stMapiDishFun.DTV_DVB_S_Set22KOnOff = DTV_DVB_S_Set22KOnOff;
    pMapiDish->stMapiDishFun.DTV_DVB_S_Get22KStatus = DTV_DVB_S_Get22KStatus;
#endif
#if 0
    pMapiDemod->stMapiDemodulatorFun.GetDemodInfo                               = GetDemodInfo;
    pMapiDemod->stMapiDemodulatorFun.GetDVBT2_Demod_s1_siso_miso                = GetDVBT2_Demod_s1_siso_miso;
    pMapiDemod->stMapiDemodulatorFun.GetDVBT2_Demod_plp_number                  = GetDVBT2_Demod_plp_number;
    pMapiDemod->stMapiDemodulatorFun.GetDVBT2_Demod_plp_id                      = GetDVBT2_Demod_plp_id;
    pMapiDemod->stMapiDemodulatorFun.GetDVBT2_Demod_constellation_l1            = GetDVBT2_Demod_constellation_l1;
    pMapiDemod->stMapiDemodulatorFun.GetDVBT2_Demod_coderate_l1                 = GetDVBT2_Demod_coderate_l1;
    pMapiDemod->stMapiDemodulatorFun.GetDVBT2_Demod_plp_constellation           = GetDVBT2_Demod_plp_constellation;
    pMapiDemod->stMapiDemodulatorFun.GetDVBT2_Demod_plp_rotation_constellation  = GetDVBT2_Demod_plp_rotation_constellation;
    pMapiDemod->stMapiDemodulatorFun.GetDVBT2_Demod_plp_coderate                = GetDVBT2_Demod_plp_coderate;
    pMapiDemod->stMapiDemodulatorFun.GetDVBT2_Demod_plp_fec                     = GetDVBT2_Demod_plp_fec;
    pMapiDemod->stMapiDemodulatorFun.GetDVBT2_Demod_plp_frame_il_length         = GetDVBT2_Demod_plp_frame_il_length;
    pMapiDemod->stMapiDemodulatorFun.GetDVBT2_Demod_plp_type_time_il            = GetDVBT2_Demod_plp_type_time_il;
    pMapiDemod->stMapiDemodulatorFun.GetDVBT2_Demod_plp_length_time_il          = GetDVBT2_Demod_plp_length_time_il;
    pMapiDemod->stMapiDemodulatorFun.GetDVBT2_Demod_pilot_pattern               = GetDVBT2_Demod_pilot_pattern;
    pMapiDemod->stMapiDemodulatorFun.GetDVBT2_Demod_mode_carrier                = GetDVBT2_Demod_mode_carrier;
    pMapiDemod->stMapiDemodulatorFun.GetDVBT2_Demod_fef_type                    = GetDVBT2_Demod_fef_type;
    pMapiDemod->stMapiDemodulatorFun.GetDVBT2_Demod_fef_length                  = GetDVBT2_Demod_fef_length;
    pMapiDemod->stMapiDemodulatorFun.GetDVBT2_Demod_fef_interval                = GetDVBT2_Demod_fef_interval;
    pMapiDemod->stMapiDemodulatorFun.GetDVBT2_Demod_papr                        = GetDVBT2_Demod_papr;
    pMapiDemod->stMapiDemodulatorFun.GetDVBT2_Demod_guard_interval              = GetDVBT2_Demod_guard_interval;
#endif

#if (PRELOAD_DSP_CODE_FROM_MAIN_CHIP_I2C == 1)

    int intPTHChk;

    pthread_t pthread_id;
    pthread_attr_t attr1;
    pthread_attr_init(&attr1);
    pthread_attr_setdetachstate(&attr1, PTHREAD_CREATE_DETACHED);

    intPTHChk = PTH_RET_CHK(pthread_create(&pthread_id, &attr1, PreLoadDSPcode, NULL));

    if(intPTHChk != 0)
    {
        ASSERT(0);
    }
#endif

    pthread_mutex_init(&m_MutexOuter, NULL);
    pthread_mutex_init(&m_MutexRWDspReg, NULL);
    pthread_mutex_init(&m_MutexRWReg, NULL);

    return MAPI_TRUE;
}
//m
void* device_demodulator_extend::PreLoadDSPcode(void *arg)
{
#if (STR_ENABLE == 1)
    prctl(PR_SET_NAME, (unsigned long)"PreLoadDSPcode Task");
    mapi_str::AutoRegister _R;
#endif

#if (DVBS_SYSTEM_ENABLE == 1)
    MAPI_U8        FWversionH = 0xFF;
    MAPI_U8        FWversionL = 0xFF;
#endif

    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    ERR_DEMOD_MSB(printf("[msb1240][beg] PreLoadDSPcode beg...\n"));

    MAPI_U8     status = TRUE;

    Connect(mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2);

    status &= I2C_CH_Reset(3);
    if(status == MAPI_FALSE)
    {
        ERR_DEMOD_MSB(printf("[msb1240][err] I2C_CH_Reset failure...\n"));
    }

#if USE_SPI_LOAD_TO_SDRAM
#if (NEED_SWITCH_TSPAD_FOR_MSPI_USE == 1)
    MSPI_PAD_Enable(0, MAPI_TRUE);
#endif

    //Initialize SPI0 for MSPI
    if(MDrv_MSPI_Init_Ext(0) != E_MSPI_OK)
    {
        ERR_DEMOD_MSB(printf(">>>>MDrv_MSPI_Init_Ext:Fail\n"));
        status= MAPI_FALSE;
    }
#endif

    status &= DTV_DVB_HW_init();
    if(status == MAPI_FALSE)
    {
        ERR_DEMOD_MSB(printf("[msb1240][err] MSB1240_HW_init failure...\n"));
    }

    mapi_scope_lock(scopeLock, &m_MutexOuter);
#if (PRELOAD_DSP_CODE_FROM_MAIN_CHIP_I2C_ONLY_LOAD_T2 == 1)
    status &= LoadDspCodeToSDRAM_dvbt2();
    if(status == MAPI_TRUE)
    {
        goto ExitPreload;
    }
    else
    {
        ERR_DEMOD_MSB(printf("[msb1240][err] LoadDspCodeToSDRAM DVBT2 failure...\n"));
    }

#else
    status &= LoadDspCodeToSDRAM(MSB1240_ALL);
    if(status == MAPI_FALSE)
    {
        ERR_DEMOD_MSB(printf("[msb1240][err] LoadDspCodeToSDRAM failure...\n"));
    }

    MAPI_U8 u8Data = 0;
    switch(device_demodulator_extend::m_enCurrentDemodulator_Type)
    {
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2:
            u8Data=MSB1240_DVBT2;
            break;
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
            u8Data=MSB1240_DVBT;
            break;
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_C:
            u8Data=MSB1240_DVBC;
            break;
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_S:
            u8Data=MSB1240_DVBS;
            break;
        default:
            u8Data=MSB1240_DVBT;
            goto ExitPreload;
            break;
    }

#if (MSPI_ENABLE)
    // mask miu access for all and mcu
    MDrv_SS_RIU_Write8(0x1200+(0x23)*2 + 1,0x7f);
    MDrv_SS_RIU_Write8(0x1200+(0x23)*2,0xfe);
    // 10us delay
    usleep(10);

    status &= MDrv_SS_RIU_Write8(0x0b00 + (0x19) * 2, 0x03);

    status &= LoadSdram2Sram(u8Data);

    // enable miu access of mcu gdma
    MDrv_SS_RIU_Write8(0x1200+(0x23)*2,0xf0);
    // 10us delay
    usleep(10);

    // Enable MCU
    status &= MDrv_SS_RIU_Write8(0x0b00+(0x19)*2, 0x00);
#else
    // mask miu access for all and mcu
    WriteReg(0x1200+(0x23)*2 + 1,0x7f);
    WriteReg(0x1200+(0x23)*2,0xfe);
    // 10us delay
    usleep(10);

    status &= WriteReg(0x0b00 + (0x19) * 2, 0x03);

    status &= LoadSdram2Sram(u8Data);

    // enable miu access of mcu gdma
    WriteReg(0x1200+(0x23)*2,0xf0);
    // 10us delay
    usleep(10);

    // Enable MCU
    status &= WriteReg(0x0b00+(0x19)*2, 0x00);
#endif

    switch(device_demodulator_extend::m_enCurrentDemodulator_Type)
    {
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2:
            DTV_DVBT2_DSPReg_Init();
            break;
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
            DTV_DVBT_DSPReg_Init();
            break;
#if (DVBC_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_C:
            DTV_DVBC_DSPReg_Init();
            break;
#endif
#if (DVBS_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_S:
            IIC_Bypass_Mode(MAPI_TRUE);
            MDrv_DVBS_Tuner_Initial();
            IIC_Bypass_Mode(MAPI_FALSE);
            ReadReg(0x0900+(0x44)*2, &FWversionL);
            ReadReg(0x0900+(0x44)*2+0x0001, &FWversionH);
            printf("MSB1240_S FW version :  %X.%X\n",FWversionL,FWversionH);
            break;
#endif
        default:
            goto ExitPreload;
    }

    if(status == MAPI_FALSE)
    {
        ERR_DEMOD_MSB(printf(">>>>MSB1240:PreloadDSPcode Fail\n"));
    }
    else
    {
        bPower_init_en = TRUE;
        g_u8_msb1240_sleep_mode_status = 0;
        ERR_DEMOD_MSB(printf("[msb1240][end] PreLoadDSPcode end...\n"));
    }
#endif
ExitPreload:
#if USE_SPI_LOAD_TO_SDRAM && (NEED_SWITCH_TSPAD_FOR_MSPI_USE == 1)
    MSPI_PAD_Enable(0, MAPI_FALSE);
#endif
    pthread_exit(NULL);
}

/*@ <Operation ID=I2b28dd03m121c8cf959bmm722c> @*/

MAPI_BOOL device_demodulator_extend::Connect(mapi_demodulator_datatype::EN_DEVICE_DEMOD_TYPE enDemodType)
{
    mapi_scope_lock(scopeLock, &m_MutexOuter);
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    DBG_DEMOD_MSB(printf("device_demodulator_msb1240: Connect type=%d\n",(int)enDemodType));

#if (MIX_EDINBURGH_WINDERMERE_ENABLE == 1)//for EDINBURGH and WINDERMERE
    mapi_mspi::Init();
    mapi_mspi::SetPadInfo(DEMOD);
    mapi_mspi::SetMaxClkInfo(DEMOD);
#endif

    if (u8DeviceBusy == 1)
        return MAPI_FALSE;
    device_demodulator_extend::m_enCurrentDemodulator_Type = enDemodType;

    // Leave sleep mode if t2 demod is in sleep.
    if (g_u8_msb1240_sleep_mode_status != 0)
    {
        (printf("T2 demod leave sleep mode \n"));
        ExtendCmd(0x85, 0, 0, NULL); 
    }
    u8DeviceBusy = 1;
    return MAPI_TRUE;
}

/*@ </Operation ID=I2b28dd03m121c8cf959bmm722c> @*/
/*@ <Operation ID=I2b28dd03m121c8cf959bmm7207> @*/

MAPI_BOOL device_demodulator_extend::Disconnect(void)
{
    mapi_scope_lock(scopeLock, &m_MutexOuter);
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    DBG_DEMOD_MSB(printf("device_demodulator_msb1240: Disconnect\n"));

    // Enter sleep mode
    if (g_u8_msb1240_sleep_mode_status == 0)
    {
        (printf("T2 demod enter sleep mode\n"));
        ExtendCmd(0x85, 1, 0, NULL);
    }
    
    u8DeviceBusy = 0;

    return MAPI_TRUE;
}

#include "drvXC_IOPort.h"
#include "apiXC.h"

void device_demodulator_extend::Reset()
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_U8     u8Data = 0x00;
    MAPI_U32    u32Retry = 0x00;

    DBG_DEMOD_MSB(printf(">>>MSB1240: Reset()\n"));
    u32StartTime = MsOS_GetSystemTime();
    DBG_DEMOD_MSB(printf("\t\t\tRESET TIME   %d\n", u32StartTime));//to measure time
    bDoReset = TRUE;

    // mask miu access for all and mcu
    WriteReg(0x1200+(0x23)*2 + 1,0x0f);
    WriteReg(0x1200+(0x23)*2,0xf0);
    // 10us delay
    usleep(10);

    WriteReg(0x0B00 + (0x19) * 2, 0x03);
    WriteReg(0x0B00 + (0x10) * 2, 0x01);
    WriteReg(REG_MB_CNTL, 0x00);             //clear MB_CNTL
    usleep(5 * 100);

    // enable miu access of mcu gdma
    WriteReg(0x1200+(0x23)*2 + 1,0x00);
    WriteReg(0x1200+(0x23)*2,0x00);
    // 10us delay
    usleep(10);

    WriteReg(0x0B00 + (0x10) * 2, 0x00);
    WriteReg(0x0B00 + (0x19) * 2, 0x00);
    usleep(5 * 1000);
    ReadReg(REG_MB_CNTL, &u8Data);           //check MB_CNTL ready
    while(u8Data != 0xff)
    {
        usleep(50*1000);
        ReadReg(REG_MB_CNTL, &u8Data);       //check MB_CNTL ready
        if (u32Retry++ > 200)
        {
            ERR_DEMOD_MSB(printf(">>>MSB1240: Reset Fail!\n"));
            break;
        }
    }

    //printf(">>>MSB1240: Reset ok!\n");

    FECLock = MAPI_FALSE;
    u32ChkScanTimeStart = MsOS_GetSystemTime();
    p1_ever_lock_flag = 0;

}

MAPI_BOOL device_demodulator_extend::IIC_Bypass_Mode(MAPI_BOOL enable)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    DBG_DEMOD_MSB(printf(">>>MSB1240: IIC_bypass() set %x\n", enable));

    if (enable)
        WriteReg(0x0900+(0x08)*2, 0x10);// IIC by-pass mode on
    else
        WriteReg(0x0900+(0x08)*2, 0x00);// IIC by-pass mode off

    return MAPI_TRUE;
}

#if (STR_ENABLE == 1)
MAPI_BOOL device_demodulator_extend::ResumeInit(void)
{
    bPower_init_en = FALSE;
#if (PRELOAD_DSP_CODE_FROM_MAIN_CHIP_I2C_ONLY_LOAD_T2 == 1)
    bPreload_T2_en = MAPI_FALSE;
    bSTR_mode_en = MAPI_TRUE;
#endif
    g_u8_msb1240_sleep_mode_status = 1;
    g_sdram_code = 0x0;
    g_sram_code  = 0x0;

    Power_On_Initialization();
    return MAPI_TRUE;
}
#endif

MAPI_BOOL device_demodulator_extend::Power_On_Initialization(void)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    

#if(TIMING_VERIFICATION == 1)
    tmm_1 = GIVE_ME_TIME
#endif


#if (PRELOAD_DSP_CODE_FROM_MAIN_CHIP_I2C == 1)
#if (PRELOAD_DSP_CODE_FROM_MAIN_CHIP_I2C_ONLY_LOAD_T2 == 0)
    return MAPI_TRUE;
#endif
#endif
    MAPI_U8     status = TRUE;
    MAPI_BOOL   bMatch = false;
    MAPI_U8     u8RetryCnt = 6;

    DBG_DEMOD_MSB(printf(">>>MSB1240: Enter Power_On_Initialization()\n"));
    if (bPower_init_en == TRUE)
    {
        return  MAPI_TRUE;
    }

    if(g_WO_SPI_FLASH == 1)
    {
#if (PRELOAD_DSP_CODE_FROM_MAIN_CHIP_I2C == 1)
#if (PRELOAD_DSP_CODE_FROM_MAIN_CHIP_I2C_ONLY_LOAD_T2 == 1)
        status &= I2C_CH_Reset(3);
        if (bSTR_mode_en == MAPI_TRUE)
        {
            status &= MSB1240_HW_init();
            status &= LoadDspCodeToSDRAM(MSB1240_DVBT2);
        }
        status &= LoadDspCodeToSDRAM(MSB1240_BOOT);
        status &= LoadDspCodeToSDRAM(MSB1240_DVBT2);
        if(status == MAPI_FALSE)
        {
            ERR_DEMOD_MSB(printf("[msb1240][err] LoadDspCodeToSDRAM failure...\n"));
        }

        if (LoadDSPCode() == MAPI_FALSE)
        {
            ERR_DEMOD_MSB(printf(">>>>MSB1240:Fail\n"));
            status= MAPI_FALSE;
        }
#endif
#else
        mapi_gpio *gptr = mapi_gpio::GetGPIO_Dev(Demodulator_Reset);
        if(gptr != NULL)
        {
            gptr->SetOff();
        }

        usleep(resetDemodTime*1000);

        if(gptr != NULL)
        {
            gptr->SetOn();
        }

        status &= I2C_CH_Reset(3);
#if (MIX_EDINBURGH_WINDERMERE_ENABLE == 0)//for EDINBURGH and WINDERMERE
#if USE_SPI_LOAD_TO_SDRAM
        //Initialize SPI0 for MSPI

#if (NEED_SWITCH_TSPAD_FOR_MSPI_USE == 1)
        MSPI_PAD_Enable(0, MAPI_TRUE);
#endif

        if(MDrv_MSPI_Init_Ext(0) != E_MSPI_OK)
        {
            ERR_DEMOD_MSB(printf(">>>>MDrv_MSPI_Init_Ext:Fail\n"));
            //status= MAPI_FALSE;
        }
        else
        {
            mapi_mspi::SetPadInfo(DEMOD);
            mapi_mspi::SetMaxClkInfo(DEMOD);
        }
#endif
#endif
#if (PRELOAD_DSP_CODE_FROM_MAIN_CHIP_I2C_ONLY_LOAD_T2 == 0)
        status &= DTV_DVB_HW_init();
#endif
        if(status == MAPI_FALSE)
        {
            ERR_DEMOD_MSB(printf("[msb1240][err] MSB1240_HW_init failure...\n"));
        }
if (status == MAPI_TRUE) 
    {
        status &= LoadDspCodeToSDRAM(MSB1240_ALL);
        if(status == MAPI_FALSE)
        {
            ERR_DEMOD_MSB(printf("[msb1240][err] LoadDspCodeToSDRAM failure...\n"));
        }
    }
 if (status == MAPI_TRUE) 
   {        
        if (LoadDSPCode() == MAPI_FALSE)
        {
        ERR_DEMOD_MSB(printf(">>>>MSB1240:Fail\n"));
        status= MAPI_FALSE;
       } 
   } 
#endif
    }
    else
    {
        MAPI_U8     u8DoneFlag = 0;
        MAPI_U16    u16_counter = 0;

        MAPI_U16    crc16 = 0;
        MAPI_U8     u8_reg = 0;

        do
        {
            DBG_DEMOD_MSB(printf(">>>MSB1240: u8RetryCnt = %d\n",u8RetryCnt));

            MAPI_U8 flash_waiting_ready_timeout = 0;
            u8RetryCnt--;

            status = msb1240_flash_mode_en();
            if (status == FALSE) {
                //g_u8_msb1240_sleep_mode_status = 0;
                bPower_init_en = MAPI_FALSE;
                ERR_DEMOD_MSB(printf("[msb1240][error]msb1240_flash_mode_en fail....\n"));
                return MAPI_FALSE;
            }

#if(TIMING_VERIFICATION)
tmm_3 = GIVE_ME_TIME
#endif
            status = msb1240_flash_boot_ready_waiting(&flash_waiting_ready_timeout);
            if ( (flash_waiting_ready_timeout == 1) || (status == FALSE) ) ERR_DEMOD_MSB(printf("[msb1240][error]msb1240_flash_boot_ready_waiting fail....\n"));

            ReadReg(0x1200+(0x01)*2,&u8_reg);
            if (u8_reg & BIT1){
                printf(">>>MSB1240: leave DRAM SR mode\n");
                // Turn on MPLL
                ReadReg(0x0A00+(0x35)*2,&u8_reg);
                u8_reg &= (0xff-0x80);            
                WriteReg(0x0A00+(0x35)*2,u8_reg);
            
                // SR begin
                WriteReg2bytes(0x1200+(0x1c)*2, 0x00b2);
                WriteReg2bytes(0x1200+(0x01)*2, 0x0101);

                usleep(1*1000);

                WriteReg2bytes(0x1200+(0x23)*2, 0xfffe);

                usleep(1*1000);

                WriteReg2bytes(0x1200+(0x00)*2, 0x0501);

                usleep(1*1000);

                WriteReg2bytes(0x1200+(0x00)*2, 0x0301);

                usleep(1*1000);

                WriteReg2bytes(0x1200+(0x00)*2, 0x0501);

                usleep(1*1000);

                WriteReg2bytes(0x1200+(0x00)*2, 0x0001);

                usleep(1*1000);

                WriteReg2bytes(0x1200+(0x1c)*2, 0x00b0);

                usleep(1*1000);

                WriteReg2bytes(0x1200+(0x01)*2, 0x010d);

                usleep(1*1000);

                // WriteReg2bytes(0x1200+(0x23)*2, 0x0000);

                // usleep(1*1000);
                //SR End

                // waiting for SR completed...
                usleep(10*1000);

                // ADC turn on.                        
                ReadReg(0x0A00+(0x0C)*2,&u8_reg);
                u8_reg &= (0xff-0x03);            
                WriteReg(0x0A00+(0x0C)*2,u8_reg);

                // ReadReg(0x0900+(0x0B)*2,&u8Data);
                // u8Data &= (0xff-0x20);
                WriteReg(0x0900+(0x0B)*2, 0x00);        

                // enable miu access of mcu gdma
                WriteReg(0x1200+(0x23)*2,0xf0);
                // 10us delay
                usleep(10);

                WriteReg(0x0B00 + (0x19) * 2, 0x00);
                WriteReg(0x0B00 + (0x10) * 2, 0x00);
            }

            ReadReg(0x0900+(0x02<<1),&u8_reg);
            if(u8_reg & _BIT5)
            {
                crc16 = MSB1240_LIB[u32SizeOfMSB1240_LIB-2];
                crc16 = (crc16<<8)|MSB1240_LIB[u32SizeOfMSB1240_LIB-1];
            }
            else
            {
                crc16 = MSB1240_LIB[u32SizeOfMSB1240_LIB-4];
                crc16 = (crc16<<8)|MSB1240_LIB[u32SizeOfMSB1240_LIB-3];
            }

#if(TIMING_VERIFICATION)
tmm_4 = GIVE_ME_TIME
printf("[tmm1]t4-t3 = %d (%d - %d)\n",tmm_4-tmm_3,tmm_4,tmm_3);
#endif

            if(status == FALSE)
            {
#ifndef T3_Winbledon
                DBG_DEMOD_MSB(printf(">>>MSB1240: Reset Demodulator\n"));

                mapi_gpio *gptr = mapi_gpio::GetGPIO_Dev(Demodulator_Reset);
                if(gptr != NULL)
                {
                    gptr->SetOff();
                }

                usleep(resetDemodTime*1000);

                if(gptr != NULL)
                {
                    gptr->SetOn();
                }

                status = msb1240_flash_mode_en();
                if (status == FALSE) ERR_DEMOD_MSB(printf("[msb1240][error]msb1240_flash_mode_en fail....\n"));
                
                status = msb1240_flash_boot_ready_waiting(&flash_waiting_ready_timeout);
                if ( (flash_waiting_ready_timeout == 1) || (status == FALSE) ) ERR_DEMOD_MSB(printf("[msb1240][error]msb1240_flash_boot_ready_waiting fail....\n"));

                // usleep(waitFlashTime * 1000);
                if (I2C_CH_Reset(3) == MAPI_FALSE)
                {
                  DBG_DEMOD_MSB(printf(">>>MSB1240 CH Reset:Fail\n"));
                  status= MAPI_FALSE;
                  continue;
                }

                u16_counter = 1000;
                do
                {
                  // 10 ms
                  usleep(10*1000);
                  u16_counter--;
                  ReadReg(0x0900+(0x4f)*2, &u8DoneFlag);
                } while(u8DoneFlag != 0x99 && u16_counter != 0);

                if(u16_counter == 0 && u8DoneFlag != 0x99)
                {
                  DBG_DEMOD_MSB(printf("[wb]Err, MSB1240 didn't ready yet\n"));
                  status = false;
                }
                else
                  status = TRUE;

#endif
            }
            // No need to switch to CH0 before SPI Flash access.
#if 0
            if (I2C_CH_Reset(0) == MAPI_FALSE)
            {
                ERR_DEMOD_MSB(printf(">>>MSB1240 CH0 Reset:Fail\n"));
                status= MAPI_FALSE;
                continue;
            }
            else
#endif

            status &= ReadReg(0x0900+0x01*2, &u8MSB1240ChipRev);    
            if (u8MSB1240ChipRev == 0)
            {
                MSB1240_LIB = MSB1240_LIB_U01;
                u32SizeOfMSB1240_LIB = sizeof(MSB1240_LIB_U01);
            }
            //else
            //{
            //    MSB1240_LIB = MSB1240_LIB_U02;
            //    u32SizeOfMSB1240_LIB = sizeof(MSB1240_LIB_U02);               
            //}

            {
            
//              DBG_DEMOD_MSB(printf(">>>MSB1240 CH0 Reset:OK\n"));

                
                // turn on spi function to read flash info.
                // mask miu access for all and mcu
                WriteReg(0x1200+(0x23)*2 + 1,0x0f);
                WriteReg(0x1200+(0x23)*2,0xf0);
                // 10us delay
                usleep(10);

                // MCU Reset
                ReadReg(0x0b00+(0x19<<1),&u8_reg);
                u8_reg |= 0x01;
                WriteReg(0x0b00+(0x19<<1),u8_reg);
                
                ReadReg(0x0900+(0x07<<1),&u8_reg);
                u8_reg |= 0x10;
                WriteReg(0x0900+(0x07<<1),u8_reg);
                
                ReadReg(0x0900+(0x3b<<1),&u8_reg);
                u8_reg |= 0x01;
                WriteReg(0x0900+(0x3b<<1),u8_reg);                           

                if (use_twin_demod == 0)
                {
                    DBG_DEMOD_MSB(printf(">>>MSB1240: Check Version..."));

                    if (IspCheckVer(MSB1240_LIB, &bMatch) == MAPI_FALSE)
                    {
                        ERR_DEMOD_MSB(printf(">>> ISP read FAIL!\n"));
                        status= MAPI_FALSE;
                        continue;
                    }
                }
                else
                    bMatch = true;

                
                if(bMatch == false)
                {
                  printf(">>> IspCheckVer FAIL!\n");
                }
                else

                {

                  if (I2C_CH_Reset(3) == MAPI_FALSE)
                  {
                    (printf(">>>MSB1240 CH Reset:Fail\n"));
                    status= MAPI_FALSE;
                    continue;
                  }
                  else
                  {
                    (printf(">>>MSB1240 CH Reset:OK\n"));
                  }

                  if (dram_crc_check(crc16, &bMatch) == MAPI_FALSE)
                  {
                      (printf(">>> reg read fail!\n"));
                      status= MAPI_FALSE;
                      continue;
                  }

                  if(bMatch == false)
                    printf(">>> dram crc check FAIL!\n");
                  else
                    printf(">>> dram crc check OK!\n");

                  if (bMatch == true)
                  {
                      // mask miu access for all and mcu
                      WriteReg(0x1200+(0x23)*2 + 1,0x0f);
                      WriteReg(0x1200+(0x23)*2,0xf0);
                      // 10us delay
                      usleep(10);
                      WriteReg(0x0B00 + (0x19) * 2, 0x03);
                  }                  
                }


#if (FLASH_WP_ENABLE == 1)
                if (bMatch == false)
                {
                  // disable flash WP, pull high.
                  if(msb1240_flash_WP(0) == false)
                  {
                    DBG_FLASH_WP(printf("[wb]Err, FLASH WP Disable Fail!!!\n");)
                  }
                  usleep(100*1000);
                }
#endif
                ////bMatch = true; //FIXME : Remove this to enable auto FW reload.
                if (bMatch == false)// Version not match
                {
                    MAPI_U8     bAddr[1];
                    MAPI_U8 bWriteData[5]={0x4D, 0x53, 0x54, 0x41, 0x52};
                    MAPI_U32 u32SizeOfLimit=0;
                    MAPI_U8     bReadData=0;

                    printf(">>> Not match! Reload Flash...");
                    if ( (u32SizeOfMSB1240_LIB%256) != 0)
                    {
                        printf(" MSB1240_LIB 256byte alignment error!%u \n",u32SizeOfMSB1240_LIB);
                    }

                    mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_2);
                    iptr->WriteBytes(0, NULL, 5, bWriteData);

                    bAddr[0] = 0x10;
                    bWriteData[0] = 0x06;
                    iptr->WriteBytes(1, bAddr, 1, bWriteData);

                    bWriteData[0] = 0x12;
                    iptr->WriteBytes(0, NULL, 1, bWriteData);

                    bAddr[0] = 0x10;

                    bWriteData[0] = 0xC7;
                    iptr->WriteBytes(1, bAddr, 1, bWriteData);

                    bWriteData[0] = 0x12;
                    iptr->WriteBytes(0, NULL, 1, bWriteData);

                    bWriteData[0]=0x24 ;
                    iptr->WriteBytes(0, NULL, 1, bWriteData);
                    DBG_DEMOD_MSB(printf("\t\t\tStart   %d\n", MsOS_GetSystemTime()));//to measure time
#if 1
                    while(1)
                    {
                        bWriteData[0] = 0x4D;
                        bWriteData[1] = 0x53;
                        bWriteData[2] = 0x54;
                        bWriteData[3] = 0x41;
                        bWriteData[4] = 0x52;
                        iptr->WriteBytes(0, NULL, 5, bWriteData);

                        bAddr[0] = 0x10;
                        bWriteData[0] = 0x05;
                        iptr->WriteBytes(1, bAddr, 1, bWriteData);
                
                        bAddr[0] = 0x11;
                        iptr->ReadBytes(1, bAddr, 1, &bReadData);
                
                        bWriteData[0] = 0x12;
                        iptr->WriteBytes(0, NULL, 1, bWriteData);
                
                        bWriteData[0]=0x24 ;
                        iptr->WriteBytes(0, NULL, 1, bWriteData);

                        printf("RDSR %x\n", bReadData);

                        if (!(bReadData & 0x01))
                            break;

                        usleep(1000000);
                    }
#endif
                    if ( (u32SizeOfMSB1240_LIB - 4) > MAX_MSB1240_LIB_LEN)
                    {
                      printf("Err, msb1240_lib size(%d) is larger than flash size(%d)\n",u32SizeOfMSB1240_LIB,MAX_MSB1240_LIB_LEN);
                    }

                    // if (IspProcFlash(MSB1240_LIB, u32SizeOfMSB1240_LIB) == MAPI_FALSE)
                    //if (IspProcFlash(MSB1240_LIB, u32SizeOfMSB1240_LIB-2) == MAPI_FALSE)
                    ReadReg(0x0900+(0x02<<1),&u8_reg);
                    if(u8_reg & _BIT5)
                        u32SizeOfLimit = u32SizeOfMSB1240_LIB-4;
                    else
                        u32SizeOfLimit = u32SizeOfMSB1240_LIB-4-0x9000;

                    if (IspProcFlash(MSB1240_LIB, u32SizeOfLimit) == MAPI_FALSE)
                    {
                        ERR_DEMOD_MSB(printf(" ISP write FAIL\n"));
                        status= MAPI_FALSE;
                        continue;
                    }
                    else
                    {
                        DBG_DEMOD_MSB(printf("\t\t\tEnd   %d\n", MsOS_GetSystemTime()));//to measure time

                        if(use_twin_demod == 0)
                        {
                            //check again
                            if ((IspCheckVer(MSB1240_LIB, &bMatch) == MAPI_FALSE)||(bMatch==false))
                            {
                                ERR_DEMOD_MSB(printf(">>> ISP read FAIL! bMatch %d \n",bMatch));
                                status= MAPI_FALSE;
                                continue;
                            }                        
                            else // reset again
                            {
#ifndef T3_Winbledon
                                DBG_DEMOD_MSB(printf(">>>MSB1240: Reset Demodulator\n"));

                                printf(">>>MSB1240[2]: Reset Demodulator\n");
                                mapi_gpio *gptr = mapi_gpio::GetGPIO_Dev(Demodulator_Reset);

                                if(gptr != NULL)
                                {
                                    gptr->SetOff();
                                }

                                usleep(resetDemodTime*1000);

                                if(gptr != NULL)
                                {
                                    gptr->SetOn();
                                }

                                status = msb1240_flash_mode_en();
                                if (status == FALSE) ERR_DEMOD_MSB(printf("[msb1240][error]msb1240_flash_mode_en fail....\n"));
                            
                                status = msb1240_flash_boot_ready_waiting(&flash_waiting_ready_timeout);
                                if ( (flash_waiting_ready_timeout == 1) || (status == FALSE) ) ERR_DEMOD_MSB(printf("[msb1240][error]msb1240_flash_boot_ready_waiting fail....\n"));
                                
                                // usleep(waitFlashTime * 1000);

                                if (I2C_CH_Reset(3) == MAPI_FALSE)
                                {
                                    ERR_DEMOD_MSB(printf(">>>MSB1240 CH Reset:Fail\n"));
                                    status= MAPI_FALSE;
                                    continue;
                                }

                                u16_counter = 1000;
                                do
                                {
                                    // 10 ms
                                    usleep(10*1000);
                                    u16_counter--;
                                    ReadReg(0x0900+(0x4f)*2, &u8DoneFlag);
                                } while(u8DoneFlag != 0x99 && u16_counter != 0);

                                if(u16_counter == 0 && u8DoneFlag != 0x99)
                                {
                                    ERR_DEMOD_MSB(printf("[wb]Err, MSB1240 didn't ready yet\n"));
                                    status = false;
                                }
                                else
                                    status = TRUE;
#endif

                                if (I2C_CH_Reset(3) == MAPI_FALSE)
                                {
                                    (printf(">>>MSB1240 CH Reset:Fail\n"));
                                    status= MAPI_FALSE;
                                    continue;
                                }
                                else
                                {
                                    (printf(">>>MSB1240 CH Reset:OK\n"));
                                }

                                if (dram_crc_check(crc16, &bMatch) == MAPI_FALSE)
                                {
                                    (printf(">>> reg read fail!\n"));
                                    status= MAPI_FALSE;
                                    continue;
                                }

                                if(bMatch == false)
                                    printf(">>> dram crc check FAIL!\n");
                                else
                                    printf(">>> dram crc check OK!\n");

                                if (bMatch == true)
                                {
                                    // mask miu access for all and mcu
                                    WriteReg(0x1200+(0x23)*2 + 1,0x0f);
                                    WriteReg(0x1200+(0x23)*2,0xf0);
                                    // 10us delay
                                    usleep(10);
                                    WriteReg(0x0B00 + (0x19) * 2, 0x03);
                                }                            
                            }                            
                                DBG_DEMOD_MSB(printf(" OK\n"));
                        }
                    }
                }
                else
                {
                    // Version match, do nothing
                    DBG_DEMOD_MSB(printf(">>> Match\n"));
                }
                
                if (use_twin_demod == 0)
                {
#if (FLASH_WP_ENABLE == 1)
                    if (bMatch == true)
                    {
                        // Enable flash WP, pull high.
                        if(msb1240_flash_WP(1) == false)
                        {
                            DBG_FLASH_WP(printf("[wb]Err, FLASH WP Enable Fail!!!\n");)
                        }
                        usleep(100*1000);
                    }
#endif
                }
            }

            if (I2C_CH_Reset(3) == MAPI_FALSE)
            {
                ERR_DEMOD_MSB(printf(">>>MSB1240 CH Reset:Fail\n"));
                status= MAPI_FALSE;
                continue;
            }
            else
            {
                DBG_DEMOD_MSB(printf(">>>MSB1240 CH Reset:OK\n"));
            }

        #if(MSB1240_SELECT_IF_INPUT)
            //[0:0] reg_ana_setting_enable
            //[6:4] reg_ana_setting_sel
            status &= WriteReg2bytes(0x0A00+(0x51)*2, 0x0021);
        #endif

#if(TIMING_VERIFICATION)
tmm_5 = GIVE_ME_TIME
#endif
            if (LoadDSPCode() == MAPI_FALSE)
            {
                ERR_DEMOD_MSB(printf(">>>>MSB1240:Fail\n"));
                status= MAPI_FALSE;
                continue;
            }
            else
            {
                MAPI_U8 u8_reg = 0;
                // turn off spi function to keep impedance
                ReadReg(0x0900+(0x07<<1),&u8_reg);
                u8_reg &= (0xff-0x10);
                WriteReg(0x0900+(0x07<<1),u8_reg);
              
                ReadReg(0x0900+(0x3b<<1),&u8_reg);
                u8_reg &= (0xff-0x01);
                WriteReg(0x0900+(0x3b<<1),u8_reg);
            
                DBG_DEMOD_MSB(printf(">>>MSB1240:OK\n"));
            }
#if(TIMING_VERIFICATION)
tmm_6 = GIVE_ME_TIME
printf("[tmm1]t6-t5 = %d (%d - %d)\n",tmm_6-tmm_5,tmm_6,tmm_5);
#endif

            ReadReg(0x0900+(0x01)*2, &gu8ChipRevId);
            DBG_DEMOD_MSB(printf(">>>MSB1240:Edinburgh RevID:%x\n", gu8ChipRevId));

            ReadReg(0x0900+(0x49)*2, &gu8ChipRevId);
            DBG_DEMOD_MSB(printf(">>>MSB1240:Edinburgh 0x49_L:%x\n", gu8ChipRevId));

            ReadReg(0x0900+(0x49)*2+1, &gu8ChipRevId);
            DBG_DEMOD_MSB(printf(">>>MSB1240:Edinburgh 0x49_H:%x\n", gu8ChipRevId));

            ReadReg(0x0900+(0x4A)*2, &gu8ChipRevId);
            DBG_DEMOD_MSB(printf(">>>MSB1240:Edinburgh 0x4A_L:%x\n", gu8ChipRevId));

        }while((u8RetryCnt>0)&&(status==FALSE));
    }

    if(status==FALSE)
    {
        ERR_DEMOD_MSB(printf("msb1240 power_on_init FAIL !!!!!! \n\n"));
    }
    else
    {              
        g_u8_msb1240_sleep_mode_status = 0;
        bPower_init_en = MAPI_TRUE;
        
        printf("msb1240 power_on_init OK !!!!!! \n\n");


#if(TIMING_VERIFICATION == 1)
        tmm_2 = GIVE_ME_TIME
        printf("[tmm]t2-t1 = %d (%d - %d)\n",tmm_2-tmm_1,tmm_2,tmm_1);
#endif


    }

#if USE_SPI_LOAD_TO_SDRAM && (NEED_SWITCH_TSPAD_FOR_MSPI_USE == 1)
    MSPI_PAD_Enable(0, MAPI_FALSE);
#endif
    return status;
}

/*@ </Operation ID=Im17018142m1221763cc7cmm46c6> @*/
//m
MAPI_BOOL device_demodulator_extend::Set_PowerOn(void)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    return TRUE;
}
//m
MAPI_BOOL device_demodulator_extend::Set_PowerOff(void)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    return TRUE;
}


MAPI_U8  device_demodulator_extend::DTV_DVBT_DSPReg_CRC(void)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));

    MAPI_U8 crc = 0;
    MAPI_U8 idx = 0;

    for (idx = 0; idx<(sizeof(MSB1240_DVBT_DSPREG_TABLE)); idx++)
    {
        crc ^= MSB1240_DVBT_DSPREG_TABLE[idx];
    }

    crc = ~crc;

    return crc;
}

void  device_demodulator_extend::DTV_DVBT_DSPReg_ReadBack(void)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));

    MAPI_U8 ret = 0;
    MAPI_U8 idx = 0;

    for (idx = T_OPMODE_RFAGC_EN; idx< (MAPI_U8)DVBT_PARAM_LEN; idx++)
    {
        ReadDspReg(idx,&ret);
        printf("@msb1240, idx=0x%x, dsp=0x%x\n",idx,ret);
    }

    return;
}

MAPI_BOOL device_demodulator_extend::DTV_DVBT2_DSPReg_Init(void)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));

    if( WriteDspReg((MAPI_U8)E_T2_BW, T2_BW_VAL) != TRUE)
    {
        printf("T2 dsp reg init NG\n"); return MAPI_FALSE;
    }
    if( WriteDspReg((MAPI_U8)E_T2_FC_L, T2_FC_L_VAL) != TRUE)
    {
        printf("T2 dsp reg init NG\n"); return MAPI_FALSE;
    }
    if( WriteDspReg((MAPI_U8)E_T2_FC_H, T2_FC_H_VAL) != TRUE)
    {
        printf("T2 dsp reg init NG\n"); return MAPI_FALSE;
    }
    if( WriteDspReg((MAPI_U8)E_T2_TS_SERIAL, T2_TS_SERIAL) != TRUE)
    {
        printf("T2 dsp reg init NG\n"); return MAPI_FALSE;
    }
    if( WriteDspReg((MAPI_U8)E_T2_TS_CLK_RATE, T2_TS_CLK_RATE_VAL) != TRUE)
    {
        printf("T2 dsp reg init NG\n"); return MAPI_FALSE;
    }
    if( WriteDspReg((MAPI_U8)E_T2_TS_OUT_INV, T2_TS_CLK_INV) != TRUE)
    {
        printf("T2 dsp reg init NG\n"); return MAPI_FALSE;
    }
    if( WriteDspReg((MAPI_U8)E_T2_TS_DATA_SWAP, T2_TS_DATA_SWAP_VAL) != TRUE)
    {
        printf("T2 dsp reg init NG\n"); return MAPI_FALSE;
    }
    if( WriteDspReg((MAPI_U8)E_T2_TS_ERR_POL, T2_TS_ERR_POL_VAL) != TRUE)
    {
        printf("T2 dsp reg init NG\n"); return MAPI_FALSE;
    }
    if( WriteDspReg((MAPI_U16)E_T2_IF_AGC_INV_PWM_EN, T2_IF_AGC_INV_PWM_EN_VAL) != TRUE)
    {
      printf("T2 dsp reg init NG\n"); return MAPI_FALSE;
    }

    if( WriteDspReg((MAPI_U16)E_T2_SPREAD_SPAN, (MAPI_U16) T2_TS_SPREAD_SPAN) != TRUE)
    {
        printf("T2 dsp reg init NG\n"); return MAPI_FALSE;
    }

    if( WriteDspReg((MAPI_U16)E_T2_SPREAD_STEP, (MAPI_U16) T2_TS_SPREAD_STEP_SIZE) != TRUE)
    {
        printf("T2 dsp reg init NG\n"); return MAPI_FALSE;
    }
   
    printf("T2 dsp reg init ok\n");

    return MAPI_TRUE;
}

MAPI_BOOL  device_demodulator_extend::DTV_DVBT_DSPReg_Init(void)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));

#if(TIMING_VERIFICATION == 1)
    tmm_7 = GIVE_ME_TIME
#endif
    if( WriteDspReg((MAPI_U8)T_CONFIG_BW, T_BW) != TRUE)
    {
        printf("T dsp reg init NG\n"); return MAPI_FALSE;
    }
    if( WriteDspReg((MAPI_U8)T_CONFIG_FC_L, T_FC_L) != TRUE)
    {
        printf("T dsp reg init NG\n"); return MAPI_FALSE;
    }
    if( WriteDspReg((MAPI_U8)T_CONFIG_FC_H, T_FC_H) != TRUE)
    {
        printf("T dsp reg init NG\n"); return MAPI_FALSE;
    }
    if( WriteDspReg((MAPI_U8)T_CONFIG_IQ_SWAP, T_IQ_SWAP) != TRUE)
    {
        printf("T dsp reg init NG\n"); return MAPI_FALSE;
    }
    if( WriteDspReg((MAPI_U8)T_CONFIG_TS_SERIAL, T_SERIAL_TS_VAL) != TRUE)
    {
        printf("T dsp reg init NG\n"); return MAPI_FALSE;
    }
    if( WriteDspReg((MAPI_U8)T_CONFIG_TS_CLK_RATE, T_TS_CLK_SEL) != TRUE)
    {
        printf("T dsp reg init NG\n"); return MAPI_FALSE;
    }
    if( WriteDspReg((MAPI_U8)T_CONFIG_TS_OUT_INV, T_TS_OUT_INV) != TRUE)
    {
        printf("T dsp reg init NG\n"); return MAPI_FALSE;
    }
    if( WriteDspReg((MAPI_U8)T_CONFIG_TS_DATA_SWAP, T_TS_DATA_SWAP) != TRUE)
    {
        printf("T dsp reg init NG\n"); return MAPI_FALSE;
    }
    if( WriteDspReg((MAPI_U16)T_CONFIG_IF_INV_PWM_OUT_EN, T_IF_INV_PWM_OUT_EN) != TRUE)
    {
        printf("T dsp reg init NG\n"); return MAPI_FALSE;
    }
    
    if( WriteDspReg((MAPI_U16)T_CONFIG_SPREAD_SPAN, (MAPI_U16) T_TS_SPREAD_SPAN) != TRUE)
    {
        printf("T dsp reg init NG\n"); return MAPI_FALSE;
    }

    if( WriteDspReg((MAPI_U16)T_CONFIG_SPREAD_STEP, (MAPI_U16) T_TS_SPREAD_STEP_SIZE) != TRUE)
    {
         printf("T dsp reg init NG\n"); return MAPI_FALSE;
    }
     
#if(TIMING_VERIFICATION == 1)
    tmm_8 = GIVE_ME_TIME
            printf("[33333]t8 - t7 = %d (%d - %d) \n",tmm_8-tmm_7,tmm_8,tmm_7);
#endif

    printf("dvbt dsp reg init ok\n");

    return MAPI_TRUE;
}
#if (DVBC_SYSTEM_ENABLE == 1)
MAPI_BOOL  device_demodulator_extend::DTV_DVBC_DSPReg_Init(void)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_U8    idx = 0;

    for (idx = 0; idx<sizeof(MSB1240_DVBC_DSPREG_TABLE); idx++)
    {
        if( WriteDspReg(idx + 0x20, MSB1240_DVBC_DSPREG_TABLE[idx])!=TRUE)
        {
            ERR_DEMOD_MSB(printf("dvbc dsp reg init NG\n"));
            return MAPI_FALSE;
        }
    }

     if( WriteDspReg((MAPI_U16)C_config_spread_span, (MAPI_U16) C_TS_SPREAD_SPAN) != TRUE)
    {
        printf("C dsp reg init NG\n"); return MAPI_FALSE;
    }

    if( WriteDspReg((MAPI_U16)C_config_spread_step, (MAPI_U16) C_TS_SPREAD_STEP_SIZE) != TRUE)
    {
        printf("C dsp reg init NG\n"); return MAPI_FALSE;
    }
  
    ERR_DEMOD_MSB(printf("DVBC dsp reg init ok\n"));

    return MAPI_TRUE;
}
#endif
MAPI_BOOL device_demodulator_extend::Active(MAPI_BOOL bEnable)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_BOOL status = MAPI_TRUE;

    status = WriteReg(REG_FSM_EN, (MAPI_U8)(bEnable)); // FSM_EN

    if(status)
    {
        printf(" @MSB1240_Active OK\n");
    }
    else
    {
        printf(" @MSB1240_Active NG\n");
    }
    return status;
}

mapi_demodulator_datatype::EN_DEVICE_DEMOD_TYPE device_demodulator_extend::GetCurrentDemodulatorType(void)
{
    //DBG_DEMOD_FLOW(printf("%s(),%d,type is %d\n",__func__,__LINE__,(int)device_demodulator_extend::m_enCurrentDemodulator_Type));
    return device_demodulator_extend::m_enCurrentDemodulator_Type;
}
#if 0
//@@++ 20120405 Arki
MAPI_BOOL device_demodulator_extend::GetDemodInfo(void)
{
    ExtendCmd(0xEE, 0, 0, &st2_mod_info);
    return TRUE;
}

MAPI_U8 device_demodulator_extend::GetDVBT2_Demod_s1_siso_miso(void)
{
    return st2_mod_info.s1_siso_miso;
}

MAPI_U8 device_demodulator_extend::GetDVBT2_Demod_plp_number(void)
{
    return st2_mod_info.plp_number;
}

MAPI_U8 device_demodulator_extend::GetDVBT2_Demod_plp_id(void)
{
    return st2_mod_info.plp_id;
}

MAPI_U8 device_demodulator_extend::GetDVBT2_Demod_constellation_l1(void)
{
    return st2_mod_info.constellation_l1;
}

MAPI_U8 device_demodulator_extend::GetDVBT2_Demod_coderate_l1(void)
{
    return st2_mod_info.coderate_l1;
}

MAPI_U8 device_demodulator_extend::GetDVBT2_Demod_plp_constellation(void)
{
    return st2_mod_info.plp_constellation;
}

MAPI_U8 device_demodulator_extend::GetDVBT2_Demod_plp_rotation_constellation(void)
{
    return st2_mod_info.plp_rotation_constellation;
}

MAPI_U8 device_demodulator_extend::GetDVBT2_Demod_plp_coderate(void)
{
    return st2_mod_info.plp_coderate;
}

MAPI_U8 device_demodulator_extend::GetDVBT2_Demod_plp_fec(void)
{
    return st2_mod_info.plp_fec;
}

MAPI_U8 device_demodulator_extend::GetDVBT2_Demod_plp_frame_il_length(void)
{
    return st2_mod_info.plp_frame_il_length;
}

MAPI_U8 device_demodulator_extend::GetDVBT2_Demod_plp_type_time_il(void)
{
    return st2_mod_info.plp_type_time_il;
}

MAPI_U8 device_demodulator_extend::GetDVBT2_Demod_plp_length_time_il(void)
{
    return st2_mod_info.plp_length_time_il;
}

MAPI_U8 device_demodulator_extend::GetDVBT2_Demod_pilot_pattern(void)
{
    return st2_mod_info.pilot_pattern;
}

MAPI_U8 device_demodulator_extend::GetDVBT2_Demod_mode_carrier(void)
{
    return st2_mod_info.mode_carrier;
}

MAPI_U8 device_demodulator_extend::GetDVBT2_Demod_fef_type(void)
{
    return st2_mod_info.fef_type;
}

MAPI_U32 device_demodulator_extend::GetDVBT2_Demod_fef_length(void)
{
    return st2_mod_info.fef_length;
}

MAPI_U32 device_demodulator_extend::GetDVBT2_Demod_fef_interval(void)
{
    return st2_mod_info.fef_interval;
}

MAPI_U8 device_demodulator_extend::GetDVBT2_Demod_papr(void)
{
    return st2_mod_info.papr;
}

MAPI_U8 device_demodulator_extend::GetDVBT2_Demod_guard_interval(void)
{
    return st2_mod_info.guard_interval;
}
//@@-- 20120405 Arki
#endif
MAPI_BOOL device_demodulator_extend::ExtendCmd(MAPI_U8 SubCmd, MAPI_U32 u32Param1, MAPI_U32 u32Param2, void *pvParam3)
{
    //DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_U8 u8Data = 0;
    MAPI_U8 u8Status = MAPI_TRUE;

    // return false if power_on_initialization() is not finished
    if (MAPI_TRUE != bPower_init_en)
    {
        return MAPI_FALSE;
    }

    SubCmd=SubCmd;
    u32Param1=u32Param1;
    u32Param2=u32Param2;
    pvParam3=pvParam3;

    if(SubCmd == 1)
    {
        //1 => write
        // printf("[dvbt2]write register\n");
        return WriteReg((MAPI_U16) u32Param1, (MAPI_U8) u32Param2);
    }

    if(SubCmd == 2)
    {
        //2 =>  Read
        // printf("[dvbt2]read register\n");
        return ReadReg((MAPI_U16)u32Param1, (MAPI_U8*)pvParam3);
    }
#if (DVBS_SYSTEM_ENABLE == 1)
    if(SubCmd == 0x10)
    {
      MAPI_U8 TR0 = 0;
      MAPI_U8 TR1 = 0;
      MAPI_U8 TR2 = 0;
      if(ReadReg(0x1b00+(0x0e)*2+1, &TR0) == MAPI_FALSE)
      {
        return MAPI_FALSE;
      }
      if(ReadReg(0x3300+(0x19)*2+1, &TR1) == MAPI_FALSE)
      {
        return MAPI_FALSE;
      }	 
      if(ReadReg(0x3300+(0x29)*2+1, &TR2) == MAPI_FALSE)
      {
        return MAPI_FALSE;
      }
      if((((TR0|TR1|TR2)&0x01) == 0x00) && 
         (mapi_time_utility::TimeDiffFromNow0(_u32LocktimeStart) > TR_TIME_OUT_WITH_FIX_SYMBOL_RATE) &&
         (_u32CurrentSR != 0)
        )
      {
        *((MAPI_U8*)pvParam3) = 0;
      }
      else
      {
        *((MAPI_U8*)pvParam3) = 1;
      }
    }
#endif
    if (SubCmd == 0x77)
    {
#if 0
        if (FECLock == TRUE)
            u8_g_pid0_timeout_flag = 1;
        else
            u8_g_pid0_timeout_flag = 0;
#endif
    }

    if (SubCmd == 0x80) // temp solution, to turn on/off IF AGC
    {
        if (u32Param1 == MAPI_TRUE)         // turn on
        {
            u8Status &= ReadReg(0x0a00+(0x18)*2, &u8Data);
            u8Data = u8Data|0x01;
            u8Status &= WriteReg(0x0a00+(0x18)*2, u8Data);
            printf("MSB1240: Turn on IF AGC: %02x\n", u8Data);
        }
        else if (u32Param1 == MAPI_FALSE)    // turn off
        {
            u8Status &= ReadReg(0x0a00+(0x18)*2, &u8Data);
            u8Data = u8Data&(0xff-0x01);
            u8Status &= WriteReg(0x0a00+(0x18)*2, u8Data);
            printf("MSB1240: Turn off IF AGC: %02x\n", u8Data);
        }
    }

    // DVB-T2 Tri-State setting
    // MAPI_FALSE(0) : Disable Tri-State, enable DVB-T2 output
    // MAPI_TRUE(1) : Enable Tri-State, disable DVB-T2 output
    if(SubCmd == 0x81)
    {
        if(u32Param1 == MAPI_TRUE)
        {
            WriteReg2bytes(0x0900+(0x2d)*2, 0x0000);

        }
        else
        {
            WriteReg2bytes(0x0900+(0x2d)*2, 0x00ff);
        }
    }

    if(SubCmd == 0x82)  // to turn on/off null packet insert
    {
        if(u32Param1 == 1)  // null packet on
        {
            //DVBT2
            u8Status &= ReadReg(0x2a00 + (0x20) * 2 , &u8Data);
            u8Status &= WriteReg(0x2a00 + (0x20) * 2 , (u8Data |0x80));
            //DVBT/C
            u8Status &= ReadReg(0x1100 + (0x20) * 2 , &u8Data);
            u8Status &= WriteReg(0x1100 + (0x20) * 2 , (u8Data |0x80));
        }
        else if(u32Param1 == 0)     // null packet off
        {
            //DVBT2
            u8Status &= ReadReg(0x2a00 + (0x20) * 2 , &u8Data);
            u8Status &= WriteReg(0x2a00 + (0x20) * 2 , (u8Data & (~0x80)));
            //DVBT/C
            u8Status &= ReadReg(0x1100 + (0x20) * 2 , &u8Data);
            u8Status &= WriteReg(0x1100 + (0x20) * 2 , (u8Data & (~0x80)));
        }
    }

    // get fef indiator
    if (SubCmd == 0x83)
    {
        MAPI_U8 u8Data = 0;
        u8Status &= ReadDspReg(E_T2_FEF_DET_IND, &u8Data);
        *((MAPI_U8*)pvParam3) = u8Data;

//        printf("[dvbt2]get fef indicator = %d\n",u8Data);
    }

    // get CFO(frequency offset) value
    // Unit: kHz
    // Real_RF = Nominal_RF + fCFO.
    if (SubCmd == 0x84)
    {
        bool   status = true;

        // get CFO(frequency offset) value
        // Unit: kHz


        switch(device_demodulator_extend::m_enCurrentDemodulator_Type)
        {
            case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2:
            {
                float fCFO = 0.0;
                MAPI_U8  u8Data = 0;
                MAPI_U8  u8_freeze = 0;
                MAPI_U16  u16Data = 0;
                MAPI_S16  s16_cfo10 = 0;

                // freeze
                status &= ReadReg(DIG_DBG_BASE+0x28*2+1, &u8_freeze);
                status &= WriteReg(DIG_DBG_BASE+0x28*2+1, u8_freeze|0x80);

                status &=ReadDspReg((MAPI_U16)E_T2_TOTAL_CFO_1, &u8Data);
                u16Data = u8Data;
                status &=ReadDspReg((MAPI_U16)E_T2_TOTAL_CFO_0, &u8Data);
                u16Data = (u16Data<<8)|u8Data;

                // unfreeze
                status &= WriteReg(DIG_DBG_BASE+0x28*2+1, u8_freeze);

                s16_cfo10 = (MAPI_S16)u16Data;
                fCFO = (float)s16_cfo10/10.0;

                 *((float*)pvParam3) = fCFO ; 
            }
            break;

        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
        {
                float fCFO = 0.0;
                MAPI_U8  u8Data = 0;
                MAPI_U8  u8_freeze = 0;
                MAPI_U16  u16Data = 0;
                MAPI_S16  s16_cfo10 = 0;

                // freeze
                status &= ReadReg(DIG_DBG_BASE+0x29*2, &u8_freeze);
                status &= WriteReg(DIG_DBG_BASE+0x29*2, u8_freeze|0x01);

                status &=ReadDspReg((MAPI_U16)T_CFO10K_H, &u8Data);
                u16Data = u8Data;
                status &=ReadDspReg((MAPI_U16)T_CFO10K_L, &u8Data);
                u16Data = (u16Data<<8)|u8Data;

                // unfreeze
                status &= WriteReg(DIG_DBG_BASE+0x29*2, u8_freeze);

                s16_cfo10 = (MAPI_S16)u16Data;
                fCFO = (float)s16_cfo10/10.0;


                *((float*)pvParam3) = fCFO ;

        }
        break;
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_C:
        {
                MAPI_BOOL status = true;
                MAPI_U8 u8Data = 0, reg_frz = 0;
                MAPI_U16 u16_tmp = 0;
                MAPI_S16 s16_cfo10 = 0;

                //u8BW = u8BW;

                // freeze
                status &= ReadReg(DIG_DBG_BASE+0x52, &reg_frz);
                status &= WriteReg(DIG_DBG_BASE+0x52, reg_frz|0x01);

                status &=ReadDspReg((MAPI_U16)C_CFO10_H, &u8Data);
                u16_tmp = u8Data;
                status &=ReadDspReg((MAPI_U16)C_CFO10_L, &u8Data);
                u16_tmp = (u16_tmp<<8)|u8Data;

                // unfreeze
                reg_frz=reg_frz&(~0x01);
                status &= WriteReg(DIG_DBG_BASE+0x52, reg_frz);

                s16_cfo10 = (MAPI_S16)u16_tmp;
                *((float*)pvParam3) = (float)s16_cfo10/10.0;
        }
        break;

        default:
             *((float*)pvParam3) = 0;
        }
        return status;

    }

    // sleep mode
    if (SubCmd == 0x85)
    {
        MAPI_U8      u8Data  = 0x00;
        MAPI_U16     u16Retry = 0x00;
        static MAPI_U8 u8_mcu_save = 0;
        
        // sleep mode enable
        if (u32Param1 == 1)
        {                
#if (MSB1240_SW_SLEEP_MODE_EN == 1)
            // mask miu access for all and mcu
            WriteReg(0x1200+(0x23)*2 + 1,0x7f);
            WriteReg(0x1200+(0x23)*2,0xfe);
            // 10us delay
            usleep(10);

            WriteReg(0x0B00 + (0x10) * 2, 0x01);
            WriteReg(0x0B00 + (0x19) * 2, 0x03);
            WriteReg(REG_MB_CNTL, 0x00);             //clear MB_CNTL
            usleep(5 * 100);

            // ADC turn off.
            ReadReg(0x0900+(0x0B)*2,&u8Data);
            u8_mcu_save = u8Data;
            u8Data |= 0x20;
            WriteReg(0x0900+(0x0B)*2,u8Data);
            
            ReadReg(0x0A00+(0x0C)*2,&u8Data);
            u8Data |= 0x03;            
            WriteReg(0x0A00+(0x0C)*2,u8Data);            
            
            // ReadReg(0x0A00+(0x35)*2,&u8Data);
            // u8Data |= 0x80;            
            // WriteReg(0x0A00+(0x35)*2,u8Data);
            
            g_u8_msb1240_sleep_mode_status = 1;
#else
            mapi_gpio *gptr = NULL;            
            // gptr = mapi_gpio::GetGPIO_Dev(MSB1240_HW_SLEEP_PIN);

            // mask miu access for all and mcu
            WriteReg(0x1200+(0x23)*2 + 1,0x7f);
            WriteReg(0x1200+(0x23)*2,0xfe);
            // 10us delay
            usleep(10);

            WriteReg(0x0B00 + (0x10) * 2, 0x01);
            WriteReg(0x0B00 + (0x19) * 2, 0x03);
            WriteReg(REG_MB_CNTL, 0x00);             //clear MB_CNTL
            usleep(5 * 100);

            // TS pad off
            WriteReg2bytes(0x0900+(0x2d)*2, 0x0000);
            
            // IF_AGC_T pad off
            u8Status &= ReadReg(0x0a00+(0x18)*2, &u8Data);
            u8Data = u8Data&(0xff-0x01);
            u8Status &= WriteReg(0x0a00+(0x18)*2, u8Data);           

            // IF_AGC_S pad off
            u8Status &= ReadReg(0x0a00+(0x18)*2+1, &u8Data);
            u8Data = u8Data&(0xff-0x01);
            u8Status &= WriteReg(0x0a00+(0x18)*2+1, u8Data);           

            // ADC turn off.
            ReadReg(0x0900+(0x0B)*2,&u8Data);          
            u8_mcu_save = u8Data;
            u8Data |= 0x20;
            WriteReg(0x0900+(0x0B)*2,u8Data);

            printf("%s, %d, r, u8_mcu_save=0x%x\n",__FILE__,__LINE__,u8_mcu_save);
                        
            ReadReg(0x0A00+(0x0C)*2,&u8Data);
            u8Data |= 0x03;            
            WriteReg(0x0A00+(0x0C)*2,u8Data);
            
            // SR begin.
            WriteReg2bytes(0x1200+(0x01)*2, 0x0101);
            WriteReg2bytes(0x1200+(0x23)*2, 0xfffe);
            WriteReg2bytes(0x1200+(0x1c)*2, 0x00b2);

            usleep(1*1000);

            WriteReg2bytes(0x1200+(0x00)*2, 0x0101);

            usleep(1*1000);

            WriteReg2bytes(0x1200+(0x00)*2, 0x0301);  

            usleep(1*1000);

            WriteReg2bytes(0x1200+(0x00)*2, 0x0501);    

            usleep(1*1000);

            WriteReg2bytes(0x1200+(0x00)*2, 0x0001);     

            usleep(1*1000);

            WriteReg2bytes(0x1200+(0x23)*2, 0xffff);       

            usleep(1*1000);

            WriteReg2bytes(0x1200+(0x01)*2, 0x0103);

            WriteReg2bytes(0x1200+(0x1c)*2, 0x00b0);

            usleep(1*1000);
            //SR end
            
            // waiting for SR completed...
            usleep(10*1000);

            // turn off MPLL
            ReadReg(0x0A00+(0x35)*2,&u8Data);
            u8Data |= 0x80;            
            WriteReg(0x0A00+(0x35)*2,u8Data);
#if 0
            WriteReg2bytes(0x0900+(0x79)*2, 0x5a5a);
            
            WriteReg2bytes(0x0900+(0x7a)*2, 0xa5a5);           
            
            WriteReg2bytes(0x0900+(0x7b)*2, 0x5a5a);
            
            WriteReg2bytes(0x0900+(0x7c)*2, 0xa5a5);
                        
            ReadReg(0x0900+(0x09)*2, &u8Data);
            u8Data &= (0xff-0x01);
            WriteReg(0x0900+(0x09)*2, u8Data);
#endif
            // gpio pull low            
            if(gptr != NULL)
            {
                gptr->SetOff();
            }
            else
            {
                printf("where is gpio??, %s, %s, %d",__FILE__,__FUNCTION__,__LINE__);
            }
            g_u8_msb1240_sleep_mode_status = 2;
            printf("%s,%d, g_u8_msb1240_sleep_mode_status=%d\n",__FILE__,__LINE__,g_u8_msb1240_sleep_mode_status);
#endif
        }
        else
        {
#if (MSB1240_SW_SLEEP_MODE_EN == 1)

            // ADC turn on.            
            // ReadReg(0x0A00+(0x35)*2,&u8Data);
            // u8Data &= (0xff-0x80);            
            // WriteReg(0x0A00+(0x35)*2,u8Data);
            
            ReadReg(0x0A00+(0x0C)*2,&u8Data);
            u8Data &= (0xff-0x03);            
            WriteReg(0x0A00+(0x0C)*2,u8Data);

            WriteReg(0x0900+(0x0B)*2,u8_mcu_save);
    
            // enable miu access of mcu gdma
            WriteReg(0x1200+(0x23)*2,0xf0);
            // 10us delay
            usleep(10);

            WriteReg(0x0B00 + (0x19) * 2, 0x00);
            WriteReg(0x0B00 + (0x10) * 2, 0x00);
            usleep(5 * 1000);
            ReadReg(REG_MB_CNTL, &u8Data);           //check MB_CNTL ready
            do 
            {
                if (u8Data == 0xff) //wake up from sleep mode successfully
                {
                    g_u8_msb1240_sleep_mode_status = 0;
                    printf(">>>MSB1240: wake up Success!\n");
                    break;
                }
                else // still in sleep mode
                {
                    usleep(50*1000);
                    ReadReg(REG_MB_CNTL, &u8Data);       //check MB_CNTL ready
                    u16Retry++;
                    printf(">>>MSB1240: wake up Fail! (u16Retry: %d) \n", u16Retry);
                }
            } while(u16Retry < 20);
#else
            mapi_gpio *gptr = NULL;
            // gptr = mapi_gpio::GetGPIO_Dev(MSB1240_HW_SLEEP_PIN);
            // gpio pull high            
            if(gptr != NULL)
            {
                gptr->SetOn();
            }
            else
            {
                printf("where is gpio??, %s, %s, %d",__FILE__,__FUNCTION__,__LINE__);
            }

            // waiting for xtal stablized
            usleep(50*1000);

            g_u8_msb1240_sleep_mode_status = 1;

            // Turn on MPLL
            ReadReg(0x0A00+(0x35)*2,&u8Data);
            u8Data &= (0xff-0x80);            
            WriteReg(0x0A00+(0x35)*2,u8Data);
            
            // SR begin
            WriteReg2bytes(0x1200+(0x1c)*2, 0x00b2);
            WriteReg2bytes(0x1200+(0x01)*2, 0x0101);

            usleep(1*1000);

            WriteReg2bytes(0x1200+(0x23)*2, 0xfffe);

            usleep(1*1000);

            WriteReg2bytes(0x1200+(0x00)*2, 0x0501);

            usleep(1*1000);

            WriteReg2bytes(0x1200+(0x00)*2, 0x0301);

            usleep(1*1000);

            WriteReg2bytes(0x1200+(0x00)*2, 0x0501);

            usleep(1*1000);

            WriteReg2bytes(0x1200+(0x00)*2, 0x0001);

            usleep(1*1000);

            WriteReg2bytes(0x1200+(0x1c)*2, 0x00b0);

            usleep(1*1000);

            WriteReg2bytes(0x1200+(0x01)*2, 0x010d);

            usleep(1*1000);

            // WriteReg2bytes(0x1200+(0x23)*2, 0x0000);

            // usleep(1*1000);
            //SR End

            // waiting for SR completed...
            usleep(10*1000);

            // ADC turn on.                        
            ReadReg(0x0A00+(0x0C)*2,&u8Data);
            u8Data &= (0xff-0x03);            
            WriteReg(0x0A00+(0x0C)*2,u8Data);

            // ReadReg(0x0900+(0x0B)*2,&u8Data);
            // u8Data &= (0xff-0x20);
            WriteReg(0x0900+(0x0B)*2,u8_mcu_save);
            printf("%s, %d, w, u8_mcu_save=0x%x\n",__FILE__,__LINE__,u8_mcu_save);            

            // enable miu access of mcu gdma
            WriteReg(0x1200+(0x23)*2,0xf0);
            // 10us delay
            usleep(10);

#if (MSB1240_SELECT_IF_INPUT) //for Keres IF_AGC_s
            WriteReg2bytes(0x0a00+(0x18)*2, 0x0100);
#endif

            WriteReg(0x0B00 + (0x19) * 2, 0x00);
            WriteReg(0x0B00 + (0x10) * 2, 0x00);
            usleep(5 * 1000);
            ReadReg(REG_MB_CNTL, &u8Data);           //check MB_CNTL ready
            do 
            {
                if (u8Data == 0xff) //wake up from sleep mode successfully
                {
                    g_u8_msb1240_sleep_mode_status = 0;
                    printf(">>>MSB1240: wake up Success!\n");
                    break;
                }
                else // still in sleep mode
                {
                    usleep(50*1000);
                    ReadReg(REG_MB_CNTL, &u8Data);       //check MB_CNTL ready
                    u16Retry++;
                    printf(">>>MSB1240: wake up Fail! (u16Retry: %d) \n", u16Retry);
                }
            } while(u16Retry < 20);
            printf("%s,%d, g_u8_msb1240_sleep_mode_status=%d\n",__FILE__,__LINE__,g_u8_msb1240_sleep_mode_status);
#endif
        }
    }
    
    // ADC enable(1)/disable(0)
    if (SubCmd == 0x86)
    {
        MAPI_U8      u8Data  = 0x00;
        MAPI_U16     u16Retry = 0x00;

        if (g_u8_msb1240_sleep_mode_status > 1)
        {
            printf("%s, %s, %d, MSB1240 is sleeping, wake him up first\n",__FILE__,__FUNCTION__,__LINE__);
            return FALSE;
        }
        
        // adc power disable
        if (u32Param1 == 0)
        {                       
            // mask miu access for all and mcu
            WriteReg(0x1200+(0x23)*2 + 1,0x7f);
            WriteReg(0x1200+(0x23)*2,0xfe);
            // 10us delay
            usleep(10);

            WriteReg(0x0B00 + (0x10) * 2, 0x01);
            WriteReg(0x0B00 + (0x19) * 2, 0x03);
            WriteReg(REG_MB_CNTL, 0x00);             //clear MB_CNTL
            usleep(5 * 100);

            // ADC turn off.
            ReadReg(0x0A00+(0x0C)*2,&u8Data);
            u8Data |= 0x03;            
            WriteReg(0x0A00+(0x0C)*2,u8Data);
            
            // ReadReg(0x0A00+(0x35)*2,&u8Data);
            // u8Data |= 0x80;            
            // WriteReg(0x0A00+(0x35)*2,u8Data);
            g_u8_msb1240_adc_mode_status  = 1;            
        }
        else
        {
            // ADC turn on.            
            // ReadReg(0x0A00+(0x35)*2,&u8Data);
            // u8Data &= (0xff-0x80);            
            // WriteReg(0x0A00+(0x35)*2,u8Data);
            
            ReadReg(0x0A00+(0x0C)*2,&u8Data);
            u8Data &= (0xff-0x03);            
            WriteReg(0x0A00+(0x0C)*2,u8Data);

            // enable miu access of mcu gdma
            WriteReg(0x1200+(0x23)*2,0xf0);
            // 10us delay
            usleep(10);

            WriteReg(0x0B00 + (0x19) * 2, 0x00);
            WriteReg(0x0B00 + (0x10) * 2, 0x00);
            usleep(5 * 1000);
            ReadReg(REG_MB_CNTL, &u8Data);           //check MB_CNTL ready
            do 
            {
                if (u8Data == 0xff) //wake up from sleep mode successfully
                {
                    printf(">>>MSB1240: ADC Enable Success!\n");
                    g_u8_msb1240_adc_mode_status  = 0;
                    break;
                }
                else // still in sleep mode
                {
                    usleep(50*1000);
                    ReadReg(REG_MB_CNTL, &u8Data);       //check MB_CNTL ready
                    u16Retry++;
                    printf(">>>MSB1240: ADC Enable Fail! (u16Retry: %d) \n", u16Retry);
                }
            } while(u16Retry < 20);
        }
    }

    // set TS serial mode, TS data swap, TS CLK inverse
    // Para1. for DVBSS2 TS output mode 
    if (SubCmd == 0x87)
    {
        DBG_DEMOD_FLOW(printf("DVBSS2 TS out configuration:\n TS serial mode %d \n ",(MAPI_U8)u32Param1));
        S_TS_Output_VAL = (MAPI_U8)u32Param1;
    }
    // Para1. for DVBSS2 TS data swap on/off 
    if (SubCmd == 0x88)
    {
        DBG_DEMOD_FLOW(printf("DVBSS2 TS out configuration:\n TS data SWAP mode %d \n",(MAPI_U8)u32Param1));
        S_TS_DataSwap_VAL = (MAPI_U8)u32Param1;
    }
    // Para1. for DVBSS2 TS CLK inverse on/off
    if (SubCmd == 0x89)
    {
        DBG_DEMOD_FLOW(printf("DVBSS2 TS out configuration:\n TS CLK inv mode %d\n",(MAPI_U8)u32Param1));
        S_TS_clk_inv_VAL = (MAPI_U8)u32Param1;
    }
    // Para1. for DVBT2 TS CLK inverse on/off
    if (SubCmd == 0x8A)
    {
        DBG_DEMOD_FLOW(printf("DVBT2 TS out configuration:\n TS CLK inv mode %d\n",(MAPI_U8)u32Param1));
        T2_TS_CLK_INV = (MAPI_U8)u32Param1;
    }

    if (SubCmd == 0xEE)
    {
        MAPI_BOOL bRet = TRUE;
       // bRet &= MSB1240_demod_info((t2_mod_info*)pvParam3);
        if (bRet == FALSE)
            printf("ERROR!!!!!!!!!!!!!!!\n");
    }

    if (SubCmd == 0xFF) // temp solution, only for internal use, can't be called by upper layer
    {
        MAPI_U32    u32Timeout;
        MAPI_U32    u32LockTimeout=2000;
        MAPI_U8     u8Data = 0;
        MAPI_U16    u16RegAddress;
        //MAPI_U8     u8LockState;
        MAPI_U8     u8BitMask;
        MAPI_BOOL   bCheckPass=FALSE;
        static MAPI_U32 u32LockTimeStartDVBT = 0;
        static MAPI_U32 u32LockTimeStartDVBC = 0;//mick
        static MAPI_U32 u32LockTimeStartDVBT2 = 0;
        MAPI_U32 u32NowTime=MsOS_GetSystemTime();
        MAPI_U8     u8_fef_ind = 0;
        // MAPI_U8 tmp1, tmp2, tmp3;
        // printf("EXTCMD:0xFF. \n");
        switch(m_enCurrentDemodulator_Type)
        {
            case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2:
            {
                DBG_DEMOD_MSB(printf("EXTCMD:0xFF.                   T2 T2 T2\n"));
                u32Timeout=8000;

                ReadDspReg(E_T2_DVBT2_LOCK_HIS, &u8Data);

                ReadDspReg(E_T2_FEF_DET_IND, &u8_fef_ind);
                if (u8_fef_ind == 1)
                    u32Timeout = 12000;

                //MAPI_U8 tmp1, tmp2, tmp3;
                //printf("=>[Debug] E_T2_DVBT2_LOCK_HIS = %d \n", u8Data);
                //ReadDspReg(E_T2_FW_VER_0, &tmp1);
                //ReadDspReg(E_T2_FW_VER_1, &tmp2);
                //ReadDspReg(E_T2_FW_VER_2, &tmp3);
                //printf("=========================>[Debug] FW Ver = %x.%x.%x\n", tmp1, tmp2, tmp3);
                //ReadReg(0x0990, &tmp1);
                //printf("=========================>[Debug] State = %d \n", tmp1);

                if((u8Data & MBIT7) != 0x00)
                    bCheckPass=MAPI_TRUE;
                else
                {
                    if (p1_ever_lock_flag == 0)
                    {
                        u16RegAddress = E_T2_DVBT2_LOCK_HIS; //P1 Lock History
                        u8BitMask = MBIT5;
                        ReadDspReg(u16RegAddress, &u8Data);
                        if((u8Data&u8BitMask) == 0)
                        {
                            // Fix me please!
                            // Tmp solution: this timeout value needs to correct. (move to UpperLayer)
                            u32Timeout=600; //1500;//2000;
                        }
                        else
                        {
                            p1_ever_lock_flag = 1;
                        }
                    }
                }

                DBG_DEMOD_MSB(printf(">>>MSB1240: [%s] Lock Status = %d\n", __FUNCTION__, u8Data));
                if (bCheckPass)
                {

                    u32LockTimeStartDVBT2=MsOS_GetSystemTime();

                    FECLock = MAPI_TRUE;
                    if(bDoReset == TRUE)
                    {
                        printf("[msb1240]++++DVBT2 lock, freq=%d\n",g_u32Frequency);
                        DBG_DEMOD_MSB(printf("\t\t\tDTV_DVB_T_GetLockStatus(LOCK/T2) TIME   %d(=%d-%d)\n", u32LockTimeStartDVBT2-u32StartTime, u32LockTimeStartDVBT2, u32StartTime));//to measure time
#if (TIMING_VERIFICATION==1)
                        tmm_14 = GIVE_ME_TIME
                        show_timer();
#endif
                    }
                    bDoReset = FALSE;
                    *((mapi_demodulator_datatype::EN_LOCK_STATUS *)pvParam3) = mapi_demodulator_datatype::E_DEMOD_LOCK;
                }
                else if ((u32NowTime - u32ChkScanTimeStart < u32Timeout)
                    ||(u32NowTime - u32LockTimeStartDVBT2 < u32LockTimeout))
                {
                    FECLock = MAPI_FALSE;
                    *((mapi_demodulator_datatype::EN_LOCK_STATUS *)pvParam3) = mapi_demodulator_datatype::E_DEMOD_CHECKING;
                }
                else
                {
                    FECLock = MAPI_FALSE;
                    *((mapi_demodulator_datatype::EN_LOCK_STATUS *)pvParam3) = mapi_demodulator_datatype::E_DEMOD_UNLOCK;
                }

                if ( (FECLock == MAPI_TRUE) && (u8_g_pid0_timeout_flag == 1))
                {
                    MAPI_U16 bch_eflag2_sum = 0;
                    MAPI_U8  reg = 0;

                    ReadReg(0x2600+0x26*2 + 1,&reg);
                    bch_eflag2_sum = reg;
                    ReadReg(0x2600+0x26*2,&reg);
                    bch_eflag2_sum = (bch_eflag2_sum<<8)|reg;

                    if (bch_eflag2_sum == 0x00)
                    {
                        //djb rst
                        ReadReg(0x2900+0x00*2,&reg);
                        reg |= 0x01;
                        WriteReg(0x2900+0x00*2,reg);
                        usleep(1000);
                        ReadReg(0x2900+0x00*2,&reg);
                        reg &= (0xff-0x01);
                        WriteReg(0x2900+0x00*2,reg);
                        printf("\ndjb_rst........\n");
                        // DBG_DEMOD_MSB(printf("\n......[mj]djb rst......\n");)
                    }
                    u8_g_pid0_timeout_flag = 0;
                }
            }
            break;
            case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
            {
                u32Timeout=1500;
                ReadDspReg(T_DVBT_LOCK_HIS, &u8Data);
                DBG_DEMOD_MSB(printf(">>>MSB1240: [%s] Lock Status = %d\n", __FUNCTION__, u8Data));
                if( (u8Data&MBIT6) == MBIT6 )
                    bCheckPass=MAPI_TRUE;
                else if ( (u8Data&MBIT3) == MBIT3 )
                {
                  u32Timeout=6000;
                }
                //DBG_DEMOD_MSB(printf(">>>MSB1240: [%s] Lock Status = %d\n", __FUNCTION__, u8Data));
                if (bCheckPass)
                {
                    u32LockTimeStartDVBT=MsOS_GetSystemTime();
                    FECLock = MAPI_TRUE;
                    if(bDoReset==TRUE)
                    {
                        printf("[msb1240]++++DVBT lock, freq=%d\n",g_u32Frequency);
                        DBG_DEMOD_MSB(printf("\t\t\tDTV_DVB_T_GetLockStatus(LOCK/T) TIME   %d(=%d-%d)\n", u32LockTimeStartDVBT-u32StartTime, u32LockTimeStartDVBT, u32StartTime));//to measure time
#if (TIMING_VERIFICATION==1)
                        tmm_14 = GIVE_ME_TIME
                        show_timer();
#endif
                    }
                    bDoReset = FALSE;
                    *((mapi_demodulator_datatype::EN_LOCK_STATUS *)pvParam3) = mapi_demodulator_datatype::E_DEMOD_LOCK;
                }
                else if ((u32NowTime - u32ChkScanTimeStart < u32Timeout)
                ||(u32NowTime - u32LockTimeStartDVBT < u32LockTimeout))
                {
                    FECLock = MAPI_FALSE;
                    *((mapi_demodulator_datatype::EN_LOCK_STATUS *)pvParam3) = mapi_demodulator_datatype::E_DEMOD_CHECKING;
                }
                else
                {
                    FECLock = MAPI_FALSE;
                    *((mapi_demodulator_datatype::EN_LOCK_STATUS *)pvParam3) = mapi_demodulator_datatype::E_DEMOD_UNLOCK;
                }
            }
            break;
            case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_C://mick
            {
                    u32Timeout=1500;
                    //u16RegAddress=0x11E0;
                    //u8LockState=0x0C;
                    //ReadReg(u16RegAddress, &u8Data);
                    //printf("[msb1240]+++++++++DVBC State---->%d\n",u8Data);//mick
                    ReadDspReg(C_lock_indicator, &u8Data);
                    if( (u8Data & MBIT7) == MBIT7 )//FEC Lock Indicator
                    {
                        bCheckPass=MAPI_TRUE;
                    }
                    else if ( (u8Data&MBIT0) == MBIT0 )//TR Lock_history Indicator
                    {
                        u32Timeout=6000;
                    }
#if 0
                    else
                    {
                        u16RegAddress =  0x0990; //TPS his Lock,
                        u8BitMask = MBIT3;
                        ReadReg(u16RegAddress, &u8Data);
                        if((u8Data&u8BitMask) > 0)
                           u32Timeout=6000;
                    }
#endif
                //DBG_DEMOD_MSB(printf(">>>MSB1240: [%s] Lock Status = %d\n", __FUNCTION__, u8Data));
                if (bCheckPass)
                {
                    u32LockTimeStartDVBC=MsOS_GetSystemTime();
                    FECLock = MAPI_TRUE;
                    if(bDoReset==TRUE)
                    {
                        printf("[msb1240]++++DVBC lock, freq=%d\n",g_u32Frequency);
                        DBG_DEMOD_MSB(printf("\t\t\tDTV_DVB_C_GetLockStatus(LOCK/C) TIME   %d(=%d-%d)\n", u32LockTimeStartDVBC-u32StartTime, u32LockTimeStartDVBC, u32StartTime));//to measure time
#if (TIMING_VERIFICATION==1)
                        tmm_14 = GIVE_ME_TIME
                        show_timer();
#endif
                    }
                    bDoReset = FALSE;
                    *((mapi_demodulator_datatype::EN_LOCK_STATUS *)pvParam3) = mapi_demodulator_datatype::E_DEMOD_LOCK;
                }
                else if ((u32NowTime - u32ChkScanTimeStart < u32Timeout)
                ||(u32NowTime - u32LockTimeStartDVBC < u32LockTimeout))
                {
                    FECLock = MAPI_FALSE;
                    *((mapi_demodulator_datatype::EN_LOCK_STATUS *)pvParam3) = mapi_demodulator_datatype::E_DEMOD_CHECKING;
                }
                else
                {
                    FECLock = MAPI_FALSE;
                    *((mapi_demodulator_datatype::EN_LOCK_STATUS *)pvParam3) = mapi_demodulator_datatype::E_DEMOD_UNLOCK;
                }
            }
            break;
            default:
                *((mapi_demodulator_datatype::EN_LOCK_STATUS *)pvParam3) = mapi_demodulator_datatype::E_DEMOD_UNLOCK;
        }
                }
    return MAPI_TRUE;
}


//##########################################################################################################
//##########################################################################################################
//########################################  Public:DTV Implementation ######################################
//##########################################################################################################
//##########################################################################################################
// DVBT2 95~101ms, DVBT 38~39ms
MAPI_BOOL device_demodulator_extend::DTV_SetFrequency(MAPI_U32 u32Frequency, RF_CHANNEL_BANDWIDTH eBandWidth, MAPI_BOOL bPalBG, MAPI_BOOL bLPsel)
{
    mapi_tuner *pTuner = NULL;
    MAPI_U32 u32DMD_IfFreq = 5000;
    MAPI_U8  u8_if_agc_mode = 0;

    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    //MAPI_BOOL b_pal_bg;
    //MAPI_BOOL bStatus = MAPI_FALSE;
    g_u32Frequency=u32Frequency;
    DBG_DEMOD_MSB(printf("DTV_SetFrequency %d ,%d \n", (int)u32Frequency, (int)eBandWidth));

    if (g_u8_msb1240_sleep_mode_status != 0)
    {
        printf("%s, %s, %d, MSB1240 is sleeping, wake him up first\n",__FILE__,__FUNCTION__,__LINE__);
        return FALSE;
    }

    mapi_scope_lock(scopeLock, &m_MutexOuter);
    FECLock = MAPI_FALSE;
    u32ChkScanTimeStart = MsOS_GetSystemTime();

#if(TIMING_VERIFICATION == 1)
    tmm_11 = GIVE_ME_TIME
#endif

    if(device_demodulator_extend::m_enCurrentDemodulator_Type==mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2)
    {
        MAPI_U8 bw = E_DEMOD_BW_8M;
        switch (eBandWidth)
        {
            case E_RF_CH_BAND_6MHz:
                bw = E_DEMOD_BW_6M;
                break;
            case E_RF_CH_BAND_7MHz:
                bw = E_DEMOD_BW_7M;
                break;
            case E_RF_CH_BAND_8MHz:
                bw = E_DEMOD_BW_8M;
                break;
            default:
                bw = E_DEMOD_BW_8M;
                break;
        }
        
        pTuner = mapi_interface::Get_mapi_pcb()->GetDvbtTuner(0);
        if (pTuner!=NULL)
        {
            if ( MAPI_FALSE == (pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_DTV_IF_FREQ, E_TUNER_DTV_DVB_T2_MODE, eBandWidth, &u32DMD_IfFreq)))
            {
                u32DMD_IfFreq = 5000;
            }
            if ( MAPI_FALSE == (pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_DEMOD_CONFIG, E_TUNER_DTV_DVB_T2_MODE, eBandWidth, (void*)&u8_if_agc_mode)))
            {
                u8_if_agc_mode = 0;
            }            
        }
        
        Reset();
        usleep(500);
        WriteDspReg((MAPI_U16)E_T2_BW, bw);      // BW: 0->1.7M, 1->5M, 2->6M, 3->7M, 4->8M, 5->10M

        WriteDspReg((MAPI_U16)E_T2_PLP_ID, g_msb1240_plp_id);
        WriteDspReg((MAPI_U16)E_T2_FC_L, (MAPI_U8)u32DMD_IfFreq);
        WriteDspReg((MAPI_U16)E_T2_FC_H, (MAPI_U8)(u32DMD_IfFreq>>8));
        WriteDspReg((MAPI_U16)E_T2_IF_AGC_INV_PWM_EN, u8_if_agc_mode);

        WriteReg(REG_FSM_EN, 0x01); // FSM_EN
        g_u8_bw = bw;
        DBG_DEMOD_MSB(printf("\n[msb1240][dvbt2]DTV_SetFrequency, plp=0x%x,bw=%d, if_KHz=%d, if_agc_mode=%d\n",g_msb1240_plp_id,bw,u32DMD_IfFreq,u8_if_agc_mode);)
    }
    else if (device_demodulator_extend::m_enCurrentDemodulator_Type==mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T)
    {
        pTuner = mapi_interface::Get_mapi_pcb()->GetDvbtTuner(0);
        if (pTuner!=NULL)
        {
            if ( MAPI_FALSE == (pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_DTV_IF_FREQ, E_TUNER_DTV_DVB_T_MODE, eBandWidth, &u32DMD_IfFreq)))
            {
                u32DMD_IfFreq = 5000;
            }
            if ( MAPI_FALSE == (pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_DEMOD_CONFIG, E_TUNER_DTV_DVB_T2_MODE, eBandWidth, (void*)&u8_if_agc_mode)))
            {
                u8_if_agc_mode = 0;
            }            
        }    
        
        Reset();
        usleep(500);

        WriteDspReg((MAPI_U16)T_CONFIG_BW, eBandWidth);      // BW: 1->6M, 2->7M, 3->8M
        WriteDspReg((MAPI_U16)T_CONFIG_FC_L, (MAPI_U8)u32DMD_IfFreq);
        WriteDspReg((MAPI_U16)T_CONFIG_FC_H, (MAPI_U8)(u32DMD_IfFreq>>8));
        WriteDspReg((MAPI_U16)T_CONFIG_IF_INV_PWM_OUT_EN, u8_if_agc_mode);

        // MSB1240_DVBT_DSPREG_TABLE[(MAPI_U16)T_CONFIG_BW-(MAPI_U16)T_OPMODE_RFAGC_EN] = (MAPI_U8)eBandWidth;
        // MSB1240_DVBT_DSPREG_TABLE[(MAPI_U16)T_PARAM_CHECK_SUM-(MAPI_U16)T_OPMODE_RFAGC_EN] = DTV_DVBT_DSPReg_CRC();

        // WriteDspReg((MAPI_U8)T_PARAM_CHECK_SUM, MSB1240_DVBT_DSPREG_TABLE[(MAPI_U8)T_PARAM_CHECK_SUM-(MAPI_U8)T_OPMODE_RFAGC_EN]);      // BW: 1->6M, 2->7M, 3->8M

        // Hierarchy mode
        WriteDspReg((MAPI_U8)T_CONFIG_LP_SEL, bLPsel? 0x01:0x00);

        WriteReg(REG_FSM_EN, 0x01); // FSM_EN//Active(1);
    }

#if(TIMING_VERIFICATION == 1)
    tmm_12 = GIVE_ME_TIME
    show_timer();
#endif

    return MAPI_TRUE;
}

#if (DVBC_SYSTEM_ENABLE == 1)
MAPI_BOOL device_demodulator_extend::DTV_DVB_C_SetFrequency(MAPI_U32 u32Frequency, RF_CHANNEL_BANDWIDTH eBandWidth,MAPI_U32 u32SymRate, mapi_demodulator_datatype::EN_CAB_CONSTEL_TYPE eQAM)
{
    printf("%s(),%d\n",__FUNCTION__,__LINE__);

    MAPI_U8             reg_symrate_l = 0;
    MAPI_U8             reg_symrate_h = 0;

    MAPI_U32            u32DMD_IfFreq = 0;
    MAPI_U8             u8_if_agc_mode = 0;

    mapi_tuner          *pTuner = NULL;
    MAPI_BOOL           status = true;


    g_u32Frequency = u32Frequency;
    eBandWidth = eBandWidth;

    DBG_DEMOD_MSB(printf("DTV_SetFrequency %d ,%d \n", (int)u32Frequency, (int)eBandWidth));
    mapi_scope_lock(scopeLock, &m_MutexOuter);

    if (g_u8_msb1240_sleep_mode_status != 0)
    {
        printf("%s, %s, %d, MSB1240 is sleeping, wake him up first\n",__FILE__,__FUNCTION__,__LINE__);
        return FALSE;
    }

    // SetDvbcParam(eQAM);//QAM

    reg_symrate_l = (MAPI_U8) (u32SymRate & 0xff);
    reg_symrate_h = (MAPI_U8) (u32SymRate >> 8);
    // reg_qam = gQamVal;

    pTuner = mapi_interface::Get_mapi_pcb()->GetDvbtTuner(0);
    if (pTuner!=NULL)
    {
        if ( MAPI_FALSE == (pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_DTV_IF_FREQ, E_TUNER_DTV_DVB_C_MODE, E_RF_CH_BAND_8MHz, &u32DMD_IfFreq)))
        {
            u32DMD_IfFreq = 5000;
        }
        if ( MAPI_FALSE == (pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_DEMOD_CONFIG, E_TUNER_DTV_DVB_T2_MODE, eBandWidth, (void*)&u8_if_agc_mode)))
        {
            u8_if_agc_mode = 0;
        }            
    }    

    //// Reset Demod ///////////////////
    Reset();
    usleep(500);
    //// DSP Register Overwrite ///////////////////
    status &= WriteDspReg((MAPI_U16)C_config_fc_l, (MAPI_U8)u32DMD_IfFreq);
    status &= WriteDspReg((MAPI_U16)C_config_fc_h, (MAPI_U8)(u32DMD_IfFreq>>8));
    status &= WriteDspReg((MAPI_U16)C_if_inv_pwm_out_en, u8_if_agc_mode);
    if (u32SymRate == 0)//Auto SR QAM Mode
    {
        // DTV_DVB_C_Set_Config_dvbc_auto(MAPI_TRUE);
        status &= WriteDspReg((MAPI_U16)C_opmode_auto_scan_sym_rate, 1);
        status &= WriteDspReg((MAPI_U16)C_opmode_auto_scan_qam, 1);       
        status &= WriteDspReg((MAPI_U16)C_config_bw_l, C_BW_L);
        status &= WriteDspReg((MAPI_U16)C_config_bw_h, C_BW_H);    
    }
    else //Manual Mode
    {
        status &= WriteDspReg((MAPI_U16)C_opmode_auto_scan_sym_rate, 0);
        status &= WriteDspReg((MAPI_U16)C_opmode_auto_scan_qam, 0);
        status &= WriteDspReg((MAPI_U16)C_config_bw_l, reg_symrate_l);
        status &= WriteDspReg((MAPI_U16)C_config_bw_h, reg_symrate_h);
        status &= WriteDspReg((MAPI_U16)C_config_qam, (MAPI_U8)eQAM);
    }
    DBG_DEMOD_MSB(printf(" @MSB1240_dvbc_config SymRate=[%d]\n",(int)u32SymRate));
    DBG_DEMOD_MSB(printf(" @MSB1240_dvbc_config QAM Mode=[%d]\n",(MAPI_U8)eQAM));
    DBG_DEMOD_MSB(printf(" @MSB1240_dvbc_config OK\n"));

    status &= WriteReg(REG_FSM_EN, 0x01);

    return status;
}

MAPI_U16 device_demodulator_extend::DTV_DVB_C_GetCurrentSymbolRate(void)
{
    MAPI_U32 u32_intp = 0;
    MAPI_U8  u8_reg = 0;
    float    symbol_rate = 0.0;

    ReadReg(0x2C00 + (0x27) * 2 + 3, &u8_reg);
    u32_intp = u8_reg;
    ReadReg(0x2C00 + (0x27) * 2 + 2, &u8_reg);
    u32_intp = (u32_intp<<8)|u8_reg;
    ReadReg(0x2C00 + (0x27) * 2 + 1, &u8_reg);
    u32_intp = (u32_intp<<8)|u8_reg;
    ReadReg(0x2C00 + (0x27) * 2 + 0, &u8_reg);
    u32_intp = (u32_intp<<8)|u8_reg;

    symbol_rate = 12.0/((float)u32_intp/4194304.0 + 1.0)*1000;
    
    printf("%s(),%d, sr=%d\n",__FUNCTION__,__LINE__,(MAPI_U16)symbol_rate);
    return (MAPI_U16)symbol_rate;
}

mapi_demodulator_datatype::EN_CAB_CONSTEL_TYPE device_demodulator_extend::DTV_DVB_C_GetSignalModulation(void)
{
    MAPI_U8  u8_reg = 0;

    // 0:16q,1:32q,2:64q,3:128q,4:256q
    ReadReg(0x2302, &u8_reg);
    u8_reg = u8_reg&0x07;

    // printf("%s(),%d, qam=%d\n",__FUNCTION__,__LINE__,u8_reg);
    
    switch (u8_reg)
    {
        case 0:
            return mapi_demodulator_datatype::E_CAB_QAM16;
        case 1:
            return mapi_demodulator_datatype::E_CAB_QAM32;
        case 2:
            return mapi_demodulator_datatype::E_CAB_QAM64;
        case 3:
            return mapi_demodulator_datatype::E_CAB_QAM128;
        case 4:
            return mapi_demodulator_datatype::E_CAB_QAM256;
        default:
            return mapi_demodulator_datatype::E_CAB_INVALID;
    }

    return mapi_demodulator_datatype::E_CAB_INVALID;
}
#endif

#if (DVBS_SYSTEM_ENABLE == 1)
MAPI_BOOL device_demodulator_extend:: MSB1240_DVBS_SetFrequency(MAPI_U16 u16CenterFreq_MHz, MAPI_U32 u32SymbolRate_Ks)
{
    MAPI_BOOL bRet=MAPI_TRUE;
    MAPI_U16 u16LockCount;
    FECLock = MAPI_FALSE;
    bRet&=MDrv_DVBS_Tuner_SetFreq(u16CenterFreq_MHz, u32SymbolRate_Ks);   
    if (bRet==MAPI_TRUE)
    {     
        u16LockCount=0;
        do
        {
            bRet=MAPI_TRUE;
            bRet&=MDrv_DVBS_Tuner_CheckLock();
            MsOS_DelayTask(1);
            u16LockCount++;		
        }while((bRet==MAPI_FALSE) && (u16LockCount<MSB1240_DVBS_TUNER_WAIT_TIMEOUT)) ;
        if (bRet==MAPI_TRUE)
        {
            printf(("Tuner PLL Lock\n"));
        }
        else
        {
            printf(("Tuner PLL Unlock\n"));
        }
    }    
    return bRet;
}
MAPI_BOOL device_demodulator_extend:: MSB1240_DVBS_Demod_Restart(DEMOD_MS_FE_CARRIER_PARAM_MSB1240* pParam)
{
    MAPI_BOOL bRet=MAPI_TRUE;
    MAPI_U16  u16Address =0;
    MAPI_U8   u8Data =0;
    MAPI_U16  u16SymbolRate =0;
    MAPI_U16  __attribute__ ((unused)) u16CenterFreq =0;
    //MAPI_U16 u16LockCount;
    MAPI_U8   u8counter = 0;
    MAPI_U16  u16_fwVER = 0;

    u16SymbolRate=(pParam->SatParam.u32SymbolRate/1000);
    u16CenterFreq=pParam->u32Frequency;
    printf("MSB1240 MSB1240_DVBS_Demod_Restart+ Fc:%d MHz SymbolRate %d KS/s\n", u16CenterFreq, u16SymbolRate);
    IIC_Bypass_Mode(MAPI_TRUE);
    bRet&=MSB1240_DVBS_SetFrequency(pParam->u32Frequency,(pParam->SatParam.u32SymbolRate/1000)); // (MHz, KS/s)
    IIC_Bypass_Mode(MAPI_FALSE);
    u16Address=0x0990;
    bRet&=ReadReg(u16Address, &u8Data);
    u8Data&=0xF0;
    bRet&=WriteReg(u16Address,u8Data);
    MsOS_DelayTask(50);
    u16Address=0x0B52;
    u8Data=(u16SymbolRate&0xFF);
    bRet&=WriteReg(u16Address,u8Data);
    u16Address=0x0B53;
    u8Data=((u16SymbolRate>>8)&0xFF);
    bRet&=WriteReg(u16Address,u8Data);

#if (STB_ENABLE == 1)
    WriteReg2bytes(0x0A00+(0x51)*2, 0x0000);// for Keres  ADC path
#endif

    //ADCPLL IQ swap
    if(MSB1240_DVBS_ADCPLL_IQ_SWAP==1)
    {
        u16Address=0x0A03;
        bRet&=ReadReg(u16Address, &u8Data);
        u8Data|=(0x10);
        bRet&=WriteReg(u16Address, u8Data);
    }
//Configure Serial/Parallel mode for TS output
    u16Address=0x2A40;
    bRet&=ReadReg(u16Address, &u8Data);
    DBG_DEMOD_FLOW(printf("setting TS serial/parallel mode as %x\n",S_TS_Output_VAL));
    if(S_TS_Output_VAL) //Serial Mode
      u8Data|=0x01;
    else               //Parallel Mode
      u8Data&=~(0x01);
    bRet&=WriteReg(u16Address,u8Data);	
//Configure TS output data swap mode
    u16Address=0x2A40;
    bRet&=ReadReg(u16Address, &u8Data);
    DBG_DEMOD_FLOW( printf("setting TS data swap mode as %x\n",S_TS_DataSwap_VAL));
    if(S_TS_DataSwap_VAL) //enable
      u8Data|=0x20;
    else               //disable  
      u8Data&=~(0x20);
    bRet&=WriteReg(u16Address,u8Data);	
//Configure TS clk inv mode
    u16Address=0x0924;
    bRet&=ReadReg(u16Address, &u8Data);
    DBG_DEMOD_FLOW(printf("setting TS clk inv mode as %x\n",S_TS_clk_inv_VAL));
    if(S_TS_clk_inv_VAL) //enable
        u8Data|=0x20;
    else               //disable  
        u8Data&=~(0x20);
    bRet&=WriteReg(u16Address,u8Data);
    
//Configure AGC IFAGC_polarity  0:positive  1:negative
    WriteDspReg((MAPI_U16)E_S2_IFAGC_POLARITY, 0x01);

//setting TS SSC (Spread Function)
    WriteDspReg((MAPI_U16)E_S2_SPREAD_SPAN , S_TS_SPREAD_SPAN);
    WriteDspReg((MAPI_U16)E_S2_SPREAD_STEP, S_TS_SPREAD_STEP_SIZE);

    u16Address=0x0990;
    bRet&=ReadReg(u16Address, &u8Data);
    u8Data&=0xF0;
    u8Data|=0x01;
    bRet&=WriteReg(u16Address,u8Data);

    u8counter = 6;
    bRet&=ReadReg(u16Address,&u8Data);
    while( ((u8Data&0x01) == 0x00) && (u8counter != 0) )
    {
        printf("0x0990=0x%x, bRet=%d, u8counter=%d\n",u8Data,bRet,u8counter);
        u8Data|=0x01;
        bRet&=WriteReg(u16Address,u8Data);
        bRet&=ReadReg(u16Address,&u8Data);
        u8counter--;
    }

    if((u8Data&0x01)==0x00)
    {
        bRet = FALSE;
    }

    bRet &=ReadReg(0x0900+(0x44)*2+1, &u8Data);
    u16_fwVER = u8Data;
    bRet &=ReadReg(0x0900+(0x44)*2, &u8Data);
    u16_fwVER = (u16_fwVER<<8)|u8Data;
    printf("MSB1240_S MDrv_Demod_Restart-, FW_VER=0x%x\n",u16_fwVER);
    return bRet;
}

MAPI_BOOL device_demodulator_extend::DTV_DVB_S_SetFrequency(MAPI_U32 u32Frequency, MAPI_U32 u32SymbolBitrate)
{
    MAPI_BOOL bRet=MAPI_TRUE;
    DEMOD_MS_FE_CARRIER_PARAM_MSB1240 pParam;

    //printf("@@@@@@@@@@@@@@@@@@@@@+++++++++++++++++++++++++++++++++++Debug_Version 1.13 \n");
    pParam.u32Frequency = u32Frequency;
    pParam.SatParam.u32SymbolRate = u32SymbolBitrate;
    _u32CurrentFreq=u32Frequency;
    _u32CurrentSR=u32SymbolBitrate;
    // _u32LocktimeStart=MsOS_GetSystemTime();
    _u32LocktimeStart = mapi_time_utility::GetTime0();
    _u8LocktimeFlag=0;
    FECLock = MAPI_FALSE;

    bRet &= MSB1240_DVBS_Demod_Restart(&pParam);
    return bRet;
}

mapi_demodulator_datatype::EN_LOCK_STATUS device_demodulator_extend::DTV_DVB_S_GetLockStatus(void)
{
    MAPI_BOOL bLock = FALSE;
    mapi_demodulator_datatype::EN_LOCK_STATUS eLockState=mapi_demodulator_datatype::E_DEMOD_UNLOCK;

    mapi_scope_lock(scopeLock, &m_MutexOuter);   
 
    bLock=MSB1240_DVBS_GetLock();
    if(bLock)
    {
        eLockState = mapi_demodulator_datatype::E_DEMOD_LOCK;
        FECLock = MAPI_TRUE;
    }
    else
    {
        eLockState = mapi_demodulator_datatype::E_DEMOD_UNLOCK;
        FECLock = MAPI_FALSE;     
    }  
    return eLockState;
}

MAPI_BOOL device_demodulator_extend::MSB1240_DVBS_GetLock(void)
{
    MAPI_BOOL bRet=MAPI_TRUE;
    MAPI_U16 u16Address =0;
    MAPI_U8 u8Data =0;

    u16Address=0x0990;
    bRet&=ReadReg(u16Address, &u8Data);
    if ((u8Data&0x02)==0x00)
    {
        u16Address=0x0B55;
        bRet&=ReadReg(u16Address, &u8Data);
        if ((u8Data>=15) && (u8Data!=0xcd)) //For lock/unlock issue.
        {
            if (u8Data==15)
            {
                _bDemodType=MAPI_FALSE;   //S
            }
            else
            {
                _bDemodType=MAPI_TRUE;    //S2
            }
            bRet = MAPI_TRUE;
        }
        else
        {
            bRet = MAPI_FALSE;
        }

    }
    else
    {
        bRet = MAPI_TRUE;
    }
    return bRet;
}


MAPI_BOOL device_demodulator_extend::DTV_DVB_S_SetToneBurst(mapi_dish_datatype::EN_SAT_TONEBUREST_TYPE eTone)
{
    MAPI_BOOL bTone1;
    if(eTone == mapi_dish_datatype::E_TONE1)
        bTone1 = MAPI_TRUE;
    else
        bTone1 = MAPI_FALSE;
    return MSB1240_DVBS_DiSEqC_SetTone(bTone1);
}

MAPI_BOOL device_demodulator_extend::MSB1240_DVBS_DiSEqC_SetTone(MAPI_BOOL bTone1)
{
    MAPI_BOOL bRet=MAPI_TRUE;
    MAPI_U16 u16Address;
    MAPI_U8 u8Data;
    //MAPI_U16 u16WaitCount;
    MAPI_U8 u8ReSet22k;
    
    mapi_scope_lock(scopeLock, &m_MutexOuter);
    //DIG_DISEQC_TX1
    u16Address=0x0DC4;
    u8Data=0x01;
    bRet&=WriteReg(u16Address, u8Data);

    //DIG_DISEQC_EN
    u16Address=0x0DC0;
    u8Data=0x4E;
    bRet&=WriteReg(u16Address, u8Data);
    //DIG_DISEQC_FCAR
    u16Address=0x0DCC;
    u8Data=0x80;
    bRet&=WriteReg(u16Address, u8Data);
    //DIG_DISEQC_MOD
    u16Address=0x0DC2;
    bRet&=ReadReg(u16Address, &u8Data);
    u8ReSet22k=u8Data;

    if (bTone1==MAPI_TRUE)
    {
       // Toneburst1
       u8Data=0x19;
       bRet&=WriteReg(u16Address, u8Data);
       _u8ToneBurstFlag=1;
    }
    else
    {
       // Toneburst0
       u8Data=0x11;
       bRet&=WriteReg(u16Address, u8Data);
       _u8ToneBurstFlag=2;
    }
    //DIG_DISEQC_TX_EN
    u16Address=0x0DCD;
    u8Data=u8Data&~(0x01);
    bRet&=ReadReg(u16Address, &u8Data);
    u8Data=u8Data|0x3E;
    bRet&=WriteReg(u16Address, u8Data);
    MsOS_DelayTask(10);
    bRet&=ReadReg(u16Address, &u8Data);
    u8Data=u8Data&~(0x3E);
    bRet&=WriteReg(u16Address, u8Data);
    MsOS_DelayTask(1);
    u8Data=u8Data|0x01;
    bRet&=WriteReg(u16Address, u8Data);

    MsOS_DelayTask(30);//(100)
    //For ToneBurst 22k issue.
    u16Address=0x0DC2;//For ToneBurst 22k issue.
    u8Data=u8ReSet22k;
    bRet&=WriteReg(u16Address, u8Data);

    return bRet;
}

MAPI_BOOL device_demodulator_extend:: DTV_DVB_S_SetLNBPower(mapi_dish_datatype::EN_SAT_LNBPOWER_TYPE eTone)
{
    return MAPI_TRUE;
}

MAPI_BOOL device_demodulator_extend:: DTV_DVB_S_Set22KOnOff(MAPI_BOOL bOn)
{
    return MSB1240_DVBS_DiSEqC_Set22kOnOff(bOn);
}

MAPI_BOOL device_demodulator_extend::MSB1240_DVBS_DiSEqC_Set22kOnOff(MAPI_BOOL b22kOn)
{
    MAPI_BOOL bRet=MAPI_TRUE;
    MAPI_U16 u16Address =0;
    MAPI_U8   u8Data =0;
    mapi_scope_lock(scopeLock, &m_MutexOuter);

    u16Address=0x0DC2;
    bRet&=ReadReg(u16Address, &u8Data);
    if (b22kOn==MAPI_TRUE)
    {
        // set bit[3:3]
        u8Data&=0xc7;  
        u8Data|=0x08;
    }
    else
    {
        // clear bits[5:3]  
        u8Data&=0xc7;          
    }
    bRet&=WriteReg(u16Address, u8Data);
    return bRet;
}


MAPI_BOOL device_demodulator_extend:: DTV_DVB_S_Get22KStatus(MAPI_BOOL* bOn)
{
    return MSB1240_DVBS_DiSEqC_Get22kOnOff(bOn);
}

MAPI_BOOL device_demodulator_extend::MSB1240_DVBS_DiSEqC_Get22kOnOff(MAPI_BOOL* b22kOn)
{
    MAPI_BOOL bRet=MAPI_TRUE;
    MAPI_U16 u16Address =0;
    MAPI_U8   u8Data =0;
    
    mapi_scope_lock(scopeLock, &m_MutexOuter);
    u16Address=0x0DC2;
    bRet&=ReadReg(u16Address, &u8Data);
    if ((u8Data&0x38)==0x08)
    {
        *b22kOn=MAPI_TRUE;
    }
    else
    {
        *b22kOn=MAPI_FALSE;
    }
    return bRet;
}

MAPI_BOOL device_demodulator_extend::DTV_DVB_S_SendDiSEqCCmd(MAPI_U8* pCmd,MAPI_U8 u8CmdSize)
{
    return MSB1240_DVBS_DiSEqC_SendCmd(pCmd, u8CmdSize);
}

MAPI_BOOL device_demodulator_extend::MSB1240_DVBS_DiSEqC_SendCmd(MAPI_U8* pCmd,MAPI_U8 u8CmdSize)
{
    MAPI_BOOL bRet=MAPI_TRUE;
    MAPI_U16 u16Address =0;
    MAPI_U8   u8Data =0;
    MAPI_U8   u8Index =0;
    MAPI_U16 u16WaitCount =0;

    mapi_scope_lock(scopeLock, &m_MutexOuter);

    printf(("\r\n MDrv_DiSEqC_SendCmd++++"));

    u16Address=0x0990;
    bRet&=ReadReg(u16Address, &u8Data);
    u8Data=(u8Data&~(0x10));
    bRet&=WriteReg(u16Address, u8Data);

    //u16Address=0x0BC4;
    for (u8Index=0; u8Index < u8CmdSize; u8Index++)
    {
        u16Address=0x0DC4+u8Index;
        u8Data=*(pCmd+u8Index);
        bRet&=WriteReg(u16Address, u8Data);
    }
    u8Data=((u8CmdSize-1)&0x07)|0x40;
    //Tone and Burst switch,Mantis 0232220
    if(((*pCmd)==0xE0)&&((*(pCmd + 1))==0x10)&&((*(pCmd + 2))==0x38)&&((((*(pCmd + 3))&0x0C)==0x0C)||(((*(pCmd + 3))&0x04)==0x04)))
    {
        u8Data|=0x80;   //u8Data|=0x20;Tone Burst1
    }
    else if(((*pCmd)==0xE0)&&((*(pCmd + 1))==0x10)&&((*(pCmd + 2))==0x38))
    {
        u8Data|=0x20;   //u8Data|=0x80;ToneBurst0
    }

    u16Address=0x0B54;
    bRet&=WriteReg(u16Address, u8Data);
    MsOS_DelayTask(10);
    u16Address=0x0990;
    bRet&=ReadReg(u16Address, &u8Data);
    u8Data=u8Data|0x10;
    bRet&=WriteReg(u16Address, u8Data);

#if 1       //For Unicable command timing,mick
    u16WaitCount=0;
    do
    {
        u16Address=0x0990;
        bRet&=ReadReg(u16Address, &u8Data);
         MsOS_DelayTask(1);
         u16WaitCount++;
    }while(((u8Data&0x10)==0x10)&&(u16WaitCount < MSB1240_DEMOD_WAIT_TIMEOUT)) ;

    if (u16WaitCount >= MSB1240_DEMOD_WAIT_TIMEOUT)
    {
        printf(("MSB1240 DVBS DiSEqC Send Command Busy!!!\n"));
        return MAPI_FALSE;
    }
#endif      //For Unicable command timing,mick

    printf(("\r\n MDrv_DiSEqC_SendCmd----"));
    return bRet;
}

//BLIND SCAN
MAPI_BOOL device_demodulator_extend::MSB1240_Demod_BlindScan_Start(MAPI_U16 u16StartFreq,MAPI_U16 u16EndFreq)
{
    MAPI_BOOL bRet=TRUE;
    MAPI_U16 u16Address = 0;
    MAPI_U8 u8Data = 0;

    DBG_DEMOD_MSB(printf("MSB1240_Demod_BlindScan_Start+\n"));
    _u16BlindScanStartFreq=u16StartFreq;
    _u16BlindScanEndFreq=u16EndFreq;
    _u16TunerCenterFreq=0;
    _u16ChannelInfoIndex=0;
    u16Address=MSB1240_TOP_WR_DBG_90_ADDR;
    bRet&=ReadReg(u16Address, &u8Data);
    u8Data&=0xF0;
    bRet&=WriteReg(u16Address, u8Data);
    u16Address=MSB1240_TOP_WR_DBG_92_ADDR;
    bRet&=WriteReg2bytes(u16Address, _u16BlindScanStartFreq);
    DBG_DEMOD_MSB(printf("MSB1240_Demod_BlindScan_Start- _u16BlindScanStartFreq%d u16StartFreq %d u16EndFreq %d\n", _u16BlindScanStartFreq, u16StartFreq, u16EndFreq));
  //  MsOS_ReleaseMutex(_s32FunMutexId);
    return bRet;
}
MAPI_BOOL device_demodulator_extend::MSB1240_Demod_BlindScan_NextFreq(MAPI_BOOL* bBlindScanEnd)
{
    MAPI_BOOL bRet=TRUE;
    MAPI_U16 u16Address = 0;
    MAPI_U8   u8Data = 0;
    MAPI_U32  u32TunerCutOffFreq = 0;
    MAPI_U16 u16WaitCount = 0;
    MAPI_U16 u16LockCount = 0;
/*
    if (MsOS_ObtainMutex(_s32FunMutexId, MSB124X_MUTEX_TIMEOUT)==FALSE)
    {
        DBG_DEMOD_MSB(printf("%s function mutex timeout\n", __FUNCTION__));
        return FALSE;
    }*/
    DBG_DEMOD_MSB(printf("MSB1240_Demod_BlindScan_NextFreq+\n"));
    u16Address=MSB1240_TOP_WR_DBG_90_ADDR;
    bRet&=ReadReg(u16Address, &u8Data);
    if ((u8Data&0x02)==0x02)
    {
            u8Data|=0x08;
            bRet&=WriteReg(u16Address, u8Data);
            u16WaitCount=0;
            do
            {
                u16Address=MSB1240_DIG_DBG_5_ADDR;
                bRet&=ReadReg(u16Address, &u8Data);
                u16WaitCount++;
                DBG_DEMOD_MSB(printf("MSB1240_Demod_BlindScan_NextFreq u8Data:0x%x u16WaitCount:%d\n", u8Data, u16WaitCount));
                MsOS_DelayTask(1);
            }while((u8Data!=0x01)&&(u16WaitCount<MSB124X_DEMOD_WAIT_TIMEOUT));
     }
    u16WaitCount=0;

    * bBlindScanEnd=FALSE;
    _u16TunerCenterFreq=0;
    u16Address=MSB1240_TOP_WR_DBG_93_ADDR;
    bRet&=ReadReg(u16Address, &u8Data);
    _u16TunerCenterFreq=u8Data;
    u16Address=MSB1240_TOP_WR_DBG_92_ADDR;
    bRet&=ReadReg(u16Address, &u8Data);
    _u16TunerCenterFreq=(_u16TunerCenterFreq<<8)|u8Data;

    if (_u16TunerCenterFreq >=_u16BlindScanEndFreq)
    {
        DBG_DEMOD_MSB(printf("MSB1240_Demod_BlindScan_NextFreq . _u16TunerCenterFreq %d _u16BlindScanEndFreq%d\n", _u16TunerCenterFreq, _u16BlindScanEndFreq));
        * bBlindScanEnd=TRUE;

        return bRet;
    }

    u32TunerCutOffFreq=44000;//34000

    IIC_Bypass_Mode(TRUE);

// configure tunerr start

    bRet&=MDrv_DVBS_Tuner_SetFreq(_u16TunerCenterFreq, u32TunerCutOffFreq);   
    if (bRet==MAPI_TRUE)
    {     
        u16LockCount=0;
        do
        {
            bRet=MAPI_TRUE;
            bRet&=MDrv_DVBS_Tuner_CheckLock();
            MsOS_DelayTask(1);
            u16LockCount++;
        }while((bRet==MAPI_FALSE) && (u16LockCount<MSB1240_DVBS_TUNER_WAIT_TIMEOUT)) ;
        if (bRet==MAPI_TRUE)
        {
            printf(("Tuner PLL Lock\n"));
        }
        else
        {
            printf(("Tuner PLL Unlock\n"));
        }
    }
   IIC_Bypass_Mode(FALSE);
//configure tuner end
    MsOS_DelayTask(10);
    u16Address=MSB1240_TOP_WR_DBG_90_ADDR;
    bRet&=ReadReg(u16Address, &u8Data);
    if ((u8Data&0x02)==0x00)
    {
        u8Data&=~(0x08);
        bRet&=WriteReg(u16Address, u8Data);
        u8Data|=0x02;
        bRet&=WriteReg(u16Address, u8Data);
        u8Data|=0x01;
        bRet&=WriteReg(u16Address, u8Data);
    }
    else
    {
        u8Data&=~(0x08);
        bRet&=WriteReg(u16Address, u8Data);
    }
    DBG_DEMOD_MSB(printf("MSB1240_Demod_BlindScan_NextFreq _u16TunerCenterFreq:%d-\n", _u16TunerCenterFreq));

    return bRet;
}
MAPI_BOOL device_demodulator_extend::MSB1240_Demod_BlindScan_WaitCurFreqFinished(MAPI_U8* u8Progress,MAPI_U8 *u8FindNum)
{
    MAPI_BOOL bRet=TRUE;
    MAPI_U16 u16Address = 0;
    MAPI_U32  u32Data = 0;
    MAPI_U16 u16Data = 0;
    MAPI_U8   u8Data = 0;
    MAPI_U16  u16WaitCount = 0;

    DBG_DEMOD_MSB(printf("MSB1240_Demod_BlindScan_WaitCurFreqFinished+\n"));
    u16WaitCount=0;
    *u8FindNum=0;
    *u8Progress=0;
    do
    {
        u16Address=MSB1240_DIG_DBG_5_ADDR; // "DIG_DBG_5" means state
        bRet&=ReadReg(u16Address, &u8Data);
        u16WaitCount++;
        DBG_DEMOD_MSB(printf("MSB1240_Demod_BlindScan_WaitCurFreqFinished+1 u8Data:0x%x u16WaitCount:%d\n", u8Data, u16WaitCount));
        MsOS_DelayTask(1);
    }while((u8Data!=17)&&(u16WaitCount<MSB124X_DEMOD_WAIT_TIMEOUT));//#define     DVBS_BLIND_SCAN      17



    if (u16WaitCount>=MSB124X_DEMOD_WAIT_TIMEOUT)
    {
        DBG_DEMOD_MSB(printf("\r\n ===>>> WaitCurFreqFinished return False!\n "));
        bRet=FALSE;
    }
    else
    {
        u16Address=MSB1240_DIG_DBG_6_ADDR;// "DIG_DBG_6" means sub_state
        bRet&=ReadReg(u16Address, &u8Data);
        if (u8Data==0)
        {

            //Center Freq -- mail box version
            bRet&=ReadDspReg(E_S2_MB_SWUSE13L,&u8Data);
            u32Data=u8Data;
            bRet&=ReadDspReg(E_S2_MB_SWUSE12H,&u8Data);
            u32Data = (u32Data<<8)|u8Data;
            bRet&=ReadDspReg(E_S2_MB_SWUSE12L,&u8Data);
            u32Data = (u32Data<<8)|u8Data;
            _u16LockedCenterFreq=((u32Data+500)/1000);
            _u16ChannelInfoArray[0][_u16ChannelInfoIndex]=_u16LockedCenterFreq; //Center Freq



            //symbol rate -- mail box version
            bRet&=ReadDspReg(E_S2_MB_SWUSE14H,&u8Data);
            u16Data = u8Data;
            bRet &= ReadDspReg(E_S2_MB_SWUSE14L,&u8Data);
            u16Data = (u16Data<<8)|u8Data;
            _u16LockedSymbolRate = u16Data;
            _u16ChannelInfoArray[1][_u16ChannelInfoIndex]=_u16LockedSymbolRate;//Symbol Rate
            _u16ChannelInfoIndex++;
            *u8FindNum = _u16ChannelInfoIndex;

            //current lock cfo -- mail box version
            bRet&=ReadDspReg(E_S2_MB_SWUSE15H,&u8Data);
            u16Data = u8Data;
            bRet&=ReadDspReg(E_S2_MB_SWUSE15L,&u8Data);
            u16Data = (u16Data<<8)|u8Data;
            if(u16Data*1000 >= 0x8000)
            {
              u16Data = 0x10000 - (u16Data*1000);
              _s16CurrentCFO = -1*u16Data/1000;
            }
            else
            {
              _s16CurrentCFO = u16Data;
            }

            //current step size  -- mail box version
            bRet&=ReadDspReg(E_S2_MB_SWUSE16H,&u8Data);
            u16Data = u8Data;
            bRet&=ReadDspReg(E_S2_MB_SWUSE16L,&u8Data);
            u16Data = (u16Data<<8)|u8Data;
            _u16CurrentStepSize=u16Data;

            //prelock HB  -- mail box version
            bRet&=ReadDspReg(E_S2_MB_SWUSE18H,&u8Data);
            u16Data=u8Data;
            bRet&=ReadDspReg(E_S2_MB_SWUSE18L,&u8Data);
            u16Data = (u16Data<<8)|u8Data;
            _u16PreLockedHB=u16Data;

            //prelock LB  -- mail box version
            bRet&=ReadDspReg(E_S2_MB_SWUSE19H,&u8Data);
            u16Data=u8Data;
            bRet&=ReadDspReg(E_S2_MB_SWUSE19L,&u8Data);
            u16Data = (u16Data<<8)|u8Data;
            _u16PreLockedLB=u16Data;

            printf("Current Locked CF:%d BW:%d BWH:%d BWL:%d CFO:%d Step:%d\n", _u16LockedCenterFreq, _u16LockedSymbolRate,_u16PreLockedHB, _u16PreLockedLB, _s16CurrentCFO, _u16CurrentStepSize);


        }
        else if (u8Data==1)
        {
            u16Address=MSB1240_TOP_WR_DBG_93_ADDR;
            bRet&=ReadReg(u16Address, &u8Data);
            u16Data=u8Data;
            u16Address=MSB1240_TOP_WR_DBG_92_ADDR;
            bRet&=ReadReg(u16Address, &u8Data);
            u16Data=(u16Data<<8)|u8Data;
            _u16NextCenterFreq=u16Data;


            //_u16CurrentSymbolRate -- mail box
            bRet&=ReadDspReg(E_S2_MB_SWUSE14H,&u8Data);
            u16Data = u8Data;
            bRet &= ReadDspReg(E_S2_MB_SWUSE14L,&u8Data);
            u16Data = (u16Data<<8)|u8Data;
            _u16CurrentSymbolRate = u16Data;


            // cfo -- mail box
            bRet&=ReadDspReg(E_S2_MB_SWUSE15H,&u8Data);
            u16Data = u8Data;
            bRet&=ReadDspReg(E_S2_MB_SWUSE15L,&u8Data);
            u16Data = (u16Data<<8)|u8Data;
            if(u16Data*1000 >= 0x8000)
            {
                u16Data = 0x10000 - (u16Data*1000);
                _s16CurrentCFO = -1*u16Data/1000;
            }
            else
            {
                _s16CurrentCFO = u16Data;
            }

            // current step size -- mail box
            bRet&=ReadDspReg(E_S2_MB_SWUSE16H,&u8Data);
            u16Data = u8Data;
            bRet&=ReadDspReg(E_S2_MB_SWUSE16L,&u8Data);
            u16Data = (u16Data<<8)|u8Data;
            _u16CurrentStepSize=u16Data;


            //prelock HB  -- mail box version
            bRet&=ReadDspReg(E_S2_MB_SWUSE12H,&u8Data);
            u16Data=u8Data;
            bRet&=ReadDspReg(E_S2_MB_SWUSE12L,&u8Data);
            u16Data = (u16Data<<8)|u8Data;
            _u16PreLockedHB=u16Data;

            //prelock LB  -- mail box version
            bRet&=ReadDspReg(E_S2_MB_SWUSE13H,&u8Data);
            u16Data=u8Data;
            bRet&=ReadDspReg(E_S2_MB_SWUSE13L,&u8Data);
            u16Data = (u16Data<<8)|u8Data;
            _u16PreLockedLB=u16Data;

            printf("Pre Locked CF:%d BW:%d HBW:%d LBW:%d Current CF:%d BW:%d CFO:%d Step:%d\n", _u16LockedCenterFreq, _u16LockedSymbolRate,_u16PreLockedHB, _u16PreLockedLB,  _u16NextCenterFreq-_u16CurrentStepSize, _u16CurrentSymbolRate, _s16CurrentCFO, _u16CurrentStepSize);
            //DBG_DEMOD_MSB(printf("Pre Locked CF:%d BW:%d HBW:%d LBW:%d Current CF:%d BW:%d CFO:%d Step:%d\n", _u16LockedCenterFreq, _u16LockedSymbolRate,_u16PreLockedHB, _u16PreLockedLB,  _u16NextCenterFreq-_u16CurrentStepSize, _u16CurrentSymbolRate, _s16CurrentCFO, _u16CurrentStepSize));
        }
    }
    *u8Progress=100;
    DBG_DEMOD_MSB(printf("MSB1240_Demod_BlindScan_WaitCurFreqFinished u8Progress%d u8FindNum %d-\n", *u8Progress, *u8FindNum));

    return bRet;
}
MAPI_BOOL device_demodulator_extend::MSB1240_Demod_BlindScan_Cancel(void)
{
    MAPI_BOOL bRet=TRUE;
    MAPI_U16 u16Address = 0;
    MAPI_U8   u8Data = 0;
    MAPI_U16 u16Data = 0;

    DBG_DEMOD_MSB(printf("MSB1240_Demod_BlindScan_Cancel+\n"));
    u16Address=MSB1240_TOP_WR_DBG_90_ADDR;
    bRet&=ReadReg(u16Address, &u8Data);
    u8Data&=0xF0;
    bRet&=WriteReg(u16Address, u8Data);
    u16Address=MSB1240_TOP_WR_DBG_92_ADDR;
    u16Data=0x0000;
    bRet&=WriteReg2bytes(u16Address, u16Data);
    _u16TunerCenterFreq=0;
    _u16ChannelInfoIndex=0;
    DBG_DEMOD_MSB(printf("MSB1240_Demod_BlindScan_Cancel-\n"));

    return bRet;
}
MAPI_BOOL device_demodulator_extend::MSB1240_Demod_BlindScan_End(void)
{
    MAPI_BOOL bRet=TRUE;
    MAPI_U16 u16Address = 0;
    MAPI_U8   u8Data = 0;
    MAPI_U16 u16Data = 0;

    DBG_DEMOD_MSB(printf("MSB1240_Demod_BlindScan_End+\n"));
    u16Address=MSB1240_TOP_WR_DBG_90_ADDR;
    bRet&=ReadReg(u16Address, &u8Data);
    u8Data&=0xF0;
    bRet&=WriteReg(u16Address, u8Data);
    u16Address=MSB1240_TOP_WR_DBG_92_ADDR;
    u16Data=0x0000;
    bRet&=WriteReg2bytes(u16Address, u16Data);
    _u16TunerCenterFreq=0;
    _u16ChannelInfoIndex=0;
    DBG_DEMOD_MSB(printf("MSB1240_Demod_BlindScan_End-\n"));

    return bRet;
}
MAPI_BOOL MSB1240_Demod_BlindScan_GetChannel(MAPI_U16 u16ReadStart,MAPI_U16* u16TPNum,DEMOD_MS_FE_CARRIER_PARAM *pTable)
{
    MAPI_BOOL bRet=TRUE;
    MAPI_U16  u16TableIndex;
    *u16TPNum=_u16ChannelInfoIndex-u16ReadStart;
    for(u16TableIndex = 0; u16TableIndex < (*u16TPNum); u16TableIndex++)
    {
       pTable[u16TableIndex].u32Frequency = _u16ChannelInfoArray[0][_u16ChannelInfoIndex-1];
       pTable[u16TableIndex].SatParam.u32SymbolRate= _u16ChannelInfoArray[1][_u16ChannelInfoIndex-1];
       DBG_DEMOD_MSB(printf("MSB1240_Demod_BlindScan_GetChannel Freq:%u SymbolRate:%u\n", pTable[u16TableIndex].u32Frequency, pTable[u16TableIndex].SatParam.u32SymbolRate));
    }
    DBG_DEMOD_MSB(printf("MS1240 u16TPNum:%d\n", *u16TPNum));

    return bRet;
}
MAPI_BOOL device_demodulator_extend::MSB1240_Demod_BlindScan_GetCurrentFreq(MAPI_U16 *u16CurrentFeq)
{
    MAPI_BOOL bRet=TRUE;
    *u16CurrentFeq=_u16TunerCenterFreq;
    return bRet;
}
//BLIND SCAN
MAPI_BOOL device_demodulator_extend::DTV_DVB_S_BlindScan_Init(MAPI_U16 u16StartFreq,MAPI_U16 u16EndFreq)
{
    MAPI_BOOL bRet;
    _u32CurrentSR = 0;
    eBlindScanStatue = mapi_demodulator_datatype::E_BLINDSCAN_NOTREADY;
    bRet=MSB1240_Demod_BlindScan_Start(u16StartFreq,u16EndFreq);
    if(bRet)
       eBlindScanStatue = mapi_demodulator_datatype::E_BLINDSCAN_INIT_OK;
    else
       eBlindScanStatue = mapi_demodulator_datatype::E_BLINDSCAN_INIT_FAILED;
          

    return bRet;
}

MAPI_BOOL device_demodulator_extend::DTV_DVB_S_BlindScan_ScanNextFreq(void)
{
    MAPI_BOOL bBlindScanEnd;
    MAPI_BOOL bRet;
    

    bRet = MSB1240_Demod_BlindScan_NextFreq(&bBlindScanEnd);
    if(bRet)
    {
      if(bBlindScanEnd)
        eBlindScanStatue = mapi_demodulator_datatype::E_BLINDSCAN_ALLFREQ_COMPLETE;
      else
        eBlindScanStatue = mapi_demodulator_datatype::E_BLINDSCAN_SCANFREQ_SCANNING;
    }
    else
    {
      eBlindScanStatue = mapi_demodulator_datatype::E_BLINDSCAN_SCANFREQ_START_FAILED;
    }
    printf("DTV_DVB_S_BlindScan_ScanNextFreq , %d, %d \n", bRet,eBlindScanStatue );


    return bRet;
}

MAPI_BOOL device_demodulator_extend::DTV_DVB_S_BlindScan_GetFoundTP(MAPI_U8 u8No, MAPI_U16 &u16Freq,MAPI_U16 &u16SymbolRate)
{
    MAPI_U16 u16Num = 1;
    DEMOD_MS_FE_CARRIER_PARAM stChannel;
    MAPI_BOOL bRet = MAPI_TRUE;

    memset(&stChannel, 0, sizeof(DEMOD_MS_FE_CARRIER_PARAM));


    if(MSB1240_Demod_BlindScan_GetChannel(u8No,&u16Num,&stChannel) != MAPI_TRUE)
    {
      u16Freq = 0;
      u16SymbolRate = 0;
      bRet = MAPI_FALSE;
    }
    else
    {
      if(u16Num == 0)
      {
        u16Freq = 0;
        u16SymbolRate = 0;
        bRet = MAPI_FALSE;
      }
      else
      {
        u16Freq = stChannel.u32Frequency;//1000;
        u16SymbolRate = stChannel.SatParam.u32SymbolRate;// /1000;
      }
    }    

    return bRet;
}

MAPI_BOOL device_demodulator_extend::DTV_DVB_S_BlindScan_Cancel(void)
{
      MAPI_BOOL bRet;
      bRet = MSB1240_Demod_BlindScan_Cancel();      
      return bRet;
}

MAPI_BOOL device_demodulator_extend::DTV_DVB_S_BlindScan_End(void)
{

    if((eBlindScanStatue == mapi_demodulator_datatype::E_BLINDSCAN_SCANFREQ_SCANNING))
    {
      if(MSB1240_Demod_BlindScan_Cancel() != MAPI_TRUE)
      {

        return MAPI_FALSE;
      }
    }

    if(MSB1240_Demod_BlindScan_End() != MAPI_TRUE)
    {

      return MAPI_FALSE;
    }

    eBlindScanStatue = mapi_demodulator_datatype::E_BLINDSCAN_NOTREADY;

    return MAPI_TRUE;
}

MAPI_BOOL device_demodulator_extend::DTV_DVB_S_BlindScan_GetStatus(mapi_demodulator_datatype::EN_BLINDSCAN_STATUS &eStatus)
{
    MAPI_U8 u8Progress,u8ChannelNum;
    MAPI_BOOL bRet = MAPI_TRUE;


    if(eBlindScanStatue == mapi_demodulator_datatype::E_BLINDSCAN_SCANFREQ_SCANNING)
    {
      if(MSB1240_Demod_BlindScan_WaitCurFreqFinished(&u8Progress,&u8ChannelNum) != MAPI_TRUE)
      {
        bRet = MAPI_FALSE;
      }
      else
      {
        if(u8Progress == 100)
        {
          eBlindScanStatue = mapi_demodulator_datatype::E_BLINDSCAN_SCANFREQ_COMPLETE;
        }
      }
    }
    eStatus = eBlindScanStatue;

    return bRet;
}

MAPI_BOOL device_demodulator_extend::DTV_DVB_S_BlindScan_GetScanFreq(MAPI_U16 &u16Freq)
{
      MAPI_BOOL bRet;

      bRet = MSB1240_Demod_BlindScan_GetCurrentFreq(&u16Freq);

      return bRet;
}
#endif
static float Log10Approx(float flt_x)
{
    MAPI_U32 u32_temp = 1;
    MAPI_U8 indx = 0;
    do
    {
      u32_temp = u32_temp << 1;
      if (flt_x < (float)u32_temp)
        break;
    }
    while (++indx < 32);
    // 10*log10(X) ~= 0.3*N, when X ~= 2^N
    return (float)0.3 *indx;
}


mapi_demodulator_datatype::EN_FRONTEND_SIGNAL_CONDITION device_demodulator_extend::DTV_GetSNR(void)
{
    MAPI_U8   status = MAPI_TRUE;
    float   f_snr = (float)0.0;

    mapi_demodulator_datatype::EN_FRONTEND_SIGNAL_CONDITION eSignalCondition;
    // Coverity issue: 113565: UNINIT
    eSignalCondition = mapi_demodulator_datatype::E_FE_SIGNAL_NO;

    switch (device_demodulator_extend::m_enCurrentDemodulator_Type)
    {
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2:
            {
                if(FECLock == FALSE) f_snr = (float)0.0;
                else
                {
                    MAPI_U16 u16_snr100 = 0;
                    MAPI_U8  u8_reg = 0;
                    MAPI_U8  u8_freeze = 0;
                    MAPI_U8  u8_win = 0;
                    MAPI_U8  u8_gi = 0;                    

                    // freeze                 
                    status &= ReadReg(0x0b00+0x28*2+1, &u8_freeze);
                    status &= WriteReg(0x0b00+0x28*2+1, u8_freeze|0x80);

                    status &= ReadDspReg((MAPI_U16)E_T2_SNR_H,&u8_reg);
                    u16_snr100 = u8_reg;
                    status &= ReadDspReg((MAPI_U16)E_T2_SNR_L,&u8_reg);
                    u16_snr100 = (u16_snr100<<8)|u8_reg;

                    // unfreeze
                    status &= WriteReg(0x0b00+0x28*2+1, u8_freeze);
                    
                    f_snr = (float)u16_snr100/100.0;
                    
                    // snr cali
                    status &= ReadReg(0x2500+0x01*2, &u8_reg);
                    u8_win = (u8_reg>>2)&0x01;

                    if (u8_win == 1)
                    {
                        float snr_offset = 0.0;
                        float snr_cali = 0.0;

                        u8_gi = DTV_DVB_T_GetSignalGuardInterval();

                        if (u8_gi == 0) snr_offset = 0.157;
                        else if(u8_gi == 1) snr_offset = 0.317;
                        else if(u8_gi == 2) snr_offset = 0.645;
                        else if(u8_gi == 3) snr_offset = 1.335;
                        else if(u8_gi == 4) snr_offset = 0.039;
                        else if(u8_gi == 5) snr_offset = 0.771;                        
                        else if(u8_gi == 6) snr_offset = 0.378;

                        snr_cali = f_snr - snr_offset;
                        if (snr_cali > 0.0) f_snr = snr_cali;
                    }                                  
                }

                g_msb1240_fSNR = f_snr;

                if (f_snr>25)
                eSignalCondition = mapi_demodulator_datatype::E_FE_SIGNAL_STRONG;
                else if (f_snr>20)
                eSignalCondition = mapi_demodulator_datatype::E_FE_SIGNAL_MODERATE;
                else if (f_snr>15)
                eSignalCondition = mapi_demodulator_datatype::E_FE_SIGNAL_WEAK;
                else
                eSignalCondition = mapi_demodulator_datatype::E_FE_SIGNAL_NO;
            }
            break;

        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
            {
                if(FECLock == FALSE) f_snr = (float)0.0;
                else
                {

                    MAPI_U16 u16_snr100 = 0;
                    MAPI_U8  u8_reg = 0;
                    MAPI_U8  u8_freeze = 0;
                          
                    // freeze                 
                    status &= ReadReg(DIG_DBG_BASE+0x29*2, &u8_freeze);
                    status &= WriteReg(DIG_DBG_BASE+0x29*2, u8_freeze|0x01);

                    status &= ReadDspReg((MAPI_U16)T_SNR100_H,&u8_reg);
                    u16_snr100 = u8_reg;
                    status &= ReadDspReg((MAPI_U16)T_SNR100_L,&u8_reg);
                    u16_snr100 = (u16_snr100<<8)|u8_reg;

                    // unfreeze
                    status &= WriteReg(DIG_DBG_BASE+0x29*2, u8_freeze);
                    
                    f_snr = (float)u16_snr100/100.0;
                    
                }

                g_msb1240_fSNR = f_snr;

                
                if (f_snr>25)
                eSignalCondition = mapi_demodulator_datatype::E_FE_SIGNAL_STRONG;
                else if (f_snr>20)
                eSignalCondition = mapi_demodulator_datatype::E_FE_SIGNAL_MODERATE;
                else if (f_snr>15)
                eSignalCondition = mapi_demodulator_datatype::E_FE_SIGNAL_WEAK;
                else
                eSignalCondition = mapi_demodulator_datatype::E_FE_SIGNAL_NO;

            }
            break;

#if (DVBC_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_C:
            {
                MAPI_U8 u8Data = 0, reg_frz = 0;
                MAPI_U16 u16_snr100 = 0;

                if(FECLock == FALSE)
                {
                    f_snr = (float)0.0;
                }
                else
                {
                    //int QAM_Mode;
                    
                    //QAM_Mode = DTV_DVB_C_GetSignalModulation();//0:16q,1:32q,2:64q,3:128q,4:256q
                    
                    // freeze
                    status &= ReadReg(DIG_DBG_BASE+0x52, &reg_frz);
                    status &= WriteReg(DIG_DBG_BASE+0x52, reg_frz|0x01);

                    status &= ReadDspReg((MAPI_U16)C_SNR100_H,&u8Data);
                    u16_snr100 = u8Data;
                    status &= ReadDspReg((MAPI_U16)C_SNR100_L,&u8Data);
                    u16_snr100 = (u16_snr100<<8)|u8Data;
                    
                    // unfreeze
                    reg_frz=reg_frz&(~0x01);
                    status &= WriteReg(DIG_DBG_BASE+0x52, reg_frz);

                    f_snr = (float)u16_snr100/100.0;

                    //printf("f_snr=%f,",f_snr);
#if 0
                    if (QAM_Mode == 0) //16qam
                    f_snr -= 0.5;
                    else if (QAM_Mode == 2) //64qam
                    f_snr -= 0.2;
#endif
                    if (f_snr < 0)
                    {
                        f_snr = 0.0;
                    }
                }

                g_msb1240_fSNR = f_snr;

                if (f_snr>25)
                eSignalCondition = mapi_demodulator_datatype::E_FE_SIGNAL_STRONG;
                else if (f_snr>20)
                eSignalCondition = mapi_demodulator_datatype::E_FE_SIGNAL_MODERATE;
                else if (f_snr>15)
                eSignalCondition = mapi_demodulator_datatype::E_FE_SIGNAL_WEAK;
                else
                eSignalCondition = mapi_demodulator_datatype::E_FE_SIGNAL_NO;
            }
            break;
#endif
#if (DVBS_SYSTEM_ENABLE == 1)       
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_S:
            {
#if 0
                MAPI_U8 u8_reg = 0;
                MAPI_U16 u16_snr10 = 0;
                status &= ReadDspReg((MAPI_U16)E_S2_10SNR_H,&u8_reg);
                u16_snr10 = u8_reg;
                status &= ReadDspReg((MAPI_U16)E_S2_10SNR_L,&u8_reg);
                u16_snr10 = (u16_snr10<<8)|u8_reg;
                f_snr = (float)u16_snr10/10.0;
#else
                MAPI_BOOL bRet=MAPI_TRUE;
                MAPI_U16 u16Address =0;
                MAPI_U8  u8Data =0;
                //NDA SNR
                MAPI_U32 u32NDA_SNR_A =0;
                MAPI_U32 u32NDA_SNR_AB =0;
                
                //NDA SNR
                float NDA_SNR_A =0.0;
                float NDA_SNR_AB =0.0;
                // float NDA_SNR =0.0;
                
                // u16Address=0x1B08;
                // bRet&=ReadReg(u16Address, &u8Data);
                // u8Data|=0x10;
                // bRet&=WriteReg(u16Address, u8Data);
                //NDA SNR
                u16Address=0x1B8E;
                bRet&=ReadReg(u16Address, &u8Data);
                u32NDA_SNR_A=(u8Data&0x03);
                u16Address=0x1B8D;
                bRet&=ReadReg(u16Address, &u8Data);
                u32NDA_SNR_A=(u32NDA_SNR_A<<8)|u8Data;
                u16Address=0x1B8C;
                bRet&=ReadReg(u16Address, &u8Data);
                u32NDA_SNR_A=(u32NDA_SNR_A<<8)|u8Data;      
                //printf("u32NDA SNR A = %lu\n",u32NDA_SNR_A);
                
                u16Address=0x1B92;
                bRet&=ReadReg(u16Address, &u8Data);
                u32NDA_SNR_AB=(u8Data&0x3F);
                u16Address=0x1B91;
                bRet&=ReadReg(u16Address, &u8Data);
                u32NDA_SNR_AB=(u32NDA_SNR_AB<<8)|u8Data;
                u16Address=0x1B90;
                bRet&=ReadReg(u16Address, &u8Data);
                u32NDA_SNR_AB=(u32NDA_SNR_AB<<8)|u8Data;    
                //printf("u32NDA SNR AB = %lu\n",u32NDA_SNR_AB);
                
                // u16Address=0x1B08;
                // bRet&=ReadReg(u16Address, &u8Data);
                // u8Data&=~(0x10);
                // bRet&=WriteReg(u16Address, u8Data);
                
                if (bRet==MAPI_FALSE)
                {
                    printf(("MSB1240_DVBS_GetSignalNoiseRatio fail!!! \n"));
                }
                //NDA SNR
                if ((u32NDA_SNR_A==0) || (u32NDA_SNR_AB==0) || (bRet==MAPI_FALSE) )
                {
                    f_snr = 0.0;
                }
                else
                {
                    NDA_SNR_A=(float)u32NDA_SNR_A/65536;
                    NDA_SNR_AB=(float)u32NDA_SNR_AB/4194304;
                    
                    NDA_SNR_AB=(float)sqrt(NDA_SNR_AB);
                    if ((NDA_SNR_A/NDA_SNR_AB) > 1.0)
                    {
                        f_snr=10.0*log10((double)(1/((NDA_SNR_A/NDA_SNR_AB)-1)));
                    }
                    else
                    {
                        f_snr = 0.0; 
                    }
                }
#endif
                //f_snr = 6.0;
                g_msb1240_fSNR = f_snr;
                
                if (f_snr>12)
                    eSignalCondition = mapi_demodulator_datatype::E_FE_SIGNAL_STRONG;
                else if (f_snr>8)
                    eSignalCondition = mapi_demodulator_datatype::E_FE_SIGNAL_MODERATE;
                else if (f_snr>4)
                    eSignalCondition = mapi_demodulator_datatype::E_FE_SIGNAL_WEAK;
                else
                    eSignalCondition = mapi_demodulator_datatype::E_FE_SIGNAL_NO;

            }
            break;
#endif            
        default:
            f_snr = 0.0;
            eSignalCondition = mapi_demodulator_datatype::E_FE_SIGNAL_NO;
            break;
    }
    DBG_GET_SIGNAL(printf(">>> SNR = %d <<<\n", (int)g_msb1240_fSNR));
    return eSignalCondition;
}

MAPI_BOOL device_demodulator_extend::DTV_GetPreBER(float *p_preBer)
{
    MAPI_U16 BitErrPeriod = 0;
    MAPI_U32 BitErr = 0;
    MAPI_U8  reg = 0;
    MAPI_U16 FecType = 0;
    MAPI_U8  status = MAPI_TRUE;
    float    fber = (float)0.0;

    if (FECLock== TRUE)
    {
       switch(device_demodulator_extend::m_enCurrentDemodulator_Type)
       {
           case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2:
           {
               /////////// Data BER /////////////
               // bank 38 0x02 [0] freeze
               status &= WriteReg(0x2604, 0x01);  // avoid confliction

               // bank 38 0x12 Data BER Window[15:0]
               status &= ReadReg(0x2625, &reg);
               BitErrPeriod = reg;
               status &= ReadReg(0x2624, &reg);
               BitErrPeriod = (BitErrPeriod << 8) | reg;

               // bank 38 0x32 Data BER count[15:0]
               // bank 38 0x33 Data BER count[31:16]
               status &= ReadReg(0x2667, &reg);
               BitErr = reg;
               status &= ReadReg(0x2666, &reg);
               BitErr = (BitErr << 8) | reg;
               status &= ReadReg(0x2665, &reg);
               BitErr = (BitErr << 8) | reg;
               status &= ReadReg(0x2664, &reg);
               BitErr = (BitErr << 8) | reg;

               // bank 38 0x02 [0] freeze
               status &= WriteReg(0x2604, 0x00);   // avoid confliction
               if (BitErrPeriod == 0)
               //protect 0
                   BitErrPeriod = 1;

               status &= ReadReg(0x278f, &reg); //FEC Type[8:7]
               FecType = reg;
               status &= ReadReg(0x278e, &reg); //FEC Type[8:7]
               FecType = (FecType << 8) | reg;
               if (FecType&0x0180)
               {
                   if (BitErr == 0)
                       fber = (float)0.5/(float)(BitErrPeriod*64800);
                   else
                       fber = (float)BitErr/(float)(BitErrPeriod*64800);
               }
               else
               {
                   if (BitErr == 0)
                       fber = (float)0.5/(float)(BitErrPeriod*16200);
                   else
                       fber = (float)BitErr/(float)(BitErrPeriod*16200);
               }

               *p_preBer = fber;
               DBG_DEMOD_MSB(printf("MSB1240 Extend Data Pre BER = %8.3e \n ", fber));
           }
           break;

           case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
           {
               /////////// Pre-Viterbi BER /////////////
               bool BEROver;
               MAPI_U8 freeze_data = 0;

               // bank 0x1c 0x08 [3] reg_rd_freezeber
               status &= ReadReg(0x1c10, &freeze_data);
               freeze_data=freeze_data|0x08;
               status &= WriteReg(0x1c10, freeze_data);

               usleep(10);

               // bank 0x1c 0x08 [3] reg_rd_freezeber
               freeze_data=freeze_data&(~0x08);
               status &= WriteReg(0x1c10, freeze_data);

               // bank 0x1c 0x0b [7:0] reg_ber_timerl
               //			   [15:8] reg_ber_timerm
               // bank 0x1c 0x0c [5:0] reg_ber_timerh
               status &= ReadReg(0x1c16, &reg);
               BitErrPeriod=reg;
               status &= ReadReg(0x1c17, &reg);
               BitErrPeriod=(reg<<8)|BitErrPeriod;
               status &= ReadReg(0x1c18, &reg);
               BitErrPeriod=((reg&0x3f)<<16)|BitErrPeriod;

               // bank 0x1c 0x0f [7:0] reg_ber_7_0
               //			   [15:8] reg_ber_15_8
               status &= ReadReg(0x1c1e, &reg);
               BitErr=reg;
               status &= ReadReg(0x1c1f, &reg);
               BitErr=(reg<<8)|BitErr;
               // bank 0x11 0x0d [13:8] reg_cor_intstat_reg
               status &= ReadReg(0x1c1b, &reg);

               if (reg & 0x10) 
                   BEROver = true;
               else 
                   BEROver = false;

               if (BitErrPeriod ==0 )//protect 0
                   BitErrPeriod=1;
               if (BitErr <=0 )
                   fber=(float)0.5 /(float)(BitErrPeriod*256);
               else
                   fber=(float)BitErr/(float)(BitErrPeriod*256);

               if (BEROver == false)
                   *p_preBer = fber;
               else 
                   *p_preBer =1;

               DBG_DEMOD_MSB(printf("MSB1240 Extend Data Pre BER = %8.3e \n ", fber));
           }
           break;

           case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_C:
           {
               *p_preBer = (float)0.0;
           }
           break;

           default:
               *p_preBer = (float)1.0;
                status = MAPI_FALSE;
       }
    }
    else
    {
        *p_preBer = (float)1.0;
        status = MAPI_FALSE;
    }
    return status;
}


MAPI_BOOL device_demodulator_extend::DTV_GetPostBER(float *p_postBer)
{
    MAPI_U16 BitErrPeriod = 0;
    MAPI_U32 BitErr = 0;
    MAPI_U8  reg = 0;
    float    fber = 0;
    MAPI_U8  status = MAPI_TRUE;
    MAPI_U16  FecType = 0;

    if (FECLock == TRUE)
    {
        switch(device_demodulator_extend::m_enCurrentDemodulator_Type)
        {
           case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2:
           {
               /////////// Data BER /////////////
               // bank 38 0x02 [0] freeze
               status &= WriteReg(0x2604, 0x01);  // avoid confliction

               // bank 38 0x12 Data BER Window[15:0]
               status &= ReadReg(0x2625, &reg);
               BitErrPeriod = reg;
               status &= ReadReg(0x2624, &reg);
               BitErrPeriod = (BitErrPeriod << 8) | reg;

               // bank 38 0x34 Data BER count[15:0]
               // bank 38 0x35 Data BER count[31:16]
               status &= ReadReg(0x2600+(0x34<<1)+3, &reg);
               BitErr = reg;
               status &= ReadReg(0x2600+(0x34<<1)+2, &reg);
               BitErr = (BitErr << 8) | reg;
               status &= ReadReg(0x2600+(0x34<<1)+1, &reg);
               BitErr = (BitErr << 8) | reg;
               status &= ReadReg(0x2600+(0x34<<1)+0, &reg);
               BitErr = (BitErr << 8) | reg;

               // bank 38 0x02 [0] freeze
               status &= WriteReg(0x2604, 0x00);   // avoid confliction

               if (BitErrPeriod == 0)
               //protect 0
                   BitErrPeriod = 1;

               status &= ReadReg(0x278f, &reg); //FEC Type[8:7]
               FecType = reg;
               status &= ReadReg(0x278e, &reg); //FEC Type[8:7]
               FecType = (FecType << 8) | reg;

               if (FecType&0x0180)
               {
                   if (BitErr == 0)
                       fber = (float)0.5/(float)(BitErrPeriod*64800);
                   else
                       fber = (float)BitErr/(float)(BitErrPeriod*64800);
               }
               else
               {
                   if (BitErr == 0)
                       fber = (float)0.5/(float)(BitErrPeriod*16200);
                   else
                       fber = (float)BitErr/(float)(BitErrPeriod*16200);
               }
               *p_postBer = fber;
               DBG_DEMOD_MSB(printf("MSB1240 Extend Data Post BER = %8.3e \n ", fber));
           }
           break;
           case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
           {
               MAPI_U8 reg_frz=0;

               status &= ReadReg(0x0B51, &reg);
               if ( (reg&0x80) == 0x00 )
               {
                   /////////// Post-Viterbi BER /////////////
                   // bank 0x11 0x19 [7] reg_bit_err_num_freeze
                   status &= ReadReg(0x1132, & reg_frz);
                   reg_frz=reg_frz|0x80;
                   status &= WriteReg(0x1132, reg_frz);

                   // bank 0x11 0x18 [7:0] reg_bit_err_sblprd_7_0
                   //                           [15:8] reg_bit_err_sblprd_15_8
                   status &= ReadReg(0x1131, &reg);
                   BitErrPeriod = reg;
                   status &= ReadReg(0x1130, &reg);
                   BitErrPeriod = (BitErrPeriod << 8)|reg;

                   // bank 0x11 0x1d [7:0] reg_bit_err_num_7_0
                   //                           [15:8] reg_bit_err_num_15_8
                   // bank 0x11 0x1e [7:0] reg_bit_err_num_23_16
                   //                           [15:8] reg_bit_err_num_31_24
                   status &= ReadReg(0x113D, &reg);
                   BitErr = reg;
                   status &= ReadReg(0x113C, &reg);
                   BitErr = (BitErr << 8)|reg;
                   status &= ReadReg(0x113B, &reg);
                   BitErr = (BitErr << 8)|reg;
                   status &= ReadReg(0x113A, &reg);
                   BitErr = (BitErr << 8)|reg;

                   // bank 0x11 0x19 [7] reg_bit_err_num_freeze
                   reg_frz=reg_frz&(~0x80);
                   status &= WriteReg(0x1132, reg_frz);

                   if (BitErrPeriod ==0 )//protect 0
                   {
                       BitErrPeriod=1;
                   }

                   if (BitErr <=0 )
                   {
                       fber =0.5f / (double)(BitErrPeriod*128*188*8);
                   }
                   else
                   {
                       fber = (double)(BitErr) / (double)(BitErrPeriod*128*188*8);
                   }

                   g_previous_ber = fber;
                   g_min_ber = 0.6f / ((float)BitErrPeriod*128*188*8);
                   *p_postBer = fber;
               }
               else
               {
                   *p_postBer = g_previous_ber;
               }
               DBG_DEMOD_MSB(printf("MSB1240[T] Extend Data Post BER = %8.3e \n ", fber));
           }
           break;
           case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_C:
           {
               MAPI_U8 reg_frz=0;

               /////////// Post-Viterbi BER /////////////
               // bank 0x11 0x19 [7] reg_bit_err_num_freeze
               status &= ReadReg(0x1132, & reg_frz);
               reg_frz=reg_frz|0x80;
               status &= WriteReg(0x1132, reg_frz);

               // bank 0x11 0x18 [7:0] reg_bit_err_sblprd_7_0
               // [15:8] reg_bit_err_sblprd_15_8
               status &= ReadReg(0x1131, &reg);
               BitErrPeriod = reg;
               status &= ReadReg(0x1130, &reg);
               BitErrPeriod = (BitErrPeriod << 8)|reg;

               // bank 0x11 0x1d [7:0] reg_bit_err_num_7_0
               // [15:8] reg_bit_err_num_15_8
               // bank 0x11 0x1e [7:0] reg_bit_err_num_23_16
               // [15:8] reg_bit_err_num_31_24
               status &= ReadReg(0x113D, &reg);
               BitErr = reg;
               status &= ReadReg(0x113C, &reg);
               BitErr = (BitErr << 8)|reg;
               status &= ReadReg(0x113B, &reg);
               BitErr = (BitErr << 8)|reg;
               status &= ReadReg(0x113A, &reg);
               BitErr = (BitErr << 8)|reg;

               // bank 0x11 0x19 [7] reg_bit_err_num_freeze
               reg_frz=reg_frz&(~0x80);
               status &= WriteReg(0x1132, reg_frz);

               if (BitErrPeriod ==0 )//protect 0
                   BitErrPeriod=1;

               if (BitErr <=0 )
               {
                   fber =0.5f / (double)(BitErrPeriod*128*188*8);
               }
               else
               {
                   fber = (double)(BitErr) / (double)(BitErrPeriod*128*188*8);
               }
               *p_postBer = fber;
               DBG_DEMOD_MSB(printf("MSB1240[C] Extend Data Post BER = %8.3e \n ", fber));
           }
           break;
#if (DVBS_SYSTEM_ENABLE == 1)           
           case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_S:
           {
               if (_bDemodType==MAPI_FALSE)//S
               {
                   MAPI_U8 reg_frz=0;

                   /////////// Post-Viterbi BER /////////////
                   // bank 0x11 0x19 [7] reg_bit_err_num_freeze
                   status &= ReadReg(0x1132, & reg_frz);
                   reg_frz=reg_frz|0x80;
                   status &= WriteReg(0x1132, reg_frz);

                   // bank 0x11 0x18 [7:0] reg_bit_err_sblprd_7_0
                   // [15:8] reg_bit_err_sblprd_15_8
                   status &= ReadReg(0x1131, &reg);
                   BitErrPeriod = reg;
                   status &= ReadReg(0x1130, &reg);
                   BitErrPeriod = (BitErrPeriod << 8)|reg;

                   // bank 0x11 0x1d [7:0] reg_bit_err_num_7_0
                   // [15:8] reg_bit_err_num_15_8
                   // bank 0x11 0x1e [7:0] reg_bit_err_num_23_16
                   // [15:8] reg_bit_err_num_31_24
                   status &= ReadReg(0x113D, &reg);
                   BitErr = reg;
                   status &= ReadReg(0x113C, &reg);
                   BitErr = (BitErr << 8)|reg;
                   status &= ReadReg(0x113B, &reg);
                   BitErr = (BitErr << 8)|reg;
                   status &= ReadReg(0x113A, &reg);
                   BitErr = (BitErr << 8)|reg;

                   // bank 0x11 0x19 [7] reg_bit_err_num_freeze
                   reg_frz=reg_frz&(~0x80);
                   status &= WriteReg(0x1132, reg_frz);

                   if (BitErrPeriod ==0 )//protect 0
                       BitErrPeriod=1;

                   if (BitErr <=0 )
                   {
                       fber =0.5f / double(BitErrPeriod*128*188*8);
                   }
                   else
                   {
                       fber = double(BitErr) / double(BitErrPeriod*128*188*8);
                   }
                   *p_postBer = fber;
                   DBG_DEMOD_MSB(printf("MSB1240[S] Extend Data Post BER = %8.3e \n ", fber));
               }
               else
               { // S2
                   /////////// Data BER /////////////
                   // bank 38 0x02 [0] freeze
                   // status &= WriteReg(0x2604, 0x01);  // avoid confliction

                   // bank 38 0x12 Data BER Window[15:0]
                   status &= ReadReg(0x2600+(25<<1)+1, &reg);
                   BitErrPeriod = reg;
                   status &= ReadReg(0x2600+(25<<1), &reg);
                   BitErrPeriod = (BitErrPeriod << 8) | reg;

                   // bank 38 0x34 Data BER count[15:0]
                   // bank 38 0x35 Data BER count[31:16]
                   // status &= ReadReg(0x2600+(0x26<<1)+3, &reg);
                   // BitErr = reg;
                   // status &= ReadReg(0x2600+(0x26<<1)+2, &reg);
                   // BitErr = (BitErr << 8) | reg;
                   status &= ReadReg(0x2600+(0x26<<1)+1, &reg);
                   BitErr = reg;
                   status &= ReadReg(0x2600+(0x26<<1)+0, &reg);
                   BitErr = (BitErr << 8) | reg;

                   // bank 38 0x02 [0] freeze
                   // status &= WriteReg(0x2604, 0x00);   // avoid confliction

                   if (BitErrPeriod == 0)
                   //protect 0
                       BitErrPeriod = 1;

                   if (BitErr == 0)
                       fber = (float)0.0001/(float)(BitErrPeriod*4);
                   else
                       fber = (float)BitErr/(float)(BitErrPeriod*4);

                   *p_postBer = fber;
                   DBG_DEMOD_MSB(printf("MSB1240[S2] Extend Data Post BER = %8.3e \n ", fber));
               }
           }
           break;
#endif           
           default:
                fber   = (float)1.0;
                status = MAPI_FALSE;

        }
    }
    else
    {
        fber = (float)1.0;
        status = MAPI_FALSE;
    }
    return status;
}

MAPI_U32 device_demodulator_extend::DTV_GetBER(void)
{
     float    fber =  (float)0.0;

//    if (DTV_GetPreBER(&fber) == MAPI_FALSE)
//        fber =  (float)0.0;

    return (MAPI_U32)fber;
}

MAPI_BOOL device_demodulator_extend::DTV_GetPacketErr(MAPI_U16 *pu16BitErr)
{
    MAPI_BOOL status = MAPI_TRUE;
    MAPI_U8   reg = 0, reg_frz;
    MAPI_U16  PktErr=0;

    mapi_scope_lock(scopeLock, &m_MutexOuter);
    if (FECLock == TRUE)
    {
        if (device_demodulator_extend::m_enCurrentDemodulator_Type == mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2)
        {
            //freeze
            status &= WriteReg(0x2604, 0x01);
            
            //read packet error
            status &= ReadReg(0x265B, &reg);
            PktErr = reg;
            status &= ReadReg(0x265A, &reg);
            PktErr = (PktErr << 8) | reg;

            //release
            status &= WriteReg(0x2604, 0x00);
            
            *pu16BitErr = PktErr;

            DBG_DEMOD_MSB(printf("===================>MSB1240 T2 PktErr = %d \n ", (int)PktErr));
        }
#if (DVBS_SYSTEM_ENABLE == 1)
        else if (device_demodulator_extend::m_enCurrentDemodulator_Type == mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_S)
        {
            if (_bDemodType==MAPI_FALSE)//S
            {
                // status &= ReadReg(0x1132, &reg);
                // reg|=0x80;
                // status &= WriteReg(0x1132, reg); //freeze
                
                // bank 17 0x1F [7:0] reg_uncrt_pkt_num_7_0 [15:8] reg_uncrt_pkt_num_15_8
                status &= ReadReg(0x113F, &reg);
                PktErr = reg;
                status &= ReadReg(0x113E, &reg);
                PktErr= (PktErr<<8)| reg; //Packet error                
                
                // status &= ReadReg(0x1132, &reg);
                // reg&=(~0x80);
                // status &= WriteReg(0x1132, reg); //unfreeze               

                *pu16BitErr = PktErr;
                DBG_DEMOD_MSB(printf("===================>MSB1240 S PktErr = %d \n ", (int)PktErr));
            }
            else //s2
            {
                // status &= ReadReg(0x2604, &reg);
                // reg|=0x01;
                // status &= WriteReg(0x2604, reg); //freeze
                
                status &= ReadReg(0x264D, &reg);
                PktErr= reg;
                status &= ReadReg(0x264C, &reg);
                PktErr= (PktErr<<8)| reg;    //E-flag, error
                
                // status &= ReadReg(0x2604, &reg);
                // reg&=(~0x01);
                // status &= WriteReg(0x2604, reg); //unfreeze
                
                *pu16BitErr = PktErr;
                DBG_DEMOD_MSB(printf("===================>MSB1240 S2 PktErr = %d \n ", (int)PktErr));
            }           
        }
#endif
        else
        {
            reg_frz = 0;
            // bank 0x11 0x19 [7] reg_bit_err_num_freeze
            status &= ReadReg(0x1132, &reg_frz);
            status &= WriteReg(0x1132, reg_frz | 0x80);

            // bank 0x11 0x1f [7:0] reg_uncrt_pkt_num_7_0
            //         [15:8] reg_uncrt_pkt_num_15_8
            status &= ReadReg(0x113F, &reg);
            PktErr = reg;
            status &= ReadReg(0x113E, &reg);
            PktErr = (PktErr << 8) | reg;

            // bank 0x11 0x19 [7] reg_bit_err_num_freeze
            status &= WriteReg(0x1132, reg_frz);
            
            *pu16BitErr = PktErr;
            
            DBG_DEMOD_MSB(printf("===================>MSB1240 T/C PktErr = %d \n ", (int)PktErr));
        }
    }
    else
        *pu16BitErr = 65535;

    if (status)
    {
        DBG_DEMOD_MSB(printf(" @MSB1240_Get_Packet_Error OK\n"));
        return  MAPI_TRUE;
    }
    else
    {
        DBG_DEMOD_MSB(printf(" @MSB1240_Get_Packet_Error NG\n"));
        return MAPI_FALSE;
    }
}

MAPI_BOOL device_demodulator_extend::DTV_GetIFAGCGain(MAPI_U16 *pu16ifagc)
{
    MAPI_U8 tmp = 0;
    MAPI_U16 if_agc_gain = 0;

    // select IF gain to read
    ReadReg(0x3E00+(0x13)*2, &tmp);
    WriteReg(0x3E00+(0x13)*2, (tmp&0xF0)|0x03);

    //freeze
    ReadReg(0x3E00+(0x02)*2+1, &tmp);
    WriteReg(0x3E00+(0x02)*2+1, tmp|0x80);

    //read value
    ReadReg(0x3E00+(0x14)*2+1, &tmp);
    if_agc_gain = tmp;
    ReadReg(0x3E00+(0x14)*2, &tmp);
    if_agc_gain = (if_agc_gain<<8)|tmp;

    //unfreeze
    ReadReg(0x3E00+(0x02)*2+1, &tmp);
    WriteReg(0x3E00+(0x02)*2+1, tmp&0x7F);

    *pu16ifagc = if_agc_gain;

    return  MAPI_TRUE;
}

MAPI_BOOL device_demodulator_extend::DTV_DVB_T2_Get_L1_Parameter(MAPI_U16 * pu16L1_parameter, E_T2_SIGNAL_INFO eSignalInfo)
{
    MAPI_U8     status = TRUE;
    MAPI_U8     u8Data = 0;
    MAPI_U16    FecType = 0;

    if (FECLock == TRUE)
    {
        if (eSignalInfo == T2_MODUL_MODE)
        {
            if (ReadReg(0x2700 + (0x47 * 2), &u8Data) == FALSE)
                return FALSE;

            *pu16L1_parameter  = (((MAPI_U16) u8Data) & (BIT5 | BIT4 | BIT3)) >> 3;
        }
        else  if (eSignalInfo == T2_CODE_RATE)
        {
            if (ReadReg(0x2700 + (0x47 * 2), &u8Data) == FALSE)
            {
                return FALSE;
            }
            *pu16L1_parameter  = (((MAPI_U16) u8Data) & (BIT2 | BIT1 | BIT0));
        }
        else if (eSignalInfo == T2_PREAMBLE)
        {
            if (ReadReg(0x2700 + (0x30 * 2) + 1, &u8Data) == FALSE)
            {
                return FALSE;
            }
            *pu16L1_parameter  = (((MAPI_U16) u8Data) & (BIT4)) >> 4;
        }
        else if (eSignalInfo == T2_S1_SIGNALLING)
        {
            if (ReadReg(0x2700 + (0x30 * 2) + 1, &u8Data) == FALSE)
            {
                return FALSE;
            }
            *pu16L1_parameter  = (((MAPI_U16) u8Data) & (BIT3 | BIT2 | BIT1)) >> 1;
        }
        else if (eSignalInfo == T2_PILOT_PATTERN)
        {
            if (ReadReg(0x2700 + (0x36 * 2), &u8Data) == FALSE)
            {
                return FALSE;
            }
            *pu16L1_parameter  = (((MAPI_U16) u8Data) & 0x0F);
        }
        else if (eSignalInfo == T2_BW_EXT)
        {
            if (ReadReg(0x2700 + (0x30 * 2) + 1, &u8Data) == FALSE)
            {
                return FALSE;
            }
            *pu16L1_parameter  = (((MAPI_U16) u8Data) & (BIT0));
        }
        else if (eSignalInfo == T2_PAPR_REDUCTION)
        {
            if (ReadReg(0x2700 + (0x31 * 2), &u8Data) == FALSE)
            {
                return FALSE;
            }
            *pu16L1_parameter  = (((MAPI_U16) u8Data) & 0xF0) >> 4;
        }
        else if (eSignalInfo == T2_OFDM_SYMBOLS_PER_FRAME)
        {
            if (ReadReg(0x2700 + (0x3C * 2), &u8Data) == FALSE)
            {
                return FALSE;
            }
            *pu16L1_parameter  = (MAPI_U16) u8Data;
            if (ReadReg(0x2700 + (0x3C * 2) + 1, &u8Data) == FALSE)
            {
                return FALSE;
            }
            *pu16L1_parameter |= (((MAPI_U16) u8Data) & 0x0F) << 8;
        }
        else if (eSignalInfo == T2_FFT_VALUE)
        {
            if (ReadReg(0x2700 + (0x30 * 2) + 1, &u8Data) == FALSE)
            {
                return FALSE;
            }
            if ((u8Data & (BIT3 | BIT2)) >> 2)
            {
                return FALSE;
            }
            *pu16L1_parameter  = (((MAPI_U16) u8Data) & (BIT7 | BIT6 | BIT5)) >> 5;
        }
        else if (eSignalInfo == T2_GUARD_INTERVAL)
        {
            if (ReadReg(0x2700 + (0x31 * 2), &u8Data) == FALSE)
            {
                return FALSE;
            }
            *pu16L1_parameter  = (((MAPI_U16) u8Data) & (BIT3 | BIT2 | BIT1)) >> 1;
        }
        else if (eSignalInfo == T2_PLP_ROTATION)
        {
            if (ReadReg(0x2700 + (0x47 * 2), &u8Data) == FALSE)
            {
                return FALSE;
            }
            *pu16L1_parameter  = (((MAPI_U16) u8Data) & BIT6) >> 6;
        }
        else if (eSignalInfo == T2_PLP_FEC_TYPE)
        {
            status &= ReadReg(0x278f, &u8Data);    //FEC Type[8:7]
            FecType = u8Data;
            status &= ReadReg(0x278e, &u8Data);    //FEC Type[8:7]
            FecType = (FecType << 8) | u8Data;

            *pu16L1_parameter = (FecType & 0x0180) >> 7;
        }
        else  if (eSignalInfo == T2_NUM_PLP)
        {
            if (ReadReg(0x2700 + (0x42 * 2), &u8Data) == FALSE)
            {
                return FALSE;
            }
            *pu16L1_parameter  = (MAPI_U16)u8Data;          
        }
        else
        {
            return FALSE;
        }
        DBG_DEMOD_MSB(printf(" Get T2 parameter %d, value = 0x%x\n", (MAPI_U16)eSignalInfo, *pu16L1_parameter));
        return status;

    }
    return FALSE;
}

MAPI_BOOL device_demodulator_extend::DTV_DVB_T_Get_TPS_Parameter(MAPI_U16 * pu16TPS_parameter, E_SIGNAL_TYPE eSignalType)
{
    MAPI_U8         u8Data = 0;

//    if(device_demodulator_extend::DTV_DVB_T_GetLockStatus() == mapi_demodulator_datatype::E_DEMOD_LOCK)
    if (FECLock == TRUE)
    {

        if(eSignalType == TS_MODUL_MODE)
        {
            if(ReadReg(0x0F00 + (0x12 * 2), &u8Data) == FALSE) 
            {
                // 0:QPSK, 1:16qam, 2:64qam
                return FALSE;
            }
            *pu16TPS_parameter  = (((MAPI_U16) u8Data) & ( MBIT1| MBIT0)) ;

        }

        else  if(eSignalType == TS_CODE_RATE)
        {
            if(ReadReg(0x0F00 + (0x12 * 2) + 1, &u8Data) == FALSE) //0: 1/2; 1:2/3 ; 2:3/4; 3: 5/6; 4: 7/8	
            {
                return FALSE;
            }
            *pu16TPS_parameter  = (((MAPI_U16) u8Data) & (MBIT6 | MBIT5 | MBIT4))>>4;

        }

        else if(eSignalType == TS_GUARD_INTERVAL)
        {
            if(ReadReg(0x0F00 + (0x01 * 2) + 1, &u8Data) == FALSE)
            {
                return FALSE;
            }
            *pu16TPS_parameter = (((MAPI_U16) u8Data) & (MBIT1 | MBIT0)) ;

        }

        else if(eSignalType == TS_FFT_VALUE)
        {
            if(ReadReg(0x0E00 + (0x30 * 2) , &u8Data) == FALSE) // 0:2k,1:8k
            {
                return FALSE;
            }
            *pu16TPS_parameter  = (((MAPI_U16) u8Data) & (MBIT0));

        }
        else
        {
            return FALSE;
        }
        DBG_DEMOD_MSB(printf(" Get T/C parameter %d, value = 0x%x\n", (MAPI_U16)eSignalType, *pu16TPS_parameter));
        return TRUE;

    }
    return FALSE;
}

mapi_demodulator_datatype::EN_DVBT_CONSTEL_TYPE device_demodulator_extend::DTV_DVB_T_GetSignalModulation(void)
{
    MAPI_U16    u16Modulation = 0;

    switch(device_demodulator_extend::m_enCurrentDemodulator_Type)
    {
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2:
        {
            if(DTV_DVB_T2_Get_L1_Parameter(&u16Modulation, T2_MODUL_MODE) == FALSE)
            {
                printf("TPS parameter can not be read.\n");
            }
            break;
        }

        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
        {
            if(DTV_DVB_T_Get_TPS_Parameter(&u16Modulation, TS_MODUL_MODE) == FALSE)
            {
                printf("TPS parameter can not be read.\n");
            }
            break;
        }

        default:
        {
            break;
        }
    }  // end of switch

    DBG_DEMOD_MSB(printf("MSB1240 Extend Data Constellation Type = %d \n ", u16Modulation));

    return (mapi_demodulator_datatype::EN_DVBT_CONSTEL_TYPE)u16Modulation;
}


mapi_demodulator_datatype::EN_DVBT_GUARD_INTERVAL device_demodulator_extend::DTV_DVB_T_GetSignalGuardInterval(void)
{
    MAPI_U16    u16GuardInterval = 0;

    switch(device_demodulator_extend::m_enCurrentDemodulator_Type)
    {
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2:
        {
            if(DTV_DVB_T2_Get_L1_Parameter(&u16GuardInterval, T2_GUARD_INTERVAL) == FALSE)
            {
                printf("TPS parameter can not be read.\n");
            }
            break;
        }

        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
        {
            if(DTV_DVB_T_Get_TPS_Parameter(&u16GuardInterval, TS_GUARD_INTERVAL) == FALSE)
            {
                printf("TPS parameter can not be read.\n");
            }
            break;
        }

        default:
        {
            break;
        }
    }  // end of switch

    DBG_DEMOD_MSB(printf("MSB1240 Extend Data Guard Interval = %d \n ", u16GuardInterval));
    return (mapi_demodulator_datatype::EN_DVBT_GUARD_INTERVAL)u16GuardInterval;

}

mapi_demodulator_datatype::EN_DVBT_FFT_VAL device_demodulator_extend::DTV_DVB_T_GetSignalFFTValue(void)
{
    MAPI_U16    u16FFTValue = 0;

    switch(device_demodulator_extend::m_enCurrentDemodulator_Type)
    {
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2:
        {
            if(DTV_DVB_T2_Get_L1_Parameter(&u16FFTValue, T2_FFT_VALUE) == FALSE)
            {
                printf("TPS parameter can not be read.\n");
            }
            break;
        }

        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
        {
            if(DTV_DVB_T_Get_TPS_Parameter(&u16FFTValue, TS_FFT_VALUE) == FALSE)
            {
                printf("TPS parameter can not be read.\n");
            }
            break;
        }

        default:
        {
            break;
        }
    }  // end of switch

    DBG_DEMOD_MSB(printf("MSB1240 Extend Data FFT Value = %d \n ", u16FFTValue));
    return (mapi_demodulator_datatype::EN_DVBT_FFT_VAL)u16FFTValue;

}

mapi_demodulator_datatype::EN_DVBT_CODE_RATE device_demodulator_extend::DTV_DVB_T_GetSignalCodeRate(void)
{
    MAPI_U16    u16CodeRate = 0;

    switch(device_demodulator_extend::m_enCurrentDemodulator_Type)
    {
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2:
        {
            if(DTV_DVB_T2_Get_L1_Parameter(&u16CodeRate, T2_CODE_RATE) == FALSE)
            {
                printf("TPS parameter can not be read.\n");
            }
            break;
        }

        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
        {
            if(DTV_DVB_T_Get_TPS_Parameter(&u16CodeRate, TS_CODE_RATE) == FALSE)
            {
                printf("TPS parameter can not be read.\n");
            }
            break;
        }

        default:
        {
            break;
        }
    }  // end of switch

    DBG_DEMOD_MSB(printf("MSB1240 Extend Data Code Rate = %d \n ", u16CodeRate));
    return (mapi_demodulator_datatype::EN_DVBT_CODE_RATE)u16CodeRate;
}


MAPI_U16 device_demodulator_extend::DTV_GetSignalQuality(void)
{
#define DVBT2_BER_TH_HY 0.1
    MAPI_U8     status = true;
    MAPI_U8     reg = 0; //, reg_frz;
    MAPI_U8     u8_index = 0;
    float       fber = (float)0.0;
    float       SQI = (float)0.0;
    float       BER_SQI = (float)0.0;
    float       cn_ref = (float)0.0;
    float       cn_rec = (float)0.0;
    float       cn_rel = (float)0.0;
    static MAPI_U8 u8State = 0;
    float fBerTH1[] = {1E-4, 1E-4*(1.0-DVBT2_BER_TH_HY), 1E-4*(1.0+DVBT2_BER_TH_HY), 1E-4};
    float fBerTH2[] = {3E-7, 3E-7, 3E-7*(1.0-DVBT2_BER_TH_HY), 3E-7*(1.0+DVBT2_BER_TH_HY)};
    static float fBerFiltered = -1.0;
    MAPI_U16    u16_sqi = 0;

    mapi_scope_lock(scopeLock, &m_MutexOuter);
    switch(device_demodulator_extend::m_enCurrentDemodulator_Type)
    {
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2:
        {
            E_T2_CONSTEL  constel =  _T2_QAM_UNKNOWN;
            E_T2_CODERATE coderate = _T2_CR_UNKNOWN;

            /////////// Check lock status /////////////
            status &= ReadDspReg(0x00f0, &reg);
            if((reg & MBIT7) == 0x00)
            {
                return 0;
            }

           status &= DTV_GetPostBER(&fber); // get BER

            if(status == FALSE)
            {
                //DBG_DEMOD_MSB(printf("DTV_GetSignalQuality_DVBT2_GetPostViterbiBer Fail!\n"));
                return 0;
            }

            if ((fBerFiltered <= 0.0) || ((fBerFiltered/fber) > 30.0 || (fBerFiltered/fber) < 0.03))
                fBerFiltered = fber;
            else
                fBerFiltered = 0.9f*fBerFiltered+0.1f*fber;
            fber = fBerFiltered;

            if (fber > fBerTH1[u8State])
            {
               BER_SQI = 0.0;
               u8State = 1;
            }
            else if (fber >=fBerTH2[u8State])
            {
               BER_SQI = 100.0/15;
               u8State = 2;
            }
            else 
            {
               BER_SQI = 100.0/6;
               u8State = 3;    
            }

            coderate = (E_T2_CODERATE)DTV_DVB_T_GetSignalCodeRate();
            constel = (E_T2_CONSTEL)DTV_DVB_T_GetSignalModulation();

            DTV_GetSNR();
            cn_rec = g_msb1240_fSNR;

            cn_ref = (float)-1.0;
            while(g_msb1240_dvbt2_sqi_cn_nordigp1[u8_index].constel != _T2_QAM_UNKNOWN)
            {
                if ((g_msb1240_dvbt2_sqi_cn_nordigp1[u8_index].constel == constel)
                    && (g_msb1240_dvbt2_sqi_cn_nordigp1[u8_index].code_rate == coderate))
                {
                   cn_ref = g_msb1240_dvbt2_sqi_cn_nordigp1[u8_index].cn_ref;
                   break;
                }
                else
                {
                   u8_index++;
                }
            }
            if (cn_ref == -1.0)
                SQI = (float)0.0;
            else
            {
                // 0.7, snr offset
                cn_rel = cn_rec - cn_ref + 0.7f;
                if (cn_rel > 3.0)
                    SQI = 100;
                else if (cn_rel >= -3)
                {
                    SQI = (cn_rel+3)*BER_SQI;
                    if (SQI > 100.0) SQI = 100.0;
                    else if (SQI < 0.0) SQI = 0.0;
                }
                else
                    SQI = (float)0.0;
            }

            u16_sqi = (MAPI_U16)SQI;
            DBG_DEMOD_MSB(printf("[msb1240]signalquality, coderate=%d, constel=%d,cn_rec=%f, cn_ref=%f, BER_SQI=%f, SQI=%f, ber=%8.3e\n",coderate,constel,cn_rec,cn_ref,BER_SQI,SQI, fber));
            DBG_GET_SIGNAL(printf(">>> [T2]SQI = %d <<<\n", u16_sqi));            

            return u16_sqi;            
        }
        break;
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
        {
            float g_msb1240_dvbt_sqi_cn_nordigp1[][5] =
            {
                {5.1, 6.9, 7.9,8.9, 9.7},
                {10.8, 13.1, 14.6, 15.6,16.0},
                {16.5,18.7, 20.2, 21.6,22.5},
            };   
            float   cn_nordig_p1 = 0;
            float   max_fber_th = (float)0.0;
            MAPI_U8   tps_info_qam,tps_info_cr;
            MAPI_U8   tbva_bypass = 0;

            status &= DTV_GetPostBER(&fber);//get DVBT post ber
            if (status == MAPI_FALSE)
            {
                ERR_DEMOD_MSB(printf("[T]DTV_GetPostBER Error, return FALSE\n");)
                return 0;
            }
            status &= ReadReg(0x1100, &tbva_bypass);

            if((tbva_bypass&0x10) == 0x00)
            {
                max_fber_th = 2.0E-2; //tbva enhance the CN
            }
            else
            {
                max_fber_th = 1.0E-3;
            }

            if (fber > max_fber_th)
            {
               BER_SQI = 0.0;
            }
            else if (fber > 1.0E-7)
            {
               BER_SQI = 20.0f*(Log10Approx(1.0f/fber)) - 22.0f;
            }
            else
            {
               BER_SQI = 100.0;
            }

            if (fber < g_min_ber)
            {
               BER_SQI = 100.0;
            }

            if( BER_SQI >= 100.0)
            {
                BER_SQI = 100.0;
            }
            else if( BER_SQI <= 0.0 )
            {
                BER_SQI = 0.0;
            }

            DTV_GetSNR();
            cn_rec = g_msb1240_fSNR;
        
            ///////// Get Constellation and Code Rate to determine Ref. C/N //////////
            ///////// (refer to Teracom min. spec 2.0 4.1.1.7) /////
            tps_info_qam=DTV_DVB_T_GetSignalModulation();// 0:QPSK, 1:16qam, 2:64qam
            tps_info_cr=DTV_DVB_T_GetSignalCodeRate();//0: 1/2; 1:2/3 ; 2:3/4; 3: 5/6; 4: 7/8

            if (tps_info_qam < 3 && tps_info_cr < 5)
                cn_nordig_p1 =  g_msb1240_dvbt_sqi_cn_nordigp1[tps_info_qam][tps_info_cr];

            // 0,5, snr offset
            cn_rel = cn_rec - cn_nordig_p1 + 0.5f;

            if (cn_rel < -7.0f)
            {
               SQI = 0.0;
            }
            else if (cn_rel < 3.0)
            {
               SQI = BER_SQI*((cn_rel - 3.0)/10.0 + 1.0);
            }
            else
            {
               SQI = BER_SQI;
            }

            if(SQI>=100.0)
            {
                SQI=100.0;
            }
            else if( (SQI<=0.0) || (cn_rec == 0.0))
            {
                SQI=0.0;
            }
            else
            {
                SQI=SQI;
            }

            // patch....
            // Noridg SQI,
            // 64QAM, CR34, GI14
            if( (tps_info_qam== 2) && (tps_info_cr == 2) && (cn_rec <= 16.1))
            {
                SQI = 0.0;
            }

            u16_sqi = (MAPI_U16)SQI;

            DBG_DEMOD_MSB(printf("[msb1240]signalquality, coderate=%d, constel=%d,cn_rec=%f, cn_nordig_p1=%f, BER_SQI=%f, SQI=%f, ber=%8.3e\n",tps_info_cr,tps_info_qam,cn_rec,cn_nordig_p1,BER_SQI,SQI, fber));
            DBG_GET_SIGNAL(printf(">>> [T]SQI = %d <<<\n", u16_sqi));
            return u16_sqi;            
        }
        break;
#if (DVBC_SYSTEM_ENABLE == 1)        
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_C:
        {
            float   f_snr = 0;
            MAPI_U8   Qam_mode;

            DTV_GetPostBER(&fber);//get DVBT post ber

            DTV_GetSNR();
            f_snr = g_msb1240_fSNR;

            Qam_mode = DTV_DVB_C_GetSignalModulation();//0:16q,1:32q,2:64q,3:128q,4:256q
            
#if(D_CUSTOMER == formula_A)
            float   ber_sqi = 0.0;
            if (fber > 1.0E-3)
            {
                ber_sqi = 0.0;
            }
            else if(fber > g_min_ber)
            {
                ber_sqi = (Log10Approx(1.0f/fber))*20.0f - 40.0f;
            }
            else 
            {
                ber_sqi = 100.0;
            }

            if( (MAPI_U8)Qam_mode <= 4)//DMD_DVBC_QAM256
            {
                cn_rel = f_snr - cn_nordig_p1_DVBC[(MAPI_U8)Qam_mode];
            }
            else
            {
                cn_rel = -100.0f;
            }

            if (cn_rel < -7.0f)
            { 
                SQI = 0;
            }
            else if (cn_rel < 3.0)
            {
                SQI = (MAPI_U16)(ber_sqi*((cn_rel - 3.0)/10.0 + 1.0));
            }
            else
            {
                SQI = (MAPI_U16)ber_sqi;
            }
            
#elif(D_CUSTOMER == formula_B)
            if (Qam_mode == 0)//DMD_DVBC_QAM16
            {
                if (f_snr <= (14.2 + 0.6)) SQI = 0;
                else if (f_snr <= (15.7+0.3)) SQI = 20;
                else if (f_snr <= (16.6+0.2)) SQI = 30;
                else if (f_snr <= (17.3+0.1)) SQI = 40;
                else if (f_snr <= 18.0) SQI = 50;
                else if (f_snr <= 18.9) SQI = 60;
                else if (f_snr <= (20.1-0.1)) SQI = 70;
                // else if (f_snr <= 20.3) SQI = 80;
                else if (f_snr <= (20.4-0.2)) SQI = 80;
                else if (f_snr <= (20.6-0.2)) SQI = 90;
                else SQI = 100;             
            }
            else if (Qam_mode == 1)//DMD_DVBC_QAM32
            {
                if (f_snr <= 17.2) SQI = 0;
                else if (f_snr <= 18.7) SQI = 20;
                else if (f_snr <= 19.6) SQI = 30;
                else if (f_snr <= 20.3) SQI = 40;
                else if (f_snr <= 21.0) SQI = 50;
                else if (f_snr <= 21.9) SQI = 60;
                else if (f_snr <= 23.1) SQI = 70;
                else if (f_snr <= 23.3) SQI = 80;
                else if (f_snr <= 23.4) SQI = 90;
                else if (f_snr <= 23.6) SQI = 100;
                else SQI = 100;
            }
            else if (Qam_mode == 2)//DMD_DVBC_QAM64
            {
                if (f_snr <= 19.5) SQI = 0;
                else if (f_snr <= 20.0) SQI = 10;
                else if (f_snr <= (21.6 + 0.1)) SQI = 20;            
                else if (f_snr <= 22.5) SQI= 30;
                else if (f_snr <= 23.3) SQI = 40;
                else if (f_snr <= 24.1) SQI = 50;
                else if (f_snr <= 25.0) SQI = 60;
                else if (f_snr <= (26.3-0.1)) SQI = 70;
                // else if (f_snr <= (26.5-0.1)) SQI = 80;
                else if (f_snr <= (26.6-0.2)) SQI = 80;
                else if (f_snr <= (26.9 -0.2)) SQI = 90;
                else SQI = 100;
            }
            else if (Qam_mode == 3)//DMD_DVBC_QAM128
            {
                if (f_snr <= (22.0 + 0.4)) SQI = 0;
                else if (f_snr <= 22.5) SQI = 10;
                else if (f_snr <= (24.4+0.2)) SQI = 20;
                else if (f_snr <= (25.3+0.1)) SQI = 30;
                else if (f_snr <= (26.2+0.1)) SQI = 40;
                else if (f_snr <= (27.0+0.1)) SQI = 50;
                else if (f_snr <= (28.0+0.1)) SQI = 60;
                else if (f_snr <= (29.2+0.2)) SQI = 70;
                // else if (f_snr <= (29.5+0.1)) SQI = 80;
                else if (f_snr <= 29.7) SQI = 80;
                // else if (f_snr <= 29.8) SQI = 90;
                else if (f_snr <= 29.9) SQI = 90;
                else SQI = 100;
            }
            else //256QAM
            {
                if (f_snr <= 25.4) SQI = 0;
                else if (f_snr <= 27.6) SQI = 20;
                else if (f_snr <= 28.5) SQI = 30;
                else if (f_snr <= 29.4) SQI = 40;
                else if (f_snr <= 30.3) SQI = 50;
                else if (f_snr <= 31.3) SQI = 60;
                else if (f_snr <= 32.8) SQI = 70;
                else if (f_snr <= 33.1) SQI = 80;
                else if (f_snr <= 33.3) SQI = 90;
                // else if (f_snr <= 33.4) SQI = 90;
                // else if (f_snr <= 33.5) SQI = 90;           
                else SQI = 100;
            }
#else
            float   log_ber;
            log_ber = (-1.0f)*Log10Approx(1.0f/fber); // Log10Approx() provide 1~2^32 input range only

            if (Qam_mode == 0)//DMD_DVBC_QAM16
            {
               if(log_ber  <= (-5.5f))
                    SQI = 100;
                else if(log_ber  <= (-5.1f))
                    SQI = (MAPI_U16)(90.0f + ((-5.1f)-log_ber)*10.0f/((-5.1f)-(-5.5f)));
                else if(log_ber  <= (-4.9f))
                    SQI = (MAPI_U16)(80.0f + ((-4.9f)-log_ber)*10.0f/((-4.9f)-(-5.1f)));
                else if(log_ber  <= (-4.5f))
                    SQI = (MAPI_U16)(70.0f + ((-4.5f)-log_ber)*10.0f/((-4.5f)-(-4.9f)));
                else if(log_ber  <= (-3.7f))
                    SQI = (MAPI_U16)(60.0f + ((-3.7f)-log_ber)*10.0f/((-3.7f)-(-4.5f)));
                else if(log_ber  <= (-3.2f))
                    SQI = (MAPI_U16)(50.0f + ((-3.2f)-log_ber)*10.0f/((-3.2f)-(-3.7f)));
                else if(log_ber  <= (-2.9f))
                    SQI = (MAPI_U16)(40.0f + ((-2.9f)-log_ber)*10.0f/((-2.9f)-(-3.2f)));
                else if(log_ber  <= (-2.5f))
                    SQI = (MAPI_U16)(30.0f + ((-2.5f)-log_ber)*10.0f/((-2.5f)-(-2.9f)));
                else if(log_ber  <= (-2.2f))
                    SQI = (MAPI_U16)(20.0f + ((-2.2f)-log_ber)*10.0f/((-2.2f)-(-2.5f)));
                else if(log_ber  <= (-2.0f))
                    SQI = (MAPI_U16)(0.0f + ((-2.0f)-log_ber)*10.0f/((-2.0f)-(-2.2f)));
                else
                    SQI = 0;
            }
            else if (Qam_mode == 1)//DMD_DVBC_QAM32
            {
                if(log_ber  <= (-5.0f))
                    SQI = 100;
                else if(log_ber  <= (-4.7f))
                    SQI = (MAPI_U16)(90.0f  + ((-4.7f)-log_ber)*10.0f/((-4.7f)-(-5.0f)));
                else if(log_ber  <= (-4.5f))
                    SQI = (MAPI_U16)(80.0f  + ((-4.5f)-log_ber)*10.0f/((-4.5f)-(-4.7f)));
                else if(log_ber  <= (-3.8f))
                    SQI = (MAPI_U16)(70.0f  + ((-3.8f)-log_ber)*10.0f/((-3.8f)-(-4.5f)));
                else if(log_ber  <= (-3.5f))
                    SQI = (MAPI_U16)(60.0f  + ((-3.5f)-log_ber)*10.0f/((-3.5f)-(-3.8f)));
                else if(log_ber  <= (-3.0f))
                    SQI = (MAPI_U16)(50.0f  + ((-3.0f)-log_ber)*10.0f/((-3.0f)-(-3.5f)));
                else if(log_ber  <= (-2.7f))
                    SQI = (MAPI_U16)(40.0f  + ((-2.7f)-log_ber)*10.0f/((-2.7f)-(-3.0f)));
                else if(log_ber  <= (-2.4f))
                    SQI = (MAPI_U16)(30.0f  + ((-2.4f)-log_ber)*10.0f/((-2.4f)-(-2.7f)));
                else if(log_ber  <= (-2.2f))
                    SQI = (MAPI_U16)(20.0f  + ((-2.2f)-log_ber)*10.0f/((-2.2f)-(-2.4f)));
                else if(log_ber  <= (-2.0f))
                    SQI = (MAPI_U16)(0.0f  + ((-2.0f)-log_ber)*10.0f/((-2.0f)-(-2.2f)));
                else
                    SQI = 0;
            }
            else if (Qam_mode == 2)//DMD_DVBC_QAM64
            {
                if(log_ber  <= (-5.4f))
                    SQI = 100;
                else if(log_ber  <= (-5.1f))
                    SQI = (MAPI_U16)(90.0f + ((-5.1f)-log_ber)*10.0f/((-5.1f)-(-5.4f)));
                else if(log_ber  <= (-4.9f))
                    SQI = (MAPI_U16)(80.0f + ((-4.9f)-log_ber)*10.0f/((-4.9f)-(-5.1f)));
                else if(log_ber  <= (-4.3f))
                    SQI = (MAPI_U16)(70.0f + ((-4.3f)-log_ber)*10.0f/((-4.3f)-(-4.9f)));
                else if(log_ber  <= (-3.7f))
                    SQI = (MAPI_U16)(60.0f + ((-3.7f)-log_ber)*10.0f/((-3.7f)-(-4.3f)));
                else if(log_ber  <= (-3.2f))
                    SQI = (MAPI_U16)(50.0f + ((-3.2f)-log_ber)*10.0f/((-3.2f)-(-3.7f)));
                else if(log_ber  <= (-2.9f))
                    SQI = (MAPI_U16)(40.0f + ((-2.9f)-log_ber)*10.0f/((-2.9f)-(-3.2f)));
                else if(log_ber  <= (-2.4f))
                    SQI = (MAPI_U16)(30.0f + ((-2.4f)-log_ber)*10.0f/((-2.4f)-(-2.9f)));
                else if(log_ber  <= (-2.2f))
                    SQI = (MAPI_U16)(20.0f + ((-2.2f)-log_ber)*10.0f/((-2.2f)-(-2.4f)));
                else if(log_ber  <= (-2.05f))
                    SQI = (MAPI_U16)(0.0f + ((-2.05f)-log_ber)*10.0f/((-2.05f)-(-2.2f)));
                else
                    SQI = 0;
            }
            else if (Qam_mode == 3)//DMD_DVBC_QAM128
            {
                if(log_ber  <= (-5.1f))
                    SQI = 100;
                else if(log_ber  <= (-4.9f))
                    SQI = (MAPI_U16)(90.0f + ((-4.9f)-log_ber)*10.0f/((-4.9f)-(-5.1f)));
                else if(log_ber  <= (-4.7f))
                    SQI = (MAPI_U16)(80.0f + ((-4.7f)-log_ber)*10.0f/((-4.7f)-(-4.9f)));
                else if(log_ber  <= (-4.1f))
                    SQI = (MAPI_U16)(70.0f + ((-4.1f)-log_ber)*10.0f/((-4.1f)-(-4.7f)));
                else if(log_ber  <= (-3.5f))
                    SQI = (MAPI_U16)(60.0f + ((-3.5f)-log_ber)*10.0f/((-3.5f)-(-4.1f)));
                else if(log_ber  <= (-3.1f))
                    SQI = (MAPI_U16)(50.0f + ((-3.1f)-log_ber)*10.0f/((-3.1f)-(-3.5f)));
                else if(log_ber  <= (-2.7f))
                    SQI = (MAPI_U16)(40.0f + ((-2.7f)-log_ber)*10.0f/((-2.7f)-(-3.1f)));
                else if(log_ber  <= (-2.5f))
                    SQI = (MAPI_U16)(30.0f + ((-2.5f)-log_ber)*10.0f/((-2.5f)-(-2.7f)));
                else if(log_ber  <= (-2.06f))
                    SQI = (MAPI_U16)(20.0f + ((-2.06f)-log_ber)*10.0f/((-2.06f)-(-2.5f)));
                else
                {
                    if (f_snr >= 27.2f)
                        SQI = 20;
                    else if (f_snr >= 25.1f)
                        SQI = (MAPI_U16)(0.0f + (f_snr - 25.1f)*20.0f/(27.2f-25.1f));
                    else
                        SQI = 0;
                }
            }
            else //256QAM
            {
                if(log_ber  <= (-4.8f))
                    SQI= 100;
                else if(log_ber  <= (-4.6f))
                    SQI = (MAPI_U16)(90.0f + ((-4.6f)-log_ber)*10.0f/((-4.6f)-(-4.8f)));
                else if(log_ber  <= (-4.4f))
                    SQI = (MAPI_U16)(80.0f + ((-4.4f)-log_ber)*10.0f/((-4.4f)-(-4.6f)));
                else if(log_ber  <= (-4.0f))
                    SQI = (MAPI_U16)(70.0f + ((-4.0f)-log_ber)*10.0f/((-4.0f)-(-4.4f)));
                else if(log_ber  <= (-3.5f))
                    SQI = (MAPI_U16)(60.0f + ((-3.5f)-log_ber)*10.0f/((-3.5f)-(-4.0f)));
                else if(log_ber  <= (-3.1f))
                    SQI = (MAPI_U16)(50.0f + ((-3.1f)-log_ber)*10.0f/((-3.1f)-(-3.5f)));
                else if(log_ber  <= (-2.7f))
                    SQI = (MAPI_U16)(40.0f + ((-2.7f)-log_ber)*10.0f/((-2.7f)-(-3.1f)));
                else if(log_ber  <= (-2.4f))
                    SQI = (MAPI_U16)(30.0f + ((-2.4f)-log_ber)*10.0f/((-2.4f)-(-2.7f)));
                else if(log_ber  <= (-2.06f))
                    SQI = (MAPI_U16)(20.0f + ((-2.06f)-log_ber)*10.0f/((-2.06f)-(-2.4f)));
                else
                {
                    if (f_snr >= 29.6f)
                        SQI = 20;
                    else if (f_snr >= 27.3f)
                        SQI = (MAPI_U16)(0.0f + (f_snr - 27.3f)*20.0f/(29.6f-27.3f));
                    else
                        SQI = 0;
                }
            }
#endif
            DBG_GET_SIGNAL(printf(">>> SQI = %d <<<\n", (int)SQI));
            return (MAPI_U16)SQI;            
        }
        break;
#endif        
#if (DVBS_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_S:
        {
            //GetSignalNoiseRatio
            MAPI_BOOL bRet=MAPI_TRUE;
            MAPI_U16 u16Address =0;
            MAPI_U8  u8Data =0;
            //NDA SNR
            // MAPI_U32 u32NDA_SNR_A =0;
            // MAPI_U32 u32NDA_SNR_AB =0;
            
            //NDA SNR
            // float NDA_SNR_A =0.0;
            // float NDA_SNR_AB =0.0;
            float NDA_SNR =0.0;
            float snr_poly =0.0;
            float Fixed_SNR =0.0;

            //GetSignalQuality
            MAPI_U16  u16Data = 0;
            MAPI_U8   u8Data2 = 0;
            MAPI_U8   _u8_DVBS2_CurrentConstellationLocal = 0, _u8_DVBS2_CurrentCodeRateLocal = 0;
            MAPI_U16  u16BitErrPeriod =0;
            MAPI_U32  u32BitErr =0;
        
            MAPI_U16  BCH_Eflag2_Window=0;
            float fber =0.0;
            float __attribute__ ((unused)) log_ber = 0.0, ber_sqi = 0.0, cn_rel = 0.0, f_snr = 0.0;
            double LDPCBer=0.0, eFlag_PER=0.0;
        
            S_SQI_TABLE *s2_sqi_ptr=NULL;
            MAPI_U8    s2_sqi_table_len = 0;
        
            float fDVBS2_SQI_CNref[][11]=
            {    //0,   1,    2,    3,    4,    5,    6,    7,    8,    9,    10
            //old 1/2, 1/3,  2/3,  1/4,  3/4,  2/5,  3/5,  4/5,  5/6,   8/9,  9/10
              //  1/4, 1/3,  2/5,  1/2,  3/5,  2/3,  3/4,  4/5,  5/6,   8/9,  9/10
                {-1.6,-0.5,  0.5,  1.8,  3.0,  3.8,  4.8,  5.4,  5.9,   6.9,  7.2}, //QPSK
                {0.0,  0.0,  0.0,  0.0,  6.3,  7.4,  8.7,  0.0, 10.1,  11.4, 11.8}, //8PSK
            };
        
           
           S_SQI_TABLE S2_QPSK_CR23[] = 
            { 
                { 20,  10}, // CN 2.0 -> SQI 10
                { 25,  30},
                { 30,  50},
                { 36,  66},
                { 40,  74},
                { 45,  90},
                { 50, 100}, 
            };
        
           S_SQI_TABLE S2_QPSK_CR34[] = 
            { 
                { 20,  5},
                { 30, 15},
                { 35, 30},
                { 40, 50},
                { 46, 67},
                { 50, 74},
                { 60, 90}, 
            };
           
           S_SQI_TABLE S2_QPSK_CR56[] = 
            { 
                { 40,    5},
                { 45,  20},
                { 50,  46},
                { 55,  54},
                { 57,  70},
                { 60,  90},
                { 70, 100}, 
            };
        
           S_SQI_TABLE S2_8PSK_CR23[] = 
            { 
                { 50,   5},
                { 60, 20},
                { 65, 45},
                { 71, 66},
                { 80, 74},
                { 90, 90},
                {100, 100}, 
            };
        
           S_SQI_TABLE S2_8PSK_CR34[] = 
            { 
                { 70,   5},
                { 75, 20},
                { 80, 45},
                { 84, 66},
                { 90, 74},
                {100, 90},
                {110, 100}, 
            };
           
           S_SQI_TABLE S2_8PSK_CR56[] = 
            { 
                { 80,   5},
                { 85, 20},
                { 90, 45},
                { 95, 66},
                { 99, 74},
                {100, 90},
                {110, 100}, 
            };
        
           S_SQI_TABLE S_QPSK_CR23[] = 
            { 
                { 30,  10},
                { 40, 30},
                { 45, 47},
                { 50, 54},
                { 58, 67},
                { 60, 73},
                { 70, 90},
               { 80, 100},     
            };
        
           S_SQI_TABLE S_QPSK_CR34[] = 
            { 
                { 40,  10},
                { 50, 30},
                { 55, 47},
                { 60, 54},
                { 68, 67},
                { 70, 73},
                { 80, 90},
               { 90, 100},     
            };
           
           S_SQI_TABLE S_QPSK_CR56[] = 
            {
                { 50,  10},
                { 60, 30},
                { 65, 47},
                { 70, 54},
                { 78, 67},
                { 80, 73},
                { 90, 90},
               { 100, 100},
            };
        

            u16Address=0x0990;
            bRet&=ReadReg(u16Address, &u8Data);
            u8Data|=0x04;
            bRet&=WriteReg(u16Address, u8Data);
        
            u16Address=0x0B55;
            bRet&=ReadReg(u16Address, &u8Data);
            DBG_DEMOD_MSB(printf("u16Address=0x0B55 =%d\n",u8Data));
            if ((u8Data>=15) && (u8Data!=0xcd))
            {
                if (u8Data==15)//(_bDemodType==MAPI_FALSE)//S
                {
        
                    MAPI_U8 u8_i = 0;
                    MAPI_U16 u16_10snr = 0;
#if 0        
                    ReadDspReg(E_S2_10SNR_H, &u8Data);
                    u16Data=u8Data;
                    ReadDspReg(E_S2_10SNR_L, &u8Data);
                    u16Data = (u16Data<<8)|u8Data;
                    NDA_SNR = (float)u16Data/10.0;
                    u16_10snr = (MAPI_U16)(NDA_SNR*10.0+0.5);
#else
                    DTV_GetSNR();
                    u16_10snr = (MAPI_U16)(g_msb1240_fSNR*10);
#endif       
                    // u16_10snr = 60;
                    u16Address = 0x1C84;
                    bRet &= ReadReg(u16Address, &u8Data);//INNER_PLSCDEC_DEBUG_OUT0
                    if ((u8Data&0x07)==0x01)  // CR2/3
                    {
                        s2_sqi_ptr=S_QPSK_CR23;
                        s2_sqi_table_len = sizeof(S_QPSK_CR23)/sizeof(S_SQI_TABLE);
                    }
                    else if ((u8Data&0x07)==0x02) //CR 3/4
                    {
                        s2_sqi_ptr=S_QPSK_CR34;
                        s2_sqi_table_len = sizeof(S_QPSK_CR34)/sizeof(S_SQI_TABLE);                
                    }
                    else
                    {
                        s2_sqi_ptr=S_QPSK_CR56;
                        s2_sqi_table_len = sizeof(S_QPSK_CR56)/sizeof(S_SQI_TABLE);   
                    }
        
                    if ((s2_sqi_table_len == 0) || (s2_sqi_ptr  == NULL))
                    {
                        u16_sqi = 0;
                    }
                    else
                    {
                        while(s2_sqi_ptr[u8_i].u16_x_10cn < u16_10snr)
                        {
                           u8_i++;
                           if(u8_i == s2_sqi_table_len) break;
                        }
        
                        if (u8_i == s2_sqi_table_len)
                        {
                           u16_sqi = (MAPI_U16)((float)(s2_sqi_ptr[u8_i-1].u8_y_sqi-s2_sqi_ptr[u8_i-2].u8_y_sqi)/(float)(s2_sqi_ptr[u8_i-1].u16_x_10cn-s2_sqi_ptr[u8_i-2].u16_x_10cn)*(float)(u16_10snr-s2_sqi_ptr[u8_i-2].u16_x_10cn)+s2_sqi_ptr[u8_i-2].u8_y_sqi);
                        }
                        else if (u8_i == 0)
                        {
                           u16_sqi = (MAPI_U16)((float)(s2_sqi_ptr[1].u8_y_sqi-s2_sqi_ptr[0].u8_y_sqi)/(float)(s2_sqi_ptr[1].u16_x_10cn-s2_sqi_ptr[0].u16_x_10cn)*(float)(u16_10snr-s2_sqi_ptr[0].u16_x_10cn)+s2_sqi_ptr[0].u8_y_sqi);
                           if (u16_sqi & 0x8000) u16_sqi = 0;
                        }
                        else
                        {
                           u16_sqi = (MAPI_U16)((float)(s2_sqi_ptr[u8_i].u8_y_sqi-s2_sqi_ptr[u8_i-1].u8_y_sqi)/(float)(s2_sqi_ptr[u8_i].u16_x_10cn-s2_sqi_ptr[u8_i-1].u16_x_10cn)*(float)(u16_10snr-s2_sqi_ptr[u8_i-1].u16_x_10cn)+s2_sqi_ptr[u8_i-1].u8_y_sqi);
                        }
        
                        if (u16_sqi > 100) u16_sqi = 100;
                    }
                }
                else    //S2
                {                    
#if 0                             
                    ReadDspReg(E_S2_10SNR_H, &u8Data);
                    u16Data=u8Data;
                    ReadDspReg(E_S2_10SNR_L, &u8Data);
                    u16Data = (u16Data<<8)|u8Data;
                    NDA_SNR = (float)u16Data/10.0;
#else
                    DTV_GetSNR();
                    NDA_SNR = g_msb1240_fSNR;
#endif             
                    // NDA_SNR = 6; 
                    //printf("[DVBS]: NDA_SNR ================================: %.1f\n", NDA_SNR);
                    _f_DVBS2_CurrentSNR = NDA_SNR;
        
                    //[DVBS/S2, QPSK/8PSK, 1/2~9/10 the same CN]
                    snr_poly = 0.0; //use Polynomial curve fitting to fix SNR
                    snr_poly = 0.005261367463671*pow(NDA_SNR, 3)-0.116517828301214*pow(NDA_SNR, 2)+0.744836970505452*pow(NDA_SNR, 1)-0.86727609780167;
                    Fixed_SNR = NDA_SNR + snr_poly;
                    //printf("[DVBS]: NDA_SNR + snr_poly =====================: %.1f\n", Fixed_SNR);
                    
                    if (Fixed_SNR < 17.0)
                        Fixed_SNR = Fixed_SNR;
                    else if ((Fixed_SNR < 20.0) && (Fixed_SNR >= 17.0))
                        Fixed_SNR = Fixed_SNR - 0.8;
                    else if ((Fixed_SNR < 22.5) && (Fixed_SNR >= 20.0))
                        Fixed_SNR = Fixed_SNR - 2.0;
                    else if ((Fixed_SNR < 27.0) && (Fixed_SNR >= 22.5))
                        Fixed_SNR = Fixed_SNR - 3.0;
                    else if ((Fixed_SNR < 29.0) && (Fixed_SNR >= 27.0))
                        Fixed_SNR = Fixed_SNR - 3.5;
                    else if (Fixed_SNR >= 29.0)
                        Fixed_SNR = Fixed_SNR - 3.0;
                    
                    
                    if (Fixed_SNR < 1.0)
                        Fixed_SNR = 1.0;
                    if (Fixed_SNR > 30.0)
                        Fixed_SNR = 30.0;
                    //printf("[DVBS]: SNR_Fixed_ =============================: %.1f\n", Fixed_SNR);
                    
                    u16Address=0x1D04;
                    bRet&=ReadReg(u16Address, &u8Data);
                    u8Data|=0x01;
                    bRet&=WriteReg(u16Address, u8Data);
                    //BCH eflag2 sum
                    u16Address=0x264D;
                    bRet&=ReadReg(u16Address, &u8Data);
                    _u16_packetError=u8Data;
                    u16Address=0x264C;
                    bRet&=ReadReg(u16Address, &u8Data);
                    _u16_packetError=(_u16_packetError<<8)|u8Data;
                
                    //LDPC_Count_Window
                    u16Address=0x2625;
                    bRet&=ReadReg(u16Address, &u8Data);
                    u16BitErrPeriod=u8Data;
                    u16Address=0x2624;
                    bRet&=ReadReg(u16Address, &u8Data);
                    u16BitErrPeriod=(u16BitErrPeriod<<8)|u8Data;
                    //LDPC_BER_Count
                    u16Address=0x2667;
                    bRet&=ReadReg(u16Address, &u8Data);
                    u32BitErr=u8Data;
                    u16Address=0x2666;
                    bRet&=ReadReg(u16Address, &u8Data);
                    u32BitErr=(u32BitErr<<8)|u8Data;
                    u16Address=0x2665;
                    bRet&=ReadReg(u16Address, &u8Data);
                    u32BitErr=(u32BitErr<<8)|u8Data;
                    u16Address=0x2664;
                    bRet&=ReadReg(u16Address, &u8Data);
                    u32BitErr=(u32BitErr<<8)|u8Data;
                
                    u16Address=0x1D04;
                    bRet&=ReadReg(u16Address, &u8Data);
                    u8Data&=~(0x01);
                    bRet&=WriteReg(u16Address, u8Data);
                
                    if (u16BitErrPeriod == 0)
                        u16BitErrPeriod = 1;
                    /*//For Debug
                              u16Address = 0x264A;                //BCH EFLAG2_Window, mick
                              u16Data = 256*8;
                              bRet &= MSB131X_WriteReg2bytes(u16Address, u16Data);
                              */
                    u16Address = 0x264A;                //BCH EFLAG2_Window, mick
                           
                    bRet &= ReadReg(u16Address + 0x0001, &u8Data);
                    u16Data = u8Data;
                    bRet &= ReadReg(u16Address, &u8Data);
                    u16Data = (u16Data<<8)|u8Data;
                
                    BCH_Eflag2_Window=u16Data;
                    //printf("E window %d\n", u16Data);
                    //fber = (float)u32BitErr/(u16BitErrPeriod*64800);          //PerLDPC BER
                
                    //New func                                                  //PostLDPC BER
                    eFlag_PER = (float)(_u16_packetError)/(float)(BCH_Eflag2_Window);
                    LDPCBer = 0.089267531133002*pow(eFlag_PER, 2) + 0.019640560289510*eFlag_PER + 0.0000000001;
                
                    fber = LDPCBer;
                    _fPostBer = LDPCBer;
                    //fgPerBER = LDPCBer;
                
                    //_fPostBer=fber;
                    //SQI
                    if (bRet==MAPI_FALSE)
                    {
                        printf("MSB1240_DVBS_GetSignalQuality GetPostViterbiBer Fail!\n");
                        return FALSE;
                    }
               
                    u16Address = 0x1BD7;
                    bRet &= ReadReg(u16Address, &u8Data);
                    _u8_DVBS2_CurrentCodeRateLocal = (u8Data & 0x3C)>>2;
                
                    u16Address = 0x1BD7;
                    bRet &= ReadReg(u16Address, &u8Data); //INNER_PLSCDEC_DEBUG_OUT1
                    u16Address = 0x1BD6;
                    bRet &= ReadReg(u16Address, &u8Data2);//INNER_PLSCDEC_DEBUG_OUT0
                
                    if(((u8Data & 0x03)==0x01) && ((u8Data2 & 0x80)==0x00))
                    {
                        _u8_DVBS2_CurrentConstellationLocal = DEMOD_SAT_QPSK_MSB1240;
                    }
                    else if (((u8Data & 0x03)==0x01) && ((u8Data2 & 0x80)==0x80))
                    {
                        _u8_DVBS2_CurrentConstellationLocal = DEMOD_SAT_8PSK_MSB1240;//8PSK
                    }
                    log_ber = ( - 1) *log10f(1 / fber);
                    if (fber > 1.0E-1)
                        ber_sqi = (log10f(1.0f/fber))*20.0f + 8.0f;
                    else if (fber > 1.0E-3)
                        ber_sqi = (log10f(1.0f/fber))*20.0f + 30.0f;
                    else if(fber > 8.5E-7)
                        ber_sqi = (log10f(1.0f/fber))*20.0f - 30.0f;
                    else
                        ber_sqi = 100.0;
          
                    f_snr = Fixed_SNR;
                    cn_rel = (f_snr*1.1) - fDVBS2_SQI_CNref[_u8_DVBS2_CurrentConstellationLocal][_u8_DVBS2_CurrentCodeRateLocal];
                
                    //printf(" [DVBS2]fber = %f\n",fber);
                    //printf(" [DVBS2]f_snr = %f\n",f_snr);
                    //printf(" [DVBS2]cn_nordig_s2 = %f\n",fDVBS2_SQI_CNref[_u8_DVBS2_CurrentConstellationLocal][_u8_DVBS2_CurrentCodeRateLocal]);
                    //printf(" [DVBS2]cn_rel = %f\n",cn_rel);
                    //printf(" [DVBS2]ber_sqi = %f\n",ber_sqi);
        
                    if ((_u8_DVBS2_CurrentCodeRateLocal == 5)||(_u8_DVBS2_CurrentCodeRateLocal ==6)||(_u8_DVBS2_CurrentCodeRateLocal == 8))
                    {
                        MAPI_U8 u8_i = 0;
                        MAPI_U16 u16_10snr = (MAPI_U16)(NDA_SNR*10.0+0.5);
                        MAPI_U16 u16_symbolrate = 0;
                        MAPI_U8  u8_reg = 0;
        
                        bRet &= ReadReg(0x0b00+0x29*2+1, &u8_reg);
                        u16_symbolrate = u8_reg;
                        bRet &= ReadReg(0x0b00+0x29*2, &u8_reg);
                        u16_symbolrate = (u16_symbolrate<<8)|u8_reg;                
        
                        if (_u8_DVBS2_CurrentConstellationLocal == DEMOD_SAT_8PSK_MSB1240)
                        {
                            // added 0.3dB for 8PSK.
                            u16_10snr+=3;
                        }
        
                        if(u16_symbolrate < 22000) 
                        {
                            u16_10snr-=1;
                        }
        
                        switch(_u8_DVBS2_CurrentCodeRateLocal)
                        {  
                            case 5: // CR2/3
                                {
                                    if (_u8_DVBS2_CurrentConstellationLocal == DEMOD_SAT_QPSK_MSB1240)
                                    {
                                        s2_sqi_ptr = S2_QPSK_CR23;
                                        s2_sqi_table_len = sizeof(S2_QPSK_CR23)/sizeof(S_SQI_TABLE);
                                    }
                                    else if (_u8_DVBS2_CurrentConstellationLocal == DEMOD_SAT_8PSK_MSB1240)
                                    {
                                        s2_sqi_ptr = S2_8PSK_CR23;
                                        s2_sqi_table_len = sizeof(S2_8PSK_CR23)/sizeof(S_SQI_TABLE);
                                    }
                                }
                                break;
                            case 6: // CR 3/4
                                {
                                    if (_u8_DVBS2_CurrentConstellationLocal == DEMOD_SAT_QPSK_MSB1240)
                                    {
                                        s2_sqi_ptr = S2_QPSK_CR34;
                                        s2_sqi_table_len = sizeof(S2_QPSK_CR34)/sizeof(S_SQI_TABLE);
                                    }
                                    else if (_u8_DVBS2_CurrentConstellationLocal == DEMOD_SAT_8PSK_MSB1240)
                                    {
                                        s2_sqi_ptr = S2_8PSK_CR34;
                                        s2_sqi_table_len = sizeof(S2_8PSK_CR34)/sizeof(S_SQI_TABLE);
                                    }
                                }
                                break;
                            case 8: // CR 5/6
                                {
                                    if (_u8_DVBS2_CurrentConstellationLocal == DEMOD_SAT_QPSK_MSB1240)
                                    {
                                        s2_sqi_ptr = S2_QPSK_CR56;
                                        s2_sqi_table_len = sizeof(S2_QPSK_CR56)/sizeof(S_SQI_TABLE);
                                    }
                                    else if (_u8_DVBS2_CurrentConstellationLocal == DEMOD_SAT_8PSK_MSB1240)
                                    {
                                        s2_sqi_ptr = S2_8PSK_CR56;
                                        s2_sqi_table_len = sizeof(S2_8PSK_CR56)/sizeof(S_SQI_TABLE);
                                    }
                                }
                                break;
                            default:
                                break;
                        }
        
                        if ((s2_sqi_table_len == 0) || (s2_sqi_ptr  == NULL))
                        {
                             u16_sqi = 0;
                        }
                        else
                        {
                           while(s2_sqi_ptr[u8_i].u16_x_10cn < u16_10snr)
                           {
                               u8_i++;
                               if(u8_i == s2_sqi_table_len) break;
                           }
        
                           if (u8_i == s2_sqi_table_len)
                           {
                               u16_sqi = (MAPI_U16)((float)(s2_sqi_ptr[u8_i-1].u8_y_sqi-s2_sqi_ptr[u8_i-2].u8_y_sqi)/(float)(s2_sqi_ptr[u8_i-1].u16_x_10cn-s2_sqi_ptr[u8_i-2].u16_x_10cn)*(float)(u16_10snr-s2_sqi_ptr[u8_i-2].u16_x_10cn)+s2_sqi_ptr[u8_i-2].u8_y_sqi);
                           }
                           else if (u8_i == 0)
                           {
                               u16_sqi = (MAPI_U16)((float)(s2_sqi_ptr[1].u8_y_sqi-s2_sqi_ptr[0].u8_y_sqi)/(float)(s2_sqi_ptr[1].u16_x_10cn-s2_sqi_ptr[0].u16_x_10cn)*(float)(u16_10snr-s2_sqi_ptr[0].u16_x_10cn)+s2_sqi_ptr[0].u8_y_sqi);
                               if (u16_sqi & 0x8000) u16_sqi = 0;
                           }
                           else
                           {
                               u16_sqi = (MAPI_U16)((float)(s2_sqi_ptr[u8_i].u8_y_sqi-s2_sqi_ptr[u8_i-1].u8_y_sqi)/(float)(s2_sqi_ptr[u8_i].u16_x_10cn-s2_sqi_ptr[u8_i-1].u16_x_10cn)*(float)(u16_10snr-s2_sqi_ptr[u8_i-1].u16_x_10cn)+s2_sqi_ptr[u8_i-1].u8_y_sqi);
                           }
        
                           if (u16_sqi > 100) u16_sqi = 100;
                        }
                    }       
                    else
                    {             
                        if (_u8_DVBS2_CurrentConstellationLocal == DEMOD_SAT_QPSK_MSB1240)
                        {
                            u16Address = 0x1BD7;
                            bRet &= ReadReg(u16Address, &u8Data);
                            if ((u8Data&0x3C) != 0x20)
                            {
                                if (cn_rel < -7.0f)
                                {
                                    SQI = 0;
                                }
                                else if (cn_rel < -2.0)
                                {
                                    SQI = (MAPI_U16)(ber_sqi*((cn_rel - 4.5)/10.0 + 1.0));
                                }
                                else if (cn_rel < -0.9)
                                {
                                    ber_sqi=27;
                                    SQI = (MAPI_U16)(ber_sqi*((cn_rel - 3.5)/10.0 + 1.0));
                                }
                                else if (cn_rel < -0.5)
                                {
                                    ber_sqi=27;
                                    SQI = (MAPI_U16)(ber_sqi*((cn_rel + 1)/10.0 + 1.0));
                                }
                                else if (cn_rel < -0.2)
                                {
                                    ber_sqi=27;
                                    SQI = (MAPI_U16)(ber_sqi*((cn_rel + 8.5)/10.0 + 1.0));
                                }
                                else if (cn_rel < 0.15)
                                {
                                    ber_sqi=58;
                                    SQI = (MAPI_U16)(ber_sqi*((cn_rel - 2.5)/10.0 + 1.0));
                                }
                                else if (cn_rel < 0.5)
                                {
                                    ber_sqi=80;
                                    SQI = (MAPI_U16)(ber_sqi*((cn_rel - 4)/10.0 + 1.0));
                                }
                                else if (cn_rel < 1.5)
                                {
                                    SQI = (MAPI_U16)(ber_sqi*((cn_rel - 3.5)/10.0 + 1.0));
                                }
                                else if (cn_rel < 3)
                                {
                                    SQI = (MAPI_U16)(ber_sqi*((cn_rel - 3)/10.0 + 1.0));
                                }
                                else
                                {
                                    SQI = (MAPI_U16)ber_sqi;
                                }
                            }
                            else
                            {
                                if (cn_rel < -7.0f)
                                {
                                    SQI = 0;
                                }
                                else if (cn_rel < -0.6)
                                {
                                    ber_sqi=27;
                                    SQI = (MAPI_U16)(ber_sqi*((cn_rel - 7.5)/10.0 + 1.0));
                                }
                                else if (cn_rel < -0.27)
                                {
                                    ber_sqi=27;
                                    SQI = (MAPI_U16)(ber_sqi*((cn_rel + 1)/10.0 + 1.0));
                                }
                                else if (cn_rel < 0.32)
                                {
                                    ber_sqi=50;
                                    SQI = (MAPI_U16)(ber_sqi*((cn_rel - 1.5)/10.0 + 1.0));
                                }
                                else if (cn_rel < 1.1)
                                {
                                    SQI = (MAPI_U16)(ber_sqi*((cn_rel - 3.5)/10.0 + 1.0));
                                }
                                else if (cn_rel < 1.5)
                                {
                                    SQI = (MAPI_U16)(ber_sqi*((cn_rel - 3)/10.0 + 1.0));
                                }
                                else if (cn_rel < 3)
                                {
                                    SQI = (MAPI_U16)(ber_sqi*((cn_rel - 3)/10.0 + 1.0));
                                }
                                else
                                {
                                    SQI = (MAPI_U16)ber_sqi;
                                }
                            }
                        }
                        else
                        {
                            if ((u8Data&0x3C) != 0x20)
                            {
                                if (cn_rel < -7.0f)
                                {
                                    SQI = 0;
                                }
                                else if (cn_rel < -0.6)
                                {
                                    ber_sqi=27;
                                    SQI = (MAPI_U16)(ber_sqi*((cn_rel - 7)/10.0 + 1.0));
                                }
                                else if (cn_rel < -0.15)
                                {
                                    ber_sqi=27;
                                    SQI = (MAPI_U16)(ber_sqi*((cn_rel + 1)/10.0 + 1.0));
                                }
                                else if (cn_rel < 0.32)
                                {
                                    ber_sqi=50;
                                    SQI = (MAPI_U16)(ber_sqi*((cn_rel - 1.5)/10.0 + 1.0));
                                }
                                else if (cn_rel < 1.1)
                                {
                                    SQI = (MAPI_U16)(ber_sqi*((cn_rel - 3.5)/10.0 + 1.0));
                                }
                                else if (cn_rel < 1.7)
                                {
                                    SQI = (MAPI_U16)(ber_sqi*((cn_rel - 3.8)/10.0 + 1.0));
                                }
                                else if (cn_rel < 3)
                                {
                                    SQI = (MAPI_U16)(ber_sqi*((cn_rel - 3)/10.0 + 1.0));
                                }
                                else
                                {
                                    SQI = (MAPI_U16)ber_sqi;
                                }
                            }
                            else
                            {
                                if (cn_rel < -7.0f)
                                {
                                    SQI = 0;
                                }
                                else if (cn_rel < -1.0)
                                {
                                    ber_sqi=27;
                                    SQI = (MAPI_U16)(ber_sqi*((cn_rel -8)/10.0 + 1.0));
                                }
                                else if (cn_rel < -0.4)
                                {
                                    ber_sqi=27;
                                    SQI = (MAPI_U16)(ber_sqi*((cn_rel + 0.4)/10.0 + 1.0));
                                }
                                else if (cn_rel < -0.1)
                                {
                                    ber_sqi=27;
                                    SQI = (MAPI_U16)(ber_sqi*((cn_rel + 7)/10.0 + 1.0));
                                }
                                else if (cn_rel < 0.35)
                                {
                                    SQI = (MAPI_U16)(ber_sqi*((cn_rel - 3.0)/10.0 + 1.0));
                                }
                                else if (cn_rel < 0.9)
                                {
                                    SQI = (MAPI_U16)(ber_sqi*((cn_rel - 2.5)/10.0 + 1.0));
                                    SQI=80;
                                }
                                else if (cn_rel < 1.5)
                                {
                                    SQI = (MAPI_U16)(ber_sqi*((cn_rel - 3)/10.0 + 1.0));
                                }
                                else if (cn_rel < 1.55)
                                {
                                    SQI = (MAPI_U16)(ber_sqi*((cn_rel - 4.5)/10.0 + 1.0));
                                }
                                else if (cn_rel < 3)
                                {
                                    SQI = (MAPI_U16)(ber_sqi*((cn_rel - 2.5)/10.0 + 1.0));
                                }
                                else
                                {
                                    SQI = (MAPI_U16)ber_sqi;
                                }
                            }
                        }
                        u16_sqi = (MAPI_U16)SQI;
                    }                
                }
            }
            else
            {
                u16_sqi = 0;
                return u16_sqi;
            }
            u16Address=0x0990;
            bRet&=ReadReg(u16Address, &u8Data);
            u8Data&=0xFB;
            bRet&=WriteReg(u16Address, u8Data);
        
            DBG_DEMOD_MSB(printf(">>> SQI = %d <<<\n",u16_sqi));

            return u16_sqi;
        }
        break;
#endif
        default:
        {
            break;
        }
    }  // end of switch
    return 0;
#undef DVBT2_BER_TH_HY
}

MAPI_U16 device_demodulator_extend::DTV_GetSignalStrength(void)
{
    MAPI_U8    ssi = 0;
    double fPrel=0.0, fPinput=0.0;
    MAPI_U8  demodState = 0;
    MAPI_U8  temp_demodState = 0;
    mapi_tuner *pTuner=NULL;
    float fRFLevel=0.0f;
    MAPI_U16 if_agc_gain = 0;

    mapi_scope_lock(scopeLock, &m_MutexOuter);
    switch(device_demodulator_extend::m_enCurrentDemodulator_Type)
    {
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2:
        {
            {
                float fDVBT2_SSI_Pref[][6]=
                {
                    {-95.7, -94.4, -93.6, -92.6, -92.0, -91.5},
                    {-90.8, -89.1, -87.9, -86.7, -85.8, -85.2},
                    {-86.9, -84.6, -83.2, -81.4, -80.3, -79.7},
                    {-83.5, -80.4, -78.6, -76.0, -74.4, -73.3},
                };
                MAPI_U8 u8Modulation=0, u8CodeRate=0;
                static MAPI_U8 u8SignalEverStable=FALSE, u8ModulationStable=0, u8CodeRateStable=0;
                // cr, 3/5(1),  2/3(2), 3/4 (3)
                float fT2_SSI_formula[][12]=
                {
                    {1.0/5,  97.0,  3.0/2,  82.0, 16.0/5,  50.0, 29.0/10.0, 21.0, 18.0/15, 3.0, 3.0/5, 0.0}, // CR3/5
                    {2.0/3,  95.0,  9.0/5,  77.0, 17.0/5,  43.0, 14.0/5.0,  15.0, 13.0/15, 2.0, 2.0/5, 0.0}, // CR2/3
                    {1.0/2,  93.0, 19.0/10, 74.0, 31.0/10, 43.0, 22.0/10.0, 21.0, 18.0/15, 3.0, 3.0/5, 0.0}, // CR3/4
                };

                DTV_GetIFAGCGain(&if_agc_gain);

                IIC_Bypass_Mode(true);

                pTuner = mapi_interface::Get_mapi_pcb()->GetDvbtTuner(0);
                if (pTuner!=NULL)
                {
                    if ( MAPI_FALSE == pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_RF_LEVEL, 1, (MAPI_U32)if_agc_gain, &fRFLevel))
                    {
                        fRFLevel=200.0f;
                    }
                    else
                    {
                        // add for 1% SSI, remove if not necessary 
                        // fRFLevel += (((float)rand()/(float)RAND_MAX) -0.5f);
                    } 
                }
                else
                {
                    fRFLevel=200.0f;
                    printf("%s Line:%d Error!!\n",__func__, __LINE__);
                }

                IIC_Bypass_Mode(false);

                fPinput = fRFLevel;
                ReadReg(0x0990, &temp_demodState);

                u8Modulation=DTV_DVB_T_GetSignalModulation();
                if (u8Modulation>=3) u8Modulation=3;
 
                u8CodeRate=DTV_DVB_T_GetSignalCodeRate();
                if (u8CodeRate>=5) u8CodeRate=5;

                ReadReg(0x0990, &demodState);
                
                //Fix me, temp solution; demod doesn't lock, then use fref = -98 to compute SSI
                if ((temp_demodState >= 0x09)   &&   (demodState >= 0x09))
                {
                    fPrel=fPinput-fDVBT2_SSI_Pref[u8Modulation][u8CodeRate];
                    if (u8Modulation || u8CodeRate)
                    {
                        u8ModulationStable = u8Modulation;
                        u8CodeRateStable = u8CodeRate;    
                        u8SignalEverStable = TRUE;
                    }
                }
                else if (u8SignalEverStable)
                {
                    fPrel=fPinput-fDVBT2_SSI_Pref[u8ModulationStable][u8CodeRateStable];
                }
                else
                {
                    fPrel=-20.0f; // for SSI to 0
                }
                   
              if ( (u8ModulationStable == 3) //256qam
                    && (u8CodeRateStable > 0 && u8CodeRateStable < 4) // CR 3/5,2/3,3/4
                    )
                {
                    MAPI_U8 u8_x = u8CodeRateStable - 1;
                    float f_ssi = 0.0;
                    
                    if (fPinput >= -50) f_ssi = fT2_SSI_formula[u8_x][0]*(fPinput + 50) + fT2_SSI_formula[u8_x][1]; 
                    else if (fPinput >= -60)  f_ssi = fT2_SSI_formula[u8_x][2]*(fPinput + 60) + fT2_SSI_formula[u8_x][3];
                    else if (fPinput >= -70)  f_ssi = fT2_SSI_formula[u8_x][4]*(fPinput + 70) + fT2_SSI_formula[u8_x][5];
                    else if (fPinput >= -80)  f_ssi = fT2_SSI_formula[u8_x][6]*(fPinput + 80) + fT2_SSI_formula[u8_x][7];
                    else if (fPinput >= -95)  f_ssi = fT2_SSI_formula[u8_x][8]*(fPinput + 95) + fT2_SSI_formula[u8_x][9];
                    else if (fPinput >= -100) f_ssi = fT2_SSI_formula[u8_x][10]*(fPinput + 100) + fT2_SSI_formula[u8_x][11];

                    if (f_ssi > 100) ssi = 100;
                    else if (f_ssi < 0) ssi = 0;
                    else ssi = (MAPI_U8)(f_ssi+0.5);

                    if (u8SignalEverStable == FALSE) ssi = 0;

                    DBG_GET_SIGNAL(printf("MSB1240 SSI... RF_level=%f, f_ssi=%f,ssi=%d,cr=%d,mod=%d\n",fPinput,f_ssi,ssi,u8CodeRateStable,u8ModulationStable);)
                }
                else
                {
                    if (fPrel<-15.0)
                    {
                        ssi = 0;
                    }
                    else if (fPrel<0.0)
                    {
                        ssi = (MAPI_U8)(((double)2.0/3)*(fPrel+15.0));
                    }
                    else if (fPrel<20.0)
                    {
                        ssi = 4*fPrel+10;
                    }
                    else if (fPrel<35.0)
                    {
                        ssi = (MAPI_U8)(((double)2.0/3)*(fPrel-20.0)+90);
                    }
                    else
                    {
                        ssi = 100;
                    }
                    DBG_GET_SIGNAL(printf("old ssi, ssi=%d, cr=%d, mod=%d\n",ssi,u8CodeRateStable,u8ModulationStable);)
                }                
            }                
            DBG_GET_SIGNAL(printf(">>> SSI = %d <<<\n", (int)ssi)); 
        }
        break;

        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
        {
            float fDVBT_SSI_Pref[][5]=
            {
                {-93, -91, -90, -89, -88},
                {-87, -85, -84, -83, -82},
                {-82, -80, -78, -77, -76},
            };
            float   ch_power_db = 0.0f;
            float   ch_power_ref = 11.0f;
            float   ch_power_rel = 0.0f;
            MAPI_U8   tps_info_qam,tps_info_cr;

            DTV_GetIFAGCGain(&if_agc_gain);

            IIC_Bypass_Mode(true);

            pTuner = mapi_interface::Get_mapi_pcb()->GetDvbtTuner(0);
            if (pTuner!=NULL)
            {
                if ( MAPI_FALSE == pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_RF_LEVEL, 1, (MAPI_U32)if_agc_gain, &fRFLevel))
                {
                    fRFLevel=200.0f;
                }
                else
                {
                     // add for 1% SSI, remove if not necessary 
                    // fRFLevel += (((float)rand()/(float)RAND_MAX) -0.5f);
                } 
            }
            else
            {
                fRFLevel=200.0f;
                printf("%s Line:%d Error!!\n",__func__, __LINE__);
            }

            IIC_Bypass_Mode(false);

            ch_power_db = fRFLevel;// direct from tuner

            tps_info_qam = DTV_DVB_T_GetSignalModulation();// 0:QPSK, 1:16qam, 2:64qam 
            tps_info_cr = DTV_DVB_T_GetSignalCodeRate();//0: 1/2; 1:2/3 ; 2:3/4; 3: 5/6; 4: 7/8

            if (tps_info_qam < 3 && tps_info_cr < 5)
                ch_power_ref =  fDVBT_SSI_Pref[tps_info_qam][tps_info_cr];

            if (ch_power_ref > 10.0f)
                ssi =0;
            else
            {
                ch_power_rel = ch_power_db - ch_power_ref;

                if (ch_power_rel<-15.0)
                {
                    ssi = 0;
                }
                else if (ch_power_rel<0.0)
                {
                    ssi = (MAPI_U8)(((double)2.0/3)*(ch_power_rel+15.0));
                }
                else if (ch_power_rel<20.0)
                {
                    ssi = 4*ch_power_rel+10;
                }
                else if (ch_power_rel<35.0)
                {
                    ssi = (MAPI_U8)(((double)2.0/3)*(ch_power_rel-20.0)+90);
                }
                else
                {
                    ssi = 100;
                } 
            }
            DBG_GET_SIGNAL(printf(">>> SSI = %d <<<\n", (int)ssi));
        }
        break;

#if (DVBC_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_C:
        {
            float   ch_power_db=0.0f;
            float   ch_power_rel=0.0f;
            MAPI_U8     Qam_mode;

            DTV_GetIFAGCGain(&if_agc_gain);

            IIC_Bypass_Mode(true);

            pTuner = mapi_interface::Get_mapi_pcb()->GetDvbtTuner(0);
            if (pTuner!=NULL)
            {
                if ( MAPI_FALSE == pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_RF_LEVEL, 1, (MAPI_U32)if_agc_gain, &fRFLevel))
                {
                    fRFLevel=200.0f;
                }
                else
                {
                     // add for 1% SSI, remove if not necessary 
                    // fRFLevel += (((float)rand()/(float)RAND_MAX) -0.5f);
                } 
            }
            else
            {
                fRFLevel=200.0f;
                printf("%s Line:%d Error!!\n",__func__, __LINE__);
            }

            IIC_Bypass_Mode(false);

            ch_power_db = fRFLevel;// direct from tuner

            Qam_mode = DTV_DVB_C_GetSignalModulation();//0:16q,1:32q,2:64q,3:128q,4:256q

            if( (MAPI_U8)Qam_mode <= 4)//DMD_DVBC_QAM256
            {
#if (D_CUSTOMER == formula_A)  
                ch_power_rel = ch_power_db - intern_dvb_c_qam_ref_1[(MAPI_U8)Qam_mode];
#else 
                ch_power_rel = ch_power_db + intern_dvb_c_qam_ref[(MAPI_U8)Qam_mode];
#endif
            }
            else
            {
                ch_power_rel = -100.0f;
            }
#if (D_CUSTOMER == formula_A)
            if (ch_power_rel<-15.0)
            {
                ssi = 0;
            }
            else if (ch_power_rel<0.0)
            {
                ssi = (MAPI_U8)(((double)2.0/3)*(ch_power_rel+15.0));
            }
            else if (ch_power_rel<20.0)
            {
                ssi = 4*ch_power_rel+10;
            }
            else if (ch_power_rel<35.0)
            {
                ssi = (MAPI_U8)(((double)2.0/3)*(ch_power_rel-20.0)+90);
            }
            else
            {
                ssi = 100;
            } 
            
#elif (D_CUSTOMER == formula_B) 
            if(ch_power_rel <= -70.0f)
            {
                ssi = 0;
            }
            else if (ch_power_rel <= -67.0f)
            {
                ssi = (MAPI_U8)(0.0f + (ch_power_rel+70.0f)*3.0f/3.0f);
            }
            else if (ch_power_rel <= -62.0f)
            {
                ssi = (MAPI_U8)(3.0f + (ch_power_rel+67.0f)*10.0f/5.0f);
            }
            else if (ch_power_rel <= -50.0f)
            {
                ssi = (MAPI_U8)(13.0f + (ch_power_rel+62.0f)*60.0f/10.0f);
            }
            else if (ch_power_rel <= -40.0f)
            {
                ssi = (MAPI_U8)(85.0f + (ch_power_rel+50.0f)*9.0f/10.0f);
            }
            else if (ch_power_rel <= -25.0f)
            {
                ssi = (MAPI_U8)(94.0f + (ch_power_rel+40.0f)*6.0f/15.0f);
            }
            else
            {
                ssi = 100;
            }

#else // others...
            if(ch_power_rel <= -85.0f)
            {
                ssi = 0;
            }
            else if (ch_power_rel <= -80.0f)
            {
                ssi = (MAPI_U8)(0.0f + (ch_power_rel+85.0f)*10.0f/5.0f);
            }
            else if (ch_power_rel <= -75.0f)
            {
                ssi = (MAPI_U8)(10.0f + (ch_power_rel+80.0f)*20.0f/5.0f);
            }
            else if (ch_power_rel <= -70.0f)
            {
                ssi = (MAPI_U8)(30.0f + (ch_power_rel+75.0f)*30.0f/5.0f);
            }
            else if (ch_power_rel <= -65.0f)
            {
                ssi = (MAPI_U8)(60.0f + (ch_power_rel+70.0f)*10.0f/5.0f);
            }
            else if (ch_power_rel <= -55.0f)
            {
                ssi = (MAPI_U8)(70.0f + (ch_power_rel+65.0f)*20.0f/10.0f);
            }
            else if (ch_power_rel <= -45.0f)
            {
                ssi = (MAPI_U8)(90.0f + (ch_power_rel+55.0f)*10.0f/10.0f);
            }
            else
            {
                ssi = 100;
            }
            
#endif
            DBG_GET_SIGNAL(printf(">>> SSI_CH_PWR(dB) = %f<<<\n",ch_power_db));
            DBG_GET_SIGNAL(printf(">>> SSI = %d <<<\n", (int)ssi));
        }
        break;
#endif
#if (DVBS_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_S:
        {
            //GetSignalStrength
            MAPI_BOOL bRet=MAPI_TRUE;
            MAPI_U16 u16Address =0;
            MAPI_U16 u16Data =0;
            MAPI_U8  u8Data =0;
            // MAPI_U8  u8Index =0;
            float    rf_level_dbm = 0.0;
            MAPI_U16 u16_ssi = 0;
            
            if (MSB1240_DVBS_GetLock()==MAPI_FALSE)//Demod unlock
            {
                return 0;
            }
            
            u16Address=0x3E26;//
            bRet&=ReadReg(u16Address, &u8Data);//
            u8Data=(u8Data&0xF0)|0x03;//
            bRet&=WriteReg(u16Address, u8Data);//
            u16Address=0x3E05;//
            bRet&=ReadReg(u16Address, &u8Data);//
            u8Data|=0x80;//
            bRet&=WriteReg(u16Address, u8Data);//
            
            u16Address=0x3E29;
            bRet&=ReadReg(u16Address, &u8Data);
            u16Data=u8Data;
            u16Address=0x3E28;
            bRet&=ReadReg(u16Address, &u8Data);
            u16Data=(u16Data<<8)|u8Data;
            //printf("===========================Tuner u16Data = %d\n",u16Data);
            u16Address=0x3E05;
            bRet&=ReadReg(u16Address, &u8Data);
            u8Data&=~(0x80);
            bRet&=WriteReg(u16Address, u8Data);
            if (bRet==MAPI_FALSE)
            {
                printf(("MSB1240_DVBS_GetSignalStrength fail!!! \n"));
                return 0;
            }

            rf_level_dbm = MDrv_DVBS_Tuner_Get_RSSI(u16Data,1);

            if (rf_level_dbm > 0) u16_ssi = 10; // -1dBm
            else u16_ssi = (MAPI_U16)(-1.0*rf_level_dbm*10.0); 

            //GetPWR
            float  fCableLess;
            fCableLess = u16_ssi;
            
            //979MHz, 22Ms/s,DVB-S, QPSK¡B2/3 No Phose noise,No CN
            /*
                    //Add CN loss
                    printf("===========================fCableLess1 = %.2f\n",fCableLess);
            
                    if (fCableLess >= 160)
                        fCableLess = fCableLess + 10;
                    else if ((fCableLess < 160) && (fCableLess >= 140))
                        fCableLess = fCableLess;
                    else if ((fCableLess < 140) && (fCableLess >= 120))
                        fCableLess = fCableLess - 10;
                    else if ((fCableLess < 120) && (fCableLess >= 110))
                        fCableLess = fCableLess - 15;
                    else
                        fCableLess = fCableLess - 20;
                    */
            if (fCableLess >= 350)
                fCableLess = fCableLess - 35;
            else if ((fCableLess < 350) && (fCableLess >= 250))
                fCableLess = fCableLess - 25;
            else
                fCableLess = fCableLess - 5;
            
            if (fCableLess < 0)
                fCableLess = 0;
            if (fCableLess > 925)
                fCableLess = 925;
            
            //printf("===========================fCableLess2 = %.2f\n",fCableLess);
            fCableLess = (-1.0)*(fCableLess/10.0);

            //GetSignalBar
            MAPI_U8   u8Data2 = 0;
            MAPI_U8   _u8_DVBS2_CurrentConstellationLocal = 0, _u8_DVBS2_CurrentCodeRateLocal = 0;
            float   ch_power_db=0.0f, ch_power_db_rel=0.0f;

            ch_power_db = fCableLess;
            if (_bDemodType==MAPI_FALSE)//S
            {
                float fDVBS_SSI_Pref[]=
                {   //0,       1,       2,       3,       4
                    -78.9,   -77.15,  -76.14,  -75.19,  -74.57,//QPSK
                };
                u16Address = 0x1184;            //DVBSFEC_VITERBI_CODE_RATE
                bRet &= ReadReg(u16Address, &u8Data);
                _u8_DVBS2_CurrentCodeRateLocal = (u8Data & 0x07);
                ch_power_db_rel = ch_power_db - fDVBS_SSI_Pref[_u8_DVBS2_CurrentCodeRateLocal];
            
                //printf("[DVBS] power_ref_s1 = %f\n",fDVBS_SSI_Pref[_u8_DVBS2_CurrentCodeRateLocal]);
                //printf("[DVBS] ch_power_db_rel = %f\n",ch_power_db_rel);
            }
            else
            {
                float fDVBS2_SSI_Pref[][11]=
                {   //  0,    1,       2,       3,       4,       5,       6,       7,       8,        9,       10
                    //1/4,    1/3,     2/5,     1/2,     3/5,     2/3,     3/4,     4/5,     5/6,      8/9,     9/10
                    {-85.17, -84.08,  -83.15,  -81.86,  -80.63,  -79.77,  -78.84,  -78.19,  -77.69,   -76.68,  -76.46}, //QPSK
                    {   0.0,    0.0,     0.0,     0.0,  -77.36,  -76.24,  -74.95,     0.0,  -73.52,   -72.18,  -71.84}  //8PSK
                };
            
                u16Address = 0x24D7;
                bRet &= ReadReg(u16Address, &u8Data);
                _u8_DVBS2_CurrentCodeRateLocal = (u8Data & 0x3C)>>2;
            
                u16Address = 0x24D7;
                bRet &= ReadReg(u16Address, &u8Data);//INNER_PLSCDEC_DEBUG_OUT1
                u16Address = 0x24D6;
                bRet &= ReadReg(u16Address, &u8Data2);//INNER_PLSCDEC_DEBUG_OUT0
            
                if(((u8Data & 0x03)==0x01) && ((u8Data2 & 0x80)==0x00))
                {
                    _u8_DVBS2_CurrentConstellationLocal = DEMOD_SAT_QPSK_MSB1240;
                }
                else if (((u8Data & 0x03)==0x01) && ((u8Data2 & 0x80)==0x80))
                {
                    _u8_DVBS2_CurrentConstellationLocal = DEMOD_SAT_8PSK_MSB1240;//8PSK
                }
            
                ch_power_db_rel = ch_power_db - fDVBS2_SSI_Pref[_u8_DVBS2_CurrentConstellationLocal][_u8_DVBS2_CurrentCodeRateLocal];
            
                //printf("[DVBS2] power_ref_s2 = %f\n",fDVBS2_SSI_Pref[_u8_DVBS2_CurrentConstellationLocal][_u8_DVBS2_CurrentCodeRateLocal]);
                //printf("[DVBS2] ch_power_db_rel = %f\n",ch_power_db_rel);
                //printf("[DVBS2] _u8_DVBS2_CurrentConstellationLocal = %x ; _u8_DVBS2_CurrentCodeRateLocal=%x \n",_u8_DVBS2_CurrentConstellationLocal,_u8_DVBS2_CurrentCodeRateLocal);
            
            }
            if(ch_power_db_rel <= -15.0f)
                {ssi = 0;}
            else if (ch_power_db_rel <= 0.0f)
                {ssi = (MAPI_U16)(2.0f/3 * (ch_power_db_rel+15.0f));}
            else if (ch_power_db_rel <= 20.0f)
                {ssi = (MAPI_U16)(4.0f * ch_power_db_rel + 10.0f);}
            else if (ch_power_db_rel <= 35.0f)
                {ssi = (MAPI_U16)(2.0f/3 * (ch_power_db_rel-20.0f) + 90.0);}
            else
                {ssi = 100;}
            //printf(("MSB1240 DVBS Signal Bar %u\n", ssi));
        }
        break;
#endif
        default:
            ssi = 0;
            DBG_GET_SIGNAL(printf("Undefined!!!\n"));
        break;
    }
    return ssi;
}

MAPI_BOOL device_demodulator_extend::DTV_Serial_Control(MAPI_BOOL bEnable)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    mapi_scope_lock(scopeLock, &m_MutexOuter);

    MAPI_U8   status = MAPI_TRUE;
    MAPI_U8 u8Data = 0;

    status = ReadReg(0x0900 + (0x2d * 2) , &u8Data) ;
    if(status != MAPI_FALSE && u8Data != 0x00){
        if(bEnable){
            // Set TS[1]~TS[6] as GPIO_A3~GPIO_A8
            WriteReg(0x0900 + (0x2d * 2), 0x81);
            // Set GPIO_A3~GPIO_A8 as input
            WriteReg2bytes(0x0900+(0x61)*2, _BIT3| _BIT4|_BIT5|_BIT6|_BIT7|_BIT8);
        }
        else
        {
            // Set TS[0]~TS[7] as TS output
            WriteReg(0x0900 + (0x2d * 2), 0xFF);
        }
    }

    if (device_demodulator_extend::m_enCurrentDemodulator_Type == mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2)
    {
        T2_TS_SERIAL = (MAPI_U8)bEnable;
    }
    else if (device_demodulator_extend::m_enCurrentDemodulator_Type == mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T)
    {
        T_SERIAL_TS_VAL = (MAPI_U8)bEnable;
        MSB1240_DVBT_DSPREG_TABLE[3] = (MAPI_U8)bEnable;
    }
    else if (device_demodulator_extend::m_enCurrentDemodulator_Type == mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_C)
    {
        MSB1240_DVBC_DSPREG_TABLE[(MAPI_U8)C_config_ts_serial-(MAPI_U8)C_opmode_auto_scan_sym_rate] = (MAPI_U8)bEnable;
    }
    return MAPI_TRUE;
}

//##########################################################################################################
//##########################################################################################################
//########################################  Public:DTV-DVB-T Implementation ################################
//##########################################################################################################
//##########################################################################################################
mapi_demodulator_datatype::EN_LOCK_STATUS device_demodulator_extend::DTV_DVB_T_GetLockStatus(void)
{

//    MAPI_U8 u8_reg = 0;
//    float   fber = 0.0;
//    MAPI_U16 err = 0;
    // t2_mod_info t2_mod_info_a;
    //DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    mapi_scope_lock(scopeLock, &m_MutexOuter);
    mapi_demodulator_datatype::EN_LOCK_STATUS eLockStatus = mapi_demodulator_datatype::E_DEMOD_CHECKING;
     ExtendCmd(0xFF, 0, 0, (void *)&eLockStatus);
#if 0
    ReadReg(0x0900+(0x48)*2, &u8_reg);
    printf("[dvbt2]fsm=%d,",u8_reg);
    DTV_GetPreBER(&fber);
    printf("preber=%.3e, ",fber);
    DTV_GetPostBER(&fber);
    printf("postber=%.3e\n",fber);
    DTV_GetSNR(&fber);
    DTV_GetPacketErr(&err);
    MSB1240_demod_info(&t2_mod_info_a);
#else
    // ReadReg(0x1100+(0x70)*2, &u8_reg);
    // printf("[dvbt]fsm=%d \n,",u8_reg);
	// the following only porting T2
    //DTV_GetPreBER(&fber);
    //printf("preber=%.3e, ",fber);
    //DTV_GetPostBER(&fber);
    //printf("postber=%.3e\n",fber);
    //DTV_GetSNR(&fber);
   // DTV_GetPacketErr(&err);
    //MSB1240_demod_info(&t2_mod_info_a);
#endif
    return eLockStatus;
}

//##########################################################################################################
//##########################################################################################################
//########################################  Public:DTV-DVB-C Implementation ################################
//##########################################################################################################
//##########################################################################################################
#if (DVBC_SYSTEM_ENABLE == 1)
mapi_demodulator_datatype::EN_LOCK_STATUS device_demodulator_extend::DTV_DVB_C_GetLockStatus(void)
{
    //printf("%s(),%d\n",__FUNCTION__,__LINE__);
    mapi_demodulator_datatype::EN_LOCK_STATUS eLockStatus = mapi_demodulator_datatype::E_DEMOD_CHECKING;
    mapi_scope_lock(scopeLock, &m_MutexOuter);
    ExtendCmd(0xFF, 0, 0, (void *)&eLockStatus);
    return eLockStatus;
}

#if 0
/************************************************************************************************
Subject:    channel change config
Function:   MSB1240_Config
Parmeter:   BW: bandwidth
Return:     BOOLEAN :
Remark:
*************************************************************************************************/
//mick,For Parameter overwrite.
MAPI_U8 device_demodulator_extend::MSB1240_Config_DVBC(MAPI_U16 SymbolRate, MAPI_U32 u32IFFreq, MAPI_U16 bSpecInv)//0723 update
{
    MAPI_U8             reg_qam;
    MAPI_U8             reg_symrate_l;
    MAPI_U8             reg_symrate_h;

    //DBG_DEMOD_MSB(printf(" @MSB1240_dvbc_config=[%d][%d][%d]\n",SymbolRate,u32IFFreq,bSpecInv));

    reg_symrate_l = (MAPI_U8) (SymbolRate & 0xff);
    reg_symrate_h = (MAPI_U8) (SymbolRate >> 8);
    reg_qam = gQamVal;

    //// Reset Demod ///////////////////
    Reset();

    //// DSP Register Overwrite ///////////////////

    // Symbol Rate 4000~7000
    if( WriteDspReg(0x17+ 0x20, reg_symrate_l)!= TRUE)//Driver update 2009/11/20
    {
        DBG_DEMOD_MSB(printf(" @MSB1240_dvbc_config symbol L ng\n"));
        return MAPI_FALSE;
    }
    if( WriteDspReg(0x18+ 0x20, reg_symrate_h)!= TRUE)//Driver update 2009/11/20
    {
        DBG_DEMOD_MSB(printf(" @MSB1240_dvbc_config symbol H ng\n"));
        return MAPI_FALSE;
    }

    DBG_DEMOD_MSB(printf(" @MSB1240_dvbc_config QAM Mode=[%d]\n",reg_qam));
    // QAM mode
    if( WriteDspReg(0x21+ 0x20, reg_qam)!= TRUE)//Driver update 2009/11/20
    {
        DBG_DEMOD_MSB(printf(" @MSB1240_dvbc_config QAM ng\n"));
        return MAPI_FALSE;
    }

    DBG_DEMOD_MSB(printf(" @MSB1240_dvbc_config OK\n"));
    return  MAPI_TRUE;
}
/************************************************************************************************
//Subject   :
//Function  :   MSB1240_Set_Config_dvbc_auto (Auto SR+QAM or Manual SR+QAM)
//Parmeter  :
//Return    :
//Remark    :
*************************************************************************************************/
MAPI_U16 device_demodulator_extend::DTV_DVB_C_Set_Config_dvbc_auto (MAPI_U8 bAutoDetect)
{
    printf("%s(),%d\n",__FUNCTION__,__LINE__);
    MAPI_U8 status = MAPI_TRUE;

    if( WriteDspReg(C_opmode_auto_scan_sym_rate, bAutoDetect)!=MAPI_TRUE)
    {
        DBG_DEMOD_MSB(printf(" @MSB1240_Set_Config_dvbc_auto NG 1\n"));
    }
    if( WriteDspReg(C_opmode_auto_scan_qam, bAutoDetect)!=MAPI_TRUE)
    {
        DBG_DEMOD_MSB(printf(" @MSB1240_Set_Config_dvbc_auto NG 2\n"));
    }

    if(bAutoDetect)
    {
        status &= WriteDspReg(C_config_bw_l, C_BW_L);
        status &= WriteDspReg(C_config_bw_h, C_BW_H);
    }
    else
    {
        status &= WriteDspReg(C_config_bw_l, C_BW1_L);
        status &= WriteDspReg(C_config_bw_h, C_BW1_L);
    }

    if(status)
    {
        DBG_DEMOD_MSB(printf(" @MSB1240_Set_Config_dvbc_auto OK\n"));
        return  MAPI_TRUE;
    }
    else
    {
        DBG_DEMOD_MSB(printf(" @MSB1240_Set_Config_dvbc_auto NG 3\n"));
        return MAPI_FALSE;
    }

}


/************************************************************************************************
//Subject   :
//Function  : MSB1240_Set_Config_dvbc_atv_detector
//Parmeter  :
//Return    :
//Remark    : CCI detection enable and disable function.
*************************************************************************************************/
MAPI_U16 device_demodulator_extend::Set_Config_dvbc_atv_detector (MAPI_U8 bEnable)
{
    printf("%s(),%d\n",__FUNCTION__,__LINE__);
    if( WriteDspReg(0x0A+0x20, bEnable)!=0) //atv detector enable //Driver update 2010/09/28
    {
        DBG_DEMOD_MSB(printf(" @MSB1240_Set_Config_dvbc_atv_detector NG 1\n"));
        return MAPI_FALSE;
    }

    if(bEnable)
    {
        DBG_DEMOD_MSB(printf(" @MSB1240_Set_Config_dvbc_atv_detector ENABLE\n"));
    }
    else
    {
        DBG_DEMOD_MSB(printf(" @MSB1240_Set_Config_dvbc_atv_detector DISABLE\n"));
    }
    return  MAPI_TRUE;
}
#endif
/************************************************************************************************
//Subject   : To get the DVBC parameter
//Function  : MSB1240_GetDvbcInfo
//Parmeter  :
//Return    :
//Remark    :
*************************************************************************************************/

MAPI_U16 device_demodulator_extend::GetDvbcInfo( MAPI_U32 * DVBC_parameter)
{
    printf("%s(),%d\n",__FUNCTION__,__LINE__);
    MAPI_U8     u8Temp=0;
    MAPI_U16    dvb_c_info_temp=0;

    //if (MSB1240_Lock(E_DEVICE_DEMOD_DVB_C,COFDM_FEC_LOCK_DVBC) == FALSE)// FEC unlock
    //    return MAPI_FALSE;

    if (ReadReg(0x0C05, &u8Temp) == FALSE)
        return MAPI_FALSE;
    *DVBC_parameter = ((MAPI_U32)u8Temp) << 16;      //QAM (b23 ~ b16)

    DBG_DEMOD_MSB(printf("QAM Mode= %d\n",u8Temp));

    //symbole rate
    if (ReadReg(0x0C07, &u8Temp) == FALSE)
        return MAPI_FALSE;

    *DVBC_parameter |= ((MAPI_U32)u8Temp) << 8;         //Symbol Rate (b15 ~ b0) HIGH Byte
    DBG_DEMOD_MSB(printf("Symbol Rate H byte= %d\n",u8Temp));

    if (ReadReg(0x0C06, &u8Temp) == FALSE)
        return MAPI_FALSE;
    *DVBC_parameter |= (MAPI_U32)u8Temp;                    //Symbol Rate (b15 ~ b0) LOW Byte
    DBG_DEMOD_MSB(printf("Symbol Rate L byte= %d\n",u8Temp));

    dvb_c_info_temp = *DVBC_parameter&0xFFFF;
    DBG_DEMOD_MSB(printf("S7 DVBC symbol rate= %d\n",dvb_c_info_temp));
    dvb_c_info_temp = *DVBC_parameter/0x10000;

    switch(dvb_c_info_temp)
    {
        case 0:
            DBG_DEMOD_MSB(printf("S7 DVBC qam mode= 16 QAM\n"));
            break;

        case 1:
            DBG_DEMOD_MSB(printf("S7 DVBC qam mode= 32 QAM\n"));
            break;

        case 2:
            DBG_DEMOD_MSB(printf("S7 DVBC qam mode= 64 QAM\n"));
            break;

        case 3:
            DBG_DEMOD_MSB(printf("S7 DVBC qam mode= 128 QAM\n"));
            break;

        case 4:
            DBG_DEMOD_MSB(printf("S7 DVBC qam mode= 256 QAM\n"));
            break;

        default:
            DBG_DEMOD_MSB(printf("S7 DVBC qam mode= No information\n"));
            break;
    }

    return MAPI_TRUE;
}

/************************************************************************************************
//Subject   :
//Function  :   MSB1240_SetDvbcParam
//Parmeter  :
//Return        :
//Remark    :
*************************************************************************************************/
MAPI_U16 device_demodulator_extend::SetDvbcParam (MAPI_U8 constel)
{
    printf("%s(),%d\n",__FUNCTION__,__LINE__);
    gQamVal = constel;
    DBG_DEMOD_MSB(printf(" @@ MSB1240_SetDvbcParam : %d\n",gQamVal));
    return MAPI_TRUE;
}

/************************************************************************************************
//Subject   :
//Function  :   MSB1240_CCI_Check
//Parmeter  :
//Return        :
//Remark    :
*************************************************************************************************/
MAPI_U16 device_demodulator_extend::CCI_Check( COFDM_CHECK_FLAG eFlag )
{
    printf("%s(),%d\n",__FUNCTION__,__LINE__);
    MAPI_U16    u16Address = 0;
    MAPI_U8     cData=0;
    MAPI_U8     cBitMask = 0;
    MAPI_U8     cBitVal = 0;

    switch( eFlag )
    {
        case COFDM_CCI_FLAG:
            u16Address =  0x0C09;//0x20C4; //0917 CCI detect
            cBitMask = MBIT1;
            cBitVal = MBIT1;
            break;

        case CHECK_FLAG_MAX_NUM:
            // to remove compile warning.

        default:
            break;
    }

    if (ReadReg(u16Address, &cData) == FALSE)
        return FALSE;

    if ((cData & cBitMask) == cBitVal)
    {
        MAPI_U16    time_check; //fisher
        MAPI_U8     data1=0, data2=0;

        //MSB1240_ReadReg(0x20DB, &data1);
        //MSB1240_ReadReg(0x20DA, &data2);
        time_check = data1 << 8;
        time_check |= data2;
        DBG_DEMOD_MSB(printf("DVB-C CCI detect time :[%d]ms\n",time_check));
        return MAPI_TRUE;
    }
    else
    {
        DBG_DEMOD_MSB(printf("DVB-C NO CCI \n"));
        return MAPI_FALSE;
    }
}
#endif
//##########################################################################################################
//##########################################################################################################
//########################################  Private Function Implementation ################################
//##########################################################################################################
//##########################################################################################################

MAPI_BOOL device_demodulator_extend::I2C_CH_Reset(MAPI_U8 ch_num)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    //MAPI_U8         addr[4] = {0x00, 0x00, 0x00, 0x00};
    MAPI_U8         data[5] = {0x53, 0x45, 0x52, 0x44, 0x42};

    DBG_DEMOD_LOAD_I2C(printf("[msb1240][beg]I2C_CH_Reset, CH=0x%x\n",ch_num));
    mapi_scope_lock(scopeLock, &m_MutexRWReg);
    mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_1);

    // 8'hb2(SRID)->8,h53(PWD1)->8,h45(PWD2)->8,h52(PWD3)->8,h44(PWD4)->8,h42(PWD5)
    data[0] = 0x53;
    iptr->WriteBytes(0, NULL, 5, data);

    // 8'hb2(SRID)->8,h71(CMD)  //TV.n_iic_
    data[0] = 0x71;
    iptr->WriteBytes(0, NULL, 1, data);

    // 8'hb2(SRID)->8,h81(CMD)  //TV.n_iic_sel_b0
    data[0] = ((ch_num & 0x01) != 0)? 0x81 : 0x80;
    iptr->WriteBytes(0, NULL, 1, data);

    // 8'hb2(SRID)->8,h83(CMD)  //TV.n_iic_sel_b1
    data[0] = ((ch_num & 0x02) != 0)? 0x83 : 0x82;
    iptr->WriteBytes(0, NULL, 1, data);

    // 8'hb2(SRID)->8,h84(CMD)  //TV.n_iic_sel_b2
    data[0] = ((ch_num & 0x04) != 0)? 0x85 : 0x84;
    iptr->WriteBytes(0, NULL, 1, data);

    // 8'hb2(SRID)->8,h53(CMD)  //TV.n_iic_ad_byte_en2, 32bit read/write
    data[0] = 0x53;
    iptr->WriteBytes(0, NULL, 1, data);

    // 8'hb2(SRID)->8,h7f(CMD)  //TV.n_iic_sel_use_cfg
    data[0] = 0x7f;
    iptr->WriteBytes(0, NULL, 1, data);

/*
    // 8'hb2(SRID)->8,h35(CMD)  //TV.n_iic_use
    data[0] = 0x35;
    iptr->WriteBytes(0, NULL, 1, data);

    // 8'hb2(SRID)->8,h71(CMD)  //TV.n_iic_Re-shape
    data[0] = 0x71;
    iptr->WriteBytes(0, NULL, 1, data);
*/
    DBG_DEMOD_LOAD_I2C(printf("[msb1240][end]I2C_CH_Reset, CH=0x%x\n",ch_num));
    return MAPI_TRUE;
}

/*@ <Operation ID=I2b28dd03m121c8cf959bmm6ff4> @*/
MAPI_BOOL device_demodulator_extend::WriteReg(MAPI_U16 u16Addr, MAPI_U8 u8Data)
{
    //DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_BOOL bRet = MAPI_TRUE;

    if (g_u8_msb1240_sleep_mode_status > 1)
    {
        printf("%s, %s, %d, MSB1240 is sleeping, wake him up first\n",__FILE__,__FUNCTION__,__LINE__);
        return FALSE;
    }

    mapi_scope_lock(scopeLock, &m_MutexRWReg);

#if USE_SPI_LOAD_TO_SDRAM && (NEED_SWITCH_TSPAD_FOR_MSPI_USE == 0)
    bRet = MDrv_SS_RIU_Write8(u16Addr, u8Data);
#else
    mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_1);
#if (PRELOAD_DSP_CODE_FROM_MAIN_CHIP_I2C_ONLY_LOAD_T2 == 1)
    bRet &= iptr->SetSpeed(350);
#endif

    u8MsbData[0] = 0x10;
    u8MsbData[1] = 0x00;
    u8MsbData[2] = 0x00;
    u8MsbData[3] = (u16Addr >> 8) &0xff;
    u8MsbData[4] = u16Addr &0xff;
    u8MsbData[5] = u8Data;

    u8MsbData[0] = 0x35;
    bRet &= iptr->WriteBytes(0, NULL, 1, u8MsbData);

    u8MsbData[0] = 0x10;
    bRet &= iptr->WriteBytes(0, NULL, 6, u8MsbData);

    u8MsbData[0] = 0x34;
    bRet &= iptr->WriteBytes(0, NULL, 1, u8MsbData);
#endif

    return bRet;

}

MAPI_BOOL device_demodulator_extend::WriteRegs(MAPI_U16 u16Addr, MAPI_U8* u8pData, MAPI_U16 data_size)
{
    //DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_BOOL bRet = MAPI_TRUE;
    MAPI_U8   u8MsbDataValue[LOAD_CODE_I2C_BLOCK_NUM + 5];
    MAPI_U16   idx = 0;

    if (g_u8_msb1240_sleep_mode_status > 1)
    {
        printf("%s, %s, %d, MSB1240 is sleeping, wake him up first\n",__FILE__,__FUNCTION__,__LINE__);
        return FALSE;
    }

    mapi_scope_lock(scopeLock, &m_MutexRWReg);
    mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_1);
#if (PRELOAD_DSP_CODE_FROM_MAIN_CHIP_I2C_ONLY_LOAD_T2 == 1)
    bRet &= iptr->SetSpeed(350);
#endif
    u8MsbDataValue[0] = 0x10;
    u8MsbDataValue[1] = 0x00;
    u8MsbDataValue[2] = 0x00;
    u8MsbDataValue[3] = (u16Addr >> 8) &0xff;
    u8MsbDataValue[4] = u16Addr &0xff;
    // u8MsbDataValue[5] = 0x00;

    for(idx = 0; idx < data_size ; idx++)
    {
        u8MsbDataValue[5+idx] = u8pData[idx];
    }

    u8MsbDataValue[0] = 0x35;
    bRet &= iptr->WriteBytes(0, NULL, 1, u8MsbDataValue);

    u8MsbDataValue[0] = 0x10;
    bRet &= iptr->WriteBytes(0, NULL, 5 + data_size, u8MsbDataValue);

    u8MsbDataValue[0] = 0x34;
    bRet &= iptr->WriteBytes(0, NULL, 1, u8MsbDataValue);

    return bRet;
}

MAPI_BOOL device_demodulator_extend::WriteReg2bytes(MAPI_U16 u16Addr, MAPI_U16 u16Data)
{
    //DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));

    MAPI_BOOL ret = true;
#if USE_SPI_LOAD_TO_SDRAM && (NEED_SWITCH_TSPAD_FOR_MSPI_USE == 0)
    ret &= MDrv_SS_RIU_Write16(u16Addr, u16Data);
#else
    ret &= WriteReg(u16Addr, (MAPI_U8)u16Data&0x00ff);
    ret &= WriteReg(u16Addr + 0x0001, (MAPI_U8)(u16Data>>8)&0x00ff);
#endif
    return ret;
}

/*@ </Operation ID=I2b28dd03m121c8cf959bmm6ff4> @*/
/*@ <Operation ID=I2b28dd03m121c8cf959bmm6fcf> @*/
MAPI_BOOL device_demodulator_extend::ReadReg(MAPI_U16 u16Addr, MAPI_U8 *pu8Data)
{
    //DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_BOOL bRet = MAPI_TRUE;

    if (g_u8_msb1240_sleep_mode_status > 1)
    {
        printf("%s, %s, %d, MSB1240 is sleeping, wake him up first\n",__FILE__,__FUNCTION__,__LINE__);
        return FALSE;
    }

    mapi_scope_lock(scopeLock, &m_MutexRWReg);
#if USE_SPI_LOAD_TO_SDRAM && (NEED_SWITCH_TSPAD_FOR_MSPI_USE == 0)
    bRet = MDrv_SS_RIU_Read8(u16Addr, pu8Data);
#else
    mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_1);

    u8MsbData[0] = 0x10;
    u8MsbData[1] = 0x00;
    u8MsbData[2] = 0x00;
    u8MsbData[3] = (u16Addr >> 8) &0xff;
    u8MsbData[4] = u16Addr &0xff;

    u8MsbData[0] = 0x35;
    bRet &= iptr->WriteBytes(0, NULL, 1, u8MsbData);

    u8MsbData[0] = 0x10;
    bRet &= iptr->WriteBytes(0, NULL, 5, u8MsbData);
    bRet &= iptr->ReadBytes(0, NULL, 1, pu8Data);

    u8MsbData[0] = 0x34;
    bRet &= iptr->WriteBytes(0, NULL, 1, u8MsbData);
#endif

    return bRet;
}

MAPI_BOOL MDrv_SS_RIU_Write8(MAPI_U16 u16Addr, MAPI_U8 data)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_BOOL bRet = true;
    MAPI_U8 Wdata[4];

    Wdata[0] = RIU_W1_CMD;
    Wdata[1] = u16Addr & 0xFF;
    Wdata[2] = (u16Addr >> 8) & 0xFF;
    Wdata[3] = data;
    //printf("MDrv_SS_RIU_Write\n");

    // Write operation
    MDrv_MSPI_SlaveEnable(TRUE);
    
    // send write address & data
    MDrv_MSPI_Write(Wdata,4);
    
    MDrv_MSPI_SlaveEnable(FALSE);

    return bRet;
}
MAPI_BOOL MDrv_SS_RIU_Read8(MAPI_U16 u16Addr, MAPI_U8 *pdata)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_BOOL bRet = true;
    MAPI_U8 Rdata[5];
    
    Rdata[0] = RIU_R1T_CMD;
    Rdata[1] = u16Addr & 0xFF;
    Rdata[2] = (u16Addr >> 8) & 0xFF;
    Rdata[3] = 0x00;
    //printf("MDrv_SS_RIU_Read8\n");
    
    MDrv_MSPI_SlaveEnable(TRUE);
    // send read command to read data
    MDrv_MSPI_Write(Rdata,4);
    // read operation
    //printf("Read Operation\n");
    MDrv_MSPI_Read(pdata, 1);
    MDrv_MSPI_SlaveEnable(FALSE);

    return bRet;

}

MAPI_BOOL MDrv_SS_RIU_Write16(MAPI_U16 u16Addr, MAPI_U16 u16Data)
{
    //DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_BOOL ret = TRUE;
    MAPI_U8 Wdata[5];

    Wdata[0] = RIU_W_CMD;
    Wdata[1] = u16Addr & 0xFF;
    Wdata[2] = (u16Addr >> 8) & 0xFF;
    Wdata[3] = (MAPI_U8)(u16Data&0x00ff);
    Wdata[4] = (MAPI_U8)((u16Data>>8)&0x00ff);
   //printf("MDrv_SS_RIU_Write\n");

    // Write operation
    MDrv_MSPI_SlaveEnable(TRUE);
    
    // send write address & data
    MDrv_MSPI_Write(Wdata,5);
    
    MDrv_MSPI_SlaveEnable(FALSE);

    //ret &= MDrv_SS_RIU_Write8(u16Addr, (MAPI_U8)(u16Data&0x00ff));
    //ret &= MDrv_SS_RIU_Write8(u16Addr + 0x0001, (MAPI_U8)((u16Data>>8)&0x00ff));
    return ret;
}

MAPI_BOOL MDrv_SS_MIU_Writes(MAPI_U32 u32Addr, MAPI_U8 *pdata, MAPI_U16 u16Size)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_BOOL bRet = true;
    MAPI_U8 Wdata[5];
    
    Wdata[0] = MIU_W_CMD;
    Wdata[1] = u32Addr & 0xFF;
    Wdata[2] = (u32Addr >> 8) & 0xFF;
    Wdata[3] = (u32Addr >> 16)& 0xFF;
    Wdata[4] = (u32Addr >> 24);
    //printf("MDrv_SS_MIU_Writes\n");

    // Write operation
    MDrv_MSPI_SlaveEnable(TRUE);
    // send write address
    MDrv_MSPI_Write(Wdata,sizeof(Wdata));
    // send data
    MDrv_MSPI_Write(pdata,u16Size);
    MDrv_MSPI_SlaveEnable(FALSE);

    return bRet;
}

MAPI_BOOL MDrv_SS_MIU_Reads(MAPI_U32 u32Addr, MAPI_U8 *pdata, MAPI_U16 u16Size)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_BOOL bRet = true;
    MAPI_U8 Rdata[SPI_DEVICE_BUFFER_SIZE];
    MAPI_U16 dataLen, i, j=0;

    do
    {
        dataLen = (u16Size>16?16:u16Size);//(len>24?24:len);
        
        Rdata[0] = MIU_R_CMD;
        Rdata[1] = u32Addr & 0xFF;
        Rdata[2] = (u32Addr >> 8) & 0xFF;
        Rdata[3] = (u32Addr >> 16)& 0xFF;
        Rdata[4] = (u32Addr >> 24);
        Rdata[5] = dataLen+1;
        //printf("MDrv_SS_MIU_Reads, addr=0x%x, dataLen=%d\n", u32Addr, dataLen);

        // send read command to read data
        MDrv_MSPI_SlaveEnable(TRUE);
        MDrv_MSPI_Write(Rdata,6);
        MDrv_MSPI_SlaveEnable(FALSE);

        // read operation
        Rdata[0] = MIU_ST_CMD;
        MDrv_MSPI_SlaveEnable(TRUE);
        MDrv_MSPI_Write(Rdata,1);
        //printf("Read Operation\n");
        MDrv_MSPI_Read(Rdata, dataLen+1);
        MDrv_MSPI_SlaveEnable(FALSE);

        if(Rdata[0] != 0x0A)
        {
            ERR_DEMOD_MSB(printf("MDrv_SS_MIU_Reads fail, status=0x%x\n", Rdata[0] ));
            return false;
        }

        for (i=1; i<dataLen+1; i++, j++) 
        {
            pdata[j] = Rdata[i];
            //printf("%x, ", pdata[j]);
        }

        u16Size -= dataLen;
        u32Addr += dataLen;
        //printf("u16Size=%d,  u32Addr=0x%x\n", u16Size, u32Addr);
    }while(u16Size);

    return bRet;
}

MAPI_BOOL  device_demodulator_extend::WriteDspReg(MAPI_U16 u16Addr, MAPI_U8 u8Data)
{
    //DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_U8     status = true;
    MAPI_U8     cntl = 0x00;
    MAPI_U16    cntr = 0x00;

    if (g_u8_msb1240_sleep_mode_status > 1)
    {
        printf("%s, %s, %d, MSB1240 is sleeping, wake him up first\n",__FILE__,__FUNCTION__,__LINE__);
        return FALSE;
    }

    if (g_u8_msb1240_adc_mode_status != 0)
    {
        printf("%s, %s, %d, MSB1240 ADC is off, wake him up first\n",__FILE__,__FUNCTION__,__LINE__);
        return FALSE;
    }

    mapi_scope_lock(scopeLock, &m_MutexRWDspReg);

#if USE_SPI_LOAD_TO_SDRAM && (NEED_SWITCH_TSPAD_FOR_MSPI_USE == 0)
    status &= MDrv_SS_RIU_Write8(REG_MB_DATA, u8Data);
    //status &= MDrv_SS_RIU_Write16(REG_MB_ADDR_L, u16Addr);
    status &= MDrv_SS_RIU_Write8(REG_MB_ADDR_H, (MAPI_U8)(u16Addr >> 8));
    status &= MDrv_SS_RIU_Write8(REG_MB_ADDR_L,  (MAPI_U8)(u16Addr));
    status &= MDrv_SS_RIU_Write8(REG_MB_CNTL, 0x04);

    do
    {
        status &= MDrv_SS_RIU_Read8(REG_MB_CNTL, &cntl);
        if (cntr++ > MSB1240_MB_CNT_TH)
        {
            ERR_DEMOD_MSB(printf("MSB1240_MB_WRITE_FAILURE\n"));
            return FALSE;
        }
    }
    while(cntl != 0xff);
#else
    status &= WriteReg(REG_MB_DATA, u8Data);
    status &= WriteReg(REG_MB_ADDR_H, (MAPI_U8)(u16Addr >> 8));
    status &= WriteReg(REG_MB_ADDR_L, (MAPI_U8)(u16Addr));
    status &= WriteReg(REG_MB_CNTL, 0x04);

    do
    {
        status &= ReadReg(REG_MB_CNTL, &cntl);
        if (cntr++ > MSB1240_MB_CNT_TH)
        {
            ERR_DEMOD_MSB(printf("MSB1240_MB_WRITE_FAILURE\n"));
            return false;
        }
    }
    while(cntl != 0xff);
#endif

    return status;
}

MAPI_BOOL device_demodulator_extend::ReadDspReg(MAPI_U16 u16Addr, MAPI_U8* pData)
{
    //DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_U8     status = true;
    MAPI_U8     cntl = 0x00;
    MAPI_U16    cntr = 0x00;

    if (g_u8_msb1240_sleep_mode_status > 1)
    {
        printf("%s, %s, %d, MSB1240 is sleeping, wake him up first\n",__FILE__,__FUNCTION__,__LINE__);
        return FALSE;
    }

    if (g_u8_msb1240_adc_mode_status != 0)
    {
        printf("%s, %s, %d, MSB1240 ADC is off, wake him up first\n",__FILE__,__FUNCTION__,__LINE__);
        return FALSE;
    }

    mapi_scope_lock(scopeLock, &m_MutexRWDspReg);

#if USE_SPI_LOAD_TO_SDRAM && (NEED_SWITCH_TSPAD_FOR_MSPI_USE == 0)
    //status &= MDrv_SS_RIU_Write16(REG_MB_ADDR_L, u16Addr);
    status &= MDrv_SS_RIU_Write8(REG_MB_ADDR_H, (MAPI_U8)(u16Addr >> 8));
    status &= MDrv_SS_RIU_Write8(REG_MB_ADDR_L, (MAPI_U8)(u16Addr));
    status &= MDrv_SS_RIU_Write8(REG_MB_CNTL, 0x03);

    do
    {
        status &= MDrv_SS_RIU_Read8(REG_MB_CNTL, &cntl);
        if (cntr++ > MSB1240_MB_CNT_TH)
        {
            ERR_DEMOD_MSB(printf("MSB1240_MB_READ_FAILURE\n"));
            return FALSE;
        }
    }
    while(cntl != 0xff);

    status &= MDrv_SS_RIU_Read8(REG_MB_DATA, pData);
#else

    status &= WriteReg(REG_MB_ADDR_H, (MAPI_U8)(u16Addr >> 8));
    status &= WriteReg(REG_MB_ADDR_L, (MAPI_U8)(u16Addr));
    status &= WriteReg(REG_MB_CNTL, 0x03);

    do
    {
        status &= ReadReg(REG_MB_CNTL, &cntl);
        if (cntr++ > MSB1240_MB_CNT_TH)
        {
            ERR_DEMOD_MSB(printf("MSB1240_MB_READ_FAILURE\n"));
            return false;
        }
    }
    while(cntl != 0xff);
#endif

    status &= ReadReg(REG_MB_DATA, pData);
    return status;
}
/*
MAPI_BOOL device_demodulator_extend::Cmd_Packet_Send(S_CMDPKTREG *pCmdPacket, MAPI_U8 param_cnt)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));

    MAPI_U8 status = true, indx;
    //MAPI_U8             reg_val, timeout = 0;
    MAPI_U16 timeout = 0;
    MAPI_U8 reg_val = 0;

    if (g_u8_msb1240_sleep_mode_status != 0)
    {
        printf("%s, %s, %d, MSB1240 is sleeping, wake him up first\n",__FILE__,__FUNCTION__,__LINE__);
        return FALSE;
    }

    mapi_scope_lock(scopeLock, &m_MutexOuter);
    // ==== Command Phase ===================
    DBG_DEMOD_MSB(printf(">>>MSB1240: (cmd=0x%x)(0x%x,0x%x,0x%x,0x%x,0x%x,0x%x,) \n", pCmdPacket->cmd_code, pCmdPacket->param[0], pCmdPacket->param[1], pCmdPacket->param[2], pCmdPacket->param[3], pCmdPacket->param[4], pCmdPacket->param[5]));
    // wait _BIT_END clear
    do
    {
        status &= ReadReg(REG_CMD_CTRL, &reg_val);
         if((reg_val & _BIT_END) != _BIT_END)
            break;
        //MDrv_Timer_Delayms(5);
        usleep(5 *1000);
        if (timeout++ > 200)
        {
            DBG_DEMOD_MSB(printf(">>>MSB1240: MSB1240_Cmd_Packet_Send fail on 'wait _BIT_END clear' \n"));
            return false;
        }
    }
    while(1);

    // set cmd_3:0 and _BIT_START
    status &= ReadReg(REG_CMD_CTRL, &reg_val);
    reg_val = (reg_val &0x0f) | (pCmdPacket->cmd_code << 4) | _BIT_START;
    status &= WriteReg(REG_CMD_CTRL, reg_val);

    // wait _BIT_START clear
    do
    {
        status &= ReadReg(REG_CMD_CTRL, &reg_val);
         if((reg_val & _BIT_START) != _BIT_START)
            break;
        usleep(5 *1000);
        if (timeout++ > 200)
        {
            DBG_DEMOD_MSB(printf(">>>MSB1240: MSB1240_Cmd_Packet_Send fail on 'wait _BIT_START clear' \n"));
            return false;
        }
    }
    while(1);

    // ==== Data Phase ======================
    status &= WriteReg(REG_CMD_ADDR, 0x00);
    for (indx = 0; indx < param_cnt; indx++)
    {
        status &= ReadReg(REG_CMD_ADDR, &reg_val);
        DBG_DEMOD_MSB(printf("demod_config: param[%d] = %x\n", reg_val, pCmdPacket->param[indx]));
        // set param[indx] and _BIT_DRQ
        status &= WriteReg(REG_CMD_DATA, pCmdPacket->param[indx]);
        status &= ReadReg(REG_DTA_CTRL, &reg_val);
        status &= WriteReg(REG_DTA_CTRL, reg_val | _BIT_DRQ);

        // wait _BIT_DRQ clear
        do
        {
            status &= ReadReg(REG_DTA_CTRL, &reg_val);
             if ((reg_val & _BIT_DRQ) != _BIT_DRQ)
                break;
            usleep(5 *1000);
            if (timeout++ > 200)
            {
                DBG_DEMOD_MSB(printf(">>>MSB1240: MSB1240_Cmd_Packet_Send fail on 'wait _BIT_DRQ clear' \n"));
                return false;
            }
        }
        while(1);
    }

    // ==== End Phase =======================
    // set _BIT_END to finish command
    status &= ReadReg(REG_CMD_CTRL, &reg_val);
    status &= WriteReg(REG_CMD_CTRL, reg_val | _BIT_END);
    return status;
}
*/
/************************************************************************************************
Subject:    Set demodulator type
Function:   SetCurrentDemodulatorType
Parmeter:   enDemodType
Return:     BOOLEAN :
*************************************************************************************************/

MAPI_BOOL device_demodulator_extend::SetCurrentDemodulatorType(mapi_demodulator_datatype::EN_DEVICE_DEMOD_TYPE enDemodType)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));

    printf("device_demodulator_msb1240: SetCurrentDemodulatorType %d\n", (int)enDemodType);
#if (PRELOAD_DSP_CODE_FROM_MAIN_CHIP_I2C == 1)
    if(enDemodType == device_demodulator_extend::m_enCurrentDemodulator_Type)
    {
        return MAPI_TRUE;
    }
#endif
    u32ChkScanTimeStart = MsOS_GetSystemTime();
    switch(enDemodType)
    {
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2:
            device_demodulator_extend::m_enCurrentDemodulator_Type = enDemodType;
            break;
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
            device_demodulator_extend::m_enCurrentDemodulator_Type = enDemodType;
            break;
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_C://mick
            device_demodulator_extend::m_enCurrentDemodulator_Type = enDemodType;
            break;
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_S:
            device_demodulator_extend::m_enCurrentDemodulator_Type = enDemodType;
            break;
        default:
            return MAPI_FALSE;
    }
#if (PRELOAD_DSP_CODE_FROM_MAIN_CHIP_I2C == 1)
    if (bPower_init_en != TRUE)
    {
        return MAPI_TRUE;
    }
#endif
    if (LoadDSPCode() == MAPI_FALSE)
    {

        ERR_DEMOD_MSB(printf(">>>>MSB1240:Fail\n"));
        return MAPI_FALSE;
    }
    else
    {
        DBG_DEMOD_MSB(printf(">>>MSB1240:OK\n"));
    }


    return MAPI_TRUE;
}

/************************************************************************************************
Subject:    Get PLP-ID Bit Map
Function:   DTV_GetPlpBitMap
Parmeter:   u32PlpBitMap
Return:     BOOLEAN :
Remark:     32 byte of data to show PLP-ID existense
            |00000000|00000000|...|00000000|00011111|11111111|
             byte31   byte30   ... byte2    byte1    byte0   => 256 bit in total
*************************************************************************************************/

MAPI_BOOL device_demodulator_extend::DTV_GetPlpBitMap(MAPI_U8* u8PlpBitMap)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_BOOL   status = MAPI_TRUE;
    MAPI_U8     u8Data = 0;
    MAPI_U8     indx = 0;
    MAPI_U8     u8_acc  = 0;

    status &= ReadDspReg(E_T2_L1_FLAG, &u8Data);     // check L1 ready
    if (u8Data != 0x30)
        return MAPI_FALSE;

    while(indx < 32)
    {
        status &= ReadDspReg(E_T2_PLP_ID_ARR+indx, &u8Data);
        u8PlpBitMap[indx] = u8Data;
        // printf("=========================>[GetPlpBitMap] u8PlpBitMap[%d] = 0x%x\n", indx, u8Data);
        indx++;
        if (u8_acc == 0)
        {
            u8_acc = u8Data;
        }         
    }
    if (u8_acc == 0)
    {
        MAPI_U8 u8_plp_id = 0;
        MAPI_U8 u8_group_id = 0;
        MAPI_U8 u8_index = 0;
        MAPI_U8 u8_mod = 0;
        
        // SPLP with common case.
        // get current PLP_ID and GROUP_ID
        ReadReg(0x2700+0x45*2, &u8_plp_id);
        ReadReg(0x2700+0x46*2+1, &u8_group_id);

        u8_index = u8_plp_id/8;
        u8_mod = u8_plp_id%8;
        if (u8_index < 32)
        {
            u8PlpBitMap[u8_index]=(1<<u8_mod);
        }
        DBG_DEMOD_MSB(printf("[msb1240][SPLP with Common]Trigger!!!!!!!, plp_id=%d, group_id=%d, u8_index=%d,u8_mod=%d\n",u8_plp_id,u8_group_id,u8_index,u8_mod));
    }
    return status;
}

/************************************************************************************************
Subject:    Get GROUP_ID upon PLP_ID for MPLP mode
Function:   DTV_GetPlpGroupID
Parmeter:   u8PlpID, u8GroupID
Return:     BOOLEAN :
Remark:
*************************************************************************************************/

MAPI_BOOL device_demodulator_extend::DTV_GetPlpGroupID(MAPI_U8 u8PlpID, MAPI_U8* u8GroupID)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_BOOL   status = MAPI_TRUE;
    MAPI_U8 u8Data = 0;
    status &= ReadDspReg(E_T2_L1_FLAG, &u8Data);         // check L1 ready
    if (u8Data != 0x30)
    {
        ERR_DEMOD_MSB(printf("[msb1240]dvbt2 L1 not ready yet\n"));
        return MAPI_FALSE;
    }
    ReadDspReg(E_T2_DVBT2_LOCK_HIS, &u8Data);

    if ((u8Data & MBIT7) == 0x00)
    {
        ERR_DEMOD_MSB(printf("[msb1240]dvbt2 is un-lock\n"));
        return MAPI_FALSE;
    }
    // assign PLP-ID value
    status &= WriteReg(0x2700+(0x78)*2, u8PlpID);
    status &= WriteReg(0x2700+(0x01)*2 + 1, 0x01);  // MEM_EN
    usleep(1000);
    status &= ReadReg(0x2700+(0x79)*2, u8GroupID);
    status &= WriteReg(0x2700+(0x01)*2 + 1, 0x00);  // ~MEM_EN
//  printf("=========================>[GetPlpGroupID] PlpID = %d, GroupID = %d\n", u8PlpID, *u8GroupID);

    return status;
}

/************************************************************************************************
Subject:    Select PLP_ID, GROUP_ID for MPLP mode
Function:   DTV_SetPlpGroupID
Parmeter:   u8PlpID, u8GroupID
Return:     BOOLEAN :
Remark:
*************************************************************************************************/

MAPI_BOOL device_demodulator_extend::DTV_SetPlpGroupID(MAPI_U8 u8PlpID, MAPI_U8 u8GroupID)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n", __func__, __LINE__));

    MAPI_BOOL   status = MAPI_TRUE;
    FECLock = MAPI_FALSE;
    u32ChkScanTimeStart = MsOS_GetSystemTime();

    g_msb1240_plp_id = u8PlpID;
    g_msb1240_group_id = u8GroupID;


    // assign Group-ID and PLP-ID value (must be written in order)
    // status &= WriteDspReg(0x0122, u8GroupID);
    // status &= WriteDspReg(0x0121, u8PlpID);

    return status;
}

// MAPI_BOOL device_demodulator_extend::DTV_SetPlpGroupID(MAPI_U8 u8PlpID, MAPI_U8 u8GroupID)
// {
//     MAPI_BOOL   status = MAPI_TRUE;
//     // assign PLP-ID and Group-ID value
//     status &= WriteReg(0x0900+(0x4f)*2, u8PlpID);
//     status &= WriteReg(0x0900+(0x4f)*2 + 1, u8GroupID);
//
//     // cmd to change PLP
//     status &= WriteReg(0x0900+(0x4e)*2, 0xaa);
//     status &= WriteReg(0x0900+(0x4e)*2 + 1, 0x55);
//     u32ChkScanTimeStart = MsOS_GetSystemTime();
//     return status;
// }

/*@ </Operation ID=Im17018142m1221763cc7cmm45a6> @*/
/************************************************************************************************
Subject:    channel change config
Function:   MSB1240_Config
Parmeter:   BW: bandwidth
Return:     BOOLEAN :
Remark:
*************************************************************************************************/
/*
MAPI_BOOL device_demodulator_extend::DTV_DVB_T_Config(RF_CHANNEL_BANDWIDTH BW, MAPI_BOOL bSerialTS, MAPI_BOOL bPalBG)
{
    U8              bandwidth;
//  U8              status = MAPI_TRUE;
    static B16      reg_force = 0;
    static B16      reg_set_lp = 0;
    static B16      reg_auto_scan = 0;

    //printf(" @MSB1240_config_dvbt start = [%d][%d][%d][%d][%d][%d]\n",u16ChBw,bSetLp,bForce,bAutoScan,u16FFTMode,u16CP);

    switch(u16ChBw)
    {
        case 6000:
            bandwidth = 1;
            break;
        case 7000:
            bandwidth = 2;
            break;
        case 8000:
        default:
            bandwidth = 3;
            break;
    }

    Reset();

    //// DSP Register Overwrite ///////////////////

    // Bandwidth: 0:5MHz, 1:6MHz, 2:7MHz, 3:8MHz
    if( WriteDspReg(0x17, bandwidth)!= 0)//Driver update 2009/11/20
    {
        printf(" @MSB1240_Config BW seting NG\n");
        return MAPI_FALSE;
    }
    if (reg_set_lp != bSetLp)
    {
        reg_set_lp = bSetLp;// andy 2009-12-15  8:03:16
        // LP select: 0: select HP, 1: setlect LP
        if( WriteDspReg(0x1A, bSetLp)!= 0)//Driver update 2009/11/20
        {
            printf(" @MSB1240_Config LP seting NG\n");
            return MAPI_FALSE;
            }
        }

        if (bForce != reg_force)
        {
        reg_force = bForce;

        // mode-cp force: 0: auto, 1: force
        if( WriteDspReg(0x0A, (U8)bForce)!= 0)//Driver update 2009/11/20
        {
            printf(" @MSB1240_Config MODE-CP seting NG\n");
            return MAPI_FALSE;
        }
        // FFT mode - 0:2K, 1:8K
        if( WriteDspReg(0x18, (U8)u16FFTMode)!= 0)//Driver update 2009/11/20
        {
            printf(" @MSB1240_Config FFT seting NG\n");
            return MAPI_FALSE;
        }
        // CP - 0:1/32, 1/16, 1/8, 1/4
        if( WriteDspReg(0x19, (U8)u16CP)!= 0)//Driver update 2009/11/20
        {
            printf(" @MSB1240_Config CP seting NG\n");
            return MAPI_FALSE;
        }
    }

    if (bAutoScan != reg_auto_scan)
    {
        reg_auto_scan = bAutoScan;

        // Auto Scan - 0:channel change, 1:auto-scan
        if( WriteDspReg(0x0C, (U8)bAutoScan)!= 0)//Driver update 2009/11/20
        {
            printf(" @MSB1240_Config auto mode seting NG\n");
            return MAPI_FALSE;
        }
    }

    printf(" @MSB1240_Config OK\n");
    return  MAPI_TRUE;
}
*/

void device_demodulator_extend::Driving_Control(MAPI_BOOL bEnable)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_U8 u8Temp;
    ReadReg(0x0958, &u8Temp);
    if (bEnable)
    {
        u8Temp = 0xFF;
    }
    else
    {
        u8Temp = 0x00;
    }
    WriteReg(0x0958, u8Temp);

    ReadReg(0x0959, &u8Temp);
    if (bEnable)
    {
        u8Temp = u8Temp | 0x0F;
    }
    else
    {
        u8Temp = u8Temp &(~0x0F);
    }
    WriteReg(0x0959, u8Temp);
}

MAPI_U16 device_demodulator_extend::DTV_GetCellID()
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_U8 id=0;
    MAPI_U8 status = MAPI_TRUE;
    MAPI_U16  cell_id  = 0;
    mapi_scope_lock(scopeLock, &m_MutexOuter);
    if (device_demodulator_extend::m_enCurrentDemodulator_Type == mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T2)
    {
        status &= ReadReg(0x2700+(0x38)*2+1, &id);
        cell_id = (MAPI_U16)id;

        status &= ReadReg(0x2700+(0x38)*2, &id);
        cell_id = (MAPI_U16)((cell_id << 8)|id);
    }
    else if ((device_demodulator_extend::m_enCurrentDemodulator_Type == mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T)
            || (device_demodulator_extend::m_enCurrentDemodulator_Type == mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_C))
    {
        status &= WriteReg(0x0ffe, 0x01);

        status &= ReadReg(0x0f2b, &id);
        cell_id = (MAPI_U16)id;

        status &= ReadReg(0x0f2a, &id);
        cell_id |= (MAPI_U16)id << 8;

        status &= WriteReg(0x0ffe, 0x00);
    }
    
    if(status)
    {
        printf(" @MSB1240_GetCellId OK\n");
    }
    else
    {
        printf(" @MSB1240_GetCellId NG\n");
        return MAPI_FALSE;
    }
    return cell_id;
}

#if 0
MAPI_U16 device_demodulator_extend::MSB1240_Lock(E_SYSTEM system, COFDM_LOCK_STATUS eStatus )
{
    printf("%s(),%d\n",__FUNCTION__,__LINE__);
    MAPI_U16        u16Address = 0;
    MAPI_U8         cData = 0;
    MAPI_U8         cBitMask = 0;
    MAPI_U16        Ret = 0;
    MAPI_U8         use_dsp_reg = 0;//addy update 0805

    if(system == E_DEVICE_DEMOD_DVB_C)
    {
        //MSB1228_Check_StepLock(E_SYS_DVBC);
    }
    else if(system == E_DEVICE_DEMOD_DVB_T)
    {
        //MSB1228_Check_StepLock(E_SYS_DVBT);
    }
    else if(system == E_DEVICE_DEMOD_DVB_T2)
    {
        //MSB1228_Check_StepLock(E_SYS_DVBT);
    }

    switch( eStatus )
    {
        //############################ DVB-C ###############################
        case COFDM_FEC_LOCK_DVBC:
            //Update reg information
            ReadReg(0x11E0, &cData);//addy update 0805
            if (cData == 0x0C)
            {
                Ret = TRUE;
            }
            else
            {
                Ret = FALSE;
            }

        return Ret;

        break;

        case COFDM_TR_LOCK_DVBC://addy update 0805
            u16Address =  0x2250; //DVB-C : TR Lock,
            cBitMask = MBIT0;

        break;
        //############################ DVB-T ###############################
        case COFDM_FEC_LOCK_DVBT:
            //Update reg information
            ReadReg(0x11E0, &cData);//addy update 0805
            if (cData == 0x0B)
            {
                return TRUE;
            }
            else
                return FALSE;

        break;

        case COFDM_PSYNC_LOCK:
            u16Address =  0x112C; //FEC: P-sync Lock,ok
            cBitMask = MBIT1;

            break;

        case COFDM_TPS_LOCK:
            u16Address =  0x0f22; //TPS Lock,ok
            cBitMask = MBIT1;

            break;
        case COFDM_TPS_LOCK_HISTORY:
            // change to use dsp reg ok
            use_dsp_reg = 1;
            u16Address =  0x00F0; //TPS lock history,// andy 2009-9-28  7:20:03
            cBitMask = MBIT3;

            break;

        case COFDM_DCR_LOCK:
            u16Address =  0x3E45; //DCR Lock,ok
            cBitMask = MBIT0;
            break;

        case COFDM_AGC_LOCK:
            u16Address =  0x3E2F; //AGC Lock,ok
            cBitMask = MBIT0;

            break;

        case COFDM_MODE_DET:
            u16Address =  0x0ECF; //Mode CP Detect,ok
            cBitMask = MBIT4;

            break;

        case COFDM_LOCK_STABLE_DVBT: //0923 update
            // change to use dsp reg
            use_dsp_reg = 1;
            u16Address =  0x00F0; //Lock Stable,
            cBitMask = MBIT6;

            break;

        case COFDM_SYNC_LOCK_DVBT://addy 0629
            // change to use dsp reg
            use_dsp_reg = 1;
            u16Address =  0x00F0; //Valid sync,
            cBitMask = MBIT2;

            break;

        case COFDM_FAST_LOCK_DVBT://addy 0920
            // change to use dsp reg
            use_dsp_reg = 1;
            u16Address =  0x00F0; //Valid sync,
            cBitMask = MBIT5;

            break;

        //############################ DVB-T2 #############################
        case COFDM_P1_LOCK:
            u16Address =  0x2482; //Pl lock,
            cBitMask = MBIT3;
            break;

        case COFDM_P1_LOCK_HISTORY:
            use_dsp_reg = 1;
            u16Address =  E_T2_DVBT2_LOCK_HIS; //Pl ever lock,
            cBitMask = MBIT3;
            break;

        case COFDM_L1_CRC_LOCK:
            u16Address =  0x2741; //L1 CRC check,
            cBitMask = MBIT5 | MBIT6 | MBIT7;
            break;

        case COFDM_FEC_LOCK_T2:
            #if 0
            {
                U16 u16RegAddress;
                U8 u8Data;
                u16RegAddress=0x0900+(0x48)*2;
                ReadReg(u16RegAddress, &u8Data);

                DBG_DEMOD_MSB1240(printf(">>>MSB1240:DVB-T2 [%s] Lock Status = 0x%x\n", __FUNCTION__, u8Data));

                if(u8Data == 0x09)
                    Ret = TRUE;
                else
                    Ret = FALSE;

                return Ret;
            }
            #else
            use_dsp_reg = 1;
            u16Address =  E_T2_DVBT2_LOCK_HIS; //FEC lock,
            cBitMask = MBIT7;
            #endif
            break;


        default:
            return FALSE;
    }

    //addy update 0805
    if (!use_dsp_reg)
    {
        if (ReadReg(u16Address, &cData) == FALSE)
        {
            return FALSE;
        }
    }
    else
    {
        if (ReadDspReg(u16Address, &cData) == FALSE)
        {
            return FALSE;
        }
    }


    if ((cData & cBitMask) == cBitMask)
    {
        return TRUE;
    }

    return FALSE;
}
#endif

MAPI_BOOL device_demodulator_extend::DTV_IsHierarchyOn(void)
{
    return FALSE;
}

MAPI_BOOL msb1240_flash_mode_en(void)
{
    MAPI_BOOL  retb = TRUE;
    MAPI_U8    data[5] = {0x53, 0x45, 0x52, 0x44, 0x42};
    MAPI_U8    u8MsbData[6] = {0};
    MAPI_U8    ch_num  = 3;
    MAPI_U8    u8Data  = 0;
    MAPI_U16   u16Addr = 0;
    MAPI_U8    retry_num = MSB1240_MAX_FLASH_ON_RETRY_NUM;

    DBG_DEMOD_LOAD_I2C(printf("[msb1240][beg]msb1240_flash_mode_en\n"));

    mapi_scope_lock(scopeLock, &m_MutexRWReg);
    mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_1);

    do{

        if (retry_num != MSB1240_MAX_FLASH_ON_RETRY_NUM)
        {
            ERR_DEMOD_MSB(printf("[msb1240][error]flash mode en fail.....retry=%d\n",retry_num));
        }
        // retb = TRUE;
        // password
        // 8'hb2(SRID)->8,h53(PWD1)->8,h45(PWD2)->8,h52(PWD3)->8,h44(PWD4)->8,h42(PWD5)
        data[0] = 0x53;
        retb &= iptr->WriteBytes(0, NULL, 5, data);

        // 8'hb2(SRID)->8,h71(CMD)  //TV.n_iic_
        data[0] = 0x71;
        retb &= iptr->WriteBytes(0, NULL, 1, data);

        // 8'hb2(SRID)->8,h81(CMD)  //TV.n_iic_sel_b0
        data[0] = ((ch_num & 0x01) != 0)? 0x81 : 0x80;
        retb &= iptr->WriteBytes(0, NULL, 1, data);

        // 8'hb2(SRID)->8,h83(CMD)  //TV.n_iic_sel_b1
        data[0] = ((ch_num & 0x02) != 0)? 0x83 : 0x82;
        retb &= iptr->WriteBytes(0, NULL, 1, data);

        // 8'hb2(SRID)->8,h84(CMD)  //TV.n_iic_sel_b2
        data[0] = ((ch_num & 0x04) != 0)? 0x85 : 0x84;
        retb &= iptr->WriteBytes(0, NULL, 1, data);

        // 8'hb2(SRID)->8,h53(CMD)  //TV.n_iic_ad_byte_en2, 32bit read/write
        data[0] = 0x53;
        retb &= iptr->WriteBytes(0, NULL, 1, data);

        // 8'hb2(SRID)->8,h7f(CMD)  //TV.n_iic_sel_use_cfg
        data[0] = 0x7f;
        retb &= iptr->WriteBytes(0, NULL, 1, data);

    /*
        // 8'hb2(SRID)->8,h35(CMD)  //TV.n_iic_use
        data[0] = 0x35;
        retb &= iptr->WriteBytes(0, NULL, 1, data);

        // 8'hb2(SRID)->8,h71(CMD)  //TV.n_iic_Re-shape
        data[0] = 0x71;
        retb &= iptr->WriteBytes(0, NULL, 1, data);
    */
        retb = TRUE;

        // confirm first, 0x99 and 0xaa.
        // beg read register
        u16Addr = 0x0900+(0x4f<<1);
        u8Data = 0x0;

        u8MsbData[0] = 0x10;
        u8MsbData[1] = 0x00;
        u8MsbData[2] = 0x00;
        u8MsbData[3] = (u16Addr >> 8) &0xff;
        u8MsbData[4] = u16Addr &0xff;

        u8MsbData[0] = 0x35;
        retb &= iptr->WriteBytes(0, NULL, 1, u8MsbData);

        u8MsbData[0] = 0x10;
        retb &= iptr->WriteBytes(0, NULL, 5, u8MsbData);
        retb &= iptr->ReadBytes(0, NULL, 1, &u8Data);

        u8MsbData[0] = 0x34;
        retb &= iptr->WriteBytes(0, NULL, 1, u8MsbData);
        // end read register

        if ((u8Data == 0x99) || (u8Data == 0xaa))
        {
            ERR_DEMOD_MSB(printf("[msb1240][warning]flash is already on....\n"));
            break;
        }
        // flash mode enable.
        // beg read register
        u16Addr = 0x0900+(0x28<<1)+1;
        u8Data = 0x0;

        u8MsbData[0] = 0x10;
        u8MsbData[1] = 0x00;
        u8MsbData[2] = 0x00;
        u8MsbData[3] = (u16Addr >> 8) &0xff;
        u8MsbData[4] = u16Addr &0xff;

        u8MsbData[0] = 0x35;

        retb &= iptr->WriteBytes(0, NULL, 1, u8MsbData);



        u8MsbData[0] = 0x10;
        retb &= iptr->WriteBytes(0, NULL, 5, u8MsbData);
        retb &= iptr->ReadBytes(0, NULL, 1, &u8Data);

        u8MsbData[0] = 0x34;
        retb &= iptr->WriteBytes(0, NULL, 1, u8MsbData);
        // end read register

        // beg write register
        u16Addr = 0x0900+(0x28<<1) + 1;
        u8Data &= (0xff-0x01);

        u8MsbData[0] = 0x10;
        u8MsbData[1] = 0x00;
        u8MsbData[2] = 0x00;
        u8MsbData[3] = (u16Addr >> 8) &0xff;
        u8MsbData[4] = u16Addr &0xff;
        u8MsbData[5] = u8Data;

        u8MsbData[0] = 0x35;
        retb &= iptr->WriteBytes(0, NULL, 1, u8MsbData);

        u8MsbData[0] = 0x10;
        retb &= iptr->WriteBytes(0, NULL, 6, u8MsbData);

        u8MsbData[0] = 0x34;
        retb &= iptr->WriteBytes(0, NULL, 1, u8MsbData);
        // end write register

        // beg write register
        u16Addr = 0x0900+(0x06<<1);
        u8Data = 0x10;

        u8MsbData[0] = 0x10;
        u8MsbData[1] = 0x00;
        u8MsbData[2] = 0x00;
        u8MsbData[3] = (u16Addr >> 8) &0xff;
        u8MsbData[4] = u16Addr &0xff;
        u8MsbData[5] = u8Data;

        u8MsbData[0] = 0x35;
        retb &= iptr->WriteBytes(0, NULL, 1, u8MsbData);

        u8MsbData[0] = 0x10;
        retb &= iptr->WriteBytes(0, NULL, 6, u8MsbData);

        u8MsbData[0] = 0x34;
        retb &= iptr->WriteBytes(0, NULL, 1, u8MsbData);
        // end write register

        // beg write register

        u16Addr = 0x0900+(0x07<<1);
        u8Data = 0x10;

        u8MsbData[0] = 0x10;
        u8MsbData[1] = 0x00;
        u8MsbData[2] = 0x00;
        u8MsbData[3] = (u16Addr >> 8) &0xff;
        u8MsbData[4] = u16Addr &0xff;
        u8MsbData[5] = u8Data;

        u8MsbData[0] = 0x35;
        retb &= iptr->WriteBytes(0, NULL, 1, u8MsbData);

        u8MsbData[0] = 0x10;
        retb &= iptr->WriteBytes(0, NULL, 6, u8MsbData);

        u8MsbData[0] = 0x34;
        retb &= iptr->WriteBytes(0, NULL, 1, u8MsbData);
    }while( (retb == FALSE) && (retry_num-- != 0));
    // end write register

    DBG_DEMOD_LOAD_I2C(printf("[msb1240][end]msb1240_flash_mode_en,retb=%d\n",retb));

    return retb;
}

MAPI_BOOL msb1240_flash_boot_ready_waiting(MAPI_U8 *ptimeout)
{

    MAPI_BOOL  retb = TRUE;
//MAPI_U8    data[5] = {0x53, 0x45, 0x52, 0x44, 0x42};
    MAPI_U8    u8MsbData[6] = {0};
    MAPI_U8    u8Data  = 0;
    MAPI_U16   u16Addr = 0;
    MAPI_U8    u8_timeout = 0;

    DBG_DEMOD_LOAD_I2C(printf("[msb1240][beg]msb1240_flash_boot_ready_waiting\n"));

    mapi_scope_lock(scopeLock, &m_MutexRWReg);
    mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_1);

    // wait for flash->dram ready.
    // read register

    u16Addr = 0x0900+(0x4f<<1);
    u8Data = 0x0;

    u8MsbData[0] = 0x10;
    u8MsbData[1] = 0x00;
    u8MsbData[2] = 0x00;
    u8MsbData[3] = (u16Addr >> 8) &0xff;
    u8MsbData[4] = u16Addr &0xff;

    u8_timeout = 0xff;

    while( (u8Data != 0x99) && (u8Data != 0xaa) && (u8_timeout-->0))
    {
        u8MsbData[0] = 0x35;
        retb &= iptr->WriteBytes(0, NULL, 1, u8MsbData);

        u8MsbData[0] = 0x10;
        retb &= iptr->WriteBytes(0, NULL, 5, u8MsbData);
        retb &= iptr->ReadBytes(0, NULL, 1, &u8Data);

        u8MsbData[0] = 0x34;
        retb &= iptr->WriteBytes(0, NULL, 1, u8MsbData);
        // 10ms
        usleep(1000*10);
    }
    // end read register
    *ptimeout = 0;
    if (u8_timeout == 0x00)
    {
        *ptimeout = 1;
        ERR_DEMOD_MSB(printf("[msb1240][error]msb1240_flash_boot_ready_waiting, timeout....\n"));
    }

    DBG_DEMOD_LOAD_I2C(printf("[msb1240][end]msb1240_flash_boot_ready_waiting, t=%d\n",u8_timeout));
    return retb;
}

/*
MAPI_BOOL device_demodulator_extend::DTV_GetVersion(MAPI_U32 *ver)
{

    MAPI_BOOL bRet = true;
    MAPI_U8 tmp = 0;
    MAPI_U32 u32_Version;

    bRet &= ReadDspReg(0x02, &tmp);
    u32_Version = tmp;
    bRet &= ReadDspReg(0x03, &tmp);
    u32_Version = (u32_Version <<8) | tmp;
    bRet &= ReadDspReg(0x04, &tmp);
    u32_Version = (u32_Version <<8) | tmp;
    *ver = u32_Version;

    return bRet;
}
*/



MAPI_BOOL msb1240_flash_boot_ready_waiting_ns(MAPI_U8 *ptimeout)
{

    MAPI_BOOL  retb = TRUE;
    //MAPI_U8    data[5] = {0x53, 0x45, 0x52, 0x44, 0x42};
    MAPI_U8    u8MsbData[6] = {0};
    MAPI_U8    u8Data  = 0;
    MAPI_U16   u16Addr = 0;
    MAPI_U8    u8_timeout = 0;

    DBG_DEMOD_LOAD_I2C(printf("[msb1240][beg]msb1240_flash_boot_ready_waiting_2nd\n"));

    mapi_scope_lock(scopeLock, &m_MutexRWReg);
    mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_3);// for second demod

    // wait for flash->dram ready.
    // read register

    u16Addr = 0x0900+(0x4f<<1);
    u8Data = 0x0;

    u8MsbData[0] = 0x10;
    u8MsbData[1] = 0x00;
    u8MsbData[2] = 0x00;
    u8MsbData[3] = (u16Addr >> 8) &0xff;
    u8MsbData[4] = u16Addr &0xff;

    u8_timeout = 0xff;

    while( (u8Data != 0x99) && (u8Data != 0xaa) && (u8_timeout-->0))
    {
        u8MsbData[0] = 0x35;
        retb &= iptr->WriteBytes(0, NULL, 1, u8MsbData);

        u8MsbData[0] = 0x10;
        retb &= iptr->WriteBytes(0, NULL, 5, u8MsbData);
        retb &= iptr->ReadBytes(0, NULL, 1, &u8Data);

        u8MsbData[0] = 0x34;
        retb &= iptr->WriteBytes(0, NULL, 1, u8MsbData);
        // 10ms
        usleep(1000*10);
    }
    // end read register
    *ptimeout = 0;
    if (u8_timeout == 0x00)
    {
        *ptimeout = 1;
        ERR_DEMOD_MSB(printf("[msb1240][error]msb1240_flash_boot_ready_waiting_2nd, timeout....\n"));
    }

    DBG_DEMOD_LOAD_I2C(printf("[msb1240][end]msb1240_flash_boot_ready_waiting_2nd, t=%d\n",u8_timeout));
    return retb;
}

static MAPI_BOOL msb1240_flash_WP_reg_read_ns(MAPI_U16 u16Addr, MAPI_U8 *pu8Data)
{
    MAPI_BOOL bRet;
    MAPI_U8   u8MsbData[5];
    mapi_scope_lock(scopeLock, &m_MutexRWReg);
    mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_3);// for second demod

    u8MsbData[0] = 0x10;
    u8MsbData[1] = 0x00;
    u8MsbData[2] = 0x00;
    u8MsbData[3] = (u16Addr >> 8) &0xff;
    u8MsbData[4] = u16Addr &0xff;

    u8MsbData[0] = 0x35;
    iptr->WriteBytes(0, NULL, 1, u8MsbData);

    u8MsbData[0] = 0x10;
    iptr->WriteBytes(0, NULL, 5, u8MsbData);
    iptr->ReadBytes(0, NULL, 1, pu8Data);

    u8MsbData[0] = 0x34;
    bRet=iptr->WriteBytes(0, NULL, 1, u8MsbData);
    return bRet;
}

static MAPI_BOOL dram_crc_check_ns(MAPI_U16 chksum_lib, MAPI_BOOL* pMatch)
{
    MAPI_U16  chksum = 0;
    // MAPI_U16  chksum_lib = 0;
    MAPI_U16  u16Addr = 0;
    MAPI_U8   u8MsbData[5];
    MAPI_U8   reg = 0;
    MAPI_BOOL bRet;
    MAPI_U8   mcu_status = 0;

    *pMatch = false;

    // MAPI_U8 bWriteData[5]={0x4D, 0x53, 0x54, 0x41, 0x52};
    mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_3);// 2nd demod
    // iptr->WriteBytes(0, NULL, 5, bWriteData);

    /// crc H byte
    u16Addr = 0x0c00+0x5a*2;

    u8MsbData[0] = 0x10;
    u8MsbData[1] = 0x00;
    u8MsbData[2] = 0x00;
    u8MsbData[3] = (u16Addr >> 8) &0xff;
    u8MsbData[4] = u16Addr &0xff;

    u8MsbData[0] = 0x35;
    iptr->WriteBytes(0, NULL, 1, u8MsbData);

    u8MsbData[0] = 0x10;
    iptr->WriteBytes(0, NULL, 5, u8MsbData);
    iptr->ReadBytes(0, NULL, 1, &reg);

    u8MsbData[0] = 0x34;
    bRet=iptr->WriteBytes(0, NULL, 1, u8MsbData);


    chksum = reg;

    /// crc L byte
    u16Addr = 0x0c00+0x5b*2;

    u8MsbData[0] = 0x10;
    u8MsbData[1] = 0x00;
    u8MsbData[2] = 0x00;
    u8MsbData[3] = (u16Addr >> 8) &0xff;
    u8MsbData[4] = u16Addr &0xff;

    u8MsbData[0] = 0x35;
    iptr->WriteBytes(0, NULL, 1, u8MsbData);

    u8MsbData[0] = 0x10;
    iptr->WriteBytes(0, NULL, 5, u8MsbData);
    iptr->ReadBytes(0, NULL, 1, &reg);

    u8MsbData[0] = 0x34;
    bRet=iptr->WriteBytes(0, NULL, 1, u8MsbData);


    chksum = (chksum<<8)|reg;

    // get mcu status

    u16Addr = 0x0900+0x4f*2;

    u8MsbData[0] = 0x10;
    u8MsbData[1] = 0x00;
    u8MsbData[2] = 0x00;
    u8MsbData[3] = (u16Addr >> 8) &0xff;
    u8MsbData[4] = u16Addr &0xff;

    u8MsbData[0] = 0x35;
    iptr->WriteBytes(0, NULL, 1, u8MsbData);

    u8MsbData[0] = 0x10;
    iptr->WriteBytes(0, NULL, 5, u8MsbData);
    iptr->ReadBytes(0, NULL, 1, &reg);

    u8MsbData[0] = 0x34;
    bRet=iptr->WriteBytes(0, NULL, 1, u8MsbData);


    mcu_status = reg;


    /// check the crc in dsp lib array

    if (mcu_status == 0xaa && ((chksum_lib&0xff00) == (chksum&0xff00)) )
        *pMatch = true;
    else if(chksum_lib == chksum)
        *pMatch = true;

    DBG_DEMOD_CHECKSUM(printf("[crc_2nd]chksum_lib=0x%x, chksum=0x%x, bRet=%d, Match=%d, mcu_status=0x%x\n",chksum_lib,chksum,bRet,*pMatch,mcu_status));

    return bRet;
}

static MAPI_BOOL msb1240_flash_WP_reg_write_ns(MAPI_U16 u16Addr, MAPI_U8 u8Data)
{
    MAPI_BOOL bRet = MAPI_TRUE;
    MAPI_U8   u8MsbData[6];
    mapi_scope_lock(scopeLock, &m_MutexRWReg);
    mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_3);// for second demod
#if (PRELOAD_DSP_CODE_FROM_MAIN_CHIP_I2C_ONLY_LOAD_T2 == 1)
    bRet &= iptr->SetSpeed(350);
#endif

    u8MsbData[0] = 0x10;
    u8MsbData[1] = 0x00;
    u8MsbData[2] = 0x00;
    u8MsbData[3] = (u16Addr >> 8) &0xff;
    u8MsbData[4] = u16Addr &0xff;
    u8MsbData[5] = u8Data;

    u8MsbData[0] = 0x35;
    iptr->WriteBytes(0, NULL, 1, u8MsbData);

    u8MsbData[0] = 0x10;
    iptr->WriteBytes(0, NULL, 6, u8MsbData);

    u8MsbData[0] = 0x34;
    bRet=iptr->WriteBytes(0, NULL, 1, u8MsbData);
    return bRet;
}

static MAPI_BOOL msb1240_flash_WP_ns(MAPI_U8 enable)
{
    MAPI_U8 reg = 0;
    MAPI_BOOL bRet = true;
    MAPI_U8 u8_count = 0;

    DBG_FLASH_WP(printf("[wb]msb1240_flash_WP_Enable_2nd=%d\n",enable);)

    if (enable == 1)
    {
        u8_count = 20;
        do
        {
        msb1240_flash_SRSR(&reg);
        usleep(1*1000);
        }while(reg&0x01 && u8_count--);

        if (u8_count == 0)
        {
        bRet = false;
        DBG_FLASH_WP(printf("[wb]Err, flash_SRSR_2nd timeout!!!\n");)
        return bRet;
        }

        msb1240_flash_WRSR(reg|0x9c);

        u8_count = 20;
        do
        {
            msb1240_flash_SRSR(&reg);
            usleep(1*1000);
        }while(reg&0x01 && u8_count--);


        if (u8_count == 0)
        {
            bRet = false;
            DBG_FLASH_WP(printf("[wb]Err, flash_SRSR_2nd timeout!!!\n");)
            return bRet;
        }

        // active low
        // init gpio0
        bRet &= msb1240_flash_WP_reg_read_ns(0x0900+0x2e*2, &reg);
        bRet &= msb1240_flash_WP_reg_write_ns(0x0900+0x2e*2, reg&(~0x02));
        bRet &= msb1240_flash_WP_reg_read_ns(0x0900+0x6b*2, &reg);
        bRet &= msb1240_flash_WP_reg_write_ns(0x0900+0x6b*2, reg&(~0x30));

        // pull low
        bRet &= msb1240_flash_WP_reg_read_ns(0x0900+0x63*2, &reg);
        bRet &= msb1240_flash_WP_reg_write_ns(0x0900+0x63*2, reg&(~0x01));

        // gpio0 output enable
        bRet &= msb1240_flash_WP_reg_read_ns(0x0900+0x64*2, &reg);
        bRet &= msb1240_flash_WP_reg_write_ns(0x0900+0x64*2, reg&(~0x01));
    }
    else
    {
        // unactive high
        // init gpio0
        bRet &= msb1240_flash_WP_reg_read_ns(0x0900+0x2e*2, &reg);
        bRet &= msb1240_flash_WP_reg_write_ns(0x0900+0x2e*2, reg&(~0x02));
        bRet &= msb1240_flash_WP_reg_read_ns(0x0900+0x6b*2, &reg);
        bRet &= msb1240_flash_WP_reg_write_ns(0x0900+0x6b*2, reg&(~0x30));

        // pull high
        bRet &= msb1240_flash_WP_reg_read_ns(0x0900+0x63*2, &reg);
        bRet &= msb1240_flash_WP_reg_write_ns(0x0900+0x63*2, reg|0x01);

        // gpio0 output enable
        bRet &= msb1240_flash_WP_reg_read_ns(0x0900+0x64*2, &reg);
        bRet &= msb1240_flash_WP_reg_write_ns(0x0900+0x64*2, reg&(~0x01));

        u8_count = 20;
        do
        {
            msb1240_flash_SRSR(&reg);
            usleep(1*1000);
        }while(reg&0x01 && u8_count--);

        if (u8_count == 0)
        {
            bRet = false;
            DBG_FLASH_WP(printf("[wb]Err, flash_SRSR_2nd timeout!!!\n");)
            return bRet;
        }

        msb1240_flash_WRSR(reg&(~0x9c));

        u8_count = 20;
        do
        {
            msb1240_flash_SRSR(&reg);
            usleep(1*1000);
        }while(reg&0x01 && u8_count--);

        if (u8_count == 0)
        {
            bRet = false;
            DBG_FLASH_WP(printf("[wb]Err, flash_SRSR_2nd timeout!!!\n");)
            return bRet;
        }
    }
    return bRet;
}

MAPI_BOOL msb1240_flash_mode_en_ns(void)
{
    MAPI_BOOL  retb = TRUE;
    MAPI_U8    data[5] = {0x53, 0x45, 0x52, 0x44, 0x42};
    MAPI_U8    u8MsbData[6] = {0};
    MAPI_U8    ch_num  = 3;
    MAPI_U8    u8Data  = 0;
    MAPI_U16   u16Addr = 0;
    MAPI_U8    retry_num = MSB1240_MAX_FLASH_ON_RETRY_NUM;

    DBG_DEMOD_FLOW(printf("Enter %s...\n",__FUNCTION__));
    DBG_DEMOD_LOAD_I2C(printf("[msb1240][beg]msb1240_flash_mode_en_2nd\n"));

    mapi_scope_lock(scopeLock, &m_MutexRWReg);
    mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_3);// for second demod

    do{

        if (retry_num != MSB1240_MAX_FLASH_ON_RETRY_NUM)
        {
            ERR_DEMOD_MSB(printf("[msb1240][error]flash mode en_2nd fail.....retry=%d\n",retry_num));
        }
        // retb = TRUE;
        // password
        // 8'hb2(SRID)->8,h53(PWD1)->8,h45(PWD2)->8,h52(PWD3)->8,h44(PWD4)->8,h42(PWD5)
        data[0] = 0x53;
        retb &= iptr->WriteBytes(0, NULL, 5, data);

        // 8'hb2(SRID)->8,h71(CMD)  //TV.n_iic_
        data[0] = 0x71;
        retb &= iptr->WriteBytes(0, NULL, 1, data);

        // 8'hb2(SRID)->8,h81(CMD)  //TV.n_iic_sel_b0
        data[0] = ((ch_num & 0x01) != 0)? 0x81 : 0x80;
        retb &= iptr->WriteBytes(0, NULL, 1, data);

        // 8'hb2(SRID)->8,h83(CMD)  //TV.n_iic_sel_b1
        data[0] = ((ch_num & 0x02) != 0)? 0x83 : 0x82;
        retb &= iptr->WriteBytes(0, NULL, 1, data);

        // 8'hb2(SRID)->8,h84(CMD)  //TV.n_iic_sel_b2
        data[0] = ((ch_num & 0x04) != 0)? 0x85 : 0x84;
        retb &= iptr->WriteBytes(0, NULL, 1, data);

        // 8'hb2(SRID)->8,h53(CMD)  //TV.n_iic_ad_byte_en2, 32bit read/write
        data[0] = 0x53;
        retb &= iptr->WriteBytes(0, NULL, 1, data);

        // 8'hb2(SRID)->8,h7f(CMD)  //TV.n_iic_sel_use_cfg
        data[0] = 0x7f;
        retb &= iptr->WriteBytes(0, NULL, 1, data);

        /*
        // 8'hb2(SRID)->8,h35(CMD)  //TV.n_iic_use
        data[0] = 0x35;
        retb &= iptr->WriteBytes(0, NULL, 1, data);

        // 8'hb2(SRID)->8,h71(CMD)  //TV.n_iic_Re-shape
        data[0] = 0x71;
        retb &= iptr->WriteBytes(0, NULL, 1, data);
        */
        retb = TRUE;

        // confirm first, 0x99 and 0xaa.
        // beg read register
        u16Addr = 0x0900+(0x4f<<1);
        u8Data = 0x0;

        u8MsbData[0] = 0x10;
        u8MsbData[1] = 0x00;
        u8MsbData[2] = 0x00;
        u8MsbData[3] = (u16Addr >> 8) &0xff;
        u8MsbData[4] = u16Addr &0xff;

        u8MsbData[0] = 0x35;
        retb &= iptr->WriteBytes(0, NULL, 1, u8MsbData);

        u8MsbData[0] = 0x10;
        retb &= iptr->WriteBytes(0, NULL, 5, u8MsbData);
        retb &= iptr->ReadBytes(0, NULL, 1, &u8Data);

        u8MsbData[0] = 0x34;
        retb &= iptr->WriteBytes(0, NULL, 1, u8MsbData);
        // end read register

        if ((u8Data == 0x99) || (u8Data == 0xaa))
        {
            ERR_DEMOD_MSB(printf("[msb1240][warning]flash is already on_2nd....\n"));
            break;
        }
        // flash mode enable.
        // beg read register
        u16Addr = 0x0900+(0x28<<1)+1;
        u8Data = 0x0;

        u8MsbData[0] = 0x10;
        u8MsbData[1] = 0x00;
        u8MsbData[2] = 0x00;
        u8MsbData[3] = (u16Addr >> 8) &0xff;
        u8MsbData[4] = u16Addr &0xff;

        u8MsbData[0] = 0x35;

        retb &= iptr->WriteBytes(0, NULL, 1, u8MsbData);



        u8MsbData[0] = 0x10;
        retb &= iptr->WriteBytes(0, NULL, 5, u8MsbData);
        retb &= iptr->ReadBytes(0, NULL, 1, &u8Data);

        u8MsbData[0] = 0x34;
        retb &= iptr->WriteBytes(0, NULL, 1, u8MsbData);
        // end read register

        // beg write register
        u16Addr = 0x0900+(0x28<<1) + 1;
        u8Data &= (0xff-0x01);

        u8MsbData[0] = 0x10;
        u8MsbData[1] = 0x00;
        u8MsbData[2] = 0x00;
        u8MsbData[3] = (u16Addr >> 8) &0xff;
        u8MsbData[4] = u16Addr &0xff;
        u8MsbData[5] = u8Data;

        u8MsbData[0] = 0x35;
        retb &= iptr->WriteBytes(0, NULL, 1, u8MsbData);

        u8MsbData[0] = 0x10;
        retb &= iptr->WriteBytes(0, NULL, 6, u8MsbData);

        u8MsbData[0] = 0x34;
        retb &= iptr->WriteBytes(0, NULL, 1, u8MsbData);
        // end write register

        // beg write register
        u16Addr = 0x0900+(0x06<<1);
        u8Data = 0x10;

        u8MsbData[0] = 0x10;
        u8MsbData[1] = 0x00;
        u8MsbData[2] = 0x00;
        u8MsbData[3] = (u16Addr >> 8) &0xff;
        u8MsbData[4] = u16Addr &0xff;
        u8MsbData[5] = u8Data;

        u8MsbData[0] = 0x35;
        retb &= iptr->WriteBytes(0, NULL, 1, u8MsbData);

        u8MsbData[0] = 0x10;
        retb &= iptr->WriteBytes(0, NULL, 6, u8MsbData);

        u8MsbData[0] = 0x34;
        retb &= iptr->WriteBytes(0, NULL, 1, u8MsbData);
        // end write register

        // beg write register

        u16Addr = 0x0900+(0x07<<1);
        u8Data = 0x10;

        u8MsbData[0] = 0x10;
        u8MsbData[1] = 0x00;
        u8MsbData[2] = 0x00;
        u8MsbData[3] = (u16Addr >> 8) &0xff;
        u8MsbData[4] = u16Addr &0xff;
        u8MsbData[5] = u8Data;

        u8MsbData[0] = 0x35;
        retb &= iptr->WriteBytes(0, NULL, 1, u8MsbData);

        u8MsbData[0] = 0x10;
        retb &= iptr->WriteBytes(0, NULL, 6, u8MsbData);

        u8MsbData[0] = 0x34;
        retb &= iptr->WriteBytes(0, NULL, 1, u8MsbData);
    }while( (retb == FALSE) && (retry_num-- != 0));
    // end write register

    DBG_DEMOD_LOAD_I2C(printf("[msb1240][end]msb1240_flash_mode_en_2nd,retb=%d\n",retb));
    DBG_DEMOD_FLOW(printf("Exit %s...\n",__FUNCTION__));

    return retb;
}

MAPI_BOOL device_demodulator_extend::WriteReg_ns(MAPI_U16 u16Addr, MAPI_U8 u8Data)
{
    //DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_BOOL bRet = MAPI_TRUE;

    if (g_u8_msb1240_sleep_mode_status > 1)
    {
        printf("%s, %s, %d, MSB1240 is sleeping, wake him up first\n",__FILE__,__FUNCTION__,__LINE__);
        return FALSE;
    }

    mapi_scope_lock(scopeLock, &m_MutexRWReg);

#if USE_SPI_LOAD_TO_SDRAM && (NEED_SWITCH_TSPAD_FOR_MSPI_USE == 0)
    bRet = MDrv_SS_RIU_Write8(u16Addr, u8Data);
#else
    mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_3);// 2nd demod
#if (PRELOAD_DSP_CODE_FROM_MAIN_CHIP_I2C_ONLY_LOAD_T2 == 1)
    bRet &= iptr->SetSpeed(350);
#endif

    u8MsbData[0] = 0x10;
    u8MsbData[1] = 0x00;
    u8MsbData[2] = 0x00;
    u8MsbData[3] = (u16Addr >> 8) &0xff;
    u8MsbData[4] = u16Addr &0xff;
    u8MsbData[5] = u8Data;

    u8MsbData[0] = 0x35;
    bRet &= iptr->WriteBytes(0, NULL, 1, u8MsbData);

    u8MsbData[0] = 0x10;
    bRet &= iptr->WriteBytes(0, NULL, 6, u8MsbData);

    u8MsbData[0] = 0x34;
    bRet &= iptr->WriteBytes(0, NULL, 1, u8MsbData);
#endif

    return bRet;

}

MAPI_BOOL device_demodulator_extend::ReadReg_ns(MAPI_U16 u16Addr, MAPI_U8 *pu8Data)
{
    //DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    MAPI_BOOL bRet = MAPI_TRUE;

    if (g_u8_msb1240_sleep_mode_status > 1)
    {
        printf("%s, %s, %d, MSB1240 is sleeping, wake him up first\n",__FILE__,__FUNCTION__,__LINE__);
        return FALSE;
    }

    mapi_scope_lock(scopeLock, &m_MutexRWReg);
#if USE_SPI_LOAD_TO_SDRAM && (NEED_SWITCH_TSPAD_FOR_MSPI_USE == 0)
    bRet = MDrv_SS_RIU_Read8(u16Addr, pu8Data);
#else
    mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_3);// 2nd demod

    u8MsbData[0] = 0x10;
    u8MsbData[1] = 0x00;
    u8MsbData[2] = 0x00;
    u8MsbData[3] = (u16Addr >> 8) &0xff;
    u8MsbData[4] = u16Addr &0xff;

    u8MsbData[0] = 0x35;
    bRet &= iptr->WriteBytes(0, NULL, 1, u8MsbData);

    u8MsbData[0] = 0x10;
    bRet &= iptr->WriteBytes(0, NULL, 5, u8MsbData);
    bRet &= iptr->ReadBytes(0, NULL, 1, pu8Data);

    u8MsbData[0] = 0x34;
    bRet &= iptr->WriteBytes(0, NULL, 1, u8MsbData);
#endif

    return bRet;
}

MAPI_BOOL device_demodulator_extend::I2C_CH_Reset_ns(MAPI_U8 ch_num)
{
    DBG_DEMOD_FLOW(printf("%s(),%d\n",__func__,__LINE__));
    //MAPI_U8         addr[4] = {0x00, 0x00, 0x00, 0x00};
    MAPI_U8         data[5] = {0x53, 0x45, 0x52, 0x44, 0x42};

    DBG_DEMOD_LOAD_I2C(printf("[msb1240][beg]I2C_CH_Reset_2nd, CH=0x%x\n",ch_num));
    mapi_scope_lock(scopeLock, &m_MutexRWReg);
    mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_3);// 2nd demod

    // 8'hb2(SRID)->8,h53(PWD1)->8,h45(PWD2)->8,h52(PWD3)->8,h44(PWD4)->8,h42(PWD5)
    data[0] = 0x53;
    iptr->WriteBytes(0, NULL, 5, data);

    // 8'hb2(SRID)->8,h71(CMD)  //TV.n_iic_
    data[0] = 0x71;
    iptr->WriteBytes(0, NULL, 1, data);

    // 8'hb2(SRID)->8,h81(CMD)  //TV.n_iic_sel_b0
    data[0] = ((ch_num & 0x01) != 0)? 0x81 : 0x80;
    iptr->WriteBytes(0, NULL, 1, data);

    // 8'hb2(SRID)->8,h83(CMD)  //TV.n_iic_sel_b1
    data[0] = ((ch_num & 0x02) != 0)? 0x83 : 0x82;
    iptr->WriteBytes(0, NULL, 1, data);

    // 8'hb2(SRID)->8,h84(CMD)  //TV.n_iic_sel_b2
    data[0] = ((ch_num & 0x04) != 0)? 0x85 : 0x84;
    iptr->WriteBytes(0, NULL, 1, data);

    // 8'hb2(SRID)->8,h53(CMD)  //TV.n_iic_ad_byte_en2, 32bit read/write
    data[0] = 0x53;
    iptr->WriteBytes(0, NULL, 1, data);

    // 8'hb2(SRID)->8,h7f(CMD)  //TV.n_iic_sel_use_cfg
    data[0] = 0x7f;
    iptr->WriteBytes(0, NULL, 1, data);

    /*
    // 8'hb2(SRID)->8,h35(CMD)  //TV.n_iic_use
    data[0] = 0x35;
    iptr->WriteBytes(0, NULL, 1, data);

    // 8'hb2(SRID)->8,h71(CMD)  //TV.n_iic_Re-shape
    data[0] = 0x71;
    iptr->WriteBytes(0, NULL, 1, data);
    */
    DBG_DEMOD_LOAD_I2C(printf("[msb1240][end]I2C_CH_Reset_2nd, CH=0x%x\n",ch_num));
    return MAPI_TRUE;
}

MAPI_BOOL device_demodulator_extend:: msb1240_flash_check(void)
{
    //step 1: check if flash ready

    MAPI_U8     status = TRUE;
    MAPI_U8     u8RetryCnt = 6;
    MAPI_U8     flash_waiting_ready_timeout = 0;
    MAPI_U16    crc16 = 0;
    MAPI_U8     flag = 0;
    MAPI_BOOL   bMatch = false;
    MAPI_U16    u16_counter = 0;
    MAPI_U8     u8DoneFlag = 0;
    MAPI_U8     u8_reg = 0;
    MAPI_U32    u32SizeOfLimit=0;
    
    use_twin_demod = 1;//set 1 when msb1240_flash_check is doing.
    
    ReadReg(0x0900+(0x02<<1),&u8_reg);
    if(u8_reg & _BIT5)
    {
        crc16 = MSB1240_LIB[u32SizeOfMSB1240_LIB-2];
        crc16 = (crc16<<8)|MSB1240_LIB[u32SizeOfMSB1240_LIB-1];
        u32SizeOfLimit = u32SizeOfMSB1240_LIB-4;
    }
    else
    {
        crc16 = MSB1240_LIB[u32SizeOfMSB1240_LIB-4];
        crc16 = (crc16<<8)|MSB1240_LIB[u32SizeOfMSB1240_LIB-3];
        u32SizeOfLimit = u32SizeOfMSB1240_LIB-4-0x9000;
    }

    DBG_DEMOD_FLOW(printf("Enter %s...\n", __FUNCTION__));
    printf(">>> Enter msb1240_flash_check !!\n");

    do
    {   
        flag = 0;//reset flag
        
        if(u8RetryCnt <6)
        {
            status = msb1240_flash_mode_en();
            if (status == FALSE) ERR_DEMOD_MSB(printf("[msb1240][error]msb1240_flash_mode_en fail....\n"));

            status = msb1240_flash_mode_en_ns();
            if (status == FALSE) ERR_DEMOD_MSB(printf("[msb1240][error]msb1240_flash_mode_en_2nd demod fail....\n"));
        }

        status = msb1240_flash_boot_ready_waiting(&flash_waiting_ready_timeout);
        if ( (flash_waiting_ready_timeout == 1) || (status == FALSE) ) ERR_DEMOD_MSB(printf("[msb1240][error]msb1240_flash_boot_ready_waiting fail....\n"));

        status &= msb1240_flash_boot_ready_waiting_ns(&flash_waiting_ready_timeout);
        if ( (flash_waiting_ready_timeout == 1) || (status == FALSE) ) ERR_DEMOD_MSB(printf("[msb1240][error]msb1240_flash_boot_ready_waiting_2nd demod fail....\n"));

        if(status == FALSE) // I2C error
        {
#ifndef T3_Winbledon
            DBG_DEMOD_MSB(printf(">>>MSB1240: Reset Demodulator\n"));

            mapi_gpio *gptr = mapi_gpio::GetGPIO_Dev(Demodulator_Reset);
            if(gptr != NULL)
            {
                gptr->SetOff();
            }

            DBG_DEMOD_MSB(printf(">>>MSB1240: Reset Demodulator_2nd demod\n"));

            gptr = mapi_gpio::GetGPIO_Dev(DVBS_RESETZ);// 2nd demod 
            if(gptr != NULL)
            {
                gptr->SetOff();
            }

            usleep(resetDemodTime*1000);

            if(gptr != NULL)
            {
                gptr->SetOn();//For B
            }

            gptr = mapi_gpio::GetGPIO_Dev(Demodulator_Reset);
            if(gptr != NULL)
            {
                gptr->SetOn();//For A
            }

            status = msb1240_flash_mode_en();
            if(status == FALSE) ERR_DEMOD_MSB(printf("[msb1240][error]msb1240_flash_mode_en fail....\n"));

            status = msb1240_flash_mode_en_ns();
            if(status == FALSE) ERR_DEMOD_MSB(printf("[msb1240][error]msb1240_flash_mode_en_2nd demod fail....\n"));

            status = msb1240_flash_boot_ready_waiting(&flash_waiting_ready_timeout);
            if((flash_waiting_ready_timeout == 1) || (status == FALSE)) ERR_DEMOD_MSB(printf("[msb1240][error]msb1240_flash_boot_ready_waiting fail....\n"));
            
            // usleep(waitFlashTime * 1000);
            if (I2C_CH_Reset(3) == MAPI_FALSE)// reset A's I2C
            {
                DBG_DEMOD_MSB(printf(">>>MSB1240 CH Reset:Fail\n"));
                status= MAPI_FALSE;
                continue;
            }

            u16_counter = 1000;
            do
            {
                // 10 ms
                usleep(10*1000);
                u16_counter--;
                ReadReg(0x0900+(0x4f)*2, &u8DoneFlag);
            } while(u8DoneFlag != 0x99 && u16_counter != 0);

            if(u16_counter == 0 && u8DoneFlag != 0x99)
            {
                DBG_DEMOD_MSB(printf("[wb]Err, MSB1240 didn't ready yet\n"));
                status = false;
            }
            else
                status = TRUE;

            status = msb1240_flash_boot_ready_waiting_ns(&flash_waiting_ready_timeout);
            if ( (flash_waiting_ready_timeout == 1) || (status == FALSE) ) ERR_DEMOD_MSB(printf("[msb1240][error]msb1240_flash_boot_ready_waiting_2nd demod fail....\n"));

            // usleep(waitFlashTime * 1000);
            if (I2C_CH_Reset_ns(3) == MAPI_FALSE)// reset B's I2C
            {
                DBG_DEMOD_MSB(printf(">>>MSB1240 CH Reset_2nd demod:Fail\n"));
                status= MAPI_FALSE;
                continue;
            }

            u16_counter = 1000;
            do
            {
                // 10 ms
                usleep(10*1000);
                u16_counter--;
                ReadReg_ns(0x0900+(0x4f)*2, &u8DoneFlag);
            } while(u8DoneFlag != 0x99 && u16_counter != 0);

            if(u16_counter == 0 && u8DoneFlag != 0x99)
            {
                DBG_DEMOD_MSB(printf("[wb]Err, MSB1240_2nd demod didn't ready yet\n"));
                status = false;  
            }
            else
                status = TRUE;
#endif
        }


        //step 2. check dram CRC

        if (dram_crc_check(crc16, &bMatch) == MAPI_FALSE)
        {
            (printf(">>>crc reg read fail!\n"));
            status= MAPI_FALSE;
            continue;
        }

        if (bMatch == false)
        {  
            flag =1;//A false
            printf(">>> dram crc_1st check FAIL!\n");
        }
        else
            printf(">>> dram crc_1st check OK!\n");

        if (bMatch == true)
        {
            // mask miu access for all and mcu
            WriteReg(0x1200+(0x23)*2 + 1,0x0f);
            WriteReg(0x1200+(0x23)*2,0xf0);
            // 10us delay
            usleep(10);
            WriteReg(0x0B00 + (0x19) * 2, 0x03);            
        }             

        if (dram_crc_check_ns(crc16, &bMatch) == MAPI_FALSE)
        {
            (printf(">>>crc reg read fail!\n"));
            status= MAPI_FALSE;
            continue;
        }

        if (bMatch == false)
        { 
            if (flag ==1)
                flag = 3; //A&B false                
            else
            {
                flag = 2; // B false
                printf(">>> dram crc_2nd check demod FAIL!\n");
            }
        }
        else
            printf(">>> dram crc_2nd check demod OK!\n");

        if (bMatch == true)
        {
            // mask miu access for all and mcu
            WriteReg_ns(0x1200+(0x23)*2 + 1,0x0f);
            WriteReg_ns(0x1200+(0x23)*2,0xf0);
            // 10us delay
            usleep(10);
            WriteReg_ns(0x0B00 + (0x19) * 2, 0x03);
        }             

        //step 3.Flashwrite
        switch (flag) 
        {
            case 0: 
                // Both CRC ok
                break;
                
            case 1:               
                // 1st flash version not match
                {
                    MAPI_U8     bAddr[1];
                    MAPI_U8     bWriteData[5]={0x4D, 0x53, 0x54, 0x41, 0x52};

                    printf(">>> Not match! Reload Flash...");
                    if ( (u32SizeOfMSB1240_LIB%256) != 0)
                    {
                        printf(" MSB1240_LIB 256byte alignment error!%u \n",u32SizeOfMSB1240_LIB);
                    }

                    //RESET B
                    DBG_DEMOD_MSB(printf(">>>MSB1240: Reset Demodulator_2nd demod\n"));

                    mapi_gpio *gptr = mapi_gpio::GetGPIO_Dev(DVBS_RESETZ); // 2nd Demod 
                    if(gptr != NULL)
                    {
                        gptr->SetOff();
                    }

                    usleep(resetDemodTime*1000);

#if (FLASH_WP_ENABLE == 1) 
             
                    // disable flash WP, pull high.
                    if(msb1240_flash_WP(0) == false)
                    {
                        DBG_FLASH_WP(printf("[wb]Err, FLASH WP Disable Fail!!!\n"));
                    }
                    usleep(100*1000);
                
#endif

                    mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_2);
                    iptr->WriteBytes(0, NULL, 5, bWriteData);

                    bAddr[0] = 0x10;
                    bWriteData[0] = 0x06;
                    iptr->WriteBytes(1, bAddr, 1, bWriteData);

                    bWriteData[0] = 0x12;
                    iptr->WriteBytes(0, NULL, 1, bWriteData);

                    bAddr[0] = 0x10;

                    bWriteData[0] = 0xC7;
                    iptr->WriteBytes(1, bAddr, 1, bWriteData);

                    bWriteData[0] = 0x12;
                    iptr->WriteBytes(0, NULL, 1, bWriteData);

                    bWriteData[0]=0x24 ;
                    iptr->WriteBytes(0, NULL, 1, bWriteData);
                    DBG_DEMOD_MSB(printf("\t\t\tStart   %d\n", MsOS_GetSystemTime()));//to measure time
                    if ( (u32SizeOfMSB1240_LIB - 4) > MAX_MSB1240_LIB_LEN)
                    {
                        printf("Err, msb1240_lib size(%d) is larger than flash size(%d)\n",u32SizeOfMSB1240_LIB,MAX_MSB1240_LIB_LEN);
                    }

                    // if (IspProcFlash(MSB1240_LIB, u32SizeOfMSB1240_LIB) == MAPI_FALSE)
                    if (IspProcFlash(MSB1240_LIB, u32SizeOfLimit) == MAPI_FALSE)
                    {
                        ERR_DEMOD_MSB(printf(" ISP write FAIL\n"));
                        status= MAPI_FALSE;

                        //release B
                        mapi_gpio *gptr = mapi_gpio::GetGPIO_Dev(DVBS_RESETZ);// 2nd demod 
                        if(gptr != NULL)
                        {
                            gptr->SetOn();
                        }

                        continue;
                    }

#if (FLASH_WP_ENABLE == 1)
              
                    // Enable flash WP, pull high.
                    if(msb1240_flash_WP(1) == false)
                    {
                        DBG_FLASH_WP(printf("[wb]Err, FLASH WP Enable Fail!!!\n"));
                    }
                    usleep(100*1000);
               
#endif

                    //release B
                    gptr = mapi_gpio::GetGPIO_Dev(DVBS_RESETZ);// 2nd demod 
                    if(gptr != NULL)
                    {
                        gptr->SetOn();
                    }

                }
                break;       

            case 2:             
                // 2nd flash Version not match
                {
                    MAPI_U8     bAddr[1];
                    MAPI_U8     bWriteData[5]={0x4D, 0x53, 0x54, 0x41, 0x52};

                    printf(">>> Not match_2nd! Reload Flash...");
                    if ( (u32SizeOfMSB1240_LIB%256) != 0)
                    {
                        printf(" MSB1240_LIB 256byte alignment error!%u \n",u32SizeOfMSB1240_LIB);
                    }

                    //RESET A
                    DBG_DEMOD_MSB(printf(">>>MSB1240: Reset Demodulator\n"));

                    mapi_gpio *gptr = mapi_gpio::GetGPIO_Dev(Demodulator_Reset);
                    if(gptr != NULL)
                    {
                        gptr->SetOff();
                    }

                    usleep(resetDemodTime*1000);

#if (FLASH_WP_ENABLE == 1) 
               
                    // disable flash WP, pull high.
                    if(msb1240_flash_WP_ns(0) == false)
                    {
                        DBG_FLASH_WP(printf("[wb]Err, FLASH WP_2nd demod Disable Fail!!!\n"));
                    }
                    usleep(100*1000);
               
#endif

                    mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_2);
                    iptr->WriteBytes(0, NULL, 5, bWriteData);

                    bAddr[0] = 0x10;
                    bWriteData[0] = 0x06;
                    iptr->WriteBytes(1, bAddr, 1, bWriteData);

                    bWriteData[0] = 0x12;
                    iptr->WriteBytes(0, NULL, 1, bWriteData);

                    bAddr[0] = 0x10;

                    bWriteData[0] = 0xC7;
                    iptr->WriteBytes(1, bAddr, 1, bWriteData);

                    bWriteData[0] = 0x12;
                    iptr->WriteBytes(0, NULL, 1, bWriteData);

                    bWriteData[0]=0x24 ;
                    iptr->WriteBytes(0, NULL, 1, bWriteData);
                    DBG_DEMOD_MSB(printf("\t\t\tStart   %d\n", MsOS_GetSystemTime()));//to measure time
                    if ( (u32SizeOfMSB1240_LIB - 4) > MAX_MSB1240_LIB_LEN)
                    {
                        printf("Err, msb1240_lib size(%d) is larger than flash size(%d)\n",u32SizeOfMSB1240_LIB,MAX_MSB1240_LIB_LEN);
                    }

                    // if (IspProcFlash(MSB1240_LIB, u32SizeOfMSB1240_LIB) == MAPI_FALSE)
                    if (IspProcFlash(MSB1240_LIB, u32SizeOfLimit) == MAPI_FALSE)
                    {
                        ERR_DEMOD_MSB(printf(" ISP write FAIL\n"));
                        status= MAPI_FALSE;

                        //release A
                        mapi_gpio *gptr = mapi_gpio::GetGPIO_Dev(Demodulator_Reset);
                        if(gptr != NULL)
                        {
                            gptr->SetOn();
                        }

                        continue;
                    }

#if (FLASH_WP_ENABLE == 1)
                
                    // Enable flash WP, pull high.
                    if(msb1240_flash_WP_ns(1) == false)
                    {
                        DBG_FLASH_WP(printf("[wb]Err, FLASH WP_2nd demod Enable Fail!!!\n"));
                    }
                    usleep(100*1000);
                
#endif            

                    //release A
                    gptr = mapi_gpio::GetGPIO_Dev(Demodulator_Reset);
                    if(gptr != NULL)
                    {
                        gptr->SetOn();
                    }

                }
                break;

            case 3:
                //Both Version not match
                {
                    MAPI_U8     bAddr[1];
                    MAPI_U8     bWriteData[5]={0x4D, 0x53, 0x54, 0x41, 0x52};

                    printf(">>> Both flash ver. Not match! Reload Flash...");
                    if ( (u32SizeOfMSB1240_LIB%256) != 0)
                    {
                        printf(" MSB1240_LIB 256byte alignment error!%u \n",u32SizeOfMSB1240_LIB);
                    }

                    //RESET B
                    DBG_DEMOD_MSB(printf(">>>MSB1240: Reset Demodulator_2nd demod\n"));

                    mapi_gpio *gptr = mapi_gpio::GetGPIO_Dev(DVBS_RESETZ);// 2nd demod 
                    if(gptr != NULL)
                    {
                        gptr->SetOff();
                    }

                    usleep(resetDemodTime*1000);

#if (FLASH_WP_ENABLE == 1) 
               
                    // disable flash WP, pull high.
                    if(msb1240_flash_WP(0) == false)
                    {
                        DBG_FLASH_WP(printf("[wb]Err, FLASH WP Disable Fail!!!\n"));
                    }
                    usleep(100*1000);
               
#endif

                    //Flashwrite A
                    printf("Flashwrite 1st flash!!\n");
                    mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_2);
                    iptr->WriteBytes(0, NULL, 5, bWriteData);

                    bAddr[0] = 0x10;
                    bWriteData[0] = 0x06;
                    iptr->WriteBytes(1, bAddr, 1, bWriteData);

                    bWriteData[0] = 0x12;
                    iptr->WriteBytes(0, NULL, 1, bWriteData);

                    bAddr[0] = 0x10;

                    bWriteData[0] = 0xC7;
                    iptr->WriteBytes(1, bAddr, 1, bWriteData);

                    bWriteData[0] = 0x12;
                    iptr->WriteBytes(0, NULL, 1, bWriteData);

                    bWriteData[0]=0x24 ;
                    iptr->WriteBytes(0, NULL, 1, bWriteData);
                    DBG_DEMOD_MSB(printf("\t\t\tStart   %d\n", MsOS_GetSystemTime()));//to measure time
                    if ( (u32SizeOfMSB1240_LIB - 4) > MAX_MSB1240_LIB_LEN)
                    {
                        printf("Err, msb1240_lib size(%d) is larger than flash size(%d)\n",u32SizeOfMSB1240_LIB,MAX_MSB1240_LIB_LEN);
                    }

                    // if (IspProcFlash(MSB1240_LIB, u32SizeOfMSB1240_LIB) == MAPI_FALSE)
                    if (IspProcFlash(MSB1240_LIB, u32SizeOfLimit) == MAPI_FALSE)
                    {
                        ERR_DEMOD_MSB(printf(" ISP write FAIL\n"));
                        status= MAPI_FALSE;

                        //release B
                        mapi_gpio *gptr = mapi_gpio::GetGPIO_Dev(DVBS_RESETZ); // 2nd demod 
                        if(gptr != NULL)
                        {
                            gptr->SetOn();
                        }
                        continue;
                    }

#if (FLASH_WP_ENABLE == 1)
               
                    // Enable flash WP, pull high.
                    if(msb1240_flash_WP(1) == false)
                    {
                        DBG_FLASH_WP(printf("[wb]Err, FLASH WP Enable Fail!!!\n"));
                    }
                    usleep(100*1000);
                
#endif

                    //RESET A
                    DBG_DEMOD_MSB(printf(">>>MSB1240: Reset Demodulator\n"));

                    gptr = mapi_gpio::GetGPIO_Dev(Demodulator_Reset);
                    if(gptr != NULL)
                    {
                        gptr->SetOff();
                    }

                    usleep(resetDemodTime*1000);

                    //release B
                    gptr = mapi_gpio::GetGPIO_Dev(DVBS_RESETZ); // 2nd demod 
                    if(gptr != NULL)
                    {
                        gptr->SetOn();
                    }

                    //flash enable for B
                    status = msb1240_flash_mode_en_ns();
                    if (status == FALSE) ERR_DEMOD_MSB(printf("[msb1240][error]msb1240_flash_mode_en_2nd demod fail....\n"));
        
#if (FLASH_WP_ENABLE == 1) 
             
                    // disable flash WP, pull high.
                    if(msb1240_flash_WP_ns(0) == false)
                    {
                        DBG_FLASH_WP(printf("[wb]Err, FLASH WP_2nd demod Disable Fail!!!\n"));
                    }
                    usleep(100*1000);               

#endif

                    //Flashwrite B
                    bWriteData[0] = 0x4D; //reset bWriteData

                    printf("Flashwrite 2nd flash!!\n");
                    iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_2);

                    iptr->WriteBytes(0, NULL, 5, bWriteData);

                    bAddr[0] = 0x10;
                    bWriteData[0] = 0x06;
                    iptr->WriteBytes(1, bAddr, 1, bWriteData);

                    bWriteData[0] = 0x12;
                    iptr->WriteBytes(0, NULL, 1, bWriteData);

                    bAddr[0] = 0x10;

                    bWriteData[0] = 0xC7;
                    iptr->WriteBytes(1, bAddr, 1, bWriteData);

                    bWriteData[0] = 0x12;
                    iptr->WriteBytes(0, NULL, 1, bWriteData);

                    bWriteData[0]=0x24 ;
                    iptr->WriteBytes(0, NULL, 1, bWriteData);
                    DBG_DEMOD_MSB(printf("\t\t\tStart   %d\n", MsOS_GetSystemTime()));//to measure time
                    if ( (u32SizeOfMSB1240_LIB - 4) > MAX_MSB1240_LIB_LEN)
                    {
                        printf("Err, msb1240_lib size(%d) is larger than flash size(%d)\n",u32SizeOfMSB1240_LIB,MAX_MSB1240_LIB_LEN);
                    }


                    // if (IspProcFlash(MSB1240_LIB, u32SizeOfMSB1240_LIB) == MAPI_FALSE)
                    if (IspProcFlash(MSB1240_LIB, u32SizeOfLimit) == MAPI_FALSE)
                    {
                        ERR_DEMOD_MSB(printf(" ISP write FAIL\n"));
                        status= MAPI_FALSE;

                        //release A
                        mapi_gpio *gptr = mapi_gpio::GetGPIO_Dev(Demodulator_Reset);
                        if(gptr != NULL)
                        {
                            gptr->SetOn();
                        }

                        continue;
                    }

#if (FLASH_WP_ENABLE == 1)
              
                    // Enable flash WP, pull high.
                    if(msb1240_flash_WP_ns(1) == false)
                    {
                        DBG_FLASH_WP(printf("[wb]Err, FLASH WP_2nd demod Enable Fail!!!\n"));
                    }
                    usleep(100*1000);
               
#endif            
 
                    //release A
                    gptr = mapi_gpio::GetGPIO_Dev(Demodulator_Reset);
                    if(gptr != NULL)
                    {
                        gptr->SetOn();
                    }

                }
                break;
        }

        //step 4. RESET        
        switch (flag)
        {
            case 0:// CRC check ok         
                break;

            case 1://A false, reset A
                {
                    DBG_DEMOD_MSB(printf(">>>MSB1240: Reset Demodulator_ns\n"));

                    mapi_gpio *gptr = mapi_gpio::GetGPIO_Dev(Demodulator_Reset);
                    if(gptr != NULL)
                    {
                        gptr->SetOff();
                    }

                    usleep(resetDemodTime*1000);

                    if(gptr != NULL)
                    {
                        gptr->SetOn();
                    }
                    
                }
                break;

            case 2: // B false , reset B
                {
                    DBG_DEMOD_MSB(printf(">>>MSB1240: Reset Demodulator_2nd\n"));

                    mapi_gpio *gptr = mapi_gpio::GetGPIO_Dev(DVBS_RESETZ); // 2nd demod 
                    if(gptr != NULL)
                    {
                        gptr->SetOff();
                    }

                    usleep(resetDemodTime*1000);

                    if(gptr != NULL)
                    {
                        gptr->SetOn();
                    }
                  
                }
                break;
   
            case 3:// A&B false , reset B
                {
                    DBG_DEMOD_MSB(printf(">>>MSB1240: Reset Demodulator_2nd\n"));

                    mapi_gpio *gptr = mapi_gpio::GetGPIO_Dev(DVBS_RESETZ);// 2nd demod 
                    if(gptr != NULL)
                    {
                        gptr->SetOff();
                    }

                    usleep(resetDemodTime*1000);

                    if(gptr != NULL)
                    {
                        gptr->SetOn();
                    }
                    
                }
                break;
        }


        //step 5. re-check dram CRC 

        u8RetryCnt--;


    }while(((u8RetryCnt>0)&&(flag != 0))||(status == false));

    if(u8RetryCnt == 0)
        printf("[msb1240][error]msb1240_flash_check_fail....\n");

    if((flag == 0)&&(status == true))
        printf("[msb1240] +++ msb1240_flash_check_ OK!!!\n");

    DBG_DEMOD_FLOW(printf("Exit %s...\n",__FUNCTION__));
    return status;
}


