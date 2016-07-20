////////////////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2008-2009 MStar Semiconductor, Inc.
// All rights reserved.
//
// Unless otherwise stipulated in writing, any and all information contained
// herein regardless in any format shall remain the sole proprietary of
// MStar Semiconductor Inc. and be kept in strict confidence
// ("MStar Confidential Information") by the recipient.
// Any unauthorized act including without limitation unauthorized disclosure,
// copying, use, reproduction, sale, distribution, modification, disassembling,
// reverse engineering and compiling of the contents of MStar Confidential
// Information is unlawful and strictly prohibited. MStar hereby reserves the
// rights to any and all damages, losses, costs and expenses resulting therefrom.
//
////////////////////////////////////////////////////////////////////////////////

#include "debug.h"
#include "mapi_system.h"
#include "drvGPIO.h"
#include "mapi_tuner.h"
#include "mapi_demodulator.h"
#include "mapi_pcb.h"
#include "mapi_vif.h"
#include "mapi_i2c.h"
#include "mapi_i2c_devTable.h"
#include "mapi_gpio.h"
#include "mapi_gpio_devTable.h"
#include "mapi_interface.h"
#include "mapi_system.h"
#include "device_demodulator.h"
#include "mapi_syscfg_table.h"
#include "SystemInfo.h"
#include "device_demodulator_monet.h"
#include "drvDMD_INTERN_ATSC.h"
#include "drvDMD_INTERN_DVBT.h"
#include "drvDMD_INTERN_DVBC.h"
#if (DTMB_SYSTEM_ENABLE==1)
#include "drvDMD_DTMB.h"
#endif
#include "drvDMD_VD_MBX.h"
#include "mapi_sar.h"
#include "drvSAR.h"
#include "MSrv_Control.h"
#include "MSrv_System_Database.h"
#include "MMAPInfo.h"

#if (ISDB_SYSTEM_ENABLE == 1)
#include "drvDMD_ISDBT.h"

//dan add for integrate ISDBT utopia driver[begin]
#include "apiSWI2C.h"
#include "drvHWI2C.h"
#define resetDemodTime 50
#define waitFlashTime 50
//dan add for integrate ISDBT utopia driver[end]

#define TS_PARALLEL_OUTPUT 1
#if TS_PARALLEL_OUTPUT
#define ISDBT_TS_SERIAL		  0x00
#else
#define ISDBT_TS_SERIAL		  0x01
#endif
// Division number of TS clock rate
// Formula: TS Clk Rate= 216 MHz/reg_isdbt_ts_clk_divnum
// Ex: ISDBT_TS_CLK_DIVNUM = 31 => TS Clk = 216/31= 6.967 MHz
#define ISDBT_TS_CLK_DIVNUM_SERIAL_MODE   4
#define ISDBT_TS_CLK_DIVNUM_PARALLEL_MODE  31

#if (ISDBT_TS_SERIAL)
#define ISDBT_TS_CLK_DIVNUM	  ISDBT_TS_CLK_DIVNUM_SERIAL_MODE
#else
#define ISDBT_TS_CLK_DIVNUM	  ISDBT_TS_CLK_DIVNUM_PARALLEL_MODE
#endif

#define ISDBT_TS_DATA_SWAP        0x00  // TS data inversion
#define ISDBT_TS_CLK_INV          0x01  // TS clock inverseion
#define ISDBT_IFAGC_REF           0x38    //dan note: this define will depend on tuner
#endif
#if (ATSC_SYSTEM_ENABLE == 0)
static DMD_SQI_CN_NORDIGP1 SqiCnNordigP1[] =
{
    {_QPSK,  _CR1Y2, 5.1 },
    {_QPSK,  _CR2Y3, 6.9 },
    {_QPSK,  _CR3Y4, 7.9 },
    {_QPSK,  _CR5Y6, 8.9 },
    {_QPSK,  _CR7Y8, 9.7 },
    {_16QAM, _CR1Y2, 10.8},
    {_16QAM, _CR2Y3, 13.1},
    {_16QAM, _CR3Y4, 14.6},
    {_16QAM, _CR5Y6, 15.6},
    {_16QAM, _CR7Y8, 16.0},
    {_64QAM, _CR1Y2, 16.5},
    {_64QAM, _CR2Y3, 18.7},
    {_64QAM, _CR3Y4, 20.2},
    {_64QAM, _CR5Y6, 21.6},
    {_64QAM, _CR7Y8, 22.5},
};
#endif
//#include "device_dvbc_t3.c"
//VIF handler patch for Tuning, or Tuning may lose channels
//Need VIF team's help to remove me.
#define VIF_HANDLER_PATCH         1
#define DMD_INIT_PARAM_LEN_MAX 256
#if VIF_HANDLER_PATCH
#ifdef __cplusplus
extern "C"
{
#endif
extern void DRV_VIF_Handler(MS_BOOL bAutoScan);
#ifdef __cplusplus
}
#endif
#endif


#if 0
#define DEMOD_DBG(fmt, arg...)           printf((char *)fmt, ##arg)
#define DEMOD_FLOW_DBG(fmt, arg...)      printf((char *)fmt, ##arg)
#define DEMOD_IFO(fmt, arg...)           printf((char *)fmt, ##arg)
#else
#define DEMOD_DBG(fmt, arg...)
#define DEMOD_FLOW_DBG(fmt, arg...)
#define DEMOD_IFO(fmt, arg...)
#endif

#define DBG_DEMOD_MSB(x)       //   x
#define DBG_DUMP_LOAD_DSP_TIME 0

#define MDrv_Timer_Delayms OS_DELAY_TASK
#define MDrv_Timer_GetTime0 OS_SYSTEM_TIME

#if (DTMB_SYSTEM_ENABLE==1)
#define DTMB_TUNER_IF           5000
#define DTMB_TS_SERIAL        0x01
#define DTMB_TS_CLK_DIVNUM_SERIAL_MODE   4
#define DTMB_TS_CLK_DIVNUM_PARALLEL_MODE  31

#if (DTMB_TS_SERIAL)
#define DTMB_TS_CLK_DIVNUM    DTMB_TS_CLK_DIVNUM_SERIAL_MODE
#else
#define DTMB_TS_CLK_DIVNUM    DTMB_TS_CLK_DIVNUM_PARALLEL_MODE
#endif

#define DTMB_TS_DATA_SWAP  0x00  // TS data inversion
#define DTMB_TS_CLK_INV      0  // TS clock inverseion //this define will depend on hw main board
// IQ Swap  //this define will depend on tuner

#if (DTMB_IQ_SWAP_ENABLE==1)
#define DTMB_IQ_SWAP   1
#else
#define DTMB_IQ_SWAP   0
#endif
#endif

MAPI_U8 device_demodulator::u8DeviceBusy=0;
MAPI_BOOL device_demodulator::m_bSerialOut=MAPI_TRUE;

MAPI_U8 device_demodulator::gVifTop = VIF_TOP;
MAPI_U8 device_demodulator::gVifSoundSystem = 0;//(MAPI_U8)mapi_vif_datatype::E_VIF_SOUND_DK2;
MAPI_U8 device_demodulator::gVifIfFreq = (MAPI_U8)mapi_vif_datatype::E_IF_FREQ_3890;
MAPI_U8 device_demodulator::u8MsbData[6] = {0};
MAPI_U8 device_demodulator::gu8ChipRevId = 0;
MAPI_U8 device_demodulator::gCalIdacCh0 = 0;
MAPI_U8 device_demodulator::gCalIdacCh1 = 0;
S_CMDPKTREG device_demodulator::gsCmdPacket = {0};
MAPI_U8 device_demodulator::gu8DemoDynamicI2cAddress=0x32;//Default
MAPI_BOOL     device_demodulator::FECLock = MAPI_FALSE;
MAPI_BOOL    device_demodulator::gbTVAutoScanEn=MAPI_FALSE;//init value=MAPI_FALSE, follow with auto/manual scan
mapi_demodulator_datatype::EN_DEVICE_DEMOD_TYPE device_demodulator::m_enCurrentDemodulator_Type = mapi_demodulator_datatype::E_DEVICE_DEMOD_NULL;

//@@++
mapi_demodulator_datatype::IF_FREQ device_demodulator::m_eIF_Freq = mapi_demodulator_datatype::IF_FREQ_INVALID;

MAPI_U32 device_demodulator::Tuner_TOP_Setting = 0xFF;
MAPI_U32 device_demodulator::Tuner_Top_Setting_LPrime=0x05;
MAPI_U32 device_demodulator::Tuner_Top_Setting_SECAM_VHF=0x04;
MAPI_U32 device_demodulator::Tuner_Top_Setting_SECAM_UHF=0x03;
MAPI_U32 device_demodulator::Tuner_Top_Setting_PAL_VHF=0x02;
MAPI_U32 device_demodulator::Tuner_Top_Setting_PAL_UHF=0x01;

MAPI_U16 device_demodulator::IfFreq = 0;
MAPI_BOOL device_demodulator::IFDM_Initialized = FALSE;
MAPI_U8 device_demodulator::Scanning_Active=0;
//@@++

MAPI_U16 g_u16_symbol_rate_list[] =
{
    1500, 2000, 2500, 3000, 3500,
    4000, 4500, 5000, 5500, 6000,
    6500, 7000,
};


#if 1
#define DemodCmd         0x110500L
#define DemodAdrL        0x110501L
#define DemodAdrH        0x110502L
#define DemodData        0x110503L
#define DemodCmdWrReg    0x01    // write register.
#define DemodCmdRdReg    0x02    // read register.


MAPI_U8 MDrv_T3VIF_ReadByte(MAPI_U32 u32Reg)
{
    #if 0
    MDrv_WriteByte(DemodAdrL, u32Reg&0xFF);
    MDrv_WriteByte(DemodAdrH, (u32Reg>>8)&0xFF);
    MDrv_WriteByte(DemodCmd, DemodCmdRdReg);
    usleep(100000);
    return MDrv_ReadByte(DemodData);
    #endif
    UNUSED(u32Reg);
    return 0;
}


void MDrv_T3VIF_WriteByte(MAPI_U32 u32Reg, MAPI_U8 u8Val)
{
    #if 0
    usleep(100000);
    MDrv_WriteByte(DemodAdrL, u32Reg&0xFF);
    MDrv_WriteByte(DemodAdrH, (u32Reg>>8)&0xFF);
    MDrv_WriteByte(DemodData, u8Val);
    MDrv_WriteByte(DemodCmd, DemodCmdWrReg);
    #endif
    UNUSED(u32Reg);
    UNUSED(u8Val);
}
#endif


//##########################################################################################################
//##########################################################################################################
//###############################  Public:Common Function Implementation ###################################
//##########################################################################################################
//##########################################################################################################


MAPI_BOOL device_demodulator::DeviceDemodCreate()
{
    mapi_demodulator *pMapiDemod;


    pMapiDemod = mapi_interface::Get_mapi_demod();

    if (pMapiDemod == NULL)
    {
        printf("%s(),%d Init Fail\n",__func__,__LINE__);
        return MAPI_FALSE;
    }

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
    pMapiDemod->stMapiDemodulatorFun.SetCurrentDemodulatorType = SetCurrentDemodulatorType;
    pMapiDemod->stMapiDemodulatorFun.ExtendCmd = ExtendCmd;


    //Public:ATV
    pMapiDemod->stMapiDemodulatorFun.ATV_GetAFC_Distance = ATV_GetAFC_Distance;
    pMapiDemod->stMapiDemodulatorFun.ATV_SetVIF_SoundSystem = ATV_SetVIF_SoundSystem;
    pMapiDemod->stMapiDemodulatorFun.ATV_SetAudioSawFilter = ATV_SetAudioSawFilter;
    pMapiDemod->stMapiDemodulatorFun.ATV_SetVIF_IfFreq = ATV_SetVIF_IfFreq;
    pMapiDemod->stMapiDemodulatorFun.ATV_VIF_Init = ATV_VIF_Init;
    pMapiDemod->stMapiDemodulatorFun.ATV_VIF_Handler = ATV_VIF_Handler;
    pMapiDemod->stMapiDemodulatorFun.ATV_GetVIF_Type = ATV_GetVIF_Type;
    pMapiDemod->stMapiDemodulatorFun.ExtendCmd = ExtendCmd;
//@@++
    pMapiDemod->stMapiDemodulatorFun.ATV_GetIF = ATV_GetIF;
    pMapiDemod->stMapiDemodulatorFun.ATV_SetIF = ATV_SetIF;
    pMapiDemod->stMapiDemodulatorFun.ATV_GetIFAGC = ATV_GetIFAGC;
    pMapiDemod->stMapiDemodulatorFun.ATV_GetVifIfFreq = ATV_GetVifIfFreq;
    pMapiDemod->stMapiDemodulatorFun.ATV_Initialized = ATV_Initialized;
    pMapiDemod->stMapiDemodulatorFun.ATV_ScanningStatus = ATV_ScanningStatus;
    pMapiDemod->stMapiDemodulatorFun.ATV_SetPeakingParameters = ATV_SetPeakingParameters;
    pMapiDemod->stMapiDemodulatorFun.ATV_SetAGCParameters = ATV_SetAGCParameters;
    pMapiDemod->stMapiDemodulatorFun.ATV_SetAudioNotch = ATV_SetAudioNotch;
    pMapiDemod->stMapiDemodulatorFun.ATV_GetVIF_InitailValue = ATV_GetVIF_InitailValue;
//@@++

    //Public:DTV
    pMapiDemod->stMapiDemodulatorFun.DTV_GetSNR = DTV_GetSNR;
    pMapiDemod->stMapiDemodulatorFun.DTV_GetBER = DTV_GetBER;
    pMapiDemod->stMapiDemodulatorFun.DTV_GetSignalQuality = DTV_GetSignalQuality;
    pMapiDemod->stMapiDemodulatorFun.DTV_GetPostBER = DTV_GetPostBER;
    pMapiDemod->stMapiDemodulatorFun.DTV_GetSignalStrength = DTV_GetSignalStrength;
    pMapiDemod->stMapiDemodulatorFun.DTV_GetCellID = DTV_GetCellID;
    pMapiDemod->stMapiDemodulatorFun.DTV_Serial_Control = DTV_Serial_Control;
    pMapiDemod->stMapiDemodulatorFun.DTV_IsHierarchyOn = DTV_IsHierarchyOn;
    pMapiDemod->stMapiDemodulatorFun.DTV_SetFrequency = DTV_SetFrequency;

#if (DVBT_SYSTEM_ENABLE == 1)
    //Public:DTV-DVB-T
    //pMapiDemod->stMapiDemodulatorFun.DTV_SetFrequency = DTV_SetFrequency;
    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_T_GetLockStatus = DTV_DVB_T_GetLockStatus;
    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_T_GetSignalModulation = DTV_DVB_T_GetSignalModulation;
    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_T_GetSignalGuardInterval = DTV_DVB_T_GetSignalGuardInterval;
    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_T_GetSignalFFTValue = DTV_DVB_T_GetSignalFFTValue;
    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_T_GetSignalCodeRate = DTV_DVB_T_GetSignalCodeRate;
#endif

#if (DVBC_SYSTEM_ENABLE == 1)
    //Public:DTV-DVB-C

    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_C_SetFrequency = DTV_DVB_C_SetFrequency;
    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_C_GetLockStatus = DTV_DVB_C_GetLockStatus;
    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_C_GetCurrentSymbolRate = DTV_DVB_C_GetCurrentSymbolRate;
    pMapiDemod->stMapiDemodulatorFun.DTV_DVB_C_GetSignalModulation = DTV_DVB_C_GetSignalModulation;
#endif
#if (ATSC_SYSTEM_ENABLE==1)
    //Public:DTV-ATSC
    pMapiDemod->stMapiDemodulatorFun.DTV_ATSC_GetLockStatus = DTV_ATSC_GetLockStatus;
    pMapiDemod->stMapiDemodulatorFun.DTV_ATSC_ClkEnable = DTV_ATSC_ClkEnable;
    pMapiDemod->stMapiDemodulatorFun.DTV_ATSC_GetModulationMode = DTV_ATSC_GetModulationMode;
    pMapiDemod->stMapiDemodulatorFun.DTV_ATSC_ChangeModulationMode = DTV_ATSC_ChangeModulationMode;
#endif
#if (ISDB_SYSTEM_ENABLE==1)
    //Public:DTV-ISDB
    pMapiDemod->stMapiDemodulatorFun.DTV_ISDB_GetLockStatus = DTV_ISDB_GetLockStatus;
#endif

#if (DTMB_SYSTEM_ENABLE==1)
    //Public:DTV-DTMB
    pMapiDemod->stMapiDemodulatorFun.DTV_DTMB_SetFrequency = DTV_DTMB_SetFrequency;
    pMapiDemod->stMapiDemodulatorFun.DTV_DTMB_GetProperity = DTV_DTMB_GetProperity;
    pMapiDemod->stMapiDemodulatorFun.DTV_DTMB_GetLockStatus = DTV_DTMB_GetLockStatus;
#endif
    pMapiDemod->stMapiDemodulatorFun.DTV_GetDemod_Version = DTV_GetDemod_Version;

    pMapiDemod->stMapiDemodulatorFun.DTV_GetPacketErr = DTV_GetPacketErr;

    MDrv_DMD_PreInit();
    return MAPI_TRUE;
}

#if (STR_ENABLE == 1)
MAPI_BOOL device_demodulator::SuspendDeInit()
{
#if (ATSC_SYSTEM_ENABLE==1)
    MDrv_DMD_ATSC_Initial_Hal_Interface();
    MDrv_DMD_ATSC_SetPowerState(E_POWER_SUSPEND);
#endif
    return MAPI_TRUE;
}
MAPI_BOOL device_demodulator::ResumeInit()
{
    MDrv_DMD_PreInit();
#if (ATSC_SYSTEM_ENABLE==1)
    MDrv_DMD_ATSC_Initial_Hal_Interface();
    MDrv_DMD_ATSC_SetPowerState(E_POWER_RESUME);
#endif
    return MAPI_TRUE;
}
#endif

MAPI_BOOL device_demodulator::Connect(mapi_demodulator_datatype::EN_DEVICE_DEMOD_TYPE enDemodType)
{
    MAPI_BOOL bReturn=MAPI_FALSE;
    SawArchitecture eSAWType;
    SarChannel eSARChannel=E_SAR_NC;
    printf("device_demodulator_monet: Connect %d\n", enDemodType);
    if (u8DeviceBusy==1)
        return MAPI_FALSE;

    eSAWType = SystemInfo::GetInstance()->GetSAWType();

    if(eSAWType < DUAL_SAW || eSAWType >= SAW_NUMS)
    {
        printf("\r\n Warning!!!!! Abnormal SAW type %d\n",eSAWType);
        ASSERT(0);
    }
    eSARChannel = SystemInfo::GetInstance()->GetSARChannel();

    if (!((eSARChannel < E_SAR_MAX_NUMS) || (eSARChannel == E_SAR_NC)))
    {
        eSARChannel=E_SAR_NC;
        printf("\r\n Warning!!!!! Abnormal SAR channel");
        ASSERT(0);
    }

    switch (enDemodType)
    {
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_ATV:
            break;
#if (DTMB_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DTMB:
        {
            static DMD_DTMB_InitData   sDMD_DTMB_InitData;
            MMapInfo_t*         m_DTMB_mem = NULL;
            static MS_U8        u8DMD_DTMB_InitExt[] = {1}; // RFAGC tristate control default value, 1:trisate 0:non-tristate,never modify unless you know the meaning

            // copy tuner config
            sDMD_DTMB_InitData.u16IF_KHZ = DTMB_TUNER_IF;//u16DtvIFFreqKHz;
            sDMD_DTMB_InitData.bIQSwap = DTMB_IQ_SWAP;

            //DTMB Mem Addr [begin]
            m_DTMB_mem = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_DTMB_IL_BUF"));

            if(m_DTMB_mem==NULL)
            {
                 printf("\n\n\n======m_DTMB_mem==NULL======\n\n\n\n");
            }
            else
            {
                 printf("JIKO^^^^^m_DTMB_addr/16:%x\n", (m_DTMB_mem->u32Addr)/16 );
                 sDMD_DTMB_InitData.u32TdiStartAddr = (m_DTMB_mem->u32Addr)/16 ;
            }

            sDMD_DTMB_InitData.u16DTMBAGCLockCheckTime = 50;
            sDMD_DTMB_InitData.u16DTMBPreLockCheckTime = 300;
            sDMD_DTMB_InitData.u16DTMBPNMLockCheckTime = 1200;  //7/24 ohya++
            sDMD_DTMB_InitData.u16DTMBFECLockCheckTime = 5000;

            // 7/24++
            sDMD_DTMB_InitData.u8IS_DUAL=0;//0
            sDMD_DTMB_InitData.bIsExtDemod=0;//0


            sDMD_DTMB_InitData.u16QAMAGCLockCheckTime = 50;
            sDMD_DTMB_InitData.u16QAMPreLockCheckTime = 1000;
            sDMD_DTMB_InitData.u16QAMMainLockCheckTime = 3000;

            sDMD_DTMB_InitData.u8DMD_DTMB_InitExt = u8DMD_DTMB_InitExt;

            //TS[begin]
            //Parallel mode
            //sDMD_DTMB_InitData.u5TsConfigByte_DivNum = DTMB_TS_CLK_DIVNUM;
            //sDMD_DTMB_InitData.u1TsConfigByte_ClockInv = DTMB_TS_CLK_INV;
            //sDMD_DTMB_InitData.u1TsConfigByte_DataSwap = 0;
            //sDMD_DTMB_InitData.u1TsConfigByte_SerialMode = DTMB_TS_SERIAL;
            //Serial mode
            sDMD_DTMB_InitData.u5TsConfigByte_DivNum = DTMB_TS_CLK_DIVNUM;
            sDMD_DTMB_InitData.u1TsConfigByte_ClockInv = DTMB_TS_CLK_INV;
            sDMD_DTMB_InitData.u1TsConfigByte_DataSwap = DTMB_TS_DATA_SWAP;
            sDMD_DTMB_InitData.u1TsConfigByte_SerialMode = DTMB_TS_SERIAL;

            MDrv_SYS_DMD_VD_MBX_Init();
            MDrv_DMD_DTMB_Initial_Hal_Interface();
            MDrv_DMD_DTMB_Init(&sDMD_DTMB_InitData, sizeof(sDMD_DTMB_InitData));
        }
        break;
#endif
#if (DVBT_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
        {

            DMD_DVBT_InitData sDMD_DVBT_InitData;
            mapi_tuner *pTuner=NULL;
            MS_U8 *pTUNER_DVBT_DSPRegInitExt=NULL;
            MS_U8 *pBOARD_DVBT_DSPRegInitExt=NULL;
            MS_U16 u16DMD_DVBT_DSPRegInitExtSize=0;
            DMD_SSI_TABLE *pSSITable=NULL;
            static MS_U8 u8DMD_DVBT_DSPRegInitExt[DMD_INIT_PARAM_LEN_MAX]={
                                                     1, // version, should be matched with library
                                                     0, // reserved
                                                     };

            sDMD_DVBT_InitData.u8DMD_DVBT_DSPRegInitSize=0;
            // copy tuner config
            pTuner = mapi_interface::Get_mapi_pcb()->GetDvbtTuner(0);
            if (pTuner!=NULL)
            {
                if ( MAPI_FALSE == pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_DEMOD_CONFIG, E_TUNER_DTV_DVB_T_MODE, 0, &pTUNER_DVBT_DSPRegInitExt))
                {
                    pTUNER_DVBT_DSPRegInitExt=NULL;
                }
            }
            else
            {
                pTUNER_DVBT_DSPRegInitExt=NULL;
                printf("%s Line:%d Error!!\n",__func__, __LINE__);
            }

            if (pTUNER_DVBT_DSPRegInitExt != NULL)
            {
                // add tuner init size
                u16DMD_DVBT_DSPRegInitExtSize=(((MS_U16)pTUNER_DVBT_DSPRegInitExt[3])<<8)+pTUNER_DVBT_DSPRegInitExt[2];
                if ((sDMD_DVBT_InitData.u8DMD_DVBT_DSPRegInitSize+u16DMD_DVBT_DSPRegInitExtSize)<(DMD_INIT_PARAM_LEN_MAX-4)/4)
                {
                    for (int i=0; i<u16DMD_DVBT_DSPRegInitExtSize*4; i++)
                    {
                        u8DMD_DVBT_DSPRegInitExt[i+2]=pTUNER_DVBT_DSPRegInitExt[i+4];
                    }
                    sDMD_DVBT_InitData.u8DMD_DVBT_DSPRegInitSize+=u16DMD_DVBT_DSPRegInitExtSize;
                }
                else
                {
                    printf("%s Line:%d Error!!\n",__func__, __LINE__);
                }
            }

            // copy board config
            SystemInfo::GetInstance()->GetDemodConfig(0, &pBOARD_DVBT_DSPRegInitExt, &(sDMD_DVBT_InitData.u8DMD_DVBT_InitExt));
            if (pBOARD_DVBT_DSPRegInitExt != NULL)
            {
                // add board init size
                u16DMD_DVBT_DSPRegInitExtSize=(((MS_U16)pBOARD_DVBT_DSPRegInitExt[3])<<8)+pBOARD_DVBT_DSPRegInitExt[2];
                if ((sDMD_DVBT_InitData.u8DMD_DVBT_DSPRegInitSize+u16DMD_DVBT_DSPRegInitExtSize)<(DMD_INIT_PARAM_LEN_MAX-4)/4)
                {
                    MS_U16 u16Offset=0;
                    u16Offset=2+sDMD_DVBT_InitData.u8DMD_DVBT_DSPRegInitSize*4;
                    for (int i=0; i<u16DMD_DVBT_DSPRegInitExtSize*4; i++)
                    {
                        u8DMD_DVBT_DSPRegInitExt[i+u16Offset]=pBOARD_DVBT_DSPRegInitExt[i+4];
                    }
                    sDMD_DVBT_InitData.u8DMD_DVBT_DSPRegInitSize+=u16DMD_DVBT_DSPRegInitExtSize;
                }
                else
                {
                    printf("%s Line:%d Error!!\n",__func__, __LINE__);
                }
            }

            // tuner parameter
            if (pTuner!=NULL)
            {
                pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_RF_TABLE, 0, 0, &pSSITable);
            }
            else
            {
                printf("%s Line:%d Error!!\n",__func__, __LINE__);
            }
            if (pSSITable != NULL)
            {
                if (pSSITable->pRfagcSsi != NULL)
                    sDMD_DVBT_InitData.u8SarChannel = eSARChannel; // 0xFF means un-connected
                else
                    sDMD_DVBT_InitData.u8SarChannel = E_SAR_NC; // 0xFF means un-connected
                sDMD_DVBT_InitData.pTuner_RfagcSsi = pSSITable->pRfagcSsi;
                sDMD_DVBT_InitData.u16Tuner_RfagcSsi_Size = pSSITable->u16RfagcSsi_Size;
                sDMD_DVBT_InitData.pTuner_IfagcSsi_LoRef = pSSITable->pIfagcSsi_LoRef;
                sDMD_DVBT_InitData.u16Tuner_IfagcSsi_LoRef_Size = pSSITable->u16IfagcSsi_LoRef_Size;
                sDMD_DVBT_InitData.pTuner_IfagcSsi_HiRef = pSSITable->pIfagcSsi_HiRef;
                sDMD_DVBT_InitData.u16Tuner_IfagcSsi_HiRef_Size = pSSITable->u16IfagcSsi_HiRef_Size;
                sDMD_DVBT_InitData.pTuner_IfagcErr_LoRef = pSSITable->pIfagcErr_LoRef;
                sDMD_DVBT_InitData.u16Tuner_IfagcErr_LoRef_Size = pSSITable->u16IfagcErr_LoRef_Size;
                sDMD_DVBT_InitData.pTuner_IfagcErr_HiRef = pSSITable->pIfagcErr_HiRef;
                sDMD_DVBT_InitData.u16Tuner_IfagcErr_HiRef_Size = pSSITable->u16IfagcErr_HiRef_Size;
            }
            else
            {
                sDMD_DVBT_InitData.u8SarChannel = E_SAR_NC; // 0xFF means un-connected
                sDMD_DVBT_InitData.pTuner_RfagcSsi = NULL;
                sDMD_DVBT_InitData.u16Tuner_RfagcSsi_Size = 0;
                sDMD_DVBT_InitData.pTuner_IfagcSsi_LoRef = NULL;
                sDMD_DVBT_InitData.u16Tuner_IfagcSsi_LoRef_Size = 0;
                sDMD_DVBT_InitData.pTuner_IfagcSsi_HiRef = NULL;
                sDMD_DVBT_InitData.u16Tuner_IfagcSsi_HiRef_Size = 0;
                sDMD_DVBT_InitData.pTuner_IfagcErr_LoRef = NULL;
                sDMD_DVBT_InitData.u16Tuner_IfagcErr_LoRef_Size = 0;
                sDMD_DVBT_InitData.pTuner_IfagcErr_HiRef = NULL;
                sDMD_DVBT_InitData.u16Tuner_IfagcErr_HiRef_Size = 0;
            }

            sDMD_DVBT_InitData.pSqiCnNordigP1 = SqiCnNordigP1;
            sDMD_DVBT_InitData.u16SqiCnNordigP1_Size = sizeof(SqiCnNordigP1) / sizeof(DMD_SQI_CN_NORDIGP1);

            // register init
            if (sDMD_DVBT_InitData.u8DMD_DVBT_DSPRegInitSize)
            {
                sDMD_DVBT_InitData.u8DMD_DVBT_DSPRegInitExt = u8DMD_DVBT_DSPRegInitExt; // TODO use system variable type
            }
            else
            {
                sDMD_DVBT_InitData.u8DMD_DVBT_DSPRegInitExt = NULL; // TODO use system variable type
            }
            MDrv_DMD_DVBT_Init(&sDMD_DVBT_InitData, sizeof(sDMD_DVBT_InitData)); // _UTOPIA
        }
        break;
#endif
#if (DVBC_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_C:
        {
            DMD_DVBC_InitData sDMD_DVBC_InitData;
            mapi_tuner *pTuner=NULL;
            MS_U8 *pTUNER_DVBC_DSPRegInitExt=NULL;
            MS_U8 *pBOARD_DVBC_DSPRegInitExt=NULL;
            MS_U16 u16DMD_DVBC_DSPRegInitExtSize=0;
            DMD_SSI_TABLE *pSSITable=NULL;
            static MS_U8 u8DMD_DVBC_DSPRegInitExt[DMD_INIT_PARAM_LEN_MAX]={
                                                     1, // version, should be matched with library
                                                     0, // reserved
                                                     };

            sDMD_DVBC_InitData.u8DMD_DVBC_DSPRegInitSize=0;
            // copy tuner config
            pTuner = mapi_interface::Get_mapi_pcb()->GetDvbcTuner(0);
            if (pTuner!=NULL)
            {
                if ( MAPI_FALSE == pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_DEMOD_CONFIG, E_TUNER_DTV_DVB_C_MODE, 0, &pTUNER_DVBC_DSPRegInitExt))
                {
                    pTUNER_DVBC_DSPRegInitExt=NULL;
                }
            }
            else
            {
                pTUNER_DVBC_DSPRegInitExt=NULL;
                printf("%s Line:%d Error!!\n",__func__, __LINE__);
            }

            if (pTUNER_DVBC_DSPRegInitExt != NULL)
            {
                // add tuner init size
                u16DMD_DVBC_DSPRegInitExtSize=(((MS_U16)pTUNER_DVBC_DSPRegInitExt[3])<<8)+pTUNER_DVBC_DSPRegInitExt[2];
                if ((sDMD_DVBC_InitData.u8DMD_DVBC_DSPRegInitSize+u16DMD_DVBC_DSPRegInitExtSize)<(DMD_INIT_PARAM_LEN_MAX-4)/4)
                {
                    for (int i=0; i<u16DMD_DVBC_DSPRegInitExtSize*4; i++)
                    {
                        u8DMD_DVBC_DSPRegInitExt[i+2]=pTUNER_DVBC_DSPRegInitExt[i+4];
                    }
                    sDMD_DVBC_InitData.u8DMD_DVBC_DSPRegInitSize+=u16DMD_DVBC_DSPRegInitExtSize;
                }
                else
                {
                    printf("%s Line:%d Error!!\n",__func__, __LINE__);
                }
            }

            // copy board config
            SystemInfo::GetInstance()->GetDemodConfig(1, &pBOARD_DVBC_DSPRegInitExt, &(sDMD_DVBC_InitData.u8DMD_DVBC_InitExt));
            if (pBOARD_DVBC_DSPRegInitExt != NULL)
            {
                // add board init size
                u16DMD_DVBC_DSPRegInitExtSize=(((MS_U16)pBOARD_DVBC_DSPRegInitExt[3])<<8)+pBOARD_DVBC_DSPRegInitExt[2];
                if ((sDMD_DVBC_InitData.u8DMD_DVBC_DSPRegInitSize+u16DMD_DVBC_DSPRegInitExtSize)<(DMD_INIT_PARAM_LEN_MAX-4)/4)
                {
                    MS_U16 u16Offset=0;
                    u16Offset=2+sDMD_DVBC_InitData.u8DMD_DVBC_DSPRegInitSize*4;
                    for (int i=0; i<u16DMD_DVBC_DSPRegInitExtSize*4; i++)
                    {
                        u8DMD_DVBC_DSPRegInitExt[i+u16Offset]=pBOARD_DVBC_DSPRegInitExt[i+4];
                    }
                    sDMD_DVBC_InitData.u8DMD_DVBC_DSPRegInitSize+=u16DMD_DVBC_DSPRegInitExtSize;
                }
                else
                {
                    printf("%s Line:%d Error!!\n",__func__, __LINE__);
                }
            }

            // tuner parameter
            pTuner = mapi_interface::Get_mapi_pcb()->GetDvbcTuner(0);
            if (pTuner!=NULL)
            {
                pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_RF_TABLE, 0, 0, &pSSITable);
            }
            else
            {
                printf("%s Line:%d Error!!\n",__func__, __LINE__);
            }
            if (pSSITable != NULL)
            {
                if (pSSITable->pRfagcSsi != NULL)
                    sDMD_DVBC_InitData.u8SarChannel = eSARChannel; // 0xFF means un-connected
                else
                    sDMD_DVBC_InitData.u8SarChannel = E_SAR_NC; // 0xFF means un-connected
                sDMD_DVBC_InitData.pTuner_RfagcSsi = pSSITable->pRfagcSsi;
                sDMD_DVBC_InitData.u16Tuner_RfagcSsi_Size = pSSITable->u16RfagcSsi_Size;
                sDMD_DVBC_InitData.pTuner_IfagcSsi_LoRef = pSSITable->pIfagcSsi_LoRef;
                sDMD_DVBC_InitData.u16Tuner_IfagcSsi_LoRef_Size = pSSITable->u16IfagcSsi_LoRef_Size;
                sDMD_DVBC_InitData.pTuner_IfagcSsi_HiRef = pSSITable->pIfagcSsi_HiRef;
                sDMD_DVBC_InitData.u16Tuner_IfagcSsi_HiRef_Size = pSSITable->u16IfagcSsi_HiRef_Size;
                sDMD_DVBC_InitData.pTuner_IfagcErr_LoRef = pSSITable->pIfagcErr_LoRef;
                sDMD_DVBC_InitData.u16Tuner_IfagcErr_LoRef_Size = pSSITable->u16IfagcErr_LoRef_Size;
                sDMD_DVBC_InitData.pTuner_IfagcErr_HiRef = pSSITable->pIfagcErr_HiRef;
                sDMD_DVBC_InitData.u16Tuner_IfagcErr_HiRef_Size = pSSITable->u16IfagcErr_HiRef_Size;
            }
            else
            {
                sDMD_DVBC_InitData.u8SarChannel = E_SAR_NC; // 0xFF means un-connected
                sDMD_DVBC_InitData.pTuner_RfagcSsi = NULL;
                sDMD_DVBC_InitData.u16Tuner_RfagcSsi_Size = 0;
                sDMD_DVBC_InitData.pTuner_IfagcSsi_LoRef = NULL;
                sDMD_DVBC_InitData.u16Tuner_IfagcSsi_LoRef_Size = 0;
                sDMD_DVBC_InitData.pTuner_IfagcSsi_HiRef = NULL;
                sDMD_DVBC_InitData.u16Tuner_IfagcSsi_HiRef_Size = 0;
                sDMD_DVBC_InitData.pTuner_IfagcErr_LoRef = NULL;
                sDMD_DVBC_InitData.u16Tuner_IfagcErr_LoRef_Size = 0;
                sDMD_DVBC_InitData.pTuner_IfagcErr_HiRef = NULL;
                sDMD_DVBC_InitData.u16Tuner_IfagcErr_HiRef_Size = 0;
            }

            sDMD_DVBC_InitData.pSqiCnNordigP1 = SqiCnNordigP1;
            sDMD_DVBC_InitData.u16SqiCnNordigP1_Size = sizeof(SqiCnNordigP1) / sizeof(DMD_SQI_CN_NORDIGP1);

            // register init
            if (sDMD_DVBC_InitData.u8DMD_DVBC_DSPRegInitSize)
            {
                sDMD_DVBC_InitData.u8DMD_DVBC_DSPRegInitExt = u8DMD_DVBC_DSPRegInitExt; // TODO use system variable type
            }
            else
            {
                sDMD_DVBC_InitData.u8DMD_DVBC_DSPRegInitExt = NULL; // TODO use system variable type
            }
            MDrv_DMD_DVBC_Init(&sDMD_DVBC_InitData, sizeof(sDMD_DVBC_InitData)); // _UTOPIA
        }
        break;
#endif
#if (ATSC_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_ATSC_VSB:
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_ATSC_64QAM:
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_ATSC_256QAM:
            {
                DMD_ATSC_InitData sDMD_ATSC_InitData;
                memset(&sDMD_ATSC_InitData,0,sizeof(DMD_ATSC_InitData));
                static MS_U8 u8DMD_ATSC_InitExt[]={1}; // RFAGC tristate control default value, 1:trisate 0:non-tristate,never modify unless you know the meaning
                U8 u8IqSwap = 0;
                U16 u16DtvIFFreqKHz = 0;
                MS_U8 *pBOARD_ATSC_DSPRegInitExt=NULL;
                MS_U8 *pBOARD_ATSC_DMD_InitConf=NULL;

                MDrv_DMD_ATSC_Initial_Hal_Interface();

                mapi_interface::Get_mapi_pcb()->GetAtscTuner(0)->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_IQ_SWAP, E_TUNER_DTV_ATSC_MODE, 0, &u8IqSwap);
                sDMD_ATSC_InitData.bIQSwap =  u8IqSwap;
                mapi_interface::Get_mapi_pcb()->GetAtscTuner(0)->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_DTV_IF_FREQ, E_TUNER_DTV_ATSC_MODE, 0, &u16DtvIFFreqKHz);
                sDMD_ATSC_InitData.u16IF_KHZ = u16DtvIFFreqKHz;

                // get ATSC demod config from board defind
                SystemInfo::GetInstance()->GetDemodConfig(2, &pBOARD_ATSC_DSPRegInitExt, &pBOARD_ATSC_DMD_InitConf);
                sDMD_ATSC_InitData.u16VSBAGCLockCheckTime = (((MS_U16)pBOARD_ATSC_DMD_InitConf[0])<<8)+pBOARD_ATSC_DMD_InitConf[1];
                sDMD_ATSC_InitData.u16VSBPreLockCheckTime = (((MS_U16)pBOARD_ATSC_DMD_InitConf[2])<<8)+pBOARD_ATSC_DMD_InitConf[3];
                sDMD_ATSC_InitData.u16VSBFSyncLockCheckTime = (((MS_U16)pBOARD_ATSC_DMD_InitConf[4])<<8)+pBOARD_ATSC_DMD_InitConf[5];
                sDMD_ATSC_InitData.u16VSBFECLockCheckTime = (((MS_U16)pBOARD_ATSC_DMD_InitConf[6])<<8)+pBOARD_ATSC_DMD_InitConf[7];
                sDMD_ATSC_InitData.u16QAMAGCLockCheckTime = (((MS_U16)pBOARD_ATSC_DMD_InitConf[8])<<8)+pBOARD_ATSC_DMD_InitConf[9];
                sDMD_ATSC_InitData.u16QAMPreLockCheckTime = (((MS_U16)pBOARD_ATSC_DMD_InitConf[10])<<8)+pBOARD_ATSC_DMD_InitConf[11];
                sDMD_ATSC_InitData.u16QAMMainLockCheckTime = (((MS_U16)pBOARD_ATSC_DMD_InitConf[12])<<8)+pBOARD_ATSC_DMD_InitConf[13];
                sDMD_ATSC_InitData.u16AGC_REFERENCE = (((MS_U16)pBOARD_ATSC_DMD_InitConf[14])<<8)+pBOARD_ATSC_DMD_InitConf[15];
                sDMD_ATSC_InitData.u8IS_DUAL = pBOARD_ATSC_DMD_InitConf[17];
                sDMD_ATSC_InitData.bIsExtDemod = pBOARD_ATSC_DMD_InitConf[18];
                #if 1
                printf("bIQSwap = %d\n",sDMD_ATSC_InitData.bIQSwap);
                printf("u16IF_KHZ = %d\n",sDMD_ATSC_InitData.u16IF_KHZ);
                printf("u16VSBAGCLockCheckTime = %d\n",sDMD_ATSC_InitData.u16VSBAGCLockCheckTime);
                printf("u16VSBPreLockCheckTime = %d\n",sDMD_ATSC_InitData.u16VSBPreLockCheckTime);
                printf("u16VSBFSyncLockCheckTime = %d\n",sDMD_ATSC_InitData.u16VSBFSyncLockCheckTime);
                printf("u16VSBFECLockCheckTime = %d\n",sDMD_ATSC_InitData.u16VSBFECLockCheckTime);
                printf("u16QAMAGCLockCheckTime = %d\n",sDMD_ATSC_InitData.u16QAMAGCLockCheckTime);
                printf("u16QAMPreLockCheckTime = %d\n",sDMD_ATSC_InitData.u16QAMPreLockCheckTime);
                printf("u16QAMMainLockCheckTime = %d\n",sDMD_ATSC_InitData.u16QAMMainLockCheckTime);
                printf("u16AGC_REFERENCE = %d\n",sDMD_ATSC_InitData.u16AGC_REFERENCE);
                printf("u8IS_DUAL = %d\n",sDMD_ATSC_InitData.u8IS_DUAL);
                printf("bIsExtDemod = %d\n",sDMD_ATSC_InitData.bIsExtDemod);
                #endif
                sDMD_ATSC_InitData.u8DMD_ATSC_DSPRegInitExt = pBOARD_ATSC_DSPRegInitExt;
                sDMD_ATSC_InitData.u8DMD_ATSC_DSPRegInitSize = 0;
                sDMD_ATSC_InitData.u8DMD_ATSC_InitExt = u8DMD_ATSC_InitExt;
                MDrv_DMD_ATSC_Init(&sDMD_ATSC_InitData, sizeof(sDMD_ATSC_InitData));
            }
            break;
#endif
#if (ISDB_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_ISDB:
            {
	        mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(DEMOD_DYNAMIC_SLAVE_ID_1);
	        DMD_ISDBT_InitData sDMD_ISDBT_InitData;
	        static MS_U8 u8DMD_ISDBT_InitExt[]={1}; // RFAGC tristate control default value, 1:trisate 0:non-tristate,never modify unless you know the meaning
        	//mapi_gpio *gptr = mapi_gpio::GetGPIO_Dev(Demodulator_Reset);
	        MS_U8 u8IqSwap = 0;
	        MS_U16 u16DtvIFFreqKHz = 0;


	        /* ISDB tuner info from ExtendCommand() is not ready, manuel value
	        is assigned   [begin] */
	        // copy tuner config
	        mapi_interface::Get_mapi_pcb()->GetIsdbTuner(0)->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_IQ_SWAP, E_TUNER_DTV_ISDB_MODE, 0, &u8IqSwap);
	        mapi_interface::Get_mapi_pcb()->GetIsdbTuner(0)->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_DTV_IF_FREQ, E_TUNER_DTV_ISDB_MODE, 0, &u16DtvIFFreqKHz);

	        sDMD_ISDBT_InitData.u16IF_KHZ = u16DtvIFFreqKHz; // 6000;
	        sDMD_ISDBT_InitData.bIQSwap = u8IqSwap; // 0;
	        /* ISDB tuner info from ExtendCommand() is not ready   [end] */


	        //depend on tuner
	        sDMD_ISDBT_InitData.u16AgcReferenceValue = 0x400;
	        sDMD_ISDBT_InitData.u16ISDBTFECLockCheckTime = 2000;
	        sDMD_ISDBT_InitData.u16ISDBTIcfoChExistCheckTime = 300;
	        sDMD_ISDBT_InitData.u8DMD_ISDBT_InitExt = u8DMD_ISDBT_InitExt;
                //sDMD_ISDBT_InitData.u16AgcReferenceValue = ISDBT_IFAGC_REF; //1400

	        //TS[begin]
	        //Parallel mode
	        sDMD_ISDBT_InitData.u5TsConfigByte_DivNum = ISDBT_TS_CLK_DIVNUM;
	        sDMD_ISDBT_InitData.u1TsConfigByte_ClockInv = ISDBT_TS_CLK_INV;
	        sDMD_ISDBT_InitData.u1TsConfigByte_DataSwap = ISDBT_TS_DATA_SWAP;
	        sDMD_ISDBT_InitData.u1TsConfigByte_SerialMode = ISDBT_TS_SERIAL;
	        //Serial mode
	        //sDMD_ISDBT_InitData.u5TsConfigByte_DivNum = 4;
	        //sDMD_ISDBT_InitData.u1TsConfigByte_ClockInv = 0;
	        //sDMD_ISDBT_InitData.u1TsConfigByte_DataSwap = 0;
	        //sDMD_ISDBT_InitData.u1TsConfigByte_SerialMode = 1;
	        //TS[end]

	        //I2C[begin]
	        sDMD_ISDBT_InitData.u8I2CSlaveAddr = iptr->GetSlaveAddr();
	        sDMD_ISDBT_InitData.u8I2CSlaveBus = 0;
	        sDMD_ISDBT_InitData.bIsExtDemod = FALSE;

	        sDMD_ISDBT_InitData.I2C_WriteBytes = MApi_SWI2C_WriteBytes;
	        sDMD_ISDBT_InitData.I2C_ReadBytes = MApi_SWI2C_ReadBytes;
	        //I2C[end]


	        //Isdb Mem Addr [begin]
	        MMapInfo_t * m_ISDB_mem;
                m_ISDB_mem = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_DMD_ISDBT"));
	        printf("JIKO^^^^^m_ISDB_addr/16:%x\n", (m_ISDB_mem->u32Addr) /16);
	        sDMD_ISDBT_InitData.u32TdiStartAddr = (m_ISDB_mem->u32Addr) /16;

                MDrv_SYS_DMD_VD_MBX_Init();
                MDrv_DMD_ISDBT_Initial_Hal_Interface();
                MDrv_DMD_ISDBT_Init(&sDMD_ISDBT_InitData, sizeof(sDMD_ISDBT_InitData));
            }
#endif
         default:
            break;
    }
    device_demodulator::m_enCurrentDemodulator_Type = enDemodType;
    u8DeviceBusy = 1;
    bReturn = MAPI_TRUE;
    return bReturn;
}


MAPI_BOOL device_demodulator::Disconnect(void)
{
    MAPI_BOOL bReturn=MAPI_FALSE;
    printf("device_demodulator_monet: Disconnect\n");
    u8DeviceBusy = 0;
    switch (device_demodulator::m_enCurrentDemodulator_Type)
    {
#if (DTMB_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DTMB:
            MDrv_DMD_DTMB_Exit();
            break;
#endif
#if (DVBT_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
            MDrv_DMD_DVBT_Exit(); // _UTOPIA
            break;
#endif
#if (DVBC_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_C:
            MDrv_DMD_DVBC_Exit(); // _UTOPIA
            break;
#endif
#if (ATSC_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_ATSC_VSB:
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_ATSC_64QAM:
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_ATSC_256QAM:
            MDrv_DMD_ATSC_Exit();
            break;
#endif
#if (ISDB_SYSTEM_ENABLE == 1)
	//dan add for integrate ISDBT utopia driver[begin]
	case mapi_demodulator_datatype::E_DEVICE_DEMOD_ISDB:
	    MDrv_DMD_ISDBT_Exit();
	    break;
#endif
         default:
            break;
    }
    return bReturn;
}



void device_demodulator::Reset()
{
    switch (device_demodulator::m_enCurrentDemodulator_Type)
    {
#if (ATSC_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_ATSC_VSB:
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_ATSC_64QAM:
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_ATSC_256QAM:
            MDrv_DMD_ATSC_SetReset();
            break;
#endif
#if (DTMB_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DTMB:
#endif
#if (ISDB_SYSTEM_ENABLE == 1)
	case mapi_demodulator_datatype::E_DEVICE_DEMOD_ISDB:
	    MDrv_DMD_ISDBT_SetReset();
	    break;
#endif

        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
            break;
         default:
            break;
    }
}


MAPI_BOOL device_demodulator::IIC_Bypass_Mode(MAPI_BOOL enable)
{
    UNUSED(enable);

    return MAPI_TRUE;
}


MAPI_BOOL device_demodulator::Power_On_Initialization(void)
{
    MAPI_BOOL bReturn = MAPI_TRUE;

    switch (device_demodulator::m_enCurrentDemodulator_Type)
    {
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_ATV:
                bReturn = ATV_SetVIF_IfFreq(38900L);
            break;
#if (DTMB_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DTMB:
            break;
#endif
#if (DVBT_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
            // _UTOPIA INTERN_DVBT_Power_On_Initialization();
            break;
#endif
#if (DVBC_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_C:
            // _UTOPIA INTERN_DVBC_Power_On_Initialization();
            break;
#endif
#if (ATSC_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_ATSC_VSB:
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_ATSC_64QAM:
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_ATSC_256QAM:
            // _UTOPIA
            break;
#endif
#if (ISDB_SYSTEM_ENABLE == 1)
	case mapi_demodulator_datatype::E_DEVICE_DEMOD_ISDB:
            break;
#endif
         default:
            ASSERT(0);
            break;
    }
    return bReturn;
}

MAPI_BOOL device_demodulator::Set_PowerOn(void)
{


    return MAPI_TRUE;
}

MAPI_BOOL device_demodulator::Set_PowerOff(void)
{


    return MAPI_TRUE;
}

MAPI_BOOL device_demodulator::Active(MAPI_BOOL bEnable)
{
     MAPI_BOOL bReturn = MAPI_FALSE;
     printf("device_demodulator::%s()\n",__func__);


     switch (device_demodulator::m_enCurrentDemodulator_Type)
    {
#if (DTMB_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DTMB:
            bReturn = MDrv_DMD_DTMB_SetConfig(DMD_DTMB_DEMOD_DTMB, bEnable); // _UTOPIA
            break;
#endif
#if (DVBT_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
            // _UTOPIA INTERN_DVBT_Active(ENABLE);
            MDrv_DMD_DVBT_SetActive(ENABLE); // _UTOPIA
            break;
#endif
#if (DVBC_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_C:
            // _UTOPIA INTERN_DVBC_Active(ENABLE);
            MDrv_DMD_DVBC_SetActive(ENABLE); // _UTOPIA
            break;
#endif
#if (ATSC_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_ATSC_VSB:
        printf("\033[45;37m  ""%s[%d] ::  E_DEVICE_DEMOD_ATSC_VSB \033[0m\n",__FUNCTION__,__LINE__);
            return MDrv_DMD_ATSC_SetConfig(DMD_ATSC_DEMOD_ATSC_VSB, bEnable);
            break;
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_ATSC_64QAM:
        printf("\033[45;37m  ""%s[%d] ::  E_DEVICE_DEMOD_ATSC_64QAM \033[0m\n",__FUNCTION__,__LINE__);
            return MDrv_DMD_ATSC_SetConfig(DMD_ATSC_DEMOD_ATSC_64QAM, bEnable);
            break;
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_ATSC_256QAM:
        printf("\033[45;37m  ""%s[%d] ::  E_DEVICE_DEMOD_ATSC_256QAM \033[0m\n",__FUNCTION__,__LINE__);
            return MDrv_DMD_ATSC_SetConfig(DMD_ATSC_DEMOD_ATSC_256QAM, bEnable);
            break;
#endif
#if (ISDB_SYSTEM_ENABLE == 1)
	//dan add for integrate ISDBT utopia driver[begin]
	case mapi_demodulator_datatype::E_DEVICE_DEMOD_ISDB:
            return MDrv_DMD_ISDBT_SetConfig(bEnable);
	    break;
#endif
         default:
            break;
    }

    return bReturn;
}

mapi_demodulator_datatype::EN_DEVICE_DEMOD_TYPE device_demodulator::GetCurrentDemodulatorType(void)
{
    return device_demodulator::m_enCurrentDemodulator_Type;
}


MAPI_BOOL device_demodulator::SetCurrentDemodulatorType(mapi_demodulator_datatype::EN_DEVICE_DEMOD_TYPE enDemodType)
{
    if(enDemodType == device_demodulator::m_enCurrentDemodulator_Type)
    {
        return MAPI_TRUE;
    }

    m_enCurrentDemodulator_Type = enDemodType;
    return MAPI_TRUE;
}


MAPI_BOOL device_demodulator::ExtendCmd(MAPI_U8 SubCmd, MAPI_U32 u32Param1, MAPI_U32 u32Param2, void *pvParam3)
{

   //1 => Write , 2 =>  Read
    if (SubCmd == 1)
    {//1 => Write
        printf("---> Write VIF Register...\n");
        //MDrv_T3VIF_WriteByte((MAPI_U32)u32Param1,(U8)u32Param2);
        //return MAPI_TRUE;
        //return MDrv_DMD_DVBT_SetReg((MS_U32) u32Param1,(MS_U8) u32Param2); // _UTOPIA
        return MDrv_SYS_DMD_VD_MBX_WriteReg((MS_U32) u32Param1,(MS_U8) u32Param2);
    }

    if (SubCmd == 2)
    { //2 =>  Read

        //U8 *pU8Data;

        printf("---> Read VIF Register...\n");
        //pU8Data = (U8 *)pvParam3;
        //*pU8Data = MDrv_T3VIF_ReadByte((MAPI_U32)u32Param1);
        //return MAPI_TRUE;
        //return MDrv_DMD_DVBT_GetReg((MS_U16)u32Param1,(MS_U8*)pvParam3); // _UTOPIA
        return MDrv_SYS_DMD_VD_MBX_ReadReg((MS_U16)u32Param1,(MS_U8*)pvParam3);
    }

    if (SubCmd ==3)
    {//Read register from dvbt demod
        // _UTOPIA return INTERN_DVBT_ReadReg((U16)u32Param1,(U8*)pvParam3);
        if (u32Param1 == 0x2123)
        {
            *((MS_U16*)pvParam3) = MDrv_SAR_Adc_GetValue(2);
        }
        else
        {
            MS_U8 u8Value;
            MDrv_DMD_DVBT_GetReg((MS_U16)u32Param1, &u8Value);
            *((MS_U16*)pvParam3) = u8Value;
        }
        //DTV_GetPostBER();
        //printf("GetReg %x %x\n",(MS_U16)u32Param1, *((MS_U16*)pvParam3));
        return TRUE;
    }

    if (SubCmd ==4)
    {//Write register from dvbt demod
        // _UTOPIA return INTERN_DVBT_WriteReg((U32) u32Param1,(U8) u32Param2);
        return MDrv_DMD_DVBT_SetReg((MS_U32) u32Param1,(MS_U8) u32Param2); // _UTOPIA
    }

    if (SubCmd ==5)
    {//Read VIF_CR_KI1 from VIF define
        MS_U8 *pU8Data;
        pU8Data = (MS_U8 *)pvParam3;
        *pU8Data = VIF_CR_KI1;
        return MAPI_TRUE;
    }

    if (SubCmd ==6)
    {//Read VIF_CR_KP1 from VIF define
        MS_U8 *pU8Data;
        pU8Data = (MS_U8 *)pvParam3;
        *pU8Data = VIF_CR_KP1;
        return MAPI_TRUE;
    }

    if(SubCmd == 0x40)
    {
        MDrv_DMD_DVBT_GetInfo(E_DMD_DVBT_MODULATION_INFO);
        return MAPI_TRUE;
    }

    if(SubCmd == 0x41)
    {
        MDrv_DMD_DVBT_GetInfo(E_DMD_DVBT_DEMOD_INFO);
        return MAPI_TRUE;
    }

    if(SubCmd == 0x42)
    {
        MDrv_DMD_DVBT_GetInfo(E_DMD_DVBT_LOCK_INFO);
        return MAPI_TRUE;
    }

    if(SubCmd == 0x43)
    {
        MDrv_DMD_DVBT_GetInfo(E_DMD_DVBT_PRESFO_INFO);
        return MAPI_TRUE;
    }

    if(SubCmd == 0x44)
    {
        MDrv_DMD_DVBT_GetInfo(E_DMD_DVBT_LOCK_TIME_INFO);
        return MAPI_TRUE;
    }

    if(SubCmd == 0x45)
    {
        MDrv_DMD_DVBT_GetInfo(E_DMD_DVBT_BER_INFO);
        return MAPI_TRUE;
    }

    if(SubCmd == 0x46)
    {
        MDrv_DMD_DVBT_GetInfo(E_DMD_DVBT_AGC_INFO);
        return MAPI_TRUE;
    }

    // write symbol rate list.
    if(SubCmd == 0x50)
    {
        MS_U8 index = 0;
        MS_U8 length = (MAPI_U8)u32Param1;
        MS_U16 *pArrary = (MS_U16*)pvParam3;


        if(length > (sizeof(g_u16_symbol_rate_list)/sizeof(MS_U16)))
          return MAPI_FALSE;

        for(index = 0; index < (sizeof(g_u16_symbol_rate_list)/sizeof(MS_U16)); index++)
          g_u16_symbol_rate_list[index] = 0;

        index = 0;

        for(index = 0; index < length; index++)
          g_u16_symbol_rate_list[index] = pArrary[index];

        return MAPI_TRUE;
    }

    // read symbol rate list.
    if(SubCmd == 0x51)
    {

        // return format, pArrary,
        // - pArrary[0], total symbol rate list number
        // - pArrary[1], 1st symbol rate.
        // - pArrary[2], 2nd symbol rate.
        // - ......
        // - pArrary[N], (N-1)th symbol rate.
        // available_length,
        // - pArrary length

        MS_U8 index = 0;

        // available_length = 1 + x, x is maximam allocated slot for symbol rate.
        MS_U8  available_length = (MAPI_U8)u32Param1;
        MS_U8  slot_available_length = available_length - 1;
        MS_U16 *pArrary = (MS_U16*)pvParam3;


        if(pArrary == NULL)
          return MAPI_FALSE;

        if(available_length == 0)
          return MAPI_FALSE;

        if( slot_available_length > (sizeof(g_u16_symbol_rate_list)/sizeof(MS_U16)) )
          slot_available_length = sizeof(g_u16_symbol_rate_list)/sizeof(MS_U16);

        for(index = 0;index < slot_available_length; index++)
        {
          pArrary[index + 1] = g_u16_symbol_rate_list[index];
        }

        pArrary[0] = (MS_U16)index;

        return MAPI_TRUE;

    }


    if(SubCmd == 0x80)  // temp solution, to turn on/off IF AGC
    {
        if(u32Param1 == 1)  // turn on
        {
            MDrv_DMD_RFAGC_Tristate(TRUE);
            MDrv_DMD_IFAGC_Tristate(FALSE);
        }
        else if(u32Param1 == 0)     // turn off
        {
            MDrv_DMD_RFAGC_Tristate(TRUE);
            MDrv_DMD_IFAGC_Tristate(TRUE);
        }
    }

    if(SubCmd == 0x82)  // to turn on/off null packet insert
    {
        MS_U8 u8Value;
        if(u32Param1 == 1)  // null packet on
        {
            MDrv_SYS_DMD_VD_MBX_ReadReg(0x1F20, &u8Value);
            MDrv_SYS_DMD_VD_MBX_WriteReg(0x1F20, u8Value | 0x04);
        }
        else if(u32Param1 == 0)     // null packet off
        {
            MDrv_SYS_DMD_VD_MBX_ReadReg(0x1F20, &u8Value);
            MDrv_SYS_DMD_VD_MBX_WriteReg(0x1F20, u8Value & (~0x04));
        }
    }

    return MAPI_TRUE;
}



//##########################################################################################################
//##########################################################################################################
//########################################  Public:ATV VIF Implementation ##################################
//##########################################################################################################
//##########################################################################################################


mapi_demodulator_datatype::AFC device_demodulator::ATV_GetAFC_Distance(void)
{

    MAPI_U8 u8Value = 0;
#if (MSTAR_TVOS == 1)
    MAPI_U8 i = 0;
    MAPI_BOOL bRet = MAPI_FALSE;

    for(i=0; i<5; i++)
    {
        u8Value = mapi_interface::Get_mapi_vif()->Read_CR_LOCK_STATUS();
        if (u8Value&0x01)
        {
            bRet = MAPI_TRUE;
            break;
        }
        usleep(500);
    }

    if (bRet == MAPI_FALSE)
    {
        return mapi_demodulator_datatype::E_AFC_OUT_OF_AFCWIN;
    }
#else
    u8Value=mapi_interface::Get_mapi_vif()->Read_CR_LOCK_STATUS();

    if (!(u8Value&0x01))
    {
        DEMOD_DBG("KKK: E_AFC_OUT_OF_AFCWIN \n");

        return mapi_demodulator_datatype::E_AFC_OUT_OF_AFCWIN;
     }
#endif

    u8Value=mapi_interface::Get_mapi_vif()->Read_CR_FOE();//For Eris one byte, AFC_FOE=register address

   // VIFDBG(printf("CR_FOE=%bx ",u8Value));

// FREQ_STEP_62_5KHz
    switch(u8Value)
    {
    case 0x00:
    case 0x01:
        return mapi_demodulator_datatype::E_AFC_PLUS_12p5KHz;//afcPLUS_12p5KHz;
    case 0x02:
        return mapi_demodulator_datatype::E_AFC_PLUS_37p5KHz;//afcPLUS_37p5KHz;
    case 0x03:
    case 0x04:
        return mapi_demodulator_datatype::E_AFC_PLUS_62p5KHz;//afcPLUS_62p5KHz;
    case 0x05:
        return mapi_demodulator_datatype::E_AFC_PLUS_87p5KHz;//afcPLUS_87p5KHz;
    case 0x06:
    case 0x07:
        return mapi_demodulator_datatype::E_AFC_PLUS_112p5KHz;//afcPLUS_112p5KHz;
    case 0x08:
        return mapi_demodulator_datatype::E_AFC_PLUS_137p5KHz;//afcPLUS_137p5KHz;
    case 0x09:
    case 0x0A:
        return mapi_demodulator_datatype::E_AFC_PLUS_162p5KHz;//afcPLUS_162p5KHz;
    case 0x0B:
    case 0x0C:
        return mapi_demodulator_datatype::E_AFC_ABOVE_PLUS_187p5KHz;//afcABOVE_PLUS_187p5KHz;

    case 0xFF:
        return mapi_demodulator_datatype::E_AFC_MINUS_12p5KHz;//afcMINUS_12p5KHz;
    case 0xFE:
        return mapi_demodulator_datatype::E_AFC_MINUS_37p5KHz;//afcMINUS_37p5KHz;
    case 0xFD:
    case 0xFC:
        return mapi_demodulator_datatype::E_AFC_MINUS_62p5KHz;//afcMINUS_62p5KHz;
    case 0xFB:
        return mapi_demodulator_datatype::E_AFC_MINUS_87p5KHz;//afcMINUS_87p5KHz;
    case 0xFA:
    case 0xF9:
        return mapi_demodulator_datatype::E_AFC_MINUS_112p5KHz;//afcMINUS_112p5KHz;
    case 0xF8:
        return mapi_demodulator_datatype::E_AFC_MINUS_137p5KHz;//afcMINUS_137p5KHz;
    case 0xF7:
    case 0xF6:
        return mapi_demodulator_datatype::E_AFC_MINUS_162p5KHz;//afcMINUS_162p5KHz;
    case 0xF5:
    case 0xF4:
        return mapi_demodulator_datatype::E_AFC_BELOW_MINUS_187p5KHz;//afcBELOW_MINUS_187p5KHz;
    default:
        break;
    }

    if(u8Value <= 0x20)  // within the range of +500KHz offset
    {
        return mapi_demodulator_datatype::E_AFC_ABOVE_PLUS_187p5KHz;//afcABOVE_PLUS_187p5KHz;
    }
    else if(u8Value >= 0xDF) // within the range of -500KHz offset
    {
        return mapi_demodulator_datatype::E_AFC_BELOW_MINUS_187p5KHz;//afcBELOW_MINUS_187p5KHz;
    }
    else
    {
        return mapi_demodulator_datatype::E_AFC_OUT_OF_AFCWIN;
    }
}

MAPI_BOOL device_demodulator::ATV_SetVIF_SoundSystem(mapi_demodulator_datatype::DEMOD_AUDIOSTANDARD_TYPE_ eIF_Freq)//B-ok
{
    DBG_DEMOD_MSB(printf("\r\n    Set SoundSystem:%x\n", (int)eIF_Freq));
    //DRV_VIF_SetSoundSystem((VIFSoundSystem)5);
    //return MAPI_TRUE;

    ATV_SetAudioSawFilter(SAW_FILTER_OTHERS_MODE);

    switch(eIF_Freq)
    {
        case mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_BG_:
        case mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_BG_A2_:
            mapi_interface::Get_mapi_vif()->SetSoundSystem(mapi_vif_datatype::E_VIF_SOUND_B_STEREO_A2);
            break;
        case mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_BG_NICAM_:
            mapi_interface::Get_mapi_vif()->SetSoundSystem(mapi_vif_datatype::E_VIF_SOUND_B_MONO_NICAM);
            break;
        case mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_I_:
            mapi_interface::Get_mapi_vif()->SetSoundSystem(mapi_vif_datatype::E_VIF_SOUND_I);
            break;
        case mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_DK1_A2_:
            mapi_interface::Get_mapi_vif()->SetSoundSystem(mapi_vif_datatype::E_VIF_SOUND_DK1_STEREO_A2);
            break;
        case mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_DK_:
        case mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_DK2_A2_:
            mapi_interface::Get_mapi_vif()->SetSoundSystem(mapi_vif_datatype::E_VIF_SOUND_DK2_STEREO_A2);
            break;
        case mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_DK3_A2_:
            mapi_interface::Get_mapi_vif()->SetSoundSystem(mapi_vif_datatype::E_VIF_SOUND_DK3_STEREO_A2);
            break;
        case mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_DK_NICAM_:
            mapi_interface::Get_mapi_vif()->SetSoundSystem(mapi_vif_datatype::E_VIF_SOUND_DK_MONO_NICAM);
            break;
        case mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_L_:
            mapi_interface::Get_mapi_vif()->SetSoundSystem(mapi_vif_datatype::E_VIF_SOUND_L);
            break;
        case mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_LP_:
            ATV_SetAudioSawFilter(SAW_FILTER_SECAML_MODE);
            mapi_interface::Get_mapi_vif()->SetSoundSystem(mapi_vif_datatype::E_VIF_SOUND_LL);
            break;
        case mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_:
        case mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_BTSC_:
        case mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_A2_:
        case mapi_demodulator_datatype::E_DEMOD_AUDIOSTANDARD_M_EIA_J_:
            mapi_interface::Get_mapi_vif()->SetSoundSystem(mapi_vif_datatype::E_VIF_SOUND_MN);
            break;

        default:
            break;
    }

    return MAPI_TRUE;

}

//@@++
mapi_demodulator_datatype::IF_FREQ device_demodulator::ATV_GetIF(void)
{
    return m_eIF_Freq;
}

MAPI_BOOL device_demodulator::ATV_SetIF(mapi_demodulator_datatype::IF_FREQ eIF_Freq)
{
    if(m_eIF_Freq == eIF_Freq)
    {
        return MAPI_TRUE;
    }

    m_eIF_Freq = eIF_Freq;
    return MAPI_TRUE;
}

MAPI_U32 device_demodulator::ATV_GetIFAGC(void)
{
    return Tuner_TOP_Setting;
}

MAPI_U16 device_demodulator::ATV_GetVifIfFreq(void)
{
    return IfFreq;
}

MAPI_BOOL device_demodulator::ATV_Initialized(void)
{
    return IFDM_Initialized;
}

MAPI_U8 device_demodulator::ATV_ScanningStatus(MAPI_BOOL status)
{
    DBG_DEMOD_MSB(printf(" >> MDrv_IFDM_ScanningStatus "));
    if (status)
    {
        DBG_DEMOD_MSB(printf(" Active \r\n"));
        Scanning_Active=1;
    }
    else
    {
        DBG_DEMOD_MSB(printf(" Passive \r\n"));
        Scanning_Active=0;
    }
    return Scanning_Active;
}

MAPI_BOOL device_demodulator::ATV_SetPeakingParameters(RFBAND eBand)
{
    MAPI_BOOL bRet = MAPI_FALSE;
    mapi_vif_datatype::stVIFUserFilter VIF_UserFilter;
    mapi_tuner* pTuner = mapi_interface::Get_mapi_pcb()->GetAtvTuner(0);
    ASSERT(pTuner);

    memset((MAPI_U8*)&VIF_UserFilter, 0, sizeof(mapi_vif_datatype::stVIFUserFilter));

    bRet = pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_PEAKING_PARAMETER, (MAPI_U32)eBand, (MAPI_U32)m_eIF_Freq, &VIF_UserFilter);

    switch(eBand)
    {
        case E_RFBAND_VHF_LOW:
            mapi_interface::Get_mapi_vif()->SetFreqBand(mapi_vif_datatype::E_FREQ_VHF_L);
            if((m_eIF_Freq==mapi_demodulator_datatype::IF_FREQ_B)||(m_eIF_Freq==mapi_demodulator_datatype::IF_FREQ_G))
            {
                //VIF_InitData.VifClampgainGainOvNegative=0X0600;
                VIF_UserFilter.VifUserPeakingFilterSelect=(m_eIF_Freq==mapi_demodulator_datatype::IF_FREQ_B)?mapi_vif_datatype::PK_B_VHF_L : mapi_vif_datatype::PK_GH_VHF_L;
                VIF_UserFilter.VifUserYcDelayFilterSelect = (m_eIF_Freq == mapi_demodulator_datatype::IF_FREQ_B) ? mapi_vif_datatype::YC_B_VHF_L : mapi_vif_datatype::YC_GH_VHF_L;
                VIF_UserFilter.VifUserGroupDelayFilterSelect = (m_eIF_Freq == mapi_demodulator_datatype::IF_FREQ_B) ? mapi_vif_datatype::GP_B_VHF_L : mapi_vif_datatype::GP_GH_VHF_L;

                if(!bRet)
                {
                    VIF_UserFilter.VifSos21FilterC0  = 0x02F8;
                    VIF_UserFilter.VifSos21FilterC1  = 0x0620;
                    VIF_UserFilter.VifSos21FilterC2  = 0x0207;
                    VIF_UserFilter.VifSos21FilterC3  = 0x0508;
                    VIF_UserFilter.VifSos21FilterC4  = 0x01DA;

                    VIF_UserFilter.VifSos22FilterC0  = 0x02EE;
                    VIF_UserFilter.VifSos22FilterC1  = 0x06C4;
                    VIF_UserFilter.VifSos22FilterC2  = 0x01F5;
                    VIF_UserFilter.VifSos22FilterC3  = 0x0512;
                    VIF_UserFilter.VifSos22FilterC4  = 0x0146;

                    VIF_UserFilter.VifSos31FilterC0  = 0x028B;
                    VIF_UserFilter.VifSos31FilterC1  = 0x06A8;
                    VIF_UserFilter.VifSos31FilterC2  = 0x0158;
                    VIF_UserFilter.VifSos31FilterC3  = 0x0576;
                    VIF_UserFilter.VifSos31FilterC4  = 0x0200;
                    VIF_UserFilter.VifSos32FilterC0  = 0x02B8;
                    VIF_UserFilter.VifSos32FilterC1  = 0x0713;
                    VIF_UserFilter.VifSos32FilterC2  = 0x00ED;
                    VIF_UserFilter.VifSos32FilterC3  = 0x0548;
                    VIF_UserFilter.VifSos32FilterC4  = 0x0200;
                }
            }
            else if(m_eIF_Freq==mapi_demodulator_datatype::IF_FREQ_DK)
            {
                //VIF_InitData.VifClampgainGainOvNegative=0X0600;
                VIF_UserFilter.VifUserPeakingFilterSelect=mapi_vif_datatype::PK_DK_VHF_L;
                VIF_UserFilter.VifUserYcDelayFilterSelect = mapi_vif_datatype::YC_DK_VHF_L;
                VIF_UserFilter.VifUserGroupDelayFilterSelect = mapi_vif_datatype::GP_DK_VHF_L;
                if(!bRet)
                {
                    VIF_UserFilter.VifSos21FilterC0  = 0x02F8;
                    VIF_UserFilter.VifSos21FilterC1  = 0x0620;
                    VIF_UserFilter.VifSos21FilterC2  = 0x0207;
                    VIF_UserFilter.VifSos21FilterC3  = 0x0508;
                    VIF_UserFilter.VifSos21FilterC4  = 0x01DA;

                    VIF_UserFilter.VifSos22FilterC0  = 0x02EE;
                    VIF_UserFilter.VifSos22FilterC1  = 0x06C4;
                    VIF_UserFilter.VifSos22FilterC2  = 0x01F5;
                    VIF_UserFilter.VifSos22FilterC3  = 0x0512;
                    VIF_UserFilter.VifSos22FilterC4  = 0x0146;

                    VIF_UserFilter.VifSos31FilterC0  = 0x028B;
                    VIF_UserFilter.VifSos31FilterC1  = 0x06A8;
                    VIF_UserFilter.VifSos31FilterC2  = 0x0158;
                    VIF_UserFilter.VifSos31FilterC3  = 0x0576;
                    VIF_UserFilter.VifSos31FilterC4  = 0x0200;
                    VIF_UserFilter.VifSos32FilterC0  = 0x02B8;
                    VIF_UserFilter.VifSos32FilterC1  = 0x0713;
                    VIF_UserFilter.VifSos32FilterC2  = 0x00ED;
                    VIF_UserFilter.VifSos32FilterC3  = 0x0548;
                    VIF_UserFilter.VifSos32FilterC4  = 0x0200;
                }
            }
            else if(m_eIF_Freq==mapi_demodulator_datatype::IF_FREQ_I)
            {
                //VIF_InitData.VifClampgainGainOvNegative=0X0690;
                VIF_UserFilter.VifUserPeakingFilterSelect=mapi_vif_datatype::PK_I_VHF_L;
                VIF_UserFilter.VifUserYcDelayFilterSelect = mapi_vif_datatype::YC_I_VHF_L;
                VIF_UserFilter.VifUserGroupDelayFilterSelect = mapi_vif_datatype::GP_I_VHF_L;

                if(!bRet)
                {
                    VIF_UserFilter.VifSos21FilterC0  = 0x02F8;
                    VIF_UserFilter.VifSos21FilterC1  = 0x0620;
                    VIF_UserFilter.VifSos21FilterC2  = 0x0207;
                    VIF_UserFilter.VifSos21FilterC3  = 0x0508;
                    VIF_UserFilter.VifSos21FilterC4  = 0x01DA;

                    VIF_UserFilter.VifSos22FilterC0  = 0x02EE;
                    VIF_UserFilter.VifSos22FilterC1  = 0x06C4;
                    VIF_UserFilter.VifSos22FilterC2  = 0x01F5;
                    VIF_UserFilter.VifSos22FilterC3  = 0x0512;
                    VIF_UserFilter.VifSos22FilterC4  = 0x0146;

                    VIF_UserFilter.VifSos31FilterC0  = 0x028B;
                    VIF_UserFilter.VifSos31FilterC1  = 0x06A8;
                    VIF_UserFilter.VifSos31FilterC2  = 0x0158;
                    VIF_UserFilter.VifSos31FilterC3  = 0x0576;
                    VIF_UserFilter.VifSos31FilterC4  = 0x0200;
                    VIF_UserFilter.VifSos32FilterC0  = 0x02B8;
                    VIF_UserFilter.VifSos32FilterC1  = 0x0713;
                    VIF_UserFilter.VifSos32FilterC2  = 0x00ED;
                    VIF_UserFilter.VifSos32FilterC3  = 0x0548;
                    VIF_UserFilter.VifSos32FilterC4  = 0x0200;
                }
            }
            else if(m_eIF_Freq == mapi_demodulator_datatype::IF_FREQ_L)
            {
                VIF_UserFilter.VifUserPeakingFilterSelect = mapi_vif_datatype::PK_L_VHF_L;
                VIF_UserFilter.VifUserYcDelayFilterSelect = mapi_vif_datatype::YC_L_VHF_L;
                VIF_UserFilter.VifUserGroupDelayFilterSelect = mapi_vif_datatype::GP_L_VHF_L;

                if(!bRet)
                {
                    VIF_UserFilter.VifSos21FilterC0  = 0x02F8;
                    VIF_UserFilter.VifSos21FilterC1  = 0x0620;
                    VIF_UserFilter.VifSos21FilterC2  = 0x0207;
                    VIF_UserFilter.VifSos21FilterC3  = 0x0508;
                    VIF_UserFilter.VifSos21FilterC4  = 0x01DA;

                    VIF_UserFilter.VifSos22FilterC0  = 0x02EE;
                    VIF_UserFilter.VifSos22FilterC1  = 0x06C4;
                    VIF_UserFilter.VifSos22FilterC2  = 0x01F5;
                    VIF_UserFilter.VifSos22FilterC3  = 0x0512;
                    VIF_UserFilter.VifSos22FilterC4  = 0x0146;

                    VIF_UserFilter.VifSos31FilterC0  = 0x028B;
                    VIF_UserFilter.VifSos31FilterC1  = 0x06A8;
                    VIF_UserFilter.VifSos31FilterC2  = 0x0158;
                    VIF_UserFilter.VifSos31FilterC3  = 0x0576;
                    VIF_UserFilter.VifSos31FilterC4  = 0x0200;
                    VIF_UserFilter.VifSos32FilterC0  = 0x02B8;
                    VIF_UserFilter.VifSos32FilterC1  = 0x0713;
                    VIF_UserFilter.VifSos32FilterC2  = 0x00ED;
                    VIF_UserFilter.VifSos32FilterC3  = 0x0548;
                    VIF_UserFilter.VifSos32FilterC4  = 0x0200;
                }
            }
            else if(m_eIF_Freq == mapi_demodulator_datatype::IF_FREQ_L_PRIME)
            {
                VIF_UserFilter.VifUserPeakingFilterSelect = mapi_vif_datatype::PK_LL_VHF_L;
                VIF_UserFilter.VifUserYcDelayFilterSelect = mapi_vif_datatype::YC_LL_VHF_L;
                VIF_UserFilter.VifUserGroupDelayFilterSelect = mapi_vif_datatype::GP_LL_VHF_L;

                if(!bRet)
                {
                    VIF_UserFilter.VifSos21FilterC0  = 0x02F8;
                    VIF_UserFilter.VifSos21FilterC1  = 0x0620;
                    VIF_UserFilter.VifSos21FilterC2  = 0x0207;
                    VIF_UserFilter.VifSos21FilterC3  = 0x0508;
                    VIF_UserFilter.VifSos21FilterC4  = 0x01DA;

                    VIF_UserFilter.VifSos22FilterC0  = 0x02EE;
                    VIF_UserFilter.VifSos22FilterC1  = 0x06C4;
                    VIF_UserFilter.VifSos22FilterC2  = 0x01F5;
                    VIF_UserFilter.VifSos22FilterC3  = 0x0512;
                    VIF_UserFilter.VifSos22FilterC4  = 0x0146;

                    VIF_UserFilter.VifSos31FilterC0  = 0x028B;
                    VIF_UserFilter.VifSos31FilterC1  = 0x06A8;
                    VIF_UserFilter.VifSos31FilterC2  = 0x0158;
                    VIF_UserFilter.VifSos31FilterC3  = 0x0576;
                    VIF_UserFilter.VifSos31FilterC4  = 0x0200;
                    VIF_UserFilter.VifSos32FilterC0  = 0x02B8;
                    VIF_UserFilter.VifSos32FilterC1  = 0x0713;
                    VIF_UserFilter.VifSos32FilterC2  = 0x00ED;
                    VIF_UserFilter.VifSos32FilterC3  = 0x0548;
                    VIF_UserFilter.VifSos32FilterC4  = 0x0200;
                }
            }
             else if(m_eIF_Freq == mapi_demodulator_datatype::IF_FREQ_MN)
            {
                VIF_UserFilter.VifUserPeakingFilterSelect = mapi_vif_datatype::PK_MN_VHF_L;
                VIF_UserFilter.VifUserYcDelayFilterSelect = mapi_vif_datatype::YC_MN_VHF_L;
                VIF_UserFilter.VifUserGroupDelayFilterSelect = mapi_vif_datatype::GP_MN_VHF_L;

                if(!bRet)
                {
                    VIF_UserFilter.VifSos21FilterC0  = 0x02F8;
                    VIF_UserFilter.VifSos21FilterC1  = 0x0620;
                    VIF_UserFilter.VifSos21FilterC2  = 0x0207;
                    VIF_UserFilter.VifSos21FilterC3  = 0x0508;
                    VIF_UserFilter.VifSos21FilterC4  = 0x01DA;

                    VIF_UserFilter.VifSos22FilterC0  = 0x02EE;
                    VIF_UserFilter.VifSos22FilterC1  = 0x06C4;
                    VIF_UserFilter.VifSos22FilterC2  = 0x01F5;
                    VIF_UserFilter.VifSos22FilterC3  = 0x0512;
                    VIF_UserFilter.VifSos22FilterC4  = 0x0146;

                    VIF_UserFilter.VifSos31FilterC0  = 0x028B;
                    VIF_UserFilter.VifSos31FilterC1  = 0x06A8;
                    VIF_UserFilter.VifSos31FilterC2  = 0x0158;
                    VIF_UserFilter.VifSos31FilterC3  = 0x0576;
                    VIF_UserFilter.VifSos31FilterC4  = 0x0200;
                    VIF_UserFilter.VifSos32FilterC0  = 0x02B8;
                    VIF_UserFilter.VifSos32FilterC1  = 0x0713;
                    VIF_UserFilter.VifSos32FilterC2  = 0x00ED;
                    VIF_UserFilter.VifSos32FilterC3  = 0x0548;
                    VIF_UserFilter.VifSos32FilterC4  = 0x0200;
                }                
            }
            break;

        case E_RFBAND_VHF_HIGH:
            mapi_interface::Get_mapi_vif()->SetFreqBand(mapi_vif_datatype::E_FREQ_VHF_H);
            if((m_eIF_Freq == mapi_demodulator_datatype::IF_FREQ_B) || (m_eIF_Freq == mapi_demodulator_datatype::IF_FREQ_G))
            {
                VIF_UserFilter.VifUserPeakingFilterSelect = (m_eIF_Freq == mapi_demodulator_datatype::IF_FREQ_B) ? mapi_vif_datatype::PK_B_VHF_H : mapi_vif_datatype::PK_GH_VHF_H;
                VIF_UserFilter.VifUserYcDelayFilterSelect = (m_eIF_Freq == mapi_demodulator_datatype::IF_FREQ_B) ? mapi_vif_datatype::YC_B_VHF_H : mapi_vif_datatype::YC_GH_VHF_H;
                VIF_UserFilter.VifUserGroupDelayFilterSelect = (m_eIF_Freq == mapi_demodulator_datatype::IF_FREQ_B) ? mapi_vif_datatype::GP_B_VHF_H : mapi_vif_datatype::GP_GH_VHF_H;

                if(!bRet)
                {
                    VIF_UserFilter.VifSos21FilterC0  = 0x02F8;
                    VIF_UserFilter.VifSos21FilterC1  = 0x0620;
                    VIF_UserFilter.VifSos21FilterC2  = 0x0207;
                    VIF_UserFilter.VifSos21FilterC3  = 0x0508;
                    VIF_UserFilter.VifSos21FilterC4  = 0x01DA;

                    VIF_UserFilter.VifSos22FilterC0  = 0x02EE;
                    VIF_UserFilter.VifSos22FilterC1  = 0x06C4;
                    VIF_UserFilter.VifSos22FilterC2  = 0x01F5;
                    VIF_UserFilter.VifSos22FilterC3  = 0x0512;
                    VIF_UserFilter.VifSos22FilterC4  = 0x0146;

                    VIF_UserFilter.VifSos31FilterC0  = 0x028B;
                    VIF_UserFilter.VifSos31FilterC1  = 0x06A8;
                    VIF_UserFilter.VifSos31FilterC2  = 0x0158;
                    VIF_UserFilter.VifSos31FilterC3  = 0x0576;
                    VIF_UserFilter.VifSos31FilterC4  = 0x0200;
                    VIF_UserFilter.VifSos32FilterC0  = 0x02B8;
                    VIF_UserFilter.VifSos32FilterC1  = 0x0713;
                    VIF_UserFilter.VifSos32FilterC2  = 0x00ED;
                    VIF_UserFilter.VifSos32FilterC3  = 0x0548;
                    VIF_UserFilter.VifSos32FilterC4  = 0x0200;
                }
            }
            else if(m_eIF_Freq == mapi_demodulator_datatype::IF_FREQ_DK)
            {
                VIF_UserFilter.VifUserPeakingFilterSelect = mapi_vif_datatype::PK_DK_VHF_H;
                VIF_UserFilter.VifUserYcDelayFilterSelect = mapi_vif_datatype::YC_DK_VHF_H;
                VIF_UserFilter.VifUserGroupDelayFilterSelect = mapi_vif_datatype::GP_DK_VHF_H;

                if(!bRet)
                {
                    VIF_UserFilter.VifSos21FilterC0  = 0x02F8;
                    VIF_UserFilter.VifSos21FilterC1  = 0x0620;
                    VIF_UserFilter.VifSos21FilterC2  = 0x0207;
                    VIF_UserFilter.VifSos21FilterC3  = 0x0508;
                    VIF_UserFilter.VifSos21FilterC4  = 0x01DA;

                    VIF_UserFilter.VifSos22FilterC0  = 0x02EE;
                    VIF_UserFilter.VifSos22FilterC1  = 0x06C4;
                    VIF_UserFilter.VifSos22FilterC2  = 0x01F5;
                    VIF_UserFilter.VifSos22FilterC3  = 0x0512;
                    VIF_UserFilter.VifSos22FilterC4  = 0x0146;

                    VIF_UserFilter.VifSos31FilterC0  = 0x028B;
                    VIF_UserFilter.VifSos31FilterC1  = 0x06A8;
                    VIF_UserFilter.VifSos31FilterC2  = 0x0158;
                    VIF_UserFilter.VifSos31FilterC3  = 0x0576;
                    VIF_UserFilter.VifSos31FilterC4  = 0x0200;
                    VIF_UserFilter.VifSos32FilterC0  = 0x02B8;
                    VIF_UserFilter.VifSos32FilterC1  = 0x0713;
                    VIF_UserFilter.VifSos32FilterC2  = 0x00ED;
                    VIF_UserFilter.VifSos32FilterC3  = 0x0548;
                    VIF_UserFilter.VifSos32FilterC4  = 0x0200;
                }
            }
            else if(m_eIF_Freq == mapi_demodulator_datatype::IF_FREQ_I)
            {
                //VIF_InitData.VifClampgainGainOvNegative=0X0690;
                VIF_UserFilter.VifUserPeakingFilterSelect = mapi_vif_datatype::PK_I_VHF_H;
                VIF_UserFilter.VifUserYcDelayFilterSelect = mapi_vif_datatype::YC_I_VHF_H;
                VIF_UserFilter.VifUserGroupDelayFilterSelect = mapi_vif_datatype::GP_I_VHF_H;

                if(!bRet)
                {
                    VIF_UserFilter.VifSos21FilterC0  = 0x02F8;
                    VIF_UserFilter.VifSos21FilterC1  = 0x0620;
                    VIF_UserFilter.VifSos21FilterC2  = 0x0207;
                    VIF_UserFilter.VifSos21FilterC3  = 0x0508;
                    VIF_UserFilter.VifSos21FilterC4  = 0x01DA;

                    VIF_UserFilter.VifSos22FilterC0  = 0x02EE;
                    VIF_UserFilter.VifSos22FilterC1  = 0x06C4;
                    VIF_UserFilter.VifSos22FilterC2  = 0x01F5;
                    VIF_UserFilter.VifSos22FilterC3  = 0x0512;
                    VIF_UserFilter.VifSos22FilterC4  = 0x0146;

                    VIF_UserFilter.VifSos31FilterC0  = 0x028B;
                    VIF_UserFilter.VifSos31FilterC1  = 0x06A8;
                    VIF_UserFilter.VifSos31FilterC2  = 0x0158;
                    VIF_UserFilter.VifSos31FilterC3  = 0x0576;
                    VIF_UserFilter.VifSos31FilterC4  = 0x0200;
                    VIF_UserFilter.VifSos32FilterC0  = 0x02B8;
                    VIF_UserFilter.VifSos32FilterC1  = 0x0713;
                    VIF_UserFilter.VifSos32FilterC2  = 0x00ED;
                    VIF_UserFilter.VifSos32FilterC3  = 0x0548;
                    VIF_UserFilter.VifSos32FilterC4  = 0x0200;
                }
            }
            else if(m_eIF_Freq == mapi_demodulator_datatype::IF_FREQ_L)
            {
                VIF_UserFilter.VifUserPeakingFilterSelect = mapi_vif_datatype::PK_L_VHF_H;
                VIF_UserFilter.VifUserYcDelayFilterSelect = mapi_vif_datatype::YC_L_VHF_H;
                VIF_UserFilter.VifUserGroupDelayFilterSelect = mapi_vif_datatype::GP_L_VHF_H;

                if(!bRet)
                {
                    VIF_UserFilter.VifSos21FilterC0  = 0x02F8;
                    VIF_UserFilter.VifSos21FilterC1  = 0x0620;
                    VIF_UserFilter.VifSos21FilterC2  = 0x0207;
                    VIF_UserFilter.VifSos21FilterC3  = 0x0508;
                    VIF_UserFilter.VifSos21FilterC4  = 0x01DA;

                    VIF_UserFilter.VifSos22FilterC0  = 0x02EE;
                    VIF_UserFilter.VifSos22FilterC1  = 0x06C4;
                    VIF_UserFilter.VifSos22FilterC2  = 0x01F5;
                    VIF_UserFilter.VifSos22FilterC3  = 0x0512;
                    VIF_UserFilter.VifSos22FilterC4  = 0x0146;

                    VIF_UserFilter.VifSos31FilterC0  = 0x028B;
                    VIF_UserFilter.VifSos31FilterC1  = 0x06A8;
                    VIF_UserFilter.VifSos31FilterC2  = 0x0158;
                    VIF_UserFilter.VifSos31FilterC3  = 0x0576;
                    VIF_UserFilter.VifSos31FilterC4  = 0x0200;
                    VIF_UserFilter.VifSos32FilterC0  = 0x02B8;
                    VIF_UserFilter.VifSos32FilterC1  = 0x0713;
                    VIF_UserFilter.VifSos32FilterC2  = 0x00ED;
                    VIF_UserFilter.VifSos32FilterC3  = 0x0548;
                    VIF_UserFilter.VifSos32FilterC4  = 0x0200;
                }
            }
            else if(m_eIF_Freq == mapi_demodulator_datatype::IF_FREQ_L_PRIME)
            {
                VIF_UserFilter.VifUserPeakingFilterSelect = mapi_vif_datatype::PK_LL_VHF_H;
                VIF_UserFilter.VifUserYcDelayFilterSelect = mapi_vif_datatype::YC_LL_VHF_H;
                VIF_UserFilter.VifUserGroupDelayFilterSelect = mapi_vif_datatype::GP_LL_VHF_H;

                if(!bRet)
                {
                    VIF_UserFilter.VifSos21FilterC0  = 0x02F8;
                    VIF_UserFilter.VifSos21FilterC1  = 0x0620;
                    VIF_UserFilter.VifSos21FilterC2  = 0x0207;
                    VIF_UserFilter.VifSos21FilterC3  = 0x0508;
                    VIF_UserFilter.VifSos21FilterC4  = 0x01DA;

                    VIF_UserFilter.VifSos22FilterC0  = 0x02EE;
                    VIF_UserFilter.VifSos22FilterC1  = 0x06C4;
                    VIF_UserFilter.VifSos22FilterC2  = 0x01F5;
                    VIF_UserFilter.VifSos22FilterC3  = 0x0512;
                    VIF_UserFilter.VifSos22FilterC4  = 0x0146;

                    VIF_UserFilter.VifSos31FilterC0  = 0x028B;
                    VIF_UserFilter.VifSos31FilterC1  = 0x06A8;
                    VIF_UserFilter.VifSos31FilterC2  = 0x0158;
                    VIF_UserFilter.VifSos31FilterC3  = 0x0576;
                    VIF_UserFilter.VifSos31FilterC4  = 0x0200;
                    VIF_UserFilter.VifSos32FilterC0  = 0x02B8;
                    VIF_UserFilter.VifSos32FilterC1  = 0x0713;
                    VIF_UserFilter.VifSos32FilterC2  = 0x00ED;
                    VIF_UserFilter.VifSos32FilterC3  = 0x0548;
                    VIF_UserFilter.VifSos32FilterC4  = 0x0200;
                }
            }
            else if(m_eIF_Freq == mapi_demodulator_datatype::IF_FREQ_MN)
            {
                VIF_UserFilter.VifUserPeakingFilterSelect = mapi_vif_datatype::PK_MN_VHF_H;
                VIF_UserFilter.VifUserYcDelayFilterSelect = mapi_vif_datatype::YC_MN_VHF_H;
                VIF_UserFilter.VifUserGroupDelayFilterSelect = mapi_vif_datatype::GP_MN_VHF_H;

                if(!bRet)
                {
                    VIF_UserFilter.VifSos21FilterC0  = 0x02F8;
                    VIF_UserFilter.VifSos21FilterC1  = 0x0620;
                    VIF_UserFilter.VifSos21FilterC2  = 0x0207;
                    VIF_UserFilter.VifSos21FilterC3  = 0x0508;
                    VIF_UserFilter.VifSos21FilterC4  = 0x01DA;

                    VIF_UserFilter.VifSos22FilterC0  = 0x02EE;
                    VIF_UserFilter.VifSos22FilterC1  = 0x06C4;
                    VIF_UserFilter.VifSos22FilterC2  = 0x01F5;
                    VIF_UserFilter.VifSos22FilterC3  = 0x0512;
                    VIF_UserFilter.VifSos22FilterC4  = 0x0146;

                    VIF_UserFilter.VifSos31FilterC0  = 0x028B;
                    VIF_UserFilter.VifSos31FilterC1  = 0x06A8;
                    VIF_UserFilter.VifSos31FilterC2  = 0x0158;
                    VIF_UserFilter.VifSos31FilterC3  = 0x0576;
                    VIF_UserFilter.VifSos31FilterC4  = 0x0200;
                    VIF_UserFilter.VifSos32FilterC0  = 0x02B8;
                    VIF_UserFilter.VifSos32FilterC1  = 0x0713;
                    VIF_UserFilter.VifSos32FilterC2  = 0x00ED;
                    VIF_UserFilter.VifSos32FilterC3  = 0x0548;
                    VIF_UserFilter.VifSos32FilterC4  = 0x0200;
                }                    
            }
            break;

        case E_RFBAND_UHF:
            mapi_interface::Get_mapi_vif()->SetFreqBand(mapi_vif_datatype::E_FREQ_UHF);
            if((m_eIF_Freq == mapi_demodulator_datatype::IF_FREQ_B) || (m_eIF_Freq == mapi_demodulator_datatype::IF_FREQ_G))
            {
                VIF_UserFilter.VifUserPeakingFilterSelect = (m_eIF_Freq == mapi_demodulator_datatype::IF_FREQ_B) ? mapi_vif_datatype::PK_B_UHF : mapi_vif_datatype::PK_GH_UHF;
                VIF_UserFilter.VifUserYcDelayFilterSelect = (m_eIF_Freq == mapi_demodulator_datatype::IF_FREQ_B) ? mapi_vif_datatype::YC_B_UHF : mapi_vif_datatype::YC_GH_UHF;
                VIF_UserFilter.VifUserGroupDelayFilterSelect = (m_eIF_Freq == mapi_demodulator_datatype::IF_FREQ_B) ? mapi_vif_datatype::GP_B_UHF : mapi_vif_datatype::GP_GH_UHF;

                if(!bRet)
                {
                    VIF_UserFilter.VifSos21FilterC0  = 0x02EE;
                    VIF_UserFilter.VifSos21FilterC1  = 0x062D;
                    VIF_UserFilter.VifSos21FilterC2  = 0x0209;
                    VIF_UserFilter.VifSos21FilterC3  = 0x0512;
                    VIF_UserFilter.VifSos21FilterC4  = 0x01CA;

                    VIF_UserFilter.VifSos22FilterC0  = 0x0291;
                    VIF_UserFilter.VifSos22FilterC1  = 0x070A;
                    VIF_UserFilter.VifSos22FilterC2  = 0x01D9;
                    VIF_UserFilter.VifSos22FilterC3  = 0x056F;
                    VIF_UserFilter.VifSos22FilterC4  = 0x011D;

                    VIF_UserFilter.VifSos31FilterC0  = 0x02D3;
                    VIF_UserFilter.VifSos31FilterC1  = 0x0685;
                    VIF_UserFilter.VifSos31FilterC2  = 0x017B;
                    VIF_UserFilter.VifSos31FilterC3  = 0x052D;
                    VIF_UserFilter.VifSos31FilterC4  = 0x0200;
                    VIF_UserFilter.VifSos32FilterC0  = 0x02E1;
                    VIF_UserFilter.VifSos32FilterC1  = 0x06F7;
                    VIF_UserFilter.VifSos32FilterC2  = 0x010A;
                    VIF_UserFilter.VifSos32FilterC3  = 0x051F;
                    VIF_UserFilter.VifSos32FilterC4  = 0x0200;
                }
            }
            else if(m_eIF_Freq == mapi_demodulator_datatype::IF_FREQ_DK)
            {
                VIF_UserFilter.VifUserPeakingFilterSelect = mapi_vif_datatype::PK_DK_UHF;
                VIF_UserFilter.VifUserYcDelayFilterSelect = mapi_vif_datatype::YC_DK_UHF;
                VIF_UserFilter.VifUserGroupDelayFilterSelect = mapi_vif_datatype::GP_DK_UHF;
                if(!bRet)
                {
                    VIF_UserFilter.VifSos21FilterC0  = 0x02EE;
                    VIF_UserFilter.VifSos21FilterC1  = 0x062D;
                    VIF_UserFilter.VifSos21FilterC2  = 0x0209;
                    VIF_UserFilter.VifSos21FilterC3  = 0x0512;
                    VIF_UserFilter.VifSos21FilterC4  = 0x01CA;

                    VIF_UserFilter.VifSos22FilterC0  = 0x0291;
                    VIF_UserFilter.VifSos22FilterC1  = 0x070A;
                    VIF_UserFilter.VifSos22FilterC2  = 0x01D9;
                    VIF_UserFilter.VifSos22FilterC3  = 0x056F;
                    VIF_UserFilter.VifSos22FilterC4  = 0x011D;

                    VIF_UserFilter.VifSos31FilterC0  = 0x02D3;
                    VIF_UserFilter.VifSos31FilterC1  = 0x0685;
                    VIF_UserFilter.VifSos31FilterC2  = 0x017B;
                    VIF_UserFilter.VifSos31FilterC3  = 0x052D;
                    VIF_UserFilter.VifSos31FilterC4  = 0x0200;
                    VIF_UserFilter.VifSos32FilterC0  = 0x02E1;
                    VIF_UserFilter.VifSos32FilterC1  = 0x06F7;
                    VIF_UserFilter.VifSos32FilterC2  = 0x010A;
                    VIF_UserFilter.VifSos32FilterC3  = 0x051F;
                    VIF_UserFilter.VifSos32FilterC4  = 0x0200;
                }
            }
            else if(m_eIF_Freq == mapi_demodulator_datatype::IF_FREQ_I)
            {
                VIF_UserFilter.VifUserPeakingFilterSelect = mapi_vif_datatype::PK_I_UHF;
                VIF_UserFilter.VifUserYcDelayFilterSelect = mapi_vif_datatype::YC_I_UHF;
                VIF_UserFilter.VifUserGroupDelayFilterSelect = mapi_vif_datatype::GP_I_UHF;
                if(!bRet)
                {
                    VIF_UserFilter.VifSos21FilterC0  = 0x02EE;
                    VIF_UserFilter.VifSos21FilterC1  = 0x062D;
                    VIF_UserFilter.VifSos21FilterC2  = 0x0209;
                    VIF_UserFilter.VifSos21FilterC3  = 0x0512;
                    VIF_UserFilter.VifSos21FilterC4  = 0x01CA;

                    VIF_UserFilter.VifSos22FilterC0  = 0x0291;
                    VIF_UserFilter.VifSos22FilterC1  = 0x070A;
                    VIF_UserFilter.VifSos22FilterC2  = 0x01D9;
                    VIF_UserFilter.VifSos22FilterC3  = 0x056F;
                    VIF_UserFilter.VifSos22FilterC4  = 0x011D;

                    VIF_UserFilter.VifSos31FilterC0  = 0x02D3;
                    VIF_UserFilter.VifSos31FilterC1  = 0x0685;
                    VIF_UserFilter.VifSos31FilterC2  = 0x017B;
                    VIF_UserFilter.VifSos31FilterC3  = 0x052D;
                    VIF_UserFilter.VifSos31FilterC4  = 0x0200;
                    VIF_UserFilter.VifSos32FilterC0  = 0x02E1;
                    VIF_UserFilter.VifSos32FilterC1  = 0x06F7;
                    VIF_UserFilter.VifSos32FilterC2  = 0x010A;
                    VIF_UserFilter.VifSos32FilterC3  = 0x051F;
                    VIF_UserFilter.VifSos32FilterC4  = 0x0200;
                }
            }
            else if(m_eIF_Freq == mapi_demodulator_datatype::IF_FREQ_L)
            {
                VIF_UserFilter.VifUserPeakingFilterSelect = mapi_vif_datatype::PK_L_UHF;
                VIF_UserFilter.VifUserYcDelayFilterSelect = mapi_vif_datatype::YC_L_UHF;
                VIF_UserFilter.VifUserGroupDelayFilterSelect = mapi_vif_datatype::GP_L_UHF;
                if(!bRet)
                {
                    VIF_UserFilter.VifSos21FilterC0  = 0x02EE;
                    VIF_UserFilter.VifSos21FilterC1  = 0x062D;
                    VIF_UserFilter.VifSos21FilterC2  = 0x0209;
                    VIF_UserFilter.VifSos21FilterC3  = 0x0512;
                    VIF_UserFilter.VifSos21FilterC4  = 0x01CA;

                    VIF_UserFilter.VifSos22FilterC0  = 0x0291;
                    VIF_UserFilter.VifSos22FilterC1  = 0x070A;
                    VIF_UserFilter.VifSos22FilterC2  = 0x01D9;
                    VIF_UserFilter.VifSos22FilterC3  = 0x056F;
                    VIF_UserFilter.VifSos22FilterC4  = 0x011D;

                    VIF_UserFilter.VifSos31FilterC0  = 0x02D3;
                    VIF_UserFilter.VifSos31FilterC1  = 0x0685;
                    VIF_UserFilter.VifSos31FilterC2  = 0x017B;
                    VIF_UserFilter.VifSos31FilterC3  = 0x052D;
                    VIF_UserFilter.VifSos31FilterC4  = 0x0200;
                    VIF_UserFilter.VifSos32FilterC0  = 0x02E1;
                    VIF_UserFilter.VifSos32FilterC1  = 0x06F7;
                    VIF_UserFilter.VifSos32FilterC2  = 0x010A;
                    VIF_UserFilter.VifSos32FilterC3  = 0x051F;
                    VIF_UserFilter.VifSos32FilterC4  = 0x0200;
                }
            }
            else if(m_eIF_Freq == mapi_demodulator_datatype::IF_FREQ_L_PRIME)
            {
                VIF_UserFilter.VifUserPeakingFilterSelect = mapi_vif_datatype::PK_LL_UHF;
                VIF_UserFilter.VifUserYcDelayFilterSelect = mapi_vif_datatype::YC_LL_UHF;
                VIF_UserFilter.VifUserGroupDelayFilterSelect = mapi_vif_datatype::GP_LL_UHF;
                if(!bRet)
                {
                    VIF_UserFilter.VifSos21FilterC0  = 0x02EE;
                    VIF_UserFilter.VifSos21FilterC1  = 0x062D;
                    VIF_UserFilter.VifSos21FilterC2  = 0x0209;
                    VIF_UserFilter.VifSos21FilterC3  = 0x0512;
                    VIF_UserFilter.VifSos21FilterC4  = 0x01CA;

                    VIF_UserFilter.VifSos22FilterC0  = 0x0291;
                    VIF_UserFilter.VifSos22FilterC1  = 0x070A;
                    VIF_UserFilter.VifSos22FilterC2  = 0x01D9;
                    VIF_UserFilter.VifSos22FilterC3  = 0x056F;
                    VIF_UserFilter.VifSos22FilterC4  = 0x011D;

                    VIF_UserFilter.VifSos31FilterC0  = 0x02D3;
                    VIF_UserFilter.VifSos31FilterC1  = 0x0685;
                    VIF_UserFilter.VifSos31FilterC2  = 0x017B;
                    VIF_UserFilter.VifSos31FilterC3  = 0x052D;
                    VIF_UserFilter.VifSos31FilterC4  = 0x0200;
                    VIF_UserFilter.VifSos32FilterC0  = 0x02E1;
                    VIF_UserFilter.VifSos32FilterC1  = 0x06F7;
                    VIF_UserFilter.VifSos32FilterC2  = 0x010A;
                    VIF_UserFilter.VifSos32FilterC3  = 0x051F;
                    VIF_UserFilter.VifSos32FilterC4  = 0x0200;
                }
            }
            else if(m_eIF_Freq == mapi_demodulator_datatype::IF_FREQ_MN)
            {
                VIF_UserFilter.VifUserPeakingFilterSelect = mapi_vif_datatype::PK_MN_UHF;
                VIF_UserFilter.VifUserYcDelayFilterSelect = mapi_vif_datatype::YC_MN_UHF;
                VIF_UserFilter.VifUserGroupDelayFilterSelect = mapi_vif_datatype::GP_MN_UHF;

                if(!bRet)
                {
                    VIF_UserFilter.VifSos21FilterC0  = 0x02EE;
                    VIF_UserFilter.VifSos21FilterC1  = 0x062D;
                    VIF_UserFilter.VifSos21FilterC2  = 0x0209;
                    VIF_UserFilter.VifSos21FilterC3  = 0x0512;
                    VIF_UserFilter.VifSos21FilterC4  = 0x01CA;

                    VIF_UserFilter.VifSos22FilterC0  = 0x0291;
                    VIF_UserFilter.VifSos22FilterC1  = 0x070A;
                    VIF_UserFilter.VifSos22FilterC2  = 0x01D9;
                    VIF_UserFilter.VifSos22FilterC3  = 0x056F;
                    VIF_UserFilter.VifSos22FilterC4  = 0x011D;

                    VIF_UserFilter.VifSos31FilterC0  = 0x02D3;
                    VIF_UserFilter.VifSos31FilterC1  = 0x0685;
                    VIF_UserFilter.VifSos31FilterC2  = 0x017B;
                    VIF_UserFilter.VifSos31FilterC3  = 0x052D;
                    VIF_UserFilter.VifSos31FilterC4  = 0x0200;
                    VIF_UserFilter.VifSos32FilterC0  = 0x02E1;
                    VIF_UserFilter.VifSos32FilterC1  = 0x06F7;
                    VIF_UserFilter.VifSos32FilterC2  = 0x010A;
                    VIF_UserFilter.VifSos32FilterC3  = 0x051F;
                    VIF_UserFilter.VifSos32FilterC4  = 0x0200;
                }                    
            }
            break;

        default:
            break;
    }
    mapi_interface::Get_mapi_vif()->VifSetParameter(mapi_vif_datatype::E_VIF_PARA_USER_FILTER, &VIF_UserFilter, sizeof(mapi_vif_datatype::stVIFUserFilter));

    return MAPI_TRUE;
}

MAPI_BOOL device_demodulator::ATV_SetAudioNotch(void)
{
    // no use, notch will be set by sound system in mapi_vif::SetSoundSystem
    return MAPI_TRUE;
}

MAPI_BOOL device_demodulator::ATV_SetAudioSawFilter(MAPI_U8 u8SawFilterMode)
{
    mapi_gpio *gptr = mapi_gpio::GetGPIO_Dev(SECAM_L_PRIME);

    DBG_DEMOD_MSB(printf(">>>J2: Enter ATV_SetAudioSawFilter() = 0x%x\n", u8SawFilterMode));

    if (gptr == NULL)
    {
        return MAPI_FALSE;
    }

    if(u8SawFilterMode > SAW_FILTER_OTHERS_MODE)
        return MAPI_FALSE;


    if(u8SawFilterMode == SAW_FILTER_SECAML_MODE)
    {
        gptr->SetOn();
        //printf("---> Set Saw = 1\n");
        DBG_DEMOD_MSB(printf(">>>J2: Set Saw = 1\n"));
    }
    else
    {
        gptr->SetOff();
        DBG_DEMOD_MSB(printf(">>>J2: Set Saw = 0\n"));
    }

    return MAPI_TRUE;
}

MAPI_BOOL device_demodulator::ATV_SetAGCParameters(mapi_demodulator_datatype::EN_EMC_IF_AGC_SETTINGS par,MAPI_U32 val)
{
    switch (par)
    {
        case mapi_demodulator_datatype::EMC_IF_AGC_LPRIME_SETTING:
            Tuner_Top_Setting_LPrime=val;
            DBG_DEMOD_MSB(printf("LPRIME AGC Setting %d",Tuner_Top_Setting_LPrime));
        break;
        case mapi_demodulator_datatype::EMC_IF_AGC_SECAM_VHF_SETTING:
            Tuner_Top_Setting_SECAM_VHF=val;
            DBG_DEMOD_MSB(printf("SECAM VHF AGC Setting %d \r\n",Tuner_Top_Setting_SECAM_VHF));
        break;
        case mapi_demodulator_datatype::EMC_IF_AGC_SECAM_UHF_SETTING:
            Tuner_Top_Setting_SECAM_UHF=val;
            DBG_DEMOD_MSB(printf("SECAM UHF AGC Setting %d \r\n",Tuner_Top_Setting_SECAM_UHF));
        break;
        case mapi_demodulator_datatype::EMC_IF_AGC_PAL_VHF_SETTING:
            Tuner_Top_Setting_PAL_VHF=val;
            DBG_DEMOD_MSB(printf("PAL VHF  AGC Setting %d \r\n",Tuner_Top_Setting_PAL_VHF));
        break;
        case mapi_demodulator_datatype::EMC_IF_AGC_PAL_UHF_SETTING:
            Tuner_Top_Setting_PAL_UHF=val;
            DBG_DEMOD_MSB(printf("PAL UHF AGC Setting %d \r\n",Tuner_Top_Setting_PAL_UHF));
        break;
        default :
            DBG_DEMOD_MSB(printf("???? AGC ERROR   \r\n"));
        break;

    }

    return MAPI_TRUE;
}
//@@++



//static MS_U32 mtime;
MAPI_BOOL device_demodulator::ATV_SetVIF_IfFreq(MAPI_U16 u16IfFreq)
{
    mapi_vif_datatype::EN_IF_FREQ_TYPE eIF_FreqTmp;


    DBG_DEMOD_MSB(printf("\n\n>>>monet: Enter ATV_SetVIF_IfFreq() = 0x%x\r\n", u16IfFreq));


    if ((u16IfFreq == 33950L)||(u16IfFreq == 33900L)||(u16IfFreq == 33625L))
        eIF_FreqTmp = mapi_vif_datatype::E_IF_FREQ_3395;   // SECAM-L'
    else if (u16IfFreq == 38000L)
        eIF_FreqTmp = mapi_vif_datatype::E_IF_FREQ_3800;   // PAL
    else if (u16IfFreq == 38900L)
        eIF_FreqTmp = mapi_vif_datatype::E_IF_FREQ_3890;   // PAL
    else if (u16IfFreq == 39500L)
        eIF_FreqTmp = mapi_vif_datatype::E_IF_FREQ_3950;   // only for PAL-I
    else if (u16IfFreq == 45750L)
        eIF_FreqTmp = mapi_vif_datatype::E_IF_FREQ_4575;   // NTSC-M/N
    else if (u16IfFreq == 58750L)
        eIF_FreqTmp = mapi_vif_datatype::E_IF_FREQ_5875;   // NTSC-M/N
    else
        //eIF_FreqTmp = IF_FREQ_3890;   // PAL
        return MAPI_FALSE;


    //printf("MsVifIffre=%bX\n",eIF_FreqTmp);

    gVifIfFreq = (MAPI_U8)eIF_FreqTmp;
    DBG_DEMOD_MSB(printf("MsVifIffre=%d\n",(int)eIF_FreqTmp));
    mapi_interface::Get_mapi_vif()->SetIfFreq(eIF_FreqTmp);

    if((u16IfFreq == 33950L) || (u16IfFreq == 33900L) || (u16IfFreq == 33625L))
        ATV_SetAudioSawFilter(SAW_FILTER_SECAML_MODE);
    else
        ATV_SetAudioSawFilter(SAW_FILTER_OTHERS_MODE);

    #if VIF_HANDLER_PATCH
    //printf("\ndiff time= %u\n",mapi_interface::Get_mapi_system()->TimeDiffFromNow0(mtime));
    //mtime=mapi_interface::Get_mapi_system()->GetTime0();
    DRV_VIF_Handler(true);
    #else
    mapi_interface::Get_mapi_vif()->Handler(true);
    #endif
    return MAPI_TRUE;

}


MAPI_BOOL device_demodulator::ATV_GetVIF_InitailValue(mapi_vif_datatype::stVIFInitialIn *VIFInitialIn_inst)
{
    //mapi_vif_datatype::stVIFInitialIn VIFInitialIn_inst;
    SawArchitecture eSAWType;
    MAPI_U32 u32VifIfBaseFreq = 0;

    //printf("MDrv_VIF_Init\r\n");
    VIFInitialIn_inst->VifTop = VIF_TOP;
    //VIFInitialIn_inst->VifIfBaseFreq = VIF_IF_BASE_FREQ;
    //VIFInitialIn_inst->VifTunerStepSize = VIF_TUNER_STEP_SIZE;

    mapi_tuner* pTuner = mapi_interface::Get_mapi_pcb()->GetAtvTuner(0);
    ASSERT(pTuner);

    pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_IF_FREQ, 0, 0, &u32VifIfBaseFreq);
    VIFInitialIn_inst->VifIfBaseFreq = ((u32VifIfBaseFreq == 38000L)? IF_FREQ_38_00 : ((u32VifIfBaseFreq == 45750L)? IF_FREQ_45_75 : IF_FREQ_38_90));
    pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_FREQ_STEP, 0, 0, &VIFInitialIn_inst->VifTunerStepSize);

    eSAWType = SystemInfo::GetInstance()->GetSAWType();

    VIFInitialIn_inst->VifSawArch = eSAWType;
    if( eSAWType == DUAL_SAW )
    {
        VIFInitialIn_inst->VifAgcRefNegative = VIF_AGC_REF_NEGATIVE_DUAL_SAW;
        VIFInitialIn_inst->VifDagc1GainOv = VIF_DAGC1_GAIN_OV_DUAL_SAW;
        VIFInitialIn_inst->VifDagc2GainOv = VIF_DAGC2_GAIN_OV_DUAL_SAW;
        VIFInitialIn_inst->VifClampgainGainOvNegative = VIF_CLAMPGAIN_GAIN_OV_NEGATIVE_DUAL_SAW;
        VIFInitialIn_inst->VifClampgainGainOvPositive = VIF_CLAMPGAIN_GAIN_OV_POSITIVE_DUAL_SAW;
    }
    else if( eSAWType == EXTERNAL_SINGLE_SAW )
    {
        VIFInitialIn_inst->VifAgcRefNegative = VIF_AGC_REF_NEGATIVE_EXTERNAL_SINGLE_SAW;
        VIFInitialIn_inst->VifDagc1GainOv = VIF_DAGC1_GAIN_OV_EXTERNAL_SINGLE_SAW;
        VIFInitialIn_inst->VifDagc2GainOv = VIF_DAGC2_GAIN_OV_EXTERNAL_SINGLE_SAW;
        VIFInitialIn_inst->VifClampgainGainOvNegative = VIF_CLAMPGAIN_GAIN_OV_NEGATIVE_EXTERNAL_SINGLE_SAW;
        VIFInitialIn_inst->VifClampgainGainOvPositive = VIF_CLAMPGAIN_GAIN_OV_POSITIVE_EXTERNAL_SINGLE_SAW;
    }
    else if( eSAWType == SILICON_TUNER )
    {
        VIFInitialIn_inst->VifAgcRefNegative = VIF_AGC_REF_NEGATIVE_SILICON_TUNER;
        VIFInitialIn_inst->VifDagc1GainOv = VIF_DAGC1_GAIN_OV_SILICON_TUNER;
        VIFInitialIn_inst->VifDagc2GainOv = VIF_DAGC2_GAIN_OV_SILICON_TUNER;
        VIFInitialIn_inst->VifClampgainGainOvNegative = VIF_CLAMPGAIN_GAIN_OV_NEGATIVE_SILICON_TUNER;
        VIFInitialIn_inst->VifClampgainGainOvPositive = VIF_CLAMPGAIN_GAIN_OV_POSITIVE_SILICON_TUNER;
    }
    else if( eSAWType == INTERNAL_SINGLE_SAW_DIF )
    {
        VIFInitialIn_inst->VifAgcRefNegative = VIF_AGC_REF_NEGATIVE_INTERNAL_SINGLE_SAW_DIF;
        VIFInitialIn_inst->VifDagc1GainOv = VIF_DAGC1_GAIN_OV_INTERNAL_SINGLE_SAW_DIF;
        VIFInitialIn_inst->VifDagc2GainOv = VIF_DAGC2_GAIN_OV_INTERNAL_SINGLE_SAW_DIF;
        VIFInitialIn_inst->VifClampgainGainOvNegative = VIF_CLAMPGAIN_GAIN_OV_NEGATIVE_INTERNAL_SINGLE_SAW_DIF;
        VIFInitialIn_inst->VifClampgainGainOvPositive = VIF_CLAMPGAIN_GAIN_OV_POSITIVE_INTERNAL_SINGLE_SAW_DIF;
    }
    else if( eSAWType == NO_SAW )
    {
        VIFInitialIn_inst->VifAgcRefNegative = VIF_AGC_REF_NEGATIVE_NO_SAW;
        VIFInitialIn_inst->VifDagc1GainOv = VIF_DAGC1_GAIN_OV_NO_SAW;
        VIFInitialIn_inst->VifDagc2GainOv = VIF_DAGC2_GAIN_OV_NO_SAW;
        VIFInitialIn_inst->VifClampgainGainOvNegative = VIF_CLAMPGAIN_GAIN_OV_NEGATIVE_NO_SAW;
        VIFInitialIn_inst->VifClampgainGainOvPositive = VIF_CLAMPGAIN_GAIN_OV_POSITIVE_NO_SAW;
    }
    else //( eSAWType == INTERNAL_SINGLE_SAW_VIF )
    {
        VIFInitialIn_inst->VifAgcRefNegative = VIF_AGC_REF_NEGATIVE_INTERNAL_SINGLE_SAW_VIF;
        VIFInitialIn_inst->VifDagc1GainOv = VIF_DAGC1_GAIN_OV_INTERNAL_SINGLE_SAW_VIF;
        VIFInitialIn_inst->VifDagc2GainOv = VIF_DAGC2_GAIN_OV_INTERNAL_SINGLE_SAW_VIF;
        VIFInitialIn_inst->VifClampgainGainOvNegative = VIF_CLAMPGAIN_GAIN_OV_NEGATIVE_INTERNAL_SINGLE_SAW_VIF;
        VIFInitialIn_inst->VifClampgainGainOvPositive = VIF_CLAMPGAIN_GAIN_OV_POSITIVE_INTERNAL_SINGLE_SAW_VIF;
    }
    VIFInitialIn_inst->VifVgaMaximum = VIF_VGA_MAXIMUM;
    VIFInitialIn_inst->VifVgaMinimum = VIF_VGA_MINIMUM;
    VIFInitialIn_inst->GainDistributionThr = GAIN_DISTRIBUTION_THR;

    VIFInitialIn_inst->VifAgcVgaBase=VIF_AGC_VGA_BASE;
    VIFInitialIn_inst->VifAgcVgaOffs=VIF_AGC_VGA_OFFS;

    VIFInitialIn_inst->VifAgcRefPositive = VIF_AGC_REF_POSITIVE;
    VIFInitialIn_inst->VifDagc1Ref = VIF_DAGC1_REF;
    VIFInitialIn_inst->VifDagc2Ref = VIF_DAGC2_REF;
    VIFInitialIn_inst->VifCrKf1=VIF_CR_KF1;
    VIFInitialIn_inst->VifCrKp1=VIF_CR_KP1;
    VIFInitialIn_inst->VifCrKi1=VIF_CR_KI1;
    VIFInitialIn_inst->VifCrKp2=VIF_CR_KP2;
    VIFInitialIn_inst->VifCrKi2=VIF_CR_KI2;

    VIFInitialIn_inst->VifCrKp = VIF_CR_KP;
    VIFInitialIn_inst->VifCrKi = VIF_CR_KI;
    VIFInitialIn_inst->VifCrLockThr = VIF_CR_LOCK_THR;

    VIFInitialIn_inst->VifCrThr= VIF_CR_THR;
    VIFInitialIn_inst->VifCrLockNum=VIF_CR_LOCK_NUM;
    VIFInitialIn_inst->VifCrUnlockNum=VIF_CR_UNLOCK_NUM;
    VIFInitialIn_inst->VifCrPdErrMax=VIF_CR_PD_ERR_MAX;
    VIFInitialIn_inst->VifCrLockLeakySel=VIF_CR_LOCK_LEAKY_SEL;
    VIFInitialIn_inst->VifCrPdX2=VIF_CR_PD_X2;
    VIFInitialIn_inst->VifCrLpfSel=VIF_CR_LPF_SEL;
    VIFInitialIn_inst->VifCrPdModeSel=VIF_CR_PD_MODE_SEL;
    VIFInitialIn_inst->VifCrKpKiAdjust=VIF_CR_KP_KI_ADJUST;
    VIFInitialIn_inst->VifCrKpKiAdjustGear=VIF_CR_KP_KI_ADJUST_GEAR;
    VIFInitialIn_inst->VifCrKpKiAdjustThr1=VIF_CR_KP_KI_ADJUST_THR1;
    VIFInitialIn_inst->VifCrKpKiAdjustThr2=VIF_CR_KP_KI_ADJUST_THR2;
    VIFInitialIn_inst->VifCrKpKiAdjustThr3=VIF_CR_KP_KI_ADJUST_THR3;
    VIFInitialIn_inst->VifDynamicTopAdjust=VIF_DYNAMIC_TOP_ADJUST;
    VIFInitialIn_inst->VifDynamicTopMin=VIF_DYNAMIC_TOP_MIN;
    VIFInitialIn_inst->VifAmHumDetection=VIF_AM_HUM_DETECTION;
    VIFInitialIn_inst->VifClampgainClampSel=VIF_CLAMPGAIN_CLAMP_SEL;
    VIFInitialIn_inst->VifClampgainSyncbottRef=VIF_CLAMPGAIN_SYNCBOTT_REF;
    VIFInitialIn_inst->VifClampgainSyncheightRef=VIF_CLAMPGAIN_SYNCHEIGHT_REF;
    VIFInitialIn_inst->VifClampgainKc=VIF_CLAMPGAIN_KC;
    VIFInitialIn_inst->VifClampgainKg=VIF_CLAMPGAIN_KG;
    VIFInitialIn_inst->VifClampgainClampOren=VIF_CLAMPGAIN_CLAMP_OREN;
    VIFInitialIn_inst->VifClampgainGainOren=VIF_CLAMPGAIN_GAIN_OREN;
    VIFInitialIn_inst->VifClampgainClampOvNegative=VIF_CLAMPGAIN_CLAMP_OV_NEGATIVE;
    VIFInitialIn_inst->VifClampgainClampOvPositive=VIF_CLAMPGAIN_CLAMP_OV_POSITIVE;
    VIFInitialIn_inst->VifClampgainClampMin=VIF_CLAMPGAIN_CLAMP_MIN;
    VIFInitialIn_inst->VifClampgainClampMax=VIF_CLAMPGAIN_CLAMP_MAX;
    VIFInitialIn_inst->VifClampgainGainMin=VIF_CLAMPGAIN_GAIN_MIN;
    VIFInitialIn_inst->VifClampgainGainMax=VIF_CLAMPGAIN_GAIN_MAX;
    VIFInitialIn_inst->VifClampgainPorchCnt=VIF_CLAMPGAIN_PORCH_CNT;

    VIFInitialIn_inst->VifPeakingFilterB_VHF_L=VIF_PEAKING_FILTER_B_VHF_L;
    VIFInitialIn_inst->VifYcDelayFilterB_VHF_L=VIF_YC_DELAY_FILTER_B_VHF_L;
    VIFInitialIn_inst->VifGroupDelayFilterB_VHF_L=VIF_GROUP_DELAY_FILTER_B_VHF_L;
    VIFInitialIn_inst->VifPeakingFilterGH_VHF_L=VIF_PEAKING_FILTER_GH_VHF_L;
    VIFInitialIn_inst->VifYcDelayFilterGH_VHF_L=VIF_YC_DELAY_FILTER_GH_VHF_L;
    VIFInitialIn_inst->VifGroupDelayFilterGH_VHF_L=VIF_GROUP_DELAY_FILTER_GH_VHF_L;
    VIFInitialIn_inst->VifPeakingFilterDK_VHF_L=VIF_PEAKING_FILTER_DK_VHF_L;
    VIFInitialIn_inst->VifYcDelayFilterDK_VHF_L=VIF_YC_DELAY_FILTER_DK_VHF_L;
    VIFInitialIn_inst->VifGroupDelayFilterDK_VHF_L=VIF_GROUP_DELAY_FILTER_DK_VHF_L;
    VIFInitialIn_inst->VifPeakingFilterI_VHF_L=VIF_PEAKING_FILTER_I_VHF_L;
    VIFInitialIn_inst->VifYcDelayFilterI_VHF_L=VIF_YC_DELAY_FILTER_I_VHF_L;
    VIFInitialIn_inst->VifGroupDelayFilterI_VHF_L=VIF_GROUP_DELAY_FILTER_I_VHF_L;
    VIFInitialIn_inst->VifPeakingFilterL_VHF_L=VIF_PEAKING_FILTER_L_VHF_L;
    VIFInitialIn_inst->VifYcDelayFilterL_VHF_L=VIF_YC_DELAY_FILTER_L_VHF_L;
    VIFInitialIn_inst->VifGroupDelayFilterL_VHF_L=VIF_GROUP_DELAY_FILTER_L_VHF_L;
    VIFInitialIn_inst->VifPeakingFilterLL_VHF_L=VIF_PEAKING_FILTER_LL_VHF_L;
    VIFInitialIn_inst->VifYcDelayFilterLL_VHF_L=VIF_YC_DELAY_FILTER_LL_VHF_L;
    VIFInitialIn_inst->VifGroupDelayFilterLL_VHF_L=VIF_GROUP_DELAY_FILTER_LL_VHF_L;
    VIFInitialIn_inst->VifPeakingFilterMN_VHF_L=VIF_PEAKING_FILTER_MN_VHF_L;
    VIFInitialIn_inst->VifYcDelayFilterMN_VHF_L=VIF_YC_DELAY_FILTER_MN_VHF_L;
    VIFInitialIn_inst->VifGroupDelayFilterMN_VHF_L=VIF_GROUP_DELAY_FILTER_MN_VHF_L;

    VIFInitialIn_inst->VifPeakingFilterB_VHF_H=VIF_PEAKING_FILTER_B_VHF_H;
    VIFInitialIn_inst->VifYcDelayFilterB_VHF_H=VIF_YC_DELAY_FILTER_B_VHF_H;
    VIFInitialIn_inst->VifGroupDelayFilterB_VHF_H=VIF_GROUP_DELAY_FILTER_B_VHF_H;
    VIFInitialIn_inst->VifPeakingFilterGH_VHF_H=VIF_PEAKING_FILTER_GH_VHF_H;
    VIFInitialIn_inst->VifYcDelayFilterGH_VHF_H=VIF_YC_DELAY_FILTER_GH_VHF_H;
    VIFInitialIn_inst->VifGroupDelayFilterGH_VHF_H=VIF_GROUP_DELAY_FILTER_GH_VHF_H;
    VIFInitialIn_inst->VifPeakingFilterDK_VHF_H=VIF_PEAKING_FILTER_DK_VHF_H;
    VIFInitialIn_inst->VifYcDelayFilterDK_VHF_H=VIF_YC_DELAY_FILTER_DK_VHF_H;
    VIFInitialIn_inst->VifGroupDelayFilterDK_VHF_H=VIF_GROUP_DELAY_FILTER_DK_VHF_H;
    VIFInitialIn_inst->VifPeakingFilterI_VHF_H=VIF_PEAKING_FILTER_I_VHF_H;
    VIFInitialIn_inst->VifYcDelayFilterI_VHF_H=VIF_YC_DELAY_FILTER_I_VHF_H;
    VIFInitialIn_inst->VifGroupDelayFilterI_VHF_H=VIF_GROUP_DELAY_FILTER_I_VHF_H;
    VIFInitialIn_inst->VifPeakingFilterL_VHF_H=VIF_PEAKING_FILTER_L_VHF_H;
    VIFInitialIn_inst->VifYcDelayFilterL_VHF_H=VIF_YC_DELAY_FILTER_L_VHF_H;
    VIFInitialIn_inst->VifGroupDelayFilterL_VHF_H=VIF_GROUP_DELAY_FILTER_L_VHF_H;
    VIFInitialIn_inst->VifPeakingFilterLL_VHF_H=VIF_PEAKING_FILTER_LL_VHF_H;
    VIFInitialIn_inst->VifYcDelayFilterLL_VHF_H=VIF_YC_DELAY_FILTER_LL_VHF_H;
    VIFInitialIn_inst->VifGroupDelayFilterLL_VHF_H=VIF_GROUP_DELAY_FILTER_LL_VHF_H;
    VIFInitialIn_inst->VifPeakingFilterMN_VHF_H=VIF_PEAKING_FILTER_MN_VHF_H;
    VIFInitialIn_inst->VifYcDelayFilterMN_VHF_H=VIF_YC_DELAY_FILTER_MN_VHF_H;
    VIFInitialIn_inst->VifGroupDelayFilterMN_VHF_H=VIF_GROUP_DELAY_FILTER_MN_VHF_H;

    VIFInitialIn_inst->VifPeakingFilterB_UHF=VIF_PEAKING_FILTER_B_UHF;
    VIFInitialIn_inst->VifYcDelayFilterB_UHF=VIF_YC_DELAY_FILTER_B_UHF;
    VIFInitialIn_inst->VifGroupDelayFilterB_UHF=VIF_GROUP_DELAY_FILTER_B_UHF;
    VIFInitialIn_inst->VifPeakingFilterGH_UHF=VIF_PEAKING_FILTER_GH_UHF;
    VIFInitialIn_inst->VifYcDelayFilterGH_UHF=VIF_YC_DELAY_FILTER_GH_UHF;
    VIFInitialIn_inst->VifGroupDelayFilterGH_UHF=VIF_GROUP_DELAY_FILTER_GH_UHF;
    VIFInitialIn_inst->VifPeakingFilterDK_UHF=VIF_PEAKING_FILTER_DK_UHF;
    VIFInitialIn_inst->VifYcDelayFilterDK_UHF=VIF_YC_DELAY_FILTER_DK_UHF;
    VIFInitialIn_inst->VifGroupDelayFilterDK_UHF=VIF_GROUP_DELAY_FILTER_DK_UHF;
    VIFInitialIn_inst->VifPeakingFilterI_UHF=VIF_PEAKING_FILTER_I_UHF;
    VIFInitialIn_inst->VifYcDelayFilterI_UHF=VIF_YC_DELAY_FILTER_I_UHF;
    VIFInitialIn_inst->VifGroupDelayFilterI_UHF=VIF_GROUP_DELAY_FILTER_I_UHF;
    VIFInitialIn_inst->VifPeakingFilterL_UHF=VIF_PEAKING_FILTER_L_UHF;
    VIFInitialIn_inst->VifYcDelayFilterL_UHF=VIF_YC_DELAY_FILTER_L_UHF;
    VIFInitialIn_inst->VifGroupDelayFilterL_UHF=VIF_GROUP_DELAY_FILTER_L_UHF;
    VIFInitialIn_inst->VifPeakingFilterLL_UHF=VIF_PEAKING_FILTER_LL_UHF;
    VIFInitialIn_inst->VifYcDelayFilterLL_UHF=VIF_YC_DELAY_FILTER_LL_UHF;
    VIFInitialIn_inst->VifGroupDelayFilterLL_UHF=VIF_GROUP_DELAY_FILTER_LL_UHF;
    VIFInitialIn_inst->VifPeakingFilterMN_UHF=VIF_PEAKING_FILTER_MN_UHF;
    VIFInitialIn_inst->VifYcDelayFilterMN_UHF=VIF_YC_DELAY_FILTER_MN_UHF;
    VIFInitialIn_inst->VifGroupDelayFilterMN_UHF=VIF_GROUP_DELAY_FILTER_MN_UHF;

    VIFInitialIn_inst->ChinaDescramblerBox = CHINA_DESCRAMBLER_BOX;
    VIFInitialIn_inst->VifDelayReduce= VIF_DELAY_REDUCE;
    VIFInitialIn_inst->VifOverModulation = VIF_OVER_MODULATION;

    VIFInitialIn_inst->VifOverModulationDetect = VIF_OM_DETECTOR;
    VIFInitialIn_inst->VifACIDetect = VIF_ACI_DETECTOR;
    VIFInitialIn_inst->VifACIAGCREF=VIF_ACI_AGC_REF;
    VIFInitialIn_inst->VifADCOverflowAGCREF=VIF_ADC_OVERFLOW_AGC_REF;
    VIFInitialIn_inst->VifChanelScanAGCREF=VIF_CHANEL_SCAN_AGC_REF;
    VIFInitialIn_inst->VifACIDetTHR1 = VIF_ACIDET_THR1;
    VIFInitialIn_inst->VifACIDetTHR2 = VIF_ACIDET_THR2;
    VIFInitialIn_inst->VifACIDetTHR3 = VIF_ACIDET_THR3;
    VIFInitialIn_inst->VifACIDetTHR4 = VIF_ACIDET_THR4;

    VIFInitialIn_inst->VifSos21FilterC0= VIF_SOS21_FILTER_C0;
    VIFInitialIn_inst->VifSos21FilterC1= VIF_SOS21_FILTER_C1;
    VIFInitialIn_inst->VifSos21FilterC2= VIF_SOS21_FILTER_C2;
    VIFInitialIn_inst->VifSos21FilterC3= VIF_SOS21_FILTER_C3;
    VIFInitialIn_inst->VifSos21FilterC4= VIF_SOS21_FILTER_C4;
    VIFInitialIn_inst->VifSos22FilterC0= VIF_SOS22_FILTER_C0;
    VIFInitialIn_inst->VifSos22FilterC1= VIF_SOS22_FILTER_C1;
    VIFInitialIn_inst->VifSos22FilterC2= VIF_SOS22_FILTER_C2;
    VIFInitialIn_inst->VifSos22FilterC3= VIF_SOS22_FILTER_C3;
    VIFInitialIn_inst->VifSos22FilterC4= VIF_SOS22_FILTER_C4;
    VIFInitialIn_inst->VifSos31FilterC0= VIF_SOS31_FILTER_C0;
    VIFInitialIn_inst->VifSos31FilterC1= VIF_SOS31_FILTER_C1;
    VIFInitialIn_inst->VifSos31FilterC2= VIF_SOS31_FILTER_C2;
    VIFInitialIn_inst->VifSos31FilterC3= VIF_SOS31_FILTER_C3;
    VIFInitialIn_inst->VifSos31FilterC4= VIF_SOS31_FILTER_C4;
    VIFInitialIn_inst->VifSos32FilterC0= VIF_SOS32_FILTER_C0;
    VIFInitialIn_inst->VifSos32FilterC1= VIF_SOS32_FILTER_C1;
    VIFInitialIn_inst->VifSos32FilterC2= VIF_SOS32_FILTER_C2;
    VIFInitialIn_inst->VifSos32FilterC3= VIF_SOS32_FILTER_C3;
    VIFInitialIn_inst->VifSos32FilterC4= VIF_SOS32_FILTER_C4;


    pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_VIF_TUNER_TYPE, 0, 0, &VIFInitialIn_inst->VifTunerType);
    pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_VIF_PARA, 0, 0, VIFInitialIn_inst);

    VIFInitialIn_inst->VifReserve = 0;


    MS_Factory_NS_VIF_SET NSVIFValue;
    MSrv_Control::GetMSrvSystemDatabase()->GetFactoryExtSetting((&NSVIFValue), EN_FACTORY_EXT_NSVIF);
    if(NSVIFValue.VifAsiaSignalOption == 1)
    {
        VIFInitialIn_inst->VifCrKf1=VIF_CR_KF1_ASIA;
        VIFInitialIn_inst->VifCrKp1=VIF_CR_KP1_ASIA;
        VIFInitialIn_inst->VifCrKi1=VIF_CR_KI1_ASIA;
        VIFInitialIn_inst->VifCrKp2=VIF_CR_KP2_ASIA;
        VIFInitialIn_inst->VifCrKi2=VIF_CR_KI2_ASIA;
        VIFInitialIn_inst->VifCrLpfSel=VIF_CR_LPF_SEL_ASIA;
        VIFInitialIn_inst->VifCrPdModeSel=VIF_CR_PD_MODE_SEL_ASIA;
        VIFInitialIn_inst->VifCrKpKiAdjustGear=VIF_CR_KP_KI_ADJUST_GEAR_ASIA;
        VIFInitialIn_inst->VifCrKpKiAdjustThr1=VIF_CR_KP_KI_ADJUST_THR1_ASIA;
        VIFInitialIn_inst->VifCrKpKiAdjustThr2=VIF_CR_KP_KI_ADJUST_THR2_ASIA;
        VIFInitialIn_inst->VifCrKpKiAdjustThr3=VIF_CR_KP_KI_ADJUST_THR3_ASIA;
        VIFInitialIn_inst->VifCrKp = VIF_CR_KP_ASIA;
        VIFInitialIn_inst->VifCrKi = VIF_CR_KI_ASIA;
        VIFInitialIn_inst->VifCrKpKiAdjust=VIF_CR_KP_KI_ADJUST_ASIA;
    }

#if 0
       printf("\r\n define VifTop:%x",(U16)VIFInitialIn_inst.VifTop);
       printf("\r\n define VIF_VGA_MAXIMUM:%x",(U16)VIFInitialIn_inst.VifVgaMaximum);
       printf("\r\n define GAIN_DISTRIBUTION_THR:%x",(U16)VIFInitialIn_inst.GainDistributionThr);
       printf("\r\n define VIF_AGC_VGA_BASE:%x",(U16)VIFInitialIn_inst.VifAgcVgaBase);
       printf("\r\n define CHINA_DESCRAMBLER_BOX:%x",(U16)VIFInitialIn_inst.ChinaDescramblerBox);
       printf("\r\n define VIF_CR_KP1:%x",(U16)VIFInitialIn_inst.VifCrKp1);
       printf("\r\n define VIF_CR_KI1:%x",(U16)VIFInitialIn_inst.VifCrKi1);
       printf("\r\n define VIF_CR_KP2:%x",(U16)VIFInitialIn_inst.VifCrKp2);
       printf("\r\n define VIF_CR_KI2:%x",(U16)VIFInitialIn_inst.VifCrKi2);
       printf("\r\n define VIF_CR_KP:%x",(U16)VIFInitialIn_inst.VifCrKp);
       printf("\r\n define VIF_CR_KI:%x",(U16)VIFInitialIn_inst.VifCrKi);
       printf("\r\n define VIF_CR_LOCK_THR:%x",(U16)VIFInitialIn_inst.VifCrLockThr);
       printf("\r\n define VIF_CR_THR:%x",(U16)VIFInitialIn_inst.VifCrKpKiAdjust);
       printf("\r\n define VIF_CR_KP_KI_ADJUST:%x",(U16)VIFInitialIn_inst.VifCrKpKiAdjust);
       printf("\r\n define VIF_DELAY_REDUCE:%x",(U16)VIFInitialIn_inst.VifDelayReduce);
       printf("\r\n define VIF_OVER_MODULATION:%x",(U16)VIFInitialIn_inst.VifOverModulation);
       printf("\r\n define VIF_CLAMPGAIN_CLAMP_OV_NEGATIVE:%x",(U16)VIFInitialIn_inst.VifClampgainClampOvNegative);
       printf("\r\n define VIF_ACI_AGC_REF:%x",(U16)VIFInitialIn_inst.VifACIAGCREF);
#endif


    return MAPI_TRUE;
}
MAPI_BOOL device_demodulator::ATV_VIF_Init(void)
{
    //Init VIF Parameter

    mapi_vif_datatype::stVIFInitialIn VIFInitialIn_inst;

    m_eIF_Freq=mapi_demodulator_datatype::IF_FREQ_INVALID;

    // Set VIF data to 0, before init VIF variable
    memset(&VIFInitialIn_inst, 0x00, sizeof(VIFInitialIn_inst));

    ATV_GetVIF_InitailValue(&VIFInitialIn_inst);

    //update data from factory setting
    MS_Factory_NS_VIF_SET NSVIFValue;
    MSrv_Control::GetMSrvSystemDatabase()->GetFactoryExtSetting((&NSVIFValue), EN_FACTORY_EXT_NSVIF);
    VIFInitialIn_inst.VifTop                    =NSVIFValue.VifTop;
    VIFInitialIn_inst.VifVgaMaximum             =NSVIFValue.VifVgaMaximum;
    VIFInitialIn_inst.GainDistributionThr       =NSVIFValue.GainDistributionThr;
    VIFInitialIn_inst.VifAgcVgaBase             =NSVIFValue.VifAgcVgaBase;
    VIFInitialIn_inst.ChinaDescramblerBox       =NSVIFValue.ChinaDescramblerBox;
    //VIFInitialIn_inst.VifCrKp1                  =NSVIFValue.VifCrKp1;
    //VIFInitialIn_inst.VifCrKi1                  =NSVIFValue.VifCrKi1;
    //VIFInitialIn_inst.VifCrKp2                  =NSVIFValue.VifCrKp2;
    //VIFInitialIn_inst.VifCrKi2                  =NSVIFValue.VifCrKi2;
    VIFInitialIn_inst.VifCrKp                   =NSVIFValue.VifCrKp;
    VIFInitialIn_inst.VifCrKi                   =NSVIFValue.VifCrKi;
    VIFInitialIn_inst.VifCrLockThr              =NSVIFValue.VifCrLockThr ;
    VIFInitialIn_inst.VifCrThr                  =NSVIFValue.VifCrThr;
    VIFInitialIn_inst.VifCrKpKiAdjust           =NSVIFValue.VifCrKpKiAdjust;
    VIFInitialIn_inst.VifDelayReduce            =NSVIFValue.VifDelayReduce;
    VIFInitialIn_inst.VifOverModulation         =NSVIFValue.VifOverModulation;
    VIFInitialIn_inst.VifClampgainClampOvNegative=NSVIFValue.VifClampgainClampOvNegative;
    VIFInitialIn_inst.VifClampgainGainOvNegative=NSVIFValue.VifClampgainGainOvNegative;
    VIFInitialIn_inst.VifACIAGCREF = NSVIFValue.VifACIAGCREF;
    VIFInitialIn_inst.VifAgcRefNegative         =NSVIFValue.VifAgcRefNegative;

    mapi_interface::Get_mapi_vif()->Init(&VIFInitialIn_inst,sizeof(VIFInitialIn_inst));

    return MAPI_TRUE;
}

MAPI_BOOL device_demodulator::ATV_VIF_Handler(MAPI_BOOL bSet)
{
//    static MAPI_U32 u32Temp=0;
//    printf(" VIF Handler ---> Counter =%d \n",(int)u32Temp++);
     mapi_interface::Get_mapi_vif()->Handler(bSet);
    return MAPI_TRUE;
}

//##########################################################################################################
//##########################################################################################################
//########################################  Public:DTV Implementation ##################################
//##########################################################################################################
//##########################################################################################################

MAPI_BOOL device_demodulator::DTV_SetFrequency(MAPI_U32 u32Frequency, RF_CHANNEL_BANDWIDTH eBandWidth, MAPI_BOOL bPalBG, MAPI_BOOL bLPsel)
{

    switch (device_demodulator::m_enCurrentDemodulator_Type)
    {
#if (ISDB_SYSTEM_ENABLE == 1)
	case mapi_demodulator_datatype::E_DEVICE_DEMOD_ISDB:
        {
            MDrv_DMD_ISDBT_SetConfig(ENABLE);
            break;
        }
#endif
#if (DTMB_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DTMB:
        {
            DMD_RF_CHANNEL_BANDWIDTH eDMD_ChannelBandwidth=E_DMD_RF_CH_BAND_8MHz;
            MS_U32 u32DMD_IfFreq=36167;
            MS_U8 u8DMD_IQSwap=0;
            mapi_tuner *pTuner=NULL;
            UNUSED(u32Frequency);
            //printf("GGG ---->DTV_SetFrequency() %d\n",MsOS_GetSystemTime());
            switch(eBandWidth)
            {
                case E_RF_CH_BAND_6MHz:
                    eDMD_ChannelBandwidth=E_DMD_RF_CH_BAND_6MHz;
                    break;
                case E_RF_CH_BAND_7MHz:
                    eDMD_ChannelBandwidth=E_DMD_RF_CH_BAND_7MHz;
                    break;
                case E_RF_CH_BAND_8MHz:
                    eDMD_ChannelBandwidth=E_DMD_RF_CH_BAND_8MHz;
                    break;
                default:
                    printf("%s Line:%d Error!!\n",__func__, __LINE__);
                    break;
            }

            pTuner = mapi_interface::Get_mapi_pcb()->GetDvbtTuner(0);
            if (pTuner!=NULL)
            {
                if ( MAPI_FALSE == (pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_DTV_IF_FREQ, E_TUNER_DTV_DVB_T_MODE, eDMD_ChannelBandwidth, &u32DMD_IfFreq)))
                {
                    u32DMD_IfFreq=36167;
                }

                if ( MAPI_FALSE == (pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_IQ_SWAP, 0, 0, &u8DMD_IQSwap)))
                {
                    u8DMD_IQSwap=0;
                }
            }
            else
            {
                printf("%s Line:%d Error!!\n",__func__, __LINE__);
            }
            //MDrv_DMD_DVBT_SetConfigHPLPSetIF(eDMD_ChannelBandwidth, m_bSerialOut, bPalBG,bLPsel,u32DMD_IfFreq,45474,u8DMD_IQSwap);
            Active(ENABLE); // _UTOPIA
            return MAPI_TRUE;

            break;
        }
#endif
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
        {
            DMD_RF_CHANNEL_BANDWIDTH eDMD_ChannelBandwidth=E_DMD_RF_CH_BAND_8MHz;
            MS_U32 u32DMD_IfFreq=36167;
            MS_U8 u8DMD_IQSwap=0;
            mapi_tuner *pTuner=NULL;
            UNUSED(u32Frequency);
            //printf("GGG ---->DTV_SetFrequency() %d\n",MsOS_GetSystemTime());
            switch(eBandWidth)
            {
                case E_RF_CH_BAND_6MHz:
                    eDMD_ChannelBandwidth=E_DMD_RF_CH_BAND_6MHz;
                    break;
                case E_RF_CH_BAND_7MHz:
                    eDMD_ChannelBandwidth=E_DMD_RF_CH_BAND_7MHz;
                    break;
                case E_RF_CH_BAND_8MHz:
                    eDMD_ChannelBandwidth=E_DMD_RF_CH_BAND_8MHz;
                    break;
                default:
                    printf("%s Line:%d Error!!\n",__func__, __LINE__);
                    break;
            }

            pTuner = mapi_interface::Get_mapi_pcb()->GetDvbtTuner(0);
            if (pTuner!=NULL)
            {
                if ( MAPI_FALSE == (pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_DTV_IF_FREQ, E_TUNER_DTV_DVB_T_MODE, eDMD_ChannelBandwidth, &u32DMD_IfFreq)))
                {
                    u32DMD_IfFreq=36167;
                }

                if ( MAPI_FALSE == (pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_IQ_SWAP, 0, E_TUNER_DTV_DVB_T_MODE, &u8DMD_IQSwap)))
                {
                    u8DMD_IQSwap=0;
                }
            }
            else
            {
                printf("%s Line:%d Error!!\n",__func__, __LINE__);
            }

            MDrv_DMD_DVBT_SetConfigHPLPSetIF(eDMD_ChannelBandwidth, m_bSerialOut, bPalBG,bLPsel,u32DMD_IfFreq,45474,u8DMD_IQSwap);
            MDrv_DMD_DVBT_SetActive(ENABLE); // _UTOPIA
            //    Active(ENABLE); // _UTOPIA

            return MAPI_TRUE;
            break;
        }
        default:
            break;
     }


    return MAPI_TRUE;

}

mapi_demodulator_datatype::EN_FRONTEND_SIGNAL_CONDITION device_demodulator::DTV_GetSNR(void)
{
    switch (device_demodulator::m_enCurrentDemodulator_Type)
    {
#if (DTMB_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DTMB:
        {
            return mapi_demodulator_datatype::E_FE_SIGNAL_NO;
            break;
        }
#endif
#if (ATSC_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_ATSC:
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_ATSC_VSB:
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_ATSC_QPSK:
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_ATSC_16QAM:
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_ATSC_64QAM:
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_ATSC_256QAM:
            switch (MDrv_DMD_ATSC_GetSignalQuality())
            {
                case DMD_ATSC_SIGNAL_NO:
                    return mapi_demodulator_datatype::E_FE_SIGNAL_NO;
                    break;
                case DMD_ATSC_SIGNAL_WEAK:
                    return mapi_demodulator_datatype::E_FE_SIGNAL_WEAK;
                    break;
                case DMD_ATSC_SIGNAL_MODERATE:
                    return mapi_demodulator_datatype::E_FE_SIGNAL_MODERATE;
                    break;
                case DMD_ATSC_SIGNAL_STRONG:
                    return mapi_demodulator_datatype::E_FE_SIGNAL_STRONG;
                    break;
                case DMD_ATSC_SIGNAL_VERY_STRONG:
                    return mapi_demodulator_datatype::E_FE_SIGNAL_VERY_STRONG;
                    break;
                default:
                    return mapi_demodulator_datatype::E_FE_SIGNAL_NO;
                    break;
            }
        break;
#endif
         default:
            break;
    }
    return mapi_demodulator_datatype::E_FE_SIGNAL_NO;

}

MAPI_U32 device_demodulator::DTV_GetBER(void)
{

    return 0;

}

MAPI_BOOL device_demodulator::DTV_GetPreBER(float *p_preBer)
{
    float ber;
    switch (device_demodulator::m_enCurrentDemodulator_Type)
    {
#if (ISDB_SYSTEM_ENABLE == 1)
	//dan add for integrate ISDBT utopia driver[begin]
	case mapi_demodulator_datatype::E_DEVICE_DEMOD_ISDB:
	    EN_ISDBT_Layer 	LayerIndex;
	    LayerIndex = E_ISDBT_Layer_A;
	    MDrv_DMD_ISDBT_GetPreViterbiBer(LayerIndex, p_preBer);
	    break;
	//dan add for integrate ISDBT utopia driver[end]
#endif
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
        default:
            // _UTOPIA INTERN_DVBT_GetPreViterbiBer(&ber);
            MDrv_DMD_DVBT_GetPreViterbiBer(&ber);  // _UTOPIA
            *p_preBer = ber;
            break;
    }
    return MAPI_TRUE;
}

MAPI_BOOL device_demodulator::DTV_GetPostBER(float *p_postBer)
{
    float ber;
    switch (device_demodulator::m_enCurrentDemodulator_Type)
    {
#if (ISDB_SYSTEM_ENABLE == 1)
	case mapi_demodulator_datatype::E_DEVICE_DEMOD_ISDB:
    	    EN_ISDBT_Layer 	LayerIndex;
	    LayerIndex = E_ISDBT_Layer_A;
	    MDrv_DMD_ISDBT_GetPostViterbiBer(LayerIndex, p_postBer);
	    break;
#endif
#if (DVBC_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_C:
            // _UTOPIA INTERN_DVBC_GetPostViterbiBer(&ber);
            MDrv_DMD_DVBC_GetPostViterbiBer(&ber); // _UTOPIA
            *p_postBer = ber;
            break;
#endif
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
        default:
            // _UTOPIA INTERN_DVBT_GetPostViterbiBer(&ber);
            MDrv_DMD_DVBT_GetPostViterbiBer(&ber); // _UTOPIA
            *p_postBer = ber;
            break;
    }
    return MAPI_TRUE;
}

MAPI_BOOL device_demodulator::DTV_GetPacketErr(void)
{
    MAPI_U16 pktErr;
    switch (device_demodulator::m_enCurrentDemodulator_Type)
    {
#if (ISDB_SYSTEM_ENABLE == 1)
	case mapi_demodulator_datatype::E_DEVICE_DEMOD_ISDB:
            MAPI_U16 pktErrA, pktErrB, pktErrC;
	    MDrv_DMD_ISDBT_Read_PKT_ERR(E_ISDBT_Layer_A, &pktErrA); // _UTOPIA
	    MDrv_DMD_ISDBT_Read_PKT_ERR(E_ISDBT_Layer_B, &pktErrB); // _UTOPIA
	    MDrv_DMD_ISDBT_Read_PKT_ERR(E_ISDBT_Layer_C, &pktErrC); // _UTOPIA
	    break;
#endif
#if (DVBC_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_C:
            // _UTOPIA MDrv_DMD_DVBC_GetPacketErr(&pktErr);
            MDrv_DMD_DVBC_GetPacketErr(&pktErr); // _UTOPIA
            break;
#endif
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
        default:
            // _UTOPIA MDrv_DMD_DVBT_GetPacketErr(&pktErr);
            MDrv_DMD_DVBT_GetPacketErr(&pktErr); // _UTOPIA
            break;
    }
    return MAPI_TRUE;
}

MAPI_BOOL device_demodulator::DTV_GetPacketErr(MAPI_U16* pkt)
{
    MAPI_U16 pktErr = 0xffff;
    switch (device_demodulator::m_enCurrentDemodulator_Type)
    {
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
        default:
            MDrv_DMD_DVBT_GetPacketErr(&pktErr); // _UTOPIA
            break;
#if (DVBC_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_C:
            MDrv_DMD_DVBC_GetPacketErr(&pktErr); // _UTOPIA
            break;
#endif
    }
    *pkt = pktErr;
    return TRUE;
}

#if (DVBT_SYSTEM_ENABLE == 1)
/****************************************************************************
  Subject:    To get the DVBT  parameter
  Function:   INTERN_DVBT_Get_TPS_Parameter
  Parmeter:   point to return parameter
              Constellation (b2 ~ b0)   : 0~2 => QPSK, 16QAM, 64QAM
              Hierarchy (b5 ~ b3))      : 0~3 => None, Aplha1, Aplha2, Aplha4
              LP Code Rate (b8 ~ b6)    : 0~4 => 1/2, 2/3, 3/4, 5/6, 7/8
              HP Code Rate (b11 ~ b9)   : 0~4 => 1/2, 2/3, 3/4, 5/6, 7/8
              GI (b13 ~ b12)            : 0~3 => 1/32, 1/16, 1/8, 1/4
              FFT ( b14)                : 0~1 => 2K, 8K
              Priority(bit 15)          : 0~1=> HP,LP

  Return:     TRUE
              FALSE
  Remark:     The TPS parameters will be available after TPS lock
*****************************************************************************/
MAPI_BOOL device_demodulator::DTV_DVB_T_Get_TPS_Parameter( MAPI_U16 * pu16TPS_parameter, E_SIGNAL_TYPE eSignalType)
{
    MAPI_U16    u16TPS_param = 0;
    MAPI_BOOL   bReturnVal = TRUE;

    if ( MDrv_DMD_DVBT_GetTPSInfo(&u16TPS_param) == TRUE )
    {

        if (eSignalType == TS_MODUL_MODE)
        {
            *pu16TPS_parameter = u16TPS_param & (BIT0|BIT1) ;
        }

        else  if (eSignalType == TS_CODE_RATE)
        {
            *pu16TPS_parameter = (u16TPS_param & (BIT11|BIT10|BIT9))>>9 ; //DenizG | 10.12.10: For correction */
        }

        else if (eSignalType == TS_GUARD_INTERVAL)
        {
            *pu16TPS_parameter = (u16TPS_param & (BIT13|BIT12))>>12;
        }

        else if (eSignalType == TS_FFT_VALUE)
        {
            *pu16TPS_parameter = (u16TPS_param & (BIT14))>>14 ;
        }
        else
            bReturnVal = FALSE;

    }
    else
    {
        bReturnVal = FALSE;
    }
    return bReturnVal;
}

mapi_demodulator_datatype::EN_DVBT_CONSTEL_TYPE device_demodulator::DTV_DVB_T_GetSignalModulation (void)
{
    MAPI_U16    u16Modulation = 0;

    if (DTV_DVB_T_Get_TPS_Parameter( &u16Modulation, TS_MODUL_MODE) == FALSE)
        DBG_DEMOD_MSB(printf("TPS parameter can not be read.\n"));

    return (mapi_demodulator_datatype::EN_DVBT_CONSTEL_TYPE)u16Modulation;
}


mapi_demodulator_datatype::EN_DVBT_GUARD_INTERVAL device_demodulator::DTV_DVB_T_GetSignalGuardInterval (void)
{
    MAPI_U16    u16GuardInterval = 0;

    if (DTV_DVB_T_Get_TPS_Parameter( &u16GuardInterval, TS_GUARD_INTERVAL) == FALSE)
        DBG_DEMOD_MSB(printf("TPS parameter can not be read.\n"));

    return (mapi_demodulator_datatype::EN_DVBT_GUARD_INTERVAL)u16GuardInterval;

}

mapi_demodulator_datatype::EN_DVBT_FFT_VAL device_demodulator::DTV_DVB_T_GetSignalFFTValue (void)
{
    MAPI_U16    u16FFTValue = 0;

    if (DTV_DVB_T_Get_TPS_Parameter( &u16FFTValue, TS_FFT_VALUE) == FALSE)
        DBG_DEMOD_MSB(printf("TPS parameter can not be read.\n"));
    return (mapi_demodulator_datatype::EN_DVBT_FFT_VAL)u16FFTValue;

}

mapi_demodulator_datatype::EN_DVBT_CODE_RATE device_demodulator::DTV_DVB_T_GetSignalCodeRate (void)
{
    MAPI_U16    u16CodeRate = 0;

    if (DTV_DVB_T_Get_TPS_Parameter( &u16CodeRate, TS_CODE_RATE) == FALSE)
        DBG_DEMOD_MSB(printf("TPS parameter can not be read.\n"));
    return (mapi_demodulator_datatype::EN_DVBT_CODE_RATE)u16CodeRate;
}
#endif

MAPI_U16 device_demodulator::DTV_GetSignalQuality(void)
{

    MAPI_U16 quality;
    mapi_tuner *pTuner=NULL;
    float fRFLevel=0.0f;
    pTuner = mapi_interface::Get_mapi_pcb()->GetDvbtTuner(0);
    if (pTuner!=NULL)
    {
        if ( MAPI_FALSE == pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_RF_LEVEL, 0, 0, &fRFLevel))
        {
            fRFLevel=200.0f;
        }
    }
    else
    {
        fRFLevel=200.0f;
        printf("%s Line:%d Error!!\n",__func__, __LINE__);
    }

    switch (device_demodulator::m_enCurrentDemodulator_Type)
    {
#if (DTMB_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DTMB:
        {
            quality=MDrv_DMD_DTMB_GetSignalQuality();
        }
        break;
#endif
#if (DVBT_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
            // _UTOPIA INTERN_DVBT_GetSignalQuality(&quality);
            MDrv_DMD_DVBT_GetSignalQualityWithRFPower(&quality, fRFLevel); // _UTOPIA
            break;
#endif
#if (DVBC_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_C:
            // _UTOPIA INTERN_DVBT_GetSignalQuality(&quality);
            MDrv_DMD_DVBC_GetSignalQualityWithRFPower(&quality, fRFLevel); // _UTOPIA
            break;
#endif
#if (ISDB_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_ISDB:
	    quality = MDrv_DMD_ISDBT_GetSignalQuality(); // _UTOPIA
	    break;
#endif
        default:
            printf("%s Line:%d Error!!\n",__func__, __LINE__);
            quality=0;
            break;
    }
    return quality;

}

MAPI_U16 device_demodulator::DTV_GetSignalStrength(void)
{
    MAPI_U16 strength;
    mapi_tuner *pTuner=NULL;
    float fRFLevel=0.0f;

    MAPI_U8 tmp = 0;
    MAPI_U16 if_agc_gain = 0;
    // select IF gain to read    
    MDrv_SYS_DMD_VD_MBX_ReadReg(0x2700 + 0x0b*2, &tmp);
    MDrv_SYS_DMD_VD_MBX_WriteReg(0x2700 + 0x0b*2, (tmp&0xF0)|0x03);

    MDrv_SYS_DMD_VD_MBX_ReadReg(0x2700 + 0x02*2, &tmp);
    MDrv_SYS_DMD_VD_MBX_WriteReg(0x2700 + 0x02*2, tmp|0x80);

    MDrv_SYS_DMD_VD_MBX_ReadReg(0x2700 + 0x0c*2+1, &tmp);
    if_agc_gain = tmp;
    MDrv_SYS_DMD_VD_MBX_ReadReg(0x2700 + 0x0c*2, &tmp);
    if_agc_gain = (if_agc_gain<<8)|tmp;  

    MDrv_SYS_DMD_VD_MBX_ReadReg(0x2700 + 0x02*2, &tmp);
    MDrv_SYS_DMD_VD_MBX_WriteReg(0x2700 + 0x02*2, tmp&0x7F);

    pTuner = mapi_interface::Get_mapi_pcb()->GetDvbtTuner(0);
    if (pTuner!=NULL)
    {
        if ( MAPI_FALSE == pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_RF_LEVEL, 0, (MAPI_U32)if_agc_gain, &fRFLevel))
        {
            fRFLevel=200.0f;
        }
    }
    else
    {
        fRFLevel=200.0f;
        printf("%s Line:%d Error!!\n",__func__, __LINE__);
    }

    switch(device_demodulator::m_enCurrentDemodulator_Type)
    {
#if (DTMB_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DTMB:
        {
           // MDrv_DMD_DTMB_GetSignalStrength(&strength);
           pTuner = mapi_interface::Get_mapi_pcb()->GetDtmbTuner(0);
           if (pTuner!=NULL)
           {
               if ( MAPI_FALSE == pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_RF_LEVEL, 0, 0, &fRFLevel))
               {
                   fRFLevel=200.0f;
               }

           }
           else
           {
               fRFLevel=200.0f;
           }

           if(fRFLevel > -35)
               strength = 100;
           else if (fRFLevel > -100.0)
               strength = (MAPI_U16)( fRFLevel + 100.0 + 0.5);
           else
               strength = 0;

        }
        break;
#endif
#if (DVBT_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
            // _UTOPIA INTERN_DVBT_GetSignalStrength(&strength);
            MDrv_DMD_DVBT_GetSignalStrengthWithRFPower(&strength, fRFLevel); // _UTOPIA
            break;
#endif
#if (DVBC_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_C:
            // _UTOPIA INTERN_DVBT_GetSignalQuality(&quality);
            MDrv_DMD_DVBC_GetSignalStrengthWithRFPower(&strength, fRFLevel); // _UTOPIA
            break;
#endif
#if (ISDB_SYSTEM_ENABLE == 1)
	case mapi_demodulator_datatype::E_DEVICE_DEMOD_ISDB:
	    MDrv_DMD_ISDBT_GetSignalStrength(&strength); // _UTOPIA
	    break;
#endif
        default:
            printf("%s Line:%d Error!!\n",__func__, __LINE__);
            strength=0;
            break;
    }

    return strength;
}

MAPI_U16 device_demodulator::DTV_GetCellID(void)
{

    MAPI_U16 u16CellID =0;

    // _UTOPIA INTERN_DVBT_Get_CELL_ID(&u16CellID);
    MDrv_DMD_DVBT_GetCellID(&u16CellID); // _UTOPIA

    return u16CellID;

}

MAPI_BOOL device_demodulator::DTV_Serial_Control(MAPI_BOOL bEnable)
{
    m_bSerialOut=bEnable;
    // _UTOPIAINTERN_DVBT_Serial_Control(bEnable);
    switch (device_demodulator::m_enCurrentDemodulator_Type)
    {
#if (DTMB_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DTMB:
        {
            MS_U8  u8TsConfigData;

            u8TsConfigData = 0;
            //printf("dan enter MSB1400_Serial_Control\n");
            if(bEnable == TRUE)  //serial mode
            {
                u8TsConfigData |= 0x01;
                u8TsConfigData |= DTMB_TS_CLK_DIVNUM_SERIAL_MODE << 3;  //0x04
            }
            else
            {
                u8TsConfigData &= ~0x01;
                u8TsConfigData |= DTMB_TS_CLK_DIVNUM_PARALLEL_MODE << 3; //0x31
            }

            u8TsConfigData |= DTMB_TS_DATA_SWAP << 1;
            u8TsConfigData |= DTMB_TS_CLK_INV << 2;

            return MDrv_DMD_DTMB_SetSerialControl(u8TsConfigData);
         }
         break;
#endif
#if (DVBC_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_C:
                printf("-1:Set DTV_Serial_Control...DVBC %d\r\n\n", bEnable);
                MDrv_DMD_DVBC_SetSerialControl(bEnable); // _UTOPIA
                break;
#endif
#if (ISDB_SYSTEM_ENABLE == 1)
	case mapi_demodulator_datatype::E_DEVICE_DEMOD_ISDB:
	{
	    printf("Set DTV_Serial_Control...ISDBT %d\r\n\n", bEnable);
	    MS_U8  u8TsConfigData;
	    u8TsConfigData = 0;
	    if(bEnable == TRUE)  //serial mode
	    {
	        u8TsConfigData |= 0x01;
		u8TsConfigData |= ISDBT_TS_CLK_DIVNUM_SERIAL_MODE << 3;  //0x04
	    }
	    else
	    {
	        u8TsConfigData &= ~0x01;
		u8TsConfigData |= ISDBT_TS_CLK_DIVNUM_PARALLEL_MODE << 3; //0x31
	    }
	    u8TsConfigData |= ISDBT_TS_DATA_SWAP << 1;
	    u8TsConfigData |= ISDBT_TS_CLK_INV << 2;
	    MDrv_DMD_ISDBT_SetSerialControl(u8TsConfigData);        // _UTOPIA
	}
	    break;
#endif
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
        default:
                MDrv_DMD_DVBT_SetSerialControl(bEnable); // _UTOPIA
                break;
    }
    return MAPI_TRUE;

}

MAPI_BOOL device_demodulator::DTV_IsHierarchyOn(void)
{
#ifdef ENABLE_HIERARCHY
    MAPI_U16 tpsInfo;
    if(MDrv_DMD_DVBT_GetTPSInfo(&tpsInfo))
    {
        //printf("tpsInfo %x %x\n",tpsInfo,tpsInfo&0x1C);
        if(tpsInfo&0x1C)
        {
            return TRUE;
        }
    }
#endif
    return FALSE;

}

#if (ISDB_SYSTEM_ENABLE == 1)
mapi_demodulator_datatype::EN_LOCK_STATUS device_demodulator::DTV_ISDB_GetLockStatus(void)
{
    DMD_ISDBT_LOCK_STATUS eLockStatus;
    eLockStatus = MDrv_DMD_ISDBT_GetLock(DMD_ISDBT_GETLOCK);
    switch (eLockStatus) // _UTOPIA
    {
        case DMD_ISDBT_LOCK:
            return mapi_demodulator_datatype::E_DEMOD_LOCK;
            break;
        case DMD_ISDBT_CHECKING:
            return mapi_demodulator_datatype::E_DEMOD_CHECKING;
            break;
        case DMD_ISDBT_CHECKEND:
            return mapi_demodulator_datatype::E_DEMOD_CHECKEND;
            break;
        case DMD_ISDBT_UNLOCK:
            return mapi_demodulator_datatype::E_DEMOD_UNLOCK;
            break;
        default:
            printf("ISDBTT_GetLockStatus error\n");
            break;
    }
    return mapi_demodulator_datatype::E_DEMOD_UNLOCK;
}
#endif

//##########################################################################################################
//##########################################################################################################
//########################################  Public:DTV-DVB-T Implementation ################################
//##########################################################################################################
//##########################################################################################################

#if (DVBT_SYSTEM_ENABLE == 1)
mapi_demodulator_datatype::EN_LOCK_STATUS device_demodulator::DTV_DVB_T_GetLockStatus(void)
{
    // _UTOPIA return INTERN_DVBT_Lock();
    DMD_LOCK_STATUS eLockStatus;
    MDrv_DMD_DVBT_GetLock(E_DMD_DMD_DVBT_GETLOCK, &eLockStatus);
    switch (eLockStatus) // _UTOPIA
    {
        case E_DMD_LOCK:
            return mapi_demodulator_datatype::E_DEMOD_LOCK;
            break;
        case E_DMD_CHECKING:
            return mapi_demodulator_datatype::E_DEMOD_CHECKING;
            break;
        case E_DMD_CHECKEND:
            return mapi_demodulator_datatype::E_DEMOD_CHECKEND;
            break;
        case E_DMD_UNLOCK:
            return mapi_demodulator_datatype::E_DEMOD_UNLOCK;
            break;
        default:
            printf("DTV_DVB_T_GetLockStatus error\n");
            break;
    }
    return mapi_demodulator_datatype::E_DEMOD_UNLOCK;
}
#endif


#if (DVBC_SYSTEM_ENABLE == 1)
//##########################################################################################################
//##########################################################################################################
//########################################  Public:DTV-DVB-C Implementation ################################
//##########################################################################################################
//##########################################################################################################
MAPI_BOOL device_demodulator::DTV_DVB_C_SetFrequency(MAPI_U32 u32Frequency, RF_CHANNEL_BANDWIDTH eBandWidth,MAPI_U32 u32SymRate, mapi_demodulator_datatype::EN_CAB_CONSTEL_TYPE eQAM)
{
    DMD_DVBC_RF_CHANNEL_BANDWIDTH eDMD_ChannelBandwidth=DMD_DVBC_RF_CH_BAND_8MHz;
    DMD_DVBC_MODULATION_TYPE eModulationType=DMD_DVBC_QAMAUTO;
    MS_U32 u32DMD_IfFreq=36125;
    MS_U8 u8DMD_IQSwap=0;
    mapi_tuner *pTuner=NULL;

    UNUSED(u32Frequency);
    //UNUSED(eBandWidth);

    switch(eBandWidth)
    {
        case E_RF_CH_BAND_6MHz:
            eDMD_ChannelBandwidth=DMD_DVBC_RF_CH_BAND_6MHz;
            break;
        case E_RF_CH_BAND_7MHz:
            eDMD_ChannelBandwidth=DMD_DVBC_RF_CH_BAND_7MHz;
            break;
        case E_RF_CH_BAND_8MHz:
            eDMD_ChannelBandwidth=DMD_DVBC_RF_CH_BAND_8MHz;
            break;
        default:
            #if (MSTAR_TVOS == 1)
            eDMD_ChannelBandwidth=DMD_DVBC_RF_CH_BAND_8MHz;
            #endif
            printf("%s Line:%d Error!!\n",__func__, __LINE__);
            break;
    }

    switch(eQAM)
    {
        case mapi_demodulator_datatype::E_CAB_QAM16:
            eModulationType = DMD_DVBC_QAM16;
            break;
        case mapi_demodulator_datatype::E_CAB_QAM32:
            eModulationType = DMD_DVBC_QAM32;
            break;
        case mapi_demodulator_datatype::E_CAB_QAM64:
            eModulationType = DMD_DVBC_QAM64;
            break;
        case mapi_demodulator_datatype::E_CAB_QAM128:
            eModulationType = DMD_DVBC_QAM128;
            break;
        case mapi_demodulator_datatype::E_CAB_QAM256:
            eModulationType = DMD_DVBC_QAM256;
            break;
        default:
            eModulationType = DMD_DVBC_QAMAUTO;
            break;
    }

    pTuner = mapi_interface::Get_mapi_pcb()->GetDvbtTuner(0);
    if (pTuner!=NULL)
    {
        if ( MAPI_FALSE == (pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_DTV_IF_FREQ, E_TUNER_DTV_DVB_C_MODE, eDMD_ChannelBandwidth, &u32DMD_IfFreq)))
        {
            u32DMD_IfFreq=36125;
        }

        if ( MAPI_FALSE == (pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_IQ_SWAP, 0, E_TUNER_DTV_DVB_C_MODE, &u8DMD_IQSwap)))
        {
            u8DMD_IQSwap=0;
        }
    }
    else
    {
        printf("%s Line:%d Error!!\n",__func__, __LINE__);
    }

    MDrv_DMD_DVBC_SetConfig(u32SymRate, eModulationType, u32DMD_IfFreq, u8DMD_IQSwap, m_bSerialOut);
/*
    MDrv_DMD_DVBC_SetConfig_symbol_rate_list(
        u32SymRate,
        eModulationType,
        u32DMD_IfFreq,
        u8DMD_IQSwap,
        m_bSerialOut,
        &(g_u16_symbol_rate_list[0]),
        sizeof(g_u16_symbol_rate_list)/sizeof(MS_U16)
        );
*/
    MDrv_DMD_DVBC_SetActive(TRUE);

#if 0
    // sample code to use MDrv_DMD_TSO_Clk_Control
    printf("[a1][dvbc]ts_clock_control\n");

    // ts output clock frequency
    u8_cmd_array[0] = 0x01; // cmd
    u8_cmd_array[1] = 0x12; // div
    MDrv_DMD_TSO_Clk_Control(u8_cmd_array);
    // ts output clock phase
    u8_cmd_array[0] = 0x02; // cmd
    u8_cmd_array[1] = 0x00; // 1:enable, 0:disable
    MDrv_DMD_TSO_Clk_Control(u8_cmd_array);
    // ts outout clock phase tuning.
    u8_cmd_array[0] = 0x03; // cmd
    u8_cmd_array[1] = 0x01; // 1:enable, 0:disable
    u8_cmd_array[2] = 0x07; // phase tuning number
    MDrv_DMD_TSO_Clk_Control(u8_cmd_array);
#endif

    return MAPI_TRUE;
}

mapi_demodulator_datatype::EN_LOCK_STATUS device_demodulator::DTV_DVB_C_GetLockStatus(void)
{
    DMD_DVBC_LOCK_STATUS eLockStatus;
    mapi_tuner *pTuner=NULL;
    float fRFLevel=0.0f;
    pTuner = mapi_interface::Get_mapi_pcb()->GetDvbtTuner(0);
    if (pTuner!=NULL)
    {
        pTuner->ExtendCommand(mapi_tuner_datatype::E_TUNER_SUBCMD_GET_RF_LEVEL, 0, 0, &fRFLevel);
    }
    else
    {
        fRFLevel=200.0f;
        printf("%s Line:%d Error!!\n",__func__, __LINE__);
    }
    MDrv_DMD_DVBC_GetLockWithRFPower(DMD_DVBC_GETLOCK, &eLockStatus, fRFLevel, -82.0f);

    if (eLockStatus == DMD_DVBC_LOCK)
        return mapi_demodulator_datatype::E_DEMOD_LOCK;
    else if (eLockStatus == DMD_DVBC_CHECKING)
        return mapi_demodulator_datatype::E_DEMOD_CHECKING;
    return mapi_demodulator_datatype::E_DEMOD_UNLOCK;

}

mapi_demodulator_datatype::EN_CAB_CONSTEL_TYPE device_demodulator::DTV_DVB_C_GetSignalModulation(void)
{
    DMD_DVBC_MODULATION_TYPE QAMMode;
    MS_U16 u16SymbolRate;
    float FreqOff;
    MDrv_DMD_DVBC_GetStatus(&QAMMode, &u16SymbolRate, &FreqOff);

    switch (QAMMode)
    {
        case DMD_DVBC_QAM16:
            return mapi_demodulator_datatype::E_CAB_QAM16;
        case DMD_DVBC_QAM32:
            return mapi_demodulator_datatype::E_CAB_QAM32;
        case DMD_DVBC_QAM64:
            return mapi_demodulator_datatype::E_CAB_QAM64;
        case DMD_DVBC_QAM128:
            return mapi_demodulator_datatype::E_CAB_QAM128;
        case DMD_DVBC_QAM256:
            return mapi_demodulator_datatype::E_CAB_QAM256;
        default:
            return mapi_demodulator_datatype::E_CAB_INVALID;
    }

    return mapi_demodulator_datatype::E_CAB_INVALID;
}

MAPI_U16 device_demodulator::DTV_DVB_C_GetCurrentSymbolRate(void)
{
    DMD_DVBC_MODULATION_TYPE QAMMode;
    MS_U16 u16SymbolRate;
    float FreqOff;
    MDrv_DMD_DVBC_GetStatus(&QAMMode, &u16SymbolRate, &FreqOff);
    return u16SymbolRate;

}
#endif

#if (ATSC_SYSTEM_ENABLE==1)
//##########################################################################################################
//##########################################################################################################
//########################################  Public:DTV-ATSC Implementation #################################
//##########################################################################################################
//##########################################################################################################
mapi_demodulator_datatype::EN_LOCK_STATUS device_demodulator::DTV_ATSC_GetLockStatus(void)
{
    switch (MDrv_DMD_ATSC_GetLock(DMD_ATSC_GETLOCK))
    {
        case DMD_ATSC_LOCK:
            return mapi_demodulator_datatype::E_DEMOD_LOCK;
        case DMD_ATSC_CHECKING:
            return mapi_demodulator_datatype::E_DEMOD_CHECKING;
        case DMD_ATSC_CHECKEND:
            return mapi_demodulator_datatype::E_DEMOD_CHECKEND;
        case DMD_ATSC_UNLOCK:
            return mapi_demodulator_datatype::E_DEMOD_UNLOCK;
        default:
            return mapi_demodulator_datatype::E_DEMOD_UNLOCK;
    }
   // return mapi_demodulator_datatype::E_ATSC_SCAN_NULL;

}
MAPI_BOOL device_demodulator::DTV_ATSC_ClkEnable(MAPI_BOOL bEnable)
{
   return MAPI_TRUE;
}

mapi_demodulator_datatype::EN_DEVICE_DEMOD_TYPE device_demodulator::DTV_ATSC_GetModulationMode(void)
{
    switch (MDrv_DMD_ATSC_GetModulationMode())
    {
        case DMD_ATSC_DEMOD_ATSC_VSB:
            return mapi_demodulator_datatype::E_DEVICE_DEMOD_ATSC_VSB;
        case DMD_ATSC_DEMOD_ATSC_64QAM:
            return mapi_demodulator_datatype::E_DEVICE_DEMOD_ATSC_64QAM;
        case DMD_ATSC_DEMOD_ATSC_256QAM:
            return mapi_demodulator_datatype::E_DEVICE_DEMOD_ATSC_256QAM;
        default:
            return mapi_demodulator_datatype::E_DEVICE_DEMOD_NULL;
    }
}

MAPI_BOOL device_demodulator::DTV_ATSC_ChangeModulationMode(mapi_demodulator_datatype::EN_DEVICE_DEMOD_TYPE eMode)
{

    device_demodulator::m_enCurrentDemodulator_Type = eMode;
    switch(eMode)
    {
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_ATSC_VSB:
        printf("\033[45;37m  ""%s[%d] ::  E_DEVICE_DEMOD_ATSC_VSB \033[0m\n",__FUNCTION__,__LINE__);
            MDrv_DMD_ATSC_SetConfig(DMD_ATSC_DEMOD_ATSC_VSB, MAPI_TRUE);
            break;
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_ATSC_64QAM:
        printf("\033[45;37m  ""%s[%d] :: E_DEVICE_DEMOD_ATSC_64QAM  \033[0m\n",__FUNCTION__,__LINE__);
            MDrv_DMD_ATSC_SetConfig(DMD_ATSC_DEMOD_ATSC_64QAM, MAPI_TRUE);
            break;
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_ATSC_256QAM:
        printf("\033[45;37m  ""%s[%d] ::  E_DEVICE_DEMOD_ATSC_256QAM \033[0m\n",__FUNCTION__,__LINE__);
            MDrv_DMD_ATSC_SetConfig(DMD_ATSC_DEMOD_ATSC_256QAM, MAPI_TRUE);
            break;
        default:
        printf("\033[45;37m  ""%s[%d] ::  DMD_ATSC_DEMOD_ATSC_64QAM \033[0m\n",__FUNCTION__,__LINE__);
            MDrv_DMD_ATSC_SetConfig(DMD_ATSC_DEMOD_ATSC_64QAM, MAPI_TRUE);
            break;
    }
    return MAPI_TRUE;


}
#endif
//##########################################################################################################
//##########################################################################################################
//########################################  Private Function Implementation ################################
//##########################################################################################################
//##########################################################################################################



MAPI_BOOL device_demodulator::WriteReg(MAPI_U16 u16Addr, MAPI_U8 u8Data)
{

    //MDrv_T3VIF_WriteByte((MAPI_U32)u16Addr,u8Data);
    //INTERN_DVBT_WriteReg(u16Addr, u8Data);
    MDrv_SYS_DMD_VD_MBX_WriteReg(u16Addr, u8Data);
    return MAPI_TRUE;

}



MAPI_BOOL device_demodulator::ReadReg(MAPI_U16 u16Addr, MAPI_U8 *pu8Data)
{

    //*pu8Data = MDrv_T3VIF_ReadByte((MAPI_U32)u16Addr);
    //INTERN_DVBT_ReadReg(u16Addr, pu8Data);
    MDrv_SYS_DMD_VD_MBX_ReadReg(u16Addr, pu8Data);
    return MAPI_TRUE;

}



MAPI_BOOL device_demodulator::Cmd_Packet_Send(S_CMDPKTREG* pCmdPacket, MAPI_U8 param_cnt)
{
    UNUSED(pCmdPacket);
    UNUSED(param_cnt);

    return MAPI_TRUE;

}



MAPI_BOOL device_demodulator::Cmd_Packet_Exe_Check(MAPI_BOOL* cmd_done)
{
    UNUSED(cmd_done);

    return MAPI_TRUE;

}



MAPI_BOOL device_demodulator::LoadDSPCode(void)
{


    return MAPI_TRUE;

}



MAPI_BOOL device_demodulator::I2C_Address_Polling(void)
{
    return MAPI_TRUE;


}







MAPI_BOOL device_demodulator::ATV_VIF_SetHandler(MAPI_BOOL bAutoScan, MAPI_U16 GainDistributionThr)
{
    UNUSED(bAutoScan);
    UNUSED(GainDistributionThr);

    return MAPI_TRUE;

}



MAPI_BOOL device_demodulator::ATV_VIF_IfInitial(MAPI_U16 VifVgaMaximum)
{
    UNUSED(VifVgaMaximum);

    return MAPI_TRUE;

}



MAPI_BOOL device_demodulator::ATV_VIF_TopAdjust(MAPI_U8 ucVifTop)
{
    UNUSED(ucVifTop);

    return MAPI_TRUE;

}


MAPI_BOOL device_demodulator::ATV_VIF_SoftReset()
{
    return MAPI_TRUE;

}

MAPI_BOOL device_demodulator::DTV_Config(RF_CHANNEL_BANDWIDTH BW, MAPI_BOOL bSerialTS, MAPI_BOOL bPalBG)
{
    UNUSED(BW);
    UNUSED(bSerialTS);
    UNUSED(bPalBG);

    return MAPI_TRUE;
}


void device_demodulator::Driving_Control(MAPI_BOOL bEnable)
{
    UNUSED(bEnable);

    return;
}



mapi_demodulator_datatype::E_VIF_TYPE device_demodulator::ATV_GetVIF_Type()
{
    return mapi_demodulator_datatype::E_VIF_INTERNAL;
}

MAPI_BOOL device_demodulator::DTV_GetDemod_Version(mapi_demodulator_datatype::EN_DEVICE_DEMOD_TYPE eDemodType, Demod_Version_t *tDemodVersion)
{
    MSIF_Version *pVersion = NULL;
    switch(eDemodType)
    {
#if (DVBC_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_C:
            MDrv_DMD_DVBC_GetLibVer(const_cast<const MSIF_Version **>(&pVersion));
            break;
#endif
#if (DVBT_SYSTEM_ENABLE == 1)
        case mapi_demodulator_datatype::E_DEVICE_DEMOD_DVB_T:
            MDrv_DMD_DVBT_GetLibVer(const_cast<const MSIF_Version **>(&pVersion));
            break;
#endif
        default:
            break;
    }

    if(NULL == pVersion)
    {
        printf("Error!!DVBC Demod version is NULL\n");
        return MAPI_FALSE;
    }
    else
    {
        memcpy(tDemodVersion->name, pVersion->MW.name, sizeof(pVersion->MW.name));
        memcpy(tDemodVersion->version, pVersion->MW.version, sizeof(pVersion->MW.version));
        memcpy(tDemodVersion->build, pVersion->MW.build, sizeof(pVersion->MW.build));
        memcpy(tDemodVersion->changelist, pVersion->MW.changelist, sizeof(pVersion->MW.changelist));
        return MAPI_TRUE;
    }
}

#if (DTMB_SYSTEM_ENABLE == 1)
MAPI_BOOL device_demodulator::DTV_DTMB_SetFrequency(MAPI_U32 u32Frequency, RF_CHANNEL_BANDWIDTH eBandWidth, mapi_demodulator_datatype::EN_DTMB_SUBCARRIERS eSubCarriers,
mapi_demodulator_datatype::EN_DTMB_PN_PADDING ePN, mapi_demodulator_datatype::EN_DTMB_MAPPING eMAPPING, mapi_demodulator_datatype::EN_DTMB_FEC_CODERATE eFEC)
{

        return FALSE;

}
MAPI_BOOL device_demodulator::DTV_DTMB_GetProperity(MAPI_U32* u32Frequency, RF_CHANNEL_BANDWIDTH* eBandWidth, mapi_demodulator_datatype::EN_DTMB_SUBCARRIERS* eSubCarriers,
mapi_demodulator_datatype::EN_DTMB_PN_PADDING* ePN, mapi_demodulator_datatype::EN_DTMB_MAPPING* eMAPPING, mapi_demodulator_datatype::EN_DTMB_FEC_CODERATE* eFEC)
{

        return FALSE;

}

mapi_demodulator_datatype::EN_LOCK_STATUS device_demodulator::DTV_DTMB_GetLockStatus(void)
{
    EN_LOCK_STATUS eLockStatus = E_DEMOD_NULL;
    DMD_DTMB_LOCK_STATUS uLockStatus = DMD_DTMB_UNLOCK;
   // MAPI_BOOL ret_lock = FALSE;
  //  printf("dan COFDM_LOCK_STATUS = %d\n", (MS_U8)eStatus);

  /*  if(eStatus == COFDM_PN_LOCK)
        eLockStatus = MDrv_DMD_DTMB_GetLock(DMD_DTMB_GETLOCK_VSB_FSYNCLOCK);//VT
      else if(eStatus == COFDM_FEC_LOCK)
      {
        MDrv_DMD_DTMB_GetLock(DMD_DTMB_GETLOCK);
        if(eLockStatus == DMD_DTMB_LOCK)
        {
            INTERN_DTMB_GetSNR();
            printf("FEC lock\n");
        }

    }

     if(eLockStatus == DMD_DTMB_LOCK)
        ret_lock = TRUE;
    */
    uLockStatus = MDrv_DMD_DTMB_GetLock(DMD_DTMB_GETLOCK);
    switch (uLockStatus) // _UTOPIA
    {
        case DMD_DTMB_LOCK:
            eLockStatus = E_DEMOD_LOCK;
            printf("FEC lock\n");
            break;
        case DMD_DTMB_CHECKING:
            eLockStatus = E_DEMOD_CHECKING;
            break;
        case DMD_DTMB_CHECKEND:
            eLockStatus = E_DEMOD_CHECKEND;
            break;
        case DMD_DTMB_UNLOCK:
            eLockStatus = E_DEMOD_UNLOCK;
            break;
        default:
            printf("DTMB_GetLockStatus error\n");
            break;
    }

    return (mapi_demodulator_datatype::EN_LOCK_STATUS)eLockStatus;
}
#endif
