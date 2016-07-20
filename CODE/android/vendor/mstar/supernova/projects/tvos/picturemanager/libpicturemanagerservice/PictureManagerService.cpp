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
#define LOG_TAG "PictureManagerService"
#include <utils/Log.h>

// EosTek Patch Begin
//ashton: for wb adjust
#include "MSrv_UartDebug.h"
// EosTek Patch End


#include <sys/shm.h>
#include <signal.h>
#include <binder/IServiceManager.h>
#include <binder/IPCThreadState.h>
#include <binder/Parcel.h>
#include <utils/String8.h>
#include <binder/IBinder.h>
#include "PictureManagerService.h"
#include "MSrv_Control.h"
#include "MSrv_Picture.h"
#if (ENABLE_BACKEND == 1)
#include "MSrv_Backend.h"
#endif
#include "MSrv_Video.h"
#include "mapi_pql.h"
#include "mapi_types.h"
#include "mapi_interface.h"
#if (HDMITX_ENABLE == 1)
#include "mapi_hdmitx.h"
#endif
#include "MSrv_Player.h"
#include "MSrv_System_Database.h"
#include <cutils/properties.h>
#include "SystemInfo.h"

#if (MWE_ENABLE == 1)
#include "MSrv_MWE.h"
#endif
#include "PictureManagerService_Customer.cpp"
#include "MyTypes.h"

#if(1==RELEASE_BINDER_TEST)
#include "PictureManagerServiceTest.h"
#endif
#if (ENABLE_4K2K_NIKEU == 1)
#include <cutils/properties.h>
#endif
#define __GETCLIENTINIT(clients, x)        \
sp<Client> x;                   \
for(int kk=0; kk<(int)(clients.size()); kk++)    \
{\
    x = clients[kk].promote(); \
    if (x == NULL)  {   \
       ALOGV("picturemanagerservice  client is null !!!!!\n");  \
       continue; \
    }


#define __GETCLIENTEND(client, remove_list)                                  \
}                                                         \
for (unsigned int jj=0;jj < remove_list.size();jj++) { \
    client.remove(remove_list[jj]);        \
}                                                         \
remove_list.clear();


#define lock(m_FuncLock) lock(m_FuncLock,(char*)(__FILE__),(char *)(__FUNCTION__),__LINE__)

extern Mutex m_FuncLock;
PictureManagerService* PictureManagerService::instantiate()
{
    PictureManagerService* comm = new PictureManagerService();
    ALOGV("PictureManagerService instantiate\n");
    defaultServiceManager()->addService(String16("mstar.PictureManager"), comm);
    return (comm);
}

// ----------------------------------------------------------------------------

PictureManagerService::PictureManagerService()
: m_Users(0)
{
    ALOGV("PictureManagerService created\n");
}

PictureManagerService::~PictureManagerService()
{
    ALOGV("PictureManagerService destroyed\n");
}

sp<IPictureManager> PictureManagerService::connect(const sp<IPictureManagerClient>& PictureManagerClient)
{
    TVOS_API_ESTIMATE_START()
    pid_t callingPid = IPCThreadState::self()->getCallingPid();
    ALOGV("PictureManagerService::connect(callingPid %d, client %p)\n", callingPid, PictureManagerClient->asBinder().get());

    sp<Client> sp_client = new Client(this, PictureManagerClient, callingPid);
    wp<Client> wp_client = sp_client;
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_Lock);
    TVOS_API_ESTIMATE_END(LOCK)
    m_Clients.add(wp_client);
    TVOS_API_ESTIMATE_END()
    return sp_client;
}

void PictureManagerService::removeClient(wp<Client> client)
{
    ALOGV("removeClient(callingPid %d)\n", IPCThreadState::self()->getCallingPid());

    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_Lock);
    TVOS_API_ESTIMATE_END(LOCK)
    if (m_bIsPosting == false)
    {
        m_Clients.remove(client);
    }
    else
    {
        m_vWaitingRemoveClients.add(client);
    }
    TVOS_API_ESTIMATE_END()
}

int32_t PictureManagerService::incUsers()
{
    TVOS_API_ESTIMATE_START()
    int32_t ret = android_atomic_inc(&m_Users);
    TVOS_API_ESTIMATE_END()
    return ret;
}

void PictureManagerService::decUsers()
{
    TVOS_API_ESTIMATE_START()
    android_atomic_dec(&m_Users);
    TVOS_API_ESTIMATE_END()
}

PictureManagerService::Client::Client(const sp<PictureManagerService>& PictureManagerService,
                                const sp<IPictureManagerClient>& PictureManagerClient,
                                pid_t clientPid)
: m_PictureManagerService(PictureManagerService), m_PictureManagerClient(PictureManagerClient), m_ClientPid(clientPid)
{
    TVOS_API_ESTIMATE_START()
    ALOGV("PictureManagerService::Client constructor(callingPid %d)\n", clientPid);
    m_ConnId = m_PictureManagerService->incUsers();
     m_bEnableDebug = false;
    TVOS_API_ESTIMATE_END()
}

PictureManagerService::Client::~Client()
{
    TVOS_API_ESTIMATE_START()
    ALOGV("PictureManagerService::Client destructor(callingPid %d)\n", m_ClientPid);
    disconnect();
    TVOS_API_ESTIMATE_END()
}

void PictureManagerService::Client::disconnect()
{
    TVOS_API_ESTIMATE_START()
    ALOGV("disconnect(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    wp<Client> client(this);
    m_PictureManagerService->removeClient(client);
    m_PictureManagerService->decUsers();
    m_PictureManagerClient.clear();
    TVOS_API_ESTIMATE_END()
}
//-------------------------------------------------------------------
///
void PictureManagerService::Client::disableDlc(void)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_DISABLEDLC()
    #endif
    ALOGV("PictureManagerService::Client::disableOverScan\n");
    MSrv_Control::GetMSrvPicture()->SetDLCOnOff(0);
    TVOS_API_ESTIMATE_END()
}

void PictureManagerService::Client::disableOverScan()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_DISABLEOVERSCAN()
    #endif
    ALOGV("PictureManagerService::Client::disableOverScan\n");
    MAPI_INPUT_SOURCE_TYPE enCurrentInputType=MSrv_Control::GetInstance()->GetCurrentInputSource();
    MSrv_Player *pMsrvPlayer = MSrv_Control::GetInstance()->GetMSrvPlayer(enCurrentInputType);
    pMsrvPlayer->SetUserOverScanMode(FALSE);
    TVOS_API_ESTIMATE_END()
}

void PictureManagerService::Client::disableWindow(void)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_DISABLEWINDOW()
    #endif
    ALOGV("PictureManagerService::Client::disableWindow\n");
    MSrv_Control::GetMSrvVideo()->DisableWindow();
    TVOS_API_ESTIMATE_END()
}

void PictureManagerService::Client::enableDlc(void)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_ENABLEDLC()
    #endif
    ALOGV("PictureManagerService::Client::disableOverScan\n");
    MSrv_Control::GetMSrvPicture()->SetDLCOnOff(1);
    TVOS_API_ESTIMATE_END()
}
///
void PictureManagerService::Client::enableOverScan()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_ENABLEOVERSCAN()
    #endif
    ALOGV("PictureManagerService::Client::enableOverScan\n");
    MAPI_INPUT_SOURCE_TYPE enCurrentInputType=MSrv_Control::GetInstance()->GetCurrentInputSource();
    MSrv_Player *pMsrvPlayer = MSrv_Control::GetInstance()->GetMSrvPlayer(enCurrentInputType);
    pMsrvPlayer->SetUserOverScanMode(TRUE);
    TVOS_API_ESTIMATE_END()

}

void PictureManagerService::Client::enableWindow(void)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_ENABLEWINDOW()
    #endif
    ALOGV("PictureManagerService::Client::enableWindow\n");
    MSrv_Control::GetMSrvVideo()->EnableWindow();
    TVOS_API_ESTIMATE_END()
}

bool PictureManagerService::Client::freezeImage()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_FREEZEIMAGE()
    #endif
    ALOGV("PictureManagerService::Client::freezeImage\n");
    MSrv_Control::GetMSrvVideo()->FreezeImage(TRUE);
    TVOS_API_ESTIMATE_END()
    return TRUE;
}

int32_t PictureManagerService::Client::getBacklight()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_GETBACKLIGHT()
    #endif
    ALOGV("PictureManagerService::Client::GetBacklight\n");
    int32_t ret = MSrv_Control::GetMSrvPicture()->GetBacklight();
    TVOS_API_ESTIMATE_END()
    return ret;
}

int32_t PictureManagerService::Client::getBacklightMaxValue()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_GETBACKLIGHTMAXVALUE()
    #endif
    ALOGV("PictureManagerService::Client::GetBacklightMaxValue\n");
    int32_t ret = MSrv_Control::GetMSrvPicture()->GetBacklightMaxValue();
    TVOS_API_ESTIMATE_END()
    return ret;
}

int32_t PictureManagerService::Client::getBacklightMinValue()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_GETBACKLIGHTMINVALUE()
    #endif
    ALOGV("PictureManagerService::Client::GetBacklightMinValue\n");
    int32_t ret = MSrv_Control::GetMSrvPicture()->GetBacklightMinValue();
    TVOS_API_ESTIMATE_END()
    return ret;
}

int32_t PictureManagerService::Client::getDemoMode(void)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
#if(1==RELEASE_BINDER_TEST)
    TEST_GETDEMOMODE()
#endif
    ALOGV("PictureManagerService::Client::GetDemoMode\n");
    int32_t ret = 0;
#if (MWE_ENABLE == 1)
    ret = MSrv_MWE::GetInstance()->GetDemoMode();
#endif
    TVOS_API_ESTIMATE_END()
    return ret;
}

bool PictureManagerService::Client::getDynamicContrastCurve(int32_t* outCurve)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_GETDYNAMICCONTRASTCURVE()
    #endif
    ALOGV("PictureManagerService::Client::GetDynamicContrastCurve\n");

    U16 pu16OutCurve[32] = {0};
    bool bretrun = FALSE;
    bretrun = MSrv_Control::GetMSrvPicture()->GetDynamicContrastCurve(pu16OutCurve, CONTRAST_CURVE_LENGTH);

    for(int i = 0; i<CONTRAST_CURVE_LENGTH; i++)
    {
        outCurve[i] = pu16OutCurve[i];
    }

    TVOS_API_ESTIMATE_END()

    return bretrun;
}

void PictureManagerService::Client::getPanelWidthHeight(PanelProperty &property)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_GETPANELWIDTHHEIGTH()
    #endif

    ALOGV("PictureManagerService::Client::getPanelWidthHeight\n");
    U16 W=0;
    U16 H=0;
    MSrv_Control::GetMSrvVideo()->GetPanelWidthHeight(&W, &H);
    property.height=H;
    property.width=W;
    TVOS_API_ESTIMATE_END()
}

bool PictureManagerService::Client::isOverscanEnabled()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_ISOVERSCANENABLED()
    #endif
    ALOGV("PictureManagerService::Client::isOverscanEnabled\n");
    MAPI_INPUT_SOURCE_TYPE enCurrentInputType=MSrv_Control::GetInstance()->GetCurrentInputSource();
    MSrv_Player *pMsrvPlayer = MSrv_Control::GetInstance()->GetMSrvPlayer(enCurrentInputType);
    bool ret = false;
    if(NULL != pMsrvPlayer)
    {
       ret = pMsrvPlayer->GetUserOverScanMode();
    }

    TVOS_API_ESTIMATE_END()

    return ret;
}

bool PictureManagerService::Client::scaleWindow()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    MAPI_INPUT_SOURCE_TYPE enCurrentInputType=MSrv_Control::GetInstance()->GetCurrentInputSource();
    #if(1==RELEASE_BINDER_TEST)
    TEST_SCALEWINDOW()
    #endif
    ALOGV("PictureManagerService::Client::ScaleWindow\n");
    MAPI_BOOL bVideoMute = MSrv_Control::GetMSrvVideo()->IsBlackVideoEnable();
    if(MAPI_FALSE == bVideoMute)
    {
        MSrv_Control::GetInstance()->SetVideoMute(TRUE, mapi_video_datatype::E_SCREEN_MUTE_BLACK, 0, enCurrentInputType);
    }
    MSrv_Control::GetMSrvVideo()->SetWindowInvisible();
    MSrv_Control::GetMSrvVideo()->DisableWindow();

    MSrv_Player *pMsrvPlayer = MSrv_Control::GetInstance()->GetMSrvPlayer(enCurrentInputType);
    if(mapi_interface::Get_mapi_video(enCurrentInputType)->IsLockDispWindow() || NULL == pMsrvPlayer)
    {
        MSrv_Control::GetMSrvVideo()->ScaleWindow();
    }
    else
    {
        mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO stVideoARCInfo;
        MSrv_Control::GetMSrvSystemDatabase()->GetVideoArc(&stVideoARCInfo.enARCType, &enCurrentInputType);
        stVideoARCInfo.s16Adj_ARC_Left = 0;
        stVideoARCInfo.s16Adj_ARC_Right = 0;
        stVideoARCInfo.s16Adj_ARC_Up = 0;
        stVideoARCInfo.s16Adj_ARC_Down = 0;
        pMsrvPlayer->SetUserOverScanMode(TRUE); // check if needed to call it or not
        pMsrvPlayer->SetAspectRatio(stVideoARCInfo);
        pMsrvPlayer->SetUserOverScanMode(FALSE);// check if needed to call it or not
    }


    MSrv_Control::GetMSrvVideo()->EnableWindow();
    MSrv_Control::GetMSrvVideo()->SetWindowVisible();
   if(MAPI_FALSE == bVideoMute)
    {
        MSrv_Control::GetInstance()->SetVideoMute(FALSE, mapi_video_datatype::E_SCREEN_MUTE_BLACK, 0, enCurrentInputType);
    }
    TVOS_API_ESTIMATE_END()
    return true;
}

bool PictureManagerService::Client::selectWindow(int32_t windowId)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_SELECTWINDOW()
    #endif
    bool ret = MSrv_Control::GetMSrvVideo()->SelectWindow((MAPI_SCALER_WIN)windowId);
    TVOS_API_ESTIMATE_END()
    return ret;

}

bool PictureManagerService::Client::isSupportedZoom()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    MAPI_INPUT_SOURCE_TYPE enCurrentInputType = MSrv_Control::GetInstance()->GetCurrentInputSource();
    MSrv_Player *pMsrvPlayer = MSrv_Control::GetInstance()->GetMSrvPlayer(enCurrentInputType);
    bool ret = pMsrvPlayer->isSupportedZoom();
    TVOS_API_ESTIMATE_END()
    return ret;
}

///
void PictureManagerService::Client::setAspectRatio(int32_t enAspectRatioTYpe)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_SETASPECTRATIO()
    #endif
    ALOGV("PictureManagerService::Client::SetAspectRatio\n");
    mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO stVideoARCInfo;
    MAPI_INPUT_SOURCE_TYPE enCurrentInputType=MSrv_Control::GetInstance()->GetCurrentMainInputSource();
    MSrv_Player *pMsrvPlayer = MSrv_Control::GetInstance()->GetMSrvPlayer(enCurrentInputType);

    // inform hwcomposer to set AspectRatio if DS is enable
    m_PictureManagerService->PostEvent(EV_SET_ASPECTRATIO, enAspectRatioTYpe, 0,true);

    if (enCurrentInputType == MAPI_INPUT_SOURCE_STORAGE || enCurrentInputType == MAPI_INPUT_SOURCE_KTV
        || enCurrentInputType == MAPI_INPUT_SOURCE_STORAGE2)
    {
        ALOGD("handle by AN hwcomposer");
        //save the DB
        stVideoARCInfo.enARCType = (mapi_video_datatype::MAPI_VIDEO_ARC_Type)enAspectRatioTYpe;
        MSrv_Control::GetMSrvSystemDatabase()->SetVideoArc(&stVideoARCInfo.enARCType, &enCurrentInputType);
        TVOS_API_ESTIMATE_END()
        return;
    }

    if(NULL != pMsrvPlayer)
    {
        //mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO stVideoARCInfo;
        stVideoARCInfo.enARCType=(mapi_video_datatype::MAPI_VIDEO_ARC_Type)enAspectRatioTYpe;
        stVideoARCInfo.s16Adj_ARC_Left = 0;
        stVideoARCInfo.s16Adj_ARC_Right = 0;
        stVideoARCInfo.s16Adj_ARC_Up = 0;
        stVideoARCInfo.s16Adj_ARC_Down = 0;


        //save the DB
        MSrv_Control::GetMSrvSystemDatabase()->SetVideoArc(&stVideoARCInfo.enARCType, &enCurrentInputType);

        pMsrvPlayer->SetUserOverScanMode(TRUE); // check if needed to call it or not
        pMsrvPlayer->SetAspectRatio(stVideoARCInfo);
        pMsrvPlayer->SetUserOverScanMode(FALSE);// check if needed to call it or not

    }
    TVOS_API_ESTIMATE_END()
}


int32_t PictureManagerService::Client::getAspectRatio()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
#if(1==RELEASE_BINDER_TEST)
    TEST_GETDLCAVERAGELUMA()
#endif
    ALOGV("PictureManagerService::Client::getAspectRatio\n");
    mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO stVideoARCInfo;
    memset(&stVideoARCInfo, 0, sizeof(stVideoARCInfo));
    MAPI_INPUT_SOURCE_TYPE enCurrentInputType=MSrv_Control::GetInstance()->GetCurrentInputSource();
    MSrv_Player *pMsrvPlayer = MSrv_Control::GetInstance()->GetMSrvPlayer(enCurrentInputType);


    if(NULL != pMsrvPlayer)
    {
         MSrv_Control::GetMSrvSystemDatabase()->GetVideoArc(&stVideoARCInfo.enARCType, &enCurrentInputType);
    }
    TVOS_API_ESTIMATE_END()
    return stVideoARCInfo.enARCType;
}


void PictureManagerService::Client::setBacklight(int32_t value)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_SETBACKLIGHT()
    #endif
    ALOGV("PictureManagerService::Client::SetBacklight\n");

    MSrv_Control::GetMSrvPicture()->SetBacklight(value);
    TVOS_API_ESTIMATE_END()
}

void PictureManagerService::Client::setColorTemperature(PQL_COLOR_TEMP_DATA &pstColorTemp)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_SETCOLORTEMPERATURE()
    #endif

    ALOGV("PictureManagerService::Client::setColorTemperature\n");


    mapi_pql_datatype::MAPI_PQL_COLOR_TEMP_DATA pstColorTemp1;

    pstColorTemp1.u8RedGain=pstColorTemp.u8RedGain;
    pstColorTemp1.u8GreenGain=pstColorTemp.u8GreenGain;
    pstColorTemp1.u8BlueGain=pstColorTemp.u8BlueGain;
    pstColorTemp1.u8RedOffset=pstColorTemp.u8RedOffset;
    pstColorTemp1.u8GreenOffset=pstColorTemp.u8GreenOffset;
    pstColorTemp1.u8BlueOffset=pstColorTemp.u8BlueOffset;

    MSrv_Control::GetMSrvPicture()->SetColorTemperature(&pstColorTemp1);
    TVOS_API_ESTIMATE_END()
}

void PictureManagerService::Client::getColorTemperature(PQL_COLOR_TEMPEX_DATA &pstColorTemp)
{
    mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA stColorTemp;
    memset(&stColorTemp, 0, sizeof(mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA));

    MSrv_Control::GetMSrvPicture()->GetColorTemperature(stColorTemp);

    stColorTemp.u16RedGain = pstColorTemp.u16RedGain;
    stColorTemp.u16GreenGain = pstColorTemp.u16GreenGain;
    stColorTemp.u16BlueGain = pstColorTemp.u16BlueGain;
    stColorTemp.u16RedOffset = pstColorTemp.u16RedOffset;
    stColorTemp.u16GreenOffset = pstColorTemp.u16GreenOffset;
    stColorTemp.u16BlueOffset = pstColorTemp.u16BlueOffset;
}

void PictureManagerService::Client::setColorTemperatureEX(PQL_COLOR_TEMPEX_DATA &pstColorTemp)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_SETCOLORTEMPERATUREEX()
    #endif

    ALOGV("PictureManagerService::Client::setColorTemperature\n");


    mapi_pql_datatype::MAPI_PQL_COLOR_TEMPEX_DATA pstColorTemp1;

    pstColorTemp1.u16RedGain=pstColorTemp.u16RedGain;
    pstColorTemp1.u16GreenGain=pstColorTemp.u16GreenGain;
    pstColorTemp1.u16BlueGain=pstColorTemp.u16BlueGain;
    pstColorTemp1.u16RedOffset=pstColorTemp.u16RedOffset;
    pstColorTemp1.u16GreenOffset=pstColorTemp.u16GreenOffset;
    pstColorTemp1.u16BlueOffset=pstColorTemp.u16BlueOffset;

    MSrv_Control::GetMSrvPicture()->SetColorTemperature(&pstColorTemp1);
    TVOS_API_ESTIMATE_END()
}

void PictureManagerService::Client::setCropWindow(int32_t h, int32_t w, int32_t y, int32_t x)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_SETCROPWINDOW()
    #endif
    mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stCropInfo;
    stCropInfo.x      = x;
    stCropInfo.y      = y;
    stCropInfo.width  = w;
    stCropInfo.height = h;
    MSrv_Control::GetMSrvVideo()->SetCropWindow(&stCropInfo);
    TVOS_API_ESTIMATE_END()
}

void PictureManagerService::Client::setDemoMode(int32_t enMsMweType)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_SETDEMOMODE()
    #endif
    MSrv_MWE::GetInstance()->SetDemoMode((MSrv_MWE::EN_MS_MWE_TYPE)enMsMweType);
    TVOS_API_ESTIMATE_END()

}
     //no use in UI ,just define
void PictureManagerService::Client::setDisplayWindow(int32_t h, int32_t w, int32_t y, int32_t x)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_SETDISPLAYWINDOW()
    #endif

    mapi_video_datatype::ST_MAPI_VIDEO_WINDOW_TYPE stDispInfo;
    stDispInfo.x      = x;
    stDispInfo.y      = y;
    stDispInfo.width  = w;
    stDispInfo.height = h;
    MSrv_Control::GetMSrvVideo()->SetDisplayWindow(&stDispInfo);
    TVOS_API_ESTIMATE_END()
}

void PictureManagerService::Client::setDynamicContrastCurve(int32_t *normalCurve, int32_t *lightCurve, int32_t *darkCurve)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_SETDYNAMICCONTRASTCURVE()
    #endif

    ALOGV("PictureManagerService::Client::SetBacklight\n");
    U16 pu16NormalCurve[17] = {0};
    U16 pu16LightCurve[17] = {0};
    U16 pu16DarkCurve[17] = {0};

    for(int i = 0; i<16; i++)
    {
        pu16NormalCurve[i] = normalCurve[i];
        pu16LightCurve[i] = lightCurve[i];
        pu16DarkCurve[i] = darkCurve[i];
    }

    MSrv_Control::GetMSrvPicture()->SetDynamicContrastCurve(pu16NormalCurve, pu16LightCurve, pu16DarkCurve);
    TVOS_API_ESTIMATE_END()
}

void PictureManagerService::Client::setFilm(int32_t enMsFile)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_SETFLIM()
    #endif
    ALOGV("PictureManagerService::Client::SetFilm\n");

    MSrv_Control::GetMSrvPicture()->SetFilm((MSrv_Picture::EN_MS_FILM)enMsFile);
    TVOS_API_ESTIMATE_END()
}

void PictureManagerService::Client::setMfc(EN_MFC_MODE enMode)
{
    TVOS_API_ESTIMATE_START()
#if (ENABLE_BACKEND == 1)
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_SETMFC()
    #endif
    ALOGV("PictureManagerService::Client::setMfc\n");

    switch (enMode)
    {
        case E_MFC_OFF:
        {
            MSrv_Control::GetMSrvBackend()->SetMfcOff();
        }
        break;
        case E_MFC_LOW:
        {
            MSrv_Control::GetMSrvBackend()->SetMfcLow();
        }
        break;
        case E_MFC_HIGH:
        {
            MSrv_Control::GetMSrvBackend()->SetMfcHigh();
        }
        break;
        case E_MFC_MIDDLE:
        {
            MSrv_Control::GetMSrvBackend()->SetMfcMiddle();
        }
        break;
        case E_MFC_BYPASS:
        {
            MSrv_Control::GetMSrvBackend()->SetMfcBypass();
        }
        break;
        default:
        {
            ALOGV("PictureManagerService::Client::setMfc  UNKNOWN mode:%d\n", enMode);
            return;
        }
    }
#endif
    TVOS_API_ESTIMATE_END()
}

EN_MFC_MODE  PictureManagerService::Client::getMfc()
{

    EN_MFC_MODE ret = E_MFC_OFF;
    TVOS_API_ESTIMATE_START()
#if (ENABLE_BACKEND == 1)
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_GETMFC()
    #endif
    MFC_LEVEL   enMfcLevel = MSrv_Control::GetMSrvBackend()->GetMfcLevel();
    switch (enMfcLevel)
    {
        case MFC_LEVEL_OFF:
        {
            ret = E_MFC_OFF;
        }
        break;
        case MFC_LEVEL_LOW:
        {
            ret = E_MFC_LOW;
        }
        break;
        case MFC_LEVEL_HIGH:
        {
            ret = E_MFC_HIGH;
        }
        break;
        case MFC_LEVEL_MID:
        {
            ret = E_MFC_MIDDLE;
        }
        break;
        case MFC_LEVEL_BYPASS:
        {
            ret = E_MFC_BYPASS;
        }
        break;
        default:
        {
            ALOGV("PictureManagerService::Client::getMfc  UNKNOWN mode:%d\n", enMfcLevel);
        }
    }
#endif
    TVOS_API_ESTIMATE_END()
    return ret;

}
bool PictureManagerService::Client::setMpegNoiseReduction(int32_t enMNRMode)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_SETMPEGNOISEREDUCTION()
    #endif
    ALOGV("PictureManagerService::Client::SetFilm\n");
    bool ret = MSrv_Control::GetMSrvPicture()->SetMpegNR((MSrv_Picture::EN_MS_PIC_MPEG_NR)enMNRMode);
    TVOS_API_ESTIMATE_END()
    return ret;

}

bool PictureManagerService::Client::setNoiseReduction(int32_t nr)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_SETNOISEREDUCTION()
    #endif
    ALOGV("PictureManagerService::Client::SetFilm\n");
    bool ret = MSrv_Control::GetMSrvPicture()->SetNoiseReduction((MSrv_Picture::EN_MS_PIC_NR)nr);
    TVOS_API_ESTIMATE_END()
    return ret;
}

void PictureManagerService::Client::setOutputPattern(bool bEnable, int16_t u16Red, int16_t u16Green, int16_t u16Blue)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_SETOUTPUTPATTERN()
    #endif
    ALOGV("PictureManagerService::Client::SetOutputPattern\n");
    MSrv_Control::GetMSrvVideo()->SetOutputPattern(bEnable, u16Red ,u16Green, u16Blue);
    TVOS_API_ESTIMATE_END()

}


void PictureManagerService::Client::setOverscan (int32_t bottom, int32_t top, int32_t right, int32_t left)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_SETOVERSCAN()
    #endif

    mapi_video_datatype::ST_MAPI_VIDEO_ARC_INFO stVideoARCInfo;
    MAPI_INPUT_SOURCE_TYPE enCurrentInputType=MSrv_Control::GetInstance()->GetCurrentInputSource();
    MSrv_Player *player = MSrv_Control::GetInstance()->GetMSrvPlayer(enCurrentInputType);


    stVideoARCInfo.s16Adj_ARC_Left = left;
    stVideoARCInfo.s16Adj_ARC_Right = right;
    stVideoARCInfo.s16Adj_ARC_Up = top;
    stVideoARCInfo.s16Adj_ARC_Down = bottom;
    stVideoARCInfo.bSetCusWin = 0;
    MSrv_Control::GetMSrvSystemDatabase()->GetVideoArc(&stVideoARCInfo.enARCType, &enCurrentInputType);
    player->FinetuneOverscan(stVideoARCInfo);
    TVOS_API_ESTIMATE_END()

}

void PictureManagerService::Client::setPictureModeBrightness(int16_t value)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_SETPICTUREMODEBRIGHTNESS()
    #endif
    ALOGV("PictureManagerService::Client::setPictureModeBrightness\n");
    MSrv_Control::GetMSrvPicture()->SetPictureModeBrightness(value);
    TVOS_API_ESTIMATE_END()
}

void PictureManagerService::Client::setPictureModeBrightness(int32_t setLocationType, int32_t value)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_SETPICTUREMODEBRIGHTNESS2()
    #endif

    ALOGV("PictureManagerService::Client::setPictureModeBrightness\n");
    MSrv_Control::GetMSrvPicture()->SetPictureModeBrightness(value);
    TVOS_API_ESTIMATE_END()
}

bool PictureManagerService::Client::GetPictureModeBrightness(int16_t * const value)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    //TEST_SETPICTUREMODEBRIGHTNESS()
    #endif
    ALOGV("PictureManagerService::Client::setPictureModeBrightness\n");


    T_MS_VIDEO stVideoTemp;
    MAPI_INPUT_SOURCE_TYPE enCurrentInputType;
    MSrv_Control* instance = MSrv_Control::GetInstance();
    if(instance == NULL)
    {
        ALOGV("GetPictureModeBrightness Fail\n");
        TVOS_API_ESTIMATE_END()
        return FALSE;
    }
    enCurrentInputType = instance->GetCurrentInputSource();

    MSrv_System_Database *database = MSrv_Control::GetMSrvSystemDatabase();
    if(database == NULL)
    {
        ALOGV("GetPictureModeBrightness Fail\n");
        TVOS_API_ESTIMATE_END()
        return FALSE;
    }
    database->GetVideoSetting(&stVideoTemp, &enCurrentInputType);
    *value = stVideoTemp.astPicture[stVideoTemp.ePicture].u8Brightness;
    ALOGV("GetPictureModeBrightness = %d\n", *value);
    TVOS_API_ESTIMATE_END()
    return TRUE;
}
void PictureManagerService::Client::setPictureModeColor(int16_t value)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_SETPICTUREMODECOLOR()
    #endif
    ALOGV("PictureManagerService::Client::setPictureModeColor\n");
    MSrv_Control::GetMSrvPicture()->SetPictureModeColor(value);
    TVOS_API_ESTIMATE_END()
}

bool PictureManagerService::Client::GetPictureModeSaturation(int16_t * const value)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    //TEST_SETPICTUREMODEBRIGHTNESS()
    #endif
    ALOGV("PictureManagerService::Client::GetPictureModeSaturation\n");


    T_MS_VIDEO stVideoTemp;
    MAPI_INPUT_SOURCE_TYPE enCurrentInputType;
    MSrv_Control* instance = MSrv_Control::GetInstance();
    if(instance == NULL)
    {
        ALOGV("GetPictureModeSaturation Fail\n");
        TVOS_API_ESTIMATE_END()
        return FALSE;
    }
    enCurrentInputType = instance->GetCurrentInputSource();

    MSrv_System_Database *database = MSrv_Control::GetMSrvSystemDatabase();
    if(database == NULL)
    {
        ALOGV("GetPictureModeSaturation Fail\n");
        TVOS_API_ESTIMATE_END()
        return FALSE;
    }
    database->GetVideoSetting(&stVideoTemp, &enCurrentInputType);
    *value = stVideoTemp.astPicture[stVideoTemp.ePicture].u8Saturation;
    ALOGV("GetPictureModeSaturation = %d\n", *value);
    TVOS_API_ESTIMATE_END()
    return TRUE;
}
void PictureManagerService::Client::setPictureModeContrast(int16_t value)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_SETPICTUREMODECOLOR()
    #endif
    ALOGV("PictureManagerService::Client::setPictureModeContrast\n");
    MSrv_Control::GetMSrvPicture()->SetPictureModeContrast(value);
    TVOS_API_ESTIMATE_END()
}


bool PictureManagerService::Client::GetPictureModeContrast(int16_t * const value)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    //TEST_SETPICTUREMODEBRIGHTNESS()
    #endif
    ALOGV("PictureManagerService::Client::GetPictureModeContrast\n");


    T_MS_VIDEO stVideoTemp;
    MAPI_INPUT_SOURCE_TYPE enCurrentInputType;
    MSrv_Control* instance = MSrv_Control::GetInstance();
    if(instance == NULL)
    {
        ALOGV("GetPictureModeContrast Fail\n");
        TVOS_API_ESTIMATE_END()
        return FALSE;
    }
    enCurrentInputType = instance->GetCurrentInputSource();

    MSrv_System_Database *database = MSrv_Control::GetMSrvSystemDatabase();
    if(database == NULL)
    {
        ALOGV("GetPictureModeContrast Fail\n");
        TVOS_API_ESTIMATE_END()
        return FALSE;
    }
    database->GetVideoSetting(&stVideoTemp, &enCurrentInputType);
    *value = stVideoTemp.astPicture[stVideoTemp.ePicture].u8Contrast;
    ALOGV("GetPictureModeContrast = %d\n", *value);
    TVOS_API_ESTIMATE_END()
    return TRUE;
}
void PictureManagerService::Client::setPictureModeSharpness(int16_t value)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_SETPICTUREMODESHARPNESS()
    #endif

    ALOGV("PictureManagerService::Client::setPictureModeSharpness\n");
    MSrv_Control::GetMSrvPicture()->SetPictureModeSharpness(value);
    TVOS_API_ESTIMATE_END()
}

bool PictureManagerService::Client::GetPictureModeSharpness(int16_t * const value)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    //TEST_SETPICTUREMODEBRIGHTNESS()
    #endif
    ALOGV("PictureManagerService::Client::GetPictureModeSharpness\n");


    T_MS_VIDEO stVideoTemp;
    MAPI_INPUT_SOURCE_TYPE enCurrentInputType;
    MSrv_Control* instance = MSrv_Control::GetInstance();
    if(instance == NULL)
    {
        ALOGV("GetPictureModeSharpness Fail\n");
        TVOS_API_ESTIMATE_END()
        return FALSE;
    }
    enCurrentInputType = instance->GetCurrentInputSource();

    MSrv_System_Database *database = MSrv_Control::GetMSrvSystemDatabase();
    if(database == NULL)
    {
        ALOGV("GetPictureModeSharpness Fail\n");
        TVOS_API_ESTIMATE_END()
        return FALSE;
    }
    database->GetVideoSetting(&stVideoTemp, &enCurrentInputType);
    *value = stVideoTemp.astPicture[stVideoTemp.ePicture].u8Sharpness;
    ALOGV("GetPictureModeSharpness = %d\n", *value);
    TVOS_API_ESTIMATE_END()
    return TRUE;
}
void PictureManagerService::Client::setPictureModeTint(int16_t value)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_SETPICTUREMODETINT()
    #endif
    ALOGV("PictureManagerService::Client::setPictureModeTint\n");
    MSrv_Control::GetMSrvPicture()->SetPictureModeTint(value);
    TVOS_API_ESTIMATE_END()
}

bool PictureManagerService::Client::GetPictureModeHue(int16_t * const value)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    //TEST_SETPICTUREMODEBRIGHTNESS()
    #endif
    ALOGV("PictureManagerService::Client::GetPictureModeHue\n");


    T_MS_VIDEO stVideoTemp;
    MAPI_INPUT_SOURCE_TYPE enCurrentInputType;
    MSrv_Control* instance = MSrv_Control::GetInstance();
    if(instance == NULL)
    {
        ALOGV("GetPictureModeHue Fail\n");
        TVOS_API_ESTIMATE_END()
        return FALSE;
    }
    enCurrentInputType = instance->GetCurrentInputSource();

    MSrv_System_Database *database = MSrv_Control::GetMSrvSystemDatabase();
    if(database == NULL)
    {
        ALOGV("GetPictureModeHue Fail\n");
        TVOS_API_ESTIMATE_END()
        return FALSE;
    }
    database->GetVideoSetting(&stVideoTemp, &enCurrentInputType);
    *value = stVideoTemp.astPicture[stVideoTemp.ePicture].u8Hue;
    ALOGV("GetPictureModeHue = %d\n", *value);
    TVOS_API_ESTIMATE_END()
    return TRUE;
}
void PictureManagerService::Client::setPictureModeInputSource(int32_t inputSource)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_SETPICTUREMODEINPUTSOURCE()
    #endif
    ALOGV("PictureManagerService::Client::setPictureModeInputSource\n");
    TVOS_API_ESTIMATE_END()

}

void PictureManagerService::Client::setWindowInvisible()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_SETWINDOWINVISIBLE()
    #endif
    ALOGV("PictureManagerService::Client::setWindowVisibled\n");
    MSrv_Control::GetMSrvVideo()->SetWindowInvisible();
    TVOS_API_ESTIMATE_END()
}

void PictureManagerService::Client::setWindowVisible()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_SETWINDOWVISIBLE()
    #endif
    ALOGV("PictureManagerService::Client::setWindowVisibled\n");
    MSrv_Control::GetMSrvVideo()->SetWindowVisible();
    TVOS_API_ESTIMATE_END()
}

bool PictureManagerService::Client::unFreezeImage(void)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_UNFREEZEIMAGE()
    #endif
    ALOGV("PictureManagerService::Client::freezeImage\n");
    MSrv_Control::GetMSrvVideo()->FreezeImage(FALSE);
    TVOS_API_ESTIMATE_END()
    return TRUE;
}

bool PictureManagerService::Client::IsImageFreezed(void)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_ISIMAGEFREEZED()
    #endif
    ALOGV("PictureManagerService::Client::IsImageFreezed\n");
    bool ret = MSrv_Control::GetMSrvVideo()->IsImageFreezed();
    TVOS_API_ESTIMATE_END()
    return ret;
}

int16_t PictureManagerService::Client::getDlcAverageLuma()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_GETDLCAVERAGELUMA()
    #endif
    ALOGV("PictureManagerService::Client::getDlcAverageLuma\n");
    int16_t ret = (int16_t)MSrv_Control::GetMSrvPicture()->GetDlcAverageLuma();
    TVOS_API_ESTIMATE_END()
    return ret;
}

void PictureManagerService::Client::setDebugMode(bool mode)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)

    m_bEnableDebug = mode;
    TVOS_API_ESTIMATE_END()
}

bool PictureManagerService::Client::disableOsdWindow(int32_t win)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_BOOL_SHORT("disableOsdWindow","win",win);
    #endif
    ALOGV("PictureManagerService::Client::disableOsdWindow\n");
    printf("NOT IMPLEMENTED\n");
    TVOS_API_ESTIMATE_END()
    return false;
}

bool PictureManagerService::Client::disableAllOsdWindow()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_BOOL_NULL("disableAllOsdWindow");
    #endif
    ALOGV("PictureManagerService::Client::disableAllOsdWindow\n");
    printf("NOT IMPLEMENTED\n");
    TVOS_API_ESTIMATE_END()
    return false;
}

bool PictureManagerService::Client::setOsdWindow(int32_t win, uint16_t u16StartX, uint16_t u16Width, uint16_t u16StartY, uint16_t u16Height)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_BOOL_INT5("setOsdWindow","win",win,"u16StartX",u16StartX,"u16Width",u16Width,"u16StartY",u16StartY,"u16Height",u16Height);
    #endif
    ALOGV("PictureManagerService::Client::setOsdWindow\n");
    printf("NOT IMPLEMENTED\n");
    TVOS_API_ESTIMATE_END()
    return false;
}

void PictureManagerService::Client::setColorRange(bool colorRange0_255)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_VOID_INI("setColorRange","colorRange0_255",colorRange0_255);
    #endif
    ALOGV("PictureManagerService::Client::setColorRange\n");
    MSrv_Control::GetMSrvPicture()->SetColorRange(colorRange0_255);
    TVOS_API_ESTIMATE_END()
}

bool PictureManagerService::Client::autoHDMIColorRange()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
#if(1==RELEASE_BINDER_TEST)
    TEST_BOOL_NULL("autoHDMIColorRange");
#endif
    ALOGV("PictureManagerService::Client::autoHDMIColorRange\n");
    bool ret = MSrv_Control::GetMSrvPicture()->AutoHDMIColorRange();
    TVOS_API_ESTIMATE_END()
    return ret;
}

int32_t PictureManagerService::Client::getCustomerPqRuleNumber()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_INI_NULL("getCustomerPqRuleNumber");
    #endif
    ALOGV("PictureManagerService::Client::getCustomerPqRuleNumber\n");
    int32_t ret = 0;
#if (STB_ENABLE == 0)
    ret = MSrv_Control::GetMSrvPicture()->GetGRuleNum();
#endif
    TVOS_API_ESTIMATE_END()
    return ret;
}

int32_t PictureManagerService::Client::getStatusNumberByCustomerPqRule(int32_t ruleType)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_INI_INT("getStatusNumberByCustomerPqRule","ruleType",ruleType);
    #endif
    ALOGV("PictureManagerService::Client::getStatusNumberByCustomerPqRule\n");
    int32_t ret = 0;
#if (STB_ENABLE == 0)
    ret = MSrv_Control::GetMSrvPicture()->GetGRuleStatusNum(ruleType);
#endif
    TVOS_API_ESTIMATE_END()
    return ret;
}

bool PictureManagerService::Client::setStatusByCustomerPqRule(int32_t ruleType, int32_t ruleStatus)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_INI_INT2("setStatusByCustomerPqRule","ruleType",ruleType,"ruleStatus",ruleStatus);
    #endif
    ALOGV("PictureManagerService::Client::setStatusByCustomerPqRule\n");

    bool ret = false;
#if (STB_ENABLE == 0)
    ret = MSrv_Control::GetMSrvPicture()->SetGRuleStatus(ruleType, ruleStatus);
#endif
    TVOS_API_ESTIMATE_END()
    return ret;
}

bool PictureManagerService::Client::moveWindow()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_BOOL_NULL("moveWindow");
    #endif
    ALOGV("PictureManagerService::Client::moveWindow\n");

    bool ret = MSrv_Control::GetMSrvVideo()->MoveWindowPosition();
    TVOS_API_ESTIMATE_END()
    return ret;
}

void PictureManagerService::Client::enableBacklight()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_VOID_NULL("enableBacklight");
    #endif
    ALOGV("PictureManagerService::Client::enableBacklight\n");

    MSrv_Control::GetMSrvPicture()->SetBacklight_OnOff(TRUE);
    TVOS_API_ESTIMATE_END()
}

void PictureManagerService::Client::disableBacklight()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_VOID_NULL("disableBacklight");
    #endif
    ALOGV("PictureManagerService::Client::disableBacklight\n");

    MSrv_Control::GetMSrvPicture()->SetBacklight_OnOff(FALSE);
    TVOS_API_ESTIMATE_END()
}

void PictureManagerService::Client::generateTestPattern(TEST_PATTERN_MODE ePatternMode, uint32_t u32Length, void* para)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_VOID_NULL("setMVOPTestPattern");
    #endif
    ALOGV("PictureManagerService::Client::generateTestPattern[PMode=%d,uL=%d]\n",ePatternMode,u32Length);
    MSrv_Control::GetMSrvVideo()->GenerateTestPattern((EN_TEST_PATTERN_MODE)ePatternMode, para, u32Length);
    TVOS_API_ESTIMATE_END()
}

void PictureManagerService::Client::getDlcLumArray(int32_t *pArray, int32_t arrayLen)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    for(int i = 0; i<arrayLen; i++)
    {
        pArray[i]=2;
    }
    TEST_VOID_INI("disableBacklight", "arrayLen", arrayLen);
    #endif
    ALOGV("PictureManagerService::Client::getDlcLumArray\n");
    U16 pu16Array[32] = {0};

    MSrv_Control::GetMSrvPicture()->GetDLCLumArray(pu16Array, arrayLen);

    for(int i = 0; i<arrayLen; i++)
    {
        pArray[i] = pu16Array[i];
    }
    TVOS_API_ESTIMATE_END()
}

int32_t PictureManagerService::Client::getDlcLumAverageTemporary()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_INI_NULL("getDlcLumAverageTemporary");
    #endif
    ALOGV("PictureManagerService::Client::getDlcLumAverageTemporary\n");

    int32_t ret = MSrv_Control::GetMSrvPicture()->GetDLCLumAverageTemp();
    TVOS_API_ESTIMATE_END()
    return ret;
}

int32_t PictureManagerService::Client::getDlcLumTotalCount()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_INI_NULL("getDlcLumTotalCount");
    #endif
    ALOGV("PictureManagerService::Client::getDlcLumTotalCount\n");

    int32_t ret = MSrv_Control::GetMSrvPicture()->GetDLCLumTotalCount();
    TVOS_API_ESTIMATE_END()
    return ret;
}

bool PictureManagerService::Client::switchDlcCurve(int16_t dlcCurveIndex)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_BOOL_INT("switchDlcCurve","dlcCurveIndex",dlcCurveIndex);
    #endif
    ALOGV("PictureManagerService::Client::switchDlcCurve\n");

    bool ret = MSrv_Control::GetMSrvPicture()->SwitchDLCCurve(dlcCurveIndex);
    TVOS_API_ESTIMATE_END()
    return ret;
}

void PictureManagerService::Client::getPixelRgb(int32_t eStage, int16_t x, int16_t y, int32_t eWindow, GET_RGB_DATA &rgbData)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    if(true == m_bEnableDebug)
    {
        rgbData.u32r = 2;
        rgbData.u32g = 2;
        rgbData.u32b =2;
    }
    TEST_VOID_INT4("getPixelRgb","eStage",eStage,"x",x,"y",y,"eWindow",eWindow);
    #endif
    ALOGV("PictureManagerService::Client::getPixelRgb\n");

    MSrv_Picture::T_MS_Get_RGB_Data data = MSrv_Control::GetMSrvPicture()->GetPixelRGB((MSrv_Picture::EN_MS_GET_PIXEL_RGB_STAGE)eStage,x,y,(MAPI_SCALER_WIN)eWindow);
    rgbData.u32r = data.u32r;
    rgbData.u32g = data.u32g;
    rgbData.u32b = data.u32b;
    TVOS_API_ESTIMATE_END()
}

bool PictureManagerService::Client::getPixelInfo(Screen_Pixel_Info *pPixInfo)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
#if(1==RELEASE_BINDER_TEST)
    TEST_INI_NULL("getPixelInfo");
#endif
    ALOGV("PictureManagerService::Client::getPixelInfo\n");

    SCREEN_PIXELINFO PinxelInfo = {0};
    PinxelInfo.u16XStart = pPixInfo->u16XStart;
    PinxelInfo.u16XEnd = pPixInfo->u16XEnd;
    PinxelInfo.u16YStart = pPixInfo->u16YStart;
    PinxelInfo.u16YEnd = pPixInfo->u16YEnd;
    bool bRet = MSrv_Control::GetMSrvPicture()->GetPixelInfoData(&PinxelInfo);
    if(true == bRet)
    {
        pPixInfo->u32ReportPixelInfo_Version = PinxelInfo.u32ReportPixelInfo_Version;
        pPixInfo->u16ReportPixelInfo_Length = PinxelInfo.u16ReportPixelInfo_Length;
        pPixInfo->u16RepWinColor = PinxelInfo.u16RepWinColor;
        pPixInfo->u16XStart = PinxelInfo.u16XStart;
        pPixInfo->u16XEnd = PinxelInfo.u16XEnd;
        pPixInfo->u16YStart = PinxelInfo.u16YStart;
        pPixInfo->u16YEnd = PinxelInfo.u16YEnd;
        pPixInfo->u16RCrMin = PinxelInfo.u16RCrMin;
        pPixInfo->u16RCrMax = PinxelInfo.u16RCrMax;
        pPixInfo->u16GYMin = PinxelInfo.u16GYMin;
        pPixInfo->u16GYMax = PinxelInfo.u16GYMax;
        pPixInfo->u16BCbMin = PinxelInfo.u16BCbMin;
        pPixInfo->u16BCbMax = PinxelInfo.u16BCbMax;
        pPixInfo->u32RCrSum = PinxelInfo.u32RCrSum;
        pPixInfo->u32GYSum = PinxelInfo.u32GYSum;
        pPixInfo->u32BCbSum = PinxelInfo.u32BCbSum;
        pPixInfo->bShowRepWin = PinxelInfo.bShowRepWin;
        switch(PinxelInfo.enStage)
        {
            case GET_PIXEL_STAGE_AFTER_DLC:
                pPixInfo->enStage = PIXEL_STAGE_AFTER_DLC;
                break;
            case GET_PIXEL_STAGE_PRE_GAMMA:
                pPixInfo->enStage = PIXEL_STAGE_PRE_GAMMA;
                break;
            case GET_PIXEL_STAGE_AFTER_OSD:
                pPixInfo->enStage = PIXEL_STAGE_AFTER_OSD;
                break;
            case GET_PIXEL_STAGE_MAX:
                pPixInfo->enStage = PIXEL_STAGE_MAX;
                break;
            default:
                break;
        }
    }

    TVOS_API_ESTIMATE_END()

    return bRet;
}

bool PictureManagerService::Client::setSwingLevel(int16_t swingLevel)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_BOOL_INT("setSwingLevel","swingLevel",swingLevel);
    #endif
    ALOGV("PictureManagerService::Client::setSwingLevel\n");

    bool ret = MSrv_Control::GetMSrvPicture()->SetSwingLevel(swingLevel);
    TVOS_API_ESTIMATE_END()
    return ret;
}

int16_t PictureManagerService::Client::getDlcHistogramMax()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_INT_NULL("getDlcHistogramMax");
    #endif
    ALOGV("PictureManagerService::Client::getDlcHistogramMax\n");

    int16_t ret = MSrv_Control::GetMSrvPicture()->GetDLCHistogramMax();
    TVOS_API_ESTIMATE_END()
    return ret;
}

int16_t PictureManagerService::Client::getDlcHistogramMin()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_INT_NULL("getDlcHistogramMin");
    #endif
    ALOGV("PictureManagerService::Client::getDlcHistogramMin\n");

    int16_t ret = MSrv_Control::GetMSrvPicture()->GetDLCHistogramMin();
    TVOS_API_ESTIMATE_END()
    return ret;
}

bool PictureManagerService::Client::forceFreerun(bool bEnable,bool b3D)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_BOOL_BOOL2("forceFreerun","bEnable",bEnable,"b3D",b3D);
    #endif
    ALOGV("PictureManagerService::Client::forceFreerun\n");

    bool ret = MSrv_Control::GetMSrvVideo()->ForceFreerun(bEnable, b3D);
    TVOS_API_ESTIMATE_END()
    return ret;
}

bool PictureManagerService::Client::setLocalDimmingMode(int16_t localDimingMode)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_BOOL_INT("setLocalDimmingMode","localDimingMode",localDimingMode);
    #endif
    ALOGV("PictureManagerService::Client::setLocalDimmingMode\n");

    bool ret = MSrv_Control::GetMSrvPicture()->SetLocalDimmingMode((EN_MAPI_LD_Mode)localDimingMode);
    TVOS_API_ESTIMATE_END()
    return ret;
}

bool PictureManagerService::Client::setLocalDimmingBrightLevel(int16_t localDimingBrightLevel)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_BOOL_INT("setLocalDimmingBrightLevel","localDimingBrightLevel",localDimingBrightLevel);
    #endif
    ALOGV("PictureManagerService::Client::setLocalDimmingBrightLevel\n");

    bool ret = MSrv_Control::GetMSrvPicture()->LocalDimmingBrightLevel(localDimingBrightLevel);
    TVOS_API_ESTIMATE_END()
    return ret;
}

bool PictureManagerService::Client::turnOffLocalDimmingBacklight(bool bTurnOffLDBL)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_BOOL_INT("turnOffLocalDimmingBacklight","bTurnOffLDBL",bTurnOffLDBL);
    #endif
    ALOGV("PictureManagerService::Client::turnOffLocalDimmingBacklight\n");

    bool ret = MSrv_Control::GetMSrvPicture()->TurnoffLocalDimmingBacklight(bTurnOffLDBL);
    TVOS_API_ESTIMATE_END()
    return ret;
}

bool PictureManagerService::Client::enter4K2KMode(bool bEnable)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_BOOL_INT("enter4K2KMode","bEnable",bEnable);
    #endif
    ALOGV("PictureManagerService::Client::enter4K2KMode\n");

    //PIP, POP, dual-view and traveling mode are not support under 4k2k MM
    //Diable PIP/POP/dual view
    disableAllDualWinMode();
    bool ret = MSrv_Control::GetMSrvVideo()->Enter4K2KMode(bEnable);
    TVOS_API_ESTIMATE_END()
    return ret;
}

void PictureManagerService::Client::lock4K2KMode(bool bLock )
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    #endif
    ALOGV("PictureManagerService::Client::lock4K2KMode\n");
    #if (ENABLE_4K2K_NIKEU == 1)
    MSrv_Control::GetMSrvVideo()->lock4K2KMode(bLock);
    #endif
    TVOS_API_ESTIMATE_END()
}
int32_t PictureManagerService::Client::get4K2KMode()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    #endif
    ALOGV("PictureManagerService::Client::get4K2KMode\n");
    int32_t ret = -1;
    #if (ENABLE_4K2K_NIKEU == 1)
    ret = (int32_t)(MSrv_Control::GetMSrvVideo()->get4K2KMode());
    #endif
    TVOS_API_ESTIMATE_END()
    return ret;
}
bool PictureManagerService::Client::set4K2KMode(int32_t enOutPutTimming,int32_t enUrsaMode)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    printf("set4K2KMode, enOutPutTimming %d,enUrsaMode %d",enOutPutTimming,enUrsaMode);
    #endif
    ALOGV("PictureManagerService::Client::Set4K2KMode\n");
    bool ret = FALSE;
    #if (ENABLE_4K2K_NIKEU == 1)
    ret = MSrv_Control::GetMSrvVideo()->Set4K2KMode((EN_VIDEO_OUTPUT_TIMING)enOutPutTimming , (EN_URSA_4K2K_MODE)enUrsaMode);
    #endif
    TVOS_API_ESTIMATE_END()
    return ret;
}
bool PictureManagerService::Client::is3DTVPlugedIn()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("PictureManagerService::Client::is3DTVPlugedIn\n");

    bool ret = false;

#if (HDMITX_ENABLE == 1) && (ENABLE_BACKEND == 1)
    ret = true; // separated tv case is always return true;
#elif (HDMITX_ENABLE == 1)
    EN_MAPI_HDMITX_EDID_3D_STRUCTURE_ALL_TYPE edid_3d_type = E_MAPI_HDMITX_EDID_3D_NONE;
    EN_MAPI_HDMITX_TIMING_TYPE enTiming = E_MAPI_HDMITX_TIMING_1080_24P;
    if(MAPI_TRUE == mapi_interface::Get_mapi_hdmitx()->GetEDID_3D_Inform(enTiming, &edid_3d_type))
    {
        if( edid_3d_type & E_MAPI_HDMITX_EDID_3D_FramePacking)
        {
            ret = true;
        }
        else
        {
           ret = false;
        }
    }
#else
#if (STB_ENABLE == 1)
    mapi_display_datatype::EN_MAPI_HDMITX_EDID_3D_STRUCTURE_ALL_TYPE edid_3d_type;
    mapi_display_datatype::EN_DISPLAY_RES_TYPE enTiming = mapi_display_datatype::DISPLAY_DACOUT_1080P_24;
    if(MAPI_TRUE == mapi_interface::Get_mapi_display()->GetEDID_3D_Inform(enTiming, &edid_3d_type))
    {
        if( edid_3d_type & mapi_display_datatype::E_MAPI_HDMITX_EDID_3D_FramePacking)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
#endif
#endif
    TVOS_API_ESTIMATE_END()
    return ret;
}
bool PictureManagerService::Client::is4K2KMode(bool bEnable)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("PictureManagerService::Client::is4K2KMode\n");

    bool ret = false;

#if (ENABLE_4K2K_NIKEU == 1)
    EN_URSA_4K2K_MODE current4k2kMode = MSrv_Control::GetMSrvVideo()->get4K2KMode();
    if((current4k2kMode == E_URSA_4K2K_MODE_4K2K)
        || (current4k2kMode == E_URSA_4K2K_MODE_HDMI4K2K)
        || (current4k2kMode == E_URSA_4K2K_MODE_PHOTO4K2K)
        || (current4k2kMode == E_URSA_4K2K_MODE_4K1K))
    {
        ret = true;
    }
#endif
    TVOS_API_ESTIMATE_END()
    return ret;
}

bool PictureManagerService::Client::disableAllDualWinMode()
{
    TVOS_API_ESTIMATE_START()
    bool ret = true;

    //Check weather the PIP/POP is supported when source resolution is 4k2k.
    //if 4k2k pip is supported, no need to disable it.
    if(TRUE == MSrv_Control::GetInstance()->IsSupportedFeature(E_MSRV_SUPPORTED_FEATURE_4K2K_PIP, NULL))
    {
        TVOS_API_ESTIMATE_END()
        return ret;
    }

#if (PIP_ENABLE == 1)
    //PIP, POP, dual-view and traveling mode are not support under 4k2k MM
    //Diable PIP/POP/dual view

#if (STEREO_3D_ENABLE == 1)
    if(MSrv_Control::GetMSrv3DManager()->GetCurrent3DFormat() == EN_3D_DUALVIEW)
    {
        ret &= MSrv_Control::GetMSrv3DManager()->Disable3DDualView();
        m_PictureManagerService->PostEvent(EV_4K2K_DISABLE_DUALVIEW,0 , 0,true);
    }
#endif
    if(MSrv_Control::GetInstance()->GetPipMode() == E_PIP_MODE_PIP)
    {
        ret &= MSrv_Control::GetInstance()->DisablePip();
        m_PictureManagerService->PostEvent(EV_4K2K_DISABLE_PIP, 0, 0,true);
    }
    if(MSrv_Control::GetInstance()->GetPipMode() == E_PIP_MODE_POP)
    {
        ret &= MSrv_Control::GetInstance()->DisablePop();
        m_PictureManagerService->PostEvent(EV_4K2K_DISABLE_POP, 0, 0,true);
    }
#endif//pip

#if (TRAVELING_ENABLE == 1)
    EN_TRAVELING_RETURN eRet = E_TRAVELING_SUCCESS;
    ST_TRAVELING_CALLBACK_EVENT_INFO stEventInfo;
    memset((void*)&stEventInfo, 0, sizeof(ST_TRAVELING_CALLBACK_EVENT_INFO));
    stEventInfo.enMsgType = E_TRAVELING_EVENT_CALLBACK_FAST_QUIT; //Notify APP to quit traveling mode
    stEventInfo.u16TravelEventInfo_Length = sizeof(ST_TRAVELING_CALLBACK_EVENT_INFO);
    stEventInfo.u32TravelEventInfo_Version = TRAVELING_EVENT_INFO_MSDK_VERSION;

    int TravelingEngineType = 0;

    for (TravelingEngineType = 0; TravelingEngineType < E_TRAVELING_ENGINE_TYPE_MAX; TravelingEngineType++)
    {
        //Disable traveling mode
        if(MSrv_Control::GetInstance()->IsTravelingModeEnable((EN_TRAVELING_ENGINE_TYPE)TravelingEngineType))
        {
            MSrv_Control::GetInstance()->MSrvTravelingEventCallback((void*)&stEventInfo, (EN_TRAVELING_ENGINE_TYPE)TravelingEngineType);
            eRet = MSrv_Control::GetInstance()->FinalizeTravelingMode((EN_TRAVELING_ENGINE_TYPE)TravelingEngineType);

            if (eRet == E_TRAVELING_SUCCESS)
            {
                ret &= true;
            }
            else
            {
                ret &= false;
            }
            m_PictureManagerService->PostEvent(EV_4K2K_DISABLE_TRAVELINGMODE, 0, 0,true);
        }
    }
#endif//Traveling

    TVOS_API_ESTIMATE_END()

    return ret;
}

bool PictureManagerService::Client::setUltraClear(bool bEnable)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_BOOL_INT("setUltraClear","bEnable",bEnable);
    #endif
    ALOGV("PictureManagerService::Client::setUltraClear\n");

    bool ret = MSrv_Control::GetMSrvPicture()->SetUltraClear(bEnable);
    TVOS_API_ESTIMATE_END()
    return ret;
}

void PictureManagerService::Client::setResolution(int8_t resolution)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("PictureManagerService::Client::setResolution\n");
    MSrv_Control::GetInstance()->SetOutputTiming(ConvertDisplayResToTimingEnum(resolution));
    TVOS_API_ESTIMATE_END()
}


int8_t PictureManagerService::Client::getResolution()
{

    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    int8_t ret = MSrv_Control::GetMSrvVideo()->GetResolution();
    TVOS_API_ESTIMATE_END()
    return ret;
}

void PictureManagerService::Client::setReproduceRate(int32_t rate)
{

    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
   // mapi_display* pdis = mapi_interface::Get_mapi_display();
   // pdis->SetGlobalBlankArea((int8_t)(rate>>24),(int8_t)(rate>>16),(int8_t)(rate>>8),(int8_t)rate);
    MSrv_Control::GetMSrvVideo()->SetReproduceRate(rate);
    TVOS_API_ESTIMATE_END()
}

int32_t PictureManagerService::Client::getReproduceRate()
{

    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    int32_t ret = MSrv_Control::GetMSrvVideo()->GetReproduceRate();
    TVOS_API_ESTIMATE_END()
    return ret;
}

void PictureManagerService::Client::EnableVideoOut(bool bEnable)
{
    ALOGV("PictureManagerService::Client::EnableVideoOut\n");
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
#if (STB_ENABLE == 1)
    MSrv_Control::GetMSrvVideo()->EnableVideoOut(bEnable);
#endif
    TVOS_API_ESTIMATE_END()
}

bool PictureManagerService::Client::setHdmiPc(bool bEn)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    bool ret = MSrv_Control::GetMSrvPicture()->SetHdmiPc(bEn);
    TVOS_API_ESTIMATE_END()
    return ret;
}
bool PictureManagerService::Client::getHdmiPc()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    bool ret = MSrv_Control::GetMSrvPicture()->GetHdmiPc();
    TVOS_API_ESTIMATE_END()
    return ret;
}

int32_t PictureManagerService::Client::getHDMIColorFormat()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("PictureManagerService::Client::getHDMIColorFormat\n");
    int32_t ret = (int32_t)(MSrv_Control::GetMSrvPicture()->GetHDMIColorFormat());
    TVOS_API_ESTIMATE_END()
    return ret;
}


bool PictureManagerService::Client::setHLinearScaling(bool bEnable, bool bSign, uint16_t u16Delta)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("PictureManagerService::Client::setHLinearScaling\n");
    bool ret = false;
#if (H_LINEAR_SCALING_ENABLE == 1)
    ret = MSrv_Control::GetMSrvVideo()->SetHLinearScaling(bEnable, bSign,u16Delta);
#endif
    TVOS_API_ESTIMATE_END()
    return ret;
}

bool PictureManagerService::Client::setMEMCMode(String8 cmd)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("PictureManagerService::Client::setMEMCMode\n");

    PictureManagerService_Customer::setMEMCMode_Customer(cmd);
    TVOS_API_ESTIMATE_END()
    return true;

}

void PictureManagerService::Client::setScalerGammaByIndex(int8_t u8Index)
{

    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("PictureManagerService::Client::setScalerGammaByIndex\n");
    MSrv_Control::GetMSrvPicture()->SetScalerGammaByIndex(u8Index);
    TVOS_API_ESTIMATE_END()
}

//no use, enableXvyccCompensation will be replaced by setxvYCCEnable
bool PictureManagerService::Client::enableXvyccCompensation(bool bEnable, int32_t eWin)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("PictureManagerService::Client::enableXvyccCompensation\n");
    //mapi_pql::GetInstance(static_cast<MAPI_PQ_WIN>(eWin))->EnablexvYCCCompensation(bEnable);
    TVOS_API_ESTIMATE_END()
    return true;
}

bool PictureManagerService::Client::keepScalerOutput4k2k(bool bEnable)
{
    ALOGV("PictureManagerService::Client::keepScalerOutput4k2k");
    if (bEnable == true)
    {
        return setOsdResolution(3840, 2160);
    }
    else
    {
        return setOsdResolution(1920, 1080);
    }
}

bool PictureManagerService::Client::setOsdResolution(int32_t width, int32_t height)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    ALOGV("PictureManagerService::Client::setOsdResolution\n");
    TVOS_API_ESTIMATE_END(LOCK)

#if (PLATFORM_TYPE == MSTAR_ANDROID)
    char value[8] = {};
    //On MStar Board , this block will avoid to changing Timing during Boot-up .
    property_get("service.bootanim.exit", value, NULL);
    if (!strcmp(value,"0"))
    {
        ALOGD("%s,%s,%d :  CAN NOT setOsdResolution until BOOTANIM is over\n", __FILE__, __FUNCTION__, __LINE__);
        TVOS_API_ESTIMATE_END()
        return false;
    }
#endif
    MAPI_U16 u16Ret = ERROR_VIDEO_FAIL;
    if ((width == 3840) && (height == 2160))
    {
        u16Ret = MSrv_Control::GetMSrvVideo()->SetOutputTiming(E_TIMING_DEFAULT, E_TIMING_4K2K);
    }
    else
    {
        u16Ret = MSrv_Control::GetMSrvVideo()->SetOutputTiming(E_TIMING_DEFAULT, E_TIMING_2K1K);
    }

    if ((u16Ret == ERROR_VIDEO_SUCCESS) || (u16Ret == ERROR_VIDEO_TIMING_NO_CHANGE))
    {
        TVOS_API_ESTIMATE_END()
        return true;
    }

    TVOS_API_ESTIMATE_END()
    return false;
}

bool PictureManagerService::Client::lockOutputTiming(int32_t eOutputTiming)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    ALOGV("PictureManagerService::Client::lockOutputTiming\n");
    TVOS_API_ESTIMATE_END(LOCK)

    if (MSrv_Control::GetMSrvVideo()->SetOutputTiming((EN_TIMING)eOutputTiming, MAPI_TRUE) != ERROR_VIDEO_SUCCESS)
    {
        TVOS_API_ESTIMATE_END()
        return false;
    }

    TVOS_API_ESTIMATE_END()
    return true;
}

bool PictureManagerService::Client::unlockOutputTiming()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    ALOGV("PictureManagerService::Client::unlockOutputTiming\n");
    TVOS_API_ESTIMATE_END(LOCK)

    if (MSrv_Control::GetMSrvVideo()->LockOutputTiming(MAPI_FALSE) != ERROR_VIDEO_SUCCESS)
    {
        TVOS_API_ESTIMATE_END()
        return false;
    }

    TVOS_API_ESTIMATE_END()
    return true;
}


//--------------------------------------------------------------------------------------
bool PictureManagerService::Client::panelInitial(String8 panelIniName)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("PictureManagerService::Client::panelInitial\n");
    //implement here
    bool ret = MSrv_Control::GetMSrvPicture()->PanelInitial((char *)panelIniName.string());
    TVOS_API_ESTIMATE_END()

    return ret;
}

bool PictureManagerService::Client::setGammaParameter(int32_t index, int32_t value)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("PictureManagerService::Client::setGammaParameter\n");
    bool ret = MSrv_Control::GetMSrvPicture()->SetGammaParameter(index, value);
    TVOS_API_ESTIMATE_END()
    return ret;
}

bool PictureManagerService::Client::calGammaTable(GAMMA_TABLE *rgbData, int32_t MapMode)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("PictureManagerService::Client::calGammaTable\n");
    bool ret = MSrv_Control::GetMSrvPicture()->CalGammaTable((GAMMA_TABLE_t*)rgbData, MapMode);
    TVOS_API_ESTIMATE_END()
    return ret;
}

bool PictureManagerService::Client::setScalerGammaTable (GAMMA_TABLE *gammaTable)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("PictureManagerService::Client::setScalerGammaTable\n");
    bool ret = MSrv_Control::GetMSrvPicture()->SetScalerGammaTable((GAMMA_TABLE_t*)gammaTable);
    TVOS_API_ESTIMATE_END()
    return ret;
}

uint8_t PictureManagerService::Client::getScalerMotion()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("PictureManagerService::Client::getScalerMotion\n");
    uint8_t ret = MSrv_Control::GetMSrvPicture()->GetScalerMotion();
    TVOS_API_ESTIMATE_END()
    return ret;
}

bool PictureManagerService::Client::setxvYCCEnable(bool bEnable, int8_t u8xvYCCmode)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("PictureManagerService::Client::set_xvYCC_Enable\n");
    bool ret = false;
#if (XVYCC_ENABLE == 1)
    MSrv_Control::GetMSrvPicture()->Set_xvYCC_Enable(bEnable, (MSrv_Picture::EN_MS_XVYCC_MODE)u8xvYCCmode);
    ret = true;
#endif
    TVOS_API_ESTIMATE_END()
    return ret;
}

int16_t PictureManagerService::Client::GetSupportedTimingList(St_Timing_Info *pTimingInfoList, int16_t u16ListSize)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("PictureManagerService::Client::GetSupportedTimingList\n");
    int16_t ret = 0;

    ret = MSrv_Control::GetMSrvVideo()->GetSupportedTimingList((ST_TIMING_INFO *)pTimingInfoList,u16ListSize);

    TVOS_API_ESTIMATE_END()
    return ret;
}

int16_t PictureManagerService::Client::GetSupportedTimingListCount()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("PictureManagerService::Client::GetSupportedTimingList\n");
    int16_t ret = 0;

    ret = MSrv_Control::GetMSrvVideo()->GetSupportedTimingList(NULL,0);

    TVOS_API_ESTIMATE_END()
    return ret;
}

uint16_t PictureManagerService::Client::getCurrentTimingId()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("PictureManagerService::Client::getCurrentTimingId\n");

    EN_TIMING enTiming = MSrv_Control::GetMSrvVideo()->GetSocOutputTiming();
    uint16_t ret = ConvertTimingEnumToDisplayRes(enTiming);

    TVOS_API_ESTIMATE_END()
    return ret;
}

bool PictureManagerService::Client::getHdrLevel(E_HDR_LEVEL *pLevel, int32_t eWin)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("PictureManagerService::Client::getHdrLevel\n");

    (*pLevel) = (E_HDR_LEVEL)MSrv_Control::GetMSrvVideo()->GetHdrLevel((MAPI_SCALER_WIN)eWin);

    TVOS_API_ESTIMATE_END()
    return true;
}

bool PictureManagerService::Client::setHdrLevel(E_HDR_LEVEL enlevel, int32_t eWin)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("PictureManagerService::Client::setHdrLevel\n");

    MSrv_Control::GetMSrvVideo()->SetHdrLevel((E_MAPI_XC_HDR_LEVEL)enlevel, (MAPI_SCALER_WIN)eWin);

    MS_USER_SYSTEM_SETTING stUserSystemSetting;
    MSrv_Control::GetMSrvSystemDatabase()->GetUserSystemSetting(&stUserSystemSetting);

    MS_USER_SYSTEM_SETTING stOrgUserSystemSetting = stUserSystemSetting;
    if (eWin == 1)
    {
        stUserSystemSetting.bSubAutoDetectHdrLevel= FALSE;
        stUserSystemSetting.u8SubHdrLevel = (U8)enlevel;
    }
    else
    {
        stUserSystemSetting.bMainAutoDetectHdrLevel = FALSE;
        stUserSystemSetting.u8MainHdrLevel = (U8)enlevel;
    }

    MSrv_Control::GetMSrvSystemDatabase()->SetUserSystemSetting(&stUserSystemSetting, &stOrgUserSystemSetting);

    TVOS_API_ESTIMATE_END()
    return true;
}

bool PictureManagerService::Client::getAutoDetectHdrLevel(int32_t eWin)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("PictureManagerService::Client::getAutoDetectHdrLevel\n");

    bool bRet = MSrv_Control::GetMSrvVideo()->GetAutoDetectHdrLevel((MAPI_SCALER_WIN)eWin);

    TVOS_API_ESTIMATE_END()
    return bRet;
}

bool PictureManagerService::Client::setAutoDetectHdrLevel(bool bAuto, int32_t eWin)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("PictureManagerService::Client::setAutoDetectHdrLevel\n");

    bool bRet = MSrv_Control::GetMSrvVideo()->SetAutoDetectHdrLevel(bAuto, (MAPI_SCALER_WIN)eWin);

    MS_USER_SYSTEM_SETTING stUserSystemSetting;
    MSrv_Control::GetMSrvSystemDatabase()->GetUserSystemSetting(&stUserSystemSetting);

    MS_USER_SYSTEM_SETTING stOrgUserSystemSetting = stUserSystemSetting;
    if (eWin == 1)
    {
        stUserSystemSetting.bSubAutoDetectHdrLevel = bAuto;
    }
    else
    {
        stUserSystemSetting.bMainAutoDetectHdrLevel = bAuto;
    }

    MSrv_Control::GetMSrvSystemDatabase()->SetUserSystemSetting(&stUserSystemSetting, &stOrgUserSystemSetting);

    TVOS_API_ESTIMATE_END()
    return bRet;
}

bool PictureManagerService::Client::IsHdrEnable(int32_t eWin)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("PictureManagerService::Client::setAutoDetectHdrLevel\n");

    bool bRet = MSrv_Control::GetMSrvVideo()->IsHdrEnable((MAPI_SCALER_WIN)eWin);

    TVOS_API_ESTIMATE_END()
    return bRet;
}

uint16_t PictureManagerService::Client::getPQDelayTime()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("PictureManagerService::Client::getPQDelayTime\n");

    uint16_t ret = MSrv_Control::GetMSrvPicture()->GetPQDelayTime();

    TVOS_API_ESTIMATE_END()
    return ret;
}

// EosTek Patch Begin
//ashton: for wb adjust
void PictureManagerService::Client::asGetWbAdjustStar()
{ 
       ALOGV("PictureManagerService::Client::asGetWbAdjustStar\n"); 
     MSrv_UartDebug::GetInstance()->Start(UARTDBG_MODE_CUS); 
     return;
}

void PictureManagerService::Client::asGetWbAdjustExit()
{ 
     ALOGV("PictureManagerService::Client::asGetWbAdjustExit\n"); 
     MSrv_UartDebug::GetInstance()->Exit(); 
     return;
}

// EosTek Patch End

//--------------------------------------------------------------------------------------
bool PictureManagerService::PostEventToClient(U32 nEvt, U32 wParam, U32 lParam)
{
    TVOS_API_ESTIMATE_START()
    bool bEvtHandled = false;

    switch (nEvt)
    {
        case EV_SET_ASPECTRATIO:
        {
            __GETCLIENTINIT(m_Clients, mCurrentClient)
            mCurrentClient->m_PictureManagerClient->PostEvent_SetAspectratio(wParam, lParam);
             bEvtHandled = true;
            __GETCLIENTEND(m_Clients, m_vWaitingRemoveClients)
            break;
        }
        case EV_4K2K_DISABLE_PIP:
        {
            __GETCLIENTINIT(m_Clients, mCurrentClient)
            mCurrentClient->m_PictureManagerClient->PostEvent_4K2KPhotoDisablePip(wParam, lParam);
             bEvtHandled = true;
            __GETCLIENTEND(m_Clients, m_vWaitingRemoveClients)
            break;
        }
        case EV_4K2K_DISABLE_POP:
        {
            __GETCLIENTINIT(m_Clients, mCurrentClient)
            mCurrentClient->m_PictureManagerClient->PostEvent_4K2KPhotoDisablePop(wParam, lParam);
             bEvtHandled = true;
            __GETCLIENTEND(m_Clients, m_vWaitingRemoveClients)
        break;
        }
        case EV_4K2K_DISABLE_DUALVIEW:
        {
            __GETCLIENTINIT(m_Clients, mCurrentClient)
            mCurrentClient->m_PictureManagerClient->PostEvent_4K2KPhotoDisableDualview(wParam, lParam);
            bEvtHandled = true;
            __GETCLIENTEND(m_Clients, m_vWaitingRemoveClients)
            break;
        }
        case EV_4K2K_DISABLE_TRAVELINGMODE:
        {
            __GETCLIENTINIT(m_Clients, mCurrentClient)
            mCurrentClient->m_PictureManagerClient->PostEvent_4K2KPhotoDisableTravelingmode(wParam, lParam);
             bEvtHandled = true;
            __GETCLIENTEND(m_Clients, m_vWaitingRemoveClients)
            break;
        }
        case EV_4K2K_STRETCH_WINDOW:
        {
            ALOGD("EV_4K2K_STRETCH_WINDOW\n");
            MSrv_Picture::T_MS_STRETCH_WINDOW_INFO *stInfo = (MSrv_Picture::T_MS_STRETCH_WINDOW_INFO *)wParam;
            const int SET_GOP_STRETCH_WIN = IBinder::FIRST_CALL_TRANSACTION + 17;
            sp<IBinder> surfaceFlinger = (defaultServiceManager()->getService((android::String16)("SurfaceFlinger")));
            if (surfaceFlinger!=0)
            {
                Parcel data,reply;
                ALOGV("gopNo=%d,srcWidth=%d,srcHeight=%d,dest_Width=%d,dest_Height=%d\n",
                                (int)stInfo->nGopNo,
                                (int)stInfo->nSrcWidth,
                                (int)stInfo->nSrcHeight,
                                (int)stInfo->nDestWidth,
                                (int)stInfo->nDestHeight);
                data.writeInterfaceToken((android::String16)("android.ui.ISurfaceComposer"));
                data.writeInt32(stInfo->nGopNo);
                data.writeInt32(stInfo->nDestWidth);
                data.writeInt32(stInfo->nDestHeight);
                surfaceFlinger->transact(SET_GOP_STRETCH_WIN,data, &reply,0);
                TVOS_API_ESTIMATE_END()
                return (bool)(reply.readInt32());
            }
            else
            {
                ALOGE("can not get SurfaceFlinger\n");
                TVOS_API_ESTIMATE_END()
                return false;
            }
        } break;
#if (PLATFORM_TYPE == MSTAR_ANDROID)
        case EV_SET_SYS_PROPERTY_ENABLE_HWCURSOR:
        {
            ALOGE("EV_SET_SYS_PROPERTY_ENABLE_HWCURSOR\n");
            property_set("mstar.desk-enable-hwcursor", (wParam==0)?"0":"1");
             bEvtHandled = true;
             break;
        }
#else
        case EV_SET_SYS_PROPERTY_ENABLE_HWCURSOR:
        {
            ALOGE("\n\033[0;31m EV_SET_SYS_PROPERTY_ENABLE_HWCURSOR not implement %s %d \033[0m \n",__FUNCTION__,__LINE__);
        } break;
#endif
        default:
        {
            __GETCLIENTINIT(m_Clients, mCurrentClient)
            mCurrentClient->m_PictureManagerClient->PostEvent_Template(nEvt, wParam, lParam);
             bEvtHandled = true;
            __GETCLIENTEND(m_Clients, m_vWaitingRemoveClients)
             break;
        }
    }
    TVOS_API_ESTIMATE_END()
    return bEvtHandled;

}

bool PictureManagerService::PostEvent(uint32_t nEvt, uint32_t wParam, uint32_t lParam, bool synchronous)
{
    TVOS_API_ESTIMATE_START()
    int flag = EVENT_FLAG_NONE;
    U32 toggleevt = NO_TOGGLE_EVT;
    PostEventToEM(nEvt, wParam, lParam,flag,toggleevt,synchronous);

    TVOS_API_ESTIMATE_END()

    return false;
}

// ----------------------------------------------------------------------------
// PictureManager
// ----------------------------------------------------------------------------

