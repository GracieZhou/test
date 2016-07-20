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
#include "mapi_video.h"

// headers of standard C libs
#include <stdio.h>
#include <string.h>
#include <limits.h>
#include <unistd.h>
#ifndef MI_MSDK
#include <directfb.h>
#endif
#include <sys/prctl.h>
// headers of standard C++ libs

// headers of the same layer's
#include "debug.h"
#include "mapi_types.h"
#include "mapi_utility.h"
#include "_mapi_trans_utility.h"
#include "mapi_syscfg_fetch.h"
#include "mapi_pql.h"
#include "mapi_display.h"
#if 1//(CERTIFICATE_IN_NAND == 1)
#include "mapi_storage.h"
//#else
#include "mapi_storage_spiflash.h"
#include "mapi_storage_factory_config.h"
#endif
#include "mapi_storage_eeprom.h"
#include "mapi_system.h"
#include "mapi_video_customer_base.h"

// headers of underlying layer's
#include "MMAPInfo.h"

#include "MsCommon.h"
#include "drvXC_IOPort.h"
#include "apiXC.h"
#include "apiXC_DWIN.h"
#include "apiXC_Adc.h"
#include "apiXC_Auto.h"
#include "drvXC_HDMI_if.h"
#include "apiXC_Ace.h"
#include "apiXC_PCMonitor.h"
#include "apiXC_ModeParse.h"
#include "apiGOP.h"
#include "apiGFX.h"
#include "drvMVOP.h"
#include "apiPNL.h"
#if (STB_ENABLE == 1)
#include "drvSYS.h"
#include "apiHDMITx.h"
#if (DUAL_XC_ENABLE == 1)
#include "apiXC_EX.h"
#include "apiPNL_EX.h"
#include "mapi_video_base.h"
#endif
#endif
#ifndef MI_MSDK
#include "jpeglib.h"
#endif
#define FRC_BYPASS_MODE 0
#if (HDMI_HDCP22_ENABLE == 1)
#include "hdcpApp.h"
#if (TEE_ENABLE == 0)
#include "hdcpCallback.h"
#include "mapi_interface.h"
#include "mapi_secure_storage.h"
#define HDCP2_HEADER_LENGTH 4
#endif
#endif

#if defined(ENABLE_MLOG)  && (ENABLE_MLOG == 1)
#include "MLog.h"
#endif

MAPI_U8 mapi_video::FRC_3D_PANEL_TYPE=E_3D_PANEL_NONE;




#if defined(ENABLE_MLOG)  && (ENABLE_MLOG == 1)
#define OSDCDBGMSG(format,...)             MLOGD("OSDC",format, ##__VA_ARGS__)
#else
#define OSDCDBGMSG(format,...)             //MLOGD("OSDC",format, ##__VA_ARGS__)
#endif

#define MSG_3D(x) //x
#define MSG_VIDEO(x) //x
#define ERR_VIDEO(x) x

MAPI_BOOL mapi_video::bHWInit = MAPI_FALSE;
MAPI_BOOL mapi_video::bFactoryMode = MAPI_FALSE;
MAPI_U32 mapi_video::u32DS_Sharemem_Buf_Base = 0;
EN_MAPI_PIP_MODES mapi_video::m_ePipMode = E_MAPI_PIP_MODE_OFF;
MAPI_INPUT_SOURCE_TYPE mapi_video::m_eMainInputSource = MAPI_INPUT_SOURCE_NONE;
MAPI_INPUT_SOURCE_TYPE mapi_video::m_eSubInputSource = MAPI_INPUT_SOURCE_NONE;
MAPI_BOOL mapi_video::m_bSubXCVaild = MAPI_FALSE;
#if ((PIP_ENABLE == 1) && (TRAVELING_ENABLE == 1))
//When traveling main source,reseting traveling window may be needed if pip mode changed.
//Use m_bForceSetTravelingWin to confirm if reseting traveling window is necessary.
MAPI_BOOL mapi_video::m_bForceSetTravelingWin = MAPI_FALSE;
#endif

mapi_video_datatype::ST_MAPI_3D_INFO mapi_video::m_st3DInfo = {MAPI_FALSE, mapi_video_datatype::E_3D_INPUT_MODE_NONE, mapi_video_datatype::E_3D_OUTPUT_MODE_NONE};
#if (STB_ENABLE == 1)
#if (DTV_CHANNEL_CHANGE_FREEZE_IMAGE_ENBALE == 1)
MAPI_BOOL mapi_video::m_bEnableSeamlessZapping = MAPI_FALSE;
#endif
#endif

MAPI_BOOL mapi_video::m_bEDIDInit = MAPI_FALSE;
MAPI_BOOL mapi_video::m_bHDMIInit = MAPI_FALSE;
#ifdef UFO_XC_HDR
MAPI_BOOL mapi_video::m_bAutoDetectHdrLevel[2] = {MAPI_TRUE, MAPI_TRUE};
E_MAPI_XC_HDR_LEVEL mapi_video::m_ActiveHdrLevel[2] = {E_MAPI_XC_HDR_MIDDLE, E_MAPI_XC_HDR_MIDDLE};
E_MAPI_XC_HDR_LEVEL mapi_video::m_AutoHdrLevel[2] = {E_MAPI_XC_HDR_MIDDLE, E_MAPI_XC_HDR_MIDDLE};
#endif

static const MAPI_S16 s16DefaultColorCorrectionMatrix[32] =
{
    0x03EA, 0x0035, -0x0020, -0x0003, 0x0420, -0x001D, 0x000A, -0x0043,
    0x0438, -0x034B, 0x0196, -0x068B, 0x03C9, -0x0439, 0x0032, -0x0004,
    -0x07EE, 0x04E7, 0x07CB, -0x04C3, 0x0404, 0x023B, -0x023E, 0x01D5,
    -0x0831, 0x0100, -0x0001, 0x0100, -0x0000, 0x0000, 0x0000, 0x0000,
};

static const MAPI_S16 s16DefaultRGB[3][3] =
{
    { 1024, 0,    0 }, // R
    { 0,  1024,   0 }, // G
    { 0,   0,  1024 }  // B
};

// FIXME !! remove this later!
//DAC output bypass color matrix
static const MAPI_S16 S16DACColorCorrectionMatrix[32] =
{
    0x0400, 0x0000, 0x0000, 0x0000, 0x0400, 0x0000, 0x0000, 0x0000,
    0x0400, -0x02E6, 0x0288, -0x05BB, 0x07A4, -0x062C, 0x06F3, -0x073C,
    -0x0024, 0x01BF, 0x07EF, -0x0116, 0x01EE, 0x052C, -0x03BB, 0x00B1,
    -0x0831, 0x0100, -0x0000, 0x0000, -0x0000, 0x0000, 0x0000, 0x0000,
};

mapi_video::mapi_video()
{
    STATIC_ASSERT((int)mapi_video_datatype::DWIN_FMT_UV7Y8         == (int)DWIN_DATA_FMT_UV7Y8);
    STATIC_ASSERT((int)mapi_video_datatype::DWIN_FMT_UV8Y8         == (int)DWIN_DATA_FMT_UV8Y8);
    STATIC_ASSERT((int)mapi_video_datatype::DWIN_FMT_ARGB8888         == (int)DWIN_DATA_FMT_ARGB8888);
    STATIC_ASSERT((int)mapi_video_datatype::DWIN_FMT_RGB565         == (int)DWIN_DATA_FMT_RGB565);


    STATIC_ASSERT((int)mapi_video::E_3D_PANEL_NONE         == (int)E_XC_3D_PANEL_NONE);
    STATIC_ASSERT((int)mapi_video::E_3D_PANEL_SHUTTER      == (int)E_XC_3D_PANEL_SHUTTER);
    STATIC_ASSERT((int)mapi_video::E_3D_PANEL_PELLICLE     == (int)E_XC_3D_PANEL_PELLICLE);
    STATIC_ASSERT((int)mapi_video::E_3D_PANEL_MAX          == (int)E_XC_3D_PANEL_MAX);
#if (STEREO_3D_ENABLE == 1)
    m_st3DInfo.bEnable3D = FALSE;
    m_st3DInfo.enInput3DMode = mapi_video_datatype::E_3D_INPUT_MODE_NONE;
    m_st3DInfo.enOutput3DMode = mapi_video_datatype::E_3D_OUTPUT_MODE_NONE;
#endif

    m_bLRSwitch = MAPI_FALSE;
    m_u163DHShift = 0;
    m_bEnablePCmode = MAPI_FALSE;

#if (ENABLE_LITE_SN == 0)
#if (FRC_INSIDE_ENABLE == 1)
    mapi_video::FRC_3D_PANEL_TYPE = E_3D_PANEL_PELLICLE;
#endif
#endif

#ifdef UFO_XC_HDR
    memset(&m_HdrMetadata, 0, sizeof(ST_MAPI_HDR_METADATA));
    m_bHdrInitialized = MAPI_FALSE;
#endif

}

mapi_video::~mapi_video()
{
}

void mapi_video::SetOSDCOutputTiming(EN_MAPI_TIMING enTiming)
{
    OSDCDBGMSG("\n====================Set OSDC ====================\n");
    MS_XC_OSDC_CTRL_INFO tmp;
    memset(&tmp, 0, sizeof(MS_XC_OSDC_CTRL_INFO));

    // 1. get the information
    getOSDCinfo(&tmp);
#if (STB_ENABLE == 0 && MSTAR_TVOS == 1) // wait for K2,K3, eiffel utopia release
    if( LINK_HS_LVDS == tmp.u16OC_Lpll_type)
    {
        MApi_XC_OSDC_InitSetting(E_XC_OSDC_TGEN_1920x1080, NULL, &tmp);
    }
    else
    {
        if ((enTiming >= E_MAPI_TIMING_2K1K) && (enTiming < E_MAPI_TIMING_2K1K_MAX))
        {
            OSDCDBGMSG("\n OC Set to 2LANE (1920x1080)\n");
            MApi_XC_OSDC_InitSetting(E_XC_OSDC_TGEN_1920x1080, NULL, &tmp);
            OSDCDBGMSG("setup OSDC Vfreqx10 %u\n",600);
            MApi_XC_OSDC_SetOutVfreqx10(600);
        }
        else if ((enTiming >= E_MAPI_TIMING_4K1K) && (enTiming < E_MAPI_TIMING_4K1K_MAX))
        {
            // Add 4K1K OSDC setting
            OSDCDBGMSG("\n OC Set to 4LANE (3840x1080)\n");
            MApi_XC_OSDC_InitSetting(E_XC_OSDC_TGEN_3840x1080, NULL, &tmp);
            OSDCDBGMSG("setup OSDC Vfreqx10 %u\n",300);
            MApi_XC_OSDC_SetOutVfreqx10(300);
        }
        else if ((enTiming >= E_MAPI_TIMING_4K2K) && (enTiming < E_MAPI_TIMING_4K2K_MAX))
        {
            OSDCDBGMSG("\n OC Set to 4LANE (3840x2160)\n");
            MApi_XC_OSDC_InitSetting(E_XC_OSDC_TGEN_3840x2160, NULL, &tmp);
            OSDCDBGMSG("setup OSDC Vfreqx10 %u\n",300);
            MApi_XC_OSDC_SetOutVfreqx10(300);
        }
    }
#endif
    MApi_XC_OSDC_Control(E_XC_OSDC_INIT);

    OSDCDBGMSG("\n====================End OSDC====================\n");
}

void mapi_video::ResetOSDC(void)
{
    SetOSDC();
}
void mapi_video::getOSDCinfo(MS_XC_OSDC_CTRL_INFO* osdc)
{
    MAPI_OSDCType info;

    if (mapi_syscfg_fetch::GetInstance()->GetOSDCInfo(&info) == FALSE)
    {
        ERR_VIDEO(printf("Can not get the OSDC information!\n"));
        ASSERT(0);
    }

    osdc->bOC_ClK_En               = info.OC_ClK_En;
    osdc->bOC_Mixer_Bypass_En      = info.OC_Mixer_Bypass_En;    //0x1337,22h, bit0
    osdc->bOC_Mixer_InvAlpha_En    = info.OC_Mixer_InvAlpha_En;  //0x1337,22h, bit1
    osdc->bOC_Mixer_Hsync_Vfde_Out = info.OC_Mixer_Hsync_Vfde_Out;
    osdc->bOC_Mixer_Hfde_Vfde_Out  = info.OC_Mixer_Hfde_Vfde_Out;
#if (STB_ENABLE == 0 && MSTAR_TVOS == 1 ) // wait for K2,K3, eiffel utopia release
    osdc->u16OC_Lpll_type          = info.OC_Mixer_u16OC_Lpll_type;
#endif
#if 0 // wait utopia release
    osdc->u8OC_OutputFormat          = info.OC_OutputFormat;
#endif

    OSDCDBGMSG("Clk_En: %d\n",osdc->bOC_ClK_En);
    OSDCDBGMSG("bOC_Mixer_Bypass_En: %d\n",osdc->bOC_Mixer_Bypass_En);
    OSDCDBGMSG("bOC_Mixer_InvAlpha_En: %d\n",osdc->bOC_Mixer_InvAlpha_En);
    OSDCDBGMSG("bOC_Mixer_Hsync_Vfde_Out: %d\n",osdc->bOC_Mixer_Hsync_Vfde_Out);
    OSDCDBGMSG("bOC_Mixer_Hfde_Vfde_Out: %d\n",osdc->bOC_Mixer_Hfde_Vfde_Out);
#if (STB_ENABLE == 0 && MSTAR_TVOS == 1) // wait for K2,K3, eiffel utopia release
    OSDCDBGMSG("u16OC_Lpll_type: %d\n",osdc->u16OC_Lpll_type);
#endif
}
void mapi_video::SetOSDC(void)
{
    OSDCDBGMSG("\n====================Set OSDC ====================\n");
    MS_XC_OSDC_CTRL_INFO tmp;
    // 1. get the information
    getOSDCinfo(&tmp);
#if (STB_ENABLE == 0 && MSTAR_TVOS == 1) // wait for K2,K3, eiffel utopia release
    if( LINK_HS_LVDS == tmp.u16OC_Lpll_type)
    {
        MApi_XC_OSDC_InitSetting(E_XC_OSDC_TGEN_1920x1080, NULL, &tmp);
    }
    else if(LINK_VBY1_10BIT_2LANE == tmp.u16OC_Lpll_type)
    {
        OSDCDBGMSG("\n OC Set to 2LANE (1920x1080)\n");
        MApi_XC_OSDC_InitSetting(E_XC_OSDC_TGEN_1920x1080, NULL, &tmp);
        OSDCDBGMSG("setup OSDC Vfreqx10 %u\n",600);
        MApi_XC_OSDC_SetOutVfreqx10(600);
    }
    else if(LINK_VBY1_10BIT_4LANE == tmp.u16OC_Lpll_type)
    {
        OSDCDBGMSG("\n OC Set to 4LANE (3840x2160)\n");
        MApi_XC_OSDC_InitSetting(E_XC_OSDC_TGEN_3840x2160, NULL, &tmp);
        OSDCDBGMSG("setup OSDC Vfreqx10 %u\n",300);
        MApi_XC_OSDC_SetOutVfreqx10(300);
    }
#endif
    MApi_XC_OSDC_Control(E_XC_OSDC_INIT);

    OSDCDBGMSG("\n====================End OSDC====================\n");
}
void mapi_video::SysInitXC(void)
{
    XC_INITDATA sXC_InitData;
    XC_INITDATA *pstXC_InitData = &sXC_InitData;
    XC_INITMISC sXC_Init_Misc;

    memset(&sXC_InitData, 0, sizeof(sXC_InitData));
    memset(&sXC_Init_Misc, 0, sizeof(XC_INITMISC));

    // Turn on Immeswitch
    sXC_Init_Misc.u32MISC_A = (E_XC_INIT_MISC_A_IMMESWITCH );

    // Init XC
    pstXC_InitData->u32XTAL_Clock =  12000000UL;
    MMapInfo_t *minfo;
    minfo = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_XC_MAIN_FB"));

    if (minfo != NULL)
    {
        pstXC_InitData->u32Main_FB_Size         = minfo->u32Size;
        pstXC_InitData->u32Sub_FB_Size          = minfo->u32Size;
        pstXC_InitData->u32Main_FB_Start_Addr = minfo->u32Addr;
        pstXC_InitData->u32Sub_FB_Start_Addr = minfo->u32Addr;
    }
    else
    {
        pstXC_InitData->u32Main_FB_Start_Addr = 0;
        pstXC_InitData->u32Sub_FB_Start_Addr = 0;
    }


    {
        {
            minfo = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_XC_MAIN_FRCM_FB"));

            if (minfo != NULL)
            {
                pstXC_InitData->u32Main_FRCM_FB_Size = minfo->u32Size;
                pstXC_InitData->u32Sub_FRCM_FB_Size = minfo->u32Size;
                pstXC_InitData->u32Main_FRCM_FB_Start_Addr = minfo->u32Addr;
                pstXC_InitData->u32Sub_FRCM_FB_Start_Addr = minfo->u32Addr;
            }
            else
            {
                pstXC_InitData->u32Main_FRCM_FB_Start_Addr = 0;
                pstXC_InitData->u32Sub_FRCM_FB_Start_Addr = 0;
            }
        }
    }

#if (ENABLE_LITE_SN == 0)
#if (PIP_ENABLE == 1)
        // Get XC main buffer
        minfo = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_XC_SUB_FB"));

        if (minfo != NULL)
        {
            pstXC_InitData->u32Sub_FB_Size          = minfo->u32Size;
            pstXC_InitData->u32Sub_FB_Start_Addr    = minfo->u32Addr;
            MSG_VIDEO(printf("u32Sub_FB_Start_Addr: 0x%x, u32Sub_FB_Size: 0x%x \n", pstXC_InitData->u32Sub_FB_Start_Addr, pstXC_InitData->u32Sub_FB_Size));
        }
        else
        {
            ERR_VIDEO(printf("**** There is no sub window memory ~! **** \n"));
            //ASSERT(0);
        }
/*
        if (((pstXC_InitData->u32Main_FB_Start_Addr <= pstXC_InitData->u32Sub_FB_Start_Addr) &&  // Sub memory starts within main memory
             (pstXC_InitData->u32Sub_FB_Start_Addr < (pstXC_InitData->u32Main_FB_Start_Addr + pstXC_InitData->u32Main_FB_Size)))
          ||((pstXC_InitData->u32Sub_FB_Start_Addr <= pstXC_InitData->u32Main_FB_Start_Addr) &&  // Main memory starts within sub memory
             (pstXC_InitData->u32Main_FB_Start_Addr < (pstXC_InitData->u32Sub_FB_Start_Addr + pstXC_InitData->u32Sub_FB_Size))))
        {
            printf("**** The sub window memory is invalid ~! **** \n");
            ASSERT(0);
        }
*/

        {
            {
                minfo = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_XC_SUB_FRCM_FB"));

                if (minfo != NULL)
                {
                    pstXC_InitData->u32Sub_FRCM_FB_Size = minfo->u32Size;
                    pstXC_InitData->u32Sub_FRCM_FB_Start_Addr = minfo->u32Addr;
                }
                else
                {
                    ERR_VIDEO(printf("**** There is no sub window memory ~! **** \n"));
                }
            }
        }
#endif
#endif
    MSG_VIDEO(printf("pstXC_InitData->u32Main_FB_Start_Addr = 0x%x\n", pstXC_InitData->u32Main_FB_Start_Addr));
    MSG_VIDEO(printf("pstXC_InitData->u32Sub_FB_Size = 0x%x\n", pstXC_InitData->u32Sub_FB_Size));

    pstXC_InitData->bCEC_Use_Interrupt = MAPI_FALSE;

    pstXC_InitData->bEnableIPAutoCoast = ENABLE_IP_AUTO_COAST;

    MirrorMode_t enVideoMirror = (MirrorMode_t)mapi_syscfg_fetch::GetInstance()->getMirrorVideoMode();
    if (mapi_syscfg_fetch::GetInstance()->GetMirrorVideoFlag() &&
        ((enVideoMirror == MIRROR_HV) || (enVideoMirror == MIRROR_V_ONLY)))
    {
        pstXC_InitData->bMirror = ENABLE;
    }
    else
    {
        pstXC_InitData->bMirror = DISABLE;
    }

#if (SHARE_GROUND == 1)
    pstXC_InitData->bIsShareGround = MAPI_TRUE;
#endif
    // panel info
    pstXC_InitData->stPanelInfo.u16HStart = g_IPanel.HStart();      // DE H start
    pstXC_InitData->stPanelInfo.u16VStart = g_IPanel.VStart();
    pstXC_InitData->stPanelInfo.u16Width  = g_IPanel.Width();
    pstXC_InitData->stPanelInfo.u16Height = g_IPanel.Height();
    pstXC_InitData->stPanelInfo.u16HTotal = g_IPanel.HTotal();
    pstXC_InitData->stPanelInfo.u16VTotal = g_IPanel.VTotal();

    pstXC_InitData->stPanelInfo.u16DefaultVFreq = g_IPanel.DefaultVFreq();

    pstXC_InitData->stPanelInfo.u8LPLL_Mode = g_IPanel.LPLL_Mode();

    //disable double clk
    const PanelInfo_t* const pPanelInfo = mapi_syscfg_fetch::GetInstance()->GetActivePanel();
    ASSERT(pPanelInfo);

    PanelType* pPnl = (PanelType*)(*pPanelInfo).PanelAttr;
    ASSERT(pPnl);

    if(NULL == pPnl)
    {
        ERR_VIDEO(printf("pPnl is NULL\n"));
        return ;
    }

    pstXC_InitData->stPanelInfo.eLPLL_Type = (E_XC_PNL_LPLL_TYPE)g_IPanel.LPLL_Type();

    pstXC_InitData->stPanelInfo.enPnl_Out_Timing_Mode = (E_XC_PNL_OUT_TIMING_MODE)g_IPanel.OutTimingMode();

#if 1
    pstXC_InitData->stPanelInfo.u16DefaultHTotal = g_IPanel.HTotal();
    pstXC_InitData->stPanelInfo.u16DefaultVTotal = g_IPanel.VTotal();

    pstXC_InitData->stPanelInfo.u32MinSET = g_IPanel.MinSET();
    MSG_VIDEO(printf("pstXC_InitData->stPanelInfo.u32MinSET = 0x%x\n", pstXC_InitData->stPanelInfo.u32MinSET));

    pstXC_InitData->stPanelInfo.u32MaxSET = g_IPanel.MaxSET();
    MSG_VIDEO(printf("pstXC_InitData->stPanelInfo.u32MaxSET = 0x%x\n", pstXC_InitData->stPanelInfo.u32MaxSET));
#else
    pstXC_InitData->stPanelInfo.u16MinHTotal = g_IPanel.MinHTotal();
    pstXC_InitData->stPanelInfo.u16DefaultHTotal = g_IPanel.DefaultHTotal();
    pstXC_InitData->stPanelInfo.u16MaxHTotal = g_IPanel.MaxtHTotal();
    pstXC_InitData->stPanelInfo.u16MinVTotal = g_IPanel.MinVTotal();
    pstXC_InitData->stPanelInfo.u16DefaultVTotal = g_IPanel.DefaultVTotal();
    pstXC_InitData->stPanelInfo.u16MaxVTotal = g_IPanel.MaxVTotal();
    pstXC_InitData->stPanelInfo.u32MinDCLK = g_IPanel.MinDCLK();
    pstXC_InitData->stPanelInfo.u32MaxDCLK = g_IPanel.MaxDCLK();
#endif

    pstXC_InitData->bDLC_Histogram_From_VBlank = MAPI_FALSE;

#if (ENABLE_LITE_SN == 0)
    if(mapi_syscfg_fetch::GetInstance()->GetFrcMode() == TRUE)
    {
        if ( MApi_XC_GetCapability(E_XC_SUPPORT_FRC_INSIDE))
        {
            sXC_Init_Misc.u32MISC_A |= E_XC_INIT_MISC_A_FRC_INSIDE;
        }

        if(sXC_Init_Misc.u32MISC_A & E_XC_INIT_MISC_A_FRC_INSIDE)
        {
            XC_PREINIT_INFO_t stXC_PANEL_INFO_ADV;
            memset(&stXC_PANEL_INFO_ADV, 0, sizeof(XC_PREINIT_INFO_t));


            stXC_PANEL_INFO_ADV.u8PanelHSyncWidth       = g_IPanel.HSynWidth();
            stXC_PANEL_INFO_ADV.u8PanelHSyncBackPorch  = g_IPanel.HSynBackPorch();
            stXC_PANEL_INFO_ADV.u8PanelVSyncWidth       =  pPnl->m_ucPanelVSyncWidth;
            stXC_PANEL_INFO_ADV.u8PanelVSyncBackPorch  = g_IPanel.VSynBackPorch();
            stXC_PANEL_INFO_ADV.u16VTrigX               = 0x82F;
            stXC_PANEL_INFO_ADV.u16VTrigY               = (g_IPanel.VStart()+g_IPanel.Height()+12)%(pPnl->m_wPanelVTotal)+1;//0x45B;

            MMapInfo_t* pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_FRC_MEMC"));
            if (pMMap != NULL)
            {
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_MEMC_MemAddr = pMMap->u32Addr;
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_MEMC_MemSize = pMMap->u32Size;
            }
            pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_FRC_OD"));
            if (pMMap != NULL)
            {
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_OD_MemAddr = pMMap->u32Addr;
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_OD_MemSize = pMMap->u32Size;
            }
            pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_FRC_LD"));
            if (pMMap != NULL)
            {
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_LD_MemAddr = pMMap->u32Addr;
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_LD_MemSize = pMMap->u32Size;
            }
            pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_FRC_ME1"));
            if (pMMap != NULL)
            {
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_ME1_MemAddr = pMMap->u32Addr;
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_ME1_MemSize = pMMap->u32Size;
            }
            pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_FRC_ME2"));
            if (pMMap != NULL)
            {
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_ME2_MemAddr = pMMap->u32Addr;
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_ME2_MemSize = pMMap->u32Size;
            }
            pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_FRC_2D3D_Render"));
            if (pMMap != NULL)
            {
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_2D3D_Render_MemAddr = pMMap->u32Addr;
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_2D3D_Render_MemSize = pMMap->u32Size;
            }
            pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_FRC_2D3D_Render_Detection"));
            if (pMMap != NULL)
            {
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_2D3D_Render_Detection_MemAddr = pMMap->u32Addr;
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_2D3D_Render_Detection_MemSize = pMMap->u32Size;
            }
            pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_FRC_Halo"));
            if (pMMap != NULL)
            {
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_Halo_MemAddr = pMMap->u32Addr;
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_Halo_MemSize = pMMap->u32Size;
            }
            pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_FRC_R2"));
            if (pMMap != NULL)
            {
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_R2_MemAddr = pMMap->u32Addr;
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_R2_MemSize = pMMap->u32Size;
            }

            pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_FRC_4K2K"));
            if (pMMap != NULL)
            {
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_MEMC_MemAddr = pMMap->u32Addr;
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_MEMC_MemSize = pMMap->u32Size;
            }

            stXC_PANEL_INFO_ADV.FRCInfo.u16FB_YcountLinePitch    = 0x00;
            stXC_PANEL_INFO_ADV.FRCInfo.u16PanelWidth            = g_IPanel.Width();
            stXC_PANEL_INFO_ADV.FRCInfo.u16PanelHeigh            = g_IPanel.Height();
            stXC_PANEL_INFO_ADV.FRCInfo.u8FRC3DPanelType         = mapi_video::FRC_3D_PANEL_TYPE;
            if(FRC_BYPASS_MODE)
            {
                stXC_PANEL_INFO_ADV.FRCInfo.bFRC                 = 0; // TRUE: Normal; FALSE: Bypass
            }
            else
            {
                stXC_PANEL_INFO_ADV.FRCInfo.bFRC                 = 1; // TRUE: Normal; FALSE: Bypass
            }
            stXC_PANEL_INFO_ADV.FRCInfo.u83Dmode                 = 0x00;
            stXC_PANEL_INFO_ADV.FRCInfo.u8IpMode                 = 0x00;
            stXC_PANEL_INFO_ADV.FRCInfo.u8MirrorMode             = 0x00;
            stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_FrameSize         = 0x00;
            stXC_PANEL_INFO_ADV.FRCInfo.u83D_FI_out              = 0x00;

            MApi_XC_PreInit( E_XC_PREINIT_FRC, &stXC_PANEL_INFO_ADV, sizeof(XC_PREINIT_INFO_t) );
    //        pstXC_InitData->stPanelInfo.u8LPLL_Mode = MOD_OUTPUT_MODE; // Quad mode
        }
    }
    else
    {
#if (FRC_INSIDE_ENABLE == 1)
        if (MApi_XC_GetCapability(E_XC_SUPPORT_FRC_INSIDE))
        {
            sXC_Init_Misc.u32MISC_A |= E_XC_INIT_MISC_A_FRC_INSIDE;
        }

        if(sXC_Init_Misc.u32MISC_A & E_XC_INIT_MISC_A_FRC_INSIDE)
        {
            XC_PREINIT_INFO_t stXC_PANEL_INFO_ADV;
            memset(&stXC_PANEL_INFO_ADV, 0, sizeof(XC_PREINIT_INFO_t));


            stXC_PANEL_INFO_ADV.u8PanelHSyncWidth       = g_IPanel.HSynWidth();
            stXC_PANEL_INFO_ADV.u8PanelHSyncBackPorch  = g_IPanel.HSynBackPorch();
            stXC_PANEL_INFO_ADV.u8PanelVSyncWidth       =  pPnl->m_ucPanelVSyncWidth;
            stXC_PANEL_INFO_ADV.u8PanelVSyncBackPorch  = g_IPanel.VSynBackPorch();
            stXC_PANEL_INFO_ADV.u16VTrigX               = 0x82F;
            stXC_PANEL_INFO_ADV.u16VTrigY               = (g_IPanel.VStart()+g_IPanel.Height()+12)%(pPnl->m_wPanelVTotal)+1;//0x45B;

            MMapInfo_t* pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_FRC_MEMC"));
            if (pMMap != NULL)
            {
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_MEMC_MemAddr = pMMap->u32Addr;
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_MEMC_MemSize = pMMap->u32Size;
            }
            pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_FRC_OD"));
            if (pMMap != NULL)
            {
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_OD_MemAddr = pMMap->u32Addr;
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_OD_MemSize = pMMap->u32Size;
            }
            pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_FRC_LD"));
            if (pMMap != NULL)
            {
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_LD_MemAddr = pMMap->u32Addr;
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_LD_MemSize = pMMap->u32Size;
            }
            pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_FRC_ME1"));
            if (pMMap != NULL)
            {
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_ME1_MemAddr = pMMap->u32Addr;
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_ME1_MemSize = pMMap->u32Size;
            }
            pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_FRC_ME2"));
            if (pMMap != NULL)
            {
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_ME2_MemAddr = pMMap->u32Addr;
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_ME2_MemSize = pMMap->u32Size;
            }
            pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_FRC_2D3D_Render"));
            if (pMMap != NULL)
            {
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_2D3D_Render_MemAddr = pMMap->u32Addr;
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_2D3D_Render_MemSize = pMMap->u32Size;
            }
            pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_FRC_2D3D_Render_Detection"));
            if (pMMap != NULL)
            {
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_2D3D_Render_Detection_MemAddr = pMMap->u32Addr;
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_2D3D_Render_Detection_MemSize = pMMap->u32Size;
            }
            pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_FRC_Halo"));
            if (pMMap != NULL)
            {
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_Halo_MemAddr = pMMap->u32Addr;
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_Halo_MemSize = pMMap->u32Size;
            }
            pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_FRC_R2"));
            if (pMMap != NULL)
            {
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_R2_MemAddr = pMMap->u32Addr;
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_R2_MemSize = pMMap->u32Size;
            }

            pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_FRC_4K2K"));
            if (pMMap != NULL)
            {
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_MEMC_MemAddr = pMMap->u32Addr;
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_MEMC_MemSize = pMMap->u32Size;
            }

            stXC_PANEL_INFO_ADV.FRCInfo.u16FB_YcountLinePitch    = 0x00;
            stXC_PANEL_INFO_ADV.FRCInfo.u16PanelWidth            = g_IPanel.Width();
            stXC_PANEL_INFO_ADV.FRCInfo.u16PanelHeigh            = g_IPanel.Height();
            stXC_PANEL_INFO_ADV.FRCInfo.u8FRC3DPanelType         = mapi_video::FRC_3D_PANEL_TYPE;
            if(FRC_BYPASS_MODE)
            {
                stXC_PANEL_INFO_ADV.FRCInfo.bFRC                 = 0; // TRUE: Normal; FALSE: Bypass
            }
            else
            {
                stXC_PANEL_INFO_ADV.FRCInfo.bFRC                 = 1; // TRUE: Normal; FALSE: Bypass
            }
            stXC_PANEL_INFO_ADV.FRCInfo.u83Dmode                 = 0x00;
            stXC_PANEL_INFO_ADV.FRCInfo.u8IpMode                 = 0x00;
            stXC_PANEL_INFO_ADV.FRCInfo.u8MirrorMode             = 0x00;
            stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_FrameSize         = 0x00;
            stXC_PANEL_INFO_ADV.FRCInfo.u83D_FI_out              = 0x00;

            MApi_XC_PreInit( E_XC_PREINIT_FRC, &stXC_PANEL_INFO_ADV, sizeof(XC_PREINIT_INFO_t) );
            //pstXC_InitData->stPanelInfo.u8LPLL_Mode = MOD_OUTPUT_MODE; // Quad mode
        }
#endif
    }
#endif

    MMapInfo_t* pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_XC_FRC_L"));
    if (pMMap != NULL)
    {
        if (MApi_XC_GetCapability(E_XC_SUPPORT_FRC_INSIDE))
        {
            sXC_Init_Misc.u32MISC_A |= E_XC_INIT_MISC_A_FRC_INSIDE;
        }

        if(sXC_Init_Misc.u32MISC_A & E_XC_INIT_MISC_A_FRC_INSIDE)
        {
            XC_PREINIT_INFO_t stXC_PANEL_INFO_ADV;
            memset(&stXC_PANEL_INFO_ADV, 0, sizeof(XC_PREINIT_INFO_t));
            stXC_PANEL_INFO_ADV.u8PanelHSyncWidth = g_IPanel.HSynWidth();
            stXC_PANEL_INFO_ADV.u8PanelHSyncBackPorch = g_IPanel.HSynBackPorch();
            stXC_PANEL_INFO_ADV.u8PanelVSyncWidth = pPnl->m_ucPanelVSyncWidth;
            stXC_PANEL_INFO_ADV.u8PanelVSyncBackPorch = g_IPanel.VSynBackPorch();
            stXC_PANEL_INFO_ADV.FRCInfo.u16PanelWidth = g_IPanel.Width();
            stXC_PANEL_INFO_ADV.FRCInfo.u16PanelHeigh = g_IPanel.Height();
            if(FRC_BYPASS_MODE)
            {
                stXC_PANEL_INFO_ADV.FRCInfo.bFRC = 0; // TRUE: Normal; FALSE: Bypass
            }
            else
            {
                stXC_PANEL_INFO_ADV.FRCInfo.bFRC = 1; // TRUE: Normal; FALSE: Bypass
            }

            pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_XC_FRC_L"));
            if (pMMap != NULL)
            {
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_MEMC_MemAddr = pMMap->u32Addr;
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_MEMC_MemSize = pMMap->u32Size;
            }
            pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_XC_FRC_PQ"));
            if (pMMap != NULL)
            {
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_ME1_MemAddr = pMMap->u32Addr;
                stXC_PANEL_INFO_ADV.FRCInfo.u32FRC_ME1_MemSize = pMMap->u32Size;
            }

            MApi_XC_PreInit(E_XC_PREINIT_FRC, &stXC_PANEL_INFO_ADV, sizeof(XC_PREINIT_INFO_t));
        }
    }

#ifdef UFO_SET_XC_CMA_INFORMATION
#if (ENABLE_CMA == 1)
    XC_CMA_CONFIG stCmaConfig;
    memset(&stCmaConfig, 0, sizeof(XC_CMA_CONFIG));

    pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_XC_SELF"));
    if (pMMap == NULL)
    {
        stCmaConfig.u32HeapID = XC_INVALID_HEAP_ID;
        stCmaConfig.u64AddrHeapOffset = 0;
        printf("\033[31m[%s][%s][%d] Can't get E_MMAP_ID_XC_SELF_CMA_HID.\033[m\n", __FILE__, __func__, __LINE__);
    }
    else
    {
        printf("\033[31m[%s][%s][%d] E_MMAP_ID_XC_SELF_CMA_HID is %d.\033[m\n", __FILE__, __func__, __LINE__, pMMap->u32CMAHid);
        stCmaConfig.u32HeapID = pMMap->u32CMAHid;
        stCmaConfig.u64AddrHeapOffset = 0;
    }
    MApi_XC_ConfigCMA(&stCmaConfig, CMA_XC_SELF_MEM, sizeof(XC_CMA_CONFIG), MAIN_WINDOW);

    pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_XC_COBUFFER"));
    if (pMMap == NULL)
    {
        stCmaConfig.u32HeapID = XC_INVALID_HEAP_ID;
        stCmaConfig.u64AddrHeapOffset = 0;
        printf("\033[31m[%s][%s][%d] Can't get E_MMAP_ID_XC_COBUFFER_CMA_HID.\033[m\n", __FILE__, __func__, __LINE__);
    }
    else
    {
        printf("\033[31m[%s][%s][%d] E_MMAP_ID_XC_COBUFFER_CMA_HID is %d.\033[m\n", __FILE__, __func__, __LINE__, pMMap->u32CMAHid);
        stCmaConfig.u32HeapID = pMMap->u32CMAHid;
        stCmaConfig.u64AddrHeapOffset = 0;
    }
    MApi_XC_ConfigCMA(&stCmaConfig, CMA_XC_COBUFF_MEM, sizeof(XC_CMA_CONFIG), MAIN_WINDOW);
#endif
#endif

    if(MApi_XC_Init(pstXC_InitData, sizeof(XC_INITDATA)) == MAPI_FALSE)
    {
        ERR_VIDEO(printf("XC_Init failed because of InitData wrong, please update header file and compile again\n"));
    }
#if (ENABLE_BACKEND == 1)
    int eUrsaType = E_URSA_NONE;
    int eUrsaConnectType = 0;
    mapi_syscfg_fetch::GetInstance()->GetModuleParameter_int("M_URSA:F_URSA_URSA_TYPE", &eUrsaType, 0);
    mapi_syscfg_fetch::GetInstance()->GetModuleParameter_int("M_URSA:F_URSA_DIRECT_CONNECT", &eUrsaConnectType, 0);
    if ((eUrsaType != E_URSA_NONE) &&(eUrsaConnectType == 1))
    {
        if(LINK_EXT == pPnl->m_ePanelLinkType)
        {
            switch((*pPanelInfo).u16PanelLinkExtType)
            {
            case LINK_VBY1_10BIT_4LANE:
            case LINK_VBY1_10BIT_2LANE:
            case LINK_VBY1_10BIT_1LANE:
            case LINK_VBY1_8BIT_4LANE:
            case LINK_VBY1_8BIT_2LANE:
            case LINK_VBY1_8BIT_1LANE:
            case LINK_VBY1_10BIT_8LANE:
            case LINK_VBY1_8BIT_8LANE:
                /* select vb1 only */
                /* set vsync step when timing change. */
                MApi_XC_FPLLCustomerMode(E_FPLL_MODE_ENABLE, E_FPLL_FLAG_PHASELIMIT, 0x600);
                break;
            default:
                break;
            }
        }
    }
#endif
    //Use HV mode to detection 720P@30hz for HDMI/DVI, default enable
    // control by new class mapi_video_customer_base
    // MApi_XC_SetHdmiSyncMode(HDMI_SYNC_HV);
    // control by new class mapi_video_customer_base
    // MApi_XC_ADC_Set_YPbPrLooseLPF(TRUE);

#if (HDMITX_ENABLE == 1)
    sXC_Init_Misc.u32MISC_A |= E_XC_INIT_MISC_A_SKIP_VIP_PEAKING_CONTROL;
#endif
#if (ENABLE_XC_LITE_MODE == 1)
    sXC_Init_Misc.u32MISC_A |= E_XC_INIT_MISC_A_SAVE_MEM_MODE;
#endif

    if(MApi_XC_Init_MISC(&sXC_Init_Misc, sizeof(XC_INITMISC)) == FALSE)
    {
        ERR_VIDEO(printf("L:%d, XC Init MISC failed because of InitData wrong, please update header file and compile again\n", __LINE__));
    }

    MApi_XC_EnableFrameBufferLess(FALSE);

    if(pstXC_InitData->u32Main_FB_Size == 0)
    {
        ERR_VIDEO(printf("Function:%s L:%d, XC memory size should not be 0!\n", __FUNCTION__, __LINE__));
    }

    // *********************************
    // Enable/Disable MCDE ME1 & MCDE ME2
    // *********************************
    MMapInfo_t* pMMap_MCDI_ME1 = NULL;
    MAPI_U32 u32MCDI_ME1_MemAddr = 0;
    MAPI_U32 u32MCDI_ME1_MemSize = 0;
    MAPI_BOOL bMCDI_ME1_Enable = FALSE;
    MMapInfo_t* pMMap_MCDI_ME2 = NULL;
    MAPI_U32 u32MCDI_ME2_MemAddr = 0;
    MAPI_U32 u32MCDI_ME2_MemSize = 0;
    MAPI_BOOL bMCDI_ME2_Enable = FALSE;

    // Get MCDE ME1 memory address & size from mmap and set values to XC driver
    pMMap_MCDI_ME1 = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_XC_MCDI_ME1"));
    if (pMMap_MCDI_ME1 != NULL)
    {
        u32MCDI_ME1_MemAddr = pMMap_MCDI_ME1->u32Addr;
        u32MCDI_ME1_MemSize = pMMap_MCDI_ME1->u32Size;

        if(u32MCDI_ME1_MemSize !=0)
        {
            MApi_XC_SetMCDIBufferAddress(u32MCDI_ME1_MemAddr, u32MCDI_ME1_MemSize, E_XC_MCDI_ME1);
            bMCDI_ME1_Enable = TRUE;
        }
        else
        {
            bMCDI_ME1_Enable = FALSE;
        }
    }
    else
    {
        bMCDI_ME1_Enable = FALSE;
    }

    // Get MCDE ME2 memory address & size from mmap and set values to XC driver
    pMMap_MCDI_ME2 = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_XC_MCDI_ME2"));
    if (pMMap_MCDI_ME2 != NULL)
    {
        u32MCDI_ME2_MemAddr = pMMap_MCDI_ME2->u32Addr;
        u32MCDI_ME2_MemSize = pMMap_MCDI_ME2->u32Size;

        if(u32MCDI_ME2_MemSize !=0)
        {
            MApi_XC_SetMCDIBufferAddress(u32MCDI_ME2_MemAddr, u32MCDI_ME2_MemSize, E_XC_MCDI_ME2);
            bMCDI_ME2_Enable = TRUE;
        }
        else
        {
            bMCDI_ME2_Enable = FALSE;
        }
    }
    else
    {
        bMCDI_ME2_Enable = FALSE;
    }

    // Enable/Disable both of MCDI ME1 and MCDI ME2
    if((bMCDI_ME1_Enable == TRUE) && (bMCDI_ME2_Enable == TRUE))
    {
        MApi_XC_EnableMCDI(TRUE, E_XC_MCDI_BOTH);
        MApi_XC_EnableMCDI(TRUE, E_XC_MCDI_SUB_BOTH);
    }
    else if((bMCDI_ME1_Enable == FALSE) && (bMCDI_ME2_Enable == FALSE))
    {
        MApi_XC_EnableMCDI(FALSE, E_XC_MCDI_BOTH);
        MApi_XC_EnableMCDI(FALSE, E_XC_MCDI_SUB_BOTH);
    }
    else
    {
        ERR_VIDEO(printf("**** MCDI ME1 / ME2  memory is invalid ~! **** \n"));
        ASSERT(0);
    }

    // enable MENU load
    minfo = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_XC_MLOAD"));
    if (minfo != NULL)
    {
        MApi_XC_MLoad_Init(minfo->u32Addr, minfo->u32Size);
    }

    MApi_XC_MLoad_Enable(TRUE);
    ////////
#if (STEREO_3D_ENABLE == 1)
        if(MApi_XC_Get_3D_IsSupportedHW2DTo3D())
        {
            MMapInfo_t* pMMapDD = NULL;
            MMapInfo_t* pMMapDR = NULL;
            MAPI_U32 u32Addr_DD = 0;
            MAPI_U32 u32Addr_DR = 0;
            //get 2d to 3d dd,dr buffer
            pMMapDD = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_XC_2DTO3D_DD_BUF"));
            pMMapDR = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_XC_2DTO3D_DR_BUF"));
            if(pMMapDD && pMMapDR)
            {
                u32Addr_DD = pMMapDD->u32Addr;
                u32Addr_DR = pMMapDR->u32Addr;
                MApi_XC_Set_3D_HW2DTo3D_Buffer(u32Addr_DD, u32Addr_DR);
            }
            else
            {
                ERR_VIDEO(printf("Attention! should allocate memory for hw 2d to 3d or auto detect 3d\n"));
            }
        }

        MApi_XC_3D_Enable_Skip_Default_LR_Flag(TRUE);
#endif
    switch ( mapi_syscfg_fetch::GetInstance()->getMirrorVideoMode() )
    {
        case MIRROR_HV:
        {
            // Video HV Morrir
            MApi_XC_EnableMirrorModeEx(MIRROR_HV, MAIN_WINDOW);
            MApi_XC_EnableMirrorModeEx(MIRROR_HV, SUB_WINDOW);
        }
        break;
        case MIRROR_H_ONLY:
        {
            // Video H Morrir
            MApi_XC_EnableMirrorModeEx(MIRROR_H_ONLY, MAIN_WINDOW);
            MApi_XC_EnableMirrorModeEx(MIRROR_H_ONLY, SUB_WINDOW);
        }
        break;
        case MIRROR_V_ONLY:
        {
            // Video V Morrir
            MApi_XC_EnableMirrorModeEx(MIRROR_V_ONLY, MAIN_WINDOW);
            MApi_XC_EnableMirrorModeEx(MIRROR_V_ONLY, SUB_WINDOW);
        }
        break;
        default:
            //MIRROR_NORMAL don't need to set Morrir
        break;
    }//end of switch(getMirrorVideoFlagEX)
    // control by new class mapi_video_customer_base
    // MApi_XC_EnableAutoDetect3D(TRUE, E_XC_3D_AUTODETECT_HW_COMPATIBLE);

    ///////////////////////////Local dimming  initial start///////////////////////////
#if (ENABLE_LITE_SN == 0)
#if (LOCAL_DIMMING == 1)
        if (mapi_syscfg_fetch::GetInstance()->GetLocalDIMMINGFlag())// flags
        {
            EN_LD_PANEL_TYPE enLDPaneltype = E_LD_PANEL_DEFAULT;
            minfo = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_LOCAL_DIMMING"));
            MMAP_U32 m_u32Miu_interval = MMAPInfo::GetInstance(E_MMAP_Original)->Get_MIU_INTERVAL();
            MMAP_U32 m_u32Miu_interval2 = MMAPInfo::GetInstance(E_MMAP_Original)->Get_MIU_INTERVAL2();
            MAPI_U32 u32Addr=0;
            if(minfo->u32MiuNo==1)
            {
                u32Addr=minfo->u32Addr-m_u32Miu_interval;
            }
            else if(minfo->u32MiuNo==2)
            {
                u32Addr=minfo->u32Addr-m_u32Miu_interval2;
            }

            if(minfo != NULL)
            {
                if(!MsOS_MPool_Mapping((minfo->u32MiuNo), u32Addr,  minfo->u32Size, 1))
                {
                    printf("Local Diming Mpool mapping error************************\n");
                    return ;
                }
                MApi_XC_LD_SetMemoryAddress((minfo->u32MiuNo),
                                            minfo->u32Addr,(minfo->u32Addr+0x1000),(minfo->u32Addr+0x2000),(minfo->u32Addr+0x2000),(minfo->u32Addr+0x5000),0x2000);
                switch(mapi_syscfg_fetch::GetInstance()->GetLocalDIMMINGPanelSelect())
                {
                    case 0:
                        enLDPaneltype= E_LD_PANEL_LG32inch_LR10 ;
                        break;
                    case 0x1:
                        enLDPaneltype= E_LD_PANEL_LG37inch_LR10 ;
                        break;
                    case 0x02:
                        enLDPaneltype=E_LD_PANEL_LG42inch_LR16 ;
                        break;
                    case 0x03:
                        enLDPaneltype=E_LD_PANEL_LG47inch_LR16;
                        break;
                    case 0x4:
                        enLDPaneltype=E_LD_PANEL_LG55inch_LR16 ;
                        break;
                    case 0x5:
                        enLDPaneltype=E_LD_PANEL_LG55inch_LR12 ;
                        break;
                    case 0x6:
                        enLDPaneltype=E_LD_PANEL_CMO42inch_LR16;
                        break;
                    default:
                        enLDPaneltype= E_LD_PANEL_DEFAULT;
                        break;
                }
                MApi_XC_LD_Init(enLDPaneltype);
                // MApi_XC_LD_SetLevel(E_LD_MODE_HIGH);
            }
            else
            {
        mapi_syscfg_fetch::GetInstance()->SetLocalDIMMINGFlag(FALSE);
    }
        }
#endif
#endif

#if (HDMITX_ENABLE == 1)
    /* Set default color value to main and sub window */
    MApi_XC_SetDispWindowColor(0x82, MAIN_WINDOW);
    MApi_XC_SetDispWindowColor(0x82, SUB_WINDOW);
#endif

    ///////////////////////////Local dimming  initial end ///////////////////////////

    ///////////////////////////mapi_video_customer_base initial start ///////////////////////////
    {
        ST_VIDEO_CUS_COMPILER_FLAG stcomflag;
        memset(&stcomflag, 0, sizeof(ST_VIDEO_CUS_COMPILER_FLAG));
#if (ENABLE_LITE_SN == 0)
#if (PIP_ENABLE == 1)
            stcomflag.bpip       = MAPI_TRUE;
#endif
#endif
#if (STEREO_3D_ENABLE == 1)
            stcomflag.bstereo3d  = MAPI_TRUE;
#endif
#if (ENABLE_LITE_SN == 0)
#if (PWS_ENABLE == 1)
            stcomflag.bpws       = MAPI_TRUE;
#endif
#endif

#if (VE_ENABLE == 1)
            stcomflag.bve        = MAPI_TRUE;
#endif
#if (ENABLE_LITE_SN == 0)
#if (MSTAR_TVOS == 1)
            stcomflag.bmstartvos = MAPI_TRUE;
#endif
#endif
#if (ENABLE_LITE_SN == 0)
#if (A3_STB_ENABLE == 1)
            stcomflag.ba3stb     = MAPI_TRUE;
#endif
#endif
        mapi_video_customer_base::GetInstance()->setOptionalfunction(stcomflag);
    }
    ///////////////////////////mapi_video_customer_base initial end   ///////////////////////////
}

#if (STB_ENABLE == 1)
#if (DUAL_XC_ENABLE == 1)
static void _MApi_XC_Sys_InputSource_InputPort_MappingEx(XC_EX_INPUT_SOURCE_TYPE src_ids , E_MUX_INPUTPORT* port_ids , MS_U8* u8port_count)
{
    switch (src_ids)
    {
        default:
            *u8port_count = 0;
            port_ids[0] = INPUT_PORT_MVOP;
            return;
        case E_XC_EX_INPUT_SOURCE_DTV:
            *u8port_count = 1;
            port_ids[0] = INPUT_PORT_MVOP;
            break;
        case E_XC_EX_INPUT_SOURCE_DTV2:
            *u8port_count = 1;
            port_ids[0] = INPUT_PORT_MVOP2;
            break;
        case E_XC_EX_INPUT_SOURCE_HDMI:
            *u8port_count = 1;
            port_ids[0] = INPUT_PORT_DVI0;
            break;
        case E_XC_EX_INPUT_SOURCE_SCALER_OP:
            *u8port_count = 1;
            port_ids[0] = INPUT_PORT_SCALER_OP;
                break;
        case E_XC_EX_INPUT_SOURCE_STORAGE:
            *u8port_count = 1;
            port_ids[0] = INPUT_PORT_MVOP;
                break;
    }
}

void mapi_video::SysInitXCEx(void)
{
    XC_EX_INITDATA sXC_InitData;
    XC_EX_INITDATA *pstXC_InitData = &sXC_InitData;

    XC_DEVICE_ID stXC_DeviceId = {E_DEVICE_VERSION_0, E_DEVICE_ID_1};
    PNL_DeviceId stPNL_DeviceId = {E_DEVICE_VERSION_0, E_DEVICE_ID_1};

    memset(&sXC_InitData, 0, sizeof(sXC_InitData));

    // Init XC1
    MApi_XC_EX_Mux_Init(&stXC_DeviceId, _MApi_XC_Sys_InputSource_InputPort_MappingEx);

    pstXC_InitData->u32XTAL_Clock =  12000000UL;
    MMapInfo_t *minfo;
    minfo = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_VE"));

    if (minfo != NULL)
    {
        pstXC_InitData->u32Main_FB_Size         = minfo->u32Size;
        pstXC_InitData->u32Sub_FB_Size          = minfo->u32Size;
        pstXC_InitData->u32Main_FB_Start_Addr   = minfo->u32Addr;
        pstXC_InitData->u32Sub_FB_Start_Addr    = minfo->u32Addr;
    }
    else
    {
        pstXC_InitData->u32Main_FB_Start_Addr = 0;
        pstXC_InitData->u32Sub_FB_Start_Addr = 0;
    }

#if (ENABLE_LITE_SN == 0)
#if (PIP_ENABLE == 1)
        // Get XC main buffer
        minfo = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_VE)"));

        if (minfo != NULL)
        {
            pstXC_InitData->u32Sub_FB_Size          = minfo->u32Size;
            pstXC_InitData->u32Sub_FB_Start_Addr    = minfo->u32Addr;
            MSG_VIDEO(printf("u32Sub_FB_Start_Addr: 0x%x, u32Sub_FB_Size: 0x%x \n", pstXC_InitData->u32Sub_FB_Start_Addr, pstXC_InitData->u32Sub_FB_Size));
        }
        else
        {
            ERR_VIDEO(printf("**** There is no sub window memory ~! **** \n"));
        }
#endif
#endif

    MSG_VIDEO(printf("pstXC_InitData->u32Main_FB_Start_Addr = 0x%x\n", pstXC_InitData->u32Main_FB_Start_Addr));
    MSG_VIDEO(printf("pstXC_InitData->u32Sub_FB_Size = 0x%x\n", pstXC_InitData->u32Sub_FB_Size));

    pstXC_InitData->bCEC_Use_Interrupt = MAPI_FALSE;

    pstXC_InitData->bEnableIPAutoCoast = ENABLE_IP_AUTO_COAST;

    if (mapi_syscfg_fetch::GetInstance()->GetMirrorVideoFlag())
    {
        pstXC_InitData->bMirror = ENABLE;
    }
    else
    {
        pstXC_InitData->bMirror = DISABLE;
    }

#if (SHARE_GROUND == 1)
    pstXC_InitData->bIsShareGround = MAPI_TRUE;
#endif

    // panel info
    pstXC_InitData->stPanelInfo.u16HStart = g_IPanelEx.HStart(&stPNL_DeviceId);      // DE H start
    pstXC_InitData->stPanelInfo.u16VStart = g_IPanelEx.VStart(&stPNL_DeviceId);
    pstXC_InitData->stPanelInfo.u16Width  = g_IPanelEx.Width(&stPNL_DeviceId);
    pstXC_InitData->stPanelInfo.u16Height = g_IPanelEx.Height(&stPNL_DeviceId);
    pstXC_InitData->stPanelInfo.u16HTotal = g_IPanelEx.HTotal(&stPNL_DeviceId);
    pstXC_InitData->stPanelInfo.u16VTotal = g_IPanelEx.VTotal(&stPNL_DeviceId);
    pstXC_InitData->stPanelInfo.u16DefaultVFreq = g_IPanelEx.DefaultVFreq(&stPNL_DeviceId);
    pstXC_InitData->stPanelInfo.u8LPLL_Mode = g_IPanelEx.LPLL_Mode(&stPNL_DeviceId);
    pstXC_InitData->stPanelInfo.enPnl_Out_Timing_Mode = (XC_EX_PNL_OUT_TIMING_MODE)g_IPanelEx.OutTimingMode(&stPNL_DeviceId);
    pstXC_InitData->stPanelInfo.u16DefaultHTotal = g_IPanelEx.HTotal(&stPNL_DeviceId);
    pstXC_InitData->stPanelInfo.u16DefaultVTotal = g_IPanelEx.VTotal(&stPNL_DeviceId);
    pstXC_InitData->stPanelInfo.u32MinSET = g_IPanelEx.MinSET(&stPNL_DeviceId);
    MSG_VIDEO(printf("pstXC_InitData->stPanelInfo.u32MinSET = 0x%x\n", pstXC_InitData->stPanelInfo.u32MinSET));
    pstXC_InitData->stPanelInfo.u32MaxSET = g_IPanelEx.MaxSET(&stPNL_DeviceId);
    MSG_VIDEO(printf("pstXC_InitData->stPanelInfo.u32MaxSET = 0x%x\n", pstXC_InitData->stPanelInfo.u32MaxSET));

    pstXC_InitData->bDLC_Histogram_From_VBlank = MAPI_FALSE;

    if(MApi_XC_EX_Init(&stXC_DeviceId, pstXC_InitData, sizeof(XC_INITDATA)) == MAPI_FALSE)
    {
        ERR_VIDEO(printf("XC_Init failed because of InitData wrong, please update header file and compile again\n"));
    }
    MApi_XC_EX_EnableFrameBufferLess(&stXC_DeviceId, FALSE);

    if(pstXC_InitData->u32Main_FB_Size == 0)
    {
        ERR_VIDEO(printf("Function:%s L:%d, XC memory size should not be 0!\n", __FUNCTION__, __LINE__));
    }

#if 0 // TBD : MENU load
    // enable MENU load
    minfo = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_XC1_MLOAD"));
    if (minfo != NULL)
    {
        if(minfo->b_is_miu0)
        {
            MApi_XC_EX_MLoad_Init(&stXC_DeviceId, minfo->u32Addr, minfo->u32Size);
        }
        else
        {
            MApi_XC_EX_MLoad_Init(&stXC_DeviceId, minfo->u32Addr + mminfo.miu_boundary, minfo->u32Size);
        }
    }

    MApi_XC_EX_MLoad_Enable(&stXC_DeviceId, TRUE);
#endif

    //STB_SC1 set Path: form SC0 OP to SC1
    MS_S16 s16PathId;
    XC_EX_MUX_PATH_INFO stPathInfo;

    memset(&stPathInfo, 0, sizeof(XC_EX_MUX_PATH_INFO));
    stPathInfo.Path_Type = E_XC_EX_PATH_TYPE_SYNCHRONOUS;
    stPathInfo.src = E_XC_EX_INPUT_SOURCE_SCALER_OP;
    stPathInfo.dest = E_XC_EX_OUTPUT_SCALER_MAIN_WINDOW;
    stPathInfo.SyncEventHandler = NULL;
    stPathInfo.DestOnOff_Event_Handler = NULL;
    stPathInfo.path_thread = NULL;
    stPathInfo.dest_periodic_handler = NULL;

    MApi_XC_EX_Mux_DeletePath(&stXC_DeviceId, stPathInfo.src, stPathInfo.dest);
    s16PathId = MApi_XC_EX_Mux_CreatePath( &stXC_DeviceId, &stPathInfo, sizeof(XC_EX_MUX_PATH_INFO) );
    if (s16PathId == -1)
    {
        ERR_VIDEO(printf(" Create path fail device id = %d, src = %d  dest = %d, your structure has wrong size with library \n", 1, stPathInfo.src, stPathInfo.dest ));
    }
    else
    {
        MApi_XC_EX_Mux_EnablePath( &stXC_DeviceId, (MS_U16)s16PathId );
    }

    MApi_XC_EX_SetInputSource(&stXC_DeviceId, E_XC_EX_INPUT_SOURCE_SCALER_OP, E_XC_EX_MAIN_WINDOW );
    MApi_XC_EX_DisableInputSource(&stXC_DeviceId, ENABLE, E_XC_EX_MAIN_WINDOW );
    MApi_XC_SetOutputCapture(ENABLE, E_XC_OP2);     // Enable op2 to ve path
}
#endif
#endif

MAPI_BOOL mapi_video::setOsdcFreq(MAPI_U16 u16OsdFreq)
{
    MS_XC_OSDC_CTRL_INFO pstOC_Ctrl;
    pstOC_Ctrl.bOC_ClK_En = TRUE;
    pstOC_Ctrl.bOC_Mixer_Bypass_En = DISABLE;
    pstOC_Ctrl.bOC_Mixer_InvAlpha_En = ENABLE;
    pstOC_Ctrl.bOC_Mixer_Hsync_Vfde_Out = DISABLE;
    pstOC_Ctrl.bOC_Mixer_Hfde_Vfde_Out = ENABLE;

    E_APIXC_ReturnValue ret = MApi_XC_OSDC_InitSetting(E_XC_OSDC_TGEN_1920x1080, NULL, &pstOC_Ctrl);

    if (ret == E_APIXC_RET_FAIL)
        return MAPI_FALSE;
    MApi_XC_OSDC_SetOutVfreqx10(u16OsdFreq);
    ret = MApi_XC_OSDC_Control(E_XC_OSDC_INIT);
    if (ret == E_APIXC_RET_FAIL)
        return MAPI_FALSE;

    return MAPI_TRUE;
}

void mapi_video::SysInitSYS(void)
{
#if (STB_ENABLE == 1)
    MDrv_SYS_Init();
#endif
}

void mapi_video::SysInitACE(void)
{
    XC_ACE_InitData sXC_ACE_InitData;

    memset(&sXC_ACE_InitData, 0, sizeof(XC_ACE_InitData));

    // Init ACE
    sXC_ACE_InitData.eWindow = MAIN_WINDOW; // FALSE: MAIN_WINDOW

#if (HDMITX_ENABLE==1)
    sXC_ACE_InitData.S16ColorCorrectionMatrix = const_cast<MS_S16*> (S16DACColorCorrectionMatrix);
#else

    if((g_IPanel.LPLL_Type() == LINK_DAC_I) || (g_IPanel.LPLL_Type() == LINK_DAC_P)) //DAC output
    {
        sXC_ACE_InitData.S16ColorCorrectionMatrix = const_cast<MS_S16*> (S16DACColorCorrectionMatrix);
    }
    else
    {
        sXC_ACE_InitData.S16ColorCorrectionMatrix = const_cast<MS_S16*> (s16DefaultColorCorrectionMatrix);
    }
#endif

    sXC_ACE_InitData.S16RGB = (MS_S16*) s16DefaultRGB;
    sXC_ACE_InitData.u16MWEHstart = g_IPanel.HStart();
    sXC_ACE_InitData.u16MWEVstart = g_IPanel.VStart();
    sXC_ACE_InitData.u16MWEWidth  = g_IPanel.Width();
    sXC_ACE_InitData.u16MWEHeight = g_IPanel.Height();

#if (STB_ENABLE == 1)
    sXC_ACE_InitData.bMWE_Enable = MAPI_FALSE;
#else
    //FIXME : move this define to ?
    //#if (MWE_FUNCTION )
    sXC_ACE_InitData.bMWE_Enable = MAPI_FALSE;
    //#endif
#endif
#if (ENABLE_LITE_SN == 0)
#if (A3_STB_ENABLE == 1)
    MApi_XC_ACE_SetBypassColorMatrix( ENABLE );
#endif
#endif

    if(MApi_XC_ACE_Init(&sXC_ACE_InitData, sizeof(XC_ACE_InitData)) == MAPI_FALSE)
    {
        ERR_VIDEO(printf("L:%d, ACE_Init failed because of InitData wrong, please update header file and compile again\n", __LINE__));
    }
#if (STB_ENABLE == 1) ||(CONNECTTV_BOX == 1)
    MApi_XC_ACE_SetColorMatrixControl(MAIN_WINDOW, FALSE); // FALSE: MAIN_WINDOW
    MApi_XC_ACE_SetRBChannelRange(MAIN_WINDOW, TRUE); // FALSE: MAIN_WINDOW
#endif
#if (ENABLE_LITE_SN == 0)
#if (A3_STB_ENABLE == 1)
    MApi_XC_ACE_SetColorMatrixControl(MAIN_WINDOW, FALSE); // FALSE: MAIN_WINDOW
    MApi_XC_ACE_SetRBChannelRange(MAIN_WINDOW, FALSE); // FALSE: MAIN_WINDOW
#endif

#if (HDMITX_ENABLE == 1)
    // Disable YUV to RGB
    MApi_XC_ACE_SelectYUVtoRGBMatrix(MAIN_WINDOW, E_XC_ACE_YUV_TO_RGB_MATRIX_USER, (MS_S16*) S16DACColorCorrectionMatrix);
    MApi_XC_ACE_SetColorCorrectionTable(MAIN_WINDOW);
#endif
#endif
}

void mapi_video::SysSet5vDetectGpioSelect()
{
#ifdef UFO_XC_HDMI_5V_DETECT
    MAPI_U32 u32Hdmi5vDetectGpioSelect = mapi_syscfg_fetch::GetInstance()->GetHdmi5vGpioSelect();
    printf("SysSet5vDetectGpioSelect u32Hdmi5vDetectGpioSelect = 0x%x\n",u32Hdmi5vDetectGpioSelect);
    MDrv_HDMI_Set5VDetectGPIOSelect(u32Hdmi5vDetectGpioSelect);
#else
    printf("SysSet5vDetectGpioSelect Not Support \n");
#endif

}


#define HDCP_KEY_SIZE 289
 // control by new class mapi_video_customer_base
    MAPI_U8 u8HdcpKey_Temp[HDCP_KEY_SIZE];
// Load HDCP key
MAPI_BOOL mapi_video::LoadHDCP(void)
{
    MAPI_BOOL bRet = FALSE;
    memset(u8HdcpKey_Temp, 0xFF, sizeof(u8HdcpKey_Temp));

// EosTek Patch Begin
#if (ENABLE_LITE_SN == 0)
#if (MSTAR_TVOS == 1)

#if (CERTIFICATE_IN_NAND == 1)
    if (mapi_syscfg_fetch::GetInstance()->GetUseNandHdcpFlag())
    {

        FILE * pFile =NULL;
        long lSize = 0;

        pFile = fopen ( mapi_syscfg_fetch::GetInstance()->GetHDCPKeyFileName().c_str() , "rb" );
        if (NULL == pFile)
        {
            printf("[mapi_video::LoadHDCP()] >>>> ERROR, secure read hdcp key file is null\n");
        }
        else
        {
            fseek (pFile , 0 , SEEK_END);
            lSize = ftell (pFile);
            fclose(pFile);
            if (0 == lSize)
            {
                printf("[mapi_video::LoadHDCP()] >>>> ERROR, secure read hdcp key file size wrong\n");
            }
            else
            {
                mapi_storage * pStorage = mapi_storage::GetInstance(mapi_syscfg_fetch::GetInstance()->GetHDCPKeyFileName().c_str());
                if (NULL != pStorage)
                {
                    bRet = pStorage->Read(0, u8HdcpKey_Temp, HDCP_KEY_SIZE);
                }
            }
        }
    }
    else if (mapi_syscfg_fetch::GetInstance()->GetUseEEPROMFlag())
    {
        mapi_storage_eeprom * pEepromStorage = mapi_storage_eeprom::GetInstance((EN_RM_TYPE)mapi_syscfg_fetch::GetInstance()->Get_Eeprom_Type(), EEPROM, EEPROM_WP);
        bRet = pEepromStorage->Read(mapi_syscfg_fetch::GetInstance()->GetHdcpEEPROMAddr(), u8HdcpKey_Temp, HDCP_KEY_SIZE);
    }
    else
    {
        bRet = FALSE;
        printf("mapi_video::LoadHDCP(): use default hdcp key in code.\n");
    }
#else
    if (mapi_syscfg_fetch::GetInstance()->GetUseSPIHdcpFlag())
    {
        //#define HDCP_KEY_BANK 0x7E
        //mapi_storage_spiflash * pSpiFlash = mapi_storage_spiflash::GetInstance();
        Imapi_storage_factory_config * pfactoryFlash = Imapi_storage_factory_config::GetInstance();
        if (NULL == pfactoryFlash)
        {
            printf("[mapi_video::LoadHDCP()] >>>> ERROR, Imapi_storage_factory_config wrong\n");
        }
        else
        {
            bRet = pfactoryFlash->Read(mapi_syscfg_fetch::GetInstance()->GetHdcpSPIBank(), mapi_syscfg_fetch::GetInstance()->getHdcpSPIOffset(), (MAPI_U32)u8HdcpKey_Temp, HDCP_KEY_SIZE);
        }
    }
    else if (mapi_syscfg_fetch::GetInstance()->GetUseEEPROMFlag())
    {
        mapi_storage_eeprom * pEepromStorage = mapi_storage_eeprom::GetInstance((EN_RM_TYPE)mapi_syscfg_fetch::GetInstance()->Get_Eeprom_Type(), EEPROM, EEPROM_WP);
        bRet = pEepromStorage->Read(mapi_syscfg_fetch::GetInstance()->GetHdcpEEPROMAddr(), u8HdcpKey_Temp, HDCP_KEY_SIZE);
    }
    else
    {
        bRet = FALSE;
        printf("mapi_video::LoadHDCP(): use default hdcp key in code.\n");
    }
#endif
#else

#if (CERTIFICATE_IN_NAND == 1)
    FILE * pFile =NULL;
    long lSize = 0;
    pFile = fopen ( mapi_syscfg_fetch::GetInstance()->GetHDCPKeyFileName().c_str() , "rb" );
    if (NULL == pFile)
    {
        printf("[mapi_video::LoadHDCP()] >>>> ERROR, secure read hdcp key file is null\n");
    }
    else
    {
        fseek (pFile , 0 , SEEK_END);
        lSize = ftell (pFile);
        fclose(pFile);
        if (0 == lSize)
        {
            printf("[mapi_video::LoadHDCP()] >>>> ERROR, secure read hdcp key file size wrong\n");
        }
        else
        {
            mapi_storage * pStorage = mapi_storage::GetInstance(mapi_syscfg_fetch::GetInstance()->GetHDCPKeyFileName().c_str());
            if (NULL != pStorage)
            {
                bRet = pStorage->Read(0, u8HdcpKey_Temp, HDCP_KEY_SIZE);
            }
        }
    }
#else
#ifndef MI_MSDK
#define HDCP_KEY_BANK 0x7E
    //mapi_storage_spiflash * pSpiFlash = mapi_storage_spiflash::GetInstance();
    Imapi_storage_factory_config * pfactoryFlash = Imapi_storage_factory_config::GetInstance();
    if (NULL == pfactoryFlash)
    {
        printf("[mapi_video::LoadHDCP()] >>>> ERROR, Imapi_storage_factory_config wrong\n");
    }
    else
    {
        bRet = pfactoryFlash->Read(HDCP_KEY_BANK, 0, (MAPI_U32)u8HdcpKey_Temp, HDCP_KEY_SIZE);
    }
#endif
#endif
#endif

#else

#if (CERTIFICATE_IN_NAND == 1)
    FILE * pFile =NULL;
    long lSize = 0;
    pFile = fopen ( mapi_syscfg_fetch::GetInstance()->GetHDCPKeyFileName().c_str() , "rb" );
    if (NULL == pFile)
    {
        printf("[mapi_video::LoadHDCP()] >>>> ERROR, secure read hdcp key file is null\n");
    }
    else
    {
        fseek (pFile , 0 , SEEK_END);
        lSize = ftell (pFile);
        fclose(pFile);
        if (0 == lSize)
        {
            printf("[mapi_video::LoadHDCP()] >>>> ERROR, secure read hdcp key file size wrong\n");
        }
        else
        {
            mapi_storage * pStorage = mapi_storage::GetInstance(mapi_syscfg_fetch::GetInstance()->GetHDCPKeyFileName().c_str());
            if (NULL != pStorage)
            {
                bRet = pStorage->Read(0, u8HdcpKey_Temp, HDCP_KEY_SIZE);
            }
        }
    }
#else
#define HDCP_KEY_BANK 0x7E
    //mapi_storage_spiflash * pSpiFlash = mapi_storage_spiflash::GetInstance();
    Imapi_storage_factory_config * pfactoryFlash = Imapi_storage_factory_config::GetInstance();
    if (NULL == pfactoryFlash)
    {
        printf("[mapi_video::LoadHDCP()] >>>> ERROR, Imapi_storage_factory_config wrong\n");
    }
    else
    {
        bRet = pfactoryFlash->Read(HDCP_KEY_BANK, 0, (MAPI_U32)u8HdcpKey_Temp, HDCP_KEY_SIZE);
    }
#endif

#endif
// EosTek Patch End

    int i = 0;
    for (i = 0; i < HDCP_KEY_SIZE; i++)
    {
        if(u8HdcpKey_Temp[i] != 0xFF)
            break;
    }
    if(i == HDCP_KEY_SIZE || bRet == FALSE)
    {
        printf("\n\n\n");
        printf("\033[1;31m**************************************\033[0m\n");
        printf("\033[1;31m**************************************\033[0m\n");
        printf("\033[1;31m    Please update HDCP key!!!   \033[0m\n");
        printf("\033[1;31m**************************************\033[0m\n\n\n");
        printf("\033[1;31m**************************************\033[0m\n\n\n");
        //ASSERT(0);
    }

#if 0
    printf("Hdcp:\n");
    for (int i = 0; i < HDCP_KEY_SIZE; ++i)
    {
        printf("0x%2X ", u8HdcpKey_Temp[i]);
        if(i%16 == 0)
            printf("\n");
    }
    printf("\n\n\n");
#endif
#if 0 // control by new class mapi_video_customer_base
    if (bRet)
    {
        memcpy(_u8HdcpKey, u8HdcpKey_Temp, HDCP_KEY_SIZE);
    }
    else
    {
        ERR_VIDEO(printf("mapi_video::LoadHDCP(). Load HDCP key failed\n\n"));
    }
#endif
    return bRet;
}

// this function is for ursa only
MAPI_BOOL mapi_video::GetHdcpKey(MAPI_U8* u8HdcpKey)
{
    return FALSE;
}

#if (HDMI_HDCP22_ENABLE == 1)
#if (TEE_ENABLE == 0)

static int GetHdcp22Key(char* strKeyPath, void* pKeyParam)
{
    int nRet = 0;
    ST_HdcpKeyStruct* pHdcpkey = (ST_HdcpKeyStruct*)pKeyParam;
    mapi_secure_storage* pSecure_storage = mapi_interface::Get_mapi_secure_storage();
    if (pSecure_storage == NULL)
    {
        printf("[mapi_video] >>>> ERROR, Get_mapi_secure fail\n");
    }
    else
    {
        MAPI_S64 s64KeySize = 0;
        MAPI_S64 s64Ret = 0;
        MAPI_U8* pu8KeyBuf = NULL;
        s64KeySize = pSecure_storage->DDI_Get_SecureFileLength(strKeyPath);

        //read in encrypted certificate content
        pu8KeyBuf = (MAPI_U8*)new char[s64KeySize+1];
        if (pu8KeyBuf == NULL)
        {
            printf("[mapi_video] >>>> ERROR, allocate memory fail\n");
            return nRet;
        }
        s64Ret = pSecure_storage->DDI_Read_SecureFile(strKeyPath, (unsigned char*)pu8KeyBuf, s64KeySize);
        if (s64Ret != s64KeySize)
        {
            printf("[mapi_video] >>>> ERROR, secure read hdcp key file size wrong\n");
        }
        else
        {
            memcpy(pHdcpkey, pu8KeyBuf+HDCP2_HEADER_LENGTH, sizeof(ST_HdcpKeyStruct));
            nRet = 1;
        }
        delete[] pu8KeyBuf;
    }
    return nRet;
}

#endif
#endif

// EosTek Patch Begin
void mapi_video::ReloadHdcpkey(MAPI_BOOL bVer2)
{
    if (bVer2)
    {
#if (HDMI_HDCP22_ENABLE == 1)
#if (TEE_ENABLE == 0)
        HDCP2_SetHdcpKeyCB(GetHdcp22Key);
#endif
        if (HDCP2_LoadKey(TRUE))
        {
            printf("[MStarSDK] mapi_video: HDMI2.0 with HDCP2.2 start.\n");
            HDCP2_SetInitCBFuncCB(MDrv_HDCP22_InitCBFunc);
            HDCP2_SetPortInitCB(MDrv_HDCP22_PortInit);
            HDCP2_SetPollingReadDoneCB(MDrv_HDCP22_PollingReadDone);
            HDCP2_SetEnableCipherCB(MDrv_HDCP22_EnableCipher);
#if (TEE_ENABLE == 0)
            HDCP2_SetFillCipherKeyCB(MDrv_HDCP22_FillCipherKey);
#endif
            HDCP2_SetSendMsgCB(MDrv_HDCP22_SendMsg);
            HDCP2_SetHandlerCB(MDrv_HDCP22_Handler);
            HDCP2_InitHDMI(TRUE, HDMI_PORT_MAX);        
        }
        else
        {
            printf("[MStarSDK] mapi_video: HDCP2_LoadKey failed.\n");
        }
#endif
    }
    else
    {
        MAPI_BOOL bReadHDCPData = MAPI_FALSE;
        bReadHDCPData = LoadHDCP();
        mapi_video_customer_base::GetInstance()->loadCustomizedHDCP(bReadHDCPData, u8HdcpKey_Temp);
    }
}
// EosTek Patch End

void mapi_video::SysInitHDMI(void)
{
    MAPI_BOOL bReadHDCPData = MAPI_FALSE;

    // !!!!!!!!!!!!!! Do not change the sequence, must set MHL support path before HDMI initxxxxxxxxxxxxxxxxxxxxxxxxxx
#if (MHL_ENABLE==ENABLE)
#if (MHL_TYPE==MHL_TYPE_INTERNAL)
    const MAPI_VIDEO_INPUTSRCTABLE *m_pInputSrcTable = NULL;
    MAPI_INPUT_SOURCE_TYPE enMHLSource = mapi_syscfg_fetch::GetInstance()->GetMHLSource();
    m_pInputSrcTable = mapi_syscfg_fetch::GetInstance()->GetInputMuxInfo();
    if(m_pInputSrcTable != NULL && m_pInputSrcTable[enMHLSource].u32EnablePort)
    {
        switch(m_pInputSrcTable[enMHLSource].u32Port[0])
        {
            case INPUT_PORT_DVI0:
                MApi_XC_Mux_SetSupportMhlPathInfo(1);
                break;

            case INPUT_PORT_DVI1:
                MApi_XC_Mux_SetSupportMhlPathInfo(2);
                break;

            case INPUT_PORT_DVI2:
                MApi_XC_Mux_SetSupportMhlPathInfo(4);
                break;

            case INPUT_PORT_DVI3:
                MApi_XC_Mux_SetSupportMhlPathInfo(8);
                break;

            default:
                MApi_XC_Mux_SetSupportMhlPathInfo(0);
                break;
        }
    }

    MApi_XC_Mux_SetMhlHotPlugInverseInfo(FALSE);
#endif
#endif

    //Mark this code , cause trunk in 116A, system database reset everytime.
    bReadHDCPData = LoadHDCP();
    MDrv_HDMI_init();
#if (HDMI_HDCP22_ENABLE == 1)

#if (TEE_ENABLE == 0)
    HDCP2_SetHdcpKeyCB(GetHdcp22Key);
#endif

    if (HDCP2_LoadKey(TRUE))
    {
        printf("[MStarSDK] mapi_video: HDMI2.0 with HDCP2.2 start.\n");
        HDCP2_SetInitCBFuncCB(MDrv_HDCP22_InitCBFunc);
        HDCP2_SetPortInitCB(MDrv_HDCP22_PortInit);
        HDCP2_SetPollingReadDoneCB(MDrv_HDCP22_PollingReadDone);
        HDCP2_SetEnableCipherCB(MDrv_HDCP22_EnableCipher);
#if (TEE_ENABLE == 0)
        HDCP2_SetFillCipherKeyCB(MDrv_HDCP22_FillCipherKey);
#endif
        HDCP2_SetSendMsgCB(MDrv_HDCP22_SendMsg);
        HDCP2_SetHandlerCB(MDrv_HDCP22_Handler);
        HDCP2_InitHDMI(TRUE, HDMI_PORT_MAX);
    }
    else
    {
        printf("[MStarSDK] mapi_video: HDCP2_LoadKey failed.\n");
    }
#endif
    #if 0
    MDrv_HDCP_initproductionkey(_u8HdcpKey);
    #else // control by new class mapi_video_customer_base
    mapi_video_customer_base::GetInstance()->loadCustomizedHDCP(bReadHDCPData, u8HdcpKey_Temp);
#if 0 //Treasure  : utopia version @ Sn trunk is too old
    MAPI_INPUT_SOURCE_TYPE enInputSrc = MAPI_INPUT_SOURCE_HDMI;
    for(enInputSrc = MAPI_INPUT_SOURCE_HDMI;enInputSrc < MAPI_INPUT_SOURCE_HDMI_MAX;enInputSrc = (MAPI_INPUT_SOURCE_TYPE)(enInputSrc + 1))
    {
        if(mapi_syscfg_fetch::GetInstance()->GetHdcpKeyEnable(enInputSrc) == 0)
        {
            /// the pointer of input source table
            const MAPI_VIDEO_INPUTSRCTABLE *m_pInputSrcTable = NULL;
            m_pInputSrcTable = mapi_syscfg_fetch::GetInstance()->GetInputMuxInfo();
            if(m_pInputSrcTable != NULL && m_pInputSrcTable[enInputSrc].u32EnablePort)
            {
                printf("\t Disable [%d]port hdcp key\n",m_pInputSrcTable[enInputSrc].u32Port[0]);
                MDrv_HDMI_SetHdcpEnable((E_MUX_INPUTPORT)(m_pInputSrcTable[enInputSrc].u32Port[0]),FALSE);
            }
        }
    }
#endif
    #endif

    SetVideoInitState(mapi_video_datatype::E_MAPI_VIDEO_INIT_HDMI, MAPI_TRUE);

    SysSet5vDetectGpioSelect();

}

static void _MApi_XC_Sys_InputSource_InputPort_Mapping(INPUT_SOURCE_TYPE_t src_ids , E_MUX_INPUTPORT* port_ids , MS_U8* u8port_count)
{
    //printf("FIXME!! %s %s %d\n", __FILE__, __PRETTY_FUNCTION__, __LINE__);
    // The I/O port here is referring the interface in MsIOPort.h

    const MAPI_VIDEO_INPUTSRCTABLE* const m_pInputSrcTable = mapi_syscfg_fetch::GetInstance()->GetInputMuxInfo();

    *u8port_count = (MAPI_U8)(m_pInputSrcTable[src_ids].u32EnablePort);
    port_ids[1] = (E_MUX_INPUTPORT)m_pInputSrcTable[src_ids].u32Port[1];
    port_ids[0] = (E_MUX_INPUTPORT)m_pInputSrcTable[src_ids].u32Port[0];

    //printf(" src_ids : port0 : port1 = %d : %d : %d \n",  src_ids, port_ids[0]  , port_ids[1]);

}

static E_XC_3D_INPUT_MODE _SDK2Driver3DInputTypeTrans(mapi_video_datatype::EN_3D_INPUT_TYPE enInMode)
{
    E_XC_3D_INPUT_MODE  enXC3DInMode = E_XC_3D_INPUT_MODE_NONE;

    switch(enInMode)
    {
        case mapi_video_datatype::E_3D_INPUT_SIDE_BY_SIDE_HALF:
            enXC3DInMode = E_XC_3D_INPUT_SIDE_BY_SIDE_HALF;
            break;

        case mapi_video_datatype::E_3D_INPUT_SIDE_BY_SIDE_HALF_INTERLACE:
            enXC3DInMode = E_XC_3D_INPUT_SIDE_BY_SIDE_HALF_INTERLACE;
            break;

        case mapi_video_datatype::E_3D_INPUT_TOP_BOTTOM:
            enXC3DInMode = E_XC_3D_INPUT_TOP_BOTTOM;
            break;

        case mapi_video_datatype::E_3D_INPUT_FRAME_PACKING:
            enXC3DInMode = E_XC_3D_INPUT_FRAME_PACKING;
            break;

        case mapi_video_datatype::E_3D_INPUT_LINE_ALTERNATIVE:
            enXC3DInMode = E_XC_3D_INPUT_LINE_ALTERNATIVE;
            break;

        case mapi_video_datatype::E_3D_INPUT_PIXEL_ALTERNATIVE:
            enXC3DInMode = E_XC_3D_INPUT_PIXEL_ALTERNATIVE;
            break;

        case mapi_video_datatype::E_3D_INPUT_CHECK_BORAD:
            enXC3DInMode = E_XC_3D_INPUT_CHECK_BORAD;
            break;

        case mapi_video_datatype::E_3D_INPUT_NORMAL_2D:
            enXC3DInMode = E_XC_3D_INPUT_NORMAL_2D;
            break;

        case mapi_video_datatype::E_3D_INPUT_NORMAL_2D_INTERLACE:
            enXC3DInMode = E_XC_3D_INPUT_NORMAL_2D_INTERLACE;
            break;

        case mapi_video_datatype::E_3D_INPUT_NORMAL_2D_INTERLACE_PTP:
            enXC3DInMode = E_XC_3D_INPUT_NORMAL_2D_INTERLACE_PTP;
            break;

        case mapi_video_datatype::E_3D_INPUT_FRAME_ALTERNATIVE:
            enXC3DInMode = E_XC_3D_INPUT_FRAME_ALTERNATIVE;
            break;

        case mapi_video_datatype::E_3D_INPUT_NORMAL_2D_HW:
            enXC3DInMode = E_XC_3D_INPUT_NORMAL_2D_HW;
            break;

        default:
             enXC3DInMode = E_XC_3D_INPUT_MODE_NONE; // 3D Bypass
            break;
    }

    return enXC3DInMode;
}

static E_XC_3D_OUTPUT_MODE _SDK2Driver3DOutputTypeTrans(mapi_video_datatype::EN_3D_OUTPUT_TYPE enOutMode)
{
    E_XC_3D_OUTPUT_MODE enXC3DOutMode = E_XC_3D_OUTPUT_MODE_NONE;

    switch(enOutMode)
    {
        case mapi_video_datatype::E_3D_OUTPUT_TOP_BOTTOM:
            enXC3DOutMode = E_XC_3D_OUTPUT_TOP_BOTTOM;
            break;

        case mapi_video_datatype::E_3D_OUTPUT_SIDE_BY_SIDE_HALF:
            enXC3DOutMode = E_XC_3D_OUTPUT_SIDE_BY_SIDE_HALF;
            break;

        case mapi_video_datatype::E_3D_OUTPUT_FRAME_ALTERNATIVE:
            enXC3DOutMode = E_XC_3D_OUTPUT_FRAME_ALTERNATIVE;
            break;
        case mapi_video_datatype::E_3D_OUTPUT_LINE_ALTERNATIVE:
        {
            // new method has problem with centering: the vstart should even, and the vend should be
            // one more than normal.
            if(//(ptARCInfo->en3DARCType != mapi_video_datatype::E_3D_AR_CENTER) &&
               (MApi_XC_Get_3D_HW_Version() == 1))
            {
                enXC3DOutMode = E_XC_3D_OUTPUT_TOP_BOTTOM;
            }
            else
            {
                enXC3DOutMode = E_XC_3D_OUTPUT_LINE_ALTERNATIVE;
            }
        }
            break;
        case mapi_video_datatype::E_3D_OUTPUT_FRAME_ALTERNATIVE_NOFRC:
            enXC3DOutMode = E_XC_3D_OUTPUT_FRAME_ALTERNATIVE_NOFRC;
            break;
        case mapi_video_datatype::E_3D_OUTPUT_FRAME_L:
            enXC3DOutMode = E_XC_3D_OUTPUT_FRAME_L;
            break;
        case mapi_video_datatype::E_3D_OUTPUT_FRAME_R:
            enXC3DOutMode = E_XC_3D_OUTPUT_FRAME_R;
            break;
        case mapi_video_datatype::E_3D_OUTPUT_LINE_ALTERNATIVE_HW:
            enXC3DOutMode = E_XC_3D_OUTPUT_LINE_ALTERNATIVE_HW;
            break;
        case mapi_video_datatype::E_3D_OUTPUT_FRAME_ALTERNATIVE_HW:
            enXC3DOutMode = E_XC_3D_OUTPUT_FRAME_ALTERNATIVE_HW;
            break;
        case mapi_video_datatype::E_3D_OUTPUT_TOP_BOTTOM_HW:
            enXC3DOutMode = E_XC_3D_OUTPUT_TOP_BOTTOM_HW;
            break;
        case mapi_video_datatype::E_3D_OUTPUT_SIDE_BY_SIDE_HALF_HW:
            enXC3DOutMode = E_XC_3D_OUTPUT_SIDE_BY_SIDE_HALF_HW;
            break;
        case mapi_video_datatype::E_3D_OUTPUT_PIXEL_ALTERNATIVE_HW:
            enXC3DOutMode = E_XC_3D_OUTPUT_PIXEL_ALTERNATIVE_HW;
            break;
        case mapi_video_datatype::E_3D_OUTPUT_CHECKBOARD_HW:
            enXC3DOutMode = E_XC_3D_OUTPUT_CHECKBOARD_HW;
            break;
        case mapi_video_datatype::E_3D_OUTPUT_FRAME_L_HW:
            enXC3DOutMode = E_XC_3D_OUTPUT_FRAME_L_HW;
            break;
        case mapi_video_datatype::E_3D_OUTPUT_FRAME_R_HW:
            enXC3DOutMode = E_XC_3D_OUTPUT_FRAME_R_HW;
            break;
#if (HDMITX_ENABLE == 1)
        case mapi_video_datatype::E_3D_OUTPUT_FRAME_PACKING:
            enXC3DOutMode = E_XC_3D_OUTPUT_FRAME_PACKING;
            break;
#endif
        default:
            enXC3DOutMode = E_XC_3D_OUTPUT_MODE_NONE; // 3D Bypass
            break;
    }

    return enXC3DOutMode;
}

void* mapi_video::InitHDMIthread(void *arg)
{
    prctl(PR_SET_NAME, (unsigned long)"InitHDMIthread");
    //Init HDMI
    mapi_video::SysInitHDMI();

    pthread_exit(NULL);
    return NULL;
}

void mapi_video::InitHW(void)
{
    if(bHWInit && (!bFactoryMode))
    {
        ASSERT(0);
        return;
    }

    stSignalDetectCount detectCount;
    MAPI_BOOL bSubXCVaild = FALSE;

    //-----------------------------------------------
    // Normal Init
    //-----------------------------------------------
    //Init SYS
    mapi_video::SysInitSYS();

    //Init XC
    mapi_video::SysInitXC();

#if (STB_ENABLE == 1)
#if (DUAL_XC_ENABLE == 1)
    //for SC1 Mux creation
    mapi_video::SysInitXCEx();
#endif
#endif

    // Init ACE
    mapi_video::SysInitACE();

    // Init timing monitor
    const XC_ApiInfo* pXC_ApiInfo = MApi_XC_GetInfo();
    ASSERT(pXC_ApiInfo);
    MApi_XC_PCMonitor_Init(pXC_ApiInfo->u8MaxWindowNum);

    detectCount = mapi_syscfg_fetch::GetInstance()->GetHdmiDetectCount();
    MApi_XC_PCMonitor_SetTimingCountEx(INPUT_SOURCE_HDMI, detectCount.u8StableCount, detectCount.u8UnstableCount); //utopia would set the configuration to all HDMI sources.

    detectCount = mapi_syscfg_fetch::GetInstance()->GetPcDetectCount();
    MApi_XC_PCMonitor_SetTimingCountEx(INPUT_SOURCE_VGA, detectCount.u8StableCount, detectCount.u8UnstableCount);

    detectCount = mapi_syscfg_fetch::GetInstance()->GetCompDetectCount();
    MApi_XC_PCMonitor_SetTimingCountEx(INPUT_SOURCE_YPBPR, detectCount.u8StableCount, detectCount.u8UnstableCount); //utopia would set the configuration to all YPbPr sources.

#if (ENABLE_LITE_SN == 0)
#if (PIP_ENABLE == 1)
        // check if scaler support sub window
        if (pXC_ApiInfo->u8MaxWindowNum >= 2)
            m_bSubXCVaild = MAPI_TRUE;
#endif
#endif
    //Init DDCRam//CH
    //The flow had move to MSrv_Control_common::InitHDMIEDIDInfoSet,after DB initialized
//    mapi_video::SysInitDDCRam();
#if ((STB_ENABLE == 0) ||(ENABLE_HDMI_RX == 1) )
#if (A3_STB_ENABLE == 0)
        pthread_t pthread_id;
        int intPTHChk;
        pthread_attr_t attr;
        pthread_attr_init(&attr);
        pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED);

        pthread_attr_setstacksize(&attr, PTHREAD_STACK_SIZE);
        intPTHChk = PTH_RET_CHK(pthread_create(&pthread_id, &attr, InitHDMIthread, NULL));
        if(intPTHChk != 0)
        {
            ASSERT(0);
        }
#endif
#endif
    //Init Mux
    MApi_XC_Mux_Init(_MApi_XC_Sys_InputSource_InputPort_Mapping);

    MApi_XC_ModeParse_Init();

    //FIXME: DAC output

    //-----------------------------------------------
    // For demo
    //-----------------------------------------------

    // output
    MApi_XC_SkipWaitVsync(MAIN_WINDOW, ENABLE);
    MApi_XC_DisableInputSource(1, MAIN_WINDOW);
    MApi_XC_GenerateBlackVideo(MAPI_TRUE ,MAIN_WINDOW);
    MApi_XC_SkipWaitVsync(MAIN_WINDOW, DISABLE);

#if (ENABLE_LITE_SN == 0)
#if (PIP_ENABLE == 1)
        bSubXCVaild = m_bSubXCVaild;
#endif
#endif
    if (bSubXCVaild)
    {
        MApi_XC_SkipWaitVsync(SUB_WINDOW, ENABLE);
        MApi_XC_DisableInputSource(1, SUB_WINDOW);
        MApi_XC_GenerateBlackVideo(MAPI_TRUE ,SUB_WINDOW);
        MApi_XC_SkipWaitVsync(SUB_WINDOW, DISABLE);
    }
#if 0 // control by new class mapi_video_customer_base
#if (STB_ENABLE == 1) || (A3_STB_ENABLE == 1)
    MApi_XC_SetDispWindowColor(0x82, MAIN_WINDOW);
#else
    MApi_XC_SetDispWindowColor(0, MAIN_WINDOW);

    if (bSubXCVaild)
    {
        MApi_XC_SetDispWindowColor(0, SUB_WINDOW);
    }
#endif
#endif
    bHWInit = MAPI_TRUE;

}

void mapi_video::FinitHW(void)
{
    MApi_XC_ACE_Exit();
    MApi_XC_Exit();
    bHWInit = MAPI_FALSE;
}

//------------------------------------------------------------------------------
/// To Set Factory Mode to enable InitHW interface
/// @param bEnable              \b IN: enable/disable bFactoryMode flag
/// @return None
//------------------------------------------------------------------------------
void mapi_video::SetFactoryMode(MAPI_BOOL bEnable)
{
    bFactoryMode = bEnable;
}

//------------------------------------------------------------------------------
/// To check current scaler blue/black screen setting is on or off
/// @param None
/// @return True/False
//------------------------------------------------------------------------------
MAPI_BOOL mapi_video::IsBlueBlackScreen(MAPI_BOOL * bIsXCReady, MAPI_SCALER_WIN eWindow)
{
    XC_ApiStatus stXCStatus;
    MAPI_BOOL ret ;

    if(MApi_XC_GetStatus(&stXCStatus, SDK2DriverScalerWinTypeTrans(eWindow)) == TRUE)
    {
        *bIsXCReady = MAPI_TRUE ;

        if((stXCStatus.bBlackscreenEnabled) || (stXCStatus.bBluescreenEnabled))
        {
            ret = MAPI_TRUE ;
        }
        else
        {
            ret = MAPI_FALSE ;
        }
    }
    else
    {
        *bIsXCReady = MAPI_FALSE ;

        ret = MAPI_FALSE ;
    }

    return ret ;
}

MAPI_BOOL mapi_video::AutoGainOffset(const MAPI_INPUT_SOURCE_TYPE CurrentMapiInputType, E_ADC_SET_INDEX *peAdcIndex, MAPI_PQL_CALIBRATION_DATA *pstADCGainOffset, const EN_MAPI_CALIBRATION_MODE enCalibrationMode)
{
    return MAPI_FALSE;
}

MAPI_BOOL mapi_video::EnableADCHWCalibration(const MAPI_BOOL bEnable)
{
    return MAPI_FALSE;
}

MAPI_BOOL mapi_video::getHWFixedADCGainOffset(const E_ADC_SET_INDEX enADCIndex, MAPI_PQL_CALIBRATION_DATA* pstADCGainOffset)
{
    XC_Auto_TuneType enADCAutoTuneType = E_XC_AUTO_TUNE_NULL;
    MAPI_BOOL bRet = MAPI_FALSE;
    APIXC_AdcGainOffsetSetting stADCGainOffset;
    memset(&stADCGainOffset, 0, sizeof(APIXC_AdcGainOffsetSetting));

    // trans adc set to auto tune type
    switch (enADCIndex)
    {
        case ADC_SET_VGA:
            enADCAutoTuneType = E_XC_AUTO_TUNE_RGB_COLOR;
            break;
        case ADC_SET_YPBPR_HD:
        case ADC_SET_YPBPR_SD:
        case ADC_SET_YPBPR2_HD:
        case ADC_SET_YPBPR2_SD:
        case ADC_SET_YPBPR3_HD:
        case ADC_SET_YPBPR3_SD:
            enADCAutoTuneType = E_XC_AUTO_TUNE_YUV_COLOR;
            break;
        case ADC_SET_SCART_RGB:
            enADCAutoTuneType = E_XC_AUTO_TUNE_SCART_RGB_GAIN;
            break;

        default:
            printf("SDK[%s]:unsupport adc set [%u]!!!\n",__PRETTY_FUNCTION__, enADCIndex);
            break;
    }

    if ((NULL == pstADCGainOffset) || (E_XC_AUTO_TUNE_NULL == enADCAutoTuneType))
    {
        ASSERT(MAPI_FALSE);
        return MAPI_FALSE;
    }

    if (MApi_XC_Auto_GetHWFixedGainOffset(enADCAutoTuneType, &stADCGainOffset))
    {
        const MAPI_U16 u16GainMaxRange = mapi_pql::GetInstance(MAPI_PQ_MAIN_WINDOW)->GetADCGainMaxRange();
        const MAPI_U16 u16OffsetMaxRange = mapi_pql::GetInstance(MAPI_PQ_MAIN_WINDOW)->GetADCOffsetMaxRange();
        if ((0 == u16GainMaxRange) || (0 == u16OffsetMaxRange))
        {
            ASSERT(MAPI_FALSE);
            return MAPI_FALSE;
        }

        pstADCGainOffset->u16RedGain = stADCGainOffset.u16RedGain * DRIVER2SDK_FACTOR / u16GainMaxRange;
        pstADCGainOffset->u16GreenGain = stADCGainOffset.u16GreenGain * DRIVER2SDK_FACTOR / u16GainMaxRange;
        pstADCGainOffset->u16BlueGain = stADCGainOffset.u16BlueGain * DRIVER2SDK_FACTOR / u16GainMaxRange;
        pstADCGainOffset->u16RedOffset = stADCGainOffset.u16RedOffset * DRIVER2SDK_FACTOR / u16OffsetMaxRange;
        pstADCGainOffset->u16GreenOffset = stADCGainOffset.u16GreenOffset * DRIVER2SDK_FACTOR / u16OffsetMaxRange;
        pstADCGainOffset->u16BlueOffset = stADCGainOffset.u16BlueOffset * DRIVER2SDK_FACTOR / u16OffsetMaxRange;
        bRet = MAPI_TRUE;
    }
    else
    {
        bRet = MAPI_FALSE;
    }

    return bRet;
}

MAPI_BOOL mapi_video::Enable3D(MAPI_BOOL bEnable,
                            mapi_video_datatype::EN_3D_INPUT_TYPE enInMode,
                            mapi_video_datatype::EN_3D_OUTPUT_TYPE enOutMode,
                            mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO *ptARCInfo)
{
    E_XC_3D_INPUT_MODE  enXC3DInMode = E_XC_3D_INPUT_MODE_NONE;
    E_XC_3D_OUTPUT_MODE enXC3DOutMode = E_XC_3D_OUTPUT_MODE_NONE;
    E_XC_3D_PANEL_TYPE enXC3dPanelType = E_XC_3D_PANEL_PELLICLE;

    MSG_3D(printf("mapi_video::Enable3D(%x, %d, %d)\n", bEnable, enInMode, enOutMode));

    Set3DInfo(bEnable, enInMode, enOutMode);
#if (MSTAR_TVOS == 1 && STB_ENABLE == 0)
    if(mapi_syscfg_fetch::GetInstance()->GetFrcMode() == TRUE)
    {
    if (bEnable && (enOutMode == mapi_video_datatype::E_3D_OUTPUT_FRAME_ALTERNATIVE))
    {
            // In inside FRC, if 3D output is frame alternative, you need to add below code.
            // The E_XC_INIT_MISC_A_FRC_INSIDE_4K1K_120HZ flag will cause utopia do something automatically,
            // like set OP using TB and FRC using TB as input.
        XC_INITMISC sXC_Init_Misc;
        memset(&sXC_Init_Misc, 0, sizeof(XC_INITMISC));
        MApi_XC_GetMISCStatus(&sXC_Init_Misc);
        sXC_Init_Misc.u32MISC_A |= E_XC_INIT_MISC_A_FRC_INSIDE_4K1K_120HZ;
        MApi_XC_Init_MISC(&sXC_Init_Misc, sizeof(XC_INITMISC));
    }
    else
    {
            // Remove E_XC_INIT_MISC_A_FRC_INSIDE_4K1K_120HZ flag when 3D output isn't frame alternative.
        XC_INITMISC sXC_Init_Misc;
        memset(&sXC_Init_Misc, 0, sizeof(XC_INITMISC));
        MApi_XC_GetMISCStatus(&sXC_Init_Misc);
        sXC_Init_Misc.u32MISC_A &= ~E_XC_INIT_MISC_A_FRC_INSIDE_4K1K_120HZ;
        MApi_XC_Init_MISC(&sXC_Init_Misc, sizeof(XC_INITMISC));
    }
    }
#endif
    if(bEnable)
    {
        //check if scaler direct output 120hz sg panel
        if(mapi_syscfg_fetch::GetInstance()->GetXCOutput120hzSGPanelFlag())
        {
            //only version 1 topbottom/2d to 3d need let kernel report GPIO
            if(MApi_XC_Get_3D_HW_Version() == 1)
            {
                if((mapi_video_datatype::E_3D_INPUT_TOP_BOTTOM == enInMode)
                   || (mapi_video_datatype::E_3D_INPUT_NORMAL_2D == enInMode))
                {
                    //MApi_GOP_Set_GPIO3DPin(xxxx);
                }
                else
                {
                    //MApi_GOP_Set_GPIO3DPin(0);
                }
            }
        }

        enXC3DInMode = _SDK2Driver3DInputTypeTrans(enInMode);
        enXC3DOutMode = _SDK2Driver3DOutputTypeTrans(enOutMode);

#if (STB_ENABLE == 1)
#if (STEREO_3D_ENABLE == 1)

        MApi_HDMITx_PKT_User_Define(HDMITX_VS_INFOFRAME, FALSE, HDMITX_CYCLIC_PACKET, 0x0);
#endif
#endif
    }
    else
    {
#if (STB_ENABLE == 1)
#if (STEREO_3D_ENABLE == 1)

        MApi_HDMITx_PKT_User_Define(HDMITX_VS_INFOFRAME, TRUE, HDMITX_STOP_PACKET, 0xFF);
#endif
#endif
        //Close 3D here
        enXC3dPanelType = E_XC_3D_PANEL_NONE;
    }
    MSG_3D(printf("-----enXC3DInMode %d, enXC3DOutMode %d, enXC3dPanelType %d---\n",enXC3DInMode,enXC3DOutMode, enXC3dPanelType));
#if (STEREO_3D_ENABLE == 1)
#if (URSA_MCP == 1)
        MS_PNL_HW_LVDSResInfo stLVDSResInfo;
        memset(&stLVDSResInfo, 0, sizeof(MS_PNL_HW_LVDSResInfo));

        if(bEnable && ((enXC3DOutMode == E_XC_3D_OUTPUT_FRAME_ALTERNATIVE)
                    || (enXC3DOutMode == E_XC_3D_OUTPUT_FRAME_ALTERNATIVE_HW)))
        {
            stLVDSResInfo.bEnable    = TRUE;
            stLVDSResInfo.u16channel = mapi_video_datatype::E_MAPI_PNL_LVDS_CHANNEL_A|mapi_video_datatype::E_MAPI_PNL_LVDS_CHANNEL_B;
            stLVDSResInfo.u32pair    = mapi_video_datatype::E_MAPI_PNL_LVDS_PAIR_3|mapi_video_datatype::E_MAPI_PNL_LVDS_PAIR_4;
        }
        MApi_PNL_HWLVDSReservedtoLRFlag(stLVDSResInfo);
#endif
#endif

    if(MApi_XC_Set_3D_Mode(enXC3DInMode, enXC3DOutMode, enXC3dPanelType, MAIN_WINDOW))
    {
         return MApi_XC_Set_3D_Mode(enXC3DInMode, enXC3DOutMode, enXC3dPanelType, SUB_WINDOW);
    }
    else
    {
        return FALSE;
    }
}

#if (STB_ENABLE == 1)
MAPI_BOOL mapi_video::SetHDMITX_Enable3D(MAPI_BOOL bEnable )
{
    if(bEnable )
    {
#if (STEREO_3D_ENABLE == 1)
        MApi_HDMITx_PKT_User_Define(HDMITX_VS_INFOFRAME, FALSE, HDMITX_CYCLIC_PACKET, 0x0);
#endif
    }
    else
    {
#if (STEREO_3D_ENABLE == 1)
        MApi_HDMITx_PKT_User_Define(HDMITX_VS_INFOFRAME, TRUE, HDMITX_STOP_PACKET, 0xFF);
#endif
    }
    return TRUE;
}

MAPI_BOOL mapi_video::SetHDMITX_3DMode(MAPI_U16 u16_3DMode)
{
#if (STEREO_3D_ENABLE == 1)
    HDMITX_VIDEO_3D_STRUCTURE e3dMode = (HDMITX_VIDEO_3D_STRUCTURE)u16_3DMode;
    MApi_HDMITx_Set_VS_InfoFrame(HDMITX_VIDEO_VS_3D, e3dMode, HDMITx_VIDEO_4k2k_Reserved);
#endif
    return TRUE;
}
#endif

MAPI_BOOL mapi_video::Set3DInfo(MAPI_BOOL bEnable, mapi_video_datatype::EN_3D_INPUT_TYPE enInMode, mapi_video_datatype::EN_3D_OUTPUT_TYPE enOutMode)
{
    m_st3DInfo.bEnable3D = bEnable;
    m_st3DInfo.enInput3DMode = enInMode;
    m_st3DInfo.enOutput3DMode = enOutMode;
    return TRUE;
}

MAPI_BOOL mapi_video::Get3DInfo(mapi_video_datatype::ST_MAPI_3D_INFO &mapi_3D_info)
{
    mapi_3D_info = m_st3DInfo;
    return TRUE;
}

MAPI_BOOL mapi_video::Set3DLRSwitch(MAPI_BOOL bEnable)
{
    MAPI_BOOL bRet = FALSE;

    if((bEnable && (!Get3DLRSwitch()))
       || ((!bEnable) && Get3DLRSwitch()))
    {
        bRet = MApi_XC_Set_3D_LR_Frame_Exchg(MAIN_WINDOW);
    }

    return bRet;
}

MAPI_BOOL mapi_video::Get3DLRSwitch()
{
    return (MAPI_BOOL)MApi_XC_3D_Is_LR_Frame_Exchged(MAIN_WINDOW);
}

MAPI_U16 mapi_video::Get3DHShiftStatus()
{
    return m_u163DHShift;
}

MAPI_BOOL mapi_video::Set3DHShift(MAPI_U16 u163DH)
{
    m_u163DHShift = u163DH;
    MApi_XC_Set_3D_HShift(m_u163DHShift);
    return TRUE;
}

MAPI_BOOL mapi_video::SetHW2DTo3DParameters(mapi_video_datatype::ST_MAPI_3D_HW2DTO3DPARA *pstHw2DTo3DPara)
{
    MS_XC_3D_HW2DTO3D_PARA _st3DHw2DTo3DPara;
    memset(&_st3DHw2DTo3DPara, 0, sizeof(MS_XC_3D_HW2DTO3D_PARA));
    _st3DHw2DTo3DPara.u32Hw2dTo3dPara_Version = pstHw2DTo3DPara->u32Hw2dTo3dPara_Version;
    _st3DHw2DTo3DPara.u16Concave              = pstHw2DTo3DPara->u16Concave;
    _st3DHw2DTo3DPara.u16ArtificialGain       = pstHw2DTo3DPara->u16ArtificialGain;
    _st3DHw2DTo3DPara.u16Offset               = pstHw2DTo3DPara->u16Offset;
    _st3DHw2DTo3DPara.u16EleSel               = pstHw2DTo3DPara->u16EleSel;
    _st3DHw2DTo3DPara.u16ModSel               = pstHw2DTo3DPara->u16ModSel;
    _st3DHw2DTo3DPara.u16Gain                 = pstHw2DTo3DPara->u16Gain;
    _st3DHw2DTo3DPara.u16EdgeBlackWidth       = pstHw2DTo3DPara->u16EdgeBlackWidth;
    return MApi_XC_Set_3D_HW2DTo3D_Parameters(_st3DHw2DTo3DPara);
}

MAPI_BOOL mapi_video::GetHW2DTo3DParameters(mapi_video_datatype::ST_MAPI_3D_HW2DTO3DPARA *pstHw2DTo3DPara)
{
    MS_XC_3D_HW2DTO3D_PARA _st3DHw2DTo3DPara;
    MAPI_BOOL bRet = TRUE;
    memset(&_st3DHw2DTo3DPara, 0, sizeof(MS_XC_3D_HW2DTO3D_PARA));
    if(MApi_XC_Get_3D_HW2DTo3D_Parameters(&_st3DHw2DTo3DPara))
    {
        pstHw2DTo3DPara->u32Hw2dTo3dPara_Version = _st3DHw2DTo3DPara.u32Hw2dTo3dPara_Version;
        pstHw2DTo3DPara->u16Concave              = _st3DHw2DTo3DPara.u16Concave;
        pstHw2DTo3DPara->u16ArtificialGain       = _st3DHw2DTo3DPara.u16ArtificialGain;
        pstHw2DTo3DPara->u16Offset               = _st3DHw2DTo3DPara.u16Offset;
        pstHw2DTo3DPara->u16EleSel               = _st3DHw2DTo3DPara.u16EleSel;
        pstHw2DTo3DPara->u16ModSel               = _st3DHw2DTo3DPara.u16ModSel;
        pstHw2DTo3DPara->u16Gain                 = _st3DHw2DTo3DPara.u16Gain;
        pstHw2DTo3DPara->u16EdgeBlackWidth       = _st3DHw2DTo3DPara.u16EdgeBlackWidth;
        bRet = TRUE;
    }
    else
    {
        bRet = FALSE;
    }
    return bRet;
}

MAPI_BOOL mapi_video::IsSupportedHW2DTo3D()
{
    return MApi_XC_Get_3D_IsSupportedHW2DTo3D();
}

MAPI_BOOL mapi_video::SetDetect3DFormatParameters(mapi_video_datatype::ST_MAPI_3D_DETECT3DFORMATPARA *pstDetect3DFormatPara)
{
    MS_XC_3D_DETECT3DFORMAT_PARA stDetect3DFormatPara;
    stDetect3DFormatPara.u32Detect3DFormatPara_Version = pstDetect3DFormatPara->u32Detect3DFormatPara_Version;
    stDetect3DFormatPara.u16HorSearchRange = pstDetect3DFormatPara->u16HorSearchRange;
    stDetect3DFormatPara.u16VerSearchRange = pstDetect3DFormatPara->u16VerSearchRange;
    stDetect3DFormatPara.u16GYPixelThreshold = pstDetect3DFormatPara->u16GYPixelThreshold;
    stDetect3DFormatPara.u16RCrPixelThreshold = pstDetect3DFormatPara->u16RCrPixelThreshold;
    stDetect3DFormatPara.u16BCbPixelThreshold = pstDetect3DFormatPara->u16BCbPixelThreshold;
    stDetect3DFormatPara.u16HorSampleCount = pstDetect3DFormatPara->u16HorSampleCount;
    stDetect3DFormatPara.u16VerSampleCount = pstDetect3DFormatPara->u16VerSampleCount;
    stDetect3DFormatPara.u16MaxCheckingFrameCount = pstDetect3DFormatPara->u16MaxCheckingFrameCount;
    stDetect3DFormatPara.u16HitPixelPercentage = pstDetect3DFormatPara->u16HitPixelPercentage;
    stDetect3DFormatPara.bEnableOverscan = pstDetect3DFormatPara->bEnableOverscan;
    return MApi_XC_Set_3D_Detect3DFormat_Parameters(&stDetect3DFormatPara);
}

MAPI_BOOL mapi_video::GetDetect3DFormatParameters(mapi_video_datatype::ST_MAPI_3D_DETECT3DFORMATPARA *pstDetect3DFormatPara)
{
    MS_XC_3D_DETECT3DFORMAT_PARA stDetect3DFormatPara;
    memset(&stDetect3DFormatPara, 0, sizeof(MS_XC_3D_DETECT3DFORMAT_PARA));
    if (MApi_XC_Get_3D_Detect3DFormat_Parameters(&stDetect3DFormatPara))
    {
        pstDetect3DFormatPara->u32Detect3DFormatPara_Version = stDetect3DFormatPara.u32Detect3DFormatPara_Version;
        pstDetect3DFormatPara->u16HorSearchRange = stDetect3DFormatPara.u16HorSearchRange;
        pstDetect3DFormatPara->u16VerSearchRange = stDetect3DFormatPara.u16VerSearchRange;
        pstDetect3DFormatPara->u16GYPixelThreshold = stDetect3DFormatPara.u16GYPixelThreshold;
        pstDetect3DFormatPara->u16RCrPixelThreshold = stDetect3DFormatPara.u16RCrPixelThreshold;
        pstDetect3DFormatPara->u16BCbPixelThreshold = stDetect3DFormatPara.u16BCbPixelThreshold;
        pstDetect3DFormatPara->u16HorSampleCount = stDetect3DFormatPara.u16HorSampleCount;
        pstDetect3DFormatPara->u16VerSampleCount = stDetect3DFormatPara.u16VerSampleCount;
        pstDetect3DFormatPara->u16MaxCheckingFrameCount = stDetect3DFormatPara.u16MaxCheckingFrameCount;
        pstDetect3DFormatPara->u16HitPixelPercentage = stDetect3DFormatPara.u16HitPixelPercentage;
        pstDetect3DFormatPara->bEnableOverscan = stDetect3DFormatPara.bEnableOverscan;
        return MAPI_TRUE;
    }
    return MAPI_FALSE;
}

mapi_video_datatype::EN_3D_INPUT_TYPE mapi_video::Detect3DFormatByContent(MAPI_SCALER_WIN eWindow)
{
    mapi_video_datatype::EN_3D_INPUT_TYPE eInput3D = mapi_video_datatype::E_3D_INPUT_MODE_NONE;
    switch(MApi_XC_Detect3DFormatByContent((SCALER_WIN)eWindow))
    {
        case E_XC_3D_INPUT_SIDE_BY_SIDE_HALF:
            eInput3D = mapi_video_datatype::E_3D_INPUT_SIDE_BY_SIDE_HALF;
            break;

        case E_XC_3D_INPUT_TOP_BOTTOM:
            eInput3D = mapi_video_datatype::E_3D_INPUT_TOP_BOTTOM;
            break;

        case E_XC_3D_INPUT_FRAME_PACKING:
            eInput3D = mapi_video_datatype::E_3D_INPUT_FRAME_PACKING;
            break;

        case E_XC_3D_INPUT_LINE_ALTERNATIVE:
            eInput3D = mapi_video_datatype::E_3D_INPUT_LINE_ALTERNATIVE;
            break;

        case E_XC_3D_INPUT_FRAME_ALTERNATIVE:
            eInput3D = mapi_video_datatype::E_3D_INPUT_FRAME_ALTERNATIVE;
            break;

        default:
             eInput3D = mapi_video_datatype::E_3D_INPUT_MODE_NONE;
            break;
    }
    return eInput3D;
}

MAPI_BOOL mapi_video::EnableAutoDetect3D(MAPI_BOOL bEnable, mapi_video_datatype::EN_3D_AUTODETECT_METHOD enDetectMethod)
{
    E_XC_3D_AUTODETECT_METHOD enXCDetectMethod = E_XC_3D_AUTODETECT_SW;
    switch(enDetectMethod)
    {
        case mapi_video_datatype::EN_3D_AUTODETECT_SW:
            enXCDetectMethod = E_XC_3D_AUTODETECT_SW;
            break;

        case mapi_video_datatype::EN_3D_AUTODETECT_HW:
            enXCDetectMethod = E_XC_3D_AUTODETECT_HW;
            break;

        case mapi_video_datatype::EN_3D_AUTODETECT_HW_COMPATIBLE:
            enXCDetectMethod = E_XC_3D_AUTODETECT_HW_COMPATIBLE;
            break;

        default:
            enXCDetectMethod = E_XC_3D_AUTODETECT_SW;
            break;

    }
    return MApi_XC_EnableAutoDetect3D(bEnable, enXCDetectMethod);
}

MAPI_BOOL mapi_video::GetAutoDetect3DFlag(MAPI_BOOL *pbEnable, mapi_video_datatype::EN_3D_AUTODETECT_METHOD *penDetectMethod)
{
    E_XC_3D_AUTODETECT_METHOD enXCDetectMethod = E_XC_3D_AUTODETECT_SW;
    MAPI_BOOL bEnable = FALSE;
    MAPI_BOOL bRet = MApi_XC_GetAutoDetect3DFlag(&enXCDetectMethod, &bEnable);

    *pbEnable = bEnable;
    switch(enXCDetectMethod)
    {
        case E_XC_3D_AUTODETECT_SW:
            *penDetectMethod = mapi_video_datatype::EN_3D_AUTODETECT_SW;
            break;

        case E_XC_3D_AUTODETECT_HW:
            *penDetectMethod = mapi_video_datatype::EN_3D_AUTODETECT_HW;
            break;

        case E_XC_3D_AUTODETECT_HW_COMPATIBLE:
            *penDetectMethod = mapi_video_datatype::EN_3D_AUTODETECT_HW_COMPATIBLE;
            break;

        default:
            *penDetectMethod = mapi_video_datatype::EN_3D_AUTODETECT_SW;
            break;
    }

    return bRet;
}

MAPI_BOOL mapi_video::Is3DFormatSupported(mapi_video_datatype::EN_3D_INPUT_TYPE enInMode,
                                        mapi_video_datatype::EN_3D_OUTPUT_TYPE enOutMode)
{
    MAPI_BOOL bSupported = FALSE;
    E_XC_3D_INPUT_MODE  enXC3DInMode = E_XC_3D_INPUT_MODE_NONE;
    E_XC_3D_OUTPUT_MODE enXC3DOutMode = E_XC_3D_OUTPUT_MODE_NONE;

    enXC3DInMode = _SDK2Driver3DInputTypeTrans(enInMode);
    enXC3DOutMode = _SDK2Driver3DOutputTypeTrans(enOutMode);

    bSupported = MApi_XC_Is3DFormatSupported(enXC3DInMode, enXC3DOutMode);
    return bSupported;
}

//==============obsolete function===========================================
MAPI_BOOL mapi_video::Set3DLRSwitchFlag(MAPI_BOOL bEnable)
{
     m_bLRSwitch = bEnable;
    return TRUE;
}

MAPI_BOOL mapi_video::Set3DLRSwitch()
{
     Set3DLRSwitchFlag(!m_bLRSwitch);
     return MApi_XC_Set_3D_LR_Frame_Exchg(MAIN_WINDOW);
}

MAPI_BOOL mapi_video::Get3DLRSwitchStatus()
{
     return m_bLRSwitch;
}
//============end==========================================================

void mapi_video::SetMirror(MAPI_BOOL bEnable ,mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO *stARCInfo)
{
#ifndef MI_MSDK
    IDirectFB              *dfb;
    IDirectFBDisplayLayer  *layer;
    DFBResult ret;
    ret = DirectFBInit( NULL, NULL );
    if (ret) {
        DirectFBError( "DirectFBInit() failed", ret );
        exit( 1 );
    }
    ret = DirectFBCreate(&dfb);
    if (ret) {
        DirectFBError( "DirectFBCreate() failed", ret );
        exit( 1 );
    }

    dfb->GetDisplayLayer( dfb, DLID_PRIMARY, &layer );
    layer->SetCooperativeLevel(layer, DLSCL_ADMINISTRATIVE);
    layer->SetHVMirrorEnable(layer, bEnable, bEnable);
    layer->Release(layer);
    dfb->Release(dfb);
#endif
    MApi_XC_EnableMirrorModeEx((bEnable != 0)?MIRROR_HV:MIRROR_NORMAL, MAIN_WINDOW);
    MApi_XC_EnableMirrorModeEx((bEnable != 0)?MIRROR_HV:MIRROR_NORMAL, SUB_WINDOW);
    this->SetVideoMute(TRUE);
    this->SetWindow(NULL, NULL, stARCInfo);
    this->SetVideoMute(FALSE);
    mapi_syscfg_fetch::GetInstance()->SetVideoMirrorCfg(bEnable);
}

MAPI_BOOL mapi_video::WaitFPLLDone(void)
{
    return (MAPI_BOOL)MApi_XC_WaitFPLLDone();
}

MAPI_BOOL mapi_video::ADC_Set_SOGBW(MAPI_U16 u16value)
{
    MApi_XC_ADC_Set_SOGBW(u16value);
    return TRUE;
}

MAPI_BOOL mapi_video::SetDynamicScaling(const mapi_video_datatype::ST_MAPI_DYNAMICSCALING_INFO* const pstDSInfo, const MAPI_U32 u32DSInfoLen, const MAPI_SCALER_WIN eWindow)
{
    XC_DynamicScaling_Info stDS_Info;
    memset(&stDS_Info, 0, sizeof(XC_DynamicScaling_Info));
    stDS_Info.u32DS_Info_BaseAddr = pstDSInfo->u32DS_Info_BaseAddr;
    stDS_Info.u8MIU_Select        = pstDSInfo->u8MIU_Select;
    stDS_Info.u8DS_Index_Depth    = pstDSInfo->u8DS_Index_Depth;
    stDS_Info.bOP_DS_On           = pstDSInfo->bOP_DS_On;
    stDS_Info.bIPS_DS_On          = pstDSInfo->bIPS_DS_On;
    stDS_Info.bIPM_DS_On          = pstDSInfo->bIPM_DS_On;
    return MApi_XC_SetDynamicScaling( &stDS_Info, sizeof(XC_DynamicScaling_Info), SDK2DriverScalerWinTypeTrans(eWindow));
}

#if (STB_ENABLE == 1)
MAPI_BOOL mapi_video::SetDynamicScalingEx(const mapi_video_datatype::ST_MAPI_DYNAMICSCALING_INFO* const pstDSInfo, const MAPI_U32 u32DSInfoLen, const MAPI_SCALER_WIN eWindow)
{

#if (DUAL_XC_ENABLE == 1)
    XC_DEVICE_ID stXC_DeviceId = {E_DEVICE_VERSION_0, E_DEVICE_ID_1};

    XC_EX_DynamicScaling_Info stExDS_Info;
    memset(&stExDS_Info, 0, sizeof(XC_EX_DynamicScaling_Info));
    stExDS_Info.u32DS_Info_BaseAddr = pstDSInfo->u32DS_Info_BaseAddr;
    stExDS_Info.u8MIU_Select        = pstDSInfo->u8MIU_Select;
    stExDS_Info.u8DS_Index_Depth    = pstDSInfo->u8DS_Index_Depth;
    stExDS_Info.bOP_DS_On           = pstDSInfo->bOP_DS_On;
    stExDS_Info.bIPS_DS_On          = pstDSInfo->bIPS_DS_On;
    stExDS_Info.bIPM_DS_On          = pstDSInfo->bIPM_DS_On;

//    if((MAPI_MAIN_WINDOW == eWindow) && (E_MAPI_PIP_MODE_OFF == m_ePipMode))
//    {
    return MApi_XC_EX_SetDynamicScaling(&stXC_DeviceId, &stExDS_Info, sizeof(XC_DynamicScaling_Info), E_XC_EX_MAIN_WINDOW);
//    }

#endif

    return FALSE;

}
#endif
void mapi_video::PQ_SetDS_OnOFF(const MAPI_BOOL bEnable)
{
    mapi_pql::GetInstance(MAPI_PQ_MAIN_WINDOW)->setDSOnOff(PQ_MAIN_WINDOW, bEnable);
}

void mapi_video::DS_GetVideoStatusFromFirmware(
        MAPI_BOOL &bDS_Status,
        MAPI_U16 &u16CurrentFrameHSize,
        MAPI_U16 &u16CurrentFrameVSize,
        MAPI_U16 &u16NextFrameHSize,
        MAPI_U16 &u16NextFrameVSize,
        MAPI_U16 &u16VsyncCNT,
        mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE &stCapWin,
        mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE &stCropWin,
        mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE &stDispWin,
        const MAPI_SCALER_WIN eWin)
{
    mapi_video_datatype::ST_MM_DS_XC_STATUS *pstMMDS_Status;
    pstMMDS_Status = (mapi_video_datatype::ST_MM_DS_XC_STATUS *) _PA2VA(u32DS_Sharemem_Buf_Base);

    u16CurrentFrameHSize = pstMMDS_Status->u16CurrentFrameHSize;
    u16CurrentFrameVSize = pstMMDS_Status->u16CurrentFrameVSize;
    u16NextFrameVSize = pstMMDS_Status->u16NextFrameVSize;
    u16NextFrameHSize = pstMMDS_Status->u16NextFrameHSize;
    u16VsyncCNT = pstMMDS_Status->u16VsyncCNT;
    bDS_Status = MApi_XC_GetDynamicScalingStatus();

    stCapWin.x      = pstMMDS_Status->stCapWin.x;
    stCapWin.y      = pstMMDS_Status->stCapWin.y;
    stCapWin.width  = pstMMDS_Status->stCapWin.width;
    stCapWin.height = pstMMDS_Status->stCapWin.height;

    stCropWin.x       = pstMMDS_Status->stCropWin.x;
    stCropWin.y       = pstMMDS_Status->stCropWin.y;
    stCropWin.width  = pstMMDS_Status->stCropWin.width;
    stCropWin.height = pstMMDS_Status->stCropWin.height;

    stDispWin.x       = pstMMDS_Status->stDispWin.x;
    stDispWin.y       = pstMMDS_Status->stDispWin.y;
    stDispWin.width  = pstMMDS_Status->stDispWin.width;
    stDispWin.height = pstMMDS_Status->stDispWin.height;
}

void mapi_video::DS_SendXCStatus2Firmware(const MAPI_U32 u32FM_Buf_Base, const MAPI_SCALER_WIN eWin)
{
    //ST_VIDEOPLAYER_INFO* pVideoPlayer = (ST_VIDEOPLAYER_INFO*)m_pVideoPlayer;
    mapi_video_datatype::ST_MM_DS_XC_STATUS *pstMMDS_Status;
    MAPI_U32 u32Timeout;
    MAPI_U16 u16VBoxWidth  = GetCurrentVirtualBoxWidth(MAPI_MAIN_WINDOW);  //only support DS on main window now
    MAPI_U16 u16VBoxHeight = GetCurrentVirtualBoxHeight(MAPI_MAIN_WINDOW); //only support DS on main window now
    MS_PNL_DST_DispInfo pDstInfo={1,1,1,1,1,1,1,1};

    MVOP_Handle stHdl = { E_MVOP_MODULE_MAIN };
    MVOP_DrvMirror enMirror = E_VOPMIRROR_NONE;
    MDrv_MVOP_GetCommand(&stHdl, E_MVOP_CMD_GET_MIRROR_MODE, &enMirror, sizeof(enMirror));

    u32DS_Sharemem_Buf_Base = u32FM_Buf_Base;

    MApi_PNL_GetDstInfo(&pDstInfo, sizeof(MS_PNL_DST_DispInfo));
    // direct write to shared memory with firmware
    //pstMMDS_Status = (mapi_video_datatype::ST_MM_DS_XC_STATUS *) MsOS_PA2KSEG1(u32FM_Buf_Base);
    pstMMDS_Status = (mapi_video_datatype::ST_MM_DS_XC_STATUS *) _PA2VA(u32FM_Buf_Base);

    // get scaler information
    XC_ApiStatusEx stXCStatusEx;
    stXCStatusEx.u16ApiStatusEX_Length = sizeof(XC_ApiStatusEx);
    stXCStatusEx.u32ApiStatusEx_Version = API_STATUS_EX_VERSION;
    if (MApi_XC_GetStatusEx(&stXCStatusEx, SDK2DriverScalerWinTypeTrans(eWin)) == MAPI_FALSE)
    {
        ERR_VIDEO(printf("MApi_XC_GetStatus failed.\n"));
    }

    u32Timeout = MsOS_GetSystemTime() + 5000;
    // copy scaler related information
    while(pstMMDS_Status->bFWIsUpdating)
    {
        sleep(0);

        if(MsOS_GetSystemTime() > u32Timeout)
        {
            ERR_VIDEO(printf("DS Send XC Status to Firmware , bFWIsUpdating time out\n"));
            break;
        }
     }
    pstMMDS_Status->bFWGotXCInfo = FALSE;
    pstMMDS_Status->bHKIsUpdating = TRUE;
    MsOS_FlushMemory();


    pstMMDS_Status->u16VirtualBoxWidth  = u16VBoxWidth;
    pstMMDS_Status->u16VirtualBoxHeight = u16VBoxHeight;
    pstMMDS_Status->bUseVBoxOfHK = TRUE;

    //memcpy(&pstMMDS_Status->stCapWin, &stXCStatusEx.stCapWin, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE));
    //memcpy(&pstMMDS_Status->stCropWin, &stXCStatusEx.stCropWin, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE));

    stXCStatusEx.stCapWin.width   = u16VBoxWidth;
    stXCStatusEx.stCapWin.height  = u16VBoxHeight;
    stXCStatusEx.stCropWin.width  = u16VBoxWidth;
    stXCStatusEx.stCropWin.height = u16VBoxHeight;

    // compile check
    SIZE_CHECK_STATIC_ASSERT( sizeof(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE) == sizeof(MS_WINDOW_TYPE) );
    memcpy(&pstMMDS_Status->stCapWin, &stXCStatusEx.stCapWin, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE));
    memcpy(&pstMMDS_Status->stCropWin, &stXCStatusEx.stCropWin, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE));
    memcpy(&pstMMDS_Status->stDispWin, &stXCStatusEx.stDispWin, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE));

    pstMMDS_Status->u32PNL_WIDTH = pDstInfo.DEHEND-pDstInfo.DEHST+1;

#if (ENABLE_4K2K_NAPOLI == 1)
    /// Napoli 4K2K HW limitation, need to Convert 2K2K
    if((pstMMDS_Status->u32PNL_WIDTH < pstMMDS_Status->stDispWin.width)&&(pstMMDS_Status->stDispWin.width > 3800))
    {
        pstMMDS_Status->stDispWin.x /= 2;
        pstMMDS_Status->stDispWin.width /= 2;
    }
#endif
    pstMMDS_Status->u16H_SizeAfterPreScaling = stXCStatusEx.u16H_SizeAfterPreScaling;
    pstMMDS_Status->u16V_SizeAfterPreScaling = stXCStatusEx.u16V_SizeAfterPreScaling;
    //VIDEO_DBG("before modify u32IPMBase0 %x,u32IPMBase1 %x,u32IPMBase2, %x---\n",
    //       pstMMDS_Status->u32IPMBase0,pstMMDS_Status->u32IPMBase1,pstMMDS_Status->u32IPMBase2);
    MirrorMode_t enVideoMirror = MApi_XC_GetMirrorModeTypeEx(MAIN_WINDOW);
    if((enVideoMirror == MIRROR_HV) || (enVideoMirror == MIRROR_V_ONLY))
    {
        pstMMDS_Status->u32IPMBase0 = stXCStatusEx.u32IPMBase0 - MApi_XC_Get_DNRBaseOffset(SDK2DriverScalerWinTypeTrans(eWin));
        pstMMDS_Status->u32IPMBase1 = stXCStatusEx.u32IPMBase1 - MApi_XC_Get_DNRBaseOffset(SDK2DriverScalerWinTypeTrans(eWin));
        pstMMDS_Status->u32IPMBase2 = stXCStatusEx.u32IPMBase2 - MApi_XC_Get_DNRBaseOffset(SDK2DriverScalerWinTypeTrans(eWin));
        pstMMDS_Status->u32FRCMBase0 = stXCStatusEx.u32FRCMBase0 - MApi_XC_Get_DNRBaseOffset(SDK2DriverScalerWinTypeTrans(eWin));
        pstMMDS_Status->u32FRCMBase1 = stXCStatusEx.u32FRCMBase1 - MApi_XC_Get_DNRBaseOffset(SDK2DriverScalerWinTypeTrans(eWin));
        pstMMDS_Status->u32FRCMBase2 = stXCStatusEx.u32FRCMBase2 - MApi_XC_Get_DNRBaseOffset(SDK2DriverScalerWinTypeTrans(eWin));
        pstMMDS_Status->bMirrorMode = TRUE;
    }
    else
    {
        pstMMDS_Status->u32IPMBase0 = stXCStatusEx.u32IPMBase0;
        pstMMDS_Status->u32IPMBase1 = stXCStatusEx.u32IPMBase1;
        pstMMDS_Status->u32IPMBase2 = stXCStatusEx.u32IPMBase2;
        pstMMDS_Status->u32FRCMBase0 = stXCStatusEx.u32FRCMBase0;
        pstMMDS_Status->u32FRCMBase1 = stXCStatusEx.u32FRCMBase1;
        pstMMDS_Status->u32FRCMBase2 = stXCStatusEx.u32FRCMBase2;
        pstMMDS_Status->bMirrorMode = FALSE;
    }

    pstMMDS_Status->bLinearMode = stXCStatusEx.bLinearMode;
    pstMMDS_Status->u8BitPerPixel = stXCStatusEx.u8BitPerPixel;
    pstMMDS_Status->bInterlace = stXCStatusEx.bInterlace;
    pstMMDS_Status->u16IPMOffset = stXCStatusEx.u16IPMOffset;
    pstMMDS_Status->u8StoreFrameNum = MApi_XC_Get_StoreFrameNum(SDK2DriverScalerWinTypeTrans(eWin));
    pstMMDS_Status->u16InputVFreq = stXCStatusEx.u16InputVFreq;
    pstMMDS_Status->u8FRCMStoreFrameNum = 0; //temp

    pstMMDS_Status->u8MVOPMirror = (MAPI_U8) enMirror;
    pstMMDS_Status->u16MVOPHStart = MDrv_MVOP_GetHStart();
    pstMMDS_Status->u16MVOPVStart = MDrv_MVOP_GetVStart();

    //VIDEO_DBG("Cap  %d, %d, %d, %d\n", pstMMDS_Status->stCapWin.x, pstMMDS_Status->stCapWin.y,
    //          pstMMDS_Status->stCapWin.width, pstMMDS_Status->stCapWin.height);
    //VIDEO_DBG("Crop %d, %d, %d, %d\n", pstMMDS_Status->stCropWin.x, pstMMDS_Status->stCropWin.y,
    //          pstMMDS_Status->stCropWin.width, pstMMDS_Status->stCropWin.height);
    //VIDEO_DBG("Disp %d, %d, %d, %d\n", pstMMDS_Status->stDispWin.x, pstMMDS_Status->stDispWin.y,
    //          pstMMDS_Status->stDispWin.width, pstMMDS_Status->stDispWin.height);

    //VIDEO_DBG("IPMBase 0/1/2 = %x, %x, %x\n", pstMMDS_Status->u32IPMBase0,
    //          pstMMDS_Status->u32IPMBase1, pstMMDS_Status->u32IPMBase2);
    //VIDEO_DBG("Linear %d, Bit/Pixel %d, Interlace %d\n", pstMMDS_Status->bLinearMode,
    //          pstMMDS_Status->u8BitPerPixel, pstMMDS_Status->bInterlace);

    pstMMDS_Status->bHKIsUpdating = FALSE;
    MsOS_FlushMemory();

    //@todo kidd: discuess with fangnao
    //pVideoPlayer->pCodec->DS_UpdateXCStatus(mapi_cpcodec::E_MM_DS_XC_CMD_UPDATE_XC_INFO);
    /*
        while(pstMMDS_Status->bFWGotXCInfo != TRUE)
        {
            MsOS_FlushMemory();
            //printf("FWIsUpdating %d\n", pstMMDS_Status->bFWIsUpdating);
        }
    */
}

void mapi_video::TransferSDKDispWinToReg(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE* const pstDispWin)
{
    if(mapi_syscfg_fetch::GetInstance()->GetMirrorVideoFlag())
    {
        CalcMirror((MS_WINDOW_TYPE *)pstDispWin);
    }
    pstDispWin->x += g_IPanel.HStart();
    pstDispWin->y += g_IPanel.VStart();
    return;
}

void mapi_video::TransferRegDispWinToSDK(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE* const pstDispWin)
{
    pstDispWin->x -= g_IPanel.HStart();
    pstDispWin->y -= g_IPanel.VStart();
    if(mapi_syscfg_fetch::GetInstance()->GetMirrorVideoFlag())
    {
        CalcMirror((MS_WINDOW_TYPE *)pstDispWin);
    }
    return;
}

void mapi_video::SetDispWinToDriver(const mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE* const pstDspwin, const MAPI_SCALER_WIN eWindow)
{
    // Calculate Mirror & Panel DE
    // Set to driver
    MS_WINDOW_TYPE stDispwin;
    stDispwin.x = pstDspwin->x;
    stDispwin.y = pstDspwin->y;
    stDispwin.width = pstDspwin->width;
    stDispwin.height= pstDspwin->height;
    TransferSDKDispWinToReg((mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *)(&stDispwin));
    MApi_XC_SetDispWinToReg(&stDispwin, SDK2DriverScalerWinTypeTrans(eWindow));
}

void mapi_video::GetDispWinFromDriver(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE* const pstDspwin, const MAPI_SCALER_WIN eWindow)
{
    MS_WINDOW_TYPE stDispwin;
    MApi_XC_GetDispWinFromReg(&stDispwin, SDK2DriverScalerWinTypeTrans(eWindow));
    TransferRegDispWinToSDK((mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *)(&stDispwin));
    pstDspwin->x = stDispwin.x;
    pstDspwin->y = stDispwin.y;
    pstDspwin->width = stDispwin.width;
    pstDspwin->height = stDispwin.height;
}

MAPI_BOOL mapi_video::GetHDuplicate(const MAPI_SCALER_WIN eWindow)
{
    XC_ApiStatusEx stXCStatusEx;
    MAPI_BOOL bHDuplicate = MAPI_FALSE;

    memset(&stXCStatusEx, 0, sizeof(XC_ApiStatusEx));
    stXCStatusEx.u16ApiStatusEX_Length = sizeof(XC_ApiStatusEx);
    stXCStatusEx.u32ApiStatusEx_Version = API_STATUS_EX_VERSION;

    if(MApi_XC_GetStatusEx(&stXCStatusEx, SDK2DriverScalerWinTypeTrans(eWindow)) == sizeof(XC_ApiStatusEx))
    {
        bHDuplicate = stXCStatusEx.bHDuplicate;
    }

    return bHDuplicate;
}

#if (H_LINEAR_SCALING_ENABLE == 1)
MS_BOOL mapi_video::SetHLinearScaling(MS_BOOL bEnable, MS_BOOL bSign, MS_U16 u16Delta, const MAPI_SCALER_WIN eWindow)
{
    return ((MApi_XC_SetHLinearScaling(bEnable, bSign, u16Delta, SDK2DriverScalerWinTypeTrans(eWindow)) == E_APIXC_RET_OK) ? TRUE : FALSE);
}
#endif

MAPI_BOOL mapi_video::GetScalerMemoryData(mapi_video_datatype::EN_MAPI_XC_OUTPUTDATA_TYPE eBufType, mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pRect, void *pRectBuf, MAPI_SCALER_WIN eWindow)
{
    MS_WINDOW_TYPE stWindowType;
    E_XC_OUTPUTDATA_TYPE eDriveOutputDataType = E_XC_OUTPUTDATA_RGB10BITS;

    stWindowType.x = pRect->x;
    stWindowType.y = pRect->y;
    stWindowType.width = pRect->width;
    stWindowType.height= pRect->height;

    switch(eBufType)
    {
        case mapi_video_datatype::E_MAPI_XC_OUTPUTDATA_RGB10BITS:
            eDriveOutputDataType = E_XC_OUTPUTDATA_RGB10BITS;
            break;
        case mapi_video_datatype::E_MAPI_XC_OUTPUTDATA_RGB8BITS:
            eDriveOutputDataType = E_XC_OUTPUTDATA_RGB8BITS;
            break;
        default:
            ASSERT(0);
            break;
    }

    MApi_XC_FreezeImg(TRUE, SDK2DriverScalerWinTypeTrans(eWindow));
    MApi_XC_Get_BufferData(eDriveOutputDataType, &stWindowType, pRectBuf, SDK2DriverScalerWinTypeTrans(eWindow));
    MApi_XC_FreezeImg(FALSE, SDK2DriverScalerWinTypeTrans(eWindow));
    return TRUE;
}

MAPI_BOOL mapi_video::SetDataOnScalerMemory(MAPI_BOOL bEnable, mapi_video_datatype::EN_MAPI_XC_INPUTDATA_TYPE eBufType, mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pRect, void *pDataBuf, MAPI_SCALER_WIN eWindow)
{
    MS_WINDOW_TYPE stMemWin;
    MS_WINDOW_TYPE stSourceWin;
    E_XC_INPUTDATA_TYPE eDriveInputDataType = E_XC_INPUTDATA_RGB10BITS;
    XC_ApiStatus DrvStatusInfo;
    MApi_XC_GetStatus(&DrvStatusInfo, SDK2DriverScalerWinTypeTrans(eWindow));

    if (pRect == NULL)
        return FALSE;

    stSourceWin.x = pRect->x;
    stSourceWin.y = pRect->y;
    stSourceWin.width = pRect->width;
    stSourceWin.height = pRect->height;

    switch(eBufType)
    {
        case mapi_video_datatype::E_MAPI_XC_INPUTDATA_RGB8BITS:
            eDriveInputDataType = E_XC_INPUTDATA_RGB8BITS;
            break;
        case mapi_video_datatype::E_MAPI_XC_INPUTDATA_4228BITS:
            eDriveInputDataType = E_XC_INPUTDATA_4228BITS;
            break;
        default:
            //ASSERT(0);
            return FALSE;
            break;
    }

    if (MAPI_TRUE == bEnable )
    {
        MApi_XC_GenerateBlackVideo(ENABLE,SDK2DriverScalerWinTypeTrans(eWindow));

        MApi_XC_FreezeImg(ENABLE, SDK2DriverScalerWinTypeTrans(eWindow));

        XC_SETWIN_INFO stXC_SetWin_Info;
        memset(&stXC_SetWin_Info, 0, sizeof(XC_SETWIN_INFO));

        stXC_SetWin_Info.stCapWin.x = stXC_SetWin_Info.stCropWin.x = stXC_SetWin_Info.stDispWin.x = stSourceWin.x;
        stXC_SetWin_Info.stCapWin.y = stXC_SetWin_Info.stCropWin.y = stXC_SetWin_Info.stDispWin.y = stSourceWin.y;
        stXC_SetWin_Info.stCapWin.width = stXC_SetWin_Info.stCropWin.width = stXC_SetWin_Info.stDispWin.width = stSourceWin.width;
        stXC_SetWin_Info.stCapWin.height = stXC_SetWin_Info.stCropWin.height = stXC_SetWin_Info.stDispWin.height = stSourceWin.height;

        stXC_SetWin_Info.bPreHCusScaling = TRUE;
        stXC_SetWin_Info.u16PreHCusScalingSrc = stXC_SetWin_Info.stCapWin.width;
        stXC_SetWin_Info.u16PreHCusScalingDst = stXC_SetWin_Info.u16PreHCusScalingSrc;

        stXC_SetWin_Info.bPreVCusScaling = TRUE;
        stXC_SetWin_Info.u16PreVCusScalingSrc = stXC_SetWin_Info.stCapWin.height;
        stXC_SetWin_Info.u16PreVCusScalingDst = stXC_SetWin_Info.u16PreVCusScalingSrc;

        if ((E_XC_INPUTDATA_4228BITS == eDriveInputDataType) && (INPUT_SOURCE_VGA == DrvStatusInfo.enInputSourceType))
            stXC_SetWin_Info.enInputSourceType = INPUT_SOURCE_YPBPR; // Error handling: not suport RGB 422 mode now!
        else
            stXC_SetWin_Info.enInputSourceType = DrvStatusInfo.enInputSourceType;

        if(MApi_XC_SetWindow(&stXC_SetWin_Info, sizeof(XC_SETWIN_INFO), SDK2DriverScalerWinTypeTrans(eWindow)) == MAPI_FALSE)
        {
            ERR_VIDEO(printf("MApi_XC_SetWindow failed!\n"));
        }

        stMemWin.x = 0;
        stMemWin.y = 0;
        stMemWin.width = stSourceWin.width;
        stMemWin.height = stSourceWin.height;

        MApi_XC_Set_BufferData(eDriveInputDataType, &stMemWin, pDataBuf, &stSourceWin, SDK2DriverScalerWinTypeTrans(eWindow));

        MApi_XC_GenerateBlackVideo(DISABLE, SDK2DriverScalerWinTypeTrans(eWindow));

        MApi_XC_ForceReadFrame(ENABLE, 0x00);
    }
    else
    {
        // TODO: need to restore SetWindow status here

        MApi_XC_ForceReadFrame(DISABLE, 0x00);

        MApi_XC_FreezeImg(DISABLE, SDK2DriverScalerWinTypeTrans(eWindow));
    }

    return TRUE;
}

void mapi_video::DS_SendZoomInfo2Firmware(const MAPI_U32 u32FM_Buf_Base, const mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE* const stNewCropWin, const mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE* const stNewDispWin, const MAPI_SCALER_WIN eWin)
{
    //ST_VIDEOPLAYER_INFO* pVideoPlayer = (ST_VIDEOPLAYER_INFO*)m_pVideoPlayer;
    mapi_video_datatype::ST_MM_DS_XC_STATUS *pstMMDS_Status;

    UNUSED(eWin);
    //VIDEO_DBG("DS_SendZoomInfo2Firmware %x\n", u32FM_Buf_Base);

    // direct write to shared memory with firmware
    //pstMMDS_Status = (mapi_video_datatype::ST_MM_DS_XC_STATUS *) MsOS_PA2KSEG1(u32FM_Buf_Base + (1024*3));
    //pstMMDS_Status = (mapi_video_datatype::ST_MM_DS_XC_STATUS *) _PA2VA(u32FM_Buf_Base + (1024 * 3));
    pstMMDS_Status = (mapi_video_datatype::ST_MM_DS_XC_STATUS *) _PA2VA(u32FM_Buf_Base);

    // copy scaler related information
    pstMMDS_Status->bFWGotNewSetting = FALSE;
    pstMMDS_Status->bHKIsUpdating = TRUE;
    MsOS_FlushMemory();

    pstMMDS_Status->stNewCropWin.x      = stNewCropWin->x;
    pstMMDS_Status->stNewCropWin.y      = stNewCropWin->y;
    pstMMDS_Status->stNewCropWin.width  = stNewCropWin->width;
    pstMMDS_Status->stNewCropWin.height = stNewCropWin->height;

    pstMMDS_Status->stNewDispWin.x      = stNewDispWin->x;
    pstMMDS_Status->stNewDispWin.y      = stNewDispWin->y;
    pstMMDS_Status->stNewDispWin.width  = stNewDispWin->width;
    pstMMDS_Status->stNewDispWin.height = stNewDispWin->height;
    MirrorMode_t enVideoMirror = MApi_XC_GetMirrorModeTypeEx(MAIN_WINDOW);
    if((enVideoMirror == MIRROR_HV) || (enVideoMirror == MIRROR_V_ONLY))
    {
        pstMMDS_Status->bMirrorMode = TRUE;
    }
    else
    {
        pstMMDS_Status->bMirrorMode = FALSE;
    }

#if (ENABLE_4K2K_NAPOLI == 1)
    /// Napoli 4K2K HW limitation, need to Convert 2K2K
    if(/*(pstMMDS_Status->u32PNL_WIDTH < pstMMDS_Status->stNewDispWin.width)&&*/(pstMMDS_Status->stNewDispWin.width > 3800))
    {
        pstMMDS_Status->stNewDispWin.x /= 2;
        pstMMDS_Status->stNewDispWin.width /= 2;
    }
#endif
    //VIDEO_DBG("NewCrop %d, %d, %d, %d\n", pstMMDS_Status->stNewCropWin.x, pstMMDS_Status->stNewCropWin.y,
    //          pstMMDS_Status->stNewCropWin.width, pstMMDS_Status->stNewCropWin.height);
    //VIDEO_DBG("NewDisp %d, %d, %d, %d\n", pstMMDS_Status->stNewDispWin.x, pstMMDS_Status->stNewDispWin.y,
    //          pstMMDS_Status->stNewDispWin.width, pstMMDS_Status->stNewDispWin.height);

    pstMMDS_Status->bHKIsUpdating = FALSE;
    MsOS_FlushMemory();

    //kidd
    //pVideoPlayer->pCodec->DS_UpdateXCStatus(mapi_cpcodec::E_MM_DS_XC_CMD_UPDATE_ZOOM_INFO);
}


MAPI_BOOL mapi_video::DS_MoveViewWindow(
        const MAPI_U32 u32DSAddr,
        mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstCropWin,
        mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstDispWin,
        const MAPI_U32 u32Height,
        const MAPI_U32 u32Width
        )
{
    MAPI_U16 u16VBoxWidth  = GetCurrentVirtualBoxWidth(MAPI_MAIN_WINDOW);  //only support DS on main window now
    MAPI_U16 u16VBoxHeight = GetCurrentVirtualBoxHeight(MAPI_MAIN_WINDOW); //only support DS on main window now

    mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stDispWin0;

    memcpy(&stDispWin0, pstDispWin, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE));

    pstCropWin->width = (MAPI_U16)(u16VBoxWidth * pstCropWin->width / u32Width);
    pstCropWin->x = (MAPI_U16)(u16VBoxWidth * pstCropWin->x / u32Width);
    pstCropWin->height = (MAPI_U16)(u16VBoxHeight * pstCropWin->height / u32Height);
    pstCropWin->y = (MAPI_U16)(u16VBoxHeight * pstCropWin->y / u32Height);


    //firmware need know real position no matter mirror or not
    if(mapi_syscfg_fetch::GetInstance()->GetMirrorVideoFlag())
    {
        MirrorMode_t enVideoMirror = (MirrorMode_t)mapi_syscfg_fetch::GetInstance()->getMirrorVideoMode();
        if ((enVideoMirror == MIRROR_HV) || (enVideoMirror == MIRROR_H_ONLY))
        {
            if(g_IPanel.Width() > (stDispWin0.x + stDispWin0.width))
            {
                stDispWin0.x = g_IPanel.Width() - (stDispWin0.x + stDispWin0.width);
            }
            else
            {
                stDispWin0.x = 0;
            }
        }

        if ((enVideoMirror == MIRROR_HV) || (enVideoMirror == MIRROR_V_ONLY))
        {
            if(g_IPanel.Height() > (stDispWin0.y + stDispWin0.height))
            {
                stDispWin0.y = g_IPanel.Height() - (stDispWin0.y + stDispWin0.height);
            }
            else
            {
                stDispWin0.y = 0;
            }
        }
    }

    DS_SendZoomInfo2Firmware(u32DSAddr, pstCropWin, &stDispWin0, MAPI_MAIN_WINDOW);

    return MAPI_TRUE;
}

MAPI_BOOL mapi_video::DS_ViewZooming(
        const MAPI_U32 u32DSAddr,
        mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstCropWin,
        mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE *pstDispWin,
        const MAPI_U32 u32CropBottom,
        const MAPI_U32 u32Height,
        const MAPI_U32 u32Width
        )
{
    mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stDispWin0;

    memcpy(&stDispWin0, pstDispWin, sizeof(mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE));

    SetDispWinToDriver(&stDispWin0, MAPI_MAIN_WINDOW);

    DS_SendZoomInfo2Firmware(u32DSAddr, pstCropWin, &stDispWin0, MAPI_MAIN_WINDOW);

    return MAPI_TRUE;
}

//-------------------------------------------------------------------------------------------------
/// Capture live screec by DWIN
/// @param u8Type \b IN : 0 UV7Y8, 1 UV8Y8, 2 ARGB8888, 3 RGB565.
/// @param u32BufAddr \b IN : buffer address for capture useage, must be physical address and in MIU0!!!
/// @param u32BufSize \b IN : buffer size for capture useage
/// @param pu32Width \b OUT : capture width
/// @param pu32Height \b OUT : capture height
/// @return MAPI_BOOL   \b TRUE : success , FALSE : Failed
//-------------------------------------------------------------------------------------------------
#define GOP_PIXEL_ALIGNMENT_FACTOR (0x0F) //Alignment 16 pixel
MAPI_BOOL mapi_video::CaptureScreen(MAPI_U8 u8Type, MAPI_U32 u32BufAddr, MAPI_U32 u32BufSize, MAPI_U32 * pu32Width, MAPI_U32 *pu32Height)
{
    XC_ApiStatus stXC_WinTime_Info;
    GOP_DwinProperty dwinProperty;
    EN_GOP_DWIN_DATA_FMT fmt = (EN_GOP_DWIN_DATA_FMT)u8Type;
    MAPI_U8 u8AlignPixelNum;
    MAPI_U8 u8Pitch = (fmt == DWIN_DATA_FMT_ARGB8888) ? 4 : 2;

    if (MApi_XC_GetStatus(&stXC_WinTime_Info, MAIN_WINDOW) == FALSE)
    {
        ERR_VIDEO(printf("MApi_XC_GetStatus failed because of InitData wrong, please update header file and compile again\n"));
    }

    if ((MAPI_U32)(stXC_WinTime_Info.stDispWin.width * stXC_WinTime_Info.stDispWin.height * u8Pitch) > u32BufSize)
    {
        *pu32Width = 0;
        *pu32Height = 0;
        return FALSE;
    }

    if (fmt == DWIN_DATA_FMT_UV8Y8)
    {
        MApi_GOP_DWIN_EnableR2YCSC(ENABLE);
        MApi_GFX_ClearFrameBufferByWord(u32BufAddr, u32BufSize, 0x80108010);
    }
    else
    {
        MApi_GOP_DWIN_EnableR2YCSC(DISABLE);
        MApi_GFX_ClearFrameBuffer(u32BufAddr, u32BufSize, 0x0);
    }

    MApi_GOP_SetClkForCapture();
    MApi_GOP_DWIN_Init();
    MApi_GOP_DWIN_SelectSourceScanType(DWIN_SCAN_MODE_PROGRESSIVE);
    MApi_GOP_DWIN_SetDataFmt(fmt);
    MApi_GOP_DWIN_SetSourceSel(DWIN_SRC_OP);
#if 1 //New method to set capture size according to HW capablility
    GOP_DWIN_CAP DwinCap;
    MApi_GOP_GetChipCaps(E_GOP_CAP_DWIN, (MS_U32*)(&DwinCap), sizeof(GOP_DWIN_CAP));
    if(DwinCap.bSupportWindowDECapture == FALSE)
    {
        dwinProperty.u16x = stXC_WinTime_Info.stDispWin.x;
        dwinProperty.u16y = stXC_WinTime_Info.stDispWin.y;
        MApi_XC_OP2VOPDESel(E_OP2VOPDE_WHOLEFRAME);
    }
    else
#endif
    {
        dwinProperty.u16x = 0;
        dwinProperty.u16y = 0;
        MApi_XC_OP2VOPDESel(E_OP2VOPDE_MAINWINDOW);
    }
    u8AlignPixelNum = dwinProperty.u16x & GOP_PIXEL_ALIGNMENT_FACTOR; //alignment pixels
    u8AlignPixelNum = (GOP_PIXEL_ALIGNMENT_FACTOR + 1) - u8AlignPixelNum; //Complement pixel for up ward alignment
    if(u8AlignPixelNum != (GOP_PIXEL_ALIGNMENT_FACTOR + 1))
    {
        // Alignment upward to avoid get left black frame data
        dwinProperty.u16x = dwinProperty.u16x + u8AlignPixelNum;
        //Alignment downward for Width to avoid get right black frame data
        dwinProperty.u16w = (stXC_WinTime_Info.stDispWin.width - u8AlignPixelNum) & (~GOP_PIXEL_ALIGNMENT_FACTOR);
    }
    else
    {
        //Alignment downward for Width to avoid get right black frame data
        dwinProperty.u16w = (stXC_WinTime_Info.stDispWin.width) & (~GOP_PIXEL_ALIGNMENT_FACTOR);
    }
    dwinProperty.u16fbw = dwinProperty.u16w;
    dwinProperty.u16h = stXC_WinTime_Info.stDispWin.height;
    dwinProperty.u32fbaddr0 = u32BufAddr;
    dwinProperty.u32fbaddr1 = u32BufAddr + u32BufSize;
    MApi_GOP_DWIN_SetWinProperty(&dwinProperty);
    MApi_GOP_DWIN_SetAlphaValue(0x0f);

    MsOS_DelayTask(10);
    MApi_GFX_FlushQueue();
    MApi_GOP_DWIN_CaptureOneFrame();
    MsOS_DelayTask(50);
    MApi_GOP_DWIN_Enable(FALSE);
    MApi_GOP_DWIN_EnableR2YCSC(DISABLE);

    *pu32Width = dwinProperty.u16w;
    *pu32Height = dwinProperty.u16h;

    return TRUE;

}

//-------------------------------------------------------------------------------------------------
/// Capture live screec by DWIN and save it as file .
/// @param logo_name \b IN : Logo file name.
/// @return MAPI_BOOL   \b TRUE : success , FALSE : Failed
/// This function get the limits, you MUST use the RGB565 format , need the 4M memory size, USE MIU0 only. attention please.
//-------------------------------------------------------------------------------------------------
MAPI_BOOL mapi_video::CaptureScreenToJpeg(char *logo_name)
{
#ifndef MI_MSDK
    unsigned char * dataBuffer =NULL;
    unsigned char *image = NULL;
    MAPI_U32 u32Width,u32Height,u32BufferSize;
    MAPI_U32 bufPhysicalAddr = 0;
    MMapInfo_t *pMappingBuffer = NULL;
    int x,y;
    MAPI_U16 *pixel16;
    MAPI_U16 temp;
    MAPI_U32 pitch =0;
    MAPI_U32 d =0;
    MAPI_U16   pR, pG, pB;
    int quality = 80;
    FILE *fp;
    #define CAPTURE_FRAME_WIDTH 1920 // Capture frame width.
    #define CAPTURE_FRAME_HEIGHT 1080 // Capture frame height.
    #define CAPTURE_BYTE_PERPIXEL 2 // RGB565 need 2 bytes.
    #define CAPTURE_PIXEL_FORMAT 3 // // RGB565 format.

    pMappingBuffer = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_CAPTURESCREEN"));
    if ((pMappingBuffer == NULL) || (pMappingBuffer->u32MiuNo == 1))
    {
        ERR_VIDEO(printf("====Error :can't get memory or MIU setting error!!!\n"));
        return FALSE;
    }

    if(pMappingBuffer->u32Size >= ((MAPI_U32)(CAPTURE_FRAME_WIDTH*CAPTURE_FRAME_HEIGHT*CAPTURE_BYTE_PERPIXEL)))
    {
        u32BufferSize =  (MAPI_U32)(CAPTURE_FRAME_WIDTH*CAPTURE_FRAME_HEIGHT*CAPTURE_BYTE_PERPIXEL);
    }
    else
    {
        ERR_VIDEO(printf("====Error : memory size not enough!!!\n"));
        return FALSE;
    }

    if(logo_name == NULL)
    {
        return FALSE;
    }
    bufPhysicalAddr =  pMappingBuffer->u32Addr;

    CaptureScreen(CAPTURE_PIXEL_FORMAT,bufPhysicalAddr,u32BufferSize,&u32Width,&u32Height);

    dataBuffer =(unsigned char *)_PA2VA(bufPhysicalAddr);
    image = (unsigned char *)malloc((size_t)(u32Width * u32Height * 3));
    if (image == NULL)
    {
        return TRUE;
    }

   pitch = u32Width * CAPTURE_BYTE_PERPIXEL;

   for (y=0; y<(int)u32Height; y++)
   {
        pixel16 = (MAPI_U16*)((MAPI_U8*)dataBuffer + y * pitch);

        for (x=0; x < (int)u32Width; x++)
        {
            temp = pixel16[x];

            pR = (temp & 0xF800) >> 11;
            image[d]= (char) pR;
            image[d] = image[d] << 3;
            d ++;

            pG = (temp & 0x07E0) >> 5;
            image[d]= (char) pG;
            image[d] = image[d] << 2;
            d ++;

            pB = (temp & 0x001f);
            image[d]= (char) pB;
            image[d] = image[d] << 3;
            d ++;
        }
    }

    jpeg_compress_struct jinfo;
    jpeg_error_mgr jerr;

    jinfo.err = jpeg_std_error(&jerr);

    jpeg_create_compress(&jinfo);
    fp = fopen(logo_name, "wb");
    if(NULL == fp)
    {
        ERR_VIDEO(printf("====Error :fopen file error!!!\n"));
        free(image);
        return FALSE;
    }

    jpeg_stdio_dest(&jinfo, fp);

    jinfo.image_width = u32Width;
    jinfo.image_height = u32Height;
    jinfo.input_components = 3;
    jinfo.in_color_space = JCS_RGB;

    jpeg_set_defaults(&jinfo);
    jpeg_set_quality(&jinfo, quality, TRUE);
    jpeg_start_compress(&jinfo, TRUE);

    JSAMPROW row_pointer[1];
    row_pointer[0] = image;

    for(int i=0;i<(int)u32Height;i++)
    {
        row_pointer[0] = image + i*3*u32Width;
        jpeg_write_scanlines(&jinfo, row_pointer, 1);
    }

    jpeg_finish_compress(&jinfo);
    jpeg_destroy_compress(&jinfo);

    fclose(fp);
    free(image);
#endif

    return TRUE;

}


//-------------------------------------------------------------------------------------------------
/// Capture mvop output by DWIN
/// @param efmt \b IN : 0 UV7Y8, 1 UV8Y8, 2 ARGB8888, 3 RGB565.
/// @param bInterlaced \b IN : input is interlaced or not
/// @param u32BufAddr \b IN : buffer address for capture useage
/// @param u32BufSize \b IN : buffer size for capture useage
/// @param pstDwinWindow \b OUT : capture window
/// @return MAPI_BOOL   \b TRUE : success , FALSE : Failed
//-------------------------------------------------------------------------------------------------
MAPI_BOOL mapi_video::CaptureMVopOutput(const mapi_video_datatype::EN_MAPI_GOP_DWIN_DATA_FMT efmt, const MAPI_BOOL bInterlaced, const MAPI_U32 u32BufAddr, const MAPI_U32 u32BufSize, mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE* const pstDwinWindow)
{
    GOP_DwinProperty dwinProperty;
    EN_GOP_DWIN_DATA_FMT fmt = (EN_GOP_DWIN_DATA_FMT)efmt;
    MAPI_U8 u8Pitch;

    if(fmt >= DWIN_DATA_FMT_MAX)
    {
        ERR_VIDEO(printf("fmt is invalid.\n"));
        return MAPI_FALSE;
    }

    u8Pitch = (fmt == DWIN_DATA_FMT_ARGB8888) ? 4 : 2;

    if ((MAPI_U32)(pstDwinWindow->width * pstDwinWindow->height * u8Pitch) > u32BufSize)
    {
        return MAPI_FALSE;
    }

    MApi_GOP_DWIN_EnableR2YCSC(FALSE);
    if (fmt == DWIN_DATA_FMT_UV8Y8)
    {
        MApi_GFX_ClearFrameBufferByWord(u32BufAddr, u32BufSize, 0x80108010);
    }
    else
    {
        MApi_GFX_ClearFrameBuffer(u32BufAddr, u32BufSize, 0x0);
    }
    MApi_GFX_FlushQueue();

    MApi_GOP_SetClkForCapture();
    MApi_GOP_DWIN_Init();
    if(bInterlaced)
    {
        MApi_GOP_DWIN_SelectSourceScanType(DWIN_SCAN_MODE_extern);
    }
    else
    {
        MApi_GOP_DWIN_SelectSourceScanType(DWIN_SCAN_MODE_PROGRESSIVE);
    }
    MApi_GOP_DWIN_SetDataFmt(fmt);
    MApi_GOP_DWIN_SetSourceSel(DWIN_SRC_MVOP);
    dwinProperty.u16x = pstDwinWindow->x;
    dwinProperty.u16y = pstDwinWindow->y;
    dwinProperty.u16w = pstDwinWindow->width;
    dwinProperty.u16fbw = dwinProperty.u16w;
    if(bInterlaced)
    {
        dwinProperty.u16h = pstDwinWindow->height;
    }
    else
    {
        dwinProperty.u16h = pstDwinWindow->height;
    }

    dwinProperty.u32fbaddr0 = u32BufAddr;
    dwinProperty.u32fbaddr1 = u32BufAddr + u32BufSize;
    MApi_GOP_DWIN_SetWinProperty(&dwinProperty);
    if(fmt == DWIN_DATA_FMT_ARGB8888)
    {
        MApi_GOP_DWIN_SetAlphaValue(0xff);
    }

    MsOS_DelayTask(10);
    MApi_GOP_DWIN_CaptureOneFrame();
    MsOS_DelayTask(50);
    MApi_GOP_DWIN_Enable(FALSE);
    MApi_GOP_DWIN_EnableR2YCSC(DISABLE);


    return MAPI_TRUE;

}


const ResolutionInfoSize mapi_video::GetVideoResSize(VideoInfo_t E_VideoInfo)
{
    const ResolutionInfoSize* const m_ResoSize = mapi_syscfg_fetch::GetInstance()->GetResolutionInfo();

    if(E_VideoInfo == E_DTV)
    {
        if(m_ResoSize[E_DTV].nResolutionSize != 0)
        {
            return (const ResolutionInfoSize)m_ResoSize[E_DTV];
        }
        else
        {
            ASSERT(0);
        }
    }
    else if(E_VideoInfo == E_HDMI)
    {
        if(m_ResoSize[E_HDMI].nResolutionSize != 0)
        {
            return (const ResolutionInfoSize)m_ResoSize[E_HDMI];
        }
        else
        {
            ASSERT(0);
        }
    }
    else if(E_VideoInfo == E_YPbPr)
    {
        if(m_ResoSize[E_YPbPr].nResolutionSize != 0)
        {
            return (const ResolutionInfoSize)m_ResoSize[E_YPbPr];
        }
        else
        {
            ASSERT(0);
        }
    }
    else if(E_VideoInfo == E_CVBS)
    {
        if(m_ResoSize[E_CVBS].nResolutionSize != 0)
        {
            return (const ResolutionInfoSize)m_ResoSize[E_CVBS];
        }
        else
        {
            ASSERT(0);
        }
    }
    else if(E_VideoInfo == E_RVU)
    {
        if(m_ResoSize[E_RVU].nResolutionSize != 0)
        {
            return (const ResolutionInfoSize)m_ResoSize[E_RVU];
        }
        else
        {
            ASSERT(0);
        }
    }
    ASSERT(0);
}

MAPI_BOOL mapi_video::KeepOpTiming4k2k(const MAPI_BOOL bEnable)
{
    MAPI_BOOL bRet = MAPI_FALSE;
#if (MSTAR_TVOS == 1 && STB_ENABLE == 0)
    if(mapi_syscfg_fetch::GetInstance()->GetFrcMode() == TRUE)
    {
    E_APIXC_ReturnValue eRet = E_APIXC_RET_FAIL;
    XC_INITMISC stXC_Init_Misc;

    memset(&stXC_Init_Misc, 0, sizeof(XC_INITMISC));
    MApi_XC_GetMISCStatus(&stXC_Init_Misc);

    if(MAPI_TRUE == bEnable)
    {
        stXC_Init_Misc.u32MISC_A |= E_XC_INIT_MISC_A_FRC_INSIDE_KEEP_OP_4K2K;
    }
    else
    {
        stXC_Init_Misc.u32MISC_A &= ~E_XC_INIT_MISC_A_FRC_INSIDE_KEEP_OP_4K2K;
    }

    eRet = MApi_XC_Init_MISC(&stXC_Init_Misc, sizeof(XC_INITMISC));
    if(E_APIXC_RET_OK == eRet)
    {
        bRet = MAPI_TRUE;
    }
    else
    {
        bRet = MAPI_FALSE;
    }
    }
#endif
    return bRet;
}
#if (STB_ENABLE == 0)
MAPI_BOOL mapi_video::SetFRCInputTiming(E_XC_FRC_InputTiming enFRC_InputTiming )
{
    if(mapi_syscfg_fetch::GetInstance()->GetFrcMode() == TRUE)
    {
    if(MApi_XC_Set_FRC_InputTiming(enFRC_InputTiming) != E_APIXC_RET_OK)
        return FALSE;
    else
            return TRUE;
    }
    else
    {
         return FALSE;
    }
}
#endif
void  mapi_video::SetFRCWindow(E_XC_3D_INPUT_MODE e3dInputMode, E_XC_3D_OUTPUT_MODE e3dOutputMode, E_XC_3D_PANEL_TYPE e3dPanelType)
{
    if(mapi_syscfg_fetch::GetInstance()->GetFrcMode() == TRUE)
    {
     MApi_XC_FRC_SetWindow(e3dInputMode, e3dOutputMode, e3dPanelType);
}
}

MAPI_BOOL mapi_video::IsPipMode()
{
    if(m_ePipMode == E_MAPI_PIP_MODE_OFF)
        return MAPI_FALSE;
    else
        return MAPI_TRUE;

    return MAPI_FALSE;
}

void mapi_video::SetPipMode(EN_MAPI_PIP_MODES ePipMode)
{
#if (STB_ENABLE == 0) && (HDMITX_ENABLE == 0)
    MS_U32 u32Version = 0;
    if(MApi_XC_GetChipCaps(E_XC_2DTO3D_VERSION,&u32Version,0) != E_APIXC_RET_OK)
    {
        ERR_VIDEO(printf("[Error] can not get Chip Caps\n"));
    }
    //Chip patch is only for nike,napoli,einstein
    //The HW limitation causes between T3D and PIP are not enabled simultaneously.
    else if(3 == u32Version)
    {
        MAPI_U16 u16ActWin = MApi_GOP_GWIN_GetActiveGWIN();
        MAPI_U16 u16WinNum = MApi_GOP_GWIN_GetMAXWinID();
        MAPI_U8 u8GwinIdx = 0;
        if (((E_MAPI_PIP_MODE_OFF == m_ePipMode) || (E_MAPI_PIP_MODE_TRAVELING == m_ePipMode)) && (E_MAPI_PIP_MODE_POP == ePipMode))
        {
            for (u8GwinIdx = 0; u8GwinIdx < u16WinNum; u8GwinIdx++)
            {
                if (0 != (u16ActWin & (1<<u8GwinIdx)))
                {
                    MApi_GOP_GWIN_Enable(u8GwinIdx, FALSE);
                }
            }
            MApi_GOP_GWIN_EnableT3DMode(FALSE);
            MApi_XC_EnableT3D(FALSE);
            for (u8GwinIdx = 0; u8GwinIdx < u16WinNum; u8GwinIdx++)
            {
                if (0 != (u16ActWin & (1<<u8GwinIdx)))
                {
                    MApi_GOP_GWIN_Enable(u8GwinIdx, TRUE);
                }
            }
        }
        else if (((E_MAPI_PIP_MODE_OFF == ePipMode) || (E_MAPI_PIP_MODE_TRAVELING == ePipMode)) && (E_MAPI_PIP_MODE_POP == m_ePipMode))
        {
            for (u8GwinIdx = 0; u8GwinIdx < u16WinNum; u8GwinIdx++)
            {
                if (0 != (u16ActWin & (1<<u8GwinIdx)))
                {
                    MApi_GOP_GWIN_Enable(u8GwinIdx, FALSE);
                }
            }
            MApi_XC_EnableWindow(FALSE, SUB_WINDOW);
            MApi_GOP_GWIN_EnableT3DMode(TRUE);
            MApi_XC_EnableT3D(TRUE);
            for (u8GwinIdx = 0; u8GwinIdx < u16WinNum; u8GwinIdx++)
            {
                if (0 != (u16ActWin & (1<<u8GwinIdx)))
                {
                    MApi_GOP_GWIN_Enable(u8GwinIdx, TRUE);
                }
             }
        }
    }
#endif

#if ((PIP_ENABLE == 1) && (TRAVELING_ENABLE == 1))
    //When traveling main source,reseting traveling window may be needed if pip mode changed.
    //Use m_bForceSetTravelingWin to confirm if reseting traveling window is necessary.
    if(((m_ePipMode != E_MAPI_PIP_MODE_PIP) && (ePipMode == E_MAPI_PIP_MODE_PIP))
        ||((m_ePipMode == E_MAPI_PIP_MODE_PIP) && (ePipMode != E_MAPI_PIP_MODE_PIP)))
    {
        m_bForceSetTravelingWin = MAPI_TRUE;
    }
    else
    {
        m_bForceSetTravelingWin = MAPI_FALSE;
    }
#endif

    m_ePipMode = ePipMode;
}

void mapi_video::SetPipMainSubInputSourceType(MAPI_INPUT_SOURCE_TYPE eMainInputSrc, MAPI_INPUT_SOURCE_TYPE eSubInputSrc)
{
    m_eMainInputSource = eMainInputSrc;
    m_eSubInputSource = eSubInputSrc;
}

MAPI_INPUT_SOURCE_TYPE mapi_video::GetPipInputSourceType(MAPI_SCALER_WIN eWin)
{
    if (eWin == MAPI_MAIN_WINDOW)
        return m_eMainInputSource;
    else
        return m_eSubInputSource;
}

MAPI_U16 mapi_video::GetOutputVFreq()
{
    return (MApi_XC_GetOutputVFreqX100() + 5) / 10;
}

MAPI_BOOL mapi_video::SetVideoOnOSD(const mapi_video_datatype::EN_MAPI_VIDEO_ON_OSD_LAYER enlayer,  const MAPI_SCALER_WIN eWindow)
{
    E_APIXC_ReturnValue ret = E_APIXC_RET_FAIL;
    ret =  MApi_XC_SetVideoOnOSD(static_cast<E_VIDEO_ON_OSD_LAYER>(enlayer), static_cast<SCALER_WIN>(eWindow));

    if(ret != E_APIXC_RET_OK)
    {
        return FALSE;
    }
    else
    {
        return TRUE;
    }
}

#if (STB_ENABLE == 1)
#if (DTV_CHANNEL_CHANGE_FREEZE_IMAGE_ENBALE == 1)
MAPI_BOOL mapi_video::isSupportSeamlessZapping(void)
{
    MS_U32 u32Ret = 0;

    if (MApi_XC_GetChipCaps(E_XC_HW_SEAMLESS_ZAPPING, &u32Ret, 0) != E_APIXC_RET_OK)
    {
        ERR_VIDEO(printf("[Error] can not get Chip Caps\n"));
        return MAPI_FALSE;
    }

    if (u32Ret == 0)
    {
        return MAPI_FALSE;
    }

    return MAPI_TRUE;
}

MAPI_BOOL mapi_video::isEnableSeamlessZapping(MAPI_SCALER_WIN eWindow)
{
    return m_bEnableSeamlessZapping;
}

MAPI_BOOL mapi_video::setupSeamlessZapping(MAPI_BOOL bEnable, MAPI_SCALER_WIN eWindow)
{
    MS_U32 u32Ret = 0;
#if (STB_ENABLE == 1)
#if (DUAL_XC_ENABLE == 1)
    XC_DEVICE_ID stXC_DeviceId = {E_DEVICE_VERSION_0, E_DEVICE_ID_1};
#endif
#endif

    m_bEnableSeamlessZapping = FALSE;

    if (MApi_XC_GetChipCaps(E_XC_HW_SEAMLESS_ZAPPING, &u32Ret, 0) != E_APIXC_RET_OK)
    {
        ERR_VIDEO(printf("[Error] can not get Chip Caps\n"));
        return MAPI_FALSE;
    }
    else if (u32Ret == 0)
    {
        ERR_VIDEO(printf("[Error] does not support Seamless Zapping\n"));
        return MAPI_FALSE;
    }

    if (bEnable)
    {
        if (MApi_XC_GetDynamicScalingStatus() == FALSE)
        {
            mapi_video_datatype::ST_MAPI_DYNAMICSCALING_INFO stDS_Info;
            memset(&stDS_Info, 0, sizeof(mapi_video_datatype::ST_MAPI_DYNAMICSCALING_INFO));

            MMapInfo_t *pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_XC_DS"));

            if (pMMap == NULL)
            {
                ERR_VIDEO(printf("[Error] can not get mmap info: E_MMAP_ID_XC_DS\n"));
                return MAPI_FALSE;
            }
            stDS_Info.u32DS_Info_BaseAddr = pMMap->u32Addr;
            stDS_Info.u8MIU_Select = pMMap->u32MiuNo;
            stDS_Info.u8DS_Index_Depth = 32;
            stDS_Info.bOP_DS_On = TRUE;
            stDS_Info.bIPS_DS_On = FALSE;
            stDS_Info.bIPM_DS_On = FALSE;
            MSG_VIDEO(printf("[DS info] Addr=0x%x  MIU=%u \n", (unsigned int)stDS_Info.u32DS_Info_BaseAddr, (unsigned int)stDS_Info.u8MIU_Select));

            if (SetDynamicScaling(&stDS_Info, sizeof(mapi_video_datatype::ST_MAPI_DYNAMICSCALING_INFO), eWindow) == FALSE)
            {
                ERR_VIDEO(printf("[Error] SetDynamicScaling fail\n"));
                return MAPI_FALSE;
            }
        }

        if(MApi_XC_SetSeamlessZapping(SDK2DriverScalerWinTypeTrans(eWindow), bEnable) != E_APIXC_RET_OK)
        {
            // Disable Dynamic Scaling
            mapi_video_datatype::ST_MAPI_DYNAMICSCALING_INFO stDS_Info;
            memset(&stDS_Info, 0, sizeof(mapi_video_datatype::ST_MAPI_DYNAMICSCALING_INFO));
            stDS_Info.bOP_DS_On = FALSE;
            stDS_Info.bIPS_DS_On = FALSE;
            stDS_Info.bIPM_DS_On = FALSE;
            SetDynamicScaling(&stDS_Info, sizeof(mapi_video_datatype::ST_MAPI_DYNAMICSCALING_INFO), eWindow);

            ERR_VIDEO(printf("[Error] MApi_XC_SetSeamlessZapping fail\n"));
            return MAPI_FALSE;
        }

#if (STB_ENABLE == 1)
#if (DUAL_XC_ENABLE == 1)
        if((MAPI_MAIN_WINDOW == eWindow) && (E_MAPI_PIP_MODE_OFF == m_ePipMode))
        {
            if (MApi_XC_EX_GetDynamicScalingStatus(&stXC_DeviceId) == FALSE)
            {
                mapi_video_datatype::ST_MAPI_DYNAMICSCALING_INFO stDS_Info;
                memset(&stDS_Info, 0, sizeof(mapi_video_datatype::ST_MAPI_DYNAMICSCALING_INFO));

    //            MMapInfo_t *pMMap = MMAPInfo::GetInstance()->get_mmap(E_MMAP_ID_XC1_DS);
                MMapInfo_t *pMMap = MMAPInfo::GetInstance()->get_mmap(MMAPInfo::GetInstance()->StrToMMAPID("E_MMAP_ID_XC1_DS"));

                if (pMMap == NULL)
                {
                    ERR_VIDEO(printf("[Error] can not get mmap info: E_MMAP_ID_XC1_DS\n"));
                    return MAPI_FALSE;
                }
                stDS_Info.u32DS_Info_BaseAddr = pMMap->u32Addr;
                stDS_Info.u8MIU_Select = pMMap->u32MiuNo;
                stDS_Info.u8DS_Index_Depth = 32;
                stDS_Info.bOP_DS_On = TRUE;
                stDS_Info.bIPS_DS_On = FALSE;
                stDS_Info.bIPM_DS_On = FALSE;
                MSG_VIDEO(printf("[XC1 DS info] Addr=0x%x  MIU=%u \n", (unsigned int)stDS_Info.u32DS_Info_BaseAddr, (unsigned int)stDS_Info.u8MIU_Select));

                if (SetDynamicScalingEx(&stDS_Info, sizeof(mapi_video_datatype::ST_MAPI_DYNAMICSCALING_INFO), eWindow) == FALSE)
                {
                    ERR_VIDEO(printf("[Error] SetDynamicScaling EX  fail"));
                    return MAPI_FALSE;
                }
            }

            if(MApi_XC_EX_SetSeamlessZapping(&stXC_DeviceId, E_XC_EX_MAIN_WINDOW, bEnable) != E_XC_EX_RET_OK)
            {
                // Disable Dynamic Scaling
                mapi_video_datatype::ST_MAPI_DYNAMICSCALING_INFO stDS_Info;
                memset(&stDS_Info, 0, sizeof(mapi_video_datatype::ST_MAPI_DYNAMICSCALING_INFO));
                stDS_Info.bOP_DS_On = FALSE;
                stDS_Info.bIPS_DS_On = FALSE;
                stDS_Info.bIPM_DS_On = FALSE;
                SetDynamicScalingEx(&stDS_Info, sizeof(mapi_video_datatype::ST_MAPI_DYNAMICSCALING_INFO), eWindow);

                ERR_VIDEO(printf("[Error] MApi_XC_SetSeamlessZapping fail\n"));
                return MAPI_FALSE;
            }
        }
#endif
#endif
    }
    else
    {
        // Disable Seamless Zapping
        MApi_XC_SetSeamlessZapping(SDK2DriverScalerWinTypeTrans(eWindow), bEnable);

        // Disable Dynamic Scaling
        mapi_video_datatype::ST_MAPI_DYNAMICSCALING_INFO stDS_Info;
        memset(&stDS_Info, 0, sizeof(mapi_video_datatype::ST_MAPI_DYNAMICSCALING_INFO));
        stDS_Info.bOP_DS_On = FALSE;
        stDS_Info.bIPS_DS_On = FALSE;
        stDS_Info.bIPM_DS_On = FALSE;
        SetDynamicScaling(&stDS_Info, sizeof(mapi_video_datatype::ST_MAPI_DYNAMICSCALING_INFO), eWindow);

#if (STB_ENABLE == 1)
#if (DUAL_XC_ENABLE == 1)
        if((MAPI_MAIN_WINDOW == eWindow) && (E_MAPI_PIP_MODE_OFF == m_ePipMode))
        {
            // Disable Seamless Zapping
            MApi_XC_EX_SetSeamlessZapping(&stXC_DeviceId, E_XC_EX_MAIN_WINDOW, bEnable);

            // Disable Dynamic Scaling
            //mapi_video_datatype::ST_MAPI_DYNAMICSCALING_INFO stDS_Info;
            memset(&stDS_Info, 0, sizeof(mapi_video_datatype::ST_MAPI_DYNAMICSCALING_INFO));
            stDS_Info.bOP_DS_On = FALSE;
            stDS_Info.bIPS_DS_On = FALSE;
            stDS_Info.bIPM_DS_On = FALSE;
            SetDynamicScalingEx(&stDS_Info, sizeof(mapi_video_datatype::ST_MAPI_DYNAMICSCALING_INFO), eWindow);
        }
#endif
#endif
    }

    m_bEnableSeamlessZapping = bEnable;

    return MAPI_TRUE;
}
#endif
#endif

void mapi_video::disableInputSource(MAPI_BOOL bDisable, MAPI_SCALER_WIN eWindow)
{
    MApi_XC_DisableInputSource(bDisable, SDK2DriverScalerWinTypeTrans(eWindow));
}

#if (STB_ENABLE == 0) //wait for K2,K3 utopia release
MAPI_BOOL mapi_video::MakeOutputDeviceHandshake(void)
{
    MAPI_BOOL bEnable_4k2k = MAPI_FALSE;
    mapi_syscfg_fetch::GetInstance()->GetModuleParameter_bool("M_BACKEND:F_BACKEND_ENABLE_4K2K", &bEnable_4k2k);
    if( bEnable_4k2k == MAPI_TRUE)
    {
        MAPI_BOOL bResult = FALSE;

        bResult = MApi_PNL_OutputDeviceHandshake();

        if (FALSE == bResult)
        {
            //ERR_VIDEO(printf("\033[0;36m [%s][%d] VB1 lock fail ????  \033[0m\n", __FUNCTION__, __LINE__));
        }

        return bResult;
    }
    return 0;
}
#endif

EN_TRAVELING_RETURN mapi_video::GetTravelingEngineCaps(ST_TRAVELING_ENGINE_CAPS *pstTravelingCaps, EN_TRAVELING_ENGINE_TYPE enEngineType)
{
#if (TRAVELING_ENABLE == 1)
    if((pstTravelingCaps == NULL) || (enEngineType >= E_TRAVELING_ENGINE_TYPE_MAX))
    {
        printf("[DIPC] Error[%s:%d], Wrong Input Para for Engine[%u] .\n", __FUNCTION__, __LINE__, enEngineType);
        return E_TRAVELING_INPUT_PARAMETER_ERROR;
    }

    if(pstTravelingCaps->u16TravelingEngineCaps_Version != TRAVELING_CAPS_INFO_MSDK_VERSION)
    {
        //Version compatible process done here
        //Currently, we do not implement it, so return fail
        printf("[DIPC] Error[%s:%d], Structure Version mismatch(%u->%u) for Engine[%u] .\n", __FUNCTION__, __LINE__,
                pstTravelingCaps->u16TravelingEngineCaps_Version, (MS_U16)TRAVELING_CAPS_INFO_MSDK_VERSION, enEngineType);
        return E_TRAVELING_INPUT_PARAMETER_ERROR;
    }

    ST_XC_DIP_CHIPCAPS stDipChipCaps={(SCALER_DIP_WIN)(enEngineType-E_TRAVELING_ENGINE_TYPE_HD0), 0};
    ST_TRAVELING_ENGINE_CAPS stTempCaps;
    memset(&stTempCaps, 0, sizeof(ST_TRAVELING_ENGINE_CAPS));
    if(E_APIXC_RET_OK != MApi_XC_GetChipCaps(E_XC_DIP_CHIP_CAPS, (MS_U32 *)(&stDipChipCaps), sizeof(ST_XC_DIP_CHIPCAPS)))
    {
        printf("[DIPC] Error[%s:%d], MApi_XC_GetChipCaps return fail for Engine[%u] .\n", __FUNCTION__, __LINE__, enEngineType);
        return E_TRAVELING_UNKNOWN_ERROR;
    }

    stTempCaps.u16TravelingEngineCaps_Version = TRAVELING_CAPS_INFO_MSDK_VERSION;
    if(pstTravelingCaps->u16TravelingEngineCaps_Length > sizeof(ST_TRAVELING_ENGINE_CAPS))
    {
        stTempCaps.u16TravelingEngineCaps_Length = sizeof(ST_TRAVELING_ENGINE_CAPS);
    }
    else
    {
        stTempCaps.u16TravelingEngineCaps_Length = pstTravelingCaps->u16TravelingEngineCaps_Length;
    }
    stTempCaps.bEngineExist = (MS_BOOL)(stDipChipCaps.u32DipChipCaps & DIP_CAP_EXIST);
    if(stTempCaps.bEngineExist)
    {
        //Engine exist, assign the property for return
        stTempCaps.bSupportYC420Fmt = (MS_BOOL)(stDipChipCaps.u32DipChipCaps & DIP_CAP_420TILE);
    }
    memcpy(pstTravelingCaps, &stTempCaps, stTempCaps.u16TravelingEngineCaps_Length);
    return E_TRAVELING_SUCCESS;
#else
    return E_TRAVELING_UNSUPPORT;
#endif
}

void mapi_video::SetPCmode(MAPI_BOOL bEnable)
{
    m_bEnablePCmode = bEnable;
}

MAPI_BOOL mapi_video::GetPCmode()
{
    return m_bEnablePCmode ;
}


MAPI_BOOL mapi_video::IsSupportedFeature(mapi_video_datatype::EN_MAPI_VIDEO_SUPPORTED_FEATURES enFeature, void *pParam)
{
    MAPI_BOOL bIsSupported = MAPI_FALSE;
    MS_U32 u32Ret = 0;
    switch (enFeature)
    {
        case mapi_video_datatype::E_MAPI_VIDEO_FEATURE_4K2K_PIP:
            MApi_XC_GetChipCaps(E_XC_SUPPORT_4K2K_WITH_PIP, &u32Ret, 0);
            if(u32Ret == (MS_U32)1)
            {
                 bIsSupported = MAPI_TRUE;
            }
            else if (u32Ret == (MS_U32)0)
            {
                 bIsSupported = MAPI_FALSE;
            }
            else
            {
                 ERR_VIDEO(printf("[Error] MApi_XC_GetChipCaps feature(%d): illegal return!\n", enFeature));
            }
            break;
        case mapi_video_datatype::E_MAPI_VIDEO_FEATURE_MAX:
        default:
            ERR_VIDEO(printf("[Error] IsSupportedFeature Invalide input feature (%d)!\n", enFeature));
            break;
    }

    return bIsSupported;
}


MAPI_BOOL mapi_video::GetAutoDetectHdrLevel(MAPI_SCALER_WIN enWin)
{
#ifdef UFO_XC_HDR
    return m_bAutoDetectHdrLevel[enWin];
#else
    return MAPI_FALSE;
#endif
}

MAPI_BOOL mapi_video::SetAutoDetectHdrLevel(MAPI_BOOL bAuto, MAPI_SCALER_WIN enWin, E_MAPI_XC_HDR_LEVEL enAutoHdrLevel)
{
#ifdef UFO_XC_HDR
    m_bAutoDetectHdrLevel[enWin] = bAuto;
    if (enAutoHdrLevel != E_MAPI_XC_HDR_MAX)
    {
        m_AutoHdrLevel[enWin] = enAutoHdrLevel;
    }

    if (m_bHdrInitialized == MAPI_TRUE)
    {
        return SetHdrMetadata(&m_HdrMetadata, enWin);
    }
    else
    {
        return MAPI_TRUE;
    }
#else
    return MAPI_FALSE;
#endif
}

E_MAPI_XC_HDR_LEVEL mapi_video::GetHdrLevel(MAPI_SCALER_WIN enWin)
{
#ifdef UFO_XC_HDR
    return m_ActiveHdrLevel[enWin];
#else
    return E_MAPI_XC_HDR_MAX;
#endif
}

MAPI_BOOL mapi_video::SetHdrLevel(E_MAPI_XC_HDR_LEVEL enHdrLevel, MAPI_SCALER_WIN enWin)
{
#ifdef UFO_XC_HDR
    m_bAutoDetectHdrLevel[enWin] = MAPI_FALSE;
    m_ActiveHdrLevel[enWin] = enHdrLevel;

    if (m_bHdrInitialized == MAPI_TRUE)
    {
        return SetHdrMetadata(&m_HdrMetadata, enWin);
    }
    else
    {
        return MAPI_TRUE;
    }
#else
    return MAPI_FALSE;
#endif
}

MAPI_BOOL mapi_video::IsSupportHdr()
{
#ifdef UFO_XC_HDR
    return MAPI_TRUE;
#else
    return MAPI_FALSE;
#endif
}

void mapi_video::SetVideoInitState(mapi_video_datatype::EN_MAPI_VIDEO_INIT_TYPE enInitType, MAPI_BOOL bInit)
{
    switch(enInitType)
    {
        case mapi_video_datatype::E_MAPI_VIDEO_INIT_EDID:
            m_bEDIDInit = bInit;
            break;
        case mapi_video_datatype::E_MAPI_VIDEO_INIT_HDMI:
            m_bHDMIInit = bInit;
            break;
        default:
            ERR_VIDEO(printf("[Error] SetVideoInitType Invalide input type (%d)!\n", enInitType));
            break;
    }
}

MAPI_BOOL mapi_video::GetVideoInitState(mapi_video_datatype::EN_MAPI_VIDEO_INIT_TYPE enInitType)
{
    MAPI_BOOL bInit = MAPI_FALSE;
    switch(enInitType)
    {
        case mapi_video_datatype::E_MAPI_VIDEO_INIT_EDID:
            bInit = m_bEDIDInit;
            break;
        case mapi_video_datatype::E_MAPI_VIDEO_INIT_HDMI:
            bInit = m_bHDMIInit;
            break;
        default:
            ERR_VIDEO(printf("[Error] GetVideoInitType Invalide input type (%d)!\n", enInitType));
            break;
    }
    return bInit;
}



