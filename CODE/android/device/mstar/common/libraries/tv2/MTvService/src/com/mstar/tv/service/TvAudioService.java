//<MStar Software>
//******************************************************************************
// MStar Software
// Copyright (c) 2010 - 2014 MStar Semiconductor, Inc. All rights reserved.
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
//    supplied together with third party's software and the use of MStar
//    Software may require additional licenses from third parties.
//    Therefore, you hereby agree it is your sole responsibility to separately
//    obtain any and all third party right and license necessary for your use of
//    such third party's software.
//
// 3. MStar Software and any modification/derivatives thereof shall be deemed as
//    MStar's confidential information and you agree to keep MStar's
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
//    MStar Software in conjunction with your or your customer's product
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

package com.mstar.tv.service;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;

import com.mstar.android.tv.ITvAudio;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.DtvSoundEffect;
import com.mstar.android.tvapi.common.vo.EnumAdvancedSoundSubProcessType;
import com.mstar.android.tvapi.common.vo.EnumAdvancedSoundType;
import com.mstar.android.tvapi.common.vo.EnumAudioReturn;
import com.mstar.android.tvapi.common.vo.EnumAudioVolumeSourceType;
import com.mstar.android.tvapi.common.vo.EnumAuidoCaptureDeviceType;
import com.mstar.android.tvapi.common.vo.EnumAuidoCaptureSource;
import com.mstar.android.tvapi.common.vo.EnumDtvSoundMode;
import com.mstar.android.tvapi.common.vo.EnumSoundEffectType;
import com.mstar.android.tvapi.common.vo.EnumSoundMode;
import com.mstar.android.tvapi.common.vo.EnumSpdifType;
import com.mstar.android.tvapi.common.vo.EnumSurroundSystemType;
import com.mstar.android.tvapi.common.vo.MuteType.EnumMuteType;
import com.mstar.android.tvapi.common.vo.EnumAudioProcessorType;
import com.mstar.tv.service.IDatabaseDesk.UserSoundSetting;
import com.mstar.tv.service.IDatabaseDesk.UserSubtitleSetting;
import com.mstar.tv.service.IDatabaseDesk.SoundModeSeting;
import android.util.Log;

public class TvAudioService extends ITvAudio.Stub {

    private static final String TAG = "TvAudioService";

    private Context mContext = null;

    public TvAudioService(Context context) {
        mContext = context;
        TvManager.getInstance().getAudioManager().setOnEventListener(DeskAudioEventListener.getInstance());
    }

    @Override
    public int getADAbsoluteVolume() throws RemoteException {
        UserSoundSetting userSoundSetting = DatabaseDesk.getInstance(mContext).querySoundSetting();
        return (int) userSoundSetting.adVolume;
    }

    @Override
    public boolean getADEnable() throws RemoteException {
        UserSoundSetting userSoundSetting = DatabaseDesk.getInstance(mContext).querySoundSetting();
        return userSoundSetting.isADEnable;
    }

    @Override
    public boolean getAvcMode() throws RemoteException {
        return (DatabaseDesk.getInstance(mContext).queryAvc() == 1);
    }

    @Override
    public int getBalance() throws RemoteException {
        return DatabaseDesk.getInstance(mContext).queryBalance();
    }

    @Override
    public int getBass() throws RemoteException {
        int soundMode = DatabaseDesk.getInstance(mContext).querySoundMode();
        return DatabaseDesk.getInstance(mContext).queryBass(soundMode);
    }

    @Override
    public int getBassSwitch() throws RemoteException {
        return DatabaseDesk.getInstance(mContext).queryBassSwitch();
    }

    @Override
    public int getBassVolume() throws RemoteException {
        return DatabaseDesk.getInstance(mContext).queryBassVolume();
    }

    @Override
    public int getDtvOutputMode() throws RemoteException {
        // TODO: null pointer handling

        EnumDtvSoundMode dtvSoundMode = EnumDtvSoundMode.E_MIXED;
        try {
            dtvSoundMode = TvManager.getInstance().getAudioManager().getDtvOutputMode();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return dtvSoundMode.ordinal();
    }

    @Override
    public int getEarPhoneVolume() throws RemoteException {
        return DatabaseDesk.getInstance(mContext).queryEarPhoneVolme();
    }

    @Override
    public int getEqBand10k() throws RemoteException {
        int soundMode = DatabaseDesk.getInstance(mContext).querySoundMode();
        return DatabaseDesk.getInstance(mContext).querySoundModeSetting(soundMode).eqBand5;
    }

    @Override
    public int getEqBand120() throws RemoteException {
        int soundMode = DatabaseDesk.getInstance(mContext).querySoundMode();
        return DatabaseDesk.getInstance(mContext).querySoundModeSetting(soundMode).eqBand1;
    }

    @Override
    public int getEqBand1500() throws RemoteException {
        int soundMode = DatabaseDesk.getInstance(mContext).querySoundMode();
        return DatabaseDesk.getInstance(mContext).querySoundModeSetting(soundMode).eqBand3;
    }

    @Override
    public int getEqBand500() throws RemoteException {
        int soundMode = DatabaseDesk.getInstance(mContext).querySoundMode();
        return DatabaseDesk.getInstance(mContext).querySoundModeSetting(soundMode).eqBand2;
    }

    @Override
    public int getEqBand5k() throws RemoteException {
        int soundMode = DatabaseDesk.getInstance(mContext).querySoundMode();
        return DatabaseDesk.getInstance(mContext).querySoundModeSetting(soundMode).eqBand4;
    }

    @Override
    public boolean getHOHStatus() throws RemoteException {
        boolean bHOHStatus = false;
        UserSubtitleSetting uss = DatabaseDesk.getInstance(mContext).queryUserSubtitleSetting();
        bHOHStatus = uss.isHoHEnable;

        return bHOHStatus;
    }

    @Override
    public int getPowerOnOffMusic() throws RemoteException {
        return DatabaseDesk.getInstance(mContext).queryPowerOnMusic();
    }

    @Override
    public int getSeparateHear() throws RemoteException {
        return DatabaseDesk.getInstance(mContext).querySeparateHearing();
    }

    @Override
    public int getSoundMode() throws RemoteException {
        return DatabaseDesk.getInstance(mContext).querySoundMode();
    }

    @Override
    public int getSpdifOutMode() throws RemoteException {
        return DatabaseDesk.getInstance(mContext).querySpdifMode();
    }

    @Override
    public int getSurroundMode() throws RemoteException {
        return DatabaseDesk.getInstance(mContext).querySrr();
    }

    @Override
    public int getTreble() throws RemoteException {
        int soundMode = DatabaseDesk.getInstance(mContext).querySoundMode();
        return DatabaseDesk.getInstance(mContext).queryTreble(soundMode);
    }

    @Override
    public int getTrueBass() throws RemoteException {
        return DatabaseDesk.getInstance(mContext).queryTrueBass();
    }

    @Override
    public int getWallmusic() throws RemoteException {
        return DatabaseDesk.getInstance(mContext).queryWallmusic();
    }

    @Override
    public void registerOnAudioEventListener(int listener) throws RemoteException {
        // TODO: not implemented on main trunk
        // why args is "integer" ???

    }

    @Override
    public void setADAbsoluteVolume(int volume) throws RemoteException {
        UserSoundSetting userSoundSetting = DatabaseDesk.getInstance(mContext).querySoundSetting();
        userSoundSetting.adVolume = (short) volume;
        TvManager.getInstance().getAudioManager().setADAbsoluteVolume(volume);
        DatabaseDesk.getInstance(mContext).updateSoundSetting(userSoundSetting);
    }

    @Override
    public void setADEnable(boolean enable) throws RemoteException {
        UserSoundSetting userSoundSetting = DatabaseDesk.getInstance(mContext).querySoundSetting();
        userSoundSetting.isADEnable = enable;
        TvManager.getInstance().getAudioManager().setADEnable(enable);
        DatabaseDesk.getInstance(mContext).updateSoundSetting(userSoundSetting);
    }

    @Override
    public int setAudioCaptureSource(int eAudioCaptureDeviceType, int eAudioCaptureSource) {
        EnumAudioReturn audioreturn = EnumAudioReturn.E_RETURN_NOTOK;
        EnumAuidoCaptureDeviceType devType = EnumAuidoCaptureDeviceType.values()[eAudioCaptureDeviceType];
        EnumAuidoCaptureSource captureSrc = EnumAuidoCaptureSource.values()[eAudioCaptureSource];
        try {
            audioreturn = TvManager.getInstance().getAudioManager()
                    .setAudioCaptureSource(devType, captureSrc);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return audioreturn.ordinal();
    }

    @Override
    public boolean setAvcMode(boolean isEnable) throws RemoteException {
        EnumAudioReturn ret = EnumAudioReturn.E_RETURN_NOTOK;

        try {
            ret = TvManager.getInstance().getAudioManager()
                    .enableBasicSoundEffect(EnumSoundEffectType.E_AVC, isEnable);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        if (ret == EnumAudioReturn.E_RETURN_OK) {
            DatabaseDesk.getInstance(mContext).updateAvc(isEnable ? 1 : 0);
        }
        return (ret == EnumAudioReturn.E_RETURN_OK) ? true : false;
    }

    @Override
    public boolean setBalance(int balanceValue) throws RemoteException {
        EnumAudioReturn ret = EnumAudioReturn.E_RETURN_NOTOK;

        DtvSoundEffect dtvSoundEff = new DtvSoundEffect();

        try {
            dtvSoundEff.balance = balanceValue;

            ret = TvManager.getInstance().getAudioManager()
                    .setBasicSoundEffect(EnumSoundEffectType.E_BALANCE, dtvSoundEff);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }

        if (ret == EnumAudioReturn.E_RETURN_OK) {
            DatabaseDesk.getInstance(mContext).updateBalance(balanceValue);
        }
        return (ret == EnumAudioReturn.E_RETURN_OK) ? true : false;
    }

    @Override
    public boolean setBass(int bassValue) throws RemoteException {
        EnumAudioReturn ret = EnumAudioReturn.E_RETURN_NOTOK;

        DtvSoundEffect dtvSoundEff = new DtvSoundEffect();

        int soundMode = DatabaseDesk.getInstance(mContext).querySoundMode();
        SoundModeSeting model = DatabaseDesk.getInstance(mContext).querySoundModeSetting(soundMode);

        try {
            dtvSoundEff.soundParameterEqs[0].eqLevel = (int) bassValue;
            dtvSoundEff.soundParameterEqs[1].eqLevel = (int) (model.eqBand2);
            dtvSoundEff.soundParameterEqs[2].eqLevel = (int) (model.eqBand3);
            dtvSoundEff.soundParameterEqs[3].eqLevel = (int) (model.eqBand4);
            dtvSoundEff.soundParameterEqs[4].eqLevel = (int) (model.eqBand5);
            dtvSoundEff.eqBandNumber = 5;
            model.eqBand1 = (short) bassValue;
            // For display
            model.bass = (short) bassValue;

            ret = TvManager.getInstance().getAudioManager()
                    .setBasicSoundEffect(EnumSoundEffectType.E_EQ, dtvSoundEff);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }

        if (ret == EnumAudioReturn.E_RETURN_OK) {
            DatabaseDesk.getInstance(mContext).updateSoundModeSetting(model, soundMode);
        }
        return (ret == EnumAudioReturn.E_RETURN_OK) ? true : false;
    }

    @Override
    public boolean setBassSwitch(int isEnable) throws RemoteException {
        // TODO: why input parameter is integer ??
        // Doesn't need to set something to TVOS ??
        // fix always return true

        DatabaseDesk.getInstance(mContext).updateBassSwitch(isEnable);
        return true;
    }

    @Override
    public boolean setBassVolume(int volumeValue) throws RemoteException {
        // TODO: doesn't need to set something to tvos?
        // fix always return true

        DatabaseDesk.getInstance(mContext).updateBassVolume(volumeValue);
        return true;
    }

    @Override
    public void setDtvOutputMode(int outputMode) throws RemoteException {
        EnumDtvSoundMode dtvSoundMode = EnumDtvSoundMode.values()[outputMode];
        try {
            TvManager.getInstance().getAudioManager().setDtvOutputMode(dtvSoundMode);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean setEarPhoneVolume(int volume, boolean saveDb) throws RemoteException {
        // TODO: return void from setAudioVolume, why we need to return boolean
        // ??

        try {
            TvManager.getInstance().getAudioManager()
                    .setAudioVolume(EnumAudioVolumeSourceType.E_VOL_SOURCE_HP_OUT, (byte) volume);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        if (saveDb) {
            DatabaseDesk.getInstance(mContext).updateEarPhoneVolume(volume);
        }
        return true;
    }

    @Override
    public boolean setEqBand10k(int eqValue) throws RemoteException {
        //TODO: thinking align data structure of database(SoundModeSeting) & tvapi (DtvSoundEffect)
        EnumAudioReturn ret = EnumAudioReturn.E_RETURN_NOTOK;

        int soundMode = DatabaseDesk.getInstance(mContext).querySoundMode();
        SoundModeSeting model = DatabaseDesk.getInstance(mContext).querySoundModeSetting(soundMode);
        model.eqBand5 = (short) eqValue;
        DtvSoundEffect dtvSoundEff = new DtvSoundEffect();
        dtvSoundEff.eqBandNumber = (short) EnumSoundMode.E_NUM.ordinal();
        try {
            dtvSoundEff.soundParameterEqs[0].eqLevel = (int) (model.eqBand1);
            dtvSoundEff.soundParameterEqs[1].eqLevel = (int) (model.eqBand2);
            dtvSoundEff.soundParameterEqs[2].eqLevel = (int) (model.eqBand3);
            dtvSoundEff.soundParameterEqs[3].eqLevel = (int) (model.eqBand4);
            dtvSoundEff.soundParameterEqs[4].eqLevel = (int) (model.eqBand5);
            dtvSoundEff.eqBandNumber = 5;
            ret = TvManager.getInstance().getAudioManager()
                    .setBasicSoundEffect(EnumSoundEffectType.E_EQ, dtvSoundEff);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }

        if (ret == EnumAudioReturn.E_RETURN_OK) {
            DatabaseDesk.getInstance(mContext).updateSoundModeSetting(model, soundMode);
        }
        return (ret == EnumAudioReturn.E_RETURN_OK) ? true : false;
    }

    @Override
    public boolean setEqBand120(int eqValue) throws RemoteException {
        //TODO: thinking align data structure of database(SoundModeSeting) & tvapi (DtvSoundEffect)
        EnumAudioReturn ret = EnumAudioReturn.E_RETURN_NOTOK;

        int soundMode = DatabaseDesk.getInstance(mContext).querySoundMode();
        SoundModeSeting model = DatabaseDesk.getInstance(mContext).querySoundModeSetting(soundMode);
        model.eqBand1 = (short) eqValue;
        DtvSoundEffect dtvSoundEff = new DtvSoundEffect();
        dtvSoundEff.eqBandNumber = (short) EnumSoundMode.E_NUM.ordinal();
        try {
            dtvSoundEff.soundParameterEqs[0].eqLevel = (int) (model.eqBand1);
            dtvSoundEff.soundParameterEqs[1].eqLevel = (int) (model.eqBand2);
            dtvSoundEff.soundParameterEqs[2].eqLevel = (int) (model.eqBand3);
            dtvSoundEff.soundParameterEqs[3].eqLevel = (int) (model.eqBand4);
            dtvSoundEff.soundParameterEqs[4].eqLevel = (int) (model.eqBand5);
            dtvSoundEff.eqBandNumber = 5;

            ret = TvManager.getInstance().getAudioManager()
                    .setBasicSoundEffect(EnumSoundEffectType.E_EQ, dtvSoundEff);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }

        if (ret == EnumAudioReturn.E_RETURN_OK) {
            DatabaseDesk.getInstance(mContext).updateSoundModeSetting(model, soundMode);
        }
        return true;
    }

    @Override
    public boolean setEqBand1500(int eqValue) throws RemoteException {
        //TODO: thinking align data structure of database(SoundModeSeting) & tvapi (DtvSoundEffect)
        EnumAudioReturn ret = EnumAudioReturn.E_RETURN_NOTOK;

        int soundMode = DatabaseDesk.getInstance(mContext).querySoundMode();
        SoundModeSeting model = DatabaseDesk.getInstance(mContext).querySoundModeSetting(soundMode);
        model.eqBand3 = (short) eqValue;
        DtvSoundEffect dtvSoundEff = new DtvSoundEffect();
        dtvSoundEff.eqBandNumber = (short) EnumSoundMode.E_NUM.ordinal();
        try {
            dtvSoundEff.soundParameterEqs[0].eqLevel = (int) (model.eqBand1);
            dtvSoundEff.soundParameterEqs[1].eqLevel = (int) (model.eqBand2);
            dtvSoundEff.soundParameterEqs[2].eqLevel = (int) (model.eqBand3);
            dtvSoundEff.soundParameterEqs[3].eqLevel = (int) (model.eqBand4);
            dtvSoundEff.soundParameterEqs[4].eqLevel = (int) (model.eqBand5);
            dtvSoundEff.eqBandNumber = 5;

            ret = TvManager.getInstance().getAudioManager()
                    .setBasicSoundEffect(EnumSoundEffectType.E_EQ, dtvSoundEff);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }

        if (ret == EnumAudioReturn.E_RETURN_OK) {
            DatabaseDesk.getInstance(mContext).updateSoundModeSetting(model, soundMode);
        }
        return (ret == EnumAudioReturn.E_RETURN_OK) ? true : false;
    }

    @Override
    public boolean setEqBand500(int eqValue) throws RemoteException {
        //TODO: thinking align data structure of database(SoundModeSeting) & tvapi (DtvSoundEffect)
        EnumAudioReturn ret = EnumAudioReturn.E_RETURN_NOTOK;

        int soundMode = DatabaseDesk.getInstance(mContext).querySoundMode();
        SoundModeSeting model = DatabaseDesk.getInstance(mContext).querySoundModeSetting(soundMode);
        model.eqBand2 = (short) eqValue;
        DtvSoundEffect dtvSoundEff = new DtvSoundEffect();
        dtvSoundEff.eqBandNumber = (short) EnumSoundMode.E_NUM.ordinal();
        try {
            dtvSoundEff.soundParameterEqs[0].eqLevel = (int) (model.eqBand1);
            dtvSoundEff.soundParameterEqs[1].eqLevel = (int) (model.eqBand2);
            dtvSoundEff.soundParameterEqs[2].eqLevel = (int) (model.eqBand3);
            dtvSoundEff.soundParameterEqs[3].eqLevel = (int) (model.eqBand4);
            dtvSoundEff.soundParameterEqs[4].eqLevel = (int) (model.eqBand5);

            dtvSoundEff.eqBandNumber = 5;
            ret = TvManager.getInstance().getAudioManager()
                    .setBasicSoundEffect(EnumSoundEffectType.E_EQ, dtvSoundEff);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }

        if (ret == EnumAudioReturn.E_RETURN_OK) {
            DatabaseDesk.getInstance(mContext).updateSoundModeSetting(model, soundMode);
        }
        return (ret == EnumAudioReturn.E_RETURN_OK) ? true : false;
    }

    @Override
    public boolean setEqBand5k(int eqValue) throws RemoteException {
        //TODO: thinking align data structure of database(SoundModeSeting) & tvapi (DtvSoundEffect)
        EnumAudioReturn ret = EnumAudioReturn.E_RETURN_NOTOK;

        int soundMode = DatabaseDesk.getInstance(mContext).querySoundMode();
        SoundModeSeting model = DatabaseDesk.getInstance(mContext).querySoundModeSetting(soundMode);
        model.eqBand4 = (short) eqValue;
        DtvSoundEffect dtvSoundEff = new DtvSoundEffect();
        dtvSoundEff.eqBandNumber = (short) EnumSoundMode.E_NUM.ordinal();
        try {
            dtvSoundEff.soundParameterEqs[0].eqLevel = (int) (model.eqBand1);
            dtvSoundEff.soundParameterEqs[1].eqLevel = (int) (model.eqBand2);
            dtvSoundEff.soundParameterEqs[2].eqLevel = (int) (model.eqBand3);
            dtvSoundEff.soundParameterEqs[3].eqLevel = (int) (model.eqBand4);
            dtvSoundEff.soundParameterEqs[4].eqLevel = (int) (model.eqBand5);
            dtvSoundEff.eqBandNumber = 5;

            ret = TvManager.getInstance().getAudioManager()
                    .setBasicSoundEffect(EnumSoundEffectType.E_EQ, dtvSoundEff);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }

        if (ret == EnumAudioReturn.E_RETURN_OK) {
            DatabaseDesk.getInstance(mContext).updateSoundModeSetting(model, soundMode);
        }
        return (ret == EnumAudioReturn.E_RETURN_OK) ? true : false;
    }

    @Override
    public void setHOHStatus(boolean state) throws RemoteException {
        TvManager.getInstance().getAudioManager().setAutoHOHEnable(state);
    }

    @Override
    public boolean setPowerOnOffMusic(int value) throws RemoteException {
        // TODO: the column "PoweronMusic" doesn't exist...
        // Doesn't need to set something to TVOS??

        DatabaseDesk.getInstance(mContext).updatePowerOnMusic(value);
        return true;
    }

    @Override
    public boolean setSeparateHear(int value) throws RemoteException {
        // TODO: the column "SeparateHearing" doesn't exist...
        // Doesn't need to set something to TVOS??

        DatabaseDesk.getInstance(mContext).updateSeparateHearing(value);
        return false;
    }

    @Override
    public boolean setSoundMode(int soundMode) throws RemoteException {
        //TODO: thinking align data structure of database(SoundModeSeting) & tvapi (DtvSoundEffect)
        EnumAudioReturn ret = EnumAudioReturn.E_RETURN_NOTOK;

        DtvSoundEffect dtvSoundEff = new DtvSoundEffect();

        SoundModeSeting model = DatabaseDesk.getInstance(mContext).querySoundModeSetting(soundMode);

        dtvSoundEff.eqBandNumber = (short) EnumSoundMode.E_NUM.ordinal();
        try {
            dtvSoundEff.soundParameterEqs[0].eqLevel = (int) (model.eqBand1);
            dtvSoundEff.soundParameterEqs[1].eqLevel = (int) (model.eqBand2);
            dtvSoundEff.soundParameterEqs[2].eqLevel = (int) (model.eqBand3);
            dtvSoundEff.soundParameterEqs[3].eqLevel = (int) (model.eqBand4);
            dtvSoundEff.soundParameterEqs[4].eqLevel = (int) (model.eqBand5);
            dtvSoundEff.eqBandNumber = 5;

            ret = TvManager.getInstance().getAudioManager()
                    .setBasicSoundEffect(EnumSoundEffectType.E_EQ, dtvSoundEff);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }

        if (ret == EnumAudioReturn.E_RETURN_OK) {
            DatabaseDesk.getInstance(mContext).updateSoundMode(soundMode);
        }
        return (ret == EnumAudioReturn.E_RETURN_OK) ? true : false;
    }

    @Override
    public boolean setSpdifOutMode(int SpdifMode) throws RemoteException {
        // TODO: fix always return true, but it's deep inside mapi...

        try {
            TvManager.getInstance().getAudioManager()
                    .setDigitalOut(EnumSpdifType.values()[SpdifMode]);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }

        DatabaseDesk.getInstance(mContext).updateSpdifMode(SpdifMode);

        try {
            TvManager.getInstance().getPlayerManager()
            .changeSetting(TvManager.SETTING_CHANGE_AUDIO_SPDIF_MODE_CHANGE);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean setSurroundMode(int surroundMode) throws RemoteException {
        EnumAudioReturn ret = EnumAudioReturn.E_RETURN_NOTOK;

        try {
            ret = TvManager.getInstance().getAudioManager()
                    .enableBasicSoundEffect(EnumSoundEffectType.E_Surround, surroundMode == 1);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }

        if (ret == EnumAudioReturn.E_RETURN_OK) {
            DatabaseDesk.getInstance(mContext).updateSrr(surroundMode);
        }
        return (ret == EnumAudioReturn.E_RETURN_OK) ? true : false;
    }

    @Override
    public boolean setTreble(int trebleValue) throws RemoteException {
        //TODO: thinking align data structure of database(SoundModeSeting) & tvapi (DtvSoundEffect)
        EnumAudioReturn ret = EnumAudioReturn.E_RETURN_NOTOK;

        DtvSoundEffect dtvSoundEff = new DtvSoundEffect();

        int soundMode = DatabaseDesk.getInstance(mContext).querySoundMode();
        SoundModeSeting model = DatabaseDesk.getInstance(mContext).querySoundModeSetting(soundMode);

        try {
            dtvSoundEff.soundParameterEqs[0].eqLevel = (int) (model.eqBand1);
            dtvSoundEff.soundParameterEqs[1].eqLevel = (int) (model.eqBand2);
            dtvSoundEff.soundParameterEqs[2].eqLevel = (int) (model.eqBand3);
            dtvSoundEff.soundParameterEqs[3].eqLevel = (int) (model.eqBand4);
            dtvSoundEff.soundParameterEqs[4].eqLevel = (int) trebleValue;
            dtvSoundEff.eqBandNumber = 5;
            model.eqBand5 = (short) trebleValue;
            // For display
            model.treble = (short) trebleValue;

            ret = TvManager.getInstance().getAudioManager()
                    .setBasicSoundEffect(EnumSoundEffectType.E_EQ, dtvSoundEff);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }

        if (ret == EnumAudioReturn.E_RETURN_OK) {
            DatabaseDesk.getInstance(mContext).updateSoundModeSetting(model, soundMode);
        }
        return (ret == EnumAudioReturn.E_RETURN_OK) ? true : false;
    }

    @Override
    public boolean setTrueBass(int value) throws RemoteException {

        DatabaseDesk.getInstance(mContext).updateTrueBass(value);
        return true;
    }

    @Override
    public boolean setWallmusic(int value) throws RemoteException {

        DatabaseDesk.getInstance(mContext).updateWallmusic(value);
        return true;
    }

    @Override
    public void enableSRS(boolean isEnable) throws RemoteException {
        EnumAudioReturn audioreturn = EnumAudioReturn.E_RETURN_NOTOK;
        boolean bRet = true;

        try {
            if (true == isEnable) {
                audioreturn = TvManager
                        .getInstance()
                        .getAudioManager()
                        .enableAdvancedSoundEffect(EnumAdvancedSoundType.E_SRS_TSHD,
                                EnumAdvancedSoundSubProcessType.E_SRS_TSHD_DEFINITION_ON);
                bRet = bRet && (EnumAudioReturn.E_RETURN_OK == audioreturn);
                audioreturn = TvManager
                        .getInstance()
                        .getAudioManager()
                        .enableAdvancedSoundEffect(EnumAdvancedSoundType.E_SRS_TSHD,
                                EnumAdvancedSoundSubProcessType.E_SRS_TSHD_SRS3D_ON);
                bRet = bRet && (EnumAudioReturn.E_RETURN_OK == audioreturn);
                audioreturn = TvManager
                        .getInstance()
                        .getAudioManager()
                        .enableAdvancedSoundEffect(EnumAdvancedSoundType.E_SRS_TSHD,
                                EnumAdvancedSoundSubProcessType.E_SRS_TSHD_TRUEBASS_ON);
                bRet = bRet && (EnumAudioReturn.E_RETURN_OK == audioreturn);
            } else {
                /* This Disbled ALL SRS effect, the 2nd parameter is ignored if 1st paramenter is E_NONE */
                audioreturn = TvManager
                        .getInstance()
                        .getAudioManager()
                        .enableAdvancedSoundEffect(EnumAdvancedSoundType.E_NONE,
                                EnumAdvancedSoundSubProcessType.E_NO_SUBPROC);
                bRet = bRet && (EnumAudioReturn.E_RETURN_OK == audioreturn);
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }

        if (true == bRet) {
            UserSoundSetting userSoundSetting = DatabaseDesk.getInstance(mContext)
                    .querySoundSetting();
            userSoundSetting.eSurroundSoundMode = isEnable ? EnumSurroundSystemType.E_SRS
                    : EnumSurroundSystemType.E_OFF;
            DatabaseDesk.getInstance(mContext).updateSoundSetting(userSoundSetting);
        }
    }

    @Override
    public int enableMute(int enMuteType) throws RemoteException {
        EnumAudioReturn audioreturn = EnumAudioReturn.E_RETURN_NOTOK;
        EnumMuteType mutetype = EnumMuteType.values()[enMuteType];
        try {
            audioreturn = TvManager.getInstance().getAudioManager().enableMute(mutetype);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return audioreturn.ordinal();
    }

    @Override
    public int disableMute(int enMuteType) throws RemoteException {
        EnumAudioReturn audioreturn = EnumAudioReturn.E_RETURN_NOTOK;
        EnumMuteType mutetype = EnumMuteType.values()[enMuteType];
        try {
            audioreturn = TvManager.getInstance().getAudioManager().disableMute(mutetype);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return audioreturn.ordinal();
    }

    @Override
    public boolean isSRSEnable() {
        UserSoundSetting userSoundSetting = DatabaseDesk.getInstance(mContext).querySoundSetting();
        return ((userSoundSetting.eSurroundSoundMode.ordinal() == 0) ? false : true);
    }

    @Override
    public void setSpeakerVolume(int volume) throws RemoteException {
        try {
            TvManager
                    .getInstance()
                    .getAudioManager()
                    .setAudioVolume(EnumAudioVolumeSourceType.E_VOL_SOURCE_SPEAKER_OUT,
                            (byte) volume);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }

        DatabaseDesk.getInstance(mContext).updateVolume(volume);
    }

    @Override
    public int getSpeakerVolume() throws RemoteException {
        return DatabaseDesk.getInstance(mContext).queryVolume();
    }

    @Override
    public int getSoundSpeakerDelay() throws RemoteException {
        UserSoundSetting userSoundSetting = DatabaseDesk.getInstance(mContext).querySoundSetting();
        return (int) userSoundSetting.speakDelay;
    }

    @Override
    public void setSoundSpeakerDelay(int delay) throws RemoteException {
        TvManager.getInstance().getAudioManager().setSoundSpeakerDelay(delay);
        DatabaseDesk.getInstance(mContext).updateSpeakerDelay(delay);
    }

    @Override
    public int getSoundSpdifDelay() throws RemoteException {
        UserSoundSetting userSoundSetting = DatabaseDesk.getInstance(mContext).querySoundSetting();
        return (int) userSoundSetting.spdifDelay;
    }

    @Override
    public void setSoundSpdifDelay(int delay) throws RemoteException {
        TvManager.getInstance().getAudioManager().setSoundSpdifDelay(delay);
        DatabaseDesk.getInstance(mContext).updateSpdifDelay(delay);
    }

    @Override
    public void setHDMITx_HDByPass(boolean enable) throws RemoteException {
        try {
            TvManager.getInstance().getAudioManager().setHDMITx_HDBypass(enable);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean getHDMITx_HDByPass() throws RemoteException {
        try {
            return TvManager.getInstance().getAudioManager().getHDMITx_HDBypass();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isSupportHDMITx_HDByPassMode() throws RemoteException {
        try {
            return TvManager.getInstance().getAudioManager().getHDMITx_HDBypass_Capability();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int setOutputSourceInfo(int enAudioPath, int enSource) throws RemoteException {
        EnumAudioReturn audioreturn = EnumAudioReturn.E_RETURN_NOTOK;
        EnumAudioVolumeSourceType audiopath = EnumAudioVolumeSourceType.values()[enAudioPath];
        EnumAudioProcessorType source = EnumAudioProcessorType.values()[enSource];

        try {
            audioreturn = TvManager.getInstance().getAudioManager().setOutputSourceInfo(audiopath, source);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
	return audioreturn.ordinal();
    }

    @Override
    public void addClient(IBinder client) throws RemoteException {
        DeskAudioEventListener.getInstance().addClient(client);
    }

    @Override
    public void removeClient(IBinder client) throws RemoteException {
        DeskAudioEventListener.getInstance().removeClient(client);
    }
	
	// EosTek Patch Begin
	@Override
    public boolean setSoundModeAll(int SoundMode, int bass, int treble) throws RemoteException {
        Log.d(TAG, "setSoundMode(), paras SoundMode = " + SoundMode);
        DtvSoundEffect dtvSoundEff = new DtvSoundEffect();
		
        SoundModeSeting model = DatabaseDesk.getInstance(mContext).querySoundModeSetting(SoundMode);

        dtvSoundEff.eqBandNumber = (short) EnumSoundMode.E_NUM.ordinal();
        try {
			model.eqBand1 = (short) bass;
            dtvSoundEff.soundParameterEqs[0].eqLevel = (int) (model.eqBand1);
            dtvSoundEff.soundParameterEqs[1].eqLevel = (int) (model.eqBand2);
            dtvSoundEff.soundParameterEqs[2].eqLevel = (int) (model.eqBand3);
            dtvSoundEff.soundParameterEqs[3].eqLevel = (int) (model.eqBand4);
			model.eqBand5 = (short) treble;
            dtvSoundEff.soundParameterEqs[4].eqLevel = (int) (model.eqBand5);
            dtvSoundEff.eqBandNumber = 5;

            if (TvManager.getInstance()!=null) {
                TvManager.getInstance().getAudioManager().setBasicSoundEffect(EnumSoundEffectType.E_EQ, dtvSoundEff);
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }

        DatabaseDesk.getInstance(mContext).updateSoundModeAll(SoundMode, bass, treble);
        return true;

    }
	
	@Override
    public int getBassBySoundMode(int soundMode) throws RemoteException {
        int i = DatabaseDesk.getInstance(mContext).queryBass(soundMode);
        Log.d(TAG, "getBass(), return int " + i);
        return i;
    }
	
	@Override
    public int getTrebleBySoundMode(int soundMode) throws RemoteException {
        int i = DatabaseDesk.getInstance(mContext).queryTreble(soundMode);
        Log.d(TAG, "getTreble(), return int " + i);
        return i;
    }	
	// EosTek Patch End
}
