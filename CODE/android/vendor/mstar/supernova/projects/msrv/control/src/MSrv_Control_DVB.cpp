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

// headers of itself
#include "MSrv_Control_DVB.h"

// headers of standard C libs
#include <limits.h>
#include <sys/prctl.h>

// headers of standard C++ libs

// headers of the same layer's
#include "MSrv_ATV_Player_Customer.h"
#include "MSrv_AV_Player.h"
#include "MSrv_SV_Player.h"
#include "MSrv_Storage_Player.h"
#include "MSrv_SCART_Player.h"
#include "MSrv_COMP_Player.h"
#include "MSrv_HDMI_Player.h"
#include "MSrv_PC_Player.h"
#include "MSrv_System_Database_DVB_Customer.h"
#include "MSrv_ATV_Database_Customer.h"
#include "MSrv_Picture_Customer.h"
#include "MSrv_Video.h"
#include "MSrv_Factory_Mode.h"
#include "MSrv_Factory_Mode_Customer.h"
#include "MSrv_Timer_Customer.h"
#if (CI_ENABLE == 1)
#include "MSrv_CIMMI_Customer.h"
#endif
#include "MSrv_SSSound_Customer.h"
#include "MSrv_DTV_Player.h"
#include "MSrv_DTV_Player_DVB.h"
#include "MSrv_DTV_Player_DVBT_Customer.h"
#include "MSrv_Control.h"
#include "MSrv_DTV_Player_DVBC_Customer.h"
#include "MSrv_DTV_Player_DVBS_Customer.h"
#include "MSrv_DTV_Player_DTMB_Customer.h"
#include "MSrv_Network_Control.h"
#include "MSrv_DivX_DRM.h"
#include "MSrv_RecordingScheduler.h"
#include "mapi_pcb.h"
#include "mapi_types.h"
#include "mapi_demodulator.h"
#if (ENABLE_LITE_SN != 1)
#include "mapi_panel.h"
#include "mapi_panel_datatype.h"
#endif


#if (CEC_ENABLE == 1)
#include "MSrv_CEC_Customer.h"
#endif

#if (MHL_ENABLE == 1)
#include "MSrv_MHL.h"
#endif //MHL_ENABLE

#if (HDMITX_ENABLE == 1)
#include "MSrv_HDMITX.h"
#include "SystemInfo.h"
#include "mapi_hdmitx.h"
#include "mapi_gpio.h"
#endif

#include "MSrv_MM_PlayerInterface.h"

#if (ENABLE_LITE_SN == 0)
#include "MSrv_BacklightControl.h"
#include "MSrv_Advert_Player.h"

#if (PVR_ENABLE == 1)
#include "MW_PVR.h"
#include "MW_PVRFileSystem.h"
#endif

#include "MW_ISystem.h"
#include "MW_Oemcrypto.h"
#include "MW_Vududrm.h"
#include "MW_SecureSys.h"
#include "MW_Secure_Storage.h"
#include "MW_Secure.h"
#include "MSrv_PreSharedKey.h"
#include "MSrv_SrcDetect.h"
#endif

#if (PVR_ENABLE == 1)
#include "MW_PVR.h"
#endif

#if (ISDB_SYSTEM_ENABLE == 1)
#include "MSrv_DTV_Player_ISDB_Customer.h"
#include "MSrv_ChannelManager_ISDB_Customer.h"
#else
#include "MSrv_ChannelManager_DVB_Customer.h"
#endif

#if (STEREO_3D_ENABLE == 1)
#include "MSrv_3DManager_Customer.h"
#endif

#if (TTX_ENABLE == 1)
#include "MW_TTX.h"
#endif

#if (HBBTV_ENABLE == 1)
#include "MW_HBBTV.h"
#endif

#if ((ISDB_CC_ENABLE == 1)||(NTSC_CC_ENABLE == 1))
#include "MW_CC.h"
#endif

#if ((AUTO_TEST == 1) && (MSTAR_TVOS == 0))
#include "AT_CmdManager_MSrv.h"
#endif

#if (ENABLE_NETREADY == 1)
#include "MSrv_DeviceAgent.h"
#endif

// headers of underlying layer's
#include "mapi_interface.h"
#include "mapi_sar.h"
#if (ENABLE_LITE_SN != 1)
#include "mapi_uartdebug.h"
#endif
#include "mapi_system.h"
#include "mapi_audio.h"
#include "SystemInfo.h"
#include "mapi_display.h"
#include "mapi_gpio_devTable.h"
#include "mapi_env_manager.h"
#if (STR_ENABLE == 1)
#include "mapi_str.h"
#endif

// header of ipc
#if (MSTAR_IPC == 1)
#include "apm.h"
#endif
#if (ENABLE_BACKEND == 1)
#include "MSrv_Backend.h"
#endif
#if (MWE_ENABLE == 1)
#include "MSrv_MWE.h"
#endif
#if(CA_ENABLE == 1)
#include "MSrv_CA.h"
#endif

#if (ENABLE_4K2K_NIKEU == 1)
#include "MSystem.h"
#endif


#if (ACR_ENABLE == 1)
#include "MSrv_SambaTv.h"
#endif

#define AUTO_SCART_NO_CHANGE                0
#define AUTO_SCART_CHANGE_SCART_1           1
#define AUTO_SCART_CHANGE_SCART_2           2
#define AUTO_SCART_CHANGE_TO_ORIGINAL       3


#define MSRV_CONTROL_DVB_ERR(fmt, arg...)          printf((char *)fmt, ##arg)
#define MSRV_CONTROL_DVB_DBG(fmt, arg...)          printf((char *)fmt, ##arg)
#define MSRV_CONTROL_DVB_INFO(fmt, arg...)         //printf((char *)fmt, ##arg)
#define MSRV_CONTROL_DVB_FLOW(fmt, arg...)         //printf((char *)fmt, ##arg)


MSrv_Control_DVB::MSrv_Control_DVB()
{
    MSrv_Control_common::m_monitor_st.p_class = this;
    m_bAutoScart_1_Enable = TRUE;
    m_bAutoScart_2_Enable = TRUE;
    m_u8DtvRouteCount = 0;
    m_bPreAutoScartConnect_1 = FALSE;
    m_bPreAutoScartConnect_2 = FALSE;
    m_bScart1Connect = FALSE;
    m_bScart2Connect = FALSE;
    m_enAutoScartRecordPreSourceType = MAPI_INPUT_SOURCE_NONE;
    m_enAutoScartRecordPreSourceType2 = MAPI_INPUT_SOURCE_NONE;
    m_bAutoScart_disable=FALSE;

    m_u32HeartBeatTime = mapi_time_utility::GetTime0();

    m_bSourceChange = FALSE;

#if (CEC_ENABLE == 1)
    memset(&m_InitCECThread, 0, sizeof(pthread_t));
    m_bCECThreadCreated = FALSE;
#endif

    m_bIsSysDBInitDone = FALSE;
}

MSrv_Control_DVB::~MSrv_Control_DVB()
{
}
EN_DVB_System_Type MSrv_Control_DVB::GetRoutePathDtvType(U8 index)
{
    EN_DVB_System_Type enDVBSystemType = E_DVB_System_NONE;

    switch(GetRouteTVMode(index))
    {
        case E_ROUTE_NONE:
            enDVBSystemType = E_DVB_System_NONE;
            break;
        case E_ROUTE_DVBT:
            enDVBSystemType = E_DVB_System_DVBT;
            break;
        case E_ROUTE_DVBC:
            enDVBSystemType = E_DVB_System_DVBC;
            break;
        case E_ROUTE_DVBS:
            enDVBSystemType = E_DVB_System_DVBS;
            break;
        case E_ROUTE_DVBT2:
            enDVBSystemType =  E_DVB_System_DVBT2;
            break;
        case E_ROUTE_DVBS2:
            enDVBSystemType = E_DVB_System_DVBS2;
            break;
        case E_ROUTE_DTMB:
            enDVBSystemType = E_DVB_System_DTMB;
            break;
        default:
            ASSERT(0);
            enDVBSystemType = E_DVB_System_NONE;
            break;
    }

    return enDVBSystemType;
}

BOOL MSrv_Control_DVB::Initialize()
{
#if (AUTO_TEST == 1)
    printf("[AT][SN][init msrv][%u]\n",  mapi_time_utility::GetPiuTimer1());
#endif

    MSRV_CONTROL_DVB_INFO("%s \n", __PRETTY_FUNCTION__);

    if(m_bInit)
    {
        ASSERT(0);
        return FALSE;
    }

    //Init Variable
    m_enAutoScartRecordPreSourceType = MAPI_INPUT_SOURCE_NONE; //Set it as default
    m_pInputSrcTable = SystemInfo::GetInstance()->GetInputMuxInfo();

    pthread_mutexattr_t attr;
    int intPTHChk;
    PTH_RET_CHK(pthread_mutexattr_init(&attr));
    PTH_RET_CHK(pthread_mutexattr_settype(&attr, PTHREAD_MUTEX_RECURSIVE));
    intPTHChk = PTH_RET_CHK(pthread_mutex_init(&m_MutexInputSrcSwitch, &attr));
    if(intPTHChk != 0)
    {
        ASSERT(0);
        return FALSE;
    }

    PTH_RET_CHK(pthread_mutexattr_init(&attr));
    PTH_RET_CHK(pthread_mutexattr_settype(&attr, PTHREAD_MUTEX_RECURSIVE));
    intPTHChk = PTH_RET_CHK(pthread_mutex_init(&m_AutoScartVariable, &attr));
    if(intPTHChk != 0)
    {
        ASSERT(0);
        return FALSE;
    }

    MSRV_CONTROL_DVB_INFO("%s \n", __PRETTY_FUNCTION__);

    pthread_t pthread_id;
    pthread_attr_t thr_attr;
    pthread_attr_init(&thr_attr);
    pthread_attr_setdetachstate(&thr_attr, PTHREAD_CREATE_DETACHED);
    pthread_attr_setstacksize(&thr_attr, PTHREAD_STACK_SIZE);
    intPTHChk = PTH_RET_CHK(pthread_create(&pthread_id, &thr_attr, InitSSoundthread, this));
    if(intPTHChk != 0)
    {
        ASSERT(0);
    }

    //<1>.To init the Source Input Info, Get it from System Class

    printf("=====> createAllMSrvClass ==> \n");
    if(createAllMSrvClass() == FALSE)
    {
        return FALSE;
    }

#if (TTX_ENABLE == 1)
#if (AUTO_TEST == 1)
    printf("[AT][SN][init ttx][%u]\n",  mapi_time_utility::GetPiuTimer1());
#endif

    MW_TTX *pTTx = MW_TTX::GetInstance();
    ASSERT(pTTx);
    pTTx->Init();
#endif

    MSrv_Factory_Mode *pFactoryMode = GetMSrvFactoryMode();
    ASSERT(pFactoryMode);
    pFactoryMode->Init();

    MSrv_Picture *pPicture = GetMSrvPicture();
    ASSERT(pPicture);
    pPicture->Initialize(MAPI_MAIN_WINDOW);

    MSrv_Video *pVideo = GetMSrvVideo();
    ASSERT(pVideo);
    pVideo->Initialize(MAPI_MAIN_WINDOW);

    //<3>.To do the IP Authentication
    ipSecurityAuthentication();

#if (AUTO_TEST == 1)
    printf("[AT][SN][init env][%u]\n",  mapi_time_utility::GetPiuTimer1());
#endif

    InitializeEnv();

#if ((ISDB_CC_ENABLE == 1)||(NTSC_CC_ENABLE == 1))
    MW_CC::GetInstance()->InitialCaption();
#endif

#if (ENABLE_LITE_SN != 1)
    mapi_panel *pPanel = NULL;
    pPanel = mapi_interface::Get_mapi_pcb()->GetPanel(0);
    if(NULL!=pPanel)
    {
        //pPanel->SetMEMCPanelMode(EN_CMD_VIDEO_MODE);//move to mboot
        pPanel->SetMEMCPanelMode(EN_CMD_COLOR_ENGINE_BYPASS);
        pPanel->SetMEMCPanelMode(EN_CMD_MEMC_LEVEL_STRONG);
    }
    else
        printf("\n[WARNING] : GetPanel is NULL!\n");
#endif

#if (SEAMLESS_ZOOMING_ENABLE == 1)
        mapi_interface::Get_mapi_display()->SetSeamlessZooming(TRUE);
#endif

    WaitAudioInit(AUDIO_INIT_WAIT_TIMEOUT);
    m_bInit = TRUE;

#if ((ISDB_CC_ENABLE == 1)||(NTSC_CC_ENABLE == 1))
    MW_CC *pCC = MW_CC::GetInstance();
    ASSERT(pCC);
    pCC->InitialCaption();
#endif
#if (HDMITX_ENABLE == 1)
    // Create Init HDMI Tx Thread
    pthread_t pthread_id_HDMMI_TX_Init;
    pthread_attr_t thr_attr_HDMMI_TX_Init;
    pthread_attr_init(&thr_attr_HDMMI_TX_Init);
    pthread_attr_setdetachstate(&thr_attr_HDMMI_TX_Init, PTHREAD_CREATE_DETACHED);
    pthread_attr_setstacksize(&thr_attr_HDMMI_TX_Init, PTHREAD_STACK_SIZE);
    intPTHChk = PTH_RET_CHK(pthread_create(&pthread_id_HDMMI_TX_Init, &thr_attr_HDMMI_TX_Init, HdmitxInitThread, this));
    if(intPTHChk != 0)
    {
        ASSERT(0);
    }

    //Enable 4k2k plug-in detection only when the gpio is defined.
    mapi_gpio *gptr = mapi_gpio::GetGPIO_Dev(ROCKET_HDMI_PLUG);
    if(gptr != NULL)
    {
        //Create a thread to monitor the pluged in/out event of the rocket.
        pthread_t pthread_id_hdmi_plug_monitor;
        pthread_attr_t thr_attr_hdmi_plug_monitor;
        pthread_attr_init(&thr_attr_hdmi_plug_monitor);
        pthread_attr_setdetachstate(&thr_attr_hdmi_plug_monitor, PTHREAD_CREATE_DETACHED);
        pthread_attr_setstacksize(&thr_attr_hdmi_plug_monitor, PTHREAD_STACK_SIZE);
        intPTHChk = PTH_RET_CHK(pthread_create(&pthread_id_hdmi_plug_monitor, &thr_attr_hdmi_plug_monitor, HPResMonitorThread, this));
        if(intPTHChk != 0)
        {
            ASSERT(0);
        }
    }
#endif
    return TRUE;
}

#if (HDMITX_ENABLE == 1)
void *MSrv_Control_DVB::HdmitxInitThread(void *arg)
{
    prctl(PR_SET_NAME, (unsigned long)__FUNCTION__);
    int res;
    const char* resolution = NULL;
    IEnvManager* pEnvMan = NULL;
    MSrv_Video *pMSrvVideo = NULL;
    MSrv_HDMITX *pMSrvHdmiTx = NULL;
    EN_TIMING enTimingType = E_TIMING_2K1KP_60;

    //Get Object
    pMSrvHdmiTx = MSrv_Control::GetMSrvHdmiTx();
    pMSrvVideo = MSrv_Control::GetMSrvVideo();
    pEnvMan = IEnvManager::Instance();

    //Error checking
    ASSERT(pMSrvHdmiTx);
    ASSERT(pMSrvVideo);
    ASSERT(pEnvMan);

    // Init HDMI Tx
    pMSrvHdmiTx->Init();
    resolution = pEnvMan->GetEnv("resolution");
    ASSERT(resolution);//check resolution Null pointer, need to add at least 1 resolution in mboot env
    res = atoi(resolution);

    enTimingType = pMSrvHdmiTx->getTimingTransformType((EN_MAPI_DEVICE_ROCKY_VIDEO_TIMING) res);
    pMSrvVideo->SetOutputTiming(enTimingType);

#if (CVBSOUT_XCTOVE_ENABLE == 1)
    EN_MAPI_HDMITX_TIMING_TYPE type = E_MAPI_HDMITX_TIMING_1080_60P;
    type = pMSrvHdmiTx->getMapiTimingType((EN_MAPI_DEVICE_ROCKY_VIDEO_TIMING) res);

    if (IsVideoOutFreeToUse() == MAPI_TRUE)
    {
        if (mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_MONITOR_MODE)->IsDestTypeExistent(MAPI_MAIN_WINDOW))
        {
            mapi_video_out *pVideoOut = mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_MONITOR_MODE);
            ASSERT(pVideoOut);

            pVideoOut->Initialize(MAPI_INPUT_SOURCE_DTV, MAPI_MAIN_WINDOW);
            if(mapi_display::Get_bootlogo_display() == FALSE)
            {
                pVideoOut->SetVideoMute(MAPI_TRUE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, MAPI_MAIN_WINDOW);

                if ((type == E_MAPI_HDMITX_TIMING_4K2K_60P)
                    || (type == E_MAPI_HDMITX_TIMING_4K2K_50P)
                    || (type == E_MAPI_HDMITX_TIMING_4K2K_30P)
                    || (type == E_MAPI_HDMITX_TIMING_4K2K_25P))
                {
                    //Unsupported output timing for 4k2k
                    printf("Warning CVBS out unsupported output timing, type=%d, please check this.\n", type);
                }
                else
                {
                    switch (type)
                    {
                        case E_MAPI_HDMITX_TIMING_576_50I:
                        case E_MAPI_HDMITX_TIMING_576_50P:
                        case E_MAPI_HDMITX_TIMING_720_50P:
                        case E_MAPI_HDMITX_TIMING_1080_50I:
                        case E_MAPI_HDMITX_TIMING_1080_50P:
                        case E_MAPI_HDMITX_TIMING_1080_25P:
                        case E_MAPI_HDMITX_TIMING_1080_24P:
                        case E_MAPI_HDMITX_TIMING_1440_50P:
                        case E_MAPI_HDMITX_TIMING_1440_24P:
                        case E_MAPI_HDMITX_TIMING_1470_50P:
                        case E_MAPI_HDMITX_TIMING_1470_24P:
                        case E_MAPI_HDMITX_TIMING_2160_24P:
                        pVideoOut->SetMode(mapi_video_out_datatype::MAPI_VIDEO_OUT_VE_PAL, MAPI_MAIN_WINDOW);
                        break;
                        case E_MAPI_HDMITX_TIMING_480_60I:
                        case E_MAPI_HDMITX_TIMING_480_60P:
                        case E_MAPI_HDMITX_TIMING_720_60P:
                        case E_MAPI_HDMITX_TIMING_1080_60I:
                        case E_MAPI_HDMITX_TIMING_1080_60P:
                        case E_MAPI_HDMITX_TIMING_1080_30P:
                        case E_MAPI_HDMITX_TIMING_1440_60P:
                        case E_MAPI_HDMITX_TIMING_1440_30P:
                        case E_MAPI_HDMITX_TIMING_1470_60P:
                        case E_MAPI_HDMITX_TIMING_1470_30P:
                        case E_MAPI_HDMITX_TIMING_2160_30P:
                        pVideoOut->SetMode(mapi_video_out_datatype::MAPI_VIDEO_OUT_VE_NTSC, MAPI_MAIN_WINDOW);
                        break;
                        default:
                        pVideoOut->SetMode(mapi_video_out_datatype::MAPI_VIDEO_OUT_VE_AUTO, MAPI_MAIN_WINDOW);
                            break;
                    }
                    pVideoOut->SetVideoMute(MAPI_FALSE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, MAPI_MAIN_WINDOW);
                }
            }
        }
    }
#endif
    pthread_exit(NULL);
}
#endif
void MSrv_Control_DVB::StartThreadMonitor()
{
    //To create threadMonitor, for Auto Scart Monitor and No Signal Monitor
    MSRV_CONTROL_DVB_INFO("---> MSrv_Control_DVB::Init- To create threadMonitor \n");

    //To Init variable for ThreadMonitor
    m_bPreAutoScartConnect_1 = FALSE;
    m_bPreAutoScartConnect_2 = FALSE;
    m_bScart1Connect = FALSE;
    m_bScart2Connect = FALSE;
    m_enAutoScartRecordPreSourceType = MAPI_INPUT_SOURCE_ATV;

    pthread_attr_t thr_attr;
    pthread_attr_init(&thr_attr);
#if (STR_ENABLE == 0)
    pthread_attr_setdetachstate(&thr_attr, PTHREAD_CREATE_DETACHED);
#endif
    pthread_attr_setstacksize(&thr_attr, PTHREAD_STACK_SIZE);

    int intPTHChk = PTH_RET_CHK(pthread_create(&m_pthreadMonitor_id, &thr_attr, threadMonitor, (void *) &MSrv_Control_common::m_monitor_st));
    ASSERT(0 == intPTHChk);
}

void* MSrv_Control_DVB::threadMonitor(void *arg)
{
    //bool *pActive;
    //pActive = (bool*)arg;
    Monitor_t *ptr = (Monitor_t *)arg;
    MSrv_Control_DVB *c_ptr = (MSrv_Control_DVB*)ptr->p_class;
    prctl(PR_SET_NAME, (unsigned long)"MControl Monitor");
#if (STR_ENABLE == 1)
    mapi_str::AutoRegister _R;
#endif
    int iRet;
    MSRV_CMD stCmd;

    BOOL bRet = FALSE;

    memset(&stCmd, 0, sizeof(MSRV_CMD));

#if (SSC_ENABLE == 1)
    UpdateSSCPara();
#endif
    // Display on must after the Sleep Mode Check //
#if (ACTIVE_STANDBY_MODE_ENABLE == 1)
    MSrv_Control_common::StandbyModeActiveProcess();
#else
    if(mapi_display::Get_bootlogo_display() == FALSE)
    {
        mapi_interface::Get_mapi_display()->OnOff(TRUE);
    }
#endif
    MSrv_Control_common::m_monitor_st.m_bFlagThreadMonitorActive = TRUE;

    while(ptr->m_bFlagThreadMonitorActive)
    {
        if (mapi_time_utility::GetTime0() > (c_ptr->m_u32HeartBeatTime + THREAD_HEART_BEAT_TIME))
        {
            SendHeartBeat(EN_THREAD_HEART_BEAT_ALIVE);
            c_ptr->m_u32HeartBeatTime = mapi_time_utility::GetTime0();
        }

#if (CHINA_ENABLE==0 && PIP_ENABLE==0)
        if(m_pMSrvList[E_MSRV_SCART_PLAYER] != NULL)
        {
            c_ptr->scartOutVif_Handler();

            c_ptr->autoScartProc_Handler();
        }
#endif
#if (MSTAR_TVOS == 1)
        c_ptr->burnIn_Nosignal_RGB();
#endif
        bRet = c_ptr->noSignalCheck_Handler();
        c_ptr->GPIOPolling_Handler();

#if (TTX_ENABLE == 1)
        c_ptr->teletextClock_Handler();
#endif

        mapi_interface::Get_mapi_audio()->AUDIO_Monitor_Service();

        if (TRUE == bRet)
        {
            iRet = c_ptr->m_pCmdEvt->Wait(&stCmd, THREAD_MONITOR_INTERVAL_MS);

            if (iRet == 0)
            {
                if (stCmd.enCmd == E_CMD_SET_CURRENT_INPUT_SOURCE)
                {
#if (CI_PLUS_ENABLE == 1)
                    MAPI_INPUT_SOURCE_TYPE enCurrentInputType;
                    enCurrentInputType = MSrv_Control::GetInstance()->GetCurrentInputSource();

                    if (MAPI_INPUT_SOURCE_DTV == enCurrentInputType)
                    {
                        MSrv_DTV_Player_DVB *pMsrvDtv = dynamic_cast<MSrv_DTV_Player_DVB *>(GetMSrvDtv());
                        if (pMsrvDtv != NULL)
                        {
                            BOOL bisCiOccupiedTuner = FALSE;
                            bisCiOccupiedTuner = pMsrvDtv->IsCiOccupiedTuner(TRUE);
                            if (TRUE == bisCiOccupiedTuner)
                            {
                                MSRV_CONTROL_DVB_INFO("Block Input Source \n");
                                c_ptr->m_pCmdAckEvt->Send(stCmd);
                            }
                            else
                            {
                                c_ptr->SetInputSourceCmd((MAPI_INPUT_SOURCE_TYPE)stCmd.u32Param1, (BOOL)stCmd.u32Param2, (MAPI_SCALER_WIN)stCmd.u32Param3);
                                c_ptr->m_pCmdAckEvt->Send(stCmd);
                            }
                        }
                        else
                        {
                            c_ptr->SetInputSourceCmd((MAPI_INPUT_SOURCE_TYPE)stCmd.u32Param1, (BOOL)stCmd.u32Param2, (MAPI_SCALER_WIN)stCmd.u32Param3);
                            c_ptr->m_pCmdAckEvt->Send(stCmd);
                        }
                    }
                    else
#endif
                    {
                        c_ptr->SetInputSourceCmd((MAPI_INPUT_SOURCE_TYPE)stCmd.u32Param1, (BOOL)stCmd.u32Param2, (MAPI_SCALER_WIN)stCmd.u32Param3);
                        c_ptr->m_pCmdAckEvt->Send(stCmd);
                    }
                }

                if (stCmd.enCmd == E_CMD_SET_DTV_ROUTE)
                {
#if (CI_PLUS_ENABLE == 1)
                    MSrv_DTV_Player_DVB *pMsrvDtv = dynamic_cast<MSrv_DTV_Player_DVB *>(GetMSrvDtv());
                    if (pMsrvDtv != NULL)
                    {
                        BOOL bisCiOccupiedTuner = FALSE;
                        if (TRUE == (BOOL)stCmd.u32Param3)
                        {
                            bisCiOccupiedTuner = pMsrvDtv->IsCiOccupiedTuner(TRUE);
                        }
                        if (TRUE == bisCiOccupiedTuner)
                        {
                            MSRV_CONTROL_DVB_INFO("Block DTV Route \n");
                            c_ptr->m_pCmdAckEvt->Send(stCmd);
                        }
                        else
                        {
                            c_ptr->SwitchMSrvDtvRouteCmd((MAPI_U8)stCmd.u32Param1, (MAPI_SCALER_WIN)stCmd.u32Param2);
                            c_ptr->m_pCmdAckEvt->Send(stCmd);
                            GetMSrvAtvDatabase()->Init();
                        }
                    }
                    else
#endif
                    {
                        c_ptr->SwitchMSrvDtvRouteCmd((MAPI_U8)stCmd.u32Param1, (MAPI_SCALER_WIN)stCmd.u32Param2);
                        c_ptr->m_pCmdAckEvt->Send(stCmd);
                        GetMSrvAtvDatabase()->Init();
                    }
                }
                if (stCmd.enCmd == E_CMD_SET_RECORD_SERVICE_BY_ROUTE)
                {
                   ST_TRIPLE_ID stService;
                   stService.u16OnId = stCmd.u32Param1 >> 16;
                   stService.u16TsId = stCmd.u32Param1 & 0xFFFF;
                   stService.u16SrvId = stCmd.u32Param2;
                   stCmd.u32Param1 = c_ptr->_DoSetRecordServiceByRoute(stService, stCmd.u32Param3);
                   c_ptr->m_pCmdAckEvt->Send(stCmd);
                }
                if (stCmd.enCmd == E_CMD_START_RECORD_ROUTE)
                {
                    stCmd.u32Param1 = static_cast<EN_PVR_STATUS>(c_ptr->_DoStartRecordRoute((U8)stCmd.u32Param1, (U16)stCmd.u32Param2));
                    c_ptr->m_pCmdAckEvt->Send(stCmd);
                }
                if (stCmd.enCmd == E_CMD_STOP_RECORD_ROUTE)
                {
                    c_ptr->_DoStopRecordRoute((U8)stCmd.u32Param1);
                    c_ptr->m_pCmdAckEvt->Send(stCmd);
                }
#if (CI_PLUS_ENABLE == 1)
                if (stCmd.enCmd == E_CMD_CI_HC_ASK_RELEASE)
                {
                    ((MSrv_DTV_Player_DVB*)(GetMSrvDtv()))->SetCiHcRelease();
                }
                if (stCmd.enCmd == E_CMD_CI_HC_ASK_RELEASE_REPLY)
                {
                    stCmd.enCmd = E_CMD_CI_HC_ASK_RELEASE_REPLY;
                    c_ptr->m_pCmdAckEvt->Send(stCmd);
                }
#endif
            }
        }
        else
        {
#if (STR_ENABLE == 1)
            for(int i=0;i<THREAD_MONITOR_INTERVAL_MS;i++)
            {
                if(!(ptr->m_bFlagThreadMonitorActive))break;
                usleep(1000);
            }
#else
            usleep(THREAD_MONITOR_INTERVAL_MS*1000);
#endif
        }
    }
    SendHeartBeat(EN_THREAD_HEART_BEAT_FINALIZE);
    pthread_exit(NULL);
}

#if (TTX_ENABLE == 1)
void MSrv_Control_DVB::teletextClock_Handler(void)
{
    MSrv_Player *pPlayer = GetMSrvPlayer(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW));

    if(pPlayer == NULL)
    {
        return;
    }

    pPlayer->MonitorTTXClockMode();
}

#endif

#if (MSTAR_TVOS == 1)
void MSrv_Control_DVB::burnIn_Nosignal_RGB(void)
{
    static U8 encolor = (U8)mapi_video_datatype::E_SCREEN_MUTE_WHITE;
    static U8 count = 0;
    BOOLEAN bBurnInNoSignalRGB = FALSE;

    MSrv_Player *pPlayer = GetMSrvPlayer(GetCurrentInputSource());
    if(pPlayer == NULL)
    {
        return;
    }

    GetMSrvSystemDatabase()->GetFactoryExtSetting((&bBurnInNoSignalRGB), EN_FACTORY_EXT_BURN_IN_NOSIGNAL_RGB);
    if((bBurnInNoSignalRGB == TRUE) && (pPlayer->IsSignalStable() == FALSE))
    {
        count++;
        if(count > 20)
        {
            count = 0;
            SetVideoMute(TRUE );
            encolor++;
            if(encolor >= mapi_video_datatype::E_SCREEN_MUTE_NUMBER)
            {
                encolor = mapi_video_datatype::E_SCREEN_MUTE_WHITE;
            }
        }
    }
}
#endif

void MSrv_Control_DVB::scartOutVif_Handler(void)
{
#ifdef SIM_BUILD
    return TRUE;
#else
    if(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW) >= MAPI_INPUT_SOURCE_STORAGE)
    {
        return; //APP Mode(MM): Do not support Auto Scart
    }

#if (VE_ENABLE == 1 ||CVBSOUT_ENABLE==1) && (CONNECTTV_BOX == 0)
    if(m_pInputSrcTable[MAPI_INPUT_SOURCE_ATV].u32EnablePort)
    {
        if((MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW) != MAPI_INPUT_SOURCE_DTV) &&
            (MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW) != MAPI_INPUT_SOURCE_ATV))
        {
            mapi_interface::Get_mapi_demod()->ATV_VIF_Handler(FALSE);
        }
    }
#if (ACTIVE_STANDBY_MODE_ENABLE == 1)
   if(TRUE == MSrv_Control_common::GetActiveStandbyMode())
   {
        if(IsVideoOutTVModeFreeToUse() == TRUE)
        {
            mapi_video_out *pVideoOut = mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_MONITOR_MODE);
            if(pVideoOut != NULL)
            {
                if(pVideoOut->IsDestTypeExistent(MAPI_MAIN_WINDOW))
                {
                    pVideoOut->SetVideoMute(MAPI_TRUE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, MAPI_MAIN_WINDOW);
                }
            }
        }
   }
#endif
#endif
#endif
}

BOOL MSrv_Control_DVB::HasAnyScartConnect(void)
{
    BOOL bScart1Connect = FALSE, bScart2Connect=FALSE;
    if(m_pInputSrcTable[MAPI_INPUT_SOURCE_SCART].u32EnablePort)
    {
        bScart1Connect = mapi_interface_TV::Get_mapi_sar()->IsScart1Connected();
    }
    else
    {
        bScart1Connect = FALSE;
    }

    if(m_pInputSrcTable[MAPI_INPUT_SOURCE_SCART2].u32EnablePort)
    {
        bScart2Connect = mapi_interface_TV::Get_mapi_sar()->IsScart2Connected();
    }
    else
    {
        bScart2Connect = FALSE;
    }

    return (bScart1Connect|bScart2Connect);
}

void MSrv_Control_DVB::DisableAutoScart(BOOL bDisabled)
{
    if(bDisabled == TRUE)
           printf(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n");
    else
           printf("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n");
    m_bScart1Connect = mapi_interface_TV::Get_mapi_sar()->IsScart1Connected();
    m_bScart2Connect = mapi_interface_TV::Get_mapi_sar()->IsScart2Connected();
    m_bPreAutoScartConnect_1 = m_bScart1Connect;
    m_bPreAutoScartConnect_2 = m_bScart2Connect;
    m_bAutoScart_disable=bDisabled;
}


void MSrv_Control_DVB::autoScartProc_Handler(void)
{
#ifdef SIM_BUILD
    return TRUE;
#else
    u8 enChangeInputSource = AUTO_SCART_NO_CHANGE;
    BOOL ret;

    if(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW) >= MAPI_INPUT_SOURCE_STORAGE)
    {
        return; //APP Mode(MM): Do not support Auto Scart
    }
    if(m_bAutoScart_disable)
    {
        return;
    }

    {
        //Other Thread will access variable m_bAutoScart_1_Enable,m_bAutoScart_2_Enable => Need to protect (Lock)
        mapi_scope_lock(scopeLock, &m_AutoScartVariable);

        if((m_pInputSrcTable[MAPI_INPUT_SOURCE_SCART].u32EnablePort ) && (m_bAutoScart_1_Enable == TRUE))
        {
            m_bScart1Connect = mapi_interface_TV::Get_mapi_sar()->IsScart1Connected();
        }
        else
        {
            m_bScart1Connect = FALSE;
        }

        if((m_pInputSrcTable[MAPI_INPUT_SOURCE_SCART2].u32EnablePort ) && (m_bAutoScart_2_Enable == TRUE))
        {
            m_bScart2Connect = mapi_interface_TV::Get_mapi_sar()->IsScart2Connected();
        }
        else
        {
            m_bScart2Connect = FALSE;
        }
    }

    //Check Follow => <1>.Status Change <2>.Coonect or Disconnect <3>.Need to change Source Input???
    if((m_bScart1Connect != m_bPreAutoScartConnect_1) || (m_bScart2Connect != m_bPreAutoScartConnect_2))
    {
        // <1>.Status Change
        if((m_bScart1Connect != m_bPreAutoScartConnect_1) && (m_bScart2Connect == m_bPreAutoScartConnect_2))
        {
            //=================Case-1: Scart-1 change, Scart-2 not change
            //Check <2>.Coonect or Disconnect
            if(m_bScart1Connect == TRUE)
            {
                //From 0 to 1, connect
                //To check input source
                if(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW) != MAPI_INPUT_SOURCE_SCART)
                {
                    enChangeInputSource = AUTO_SCART_CHANGE_SCART_1;
                }
            }
            else
            {
                //From 1 to 0, Disconnect
                if(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW) == MAPI_INPUT_SOURCE_SCART)
                {
                    enChangeInputSource = AUTO_SCART_CHANGE_TO_ORIGINAL;
                }
            }

        }
        else if((m_bScart1Connect == m_bPreAutoScartConnect_1) && (m_bScart2Connect != m_bPreAutoScartConnect_2))
        {
            //===========Case-2: Scart-1 Not change, Scart-2 change
            //Check <2>.Coonect or Disconnect
            if(m_bScart2Connect == TRUE)
            {
                //From 0 to 1, connect
                //To check input source
                if(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW) != MAPI_INPUT_SOURCE_SCART2)
                {
                    enChangeInputSource = AUTO_SCART_CHANGE_SCART_2;
                }
            }
            else  //From 1 to 0, Disconnect
            {
                if(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW) == MAPI_INPUT_SOURCE_SCART2)
                {
                    enChangeInputSource = AUTO_SCART_CHANGE_TO_ORIGINAL;
                }
            }
        }
        else if((m_bScart1Connect != m_bPreAutoScartConnect_1) && (m_bScart2Connect != m_bPreAutoScartConnect_2))
        {
            //Case-3: Scart-1 change, Scart-2 change at the same time => Change to Scart-1
            if((m_bScart1Connect == TRUE) && (m_bScart2Connect == TRUE))
            {
                enChangeInputSource = AUTO_SCART_CHANGE_SCART_1;
            }
            else  if((m_bScart1Connect == TRUE) && (m_bScart2Connect == FALSE))
            {
                enChangeInputSource = AUTO_SCART_CHANGE_SCART_1;
            }
            else  if((m_bScart1Connect == FALSE) && (m_bScart2Connect == TRUE))
            {
                enChangeInputSource = AUTO_SCART_CHANGE_SCART_2;
            }
            else
            {
                enChangeInputSource = AUTO_SCART_CHANGE_TO_ORIGINAL;
            }
        }

        if(enChangeInputSource == AUTO_SCART_CHANGE_SCART_1)
        {
            if(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW) != MAPI_INPUT_SOURCE_SCART)
            {
                if(m_enAutoScartRecordPreSourceType == MAPI_INPUT_SOURCE_NONE)
                {
                    m_enAutoScartRecordPreSourceType = MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW);
                }
                else
                {
                    m_enAutoScartRecordPreSourceType2 = MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW);
                }
                m_bSourceChange = TRUE;
                PostEvent(0, EV_SCARTMUTE_OSD_MODE, 0);
                SetInputSource(MAPI_INPUT_SOURCE_SCART,TRUE);
                m_bSourceChange = FALSE;
                //SetInputSource(MAPI_INPUT_SOURCE_SCART);
                //sleep(1);//????? Need to explain
            }
        }
        else  if(enChangeInputSource == AUTO_SCART_CHANGE_SCART_2)
        {
            if(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW) != MAPI_INPUT_SOURCE_SCART2)
            {
                if(m_enAutoScartRecordPreSourceType == MAPI_INPUT_SOURCE_NONE)
                {
                    m_enAutoScartRecordPreSourceType = MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW);
                }
                else
                {
                    m_enAutoScartRecordPreSourceType2 = MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW);
                }
                m_bSourceChange = TRUE;
                PostEvent(0, EV_SCARTMUTE_OSD_MODE, 0);
                SetInputSource(MAPI_INPUT_SOURCE_SCART2,TRUE);
                m_bSourceChange = FALSE;
                //SetInputSource(MAPI_INPUT_SOURCE_SCART2);
                //sleep(1);
            }
        }
        else  if(enChangeInputSource == AUTO_SCART_CHANGE_TO_ORIGINAL)//Change to old source
        {
            if(m_enAutoScartRecordPreSourceType2 != MAPI_INPUT_SOURCE_NONE)
            {
                m_bSourceChange = TRUE;
                ret = SetInputSource(m_enAutoScartRecordPreSourceType2,TRUE);
                //ret = SetInputSource(m_enAutoScartRecordPreSourceType2);
                if(m_enAutoScartRecordPreSourceType2 == MAPI_INPUT_SOURCE_DTV)
                {
                    if(ret == TRUE)
                    {
                        GetMSrvDtv()->PlayCurrentProgram();
                    }
                }
                else if (m_enAutoScartRecordPreSourceType2 == MAPI_INPUT_SOURCE_ATV)
                {
                    if(ret == TRUE)
                    {
                        GetMSrvAtv()->SetChannel(GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER , 0, 0, NULL), 1);
                    }

                }

                m_enAutoScartRecordPreSourceType2 = MAPI_INPUT_SOURCE_NONE;
                m_bSourceChange = FALSE;
            }
            else if(m_enAutoScartRecordPreSourceType != MAPI_INPUT_SOURCE_NONE)
            {
                m_bSourceChange = TRUE;
                ret = SetInputSource(m_enAutoScartRecordPreSourceType,TRUE);
                //ret = SetInputSource(m_enAutoScartRecordPreSourceType);
                //sleep(1);//we need one second to solve the Scart debounce issue
                if(m_enAutoScartRecordPreSourceType2 == MAPI_INPUT_SOURCE_DTV)
                {
                    if(ret == TRUE)
                    {
                        GetMSrvDtv()->PlayCurrentProgram();
                    }
                }
                m_bSourceChange = FALSE;

                //sleep(1);
                m_enAutoScartRecordPreSourceType = MAPI_INPUT_SOURCE_NONE;
            }
        }

        m_bPreAutoScartConnect_1 = m_bScart1Connect;
        m_bPreAutoScartConnect_2 = m_bScart2Connect;
    }
#endif
}

BOOL MSrv_Control_DVB::SetInputSourceCmd(MAPI_INPUT_SOURCE_TYPE eInputSrc, BOOL bWriteDB, MAPI_SCALER_WIN eWin)
{
#if (AUTO_TEST == 1)
    printf("[AutoTest][SourceChange][SetInputSourceCmd START][%u]\n", mapi_time_utility::GetTime0());
#endif

    mapi_scope_lock(scopeLock, &m_MutexInputSrcSwitch);

#if(SQL_DB_ENABLE == 1 && MSTAR_TVOS == 1)
    GetMSrvSystemDatabase()->LoadVideoSetting(eInputSrc);
    GetMSrvSystemDatabase()->LoadNLASetting(eInputSrc);
#if (STEREO_3D_ENABLE == 1)
    GetMSrvSystemDatabase()->LoadNLASetting3D(eInputSrc);
#endif
#endif
#if (ENABLE_4K2K_NIKEU == 1)
    IDirectFB *dfb = MSystem::GetInstance()->dfb;
    if(!dfb)
    {
        printf("error!dfb is NULL!!!\n");
    }
    else
    {
        if(eInputSrc == MAPI_INPUT_SOURCE_DTV)
        {
            dfb->ResumeLayers(dfb);
        }
        else
        {
            dfb->SuspendLayers(dfb);
        }
    }
#endif
    MSRV_CONTROL_DVB_INFO("%s : %d\n", __PRETTY_FUNCTION__, (int)eInputSrc);
    if(eWin == MAPI_MAIN_WINDOW)
    {
#if (MWE_ENABLE == 1)
        if(MSrv_MWE::GetInstance()->GetMWEStatus() != MSrv_MWE::E_EN_MS_MWE_OFF)
        {
            MSrv_MWE::GetInstance()->SetMWEStatus(MSrv_MWE::E_EN_MS_MWE_OFF);
        }
#endif
    }
    MSRV_CONTROL_DVB_INFO("%s::: %u\n", __PRETTY_FUNCTION__, __LINE__);

#if (PIP_ENABLE == 1)
    if(m_enPipMode==E_PIP_MODE_OFF && m_enPrePipMode== E_PIP_MODE_OFF)
#endif
    {
        //<1>.Input Parameter Check
        if(eInputSrc > MAPI_INPUT_SOURCE_NUM)
        {
            MSRV_CONTROL_DVB_INFO("MSrv_Control_DVB: SetInputSource, enInput(%d) error, Force to ATV\n", (int)eInputSrc);
            ASSERT(0);
            eInputSrc = MAPI_INPUT_SOURCE_ATV;
        }
    }

#if (INPUT_SOURCE_LOCK_ENABLE == 1)
    //For clean input source lock(ISL) pin code
#if (PIP_ENABLE == 1)
    if(eWin == MAPI_MAIN_WINDOW)
#endif
    {
        if (IsInputSourceLock(eInputSrc) == MAPI_FALSE)
        {
#if (MSTAR_TVOS == 1)
            GetMSrvPlayer(eInputSrc)->PostEvent(NULL, EV_INPUT_SOURCE_LOCK, E_INPUT_SOURCE_LOCK_EVENT_OFF, E_INPUT_SOURCE_LOCK_EVENT_UNDEFINED);
#else
            PostEvent(NULL, EV_INPUT_SOURCE_LOCK, E_INPUT_SOURCE_LOCK_EVENT_OFF, E_INPUT_SOURCE_LOCK_EVENT_UNDEFINED);
#endif
        }
    }
#endif

    m_bSourceChange = TRUE;


#if (CEC_ENABLE == 1)
    ST_INPUTSOURCE stInputSrc;
    stInputSrc.CurrInputSrc = eInputSrc;
    stInputSrc.OriInputSrc = MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW);
    if(m_bCECThreadCreated == TRUE)
    {
        pthread_join(m_InitCECThread, NULL);
        m_bCECThreadCreated = FALSE;
    }
    if(PTH_RET_CHK(pthread_create(&m_InitCECThread, NULL, MSrv_Control_common::InitCECThread, (void *)&stInputSrc)) != 0)
    {
        ASSERT(0);
    }
    m_bCECThreadCreated = TRUE;
#endif

    // Audio should be mute before source change
#if (PIP_ENABLE == 1)
    if(eWin == MAPI_MAIN_WINDOW)
    {
        GetMSrvSSSound()->SetMuteStatus(MUTE_SOURCESWITCH, MAPI_TRUE, MSRV_AUDIO_PROCESSOR_MAIN);
#if (INPUT_SOURCE_LOCK_ENABLE == 1)
        //Check input source lock(ISL) status of target source
        if (IsInputSourceLock(eInputSrc) == MAPI_TRUE)
        {
            GetMSrvSSSound()->SetMuteStatus(MUTE_INPUT_SOURCEL_LOCK, MAPI_TRUE, MSRV_AUDIO_PROCESSOR_MAIN);
        }
#endif
    }
    else
    {
#if (PREVIEW_MODE_ENABLE == 1)
        if(FALSE == IsPreviewModeRunning())
#endif
        {
            //@ swith all to main
            MSAPI_AUDIO_OUTPORT_SOURCE_INFO  pSourceInfo;
            mapi_interface::Get_mapi_audio()->GetOutputSourceInfo(&pSourceInfo);
#if (CVBSOUT_XCTOVE_ENABLE == 1)
            pSourceInfo.MonitorOut = AUDIO_PROCESSOR_MAIN;
#else
            pSourceInfo.MonitorOut = AUDIO_PROCESSOR_SUB;
#endif
            pSourceInfo.HpOut = AUDIO_PROCESSOR_SUB;
            mapi_interface::Get_mapi_audio()->SetOutputSourceInfo(&pSourceInfo);
            GetMSrvSSSound()->SetMuteStatus(MUTE_SOURCESWITCH, MAPI_TRUE, MSRV_AUDIO_PROCESSOR_SUB);
#if (INPUT_SOURCE_LOCK_ENABLE == 1)
            //Check input source lock(ISL) status of target source
            if (IsInputSourceLock(eInputSrc) == MAPI_TRUE)
            {
                GetMSrvSSSound()->SetMuteStatus(MUTE_INPUT_SOURCEL_LOCK, MAPI_TRUE, MSRV_AUDIO_PROCESSOR_SUB);
            }
#endif
            //PIP -> None PIP
            if( (m_enPipMode==E_PIP_MODE_OFF) && (m_enPrePipMode != E_PIP_MODE_OFF) )
            {
                GetMSrvSSSound()->SetAudioSource(MAPI_INPUT_SOURCE_NONE, MSRV_AUDIO_PROCESSOR_SUB);
            }
        }
    }
#else
    mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_SOURCESWITCH_MUTEON_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
#if (INPUT_SOURCE_LOCK_ENABLE == 1)
    //Check input source lock(ISL) status of target source
    if (IsInputSourceLock(eInputSrc) == MAPI_TRUE)
    {
        GetMSrvSSSound()->SetMuteStatus(MUTE_INPUT_SOURCEL_LOCK, MAPI_TRUE, MSRV_AUDIO_PROCESSOR_MAIN);
    }
#endif
#endif
    MSRV_CONTROL_DVB_INFO("%s::: %u\n", __PRETTY_FUNCTION__, __LINE__);

    //<2>.To Disconnect the Player
    MSRV_CONTROL_DVB_INFO("Disconnect old player! \n");
#if (STEREO_3D_ENABLE == 1)
    if(EN_3D_NONE != GetMSrv3DManager()->GetCurrent3DFormat())
    {
        SetVideoMute(TRUE );
    }
    GetMSrv3DManager()->InputChangeHandler();
#endif
    ST_PLAYER_STATE stPlayerState;
    MSrv_PlayerControl::GetInstance()->GetPlayerState(stPlayerState);
    stPlayerState.v_PIPSource.at(eWin) = eInputSrc;

    if (MSrv_PlayerControl::GetInstance()->SwitchInputSource(stPlayerState) == FALSE)
    {
        ASSERT(0);
        return FALSE;
    }

#if (MSTAR_TVOS == 1)
    if((bWriteDB) && (eInputSrc != MAPI_INPUT_SOURCE_STORAGE))
#else
    if(bWriteDB && (eWin == MAPI_MAIN_WINDOW))
#endif
    {
#if (STR_ENABLE == 1)
        if (eInputSrc != MAPI_INPUT_SOURCE_NONE)
#endif
        {
            GetMSrvSystemDatabase()->SetUserSourceType(&eInputSrc);
        }
    }

    MSRV_CONTROL_DVB_INFO("%s::: %u\n", __PRETTY_FUNCTION__, __LINE__);

    //To set Audio, Mute protect in InputSource_ChangeAudioSource kernel
#if (PIP_ENABLE == 1)
    if (eWin == MAPI_MAIN_WINDOW)
    {
        GetMSrvSSSound()->SetMuteStatus(MUTE_SOURCESWITCH, MAPI_FALSE, MSRV_AUDIO_PROCESSOR_MAIN);
#if (INPUT_SOURCE_LOCK_ENABLE == 1)
        //Check input source lock(ISL) status of target source
        if (IsInputSourceLock(eInputSrc) == MAPI_FALSE)
        {
            GetMSrvSSSound()->SetMuteStatus(MUTE_INPUT_SOURCEL_LOCK, MAPI_FALSE, MSRV_AUDIO_PROCESSOR_MAIN);
        }
        else
        {
            GetMSrvPlayer(eInputSrc)->EnableMiddleware(MAPI_FALSE);
#if (MSTAR_TVOS == 1)
            GetMSrvPlayer(eInputSrc)->PostEvent(NULL, EV_INPUT_SOURCE_LOCK, E_INPUT_SOURCE_LOCK_EVENT_ON, E_INPUT_SOURCE_LOCK_EVENT_UNDEFINED);
#else
            PostEvent(NULL, EV_INPUT_SOURCE_LOCK, E_INPUT_SOURCE_LOCK_EVENT_ON, E_INPUT_SOURCE_LOCK_EVENT_UNDEFINED);
#endif
        }
#endif
    }
    else
    {
#if (PREVIEW_MODE_ENABLE == 1)
        if(FALSE == IsPreviewModeRunning())
#endif
        {
            //@ line/hp to sub
            MSAPI_AUDIO_OUTPORT_SOURCE_INFO  pSourceInfo;
            mapi_interface::Get_mapi_audio()->GetOutputSourceInfo(&pSourceInfo);
#if (CVBSOUT_XCTOVE_ENABLE == 1)
            pSourceInfo.MonitorOut = AUDIO_PROCESSOR_MAIN;
#else
            pSourceInfo.MonitorOut = AUDIO_PROCESSOR_SUB;
#endif
            pSourceInfo.HpOut = AUDIO_PROCESSOR_SUB;
            mapi_interface::Get_mapi_audio()->SetOutputSourceInfo(&pSourceInfo);
            GetMSrvSSSound()->SetMuteStatus(MUTE_SOURCESWITCH, MAPI_FALSE, MSRV_AUDIO_PROCESSOR_SUB);
#if (INPUT_SOURCE_LOCK_ENABLE == 1)
            //Check input source lock(ISL) status of target source
            if (IsInputSourceLock(eInputSrc) == MAPI_FALSE)
            {
                GetMSrvSSSound()->SetMuteStatus(MUTE_INPUT_SOURCEL_LOCK, MAPI_FALSE, MSRV_AUDIO_PROCESSOR_SUB);
            }
#endif
        }
    }
#else
    mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_SOURCESWITCH_MUTEOFF_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
#if (INPUT_SOURCE_LOCK_ENABLE == 1)
    //Check input source lock(ISL) status of target source
    if (IsInputSourceLock(eInputSrc) == MAPI_FALSE)
    {
        GetMSrvSSSound()->SetMuteStatus(MUTE_INPUT_SOURCEL_LOCK, MAPI_FALSE, MSRV_AUDIO_PROCESSOR_MAIN);
    }
    else
    {
        GetMSrvPlayer(eInputSrc)->EnableMiddleware(MAPI_FALSE);
#if (MSTAR_TVOS == 1)
        GetMSrvPlayer(eInputSrc)->PostEvent(NULL, EV_INPUT_SOURCE_LOCK, E_INPUT_SOURCE_LOCK_EVENT_ON, E_INPUT_SOURCE_LOCK_EVENT_UNDEFINED);
#else
        PostEvent(NULL, EV_INPUT_SOURCE_LOCK, E_INPUT_SOURCE_LOCK_EVENT_ON, E_INPUT_SOURCE_LOCK_EVENT_UNDEFINED);
#endif
    }
#endif //This end macro is for INPUT_SOURCE_LOCK_ENABLE
#endif

#if (MSTAR_TVOS == 1)
    if(SystemInfo::GetInstance()->GetVolumeCompensationFlag() == TRUE)
    {
        if(eInputSrc == MAPI_INPUT_SOURCE_ATV)
        {
            GetMSrvAtv()->SetChannelVolumeCompensation(MSrv_ATV_Player::E_CURRENT_CHNNEL_VOLUME_COMPENSATION);
        }
    }
#endif
#if (PIP_ENABLE == 1)
    //PIP -> None PIP
    if((eWin == MAPI_SUB_WINDOW) && 
        ((m_enPipMode == E_PIP_MODE_OFF) && (m_enPrePipMode != E_PIP_MODE_OFF)))
    {
        MSRV_CONTROL_DVB_INFO("The Sub volume don't need to set when exit the pip!\n");
    }
    else
#endif
    {
        GetMSrvSSSound()->SetAbsoluteVolume(eWin);
    }

    MSRV_CONTROL_DVB_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);

    if(eWin == MAPI_MAIN_WINDOW)
    {
        MS_USER_SOUND_SETTING stAudioTemp;
        GetMSrvSystemDatabase()->GetAudioSetting(&stAudioTemp);
        GetMSrvSSSound()->SetSPDIFDelay(stAudioTemp.SPDIF_Delay);
        GetMSrvSSSound()->SetSNDSpeakerDelay(stAudioTemp.Speaker_Delay);
    }

    if(eInputSrc == MAPI_INPUT_SOURCE_DTV)
    {
        SwitchMSrvDtvRouteCmd(GetMSrvSystemDatabase()->GetDtvRoute());
    }

#if (AUTO_TEST == 1)
    printf("[AutoTest][SourceChange][SetInputSourceCmd END][%u]\n", mapi_time_utility::GetTime0());
#endif
    m_bSourceChange = FALSE;
    return TRUE;
}


BOOL MSrv_Control_DVB::SwitchMSrvDtvRouteCmd(U8 u8DtvRoute, MAPI_SCALER_WIN eWin)
{
    mapi_scope_lock(scopeLock, &m_MutexInputSrcSwitch);

    if(u8DtvRoute >= MAXROUTECOUNT)
    {
        MSRV_CONTROL_DVB_ERR("m_u8DtvRoute %d > MAXROUTECOUNT %d\n", u8DtvRoute, MAXROUTECOUNT);
        ASSERT(0);
        return FALSE;
    }

    // The same DTV Route
    if (MSrv_PlayerControl::GetInstance()->GetCurrentDtvRoute(eWin) == u8DtvRoute)
    {
        if (eWin == MAPI_MAIN_WINDOW)
        {
            if (MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW) == MAPI_INPUT_SOURCE_DTV)
            {
                return TRUE;
            }
        }
        else if (eWin == MAPI_SUB_WINDOW)
        {
            return TRUE;
        }
        else
        {
            ASSERT(0);
        }
    }

    //Stop BGPVR if necessary.
    //U8 u8RouteUnused = 0;
    //if(WillRecordBeStopped(MAPI_INPUT_SOURCE_DTV, u8DtvRoute, u8RouteUnused))
    //{
    //    StopRecordRoute();
    //}

    //set to ture means now doing switch DTV Route action
    m_bSourceChange = TRUE;

    // Audio should be mute before source change
#if (PIP_ENABLE == 1)
    if (eWin == MAPI_MAIN_WINDOW)
    {
        GetMSrvSSSound()->SetMuteStatus(MUTE_SOURCESWITCH, MAPI_TRUE, MSRV_AUDIO_PROCESSOR_MAIN);
    }
    else if (eWin == MAPI_SUB_WINDOW)
    {
#if (PREVIEW_MODE_ENABLE == 1)
        if(FALSE == IsPreviewModeRunning())
#endif
        {
            //@ swith all to main
            MSAPI_AUDIO_OUTPORT_SOURCE_INFO  pSourceInfo;
            mapi_interface::Get_mapi_audio()->GetOutputSourceInfo(&pSourceInfo);
#if (CVBSOUT_XCTOVE_ENABLE == 1)
            pSourceInfo.MonitorOut = AUDIO_PROCESSOR_MAIN;
#else
            pSourceInfo.MonitorOut = AUDIO_PROCESSOR_SUB;
#endif
            pSourceInfo.HpOut = AUDIO_PROCESSOR_SUB;
            mapi_interface::Get_mapi_audio()->SetOutputSourceInfo(&pSourceInfo);
            GetMSrvSSSound()->SetMuteStatus(MUTE_SOURCESWITCH, MAPI_TRUE, MSRV_AUDIO_PROCESSOR_SUB);
            //PIP -> None PIP
            if ((m_enPipMode == E_PIP_MODE_OFF) && (m_enPrePipMode != E_PIP_MODE_OFF))
            {
                GetMSrvSSSound()->SetAudioSource(MAPI_INPUT_SOURCE_NONE, MSRV_AUDIO_PROCESSOR_SUB);
            }
        }
    }
#else
    mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_SOURCESWITCH_MUTEON_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
#endif

    MSRV_CONTROL_DVB_INFO("%s : m_u8DtvRoute %d -> u8DtvRoute %d\n", __PRETTY_FUNCTION__, this->GetCurrentDtvRoute(), u8DtvRoute);

#if (CEC_ENABLE == 1)
    if (eWin == MAPI_MAIN_WINDOW)
    {
        ST_INPUTSOURCE stInputSrc;
        stInputSrc.CurrInputSrc = MAPI_INPUT_SOURCE_DTV;
        stInputSrc.OriInputSrc = MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW);
        if (stInputSrc.OriInputSrc != MAPI_INPUT_SOURCE_DTV)
        {
            if (m_bCECThreadCreated == TRUE)
            {
                pthread_join(m_InitCECThread, NULL);
                m_bCECThreadCreated = FALSE;
            }
            if (PTH_RET_CHK(pthread_create(&m_InitCECThread, NULL, MSrv_Control_common::InitCECThread, (void *)&stInputSrc)) != 0)
            {
                ASSERT(0);
            }
            m_bCECThreadCreated = TRUE;
        }
    }
#endif

    ST_PLAYER_STATE stPlayerState;
    MSrv_PlayerControl::GetInstance()->GetPlayerState(stPlayerState);

    //Set audio source type for support single dtv and multi dtv
    if (eWin == MAPI_MAIN_WINDOW)
    {
        stPlayerState.v_PIPSource[eWin] = MAPI_INPUT_SOURCE_DTV;
    }
    else if (eWin == MAPI_SUB_WINDOW)
    {
        stPlayerState.v_PIPSource[eWin] = MAPI_INPUT_SOURCE_DTV2;
    }
    stPlayerState.v_PIPRoute[eWin] = u8DtvRoute;

    if (TRUE == MSrv_PlayerControl::GetInstance()->SwitchInputSource(stPlayerState))
    {
        if (eWin == MAPI_MAIN_WINDOW)
        {
            GetMSrvSystemDatabase()->SetUserSourceType(&stPlayerState.v_PIPSource[MAPI_MAIN_WINDOW]);
            _SaveDtvRouteInDb(u8DtvRoute);
        }

        //To set Audio, Mute protect in InputSource_ChangeAudioSource kernel
#if (PIP_ENABLE == 1)
        if (eWin == MAPI_MAIN_WINDOW)
        {
            GetMSrvSSSound()->SetMuteStatus(MUTE_SOURCESWITCH, MAPI_FALSE, MSRV_AUDIO_PROCESSOR_MAIN);
        }
        else if (eWin == MAPI_SUB_WINDOW)
        {
#if (PREVIEW_MODE_ENABLE == 1)
            if(FALSE == IsPreviewModeRunning())
#endif
            {
                //@ line/hp to sub
                MSAPI_AUDIO_OUTPORT_SOURCE_INFO  pSourceInfo;
                mapi_interface::Get_mapi_audio()->GetOutputSourceInfo(&pSourceInfo);
#if (CVBSOUT_XCTOVE_ENABLE == 1)
                pSourceInfo.MonitorOut = AUDIO_PROCESSOR_MAIN;
#else
                pSourceInfo.MonitorOut = AUDIO_PROCESSOR_SUB;
#endif
                pSourceInfo.HpOut = AUDIO_PROCESSOR_SUB;
                mapi_interface::Get_mapi_audio()->SetOutputSourceInfo(&pSourceInfo);
                GetMSrvSSSound()->SetMuteStatus(MUTE_SOURCESWITCH, MAPI_FALSE, MSRV_AUDIO_PROCESSOR_SUB);
            }
        }
#else
        mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_SOURCESWITCH_MUTEOFF_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
#endif
#if (PIP_ENABLE == 1)
    //PIP -> None PIP
    if((eWin == MAPI_SUB_WINDOW) && 
        (m_enPipMode == E_PIP_MODE_OFF) && (m_enPrePipMode != E_PIP_MODE_OFF))
    {
        MSRV_CONTROL_DVB_INFO("The Sub volume don't need to set when exit the pip!\n");
    }
    else
#endif
    {
        GetMSrvSSSound()->SetAbsoluteVolume(eWin);
    }

        
        MSRV_CONTROL_DVB_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);

        MS_USER_SOUND_SETTING stAudioTemp;
        GetMSrvSystemDatabase()->GetAudioSetting(&stAudioTemp);
        GetMSrvSSSound()->SetSPDIFDelay(stAudioTemp.SPDIF_Delay);
        GetMSrvSSSound()->SetSNDSpeakerDelay(stAudioTemp.Speaker_Delay);

        MSrv_DTV_Player_DVB *p = dynamic_cast<MSrv_DTV_Player_DVB *>(GetMSrvDtv(eWin));
        ASSERT(p);
        p->PlayCurrentProgram();
    }

    m_bSourceChange = FALSE;

    return TRUE;

}

MSrv_SCART_Player * MSrv_Control_DVB::GetMSrvScart(void)
{
    MSRV_CONTROL_DVB_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
    MSrv_SCART_Player *p = dynamic_cast<MSrv_SCART_Player *>(MSrv_PlayerControl::GetInstance()->GetMSrvPlayer(E_MSRV_SCART_PLAYER));
    ASSERT(p);
    return p;
}
#if (CI_ENABLE == 1)
MSrv_CIMMI * MSrv_Control_DVB::GetMSrvCIMMI(MAPI_U8 u8CiSlot)
{
    MSRV_CONTROL_DVB_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
    MSrv_CIMMI *p;
    p = dynamic_cast<MSrv_CIMMI *>(m_pMSrvList[E_MSRV_CIMMI]);
    ASSERT(p);
    if(TRUE == p->IsConnected(u8CiSlot))
        return p;
    p = dynamic_cast<MSrv_CIMMI *>(m_pMSrvList[E_MSRV_CIMMI_EX]);
    ASSERT(p);
    if(TRUE == p->IsConnected(u8CiSlot))
    return p;
    ASSERT(0);
    return NULL;
}
#endif
#if(CA_ENABLE == 1)//colin@2012-0302
MSrv_CA * MSrv_Control_DVB::GetMSrvCA(void)
{
    MSRV_CONTROL_DVB_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
    MSrv_CA *p = dynamic_cast<MSrv_CA *>(m_pMSrvList[E_MSRV_CA]);
    ASSERT(p);
    return p;
}
#endif


#if (HDMITX_ENABLE == 1)
void* MSrv_Control_DVB::HPResMonitorThread(void *arg)
{
    prctl(PR_SET_NAME, (unsigned long)__FUNCTION__);
    U32 u32Res, u32PreRes;
    const char* pResolution;
    IEnvManager* pEnvMan = IEnvManager::Instance();
    EN_MAPI_HDMITX_EDID_4K2K_VIC enHDMITx_4K2K[8];
    mapi_gpio *gptr = mapi_gpio::GetGPIO_Dev(ROCKET_HDMI_PLUG);
    BOOL bIs4KTV, bCurState, bPreState, bEnMonitor;
    MSrv_Control_DVB* pHandler=(MSrv_Control_DVB*)arg;

    /* Enable after complete AN part */
    bEnMonitor = TRUE;
    sleep(10);
    ASSERT(pHandler);
    bCurState = gptr->GetOnOff();
    /* Let it go into it for the boot time case */
    bPreState = ~bCurState;
    u32PreRes = E_MAPI_ROCKY_RES_1920x1080p_60Hz;

    while(bEnMonitor)
    {
        if(bCurState != bPreState)
        {
            /* 1. Get Resolution info */
            pResolution = pEnvMan->GetEnv("resolution");
            ASSERT(pResolution);
            u32Res = atoi(pResolution);
            /* Plugged IN */
            if(bCurState == TRUE)
            {
                bIs4KTV = FALSE;

                /* Query TV set 4k2k capability */
                sleep(2);
                memset(enHDMITx_4K2K, (EN_MAPI_HDMITX_EDID_4K2K_VIC)0, sizeof(EN_MAPI_HDMITX_EDID_4K2K_VIC)*8);
                mapi_interface::Get_mapi_hdmitx()->GetEDID_4K2K_Inform(8, enHDMITx_4K2K);
                for(int i = 0; i < 8; i++)
                {
                    if((enHDMITx_4K2K[i]==E_HMAPI_HDMITX_EDID_4K2K_30Hz) || (enHDMITx_4K2K[i]== E_HMAPI_HDMITX_EDID_4K2K_25Hz))
                    {
                        bIs4KTV = TRUE;
                        break;
                    }
                }

                /* Enable or disable 4K2K resolution UI */
                if(bIs4KTV == TRUE)
                {
                    pHandler->PostEvent(0, EV_SET_SYS_PROPERTY_4K2K_ENABLE, (U32)"4k2kEnableUI");
                }
                else
                {
                    pHandler->PostEvent(0, EV_SET_SYS_PROPERTY_4K2K_ENABLE, (U32)"4k2kDisableUI");
                }

                /* For the booting up case */
                if((u32Res == E_MAPI_ROCKY_RES_4K2Kp_60Hz)
                    || (u32Res == E_MAPI_ROCKY_RES_4K2Kp_50Hz)
                    || (u32Res == E_MAPI_ROCKY_RES_4K2Kp_30Hz)
                    || (u32Res == E_MAPI_ROCKY_RES_4K2Kp_25Hz))
                {
                    if( bIs4KTV == FALSE)
                    {
                        printf("resolution is 4k2k, but Not 4K2K TV, switching to 1080P@60Hz, post event to AN\n");
                        pHandler->PostEvent(0, EV_SET_SYS_PROPERTY_RESOLUTION_STATE, (U32)"RESOLUTION_1080P");
                        MSrv_Control::GetMSrvVideo()->SetResolution(mapi_display_datatype::DISPLAY_DACOUT_1080P_60);
                        u32PreRes = E_MAPI_ROCKY_RES_1920x1080p_60Hz;
                        sleep(4);
                    }
                }/* Last resolution setting is 4k2k */
                else if((u32PreRes == E_MAPI_ROCKY_RES_4K2Kp_60Hz)
                    || (u32PreRes == E_MAPI_ROCKY_RES_4K2Kp_50Hz)
                    || (u32PreRes == E_MAPI_ROCKY_RES_4K2Kp_30Hz)
                    || (u32PreRes == E_MAPI_ROCKY_RES_4K2Kp_25Hz))
                {
                    if(bIs4KTV == TRUE)
                    {
                        switch (u32PreRes)
                        {
                            case E_MAPI_ROCKY_RES_4K2Kp_60Hz:
                                pHandler->PostEvent(0, EV_SET_SYS_PROPERTY_RESOLUTION_STATE, (U32)"RESOLUTION_4K2K_60");
                                MSrv_Control::GetMSrvVideo()->SetResolution(mapi_display_datatype::DISPLAY_4K2K_60P);
                                printf("Previous resolution is RESOLUTION_4K2K_60, switching back to it\n");
                                break;
                            case E_MAPI_ROCKY_RES_4K2Kp_50Hz:
                                pHandler->PostEvent(0, EV_SET_SYS_PROPERTY_RESOLUTION_STATE, (U32)"RESOLUTION_4K2K_50");
                                MSrv_Control::GetMSrvVideo()->SetResolution(mapi_display_datatype::DISPLAY_4K2K_50P);
                                printf("Previous resolution is RESOLUTION_4K2K_50, switching back to it\n");
                                break;
                            case E_MAPI_ROCKY_RES_4K2Kp_30Hz:
                                pHandler->PostEvent(0, EV_SET_SYS_PROPERTY_RESOLUTION_STATE, (U32)"RESOLUTION_4K2K_30");
                                MSrv_Control::GetMSrvVideo()->SetResolution(mapi_display_datatype::DISPLAY_4K2K_30P);
                                printf("Previous resolution is RESOLUTION_4K2K_30, switching back to it\n");
                                break;
                            case E_MAPI_ROCKY_RES_4K2Kp_25Hz:
                                pHandler->PostEvent(0, EV_SET_SYS_PROPERTY_RESOLUTION_STATE, (U32)"RESOLUTION_4K2K_25");
                                MSrv_Control::GetMSrvVideo()->SetResolution(mapi_display_datatype::DISPLAY_4K2K_25P);
                                printf("Previous resolution is RESOLUTION_4K2K_25, switching back to it\n");
                                break;
                            default:
                                /* impossible case */
                                break;
                        }
                        sleep(4);
                    }
                }
            }
            else /* Plugged Out */
            {
                u32PreRes = u32Res;
                if((u32Res == E_MAPI_ROCKY_RES_4K2Kp_60Hz)
                    || (u32Res == E_MAPI_ROCKY_RES_4K2Kp_50Hz)
                    || (u32Res == E_MAPI_ROCKY_RES_4K2Kp_30Hz)
                    || (u32Res == E_MAPI_ROCKY_RES_4K2Kp_25Hz))
                {
                    printf("Plugged Out, switching resolution to 1080P@60Hz, post event to AN\n");
                    pHandler->PostEvent(0, EV_SET_SYS_PROPERTY_RESOLUTION_STATE, (U32)"RESOLUTION_1080P");
                    MSrv_Control::GetMSrvVideo()->SetResolution(mapi_display_datatype::DISPLAY_DACOUT_1080P_60);
                    sleep(4);
                }
                pHandler->PostEvent(0, EV_SET_SYS_PROPERTY_4K2K_ENABLE, (U32)"4k2kDisableUI");
            }

        }

        bPreState = bCurState;

        sleep(2);

        bCurState = gptr->GetOnOff();
    }

    pthread_exit(NULL);
}
#endif

void* MSrv_Control_DVB::InitSSoundthread(void *arg)
{

    MSrv_Control_DVB* pHandler=(MSrv_Control_DVB*)arg;
    ASSERT(pHandler);
    while(((FALSE == pHandler->m_bIsSysDBInitDone) || (m_pMSrvList[E_MSRV_SSSOUND]==NULL)))
    {
        usleep(15000); //15ms
    }



    MS_USER_SOUND_SETTING stSoundSetting;
    MS_USER_SYSTEM_SETTING stSysSetting;

    prctl(PR_SET_NAME, (unsigned long)"InitSSoundthread");
#if (STR_ENABLE == 1)
    mapi_str::AutoRegister _R;
#endif

    //Initial audio DSP
    MSrv_Control_common::SetGpioDeviceStatus(Audio_Amplifier, FALSE);
    MSrv_SSSound *pSSSound = GetMSrvSSSound();
    pSSSound->Initialize();
    MSrv_Control_common::SetGpioDeviceStatus(Audio_Amplifier, TRUE);

    GetMSrvSystemDatabase()->GetSoundSetting(&stSoundSetting);
    GetMSrvSystemDatabase()->GetUserSystemSetting(&stSysSetting);
    mapi_interface::Get_mapi_audio()->SND_SetBalance(stSoundSetting.Balance);
    // add AVL function
    mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_MOMENT_MUTEON_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
    mapi_interface::Get_mapi_audio()->SND_EnableAutoVolume((BOOLEAN)stSoundSetting.bEnableAVC);
    GetMSrvSSSound()->SetAbsoluteVolume(MAPI_MAIN_WINDOW);
    mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_MOMENT_MUTEOFF_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
    mapi_interface::Get_mapi_audio()->SPDIF_UI_SetMode(stSysSetting.enSPDIFMODE);
    mapi_interface::Get_mapi_audio()->DECODER_SetAC3Info(Audio_AC3_infoType_DrcMode_, (BOOLEAN)stSoundSetting.bEnableDRC, 0);

    const AudioPath_t* const p_AudioPath = SystemInfo::GetInstance()->GetAudioPathInfo();
    MS_USER_SOUND_SETTING stAudioSetting;
    GetMSrvSystemDatabase()->GetAudioSetting(&stAudioSetting);
    mapi_interface::Get_mapi_audio()->SetAbsoluteVolume(p_AudioPath[MAPI_AUDIO_PATH_MAIN_SPEAKER].u32Path, stAudioSetting.Volume, 0);   // AUDIO_T3_PATH_AUOUT0
    mapi_interface::Get_mapi_audio()->DECODER_SetADAbsoluteVolume(stAudioSetting.ADVolume);

    pSSSound->SetSurroundSound( (SOUND_SURROUND_MODE)stAudioSetting.Surround);

#if (MSTAR_TVOS == 1)
    BSND_PARAMETER stSndParameter;
    memset(&stSndParameter, 0, sizeof(BSND_PARAMETER));
    stSndParameter.BSND_PARAM_EQ_BAND_NUM = 5;
    stSndParameter.BSND_PARAM_EQ[0].BSND_PARAM_EQ_LEVEL = stAudioSetting.astSoundModeSetting[int(stAudioSetting.SoundMode)].EqBand1;
    stSndParameter.BSND_PARAM_EQ[1].BSND_PARAM_EQ_LEVEL = stAudioSetting.astSoundModeSetting[int(stAudioSetting.SoundMode)].EqBand2;
    stSndParameter.BSND_PARAM_EQ[2].BSND_PARAM_EQ_LEVEL = stAudioSetting.astSoundModeSetting[int(stAudioSetting.SoundMode)].EqBand3;
    stSndParameter.BSND_PARAM_EQ[3].BSND_PARAM_EQ_LEVEL = stAudioSetting.astSoundModeSetting[int(stAudioSetting.SoundMode)].EqBand4;
    stSndParameter.BSND_PARAM_EQ[4].BSND_PARAM_EQ_LEVEL = stAudioSetting.astSoundModeSetting[int(stAudioSetting.SoundMode)].EqBand5;
    stSndParameter.BSND_PARAM_BASS = stAudioSetting.astSoundModeSetting[int(stAudioSetting.SoundMode)].Bass;
    stSndParameter.BSND_PARAM_TREBLE = stAudioSetting.astSoundModeSetting[int(stAudioSetting.SoundMode)].Treble;
    pSSSound->SetBasicSoundEffect((BSOUND_EFFECT_TYPE)BSND_EQ, &stSndParameter);
    pSSSound->SetBasicSoundEffect((BSOUND_EFFECT_TYPE)BSND_TREBLE, &stSndParameter);
    pSSSound->SetBasicSoundEffect((BSOUND_EFFECT_TYPE)BSND_BASS, &stSndParameter);
#endif

    pthread_exit(NULL);
}

BOOL MSrv_Control_DVB::createAllMSrvClass(void)
{
    MSRV_CONTROL_DVB_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);

    m_pMSrvList[E_MSRV_SSSOUND] = new (std::nothrow) MSrv_SSSound_Customer;
    MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_SSSOUND]);

    // new system db
    MSrv_System_Database_DVB *pSysDB;
    pSysDB = dynamic_cast<MSrv_System_Database_DVB *>(m_pMSrvList[E_MSRV_SYSTEM_DATABASE]);
    ASSERT(pSysDB);

    BOOL bDBExist;
    bDBExist = pSysDB->IsDatabaseExist(EN_DB_TYPE_SYSTEM);

    pSysDB->Init();

    //restore input source and count to default
    if(bDBExist == MAPI_FALSE)
    {
        MS_USER_SYSTEM_SETTING m_stSysSetting;

        GetMSrvSystemDatabase()->GetUserSystemSetting(&m_stSysSetting);

        const MAPI_VIDEO_INPUTSRCTABLE *pSrcTable;
        pSrcTable = GetSourceList();

        if(pSrcTable[MAPI_INPUT_SOURCE_DTV].u32EnablePort)
        {
            m_stSysSetting.enInputSourceType = MAPI_INPUT_SOURCE_DTV;//FIXME;;We should read the default from system config file
        }
        else  if(pSrcTable[MAPI_INPUT_SOURCE_ATV].u32EnablePort)
        {
            m_stSysSetting.enInputSourceType = MAPI_INPUT_SOURCE_ATV;//FIXME;;We should read the default from system config file
        }
        else
        {
            m_stSysSetting.enInputSourceType = MAPI_INPUT_SOURCE_NONE;//FIXME;;We should read the default from system config file
        }

        if(IsSupportTheDTVSystemType(ATSC_ENABLE))
        {
            m_stSysSetting.Country = E_US;
        }
        else
        {
            m_stSysSetting.Country = E_UK;
        }

        GetMSrvSystemDatabase()->SetUserSystemSetting(&m_stSysSetting);

        #if (SKYPE_ENABLE == 1)
        #if (MSTAR_IPC == 1)
        int ret=0;

        ret = APM_UnregisterAutoStartApp("Skype");
        if(0!=ret)
        {
            printf("APM_UnregisterAutoStartApp fail\n");
        }
        #endif
        SystemCmd("/config/apps_file/skype/skype_reset_default.sh");
        #endif
    }

    MS_USER_SOUND_SETTING stSoundSetting;

    pSysDB->GetSoundSetting(&stSoundSetting);

    for(U32 i=0 ; i<MAPI_INPUT_SOURCE_NUM ; i++)
    {
        GetMSrvSSSound()->SetPreScaleTable((MAPI_INPUT_SOURCE_TYPE)i, stSoundSetting.SpeakerPreScale[i]);
    }

    m_bIsSysDBInitDone = TRUE;

#if (ENABLE_DIVXDRM == 1)
    /* new MSrv_DivX_DRM */
    m_pMSrvList[E_MSRV_DIVX_DRM] = new (std::nothrow) MSrv_DivX_DRM();
    MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_DIVX_DRM]);
#endif

#if (SECURE_ENABLE == 1)
    m_pMSrvList[E_MSRV_PRESHAREDKEY] = new (std::nothrow) MSrv_PreSharedKey();
    MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_PRESHAREDKEY]);
#endif
#if (CI_ENABLE == 1)
    m_pMSrvList[E_MSRV_CIMMI] = new (std::nothrow) MSrv_CIMMI_Customer;
    MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_CIMMI]);

    ASSERT(dynamic_cast<MSrv_CIMMI *>(m_pMSrvList[E_MSRV_CIMMI]));
    dynamic_cast<MSrv_CIMMI *>(m_pMSrvList[E_MSRV_CIMMI])->Init(0);
    if(SystemInfo::GetInstance()->GetCISlotCount()>1)//dynamic for dual CI, define in board
    {
        m_pMSrvList[E_MSRV_CIMMI_EX] = new (std::nothrow) MSrv_CIMMI_Customer;
        MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_CIMMI_EX]);

        ASSERT(dynamic_cast<MSrv_CIMMI *>(m_pMSrvList[E_MSRV_CIMMI_EX]));
        dynamic_cast<MSrv_CIMMI *>(m_pMSrvList[E_MSRV_CIMMI_EX])->Init(1);
    }
    else
    {
        m_pMSrvList[E_MSRV_CIMMI_EX] = NULL;
    }
#endif
#if (CA_ENABLE == 1)
    m_pMSrvList[E_MSRV_CA] = MSrv_CA::GetInstance();
    MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_CA]);
#endif

    MSrv_PlayerControl::GetInstance()->CreateAllMSrvPlayers();

    m_pMSrvList[E_MSRV_ATV_DATABASE] = new (std::nothrow) MSrv_ATV_Database_Customer;
    MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_ATV_DATABASE]);

#if 1 //Read data from storage before channel manager srv init!
    if(m_pMSrvList[E_MSRV_ATV_DATABASE] != NULL)
    {
        MSrv_ATV_Database *p = dynamic_cast<MSrv_ATV_Database *>(m_pMSrvList[E_MSRV_ATV_DATABASE]);
        ASSERT(p);
        p->Init();
    }
#endif

#if (CEC_ENABLE == 1)
    m_pMSrvList[E_MSRV_CEC] = MSrv_CEC_Customer::GetInstance();
    MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_CEC]);
#endif

#if (ENABLE_LITE_SN == 0)
    m_pMSrvList[E_MSRV_ADVERT_PLAYER] = new (std::nothrow) MSrv_Advert_Player;
    MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_ADVERT_PLAYER]);
#endif
#if (ENABLE_BACKEND == 1)
    m_pMSrvList[E_MSRV_BACKEND] = new (std::nothrow) MSrv_Backend;
    MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_BACKEND]);
#endif

  #if (MHL_ENABLE == 1)
    m_pMSrvList[E_MSRV_MHL] = new (std::nothrow) MSrv_MHL;
    MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_MHL]);
  #endif //MHL_ENABLE

#if (HDMITX_ENABLE == 1)
    m_pMSrvList[E_MSRV_HDMITX] = new (std::nothrow) MSrv_HDMITX;
    MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_HDMITX]);
#endif //HDMITX_ENABLE

#if (PVR_ENABLE == 1)
    if(m_pMSrvList[E_MSRV_PVRBROWSER] == NULL)
    {
        m_pMSrvList[E_MSRV_PVRBROWSER] = new (std::nothrow) MW_PVRFileSystem;
        MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_PVRBROWSER]);
    }
#endif

#if (SECURE_ENABLE == 1)
    m_pMSrvList[E_MSRV_ISYSTEM] = new (std::nothrow) MW_ISystem;
    MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_ISYSTEM]);

    m_pMSrvList[E_MSRV_OEMCRYPTO] = new (std::nothrow) MW_Oemcrypto;
    MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_OEMCRYPTO]);

    m_pMSrvList[E_MSRV_VUDUDRM] = new (std::nothrow) MW_vuduDRM;
    MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_VUDUDRM]);

    m_pMSrvList[E_MSRV_SECURESYS] = new (std::nothrow) MW_SecureSys;
    MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_SECURESYS]);

    m_pMSrvList[E_MSRV_SECURE_STORAGE] = new (std::nothrow) MW_Secure_Storage;
    MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_SECURE_STORAGE]);

    m_pMSrvList[E_MSRV_SECURE] = new (std::nothrow) MW_Secure;
    MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_SECURE]);
#endif

    m_pMSrvList[E_MSRV_NETWORK] = MSrv_Network_Control::GetInstance();
    MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_NETWORK]);

    m_pMSrvList[E_MSRV_PICTURE] = new (std::nothrow) MSrv_Picture_Customer;
    MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_PICTURE]);

    m_pMSrvList[E_MSRV_VIDEO] = new (std::nothrow) MSrv_Video;
    MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_VIDEO]);

#if (ENABLE_LITE_SN == 0)
    m_pMSrvList[E_MSRV_BACKLIGHT_CONTROL] = new (std::nothrow) MSrv_BacklightControl;
    MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_BACKLIGHT_CONTROL]);
#endif

    m_pMSrvList[E_MSRV_FACTORY] = new (std::nothrow) MSrv_Factory_Mode_Customer;
    MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_FACTORY]);

    m_pMSrvList[E_MSRV_TIMER] = new (std::nothrow) MSrv_Timer_Customer;
    MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_TIMER]);

#if (STEREO_3D_ENABLE == 1)
    m_pMSrvList[E_MSRV_3DMANAGER] = new MSrv_3DManager_Customer;
    MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_3DMANAGER]);
#endif

#if ((AUTO_TEST == 1) && (MSTAR_TVOS == 0))
    AT_CmdManager_MSrv::GetInstance();
#endif

#if (ENABLE_NETREADY == 1)
    m_pMSrvList[E_MSRV_DEVICE_AGENT] = MSrv_DeviceAgent::GetDAInstance();
    MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_DEVICE_AGENT]);
#endif

#if (ENABLE_LITE_SN == 0)
    m_pMSrvList[E_MSRV_SRC_DET] = new (std::nothrow) MSrv_SrcDetect;
    MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_SRC_DET]);
#endif

    // new channel manager
    const MAPI_VIDEO_INPUTSRCTABLE *pSrcTable;
    pSrcTable = GetSourceList();
    if(pSrcTable[MAPI_INPUT_SOURCE_DTV].u32EnablePort)
    {
#if (ISDB_SYSTEM_ENABLE == 1)
        MSrv_ChannelManager_ISDB * pCM;
        pCM = new (std::nothrow) MSrv_ChannelManager_ISDB_Customer;
#else
        MSrv_ChannelManager_DVB * pCM;
        pCM = new (std::nothrow) MSrv_ChannelManager_DVB_Customer;
#endif
        ASSERT(pCM);
        m_pMSrvList[E_MSRV_CHANNEL_MANAGER] = pCM;
        MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_CHANNEL_MANAGER]);
    }

    m_pMSrvList[E_MSRV_RECORDINGSCHEDULER] = new (std::nothrow) MSrv_RecordingScheduler;
    MSRV_CONTROL_ALLOC_CHK(m_pMSrvList[E_MSRV_RECORDINGSCHEDULER]);

    MSRV_CONTROL_DVB_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
    return TRUE;

CREATE_ERR_EXIT:
    for(int i = (E_MSRV_MAX - 1); i >= 0; i--)
    {
        if(m_pMSrvList[i] != NULL)
        {
            delete m_pMSrvList[i];
            m_pMSrvList[i] = NULL;
        }
    }
    MSRV_CONTROL_DVB_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
    return FALSE;
}

MSrv_Player* MSrv_Control_DVB::GetMSrvPlayer(MAPI_INPUT_SOURCE_TYPE eMapiSrcType)
{
    MSRV_CONTROL_DVB_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
    switch(eMapiSrcType)
    {
        case MAPI_INPUT_SOURCE_VGA:
        case MAPI_INPUT_SOURCE_VGA2:
        case MAPI_INPUT_SOURCE_VGA3:
            MSRV_CONTROL_DVB_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
            return GetMSrvVga();
        case MAPI_INPUT_SOURCE_ATV:
            MSRV_CONTROL_DVB_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
            return GetMSrvAtv();
        case MAPI_INPUT_SOURCE_CVBS:
        case MAPI_INPUT_SOURCE_CVBS2:
        case MAPI_INPUT_SOURCE_CVBS3:
        case MAPI_INPUT_SOURCE_CVBS4:
        case MAPI_INPUT_SOURCE_CVBS5:
        case MAPI_INPUT_SOURCE_CVBS6:
        case MAPI_INPUT_SOURCE_CVBS7:
        case MAPI_INPUT_SOURCE_CVBS8:
            MSRV_CONTROL_DVB_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
            return GetMSrvAv();
        case MAPI_INPUT_SOURCE_SVIDEO:
        case MAPI_INPUT_SOURCE_SVIDEO2:
        case MAPI_INPUT_SOURCE_SVIDEO3:
        case MAPI_INPUT_SOURCE_SVIDEO4:
            MSRV_CONTROL_DVB_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
            return GetMSrvSv();
        case MAPI_INPUT_SOURCE_YPBPR:
        case MAPI_INPUT_SOURCE_YPBPR2:
        case MAPI_INPUT_SOURCE_YPBPR3:
            MSRV_CONTROL_DVB_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
            return GetMSrvComp();
        case MAPI_INPUT_SOURCE_SCART:
        case MAPI_INPUT_SOURCE_SCART2:
            MSRV_CONTROL_DVB_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
            return GetMSrvScart();
        case MAPI_INPUT_SOURCE_HDMI:
        case MAPI_INPUT_SOURCE_HDMI2:
        case MAPI_INPUT_SOURCE_HDMI3:
        case MAPI_INPUT_SOURCE_HDMI4:
            MSRV_CONTROL_DVB_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
            return GetMSrvHdmi();
        case MAPI_INPUT_SOURCE_DVI:
        case MAPI_INPUT_SOURCE_DVI2:
        case MAPI_INPUT_SOURCE_DVI3:
        case MAPI_INPUT_SOURCE_DVI4:
            MSRV_CONTROL_DVB_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
            MSRV_CONTROL_DVB_INFO("Get DVI Player, return NULL!!\n");
            return NULL;
        case MAPI_INPUT_SOURCE_STORAGE:
        //case MAPI_INPUT_SOURCE_STORAGE2:
        case MAPI_INPUT_SOURCE_KTV:
        case MAPI_INPUT_SOURCE_JPEG:
            MSRV_CONTROL_DVB_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
            return GetMSrvStorage(MAPI_MAIN_WINDOW);
        case MAPI_INPUT_SOURCE_STORAGE2:
            return GetMSrvStorage(MAPI_SUB_WINDOW);;
        case MAPI_INPUT_SOURCE_DTV:
            MSRV_CONTROL_DVB_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
            return GetMSrvDtv(MAPI_MAIN_WINDOW);
        case MAPI_INPUT_SOURCE_DTV2:
            MSRV_CONTROL_DVB_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
            return GetMSrvDtv(MAPI_SUB_WINDOW);
        default:
            MSRV_CONTROL_DVB_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
            MSRV_CONTROL_DVB_INFO("Get NULL Player \n");
            return NULL;
    }
}

MSrv_DTV_Player_DVB* MSrv_Control_DVB::GetFocusMSrvDtv(void)
{
    MSrv_DTV_Player_DVB* pDvbPlayer = NULL;
    //Focus on sub window
    if(TRUE == this->IsFocusOnSubSource())
    {
        pDvbPlayer = dynamic_cast<MSrv_DTV_Player_DVB*>(this->GetMSrvDtv(MAPI_SUB_WINDOW));
    }
    else
    {
        pDvbPlayer = dynamic_cast<MSrv_DTV_Player_DVB*>(this->GetMSrvDtv(MAPI_MAIN_WINDOW));
    }
    ASSERT(pDvbPlayer);
    return pDvbPlayer;
}

BOOL MSrv_Control_DVB::ResetToFactoryDefault(void)
{
#if (CI_PLUS_ENABLE == 1)
    MAPI_INPUT_SOURCE_TYPE enCurrentInputType;
    enCurrentInputType = MSrv_Control::GetInstance()->GetCurrentInputSource();
    if (MAPI_INPUT_SOURCE_DTV == enCurrentInputType)
    {
        MSrv_DTV_Player_DVB *pMsrvDtv = dynamic_cast<MSrv_DTV_Player_DVB *>(GetMSrvDtv());
        if (pMsrvDtv != NULL)
        {
            BOOL bisCiOccupiedTuner = FALSE;
            bisCiOccupiedTuner = pMsrvDtv->IsCiOccupiedTuner(TRUE);
            if (TRUE == bisCiOccupiedTuner)
            {
                MSRV_CONTROL_DVB_INFO("Block ResetToFactoryDefault \n");
                return FALSE;
            }
        }
    }
#endif
    const MAPI_VIDEO_INPUTSRCTABLE *pSrcTable;
    pSrcTable = GetSourceList();
    //pSrcTable = GetSourceList();

    if(pSrcTable[MAPI_INPUT_SOURCE_DTV].u32EnablePort && pSrcTable[MAPI_INPUT_SOURCE_ATV].u32EnablePort)
    {
        ST_MEDIUM_SETTING MediumSetting;
        GetMSrvSystemDatabase()->GetMediumSetting(&MediumSetting);
#if (ISDB_SYSTEM_ENABLE == 1)

        MSrv_ATV_Database *pAtvDb = dynamic_cast<MSrv_ATV_Database *>(GetMSrvAtvDatabase());

        // clear air atv
        MediumSetting.AntennaType = E_ANTENNA_TYPE_AIR;
        GetMSrvSystemDatabase()->SetMediumSetting(&MediumSetting);
        pAtvDb->ConnectDatabase(MSrv_ATV_Database::DVBT_DB);
        GetMSrvAtvDatabase()->CommondCmd(RESET_ATV_DATA_MANAGER , 0, 0, NULL);

        // clear cable atv
        MediumSetting.AntennaType = E_ANTENNA_TYPE_CABLE;
        GetMSrvSystemDatabase()->SetMediumSetting(&MediumSetting);
        pAtvDb->ConnectDatabase(MSrv_ATV_Database::DVBC_DB);
        GetMSrvAtvDatabase()->CommondCmd(RESET_ATV_DATA_MANAGER , 0, 0, NULL);
#else
        //SetInputSource(MAPI_INPUT_SOURCE_ATV);
        MediumSetting.AntennaType = E_ANTENNA_TYPE_CABLE;
        GetMSrvSystemDatabase()->SetMediumSetting(&MediumSetting);
        GetMSrvAtvDatabase()->CommondCmd(RESET_ATV_DATA_MANAGER , 0 , 0 , NULL);
#endif
#if 0
        SetInputSource(MAPI_INPUT_SOURCE_ATV);
        GetMSrvAtvDatabase()->CommondCmd(RESET_ATV_DATA_MANAGER , NULL , NULL , NULL);
        SetInputSource(MAPI_INPUT_SOURCE_DTV);
#endif

#if (DVBS_SYSTEM_ENABLE==1)
        if(IsSupportTheDTVSystemType(DVBS2_ENABLE))
        {
            U8 u8DvbsRoute = 0;
            for(U8 i = 0; i < MAXROUTECOUNT; i++)
            {
                if(GetRouteTVMode(i) == E_ROUTE_DVBS2)
                {
                    u8DvbsRoute = i;
                    break;
                }
            }

            MSrv_DTV_Player_DVBS * pDtv = NULL;
            pDtv = dynamic_cast<MSrv_DTV_Player_DVBS*>(GetMSrvDtvByIndex(u8DvbsRoute));
            ASSERT(pDtv);
            pDtv->CleanAllSatelliteDB();
            pDtv->ReSetSatelliteDB();
        }
#endif
        for(U8 uDtvRoute = 0; uDtvRoute < MAXROUTECOUNT; uDtvRoute++)
        {
            if(GetRouteTVMode(uDtvRoute) != E_ROUTE_NONE)
            {
                GetMSrvDtvByIndex(uDtvRoute)->ProgramDbReset(FALSE);
            }
        }
#if (CI_PLUS_ENABLE == 1)
        ((MSrv_DTV_Player_DVB*)(GetMSrvDtv()))->ResetOPCacheDB(FALSE);
#endif
    }
    else if(pSrcTable[MAPI_INPUT_SOURCE_ATV].u32EnablePort)
    {
#if (ISDB_SYSTEM_ENABLE == 1)

        MSrv_ATV_Database *pAtvDb = dynamic_cast<MSrv_ATV_Database *>(GetMSrvAtvDatabase());
        // clear air atv
        pAtvDb->ConnectDatabase(MSrv_ATV_Database::DVBT_DB);
        GetMSrvAtvDatabase()->CommondCmd(RESET_ATV_DATA_MANAGER , 0 , 0 , NULL);

        // clear cable atv
        pAtvDb->ConnectDatabase(MSrv_ATV_Database::DVBC_DB);
        GetMSrvAtvDatabase()->CommondCmd(RESET_ATV_DATA_MANAGER , 0 , 0 , NULL);
#else
        //SetInputSource(MAPI_INPUT_SOURCE_ATV);
        GetMSrvAtvDatabase()->CommondCmd(RESET_ATV_DATA_MANAGER , 0 , 0 , NULL);
#endif
    }
    else
    {
        if(pSrcTable[MAPI_INPUT_SOURCE_DTV].u32EnablePort)
        {
            GetMSrvDtv()->ProgramDbReset(FALSE);
        }
        else
        {
            ASSERT(0);
        }
    }

    //reset Power Setting
    if (!MSrv_Control_common::SetEnvPowerOnMusicVolume(DEFAULT_POWERON_MUSIC_VOL))
    {
        MSRV_CONTROL_DVB_ERR("set EnvPowerOnMusicVolume to default error\n");
    }
    if (!MSrv_Control_common::SetEnvPowerOnMusicMode(EN_POWERON_MUSIC_DEFAULT))
    {
        MSRV_CONTROL_DVB_ERR("set EnvPowerOnMusicMode to default error\n");
    }
    if (!MSrv_Control_common::SetEnvPowerOnLogoMode(EN_LOGO_DEFAULT))
    {
        MSRV_CONTROL_DVB_ERR("set EnvPowerOnLogoMode to default error\n");
    }

#if (MHEG5_ENABLE == 1)
    MSrv_DTV_Player_DVB *pMsrvDtv = dynamic_cast<MSrv_DTV_Player_DVB *>(GetMSrvDtv());
    if (pMsrvDtv != NULL)
    {
        if (pMsrvDtv->DoMHEG5FactoryReset() == FALSE)
        {
            MSRV_CONTROL_DVB_ERR("m_pcMHEG5_Player is NULL\n");
        }
    }
#endif

#if (MSTAR_TVOS == 0)
#if (CI_ENABLE == 1)
    //Finalize GetMSrvCIMMI
    {
        MSrv_CIMMI *pInstance = NULL;
        pInstance = dynamic_cast<MSrv_CIMMI *>(m_pMSrvList[E_MSRV_CIMMI]);
        ASSERT(pInstance);
        pInstance->Finalize();
        pInstance = dynamic_cast<MSrv_CIMMI *>(m_pMSrvList[E_MSRV_CIMMI_EX]);
        if(NULL != pInstance)
        {
            pInstance->Finalize();
        }
    }
#endif
#if (CEC_ENABLE == 1)
    //Finalize MSrvCEC
    // join CEC monitor destory  because the monitor will accese the value of MSrv_CEC
    if (m_stCecThreadInfo.bActive== TRUE)
    {
        m_stCecThreadInfo.bActive = FALSE;
        if(m_CECThread != 0)
            {
                pthread_join(m_CECThread,NULL);
            }
    }
    {
        MSrv_CEC *pInstance = GetMSrvCEC();
        ASSERT(pInstance);
        pInstance->Finalize();
    }
#endif

#if (ENABLE_LITE_SN == 0)
    {
        MSrv_Advert_Player *pInstance = GetMSrvAdvertPlayer();
        ASSERT(pInstance);
        pInstance->Finalize();
    }
#endif

    //Finalize MSrv_ATV_Database
    {
        MSrv_ATV_Database *pInstance = GetMSrvAtvDatabase();
        ASSERT(pInstance);
        pInstance->Finalize();
    }

    //Finalize MSrv_Network
    {
#if(0==MSTAR_TVOS)
        MSrv_Network_Control *pInstance = GetMSrvNetwork();
        ASSERT(pInstance);
        pInstance->Finalize();
#endif
    }

    //Finalize MSrv_Factory_Mode
    {
        MSrv_Factory_Mode *pInstance = GetMSrvFactoryMode();
        ASSERT(pInstance);
        pInstance->Finalize();
    }

    //Finalize MSrv_Picture
    {
        MSrv_Picture *pInstance = GetMSrvPicture();
        ASSERT(pInstance);
        pInstance->Finalize();
    }

#if 0
    //Finalize MSrv_SSSound
    {
        MSrv_SSSound *pInstance = GetMSrvSSSound();
        ASSERT(pInstance);
        pInstance->Finalize();
    }
#endif

    //Reset System Database
    {
        GetMSrvSystemDatabase()->SetFactoryRestoreDefault();
        {
            MS_USER_SYSTEM_SETTING m_stSysSetting;

            GetMSrvSystemDatabase()->GetUserSystemSetting(&m_stSysSetting);

            const MAPI_VIDEO_INPUTSRCTABLE *pSrcTable;
            pSrcTable = GetSourceList();

            if(pSrcTable[MAPI_INPUT_SOURCE_DTV].u32EnablePort)
            {
                m_stSysSetting.enInputSourceType = MAPI_INPUT_SOURCE_DTV;//FIXME;;We should read the default from system config file
            }
            else  if(pSrcTable[MAPI_INPUT_SOURCE_ATV].u32EnablePort)
            {
                m_stSysSetting.enInputSourceType = MAPI_INPUT_SOURCE_ATV;//FIXME;;We should read the default from system config file
            }
            else
            {
                m_stSysSetting.enInputSourceType = MAPI_INPUT_SOURCE_NONE;//FIXME;;We should read the default from system config file
            }

            if(IsSupportTheDTVSystemType(ATSC_ENABLE))
            {
                m_stSysSetting.Country = E_US;
            }
            else
            {
                m_stSysSetting.Country = E_UK;
            }

            GetMSrvSystemDatabase()->SetUserSystemSetting(&m_stSysSetting);
        }

        #if (SKYPE_ENABLE == 1)
        #if (MSTAR_IPC == 1)
        int ret=0;

        ret = APM_UnregisterAutoStartApp("Skype");
        if(0!=ret)
        {
            printf("APM_UnregisterAutoStartApp fail\n");
        }
        #endif
        SystemCmd("/config/apps_file/skype/skype_reset_default.sh");
        #endif


        usleep(1 * 1000 * 1000); //FIXME;;waiting 1 sec for storage backgroung write back
    }

    mapi_interface::Get_mapi_display()->OnOff(FALSE); // set display off to prevent some garbage message on some panel

    // clear browser's cache, cookie, local storage and application cache.
    #if (MSTAR_IPC == 1)
        APM_RequestToExit((char *) "MstarLauncher");

        MAPI_BOOL checkClear = MAPI_FALSE;
        checkClear = browserClearLocalStorage();
        if (checkClear == MAPI_FALSE)
        {
            MSRV_CONTROL_DVB_ERR("Browser's local storage clear fail when reset factory.\n");
        }
    #endif
    mapi_interface::FinalizeDB();   //Finalize mapi_strorage DB, we need to make sure all the DB actions are done before reboot
    SystemReboot();
#endif
    return TRUE;
}


MAPI_BOOL MSrv_Control_DVB::_SetVideoMute(MAPI_BOOL bVideoMute , MAPI_U16 u16VideoUnMuteTime , MAPI_INPUT_SOURCE_TYPE eMapiSrcType ,EN_MUTE_ENGINE engine )
{
    MSrv_MM_PlayerInterface *pPlayerInterface = MSrv_MM_PlayerInterface::GetInstance();
    mapi_video *pMapiVideo = mapi_interface::Get_mapi_video(eMapiSrcType);

#if (INPUT_SOURCE_LOCK_ENABLE == 1)
    //Check input source lock(ISL) status and decide video mute/unmute action
    if (IsInputSourceLock(eMapiSrcType) == MAPI_TRUE)
    {
        bVideoMute = MAPI_TRUE;
    }
#endif

    if((!bVideoMute) && (pPlayerInterface != NULL)
       && pPlayerInterface->MM_IsSuspendDSProcessing()
       && (engine == ENGINE_XC))
    {
        //suspend ds procedure will trigger a sequenc change event, and it cannot control by HK,
        //so mm should take responsiblity for disable screen mute in this procedure,
        //or else unexpected garbage will be shown
    }
    else
    {
        if(pMapiVideo == NULL)
        {
            MSRV_CONTROL_DVB_ERR("pMapiVideo is NULL. so we can't Mute\n");
            return MAPI_FALSE;
        }
        pMapiVideo->SetVideoMute(bVideoMute , u16VideoUnMuteTime,(E_MUTE_ENGINE)engine);
    }

#if (CVBSOUT_XCTOVE_ENABLE == 0)
#if (VE_ENABLE == 1)//Add ATV/AV cvbs out control here, for main src only
    if(  (eMapiSrcType == MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW))
       &&(IsVideoOutFreeToUse()))
    {
        mapi_video_out *pMapiVideoOut = mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_MONITOR_MODE);
        if(  pMapiVideoOut->IsDestTypeExistent(MAPI_MAIN_WINDOW)
           &&pMapiVideoOut->IsActive())
        {
            if(IsSrcATV(eMapiSrcType) || IsSrcAV(eMapiSrcType))
            {
                if(bVideoMute == MAPI_TRUE) //Screen off
                {
                    //below is to avoid Av out off, when main win do video mute in signal stable case, such as set aspect ratio .
                    if(GetMSrvPlayer(eMapiSrcType)->IsSignalStable() == MAPI_FALSE)
                    {
                        pMapiVideoOut->SetVideoMute(MAPI_TRUE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, MAPI_MAIN_WINDOW);
                    }
                }
                else //Screen on
                {
                    if(IsSrcATV(eMapiSrcType))
                    {
                        if(GetMSrvPlayer(eMapiSrcType)->IsSignalStable() == MAPI_TRUE)//check if it is ATV snow screen now
                        {
                            pMapiVideoOut->SetVideoMute(MAPI_FALSE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, MAPI_MAIN_WINDOW);
                        }
                    }
                    else
                    {
                        pMapiVideoOut->SetVideoMute(MAPI_FALSE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, MAPI_MAIN_WINDOW);
                    }
                }
            }
        }
    }
#endif
#endif

    return TRUE;
}

#if (AUTO_TEST == 1)
BOOL MSrv_Control_DVB::SetAutoScartEnable(U8 u8Port, BOOL bEnable)
{
    mapi_scope_lock(scopeLock, &m_AutoScartVariable);

    if(u8Port == 1)
    {
        m_bAutoScart_1_Enable = bEnable;
    }
    else  if(u8Port == 2)
    {
        m_bAutoScart_2_Enable = bEnable;
    }
    else
    {
        MSRV_CONTROL_DVB_ERR(" Error, SetAutoScartEnable port not support \n");
        return FALSE;
    }
    return TRUE;

}

U32 MSrv_Control_DVB::GetSoundVolume(void)
{
    U32 nVolume;
    nVolume = GetMSrvSSSound()->GetVolume();

    return nVolume;
}

MAPI_U8 MSrv_Control_DVB::SetSoundVolume(const MAPI_U8 u8Vol)
{
    GetMSrvSSSound()->SetVolume(u8Vol);

    return SSSOUND_OK;
}

MAPI_BOOL MSrv_Control_DVB::GetMuteStatus(const MAPI_SOUND_MUTE_STATUS_TYPE mute_status_type)
{
    MAPI_BOOL status_rtn;
    status_rtn = GetMSrvSSSound()->GetMuteStatus(mute_status_type);

    return status_rtn;
}
#endif

#if (ENABLE_LITE_SN != 1)
BOOL MSrv_Control_DVB::StartUartDebug()
{
#if (MSTAR_TVOS == 1)
    mapi_uartdebug* uartDebug = mapi_uartdebug::GetInstance();
    return uartDebug->Start(UARTDBG_MODE_STD_UART1);
#else
    MS_USER_SYSTEM_SETTING stUserSetting;
    GetMSrvSystemDatabase()->GetUserSystemSetting(&stUserSetting);
    if(stUserSetting.bUartBus)
    {
        mapi_uartdebug* uartDebug = mapi_uartdebug::GetInstance();
        return uartDebug->Start();
    }
    return FALSE;
#endif
}

BOOL MSrv_Control_DVB::UartSwitch()
{
    mapi_uartdebug* uartDebug = mapi_uartdebug::GetInstance();
    return uartDebug->Start();
}
#endif

// <BGPVR
#define NOT_IMPLEMENTED()    printf("Not implemented: %s, %s, %d\n", __FILE__, __FUNCTION__, __LINE__)
/*
EN_DTV_RECORDER_RESOURCE_STATUS MSrv_Control_DVB::GetRecorderRouteStatus(
    U8 u8RecorderRouteIndex, U8 &u8UsedRoute) const
{
    u8UsedRoute = MSrv_Control::GetInstance()->GetCurrentDtvRoute();
    return _GetRecorderRouteStatus(u8RecorderRouteIndex);
}
*/

BOOL MSrv_Control_DVB::SetRecordServiceByRoute(const ST_TRIPLE_ID &stService,const U8 u8RouteIndex, BOOL bForceSet)
{
#if (PVR_ENABLE == 1)
    ASSERT(TRUE == m_bInit);

#if (PIP_ENABLE == 1)
    //Check direct set background route conflict
    ST_PLAYER_STATE stPlayerState;
    MSrv_PlayerControl::GetInstance()->GetPlayerState(stPlayerState);
    stPlayerState.v_PVRRoute.push_back(u8RouteIndex);

    if (MSrv_PlayerControl::GetInstance()->CheckConflict(stPlayerState) != EN_CONFLICT_NONE)
    {
        if (bForceSet == FALSE)
        {
            return FALSE;
        }
    }
#endif

    if(_IsSameThread() == TRUE)
    {
        return _DoSetRecordServiceByRoute(stService, u8RouteIndex);
    }

    MSRV_CMD stCmd;
    int iRet;
    memset(&stCmd, 0, sizeof(MSRV_CMD));

    stCmd.enCmd = E_CMD_SET_RECORD_SERVICE_BY_ROUTE;
    stCmd.u32Param1 = (stService.u16OnId << 16) | stService.u16TsId;
    stCmd.u32Param2 = stService.u16SrvId;
    stCmd.u32Param3 = u8RouteIndex;
    m_pCmdEvt->Send(stCmd);

    iRet = m_pCmdAckEvt->Wait(&stCmd);
    ASSERT(0 == iRet);
    ASSERT(E_CMD_SET_RECORD_SERVICE_BY_ROUTE == stCmd.enCmd);

    return (BOOL)stCmd.u32Param1;
#else
    return FALSE;
#endif
}

EN_PVR_STATUS MSrv_Control_DVB::StartRecordRoute(U8 u8DtvRouteIndex, U16 u16CachedPinCode)
{
#if (PVR_ENABLE == 1)
    ASSERT(TRUE == m_bInit);

    if(_IsSameThread() == TRUE)
    {
        return _DoStartRecordRoute(u8DtvRouteIndex, u16CachedPinCode);
    }

    MSRV_CMD stCmd;
    int iRet;
    memset(&stCmd, 0, sizeof(MSRV_CMD));

    stCmd.enCmd = E_CMD_START_RECORD_ROUTE;
    stCmd.u32Param1 = u8DtvRouteIndex;
    stCmd.u32Param2 = u16CachedPinCode;
    m_pCmdEvt->Send(stCmd);

    iRet = m_pCmdAckEvt->Wait(&stCmd);
    ASSERT(0 == iRet);
    ASSERT(E_CMD_START_RECORD_ROUTE == stCmd.enCmd);

    return (EN_PVR_STATUS)stCmd.u32Param1;
#else
    return E_PVR_ERROR;
#endif
}

void MSrv_Control_DVB::StopRecordRoute(U8 u8DtvRouteIndex)
{
#if (PVR_ENABLE == 1)
    ASSERT(TRUE == m_bInit);

    if(_IsSameThread() == TRUE)
    {
        return _DoStopRecordRoute(u8DtvRouteIndex);
    }

    MSRV_CMD stCmd;
    int iRet;
    memset(&stCmd, 0, sizeof(MSRV_CMD));

    stCmd.enCmd = E_CMD_STOP_RECORD_ROUTE;
    stCmd.u32Param1 = u8DtvRouteIndex;
    m_pCmdEvt->Send(stCmd);

    iRet = m_pCmdAckEvt->Wait(&stCmd);
    ASSERT(0 == iRet);
    ASSERT(E_CMD_STOP_RECORD_ROUTE == stCmd.enCmd);
#endif
    return;
}

BOOL MSrv_Control_DVB::_DoSetRecordServiceByRoute(const ST_TRIPLE_ID &stService, const U8 u8RouteIndex)
{
    return MSrv_PlayerControl::GetInstance()->SetRecordServiceByRoute(stService, u8RouteIndex);
}

EN_PVR_STATUS MSrv_Control_DVB::_DoStartRecordRoute(U8 u8DtvRouteIndex, U16 u16CachedPinCode)
{
    return (EN_PVR_STATUS)MSrv_PlayerControl::GetInstance()->StartRecordRoute(u8DtvRouteIndex, u16CachedPinCode);
}

void MSrv_Control_DVB::_DoStopRecordRoute(U8 u8DtvRouteIndex)
{
    MSrv_PlayerControl::GetInstance()->StopRecordRoute(u8DtvRouteIndex);
}
// BGPVR>

BOOL MSrv_Control_DVB::_IsSameThread(void) const
{
    if (m_pthreadMonitor_id == pthread_self())
    {
        return TRUE;
    }

    if (FALSE==MSrv_Control_common::m_monitor_st.m_bFlagThreadMonitorActive)
    {
        return TRUE;
    }

    return FALSE;
}

void MSrv_Control_DVB::_SaveDtvRouteInDb(const U8 u8DtvRoute)
{
    MSrv_ATV_Database *pAtvDb = dynamic_cast<MSrv_ATV_Database *>(m_pMSrvList[E_MSRV_ATV_DATABASE]);
    ASSERT(pAtvDb);

    MSrv_System_Database *pDtvDb = dynamic_cast<MSrv_System_Database_DVB *>(GetMSrvSystemDatabase());
    ASSERT(pDtvDb);

    switch(GetRouteTVMode(u8DtvRoute))
    {
#if (ISDB_SYSTEM_ENABLE == 1)
        case E_ROUTE_ISDB:
        {
            ST_MEDIUM_SETTING stMedium;
            pDtvDb->GetMediumSetting(&stMedium);
            if(stMedium.AntennaType == E_ANTENNA_TYPE_AIR)
            {
                pAtvDb->ConnectDatabase(MSrv_ATV_Database::DVBT_DB);
                pDtvDb->SetDtvRoute(u8DtvRoute);
            }
            else
            {
                pAtvDb->ConnectDatabase(MSrv_ATV_Database::DVBC_DB);
                pDtvDb->SetDtvRoute(u8DtvRoute);
            }
            break;
        }
#endif
        case E_ROUTE_DTMB:
        case E_ROUTE_DVBT:
        case E_ROUTE_DVBT2:
             {
                 pAtvDb->ConnectDatabase(MSrv_ATV_Database::DVBT_DB);
                 pDtvDb->SetDtvRoute(u8DtvRoute);
             }
            break;

        case E_ROUTE_DVBC:
        case E_ROUTE_DVBS:
        case E_ROUTE_DVBS2:
             {
                 pAtvDb->ConnectDatabase(MSrv_ATV_Database::DVBC_DB);
                 pDtvDb->SetDtvRoute(u8DtvRoute);
             }
            break;

        default:
            MSRV_CONTROL_DVB_ERR("GetRouteTVMode(1) %d Error.", GetRouteTVMode(1));
            ASSERT(0);
            break;
    }

}

