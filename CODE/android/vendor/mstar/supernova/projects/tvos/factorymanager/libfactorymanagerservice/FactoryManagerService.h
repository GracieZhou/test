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
#ifndef _FACTORYMANAGER_SERVICE_H_
#define _FACTORYMANAGER_SERVICE_H_

#include <utils/threads.h>
#include <utils/SortedVector.h>
#include "IFactoryManagerService.h"
#include "IFactoryManager.h"
#include "TVOS_Common.h"
#include "FactoryManagerType.h"
using namespace android;

class MSrv_Factory_Mode;

class FactoryManagerService : public BnFactoryManagerService  , public TVOS_Service
{
    class Client;

public:
    static FactoryManagerService* instantiate();

    // IFactoryManagerService interface
    virtual sp<IFactoryManager> connect(const sp<IFactoryManagerClient>& client);

    void removeClient(wp<Client> client);

    bool PostEvent(uint32_t nEvt, uint32_t wParam, uint32_t lParam, bool synchronous = false);
    //Post a event to client, Internal use only!!!
    //Only deal with things for PostEvent when synchronous=true
    bool PostEventToClient(U32 nEvt, U32 wParam, U32 lParam);

private:

// ----------------------------------------------------------------------------

    class Client : public BnFactoryManager, public TVOS_Utility
    {
    public:

        virtual void disconnect();

    virtual bool autoAdc();
    virtual void copySubColorDataToAllSource();
    virtual void copyWhiteBalanceSettingToAllSource();
    virtual bool disablePVRRecordAll();
    virtual bool disableUart();
    virtual bool disableWdt();
    virtual bool enablePVRRecordAll();
    virtual bool enableUart();
    virtual bool enableWdt();
    virtual void getAdcGainOffset(int enWin, int eAdcIndex,PqlCalibrationData &pstADCGainOffsetOut);
    virtual void setUartEnv(bool on);
    virtual bool getUartEnv();
    virtual int getDisplayResolution();
    virtual void getPictureModeValue(PictureModeValue &PModeValue);
    virtual int getQmapCurrentTableIdx(short ipIndex);
    virtual String8 getQmapIpName(short ipIndex);
    virtual int getQmapIpNum();
    virtual String8 getQmapTableName(short ipIndex, short tableIndex);
    virtual int getQmapTableNum(short ipIndex);
    virtual void getWbGainOffset(int eColorTemp,WbGainOffset &GainOffset);
    virtual void getWbGainOffsetEx(int eColorTemp, int enSrcType,WbGainOffsetEx &WbExOut);
    virtual bool isAgingModeOn();
    virtual bool isPVRRecordAllOn();
    virtual bool isUartOn() ;
    virtual bool isWdtOn() ;
    virtual void loadPqTable(int tableIndex, int ipIndex) ;
    virtual bool resetDisplayResolution() ;
    virtual bool restoreDbFromUsb();
    virtual void setAdcGainOffset(int enWin, int eAdcIndex, PqlCalibrationData stADCGainOffset) ;
    virtual bool setBrightness(short subBrightness) ;
    virtual bool setContrast(short subContrast);
    virtual void setFactoryVdInitParameter(FactoryNsVdSet factoryNsVdSetVo);
    virtual void setFactoryVDParameter(FactoryNsVdSet factoryNsVdSetVo) ;
    virtual bool setHue(short hue);
    virtual bool setSaturation(short saturation);
    virtual bool setSharpness(short sharpness);
    virtual void setVideoTestPattern(int enColor);
    virtual bool setVideoMuteColor(int enColor);
    virtual void setWbGainOffset(int eColorTemp, short redGain, short greenGain, short blueGain, short redOffset, short greenOffset, short blueOffset);
    virtual void setWbGainOffsetEx(int eColorTemp, int redGain, int greenGain, int blueGain, int redOffset, int greenOffset, int blueOffset, int enSrcType) ;
    virtual bool storeDbToUsb() ;
    virtual int32_t getFwVersion(int32_t type);
    virtual bool updateSscParameter();
    virtual void setDebugMode(bool mode);
    virtual void getSoftwareVersion(String8 &version);
    virtual void stopTvService();
    virtual void restoreFactoryAtvProgramTable(short cityIndex);
    virtual void restoreFactoryDtvProgramTable(short cityIndex);
    virtual void setPQParameterViaUsbKey();
    virtual void setHDCPKeyViaUsbKey();
    virtual void setCIPlusKeyViaUsbKey();
    virtual void setMACAddrViaUsbKey();
    virtual void getMACAddrString(String8 &mac);
    virtual bool startUartDebug();
    virtual bool uartSwitch();
    virtual int16_t getResolutionMappingIndex(int32_t enCurrentInputType);
    virtual bool readBytesFromI2C(int32_t u32gID, uint8_t u8AddrSize, uint8_t *pu8Addr, uint16_t u16Size, uint8_t *pu8Data);
    virtual bool writeBytesToI2C(int32_t u32gID, uint8_t u8AddrSize, uint8_t *pu8Addr, uint16_t u16Size, uint8_t *pu8Data);
    virtual bool setEnvironmentPowerMode(int32_t ePowerMode);
    virtual int32_t getEnvironmentPowerMode();
    virtual bool setEnvironmentPowerOnMusicVolume(uint8_t volume);
    virtual uint8_t getEnvironmentPowerOnMusicVolume();

    virtual bool getUpdatePQFilePath(int32_t enumpqfile, String8 &filePath);
    virtual void updatePQiniFiles();

    virtual String8 getPQVersion(int32_t escalerwindow);

    virtual bool UrsaGetVersionInfo(Ursa_Version_Info *pVersionInfo);
    virtual void setWOLEnableStatus(bool flag);
    virtual bool getWOLEnableStatus();
    virtual bool setXvyccDataFromPanel(float fRedX, float fRedY,
                                        float fGreenX, float fGreenY,
                                        float fBlueX, float fBlueY,
                                        float fWhiteX, float fWhiteY, int32_t eWin);
    virtual void getEnableIPInfo(uint8_t *pBitTable, int32_t sBitTableLen);

//------------------------------------------------------------------------------------
    virtual uint8_t getAutoFineGain();
    virtual bool setFixedFineGain(uint8_t fineGain);
    virtual uint8_t getAutoRFGain();
    virtual bool setRFGain(uint8_t rfGain);
//------------------------------------------------------------------------------------

    virtual bool EosSetHDCPKey(const uint8_t *pu8Key, uint32_t u32Key_len, bool bVer2Flag);
    virtual bool EosGetHDCPKey(uint8_t *pu8Key, uint32_t u32Key_len, bool bVer2Flag);
    virtual bool getTunerStatus();
        const sp<IFactoryManagerClient>& getFactoryManagerClient() const
        {
            return m_FactoryManagerClient;
        };

    private:
        friend class FactoryManagerService;
        Client(const sp<FactoryManagerService>& service,
               const sp<IFactoryManagerClient>& client,
               pid_t clientPid);
        ~Client();

        mutable Mutex m_Lock;
        sp<FactoryManagerService> m_FactoryManagerService;
        sp<IFactoryManagerClient> m_FactoryManagerClient;
        pid_t m_ClientPid;
        int32_t m_ConnId;
        bool m_bEnableDebug;
    };

// ----------------------------------------------------------------------------

    FactoryManagerService();
    ~FactoryManagerService();

    static MSrv_Factory_Mode* m_MSrvFactory_Mode;

//    mutable Mutex m_Lock;
    //wp<Client> m_Client;
    SortedVector< wp<Client> > m_Clients;
    SortedVector< wp<Client> > m_vWaitingRemoveClients;

    volatile int32_t m_Users;
    virtual int32_t incUsers();
    virtual void decUsers();
};

#endif // _FACTORYMANAGER_SERVICE_H_
