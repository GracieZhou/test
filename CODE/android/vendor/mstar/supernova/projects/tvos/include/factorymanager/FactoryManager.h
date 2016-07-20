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
#ifndef _FACTORYMANAGER_H_
#define _FACTORYMANAGER_H_

#include <utils/threads.h>
#include "IFactoryManagerClient.h"
#include "IFactoryManagerService.h"
#include "FactoryManagerType.h"

using namespace android;

// ref-counted object for callbacks
class FactoryManagerListener : virtual public RefBase
{
public:
    virtual void notify(int32_t msgType, int32_t ext1, int32_t ext2) = 0;
    virtual void PostEvent_Template(int32_t ext1, int32_t ext2) = 0;
    virtual void PostEvent_SnServiceDeadth(int32_t ext1, int32_t ext2) = 0;

};

class FactoryManager : public BnFactoryManagerClient, public IBinder::DeathRecipient
{
public:
    FactoryManager();
    ~FactoryManager();

    static sp<FactoryManager> connect();
    void disconnect();

    bool autoAdc();
    void copySubColorDataToAllSource();
    void copyWhiteBalanceSettingToAllSource();
    bool disablePVRRecordAll(); //remove in java doc
    bool disableUart();
    bool disableWdt();
    bool enablePVRRecordAll(); //remove in java doc
    bool enableUart();
    bool enableWdt();
    void getAdcGainOffset(int enWin, int eAdcIndex,PqlCalibrationData &pstADCGainOffset);
    void setUartEnv(bool on);
    bool getUartEnv();
    int getDisplayResolution();
    void getPictureModeValue(PictureModeValue &PModeValue);
    int getQmapCurrentTableIdx(short ipIndex);
    String8 getQmapIpName(short ipIndex);
    int getQmapIpNum();
    String8 getQmapTableName(short ipIndex, short tableIndex);
    int getQmapTableNum(short ipIndex);
    void getWbGainOffset(int eColorTemp,WbGainOffset &GainOffset);
    void getWbGainOffsetEx(int eColorTemp, int enSrcType ,WbGainOffsetEx &WbExOut);
    bool isAgingModeOn();
    bool isPVRRecordAllOn();  //remove in java doc
    bool isUartOn() ;
    bool isWdtOn() ;
    void loadPqTable(int tableIndex, int ipIndex) ;
    bool resetDisplayResolution() ;
    bool restoreDbFromUsb();
    void setAdcGainOffset(int enWin, int eAdcIndex, PqlCalibrationData stADCGainOffset) ;
    bool setBrightness(short subBrightness) ;
    bool setContrast(short subContrast);
    void setFactoryVdInitParameter(FactoryNsVdSet factoryNsVdSetVo);
    void setFactoryVDParameter(FactoryNsVdSet factoryNsVdSetVo) ;
    bool setHue(short hue);
    bool setSaturation(short saturation);
    bool setSharpness(short sharpness);
    void setVideoTestPattern(int enColor);
    bool setVideoMuteColor(int enColor);
    void setWbGainOffset(int eColorTemp, short redGain, short greenGain, short blueGain, short redOffset, short greenOffset, short blueOffset);
    void setWbGainOffsetEx(int eColorTemp, int redGain, int greenGain, int blueGain, int redOffset, int greenOffset, int blueOffset, int enSrcType) ;
    bool storeDbToUsb() ;

    int32_t getFwVersion(int32_t type);
    bool updateSscParameter();

    void setDebugMode(bool mode);

    void getSoftwareVersion(String8 &version);
    void stopTvService();

    void restoreFactoryAtvProgramTable(short cityIndex);
    void restoreFactoryDtvProgramTable(short cityIndex);

    void setPQParameterViaUsbKey();
    void setHDCPKeyViaUsbKey();
    void setCIPlusKeyViaUsbKey();
    void setMACAddrViaUsbKey();
    void getMACAddrString(String8 &mac);

    bool startUartDebug();

    bool uartSwitch();

    bool readBytesFromI2C(int32_t u32gID, uint8_t u8AddrSize, uint8_t *pu8Addr, uint16_t u16Size, uint8_t *pu8Data);

    bool writeBytesToI2C(int32_t u32gID, uint8_t u8AddrSize, uint8_t *pu8Addr, uint16_t u16Size, uint8_t *pu8Data);
    int16_t getResolutionMappingIndex(int32_t enCurrentInputType);

    bool setEnvironmentPowerMode(int32_t ePowerMode);
    int32_t getEnvironmentPowerMode();
    bool setEnvironmentPowerOnMusicVolume(uint8_t volume);
    uint8_t getEnvironmentPowerOnMusicVolume();

    bool getUpdatePQFilePath(int32_t enumpqfile, String8 &filePath);
    void updatePQiniFiles();

    String8 getPQVersion(int32_t escalerwindow);

    bool UrsaGetVersionInfo(Ursa_Version_Info *pVersionInfo);
    void setWOLEnableStatus(bool flag);
    bool getWOLEnableStatus();

    status_t setListener(const sp<FactoryManagerListener>& listener);

    // ITimerClient interface
    virtual void notify(int32_t msgType, int32_t ext, int32_t ext2);

    virtual void PostEvent_Template(int32_t ext1, int32_t ext2);
    virtual void PostEvent_SnServiceDeadth( int32_t ext1, int32_t ext2);
    bool setXvyccDataFromPanel(float fRedX, float fRedY,
                                                float fGreenX, float fGreenY,
                                                float fBlueX, float fBlueY,
                                                float fWhiteX, float fWhiteY, int32_t eWin);
    void getEnableIPInfo(uint8_t *pBitTable, int32_t sBitTableLen);

//------------------------------------------------------------------------------------
/// API for customize
    uint8_t getAutoFineGain();
    bool setFixedFineGain(uint8_t fineGain);
    uint8_t getAutoRFGain();
    bool setRFGain(uint8_t rfGain);
//------------------------------------------------------------------------------------

    bool EosSetHDCPKey(const uint8_t *pu8Key, uint32_t u32Key_len, bool bVer2Flag);
    bool EosGetHDCPKey(uint8_t *pu8Key, uint32_t u32Key_len, bool bVer2Flag);
    bool getTunerStatus();
private:
    static const sp<IFactoryManagerService>& getFactoryManagerService();

    static Mutex mLock;
    static sp<IFactoryManagerService> mFactoryManagerService;
    sp<IFactoryManager> mFactoryManager;
    sp<FactoryManagerListener> mListener;

// ----------------------------------------------------------------------------

    virtual void binderDied(const wp<IBinder>& who);
    class DeathNotifier : public IBinder::DeathRecipient
    {
    public:
        ~DeathNotifier();

        virtual void binderDied(const wp<IBinder>& who);
    };
    static sp<DeathNotifier> mDeathNotifier;

// ----------------------------------------------------------------------------
};

#endif // _FACTORYMANAGER_H_
