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

package com.mstar.android.tvapi.common;

import java.lang.ref.WeakReference;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;

import com.mstar.android.tvapi.common.listener.OnEventListener;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.listener.OnAudioEventListener;
import com.mstar.android.tvapi.common.vo.AdvancedSoundParameter;
import com.mstar.android.tvapi.common.vo.AtvSystemStandard.EnumAtvSystemStandard;
import com.mstar.android.tvapi.common.vo.AudioCommonInfoType.EnumAudioCommonInfoType;
import com.mstar.android.tvapi.common.vo.AudioOutParameter;
import com.mstar.android.tvapi.common.vo.DtvSoundEffect;
import com.mstar.android.tvapi.common.vo.EnumAdvancedSoundParameterType;
import com.mstar.android.tvapi.common.vo.EnumAdvancedSoundSubProcessType;
import com.mstar.android.tvapi.common.vo.EnumAdvancedSoundType;
import com.mstar.android.tvapi.common.vo.EnumAtvAudioModeType;
import com.mstar.android.tvapi.common.vo.EnumAtvInfoType;
import com.mstar.android.tvapi.common.vo.EnumAudioInputLevelSourceType;
import com.mstar.android.tvapi.common.vo.EnumAudioOutType;
import com.mstar.android.tvapi.common.vo.EnumAudioProcessorType;
import com.mstar.android.tvapi.common.vo.EnumAudioReturn;
import com.mstar.android.tvapi.common.vo.EnumAudioVolumeSourceType;
import com.mstar.android.tvapi.common.vo.EnumAuidoCaptureDeviceType;
import com.mstar.android.tvapi.common.vo.EnumAuidoCaptureSource;
import com.mstar.android.tvapi.common.vo.EnumDtvSoundMode;
import com.mstar.android.tvapi.common.vo.EnumEqualizerType;
import com.mstar.android.tvapi.common.vo.EnumKtvAudioMpegSoundMode;
import com.mstar.android.tvapi.common.vo.EnumKtvMixVolumeType;
import com.mstar.android.tvapi.common.vo.EnumMuteStatusType;
import com.mstar.android.tvapi.common.vo.EnumSoundEffectType;
import com.mstar.android.tvapi.common.vo.EnumSoundGetParameterType;
import com.mstar.android.tvapi.common.vo.EnumSoundHidevMode;
import com.mstar.android.tvapi.common.vo.EnumSoundSetParamType;
import com.mstar.android.tvapi.common.vo.EnumSpdifType;
import com.mstar.android.tvapi.common.vo.KtvInfoType.EnumKtvInfoType;
import com.mstar.android.tvapi.common.vo.MuteType.EnumMuteType;
import com.mstar.android.tvapi.common.vo.TvOsType;

/**
 * <p>
 * Title: Audio Manager
 * </p>
 * <p>
 * Description: This class provides the function to manipulate audio functions.
 * </p>
 * ----------------------------------------------------------------------------
 * --------------------<br>
 * [Sample 1] sample code to get AudioManager and manipulate AudioManager class<br>
 * ----------------------------------------------------------------------------
 * --------------------<br>
 * <br>
 * ...<br>
 * private AudioManager am = TvManager.getAudioManager();<br>
 * <br>
 * int volume = 0;<br>
 * try<br>
 * {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;
 * am.setAudioVolume(EnumAudioVolumeSourceType.E_VOL_SOURCE_SPEAKER_OUT,(byte)
 * volume);<br>
 * }<br>
 * catch (TvCommonException e)<br>
 * {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp; e.printStackTrace();<br>
 * }<br>
 * ...<br>
 * <br>
 * <br>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Mstarsemi Inc.
 * </p>
 *
 * @author Jacky.Lin
 * @version 1.0
 */
public final class AudioManager {
    private static final String TAG = "AudioManager";

    /* Do not change these values without updating their counterparts
     * in device/mstar/common/libraries/tv2/java/com/mstar/android/tv2/TvAudioManager.java
     * Currently, we reserve 1000 events for each group for scalability. */
    public static final int TVAUDIO_VOLUME_EVENT_START = 0;
    public static final int TVAUDIO_AP_SET_VOLUME = 1;
    public static final int TVAUDIO_VOLUME_EVENT_END = 999;

    public static final int E_ATVPLAYER_AUTO_TUNING_RECEIVE_EVENT_INTERVAL = (800 * 1000);

    private static AudioManager _audioManager = null;

    protected static AudioManager getInstance() {
        if (_audioManager == null) {
            synchronized (AudioManager.class) {

                if (_audioManager == null) {
                    _audioManager = new AudioManager();
                }
            }
        }
        return _audioManager;
    }

    /* add by owen.qin begin */
    private native void native_setAutoHOHEnable(boolean enable);

    /**
     * Set HOH Status
     *
     * @param enable boolean
     */
    public void setAutoHOHEnable(boolean enable) {
        native_setAutoHOHEnable(enable);
    }

    /* add by owen.qin end */

    /* add by owen.qin begin */
    private native void native_setADEnable(boolean enable);

    private native void native_setADAbsoluteVolume(int volume);

    /**
     * Set the AD (audio description) on/off
     *
     * @param enable boolean
     */
    public void setADEnable(boolean enable) {

        native_setADEnable(enable);

    }

    /**
     * Set the AD volume (audio description) on/off
     *
     * @param volume int
     */
    public void setADAbsoluteVolume(int volume) {

        native_setADAbsoluteVolume(volume);

    }

    /* add by owen.qin end */

    /**
     * Gets the volume value for the sound path which be set
     *
     * @param volSrcType EnumAudioVolumeSourceType
     * @return byte 0~100
     * @throws TvCommonException
     */
    public final byte getAudioVolume(EnumAudioVolumeSourceType volSrcType) throws TvCommonException {
        return native_getAudioVolume(volSrcType.ordinal());
    }

    private native final byte native_getAudioVolume(int volSrcType) throws TvCommonException;

    /**
     * Sets the volume value for the given the sound path
     *
     * @param volSrcType EnumAudioVolumeSourceType source type of audio volume
     * @param volume byte volumn value to be set, 0~100
     * @throws TvCommonException
     */
    public void setAudioVolume(EnumAudioVolumeSourceType volSrcType, byte volume)
    throws TvCommonException {
        native_setAudioVolume(volSrcType.ordinal(), volume);
    }

    private native final void native_setAudioVolume(int volSrcType, byte volume)
    throws TvCommonException;

    /**
     * Set Audio Output
     *
     * @param audioOutType EnumAudioOutType audio out type
     * @param parameter AudioOutParameter
     * @return EnumAudioReturn
     * @throws TvCommonException
     */
    public EnumAudioReturn setAudioOutput(EnumAudioOutType audioOutType, AudioOutParameter parameter)
    throws TvCommonException {
        int iReturn = -1;
        iReturn = native_setAudioOutput(audioOutType.ordinal(), parameter);
        if (iReturn < EnumAudioReturn.E_RETURN_NOTOK.ordinal()
                || iReturn > EnumAudioReturn.E_RETURN_UNSUPPORT.ordinal()) {
            throw new TvCommonException("setAudioOutput failed");
        } else {
            return EnumAudioReturn.values()[iReturn];
        }
    }

    private native int native_setAudioOutput(int audioOutType, AudioOutParameter parameter)
    throws TvCommonException;

    /**
     * Enable mute function for the different mute type
     *
     * @param enMuteType EnumMuteType specify the mute type which want to query
     * @return EnumAudioReturn status of auto volumn fuctionality
     * @throws TvCommonException
     */
    public EnumAudioReturn enableMute(EnumMuteType enMuteType) throws TvCommonException {
        int iReturn = -1;
        iReturn = native_enableMute(enMuteType.getValue());
        if (iReturn < EnumAudioReturn.E_RETURN_NOTOK.ordinal()
                || iReturn > EnumAudioReturn.E_RETURN_UNSUPPORT.ordinal()) {
            throw new TvCommonException("enableMute failed");
        } else {
            return EnumAudioReturn.values()[iReturn];
        }

    }

    private native final int native_enableMute(int enMuteType) throws TvCommonException;

    /**
     * Disable mute function for the different mute type.
     *
     * @param enMuteType one of EnumMuteType
     * @return EnumAudioReturn status of auto volumn fuctionality
     * @throws TvCommonException
     */
    public EnumAudioReturn disableMute(EnumMuteType enMuteType) throws TvCommonException {
        int iReturn = -1;
        iReturn = native_disableMute(enMuteType.getValue());
        if (iReturn < EnumAudioReturn.E_RETURN_NOTOK.ordinal()
                || iReturn > EnumAudioReturn.E_RETURN_UNSUPPORT.ordinal()) {
            throw new TvCommonException("native_disableMute failed");
        } else {
            return EnumAudioReturn.values()[iReturn];
        }

    }

    private native final int native_disableMute(int enMuteType) throws TvCommonException;

    /**
     * Get mute status according the mute type.
     *
     * @param enMuteStatusType EnumMuteStatusType one of EnumMuteStatusType
     * @return boolean
     * @throws TvCommonException
     */
    public final boolean isMuteEnabled(EnumMuteStatusType enMuteStatusType)
    throws TvCommonException {
        return native_isMuteEnabled(enMuteStatusType.ordinal());
    }

    private final native boolean native_isMuteEnabled(int enMuteStatusType)
    throws TvCommonException;

    /**
     * Set Audio ATV MTS mode
     *
     * @param enAtvMtsMode EnumAtvAudioModeType atv mts mode
     * @return EnumAudioReturn
     * @throws TvCommonException
     */
    public EnumAudioReturn setAtvMtsMode(EnumAtvAudioModeType enAtvMtsMode)
    throws TvCommonException {
        int iReturn = -1;
        iReturn = native_setAtvMtsMode(enAtvMtsMode.ordinal());
        if (iReturn < EnumAudioReturn.E_RETURN_NOTOK.ordinal()
                || iReturn > EnumAudioReturn.E_RETURN_UNSUPPORT.ordinal()) {
            throw new TvCommonException("native_SetAtvMtsMode failed");
        } else {
            return EnumAudioReturn.values()[iReturn];
        }

    }

    private native final int native_setAtvMtsMode(int enAtvMtsType) throws TvCommonException;

    /**
     * Get Audio ATV MTS mode
     *
     * @return EnumAtvMtsMode atv mts mode
     * @throws TvCommonException
     */
    public EnumAtvAudioModeType getAtvMtsMode() throws TvCommonException {

        int iReturn = native_getAtvMtsMode();
        if (iReturn < EnumAtvAudioModeType.E_ATV_AUDIOMODE_INVALID.ordinal()
                || iReturn > EnumAtvAudioModeType.E_ATV_AUDIOMODE_NUM.ordinal()) {
            throw new TvCommonException("native_getAtvMtsMode failed");
        } else {
            return EnumAtvAudioModeType.values()[iReturn];
        }

    }

    private native final int native_getAtvMtsMode() throws TvCommonException;

    /**
     * Get the available ATV Sound mode
     *
     * @return EnumAtvAudioModeType atv sound mode
     * @throws TvCommonException
     */
    public EnumAtvAudioModeType getAtvSoundMode() throws TvCommonException {

        int iReturn = native_getAtvSoundMode();
        if (iReturn < EnumAtvAudioModeType.E_ATV_AUDIOMODE_INVALID.ordinal()
                || iReturn > EnumAtvAudioModeType.E_ATV_AUDIOMODE_NUM.ordinal()) {
            throw new TvCommonException("native_getAtvSoundMode failed");
        } else {
            return EnumAtvAudioModeType.values()[iReturn];
        }

    }

    private native final int native_getAtvSoundMode() throws TvCommonException;

    /**
     * Set the atv sound system
     *
     * @param enAtvSystemStandard EnumAtvSystemStandard one of
     *            EnumAtvSystemStandard
     * @return boolean status of set atv sound system
     * @throws TvCommonException
     */
    public boolean setAtvSoundSystem(EnumAtvSystemStandard enAtvSystemStandard)
    throws TvCommonException {
        return native_setAtvSoundSystem(enAtvSystemStandard.getValue());

    }

    private native final boolean native_setAtvSoundSystem(int enAtvSystemStandard)
    throws TvCommonException;

    /**
     * Get the available ATV Sound mode
     *
     * @return EnumAtvSystemStandard currently atv sound system
     * @throws TvCommonException
     */
    public EnumAtvSystemStandard getAtvSoundSystem() throws TvCommonException {
        int ass = native_getAtvSoundSystem();
        int iReturn = EnumAtvSystemStandard.getOrdinalThroughValue(ass);
        if (iReturn < EnumAtvSystemStandard.E_BG.getValue()
                || iReturn > EnumAtvSystemStandard.E_NUM.getValue()) {
            throw new TvCommonException("getAtvSoundSystem failed");
        } else {
            return EnumAtvSystemStandard.values()[iReturn];
        }

    }

    private native final int native_getAtvSoundSystem() throws TvCommonException;

    /**
     * Sets DTV output mode by the given dtv sound mode
     *
     * @param enDtvSoundMode EnumDtvSoundMode one of EnumDtvSoundMode
     * @throws TvCommonException
     */
    public void setDtvOutputMode(EnumDtvSoundMode enDtvSoundMode) throws TvCommonException {
        native_setDtvOutputMode(enDtvSoundMode.ordinal());
    }

    private native final void native_setDtvOutputMode(int enDtvSoundMode) throws TvCommonException;

    /**
     * Gets DTV output mode by the given dtv sound mode
     *
     * @return EnumDtvSoundMode return one of EnumDtvSoundModeS
     * @throws TvCommonException
     */
    public EnumDtvSoundMode getDtvOutputMode() throws TvCommonException {

        int iReturn = native_getDtvOutputMode();
        if (iReturn < EnumDtvSoundMode.E_STEREO.ordinal()
                || iReturn > EnumDtvSoundMode.E_NUM.ordinal()) {
            throw new TvCommonException("getDtvOutputMode failed");
        } else {
            return EnumDtvSoundMode.values()[iReturn];
        }

    }

    private native final int native_getDtvOutputMode() throws TvCommonException;

    /**
     * Sets basic sound effect which do not need license.
     *
     * @param effectType EnumSoundEffectType basic sound effect type
     * @param dtvSoundEffectVo set value of Treble, bass, balance, eq peq value
     * @return EnumAudioReturn status of set basic sound fuctionality
     * @throws TvCommonException
     */
    public final EnumAudioReturn setBasicSoundEffect(EnumSoundEffectType effectType,
            DtvSoundEffect dtvSoundEffectVo) throws TvCommonException {
        int iReturn = native_setBasicSoundEffect(effectType.ordinal(), dtvSoundEffectVo);
        if (iReturn < EnumAudioReturn.E_RETURN_NOTOK.ordinal()
                || iReturn > EnumAudioReturn.E_RETURN_UNSUPPORT.ordinal()) {
            throw new TvCommonException("setBasicSoundEffect failed");
        } else {
            return EnumAudioReturn.values()[iReturn];
        }

    }

    private native final int native_setBasicSoundEffect(int effectType,
            DtvSoundEffect dtvSoundEffectVO) throws TvCommonException;

    /**
     * Gets basic sound effect which do not need license.
     *
     * @param effectType EnumSoundEffectType basic sound effect type
     * @return int return sound effect value
     * @throws TvCommonException
     */
    public final int getBasicSoundEffect(EnumSoundGetParameterType effectType)
    throws TvCommonException {
        return native_getBasicSoundEffect(effectType.ordinal());
    }

    private native final int native_getBasicSoundEffect(int effectType) throws TvCommonException;

    /**
     * Enable/Disable Basic Sound Effect
     *
     * @param soundType EnumSoundEffectType sound effect type
     * @param enable boolean
     * @return EnumAudioReturn E_RETURN_OK, E_RETURN_NOTOK, E_RETURN_NOTSUPPORT
     * @throws TvCommonException
     */
    public EnumAudioReturn enableBasicSoundEffect(EnumSoundEffectType soundType, boolean enable)
    throws TvCommonException {
        int iReturn = native_enableBasicSoundEffect(soundType.ordinal(), enable);
        if (iReturn < EnumAudioReturn.E_RETURN_NOTOK.ordinal()
                || iReturn > EnumAudioReturn.E_RETURN_UNSUPPORT.ordinal()) {
            throw new TvCommonException("native_checkAtvSoundSystem failed");
        } else {
            return EnumAudioReturn.values()[iReturn];
        }

    }

    private native int native_enableBasicSoundEffect(int soundType, boolean enable)
    throws TvCommonException;

    /**
     * Set input level for given audio input source
     *
     * @param enAudioInputLevelSource EnumAudioInputLevelSourceType audio input
     *            level source type
     * @param level the level value to be set
     * @throws TvCommonException
     */
    public void setInputLevel(EnumAudioInputLevelSourceType enAudioInputLevelSource, short level)
    throws TvCommonException {
        native_setInputLevel(enAudioInputLevelSource.ordinal(), level);
    }

    private native final void native_setInputLevel(int enAudioInputLevelSource, short level)
    throws TvCommonException;

    /**
     * Get input level for MPC, MP3, PCM
     *
     * @param enAudioInputLevelSource EnumAudioInputLevelSourceType audio input
     *            level source type
     * @throws NotSupportException
     * @return the level of the auido input source
     * @throws TvCommonException
     */
    public short getInputLevel(EnumAudioInputLevelSourceType enAudioInputLevelSource)
    throws TvCommonException {
        return native_getInputLevel(enAudioInputLevelSource.ordinal());
    }

    private native final short native_getInputLevel(int enAudioInputLevelSource)
    throws TvCommonException;

    /**
     * This routine is used to set S/PDIF output mode
     *
     * @param enSpdifMode EnumSpdifMode SPDIF mode
     * @throws TvCommonException
     */
    public void setDigitalOut(EnumSpdifType enSpdifMode) throws TvCommonException {
        native_setDigitalOut(enSpdifMode.ordinal());
    }

    private native final void native_setDigitalOut(int enSpdifMode) throws TvCommonException;

    /**
     * Sets audio input source
     *
     * @param enAudioInputSource TvOsType.EnumInputSource audio input source
     *            type
     * @throws TvCommonException
     */
    public void setInputSource(TvOsType.EnumInputSource enAudioInputSource)
    throws TvCommonException {
        native_setInputSource(enAudioInputSource.ordinal());
    }

    private native final void native_setInputSource(int enAudioInputSource)
    throws TvCommonException;

    /**
     * Check ATV sound system
     *
     * @return EnumAudioReturn
     * @throws TvCommonException
     */
    public EnumAudioReturn checkAtvSoundSystem() throws TvCommonException {
        int iReturn = native_checkAtvSoundSystem();
        if (iReturn < EnumAudioReturn.E_RETURN_NOTOK.ordinal()
                || iReturn > EnumAudioReturn.E_RETURN_UNSUPPORT.ordinal()) {
            throw new TvCommonException("native_checkAtvSoundSystem failed");
        } else {
            return EnumAudioReturn.values()[iReturn];
        }

    }

    private native int native_checkAtvSoundSystem() throws TvCommonException;

    /**
     * Set ATV Info
     *
     * @param infoType EnumAtvInfoType ATV Info
     * @param atvInfoConfig AtvInfoConfig1 ATV Info CFG
     * @return EnumAudioReturn
     * @throws TvCommonException
     */
    public EnumAudioReturn setAtvInfo(EnumAtvInfoType infoType, EnumSoundHidevMode atvInfoConfig)
    throws TvCommonException {
        int iReturn = native_setAtvInfo(infoType.ordinal(), atvInfoConfig.ordinal());
        if (iReturn < EnumAudioReturn.E_RETURN_NOTOK.ordinal()
                || iReturn > EnumAudioReturn.E_RETURN_UNSUPPORT.ordinal()) {
            throw new TvCommonException("native_setAtvInfo failed");
        } else {
            return EnumAudioReturn.values()[iReturn];
        }

    }

    private native int native_setAtvInfo(int infotype, int atvInfoConfig) throws TvCommonException;

    /**
     * Get atv info type
     *
     * @return EnumAtvInfoType
     * @throws TvCommonException
     */
    public EnumAtvInfoType getAtvInfo() throws TvCommonException {
        int iReturn = native_getAtvInfo();
        if (iReturn < EnumAtvInfoType.E_ATV_HIDEV_INFO.ordinal()
                || iReturn > EnumAtvInfoType.E_ATV_HIDEV_INFO.ordinal()) {
            throw new TvCommonException("native_getAtvInfo failed");
        } else {
            return EnumAtvInfoType.values()[iReturn];
        }
    }

    private native int native_getAtvInfo() throws TvCommonException;

    /**
     * Get Audio InputSource Switch
     *
     * @return TvOsType.EnumInputSource audio input source type
     * @throws TvCommonException
     */
    public TvOsType.EnumInputSource getInputSource() throws TvCommonException {

        int iReturn = native_getInputSource();
        if ((iReturn < TvOsType.EnumInputSource.E_INPUT_SOURCE_VGA.ordinal()
                || iReturn > TvOsType.EnumInputSource.E_INPUT_SOURCE_NONE.ordinal())
            && (iReturn != TvOsType.EnumInputSource.E_INPUT_SOURCE_VGA2.ordinal())
            && (iReturn != TvOsType.EnumInputSource.E_INPUT_SOURCE_VGA3.ordinal())) {
            throw new TvCommonException("getInputSource failed");
        } else {
            return TvOsType.EnumInputSource.values()[iReturn];
        }

    }

    private native final int native_getInputSource() throws TvCommonException;

    /**
     * setVideoMute
     *
     * @param screenUnMuteTime int
     * @param eSrcType TvOsType.EnumInputSource
     * @return boolean
     * @throws TvCommonException
     */
    public final boolean setMuteStatus(int screenUnMuteTime, TvOsType.EnumInputSource eSrcType)
    throws TvCommonException {
        return native_setMuteStatus(screenUnMuteTime, eSrcType.ordinal());
    }

    private native final boolean native_setMuteStatus(int screenUnMuteTime, int eSrcType)
    throws TvCommonException;

    /**
     * Set Audio ATV Next MTS mode
     *
     * @return EnumAudioReturn status of set to next atv mts mode
     * @throws TvCommonException
     */
    public EnumAudioReturn setToNextAtvMtsMode() throws TvCommonException {
        int iReturn = -1;
        iReturn = native_setToNextAtvMtsMode();

        if (iReturn < EnumAudioReturn.E_RETURN_NOTOK.ordinal()
                || iReturn > EnumAudioReturn.E_RETURN_UNSUPPORT.ordinal()) {
            throw new TvCommonException("setToNextAtvMtsMode failed");
        } else {
            return EnumAudioReturn.values()[iReturn];
        }
    }

    private native final int native_setToNextAtvMtsMode() throws TvCommonException;

    public native final void setDebugMode(boolean mode) throws TvCommonException;

    /**
     * Enable Advanced Sound Effect Type
     *
     * @param soundType EnumAdvancedSoundType advanced sound effect type
     * @param subProcessType EnumAdvancedSoundSubProcessType subprocess of
     *            advanced sound effect
     * @return EnumAudioReturn E_RETURN_OK, E_RETURN_NOTOK, E_RETURN_NOTSUPPORT
     * @throws TvCommonException
     */
    public EnumAudioReturn enableAdvancedSoundEffect(EnumAdvancedSoundType soundType,
            EnumAdvancedSoundSubProcessType subProcessType) throws TvCommonException {
        int iReturn = native_enableAdvancedSoundEffect(soundType.ordinal(),
                      subProcessType.ordinal());
        if (iReturn < EnumAudioReturn.E_RETURN_NOTOK.ordinal()
                || iReturn > EnumAudioReturn.E_RETURN_UNSUPPORT.ordinal()) {
            throw new TvCommonException("native_enableAdvancedSoundEffect failed");
        } else {
            return EnumAudioReturn.values()[iReturn];
        }

    }

    private native int native_enableAdvancedSoundEffect(int soundType, int subProcessType)
    throws TvCommonException;

    /**
     * Set advanced sound effect parameter.
     *
     * @param advancedSoundParamType EnumAdvancedSoundParameterType
     * @param advancedSoundParameterVo AdvancedSoundParameter
     * @return EnumAudioReturn E_RETURN_OK, E_RETURN_NOTOK, E_RETURN_NOTSUPPORT
     * @throws TvCommonException
     */
    public final EnumAudioReturn setAdvancedSoundEffect(
        EnumAdvancedSoundParameterType advancedSoundParamType,
        AdvancedSoundParameter advancedSoundParameterVo) throws TvCommonException {
        int iReturn = native_setAdvancedSoundEffect(advancedSoundParamType.ordinal(),
                      advancedSoundParameterVo);
        if (iReturn < EnumAudioReturn.E_RETURN_NOTOK.ordinal()
                || iReturn > EnumAudioReturn.E_RETURN_UNSUPPORT.ordinal()) {
            throw new TvCommonException("native_setAdvancedSoundEffect failed");
        } else {
            return EnumAudioReturn.values()[iReturn];
        }

    }

    private native final int native_setAdvancedSoundEffect(int advancedSoundParamType,
            AdvancedSoundParameter advancedSoundParameterVo) throws TvCommonException;

    /**
     * Gets advanced sound effect
     *
     * @param advancedSoundParamType EnumAdvancedSoundParameterType
     * @return int return sound effect value
     * @throws TvCommonException
     */
    public final int getAdvancedSoundEffect(EnumAdvancedSoundParameterType advancedSoundParamType)
    throws TvCommonException {
        return native_getAdvancedSoundEffect(advancedSoundParamType.ordinal());
    }

    private native final int native_getAdvancedSoundEffect(int effectType) throws TvCommonException;

    /**
     * To Set sub woofer volume and mute.
     *
     * @param mute boolean TRUE for Mute enable; FALSE for Mute disable.
     * @param value short Volume Value(0~100), set 0 is mute.
     * @return short
     * @throws TvCommonException
     */
    public native final short setSubWooferVolume(boolean mute, short value)
    throws TvCommonException;

    /**
     * Reserve extend command for customer. If you don't need it, you skip it.
     *
     * @param subCmd short Commad defined by the customer.
     * @param param1 int Defined by the customer.
     * @param param2 int Defined by the customer.
     * @param Param3 int[] Defined by the customer.
     * @return short 0 false 1 true
     * @throws TvCommonException
     */

    public native final short executeAmplifierExtendedCommand(short subCmd, int param1, int param2,
            int[] Param3) throws TvCommonException;

    /**
     * mute amplifier
     *
     * @param bmute boolean
     * @return boolean false true
     * @throws TvCommonException
     */

    public native final boolean setAmplifierMute(boolean bmute) throws TvCommonException;

    /**
     * set amplifier amplifier EQ by mode
     *
     * @param equalizertype EnumEqualizerType
     * @throws TvCommonException
     */
    public final void setAmplifierEqualizerByMode(EnumEqualizerType equalizertype)
    throws TvCommonException {
        native_setAmplifierEqualizerByMode(equalizertype.ordinal());
    }

    private native void native_setAmplifierEqualizerByMode(int equalizertype);

    /*
     * set sound param for each sound type
     * @param eSoundSetParamType EnumSoundSetParamType the sound type
     * @param param1 short
     * @param param2 short
     * @return short 0 is success other is failed
     */

    public final short setSoundParameter(EnumSoundSetParamType eSoundSetParamType, int param1,
                                         int param2) {
        return native_SetSoundParameter(eSoundSetParamType.ordinal(), param1, param2);
    }

    private final native short native_SetSoundParameter(int soundSetParamType, int param1,
            int param2);

    /*
     * get sound param for each sound type
     * @param eSoundSetParamType EnumSoundSetParamType the sound type
     * @param param1 short
     * @return short
     */

    public final int getSoundParameter(EnumSoundSetParamType eSoundSetParamType, int param1) {
        return native_getSoundParameter(eSoundSetParamType.ordinal(), param1);
    }

    private final native int native_getSoundParameter(int soundParamType, int param1);

    @Deprecated
    public final native short setSoundSpeakerDelay(int delay);
    public final native short setSpeakerDelay(int delay);

    @Deprecated
    public final native short setSoundSpdifDelay(int delay);
    public final native short setSpdifDelay(int delay);

    /*
     * set each volume in KTV mode
     * @param eKtvMixVolumeType EnumKtvMixVolumeType ktv volume type
     * @param volume1 short MSB 7-bit register value of 10-bit u8Volume (0x00 ~
     * 0x7E, gain: +12db to -114db (-1 db per step))
     * @param volume2 short LSB 3-bit register value of 10-bit u8Volume (0x00 ~
     * 0x07, gain: -0db to -0.875db (-0.125 db per step))
     * @return short 0 is success other is failed
     */
    public final short setKtvMixModeVolume(EnumKtvMixVolumeType eKtvMixVolumeType, short volume1,
                                           short volume2) {
        return native_setKtvMixModeVolume(eKtvMixVolumeType.ordinal(), volume1, volume2);
    }

    private final native short native_setKtvMixModeVolume(int ktvMixVolumeType, short volume1,
            short volume2);

    /*
     * enable each volume in KTV mode to Mute
     * @param eKtvMixVolumeType EnumKtvMixVolumeType the KTV mix Volume type
     * @return short 0 is success other is failed
     */

    public final short enableKtvMixModeMute(EnumKtvMixVolumeType eKtvMixVolumeType) {
        return native_enableKtvMixModeMute(eKtvMixVolumeType.ordinal());
    }

    private final native short native_enableKtvMixModeMute(int ktvMixVolume);

    /*
     * disable each volume in KTV mode to Mute
     * @param eKtvMixVolumeType EnumKtvMixVolumeType the KTV mix Volume type
     * @return short 0 is success other is failed
     */

    public final short disableKtvMixModeMute(EnumKtvMixVolumeType eKtvMixVolumeType) {
        return native_disableKtvMixModeMute(eKtvMixVolumeType.ordinal());
    }

    private final native short native_disableKtvMixModeMute(int ktvMixVolume);

    /*
     * set audio Capture Source
     * @param eAudioCaptureSource EnumAuidoCaptureSource the audio source
     * @return EnumAudioReturn
     */

    public final EnumAudioReturn setAudioCaptureSource(
        EnumAuidoCaptureDeviceType eAudioCaptureDeviceType,
        EnumAuidoCaptureSource eAudioCaptureSource) throws TvCommonException {

        short iReturn = native_setAudioCaptureSource(eAudioCaptureDeviceType.ordinal(),
                        eAudioCaptureSource.ordinal());

        if (iReturn < EnumAudioReturn.E_RETURN_NOTOK.ordinal()
                || iReturn > EnumAudioReturn.E_RETURN_UNSUPPORT.ordinal()) {
            throw new TvCommonException("native_setAudioCaptureSource  failed");
        } else {
            return EnumAudioReturn.values()[iReturn];
        }

    }

    private native final short native_setAudioCaptureSource(int audioCaptureDeviceType,
            int audioCaptureSource);


    /*
    *set audio output source
    *@param eSource EnumAuidoCaptureSource
    *@return EnumAudioReturn
    */
    public final EnumAudioReturn setOutputSourceInfo(EnumAudioVolumeSourceType eAudioPath,EnumAudioProcessorType eSource)
    throws TvCommonException {
        short iReturn = native_setOutputSourceInfo(eAudioPath.ordinal(),  eSource.ordinal());
        if (iReturn < EnumAudioReturn.E_RETURN_NOTOK.ordinal()
                || iReturn > EnumAudioReturn.E_RETURN_UNSUPPORT.ordinal()) {
            throw new TvCommonException("native_setAudioCaptureSource  failed");
        } else {
            return EnumAudioReturn.values()[iReturn];
        }
    }

    private native final short native_setOutputSourceInfo(int audioPath , int source);

    /*
     * set ktv sound info
     * @param ektvInfoType EnumKtvInfoType ktv info type
     * @param param1 int
     * @param param2 int
     * @return short 0 is success other is failed
     */

    public final short setKtvSoundInfo(EnumKtvInfoType ektvInfoType, int param1, int param2)
    throws TvCommonException {
        return native_setKtvSoundInfo(ektvInfoType.getValue(), param1, param2);
    }

    private native final short native_setKtvSoundInfo(int ktvInfoType, int param1, int param2)
    throws TvCommonException;

    /*
     * get ktv sound info
     * @param ektvInfoType EnumKtvInfoType ktv info type
     * @return int
     */

    public final int getKtvSoundInfo(EnumKtvInfoType ektvInfoType) throws TvCommonException {
        return native_getKtvSoundInfo(ektvInfoType.getValue());
    }

    private native final int native_getKtvSoundInfo(int ktvInfoType) throws TvCommonException;

    /*
     * set sound track mode for KTV
     * @param enSoundMode EnumKtvAudioMpegSoundMode
     * @return int 0 is sucuess other is failed
     */

    public int setKtvSoundTrack(EnumKtvAudioMpegSoundMode enSoundMode) {
        return native_setKtvSoundTrack(enSoundMode.ordinal());
    }

    private native int native_setKtvSoundTrack(int soundMode);

    /*
     * set sound track mode for KTV
     * @param audioinfoType EnumAudioCommonInfoType
     * @param param1 int
     * @param param2 int
     * @return int 0 is sucuess other is failed
     */

    public boolean setCommonAudioInfo(EnumAudioCommonInfoType audioinfoType, int param1, int param2) {
        return native_setCommonAudioInfo(audioinfoType.getValue(), param1, param2);
    }

    private native boolean native_setCommonAudioInfo(int audioinfoType, int param1, int param2);

    /*
     * set sound track mode for KTV
     * @param audioinfoType EnumAudioCommonInfoType
     * @param param1 int
     * @param param2 int
     * @return int 0 is sucuess other is failed
     */

    public int setAudioSource(TvOsType.EnumInputSource eInputSrc,
                              EnumAudioProcessorType eAudioProcessType) {
        return native_setAudioSource(eInputSrc.ordinal(), eAudioProcessType.ordinal());
    }

    private native int native_setAudioSource(int eInputSrc, int eAudioProcessType);

    /*
     * set primary audio language
     * @param enLanguage Audio Language
     */

    public void setAudioPrimaryLanguage(int enLanguage) {
        native_setAudioLanguage1(enLanguage);
    }

    private native void native_setAudioLanguage1(int enLanguage);

    /*
     * set secondary audio language
     * @param enLanguage Audio Language
     */

    public void setAudioSecondaryLanguage(int enLanguage) {
        native_setAudioLanguage2(enLanguage);
    }

    private native void native_setAudioLanguage2(int enLanguage);

    /*
     * get primary audio language
     * @return primary audio language
     */

    public int getAudioPrimaryLanguage() {
        return native_getAudioLanguage1();
    }

    private native int native_getAudioLanguage1();

    /*
     * get secondary audio language
     * @return secondary audio language
     */

    public int getAudioSecondaryLanguage() {
        return native_getAudioLanguage2();
    }
		// EosTek Patch Begin
	/*
     * setMicSSound 
     * 
     * @return none
     * @throws TvCommonException
     */
   public native void setMicSSound(int val) throws TvCommonException;
   /*
     * setMicEcho 
     * 
     * @return none
     * @throws TvCommonException
     */
     public native void setMicEcho(int val) throws TvCommonException;
    /*
     * getMicSSound  
     * 
     * @return none
     * @throws TvCommonException
     */
   public native int getMicSSound() throws TvCommonException;
	 /*
     * getMicEcho 
     * 
     * @return none
     * @throws TvCommonException
     */

    public native int getMicEcho() throws TvCommonException;
	 // EosTek Patch End

    private native int native_getAudioLanguage2();

    public native final void setAutoVolume(boolean enable) throws TvCommonException;

    public native final boolean getAutoVolume() throws TvCommonException;

    public native final int getSoundAC3PlusInfo(int infoType) throws TvCommonException;

    public native final boolean setSoundAC3PlusInfo(int infoType, int param1, int param2) throws TvCommonException;

    public native final short setOutputSourceInfo(int path, int source) throws TvCommonException;

    public native final void setHDMITx_HDBypass(boolean enable) throws TvCommonException;

    public native final boolean getHDMITx_HDBypass() throws TvCommonException;

    public native final boolean getHDMITx_HDBypass_Capability() throws TvCommonException;

    private AudioManager() {
        Looper looper;
        if ((looper = Looper.myLooper()) != null) {
            mEventHandler = new EventHandler(this, looper);
        } else if ((looper = Looper.getMainLooper()) != null) {
            mEventHandler = new EventHandler(this, looper);
        } else {
            mEventHandler = null;
        }

        native_setup(new WeakReference<AudioManager>(this));
    }

    /**
     * Register setOnEventListener(OnEventListener listener), your
     * listener will be triggered when the events posted from native
     * code.
     *
     * @param listener OnEventListener
     */
    public void setOnEventListener(OnEventListener listener) {
        mOnEventListener = listener;
    }

    /**
     * Register setOnAudioEventListener(OnAudioEventListener listener), your
     * listener will be triggered when the events posted from native code.
     *
     * @param listener OnAudioEventListener
     * @deprecated Use {@link setOnEventListener(OnEventListener
     *             listener)}.
     */
    public void setOnAudioEventListener(OnAudioEventListener listener) {
        mOnAudioEventListener = listener;
    }

    private class EventHandler extends Handler {
        private AudioManager mMSrv;

        public EventHandler(AudioManager srv, Looper looper) {
            super(looper);
            mMSrv = srv;
        }

        @Override
        public void handleMessage(Message msg) {
            if (mMSrv.mNativeContext == 0) {
                return;
            }

            if (mOnEventListener != null) {
                mOnEventListener.onEvent(msg);
            }

            // FIXME: old architecture, remove later
            switch (msg.what) {
                case TVAUDIO_AP_SET_VOLUME:
                    if (mOnAudioEventListener != null) {
                        mOnAudioEventListener.onApSetVolumeEvent(msg.what);
                    }
                    return;
                default:
                    Log.d(TAG, "Unknown message type " + msg.what);
                    return;
            }
        }
    }

    private static void postEventFromNative(Object srv_ref, int what, int arg1, int arg2, Object obj) {
        AudioManager srv = (AudioManager)((WeakReference) srv_ref).get();
        if (srv == null) {
            return;
        }

        if (srv.mEventHandler != null) {
            Message m = srv.mEventHandler.obtainMessage(what, arg1, arg2, obj);
            srv.mEventHandler.sendMessage(m);
        }

        Log.d(TAG, "Native Audio callback , postEventFromNative");
        return;
    }

    private long mNativeContext; // accessed by native methods

    private int mAudioManagerContext; // accessed by native methods

    private OnEventListener mOnEventListener;

    // FIXME: old architecture, remove later
    private OnAudioEventListener mOnAudioEventListener;

    private EventHandler mEventHandler;

    static {
        try {
            System.loadLibrary("audiomanager_jni");
            native_init();
        } catch (UnsatisfiedLinkError e) {
            Log.d(TAG, "Cannot load audiomanager_jni library:\n" + e.toString());
        }
    }

    private static native final void native_init();

    private native final void native_setup(Object msrv_this);

    private native final void native_finalize();

    protected void release() throws Throwable {
        _audioManager = null;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        native_finalize();
        _audioManager = null;
    }
}
