/**
 *<MStar Software>
 *******************************************************************************
 * MStar Software
 * Copyright (c) 2010 - 2015 MStar Semiconductor, Inc. All rights reserved.
 * All software, firmware and related documentation herein ("MStar Software") are
 * intellectual property of MStar Semiconductor, Inc. ("MStar") and protected by
 * law, including, but not limited to, copyright law and international treaties.
 * Any use, modification, reproduction, retransmission, or republication of all
 * or part of MStar Software is expressly prohibited, unless prior written
 * permission has been granted by MStar.
 *
 * By accessing, browsing and/or using MStar Software, you acknowledge that you
 * have read, understood, and agree, to be bound by below terms ("Terms") and to
 * comply with all applicable laws and regulations:
 *
 * 1. MStar shall retain any and all right, ownership and interest to MStar
 *    Software and any modification/derivatives thereof.
 *    No right, ownership, or interest to MStar Software and any
 *    modification/derivatives thereof is transferred to you under Terms.
 *
 * 2. You understand that MStar Software might include, incorporate or be
 *    supplied together with third party's software and the use of MStar
 *    Software may require additional licenses from third parties.
 *    Therefore, you hereby agree it is your sole responsibility to separately
 *    obtain any and all third party right and license necessary for your use of
 *    such third party's software.
 *
 * 3. MStar Software and any modification/derivatives thereof shall be deemed as
 *    MStar's confidential information and you agree to keep MStar's
 *    confidential information in strictest confidence and not disclose to any
 *    third party.
 *
 * 4. MStar Software is provided on an "AS IS" basis without warranties of any
 *    kind. Any warranties are hereby expressly disclaimed by MStar, including
 *    without limitation, any warranties of merchantability, non-infringement of
 *    intellectual property rights, fitness for a particular purpose, error free
 *    and in conformity with any international standard.  You agree to waive any
 *    claim against MStar for any loss, damage, cost or expense that you may
 *    incur related to your use of MStar Software.
 *    In no event shall MStar be liable for any direct, indirect, incidental or
 *    consequential damages, including without limitation, lost of profit or
 *    revenues, lost or damage of data, and unauthorized system use.
 *    You agree that this Section 4 shall still apply without being affected
 *    even if MStar Software has been modified by MStar in accordance with your
 *    request or instruction for your use, except otherwise agreed by both
 *    parties in writing.
 *
 * 5. If requested, MStar may from time to time provide technical supports or
 *    services in relation with MStar Software to you for your use of
 *    MStar Software in conjunction with your or your customer's product
 *    ("Services").
 *    You understand and agree that, except otherwise agreed by both parties in
 *    writing, Services are provided on an "AS IS" basis and the warranty
 *    disclaimer set forth in Section 4 above shall apply.
 *
 * 6. Nothing contained herein shall be construed as by implication, estoppels
 *    or otherwise:
 *    (a) conferring any license or right to use MStar name, trademark, service
 *        mark, symbol or any other identification;
 *    (b) obligating MStar or any of its affiliates to furnish any person,
 *        including without limitation, you and your customers, any assistance
 *        of any kind whatsoever, or any information; or
 *    (c) conferring any license or right under any intellectual property right.
 *
 * 7. These terms shall be governed by and construed in accordance with the laws
 *    of Taiwan, R.O.C., excluding its conflict of law rules.
 *    Any and all dispute arising out hereof or related hereto shall be finally
 *    settled by arbitration referred to the Chinese Arbitration Association,
 *    Taipei in accordance with the ROC Arbitration Law and the Arbitration
 *    Rules of the Association by three (3) arbitrators appointed in accordance
 *    with the said Rules.
 *    The place of arbitration shall be in Taipei, Taiwan and the language shall
 *    be English.
 *    The arbitration award shall be final and binding to both parties.
 *
 *******************************************************************************
 *<MStar Software>
 */

package com.mstar.android.tv;

interface ITvAudio {
    boolean setSoundMode(in int SoundMode);
    int getSoundMode();
    boolean setEarPhoneVolume(in int volume, in boolean saveDb);
    int getEarPhoneVolume();
    boolean setBass(in int bassValue);
    int getBass();
    boolean setTreble(in int bassValue);
    int getTreble();
    boolean setBalance(in int balanceValue);
    int getBalance();
    boolean setAvcMode(in boolean isAvcEnable);
    boolean getAvcMode();
    boolean setSurroundMode(in int surroundMode);
    int getSurroundMode();
    boolean setSpdifOutMode(in int SpdifMode);
    int getSpdifOutMode();
    boolean setEqBand120(in int eqValue);
    int getEqBand120();
    boolean setEqBand500(in int eqValue);
    int getEqBand500();
    boolean setEqBand1500(in int eqValue);
    int getEqBand1500();
    boolean setEqBand5k(in int eqValue);
    int getEqBand5k();
    boolean setEqBand10k(in int eqValue);
    int getEqBand10k();
    boolean setBassSwitch(in int en);
    int getBassSwitch();
    boolean setBassVolume(in int volume);
    int getBassVolume();
    boolean setPowerOnOffMusic(in int en);
    int getPowerOnOffMusic();
    boolean setWallmusic(in int en);
    int getWallmusic();
    boolean setSeparateHear(in int en);
    int getSeparateHear();
    boolean setTrueBass(in int en);
    int getTrueBass();
    void setDtvOutputMode(in int enDtvSoundMode);
    int getDtvOutputMode();
    int setAudioCaptureSource(int eAudioCaptureDeviceType, int eAudioCaptureSource);
    int enableMute(int enMuteType);
    int disableMute(int enMuteType);
    void registerOnAudioEventListener(in int listener);
    void enableSRS(in boolean isEnable);
    boolean isSRSEnable();
    void setADAbsoluteVolume(in int volume);
    int getADAbsoluteVolume();
    void setADEnable(in boolean enable);
    boolean getADEnable();
    void setHOHStatus(in boolean state);
    boolean getHOHStatus();
    void setSpeakerVolume(int volume);
    int getSpeakerVolume();
    int getSoundSpeakerDelay();
    void setSoundSpeakerDelay(int delay);
    int getSoundSpdifDelay();
    void setSoundSpdifDelay(int delay);
    void setHDMITx_HDByPass(boolean enable);
    boolean getHDMITx_HDByPass();
    boolean isSupportHDMITx_HDByPassMode();
    int setOutputSourceInfo(int enAudioPath, int enSource);

    /**
     * add client for callback event
     *
     * @throws TvCommonException
     */
    void addClient(IBinder client);

    /**
     * Remove client for callback event
     *
     * @throws TvCommonException
     */
    void removeClient(IBinder client);

     /**
     * EosTek Patch End 
     */
    int getTrebleBySoundMode(int soundMode);
	
    int getBassBySoundMode(int soundMode);
	
    boolean setSoundModeAll(int SoundMode, int bass, int treble);
    /**
     * EosTek Patch End 
     */
}
