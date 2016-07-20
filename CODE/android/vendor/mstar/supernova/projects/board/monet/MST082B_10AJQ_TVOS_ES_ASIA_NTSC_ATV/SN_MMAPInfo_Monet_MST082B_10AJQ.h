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
//**********************************************************************
//** System-MemMap
//**********************************************************************
//Use SCA to generate the file

#define BOARD_MMAP_ITEMS_NUM    90

static MMInfo_t BOARD_MMInfo={
    MIU_DRAM_LEN,
    MIU_DRAM_LEN0,
    MIU_DRAM_LEN1,
    MIU_INTERVAL,
    BOARD_MMAP_ITEMS_NUM
};

//u32gID, u32Addr, u32Size, u8Layer, u32Align, b_is_miu0
static MMapInfo_t BOARD_MMAPInfo[BOARD_MMAP_ITEMS_NUM]={
        //miu 1
    {E_MMAP_ID_VDEC_CPU,                                E_MMAP_ID_VDEC_CPU_ADR,                         E_MMAP_ID_VDEC_CPU_LEN, 0, 0,
        (~E_MMAP_ID_VDEC_CPU_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_VDEC_FRAMEBUFFER,                  E_MMAP_ID_VDEC_FRAMEBUFFER_ADR,           E_MMAP_ID_VDEC_FRAMEBUFFER_LEN, 0, 0,
            (~E_MMAP_ID_VDEC_FRAMEBUFFER_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_VDEC_FRAMEBUFFER_SD,            E_MMAP_ID_VDEC_FRAMEBUFFER_SD_ADR,     E_MMAP_ID_VDEC_FRAMEBUFFER_SD_LEN, 0, 0,
            (~E_MMAP_ID_VDEC_FRAMEBUFFER_SD_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_DOWNLOAD_BUFFER,                   E_MMAP_ID_DOWNLOAD_BUFFER_ADR,           E_MMAP_ID_DOWNLOAD_BUFFER_LEN, 0, 0,
            (~E_MMAP_ID_DOWNLOAD_BUFFER_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_VDEC_BITSTREAM,                      E_MMAP_ID_VDEC_BITSTREAM_ADR,              E_MMAP_ID_VDEC_BITSTREAM_LEN, 0, 0,
            (~E_MMAP_ID_VDEC_BITSTREAM_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_VDEC_BITSTREAM_SD,                E_MMAP_ID_VDEC_BITSTREAM_SD_ADR,        E_MMAP_ID_VDEC_BITSTREAM_SD_LEN, 0, 0,
            (~E_MMAP_ID_VDEC_BITSTREAM_SD_MEMORY_TYPE & MIU1)},

    {E_MST_GEVQ,                                                E_MST_GEVQ_ADR,                                        E_MST_GEVQ_LEN, 0, 0,
            (~E_MST_GEVQ_MEMORY_TYPE & MIU1)},

    {E_MST_GOP_REGDMA,                                    E_MST_GOP_REGDMA_ADR,                           E_MST_GOP_REGDMA_LEN, 0, 0,
            (~E_MST_GOP_REGDMA_MEMORY_TYPE & MIU1)},

    {E_DFB_FRAMEBUFFER,                                    E_DFB_FRAMEBUFFER_ADR,                             E_DFB_FRAMEBUFFER_LEN, 0, 0,
            (~E_DFB_FRAMEBUFFER_MEMORY_TYPE & MIU1)},

    {E_LX_MEM2,                                                  E_LX_MEM2_ADR,                                            E_LX_MEM2_LEN, 0, 0,
            (~E_LX_MEM2_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_XC_MLOAD,                                E_MMAP_ID_XC_MLOAD_ADR,                         E_MMAP_ID_XC_MLOAD_LEN, 0, 0,
            (~E_MMAP_ID_XC_MLOAD_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_DUMMY3,                                    E_MMAP_ID_DUMMY3_ADR,                             E_MMAP_ID_DUMMY3_LEN, 0, 0,
            (~E_MMAP_ID_DUMMY3_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_FLUSH_BUFFER,                      E_MMAP_ID_FLUSH_BUFFER_ADR,                        E_MMAP_ID_FLUSH_BUFFER_LEN, 0, 0,
            (~E_MMAP_ID_FLUSH_BUFFER_MEMORY_TYPE & MIU1)},


		//miu0
    {E_LX_MEM,                                                E_LX_MEM_ADR,                                   E_LX_MEM_LEN, 0, 0,
            (~E_LX_MEM_MEMORY_TYPE & MIU1)},

    {E_EMAC_MEM,                                           E_EMAC_MEM_ADR,                                 E_EMAC_MEM_LEN, 0, 0,
            (~E_EMAC_MEM_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_MAD_R2,                                E_MMAP_ID_MAD_R2_ADR,                           E_MMAP_ID_MAD_R2_LEN, 0, 0,
            (~E_MMAP_ID_MAD_R2_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_MAD_SE,                                E_MMAP_ID_MAD_SE_ADR,                           E_MMAP_ID_MAD_SE_LEN, 0, 0,
            (~E_MMAP_ID_MAD_SE_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_MAD_DEC,                             E_MMAP_ID_MAD_DEC_ADR,                          E_MMAP_ID_MAD_DEC_LEN, 0, 0,
            (~E_MMAP_ID_MAD_DEC_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_VE,                                        E_MMAP_ID_VE_ADR,                               E_MMAP_ID_VE_LEN, 0, 0,
            (~E_MMAP_ID_VE_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_DMX_SECBUF,                        E_MMAP_ID_DMX_SECBUF_ADR,                       E_MMAP_ID_DMX_SECBUF_LEN, 0, 0,
            (~E_MMAP_ID_DMX_SECBUF_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_DUMMY1,                                E_MMAP_ID_DUMMY1_ADR,                           E_MMAP_ID_DUMMY1_LEN, 0, 0,
            (~E_MMAP_ID_DUMMY1_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_PVR_DOWNLOAD,                   E_MMAP_ID_PVR_DOWNLOAD_ADR,                     E_MMAP_ID_PVR_DOWNLOAD_LEN, 0, 0,
            (~E_MMAP_ID_PVR_DOWNLOAD_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_PVR_UPLOAD,                         E_MMAP_ID_PVR_UPLOAD_ADR,                       E_MMAP_ID_PVR_UPLOAD_LEN, 0, 0,
            (~E_MMAP_ID_PVR_UPLOAD_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_PVR_BROWSER_BUFFER,          E_MMAP_ID_PVR_BROWSER_BUFFER_ADR,               E_MMAP_ID_PVR_BROWSER_BUFFER_LEN, 0, 0,
            (~E_MMAP_ID_PVR_BROWSER_BUFFER_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_PVR_BUFFER,                          E_MMAP_ID_PVR_BUFFER_ADR,                       E_MMAP_ID_PVR_BUFFER_LEN, 0, 0,
            (~E_MMAP_ID_PVR_BUFFER_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_TTX,                                       E_MMAP_ID_TTX_ADR,                              E_MMAP_ID_TTX_LEN, 0, 0,
            (~E_MMAP_ID_TTX_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_TTX_VE,                                 E_MMAP_ID_TTX_VE_ADR,                           E_MMAP_ID_TTX_VE_LEN, 0, 0,
            (~E_MMAP_ID_TTX_VE_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_MHEG5_BUFFER,                     E_MMAP_ID_MHEG5_BUFFER_ADR,                     E_MMAP_ID_MHEG5_BUFFER_LEN, 0, 0,
            (~E_MMAP_ID_MHEG5_BUFFER_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_MHEG5_CI_PLUS_BUFFER,       E_MMAP_ID_MHEG5_CI_PLUS_BUFFER_ADR,             E_MMAP_ID_MHEG5_CI_PLUS_BUFFER_LEN, 0, 0,
            (~E_MMAP_ID_MHEG5_CI_PLUS_BUFFER_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_VDPLAYER,                            E_MMAP_ID_VDPLAYER_ADR,                         E_MMAP_ID_VDPLAYER_LEN, 0, 0,
            (~E_MMAP_ID_VDPLAYER_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_MM_DIVX_FONT_TBL,              E_MMAP_ID_MM_DIVX_FONT_TBL_ADR,                 E_MMAP_ID_MM_DIVX_FONT_TBL_LEN, 0, 0,
            (~E_MMAP_ID_MM_DIVX_FONT_TBL_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_VDPLAYER_DATA,                    E_MMAP_ID_VDPLAYER_DATA_ADR,                    E_MMAP_ID_VDPLAYER_DATA_LEN, 0, 0,
            (~E_MMAP_ID_VDPLAYER_DATA_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_VDPLAYER_SUBTITLE_BITSTREAM_BUFF, E_MMAP_ID_VDPLAYER_SUBTITLE_BITSTREAM_BUFF_ADR, E_MMAP_ID_VDPLAYER_SUBTITLE_BITSTREAM_BUFF_LEN, 0, 0,
            (~E_MMAP_ID_VDPLAYER_SUBTITLE_BITSTREAM_BUFF_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_VDPLAYER_BITSTREAM_EXT,    E_MMAP_ID_VDPLAYER_BITSTREAM_EXT_ADR,           E_MMAP_ID_VDPLAYER_BITSTREAM_EXT_LEN, 0, 0,
            (~E_MMAP_ID_VDPLAYER_BITSTREAM_EXT_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_VDPLAYER_BITSTREAM,          E_MMAP_ID_VDPLAYER_BITSTREAM_ADR,               E_MMAP_ID_VDPLAYER_BITSTREAM_LEN, 0, 0,
            (~E_MMAP_ID_VDPLAYER_BITSTREAM_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_MM_SUBTITLE_BUFFER,          E_MMAP_ID_MM_SUBTITLE_BUFFER_ADR,               E_MMAP_ID_MM_SUBTITLE_BUFFER_LEN, 0, 0,
            (~E_MMAP_ID_MM_SUBTITLE_BUFFER_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_PHOTO,                                 E_MMAP_ID_PHOTO_ADR,                            E_MMAP_ID_PHOTO_LEN, 0, 0,
            (~E_MMAP_ID_PHOTO_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_PHOTO_DISPLAY,                   E_MMAP_ID_PHOTO_DISPLAY_ADR,                    E_MMAP_ID_PHOTO_DISPLAY_LEN, 0, 0,
            (~E_MMAP_ID_PHOTO_DISPLAY_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_PHOTO_SHAREMEM,               E_MMAP_ID_PHOTO_SHAREMEM_ADR,                   E_MMAP_ID_PHOTO_SHAREMEM_LEN, 0, 0,
            (~E_MMAP_ID_PHOTO_SHAREMEM_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_JPD_READ,                            E_MMAP_ID_JPD_READ_ADR,                         E_MMAP_ID_JPD_READ_LEN, 0, 0,
            (~E_MMAP_ID_JPD_READ_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_JPD_WRITE,                          E_MMAP_ID_JPD_WRITE_ADR,                        E_MMAP_ID_JPD_WRITE_LEN, 0, 0,
            (~E_MMAP_ID_JPD_WRITE_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_PHOTO_INTER,                      E_MMAP_ID_PHOTO_INTER_ADR,                      E_MMAP_ID_PHOTO_INTER_LEN, 0, 0,
            (~E_MMAP_ID_PHOTO_INTER_MEMORY_TYPE & MIU1)},
 
    {E_MMAP_ID_PHOTO_MPO_INTER,                   E_MMAP_ID_PHOTO_MPO_INTER_ADR,                    E_MMAP_ID_PHOTO_MPO_INTER_LEN, 0, 0,
            (~E_MMAP_ID_PHOTO_MPO_INTER_MEMORY_TYPE & MIU1)},
            
    {E_MMAP_ID_COMB_3D_BUF,                     E_MMAP_ID_COMB_3D_BUF_ADR,                      E_MMAP_ID_COMB_3D_BUF_LEN, 0, 0,
            (~E_MMAP_ID_COMB_3D_BUF_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_DMX_VQUEUE,                       E_MMAP_ID_DMX_VQUEUE_ADR,                       E_MMAP_ID_DMX_VQUEUE_LEN, 0, 0,
            (~E_MMAP_ID_DMX_VQUEUE_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_DUMMY2,                              E_MMAP_ID_DUMMY2_ADR,                           E_MMAP_ID_DUMMY2_LEN, 0, 0,
            (~E_MMAP_ID_DUMMY2_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_DUMP_BUFFER,                      E_MMAP_ID_DUMP_BUFFER_ADR,                      E_MMAP_ID_DUMP_BUFFER_LEN, 0, 0,
            (~E_MMAP_ID_DUMP_BUFFER_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_XC_MAIN_FB,                        E_MMAP_ID_XC_MAIN_FB_ADR,                       E_MMAP_ID_XC_MAIN_FB_LEN, 0, 0,
            (~E_MMAP_ID_XC_MAIN_FB_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_SCALER_DNR_SUB_BUF,        E_MMAP_ID_SCALER_DNR_SUB_BUF_ADR,               E_MMAP_ID_SCALER_DNR_SUB_BUF_LEN, 0, 0,
            (~E_MMAP_ID_SCALER_DNR_SUB_BUF_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_MHEG5_JPEG_BUFFER,            E_MMAP_ID_MHEG5_JPEG_BUFFER_ADR,                E_MMAP_ID_MHEG5_JPEG_BUFFER_LEN, 0, 0,
            (~E_MMAP_ID_MHEG5_JPEG_BUFFER_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_EVENTDB_SDRAM,                 E_MMAP_ID_EVENTDB_SDRAM_ADR,                    E_MMAP_ID_EVENTDB_SDRAM_LEN, 0, 0,
            (~E_MMAP_ID_EVENTDB_SDRAM_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_EPGEXTDB_SDRAM,               E_MMAP_ID_EPGEXTDB_SDRAM_ADR,                   E_MMAP_ID_EPGEXTDB_SDRAM_LEN, 0, 0,
            (~E_MMAP_ID_EPGEXTDB_SDRAM_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_PM51_VAR_MEM,                    E_MMAP_ID_PM51_VAR_MEM_ADR,                     E_MMAP_ID_PM51_VAR_MEM_LEN, 0, 0,
            (~E_MMAP_ID_PM51_VAR_MEM_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_PVR_THUMBNAIL,                   E_MMAP_ID_PVR_THUMBNAIL_ADR,                    E_MMAP_ID_PVR_THUMBNAIL_LEN, 0, 0,
            (~E_MMAP_ID_PVR_THUMBNAIL_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_GOP_REGDMA,                       E_MMAP_ID_GOP_REGDMA_ADR,                       E_MMAP_ID_GOP_REGDMA_LEN, 0, 0,
            (~E_MMAP_ID_GOP_REGDMA_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_DUMMY20,                              E_MMAP_ID_DUMMY20_ADR,                          E_MMAP_ID_DUMMY20_LEN, 0, 0,
            (~E_MMAP_ID_DUMMY20_MEMORY_TYPE & MIU1)},

    {E_DFB_JPD_READ,                                     E_DFB_JPD_READ_ADR,                             E_DFB_JPD_READ_LEN, 0, 0,
            (~E_DFB_JPD_READ_MEMORY_TYPE & MIU1)},

    {E_DFB_JPD_INTERNAL,                             E_DFB_JPD_INTERNAL_ADR,                         E_DFB_JPD_INTERNAL_LEN, 0, 0,
            (~E_DFB_JPD_INTERNAL_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_DUMMY16,                              E_MMAP_ID_DUMMY16_ADR,                          E_MMAP_ID_DUMMY16_LEN, 0, 0,
            (~E_MMAP_ID_DUMMY16_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_DUMMY18,                              E_MMAP_ID_DUMMY18_ADR,                          E_MMAP_ID_DUMMY18_LEN, 0, 0,
            (~E_MMAP_ID_DUMMY18_MEMORY_TYPE & MIU1)},

    {E_DFB_JPD_WRITE,                                E_DFB_JPD_WRITE_ADR,                            E_DFB_JPD_WRITE_LEN, 0, 0,
            (~E_DFB_JPD_WRITE_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_DUMMY21,                              E_MMAP_ID_DUMMY21_ADR,                          E_MMAP_ID_DUMMY21_LEN, 0, 0,
            (~E_MMAP_ID_DUMMY21_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_DUMMY5,                               E_MMAP_ID_DUMMY5_ADR,                           E_MMAP_ID_DUMMY5_LEN, 0, 0,
            (~E_MMAP_ID_DUMMY5_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_DUMMY6,                               E_MMAP_ID_DUMMY6_ADR,                           E_MMAP_ID_DUMMY6_LEN, 0, 0,
            (~E_MMAP_ID_DUMMY6_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_DUMMY7,                               E_MMAP_ID_DUMMY7_ADR,                           E_MMAP_ID_DUMMY7_LEN, 0, 0,
            (~E_MMAP_ID_DUMMY7_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_DUMMY8,                               E_MMAP_ID_DUMMY8_ADR,                           E_MMAP_ID_DUMMY8_LEN, 0, 0,
            (~E_MMAP_ID_DUMMY8_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_INVALID,                                E_MMAP_ID_INVALID_ADR,                          E_MMAP_ID_INVALID_LEN, 0, 0,
            (~E_MMAP_ID_INVALID_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_DUMMY17,                               E_MMAP_ID_DUMMY17_ADR,                          E_MMAP_ID_DUMMY17_LEN, 0, 0,
            (~E_MMAP_ID_DUMMY17_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_DUMMY4,                               E_MMAP_ID_DUMMY4_ADR,                           E_MMAP_ID_DUMMY4_LEN, 0, 0,
            (~E_MMAP_ID_DUMMY4_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_DUMP_BUFFER2,                     E_MMAP_ID_DUMP_BUFFER2_ADR,                     E_MMAP_ID_DUMP_BUFFER2_LEN, 0, 0,
            (~E_MMAP_ID_DUMP_BUFFER2_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_MFE,                                      E_MMAP_ID_MFE_ADR,                              E_MMAP_ID_MFE_LEN, 0, 0,
            (~E_MMAP_ID_MFE_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_DIP,                                      E_MMAP_ID_DIP_ADR,                              E_MMAP_ID_DIP_LEN, 0, 0,
            (~E_MMAP_ID_DIP_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_3D_BUF0,                               E_MMAP_ID_3D_BUF0_ADR,                          E_MMAP_ID_3D_BUF0_LEN, 0, 0,
            (~E_MMAP_ID_3D_BUF0_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_3D_BUF1,                               E_MMAP_ID_3D_BUF1_ADR,                          E_MMAP_ID_3D_BUF1_LEN, 0, 0,
            (~E_MMAP_ID_3D_BUF1_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_3D_COLOR,                            E_MMAP_ID_3D_COLOR_ADR,                         E_MMAP_ID_3D_COLOR_LEN, 0, 0,
            (~E_MMAP_ID_3D_COLOR_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_3D_CMD,                              E_MMAP_ID_3D_CMD_ADR,                           E_MMAP_ID_3D_CMD_LEN, 0, 0,
            (~E_MMAP_ID_3D_CMD_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_3D_VTX,                                E_MMAP_ID_3D_VTX_ADR,                           E_MMAP_ID_3D_VTX_LEN, 0, 0,
            (~E_MMAP_ID_3D_VTX_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_3D_DWIN,                             E_MMAP_ID_3D_DWIN_ADR,                          E_MMAP_ID_3D_DWIN_LEN, 0, 0,
            (~E_MMAP_ID_3D_DWIN_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_3D_MPOOL,                            E_MMAP_ID_3D_MPOOL_ADR,                         E_MMAP_ID_3D_MPOOL_LEN, 0, 0,
            (~E_MMAP_ID_3D_MPOOL_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_3D_MPOOL_M1,                      E_MMAP_ID_3D_MPOOL_M1_ADR,                      E_MMAP_ID_3D_MPOOL_M1_LEN, 0, 0,
            (~E_MMAP_ID_3D_MPOOL_M1_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_XC_SUB_FB,                      E_MMAP_ID_XC_SUB_FB_ADR,                      E_MMAP_ID_XC_SUB_FB_LEN, 0, 0,
            (~E_MMAP_ID_XC_SUB_FB_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_VDEC_SUB_FRAMEBUFFER,                      E_MMAP_ID_VDEC_SUB_FRAMEBUFFER_ADR,                      E_MMAP_ID_VDEC_SUB_FRAMEBUFFER_LEN, 0, 0,
            (~E_MMAP_ID_VDEC_SUB_FRAMEBUFFER_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_VDEC_SUB_BITSTREAM,                      E_MMAP_ID_VDEC_SUB_BITSTREAM_ADR,                      E_MMAP_ID_VDEC_SUB_BITSTREAM_LEN, 0, 0,
            (~E_MMAP_ID_VDEC_SUB_BITSTREAM_MEMORY_TYPE & MIU1)},
    
    {E_MMAP_ID_VDEC_MVC_FRAMEBUFFER,            E_MMAP_ID_VDEC_MVC_FRAMEBUFFER_ADR,     E_MMAP_ID_VDEC_MVC_FRAMEBUFFER_LEN, 0, 0,
            (~E_MMAP_ID_VDEC_MVC_FRAMEBUFFER_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_VDEC_MVC_BITSTREAM,                      E_MMAP_ID_VDEC_MVC_BITSTREAM_ADR,              E_MMAP_ID_VDEC_MVC_BITSTREAM_LEN, 0, 0,
            (~E_MMAP_ID_VDEC_MVC_BITSTREAM_MEMORY_TYPE & MIU1)},
    
    {E_MMAP_ID_XC_2DTO3D_DD_BUF,                 E_MMAP_ID_XC_2DTO3D_DD_BUF_ADR,
       E_MMAP_ID_XC_2DTO3D_DD_BUF_LEN, 0, 0,
            (~E_MMAP_ID_XC_2DTO3D_DD_BUF_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_XC_2DTO3D_DR_BUF,                 E_MMAP_ID_XC_2DTO3D_DR_BUF_ADR,
       E_MMAP_ID_XC_2DTO3D_DR_BUF_LEN, 0, 0,
            (~E_MMAP_ID_XC_2DTO3D_DR_BUF_MEMORY_TYPE & MIU1)},
            
    {E_MMAP_ID_HW_AES_BUF,                       E_MMAP_ID_HW_AES_BUF_ADR,                         
       E_MMAP_ID_HW_AES_BUF_LEN, 0, 0,
            (~E_MMAP_ID_HW_AES_BUF_MEMORY_TYPE & MIU1)},

    {E_MMAP_ID_LOCAL_DIMMING,                 E_MMAP_ID_LOCAL_DIMMING_ADR,
       E_MMAP_ID_LOCAL_DIMMING_LEN, 0, 0,
            (~E_MMAP_ID_LOCAL_DIMMING_MEMORY_TYPE & MIU1)},
    
    {E_MMAP_ID_MAD_COMMON,                       E_MMAP_ID_MAD_COMMON_ADR,                         
       E_MMAP_ID_MAD_COMMON_LEN, 0, 0,
            (~E_MMAP_ID_MAD_COMMON_MEMORY_TYPE & MIU1)},
};
