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
#include "MSrv_Advert_Player.h"

// headers of standard C libs
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <string.h>

#include "MTypes.h"

#if(ATSC_SYSTEM_ENABLE==0)
#include "MSrv_System_Database_DVB.h"
#else
#include "MSrv_System_Database_ATSC.h"
#endif
#include "MufEvent.h"
#include "SystemInfo.h"
#include "MSrv_Control.h"
#include "MSrv_3DManager.h"
U32   MSrv_Advert_Player::u32Item = 0;
BOOL MSrv_Advert_Player::mm_init_flag = FALSE;
BOOL MSrv_Advert_Player::mm_init_fail = FALSE;
BOOL MSrv_Advert_Player::mm_decode_flag= FALSE;
BOOL MSrv_Advert_Player::mm_decode_fail = FALSE;
BOOL MSrv_Advert_Player::bAdvert_player = FALSE;
BOOL MSrv_Advert_Player::m_bRestore3DdetectFlag = FALSE;
ST_MEDIAFILE_INFO * MSrv_Advert_Player::m_pMediaFile1st = NULL;
ST_MEDIAFILE_INFO * MSrv_Advert_Player::m_pMediaFile2nd = NULL;
ST_MEDIAFILE_INFO * MSrv_Advert_Player::m_pMediaFile3rd = NULL;
ST_MEDIAFILE_INFO * MSrv_Advert_Player::m_pMediaFile4th = NULL;


MSrv_MM_PlayerInterface * MSrv_Advert_Player::MM_interface = NULL;



MSrv_Advert_Player* MSrv_Advert_Player::m_instance = NULL;


MSrv_Advert_Player::MSrv_Advert_Player()
{
    MM_interface = NULL;
    m_pMediaFile1st = NULL;
    m_pMediaFile2nd = NULL;
    m_pMediaFile3rd = NULL;
    m_pMediaFile4th = NULL;
    memset(&stMedia, 0, sizeof(ST_MEDIA_INFO));
    memset(&m_stMediaFileInfo, 0, sizeof(ST_MEDIAFILE_INFO)*E_MEDIAFILE_ID_NUM);
    mm_init_flag = FALSE;
    mm_init_fail = FALSE;
    mm_decode_flag = FALSE;
    mm_decode_fail = FALSE;
    m_instance = this;
}

MSrv_Advert_Player::~MSrv_Advert_Player()
{
     m_instance = NULL;
}

MSrv_Advert_Player* MSrv_Advert_Player::GetInstance()
{
    if(m_instance == NULL)
    {
        m_instance = new(std::nothrow) MSrv_Advert_Player;
        ASSERT(m_instance);
    }

    return m_instance;
}

void MSrv_Advert_Player::DestroyInstance(void)
{
    if(m_instance != NULL)
    {
        delete m_instance;
        m_instance = NULL;
    }
}

BOOL MSrv_Advert_Player::EventHandler(void *arg1, void *arg2, void *arg3)
{
    ST_FILE_ACK_INFO stFileAck;

    const EN_NOTIFY_EVENT e32Event = (EN_NOTIFY_EVENT)(U32)arg2;

    const ST_MMPLAYER_EVENT_INFO* stFileMsgInfo = (const ST_MMPLAYER_EVENT_INFO *)arg3;

    ST_MEDIAFILE_INFO * pMediaFileInfo = NULL;

    if ((e32Event == E_EVENT_FILE_READ) || (e32Event == E_EVENT_FILE_SEEK) || (e32Event == E_EVENT_FILE_TELL))
    {
        pMediaFileInfo = (ST_MEDIAFILE_INFO *)stFileMsgInfo->u32FileID;
    }

    switch(e32Event)
    {
        case E_EVENT_FILE_READ:
        {
            memset(&stFileAck, 0, sizeof(ST_FILE_ACK_INFO));

            if(((pMediaFileInfo == m_pMediaFile1st) ||
                 (pMediaFileInfo == m_pMediaFile2nd) ||
                 (pMediaFileInfo == m_pMediaFile3rd) ||
                 (pMediaFileInfo == m_pMediaFile4th)) &&
                 (pMediaFileInfo != NULL))
            {
                if(((pMediaFileInfo->fp_mminterface) == NULL) || (stFileMsgInfo->u32MediaItem != u32Item))
                {
                    MM_interface->MM_SetFileProcessInfo(u32Item, E_FILE_READ_INVALID_ACK, &stFileAck);
                }
                else
                {
                    stFileAck.u32ReadSize = 0;
                    stFileAck.u32ReadSize = fread((void*)stFileMsgInfo->pu8FileBuff, 1,
                                                  stFileMsgInfo->u32FileRequestSize, (pMediaFileInfo->fp_mminterface));
                    pMediaFileInfo->u64FilePos += stFileAck.u32ReadSize;
                    //stFileAck.bIsFileEnd = ((U64)u64FilePos >= u64FileLength);

                    if (((U64)(pMediaFileInfo->u64FilePos-pMediaFileInfo->u64StartPos)) >= pMediaFileInfo->u64FileLength)
                    {
                        /////////stFileAck.bIsFileEnd = ((U64)u64FilePos >= u64FileLength);
                        //printf("INIpos:0x%llx, pos:0x%llx(0x%llx)->diff:0x%llx, size:0x%x, end:0x%x",u64StartPos,u64FilePos,u64FileLength,(U64)(u64FilePos-u64StartPos),stFileAck.u32ReadSize,stFileAck.bIsFileEnd);
                        stFileAck.bIsFileEnd = (((U64)(pMediaFileInfo->u64FilePos - pMediaFileInfo->u64StartPos)) >= pMediaFileInfo->u64FileLength);
                        pMediaFileInfo->u64StartPos = pMediaFileInfo->u64FilePos;
                    }

                    MM_interface->MM_SetFileProcessInfo(u32Item, E_FILE_READ_ACK, &stFileAck);
                }
            }
            else
            {
                MM_interface->MM_SetFileProcessInfo(u32Item, E_FILE_READ_INVALID_ACK, &stFileAck);
            }
        }
            break;
        case E_EVENT_FILE_SEEK:
        {
            memset(&stFileAck, 0, sizeof(ST_FILE_ACK_INFO));

            if(((pMediaFileInfo == m_pMediaFile1st) ||
                 (pMediaFileInfo == m_pMediaFile2nd) ||
                 (pMediaFileInfo == m_pMediaFile3rd) ||
                 (pMediaFileInfo == m_pMediaFile4th)) &&
                 (pMediaFileInfo != NULL))
            {
                if(((pMediaFileInfo->fp_mminterface) == NULL) || (stFileMsgInfo->u32MediaItem != u32Item))
                {
                    MM_interface->MM_SetFileProcessInfo(u32Item, E_FILE_SEEK_INVALID_ACK, &stFileAck);
                }
                else
                {
                    if(!fseeko64(pMediaFileInfo->fp_mminterface, stFileMsgInfo->u64FileSeekPos, SEEK_SET))
                    {
                        MM_interface->MM_SetFileProcessInfo(u32Item, E_FILE_SEEK_ACK, &stFileAck);
                    }
                    else
                    {
                        MM_interface->MM_SetFileProcessInfo(u32Item, E_FILE_SEEK_INVALID_ACK, &stFileAck);
                    }
                }
            }
            else
            {
                MM_interface->MM_SetFileProcessInfo(u32Item, E_FILE_SEEK_INVALID_ACK, &stFileAck);
            }
        }
            break;
        case E_EVENT_FILE_TELL:
        {
            memset(&stFileAck, 0, sizeof(ST_FILE_ACK_INFO));

            if(((pMediaFileInfo == m_pMediaFile1st) ||
                 (pMediaFileInfo == m_pMediaFile2nd) ||
                 (pMediaFileInfo == m_pMediaFile3rd) ||
                 (pMediaFileInfo == m_pMediaFile4th)) &&
                 (pMediaFileInfo != NULL))
            {
                        if(((pMediaFileInfo->fp_mminterface) == NULL) || (stFileMsgInfo->u32MediaItem != u32Item))
                        {
                            MM_interface->MM_SetFileProcessInfo(u32Item, E_FILE_TELL_INVALID_ACK, &stFileAck);
                        }
                        else
                        {
                            stFileAck.u64FilePosition = ftello64(pMediaFileInfo->fp_mminterface);
                            MM_interface->MM_SetFileProcessInfo(u32Item, E_FILE_TELL_ACK, &stFileAck);
                        }

            }
            else
            {
                stFileAck.u64FilePosition = ftello64(pMediaFileInfo->fp_mminterface);
                MM_interface->MM_SetFileProcessInfo(u32Item, E_FILE_TELL_ACK, &stFileAck);
            }

        }
            break;
        case E_EVENT_INIT_OK:
        {
            mm_init_flag = TRUE;
        }
            break;
        case E_EVENT_INIT_FAIL:
        {
            mm_init_fail = TRUE;
        }
            break;
        case E_EVENT_DECODE_ERROR:
        {
            mm_decode_fail = TRUE;
        }
            break;
        case E_EVENT_DECODE_DONE:
        {
            mm_decode_flag = TRUE;
        }
            break;
        case E_EVENT_DECODE_FIRST_FRAME_DONE:
        {
            printf("\n---------------------- PERFORMANCE_FIRST_FRAME_DONE ----------------------\n");
        }
            break;
        default:
            return FALSE;
    }
    return TRUE;
}




BOOL MSrv_Advert_Player::AdvPlayerInit(void)
{
    MS_USER_SYSTEM_SETTING stGetSystemSetting;
    BOOL bRet = FALSE;

    // Video.TS
    char *u8FileName = NULL;
    u8FileName = SystemInfo::GetInstance()->GetVideoFileName();

    MSrv_Control::GetMSrvSystemDatabase()->GetUserSystemSetting(&stGetSystemSetting);

    if(!OpenFile(u8FileName))
    {
        printf(" %s : %d Open video file failed \n", __FILE__, __LINE__);
        return FALSE;
    }
    else
    {
        CloseFile();
        MSrv_Control::GetInstance()->SetInputSource(MAPI_INPUT_SOURCE_STORAGE, FALSE);
    }

    if(MM_interface == NULL)
    {
        MM_interface = new (std::nothrow) MSrv_MM_PlayerInterface;
        ASSERT(MM_interface);
    }

   if(MM_interface->MM_Initialize() == E_PLAYER_OK)
   {
        bAdvert_player = TRUE;
        bRet = TRUE;
   }
   else
   {
        bRet = FALSE;
   }

    return bRet;
}

BOOL MSrv_Advert_Player::Open(const char* const pString, ST_MEDIAFILE_INFO * pMediaFileInfo)
{
    if (pMediaFileInfo !=NULL)
    {
        if(pMediaFileInfo->fp_mminterface != NULL)
        {
            return FALSE;
        }

        pMediaFileInfo->fp_mminterface = fopen64(pString, "rb");
        if(pMediaFileInfo->fp_mminterface == NULL)
        {
            return FALSE;
        }
        pMediaFileInfo->u64FilePos = 0;
        pMediaFileInfo->u64StartPos = pMediaFileInfo->u64FilePos;

        fseeko64(pMediaFileInfo->fp_mminterface, 0, SEEK_END);
        pMediaFileInfo->u64FileLength = ftello64(pMediaFileInfo->fp_mminterface);

        fseeko64(pMediaFileInfo->fp_mminterface, 0, SEEK_SET);

        return TRUE;
    }

    return FALSE;
}


BOOL MSrv_Advert_Player::OpenFile(const char* const pString)
{
    U32 i = 0;
    BOOL bRet = FALSE;

    for (i=0; i<E_MEDIAFILE_ID_NUM; i++)
    {
        bRet = Open(pString, &m_stMediaFileInfo[i]);

        if ( bRet == FALSE)
            goto FUNC_END;
    }

FUNC_END:

    if ( bRet == FALSE)
    {
        for (i=0; i<E_MEDIAFILE_ID_NUM; i++)
        {
            if (m_stMediaFileInfo[i].fp_mminterface !=NULL)
            {
                fclose(m_stMediaFileInfo[i].fp_mminterface);
                m_stMediaFileInfo[i].fp_mminterface = NULL;
                m_stMediaFileInfo[i].u64FileLength = 0;
                m_stMediaFileInfo[i].u64FilePos = 0;
                m_stMediaFileInfo[i].u64StartPos = 0;
            }
        }
    }

    return bRet;
}

BOOL MSrv_Advert_Player::Close(ST_MEDIAFILE_INFO * pMediaFileInfo)
{
    if(pMediaFileInfo->fp_mminterface != NULL)
    {
        fclose(pMediaFileInfo->fp_mminterface);
        pMediaFileInfo->fp_mminterface = NULL;
        pMediaFileInfo->u64FileLength = 0;
        pMediaFileInfo->u64FilePos = 0;
        pMediaFileInfo->u64StartPos = 0;
    }
    return TRUE;
}

BOOL MSrv_Advert_Player::CloseFile(void)
{
    U32 i = 0;

    for (i=0; i<E_MEDIAFILE_ID_NUM; i++)
    {
        Close(&m_stMediaFileInfo[i]);
    }

    return TRUE;
}

BOOL MSrv_Advert_Player::GetAdvPlayerStatus(void)
{
    return bAdvert_player;
}

BOOL MSrv_Advert_Player::AdvPlayerPlay(void)
{
    BOOL bRet = FALSE;
    clock_t start_t = 0, end_t = 0;

    // Video.TS
    char *u8FileName = NULL;
    u8FileName = SystemInfo::GetInstance()->GetVideoFileName();

    if(MM_interface->AddEventRecipient(&MSrv_Advert_Player::EventHandler))
    {
        bRet=TRUE;
    }
    else
    {
        MM_interface->MM_Finalize();
        return bRet;
    }

    if(OpenFile(u8FileName))
    {
        bRet=TRUE;
    }
    else
    {
        printf("Open file fail\n");
        MM_interface->RemoveEventRecipient(&MSrv_Advert_Player::EventHandler);
        MM_interface->MM_Finalize();

        return bRet;
    }
    /*if autodetect flag on, force disable 3D auto-detect for Ad player*/
    if (TRUE == MSrv_Control_common::GetMSrv3DManager()->Get3DFormatDetectFlag())
    {
        MSrv_Control_common::GetMSrv3DManager()->Set3DFormatDetectFlag(FALSE);
        m_bRestore3DdetectFlag = TRUE;
    }
    // Initialization
    mm_init_flag = FALSE;
    mm_init_fail = FALSE;
    mm_decode_flag = FALSE;
    m_pMediaFile1st = (&m_stMediaFileInfo[E_MEDIAFILE_ID_FIRST]);
    m_pMediaFile2nd = (&m_stMediaFileInfo[E_MEDIAFILE_ID_SECOND]);
    m_pMediaFile3rd = (&m_stMediaFileInfo[E_MEDIAFILE_ID_THIRD]);
    m_pMediaFile4th = (&m_stMediaFileInfo[E_MEDIAFILE_ID_FOURTH]);
    memset(&stMedia, 0, sizeof(ST_MEDIA_INFO));
    stMedia.u32EncryptingType = 0;

    stMedia.u64Version      = MM_IF_VERSION;
    stMedia.u32StructureSize = (U32)sizeof(ST_MEDIA_INFO);
    stMedia.u32FileID = (U32)m_pMediaFile1st;
    stMedia.u32FileSecondID = (U32)m_pMediaFile2nd;
    stMedia.u32FileThirdID = (U32)m_pMediaFile3rd;
    stMedia.u32FileFourthID = (U32)m_pMediaFile4th;
    stMedia.u64FileLength = m_stMediaFileInfo[E_MEDIAFILE_ID_FIRST].u64FileLength;
    stMedia.eMediaType = E_PLAYER_MEDIA_TYPE_VIDEO;

    #if (ADVERT_BOOTING_ENABLE == 1)
    stMedia.eAppType = E_MM_PLAYER_AP_ADVERT;
    #endif


    stMedia.eMediaFileType = E_MEDIA_FILE_TYPE_MASS_STORAGE;
    stMedia.u32CtlFlag = 0;
    stMedia.u32CtlFlag |= E_PLAYER_CTL_FLAG_ENABLE_AUDIO_HANDLER;

    printf("\n---------------------- AD_CREATE_MEDIA_ITEM ----------------------\n");
    if(MM_interface->MM_CreateMediaItem(stMedia, &u32Item) == E_PLAYER_OK)
    {
        printf("\nMM_CreateMediaItem done\n");
        bRet=TRUE;
    }
    else
    {
        printf("\nMM_CreateMediaItem fail\n");
        return bRet;
    }

    start_t = clock();
    do
    {
        usleep(10);
        end_t = clock();
    }while((!mm_init_flag) && (!mm_init_fail) &&
                (((end_t - start_t)/CLOCKS_PER_SEC) <= 10));

    if(!mm_init_flag)
    {
        bRet = FALSE;
        return bRet;
    }

    printf("\n---------------------- AD_PLAY_START ----------------------\n");
    if(MM_interface->MM_Play(u32Item) == E_PLAYER_OK)
    {
        printf("MM_Play done\n");
        bRet=TRUE;
    }
    else
    {
        printf("MM_Play fail\n");
        return bRet;
    }

    return bRet;
}

BOOL MSrv_Advert_Player::AdvPlayerStop(void)
{
    BOOL bRet = FALSE;

    printf("\n---------------------- AD_STOP_STOP ----------------------\n");
    if(MM_interface->MM_Stop(u32Item) == E_PLAYER_OK)
    {
        bRet = TRUE;
    }
    else
    {
        return bRet;
    }

    if(MM_interface->MM_DeleteMediaItem(u32Item) == E_PLAYER_OK)
    {
        printf("MM_DeleteMediaItem done\n");
        bRet = TRUE;
    }
    else
    {
        printf("MM_DeleteMediaItem fail\n");
        return bRet;
    }

    for (U32 i=0; i<E_MEDIAFILE_ID_NUM; i++)
    {
        fseeko64(m_stMediaFileInfo[i].fp_mminterface, 0, SEEK_SET);
    }
    /*restore 3D auto-detect flag after ad player, if change 3D detect flag is false*/
    if (TRUE == m_bRestore3DdetectFlag)
    {
        MSrv_Control_common::GetMSrv3DManager()->Set3DFormatDetectFlag(TRUE);
        m_bRestore3DdetectFlag = FALSE;
    }
    return bRet;
}

BOOL MSrv_Advert_Player::AdvPlayerExit(void)
{
    BOOL bret = FALSE;

    CloseFile();
    m_pMediaFile1st = NULL;
    m_pMediaFile2nd = NULL;
    m_pMediaFile3rd = NULL;
    m_pMediaFile4th = NULL;

    if(MM_interface->RemoveEventRecipient(this))
    {
        bret = TRUE;
    }
    else
    {
        printf("\nRemoveEventRecipient fail\n");
        bret = FALSE;
    }
    if(MM_interface->MM_Finalize() == E_PLAYER_OK)
    {
        bret = TRUE;
    }
    else
    {
        printf("MM_Finalize fail\n");
        bret = FALSE;
    }

     bAdvert_player = FALSE;

    return bret;
}


BOOL MSrv_Advert_Player::Finalize(void)
{
    return TRUE;
}

EN_ADVERT_PLAYER_STATUS MSrv_Advert_Player::GetAdvPlayerPlayStatus(void)
{

    EN_ADVERT_PLAYER_STATUS enAdvertPlayerStatus = EN_NOT_INITIAL;

    if(bAdvert_player == FALSE)
    {
        enAdvertPlayerStatus = EN_NOT_INITIAL;
    }
    else
    {
        if(mm_init_fail == FALSE)
        {
            if(mm_decode_fail == TRUE)
            {
                enAdvertPlayerStatus =  EN_DECODE_FAIL;
            }
            else if(mm_decode_flag == TRUE)
            {
                enAdvertPlayerStatus =  E_DECODE_DONE;
            }
            else
            {
                enAdvertPlayerStatus =  EN_INITIAL_OK;
            }
        }
        else
        {
            enAdvertPlayerStatus =  EN_INITIAL_FAIL;
        }
    }

    return enAdvertPlayerStatus;
}

