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
#define LOG_TAG "FactoryManagerService"
#include <utils/Log.h>

#include <sys/shm.h>
#include <signal.h>
#include <binder/IServiceManager.h>
#include <binder/IPCThreadState.h>
#include "FactoryManagerService.h"
#include "MSrv_Control.h"
#include "MSrv_Factory_Mode.h"
#include "MSrv_Control.h"
#include "MSrv_ATV_Player.h"

#include "mapi_types.h"
#include "MSrv_System_Database.h"

#if(RELEASE_BINDER_TEST == 1)
#include "FactoryManagerTest.h"
#endif

#include "mapi_pql.h"
#include "MSystem.h"
#define __GETCLIENTINIT(clients, x)        \
sp<Client> x;                   \
for(int kk=0; kk<(int)(clients.size()); kk++)    \
{\
    x = clients[kk].promote(); \
    if (x == NULL)  {   \
       ALOGV("factorymanagerserivce  client is null !!!!!\n");  \
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
MSrv_Factory_Mode *FactoryManagerService::m_MSrvFactory_Mode = NULL;

FactoryManagerService* FactoryManagerService::instantiate()
{
    FactoryManagerService* comm = new FactoryManagerService();
    ALOGV("FactoryManagerService instantiate\n");
    defaultServiceManager()->addService(String16("mstar.FactoryManager"), comm);
    m_MSrvFactory_Mode = MSrv_Control::GetMSrvFactoryMode();
    return (comm);
}

// ----------------------------------------------------------------------------

FactoryManagerService::FactoryManagerService()
: m_Users(0)
{
    ALOGV("FactoryManagerService created\n");
}

FactoryManagerService::~FactoryManagerService()
{
    ALOGV("FactoryManagerService destroyed\n");
}

sp<IFactoryManager> FactoryManagerService::connect(const sp<IFactoryManagerClient>& client)
{
    TVOS_API_ESTIMATE_START()
    pid_t callingPid = IPCThreadState::self()->getCallingPid();
    ALOGV("FactoryManagerService::connect(callingPid %d, client %p)\n", callingPid, client->asBinder().get());

    sp<Client> sp_client = new Client(this, client, callingPid);
    wp<Client> wp_client = sp_client;
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_Lock);
    TVOS_API_ESTIMATE_END(LOCK)
    m_Clients.add(wp_client);
    TVOS_API_ESTIMATE_END()
    return sp_client;
}

void FactoryManagerService::removeClient(wp<Client> client)
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

int32_t FactoryManagerService::incUsers()
{
    TVOS_API_ESTIMATE_START()
    int32_t ret = android_atomic_inc(&m_Users);
    TVOS_API_ESTIMATE_END()
    return ret;
}

void FactoryManagerService::decUsers()
{
    TVOS_API_ESTIMATE_START()
    android_atomic_dec(&m_Users);
    TVOS_API_ESTIMATE_END()
}

FactoryManagerService::Client::Client(const sp<FactoryManagerService>& service,
                                const sp<IFactoryManagerClient>& client,
                                pid_t clientPid)
: m_FactoryManagerService(service), m_FactoryManagerClient(client), m_ClientPid(clientPid)
{
    TVOS_API_ESTIMATE_START()
    ALOGV("FactoryManagerService::Client constructor(callingPid %d)\n", clientPid);
    m_ConnId = m_FactoryManagerService->incUsers();
    m_bEnableDebug = false;
    TVOS_API_ESTIMATE_END()
}

FactoryManagerService::Client::~Client()
{
    TVOS_API_ESTIMATE_START()
    ALOGV("FactoryManagerService::Client destructor(callingPid %d)\n", m_ClientPid);
    disconnect();
    TVOS_API_ESTIMATE_END()
}

void FactoryManagerService::Client::disconnect()
{
    TVOS_API_ESTIMATE_START()
    ALOGV("disconnect(callingPid %d, clientPid %d)\n", IPCThreadState::self()->getCallingPid(), m_ClientPid);
    wp<Client> client(this);
    m_FactoryManagerService->removeClient(client);
    m_FactoryManagerService->decUsers();
    m_FactoryManagerClient.clear();
    TVOS_API_ESTIMATE_END()
}

bool FactoryManagerService::Client::setXvyccDataFromPanel(float fRedX, float fRedY,
                                            float fGreenX, float fGreenY,
                                            float fBlueX, float fBlueY,
                                            float fWhiteX, float fWhiteY, int32_t eWin)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::setXvyccDataFromPanel\n");
    mapi_pql::GetInstance(static_cast<MAPI_PQ_WIN>(eWin))->SetxvYCCDataFromPanel(fRedX, fRedY,
    fGreenX, fGreenY, fBlueX, fBlueY, fWhiteX, fWhiteY);
    TVOS_API_ESTIMATE_END()
    return true;
}

//------------------------------------------------------------------------------------
uint8_t FactoryManagerService::Client::getAutoFineGain()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_BOOL_NULL("GetAutoFineGain") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::GetAutoFineGain()");
    uint8_t ret = MSrv_Control::GetMSrvFactoryMode()->GetAutoFineGain();
    TVOS_API_ESTIMATE_END()
    return ret;
}

bool FactoryManagerService::Client::setFixedFineGain(uint8_t fineGain)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_BOOL_NULL("SetFixedFineGain") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::SetFixedFineGain()");
    return MSrv_Control::GetMSrvFactoryMode()->SetFixedFineGain(fineGain);
}


uint8_t FactoryManagerService::Client::getAutoRFGain()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_BOOL_NULL("GetAutoRFGain") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::GetAutoRFGain()");
    uint8_t ret = MSrv_Control::GetMSrvFactoryMode()->GetAutoRFGain();
    TVOS_API_ESTIMATE_END()
    return ret;
}


bool FactoryManagerService::Client::setRFGain(uint8_t rfGain)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_BOOL_NULL("SetRFGain") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::SetRFGain()");
    bool ret = MSrv_Control::GetMSrvFactoryMode()->SetRFGain(rfGain);
    TVOS_API_ESTIMATE_END()
    return ret;
}
//------------------------------------------------------------------------------------

bool FactoryManagerService::PostEventToClient(U32 nEvt, U32 wParam, U32 lParam)
{
    TVOS_API_ESTIMATE_START()
    bool bEvtHandled = false;
    switch (nEvt)
    {
        default:
        {
            __GETCLIENTINIT(m_Clients, mCurrentClient)
            mCurrentClient->m_FactoryManagerClient->PostEvent_Template(wParam, lParam);
            bEvtHandled = true;
            __GETCLIENTEND(m_Clients, m_vWaitingRemoveClients)
        }
    }
    TVOS_API_ESTIMATE_END()
    return bEvtHandled;
}

bool FactoryManagerService::PostEvent(uint32_t nEvt, uint32_t wParam, uint32_t lParam, bool synchronous)
{
    TVOS_API_ESTIMATE_START()
    int flag = EVENT_FLAG_NONE;
    U32 toggleevt = NO_TOGGLE_EVT;
    PostEventToEM(nEvt, wParam, lParam,flag,toggleevt,synchronous);
    TVOS_API_ESTIMATE_END()
    return false;
}

// ----------------------------------------------------------------------------
// FactoryManager
// ----------------------------------------------------------------------------
bool FactoryManagerService::Client::autoAdc()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_BOOL_NULL("autoAdc") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::autoAdc()");
    bool ret = m_MSrvFactory_Mode->AutoADC();
    TVOS_API_ESTIMATE_END()
    return ret;
}

void FactoryManagerService::Client::copySubColorDataToAllSource()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_VOID_NULL("copySubColorDataToAllSource") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::copySubColorDataToAllSource()");
    MSrv_Control::GetMSrvFactoryMode()->CopySubColorDataToAllInput();
    TVOS_API_ESTIMATE_END()

}
void FactoryManagerService::Client::copyWhiteBalanceSettingToAllSource()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_VOID_NULL("copyWhiteBalanceSettingToAllSource") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::copyWhiteBalanceSettingToAllSource()");
    MSrv_Control::GetMSrvFactoryMode()->CopyWhiteBalanceSettingToAllInput();
    TVOS_API_ESTIMATE_END()
}

bool FactoryManagerService::Client::disablePVRRecordAll()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_BOOL_NULL("disablePVRRecordAll") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::disablePVRRecordAll()");
    MSrv_Control::GetMSrvFactoryMode()->SetPVRRecordAll_ONOFF(0);

    TVOS_API_ESTIMATE_END()

    return true;
}

bool FactoryManagerService::Client::disableUart()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_BOOL_NULL("disableUart") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::disableUart()");
    printf("pFactoryManager client->disableUart\n");
    printf("pFactoryManager client->disableUart\n");
    printf("pFactoryManager client->disableUart\n");
    MSrv_Control::GetMSrvFactoryMode()->SetUart_ONOFF(0);
    TVOS_API_ESTIMATE_END()
    return true;
}

bool FactoryManagerService::Client::disableWdt()
{
 #if(RELEASE_BINDER_TEST == 1)
    TEST_BOOL_NULL("disableWdt") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::disableWdt()");
    MSrv_Control::GetMSrvFactoryMode()->SetWDT_ONOFF(0);

    TVOS_API_ESTIMATE_END()

    return true;
}

bool FactoryManagerService::Client::enablePVRRecordAll()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_BOOL_NULL("enablePVRRecordAll") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::enablePVRRecordAll()");
    MSrv_Control::GetMSrvFactoryMode()->SetPVRRecordAll_ONOFF(1);

    TVOS_API_ESTIMATE_END()

    return true;
}

bool FactoryManagerService::Client::enableUart()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_BOOL_NULL("enableUart") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::enableUart()");
    MSrv_Control::GetMSrvFactoryMode()->SetUart_ONOFF(1);
    TVOS_API_ESTIMATE_END()
    return true;
}

bool FactoryManagerService::Client::enableWdt()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_BOOL_NULL("enableWdt") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::enableWdt()");
    MSrv_Control::GetMSrvFactoryMode()->SetWDT_ONOFF(1);
    TVOS_API_ESTIMATE_END()
    return true;
}

void FactoryManagerService::Client::getAdcGainOffset(int enWin, int eAdcIndex,PqlCalibrationData &pstADCGainOffsetOut)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_GETADCGAINOFFSET() ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::getAdcGainOffset()");
    printf("Client::getAdcGainOffset()\n");
    MAPI_PQL_CALIBRATION_DATA ADCGainOffsetOut={0};
    MSrv_Control::GetMSrvFactoryMode()->GetADCGainOffset((MAPI_SCALER_WIN)enWin,(E_ADC_SET_INDEX)eAdcIndex,&ADCGainOffsetOut);
    pstADCGainOffsetOut.blueGain = (int32_t)ADCGainOffsetOut.u16BlueGain;
    pstADCGainOffsetOut.blueOffset =(int32_t)ADCGainOffsetOut.u16BlueOffset;
    pstADCGainOffsetOut.greenGain = (int32_t)ADCGainOffsetOut.u16GreenGain;
    pstADCGainOffsetOut.greenOffset = (int32_t)ADCGainOffsetOut.u16GreenOffset;
    pstADCGainOffsetOut.redGain = (int32_t)ADCGainOffsetOut.u16RedGain;
    pstADCGainOffsetOut.redOffset = (int32_t)ADCGainOffsetOut.u16RedOffset;
    TVOS_API_ESTIMATE_END()
}

void FactoryManagerService::Client::setUartEnv(bool on)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_VOID_BOOL("setUartEnv","on",on) ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::setUartEnv()");
    m_MSrvFactory_Mode->SetUartEnv(on);
    TVOS_API_ESTIMATE_END()

}

bool FactoryManagerService::Client::getUartEnv()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_BOOL_NULL("getUartEnv") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::setUartEnv()");
    bool ret = m_MSrvFactory_Mode->GetUartEnv();
    TVOS_API_ESTIMATE_END()
    return ret;

}


int FactoryManagerService::Client::getDisplayResolution()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_INT_NULL("getDisplayResolution") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::getDisplayResolution()");
    mapi_display_datatype::EN_DISPLAY_RES_TYPE enDisplayRes = (mapi_display_datatype::EN_DISPLAY_RES_TYPE)0;
    MSrv_Control::GetMSrvFactoryMode()->GetDisplayResolution(enDisplayRes);
    TVOS_API_ESTIMATE_END()
    return enDisplayRes;
}

void FactoryManagerService::Client::getPictureModeValue(PictureModeValue &PModeValue)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_GETPICTUREMODEVALUE() ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::getPictureModeValue()");
    U8 brightness;
    U8 contrast;
    U8 hue;
    U8 saturation;
    U8 sharpness;

    MSrv_Control::GetMSrvFactoryMode()->GetPictureModeValue(brightness,contrast,saturation,sharpness,hue);

    PModeValue.brightness=brightness;
    PModeValue.contrast=contrast;
    PModeValue.hue=hue;
    PModeValue.saturation=saturation;
    PModeValue.sharpness=sharpness;
    TVOS_API_ESTIMATE_END()
}

int FactoryManagerService::Client::getQmapCurrentTableIdx(short ipIndex)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_INT_SHORT("getQmapCurrentTableIdx","ipIndex",ipIndex) ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::getQmapCurrentTableIdx()");
    int ret = m_MSrvFactory_Mode->GetQMAPCurrentTableIdx(ipIndex);
    TVOS_API_ESTIMATE_END()
    return ret;
}

String8 FactoryManagerService::Client::getQmapIpName(short ipIndex)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_STRING8_SHORT("getQmapIpName","ipIndex",ipIndex) ;
#endif
    String8 QMAPIPName;
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::getQmapIpName()");
    MString ComboText;
    MSrv_Control::GetMSrvFactoryMode()->GetQMAPIPName(ComboText,ipIndex);
    QMAPIPName.setTo(ComboText.str.c_str(),ComboText.str.size());
    TVOS_API_ESTIMATE_END()
    return QMAPIPName;
}

int FactoryManagerService::Client::getQmapIpNum()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_INT_NULL("getQmapIpNum") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::getQmapIpNum()");
    int ret = m_MSrvFactory_Mode->GetQMAPIPNum();
    TVOS_API_ESTIMATE_END()
    return ret;
}

String8 FactoryManagerService::Client::getQmapTableName(short ipIndex, short tableIndex)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_STRING8_SHORT_SHORT("getQmapTableName","ipIndex",ipIndex,"tableIndex",tableIndex) ;
#endif
    String8 QMAPTableName;
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::getQmapTableName()");
    MString TableName;
    MSrv_Control::GetMSrvFactoryMode()->GetQMAPTableName(TableName,ipIndex,tableIndex);
    QMAPTableName.setTo(TableName.str.c_str(),TableName.str.size());

    TVOS_API_ESTIMATE_END()

    return QMAPTableName;
}

int FactoryManagerService::Client::getQmapTableNum(short ipIndex)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_INT_SHORT("getQmapTableNum","ipIndex",ipIndex) ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::getQmapTableNum()");

    int ret = m_MSrvFactory_Mode->GetQMAPTableNum(ipIndex);
    TVOS_API_ESTIMATE_END()

    return ret;
}

void FactoryManagerService::Client::getWbGainOffset(int eColorTemp,WbGainOffset &GainOffset )
{
 #if(RELEASE_BINDER_TEST == 1)
    TEST_GETWBGAINOFFSET() ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::getWbGainOffset()");
    U8 blueGain;
    U8 blueOffset;
    U8 greenGain;
    U8 greenOffset;
    U8 redGain;
    U8 redOffset;

    MSrv_Control::GetMSrvFactoryMode()->GetWBGainOffset((MSrv_Picture::EN_MS_COLOR_TEMP)eColorTemp,
        &(redGain),   &(greenGain),   &(blueGain),
        &(redOffset), &(greenOffset), &(blueOffset));

    GainOffset.blueGain=blueGain;
    GainOffset.blueOffset=blueOffset;
    GainOffset.greenGain=greenGain;
    GainOffset.greenOffset=greenOffset;
    GainOffset.redGain=redGain;
    GainOffset.redOffset=redOffset;
    TVOS_API_ESTIMATE_END()

}

void FactoryManagerService::Client::getWbGainOffsetEx(int eColorTemp, int enSrcType,WbGainOffsetEx &WbExOut)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_GETWBGAINOFFSETEX() ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::getWbGainOffsetEx()");
    U16 blueGain;
    U16 blueOffset;
    U16 greenGain;
    U16 greenOffset;
    U16 redGain;
    U16 redOffset;

    MSrv_Control::GetMSrvFactoryMode()->GetWBGainOffsetEx((MSrv_Picture::EN_MS_COLOR_TEMP)eColorTemp,
        &(redGain),   &(greenGain),   &(blueGain),
        &(redOffset), &(greenOffset), &(blueOffset)
        ,(MAPI_INPUT_SOURCE_TYPE)enSrcType);

    WbExOut.blueGain=blueGain;
    WbExOut.blueOffset=blueOffset;
    WbExOut.greenGain=greenGain;
    WbExOut.greenOffset=greenOffset;
    WbExOut.redGain=redGain;
    WbExOut.redOffset=redOffset;
    TVOS_API_ESTIMATE_END()
}

bool FactoryManagerService::Client::isAgingModeOn()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_BOOL_NULL("isAgingModeOn") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::isAgingModeOn()");
    bool ret = MSrv_Control::GetMSrvFactoryMode()->GetAgingMode_ONOFF();
    TVOS_API_ESTIMATE_END()
    return ret;

}

bool FactoryManagerService::Client::isPVRRecordAllOn()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_BOOL_NULL("isPVRRecordAllOn") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::isPVRRecordAllOn()");
    bool ret = MSrv_Control::GetInstance()->GetMSrvFactoryMode()->GetPVRRecordAll_ONOFF();
    TVOS_API_ESTIMATE_END()
    return ret;
}

bool FactoryManagerService::Client::isUartOn()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_BOOL_NULL("isUartOn") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::isUartOn()");
    bool ret = MSrv_Control::GetInstance()->GetMSrvFactoryMode()->GetUart_ONOFF();
    TVOS_API_ESTIMATE_END()
    return ret;
}

bool FactoryManagerService::Client::isWdtOn()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_BOOL_NULL("isWdtOn") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::isWdtOn()");
    bool ret = MSrv_Control::GetInstance()->GetMSrvFactoryMode()->GetWDT_ONOFF();
    TVOS_API_ESTIMATE_END()
    return ret;

}

void FactoryManagerService::Client::loadPqTable(int tableIndex, int ipIndex)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_VOID_INI_INT("loadPqTable","tableIndex",tableIndex,"ipIndex",ipIndex) ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::loadPqTable()");
    MSrv_Control::GetMSrvFactoryMode()->PQ_LoadTable(tableIndex,ipIndex);
    TVOS_API_ESTIMATE_END()

}

bool FactoryManagerService::Client::resetDisplayResolution()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_BOOL_NULL("resetDisplayResolution") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::resetDisplayResolution()");
    m_MSrvFactory_Mode->ResetDisplayResolution();
    TVOS_API_ESTIMATE_END()
    return true;

}

bool FactoryManagerService::Client::restoreDbFromUsb()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_BOOL_NULL("restoreDbFromUsb") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::restoreDbFromUsb()");
    m_MSrvFactory_Mode->RestoreDBFromUSB();
    TVOS_API_ESTIMATE_END()
    return true;
}

void FactoryManagerService::Client::setAdcGainOffset(int enWin, int eAdcIndex, PqlCalibrationData stADCGainOffset)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_SETADCGAINOFFSET() ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::setAdcGainOffset()");
    MAPI_PQL_CALIBRATION_DATA tmp_AdcGainOffset;


    tmp_AdcGainOffset.u16RedGain=stADCGainOffset.redGain;
    tmp_AdcGainOffset.u16GreenGain=stADCGainOffset.greenGain;
    tmp_AdcGainOffset.u16BlueGain=stADCGainOffset.blueGain;
    tmp_AdcGainOffset.u16RedOffset=stADCGainOffset.redOffset;
    tmp_AdcGainOffset.u16GreenOffset=stADCGainOffset.greenOffset;
    tmp_AdcGainOffset.u16BlueOffset=stADCGainOffset.blueOffset;

    MSrv_Control::GetMSrvFactoryMode()->SetADCGainOffset((MAPI_SCALER_WIN)enWin, (E_ADC_SET_INDEX)eAdcIndex, tmp_AdcGainOffset);

    TVOS_API_ESTIMATE_END()

}

bool FactoryManagerService::Client::setBrightness(short subBrightness)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_BOOL_SHORT("setBrightness","subBrightness",subBrightness) ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::setBrightness()");
    m_MSrvFactory_Mode->SetBrightness(subBrightness);
    TVOS_API_ESTIMATE_END()
    return true;
}

bool FactoryManagerService::Client::setContrast(short subContrast)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_BOOL_SHORT("setContrast","subContrast",subContrast) ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::setContrast()");
    m_MSrvFactory_Mode->SetContrast(subContrast);
    TVOS_API_ESTIMATE_END()
    return true;
}

void FactoryManagerService::Client::setFactoryVdInitParameter(FactoryNsVdSet factoryNsVdSetVo)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_SETFACTORYVDINITPARAMETER() ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::setFactoryVdInitParameter()");
    MS_Factory_NS_VD_SET VDNSValue;

    VDNSValue.u8AFEC_44 = factoryNsVdSetVo.aFEC_44 ;
    VDNSValue.u8AFEC_D4 = factoryNsVdSetVo.aFEC_D4;
    VDNSValue.u8AFEC_D5_Bit2 = factoryNsVdSetVo.aFEC_D5_Bit2;
    VDNSValue.u8AFEC_D7_LOW_BOUND = factoryNsVdSetVo.aFEC_D7_LOW_BOUND;
    VDNSValue.u8AFEC_D7_HIGH_BOUND = factoryNsVdSetVo.aFEC_D7_HIGH_BOUND;
    VDNSValue.u8AFEC_D8_Bit3210 = factoryNsVdSetVo.aFEC_D8_Bit3210;
    VDNSValue.u8AFEC_D9_Bit0 = factoryNsVdSetVo.aFEC_D9_Bit0;
    VDNSValue.u8AFEC_A0 = factoryNsVdSetVo.aFEC_A0;
    VDNSValue.u8AFEC_A1 = factoryNsVdSetVo.aFEC_A1;
    VDNSValue.u8AFEC_66_Bit76 = factoryNsVdSetVo.aFEC_66_Bit76;
    VDNSValue.u8AFEC_6E_Bit7654 = factoryNsVdSetVo.aFEC_6E_Bit7654;
    VDNSValue.u8AFEC_6E_Bit3210 = factoryNsVdSetVo.aFEC_6E_Bit3210;
#if 1 //  AFEC_43 -->VD auto AGC on /off ;AFEC_44   -> VD AGC gain
    VDNSValue.u8AFEC_43 = factoryNsVdSetVo.aFEC_43;
    VDNSValue.u8AFEC_44 = factoryNsVdSetVo.aFEC_44;
#endif
    VDNSValue.u8AFEC_CB = factoryNsVdSetVo.aFEC_CB;
    VDNSValue.u8AFEC_CF_Bit2_ATV = factoryNsVdSetVo.aFEC_CF_Bit2_ATV;
    VDNSValue.u8AFEC_CF_Bit2_AV = factoryNsVdSetVo.aFEC_CF_Bit2_AV;
    MSrv_Control::GetMSrvFactoryMode()->SetFactoryVDInitParameter(&VDNSValue);
    TVOS_API_ESTIMATE_END()
}

void FactoryManagerService::Client::setFactoryVDParameter(FactoryNsVdSet factoryNsVdSetVo)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_SETFACTORYVDPARAMETER() ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::setFactoryVDParameter()");
    MS_Factory_NS_VD_SET VDNSValue;

    VDNSValue.u8AFEC_44 = factoryNsVdSetVo.aFEC_44 ;
    VDNSValue.u8AFEC_D4 = factoryNsVdSetVo.aFEC_D4;
    VDNSValue.u8AFEC_D5_Bit2 = factoryNsVdSetVo.aFEC_D5_Bit2;
    VDNSValue.u8AFEC_D7_LOW_BOUND = factoryNsVdSetVo.aFEC_D7_LOW_BOUND;
    VDNSValue.u8AFEC_D7_HIGH_BOUND = factoryNsVdSetVo.aFEC_D7_HIGH_BOUND;
    VDNSValue.u8AFEC_D8_Bit3210 = factoryNsVdSetVo.aFEC_D8_Bit3210;
    VDNSValue.u8AFEC_D9_Bit0 = factoryNsVdSetVo.aFEC_D9_Bit0;
    VDNSValue.u8AFEC_A0 = factoryNsVdSetVo.aFEC_A0;
    VDNSValue.u8AFEC_A1 = factoryNsVdSetVo.aFEC_A1;
    VDNSValue.u8AFEC_66_Bit76 = factoryNsVdSetVo.aFEC_66_Bit76;
    VDNSValue.u8AFEC_6E_Bit7654 = factoryNsVdSetVo.aFEC_6E_Bit7654;
    VDNSValue.u8AFEC_6E_Bit3210 = factoryNsVdSetVo.aFEC_6E_Bit3210;
#if 1 //  AFEC_43 -->VD auto AGC on /off ;AFEC_44   -> VD AGC gain
    VDNSValue.u8AFEC_43 = factoryNsVdSetVo.aFEC_43;
    VDNSValue.u8AFEC_44 = factoryNsVdSetVo.aFEC_44;
#endif
    VDNSValue.u8AFEC_CB = factoryNsVdSetVo.aFEC_CB;
    VDNSValue.u8AFEC_CF_Bit2_ATV = factoryNsVdSetVo.aFEC_CF_Bit2_ATV;
    VDNSValue.u8AFEC_CF_Bit2_AV = factoryNsVdSetVo.aFEC_CF_Bit2_AV;
    MSrv_Control::GetMSrvFactoryMode()->SetFactoryVDParameter(&VDNSValue);
    TVOS_API_ESTIMATE_END()

}

bool FactoryManagerService::Client::setHue(short hue)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_BOOL_SHORT("setHue","hue",hue) ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::setHue()");
    m_MSrvFactory_Mode->SetHue(hue);
    TVOS_API_ESTIMATE_END()
    return true;
}

bool FactoryManagerService::Client::setSaturation(short saturation)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_BOOL_SHORT("setSaturation","saturation",saturation) ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::setSaturation()");
    m_MSrvFactory_Mode->SetSaturation(saturation);
    TVOS_API_ESTIMATE_END()
    return true;
}

bool FactoryManagerService::Client::setSharpness(short sharpness)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_BOOL_SHORT("setSharpness","sharpness",sharpness) ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::setSharpness()");
    m_MSrvFactory_Mode->SetSharpness(sharpness);
    TVOS_API_ESTIMATE_END()
    return true;
}

void FactoryManagerService::Client::setVideoTestPattern(int enColor)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_VOID_BOOL("setVideoTestPattern","enColor",enColor) ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::setVideoTestPattern()");
    MSrv_Control::GetMSrvFactoryMode()->SetVideoTestPattern((MSrv_Factory_Mode::Screen_Mute_Color)enColor);
    TVOS_API_ESTIMATE_END()
}

bool FactoryManagerService::Client::setVideoMuteColor(int enColor)
{
    ALOGV("FactoryManagerService::Client::setVideoMuteColor()");
#if(RELEASE_BINDER_TEST == 1)
    TEST_BOOL_INT("setVideoMuteColor","enColor",enColor) ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::setVideoMuteColor(%d)",enColor);
    bool ret = MSrv_Control::GetInstance()->setVideoMuteColor((mapi_video_datatype::MAPI_VIDEO_Screen_Mute_Color)enColor);
    TVOS_API_ESTIMATE_END()
    return ret;
}

void FactoryManagerService::Client::setWbGainOffset(int eColorTemp, short redGain, short greenGain, short blueGain, short redOffset, short greenOffset, short blueOffset)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_VOID_INT7("setWbGainOffset","eColorTemp",eColorTemp,"redGain",redGain,"greenGain",greenGain,"blueGain",blueGain,"redOffset",redOffset,"greenOffset",greenOffset,"blueOffset",blueOffset) ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::setWbGainOffset()");
    MSrv_Control::GetMSrvFactoryMode()->SetWBGainOffset((MSrv_Picture::EN_MS_COLOR_TEMP)eColorTemp, redGain, greenGain, blueGain, redOffset, greenOffset, blueOffset);

    TVOS_API_ESTIMATE_END()

}

void FactoryManagerService::Client::setWbGainOffsetEx(int eColorTemp, int redGain, int greenGain, int blueGain, int redOffset, int greenOffset, int blueOffset, int enSrcType)
{
 #if(RELEASE_BINDER_TEST == 1)
    TEST_VOID_INT8("setWbGainOffset","eColorTemp",eColorTemp,"redGain",redGain,"greenGain",greenGain,"blueGain",blueGain,"redOffset",redOffset,"greenOffset",greenOffset,"blueOffset",blueOffset,"enSrcType",enSrcType) ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::setWbGainOffsetEx()");
    MSrv_Control::GetMSrvFactoryMode()->SetWBGainOffsetEx((MSrv_Picture::EN_MS_COLOR_TEMP)eColorTemp, redGain, greenGain, blueGain, redOffset, greenOffset, blueOffset, (MAPI_INPUT_SOURCE_TYPE)enSrcType);
    TVOS_API_ESTIMATE_END()
}

bool FactoryManagerService::Client::storeDbToUsb()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_BOOL_NULL("storeDbToUsb") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::storeDbToUsb()");
    MSrv_Control::GetMSrvFactoryMode()->BeckupDBToUSB();
    TVOS_API_ESTIMATE_END()
    return TRUE;
}

int32_t FactoryManagerService::Client::getFwVersion(int32_t type)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_INT_SHORT("getFwVersion","type",type) ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::storeDbToUsb()");
    int32_t ret = (int32_t)MSrv_Control::GetMSrvFactoryMode()->GetFwVersion((MSrv_Factory_Mode::SN_FW_TYPE)type);
    TVOS_API_ESTIMATE_END()
    return ret;
}

bool FactoryManagerService::Client::updateSscParameter()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_BOOL_NULL("updateSscParameter") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::updateSscParameter()");
    bool ret = (bool)MSrv_Control::UpdateSSCPara();
    TVOS_API_ESTIMATE_END()
    return ret;
}

void  FactoryManagerService::Client::setDebugMode(bool mode)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    m_bEnableDebug = mode;
    TVOS_API_ESTIMATE_END()
}

void FactoryManagerService::Client::getSoftwareVersion(String8 &version)
{
#if(RELEASE_BINDER_TEST == 1)
TEST_VOID_STRING8_RE("getSoftwareVersion","version",version);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::getSoftwareVersion()");
    MString mstr;
    MSrv_Control::GetMSrvFactoryMode()->GetSWVersion(mstr);
    version.setTo(mstr.str.c_str(), mstr.str.size());
    TVOS_API_ESTIMATE_END()
}

void  FactoryManagerService::Client::stopTvService()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_VOID_NULL("stopTvService") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::stopTvService()\n");
    MSrv_Control::GetMSrvFactoryMode()->SetWDT_ONOFF(0);
    kill(getpid(), SIGKILL);
    TVOS_API_ESTIMATE_END()
}

void FactoryManagerService::Client::restoreFactoryAtvProgramTable(short cityIndex)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_VOID_SHORT("restoreFactoryAtvProgramTable","cityIndex",cityIndex) ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::restoreFactoryAtvProgramTable()");
    #if (MSTAR_TVOS == 1)
    MSrv_Control::GetMSrvFactoryMode()->RestoreFactoryAtvProgramTable(cityIndex);
    #endif
    TVOS_API_ESTIMATE_END()
}

void FactoryManagerService::Client::restoreFactoryDtvProgramTable(short cityIndex)
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_VOID_SHORT("restoreFactoryDtvProgramTable","cityIndex",cityIndex) ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::restoreFactoryDtvProgramTable()");
    #if (MSTAR_TVOS == 1)
    MSrv_Control::GetMSrvFactoryMode()->RestoreFactoryDtvProgramTable(cityIndex);
    #endif
    TVOS_API_ESTIMATE_END()
}

void FactoryManagerService::Client::setPQParameterViaUsbKey()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_VOID_NULL("setPQParameterViaUsbKey") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::setPQParameterViaUsbKey()");
    MSrv_Control::GetMSrvFactoryMode()->SetPQParameterViaUsbKey();
    TVOS_API_ESTIMATE_END()
}

void FactoryManagerService::Client::setHDCPKeyViaUsbKey()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_VOID_NULL("setHDCPKeyViaUsbKey") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::setHDCPKeyViaUsbKey()");
    MSrv_Control::GetMSrvFactoryMode()->SetHDCPKeyViaUsbKey();
    TVOS_API_ESTIMATE_END()
}

void FactoryManagerService::Client::setCIPlusKeyViaUsbKey()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_VOID_NULL("setCIPlusKeyViaUsbKey") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::setCIPlusKeyViaUsbKey()");
    MSrv_Control::GetMSrvFactoryMode()->SetCIPlusKeyViaUsbKey();
    TVOS_API_ESTIMATE_END()
}

void FactoryManagerService::Client::setMACAddrViaUsbKey()
{
#if(RELEASE_BINDER_TEST == 1)
    TEST_VOID_NULL("setMACAddrViaUsbKey") ;
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::setMACAddrViaUsbKey()");
    MSrv_Control::GetMSrvFactoryMode()->SetMACAddrViaUsbKey();
    TVOS_API_ESTIMATE_END()
}

void FactoryManagerService::Client::getMACAddrString(String8 &mac)
{
#if(RELEASE_BINDER_TEST == 1)
TEST_VOID_STRING8_RE("getMACAddrString","mac",mac);
#endif
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::getMACAddrString()");
    string str;
    MSrv_Control::GetMSrvFactoryMode()->GetMACAddrString(str);
    mac.setTo(str.c_str(), str.size());
    TVOS_API_ESTIMATE_END()
}

bool FactoryManagerService::Client::startUartDebug()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_BOOL_NULL("startUartDebug");
    #endif
    ALOGV("FactoryManagerService::Client::startUartDebug\n");

    bool ret = false;
#if (STB_ENABLE == 0)
    ret = MSrv_Control::GetInstance()->StartUartDebug();
#endif
    TVOS_API_ESTIMATE_END()
    return ret;
}

bool FactoryManagerService::Client::uartSwitch()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_BOOL_NULL("uartSwitch");
    #endif
    ALOGV("FactoryManagerService::Client::uartSwitch\n");

    bool ret = false;
#if (STB_ENABLE == 0)
    ret = MSrv_Control::GetInstance()->UartSwitch();
#endif
    TVOS_API_ESTIMATE_END()
    return ret;
}

bool FactoryManagerService::Client::readBytesFromI2C(int32_t u32gID, uint8_t u8AddrSize, uint8_t *pu8Addr, uint16_t u16Size, uint8_t *pu8Data)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_READBYTESFROMI2C();
    #endif
    ALOGV("FactoryManagerService::Client::ReadBytesFromI2C\n");

    bool ret = MSrv_Control::GetInstance()->ReadBytesFromI2C(u32gID, u8AddrSize, pu8Addr, u16Size, pu8Data);
    TVOS_API_ESTIMATE_END()
    return ret;
}

bool FactoryManagerService::Client::writeBytesToI2C(int32_t u32gID, uint8_t u8AddrSize, uint8_t *pu8Addr, uint16_t u16Size, uint8_t *pu8Data)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_WRITEBYTESTOI2C();
    #endif
    ALOGV("FactoryManagerService::Client::WriteBytesToI2C\n");

    bool ret = MSrv_Control::GetInstance()->WriteBytesToI2C(u32gID, u8AddrSize, pu8Addr, u16Size, pu8Data);
    TVOS_API_ESTIMATE_END()
    return ret;
}

int16_t FactoryManagerService::Client::getResolutionMappingIndex(int32_t enCurrentInputType)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_SHORT_INT("getResolutionMappingIndex", "enCurrentInputType", enCurrentInputType);
    #endif
    ALOGV("FactoryManagerService::Client::getResolutionMappingIndex\n");

    U8 ret=0;

    MSrv_Control::GetMSrvFactoryMode()->GetResolutionMapping(&ret, (MAPI_INPUT_SOURCE_TYPE) enCurrentInputType);

    TVOS_API_ESTIMATE_END()

    return ret;
}

bool FactoryManagerService::Client::setEnvironmentPowerMode(int32_t ePowerMode)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_BOOL_INT("setEnvironmentPowerMode", "ePowerMode", ePowerMode);
    #endif
    ALOGV("TvManagerService::Client::setEnvironmentPowerMode\n");

    bool ret = MSrv_Control::GetInstance()->SetEnvPowerMode((EN_ACON_POWERON_MODE)ePowerMode);
    TVOS_API_ESTIMATE_END()
    return ret;
}

int32_t FactoryManagerService::Client::getEnvironmentPowerMode()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_INT_NULL("getEnvironmentPowerMode");
    #endif
    ALOGV("TvManagerService::Client::getEnvironmentPowerMode\n");

    int32_t ret = MSrv_Control::GetInstance()->GetEnvPowerMode();
    TVOS_API_ESTIMATE_END()
    return ret;
}

bool FactoryManagerService::Client::setEnvironmentPowerOnMusicVolume(uint8_t volume)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_BOOL_INT("setEnvironmentPowerOnMusicVolume", "volume", volume);
    #endif
    ALOGV("TvManagerService::Client::setEnvironmentPowerOnMusicVolume\n");

    bool ret = MSrv_Control::GetInstance()->SetEnvPowerOnMusicVolume((U8)volume);
    TVOS_API_ESTIMATE_END()
    return ret;
}


uint8_t FactoryManagerService::Client::getEnvironmentPowerOnMusicVolume()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_INT_NULL("getEnvironmentPowerMode");
    #endif
    ALOGV("TvManagerService::Client::getEnvironmentPowerMode\n");

    uint8_t ret = MSrv_Control::GetInstance()->GetEnvPowerOnMusicVolume();
    TVOS_API_ESTIMATE_END()
    return ret;
}

bool FactoryManagerService::Client::getUpdatePQFilePath(int32_t enumpqfile, String8 &filePath)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    filePath.setTo("test",4);
    TEST_BOOL_INT_STRING8("getUpdatePQFilePath","enumpqfile",enumpqfile,"filePath",filePath);
    #endif
    ALOGV("TvManagerService::Client::getUpdatePQFilePath\n");

    char pFilePath[64] = {0};

    BOOL ret = MSrv_Control::GetInstance()->GetUpdatePQFilePath(pFilePath, (EN_MSRV_PQ_UPDATE_FILE)enumpqfile);

    filePath.setTo(pFilePath, 64);

    TVOS_API_ESTIMATE_END()

    return ret;
}

void FactoryManagerService::Client::updatePQiniFiles()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_VOID_NULL("updatePQiniFiles");
    #endif
    ALOGV("TvManagerService::Client::updatePQiniFiles\n");

    MSrv_Control::GetInstance()->UpdatePQiniFiles();
    TVOS_API_ESTIMATE_END()
}

String8 FactoryManagerService::Client::getPQVersion(int32_t escalerwindow)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    #if(1==RELEASE_BINDER_TEST)
    TEST_STRING8_SHORT("getPQVersion", "escalerwindow", escalerwindow);
    #endif
    ALOGV("FactoryManagerService::Client::getPQVersion\n");

    MString MainWinVersion;
    MString SubWinVersion;

    MSrv_Control::GetMSrvFactoryMode()->GetPQVersion(MainWinVersion, SubWinVersion);

    String8 str;

    if (MAPI_MAIN_WINDOW == escalerwindow)
    {
        str = MainWinVersion.str.c_str();
    }
    else if (MAPI_SUB_WINDOW == escalerwindow)
    {
        str = SubWinVersion.str.c_str();
    }

    TVOS_API_ESTIMATE_END()

    return str;
}

bool FactoryManagerService::Client::UrsaGetVersionInfo(Ursa_Version_Info *pVersionInfo)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)

    ALOGV("FactoryManagerService::Client::UrsaGetVersionInfo\n");

    if(NULL == pVersionInfo)
    {
        return false;
    }
    U16 u16Version = 0xFF;
    U32 u32Changelist = 0xFF;
    BOOL ret = MSrv_Control::GetMSrvFactoryMode()->GetUrsaVersionInfo(&u16Version, &u32Changelist);

    pVersionInfo->u16Version = u16Version;
    pVersionInfo->u32Changelist = u32Changelist;
    TVOS_API_ESTIMATE_END()
    return ret;
}

void FactoryManagerService::Client::setWOLEnableStatus(bool flag)
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::setWOLEnableStatus\n");
#if (WOL_ENABLE == 1)
    MSrv_Control::GetMSrvFactoryMode()->SetWOLEnableStatus(flag);
#endif
    TVOS_API_ESTIMATE_END()
}

bool FactoryManagerService::Client::getWOLEnableStatus()
{
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    ALOGV("FactoryManagerService::Client::getWOLEnableStatus\n");
    bool ret = 0;
#if (WOL_ENABLE == 1)
    ret = MSrv_Control::GetMSrvFactoryMode()->GetWOLEnableStatus();
#endif
    TVOS_API_ESTIMATE_END()
    return ret;
}

void FactoryManagerService::Client::getEnableIPInfo(uint8_t *pBitTable, int32_t sBitTableLen)
{
    ALOGV("FactoryManagerService::Client::getEnableIPInfo\n");
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
#if (ENABLE_LITE_SN != 1)
    MSrv_Control::GetInstance()->GetEnableIPInfo(pBitTable,sBitTableLen);
#endif
    TVOS_API_ESTIMATE_END()
}

bool FactoryManagerService::Client::EosSetHDCPKey(const uint8_t *pu8Key, uint32_t u32Key_len, bool bVer2Flag)
{
    ALOGV("FactoryManagerService::Client::EosSetHDCPKey(const uint8_t *pu8Key, uint32_t u8Key_len)\n");
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    BOOL bRet = MSrv_Control::GetMSrvFactoryMode()->EosSetHDCPKey(pu8Key, u32Key_len, bVer2Flag);
    TVOS_API_ESTIMATE_END()
    return bRet;
}

bool FactoryManagerService::Client::EosGetHDCPKey(uint8_t *pu8Key, uint32_t u32Key_len, bool bVer2Flag)
{
    ALOGV("FactoryManagerService::Client::EosGetHDCPKey(uint8_t *pu8Key, uint32_t u8Key_len)\n");
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    BOOL bRet = MSrv_Control::GetMSrvFactoryMode()->EosGetHDCPKey(pu8Key, u32Key_len, bVer2Flag);
    TVOS_API_ESTIMATE_END()
    return bRet;
}

bool FactoryManagerService::Client::getTunerStatus()
{
    ALOGV("FactoryManagerService::Client::getTunerStatus\n");
    TVOS_API_ESTIMATE_START()
    TVOS_API_ESTIMATE_START(LOCK)
    Mutex::Autolock lock(m_FuncLock);
    TVOS_API_ESTIMATE_END(LOCK)
    BOOL bRet = MSrv_Control::GetMSrvAtv()->TunerStatus();
    TVOS_API_ESTIMATE_END()
    return bRet;

} 

