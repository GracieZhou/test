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
#include "MSrv_Control_common.h"

// headers of standard C libs
#include <sys/prctl.h>
#include <sys/syscall.h>
#include <dirent.h>
#include <fcntl.h>
#include <limits.h>
#include "dirent.h"
#include <sys/wait.h>
#include <sys/stat.h>
#include <unistd.h>
#include <fstream>

// headers of standard C++ libs
#include "jpeglib.h"

// headers of the same layer's
#include "MSrv_Player.h"
#include "MSrv_ATV_Player.h"
#include "MSrv_ATV_Player_Customer.h"

#if (CHANNEL_CHANGE_FREEZE_IMAGE_BYDFB_ENBALE == 1)
#include "MSrv_TV_Player.h"
#endif

#if (DVB_ENABLE == 1)
#include "MSrv_DTV_Player_DVBT_Customer.h"
#include "MSrv_DTV_Player_DVBC_Customer.h"
#include "MSrv_DTV_Player_DVBS_Customer.h"
#include "MSrv_DTV_Player_DTMB_Customer.h"
#include "MSrv_DTV_Player_ISDB_Customer.h"
#endif

#if (ATSC_SYSTEM_ENABLE == 1)
#include "MSrv_ATSC_Player.h"
#else
#include "MSrv_System_Database_DVB_Customer.h"
#endif

#if (ENABLE_BACKEND == 1)
#include "MSrv_Backend.h"
#endif
#include "MSrv_SCART_Player_Customer.h"
#include "MSrv_AV_Player_Customer.h"
#include "MSrv_SV_Player_Customer.h"
#include "MSrv_COMP_Player_Customer.h"
#include "MSrv_HDMI_Player_Customer.h"
#include "MSrv_PC_Player_Customer.h"
#include "MSrv_Storage_Player.h"
#if (ACR_ENABLE == 1)
#include "MSrv_SambaTv.h"
#endif
#if (RVU_ENABLE == 1)
#include "MSrv_RVU_Player_Customer.h"
#endif
#include "MSrv_System_Database.h"
#include "MSrv_ChannelManager.h"
#include "MSrv_Picture.h"
#include "MSrv_Video.h"
#if (ENABLE_LITE_SN == 0)
#include "MSrv_SrcDetect.h"
#include "MSrv_BacklightControl.h"
#endif
#include "MSrv_Factory_Mode.h"
#include "MSrv_Timer.h"
#include "MSrv_SSSound.h"
#include "MSrv_MountNotifier.h"
#include "MSrv_DivX_DRM.h"
#include "CustomerKeycode.h"
#include "MSrv_MountNotifier.h"
#include "MSrv_Network_Control.h"
#include "MSrv_PreSharedKey.h"
#if (STEREO_3D_ENABLE == 1)
#include "MSrv_3DManager.h"
#endif
#if (FRONTPNL_ENABLE == 1)
#include "mapi_frontpnl.h"
#endif //FRONTPNL_ENABLE

#if (STB_ENABLE == 0)
#if (ENABLE_LITE_SN == 0)
#include "MSrv_NetworkUpgrade.h"
#endif
#endif

#if (CEC_ENABLE == 1)
#include "MSrv_CEC.h"
#endif

#include "MSrv_Advert_Player.h"
#include "MSrv_Control.h"

#if (MHL_ENABLE == 1)
#include "MSrv_MHL.h"
#endif

#if (HDMITX_ENABLE == 1)
#include "MSrv_HDMITX.h"
#endif

#if (VCHIP_ENABLE == 1)
#include "MW_VChip.h"
#endif

#if (PVR_ENABLE == 1)
#include "MW_PVR.h"
#endif

#if (POWEROFF_MUSIC_ENABLE)
#include "MSrv_ZmplayerSupernova.h"
#endif

#if (HBBTV_ENABLE == 1)
#include "MW_HBBTV.h"
#endif

#if ((AUTO_TEST == 1) && (MSTAR_TVOS == 0))
#include "AT_CmdManager_MSrv.h"
#endif

#if (MODULE_TEST == 1)
#include "MT_CmdManager_MSrv.h"
#if (MSTAR_TVOS == 1)
#include "MT_CmdManager_TVOS.h"
#endif
#endif

#if (ENABLE_NETREADY == 1)
#include "MSrv_DeviceAgent.h"
#endif

#if (STR_ENABLE == 1)
#include "MSrv_MWE.h"
#if (INTEL_WIDI_ENABLE == 1)
#include "MSrv_WIDI.h"
#endif
#endif

#if (MWB_LAUNCHER_ENABLE==1)
#include "MstarLauncherAgent.h"
#endif
#if (MSTAR_IPC == 1)
#include "apm.h"
#endif

#if (CI_ENABLE == 1)
#include "MSrv_CIMMI.h"
#endif

// headers of underlying layer's
#include "debug.h"
#include "mapi_types.h"
#include "mapi_vd.h"
#include "mapi_video_vd_cfg.h"
#include "mapi_video.h"
#include "mapi_interface.h"
#include "mapi_system.h"
#include "mapi_utility.h"
#include "mapi_audio.h"
#include "mapi_sar.h"
#include "mapi_video_dtv_cfg.h"
#include "mapi_gpio.h"
#include "mapi_gpio_devTable.h"
#include "mapi_pql_customer.h"
#include "mapi_video_customer.h"
#include "mapi_audio_customer.h"
#include "mapi_miu.h"
#include "mapi_display.h"
#include "mapi_env_manager.h"
#include "mapi_storage_spiflash.h"
#include "mapi_storage_factory_config.h"
#include "mapi_storage_eeprom.h"
#include "mapi_syscfg_table.h"
#include "mapi_i2c.h"
#include "mapi_timing_transform_container_customer.h"
#include "mapi_hdmi_video.h"
#include "mapi_pc_video.h"

#if (HSL_ENABLE == 1)
#include "mapi_hsl.h"
#endif

#if (STR_ENABLE == 1)
#include "mapi_pcb.h"
#include "mapi_str.h"
#endif

#if (OAD_ENABLE == 1)
#include "MW_OAD_Parser.h"
#endif

#include "SystemInfo.h"
#include "DFBInfo.h"

#include "CusInfo.h"

#include "MSystem.h"

#include "MMAPInfo.h"

#if (BRICK_TERMINATOR_ENABLE == 1)
#include "BrickTerminatorInfo.h"
#endif
#if (MSTAR_TVOS == 1)
#include <stdio.h>
#include <sys/wait.h>
#include <cutils/properties.h>
#endif

#define MSRV_CONTROL_COMMON_ERR(fmt, arg...)          printf((char *)fmt, ##arg)
#define MSRV_CONTROL_COMMON_DBG(fmt, arg...)          //printf((char *)fmt, ##arg)
#define MSRV_CONTROL_COMMON_INFO(fmt, arg...)         //pritf((char *)fmt, ##arg)
#define MSRV_CONTROL_COMMON_FLOW(fmt, arg...)         //printf((char *)fmt, ##arg)

//Default enable PIP+Traveling dbg msg, normal auto test will not enter its flow
#define MSRV_CONTROL_PIP_ERR(fmt, arg...)               printf((char *)fmt, ##arg)
#define MSRV_CONTROL_TRAVELING_CHECK(fmt, arg...)       //printf((char *)fmt, ##arg)
#define MSRV_CONTROL_TRAVELING_ERR(fmt, arg...)         printf((char *)fmt, ##arg)

#define SCART_OUT_TV_MONITOR 0
#define SCART_OUT_MONITOR_TV 1
#define SCART_OUT_TV_NONE 2
#define SCART_OUT_MONITOR_NONE 3

#define WIDTH_4K2K      (3840)
#define HEIGHT_4K2K     (2160)
#define OFFSET_4K2K     (10)

#define SPISIZE 0x1D0000 //SPI offset to store USB/OAD upgrade flag
#define SYSTEM_BANK_SIZE            0x10000 // 64k

#ifdef STB_TUNER_T2
#define POST_VIDEO_INIT_TIMEOUT 4000
#else
#define POST_VIDEO_INIT_TIMEOUT 2000
#endif

#if (STR_ENABLE == 1)
#define STR_MAX_CNT_FILE "/sys/power/str_max_cnt"
#define MAX_SUSPEND_IDX   10
#endif

#if (PREVIEW_MODE_ENABLE == 1)
#define PREVIEW_MODE_DEBUG_MSG(fmt, arg...)          //printf((char *)"\033[0;32;32m[Preview Mode]" fmt "\033[0m", ##arg)
#define PREVIEW_MODE_WIDTH                           1280
#define PREVIEW_MODE_HEIGHT                          720
#define BYTE_OF_YUV_422                              2
#define SIZE_OF_YUV_422_IMAGE                        PREVIEW_MODE_WIDTH*PREVIEW_MODE_HEIGHT*BYTE_OF_YUV_422
#define PREVIEW_MODE_IP                              "E_MMAP_ID_PREVIEW_MODE"
#define FHD_PANEL_WIDTH                              1920
#define FHD_PANEL_HEIGHT                             1080
#if (MSTAR_TVOS == 1)
#define PREVIEW_MODE_IMAGE_SAVE_PATH "/var/tmp/"    // the preview picture placed in memory
#else
#define PREVIEW_MODE_IMAGE_SAVE_PATH "/dev/shm/"
#endif
#endif //end of #if (PREVIEW_MODE_ENABLE == 1)

#define CMDLINE_PATH "/proc/cmdline"
#define CMDLINE_SIZE 2048
#define KEYWORD_TO_AUTOTEST "autotest=true"
#define KEYWORD_TO_MODULETEST "moduletest=true"
#define KEYWORD_TO_HSL "hsl=true"

//Compiler flag file path
#define FLAG_FILE "/config/Flags"

typedef struct
{
    U8 u8UsbPort;
    char cPath[MAX_USB_PATH_LEN + 1];

} USB_UpgradeInfo;


typedef struct
{
    long bist_info;
    long autophase_info;
    unsigned char auto_upgrade_info[8];
} BOOT_DB; // Should sync the same struct with MBoot

MSrv* MSrv_Control_common::m_pMSrvList[MSrv_Control_common::E_MSRV_MAX] = {0,};

S8 MSrv_Control_common::m_USBUpgradePort = -1;

mapi_event<THREAD_HEART_BEAT_INFO> * MSrv_Control_common::m_pEvHeartBeat = NULL;
pthread_t MSrv_Control_common::m_pthreadHeartBeat = (pthread_t)NULL;
pthread_mutex_t MSrv_Control_common::m_MutHeartBeat = PTHREAD_MUTEX_INITIALIZER;
vector<THREAD_HEART_BEAT_INFO> MSrv_Control_common::m_vThreadInfo;

Monitor_t MSrv_Control_common::m_monitor_st = {NULL, 0};

BOOL MSrv_Control_common::m_bHeartBeatMonitor = TRUE;

#if (TRAVELING_ENABLE == 1)
TRAVELMODEEVENTCALLBACK MSrv_Control_common::m_pMSrvTravelEventCallback[E_TRAVELING_ENGINE_TYPE_MAX] = {NULL, NULL};
TRAVELMODEDATACALLBACK MSrv_Control_common::m_pMSrvTravelDataCallback[E_TRAVELING_ENGINE_TYPE_MAX] = {NULL, NULL};
#endif

#if (PIP_ENABLE == 1)
U16 MSrv_Control_common::m_PipHardwareLimitation = 944; //by hardware limiation, in PIP/POP, with only support to 944 pixels
#endif

#if (CEC_ENABLE == 1)
ST_THREAD_ARG_INFO MSrv_Control_common::m_stCecThreadInfo = {NULL, 0};

#endif
#if (ACTIVE_STANDBY_MODE_ENABLE == 1)
BOOL MSrv_Control_common::m_bStandbyModeActive = FALSE;

BEGIN_EVENT_MAP(MSrv_Control_common, MSrv)
ON_EVENT(EV_ACTIVE_STANDBY_MODE, &MSrv_Control_common::OnEvent_ActiveStandbyMode)
END_EVENT_MAP();

static ST_ACTIVE_STANDBY_TASK_LIST stActiveStandbyTask[E_ACTIVE_STANDBY_TASK_NUM] =
{
        {E_ACTIVE_STANDBY_TASK_PVR, FALSE, TRUE,1},
        {E_ACTIVE_STANDBY_TASK_STANDBY_SCAN, FALSE, TRUE, 2},
        {E_ACTIVE_STANDBY_TASK_OAD, FALSE, TRUE, 3},
        {E_ACTIVE_STANDBY_TASK_MHL, FALSE, TRUE, 4},
};

#endif

#if (MSTAR_TVOS == 1)
MAPI_BOOL bFirstBoot = MAPI_TRUE;
#endif
#if (STR_ENABLE == 1)
static U16 u16SuspendIdx = 0;
static void *pSuspendFunc[MAX_SUSPEND_IDX];
#endif

#if (PREVIEW_MODE_ENABLE == 1)
mapi_event<ST_TRAVELING_MODE_CALLBACK_INFORMATION> * MSrv_Control_common::m_pEvPreviewMode = NULL;

void WriteYUV422RawDataToJPEGFile(unsigned char* data, int quality, int image_width, int image_height, const char *filename)
{
#if 0 //dump original raw data
    FILE *sourcefile = fopen("/applications/Original.yuv", "wb");
    if (sourcefile == NULL)
    {
        ASSERT(0);
    }
    fwrite(data, 1, image_width*image_height*2, sourcefile);
    fclose(sourcefile);
#endif

    FILE *outfile = fopen(filename, "wb");
    if (outfile == NULL)
    {
        ASSERT(0);
    }

    struct jpeg_compress_struct cinfo;
    struct jpeg_error_mgr jerr;
    JSAMPIMAGE buffer;
    int band,i,buf_width[3],buf_height[3];

    cinfo.err = jpeg_std_error(&jerr);
    jpeg_create_compress(&cinfo);
    jpeg_stdio_dest(&cinfo, outfile);

    cinfo.image_width = image_width; /* image width and height, in pixels */
    cinfo.image_height = image_height;
    cinfo.input_components = 3; /* # of color components per pixel */
    cinfo.in_color_space = JCS_YCbCr; /* colorspace of input image */

    jpeg_set_defaults(&cinfo);
    jpeg_set_quality(&cinfo, quality, TRUE );

    //////////////////////////////
    cinfo.raw_data_in = TRUE;
    cinfo.jpeg_color_space = JCS_YCbCr;
    cinfo.comp_info[0].h_samp_factor = 2;
    cinfo.comp_info[0].v_samp_factor = 1;
    /////////////////////////

    jpeg_start_compress(&cinfo, TRUE);

    buffer = (JSAMPIMAGE) (*cinfo.mem->alloc_small) ((j_common_ptr) &cinfo,
             JPOOL_IMAGE, 3 * sizeof(JSAMPARRAY));
    for(band=0; band<3; band++)
    {
        buf_width[band] = cinfo.comp_info[band].width_in_blocks * DCTSIZE;
        buf_height[band] = cinfo.comp_info[band].v_samp_factor * DCTSIZE;
        buffer[band] = (*cinfo.mem->alloc_sarray) ((j_common_ptr) &cinfo,
                       JPOOL_IMAGE, buf_width[band], buf_height[band]);
    }

    int yIndex = 0;
    int yCount = 0;
    int uIndex = image_width*image_height;
    int uCount = 0;
    int vIndex = uIndex + (image_width*image_height/2);
    int vCount = 0;

    //yuv422(YUYV) format is y0, u0, y1, v0...
    unsigned char *yuvData = new (std::nothrow) unsigned char[(image_width*image_height)*2];
    ASSERT(yuvData != NULL);
    for (int i = 0; i < (image_width*image_height)*2; i+=4)
    {
        //y
        yuvData[yIndex+yCount] = data[i];
        yCount++;
        yuvData[yIndex+yCount] = data[i+2];
        yCount++;
        //u
        yuvData[uIndex+uCount] = data[i+1];
        uCount++;
        //v
        yuvData[vIndex+vCount] = data[i+3];
        vCount++;
    }

    unsigned char *rawData[3];
    rawData[0]=yuvData;
    rawData[1]=yuvData+(image_width*image_height);
    rawData[2]=yuvData+(image_width*image_height*3/2);

    int max_line = cinfo.max_v_samp_factor*DCTSIZE;
    for(int counter=0; cinfo.next_scanline < cinfo.image_height; counter++)
    {
        //buffer image copy.
        for(band=0; band<3; band++)
        {
            int mem_size = buf_width[band];
            unsigned char *pDst = (unsigned char *) buffer[band][0];
            unsigned char *pSrc = (unsigned char *) (rawData[band] + //yuv.data[band] YUV data base address
                                  counter*buf_height[band] * buf_width[band]);

            for(i=0; i<buf_height[band]; i++)
            {
                memcpy(pDst, pSrc, mem_size);
                pSrc += buf_width[band];
                pDst += buf_width[band];
            }
        }
        jpeg_write_raw_data(&cinfo, buffer, max_line);
    }

    jpeg_finish_compress(&cinfo);
    fclose(outfile);
    delete yuvData;
    jpeg_destroy_compress(&cinfo);
}
#endif//end of #if (PREVIEW_MODE_ENABLE == 1)

MSrv_Control_common::MSrv_Control_common()
{
    m_bInit = FALSE;
    m_pInputSrcTable = NULL;
    m_enCurrentFocusSource = MAPI_INPUT_SOURCE_NONE;
    m_enLastTVSource = MAPI_INPUT_SOURCE_NONE;
    MSrv_Control_common::m_monitor_st.m_bFlagThreadMonitorActive = FALSE;
    MSrv_Control_common::m_monitor_st.p_class = this;
    m_bSocOutputTimingLock = MAPI_FALSE;

    m_USBUpgradePort = -1;
#if (OAD_ENABLE == 1)
    m_bOADInMBoot = FALSE;
#if (FREEVIEW_AU_ENABLE == 1)
    m_stOADInfo.u32OUI         = SystemInfo::GetInstance()->getOADCustomerOUI();
    m_stOADInfo.u16HWModel     = SystemInfo::GetInstance()->getOADHWModel();
    m_stOADInfo.u16HWVersion   = SystemInfo::GetInstance()->getOADHWVersion();
    m_stOADInfo.u16SWAPModel   = SystemInfo::GetInstance()->getOADAPSWModel();
    m_stOADInfo.u16SWAPVersion = SystemInfo::GetInstance()->getOADAPSWVersion();
#endif
#endif

    m_enPipMode = E_PIP_MODE_OFF;
    m_bPipFinished = MAPI_TRUE;

#if (PIP_ENABLE == 1)
    m_enPrePipMode = E_PIP_MODE_OFF;
    m_stMainWinInfo.x = 0;
    m_stMainWinInfo.y = 0;
    mapi_display* pDisplay = mapi_interface::Get_mapi_display();
    pDisplay->GetInfo(mapi_display_datatype::DISPLAY_INFO_WIDTH, &m_stMainWinInfo.width);
    pDisplay->GetInfo(mapi_display_datatype::DISPLAY_INFO_HEIGHT, &m_stMainWinInfo.height);
    memset(&m_stSubWinInfo, 0, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE));
#endif
#if (TRAVELING_ENABLE == 1)
    memset(m_stTravelModeInfo, 0, E_TRAVELING_ENGINE_TYPE_MAX*sizeof(ST_TRAVELING_MODE_INFO));
#endif
    m_u32HeartBeatTime = 0;

    memset(&m_pthreadMonitor_id, 0, sizeof(pthread_t));


    m_pCmdEvt = new (std::nothrow) mapi_event<MSRV_CMD>;
    m_pCmdAckEvt = new (std::nothrow) mapi_event<MSRV_CMD>;
    ASSERT(m_pCmdEvt);
    ASSERT(m_pCmdAckEvt);
    m_pTVAsyncCmdEvt = new (std::nothrow) mapi_event<MSRV_TV_ASYNC_CMD_EVENT>;
    ASSERT(m_pTVAsyncCmdEvt);

#if (CEC_ENABLE == 1)
    memset(&m_CECThread, 0, sizeof(pthread_t));
    m_stCecThreadInfo.bActive = FALSE;
#endif

    pthread_mutex_init(&m_MutexPostVideoInit, NULL);
#if (ACTIVE_STANDBY_MODE_ENABLE == 1 )
    pthread_mutex_init(&m_MutexSetActiveStandby, NULL);
#endif
    m_pBootUpEvent = new (std::nothrow) mapi_event<BOOL>;
    m_bBootupInit = FALSE;

    pthread_mutex_init(&m_MutexLock, NULL);

    m_block=FALSE;
#if (MHL_ENABLE_BY_STANDBY_MODE == 1 )
    m_bPmOnRam = TRUE;
#else
    m_bPmOnRam = FALSE;
#endif
    IsMuteFlag = FALSE;
#if (STR_ENABLE == 1)
    m_bAndroidAudioMute = FALSE;
#endif
#if (CI_PLUS_ENABLE == 1)
    m_bOperatorProfile = FALSE;
#endif
#if (ACTIVE_STANDBY_MODE_ENABLE == 1)
#if (MSTAR_TVOS == 0)
    MWindowManager *wm = (MSystem::GetInstance())->GetMWindowManager();
    ASSERT(wm);
    wm->RegisterMSrvActiveStandbyMode(this);
#endif
#endif
#if(MSTAR_TVOS == 1) // for tvapp and supernova mutually send message
    memset(&m_aCharDataBlock,0,sizeof(m_aCharDataBlock));
    memset(&m_u16DataBlock,0,sizeof(m_u16DataBlock));
    pthread_mutex_init(&m_TvosInterfaceCMDMutex, NULL);
#endif

#if (PREVIEW_MODE_ENABLE == 1)
    memset(&m_PreviewModeThread, 0, sizeof(pthread_t));
    m_bPreviewModeExit = FALSE;
    pthread_mutex_init(&m_PreviewModeMutex, NULL);

    enFirstPreviewModeInputSource = MAPI_INPUT_SOURCE_VGA;
    m_bPreviewModeRunning = FALSE;
#endif//end of #if (PREVIEW_MODE_ENABLE == 1)
    m_bSourceChange = FALSE;

#if (MODULE_TEST == 1)
    m_bEnableChannelChangeProfileMSG = FALSE;
    m_bEnableVdecDecodeInfoProfileMSG = FALSE;
#endif

    pthread_mutex_init(&m_BootCompleteMutex,NULL);
}

MSrv_Control_common::~MSrv_Control_common()
{
    if(m_bInit)
    {
        Finalize();
    }
#if (ACTIVE_STANDBY_MODE_ENABLE == 1)
#if (MSTAR_TVOS == 0)
    MWindowManager *wm = (MSystem::GetInstance())->GetMWindowManager();
    ASSERT(wm);
    wm->RegisterMSrvActiveStandbyMode(NULL);
#endif
#endif
}
#if (MSTAR_TVOS == 1)
U8 MSrv_Control_common::Command_execv(const char *pathname, char * const argv[])
{
    pid_t pid = fork();
    if (pid == 0)
    {
        unsetenv("LD_PRELOAD");
        execv(pathname, argv);
        printf("execv command fail:%s",strerror(errno));
        exit(-1);
    }
    else
    {
        if(waitpid(pid, NULL, 0) == pid)
         {
            printf("successfully get child %d\n",pid);
            return 0;
         }
        else
        {
            printf("some error occured\n");
            return -1;
        }
    }
    return -1;
}
#endif
#if (PREVIEW_MODE_ENABLE == 1)
//-------------------------------------------------------------------------------------------------
/// Query Preview Mode running or not
/// @return                 \b TRUE: running, or FALSE: stop.
//-------------------------------------------------------------------------------------------------
BOOL MSrv_Control_common::IsPreviewModeRunning(void)
{
    return m_bPreviewModeRunning;
}

//-------------------------------------------------------------------------------------------------
/// Set First Preview Mode Input Source
/// @param enInputSourceType   \b MAPI_INPUT_SOURCE_TYPE: Input Source
/// @return None
//-------------------------------------------------------------------------------------------------
void MSrv_Control_common::SetFirstPreviewModeInputSource(MAPI_INPUT_SOURCE_TYPE enInputSourceType)
{
    enFirstPreviewModeInputSource = enInputSourceType;
}

//-------------------------------------------------------------------------------------------------
/// Enable Preview Mode Thread
/// @return None
//-------------------------------------------------------------------------------------------------
void MSrv_Control_common::EnablePreviewModeThread()
{
    mapi_scope_lock(scopeLock, &m_PreviewModeMutex);
    m_bPreviewModeExit = FALSE;

    int intPTHChk;
    pthread_attr_t attr1;

    pthread_attr_init(&attr1);
    pthread_attr_setdetachstate(&attr1, PTHREAD_CREATE_DETACHED);
    pthread_attr_setstacksize(&attr1, PTHREAD_STACK_SIZE);
    intPTHChk = PTH_RET_CHK(pthread_create(&m_PreviewModeThread, &attr1, PreviewModeThreadFunc, (void *)this));

    ASSERT(intPTHChk == 0);
    m_bPreviewModeRunning = TRUE;
}

//-------------------------------------------------------------------------------------------------
/// Disable Preview Mode
/// @return None
//-------------------------------------------------------------------------------------------------
void MSrv_Control_common::DisablePreviewModeThread()
{
    if(m_PreviewModeThread != 0)
    {
        m_bPreviewModeExit = TRUE;
        mapi_scope_lock(scopeLock, &m_PreviewModeMutex);
    }
}

void * MSrv_Control_common::PreviewModeThreadFunc(void *arg)
{
    MSrv_Control_common *_this = (MSrv_Control_common *) arg;
    ASSERT(_this != NULL);

    prctl(PR_SET_NAME, (unsigned long)"Preview Mode Thread");

#if (STR_ENABLE == 1)
    mapi_str::AutoRegister _R;
#endif

    const MAPI_VIDEO_INPUTSRCTABLE *pInputSrcTable;
    pInputSrcTable = _this->GetSourceList();

    MS_USER_SYSTEM_SETTING stGetSystemSetting;
    MSrv_Control::GetMSrvSystemDatabase()->GetUserSystemSetting(&stGetSystemSetting);

    int PreviewModeSourceBoundary_Start = MAPI_INPUT_SOURCE_VGA;
    int PreviewModeSourceBoundary_End = MAPI_INPUT_SOURCE_DTV2;

    while (1)
    {
        int enSubInputSource = _this->enFirstPreviewModeInputSource;
#if (STR_ENABLE == 1)
        if (mapi_interface::Get_mapi_str()->strThreadItemSuspend())
        {
            printf("Fixme, I am resumed %s::%d::%s\n", __FILE__, __LINE__, __FUNCTION__);
        }
#endif

        MAPI_INPUT_SOURCE_TYPE enCurrentMainInputSource = _this->GetCurrentMainInputSource();
        for(enSubInputSource = PreviewModeSourceBoundary_Start; enSubInputSource <= PreviewModeSourceBoundary_End; enSubInputSource++)
        {
            if(enSubInputSource == MAPI_INPUT_SOURCE_STORAGE)
            {
                continue;
            }
            usleep(100*1000);//100ms

            mapi_scope_lock(scopeLock, &_this->m_PreviewModeMutex);

            if(TRUE == _this->m_bPreviewModeExit)
            {
                PREVIEW_MODE_DEBUG_MSG("Disable preview mode by user (check point 1)!!!!!!!!!!!\n");
                _this->m_bPreviewModeRunning = FALSE;
                return NULL;
            }

            //Check device port enable
            if(pInputSrcTable[enSubInputSource].u32EnablePort)
            {
                //Check source is not active source
                if(enCurrentMainInputSource != enSubInputSource)
                {
                    //Preview image save path
                    char cPreviewImageName[8];
                    memset(cPreviewImageName, 0, sizeof(cPreviewImageName));
                    snprintf(cPreviewImageName, 3, "%d", enSubInputSource);
                    cPreviewImageName[7] = '\0';
                    strncat(cPreviewImageName, ".jpg", 4);

                    //Remove image first
                    char cPreviewModeImageSavePath[33];//becareful length of char array, it's related to "PREVIEW_MODE_IMAGE_SAVE_PATH"
                    memset(cPreviewModeImageSavePath, 0, sizeof(cPreviewModeImageSavePath));
                    snprintf(cPreviewModeImageSavePath, 32, "%s", PREVIEW_MODE_IMAGE_SAVE_PATH);
                    cPreviewModeImageSavePath[32] = '\0';
                    strncat(cPreviewModeImageSavePath, cPreviewImageName, 7);

                    PREVIEW_MODE_DEBUG_MSG("Preview image file path: %s\n", cPreviewModeImageSavePath);

#if (TRAVELING_ENABLE == 1)
                    //DIP capture picture
                    //Check traveling mode supported source
                    if(FALSE == _this->CheckTravelingModeSupport(enCurrentMainInputSource, (MAPI_INPUT_SOURCE_TYPE)enSubInputSource))
                    {
                        PREVIEW_MODE_DEBUG_MSG("unsupported source pair => enCurrentMainInputSource: %d        enSubInputSource: %d\n", enCurrentMainInputSource, enSubInputSource);
                        _this->PostEvent(NULL, EV_REFRESH_PREVIEW_MODE_WINDOW, (U32) enSubInputSource, (U32) EN_PREVIEW_MODE_SOURCE_CONFLICT);
                    }
                    else
                    {
#if (OFL_DET == 1)
                        //Offline detection
                        if(   (TRUE == stGetSystemSetting.bSourceDetectEnable)
                            &&(enSubInputSource != MAPI_INPUT_SOURCE_ATV)//atv offline detection always return fail,so skip it.
                            &&(enSubInputSource != MAPI_INPUT_SOURCE_DTV2)//dtv offline detection always return fail,so skip it.
                            &&(FALSE == _this->GetMSrvSourceDetect()->GetSrvDetGoodStatus((MAPI_INPUT_SOURCE_TYPE) enSubInputSource)))
                        {
                            PREVIEW_MODE_DEBUG_MSG("Offline detection: Signal(%u) Unstale !!!!!!!!!!!\n",(int)enSubInputSource);
                            _this->PostEvent(NULL, EV_REFRESH_PREVIEW_MODE_WINDOW, (U32) enSubInputSource, (U32) EN_PREVIEW_MODE_SIGNAL_UNSTABLE);
#if (MSTAR_TVOS == 1)
                            unlink(cPreviewModeImageSavePath);
#else
                            char tmpCmd[512];
                            memset(tmpCmd, 0, sizeof(tmpCmd));
                            sprintf(tmpCmd, "rm -f %s", cPreviewModeImageSavePath);
                            mapi_interface::Get_mapi_system()->SystemCmd(tmpCmd);
                            PREVIEW_MODE_DEBUG_MSG("System command: %s\n", tmpCmd);
#endif
                            continue;
                        }
#endif
                        PREVIEW_MODE_DEBUG_MSG("supported source pair => enCurrentMainInputSource: %d        enSubInputSource: %d\n", enCurrentMainInputSource, enSubInputSource);

                        MMapInfo_t * pMMAPinfo;
                        pMMAPinfo = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID(PREVIEW_MODE_IP));

                        PREVIEW_MODE_DEBUG_MSG("pMMAPinfo->u32Addr: %u\n", pMMAPinfo->u32Addr);
                        PREVIEW_MODE_DEBUG_MSG("pMMAPinfo->u32Size: %u\n", pMMAPinfo->u32Size);
                        PREVIEW_MODE_DEBUG_MSG("pMMAPinfo->u32Align: %u\n", pMMAPinfo->u32Align);

                        //1. Init traveling mode
                        ST_TRAVELING_MODE_INFO stTravelingModeInfo;
                        stTravelingModeInfo.u32TravelModeInfo_Version = TRAVELING_MODE_INFO_MSDK_VERSION;

                        stTravelingModeInfo.u16TravelModeInfo_Length = sizeof(ST_TRAVELING_MODE_INFO);
                        stTravelingModeInfo.u32MemoryPhyAddress = pMMAPinfo->u32Addr;
                        stTravelingModeInfo.u32MemorySize = pMMAPinfo->u32Size;
                        stTravelingModeInfo.enTravelingSource = E_TRAVELING_SUB_WINDOW;
                        stTravelingModeInfo.enMapiSubsource = (MAPI_INPUT_SOURCE_TYPE)enSubInputSource;
                        stTravelingModeInfo.enMemFormat = E_TRAVELING_MEM_FORMAT_YUV422_YUYV;
                        stTravelingModeInfo.bCaptureOSD = FALSE;
                        stTravelingModeInfo.u16MemoryAddrAlignment = 256;//pMMAPinfo->u32Align;
                        stTravelingModeInfo.u16TravelWidth = PREVIEW_MODE_WIDTH;
                        stTravelingModeInfo.u16TravelHeight = PREVIEW_MODE_HEIGHT;
                        stTravelingModeInfo.u16FrameRateX10 = 0;
                        stTravelingModeInfo.enCaptureStage = E_TRAVELING_CAP_STAGE_AUTO;

                        EN_TRAVELING_ENGINE_TYPE enTravelingEngineType;
                        MS_U16 u16PanelWidth = 0;
                        MS_U16 u16PanelHeight = 0;
                        MSrv_Video* pMsrvVideo = MSrv_Control_common::GetMSrvVideo();
                        pMsrvVideo->GetPanelWidthHeight(&u16PanelWidth,&u16PanelHeight);
                        enTravelingEngineType = E_TRAVELING_ENGINE_TYPE_HD1;
                        if((u16PanelWidth > FHD_PANEL_WIDTH) || (u16PanelHeight > FHD_PANEL_HEIGHT))
                        {
                            enTravelingEngineType = E_TRAVELING_ENGINE_TYPE_HD0;
                        }

                        if(E_TRAVELING_SUCCESS == _this->InitTravelingMode(&stTravelingModeInfo, enTravelingEngineType))
                        {
                            PREVIEW_MODE_DEBUG_MSG("Init Traveling Mode Success !!!!!!!!!!!\n");
#if (STB_ENABLE == 0)
                            // set channel for atv and dtv,than atv and dtv player can run
                            if(enSubInputSource == MAPI_INPUT_SOURCE_ATV)
                            {
                                MSrv_Control::GetMSrvAtv()->SetChannel(MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER , 0, 0, NULL), 1);
                            }
                            else if((enSubInputSource == MAPI_INPUT_SOURCE_DTV) || (enSubInputSource == MAPI_INPUT_SOURCE_DTV2))
                            {
                                MSrv_Control::GetMSrvDtv(MAPI_SUB_WINDOW)->PlayCurrentProgram();
                            }
#else
                            if((enSubInputSource == MAPI_INPUT_SOURCE_DTV) || (enSubInputSource == MAPI_INPUT_SOURCE_DTV2))
                            {
                                MSrv_Control::GetMSrvDtv(MAPI_SUB_WINDOW)->PlayCurrentProgram();
                            }
#endif
                            //new, delete mechanism to prevent using unsynchronous data
                            m_pEvPreviewMode = new (std::nothrow) mapi_event<ST_TRAVELING_MODE_CALLBACK_INFORMATION>;
                            ASSERT(m_pEvPreviewMode);

                            ST_TRAVELING_MODE_CALLBACK_INFORMATION stTravelingModeCallbackInfo;
                            memset(&stTravelingModeCallbackInfo, 0, sizeof(ST_TRAVELING_MODE_CALLBACK_INFORMATION));

                            //2. Start traveling mode
                            if(E_TRAVELING_SUCCESS == _this->StartTravelingMode(TravelingModeDataCallBackFunc, TravelingModeEventCallBackFunc, enTravelingEngineType))
                            {
                                PREVIEW_MODE_DEBUG_MSG("Start Traveling Mode Success !!!!!!!!!!!\n");

                                U8 u8RetryCount = 60;
                                BOOL bCallBackSuccessOrDisableByUser = FALSE;
                                while (u8RetryCount > 0)
                                {
                                    if (TRUE == _this->m_bPreviewModeExit)
                                    {
                                        PREVIEW_MODE_DEBUG_MSG("Disable preview mode by user (check point 2)!!!!!!!!!!!\n");
                                        bCallBackSuccessOrDisableByUser = TRUE;

                                        //3. Finalize traveling mode
                                        _this->FinalizeTravelingMode(enTravelingEngineType);

                                        break;
                                    }

                                    if (0 == m_pEvPreviewMode->Wait(&stTravelingModeCallbackInfo, 50))//wait 50 ms, 60 times, total 3000ms
                                    {
                                        bCallBackSuccessOrDisableByUser = TRUE;

                                        //3. StopTravelingMode (Pause)
                                        _this->StopTravelingMode((EN_TRAVELING_ENGINE_TYPE) stTravelingModeCallbackInfo.enEngineType);

                                        //4. Finalize traveling mode
                                        _this->FinalizeTravelingMode(enTravelingEngineType);

                                        if (TRUE == _this->m_bPreviewModeExit)
                                        {
                                            PREVIEW_MODE_DEBUG_MSG("Disable preview mode by user (check point 3)!!!!!!!!!!!\n");
                                            break;
                                        }

                                        //5. Process data and save image
                                        if(TRUE == _ProcessDipData(stTravelingModeCallbackInfo.pstTravelDataInfo, stTravelingModeCallbackInfo.enEngineType, cPreviewModeImageSavePath))
                                        {
                                            //6. Post event to notify UI refresh image
                                            _this->PostEvent(NULL, EV_REFRESH_PREVIEW_MODE_WINDOW, (U32) enSubInputSource, (U32) EN_PREVIEW_MODE_PROCESS_SUCCESS);
                                        }
                                        else
                                        {
                                            //6. Post event to notify UI refresh image
                                            _this->PostEvent(NULL, EV_REFRESH_PREVIEW_MODE_WINDOW, (U32) enSubInputSource, (U32) EN_PREVIEW_MODE_PROCESS_FAIL);
                                        }

                                        break;
                                    }
                                    u8RetryCount--;
                                }

                                if (FALSE == bCallBackSuccessOrDisableByUser)
                                {
                                    PREVIEW_MODE_DEBUG_MSG("DIP capture image time out !!!!!!!!!!!\n");
                                    //3. Finalize traveling mode
                                    _this->FinalizeTravelingMode(enTravelingEngineType);

                                    //4. Post event to notify UI refresh image
                                    _this->PostEvent(NULL, EV_REFRESH_PREVIEW_MODE_WINDOW, (U32) enSubInputSource, (U32) EN_PREVIEW_MODE_SIGNAL_UNSTABLE);
#if (MSTAR_TVOS == 1)
                                    unlink(cPreviewModeImageSavePath);
#else
                                    char tmpCmd[512];
                                    memset(tmpCmd, 0, sizeof(tmpCmd));
                                    sprintf(tmpCmd, "rm -f %s", cPreviewModeImageSavePath);
                                    mapi_interface::Get_mapi_system()->SystemCmd(tmpCmd);
                                    PREVIEW_MODE_DEBUG_MSG("System command: %s\n", tmpCmd);
#endif
                                }

                                delete m_pEvPreviewMode;
                                m_pEvPreviewMode = NULL;
                            }
                            else
                            {
                                PREVIEW_MODE_DEBUG_MSG("Start Traveling Mode fail !!!!!!!!!!!\n");
                                //3. Finalize traveling mode
                                _this->FinalizeTravelingMode(enTravelingEngineType);

                                //4. Post event to notify UI refresh image
                                _this->PostEvent(NULL, EV_REFRESH_PREVIEW_MODE_WINDOW, (U32) enSubInputSource, (U32) EN_PREVIEW_MODE_PROCESS_FAIL);
#if (MSTAR_TVOS == 1)
                                unlink(cPreviewModeImageSavePath);
#else
                                char tmpCmd[512];
                                memset(tmpCmd, 0, sizeof(tmpCmd));
                                sprintf(tmpCmd, "rm -f %s", cPreviewModeImageSavePath);
                                mapi_interface::Get_mapi_system()->SystemCmd(tmpCmd);
                                PREVIEW_MODE_DEBUG_MSG("System command: %s\n", tmpCmd);
#endif

                                delete m_pEvPreviewMode;
                                m_pEvPreviewMode = NULL;
                            }
                        }
                        else
                        {
                            PREVIEW_MODE_DEBUG_MSG("Init Traveling Mode fail !!!!!!!!!!!\n");
                            //3. Finalize traveling mode
                            _this->FinalizeTravelingMode(enTravelingEngineType);

                            //4. Post event to notify UI refresh image
                            _this->PostEvent(NULL, EV_REFRESH_PREVIEW_MODE_WINDOW, (U32) enSubInputSource, (U32) EN_PREVIEW_MODE_PROCESS_FAIL);
#if (MSTAR_TVOS == 1)
                            unlink(cPreviewModeImageSavePath);
#else
                            char tmpCmd[512];
                            memset(tmpCmd, 0, sizeof(tmpCmd));
                            sprintf(tmpCmd, "rm -f %s", cPreviewModeImageSavePath);
                            mapi_interface::Get_mapi_system()->SystemCmd(tmpCmd);
                            PREVIEW_MODE_DEBUG_MSG("System command: %s\n", tmpCmd);
#endif
                        }
                    }
#endif
                }
            }
        }
    }
    return NULL;
}

BOOL MSrv_Control_common::TravelingModeCaptureImage(void)
{
    BOOL bRet = FALSE;
    MMapInfo_t * pMMAPinfo;
    pMMAPinfo = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID(PREVIEW_MODE_IP));
    if(pMMAPinfo)
    {
        PREVIEW_MODE_DEBUG_MSG("pMMAPinfo->u32Addr: %u\n", pMMAPinfo->u32Addr);
        PREVIEW_MODE_DEBUG_MSG("pMMAPinfo->u32Size: %u\n", pMMAPinfo->u32Size);
        PREVIEW_MODE_DEBUG_MSG("pMMAPinfo->u32Align: %u\n", pMMAPinfo->u32Align);
    }
    else
    {
        printf("Get MMAP:%s fail. Please check MMAP setting\n", PREVIEW_MODE_IP);
        return FALSE;
    }

    //1. Init traveling mode
    ST_TRAVELING_MODE_INFO stTravelingModeInfo;
    stTravelingModeInfo.u32TravelModeInfo_Version = TRAVELING_MODE_INFO_MSDK_VERSION;
    stTravelingModeInfo.u16TravelModeInfo_Length = sizeof(ST_TRAVELING_MODE_INFO);
    stTravelingModeInfo.u32MemoryPhyAddress = pMMAPinfo->u32Addr;
    stTravelingModeInfo.u32MemorySize = pMMAPinfo->u32Size;
    stTravelingModeInfo.enTravelingSource = E_TRAVELING_ALL_WINDOW;
    stTravelingModeInfo.enMapiSubsource = enFirstPreviewModeInputSource;
    stTravelingModeInfo.enMemFormat = E_TRAVELING_MEM_FORMAT_YUV422_YUYV;
    stTravelingModeInfo.bCaptureOSD = TRUE;
    stTravelingModeInfo.u16MemoryAddrAlignment = pMMAPinfo->u32Align;
    stTravelingModeInfo.u16TravelWidth = PREVIEW_MODE_WIDTH;
    stTravelingModeInfo.u16TravelHeight = PREVIEW_MODE_HEIGHT;
    stTravelingModeInfo.u16FrameRateX10 = 10;
    stTravelingModeInfo.enCaptureStage = E_TRAVELING_CAP_STAGE_AUTO;

    EN_TRAVELING_ENGINE_TYPE enTravelingEngineType = E_TRAVELING_ENGINE_TYPE_HD0;

    if(E_TRAVELING_SUCCESS == InitTravelingMode(&stTravelingModeInfo, enTravelingEngineType))
    {
        PREVIEW_MODE_DEBUG_MSG("Init Traveling Mode Success !!!!!!!!!!!\n");

        //new, delete mechanism to prevent using unsynchronous data
        m_pEvPreviewMode = new (std::nothrow) mapi_event<ST_TRAVELING_MODE_CALLBACK_INFORMATION>;
        ASSERT(m_pEvPreviewMode);

        ST_TRAVELING_MODE_CALLBACK_INFORMATION stTravelingModeCallbackInfo;
        memset(&stTravelingModeCallbackInfo, 0, sizeof(ST_TRAVELING_MODE_CALLBACK_INFORMATION));

        //2. Start traveling mode
        if(E_TRAVELING_SUCCESS == StartTravelingMode(TravelingModeDataCallBackFunc, TravelingModeEventCallBackFunc, enTravelingEngineType))
        {
            PREVIEW_MODE_DEBUG_MSG("Start Traveling Mode Success !!!!!!!!!!!\n");

            if(0 == m_pEvPreviewMode->Wait(&stTravelingModeCallbackInfo, 3000))//wait 3000 ms
            {
                //3. StopTravelingMode (Pause)
                StopTravelingMode((EN_TRAVELING_ENGINE_TYPE) stTravelingModeCallbackInfo.enEngineType);
                //4. Finalize traveling mode
                FinalizeTravelingMode(enTravelingEngineType);

                //Preview image save path
                char cPreviewImageName[6] = {0};
                snprintf(cPreviewImageName, sizeof(cPreviewImageName), "0.jpg");
                cPreviewImageName[sizeof(cPreviewImageName)-1] = '\0';

                //Remove image first
                char cPreviewModeImageSavePath[33];//becareful length of char array, it's related to "PREVIEW_MODE_IMAGE_SAVE_PATH"
                memset(cPreviewModeImageSavePath, 0, sizeof(cPreviewModeImageSavePath));
                snprintf(cPreviewModeImageSavePath, 32, "%s", PREVIEW_MODE_IMAGE_SAVE_PATH);
                strncat(cPreviewModeImageSavePath, cPreviewImageName, 6);

                PREVIEW_MODE_DEBUG_MSG("Preview image file path: %s\n", cPreviewModeImageSavePath);

                //5. Process data and save image
                bRet = _ProcessDipData(stTravelingModeCallbackInfo.pstTravelDataInfo, stTravelingModeCallbackInfo.enEngineType, cPreviewModeImageSavePath);
                PREVIEW_MODE_DEBUG_MSG("stTravelingModeCallbackInfo.pstTravelDataInfo.u16BuffIndex = %d \n",stTravelingModeCallbackInfo.pstTravelDataInfo.u16BuffIndex);
            }
            else
            {
                PREVIEW_MODE_DEBUG_MSG("Capture image fail due to timeout\n");
                //3. StopTravelingMode (Pause)
                StopTravelingMode((EN_TRAVELING_ENGINE_TYPE) stTravelingModeCallbackInfo.enEngineType);
                //4. Finalize traveling mode
                FinalizeTravelingMode(enTravelingEngineType);
            }
        }
        else
        {
            PREVIEW_MODE_DEBUG_MSG("Start Traveling Mode Fail !!!!!!!!!!!\n");
            //3. StopTravelingMode (Pause)
            StopTravelingMode((EN_TRAVELING_ENGINE_TYPE) stTravelingModeCallbackInfo.enEngineType);
            //4. Finalize traveling mode
            FinalizeTravelingMode(enTravelingEngineType);
        }
        delete m_pEvPreviewMode;
        m_pEvPreviewMode = NULL;
    }
    else
    {
        FinalizeTravelingMode(enTravelingEngineType);
    }

    return bRet;
}

void MSrv_Control_common::TravelingModeDataCallBackFunc(void *pstTravelDataInfo, int enEngineType)
{
    PREVIEW_MODE_DEBUG_MSG("%s          %s()             %d\n", __FILE__, __FUNCTION__, __LINE__);
    if(NULL != m_pEvPreviewMode)
    {
        PREVIEW_MODE_DEBUG_MSG("%s          %s()             %d\n", __FILE__, __FUNCTION__, __LINE__);

        ST_TRAVELING_MODE_CALLBACK_INFORMATION stTravelingModeCallbackInfo;
        memset(&stTravelingModeCallbackInfo, 0, sizeof(ST_TRAVELING_MODE_CALLBACK_INFORMATION));
        stTravelingModeCallbackInfo.pstTravelDataInfo = *(ST_TRAVELING_CALLBACK_DATA_INFO *)pstTravelDataInfo;
        stTravelingModeCallbackInfo.enEngineType = enEngineType;

        m_pEvPreviewMode->Send(stTravelingModeCallbackInfo);
    }
}

BOOL MSrv_Control_common::_ProcessDipData(ST_TRAVELING_CALLBACK_DATA_INFO stTravelDataInfo, int enEngineType, const char *cFileName)
{
    PREVIEW_MODE_DEBUG_MSG("%s          %s()             %d\n", __FILE__, __FUNCTION__, __LINE__);
    PREVIEW_MODE_DEBUG_MSG("stTravelingCallbackDataInfo.u32FrameAddress0: %u\n", stTravelDataInfo.u32FrameAddress0);
    PREVIEW_MODE_DEBUG_MSG("stTravelingCallbackDataInfo.u32FrameAddress0: %u\n", stTravelDataInfo.u32FrameAddress1);

    MMapInfo_t * pMMAPinfo;
    pMMAPinfo = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID(PREVIEW_MODE_IP));

    PREVIEW_MODE_DEBUG_MSG("pMMAPinfo->u32Addr: %u\n", pMMAPinfo->u32Addr);
    PREVIEW_MODE_DEBUG_MSG("pMMAPinfo->u32Size: %u\n", pMMAPinfo->u32Size);
    PREVIEW_MODE_DEBUG_MSG("pMMAPinfo->u32Align: %u\n", pMMAPinfo->u32Align);
    PREVIEW_MODE_DEBUG_MSG("SIZE_OF_YUV_422_IMAGE: %d\n", SIZE_OF_YUV_422_IMAGE);

    //Check memory size is valid or not
    if(SIZE_OF_YUV_422_IMAGE > pMMAPinfo->u32Size)
    {
        PREVIEW_MODE_DEBUG_MSG("Memory size out of boundary!!!!!!!!!!!!!!!!!!!!!!!\n");
        return FALSE;
    }

    U32 u32PreviewModeMemoryVirtualAddress = 0;
    //Transfer physical address to virtual address
    u32PreviewModeMemoryVirtualAddress = _PA2VA(stTravelDataInfo.u32FrameAddress0);

    PREVIEW_MODE_DEBUG_MSG("u32PreviewModeMemoryVirtualAddress: %u\n", u32PreviewModeMemoryVirtualAddress);

    if(0 != u32PreviewModeMemoryVirtualAddress)//0 means that transfer fail
    {
        //Transfer YUV422 raw data to JPEG and save image
        WriteYUV422RawDataToJPEGFile((unsigned char *)u32PreviewModeMemoryVirtualAddress, 100, PREVIEW_MODE_WIDTH, PREVIEW_MODE_HEIGHT, cFileName);
        return TRUE;
    }
    else
    {
        PREVIEW_MODE_DEBUG_MSG("PA 2 VA transfer fail!!!!!!!!!!!!!!!!!!!!!!!\n");
        return FALSE;
    }

    return FALSE;
}

void MSrv_Control_common::TravelingModeEventCallBackFunc(void *pstTravelEventInfo, int enEngineType)
{
    PREVIEW_MODE_DEBUG_MSG("%s          %s()             %d\n", __FILE__, __FUNCTION__, __LINE__);
}
#endif//end of #if (PREVIEW_MODE_ENABLE == 1)

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: GetMuteFlag()
/// @brief \b Function \b Description: Get audio mute flag
/// @param <IN>        \b NONE
/// @return <OUT>      \b True: mute on, False: mute off
//-------------------------------------------------------------------------------------------------
MAPI_BOOL MSrv_Control_common::GetMuteFlag()
{
    return IsMuteFlag;
}


//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: SetMuteFlag()
/// @brief \b Function \b Description: Set audio mute flag
/// @param <IN>        \b True: mute on, False: mute off
/// @return <OUT>      \b NONE
//-------------------------------------------------------------------------------------------------
void MSrv_Control_common::SetMuteFlag(MAPI_BOOL IsMute)
{
    IsMuteFlag = IsMute;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: ToggleMute
/// @brief \b Function \b Description: Toggle audio mute
/// @param <IN>        \b NONE
/// @return <OUT>      \b SSSOUND_OK: Success
//-------------------------------------------------------------------------------------------------
MAPI_BOOL MSrv_Control_common::ToggleMute()
{
    PostEvent(0, EV_KEY, MVK_MUTE, 0);
    return TRUE;
}


//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: NotifyMute()
/// @brief \b Function \b Description: Notify audio mute
/// @return <OUT>      \b NONE
//-------------------------------------------------------------------------------------------------
void MSrv_Control_common::NotifyMute()
{
    PostEvent(0, EV_AUDIO_MUTE, 1, 0);
}


//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: NotifyUnmute()
/// @brief \b Function \b Description: Notify audio unmute
/// @return <OUT>      \b NONE
//-------------------------------------------------------------------------------------------------
void MSrv_Control_common::NotifyUnmute()
{
    PostEvent(0, EV_AUDIO_MUTE, 0, 0);
}


//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: APVolumeChange
/// @brief \b Function \b Description: Set audio volume change
/// @param <IN>        \b keycode : volume Keycode value
/// @return <OUT>      \b True: OK, False: Fail
//-------------------------------------------------------------------------------------------------
MAPI_BOOL MSrv_Control_common::APVolumeChange(U32 keycode)
{
    PostEvent(0, EV_KEY, keycode, 0);
    return TRUE;
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: APVolumeUp
/// @brief \b Function \b Description: Set audio volume up
/// @param <IN>        \b NONE
/// @return <OUT>      \b NONE
//-------------------------------------------------------------------------------------------------
void MSrv_Control_common::APVolumeUp()
{
    PostEvent(0, EV_KEY, MVK_VOLUME_PLUS, 0);
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: APVolumeDown
/// @brief \b Function \b Description: Set audio volume down
/// @param <IN>        \b NONE
/// @return <OUT>      \b NONE
//-------------------------------------------------------------------------------------------------
void MSrv_Control_common::APVolumeDown()
{
    PostEvent(0, EV_KEY, MVK_VOLUME_MINUS, 0);
}

//-------------------------------------------------------------------------------------------------
/// @brief \b Function \b Name: APSetVolume
/// @brief \b Function \b Description: Set audio volume
/// @param <IN>        \b u8Vol : volume value
/// @return <OUT>      \b NONE
//-------------------------------------------------------------------------------------------------
void MSrv_Control_common::APSetVolume(U8 u8Vol)
{
    PostEvent(0, EV_AP_SETVOLUME_EVENT, (U32)u8Vol, 0);
}

BOOL MSrv_Control_common::FinalizeDB()
{
    mapi_interface::FinalizeDB();
    return TRUE;
}

BOOL MSrv_Control_common::SetAudioMute(MAPI_U16 eAudioMuteType,
                                   MAPI_INPUT_SOURCE_TYPE eMapiSrcType)
{

    if(eMapiSrcType == MAPI_INPUT_SOURCE_NONE)  // set to current input source
    {
        eMapiSrcType = m_enCurrentFocusSource;
    }

    if((IsPipModeEnable()) &&
        ((eMapiSrcType != MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW)) &&
        (eMapiSrcType != MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_SUB_WINDOW))))
    {
        return FALSE;
    }

    if( false == IsPipModeEnable())
    {
        mapi_interface::Get_mapi_audio()->SetSoundMuteStatus((AUDIOMUTETYPE_)eAudioMuteType, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
    }
    else
    {
        if(eMapiSrcType == MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW))
        {
            mapi_interface::Get_mapi_audio()->SetSoundMuteStatus((AUDIOMUTETYPE_)eAudioMuteType, E_AUDIOMUTESOURCE_MAINSOURCE_);
        }
        else if(eMapiSrcType == MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_SUB_WINDOW))
        {
            mapi_interface::Get_mapi_audio()->SetSoundMuteStatus((AUDIOMUTETYPE_)eAudioMuteType, E_AUDIOMUTESOURCE_SUBSOURCE_);
        }
    }
    return TRUE;
}
void MSrv_Control_common::SetSystemInfo()
{
    MAPI_U16 u16SpiProjectID;
    MAPI_BOOL bSelectModelViaProjectID = SystemInfo::GetInstance()->Get_bSelectModelViaProjectID();
    if(bSelectModelViaProjectID == TRUE)
    {
        u16SpiProjectID = SystemInfo::GetInstance()->GetProjectId();
        SystemInfo::GetInstance()->Set_SpiProjectID(u16SpiProjectID);
    }
    SystemInfo::GetInstance()->SetSystemInfo();
}
void MSrv_Control_common::PostBuildSystem()
{
// EosTek Patch Begin
    MSrv_Control::GetInstance()->SetIRLedByOnOff(FALSE);
// EosTek Patch End
    // new system db
#if (ATSC_SYSTEM_ENABLE == 1)
    MSrv_System_Database_ATSC *pSysDB;
    pSysDB = MSrv_System_Database_ATSC::GetInstance();
    ASSERT(pSysDB);
#else
    MSrv_System_Database_DVB *pSysDB;
    pSysDB = MSrv_System_Database_DVB::GetInstance();
    ASSERT(pSysDB);
#endif
    m_pMSrvList[E_MSRV_SYSTEM_DATABASE] = pSysDB;

    pSysDB->IsDatabaseExist(EN_DB_TYPE_SYSTEM);
    pSysDB->preInit();

    InitHDMIEDIDInfoSet();

#if ((STB_ENABLE == 0) || (ENABLE_HDMI_RX == 1))
    mapi_hdmi_video::InitAllHPD();
#endif
}
void MSrv_Control_common::PreBuildSystem()
{
    STATIC_ASSERT((int)MSRV_TM_START_YEAR         == (int)TM_YEAR_BASE);

#if 1
    int rc;
    sigset_t mask;

    sigemptyset(&mask); /* Mask all allowed signals */
    sigaddset(&mask, SIGALRM);
    rc = pthread_sigmask(SIG_BLOCK, &mask, NULL);

    if (rc != 0) {
        printf("%d, %s\n", rc, strerror(rc));
        ASSERT(0);
    }


#endif


    if(mapi_interface::new_mapi_system() == MAPI_ERR_MEM_ALLOC)
    {
        MSRV_CONTROL_COMMON_ERR("%s:[%d]Failed to allocate system memory!\n", __PRETTY_FUNCTION__, __LINE__);
        ASSERT(0);
    }

    mapi_interface::CreateSystem();


    // Create and register customized mapi_video_customer,
    // which is a utiliy class being used by mapi_video and mapi_video_base
    mapi_video_customer * pVideo = mapi_video_customer::GetInstance();
    mapi_video_customer_base::SetCustomizedInstance(pVideo);

    mapi_audio_customer::GetInstance();

#if (AUTO_TEST == 1)
    printf("[AT][SN][init sysinfo][%u]\n",  mapi_time_utility::GetPiuTimer1());
#endif

    SetSystemInfo();

    // Create timing transform
    mapi_timing_transform_container_customer::GetInstance();

    mapi_pql_customer::GetInstance(MAPI_PQ_MAIN_WINDOW);
    mapi_pql_customer::GetInstance(MAPI_PQ_SUB_WINDOW);

    ::SetKEYPADCfg();

#if ((AUTO_TEST == 1) && (MSTAR_TVOS == 0))
#if (HSL_ENABLE ==1)

        FILE *cmdLine = fopen(CMDLINE_PATH, "r");
        char cmdLineBuf[CMDLINE_SIZE];
        mapi_hsl* pHsl = mapi_interface::Get_mapi_hsl();
        if(cmdLine != NULL)
        {
            fgets(cmdLineBuf, CMDLINE_SIZE, cmdLine);
            fclose(cmdLine);
            if (pHsl != NULL)
            {
                if(strstr(cmdLineBuf, KEYWORD_TO_AUTOTEST))
                {
                    if(strstr(cmdLineBuf, KEYWORD_TO_HSL))
                    {
                        if(!pHsl->InitHSL())
                        {
                            printf("HSL:Init HSL fail\n");
                        }
                    }
                }
            }
        }
        else
        {
            printf("\nAUTO_TEST is Enable, but read cmdline FAIL!!\n\n");
        }
#endif
#endif

#if 0
    if(!mapi_interface::Get_mapi_system()->CheckBootParameter())
    {
        IEnvManager* pEnvMan = IEnvManager::Instance();
        if (NULL!=pEnvMan)
        {
            printf("mboot&SN LX mis-match! It will try to reload env after reboot!!!\n");
            pEnvMan->SetEnv_Protect("force_env_reload", "1");
            pEnvMan->SaveEnv();
        }
        ASSERT(0);
    }
#endif

#if (ENABLE_LED_BREATH == 1)
    if (TRUE !=  mapi_interface::Get_mapi_system()->SetLedMode(LED_MODE_ON))
    {
        printf("FAIL LED_MODE_ON: [%s:%s:%d]\n", __FUNCTION__, __FILE__, __LINE__);
    }
#endif
}

#if (FREEVIEW_AU_ENABLE==1)
BOOL MSrv_Control_common::SaveOadWakeUpStatus()
{
    MS_USER_SYSTEM_SETTING system_setting;
    GetMSrvSystemDatabase()->GetUserSystemSetting(&system_setting);
    if (system_setting.fSoftwareUpdate && (system_setting.U8OADTime < 24))
    {
        struct tm timeinfo;
        U16 currMinutes, targetMinutes;
        MSrv_Timer* pTtimer;
        pTtimer  = GetMSrvTimer();

        MAPI_U32 u32PowerOnTime = mapi_interface::Get_mapi_system()->RTCGetCLK();

        pTtimer->GetCLKTime(timeinfo);
        currMinutes = (U16)((timeinfo.tm_hour * 60) + timeinfo.tm_min);
        targetMinutes = (U16)(system_setting.U8OADTime * 60);
        if (targetMinutes > currMinutes)
        {
            printf("OAD SCAN will start after %d minutes..........\n", (targetMinutes-currMinutes));
            u32PowerOnTime += (60*(targetMinutes-currMinutes));
        }
        else
        {
            // OAD Scan in next day
            printf("OAD SCAN will start after %d minutes..........\n", (1440+targetMinutes-currMinutes));
            u32PowerOnTime += (60*(1440+targetMinutes-currMinutes));
        }
        printf("Power ON Time is 0x%x .....\n", u32PowerOnTime);
        pTtimer->SetOADWakeUpTime(u32PowerOnTime);

        system_setting.fOADScanAfterWakeup = 1;
        GetMSrvSystemDatabase()->SetUserSystemSetting(&system_setting);
    }
    return TRUE;
}
#endif

BOOL MSrv_Control_common::Finalize()
{
    MSRV_CONTROL_COMMON_INFO("%s \n", __PRETTY_FUNCTION__);

    if(!m_bInit)
    {
        ASSERT(0);
        return FALSE;
    }

    if(MSrv_Control_common::m_monitor_st.m_bFlagThreadMonitorActive)
    {
        MSRV_CONTROL_COMMON_INFO("MSrv_Control_common::Start ThreadMonitor join...\n");
        void *thread_result;
        MSrv_Control_common::m_monitor_st.m_bFlagThreadMonitorActive = FALSE;

        if(m_pthreadMonitor_id != 0)
        {
            PTH_RET_CHK(pthread_join(m_pthreadMonitor_id, &thread_result));
            m_pthreadMonitor_id = 0;
        }
    }
#if (FRONTPNL_ENABLE == 1)
    void *thread_result;
    if(pthread_Keydetect_id != 0)
    {
        PTH_RET_CHK(pthread_join(pthread_Keydetect_id,&thread_result));
        pthread_Keydetect_id = 0;
    }

#endif //FRONTPNL_ENABLE


    PTH_RET_CHK(pthread_mutex_destroy(&m_MutexInputSrcSwitch));

    // destroy all srv in list
    for(int i = (E_MSRV_MAX - 1); i >= 0; i--)
    {
        if(m_pMSrvList[i] != NULL)
        {
            delete m_pMSrvList[i];
            m_pMSrvList[i] = NULL;
        }
    }

#if (CEC_ENABLE == 1)
    if (m_stCecThreadInfo.bActive== TRUE)
    {
        m_stCecThreadInfo.bActive = FALSE;
        if(m_CECThread != 0)
        {
            pthread_join(m_CECThread,NULL);
        }
    }
#endif

    delete m_pBootUpEvent;
    m_pBootUpEvent = NULL;

    // Destory mapi_timing_transform container.
    mapi_timing_transform_container::DestroyInstance();

    pthread_mutex_destroy(&m_MutexPostVideoInit);
    pthread_mutex_destroy(&m_MutexLock);
#if (ACTIVE_STANDBY_MODE_ENABLE == 1 )
    pthread_mutex_destroy(&m_MutexSetActiveStandby);
#endif
    delete m_pCmdAckEvt;
    m_pCmdAckEvt = NULL;
    delete m_pCmdEvt;
    m_pCmdEvt = NULL;
    delete m_pTVAsyncCmdEvt;
    m_pTVAsyncCmdEvt = NULL;
    m_bInit = FALSE;
    return TRUE;
}

void MSrv_Control_common::SetPowerOffFlag(MAPI_BOOL bEnable)///ac power off also use this flag -> dc_poweroff
{
#if (BOOT_FROM_ROM == 1)
    if (mapi_interface::Get_mapi_system()->GetBootSource() == EN_BOOT_FROM_EMBEDDED_ROM
        && IEnvManager::GetEnvType() == E_ENV_IN_NAND)
    {
        if (bEnable)
        {
            FILE *pPoweroffFile = fopen(FOLDER_FOR_POWEROFF_FLAG, "w");
            char cString[512];
            memset(cString, 0, sizeof(cString));
            sprintf(cString, "mark for dc poweroff\n");
            fwrite(cString, 1, sizeof(cString), pPoweroffFile);
            fclose(pPoweroffFile);
            pPoweroffFile = NULL;
        }
        else
        {
            remove(FOLDER_FOR_POWEROFF_FLAG);
        }
    }
    else
#endif
    {
        IEnvManager* pEnvMan = IEnvManager::Instance();
        if (pEnvMan)
        {
            MAPI_BOOL bOldValue = MAPI_FALSE;
            IEnvManager_scope_lock block(pEnvMan);
            int size = pEnvMan->QueryLength("dc_poweroff");
            char tmp[size+1];
            memset(tmp,0, size+1);
            pEnvMan->GetEnv_Protect("dc_poweroff", tmp, size);
            if (strncmp(tmp, "1", 1) == 0)
            {
                bOldValue = MAPI_TRUE;
            }

            if (bEnable != bOldValue)
            {
                if (bEnable)
                {
                    pEnvMan->SetEnv_Protect("dc_poweroff", "1");

                }
                else
                {
                    pEnvMan->SetEnv_Protect("dc_poweroff", "0");
                }
                pEnvMan->SaveEnv();
            }
        }
        else
        {
        #if (MSTAR_TVOS == 1)
            printf("can't get env manager\n");
        #else
            ASSERT(0);
        #endif
        }
    }
}
#if (MSTAR_TVOS == 1)&&(STR_ENABLE == 0)
static int remount_ro_done(void)
{
    FILE *f;
    char mount_dev[256];
    char mount_dir[256];
    char mount_type[256];
    char mount_opts[256];
    int mount_freq;
    int mount_passno;
    int match;
    int found_rw_fs = 0;

    f = fopen("/proc/mounts", "r");
    if (! f) {
        /* If we can't read /proc/mounts, just give up */
        return 1;
    }

    do {
        match = fscanf(f, "%255s %255s %255s %255s %d %d\n",
                       mount_dev, mount_dir, mount_type,
                       mount_opts, &mount_freq, &mount_passno);
        mount_dev[255] = 0;
        mount_dir[255] = 0;
        mount_type[255] = 0;
        mount_opts[255] = 0;
        if ((match == 6) && !strncmp(mount_dev, "/dev/block", 10) && strstr(mount_opts, "rw")) {
            found_rw_fs = 1;
            break;
        }
    } while (match != EOF);

    fclose(f);

    return !found_rw_fs;
}

static void remount_ro(void)
{
    int fd, cnt = 0;

    /* Trigger the remount of the filesystems as read-only,
     * which also marks them clean.
     */
    fd = open("/proc/sysrq-trigger", O_WRONLY);
    if (fd < 0) {
        return;
    }
    write(fd, "u", 1);
    close(fd);


    /* Now poll /proc/mounts till it's done */
    while (!remount_ro_done() && (cnt < 50)) {
        usleep(100000);
        cnt++;
    }

    return;
}
#endif
void MSrv_Control_common::EnterSleepMode(MAPI_BOOL bMode ,MAPI_BOOL bNoSignalPwD)
{
#if (ENABLE_LED_BREATH == 1)
    if (TRUE != mapi_interface::Get_mapi_system()->SetLedMode(LED_MODE_FLICKER_POWER, LED_FLICKER_FREQUENCY))
    {
        printf("FAIL LED_MODE_FLICKER: [%s:%s:%d]\n", __FUNCTION__, __FILE__, __LINE__);
    }
#endif

#if (ENABLE_NETREADY == 1)
    MSrv_DeviceAgent::GetDAInstance()->UserTriggerServicAbort();
#endif

#if (STR_ENABLE == 0)
#if (CI_PLUS_ENABLE == 1)
    if(FALSE == ((MSrv_DTV_Player_DVB*)(MSrv_Control::GetMSrvDtv()))->SetCuUpgradeReplyAnswerMode(E_CU_UPGRADE_REPLY_ANSWER_MODE_NO, TRUE))
    {
        return;
    }
#endif
#endif
#if (MSTAR_IPC == 1)
    APM_RequestToPowerDown();
#endif
#if (STR_ENABLE == 1)
    //move to suspend
#else
    U8  DcOffMode = TRUE;
#endif
    BOOL bRet = FALSE;

    // set heart beat flag for stopping watch dog
#if (STR_ENABLE == 1)
    // not necessary in STR case
#else
    m_bHeartBeatMonitor = FALSE;
#endif
    mapi_system *pSystem = mapi_interface::Get_mapi_system();
    ASSERT(pSystem != NULL);

    pSystem->SetWatchDogTimer(10);
    pSystem->RefreshWatchDog();
#if (STR_ENABLE == 0)
    //for STR, move to suspend
    GetMSrvTimer()->ReconfigTimerFromList();
#endif

    GetMSrvTimer()->StoreTimerDB();

#if (STB_ENABLE == 1)
    if(bMode == TRUE)
    {
        //Set XC video mute
        MSRV_CONTROL_COMMON_DBG("[%s]: Get_mapi_video()->SetVideoMute\n",__FUNCTION__);
        SetVideoMute(MAPI_TRUE);
        //Disable OSD
        MSystem *sys = MSystem::GetInstance();
        ASSERT(sys);
        if (sys->main_dfb_layer)
        {
            sys->main_dfb_layer->SetOpacity(sys->main_dfb_layer, 0x00);
        }
        if (sys->aux_dfb_layer)
        {
            sys->aux_dfb_layer->SetOpacity(sys->aux_dfb_layer, 0x00);
        }
        //Set audio decoder mute
        MSRV_CONTROL_COMMON_DBG("[%s]: Get_mapi_audio()->SetSoundMuteStatus\n",__FUNCTION__);
        mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_SIGNAL_UNSTABLE_MUTEON_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
    }
#endif

#if (TRAVELING_ENABLE == 1)
    EN_TRAVELING_ENGINE_TYPE enEngineType = E_TRAVELING_ENGINE_TYPE_SD;
    do
    {
        if(IsTravelingModeEnable(enEngineType) == TRUE)
        {
            StopTravelingMode(enEngineType);
        }
        enEngineType = (EN_TRAVELING_ENGINE_TYPE)((MAPI_U8)enEngineType+1);
    }while(enEngineType < E_TRAVELING_ENGINE_TYPE_MAX);
#endif

#if (PIP_ENABLE == 1)
    if(m_enPipMode != E_PIP_MODE_OFF)
    {
        SetVideoMute(MAPI_TRUE, 0,MAPI_SUB_WINDOW);
    }
#endif

#if (STR_ENABLE == 0)
    ((mapi_hdmi_video *)mapi_interface::Get_mapi_video(MAPI_INPUT_SOURCE_HDMI))->SetAllHPD(FALSE);
#endif

    // SW Mute first
    mapi_interface::Get_mapi_audio()->SetSoundMute(SOUND_MUTE_ALL_, E_MUTE_ON_);

    // Mute EarPhone
    MSrv_Control_common::SetEarPhoneByOnOff(FALSE);


    // HW mute then
    mapi_interface::Get_mapi_audio()->SetSoundMute(SOUND_MUTE_AMP_, E_MUTE_ON_);
#if (STR_ENABLE == 0)
    MSrv_Control::GetMSrvPicture()->SetBacklight_OnOff(FALSE);//obey panel off timing
#endif
// EosTek Patch Begin
    MSrv_Control::GetInstance()->SetIRLedByOnOff(TRUE);
// EosTek Patch End
// EosTek Patch Begin
//    bRet = MSrv_Control_common::SetGpioDeviceStatus(LED_ON, FALSE);
//    if (bRet == TRUE)
//    {
//        MSRV_CONTROL_COMMON_INFO("Turn off Led green !!\n");
//    }
// EosTek Patch End

#if (STR_ENABLE == 0)
#if (MHL_ENABLE == 1)
    //MSrv_Control::GetMSrvMHL()->CbusWakeupIntSetting();
    MSrv_Control::GetMSrvMHL()->PowerDown();
#endif
#endif

#if (FRONTPNL_ENABLE == 1)
    S32 s32OffsetTime = mapi_interface::Get_mapi_system()->GetClockOffset();/* 7Segment Front Panel GMT offset problem solved. */
    mapi_interface::SetOffsetTime2PM(s32OffsetTime);/* 7Segment Front Panel GMT offset problem solved. */
    mapi_frontpnl *m_pcFrontpnl;
    m_pcFrontpnl = mapi_interface::Get_mapi_frontpnl();
    if(m_pcFrontpnl != NULL)
    {
        m_pcFrontpnl->stMapifrontpnlFun.Frontpnl_Enter_Sleep();
    }
    else
    {
        printf("Do NOT have frontpnl device!\n");
        ASSERT(0);
    }
#else //FRONTPNL_ENABLE

    bRet = MSrv_Control_common::SetGpioDeviceStatus(POWER_RST, FALSE);
    if (bRet == TRUE)
    {
        MSRV_CONTROL_COMMON_INFO("Turn off Pw Reset !!\n");
    }
#endif //FRONTPNL_ENABLE

#if (STR_ENABLE == 1)
    //if STR enabled, this heavily time cost job is moved to MSrv_Control_common::Suspend
#else
    SetPowerOffFlag(MAPI_TRUE);
#endif

    if(bMode == TRUE)
    {
#if (STR_ENABLE == 0)
        // save system database befor sleep
        GetMSrvSystemDatabase()->SetDcPoweroffMode(DcOffMode); //For STR, move to MSrv_Control_common::Suspend
#endif
#if (STR_ENABLE == 0)
        FinalizeDB();
#endif
    }
#if (MSTAR_TVOS == 1)&&(STR_ENABLE == 0)
    sync();
    remount_ro();
#endif
    // set mailbox to PM
    mapi_interface::Get_mapi_system()->Standby_PreInit(bMode, m_bPmOnRam);

    // Initialize IR before power down
    mapi_interface::Get_mapi_system()->Load51_IR_Initialize(GetIR(), GetCrystalClkHz() / 10000000UL);

    // Initialize SAR before power down
    mapi_interface::Get_mapi_system()->Load51_KeyPad_Initialize(GetKeypadChInfo());

    if(bMode == TRUE)
    {
#if (FREEVIEW_AU_ENABLE==1)
        SaveOadWakeUpStatus();
#endif
    }

    PM_Cfg_t stPMCfg;
    memset(&stPMCfg, 0, sizeof(stPMCfg));

    //FIXME , need to get wake up key via system.cfg

    MAPI_U8 powerkey = 0xFF;
    powerkey = ::GetIRPowerupKey()->keys;
    if(powerkey == 0x00)  //if not set in config
        powerkey = 0xFF;

    MAPI_U8 u8PowerOnNetflixKey = 0xFF;
#if (NETFLIX_ENABLE==1)
    u8PowerOnNetflixKey = SystemInfo::GetInstance()->GetPowerOnNetflixKey();
#endif
    //EosTek Patch Begin
    MAPI_U8 pwkey = 0x45;
    MAPI_U8 u8PmWakeIRArray[] =
    {
        //IR wake-up key define
        pwkey, powerkey, u8PowerOnNetflixKey, 0xFF,  //IRKEY_POWER : 0x45
        0xFF, 0xFF, 0xFF, 0xFF,
        0xFF, 0xFF, 0xFF, 0xFF,
        0xFF, 0xFF, 0xFF, 0xFF,
        0xFF, 0xFF, 0xFF, 0xFF,
        0xFF, 0xFF, 0xFF, 0xFF,
        0xFF, 0xFF, 0xFF, 0xFF,
        0xFF, 0xFF, 0xFF, 0xFF
    };
    MAPI_U8 u8PmWakeIRArray2[] =
    {
        //IR wake-up key 2 define
        pwkey, powerkey, u8PowerOnNetflixKey, 0xFF,  //IRKEY_POWER : 0x45
        0xFF, 0xFF, 0xFF, 0xFF,
        0xFF, 0xFF, 0xFF, 0xFF,
        0xFF, 0xFF, 0xFF, 0xFF
    };
    //EosTek Patch End
    stPMCfg.stPMWakeCfg.bPmWakeEnableIR = TRUE;
    stPMCfg.stPMWakeCfg.bPmWakeEnableSAR = TRUE;
    stPMCfg.stPMWakeCfg.bPmWakeEnableGPIO0 = FALSE;
    stPMCfg.stPMWakeCfg.bPmWakeEnableGPIO1 = FALSE;
    stPMCfg.stPMWakeCfg.bPmWakeEnableUART1 = FALSE;

    if(bNoSignalPwD == TRUE)
    {
        stPMCfg.stPMWakeCfg.bPmWakeEnableSYNC = TRUE;
    }
    else
    {
        stPMCfg.stPMWakeCfg.bPmWakeEnableSYNC = FALSE;
    }

    stPMCfg.stPMWakeCfg.bPmWakeEnableESYNC = FALSE;
    stPMCfg.stPMWakeCfg.bPmWakeEnableRTC0 = TRUE;
    stPMCfg.stPMWakeCfg.bPmWakeEnableRTC1 = TRUE;

    if(bNoSignalPwD == TRUE)
    {
        stPMCfg.stPMWakeCfg.bPmWakeEnableDVI0 = TRUE;
    }
    else
    {
        stPMCfg.stPMWakeCfg.bPmWakeEnableDVI0 = FALSE;
    }

    stPMCfg.stPMWakeCfg.bPmWakeEnableDVI2 = FALSE;
    stPMCfg.stPMWakeCfg.bPmWakeEnableCEC = TRUE;
    stPMCfg.stPMWakeCfg.bPmWakeEnableAVLINK = FALSE;
#if (WOL_ENABLE == 1)

#if (MSTAR_TVOS == 1)
    stPMCfg.stPMWakeCfg.bPmWakeEnableWOL = GetMSrvFactoryMode()->GetWOLEnableStatus();
    if(stPMCfg.stPMWakeCfg.bPmWakeEnableWOL == 0)
    {
        printf("WOL Fuction is not working !!! bPmWakeEnableWOL = 0 !!!\n");
    }

    {
        IEnvManager* pEnvMan = IEnvManager::Instance();
        if((NULL != pEnvMan) && pEnvMan->Initialize())
        {
            IEnvManager_scope_lock block(pEnvMan);
            int MacTemp = 0;
            int size=pEnvMan->QueryLength("ethaddr");
            char tmp[size+1];
            memset(tmp,0,size+1);
            bool result=pEnvMan->GetEnv_Protect("ethaddr",tmp,size);
            printf("ethaddr = %s, get %d\n", tmp, result);
            char *pch = strtok(tmp, ":");
            if(result == 0)
            {
                MSRV_CONTROL_COMMON_ERR("%s:[%d]Failed to get ethaddr!\n", __PRETTY_FUNCTION__, __LINE__);
            }
            else
            {
                for(int iCnt = 0; iCnt < MAX_BUF_WAKE_MAC_ADDRESS; iCnt++)
                {
                string tmpStr = string(pch);

                if (tmpStr.empty())
                    {
                        ASSERT(0);
                    }
                sscanf(tmpStr.c_str(), "%x", &MacTemp);
                    stPMCfg.stPMWakeCfg.u8PmWakeMACAddress[iCnt] = (U8)MacTemp;
                    pch = strtok(NULL, ":");
                }
            }
        }
        else
        {
            MSRV_CONTROL_COMMON_ERR("---> IEnvManager Init Fail\n");
        }
    }
#else
    stPMCfg.stPMWakeCfg.bPmWakeEnableWOL = TRUE;

    msrv_network_mac_t mac;
    memset((void*)&mac, 0, sizeof(mac));

    S32 ret=E_MSRV_NETWORK_OK;
    ret= MSrv_Control::GetMSrvNetwork()->GetEthMacAddress(&mac);
    if(ret != E_MSRV_NETWORK_OK)
    {
        MSRV_CONTROL_COMMON_ERR("%s:[%d]Failed to get ethMac address!\n", __PRETTY_FUNCTION__, __LINE__);
    }
    else
    {
        for(int iCnt = 0; iCnt < MAX_BUF_WAKE_MAC_ADDRESS; iCnt++)
        {
            stPMCfg.stPMWakeCfg.u8PmWakeMACAddress[iCnt] = mac.macAddr[iCnt];
        }
    }
#endif
#else
    stPMCfg.stPMWakeCfg.bPmWakeEnableWOL = FALSE;
    for(int iCnt = 0; iCnt < MAX_BUF_WAKE_MAC_ADDRESS; iCnt++)
    {
        stPMCfg.stPMWakeCfg.u8PmWakeMACAddress[iCnt] = 0;
    }
#endif

    // E_STR_NONE is default when STR_ENABLE != 1
    stPMCfg.stPMWakeCfg.u8PmStrMode = E_STR_NONE;
#if 1
    stPMCfg.stPMWakeCfg.u8PmStrMode = E_STR_NORMAL;
    IEnvManager* pEnvMan = IEnvManager::Instance();
    if ((pEnvMan != NULL) && pEnvMan->Initialize())
    {
        IEnvManager_scope_lock block(pEnvMan);
        int size = pEnvMan->QueryLength("str_crc");
        if (size == 1)
        {
            char valueTemp[size + 1];
            int value = 0;
            memset(valueTemp, 0, size + 1);
            pEnvMan->GetEnv_Protect("str_crc", valueTemp, size);
            value = atoi(valueTemp);
            printf("get str_crc %d\n", value);
            stPMCfg.stPMWakeCfg.u8PmStrMode = value;
            if (value == E_STR_CRC)
            {
                MMapInfo_t* pMMap_LX = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_LX_MEM"));
                MMapInfo_t* pMMap_LX2 = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_LX_MEM2"));
                MMapInfo_t* pMMap_LX3 = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_LX_MEM3"));

                if(pMMap_LX != NULL)
                {
                    printf("LX_MEM for CRC %x,%x, miu%d\n",pMMap_LX->u32Addr,pMMap_LX->u32Size,(pMMap_LX->u32MiuNo ));
                    stPMCfg.stPMWakeCfg.bLxCRCMiu[0] = (pMMap_LX->u32MiuNo);
                    stPMCfg.stPMWakeCfg.u32LxCRCAddress[0] = pMMap_LX->u32Addr + 0x10000;
                    stPMCfg.stPMWakeCfg.u32LxCRCSize[0] = pMMap_LX->u32Size - 0x10000;
                }
                if(pMMap_LX2 != NULL)
                {
                    printf("LX_MEM2 for CRC %x,%x,miu%d\n",pMMap_LX2->u32Addr,pMMap_LX2->u32Size,(pMMap_LX2->u32MiuNo ));
                    stPMCfg.stPMWakeCfg.bLxCRCMiu[1] = (pMMap_LX2->u32MiuNo);
                    stPMCfg.stPMWakeCfg.u32LxCRCAddress[1] = pMMap_LX2->u32Addr;
                    stPMCfg.stPMWakeCfg.u32LxCRCSize[1] = pMMap_LX2->u32Size;
                }
                if(pMMap_LX3 != NULL)
                {
                    printf("LX_MEM3 for CRC %x,%x, miu%d\n",pMMap_LX3->u32Addr,pMMap_LX3->u32Size,(pMMap_LX3->u32MiuNo));
                    stPMCfg.stPMWakeCfg.bLxCRCMiu[2] = (pMMap_LX3->u32MiuNo);
                    stPMCfg.stPMWakeCfg.u32LxCRCAddress[2] = pMMap_LX3->u32Addr;
                    stPMCfg.stPMWakeCfg.u32LxCRCSize[2] = pMMap_LX3->u32Size;
                }
            }
        }
    }
#endif

#if (CEC_ENABLE == 1)
    MS_CEC_SETTING CEC_Setting;
    memset(&CEC_Setting, 0, sizeof(CEC_Setting));
    GetMSrvCEC()->CECTxInActiveSource();
    GetMSrvCEC()->CECStandby(1);
    GetMSrvCEC()->GetCECConfiguration(&CEC_Setting);

    //CEC wake up function default enable when CEC is worked
    if((CEC_Setting.u8CECStatus == TRUE)&&(CEC_Setting.u8TvAutpPowerOn == TRUE))
    {
        stPMCfg.stPMWakeCfg.bPmWakeEnableCEC = TRUE;
        mapi_interface::Get_mapi_cec()->CEC_ConfigWakeUp();
    }
    else
    {
        stPMCfg.stPMWakeCfg.bPmWakeEnableCEC = FALSE;
    }
#endif

    for(int iCnt = 0; iCnt < MAX_BUF_WAKE_IR; iCnt++)
    {
        stPMCfg.stPMWakeCfg.u8PmWakeIR[iCnt] = u8PmWakeIRArray[iCnt];
    }

    for(int iCnt = 0; iCnt < MAX_BUF_WAKE_IR2; iCnt++)
    {
        stPMCfg.stPMWakeCfg.u8PmWakeIR2[iCnt] = u8PmWakeIRArray2[iCnt];
    }

    if(m_bPmOnRam == TRUE) // PM51 runs on DRAM
    {
        stPMCfg.stPMPowerDownCfg.u8PowerDownMode = EN_PM_STANDBY;
    }
    else
    {
#if ((STR_ENABLE == 1) && (MSTAR_TVOS == 0))
        stPMCfg.stPMPowerDownCfg.u8PowerDownMode = EN_PM_STANDBY;
#else
        stPMCfg.stPMPowerDownCfg.u8PowerDownMode = EN_PM_SLEEP;
#endif
    }
    stPMCfg.stPMPowerDownCfg.u8WakeAddress = E_PM_LAST_TWOSTAGE_POWERDOWN;

    pSystem->RefreshWatchDog();

    // do power down flow, ex: set wake up key, sync, power off...

    MAPI_BOOL bTrigger = FALSE;
#if (ACTIVE_STANDBY_MODE_ENABLE == 1)
    bTrigger = GetMSrvTimer()->IsActiveStandbyModeNeedTrigger();
    #if (OAD_ENABLE == 1)
    MSrv_DTV_Player_DVB* pDvbPlayer = dynamic_cast<MSrv_DTV_Player_DVB*>(MSrv_Control::GetMSrvDtv());
    if(pDvbPlayer && pDvbPlayer->ShouldDownloadInStandby())
    {
        MSrv_Control::GetMSrvTimer()->SetOADWakeUpTime(mapi_interface::Get_mapi_system()->RTCGetCLK()+20);
        bTrigger = TRUE;
    }
    #endif
#endif
    mapi_interface::Get_mapi_system()->PowerDown(&stPMCfg,bTrigger);
}


EN_POWER_ON_MODE MSrv_Control_common::QueryPowerOnMode(void)
{
    return mapi_interface::Get_mapi_system()->QueryPowerOnMode();
}

EN_WAKEUP_SOURCE MSrv_Control_common::QueryWakeupSource(void)
{
    return mapi_interface::Get_mapi_system()->QueryWakeupSource();
}

BOOL MSrv_Control_common::IsWakeUpByRTC(void)
{
    return mapi_interface::Get_mapi_system()->IsWakeUpByRTC();
}

const S_TV_TYPE_INFO & MSrv_Control_common::GetTVInfo(void)
{
    return SystemInfo::GetInstance()->GetTVInfo();
}

MAPI_U8 MSrv_Control_common::GetRouteTVMode(U8 u8RouteIndex)
{
    return SystemInfo::GetInstance()->GetRouteTVMode(u8RouteIndex);
}

static BOOL CompareDTVRouteWithTVMode(U8 u8RouteIndex, EN_TV_ROUTE_TYPE enRouteType)
{
    switch (enRouteType)
    {
        case E_ROUTE_DVBT:
        case E_ROUTE_DVBT2:
            if ((MSrv_Control::GetRouteTVMode(u8RouteIndex) == E_ROUTE_DVBT) || (MSrv_Control::GetRouteTVMode(u8RouteIndex) == E_ROUTE_DVBT2))
            {
                return TRUE;
            }
            break;
        case E_ROUTE_DVBS:
        case E_ROUTE_DVBS2:
            if ((MSrv_Control::GetRouteTVMode(u8RouteIndex) == E_ROUTE_DVBS) || (MSrv_Control::GetRouteTVMode(u8RouteIndex) == E_ROUTE_DVBS2))
            {
                return TRUE;
            }
            break;
        case E_ROUTE_DVBC:
            if(MSrv_Control::GetRouteTVMode(u8RouteIndex) == E_ROUTE_DVBC)
            {
                return TRUE;
            }
            break;
        default:
            if (MSrv_Control::GetRouteTVMode(u8RouteIndex) == enRouteType)
            {
                return TRUE;
            }
            break;
    }
    return FALSE;
}

MAPI_U8 MSrv_Control_common::GetRouteIndexByTVMode(EN_TV_ROUTE_TYPE routeType)
{
#if (TWIN_TUNER == 1)
    ST_PLAYER_STATE stPlayerState;
    MSrv_PlayerControl::GetInstance()->GetPlayerState(stPlayerState);

    //First priority, idle player
    for (MAPI_U8 i = 0; i < MSrv_PlayerControl::GetInstance()->GetDtvRouteCount(); i++)
    {
        if (MSrv_PlayerControl::GetInstance()->CheckDtvRouteInForeground(stPlayerState, i) == TRUE)
        {
            continue;
        }

        MSrv_DTV_Player_DVB *pDTVPlayer = dynamic_cast<MSrv_DTV_Player_DVB*>(MSrv_Control::GetInstance()->GetMSrvDtvByIndex(i));
        ASSERT(pDTVPlayer);

        if (pDTVPlayer->IsRecording() == TRUE)
        {
            continue;
        }

        if (CompareDTVRouteWithTVMode(i, routeType) == TRUE)
        {
            return i;
        }
    }

    //Secode priority, pvr recording player
    while (!stPlayerState.v_PVRRoute.empty())
    {
        MAPI_U8 i = stPlayerState.v_PVRRoute.back();
        stPlayerState.v_PVRRoute.pop_back();

        if (CompareDTVRouteWithTVMode(i, routeType) == TRUE)
        {
            return i;
        }
    }
#else
    for (MAPI_U8 i = 0; i < MSrv_PlayerControl::GetInstance()->GetDtvRouteCount(); i++)
    {
        if (CompareDTVRouteWithTVMode(i, routeType) == TRUE)
        {
            return i;
        }
    }
#endif
    return UNUSED_DTV_ROUTE;
}

#if (ENABLE_LITE_SN != 1)
MAPI_U8 MSrv_Control_common::GetATVSystemType(void)
{
    return SystemInfo::GetInstance()->get_ATVSystemType();
}

MAPI_U8 MSrv_Control_common::GetDTVSystemType(void)
{
    return SystemInfo::GetInstance()->get_DTV_SystemType();
}

MAPI_U8 MSrv_Control_common::GetSTBSystemType(void)
{
    return SystemInfo::GetInstance()->get_STB_SystemType();
}
#endif

MAPI_BOOL MSrv_Control_common::IsSupportTheDTVSystemType(MAPI_U8 u8Type)
{
    return SystemInfo::GetInstance()->IsSupportTheDTVSystemType(u8Type);
}

#if (ENABLE_LITE_SN != 1)
void MSrv_Control_common::BypassScart()
{
    mapi_interface::Get_mapi_display()->BypassScart();
}

void MSrv_Control_common::SetDisplayTiming(mapi_display_datatype::EN_DISPLAY_RES_TYPE enDisplayRes)
{
    mapi_interface::Get_mapi_display()->SetDisplayTiming(enDisplayRes);
}
#endif

MAPI_U32 MSrv_Control_common::RTCGetCLK()
{
    return mapi_interface::Get_mapi_system()->RTCGetCLK();
}

MAPI_S32 MSrv_Control_common::GetClockOffset(void)
{
    return mapi_interface::Get_mapi_system()->GetClockOffset();
}

MAPI_INPUT_SOURCE_TYPE MSrv_Control_common::GetCurrentInputSource(void)
{
    return m_enCurrentFocusSource;
}

MSrv_Player* MSrv_Control_common::GetCurrentFocusPlayer(void)
{
    switch (m_enCurrentFocusSource)
    {
        case MAPI_INPUT_SOURCE_DTV:
            {
                U8 dtvRoute = MSrv_PlayerControl::GetInstance()->GetCurrentDtvRoute(MAPI_MAIN_WINDOW);
                return dynamic_cast<MSrv_Player*> (MSrv_PlayerControl::GetInstance()->GetMSrvPlayer(EN_MSRV_PLAYER_LIST(E_MSRV_DTV_PLAYER_0 + dtvRoute)));
            }
            break;
        case MAPI_INPUT_SOURCE_DTV2:
            {
                U8 dtvRoute = MSrv_PlayerControl::GetInstance()->GetCurrentDtvRoute(MAPI_SUB_WINDOW);
                return dynamic_cast<MSrv_Player*> (MSrv_PlayerControl::GetInstance()->GetMSrvPlayer(EN_MSRV_PLAYER_LIST(E_MSRV_DTV_PLAYER_0 + dtvRoute)));
            }
            break;
        case MAPI_INPUT_SOURCE_ATV:
            return dynamic_cast<MSrv_Player*> (MSrv_PlayerControl::GetInstance()->GetMSrvPlayer(E_MSRV_ATV_PLAYER));
            break;
        case MAPI_INPUT_SOURCE_SCART:
            return dynamic_cast<MSrv_Player*> (MSrv_PlayerControl::GetInstance()->GetMSrvPlayer(E_MSRV_SCART_PLAYER));
            break;
        case MAPI_INPUT_SOURCE_CVBS:
            return dynamic_cast<MSrv_Player*> (MSrv_PlayerControl::GetInstance()->GetMSrvPlayer(E_MSRV_AV_PLAYER));
            break;
        case MAPI_INPUT_SOURCE_SVIDEO:
            return dynamic_cast<MSrv_Player*> (MSrv_PlayerControl::GetInstance()->GetMSrvPlayer(E_MSRV_SV_PLAYER));
            break;
        case MAPI_INPUT_SOURCE_YPBPR:
            return dynamic_cast<MSrv_Player*> (MSrv_PlayerControl::GetInstance()->GetMSrvPlayer(E_MSRV_COMPONENT_PLAYER));
            break;
        case MAPI_INPUT_SOURCE_HDMI:
            return dynamic_cast<MSrv_Player*> (MSrv_PlayerControl::GetInstance()->GetMSrvPlayer(E_MSRV_HDMI_PLAYER));
            break;
        case MAPI_INPUT_SOURCE_VGA:
            return dynamic_cast<MSrv_Player*> (MSrv_PlayerControl::GetInstance()->GetMSrvPlayer(E_MSRV_VGA_PLAYER));
            break;
        case MAPI_INPUT_SOURCE_STORAGE:
            return dynamic_cast<MSrv_Player*> (MSrv_PlayerControl::GetInstance()->GetMSrvPlayer(E_MSRV_STORAGE_PLAYER));
            break;
        case MAPI_INPUT_SOURCE_RVU:
            return dynamic_cast<MSrv_Player*> (MSrv_PlayerControl::GetInstance()->GetMSrvPlayer(E_MSRV_RVU_PLAYER));
            break;
        default:
            ASSERT(0);
            break;
    }
    return NULL;
}

MAPI_INPUT_SOURCE_TYPE MSrv_Control_common::GetCurrentMainInputSource(void)
{
    return MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW);
}

MAPI_INPUT_SOURCE_TYPE MSrv_Control_common::GetCurrentSubInputSource(void)
{
    return MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_SUB_WINDOW);
}

MAPI_BOOL  MSrv_Control_common::CheckCurrentInputSource(MAPI_INPUT_SOURCE_TYPE eInputSrcType )
{
    if(eInputSrcType >= MAPI_INPUT_SOURCE_NUM )
    {
        MSRV_CONTROL_COMMON_ERR("InputSource Type(%u) is incorrect.\n", (MAPI_U32)eInputSrcType);
        return MAPI_FALSE;
    }

#if (PIP_ENABLE == 1)
    if ((eInputSrcType != GetCurrentMainInputSource())  &&
        (eInputSrcType != GetCurrentSubInputSource()))
#else
    if (eInputSrcType !=GetCurrentInputSource())
#endif
    {
        MSRV_CONTROL_COMMON_ERR("InputSource Type(%u) doesn't match Sub(%d)/Main(%d) input source.\n", (MAPI_U32)eInputSrcType, GetCurrentSubInputSource(),GetCurrentMainInputSource());
        return MAPI_FALSE;
    }

    return MAPI_TRUE;
}
MAPI_BOOL MSrv_Control_common::setVideoMuteColor(mapi_video_datatype::MAPI_VIDEO_Screen_Mute_Color enColor, EN_MUTE_ENGINE engine)
{
    MS_BOOT_SETTING value;
    MAPI_BOOL isModify = MAPI_FALSE;

    if(enColor >= mapi_video_datatype::E_SCREEN_MUTE_NUMBER)
    {
        MSRV_CONTROL_COMMON_ERR("Value is overspec [color:%d]\n", (U32)enColor);
        return MAPI_FALSE;
    }

    GetMSrvSystemDatabase()->GetBootSetting(&value);

    if(engine == ENGINE_XC)
    {
        if(value.enMuteColor != (U32)enColor)
        {
            value.enMuteColor = enColor;
            isModify = MAPI_TRUE;
        }
    }

    if(engine == ENGINE_BACKEND)
    {
        if(value.enBackendColor != (U32)enColor)
        {
            value.enBackendColor = (U32)enColor;
            isModify = MAPI_TRUE;
        }
    }

    if(isModify)
    {
        // notify MSDK and database
        mapi_interface::Get_mapi_video(GetCurrentInputSource())->setMuteColor(enColor, (E_MUTE_ENGINE)engine);
        GetMSrvSystemDatabase()->SetBootSetting(&value);
    }else{
        MSRV_CONTROL_COMMON_ERR("[Backend] the same, so don't change [%d:%d:%d]\n", (U32)enColor, value.enMuteColor, value.enBackendColor);
    }

    return MAPI_TRUE;
}
MAPI_BOOL MSrv_Control_common::setVideoFrameColor(mapi_video_datatype::MAPI_VIDEO_Screen_Mute_Color enColor, EN_MUTE_ENGINE engine)
{
    MS_BOOT_SETTING value;

    if(enColor >= mapi_video_datatype::E_SCREEN_MUTE_NUMBER)
    {
        MSRV_CONTROL_COMMON_ERR("Value is overspec [color:%d]\n", (U32)enColor);
        return MAPI_FALSE;
    }

    GetMSrvSystemDatabase()->GetBootSetting(&value);

    if(value.enFrameColor != (U32)enColor)
    {
        value.enFrameColor = (U32)enColor;
        // notify MSDK and database
        mapi_interface::Get_mapi_video(GetCurrentInputSource())->setFrameColor(enColor);
        GetMSrvSystemDatabase()->SetBootSetting(&value);
    }else{
        MSRV_CONTROL_COMMON_ERR("the same so don't change[%d:%d]\n", (U32)enColor, value.enFrameColor);
    }
    return MAPI_TRUE;
}
MAPI_SCALER_WIN MSrv_Control_common::InputSource2Win(MAPI_INPUT_SOURCE_TYPE src)
{
    MAPI_SCALER_WIN eWIN = MAPI_MAIN_WINDOW;
    if(CheckCurrentInputSource(src) == MAPI_FALSE)
    {
        MSRV_CONTROL_COMMON_ERR("InputSource Type(%u) is invalid.\n", (MAPI_U32)src);
        return MAPI_MAX_WINDOW;
    }
#if (PIP_ENABLE == 1)
    if(src == GetCurrentSubInputSource())
    {
        eWIN = MAPI_SUB_WINDOW;
    }
    else
    {
        eWIN = MAPI_MAIN_WINDOW;
    }
#endif
    return eWIN;
}
MAPI_INPUT_SOURCE_TYPE MSrv_Control_common::Win2InputSource(MAPI_SCALER_WIN eWIN)
{
    MAPI_INPUT_SOURCE_TYPE  eMapiSrcType = MAPI_INPUT_SOURCE_NONE;
    int count = 0;
#if (PIP_ENABLE == 1)
    if(eWIN == MAPI_MAIN_WINDOW)
    {
        eMapiSrcType = GetCurrentMainInputSource();
    }
    else if(eWIN == MAPI_SUB_WINDOW)
    {
        eMapiSrcType = GetCurrentSubInputSource();
    }
#else
    eMapiSrcType = GetCurrentInputSource();
#endif

    if(CheckCurrentInputSource(eMapiSrcType) == MAPI_FALSE)
    {
        do{
            MSRV_CONTROL_COMMON_ERR("check current input source is failed, retry %d times[%u](Win:%d)\n",
                                                                        count,(MAPI_U32)eMapiSrcType,eWIN);
            if(eWIN == MAPI_SUB_WINDOW)
                eMapiSrcType = GetCurrentSubInputSource();
            else
                eMapiSrcType = GetCurrentInputSource();
            count++;
            usleep(10*1000);
        }while((CheckCurrentInputSource(eMapiSrcType) == MAPI_FALSE) && (count < 10));

        if(count >= 10)
        {
            MSRV_CONTROL_COMMON_ERR("InputSource Type(%u) is invalid. (Win:%d)\n", (MAPI_U32)eMapiSrcType, eWIN);
            return MAPI_INPUT_SOURCE_NONE;
        }
    }
    return eMapiSrcType;
}
MAPI_BOOL MSrv_Control_common::GetVideoMute(MAPI_SCALER_WIN eWIN , EN_MUTE_ENGINE engine)
{
    MAPI_BOOL ret = MAPI_FALSE;
    MAPI_INPUT_SOURCE_TYPE eInputSrcType = GetCurrentInputSource();
#if (PIP_ENABLE == 1)
    if(eWIN == MAPI_SUB_WINDOW)
    {
        eInputSrcType = GetCurrentSubInputSource();
    }
#endif
    if(mapi_interface::Get_mapi_video(eInputSrcType)->getMuteFlag((E_MUTE_ENGINE)engine,eWIN) > 0)
    {
        ret = MAPI_TRUE;
    }

    return ret;
}
MAPI_BOOL MSrv_Control_common::SetVideoMuteFromAPP(MAPI_BOOL bVideoMute, MAPI_INPUT_SOURCE_TYPE eInputSrcType, EN_MUTE_ENGINE enMuteEngine,...)
{
    MAPI_BOOL ret = MAPI_FALSE;
#if 0 // if we need ext information
    va_list ext_para;
    va_start(ext_para, eWIN);
    double para1 = va_arg(ext_para, double /* parameter type */);
    int para2 = va_arg(ext_para, int /* parameter type */);
    printf("%lf %d\n",para1,para2);
    va_end(ext_para);
#endif
    if(eInputSrcType == MAPI_INPUT_SOURCE_NONE)
    {
#if (PIP_ENABLE == 1)
        eInputSrcType = GetCurrentMainInputSource();
#else
        eInputSrcType = GetCurrentInputSource();
#endif
    }
    else
    {
        if(CheckCurrentInputSource(eInputSrcType) == MAPI_FALSE)
        {
            MSRV_CONTROL_COMMON_ERR("InputSource Type is invalid.\n");
            return MAPI_FALSE;
        }
    }

    if(enMuteEngine == ENGINE_BACKEND)
    {
        printf("---> Backend mute\n");
        ret = SetVideoMute(bVideoMute, 0, MAPI_MAIN_WINDOW, ENGINE_BACKEND);
    }
    else
    {
        ret = mapi_interface::Get_mapi_video(eInputSrcType)->LockMuteStatus(bVideoMute , MUTE_LOCK_AP);
    }

    return ret;
}
MAPI_BOOL MSrv_Control_common::SetVideoMute(MAPI_BOOL bVideoMute , mapi_video_datatype::MAPI_VIDEO_Screen_Mute_Color enColor, MAPI_U16 u16VideoUnMuteTime, MAPI_INPUT_SOURCE_TYPE eMapiSrcType)
{
    MSRV_CONTROL_COMMON_ERR("This API will be removed in next version, please reduce use it. thanks!!\n");
    MSRV_CONTROL_COMMON_ERR("[enable:%d] [eMapiSrcType: %d]\n",bVideoMute, eMapiSrcType);
    MAPI_BOOL ret = MAPI_FALSE;

    if( CheckCurrentInputSource(eMapiSrcType) == MAPI_FALSE)
    {
        MSRV_CONTROL_COMMON_ERR("InputSource to window is failed.\n");
        return MAPI_FALSE;
    }

    setVideoMuteColor(enColor);
    ret = SetVideoMuteFromAPP(bVideoMute, eMapiSrcType);

    if(ret == MAPI_FALSE)
    {
        MSRV_CONTROL_COMMON_ERR("OLD API call NEW API is failed\n");
    }

    return ret;
}
MAPI_BOOL MSrv_Control_common::SetVideoMute(MAPI_BOOL bVideoMute , MAPI_U16 u16VideoUnMuteTime, MAPI_SCALER_WIN eWIN, EN_MUTE_ENGINE engine)
{
    MAPI_INPUT_SOURCE_TYPE eMapiSrcType = MAPI_INPUT_SOURCE_NONE;
    MAPI_BOOL ret = MAPI_FALSE;
    eMapiSrcType = Win2InputSource(eWIN);


    if(eMapiSrcType == MAPI_INPUT_SOURCE_NONE)
    {
        MSRV_CONTROL_COMMON_ERR("InputSource Type is invalid.\n");
        return MAPI_FALSE;
    }

    ret = _SetVideoMute(bVideoMute, u16VideoUnMuteTime, eMapiSrcType , engine);

    if( ret == MAPI_FALSE)
    {
        MSRV_CONTROL_COMMON_ERR("_SetVideoMute failed.\n");
    }
    if ((FALSE == bVideoMute) && (ret == MAPI_TRUE))
    {
#if (AUTO_TEST == 1)
        printf("[AUTO_TEST][source change]: Unmute [%u]\n", mapi_time_utility::GetTime0());
        printf("[AUTO_TEST][source change]: Unmute \n");
        printf("[AT][SN][unmute][%u]\n",  mapi_time_utility::GetPiuTimer1());
        printf("[AT][T_SN][unmute][%u]\n",  mapi_time_utility::GetTime0());
#endif
        SendBootUpEvent();
    }
    return ret;
}
#define _NONE_      "\033[0m\n"
#define _GREEN_     "\033[0;32;32m[MSG]"
#define _YELLOW_    "\033[1;33m[WAR]"
#define _RED_       "\033[0;32;31m[ERR]"

#define PC_MSG(fmt, arg...)    //printf((char *)_GREEN_ fmt _NONE_, ##arg)
#define PC_WAR(fmt, arg...)    //printf((char *)_YELLOW_ fmt _NONE_, ##arg)
#define PC_ERR(fmt, arg...)    //printf((char *)_RED_ fmt _NONE_, ##arg)

#if(PIP_ENABLE == 1)
void MSrv_Control_common::SetPipDisplayFocusWindow(MAPI_SCALER_WIN eWin)
{
    if( (IsPipModeEnable() == FALSE) && (eWin == MAPI_SUB_WINDOW))
    {
        ASSERT(0);
    }

    if ((MAPI_MAIN_WINDOW == eWin) || (MAPI_SUB_WINDOW == eWin))
    {
        m_enCurrentFocusSource = MSrv_PlayerControl::GetInstance()->GetPIPSourceType(eWin);
    }
    else
    {
       ASSERT(0);
    }
}
#endif

BOOL MSrv_Control_common::SetInputSource(MAPI_INPUT_SOURCE_TYPE eInputSrc, BOOL bWriteDB, BOOL bUpdateLock, BOOL bLock, MAPI_SCALER_WIN eWin, BOOL bForceSet)
{
#if(PIP_ENABLE == 1)
    //Get current player state
    ST_PLAYER_STATE stPlayerState;
    MSrv_PlayerControl::GetInstance()->GetPlayerState(stPlayerState);

    //Set the change input source
    stPlayerState.v_PIPSource[eWin] = eInputSrc;

    //Check the change result is conflict
    EN_CONFLICT_TYPE retType = MSrv_PlayerControl::GetInstance()->CheckConflict(stPlayerState);
    if (retType != EN_CONFLICT_NONE)
    {
        if (bForceSet == FALSE)
        {
            MSrv_Control::GetInstance()->PostEvent(NULL, EV_INPUT_SOURCE_CONFLICT, 0, 0);
            return FALSE;
        }

        if (retType != EN_CONFLICT_BACKGROUND)
        {
            if (GetPipMode() == E_PIP_MODE_PIP)
            {
                DisablePip();
            }
        }
    }
#endif

    MSRV_CMD stCmd;
    BOOL bRet = TRUE;

    memset(&stCmd, 0, sizeof(MSRV_CMD));

    if (m_bInit == FALSE)
    {
        return MAPI_INPUT_SOURCE_NONE;
    }

#if (TRAVELING_ENABLE == 1)
    if((MAPI_MAIN_WINDOW == eWin) && (MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_SUB_WINDOW) == eInputSrc))
    {
        MAPI_U16 enEngineType = E_TRAVELING_ENGINE_TYPE_SD;

        for (; enEngineType < E_TRAVELING_ENGINE_TYPE_MAX; enEngineType++)
        {
            if(m_stTravelModeInfo[enEngineType].enTravelingSource == E_TRAVELING_SUB_WINDOW)
            {
                ST_TRAVELING_CALLBACK_EVENT_INFO stEventInfo;
                memset((void*)&stEventInfo, 0, sizeof(ST_TRAVELING_CALLBACK_EVENT_INFO));
                stEventInfo.enMsgType = E_TRAVELING_EVENT_CALLBACK_UNSUPPORT; //Notify APP to stop or restart traveling
                stEventInfo.u16TravelEventInfo_Length = sizeof(ST_TRAVELING_CALLBACK_EVENT_INFO);
                stEventInfo.u32TravelEventInfo_Version = TRAVELING_EVENT_INFO_MSDK_VERSION;

                MSrvTravelingEventCallback((void*)&stEventInfo, enEngineType);
                FinalizeTravelingMode((EN_TRAVELING_ENGINE_TYPE)enEngineType);
            }
        }
    }
#endif

    if (MSrv_PlayerControl::GetInstance()->GetPIPSourceType(eWin) == eInputSrc)
    {
        MSRV_CONTROL_COMMON_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
        return TRUE;
    }

    selectAdcTableByCalibrationMode(eInputSrc);

#ifdef TARGET_BUILD
    if(eInputSrc == MAPI_INPUT_SOURCE_DTV)
    {
        PostEvent(0, EV_DTV_READY_POPUP_DIALOG, 0);  //resume IdleAppFrame DTV event recipient
    }
    else
    {
        PostEvent(0, EV_DTV_READY_POPUP_DIALOG, 1);  //pause IdleAppFrame DTV event recipient
    }
#endif
// EosTek Patch Begin
    if((eInputSrc == MAPI_INPUT_SOURCE_ATV)||(eInputSrc == MAPI_INPUT_SOURCE_DTV))
    {
    	mapi_gpio *gptr = mapi_gpio::GetGPIO_Dev(HTV_TUNER_SWITCH);
		if (NULL != gptr)
		{
	    	if (eInputSrc == MAPI_INPUT_SOURCE_DTV)
			{
				gptr->SetOn();
				printf("SetInputSource DTV set on\n");
			}
			else
			{
				gptr->SetOff();
				printf("SetInputSource ATV set off\n");
			}
		}
    }
// EosTek Patch End
    mapi_scope_lock(scopeLock, &m_MutexLock);
    if(m_block && (!bUpdateLock))
    {
        //printf("SetInputSource is lock\n");
        return FALSE;
    }
    if(bUpdateLock)
    {
        m_block=bLock;
    }
    GetMSrvTimer()->DisableOffMode(OFF_MODE_AUTOSLEEP);

#if (OFL_DET == 1)
    //setinput source is ready to go, offline detection should wait
    if(m_bBootupInit)
        MSrv_Control::GetMSrvSourceDetect()->WaitPause(TRUE);
#endif

#if 0//(MHL_ENABLE == 1)
//set up before switch input source
    if(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW) != HDMI_PORT_FOR_MHL)
    {
        if((eInputSrc == HDMI_PORT_FOR_MHL) && MSrv_Control::GetMSrvMHL()->CableDetect())
        {
            MSrv_Control::GetMSrvMHL()->CbusIsolate();
        }
    }
#endif

#if (ENABLE_BACKEND == 1)
    MSrv_Control::GetMSrvBackend()->SetMfcBypass();
#endif

#if (RVU_ENABLE == 1)
    if(eInputSrc == MAPI_INPUT_SOURCE_RVU)
    {
        mapi_interface::Get_mapi_audio()->DECODER_SwitchAudioDSPSystem(E_AUDIO_DSP_MPEG_);
    }
#endif

    // if it is the thread itself, use function call directly
    if (m_pthreadMonitor_id == pthread_self())
    {
        bRet = SetInputSourceCmd(eInputSrc, bWriteDB, eWin);
    }
    else if(FALSE == MSrv_Control_common::m_monitor_st.m_bFlagThreadMonitorActive)
    {
        // when threadmonitor is inactive, use function call directly
        bRet = SetInputSourceCmd(eInputSrc, bWriteDB, eWin);
    }
    else
    {
        m_bSourceChange = TRUE;
        stCmd.enCmd = E_CMD_SET_CURRENT_INPUT_SOURCE;
        stCmd.u32Param1 = (U32)eInputSrc;
        stCmd.u32Param2 = (U32)bWriteDB;
        stCmd.u32Param3 = eWin;
        m_pCmdEvt->Send(stCmd);
        m_pCmdAckEvt->Wait(&stCmd);
        m_bSourceChange = FALSE;
        bRet = TRUE;
    }

#if (OFL_DET == 1)
    //setinput source is done, offline detection could go
    if(m_bBootupInit)
        MSrv_Control::GetMSrvSourceDetect()->WaitPause(FALSE);
#endif

    return bRet;
}

#if (PIP_ENABLE == 1)
BOOL MSrv_Control_common::SetPIPSubInputSource(EN_PIP_SUB_SOURCE_LIST eSubSource, const mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstDispWin, BOOL bForceSet)
{
    MAPI_INPUT_SOURCE_TYPE enSubInputSrc = MAPI_INPUT_SOURCE_NONE;
    EN_TV_ROUTE_TYPE wantChangeRouteType = E_ROUTE_NONE;

    switch (eSubSource)
    {
        case EN_PIP_SUB_SOURCE_NONE:
            break;
        case EN_PIP_SUB_SOURCE_DVBT:
            wantChangeRouteType = E_ROUTE_DVBT;
            enSubInputSrc = MAPI_INPUT_SOURCE_DTV2;
            break;
        case EN_PIP_SUB_SOURCE_DVBC:
            wantChangeRouteType = E_ROUTE_DVBC;
            enSubInputSrc = MAPI_INPUT_SOURCE_DTV2;
            break;
        case EN_PIP_SUB_SOURCE_DVBS:
            wantChangeRouteType = E_ROUTE_DVBS;
            enSubInputSrc = MAPI_INPUT_SOURCE_DTV2;
            break;
        case EN_PIP_SUB_SOURCE_ISDB:
            wantChangeRouteType = E_ROUTE_ISDB;
            enSubInputSrc = MAPI_INPUT_SOURCE_DTV2;
            break;
        case EN_PIP_SUB_SOURCE_ATV:
            enSubInputSrc = MAPI_INPUT_SOURCE_ATV;
            break;
        case EN_PIP_SUB_SOURCE_SCART:
            enSubInputSrc = MAPI_INPUT_SOURCE_SCART;
            break;
        case EN_PIP_SUB_SOURCE_SCART2:
            enSubInputSrc = MAPI_INPUT_SOURCE_SCART2;
            break;
        case EN_PIP_SUB_SOURCE_YPBPR:
            enSubInputSrc = MAPI_INPUT_SOURCE_YPBPR;
            break;
        case EN_PIP_SUB_SOURCE_YPBPR2:
            enSubInputSrc = MAPI_INPUT_SOURCE_YPBPR2;
            break;
        case EN_PIP_SUB_SOURCE_YPBPR3:
            enSubInputSrc = MAPI_INPUT_SOURCE_YPBPR3;
            break;
        case EN_PIP_SUB_SOURCE_VGA:
            enSubInputSrc = MAPI_INPUT_SOURCE_VGA;
            break;
        case EN_PIP_SUB_SOURCE_VGA2:
            enSubInputSrc = MAPI_INPUT_SOURCE_VGA2;
            break;
        case EN_PIP_SUB_SOURCE_VGA3:
            enSubInputSrc = MAPI_INPUT_SOURCE_VGA3;
            break;
        case EN_PIP_SUB_SOURCE_HDMI:
            enSubInputSrc = MAPI_INPUT_SOURCE_HDMI;
            break;
        case EN_PIP_SUB_SOURCE_HDMI2:
            enSubInputSrc = MAPI_INPUT_SOURCE_HDMI2;
            break;
        case EN_PIP_SUB_SOURCE_HDMI3:
            enSubInputSrc = MAPI_INPUT_SOURCE_HDMI3;
            break;
        case EN_PIP_SUB_SOURCE_HDMI4:
            enSubInputSrc = MAPI_INPUT_SOURCE_HDMI4;
            break;
        case EN_PIP_SUB_SOURCE_CVBS:
            enSubInputSrc = MAPI_INPUT_SOURCE_CVBS;
            break;
        case EN_PIP_SUB_SOURCE_CVBS2:
            enSubInputSrc = MAPI_INPUT_SOURCE_CVBS2;
            break;
        case EN_PIP_SUB_SOURCE_CVBS3:
            enSubInputSrc = MAPI_INPUT_SOURCE_CVBS3;
            break;
        case EN_PIP_SUB_SOURCE_CVBS4:
            enSubInputSrc = MAPI_INPUT_SOURCE_CVBS4;
            break;
        case EN_PIP_SUB_SOURCE_CVBS5:
            enSubInputSrc = MAPI_INPUT_SOURCE_CVBS5;
            break;
        case EN_PIP_SUB_SOURCE_CVBS6:
            enSubInputSrc = MAPI_INPUT_SOURCE_CVBS6;
            break;
        case EN_PIP_SUB_SOURCE_CVBS7:
            enSubInputSrc = MAPI_INPUT_SOURCE_CVBS7;
            break;
        case EN_PIP_SUB_SOURCE_CVBS8:
            enSubInputSrc = MAPI_INPUT_SOURCE_CVBS8;
            break;
        case EN_PIP_SUB_SOURCE_SVIDEO:
            enSubInputSrc = MAPI_INPUT_SOURCE_SVIDEO;
            break;
        case EN_PIP_SUB_SOURCE_SVIDEO2:
            enSubInputSrc = MAPI_INPUT_SOURCE_SVIDEO2;
            break;
        case EN_PIP_SUB_SOURCE_SVIDEO3:
            enSubInputSrc = MAPI_INPUT_SOURCE_SVIDEO3;
            break;
        case EN_PIP_SUB_SOURCE_SVIDEO4:
            enSubInputSrc = MAPI_INPUT_SOURCE_SVIDEO4;
            break;
        default:
            ASSERT(0);
    }

    ST_PLAYER_STATE stPlayerState;
    MSrv_PlayerControl::GetInstance()->GetPlayerState(stPlayerState);

    stPlayerState.v_PIPSource[MAPI_SUB_WINDOW] = enSubInputSrc;
    stPlayerState.v_PIPRoute[MAPI_SUB_WINDOW] = MSrv_Control_common::GetRouteIndexByTVMode(wantChangeRouteType);

    //When sub source want to change to DTV, then check dtv route is available
    if (stPlayerState.v_PIPSource[MAPI_SUB_WINDOW] == MAPI_INPUT_SOURCE_DTV2)
    {
        if (stPlayerState.v_PIPRoute[MAPI_SUB_WINDOW] == UNUSED_DTV_ROUTE)
        {
            return FALSE;
        }
    }

    EN_CONFLICT_TYPE retType = MSrv_PlayerControl::GetInstance()->CheckConflict(stPlayerState);

    switch (retType)
    {
        case EN_CONFLICT_NONE:
            //pip -> none
            if ((GetPipMode() == E_PIP_MODE_PIP) && (enSubInputSrc == MAPI_INPUT_SOURCE_NONE))
            {
                DisablePip();
            }
            else if (enSubInputSrc != MAPI_INPUT_SOURCE_NONE)
            {
                //none -> pip
                if (GetPipMode() == E_PIP_MODE_OFF)
                {
                    //from none-->pip, disable dms
                    setPipMode(E_PIP_MODE_PIP);
                    mapi_video::SetPipMainSubInputSourceType(stPlayerState.v_PIPSource[MAPI_MAIN_WINDOW], stPlayerState.v_PIPSource[MAPI_SUB_WINDOW]);

                    GetMSrvPicture()->SetDMSV12L(FALSE);
                    GetMSrvSSSound()->SetHPVolume(GetMSrvSSSound()->GetVolume());
                    SetPipSubwindow(pstDispWin);
                }
                //pip -> pip
                else if (GetPipMode() == E_PIP_MODE_PIP)
                {
                    mapi_video::SetPipMainSubInputSourceType(stPlayerState.v_PIPSource[MAPI_MAIN_WINDOW], stPlayerState.v_PIPSource[MAPI_SUB_WINDOW]);
                }

                GetMSrvPlayer(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW))->OnNotifyPipEnable();

                if (stPlayerState.v_PIPSource[MAPI_SUB_WINDOW] == MAPI_INPUT_SOURCE_DTV2)
                {
                    SwitchMSrvDtvRoute(stPlayerState.v_PIPRoute[MAPI_SUB_WINDOW], MAPI_SUB_WINDOW);
                }
                else
                {
                    SetInputSource(stPlayerState.v_PIPSource[MAPI_SUB_WINDOW], FALSE, FALSE, FALSE, MAPI_SUB_WINDOW);
                }

                GetMSrvPlayer(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_SUB_WINDOW))->OnNotifyPipEnable();
#if (STB_ENABLE == 0)
                if (stPlayerState.v_PIPSource[MAPI_SUB_WINDOW] == MAPI_INPUT_SOURCE_ATV)
                {
                    MSrv_Control::GetMSrvAtv()->SetChannel(MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER , 0, 0, NULL), 1);
                }
#endif
            }
            break;

        case EN_CONFLICT_ROUTE:
            if (bForceSet == FALSE)
            {
                MSrv_Control::GetInstance()->PostEvent(NULL, EV_INPUT_SOURCE_CONFLICT, 0, 0);
                return FALSE;
            }

            //Old mode is PIP, so sub windows be close
            if (GetPipMode() == E_PIP_MODE_PIP)
            {
                DisablePip();
            }

            SwitchMSrvDtvRoute(stPlayerState.v_PIPRoute[MAPI_MAIN_WINDOW], MAPI_MAIN_WINDOW, TRUE);
            break;

        case EN_CONFLICT_SOURCE:
            if (bForceSet == FALSE)
            {
                MSrv_Control::GetInstance()->PostEvent(NULL, EV_INPUT_SOURCE_CONFLICT, 0, 0);
                return FALSE;
            }

            //Old mode is PIP, so sub windows be close
            if (GetPipMode() == E_PIP_MODE_PIP)
            {
                DisablePip();
            }

            SetInputSource(stPlayerState.v_PIPSource[MAPI_MAIN_WINDOW], TRUE, FALSE, FALSE, MAPI_MAIN_WINDOW, TRUE);

            if (stPlayerState.v_PIPSource[MAPI_MAIN_WINDOW] == MAPI_INPUT_SOURCE_DTV)
            {
                ASSERT(0);
            }
#if (STB_ENABLE == 0)
            else if (stPlayerState.v_PIPSource[MAPI_MAIN_WINDOW] == MAPI_INPUT_SOURCE_ATV)
            {
                MSrv_Control::GetMSrvAtv()->SetChannel(MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER , 0, 0, NULL), 1);
            }
#endif
            break;
        case EN_CONFLICT_BACKGROUND:
            if (bForceSet == FALSE)
            {
                MSrv_Control::GetInstance()->PostEvent(NULL, EV_INPUT_SOURCE_CONFLICT, 0, 0);
                return FALSE;
            }
            if (stPlayerState.v_PIPSource[MAPI_SUB_WINDOW] == MAPI_INPUT_SOURCE_DTV2)
            {
                SwitchMSrvDtvRoute(stPlayerState.v_PIPRoute[MAPI_SUB_WINDOW], MAPI_SUB_WINDOW);
            }
            else
            {
                SetInputSource(stPlayerState.v_PIPSource[MAPI_SUB_WINDOW], FALSE, FALSE, FALSE, MAPI_SUB_WINDOW);
            }
            break;
        default:
            ASSERT(0);
            break;
    }

    return TRUE;
}
#endif

BOOL MSrv_Control_common::selectAdcTableByCalibrationMode(const MAPI_INPUT_SOURCE_TYPE enInputSrc)
{
    MAPI_BOOL bResult = MAPI_FALSE;
    EN_MAPI_CALIBRATION_MODE enADCCalibrationMode = E_MAPI_CALIBRATION_MODE_NUMS;
    E_ADC_SET_INDEX enADCIndex = ADC_SET_NUMS;
    const U16 u16InputVSize = mapi_interface::Get_mapi_pql(MAPI_MAIN_WINDOW)->GetVsize();

    if(  ((enInputSrc >= MAPI_INPUT_SOURCE_YPBPR) && (enInputSrc < MAPI_INPUT_SOURCE_YPBPR_MAX))
      || ((enInputSrc >= MAPI_INPUT_SOURCE_SCART) && (enInputSrc < MAPI_INPUT_SOURCE_SCART_MAX))
      || (IsSrcVga(enInputSrc)))
    {
        if((enInputSrc >= MAPI_INPUT_SOURCE_YPBPR) && (enInputSrc < MAPI_INPUT_SOURCE_YPBPR_MAX))
        {
            if(u16InputVSize < 1080)
            {
                enADCIndex = ADC_SET_YPBPR_SD;
            }
            else
            {
                enADCIndex = ADC_SET_YPBPR_HD;
            }
        }
        else if(IsSrcVga(enInputSrc))
        {
            enADCIndex = ADC_SET_VGA;
        }
        else if ((enInputSrc >= MAPI_INPUT_SOURCE_SCART) && (enInputSrc < MAPI_INPUT_SOURCE_SCART_MAX))
        {
            enADCIndex = ADC_SET_SCART_RGB;
        }
        enADCCalibrationMode = MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->GetADCCalibrationMode(enADCIndex);
        if (E_MAPI_CALIBRATION_MODE_HW == enADCCalibrationMode)
        {
            mapi_interface::Get_mapi_video(enInputSrc)->EnableADCHWCalibration(TRUE);
            bResult = MAPI_TRUE;
        }
        else if (E_MAPI_CALIBRATION_MODE_SW == enADCCalibrationMode)
        {
            mapi_interface::Get_mapi_video(enInputSrc)->EnableADCHWCalibration(FALSE);
            bResult = MAPI_TRUE;
        }
        else
        {
            MSRV_CONTROL_COMMON_ERR("%s:[%d]got bad ADC CALIBRATION MODE-%u from system database!\n", __PRETTY_FUNCTION__, __LINE__, enADCCalibrationMode);
            bResult = MAPI_FALSE;
        }
    }
    else
    {
        bResult = MAPI_FALSE;
    }
    return bResult;
}

BOOL MSrv_Control_common::SwitchMSrvDtvRoute(U8 u8DtvRoute, MAPI_SCALER_WIN eWin, BOOL bForceSet, BOOL bFromUser)
{
#if (PIP_ENABLE == 1)
    //Get current player state
    ST_PLAYER_STATE stPlayerState;
    MSrv_PlayerControl::GetInstance()->GetInstance()->GetPlayerState(stPlayerState);

    //Set DTV source and route
    if(MAPI_MAIN_WINDOW == eWin)
    {
        stPlayerState.v_PIPSource[eWin] = MAPI_INPUT_SOURCE_DTV;
    }
    else
    {
        stPlayerState.v_PIPSource[eWin] = MAPI_INPUT_SOURCE_DTV2;
    }
    stPlayerState.v_PIPRoute[eWin] = u8DtvRoute;

    //Check the change result is conflict
    EN_CONFLICT_TYPE retType = MSrv_PlayerControl::GetInstance()->CheckConflict(stPlayerState);
    if (retType != EN_CONFLICT_NONE)
    {
        if (bForceSet == FALSE)
        {
            MSrv_Control::GetInstance()->PostEvent(NULL, EV_INPUT_SOURCE_CONFLICT, 0, 0);
            return FALSE;
        }

        if (retType != EN_CONFLICT_BACKGROUND)
        {
            if (GetPipMode() == E_PIP_MODE_PIP)
            {
                DisablePip();
            }
        }
    }
#endif

    MSRV_CMD stCmd;

    memset(&stCmd, 0, sizeof(MSRV_CMD));

    if (m_bInit == FALSE)
    {
        return MAPI_INPUT_SOURCE_NONE;
    }

    m_bSourceChange = TRUE;
    stCmd.enCmd = E_CMD_SET_DTV_ROUTE;
    stCmd.u32Param1 = (U32)u8DtvRoute;
    stCmd.u32Param2 = (U32)eWin;
    // Add For CI
    stCmd.u32Param3 = (U32)bFromUser;

    m_pCmdEvt->Send(stCmd);

    m_pCmdAckEvt->Wait(&stCmd);
    m_bSourceChange = FALSE;

    return TRUE;
}

#if (ATSC_SYSTEM_ENABLE == 1)
MAPI_INPUT_SOURCE_TYPE MSrv_Control_common::GetLastTVInputSource(void)
{
    MSRV_CONTROL_COMMON_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
    MAPI_INPUT_SOURCE_TYPE eCurrSrcType = m_enLastTVSource;
    return eCurrSrcType;
}
#endif
MSrv_System_Database * MSrv_Control_common::GetMSrvSystemDatabase(void)
{
    MSRV_CONTROL_COMMON_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
    MSrv_System_Database *p = dynamic_cast<MSrv_System_Database *>(m_pMSrvList[E_MSRV_SYSTEM_DATABASE]);
    ASSERT(p);
    return p;
}

MSrv_ChannelManager * MSrv_Control_common::GetMSrvChannelManager(void)
{
    MSRV_CONTROL_COMMON_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
    MSrv_ChannelManager *p = dynamic_cast<MSrv_ChannelManager *>(m_pMSrvList[E_MSRV_CHANNEL_MANAGER]);
    ASSERT(p);
    return p;
}

MSrv_Picture * MSrv_Control_common::GetMSrvPicture(void)
{
    MSRV_CONTROL_COMMON_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
    MSrv_Picture *p = dynamic_cast<MSrv_Picture *>(m_pMSrvList[E_MSRV_PICTURE]);
    ASSERT(p);
    return p;
}

MSrv_Video * MSrv_Control_common::GetMSrvVideo(void)
{
    MSRV_CONTROL_COMMON_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
    MSrv_Video *p = dynamic_cast<MSrv_Video *>(m_pMSrvList[E_MSRV_VIDEO]);
    ASSERT(p);
    return p;
}
#if (ENABLE_BACKEND == 1)
MSrv_Backend * MSrv_Control_common::GetMSrvBackend(void)
{
    MSRV_CONTROL_COMMON_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
    MSrv_Backend *p = dynamic_cast<MSrv_Backend *>(m_pMSrvList[E_MSRV_BACKEND]);
    ASSERT(p);
    return p;
}
#endif
MSrv_Factory_Mode * MSrv_Control_common::GetMSrvFactoryMode(void)
{
    MSRV_CONTROL_COMMON_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
    MSrv_Factory_Mode *p = dynamic_cast<MSrv_Factory_Mode *>(m_pMSrvList[E_MSRV_FACTORY]);
    ASSERT(p);
    return p;
}

MSrv_Timer * MSrv_Control_common::GetMSrvTimer(void)
{
    MSRV_CONTROL_COMMON_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
    MSrv_Timer *p = dynamic_cast<MSrv_Timer *>(m_pMSrvList[E_MSRV_TIMER]);
    ASSERT(p);
    return p;
}


MSrv_SSSound * MSrv_Control_common::GetMSrvSSSound(void)
{
    MSRV_CONTROL_COMMON_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
    MSrv_SSSound *p = dynamic_cast<MSrv_SSSound *>(m_pMSrvList[E_MSRV_SSSOUND]);
    ASSERT(p);
    return p;
}

MSrv_Network_Control* MSrv_Control_common::GetMSrvNetwork(void)
{

    MSRV_CONTROL_COMMON_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
    MSrv_Network_Control*p = dynamic_cast<MSrv_Network_Control*>(m_pMSrvList[E_MSRV_NETWORK]);
    ASSERT(p);
    return p;
}

#if (ENABLE_NETREADY == 1)
MSrv_DeviceAgent* MSrv_Control_common::GetMSrvDeviceAgent(void)
{
    MSRV_CONTROL_COMMON_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
    MSrv_DeviceAgent*p = dynamic_cast<MSrv_DeviceAgent*>(m_pMSrvList[E_MSRV_DEVICE_AGENT]);
    ASSERT(p);
    return p;
}
#endif


#if (STEREO_3D_ENABLE == 1)
MSrv_3DManager * MSrv_Control_common::GetMSrv3DManager(void)
{
    MSRV_CONTROL_COMMON_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
    MSrv_3DManager *p = dynamic_cast<MSrv_3DManager*>(m_pMSrvList[E_MSRV_3DMANAGER]);
    ASSERT(p);
    return p;
}
#endif

#if (RVU_ENABLE == 1)
MSrv_RVU_Player * MSrv_Control_common::GetMSrvRvu(void)
{
    MSRV_CONTROL_COMMON_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
    MSrv_RVU_Player *p = dynamic_cast<MSrv_RVU_Player *>(MSrv_PlayerControl::GetInstance()->GetMSrvPlayer(E_MSRV_RVU_PLAYER));
    ASSERT(p);
    return p;
}
#endif

#if (ENABLE_LITE_SN == 0)
MSrv_BacklightControl * MSrv_Control_common::GetMSrvBacklightControl(void)
{
    MSRV_CONTROL_COMMON_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
    MSrv_BacklightControl *p = dynamic_cast<MSrv_BacklightControl *>(m_pMSrvList[E_MSRV_BACKLIGHT_CONTROL]);
    ASSERT(p);
    return p;
}

MSrv_SrcDetect *MSrv_Control_common::GetMSrvSourceDetect(void)
{
    //CTRLSRV_I("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
    MSrv_SrcDetect *p = dynamic_cast<MSrv_SrcDetect*>(m_pMSrvList[E_MSRV_SRC_DET]);
    ASSERT(p);
    return p;
}
#if (SECURE_ENABLE == 1)
MSrv_PreSharedKey *MSrv_Control_common::GetMSrvPreSharedKey(void)
{
    MSRV_CONTROL_COMMON_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
    MSrv_PreSharedKey *p = dynamic_cast<MSrv_PreSharedKey *>(m_pMSrvList[E_MSRV_PRESHAREDKEY]);
    ASSERT(p);
    return p;
}
#endif
MSrv_Advert_Player * MSrv_Control_common::GetMSrvAdvertPlayer(void)
{
    MSRV_CONTROL_COMMON_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
    MSrv_Advert_Player *p = dynamic_cast<MSrv_Advert_Player *>(m_pMSrvList[E_MSRV_ADVERT_PLAYER]);
    ASSERT(p);
    return p;
}
#endif //ENABLE_LITE_SN

#if ((AUTO_TEST == 1) && (MSTAR_TVOS == 0))
AT_CmdManager * MSrv_Control_common::GetMSrvAutoTest(void)
{
    AT_CmdManager*p = AT_CmdManager_MSrv::GetInstance();
    ASSERT(p);
    return p;
}
#endif

#if (MODULE_TEST == 1)
MT_CmdManager * MSrv_Control_common::GetMSrvModuleTest(void)
{
#if (MSTAR_TVOS == 1)
    MT_CmdManager*p = MT_CmdManager_TVOS::GetInstance();
#else
    MT_CmdManager*p = MT_CmdManager_MSrv::GetInstance();
#endif
    ASSERT(p);
    return p;
}
#endif

#if (ENABLE_DIVXDRM == 1)
/* for DivX DRM */
MSrv_DivX_DRM *MSrv_Control_common::GetMSrvDivXDRM(void)
{
    MSRV_CONTROL_COMMON_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
    MSrv_DivX_DRM *p = dynamic_cast<MSrv_DivX_DRM *>(m_pMSrvList[E_MSRV_DIVX_DRM]);
    ASSERT(p);
    return p;
}
#endif
const MAPI_VIDEO_INPUTSRCTABLE * MSrv_Control_common::GetSourceList(void)
{
    MSRV_CONTROL_COMMON_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
    return m_pInputSrcTable;
}

BOOL MSrv_Control_common::IsUSBUpgradeFileValid()
{
#if 0
    {
        /*
        1. check if /sys/bus/usb/devices/X-1 exist (X: 1, 2, 3)
        -> if yes, some usb device is attached
        2. check Y as: /sys/bus/usb/devices/X-1/X-1:1.0/hostY/
        3. check the device name in /sys/bus/usb/devices/X-1/X-1:1.0/hostY/targetY:0:0/Y:0:0:0/
        4. check if the device is mounted
        5. check if usb.bin in the file
        */
#define MAX_USB_PORTS 3
#define AUTO_MOUNT 0 //can't enable at present
        U8 idx = 0;
        S8 stPath[128];
        DIR *dirp = NULL;
        S16 usb_port_with_disk = -1;
#if (PVR_ENABLE == 1)
        struct dirent *dp = NULL;
        S16 host = 0;
#endif
        for(idx = 0; (idx < MAX_USB_PORTS) && (usb_port_with_disk == -1); idx++)
        {
            /*STEP2: find X*/
            memset(stPath, 0, sizeof(stPath));
            snprintf((char*)stPath, 128, "/sys/bus/usb/devices/%u-1/", idx + 1);

            dirp = opendir((char*)stPath);
            if(dirp == NULL)
            {
                MSRV_CONTROL_COMMON_DBG("%s: no disk on USB-%u, at %d\n", __func__, idx + 1, __LINE__);
                continue;
            }
            closedir(dirp);
            dirp = NULL;
#if (PVR_ENABLE == 1) //Use auto mount in PVR
            memset(stPath, 0, sizeof(stPath));
            snprintf((char*)stPath, 128, "/sys/bus/usb/devices/%u-1/%u-1:1.0/", idx + 1, idx + 1);

            dirp = opendir((char*)stPath);
            if(dirp == NULL)
            {
                MSRV_CONTROL_COMMON_DBG("%s: no disk on USB-%u, at %d\n", __func__, idx + 1, __LINE__);
                continue;
            }
            do
            {
                /*STEP2: find Y*/
                host = -1;
                dp = readdir(dirp);
                if(dp != NULL)
                {
                    if(strncmp(dp->d_name, "host", 4) != 0)
                    {
                        continue;
                    }

                    host = atoi(dp->d_name + 4);
                    break;
                }
            }
            while(dp != NULL);
            closedir(dirp);
            dirp = NULL;
            if(host != -1)
            {
                /*STEP3: check device name*/
                memset(stPath, 0, sizeof(stPath));
                snprintf((char*)stPath, 128, "/sys/bus/usb/devices/%u-1/%u-1:1.0/host%d/target%d:0:0/%d:0:0:0/block/", idx + 1, idx + 1, host, host, host);

                dirp = opendir((char*)stPath);
                if(dirp == NULL)
                {
                    MSRV_CONTROL_COMMON_DBG("%s: no disk on USB-%u, at %d\n", __func__, idx + 1, __LINE__);
                    continue;
                }
                do
                {
                    dp = readdir(dirp);
                    if(dp != NULL)
                    {
                        if(strncmp(dp->d_name, "sd", 2) != 0)
                            continue;
                        /*STEP 4: check if the device is mounted*/
                        {
                            //check /proc/mount
                            FILE *fp = NULL;
                            S8 buf[256], dev[16], str_mount[6][128];
#define TEMP_MOUNT_PATH "/tmp/usbmnt"
                            extern int mount(const char * source, const char * target, const char * filesystemtype, unsigned long mountflags, const void * data);
                            extern int umount2(const char * target, int flags);
                            memset(dev, 0, sizeof(dev));
                            snprintf((char*)dev, 16, "/dev/%s1", dp->d_name);
                            fp = fopen("/proc/mounts", "r");
                            if(fp != NULL)
                            {
                                memset(buf, 0, sizeof(buf));
                                while(NULL != fgets((char*)buf, sizeof(buf), fp))
                                {
                                    buf[255] = '\0';
                                    if(NULL != strstr((char*)buf, (char*)dev))
                                    {
                                        memset(str_mount, 0, sizeof(str_mount));
                                        sscanf((char*)buf, "%s %s %s %s %s %s", (char*)str_mount[0], (char*)str_mount[1], (char*)str_mount[2], (char*)str_mount[3], (char*)str_mount[4], (char*)str_mount[5]);
                                        break;
                                    }
                                    memset(buf, 0, sizeof(buf));
                                }
                                fclose(fp);
                                if(str_mount[1][0] == 0) //no mounted
                                {
#if AUTO_MOUNT //TODO
#error "AUTO_MOUNT is not ready!!"
                                    /*mount to a temp place*/
                                    if(mount((char*)dev, TEMP_MOUNT_PATH, "vfat", 0, "iocharset=utf8,shortname=winnt"))
                                    {
                                        //mount fail
                                        continue;
                                    }
                                    memset(str_mount[1], 0, sizeof(str_mount[1]));
                                    snprintf((char*)str_mount[1], 16, "%s", TEMP_MOUNT_PATH);
#else
                                    continue;
#endif
                                }
                                /*STEP 5: check if usb.bin in the file*/
                                memset(buf, 0, sizeof(buf));
                                snprintf((char*)buf, 256, "%s/%s", str_mount[1], USB_BIN_FILE_NAME);
                                fp = fopen((char*)buf, "r");
                                if(fp != NULL)
                                {
                                    usb_port_with_disk = (S8)idx;
                                    m_USBUpgradePort = (S8)idx;
                                    MSRV_CONTROL_COMMON_INFO("found %s, host: %d, usb_port_with_disk: %d\n", dp->d_name, host, usb_port_with_disk);
                                    fclose(fp);
                                }
#if AUTO_MOUNT
                                umount2(TEMP_MOUNT_PATH, 2); //check what is 2. see: /home/chihhua.huang/T8/THEALE/RedLion/2.6.28.9/fs/gfs2/mount.h
#endif //AUTO_MOUNT
                            }
                        }
                        break;
                    }
                }
                while(dp != NULL);
                closedir(dirp);
                dirp = NULL;
            }
#else //(PVR_ENABLE == 1)
            usb_port_with_disk = (S8)idx;
            m_USBUpgradePort = (S8)idx;
            MSRV_CONTROL_COMMON_INFO("found %s, host: %d, usb_port_with_disk: %d\n", dp->d_name, host, usb_port_with_disk);
#endif //(PVR_ENABLE == 1)
        }
        if(usb_port_with_disk != -1)
        {
            MSRV_CONTROL_COMMON_INFO("%s: at %d\n", __func__, __LINE__);
            return TRUE;
        }
    }
#else
    DIR *dirp = NULL;
    FILE *fp = NULL;
    string s;
    struct dirent *dp = NULL;
    string stPath = USB_MOUNT_FOLDER;
    dirp = opendir(stPath.c_str());
    if(dirp == NULL)
    {
        MSRV_CONTROL_COMMON_DBG("%s: no USB directory, at %d\n", __func__, __LINE__);
        return FALSE;
    }

    do
    {
        dp = readdir(dirp);
        if(dp != NULL)
        {
            s = dp->d_name;
            if(s != "." && s != "..")
            {
                s = stPath + "/" + s + USB_BIN_FILE_NAME;
                MSRV_CONTROL_COMMON_DBG(" search s:%s\n",s.c_str());
                fp = fopen(s.c_str(), "r");
                if(fp != NULL) //find usb upgrade bin file!
                {
                    MSRV_CONTROL_COMMON_DBG(" Fund it!\n");
                    fclose(fp);
                    fp = NULL;
                    closedir(dirp);
                    m_USBUpgradePort = 0;
                    return TRUE;
                }
            }
        }
    }while(dp != NULL);

    closedir(dirp);
    dirp = NULL;
#endif
    MSRV_CONTROL_COMMON_DBG("%s: find no USB with correct upgrade file, at %d\n", __func__, __LINE__);
    return FALSE;
}


BOOL MSrv_Control_common::IsUSBUpgradeFileValid(char *pFilename)
{
#if (MSTAR_TVOS == 1)
    /*
    1. check if /sys/bus/usb/devices/X-1 exist (X: 1, 2, 3)
    -> if yes, some usb device is attached
    2. check Y as: /sys/bus/usb/devices/X-1/X-1:1.0/hostY/
    3. check the device name in /sys/bus/usb/devices/X-1/X-1:1.0/hostY/targetY:0:0/Y:0:0:0/
    4. check if pFilename in the file
    */
#define MAX_USB_PORTS 3
    U8 idx = 0;
    S8 stPath[128];
    DIR *dirp = NULL;
    S16 usb_port_with_disk = -1;
    struct dirent *dp = NULL;
    S16 host = 0;
    for(idx = 0; (idx < MAX_USB_PORTS) && (usb_port_with_disk == -1); idx++)
    {
        memset(stPath, 0, sizeof(stPath));
        snprintf((char*)stPath, 128, "/sys/bus/usb/devices/%u-1/%u-1:1.0/", idx + 1, idx + 1);
        /*STEP1: find X*/
        dirp = opendir((char*)stPath);
        if(dirp == NULL)
        {
            MSRV_CONTROL_COMMON_DBG("%s: no disk on USB-%u, at %d\n", __func__, idx + 1, __LINE__);
            continue;
        }
        do
{
            /*STEP2: find Y*/
            host = -1;
            dp = readdir(dirp);
            if(dp != NULL)
            {
                if(strncmp(dp->d_name, "host", 4) != 0)
                {
                    continue;
                }

                host = atoi(dp->d_name + 4);
                break;
            }
        } while(dp != NULL);
        closedir(dirp);
        dirp = NULL;
        if(host != -1)
        {
            /*STEP3: check device name*/
            memset(stPath, 0, sizeof(stPath));
            snprintf((char*)stPath, 128, "/sys/bus/usb/devices/%u-1/%u-1:1.0/host%d/target%d:0:0/%d:0:0:0/block/", idx + 1, idx + 1, host, host, host);

            dirp = opendir((char*)stPath);
            if(dirp == NULL)
            {
                MSRV_CONTROL_COMMON_DBG("%s: no disk on USB-%u, at %d\n", __func__, idx + 1, __LINE__);
                continue;
            }
            do
            {
                dp = readdir(dirp);
                if(dp != NULL)
                {
                    if(strncmp(dp->d_name, "sd", 2) != 0)
                    {
                        continue;
                    }
                    else
                    {
                        FILE *fp = NULL;
                        S8 buf[256];

                        /*STEP 4: check if pFilename in the file*/
                        memset(buf, 0, sizeof(buf));
                        snprintf((char*)buf, 256, "/mnt/usb/%s1/%s",dp->d_name, pFilename);
                        //strncpy(pFilename,(const char*)buf,strlen((const char*)buf));
                        ResetForMbootUpgrade((char*)buf);
                        fp = fopen((char*)buf, "r");
                        if(fp != NULL)
                        {
                            usb_port_with_disk = (S8)idx;
                            m_USBUpgradePort = (S8)idx;
                            MSRV_CONTROL_COMMON_INFO("found %s, host: %d, usb_port_with_disk: %d\n", dp->d_name, host, usb_port_with_disk);
                            fclose(fp);
                        }
                    }
                    break;
                }
            } while(dp != NULL);

            closedir(dirp);
            dirp = NULL;
        }
    }
    if(usb_port_with_disk != -1)
    {
        MSRV_CONTROL_COMMON_DBG("%s: at %d\n", __func__, __LINE__);
        return TRUE;
    }
    else
    {
        MSRV_CONTROL_COMMON_DBG("%s: find no USB with correct upgrade file, at %d\n", __func__, __LINE__);
        return FALSE;
    }

#else //no TVOS

    {
        /*
        1. check if /sys/bus/usb/devices/X-1 exist (X: 1, 2, 3)
        -> if yes, some usb device is attached
        2. check Y as: /sys/bus/usb/devices/X-1/X-1:1.0/hostY/
        3. check the device name in /sys/bus/usb/devices/X-1/X-1:1.0/hostY/targetY:0:0/Y:0:0:0/
        4. check if the device is mounted
        5. check if pFilename in the file
        */
#define MAX_USB_PORTS 3
#define AUTO_MOUNT 0 //can't enable at present
        U8 idx = 0;
        S8 stPath[128];
        DIR *dirp = NULL;
        S16 usb_port_with_disk = -1;
#if (PVR_ENABLE == 1)
        struct dirent *dp = NULL;
        S16 host = 0;
#endif
        for(idx = 0; (idx < MAX_USB_PORTS) && (usb_port_with_disk == -1); idx++)
        {
            /*STEP2: find X*/
            memset(stPath, 0, sizeof(stPath));
            snprintf((char*)stPath, 128, "/sys/bus/usb/devices/%u-1/", idx + 1);

            dirp = opendir((char*)stPath);
            if(dirp == NULL)
            {
                MSRV_CONTROL_COMMON_DBG("%s: no disk on USB-%u, at %d\n", __func__, idx + 1, __LINE__);
                continue;
            }
            closedir(dirp);
            dirp = NULL;
#if (PVR_ENABLE == 1) //Use auto mount in PVR
            memset(stPath, 0, sizeof(stPath));
            snprintf((char*)stPath, 128, "/sys/bus/usb/devices/%u-1/%u-1:1.0/", idx + 1, idx + 1);

            dirp = opendir((char*)stPath);
            if(dirp == NULL)
            {
                MSRV_CONTROL_COMMON_DBG("%s: no disk on USB-%u, at %d\n", __func__, idx + 1, __LINE__);
                continue;
            }
            do
            {
                /*STEP2: find Y*/
                host = -1;
                dp = readdir(dirp);
                if(dp != NULL)
                {
                    if(strncmp(dp->d_name, "host", 4) != 0)
                    {
                        continue;
                    }

                    host = atoi(dp->d_name + 4);
                    break;
                }
            }
            while(dp != NULL);
            closedir(dirp);
            dirp = NULL;
            if(host != -1)
            {
                /*STEP3: check device name*/
                memset(stPath, 0, sizeof(stPath));
                snprintf((char*)stPath, 128, "/sys/bus/usb/devices/%u-1/%u-1:1.0/host%d/target%d:0:0/%d:0:0:0/block/", idx + 1, idx + 1, host, host, host);

                dirp = opendir((char*)stPath);
                if(dirp == NULL)
                {
                    MSRV_CONTROL_COMMON_DBG("%s: no disk on USB-%u, at %d\n", __func__, idx + 1, __LINE__);
                    continue;
                }
                do
                {
                    dp = readdir(dirp);
                    if(dp != NULL)
                    {
                        if(strncmp(dp->d_name, "sd", 2) != 0)
                            continue;
                        /*STEP 4: check if the device is mounted*/
                        {
                            //check /proc/mount
                            FILE *fp = NULL;
                            S8 buf[256], dev[16], str_mount[6][128];
#define TEMP_MOUNT_PATH "/tmp/usbmnt"
                            extern int mount(const char * source, const char * target, const char * filesystemtype, unsigned long mountflags, const void * data);
                            extern int umount2(const char * target, int flags);
                            memset(dev, 0, sizeof(dev));
                            snprintf((char*)dev, 16, "/dev/%s1", dp->d_name);
                            fp = fopen("/proc/mounts", "r");
                            if(fp != NULL)
                            {
                                memset(buf, 0, sizeof(buf));
                                while(NULL != fgets((char*)buf, sizeof(buf), fp))
                                {
                                    buf[255] = '\0';
                                    if(NULL != strstr((char*)buf, (char*)dev))
                                    {
                                        memset(str_mount, 0, sizeof(str_mount));
                                        sscanf((char*)buf, "%s %s %s %s %s %s", (char*)str_mount[0], (char*)str_mount[1], (char*)str_mount[2], (char*)str_mount[3], (char*)str_mount[4], (char*)str_mount[5]);
                                        break;
                                    }
                                    memset(buf, 0, sizeof(buf));
                                }
                                fclose(fp);
                                if(str_mount[1][0] == 0) //no mounted
                                {
#if AUTO_MOUNT //TODO
#error "AUTO_MOUNT is not ready!!"
                                    /*mount to a temp place*/
                                    if(mount((char*)dev, TEMP_MOUNT_PATH, "vfat", 0, "iocharset=utf8,shortname=winnt"))
                                    {
                                        //mount fail
                                        continue;
                                    }
                                    memset(str_mount[1], 0, sizeof(str_mount[1]));
                                    snprintf((char*)str_mount[1], 16, "%s", TEMP_MOUNT_PATH);
#else
                                    continue;
#endif
                                }
                                /*STEP 5: check if pFilename in the file*/
                                memset(buf, 0, sizeof(buf));
                                snprintf((char*)buf, 256, "%s/%s", str_mount[1], pFilename);
                                strncpy(pFilename,(const char*)buf,strlen((const char*)buf));
                                fp = fopen((char*)buf, "r");
                                if(fp != NULL)
                                {
                                    usb_port_with_disk = (S8)idx;
                                    m_USBUpgradePort = (S8)idx;
                                    MSRV_CONTROL_COMMON_INFO("found %s, host: %d, usb_port_with_disk: %d\n", dp->d_name, host, usb_port_with_disk);
                                    fclose(fp);
                                }
#if AUTO_MOUNT
                                umount2(TEMP_MOUNT_PATH, 2); //check what is 2. see: /home/chihhua.huang/T8/THEALE/RedLion/2.6.28.9/fs/gfs2/mount.h
#endif //AUTO_MOUNT
                            }
                        }
                        break;
                    }
                }
                while(dp != NULL);
                closedir(dirp);
                dirp = NULL;
            }
#else //(PVR_ENABLE == 1)
            usb_port_with_disk = (S8)idx;
            m_USBUpgradePort = (S8)idx;
            MSRV_CONTROL_COMMON_INFO("found %s, host: %d, usb_port_with_disk: %d\n", dp->d_name, host, usb_port_with_disk);
#endif //(PVR_ENABLE == 1)
        }
        if(usb_port_with_disk != -1)
        {
            MSRV_CONTROL_COMMON_INFO("%s: at %d\n", __func__, __LINE__);
            return TRUE;
        }
    }
    MSRV_CONTROL_COMMON_DBG("%s: find no USB with correct upgrade file, at %d\n", __func__, __LINE__);
    return FALSE;
#endif //end MSTAR_TVOS?

}


BOOL MSrv_Control_common::ResetForMbootUpgrade(char *pFilename)
{
    char Path[128] = {0};
    if(pFilename == NULL)
        return FALSE;

    strncpy(Path, pFilename ,strlen(pFilename));

    Path[127] = '\0';

    MSRV_CONTROL_COMMON_DBG("===Mboot Path : [%s]\n",Path);
    if(access(Path, 0) != 0)
    {
        return FALSE;
    }

    if(mapi_interface::Get_mapi_spiflash()->Upgrade(Path) == TRUE)
    {
        MSRV_CONTROL_COMMON_DBG("===Upgrade spi flash Success===\n");
        SystemCmd("reboot");
    }
    else
    {
        MSRV_CONTROL_COMMON_ERR("===Upgrade Mboot Fail,maybe no mboot.bin in disk===\n");
        return FALSE;
    }
    return TRUE;
}

BOOL MSrv_Control_common::ResetForNandUpgrade(void)
{
    if(access("/upgrade/usb.bin", 0) != 0)//Check /backup/usb.bin is exist
    {
        return FALSE;
    }
    IEnvManager* pEnvMan = IEnvManager::Instance();

        //TODO
    if((pEnvMan)
    && (pEnvMan->SetEnv_Protect(LOAD_UPGRADEFILE_PATH, "nandfile"))
    && (pEnvMan->SetEnv_Protect(USB_UPGRADE_PATH, "usb.bin"))
    && (pEnvMan->SetEnv_Protect(UPGRADE_MODE, "usb")))
    {
        pEnvMan->SaveEnv();
    }
    SystemReboot();
    return TRUE;
}


BOOL MSrv_Control_common::ResetForUSBUpgrade(const U8 *pData, const U16 u16Size)
{
    //TODO
    if(m_USBUpgradePort == -1)
        return FALSE;

    IEnvManager* pEnvMan = IEnvManager::Instance();
    U8 stPort[8];
    memset(stPort, 0, sizeof(stPort));
    snprintf((char*)stPort, 8, "%d", m_USBUpgradePort);

    //TODO
    char u8name[MAX_USB_PATH_LEN];
    {
        memset(u8name, 0 ,sizeof(u8name));
        #if 0
        if(IEnvManager::GetSecFlag() == E_SECURITY_IS_ON)
        {
            strncpy(u8name, SECURE_USB_BIN_FILE_NAME, MAX_USB_PATH_LEN);
        }
        else
        {
            strncpy(u8name, USB_BIN_FILE_NAME, MAX_USB_PATH_LEN);
        }
        #endif
        strncpy(u8name, USB_BIN_FILE_NAME, (unsigned int)MAX_USB_PATH_LEN);
    }
    if((pEnvMan)
            && (pEnvMan->SetEnv_Protect(USB_UPGRADE_PORT, (char*)stPort))
            && (pEnvMan->SetEnv_Protect(USB_UPGRADE_PATH, u8name))
            && (pEnvMan->SetEnv_Protect(UPGRADE_MODE, "usb")))
    {
        pEnvMan->SaveEnv();
    }

    mapi_interface::Get_mapi_display()->OnOff(FALSE); // set display off to prevent some garbage message on some panel
    SystemReboot();
    return TRUE;
}

#if (ENABLE_NETREADY == 1)
BOOL MSrv_Control_common::ResetForNetReadyUpgrade(void)
{
    IEnvManager* pEnvMan = IEnvManager::Instance();

    if((pEnvMan) && (pEnvMan->SetEnv_Protect(UPGRADE_MODE, "oad")))
    {
        pEnvMan->SaveEnv();
    }
    else
    {
        ASSERT(0);
    }

    mapi_interface::Get_mapi_display()->OnOff(FALSE); // set display off to prevent some garbage message on some panel
    SystemReboot();

    return TRUE;
}
#endif

BOOL MSrv_Control_common::ResetForNetworkUpgrade(void)
{

    IEnvManager* pEnvMan = IEnvManager::Instance();

    if((pEnvMan) && (pEnvMan->SetEnv_Protect(UPGRADE_MODE, "net")))
    {
        pEnvMan->SaveEnv();
    }
    else
    {
        ASSERT(0);
    }
    mapi_interface::Get_mapi_display()->OnOff(FALSE); // set display off to prevent some garbage message on some panel
    SystemReboot();

    return TRUE;
}

#if (OAD_ENABLE == 1)
BOOL MSrv_Control_common::ResetForOADUpgrade(ST_OAD_UPDGRADE_CONFIG* pOADUpgradeConfig)
{
#if (MSTAR_TVOS == 1)
    MSRV_CONTROL_COMMON_DBG("%s: do nothing\n", __FUNCTION__);
#else
    usleep(5 * 1000 * 1000); //for oad download lost DDB and update fail issue

    if(m_bOADInMBoot)
    {
        // Should call ResetForOADUpgrade(const U8 *pData, const U16 u16Size)
        ASSERT(0);
        return FALSE;
    }

    IEnvManager* pEnvMan = IEnvManager::Instance();

    if((pEnvMan) && (pEnvMan->SetEnv_Protect(UPGRADE_MODE, "oad")))
    {
        pEnvMan->SaveEnv();
    }
    else
    {
        ASSERT(0);
    }
    if(NULL != pOADUpgradeConfig)
    {
        if(pEnvMan != NULL )
        {
            string sDisplayOSD = ((pOADUpgradeConfig->bDisplayUpgradeOSD == TRUE )?"on":"off");
            string sRebootAfterUpgrade = ((pOADUpgradeConfig->bRebootAfterUpgrade == TRUE)?"1":"0");
            pEnvMan->SetEnv_Protect("osd_active", sDisplayOSD.c_str());
            pEnvMan->SetEnv_Protect("is_reboot_after_upgrade", sRebootAfterUpgrade.c_str());
            pEnvMan->SaveEnv();
        }
    }
    mapi_interface::Get_mapi_display()->OnOff(FALSE); // set display off to prevent some garbage message on some panel
    SystemReboot();
#endif
    return TRUE;
}

BOOL MSrv_Control_common::ResetForOADUpgrade(const U8 *pData, const U16 u16Size)
{
#if (MSTAR_TVOS == 1)
    MSRV_CONTROL_COMMON_DBG("%s: do nothing\n", __FUNCTION__);
#else
    OAD_DL_INFO stOADInfo;
    if(sizeof(stOADInfo) != u16Size)
    {
        ASSERT(0);
        return FALSE;
    }
    if(m_bOADInMBoot == FALSE)
    {
        // Should call ResetForOADUpgrade(ST_OAD_UPDGRADE_CONFIG* pOADUpgradeConfig)
        ASSERT(0);
        return FALSE;
    }
    memcpy(&stOADInfo, pData, u16Size);

    IEnvManager* pEnvMan = IEnvManager::Instance();
    U8 stPid[8], stBand[8], stFreq[16];
#if (SDTT_OAD_ENABLE == 1)
    U8 stGroupPid[16],stSdttOad[8];
#endif
    memset(stPid, 0, sizeof(stPid));
    memset(stBand, 0, sizeof(stBand));
    memset(stFreq, 0, sizeof(stFreq));
    snprintf((char*)stPid, 8, "%d", stOADInfo.pid);
    snprintf((char*)stBand, 8, "%d", stOADInfo.band);
    snprintf((char*)stFreq, 16, "%u", stOADInfo.freq);
#if (SDTT_OAD_ENABLE == 1)
    snprintf((char*)stGroupPid, 16, "%u", stOADInfo.u32GroupId);
    snprintf((char*)stSdttOad, 8, "%d", stOADInfo.bIsSdttOad);
#endif
    if((pEnvMan)
            && (pEnvMan->SetEnv_Protect(OAD_UPGRADE_FREQ, (char*)stFreq))
            && (pEnvMan->SetEnv_Protect(OAD_UPGRADE_BAND, (char*)stBand))
            && (pEnvMan->SetEnv_Protect(OAD_UPGRADE_PID, (char*)stPid))
#if (SDTT_OAD_ENABLE == 1)
            && (pEnvMan->SetEnv_Protect(OAD_GROUP_PID, (char*)stGroupPid))
            && (pEnvMan->SetEnv_Protect(OAD_IS_SDTT, (char*)stSdttOad))
#endif
            && (pEnvMan->SetEnv_Protect(UPGRADE_MODE, "oad")))
    {
        pEnvMan->SaveEnv();
    }
    else
    {
        ASSERT(0);
    }
    mapi_interface::Get_mapi_display()->OnOff(FALSE); // set display off to prevent some garbage message on some panel
    SystemReboot();
#endif
    return TRUE;
}

BOOL MSrv_Control_common::StandbyForOADUpgrade()
{
#if (OAD_ENABLE == 1)
    if(m_bOADInMBoot)
    {
        // Should call ResetForOADUpgrade(const U8 *pData, const U16 u16Size)
        ASSERT(0);
        return FALSE;
    }
    IEnvManager* pEnvMan = IEnvManager::Instance();

    if((pEnvMan)
            && (pEnvMan->SetEnv_Protect(UPGRADE_MODE, "oad")))
    {
        pEnvMan->SaveEnv();
    }
    else
    {
        ASSERT(0);
    }

    /* power off */

    EnterSleepMode();

#endif
    return TRUE;
}
#endif

void MSrv_Control_common::GetBootConfig()
{
    IEnvManager* pEnvMan = IEnvManager::Instance();
    if((pEnvMan) && pEnvMan->Initialize())
    {
        IEnvManager_scope_lock block(pEnvMan);
        int size=pEnvMan->QueryLength(INFO_PM51_RUN_ON_DRAM);
        char data[size+1];
        memset(data,0,size+1);
        bool pInfo = pEnvMan->GetEnv_Protect(INFO_PM51_RUN_ON_DRAM,data,size);
        if((pInfo) && (strncmp(data, "1", 1) == 0))
        {
            m_bPmOnRam = TRUE;
        }
    }
}

void MSrv_Control_common::InitializeEnv()
{
    //Get env cfg
    int fd = open(PROC_CMD_LINE, O_RDONLY);
    if(fd >= 0)
    {
        int retRead = 0;
        char buf[CMDLINE_SIZE] = {0};

        retRead = read(fd, buf, CMDLINE_SIZE-1);
        if(retRead >= 0)
        {
            buf[retRead] = '\0';
            char* pEnv = strstr(buf, ENV_CFG_PREFIX);
            if(pEnv)
            {
                char* pTypeStart = strchr(pEnv, '=');
                if(pTypeStart)
                {
                    char* pTypeEnd = strchr(pEnv, ' ');
                    if(pTypeEnd)
                    {
                        *pTypeEnd = 0;
                    }

                    ++pTypeStart;

                    if(0 == strncmp(pTypeStart, ENV_IN_SERIAL, strlen(ENV_IN_SERIAL)))
                    {
                        IEnvManager::SetEnvType(E_ENV_IN_SERIAL);
                        Imapi_storage_factory_config::SetEnvType(E_SPI_CONFIG_STORAGE);
                    }
                    else if(0 == strncmp(pTypeStart, ENV_IN_NAND, strlen(ENV_IN_NAND)))
                    {
                        IEnvManager::SetEnvType(E_ENV_IN_NAND);
                        Imapi_storage_factory_config::SetEnvType(E_NAND_CONFIG_STORAGE);
                    }
                    else if(0 == strncmp(pTypeStart, ENV_IN_UBI, strlen(ENV_IN_UBI)))
                    {
                        IEnvManager::SetEnvType(E_ENV_IN_UBI);
                        MSRV_CONTROL_COMMON_ERR("Unsupport for the moment!!");
                    }
                    else if(0 == strncmp(pTypeStart, ENV_IN_EMMC, strlen(ENV_IN_EMMC)))
                    {
                        IEnvManager::SetEnvType(E_ENV_IN_MMC);
                        Imapi_storage_factory_config::SetEnvType(E_MMC_CONFIG_STORAGE);
                    }
                }
            }


            pEnv = strstr(buf, SECURITY_CFG_PREFIX);
            if(pEnv)
            {
                char* pTypeStart = strchr(pEnv, '=');

                if(pTypeStart)
                {
                    char* pTypeEnd = strchr(pEnv, ' ');

                    if(pTypeEnd)
                    {
                        *pTypeEnd = 0;
                    }

                    ++pTypeStart;

                    if(0 == strncmp(pTypeStart, SECURITY_IS_ON, strlen(SECURITY_IS_ON)))
                    {
                        IEnvManager::SetSecFlag(E_SECURITY_IS_ON);
                    }
                    else
                    {
                        IEnvManager::SetSecFlag(E_SECURITY_IS_OFF);
                    }
                }
            }
        }

        close(fd);
    }

    IEnvManager* pEnvMan = IEnvManager::Instance();
    if(pEnvMan && pEnvMan->Initialize())
    {
        IEnvManager_scope_lock block(pEnvMan);
        int size=pEnvMan->QueryLength(UPGRADE_MODE);
        char data[size+1];
        memset(data,0,size+1);
        bool pInfo = pEnvMan->GetEnv_Protect(UPGRADE_MODE,data,size);
        if(pInfo && (strncmp(data, "null", 4) != 0))
        {

            if(pEnvMan->SetEnv_Protect(UPGRADE_MODE, "null"))
            {
        pEnvMan->SaveEnv();
            }
        }
#if (OAD_ENABLE == 1)
        {
            IEnvManager_scope_lock block1(pEnvMan);
            int size=pEnvMan->QueryLength(OAD_IN_MBOOT_STR);
            char data[size+1];
            memset(data,0,size+1);
            bool pInfo = pEnvMan->GetEnv_Protect(OAD_IN_MBOOT_STR,data,size);
            if(pInfo && (strncmp(data, "0", 1) != 0))
            {
                m_bOADInMBoot = TRUE;
            }
        }

#endif
    }
    GetBootConfig();

    SetPowerOffFlag(MAPI_FALSE);///Clear the poweroff flag
}

void MSrv_Control_common::ipSecurityAuthentication(void)
{
    U8 u8Customer_info[MSRV_CUS_INFO_SIZE];
    U8 u8Customer_hash[MSRV_CUS_HASH_SIZE];
    memset(u8Customer_info, 0, MSRV_CUS_INFO_SIZE);
    memset(u8Customer_hash, 0, MSRV_CUS_HASH_SIZE);
    mapi_system * pSystem = mapi_interface::Get_mapi_system();
    STATIC_ASSERT(MSRV_CUS_INFO_SIZE == CUSTOMER_INFO_SIZE);
    STATIC_ASSERT(MSRV_CUS_HASH_SIZE == CUSTOMER_HASH_SIZE);
    CusInfo::GetInstance()->GetCustomerInfo(u8Customer_info, MSRV_CUS_INFO_SIZE);
    CusInfo::GetInstance()->GetHashKey(u8Customer_hash, MSRV_CUS_HASH_SIZE);
    MSRV_CONTROL_COMMON_INFO("###### Ip Security Authentication ######\n");
    pSystem->IpSecurityAuthentication(u8Customer_info, (U8 *)u8Customer_hash);
}

#if (ENABLE_LITE_SN != 1)
void MSrv_Control_common::GetEnableIPInfo(MAPI_U8 * BitTable,const int  BitTableLen)
{
    U8 u8Customer_info[MSRV_CUS_INFO_SIZE];
    U8 u8Customer_hash[MSRV_CUS_HASH_SIZE];

    memset(u8Customer_info, 0, MSRV_CUS_INFO_SIZE);
    memset(u8Customer_hash, 0, MSRV_CUS_HASH_SIZE);

    mapi_system * pSystem = mapi_interface::Get_mapi_system();
    CusInfo::GetInstance()->GetCustomerInfo(u8Customer_info, MSRV_CUS_INFO_SIZE);
    CusInfo::GetInstance()->GetHashKey(u8Customer_hash, MSRV_CUS_HASH_SIZE);
    pSystem->getBitTable(u8Customer_info, u8Customer_hash, BitTable,BitTableLen);
}
#endif

void MSrv_Control_common::GetCustomerInfo(MAPI_U8 * Customer_info,const int CustomerInfoLen)
{
    CusInfo::GetInstance()->GetCustomerInfo(Customer_info, CustomerInfoLen);
}
#if ( (OAD_ENABLE == 1) && ( FREEVIEW_AU_ENABLE == 1 ) )
BOOL MSrv_Control_common::SetOADInfo(ST_CUSTOMER_OAD_INFO *pstOADInfo)
{
    if (pstOADInfo)
    {
        memcpy(&m_stOADInfo, pstOADInfo, sizeof(ST_CUSTOMER_OAD_INFO));
    }
    return TRUE;
}

BOOL MSrv_Control_common::GetOADInfo(ST_CUSTOMER_OAD_INFO *pstOADInfo)
{
    if (pstOADInfo)
    {
        memcpy(pstOADInfo, &m_stOADInfo, sizeof(ST_CUSTOMER_OAD_INFO));
    }
    return TRUE;
}
#endif

#if (OAD_ENABLE == 1)
BOOL MSrv_Control_common::IsOADInMBoot()
{
    return m_bOADInMBoot;
}
#endif

void MSrv_Control_common::InitHeartBeat(void)
{
    int intPTHChk;

    m_u32HeartBeatTime = mapi_time_utility::GetTime0();
    if (m_pEvHeartBeat == NULL)
    {
        m_pEvHeartBeat = new (std::nothrow) mapi_event<THREAD_HEART_BEAT_INFO>;
    }
    ASSERT(m_pEvHeartBeat);
    pthread_attr_t thr_attr;
    pthread_attr_init(&thr_attr);
    pthread_attr_setdetachstate(&thr_attr, PTHREAD_CREATE_DETACHED);
    intPTHChk = PTH_RET_CHK(pthread_create(&m_pthreadHeartBeat,&thr_attr, HeartBeatMonitor, (void *)NULL));
    ASSERT(intPTHChk == 0);
    intPTHChk = PTH_RET_CHK(pthread_mutex_init(&m_MutHeartBeat, NULL));
    ASSERT(intPTHChk == 0);
}

void MSrv_Control_common::SendHeartBeat(EN_THREAD_HEART_BEAT_STATUS enStatus)
{
    THREAD_HEART_BEAT_INFO stThreadInfo;
    char cThreadName[64];
    mapi_scope_lock(scopeLock, &m_MutHeartBeat);

    if (m_pEvHeartBeat == NULL)
    {
        return;
    }

    stThreadInfo.enHearBeatStatus = enStatus;
    stThreadInfo.thread = pthread_self();
    stThreadInfo.threadId = (U32)syscall(SYS_gettid);
    prctl(PR_GET_NAME, cThreadName);
    stThreadInfo.sName = cThreadName;
    stThreadInfo.u32Time = 0;

    m_pEvHeartBeat->Send(stThreadInfo);
}

void *MSrv_Control_common::HeartBeatMonitor(void* ptr)
{
    THREAD_HEART_BEAT_INFO stThreadInfo;
    BOOL bSuspend = FALSE;
    prctl(PR_SET_NAME, (unsigned long)"HeartBeatMonitor");

    mapi_system *pSystem = mapi_interface::Get_mapi_system();
    //MS_USER_SYSTEM_SETTING stUserSetting;
    m_bHeartBeatMonitor = TRUE;
    while (m_bHeartBeatMonitor)
    {
        vector<THREAD_HEART_BEAT_INFO>::iterator it;
        int intPTHChk;

        intPTHChk = m_pEvHeartBeat->Wait(&stThreadInfo, THREAD_HEART_BEAT_TIME);

        if (intPTHChk == 0)
        {
            //printf("** Thread [%s], [%u] event %u\n", stThreadInfo.sName.data(), stThreadInfo.threadId, stThreadInfo.enHearBeatStatus);

            if (stThreadInfo.enHearBeatStatus == EN_THREAD_HEART_BEAT_MONITOR_SUSPEND)
            {
                bSuspend = TRUE;
            }

            if (stThreadInfo.enHearBeatStatus == EN_THREAD_HEART_BEAT_MONITOR_RESUME)
            {
                bSuspend = FALSE;
                for(it = m_vThreadInfo.begin(); it != m_vThreadInfo.end(); ++it)
                {
                    // refresh timer
                    it->u32Time = mapi_time_utility::GetTime0();
                }
            }


            stThreadInfo.u32Time = mapi_time_utility::GetTime0();

            for(it = m_vThreadInfo.begin(); it != m_vThreadInfo.end(); ++it)
            {
                if (it->threadId == stThreadInfo.threadId)
                {
                    it->u32Time = stThreadInfo.u32Time;
                    break;
                }
            }

            if ((it == m_vThreadInfo.end()) && (stThreadInfo.enHearBeatStatus == EN_THREAD_HEART_BEAT_ALIVE))
            {
                m_vThreadInfo.push_back(stThreadInfo);
            }
            else if (stThreadInfo.enHearBeatStatus == EN_THREAD_HEART_BEAT_FINALIZE)
            {
                if (it != m_vThreadInfo.end())
                {
                    m_vThreadInfo.erase(it);
                }
            }
        }

        //GetMSrvSystemDatabase()->GetUserSystemSetting(&stUserSetting);

        if (bSuspend == TRUE)
        {
            if(GetMSrvFactoryMode()->GetWDT_ONOFF())
            {
                pSystem->RefreshWatchDog();
            }
            continue;
        }


#if (MSTAR_TVOS == 0)
        U32 u32Timeout = 10000;
#else
        U32 u32Timeout = 30000;
#endif

        U8 idx = 0;
        for(it = m_vThreadInfo.begin(); it != m_vThreadInfo.end(); ++it)
        {
            //printf("Thread [%u] [%s], [%u] time %u\n", idx, it->sName.data(), it->threadId, it->u32Time);
            if (mapi_time_utility::GetTime0() >= (it->u32Time + u32Timeout))
            {
                printf("Thread [%u] [%s], [%u] time %u %u ----> Dead???\n", idx, it->sName.data(), it->threadId, mapi_time_utility::GetTime0(), it->u32Time);
                while(1)
                {
                    ;//do nothing
                }
            }
            idx++;
        }

        // refresh WDT
        if(GetMSrvFactoryMode()->GetWDT_ONOFF())
        {
            pSystem->RefreshWatchDog();
        }
    }

    pthread_exit(NULL);
}

#if (SSC_ENABLE == 1)
BOOL  MSrv_Control_common::UpdateSSCPara(void)
{
    MS_FACTORY_SSC_SET stSSCValue;
    GetMSrvSystemDatabase()->GetFactoryExtSetting((&stSSCValue), EN_FACTORY_EXT_SSC);

    mapi_display::SetLvdsSsc((MAPI_BOOL)stSSCValue.Lvds_SscEnable, (MAPI_U16)stSSCValue.Lvds_SscSpan, (MAPI_U16)stSSCValue.Lvds_SscStep);
    int miu_counts = MMAPInfo::GetInstance()->GetMIUCounts();
    for (int i= 0; i < miu_counts; i++)
    {
        mapi_miu::GetInstance()->SetMiuSscByIndex(i, (MAPI_BOOL)stSSCValue.Miu_SscEnable, (MAPI_U16)stSSCValue.Miu_SscSpan[i], (MAPI_U16)stSSCValue.Miu_SscStep[i]);
    }
    return MAPI_TRUE;
}
#endif

#if (ENABLE_LITE_SN != 1)
char * MSrv_Control_common::GetSystemBoardName(void)
{
    return SystemInfo::GetInstance()->GetBoardName();
}

char * MSrv_Control_common::GetSystemSoftWareVer(void)
{
    return SystemInfo::GetInstance()->GetSoftWareVer();
}

char * MSrv_Control_common::GetSystemPanelName(void)
{
    return SystemInfo::GetInstance()->GetSystemPanelName();
}
#endif

U16 MSrv_Control_common::GetSystemCurrentGammaTableNo(void)
{
    return SystemInfo::GetInstance()->GetCurrentGammaTableNo();
}

U16 MSrv_Control_common::GetSystemTotalGammaTableNo(void)
{
    return SystemInfo::GetInstance()->GetTotalGammaTableNo();
}

#if (ENABLE_LITE_SN != 1)
BOOL MSrv_Control_common::UpdatePanelIniFile(char * pKeycode,char * pKeyvalue)
{
    return SystemInfo::GetInstance()->IniUpdatePanelini(pKeycode, pKeyvalue);
}
BOOL MSrv_Control_common::UpdateCustomerIniFile(char *pKeycode ,char * pKeyValue)
{
    return SystemInfo::GetInstance()->IniUpdateCustomerini(pKeycode,pKeyValue);
}
#endif

BOOL MSrv_Control_common::UpdatePQParameterViaUsbKey(void)
{
    return SystemInfo::GetInstance()->UpdatePQParameterViaUsbKey();
}

#if (ENABLE_LITE_SN != 1)
BOOL MSrv_Control_common::GetUpdatePQFilePath(char pFilePath[MSRV_MAX_BUFFER], const EN_MSRV_PQ_UPDATE_FILE enPQFile)
{
    return SystemInfo::GetInstance()->GetUpdatePQFilePath(pFilePath, (EN_PQ_UPDATE_FILE)enPQFile);
}

void MSrv_Control_common::UpdatePQiniFiles()
{
    SystemInfo::GetInstance()->UpdatePQiniFiles();
}
#endif

void MSrv_Control_common::SystemReboot()
{
    ::SystemReboot();
}

#if (POWEROFF_MUSIC_ENABLE)
static MPLAYER_STREAM_IOCB _s_sStreamHandle;
static BOOLEAN _MSrv_Common_PlayMusic(char * fn, EN_MPLAYER_FILE_MODE eFileMode)
{
    MPLAYER_MEDIA sMplayerMeida;

    memset(&sMplayerMeida, 0, sizeof(MPLAYER_MEDIA));
    memset(&_s_sStreamHandle, 0, sizeof(MPLAYER_STREAM_IOCB));

    sMplayerMeida.eFileMode = eFileMode;

    sMplayerMeida.filename = fn;
    sMplayerMeida.sSubtitle.eSubtitleType = MPLAYER_SUBTITLE_INVALID;
    return MApi_MPlayer_PlayFile(E_MPLAYER_MEDIA_MUSIC, (MPLAYER_MEDIA *const)&sMplayerMeida, (MPLAYER_STREAM_IOCB *const)&_s_sStreamHandle);
}

static void _MSrv_Common_Music_CallBack(EN_MPLAYER_COMMAND_TYPE eCmd, unsigned int u32Param, unsigned int u32Info)
{
    printf("Music CallBack[0x%x][0x%x][0x%x]\n", eCmd, u32Param, u32Info);
    switch (eCmd)
    {
        case MPLAYER_COMMAND_NOTIFY:
            printf("Got notify from chakra[%d]\n", u32Param);
            switch(u32Param)
            {
                case MPLAYER_START_PLAY:
                    printf("Music Init ok!\n");
                    break;

                case MPLAYER_EXIT_ERROR_FILE:
                    printf("Music play fail!\n");
                    break;

                case MPLAYER_EXIT_OK:
                    printf("Music exit ok!\n");
                    break;

                default:
                    break;
            }
            break;

        default:
            break;
    }
}

//-------------------------------------------------------------------------------------------------
/// To play power off music when system power down
/// @param    const char *path   power off music patch
/// @param    timer_us   music play timer
/// @return   NULL
//-------------------------------------------------------------------------------------------------

void MSrv_Control_common::Play_Poweroff_Music(const char *path,int timer_us)
{
    if(path ==NULL)
    {
        ASSERT(0);
    }

    if(access(path,R_OK) != 0)//file not exit
    {
        printf("%s Not exit",path);
        return ;
    }
    GetMSrvSSSound()->SetMuteStatus(MUTE_BYUSER, TRUE);
    MApi_MPlayer_Initialize();

    MApi_MPlayer_SetOption(E_MPLAYER_MEDIA_MUSIC, MPLAYER_OPTION_SET_KTV_MODE, FALSE);
    MApi_MPlayer_SetOption(E_MPLAYER_MEDIA_MUSIC, MPLAYER_OPTION_RING_BUFFER_HANDLE, 1);
    MApi_MPlayer_RegisterCallBack(E_MPLAYER_MEDIA_MUSIC, _MSrv_Common_Music_CallBack);
    MApi_MPlayer_Stop(E_MPLAYER_MEDIA_MUSIC);
    GetMSrvSSSound()->SetVolume(GetMSrvSSSound()->GetVolume());
    _MSrv_Common_PlayMusic((char *)path, MPLAYER_FILE_MODE);
    GetMSrvSSSound()->SetMuteStatus(MUTE_BYUSER, FALSE);
    usleep(timer_us*1000);
    MApi_MPlayer_Stop(E_MPLAYER_MEDIA_MUSIC);
    MApi_MPlayer_Finalize();
}
#endif

BOOL MSrv_Control_common::WritePowerOnLogoMode(MAPI_U32 ePowerOnLogoMode)
{
    char buffer[256];
    int size = sizeof(buffer) / sizeof(char);
    MSRV_CONTROL_COMMON_INFO("%s:[%d]ePowerOnLogoMode=%u\n", __PRETTY_FUNCTION__, __LINE__, ePowerOnLogoMode);

    if (ePowerOnLogoMode == EN_POWERON_LOGO_DEFAULT)
    {
        snprintf(buffer, size, "sed -ir 's/[l|L][o|O][g|G][o|O]_[o|O][n|N][ ]*=[ ]*[0-9]/LOGO_ON = 1/' /Customer/boot.ini");
        MSRV_CONTROL_COMMON_INFO("%s:[%d]buffer=%s\n", __PRETTY_FUNCTION__, __LINE__, buffer);
        SystemCmd(buffer);
        snprintf(buffer, size, "sed -ir 's/[l|L][o|O][g|G][o|O]_[n|N][a|A][m|M][e|E][ ]*=[ ]*.*/LOGO_NAME = boot0.jpg;/' /Customer/boot.ini");
        MSRV_CONTROL_COMMON_INFO("%s:[%d]buffer=%s\n", __PRETTY_FUNCTION__, __LINE__, buffer);
        SystemCmd(buffer);
    }
    else if (ePowerOnLogoMode == EN_POWERON_LOGO_CAPTURE)
    {
        snprintf(buffer, size, "sed -ir 's/[l|L][o|O][g|G][o|O]_[o|O][n|N][ ]*=[ ]*[0-9]/LOGO_ON = 1/' /Customer/boot.ini");
        MSRV_CONTROL_COMMON_INFO("%s:[%d]buffer=%s\n", __PRETTY_FUNCTION__, __LINE__, buffer);
        system(buffer);
        snprintf(buffer, size, "sed -ir 's/[l|L][o|O][g|G][o|O]_[n|N][a|A][m|M][e|E][ ]*=[ ]*.*/LOGO_NAME = cap_logo.jpeg;/' /Customer/boot.ini");
        MSRV_CONTROL_COMMON_INFO("%s:[%d]buffer=%s\n", __PRETTY_FUNCTION__, __LINE__, buffer);
        SystemCmd(buffer);
    }
    else if (ePowerOnLogoMode == EN_POWERON_LOGO_OFF)
    {
        snprintf(buffer, size, "sed -ir 's/[l|L][o|O][g|G][o|O]_[o|O][n|N][ ]*=[ ]*[0-9]/LOGO_ON = 0/' /Customer/boot.ini");
        MSRV_CONTROL_COMMON_INFO("%s:[%d]buffer=%s\n", __PRETTY_FUNCTION__, __LINE__, buffer);
        SystemCmd(buffer);
    }
    else
    {
        MSRV_CONTROL_COMMON_DBG("%s:[%d]Invalid PowerOnLogoMode !!\n", __PRETTY_FUNCTION__, __LINE__);
    }

    sync();

    return MAPI_TRUE;
}

BOOL MSrv_Control_common::WritePowerOnMusicEnable(BOOL bPowerOnMusic)
{
    char buffer[256];
    int size = sizeof(buffer) / sizeof(char);

    MSRV_CONTROL_COMMON_INFO("%s:[%d]MusicEnable=%d\n", __PRETTY_FUNCTION__, __LINE__, bPowerOnMusic);

    snprintf(buffer, size, "sed -i 's/[m|M][u|U][s|S][i|I][c|C]_[o|O][n|N][ ]*=[ ]*[0-9]/MUSIC_ON = %d/' /Customer/boot.ini", (int)bPowerOnMusic);
    MSRV_CONTROL_COMMON_INFO("%s:[%d]buffer=%s\n", __PRETTY_FUNCTION__, __LINE__, buffer);
    SystemCmd(buffer);

    sync();
    sync();
    sync();

    return MAPI_TRUE;
}

#if (CEC_ENABLE == 1)
void* MSrv_Control_common::InitCECThread(void* arg)
{
    prctl(PR_SET_NAME, (unsigned long)"InitCECThread");
#if (STR_ENABLE == 1)
    mapi_str::AutoRegister _R;
#endif

    ST_INPUTSOURCE *InputSrc = (ST_INPUTSOURCE *)arg;
    GetMSrvCEC()->SetMyPhyAddr(InputSrc->OriInputSrc, InputSrc->CurrInputSrc);
    GetMSrvCEC()->RoutingChange(InputSrc->OriInputSrc, InputSrc->CurrInputSrc);

    {
        MS_CEC_SETTING CEC_Setting;
        memset(&CEC_Setting, 0, sizeof(CEC_Setting));

        GetMSrvCEC()->GetCECConfiguration(&CEC_Setting);

        if ( CEC_Setting.u8ARCStatus == TRUE )
        {
            GetMSrvCEC()->TxMsg_ReqARCInitiation() ;
        }
        else
        {
            GetMSrvCEC()->TxMsgDirectlySystemAudioModeRequest(E_MAPI_LA_AUDIO_SYS, FALSE) ;
        }
    }
    return (void *)0;
}

MSrv_CEC * MSrv_Control_common::GetMSrvCEC(void)
{
    MSRV_CONTROL_COMMON_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
    MSrv_CEC *p = dynamic_cast<MSrv_CEC *>(m_pMSrvList[E_MSRV_CEC]);
    ASSERT(p);
    return p;
}

void MSrv_Control_common::CECStart(void)
{
    pthread_attr_t attr2;
    pthread_attr_init(&attr2);
    pthread_attr_setstacksize(&attr2, PTHREAD_STACK_SIZE);

    m_stCecThreadInfo.pclass = GetMSrvCEC();

    if(m_stCecThreadInfo.bActive == FALSE)
    {
        m_stCecThreadInfo.bActive = TRUE;
        if(PTH_RET_CHK(pthread_create(&m_CECThread, &attr2, GetMSrvCEC()->CECMonitor, &m_stCecThreadInfo)) != 0)
        {
            ASSERT(0);
        }
    }
}
#endif

BOOL MSrv_Control_common::IsTVFinishBooting()
{
    mapi_scope_lock(scopeLock, &m_MutexPostVideoInit);

    return m_bBootupInit;
}

BOOL MSrv_Control_common::StartWatchDog()
{
    MS_USER_SYSTEM_SETTING stUserSetting;
    GetMSrvSystemDatabase()->GetUserSystemSetting(&stUserSetting);
#if (MSTAR_IPC == 0)
    if(stUserSetting.bEnableWDT)
    {
        mapi_interface::Get_mapi_system()->EnableWatchDog();
    }
    else
    {
        mapi_interface::Get_mapi_system()->DisableWatchDog();
    }
#endif
    return TRUE;
}

#if (MHL_ENABLE == 1)
MSrv_MHL * MSrv_Control_common::GetMSrvMHL(void)
{
    MSRV_CONTROL_COMMON_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
    MSrv_MHL *p = dynamic_cast<MSrv_MHL *>(m_pMSrvList[E_MSRV_MHL]);
    ASSERT(p);
    return p;
}
#endif //MHL_ENABLE

#if (HDMITX_ENABLE == 1)
MSrv_HDMITX * MSrv_Control_common::GetMSrvHdmiTx(void)
{
    MSRV_CONTROL_COMMON_INFO("%s line %d\n", __PRETTY_FUNCTION__, __LINE__);
    MSrv_HDMITX *p = dynamic_cast<MSrv_HDMITX *>(m_pMSrvList[E_MSRV_HDMITX]);
    ASSERT(p);
    return p;
}
#endif //HDMITX_ENABLE

#if (ENABLE_LITE_SN != 1)
BOOL MSrv_Control_common::StartUartDebug()
{
    return FALSE;
}
#endif

void MSrv_Control_common::StartThreadMonitor()
{

}

#if (STR_ENABLE == 1)
void MSrv_Control_common::StopThreadMonitor()
{
    if (m_monitor_st.m_bFlagThreadMonitorActive == TRUE)
    {
        m_monitor_st.m_bFlagThreadMonitorActive = FALSE;
    }

    if (m_pthreadMonitor_id != 0)
    {
        pthread_join(m_pthreadMonitor_id, NULL);
    }
}
#endif

void MSrv_Control_common::StartInsertModule()
{
    SystemCmd("insmod /lib/modules/alsa/snd-hwdep.ko");
    SystemCmd("insmod /lib/modules/alsa/snd-mixer-oss.ko");
    SystemCmd("insmod /lib/modules/alsa/snd-page-alloc.ko");
    SystemCmd("insmod /lib/modules/alsa/snd-rawmidi.ko");
    SystemCmd("insmod /lib/modules/alsa/snd-timer.ko");
    SystemCmd("insmod /lib/modules/alsa/snd-pcm.ko");
    SystemCmd("insmod /lib/modules/alsa/snd-pcm-oss.ko");
    SystemCmd("insmod /lib/modules/alsa/snd-usb-lib.ko");
    SystemCmd("insmod /lib/modules/alsa/snd-usbmidi-lib.ko");
    SystemCmd("insmod /lib/modules/alsa/snd-usb-audio.ko");

    // for MStar ALSA Driver
    SystemCmd("insmod /lib/modules/MStar_alsa/mdrv-alsa-audio.ko");
    SystemCmd("insmod /lib/modules/MStar_alsa/mhal-alsa-audio.ko");

#if (UMP_ENABLE == 1)
    // for UMP Kernel Driver
    SystemCmd("insmod /lib/modules/mali/ump.ko");
#endif

#if (MALI_ENABLE == 1)
    // for MALI Kernel Driver
#if (MALI_MIDGARD == 1)
    SystemCmd("insmod /lib/modules/mali/kds.ko");
    SystemCmd("insmod /lib/modules/mali/ump.ko");
    SystemCmd("insmod /lib/modules/mali/mali_kbase.ko");
#else
    SystemCmd("insmod /lib/modules/mali/mali.ko");
#endif
#endif

}

#if ((AUTO_TEST == 1) && (MSTAR_TVOS == 0))
void MSrv_Control_common::StartAutotest()
{

        FILE *cmdLine;
        char cmdLineBuf[CMDLINE_SIZE];

        cmdLine = fopen(CMDLINE_PATH, "r");
        if(cmdLine != NULL)
        {
            fgets(cmdLineBuf, CMDLINE_SIZE, cmdLine);
            fclose(cmdLine);

            if(strstr(cmdLineBuf, KEYWORD_TO_AUTOTEST))
            {
#if 0 // Remove HSL relative code base temporarily until HSL be implemented in new autotest architecture
                if(strstr(cmdLineBuf, KEYWORD_TO_HSL))
                {
                    GetMSrvAutoTest()->OpenHslFlag();
                }
#endif
                GetMSrvAutoTest()->Start();
            }
        }
        else
        {
            printf("\nRead cmdline FAIL!!\n\n");
        }

}
#endif

#if (MODULE_TEST == 1)
void MSrv_Control_common::StartModuletest()
{

    FILE *cmdLine;
    char cmdLineBuf[CMDLINE_SIZE];

    cmdLine = fopen(CMDLINE_PATH, "r");
    if(cmdLine != NULL)
    {
        fgets(cmdLineBuf, CMDLINE_SIZE, cmdLine);
        fclose(cmdLine);

        if(strstr(cmdLineBuf, KEYWORD_TO_MODULETEST))
        {
            GetMSrvModuleTest()->Start();
        }
    }
    else
    {
        printf("\nRead cmdline FAIL!!\n\n");
    }

}
#endif

#define KEYWORD_TO_BOOTLOGO_DELAY  "delaylogo=true"
BOOL MSrv_Control_common::FinishBootlogo()
{
    int ret = FALSE;
    int miu = 0;
    int controller = -1;
    FILE *fp = NULL;
    char buf[CMDLINE_SIZE] = {0};
    U8 bootlogo_gopidx = 0;

    IEnvManager* pEnvMan = IEnvManager::Instance();
    if(pEnvMan)
    {
        const char* pbootlogo_gop = NULL;
        pbootlogo_gop =pEnvMan->GetEnv("bootlogo_gopidx");
        if(pbootlogo_gop != NULL)
        {
            bootlogo_gopidx =(MAPI_U32) strtoul(pbootlogo_gop, 0, 10);
        }
        else
            MSRV_CONTROL_COMMON_ERR("connot find \"bootlogo_gopidx\" in bootargs!\n");
    }
    else
        MSRV_CONTROL_COMMON_ERR("pEnvMan=NULL!\n");

    MSRV_CONTROL_COMMON_FLOW("bootlogo_gopidx:%d \n",bootlogo_gopidx);

    controller = GetControllerByGOPIndex(bootlogo_gopidx);
    if(controller == 0xFE) //it means gop control by AN, close gop in an,wo do nothing in here.
    {
        MSRV_CONTROL_COMMON_FLOW("bootlogo gop control by AN, finish it by AN!\n");
        return TRUE;
    }
    else if(controller == 0xFF)
    {
        MSRV_CONTROL_COMMON_FLOW("bootlogo gop control by mboot, finish it by disable gwin!\n");
        mapi_display::SetGWINDisableByGOPIndex(bootlogo_gopidx);
        return TRUE;
    }
    else//it means gop control by dfb
    {
        MSRV_CONTROL_COMMON_FLOW("bootlogo gop control by DFB, layer=%d, finish it by DFB init GOP!\n", controller);
        fp = fopen(CMDLINE_PATH, "r");
        if (fp != NULL)
        {
            if (fgets(buf, CMDLINE_SIZE, fp) != NULL)
            {
                if (strstr(buf, KEYWORD_TO_BOOTLOGO_DELAY) == NULL)
                {
                    MSRV_CONTROL_COMMON_ERR("connot find \"%s\" in bootargs!\n", KEYWORD_TO_BOOTLOGO_DELAY);
                }
                else
                {
                    // find the index of the MIU which is used by DFB
                    miu = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_DFB_FRAMEBUFFER"))->u32MiuNo;
                    DirectFBSetBootLogoPatch(controller, miu);
                    if (DirectFBGetGOPDst(controller) == DLGD_IP0)
                        DirectFBSetGOPDst(controller, DLGD_IP0);
                    ret = TRUE;
                }
            }
            fclose(fp);
        }
        return ret;
    }
}

void MSrv_Control_common::SendBootUpEvent()
{
    mapi_scope_lock(scopeLock, &m_MutexPostVideoInit);

    if (NULL!=m_pBootUpEvent)
    {
        m_pBootUpEvent->Send(TRUE);
    }
}

BOOL MSrv_Control_common::PostVideoInit()
{
    BOOL bStatus = FALSE;

    if (NULL != m_pBootUpEvent)
    {
        // waiting signal, or timeout
        m_pBootUpEvent->Wait(&bStatus, POST_VIDEO_INIT_TIMEOUT);

        if ( FALSE == bStatus )
        {
            // timeout case
        }
#if (MSTAR_TVOS == 1)
        if( MAPI_TRUE == bFirstBoot )
        {
            int fd = open("/dev/console",O_RDWR);
            const char *pSnReady = "TVOS *checkPoint*\n";
            if (fd >= 0)
            {
                write(fd, pSnReady, strlen(pSnReady));
                close(fd);
            }

            /* for TV boottime */
            pid_t pid = fork();
            if (pid == 0)
            {
                /* renice TVOS to 0 */
                char const* args[] = { "start", "renice-tvos-0", NULL };
                unsetenv("LD_PRELOAD");
                execv("/system/bin/start", (char * const *)args);
            }
            else
            {
                if(waitpid(pid, NULL, 0) < 0)
                    perror("renice tvos to 0 fail !!!\n");
            }

            bFirstBoot = MAPI_FALSE;
        }
#endif
    }

#if ((AUTO_TEST == 1) && (MSTAR_TVOS == 0))
    printf("*checkPoint*\n");
#endif

    mapi_scope_lock(scopeLock, &m_MutexPostVideoInit);
    if (NULL!=m_pBootUpEvent)
    {
        delete m_pBootUpEvent;
        m_pBootUpEvent = NULL;
    }

    // Add the code for post-video init when boot-up, this part will init only once
    if (TRUE != m_bBootupInit)
    {
#if (MSTAR_TVOS == 0)
        //  tvos finish bootlogo in main.cpp
        FinishBootlogo();
#endif

        GetMSrvTimer()->InitThreads();

#if (OFL_DET == 1)
    MSrv_SrcDetect *pSrcDetect = GetMSrvSourceDetect();
    ASSERT(pSrcDetect);
    pSrcDetect->Init();
#endif

#if (CEC_ENABLE == 1)
        CECStart();
#endif

#if (MHL_ENABLE == 1)
        MSrv_Control::GetMSrvMHL()->Initialize();
#endif //MHL_ENABLE

        StartWatchDog();

#if (ENABLE_LITE_SN != 1)
#if (MSTAR_TVOS == 0)
        StartUartDebug();
#endif
#endif

        InitHeartBeat();

        MSrv_MountNotifier::GetInstance()->Start();

        StartInsertModule();

  #if (MSTAR_TVOS == 0)
        GetMSrvNetwork()->Initialize(NULL);
 #endif
#if (HBBTV_ENABLE == 1)
        if (1 == SystemInfo::GetInstance()->GetHbbtvDelayInitFlag())
            MW_HBBTV::GetInstance().Init();
#endif

#if (ENABLE_NETREADY == 1)
        GetMSrvDeviceAgent()->init();
#endif

    IEnvManager* pEnvMan = IEnvManager::Instance();
    if((NULL != pEnvMan) && pEnvMan->Initialize())
    {
        IEnvManager_scope_lock block(pEnvMan);
        int size=pEnvMan->QueryLength("sync_miuprot");
        int value = 0;
        char tmp[size+1];
        memset(tmp,0,size+1);
        bool pInfo =pEnvMan->GetEnv_Protect("sync_miuprot",tmp,size);
        value = atoi(tmp);
        if((pInfo) &&(value>0))
        {
            mapi_miu::GetInstance()->StartMiuMonitorThread();
        }
    }
#if ((AUTO_TEST == 1) && (MSTAR_TVOS == 0))
        StartAutotest();
#endif
#if (RVU_ENABLE == 1)
        GetMSrvRvu()->Initialize();
#endif
#if (MODULE_TEST == 1)
        StartModuletest();
#endif

#if (BRICK_TERMINATOR_ENABLE == 1)
        // when brick terminator info disable, will return 0
        if (0 != BrickTerminatorInfo_is_enable())
        {
            BrickTerminatorInfo_reset();
        }
#endif

#if(PIP_ENABLE == 1)
        SystemInfo::GetInstance()->SetPipInfoSet();
        SystemInfo::GetInstance()->SetPopInfoSet();
        if (GetPipMode() == E_PIP_MODE_POP)
        {
            MAPI_BOOL bEnable_4k2k_FRC = MAPI_FALSE;
            SystemInfo::GetInstance()->GetModuleParameter_bool("M_BACKEND:F_BACKEND_ENABLE_4K2K_FRC", &bEnable_4k2k_FRC);
            if(bEnable_4k2k_FRC == MAPI_TRUE)
            {
                mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stDispMainWin;
                U16 panelWidth = 3840, panelHeight = 2160;
                MSrv_Control::GetMSrvVideo()->GetPanelWidthHeight(&panelWidth, &panelHeight);

                stDispMainWin.x = 0;
                stDispMainWin.y = 0;
                stDispMainWin.width = panelWidth;
                stDispMainWin.height = panelHeight;

                mapi_interface::Get_mapi_video(MSrv_Control::GetInstance()->GetCurrentMainInputSource()) ->SetDispWinToDriver(&stDispMainWin, MAPI_MAIN_WINDOW);
            }
        }
#endif

#if (MWB_LAUNCHER_ENABLE ==1)
        // Init MstarLauncherAgent
        MstarLauncherAgent::GetInstance().Init();
#endif

#if (ACR_ENABLE == 1)
        MSrv_SambaTv::GetInstance()->SambaTVExecuteCidclient(true);
#endif

        // this will be the last step for boot-up
        m_bBootupInit = TRUE;
    }

    return TRUE;
}

#if (HBBTV_ENABLE==1)
void MSrv_Control_common::RedirectKeyEvent(int key, bool bypass)
{
    PostEvent(0, EV_KEY, key, bypass ? 1 : 0);
}
#endif

BOOL MSrv_Control_common::IsPipModeEnable()
{
    BOOL bPipModeEnable = FALSE;

#if (PIP_ENABLE == 1)
    if(m_enPipMode != E_PIP_MODE_OFF)
    {
        bPipModeEnable = TRUE;
    }
    else
    {
        if(m_bPipFinished == MAPI_FALSE)
        {
            bPipModeEnable  = TRUE;
        }
    }
#endif

    return bPipModeEnable;
}

BOOL MSrv_Control_common::IsFocusOnSubSource(void)
{
    BOOL bFocusOnSubSource = FALSE;

#if (PIP_ENABLE == 1)
    if(TRUE == IsPipModeEnable())
    {
        if(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_SUB_WINDOW) == m_enCurrentFocusSource)
        {
            bFocusOnSubSource = TRUE;
        }
    }
#endif

    return bFocusOnSubSource;
}

BOOL MSrv_Control_common::IsTravelingModeEnable(EN_TRAVELING_ENGINE_TYPE enEngineType)
{
#if (TRAVELING_ENABLE == 1)
    MS_BOOL bRet = MAPI_FALSE;
    if(enEngineType >= E_TRAVELING_ENGINE_TYPE_MAX)
    {
        printf("[%s][%d], error engine type(%u) > Max(%u) \n",__FUNCTION__,__LINE__, enEngineType, E_TRAVELING_ENGINE_TYPE_MAX);
    }
    else
    {
        if(IsTravelingMode(m_stTravelModeInfo[enEngineType].enTravelingSource, m_stTravelModeInfo[enEngineType].bCaptureOSD))
        {
            bRet = MAPI_TRUE;
        }
    }
    return bRet;
#else
    return FALSE; //Not support
#endif
}

EN_TRAVELING_RETURN MSrv_Control_common::GetTravelingEngineCaps(ST_TRAVELING_ENGINE_CAPS *pstTravelingCaps, EN_TRAVELING_ENGINE_TYPE enEngineType)
{
    EN_TRAVELING_RETURN eRet = E_TRAVELING_UNSUPPORT;
#if (TRAVELING_ENABLE == 1)
    ASSERT(enEngineType < E_TRAVELING_ENGINE_TYPE_MAX);
    if(pstTravelingCaps != NULL)
    {
        ST_TRAVELING_ENGINE_CAPS stSDKTravelingCaps;
        memset(&stSDKTravelingCaps, 0, sizeof(ST_TRAVELING_ENGINE_CAPS));
        stSDKTravelingCaps.u16TravelingEngineCaps_Length = sizeof(ST_TRAVELING_ENGINE_CAPS);
        stSDKTravelingCaps.u16TravelingEngineCaps_Version = TRAVELING_CAPS_INFO_MSDK_VERSION;
        if(enEngineType == E_TRAVELING_ENGINE_TYPE_SD)
        {
            stSDKTravelingCaps.bEngineExist = MAPI_TRUE; //For ve, return exist only , no other HW feature
            eRet = E_TRAVELING_SUCCESS;
        }
        else
        {
            eRet = mapi_video::GetTravelingEngineCaps(&stSDKTravelingCaps, (EN_TRAVELING_ENGINE_TYPE)enEngineType);
            if(E_TRAVELING_SUCCESS == eRet)
            {
                if(stSDKTravelingCaps.u16TravelingEngineCaps_Length > pstTravelingCaps->u16TravelingEngineCaps_Length)
                {
                    stSDKTravelingCaps.u16TravelingEngineCaps_Length = pstTravelingCaps->u16TravelingEngineCaps_Length;
                }
                memcpy(pstTravelingCaps, &stSDKTravelingCaps, stSDKTravelingCaps.u16TravelingEngineCaps_Length);
            }
        }
    }
    else
    {
        eRet = E_TRAVELING_INPUT_PARAMETER_ERROR;
    }
#endif
    return eRet;
}

#if (PIP_ENABLE == 1)
BOOL MSrv_Control_common::GetPipSupportedSubInputSourceList(EN_PIP_MODES pipMode, BOOL *pSubInputSourceList, U32 *pListSize, EN_TRAVELING_ENGINE_TYPE enEngineType)
{
    *pListSize = MAPI_INPUT_SOURCE_NUM;
    if(pipMode == E_PIP_MODE_PIP)
    {
        for(int i=0; i<MAPI_INPUT_SOURCE_NUM; i++)
        {
            pSubInputSourceList[i] = CheckPipSupport(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW), (MAPI_INPUT_SOURCE_TYPE)i);
        }
    }
    else if(pipMode == E_PIP_MODE_POP)
    {
        for(int i=0; i<MAPI_INPUT_SOURCE_NUM; i++)
        {
            pSubInputSourceList[i] = CheckPopSupport(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW), (MAPI_INPUT_SOURCE_TYPE)i);
        }
    }
#if (TRAVELING_ENABLE == 1)
    else if(pipMode == E_PIP_MODE_TRAVELING)
    {
        ASSERT(enEngineType < E_TRAVELING_ENGINE_TYPE_MAX);
        for(int i=0; i<MAPI_INPUT_SOURCE_NUM; i++)
        {
            pSubInputSourceList[i] = CheckTravelingModeSupport(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW), (MAPI_INPUT_SOURCE_TYPE)i, enEngineType);
        }
    }
#endif
    else
    {
        return FALSE;
    }
    return TRUE;
}

EN_PIP_MODES MSrv_Control_common::GetPipMode(void)
{
    return m_enPipMode;
}

BOOL MSrv_Control_common::CheckPipSupport(MAPI_INPUT_SOURCE_TYPE eMainInputSrc, MAPI_INPUT_SOURCE_TYPE eSubInputSrc)
{
    if(eMainInputSrc<0 || eMainInputSrc>MAPI_INPUT_SOURCE_NUM)
    {
        MSRV_CONTROL_PIP_ERR("MSrv_Control_common: CheckPipSupportCombination, eMainInputSrc(%d) error\n", (int)eMainInputSrc);
        ASSERT(0);
    }

    if(eSubInputSrc<0 || eSubInputSrc>MAPI_INPUT_SOURCE_NUM)
    {
        MSRV_CONTROL_PIP_ERR("MSrv_Control_common: CheckPipSupportCombination, eSubInputSrc(%d) error\n", (int)eSubInputSrc);
        ASSERT(0);
    }

    return SystemInfo::GetInstance()->GetPipPairInfo(eMainInputSrc, eSubInputSrc);
}

EN_PIP_RETURN MSrv_Control_common::EnablePipTV(MAPI_INPUT_SOURCE_TYPE eMainInputSrc, MAPI_INPUT_SOURCE_TYPE eSubInputSrc, const mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstDispWin)
{
    mapi_video * pMapiVideo = NULL;
    ST_VIDEO_INFO stVideoInfo;
    MSrv_Player *pMainMsrvPlayer = MSrv_Control::GetInstance()->GetMSrvPlayer(eMainInputSrc);
    mapi_pql* pMApiPql = mapi_pql::GetInstance(MAPI_PQ_MAIN_WINDOW);

    if(NULL == pstDispWin)
        return E_PIP_WINDOW_SETTING_ERROR;

    EN_PIP_MODES mode = GetPipMode();
    if(mode == E_PIP_MODE_POP)
    {
        return E_PIP_POP_MODE_OPENED;
    }
    if(mode == E_PIP_MODE_TRAVELING)
    {
        pMapiVideo = mapi_interface::Get_mapi_video(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_SUB_WINDOW));
        if(pMapiVideo != NULL)
        {
            //Mute traveling before PIP state change
            pMapiVideo->SetVideoMute(MAPI_TRUE);
        }
        resetPipPopToMainSource();
    }

#if (STEREO_3D_ENABLE == 1)
    EN_3D_TYPE e3DType = GetMSrv3DManager()->GetCurrent3DFormat();
    if(e3DType != EN_3D_NONE)
    {
        MSRV_CONTROL_PIP_ERR("%s[%d]: Error, e3DType(%u) is not OFF\n", __FUNCTION__, __LINE__, e3DType);
        return E_PIP_3D_MODE_OPENED;
    }
#endif

    BOOL bRet = CheckPipSupport(eMainInputSrc, eSubInputSrc);
    if(bRet == FALSE)
    {
        MSRV_CONTROL_PIP_ERR("%s[%d]: Error, unsupported PIP combination(%u, %u)\n", __FUNCTION__, __LINE__, eMainInputSrc, eSubInputSrc);
        return E_PIP_NOT_SUPPORT;
    }

    if((pMApiPql != NULL) && (pMainMsrvPlayer != NULL))
    {
        MAPI_PQ_CAP_INFO stCapInfo;
        memset(&stCapInfo, 0, sizeof(MAPI_PQ_CAP_INFO));
        if(pMApiPql->GetPQCaps(&stCapInfo) && (!stCapInfo.b4K2KPIP_Supported))
        {
            memset(&stVideoInfo, 0, sizeof(ST_VIDEO_INFO));
            pMainMsrvPlayer->GetVideoInfo(&stVideoInfo);
            if((((stVideoInfo.u16HResolution > (WIDTH_4K2K  - OFFSET_4K2K))
                  && (stVideoInfo.u16HResolution < (WIDTH_4K2K + OFFSET_4K2K)))
               || ((stVideoInfo.u16VResolution > (HEIGHT_4K2K - OFFSET_4K2K))
                  && (stVideoInfo.u16VResolution < (HEIGHT_4K2K + OFFSET_4K2K))))
                  && mapi_interface::Get_mapi_video(MSrv_Control::GetInstance()->GetCurrentMainInputSource())
                  && mapi_interface::Get_mapi_video(MSrv_Control::GetInstance()->GetCurrentMainInputSource())->IsActive())
            {
                if(MAPI_FALSE == IsSupportedFeature(E_MSRV_SUPPORTED_FEATURE_4K2K_PIP, NULL))
                {
                    MSRV_CONTROL_PIP_ERR("%s[%d]: Error, not support PIP with 4K2K!\n", __FUNCTION__, __LINE__);
                    return E_PIP_NOT_SUPPORT;
                }
            }
        }
    }

    if(mode == E_PIP_MODE_PIP)
    {
        if((eSubInputSrc == MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_SUB_WINDOW)) &&
            (eMainInputSrc == MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW)))
        {
            MSRV_CONTROL_COMMON_DBG("%s[%d]: Warning, Duplicate operation for source(%u, %u)\n", __FUNCTION__, __LINE__, eMainInputSrc, eSubInputSrc);
            return E_PIP_PIP_MODE_OPENED;
        }
    }

#if (STB_ENABLE == 0)
#if (CVBSOUT_XCTOVE_ENABLE == 0)
#if (VE_ENABLE == 1 ||CVBSOUT_ENABLE==1)
    if(IsTravelingModeEnable(E_TRAVELING_ENGINE_TYPE_SD) == FALSE)
    {
        mapi_video_out *pVideoOut = mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_TV_MODE);
        if((pVideoOut != NULL) && (pVideoOut->IsDestTypeExistent(MAPI_MAIN_WINDOW)) && (pVideoOut->IsActive()))
        {
            // For TV mode to always output ATV.
            pVideoOut->SetVideoMute(FALSE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, MAPI_MAIN_WINDOW);
            pVideoOut->Finalize(MAPI_MAIN_WINDOW);
        }
    }
#endif
#endif
#endif



    setPipMode(E_PIP_MODE_PIP);
    mapi_video::SetPipMainSubInputSourceType(eMainInputSrc, eSubInputSrc);

    //for pip -> pip, only change sub inputsource, no need to reload pq of main input source
    if(m_enPrePipMode == E_PIP_MODE_OFF)
    {
        //from none-->pip, disable dms
        GetMSrvPicture()->SetDMSV12L(FALSE);
        GetMSrvSSSound()->SetHPVolume(GetMSrvSSSound()->GetVolume());
        SetPipSubwindow(pstDispWin);
    }
//    printf("Start SetPipInputSource\n");

    setPipInputSource(eMainInputSrc, eSubInputSrc);

    return E_PIP_SUCCESS;
}

EN_PIP_RETURN MSrv_Control_common::EnablePipMM(MAPI_INPUT_SOURCE_TYPE eMainInputSrc, const mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstDispWin)
{
    return EnablePipTV(eMainInputSrc, MAPI_INPUT_SOURCE_STORAGE, pstDispWin);
}

BOOL MSrv_Control_common::resetPipPopToMainSource()
{
    m_bPipFinished = MAPI_FALSE;
    setPipMode(E_PIP_MODE_OFF);

    if(m_enPrePipMode == E_PIP_MODE_POP)
    {
        mapi_interface::Get_mapi_video(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW))->LockDispWindowSize(MAPI_FALSE, NULL);
        mapi_interface::Get_mapi_video(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_SUB_WINDOW))->LockDispWindowSize(MAPI_FALSE, NULL);
    }

    m_enCurrentFocusSource = MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW);
    mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stDispWin;
    BOOL bRet = setPipInputSource(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW), MAPI_INPUT_SOURCE_NONE);

    if(m_enPrePipMode == E_PIP_MODE_POP)
    {
        GetMSrvPicture()->Off();
        resetPQ();
    }
    else if(m_enPrePipMode == E_PIP_MODE_PIP)
    {
        resetPQ();
    }

    if(bRet == FALSE)
        return FALSE;

    m_bPipFinished = MAPI_TRUE;

#if (CEC_ENABLE == 1)
    CECStart();
#endif

    if(m_enPrePipMode == E_PIP_MODE_POP)
    {
        U16 panelWidth = 0;
        U16 panelHeight = 0;
        mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stCropWinTemp;
        mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stCapWinTemp;
        mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stDispWinTemp;

        memset(&stCropWinTemp, 0, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE));
        memset(&stCapWinTemp,  0, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE));
        memset(&stDispWinTemp, 0, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE));
        GetMSrvVideo()->GetPanelWidthHeight(&panelWidth, &panelHeight);
        stDispWin.x = 0;
        stDispWin.y = 0;
        stDispWin.width = panelWidth;
        stDispWin.height = panelHeight;
        setMainwindow(&stDispWin);
        //for rescale main window
        mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO stARCInfo;
        memset(&stARCInfo, 0, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO));
        MAPI_INPUT_SOURCE_TYPE sourceType = MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW);
        GetMSrvSystemDatabase()->GetVideoArc(&stARCInfo.enARCType, &sourceType);
        stARCInfo.s16Adj_ARC_Left = 0;
        stARCInfo.s16Adj_ARC_Right = 0;
        stARCInfo.s16Adj_ARC_Up = 0;
        stARCInfo.s16Adj_ARC_Down = 0;
        mapi_interface::Get_mapi_video(sourceType)->GetWindowInfo(&stCapWinTemp, &stCropWinTemp, &stDispWinTemp);
        mapi_interface::Get_mapi_video(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW))->SetWindow(&stCropWinTemp, &stDispWin, &stARCInfo);
        GetMSrvPicture()->On();
    }

    //from pip,pop->none, retore dms
    GetMSrvPicture()->SetDMSV12L(GetMSrvPicture()->GetDMSV12LFromXRuleTable());

    stDispWin.x = 0;
    stDispWin.y = 0;
    stDispWin.width = 0;
    stDispWin.height = 0;
    SetPipSubwindow(&stDispWin);
    return TRUE;
}

BOOL MSrv_Control_common::DisablePip(MAPI_SCALER_WIN eWin)
{
    EN_PIP_MODES mode = GetPipMode();
    if(mode==E_PIP_MODE_OFF || mode==E_PIP_MODE_POP || mode==E_PIP_MODE_TRAVELING)
        return FALSE;
#if (CHANNEL_CHANGE_FREEZE_IMAGE_BYDFB_ENBALE == 1)
        MSrv_Player *pMsrvPlayer = MSrv_Control::GetInstance()->GetMSrvPlayer(MSrv_Control::GetInstance()->GetCurrentSubInputSource());
        if((pMsrvPlayer->IsShowFreezeImageByDFB()==TRUE))
        {
            pMsrvPlayer->ShowFreezeImageByDFB(FALSE);
        }
#endif

#if (TRAVELING_ENABLE == 1)
    MAPI_U16 enEngineType = E_TRAVELING_ENGINE_TYPE_SD;

    for (;enEngineType < E_TRAVELING_ENGINE_TYPE_MAX; enEngineType++)
    {
    // invisible sub win or move sub win out of pnl when open pip and 2nd traveling mode both
        if (IsTravelingModeEnable((EN_TRAVELING_ENGINE_TYPE)enEngineType) == MAPI_TRUE)
    {
            if(m_stTravelModeInfo[enEngineType].enTravelingSource == E_TRAVELING_SUB_WINDOW)
        {
            MAPI_INPUT_SOURCE_TYPE eInputSource = MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_SUB_WINDOW);
            mapi_video * pMapiVideo = mapi_interface::Get_mapi_video(eInputSource);
            MS_BOOL bIsStable = FALSE, bXCReady = FALSE;

            if(pMapiVideo != NULL)
            {
                // 1. invisible sub win when signal is not stable,then player and traveling is still alive
                // 2. invisible sub win when traveling capture sub IP
                // 3. invisible sub win when traveling capture sub OP,and will visible sub win in disable traveling mute
                pMapiVideo->SetWindowEnable(MAPI_FALSE, MAPI_SUB_WINDOW);
                if(IsSrcStorage(eInputSource)) //MM player always return signal stable
                {
                    if((MAPI_FALSE == pMapiVideo->IsBlueBlackScreen(&bXCReady)) && bXCReady)
                    {
                        bIsStable = TRUE;
                    }
                }
                else
                {
                    bIsStable = GetMSrvPlayer(eInputSource)->IsSignalStable();
                }

                if (bIsStable)
                {
                    mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO stARCInfo;
                    memset(&stARCInfo, 0, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO));
                    pMapiVideo->SetVideoMute(MAPI_TRUE);  // stop traveling

                    setPipMode(E_PIP_MODE_TRAVELING);
                    GetMSrvSystemDatabase()->GetVideoArc(&stARCInfo.enARCType, &eInputSource);
                    stARCInfo.s16Adj_ARC_Left = 0;
                    stARCInfo.s16Adj_ARC_Right = 0;
                    stARCInfo.s16Adj_ARC_Up = 0;
                    stARCInfo.s16Adj_ARC_Down = 0;
                    pMapiVideo->SetTravelingWindow(NULL, &stARCInfo, (EN_TRAVELING_ENGINE_TYPE)enEngineType);   // PIP mode is traveling mode
                        pMapiVideo->SetVideoMute(MAPI_FALSE);  // start traveling

                    }
                }
                return TRUE;
            }
    }
    }
#endif //#if (TRAVELING_ENABLE == 1)
    BOOL bRet = FALSE;
    //Reset the focus inputsource to main
    GetMSrvVideo()->SelectWindow(MAPI_MAIN_WINDOW);
    //GetCurrentSubInputSource must put before resetPipPopToMainSource, because in GetCurrentSubInputSource, m_enCurrentSubSource will be set to NONE
    MAPI_INPUT_SOURCE_TYPE eSubInputSrc = GetCurrentSubInputSource();
    bRet = resetPipPopToMainSource();
    if(bRet == FALSE)
    {
        return bRet;
    }

    if (eWin == MAPI_SUB_WINDOW)
    {
        if(eSubInputSrc == MAPI_INPUT_SOURCE_DTV2)
        {
           eSubInputSrc = MAPI_INPUT_SOURCE_DTV;
        }
        bRet = SetInputSource(eSubInputSrc);
    }

#if (STB_ENABLE == 0)
#if (CVBSOUT_XCTOVE_ENABLE == 0)
#if (VE_ENABLE == 1 ||CVBSOUT_ENABLE==1)
    if(IsVideoOutTVModeFreeToUse() == TRUE)
    {
        //No pip mode, then can enable tv mode av out
        mapi_video_out *pVideoOut = mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_TV_MODE);
        if((pVideoOut != NULL) && (pVideoOut->IsDestTypeExistent(MAPI_MAIN_WINDOW)))
        {
            // For TV mode to always output ATV.
            pVideoOut->Initialize(MAPI_INPUT_SOURCE_ATV, MAPI_MAIN_WINDOW);
            pVideoOut->SetVideoMute(FALSE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, MAPI_MAIN_WINDOW);
        }
    }
#endif
#endif
#endif

    mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_USER_SPEAKER_MUTEOFF_ , E_AUDIOMUTESOURCE_ACTIVESOURCE_);
    mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_USER_HP_MUTEOFF_ , E_AUDIOMUTESOURCE_ACTIVESOURCE_);
    return bRet;
}

BOOL MSrv_Control_common::SetPipSubwindow(const mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstSubDispWin)
{
    mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stCurDispWin;
    memset(&stCurDispWin, 0, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE));

    if(pstSubDispWin == NULL)
        return FALSE;

    U16 panelWidth = 0, panelHeight = 0;
    GetMSrvVideo()->GetPanelWidthHeight(&panelWidth, &panelHeight);

    if(pstSubDispWin->width < 0)
    {
        MSRV_CONTROL_PIP_ERR("MSrv_Control_common: SetPipSubwindow, The subwindow width setting error %d\n", (int)pstSubDispWin->width);
        return FALSE;
    }
    /*//Remove below limitation for dualview display
    if(panelWidth/2 > m_PipHardwareLimitation)
    {
        if(pstSubDispWin->width > m_PipHardwareLimitation)
        {
            MSRV_CONTROL_PIP_ERR("MSrv_Control_common: SetPipSubwindow, The subwindow width can't larger than %d\n", (int)m_PipHardwareLimitation);
            return FALSE;
        }
    }
    else if(pstSubDispWin->width > panelWidth/2)
    {
        MSRV_CONTROL_PIP_ERR("MSrv_Control_common: SetPipSubwindow, The subwindow width can't larger than %d\n", (int)(panelWidth/2));
        return FALSE;
    }*/
    if(pstSubDispWin->x<0 || pstSubDispWin->x>panelWidth)
    {
        MSRV_CONTROL_PIP_ERR("MSrv_Control_common: SetPipSubwindow, The subwindow x position error\n");
        return FALSE;
    }
    if(pstSubDispWin->width+pstSubDispWin->x > panelWidth)
    {
        MSRV_CONTROL_PIP_ERR("MSrv_Control_common: SetPipSubwindow, The subwindow width exist panel boundary\n");
        return FALSE;
    }

    if(pstSubDispWin->height < 0)
    {
        MSRV_CONTROL_PIP_ERR("MSrv_Control_common: SetPipSubwindow, The subwindow height setting error %d\n", (int)pstSubDispWin->height);
        return FALSE;
    }
    if(pstSubDispWin->height > panelHeight)
    {
        MSRV_CONTROL_PIP_ERR("MSrv_Control_common: SetPipSubwindow, The subwindow heightcan't larger than %d\n", (int)panelHeight);
        return FALSE;
    }
    if(pstSubDispWin->y<0 || pstSubDispWin->y>panelHeight)
    {
        MSRV_CONTROL_PIP_ERR("MSrv_Control_common: SetPipSubwindow, The subwindow y position error\n");
        return FALSE;
    }
    if(pstSubDispWin->height+pstSubDispWin->y > panelHeight)
    {
        MSRV_CONTROL_PIP_ERR("MSrv_Control_common: SetPipSubwindow, The subwindow panelHeight exist panel boundary\n");
        return FALSE;
    }

    memcpy(&m_stSubWinInfo, pstSubDispWin, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE));

    //for rescale sub window
    if(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_SUB_WINDOW) != MAPI_INPUT_SOURCE_NONE)
    {
        mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO stARCInfo;
        memset(&stARCInfo, 0, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO));
        MAPI_INPUT_SOURCE_TYPE sourceType = MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_SUB_WINDOW);
        GetMSrvSystemDatabase()->GetVideoArc(&stARCInfo.enARCType, &sourceType);
        stARCInfo.s16Adj_ARC_Left = 0;
        stARCInfo.s16Adj_ARC_Right = 0;
        stARCInfo.s16Adj_ARC_Up = 0;
        stARCInfo.s16Adj_ARC_Down = 0;
        //mapi_interface::Get_mapi_video(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_SUB_WINDOW))->SetVideoMute(MAPI_TRUE, mapi_video_datatype::E_SCREEN_MUTE_BLACK,50);

        GetMSrvVideo()->SelectWindow(MAPI_SUB_WINDOW);
        GetMSrvVideo()->GetDisplayWindow(&stCurDispWin);
        if((stCurDispWin.width == m_stSubWinInfo.width) && (stCurDispWin.height == m_stSubWinInfo.height))
        {
             GetMSrvVideo()->SetDisplayWindow(&m_stSubWinInfo);
             GetMSrvVideo()->MoveWindowPosition();
        }
        else
        {
             mapi_interface::Get_mapi_video(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_SUB_WINDOW))->SetWindow(NULL, &m_stSubWinInfo, &stARCInfo);
        }
        GetMSrvVideo()->SelectWindow(MAPI_MAIN_WINDOW);
        mapi_interface::Get_mapi_video(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_SUB_WINDOW))->SetDispWinToDriver(&m_stSubWinInfo, MAPI_SUB_WINDOW);//set display window for no signal case
        SetVideoMute(MAPI_FALSE ,50,MAPI_SUB_WINDOW);
    }

    return TRUE;
}

BOOL MSrv_Control_common::GetPipSubwindow(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstSubDispWin)
{
    if(pstSubDispWin == NULL)
        return FALSE;

    memcpy(pstSubDispWin, &m_stSubWinInfo, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE));
    return TRUE;
}

BOOL MSrv_Control_common::GetMainwindow(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstMainDispWin)
{
    if(pstMainDispWin == NULL)
        return FALSE;

    memcpy(pstMainDispWin, &m_stMainWinInfo, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE));
    return TRUE;
}

BOOL MSrv_Control_common::CheckPopSupport(MAPI_INPUT_SOURCE_TYPE eMainInputSrc, MAPI_INPUT_SOURCE_TYPE eSubInputSrc)
{
    if(eMainInputSrc<0 || eMainInputSrc>MAPI_INPUT_SOURCE_NUM)
    {
        MSRV_CONTROL_PIP_ERR("MSrv_Control_common: CheckPopSupportCombination, eMainInputSrc(%d) error\n", (int)eMainInputSrc);
        ASSERT(0);
    }

    if(eSubInputSrc<0 || eSubInputSrc>MAPI_INPUT_SOURCE_NUM)
    {
        MSRV_CONTROL_PIP_ERR("MSrv_Control_common: CheckPopSupportCombination, eSubInputSrc(%d) error\n", (int)eSubInputSrc);
        ASSERT(0);
    }

    return SystemInfo::GetInstance()->GetPopPairInfo(eMainInputSrc, eSubInputSrc);
}

EN_PIP_RETURN MSrv_Control_common::EnablePopTV(MAPI_INPUT_SOURCE_TYPE eMainInputSrc, MAPI_INPUT_SOURCE_TYPE eSubInputSrc, const mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstDispMainWin, const mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstDispSubWin)
{
    EN_PIP_MODES mode = GetPipMode();
    mapi_video * pMapiVideo = NULL;
    ST_VIDEO_INFO stVideoInfo;
    MSrv_Player *pMainMsrvPlayer = MSrv_Control::GetInstance()->GetMSrvPlayer(eMainInputSrc);
    mapi_pql* pMApiPql = mapi_pql::GetInstance(MAPI_PQ_MAIN_WINDOW);

    if(mode == E_PIP_MODE_PIP)
    {
        return E_PIP_PIP_MODE_OPENED;
    }

    if(mode == E_PIP_MODE_TRAVELING)
    {
        pMapiVideo = mapi_interface::Get_mapi_video(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_SUB_WINDOW));
        if(pMapiVideo != NULL)
        {
            //Mute traveling before PIP state change
            pMapiVideo->SetVideoMute(MAPI_TRUE);
        }
        resetPipPopToMainSource();
    }

#if (STEREO_3D_ENABLE == 1)
    EN_3D_TYPE e3DType = GetMSrv3DManager()->GetCurrent3DFormat();
    if((e3DType != EN_3D_NONE) && (e3DType != EN_3D_DUALVIEW))
    {
        MSRV_CONTROL_PIP_ERR("%s[%d]: Error, e3DType(%u) is not OFF\n", __FUNCTION__, __LINE__, e3DType);
        return E_PIP_3D_MODE_OPENED;
    }
#endif

    BOOL bRet = CheckPopSupport(eMainInputSrc, eSubInputSrc);
    if(bRet == FALSE)
    {
        MSRV_CONTROL_PIP_ERR("%s[%d]: Error, unsupported POP combination(%u, %u)\n", __FUNCTION__, __LINE__, eMainInputSrc, eSubInputSrc);
        return E_PIP_NOT_SUPPORT;
    }

    if((pMApiPql != NULL) && (pMainMsrvPlayer != NULL))
    {
        MAPI_PQ_CAP_INFO stCapInfo;
        memset(&stCapInfo, 0, sizeof(MAPI_PQ_CAP_INFO));
        if(pMApiPql->GetPQCaps(&stCapInfo) && (!stCapInfo.b4K2KPIP_Supported))
        {
            memset(&stVideoInfo, 0, sizeof(ST_VIDEO_INFO));
            pMainMsrvPlayer->GetVideoInfo(&stVideoInfo);
            if((stVideoInfo.u16HResolution > (WIDTH_4K2K  - OFFSET_4K2K))
               || (stVideoInfo.u16VResolution > (HEIGHT_4K2K - OFFSET_4K2K)))

            {
                if(MAPI_FALSE == IsSupportedFeature(E_MSRV_SUPPORTED_FEATURE_4K2K_PIP, NULL))
                {
                    MSRV_CONTROL_PIP_ERR("%s[%d]: Error, not support POP with 4K2K!\n", __FUNCTION__, __LINE__);
                    return E_PIP_NOT_SUPPORT;
                }
            }
        }
    }

    if(mode == E_PIP_MODE_POP)
    {
        if( (eSubInputSrc == MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_SUB_WINDOW)) &&
            (eMainInputSrc == MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW)))
        {
            MSRV_CONTROL_COMMON_DBG("%s[%d]: Warning, Duplicate operation for source(%u, %u)\n", __FUNCTION__, __LINE__, eMainInputSrc, eSubInputSrc);
            return E_PIP_POP_MODE_OPENED;
        }
    }

#if (STB_ENABLE == 0)
#if (CVBSOUT_XCTOVE_ENABLE == 0)
#if (VE_ENABLE == 1 ||CVBSOUT_ENABLE==1)
    if(IsTravelingModeEnable(E_TRAVELING_ENGINE_TYPE_SD) == FALSE)
    {
        mapi_video_out *pVideoOut = mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_TV_MODE);
        if((pVideoOut != NULL) && (pVideoOut->IsDestTypeExistent(MAPI_MAIN_WINDOW)) && (pVideoOut->IsActive()))
    {
            // For TV mode to always output ATV.
            pVideoOut->SetVideoMute(FALSE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, MAPI_MAIN_WINDOW);
            pVideoOut->Finalize(MAPI_MAIN_WINDOW);
        }
    }
#endif
#endif
#endif


    setPipMode(E_PIP_MODE_POP);
    mapi_video::SetPipMainSubInputSourceType(eMainInputSrc, eSubInputSrc);

    mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stDispMainWin, stDispSubWin;

    if(pstDispMainWin==NULL || pstDispSubWin==NULL)
    {
        MSRV_CONTROL_COMMON_DBG("%s, %d, Either pstDispMainWin or pstDispSubWin is NULL, use the default window setting\n", __PRETTY_FUNCTION__, __LINE__);
        U16 panelWidth = 0, panelHeight = 0;
        GetMSrvVideo()->GetPanelWidthHeight(&panelWidth, &panelHeight);
        U16 shifty = 0, shiftx=0;
        if(panelWidth == 1920)
            shiftx = ((panelWidth)/2-m_PipHardwareLimitation)/2;
        //main
        stDispMainWin.x = shiftx;
        stDispMainWin.y = shifty;
        stDispMainWin.width = panelWidth/2-shiftx*2;
        stDispMainWin.height = panelHeight-shifty*2;
        setMainwindow(&stDispMainWin);
        //sub
        stDispSubWin.x = panelWidth/2+shiftx;
        stDispSubWin.y = shifty;
        stDispSubWin.width = panelWidth/2-shiftx*2;
        stDispSubWin.height = panelHeight-shifty*2;
    }
    else
    {
        memcpy(&stDispMainWin, pstDispMainWin, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE));
        memcpy(&stDispSubWin, pstDispSubWin, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE));
    }

    //for rescale main window
    mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO stARCInfo;
    memset(&stARCInfo, 0, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO));
    MAPI_INPUT_SOURCE_TYPE sourceType = MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW);
    GetMSrvSystemDatabase()->GetVideoArc(&stARCInfo.enARCType, &sourceType);
    stARCInfo.s16Adj_ARC_Left = 0;
    stARCInfo.s16Adj_ARC_Right = 0;
    stARCInfo.s16Adj_ARC_Up = 0;
    stARCInfo.s16Adj_ARC_Down = 0;

    //Lock display window for POP
    mapi_interface::Get_mapi_video(eMainInputSrc)->LockDispWindowSize(TRUE, &stDispMainWin);
    mapi_interface::Get_mapi_video(eSubInputSrc)->LockDispWindowSize(TRUE, &stDispSubWin);

    //for pop -> pop, only change sub inputsource, no need to reload pq of main input source
    if(m_enPrePipMode == E_PIP_MODE_OFF)
    {
        setMainwindow(&stDispMainWin);
        GetMSrvPicture()->Off();
        resetPQ();
        GetMSrvPicture()->SetDMSV12L(FALSE);
        if(eMainInputSrc == MAPI_INPUT_SOURCE_STORAGE)
        {
            MApi_XC_set_FD_Mask(FALSE);
        }
        mapi_interface::Get_mapi_video(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW))->SetWindow(NULL, &stDispMainWin, &stARCInfo);
        if(eMainInputSrc == MAPI_INPUT_SOURCE_STORAGE)
        {
            MApi_XC_set_FD_Mask(TRUE);
        }
        GetMSrvPicture()->On();
        GetMSrvSSSound()->SetHPVolume(GetMSrvSSSound()->GetVolume());
        SetPipSubwindow(&stDispSubWin);
    }
    else // when displaywindow of main input source changes
    {
        if(  !((m_stMainWinInfo.x == stDispMainWin.x)
            && (m_stMainWinInfo.y == stDispMainWin.y)
            && (m_stMainWinInfo.width == stDispMainWin.width)
            && (m_stMainWinInfo.height == stDispMainWin.height))
         )
        {
            setMainwindow(&stDispMainWin);
            GetMSrvPicture()->Off();
            mapi_interface::Get_mapi_video(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW))->SetWindow(NULL, &stDispMainWin, &stARCInfo);
            GetMSrvPicture()->On();
        }
    }

    setPipInputSource(eMainInputSrc, eSubInputSrc);

    return E_PIP_SUCCESS;
}

EN_PIP_RETURN MSrv_Control_common::EnablePopMM(MAPI_INPUT_SOURCE_TYPE eMainInputSrc, const mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstDispMainWin, const mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstDispSubWin)
{
    return EnablePopTV(eMainInputSrc, MAPI_INPUT_SOURCE_STORAGE, pstDispMainWin, pstDispSubWin);
}

BOOL MSrv_Control_common::DisablePop(MAPI_SCALER_WIN eWin)
{
    EN_PIP_MODES mode = GetPipMode();
    if(mode==E_PIP_MODE_OFF || mode==E_PIP_MODE_PIP || mode==E_PIP_MODE_TRAVELING)
        return FALSE;
#if (CHANNEL_CHANGE_FREEZE_IMAGE_BYDFB_ENBALE == 1)
            MSrv_Player *pMsrvPlayer = MSrv_Control::GetInstance()->GetMSrvPlayer(MSrv_Control::GetInstance()->GetCurrentSubInputSource());
            if((pMsrvPlayer->IsShowFreezeImageByDFB()==TRUE))
            {
                pMsrvPlayer->ShowFreezeImageByDFB(FALSE);
            }
#endif

    BOOL bRet = FALSE;

    SetVideoMute(MAPI_TRUE, 0,MAPI_SUB_WINDOW);

    //Reset the focus inputsource to main
    GetMSrvVideo()->SelectWindow(MAPI_MAIN_WINDOW);
    //GetCurrentSubInputSource must put before resetPipPopToMainSource, because in GetCurrentSubInputSource, m_enCurrentSubSource will be set to NONE
    MAPI_INPUT_SOURCE_TYPE eSubInputSrc = GetCurrentSubInputSource();

    MAPI_BOOL bSetMute = MAPI_FALSE;

    mapi_video *pVideo;
    pVideo = mapi_interface::Get_mapi_video(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW));
    if((pVideo != NULL) && (pVideo->IsVideoMute() == MAPI_FALSE))
    {
        pVideo->SetVideoMute(MAPI_TRUE);
        bSetMute = MAPI_TRUE;
    }

    bRet = resetPipPopToMainSource();
    if(bRet == FALSE)
    {
        return bRet;
    }

    if (eWin == MAPI_SUB_WINDOW)
    {
        if(eSubInputSrc == MAPI_INPUT_SOURCE_DTV2)
        {
           eSubInputSrc = MAPI_INPUT_SOURCE_DTV;
        }
        bRet = SetInputSource(eSubInputSrc);
    }

    MAPI_INPUT_SOURCE_TYPE eMainInputSource = GetCurrentMainInputSource();
    MS_BOOL bIsStable = GetMSrvPlayer(eMainInputSource)->IsSignalStable();
    if((pVideo != NULL) && (bIsStable || (eMainInputSource==MAPI_INPUT_SOURCE_ATV)) && (bSetMute == MAPI_TRUE))
    {
        pVideo->SetVideoMute(MAPI_FALSE, 1000 );
    }
#if (STB_ENABLE == 0)
#if (CVBSOUT_XCTOVE_ENABLE == 0)
#if (VE_ENABLE == 1 ||CVBSOUT_ENABLE==1)
    if(IsVideoOutTVModeFreeToUse() == TRUE)
    {
        //No pip mode, then can enable tv mode av out
        mapi_video_out *pVideoOut = mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_TV_MODE);
        if((pVideoOut != NULL) && (pVideoOut->IsDestTypeExistent(MAPI_MAIN_WINDOW)))
        {
            // For TV mode to always output ATV.
            pVideoOut->Initialize(MAPI_INPUT_SOURCE_ATV, MAPI_MAIN_WINDOW);
            pVideoOut->SetVideoMute(FALSE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, MAPI_MAIN_WINDOW);
        }
    }
#endif
#endif
#endif

    mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_USER_SPEAKER_MUTEOFF_ , E_AUDIOMUTESOURCE_ACTIVESOURCE_);
    mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_USER_HP_MUTEOFF_ , E_AUDIOMUTESOURCE_ACTIVESOURCE_);
    return bRet;
}

//private:
void MSrv_Control_common::resetPQ()
{
    mapi_interface::Get_mapi_pql(MAPI_MAIN_WINDOW)->ResetPqType();
}

BOOL MSrv_Control_common::setMainwindow(const mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstMainDispWin)
{
    if(pstMainDispWin == NULL)
        return FALSE;

    memcpy(&m_stMainWinInfo, pstMainDispWin, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE));

    return TRUE;
}

void MSrv_Control_common::setPipMode(EN_PIP_MODES ePipMode)
{
    EN_MAPI_PIP_MODES mapiPipMode;
    m_enPrePipMode = m_enPipMode;
    m_enPipMode = ePipMode;
    mapi_pql_datatype::MAPI_PQL_PIP_MODE ePqPipMode;

    switch(m_enPipMode)
    {
        case E_PIP_MODE_OFF:
            mapiPipMode = E_MAPI_PIP_MODE_OFF;
            break;
        case E_PIP_MODE_PIP:
            mapiPipMode = E_MAPI_PIP_MODE_PIP;
            break;
        case E_PIP_MODE_POP:
            mapiPipMode = E_MAPI_PIP_MODE_POP;
            break;
#if (TRAVELING_ENABLE == 1)
        case E_PIP_MODE_TRAVELING:
            mapiPipMode = E_MAPI_PIP_MODE_TRAVELING;
            break;
#endif
        default:
            mapiPipMode = E_MAPI_PIP_MODE_OFF;
            break;
    }

    mapi_video::SetPipMode(mapiPipMode);
    if (mapiPipMode==E_MAPI_PIP_MODE_OFF)
        mapi_video::SetPipMainSubInputSourceType(MAPI_INPUT_SOURCE_NONE, MAPI_INPUT_SOURCE_NONE);

    switch(ePipMode)
    {
        case E_PIP_MODE_OFF:
            ePqPipMode =  mapi_pql_datatype::E_MAPI_PQL_PIP_MODE_OFF;
            break;
        case E_PIP_MODE_PIP:
            ePqPipMode =  mapi_pql_datatype::E_MAPI_PQL_PIP_MODE_PIP;
            break;
        case E_PIP_MODE_POP:
            ePqPipMode =  mapi_pql_datatype::E_MAPI_PQL_PIP_MODE_POP;
            break;
#if (TRAVELING_ENABLE == 1)
        case E_PIP_MODE_TRAVELING:
            ePqPipMode =  mapi_pql_datatype::E_MAPI_PQL_PIP_MODE_TRAVELING;
            break;
#endif
        default:
            ePqPipMode =  mapi_pql_datatype::E_MAPI_PQL_PIP_MODE_INVALID;
            break;
    }

    mapi_interface::Get_mapi_pql(MAPI_MAIN_WINDOW)->SetPIPMode(ePqPipMode);
    mapi_interface::Get_mapi_pql(MAPI_SUB_WINDOW)->SetPIPMode(ePqPipMode);
}

BOOL MSrv_Control_common::setPipInputSource(MAPI_INPUT_SOURCE_TYPE eMainInputSrc, MAPI_INPUT_SOURCE_TYPE eSubInputSrc)
{
    printf("Function = %s, Line = %d\n", __PRETTY_FUNCTION__, __LINE__);
    if(eMainInputSrc<0 || eMainInputSrc>MAPI_INPUT_SOURCE_NUM)
    {
        MSRV_CONTROL_PIP_ERR("MSrv_Control_common: SetPipInputSource, eMainInputSrc(%d) error\n", (int)eMainInputSrc);
        ASSERT(0);
    }

    //Not PIP mode should not use this API
    if(m_enPipMode==E_PIP_MODE_OFF && m_enPrePipMode==E_PIP_MODE_OFF)
    {
        MSRV_CONTROL_PIP_ERR("MSrv_Control_common: SetPipInputSource, this api only for pip use\n");
        return FALSE;
    }

    //Not PIP mode should not use this API
    if((eMainInputSrc<0||eMainInputSrc>=MAPI_INPUT_SOURCE_NUM||eMainInputSrc==MAPI_INPUT_SOURCE_NONE)
        && (eSubInputSrc<0||eSubInputSrc>=MAPI_INPUT_SOURCE_NUM||eSubInputSrc==MAPI_INPUT_SOURCE_NONE))
    {
        MSRV_CONTROL_PIP_ERR("MSrv_Control_common: SetPipInputSource, main/sub can't all none input source \n");
        m_enPipMode = m_enPrePipMode;
        return FALSE;
    }

    int ret = FALSE;

    //PIP --> none PIP, (main to full screen)
    if(m_enPipMode==E_PIP_MODE_OFF && m_enPrePipMode> E_PIP_MODE_OFF)
    {
        if(eSubInputSrc == MAPI_INPUT_SOURCE_NONE)
        {
            printf("PIP --> non PIP Function, main exists = %s, Line = %d\n", __PRETTY_FUNCTION__, __LINE__);
            SetInputSource(MAPI_INPUT_SOURCE_NONE, FALSE, FALSE, FALSE, MAPI_SUB_WINDOW);
            SetInputSource(eMainInputSrc, FALSE, FALSE, FALSE, MAPI_MAIN_WINDOW);
            if(GetMSrvPlayer(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW)) != NULL)
            {
                GetMSrvPlayer(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW))->OnNotifyPipDisable();
            }
            return TRUE;
        }
        else
        {
            MSRV_CONTROL_PIP_ERR("PIP --> non PIP Function, Sub can't be full screen = %s, Line = %d\n", __PRETTY_FUNCTION__, __LINE__);
            m_enPipMode = m_enPrePipMode;
            return FALSE;
            //SetInputSource(MAPI_INPUT_SOURCE_NONE, FALSE, FALSE, FALSE, FALSE, MAPI_MAIN_WINDOW);
            //ret = SetInputSource(eSubInputSrc, FALSE, FALSE, FALSE, FALSE, MAPI_SUB_WINDOW);
        }
    }

    if(m_enPipMode> E_PIP_MODE_OFF && (eSubInputSrc<0 || eSubInputSrc>MAPI_INPUT_SOURCE_NUM))
    {
        MSRV_CONTROL_PIP_ERR("MSrv_Control_common: SetInputSource, eSubInputSrc(%d) error\n", (int)eSubInputSrc);
        ASSERT(0);
    }
    //PIP --> PIP
    if(m_enPipMode>0 && m_enPrePipMode>0)
    {
        if(m_enPipMode != m_enPrePipMode)
        {
            printf("PIP --> PIP Function = %s, Line = %d\n", __PRETTY_FUNCTION__, __LINE__);
            MSRV_CONTROL_PIP_ERR("MSrv_Control_common: SetInputSource, in PIP->PIP, Pre-PIP mode must equals to current PIP mode\n");
            m_enPipMode = m_enPrePipMode;
            return FALSE;
        }
#if (TRAVELING_ENABLE == 1)
        if(m_enPipMode == E_PIP_MODE_TRAVELING)
        {
            printf("PIP --> PIP Function = %s, Line = %d\n", __PRETTY_FUNCTION__, __LINE__);
            MSRV_CONTROL_PIP_ERR("MSrv_Control_common: SetInputSource, must follow PIP -> none PIP -> PIP\n");
            m_enPipMode = m_enPrePipMode;
            return FALSE;
        }
#endif
    }

    //none PIP --> PIP
    printf("none PIP --> PIP Function = %s, Line = %d\n", __PRETTY_FUNCTION__, __LINE__);
    EN_PIP_CONDIITON eRet = E_INPUT_SOURCE_SWITCH_DENY;
#if (TRAVELING_ENABLE == 1)
    if(m_enPipMode==E_PIP_MODE_TRAVELING)
    {
        MAPI_U16 enEngineType = E_TRAVELING_ENGINE_TYPE_SD;

        for (; enEngineType < E_TRAVELING_ENGINE_TYPE_MAX; enEngineType++)
        {
            if(  (m_stTravelModeInfo[enEngineType].enTravelingSource == E_TRAVELING_SUB_WINDOW)
                    &&(m_stTravelModeInfo[enEngineType].bCaptureOSD == MAPI_FALSE))
            {
                //Traveling with HD engine
                eRet = checkPipInputSourceSwitch(eMainInputSrc, eSubInputSrc, m_enPipMode, (EN_TRAVELING_ENGINE_TYPE)enEngineType);
            }
        }
    }
    else
#endif //(TRAVELING_ENABLE == 1)
    {
        //Normal PIP mode, not traveling
        eRet = checkPipInputSourceSwitch(eMainInputSrc, eSubInputSrc, m_enPipMode, E_TRAVELING_ENGINE_TYPE_MAX);
    }
    printf("none PIP --> PIP Function = %s, Line = %d, ret = %d\n", __PRETTY_FUNCTION__, __LINE__, (int)eRet);

    if(eRet == E_INPUT_SOURCE_SWITCH_NONE)
        return TRUE;

    if(eRet == E_INPUT_SOURCE_SWITCH_DENY)
    {
        MSRV_CONTROL_PIP_ERR("MSrv_Control_common: SetInputSource, This inputsource pair can't support\n");
        m_enPipMode = m_enPrePipMode;
        return FALSE;
    }
    ret = FALSE;

    if(eRet==E_INPUT_SOURCE_SWITCH_MAIN || eRet==E_INPUT_SOURCE_SWITCH_BOTH)
    {
        printf("none PIP --> PIP Switch Main Function Begin = %s, Line = %d\n", __PRETTY_FUNCTION__, __LINE__);
        ret = SetInputSource(eMainInputSrc, FALSE, FALSE, FALSE, MAPI_MAIN_WINDOW);
        if(ret == FALSE)
        {
            m_enPipMode = m_enPrePipMode;
            return FALSE;
        }
        printf("none PIP --> PIP Switch Main  Function End = %s, Line = %d\n", __PRETTY_FUNCTION__, __LINE__);
    }
    GetMSrvPlayer(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW))->OnNotifyPipEnable();
    if(eRet==E_INPUT_SOURCE_SWITCH_SUB || eRet==E_INPUT_SOURCE_SWITCH_BOTH)
    {
        printf("none PIP --> PIP Switch Sub  Function Begin = %s, Line = %d\n", __PRETTY_FUNCTION__, __LINE__);
        ret = SetInputSource(eSubInputSrc, FALSE, FALSE, FALSE, MAPI_SUB_WINDOW);
        if(ret == FALSE)
        {
            m_enPipMode = m_enPrePipMode;
            return FALSE;
        }
        printf("none PIP --> PIP Switch Sub Function End = %s, Line = %d\n", __PRETTY_FUNCTION__, __LINE__);
    }
    if(GetMSrvPlayer(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_SUB_WINDOW)) != NULL)
    {
        GetMSrvPlayer(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_SUB_WINDOW))->OnNotifyPipEnable();
    }
    return TRUE;
}

MSrv_Control_common::EN_PIP_CONDIITON MSrv_Control_common::checkPipInputSourceSwitch(MAPI_INPUT_SOURCE_TYPE eMainInputSrc, MAPI_INPUT_SOURCE_TYPE eSubInputSrc, EN_PIP_MODES ePipMode, EN_TRAVELING_ENGINE_TYPE enEngineType)
{
    if((eMainInputSrc == MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW)) &&
        (eSubInputSrc == MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_SUB_WINDOW)))
    {
        return E_INPUT_SOURCE_SWITCH_NONE;
    }

    if(ePipMode == E_PIP_MODE_PIP)
    {
        if(FALSE == SystemInfo::GetInstance()->GetPipPairInfo(eMainInputSrc, eSubInputSrc))
            return E_INPUT_SOURCE_SWITCH_DENY;
    }
    else if(ePipMode == E_PIP_MODE_POP)
    {
        if(FALSE == SystemInfo::GetInstance()->GetPopPairInfo(eMainInputSrc, eSubInputSrc))
            return E_INPUT_SOURCE_SWITCH_DENY;
    }
#if (TRAVELING_ENABLE == 1)
    else if(ePipMode == E_PIP_MODE_TRAVELING)
    {
        ASSERT(enEngineType < E_TRAVELING_ENGINE_TYPE_MAX);
        if( FALSE == SystemInfo::GetInstance()->GetTravelingPairInfo(eMainInputSrc, eSubInputSrc))
            return E_INPUT_SOURCE_SWITCH_DENY;
    }
#endif
    else
        return E_INPUT_SOURCE_SWITCH_DENY;

    if(eMainInputSrc == MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW))
    {
        return E_INPUT_SOURCE_SWITCH_SUB;
    }

    if(eSubInputSrc == MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_SUB_WINDOW))
    {
        return E_INPUT_SOURCE_SWITCH_MAIN;
    }

    return E_INPUT_SOURCE_SWITCH_BOTH;
}
#endif

#if (TRAVELING_ENABLE == 1)
BOOL MSrv_Control_common::CheckTravelingModeSupport(MAPI_INPUT_SOURCE_TYPE eMainInputSrc, MAPI_INPUT_SOURCE_TYPE eSubInputSrc, EN_TRAVELING_ENGINE_TYPE enEngineType)
{
#if (PIP_ENABLE == 1)
    if(  (eMainInputSrc<0 || eMainInputSrc>MAPI_INPUT_SOURCE_NUM)
            ||(eSubInputSrc<0  || eSubInputSrc>MAPI_INPUT_SOURCE_NUM)
            ||(enEngineType >= E_TRAVELING_ENGINE_TYPE_MAX))
    {
        MSRV_CONTROL_COMMON_ERR("MSrv_Control_common: CheckTravelingModeSupportCombination error: Engine[%u], eMainInputSrc[%d] , eSubInputSrc[%d] \n", enEngineType, (int)eMainInputSrc, (int)eSubInputSrc);
        return MAPI_FALSE;
    }

    return SystemInfo::GetInstance()->GetTravelingPairInfo(eMainInputSrc, eSubInputSrc);
#else
    return MAPI_FALSE;
#endif
}

void MSrv_Control_common::GetTravelingModeInfo(ST_TRAVELING_MODE_INFO *pstTravelModeInfo, EN_TRAVELING_ENGINE_TYPE enEngineType)
{
    ASSERT(enEngineType < E_TRAVELING_ENGINE_TYPE_MAX);
    memcpy(pstTravelModeInfo, &m_stTravelModeInfo[enEngineType], sizeof(ST_TRAVELING_MODE_INFO));
}

EN_TRAVELING_RETURN MSrv_Control_common::InitTravelingMode(ST_TRAVELING_MODE_INFO *pstTravelModeInfo, EN_TRAVELING_ENGINE_TYPE enEngineType, EN_TV_ROUTE_TYPE enTvRoutType)
{
    EN_TRAVELING_RETURN eRet = E_TRAVELING_UNSUPPORT;
    mapi_video *pMapiVideo = NULL;
#if (STB_ENABLE == 0)
#if (VE_ENABLE == 1 || CVBSOUT_ENABLE == 1)
    mapi_video_out *pMonitorVideoOut = NULL;
#endif
#endif
#if (PIP_ENABLE == 1)
    EN_PIP_MODES enMode = E_PIP_MODE_OFF;
    MAPI_BOOL bUseFullSubXC = MAPI_FALSE;
    enMode = GetPipMode();
#endif
    //mapi_scope_lock(scopeLock, &m_MutexVE); //Apply mutex to avoid competition between call back function and traveling ctrl functions

    if(enEngineType == E_TRAVELING_ENGINE_TYPE_SD)//not support SD engine
    {
        MSRV_CONTROL_TRAVELING_ERR("[Traveling %u]---%s[%d]:Traveling Error, No support of this traveling engine \n", enEngineType, __FUNCTION__, __LINE__);
        return E_TRAVELING_UNSUPPORT;
    }

    ASSERT(pstTravelModeInfo);

    if(enEngineType >= E_TRAVELING_ENGINE_TYPE_MAX)
    {
        MSRV_CONTROL_COMMON_ERR("[Traveling %u]---%s[%d]:Traveling Error, No support of this traveling engine \n", enEngineType, __FUNCTION__, __LINE__);
        return E_TRAVELING_UNSUPPORT;
    }

    if(TRUE == IsTravelingModeEnable(enEngineType))
    {
        MSRV_CONTROL_TRAVELING_ERR("[Traveling %u]---%s[%d]:Traveling Error, Traveling mode has already configed, if u want to reconfig, try finalize it first\n", enEngineType, __FUNCTION__, __LINE__);
        return E_TRAVELING_ALREADY_OPENED;
    }

    if (MAPI_FALSE == IsTravelingMode(pstTravelModeInfo->enTravelingSource, pstTravelModeInfo->bCaptureOSD))
    {
        MSRV_CONTROL_TRAVELING_ERR("[Traveling %u]---%s[%d]:Traveling Error, Unknown traveling [%u](enTravelingSource), [%d](bOSD)\n",
                                enEngineType, __FUNCTION__, __LINE__, pstTravelModeInfo->enTravelingSource, pstTravelModeInfo->bCaptureOSD);
        return E_TRAVELING_UNSUPPORT;
    }

    if(pstTravelModeInfo->enTravelingSource == E_TRAVELING_MAIN_WINDOW && pstTravelModeInfo->bCaptureOSD == MAPI_FALSE)
    {
        if(enEngineType != E_TRAVELING_ENGINE_TYPE_SD)
        {
#if (STEREO_3D_ENABLE == 1)
            EN_3D_TYPE e3DType = GetMSrv3DManager()->GetCurrent3DFormat();
            if((e3DType != EN_3D_NONE) && ((e3DType != EN_3D_DUALVIEW)))
            {
                MSRV_CONTROL_TRAVELING_ERR("[Traveling %u]---%s[%d]:Traveling Error, 3D enabled\n", enEngineType, __FUNCTION__, __LINE__);
                return E_TRAVELING_UNSUPPORT;
            }
#endif
        }
        else
        {
            MSRV_CONTROL_TRAVELING_ERR("[Traveling %u]---%s[%d]:Traveling Error, unsupported combination [%u](enTravelingSource), [%d](bOSD)\n",
                                     enEngineType, __FUNCTION__, __LINE__, pstTravelModeInfo->enTravelingSource, pstTravelModeInfo->bCaptureOSD);
            return E_TRAVELING_INPUT_PARAMETER_ERROR;
        }
    }
#if (PIP_ENABLE == 1)
    else if(pstTravelModeInfo->enTravelingSource == E_TRAVELING_SUB_WINDOW && pstTravelModeInfo->bCaptureOSD == MAPI_FALSE)
    {
        if((m_enPipMode == E_PIP_MODE_PIP) || (m_enPipMode == E_PIP_MODE_POP))
        {
            if (enEngineType == E_TRAVELING_ENGINE_TYPE_SD)
            {
                MSRV_CONTROL_TRAVELING_ERR("[Traveling %u]---%s[%d]:Traveling Error, PIP Mode[%u] enabled\n", enEngineType, __FUNCTION__, __LINE__, enMode);
                return E_TRAVELING_INPUT_PARAMETER_ERROR;
            }

            bUseFullSubXC = MAPI_FALSE;
        }
        else
        {
            MAPI_BOOL bEnable_4k2k_FRC = MAPI_FALSE;
            SystemInfo::GetInstance()->GetModuleParameter_bool("M_BACKEND:F_BACKEND_ENABLE_4K2K_FRC", &bEnable_4k2k_FRC);
            if(bEnable_4k2k_FRC == MAPI_TRUE)
            {
                // 4K2K Platform OP timing is 2K2K@60HZ or 4K2K@30HZ using FCLK 240MHZ,MAX FCLK is 300MHZ,
                // when sub window out OP timing,there is no more FCLK for sub window,
                // so this case sub traveling capture IP2
                pstTravelModeInfo->enCaptureStage = E_TRAVELING_CAP_STAGE_SIGNAL_INPUT;
            }
            bUseFullSubXC = MAPI_TRUE;
        }

#if (STEREO_3D_ENABLE == 1)
        EN_3D_TYPE e3DType = GetMSrv3DManager()->GetCurrent3DFormat();
        if((e3DType != EN_3D_NONE) && ((e3DType != EN_3D_DUALVIEW)))
        {
            MSRV_CONTROL_TRAVELING_ERR("[Traveling %u]---%s[%d]:Traveling Error, 3D enabled\n", enEngineType, __FUNCTION__, __LINE__);
            return E_TRAVELING_UNSUPPORT;
        }
#endif
        if (CheckTravelingModeSupport(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW), pstTravelModeInfo->enMapiSubsource, enEngineType) == FALSE)
        {
            MSRV_CONTROL_TRAVELING_ERR("[Traveling %u]---%s[%d]:Traveling Error, CheckTravelingModeSupport error, Main/Sub InputSrc(%d/%d)\n", enEngineType, __FUNCTION__, __LINE__, (int)(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW)), (int)pstTravelModeInfo->enMapiSubsource);
            return E_TRAVELING_UNSUPPORT;
        }
    }
#endif

#if (STB_ENABLE == 0)
#if (VE_ENABLE == 1 ||CVBSOUT_ENABLE==1)
    mapi_video_out *pTVVideoOut = mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_TV_MODE);
    if(enEngineType == E_TRAVELING_ENGINE_TYPE_SD)
    {
        //Traveling with VE, finalize av out
        //Finalize monitor mode
        if((pMonitorVideoOut != NULL) && (pMonitorVideoOut->IsDestTypeExistent(MAPI_MAIN_WINDOW)) && (pMonitorVideoOut->IsActive()))
        {
            MSrv_Player* pPlayer = GetMSrvPlayer(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW));
            if(pPlayer != NULL)
            {
                pPlayer->FinalizeVideoOut();//Use player finalize to finalize audio too.
            }
            else
            {
                pMonitorVideoOut->SetVideoMute(FALSE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, MAPI_MAIN_WINDOW);
                pMonitorVideoOut->Finalize(MAPI_MAIN_WINDOW);
            }
        }
    }

    if((pstTravelModeInfo->enTravelingSource == E_TRAVELING_SUB_WINDOW) || (enEngineType == E_TRAVELING_ENGINE_TYPE_SD))
    {
        //PIP mode traveling or ve traveling, can not open tv mode av output
        if((pTVVideoOut != NULL) && (pTVVideoOut->IsDestTypeExistent(MAPI_MAIN_WINDOW)) && (pTVVideoOut->IsActive()))
        {
            pTVVideoOut->SetVideoMute(FALSE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, MAPI_MAIN_WINDOW);
            pTVVideoOut->Finalize(MAPI_MAIN_WINDOW);
        }
    }
#endif
#endif

    if(  (pstTravelModeInfo->enTravelingSource == E_TRAVELING_ALL_WINDOW)
       ||(pstTravelModeInfo->enTravelingSource == E_TRAVELING_MAIN_WINDOW && pstTravelModeInfo->bCaptureOSD == MAPI_FALSE))
    {
        memcpy(&m_stTravelModeInfo[enEngineType], pstTravelModeInfo, sizeof(ST_TRAVELING_MODE_INFO));
        pMapiVideo = mapi_interface::Get_mapi_video(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW));
        if(pMapiVideo == NULL)
        {
            memset(&m_stTravelModeInfo[enEngineType], 0, sizeof(ST_TRAVELING_MODE_INFO));
            MSRV_CONTROL_TRAVELING_ERR("[Traveling %u]---%s[%d]:Traveling Error, Can not get SDK handler \n", enEngineType, __FUNCTION__, __LINE__);
            return E_TRAVELING_UNKNOWN_ERROR;
        }
        eRet = pMapiVideo->InitTravelingMode(pstTravelModeInfo, enEngineType);
    }
#if (PIP_ENABLE == 1)
    else if(pstTravelModeInfo->enTravelingSource == E_TRAVELING_SUB_WINDOW && pstTravelModeInfo->bCaptureOSD == MAPI_FALSE)
    {
        pMapiVideo = mapi_interface::Get_mapi_video(pstTravelModeInfo->enMapiSubsource);
        if(pMapiVideo == NULL)
        {
            memset(&m_stTravelModeInfo[enEngineType], 0, sizeof(ST_TRAVELING_MODE_INFO));
            MSRV_CONTROL_TRAVELING_ERR("[Traveling %u]---%s[%d]:Traveling Error, Can not get SDK handler \n", enEngineType, __FUNCTION__, __LINE__);
            return E_TRAVELING_UNKNOWN_ERROR;
        }

        if(bUseFullSubXC)
        {
            GetMSrvPicture()->SetDMSV12L(FALSE);
        }
        eRet = pMapiVideo->InitTravelingMode(pstTravelModeInfo, enEngineType);

        memcpy(&m_stTravelModeInfo[enEngineType], pstTravelModeInfo, sizeof(ST_TRAVELING_MODE_INFO));
        if((m_enPipMode == E_PIP_MODE_PIP) || (m_enPipMode == E_PIP_MODE_POP))
        {
            if(SetInputSource(pstTravelModeInfo->enMapiSubsource, FALSE, FALSE, FALSE, MAPI_SUB_WINDOW) == FALSE)
            {
                MSRV_CONTROL_TRAVELING_ERR("[Traveling %u]---%s[%d]:Traveling Error for sub source(%d)\n", enEngineType, __FUNCTION__, __LINE__, pstTravelModeInfo->enMapiSubsource);
                memset(&m_stTravelModeInfo[enEngineType], 0, sizeof(ST_TRAVELING_MODE_INFO));
                return E_TRAVELING_UNSUPPORT;
            }
        }
        else
        {
            if(enMode == E_PIP_MODE_OFF)
            {
                setPipMode(E_PIP_MODE_TRAVELING); // 2nd video traveling is a pipmode
                mapi_video::SetPipMainSubInputSourceType(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW), pstTravelModeInfo->enMapiSubsource);
                if ((pstTravelModeInfo->enMapiSubsource == MAPI_INPUT_SOURCE_DTV) ||(pstTravelModeInfo->enMapiSubsource == MAPI_INPUT_SOURCE_DTV2))
                {
                    if (enTvRoutType != E_ROUTE_NONE)
                    {
                        SwitchMSrvDtvRoute(MSrv_Control::GetInstance()->GetRouteIndexByTVMode(enTvRoutType), MAPI_SUB_WINDOW);
                    }
                }
            }

            if(setPipInputSource((MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW)), pstTravelModeInfo->enMapiSubsource) == FALSE)
            {
                //Switch source error, return false
                setPipMode(E_PIP_MODE_OFF);
                memset(&m_stTravelModeInfo[enEngineType], 0, sizeof(ST_TRAVELING_MODE_INFO));
                MSRV_CONTROL_TRAVELING_ERR("[Traveling %u]---%s[%d]:Traveling Error, SetPipInputSource error, Main/Sub InputSrc(%d/%d)\n", enEngineType, __FUNCTION__, __LINE__, (int)(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW)), (int)pstTravelModeInfo->enMapiSubsource);
                return E_TRAVELING_UNSUPPORT;
            }
        }
    }
#endif
    else if(pstTravelModeInfo->enTravelingSource == E_TRAVELING_MEMORY)
    {
        EN_TRAVELING_ENGINE_TYPE enTempEngineType = E_TRAVELING_ENGINE_TYPE_SD;
        while (enTempEngineType < E_TRAVELING_ENGINE_TYPE_MAX)
        {
            if(m_stTravelModeInfo[enEngineType].enTravelingSource == E_TRAVELING_MEMORY)
            {
                MSRV_CONTROL_TRAVELING_ERR("[Traveling %u]---%s[%d]:Traveling Memory already open at [%d],we can not use another dipr\n", enEngineType, __FUNCTION__, __LINE__,enTempEngineType);
                return E_TRAVELING_ALREADY_OPENED;
            }
            enTempEngineType =  (EN_TRAVELING_ENGINE_TYPE)((MS_U16)enTempEngineType + 1);
        }

        memcpy(&m_stTravelModeInfo[enEngineType], pstTravelModeInfo, sizeof(ST_TRAVELING_MODE_INFO));
        pMapiVideo = mapi_interface::Get_mapi_video(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW));
        if(pMapiVideo == NULL)
        {
            memset(&m_stTravelModeInfo[enEngineType], 0, sizeof(ST_TRAVELING_MODE_INFO));
            MSRV_CONTROL_TRAVELING_ERR("[Traveling %u]---%s[%d]:Traveling Error, Can not get SDK handler \n", enEngineType, __FUNCTION__, __LINE__);
            return E_TRAVELING_UNKNOWN_ERROR;
        }
        eRet = pMapiVideo->InitTravelingMode(pstTravelModeInfo, enEngineType);
    }
    else
    {
        MSRV_CONTROL_TRAVELING_ERR("[Traveling %u]---%s[%d]:Traveling Error, Wrong traveling combination [%u](enTravelingSource), [%d](bOSD)\n",
                                 enEngineType, __FUNCTION__, __LINE__, pstTravelModeInfo->enTravelingSource, pstTravelModeInfo->bCaptureOSD);
        return E_TRAVELING_UNSUPPORT;
    }

    m_pMSrvTravelDataCallback[enEngineType] = NULL;
    m_pMSrvTravelEventCallback[enEngineType] = NULL;
    if (eRet != E_TRAVELING_SUCCESS)
    {
        memset(&m_stTravelModeInfo[enEngineType], 0, sizeof(ST_TRAVELING_MODE_INFO));
    }
    MSRV_CONTROL_TRAVELING_CHECK("[Traveling %u]---%s:%d bUseFullSubXC=%u, eRet=%d\n", enEngineType, __FUNCTION__, __LINE__, bUseFullSubXC, eRet);
    return eRet;
}

void MSrv_Control_common::MSrvTravelingDataCallback(void *pstTravelDataInfo, int enEngineType)
{
    //printf("MSrv Enter Data Call back...  buf index=%u\n",((ST_TRAVELING_CALLBACK_DATA_INFO*)pstTravelDataInfo)->u16BuffIndex);
    if(m_pMSrvTravelDataCallback[enEngineType] != NULL)
    {
        m_pMSrvTravelDataCallback[enEngineType]((void *)pstTravelDataInfo, enEngineType);
    }
    else
    {
        printf("[Traveling %u] MSRV CallBackPointer == NULL\n", enEngineType);
    }
}

void MSrv_Control_common::MSrvTravelingEventCallback(void *pstTraveEventlInfo, int enEngineType)
{
    //printf("MSrv Enter Event Call back...\n");
    if(m_pMSrvTravelEventCallback[enEngineType] != NULL)
    {
        m_pMSrvTravelEventCallback[enEngineType]((void *)pstTraveEventlInfo, enEngineType);
    }
}

//-------------------------------------------------------------------------------------------------
/// For PIP manager or internal use: Stop Traveling mode Capture for ever
/// if you want to restart it, you need call 'StartTravelingMode'
/// @return                 \b TRUE: success, or FALSE: failure.
//-------------------------------------------------------------------------------------------------
EN_TRAVELING_RETURN MSrv_Control_common::StopTravelingMode(EN_TRAVELING_ENGINE_TYPE enEngineType)
{
    EN_TRAVELING_RETURN eRet = E_TRAVELING_UNSUPPORT;
    if(enEngineType >= E_TRAVELING_ENGINE_TYPE_MAX)
    {
        MSRV_CONTROL_TRAVELING_ERR("[Traveling %u]---%s[%d]:Traveling Error, Wrong engine type\n", enEngineType, __FUNCTION__, __LINE__);
        return E_TRAVELING_INPUT_PARAMETER_ERROR; //Invalid mode
    }
    if(IsTravelingModeEnable(enEngineType) == MAPI_TRUE)
    {
        if (m_stTravelModeInfo[enEngineType].enTravelingSource == E_TRAVELING_ALL_WINDOW)
        {
            MAPI_BOOL bEnable_4k2k = MAPI_FALSE;
            SystemInfo::GetInstance()->GetModuleParameter_bool("M_BACKEND:F_BACKEND_ENABLE_4K2K", &bEnable_4k2k);
            if( bEnable_4k2k == MAPI_TRUE )
            {
#if (ENABLE_4K2K_OP_DEFAULT == 0)
            mapi_display* pDisplay = mapi_interface::Get_mapi_display();
            if(pDisplay != NULL)
            {
                    if(pDisplay->SetGOPDstOpOrFrc(FALSE) == FALSE)
                {
                        printf("SetGOPDstOpOrFrc-->FRC fail!\n");
                }
            }
#endif
            }
        }

        if(  (m_stTravelModeInfo[enEngineType].enTravelingSource == E_TRAVELING_SUB_WINDOW)
           &&(m_stTravelModeInfo[enEngineType].bCaptureOSD == MAPI_FALSE))
        {
            eRet = mapi_interface::Get_mapi_video(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_SUB_WINDOW))->StopTravelingMode(enEngineType);
        }
        else
        {
            eRet = mapi_interface::Get_mapi_video(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW))->StopTravelingMode(enEngineType);
        }
        m_pMSrvTravelDataCallback[enEngineType] = NULL;
    }
    else
    {
        eRet = E_TRAVELING_SUCCESS;
        MSRV_CONTROL_TRAVELING_CHECK("[Traveling %u]---%s[%d]:Try stop traveling, but raveling mode not enable\n", enEngineType, __FUNCTION__, __LINE__);
    }
    MSRV_CONTROL_TRAVELING_CHECK("[Traveling %u]---%s:%d bRet=%u\n", enEngineType, __FUNCTION__, __LINE__, eRet);
    return eRet;
}

//-------------------------------------------------------------------------------------------------
/// For PIP manager: start to capture, when one frame is done, call back function( TRAVELMODECALLBACK ) will be called
/// @param pstTravelInfo  \b OUT: The information structure of current finished capture frame
/// @return                 \b MAPI_BOOL
//-------------------------------------------------------------------------------------------------
EN_TRAVELING_RETURN MSrv_Control_common::StartTravelingMode(TRAVELMODEDATACALLBACK pTravelDataCallback, TRAVELMODEEVENTCALLBACK pTravelEventCallback, EN_TRAVELING_ENGINE_TYPE enEngineType)
{
    EN_TRAVELING_RETURN eRet = E_TRAVELING_UNSUPPORT;

    if(enEngineType >= E_TRAVELING_ENGINE_TYPE_MAX)
    {
        MSRV_CONTROL_TRAVELING_ERR("[Traveling %u]---%s[%d]:Traveling Error, Wrong engine type\n", enEngineType, __FUNCTION__, __LINE__);
        return E_TRAVELING_INPUT_PARAMETER_ERROR; //Invalid mode
    }

    if(pTravelDataCallback == NULL || pTravelEventCallback == NULL)
    {
        MSRV_CONTROL_TRAVELING_ERR("[Traveling %u]---%s[%d]:Traveling Error, CallBack Pointer is NULL\n", enEngineType, __FUNCTION__, __LINE__);
        return E_TRAVELING_INPUT_PARAMETER_ERROR; //Invalid mode
    }

    if(enEngineType >= E_TRAVELING_ENGINE_TYPE_MAX)
    {
        MSRV_CONTROL_TRAVELING_ERR("[Traveling %u]---%s[%d]:Traveling Error, Wrong engine type\n", enEngineType, __FUNCTION__, __LINE__);
        return E_TRAVELING_INPUT_PARAMETER_ERROR; //Invalid mode
    }

    if(IsTravelingModeEnable(enEngineType) == FALSE)
    {
        MSRV_CONTROL_TRAVELING_ERR("[Traveling %u]---%s[%d]:Traveling Error, Invalid traveling mode\n", enEngineType, __FUNCTION__, __LINE__);
        return E_TRAVELING_INPUT_PARAMETER_ERROR; //Invalid mode
    }

    if (m_stTravelModeInfo[enEngineType].enTravelingSource == E_TRAVELING_ALL_WINDOW)
    {
        MAPI_BOOL bEnable_4k2k = MAPI_FALSE;
        SystemInfo::GetInstance()->GetModuleParameter_bool("M_BACKEND:F_BACKEND_ENABLE_4K2K", &bEnable_4k2k);
        if( bEnable_4k2k == MAPI_TRUE )
        {
#if (ENABLE_4K2K_OP_DEFAULT == 0)
        mapi_display* pDisplay = mapi_interface::Get_mapi_display();
        if(pDisplay != NULL)
        {
                if(pDisplay->SetGOPDstOpOrFrc(TRUE) == FALSE)
            {
                    printf("SetGOPDstOpOrFrc-->OP fail!\n");
            }
        }
#endif
        }
    }

    mapi_video *pMApi = NULL;
    if(  (m_stTravelModeInfo[enEngineType].enTravelingSource == E_TRAVELING_SUB_WINDOW)
       &&(m_stTravelModeInfo[enEngineType].bCaptureOSD == MAPI_FALSE))
    {
        pMApi = mapi_interface::Get_mapi_video(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_SUB_WINDOW));
    }
    else
    {
        pMApi = mapi_interface::Get_mapi_video(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW));
    }

    eRet = pMApi->StartTravelingMode(MSrvTravelingDataCallback, MSrvTravelingEventCallback, enEngineType);

    if(eRet == E_TRAVELING_SUCCESS)
    {
        //For traveling all video mode: start traveling and pass call back function directly, because their is no player and signal stable check flow.
        //For traveling 2nd video mode: pass call back function directly, let players to decide when to really start/stop traveling
        m_pMSrvTravelDataCallback[enEngineType] = pTravelDataCallback;
        m_pMSrvTravelEventCallback[enEngineType] = pTravelEventCallback;
    }
    MSRV_CONTROL_TRAVELING_CHECK("[Traveling %u]---%s:%d bRet=%d\n", enEngineType, __FUNCTION__, __LINE__, eRet);
    return eRet;
}

EN_TRAVELING_RETURN MSrv_Control_common::FrameProcessDone(MAPI_U32 u32Index, EN_TRAVELING_ENGINE_TYPE enEngineType)
{
    EN_TRAVELING_RETURN eRet = E_TRAVELING_UNSUPPORT;
    u32Index = u32Index;
    if(enEngineType >= E_TRAVELING_ENGINE_TYPE_MAX)
    {
        MSRV_CONTROL_TRAVELING_ERR("[Traveling %u]---%s[%d]:Traveling Error, Wrong engine type\n", enEngineType, __FUNCTION__, __LINE__);
        return E_TRAVELING_INPUT_PARAMETER_ERROR; //Invalid mode
    }
    if(TRUE == IsTravelingModeEnable(enEngineType))
    {
        if(  (m_stTravelModeInfo[enEngineType].enTravelingSource == E_TRAVELING_SUB_WINDOW)
           &&(m_stTravelModeInfo[enEngineType].bCaptureOSD == MAPI_FALSE))
        {
            eRet = mapi_interface::Get_mapi_video(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_SUB_WINDOW))->FrameProcessDone(u32Index, enEngineType);
        }
        else
        {
            eRet = mapi_interface::Get_mapi_video(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW))->FrameProcessDone(u32Index, enEngineType);
        }
    }
    //MSRV_CONTROL_TRAVELING_CHECK("[Traveling %u][Index %u]---%s:%d bRet=%u\n", enEngineType, u32Index, __FUNCTION__, __LINE__, eRet);
    return eRet;
}

EN_TRAVELING_RETURN MSrv_Control_common::FinalizeTravelingMode(EN_TRAVELING_ENGINE_TYPE enEngineType)
{
    EN_TRAVELING_RETURN eRet = E_TRAVELING_SUCCESS;
    if(enEngineType >= E_TRAVELING_ENGINE_TYPE_MAX)
    {
        MSRV_CONTROL_TRAVELING_ERR("[Traveling %u]---%s[%d]:Traveling Error, Wrong engine type\n", enEngineType, __FUNCTION__, __LINE__);
        return E_TRAVELING_INPUT_PARAMETER_ERROR; //Invalid mode
    }

    if(FALSE == IsTravelingModeEnable(enEngineType))
    {
        return E_TRAVELING_INPUT_PARAMETER_ERROR;
    }
    StopTravelingMode(enEngineType);

    if(  (m_stTravelModeInfo[enEngineType].enTravelingSource == E_TRAVELING_SUB_WINDOW)
       &&(m_stTravelModeInfo[enEngineType].bCaptureOSD == MAPI_FALSE))
    {
        eRet = mapi_interface::Get_mapi_video(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_SUB_WINDOW))->FinalizeTravelingMode(enEngineType);
    }
    else
    {
        eRet = mapi_interface::Get_mapi_video(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW))->FinalizeTravelingMode(enEngineType);
    }

#if (PIP_ENABLE == 1)
    if(  (m_stTravelModeInfo[enEngineType].enTravelingSource == E_TRAVELING_SUB_WINDOW)
       &&(m_stTravelModeInfo[enEngineType].bCaptureOSD == MAPI_FALSE))
    {
        if(m_enPipMode == E_PIP_MODE_TRAVELING)//Clear for absolute 2nd traveling mode
        {
            mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_USER_HP_MUTEON_ , E_AUDIOMUTESOURCE_ACTIVESOURCE_);
            MAPI_BOOL bRet = MAPI_TRUE;
            m_bPipFinished = MAPI_FALSE;
            setPipMode(E_PIP_MODE_OFF);
            m_enCurrentFocusSource = (MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW));

            GetMSrvVideo()->SelectWindow(MAPI_MAIN_WINDOW);
            bRet = setPipInputSource(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW), MAPI_INPUT_SOURCE_NONE);
            if(bRet == FALSE)
            {
                return E_TRAVELING_INPUT_PARAMETER_ERROR;
            }

            //from pip,pop->none, retore dms
            GetMSrvPicture()->SetDMSV12L(GetMSrvPicture()->GetDMSV12LFromXRuleTable());
            mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_USER_HP_MUTEOFF_ , E_AUDIOMUTESOURCE_ACTIVESOURCE_);
        }
    }

    m_bPipFinished = MAPI_TRUE;
#endif

#if (VE_ENABLE == 1 ||CVBSOUT_ENABLE==1)
    mapi_video_out *pVideoOut = mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_MONITOR_MODE);
    if(enEngineType == E_TRAVELING_ENGINE_TYPE_SD)
    {
        //Restore Monitor+TV mode av out, when quit ve traveling
        if(pVideoOut->IsDestTypeExistent(MAPI_MAIN_WINDOW))
        {
            MAPI_INPUT_SOURCE_TYPE enInputSrc = MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW);
            MSrv_Player* pPlayer = GetMSrvPlayer(enInputSrc);
            if(pPlayer != NULL)
            {
                pPlayer->InitVideoOut(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW), MAPI_MAIN_WINDOW);
                if(pPlayer->IsSignalStable())
                {
                    if(IsSrcDTV(enInputSrc))
                    {
                        //For DTV, reload VE setting
                        pVideoOut->SetMode(mapi_video_out_datatype::MAPI_VIDEO_OUT_VE_AUTO, MAPI_MAIN_WINDOW);
                        pVideoOut->SetVideoMute(MAPI_FALSE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, MAPI_MAIN_WINDOW);
                    }
                    else if(IsSrcAV(enInputSrc) ||
                            IsSrcATV(enInputSrc) )
                    {
                        pVideoOut->SetVideoMute(MAPI_FALSE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, MAPI_MAIN_WINDOW);
                    }
                }
            }
        }
    }

    if((enEngineType == E_TRAVELING_ENGINE_TYPE_SD) || (m_stTravelModeInfo[enEngineType].enTravelingSource == E_TRAVELING_SUB_WINDOW))
    {
        //Restore TV mode av out
        if(m_enPipMode == E_PIP_MODE_OFF)
        {
            //No pip mode, then can enable tv mode av out
            pVideoOut = mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_TV_MODE);
            if((pVideoOut != NULL) && (pVideoOut->IsDestTypeExistent(MAPI_MAIN_WINDOW)))
            {
                // For TV mode to always output ATV.
                pVideoOut->Initialize(MAPI_INPUT_SOURCE_ATV, MAPI_MAIN_WINDOW);
                pVideoOut->SetVideoMute(FALSE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN, MAPI_MAIN_WINDOW);
            }
        }
    }
#endif

#if (CEC_ENABLE == 1)
    CECStart();
#endif

    memset(&m_stTravelModeInfo[enEngineType], 0, sizeof(ST_TRAVELING_MODE_INFO));
    m_pMSrvTravelDataCallback[enEngineType] = NULL;
    m_pMSrvTravelEventCallback[enEngineType] = NULL;
    MSRV_CONTROL_TRAVELING_CHECK("[Traveling %u]---%s:%d bRet=%u\n", enEngineType, __FUNCTION__, __LINE__, eRet);
    return eRet;
}

EN_TRAVELING_RETURN MSrv_Control_common::SetMemoryTravelingConfig(ST_MEMORY_TRAVELING_INFO *pstMemInfo,EN_TRAVELING_ENGINE_TYPE enEngineType)
{
    EN_TRAVELING_RETURN eRet = E_TRAVELING_UNSUPPORT;
    ASSERT(pstMemInfo);

    if (enEngineType >= E_TRAVELING_ENGINE_TYPE_MAX)
    {
        MSRV_CONTROL_TRAVELING_ERR("[Traveling %u]---%s[%d]:Traveling Error, Wrong engine type\n", enEngineType, __FUNCTION__, __LINE__);
        return E_TRAVELING_INPUT_PARAMETER_ERROR; //Invalid mode
    }

    if ((m_stTravelModeInfo == NULL) || (m_stTravelModeInfo[enEngineType].enTravelingSource != E_TRAVELING_MEMORY))
    {
        MSRV_CONTROL_TRAVELING_ERR("[Traveling %u]---%s[%d]:Traveling Error, Invalid traveling mode\n", enEngineType, __FUNCTION__, __LINE__);
        return E_TRAVELING_INPUT_PARAMETER_ERROR; //Invalid mode
    }

    mapi_video *pMApi = mapi_interface::Get_mapi_video(MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW));
    eRet = pMApi->SetMemoryTravelingConfig(pstMemInfo, enEngineType);
    return eRet;
}
#endif //#if (TRAVELING_ENABLE == 1)

#if (ENABLE_LITE_SN != 1)
BOOL MSrv_Control_common::GetBinNameByFilterStr(char *pPath,char *pFilterStr,char* pOutFileName,unsigned int iOutFnLen)
{

    struct   dirent   *dt;
    struct   stat   s;
    DIR *dir = NULL;
    char temp[128]={0};
    char *ptr = NULL;
    int iRetChk = -1;
    BOOL bRet = FALSE;

    if((pPath == NULL) || (pFilterStr == NULL) || (pOutFileName == NULL))
    {
        printf("error, NULL Pointer\n");
        return FALSE;
    }

    dir =  opendir(pPath);
    if(dir == NULL)
    {
       printf("opendir   %s   fail\n",pPath);
       return FALSE;
    }

    while(1)
    {
            dt  = readdir(dir);
            if(dt == NULL)
            {
                    break;
            }

        if(dt->d_name[0] == '.')//skip '.' or '..' file
        {
           continue;
        }

        memset(temp,0,sizeof(temp));
        snprintf(temp, 50, "%s/%s",pPath,dt->d_name);
        iRetChk = stat(temp,&s);
        if(iRetChk < 0)
        {
            printf("stat error,%d\n",iRetChk);
            continue;
        }

        if(S_ISDIR(s.st_mode))
        {
            continue;
        }

        if(strstr(dt->d_name,pFilterStr)!=NULL)
        {
            ptr = strrchr(dt->d_name,'.');
            if(ptr!=NULL)
            {
                if(strncmp(ptr,".bin",sizeof(".bin")) == 0)
                {
                    printf("find file:%s\n",dt->d_name);
                    if(strlen(dt->d_name) < iOutFnLen)
                    {
                        strncpy(pOutFileName,dt->d_name,strlen(dt->d_name));
                        pOutFileName[strlen(dt->d_name)] = '\0';
                        bRet = TRUE;
                        break;
                    }
                    else
                    {
                        printf("\t File Name buffer too small,can't get upgrade bin Name\n");
                    }

                }
            }
        }

    }
    closedir(dir);
    return bRet;
}



int MSrv_Control_common::SearchFileInUsbDevByFilter(char* pFilterStr,char* pOutFilePath,unsigned int iOutFilePathLen)
{
    #define BUF_SIZE 256
    #define STR_CNT  6
    #define STR_LEN  128
    FILE *fp = NULL;
    int iTotalFileCnt = 0;
    S8 buf[BUF_SIZE], str_mount[STR_CNT][STR_LEN]={{0}};

    //check /proc/mounts
    fp = fopen("/proc/mounts", "r");
    if(fp != NULL)
    {
        memset(buf, 0, sizeof(buf));
        while(NULL != fgets((char*)buf, sizeof(buf), fp))
        {
            buf[BUF_SIZE - 1] = '\0';
            if((NULL != strstr((char*)buf, (char*)"/dev/block/"))
                || (NULL != strstr((char*)buf, (char*)"/dev/sd")))
            {
                memset(str_mount, 0, sizeof(str_mount));
                sscanf((char*)buf, "%s %s %s %s %s %s", (char*)str_mount[0], (char*)str_mount[1], (char*)str_mount[2], (char*)str_mount[3], (char*)str_mount[4], (char*)str_mount[5]);
                if(str_mount[1][0] != 0) //not mounted
                {
                    char FileName[STR_LEN] = {0};
                    BOOL bRet = GetBinNameByFilterStr((char *)(str_mount[1]),pFilterStr,FileName,sizeof(FileName));
                    if(bRet == TRUE)
                    {
                        char FilePath[2*STR_LEN+1] = {0};
                        strncpy(FilePath,(char *)(str_mount[1]),strlen((char *)(str_mount[1])));
                        strncat(FilePath,(const char *)"/",1);
                        strncat(FilePath,(const char *)FileName,strlen(FileName));
                        if(strlen(FilePath) < iOutFilePathLen)
                        {
                            strncpy(pOutFilePath,FilePath,strlen(FilePath));
                        }
                        else
                        {
                            printf("\t Error,path too long Large than UpgradeBinPath Buffer\n");
                        }
                        printf("\t Find Upgrade Bin Path:[%s]\n",FilePath);
                        iTotalFileCnt++;
                    }
                }

                for(int i=0;i<STR_CNT;i++)
                {
                    memset(str_mount[i],0,STR_LEN);
                }

            }
            memset(buf, 0, sizeof(buf));
        }
        fclose(fp);
    }

    return iTotalFileCnt;
}
#endif

BOOL MSrv_Control_common::SetGpioDeviceStatus(const U32 u32PinID, const BOOL bEnable)
{
    BOOL bRet = FALSE;
    mapi_gpio *gptr = mapi_gpio::GetGPIO_Dev(u32PinID);

    if (gptr != NULL)
    {
        if(bEnable)
        {
            gptr->SetOn();
        }
        else
        {
            gptr->SetOff();
        }

        bRet = TRUE;
    }

    return bRet;
}

U32 MSrv_Control_common::GetGpioDeviceStatus(const U32 u32PinID)
{
    mapi_gpio *gptr = mapi_gpio::GetGPIO_Dev(u32PinID);

    if (gptr != NULL)
    {
        return gptr->GetLevel();
    }
    return 0xFFFF;
}

#define MMC_BANK_SIZE 0x10000
BOOL MSrv_Control_common::ReadFromFlashByBank(const U8 u8Bank,  U8 * const pu8Buffer, const U32 u32Size)
{
    Imapi_storage_factory_config *pFlash = NULL;
    EN_BOOT_STORAGE envType = Imapi_storage_factory_config::GetEnvType();
    MAPI_BOOL bRet = MAPI_FALSE;
    //printf("envType = %d\n",envType);
    pFlash = Imapi_storage_factory_config::GetInstance(envType);
    if (NULL==pFlash)
    {
        MSRV_CONTROL_COMMON_ERR("GetFlash Fail\n");
        return FALSE;
    }
    else
    {
        if(E_MMC_CONFIG_STORAGE == envType)
        {
            bRet = pFlash->Read((MMC_BANK_SIZE*u8Bank), u32Size, pu8Buffer);
        }
        else
        {
            bRet = pFlash->Read((U16)u8Bank,(U16)0,(U32)pu8Buffer,u32Size);
        }
        if(bRet == MAPI_FALSE)
        {
            MSRV_CONTROL_COMMON_ERR("ReadFlash Fail\n");
            return FALSE;
        }
        MSRV_CONTROL_COMMON_ERR("ReadFlash Success\n");
        return TRUE;
    }
}

BOOL MSrv_Control_common::ReadFromFlashByAddr(const U32 u32FlashAddr, const U32 u32ReadSize, U8* const pu8Readbuffer)
{
    Imapi_storage_factory_config *pFlash = NULL;
    EN_BOOT_STORAGE envType = Imapi_storage_factory_config::GetEnvType();
    //printf("envType = %d\n",envType);
    pFlash = Imapi_storage_factory_config::GetInstance(envType);
    if(NULL==pFlash)
    {
        MSRV_CONTROL_COMMON_ERR("GetFlash Fail\n");
        return FALSE;
    }
    else
    {
        if(pFlash->Read(u32FlashAddr, u32ReadSize, pu8Readbuffer) == FALSE)
        {
            MSRV_CONTROL_COMMON_ERR("ReadFlash Fail\n");
            return FALSE;
        }
        MSRV_CONTROL_COMMON_ERR("ReadFlash Success\n");
        return TRUE;
    }
}

BOOL MSrv_Control_common::WriteToFlashByBank(const U8 u8Bank, const U8 * const pu8Buffer, const U32 u32Size)
{
    Imapi_storage_factory_config *pFlash = NULL;
    EN_BOOT_STORAGE envType = Imapi_storage_factory_config::GetEnvType();
    MAPI_BOOL bRet = MAPI_FALSE;
    //printf("envType = %d\n",envType);
    pFlash = Imapi_storage_factory_config::GetInstance(envType);
    if(NULL==pFlash)
    {
        MSRV_CONTROL_COMMON_ERR("GetFlash Fail\n");
        return FALSE;
    }
    else
    {
        if(E_MMC_CONFIG_STORAGE == envType)
        {
            bRet = pFlash->Write((MMC_BANK_SIZE*u8Bank), u32Size, (MAPI_U8 *)pu8Buffer);
        }
        else
        {
            bRet = pFlash->Write(u8Bank,0,(U32)pu8Buffer,u32Size, TRUE);
        }
        if(bRet == MAPI_FALSE)
        {
            MSRV_CONTROL_COMMON_ERR("WriteFlash Fail\n");
            return FALSE;
        }
        MSRV_CONTROL_COMMON_ERR("WriteFlash Success\n");
        return TRUE;
    }
}

BOOL MSrv_Control_common::WriteToFlashByAddr(const U32 u32FlashAddr, const U32 u32WriteSize, U8* const pu8Writebuffer)
{
    Imapi_storage_factory_config *pFlash = NULL;
    EN_BOOT_STORAGE envType = Imapi_storage_factory_config::GetEnvType();
    //printf("envType = %d\n",envType);
    pFlash = Imapi_storage_factory_config::GetInstance(envType);
    if(NULL==pFlash)
    {
        MSRV_CONTROL_COMMON_ERR("GetFlash Fail\n");
        return FALSE;
    }
    else
    {
        if(pFlash->Write(u32FlashAddr, u32WriteSize, pu8Writebuffer) == FALSE)
        {
            MSRV_CONTROL_COMMON_ERR("WriteFlash Fail\n");
            return FALSE;
        }
        MSRV_CONTROL_COMMON_ERR("WriteFlash Success\n");
        return TRUE;
    }

}

BOOL MSrv_Control_common::ReadFromSPIFlashByBank(const U8 u8Bank,  U8 * const pu8Buffer, const U32 u32Size)
{
    mapi_storage_spiflash *pSpiFlash = NULL;
    pSpiFlash = mapi_interface::Get_mapi_spiflash();
    if(NULL==pSpiFlash)
    {
        MSRV_CONTROL_COMMON_ERR("GetSPIFlash Fail\n");
        return FALSE;
    }
    else
    {
        if(pSpiFlash->Read((U16)u8Bank,(U16)0,(U32)pu8Buffer,u32Size) == FALSE)
        {
            MSRV_CONTROL_COMMON_ERR("ReadSPIFlash Fail\n");
            return FALSE;
        }
        MSRV_CONTROL_COMMON_ERR("ReadSPIFlash Success\n");
        return TRUE;
    }
}

BOOL MSrv_Control_common::ReadFromSPIFlashByAddr(const U32 u32FlashAddr, const U32 u32ReadSize, U8* const pu8Readbuffer)
{
    mapi_storage_spiflash *pSpiFlash = NULL;
    pSpiFlash = mapi_interface::Get_mapi_spiflash();
    if(NULL==pSpiFlash)
    {
        MSRV_CONTROL_COMMON_ERR("GetSPIFlash Fail\n");
        return FALSE;
    }
    else
    {
        if(pSpiFlash->Read(u32FlashAddr, u32ReadSize, pu8Readbuffer) == FALSE)
        {
            MSRV_CONTROL_COMMON_ERR("ReadSPIFlash Fail\n");
            return FALSE;
        }
        MSRV_CONTROL_COMMON_ERR("ReadSPIFlash Success\n");
        return TRUE;
    }
}

BOOL MSrv_Control_common::WriteToSPIFlashByBank(const U8 u8Bank, const U8 * const pu8Buffer, const U32 u32Size)
{
    mapi_storage_spiflash *pSpiFlash = NULL;
    pSpiFlash = mapi_interface::Get_mapi_spiflash();
    if(NULL==pSpiFlash)
    {
        MSRV_CONTROL_COMMON_ERR("GetSPIFlash Fail\n");
        return FALSE;
    }
    else
    {
        if(pSpiFlash->Write(u8Bank,0,(U32)pu8Buffer,u32Size, TRUE) == FALSE)
        {
            MSRV_CONTROL_COMMON_ERR("WriteSPIFlash Fail\n");
            return FALSE;
        }
        MSRV_CONTROL_COMMON_ERR("WriteSPIFlash Success\n");
        return TRUE;
    }
}

BOOL MSrv_Control_common::WriteToSPIFlashByAddr(const U32 u32FlashAddr, const U32 u32WriteSize, U8* const pu8Writebuffer)
{
    mapi_storage_spiflash *pSpiFlash = NULL;
    pSpiFlash = mapi_interface::Get_mapi_spiflash();
    if(NULL==pSpiFlash)
    {
        MSRV_CONTROL_COMMON_ERR("GetSPIFlash Fail\n");
        return FALSE;
    }
    else
    {
        if(pSpiFlash->Write(u32FlashAddr, u32WriteSize, pu8Writebuffer) == FALSE)
        {
            MSRV_CONTROL_COMMON_ERR("WriteSPIFlash Fail\n");
            return FALSE;
        }
        MSRV_CONTROL_COMMON_ERR("WriteSPIFlash Success\n");
        return TRUE;
    }
}

#if (ENABLE_LITE_SN != 1)
BOOL MSrv_Control_common::ReadFromEeprom(const U16 u16Index, U8 * const pu8Buffer, const U16 u16Size)
{
    mapi_storage_eeprom *pEeprom = NULL;
    pEeprom = mapi_interface::Get_mapi_eeprom();
    if(NULL==pEeprom)
    {
        MSRV_CONTROL_COMMON_ERR("GetEeprom Fail\n");
        return FALSE;
    }
    if(pEeprom->Read(u16Index, pu8Buffer, u16Size) == FALSE)
    {
        MSRV_CONTROL_COMMON_ERR("ReadEeprom Fail\n");
        return FALSE;
    }
    MSRV_CONTROL_COMMON_ERR("ReadEeprom Success\n");
    return TRUE;
}

BOOL MSrv_Control_common::WriteToEeprom(const U16 u16Index, U8 * const pu8Buffer, const U16 u16Size)
{
    mapi_storage_eeprom *pEeprom = NULL;
    pEeprom = mapi_interface::Get_mapi_eeprom();
    if(NULL==pEeprom)
    {
        MSRV_CONTROL_COMMON_ERR("GetEeprom Fail\n");
        return FALSE;
    }
    if(pEeprom->Write(u16Index, pu8Buffer, u16Size) == FALSE)
    {
        MSRV_CONTROL_COMMON_ERR("WriteEeprom Fail\n");
        return FALSE;
    }
    MSRV_CONTROL_COMMON_ERR("WriteEeprom Success\n");
    return TRUE;
}

U8 MSrv_Control_common::GetSarAdcLevel(U8 u8Channel)
{
    U8 u8Ret;
    u8Ret = mapi_sar::GetInstance()->GetSarAdcLevel(u8Channel);
    return u8Ret;
}
MAPI_BOOL MSrv_Control_common::PM51PWMInitialize(MAPI_BOOL bPWMONFF)
{
    return mapi_interface::Get_mapi_system()->PM51PWMInitialize(bPWMONFF);
}


MAPI_BOOL MSrv_Control_common::PM51PWMConfig(MAPI_PWM_SimIR_CFG *pPmPWMCfg)
{
    return mapi_interface::Get_mapi_system()->PM51PWMConfig(pPmPWMCfg);
}

void MSrv_Control_common::SwitchI2COnOff(MAPI_U32 u32gID, BOOL bEnabled)
{
    mapi_i2c *iptr = mapi_i2c::GetI2C_Dev(u32gID);
    iptr->SwitchOnOff(bEnabled);
}

BOOL MSrv_Control_common::SetEnvPowerMode(EN_ACON_POWERON_MODE ePowerMode)
{
    BOOL bRet = FALSE;
    if(ePowerMode >= EN_ACON_POWERON_MAX)
    {
        return bRet;
    }

    IEnvManager* pEnvMan = IEnvManager::Instance();
    if (pEnvMan == NULL)
    {
        ASSERT(0);
        return bRet;
    }

    switch(ePowerMode)
    {
        case EN_ACON_POWERON_SECONDARY: //Secondary, AC on will always enter standby mode
            pEnvMan->SetEnv_Protect("factory_poweron_mode", "secondary");
            break;

        case EN_ACON_POWERON_MEMORY: //Memory, DC off -> AC off -> AC on -> Standby
            pEnvMan->SetEnv_Protect("factory_poweron_mode", "memory");
            break;

        case EN_ACON_POWERON_DIRECT: //Direct, AC on will never enter standby mode
            pEnvMan->SetEnv_Protect("factory_poweron_mode", "direct");
            break;

        default:
            break;
    }

    pEnvMan->SaveEnv();

    bRet = TRUE;
    return bRet;
}

EN_ACON_POWERON_MODE MSrv_Control_common::GetEnvPowerMode(void)
{
    IEnvManager* pEnvMan = IEnvManager::Instance();
    if (pEnvMan == NULL)
    {
        ASSERT(0);
        return EN_ACON_POWERON_DIRECT;
    }

    IEnvManager_scope_lock block(pEnvMan);
    int size=pEnvMan->QueryLength("factory_poweron_mode");
    char tmp[size+1];
    memset(tmp,0,size+1);
    pEnvMan->GetEnv_Protect("factory_poweron_mode",tmp,size);
    if(tmp[0] == 0)
    {
        pEnvMan->SetEnv_Protect("factory_poweron_mode", "direct");
        pEnvMan->SaveEnv();

        printf("\r\n====power mode env not set===\r\n");
        return EN_ACON_POWERON_DIRECT;
    }

    if(strcmp(tmp,"secondary") == 0)
    {
        return EN_ACON_POWERON_SECONDARY;
    }
    else if(strcmp(tmp,"memory") == 0)
    {
        return EN_ACON_POWERON_MEMORY;
    }
    else if(strcmp(tmp,"direct") == 0)
    {
        return EN_ACON_POWERON_DIRECT;
    }
    else
    {
        pEnvMan->SetEnv_Protect("factory_poweron_mode", "direct");
        pEnvMan->SaveEnv();

        printf("\r\n====invalid power mode env,reset===\r\n");
        return EN_ACON_POWERON_DIRECT;
    }
}

BOOL MSrv_Control_common::SetEnvPowerOnLogoMode(EN_LOGO_MODE eLogoMode)
{
    BOOL bRet = FALSE;
    if(eLogoMode >= EN_LOGO_MAX)
    {
        return bRet;
    }

    IEnvManager* pEnvMan = IEnvManager::Instance();
    if (pEnvMan == NULL)
    {
        return bRet;
    }

    switch(eLogoMode)
    {
        case EN_LOGO_OFF: //no logo
            pEnvMan->SetEnv_Protect("logo", "0");
            break;

        case EN_LOGO_DEFAULT: //default logo
            pEnvMan->SetEnv_Protect("logo", "1");
            break;

        case EN_LOGO_CAPTURE1: //capture logo
            pEnvMan->SetEnv_Protect("logo", "2");
            break;

        case EN_LOGO_CAPTURE2: //capture logo2
            pEnvMan->SetEnv_Protect("logo", "3");
            break;

        default:
            break;
    }
    pEnvMan->SetEnv_Protect("db_table", "0");//for reload logo to SPI Flash

    pEnvMan->SaveEnv();

    bRet = TRUE;
    return bRet;
}

EN_LOGO_MODE MSrv_Control_common::GetEnvPowerOnLogoMode(void)
{
    IEnvManager* pEnvMan = IEnvManager::Instance();
    if (pEnvMan == NULL)
    {
        ASSERT(0);
        return EN_LOGO_DEFAULT;
    }

    IEnvManager_scope_lock block(pEnvMan);
    int size=pEnvMan->QueryLength("logo");
    char tmp[size+1];
    memset(tmp,0,size+1);
    pEnvMan->GetEnv_Protect("logo",tmp,size);
    if(tmp[0] == 0)
    {
        pEnvMan->SetEnv_Protect("logo", "1");
        pEnvMan->SetEnv_Protect("db_table", "0");//for reload logo to SPI Flash
        pEnvMan->SaveEnv();

        printf("\r\n====Logo env not set===\r\n");
        return EN_LOGO_DEFAULT;
    }

    if(strcmp(tmp,"0") == 0)
    {
        return EN_LOGO_OFF;
    }
    else if(strcmp(tmp,"1") == 0)
    {
        return EN_LOGO_DEFAULT;
    }
    else if(strcmp(tmp,"2") == 0)
    {
        return EN_LOGO_CAPTURE1;
    }
    else if(strcmp(tmp,"3") == 0)
    {
        return EN_LOGO_CAPTURE2;
    }
    else
    {
        pEnvMan->SetEnv_Protect("logo", "0");
        pEnvMan->SetEnv_Protect("db_table", "0");//for reload logo to SPI Flash
        pEnvMan->SaveEnv();

        printf("\r\n====invalid Logo env,reset===\r\n");
        return EN_LOGO_DEFAULT;
    }
}

BOOL MSrv_Control_common::SetEnvPowerOnMusicMode(EN_POWERON_MUSIC_MODE eMusicMode)
{
    BOOL bRet = FALSE;
    if(eMusicMode >= EN_POWERON_MUSIC_MAX)
    {
        return bRet;
    }

    IEnvManager* pEnvMan = IEnvManager::Instance();
    if (pEnvMan == NULL)
    {
        ASSERT(0);
        return bRet;
    }

    switch(eMusicMode)
    {
        case EN_POWERON_MUSIC_OFF: //music off
            pEnvMan->SetEnv_Protect("music", "0");
            break;

        case EN_POWERON_MUSIC_DEFAULT: //default music0
            pEnvMan->SetEnv_Protect("music", "1");
            break;

        case EN_POWERON_MUSIC_ONE: //music1
            pEnvMan->SetEnv_Protect("music", "2");
            break;

        default:
            break;
     }
    pEnvMan->SetEnv_Protect("db_table", "0");//for reload music to SPI Flash
    pEnvMan->SaveEnv();

    bRet = TRUE;
    return bRet;
}

EN_POWERON_MUSIC_MODE MSrv_Control_common::GetEnvPowerOnMusicMode(void)
{
    IEnvManager* pEnvMan = IEnvManager::Instance();

    if (pEnvMan == NULL)
    {
        ASSERT(0);
        return EN_POWERON_MUSIC_DEFAULT;
    }

    IEnvManager_scope_lock block(pEnvMan);
    int size=pEnvMan->QueryLength("music");
    char tmp[size+1];
    memset(tmp,0,size+1);
    pEnvMan->GetEnv_Protect("music",tmp,size);
    if(tmp[0] == 0)
    {
        pEnvMan->SetEnv_Protect("music", "1");
        pEnvMan->SetEnv_Protect("db_table", "0");//for reload music to SPI Flash
        pEnvMan->SaveEnv();

        printf("\r\n====music env not set===\r\n");
        return EN_POWERON_MUSIC_DEFAULT;
    }

    if(strcmp(tmp,"0") == 0)
    {
        return EN_POWERON_MUSIC_OFF;
    }
    else if(strcmp(tmp,"1") == 0)
    {
        return EN_POWERON_MUSIC_DEFAULT;
    }
    else if(strcmp(tmp,"2") == 0)
    {
        return EN_POWERON_MUSIC_ONE;
    }
    else
    {
        pEnvMan->SetEnv_Protect("music", "0");
        pEnvMan->SetEnv_Protect("db_table", "0");//for reload music to SPI Flash
        pEnvMan->SaveEnv();

        printf("\r\n====invalid music env,reset===\r\n");
        return EN_POWERON_MUSIC_DEFAULT;
    }
}

BOOL MSrv_Control_common::SetEnvPowerOnMusicVolume(U8 u8Volume)
{
    BOOL bRet = FALSE;
    if(u8Volume > MAX_POWERON_MUSIC_VOL)
    {
        return bRet;
    }

    char strVolume[6]={0};
    snprintf(strVolume,sizeof(strVolume),"0x%x",u8Volume);

    IEnvManager* pEnvMan = IEnvManager::Instance();
    if (pEnvMan != NULL)
    {
        pEnvMan->SetEnv_Protect("music_vol", (const char*)strVolume);
        pEnvMan->SaveEnv();
        bRet = TRUE;
    }
    else
    {
        ASSERT(0);
    }

    return bRet;
}

MAPI_U8 MSrv_Control_common::GetEnvPowerOnMusicVolume(void)
{
   BOOL bEnvError = FALSE;
   MAPI_U8 u8Volume = DEFAULT_POWERON_MUSIC_VOL;
    MAPI_U8 u8Volumetemp =0;
   IEnvManager* pEnvMan = IEnvManager::Instance();
    if (pEnvMan == NULL)
    {
        ASSERT(0);
        return u8Volume;
    }

    IEnvManager_scope_lock block(pEnvMan);
    int size=pEnvMan->QueryLength("music_vol");
    char tmp[size+1];
    memset(tmp,0,size+1);
    pEnvMan->GetEnv_Protect("music_vol",tmp,size);
    for(int index = 0; (index  < size) ; index ++)
    {
        if((tmp[0] !=  '0') && ((tmp[1] != 'x') || (tmp[1] != 'X')))
    {
            u8Volumetemp = 0;
        bEnvError = TRUE;
            continue;
    }
        else if(tmp[index] == '0' && (tmp[index+1] == 'x' || tmp[index+1] == 'X'))
    {
            u8Volumetemp = 0;
            index ++;
            bEnvError = FALSE;
            continue;
    }

    if(bEnvError == FALSE)
    {
            if((tmp[index] >= '0') && (tmp[index] <= '9'))
        {
                u8Volumetemp = (u8Volumetemp << 4) + (tmp[index] - '0');
    }
            else if((tmp[index] >= 'A') && (tmp[index] <= 'F'))
    {
                u8Volumetemp = (u8Volumetemp << 4) + (tmp[index] - 'A' + 10);
        }
            else if((tmp[index] >= 'a') && (tmp[index] <= 'f'))
        {
                u8Volumetemp = (u8Volumetemp << 4) + (tmp[index] - 'a' + 10);
        }
        }
        }
        if(bEnvError == FALSE)
        {
    u8Volume=u8Volumetemp;
        }
    if(bEnvError == TRUE)
    {
        char strVolume[5] = {0};
        snprintf(strVolume,sizeof(strVolume),"0x%x",DEFAULT_POWERON_MUSIC_VOL);
        pEnvMan->SetEnv_Protect("music_vol", (const char*)strVolume);
        pEnvMan->SaveEnv();
    }

    return u8Volume;
}
#endif

#if (ACTIVE_STANDBY_MODE_ENABLE == 1)
BOOL MSrv_Control_common::SetActiveStandbyMode(BOOL bActive)
{
    BOOL bRet = TRUE;

    if(bActive != m_bStandbyModeActive)
    {
            m_bStandbyModeActive = bActive;
#if (MSTAR_TVOS == 0)
            MSystem::SetActiveStandbyMode(m_bStandbyModeActive);
#endif
            if(TRUE == m_bStandbyModeActive)
            {
                mapi_interface::Get_mapi_audio()->SPDIF_HWEN(FALSE);               //set SPDIF mute
                mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_MONITOR_MODE)->SetVideoMute(MAPI_TRUE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN);
#if (STB_ENABLE == 1)
                //turn off HD & SD output
                //need to refine, only workable on MST138B
                mapi_interface::Get_mapi_display()->HDOutputOnOff(FALSE);
                mapi_interface::Get_mapi_display()->DacOnOff(FALSE);
#else //STB_ENABLE
                // panel off flow: lamp -> LVDS -> LCD power
                if(FALSE == MSrv_Control_common::SetGpioDeviceStatus(INV_CTL, FALSE))
                {
                    printf("\x1b[37;41m [%s:%s:%d] MSrv_Control_common::SetGpioDeviceStatus FAIL \x1b[0m\n",__FILE__,__FUNCTION__,__LINE__);
                    bRet = FALSE;
                }
#endif //STB_ENABLE
                mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_PERMANENT_MUTEON_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
            }
            else
            {
                mapi_interface::Get_mapi_audio()->SPDIF_HWEN(TRUE);               //set SPDIF unmute
                mapi_interface::Get_mapi_video_out(MAPI_VIDEO_OUT_MONITOR_MODE)->SetVideoMute(MAPI_FALSE, mapi_video_out_datatype::MAPI_VIDEO_OUT_MUTE_GEN);
#if (STB_ENABLE == 1)
                //turn on HD & SD output
                //need to refine, only workable on MST138B
                mapi_interface::Get_mapi_display()->DacOnOff(TRUE);
                mapi_interface::Get_mapi_display()->HDOutputOnOff(TRUE);
#else //STB_ENABLE
                // panel off flow: lamp -> LVDS -> LCD power
                if(FALSE == MSrv_Control_common::SetGpioDeviceStatus(INV_CTL, TRUE))
                {
                    printf("\x1b[37;41m [%s:%s:%d] MSrv_Control_common::SetGpioDeviceStatus FAIL \x1b[0m\n",__FILE__,__FUNCTION__,__LINE__);
                    bRet = FALSE;
                }
#endif //STB_ENABLE
              mapi_interface::Get_mapi_audio()->SetSoundMuteStatus(E_AUDIO_PERMANENT_MUTEOFF_, E_AUDIOMUTESOURCE_ACTIVESOURCE_);
            }
    }

    return bRet;
}

BOOL MSrv_Control_common::GetActiveStandbyMode(void)
{
    if(TRUE == m_bStandbyModeActive)
    {
        if(0 == MSrv_Control_common::GetGpioDeviceStatus(INV_CTL))
        {
            //printf("\x1b[37;41m [%s:%s:%d] MSrv_Control_common::GetGpioDeviceStatus Mismatch ERROR \x1b[0m\n",__FILE__,__FUNCTION__,__LINE__);
        }
    }
#if (MSTAR_TVOS == 0)
    if(m_bStandbyModeActive != MSystem::GetActiveStandbyMode())
    {
        printf("\x1b[37;41m [%s:%s:%d] MSystem::GetBackGroundMode() Mismatch ERROR \x1b[0m\n",__FILE__,__FUNCTION__,__LINE__);
    }
#endif
    return m_bStandbyModeActive;
}

BOOL MSrv_Control_common::StandbyModeActiveProcess(void)
{
    BOOL bActive = FALSE;
    if(FALSE == mapi_interface::Get_mapi_system()->PM51IsStandbyModeActive(bActive))
    {
        m_bStandbyModeActive = FALSE;
        printf("\x1b[37;41m [%s:%s:%d] mapi_interface::Get_mapi_system()->PM51IsStandbyModeActive(bActive) FAIL \x1b[0m\n",__FILE__,__FUNCTION__,__LINE__);
        return FALSE;
    }
#if (MSTAR_TVOS == 1)
    if(bActive)
        property_set("mstar.pvr.standby.recording","true");
    else
        property_set("mstar.pvr.standby.recording","false");
#endif
    return SetActiveStandbyMode(bActive);
}

void MSrv_Control_common::SetIsActiveStandbyMode(BOOL bActive)
{
#if (MSTAR_TVOS == 0)
    if(bActive == MSystem::GetActiveStandbyMode())
    {
        m_bStandbyModeActive = bActive;
    }
    else
    {
        printf("\x1b[37;41m [%s:%s:%d] MSystem::GetActiveStandbyMode() Mismatch, SetIsActiveStandbyMode Unsuccess!! \x1b[0m\n",__FILE__,__FUNCTION__,__LINE__);
    }
#else //MSTAR_TVOS == 1
    m_bStandbyModeActive = bActive;

    if(bActive)
        property_set("mstar.pvr.standby.recording","true");
    else
        property_set("mstar.pvr.standby.recording","false");


#endif
}

BOOL MSrv_Control_common::OnEvent_ActiveStandbyMode(void* arg1, void* arg2, void* arg3)
{
    U32 u32OnOff = (U32)arg2;
    BOOL bActive = (u32OnOff != 0);
    return SetActiveStandbyMode(bActive);

}

BOOL MSrv_Control_common::CheckAllActiveStandbyTaskFinished(void)
{
    U8 u8_i=0;
    BOOL bRet = TRUE;

    for(u8_i = 0; u8_i < E_ACTIVE_STANDBY_TASK_NUM;u8_i++)
    {
        if(stActiveStandbyTask[u8_i].bFinished == FALSE)
        {
            bRet = FALSE;
            break;
        }
    }

    return bRet;
}

BOOL MSrv_Control_common::RequestActiveStandbyMode(EN_ACTIVE_STANDBY_CMD enActiveCmd, EN_ACTIVE_STANDBY_MODE_TASK taskId , BOOL bActiveStandby, BOOL bFinished)
{
    mapi_scope_lock(scopeLock, &m_MutexSetActiveStandby);

    BOOL bActive=FALSE;
    BOOL bRet=TRUE;

    bActive = bActiveStandby;

    if(enActiveCmd ==E_ACTIVE_STANDBY_CMD_SET_ACTIVE_STANDBY)
    {
            switch(taskId)
            {
                case E_ACTIVE_STANDBY_TASK_PVR:
                       {
                            if(bActive == TRUE)
                            {
                                if(stActiveStandbyTask[E_ACTIVE_STANDBY_TASK_STANDBY_SCAN].bInActiveStandby == TRUE)
                                {
                                    //need to stop Standby scan
                                    bRet = TRUE;
                                     stActiveStandbyTask[E_ACTIVE_STANDBY_TASK_STANDBY_SCAN].bInActiveStandby = FALSE;
                                     stActiveStandbyTask[E_ACTIVE_STANDBY_TASK_STANDBY_SCAN].bFinished = TRUE;
                                }
                                else if(stActiveStandbyTask[E_ACTIVE_STANDBY_TASK_OAD].bInActiveStandby == TRUE)
                                {
                                    //need to stop OAD
                                    bRet = TRUE;    //if OAD terminate successully
                                     stActiveStandbyTask[E_ACTIVE_STANDBY_TASK_OAD].bInActiveStandby = FALSE;
                                     stActiveStandbyTask[E_ACTIVE_STANDBY_TASK_OAD].bFinished = TRUE;
                                }
                            }

                    }
                    break;

                case E_ACTIVE_STANDBY_TASK_STANDBY_SCAN:
                    {
                            if(bActive == TRUE)
                            {
                                if(stActiveStandbyTask[E_ACTIVE_STANDBY_TASK_PVR].bInActiveStandby == TRUE)
                                {
                                       bRet = FALSE;
                                }
                                else if(stActiveStandbyTask[E_ACTIVE_STANDBY_TASK_OAD].bInActiveStandby == TRUE)
                                {
                                    //need to stop OAD
                                    bRet = TRUE;
                                     stActiveStandbyTask[E_ACTIVE_STANDBY_TASK_OAD].bInActiveStandby = FALSE;
                                     stActiveStandbyTask[E_ACTIVE_STANDBY_TASK_OAD].bFinished = TRUE;

                                }
                            }

                    }
                    break;

                case  E_ACTIVE_STANDBY_TASK_OAD:
                    {
                            if(bActive == TRUE)
                            {
                                if(stActiveStandbyTask[E_ACTIVE_STANDBY_TASK_PVR].bInActiveStandby == TRUE)
                                {
                                       bRet = FALSE;
                                }
                                else if(stActiveStandbyTask[E_ACTIVE_STANDBY_TASK_STANDBY_SCAN].bInActiveStandby == TRUE)
                                {
                                    bRet = FALSE;
                                }
                                else
                                {
                                    bRet = TRUE;
                                }
                            }

                    }
                    break;

                case E_ACTIVE_STANDBY_TASK_MHL:
                    {
                        // if MHL device is connected, tv enter active standby mode.
                        bRet = TRUE;
                    }
                    break;

                default:
                        bRet = FALSE;
                    break;
            }

        if(bRet == TRUE)
        {
            stActiveStandbyTask[taskId].bInActiveStandby = bActive;
            stActiveStandbyTask[taskId].bFinished = bFinished;
        }
        else
        {
             printf("\x1b[37;41m [%s:%s:%d] MSrv_Control_common::RequestActiveStandbyMode FAIL \x1b[0m\n",__FILE__,__FUNCTION__,__LINE__);
             return FALSE;
        }


    }

    //check to enter standby mode
    if(bFinished == TRUE && bActive == TRUE)
    {
        if(CheckAllActiveStandbyTaskFinished())
        {
            EnterSleepMode();
        }
    }

   return SetActiveStandbyMode(bActive);
}

#endif

BOOL MSrv_Control_common::IsSupportActiveStandBy()
{
#if (ACTIVE_STANDBY_MODE_ENABLE == 1)
    return TRUE;
#else
    return FALSE;
#endif
}

BOOL MSrv_Control_common::RequestAsynclyCommand(EN_TV_ASYNC_CMD enTvCmd, U8 u8OriginalDtvRoute, U8 u8TargetDtvRoute ,MSrvAsyncCmdCallBack pfunc)
{
    MSRV_TV_ASYNC_CMD_EVENT stCmd;
    int iRet = 0;
    UNUSED(iRet);

    memset(&stCmd, 0, sizeof(MSRV_TV_ASYNC_CMD_EVENT));

    if (m_bInit == FALSE)
    {
        return FALSE;
    }

    stCmd.enCmd = enTvCmd;
    stCmd.pCallback = pfunc;
    stCmd.u32Param1 = (U32)u8OriginalDtvRoute;
    stCmd.u32Param2 = (U32)u8TargetDtvRoute;
    m_pTVAsyncCmdEvt->Send(stCmd);
    switch(enTvCmd)
    {
        case E_TV_CMD_SWITCH_ROUTE_CI:
        case E_TV_CMD_SWITCH_DTV_CI_SLOT:
        {
#if (CI_ENABLE == 1)
            MSrv_Control::GetMSrvCIMMI()->PostEvent(NULL,EV_SYSTEM_ASYNC_CMD,enTvCmd,0);
#endif
            break;
        }
        default:
        {
            PostEvent(NULL,EV_SYSTEM_ASYNC_CMD,enTvCmd,0);
            break;
        }
    }
    return TRUE;
}
BOOL MSrv_Control_common::HandleUICmdService()
{
    int iRet = 0;
    MSRV_TV_ASYNC_CMD_EVENT stAsyncCmdEvent;
    memset(&stAsyncCmdEvent, 0, sizeof(MSRV_TV_ASYNC_CMD_EVENT));
    iRet = m_pTVAsyncCmdEvt->Wait(&stAsyncCmdEvent, THREAD_MONITOR_INTERVAL_MS);
    if (iRet == 0)
    {
        switch(stAsyncCmdEvent.enCmd)
        {
            case E_TV_CMD_SWITCH_ROUTE_CI:
            {
                printf("E_TV_CMD_SWITCH_ROUTE_CI\n");
                SwitchMSrvDtvRoute((MAPI_U8)stAsyncCmdEvent.u32Param2, MAPI_MAIN_WINDOW, FALSE, FALSE);
                if(stAsyncCmdEvent.pCallback != 0)
                {
                    stAsyncCmdEvent.pCallback((void *)stAsyncCmdEvent.u32Param1, (void *)stAsyncCmdEvent.u32Param2);
                }
                break;
            }
            case E_TV_CMD_SWITCH_DTV_CI_SLOT:
        {
                printf("E_TV_CMD_SWITCH_DTV_CI_SLOT\n");
            if(stAsyncCmdEvent.pCallback != 0)
                {
                    stAsyncCmdEvent.pCallback((void *)stAsyncCmdEvent.u32Param1, (void *)stAsyncCmdEvent.u32Param2);
                }
                break;
            }
            default:
                break;
        }
    }
    return MAPI_TRUE;
}
BOOL MSrv_Control_common::WaitAudioInit(U32 u32WaitTime)
{
    U32 u32WaitStart=mapi_time_utility::GetTime0();
    while((FALSE == mapi_interface::Get_mapi_audio()->IsAudioInitDone())
        && (mapi_time_utility::TimeDiffFromNow0(u32WaitStart) < u32WaitTime))
    {
        usleep(15000); //15ms
    }
    /*
    if(mapi_interface::Get_mapi_audio()->IsAudioInitDone())
    {
        printf("\033[1;31m  wait time %d \033[0m\n",mapi_time_utility::TimeDiffFromNow0(u32WaitStart));
    }
    */
    return mapi_interface::Get_mapi_audio()->IsAudioInitDone();
}

#if (ENABLE_LITE_SN != 1)
BOOL MSrv_Control_common::ReadBytesFromI2C(MAPI_U32 u32gID, MAPI_U8 u8AddrSize, MAPI_U8 *pu8Addr, MAPI_U16 u16Size, MAPI_U8 *pu8Data)
{
    mapi_i2c *iptr;
    iptr = mapi_i2c::GetI2C_Dev(u32gID);
    if (NULL == iptr)
        return FALSE;
    else
        return iptr->ReadBytes(u8AddrSize, pu8Addr, u16Size, pu8Data);
}

BOOL MSrv_Control_common::WriteBytesToI2C(MAPI_U32 u32gID, MAPI_U8 u8AddrSize, MAPI_U8 *pu8Addr, MAPI_U16 u16Size, MAPI_U8 *pu8Data)
{
    mapi_i2c *iptr;
    iptr = mapi_i2c::GetI2C_Dev(u32gID);
    if (NULL == iptr)
        return FALSE;
    else
        return iptr->WriteBytes(u8AddrSize, pu8Addr, u16Size, pu8Data);
}
#endif

#if(MSTAR_TVOS == 1) // for tvapp and supernova mutually send message
BOOL MSrv_Control_common::GetTvosInterfaceCMD(char* pCharDataBlock, const U8 u8length)
{
    mapi_scope_lock(scopeLock, &m_TvosInterfaceCMDMutex);
    ASSERT(pCharDataBlock);

    U8 u8Len = strlen(m_aCharDataBlock);
    if(u8length <= u8Len)
    {
        MSRV_CONTROL_COMMON_ERR("ERROR pCharDataBlock length out of your getting u8length\n");
        return FALSE;
    }

    strncpy(pCharDataBlock, m_aCharDataBlock, u8Len);
    pCharDataBlock[u8Len] = '\0';

    return TRUE;
}

BOOL MSrv_Control_common::SetTvosInterfaceCMD(const char* pCharCommand)
{
    mapi_scope_lock(scopeLock, &m_TvosInterfaceCMDMutex);

    U8 u8length  = strlen(pCharCommand);
    if(u8length >= MAX_DATA_BLOCK_LEN)
    {
        MSRV_CONTROL_COMMON_ERR("ERROR SetTvosInterfaceCMD over MAX_DATA_BLOCK_LEN\n");
        return FALSE;
    }
    strncpy(m_aCharDataBlock, pCharCommand, u8length);
    m_aCharDataBlock[u8length] = '\0';

    return TRUE;
}

U16* MSrv_Control_common::SetTvosCommonCommand(const char* pCharCommand, int* u8length)
{
    U8 i;

    if((pCharCommand) && (strncmp(pCharCommand, TVOS_COMMON_CMD_GET_SOURCE_STATUS, strlen(pCharCommand)) == 0))
    {
        *u8length = MAPI_INPUT_SOURCE_NUM;
        for(i=0;i<MAPI_INPUT_SOURCE_NUM;i++)
        {
            m_u16DataBlock[i] = (U16)GetMSrvSourceDetect()->GetSrvDetGoodStatus((MAPI_INPUT_SOURCE_TYPE)i);
        }
    }
#if (ATSC_SYSTEM_ENABLE == 1)
    else if((pCharCommand) && (strncmp(TVOS_COMMON_CMD_CHECK_DIRECT_TUNE_DTV_RF_VALID, pCharCommand, strlen(TVOS_COMMON_CMD_CHECK_DIRECT_TUNE_DTV_RF_VALID)) == 0)
            && (0 < strlen(pCharCommand) - strlen(TVOS_COMMON_CMD_CHECK_DIRECT_TUNE_DTV_RF_VALID))
            && strstr(pCharCommand, "/"))
    {
        ST_MEDIUM_SETTING stMediumSetting;
        U16 u16RfNum = 0;

        u16RfNum = atoi(strstr(pCharCommand, "/") + 1);
        memset(&m_u16DataBlock,0,sizeof(m_u16DataBlock));
        *u8length = 1;
        GetMSrvSystemDatabase()->GetMediumSetting(&stMediumSetting);
        if (stMediumSetting.AntennaType == E_ANTENNA_TYPE_AIR)
        {
            m_u16DataBlock[0] = (u16RfNum >= 2) ? ((u16RfNum <= 69) ? 1 : 0) : 0;
        }
        else if (stMediumSetting.AntennaType == E_ANTENNA_TYPE_CABLE)
        {
            m_u16DataBlock[0] = (u16RfNum >= 1) ? ((u16RfNum <= 135) ? 1 : 0) : 0;
        }
    }
    else if((pCharCommand) && (strncmp(TVOS_COMMON_CMD_CHECK_DIRECT_TUNE_ATV_RF_VALID, pCharCommand, strlen(TVOS_COMMON_CMD_CHECK_DIRECT_TUNE_ATV_RF_VALID)) == 0)
            && (0 < strlen(pCharCommand) - strlen(TVOS_COMMON_CMD_CHECK_DIRECT_TUNE_ATV_RF_VALID))
            && strstr(pCharCommand, "/"))
    {
        ST_ATV_MISC Misc;
        U16 u16RfNum = 0;

        u16RfNum = atoi(strstr(pCharCommand, "/") + 1);
        memset(&m_u16DataBlock,0,sizeof(m_u16DataBlock));
        *u8length = 1;
        if (u16RfNum > 0xFF)
        {
            m_u16DataBlock[0] = 0;
        }
        else
        {
            if(MSrv_Control::GetMSrvAtvDatabase()->GetProgramInfo(IS_DIRECT_TUNED, u16RfNum , 0 , (U8 *)&Misc))
            {
                m_u16DataBlock[0] = 0;
            }
            else
            {
                m_u16DataBlock[0] = 1;
            }
        }
    }
#endif
    else
    {
        memset(&m_u16DataBlock,0,sizeof(m_u16DataBlock));
        *u8length = MAX_DATA_BLOCK_LEN;
    }

    return m_u16DataBlock;
}

BOOL MSrv_Control_common::SendTvosUtilityEvent(U32 nEvt, U32 wParam)
{
    BOOL bRet = FALSE;
    bRet = PostEvent(NULL, EV_TVOS_UTIITY_EVENT, nEvt, wParam);
    return bRet;
}

BOOL MSrv_Control_common::SendTvos3DFormat(U32 _3dFormat)
{
    BOOL bRet = FALSE;

    bRet = PostEvent(NULL, EV_TVOS_UTIITY_EVENT, ENABLE_3D_FORMAT, _3dFormat);

    return bRet;
}

BOOL MSrv_Control_common::SendExitTvos3DFormat(void)
{
    BOOL bRet = FALSE;

    bRet = PostEvent(NULL, EV_TVOS_UTIITY_EVENT, DISABLE_3D_FORMAT, 0x00);
    return bRet;
}

#endif

#if (CI_PLUS_ENABLE == 1)
BOOL MSrv_Control_common::AskCiHcRelease(void)
{
    MSRV_CMD stCmd;
    memset(&stCmd, 0, sizeof(MSRV_CMD));
    stCmd.enCmd = E_CMD_CI_HC_ASK_RELEASE;
    m_pCmdEvt->Send(stCmd);

    if(0 ==  m_pCmdAckEvt->Wait(&stCmd))
    {
        if(E_CMD_CI_HC_ASK_RELEASE_REPLY == stCmd.enCmd)
        {
            if((MAPI_U32)TRUE == stCmd.u32Param1)
            {
                return TRUE;
            }
            else if((MAPI_U32)FALSE == stCmd.u32Param1)
            {
                return FALSE;
            }
        }
    }

    return TRUE;
}

void MSrv_Control_common::SendCiHcReleaseReply(BOOL bIsReleaseOk)
{
    MSRV_CMD stCmd;
    memset(&stCmd, 0, sizeof(MSRV_CMD));
    stCmd.enCmd = E_CMD_CI_HC_ASK_RELEASE_REPLY;
    stCmd.u32Param1 = (MAPI_U32)bIsReleaseOk;
    m_pCmdEvt->Send(stCmd);
}
#endif

MSrv_Control_common::MSrv_PlayerControl* MSrv_Control_common::MSrv_PlayerControl::m_pInstance = NULL;
MSrv* MSrv_Control_common::MSrv_PlayerControl::m_pMSrvPlayerList[MSrv_Control_common::E_MSRV_PLAYER_MAX] = {0,};

MSrv_Control_common::MSrv_PlayerControl* MSrv_Control_common::MSrv_PlayerControl::GetInstance()
{
    if (m_pInstance ==NULL)
    {
        m_pInstance = new MSrv_PlayerControl();
    }
    return m_pInstance;
}

MSrv_Control_common::MSrv_PlayerControl::MSrv_PlayerControl()
{
    m_stPlayerState = ST_PLAYER_STATE();
    for (unsigned int i = MAPI_MAIN_WINDOW; i < MAPI_MAX_WINDOW; i++)
    {
        m_stPlayerState.v_PIPSource.push_back(MAPI_INPUT_SOURCE_NONE);
        m_stPlayerState.v_PIPRoute.push_back(UNUSED_DTV_ROUTE);
    }

    m_stConflictMsg.strConflictMsg = "";
    m_u8DtvRouteCount = 0;
    m_pInputSrcTable = SystemInfo::GetInstance()->GetInputMuxInfo();
    m_enPVRStatus = E_RECORD_NONE;

    pthread_mutexattr_t attr;
    PTH_RET_CHK(pthread_mutexattr_init(&attr));
    PTH_RET_CHK(pthread_mutexattr_settype(&attr, PTHREAD_MUTEX_RECURSIVE));
    PTH_RET_CHK(pthread_mutex_init(&m_MutexPlayerControl, &attr));
}

MSrv_Control_common::MSrv_PlayerControl::~MSrv_PlayerControl()
{
    pthread_mutex_destroy(&m_MutexPlayerControl);
}

BOOL MSrv_Control_common::MSrv_PlayerControl::GetPlayerState(ST_PLAYER_STATE& stPlayerState)
{
    mapi_scope_lock(scopeLock, &m_MutexPlayerControl);
    stPlayerState = m_stPlayerState;
    return TRUE;
}

BOOL MSrv_Control_common::MSrv_PlayerControl::SetPlayerState(const ST_PLAYER_STATE& stPlayerState)
{
    mapi_scope_lock(scopeLock, &m_MutexPlayerControl);
    m_stPlayerState = stPlayerState;
#if (MSTAR_TVOS == 1)
    if(!MSrv_Control::GetInstance()->IsPipModeEnable())
    {
        m_stPlayerState.v_PIPRoute[MAPI_SUB_WINDOW] = m_stPlayerState.v_PIPRoute[MAPI_MAIN_WINDOW];
    }
#endif
    UpdateRecordStatusNotify();
    return TRUE;
}

MAPI_INPUT_SOURCE_TYPE MSrv_Control_common::MSrv_PlayerControl::GetPIPSourceType(MAPI_SCALER_WIN eWin)
{
    mapi_scope_lock(scopeLock, &m_MutexPlayerControl);
    return m_stPlayerState.v_PIPSource[eWin];
}

#if (MSTAR_TVOS == 1)
BOOL MSrv_Control_common::MSrv_PlayerControl::SetPIPSourceType(MAPI_INPUT_SOURCE_TYPE eSource, MAPI_SCALER_WIN eWin)
{
    mapi_scope_lock(scopeLock, &m_MutexPlayerControl);
    m_stPlayerState.v_PIPSource[eWin] = eSource;
    return TRUE;
}
#endif

U8 MSrv_Control_common::MSrv_PlayerControl::GetCurrentDtvRoute(MAPI_SCALER_WIN eWin)
{
    mapi_scope_lock(scopeLock, &m_MutexPlayerControl);
    return m_stPlayerState.v_PIPRoute[eWin];
}

U8 MSrv_Control_common::MSrv_PlayerControl::GetDtvRouteCount(void)
{
    mapi_scope_lock(scopeLock, &m_MutexPlayerControl);
    return m_u8DtvRouteCount;
}

#if(PIP_ENABLE == 1) || (TWIN_TUNER == 1)
MSrv_Control_common::EN_CONFLICT_TYPE MSrv_Control_common::MSrv_PlayerControl::CheckConflict(ST_PLAYER_STATE& stPlayerState)
{
    mapi_scope_lock(scopeLock, &m_MutexPlayerControl);

    /*
    PC_WAR("===User case===");
    PC_MSG("[Main Source] from [%d] to [%d]", m_stPlayerState.v_PIPSource[MAPI_MAIN_WINDOW], stPlayerState.v_PIPSource[MAPI_MAIN_WINDOW]);
    PC_MSG("[Main Route]  from [%d] to [%d]", m_stPlayerState.v_PIPRoute[MAPI_MAIN_WINDOW], stPlayerState.v_PIPRoute[MAPI_MAIN_WINDOW]);
    PC_MSG("[Sub Source] from [%d] to [%d]", m_stPlayerState.v_PIPSource[MAPI_SUB_WINDOW], stPlayerState.v_PIPSource[MAPI_SUB_WINDOW]);
    PC_MSG("[Sub Route] from [%d] to [%d]", m_stPlayerState.v_PIPRoute[MAPI_SUB_WINDOW], stPlayerState.v_PIPRoute[MAPI_SUB_WINDOW]);
    printf("[PVR Route] from [");
    for (unsigned int i = 0; i < m_stPlayerState.v_PVRRoute.size(); i++)
    {
        printf("%d ", m_stPlayerState.v_PVRRoute[i]);
    }
    printf("] to [");
    for (unsigned int i = 0; i < stPlayerState.v_PVRRoute.size(); i++)
    {
        printf("%d ", stPlayerState.v_PVRRoute[i]);
    }
    printf("]\n");
    */

    MAPI_SCALER_WIN eChangeWin = MAPI_MAX_WINDOW;
    EN_CONFLICT_TYPE retType = EN_CONFLICT_NONE;
    U8 u8DisableRouteIndex = UNUSED_DTV_ROUTE;

    BOOL bBBConflict = FALSE;
    BOOL bBGChange = FALSE;
    BOOL bTunerConflict = FALSE;
    BOOL bDisableBG = FALSE;
    BOOL bPIPConflict = FALSE;
    BOOL bFGRecordConflict = FALSE;

    m_stConflictMsg.strConflictMsg = "";

#if (MSTAR_TVOS == 0)
    if ((m_stPlayerState.v_PIPSource[MAPI_MAIN_WINDOW] != stPlayerState.v_PIPSource[MAPI_MAIN_WINDOW]) ||
        (m_stPlayerState.v_PIPRoute[MAPI_MAIN_WINDOW] != stPlayerState.v_PIPRoute[MAPI_MAIN_WINDOW]))
    {
        eChangeWin = MAPI_MAIN_WINDOW;
    }
    else if ((m_stPlayerState.v_PIPSource[MAPI_SUB_WINDOW] != stPlayerState.v_PIPSource[MAPI_SUB_WINDOW]) ||
        (m_stPlayerState.v_PIPRoute[MAPI_SUB_WINDOW] != stPlayerState.v_PIPRoute[MAPI_SUB_WINDOW]))
    {
        eChangeWin = MAPI_SUB_WINDOW;
    }

    //DTV change case from background to foreground
    if ((stPlayerState.v_PIPSource[eChangeWin] == MAPI_INPUT_SOURCE_DTV) ||
        (stPlayerState.v_PIPSource[eChangeWin] == MAPI_INPUT_SOURCE_DTV2))
    {
        for (unsigned int i = 0; i < stPlayerState.v_PVRRoute.size(); i++)
        {
            if (stPlayerState.v_PVRRoute[i] == stPlayerState.v_PIPRoute[eChangeWin])
            {
                stPlayerState.v_PVRRoute.erase(stPlayerState.v_PVRRoute.begin()+i);
                break;
            }
        }
    }

    //Debug msg for background record
    /*
    printf("[PVR Route] from [");
    for (unsigned int i = 0; i < m_stPlayerState.v_PVRRoute.size(); i++)
    {
        printf("%d ", m_stPlayerState.v_PVRRoute[i]);
    }
    printf("] to [");
    for (unsigned int i = 0; i < stPlayerState.v_PVRRoute.size(); i++)
    {
        printf("%d ", stPlayerState.v_PVRRoute[i]);
    }
    printf("]\n");
    */

    //Check background state change
    if (m_stPlayerState.v_PVRRoute.size() != stPlayerState.v_PVRRoute.size())
    {
        bBGChange = TRUE;
    }

    //Check background route set conflict
    vector<EN_DTV_TYPE> dtvRouteSet;
    for (unsigned int i = 0; i < stPlayerState.v_PVRRoute.size(); i++)
    {
        dtvRouteSet.push_back(SystemInfo::GetInstance()->GetDTVRouteEnumByRouteIndex(stPlayerState.v_PVRRoute[i]));
    }
    if (TRUE == SystemInfo::GetInstance()->IsDTVRouteConflict(dtvRouteSet))
    {
        bBBConflict = TRUE;
    }

    //Check foreground and background tuner conflict
    if (TRUE == MSrv_PlayerConfigTable_Tuner::GetInstance()->CheckConflict(stPlayerState))
    {
        bTunerConflict = TRUE;

        ST_PLAYER_STATE tmpPlayerState = stPlayerState;
        tmpPlayerState.v_PVRRoute.clear();
        if (FALSE == MSrv_PlayerConfigTable_Tuner::GetInstance()->CheckConflict(tmpPlayerState))
        {
            bDisableBG = TRUE;
        }
    }

    //Check foreground PIP source conflict
    if (TRUE == MSrv_PlayerConfigTable_PIP::GetInstance()->CheckConflict(stPlayerState))
    {
        bPIPConflict = TRUE;
    }
#if (PVR_ENABLE == 1)
    //Check foreground record change to background be conflict
    if ((eChangeWin != MAPI_MAX_WINDOW) &&
        ((m_stPlayerState.v_PIPSource[eChangeWin] == MAPI_INPUT_SOURCE_DTV) ||
        (m_stPlayerState.v_PIPSource[eChangeWin] == MAPI_INPUT_SOURCE_DTV2)))
    {
        MSrv_DTV_Player_DVB *dtvPlayer = dynamic_cast<MSrv_DTV_Player_DVB *>(m_pMSrvPlayerList[E_MSRV_DTV_PLAYER_0 + m_stPlayerState.v_PIPRoute[eChangeWin]]);
        ASSERT(dtvPlayer);

        if (dtvPlayer->IsRecording() == TRUE)
        {
            ST_PLAYER_STATE tmpPlayerState = stPlayerState;
            tmpPlayerState.v_PVRRoute.push_back(m_stPlayerState.v_PIPRoute[eChangeWin]);
            if (TRUE == MSrv_PlayerConfigTable_Tuner::GetInstance()->CheckConflict(tmpPlayerState))
            {
                bFGRecordConflict = TRUE;
            }
        }
    }
#endif
    /*
    PC_WAR("BBConflict=%d", bBBConflict);
    PC_WAR("BGChange=%d", bBGChange);
    PC_WAR("TunerConflict=%d", bTunerConflict);
    PC_WAR("DisableBG=%d", bDisableBG);
    PC_WAR("PIPConflict=%d", bPIPConflict);
    */

#endif

    if ((bBBConflict == FALSE) && (bTunerConflict == FALSE) && (bPIPConflict == FALSE))
    {
        if (bFGRecordConflict != TRUE)
        {
            return EN_CONFLICT_NONE;
        }
    }
    else
    {
        bFGRecordConflict = FALSE;
    }

    //Main source or route change, with sub source conflict
    if ((eChangeWin == MAPI_MAIN_WINDOW) &&
        ((bBGChange == FALSE) && (bBBConflict == FALSE) && (bDisableBG == FALSE) && (bFGRecordConflict == FALSE)))
    {
        eChangeWin = MAPI_MAIN_WINDOW;

        //Main route change
        if (stPlayerState.v_PIPSource[MAPI_MAIN_WINDOW] == MAPI_INPUT_SOURCE_DTV)
        {
            retType = EN_CONFLICT_ROUTE;
        }
        //Main source change
        else
        {
            retType = EN_CONFLICT_SOURCE;
        }

        //Sub clean
        stPlayerState.v_PIPSource[MAPI_SUB_WINDOW] = MAPI_INPUT_SOURCE_NONE;
        stPlayerState.v_PIPRoute[MAPI_SUB_WINDOW] = UNUSED_DTV_ROUTE;
    }
    //Sub source or route change, with main source conflict
    else if ((eChangeWin == MAPI_SUB_WINDOW) &&
        ((bBGChange == FALSE) && (bBBConflict == FALSE) && (bDisableBG == FALSE) && (bFGRecordConflict == FALSE)))
    {
        eChangeWin = MAPI_SUB_WINDOW;

        if (stPlayerState.v_PIPSource[MAPI_SUB_WINDOW] == MAPI_INPUT_SOURCE_DTV2)
        {
            retType = EN_CONFLICT_ROUTE;
        }
        else
        {
            retType = EN_CONFLICT_SOURCE;
        }

        //Assign sub to main
        stPlayerState.v_PIPSource[MAPI_MAIN_WINDOW] = stPlayerState.v_PIPSource[MAPI_SUB_WINDOW];
        if (stPlayerState.v_PIPSource[MAPI_MAIN_WINDOW] == MAPI_INPUT_SOURCE_DTV2)
        {
            stPlayerState.v_PIPRoute[MAPI_MAIN_WINDOW] = stPlayerState.v_PIPRoute[MAPI_SUB_WINDOW];
            stPlayerState.v_PIPSource[MAPI_MAIN_WINDOW] = MAPI_INPUT_SOURCE_DTV;
        }

        //Clean sub
        stPlayerState.v_PIPSource[MAPI_SUB_WINDOW] = MAPI_INPUT_SOURCE_NONE;
        stPlayerState.v_PIPRoute[MAPI_SUB_WINDOW] = UNUSED_DTV_ROUTE;
    }
    //Foreground or background change, with background conflict
    else if (((bBGChange == FALSE) && (bBBConflict == FALSE) && (bDisableBG == TRUE)) ||
        ((bBGChange == TRUE) && (bBBConflict == TRUE)))
    {
        for (unsigned int i = 0; i < stPlayerState.v_PVRRoute.size(); i++)
        {
            ST_PLAYER_STATE tmpPlayerState = stPlayerState;
            tmpPlayerState.v_PVRRoute.erase(tmpPlayerState.v_PVRRoute.begin()+i);
            if (FALSE == MSrv_PlayerConfigTable_Tuner::GetInstance()->CheckConflict(tmpPlayerState))
            {
                u8DisableRouteIndex = stPlayerState.v_PVRRoute[i];
                stPlayerState = tmpPlayerState;

                //When background switch to foreground
                if (CheckDtvRouteInForeground(stPlayerState, u8DisableRouteIndex) == TRUE)
                {
                    return EN_CONFLICT_NONE;
                }
                //When foreground change, then disable background
                else
                {
                    if ((m_stPlayerState.v_PIPSource[MAPI_MAIN_WINDOW] != stPlayerState.v_PIPSource[MAPI_MAIN_WINDOW]) ||
                        (m_stPlayerState.v_PIPRoute[MAPI_MAIN_WINDOW] != stPlayerState.v_PIPRoute[MAPI_MAIN_WINDOW]))
                    {
                        eChangeWin = MAPI_MAIN_WINDOW;
                    }
                    else if ((m_stPlayerState.v_PIPSource[MAPI_SUB_WINDOW] != stPlayerState.v_PIPSource[MAPI_SUB_WINDOW]) ||
                        (m_stPlayerState.v_PIPRoute[MAPI_SUB_WINDOW] != stPlayerState.v_PIPRoute[MAPI_SUB_WINDOW]))
                    {
                        eChangeWin = MAPI_SUB_WINDOW;
                    }
                    retType = EN_CONFLICT_BACKGROUND;
                }
                break;
            }
        }

        if (u8DisableRouteIndex == UNUSED_DTV_ROUTE)
        {
            //Can't handle case
            ASSERT(0);
        }
    }
    //Background change, with foreground conflict
    else if ((bBGChange == TRUE) && (bBBConflict == FALSE) && (bTunerConflict == TRUE))
    {
        BOOL bConflict = TRUE;
        retType = EN_CONFLICT_ROUTE;

        ST_PLAYER_STATE tmpPlayerState;

        //Change main source and route, then check conflict
        tmpPlayerState = stPlayerState;
        tmpPlayerState.v_PIPSource[MAPI_MAIN_WINDOW] = MAPI_INPUT_SOURCE_DTV;
        tmpPlayerState.v_PIPRoute[MAPI_MAIN_WINDOW] = tmpPlayerState.v_PVRRoute.back();
        tmpPlayerState.v_PVRRoute.pop_back();
        if (FALSE == MSrv_PlayerConfigTable_Tuner::GetInstance()->CheckConflict(tmpPlayerState))
        {
            bConflict = FALSE;
            stPlayerState = tmpPlayerState;
        }

        //Change sub source and route, then check conflict
        if (bConflict == TRUE)
        {
            tmpPlayerState = stPlayerState;
            tmpPlayerState.v_PIPSource[MAPI_SUB_WINDOW] = MAPI_INPUT_SOURCE_DTV2;
            tmpPlayerState.v_PIPRoute[MAPI_SUB_WINDOW] = tmpPlayerState.v_PVRRoute.back();
            tmpPlayerState.v_PVRRoute.pop_back();
            if (FALSE == MSrv_PlayerConfigTable_Tuner::GetInstance()->CheckConflict(tmpPlayerState))
            {
                stPlayerState = tmpPlayerState;
            }
        }
    }
    else if (bFGRecordConflict == TRUE)
    {
        retType = EN_CONFLICT_FGRECORD;
    }
    else
    {
        ASSERT(0);
    }

    /*
    PC_WAR("===Proposal case===");
    PC_MSG("[Main Source] from[%d] to [%d]", m_stPlayerState.v_PIPSource[MAPI_MAIN_WINDOW], stPlayerState.v_PIPSource[MAPI_MAIN_WINDOW]);
    PC_MSG("[Main Route]  from[%d] to [%d]", m_stPlayerState.v_PIPRoute[MAPI_MAIN_WINDOW], stPlayerState.v_PIPRoute[MAPI_MAIN_WINDOW]);
    PC_MSG("[Sub Source] from [%d] to [%d]", m_stPlayerState.v_PIPSource[MAPI_SUB_WINDOW], stPlayerState.v_PIPSource[MAPI_SUB_WINDOW]);
    PC_MSG("[Sub Route] from[%d] to [%d]", m_stPlayerState.v_PIPRoute[MAPI_SUB_WINDOW], stPlayerState.v_PIPRoute[MAPI_SUB_WINDOW]);
    printf("[PVR Route] from [");
    for (unsigned int i = 0; i < m_stPlayerState.v_PVRRoute.size(); i++)
    {
        printf("%d ", m_stPlayerState.v_PVRRoute[i]);
    }
    printf("] to [");
    for (unsigned int i = 0; i < stPlayerState.v_PVRRoute.size(); i++)
    {
        printf("%d ", stPlayerState.v_PVRRoute[i]);
    }
    printf("]\n");
    */

    if (eChangeWin == MAPI_MAIN_WINDOW)
    {
        m_stConflictMsg.strConflictMsg = "\nMain window want change to ";
    }
    else if (eChangeWin == MAPI_SUB_WINDOW)
    {
        m_stConflictMsg.strConflictMsg = "\nSub window want change to ";
    }

    if (retType == EN_CONFLICT_FGRECORD)
    {
        switch (stPlayerState.v_PIPSource[eChangeWin])
        {
            case MAPI_INPUT_SOURCE_ATV:
                m_stConflictMsg.strConflictMsg += "ATV";
                break;
            case MAPI_INPUT_SOURCE_DTV:
            case MAPI_INPUT_SOURCE_DTV2:
                switch (SystemInfo::GetInstance()->GetDTVRouteEnumByRouteIndex(stPlayerState.v_PIPRoute[eChangeWin]))
                {
                    case DTV_TYPE_DVBT:
                    case DTV_TYPE_DVBT2:
                        m_stConflictMsg.strConflictMsg += "DVBT";
                        break;
                    case DTV_TYPE_DVBC:
                        m_stConflictMsg.strConflictMsg += "DVBC";
                        break;
                    case DTV_TYPE_DVBS:
                    case DTV_TYPE_DVBS2:
                        m_stConflictMsg.strConflictMsg += "DVBS";
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        m_stConflictMsg.strConflictMsg += " be conflict with ";
        if (eChangeWin == MAPI_MAIN_WINDOW)
        {
            m_stConflictMsg.strConflictMsg += "main window record.\n\n";
        }
        else if (eChangeWin == MAPI_SUB_WINDOW)
        {
            m_stConflictMsg.strConflictMsg += "sub window record.\n\n";
        }
        m_stConflictMsg.strConflictMsg += "Then record will be stop\n\n";
    }
    else if (retType == EN_CONFLICT_BACKGROUND)
    {
        switch (stPlayerState.v_PIPSource[eChangeWin])
        {
            case MAPI_INPUT_SOURCE_ATV:
                m_stConflictMsg.strConflictMsg += "ATV";
                break;
            case MAPI_INPUT_SOURCE_DTV:
            case MAPI_INPUT_SOURCE_DTV2:
                switch (SystemInfo::GetInstance()->GetDTVRouteEnumByRouteIndex(stPlayerState.v_PIPRoute[eChangeWin]))
                {
                    case DTV_TYPE_DVBT:
                    case DTV_TYPE_DVBT2:
                        m_stConflictMsg.strConflictMsg += "DVBT";
                        break;
                    case DTV_TYPE_DVBC:
                        m_stConflictMsg.strConflictMsg += "DVBC";
                        break;
                    case DTV_TYPE_DVBS:
                    case DTV_TYPE_DVBS2:
                        m_stConflictMsg.strConflictMsg += "DVBS";
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        m_stConflictMsg.strConflictMsg += " be conflict with background record.\n\n";
        m_stConflictMsg.strConflictMsg += "Then background record ";
        switch (SystemInfo::GetInstance()->GetDTVRouteEnumByRouteIndex(u8DisableRouteIndex))
        {
            case DTV_TYPE_DVBT:
            case DTV_TYPE_DVBT2:
                m_stConflictMsg.strConflictMsg += "DVBT";
                break;
            case DTV_TYPE_DVBC:
                m_stConflictMsg.strConflictMsg += "DVBC";
                break;
            case DTV_TYPE_DVBS:
            case DTV_TYPE_DVBS2:
                m_stConflictMsg.strConflictMsg += "DVBS";
                break;
            default:
                break;
        }
        m_stConflictMsg.strConflictMsg += " be stop.\n\n";
    }
    else
    {
        if (stPlayerState.v_PIPSource[MAPI_MAIN_WINDOW] == MAPI_INPUT_SOURCE_DTV)
        {
            switch (SystemInfo::GetInstance()->GetDTVRouteEnumByRouteIndex(stPlayerState.v_PIPRoute[MAPI_MAIN_WINDOW]))
            {
                case DTV_TYPE_DVBT:
                case DTV_TYPE_DVBT2:
                    m_stConflictMsg.strConflictMsg += "DVBT";
                    break;
                case DTV_TYPE_DVBC:
                    m_stConflictMsg.strConflictMsg += "DVBC";
                    break;
                case DTV_TYPE_DVBS:
                case DTV_TYPE_DVBS2:
                    m_stConflictMsg.strConflictMsg += "DVBS";
                    break;
                default:
                    break;
            }
        }
        else
        {
            switch (stPlayerState.v_PIPSource[MAPI_MAIN_WINDOW])
            {
                case MAPI_INPUT_SOURCE_ATV:
                    m_stConflictMsg.strConflictMsg += "ATV";
                    break;
                case MAPI_INPUT_SOURCE_SCART:
                    m_stConflictMsg.strConflictMsg += "SCART1";
                    break;
                case MAPI_INPUT_SOURCE_YPBPR:
                    m_stConflictMsg.strConflictMsg += "COMP1";
                    break;
                case MAPI_INPUT_SOURCE_VGA:
                    m_stConflictMsg.strConflictMsg += "PC-RGB";
                    break;
                case MAPI_INPUT_SOURCE_VGA2:
                    m_stConflictMsg.strConflictMsg += "PC-RGB2";
                    break;
                case MAPI_INPUT_SOURCE_VGA3:
                    m_stConflictMsg.strConflictMsg += "PC-RGB3";
                    break;
                case MAPI_INPUT_SOURCE_HDMI:
                    m_stConflictMsg.strConflictMsg += "HDMI1";
                    break;
                case MAPI_INPUT_SOURCE_HDMI2:
                    m_stConflictMsg.strConflictMsg += "HDMI2";
                    break;
                case MAPI_INPUT_SOURCE_HDMI3:
                    m_stConflictMsg.strConflictMsg += "HDMI3";
                    break;
                case MAPI_INPUT_SOURCE_HDMI4:
                    m_stConflictMsg.strConflictMsg += "HDMI4";
                    break;
                case MAPI_INPUT_SOURCE_CVBS:
                    m_stConflictMsg.strConflictMsg += "AV1";
                    break;
                case MAPI_INPUT_SOURCE_SVIDEO:
                    m_stConflictMsg.strConflictMsg += "S-Video1";
                    break;
                default:
                    break;
            }
        }

        if (eChangeWin == MAPI_MAIN_WINDOW)
        {
            m_stConflictMsg.strConflictMsg += " be conflict with sub window source.\n\n";
            m_stConflictMsg.strConflictMsg += "Then sub window be close.\n\n";
        }
        else if (eChangeWin == MAPI_SUB_WINDOW)
        {
            m_stConflictMsg.strConflictMsg += " be conflict with main window source.\n\n";
            m_stConflictMsg.strConflictMsg += "Then main window source be replace.\n\n";
        }
    }

    m_stConflictMsg.strConflictMsg += "Is this change be you want?\n";

    return retType;
}
#endif

BOOL MSrv_Control_common::MSrv_PlayerControl::SwitchInputSource(const ST_PLAYER_STATE& stPlayerState)
{
    ST_PLAYER_STATE tmpPlayerState = stPlayerState;

#if(PIP_ENABLE == 1)
    if (CheckConflict(tmpPlayerState) != EN_CONFLICT_NONE)
    {
        PC_ERR("SwichInputSource check conflict error!...");
    }
#endif

#if(MSTAR_TVOS == 0)
    if (tmpPlayerState.v_PIPSource[MAPI_SUB_WINDOW] != MAPI_INPUT_SOURCE_DTV2)
    {
        //Patch for sub ATV source get program info, channel manager need dtv player to get dtv program count
        // Patch for all Traveling mode, when video mirror,all traveling mode use full sub xc but player not realy run
        if(tmpPlayerState.v_PIPSource[MAPI_SUB_WINDOW] == MAPI_INPUT_SOURCE_ATV)
        {
            tmpPlayerState.v_PIPRoute[MAPI_SUB_WINDOW] = tmpPlayerState.v_PIPRoute[MAPI_MAIN_WINDOW];
        }
        else
        {
            tmpPlayerState.v_PIPRoute[MAPI_SUB_WINDOW] = UNUSED_DTV_ROUTE;
        }
    }
#endif

#if (PVR_ENABLE == 1) && (ATSC_SYSTEM_ENABLE == 0)
    //Disable conflict background pvr
    for (unsigned int i = 0; i < m_stPlayerState.v_PVRRoute.size(); i++)
    {
        BOOL bDisable = TRUE;
        for (unsigned int j = 0; j < tmpPlayerState.v_PVRRoute.size(); j++)
        {
            if (tmpPlayerState.v_PVRRoute[j] == m_stPlayerState.v_PVRRoute[i])
            {
                bDisable = FALSE;
                break;
            }
        }

        if (bDisable == TRUE)
        {
            //Not in foreground, then disable background mode
            if (CheckDtvRouteInForeground(tmpPlayerState, m_stPlayerState.v_PVRRoute[i]) == FALSE)
            {
                MSrv_DTV_Player_DVB *dtvPlayer = dynamic_cast<MSrv_DTV_Player_DVB *>(m_pMSrvPlayerList[E_MSRV_DTV_PLAYER_0 + m_stPlayerState.v_PVRRoute[i]]);
                ASSERT(dtvPlayer);

                if (dtvPlayer->IsRecording() == TRUE)
                {
                    PC_MSG("DoStopRecordRoute...[%d]", m_stPlayerState.v_PVRRoute[i]);
                    dtvPlayer->StopRecord();
                }

                if (dtvPlayer->IsBackgroundMode() == TRUE)
                {
                    dtvPlayer->DisableBackgroundMode();
                }
            }
        }
    }
#endif

    for (int windowIndex = MAPI_MAIN_WINDOW; windowIndex < MAPI_MAX_WINDOW; windowIndex++)
    {
        MAPI_INPUT_SOURCE_TYPE oldSource = m_stPlayerState.v_PIPSource[windowIndex];
        MAPI_INPUT_SOURCE_TYPE newSource = tmpPlayerState.v_PIPSource[windowIndex];
        U8 oldRoute = m_stPlayerState.v_PIPRoute[windowIndex];
        U8 newRoute = tmpPlayerState.v_PIPRoute[windowIndex];

        //source or route change
        if ((oldSource != newSource) ||
            (oldRoute != newRoute))
        {
            PC_MSG("Start [%s] window finalize...", windowIndex ? "Sub" : "Main");
            MSrv_Player *pPlayerOld = MSrv_Control::GetInstance()->GetMSrvPlayer(oldSource);
            if (pPlayerOld != NULL)
            {
#if (AUTO_TEST == 1)
                printf("[AutoTest][SourceChange][player finalize START][%u]\n", mapi_time_utility::GetTime0());
#endif
                if (pPlayerOld->isActive())
                {
                    if (!pPlayerOld->Finalize())
                    {
                        ASSERT(0);
                        return FALSE;
                    }
                }
#if (AUTO_TEST == 1)
                printf("[AutoTest][SourceChange][player finalize END][%u]\n", mapi_time_utility::GetTime0());
#endif
            }
#if (VCHIP_ENABLE == 1)
            m_stPlayerState.v_PIPSource[windowIndex] = newSource;
#endif
            PC_MSG("End [%s] window finalize...", windowIndex ? "Sub" : "Main");
#if (ATSC_SYSTEM_ENABLE == 0)
            BOOL bBGModeNeedExit = FALSE;
#endif
#if ((PIP_ENABLE == 1) || (TWIN_TUNER == 1)) && (PVR_ENABLE == 1) && (ATSC_SYSTEM_ENABLE == 0)
            //Check old player is DTV from foreground to background
            if ((oldSource == MAPI_INPUT_SOURCE_DTV) || (oldSource == MAPI_INPUT_SOURCE_DTV2))
            {
                MSrv_DTV_Player_DVB *p = dynamic_cast<MSrv_DTV_Player_DVB *>(pPlayerOld);
                ASSERT(p);
                BOOL bConflict = TRUE;
                if (p->IsBackgroundMode() == TRUE)
                {
                    if (p->IsRecording() == TRUE)
                    {
                        //Push to background PVR vector
                        tmpPlayerState.v_PVRRoute.push_back(oldRoute);
                        bConflict = MSrv_PlayerConfigTable_Tuner::GetInstance()->CheckConflict(tmpPlayerState);
                        if (TRUE == bConflict)
                        {
                            tmpPlayerState.v_PVRRoute.pop_back();
                            p->StopRecord();
                        }
                    }

                    if (TRUE == bConflict)
                    {
                        //p->DisableBackgroundMode();
                        bBGModeNeedExit = TRUE;
                    }
                }
            }
#endif

#if (CVBSOUT_XCTOVE_ENABLE == 0)
#if (VE_ENABLE == 1 ||CVBSOUT_ENABLE==1) && (CONNECTTV_BOX == 0)
            if ((windowIndex == MAPI_MAIN_WINDOW) &&
                (oldSource != MAPI_INPUT_SOURCE_NONE) &&
                (m_pInputSrcTable[MAPI_INPUT_SOURCE_SCART].u32EnablePort || ISSCART_OUT_EN()))
            {
                ScartInfo_t scartInfo = SystemInfo::GetInstance()->GetScartInfo();

                switch (scartInfo.u32SCARTOUT_MODE)
                {
                    case SCART_OUT_TV_NONE:
                        {
                            MS_USER_SYSTEM_SETTING stSysSetting;
                            GetMSrvSystemDatabase()->GetUserSystemSetting(&stSysSetting);

                            // If prev source is SCART, we don't need to finalize anything.
                            if(oldSource == MAPI_INPUT_SOURCE_SCART)
                            {
                                break;
                            }
                            if (stSysSetting.enLastTVInputSourceType == MAPI_INPUT_SOURCE_DTV)
                            {
                                MSrv_DTV_Player *dtvPlayer = dynamic_cast<MSrv_DTV_Player*>(m_pMSrvPlayerList[E_MSRV_DTV_PLAYER_0 + oldRoute]);
                                ASSERT(dtvPlayer);
                                //TV_Mode: DTV->DTV different route
                                //TV_Mode: DTV->DTV same route, to be refine, reinit problem.
                                if ((newSource == MAPI_INPUT_SOURCE_DTV) ||
                                //TV_Mode: DTV->ATV
                                    (newSource == MAPI_INPUT_SOURCE_ATV))
                                {
                                    dtvPlayer->FinalizeDtvDemodTuner();
                                }
                                //Scart port I/O same port, input first
                                else if (newSource == MAPI_INPUT_SOURCE_SCART)
                                {
#if (VE_ENABLE == 1)
                                    dtvPlayer->FinalizeDtvDemodTuner();
#endif
                                }
                            }
#if (STB_ENABLE == 0)
                            else if (stSysSetting.enLastTVInputSourceType == MAPI_INPUT_SOURCE_ATV)
                            {
                                MSrv_ATV_Player *atvPlayer = dynamic_cast<MSrv_ATV_Player*>(m_pMSrvPlayerList[E_MSRV_ATV_PLAYER]);
                                ASSERT(atvPlayer);

                                //TV_Mode: ATV->DTV
                                if ((newSource == MAPI_INPUT_SOURCE_DTV) ||
                                //TV_Mode: ATV->ATV to be refine, reinit problem.
                                    (newSource == MAPI_INPUT_SOURCE_ATV))
                                {
                                    atvPlayer->FinalizeAtvDemodTuner();
                                }
                                //Scart port I/O same port, input first
                                else if (newSource == MAPI_INPUT_SOURCE_SCART)
                                {
                                    atvPlayer->FinalizeAtvDemodTuner();
                                }
                            }
#endif
                        }
                        break;
                    case SCART_OUT_MONITOR_NONE:
                        if (oldSource == MAPI_INPUT_SOURCE_DTV)
                        {
                            MSrv_DTV_Player *dtvPlayer = dynamic_cast<MSrv_DTV_Player*>(m_pMSrvPlayerList[E_MSRV_DTV_PLAYER_0 + oldRoute]);
                            ASSERT(dtvPlayer);
                            dtvPlayer->FinalizeDtvDemodTuner();
#if (ATSC_SYSTEM_ENABLE == 0)
                            bBGModeNeedExit = FALSE;
#endif
                        }
#if (STB_ENABLE == 0)
                        else if (oldSource == MAPI_INPUT_SOURCE_ATV)
                        {
                            MSrv_ATV_Player *atvPlayer = dynamic_cast<MSrv_ATV_Player*>(m_pMSrvPlayerList[E_MSRV_ATV_PLAYER]);
                            ASSERT(atvPlayer);
                            atvPlayer->FinalizeAtvDemodTuner();
                        }
#endif
                        break;
                    default:
                        //unsupported case, to do.
                        ASSERT(0);
                        break;
                }
            }
#endif
#endif

            if ((windowIndex == MAPI_MAIN_WINDOW) &&
                ((oldSource != newSource) || (oldRoute != newRoute)))
            {
                if(oldSource == MAPI_INPUT_SOURCE_ATV)
                {
                    MSrv_ATV_Player *atvPlayer = (MSrv_ATV_Player*)m_pMSrvPlayerList[E_MSRV_ATV_PLAYER];
                    atvPlayer->FinalizeAtvDemodTuner();
                }
            }
#if (ATSC_SYSTEM_ENABLE == 0)
            if (bBGModeNeedExit == TRUE)
            {
                MSrv_DTV_Player_DVB *p = dynamic_cast<MSrv_DTV_Player_DVB *>(pPlayerOld);
                ASSERT(p);
                p->DisableBackgroundMode();
            }
#endif

#if (DVB_ENABLE == 1)
            //route change
            if (oldRoute != newRoute)
            {
                m_stPlayerState.v_PIPRoute[windowIndex] = newRoute;
            }
#endif
            if (((MSrv_Control::GetInstance()->IsFocusOnSubSource() == FALSE) && (windowIndex == MAPI_MAIN_WINDOW)) ||
                ((MSrv_Control::GetInstance()->IsFocusOnSubSource() == TRUE) && (windowIndex == MAPI_SUB_WINDOW)))
            {
                MSrv_Control::GetInstance()->m_enCurrentFocusSource = newSource;
            }

#if (VCHIP_ENABLE == 1)
            if (IsSupportTheDTVSystemType(ATSC_ENABLE))
            {
                if (newSource == MAPI_INPUT_SOURCE_DTV)
                {
                    MW_VCHIP::GetInstance()->SetInputSource(EN_VCHIP_INPUT_DIGITAL);
                }
                else if(newSource == MAPI_INPUT_SOURCE_ATV
                        || (MAPI_INPUT_SOURCE_CVBS   <= newSource && newSource <= MAPI_INPUT_SOURCE_CVBS8)
                        || (MAPI_INPUT_SOURCE_SVIDEO <= newSource && newSource <= MAPI_INPUT_SOURCE_SVIDEO4)
                        || (MAPI_INPUT_SOURCE_YPBPR  <= newSource && newSource <= MAPI_INPUT_SOURCE_YPBPR3))
                {
                    MW_VCHIP::GetInstance()->SetInputSource(EN_VCHIP_INPUT_ANALOG);
                }
            }
#endif

            if (newSource != MAPI_INPUT_SOURCE_NONE)
            {
                PC_MSG("Start [%s] window init...", windowIndex ? "Sub" : "Main");
                MSrv_Player *pPlayerNew = MSrv_Control::GetInstance()->GetMSrvPlayer(newSource);
                if (pPlayerNew == NULL)
                {
                    //Audio should  unmute when exit accidently
                    if (windowIndex == MAPI_MAIN_WINDOW)
                    {
                        MSrv_Control::GetInstance()->GetMSrvSSSound()->SetMuteStatus(MUTE_SOURCESWITCH, MAPI_FALSE, MSRV_AUDIO_PROCESSOR_MAIN);
                    }
                    else
                    {
                        MSrv_Control::GetInstance()->GetMSrvSSSound()->SetMuteStatus(MUTE_SOURCESWITCH, MAPI_FALSE, MSRV_AUDIO_PROCESSOR_SUB);
                    }

                    ASSERT(0);
                    return FALSE;
                }
                else
                {
#if (AUTO_TEST == 1)
                    printf("[AutoTest][SourceChange][player init START][%u]\n", mapi_time_utility::GetTime0());
#endif
                    MAPI_INPUT_SOURCE_TYPE eAudioInputSrc = newSource;
#if (STB_ENABLE == 0) && (MSTAR_TVOS == 1)
                    // Customer request VGA audio out when input source is HDMI
                    // enHdmi[1~4]AudioSource was initialized in LoadSoundSetting(from db)
                    // also can be restored in RestoreDefaultSoundSetting and it's value is E_AUDIO_SOURCE_ORIGINAL
                    // if UI set the value of enHDMI[1~4]AudioSource to E_AUDIO_SOURCE_VGA
                    // system will output VGA audio when input is HDMI
                    EN_HDMI_AUDIO_SOURCE enRequireAudioSrc = E_AUDIO_SOURCE_ORIGINAL;
                    MS_USER_SOUND_SETTING stSoundSetting;
                    GetMSrvSystemDatabase()->GetSoundSetting(&stSoundSetting);
                    switch(newSource)
                    {
                        case MAPI_INPUT_SOURCE_HDMI:
                            enRequireAudioSrc = stSoundSetting.enHdmi1AudioSource;
                            break;
                        case MAPI_INPUT_SOURCE_HDMI2:
                            enRequireAudioSrc = stSoundSetting.enHdmi2AudioSource;
                            break;
                        case MAPI_INPUT_SOURCE_HDMI3:
                            enRequireAudioSrc = stSoundSetting.enHdmi3AudioSource;
                            break;
                        case MAPI_INPUT_SOURCE_HDMI4:
                            enRequireAudioSrc = stSoundSetting.enHdmi4AudioSource;
                            break;
                        default:
                            break;
                    }
                    if(enRequireAudioSrc == E_AUDIO_SOURCE_VGA)
                    {
                        eAudioInputSrc = MAPI_INPUT_SOURCE_VGA;
                    }
#endif
#if (ATSC_SYSTEM_ENABLE == 1) && (RVU_ENABLE == 1)
                    if(newSource ==MAPI_INPUT_SOURCE_RVU)
                    {
                        eAudioInputSrc = MAPI_INPUT_SOURCE_STORAGE;
                    }
#endif
                    //Set audio source type for support single dtv and multi dtv
                    if ((newSource == MAPI_INPUT_SOURCE_DTV) && (windowIndex == MAPI_MAIN_WINDOW))
                    {
                        if (MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_SUB_WINDOW) != MAPI_INPUT_SOURCE_DTV2)
                        {
                            eAudioInputSrc = MAPI_INPUT_SOURCE_DTV;
                        }
                        else
                        {
                            eAudioInputSrc = MAPI_INPUT_SOURCE_DTV2;
                        }
                    }
                    else if ((newSource == MAPI_INPUT_SOURCE_DTV2) && (windowIndex == MAPI_SUB_WINDOW))
                    {
#if (PREVIEW_MODE_ENABLE == 1)
                        if(FALSE == MSrv_Control::GetInstance()->IsPreviewModeRunning())
#endif
                        {
                            if (MSrv_PlayerControl::GetInstance()->GetPIPSourceType(MAPI_MAIN_WINDOW) != MAPI_INPUT_SOURCE_DTV)
                            {
                                eAudioInputSrc = MAPI_INPUT_SOURCE_DTV;
                            }
                            else
                            {
                                eAudioInputSrc = MAPI_INPUT_SOURCE_DTV2;
                            }
                        }
                    }

                    //To set Audio, Mute protect in InputSource_ChangeAudioSource kernel
#if (PIP_ENABLE == 1)
                    if (windowIndex == MAPI_MAIN_WINDOW)
                    {
                        GetMSrvSSSound()->SetAudioSource(eAudioInputSrc, MSRV_AUDIO_PROCESSOR_MAIN);
                    }
                    else
                    {
#if (PREVIEW_MODE_ENABLE == 1)
                        if(FALSE == MSrv_Control::GetInstance()->IsPreviewModeRunning())
#endif
                        {
                            GetMSrvSSSound()->SetAudioSource(eAudioInputSrc, MSRV_AUDIO_PROCESSOR_SUB);
                        }
                    }
#else
                    mapi_interface::Get_mapi_audio()->InputSource_ChangeAudioSource(eAudioInputSrc);
#endif
                    if (!pPlayerNew->Init(newSource, (MAPI_SCALER_WIN)windowIndex))
                    {
                        ASSERT(0);
                        return FALSE;
                    }
                    else
                    {
#if (AUTO_TEST == 1)
                        printf("[AutoTest][SourceChange][player init END][%u]\n", mapi_time_utility::GetTime0());
#endif
                        //Check new player is DTV from background to foreground
                        if ((newSource == MAPI_INPUT_SOURCE_DTV) || (newSource == MAPI_INPUT_SOURCE_DTV2))
                        {
                            for (unsigned int i = 0; i < tmpPlayerState.v_PVRRoute.size(); i++)
                            {
                                if (tmpPlayerState.v_PVRRoute[i] == newRoute)
                                {
                                    //Erase from background PVR vector
                                    tmpPlayerState.v_PVRRoute.erase(tmpPlayerState.v_PVRRoute.begin()+i);
                                    break;
                                }
                            }
                        }
                    }
                }
                PC_MSG("End [%s] window init...", windowIndex ? "Sub" : "Main");
            }

#if (CVBSOUT_XCTOVE_ENABLE == 0)
#if (VE_ENABLE == 1 ||CVBSOUT_ENABLE==1) && (CONNECTTV_BOX == 0)

            ScartInfo_t scartInfo = SystemInfo::GetInstance()->GetScartInfo();

            if ((windowIndex == MAPI_MAIN_WINDOW) &&
                (scartInfo.u32SCARTOUT_MODE == SCART_OUT_TV_NONE) &&
                (m_pInputSrcTable[MAPI_INPUT_SOURCE_SCART].u32EnablePort || ISSCART_OUT_EN()) )
            {
                MS_USER_SYSTEM_SETTING stSysSetting;
                GetMSrvSystemDatabase()->GetUserSystemSetting(&stSysSetting);
                if (stSysSetting.enLastTVInputSourceType == MAPI_INPUT_SOURCE_DTV)
                {
                    MSrv_DTV_Player *dtvPlayer = dynamic_cast<MSrv_DTV_Player*>(m_pMSrvPlayerList[E_MSRV_DTV_PLAYER_0 + oldRoute]);
                    ASSERT(dtvPlayer);

                    if ((oldSource == MAPI_INPUT_SOURCE_NONE) && (newSource != MAPI_INPUT_SOURCE_SCART))
                    {
                        dtvPlayer->InitDTVForOtherSource();
                    }
                    //Switching from SCART to any non-TV source, we need to initialize last tv source
                    else if ((oldSource == MAPI_INPUT_SOURCE_SCART) && (newSource != MAPI_INPUT_SOURCE_ATV) && (newSource != MAPI_INPUT_SOURCE_DTV))
                    {
#if (VE_ENABLE == 1)
                        dtvPlayer->InitDTVForOtherSource();
#endif
                    }
                }
#if (STB_ENABLE == 0)
                else if (stSysSetting.enLastTVInputSourceType == MAPI_INPUT_SOURCE_ATV)
                {
                    MSrv_ATV_Player *atvPlayer = dynamic_cast<MSrv_ATV_Player*>(m_pMSrvPlayerList[E_MSRV_ATV_PLAYER]);
                    ASSERT(atvPlayer);

                    if ((oldSource == MAPI_INPUT_SOURCE_NONE) && (newSource != MAPI_INPUT_SOURCE_SCART))
                    {
                        atvPlayer->InitATVForOtherSource();
                    }
                    else if ((oldSource == MAPI_INPUT_SOURCE_SCART) && (newSource != MAPI_INPUT_SOURCE_ATV) && (newSource != MAPI_INPUT_SOURCE_DTV))
                    {
                        atvPlayer->InitATVForOtherSource();
                    }
                }
#endif
            }
#endif
#endif

            MSrv_Control::GetMSrvPicture()->ResetPQSrcType((MAPI_SCALER_WIN)windowIndex);
        }
    }

    SetPlayerState(tmpPlayerState);

    return TRUE;
}

BOOL MSrv_Control_common::MSrv_PlayerControl::SetRecordServiceByRoute(const ST_TRIPLE_ID &stService, const U8 u8RouteIndex, BOOL bForceSet)
{
#if (PIP_ENABLE == 1) && (PVR_ENABLE == 1) && (ATSC_SYSTEM_ENABLE == 0)
    if ((u8RouteIndex >= MAXROUTECOUNT) || (u8RouteIndex < 0))
    {
        ASSERT(0);
    }

    MSrv_DTV_Player_DVB *dtvPlayer = dynamic_cast<MSrv_DTV_Player_DVB *>(m_pMSrvPlayerList[E_MSRV_DTV_PLAYER_0 + u8RouteIndex]);
    if (dtvPlayer == NULL)
    {
        ASSERT(0);
    }

    //Check direct set background route conflict
    ST_PLAYER_STATE stPlayerState;
    MSrv_PlayerControl::GetInstance()->GetPlayerState(stPlayerState);

    if (CheckDtvRouteInForeground(stPlayerState, u8RouteIndex) == FALSE)
    {
        stPlayerState.v_PVRRoute.push_back(u8RouteIndex);
    }

    if (MSrv_PlayerControl::GetInstance()->CheckConflict(stPlayerState) != EN_CONFLICT_NONE)
    {
        //Background conflict with foreground
        if (bForceSet == FALSE)
        {
            //Conflict notify UI post event
            //MSrv_Control::GetInstance()->PostEvent(NULL, EV_INPUT_SOURCE_CONFLICT, 0, 0);
            return FALSE;
        }
        else
        {
            //Set main or sub for foreground
            //Need change to foreground or ready in background
            if (CheckDtvRouteInForeground(stPlayerState, u8RouteIndex) == TRUE)
            {
                MSrv_PlayerControl::GetInstance()->SwitchInputSource(stPlayerState);

                // Call ProgramSel to change channel
                ST_DTV_PROGRAM_INFO stDtvProgInfo;
                if (FALSE == dtvPlayer->GetProgramInfoByTripleID(stService.u16OnId, stService.u16TsId, stService.u16SrvId, stDtvProgInfo))
                {
                    return FALSE;
                }
                MSrv_Control_common::GetMSrvChannelManager()->ProgramSel((U32)stDtvProgInfo.m_u16Number, stDtvProgInfo.m_u8ServiceType,0,TRUE);

                return TRUE;
            }
        }
    }

    //Set Background route
    if (dtvPlayer->IsBackgroundMode() == TRUE)
    {
        if (dtvPlayer->DisableBackgroundMode() == FALSE)
        {
            ASSERT(0);
        }
    }

    if (dtvPlayer->EnableBackgroundMode() == FALSE)
    {
        ASSERT(0);
    }
    else
    {
        if (dtvPlayer->IsBackgroundMode() == TRUE)
        {
            dtvPlayer->SetRecordService(stService);
        }
        else
        {
            // Call ProgramSel to change channel
            ST_DTV_PROGRAM_INFO stDtvProgInfo;
            if (FALSE == dtvPlayer->GetProgramInfoByTripleID(stService.u16OnId, stService.u16TsId, stService.u16SrvId, stDtvProgInfo))
            {
                return FALSE;
            }
            dtvPlayer->ProgramSel((U32)stDtvProgInfo.m_u16Number, stDtvProgInfo.m_u8ServiceType, TRUE, FALSE, FALSE);
        }
        SetPlayerState(stPlayerState);
        return TRUE;
    }
    return FALSE;
#else
    return FALSE;
#endif
}

U8 MSrv_Control_common::MSrv_PlayerControl::StartRecordRoute(U8 u8DtvRouteIndex, U16 u16CachedPinCode)
{
    PC_MSG("StartRecordRoute...[%d]", u8DtvRouteIndex);
#if (PVR_ENABLE == 1) && (ATSC_SYSTEM_ENABLE == 0)
    BOOL bCanStart = FALSE;

    for (unsigned int i = 0; i < m_stPlayerState.v_PVRRoute.size(); i++)
    {
        if (m_stPlayerState.v_PVRRoute[i] == u8DtvRouteIndex)
        {
            bCanStart = TRUE;
            break;
        }
    }

    if ((m_stPlayerState.v_PIPSource[MAPI_MAIN_WINDOW] == MAPI_INPUT_SOURCE_DTV) &&
        (m_stPlayerState.v_PIPRoute[MAPI_MAIN_WINDOW] == u8DtvRouteIndex))
    {
        bCanStart = TRUE;
    }

    if ((m_stPlayerState.v_PIPSource[MAPI_SUB_WINDOW] == MAPI_INPUT_SOURCE_DTV2) &&
        (m_stPlayerState.v_PIPRoute[MAPI_SUB_WINDOW] == u8DtvRouteIndex))
    {
        bCanStart = TRUE;
    }

    if (bCanStart == TRUE)
    {
        PC_MSG("DoStartRecordRoute...[%d]", u8DtvRouteIndex);
        MSrv_DTV_Player_DVB* pDvbPlayer = dynamic_cast<MSrv_DTV_Player_DVB*>(MSrv_Control::GetInstance()->GetMSrvDtvByIndex(u8DtvRouteIndex));
        ASSERT(pDvbPlayer);
        EN_PVR_STATUS enPVRStatus = pDvbPlayer->StartRecord(u16CachedPinCode);
        UpdateRecordStatusNotify();
        return enPVRStatus;
    }
    else
    {
        return E_PVR_ERROR;
    }
#else
    return FALSE;
#endif
}

BOOL MSrv_Control_common::MSrv_PlayerControl::StopRecordRoute(U8 u8DtvRouteIndex)
{
    PC_MSG("StopRecordRoute...[%d]", u8DtvRouteIndex);
#if (PVR_ENABLE == 1) && (ATSC_SYSTEM_ENABLE == 0)
    BOOL bCanStop = FALSE;
    U8 removeIndex = UNUSED_DTV_ROUTE;

    //Check route is background can be stop
    for (unsigned int i = 0; i < m_stPlayerState.v_PVRRoute.size(); i++)
    {
        if (m_stPlayerState.v_PVRRoute[i] == u8DtvRouteIndex)
        {
            bCanStop = TRUE;
            removeIndex = i;
            break;
        }
    }

    //Check route is foreground can be stop
    if ((m_stPlayerState.v_PIPSource[MAPI_MAIN_WINDOW] == MAPI_INPUT_SOURCE_DTV) &&
        (m_stPlayerState.v_PIPRoute[MAPI_MAIN_WINDOW] == u8DtvRouteIndex))
    {
        bCanStop = TRUE;
    }
    if ((m_stPlayerState.v_PIPSource[MAPI_SUB_WINDOW] == MAPI_INPUT_SOURCE_DTV2) &&
        (m_stPlayerState.v_PIPRoute[MAPI_SUB_WINDOW] == u8DtvRouteIndex))
    {
        bCanStop = TRUE;
    }

    if (bCanStop == TRUE)
    {
        MSrv_DTV_Player_DVB* pDvbPlayer = dynamic_cast<MSrv_DTV_Player_DVB*>(MSrv_Control::GetInstance()->GetMSrvDtvByIndex(u8DtvRouteIndex));
        if (pDvbPlayer->IsRecording() == TRUE)
        {
            PC_MSG("DoStopRecordRoute...[%d]", u8DtvRouteIndex);
            pDvbPlayer->StopRecord();
        }
        if (pDvbPlayer->IsBackgroundMode() == TRUE)
        {
            if (pDvbPlayer->DisableBackgroundMode() == TRUE)
            {
                m_stPlayerState.v_PVRRoute.erase(m_stPlayerState.v_PVRRoute.begin()+removeIndex);
                UpdateRecordStatusNotify();
                return TRUE;
            }
            else
            {
                return FALSE;
            }
        }
        else
        {
            return TRUE;
        }
    }
    else
    {
        return FALSE;
    }
#else
    return FALSE;
#endif
}

#if (VE_ENABLE == 1||CVBSOUT_ENABLE==1)
BOOL MSrv_Control_common::MSrv_PlayerControl::InitScartOut(MAPI_INPUT_SOURCE_TYPE eOldInputSrc, MAPI_INPUT_SOURCE_TYPE eInputSrc) const
{
    return TRUE;
}

BOOL MSrv_Control_common::MSrv_PlayerControl::FinalizeScartOut(MAPI_INPUT_SOURCE_TYPE eOldInputSrc, MAPI_INPUT_SOURCE_TYPE eInputSrc) const
{
    return TRUE;
}
#endif

MSrv_Player* MSrv_Control_common::MSrv_PlayerControl::GetMSrvPlayer(EN_MSRV_PLAYER_LIST eMSrvPlayer)
{
    if ((eMSrvPlayer >= E_MSRV_DTV_PLAYER_0) && (eMSrvPlayer < E_MSRV_PLAYER_MAX))
    {
        return dynamic_cast<MSrv_Player *>(m_pMSrvPlayerList[eMSrvPlayer]);
    }
    return NULL;
}

BOOL MSrv_Control_common::MSrv_PlayerControl::CreateAllMSrvPlayers()
{
    if(m_pInputSrcTable[MAPI_INPUT_SOURCE_DTV].u32EnablePort)
    {
        MS_USER_SYSTEM_SETTING stGetSystemSetting;
        MSrv_Control_common::GetMSrvSystemDatabase()->GetUserSystemSetting(&stGetSystemSetting);

        MSrv_Control::GetInstance()->m_enCurrentFocusSource = stGetSystemSetting.enInputSourceType;

        m_stPlayerState.v_PIPRoute[MAPI_MAIN_WINDOW] = stGetSystemSetting.DtvRoute;

        for(U8 u8RouteIndex = E_MSRV_DTV_ROUTE_0; u8RouteIndex < E_MSRV_DTV_ROUTE_MAX; ++u8RouteIndex)
        {
            U8 u8PlayerIndex = u8RouteIndex + E_MSRV_DTV_PLAYER_0;
            switch(MSrv_Control_common::GetRouteTVMode(u8RouteIndex))
            {
                case E_ROUTE_NONE:
                    if(u8RouteIndex == E_MSRV_DTV_ROUTE_0)
                    {
                        ASSERT(0);
                        break;
                    }
                    m_pMSrvPlayerList[u8PlayerIndex] = NULL;
                    break;
#if (DVBT_SYSTEM_ENABLE == 1)
                case E_ROUTE_DVBT:
                    m_pMSrvPlayerList[u8PlayerIndex] = new (std::nothrow) MSrv_DTV_Player_DVBT_Customer(u8RouteIndex);
                    ASSERT(m_pMSrvPlayerList[u8PlayerIndex]);
                    m_u8DtvRouteCount++;
                    break;

                case E_ROUTE_DVBT2:
                    m_pMSrvPlayerList[u8PlayerIndex] = new (std::nothrow) MSrv_DTV_Player_DVBT_Customer(u8RouteIndex);
                    ASSERT(m_pMSrvPlayerList[u8PlayerIndex]);
                    m_u8DtvRouteCount++;
                    break;
#endif
#if (DVBC_SYSTEM_ENABLE == 1)
                case E_ROUTE_DVBC:
                    m_pMSrvPlayerList[u8PlayerIndex] = new (std::nothrow) MSrv_DTV_Player_DVBC_Customer(u8RouteIndex);
                    ASSERT(m_pMSrvPlayerList[u8PlayerIndex]);
                    m_u8DtvRouteCount++;
                    break;
#endif
#if (DVBS_SYSTEM_ENABLE == 1)
                case E_ROUTE_DVBS:
                    m_pMSrvPlayerList[u8PlayerIndex] = new (std::nothrow) MSrv_DTV_Player_DVBS_Customer(u8RouteIndex);
                    ASSERT(m_pMSrvPlayerList[u8PlayerIndex]);
                    m_u8DtvRouteCount++;
                    break;

                case E_ROUTE_DVBS2:
                    m_pMSrvPlayerList[u8PlayerIndex] = new (std::nothrow) MSrv_DTV_Player_DVBS_Customer(u8RouteIndex);
                    ASSERT(m_pMSrvPlayerList[u8PlayerIndex]);
                    m_u8DtvRouteCount++;
                    break;
#endif
#if (ISDB_SYSTEM_ENABLE == 1)
                case E_ROUTE_ISDB:
                    m_pMSrvPlayerList[u8PlayerIndex] = new (std::nothrow) MSrv_DTV_Player_ISDB_Customer(u8RouteIndex);
                    ASSERT(m_pMSrvPlayerList[u8PlayerIndex]);
                    m_u8DtvRouteCount++;
                    break;
#endif
#if (DTMB_SYSTEM_ENABLE == 1)
                case E_ROUTE_DTMB:
                    m_pMSrvPlayerList[u8PlayerIndex] = new (std::nothrow) MSrv_DTV_Player_DTMB_Customer(u8RouteIndex);
                    ASSERT(m_pMSrvPlayerList[u8PlayerIndex]);
                    m_u8DtvRouteCount++;
                    break;
#endif
#if (ATSC_SYSTEM_ENABLE == 1)
                case E_ROUTE_ATSC:
                    m_pMSrvPlayerList[u8PlayerIndex] = new (std::nothrow) MSrv_DTV_Player_ATSC;
                    ASSERT(m_pMSrvPlayerList[u8PlayerIndex]);
                    m_u8DtvRouteCount++;
                    break;
#endif
                default:
                    ASSERT(0);
                    break;
            }
        }
    }

#if (STB_ENABLE == 0)
    if(this->m_pInputSrcTable[MAPI_INPUT_SOURCE_ATV].u32EnablePort)
    {
        m_pMSrvPlayerList[E_MSRV_ATV_PLAYER] = new (std::nothrow) MSrv_ATV_Player_Customer;
        ASSERT(m_pMSrvPlayerList[E_MSRV_ATV_PLAYER]);
    }
#endif

#if (STB_ENABLE == 0)
#if (ATSC_SYSTEM_ENABLE == 0)
    if(this->m_pInputSrcTable[MAPI_INPUT_SOURCE_SCART].u32EnablePort)
    {
        if(m_pMSrvPlayerList[E_MSRV_SCART_PLAYER] == NULL)
        {
            m_pMSrvPlayerList[E_MSRV_SCART_PLAYER] = new (std::nothrow) MSrv_SCART_Player_Customer;
            ASSERT(m_pMSrvPlayerList[E_MSRV_SCART_PLAYER]);
        }
    }
#endif
#endif

#if (STB_ENABLE == 0)
    if(this->m_pInputSrcTable[MAPI_INPUT_SOURCE_CVBS].u32EnablePort)
    {
        if(m_pMSrvPlayerList[E_MSRV_AV_PLAYER] == NULL)
        {
            m_pMSrvPlayerList[E_MSRV_AV_PLAYER] = new (std::nothrow) MSrv_AV_Player_Customer;
            ASSERT(m_pMSrvPlayerList[E_MSRV_AV_PLAYER]);
        }
    }
#endif

#if (STB_ENABLE == 0)
    if(this->m_pInputSrcTable[MAPI_INPUT_SOURCE_SVIDEO].u32EnablePort)
    {
        if(m_pMSrvPlayerList[E_MSRV_SV_PLAYER] == NULL)
        {
            m_pMSrvPlayerList[E_MSRV_SV_PLAYER] = new (std::nothrow) MSrv_SV_Player_Customer;
            ASSERT(m_pMSrvPlayerList[E_MSRV_SV_PLAYER]);
        }
    }
#endif

#if (STB_ENABLE == 0)
    if(this->m_pInputSrcTable[MAPI_INPUT_SOURCE_YPBPR].u32EnablePort)
    {
        if(m_pMSrvPlayerList[E_MSRV_COMPONENT_PLAYER] == NULL)
        {
            m_pMSrvPlayerList[E_MSRV_COMPONENT_PLAYER] = new (std::nothrow) MSrv_COMP_Player_Customer;
            ASSERT(m_pMSrvPlayerList[E_MSRV_COMPONENT_PLAYER]);
        }
    }
#endif

#if ((STB_ENABLE == 0) || (ENABLE_HDMI_RX == 1))
    if(this->m_pInputSrcTable[MAPI_INPUT_SOURCE_HDMI].u32EnablePort)
    {
        if(m_pMSrvPlayerList[E_MSRV_HDMI_PLAYER] == NULL)
        {
            m_pMSrvPlayerList[E_MSRV_HDMI_PLAYER] = new (std::nothrow) MSrv_HDMI_Player_Customer;
            ASSERT(m_pMSrvPlayerList[E_MSRV_HDMI_PLAYER]);
        }
    }
#endif

#if (STB_ENABLE == 0)
    if(this->m_pInputSrcTable[MAPI_INPUT_SOURCE_VGA].u32EnablePort)
    {
        if(m_pMSrvPlayerList[E_MSRV_VGA_PLAYER] == NULL)
        {
            m_pMSrvPlayerList[E_MSRV_VGA_PLAYER] = new (std::nothrow) MSrv_PC_Player_Customer;
            ASSERT(m_pMSrvPlayerList[E_MSRV_VGA_PLAYER]);
        }
    }
#endif

#if ((STB_ENABLE == 0) && (RVU_ENABLE == 1))
    if(this->m_pInputSrcTable[MAPI_INPUT_SOURCE_RVU].u32EnablePort)
    {
        if(m_pMSrvPlayerList[E_MSRV_RVU_PLAYER] == NULL)
        {
            m_pMSrvPlayerList[E_MSRV_RVU_PLAYER] = new (std::nothrow) MSrv_RVU_Player_Customer;
            ASSERT(m_pMSrvPlayerList[E_MSRV_RVU_PLAYER]);
        }
    }
#endif

    m_pMSrvPlayerList[E_MSRV_STORAGE_PLAYER] = new (std::nothrow) MSrv_STORAGE_Player;
    ASSERT(m_pMSrvPlayerList[E_MSRV_STORAGE_PLAYER]);
    m_pMSrvPlayerList[E_MSRV_STORAGE_PLAYER_2] = new (std::nothrow) MSrv_STORAGE_Player;
    ASSERT(m_pMSrvPlayerList[E_MSRV_STORAGE_PLAYER_2]);

#if (ATSC_SYSTEM_ENABLE == 1)
    m_pMSrvList[E_MSRV_ADVERT_PLAYER] = new (std::nothrow) MSrv_Advert_Player;
    ASSERT(m_pMSrvList[E_MSRV_ADVERT_PLAYER]);
#endif

    return TRUE;
}

#if (PIP_ENABLE == 1) || (TWIN_TUNER == 1)
vector<MSrv_Control_common::MSrv_PlayerControl::ST_TUNER_TABLE>* MSrv_Control_common::MSrv_PlayerControl::MSrv_PlayerConfigTable::m_pTunerTable = NULL;

MSrv_Control_common::MSrv_PlayerControl::MSrv_PlayerConfigTable::MSrv_PlayerConfigTable()
{
    if (m_pTunerTable == NULL)
    {
        SetTunerModeTable();
    }
}

const vector<MSrv_Control_common::MSrv_PlayerControl::ST_TUNER_TABLE>* MSrv_Control_common::MSrv_PlayerControl::MSrv_PlayerConfigTable::GetTunerModeTable() const
{
    ASSERT(m_pTunerTable != NULL);
    return m_pTunerTable;
}

BOOL MSrv_Control_common::MSrv_PlayerControl::MSrv_PlayerConfigTable::SetTunerModeTable()
{
    if (m_pTunerTable != NULL)
    {
        ASSERT(0);
    }

    m_pTunerTable = new (std::nothrow) vector<ST_TUNER_TABLE>();

    vector < vector<string> > strArray = SystemInfo::GetInstance()->GetTunerModeTable();

    for (unsigned int i = 0; i < strArray.size(); i++)
    {
        ST_TUNER_TABLE stTunerTable;

        stTunerTable.bATVSupport = FALSE;
        stTunerTable.v_stDTVsRoute = vector<EN_DTV_TYPE>();

        for (unsigned int j = 0; j < strArray[i].size(); j++)
        {
            if (strArray[i][j] == "ATV")
            {
                stTunerTable.bATVSupport = TRUE;
            }
            else
            {
                EN_DTV_TYPE enDTVType = SystemInfo::GetInstance()->GetDTVRouteEnumByString(strArray[i][j]);
                if (enDTVType != DTV_TYPE_UNKNOW)
                {
                    stTunerTable.v_stDTVsRoute.push_back(enDTVType);
                }
                else
                {
                    ASSERT(0);
                }
            }
        }
        m_pTunerTable->push_back(stTunerTable);
    }
    return TRUE;
}

MSrv_Control_common::MSrv_PlayerControl::MSrv_PlayerConfigTable_Tuner* MSrv_Control_common::MSrv_PlayerControl::MSrv_PlayerConfigTable_Tuner::m_pInstance = NULL;

MSrv_Control_common::MSrv_PlayerControl::MSrv_PlayerConfigTable_Tuner::MSrv_PlayerConfigTable_Tuner()
{
}

MSrv_Control_common::MSrv_PlayerControl::MSrv_PlayerConfigTable_Tuner* MSrv_Control_common::MSrv_PlayerControl::MSrv_PlayerConfigTable_Tuner::GetInstance()
{
    if (m_pInstance == NULL)
    {
        m_pInstance = new (std::nothrow) MSrv_PlayerConfigTable_Tuner;
    }
    return m_pInstance;
}

BOOL MSrv_Control_common::MSrv_PlayerControl::MSrv_PlayerConfigTable_Tuner::CheckConflict(ST_PLAYER_STATE& stPlayerState) const
{
    if (stPlayerState.v_PIPSource.size() != stPlayerState.v_PIPRoute.size())
    {
        ASSERT(0);
    }

    const vector<ST_TUNER_TABLE>* pTunerTable = GetTunerModeTable();

    vector<BOOL> tunersUsedFlag(pTunerTable->size(), FALSE);

    //Check foreground tuner conflict
    for (unsigned int i = 0; i < stPlayerState.v_PIPSource.size(); i++)
    {
        BOOL bGetTuner = FALSE;
        for (unsigned int j = 0; j < pTunerTable->size(); j++)
        {
            ST_TUNER_TABLE stTunerTable = ((*pTunerTable)[j]);

            //Check tuner can support ATV source
            if (stPlayerState.v_PIPSource[i] == MAPI_INPUT_SOURCE_ATV)
            {
                //Tuner be used, don't check this tuner
                if (tunersUsedFlag[j] == TRUE)
                {
                    continue;
                }

                if (stTunerTable.bATVSupport == TRUE)
                {
                    //ATV used this tuner, check next pip source
                    tunersUsedFlag[j] = TRUE;
                    bGetTuner = TRUE;
                    break;
                }
                else
                {
                    //Find next support ATV tuner
                    continue;
                }
            }
            else if ((stPlayerState.v_PIPSource[i] == MAPI_INPUT_SOURCE_DTV) ||
                (stPlayerState.v_PIPSource[i] == MAPI_INPUT_SOURCE_DTV2))
            {
                //Tuner be used, don't check this tuner
                if (tunersUsedFlag[j] == TRUE)
                {
                    continue;
                }

                //Check tuner can support DTV source
                EN_DTV_TYPE dtvType = SystemInfo::GetInstance()->GetDTVRouteEnumByRouteIndex(stPlayerState.v_PIPRoute[i]);

                for (unsigned int k = 0; k < stTunerTable.v_stDTVsRoute.size(); k++)
                {
                    if (dtvType == stTunerTable.v_stDTVsRoute[k])
                    {
                        //DTV used this tuner, check next pip source
                        tunersUsedFlag[j] = TRUE;
                        bGetTuner = TRUE;
                        break;
                    }
                }
            }
            else
            {
                //Not ATV or DTV, don't check pip source
                bGetTuner = TRUE;
                break;
            }
            if (bGetTuner == TRUE)
            {
                break;
            }
        }

        //Can't finded support tuner, return conflict or not support
        if (bGetTuner == FALSE)
        {
            return TRUE;
        }
    }

    //Check background tuner conflict
    for (unsigned int i = 0; i < stPlayerState.v_PVRRoute.size(); i++)
    {
        BOOL bGetTuner = FALSE;
        for (unsigned int j = 0; j < pTunerTable->size(); j++)
        {
            ST_TUNER_TABLE stTunerTable = ((*pTunerTable)[j]);

            //Tuner be used, don't check this tuner
            if (tunersUsedFlag[j] == TRUE)
            {
                continue;
            }

            //Check tuner can support DTV source
            EN_DTV_TYPE dtvType = SystemInfo::GetInstance()->GetDTVRouteEnumByRouteIndex(stPlayerState.v_PVRRoute[i]);

            for (unsigned int k = 0; k < stTunerTable.v_stDTVsRoute.size(); k++)
            {
                if (dtvType == stTunerTable.v_stDTVsRoute[k])
                {
                    //PVR used this tuner, check next pip source
                    tunersUsedFlag[j] = TRUE;
                    bGetTuner = TRUE;
                    break;
                }
            }

            //finded support tuner break
            if (bGetTuner == TRUE)
            {
                break;
            }
        }

        //Can't finded support tuner, return conflict or not support
        if (bGetTuner == FALSE)
        {
            return TRUE;
        }
    }

    return FALSE;
}

MSrv_Control_common::MSrv_PlayerControl::MSrv_PlayerConfigTable_PIP* MSrv_Control_common::MSrv_PlayerControl::MSrv_PlayerConfigTable_PIP::m_pInstance = NULL;

MSrv_Control_common::MSrv_PlayerControl::MSrv_PlayerConfigTable_PIP* MSrv_Control_common::MSrv_PlayerControl::MSrv_PlayerConfigTable_PIP::GetInstance()
{
    if (m_pInstance == NULL)
    {
        m_pInstance = new (std::nothrow) MSrv_PlayerConfigTable_PIP;
    }
    return m_pInstance;
}

BOOL MSrv_Control_common::MSrv_PlayerControl::MSrv_PlayerConfigTable_PIP::CheckConflict(ST_PLAYER_STATE& stPlayerState) const
{
    if (stPlayerState.v_PIPSource[MAPI_SUB_WINDOW] == MAPI_INPUT_SOURCE_NONE ||
        stPlayerState.v_PIPSource[MAPI_MAIN_WINDOW] == MAPI_INPUT_SOURCE_NONE)
    {
        return FALSE;
    }

#if (PIP_ENABLE == 1)
    if (SystemInfo::GetInstance()->GetPipPairInfo((MAPI_INPUT_SOURCE_TYPE)stPlayerState.v_PIPSource[MAPI_MAIN_WINDOW], (MAPI_INPUT_SOURCE_TYPE)stPlayerState.v_PIPSource[MAPI_SUB_WINDOW]) == TRUE)
    {
        return FALSE;
    }
#endif

    return TRUE;
}
#endif

BOOL MSrv_Control_common::MSrv_PlayerControl::GetConflictMessage(ST_CONFLICT_MSG& stConflictMsg)
{
#if (PIP_ENABLE == 1)
    mapi_scope_lock(scopeLock, &m_MutexPlayerControl);
    stConflictMsg = m_stConflictMsg;
#endif
    return TRUE;
}

BOOL MSrv_Control_common::GetConflictMessage(ST_CONFLICT_MSG& stConflictMsg) const
{
    return MSrv_PlayerControl::GetInstance()->GetConflictMessage(stConflictMsg);
}


BOOL MSrv_Control_common::MSrv_PlayerControl::CheckDtvRouteInForeground(const ST_PLAYER_STATE& stPlayerState, U8 u8DtvRouteIndex) const
{
    if (((stPlayerState.v_PIPSource[MAPI_MAIN_WINDOW] == MAPI_INPUT_SOURCE_DTV) &&
        (stPlayerState.v_PIPRoute[MAPI_MAIN_WINDOW] == u8DtvRouteIndex)) ||
        ((stPlayerState.v_PIPSource[MAPI_SUB_WINDOW] == MAPI_INPUT_SOURCE_DTV2) &&
        (stPlayerState.v_PIPRoute[MAPI_SUB_WINDOW] == u8DtvRouteIndex)))
    {
        return TRUE;
    }
    return FALSE;
}

BOOL MSrv_Control_common::MSrv_PlayerControl::UpdateRecordStatusNotify(void)
{
#if (PVR_ENABLE == 1) && (ATSC_SYSTEM_ENABLE == 0)
    EN_PVR_RECORD_STATUS enPVRStatus = E_RECORD_NONE;
    BOOL bSubIsRecording = FALSE;
    BOOL bBackgroundIsRecording = FALSE;

    //Check sub input source is DTV and recording
    if (m_stPlayerState.v_PIPSource[MAPI_SUB_WINDOW] == MAPI_INPUT_SOURCE_DTV2)
    {
        MSrv_DTV_Player_DVB *pPlayer = dynamic_cast<MSrv_DTV_Player_DVB*>(MSrv_Control::GetInstance()->GetMSrvPlayer(MAPI_INPUT_SOURCE_DTV2));
        ASSERT(pPlayer);

        if (pPlayer->IsRecording() == TRUE)
        {
            bSubIsRecording = TRUE;
        }
    }

    //Check background DTV recording
    for (unsigned int i = 0; i < m_stPlayerState.v_PVRRoute.size(); i++)
    {
        MSrv_DTV_Player_DVB* pPlayer = dynamic_cast<MSrv_DTV_Player_DVB*>(MSrv_Control::GetInstance()->GetMSrvDtvByIndex(m_stPlayerState.v_PVRRoute[i]));
        ASSERT(pPlayer);

        if (pPlayer->IsRecording() == TRUE)
        {
            bBackgroundIsRecording = TRUE;
            break;
        }
    }

    if ((bSubIsRecording == TRUE) && (bBackgroundIsRecording == TRUE))
    {
        enPVRStatus = E_RECORD_SUB_BACKGROUND;
    }
    else if (bSubIsRecording == TRUE)
    {
        enPVRStatus = E_RECORD_SUB;
    }
    else if (bBackgroundIsRecording == TRUE)
    {
        enPVRStatus = E_RECORD_BACKGROUND;
    }
    else
    {
        enPVRStatus = E_RECORD_NONE;
    }

    if (m_enPVRStatus != enPVRStatus)
    {
        m_enPVRStatus = enPVRStatus;
        MSrv_Control::GetInstance()->PostEvent(NULL, EV_PVR_UPDATE_RECORD_ICON_STATUS, m_enPVRStatus, 0);
    }
    return TRUE;
#else
    return FALSE;
#endif
}

#if (PVR_ENABLE == 1)
BOOL MSrv_Control_common::IsAnyDTVPlayerRecording(void)
{
#if (ATSC_SYSTEM_ENABLE == 0)
    //Get PlayerControl status
    ST_PLAYER_STATE stPlayerState;
    MSrv_PlayerControl::GetInstance()->GetPlayerState(stPlayerState);

    //Check foreground main DTV is recording
    if (stPlayerState.v_PIPSource[MAPI_MAIN_WINDOW] == MAPI_INPUT_SOURCE_DTV)
    {
        MSrv_DTV_Player_DVB* pPlayer = dynamic_cast<MSrv_DTV_Player_DVB*>(MSrv_Control::GetInstance()->GetMSrvDtvByIndex(stPlayerState.v_PIPRoute[MAPI_MAIN_WINDOW]));
        ASSERT(pPlayer);

        if (pPlayer->IsRecording() == TRUE)
        {
            return TRUE;
        }
    }

    //Check foreground main DTV is recording
    if (stPlayerState.v_PIPSource[MAPI_SUB_WINDOW] == MAPI_INPUT_SOURCE_DTV2)
    {
        MSrv_DTV_Player_DVB* pPlayer = dynamic_cast<MSrv_DTV_Player_DVB*>(MSrv_Control::GetInstance()->GetMSrvDtvByIndex(stPlayerState.v_PIPRoute[MAPI_SUB_WINDOW]));
        ASSERT(pPlayer);

        if (pPlayer->IsRecording() == TRUE)
        {
            return TRUE;
        }
    }

    //Check background DTV is recording
    for (unsigned int i = 0; i < stPlayerState.v_PVRRoute.size(); i++)
    {
        MSrv_DTV_Player_DVB* pPlayer = dynamic_cast<MSrv_DTV_Player_DVB*>(MSrv_Control::GetInstance()->GetMSrvDtvByIndex(stPlayerState.v_PVRRoute[i]));
        ASSERT(pPlayer);

        if (pPlayer->IsRecording() == TRUE)
        {
            return TRUE;
        }
    }
#endif
    return FALSE;
}

BOOL MSrv_Control_common::GetRECMSrvDTVPlayer(vector<U8>& vDTVRoute)
{
#if (ATSC_SYSTEM_ENABLE == 0)
    //Get PlayerControl status
    ST_PLAYER_STATE stPlayerState;
    MSrv_PlayerControl::GetInstance()->GetPlayerState(stPlayerState);

    vDTVRoute.clear();

    //Check foreground main DTV is recording
    if (stPlayerState.v_PIPSource[MAPI_MAIN_WINDOW] == MAPI_INPUT_SOURCE_DTV)
    {
        MSrv_DTV_Player_DVB* pPlayer = dynamic_cast<MSrv_DTV_Player_DVB*>(MSrv_Control::GetInstance()->GetMSrvDtvByIndex(stPlayerState.v_PIPRoute[MAPI_MAIN_WINDOW]));
        ASSERT(pPlayer);

        if (pPlayer->IsRecording() == TRUE)
        {
            vDTVRoute.push_back(stPlayerState.v_PIPRoute[MAPI_MAIN_WINDOW]);
        }
    }

    //Check foreground main DTV is recording
    if (stPlayerState.v_PIPSource[MAPI_SUB_WINDOW] == MAPI_INPUT_SOURCE_DTV2)
    {
        MSrv_DTV_Player_DVB* pPlayer = dynamic_cast<MSrv_DTV_Player_DVB*>(MSrv_Control::GetInstance()->GetMSrvDtvByIndex(stPlayerState.v_PIPRoute[MAPI_SUB_WINDOW]));
        ASSERT(pPlayer);

        if (pPlayer->IsRecording() == TRUE)
        {
            vDTVRoute.push_back(stPlayerState.v_PIPRoute[MAPI_SUB_WINDOW]);
        }
    }

    //Check background DTV is recording
    for (unsigned int i = 0; i < stPlayerState.v_PVRRoute.size(); i++)
    {
        MSrv_DTV_Player_DVB* pPlayer = dynamic_cast<MSrv_DTV_Player_DVB*>(MSrv_Control::GetInstance()->GetMSrvDtvByIndex(stPlayerState.v_PVRRoute[i]));
        ASSERT(pPlayer);

        if (pPlayer->IsRecording() == TRUE)
        {
            vDTVRoute.push_back(stPlayerState.v_PVRRoute[i]);
        }
    }
#endif
    return TRUE;
}
#endif


#if (STR_ENABLE == 1)
static void *Do_UpdateEnv(void *arg)
{
    prctl(PR_SET_NAME, (unsigned long)"Do_UpdateEnv");
    mapi_str::AutoRegister _R;
    MSrv_Control::GetInstance()->SetPowerOffFlag(MAPI_TRUE);
    return NULL;
}

void Suspend_UpdateEnv()
{
    pthread_t tid;
    pthread_attr_t thr_attr;
    pthread_attr_init(&thr_attr);
    pthread_attr_setdetachstate(&thr_attr, PTHREAD_CREATE_DETACHED);
    ASSERT(0 == PTH_RET_CHK(pthread_create(&tid, &thr_attr, Do_UpdateEnv, 0)));
    pthread_attr_destroy(&thr_attr);
}

static void *Do_RsmUpdateEnv(void *arg)
{
    prctl(PR_SET_NAME, (unsigned long)"Do_RsmUpdateEnv");
    mapi_str::AutoRegister _R;
    MSrv_Control::GetInstance()->SetPowerOffFlag(MAPI_FALSE);
    return NULL;
}

void Resume_UpdateEnv()
{
    pthread_t tid;
    pthread_attr_t thr_attr;
    pthread_attr_init(&thr_attr);
    pthread_attr_setdetachstate(&thr_attr, PTHREAD_CREATE_DETACHED);
    ASSERT(0 == PTH_RET_CHK(pthread_create(&tid, &thr_attr, Do_RsmUpdateEnv, 0)));
    pthread_attr_destroy(&thr_attr);
}

void AddMultiTasks(void *func)
{
    if (u16SuspendIdx == MAX_SUSPEND_IDX)
    {
        printf("Error %s::%d::%s\n",__FILE__,__LINE__,__FUNCTION__);
    }
    else
    {
        pSuspendFunc[u16SuspendIdx] = func;
        u16SuspendIdx++;
    }
}

void DoMultiTasks()
{
    pthread_t tid;
    pthread_attr_t thr_attr;
    int i = 0;
    pthread_attr_init(&thr_attr);
    pthread_attr_setdetachstate(&thr_attr, PTHREAD_CREATE_DETACHED);
    for (i=0; i<u16SuspendIdx; i++)
        ASSERT(0 == PTH_RET_CHK(pthread_create(&tid, &thr_attr, (void* (*)(void*))pSuspendFunc[i], NULL)));
    pthread_attr_destroy(&thr_attr);
    u16SuspendIdx = 0;
}

static void *SSSoundSuspend(void *arg)
{
    prctl(PR_SET_NAME, (unsigned long)"SSSoundSuspend");
    mapi_str::AutoRegister _R;
    MSrv_Control::GetInstance()->GetMSrvSSSound()->Suspend();
    return NULL;
}
#if (CI_ENABLE == 1)
static void *CISuspend(void *arg)
{
    prctl(PR_SET_NAME, (unsigned long)"CISuspend");
    mapi_str::AutoRegister _R;
    for (U8 i=0; i<MSrv_CIMMI::GetCiSlotCount(); i++)
    {
        MSrv_Control::GetMSrvCIMMI(i)->SuspendCIThread();
    }
    return NULL;
}
#endif
static void *SuspendRecfgTimerFrmList(void *arg)
{
    prctl(PR_SET_NAME, (unsigned long)"SuspendRecfgTimerFrmList");
    mapi_str::AutoRegister _R;
    MSrv_Control::GetInstance()->GetMSrvTimer()->ReconfigTimerFromList();
    return NULL;
}
static void *SuspendSetDcPoweroffMode(void *arg)
{
    prctl(PR_SET_NAME, (unsigned long)"SuspendSetDcPoweroffMode");
    mapi_str::AutoRegister _R;
    MSrv_Control::GetInstance()->GetMSrvSystemDatabase()->SetDcPoweroffMode(TRUE);
    return NULL;
}
MAPI_BOOL MSrv_Control_common::Suspend(void)
{
    printf(" %s::%d::%s\n",__FILE__,__LINE__,__FUNCTION__);

#if (ENABLE_NETREADY == 1)
    MSrv_DeviceAgent::GetDAInstance()->UserTriggerServicAbort();
#endif

#if (CI_PLUS_ENABLE == 1)
    if(TRUE == ((MSrv_DTV_Player_DVB*)(MSrv_Control::GetMSrvDtv()))->IsCiOccupiedTuner(TRUE))
    {
       return MAPI_FALSE;
    }
#endif

#if (MSTAR_TVOS == 0) /* Moving setStrStatus to SendStrCommand in TVOS */
    mapi_interface::Get_mapi_str()->setStrStatus(MAPI_STR_STATUS_SUSPENDING);
#endif

#if (HDMITX_ENABLE == 1)
    GetMSrvHdmiTx()->Suspend();
#endif

    //enter source none
    printf("mstar timer suspend start\n");
    GetMSrvTimer()->Suspend();
    printf("mstar timer suspend end\n");
    Suspend_UpdateEnv();
    m_bHeartBeatMonitor = FALSE;

    AddMultiTasks((void*)SuspendRecfgTimerFrmList);
    AddMultiTasks((void*)SuspendSetDcPoweroffMode);
    DoMultiTasks();

    StopThreadMonitor();

#if (MSTAR_TVOS != 1)
    m_enStrFocusSource = GetCurrentInputSource();
#endif

#if (GINGA_ENABLE == 1)
    if(GetCurrentInputSource() == MAPI_INPUT_SOURCE_DTV)
    {
        ((MSrv_DTV_Player_ISDB*)(MSrv_Control::GetMSrvDtv()))->GingaEnable(FALSE);
    }
#endif

#if (PIP_ENABLE == 1)
    EN_PIP_MODES mode = GetPipMode();
    if (mode == E_PIP_MODE_PIP)
    {
        DisablePip();
    }
    else if (mode == E_PIP_MODE_POP)
    {
        if(MSrv_Control::GetMSrv3DManager()->GetCurrent3DFormat() == EN_3D_DUALVIEW)
        {
            MSrv_Control::GetMSrv3DManager()->Disable3DDualView();
        }
        else
        {
            DisablePop();
        }
    }
#endif
#if (TRAVELING_ENABLE == 1)
    MAPI_U16 enEngineType = E_TRAVELING_ENGINE_TYPE_SD;
    for (; enEngineType < E_TRAVELING_ENGINE_TYPE_MAX; enEngineType++)
    {
        if (TRUE == IsTravelingModeEnable((EN_TRAVELING_ENGINE_TYPE)enEngineType))
        {
            ST_TRAVELING_CALLBACK_EVENT_INFO stEventInfo;
            memset(&stEventInfo, 0, sizeof(ST_TRAVELING_CALLBACK_EVENT_INFO));

            stEventInfo.u32TravelEventInfo_Version = TRAVELING_EVENT_INFO_MSDK_VERSION;
            stEventInfo.u16TravelEventInfo_Length = sizeof(ST_TRAVELING_CALLBACK_EVENT_INFO);
            stEventInfo.enMsgType = E_TRAVELING_EVENT_CALLBACK_FAST_QUIT;   //Notify APP to quit traveling mode
            MSrvTravelingEventCallback((void*)&stEventInfo, (EN_TRAVELING_ENGINE_TYPE)enEngineType);
            FinalizeTravelingMode((EN_TRAVELING_ENGINE_TYPE)enEngineType);
        }
    }
#endif //#if (TRAVELING_ENABLE == 1)
    // finalize all DTV player background mode brfore suspend
    for (MAPI_U8 i = 0; i < MSrv_PlayerControl::GetInstance()->GetDtvRouteCount(); i++)
    {
        MSrv_DTV_Player *pDTVPlayer = dynamic_cast<MSrv_DTV_Player*>(MSrv_Control::GetInstance()->GetMSrvDtvByIndex(i));
        if(pDTVPlayer->IsBackgroundMode())
        {
            if (!pDTVPlayer->DisableBackgroundMode())
            {
                ASSERT(0);
                return FALSE;
            }
        }
    }

    //make sure SetInputSource won't send cmd
    while(MSrv_Control_common::m_monitor_st.m_bFlagThreadMonitorActive)usleep(1000);
    SetInputSource(MAPI_INPUT_SOURCE_NONE);

    AddMultiTasks((void*)SSSoundSuspend);
#if (CI_ENABLE == 1)
    AddMultiTasks((void*)CISuspend);
#endif
    DoMultiTasks();

#if (INTEL_WIDI_ENABLE == 1)
    MSrv_WIDI_ANDROIDUI::GetInstance()->Suspend();
#endif

    // TODO: Refine CEC
#if (CEC_ENABLE == 1)
    if (m_stCecThreadInfo.bActive == TRUE)
    {
        m_stCecThreadInfo.bActive = FALSE;
        if (m_CECThread != 0)
        {
            pthread_join(m_CECThread, NULL);
            GetMSrvCEC()->Finalize();
        }
    }
#endif

    GetMSrvSourceDetect()->Suspend();

    GetMSrvPicture()->Suspend();
    MSrv_MWE::GetInstance()->Suspend();

#if (MHL_ENABLE == 1)
    GetMSrvMHL()->Suspend();
#endif

#if (HDMI_HDCP22_ENABLE == 1)
    mapi_hdmi_video::SuspendHDMIHdcp22();
#endif

    //thread suspend
    mapi_interface::Get_mapi_str()->enterStrSuspend();
    mapi_interface::Get_mapi_str()->waitforAllThreadSuspend();

    mapi_interface::Get_mapi_pcb()->DeviceDeInit();

#if (MSTAR_TVOS == 1)
    mapi_interface::Get_mapi_str()->lockMutex();
#endif

    //driver suspend
    mapi_interface::Get_mapi_str()->driverSuspend();

    return MAPI_TRUE;
}

void MSrv_Control_common::Resume(void)
{
    printf(" %s::%d::%s\n",__FILE__,__LINE__,__FUNCTION__);

    /* STR resuming start */
    mapi_interface::Get_mapi_str()->setStrStatus(MAPI_STR_STATUS_RESUMING);

    //driver resume
    mapi_interface::Get_mapi_str()->driverResume();

#if (MSTAR_TVOS == 1)
    mapi_interface::Get_mapi_str()->unlockMutex();
#endif
#if (CEC_ENABLE == 1)
    mapi_interface::Get_mapi_cec()->CEC_Exit();
#endif
    mapi_interface::Get_mapi_pcb()->DeviceInit();
    //thread resume
    mapi_interface::Get_mapi_str()->enterStrResume();

    Resume_UpdateEnv();

#if (CI_ENABLE == 1)
    for (U8 i=0; i<MSrv_CIMMI::GetCiSlotCount(); i++)
    {
        MSrv_Control::GetMSrvCIMMI(i)->Init(i);
    }
#endif

    MSrv_MWE::GetInstance()->Resume();

#if (HDMI_HDCP22_ENABLE == 1)
    mapi_hdmi_video::ResumeHDMIHdcp22();
#endif

#if (MHL_ENABLE == 1)
    GetMSrvMHL()->Resume();
#endif

    GetMSrvPicture()->Resume();
    GetMSrvSourceDetect()->Resume();

    GetMSrvSSSound()->Resume();

    // FIXME: call subclass's function from superclass
    MSrv_Control::GetInstance()->StartThreadMonitor();

    // TODO: Refine CEC
#if (CEC_ENABLE == 1)
    CECStart();
#endif

    // Restore auido mute status of USER
    if (TRUE == GetAndroidAudioMuteFlag())
    {
        GetMSrvSSSound()->SetMute(MUTE_BYUSER, 1);
    }
#if (STR_ENABLE == 1)
    map<int,BOOL>::iterator it  = m_bAudioMute.begin(),
                          itend = m_bAudioMute.end();
    while(it != itend )
    {
        GetMSrvSSSound()->SetMute((SSSOUND_MUTE_TYPE)(*it).first, (*it).second);
        ++it;
    }
#endif
#if (INTEL_WIDI_ENABLE == 1)
    MSrv_WIDI_ANDROIDUI::GetInstance()->Resume();
#endif

     //restore input source
    MSrv_Timer* pTimer;
    struct tm timeinfo;
    MSrv_Timer::ST_time stTime;
    MSrv_Timer::EN_Timer_Period enState;
    MSrv_Timer::ST_OnTime_TVDes stDes;
    EN_TIMER_BOOT_TYPE enBootMode;
    MSrv_Timer::ST_time stEPGTimerStartTime;
    ST_EPG_EVENT_TIMER_INFO stCurTimerInfo={0};

    pTimer = GetMSrvTimer();
    pTimer->GetOnTime(stTime, enState, stDes, enBootMode);
    pTimer->GetCLKTime(timeinfo);

    if(IsWakeUpByRTC() && (((enBootMode == EN_TIMER_BOOT_REMINDER) || (enBootMode == EN_TIMER_BOOT_RECORDER) || (enBootMode == EN_TIMER_BOOT_CI_OP_REFRESH) || (enBootMode == EN_TIMER_BOOT_OAD_DOWNLOAD))))
    {
        m_enStrFocusSource = MAPI_INPUT_SOURCE_DTV;
    }

    if ((QueryPowerOnMode() == EN_POWER_DC_BOOT)
          && (enBootMode == EN_TIMER_BOOT_ON_TIMER)
          && (enState != MSrv_Timer::EN_Timer_Off)
          && (stTime.u8Hour == (U8) timeinfo.tm_hour)
          && (stTime.u8Minute == (U8)timeinfo.tm_min))
    {
        m_enStrFocusSource = pTimer->GetTVSrc(stDes.enTVSrc);
        printf("BootOnTime:switch to preset input src : %d\n",m_enStrFocusSource);
    }

    SetInputSource(m_enStrFocusSource);

    if ((enBootMode == EN_TIMER_BOOT_ON_TIMER) && (enState != MSrv_Timer::EN_Timer_Off) && (stTime.u8Hour == (U8) timeinfo.tm_hour) && (stTime.u8Minute == (U8)timeinfo.tm_min))
    {
        if (enState == MSrv_Timer::EN_Timer_Once)
        {
            pTimer->SetOnTime(stTime, MSrv_Timer::EN_Timer_Off, stDes, TRUE);
        }

        if (m_enStrFocusSource == MAPI_INPUT_SOURCE_DTV)
        {
            GetMSrvChannelManager()->ChangeToFirstProgram(E_FIRST_PROG_DTV, E_ON_TIME_BOOT_TYPE);
        }
        else if (m_enStrFocusSource == MAPI_INPUT_SOURCE_ATV)
        {
            GetMSrvChannelManager()->ChangeToFirstProgram(E_FIRST_PROG_ATV, E_ON_TIME_BOOT_TYPE);
        }
    }
    else if (((enBootMode == EN_TIMER_BOOT_REMINDER) || (enBootMode == EN_TIMER_BOOT_RECORDER) || (enBootMode == EN_TIMER_BOOT_CI_OP_REFRESH)) && (m_enStrFocusSource == MAPI_INPUT_SOURCE_DTV))
    {
        pTimer->GetEPGTimerEventByIndex(stCurTimerInfo, 0);
#if (ATSC_SYSTEM_ENABLE == 1)
        stCurTimerInfo.u32StartTime = mapi_interface::Get_mapi_system()->GetClockOffset();
#else
        MSrv_DTV_Player_DVB* pPlayer = dynamic_cast<MSrv_DTV_Player_DVB*>(MSrv_Control::GetMSrvDtv());
        if (pPlayer)
        {
            pPlayer->GetOffsetTime(stCurTimerInfo.u32StartTime, MAPI_TRUE);
        }
#endif
        pTimer->ConvertSeconds2StTime(stCurTimerInfo.u32StartTime, &stEPGTimerStartTime);
        if (IsWakeUpByRTC())
        {
            GetMSrvChannelManager()->ChangeToFirstProgram(E_FIRST_PROG_DTV, E_ON_TIME_BOOT_TYPE);
        }
        else
        {
            GetMSrvChannelManager()->ChangeToFirstProgram(E_FIRST_PROG_DTV, E_AC_DC_BOOT_TYPE);
        }
    }
    #if (OAD_ENABLE == 1)
    else if((enBootMode == EN_TIMER_BOOT_OAD_DOWNLOAD) && (m_enStrFocusSource == MAPI_INPUT_SOURCE_DTV))
    {
        if(GetActiveStandbyMode())
        {
            MSrv_DTV_Player_DVB *pMsrvDtv =  (MSrv_DTV_Player_DVB*)(MSrv_Control::GetMSrvDtv());
            if(pMsrvDtv)
            {
                pMsrvDtv->StartDownloadInStandby();
            }
        }
        else
        {
            MSrv_Control::GetMSrvDtv()->PlayCurrentProgram();
        }
    }
#endif
    else
    {
        if (m_enStrFocusSource == MAPI_INPUT_SOURCE_ATV)
        {
#if (STB_ENABLE == 0)
            MSrv_Control::GetMSrvAtv()->SetChannel(MSrv_Control::GetMSrvAtvDatabase()->GetProgramCtrl(GET_CURRENT_PROGRAM_NUMBER, 0, 0, NULL), 1);
#endif
        }
        else if (m_enStrFocusSource == MAPI_INPUT_SOURCE_DTV)
        {
            MSrv_Control::GetMSrvDtv()->PlayCurrentProgram();
        }
    }

    GetMSrvTimer()->Resume();
#if (HDMITX_ENABLE == 1)
    MSrv_Control::GetMSrvHdmiTx()->Resume();
#endif
#if (GINGA_ENABLE == 1)
    if(GetCurrentInputSource() == MAPI_INPUT_SOURCE_DTV)
    {
        MSrv_System_Database_DVB *pDB;
        pDB = dynamic_cast<MSrv_System_Database_DVB *>(MSrv_Control::GetMSrvSystemDatabase());
        ASSERT(pDB);
        ((MSrv_DTV_Player_ISDB*)(MSrv_Control::GetMSrvDtv()))->GingaEnable(pDB->GetGingaMode());
    }
#endif

    StartWatchDog();
    InitHeartBeat();
    /* STR final step */
    mapi_interface::Get_mapi_str()->setStrStatus(MAPI_STR_STATUS_NORMAL);
}

void MSrv_Control_common::InitializeStr(void)
{
    int powerMode = GetMSrvFactoryMode()->GetStrPowerMode();
    SendStrCommand(E_STR_CMD_SET_MAX_CNT, powerMode, 0);
}

MAPI_BOOL MSrv_Control_common::GetAndroidAudioMuteFlag()
{
    return m_bAndroidAudioMute;
}

void MSrv_Control_common::SetAndroidAudioMuteFlag(MAPI_BOOL bAndroidAudioMute)
{
    m_bAndroidAudioMute = bAndroidAudioMute;
}

MAPI_BOOL MSrv_Control_common::GetAudioMuteFlag(int MuteType)
{
    map<int,BOOL>::iterator it  = m_bAudioMute.find(MuteType);
    if(it != m_bAudioMute.end())
    {
       return (*it).second ;
    }
    else
    {
       return MAPI_FALSE;
    }
}
MAPI_BOOL MSrv_Control_common::SetAudioMuteFlag(int MuteType,MAPI_BOOL bAudioMute)
{
    map<int,BOOL>::iterator it  = m_bAudioMute.find(MuteType);
    if(it != m_bAudioMute.end())
    {
       (*it).second = bAudioMute;
    }
    else
    {
       m_bAudioMute.insert(map<int,BOOL>::value_type(MuteType,bAudioMute));
    }
    return MAPI_TRUE;
}
MAPI_BOOL MSrv_Control_common::SendStrCommand(EN_STR_CMD eCmd, int wParam, int lParam)
{
    if (E_STR_CMD_MAX <= eCmd)
    {
        return MAPI_FALSE;
    }
    switch (eCmd)
    {
        case E_STR_CMD_SET_MAX_CNT:
        {
            char maxCntString[10];
            int maxCnt = (wParam >= 0 ? wParam : 0);
            memset(maxCntString, 0, 10);

            sprintf(maxCntString, "%d", maxCnt);

            int strMaxCntFd = open(STR_MAX_CNT_FILE, O_RDWR);
            if (strMaxCntFd < 0)
            {
                printf("open /sys/power/str_max_cnt failed\n");
                return MAPI_FALSE;
            }
            else
            {
                if (write(strMaxCntFd, maxCntString, strlen(maxCntString)) < 0)
                {
                    printf("write mstr /sys/power/str_max_cnt failed\n");
                }
            }
            close(strMaxCntFd);
            break;
        }
        case E_STR_CMD_SET_LAST_INPUT_SOURCE:
        {
#if (MSTAR_TVOS == 1) /* Changing STR status here where STR flow started from in TVOS. */
            mapi_interface::Get_mapi_str()->setStrStatus(MAPI_STR_STATUS_SUSPENDING);
#endif
            m_enStrFocusSource = (MAPI_INPUT_SOURCE_TYPE)wParam;
            break;
        }
        case E_STR_CMD_SET_STATUS:
        {
            mapi_interface::Get_mapi_str()->setStrStatus((MAPI_STR_STATUS)wParam);
            break;
        }
        default:
            break;
    }
    return MAPI_TRUE;
}

MAPI_BOOL MSrv_Control_common::CheckEnterSTRSuspendMode()
{
    EN_WAKEUP_SOURCE bootsrc = mapi_interface::Get_mapi_system()->QueryWakeupSource();
    if (EN_WAKEUPSRC_AVLINK == bootsrc)
    {
        return MAPI_TRUE;
    }
    else
    {
        return MAPI_FALSE;
    }
}
#endif

#if (ENABLE_PARTIAL_STANDBY == 1)
BOOL MSrv_Control_common::EnterTVPWOnControlMode()
{
    static BOOL bCurPanelPowerState = true;
    //Todo: Close all backlight
    MSrv_Timer* pTtimer;
    pTtimer  = MSrv_Control::GetMSrvTimer();

    if (bCurPanelPowerState)
    {
        // SW Mute first
        mapi_interface::Get_mapi_audio()->SetSoundMute(SOUND_MUTE_ALL_, E_MUTE_ON_);
        // HW mute then
        mapi_interface::Get_mapi_audio()->SetSoundMute(SOUND_MUTE_AMP_, E_MUTE_ON_);

        MSrv_Control::GetMSrvPicture()->SetBacklight_OnOff(FALSE);

        pTtimer->SetTVPowerDoneTimer(SECOND_TV_PW_CONTROL_TIME);
        printf(" [PW ON CTRL] Entering Partial Standby!\n");
        bCurPanelPowerState = false;
        return MAPI_FALSE;
    }
    else
    {
        // SW Mute first
        mapi_interface::Get_mapi_audio()->SetSoundMute(SOUND_MUTE_ALL_, E_MUTE_OFF_);
        // HW mute then
        mapi_interface::Get_mapi_audio()->SetSoundMute(SOUND_MUTE_AMP_, E_MUTE_OFF_);

        MSrv_Control::GetMSrvPicture()->SetBacklight_OnOff(TRUE);//obey panel off timing

        pTtimer->SetTVPowerDoneTimer(0xFFFFFFFF);
        printf(" [PW ON CTRL] Go to normal standby!\n");
        bCurPanelPowerState = true;
        return MAPI_TRUE;
    }
    return MAPI_TRUE;
}
#endif

MAPI_U32 MSrv_Control_common::SendIRKeyStart(void)
{
    MAPI_U32 ret = 0;
    MSRV_CONTROL_COMMON_INFO("SendIRKeyStart in\n");
#ifdef IRSEND_ENABLE
    ret = mapi_interface::Get_mapi_system()->IrSendKeyStart();
#endif
    if (0 == ret) {
        MSRV_CONTROL_COMMON_ERR("SendIRKeyStart, ret = [%d]", ret);
    }

    return ret;
}

MAPI_U32 MSrv_Control_common::SendIRKey(MAPI_S32 i32sendkeyvalue)
{
    MAPI_U32 ret = 0;
    MSRV_CONTROL_COMMON_INFO("SendIRKey in\n");

#ifdef IRSEND_ENABLE
    ret = mapi_interface::Get_mapi_system()->IrSendKey(i32sendkeyvalue);
#endif

    if (0 == ret) {
        MSRV_CONTROL_COMMON_ERR("SendIRKey, ret = [%d]", ret);
    }

    return ret;
}

MAPI_U32 MSrv_Control_common::SendIRKeyStop(void)
{
    MAPI_U32 ret = 0;
    MSRV_CONTROL_COMMON_INFO("SendIRKeyStop in\n");

#ifdef IRSEND_ENABLE
    ret = mapi_interface::Get_mapi_system()->IrSendKeyStop();
#endif

    if (0 == ret) {
        MSRV_CONTROL_COMMON_ERR("SendIRKeyStop, ret = [%d]", ret);
    }

    return ret;
}

void MSrv_Control_common::GetCompilerFlag(string sFlagName, string& sValue)
{
#if (MSTAR_TVOS == 1)
#define COMPILERFLAGE_ENABLE '1'
#define COMPILERFLAGE_DISABLE '0'
    string sFlagName_original = sFlagName;

    // open file
    std::ifstream ifs;
    ifs.open(FLAG_FILE, std::ifstream::binary);

    string sBuffer;
    std::getline(ifs, sBuffer);

    // close file
    ifs.close();

    // make sure string compare is absolutely match
    // pre-charactor before flag name must be a blank
    sFlagName = " " + sFlagName;
    sFlagName = sFlagName + '=';
    std::size_t FlagPosition = 0;

    // find matched flag name
    FlagPosition = sBuffer.find(sFlagName);
    if(FlagPosition != std::string::npos)
    {
        // find end position of flag value
        std::size_t ValuePosition = sBuffer.find(' ', FlagPosition + sFlagName.size());
        if(ValuePosition != std::string::npos)
        {
            // calculate length of flag value
            int iValueLength = ValuePosition - (FlagPosition + sFlagName.size());
            // get and return value of flag
            sValue = sBuffer.substr(FlagPosition + sFlagName.size(), iValueLength);
        }
    }

    //Check ATV System and Types.
    if (strncmp(sFlagName_original.c_str(), "ATV_NTSC_ENABLE", strlen("ATV_NTSC_ENABLE")) == 0)
    {
        if (SystemInfo::GetInstance()->get_ATVSystemType() == E_NTSC_ENABLE)
        {
           sValue = COMPILERFLAGE_ENABLE;
        }
        else
        {
            sValue = COMPILERFLAGE_DISABLE;
        }
    }
    if (strncmp(sFlagName_original.c_str(), "ATV_PAL_ENABLE", strlen("ATV_PAL_ENABLE")) == 0)
    {
        if (SystemInfo::GetInstance()->get_ATVSystemType() == E_PAL_ENABLE
            || SystemInfo::GetInstance()->get_ATVSystemType() == E_CHINA_ENABLE
            || SystemInfo::GetInstance()->get_ATVSystemType() == E_PAL_M_ENABLE)
        {
            sValue = COMPILERFLAGE_ENABLE;
        }
        else
        {
            sValue = COMPILERFLAGE_DISABLE;
        }
    }
    if (strncmp(sFlagName_original.c_str(), "ATV_CHINA_ENABLE", strlen("ATV_CHINA_ENABLE")) == 0)
    {
        if (SystemInfo::GetInstance()->get_ATVSystemType() == E_CHINA_ENABLE)
        {
            sValue = COMPILERFLAGE_ENABLE;
        }
        else
        {
            sValue = COMPILERFLAGE_DISABLE;
        }
    }
    if (strncmp(sFlagName_original.c_str(), "ATV_PAL_M_ENABLE", strlen("ATV_PAL_M_ENABLE")) == 0)
    {
        if (SystemInfo::GetInstance()->get_ATVSystemType() == E_PAL_M_ENABLE)
        {
            sValue = COMPILERFLAGE_ENABLE;
        }
        else
        {
            sValue = COMPILERFLAGE_DISABLE;
        }
    }

    if (strncmp(sFlagName_original.c_str(), "HDR_SUPPORT", strlen("HDR_SUPPORT")) == 0)
    {
        if (GetMSrvVideo()->IsSupportHdr() == MAPI_TRUE)
        {
            sValue = COMPILERFLAGE_ENABLE;
        }
        else
        {
            sValue = COMPILERFLAGE_DISABLE;
        }
    }
#endif
}

MAPI_BOOL MSrv_Control_common::RefreshWindow(MAPI_SCALER_WIN eWin)
{
    MAPI_INPUT_SOURCE_TYPE eInputType = (eWin==MAPI_MAIN_WINDOW)?GetCurrentMainInputSource():GetCurrentSubInputSource();
    if (eInputType >= MAPI_INPUT_SOURCE_NONE)
    {
        return MAPI_FALSE;
    }

    MSrv_Player *pPlayer = MSrv_Control::GetInstance()->GetMSrvPlayer(eInputType);
    if (pPlayer == NULL)
    {
        return MAPI_FALSE;
    }

    pPlayer->RefreshWindow();
    return MAPI_TRUE;
}

BOOL MSrv_Control_common::IsSupportedFeature(EN_MSRV_SUPPORTED_FEATURES enFeature, void *pParam)
{
    BOOL bIsSupported = FALSE;
    mapi_video *pMapiVideo = NULL;
    MAPI_INPUT_SOURCE_TYPE enCurrentInputType = MAPI_INPUT_SOURCE_NONE;

    switch (enFeature)
    {
        case E_MSRV_SUPPORTED_FEATURE_4K2K_PIP:
            enCurrentInputType = GetCurrentInputSource();
            pMapiVideo = mapi_interface::Get_mapi_video(enCurrentInputType);
            if(pMapiVideo != NULL)
            {
                bIsSupported = pMapiVideo->IsSupportedFeature(mapi_video_datatype::E_MAPI_VIDEO_FEATURE_4K2K_PIP, NULL);
                MSRV_CONTROL_COMMON_INFO("Input feature (%d), supported(%d)\n", enFeature, bIsSupported);
            }
            else
            {
                MSRV_CONTROL_COMMON_ERR("Get video fail, get source(%d)\n", enCurrentInputType);
            }
            break;
        case E_MSRV_SUPPORTED_FEATURE_MAX:
        default:
            MSRV_CONTROL_COMMON_ERR("Invalid input feature (%d)\n", enFeature);
            break;
    }

    return bIsSupported;
}

void MSrv_Control_common::SetBlindScreen(void)
{
}

#if (MSTAR_TVOS == 1)
MAPI_U8 MSrv_Control_common::GetResolution(void)
{
    MAPI_U8 resolution_index = 5;
    IEnvManager* pEnvMan = IEnvManager::Instance();
    if(pEnvMan)
    {
        const char* pInfo = pEnvMan->GetEnv("resolution");
        if(pInfo)
        {
            resolution_index = (int)strtol(pInfo, NULL, 10);
        }
    }
    return resolution_index;
}

MAPI_BOOL MSrv_Control_common::CompareResolution(MAPI_U32 res,MAPI_U8 res_indx)
{
    MAPI_BOOL ret = MAPI_FALSE;
    switch(res)
    {
        case 480:
#if (HDMITX_ENABLE == 1)
            if( (res_indx == E_MAPI_ROCKY_RES_720x480i) ||
                (res_indx == E_MAPI_ROCKY_RES_720x480p))
#else
            if((res_indx==0)||(res_indx==6))
#endif
                ret = MAPI_TRUE;
            break;
        case 576:
#if (HDMITX_ENABLE == 1)
            if( (res_indx == E_MAPI_ROCKY_RES_720x576p) ||
                (res_indx == E_MAPI_ROCKY_RES_720x576i))
#else
            if((res_indx==1)||(res_indx==7))
#endif
                ret = MAPI_TRUE;
            break;
        case 720:
#if (HDMITX_ENABLE == 1)
            if( (res_indx == E_MAPI_ROCKY_RES_1280x720p_50Hz) ||
                (res_indx == E_MAPI_ROCKY_RES_1280x720p_60Hz))
#else
            if((res_indx==2)||(res_indx==3))
#endif
                ret = MAPI_TRUE;
            break;
        case 1080:
#if (HDMITX_ENABLE == 1)
            if( (res_indx == E_MAPI_ROCKY_RES_1920x1080i_50Hz) ||
                (res_indx == E_MAPI_ROCKY_RES_1920x1080i_60Hz) ||
                (res_indx == E_MAPI_ROCKY_RES_1920x1080p_24Hz) ||
                (res_indx == E_MAPI_ROCKY_RES_1920x1080p_25Hz) ||
                (res_indx == E_MAPI_ROCKY_RES_1920x1080p_30Hz) ||
                (res_indx == E_MAPI_ROCKY_RES_1920x1080p_50Hz) ||
                (res_indx == E_MAPI_ROCKY_RES_1920x1080p_60Hz))
#else
            if((res_indx==4)||(res_indx==5)||(res_indx==8)||(res_indx==9))
#endif
                ret = MAPI_TRUE;
            break;
        case 2160:
#if (HDMITX_ENABLE == 1)
            if((res_indx == E_MAPI_ROCKY_RES_4K2Kp_60Hz) ||
                (res_indx == E_MAPI_ROCKY_RES_4K2Kp_50Hz) ||
                (res_indx == E_MAPI_ROCKY_RES_4K2Kp_30Hz) ||
                (res_indx == E_MAPI_ROCKY_RES_4K2Kp_25Hz) ||
                (res_indx == E_MAPI_ROCKY_RES_4K2Kp_24Hz))
#else
             printf("need to be add if support compare 4K2K resolution\n");
             if(false)
#endif
                ret = MAPI_TRUE;
        break;
    }
    printf("[%s:%d]res_index %d, match %d case\n",__FUNCTION__,__LINE__,res_indx,res);
    return ret;
}

MAPI_U8 MSrv_Control_common::SetReproduceRate(MAPI_U32 u32Rate)
{
    MAPI_U8 resolutionindex=GetResolution();
    IEnvManager* pEnvMan = IEnvManager::Instance();
    char buf[64];
    sprintf(buf, "%d", u32Rate);

    if (pEnvMan)
    {
        if(CompareResolution(2160,resolutionindex))  ////3840x2160
        {
            pEnvMan->SetEnv("reproducerate2160", buf);
            pEnvMan->SetEnv2BootArgs("rate2160=",buf);
        }
        else if(CompareResolution(1080,resolutionindex)) ////1920*1080
        {
            pEnvMan->SetEnv("reproducerate1080", buf);
            pEnvMan->SetEnv2BootArgs("rate1080=",buf);
        }
        else if(CompareResolution(720,resolutionindex)) ////720
        {
            pEnvMan->SetEnv("reproducerate720", buf);
            pEnvMan->SetEnv2BootArgs("rate720=",buf);
        }
        else if(CompareResolution(576,resolutionindex)) ////576
        {
            pEnvMan->SetEnv("reproducerate576", buf);
            pEnvMan->SetEnv2BootArgs("rate576=",buf);
        }
        else if(CompareResolution(480,resolutionindex)) ////480
        {
            pEnvMan->SetEnv("reproducerate480", buf);
            pEnvMan->SetEnv2BootArgs("rate480=",buf);
        }
        //we should  set the  last value in bootargs.
        pEnvMan->SetEnv2BootArgs("reproducerate=", buf);
        pEnvMan->SaveEnv();
    }
    else
    {
        printf("\n pEnvMan error");
    }
    return MAPI_TRUE;
}

MAPI_U32 MSrv_Control_common::GetReproduceRate()
{
    MAPI_U32 reproduceRate=0;
    MAPI_U8 resolutionindex=GetResolution();
    IEnvManager* pEnvMan = IEnvManager::Instance();
    if(pEnvMan)
    {
        const char* pInfo = NULL;

        if(CompareResolution(2160,resolutionindex)) ////3840*2160
        {
            pInfo= pEnvMan->GetEnv("reproducerate2160");
        }
        else if(CompareResolution(1080,resolutionindex)) ////1920*1080
        {
            pInfo= pEnvMan->GetEnv("reproducerate1080");
        }
        else if(CompareResolution(720,resolutionindex)) ////720
        {
            pInfo= pEnvMan->GetEnv("reproducerate720");
        }
        else if(CompareResolution(576,resolutionindex)) ////576
        {
            pInfo= pEnvMan->GetEnv("reproducerate576");
        }
        else if(CompareResolution(480,resolutionindex)) ////480
        {
            pInfo= pEnvMan->GetEnv("reproducerate480");
        }

        if(pInfo)
        {
            reproduceRate =(MAPI_U32) strtoul(pInfo, NULL, 0);
        }
    }
    return reproduceRate;
}

MAPI_BOOL MSrv_Control_common::SaveEnvForMBoot(EN_TIMING enTiming)
{
#if (STB_ENABLE == 1) || (HDMITX_ENABLE == 1)
    string strPanelName;
    string strResolution;
    IEnvManager* pEnvMan = IEnvManager::Instance();
    //mapi_display_datatype::EN_DISPLAY_RES_TYPE eRes;

    const MAPI_U8 u8BufSize = 50;
    char as8PanelFolderPath[u8BufSize] = {0, };
    char as8PanelFilePath[u8BufSize] = {0, };
    char resolution[u8BufSize] = {0, };

    strncpy(as8PanelFolderPath, "/config/panel", sizeof(as8PanelFolderPath));

#if (HDMITX_ENABLE == 1)
    // must disconnect first, because timing change will make unavoidable dclk floating
    //MSrv_Control::GetMSrvHdmiTx()->Disconnect();
    EN_MAPI_HDMITX_TIMING_TYPE eHDMITimgType = E_MAPI_HDMITX_TIMING_1080_60P;
    MAPI_U32 ReproduceRate = GetReproduceRate();
#endif

    MAPI_BOOL bSaveEnv = MAPI_TRUE;

    switch(enTiming)
    {
        case E_TIMING_4K2KP_60:
            //eRes = mapi_display_datatype::DISPLAY_4K2K_60P;
            strPanelName = "DACOUT_4K2KP_60";
            strResolution = "DACOUT_4K2KP_60";
            //snprintf(resolution,sizeof(resolution),"%d",99);
#if (HDMITX_ENABLE == 1)
            eHDMITimgType = E_MAPI_HDMITX_TIMING_4K2K_60P;
#endif
            break;
        case E_TIMING_4K2KP_50:
            //eRes = mapi_display_datatype::DISPLAY_4K2K_50P;
            strPanelName = "DACOUT_4K2KP_50";
            strResolution = "DACOUT_4K2KP_50";
            //snprintf(resolution,sizeof(resolution),"%d",99);
#if (HDMITX_ENABLE == 1)
            eHDMITimgType = E_MAPI_HDMITX_TIMING_4K2K_50P;
#endif
            break;
        case E_TIMING_4K2KP_30:
            //eRes = mapi_display_datatype::DISPLAY_4K2K_30P;
            strPanelName = "DACOUT_4K2KP_30";
            strResolution = "DACOUT_4K2KP_30";
            //snprintf(resolution,sizeof(resolution),"%d",19);
#if (HDMITX_ENABLE == 1)
            eHDMITimgType = E_MAPI_HDMITX_TIMING_4K2K_30P;
#endif
            break;
        case E_TIMING_4K2KP_25:
            //eRes = mapi_display_datatype::DISPLAY_4K2K_25P;
            strPanelName = "DACOUT_4K2KP_25";
            strResolution = "DACOUT_4K2KP_25";
            //snprintf(resolution,sizeof(resolution),"%d",18);
#if (HDMITX_ENABLE == 1)
            eHDMITimgType = E_MAPI_HDMITX_TIMING_4K2K_25P;
#endif
            break;
        case E_TIMING_4K2KP_24:
            strPanelName = "DACOUT_4K2KP_24";
            strResolution = "DACOUT_4K2KP_24";
            //snprintf(resolution,sizeof(resolution),"%d",99); //check dac table for 4K2k24p
#if (HDMITX_ENABLE == 1)
            eHDMITimgType = E_MAPI_HDMITX_TIMING_4K2K_24P;
#endif
            break;
        case E_TIMING_4096x2160P_24:
            strPanelName = "DACOUT_4096x2160P_24";
            strResolution = "DACOUT_4096x2160P_24";
            //snprintf(resolution,sizeof(resolution),"%d",99); //check dac table for 4K2k24p
#if (HDMITX_ENABLE == 1)
            eHDMITimgType = E_MAPI_HDMITX_TIMING_4096x2160_24P;
#endif
            break;
        case E_TIMING_4K1KP_60:
            bSaveEnv = MAPI_FALSE;
            strPanelName = "DACOUT_4K1KP_60";
            strResolution = "DACOUT_4K1KP_60";
            //snprintf(resolution,sizeof(resolution),"%d",99); //check dac table for 4K1k60p
#if (HDMITX_ENABLE == 1)
            eHDMITimgType = E_MAPI_HDMITX_TIMING_4K1K_60P;
#endif
            break;
        case E_TIMING_2K1KP_50:
            //eRes = mapi_display_datatype::DISPLAY_DACOUT_1080P_50;
            strPanelName = "DACOUT_1080P_50";
            strResolution = "DACOUT_1080P_50";
            snprintf(resolution,sizeof(resolution),"%d",4);
#if (HDMITX_ENABLE == 1)
            eHDMITimgType = E_MAPI_HDMITX_TIMING_1080_50P;
#endif
            break;
        case E_TIMING_2K1KP_60:
            //eRes = mapi_display_datatype::DISPLAY_DACOUT_1080P_60;
            strPanelName = "DACOUT_1080P_60";
            strResolution = "DACOUT_1080P_60";
            snprintf(resolution,sizeof(resolution),"%d",5);
#if (HDMITX_ENABLE == 1)
            eHDMITimgType = E_MAPI_HDMITX_TIMING_1080_60P;
#endif
            break;
        case E_TIMING_2K1KI_50:
            //eRes = mapi_display_datatype::DISPLAY_DACOUT_1080I_50;
            strPanelName = "DACOUT_1080I_50";
            strResolution = "DACOUT_1080I_50";
            snprintf(resolution,sizeof(resolution),"%d",8);
 #if (HDMITX_ENABLE == 1)
            eHDMITimgType = E_MAPI_HDMITX_TIMING_1080_50I;
#endif
            break;
        case E_TIMING_2K1KI_60:
            //eRes = mapi_display_datatype::DISPLAY_DACOUT_1080I_60;
            strPanelName = "DACOUT_1080I_60";
            strResolution = "DACOUT_1080I_60";
            snprintf(resolution,sizeof(resolution),"%d",9);
#if (HDMITX_ENABLE == 1)
            eHDMITimgType = E_MAPI_HDMITX_TIMING_1080_60I;
#endif
            break;
        case E_TIMING_2K1KP_30:
            //eRes = mapi_display_datatype::DISPLAY_DACOUT_1080P_30;
            strPanelName = "DACOUT_1080P_30";
            strResolution = "DACOUT_1080P_30";
            snprintf(resolution,sizeof(resolution),"%d",10);
#if (HDMITX_ENABLE == 1)
            eHDMITimgType = E_MAPI_HDMITX_TIMING_1080_30P;
#endif
            break;
        case E_TIMING_2K1KP_25:
            //eRes = mapi_display_datatype::DISPLAY_DACOUT_1080P_25;
            strPanelName = "DACOUT_1080P_25";
            strResolution = "DACOUT_1080P_25";
            snprintf(resolution,sizeof(resolution),"%d",11);
#if (HDMITX_ENABLE == 1)
            eHDMITimgType = E_MAPI_HDMITX_TIMING_1080_25P;
#endif
            break;
        case E_TIMING_2K1KP_24:
            //eRes = mapi_display_datatype::DISPLAY_DACOUT_1080P_24;
            strPanelName = "DACOUT_1080P_24";
            strResolution = "DACOUT_1080P_24";
            snprintf(resolution,sizeof(resolution),"%d",12);
#if (HDMITX_ENABLE == 1)
            eHDMITimgType = E_MAPI_HDMITX_TIMING_1080_24P;
#endif
            break;
        case E_TIMING_720P_50:
            //eRes = mapi_display_datatype::DISPLAY_DACOUT_720P_50;
            strPanelName = "DACOUT_720P_50";
            strResolution = "DACOUT_720P_50";
            snprintf(resolution,sizeof(resolution),"%d",2);
#if (HDMITX_ENABLE == 1)
            eHDMITimgType = E_MAPI_HDMITX_TIMING_720_50P;
#endif
            break;
        case E_TIMING_720P_60:
            //eRes = mapi_display_datatype::DISPLAY_DACOUT_720P_60;
            strPanelName = "DACOUT_720P_60";
            strResolution = "DACOUT_720P_60";
            snprintf(resolution,sizeof(resolution),"%d",3);
#if (HDMITX_ENABLE == 1)
            eHDMITimgType = E_MAPI_HDMITX_TIMING_720_60P;
#endif
            break;
        case E_TIMING_576P_50:
            //eRes = mapi_display_datatype::DISPLAY_DACOUT_576P;
            strPanelName = "DACOUT_576P_50";
            strResolution = "DACOUT_576P_50";
            snprintf(resolution,sizeof(resolution),"%d",1);
#if (HDMITX_ENABLE == 1)
            eHDMITimgType = E_MAPI_HDMITX_TIMING_576_50P;
#endif
            break;
        case E_TIMING_576I_50:
            //eRes = mapi_display_datatype::DISPLAY_DACOUT_576I;
            strPanelName = "DACOUT_576I_50";
            strResolution = "DACOUT_576I_50";
            snprintf(resolution,sizeof(resolution),"%d",7);
#if (HDMITX_ENABLE == 1)
            eHDMITimgType = E_MAPI_HDMITX_TIMING_576_50I;
#endif
            break;
        case E_TIMING_480P_60:
            //eRes = mapi_display_datatype::DISPLAY_DACOUT_480P;
            strPanelName = "DACOUT_480P_60";
            strResolution = "DACOUT_480P_60";
            snprintf(resolution,sizeof(resolution),"%d",0);
#if (HDMITX_ENABLE == 1)
            eHDMITimgType = E_MAPI_HDMITX_TIMING_480_60P;
#endif
            break;
        case E_TIMING_480I_60:
            //eRes = mapi_display_datatype::DISPLAY_DACOUT_480I;
            strPanelName = "DACOUT_480I_60";
            strResolution = "DACOUT_480I_60";
            snprintf(resolution,sizeof(resolution),"%d",6);
#if (HDMITX_ENABLE == 1)
            eHDMITimgType = E_MAPI_HDMITX_TIMING_480_60I;
#endif
            break;
        default:
            return false;
            break;
    }
#if (HDMITX_ENABLE == 1)
    strPanelName = mapi_syscfg_fetch::GetInstance()->GetPanelName((EN_MAPI_TIMING)enTiming);
#endif
    snprintf(as8PanelFilePath, sizeof(as8PanelFilePath),"%s/%s.ini", as8PanelFolderPath, strPanelName.c_str());

#if (HDMITX_ENABLE == 1)
    MAPI_U16 res = (MAPI_U32)MSrv_Control::GetMSrvHdmiTx()->getDeviceTimingType(eHDMITimgType);
    snprintf(resolution,sizeof(resolution),"%d",res);
#endif
    if(bSaveEnv == TRUE)
    {
        if (pEnvMan)
        {
            pEnvMan->SetEnv2BootArgs("resolution=",strResolution.c_str());
            pEnvMan->SetEnv("resolution", (const char *)resolution);
            pEnvMan->SetEnv("resolution_reset", (const char *)resolution);
            pEnvMan->SetEnv2BootArgs("panel_path=",as8PanelFilePath);
            pEnvMan->SetEnv("panel_path", as8PanelFilePath);
            pEnvMan->SetEnv("db_table", "0");
            pEnvMan->SaveEnv();
#if (HDMITX_ENABLE == 1)
            SetReproduceRate(ReproduceRate);
#endif
        }
        else
        {
            printf("Get EnvManager failed\n");
            ASSERT(0);
        }
    }

#endif

    return MAPI_TRUE;
}

MAPI_U16 MSrv_Control_common::SetOutputTiming(EN_TIMING enTiming, MAPI_U32 u32Operation, EN_OP_LOCK_TIMING_STATE enOpLockState)
{

    if(enOpLockState >= E_OP_LOCK_TIMING_STATE_MAX)
    {
        return ERROR_VIDEO_FAIL;
    }

    if((enOpLockState == E_OP_LOCK_TIMING_STATE_NONE) && (m_bSocOutputTimingLock == MAPI_TRUE))
    {
        return ERROR_VIDEO_TIMING_NO_CHANGE;
    }

    MAPI_U16 u16Ret = ERROR_VIDEO_FAIL;

    //Set lock status
    if(enOpLockState == E_OP_LOCK_TIMING_STATE_LOCK_TIMING)
    {
        u16Ret = GetMSrvVideo()->SetOutputTiming(enTiming, MAPI_TRUE);
        if(u16Ret == ERROR_VIDEO_SUCCESS)
        {
            m_bSocOutputTimingLock = MAPI_TRUE;
        }
    }
    else if(enOpLockState == E_OP_LOCK_TIMING_STATE_UNLOCK_TIMING)
    {
        u16Ret = GetMSrvVideo()->SetOutputTiming(enTiming, MAPI_FALSE);
        if(u16Ret == ERROR_VIDEO_SUCCESS)
        {
            m_bSocOutputTimingLock = MAPI_FALSE;
        }
    }
    else if(enOpLockState == E_OP_LOCK_TIMING_STATE_NONE)
    {
        u16Ret = GetMSrvVideo()->SetOutputTiming(enTiming, MAPI_FALSE);
    }


    if (u16Ret == ERROR_VIDEO_SUCCESS)
    {
        if(u32Operation & (MAPI_U32)E_SET_OP_TIMING_OPERATION_SAVE_TIMING)
        {
            SaveEnvForMBoot(enTiming);
        }
    }

    return u16Ret;
}
#else
// Set cvbs out system (PAL/NTSC)
MAPI_U16 MSrv_Control_common::SetCvbsOutSystem(EN_CVBS_OUT_SYSTEM enSystem)
{
#if (STB_ENABLE == 1) || (HDMITX_ENABLE == 1)

    MAPI_U16 u16Ret = GetMSrvVideo()->SetCvbsOutSystem(enSystem);
    if (u16Ret != ERROR_VIDEO_SUCCESS)
    {
        return u16Ret;
    }

#if (CONNECTTV_BOX == 0)
    SetBlindScreen();
#endif

    //
    T_MS_VIDEO stVideoTemp;
    MAPI_INPUT_SOURCE_TYPE enSrcType = MAPI_INPUT_SOURCE_DTV;
    GetMSrvSystemDatabase()->GetVideoSetting(&stVideoTemp, &enSrcType);

    // Get new output timing.
    EN_TIMING enTiming = MSrv_Control::GetMSrvVideo()->GetSocOutputTiming();

    // From MSrv_DTV_Player_DVB::SetOutputTiming.
    if (MSrv_Control::GetInstance()->GetCurrentInputSource() == MAPI_INPUT_SOURCE_DTV)
    {
        MSrv_DTV_Player_DVB *pMsrvDtv =  (MSrv_DTV_Player_DVB*)(MSrv_Control::GetMSrvDtv());
        if (stVideoTemp.eTvFormat == mapi_display_datatype::DISPLAY_TVFORMAT_16TO9SD)
        {
            if ((enTiming != E_TIMING_576I_50) &&  \
                (enTiming != E_TIMING_576P_50) &&  \
                (enTiming != E_TIMING_480I_60) &&  \
                (enTiming != E_TIMING_480P_60))
            {
                pMsrvDtv->Set_Default_Display(mapi_display_datatype::DISPLAY_TVFORMAT_16TO9HD);
            }
        }
        else if (stVideoTemp.eTvFormat == mapi_display_datatype::DISPLAY_TVFORMAT_16TO9HD)
        {
            if ((enTiming == E_TIMING_576I_50) ||  \
                (enTiming == E_TIMING_576P_50) ||  \
                (enTiming == E_TIMING_480I_60) ||  \
                (enTiming == E_TIMING_480P_60))
            {
                pMsrvDtv->Set_Default_Display(mapi_display_datatype::DISPLAY_TVFORMAT_16TO9SD);
            }
        }

#if (HBBTV_ENABLE == 1)
        if(MW_HBBTV::GetCheckedInstance() != NULL && MW_HBBTV::GetInstance().IsAnyApplicationRunning())
        {
            printf("\033[44m\033[33m[HBBTV][change Resolution, so restart HBBTV]-%s\033[m\n", __PRETTY_FUNCTION__);
            MW_HBBTV::GetInstance().Invalidate();
            MW_HBBTV::GetInstance().Validate();
        }
#endif
    }

    SetDisplayTiming((mapi_display_datatype::EN_DISPLAY_RES_TYPE)ConvertTimingEnumToDisplayRes(enTiming));

    return u16Ret;
#else
    return ERROR_VIDEO_FAIL;
#endif
}

// Set output timing.
MAPI_U16 MSrv_Control_common::SetOutputTiming(EN_TIMING enTiming, MAPI_U32 u32Operation, EN_OP_LOCK_TIMING_STATE enOpLockState)
{
#if (STB_ENABLE == 1) || (HDMITX_ENABLE == 1)

    if(enOpLockState >= E_OP_LOCK_TIMING_STATE_MAX)
    {
        return ERROR_VIDEO_FAIL;
    }

    if((enOpLockState == E_OP_LOCK_TIMING_STATE_NONE) && (m_bSocOutputTimingLock == MAPI_TRUE))
    {
        return ERROR_VIDEO_TIMING_NO_CHANGE;
    }

    MAPI_U16 u16Ret = ERROR_VIDEO_FAIL;

    //Set lock status
    if(enOpLockState == E_OP_LOCK_TIMING_STATE_LOCK_TIMING)
    {
        u16Ret = GetMSrvVideo()->SetOutputTiming(enTiming, MAPI_TRUE);
        if(u16Ret == ERROR_VIDEO_SUCCESS)
        {
            m_bSocOutputTimingLock = MAPI_TRUE;
        }
    }
    else if(enOpLockState == E_OP_LOCK_TIMING_STATE_UNLOCK_TIMING)
    {
        u16Ret = GetMSrvVideo()->SetOutputTiming(enTiming, MAPI_FALSE);
        if(u16Ret == ERROR_VIDEO_SUCCESS)
        {
            m_bSocOutputTimingLock = MAPI_FALSE;
        }
    }
    else if(enOpLockState == E_OP_LOCK_TIMING_STATE_NONE)
    {
        u16Ret = GetMSrvVideo()->SetOutputTiming(enTiming, MAPI_FALSE);
    }

    if (u16Ret != ERROR_VIDEO_SUCCESS)
    {
        return u16Ret;
    }
    T_MS_VIDEO stVideoTemp;
    MAPI_INPUT_SOURCE_TYPE enInputType = MAPI_INPUT_SOURCE_DTV;
    GetMSrvSystemDatabase()->GetVideoSetting(&stVideoTemp, &enInputType);

#if (CONNECTTV_BOX == 0)
    SetBlindScreen();
#endif

    // Move from MSrv_DTV_Player_DVB::SetOutputTiming
    if (MSrv_Control::GetInstance()->GetCurrentInputSource() == MAPI_INPUT_SOURCE_DTV)
    {
        MSrv_DTV_Player_DVB *pMsrvDtv =  (MSrv_DTV_Player_DVB*)(MSrv_Control::GetMSrvDtv());
        if (stVideoTemp.eTvFormat == mapi_display_datatype::DISPLAY_TVFORMAT_16TO9SD)
        {
            if ((enTiming != E_TIMING_576I_50) &&  \
                (enTiming != E_TIMING_576P_50) &&  \
                (enTiming != E_TIMING_480I_60) &&  \
                (enTiming != E_TIMING_480P_60))
            {
                pMsrvDtv->Set_Default_Display(mapi_display_datatype::DISPLAY_TVFORMAT_16TO9HD);
            }
        }
        else if (stVideoTemp.eTvFormat == mapi_display_datatype::DISPLAY_TVFORMAT_16TO9HD)
        {
            if ((enTiming == E_TIMING_576I_50) ||  \
                (enTiming == E_TIMING_576P_50) ||  \
                (enTiming == E_TIMING_480I_60) ||  \
                (enTiming == E_TIMING_480P_60))
            {
                pMsrvDtv->Set_Default_Display(mapi_display_datatype::DISPLAY_TVFORMAT_16TO9SD);
            }
        }

#if (HBBTV_ENABLE == 1)
        if(MW_HBBTV::GetCheckedInstance() != NULL && MW_HBBTV::GetInstance().IsAnyApplicationRunning())
        {
            printf("\033[44m\033[33m[HBBTV][change Resolution, so restart HBBTV]-%s\033[m\n", __PRETTY_FUNCTION__);
            MW_HBBTV::GetInstance().Invalidate();
            MW_HBBTV::GetInstance().Validate();
        }
#endif
    }

    SetDisplayTiming((mapi_display_datatype::EN_DISPLAY_RES_TYPE)ConvertTimingEnumToDisplayRes(enTiming));

    if(u32Operation & (MAPI_U32)E_SET_OP_TIMING_OPERATION_SAVE_TIMING)
    {
        T_MS_VIDEO stVideoTemp;
        MAPI_INPUT_SOURCE_TYPE enSrcType = MAPI_INPUT_SOURCE_DTV;

        MSrv_Control::GetMSrvSystemDatabase()->GetVideoSetting(&stVideoTemp, &enSrcType);
        stVideoTemp.fOutput_RES = (mapi_display_datatype::EN_DISPLAY_RES_TYPE)ConvertTimingEnumToDisplayRes(enTiming);
        MSrv_Control::GetMSrvSystemDatabase()->SetVideo(&stVideoTemp, &enSrcType);

        enSrcType = MAPI_INPUT_SOURCE_STORAGE;
        MSrv_Control::GetMSrvSystemDatabase()->GetVideoSetting(&stVideoTemp, &enSrcType);
        stVideoTemp.fOutput_RES = (mapi_display_datatype::EN_DISPLAY_RES_TYPE)ConvertTimingEnumToDisplayRes(enTiming);
        MSrv_Control::GetMSrvSystemDatabase()->SetVideo(&stVideoTemp, &enSrcType);
    }

    return u16Ret;
#else
    return ERROR_VIDEO_FAIL;
#endif
}
#endif

MAPI_BOOL MSrv_Control_common::browserClearLocalStorage(void)
{
    char *retStr = NULL;
    retStr = SystemInfo::GetInstance()->getLocalStoragePathOfMWBLauncherIni();
    if (NULL == retStr)
    {
        MSRV_CONTROL_COMMON_ERR("%s():%d Browser's local storage path parse fail from MWBLauncher.ini! \n",__FUNCTION__, __LINE__);
        return MAPI_FALSE;
    }
    else
    {
        std::string c_retStr(retStr);
        c_retStr = "rm -rf " + c_retStr + "*";
        MSRV_CONTROL_COMMON_DBG("%s\n", c_retStr.c_str());
        SystemCmd(c_retStr.c_str());
        MSRV_CONTROL_COMMON_INFO("%s():%d Clear cookieJar of LocalStorage\n",__FUNCTION__, __LINE__);
    }
    return MAPI_TRUE;
}

MAPI_BOOL MSrv_Control_common::IsSourceChanging(void)
{
    return m_bSourceChange;
}

void MSrv_Control_common::SetSourceChangingFlag(BOOL bSourceChange)
{
    m_bSourceChange = bSourceChange;
}

EN_MSRV_BOOT_KEY MSrv_Control_common::GetPowerOnIRKey(void)
{

    EN_WAKEUP_SOURCE enWakeupSource = QueryWakeupSource();
    EN_MSRV_BOOT_KEY enRet = EN_BOOT_KEY_NONE;

    if (enWakeupSource == EN_WAKEUPSRC_IR)
    {
        MAPI_U8 u8WakeupKeycode = 0xFF;
        MAPI_U8 u8PowerOnNetflixKey = 0xFF;

        u8WakeupKeycode = mapi_interface::Get_mapi_system()->GetIRPowerOnKey();
        u8PowerOnNetflixKey = SystemInfo::GetInstance()->GetPowerOnNetflixKey();

        if (u8WakeupKeycode == u8PowerOnNetflixKey)
        {
            enRet = EN_BOOT_KEY_NETFLIX_KEY;
        }
    }
    return enRet;
}

void MSrv_Control_common::InitHDMIEDIDInfoSet()
{
#if ((STB_ENABLE == 0) || (ENABLE_HDMI_RX == 1))
    MSrv_HDMI_Player::SetHDMIEDIDInfoSet();
#endif

    VGA_EDID_Info_t* stVgaEdid = SystemInfo::GetInstance()->GetVGAEDIDInfo();
    mapi_pc_video::SetVgaDdc(stVgaEdid);
    HDMI_EDID_InfoSet_t* stHdmiEdid = SystemInfo::GetInstance()->GetHDMIEDIDInfoSet();
    mapi_hdmi_video::SetHdmiDdc(stHdmiEdid);

    mapi_video::SetVideoInitState(mapi_video_datatype::E_MAPI_VIDEO_INIT_EDID, MAPI_TRUE);
}
#if (MODULE_TEST == 1)
void MSrv_Control_common::SetProfileValue(EN_PROFILE_CHECKPOINT eCheckPoint, U8 u8ReservedIndex, U32 u32Value)
{
    switch(eCheckPoint)
    {
        case EN_PROFILE_CP_CC_BEGIN:
            m_stProfileContainer.m_u32CC_begin = u32Value;
            break;
        case EN_PROFILE_CP_CC_END:
            m_stProfileContainer.m_u32CC_end = u32Value;
            break;
        case EN_PROFILE_CP_DC_BEGIN:
            m_stProfileContainer.m_u32DC_begin = u32Value;
            break;
        case EN_PROFILE_CP_SI_STOP:
            m_stProfileContainer.m_u32DC_SI_Stop = u32Value;
            break;
        case EN_PROFILE_CP_VDECSTOP:
            m_stProfileContainer.m_u32DC_VDecStop = u32Value;
            break;
        case EN_PROFILE_CP_VIDEOMUTE:
            m_stProfileContainer.m_u32DC_videoMute = u32Value;
            break;
        case EN_PROFILE_CP_DC_END:
            m_stProfileContainer.m_u32DC_end = u32Value;
            break;
        case EN_PROFILE_CP_EC_BEGIN:
            m_stProfileContainer.m_u32EC_begin = u32Value;
            break;
        case EN_PROFILE_CP_VDECSETVDECTYPE:
            m_stProfileContainer.m_u32_VDecSetVdecType = u32Value;
            break;
        case EN_PROFILE_CP_AUDPLAY:
            m_stProfileContainer.m_u32_audPlay = u32Value;
            break;
        case EN_PROFILE_CP_SPS:
            m_stProfileContainer.m_u32_SPS = u32Value;
            break;
        case EN_PROFILE_CP_SETWINDOW:
            m_stProfileContainer.m_u32_setWindow = u32Value;
            break;
        case EN_PROFILE_CP_PICON:
            m_stProfileContainer.m_u32_picOn = u32Value;
            break;
        case EN_PROFILE_CP_VIDEOUNMUTE:
            m_stProfileContainer.m_u32_videoUnmute = u32Value;
            break;
        case EN_PROFILE_CP_VE_ON:
            m_stProfileContainer.m_u32_VE_On = u32Value;
            break;
        case EN_PROFILE_CP_DOSETCOUNT:
            m_stProfileContainer.DoSetCount = u32Value;
            break;
        case EN_PROFILE_CP_CUR_VPID:
            m_stProfileContainer.cur_vpid = u32Value;
            break;
        case EN_PROFILE_CP_RESVERD:
            m_stProfileContainer.m_u32_reserved_checkpoint[u8ReservedIndex] = u32Value;
            break;
    }
}
ST_PROFILE_CONTAINER MSrv_Control_common::GetProfileContainer()
{
    return m_stProfileContainer;
}

void MSrv_Control_common::EnableChannelChangeProfile()
{
    m_bEnableChannelChangeProfileMSG = TRUE;
}

BOOL MSrv_Control_common::GetChannelChangeProfileFlag()
{
    return m_bEnableChannelChangeProfileMSG;
}

void MSrv_Control_common::EnableVdecDecodeInfoPorfile()
{
    m_bEnableVdecDecodeInfoProfileMSG = TRUE;
}

BOOL MSrv_Control_common::GetVdecDecodeInfoProfileFlag()
{
    return m_bEnableVdecDecodeInfoProfileMSG;
}
#endif

void MSrv_Control_common::WaitBootComplete()
{
    pthread_mutex_lock(&m_BootCompleteMutex);
    //do nothing
    pthread_mutex_unlock(&m_BootCompleteMutex);
}

void MSrv_Control_common::SetBootComplete(BOOL bIsComplete)
{
    if(!bIsComplete)
       pthread_mutex_lock(&m_BootCompleteMutex);
    else
       pthread_mutex_unlock(&m_BootCompleteMutex);
}

void MSrv_Control_common::SetWatchdogTimer(U16 u16Second)
{
    MS_USER_SYSTEM_SETTING stUserSetting;
    MSrv_Control::GetMSrvSystemDatabase()->GetUserSystemSetting(&stUserSetting);

    if(stUserSetting.bEnableWDT)
    {
        mapi_system *pSystem = mapi_interface::Get_mapi_system();
        ASSERT(pSystem != NULL);

        pSystem->SetWatchDogTimer(u16Second);
        pSystem->RefreshWatchDog();
    }
}

#if (INPUT_SOURCE_LOCK_ENABLE == 1)
MAPI_BOOL MSrv_Control_common::CheckInputSourceLockSupport(MAPI_INPUT_SOURCE_TYPE eMapiSrcType)
{
    switch (eMapiSrcType)
    {
        case MAPI_INPUT_SOURCE_VGA:
        case MAPI_INPUT_SOURCE_ATV:
        case MAPI_INPUT_SOURCE_CVBS:
        case MAPI_INPUT_SOURCE_CVBS2:
        case MAPI_INPUT_SOURCE_CVBS3:
        case MAPI_INPUT_SOURCE_CVBS4:
        case MAPI_INPUT_SOURCE_CVBS5:
        case MAPI_INPUT_SOURCE_CVBS6:
        case MAPI_INPUT_SOURCE_CVBS7:
        case MAPI_INPUT_SOURCE_CVBS8:
        case MAPI_INPUT_SOURCE_SVIDEO:
        case MAPI_INPUT_SOURCE_SVIDEO2:
        case MAPI_INPUT_SOURCE_SVIDEO3:
        case MAPI_INPUT_SOURCE_SVIDEO4:
        case MAPI_INPUT_SOURCE_YPBPR:
        case MAPI_INPUT_SOURCE_YPBPR2:
        case MAPI_INPUT_SOURCE_YPBPR3:
        case MAPI_INPUT_SOURCE_SCART:
        case MAPI_INPUT_SOURCE_SCART2:
        case MAPI_INPUT_SOURCE_HDMI:
        case MAPI_INPUT_SOURCE_HDMI2:
        case MAPI_INPUT_SOURCE_HDMI3:
        case MAPI_INPUT_SOURCE_HDMI4:
        case MAPI_INPUT_SOURCE_DTV:
        case MAPI_INPUT_SOURCE_DVI:
        case MAPI_INPUT_SOURCE_DVI2:
        case MAPI_INPUT_SOURCE_DVI3:
        case MAPI_INPUT_SOURCE_DVI4:
        case MAPI_INPUT_SOURCE_DTV2:
        case MAPI_INPUT_SOURCE_DTV3:
        case MAPI_INPUT_SOURCE_VGA2:
        case MAPI_INPUT_SOURCE_VGA3:
            return MAPI_TRUE;
            break;
        default:
            return MAPI_FALSE;
            break;
    }

    return MAPI_FALSE;
}
#endif

MAPI_BOOL MSrv_Control_common::IsInputSourceLock(MAPI_INPUT_SOURCE_TYPE eMapiSrcType)
{
#if (INPUT_SOURCE_LOCK_ENABLE == 0)
    return MAPI_FALSE;
#else
    ST_INPUT_SOURCE_LOCK_SETTING stInputSourceLock;
    MSrv_System_Database *p = GetMSrvSystemDatabase();

    ASSERT(p);

    if (CheckInputSourceLockSupport(eMapiSrcType) == MAPI_FALSE)
    {
        MSRV_CONTROL_COMMON_ERR("ERROR Unsupport source type[%d] for InputSourceLock.\n", eMapiSrcType);
        ASSERT(0);
    }

    p->GetInputSourceLockSetting(&stInputSourceLock);
    return stInputSourceLock.bInputSourceLockStatus[eMapiSrcType];
#endif
}

MAPI_BOOL MSrv_Control_common::SetInputSourceLock(MAPI_BOOL bLock, MAPI_INPUT_SOURCE_TYPE eMapiSrcType)
{
#if (INPUT_SOURCE_LOCK_ENABLE == 0)
    return MAPI_FALSE;
#else
    MAPI_BOOL bRet = MAPI_TRUE;
    ST_INPUT_SOURCE_LOCK_SETTING stInputSourceLock;
    MSrv_System_Database *p = GetMSrvSystemDatabase();

    ASSERT(p);

    if (CheckInputSourceLockSupport(eMapiSrcType) == MAPI_FALSE)
    {
        MSRV_CONTROL_COMMON_ERR("ERROR Unsupport source type[%d] for InputSourceLock.\n", eMapiSrcType);
        ASSERT(0);
    }

    //Write input source lock(ISL) status to DB
    //*IMPORTANT* This step must be front of doing video/audio mute/unmute
    p->GetInputSourceLockSetting(&stInputSourceLock);
    if (stInputSourceLock.bInputSourceLockStatus[eMapiSrcType] == bLock)
    {
        return MAPI_FALSE;
    }
    stInputSourceLock.bInputSourceLockStatus[eMapiSrcType] = bLock;
    bRet &= p->SetInputSourceLockSetting(&stInputSourceLock);

    //Set video/audio mute/unmute
    bRet &= GetMSrvPlayer(eMapiSrcType)->SetInputSourceLock(bLock);

    if (eMapiSrcType == GetCurrentMainInputSource())
    {
        if (bLock == MAPI_TRUE)
        {
            //Post event to UI
#if (MSTAR_TVOS == 1)
            GetMSrvPlayer(eMapiSrcType)->PostEvent(NULL, EV_INPUT_SOURCE_LOCK, E_INPUT_SOURCE_LOCK_EVENT_ON, E_INPUT_SOURCE_LOCK_EVENT_UNDEFINED);
#else
            PostEvent(NULL, EV_INPUT_SOURCE_LOCK, E_INPUT_SOURCE_LOCK_EVENT_ON, E_INPUT_SOURCE_LOCK_EVENT_UNDEFINED);
#endif
        }
        else
        {
            //Post event to UI
#if (MSTAR_TVOS == 1)
            GetMSrvPlayer(eMapiSrcType)->PostEvent(NULL, EV_INPUT_SOURCE_LOCK, E_INPUT_SOURCE_LOCK_EVENT_OFF, E_INPUT_SOURCE_LOCK_EVENT_UNDEFINED);
#else
            PostEvent(NULL, EV_INPUT_SOURCE_LOCK, E_INPUT_SOURCE_LOCK_EVENT_OFF, E_INPUT_SOURCE_LOCK_EVENT_UNDEFINED);
#endif
        }
    }

    return bRet;
#endif
}

MAPI_BOOL MSrv_Control_common::ResetInputSourceLock(void)
{
#if (INPUT_SOURCE_LOCK_ENABLE == 0)
    return MAPI_FALSE;
#else
    MAPI_BOOL bRet = MAPI_TRUE;
    ST_INPUT_SOURCE_LOCK_SETTING stInputSourceLock;
    MSrv_System_Database *p = GetMSrvSystemDatabase();

    ASSERT(p);

    //Video/Audio unmute for main source
    SetInputSourceLock(MAPI_FALSE, GetCurrentMainInputSource());

    //Video/Audio unmute for sub source
    SetInputSourceLock(MAPI_FALSE, GetCurrentSubInputSource());

    //Reset all input source lock status and write to DB
    memset(&stInputSourceLock, 0, sizeof(ST_INPUT_SOURCE_LOCK_SETTING));
    bRet = p->SetInputSourceLockSetting(&stInputSourceLock);

    return bRet;
#endif
}

// EosTek Patch Begin
void MSrv_Control_common::SetIRLedByOnOff(BOOL OnOff)
{
	BOOL bRet = FALSE;
	// Set Green Led
	//MApi_XC_WriteByteMask(0x101EB9, 0x00, 0x02);//Enable Led Green CTRL
	bRet = MSrv_Control_common::SetGpioDeviceStatus(LED_ON, OnOff);
    if(bRet)
    {
		MSRV_CONTROL_COMMON_INFO("SetIRLedByOnOff, Turn Green Led %s !!\n", OnOff ? "on" : "off");
    }

	// Set Red Led
    bRet = MSrv_Control_common::SetGpioDeviceStatus(LED_WORK, !OnOff);
    if(bRet)
    {
		MSRV_CONTROL_COMMON_INFO("SetIRLedByOnOff, Turn Red Led %s !!\n", OnOff ? "off" : "on");
    }
}

void MSrv_Control_common::SetEarPhoneByOnOff(BOOL OnOff)
{
	BOOL bRet = FALSE;

	bRet = MSrv_Control_common::SetGpioDeviceStatus(MUTE_POP, OnOff);
    if(bRet)
    {
        MSRV_CONTROL_COMMON_INFO("SetEarPhoneByOnOff, Turn %s !!\n", OnOff ? "on" : "off");
    }
	//printf("EarPhone Gpio status: %ld\n", MSrv_Control_common::GetGpioDeviceStatus(MUTE_POP));

}
// EosTek Patch End
