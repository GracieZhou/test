
package com.eostek.tvmenu.picture;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.widget.FrameLayout;

import com.eostek.tvmenu.R;
import com.eostek.tvmenu.picture.VideoDataSet.EN_MS_COLOR_TEMP;
import com.eostek.tvmenu.picture.VideoDataSet.EN_MS_MPEG_NR;
import com.eostek.tvmenu.picture.VideoDataSet.EN_MS_NR;
import com.eostek.tvmenu.picture.VideoDataSet.EN_MS_PICTURE;
import com.eostek.tvmenu.picture.VideoDataSet.T_MS_NR_MODE;
import com.eostek.tvmenu.picture.VideoDataSet.T_MS_PICTURE;
import com.eostek.tvmenu.utils.ExTvChannelManager;
import com.eostek.tvmenu.utils.FactoryDB;
import com.eostek.tvmenu.utils.FactoryDeskImpl;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvPictureManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.ColorTemperatureExData;
import com.mstar.android.tvapi.common.vo.EnumColorTemperature;
import com.mstar.android.tvapi.common.vo.EnumPictureMode;
import com.mstar.android.tvapi.common.vo.EnumVideoItem;
import com.mstar.android.tvapi.common.vo.MpegNoiseReduction.EnumMpegNoiseReduction;
import com.mstar.android.tvapi.common.vo.NoiseReduction.EnumNoiseReduction;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;

public class PictureSettingLogic {
    private final static String TAG = "PictureSettingLogic";
    
    PictureSettingFragment mFragment;
    
    private TvPictureManager mTvPictureManager = TvPictureManager.getInstance();

    private VideoDataSet mVideoPara;
    
    ColorTemperatureExData mColorTempExData;

    private int mCurSourceType = TvCommonManager.INPUT_SOURCE_NONE;
    
    private int CHECKSUM_COLER = 0xFFFF;
    
    PictureSettingLogic(PictureSettingFragment f) {
        mFragment = f;
    }
    
    /**
     * set tv data queried to the item data structure and update view.
     * 
     * @param
     */
    void initData() {
        initVideoPara();
        
        TvPictureManager manager = TvPictureManager.getInstance();
        mCurSourceType = TvCommonManager.getInstance().getCurrentTvInputSource();
        
        //picture mode
        EnumPictureMode pictureMode = manager.getPictureModeIdx();
        int initValue = pictureMode.ordinal();
        // picture mode only support dynamic,normal,soft,user
        if (initValue > 3) {
            Log.v("PictureMode", "initValue = " + initValue + ";should to reset to 0");
            initValue = 0;
        }
        mFragment.mHolder.mItemPictureMode.setCurValue(initValue);
        String[] picturemodeVals = mFragment.getActivity().getResources()
                .getStringArray(R.array.setting_picturemode_vals);
        mFragment.mHolder.mPictureModeContentTxt.setText(picturemodeVals[initValue]);
        // values[0]:Brightness,values[1]:Contrast,values[2]:Hue,values[3]:Saturation,values[4]:Sharpness,values[5]:Backlight
        int[] values = manager.getVideoItems(EnumInputSource.values()[mCurSourceType], pictureMode);
        
        //picture contrast
        mFragment.mHolder.mItemPictureContrast.setCurValue(values[1]);
        mFragment.mHolder.updateSeekBar(mFragment.mHolder.mItemPictureContrast, mFragment.mHolder.mPictureContrastSeekBar, mFragment.mHolder.mPictureContrastSeekBarNum);
        
        //picture brightness
        mFragment.mHolder.mItemPictureBrightness.setCurValue(values[0]);
        mFragment.mHolder.updateSeekBar(mFragment.mHolder.mItemPictureBrightness, mFragment.mHolder.mPictureBrightnessSeekBar, mFragment.mHolder.mPictureBrightnessSeekBarNum);
        
        //picture hue
        mFragment.mHolder.mItemPictureHue.setCurValue(values[2]);
        mFragment.mHolder.updateSeekBar(mFragment.mHolder.mItemPictureHue, mFragment.mHolder.mPictureHueSeekBar, mFragment.mHolder.mPictureHueSeekBarNum);
        
        //picture sharpness
        mFragment.mHolder.mItemPictureSharpness.setCurValue(values[4]);
        mFragment.mHolder.updateSeekBar(mFragment.mHolder.mItemPictureSharpness, mFragment.mHolder.mPictureSharpnessSeekBar, mFragment.mHolder.mPictureSharpnessSeekBarNum);
        
        //picture saturation
        mFragment.mHolder.mItemPictureSaturation.setCurValue(values[3]);
        mFragment.mHolder.updateSeekBar(mFragment.mHolder.mItemPictureSaturation, mFragment.mHolder.mPictureSaturationSeekBar, mFragment.mHolder.mPictureSaturationSeekBarNum);
        
        //picture backlight
        mFragment.mHolder.mItemPictureBackLight.setCurValue(values[5]);
        mFragment.mHolder.updateSeekBar(mFragment.mHolder.mItemPictureBackLight, mFragment.mHolder.mPictureBackLightSeekBar, mFragment.mHolder.mPictureBackLightSeekBarNum);
        
        //color temperature
        initValue = manager.getColorTempratureIdx();
        mFragment.mHolder.mItemColorTemperature.setCurValue(initValue);
        mFragment.mHolder.mColorTemperatureContentTxt.setText(mFragment.mHolder.mItemColorTemperature.getValues()[mFragment.mHolder.mItemColorTemperature.getCurValue()]);
        mColorTempExData = manager.getWbGainOffsetEx(initValue, mCurSourceType);
        
        //color red
        mFragment.mHolder.mItemRed.setCurValue(mColorTempExData.redGain / PictureConstants.COLOR_TEMPERATURE);
        mFragment.mHolder.updateSeekBar(mFragment.mHolder.mItemRed, mFragment.mHolder.mRedSeekBar, mFragment.mHolder.mRedSeekBarNum);

        //color green
        mFragment.mHolder.mItemGreen.setCurValue(mColorTempExData.greenGain / PictureConstants.COLOR_TEMPERATURE);
        mFragment.mHolder.updateSeekBar(mFragment.mHolder.mItemGreen, mFragment.mHolder.mGreenSeekBar, mFragment.mHolder.mGreenSeekBarNum);

        //colorblue
        mFragment.mHolder.mItemBlue.setCurValue(mColorTempExData.blueGain / PictureConstants.COLOR_TEMPERATURE);
        mFragment.mHolder.updateSeekBar(mFragment.mHolder.mItemBlue, mFragment.mHolder.mBlueSeekBar, mFragment.mHolder.mBlueSeekBarNum);

        //hdmi full
        mFragment.mHolder.mItemHdmiFull.setCurValue(getHdmiUnderScan());
        String[] hdmifullVals = mFragment.getActivity().getResources().getStringArray(R.array.setting_hdmifull_vals);
        mFragment.mHolder.mHdmiFullContentTxt.setText(hdmifullVals[(getHdmiUnderScan())]);
        
        //hdmi rgb
//        int curhdmirgbindex = getHdmiRgbScan();
//        mFragment.mHolder.mItemHdmiRgb.setCurValue(curhdmirgbindex);
//        String[] hdmirgbVals = mFragment.getActivity().getResources().getStringArray(R.array.setting_hdmirgb_vals);
//        Log.d(TAG, "---"+hdmirgbVals[curhdmirgbindex]);
//        mFragment.mHolder.mHdmiRgbContentTxt.setText(hdmirgbVals[curhdmirgbindex]);

        //zoom mode
        int type = manager.getVideoArcType();
        switch (type) {
            case TvPictureManager.VIDEO_ARC_16x9:
                initValue = 0;
                break;
            case TvPictureManager.VIDEO_ARC_4x3:
                initValue = 1;
                break;
            case TvPictureManager.VIDEO_ARC_AUTO:
                initValue = 2;
                break;
            case TvPictureManager.VIDEO_ARC_PANORAMA:
                initValue = 3;
                break;
            case TvPictureManager.VIDEO_ARC_DEFAULT:
                if (mCurSourceType == TvCommonManager.INPUT_SOURCE_ATV
                        || mCurSourceType == TvCommonManager.INPUT_SOURCE_DTV) {
                    manager.setVideoArcType(TvPictureManager.VIDEO_ARC_16x9);
                    initValue = 0;
                } else {
                    initValue = 4;
                }
                break;
            default:
                manager.setVideoArcType(TvPictureManager.VIDEO_ARC_16x9);
                initValue = 0;
                break;
        }
        if (mCurSourceType == TvCommonManager.INPUT_SOURCE_STORAGE) {
            mFragment.mHolder.mItemZoomMode.setFocusable(false);
            mFragment.mHolder.mZoomModeLl.setFocusable(false);
        }
        mFragment.mHolder.mItemZoomMode.setCurValue(initValue);
        String[] zoomModeVals = mFragment.getActivity().getResources().getStringArray(R.array.setting_zoommode_vals_tv);
        mFragment.mHolder.mZoomModeContentTxt.setText(zoomModeVals[initValue]);
        //image noise reduction
        initValue = manager.getNoiseReduction();
        mFragment.mHolder.mItemImageNoiseReduction.setCurValue(initValue);
        String[] imgNoiseReductionVals = mFragment.getActivity().getResources()
                .getStringArray(R.array.setting_imgnoisereduction_vals);
        mFragment.mHolder.mImageNoiseReductionContentTxt.setText(imgNoiseReductionVals[initValue]);

        //mpeg noise reduction
        queryVideoPara(mCurSourceType);
        EN_MS_MPEG_NR mpegNR = getMpegNR();
        mFragment.mHolder.mItemMpegNoiseReduction.setCurValue(mpegNR.ordinal());
        String[] mpegNoiseReductionVals = mFragment.getActivity().getResources()
                .getStringArray(R.array.setting_mpegnoisereduction_vals);
        mFragment.mHolder.mMpegNoiseReductionContentTxt.setText(mpegNoiseReductionVals[mpegNR.ordinal()]);
    }
    
    /**
     * set user data to SN, which may lead to other tv date's change. Surely we should update other data structure and UI accordingly.
     * 
     * @param resultValue value to be set in tv system.
     * @param position corresponding position
     */
    void setTvValue(int resultValue, int position) {
        switch (position) {
            case PictureConstants.INDEX_PICTURE_MODE: 
                Log.i(TAG, "source " + mCurSourceType + ", choose picture mode --- setValue : " + resultValue);
                mTvPictureManager.setPictureMode(resultValue);
                int[] values = mTvPictureManager.getVideoItems(EnumInputSource.values()[mCurSourceType], EnumPictureMode.values()[resultValue]);
                for(int v: values){
                    Log.i(TAG, "source " + mCurSourceType + ", choose picture mode --- getValue : " + v);
                }
                mFragment.mHolder.mItemPictureContrast.setCurValue(values[1]);
                mFragment.mHolder.mItemPictureBrightness.setCurValue(values[0]);
                mFragment.mHolder.mItemPictureHue.setCurValue(values[2]);
                mFragment.mHolder.mItemPictureSharpness.setCurValue(values[4]);
                mFragment.mHolder.mItemPictureSaturation.setCurValue(values[3]);
                mFragment.mHolder.mItemPictureBackLight.setCurValue(values[5]);
                mFragment.mHolder.mItemColorTemperature.setCurValue(mTvPictureManager.getColorTempratureIdx());
                Log.i(TAG, "source " + mCurSourceType + ", choose picture mode --- colortempratureidx : " + mTvPictureManager.getColorTempratureIdx());
                mColorTempExData = mTvPictureManager.getWbGainOffsetEx(mFragment.mHolder.mItemColorTemperature.getCurValue(), mCurSourceType);
                mFragment.mHolder.mItemRed.setCurValue(mColorTempExData.redGain / PictureConstants.COLOR_TEMPERATURE);
                mFragment.mHolder.mItemGreen.setCurValue(mColorTempExData.greenGain / PictureConstants.COLOR_TEMPERATURE);
                mFragment.mHolder.mItemBlue.setCurValue(mColorTempExData.blueGain / PictureConstants.COLOR_TEMPERATURE);
                mFragment.mHolder.mHandler.sendEmptyMessage(PictureConstants.MSG_UPDATEVIEW_PICTURE_MODE);
                break;
            case PictureConstants.INDEX_PICTURE_CONTRAST:
                mTvPictureManager.setPictureValue(EnumPictureMode.PICTURE_USER.ordinal(), mFragment.mHolder.mItemPictureMode.getCurValue(),
                        EnumVideoItem.MS_VIDEOITEM_CONTRAST.ordinal(), resultValue);
                Log.i(TAG, "source " + mCurSourceType + ", choose picture contrast --- picture mode : " + mFragment.mHolder.mItemPictureMode.getCurValue());
                Log.i(TAG, "source " + mCurSourceType + ", choose picture contrast --- color temperature : " + mTvPictureManager.getColorTempratureIdx());
                Log.i(TAG, "source " + mCurSourceType + ", choose picture contrast --- setvalue : " + resultValue);
                
                mFragment.mHolder.mItemPictureMode.setCurValue(EnumPictureMode.PICTURE_USER.ordinal());
                mColorTempExData = mTvPictureManager.getWbGainOffsetEx(mFragment.mHolder.mItemColorTemperature.getCurValue(), mCurSourceType);
                mFragment.mHolder.mItemRed.setCurValue(mColorTempExData.redGain / PictureConstants.COLOR_TEMPERATURE);
                mFragment.mHolder.mItemGreen.setCurValue(mColorTempExData.greenGain / PictureConstants.COLOR_TEMPERATURE);
                mFragment.mHolder.mItemBlue.setCurValue(mColorTempExData.blueGain / PictureConstants.COLOR_TEMPERATURE);
                mFragment.mHolder.mHandler.sendEmptyMessage(PictureConstants.MSG_UPDATEVIEW_PICTURE_CONTRAST);
                break;
            case PictureConstants.INDEX_PICTURE_BRIGHTNESS:
                mTvPictureManager.setPictureValue(EnumPictureMode.PICTURE_USER.ordinal(), mFragment.mHolder.mItemPictureMode.getCurValue(),
                        EnumVideoItem.MS_VIDEOITEM_BRIGHTNESS.ordinal(), resultValue);
                Log.i(TAG, "source " + mCurSourceType + ", choose picture brightness --- picture mode : " + mFragment.mHolder.mItemPictureMode.getCurValue());
                Log.i(TAG, "source " + mCurSourceType + ", choose picture brightness --- color temperature : " + mTvPictureManager.getColorTempratureIdx());
                Log.i(TAG, "source " + mCurSourceType + ", choose picture brightness --- setvalue : " + resultValue);
                mFragment.mHolder.mItemPictureMode.setCurValue(EnumPictureMode.PICTURE_USER.ordinal());
                mFragment.mHolder.mItemColorTemperature.setCurValue(mTvPictureManager.getColorTempratureIdx());
                mColorTempExData = FactoryDB.getInstance(mFragment.getActivity()).queryFactoryColorTempExData(EnumInputSource.values()[mCurSourceType],
                        mFragment.mHolder.mItemColorTemperature.getCurValue());
                mFragment.mHolder.mItemRed.setCurValue(mColorTempExData.redGain / PictureConstants.COLOR_TEMPERATURE);
                mFragment.mHolder.mItemGreen.setCurValue(mColorTempExData.greenGain / PictureConstants.COLOR_TEMPERATURE);
                mFragment.mHolder.mItemBlue.setCurValue(mColorTempExData.blueGain / PictureConstants.COLOR_TEMPERATURE);
                mFragment.mHolder.mHandler.sendEmptyMessage(PictureConstants.MSG_UPDATEVIEW_PICTURE_BRIGHTNESS);
                break;
            case PictureConstants.INDEX_PICTURE_HUE:
                mTvPictureManager.setPictureValue(EnumPictureMode.PICTURE_USER.ordinal(), mFragment.mHolder.mItemPictureMode.getCurValue(),
                        EnumVideoItem.MS_VIDEOITEM_HUE.ordinal(), resultValue);
                Log.i(TAG, "source " + mCurSourceType + ", choose picture hue --- picture mode : " + mFragment.mHolder.mItemPictureMode.getCurValue());
                Log.i(TAG, "source " + mCurSourceType + ", choose picture hue --- color temperature : " + mTvPictureManager.getColorTempratureIdx());
                Log.i(TAG, "source " + mCurSourceType + ", choose picture hue --- setvalue : " + resultValue);
                mFragment.mHolder.mItemPictureMode.setCurValue(EnumPictureMode.PICTURE_USER.ordinal());
                mFragment.mHolder.mItemColorTemperature.setCurValue(mTvPictureManager.getColorTempratureIdx());
                mColorTempExData = FactoryDB.getInstance(mFragment.getActivity()).queryFactoryColorTempExData(EnumInputSource.values()[mCurSourceType],
                        mFragment.mHolder.mItemColorTemperature.getCurValue());
                mFragment.mHolder.mItemRed.setCurValue(mColorTempExData.redGain / PictureConstants.COLOR_TEMPERATURE);
                mFragment.mHolder.mItemGreen.setCurValue(mColorTempExData.greenGain / PictureConstants.COLOR_TEMPERATURE);
                mFragment.mHolder.mItemBlue.setCurValue(mColorTempExData.blueGain / PictureConstants.COLOR_TEMPERATURE);
                mFragment.mHolder.mHandler.sendEmptyMessage(PictureConstants.MSG_UPDATEVIEW_PICTURE_BRIGHTNESS);
                break;
            case PictureConstants.INDEX_PICTURE_SHARPNESS:
                mTvPictureManager.setPictureValue(EnumPictureMode.PICTURE_USER.ordinal(), mFragment.mHolder.mItemPictureMode.getCurValue(),
                        EnumVideoItem.MS_VIDEOITEM_SHARPNESS.ordinal(), resultValue);
                Log.i(TAG, "source " + mCurSourceType + ", choose picture sharpness --- picture mode : " + mFragment.mHolder.mItemPictureMode.getCurValue());
                Log.i(TAG, "source " + mCurSourceType + ", choose picture sharpness --- color temperature : " + mTvPictureManager.getColorTempratureIdx());
                Log.i(TAG, "source " + mCurSourceType + ", choose picture sharpness --- setvalue : " + resultValue);
                mFragment.mHolder.mItemPictureMode.setCurValue(EnumPictureMode.PICTURE_USER.ordinal());
                mFragment.mHolder.mItemColorTemperature.setCurValue(mTvPictureManager.getColorTempratureIdx());
                mColorTempExData = FactoryDB.getInstance(mFragment.getActivity()).queryFactoryColorTempExData(EnumInputSource.values()[mCurSourceType],
                        mFragment.mHolder.mItemColorTemperature.getCurValue());
                mFragment.mHolder.mItemRed.setCurValue(mColorTempExData.redGain / PictureConstants.COLOR_TEMPERATURE);
                mFragment.mHolder.mItemGreen.setCurValue(mColorTempExData.greenGain / PictureConstants.COLOR_TEMPERATURE);
                mFragment.mHolder.mItemBlue.setCurValue(mColorTempExData.blueGain / PictureConstants.COLOR_TEMPERATURE);
                mFragment.mHolder.mHandler.sendEmptyMessage(PictureConstants.MSG_UPDATEVIEW_PICTURE_HUE);
                break;
            case PictureConstants.INDEX_PICTURE_SATURATION:
                mTvPictureManager.setPictureValue(EnumPictureMode.PICTURE_USER.ordinal(), mFragment.mHolder.mItemPictureMode.getCurValue(),
                        EnumVideoItem.MS_VIDEOITEM_SATURATION.ordinal(), resultValue);
                Log.i(TAG, "source " + mCurSourceType + ", choose picture saturation --- picture mode : " + mFragment.mHolder.mItemPictureMode.getCurValue());
                Log.i(TAG, "source " + mCurSourceType + ", choose picture saturation --- color temperature : " + mTvPictureManager.getColorTempratureIdx());
                Log.i(TAG, "source " + mCurSourceType + ", choose picture saturation --- setvalue : " + resultValue);
                mFragment.mHolder.mItemPictureMode.setCurValue(EnumPictureMode.PICTURE_USER.ordinal());
                mFragment.mHolder.mItemColorTemperature.setCurValue(mTvPictureManager.getColorTempratureIdx());
                mColorTempExData = FactoryDB.getInstance(mFragment.getActivity()).queryFactoryColorTempExData(EnumInputSource.values()[mCurSourceType],
                        mFragment.mHolder.mItemColorTemperature.getCurValue());
                mFragment.mHolder.mItemRed.setCurValue(mColorTempExData.redGain / PictureConstants.COLOR_TEMPERATURE);
                mFragment.mHolder.mItemGreen.setCurValue(mColorTempExData.greenGain / PictureConstants.COLOR_TEMPERATURE);
                mFragment.mHolder.mItemBlue.setCurValue(mColorTempExData.blueGain / PictureConstants.COLOR_TEMPERATURE);
                mFragment.mHolder.mHandler.sendEmptyMessage(PictureConstants.MSG_UPDATEVIEW_PICTURE_SATURATION);
                break;
            case PictureConstants.INDEX_PICTURE_BACKLIGHT:
                // back light.
                mTvPictureManager.setPictureValue(EnumPictureMode.PICTURE_USER.ordinal(), mFragment.mHolder.mItemPictureMode.getCurValue(), 5,
                        resultValue);
                Log.i(TAG, "source " + mCurSourceType + ", choose picture backlight --- picture mode : " + mFragment.mHolder.mItemPictureMode.getCurValue());
                Log.i(TAG, "source " + mCurSourceType + ", choose picture backlight --- color temperature : " + mTvPictureManager.getColorTempratureIdx());
                Log.i(TAG, "source " + mCurSourceType + ", choose picture backlight --- setvalue : " + resultValue);
                mFragment.mHolder.mItemPictureMode.setCurValue(EnumPictureMode.PICTURE_USER.ordinal());
//                mFragment.mHolder.mItemColorTemperature.setCurValue(mTvPictureManager.getColorTempIdx().getValue());
                mFragment.mHolder.mItemColorTemperature.setCurValue(mTvPictureManager.getColorTempratureIdx());
                mColorTempExData = FactoryDB.getInstance(mFragment.getActivity()).queryFactoryColorTempExData(EnumInputSource.values()[mCurSourceType],
                        mFragment.mHolder.mItemColorTemperature.getCurValue());
                mFragment.mHolder.mItemRed.setCurValue(mColorTempExData.redGain / PictureConstants.COLOR_TEMPERATURE);
                mFragment.mHolder.mItemGreen.setCurValue(mColorTempExData.greenGain / PictureConstants.COLOR_TEMPERATURE);
                mFragment.mHolder.mItemBlue.setCurValue(mColorTempExData.blueGain / PictureConstants.COLOR_TEMPERATURE);
                mFragment.mHolder.mHandler.sendEmptyMessage(PictureConstants.MSG_UPDATEVIEW_PICTURE_BACKLIGHT);
                break;
            case PictureConstants.INDEX_PICTURE_TEMPERATURE:
                // color temperature.
                mTvPictureManager.setColorTempIdxAndRGB(mFragment.mHolder.mItemPictureMode.getCurValue(), resultValue, mCurSourceType);
                Log.i(TAG, "source " + mCurSourceType + ", choose color temperature --- color temperature : " + mTvPictureManager.getColorTempratureIdx());
                Log.i(TAG, "source " + mCurSourceType + ", choose color temperature --- setvalue : " + resultValue);
                mColorTempExData = FactoryDB.getInstance(mFragment.getActivity()).queryFactoryColorTempExData(EnumInputSource.values()[mCurSourceType],
                        mFragment.mHolder.mItemColorTemperature.getCurValue());
                mFragment.mHolder.mItemRed.setCurValue(mColorTempExData.redGain / PictureConstants.COLOR_TEMPERATURE);
                mFragment.mHolder.mItemGreen.setCurValue(mColorTempExData.greenGain / PictureConstants.COLOR_TEMPERATURE);
                mFragment.mHolder.mItemBlue.setCurValue(mColorTempExData.blueGain / PictureConstants.COLOR_TEMPERATURE);
                mFragment.mHolder.mHandler.sendEmptyMessage(PictureConstants.MSG_UPDATEVIEW_PICTURE_TEMPERATURE);
                break;
            case PictureConstants.INDEX_RED:
                if (mFragment.mHolder.mItemColorTemperature.getCurValue() != EnumColorTemperature.E_COLOR_TEMP_USER.getValue()) {
                    mTvPictureManager.setColorTempratureIdx(TvPictureManager.COLOR_TEMP_USER1);
                    mFragment.mHolder.mItemColorTemperature.setCurValue(EnumColorTemperature.E_COLOR_TEMP_USER.getValue());
                }
                mColorTempExData.redGain = resultValue * PictureConstants.COLOR_TEMPERATURE;
                mTvPictureManager.setWbGainOffsetEx(mColorTempExData, EnumColorTemperature.E_COLOR_TEMP_USER.getValue(),
                        mCurSourceType);
                mFragment.mHolder.mHandler.sendEmptyMessage(PictureConstants.MSG_UPDATEVIEW_RED);
                break;
            case PictureConstants.INDEX_GREEN:
                if (mFragment.mHolder.mItemColorTemperature.getCurValue() != EnumColorTemperature.E_COLOR_TEMP_USER.getValue()) {
                    mTvPictureManager.setColorTempratureIdx(TvPictureManager.COLOR_TEMP_USER1);
                    mFragment.mHolder.mItemColorTemperature.setCurValue(EnumColorTemperature.E_COLOR_TEMP_USER.getValue());
                }
                mColorTempExData.greenGain = resultValue * PictureConstants.COLOR_TEMPERATURE;
                mTvPictureManager.setWbGainOffsetEx(mColorTempExData, EnumColorTemperature.E_COLOR_TEMP_USER.getValue(),
                        mCurSourceType);
                mFragment.mHolder.mHandler.sendEmptyMessage(PictureConstants.MSG_UPDATEVIEW_GREEN);
                break;
            case PictureConstants.INDEX_BLUE:
                if (mFragment.mHolder.mItemColorTemperature.getCurValue() != EnumColorTemperature.E_COLOR_TEMP_USER.getValue()) {
                    mTvPictureManager.setColorTempratureIdx(TvPictureManager.COLOR_TEMP_USER1);
                    mFragment.mHolder.mItemColorTemperature.setCurValue(EnumColorTemperature.E_COLOR_TEMP_USER.getValue());
                }
                mColorTempExData.blueGain = resultValue * PictureConstants.COLOR_TEMPERATURE;
                mTvPictureManager.setWbGainOffsetEx(mColorTempExData, EnumColorTemperature.E_COLOR_TEMP_USER.getValue(),
                        mCurSourceType);
                mFragment.mHolder.mHandler.sendEmptyMessage(PictureConstants.MSG_UPDATEVIEW_BLUE);
                break;
            case PictureConstants.INDEX_HDMI_FULL:
                setHdmiUnderScan(resultValue);
                resetZoomMode();
                mFragment.mHolder.mHandler.sendEmptyMessage(PictureConstants.MSG_UPDATEVIEW_HDMI_FULL);
                break;
            case PictureConstants.INDEX_ZOOM_MODE:
                switch (resultValue) {
                    case 0:
                        mTvPictureManager.setVideoArcType(TvPictureManager.VIDEO_ARC_16x9);
                        setTVWindow(0);
                        break;
                    case 1:
                        mTvPictureManager.setVideoArcType(TvPictureManager.VIDEO_ARC_4x3);
                        setTVWindow(1);
                        break;
                    case 2:
                        mTvPictureManager.setVideoArcType(TvPictureManager.VIDEO_ARC_AUTO);
                        setTVWindow(2);
                        break;
                    case 3:
                        mTvPictureManager.setVideoArcType(TvPictureManager.VIDEO_ARC_PANORAMA);
                        setTVWindow(3);
                        break;
                    case 4:
                        mTvPictureManager.setVideoArcType(TvPictureManager.VIDEO_ARC_DEFAULT);
                        setTVWindow(6);
                        break;
                    default:
                        break;
                }
                break;
            case PictureConstants.INDEX_IMAGE_NOISE_REDUCTION:
                mTvPictureManager.setNR(EnumNoiseReduction.values()[EnumNoiseReduction.getOrdinalThroughValue(resultValue)]);
                Log.i(TAG, "source " + mCurSourceType + ", choose image noise reduction --- setvalue : " + resultValue);
                break;
            case PictureConstants.INDEX_MPEG_NOISE_REDUCTION:
                setMpegNR(EN_MS_MPEG_NR.values()[resultValue]);
                break;
            case PictureConstants.INDEX_OSD_TRANSPARENCY:
                setOSDTransparency(resultValue);
                break;
            default:
                break;
        }
    }
    
    void initVideoPara() {
        mVideoPara = new VideoDataSet();
        mVideoPara.CheckSum = CHECKSUM_COLER;
        mVideoPara.ePicture = EN_MS_PICTURE.PICTURE_NORMAL;
        int count = EN_MS_PICTURE.PICTURE_NUMS.ordinal();
        mVideoPara.astPicture = new T_MS_PICTURE[count];
        for (int i = 0; i < count; i++) {

            mVideoPara.astPicture[i] = new T_MS_PICTURE((short) 50, (short) 50, (short) 50, (short) 50, (short) 50,
                    (short) 50, EN_MS_COLOR_TEMP.MS_COLOR_TEMP_NATURE);
        }
        count = EN_MS_COLOR_TEMP.MS_COLOR_TEMP_NUM.ordinal();
        mVideoPara.eNRMode = new T_MS_NR_MODE[count];
        for (int i = 0; i < count; i++) {
            mVideoPara.eNRMode[i] = new T_MS_NR_MODE(EN_MS_NR.MS_NR_MIDDLE, EN_MS_MPEG_NR.MS_MPEG_NR_MIDDLE);
        }
    }

    void queryVideoPara(int inputSrcType) {
        // query tbl_VideoSetting for T_MS_VIDEO videopara base info and for
        // T_MS_SUB_COLOR g_astSubColor of videoPara
        Cursor cursorVideo = mFragment.getActivity().getContentResolver().query(
                Uri.parse(PictureConstants.CURSORVIDEO + inputSrcType), null, null, null,
                null);
        while (cursorVideo.moveToNext()) {
            // videopara base info
            mVideoPara.ePicture = EN_MS_PICTURE.values()[cursorVideo.getInt(cursorVideo.getColumnIndex(PictureConstants.EPICTURE))];
        }
        cursorVideo.close();
        // query tbl_PicMode_Setting for T_MS_PICTURE astPicture[] of videoPara
        Cursor cursorPicMode = mFragment.getActivity().getContentResolver().query(
                Uri.parse(PictureConstants.CURSORPICMODE), null, "InputSrcType = " + inputSrcType,
                null, PictureConstants.PICTUREMODETYPE);
        int picModeIdx = 0;
        int length = mVideoPara.astPicture.length;
        while (cursorPicMode.moveToNext()) {
            if (picModeIdx > length - 1) {
                break;
            }
            mVideoPara.astPicture[picModeIdx].eColorTemp = EN_MS_COLOR_TEMP.values()[cursorPicMode.getInt(cursorPicMode
                    .getColumnIndex(PictureConstants.ECOLORTEMP))];
            picModeIdx++;
        }
        cursorPicMode.close();
        // query tbl_NRMode for T_MS_NR_MODE eNRMode[] of videoPara
        Cursor cursorNRMode = mFragment.getActivity().getContentResolver().query(
                Uri.parse(PictureConstants.CURSORNRMODE), null, "InputSrcType = " + inputSrcType, null,
                PictureConstants.NRMODE);
        int NRModeIdx = 0;
        int length1 = mVideoPara.eNRMode.length;
        while (cursorNRMode.moveToNext()) {
            if (NRModeIdx > length1 - 1) {
                break;
            }
            mVideoPara.eNRMode[NRModeIdx].eNR = VideoDataSet.EN_MS_NR.values()[cursorNRMode.getInt(cursorNRMode
                    .getColumnIndex(PictureConstants.ENR))];
            mVideoPara.eNRMode[NRModeIdx].eMPEG_NR = VideoDataSet.EN_MS_MPEG_NR.values()[cursorNRMode
                    .getInt(cursorNRMode.getColumnIndex(PictureConstants.EMPEG_NR))];
            NRModeIdx++;
        }
        cursorNRMode.close();
    }

    EN_MS_MPEG_NR getMpegNR() {
        int idx;
        idx = mVideoPara.ePicture.ordinal();
        idx = mVideoPara.astPicture[idx].eColorTemp.ordinal();
        return mVideoPara.eNRMode[idx].eMPEG_NR;
    }

    void setMpegNR(EN_MS_MPEG_NR eMpNRIdx) {
        int idx;
        EnumMpegNoiseReduction mpegnrType = EnumMpegNoiseReduction.E_MPEG_NR_OFF;
        idx = mVideoPara.ePicture.ordinal();
        idx = mVideoPara.astPicture[idx].eColorTemp.ordinal();
        // com.printfE("TvService", "SetMpegNR nothing to do!!");
        mVideoPara.eNRMode[idx].eMPEG_NR = eMpNRIdx;
        updateVideoNRMode(mVideoPara.eNRMode[idx], mCurSourceType, idx);

        switch (eMpNRIdx) {
            case MS_MPEG_NR_OFF:
                mpegnrType = EnumMpegNoiseReduction.E_MPEG_NR_OFF;
                break;
            case MS_MPEG_NR_LOW:
                mpegnrType = EnumMpegNoiseReduction.E_MPEG_NR_LOW;
                break;
            case MS_MPEG_NR_MIDDLE:
                mpegnrType = EnumMpegNoiseReduction.E_MPEG_NR_MIDDLE;
                break;
            case MS_MPEG_NR_HIGH:
                mpegnrType = EnumMpegNoiseReduction.E_MPEG_NR_HIGH;
                break;
            case MS_MPEG_NR_NUM:
                mpegnrType = EnumMpegNoiseReduction.E_MPEG_NR_NUM;
                break;
            default:
                break;
        }
        try {
            if (TvManager.getInstance() != null) {
                TvManager.getInstance().getPictureManager().setMpegNoiseReduction(mpegnrType);
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    void updateVideoNRMode(T_MS_NR_MODE model, int inputSrcType, int NRModeIdx) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put(PictureConstants.ENR, mFragment.mHolder.mItemImageNoiseReduction.getCurValue());
        vals.put(PictureConstants.EMPEG_NR, model.eMPEG_NR.ordinal());
        try {
            ret = mFragment.getActivity().getContentResolver()
                    .update(Uri.parse(PictureConstants.RETSORNRMODE + NRModeIdx + PictureConstants.INPUTSRC
                            + inputSrcType), vals, null, null);
        } catch (SQLException e) {
        }
        if (ret == -1) {
            System.out.println(PictureConstants.UPDATE_TBL_NRMODE_IGNORED);
        }
        try {
            if (TvManager.getInstance() != null) {
                TvManager.getInstance().getDatabaseManager().setDatabaseDirtyByApplication(PictureConstants.T_NRMode_IDX);
            }

        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    void updateColorRange(short value) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put(PictureConstants.U8COLORRANGEMODE, value);
        try {
            ret = mFragment.getActivity().getContentResolver().update(Uri.parse(PictureConstants.SYSTEMSETTING),
                    vals, null, null);
        } catch (SQLException e) {
        }
        if (ret == -1) {
            System.out.println(PictureConstants.UPDATE_TBL_SYSTEMSETTING_IGNORED);
        }

        try {
            TvManager.getInstance().getDatabaseManager().setDatabaseDirtyByApplication(PictureConstants.T_SystemSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        ExTvChannelManager.getInstance().SetColorRanger(value);
    }

    void setHdmiUnderScan(int valuse) {
        Settings.System.putInt(mFragment.getActivity().getContentResolver(), PictureConstants.HDMIUNDERSCAN, valuse);
    }

    int getHdmiUnderScan() {
        return Settings.System.getInt(mFragment.getActivity().getContentResolver(), PictureConstants.HDMIUNDERSCAN, 0);
    }
    
    int getHdmiRgbScan() {
        int curindex = (int)TvPictureManager.getInstance().getColorRange();
        Log.d(TAG, "curindex:"+curindex);
        return curindex;
    }

    void resetZoomMode() {
        if (mCurSourceType >= TvCommonManager.INPUT_SOURCE_HDMI
                && mCurSourceType < TvCommonManager.INPUT_SOURCE_HDMI_MAX) {
            if (mFragment.mHolder.mItemHdmiFull.getCurValue() == 2) {
                mFragment.mHolder.mItemZoomMode.setFocusable(false);
            } else {
                mFragment.mHolder.mItemZoomMode.setFocusable(true);
            }
            if (mFragment.mHolder.mItemHdmiFull.getCurValue() == 2) {
                if (mFragment.mHolder.mItemZoomMode.getCurValue() != 4) {
                    mFragment.mHolder.mItemZoomMode.setCurValue(4);
                    mTvPictureManager.setVideoArcType(TvPictureManager.VIDEO_ARC_DEFAULT);
                    setTVWindow(6);
                }
            } else if (mFragment.mHolder.mItemHdmiFull.getCurValue() == 0) {
                if (TvManager.getInstance().getPlayerManager().isHdmiMode()) {
                    if (mFragment.mHolder.mItemZoomMode.getCurValue() != 0) {
                        mFragment.mHolder.mItemZoomMode.setCurValue(0);
                        mTvPictureManager.setVideoArcType(TvPictureManager.VIDEO_ARC_16x9);
                        setTVWindow(0);
                    }
                }
            }
        }
    }
    
    /**
     * set OSD Transparency
     * @param OSDTransparencyVal
     */
    void setOSDTransparency(int OSDTransparencyVal) {
        Drawable content = mFragment.getActivity().getResources().getDrawable(R.drawable.content_bg);
        content.setAlpha(OSDTransparencyVal * 25);
        FrameLayout frameLayout = (FrameLayout)mFragment.getActivity().findViewById(R.id.content_setting);
        frameLayout.setBackground(content);
        
//        Drawable title = mFragment.getActivity().getResources().getDrawable(R.drawable.titlebar_bg2);
//        title.setAlpha(mOSDTransparencyVal * 25);
//        RelativeLayout relativeLayout = (RelativeLayout)mFragment.getActivity().findViewById(R.id.title_item_rl1);
//        frameLayout.setBackground(title);
    }

    void setTVWindow(int zoomMode) {
        FactoryDeskImpl impl = FactoryDeskImpl.getInstance(mFragment.getActivity());;
        switch (zoomMode) {
            case 0:
            case 1:
            case 2:
            case 3: {
                switch (mCurSourceType) {
                    case TvCommonManager.INPUT_SOURCE_ATV:
                    case TvCommonManager.INPUT_SOURCE_CVBS:
                    case TvCommonManager.INPUT_SOURCE_YPBPR:
                        impl.setOverScan((short) 40, (short) 40, (short) 40, (short) 38);
                        break;
                    case TvCommonManager.INPUT_SOURCE_DTV:
                    case TvCommonManager.INPUT_SOURCE_HDMI:
                    case TvCommonManager.INPUT_SOURCE_HDMI4:
                        impl.setOverScan((short) 20, (short) 20, (short) 20, (short) 20);
                        break;
                    default:
                        impl.setOverScan((short) 0, (short) 0, (short) 0, (short) 0);
                        break;
                }
                break;
            }
            case 4: {
                switch (mCurSourceType) {
                    case TvCommonManager.INPUT_SOURCE_ATV:
                    case TvCommonManager.INPUT_SOURCE_CVBS:
                    case TvCommonManager.INPUT_SOURCE_YPBPR:
                    case TvCommonManager.INPUT_SOURCE_DTV:
                    case TvCommonManager.INPUT_SOURCE_HDMI:
                    case TvCommonManager.INPUT_SOURCE_HDMI4:
                        impl.setOverScan((short) 26, (short) 22, (short) 26, (short) 26);
                        break;
                    default:
                        impl.setOverScan((short) 0, (short) 0, (short) 0, (short) 0);
                        break;
                }
                break;
            }
            case 5: {
                switch (mCurSourceType) {
                    case TvCommonManager.INPUT_SOURCE_ATV:
                    case TvCommonManager.INPUT_SOURCE_CVBS:
                    case TvCommonManager.INPUT_SOURCE_YPBPR:
                        impl.setOverScan((short) 40, (short) 0, (short) 40, (short) 38);
                        break;
                    case TvCommonManager.INPUT_SOURCE_DTV:
                    case TvCommonManager.INPUT_SOURCE_HDMI:
                    case TvCommonManager.INPUT_SOURCE_HDMI4:
                        impl.setOverScan((short) 20, (short) 0, (short) 20, (short) 20);
                        break;
                    default:
                        impl.setOverScan((short) 0, (short) 0, (short) 0, (short) 0);
                        break;
                }
                break;
            }
            case 6: {
                impl.setOverScan((short) 0, (short) 0, (short) 0, (short) 0);
                break;
            }
            default:
                break;
        }
    }
}
