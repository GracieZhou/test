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

import java.lang.reflect.Method;

import android.content.Context;
import android.os.RemoteException;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.mstar.android.tv.ITvPicture;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.ColorTemperature;
import com.mstar.android.tvapi.common.vo.ColorTemperatureExData;
import com.mstar.android.tvapi.common.vo.EnumScalerWindow;
import com.mstar.android.tvapi.common.vo.EnumVideoArcType;
import com.mstar.android.tvapi.common.vo.EnumPictureMode;
import com.mstar.android.tvapi.common.vo.MpegNoiseReduction.EnumMpegNoiseReduction;
import com.mstar.android.tvapi.common.vo.MweType.EnumMweType;
import com.mstar.android.tvapi.common.vo.PanelProperty;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.mstar.android.tvapi.common.vo.VideoInfo;
import com.mstar.android.tvapi.common.vo.VideoInfo.EnumScanType;
import com.mstar.android.tvapi.common.vo.VideoWindowType;
import com.mstar.android.tvapi.common.vo.EnumColorTemperature;
import com.mstar.android.tvapi.common.vo.Film.EnumFilm;
import com.mstar.android.tvapi.common.vo.NoiseReduction.EnumNoiseReduction;
import com.mstar.android.tvapi.common.vo.EnumMfcMode;
import com.mstar.android.tvapi.common.vo.TimingInfo;

import com.mstar.tv.service.IDatabaseDesk.EnumColorTemperature_;
import com.mstar.tv.service.IDatabaseDesk.EnumColorTempExInputSource;
import com.mstar.tv.service.IDatabaseDesk.EnumAspectRatioType;
import com.mstar.tv.service.IDatabaseDesk.EN_MS_VIDEOITEM;
import com.mstar.tv.service.IDatabaseDesk.UserSetting;
import com.mstar.tv.service.IDatabaseDesk.PictureModeSetting;

public class TvPictureService extends ITvPicture.Stub {

    private static final String TAG = "TvPictureService";

    private final static int cmvalue1 = 0x11;

    private final static int cmvalue2 = 0x30;

    private final static int cmvalue3 = 0x58;

    private final static int cmvalue4 = 0x70;

    private final static int cmvalue5 = 0x85;

    private final static int cmvalue6 = 0xa0;

    private final static int cmvalue7 = 0xf0;

    private final static int cmvalue8 = 0xff;

    private final static int PBLV1 = 20;

    private final static int PBLV2 = 40;

    private final static int PBLV3 = 60;

    private final static int PBLV4 = 80;

    private final static int PBLV5 = 100;

    private static int mSleepTimeCounter = 500;

    private boolean mIsEnableDBC = false;

    private boolean mIsEnableDCC = false;

    private boolean mIsEnableDLC = true;

    private boolean mIsForceThreadSleep = true;

    private boolean mIsUClearOn = false;

    private Context mContext = null;

    private String mProduct = null;

    public TvPictureService(Context context) {
        mContext = context;
        new DynamicBackLightContrastControlThread().start();
    }

    @Override
    public void disableBacklight() throws RemoteException {
        try {
            TvManager.getInstance().getPictureManager().disableBacklight();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean disableDbc() throws RemoteException {
        mIsEnableDBC = false;
        mIsForceThreadSleep = false;
        setSleepTimeCounter();
        return true;
    }

    @Override
    public boolean disableDcc() throws RemoteException {
        mIsEnableDCC = false;
        mIsForceThreadSleep = false;
        setSleepTimeCounter();
        return true;
    }

    @Override
    public boolean disableDlc() throws RemoteException {
        mIsEnableDLC = false;
        try {
            TvManager.getInstance().getPictureManager().disableDlc();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void enableBacklight() throws RemoteException {
        try {
            TvManager.getInstance().getPictureManager().enableBacklight();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean enableDbc() throws RemoteException {
        mIsEnableDBC = true;
        mIsForceThreadSleep = false;
        setSleepTimeCounter();
        return true;
    }

    @Override
    public boolean enableDcc() throws RemoteException {
        mIsEnableDCC = true;
        mIsForceThreadSleep = false;
        setSleepTimeCounter();
        return true;
    }

    @Override
    public boolean enableDlc() throws RemoteException {
        mIsEnableDLC = true;
        try {
            TvManager.getInstance().getPictureManager().enableDlc();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean execAutoPc() throws RemoteException {
        boolean autotune_flag = false;
        try {
            autotune_flag = TvManager.getInstance().getPlayerManager().startPcModeAtuoTune();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return autotune_flag;
    }

    @Override
    public boolean enableXvyccCompensation(boolean bEn, int eWin) {
        try {
            return TvManager.getInstance().getPictureManager().enableXvyccCompensation(bEn, eWin);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean setxvYCCEnable(boolean bEn, int eMode) {
        boolean ret = false;
        try {
            ret = TvManager.getInstance().getPictureManager().setxvYCCEnable(bEn, eMode);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        if (ret == true) {
            DatabaseDesk.getInstance(mContext).updatexvYCCEnable(bEn);
        }
        return ret;
    }

    @Override
    public boolean getxvYCCEnable() {
        return DatabaseDesk.getInstance(mContext).queryxvYCCEnable();
    }

    @Override
    public int getHDMIColorFormat() {
        int ret = 0;
        try {
            ret = TvManager.getInstance().getPictureManager().getHDMIColorFormat();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public boolean freezeImage() throws RemoteException {
        try {
            return TvManager.getInstance().getPictureManager().freezeImage();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int getBacklight() throws RemoteException {
        int inputSrcType = getCurrentInputSource();
        int pictureModeType = DatabaseDesk.getInstance(mContext).queryPictureMode(inputSrcType);
        int i = DatabaseDesk.getInstance(mContext).queryPicModeSetting(EN_MS_VIDEOITEM.E_BACKLIGHT,
                inputSrcType, pictureModeType);
        return i;
    }

    @Override
    public byte getColorRange() throws RemoteException {
        return (byte) DatabaseDesk.getInstance(mContext).queryUserSysSetting().colorRangeMode;
    }

    @Override
    public int getColorTempIdx() throws RemoteException {
        // TODO: modify return type int to EnumColorTemperature
        // need to modify TV api & DatabaseDesk as well

        int inputSrc = getCurrentInputSource();
        int pictureMode = DatabaseDesk.getInstance(mContext).queryPictureMode(inputSrc);

        return DatabaseDesk.getInstance(mContext).queryColorTempIdx(inputSrc, pictureMode)
                .ordinal();
    }

    @Override
    public int getDemoMode() throws RemoteException {
        EnumMweType eMweType = null;
        try {
            eMweType = TvManager.getInstance().getPictureManager().getDemoMode();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return eMweType.ordinal();
    }

    @Override
    public int getFilmMode() throws RemoteException {
        return DatabaseDesk.getInstance(mContext).queryFilmMode(getCurrentInputSource());
    }

    @Override
    public int getNR() throws RemoteException {
        return DatabaseDesk.getInstance(mContext)
                .queryNR(getCurrentInputSource(), getColorTempIdx()).ordinal();
    }

    @Override
    public int getPCClock() throws RemoteException {
        return DatabaseDesk.getInstance(mContext).queryPCClock();
    }

    @Override
    public int getPCHPos() throws RemoteException {
        return DatabaseDesk.getInstance(mContext).queryPCHPos();
    }

    @Override
    public int getPCPhase() throws RemoteException {
        return DatabaseDesk.getInstance(mContext).queryPCPhase();
    }

    @Override
    public int getPCVPos() throws RemoteException {
        return DatabaseDesk.getInstance(mContext).queryPCVPos();
    }

    @Override
    public int getPictureModeIdx() throws RemoteException {
        return DatabaseDesk.getInstance(mContext).queryPictureMode(getCurrentInputSource());
    }

    @Override
    public int getIsPcMode() throws RemoteException {
        return DatabaseDesk.getInstance(mContext).queryIsPcMode(getCurrentInputSource());
    }

    @Override
    public void setITC(int ITC) {
        DatabaseDesk.getInstance(mContext).updateMonitorITC(ITC);
    }

    @Override
    public int getITC() {
        return DatabaseDesk.getInstance(mContext).queryMonitorITC();
    }

    @Override
    public int getMpegNR() throws RemoteException {
        int i = getColorTempIdx();

        return DatabaseDesk.getInstance(mContext).queryMpegNR(getCurrentInputSource(), i).ordinal();
    }

    @Override
    public int getReproduceRate() throws RemoteException {
        try {
            return TvManager.getInstance().getPictureManager().getReproduceRate();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return 0x00;
    }

    @Override
    public byte getResolution() throws RemoteException {
        try {
            return TvManager.getInstance().getPictureManager().getResolution();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return 0x00;
    }

    @Override
    public int getVideoArc() throws RemoteException {
        // Through SetAspectRatio get ArcType
        EnumVideoArcType eArcIdx = EnumVideoArcType.E_DEFAULT;
        try {
            eArcIdx = TvManager.getInstance().getPictureManager().getAspectRatio();
        } catch (TvCommonException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return eArcIdx.ordinal();
    }

    @Override
    public VideoInfo getVideoInfo() throws RemoteException {
        try {
            return TvManager.getInstance().getPlayerManager().getVideoInfo();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getVideoItem(int index) throws RemoteException {
        // TODO: modify (int index) to (EnumVideoItem index)
        // need to modify TV api & DeskbaseDesk as well

        int inputSrc = getCurrentInputSource();
        int pictureMode = DatabaseDesk.getInstance(mContext).queryPictureMode(inputSrc);
        return DatabaseDesk.getInstance(mContext).queryPicModeSetting(
                EN_MS_VIDEOITEM.values()[index], inputSrc, pictureMode);
    }

    @Override
    public int getVideoItemByInputSource(int index, int input) throws RemoteException {
        // TODO: modify (int index) to (EnumVideoItem index)
        // need to modify TV api & DeskbaseDesk as well

        int pictureModeType = DatabaseDesk.getInstance(mContext).queryPictureMode(input);
        return DatabaseDesk.getInstance(mContext).queryPicModeSetting(
                EN_MS_VIDEOITEM.values()[index], input, pictureModeType);
    }

    @Override
    public boolean is4K2KMode(boolean bEn) throws RemoteException {
        try {
            return TvManager.getInstance().getPictureManager().is4K2KMode(bEn);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isDbcEnabled() throws RemoteException {
        return mIsEnableDBC;
    }

    @Override
    public boolean isDccEnabled() throws RemoteException {
        return mIsEnableDCC;
    }

    @Override
    public boolean isDlcEnabled() throws RemoteException {
        return mIsEnableDLC;
    }

    @Override
    public void setUClearStatus(boolean bFlag) {
        try {
            TvManager.getInstance().getPictureManager().setUltraClear(bFlag);
            mIsUClearOn = bFlag;
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isUClearOn() {
        return mIsUClearOn;
    }

    @Override
    public boolean setBacklight(int value) throws RemoteException {
        // TODO: modify (EN_MS_VIDEOITEM) to (EnumVideoItem)
        // need to align them

        int inputSrc = getCurrentInputSource();
        int pictureMode = DatabaseDesk.getInstance(mContext).queryPictureMode(inputSrc);
        try {
            TvManager.getInstance().getPictureManager().setBacklight(value);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        DatabaseDesk.getInstance(mContext).updatePicModeSetting(EN_MS_VIDEOITEM.E_BACKLIGHT,
                inputSrc, pictureMode, value);
        return true;
    }

    @Override
    public boolean setColorRange(byte value) throws RemoteException {
        boolean colorRange0_255 = false;
        UserSetting stUsrData = null;

        try {
            // TODO: what is magic number 2 means??
            if (value == 2) {
                if (TvManager.getInstance() != null) {
                    TvManager.getInstance().getPictureManager().autoHDMIColorRange();
                }
            } else {
                if (value != 0) {
                    colorRange0_255 = false;
                } else {
                    colorRange0_255 = true;
                }

                if (TvManager.getInstance() != null) {
                    TvManager.getInstance().getPictureManager().setColorRange(colorRange0_255);
                }
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }

        stUsrData = DatabaseDesk.getInstance(mContext).queryUserSysSetting();
        if (stUsrData != null) {
            stUsrData.colorRangeMode = value;
            DatabaseDesk.getInstance(mContext).updateUserSysSetting(stUsrData);
        } else {
            // TODO: error handling
        }
        return true;
    }

    @Override
    public boolean setColorTempIdx(int colorTemp) throws RemoteException {
        EnumInputSource currentInputSrc = null;
        try {
            currentInputSrc = TvManager.getInstance().getCurrentInputSource();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        int pictureMode = DatabaseDesk.getInstance(mContext).queryPictureMode(
                currentInputSrc.ordinal());

        ColorTemperature vo = DatabaseDesk.getInstance(mContext).queryFactoryColorTempExData(
                getColorTempInputType(), colorTemp);

        // TODO: why [colorTemp+1] ??? need to check
        try {
            TvManager
                    .getInstance()
                    .getFactoryManager()
                    .setWbGainOffsetEx(EnumColorTemperature.values()[colorTemp + 1], vo.redGain,
                            vo.greenGain, vo.buleGain, vo.redOffset, vo.greenOffset, vo.blueOffset,
                            currentInputSrc);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }

        // TODO: modify EN_MS_COLOR_TEMP to EnumColorTemperature
        DatabaseDesk.getInstance(mContext).updateColorTempIdx(currentInputSrc.ordinal(),
                pictureMode, EnumColorTemperature_.values()[colorTemp]);
        return true;

    }

    @Override
    public ColorTemperatureExData getColorTempratureEx() throws RemoteException {
        int nColorTempIndex = getColorTempIdx();
        ColorTemperatureExData data = null;

        // FIXME: Enum is mutually exclusive, the
        // EnumColorTemperature.E_COLOR_TEMP_MIN occupied index 0,
        // hence we increase index by 1 here, remove the patch if
        // EnumColorTemperature removed E_COLOR_TEMP_MIN
        if ((EnumColorTemperature.E_COLOR_TEMP_USER.ordinal() <= (1 + nColorTempIndex))
                && (EnumColorTemperature.E_COLOR_TEMP_USER2.ordinal() >= (1 + nColorTempIndex))) {
            data = DatabaseDesk.getInstance(mContext).queryUsrColorTmpExData(
                    getColorTempInputType());
        } else {
            data = DatabaseDesk.getInstance(mContext).queryFactoryColorTemperatureExData(
                    getColorTempInputType(), nColorTempIndex);
        }
        return data;
    }

    @Override
    public void setColorTempratureEx(ColorTemperatureExData colorTemp) throws RemoteException {
        try {
            TvManager.getInstance().getPictureManager().setColorTemperature(colorTemp);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }

        DatabaseDesk.getInstance(mContext).updateUsrColorTmpExData(colorTemp,
                getColorTempInputType());
    }

    @Override
    public void setDemoMode(int enMweType) throws RemoteException {
        EnumMweType eMweType = EnumMweType.values()[enMweType];
        try {
            TvManager.getInstance().getPictureManager().setDemoMode(eMweType);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setDisplayWindow(VideoWindowType videoWindowType) throws RemoteException {
        try {
            TvManager.getInstance().getPictureManager()
                    .selectWindow(EnumScalerWindow.E_MAIN_WINDOW);

            TvManager.getInstance().getPictureManager().setDisplayWindow(videoWindowType);

            TvManager.getInstance().getPictureManager().scaleWindow();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean setFilmMode(int mode) throws RemoteException {
        // TODO: wrong code? (int mode) is the order, but use as value.....
        // check the logic, and modify (int mode) to (EnumFilm mode)
        // need to modify TV api as well

        try {
            TvManager.getInstance().getPictureManager()
                    .setFilm(EnumFilm.values()[EnumFilm.getOrdinalThroughValue(mode)]);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        DatabaseDesk.getInstance(mContext).updateFilmMode(mode, getCurrentInputSource());
        return true;
    }

    @Override
    public boolean setMEMCMode(String interfaceCommand) throws RemoteException {
        try {
            return TvManager.getInstance().getPictureManager().setMEMCMode(interfaceCommand);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean setMpegNR(int eMpNRIdx) throws RemoteException {
        boolean ret = false;
        int i = getColorTempIdx();
        int cur = getCurrentInputSource();
        EnumMpegNoiseReduction nrType = EnumMpegNoiseReduction.values()[EnumMpegNoiseReduction
                .getOrdinalThroughValue(eMpNRIdx)];
        try {
            ret = TvManager.getInstance().getPictureManager().setMpegNoiseReduction(nrType);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        if (ret == true) {
            DatabaseDesk.getInstance(mContext).updateMpegNR(cur, i, nrType);
        }
        return ret;
    }

    @Override
    public boolean setNR(int nrIndex) throws RemoteException {
        // TODO: modify (int nrIndex) to (EnumNoiseReduction nrIndex)
        // need to modify TV api as well
        boolean ret = false;
        EnumNoiseReduction nrType = EnumNoiseReduction.values()[EnumNoiseReduction
                .getOrdinalThroughValue(nrIndex)];
        try {
            ret = TvManager.getInstance().getPictureManager().setNoiseReduction(nrType);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        if (ret == true) {
            DatabaseDesk.getInstance(mContext).updateNR(getCurrentInputSource(), getColorTempIdx(),
                    nrType);
        }
        return ret;
    }

    @Override
    public boolean setPCClock(int clock) throws RemoteException {
        try {
            return TvManager.getInstance().getPlayerManager().setSize(clock);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean setPCHPos(int hpos) throws RemoteException {
        try {
            return TvManager.getInstance().getPlayerManager().setHPosition(hpos);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;

    }

    @Override
    public boolean setPCPhase(int phase) throws RemoteException {
        try {
            return TvManager.getInstance().getPlayerManager().setPhase(phase);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean setPCVPos(int vpos) throws RemoteException {
        try {
            return TvManager.getInstance().getPlayerManager().setVPosition(vpos);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean setPictureModeIdx(int ePicMode) throws RemoteException {
        // TODO: don't we need to update PicMode setting after set them to
        // TvOS??

        PictureModeSetting picturMode = DatabaseDesk.getInstance(mContext)
                .queryPictureModeSettings(ePicMode, getCurrentInputSource());
        DatabaseDesk.getInstance(mContext).updatePictureMode(ePicMode, getCurrentInputSource());
        try {
            TvManager.getInstance().getPictureManager()
                    .setPictureModeBrightness(picturMode.brightness);
            TvManager.getInstance().getPictureManager().setPictureModeContrast(picturMode.contrast);
            TvManager.getInstance().getPictureManager().setPictureModeColor(picturMode.saturation);
            TvManager.getInstance().getPictureManager()
                    .setPictureModeSharpness(picturMode.sharpness);
            TvManager.getInstance().getPictureManager().setPictureModeTint(picturMode.hue);
            TvManager.getInstance().getPictureManager().setBacklight(picturMode.backlight);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void setReproduceRate(int rate) throws RemoteException {
        try {
            TvManager.getInstance().getPictureManager().setReproduceRate(rate);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setResolution(byte resolution) throws RemoteException {
        try {
            TvManager.getInstance().getPictureManager().setResolution(resolution);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean setVideoArc(int eArcIdx) throws RemoteException {
        /*
         * The following is current supported input source and video arc matrix.
         * ATV DTV S-Video CVBS HDMI/DVI VGA YPbPr Storage E_DEFAULT o o o o o o
         * o o E_16x9 o o o o o o o o E_4x3 o o o o o o o o E_AUTO o o o o o o o
         * o E_Panorama o o o o o o o x E_JustScan o o o o o o o o E_Zoom1 o o o
         * o o o o o E_Zoom2 o o o o o o o o E_14x9 o o o o o o o o //point to
         * point (HDMI RGB,HDMI YUV444) E_AR_DotByDot x x x x o o x x
         */

        // TODO: always return true?

        EnumVideoArcType arcType = EnumVideoArcType.E_DEFAULT;
        int inputSource = getCurrentInputSource();
        if (eArcIdx <= EnumVideoArcType.E_14x9.ordinal()
                || (eArcIdx == EnumVideoArcType.E_AR_Movie.ordinal())
                || (eArcIdx == EnumVideoArcType.E_AR_DotByDot.ordinal() && (inputSource >= EnumInputSource.E_INPUT_SOURCE_HDMI
                        .ordinal() && inputSource < EnumInputSource.E_INPUT_SOURCE_HDMI_MAX
                        .ordinal()))
                || (eArcIdx == EnumVideoArcType.E_AR_DotByDot.ordinal() && (inputSource == EnumInputSource.E_INPUT_SOURCE_VGA
                        .ordinal()))
                || (eArcIdx == EnumVideoArcType.E_AR_DotByDot.ordinal() && (inputSource == EnumInputSource.E_INPUT_SOURCE_VGA2
                        .ordinal()))
                || (eArcIdx == EnumVideoArcType.E_AR_DotByDot.ordinal() && (inputSource == EnumInputSource.E_INPUT_SOURCE_VGA3
                        .ordinal()))) {
            if (inputSource == EnumInputSource.E_INPUT_SOURCE_STORAGE.ordinal()
                    && eArcIdx == EnumVideoArcType.E_Panorama.ordinal()) {
                return false;
            }
            arcType = EnumVideoArcType.values()[eArcIdx];
            try {
                TvManager.getInstance().getPictureManager().setAspectRatio(arcType);
            } catch (TvCommonException e) {
                e.printStackTrace();
            }
        } else {
            return false;
        }

        return true;
    }

    @Override
    public boolean setVideoItem(int index, int value) throws RemoteException {
        // TODO: use EnumVideoItem to replace EN_MS_VIDEOITEM
        // consider use switch-case to replace if-else
        // modify (int index) to (EnumVideoItem index) , need to modify TV api
        // as well

        int inputSrc = getCurrentInputSource();
        int pictureMode = DatabaseDesk.getInstance(mContext).queryPictureMode(inputSrc);
        try {
            if (index == EN_MS_VIDEOITEM.E_BRIGHTNESS.ordinal()) {
                TvManager.getInstance().getPictureManager().setPictureModeBrightness((short) value);
            } else if (index == EN_MS_VIDEOITEM.E_CONTRAST.ordinal()) {
                TvManager.getInstance().getPictureManager().setPictureModeContrast((short) value);
            } else if (index == EN_MS_VIDEOITEM.E_SHARPNESS.ordinal()) {
                TvManager.getInstance().getPictureManager().setPictureModeSharpness((short) value);
            } else if (index == EN_MS_VIDEOITEM.E_SATURATION.ordinal()) {
                TvManager.getInstance().getPictureManager().setPictureModeColor((short) value);
            } else if (index == EN_MS_VIDEOITEM.E_HUE.ordinal()) {
                TvManager.getInstance().getPictureManager().setPictureModeTint((short) value);
            } else
                return false;
        } catch (TvCommonException e) {
            e.printStackTrace();
            return false;
        }
        DatabaseDesk.getInstance(mContext).updatePicModeSetting(EN_MS_VIDEOITEM.values()[index],
                inputSrc, pictureMode, value);
        return true;
    }

    @Override
    public boolean setVideoItemByInputSource(int index, int value, int input)
            throws RemoteException {
        // TODO: modify (int index) to (EnumVideoItem index), need to modify TV
        // api as well
        // why update database only?? how about tvos ?

        int pictureMode = DatabaseDesk.getInstance(mContext).queryPictureMode(input);
        DatabaseDesk.getInstance(mContext).updatePicModeSetting(EN_MS_VIDEOITEM.values()[index],
                input, pictureMode, value);
        return true;
    }

    @Override
    public boolean unFreezeImage() throws RemoteException {
        try {
            return TvManager.getInstance().getPictureManager().unFreezeImage();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isImageFreezed() throws RemoteException {
        try {
            return TvManager.getInstance().getPictureManager().isImageFreezed();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int[] getPcModeInfo() throws RemoteException {
        return DatabaseDesk.getInstance(mContext).queryPcModeInfo();
    }

    @Override
    public PanelProperty getPanelWidthHeight() throws RemoteException {
        try {
            return TvManager.getInstance().getPictureManager().getPanelWidthHeight();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void forceThreadSleep(boolean flag) {
        mIsForceThreadSleep = flag;
    }

    @Override
    public boolean turnOffLocalDimmingBacklight(boolean bOffFlag) {
        boolean ret = false;
        try {
            ret = TvManager.getInstance().getPictureManager()
                    .turnOffLocalDimmingBacklight(bOffFlag);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public void setMfcMode(int mode) throws RemoteException {

        try {
            TvManager.getInstance().getPictureManager().setMfc(EnumMfcMode.values()[mode]);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isSupportedZoom() throws RemoteException {
        try {
            return TvManager.getInstance().getPictureManager().isSupportedZoom();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isBox() {
        if (null == mProduct) {
            Class<?> systemProperties = null;
            Method method = null;
            try {
                systemProperties = Class.forName("android.os.SystemProperties");
                method = systemProperties.getMethod("get", String.class, String.class);
                mProduct = (String) method.invoke(null, "mstar.product.characteristics", "");
            } catch (Exception e) {
                return false;
            }
        }
        if ("stb".equals(mProduct)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean[] getAspectRationList() throws RemoteException {
        boolean[] supportArcListTypes = new boolean[EnumVideoArcType.E_MAX.ordinal()];
        VideoInfo videoInfo = null;
        boolean isHdmiMode = false;
        EnumInputSource currInputSource = EnumInputSource.E_INPUT_SOURCE_NONE;
        Cursor cursorVideo = null;
        int iIsPcMode = 0;
        EnumVideoArcType arcType;
        for (int i = supportArcListTypes.length - 1; i >= 0; i--) {
            supportArcListTypes[i] = true;
        }
        // Currently does not support
        supportArcListTypes[EnumVideoArcType.E_AR_Subtitle.ordinal()] = false;
        supportArcListTypes[EnumVideoArcType.E_AR_Movie.ordinal()] = false;
        supportArcListTypes[EnumVideoArcType.E_AR_Personal.ordinal()] = false;
        supportArcListTypes[EnumVideoArcType.E_4x3_PanScan.ordinal()] = false;
        supportArcListTypes[EnumVideoArcType.E_4x3_LetterBox.ordinal()] = false;
        supportArcListTypes[EnumVideoArcType.E_16x9_PillarBox.ordinal()] = false;
        supportArcListTypes[EnumVideoArcType.E_16x9_PanScan.ordinal()] = false;
        supportArcListTypes[EnumVideoArcType.E_4x3_Combind.ordinal()] = false;
        supportArcListTypes[EnumVideoArcType.E_16x9_Combind.ordinal()] = false;
        supportArcListTypes[EnumVideoArcType.E_Zoom_2x.ordinal()] = false;
        supportArcListTypes[EnumVideoArcType.E_Zoom_3x.ordinal()] = false;
        supportArcListTypes[EnumVideoArcType.E_Zoom_4x.ordinal()] = false;

        try {
            // Get info from TvManger
            currInputSource = TvManager.getInstance().getCurrentInputSource();
            videoInfo = TvManager.getInstance().getPlayerManager().getVideoInfo();
            isHdmiMode = TvManager.getInstance().getPlayerManager().isHdmiMode();
            arcType = TvManager.getInstance().getPictureManager().getAspectRatio();
            // for query isPcMode
            cursorVideo = mContext.getContentResolver().query(
                    Uri.parse("content://mstar.tv.usersetting/videosetting/inputsrc/"
                            + currInputSource.ordinal()), null, null, null, null);
            if (cursorVideo.moveToFirst()) {
                iIsPcMode = cursorVideo.getInt(cursorVideo.getColumnIndex("bIsPcMode"));
            }
            cursorVideo.close();
            int index = getPictureModeIdx();
            Log.d(TAG, "current input source = " + currInputSource);
            Log.d(TAG, "scan type = " + videoInfo.getScanType());
            Log.d(TAG, "arcType = " + arcType);
            Log.d(TAG, "isHdmiMode = " + isHdmiMode);
            Log.d(TAG, "iIsPcMode = " + iIsPcMode);
            Log.d(TAG, "picture index = " + index);
            // [RR1174]Game mode only support 16:9, Just Scan
            if (index == EnumPictureMode.PICTURE_GAME.ordinal()) {
                supportArcListTypes[EnumVideoArcType.E_DEFAULT.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_4x3.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_AUTO.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_Panorama.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_Zoom1.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_Zoom2.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_14x9.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_AR_DotByDot.ordinal()] = false;

            }
            // AUTO mode support 4:3 & 16:9, only when DTD && pcmode
            // will support DTD only
            else if (index == EnumPictureMode.PICTURE_AUTO.ordinal()
                    && ((currInputSource == EnumInputSource.E_INPUT_SOURCE_VGA)
                            || (currInputSource == EnumInputSource.E_INPUT_SOURCE_VGA2) || (currInputSource == EnumInputSource.E_INPUT_SOURCE_VGA3))) {
                supportArcListTypes[EnumVideoArcType.E_DEFAULT.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_JustScan.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_AUTO.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_Panorama.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_Zoom1.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_Zoom2.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_14x9.ordinal()] = false;

                if (arcType == EnumVideoArcType.E_AR_DotByDot && (iIsPcMode != 0)) {
                    supportArcListTypes[EnumVideoArcType.E_4x3.ordinal()] = false;
                    supportArcListTypes[EnumVideoArcType.E_16x9.ordinal()] = false;

                } else {
                    supportArcListTypes[EnumVideoArcType.E_AR_DotByDot.ordinal()] = false;
                }
            } else if (index == EnumPictureMode.PICTURE_AUTO.ordinal()
                    && (currInputSource.ordinal() >= EnumInputSource.E_INPUT_SOURCE_HDMI.ordinal()
                            && currInputSource.ordinal() < EnumInputSource.E_INPUT_SOURCE_HDMI_MAX
                                    .ordinal() && isHdmiMode) && (iIsPcMode != 0)) {
                supportArcListTypes[EnumVideoArcType.E_DEFAULT.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_JustScan.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_AUTO.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_Panorama.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_Zoom1.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_Zoom2.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_14x9.ordinal()] = false;

                if (arcType.ordinal() == EnumVideoArcType.E_AR_DotByDot.ordinal()) {
                    supportArcListTypes[EnumVideoArcType.E_4x3.ordinal()] = false;
                    supportArcListTypes[EnumVideoArcType.E_16x9.ordinal()] = false;
                } else {
                    supportArcListTypes[EnumVideoArcType.E_AR_DotByDot.ordinal()] = false;
                }
            }
            // PC mode && VGA support 4:3 && 16:9
            else if (index == EnumPictureMode.PICTURE_PC.ordinal()
                    && ((currInputSource.ordinal() == EnumInputSource.E_INPUT_SOURCE_VGA.ordinal())
                            || (currInputSource.ordinal() == EnumInputSource.E_INPUT_SOURCE_VGA2
                                    .ordinal()) || (currInputSource.ordinal() == EnumInputSource.E_INPUT_SOURCE_VGA3
                            .ordinal()))) {
                supportArcListTypes[EnumVideoArcType.E_DEFAULT.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_JustScan.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_AUTO.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_Panorama.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_Zoom1.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_Zoom2.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_14x9.ordinal()] = false;

                if (arcType.ordinal() == EnumVideoArcType.E_AR_DotByDot.ordinal()) {
                    supportArcListTypes[EnumVideoArcType.E_4x3.ordinal()] = false;
                    supportArcListTypes[EnumVideoArcType.E_16x9.ordinal()] = false;
                } else {
                    supportArcListTypes[EnumVideoArcType.E_AR_DotByDot.ordinal()] = false;
                }
            }
            // PC HDMI support 4:3 && 16:9, except DTD case only
            // support DTD
            else if (index == EnumPictureMode.PICTURE_PC.ordinal()
                    && (currInputSource.ordinal() >= EnumInputSource.E_INPUT_SOURCE_HDMI.ordinal()
                            && currInputSource.ordinal() < EnumInputSource.E_INPUT_SOURCE_HDMI_MAX
                                    .ordinal() && isHdmiMode)) {
                supportArcListTypes[EnumVideoArcType.E_DEFAULT.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_JustScan.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_AUTO.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_Panorama.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_Zoom1.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_Zoom2.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_14x9.ordinal()] = false;

                if (arcType.ordinal() == EnumVideoArcType.E_AR_DotByDot.ordinal()) {
                    supportArcListTypes[EnumVideoArcType.E_4x3.ordinal()] = false;
                    supportArcListTypes[EnumVideoArcType.E_16x9.ordinal()] = false;
                } else {
                    supportArcListTypes[EnumVideoArcType.E_AR_DotByDot.ordinal()] = false;
                }
            }
            // Original flow
            else {
                // ScanType:INTERLACED or Source:YPBPR
                if (videoInfo.getScanType() == EnumScanType.E_INTERLACED
                        || currInputSource == EnumInputSource.E_INPUT_SOURCE_YPBPR
                        || currInputSource == EnumInputSource.E_INPUT_SOURCE_YPBPR2
                        || currInputSource == EnumInputSource.E_INPUT_SOURCE_YPBPR3) {
                    supportArcListTypes[EnumAspectRatioType.E_DotByDot.ordinal()] = false;
                }
                // Source:VGA/DVI/HDMI
                if ((currInputSource.ordinal() >= EnumInputSource.E_INPUT_SOURCE_DVI.ordinal() && currInputSource
                        .ordinal() < EnumInputSource.E_INPUT_SOURCE_DVI_MAX.ordinal())
                        || currInputSource.ordinal() == EnumInputSource.E_INPUT_SOURCE_VGA
                                .ordinal()
                        || currInputSource.ordinal() == EnumInputSource.E_INPUT_SOURCE_VGA2
                                .ordinal()
                        || currInputSource.ordinal() == EnumInputSource.E_INPUT_SOURCE_VGA3
                                .ordinal()
                        || (currInputSource.ordinal() >= EnumInputSource.E_INPUT_SOURCE_HDMI
                                .ordinal()
                                && currInputSource.ordinal() < EnumInputSource.E_INPUT_SOURCE_HDMI_MAX
                                        .ordinal() && !isHdmiMode)) {
                    supportArcListTypes[EnumVideoArcType.E_AUTO.ordinal()] = false;
                    supportArcListTypes[EnumVideoArcType.E_JustScan.ordinal()] = false;
                    supportArcListTypes[EnumVideoArcType.E_Panorama.ordinal()] = false;
                    supportArcListTypes[EnumVideoArcType.E_Zoom1.ordinal()] = false;
                    supportArcListTypes[EnumVideoArcType.E_Zoom2.ordinal()] = false;
                }

                // Source:DTV
                if (currInputSource.ordinal() == EnumInputSource.E_INPUT_SOURCE_DTV.ordinal()) {
                    /* DotByDot mode is not supported in DTV */
                    supportArcListTypes[EnumVideoArcType.E_AR_DotByDot.ordinal()] = false;
                }

                // Source:STORAGE
                if ((currInputSource.ordinal() >= EnumInputSource.E_INPUT_SOURCE_STORAGE.ordinal())
                        && (currInputSource.ordinal() != EnumInputSource.E_INPUT_SOURCE_VGA2
                                .ordinal())
                        && (currInputSource.ordinal() != EnumInputSource.E_INPUT_SOURCE_VGA3
                                .ordinal())) {
                    supportArcListTypes[EnumVideoArcType.E_Panorama.ordinal()] = false;
                    supportArcListTypes[EnumVideoArcType.E_AR_DotByDot.ordinal()] = false;
                }

                // Source:CVBS
                if ((currInputSource.ordinal() >= EnumInputSource.E_INPUT_SOURCE_CVBS.ordinal())
                        && (currInputSource.ordinal() < EnumInputSource.E_INPUT_SOURCE_CVBS_MAX
                                .ordinal())) {
                    /*
                     * mantis:0738614 In input source CVBS with just scan mode,
                     * the white line is displayed at the top of the screen. The
                     * behavior is normal because "overscan" is not implemented.
                     * (The comment comes from scaler team) To fix the issue,
                     * disable just scan mode temporary.
                     */
                    supportArcListTypes[EnumVideoArcType.E_JustScan.ordinal()] = false;
                }
            }
            if (true == isBox()) {
                supportArcListTypes[EnumVideoArcType.E_Zoom1.ordinal()] = false;
                supportArcListTypes[EnumVideoArcType.E_Zoom2.ordinal()] = false;
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return supportArcListTypes;
    }

    private int getCurrentInputSource() {
        EnumInputSource inputSrc = null;
        try {
            inputSrc = TvManager.getInstance().getCurrentInputSource();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return inputSrc.ordinal();
    }

    private void setSleepTimeCounter() {
        if (!mIsEnableDBC && !mIsEnableDCC) {
            mSleepTimeCounter = 2000;
        } else if (mIsEnableDBC && mIsEnableDCC) {
            mSleepTimeCounter = 500;
        } else {
            mSleepTimeCounter = 1000;
        }
    }

    private void dbcHandler() {
        int value = mapi_GetImageBackLight();

        try {
            TvManager.getInstance().getPictureManager().setBacklight(value);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    private void dccHandler() {
        int value = mapi_GetImageBackLight();

        try {
            TvManager.getInstance().getPictureManager().setPictureModeContrast((short) value);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    private int mapi_GetRealValue(int MaxLVal, int MinLVal, int ADVal, int MaxADVal, int MinADVal) {
        return (int) (ADVal - MinADVal) * (MaxLVal - MinLVal) / (MaxADVal - MinADVal);
    }

    private int mapi_GetImageBackLight() {
        int backlight, temp = 0;

        try {
            temp = TvManager.getInstance().getPictureManager().getDlcAverageLuma();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }

        if (temp < cmvalue1)
            backlight = PBLV1 - mapi_GetRealValue(PBLV2, PBLV1, temp, cmvalue1, 0);
        else if (temp < cmvalue2)
            backlight = PBLV2 - mapi_GetRealValue(PBLV3, PBLV2, temp, cmvalue2, cmvalue1);
        else if (temp < cmvalue3)
            backlight = PBLV3 - mapi_GetRealValue(PBLV4, PBLV3, temp, cmvalue3, cmvalue2);
        else if (temp < cmvalue4)
            backlight = PBLV4 - mapi_GetRealValue(PBLV5, PBLV4, temp, cmvalue4, cmvalue3);
        else if (temp < cmvalue5)
            backlight = PBLV5 + mapi_GetRealValue(PBLV5, PBLV4, temp, cmvalue5, cmvalue4);
        else if (temp < cmvalue6)
            backlight = PBLV4 + mapi_GetRealValue(PBLV4, PBLV3, temp, cmvalue6, cmvalue5);
        else if (temp < cmvalue7)
            backlight = PBLV3 + mapi_GetRealValue(PBLV3, PBLV2, temp, cmvalue7, cmvalue6);
        else
            backlight = PBLV2 + mapi_GetRealValue(PBLV2, PBLV1, temp, cmvalue8, cmvalue7);
        return backlight;
    }

    @Override
    public int getSupportedTimingListCount() {
        int ret = 0;
        try {
            ret = TvManager.getInstance().getPictureManager().native_getSupportedTimingListCount();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public int getCurrentTimingId() {
        int ret = 0;
        try {
            ret = TvManager.getInstance().getPictureManager().native_getCurrentTimingId();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private int getColorTempInputType() {
        EnumInputSource currentInputSrc = null;
        int source = 0;
        try {
            currentInputSrc = TvManager.getInstance().getCurrentInputSource();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }

        switch (currentInputSrc) {
            case E_INPUT_SOURCE_VGA:
            case E_INPUT_SOURCE_VGA2:
            case E_INPUT_SOURCE_VGA3:
                source = EnumColorTempExInputSource.E_VGA.ordinal();
                break;
            case E_INPUT_SOURCE_ATV:
                source = EnumColorTempExInputSource.E_ATV.ordinal();
                break;
            case E_INPUT_SOURCE_CVBS:
            case E_INPUT_SOURCE_CVBS2:
            case E_INPUT_SOURCE_CVBS3:
            case E_INPUT_SOURCE_CVBS4:
            case E_INPUT_SOURCE_CVBS5:
            case E_INPUT_SOURCE_CVBS6:
            case E_INPUT_SOURCE_CVBS7:
            case E_INPUT_SOURCE_CVBS8:
                source = EnumColorTempExInputSource.E_CVBS.ordinal();
                break;
            case E_INPUT_SOURCE_SVIDEO:
            case E_INPUT_SOURCE_SVIDEO2:
            case E_INPUT_SOURCE_SVIDEO3:
            case E_INPUT_SOURCE_SVIDEO4:
                source = EnumColorTempExInputSource.E_SVIDEO.ordinal();
                break;
            case E_INPUT_SOURCE_YPBPR:
            case E_INPUT_SOURCE_YPBPR2:
            case E_INPUT_SOURCE_YPBPR3:
                source = EnumColorTempExInputSource.E_YPBPR.ordinal();
                break;
            case E_INPUT_SOURCE_SCART:
            case E_INPUT_SOURCE_SCART2:
                source = EnumColorTempExInputSource.E_SCART.ordinal();
                break;
            case E_INPUT_SOURCE_HDMI:
            case E_INPUT_SOURCE_HDMI2:
            case E_INPUT_SOURCE_HDMI3:
            case E_INPUT_SOURCE_HDMI4:
                source = EnumColorTempExInputSource.E_HDMI.ordinal();
                break;
            case E_INPUT_SOURCE_DTV:
            case E_INPUT_SOURCE_DTV2:
                source = EnumColorTempExInputSource.E_DTV.ordinal();
                break;
            case E_INPUT_SOURCE_DVI:
            case E_INPUT_SOURCE_DVI2:
            case E_INPUT_SOURCE_DVI3:
            case E_INPUT_SOURCE_DVI4:
            case E_INPUT_SOURCE_STORAGE:
            case E_INPUT_SOURCE_KTV:
            case E_INPUT_SOURCE_JPEG:
            case E_INPUT_SOURCE_STORAGE2:
                source = EnumColorTempExInputSource.E_OTHERS.ordinal();
                break;
            default:
                break;
        }
        return source;
    }

    @Override
    public TimingInfo[] getSupportedTimingList() {
        try {
            return TvManager.getInstance().getPictureManager().native_getSupportedTimingList();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return null;
    }

    // TODO: make this thread more efficiency...
    private class DynamicBackLightContrastControlThread extends Thread {
        public void run() {
            while (true) {
                if (mIsForceThreadSleep) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (mIsEnableDBC) {
                        dbcHandler();
                    }

                    if (mIsEnableDCC) {
                        dccHandler();
                    }

                    try {
                        Thread.sleep(mSleepTimeCounter);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
	
	// EosTek Patch Begin
	@Override
    public boolean setPictureValue(int curPicMode, int lastPicMode, int index, int value) {
        EnumInputSource inputSrc = null;
        try {
            if (TvManager.getInstance() != null) {
                inputSrc = TvManager.getInstance().getCurrentInputSource();
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        PictureModeSetting picturMode = DatabaseDesk.getInstance(mContext).queryPictureModeSettings(lastPicMode,
                inputSrc.ordinal());
        DatabaseDesk.getInstance(mContext).updatePictureMode(curPicMode, inputSrc.ordinal());
        switch (EN_MS_VIDEOITEM.values()[index]) {
            case E_BRIGHTNESS:
                picturMode.brightness = (short) value;
                break;
            case E_CONTRAST:
                picturMode.contrast = (short) value;
                break;
            case E_HUE:
                picturMode.hue = (short) value;
                break;
            case E_SATURATION:
                picturMode.saturation = (short) value;
                break;
            case E_SHARPNESS:
                picturMode.sharpness = (short) value;
                break;
            case E_BACKLIGHT:
                picturMode.backlight = (short) value;
                break;
            case E_COLORTEMP:
                picturMode.eColorTemp = EnumColorTemperature_.values()[value];
                break;
            default:
                break;
        }
        DatabaseDesk.getInstance(mContext).updatePicValues(picturMode, inputSrc.ordinal(), curPicMode);
        try {
            if (TvManager.getInstance() != null) {
                TvManager.getInstance().getPictureManager()
                        .setPictureModeBrightness(picturMode.brightness);
                TvManager.getInstance().getPictureManager()
                        .setPictureModeContrast(picturMode.contrast);
                TvManager.getInstance().getPictureManager()
                        .setPictureModeColor(picturMode.saturation);
                TvManager.getInstance().getPictureManager()
                        .setPictureModeSharpness(picturMode.sharpness);
                TvManager.getInstance().getPictureManager().setPictureModeTint(picturMode.hue);
                TvManager.getInstance().getPictureManager().setBacklight(picturMode.backlight);
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return true;
    }

	@Override
	 public boolean setColorTempIdxAndRGB(int pictureModeType,int eColorTemp, int curSource) {
        Log.d(TAG, "setColorTempIdx, paras eColorTemp is " + eColorTemp+"::picture mode::"+pictureModeType+"::current source::"+curSource);
		ColorTemperatureExData colorTempExData = DatabaseDesk.getInstance(mContext).queryFactoryColorTempExData(EnumInputSource.values()[curSource], eColorTemp);
        try {
            if (TvManager.getInstance() != null) {
                TvManager
                        .getInstance()
                        .getFactoryManager()
                        .setWbGainOffsetEx(EnumColorTemperature.values()[eColorTemp + 1],
                                (short) colorTempExData.redGain, (short) colorTempExData.greenGain,
                                (short) colorTempExData.blueGain, (short) colorTempExData.redOffset,
                                (short) colorTempExData.greenOffset, (short) colorTempExData.blueOffset, EnumInputSource.values()[curSource]);
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
		DatabaseDesk.getInstance(mContext).updateColorTempIdx(curSource, pictureModeType, EnumColorTemperature_.values()[eColorTemp]);
        return true;
    }

	@Override
	public ColorTemperatureExData getWbGainOffsetEx(int eColorTemp, int curSource){
		ColorTemperatureExData data = DatabaseDesk.getInstance(mContext).queryFactoryColorTempExData(EnumInputSource.values()[curSource], eColorTemp);
		Log.d(TAG, "getWbGainOffsetEx, redGain " + data.redGain+"::blueGain::"+data.blueGain+"::greenGain::"+data.greenGain);
		return data;
	}

	@Override
	public void setWbGainOffsetEx(ColorTemperatureExData colorTempExData, int eColorTemp, int curSource){
		try {
            if (TvManager.getInstance() != null) {
                TvManager
                        .getInstance()
                        .getFactoryManager()
                        .setWbGainOffsetEx(EnumColorTemperature.values()[eColorTemp + 1],
                                (short) colorTempExData.redGain, (short) colorTempExData.greenGain,
                                (short) colorTempExData.blueGain, (short) colorTempExData.redOffset,
                                (short) colorTempExData.greenOffset, (short) colorTempExData.blueOffset, EnumInputSource.values()[curSource]);
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
		if(curSource != EnumInputSource.E_INPUT_SOURCE_STORAGE.ordinal()){
			DatabaseDesk.getInstance(mContext).updateFactoryColorTempExData(colorTempExData, curSource, eColorTemp);
		}
	}
	
	@Override
	public int[] getVideoItems(int inputSrc,int pictureMode){
		Log.d(TAG, "getVideoItems, paras inputSrc is " + inputSrc + ", pictureMode is " + pictureMode);
		return DatabaseDesk.getInstance(mContext).queryVideoItems(inputSrc, pictureMode);
	}

	@Override
    public int[] getPCImage() {
        Log.d(TAG, "getPCImage values values[0]:pcClock,values[1]:pcPhase,values[2]:pcHposition,values[3]:pcVposition");
        return DatabaseDesk.getInstance(mContext).queryPCImage();
    }
	// EosTek Patch End
}
