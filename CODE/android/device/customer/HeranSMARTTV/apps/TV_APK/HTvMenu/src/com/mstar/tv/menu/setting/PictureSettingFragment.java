
package com.mstar.tv.menu.setting;

import java.util.ArrayList;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;

import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvPictureManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.ColorTemperatureExData;
import com.mstar.android.tvapi.common.vo.EnumColorTemperature;
import com.mstar.android.tvapi.common.vo.EnumPictureMode;
import com.mstar.android.tvapi.common.vo.EnumVideoArcType;
import com.mstar.android.tvapi.common.vo.EnumVideoItem;
import com.mstar.android.tvapi.common.vo.MpegNoiseReduction.EnumMpegNoiseReduction;
import com.mstar.android.tvapi.common.vo.NoiseReduction.EnumNoiseReduction;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.mstar.tv.ExTvChannelManager;
import com.mstar.tv.FactoryDB;
import com.mstar.tv.FactoryDeskImpl;
import com.mstar.tv.MenuConstants;
import com.mstar.tv.menu.R;
import com.mstar.tv.menu.setting.DatabaseDesk.EN_MS_COLOR_TEMP;
import com.mstar.tv.menu.setting.DatabaseDesk.EN_MS_MPEG_NR;
import com.mstar.tv.menu.setting.DatabaseDesk.EN_MS_NR;
import com.mstar.tv.menu.setting.DatabaseDesk.EN_MS_PICTURE;
import com.mstar.tv.menu.setting.DatabaseDesk.T_MS_NR_MODE;
import com.mstar.tv.menu.setting.DatabaseDesk.T_MS_PICTURE;
import com.mstar.tv.menu.setting.util.Tools;

/**
 * @projectName： EOSTVMenu
 * @moduleName： PictureSettingFragment.java
 * @author jachensy.chen
 * @version 1.0.0
 * @time 2013-12-16
 * @Copyright © 2013 MStar Semiconductor, Inc.
 */
public class PictureSettingFragment extends PublicFragement {

    private TvPictureManager manager;

    private DatabaseDesk videopara;

    private EnumPictureMode pictureMode;

    private EnumInputSource curSourceType = EnumInputSource.E_INPUT_SOURCE_NONE;

    public final static short T_NRMode_IDX = 0x0E;

    public final static short T_SystemSetting_IDX = 0x19;

    private final static int COLORTEMP = 16;

    private EosSettingItem itemPictureMode = null;

    private EosSettingItem itemPictureContrast = null;

    private EosSettingItem itemPictureBrightness = null;

    private EosSettingItem itemPictureHue = null;

    private EosSettingItem itemPictureSharpness = null;

    private EosSettingItem itemPictureSaturation = null;

    private EosSettingItem itemPictureBackLight = null;

    private EosSettingItem itemColorTemperature = null;

    private EosSettingItem itemRed = null;

    private EosSettingItem itemGreen = null;

    private EosSettingItem itemBlue = null;

    private EosSettingItem itemHdmiFull = null;

    private EosSettingItem itemZoomMode = null;

    private EosSettingItem itemImageNoiseReduction = null;

    private EosSettingItem itemMpegNoiseReduction = null;

    private EosSettingItem itemLowBlueMode = null;

    private FactoryDeskImpl impl = null;

    private ColorTemperatureExData colorTempExData = null;

    @Override
    protected void initItems() {
        setTag("pic");
        manager = TvPictureManager.getInstance();
        curSourceType = TvCommonManager.getInstance().getCurrentInputSource();
        impl = FactoryDeskImpl.getInstance(getActivity());
        mItems = new ArrayList<EosSettingItem>();
        initVideoPara();
        String pictureEosSettingmItems[] = getResources().getStringArray(R.array.setting_picture);
        /* picture mode */
        String initArray[] = getResources().getStringArray(R.array.setting_picturemode_vals);
        itemPictureMode = new EosSettingItem(this, pictureEosSettingmItems[0], initArray, 0,
                MenuConstants.ITEMTYPE_ENUM, true);
        mItems.add(itemPictureMode);
        /* contrast */
        itemPictureContrast = new EosSettingItem(this, getResources().getStringArray(R.array.setting_picture)[1], 0,
                100, 0, MenuConstants.ITEMTYPE_DIGITAL, true);
        mItems.add(itemPictureContrast);
        /* brightness */
        itemPictureBrightness = new EosSettingItem(this, getResources().getStringArray(R.array.setting_picture)[2], 0,
                100, 0, MenuConstants.ITEMTYPE_DIGITAL, true);
        mItems.add(itemPictureBrightness);
        /* hue */
        itemPictureHue = new EosSettingItem(this, getResources().getStringArray(R.array.setting_picture)[3], 0, 100, 0,
                MenuConstants.ITEMTYPE_DIGITAL, true);
        mItems.add(itemPictureHue);
        /* sharpness */
        itemPictureSharpness = new EosSettingItem(this, getResources().getStringArray(R.array.setting_picture)[4], 0,
                100, 0, MenuConstants.ITEMTYPE_DIGITAL, true);
        mItems.add(itemPictureSharpness);
        /* saturation */
        itemPictureSaturation = new EosSettingItem(this, getResources().getStringArray(R.array.setting_picture)[5], 0,
                100, 0, MenuConstants.ITEMTYPE_DIGITAL, true);
        mItems.add(itemPictureSaturation);
        /* back light */
        itemPictureBackLight = new EosSettingItem(this, getResources().getStringArray(R.array.setting_picture)[6], 0,
                100, 0, MenuConstants.ITEMTYPE_DIGITAL, true);
        mItems.add(itemPictureBackLight);
        /* color temperature */
        initArray = getResources().getStringArray(R.array.setting_colortemperature_vals);
        itemColorTemperature = new EosSettingItem(this, pictureEosSettingmItems[7], initArray, 0,
                MenuConstants.ITEMTYPE_ENUM, true);
        mItems.add(itemColorTemperature);
        itemRed = new EosSettingItem(this, getResources().getStringArray(R.array.setting_picture)[8], 0, 128, 0,
                MenuConstants.ITEMTYPE_DIGITAL, true);
        mItems.add(itemRed);
        itemGreen = new EosSettingItem(this, getResources().getStringArray(R.array.setting_picture)[9], 0, 128, 0,
                MenuConstants.ITEMTYPE_DIGITAL, true);
        mItems.add(itemGreen);
        itemBlue = new EosSettingItem(this, getResources().getStringArray(R.array.setting_picture)[10], 0, 128, 0,
                MenuConstants.ITEMTYPE_DIGITAL, true);
        mItems.add(itemBlue);

        initArray = getResources().getStringArray(R.array.setting_hdmifull_vals);
        itemHdmiFull = new EosSettingItem(this, pictureEosSettingmItems[11], initArray, 0, MenuConstants.ITEMTYPE_ENUM,
                false);
        if (curSourceType.ordinal() >= EnumInputSource.E_INPUT_SOURCE_HDMI.ordinal()
                && curSourceType.ordinal() < EnumInputSource.E_INPUT_SOURCE_HDMI_MAX.ordinal()) {
            itemHdmiFull.setFocusable(true);
        }
        mItems.add(itemHdmiFull);
        /* zoom mode */
        if (curSourceType == EnumInputSource.E_INPUT_SOURCE_ATV || curSourceType == EnumInputSource.E_INPUT_SOURCE_DTV) {
            initArray = getResources().getStringArray(R.array.setting_zoommode_vals_tv);
        } else {
            initArray = getResources().getStringArray(R.array.setting_zoommode_vals);
        }
        itemZoomMode = new EosSettingItem(this, pictureEosSettingmItems[12], initArray, 0, MenuConstants.ITEMTYPE_ENUM,
                true);
        if (curSourceType.ordinal() == EnumInputSource.E_INPUT_SOURCE_VGA.ordinal()) {
            itemZoomMode.setFocusable(false);
        }
        mItems.add(itemZoomMode);
        /* image noise reduction */
        initArray = getResources().getStringArray(R.array.setting_imgnoisereduction_vals);
        itemImageNoiseReduction = new EosSettingItem(this, pictureEosSettingmItems[13], initArray, 0,
                MenuConstants.ITEMTYPE_ENUM, true);
        mItems.add(itemImageNoiseReduction);
        /* MpegNoiseReduction */
        initArray = getResources().getStringArray(R.array.setting_mpegnoisereduction_vals);
        itemMpegNoiseReduction = new EosSettingItem(this, pictureEosSettingmItems[14], initArray, 0,
                MenuConstants.ITEMTYPE_ENUM, true);
        mItems.add(itemMpegNoiseReduction);
        /* Color Range */
        initArray = getResources().getStringArray(R.array.setting_set_colorrangetype);
        itemLowBlueMode = new EosSettingItem(this, pictureEosSettingmItems[16], MenuConstants.ITEMTYPE_BUTTON, true);
        //低蓝护眼模式功能在sys.ini里面加了配置开关
        String lowBlueState = Tools.readSysIni("LowBlueMode");    
        if(lowBlueState.equals("1")||lowBlueState.equals(1)){
            mItems.add(itemLowBlueMode);
        }
      
        initDate();
    }

    @Override
    protected void initDate() {
        /* picture mode */
        pictureMode = manager.getPictureModeIdx();
        int initValue = pictureMode.ordinal();
        // menu only support dynamic,normal,soft,user
        if (initValue > 3) {
            Log.v("PictureMode", "initValue = " + initValue + ";should to reset to 0");
            initValue = 0;
        }
        itemPictureMode.setCurValue(initValue);
        // values[0]:Brightness,values[1]:Contrast,values[2]:Hue,values[3]:Saturation,values[4]:Sharpness,values[5]:Backlight
        int[] values = manager.getVideoItems(curSourceType, pictureMode);
        itemPictureContrast.setCurValue(values[1]);
        itemPictureBrightness.setCurValue(values[0]);
        itemPictureHue.setCurValue(values[2]);
        itemPictureSharpness.setCurValue(values[4]);
        itemPictureSaturation.setCurValue(values[3]);
        itemPictureBackLight.setCurValue(values[5]);
        //itemPictureBackLight ,itemColorTemperature focus is from low blue mode.
        int status = android.provider.Settings.System.getInt(getActivity().getContentResolver(), MenuConstants.MODE, 0);
        boolean isFocus = (status == 0);
        itemPictureBackLight.setFocusable(isFocus);
        initValue = manager.getColorTempIdx().getValue();
        itemColorTemperature.setCurValue(initValue);
        itemColorTemperature.setFocusable(isFocus);

        colorTempExData = manager.getWbGainOffsetEx(initValue, curSourceType.ordinal());
        itemRed.setCurValue(colorTempExData.redGain / COLORTEMP);
        itemGreen.setCurValue(colorTempExData.greenGain / COLORTEMP);
        itemBlue.setCurValue(colorTempExData.blueGain / COLORTEMP);
        itemHdmiFull.setCurValue(getHdmiUnderScan());

        /* zoom mode */
        EnumVideoArcType type = manager.getVideoArc();
        switch (type) {
            case E_16x9:
                initValue = 0;
                break;
            case E_4x3:
                initValue = 1;
                break;
            case E_AUTO:
                initValue = 2;
                break;
            case E_Panorama:
                initValue = 3;
                break;
            // case E_Zoom1:
            // initValue = 4;
            // break;
            // case E_Zoom2:
            // initValue = 5;
            // break;
            case E_DEFAULT:
                if (curSourceType == EnumInputSource.E_INPUT_SOURCE_ATV
                        || curSourceType == EnumInputSource.E_INPUT_SOURCE_DTV) {
                    manager.setVideoArc(EnumVideoArcType.E_16x9);
                    initValue = 0;
                } else {
                    initValue = 4;
                }
                break;
            default:
                manager.setVideoArc(EnumVideoArcType.E_16x9);
                initValue = 0;
                break;
        }
        if (curSourceType == EnumInputSource.E_INPUT_SOURCE_STORAGE) {
            itemZoomMode.setFocusable(false);
        }
        itemZoomMode.setCurValue(initValue);
        // resetZoomMode();

        if (itemHdmiFull.getCurValue() == 2) {
            if (curSourceType.ordinal() >= EnumInputSource.E_INPUT_SOURCE_HDMI.ordinal()
                    && curSourceType.ordinal() < EnumInputSource.E_INPUT_SOURCE_HDMI_MAX.ordinal()) {
                itemZoomMode.setFocusable(false);
            }
            // manager.setVideoArc(EnumVideoArcType.E_DEFAULT);
            // setTVWindow(6);
        }

        /* image noise reduction */
        initValue = manager.getNR().getValue();
        itemImageNoiseReduction.setCurValue(initValue);

//        queryVideoPara(curSourceType.ordinal());
//        EN_MS_MPEG_NR mpegNR = getMpegNR();
        int value = TvPictureManager.getInstance().getMpegNoiseReduction();
        itemMpegNoiseReduction.setCurValue(value);
        mAdapter.setHasShowValue(true);
    }

    @Override
    void callBack(int resultValue, int position) {
        if (mItems.size() <= 0) {
            return;
        }
        switch (position) {
            case 0: {
                manager.setPictureModeIdx(EnumPictureMode.values()[resultValue]);
                int[] values = manager.getVideoItems(curSourceType, EnumPictureMode.values()[resultValue]);
                mItems.get(1).setCurValue(values[1]);
                mItems.get(2).setCurValue(values[0]);
                mItems.get(3).setCurValue(values[2]);
                mItems.get(4).setCurValue(values[4]);
                mItems.get(5).setCurValue(values[3]);
                mItems.get(6).setCurValue(values[5]);
                mItems.get(7).setCurValue(manager.getColorTempIdx().getValue());
                colorTempExData = manager.getWbGainOffsetEx(mItems.get(7).getCurValue(), curSourceType.ordinal());
                itemRed.setCurValue(colorTempExData.redGain / COLORTEMP);
                itemGreen.setCurValue(colorTempExData.greenGain / COLORTEMP);
                itemBlue.setCurValue(colorTempExData.blueGain / COLORTEMP);
                break;
            }
            case 1:
                manager.setPictureValue(EnumPictureMode.PICTURE_USER.ordinal(), mItems.get(0).getCurValue(),
                        EnumVideoItem.MS_VIDEOITEM_CONTRAST.ordinal(), resultValue);
                mItems.get(0).setCurValue(EnumPictureMode.PICTURE_USER.ordinal());
                colorTempExData = manager.getWbGainOffsetEx(mItems.get(7).getCurValue(), curSourceType.ordinal());
                itemRed.setCurValue(colorTempExData.redGain / COLORTEMP);
                itemGreen.setCurValue(colorTempExData.greenGain / COLORTEMP);
                itemBlue.setCurValue(colorTempExData.blueGain / COLORTEMP);
                break;
            case 2:
                manager.setPictureValue(EnumPictureMode.PICTURE_USER.ordinal(), mItems.get(0).getCurValue(),
                        EnumVideoItem.MS_VIDEOITEM_BRIGHTNESS.ordinal(), resultValue);
                mItems.get(0).setCurValue(EnumPictureMode.PICTURE_USER.ordinal());
                mItems.get(7).setCurValue(manager.getColorTempIdx().getValue());
                colorTempExData = FactoryDB.getInstance(getActivity()).queryFactoryColorTempExData(curSourceType,
                        mItems.get(7).getCurValue());
                itemRed.setCurValue(colorTempExData.redGain / COLORTEMP);
                itemGreen.setCurValue(colorTempExData.greenGain / COLORTEMP);
                itemBlue.setCurValue(colorTempExData.blueGain / COLORTEMP);
                break;
            case 3:
                manager.setPictureValue(EnumPictureMode.PICTURE_USER.ordinal(), mItems.get(0).getCurValue(),
                        EnumVideoItem.MS_VIDEOITEM_HUE.ordinal(), resultValue);
                mItems.get(0).setCurValue(EnumPictureMode.PICTURE_USER.ordinal());
                mItems.get(7).setCurValue(manager.getColorTempIdx().getValue());
                colorTempExData = FactoryDB.getInstance(getActivity()).queryFactoryColorTempExData(curSourceType,
                        mItems.get(7).getCurValue());
                itemRed.setCurValue(colorTempExData.redGain / COLORTEMP);
                itemGreen.setCurValue(colorTempExData.greenGain / COLORTEMP);
                itemBlue.setCurValue(colorTempExData.blueGain / COLORTEMP);
                break;
            case 4:
                manager.setPictureValue(EnumPictureMode.PICTURE_USER.ordinal(), mItems.get(0).getCurValue(),
                        EnumVideoItem.MS_VIDEOITEM_SHARPNESS.ordinal(), resultValue);
                mItems.get(0).setCurValue(EnumPictureMode.PICTURE_USER.ordinal());
                mItems.get(7).setCurValue(manager.getColorTempIdx().getValue());
                colorTempExData = FactoryDB.getInstance(getActivity()).queryFactoryColorTempExData(curSourceType,
                        mItems.get(7).getCurValue());
                itemRed.setCurValue(colorTempExData.redGain / COLORTEMP);
                itemGreen.setCurValue(colorTempExData.greenGain / COLORTEMP);
                itemBlue.setCurValue(colorTempExData.blueGain / COLORTEMP);
                break;
            case 5:
                manager.setPictureValue(EnumPictureMode.PICTURE_USER.ordinal(), mItems.get(0).getCurValue(),
                        EnumVideoItem.MS_VIDEOITEM_SATURATION.ordinal(), resultValue);
                mItems.get(0).setCurValue(EnumPictureMode.PICTURE_USER.ordinal());
                mItems.get(7).setCurValue(manager.getColorTempIdx().getValue());
                mItems.get(7).setCurValue(manager.getColorTempIdx().getValue());
                colorTempExData = FactoryDB.getInstance(getActivity()).queryFactoryColorTempExData(curSourceType,
                        mItems.get(7).getCurValue());
                itemRed.setCurValue(colorTempExData.redGain / COLORTEMP);
                itemGreen.setCurValue(colorTempExData.greenGain / COLORTEMP);
                itemBlue.setCurValue(colorTempExData.blueGain / COLORTEMP);
                break;
            case 6:
                // back light.
                manager.setPictureValue(EnumPictureMode.PICTURE_USER.ordinal(), mItems.get(0).getCurValue(), 5,
                        resultValue);
                mItems.get(0).setCurValue(EnumPictureMode.PICTURE_USER.ordinal());
                mItems.get(7).setCurValue(manager.getColorTempIdx().getValue());
                colorTempExData = FactoryDB.getInstance(getActivity()).queryFactoryColorTempExData(curSourceType,
                        mItems.get(7).getCurValue());
                itemRed.setCurValue(colorTempExData.redGain / COLORTEMP);
                itemGreen.setCurValue(colorTempExData.greenGain / COLORTEMP);
                itemBlue.setCurValue(colorTempExData.blueGain / COLORTEMP);
                break;
            case 7:
                // color temperature.
                manager.setColorTempIdxAndRGB(mItems.get(0).getCurValue(), resultValue, curSourceType.ordinal());
                colorTempExData = FactoryDB.getInstance(getActivity()).queryFactoryColorTempExData(curSourceType,
                        mItems.get(7).getCurValue());
                itemRed.setCurValue(colorTempExData.redGain / COLORTEMP);
                itemGreen.setCurValue(colorTempExData.greenGain / COLORTEMP);
                itemBlue.setCurValue(colorTempExData.blueGain / COLORTEMP);
                break;
            case 8:
                if (itemColorTemperature.getCurValue() != EnumColorTemperature.E_COLOR_TEMP_USER.getValue()) {
                    manager.setColorTempIdx(EnumColorTemperature.E_COLOR_TEMP_USER);
                    itemColorTemperature.setCurValue(EnumColorTemperature.E_COLOR_TEMP_USER.getValue());
                }
                colorTempExData.redGain = resultValue * COLORTEMP;
                manager.setWbGainOffsetEx(colorTempExData, EnumColorTemperature.E_COLOR_TEMP_USER.getValue(),
                        curSourceType.ordinal());
                break;
            case 9:
                if (itemColorTemperature.getCurValue() != EnumColorTemperature.E_COLOR_TEMP_USER.getValue()) {
                    manager.setColorTempIdx(EnumColorTemperature.E_COLOR_TEMP_USER);
                    itemColorTemperature.setCurValue(EnumColorTemperature.E_COLOR_TEMP_USER.getValue());
                }
                colorTempExData.greenGain = resultValue * COLORTEMP;
                manager.setWbGainOffsetEx(colorTempExData, EnumColorTemperature.E_COLOR_TEMP_USER.getValue(),
                        curSourceType.ordinal());
                break;
            case 10:
                if (itemColorTemperature.getCurValue() != EnumColorTemperature.E_COLOR_TEMP_USER.getValue()) {
                    manager.setColorTempIdx(EnumColorTemperature.E_COLOR_TEMP_USER);
                    itemColorTemperature.setCurValue(EnumColorTemperature.E_COLOR_TEMP_USER.getValue());
                }
                colorTempExData.blueGain = resultValue * COLORTEMP;
                manager.setWbGainOffsetEx(colorTempExData, EnumColorTemperature.E_COLOR_TEMP_USER.getValue(),
                        curSourceType.ordinal());
                break;
            case 11:
                setHdmiUnderScan(resultValue);
                resetZoomMode();
                break;
            case 12: {
                // impl.setOverScanSourceType();
                switch (resultValue) {
                    case 0:
                        manager.setVideoArc(EnumVideoArcType.E_16x9);
                        setTVWindow(0);
                        break;
                    case 1:
                        manager.setVideoArc(EnumVideoArcType.E_4x3);
                        setTVWindow(1);
                        break;
                    case 2:
                        manager.setVideoArc(EnumVideoArcType.E_AUTO);
                        setTVWindow(2);
                        break;
                    case 3:
                        manager.setVideoArc(EnumVideoArcType.E_Panorama);
                        setTVWindow(3);
                        break;
                    case 4:
                        manager.setVideoArc(EnumVideoArcType.E_DEFAULT);
                        setTVWindow(6);
                        break;
                    default:
                        break;
                }
            }
                break;
            case 13:
                manager.setNR(EnumNoiseReduction.values()[EnumNoiseReduction.getOrdinalThroughValue(resultValue)]);
                break;
            case 14:
//                setMpegNR(EN_MS_MPEG_NR.values()[resultValue]);
                TvPictureManager.getInstance().setMpegNoiseReduction(resultValue);
                break;
            default:
                break;
        }
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    void callBack(Boolean resultVaule, int position) {
    }

    @Override
    void callBack(int position) {
        if (position == 15) {
            LowBlueModelFragmnet lowBlueModelFragmnet = new LowBlueModelFragmnet();
            FragmentManager fm = getFragmentManager();
            fm.popBackStack();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.eos_menu_anim_rightin, R.anim.eos_menu_anim_leftout);
            ft.replace(R.id.content_setting, lowBlueModelFragmnet);
            ft.commit();
        }
    }

    private void initVideoPara() {
        videopara = new DatabaseDesk();
        videopara.CheckSum = 0xFFFF;
        videopara.ePicture = EN_MS_PICTURE.PICTURE_NORMAL;
        int count = EN_MS_PICTURE.PICTURE_NUMS.ordinal();
        videopara.astPicture = new T_MS_PICTURE[count];
        for (int i = 0; i < count; i++) {

            videopara.astPicture[i] = new T_MS_PICTURE((short) 50, (short) 50, (short) 50, (short) 50, (short) 50,
                    (short) 50, EN_MS_COLOR_TEMP.MS_COLOR_TEMP_NATURE);
        }
        count = EN_MS_COLOR_TEMP.MS_COLOR_TEMP_NUM.ordinal();
        videopara.eNRMode = new T_MS_NR_MODE[count];
        for (int i = 0; i < count; i++) {
            videopara.eNRMode[i] = new T_MS_NR_MODE(EN_MS_NR.MS_NR_MIDDLE, EN_MS_MPEG_NR.MS_MPEG_NR_MIDDLE);
        }
    }

    public short getColorRange() {
        short colorRangeValues = -1;
        Cursor mcursor = getActivity().getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/systemsetting"), null, null, null, null);
        if (mcursor.moveToFirst()) {
            colorRangeValues = mcursor.getShort(mcursor.getColumnIndex("u8ColorRangeMode"));
        }
        mcursor.close();
        Log.v("xpf", "colorRangeValues=" + colorRangeValues);
        return colorRangeValues;
    }

    public void queryVideoPara(int inputSrcType) {
        // query tbl_VideoSetting for T_MS_VIDEO videopara base info and for
        // T_MS_SUB_COLOR g_astSubColor of videoPara
        Cursor cursorVideo = getActivity().getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/videosetting/inputsrc/" + inputSrcType), null, null, null,
                null);
        while (cursorVideo.moveToNext()) {
            // videopara base info
            videopara.ePicture = EN_MS_PICTURE.values()[cursorVideo.getInt(cursorVideo.getColumnIndex("ePicture"))];
        }
        cursorVideo.close();
        // query tbl_PicMode_Setting for T_MS_PICTURE astPicture[] of videoPara
        Cursor cursorPicMode = getActivity().getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/picmode_setting"), null, "InputSrcType = " + inputSrcType,
                null, "PictureModeType");
        int picModeIdx = 0;
        int length = videopara.astPicture.length;
        while (cursorPicMode.moveToNext()) {
            if (picModeIdx > length - 1) {
                break;
            }
            videopara.astPicture[picModeIdx].eColorTemp = EN_MS_COLOR_TEMP.values()[cursorPicMode.getInt(cursorPicMode
                    .getColumnIndex("eColorTemp"))];
            picModeIdx++;
        }
        cursorPicMode.close();
        // query tbl_NRMode for T_MS_NR_MODE eNRMode[] of videoPara
        Cursor cursorNRMode = getActivity().getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/nrmode"), null, "InputSrcType = " + inputSrcType, null,
                "NRMode");
        int NRModeIdx = 0;
        int length1 = videopara.eNRMode.length;
        while (cursorNRMode.moveToNext()) {
            if (NRModeIdx > length1 - 1) {
                break;
            }
            videopara.eNRMode[NRModeIdx].eNR = DatabaseDesk.EN_MS_NR.values()[cursorNRMode.getInt(cursorNRMode
                    .getColumnIndex("eNR"))];
            videopara.eNRMode[NRModeIdx].eMPEG_NR = DatabaseDesk.EN_MS_MPEG_NR.values()[cursorNRMode
                    .getInt(cursorNRMode.getColumnIndex("eMPEG_NR"))];
            NRModeIdx++;
        }
        cursorNRMode.close();
    }

    private EN_MS_MPEG_NR getMpegNR() {
        int idx;
        idx = videopara.ePicture.ordinal();
        idx = videopara.astPicture[idx].eColorTemp.ordinal();
        return videopara.eNRMode[idx].eMPEG_NR;
    }

    @Override
    boolean doKeyDownOrUp(int keyCode, KeyEvent event) {
        return false;
    }

    private void setMpegNR(EN_MS_MPEG_NR eMpNRIdx) {
        int idx;
        EnumMpegNoiseReduction mpegnrType = EnumMpegNoiseReduction.E_MPEG_NR_OFF;
        idx = videopara.ePicture.ordinal();
        idx = videopara.astPicture[idx].eColorTemp.ordinal();
        // com.printfE("TvService", "SetMpegNR nothing to do!!");
        videopara.eNRMode[idx].eMPEG_NR = eMpNRIdx;
        updateVideoNRMode(videopara.eNRMode[idx], curSourceType.ordinal(), idx);

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

    public void updateVideoNRMode(T_MS_NR_MODE model, int inputSrcType, int NRModeIdx) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("eNR", itemImageNoiseReduction.getCurValue());
        vals.put("eMPEG_NR", model.eMPEG_NR.ordinal());
        try {
            ret = getActivity().getContentResolver()
                    .update(Uri.parse("content://mstar.tv.usersetting/nrmode/nrmode/" + NRModeIdx + "/inputsrc/"
                            + inputSrcType), vals, null, null);
        } catch (SQLException e) {
        }
        if (ret == -1) {
            System.out.println("update tbl_NRMode ignored");
        }
        try {
            if (TvManager.getInstance() != null) {
                TvManager.getInstance().getDatabaseManager().setDatabaseDirtyByApplication(T_NRMode_IDX);
            }

        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    public void updateColorRange(short value) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("u8ColorRangeMode", value);
        try {
            ret = getActivity().getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/systemsetting"),
                    vals, null, null);
        } catch (SQLException e) {
        }
        if (ret == -1) {
            System.out.println("update tbl_systemsetting ignored");
        }

        try {
            TvManager.getInstance().getDatabaseManager().setDatabaseDirtyByApplication(T_SystemSetting_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        ExTvChannelManager.getInstance().SetColorRanger(value);
    }

    private void setHdmiUnderScan(int valuse) {
        Settings.System.putInt(getActivity().getContentResolver(), "hdmiunderscan", valuse);
    }

    private int getHdmiUnderScan() {
        return Settings.System.getInt(getActivity().getContentResolver(), "hdmiunderscan", 0);
    }

    private void resetZoomMode() {
        if (curSourceType.ordinal() >= EnumInputSource.E_INPUT_SOURCE_HDMI.ordinal()
                && curSourceType.ordinal() < EnumInputSource.E_INPUT_SOURCE_HDMI_MAX.ordinal()) {
            if (itemHdmiFull.getCurValue() == 2) {
                itemZoomMode.setFocusable(false);
            } else {
                itemZoomMode.setFocusable(true);
            }
            if (itemHdmiFull.getCurValue() == 2) {
                if (itemZoomMode.getCurValue() != 4) {
                    itemZoomMode.setCurValue(4);
                    manager.setVideoArc(EnumVideoArcType.E_DEFAULT);
                    setTVWindow(6);
                }
            } else if (itemHdmiFull.getCurValue() == 0) {
                if (TvManager.getInstance().getPlayerManager().isHdmiMode()) {
                    if (itemZoomMode.getCurValue() != 0) {
                        itemZoomMode.setCurValue(0);
                        manager.setVideoArc(EnumVideoArcType.E_16x9);
                        setTVWindow(0);
                    }
                }
            }
        }
    }

    @Override
    protected void updateView(int position) {
        if (mDialog != null) {
            mAdapter.notifyDataSetChanged();
            mDialog.setCurValue();
        }
    }

    private void setTVWindow(int zoomMode) {
        switch (zoomMode) {
            case 0:
            case 1:
            case 2:
            case 3: {
                switch (curSourceType) {
                    case E_INPUT_SOURCE_ATV:
                    case E_INPUT_SOURCE_CVBS:
                    case E_INPUT_SOURCE_YPBPR:
                        impl.setOverScan((short) 40, (short) 40, (short) 40, (short) 38);
                        break;
                    case E_INPUT_SOURCE_DTV:
                    case E_INPUT_SOURCE_HDMI:
                    case E_INPUT_SOURCE_HDMI2:
                    case E_INPUT_SOURCE_HDMI3:
                        impl.setOverScan((short) 20, (short) 20, (short) 20, (short) 20);
                        break;
                    default:
                        impl.setOverScan((short) 0, (short) 0, (short) 0, (short) 0);
                        break;
                }
                break;
            }
            case 4: {
                switch (curSourceType) {
                    case E_INPUT_SOURCE_ATV:
                    case E_INPUT_SOURCE_CVBS:
                    case E_INPUT_SOURCE_YPBPR:
                    case E_INPUT_SOURCE_DTV:
                    case E_INPUT_SOURCE_HDMI:
                    case E_INPUT_SOURCE_HDMI2:
                    case E_INPUT_SOURCE_HDMI3:
                        impl.setOverScan((short) 26, (short) 22, (short) 26, (short) 26);
                        break;
                    default:
                        impl.setOverScan((short) 0, (short) 0, (short) 0, (short) 0);
                        break;
                }
                break;
            }
            case 5: {
                switch (curSourceType) {
                    case E_INPUT_SOURCE_ATV:
                    case E_INPUT_SOURCE_CVBS:
                    case E_INPUT_SOURCE_YPBPR:
                        impl.setOverScan((short) 40, (short) 0, (short) 40, (short) 38);
                        break;
                    case E_INPUT_SOURCE_DTV:
                    case E_INPUT_SOURCE_HDMI:
                    case E_INPUT_SOURCE_HDMI2:
                    case E_INPUT_SOURCE_HDMI3:
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
