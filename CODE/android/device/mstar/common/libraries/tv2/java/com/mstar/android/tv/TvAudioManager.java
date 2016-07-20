//<MStar Software>
//******************************************************************************
// MStar Software
// Copyright (c) 2010 - 2015 MStar Semiconductor, Inc. All rights reserved.
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

package com.mstar.android.tv;

import java.util.ArrayList;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.mstar.android.tv.IEventClient;
import com.mstar.android.tv.ITvAudio;
import com.mstar.android.tvapi.common.AudioManager;
import com.mstar.android.tvapi.common.vo.EnumOnOffType;
import com.mstar.android.tvapi.common.vo.EnumDtvSoundMode;
import com.mstar.android.tvapi.common.vo.EnumSoundMode;
import com.mstar.android.tvapi.common.vo.EnumSpdifType;
import com.mstar.android.tvapi.common.vo.EnumSurroundMode;
import com.mstar.android.tvapi.common.vo.EnumAudioReturn;
import com.mstar.android.tvapi.common.vo.EnumAuidoCaptureDeviceType;
import com.mstar.android.tvapi.common.vo.EnumAuidoCaptureSource;
import com.mstar.android.tvapi.common.vo.MuteType.EnumMuteType;
import com.mstar.android.tvapi.common.listener.OnAudioEventListener;
import com.mstar.android.tvapi.common.exception.TvCommonException;

/**
 * <b>TvAudioManager class is for purpose of controlling audio module
 * from client APK.</b><br/>
 */
public class TvAudioManager extends IEventClient.Stub {
    private final static String TAG = "TvAudioManager";

    /* This value is mapping to EN_SOUND_MODE */
    /**
     * Sound Mode , standard.
     */
    public static final int SOUND_MODE_STANDARD = 0;
    /**
     * Sound Mode , music.
     */
    public static final int SOUND_MODE_MUSIC = 1;
    /**
     * Sound Mode , movie.
     */
    public static final int SOUND_MODE_MOVIE = 2;
    /**
     * Sound Mode , sports.
     */
    public static final int SOUND_MODE_SPORTS = 3;
    /**
     * Sound Mode , user.
     */
    public static final int SOUND_MODE_USER = 4;
    /**
     * Sound Mode , onsite1.
     */
    public static final int SOUND_MODE_ONSITE1 = 5;
    /**
     * Sound Mode , onsite2.
     */
    public static final int SOUND_MODE_ONSITE2 = 6;

    /* This value is mapping to EN_SURROUND_MODE */
    /**
     * Surround mode type,  OFF
     */
    public static final int SURROUND_MODE_OFF = 0;
    /**
     * Surround mode type,  ON
     */
    public static final int SURROUND_MODE_ON = 1;

    /* This value is mapping to EN_SPDIF_TYPE */
    /**
     * SPDIF output type,  PCM
     */
    public static final int SPDIF_TYPE_PCM = 0;
    /**
     * SPDIF output software OFF, don't use
     */
    public static final int SPDIF_TYPE_OFF = 1;
    /**
     * SPDIF output type, nonPCM
     */
    public static final int SPDIF_TYPE_NONPCM = 2;

    /* This value is mapping to EN_ON_OFF_TYPE */
    /**
     * on/off type,  OFF
     */
    public static final int ON_OFF_TYPE_OFF = 0;
    /**
     * on/off type,  ON
     */
    public static final int ON_OFF_TYPE_ON = 1;

    /* This value is mapping to EN_DTV_SOUND_MODE */
    /**
     * dtv sound mode,  stereo
     */
    public static final int DTV_SOUND_MODE_STEREO = 0;
    /**
     * dtv sound mode,  left
     */
    public static final int DTV_SOUND_MODE_LEFT = 1;
    /**
     * dtv sound mode,  right
     */
    public static final int DTV_SOUND_MODE_RIGHT = 2;
    /**
     * dtv sound mode,  mixed
     */
    public static final int DTV_SOUND_MODE_MIXED = 3;

    /* This value is mapping to EN_AUDIO_RETURN */
    /**
     * Audio Function Return Value Type,  notOk
     */
    public static final int AUDIO_RETURN_NOT_OK = 0;
    /**
     * Audio Function Return Value Type, Ok
     */
    public static final int AUDIO_RETURN_OK = 1;
    /**
     * Audio Function Return Value Type, unsupport format
     */
    public static final int AUDIO_RETURN_UNSUPPORT = 2;

    /* This value is mapping to EN_AUDIO_CAPTURE_DEVICE_TYPE */
    /**
     * Audio Data Capture Device Selection,  device0
     */
    public static final int AUDIO_CAPTURE_DEVICE_TYPE_DEVICE0 = 0;
    /**
     * Audio Data Capture Device Selection,  device1
     */
    public static final int AUDIO_CAPTURE_DEVICE_TYPE_DEVICE1 = 1;
    /**
     * Audio Data Capture Device Selection,  device2
     */
    public static final int AUDIO_CAPTURE_DEVICE_TYPE_DEVICE2 = 2;
    /**
     * Audio Data Capture Device Selection,  device3
     */
    public static final int AUDIO_CAPTURE_DEVICE_TYPE_DEVICE3 = 3;
    /**
     * Audio Data Capture Device Selection,  device4
     */
    public static final int AUDIO_CAPTURE_DEVICE_TYPE_DEVICE4 = 4;
    /**
     * Audio Data Capture Device Selection,  device5
     */
    public static final int AUDIO_CAPTURE_DEVICE_TYPE_DEVICE5 = 5;

    /* This value is mapping to EN_AUDIO_CAPTURE_SOURCE */
    /**
     * Audio Data Capture Source Selection,  main audio sound data
     */
    public static final int AUDIO_CAPTURE_SOURCE_MAIN_SOUND = 0;
    /**
     * Audio Data Capture Source Selection,  sub audio sound data
     */
    public static final int AUDIO_CAPTURE_SOURCE_SUB_SOUND = 1;
    /**
     * Audio Data Capture Source Selection,  microphone audio sound data
     */
    public static final int AUDIO_CAPTURE_SOURCE_MICROPHONE_SOUND = 2;
    /**
     * Audio Data Capture Source Selection,  mixed audio sound data
     */
    public static final int AUDIO_CAPTURE_SOURCE_MIXED_SOUND = 3;
    /**
     * Audio Data Capture Source Selection,  user defined sound source1
     */
    public static final int AUDIO_CAPTURE_SOURCE_USER_DEFINE1 = 4;
    /**
     * Audio Data Capture Source Selection,  user defined sound source2
     */
    public static final int AUDIO_CAPTURE_SOURCE_USER_DEFINE2 = 5;

    /* This value is mapping to EN_MUTE_TYPE */
    /**
     * sound mute type,  permanent
     */
    public static final int AUDIO_MUTE_PERMANENT = 0;
    /**
     * sound mute type, moment
     */
    public static final int AUDIO_MUTE_MOMENT = 1;
    /**
     * sound mute type, by user
     */
    public static final int AUDIO_MUTE_BYUSER = 2;
    /**
     * sound mute type, by sync
     */
    public static final int AUDIO_MUTE_BYSYNC = 3;
    /**
     * sound mute type, by chip
     */
    public static final int AUDIO_MUTE_BYCHIP = 4;
    /**
     * sound mute type, by block
     */
    public static final int AUDIO_MUTE_BYBLOCK = 5;
    /**
     * sound mute type, internal1
     */
    public static final int AUDIO_MUTE_INTERNAL1 = 6;
    /**
     * sound mute type, internal2
     */
    public static final int AUDIO_MUTE_INTERNAL2 = 7;
    /**
     * sound mute type, internal3
     */
    public static final int AUDIO_MUTE_INTERNAL3 = 8;
    /**
     * sound mute type, during limited time
     */
    public static final int AUDIO_MUTE_DURING_LIMITED_TIME = 9;
    /**
     * sound mute type, mhegap
     */
    public static final int AUDIO_MUTE_MHEGAP = 10;
    /**
     * sound mute type, ci
     */
    public static final int AUDIO_MUTE_CI = 11;
    /**
     * sound mute type, scan
     */
    public static final int AUDIO_MUTE_SCAN = 12;
    /**
     * sound mute type, source switch
     */
    public static final int AUDIO_MUTE_SOURCESWITCH = 13;
    /**
     * sound mute type, source switch
     */
    public static final int AUDIO_MUTE_USER_SPEAKER = 14;
    /**
     * sound mute type, source switch
     */
    public static final int AUDIO_MUTE_USER_HP = 15;
    /**
     * sound mute type, source switch
     */
    public static final int AUDIO_MUTE_USER_SPDIF = 16;
    /**
     * sound mute type, source switch
     */
    public static final int AUDIO_MUTE_USER_SCART1 = 17;
    /**
     * sound mute type, source switch
     */
    public static final int AUDIO_MUTE_USER_SCART2 = 18;
    /**
     * sound mute type, Set all sound mute type
     */
    public static final int AUDIO_MUTE_ALL = 19;
    /**
     * sound mute type, data input (From Alsa)
     */
    public static final int AUDIO_MUTE_USER_DATA_IN = 20;

    /* This value is mapping to enum Audio Type */
    /**
     * Audio type,MPEG
     */
    public static final short AUDIO_TYPE_MPEG = 0x00;
    /**
     * Audio type,Dolby D (AC3)
     */
    public static final short AUDIO_TYPE_Dolby_D = 0x01;
    /**
     * Audio type,AAC
     */
    public static final short  AUDIO_TYPE_AAC = 0x03;
    /**
     * Audio type,AC3P
     */
    public static final short AUDIO_TYPE_AC3P = 0x04;
    /**
     * Audio type,DRA1
     */
    public static final short AUDIO_TYPE_DRA1 = 0x15;

    /* This value is mapping to EN_AAC_PROFILE_AND_LEVEL */
    /**
     * AAC Profile And Level,AAC_LEVEL_RESERVED
     */
    public static final short AAC_LEVEL_RESERVED = 0;
    /**
     * AAC Profile And Level,AAC_LEVEL1_BRAZIL
     */
    public static final short AAC_LEVEL1_BRAZIL = 40;
    /**
     * AAC Profile And Level,AAC_LEVEL2_BRAZIL
     */
    public static final short AAC_LEVEL2_BRAZIL = 41;
    /**
     * AAC Profile And Level,AAC_LEVEL4_BRAZIL
     */
    public static final short AAC_LEVEL4_BRAZIL = 42;
    /**
     * AAC Profile And Level,AAC_LEVEL5_BRAZIL
     */
    public static final short AAC_LEVEL5_BRAZIL = 43;
    /**
     * AAC Profile And Level,HE_AAC_LEVEL2_BRAZIL
     */
    public static final short HE_AAC_LEVEL2_BRAZIL = 44;
    /**
     * AAC Profile And Level,HE_AAC_LEVEL3_BRAZIL
     */
    public static final short HE_AAC_LEVEL3_BRAZIL = 45;
    /**
     * AAC Profile And Level,HE_AAC_LEVEL4_BRAZIL
     */
    public static final short HE_AAC_LEVEL4_BRAZIL = 46;
    /**
     * AAC Profile And Level,HE_AAC_LEVEL5_BRAZIL
     */
    public static final short HE_AAC_LEVEL5_BRAZIL = 47;
    /**
     * AAC Profile And Level,AAC_LEVEL1
     */
    public static final short AAC_LEVEL1 = 80;
    /**
     * AAC Profile And Level,AAC_LEVEL2
     */
    public static final short AAC_LEVEL2 = 81;
    /**
     * AAC Profile And Level,AAC_LEVEL4
     */
    public static final short AAC_LEVEL4 = 82;
    /**
     * AAC Profile And Level,AAC_LEVEL5
     */
    public static final short AAC_LEVEL5 = 83;
    /**
     * AAC Profile And Level,HE_AAC_LEVEL2
     */
    public static final short HE_AAC_LEVEL2 = 88;
    /**
     * AAC Profile And Level,HE_AAC_LEVEL3
     */
    public static final short HE_AAC_LEVEL3 = 89;
    /**
     * AAC Profile And Level,HE_AAC_LEVEL4
     */
    public static final short HE_AAC_LEVEL4 = 90;
    /**
     * AAC Profile And Level,HE_AAC_LEVEL5
     */
    public static final short HE_AAC_LEVEL5 = 91;

    private final static int TVAUDIO_VOLUME_EVENT_START = AudioManager.TVAUDIO_VOLUME_EVENT_START;
    private final static int TVAUDIO_VOLUME_EVENT_END = AudioManager.TVAUDIO_VOLUME_EVENT_END;

    /* Do not change these values without updating their counterparts
     * in device/mstar/common/libraries/tvapi/dtv/common/AudioManager.java
     */
    /**
     * AP set volume
     * @see com.mstar.android.tv.TvAudioManager.OnVolumeEventListener
     */
    public final static int TVAUDIO_AP_SET_VOLUME = AudioManager.TVAUDIO_AP_SET_VOLUME;

    /**
     * Interface definition of a callback to be invoked when there
     * has a volume event.
     */
    public interface OnVolumeEventListener {
        /**
         * Called to indicate a volume event receive.
         *
         * @param what  the type of volume event that has occurred:
         * <ul>
         * <li>{@link #TVAUDIO_AP_SET_VOLUME}
         * </ul>

         * @param arg1 volume
         * @param arg2 reserved
         * @param obj reserved
         * @return True if the method handled the error, false if it didn't.
         */
        boolean onVolumeEvent(int what, int arg1, int arg2, Object obj);
    }

    @Deprecated
    private ArrayList<OnAudioEventListener> mAudioListeners = new ArrayList<OnAudioEventListener>();

    private ArrayList<OnVolumeEventListener> mVolumeEventListeners = new ArrayList<OnVolumeEventListener>();

    private static TvAudioManager mInstance = null;

    private static ITvAudio mService = null;

    private EventHandler mHandler = null;

    private TvAudioManager() throws TvCommonException {
        IBinder b = ServiceManager.getService(Context.TV_SERVICE);

        if (b == null) {
            Log.e(TAG, "TvService doesn't exist!!");
            throw new TvCommonException("TvService doesn't exist.");
        }

        try {
            mService = ITvService.Stub.asInterface(b).getTvAudio();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        Looper looper;
        if ((looper = Looper.myLooper()) != null) {
            mHandler = new EventHandler(looper);
        } else if ((looper = Looper.getMainLooper()) != null) {
            mHandler = new EventHandler(looper);
        } else {
            mHandler = null;
        }

        try {
            mService.addClient(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private class EventHandler extends Handler {
        EventHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what > TVAUDIO_VOLUME_EVENT_START && msg.what < TVAUDIO_VOLUME_EVENT_END) {
                synchronized (mVolumeEventListeners) {
                    for (OnVolumeEventListener l : mVolumeEventListeners) {
                        l.onVolumeEvent(msg.what, msg.arg1, msg.arg2, msg.obj);
                    }
                }
            }

            // FIXME: old architecture, remove later
            switch (msg.what) {
                case TVAUDIO_AP_SET_VOLUME: {
                    synchronized (mAudioListeners) {
                        for (OnAudioEventListener l : mAudioListeners) {
                            l.onApSetVolumeEvent(msg.what);
                        }
                    }
                    break;
                }
                default: {
                    Log.e(TAG, "Unknown message type " + msg.what);
                    break;
                }
            }
            return;
        }
    }


    public static TvAudioManager getInstance() {
        /* Double-checked locking */
        if (mInstance == null) {
            synchronized (TvAudioManager.class) {
                if (mInstance == null) {
                    try {
                        mInstance = new TvAudioManager();
                    } catch (TvCommonException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
        return mInstance;
    }

    /**
     * Set sound mode.
     *
     * @param Member of SoundMode
     * @return TRUE:Success, or FALSE: failed
     * @deprecated Use {@link setAudioSoundMode()}.
     */
    @Deprecated
    public boolean setSoundMode(EnumSoundMode SoundMode) {
        Log.d(TAG, "setSoundMode(), paras SoundMode = " + SoundMode);
        return setAudioSoundMode(SoundMode.ordinal());
    }

    /**
     * Set sound mode
     *
     * <p> The supported sound mode are:
     * <ul>
     * <li> {@link #SOUND_MODE_STANDARD}
     * <li> {@link #SOUND_MODE_MUSIC}
     * <li> {@link #SOUND_MODE_MOVIE}
     * <li> {@link #SOUND_MODE_SPORTS}
     * <li> {@link #SOUND_MODE_USER}
     * <li> {@link #SOUND_MODE_ONSITE1}
     * <li> {@link #SOUND_MODE_ONSITE2}
     * </ul>
     *
     * @param  int
     * @see TvAudioManager#SOUND_MODE_STANDARD
     * @see TvAudioManager#SOUND_MODE_MUSIC
     * @see TvAudioManager#SOUND_MODE_MOVIE
     * @see TvAudioManager#SOUND_MODE_SPORTS
     * @see TvAudioManager#SOUND_MODE_USER
     * @see TvAudioManager#SOUND_MODE_ONSITE1
     * @see TvAudioManager#SOUND_MODE_ONSITE2
     *
     * @return TRUE:Success, or FALSE: failed
     */
    public boolean setAudioSoundMode(int soundMode) {
        Log.d(TAG, "setSoundMode(), paras SoundMode = " + soundMode);
        try {
            return mService.setSoundMode(soundMode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Get sound mode.
     *
     * @return Member of EnumSoundMode.
     * @deprecated Use {@link getAudioSoundMode()}.
     */
    @Deprecated
    public EnumSoundMode getSoundMode() {
        return EnumSoundMode.values()[getAudioSoundMode()];
    }

    /**
     * Get sound mode.
     *
     * <p> The supported sound mode are:
     * <li> {@link #SOUND_MODE_STANDARD
     * <li> {@link #SOUND_MODE_MUSIC
     * <li> {@link #SOUND_MODE_MOVIE
     * <li> {@link #SOUND_MODE_SPORTS
     * <li> {@link #SOUND_MODE_USER
     * <li> {@link #SOUND_MODE_ONSITE1
     * <li> {@link #SOUND_MODE_ONSITE2
     *
     * @return int ,sound mode.
     * @see TvAudioManager#SOUND_MODE_STANDARD
     * @see TvAudioManager#SOUND_MODE_MUSIC
     * @see TvAudioManager#SOUND_MODE_MOVIE
     * @see TvAudioManager#SOUND_MODE_SPORTS
     * @see TvAudioManager#SOUND_MODE_USER
     * @see TvAudioManager#SOUND_MODE_ONSITE1
     * @see TvAudioManager#SOUND_MODE_ONSITE2
     */
    public int getAudioSoundMode() {
        try {
            return mService.getSoundMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return SOUND_MODE_STANDARD;
    }

    /**
     * Set EarPhone Volume.
     *
     * @param value of volume
     * @return TRUE:Success, or FALSE: failed
     */
    public boolean setEarPhoneVolume(int volume) {
        Log.d(TAG, "setEarPhoneVolume(), paras volume = " + volume);
        return setEarPhoneVolume(volume, true);
    }

    /**
     * Set EarPhone Volume,According to the saveDb
     * determine whether to save the database.
     * @param value of volume
     * @param saveDb, boolean value, true if save, false if not save
     * @return TRUE:Success, or FALSE: failed
     */
    public boolean setEarPhoneVolume(int volume, boolean saveDb) {
        Log.d(TAG, "setEarPhoneVolume(volume, saveDb), paras volume = " + volume + "paras saveDb = " + saveDb);
        try {
            return mService.setEarPhoneVolume(volume, saveDb);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get EarPhone volume.
     *
     * @return value of EarPhone volume
     */
    public int getEarPhoneVolume() {
        int i = -1;
        try {
            i = mService.getEarPhoneVolume();
            Log.d(TAG, "getEarPhoneVolume(), return int " + i);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * Set Bass volume.
     *
     * @param value 0~100
     * @return true if operation success or false if fail.
     */
    public boolean setBass(int bassValue) {
        Log.d(TAG, "setBass(), paras bassValue = " + bassValue);
        try {
            return mService.setBass(bassValue);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get Bass Value.
     *
     * @return Bass Value 0~100
     */
    public int getBass() {
        int i = -1;
        try {
            i = mService.getBass();
            Log.d(TAG, "getBass(), return int " + i);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return i;
    }

    /**
     * Set treble value.
     *
     * @param treble value 0~100
     * @return true if operation success or false if fail.
     */
    public boolean setTreble(int bassValue) {
        Log.d(TAG, "setTreble(), paras bassValue = " + bassValue);
        try {
            return mService.setTreble(bassValue);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get treble value.
     *
     * @return treble value 0~100
     */
    public int getTreble() {
        int i = -1;
        try {
            i = mService.getTreble();
            Log.d(TAG, "getTreble(), return int " + i);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * Set balance.
     *
     * @param balance value 0~100
     * @return true if operation success or false if fail.
     */
    public boolean setBalance(int balanceValue) {
        Log.d(TAG, "setBalance(), paras balanceValue = " + balanceValue);
        try {
            return mService.setBalance(balanceValue);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get balance.
     *
     * @return balance value 0~100
     */
    public int getBalance() {
        int i = -1;
        try {
            i = mService.getBalance();
            Log.d(TAG, "getBalance(), return int " + i);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * Set Avc mode.
     *
     * @param AvcEnable, boolean value, true if enable, false if disable
     * @return true if operation success or false if fail.
     */
    public boolean setAvcMode(boolean isAvcEnable) {
        Log.d(TAG, "setAvcMode(), paras isAvcEnable = " + isAvcEnable);
        try {
            return mService.setAvcMode(isAvcEnable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get Avc mode.
     *
     * @return isAvcEnable, boolean value, true if enable, false if disable
     */
    public boolean getAvcMode() {
        try {
            Log.d(TAG, "getAvcMode()");
            return mService.getAvcMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Set surround sound mode.
     *
     * @param EnumSurroundMode
     * @return boolean value, true if enable, false if disable
     * @deprecated Use {@link setAudioSurroundMode()}.
     */
    @Deprecated
    public boolean setSurroundMode(EnumSurroundMode surroundMode) {
        Log.d(TAG, "setSurroundMode(), paras surroundMode = " + surroundMode);
        return setAudioSurroundMode(surroundMode.ordinal());
    }

    /**
     * Set surround sound mode.
     *
     * <p> The supported surround sound mode are:
     * <ul>
     * <li> {@link #SURROUND_MODE_OFF}
     * <li> {@link #SURROUND_MODE_ON}
     * </ul>
     *
     * @param  surroundMode int
     * @see TvAudioManager#SURROUND_MODE_OFF
     * @see TvAudioManager#SURROUND_MODE_ON
     *
     * @return boolean value, true if enable, false if disable
     */
    public boolean setAudioSurroundMode(int surroundMode) {
        Log.d(TAG, "setSurroundMode(), paras surroundMode = " + surroundMode);
        try {
            return mService.setSurroundMode(surroundMode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get surround sound mode.
     *
     * @return EnumSurroundMode
     * @deprecated Use {@link getAudioSurroundMode()}.
     */
    @Deprecated
    public EnumSurroundMode getSurroundMode() {
        return EnumSurroundMode.values()[getAudioSurroundMode()];
    }

    /**
     * Get surround sound mode.
     *
     * <p> The supported surround sound mode are:
     * <ul>
     * <li> {@link #SURROUND_MODE_OFF}
     * <li> {@link #SURROUND_MODE_ON}
     * <ul>
     *
     * @return int, surround sound mode
     * @see TvAudioManager#SURROUND_MODE_OFF
     * @see TvAudioManager#SURROUND_MODE_ON
     */
    public int getAudioSurroundMode() {
        try {
            return mService.getSurroundMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return SURROUND_MODE_OFF;
    }

    /**
     * set SpdifOut mode.
     *
     * @return boolean value, true if enable, false if disable
     * @deprecated Use {@link setAudioSpdifOutMode()}.
     */
    @Deprecated
    public boolean setSpdifOutMode(EnumSpdifType SpdifMode) {
        Log.d(TAG, "setSpdifOutMode(), paras SpdifMode = " + SpdifMode);
        return setAudioSpdifOutMode(SpdifMode.ordinal());
    }

    /**
     * set SpdifOut mode.
     *
     * <p> The supported spdif output mode are:
     * <ul>
     * <li> {@link #SPDIF_TYPE_PCM}
     * <li> {@link #SPDIF_TYPE_OFF}
     * <li> {@link #SPDIF_TYPE_NONPCM}
     * </ul>
     *
     * @param  SpdifMode int
     * @see TvAudioManager#SPDIF_TYPE_PCM
     * @see TvAudioManager#SPDIF_TYPE_OFF
     * @see TvAudioManager#SPDIF_TYPE_NONPCM
     *
     * @return boolean value, true if enable, false if disable
     */
    public boolean setAudioSpdifOutMode(int spdifMode) {
        Log.d(TAG, "setSpdifOutMode(), paras SpdifMode = " + spdifMode);
        try {
            return mService.setSpdifOutMode(spdifMode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get SpdifOut mode.
     *
     * @return EN_SPDIF_OUT_MODE
     * @deprecated Use {@link setAudioSpdifOutMode()}.
     */
    @Deprecated
    public EnumSpdifType getSpdifOutMode() {
        return EnumSpdifType.values()[getAudioSpdifOutMode()];
    }

    /**
     * Get SpdifOut mode.
     *
     * <p> The supported spdif output mode are:
     * <ul>
     * <li> {@link #SPDIF_TYPE_PCM}
     * <li> {@link #SPDIF_TYPE_OFF}
     * <li> {@link #SPDIF_TYPE_NONPCM}
     * </ul>
     *
     * @return int, spidf out mode
     * @see TvAudioManager#SPDIF_TYPE_PCM
     * @see TvAudioManager#SPDIF_TYPE_OFF
     * @see TvAudioManager#SPDIF_TYPE_NONPCM
     */
    public int getAudioSpdifOutMode() {
        try {
            return mService.getSpdifOutMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return SPDIF_TYPE_PCM;
    }

    /**
     * Set Equilizer 120HZ EQBand value
     *
     * @param EQBand value 0~100
     * @return boolean value, true if enable, false if disable
     */
    public boolean setEqBand120(int eqValue) {
        Log.d(TAG, "setEqBand120(), paras eqValue = " + eqValue);
        try {
            return mService.setEqBand120(eqValue);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get Equilizer 120HZ EQBand value
     *
     * @retur 120HZ EQBand value 0~100
     */
    public int getEqBand120() {
        int i = -1;
        try {
            i = mService.getEqBand120();
            Log.d(TAG, "getEqBand120(), return int " + i);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * Set Equilizer 500HZ EQBand value
     *
     * @param EQBand value 0~100
     * @return boolean value, true if enable, false if disable
     */
    public boolean setEqBand500(int eqValue) {
        Log.d(TAG, "setEqBand500(), paras eqValue = " + eqValue);
        try {
            return mService.setEqBand500(eqValue);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get Equilizer 500HZ EQBand value
     *
     * @return 500HZ EQBand value 0~100
     */
    public int getEqBand500() {
        int i = -1;
        try {
            i = mService.getEqBand500();
            Log.d(TAG, "getEqBand500(), return int " + i);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * Set Equilizer 1500HZ EQBand value
     *
     * @param EQBand value 0~100
     * @return boolean value, true if enable, false if disable
     */
    public boolean setEqBand1500(int eqValue) {
        Log.d(TAG, "setEqBand1500(, paras eqValue = " + eqValue);
        try {
            return mService.setEqBand1500(eqValue);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get Equilizer 1500HZ EQBand value
     *
     * @return 1500HZ EQBand value 0~100
     */
    public int getEqBand1500() {
        int i = -1;
        try {
            i = mService.getEqBand1500();
            Log.d(TAG, "getEqBand1500(), return int " + i);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * Set Equilizer 5kHZ EQBand value
     *
     * @param EQBand value 0~100
     * @return boolean value, true if enable, false if disable
     */
    public boolean setEqBand5k(int eqValue) {
        Log.d(TAG, "setEqBand5k(), paras eqValue = " + eqValue);
        try {
            return mService.setEqBand5k(eqValue);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get Equilizer 5kHZ EQBand value
     *
     * @return 5kHZ EQBand value 0~100
     */
    public int getEqBand5k() {
        int i = -1;
        try {
            i = mService.getEqBand5k();
            Log.d(TAG, "getEqBand5k(), return int " + i);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * Set Equilizer 10kHZ EQBand value
     *
     * @param EQBand value 0~100
     * @return boolean value, true if enable, false if disable
     */
    public boolean setEqBand10k(int eqValue) {
        Log.d(TAG, "setEqBand10k(), paras eqValue = " + eqValue);
        try {
            return mService.setEqBand10k(eqValue);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get Equilizer 10kHZ EQBand value
     *
     * @return 10kHZ EQBand value 0~100
     */
    public int getEqBand10k() {
        int i = -1;
        try {
            i = mService.getEqBand10k();
            Log.d(TAG, "getEqBand10k(), return int " + i);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * Set BassSwitch mode.
     *
     * @param EnumOnOffType
     * @return boolean value, true if enable, false if fail
     * @deprecated Use {@link setAudioBassSwitch()}.
     */
    @Deprecated
    public boolean setBassSwitch(EnumOnOffType en) {
        Log.d(TAG, "setBassSwitch(), paras en = " + en);
        return setAudioBassSwitch(en.ordinal());
    }

    /**
     * Set BassSwitch mode.
     *
     * <p> The supported on/off type are:
     * <ul>
     * <li> {@link #ON_OFF_TYPE_OFF}
     * <li> {@link #ON_OFF_TYPE_ON}
     * </ul>
     *
     * @param int, On Off Type
     * @see TvAudioManager#ON_OFF_TYPE_OFF
     * @see TvAudioManager#ON_OFF_TYPE_ON
     *
     * @return boolean value, true if enable, false if fail
     */
    public boolean setAudioBassSwitch(int onOffType) {
        Log.d(TAG, "setBassSwitch(), paras onOffType = " + onOffType);
        try {
            return mService.setBassSwitch(onOffType);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get BassSwitch mode.
     *
     * @return EnumOnOffType
     * @deprecated Use {@link getAudioBassSwitch()}.
     */
    @Deprecated
    public EnumOnOffType getBassSwitch() {
        return EnumOnOffType.values()[getAudioBassSwitch()];
    }

    /**
     * Get BassSwitch mode.
     *
     * <p> The supported on/off type are:
     * <ul>
     * <li> {@link #ON_OFF_TYPE_OFF}
     * <li> {@link #ON_OFF_TYPE_ON}
     * </ul>
     *
     * @return int, On/Off Type
     * @see TvAudioManager#ON_OFF_TYPE_OFF
     * @see TvAudioManager#ON_OFF_TYPE_ON
     */
    public int getAudioBassSwitch() {
        try {
            return mService.getBassSwitch();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ON_OFF_TYPE_OFF;
    }

    /**
     * Set BassVolume.
     *
     * @param int value 0~100
     * @return boolean value, true if success, false if disable
     */
    public boolean setBassVolume(int volume) {
        Log.d(TAG, "setBassVolume(), paras volume = " + volume);
        try {
            return mService.setBassVolume(volume);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get Bass volume.
     *
     * @return value 0~100
     */
    public int getBassVolume() {
        int i = -1;

        try {
            i = mService.getBassVolume();
            Log.d(TAG, "getBassVolume(), return int " + i);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * Set PowerOnOffMusic mode.
     *
     * @param EnumOnOffType
     * @return boolean value, true if enable, false if fail
     * @deprecated Use {@link setAudioPowerOnOffMusic()}.
     */
    @Deprecated
    public boolean setPowerOnOffMusic(EnumOnOffType en) {
        Log.d(TAG, "setPowerOnOffMusic(), paras en = " + en);
        return setAudioPowerOnOffMusic(en.ordinal());
    }

    /**
     * Set PowerOnOffMusic mode.
     *
     * <p> The supported on/off type are:
     * <ul>
     * <li> {@link #ON_OFF_TYPE_OFF}
     * <li> {@link #ON_OFF_TYPE_ON}
     * </ul>
     *
     * @param en int, On/Off Type
     * @see TvAudioManager#ON_OFF_TYPE_OFF
     * @see TvAudioManager#ON_OFF_TYPE_ON
     *
     * @return boolean value, true if enable, false if fail
     */
    public boolean setAudioPowerOnOffMusic(int onOffType) {
        Log.d(TAG, "setPowerOnOffMusic(), paras onOffType = " + onOffType);
        try {
            return mService.setPowerOnOffMusic(onOffType);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get PowerOnOffMusic mode.
     *
     * @return EnumOnOffType
     * @deprecated Use {@link getAudioPowerOnOffMusic()}.
     */
    @Deprecated
    public EnumOnOffType getPowerOnOffMusic() {
        return EnumOnOffType.values()[getAudioPowerOnOffMusic()];
    }

    /**
     * Get PowerOnOffMusic mode.
     *
     * <p> The supported on/off type are:
     * <ul>
     * <li> {@link #ON_OFF_TYPE_OFF}
     * <li> {@link #ON_OFF_TYPE_ON}
     * </ul>
     *
     * @return int, on/off type
     * @see TvAudioManager#ON_OFF_TYPE_OFF
     * @see TvAudioManager#ON_OFF_TYPE_ON
     */
    public int getAudioPowerOnOffMusic() {
        try {
            return mService.getPowerOnOffMusic();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ON_OFF_TYPE_OFF;
    }

    /**
     * Set Wallmusic mode.
     *
     * @param EnumOnOffType
     * @return boolean value, true if enable, false if fail
     * @deprecated Use {@link setAudioWallmusic()}.
     */
    @Deprecated
    public boolean setWallmusic(EnumOnOffType en) {
        Log.d(TAG, "setWallmusic(), paras en = " + en);
        return setAudioWallmusic(en.ordinal());
    }

    /**
     * Set Wallmusic mode.
     *
     * <p> The supported on/off type are:
     * <ul>
     * <li> {@link #ON_OFF_TYPE_OFF}
     * <li> {@link #ON_OFF_TYPE_ON}
     * </ul>
     *
     * @param en int, On/Off Type
     * @see TvAudioManager#ON_OFF_TYPE_OFF
     * @see TvAudioManager#ON_OFF_TYPE_ON
     *
     * @return boolean value, true if enable, false if fail
     */
    public boolean setAudioWallmusic(int onOffType) {
        Log.d(TAG, "setWallmusic(), paras onOffType = " + onOffType);
        try {
            return mService.setWallmusic(onOffType);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get Wallmusic mode.
     *
     * @return EnumOnOffType
     * @deprecated Use {@link getAudioWallmusic()}.
     */
    @Deprecated
    public EnumOnOffType getWallmusic() {
        return EnumOnOffType.values()[getAudioWallmusic()];
    }

    /**
     * Get Wallmusic mode.
     *
     * <p> The supported on/off type are:
     * <ul>
     * <li> {@link #ON_OFF_TYPE_OFF}
     * <li> {@link #ON_OFF_TYPE_ON}
     * </ul>
     *
     * @return int, on/off type
     * @see TvAudioManager#ON_OFF_TYPE_OFF
     * @see TvAudioManager#ON_OFF_TYPE_ON
     */
    public int getAudioWallmusic() {
        try {
            return mService.getWallmusic();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ON_OFF_TYPE_OFF;
    }

    /**
     * Set SeparateHear mode.
     *
     * @param EnumOnOffType
     * @return boolean value, true if enable, false if fail
     * @deprecated Use {@link setSeparateHear()}.
     */
    @Deprecated
    public boolean setSeparateHear(EnumOnOffType en) {
        Log.d(TAG, "setSeparateHear(), paras en = " + en);
        return setAudioSeparateHear(en.ordinal());
    }

    /**
     * Set SeparateHear mode.
     *
     * <p> The supported on/off type are:
     * <ul>
     * <li> {@link #ON_OFF_TYPE_OFF}
     * <li> {@link #ON_OFF_TYPE_ON}
     * </ul>
     *
     * @param en int, On/Off Type
     * @see TvAudioManager#ON_OFF_TYPE_OFF
     * @see TvAudioManager#ON_OFF_TYPE_ON
     *
     * @return boolean value, true if enable, false if fail
     */
    public boolean setAudioSeparateHear(int onOffType) {
        Log.d(TAG, "setSeparateHear(), paras onOffType = " + onOffType);
        try {
            return mService.setSeparateHear(onOffType);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get getSeparateHear mode.
     *
     * @return EnumOnOffType
     * @deprecated Use {@link getAudioSeparateHear()}.
     */
    @Deprecated
    public EnumOnOffType getSeparateHear() {
        return EnumOnOffType.values()[getAudioSeparateHear()];
    }

    /**
     * Get getSeparateHear mode.
     *
     * <p> The supported on/off type are:
     * <ul>
     * <li> {@link #ON_OFF_TYPE_OFF}
     * <li> {@link #ON_OFF_TYPE_ON}
     * </ul>
     *
     * @return int, on/off type
     * @see TvAudioManager#ON_OFF_TYPE_OFF
     * @see TvAudioManager#ON_OFF_TYPE_ON
     */
    public int getAudioSeparateHear() {
        try {
            return mService.getSeparateHear();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ON_OFF_TYPE_OFF;
    }

    /**
     * Set TrueBass mode.
     *
     * @param EnumOnOffType
     * @return boolean value, true if enable, false if fail
     * @deprecated Use {@link setAudioTrueBass()}.
     */
    @Deprecated
    public boolean setTrueBass(EnumOnOffType en) {
        Log.d(TAG, "setTrueBass(), paras en = " + en);
        return setAudioTrueBass(en.ordinal());
    }

    /**
     * Set TrueBass mode.
     *
     * <p> The supported on/off type are:
     * <ul>
     * <li> {@link #ON_OFF_TYPE_OFF}
     * <li> {@link #ON_OFF_TYPE_ON}
     * </ul>
     *
     * @param int, On/Off Type
     * @see TvAudioManager#ON_OFF_TYPE_OFF
     * @see TvAudioManager#ON_OFF_TYPE_ON
     *
     * @return boolean value, true if enable, false if fail
     */
    public boolean setAudioTrueBass(int onOffType) {
        Log.d(TAG, "setTrueBass(), paras onOffType = " + onOffType);
        try {
            return mService.setTrueBass(onOffType);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get TrueBass mode.
     *
     * @return EnumOnOffType
     * @deprecated Use {@link getAudioTrueBass()}.
     */
    @Deprecated
    public EnumOnOffType getTrueBass() {
        return EnumOnOffType.values()[getAudioTrueBass()];
    }

    /**
     * Get TrueBass mode.
     *
     * <p> The supported on/off type are:
     * <li> {@link #ON_OFF_TYPE_OFF}
     * <li> {@link #ON_OFF_TYPE_ON}
     *
     * @return int on/off type
     * @see TvAudioManager#ON_OFF_TYPE_OFF
     * @see TvAudioManager#ON_OFF_TYPE_ON
     */
    public int getAudioTrueBass() {
        try {
            return mService.getTrueBass();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ON_OFF_TYPE_OFF;
    }

    /**
     * Set Dtv output sound mode.
     *
     * @param enDtvSoundMode
     * @deprecated Use {@link setAudioDtvOutputMode()}.
     */
    @Deprecated
    public void setDtvOutputMode(EnumDtvSoundMode enDtvSoundMode) {
        Log.d(TAG, "setDtvOutputMode(), paras enDtvSoundMode = " + enDtvSoundMode);
        setAudioDtvOutputMode(enDtvSoundMode.ordinal());
    }

    /**
     * Set Dtv output sound mode.
     *
     * <p> The supported Dtv output sound mode are:
     * <ul>
     * <li> {@link #DTV_SOUND_MODE_STEREO}
     * <li> {@link #DTV_SOUND_MODE_LEFT}
     * <li> {@link #DTV_SOUND_MODE_RIGHT}
     * <li> {@link #DTV_SOUND_MODE_MIXED}
     * </ul>
     *
     * @param int dtvSoundMode
     * @see TvAudioManager#DTV_SOUND_MODE_STEREO
     * @see TvAudioManager#DTV_SOUND_MODE_LEFT
     * @see TvAudioManager#DTV_SOUND_MODE_RIGHT
     * @see TvAudioManager#DTV_SOUND_MODE_MIXED
     */
    public void setAudioDtvOutputMode(int dtvSoundMode) {
        Log.d(TAG, "setDtvOutputMode(), paras dtvSoundMode = " + dtvSoundMode);
        try {
            mService.setDtvOutputMode(dtvSoundMode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get Dtv output sound mode.
     *
     * @return
     * @deprecated Use {@link getAudioDtvOutputMode()}.
     */
    @Deprecated
    public EnumDtvSoundMode getDtvOutputMode() {
        return EnumDtvSoundMode.values()[getAudioDtvOutputMode()];
    }

    /**
     * Get Dtv output sound mode.
     *
     * <p> The supported Dtv output sound mode are:
     * <ul>
     * <li> {@link #DTV_SOUND_MODE_STEREO}
     * <li> {@link #DTV_SOUND_MODE_LEFT}
     * <li> {@link #DTV_SOUND_MODE_RIGHT}
     * <li> {@link #DTV_SOUND_MODE_MIXED}
     * </ul>
     *
     * @return int, Dtv output sound mode
     * @see TvAudioManager#DTV_SOUND_MODE_STEREO
     * @see TvAudioManager#DTV_SOUND_MODE_LEFT
     * @see TvAudioManager#DTV_SOUND_MODE_RIGHT
     * @see TvAudioManager#DTV_SOUND_MODE_MIXED
     */
    public int getAudioDtvOutputMode() {
        try {
            return mService.getDtvOutputMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return DTV_SOUND_MODE_STEREO;
    }

    /**
     * set audio Capture Source
     *
     * @param eAudioCaptureSource EnumAuidoCaptureSource the audio source
     * @return EnumAudioReturn
     * @deprecated Use {@link setAudioCaptureSource()}.
     */
    @Deprecated
    public EnumAudioReturn setAudioCaptureSource(
            EnumAuidoCaptureDeviceType eAudioCaptureDeviceType,
            EnumAuidoCaptureSource eAudioCaptureSource) {
        return EnumAudioReturn.values()[setAudioCaptureSource(
                    eAudioCaptureDeviceType.ordinal(), eAudioCaptureSource.ordinal())];
    }

    /**
     * set audio Capture Source
     *
     * <p> The supported Audio Data Capture Device are:
     * <ul>
     * <li> {@link #AUDIO_CAPTURE_DEVICE_TYPE_DEVICE0}
     * <li> {@link #AUDIO_CAPTURE_DEVICE_TYPE_DEVICE1}
     * <li> {@link #AUDIO_CAPTURE_DEVICE_TYPE_DEVICE2}
     * <li> {@link #AUDIO_CAPTURE_DEVICE_TYPE_DEVICE3}
     * <li> {@link #AUDIO_CAPTURE_DEVICE_TYPE_DEVICE4}
     * <li> {@link #AUDIO_CAPTURE_DEVICE_TYPE_DEVICE5}
     * </ul>
     * <p> The supported Audio Data Capture Source are:
     * <ul>
     * <li> {@link #AUDIO_CAPTURE_SOURCE_MAIN_SOUND}
     * <li> {@link #AUDIO_CAPTURE_SOURCE_SUB_SOUND}
     * <li> {@link #AUDIO_CAPTURE_SOURCE_MICROPHONE_SOUND}
     * <li> {@link #AUDIO_CAPTURE_SOURCE_MIXED_SOUND}
     * <li> {@link #AUDIO_CAPTURE_SOURCE_USER_DEFINE1}
     * <li> {@link #AUDIO_CAPTURE_SOURCE_USER_DEFINE2}
     * </ul>
     *
     * @param int iAudioCaptureDeviceType the audio device type
     * @see TvAudioManager#AUDIO_CAPTURE_DEVICE_TYPE_DEVICE0
     * @see TvAudioManager#AUDIO_CAPTURE_DEVICE_TYPE_DEVICE1
     * @see TvAudioManager#AUDIO_CAPTURE_DEVICE_TYPE_DEVICE2
     * @see TvAudioManager#AUDIO_CAPTURE_DEVICE_TYPE_DEVICE3
     * @see TvAudioManager#AUDIO_CAPTURE_DEVICE_TYPE_DEVICE4
     * @see TvAudioManager#AUDIO_CAPTURE_DEVICE_TYPE_DEVICE5
     *
     * @param int iAuidoCaptureSource the audio source
     * @see TvAudioManager#AUDIO_CAPTURE_SOURCE_MAIN_SOUND
     * @see TvAudioManager#AUDIO_CAPTURE_SOURCE_SUB_SOUND
     * @see TvAudioManager#AUDIO_CAPTURE_SOURCE_MICROPHONE_SOUND
     * @see TvAudioManager#AUDIO_CAPTURE_SOURCE_MIXED_SOUND
     * @see TvAudioManager#AUDIO_CAPTURE_SOURCE_USER_DEFINE1
     * @see TvAudioManager#AUDIO_CAPTURE_SOURCE_USER_DEFINE2
     *
     * @return int audioReturn
     * @see TvAudioManager#AUDIO_RETURN_NOT_OK
     * @see TvAudioManager#AUDIO_RETURN_OK
     * @see TvAudioManager#AUDIO_RETURN_UNSUPPORT
     */
    public int setAudioCaptureSource(int iAudioCaptureDeviceType, int iAudioCaptureSource) {
        try {
            return mService.setAudioCaptureSource(iAudioCaptureDeviceType, iAudioCaptureSource);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return AUDIO_RETURN_NOT_OK;
    }

    /**
     * Enable mute function for the different mute type
     *
     * @param enMuteType EnumMuteType specify the mute type which want to query
     * @return EnumAudioReturn status of auto volumn fuctionality
     * @throws TvCommonException
     * @deprecated Use {@link enableAudioMute()}.
     */
    @Deprecated
    public EnumAudioReturn enableMute(EnumMuteType enMuteType) {
        return EnumAudioReturn.values()[enableAudioMute(enMuteType.ordinal())];
    }

    /**
     * Enable mute function for the different mute type
     *
     * <p> The supported Audio mute type are:
     * <ul>
     * <li> {@link #AUDIO_MUTE_PERMANENT}
     * <li> {@link #AUDIO_MUTE_MOMENT}
     * <li> {@link #AUDIO_MUTE_BYUSER}
     * <li> {@link #AUDIO_MUTE_BYSYNC}
     * <li> {@link #AUDIO_MUTE_BYCHIP}
     * <li> {@link #AUDIO_MUTE_BYBLOCK}
     * <li> {@link #AUDIO_MUTE_INTERNAL1}
     * <li> {@link #AUDIO_MUTE_INTERNAL2}
     * <li> {@link #AUDIO_MUTE_INTERNAL3}
     * <li> {@link #AUDIO_MUTE_DURING_LIMITED_TIME}
     * <li> {@link #AUDIO_MUTE_MHEGAP}
     * <li> {@link #AUDIO_MUTE_CI}
     * <li> {@link #AUDIO_MUTE_SCAN}
     * <li> {@link #AUDIO_MUTE_SOURCESWITCH}
     * <li> {@link #AUDIO_MUTE_USER_SPEAKER}
     * <li> {@link #AUDIO_MUTE_USER_HP}
     * <li> {@link #AUDIO_MUTE_USER_SPDIF}
     * <li> {@link #AUDIO_MUTE_USER_SCART1}
     * <li> {@link #AUDIO_MUTE_USER_SCART2}
     * <li> {@link #AUDIO_MUTE_ALL}
     * <li> {@link #AUDIO_MUTE_USER_DATA_IN}
     * <ul>
     *
     * @param iMuteType int, specify the mute type which want to query
     * @see TvAudioManager#AUDIO_MUTE_PERMANENT
     * @see TvAudioManager#AUDIO_MUTE_MOMENT
     * @see TvAudioManager#AUDIO_MUTE_BYUSER
     * @see TvAudioManager#AUDIO_MUTE_BYSYNC
     * @see TvAudioManager#AUDIO_MUTE_BYCHIP
     * @see TvAudioManager#AUDIO_MUTE_BYBLOCK
     * @see TvAudioManager#AUDIO_MUTE_INTERNAL1
     * @see TvAudioManager#AUDIO_MUTE_INTERNAL2
     * @see TvAudioManager#AUDIO_MUTE_INTERNAL3
     * @see TvAudioManager#AUDIO_MUTE_DURING_LIMITED_TIME
     * @see TvAudioManager#AUDIO_MUTE_MHEGAP
     * @see TvAudioManager#AUDIO_MUTE_CI
     * @see TvAudioManager#AUDIO_MUTE_SCAN
     * @see TvAudioManager#AUDIO_MUTE_SOURCESWITCH
     * @see TvAudioManager#AUDIO_MUTE_USER_SPEAKER
     * @see TvAudioManager#AUDIO_MUTE_USER_HP
     * @see TvAudioManager#AUDIO_MUTE_USER_SPDIF
     * @see TvAudioManager#AUDIO_MUTE_USER_SCART1
     * @see TvAudioManager#AUDIO_MUTE_USER_SCART2
     * @see TvAudioManager#AUDIO_MUTE_ALL
     * @see TvAudioManager#AUDIO_MUTE_USER_DATA_IN
     *
     * @return int, AudioReturn status of auto volumn fuctionality
     * @see TvAudioManager#AUDIO_RETURN_NOT_OK
     * @see TvAudioManager#AUDIO_RETURN_OK
     * @see TvAudioManager#AUDIO_RETURN_UNSUPPORT
     *
     * @throws TvCommonException
     */
    public int enableAudioMute(int muteType) {
        try {
            return mService.enableMute(muteType);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return AUDIO_RETURN_NOT_OK;
    }

    /**
     * Disable mute function for the different mute type.
     *
     * @param enMuteType one of EnumMuteType
     * @return EnumAudioReturn status of auto volumn fuctionality
     * @throws TvCommonException
     * @deprecated Use {@link disableAudioMute()}.
     */
    @Deprecated
    public EnumAudioReturn disableMute(EnumMuteType enMuteType) {
        return EnumAudioReturn.values()[disableAudioMute(enMuteType.ordinal())];
    }

    /**
     * Disable mute function for the different mute type.
     *
     * <p> The supported Audio mute type are:
     * <ul>
     * <li> {@link #AUDIO_MUTE_PERMANENT}
     * <li> {@link #AUDIO_MUTE_MOMENT}
     * <li> {@link #AUDIO_MUTE_BYUSER}
     * <li> {@link #AUDIO_MUTE_BYSYNC}
     * <li> {@link #AUDIO_MUTE_BYCHIP}
     * <li> {@link #AUDIO_MUTE_BYBLOCK}
     * <li> {@link #AUDIO_MUTE_INTERNAL1}
     * <li> {@link #AUDIO_MUTE_INTERNAL2}
     * <li> {@link #AUDIO_MUTE_INTERNAL3}
     * <li> {@link #AUDIO_MUTE_DURING_LIMITED_TIME}
     * <li> {@link #AUDIO_MUTE_MHEGAP}
     * <li> {@link #AUDIO_MUTE_CI}
     * <li> {@link #AUDIO_MUTE_SCAN}
     * <li> {@link #AUDIO_MUTE_SOURCESWITCH}
     * <li> {@link #AUDIO_MUTE_USER_SPEAKER}
     * <li> {@link #AUDIO_MUTE_USER_HP}
     * <li> {@link #AUDIO_MUTE_USER_SPDIF}
     * <li> {@link #AUDIO_MUTE_USER_SCART1}
     * <li> {@link #AUDIO_MUTE_USER_SCART2}
     * <li> {@link #AUDIO_MUTE_ALL}
     * <li> {@link #AUDIO_MUTE_USER_DATA_IN}
     * </ul>
     *
     * @param iMuteType int, specify the mute type which want to query
     * @see TvAudioManager#AUDIO_MUTE_PERMANENT
     * @see TvAudioManager#AUDIO_MUTE_MOMENT
     * @see TvAudioManager#AUDIO_MUTE_BYUSER
     * @see TvAudioManager#AUDIO_MUTE_BYSYNC
     * @see TvAudioManager#AUDIO_MUTE_BYCHIP
     * @see TvAudioManager#AUDIO_MUTE_BYBLOCK
     * @see TvAudioManager#AUDIO_MUTE_INTERNAL1
     * @see TvAudioManager#AUDIO_MUTE_INTERNAL2
     * @see TvAudioManager#AUDIO_MUTE_INTERNAL3
     * @see TvAudioManager#AUDIO_MUTE_DURING_LIMITED_TIME
     * @see TvAudioManager#AUDIO_MUTE_MHEGAP
     * @see TvAudioManager#AUDIO_MUTE_CI
     * @see TvAudioManager#AUDIO_MUTE_SCAN
     * @see TvAudioManager#AUDIO_MUTE_SOURCESWITCH
     * @see TvAudioManager#AUDIO_MUTE_USER_SPEAKER
     * @see TvAudioManager#AUDIO_MUTE_USER_HP
     * @see TvAudioManager#AUDIO_MUTE_USER_SPDIF
     * @see TvAudioManager#AUDIO_MUTE_USER_SCART1
     * @see TvAudioManager#AUDIO_MUTE_USER_SCART2
     * @see TvAudioManager#AUDIO_MUTE_ALL
     * @see TvAudioManager#AUDIO_MUTE_USER_DATA_IN
     *
     * @return int, AudioReturn status of auto volumn fuctionality
     * @see TvAudioManager#AUDIO_RETURN_NOT_OK
     * @see TvAudioManager#AUDIO_RETURN_OK
     * @see TvAudioManager#AUDIO_RETURN_UNSUPPORT
     *
     * @throws TvCommonException
     */
    public int disableAudioMute(int muteType) {
        try {
            return mService.disableMute(muteType);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return AUDIO_RETURN_NOT_OK;
    }

    /**
     * Enable SRS sound effect function
     *
     * @param isEnable to enable/disable SRS
     * @throws TvCommonException
     */
    public void enableSRS(boolean isEnable) {
        Log.d(TAG, "enableSRS(), paras isEnable = " + isEnable);
        try {
            mService.enableSRS(isEnable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get SRS sound effect setting
     *
     * @return boolean for SRS status
     * @throws TvCommonException
     */
    public boolean isSRSEnable() {
        boolean bRet = false;
        try {
            bRet = mService.isSRSEnable();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "isSRSEnable(), return = " + bRet);
        return bRet;
    }

    /**
     * set Absolute Volume function for AD.
     *
     * @param volume AD volume
     * @throws TvCommonException
     */
    public void setADAbsoluteVolume(int volume) {
        Log.d(TAG, "setADAbsoluteVolume(), paras volume = " + volume);
        try {
            mService.setADAbsoluteVolume(volume);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * get Absolute Volume function for AD.
     *
     * @param volume AD volume
     * @throws TvCommonException
     */
    public int getADAbsoluteVolume() {
        int retVolume = 0;
        try {
            retVolume = mService.getADAbsoluteVolume();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getADAbsoluteVolume(), get volume = " + retVolume);
        return retVolume;
    }

    /**
     * set OnOff for AD.
     *
     * @param enable to enable AD function
     * @throws TvCommonException
     */
    public void setADEnable(boolean enable) {
        Log.d(TAG, "setADEnable(), paras enable = " + enable);
        try {
            int cmd = (enable == true) ? TvChannelManager.M5_ICS_AD_ON:TvChannelManager.M5_ICS_AD_OFF;
            TvChannelManager.getInstance().sendMheg5IcsCommand(cmd, TvChannelManager.M5_ICS_FREE_STATUS);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            mService.setADEnable(enable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * get AD enable status.
     *
     * @return is enable AD function
     * @throws TvCommonException
     */
    public boolean getADEnable() {
        boolean bRet = false;
        try {
            bRet = mService.getADEnable();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getADEnable(), return enable = " + bRet);
        return bRet;
    }

    /**
     * set state for HOH.
     *
     * @param state for HOH status
     * @throws TvCommonException
     */
    public void setHOHStatus(boolean state) {
        Log.d(TAG, "setHOHStatus(), paras state = " + state);
        try {
            mService.setHOHStatus(state);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * get HOH status
     *
     * @return state for HOH
     * @throws TvCommonException
     */
    public boolean getHOHStatus() {
        boolean bStatus = false;
        try {
            bStatus = mService.getHOHStatus();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getHOHStatus(), return state = " + bStatus);
        return bStatus;
    }

    /**
     * Set Speaker Volume.
     *
     * @param value of volume
     */
    public void setSpeakerVolume(int volume) {
        Log.d(TAG, "setSpeakerVolume(), paras volume = " + volume);
        try {
            mService.setSpeakerVolume(volume);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get Speaker volume.
     *
     * @return value of Volume
     */
    public int getSpeakerVolume() {
        int i = -1;
        try {
            i = mService.getSpeakerVolume();
            Log.d(TAG, "getSpeakerVolume(), return int " + i);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * Get sound speaker delay time.
     *
     * @return value of Delay
     */
    public int getSoundSpeakerDelay() {
        int i = -1;
        try {
            i = mService.getSoundSpeakerDelay();
            Log.d(TAG, "getSoundSpeakerDelay(), return int " + i);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * Set sound speaker delay time. For Audio and video synchronization, users can
     * decide the delay time of the sound through the UI interface.
     *
     * @param value of delay, 0 ~ max.ms (unit:ms). max depend on chip resource (200~ )
     * @return none
     */
    public void setSoundSpeakerDelay(int delay) {
        Log.d(TAG, "setSoundSpeakerDelay(), paras delay = " + delay);
        try {
            mService.setSoundSpeakerDelay(delay);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get sound SPDIF delay time.
     *
     * @return value of Delay, 0 ~ 250 (unit:ms)
     */
    public int getSoundSpdifDelay() {
        int i = -1;
        try {
            i = mService.getSoundSpdifDelay();
            Log.d(TAG, "getSoundSpdifDelay(), return int " + i);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * Set sound SPDIF delay time. For Audio and video synchronization, users can
     * decide the delay time of the sound through the UI interface.
     *
     * @param value of delay, 0 ~ 250 (unit:ms)
     */
    public void setSoundSpdifDelay(int delay) {
        Log.d(TAG, "setSoundSpdifDelay(), paras delay = " + delay);
        try {
            mService.setSoundSpdifDelay(delay);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set HDMITx HDByPass mode.
     *
     * @param boolean true: turn on HD ByPass mode, false: turn off HD ByPass mode.
     */
    public void setHDMITx_HDByPass(boolean enable) {
        Log.d(TAG, "setHDMITx_HDByPass(), paras enable = " + enable);
        try {
            mService.setHDMITx_HDByPass(enable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get HDMITx HDByPass mode status.
     *
     * @return boolean  true: turn on HD ByPass mode, false: turn off HD ByPass mode.
     */
    public boolean getHDMITx_HDByPass() {
        Log.d(TAG, "getHDMITx_HDByPass()");
        try {
            return mService.getHDMITx_HDByPass();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get HDMITx HDByPass Mode Capability.
     * UI can depend on this result to decide to show the option button or not.
     *
     * @return boolean  true: support, false: unsupport.
     */
    public boolean isSupportHDMITx_HDByPassMode() {
        Log.d(TAG, "isSupportHDMITx_HDByPassMode()");
        try {
            return mService.isSupportHDMITx_HDByPassMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * set audio output source
     *
     * <p> The supported Audio output path are:
     * <ul>
     * <li> {@link #AUDIO_PATH_SPEAKER_OUT}
     * <li> {@link #AUDIO_PATH_HP_OUT}
     * <li> {@link #AUDIO_PATH_LINE_OUT}
     * <li> {@link #AUDIO_PATH_SCART1_OUT}
     * <li> {@link #AUDIO_PATH_SCART2_OUT}
     * <li> {@link #AUDIO_PATH_SPDIF_OUT}
     * </ul>
     * <p> The supported Audio output Source are:
     * <ul>
     * <li> {@link #AUDIO_OUTPUT_SOURCE_MAIN}
     * <li> {@link #AUDIO_OUTPUT_SOURCE_SUB}
     * <li> {@link #AUDIO_OUTPUT_SOURCE_SCART}
     * </ul>
     *
     * @param int enAudioPath the audio path type
     * @see TvAudioManager#AUDIO_PATH_SPEAKER_OUT
     * @see TvAudioManager#AUDIO_PATH_HP_OUT
     * @see TvAudioManager#AUDIO_PATH_LINE_OUT
     * @see TvAudioManager#AUDIO_PATH_SCART1_OUT
     * @see TvAudioManager#AUDIO_PATH_SCART2_OUT
     * @see TvAudioManager#AUDIO_PATH_SPDIF_OUT
     *
     * @param int enSource the output  source type
     * @see TvAudioManager#AUDIO_OUTPUT_SOURCE_MAIN
     * @see TvAudioManager#AUDIO_OUTPUT_SOURCE_SUB
     * @see TvAudioManager#AUDIO_OUTPUT_SOURCE_SCART
     *
     * @return int audioReturn
     * @see TvAudioManager#AUDIO_RETURN_NOT_OK
     * @see TvAudioManager#AUDIO_RETURN_OK
     * @see TvAudioManager#AUDIO_RETURN_UNSUPPORT
     */

    public int setOutputSourceInfo(int enAudioPath, int enSource) {
        Log.d(TAG, "setOutputSourceInfo()");
        try {
            return mService.setOutputSourceInfo(enAudioPath, enSource);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return AUDIO_RETURN_NOT_OK;
    }

    @Override
    public boolean onEvent(Message msg) throws RemoteException {
        if (mHandler != null) {
            Message msgTmp = mHandler.obtainMessage();
            msgTmp.copyFrom(msg);
            mHandler.sendMessage(msgTmp);
        }
        return true;
    }


    /**
     * Register your listener class to
     * registerOnAudioEventListener(OnAudioEventListener listener), your
     * implements will be triggered when the events posted from native code.
     * note: remember to unregister the listener before your application on
     * destroy.
     *
     * @param listener OnAudioEventListener
     * @deprecated Use {@link #registerOnVolumeEventListener(OnVolumeEventListener)}
     */
    @Deprecated
    public void registerOnAudioEventListener(OnAudioEventListener listener) {
        Log.d(TAG, "registerOnAudioEventListener");
        synchronized(mAudioListeners) {
            mAudioListeners.add(listener);
        }
    }

    /**
     * Unegister your unregisterOnAudioEventListener class to service. Remember
     * to unregister the listener before your application on destroy.
     *
     * @param listener unregisterOnAudioEventListener
     * @deprecated Use {@link #unregisterOnVolumeEventListener(OnVolumeEventListener)}
     */
    @Deprecated
    public synchronized boolean unregisterOnAudioEventListener(OnAudioEventListener listener) {
        synchronized(mAudioListeners) {
            mAudioListeners.remove(listener);
            Log.d(TAG, "unregisterOnAudioEventListener  size: " + mAudioListeners.size());
        }
        return true;
    }

    /**
     * Register registerOnVolumeEventListener(OnVolumeEventListener listener), your
     * listener will be triggered when the events posted from native code.
     * Note: Remember to unregister the listener before your application on destroy.
     * @param listener OnVolumeEventListener
     * @return TRUE - register success, FALSE - register fail.
     */
    public boolean registerOnVolumeEventListener(OnVolumeEventListener listener) {
        Log.d(TAG, "registerOnVolumeEventListener");
        synchronized (mVolumeEventListeners) {
            mVolumeEventListeners.add(listener);
        }
        return true;
    }

    /**
     * Unegister your unregisterOnVolumeEventListener class to service.
     * Remember to unregister the listener before your application on destroy.
     * @param listener OnVolumeEventListener
     * @return TRUE - unregister success, FALSE - unregister fail.
     */
    public boolean unregisterOnVolumeEventListener(OnVolumeEventListener listener) {
        synchronized (mVolumeEventListeners) {
            mVolumeEventListeners.remove(listener);
            Log.d(TAG, "unregisterOnVolumeEventListener  size: " + mVolumeEventListeners.size());
        }
        return true;
    }

    /**
     * To finalize TvAudioManager
     *
     */
    protected void finalize() throws Throwable {
        Log.d(TAG, "finalize TvAudioManager ");
        try {
            mService.removeClient(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
	
	// EosTek Patch Begin
	public int getBassBySoundMode(int soundMode) {
        int i = -1;
        try {
            i = mService.getBassBySoundMode(soundMode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return i;
    }

	public int getTrebleBySoundMode(int soundMode) {
        int i = -1;
        try {
            i = mService.getTrebleBySoundMode(soundMode);
            Log.d(TAG, "getTrebleBySoundMode(), return int " + i);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return i;
    }

	public boolean setSoundModeAll(int soundMode, int bass, int treble) {
        try {
            return mService.setSoundModeAll(soundMode, bass, treble);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }
	// EosTek Patch End
}
