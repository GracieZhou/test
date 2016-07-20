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
#ifndef _IFACTORYMANAGER_H_
#define _IFACTORYMANAGER_H_

#include <binder/IInterface.h>
#include <binder/Parcel.h>
#include <binder/IPCThreadState.h>
#include "FactoryManagerType.h"
using namespace android;

class IFactoryManager: public IInterface
{
public:
    DECLARE_META_INTERFACE(FactoryManager);

    virtual void disconnect() = 0;

    virtual bool autoAdc() =0;
    virtual void copySubColorDataToAllSource() =0;
    virtual void copyWhiteBalanceSettingToAllSource() =0;
    virtual bool disablePVRRecordAll() =0;
    virtual bool disableUart() =0;
    virtual bool disableWdt() =0;
    virtual bool enablePVRRecordAll() =0;
    virtual bool enableUart() =0;
    virtual bool enableWdt() =0;
    virtual void getAdcGainOffset(int enWin, int eAdcIndex,PqlCalibrationData &pstADCGainOffsetOut) =0;
    virtual void setUartEnv(bool on) = 0;
    virtual bool getUartEnv() = 0;
    virtual int getDisplayResolution() =0;
    virtual void getPictureModeValue(PictureModeValue &PModeValue) =0;
    virtual int getQmapCurrentTableIdx(short ipIndex) =0;
    virtual String8 getQmapIpName(short ipIndex) =0;
    virtual int getQmapIpNum() =0;
    virtual String8 getQmapTableName(short ipIndex, short tableIndex) =0;
    virtual int getQmapTableNum(short ipIndex) =0;
    virtual void getWbGainOffset(int eColorTemp,WbGainOffset &GainOffset) =0;
    virtual void getWbGainOffsetEx(int eColorTemp, int enSrcType,WbGainOffsetEx &WbExOut) =0;
    virtual bool isAgingModeOn() =0;
    virtual bool isPVRRecordAllOn() =0;
    virtual bool isUartOn()  =0;
    virtual bool isWdtOn()  =0;
    virtual void loadPqTable(int tableIndex, int ipIndex)  =0;
    virtual bool resetDisplayResolution()  =0;
    virtual bool restoreDbFromUsb() =0;
    virtual void setAdcGainOffset(int enWin, int eAdcIndex, PqlCalibrationData stADCGainOffset)  =0;
    virtual bool setBrightness(short subBrightness)  =0;
    virtual bool setContrast(short subContrast) =0;
    virtual void setFactoryVdInitParameter(FactoryNsVdSet factoryNsVdSetVo) =0;
    virtual void setFactoryVDParameter(FactoryNsVdSet factoryNsVdSetVo)  =0;
    virtual bool setHue(short hue) =0;
    virtual bool setSaturation(short saturation) =0;
    virtual bool setSharpness(short sharpness) =0;
    virtual void setVideoTestPattern(int enColor) =0;
    virtual bool setVideoMuteColor(int enColor) =0;
    virtual void setWbGainOffset(int eColorTemp, short redGain, short greenGain, short blueGain, short redOffset, short greenOffset, short blueOffset) =0;
    virtual void setWbGainOffsetEx(int eColorTemp, int redGain, int greenGain, int blueGain, int redOffset, int greenOffset, int blueOffset, int enSrcType)  =0;
    virtual bool storeDbToUsb()  =0;
    virtual int32_t getFwVersion(int32_t type) = 0;
    virtual bool updateSscParameter() =0;
    virtual void setDebugMode(bool mode) = 0;
    virtual void getSoftwareVersion(String8 &version) = 0;
    virtual void stopTvService() = 0;
    virtual void restoreFactoryAtvProgramTable(short cityIndex) = 0;
    virtual void restoreFactoryDtvProgramTable(short cityIndex) = 0;
    virtual void setPQParameterViaUsbKey() = 0;
    virtual void setHDCPKeyViaUsbKey() = 0;
    virtual void setCIPlusKeyViaUsbKey() = 0;
    virtual void setMACAddrViaUsbKey() = 0;
    virtual void getMACAddrString(String8 &mac) = 0;
    virtual bool startUartDebug() =0;
    virtual bool uartSwitch() =0;
    virtual bool readBytesFromI2C(int32_t u32gID, uint8_t u8AddrSize, uint8_t *pu8Addr, uint16_t u16Size, uint8_t *pu8Data) =0;
    virtual bool writeBytesToI2C(int32_t u32gID, uint8_t u8AddrSize, uint8_t *pu8Addr, uint16_t u16Size, uint8_t *pu8Data) =0;
    virtual int16_t getResolutionMappingIndex(int32_t enCurrentInputType) =0;
    virtual bool setEnvironmentPowerMode(int32_t ePowerMode) =0;
    virtual int32_t getEnvironmentPowerMode() =0;
    virtual bool setEnvironmentPowerOnMusicVolume(uint8_t volume) =0;
    virtual uint8_t getEnvironmentPowerOnMusicVolume() =0;

    virtual bool getUpdatePQFilePath(int32_t enumpqfile, String8 &filePath) =0;
    virtual void updatePQiniFiles() =0;

    virtual String8 getPQVersion(int32_t escalerwindow) = 0;

    virtual bool UrsaGetVersionInfo(Ursa_Version_Info *pVersionInfo) = 0;
    virtual void setWOLEnableStatus(bool flag) = 0;
    virtual bool getWOLEnableStatus() = 0;
    virtual bool setXvyccDataFromPanel(float fRedX, float fRedY,
                                        float fGreenX, float fGreenY,
                                        float fBlueX, float fBlueY,
                                        float fWhiteX, float fWhiteY, int32_t eWin) = 0;
    virtual void getEnableIPInfo(uint8_t *pBitTable, int32_t sBitTableLen) = 0;

//------------------------------------------------------------------------------------
    virtual uint8_t getAutoFineGain() = 0;
    virtual bool setFixedFineGain(uint8_t fineGain) = 0;
    virtual uint8_t getAutoRFGain() = 0;
    virtual bool setRFGain(uint8_t rfGain) = 0;
//------------------------------------------------------------------------------------
    virtual bool EosSetHDCPKey(const uint8_t *pu8Key, uint32_t u32Key_len, bool bVer2Flag) = 0;
    virtual bool EosGetHDCPKey(uint8_t *pu8Key, uint32_t u32Key_len, bool bVer2Flag) = 0;
    virtual bool getTunerStatus(void) =0;
};

// ----------------------------------------------------------------------------

class BnFactoryManager: public BnInterface<IFactoryManager>
{
public:

    virtual status_t onTransact(uint32_t code,
                                const Parcel& data,
                                Parcel* reply,
                                uint32_t flags = 0);



};

#endif // _IFACTORYMANAGER_H_

