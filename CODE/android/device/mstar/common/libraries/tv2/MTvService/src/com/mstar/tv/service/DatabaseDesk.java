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

package com.mstar.tv.service;

import java.lang.IllegalStateException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.util.Log;

import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.CCSetting;
import com.mstar.android.tvapi.common.vo.CaptionOptionSetting;
import com.mstar.android.tvapi.common.vo.ColorTemperature;
import com.mstar.android.tvapi.common.vo.ColorTemperatureExData;
import com.mstar.android.tvapi.common.vo.EnumAudioMode;
import com.mstar.android.tvapi.common.vo.EnumChannelSwitchMode;
import com.mstar.android.tvapi.common.vo.EnumMaxDtvResolutionInfo;
import com.mstar.android.tvapi.common.vo.EnumPowerOnLogoMode;
import com.mstar.android.tvapi.common.vo.EnumPowerOnMusicMode;
import com.mstar.android.tvapi.common.vo.EnumSoundAdOutput;
import com.mstar.android.tvapi.common.vo.EnumSoundHidevMode;
import com.mstar.android.tvapi.common.vo.EnumSoundMode;
import com.mstar.android.tvapi.common.vo.EnumSpdifType;
import com.mstar.android.tvapi.common.vo.EnumSurroundMode;
import com.mstar.android.tvapi.common.vo.EnumSurroundSystemType;
import com.mstar.android.tvapi.common.vo.EnumThreeDVideoDisplayFormat;
import com.mstar.android.tvapi.common.vo.EnumCableOperator;
import com.mstar.android.tvapi.common.vo.MpegNoiseReduction.EnumMpegNoiseReduction;
import com.mstar.android.tvapi.common.vo.NoiseReduction.EnumNoiseReduction;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.mstar.android.tvapi.common.vo.VideoInfo;
import com.mstar.android.tvapi.common.vo.EnumPictureMode;
import com.mstar.android.tvapi.dtv.atsc.vo.RR5RatingPair;
import com.mstar.android.tvapi.dtv.atsc.vo.Regin5DimensionInformation;
import com.mstar.android.tvapi.dtv.atsc.vo.UsaMpaaRatingType;
import com.mstar.android.tvapi.dtv.atsc.vo.UsaMpaaRatingType.EnumUsaMpaaRatingType;
import com.mstar.android.tvapi.dtv.atsc.vo.UsaTvRatingInformation;
import com.mstar.android.tvapi.factory.vo.FactoryNsVdSet;
import com.mstar.android.tvapi.factory.vo.PqlCalibrationData;
import com.mstar.android.tv.TvLanguage;

public class DatabaseDesk implements IDatabaseDesk {
    private static final String TAG = "DatabaseDesk";

    private static DatabaseDesk sDataBaseDesk;

    private static Context sContext = null;

    private ContentResolver mContentResolver = null;

    private UserSetting mUserSetting = null;

    private UserSubtitleSetting mSubtitleSetting = null;

    private LocationSetting mLocationSetting = null;

    private UserSoundSetting mSoundSetting = null;

    private TimeSetting mTimeSetting = null;

    public ContentResolver getContentResolver() {
        if (mContentResolver == null) {
            mContentResolver = sContext.getContentResolver();
        }
        return mContentResolver;
    }

    public static DatabaseDesk getInstance(Context context) {
        sContext = context;
        if (sDataBaseDesk == null) {
            sDataBaseDesk = new DatabaseDesk();
        }
        return sDataBaseDesk;
    }

    private DatabaseDesk() {
        InitSettingVar();
        initVarSound();
    }

    @Override
    public void updateADCAdjust(CalbrationData model, int sourceId) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("u16RedGain", model.redGain);
        vals.put("u16GreenGain", model.greenGain);
        vals.put("u16BlueGain", model.blueGain);
        vals.put("u16RedOffset", model.redOffset);
        vals.put("u16GreenOffset", model.greenOffset);
        vals.put("u16BlueOffset", model.blueOffset);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.factory/adcadjust/sourceid/" + sourceId), vals,
                    null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_ADCAdjust ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_ADCAdjust_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public NonLinearAdjustSetting queryNonLinearAdjusts() {
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.factory/nonlinearadjust"), null,
                "InputSrcType = " + getCurrentInputSource(), null, "CurveTypeIndex");
        NonLinearAdjustSetting model = new NonLinearAdjustSetting();
        int i = 0;
        int length = model.nlaSetting.length;

        while (cursor.moveToNext()) {
            if (i > length - 1) {
                break;
            }
            model.nlaSetting[i].u8OSD_V0 = (short) cursor.getInt(cursor.getColumnIndex("u8OSD_V0"));
            model.nlaSetting[i].u8OSD_V25 = (short) cursor.getInt(cursor
                    .getColumnIndex("u8OSD_V25"));
            model.nlaSetting[i].u8OSD_V50 = (short) cursor.getInt(cursor
                    .getColumnIndex("u8OSD_V50"));
            model.nlaSetting[i].u8OSD_V75 = (short) cursor.getInt(cursor
                    .getColumnIndex("u8OSD_V75"));
            model.nlaSetting[i].u8OSD_V100 = (short) cursor.getInt(cursor
                    .getColumnIndex("u8OSD_V100"));
            i++;
        }
        cursor.close();
        return model;
    }

    @Override
    public void updateNonStandardAdjust(NonStandardVdSetting nonStandSet) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("u8AFEC_D4", nonStandSet.u8AFEC_D4);
        vals.put("u8AFEC_D5_Bit2", nonStandSet.u8AFEC_D5_Bit2);
        vals.put("u8AFEC_D8_Bit3210", nonStandSet.u8AFEC_D8_Bit3210);
        vals.put("u8AFEC_D9_Bit0", nonStandSet.u8AFEC_D9_Bit0);
        vals.put("u8AFEC_D7_LOW_BOUND", nonStandSet.u8AFEC_D7_LOW_BOUND);
        vals.put("u8AFEC_D7_HIGH_BOUND", nonStandSet.u8AFEC_D7_HIGH_BOUND);
        vals.put("u8AFEC_A0", nonStandSet.u8AFEC_A0);
        vals.put("u8AFEC_A1", nonStandSet.u8AFEC_A1);
        vals.put("u8AFEC_66_Bit76", nonStandSet.u8AFEC_66_Bit76);
        vals.put("u8AFEC_6E_Bit7654", nonStandSet.u8AFEC_6E_Bit7654);
        vals.put("u8AFEC_6E_Bit3210", nonStandSet.u8AFEC_6E_Bit3210);
        vals.put("u8AFEC_43", nonStandSet.u8AFEC_43);
        vals.put("u8AFEC_44", nonStandSet.u8AFEC_44);
        vals.put("u8AFEC_CB", nonStandSet.u8AFEC_CB);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.factory/nonstandardadjust"), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_NonStandardAdjust nonStandSet AFEC ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_NonStarndardAdjust_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateOverscanAdjust(int FactoryOverScanType, VideoWindowInfo[][] model) {
        int max1 = 0;
        long ret = -1;
        switch (FactoryOverScanType) {
            case 0:
                max1 = EnumMaxDtvResolutionInfo.E_MAX.ordinal();
                break;
            case 1:
                max1 = EnumHdmiResolutionInfo.E_NUM.ordinal();
                break;
            case 2:
                max1 = EnumYpbprResolutionInfo.E_NUM.ordinal();
                break;
            case 3:
                max1 = EnumVdSignalType.E_NUM.ordinal();
                break;
        }
        int max2 = EnumAspectRatioType.E_MAX.ordinal();
        int _id = 0;
        for (int i = 0; i < max1; i++) {
            for (int j = 0; j < max2; j++) {
                ContentValues vals = new ContentValues();
                vals.put("u16H_CapStart", model[i][j].hCapStart);
                vals.put("u16V_CapStart", model[i][j].vCapStart);
                vals.put("u8HCrop_Left", model[i][j].hCropLeft);
                vals.put("u8HCrop_Right", model[i][j].hCropRight);
                vals.put("u8VCrop_Up", model[i][j].vCropUp);
                vals.put("u8VCrop_Down", model[i][j].vCropDown);
                _id = i * max2 + j;
                try {
                    ret = getContentResolver()
                            .update(Uri.parse("content://mstar.tv.factory/overscanadjust/factoryoverscantype/"
                                    + FactoryOverScanType + "/_id/" + _id), vals, null, null);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (ret == -1) {
                    Log.d(TAG, "update tbl_OverscanAdjust ignored");
                }
            }
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_OverscanAdjust_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryVideo3DLrSwitchMode(int inputSrcType) {
        int value = 0;
        Cursor cursor3DMode = getContentResolver()
                .query(Uri.parse("content://mstar.tv.usersetting/threedvideomode/inputsrc/"
                        + inputSrcType), null, null, null, null);
        if (cursor3DMode.moveToFirst()) {
            value = cursor3DMode.getInt(cursor3DMode.getColumnIndex("eThreeDVideoLRViewSwitch"));
        }
        cursor3DMode.close();
        return value;
    }

    @Override
    public EnumThreeDVideoDisplayFormat queryThreeDVideoDisplayFormat(int inputSrcType) {
        EnumThreeDVideoDisplayFormat threeDVideoDisplayFormat = EnumThreeDVideoDisplayFormat.E_ThreeD_Video_DISPLAYFORMAT_NONE;
        Cursor cursor3DMode = getContentResolver()
                .query(Uri.parse("content://mstar.tv.usersetting/threedvideomode/inputsrc/"
                        + inputSrcType), null, null, null, null);
        if (cursor3DMode.moveToFirst()) {
            threeDVideoDisplayFormat = EnumThreeDVideoDisplayFormat.values()[cursor3DMode
                    .getInt(cursor3DMode.getColumnIndex("eThreeDVideoDisplayFormat"))];
        }
        cursor3DMode.close();
        return threeDVideoDisplayFormat;
    }

    @Override
    public void updatePicModeSetting(EN_MS_VIDEOITEM eIndex, int inputSrcType, int pictureModeType,
            int value) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        switch (eIndex) {
            case E_BRIGHTNESS:
                vals.put("u8Brightness", value);
                break;
            case E_CONTRAST:
                vals.put("u8Contrast", value);
                break;
            case E_HUE:
                vals.put("u8Hue", value);
                break;
            case E_SATURATION:
                vals.put("u8Saturation", value);
                break;
            case E_SHARPNESS:
                vals.put("u8Sharpness", value);
                break;
            case E_BACKLIGHT:
                vals.put("u8Backlight", value);
            default:
                break;
        }
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/picmode_setting/inputsrc/"
                            + inputSrcType + "/picmode/" + pictureModeType), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_PicMode_Setting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_PicMode_Setting ignored");
        }
    }

    @Override
    public int queryPicModeSetting(EN_MS_VIDEOITEM eIndex, int inputSrcType, int pictureModeType) {
        Cursor cursorPicMode = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/picmode_setting/inputsrc/" + inputSrcType
                        + "/picmode/" + pictureModeType), null, null, null, null);
        cursorPicMode.moveToFirst();
        int value = 0;
        switch (eIndex) {
            case E_BRIGHTNESS:
                value = cursorPicMode.getInt(cursorPicMode.getColumnIndex("u8Brightness"));
                break;
            case E_CONTRAST:
                value = cursorPicMode.getInt(cursorPicMode.getColumnIndex("u8Contrast"));
                break;
            case E_HUE:
                value = cursorPicMode.getInt(cursorPicMode.getColumnIndex("u8Hue"));
                break;
            case E_SATURATION:
                value = cursorPicMode.getInt(cursorPicMode.getColumnIndex("u8Saturation"));
                break;
            case E_SHARPNESS:
                value = cursorPicMode.getInt(cursorPicMode.getColumnIndex("u8Sharpness"));
                break;
            case E_BACKLIGHT:
                value = cursorPicMode.getInt(cursorPicMode.getColumnIndex("u8Backlight"));
                break;
            default:
                break;
        }
        cursorPicMode.close();
        return value;
    }

    @Override
    public PictureModeSetting queryPictureModeSettings(int eModeIdx, int inputSrcType) {
        PictureModeSetting model = new PictureModeSetting((short) 0, (short) 0, (short) 0,
                (short) 0, (short) 0, (short) 0, EnumColorTemperature_.E_USER,
                EnumAdvancedPictureSetting.E_OFF, EnumAdvancedPictureSetting.E_OFF,
                EnumAdvancedPictureSetting.E_OFF, EnumAdvancedPictureSetting.E_OFF);
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/picmode_setting/inputsrc/" + inputSrcType
                        + "/picmode/" + eModeIdx), null, null, null, null);
        if (cursor.moveToFirst()) {
            model.backlight = (short) cursor.getInt(cursor.getColumnIndex("u8Backlight"));
            model.contrast = (short) cursor.getInt(cursor.getColumnIndex("u8Contrast"));
            model.brightness = (short) cursor.getInt(cursor.getColumnIndex("u8Brightness"));
            model.saturation = (short) cursor.getInt(cursor.getColumnIndex("u8Saturation"));
            model.sharpness = (short) cursor.getInt(cursor.getColumnIndex("u8Sharpness"));
            model.hue = (short) cursor.getInt(cursor.getColumnIndex("u8Hue"));
        }
        cursor.close();
        return model;
    }

    @Override
    public int queryPictureMode(int inputSrcType) {
        int ret = -1;
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/videosetting/inputsrc/" + inputSrcType),
                null, null, null, null);
        if (cursor.moveToFirst()) {
            ret = cursor.getInt(cursor.getColumnIndex("ePicture"));
        }
        cursor.close();
        return ret;
    }

    @Override
    public void updatePictureMode(int ePicMode, int inputSrcType) {
        ContentValues vals = new ContentValues();
        vals.put("ePicture", ePicMode);
        try {
            getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/videosetting/inputsrc/"
                            + inputSrcType), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_VideoSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateColorTempIdx(int inputSrcType, int pictureModeType,
            EnumColorTemperature_ eColorTemp) {
        ContentValues vals = new ContentValues();
        vals.put("eColorTemp", eColorTemp.ordinal());
        try {
            getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/picmode_setting/inputsrc/"
                            + inputSrcType + "/picmode/" + pictureModeType), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_PicMode_Setting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public EnumColorTemperature_ queryColorTempIdx(int inputSrcType, int pictureModeType) {
        Cursor cursorPicMode = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/picmode_setting/inputsrc/" + inputSrcType
                        + "/picmode/" + pictureModeType), null, null, null, "PictureModeType");
        cursorPicMode.moveToFirst();
        EnumColorTemperature_ eColorTemp = EnumColorTemperature_.values()[cursorPicMode
                .getInt(cursorPicMode.getColumnIndex("eColorTemp"))];
        cursorPicMode.close();
        return eColorTemp;
    }

    @Override
    public int queryArcMode(int inputSrcType) {
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/videosetting/inputsrc/" + inputSrcType),
                null, null, null, null);
        int eArcMode = 0;
        if (cursor.moveToFirst()) {
            eArcMode = cursor.getInt(cursor.getColumnIndex("enARCType"));
        }
        cursor.close();
        return eArcMode;
    }

    @Override
    public void updateArcMode(int eArcMode, int inputSrcType) {
        ContentValues vals = new ContentValues();
        vals.put("enARCType", eArcMode);
        try {
            getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/videosetting/inputsrc/"
                            + inputSrcType), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_VideoSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryArcMode(int inputSrcType, int picturemode) {
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/videosetting/inputsrc/" + inputSrcType),
                null, null, null, null);
        int eArcMode = 0;
        if (cursor.moveToFirst()) {
            if (picturemode == EnumPictureMode.PICTURE_GAME.ordinal()) {
                eArcMode = cursor.getInt(cursor.getColumnIndex("enGameModeARCType"));
            } else if (picturemode == EnumPictureMode.PICTURE_AUTO.ordinal()) {
                eArcMode = cursor.getInt(cursor.getColumnIndex("enAutoModeARCType"));
            } else if (picturemode == EnumPictureMode.PICTURE_PC.ordinal()) {
                eArcMode = cursor.getInt(cursor.getColumnIndex("enPcModeARCType"));
            } else {
                eArcMode = cursor.getInt(cursor.getColumnIndex("enARCType"));
            }
        }
        cursor.close();
        return eArcMode;
    }

    @Override
    public void updateArcMode(int eArcMode, int inputSrcType, int picturemode) {
        ContentValues vals = new ContentValues();
        if (picturemode == EnumPictureMode.PICTURE_GAME.ordinal()) {
            vals.put("enGameModeARCType", eArcMode);
        } else if (picturemode == EnumPictureMode.PICTURE_AUTO.ordinal()) {
            vals.put("enAutoModeARCType", eArcMode);
        } else if (picturemode == EnumPictureMode.PICTURE_PC.ordinal()) {
            vals.put("enPcModeARCType", eArcMode);
        } else {
            vals.put("enARCType", eArcMode);
        }
        try {
            getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/videosetting/inputsrc/"
                            + inputSrcType), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_VideoSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryFilmMode(int inputSrcType) {
        int ret = 0;
        Cursor cursorVideo = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/videosetting/inputsrc/" + inputSrcType),
                null, null, null, null);

        if (cursorVideo.moveToFirst()) {
            ret = cursorVideo.getInt(cursorVideo.getColumnIndex("eFilm"));
        }
        cursorVideo.close();
        return ret;
    }

    @Override
    public void updateFilmMode(int mode, int inputSrcType) {
        ContentValues vals = new ContentValues();
        vals.put("eFilm", mode);
        try {
            getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/videosetting/inputsrc/"
                            + inputSrcType), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_VideoSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateVideoAstPicture(PictureModeSetting model, int inputSrcType,
            int pictureModeType) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("u8Backlight", model.backlight);
        vals.put("u8Contrast", model.contrast);
        vals.put("u8Brightness", model.brightness);
        vals.put("u8Saturation", model.saturation);
        vals.put("u8Sharpness", model.sharpness);
        vals.put("u8Hue", model.hue);
        vals.put("eColorTemp", model.eColorTemp.ordinal());
        vals.put("eVibrantColour", model.eVibrantColour.ordinal());
        vals.put("ePerfectClear", model.ePerfectClear.ordinal());
        vals.put("eDynamicContrast", model.eDynamicContrast.ordinal());
        vals.put("eDynamicBacklight", model.eDynamicBacklight.ordinal());
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/picmode_setting/inputsrc/"
                            + inputSrcType + "/picmode/" + pictureModeType), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_PicMode_Setting ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_PicMode_Setting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateVideoNRMode(NrMode model, int inputSrcType, int NRModeIdx) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("eNR", model.eNR.ordinal());
        vals.put("eMPEG_NR", model.eMPEG_NR.ordinal());
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/nrmode/nrmode/" + NRModeIdx
                            + "/inputsrc/" + inputSrcType), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_NRMode ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_NRMode_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateVideoAstSubColor(SubColorSetting model, int inputSrcType) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("u8SubBrightness", model.subBrightness);
        vals.put("u8SubContrast", model.subContrast);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/videosetting/inputsrc/"
                            + inputSrcType), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_VideoSetting ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_VideoSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateVideo3DMode(EnumS3dTableField field, int value, int inputSrcType) {
        ContentValues vals = new ContentValues();
        switch (field) {
            case E_VIDEO:
                vals.put("eThreeDVideo", value);
                break;
            case E_DISPLAYFORMAT:
                vals.put("eThreeDVideoDisplayFormat", value);
                break;
            case E_3D_DEPTH:
                vals.put("eThreeDVideo3DDepth", value);
                break;
            case E_AUTO_START:
                vals.put("eThreeDVideoAutoStart", value);
                break;
            case E_3D_OUTPUT_ASPECT:
                vals.put("eThreeDVideo3DOutputAspect", value);
                break;
            case E_LR_VIEW_SWITCH:
                vals.put("eThreeDVideoLRViewSwitch", value);
                break;
            case E_SELF_ADAPTIVE_DETECT:
                vals.put("eThreeDVideoSelfAdaptiveDetect", value);
                break;
            case E_SELF_ADAPTIVE_LEVEL:
                vals.put("eThreeDVideoSelfAdaptiveLevel", value);
                break;
            case E_3D_TO_2D:
                vals.put("eThreeDVideo3DTo2D", value);
                break;
            case E_3D_OFFSET:
                vals.put("eThreeDVideo3DOffset", value);
                break;
            default:
                return;
        }
        try {
            getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/threedvideomode/inputsrc/"
                            + inputSrcType), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_ThreeDVideoMode_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryVideo3DMode(EnumS3dTableField field, int inputSrcType) {
        int value = 0;
        String fieldStr = "";
        switch (field) {
            case E_VIDEO:
                fieldStr = "eThreeDVideo";
                break;
            case E_DISPLAYFORMAT:
                fieldStr = "eThreeDVideoDisplayFormat";
                break;
            case E_3D_DEPTH:
                fieldStr = "eThreeDVideo3DDepth";
                break;
            case E_AUTO_START:
                fieldStr = "eThreeDVideoAutoStart";
                break;
            case E_3D_OUTPUT_ASPECT:
                fieldStr = "eThreeDVideo3DOutputAspect";
                break;
            case E_LR_VIEW_SWITCH:
                fieldStr = "eThreeDVideoLRViewSwitch";
                break;
            case E_SELF_ADAPTIVE_DETECT:
                fieldStr = "eThreeDVideoSelfAdaptiveDetect";
                break;
            case E_SELF_ADAPTIVE_LEVEL:
                fieldStr = "eThreeDVideoSelfAdaptiveLevel";
                break;
            case E_3D_TO_2D:
                fieldStr = "eThreeDVideo3DTo2D";
                break;
            case E_3D_OFFSET:
                fieldStr = "eThreeDVideo3DOffset";
                break;
            default:
                return -1;
        }
        Cursor cursor3DMode = getContentResolver()
                .query(Uri.parse("content://mstar.tv.usersetting/threedvideomode/inputsrc/"
                        + inputSrcType), null, null, null, null);
        if (cursor3DMode.moveToFirst()) {
            value = cursor3DMode.getInt(cursor3DMode.getColumnIndex(fieldStr));
        }
        cursor3DMode.close();
        return value;
    }

    @Override
    public void updateVideoUserOverScanMode(UserOverScanSetting model, int inputSrcType) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("OverScanHposition", model.overScanHposition);
        vals.put("OverScanVposition", model.overScanVposition);
        vals.put("OverScanHRatio", model.overScanHRatio);
        vals.put("OverScanVRatio", model.overScanVRatio);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/useroverscanmode/inputsrc/"
                            + inputSrcType), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_UserOverScanMode ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_UserOverScanMode_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateNR(int inputSrcType, int colorTmpIdx, EnumNoiseReduction eNRIdx) {
        ContentValues vals = new ContentValues();
        vals.put("eNR", eNRIdx.getValue());
        try {
            getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/nrmode/nrmode/" + colorTmpIdx
                            + "/inputsrc/" + inputSrcType), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_NRMode_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public EnumNrSetting queryNR(int inputSrcType, int colorTmpIdx) {
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/nrmode/nrmode/" + colorTmpIdx
                        + "/inputsrc/" + inputSrcType), null, null, null, null);
        EnumNrSetting value = null;
        if (cursor.moveToFirst()) {
            value = EnumNrSetting.values()[cursor.getInt(cursor.getColumnIndex("eNR"))];
        }
        cursor.close();
        return value;
    }

    @Override
    public EnumMpegNrSetting queryMpegNR(int inputSrcType, int colorTmpIdx) {
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/nrmode/nrmode/" + colorTmpIdx
                        + "/inputsrc/" + inputSrcType), null, null, null, null);
        EnumMpegNrSetting value = null;
        if (cursor.moveToNext()) {
            value = EnumMpegNrSetting.values()[cursor.getInt(cursor.getColumnIndex("eMPEG_NR"))];
        }
        cursor.close();
        return value;
    }

    @Override
    public void updateMpegNR(int inputSrcType, int colorTmpIdx, EnumMpegNoiseReduction eMpNRIdx) {
        ContentValues vals = new ContentValues();
        vals.put("eMPEG_NR", eMpNRIdx.getValue());
        try {
            getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/nrmode/nrmode/" + colorTmpIdx
                            + "/inputsrc/" + inputSrcType), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_NRMode_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ColorTemperatureExData queryUsrColorTmpExData(int nInputType) {
        ColorTemperatureExData colorTempatureExData = new ColorTemperatureExData();
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/usercolortempex/" + nInputType), null,
                null, null, null);
        if (cursor.moveToFirst()) {
            colorTempatureExData.redGain = cursor.getInt(cursor.getColumnIndex("u16RedGain"));
            colorTempatureExData.greenGain = cursor.getInt(cursor.getColumnIndex("u16GreenGain"));
            colorTempatureExData.blueGain = cursor.getInt(cursor.getColumnIndex("u16BlueGain"));
            colorTempatureExData.redOffset = cursor.getInt(cursor.getColumnIndex("u16RedOffset"));
            colorTempatureExData.greenOffset = cursor.getInt(cursor
                    .getColumnIndex("u16GreenOffset"));
            colorTempatureExData.blueOffset = cursor.getInt(cursor.getColumnIndex("u16BlueOffset"));
        }
        cursor.close();
        return colorTempatureExData;
    }

    @Override
    public void updateUsrColorTmpExData(ColorTemperatureExData model, int nInputType) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("u16RedGain", model.redGain);
        vals.put("u16GreenGain", model.greenGain);
        vals.put("u16BlueGain", model.blueGain);
        vals.put("u16RedOffset", model.redOffset);
        vals.put("u16GreenOffset", model.greenOffset);
        vals.put("u16BlueOffset", model.blueOffset);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/usercolortempex/" + nInputType),
                    vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_UserColorTempEx ignored");
        }

        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_USER_COLORTEMP_EX_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public UserSetting queryUserSysSetting() {
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/systemsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            mUserSetting.isRunInstallationGuide = cursor.getInt(cursor
                    .getColumnIndex("fRunInstallationGuide")) == 0 ? false : true;
            mUserSetting.isNoChannel = cursor.getInt(cursor.getColumnIndex("fNoChannel")) == 0 ? false
                    : true;
            mUserSetting.isDisableSiAutoUpdate = cursor.getInt(cursor
                    .getColumnIndex("bDisableSiAutoUpdate")) == 0 ? false : true;
            mUserSetting.enInputSourceType = EnumInputSource.values()[cursor.getInt(cursor
                    .getColumnIndex("enInputSourceType"))];
            mUserSetting.eCableOperators = EnumCableOperator.values()[cursor.getInt(cursor
                    .getColumnIndex("enCableOperators"))];
            mUserSetting.eSatellitePlatform = EnumSatellitePlatform.values()[cursor.getInt(cursor
                    .getColumnIndex("enSatellitePlatform"))];
            mUserSetting.networkId = cursor.getInt(cursor.getColumnIndex("u16NetworkId"));
            mUserSetting.osdLanguage = cursor.getInt(cursor.getColumnIndex("Language"));
            mUserSetting.eSpdifMode = EnumSpdifType.values()[cursor.getInt(cursor
                    .getColumnIndex("enSPDIFMODE"))];
            mUserSetting.softwareUpdate = (short) cursor.getInt(cursor
                    .getColumnIndex("fSoftwareUpdate"));
            mUserSetting.oadTime = (short) cursor.getInt(cursor.getColumnIndex("U8OADTime"));
            mUserSetting.oadScanAfterWakeup = (short) cursor.getInt(cursor
                    .getColumnIndex("fOADScanAfterWakeup"));
            mUserSetting.autoVolume = (short) cursor.getInt(cursor.getColumnIndex("fAutoVolume"));
            mUserSetting.dcPowerOFFMode = (short) cursor.getInt(cursor
                    .getColumnIndex("fDcPowerOFFMode"));
            mUserSetting.dtvRoute = (short) cursor.getInt(cursor.getColumnIndex("DtvRoute"));
            mUserSetting.scartOutRGB = (short) cursor.getInt(cursor.getColumnIndex("ScartOutRGB"));
            mUserSetting.transparency = (short) cursor.getInt(cursor
                    .getColumnIndex("U8Transparency"));
            mUserSetting.menuTimeOut = cursor.getLong(cursor.getColumnIndex("u32MenuTimeOut"));
            mUserSetting.audioOnly = (short) cursor.getInt(cursor.getColumnIndex("AudioOnly"));
            mUserSetting.isEnableWDT = (short) cursor.getInt(cursor.getColumnIndex("bEnableWDT"));
            mUserSetting.favoriteRegion = (short) cursor.getInt(cursor
                    .getColumnIndex("u8FavoriteRegion"));
            mUserSetting.bandwidth = (short) cursor.getInt(cursor.getColumnIndex("u8Bandwidth"));
            mUserSetting.timeShiftSizeType = (short) cursor.getInt(cursor
                    .getColumnIndex("u8TimeShiftSizeType"));
            mUserSetting.oadScan = (short) cursor.getInt(cursor.getColumnIndex("fOadScan"));
            mUserSetting.enablePVRRecordAll = (short) cursor.getInt(cursor
                    .getColumnIndex("bEnablePVRRecordAll"));
            mUserSetting.colorRangeMode = (short) cursor.getInt(cursor
                    .getColumnIndex("u8ColorRangeMode"));
            mUserSetting.hdmiAudioSource = (short) cursor.getInt(cursor
                    .getColumnIndex("u8HDMIAudioSource"));
            mUserSetting.enableAlwaysTimeshift = (short) cursor.getInt(cursor
                    .getColumnIndex("bEnableAlwaysTimeshift"));
            mUserSetting.eSUPER = EnumSuperModeSettings.values()[cursor.getInt(cursor
                    .getColumnIndex("eSUPER"))];
            mUserSetting.isCheckUartBus = cursor.getInt(cursor.getColumnIndex("bUartBus")) == 0 ? false
                    : true;
            mUserSetting.autoZoom = (short) cursor.getInt(cursor.getColumnIndex("m_AutoZoom"));
            mUserSetting.isOverScanForAllSource = cursor.getInt(cursor.getColumnIndex("bOverScan")) == 0 ? false
                    : true;
            mUserSetting.brazilVideoStandardType = (short) cursor.getInt(cursor
                    .getColumnIndex("m_u8BrazilVideoStandardType"));
            mUserSetting.softwareUpdateMode = (short) cursor.getInt(cursor
                    .getColumnIndex("m_u8SoftwareUpdateMode"));
            mUserSetting.osdActiveTime = cursor.getLong(cursor.getColumnIndex("OSD_Active_Time"));
            mUserSetting.isMessageBoxExist = cursor.getInt(cursor
                    .getColumnIndex("m_MessageBoxExist")) == 0 ? false : true;
            mUserSetting.lastOADVersion = cursor.getInt(cursor.getColumnIndex("u16LastOADVersion"));
            mUserSetting.isAutoChannelUpdateEnable = cursor.getInt(cursor
                    .getColumnIndex("bEnableAutoChannelUpdate")) == 0 ? false : true;
            mUserSetting.eChannelSwitchMode = cursor.getInt(cursor
                    .getColumnIndex("bATVChSwitchFreeze")) == 0 ? EnumChannelSwitchMode.E_CHANNEL_SWM_BLACKSCREEN
                    : EnumChannelSwitchMode.E_CHANNEL_SWM_FREEZE;
            try {
                mUserSetting.bViewerPrompt = cursor.getInt(cursor
                        .getColumnIndexOrThrow("bViewerPrompt")) == 0 ? false : true;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            try {
                mUserSetting.bEnableStoreCookies = cursor.getInt(cursor
                        .getColumnIndexOrThrow("bEnableStoreCookies")) == 0 ? false : true;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return mUserSetting;
    }

    @Override
    public int queryCountry() {
        int country = 0;
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/systemsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            country = cursor.getInt(cursor.getColumnIndex("Country"));
        }
        cursor.close();
        return country;
    }

    @Override
    public void updateUserSysSetting(UserSetting model) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("fRunInstallationGuide", model.isRunInstallationGuide ? 1 : 0);
        vals.put("fNoChannel", model.isNoChannel ? 1 : 0);
        vals.put("bDisableSiAutoUpdate", model.isDisableSiAutoUpdate ? 1 : 0);
        vals.put("enCableOperators", model.eCableOperators.ordinal());
        vals.put("enSatellitePlatform", model.eSatellitePlatform.ordinal());
        vals.put("u16NetworkId", model.networkId);
        vals.put("Language", model.osdLanguage);
        vals.put("enSPDIFMODE", model.eSpdifMode.ordinal());
        vals.put("fSoftwareUpdate", model.softwareUpdate);
        vals.put("U8OADTime", model.oadTime);
        vals.put("fOADScanAfterWakeup", model.oadScanAfterWakeup);
        vals.put("fAutoVolume", model.autoVolume);
        vals.put("fDcPowerOFFMode", model.dcPowerOFFMode);
        vals.put("DtvRoute", model.dtvRoute);
        vals.put("ScartOutRGB", model.scartOutRGB);
        vals.put("U8Transparency", model.transparency);
        vals.put("u32MenuTimeOut", model.menuTimeOut);
        vals.put("AudioOnly", model.audioOnly);
        vals.put("bEnableWDT", model.isEnableWDT);
        vals.put("u8FavoriteRegion", model.favoriteRegion);
        vals.put("u8Bandwidth", model.bandwidth);
        vals.put("u8TimeShiftSizeType", model.timeShiftSizeType);
        vals.put("fOadScan", model.oadScan);
        vals.put("bEnablePVRRecordAll", model.enablePVRRecordAll);
        vals.put("u8ColorRangeMode", model.colorRangeMode);
        vals.put("u8HDMIAudioSource", model.hdmiAudioSource);
        vals.put("bEnableAlwaysTimeshift", model.enableAlwaysTimeshift);
        vals.put("eSUPER", model.eSUPER.ordinal());
        vals.put("bUartBus", model.isCheckUartBus ? 1 : 0);
        vals.put("m_AutoZoom", model.autoZoom);
        vals.put("bOverScan", model.isOverScanForAllSource ? 1 : 0);
        vals.put("m_u8BrazilVideoStandardType", model.brazilVideoStandardType);
        vals.put("m_u8SoftwareUpdateMode", model.softwareUpdateMode);
        vals.put("OSD_Active_Time", model.osdActiveTime);
        vals.put("m_MessageBoxExist", model.isMessageBoxExist ? 1 : 0);
        vals.put("u16LastOADVersion", model.lastOADVersion);
        vals.put("bEnableAutoChannelUpdate", model.isAutoChannelUpdateEnable ? 1 : 0);
        vals.put("bATVChSwitchFreeze", model.eChannelSwitchMode.ordinal());
        vals.put("bViewerPrompt", model.bViewerPrompt);
        vals.put("bEnableStoreCookies", model.bEnableStoreCookies);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/systemsetting"), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_SystemSetting ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_SystemSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int querySpdifMode() {
        int ret = 0;
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/systemsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            ret = cursor.getInt(cursor.getColumnIndex("enSPDIFMODE"));
        }
        cursor.close();
        return ret;
    }

    @Override
    public void updateSpdifMode(int mode) {
        ContentValues vals = new ContentValues();
        vals.put("enSPDIFMODE", mode);
        try {
            getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/systemsetting"),
                    vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_SystemSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public UserSubtitleSetting queryUserSubtitleSetting() {
        Cursor cursor = getContentResolver()
                .query(Uri.parse("content://mstar.tv.usersetting/subtitlesetting"), null, null,
                        null, null);
        if (cursor.moveToFirst()) {
            mSubtitleSetting.subtitleLanguage1 = cursor.getInt(cursor
                    .getColumnIndex("SubtitleDefaultLanguage"));
            mSubtitleSetting.subtitleLanguage2 = cursor.getInt(cursor
                    .getColumnIndex("SubtitleDefaultLanguage_2"));
            mSubtitleSetting.isHoHEnable = cursor.getInt(cursor.getColumnIndex("fHardOfHearing")) == 0 ? false
                    : true;
            mSubtitleSetting.isSubtitleEnable = cursor.getInt(cursor
                    .getColumnIndex("fEnableSubTitle")) == 0 ? false : true;
        }
        cursor.close();
        return mSubtitleSetting;
    }

    @Override
    public void updateUserSubtitleSetting(UserSubtitleSetting model) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("SubtitleDefaultLanguage", model.subtitleLanguage1);
        vals.put("SubtitleDefaultLanguage_2", model.subtitleLanguage2);
        vals.put("fHardOfHearing", model.isHoHEnable ? 1 : 0);
        vals.put("fEnableSubTitle", model.isSubtitleEnable ? 1 : 0);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/subtitlesetting"), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_SubtitleSetting ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_SubtitleSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public LocationSetting queryUserLocSetting() {
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/userlocationsetting"), null, null, null,
                null);
        if (cursor.moveToFirst()) {
            mLocationSetting.locationNo = (short) cursor.getInt(cursor
                    .getColumnIndex("u16LocationNo"));
            mLocationSetting.manualLongitude = (short) cursor.getInt(cursor
                    .getColumnIndex("s16ManualLongitude"));
            mLocationSetting.manualLatitude = (short) cursor.getInt(cursor
                    .getColumnIndex("s16ManualLatitude"));
        }
        cursor.close();
        return mLocationSetting;
    }

    @Override
    public void updateUserLocSetting(LocationSetting model) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("u16LocationNo", model.locationNo);
        vals.put("s16ManualLongitude", model.manualLongitude);
        vals.put("s16ManualLatitude", model.manualLatitude);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/userlocationsetting"), vals, null,
                    null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_UserLocationSetting ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_UserLocationSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int querySoundMode() {
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/soundsetting"), null, null, null, null);
        int ret = 0;
        if (cursor.moveToFirst()) {
            ret = cursor.getInt(cursor.getColumnIndex("SoundMode"));
        }
        cursor.close();
        return ret;
    }

    @Override
    public void updateSoundMode(int soudModeType) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("SoundMode", soudModeType);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/soundsetting"), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_SoundSetting ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_SoundSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryVolume() {
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/soundsetting"), null, null, null, null);
        int ret = 0;
        if (cursor.moveToFirst()) {
            ret = (short) cursor.getInt(cursor.getColumnIndex("Volume"));
        }
        cursor.close();
        return ret;
    }

    @Override
    public void updateVolume(int vol) {
        ContentValues vals = new ContentValues();
        vals.put("Volume", vol);
        try {
            getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/soundsetting"),
                    vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_SoundSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public UserSoundSetting querySoundSetting() {
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/soundsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            mSoundSetting.eSoundMode = EnumSoundMode.values()[cursor.getInt(cursor
                    .getColumnIndex("SoundMode"))];
            mSoundSetting.eAudysseyDynamicVolume = EnumAudysseyDynamicVolumeMode.values()[cursor
                    .getInt(cursor.getColumnIndex("AudysseyDynamicVolume"))];
            mSoundSetting.eAudysseyEQ = EnumAudysseyEqMode.values()[cursor.getInt(cursor
                    .getColumnIndex("AudysseyEQ"))];
            mSoundSetting.eSurroundSoundMode = EnumSurroundSystemType.values()[cursor.getInt(cursor
                    .getColumnIndex("SurroundSoundMode"))];
            mSoundSetting.eSurroundMode = EnumSurroundMode.values()[cursor.getInt(cursor
                    .getColumnIndex("Surround"))];
            mSoundSetting.isAVCEnable = cursor.getInt(cursor.getColumnIndex("bEnableAVC")) == 0 ? false
                    : true;
            mSoundSetting.volume = (short) cursor.getInt(cursor.getColumnIndex("Volume"));
            mSoundSetting.headphoneVolume = (short) cursor
                    .getInt(cursor.getColumnIndex("HPVolume"));
            mSoundSetting.balance = (short) cursor.getInt(cursor.getColumnIndex("Balance"));
            mSoundSetting.primaryFlag = (short) cursor
                    .getInt(cursor.getColumnIndex("Primary_Flag"));
            mSoundSetting.eSoundAudioLan1 = cursor.getInt(cursor.getColumnIndex("enSoundAudioLan1"));
            mSoundSetting.eSoundAudioLan2 = cursor.getInt(cursor.getColumnIndex("enSoundAudioLan2"));
            mSoundSetting.muteFlag = (short) cursor.getInt(cursor.getColumnIndex("MUTE_Flag"));
            mSoundSetting.eSoundAudioChannel = EnumAudioMode.values()[cursor.getInt(cursor
                    .getColumnIndex("enSoundAudioChannel"))];
            mSoundSetting.isADEnable = cursor.getInt(cursor.getColumnIndex("bEnableAD")) == 0 ? false
                    : true;
            mSoundSetting.adVolume = (short) cursor.getInt(cursor.getColumnIndex("ADVolume"));
            mSoundSetting.eADOutput = EnumSoundAdOutput.values()[cursor.getInt(cursor
                    .getColumnIndex("ADOutput"))];
            mSoundSetting.spdifDelay = (short) cursor.getInt(cursor.getColumnIndex("SPDIF_Delay"));
            mSoundSetting.speakDelay = (short) cursor
                    .getInt(cursor.getColumnIndex("Speaker_Delay"));
        }
        cursor.close();
        return mSoundSetting;
    }

    @Override
    public void updateSoundSetting(UserSoundSetting model) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("SoundMode", model.eSoundMode.ordinal());
        vals.put("AudysseyDynamicVolume", model.eAudysseyDynamicVolume.ordinal());
        vals.put("AudysseyEQ", model.eAudysseyEQ.ordinal());
        vals.put("SurroundSoundMode", model.eSurroundSoundMode.ordinal());
        vals.put("Surround", model.eSurroundMode.ordinal());
        vals.put("bEnableAVC", model.isAVCEnable ? 1 : 0);
        vals.put("Volume", model.volume);
        vals.put("HPVolume", model.headphoneVolume);
        vals.put("Balance", model.balance);
        vals.put("Primary_Flag", model.primaryFlag);
        vals.put("enSoundAudioLan1", model.eSoundAudioLan1);
        vals.put("enSoundAudioLan2", model.eSoundAudioLan2);
        vals.put("MUTE_Flag", model.muteFlag);
        vals.put("enSoundAudioChannel", model.eSoundAudioChannel.ordinal());
        vals.put("bEnableAD", model.isADEnable ? 1 : 0);
        vals.put("ADVolume", model.adVolume);
        vals.put("ADOutput", model.eADOutput.ordinal());
        vals.put("SPDIF_Delay", model.spdifDelay);
        vals.put("Speaker_Delay", model.speakDelay);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/soundsetting"), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_SoundSetting ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_SoundSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSoundModeSetting(SoundModeSeting model, int soundModeType) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("Bass", model.bass);
        vals.put("Treble", model.treble);
        vals.put("EqBand1", model.eqBand1);
        vals.put("EqBand2", model.eqBand2);
        vals.put("EqBand3", model.eqBand3);
        vals.put("EqBand4", model.eqBand4);
        vals.put("EqBand5", model.eqBand5);
        vals.put("EqBand6", model.eqBand6);
        vals.put("EqBand7", model.eqBand7);
        vals.put("UserMode", model.isUserMode ? 1 : 0);
        vals.put("Balance", model.balance);
        vals.put("enSoundAudioChannel", model.eSoundAudioChannel.ordinal());
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/soundmodesetting/" + soundModeType),
                    vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_SoundModeSetting ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_SoundMode_Setting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SoundModeSeting querySoundModeSetting(int soundModeType) {
        SoundModeSeting model = new SoundModeSeting((short) 0, (short) 0, (short) 0, (short) 0,
                (short) 0, (short) 0, (short) 0);
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/soundmodesetting/" + soundModeType),
                null, null, null, null);
        if (cursor.moveToFirst()) {
            model.bass = (short) cursor.getInt(cursor.getColumnIndex("Bass"));
            model.treble = (short) cursor.getInt(cursor.getColumnIndex("Treble"));
            model.eqBand1 = (short) cursor.getInt(cursor.getColumnIndex("EqBand1"));
            model.eqBand2 = (short) cursor.getInt(cursor.getColumnIndex("EqBand2"));
            model.eqBand3 = (short) cursor.getInt(cursor.getColumnIndex("EqBand3"));
            model.eqBand4 = (short) cursor.getInt(cursor.getColumnIndex("EqBand4"));
            model.eqBand5 = (short) cursor.getInt(cursor.getColumnIndex("EqBand5"));
            model.eqBand6 = (short) cursor.getInt(cursor.getColumnIndex("EqBand6"));
            model.eqBand7 = (short) cursor.getInt(cursor.getColumnIndex("EqBand7"));
            model.isUserMode = cursor.getInt(cursor.getColumnIndex("UserMode")) == 0 ? false : true;
            model.balance = (short) cursor.getInt(cursor.getColumnIndex("Balance"));
            model.eSoundAudioChannel = EnumAudioMode.values()[cursor.getInt(cursor
                    .getColumnIndex("enSoundAudioChannel"))];
        }
        cursor.close();
        return model;
    }

    @Override
    public int queryBass(int soundMode) {
        int ret = 0;
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/soundmodesetting/" + soundMode), null,
                null, null, null);
        if (cursor.moveToFirst()) {
            ret = cursor.getInt(cursor.getColumnIndex("Bass"));
        }
        cursor.close();
        return ret;
    }

    @Override
    public void updateBass(int bass, int soundMode) {
        ContentValues vals = new ContentValues();
        vals.put("Bass", bass);
        try {
            getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/soundmodesetting/" + soundMode),
                    vals, null, null);
        } catch (SQLException e) {
        }

        try {
            if (TvManager.getInstance() != null) {
                TvManager.getInstance().getDatabaseManager()
                        .setDatabaseDirtyByApplication(T_SoundMode_Setting_IDX);
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryTreble(int soundMode) {
        int ret = 0;
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/soundmodesetting/" + soundMode), null,
                null, null, null);
        if (cursor.moveToFirst()) {
            ret = cursor.getInt(cursor.getColumnIndex("Treble"));
        }
        cursor.close();
        return ret;
    }

    @Override
    public void updateTreble(int treble, int soundMode) {
        ContentValues vals = new ContentValues();
        vals.put("Treble", treble);
        try {
            getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/soundmodesetting/" + soundMode),
                    vals, null, null);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (TvManager.getInstance() != null) {
                TvManager.getInstance().getDatabaseManager()
                        .setDatabaseDirtyByApplication(T_SoundMode_Setting_IDX);
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryBalance() {
        int ret = 0;
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/soundsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            ret = cursor.getInt(cursor.getColumnIndex("Balance"));
        }
        cursor.close();

        return ret;
    }

    @Override
    public void updateBalance(int balance) {
        ContentValues vals = new ContentValues();
        vals.put("Balance", balance);
        try {
            getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/soundsetting"),
                    vals, null, null);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (TvManager.getInstance() != null) {
                TvManager.getInstance().getDatabaseManager()
                        .setDatabaseDirtyByApplication(T_SoundMode_Setting_IDX);
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryAvc() {
        int ret = 0;
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/soundsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            ret = cursor.getInt(cursor.getColumnIndex("bEnableAVC"));
        }
        cursor.close();
        return ret;
    }

    @Override
    public void updateAvc(int isAvc) {
        ContentValues vals = new ContentValues();
        vals.put("bEnableAVC", isAvc == 1);
        try {
            getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/soundsetting"),
                    vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_SoundSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int querySrr() {
        int ret = 0;
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/soundsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            ret = cursor.getInt(cursor.getColumnIndex("Surround"));
        }
        cursor.close();
        return ret;
    }

    @Override
    public void updateSrr(int isSrr) {
        ContentValues vals = new ContentValues();
        vals.put("Surround", isSrr == 1);
        try {
            getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/soundsetting"),
                    vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_SoundSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryPCHPos() {
        int id = 0;
        for (id = 0; id < 10; id++) {
            if ((short) queryPCModeIndex(id) == getVideoInfo().modeIndex) {
                break;
            }
        }
        String str = "content://mstar.tv.usersetting/userpcmodesetting/" + String.valueOf(id);
        Cursor cursor = getContentResolver().query(Uri.parse(str), null, null, null, null);
        int value = -1;
        if (cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex("u16UI_HorizontalStart"));
        }
        cursor.close();
        return value;
    }

    @Override
    public int queryPCVPos() {
        int id = 0;
        for (id = 0; id < 10; id++) {
            if ((short) queryPCModeIndex(id) == getVideoInfo().modeIndex) {
                break;
            }
        }
        String str = "content://mstar.tv.usersetting/userpcmodesetting/" + String.valueOf(id);
        Cursor cursor = getContentResolver().query(Uri.parse(str), null, null, null, null);
        int value = -1;
        if (cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex("u16UI_VorizontalStart"));
        }
        cursor.close();
        return value;
    }

    @Override
    public int queryPCClock() {
        int id = 0;
        for (id = 0; id < 10; id++) {
            if ((short) queryPCModeIndex(id) == getVideoInfo().modeIndex) {
                break;
            }
        }
        String str = "content://mstar.tv.usersetting/userpcmodesetting/" + String.valueOf(id);
        Cursor cursor = getContentResolver().query(Uri.parse(str), null, null, null, null);
        int value = -1;
        if (cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex("u16UI_Clock"));
        }
        cursor.close();
        return value;
    }

    @Override
    public int queryPCModeIndex(int id) {
        String str = "content://mstar.tv.usersetting/userpcmodesetting/" + String.valueOf(id);
        Cursor cursor = getContentResolver().query(Uri.parse(str), null, null, null, null);
        int value = -1;
        if (cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex("u8ModeIndex"));
        }
        cursor.close();
        return value;
    }

    @Override
    public boolean isPCTimingNew() {
        int id = 0;
        for (id = 0; id < 9; id++) {
            if ((short) queryPCModeIndex(id) == getVideoInfo().modeIndex) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int queryPCPhase() {
        int id = 0;
        for (id = 0; id < 10; id++) {
            if ((short) queryPCModeIndex(id) == getVideoInfo().modeIndex) {
                break;
            }
        }
        String str = "content://mstar.tv.usersetting/userpcmodesetting/" + String.valueOf(id);
        Cursor cursor = getContentResolver().query(Uri.parse(str), null, null, null, null);
        int value = -1;
        if (cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex("u16UI_Phase"));
        }
        cursor.close();
        return value;
    }

    @Override
    public int queryCurrentInputSource() {
        int value = 0;
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/systemsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex("enInputSourceType"));
        }
        cursor.close();
        return value;
    }

    @Override
    public void updateCurrentInputSource(int inputSrc) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("enInputSourceType", inputSrc);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/systemsetting"), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_SystemSetting field enInputSourceType ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_SystemSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updatePowerOnSource(int eSour) {
        ContentValues vals = new ContentValues();
        vals.put("enInputSourceType", eSour);
        try {
            getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/systemsetting"),
                    vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_SystemSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryPowerOnSource() {
        int value = 0;
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/systemsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex("enInputSourceType"));
        }
        cursor.close();
        return value;
    }

    @Override
    public void updatePowerOnAVMute(boolean enable) {
        ContentValues vals = new ContentValues();
        vals.put("bPowerOnAVMute", enable ? 1 : 0);
        try {
            getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/systemsetting"),
                    vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_SystemSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean queryPowerOnAVMute() {
        int value = 0;
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/systemsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex("bPowerOnAVMute"));
        }
        cursor.close();
        return value == 0 ? false : true;
    }

    @Override
    public int queryTrueBass() {
        int value = 0;
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/soundsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex("TrueBass"));
        }
        cursor.close();
        return value;
    }

    @Override
    public void updateTrueBass(int value) {
        ContentValues vals = new ContentValues();
        vals.put("TrueBass", value);
        try {
            getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/soundsetting"),
                    vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_SoundSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int querySeparateHearing() {
        int value = 0;
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/soundsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex("SeparateHearing"));
        }
        cursor.close();
        return value;
    }

    @Override
    public void updateSeparateHearing(int value) {
        ContentValues vals = new ContentValues();
        vals.put("SeparateHearing", value);
        try {
            getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/soundsetting"),
                    vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_SoundSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryWallmusic() {
        int value = 0;
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/soundsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex("WallMusic"));
        }
        cursor.close();
        return value;
    }

    @Override
    public void updateWallmusic(int value) {
        ContentValues vals = new ContentValues();
        vals.put("WallMusic", value);
        try {
            getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/soundsetting"),
                    vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_SoundSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryPowerOnMusic() {
        int value = 0;
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/soundsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex("PoweronMusic"));
        }
        cursor.close();
        return value;
    }

    @Override
    public void updatePowerOnMusic(int value) {
        ContentValues vals = new ContentValues();
        vals.put("PoweronMusic", value);
        try {
            getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/soundsetting"),
                    vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_SoundSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryBassSwitch() {
        int value = 0;
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/soundsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex("bEnableHeavyBass"));
        }
        cursor.close();
        return value;
    }

    @Override
    public void updateBassSwitch(int value) {
        ContentValues vals = new ContentValues();
        vals.put("bEnableHeavyBass", value);
        try {
            getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/soundsetting"),
                    vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_SoundSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryBassVolume() {
        int value = 0;
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/soundsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex("HeavyBassVolume"));
        }
        cursor.close();
        return value;
    }

    @Override
    public void updateBassVolume(int value) {
        ContentValues vals = new ContentValues();
        vals.put("HeavyBassVolume", value);
        try {
            getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/soundsetting"),
                    vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_SoundSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryEarPhoneVolme() {
        int vol = 0;
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/soundsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            vol = cursor.getInt(cursor.getColumnIndex("HPVolume"));
        }
        cursor.close();
        return vol;
    }

    @Override
    public void updateEarPhoneVolume(int vol) {
        ContentValues vals = new ContentValues();
        vals.put("HPVolume", vol);
        try {
            getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/soundsetting"),
                    vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(IDatabaseDesk.T_SoundSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PqlCalibrationData queryADCAdjust(int adcIndex) {
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.factory/adcadjust/sourceid/" + adcIndex), null, null,
                null, null);
        PqlCalibrationData model = new PqlCalibrationData();
        if (cursor.moveToFirst()) {
            model.redGain = cursor.getInt(cursor.getColumnIndex("u16RedGain"));
            model.greenGain = cursor.getInt(cursor.getColumnIndex("u16GreenGain"));
            model.blueGain = cursor.getInt(cursor.getColumnIndex("u16BlueGain"));
            model.redOffset = cursor.getInt(cursor.getColumnIndex("u16RedOffset"));
            model.greenOffset = cursor.getInt(cursor.getColumnIndex("u16GreenOffset"));
            model.blueOffset = cursor.getInt(cursor.getColumnIndex("u16BlueOffset"));
        }
        cursor.close();
        return model;
    }

    @Override
    public void updateADCAdjust(PqlCalibrationData model, int sourceId) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("u16RedGain", model.redGain);
        vals.put("u16GreenGain", model.greenGain);
        vals.put("u16BlueGain", model.blueGain);
        vals.put("u16RedOffset", model.redOffset);
        vals.put("u16GreenOffset", model.greenOffset);
        vals.put("u16BlueOffset", model.blueOffset);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.factory/adcadjust/sourceid/" + sourceId), vals,
                    null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_ADCAdjust ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(IDatabaseDesk.T_ADCAdjust_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateNonLinearAdjust(NonLinearAdjustPointSetting dataModel, int curveTypeIndex) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("u8OSD_V0", dataModel.u8OSD_V0);
        vals.put("u8OSD_V25", dataModel.u8OSD_V25);
        vals.put("u8OSD_V50", dataModel.u8OSD_V50);
        vals.put("u8OSD_V75", dataModel.u8OSD_V75);
        vals.put("u8OSD_V100", dataModel.u8OSD_V100);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.factory/nonlinearadjust/inputsrctype/"
                            + queryCurrentInputSource() + "/curvetypeindex/" + curveTypeIndex),
                    vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_NonLinearAdjust ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(IDatabaseDesk.T_NonLinearAdjust_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public FactoryNsVdSet queryNoStandSet() {
        FactoryNsVdSet model = new FactoryNsVdSet();
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.factory/nonstandardadjust"), null, null, null, null);
        if (cursor.moveToFirst()) {
            model.aFEC_D4 = (short) cursor.getInt(cursor.getColumnIndex("u8AFEC_D4"));
            model.aFEC_D5_Bit2 = (short) cursor.getInt(cursor.getColumnIndex("u8AFEC_D5_Bit2"));
            model.aFEC_D8_Bit3210 = (short) cursor.getInt(cursor
                    .getColumnIndex("u8AFEC_D8_Bit3210"));
            model.aFEC_D9_Bit0 = (short) cursor.getInt(cursor.getColumnIndex("u8AFEC_D9_Bit0"));
            model.aFEC_D7_LOW_BOUND = (short) cursor.getInt(cursor
                    .getColumnIndex("u8AFEC_D7_LOW_BOUND"));
            model.aFEC_D7_HIGH_BOUND = (short) cursor.getInt(cursor
                    .getColumnIndex("u8AFEC_D7_HIGH_BOUND"));
            model.aFEC_A0 = (short) cursor.getInt(cursor.getColumnIndex("u8AFEC_A0"));
            model.aFEC_A1 = (short) cursor.getInt(cursor.getColumnIndex("u8AFEC_A1"));
            model.aFEC_66_Bit76 = (short) cursor.getInt(cursor.getColumnIndex("u8AFEC_66_Bit76"));
            model.aFEC_6E_Bit7654 = (short) cursor.getInt(cursor
                    .getColumnIndex("u8AFEC_6E_Bit7654"));
            model.aFEC_6E_Bit3210 = (short) cursor.getInt(cursor
                    .getColumnIndex("u8AFEC_6E_Bit3210"));
            model.aFEC_43 = (short) cursor.getInt(cursor.getColumnIndex("u8AFEC_43"));
            model.aFEC_44 = (short) cursor.getInt(cursor.getColumnIndex("u8AFEC_44"));
            model.aFEC_CB = (short) cursor.getInt(cursor.getColumnIndex("u8AFEC_CB"));
            model.aFEC_CF_Bit2_ATV = (short) cursor.getInt(cursor
                    .getColumnIndex("u8AFEC_CF_Bit2_ATV"));
            model.aFEC_CF_Bit2_AV = (short) cursor.getInt(cursor
                    .getColumnIndex("u8AFEC_CF_Bit2_AV"));
        }
        cursor.close();
        return model;
    }

    @Override
    public NonStandardVifSetting queryNoStandVifSet() {
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.factory/nonstandardadjust"), null, null, null, null);
        NonStandardVifSetting model = new NonStandardVifSetting();
        if (cursor.moveToFirst()) {
            model.vifTop = (short) cursor.getInt(cursor.getColumnIndex("VifTop"));
            model.vifVgaMaximum = cursor.getInt(cursor.getColumnIndex("VifVgaMaximum"));
            model.vifCrKp = (short) cursor.getInt(cursor.getColumnIndex("VifCrKp"));
            model.vifCrKi = (short) cursor.getInt(cursor.getColumnIndex("VifCrKi"));
            model.vifCrKp1 = (short) cursor.getInt(cursor.getColumnIndex("VifCrKp1"));
            model.vifCrKi1 = (short) cursor.getInt(cursor.getColumnIndex("VifCrKi1"));
            model.vifCrKp2 = (short) cursor.getInt(cursor.getColumnIndex("VifCrKp2"));
            model.vifCrKi2 = (short) cursor.getInt(cursor.getColumnIndex("VifCrKi2"));
            model.vifAsiaSignalOption = cursor.getInt(cursor.getColumnIndex("VifAsiaSignalOption")) == 0 ? false
                    : true;
            model.vifCrKpKiAdjust = (short) cursor.getInt(cursor.getColumnIndex("VifCrKpKiAdjust")) == 0 ? false
                    : true;
            model.vifOverModulation = cursor.getInt(cursor.getColumnIndex("VifOverModulation")) == 0 ? false
                    : true;
            model.vifClampgainGainOvNegative = cursor.getInt(cursor
                    .getColumnIndex("VifClampgainGainOvNegative"));
            model.chinaDescramblerBox = (short) cursor.getInt(cursor
                    .getColumnIndex("ChinaDescramblerBox"));
            model.chinaDescramblerBoxDelay = cursor.getInt(cursor
                    .getColumnIndex("ChinaDescramblerBoxDelay"));
            model.vifDelayReduce = (short) cursor.getInt(cursor.getColumnIndex("VifDelayReduce"));
            model.vifCrThr = (short) cursor.getInt(cursor.getColumnIndex("VifCrThr"));
            model.vifVersion = (short) cursor.getInt(cursor.getColumnIndex("VifVersion"));
            model.vifACIAGCREF = (short) cursor.getInt(cursor.getColumnIndex("VifACIAGCREF"));
            model.vifAgcRefNegative = (short) cursor.getInt(cursor
                    .getColumnIndex("VifAgcRefNegative"));
            model.gainDistributionThr = cursor.getInt(cursor.getColumnIndex("GainDistributionThr"));
        }
        cursor.close();
        return model;
    }

    @Override
    public void updateNonStandardAdjust(FactoryNsVdSet model) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("u8AFEC_D4", model.aFEC_D4);
        vals.put("u8AFEC_D5_Bit2", model.aFEC_D5_Bit2);
        vals.put("u8AFEC_D8_Bit3210", model.aFEC_6E_Bit3210);
        vals.put("u8AFEC_D9_Bit0", model.aFEC_D9_Bit0);
        vals.put("u8AFEC_D7_LOW_BOUND", model.aFEC_D7_LOW_BOUND);
        vals.put("u8AFEC_D7_HIGH_BOUND", model.aFEC_D7_HIGH_BOUND);
        vals.put("u8AFEC_A0", model.aFEC_A0);
        vals.put("u8AFEC_A1", model.aFEC_A1);
        vals.put("u8AFEC_66_Bit76", model.aFEC_66_Bit76);
        vals.put("u8AFEC_6E_Bit7654", model.aFEC_6E_Bit7654);
        vals.put("u8AFEC_6E_Bit3210", model.aFEC_6E_Bit3210);
        vals.put("u8AFEC_43", model.aFEC_43);
        vals.put("u8AFEC_44", model.aFEC_44);
        vals.put("u8AFEC_CB", model.aFEC_CB);
        vals.put("u8AFEC_CF_Bit2_ATV", model.aFEC_CF_Bit2_ATV);
        vals.put("u8AFEC_CF_Bit2_AV", model.aFEC_CF_Bit2_AV);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.factory/nonstandardadjust"), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_NonLinearAdjust AFEC ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(IDatabaseDesk.T_NonStarndardAdjust_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateNonStandardAdjust(NonStandardVifSetting vifSet) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("VifTop", vifSet.vifTop);
        vals.put("VifVgaMaximum", vifSet.vifVgaMaximum);
        vals.put("VifCrKp", vifSet.vifCrKp);
        vals.put("VifCrKi", vifSet.vifCrKi);
        vals.put("VifCrKp1", vifSet.vifCrKp1);
        vals.put("VifCrKi1", vifSet.vifCrKi1);
        vals.put("VifCrKp2", vifSet.vifCrKp2);
        vals.put("VifCrKi2", vifSet.vifCrKi2);
        vals.put("VifAsiaSignalOption", vifSet.vifAsiaSignalOption ? 1 : 0);
        vals.put("VifCrKpKiAdjust", vifSet.vifCrKpKiAdjust ? 1 : 0);
        vals.put("VifOverModulation", vifSet.vifOverModulation ? 1 : 0);
        vals.put("VifClampgainGainOvNegative", vifSet.vifClampgainGainOvNegative);
        vals.put("ChinaDescramblerBox", vifSet.chinaDescramblerBox);
        vals.put("ChinaDescramblerBoxDelay", vifSet.chinaDescramblerBoxDelay);
        vals.put("VifDelayReduce", vifSet.vifDelayReduce);
        vals.put("VifCrThr", vifSet.vifCrThr);
        vals.put("VifVersion", vifSet.vifVersion);
        vals.put("VifACIAGCREF", vifSet.vifACIAGCREF);
        vals.put("VifAgcRefNegative", vifSet.vifAgcRefNegative);
        vals.put("GainDistributionThr", vifSet.gainDistributionThr);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.factory/nonstandardadjust"), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_NonStandardAdjust Vif ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(IDatabaseDesk.T_NonStarndardAdjust_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ExternSetting queryFactoryExtern() {
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.factory/factoryextern"), null, null, null, null);
        ExternSetting model = new ExternSetting();
        if (cursor.moveToFirst()) {
            model.softVersion = cursor.getString(cursor.getColumnIndex("SoftWareVersion"));
            model.boardType = cursor.getString(cursor.getColumnIndex("BoardType"));
            model.panelType = cursor.getString(cursor.getColumnIndex("PanelType"));
            model.dayAndTime = cursor.getString(cursor.getColumnIndex("CompileTime"));
            model.eTestPatternMode = EnumTestPatternMode.values()[cursor.getInt(cursor
                    .getColumnIndex("TestPatternMode"))];
            model.ePowerOnMode = EnumFactoryPowerOnMode.values()[cursor.getInt(cursor
                    .getColumnIndex("stPowerMode"))];
            model.isDtvAvAbnormalDelay = cursor.getInt(cursor.getColumnIndex("DtvAvAbnormalDelay")) == 0 ? false
                    : true;
            model.eFactoryPreset = EnumFactoryPreSet.values()[cursor.getInt(cursor
                    .getColumnIndex("FactoryPreSetFeature"))];
            model.panelSwingVal = (short) cursor.getInt(cursor.getColumnIndex("PanelSwing"));
            model.audioPreScale = (short) cursor.getInt(cursor.getColumnIndex("AudioPrescale"));
            model.vdDspVersion = (short) cursor.getInt(cursor.getColumnIndex("vdDspVersion"));
            model.eHidevMode = EnumSoundHidevMode.values()[cursor.getInt(cursor
                    .getColumnIndex("eHidevMode"))];
            model.audioNrThr = (short) cursor.getInt(cursor.getColumnIndex("audioNrThr"));
            model.audioSifThreshold = (short) cursor.getInt(cursor
                    .getColumnIndex("audioSifThreshold"));
            model.audioDspVersion = (short) cursor.getInt(cursor.getColumnIndex("audioDspVersion"));
            try {
                model.isNoSignalAutoShutdownEnable = cursor.getInt(
                        cursor.getColumnIndexOrThrow("NoSignalAutoShutdown")) == 0 ? false : true;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                model.isNoSignalAutoShutdownEnable = true;
            }
        }
        cursor.close();
        return model;
    }

    @Override
    public void updateFactoryExtern(ExternSetting model) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("SoftWareVersion", model.softVersion);
        vals.put("BoardType", model.boardType);
        vals.put("PanelType", model.panelType);
        vals.put("CompileTime", model.dayAndTime);
        vals.put("TestPatternMode", model.eTestPatternMode.ordinal());
        vals.put("stPowerMode", model.ePowerOnMode.ordinal());
        vals.put("DtvAvAbnormalDelay", model.isDtvAvAbnormalDelay ? 1 : 0);
        vals.put("FactoryPreSetFeature", model.eFactoryPreset.ordinal());
        vals.put("PanelSwing", model.panelSwingVal);
        vals.put("AudioPrescale", model.audioPreScale);
        vals.put("vdDspVersion", model.vdDspVersion);
        vals.put("eHidevMode", model.eHidevMode.ordinal());
        vals.put("audioNrThr", model.audioNrThr);
        vals.put("audioSifThreshold", model.audioSifThreshold);
        vals.put("audioDspVersion", model.audioDspVersion);
        vals.put("NoSignalAutoShutdown", model.isNoSignalAutoShutdownEnable ? 1 : 0);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.factory/factoryextern"), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_FactoryExtern ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(IDatabaseDesk.T_FactoryExtern_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryAudioPrescale() {
        String audioPrescale = new String(
                "0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0, 0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0, 0x0,0x0,0x0,0x0,0x0,0x0,");
        int value = 0;
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/soundsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            audioPrescale = cursor.getString(cursor.getColumnIndex("SpeakerPreScale"));
            String[] array = audioPrescale.split(",");
            int iSource = queryCurrentInputSource();
            String str = array[iSource];
            String substr = str.substring(2);
            int prevalue = Integer.parseInt(substr);
            value = prevalue / 10 * 16 + prevalue % 10;
        }
        cursor.close();
        return value;
    }

    @Override
    public VideoWindowInfo[][] queryOverscanAdjusts(int overScanType) {
        VideoWindowInfo[][] model;
        switch (overScanType) {
            case 0:
                Cursor cursor = getContentResolver().query(
                        Uri.parse("content://mstar.tv.factory/dtvoverscansetting"), null, null,
                        null, "ResolutionTypeNum");
                int maxDTV1 = EnumMaxDtvResolutionInfo.E_MAX.ordinal();
                int maxDTV2 = EnumAspectRatioType.E_MAX.ordinal();
                model = new VideoWindowInfo[maxDTV1][maxDTV2];
                for (int i = 0; i < maxDTV1; i++) {
                    for (int j = 0; j < maxDTV2; j++) {
                        VideoWindowInfo item = new VideoWindowInfo();
                        if (cursor.moveToNext()) {
                            item.hCapStart = cursor.getInt(cursor.getColumnIndex("u16H_CapStart"));
                            item.vCapStart = cursor.getInt(cursor.getColumnIndex("u16V_CapStart"));
                            item.hCropLeft = (short) cursor.getInt(cursor
                                    .getColumnIndex("u8HCrop_Left"));
                            item.hCropRight = (short) cursor.getInt(cursor
                                    .getColumnIndex("u8HCrop_Right"));
                            item.vCropUp = (short) cursor.getInt(cursor
                                    .getColumnIndex("u8VCrop_Up"));
                            item.vCropDown = (short) cursor.getInt(cursor
                                    .getColumnIndex("u8VCrop_Down"));
                        }
                        model[i][j] = item;
                    }
                }
                cursor.close();
                break;
            case 1:
                Cursor cursor1 = getContentResolver().query(
                        Uri.parse("content://mstar.tv.factory/hdmioverscansetting"), null, null,
                        null, "ResolutionTypeNum");
                int maxHDMI1 = EnumHdmiResolutionInfo.E_NUM.ordinal();
                int maxHDMI2 = EnumAspectRatioType.E_MAX.ordinal();
                model = new VideoWindowInfo[maxHDMI1][maxHDMI2];
                for (int i = 0; i < maxHDMI1; i++) {
                    for (int j = 0; j < maxHDMI2; j++) {
                        VideoWindowInfo item = new VideoWindowInfo();
                        if (cursor1.moveToNext()) {
                            item.hCapStart = cursor1
                                    .getInt(cursor1.getColumnIndex("u16H_CapStart"));
                            item.vCapStart = cursor1
                                    .getInt(cursor1.getColumnIndex("u16V_CapStart"));
                            item.hCropLeft = (short) cursor1.getInt(cursor1
                                    .getColumnIndex("u8HCrop_Left"));
                            item.hCropRight = (short) cursor1.getInt(cursor1
                                    .getColumnIndex("u8HCrop_Right"));
                            item.vCropUp = (short) cursor1.getInt(cursor1
                                    .getColumnIndex("u8VCrop_Up"));
                            item.vCropDown = (short) cursor1.getInt(cursor1
                                    .getColumnIndex("u8VCrop_Down"));
                        }
                        model[i][j] = item;
                    }
                }
                cursor1.close();
                break;
            case 2:
                Cursor cursor2 = getContentResolver().query(
                        Uri.parse("content://mstar.tv.factory/ypbproverscansetting"), null, null,
                        null, "ResolutionTypeNum");
                int maxYPbPr1 = EnumYpbprResolutionInfo.E_NUM.ordinal();
                int maxYPbPr2 = EnumAspectRatioType.E_MAX.ordinal();
                model = new VideoWindowInfo[maxYPbPr1][maxYPbPr2];
                for (int i = 0; i < maxYPbPr1; i++) {
                    for (int j = 0; j < maxYPbPr2; j++) {
                        VideoWindowInfo item = new VideoWindowInfo();
                        if (cursor2.moveToNext()) {
                            item.hCapStart = cursor2
                                    .getInt(cursor2.getColumnIndex("u16H_CapStart"));
                            item.vCapStart = cursor2
                                    .getInt(cursor2.getColumnIndex("u16V_CapStart"));
                            item.hCropLeft = (short) cursor2.getInt(cursor2
                                    .getColumnIndex("u8HCrop_Left"));
                            item.hCropRight = (short) cursor2.getInt(cursor2
                                    .getColumnIndex("u8HCrop_Right"));
                            item.vCropUp = (short) cursor2.getInt(cursor2
                                    .getColumnIndex("u8VCrop_Up"));
                            item.vCropDown = (short) cursor2.getInt(cursor2
                                    .getColumnIndex("u8VCrop_Down"));
                        }
                        model[i][j] = item;
                    }
                }
                cursor2.close();
                break;
            case 3:
                Cursor cursor3 = getContentResolver().query(
                        Uri.parse("content://mstar.tv.factory/overscanadjust"), null, null, null,
                        "FactoryOverScanType");
                int maxVD1 = EnumVdSignalType.E_NUM.ordinal();
                int maxVD2 = EnumAspectRatioType.E_MAX.ordinal();
                model = new VideoWindowInfo[maxVD1][maxVD2];
                for (int i = 0; i < maxVD1; i++) {
                    for (int j = 0; j < maxVD2; j++) {
                        VideoWindowInfo item = new VideoWindowInfo();
                        if (cursor3.moveToNext()) {
                            item.hCapStart = cursor3
                                    .getInt(cursor3.getColumnIndex("u16H_CapStart"));
                            item.vCapStart = cursor3
                                    .getInt(cursor3.getColumnIndex("u16V_CapStart"));
                            item.hCropLeft = (short) cursor3.getInt(cursor3
                                    .getColumnIndex("u8HCrop_Left"));
                            item.hCropRight = (short) cursor3.getInt(cursor3
                                    .getColumnIndex("u8HCrop_Right"));
                            item.vCropUp = (short) cursor3.getInt(cursor3
                                    .getColumnIndex("u8VCrop_Up"));
                            item.vCropDown = (short) cursor3.getInt(cursor3
                                    .getColumnIndex("u8VCrop_Down"));
                        }
                        model[i][j] = item;
                    }
                }
                cursor3.close();
                break;
            case 4:
                Cursor cursor4 = getContentResolver().query(
                        Uri.parse("content://mstar.tv.factory/overscanadjust"), null, null, null,
                        "FactoryOverScanType");
                int maxVD3 = EnumVdSignalType.E_NUM.ordinal();
                int maxVD4 = EnumAspectRatioType.E_MAX.ordinal();
                model = new VideoWindowInfo[maxVD3][maxVD4];
                for (int i = 0; i < maxVD3; i++) {
                    for (int j = 0; j < maxVD4; j++) {
                        VideoWindowInfo item = new VideoWindowInfo();
                        if (cursor4.moveToNext()) {
                            item.hCapStart = cursor4
                                    .getInt(cursor4.getColumnIndex("u16H_CapStart"));
                            item.vCapStart = cursor4
                                    .getInt(cursor4.getColumnIndex("u16V_CapStart"));
                            item.hCropLeft = (short) cursor4.getInt(cursor4
                                    .getColumnIndex("u8HCrop_Left"));
                            item.hCropRight = (short) cursor4.getInt(cursor4
                                    .getColumnIndex("u8HCrop_Right"));
                            item.vCropUp = (short) cursor4.getInt(cursor4
                                    .getColumnIndex("u8VCrop_Up"));
                            item.vCropDown = (short) cursor4.getInt(cursor4
                                    .getColumnIndex("u8VCrop_Down"));
                        }
                        model[i][j] = item;
                    }
                }
                cursor4.close();
                break;
            default:
                return null;
        }
        return model;
    }

    @Override
    public void updateOverscanAdjust(int overScanType, int arcMode, VideoWindowInfo[][] model) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("u16H_CapStart", model[overScanType][arcMode].hCapStart);
        vals.put("u16V_CapStart", model[overScanType][arcMode].vCapStart);
        vals.put("u8HCrop_Left", model[overScanType][arcMode].hCropLeft);
        vals.put("u8HCrop_Right", model[overScanType][arcMode].hCropRight);
        vals.put("u8VCrop_Up", model[overScanType][arcMode].vCropUp);
        vals.put("u8VCrop_Down", model[overScanType][arcMode].vCropDown);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.factory/overscanadjust/factoryoverscantype/"
                            + overScanType + "/_id/" + arcMode), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_OverscanAdjust ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(IDatabaseDesk.T_OverscanAdjust_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateYPbPrOverscanAdjust(int overScanType, int arcMode, VideoWindowInfo[][] model) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("u16H_CapStart", model[overScanType][arcMode].hCapStart);
        vals.put("u16V_CapStart", model[overScanType][arcMode].vCapStart);
        vals.put("u8HCrop_Left", model[overScanType][arcMode].hCropLeft);
        vals.put("u8HCrop_Right", model[overScanType][arcMode].hCropRight);
        vals.put("u8VCrop_Up", model[overScanType][arcMode].vCropUp);
        vals.put("u8VCrop_Down", model[overScanType][arcMode].vCropDown);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.factory/ypbproverscansetting/resolutiontypenum/"
                            + overScanType + "/_id/" + arcMode), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_YPbPrOverscanSetting ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(IDatabaseDesk.T_YPbPrOverscanSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateHDMIOverscanAdjust(int overScanType, int arcMode, VideoWindowInfo[][] model) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("u16H_CapStart", model[overScanType][arcMode].hCapStart);
        vals.put("u16V_CapStart", model[overScanType][arcMode].vCapStart);
        vals.put("u8HCrop_Left", model[overScanType][arcMode].hCropLeft);
        vals.put("u8HCrop_Right", model[overScanType][arcMode].hCropRight);
        vals.put("u8VCrop_Up", model[overScanType][arcMode].vCropUp);
        vals.put("u8VCrop_Down", model[overScanType][arcMode].vCropDown);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.factory/hdmioverscansetting/resolutiontypenum/"
                            + overScanType + "/_id/" + arcMode), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_HDMIOverscanSetting ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(IDatabaseDesk.T_HDMIOverscanSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateDTVOverscanAdjust(int FactoryOverScanType, int arcMode,
            VideoWindowInfo[][] model) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("u16H_CapStart", model[FactoryOverScanType][arcMode].hCapStart);
        vals.put("u16V_CapStart", model[FactoryOverScanType][arcMode].vCapStart);
        vals.put("u8HCrop_Left", model[FactoryOverScanType][arcMode].hCropLeft);
        vals.put("u8HCrop_Right", model[FactoryOverScanType][arcMode].hCropRight);
        vals.put("u8VCrop_Up", model[FactoryOverScanType][arcMode].vCropUp);
        vals.put("u8VCrop_Down", model[FactoryOverScanType][arcMode].vCropDown);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.factory/dtvoverscansetting/resolutiontypenum/"
                            + FactoryOverScanType + "/_id/" + arcMode), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_DTVOverscanSetting ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(IDatabaseDesk.T_DTVOverscanSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateATVOverscanAdjust(int FactoryOverScanType, int arcMode,
            VideoWindowInfo[][] model) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("u16H_CapStart", model[FactoryOverScanType][arcMode].hCapStart);
        vals.put("u16V_CapStart", model[FactoryOverScanType][arcMode].vCapStart);
        vals.put("u8HCrop_Left", model[FactoryOverScanType][arcMode].hCropLeft);
        vals.put("u8HCrop_Right", model[FactoryOverScanType][arcMode].hCropRight);
        vals.put("u8VCrop_Up", model[FactoryOverScanType][arcMode].vCropUp);
        vals.put("u8VCrop_Down", model[FactoryOverScanType][arcMode].vCropDown);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.factory/overscanadjust/factoryoverscantype/"
                            + FactoryOverScanType + "/_id/" + arcMode), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_ATVOverscanSetting ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(IDatabaseDesk.T_OverscanAdjust_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PeqSetting queryPEQAdjusts() {
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.factory/peqadjust"), null, null, null, null);
        PeqSetting model = new PeqSetting();
        int i = 0;
        int length = model.peqParams.length;
        while (cursor.moveToNext()) {
            if (i > length - 1) {
                break;
            }
            model.peqParams[i].band = cursor.getInt(cursor.getColumnIndex("Band"));
            model.peqParams[i].gain = cursor.getInt(cursor.getColumnIndex("Gain"));
            model.peqParams[i].foh = cursor.getInt(cursor.getColumnIndex("Foh"));
            model.peqParams[i].fol = cursor.getInt(cursor.getColumnIndex("Fol"));
            model.peqParams[i].qValue = cursor.getInt(cursor.getColumnIndex("QValue"));
            i++;
        }
        cursor.close();
        return model;
    }

    @Override
    public void updatePEQAdjust(PeqParameter model, int index) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("Band", model.band);
        vals.put("Gain", model.gain);
        vals.put("Foh", model.foh);
        vals.put("Fol", model.fol);
        vals.put("QValue", model.qValue);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.factory/peqadjust/" + index), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_PEQAdjust ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(IDatabaseDesk.T_PEQAdjust_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public FactorySscSetting querySSCAdjust() {
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.factory/sscadjust"), null, null, null, null);
        FactorySscSetting model = new FactorySscSetting();
        if (cursor.moveToFirst()) {
            model.isMiuSscEnable = cursor.getInt(cursor.getColumnIndex("Miu_SscEnable")) == 0 ? false
                    : true;
            model.isLvdsSscEnable = cursor.getInt(cursor.getColumnIndex("Lvds_SscEnable")) == 0 ? false
                    : true;
            model.lvdsSscSpan = cursor.getInt(cursor.getColumnIndex("Lvds_SscSpan"));
            model.lvdsSscStep = cursor.getInt(cursor.getColumnIndex("Lvds_SscStep"));
            model.miu0SscSpan = cursor.getInt(cursor.getColumnIndex("Miu_SscSpan"));
            model.miu0SscStep = cursor.getInt(cursor.getColumnIndex("Miu_SscStep"));
            model.miu1SscSpan = cursor.getInt(cursor.getColumnIndex("Miu1_SscSpan"));
            model.miu1SscStep = cursor.getInt(cursor.getColumnIndex("Miu1_SscStep"));
            model.miu2SscSpan = cursor.getInt(cursor.getColumnIndex("Miu2_SscSpan"));
            model.miu2SscStep = cursor.getInt(cursor.getColumnIndex("Miu2_SscStep"));
        }
        cursor.close();
        return model;
    }

    @Override
    public void updateSSCAdjust(FactorySscSetting model) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("Miu_SscEnable", model.isMiuSscEnable ? 1 : 0);
        vals.put("Lvds_SscEnable", model.isLvdsSscEnable ? 1 : 0);
        vals.put("Lvds_SscSpan", model.lvdsSscSpan);
        vals.put("Lvds_SscStep", model.lvdsSscStep);
        vals.put("Miu_SscSpan", model.miu0SscSpan);
        vals.put("Miu_SscStep", model.miu0SscStep);
        vals.put("Miu1_SscSpan", model.miu1SscSpan);
        vals.put("Miu1_SscStep", model.miu1SscStep);
        vals.put("Miu2_SscSpan", model.miu2SscSpan);
        vals.put("Miu2_SscStep", model.miu2SscStep);
        try {
            ret = getContentResolver().update(Uri.parse("content://mstar.tv.factory/sscadjust"),
                    vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_SSCAdjust ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(IDatabaseDesk.T_SSCAdjust_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryIsPcMode(int inputSrcType) {
        int ret = -1;
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/videosetting/inputsrc/" + inputSrcType),
                null, null, null, null);
        if (cursor.moveToFirst()) {
            ret = cursor.getInt(cursor.getColumnIndex("bIsPcMode"));
        }
        cursor.close();
        return ret;
    }

    @Override
    public void updateMonitorITC(int curStatue) {
        int ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("bMonitorITC", curStatue);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/systemsetting"), vals, null, null);
        } catch (SQLException e) {
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_SystemSetting ignored");
        }
        try {
            if (TvManager.getInstance() != null) {
                TvManager.getInstance().getDatabaseManager()
                        .setDatabaseDirtyByApplication(T_SystemSetting_IDX);
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryMonitorITC() {
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/systemsetting"), null, null, null, null);
        if (cursor == null) {
            return -1;
        }
        int result = 0;
        try {
            if (cursor.moveToFirst()) {
                result = cursor.getInt(cursor.getColumnIndex("bMonitorITC"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();
        return result;
    }

    @Override
    public void updatexvYCCEnable(boolean xvYCCEn) {
        int ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("bxvYCCOnOff", xvYCCEn);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/systemsetting"), vals, null, null);
        } catch (SQLException e) {
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_SystemSetting ignored");
        }
        try {
            if (TvManager.getInstance() != null) {
                TvManager.getInstance().getDatabaseManager()
                        .setDatabaseDirtyByApplication(T_SystemSetting_IDX);
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean queryxvYCCEnable() {
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/systemsetting"), null, null, null, null);
        int result = 0;
        try {
            if (cursor.moveToFirst()) {
                result = cursor.getInt(cursor.getColumnIndex("bxvYCCOnOff"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();
        return result == 0 ? false : true;
    }

    @Override
    public int queryePicMode(int inputSrcType) {
        Cursor cursorVideo = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/videosetting/inputsrc/" + inputSrcType),
                null, null, null, null);
        int value = -1;
        if (cursorVideo.moveToFirst()) {
            value = cursorVideo.getInt(cursorVideo.getColumnIndex("ePicture"));
        }
        cursorVideo.close();
        return value;
    }

    @Override
    public int queryColorTmpIdx(int inputSrcType, int ePicture) {
        Cursor cursorPicMode = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/picmode_setting/inputsrc/" + inputSrcType
                        + "/picmode/" + ePicture), null, null, null, null);
        int value = -1;
        if (cursorPicMode.moveToFirst()) {
            value = cursorPicMode.getInt(cursorPicMode.getColumnIndex("eColorTemp"));
        }
        cursorPicMode.close();
        return value;
    }

    @Override
    public ColorTempModeSetting queryFactoryColorTempExData() {
        ColorTempModeSetting model = new ColorTempModeSetting();
        for (int sourceIdx = 0; sourceIdx < EnumColorTempExInputSource.E_NUM.ordinal(); sourceIdx++) {
            Cursor cursor = getContentResolver().query(
                    Uri.parse("content://mstar.tv.factory/factorycolortempex"), null,
                    "InputSourceID = " + sourceIdx, null, "ColorTemperatureID");

            for (int colorTmpIdx = 0; colorTmpIdx < EnumColorTemperature_.E_NUM.ordinal(); colorTmpIdx++) {
                if (cursor.moveToNext()) {
                    model.colorTempExData[colorTmpIdx][sourceIdx].redGain = cursor.getInt(cursor
                            .getColumnIndex("u16RedGain"));
                    model.colorTempExData[colorTmpIdx][sourceIdx].greenGain = cursor.getInt(cursor
                            .getColumnIndex("u16GreenGain"));
                    model.colorTempExData[colorTmpIdx][sourceIdx].blueGain = cursor.getInt(cursor
                            .getColumnIndex("u16BlueGain"));
                    model.colorTempExData[colorTmpIdx][sourceIdx].redOffset = cursor.getInt(cursor
                            .getColumnIndex("u16RedOffset"));
                    model.colorTempExData[colorTmpIdx][sourceIdx].greenOffset = cursor
                            .getInt(cursor.getColumnIndex("u16GreenOffset"));
                    model.colorTempExData[colorTmpIdx][sourceIdx].blueOffset = cursor.getInt(cursor
                            .getColumnIndex("u16BlueOffset"));
                }
            }
            cursor.close();
        }
        return model;
    }

    @Override
    public ColorTemperature queryFactoryColorTempExData(int inputsource, int colorTemperatureID) {
        ColorTemperature vo = new ColorTemperature();
        Cursor cursor = sContext.getContentResolver().query(
                Uri.parse("content://mstar.tv.factory/factorycolortempex"),

                null,
                "InputSourceID = " + inputsource + " AND ColorTemperatureID = "
                        + colorTemperatureID, null, null);
        cursor.moveToFirst();
        vo.redGain = (short) cursor.getInt(cursor.getColumnIndex("u16RedGain"));
        vo.greenGain = (short) cursor.getInt(cursor.getColumnIndex("u16GreenGain"));
        vo.buleGain = (short) cursor.getInt(cursor.getColumnIndex("u16BlueGain"));
        vo.redOffset = (short) cursor.getInt(cursor.getColumnIndex("u16RedOffset"));
        vo.greenOffset = (short) cursor.getInt(cursor.getColumnIndex("u16GreenOffset"));
        vo.blueOffset = (short) cursor.getInt(cursor.getColumnIndex("u16BlueOffset"));
        cursor.close();
        return vo;
    }

    @Override
    public ColorTemperatureExData queryFactoryColorTemperatureExData(int inputsource, int colorTemperatureID) {
        ColorTemperatureExData vo = new ColorTemperatureExData();
        Cursor cursor = sContext.getContentResolver().query(
                Uri.parse("content://mstar.tv.factory/factorycolortempex"),
                null,
                "InputSourceID = " + inputsource + " AND ColorTemperatureID = "
                + colorTemperatureID, null, null);
        cursor.moveToFirst();
        vo.redGain = cursor.getInt(cursor.getColumnIndex("u16RedGain"));
        vo.greenGain = cursor.getInt(cursor.getColumnIndex("u16GreenGain"));
        vo.blueGain = cursor.getInt(cursor.getColumnIndex("u16BlueGain"));
        vo.redOffset = cursor.getInt(cursor.getColumnIndex("u16RedOffset"));
        vo.greenOffset = cursor.getInt(cursor.getColumnIndex("u16GreenOffset"));
        vo.blueOffset = cursor.getInt(cursor.getColumnIndex("u16BlueOffset"));
        cursor.close();
        return vo;
    }

    @Override
    public void updateFactoryColorTempExData(ColorTempExData model, int sourceId, int colorTmpId) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("u16RedGain", model.redGain);
        vals.put("u16GreenGain", model.greenGain);
        vals.put("u16BlueGain", model.blueGain);
        vals.put("u16RedOffset", model.redOffset);
        vals.put("u16GreenOffset", model.greenOffset);
        vals.put("u16BlueOffset", model.blueOffset);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.factory/factorycolortempex/inputsourceid/"
                            + sourceId + "/colortemperatureid/" + colorTmpId), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_FactoryColorTempEx ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(IDatabaseDesk.T_FacrotyColorTempEx_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryUserSysSetting(EnumUserSettingField field) {
        String fieldStr = "";
        switch (field) {
            case E_ENABLE_WDT:
                fieldStr = "bEnableWDT";
                break;
            case E_UART_USB:
                fieldStr = "bUartBus";
                break;
            default:
                return -1;
        }
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/systemsetting"), null, null, null, null);
        int value = -1;
        if (cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex(fieldStr));
        }
        cursor.close();
        return value;
    }

    @Override
    public void updateUserSysSetting(EnumUserSettingField field, int value) {
        long ret = -1;
        String fieldStr = "";
        switch (field) {
            case E_ENABLE_WDT:
                fieldStr = "bEnableWDT";
                break;
            case E_UART_USB:
                fieldStr = "bUartBus";
                break;
            default:
                return;
        }
        ContentValues vals = new ContentValues();
        vals.put(fieldStr, value);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/systemsetting"), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_SystemSetting field bEnableWDT or bUartBus ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(IDatabaseDesk.T_SystemSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryIsdbUserSettingATVCCMode() {
        int ccMode = 0;
        Cursor cursorCcMode = getContentResolver()
                .query(Uri.parse("content://mstar.tv.usersetting/isdbusersetting"), null, null, null, null);
        if (cursorCcMode.moveToFirst()) {
            ccMode = cursorCcMode.getInt(cursorCcMode.getColumnIndex("u8ATVCCMode"));
        }
        cursorCcMode.close();
        return ccMode;
    }

    @Override
    public void updateIsdbUserSettingATVCCMode(int ccMode) {
        ContentValues vals = new ContentValues();
        vals.put("u8ATVCCMode", ccMode);

        try {
            getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/isdbusersetting"), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(IDatabaseDesk.T_IsdbUserSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryIsdbUserSettingDTVCCMode() {
        int ccMode = 0;
        Cursor cursorCcMode = getContentResolver()
                .query(Uri.parse("content://mstar.tv.usersetting/isdbusersetting"), null, null, null, null);
        if (cursorCcMode.moveToFirst()) {
            ccMode = cursorCcMode.getInt(cursorCcMode.getColumnIndex("u8DTVCCMode"));
        }
        cursorCcMode.close();
        return ccMode;
    }

    @Override
    public void updateIsdbUserSettingDTVCCMode(int ccMode) {
        ContentValues vals = new ContentValues();
        vals.put("u8DTVCCMode", ccMode);

        try {
            getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/isdbusersetting"), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(IDatabaseDesk.T_IsdbUserSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateCiCamPinCode(int camPinCode) {
        ContentValues vals = new ContentValues();
        vals.put("u16CiPinCode", camPinCode);
        long ret = -1;

        try {
            ret = getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/systemsetting"), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.i(TAG, "update tbl_SystemSetting u16CiPinCode ignored");
            return;
        }
        try {
            if (TvManager.getInstance() != null) {
                TvManager.getInstance().getDatabaseManager()
                        .setDatabaseDirtyByApplication(T_SystemSetting_IDX);
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryCiCamPinCode() {
        int ciCamPinCode = 0;
        try {
            Cursor cursor = getContentResolver()
                    .query(Uri.parse("content://mstar.tv.usersetting/systemsetting"), null, null, null, null);
            if (cursor.moveToFirst()) {
                ciCamPinCode = cursor.getInt(cursor.getColumnIndex("u16CiPinCode"));
                Log.d(TAG, "get from db ciCamPinCode"+ciCamPinCode);
            }
            cursor.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ciCamPinCode;
    }

    @Override
    public void updateSelfAdaptiveLevel(int value, int inputSrcType) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("eThreeDVideoSelfAdaptiveLevel", value);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/threedvideomode/inputsrc/"
                            + inputSrcType), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            System.out.println("updateSelfAdaptiveLevel fail");
        }

        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_ThreeDVideoMode_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int querySelfAdaptiveLevel(int inputSrcType) {
        int value = 0;
        Cursor cursor3DMode = getContentResolver()
                .query(Uri.parse("content://mstar.tv.usersetting/threedvideomode/inputsrc/"
                        + inputSrcType), null, null, null, null);
        if (cursor3DMode.moveToFirst()) {
            value = cursor3DMode.getInt(cursor3DMode
                    .getColumnIndex("eThreeDVideoSelfAdaptiveLevel"));
        }
        cursor3DMode.close();
        return value;
    }

    @Override
    public EnumChannelSwitchMode queryChannelSwitchMode() {
        EnumChannelSwitchMode chSwMode = null;

        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/systemsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            chSwMode = cursor.getInt(cursor.getColumnIndex("bATVChSwitchFreeze")) == 0 ? EnumChannelSwitchMode.E_CHANNEL_SWM_BLACKSCREEN
                    : EnumChannelSwitchMode.E_CHANNEL_SWM_FREEZE;
        }
        cursor.close();
        return chSwMode;
    }

    @Override
    public void updateChannelSwitchMode(EnumChannelSwitchMode eChSwMode) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("bATVChSwitchFreeze", eChSwMode.ordinal());
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/systemsetting"), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_SystemSetting ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_SystemSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryAntennaType() {
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/mediumsetting/"), null, null, null, null);
        cursor.moveToFirst();
        int antennaType = 0;
        antennaType = cursor.getInt(cursor.getColumnIndex("AntennaType"));
        cursor.close();
        return antennaType;
    }

    @Override
    public void updateAntennaType(int antennaType) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("AntennaType", antennaType);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/mediumsetting"), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_SystemSetting ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_MediumSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public UsaMpaaRatingType queryVchipMpaaItem() {
        UsaMpaaRatingType value = new UsaMpaaRatingType();
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/vchipmpaaitem/"), null, null, null, null);
        if (cursor.moveToFirst()) {
            value.enUaMpaaRatingType = EnumUsaMpaaRatingType.values()[cursor.getInt(cursor
                    .getColumnIndex("Rating"))];
            value.isNr = cursor.getInt(cursor.getColumnIndex("bIsNR")) == 1 ? true : false;
        }
        cursor.close();
        return value;
    }

    @Override
    public void updateVchipMpaaItem(UsaMpaaRatingType value) {
        ContentValues vals = new ContentValues();
        vals.put("Rating", value.enUaMpaaRatingType.ordinal());
        vals.put("bIsNR", value.isNr ? 1 : 0);
        try {
            getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/vchipmpaaitem"),
                    vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_VChipMappItem_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public UsaTvRatingInformation queryVchipSetting() {
        UsaTvRatingInformation value = new UsaTvRatingInformation();
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/vchipsetting/"), null, null, null, null);
        if (cursor.moveToFirst()) {
            int tv_none = cursor.getInt(cursor.getColumnIndex("stVChipTVItem_NONE"));
            value.bTV_NONE_ALL_Lock = (tv_none >> 0 & 0x01) == 1 ? true : false;
            int tv_y = cursor.getInt(cursor.getColumnIndex("stVChipTVItem_Y"));
            value.bTV_Y_ALL_Lock = (tv_y >> 0 & 0x01) == 1 ? true : false;
            int tv_y7 = cursor.getInt(cursor.getColumnIndex("stVChipTVItem_Y7"));
            value.bTV_Y7_ALL_Lock = (tv_y7 >> 0 & 0x01) == 1 ? true : false;
            value.bTV_Y7_FV_Lock = (tv_y7 >> 1 & 0x01) == 1 ? true : false;
            int tv_g = cursor.getInt(cursor.getColumnIndex("stVChipTVItem_G"));
            value.bTV_G_ALL_Lock = (tv_g >> 0 & 0x01) == 1 ? true : false;
            int tv_pg = cursor.getInt(cursor.getColumnIndex("stVChipTVItem_PG"));
            value.bTV_PG_ALL_Lock = (tv_pg >> 0 & 0x01) == 1 ? true : false;
            value.bTV_PG_V_Lock = (tv_pg >> 2 & 0x01) == 1 ? true : false;
            value.bTV_PG_S_Lock = (tv_pg >> 3 & 0x01) == 1 ? true : false;
            value.bTV_PG_L_Lock = (tv_pg >> 4 & 0x01) == 1 ? true : false;
            value.bTV_PG_D_Lock = (tv_pg >> 5 & 0x01) == 1 ? true : false;
            int tv_14 = cursor.getInt(cursor.getColumnIndex("stVChipTVItem_14"));
            value.bTV_14_ALL_Lock = (tv_14 >> 0 & 0x01) == 1 ? true : false;
            value.bTV_14_V_Lock = (tv_14 >> 2 & 0x01) == 1 ? true : false;
            value.bTV_14_S_Lock = (tv_14 >> 3 & 0x01) == 1 ? true : false;
            value.bTV_14_L_Lock = (tv_14 >> 4 & 0x01) == 1 ? true : false;
            value.bTV_14_D_Lock = (tv_14 >> 5 & 0x01) == 1 ? true : false;
            int tv_ma = cursor.getInt(cursor.getColumnIndex("stVChipTVItem_MA"));
            value.bTV_MA_ALL_Lock = (tv_ma >> 0 & 0x01) == 1 ? true : false;
            value.bTV_MA_V_Lock = (tv_ma >> 2 & 0x01) == 1 ? true : false;
            value.bTV_MA_S_Lock = (tv_ma >> 3 & 0x01) == 1 ? true : false;
            value.bTV_MA_L_Lock = (tv_ma >> 4 & 0x01) == 1 ? true : false;
        }
        cursor.close();
        return value;
    }

    @Override
    public void updateVchipSetting(int[] value) {
        ContentValues vals = new ContentValues();
        vals.put("stVChipTVItem_NONE", value[0]);
        vals.put("stVChipTVItem_Y", value[1]);
        vals.put("stVChipTVItem_Y7", value[2]);
        vals.put("stVChipTVItem_G", value[3]);
        vals.put("stVChipTVItem_PG", value[4]);
        vals.put("stVChipTVItem_14", value[5]);
        vals.put("stVChipTVItem_MA", value[6]);
        try {
            getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/vchipsetting"),
                    vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_VChipSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryCanadaEngRatingLock() {
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/vchipsetting/"), null, null, null, null);
        int rank = 0;
        if (cursor.moveToFirst()) {
            rank = cursor.getInt(cursor.getColumnIndex("u8VChipCEItem"));
        }
        cursor.close();
        return rank;
    }

    @Override
    public void updateCanadaEngRatingLock(int value) {
        ContentValues vals = new ContentValues();
        vals.put("u8VChipCEItem", value);
        try {
            getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/vchipsetting"),
                    vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_VChipSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryCanadaFreRatingLock() {
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/vchipsetting/"), null, null, null, null);
        int rank = 0;
        if (cursor.moveToFirst()) {
            rank = cursor.getInt(cursor.getColumnIndex("u8VChipCFItem"));
        }
        cursor.close();
        return rank;
    }

    @Override
    public void updateCanadaFreRatingLock(int value) {
        ContentValues vals = new ContentValues();
        vals.put("u8VChipCFItem", value);
        try {
            getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/vchipsetting"),
                    vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_VChipSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean queryBlockUnlockUnrated() {
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/miscsetting/"), null, null, null, null);
        int rank = 0;
        if (cursor.moveToFirst()) {
            rank = cursor.getInt(cursor.getColumnIndex("BlockUnratedTV"));
        }
        cursor.close();
        return rank == 0 ? false : true;
    }

    @Override
    public void updateBlockUnlockUnrated(boolean enable) {
        ContentValues vals = new ContentValues();
        vals.put("BlockUnratedTV", enable ? 1 : 0);
        try {
            getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/miscsetting"),
                    vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_MiscSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Regin5DimensionInformation> queryRRT5Dimension() {
        List<Regin5DimensionInformation> infos = new ArrayList<Regin5DimensionInformation>();
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/vchipratinginfo/"), null, null, null,
                null);
        int rank = 0;
        if (cursor.moveToFirst()) {
            rank = cursor.getInt(cursor.getColumnIndex("u8NoDimension"));
        }
        cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/regin5dimensioninfo/"), null, null, null,
                null);
        int count = 0;
        while (cursor.moveToNext() && count < rank) {
            int index = cursor.getInt(cursor.getColumnIndex("_id"));
            String dimensionName = cursor.getString(cursor.getColumnIndex("u8DimensionName"));
            int values_Defined = cursor.getInt(cursor.getColumnIndex("u8Values_Defined"));
            int graduated_Scale = cursor.getInt(cursor.getColumnIndex("u16Graduated_Scale"));
            Regin5DimensionInformation info = new Regin5DimensionInformation(index, dimensionName,
                    values_Defined, graduated_Scale);
            infos.add(info);
            count++;
        }
        cursor.close();
        return infos;
    }

    @Override
    public List<RR5RatingPair> queryRR5RatingPair(int index, int count) {
        List<RR5RatingPair> list = new ArrayList<RR5RatingPair>();
        Cursor cursorA = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/abbratingtext/" + index * 15 + "/"
                        + (index * 15 + count)), null, null, null, null);
        int countA = 0;
        while (cursorA.moveToNext() && countA < count) {
            String stAbbRatingText = cursorA.getString(cursorA.getColumnIndex("stAbbRatingText"));
            RR5RatingPair pair = new RR5RatingPair();
            pair.regin5Dimension_index = countA;
            pair.abbRatingText = stAbbRatingText;
            list.add(pair);
            countA++;
        }
        cursorA.close();
        Cursor cursorB = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/rr5ratingpair/" + index), null, null,
                null, null);
        if (cursorB.moveToFirst()) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).rR5RatingPair_id = cursorB.getInt(cursorB
                        .getColumnIndex("stRR5RatingPair_id_" + i));
            }
        }
        cursorB.close();
        return list;
    }

    @Override
    public void updateRR5RatingPair(int title, int index, int value) {
        ContentValues vals = new ContentValues();
        vals.put("stRR5RatingPair_id_" + title, value);
        try {
            getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/rr5ratingpair/" + index), vals, null,
                    null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_MiscSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryRRT5NoDimension() {
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/vchipratinginfo/"), null, null, null,
                null);
        int rank = 0;
        if (cursor.moveToFirst()) {
            rank = cursor.getInt(cursor.getColumnIndex("u8NoDimension"));
        }
        cursor.close();
        return rank;
    }

    @Override
    public CCSetting queryCCSetting() {
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/ccsetting/"), null, null, null, null);
        short ccMode = 0;
        short basicMode = 0;
        short advancedMode = 0;
        if (cursor.moveToFirst()) {
            ccMode = cursor.getShort(cursor.getColumnIndex("OnOffMode"));
            basicMode = cursor.getShort(cursor.getColumnIndex("BasicMode"));
            advancedMode = cursor.getShort(cursor.getColumnIndex("AdvancedMode"));
        }
        CCSetting setting = new CCSetting(ccMode, basicMode, advancedMode);
        cursor.close();
        return setting;
    }

    @Override
    public void updateCCSetting(CCSetting seting) {
        ContentValues vals = new ContentValues();
        vals.put("OnOffMode", seting.ccMode);
        vals.put("BasicMode", seting.basicMode);
        vals.put("AdvancedMode", seting.advancedMode);
        try {
            getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/ccsetting/"),
                    vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_CCSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CaptionOptionSetting queryAdvancedSetting(int index) {
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/ccadvancedsetting/" + index), null, null,
                null, null);
        short isDefault = 0;
        short fGColor = 0;
        short bGColor = 0;
        short fGOpacity = 0;
        short bGOpacity = 0;
        short fontStyle = 0;
        short fontSize = 0;
        short fontEdgeStyle = 0;
        short fontEdgeColor = 0;
        short italicsAttr = 0;
        short underlineAttr = 0;

        if (cursor.moveToFirst()) {
            isDefault = cursor.getShort(cursor.getColumnIndex("bIsDefault"));
            fGColor = cursor.getShort(cursor.getColumnIndex("FGColor"));
            bGColor = cursor.getShort(cursor.getColumnIndex("BGColor"));
            fGOpacity = cursor.getShort(cursor.getColumnIndex("FGOpacity"));
            bGOpacity = cursor.getShort(cursor.getColumnIndex("BGOpacity"));
            fontStyle = cursor.getShort(cursor.getColumnIndex("FontStyle"));
            fontSize = cursor.getShort(cursor.getColumnIndex("FontSize"));
            fontEdgeStyle = cursor.getShort(cursor.getColumnIndex("FontEdgeStyle"));
            fontEdgeColor = cursor.getShort(cursor.getColumnIndex("FontEdgeColor"));
            italicsAttr = cursor.getShort(cursor.getColumnIndex("ItalicsAttr"));
            underlineAttr = cursor.getShort(cursor.getColumnIndex("UnderlineAttr"));
        }
        CaptionOptionSetting setting = new CaptionOptionSetting(isDefault, fGColor, bGColor,
                fGOpacity, bGOpacity, fontSize, fontStyle, fontEdgeStyle, fontEdgeColor,
                italicsAttr, underlineAttr);

        cursor.close();
        return setting;
    }

    @Override
    public void updateAdvancedSetting(CaptionOptionSetting setting, int index) {
        ContentValues vals = new ContentValues();
        vals.put("bIsDefault", setting.currProgInfoIsDefault);
        vals.put("FGColor", setting.currProgInfoFGColor);
        vals.put("BGColor", setting.currProgInfoBGColor);
        vals.put("FGOpacity", setting.currProgInfoFGOpacity);
        vals.put("BGOpacity", setting.currProgInfoBGOpacity);
        vals.put("FontStyle", setting.currProgInfoFontStyle);
        vals.put("FontSize", setting.currProgInfoFontSize);
        vals.put("FontEdgeStyle", setting.currProgInfoEdgeStyle);
        vals.put("FontEdgeColor", setting.currProgInfoEdgeColor);
        vals.put("ItalicsAttr", setting.currProgInfoItalicsAttr);
        vals.put("UnderlineAttr", setting.currProgInfoUnderlineAttr);

        try {
            getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/ccadvancedsetting/" + index), vals,
                    null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_CCAdvancedSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateVersionNo() {
        ContentValues vals = new ContentValues();
        // the default value is 255.
        vals.put("u8VersionNo", 255);
        vals.put("u8NoDimension", 0);
        try {
            getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/vchipratinginfo/"), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_VChipRatingInfo_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryBlockSysLockMode() {
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/blocksyssetting/"), null, null, null,
                null);
        int mode = 0;
        if (cursor.moveToFirst()) {
            mode = cursor.getInt(cursor.getColumnIndex("u8BlockSysLockMode"));
        }
        cursor.close();
        return mode;
    }

    @Override
    public void updateBlockSysLockMode(int value) {
        ContentValues vals = new ContentValues();
        vals.put("u8BlockSysLockMode", value);
        try {
            getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/blocksyssetting/"), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_BlockSysSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int[] queryInputBlockFlag() {
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/vchipsetting/"), null, null, null, null);
        int[] values = {
                0, 0, 0, 0, 0, 0, 0, 0, 0
        };
        if (cursor.moveToFirst()) {
            int inputBlock = cursor.getInt(cursor.getColumnIndex("u16InputBlockItem"));
            for (int i = 0; i < values.length; i++) {
                values[i] = inputBlock >> i & 0x01;
            }
        }
        cursor.close();
        return values;
    }

    @Override
    public void updateInputBlockFlag(int[] values) {
        ContentValues vals = new ContentValues();
        int value = 0;
        for (int i = 0; i < values.length; i++) {
            value += values[i] << i;
        }
        vals.put("u16InputBlockItem", value);
        vals.put("u16InputBlockItem_Loop", value);
        try {
            getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/vchipsetting"),
                    vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_VChipSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryAudioLanguageDefaultValue() {
        int ret = 0;
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/soundsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            ret = cursor.getInt(cursor.getColumnIndex("enSoundAudioLan1"));
        }
        cursor.close();
        return ret;
    }

    @Override
    public void updateAudioLanguageDefaultValue(int value) {
        ContentValues vals = new ContentValues();
        vals.put("enSoundAudioLan1", value);
        try {
            getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/soundsetting"),
                    vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_SoundSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int[] queryPcModeInfo() {
        int id = 0;
        int modeIndex = getVideoInfo().modeIndex;
        for (id = 0; id < 10; id++) {
            if ((short) queryPCModeIndex(id) == modeIndex) {
                break;
            }
        }
        String str = "content://mstar.tv.usersetting/userpcmodesetting/" + String.valueOf(id);
        Cursor cursor = getContentResolver().query(Uri.parse(str), null, null, null, null);
        int[] values = {
                0, 0, 0, 0
        };
        if (cursor.moveToFirst()) {
            values[0] = cursor.getInt(cursor.getColumnIndex("u16UI_Clock"));
            values[1] = cursor.getInt(cursor.getColumnIndex("u16UI_Phase"));
            values[2] = cursor.getInt(cursor.getColumnIndex("u16UI_HorizontalStart"));
            values[3] = cursor.getInt(cursor.getColumnIndex("u16UI_VorizontalStart"));
        }
        cursor.close();
        return values;
    }

    @Override
    public void updateSpeakerDelay(int delay) {
        ContentValues vals = new ContentValues();
        vals.put("Speaker_Delay", delay);
        try {
            getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/soundsetting"),
                    vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_SoundSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSpdifDelay(int delay) {
        ContentValues vals = new ContentValues();
        vals.put("SPDIF_Delay", delay);
        try {
            getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/soundsetting"),
                    vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_SoundSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int querySourceIdent() {
        int ret = 0;
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/systemsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            ret = cursor.getInt(cursor.getColumnIndex("bSourceDetectEnable"));
        }
        cursor.close();
        return ret;
    }

    @Override
    public void updateSourceIdent(int currentState) {
        ContentValues vals = new ContentValues();
        vals.put("bSourceDetectEnable", currentState);
        try {
            getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/systemsetting"), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_SystemSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int querySourcePreview() {
        int ret = 0;
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/systemsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            ret = cursor.getInt(cursor.getColumnIndex("bSourcePreview"));
        }
        cursor.close();
        return ret;
    }

    @Override
    public void updateSourcePreview(int currentState) {
        ContentValues vals = new ContentValues();
        vals.put("bSourcePreview", currentState);
        try {
            getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/systemsetting"), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_SystemSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int querySourceSwitch() {
        int ret = 0;
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/systemsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            ret = cursor.getInt(cursor.getColumnIndex("bAutoSourceSwitch"));
        }
        cursor.close();
        return ret;
    }

    @Override
    public void updateSourceSwitch(int currentState) {
        ContentValues vals = new ContentValues();
        vals.put("bAutoSourceSwitch", currentState);
        try {
            getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/systemsetting"), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_SystemSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryDGClarity() {
        int value = 0;
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/soundsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex("DGClarity"));
        }
        cursor.close();
        return value;
    }

    @Override
    public void updateDGClarity(int value) {
        ContentValues vals = new ContentValues();
        vals.put("DGClarity", value);
        try {
            getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/soundsetting"),vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public TimeSetting queryTimeSetting() {
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/timesetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            mTimeSetting.onTimeFlag = cursor.getInt(cursor.getColumnIndex("bOnTimeFlag")) == 0 ? false
                    : true;
            mTimeSetting.offTimeFlag = cursor.getInt(cursor.getColumnIndex("bOffTimeFlag")) == 0 ? false
                    : true;
            mTimeSetting.offTimeState = (short) cursor.getInt(cursor.getColumnIndex("enOffTimeState"));
            mTimeSetting.offTimeInfo_Hour = (short) cursor.getInt(cursor.getColumnIndex("u8OffTimer_Info_Hour"));
            mTimeSetting.offTimeInfo_Min = (short) cursor.getInt(cursor.getColumnIndex("u8OffTimer_Info_Min"));
            mTimeSetting.onTimeState = (short) cursor.getInt(cursor.getColumnIndex("enOnTimeState"));
            mTimeSetting.onTimeInfo_Hour = (short) cursor.getInt(cursor.getColumnIndex("u8OnTimer_Info_Hour"));
            mTimeSetting.onTimeInfo_Min = (short) cursor.getInt(cursor.getColumnIndex("u8OnTimer_Info_Min"));
            mTimeSetting.onTimeChannel = cursor.getInt(cursor.getColumnIndex("cOnTimerChannel"));
            mTimeSetting.onTimeTVSrc = (short) cursor.getInt(cursor.getColumnIndex("cOnTimeTVSrc"));
            mTimeSetting.onTimeAntennaType = (short) cursor.getInt(cursor.getColumnIndex("cOnTimeAntennaType"));
            mTimeSetting.onTimeVolume = (short) cursor.getInt(cursor.getColumnIndex("cOnTimerVolume"));
            mTimeSetting.timeZoneInfo = (short) cursor.getInt(cursor.getColumnIndex("eTimeZoneInfo"));
            mTimeSetting.is12Hour = cursor.getInt(cursor.getColumnIndex("bIs12Hour")) == 0 ? false
                    : true;
            mTimeSetting.isAutoSync = cursor.getInt(cursor.getColumnIndex("bIsAutoSync")) == 0 ? false
                    : true;
            mTimeSetting.isClockMode = (short) cursor.getInt(cursor.getColumnIndex("bClockMode")) == 0 ? false
                    : true;
            mTimeSetting.autoSleepFlag = cursor.getInt(cursor.getColumnIndex("bAutoSleepFlag")) == 0 ? false
                    : true;
            mTimeSetting.isDaylightSaving = (short) cursor.getInt(cursor.getColumnIndex("bIsDaylightsaving")) == 0 ? false
                    : true;
            mTimeSetting.timerBootMode = cursor.getInt(cursor.getColumnIndex("enTimerBootMode"));
            mTimeSetting.offsetTime = cursor.getInt(cursor.getColumnIndex("s32OffsetTime"));
            try {
                mTimeSetting.daylightSavingMode = cursor.getInt(cursor.getColumnIndexOrThrow("enDaylightSavingMode"));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return mTimeSetting;
    }

    @Override
    public void updateTimeSetting(TimeSetting model) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("bOnTimeFlag", model.onTimeFlag ? 1 : 0);
        vals.put("bOffTimeFlag", model.offTimeFlag ? 1 : 0);
        vals.put("enOffTimeState", model.offTimeState);
        vals.put("u8OffTimer_Info_Hour", model.offTimeInfo_Hour);
        vals.put("u8OffTimer_Info_Min", model.offTimeInfo_Min);
        vals.put("enOnTimeState", model.onTimeState);
        vals.put("u8OnTimer_Info_Hour", model.onTimeInfo_Hour);
        vals.put("u8OnTimer_Info_Min", model.onTimeInfo_Min);
        vals.put("cOnTimerChannel", model.onTimeChannel);
        vals.put("cOnTimeTVSrc", model.onTimeTVSrc);
        vals.put("cOnTimeAntennaType", model.onTimeAntennaType);
        vals.put("cOnTimerVolume", model.onTimeVolume);
        vals.put("eTimeZoneInfo", model.timeZoneInfo);
        vals.put("bIs12Hour", model.is12Hour ? 1 : 0);
        vals.put("bIsAutoSync", model.isAutoSync ? 1 : 0);
        vals.put("bClockMode", model.isClockMode ? 1 : 0);
        vals.put("bAutoSleepFlag", model.autoSleepFlag ? 1 : 0);
        vals.put("bIsDaylightsaving", model.isDaylightSaving ? 1 : 0);
        vals.put("enTimerBootMode", model.timerBootMode);
        vals.put("s32OffsetTime", model.offsetTime);
        vals.put("enDaylightSavingMode", model.daylightSavingMode);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/timesetting"), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_TimeSetting ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_TimeSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateVideoMuteColor(int nColor) {
        ContentValues vals = new ContentValues();
        vals.put("enMuteColor", nColor);
        try {
            getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/bootsetting"),vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int queryVideoMuteColor() {
        int value = 0;
        try {
            Cursor cursor = getContentResolver().query(
                    Uri.parse("content://mstar.tv.usersetting/bootsetting"), null, null, null, null);
            if (cursor.moveToFirst()) {
                value = cursor.getInt(cursor.getColumnIndexOrThrow("enMuteColor"));
            }
            cursor.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return value;
    }

    private int getCurrentInputSource() {
        EnumInputSource curSourceType = null;
        try {
            curSourceType = TvManager.getInstance().getCurrentInputSource();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return curSourceType.ordinal();
    }

    private boolean initVarSound() {
        mSoundSetting = new UserSoundSetting();
        mSoundSetting.checkSum = 0xFFFF;
        return true;
    }

    private boolean InitSettingVar() {
        mUserSetting = new UserSetting();
        // init data
        mUserSetting.checkSum = 0xFFFF;
        mUserSetting.isRunInstallationGuide = true;
        mUserSetting.isNoChannel = false;
        mUserSetting.isDisableSiAutoUpdate = false;
        mUserSetting.enInputSourceType = EnumInputSource.E_INPUT_SOURCE_ATV;
        mUserSetting.eCableOperators = EnumCableOperator.E_OTHER;
        mUserSetting.eSatellitePlatform = EnumSatellitePlatform.E_HDPLUS;
        mUserSetting.networkId = 0;
        mUserSetting.oadTime = 0x00;
        mUserSetting.oadScanAfterWakeup = 0x00;
        mUserSetting.autoVolume = 0x00;
        mUserSetting.dcPowerOFFMode = 0x00;
        mUserSetting.dtvRoute = 0x00;
        mUserSetting.scartOutRGB = 0x00;
        mUserSetting.transparency = 0x00;
        mUserSetting.menuTimeOut = 0x00000000;
        mUserSetting.audioOnly = 0x00;
        mUserSetting.isEnableWDT = 0x00;
        mUserSetting.favoriteRegion = 0x00;
        mUserSetting.bandwidth = 0x00;
        mUserSetting.timeShiftSizeType = 0x00;
        mUserSetting.oadScan = 0x00;
        mUserSetting.enablePVRRecordAll = 0x00;
        mUserSetting.colorRangeMode = 0x00;
        mUserSetting.hdmiAudioSource = 0x00;
        mUserSetting.enableAlwaysTimeshift = 0x00;
        mUserSetting.eSUPER = EnumSuperModeSettings.E_OFF;
        mUserSetting.isCheckUartBus = false;
        mUserSetting.autoZoom = 0x00;
        mUserSetting.isOverScanForAllSource = false;
        mUserSetting.brazilVideoStandardType = 0x00;
        mUserSetting.softwareUpdateMode = 0x00;
        mUserSetting.osdActiveTime = 0x00000000;
        mUserSetting.isMessageBoxExist = false;
        mUserSetting.lastOADVersion = 0x0000;
        mUserSetting.isAutoChannelUpdateEnable = false;
        mUserSetting.osdDuration = 0x00;
        mUserSetting.eChannelSwitchMode = EnumChannelSwitchMode.E_CHANNEL_SWM_BLACKSCREEN;
        mUserSetting.eOfflineDetectionMode = EnumOfflineDetectMode.E_OFF;
        mUserSetting.bBlueScreen = false;
        mUserSetting.ePowerOnMusic = EnumPowerOnMusicMode.E_POWERON_MUSIC_DEFAULT;
        mUserSetting.ePowerOnLogo = EnumPowerOnLogoMode.E_POWERON_LOGO_DEFAULT;
        mUserSetting.bViewerPrompt = false;
        mUserSetting.osdLanguage = TvLanguage.ENGLISH;
        mUserSetting.bEnableStoreCookies = false;
        mSubtitleSetting = new UserSubtitleSetting(TvLanguage.ENGLISH, TvLanguage.ENGLISH, false, false);
        mLocationSetting = new LocationSetting(0x00, 0x00, 0x00);
        mTimeSetting = new TimeSetting();
        return true;
    }

    private VideoInfo getVideoInfo() {
        VideoInfo videoInfo = null;
        try {
            videoInfo = TvManager.getInstance().getPlayerManager().getVideoInfo();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return videoInfo;
    }

    public int queryHdmiEdidVersion() {
        int ret = 0;
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/systemsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            ret = cursor.getInt(cursor.getColumnIndex("u16HdmidEdidVersion"));
        }
        cursor.close();
        return ret;
    }

    public void updataHdmiEdidVersion(int iHdmiEdidVersion) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("u16HdmidEdidVersion", iHdmiEdidVersion);
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/systemsetting"), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            Log.d(TAG, "update tbl_SystemSetting u16HdmidEdidVersion ignored");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_SystemSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean queryDvbUhf7MhzEnabled() {
        boolean ret = false;
        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/dvbusersetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            ret = (1 == cursor.getInt(cursor.getColumnIndex("bEnableUHF7M")));
        }
        cursor.close();
        return ret;
    }

    @Override
    public void updateDvbUhf7MhzEnabled(boolean bEnable) {
        ContentValues vals = new ContentValues();
        vals.put("bEnableUHF7M", bEnable ? 1 : 0);
        try {
            getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/dvbusersetting"),
                    vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_DvbUserSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

	// EosTek Patch Begin
	public void updatePicValues(PictureModeSetting picturMode, int inputSrc, int curPicMode) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("u8Brightness", picturMode.brightness);
        vals.put("u8Contrast", picturMode.contrast);
        vals.put("u8Hue", picturMode.hue);
        vals.put("u8Saturation", picturMode.saturation);
        vals.put("u8Sharpness", picturMode.sharpness);
        vals.put("u8Backlight", picturMode.backlight);
        vals.put("eColorTemp", picturMode.eColorTemp.ordinal());
        vals.put("eVibrantColour", picturMode.eVibrantColour.ordinal());
        vals.put("ePerfectClear", picturMode.ePerfectClear.ordinal());
        vals.put("eDynamicContrast", picturMode.eDynamicContrast.ordinal());
        vals.put("eDynamicBacklight", picturMode.eDynamicBacklight.ordinal());
        try {
            ret = getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/picmode_setting/inputsrc/" + inputSrc
                            + "/picmode/" + curPicMode), vals, null, null);
        } catch (SQLException e) {
        }
        if (ret == -1) {
            System.out.println("update tbl_PicMode_Setting ignored");
        }
    }

	public ColorTemperatureExData queryFactoryColorTempExData(EnumInputSource source, int colorTmpIdx) {
			int curSource = -1;
			switch (source) {
            case E_INPUT_SOURCE_VGA:
                curSource = EnumColorTempExInputSource.E_VGA.ordinal();
                break;
            case E_INPUT_SOURCE_ATV:
                curSource = EnumColorTempExInputSource.E_ATV.ordinal();
                break;
            case E_INPUT_SOURCE_CVBS:
            case E_INPUT_SOURCE_CVBS2:
            case E_INPUT_SOURCE_CVBS3:
            case E_INPUT_SOURCE_CVBS4:
            case E_INPUT_SOURCE_CVBS5:
            case E_INPUT_SOURCE_CVBS6:
            case E_INPUT_SOURCE_CVBS7:
            case E_INPUT_SOURCE_CVBS8:
                curSource = EnumColorTempExInputSource.E_CVBS.ordinal();
                break;
            case E_INPUT_SOURCE_SVIDEO:
            case E_INPUT_SOURCE_SVIDEO2:
            case E_INPUT_SOURCE_SVIDEO3:
            case E_INPUT_SOURCE_SVIDEO4:
                curSource = EnumColorTempExInputSource.E_SVIDEO.ordinal();
                break;
            case E_INPUT_SOURCE_YPBPR:
            case E_INPUT_SOURCE_YPBPR2:
            case E_INPUT_SOURCE_YPBPR3:
                curSource = EnumColorTempExInputSource.E_YPBPR.ordinal();
                break;
            case E_INPUT_SOURCE_SCART:
            case E_INPUT_SOURCE_SCART2:
                curSource = EnumColorTempExInputSource.E_SCART.ordinal();
                break;
            case E_INPUT_SOURCE_HDMI:
            case E_INPUT_SOURCE_HDMI2:
            case E_INPUT_SOURCE_HDMI3:
            case E_INPUT_SOURCE_HDMI4:
                curSource = EnumColorTempExInputSource.E_HDMI.ordinal();
                break;
            case E_INPUT_SOURCE_DTV:
            case E_INPUT_SOURCE_DTV2:
                curSource = EnumColorTempExInputSource.E_DTV.ordinal();
                break;
            case E_INPUT_SOURCE_DVI:
            case E_INPUT_SOURCE_DVI2:
            case E_INPUT_SOURCE_DVI3:
            case E_INPUT_SOURCE_DVI4:
            case E_INPUT_SOURCE_STORAGE:
            case E_INPUT_SOURCE_KTV:
            case E_INPUT_SOURCE_JPEG:
            case E_INPUT_SOURCE_STORAGE2:
                curSource = EnumColorTempExInputSource.E_OTHERS.ordinal();
                break;
            default:
                break;
        }

        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mstar.tv.factory/factorycolortempex"),
                null, "InputSourceID = " + curSource+" and ColorTemperatureID = "+colorTmpIdx, null, null);
        Log.d("DataBaseDeskImpl", "cursor?" + (cursor == null));

        if (cursor.moveToFirst()) {
			ColorTemperatureExData model = new ColorTemperatureExData();
			model.redGain = cursor.getInt(cursor.getColumnIndex("u16RedGain"));
            model.greenGain = cursor.getInt(cursor.getColumnIndex("u16GreenGain"));
			model.blueGain = cursor.getInt(cursor.getColumnIndex("u16BlueGain"));
            model.redOffset = cursor.getInt(cursor.getColumnIndex("u16RedOffset"));
            model.greenOffset = cursor.getInt(cursor.getColumnIndex("u16GreenOffset"));
            model.blueOffset = cursor.getInt(cursor.getColumnIndex("u16BlueOffset"));
            Log.d("DataBaseDeskImpl", "cursor?" + cursor.getInt(cursor.getColumnIndex("u16RedGain")));
            Log.d("DataBaseDeskImpl", "cursor?" + cursor.getInt(cursor.getColumnIndex("u16GreenGain")));
            Log.d("DataBaseDeskImpl", "cursor?" + cursor.getInt(cursor.getColumnIndex("u16BlueGain")));
            Log.d("DataBaseDeskImpl", "cursor?" + cursor.getInt(cursor.getColumnIndex("u16RedOffset")));
            Log.d("DataBaseDeskImpl", "cursor?" + cursor.getInt(cursor.getColumnIndex("u16GreenOffset")));
            Log.d("DataBaseDeskImpl", "cursor?" + cursor.getInt(cursor.getColumnIndex("u16BlueOffset")));
            return model;
        }
        cursor.close();
        return null;
    }

	public void updateFactoryColorTempExData(ColorTemperatureExData model, int sourceId, int colorTmpId) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("u16RedGain", model.redGain);
        vals.put("u16GreenGain", model.greenGain);
        vals.put("u16BlueGain", model.blueGain);
        vals.put("u16RedOffset", model.redOffset);
        vals.put("u16GreenOffset", model.greenOffset);
        vals.put("u16BlueOffset", model.blueOffset);
        try {
            ret = getContentResolver().update(
                                             Uri.parse("content://mstar.tv.factory/factorycolortempex/inputsourceid/" + sourceId
                                                       + "/colortemperatureid/" + colorTmpId), vals, null, null);
        } catch (SQLException e) {
        }
        if (ret == -1) {
            System.out.println("update tbl_FactoryColorTempEx ignored");
        }

        try {
            if (TvManager.getInstance()!=null) {
                TvManager.getInstance().getDatabaseManager().setDatabaseDirtyByApplication(T_FacrotyColorTempEx_IDX);
            }
        }
        catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

	public void updateSoundModeAll(int soundMode, int bass, int treble) {
		long ret = -1;
        ContentValues soundModeVals = new ContentValues();
        soundModeVals.put("SoundMode", soundMode);
        try {
            ret = getContentResolver().update(
                                             Uri.parse("content://mstar.tv.usersetting/soundsetting"), soundModeVals, null, null);
        } catch (SQLException e) {
        }
        if (ret == -1) {
            System.out.println("update tbl_SoundSetting ignored");
        }

		try {
            if (TvManager.getInstance()!=null) {
                TvManager.getInstance().getDatabaseManager().setDatabaseDirtyByApplication(T_SoundSetting_IDX);
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }

        ContentValues vals = new ContentValues();
        vals.put("Bass", bass);
		vals.put("Treble", treble);
        try {
            getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/soundmodesetting/" + soundMode), vals, null, null);
        } catch (SQLException e) {

        }
		try {
            if (TvManager.getInstance()!=null) {
                TvManager.getInstance().getDatabaseManager().setDatabaseDirtyByApplication(T_SoundMode_Setting_IDX);
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

	public int[] queryVideoItems(int inputSrc, int pictureMode) {
		Log.e("DataBaseDeskImpl", "queryVideoItems");
        Cursor cursorPicMode = getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/picmode_setting/inputsrc/" + inputSrc
                        + "/picmode/" + pictureMode), null, null, null, null);
        cursorPicMode.moveToFirst();
        int[] values = {
                0, 0, 0, 0, 0, 0
        };
        if (cursorPicMode.moveToFirst()) {
            values[0] = cursorPicMode.getInt(cursorPicMode.getColumnIndex("u8Brightness"));
            values[1] = cursorPicMode.getInt(cursorPicMode.getColumnIndex("u8Contrast"));
            values[2] = cursorPicMode.getInt(cursorPicMode.getColumnIndex("u8Hue"));
            values[3] = cursorPicMode.getInt(cursorPicMode.getColumnIndex("u8Saturation"));
            values[4] = cursorPicMode.getInt(cursorPicMode.getColumnIndex("u8Sharpness"));
            values[5] = cursorPicMode.getInt(cursorPicMode.getColumnIndex("u8Backlight"));
        }
        cursorPicMode.close();
        return values;
    }

	public int[] queryPCImage() {
		Log.e("DataBaseDeskImpl", "queryPCImage start");
        int id = 0;
		int modeIndex= getVideoInfo().modeIndex;
		int[] pcModeindex = queryPCModeIndex();
        for (id = 0; id < 10; id++) {
            if ((short) pcModeindex[id] == modeIndex) {
                Log.i("DataBaseDeskImpl", "~~~~~~~ id is " + id + "~~~~~~~~~~~");
                break;
            }
        }
        String str = "content://mstar.tv.usersetting/userpcmodesetting/" + String.valueOf(id);
        Cursor cursor = getContentResolver().query(Uri.parse(str), null, null, null, null);
        int[] values = {
                0, 0, 0, 0
        };
        if (cursor.moveToFirst()) {
            values[0] = cursor.getInt(cursor.getColumnIndex("u16UI_Clock"));
            values[1] = cursor.getInt(cursor.getColumnIndex("u16UI_Phase"));
            values[2] = cursor.getInt(cursor.getColumnIndex("u16UI_HorizontalStart"));
            values[3] = cursor.getInt(cursor.getColumnIndex("u16UI_VorizontalStart"));
        }
        cursor.close();
		Log.i("DataBaseDeskImpl", "queryPCImage end~~~~~~~~~~~");
        return values;
    }

	private int[] queryPCModeIndex() {
        String str = "content://mstar.tv.usersetting/userpcmodesetting/";
        Cursor cursor = getContentResolver().query(Uri.parse(str), null, null, null, null);
        int[] values = {
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0
        };
		for (int i = 0; i < 10; i++) {
			if (cursor.moveToNext()) {
				values[i] = cursor.getInt(cursor.getColumnIndex("u8ModeIndex"));
			}
		}
        cursor.close();
        return values;
    }
	// EosTek Patch End
	
}
