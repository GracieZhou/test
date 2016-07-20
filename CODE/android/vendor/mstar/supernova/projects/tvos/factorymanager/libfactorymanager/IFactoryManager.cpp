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
#define LOG_TAG "IFactoryManager"
#include <utils/Log.h>

#include "IFactoryManager.h"

enum
{
    DISCONNECT = IBinder::FIRST_CALL_TRANSACTION,
    FACTORY_autoAdc =IBinder::FIRST_CALL_TRANSACTION+1,
    FACTORY_copySubColorDataToAllSource =IBinder::FIRST_CALL_TRANSACTION+2,
    FACTORY_copyWhiteBalanceSettingToAllSource =IBinder::FIRST_CALL_TRANSACTION+3,
    FACTORY_disablePVRRecordAll =IBinder::FIRST_CALL_TRANSACTION+4,
    FACTORY_disableUart =IBinder::FIRST_CALL_TRANSACTION+5,
    FACTORY_disableWdt =IBinder::FIRST_CALL_TRANSACTION+6,
    FACTORY_enablePVRRecordAll =IBinder::FIRST_CALL_TRANSACTION+7,
    FACTORY_enableUart =IBinder::FIRST_CALL_TRANSACTION+8,
    FACTORY_enableWdt =IBinder::FIRST_CALL_TRANSACTION+9,
    FACTORY_getAdcGainOffset=IBinder::FIRST_CALL_TRANSACTION+10,
    FACTORY_setUartEnv =IBinder::FIRST_CALL_TRANSACTION+11,
    FACTORY_getUartEnv =IBinder::FIRST_CALL_TRANSACTION+12,
    FACTORY_getDisplayResolution =IBinder::FIRST_CALL_TRANSACTION+13,
    FACTORY_getPictureModeValue =IBinder::FIRST_CALL_TRANSACTION+14,
    FACTORY_getQmapCurrentTableIdx =IBinder::FIRST_CALL_TRANSACTION+15,
    FACTORY_getQmapIpName =IBinder::FIRST_CALL_TRANSACTION+16,
    FACTORY_getQmapIpNum =IBinder::FIRST_CALL_TRANSACTION+17,
    FACTORY_getQmapTableName =IBinder::FIRST_CALL_TRANSACTION+18,
    FACTORY_getQmapTableNum =IBinder::FIRST_CALL_TRANSACTION+19,
    FACTORY_getWbGainOffset =IBinder::FIRST_CALL_TRANSACTION+20,
    FACTORY_getWbGainOffsetEx =IBinder::FIRST_CALL_TRANSACTION+21,
    FACTORY_isAgingModeOn =IBinder::FIRST_CALL_TRANSACTION+22,
    FACTORY_isPVRRecordAllOn =IBinder::FIRST_CALL_TRANSACTION+23,
    FACTORY_isUartOn =IBinder::FIRST_CALL_TRANSACTION+24,
    FACTORY_isWdtOn =IBinder::FIRST_CALL_TRANSACTION+25,
    FACTORY_loadPqTable =IBinder::FIRST_CALL_TRANSACTION+26,
    FACTORY_resetDisplayResolution =IBinder::FIRST_CALL_TRANSACTION+27,
    FACTORY_restoreDbFromUsb =IBinder::FIRST_CALL_TRANSACTION+28,
    FACTORY_setAdcGainOffset =IBinder::FIRST_CALL_TRANSACTION+29,
    FACTORY_setBrightness =IBinder::FIRST_CALL_TRANSACTION+30,
    FACTORY_setContrast =IBinder::FIRST_CALL_TRANSACTION+31,
    FACTORY_setFactoryVdInitParameter =IBinder::FIRST_CALL_TRANSACTION+32,
    FACTORY_setFactoryVDParameter =IBinder::FIRST_CALL_TRANSACTION+33,
    FACTORY_setHue =IBinder::FIRST_CALL_TRANSACTION+34,
    FACTORY_setSaturation =IBinder::FIRST_CALL_TRANSACTION+35,
    FACTORY_setSharpness =IBinder::FIRST_CALL_TRANSACTION+36,
    FACTORY_setVideoTestPattern =IBinder::FIRST_CALL_TRANSACTION+37,
    FACTORY_setWbGainOffset =IBinder::FIRST_CALL_TRANSACTION+38,
    FACTORY_setWbGainOffsetEx =IBinder::FIRST_CALL_TRANSACTION+39,
    FACTORY_storeDbToUsb =IBinder::FIRST_CALL_TRANSACTION+40,
    FACTORY_getFwVersion =IBinder::FIRST_CALL_TRANSACTION+41,
    FACTORY_updateSscParameter =IBinder::FIRST_CALL_TRANSACTION+42,
    SETDEBUGMODE =IBinder::FIRST_CALL_TRANSACTION+43,
    FACTORY_getSoftwareVersion =IBinder::FIRST_CALL_TRANSACTION+44,
    STOPTVSERVICE =IBinder::FIRST_CALL_TRANSACTION+45,
    FACTORY_restoreFactoryAtvProgramTable = IBinder::FIRST_CALL_TRANSACTION+46,
    FACTORY_restoreFactoryDtvProgramTable = IBinder::FIRST_CALL_TRANSACTION+47,
    FACTORY_setPQParameterViaUsbKey = IBinder::FIRST_CALL_TRANSACTION+48,
    FACTORY_startUartDebug = IBinder::FIRST_CALL_TRANSACTION+49,
    FACTORY_uartSwitch = IBinder::FIRST_CALL_TRANSACTION+50,
    FACTORY_getResolutionMappingIndex = IBinder::FIRST_CALL_TRANSACTION+51,
    FACTORY_SETENVPOWERMODE=IBinder::FIRST_CALL_TRANSACTION+52,
    FACTORY_GETENVPOWERMODE=IBinder::FIRST_CALL_TRANSACTION+53,
    FACTORY_SETENVPOWERVOLUME=IBinder::FIRST_CALL_TRANSACTION+54,
    FACTORY_GETENVPOWERVOLUME=IBinder::FIRST_CALL_TRANSACTION+55,
    FACTORY_READBYTESFROMI2C=IBinder::FIRST_CALL_TRANSACTION+56,
    FACTORY_WRITEBYTESTOI2C=IBinder::FIRST_CALL_TRANSACTION+57,
    FACTORY_GETUPDATEPQFILEPATH=IBinder::FIRST_CALL_TRANSACTION+58,
    FACTORY_UPDATEPQINIFILES=IBinder::FIRST_CALL_TRANSACTION+59,
    FACTORY_GETPQVERSION=IBinder::FIRST_CALL_TRANSACTION+60,
    FACTORY_GETURSAVERSIONINFO=IBinder::FIRST_CALL_TRANSACTION+61,
    FACTORY_setHDCPKeyViaUsbKey=IBinder::FIRST_CALL_TRANSACTION+62,
    FACTORY_setCIPlusKeyViaUsbKey=IBinder::FIRST_CALL_TRANSACTION+63,
    FACTORY_setMACAddrViaUsbKey=IBinder::FIRST_CALL_TRANSACTION+64,
    FACTORY_getMACAddrString=IBinder::FIRST_CALL_TRANSACTION+65,
    FACTORY_SETWOLENABLESTATUS=IBinder::FIRST_CALL_TRANSACTION+66,
    FACTORY_GETWOLENABLESTATUS=IBinder::FIRST_CALL_TRANSACTION+67,
    FACTORY_SETXVYCCDATAFROMPANEL = IBinder::FIRST_CALL_TRANSACTION + 68,
    FACTORY_GETENABLEIPINFO = IBinder::FIRST_CALL_TRANSACTION + 69,
//------------------------------------------------------------------------------------
    FACTORY_GETAUTOFINEGAIN = IBinder::FIRST_CALL_TRANSACTION + 70,
    FACTORY_SETFIXEDFINEGAIN = IBinder::FIRST_CALL_TRANSACTION + 71,
    FACTORY_GETAUTORFGAIN = IBinder::FIRST_CALL_TRANSACTION + 72,
    FACTORY_SETRFGAIN = IBinder::FIRST_CALL_TRANSACTION + 73,
    FACTORY_SETVIDEOMUTECOLOR = IBinder::FIRST_CALL_TRANSACTION + 74,
//------------------------------------------------------------------------------------
    FACTORY_EosSetHDCPKey = IBinder::FIRST_CALL_TRANSACTION + 75,
    FACTORY_EosGetHDCPKey = IBinder::FIRST_CALL_TRANSACTION + 76,
    FACTORY_getTunerStatus = IBinder::FIRST_CALL_TRANSACTION + 77,
};

class BpFactoryManager: public BpInterface<IFactoryManager>
{
public:
    explicit BpFactoryManager(const sp<IBinder>& impl);
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
    virtual bool setEnvironmentPowerMode(int32_t ePowerMode);
    virtual int32_t getEnvironmentPowerMode();
    virtual bool setEnvironmentPowerOnMusicVolume(uint8_t volume);
    virtual uint8_t getEnvironmentPowerOnMusicVolume();
    virtual bool readBytesFromI2C(int32_t u32gID, uint8_t u8AddrSize, uint8_t *pu8Addr, uint16_t u16Size, uint8_t *pu8Data);
    virtual bool writeBytesToI2C(int32_t u32gID, uint8_t u8AddrSize, uint8_t *pu8Addr, uint16_t u16Size, uint8_t *pu8Data);
    virtual bool getUpdatePQFilePath(int32_t enumpqfile, String8 &filePath);
    virtual void updatePQiniFiles();
    virtual String8 getPQVersion(int escalerwindow);
    virtual bool UrsaGetVersionInfo(Ursa_Version_Info *pVersionInfo);
    virtual void setWOLEnableStatus(bool flag);
    virtual bool getWOLEnableStatus();
    virtual bool setXvyccDataFromPanel(float fRedX, float fRedY,
                                        float fGreenX, float fGreenY,
                                        float fBlueX, float fBlueY,
                                        float fWhiteX, float fWhiteY, int32_t eWin);
    virtual void getEnableIPInfo(uint8_t *pBitTable, int32_t sBitTableLen);
//------------------------------------------------------------------------------------
/// API for customize
    virtual uint8_t getAutoFineGain();
    virtual bool setFixedFineGain(uint8_t fineGain);
    virtual uint8_t getAutoRFGain();
    virtual bool setRFGain(uint8_t rfGain);
//------------------------------------------------------------------------------------

    virtual bool EosSetHDCPKey(const uint8_t *pu8Key, uint32_t u32Key_len, bool bVer2Flag);
    virtual bool EosGetHDCPKey(uint8_t *pu8Key, uint32_t u32Key_len, bool bVer2Flag);
    virtual bool getTunerStatus();
};



//////////////////////////////////////////////////////////////////////////////////

BpFactoryManager::BpFactoryManager(const sp<IBinder>& impl)
: BpInterface<IFactoryManager>(impl)
{
}

void BpFactoryManager::disconnect()
{
    ALOGV("Send DISCONNECT\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(DISCONNECT, data, &reply);
}

bool BpFactoryManager::autoAdc()
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_autoAdc, data, &reply);
    return static_cast<bool>(reply.readInt32());
}
void BpFactoryManager::copySubColorDataToAllSource()
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_copySubColorDataToAllSource, data, &reply);

}
void BpFactoryManager::copyWhiteBalanceSettingToAllSource()
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_copyWhiteBalanceSettingToAllSource, data, &reply);

}
bool BpFactoryManager::disablePVRRecordAll()
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_disablePVRRecordAll, data, &reply);
    return static_cast<bool>(reply.readInt32());

}
bool BpFactoryManager::disableUart()
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_disableUart, data, &reply);
    return static_cast<bool>(reply.readInt32());

}
bool BpFactoryManager::disableWdt()
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_disableWdt, data, &reply);
    return static_cast<bool>(reply.readInt32());

}
bool BpFactoryManager::enablePVRRecordAll()
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_enablePVRRecordAll, data, &reply);
    return static_cast<bool>(reply.readInt32());

}
bool BpFactoryManager::enableUart()
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_enableUart, data, &reply);
    return static_cast<bool>(reply.readInt32());

}
bool BpFactoryManager::enableWdt()
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_enableWdt, data, &reply);
    return static_cast<bool>(reply.readInt32());

}
void BpFactoryManager::getAdcGainOffset(int enWin, int eAdcIndex,PqlCalibrationData &pstADCGainOffsetOut)
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(enWin);
    data.writeInt32(eAdcIndex);
    remote()->transact(FACTORY_getAdcGainOffset, data, &reply);
    pstADCGainOffsetOut.blueGain=reply.readInt32();
    pstADCGainOffsetOut.blueOffset=reply.readInt32();
    pstADCGainOffsetOut.greenGain=reply.readInt32();
    pstADCGainOffsetOut.greenOffset=reply.readInt32();
    pstADCGainOffsetOut.redGain=reply.readInt32();
    pstADCGainOffsetOut.redOffset=reply.readInt32();
}
void BpFactoryManager::setUartEnv(bool on)
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(on);
    remote()->transact(FACTORY_setUartEnv, data, &reply);

}

bool BpFactoryManager::getUartEnv()
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_getUartEnv, data, &reply);
    return static_cast<bool>(reply.readInt32());
}


int BpFactoryManager::getDisplayResolution()
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_getDisplayResolution, data, &reply);
    return static_cast<bool>(reply.readInt32());

}
void BpFactoryManager::getPictureModeValue(PictureModeValue &PModeValue)
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_getPictureModeValue, data, &reply);

    PModeValue.brightness=reply.readInt32();
    PModeValue.contrast=reply.readInt32();
    PModeValue.hue=reply.readInt32();
    PModeValue.saturation=reply.readInt32();
    PModeValue.sharpness=reply.readInt32();

}
int BpFactoryManager::getQmapCurrentTableIdx(short ipIndex)
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(ipIndex);
    remote()->transact(FACTORY_getQmapCurrentTableIdx, data, &reply);

    return reply.readInt32();
}
String8 BpFactoryManager::getQmapIpName(short ipIndex)
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(ipIndex);
    remote()->transact(FACTORY_getQmapIpName, data, &reply);
    return reply.readString8();

}
int BpFactoryManager::getQmapIpNum()
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_getQmapIpNum, data, &reply);
    return reply.readInt32();

}
String8 BpFactoryManager::getQmapTableName(short ipIndex, short tableIndex)
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(ipIndex);
    data.writeInt32(tableIndex);
    remote()->transact(FACTORY_getQmapTableName, data, &reply);
    return reply.readString8();

}
int BpFactoryManager::getQmapTableNum(short ipIndex)
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(ipIndex);
    remote()->transact(FACTORY_getQmapTableNum, data, &reply);
    return reply.readInt32();

}
void BpFactoryManager::getWbGainOffset(int eColorTemp,WbGainOffset &GainOffset)
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(eColorTemp);
    remote()->transact(FACTORY_getWbGainOffset, data, &reply);
    GainOffset.blueGain = static_cast<short>(reply.readInt32());
    GainOffset.blueOffset = static_cast<short>(reply.readInt32());
    GainOffset.greenGain = static_cast<short>(reply.readInt32());
    GainOffset.greenOffset = static_cast<short>(reply.readInt32());
    GainOffset.redGain = static_cast<short>(reply.readInt32());
    GainOffset.redOffset = static_cast<short>(reply.readInt32());
}
void BpFactoryManager::getWbGainOffsetEx(int eColorTemp, int enSrcType,WbGainOffsetEx &WbExOut)
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(eColorTemp);
    data.writeInt32(enSrcType);
    remote()->transact(FACTORY_getWbGainOffsetEx, data, &reply);
    WbExOut.blueGain = reply.readInt32();
    WbExOut.blueOffset = reply.readInt32();
    WbExOut.greenGain = reply.readInt32();
    WbExOut.greenOffset = reply.readInt32();
    WbExOut.redGain = reply.readInt32();
    WbExOut.redOffset = reply.readInt32();
}
bool BpFactoryManager::isAgingModeOn()
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_isAgingModeOn, data, &reply);
    return static_cast<bool>(reply.readInt32());

}
bool BpFactoryManager::isPVRRecordAllOn()
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_isPVRRecordAllOn, data, &reply);
    return static_cast<bool>(reply.readInt32());

}
bool BpFactoryManager::isUartOn()
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_isUartOn, data, &reply);
    return static_cast<bool>(reply.readInt32());

}
bool BpFactoryManager::isWdtOn()
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_isWdtOn, data, &reply);
    return static_cast<bool>(reply.readInt32());

}
void BpFactoryManager::loadPqTable(int tableIndex, int ipIndex)
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(tableIndex);
    data.writeInt32(ipIndex);
    remote()->transact(FACTORY_loadPqTable, data, &reply);
}
bool BpFactoryManager::resetDisplayResolution()
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_resetDisplayResolution, data, &reply);
    return static_cast<bool>(reply.readInt32());

}
bool BpFactoryManager::restoreDbFromUsb()
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_restoreDbFromUsb, data, &reply);
    return static_cast<bool>(reply.readInt32());

}
void BpFactoryManager::setAdcGainOffset(int enWin, int eAdcIndex, PqlCalibrationData stADCGainOffset)
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(enWin);
    data.writeInt32(eAdcIndex);
    data.writeInt32(stADCGainOffset.blueGain);
    data.writeInt32(stADCGainOffset.blueOffset);
    data.writeInt32(stADCGainOffset.greenGain);
    data.writeInt32(stADCGainOffset.greenOffset);
    data.writeInt32(stADCGainOffset.redGain);
    data.writeInt32(stADCGainOffset.redOffset);
    remote()->transact(FACTORY_setAdcGainOffset, data, &reply);
}
bool BpFactoryManager::setBrightness(short subBrightness)
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(subBrightness);
    remote()->transact(FACTORY_setBrightness, data, &reply);
    return static_cast<bool>(reply.readInt32());
}
bool BpFactoryManager::setContrast(short subContrast)
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(subContrast);
    remote()->transact(FACTORY_setContrast, data, &reply);
    return static_cast<bool>(reply.readInt32());
}
void BpFactoryManager::setFactoryVdInitParameter(FactoryNsVdSet factoryNsVdSetVo)
{

    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(factoryNsVdSetVo.aFEC_43);
    data.writeInt32(factoryNsVdSetVo.aFEC_44);
    data.writeInt32(factoryNsVdSetVo.aFEC_66_Bit76);
    data.writeInt32(factoryNsVdSetVo.aFEC_6E_Bit3210);
    data.writeInt32(factoryNsVdSetVo.aFEC_6E_Bit3210);
    data.writeInt32(factoryNsVdSetVo.aFEC_A0);
    data.writeInt32(factoryNsVdSetVo.aFEC_A1);
    data.writeInt32(factoryNsVdSetVo.aFEC_CB);
    data.writeInt32(factoryNsVdSetVo.aFEC_D4);
    data.writeInt32(factoryNsVdSetVo.aFEC_D5_Bit2);
    data.writeInt32(factoryNsVdSetVo.aFEC_D7_HIGH_BOUND);
    data.writeInt32(factoryNsVdSetVo.aFEC_D7_LOW_BOUND);
    data.writeInt32(factoryNsVdSetVo.aFEC_D8_Bit3210);
    data.writeInt32(factoryNsVdSetVo.aFEC_D9_Bit0);
    data.writeInt32(factoryNsVdSetVo.aFEC_CF_Bit2_ATV);
    data.writeInt32(factoryNsVdSetVo.aFEC_CF_Bit2_AV);
    remote()->transact(FACTORY_setFactoryVdInitParameter, data, &reply);
}

void BpFactoryManager::setFactoryVDParameter(FactoryNsVdSet factoryNsVdSetVo)
{
    Parcel data, reply;

    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(factoryNsVdSetVo.aFEC_43);
    data.writeInt32(factoryNsVdSetVo.aFEC_44);
    data.writeInt32(factoryNsVdSetVo.aFEC_66_Bit76);
    data.writeInt32(factoryNsVdSetVo.aFEC_6E_Bit3210);
    data.writeInt32(factoryNsVdSetVo.aFEC_6E_Bit7654);
    data.writeInt32(factoryNsVdSetVo.aFEC_A0);
    data.writeInt32(factoryNsVdSetVo.aFEC_A1);
    data.writeInt32(factoryNsVdSetVo.aFEC_CB);
    data.writeInt32(factoryNsVdSetVo.aFEC_D4);
    data.writeInt32(factoryNsVdSetVo.aFEC_D5_Bit2);
    data.writeInt32(factoryNsVdSetVo.aFEC_D7_HIGH_BOUND);
    data.writeInt32(factoryNsVdSetVo.aFEC_D7_LOW_BOUND);
    data.writeInt32(factoryNsVdSetVo.aFEC_D8_Bit3210);
    data.writeInt32(factoryNsVdSetVo.aFEC_D9_Bit0);
    data.writeInt32(factoryNsVdSetVo.aFEC_CF_Bit2_ATV);
    data.writeInt32(factoryNsVdSetVo.aFEC_CF_Bit2_AV);
    remote()->transact(FACTORY_setFactoryVDParameter, data, &reply);

}
bool BpFactoryManager::setHue(short hue)
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(hue);
    remote()->transact(FACTORY_setHue, data, &reply);
    return static_cast<bool>(reply.readInt32());

}
bool BpFactoryManager::setSaturation(short saturation)
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(saturation);
    remote()->transact(FACTORY_setSaturation, data, &reply);
    return static_cast<bool>(reply.readInt32());

}
bool BpFactoryManager::setSharpness(short sharpness)
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(sharpness);
    remote()->transact(FACTORY_setSharpness, data, &reply);
    return static_cast<bool>(reply.readInt32());

}
void BpFactoryManager::setVideoTestPattern(int enColor)
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(enColor);
    remote()->transact(FACTORY_setVideoTestPattern, data, &reply);
}
bool BpFactoryManager::setVideoMuteColor(int enColor)
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(enColor);
    remote()->transact(FACTORY_SETVIDEOMUTECOLOR, data, &reply);
    return static_cast<bool>(reply.readInt32());
}
void BpFactoryManager::setWbGainOffset(int eColorTemp, short redGain, short greenGain, short blueGain, short redOffset, short greenOffset, short blueOffset)
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(eColorTemp);
    data.writeInt32(redGain);
    data.writeInt32(greenGain);
    data.writeInt32(blueGain);
    data.writeInt32(redOffset);
    data.writeInt32(greenOffset);
    data.writeInt32(blueOffset);
    remote()->transact(FACTORY_setWbGainOffset, data, &reply);

}
void BpFactoryManager::setWbGainOffsetEx(int eColorTemp, int redGain, int greenGain, int blueGain, int redOffset, int greenOffset, int blueOffset, int enSrcType)
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(eColorTemp);
    data.writeInt32(redGain);
    data.writeInt32(greenGain);
    data.writeInt32(blueGain);
    data.writeInt32(redOffset);
    data.writeInt32(greenOffset);
    data.writeInt32(blueOffset);
    data.writeInt32(enSrcType);
    remote()->transact(FACTORY_setWbGainOffsetEx, data, &reply);
}
bool BpFactoryManager::storeDbToUsb()
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_storeDbToUsb, data, &reply);
    return static_cast<bool>(reply.readInt32());

}

int32_t BpFactoryManager::getFwVersion(int32_t type)
{
       Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(type);
    remote()->transact(FACTORY_getFwVersion, data, &reply);
    return static_cast<int32_t>(reply.readInt32());

}

bool BpFactoryManager::updateSscParameter()
{
     Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_updateSscParameter, data, &reply);
    return static_cast<bool>(reply.readInt32());
}


void BpFactoryManager::setDebugMode(bool mode)
{
    ALOGV("Send SETDEBUGMODE\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(mode);
    remote()->transact(SETDEBUGMODE, data, &reply);
}

void BpFactoryManager::getSoftwareVersion(String8 &version)
{
    ALOGV("Send FACTORY_getSoftwareVersion\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_getSoftwareVersion, data, &reply);
    version = reply.readString8();
}

void BpFactoryManager::stopTvService()
{
    ALOGV("Send STOPTVSERVICE\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(STOPTVSERVICE, data, &reply);
}

void BpFactoryManager::restoreFactoryAtvProgramTable(short cityIndex)
{
    ALOGV("Send FACTORY_restoreFactoryAtvProgramTable\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(cityIndex);
    remote()->transact(FACTORY_restoreFactoryAtvProgramTable, data, &reply);
}

void BpFactoryManager::restoreFactoryDtvProgramTable(short cityIndex)
{
    ALOGV("Send FACTORY_restoreFactoryDtvProgramTable\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(cityIndex);
    remote()->transact(FACTORY_restoreFactoryDtvProgramTable, data, &reply);
}

void BpFactoryManager::setPQParameterViaUsbKey()
{
    ALOGV("Send FACTORY_setPQParameterViaUsbKey\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_setPQParameterViaUsbKey, data, &reply);
}

void BpFactoryManager::setHDCPKeyViaUsbKey()
{
    ALOGV("Send FACTORY_setHDCPKeyViaUsbKey\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_setHDCPKeyViaUsbKey, data, &reply);
}

void BpFactoryManager::setCIPlusKeyViaUsbKey()
{
    ALOGV("Send FACTORY_setCIPlusKeyViaUsbKey\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_setCIPlusKeyViaUsbKey, data, &reply);
}

void BpFactoryManager::setMACAddrViaUsbKey()
{
    ALOGV("Send FACTORY_setMACAddrViaUsbKey\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_setMACAddrViaUsbKey, data, &reply);
}

void BpFactoryManager::getMACAddrString(String8 &mac)
{
    ALOGV("Send FACTORY_getMACAddrString\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_getMACAddrString, data, &reply);
    mac = reply.readString8();
}

bool BpFactoryManager::startUartDebug()
{
    ALOGV("Send FACTORY_startUartDebug\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_startUartDebug, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

bool BpFactoryManager::uartSwitch()
{
    ALOGV("Send FACTORY_uartSwitch\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_uartSwitch, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

bool BpFactoryManager::readBytesFromI2C(int32_t u32gID, uint8_t u8AddrSize, uint8_t *pu8Addr, uint16_t u16Size, uint8_t *pu8Data)
{
    ALOGV("Send FACTORY_READBYTESFROMI2C\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(u32gID);
    data.writeInt32(u8AddrSize);
    data.writeInt32(u16Size);
    for (uint8_t i=0; i<u8AddrSize; i++)
    {
        data.writeInt32(pu8Addr[i]);
    }
    remote()->transact(FACTORY_READBYTESFROMI2C, data, &reply);
    for (uint8_t i=0; i<u16Size; i++)
    {
        pu8Data[i] = static_cast<uint8_t>(reply.readInt32());
    }
    return static_cast<bool>(reply.readInt32());
}

bool BpFactoryManager::writeBytesToI2C(int32_t u32gID, uint8_t u8AddrSize, uint8_t *pu8Addr, uint16_t u16Size, uint8_t *pu8Data)
{
    ALOGV("Send FACTORY_WRITEBYTESTOI2C\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(u32gID);
    data.writeInt32(u8AddrSize);
    data.writeInt32(u16Size);
    for (uint8_t i=0; i<u8AddrSize; i++)
    {
        data.writeInt32(pu8Addr[i]);
    }
    for (uint8_t i=0; i<u16Size; i++)
    {
        data.writeInt32(pu8Data[i]);
    }
    remote()->transact(FACTORY_WRITEBYTESTOI2C, data, &reply);
    return static_cast<bool>(reply.readInt32());
}


int16_t BpFactoryManager::getResolutionMappingIndex(int32_t enCurrentInputType)
{
    ALOGV("Send FACTORY_getResolutionMappingIndex\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(enCurrentInputType);
    remote()->transact(FACTORY_getResolutionMappingIndex, data, &reply);
    return static_cast<int16_t>(reply.readInt32());
}

bool BpFactoryManager::setEnvironmentPowerMode(int32_t ePowerMode)
{
    ALOGV("Send FACTORY_SETENVPOWERMODE\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(ePowerMode);
    remote()->transact(FACTORY_SETENVPOWERMODE, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

int32_t BpFactoryManager::getEnvironmentPowerMode()
{
    ALOGV("Send FACTORY_GETENVPOWERMODE\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_GETENVPOWERMODE, data, &reply);
    return static_cast<int32_t>(reply.readInt32());
}

bool BpFactoryManager::setEnvironmentPowerOnMusicVolume(uint8_t volume)
{
    ALOGV("Send FACTORY_SETENVPOWERVOLUME\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(volume);
     remote()->transact(FACTORY_SETENVPOWERVOLUME, data, &reply);
     return static_cast<bool>(reply.readInt32());
}

uint8_t BpFactoryManager::getEnvironmentPowerOnMusicVolume()
{
    ALOGV("Send FACTORY_GETENVPOWERVOLUME\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_GETENVPOWERVOLUME, data, &reply);
    return static_cast<int32_t>(reply.readInt32());
}

bool BpFactoryManager::getUpdatePQFilePath(int32_t enumpqfile, String8 &filePath)
{
    ALOGV("Send FACTORY_GETUPDATEPQFILEPATH\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(enumpqfile);
    remote()->transact(FACTORY_GETUPDATEPQFILEPATH, data, &reply);
    filePath = reply.readString8();
    return static_cast<bool>(reply.readInt32());
}

void BpFactoryManager::updatePQiniFiles()
{
    ALOGV("Send FACTORY_UPDATEPQINIFILES\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_UPDATEPQINIFILES, data, &reply);
}

String8 BpFactoryManager::getPQVersion(int escalerwindow)
{
    ALOGV("Send FACTORY_GETPQVERSION\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(escalerwindow);
    remote()->transact(FACTORY_GETPQVERSION, data, &reply);
    return reply.readString8();
}

bool BpFactoryManager::UrsaGetVersionInfo(Ursa_Version_Info *pVersionInfo)
{
    ALOGV("Send FACTORY_GETURSAVERSIONINFO\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_GETURSAVERSIONINFO, data, &reply);
    reply.read(pVersionInfo, sizeof(Ursa_Version_Info));
    return static_cast<bool>(reply.readInt32());
}

void BpFactoryManager::setWOLEnableStatus(bool flag)
{
    ALOGV("Send FACTORY_SETWOLENABLESTATUS\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(flag);
    remote()->transact(FACTORY_SETWOLENABLESTATUS, data, &reply);
    return ;
}

bool BpFactoryManager::getWOLEnableStatus()
{
    ALOGV("Send FACTORY_GETWOLENABLESTATUS\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_GETWOLENABLESTATUS, data, &reply);
    return static_cast<bool>(reply.readInt32());
}


bool BpFactoryManager::setXvyccDataFromPanel(float fRedX, float fRedY,
                            float fGreenX, float fGreenY,
                            float fBlueX, float fBlueY,
                            float fWhiteX, float fWhiteY, int32_t eWin)
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeFloat(fRedX);
    data.writeFloat(fRedY);
    data.writeFloat(fGreenX);
    data.writeFloat(fGreenY);
    data.writeFloat(fBlueX);
    data.writeFloat(fBlueY);
    data.writeFloat(fWhiteX);
    data.writeFloat(fWhiteY);
    data.writeInt32(eWin);
    remote()->transact(FACTORY_SETXVYCCDATAFROMPANEL, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

void BpFactoryManager::getEnableIPInfo(uint8_t *pBitTable, int32_t sBitTableLen)
{
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(sBitTableLen);
    remote()->transact(FACTORY_GETENABLEIPINFO, data, &reply);
    reply.read(pBitTable, sBitTableLen*sizeof(uint8_t));
}
//------------------------------------------------------------------------------------
uint8_t BpFactoryManager::getAutoFineGain()
{
    ALOGV("Send FACTORY_GETAUTOFINEGAIN\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_GETWOLENABLESTATUS, data, &reply);
    return static_cast<uint8_t>(reply.readInt32());
}

bool BpFactoryManager::setFixedFineGain(uint8_t fineGain)
{
    ALOGV("Send FACTORY_SETFIXEDFINEGAIN\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(fineGain);
    remote()->transact(FACTORY_SETFIXEDFINEGAIN, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

uint8_t BpFactoryManager::getAutoRFGain()
{
    ALOGV("Send FACTORY_GETAUTORFGAIN\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    remote()->transact(FACTORY_GETAUTORFGAIN, data, &reply);
    return static_cast<uint8_t>(reply.readInt32());
}

bool BpFactoryManager::setRFGain(uint8_t rfGain)
{
    ALOGV("Send FACTORY_SETRFGAIN\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(rfGain);
    remote()->transact(FACTORY_SETRFGAIN, data, &reply);
    return static_cast<bool>(reply.readInt32());
}
//------------------------------------------------------------------------------------
bool BpFactoryManager::EosSetHDCPKey(const uint8_t * pu8Key, uint32_t u32Key_len, bool bVer2Flag)
{
    ALOGV("Send FACTORY_EosSetHDCPKey\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(u32Key_len);
    data.writeInt32(bVer2Flag);
    for (uint32_t i=0; i<u32Key_len; i++)
    {
        data.writeInt32(pu8Key[i]);
    }
    remote()->transact(FACTORY_EosSetHDCPKey, data, &reply);
    return static_cast<bool>(reply.readInt32());
}

bool BpFactoryManager::EosGetHDCPKey(uint8_t * pu8Key, uint32_t u32Key_len, bool bVer2Flag)
{
    ALOGV("Send FACTORY_EosGetHDCPKey\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor());
    data.writeInt32(u32Key_len);
    data.writeInt32(bVer2Flag);
    remote()->transact(FACTORY_EosGetHDCPKey, data, &reply);
    for (uint32_t i=0; i<u32Key_len; i++)
    {
        pu8Key[i] = static_cast<uint8_t>(reply.readInt32());
    }
    return static_cast<bool>(reply.readInt32());
}

bool BpFactoryManager::getTunerStatus()
{
     ALOGV("Send FACTORY_getTunerStatus\n");
    Parcel data, reply;
    data.writeInterfaceToken(IFactoryManager::getInterfaceDescriptor()); 
    remote()->transact(FACTORY_getTunerStatus, data, &reply);
   return static_cast<bool>(reply.readInt32());
}


IMPLEMENT_META_INTERFACE(FactoryManager, "mstar.IFactoryManager");

status_t BnFactoryManager::onTransact(uint32_t code,
                                const Parcel& data,
                                Parcel* reply,
                                uint32_t flags)
{
    switch(code)
    {
        case DISCONNECT:
        {
            ALOGV("Receive DISCONNECT\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            disconnect();
            return NO_ERROR;
        } break;
        case FACTORY_autoAdc:
        {
            ALOGV("Receive FACTORY_autoAdc\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            reply->writeInt32(autoAdc());
            return NO_ERROR;
        } break;
        case FACTORY_copySubColorDataToAllSource:
        {
            ALOGV("Receive FACTORY_copySubColorDataToAllSource\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            copySubColorDataToAllSource();
            return NO_ERROR;
        } break;
        case FACTORY_copyWhiteBalanceSettingToAllSource:
        {
            ALOGV("Receive FACTORY_copyWhiteBalanceSettingToAllSource\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            copyWhiteBalanceSettingToAllSource();
            return NO_ERROR;
        } break;
        case FACTORY_disablePVRRecordAll:
        {
            ALOGV("Receive TIMER_CONVERT_ST_TIME_2_SECONDS\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            reply->writeInt32(disablePVRRecordAll());
            return NO_ERROR;
        } break;
        case FACTORY_disableUart:
        {
            ALOGV("Receive FACTORY_disableUart\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            reply->writeInt32(disableUart());
            return NO_ERROR;
        } break;
        case FACTORY_disableWdt:
        {
            ALOGV("Receive FACTORY_disableWdt\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            reply->writeInt32(disableWdt());
            return NO_ERROR;
        } break;
        case FACTORY_enablePVRRecordAll:
        {
            ALOGV("Receive FACTORY_disableWdt\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            reply->writeInt32(enablePVRRecordAll());
            return NO_ERROR;
        } break;
        case FACTORY_enableUart:
        {
            ALOGV("Receive FACTORY_disableWdt\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            reply->writeInt32(enableUart());
            return NO_ERROR;
        } break;
        case FACTORY_enableWdt:
        {
            ALOGV("Receive FACTORY_enableWdt\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            reply->writeInt32(enableWdt());
            return NO_ERROR;
        } break;
        case FACTORY_getAdcGainOffset:
        {
            ALOGV("Receive FACTORY_getAdcGainOffset\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            int enWin = data.readInt32();
            int eAdcIndex = data.readInt32();

            PqlCalibrationData pstADCGainOffsetOut;
            getAdcGainOffset(enWin,eAdcIndex,pstADCGainOffsetOut);
            reply->writeInt32(pstADCGainOffsetOut.blueGain);
            reply->writeInt32(pstADCGainOffsetOut.blueOffset);
            reply->writeInt32(pstADCGainOffsetOut.greenGain);
            reply->writeInt32(pstADCGainOffsetOut.greenOffset);
            reply->writeInt32(pstADCGainOffsetOut.redGain);
            reply->writeInt32(pstADCGainOffsetOut.redOffset);
            return NO_ERROR;
        } break;
        case FACTORY_setUartEnv:
        {
            ALOGV("Receive FACTORY_getAdcGainOffset\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            int on = data.readInt32();
            setUartEnv(on);
            return NO_ERROR;
        } break;
        case FACTORY_getUartEnv:
        {
            ALOGV("Receive FACTORY_getAdcGainOffset\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            reply->writeInt32(getUartEnv());
            return NO_ERROR;
        } break;
        case FACTORY_getDisplayResolution:
        {
            ALOGV("Receive FACTORY_getDisplayResolution\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            reply->writeInt32(getDisplayResolution());
            return NO_ERROR;
        } break;
        case FACTORY_getPictureModeValue:
        {
            ALOGV("Receive FACTORY_getPictureModeValue\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            PictureModeValue PModeValue;

            getPictureModeValue(PModeValue);

            reply->writeInt32(PModeValue.brightness);
            reply->writeInt32(PModeValue.contrast);
            reply->writeInt32(PModeValue.hue);
            reply->writeInt32(PModeValue.saturation);
            reply->writeInt32(PModeValue.sharpness);

            return NO_ERROR;
        } break;
        case FACTORY_getQmapCurrentTableIdx:
        {
            ALOGV("Receive TIMER_GET_SLEEPER_STATE\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            short ipIndex = static_cast<short>(data.readInt32());
            reply->writeInt32(getQmapCurrentTableIdx(ipIndex));
            return NO_ERROR;
        } break;
        case FACTORY_getQmapIpName:
        {
            ALOGV("Receive TIMER_GET_SLEEPER_STATE\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            short ipIndex = static_cast<short>(data.readInt32());
            reply->writeString8(getQmapIpName(ipIndex));
            return NO_ERROR;
        } break;
        case FACTORY_getQmapIpNum:
        {
            ALOGV("Receive FACTORY_getQmapIpNum\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            reply->writeInt32(getQmapIpNum());
            return NO_ERROR;
        } break;
        case FACTORY_getQmapTableName:
        {
            ALOGV("Receive TIMER_GET_SLEEPER_STATE\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            short ipIndex = static_cast<short>(data.readInt32());
            short tableIndex = static_cast<short>(data.readInt32());
            reply->writeString8(getQmapTableName(ipIndex,tableIndex));
            return NO_ERROR;
        } break;
        case FACTORY_getQmapTableNum:
        {
            ALOGV("Receive FACTORY_getQmapTableNum\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            int ipIndex = data.readInt32();
            reply->writeInt32(getQmapTableNum(ipIndex));
            return NO_ERROR;
        } break;
        case FACTORY_getWbGainOffset:
        {
            ALOGV("Receive FACTORY_getWbGainOffset\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            short eColorTemp = static_cast<short>(data.readInt32());
            WbGainOffset GainOffset;
            getWbGainOffset(eColorTemp ,GainOffset);
            reply->writeInt32(GainOffset.blueGain);
            reply->writeInt32(GainOffset.blueOffset);
            reply->writeInt32(GainOffset.greenGain);
            reply->writeInt32(GainOffset.greenOffset);
            reply->writeInt32(GainOffset.redGain);
            reply->writeInt32(GainOffset.redOffset);
            return NO_ERROR;
        } break;
        case FACTORY_getWbGainOffsetEx:
        {
            ALOGV("Receive FACTORY_getWbGainOffsetEx\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            short eColorTemp = static_cast<short>(data.readInt32());
            short enSrcType = static_cast<short>(data.readInt32());
            WbGainOffsetEx WbEx;
            getWbGainOffsetEx(eColorTemp,enSrcType,WbEx);
            reply->writeInt32(WbEx.blueGain);
            reply->writeInt32(WbEx.blueOffset);
            reply->writeInt32(WbEx.greenGain);
            reply->writeInt32(WbEx.greenOffset);
            reply->writeInt32(WbEx.redGain);
            reply->writeInt32(WbEx.redOffset);
            return NO_ERROR;
        } break;
        case FACTORY_isAgingModeOn:
        {
            ALOGV("Receive FACTORY_isAgingModeOn\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            reply->writeInt32(isAgingModeOn());
            return NO_ERROR;
        } break;
        case FACTORY_isPVRRecordAllOn:
        {
            ALOGV("Receive FACTORY_isPVRRecordAllOn\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            reply->writeInt32(isPVRRecordAllOn());
            return NO_ERROR;
        } break;
        case FACTORY_isUartOn:
        {
            ALOGV("Receive FACTORY_isUartOn\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            reply->writeInt32(isUartOn());
            return NO_ERROR;
        } break;
        case FACTORY_isWdtOn:
        {
            ALOGV("Receive FACTORY_isWdtOn\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            reply->writeInt32(isWdtOn());
            return NO_ERROR;
        } break;
        case FACTORY_loadPqTable:
        {
            ALOGV("Receive FACTORY_loadPqTable\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            short tableIndex = static_cast<short>(data.readInt32());
            short ipIndex = static_cast<short>(data.readInt32());
            loadPqTable(tableIndex,ipIndex);
            return NO_ERROR;
        } break;
        case FACTORY_resetDisplayResolution:
        {
            ALOGV("Receive FACTORY_isWdtOn\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            reply->writeInt32(resetDisplayResolution());
            return NO_ERROR;
        } break;
        case FACTORY_restoreDbFromUsb:
        {
            ALOGV("Receive FACTORY_isWdtOn\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            reply->writeInt32(restoreDbFromUsb());
            return NO_ERROR;
        } break;
        case FACTORY_setAdcGainOffset:
        {
            ALOGV("Receive FACTORY_setAdcGainOffset\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            short enWin = data.readInt32();
            short eAdcIndex = data.readInt32();
            PqlCalibrationData stADCGainOffset;
            stADCGainOffset.blueGain= data.readInt32();
            stADCGainOffset.blueOffset= data.readInt32();
            stADCGainOffset.greenGain= data.readInt32();
            stADCGainOffset.greenOffset= data.readInt32();
            stADCGainOffset.redGain= data.readInt32();
            stADCGainOffset.redOffset= data.readInt32();
            setAdcGainOffset(enWin,eAdcIndex,stADCGainOffset);
            return NO_ERROR;
        } break;
        case FACTORY_setBrightness:
        {
            ALOGV("Receive FACTORY_setBrightness\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            short subBrightness = static_cast<short>(data.readInt32());
            reply->writeInt32(setBrightness(subBrightness));
            return NO_ERROR;
        } break;
        case FACTORY_setContrast:
        {
            ALOGV("Receive FACTORY_setBrightness\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            short subContrast = static_cast<short>(data.readInt32());
            reply->writeInt32(setContrast(subContrast));
            return NO_ERROR;
        } break;
        case FACTORY_setFactoryVdInitParameter:
        {
            ALOGV("Receive FACTORY_setFactoryVdInitParameter\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);

            FactoryNsVdSet factoryNsVdSetVo;
            factoryNsVdSetVo.aFEC_43  = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_44  = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_66_Bit76  = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_6E_Bit3210  = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_6E_Bit7654  = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_A0  = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_A1  = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_CB  = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_D4  = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_D5_Bit2  = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_D7_HIGH_BOUND  = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_D7_LOW_BOUND  = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_D8_Bit3210  = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_D9_Bit0  = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_CF_Bit2_ATV = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_CF_Bit2_AV = static_cast<uint8_t>(data.readInt32());

            setFactoryVdInitParameter(factoryNsVdSetVo);
            return NO_ERROR;
        } break;
        case FACTORY_setFactoryVDParameter:
        {
            ALOGV("Receive FACTORY_setFactoryVdInitParameter\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);

            FactoryNsVdSet factoryNsVdSetVo;
            factoryNsVdSetVo.aFEC_43  = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_44  = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_66_Bit76  = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_6E_Bit3210  = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_6E_Bit7654  = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_A0  = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_A1  = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_CB  = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_D4  = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_D5_Bit2  = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_D7_HIGH_BOUND  = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_D7_LOW_BOUND  = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_D8_Bit3210  = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_D9_Bit0  = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_CF_Bit2_ATV = static_cast<uint8_t>(data.readInt32());
            factoryNsVdSetVo.aFEC_CF_Bit2_AV = static_cast<uint8_t>(data.readInt32());

            setFactoryVDParameter(factoryNsVdSetVo);

            return NO_ERROR;
        } break;
        case FACTORY_setHue:
        {
            ALOGV("Receive FACTORY_setHue\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            short hue = static_cast<short>(data.readInt32());
            reply->writeInt32(setHue(hue));
            return NO_ERROR;
        } break;
        case FACTORY_setSaturation:
        {
            ALOGV("Receive FACTORY_setSaturation\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            short saturation = static_cast<short>(data.readInt32());
            reply->writeInt32(setSaturation(saturation));
            return NO_ERROR;
        } break;
        case FACTORY_setSharpness:
        {
            ALOGV("Receive FACTORY_setSharpness\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            short saturation = static_cast<short>(data.readInt32());
            reply->writeInt32(setSharpness(saturation));
            return NO_ERROR;
        } break;
        case FACTORY_setVideoTestPattern:
        {
            ALOGV("Receive FACTORY_setVideoTestPattern\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            int enColor = data.readInt32();
            setVideoTestPattern(enColor);
            return NO_ERROR;
        } break;
        case FACTORY_SETVIDEOMUTECOLOR:
        {
            ALOGV("Receive FACTORY_SETVIDEOMUTECOLOR\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            short enColor = static_cast<short>(data.readInt32());
            reply->writeInt32(setVideoMuteColor(enColor));
            return NO_ERROR;
        } break;
        case FACTORY_setWbGainOffset:
        {
            ALOGV("Receive FACTORY_setWbGainOffset\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            int eColorTemp = data.readInt32();
            short redGain = static_cast<short>(data.readInt32());
            short greenGain = static_cast<short>(data.readInt32());
            short blueGain = static_cast<short>(data.readInt32());
            short redOffset = static_cast<short>(data.readInt32());
            short greenOffset = static_cast<short>(data.readInt32());
            short blueOffset = static_cast<short>(data.readInt32());
            setWbGainOffset(eColorTemp,redGain,greenGain,blueGain,redOffset,greenOffset,blueOffset);
            return NO_ERROR;
        } break;
        case FACTORY_setWbGainOffsetEx:
        {
            ALOGV("Receive FACTORY_setWbGainOffsetEx\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            int eColorTemp = data.readInt32();
            int redGain = data.readInt32();
            int greenGain = data.readInt32();
            int blueGain = data.readInt32();
            int redOffset = data.readInt32();
            int greenOffset = data.readInt32();
            int blueOffset = data.readInt32();
            int enSrcType = data.readInt32();
            setWbGainOffsetEx(eColorTemp,redGain,greenGain,blueGain,redOffset,greenOffset,blueOffset,enSrcType);
            return NO_ERROR;
        } break;
        case FACTORY_storeDbToUsb:
        {
            ALOGV("Receive FACTORY_storeDbToUsb\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            reply->writeInt32(storeDbToUsb());
            return NO_ERROR;
        } break;
        case FACTORY_getFwVersion:
        {
            ALOGV("Receive FACTORY_getFwVersion\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            int32_t type = data.readInt32();
            reply->writeInt32(getFwVersion(type));
            return NO_ERROR;

        }break;

        case FACTORY_updateSscParameter:
        {
            ALOGV("Receive FACTORY_updateSscParameter\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            reply->writeInt32(updateSscParameter());
            return NO_ERROR;
        }break;
        case SETDEBUGMODE:
        {
            ALOGV("Receive SETDEBUGMODE\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            bool mode = static_cast<bool>(data.readInt32());
            setDebugMode(mode);
            return NO_ERROR;
        } break;
        case FACTORY_getSoftwareVersion:
        {
            ALOGV("Receive FACTORY_getSoftwareVersion\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            String8 version;
            getSoftwareVersion(version);
            reply->writeString8(version);
            return NO_ERROR;
        } break;
        case STOPTVSERVICE:
        {
            ALOGV("Receive STOPTVSERVICE\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            stopTvService();
            return NO_ERROR;
        } break;
        case FACTORY_restoreFactoryAtvProgramTable:
        {
            ALOGV("Receive FACTORY_restoreFactoryAtvProgramTable\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            short cityIndex = static_cast<short>(data.readInt32());
            restoreFactoryAtvProgramTable(cityIndex);
            return NO_ERROR;
        } break;
        case FACTORY_restoreFactoryDtvProgramTable:
        {
            ALOGV("Receive FACTORY_restoreFactoryDtvProgramTable\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            short cityIndex = static_cast<short>(data.readInt32());
            restoreFactoryDtvProgramTable(cityIndex);
            return NO_ERROR;
        } break;
        case FACTORY_setPQParameterViaUsbKey:
        {
            ALOGV("Receive FACTORY_setPQParameterViaUsbKey\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            setPQParameterViaUsbKey();
            return NO_ERROR;
        } break;
        case FACTORY_startUartDebug:
        {
            ALOGV("Receive FACTORY_setPQParameterViaUsbKey\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            reply->writeInt32( startUartDebug() );
            return NO_ERROR;
        } break;
        case FACTORY_uartSwitch:
        {
            ALOGV("Receive FACTORY_uartSwitch\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            reply->writeInt32( uartSwitch() );
            return NO_ERROR;
        } break;
        case FACTORY_READBYTESFROMI2C:
        {
            ALOGV("Receive FACTORY_READBYTESFROMI2C\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            int32_t u32gID = data.readInt32();
            uint8_t u8AddrSize = static_cast<uint8_t>(data.readInt32());
            uint16_t u16Size = static_cast<uint16_t>(data.readInt32());
            uint8_t *pu8Addr = new uint8_t[u8AddrSize];
            uint8_t *pu8Data = new uint8_t[u16Size];
            for (uint8_t i=0; i<u8AddrSize; i++)
            {
                pu8Addr[i] = static_cast<uint8_t>(data.readInt32());
            }
            bool ret = readBytesFromI2C(u32gID, u8AddrSize, pu8Addr, u16Size, pu8Data);
            for (uint8_t i=0; i<u16Size; i++)
            {
                reply->writeInt32(pu8Data[i]);
            }
            reply->writeInt32(ret);
            delete [] pu8Addr;
            delete [] pu8Data;
            return NO_ERROR;
        } break;

        case FACTORY_WRITEBYTESTOI2C:
        {
            ALOGV("Receive FACTORY_WRITEBYTESTOI2C\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            int32_t u32gID = data.readInt32();
            uint8_t u8AddrSize = static_cast<uint8_t>(data.readInt32());
            uint16_t u16Size = static_cast<uint16_t>(data.readInt32());
            uint8_t *pu8Addr = new uint8_t[u8AddrSize];
            uint8_t *pu8Data = new uint8_t[u16Size];
            for (uint8_t i=0; i<u8AddrSize; i++)
            {
                pu8Addr[i] = static_cast<uint8_t>(data.readInt32());
            }
            for (uint8_t i=0; i<u16Size; i++)
            {
                pu8Data[i] = static_cast<uint8_t>(data.readInt32());
            }
           reply->writeInt32(  writeBytesToI2C(u32gID, u8AddrSize, pu8Addr, u16Size, pu8Data ));

            delete [] pu8Addr;
            delete [] pu8Data;
            return NO_ERROR;
        } break;
        case FACTORY_getResolutionMappingIndex:
        {
            ALOGV("Receive FACTORY_getResolutionMappingIndex\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            int32_t enCurrentInputType = data.readInt32();
            reply->writeInt32( getResolutionMappingIndex(enCurrentInputType) );
            return NO_ERROR;
        } break;

        case FACTORY_SETENVPOWERMODE:
        {
            ALOGV("Receive FACTORY_SETENVPOWERMODE\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            int32_t ePowerMode = data.readInt32();
            reply->writeInt32(setEnvironmentPowerMode(ePowerMode));
            return NO_ERROR;
        } break;

        case FACTORY_GETENVPOWERMODE:
        {
            ALOGV("Receive FACTORY_GETENVPOWERONMUSIC\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            reply->writeInt32(getEnvironmentPowerMode());
            return NO_ERROR;
        } break;

        case FACTORY_SETENVPOWERVOLUME:
        {
             ALOGV("Receive FACTORY_SETENVPOWERVOLUME\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            int8_t volume = data.readInt32();
            reply->writeInt32(setEnvironmentPowerOnMusicVolume(volume));
            return NO_ERROR;
        } break;

        case FACTORY_GETENVPOWERVOLUME:
        {
            ALOGV("Receive FACTORY_GETENVPOWERVOLUME\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            reply->writeInt32(getEnvironmentPowerOnMusicVolume());
            return NO_ERROR;
        } break;

        case FACTORY_GETUPDATEPQFILEPATH:
        {
            ALOGV("Receive FACTORY_GETUPDATEPQFILEPATH\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            int32_t enumpqfile = data.readInt32();
            String8 filePath;
            bool ret = getUpdatePQFilePath(enumpqfile, filePath);
            reply->writeString8(filePath);
            reply->writeInt32(ret);
            return NO_ERROR;
        } break;

        case FACTORY_UPDATEPQINIFILES:
        {
            ALOGV("Receive FACTORY_UPDATEPQINIFILES\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            updatePQiniFiles();
            return NO_ERROR;
        } break;

        case FACTORY_GETPQVERSION:
        {
            ALOGV("Receive FACTORY_GETPQVERSION\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            int32_t escalerwindow = data.readInt32();
            reply->writeString8(getPQVersion(escalerwindow));
            return NO_ERROR;
        } break;

        case FACTORY_GETURSAVERSIONINFO:
        {
            ALOGV("Receive FACTORY_GETURSAVERSIONINFO\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            Ursa_Version_Info pVersion;
            bool ret = UrsaGetVersionInfo(&pVersion);
            reply->write(&pVersion, sizeof(Ursa_Version_Info));
            reply->writeInt32(ret);
            return NO_ERROR;
        }break;
        case FACTORY_SETWOLENABLESTATUS:
        {
            ALOGV("Receive FACTORY_SETWOLENABLESTATUS\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            bool flag = data.readInt32();
            setWOLEnableStatus(flag);
            return NO_ERROR;
        } break;

        case FACTORY_GETWOLENABLESTATUS:
        {
            ALOGV("Receive FACTORY_GETWOLENABLESTATUS\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            reply->writeInt32(getWOLEnableStatus());
            return NO_ERROR;
        } break;
        case FACTORY_SETXVYCCDATAFROMPANEL:
        {
            ALOGV("Receive FACTORY_SETXVYCCDATAFROMPANEL\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            float fRedX = data.readFloat();
            float fRedY = data.readFloat();
            float fGreenX = data.readFloat();
            float fGreenY = data.readFloat();
            float fBlueX = data.readFloat();
            float fBlueY = data.readFloat();
            float fWhiteX = data.readFloat();
            float fWhiteY = data.readFloat();
            int32_t eWin = data.readInt32();
            setXvyccDataFromPanel(fRedX, fRedY, fGreenX, fGreenY,
                                fBlueX, fBlueY, fWhiteX, fWhiteY, eWin);
            return NO_ERROR;
        } break;
        case FACTORY_GETENABLEIPINFO:
        {
            ALOGV("Receive FACTORY_GETENABLEIPINFO\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            int32_t sBitTableLen = data.readInt32();
            uint8_t BitTable[sBitTableLen];
            getEnableIPInfo(BitTable, sBitTableLen);
            reply->write(BitTable, sBitTableLen);
            return NO_ERROR;
        } break;
//-------------------------------------------------------------------------
        case FACTORY_GETAUTOFINEGAIN:
        {
            ALOGV("Receive FACTORY_GETAUTOFINEGAIN\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            reply->writeInt32(getAutoFineGain());
            return NO_ERROR;
        } break;

        case FACTORY_SETFIXEDFINEGAIN:
        {
            ALOGV("Receive FACTORY_GETAUTOFINEGAIN\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            uint8_t fineGain = data.readInt32();
            reply->writeInt32(setFixedFineGain(fineGain));
            return NO_ERROR;
        } break;
        case FACTORY_GETAUTORFGAIN:
        {
            ALOGV("Receive FACTORY_GETAUTOFINEGAIN\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            reply->writeInt32(getAutoRFGain());
            return NO_ERROR;
        } break;
        case FACTORY_SETRFGAIN:
        {
            ALOGV("Receive FACTORY_GETAUTOFINEGAIN\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            uint8_t rfGain = data.readInt32();
            reply->writeInt32(setRFGain(rfGain));
            return NO_ERROR;
        } break;
        case FACTORY_EosSetHDCPKey:
        {
            ALOGV("Receive FACTORY_EosSetHDCPKey\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            uint32_t size = data.readInt32();
            bool flag = data.readInt32();
            uint8_t *buffer = new uint8_t[size];
            for (uint32_t i=0; i<size; i++)
            {
                buffer[i] = static_cast<uint8_t>(data.readInt32());
            }
            reply->writeInt32(EosSetHDCPKey(buffer, size, flag));
            delete [] buffer;
            return NO_ERROR;
        } break;
        case FACTORY_EosGetHDCPKey:
        {
            ALOGV("Receive FACTORY_EosGetHDCPKey\n");
            CHECK_INTERFACE(IFactoryManager, data, reply);
            uint32_t size = data.readInt32();
            bool flag = data.readInt32();
            uint8_t *buffer = new uint8_t[size];
            bool ret = EosGetHDCPKey(buffer, size, flag);
            for (uint32_t i=0; i<size; i++)
            {
                reply->writeInt32(buffer[i]);
            }
            reply->writeInt32(ret);
            delete [] buffer;
            return NO_ERROR;
        } break;
       case FACTORY_getTunerStatus:
      {
             ALOGV("Receive FACTORY_getTunerStatus\n");
             CHECK_INTERFACE(IFactoryManager, data, reply);
            reply->writeInt32( getTunerStatus() );           
             return NO_ERROR;
          }break;
//-------------------------------------------------------------------------
        default:
            ALOGV("Receive unknown code(%08x)\n", code);
        return BBinder::onTransact(code, data, reply, flags);
    }
}

