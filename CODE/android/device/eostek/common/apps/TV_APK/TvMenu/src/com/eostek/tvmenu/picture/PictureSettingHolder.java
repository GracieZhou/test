
package com.eostek.tvmenu.picture;

import java.util.ArrayList;

import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.eostek.tvmenu.R;
import com.eostek.tvmenu.picture.VideoDataSet.EN_MS_MPEG_NR;
import com.eostek.tvmenu.utils.Constants;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvPictureManager;
import com.mstar.android.tvapi.common.vo.ColorTemperatureExData;
import com.mstar.android.tvapi.common.vo.EnumColorTemperature;

public class PictureSettingHolder {
    private int mCurSourceType = TvCommonManager.INPUT_SOURCE_NONE;

    private PictureSettingFragment mFragment;

    // item dialog, only contained in PictureSetting module
    private ItemDialog mDialog;

    // item data structure
    private ArrayList<SettingItem> mItems;

    SettingItem mItemPictureMode = null;

    SettingItem mItemPictureContrast = null;

    SettingItem mItemPictureBrightness = null;

    SettingItem mItemPictureHue = null;

    SettingItem mItemPictureSharpness = null;

    SettingItem mItemPictureSaturation = null;

    SettingItem mItemPictureBackLight = null;

    SettingItem mItemColorTemperature = null;

    SettingItem mItemRed = null;

    SettingItem mItemGreen = null;

    SettingItem mItemBlue = null;
    
    SettingItem mItemHdmiFull = null;
    
    SettingItem mItemHdmiRgb = null;

    SettingItem mItemZoomMode = null;

    SettingItem mItemImageNoiseReduction = null;

    SettingItem mItemMpegNoiseReduction = null;
    
    SettingItem mItemOSDTransparency = null;
    
    // handle to item layout's child view
    LinearLayout mPictureModeLl;

    TextView mPictureModeTitleTxt;

    TextView mPictureModeContentTxt;

    LinearLayout mPictureContrastLl;

    TextView mPictureContrastTitleTxt;

    SeekBar mPictureContrastSeekBar;

    TextView mPictureContrastSeekBarNum;

    LinearLayout mPictureBrightnessLl;

    TextView mPictureBrightnessTitleTxt;

    SeekBar mPictureBrightnessSeekBar;

    TextView mPictureBrightnessSeekBarNum;

    LinearLayout mPictureHueLl;

    TextView mPictureHueTitleTxt;

    SeekBar mPictureHueSeekBar;

    TextView mPictureHueSeekBarNum;

    LinearLayout mPictureSharpnessLl;

    TextView mPictureSharpnessTitleTxt;

    SeekBar mPictureSharpnessSeekBar;

    TextView mPictureSharpnessSeekBarNum;

    LinearLayout mPictureSaturationLl;

    TextView mPictureSaturationTitleTxt;

    SeekBar mPictureSaturationSeekBar;

    TextView mPictureSaturationSeekBarNum;

    LinearLayout mPictureBackLightLl;

    TextView mPictureBackLightTitleTxt;

    SeekBar mPictureBackLightSeekBar;

    TextView mPictureBackLightSeekBarNum;

    LinearLayout mColorTemperatureLl;

    TextView mColorTemperatureTitleTxt;

    TextView mColorTemperatureContentTxt;

    LinearLayout mRedLl;

    TextView mRedTitleTxt;

    SeekBar mRedSeekBar;

    TextView mRedSeekBarNum;

    LinearLayout mGreenLl;

    TextView mGreenTitleTxt;

    SeekBar mGreenSeekBar;

    TextView mGreenSeekBarNum;

    LinearLayout mBlueLl;

    TextView mBlueTitleTxt;

    SeekBar mBlueSeekBar;

    TextView mBlueSeekBarNum;

    LinearLayout mHdmiFullLl;

    TextView mHdmiFullTitleTxt;

    TextView mHdmiFullContentTxt;
    
    LinearLayout mHdmiRgbLl;

    TextView mHdmiRgbTitleTxt;

    TextView mHdmiRgbContentTxt;

    LinearLayout mZoomModeLl;

    TextView mZoomModeTitleTxt;

    TextView mZoomModeContentTxt;

    LinearLayout mImageNoiseReductionLl;

    TextView mImageNoiseReductionTitleTxt;

    TextView mImageNoiseReductionContentTxt;

    LinearLayout mMpegNoiseReductionLl;

    TextView mMpegNoiseReductionTitleTxt;

    TextView mMpegNoiseReductionContentTxt;
    
    LinearLayout mOSDTransparencyLl;
    
    TextView mOSDTransparencyTitleTxt;

    TextView mOSDTransparencyContentTxt;
    
    private TvPictureManager tvPictureManager = null;
    
    private int mPictureModeVal;
    
    private String[] mPictureModeStr;
    
    private int mColorTemperatureVal;
    
    private String[] mColorTemperatureStr;
    
    private int mHdmiRgbVal;
    
    private String[] mHdmiRgbStr;
    
    private int mZoomModeVal;
    
    private String[] mZoomModeStr;
    
    private int mNRModeVal;
    
    private String[] mNRModeStr;
    
    private int mMPEGNRModeVal;
    
    private String[] mMPEGNRModeStr;
    
    private String[] mOSDTransparencyStr;
    
    private int mPictureContrastVal;
    
    private int mPictureBrightnessVal;
    
    private int mPictureHueVal;
    
    private int mPictureSharpnessVal;
    
    private int mPictureSaturationVal;
    
    private int mPictureBackLightVal;
    
    private int mRedVal;
    
    private int mGreenVal;
    
    private int mBlueVal;
    
    private int mOSDTransparencyVal;
    
    FrameLayout.LayoutParams mParams;
    
    ColorTemperatureExData mColorTempExData;

    Resources mR;

    protected Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PictureConstants.MSG_UPDATEVIEW_PICTURE_MODE:
                    updateSeekBar(mItemPictureContrast, mPictureContrastSeekBar, mPictureContrastSeekBarNum);
                    updateSeekBar(mItemPictureBrightness, mPictureBrightnessSeekBar, mPictureBrightnessSeekBarNum);
                    updateSeekBar(mItemPictureHue, mPictureHueSeekBar, mPictureHueSeekBarNum);
                    updateSeekBar(mItemPictureSharpness, mPictureSharpnessSeekBar, mPictureSharpnessSeekBarNum);
                    updateSeekBar(mItemPictureSaturation, mPictureSaturationSeekBar, mPictureSaturationSeekBarNum);
                    updateSeekBar(mItemPictureBackLight, mPictureBackLightSeekBar, mPictureBackLightSeekBarNum);
                    mColorTemperatureContentTxt.setText(mItemColorTemperature.getValues()[mItemColorTemperature
                            .getCurValue()]);
                    updateSeekBar(mItemRed, mRedSeekBar, mRedSeekBarNum);
                    updateSeekBar(mItemGreen, mGreenSeekBar, mGreenSeekBarNum);
                    updateSeekBar(mItemBlue, mBlueSeekBar, mBlueSeekBarNum);
                    break;
                case PictureConstants.MSG_UPDATEVIEW_PICTURE_CONTRAST:
                    mPictureModeContentTxt.setText(mItemPictureMode.getValues()[mItemPictureMode.getCurValue()]);
                    updateSeekBar(mItemRed, mRedSeekBar, mRedSeekBarNum);
                    updateSeekBar(mItemGreen, mGreenSeekBar, mGreenSeekBarNum);
                    updateSeekBar(mItemBlue, mBlueSeekBar, mBlueSeekBarNum);
                    break;
                case PictureConstants.MSG_UPDATEVIEW_PICTURE_BRIGHTNESS:
                case PictureConstants.MSG_UPDATEVIEW_PICTURE_HUE:
                case PictureConstants.MSG_UPDATEVIEW_PICTURE_SHARPNESS:
                case PictureConstants.MSG_UPDATEVIEW_PICTURE_SATURATION:
                case PictureConstants.MSG_UPDATEVIEW_PICTURE_BACKLIGHT:
                    mPictureModeContentTxt.setText(mItemPictureMode.getValues()[mItemPictureMode.getCurValue()]);
                    mColorTemperatureContentTxt.setText(mItemColorTemperature.getValues()[mItemColorTemperature
                            .getCurValue()]);
                    updateSeekBar(mItemRed, mRedSeekBar, mRedSeekBarNum);
                    updateSeekBar(mItemGreen, mGreenSeekBar, mGreenSeekBarNum);
                    updateSeekBar(mItemBlue, mBlueSeekBar, mBlueSeekBarNum);
                    break;
                case PictureConstants.MSG_UPDATEVIEW_PICTURE_TEMPERATURE:
                    updateSeekBar(mItemRed, mRedSeekBar, mRedSeekBarNum);
                    updateSeekBar(mItemGreen, mGreenSeekBar, mGreenSeekBarNum);
                    updateSeekBar(mItemBlue, mBlueSeekBar, mBlueSeekBarNum);
                    break;
                case PictureConstants.MSG_UPDATEVIEW_RED:
                case PictureConstants.MSG_UPDATEVIEW_GREEN:
                case PictureConstants.MSG_UPDATEVIEW_BLUE:
                    mColorTemperatureContentTxt.setText(mItemColorTemperature.getValues()[mItemColorTemperature
                            .getCurValue()]);
                    break;
                case PictureConstants.MSG_UPDATEVIEW_HDMI_FULL:
                    mZoomModeContentTxt.setText(mItemZoomMode.getValues()[mItemZoomMode.getCurValue()]);
                    break;
                default:
            }
            super.handleMessage(msg);
        }
    };

    PictureSettingHolder(PictureSettingFragment f) {
        mFragment = f;
        mR = mFragment.getActivity().getResources();
        tvPictureManager = TvPictureManager.getInstance();
    }

    /**
     * initiate the designated view and it's children.
     * 
     * @param root view
     */
    void initView(View view) {
        // get handle.
        mPictureModeLl = (LinearLayout) view.findViewById(R.id.itemPictureMode);
        mPictureModeContentTxt = (TextView) mPictureModeLl.findViewById(R.id.value);
        mPictureModeTitleTxt = (TextView) mPictureModeLl.findViewById(R.id.title_txt);

        mPictureContrastLl = (LinearLayout) view.findViewById(R.id.itemPictureContrast);
        mPictureContrastTitleTxt = (TextView) mPictureContrastLl.findViewById(R.id.title_txt);
        mPictureContrastSeekBar = (SeekBar) mPictureContrastLl.findViewById(R.id.seekbar);
        mPictureContrastSeekBarNum = (TextView) mPictureContrastLl.findViewById(R.id.seekbar_number);

        mPictureBrightnessLl = (LinearLayout) view.findViewById(R.id.itemPictureBrightness);
        mPictureBrightnessTitleTxt = (TextView) mPictureBrightnessLl.findViewById(R.id.title_txt);
        mPictureBrightnessSeekBar = (SeekBar) mPictureBrightnessLl.findViewById(R.id.seekbar);
        mPictureBrightnessSeekBarNum = (TextView) mPictureBrightnessLl.findViewById(R.id.seekbar_number);

        mPictureHueLl = (LinearLayout) view.findViewById(R.id.itemPictureHue);
        mPictureHueTitleTxt = (TextView) mPictureHueLl.findViewById(R.id.title_txt);
        mPictureHueSeekBar = (SeekBar) mPictureHueLl.findViewById(R.id.seekbar);
        mPictureHueSeekBarNum = (TextView) mPictureHueLl.findViewById(R.id.seekbar_number);

        mPictureSharpnessLl = (LinearLayout) view.findViewById(R.id.itemPictureSharpness);
        mPictureSharpnessTitleTxt = (TextView) mPictureSharpnessLl.findViewById(R.id.title_txt);
        mPictureSharpnessSeekBar = (SeekBar) mPictureSharpnessLl.findViewById(R.id.seekbar);
        mPictureSharpnessSeekBarNum = (TextView) mPictureSharpnessLl.findViewById(R.id.seekbar_number);

        mPictureSaturationLl = (LinearLayout) view.findViewById(R.id.itemPictureSaturation);
        mPictureSaturationTitleTxt = (TextView) mPictureSaturationLl.findViewById(R.id.title_txt);
        mPictureSaturationSeekBar = (SeekBar) mPictureSaturationLl.findViewById(R.id.seekbar);
        mPictureSaturationSeekBarNum = (TextView) mPictureSaturationLl.findViewById(R.id.seekbar_number);

        mPictureBackLightLl = (LinearLayout) view.findViewById(R.id.itemPictureBackLight);
        mPictureBackLightTitleTxt = (TextView) mPictureBackLightLl.findViewById(R.id.title_txt);
        mPictureBackLightSeekBar = (SeekBar) mPictureBackLightLl.findViewById(R.id.seekbar);
        mPictureBackLightSeekBarNum = (TextView) mPictureBackLightLl.findViewById(R.id.seekbar_number);

        mColorTemperatureLl = (LinearLayout) view.findViewById(R.id.itemColorTemperature);
        mColorTemperatureContentTxt = (TextView) mColorTemperatureLl.findViewById(R.id.value);
        mColorTemperatureTitleTxt = (TextView) mColorTemperatureLl.findViewById(R.id.title_txt);

        mRedLl = (LinearLayout) view.findViewById(R.id.itemRed);
        mRedTitleTxt = (TextView) mRedLl.findViewById(R.id.title_txt);
        mRedSeekBar = (SeekBar) mRedLl.findViewById(R.id.seekbar);
        mRedSeekBarNum = (TextView) mRedLl.findViewById(R.id.seekbar_number);

        mGreenLl = (LinearLayout) view.findViewById(R.id.itemGreen);
        mGreenTitleTxt = (TextView) mGreenLl.findViewById(R.id.title_txt);
        mGreenSeekBar = (SeekBar) mGreenLl.findViewById(R.id.seekbar);
        mGreenSeekBarNum = (TextView) mGreenLl.findViewById(R.id.seekbar_number);

        mBlueLl = (LinearLayout) view.findViewById(R.id.itemBlue);
        mBlueTitleTxt = (TextView) mBlueLl.findViewById(R.id.title_txt);
        mBlueSeekBar = (SeekBar) mBlueLl.findViewById(R.id.seekbar);
        mBlueSeekBarNum = (TextView) mBlueLl.findViewById(R.id.seekbar_number);

        mHdmiFullLl = (LinearLayout) view.findViewById(R.id.itemHdmiFull);
        mHdmiFullContentTxt = (TextView) mHdmiFullLl.findViewById(R.id.value);
        mHdmiFullTitleTxt = (TextView) mHdmiFullLl.findViewById(R.id.title_txt);
        
        mHdmiRgbLl = (LinearLayout) view.findViewById(R.id.itemHdmiRgb);
        mHdmiRgbContentTxt = (TextView) mHdmiRgbLl.findViewById(R.id.value);
        mHdmiRgbTitleTxt = (TextView) mHdmiRgbLl.findViewById(R.id.title_txt);

        mZoomModeLl = (LinearLayout) view.findViewById(R.id.itemZoomMode);
        mZoomModeContentTxt = (TextView) mZoomModeLl.findViewById(R.id.value);
        mZoomModeTitleTxt = (TextView) mZoomModeLl.findViewById(R.id.title_txt);

        mImageNoiseReductionLl = (LinearLayout) view.findViewById(R.id.itemImageNoiseReduction);
        mImageNoiseReductionContentTxt = (TextView) mImageNoiseReductionLl.findViewById(R.id.value);
        mImageNoiseReductionTitleTxt = (TextView) mImageNoiseReductionLl.findViewById(R.id.title_txt);

        mMpegNoiseReductionLl = (LinearLayout) view.findViewById(R.id.itemMpegNoiseReduction);
        mMpegNoiseReductionContentTxt = (TextView) mMpegNoiseReductionLl.findViewById(R.id.value);
        mMpegNoiseReductionTitleTxt = (TextView) mMpegNoiseReductionLl.findViewById(R.id.title_txt);
        
        mOSDTransparencyLl = (LinearLayout) view.findViewById(R.id.itemOSDTransparency);
        mOSDTransparencyContentTxt = (TextView) mOSDTransparencyLl.findViewById(R.id.value);
        mOSDTransparencyTitleTxt = (TextView) mOSDTransparencyLl.findViewById(R.id.title_txt);

        // set item title
        String[] pictureSettingTitles = mFragment.getActivity().getResources().getStringArray(R.array.setting_picture);
        mPictureModeStr = mFragment.getActivity().getResources().getStringArray(R.array.setting_picturemode_vals);
        mColorTemperatureStr = mFragment.getActivity().getResources().getStringArray(R.array.setting_colortemperature_vals);
        mZoomModeStr = mFragment.getActivity().getResources().getStringArray(R.array.setting_zoommode_vals);
        mNRModeStr = mFragment.getActivity().getResources().getStringArray(R.array.setting_imgnoisereduction_vals);
        mMPEGNRModeStr = mFragment.getActivity().getResources().getStringArray(R.array.setting_mpegnoisereduction_vals);
        mOSDTransparencyStr = mFragment.getActivity().getResources().getStringArray(R.array.osd_transparency_vals);
        mHdmiRgbStr = mFragment.getActivity().getResources().getStringArray(R.array.setting_hdmirgb_vals);
        
        mParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        
        mPictureModeTitleTxt.setText(pictureSettingTitles[0]);
        mPictureContrastTitleTxt.setText(pictureSettingTitles[1]);
        mPictureBrightnessTitleTxt.setText(pictureSettingTitles[2]);
        mPictureHueTitleTxt.setText(pictureSettingTitles[3]);
        mPictureSharpnessTitleTxt.setText(pictureSettingTitles[4]);
        mPictureSaturationTitleTxt.setText(pictureSettingTitles[5]);
        mPictureBackLightTitleTxt.setText(pictureSettingTitles[6]);
        mColorTemperatureTitleTxt.setText(pictureSettingTitles[7]);
        mRedTitleTxt.setText(pictureSettingTitles[8]);
        mGreenTitleTxt.setText(pictureSettingTitles[9]);
        mBlueTitleTxt.setText(pictureSettingTitles[10]);
        mHdmiFullTitleTxt.setText(pictureSettingTitles[11]);
        mHdmiRgbTitleTxt.setText(pictureSettingTitles[17]);
        mZoomModeTitleTxt.setText(pictureSettingTitles[12]);
        mImageNoiseReductionTitleTxt.setText(pictureSettingTitles[13]);
        mOSDTransparencyTitleTxt.setText(pictureSettingTitles[14]);
        mMpegNoiseReductionTitleTxt.setText(pictureSettingTitles[15]);

        // had to set seekbar's attribute by code instead of static XML's way,
        // since the latter one cause a tricky problem.
        mPictureContrastSeekBar.setProgressDrawable(mR.getDrawable(R.drawable.seekbar_progress1));
        mPictureContrastSeekBar.setThumb(mR.getDrawable(R.drawable.seekbar_thumb1));
        mPictureBrightnessSeekBar.setProgressDrawable(mR.getDrawable(R.drawable.seekbar_progress1));
        mPictureBrightnessSeekBar.setThumb(mR.getDrawable(R.drawable.seekbar_thumb1));
        mPictureHueSeekBar.setProgressDrawable(mR.getDrawable(R.drawable.seekbar_progress1));
        mPictureHueSeekBar.setThumb(mR.getDrawable(R.drawable.seekbar_thumb1));
        mPictureSharpnessSeekBar.setProgressDrawable(mR.getDrawable(R.drawable.seekbar_progress1));
        mPictureSharpnessSeekBar.setThumb(mR.getDrawable(R.drawable.seekbar_thumb1));
        mPictureSaturationSeekBar.setProgressDrawable(mR.getDrawable(R.drawable.seekbar_progress1));
        mPictureSaturationSeekBar.setThumb(mR.getDrawable(R.drawable.seekbar_thumb1));
        mPictureBackLightSeekBar.setProgressDrawable(mR.getDrawable(R.drawable.seekbar_progress1));
        mPictureBackLightSeekBar.setThumb(mR.getDrawable(R.drawable.seekbar_thumb1));
        mRedSeekBar.setProgressDrawable(mR.getDrawable(R.drawable.seekbar_progress1));
        mRedSeekBar.setThumb(mR.getDrawable(R.drawable.seekbar_thumb1));
        mGreenSeekBar.setProgressDrawable(mR.getDrawable(R.drawable.seekbar_progress1));
        mGreenSeekBar.setThumb(mR.getDrawable(R.drawable.seekbar_thumb1));
        mBlueSeekBar.setProgressDrawable(mR.getDrawable(R.drawable.seekbar_progress1));
        mBlueSeekBar.setThumb(mR.getDrawable(R.drawable.seekbar_thumb1));
        
    }

    /**
     * initiate item data structure, and set to default value.
     * 
     * @param
     */
    void initItems() {
        mCurSourceType = TvCommonManager.getInstance().getCurrentTvInputSource();
        mItems = new ArrayList<SettingItem>();
        String pictureEosSettingmItems[] = mR.getStringArray(R.array.setting_picture);
        /* picture mode */
        String initArray[] = mR.getStringArray(R.array.setting_picturemode_vals);
        
        mPictureModeVal = TvPictureManager.getInstance().getPictureMode();
        if(mPictureModeVal != Constants.PICTURE_MODE_USER && mPictureModeVal != Constants.PICTURE_MODE_BRIGHTNESS && mPictureModeVal != Constants.PICTURE_MODE_NORMAL
                && mPictureModeVal != Constants.PICTURE_MODE_SOFT){
            TvPictureManager.getInstance().setPictureMode(Constants.PICTURE_MODE_NORMAL);
            mPictureModeVal = Constants.PICTURE_MODE_NORMAL;
        }
        
        mItemPictureMode = new SettingItem(mFragment, pictureEosSettingmItems[0], initArray, mPictureModeVal,
                PictureConstants.TYPE_ITEM_ENUM, true);
        mItems.add(mItemPictureMode);
        /* contrast */
        mItemPictureContrast = new SettingItem(mFragment, pictureEosSettingmItems[1], 0, 100, 0,
                PictureConstants.TYPE_ITEM_DIGITAL, true);
        mItems.add(mItemPictureContrast);
        /* brightness */
        mItemPictureBrightness = new SettingItem(mFragment, pictureEosSettingmItems[2], 0, 100, 0,
                PictureConstants.TYPE_ITEM_DIGITAL, true);
        mItems.add(mItemPictureBrightness);
        /* hue */
        mItemPictureHue = new SettingItem(mFragment, pictureEosSettingmItems[3], 0, 100, 0,
                PictureConstants.TYPE_ITEM_DIGITAL, true);
        mItems.add(mItemPictureHue);
        /* sharpness */
        mItemPictureSharpness = new SettingItem(mFragment, pictureEosSettingmItems[4], 0, 100, 0,
                PictureConstants.TYPE_ITEM_DIGITAL, true);
        mItems.add(mItemPictureSharpness);
        /* saturation */
        mItemPictureSaturation = new SettingItem(mFragment, pictureEosSettingmItems[5], 0, 100, 0,
                PictureConstants.TYPE_ITEM_DIGITAL, true);
        mItems.add(mItemPictureSaturation);
        /* back light */
        mItemPictureBackLight = new SettingItem(mFragment, pictureEosSettingmItems[6], 0, 100, 0,
                PictureConstants.TYPE_ITEM_DIGITAL, true);
        mItems.add(mItemPictureBackLight);
        /* color temperature */
        initArray = mR.getStringArray(R.array.setting_colortemperature_vals);
        mItemColorTemperature = new SettingItem(mFragment, pictureEosSettingmItems[7], initArray, 0,
                PictureConstants.TYPE_ITEM_ENUM, true);
        mItems.add(mItemColorTemperature);
        mItemRed = new SettingItem(mFragment, pictureEosSettingmItems[8], 0, 128, 0,
                PictureConstants.TYPE_ITEM_DIGITAL, true);
        mItems.add(mItemRed);
        mItemGreen = new SettingItem(mFragment, pictureEosSettingmItems[9], 0, 128, 0,
                PictureConstants.TYPE_ITEM_DIGITAL, true);
        mItems.add(mItemGreen);
        mItemBlue = new SettingItem(mFragment, pictureEosSettingmItems[10], 0, 128, 0,
                PictureConstants.TYPE_ITEM_DIGITAL, true);
        mItems.add(mItemBlue);

        initArray = mR.getStringArray(R.array.setting_hdmifull_vals);
        mItemHdmiFull = new SettingItem(mFragment, pictureEosSettingmItems[11], initArray, 0,
                PictureConstants.TYPE_ITEM_ENUM, false);
        initArray = mR.getStringArray(R.array.setting_hdmirgb_vals);
        mItemHdmiRgb = new SettingItem(mFragment,  pictureEosSettingmItems[17], initArray, 0,
                PictureConstants.TYPE_ITEM_ENUM, false);
        if (mCurSourceType >= TvCommonManager.INPUT_SOURCE_HDMI
                && mCurSourceType < TvCommonManager.INPUT_SOURCE_HDMI_MAX) {
            mItemHdmiFull.setFocusable(true);
            mItemHdmiRgb.setFocusable(true);
        }
        mItems.add(mItemHdmiFull);
        mItems.add(mItemHdmiRgb);
        /* zoom mode */
        if (mCurSourceType == TvCommonManager.INPUT_SOURCE_ATV || mCurSourceType == TvCommonManager.INPUT_SOURCE_DTV) {
            initArray = mR.getStringArray(R.array.setting_zoommode_vals_tv);
        } else {
            initArray = mR.getStringArray(R.array.setting_zoommode_vals);
        }
        mItemZoomMode = new SettingItem(mFragment, pictureEosSettingmItems[12], initArray, 0,
                PictureConstants.TYPE_ITEM_ENUM, true);
        if (mCurSourceType == TvCommonManager.INPUT_SOURCE_VGA) {
            mItemZoomMode.setFocusable(false);
        }
        mItems.add(mItemZoomMode);
        /* image noise reduction */
        initArray = mR.getStringArray(R.array.setting_imgnoisereduction_vals);
        mItemImageNoiseReduction = new SettingItem(mFragment, pictureEosSettingmItems[13], initArray, 0,
                PictureConstants.TYPE_ITEM_ENUM, true);
        mItems.add(mItemImageNoiseReduction);
        /* MpegNoiseReduction */
        initArray = mR.getStringArray(R.array.setting_mpegnoisereduction_vals);
        mItemMpegNoiseReduction = new SettingItem(mFragment, pictureEosSettingmItems[14], initArray, 0,
                PictureConstants.TYPE_ITEM_ENUM, true);
        mItems.add(mItemMpegNoiseReduction);
        /*OSDTransparency */
        initArray = mR.getStringArray(R.array.osd_transparency_vals);
        mItemOSDTransparency = new SettingItem(mFragment, pictureEosSettingmItems[14], initArray, 0,
                PictureConstants.TYPE_ITEM_ENUM, true);
        mItems.add(mItemOSDTransparency);
        
        
        mColorTemperatureVal = TvPictureManager.getInstance().getColorTempratureIdx();
        
        //Red
        mColorTempExData = TvPictureManager.getInstance().getWbGainOffsetEx(mColorTemperatureVal,mCurSourceType);
        mRedVal = mColorTempExData.redGain / PictureConstants.COLOR_TEMPERATURE;
        
        //Green
        mGreenVal = mColorTempExData.greenGain / PictureConstants.COLOR_TEMPERATURE;
        
        //Blue
        mBlueVal = mColorTempExData.blueGain / PictureConstants.COLOR_TEMPERATURE;
        
        //get OSDTransparency Value
        mOSDTransparencyVal = Settings.System.getInt(mFragment.getActivity().getContentResolver(),
                "OSDTransparency", 5);
        mOSDTransparencyContentTxt.setText(mOSDTransparencyStr[mOSDTransparencyVal]);
        mFragment.mLogic.setOSDTransparency(mOSDTransparencyVal);
        
        // get hdmi rgb
        // Refine performance with query DB by content provider to reduce
        // startup time in setting page.
        Cursor cursor = mFragment.getActivity().getContentResolver()
                .query(Uri.parse("content://mstar.tv.usersetting/systemsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            mHdmiRgbVal = cursor.getInt(cursor.getColumnIndex("u8ColorRangeMode"));
            Log.d("laird", "mHdmiRgbVal：" + mHdmiRgbVal);
        }
        cursor.close();
        mHdmiRgbContentTxt.setText(mHdmiRgbStr[mHdmiRgbVal]);

    }

    /**
     * set listener.
     * 
     * @param
     */
    void setListener() {
        OnKeyListener onKeyListener = new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
            	//暂时去掉进入2级Dialog进行设置的功能
//                if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                    switch (keyCode) {
//                        case KeyEvent.KEYCODE_ENTER: {
//                            if (!v.isFocusable()) {
//                                return true;
//                            }
//                            if (v.getId() != R.id.itemOSDTransparency) {
//                                mFragment.getActivity().findViewById(R.id.main).setVisibility(View.INVISIBLE);
//                                int index = 0;
//                                switch (v.getId()) {
//                                    case R.id.itemPictureMode:
//                                    	index = PictureConstants.INDEX_PICTURE_MODE;
//                                        break;
//                                    case R.id.itemPictureContrast:
//                                    	index = PictureConstants.INDEX_PICTURE_CONTRAST;
//                                    	break;
//                                    case R.id.itemPictureBrightness:
//                                    	index = PictureConstants.INDEX_PICTURE_BRIGHTNESS;
//                                        break;
//                                    case R.id.itemPictureHue:
//                                    	index = PictureConstants.INDEX_PICTURE_HUE;
//                                        break;
//                                    case R.id.itemPictureSharpness:
//                                    	index = PictureConstants.INDEX_PICTURE_SHARPNESS;
//                                        break;
//                                    case R.id.itemPictureSaturation:
//                                    	index = PictureConstants.INDEX_PICTURE_SATURATION;
//                                        break;
//                                    case R.id.itemPictureBackLight:
//                                    	index = PictureConstants.INDEX_PICTURE_BACKLIGHT;
//                                        break;
//                                    case R.id.itemColorTemperature:
//                                    	index = PictureConstants.INDEX_PICTURE_TEMPERATURE;
//                                        break;
//                                    case R.id.itemRed:
//                                    	index = PictureConstants.INDEX_RED;
//                                        break;
//                                    case R.id.itemGreen:
//                                    	index = PictureConstants.INDEX_GREEN;
//                                        break;
//                                    case R.id.itemBlue:
//                                    	index = PictureConstants.INDEX_BLUE;
//                                        break;
//                                    case R.id.itemHdmiFull:
//                                    	index = PictureConstants.INDEX_HDMI_FULL;
//                                        break;
//                                    case R.id.itemZoomMode:
//                                    	index = PictureConstants.INDEX_ZOOM_MODE;
//                                        break;
//                                    case R.id.itemImageNoiseReduction:
//                                    	index = PictureConstants.INDEX_IMAGE_NOISE_REDUCTION;
//                                        break;
//    //                                case R.id.itemMpegNoiseReduction:
//    //                                	index = PictureConstants.INDEX_MPEG_NOISE_REDUCTION;
//    //                                    break;
////                                    case R.id.itemOSDTransparency:
////                                        index = PictureConstants.INDEX_OSD_TRANSPARENCY;
////                                        break;
//                                    default: 
//                                }
//                                mDialog = new ItemDialog(mFragment, mItems, index);
//                                mDialog.show();
//                            }
//                        }
//                    }
//                }
                
                switch (v.getId()) {
                    case R.id.itemPictureMode:{
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    mPictureModeVal = TvPictureManager.getInstance().getPictureMode();
                                    if(mPictureModeVal == Constants.PICTURE_MODE_USER){
                                        mFragment.mApplication.addTvApiTask(new Runnable() {
                                            @Override
                                            public void run() {
                                                TvPictureManager.getInstance().setPictureMode(Constants.PICTURE_MODE_BRIGHTNESS);
                                            }
                                        });
                                        
                                        mPictureModeVal = Constants.PICTURE_MODE_BRIGHTNESS;
                                    }else{
                                        TvPictureManager.getInstance().setPictureMode(++mPictureModeVal);
                                    }
                                    mPictureModeContentTxt.setText(mPictureModeStr[mPictureModeVal]);
                                    mFragment.mLogic.setTvValue(mPictureModeVal,PictureConstants.INDEX_PICTURE_MODE);
                                    //set value of ItemDialog
                                    setDialogValue(mItemPictureMode,mPictureModeVal);
                                }
                                return true;
                                
                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    mPictureModeVal = TvPictureManager.getInstance().getPictureMode();
                                    if(mPictureModeVal == Constants.PICTURE_MODE_BRIGHTNESS){
                                        mFragment.mApplication.addTvApiTask(new Runnable() {
                                            @Override
                                            public void run() {
                                                TvPictureManager.getInstance().setPictureMode(Constants.PICTURE_MODE_USER);
                                            }
                                        });
                                        mPictureModeVal = Constants.PICTURE_MODE_USER;
                                    }else{
                                        TvPictureManager.getInstance().setPictureMode(--mPictureModeVal);
                                    }
                                    mPictureModeContentTxt.setText(mPictureModeStr[mPictureModeVal]);
                                    mFragment.mLogic.setTvValue(mPictureModeVal,PictureConstants.INDEX_PICTURE_MODE);
                                    //set value of ItemDialog
                                    setDialogValue(mItemPictureMode,mPictureModeVal);
                                }
                                return true;
                            }
                        }
                    }
                    break;
                    case R.id.itemPictureContrast:{
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    mPictureContrastVal = TvPictureManager.getInstance().getVideoItem(Constants.PICTURE_CONTRAST);
                                    if (mPictureContrastVal >= 0 && mPictureContrastVal < 100) {
                                        mPictureContrastSeekBar.setProgress(++mPictureContrastVal);
                                        mFragment.mApplication.addTvApiTask(new Runnable() {
                                            @Override
                                            public void run() {
                                                TvPictureManager.getInstance().setVideoItem(Constants.PICTURE_CONTRAST,mPictureContrastVal);
                                                mFragment.mLogic.setTvValue(mPictureContrastVal,PictureConstants.INDEX_PICTURE_CONTRAST);
                                                //set value of ItemDialog
                                                setDialogValue(mItemPictureContrast,mPictureContrastVal);
                                            }
                                        });
                                       
                                        setSeekBarView(mPictureContrastSeekBarNum,mPictureContrastVal,mParams);
                                    }
                                    mFragment.mLogic.setTvValue(mPictureContrastVal,PictureConstants.INDEX_PICTURE_CONTRAST);
                                    //set value of ItemDialog
                                    setDialogValue(mItemPictureContrast,mPictureContrastVal);
                                }
                                return true;
                                
                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    mPictureContrastVal = TvPictureManager.getInstance().getVideoItem(Constants.PICTURE_CONTRAST);
                                    if (mPictureContrastVal > 0 && mPictureContrastVal <= 100) {
                                        mPictureContrastSeekBar.setProgress(--mPictureContrastVal);
                                        mFragment.mApplication.addTvApiTask(new Runnable() {
                                            @Override
                                            public void run() {
                                                TvPictureManager.getInstance().setVideoItem(Constants.PICTURE_CONTRAST,mPictureContrastVal);
                                                mFragment.mLogic.setTvValue(mPictureContrastVal,PictureConstants.INDEX_PICTURE_CONTRAST);
                                                //set value of ItemDialog
                                                setDialogValue(mItemPictureContrast,mPictureContrastVal);
                                            }
                                        });
                                        setSeekBarView(mPictureContrastSeekBarNum,mPictureContrastVal,mParams);
                                    }
                                   
                                }
                                return true;
                            }
                        }
                    }
                    break;
                    case R.id.itemPictureBrightness:{
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    mPictureBrightnessVal = TvPictureManager.getInstance().getVideoItem(Constants.PICTURE_BRIGHTNESS);
                                    if (mPictureBrightnessVal >= 0 && mPictureBrightnessVal < 100) {
                                        mPictureBrightnessSeekBar.setProgress(++mPictureBrightnessVal);
                                        TvPictureManager.getInstance().setVideoItem(Constants.PICTURE_BRIGHTNESS,mPictureBrightnessVal);
                                        setSeekBarView(mPictureBrightnessSeekBarNum,mPictureBrightnessVal,mParams);
                                    }
                                    mFragment.mLogic.setTvValue(mPictureBrightnessVal,PictureConstants.INDEX_PICTURE_BRIGHTNESS);
                                    //set value of ItemDialog
                                    setDialogValue(mItemPictureBrightness,mPictureBrightnessVal);
                                }
                                return true;
                                
                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    mPictureBrightnessVal = TvPictureManager.getInstance().getVideoItem(Constants.PICTURE_BRIGHTNESS);
                                    if (mPictureBrightnessVal > 0 && mPictureBrightnessVal <= 100) {
                                        mPictureBrightnessSeekBar.setProgress(--mPictureBrightnessVal);
                                        TvPictureManager.getInstance().setVideoItem(Constants.PICTURE_BRIGHTNESS,mPictureBrightnessVal);
                                        setSeekBarView(mPictureBrightnessSeekBarNum,mPictureBrightnessVal,mParams);
                                    }
                                    mFragment.mLogic.setTvValue(mPictureBrightnessVal,PictureConstants.INDEX_PICTURE_BRIGHTNESS);
                                    //set value of ItemDialog
                                    setDialogValue(mItemPictureBrightness,mPictureBrightnessVal);
                                }
                                return true;
                            }
                        }
                    }
                    break;
                    case R.id.itemPictureHue:{
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    mPictureHueVal = TvPictureManager.getInstance().getVideoItem(Constants.PICTURE_HUE);
                                    if (mPictureHueVal >= 0 && mPictureHueVal < 100) {
                                        mPictureHueSeekBar.setProgress(++mPictureHueVal);
                                        TvPictureManager.getInstance().setVideoItem(Constants.PICTURE_HUE,mPictureHueVal);
                                        setSeekBarView(mPictureHueSeekBarNum,mPictureHueVal,mParams);
                                    }
                                    mFragment.mLogic.setTvValue(mPictureHueVal,PictureConstants.INDEX_PICTURE_HUE);
                                    //set value of ItemDialog
                                    setDialogValue(mItemPictureHue,mPictureHueVal);
                                }
                                return true;
                                
                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    mPictureHueVal = TvPictureManager.getInstance().getVideoItem(Constants.PICTURE_HUE);
                                    if (mPictureHueVal > 0 && mPictureHueVal <= 100) {
                                        mPictureHueSeekBar.setProgress(--mPictureHueVal);
                                        TvPictureManager.getInstance().setVideoItem(Constants.PICTURE_HUE,mPictureHueVal);
                                        setSeekBarView(mPictureHueSeekBarNum,mPictureHueVal,mParams);
                                    }
                                    mFragment.mLogic.setTvValue(mPictureHueVal,PictureConstants.INDEX_PICTURE_HUE);
                                    //set value of ItemDialog
                                    setDialogValue(mItemPictureHue,mPictureHueVal);
                                }
                                return true;
                            }
                        }
                    }
                    break;
                    case R.id.itemPictureSharpness:{
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    mPictureSharpnessVal = TvPictureManager.getInstance().getVideoItem(Constants.PICTURE_SHARPNESS);
                                    if (mPictureSharpnessVal >= 0 && mPictureSharpnessVal < 100) {
                                        mPictureSharpnessSeekBar.setProgress(++mPictureSharpnessVal);
                                        TvPictureManager.getInstance().setVideoItem(Constants.PICTURE_SHARPNESS,mPictureSharpnessVal);
                                        setSeekBarView(mPictureSharpnessSeekBarNum,mPictureSharpnessVal,mParams);
                                    }
                                    mFragment.mLogic.setTvValue(mPictureSharpnessVal,PictureConstants.INDEX_PICTURE_SHARPNESS);
                                    //set value of ItemDialog
                                    setDialogValue(mItemPictureSharpness,mPictureSharpnessVal);
                                }
                                return true;
                                
                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    mPictureSharpnessVal = TvPictureManager.getInstance().getVideoItem(Constants.PICTURE_SHARPNESS);
                                    if (mPictureSharpnessVal > 0 && mPictureSharpnessVal <= 100) {
                                        mPictureSharpnessSeekBar.setProgress(--mPictureSharpnessVal);
                                        TvPictureManager.getInstance().setVideoItem(Constants.PICTURE_SHARPNESS,mPictureSharpnessVal);
                                        setSeekBarView(mPictureSharpnessSeekBarNum,mPictureSharpnessVal,mParams);
                                    }
                                    mFragment.mLogic.setTvValue(mPictureSharpnessVal,PictureConstants.INDEX_PICTURE_SHARPNESS);
                                    //set value of ItemDialog
                                    setDialogValue(mItemPictureSharpness,mPictureSharpnessVal);
                                }
                                return true;
                            }
                        }
                    }
                    break;
                    case R.id.itemPictureSaturation:{
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    mPictureSaturationVal = TvPictureManager.getInstance().getVideoItem(Constants.PICTURE_SATURATION);
                                    if (mPictureSaturationVal >= 0 && mPictureSaturationVal < 100) {
                                        mPictureSaturationSeekBar.setProgress(++mPictureSaturationVal);
                                        TvPictureManager.getInstance().setVideoItem(Constants.PICTURE_SATURATION,mPictureSaturationVal);
                                        setSeekBarView(mPictureSaturationSeekBarNum,mPictureSaturationVal,mParams);
                                    }
                                    mFragment.mLogic.setTvValue(mPictureSaturationVal,PictureConstants.INDEX_PICTURE_SATURATION);
                                    //set value of ItemDialog
                                    setDialogValue(mItemPictureSaturation,mPictureSaturationVal);
                                }
                                return true;
                                
                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    mPictureSaturationVal = TvPictureManager.getInstance().getVideoItem(Constants.PICTURE_SATURATION);
                                    if (mPictureSaturationVal > 0 && mPictureSaturationVal <= 100) {
                                        mPictureSaturationSeekBar.setProgress(--mPictureSaturationVal);
                                        TvPictureManager.getInstance().setVideoItem(Constants.PICTURE_SATURATION,mPictureSaturationVal);
                                        setSeekBarView(mPictureSaturationSeekBarNum,mPictureSaturationVal,mParams);
                                    }
                                    mFragment.mLogic.setTvValue(mPictureSaturationVal,PictureConstants.INDEX_PICTURE_SATURATION);
                                    //set value of ItemDialog
                                    setDialogValue(mItemPictureSaturation,mPictureSaturationVal);
                                }
                                return true;
                            }
                        }
                    }
                    break;
                    case R.id.itemPictureBackLight:{
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
//                                    int[] values = TvPictureManager.getInstance().getVideoItems(EnumInputSource.values()[mCurSourceType], TvPictureManager.getInstance().getPictureModeIdx());
                                    
                                    mPictureBackLightVal = TvPictureManager.getInstance().getVideoItem(Constants.PICTURE_BACKLIGHT);
                                    Log.e("chensen","mPictureBackLightVal = " + mPictureBackLightVal);
                                    if (mPictureBackLightVal >= 0 && mPictureBackLightVal < 100) {
                                        mPictureBackLightSeekBar.setProgress(++mPictureBackLightVal);
                                        TvPictureManager.getInstance().setVideoItem(Constants.PICTURE_BACKLIGHT,mPictureBackLightVal);
                                        setSeekBarView(mPictureBackLightSeekBarNum,mPictureBackLightVal,mParams);
                                    }
                                    mFragment.mLogic.setTvValue(mPictureBackLightVal,PictureConstants.INDEX_PICTURE_BACKLIGHT);
                                    //set value of ItemDialog
                                    setDialogValue(mItemPictureBackLight,mPictureBackLightVal);
                                }
                                return true;
                                
                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    mPictureBackLightVal = TvPictureManager.getInstance().getVideoItem(Constants.PICTURE_BACKLIGHT);
                                    Log.e("chensen","mPictureBackLightVal = " + mPictureBackLightVal);
                                    if (mPictureBackLightVal > 0 && mPictureBackLightVal <= 100) {
                                        mPictureBackLightSeekBar.setProgress(--mPictureBackLightVal);
                                        TvPictureManager.getInstance().setVideoItem(Constants.PICTURE_BACKLIGHT,mPictureBackLightVal);
                                        setSeekBarView(mPictureBackLightSeekBarNum,mPictureBackLightVal,mParams);
                                    }
                                    mFragment.mLogic.setTvValue(mPictureBackLightVal,PictureConstants.INDEX_PICTURE_BACKLIGHT);
                                    //set value of ItemDialog
                                    setDialogValue(mItemPictureBackLight,mPictureBackLightVal);
                                }
                                return true;
                            }
                        }
                    }
                    break;
                    case R.id.itemColorTemperature:{
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    mColorTemperatureVal = TvPictureManager.getInstance().getColorTempratureIdx();
                                    if(mColorTemperatureVal == Constants.COLOR_TEMP_USER){
                                        TvPictureManager.getInstance().setColorTempratureIdx(Constants.COLOR_TEMP_COOL);
                                        mColorTemperatureVal = Constants.COLOR_TEMP_COOL;
                                    }else{
                                        TvPictureManager.getInstance().setColorTempratureIdx(++mColorTemperatureVal);
                                    }
                                    mColorTemperatureContentTxt.setText(mColorTemperatureStr[mColorTemperatureVal]);
                                    
                                    mFragment.mLogic.setTvValue(mColorTemperatureVal,PictureConstants.INDEX_PICTURE_TEMPERATURE);
                                    //set valur of Item Dialog
                                    setDialogValue(mItemColorTemperature,mColorTemperatureVal);
                                }
                                return true;
                                
                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    mColorTemperatureVal = TvPictureManager.getInstance().getColorTempratureIdx();
                                    if(mColorTemperatureVal == Constants.COLOR_TEMP_COOL){
                                        TvPictureManager.getInstance().setColorTempratureIdx(Constants.COLOR_TEMP_USER);
                                        mColorTemperatureVal = Constants.COLOR_TEMP_USER;
                                    }else{
                                        TvPictureManager.getInstance().setColorTempratureIdx(--mColorTemperatureVal);
                                    }
                                    mColorTemperatureContentTxt.setText(mColorTemperatureStr[mColorTemperatureVal]);
                                    
                                    mFragment.mLogic.setTvValue(mColorTemperatureVal,PictureConstants.INDEX_PICTURE_TEMPERATURE);
                                    setDialogValue(mItemColorTemperature,mColorTemperatureVal);
                                }
                                return true;
                            }
                        }
                    }
                    break;
                    case R.id.itemRed:{
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        	//change ColorTempera to USER
                            if (mItemColorTemperature.getCurValue() != EnumColorTemperature.E_COLOR_TEMP_USER.getValue()) {
                                TvPictureManager.getInstance().setColorTempratureIdx(TvPictureManager.COLOR_TEMP_USER1);
                                mItemColorTemperature.setCurValue(EnumColorTemperature.E_COLOR_TEMP_USER.getValue());
                                mColorTemperatureVal = TvPictureManager.getInstance().getColorTempratureIdx();
                                mColorTemperatureContentTxt.setText(mItemColorTemperature.getValues()[mItemColorTemperature.getCurValue()]);
                            }
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    
                                    if (mRedVal >= 0 && mRedVal < 128) {
                                        mRedSeekBar.setProgress(++mRedVal);
                                        setColorSeekBarView(mRedSeekBarNum,mRedVal,mParams);
                                    }
                                }
                                return true;
                                
                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    
                                    if (mRedVal > 0 && mRedVal <= 128) {
                                        mRedSeekBar.setProgress(--mRedVal);
                                        setColorSeekBarView(mRedSeekBarNum,mRedVal,mParams);
                                    }
                                    
                                }
                                return true;
                            }
                        }
                        
                        //when key up,set value to enviroment
                        if (event.getAction() == KeyEvent.ACTION_UP) {
                        	mFragment.mApplication.addTvApiTask(new Runnable() {
                                @Override
                                public void run() {
                                	mColorTempExData.redGain = mRedVal * PictureConstants.COLOR_TEMPERATURE;
                                    TvPictureManager.getInstance().setWbGainOffsetEx(mColorTempExData, EnumColorTemperature.E_COLOR_TEMP_USER.getValue(),
                                            mCurSourceType); 
//                                    mFragment.mLogic.setTvValue(mRedVal,PictureConstants.INDEX_RED);
                                    //set value of ItemDialog
//                                    setDialogValue(mItemRed,mRedVal);
                                }
                            });
                        }
                    }
                    break;
                    case R.id.itemGreen:{
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        	//change ColorTempera to USER
                            if (mItemColorTemperature.getCurValue() != EnumColorTemperature.E_COLOR_TEMP_USER.getValue()) {
                                TvPictureManager.getInstance().setColorTempratureIdx(TvPictureManager.COLOR_TEMP_USER1);
                                mItemColorTemperature.setCurValue(EnumColorTemperature.E_COLOR_TEMP_USER.getValue());
                                mColorTemperatureContentTxt.setText(mItemColorTemperature.getValues()[mItemColorTemperature.getCurValue()]);
                            }
                            
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    
                                    if (mGreenVal >= 0 && mGreenVal < 128) {
                                        mGreenSeekBar.setProgress(++mGreenVal);
                                        setColorSeekBarView(mGreenSeekBarNum,mGreenVal,mParams);
                                    }
                                }
                                return true;
                                
                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    
                                    if (mGreenVal > 0 && mGreenVal <= 128) {
                                        mGreenSeekBar.setProgress(--mGreenVal);
                                        setColorSeekBarView(mGreenSeekBarNum,mGreenVal,mParams);
                                    }
                                    
                                }
                                return true;
                            }
                        }
                        
                      //when key up,set value to enviroment
                        if (event.getAction() == KeyEvent.ACTION_UP) {
                        	 mFragment.mApplication.addTvApiTask(new Runnable() {
                                 @Override
                                 public void run() {
                                 	mColorTempExData.greenGain = mGreenVal * PictureConstants.COLOR_TEMPERATURE;
                                     TvPictureManager.getInstance().setWbGainOffsetEx(mColorTempExData, EnumColorTemperature.E_COLOR_TEMP_USER.getValue(),
                                             mCurSourceType);
                                     
//                                     mFragment.mLogic.setTvValue(mBlueVal,PictureConstants.INDEX_BLUE);
//                                     //set value of ItemDialog
//                                     setDialogValue(mItemBlue,mBlueVal);
                                 }
                             });
                        }

                    }
                    break;
                    case R.id.itemBlue:{
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        	//change ColorTempera to USER
                            if (mItemColorTemperature.getCurValue() != EnumColorTemperature.E_COLOR_TEMP_USER.getValue()) {
                                TvPictureManager.getInstance().setColorTempratureIdx(TvPictureManager.COLOR_TEMP_USER1);
                                mItemColorTemperature.setCurValue(EnumColorTemperature.E_COLOR_TEMP_USER.getValue());
                                mColorTemperatureContentTxt.setText(mItemColorTemperature.getValues()[mItemColorTemperature.getCurValue()]);
                            }
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    
                                    if (mBlueVal >= 0 && mBlueVal < 128) {
                                        mBlueSeekBar.setProgress(++mBlueVal);
                                        setColorSeekBarView(mBlueSeekBarNum,mBlueVal,mParams);
                                    }
                                    
                                }
                                return true;
                                
                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    
                                    if (mBlueVal > 0 && mBlueVal <= 128) {
                                        mBlueSeekBar.setProgress(--mBlueVal);
                                        setColorSeekBarView(mBlueSeekBarNum,mBlueVal,mParams);
                                    }
                                }
                                return true;
                            }
                        }
                        
                      //when key up,set value to enviroment
                        if (event.getAction() == KeyEvent.ACTION_UP) {
                        	 mFragment.mApplication.addTvApiTask(new Runnable() {
                                 @Override
                                 public void run() {
                                 	mColorTempExData.blueGain = mBlueVal * PictureConstants.COLOR_TEMPERATURE;
                                     TvPictureManager.getInstance().setWbGainOffsetEx(mColorTempExData, EnumColorTemperature.E_COLOR_TEMP_USER.getValue(),
                                             mCurSourceType);
                                     
//                                     mFragment.mLogic.setTvValue(mBlueVal,PictureConstants.INDEX_BLUE);
//                                     //set value of ItemDialog
//                                     setDialogValue(mItemBlue,mBlueVal);
                                 }
                             });
                        }
                    }
                    break;
                    case R.id.itemHdmiFull:{
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    
                                }
                                return true;
                                
                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                }
                                return true;
                            }
                        }
                    }
                    break;
                    case R.id.itemHdmiRgb: {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    if (mHdmiRgbVal == 2)
                                        mHdmiRgbVal = 0;
                                    else
                                        mHdmiRgbVal++;
                                    mHdmiRgbContentTxt.setText(mHdmiRgbStr[mHdmiRgbVal]);
                                    setDialogValue(mItemHdmiRgb, mHdmiRgbVal);
                                    tvPictureManager.setColorRange((byte) mHdmiRgbVal);

                                }
                                    return true;

                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                }
                                    if (mHdmiRgbVal == 0)
                                        mHdmiRgbVal = 2;
                                    else
                                        mHdmiRgbVal--;
                                    mHdmiRgbContentTxt.setText(mHdmiRgbStr[mHdmiRgbVal]);
                                    setDialogValue(mItemHdmiRgb, mHdmiRgbVal);
                                    tvPictureManager.setColorRange((byte) mHdmiRgbVal);
                                    return true;
                            }
                        }
                    }
                        break;
                    case R.id.itemZoomMode:{
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    mZoomModeVal = TvPictureManager.getInstance().getVideoArcType();
                                    if(mZoomModeVal == Constants.ASPECT_RATIO_Real){
                                        TvPictureManager.getInstance().setVideoArcType(Constants.ASPECT_RATIO_16x9);
                                        mZoomModeVal = Constants.ASPECT_RATIO_16x9;
                                    }else{
                                        TvPictureManager.getInstance().setVideoArcType(++mZoomModeVal);
                                    }
                                    mZoomModeContentTxt.setText(mZoomModeStr[mZoomModeVal - 1]);
                                  //set value of ItemDialog
                                    setDialogValue(mItemZoomMode,mZoomModeVal -1);
                                }
                                return true;
                                
                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    mZoomModeVal = TvPictureManager.getInstance().getVideoArcType();
                                    if(mZoomModeVal == Constants.ASPECT_RATIO_16x9){
                                        TvPictureManager.getInstance().setVideoArcType(Constants.ASPECT_RATIO_Real);
                                        mZoomModeVal = Constants.ASPECT_RATIO_Real;
                                    }else{
                                        TvPictureManager.getInstance().setVideoArcType(--mZoomModeVal);
                                    }
                                    mZoomModeContentTxt.setText(mZoomModeStr[mZoomModeVal - 1]);
                                    //set value of ItemDialog
                                    setDialogValue(mItemZoomMode,mZoomModeVal - 1);
                                }
                                return true;
                            }
                        }
                    }
                    break;
                    case R.id.itemImageNoiseReduction:{
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    mNRModeVal = TvPictureManager.getInstance().getNoiseReduction();
                                    if(mNRModeVal == Constants.NR_MODE_AUTO){
                                        TvPictureManager.getInstance().setNoiseReduction(Constants.NR_MODE_OFF);
                                        mNRModeVal = Constants.NR_MODE_OFF;
                                    }else{
                                        TvPictureManager.getInstance().setNoiseReduction(++mNRModeVal);
                                    }
                                    mImageNoiseReductionContentTxt.setText(mNRModeStr[mNRModeVal]);
                                    //set value of ItemDialog
                                    setDialogValue(mItemImageNoiseReduction,mNRModeVal);
                                }
                                return true;
                                
                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    mNRModeVal = TvPictureManager.getInstance().getNoiseReduction();
                                    if(mNRModeVal == Constants.NR_MODE_OFF){
                                        TvPictureManager.getInstance().setNoiseReduction(Constants.NR_MODE_AUTO);
                                        mNRModeVal = Constants.NR_MODE_AUTO;
                                    }else{
                                        TvPictureManager.getInstance().setNoiseReduction(--mNRModeVal);
                                    }
                                    mImageNoiseReductionContentTxt.setText(mNRModeStr[mNRModeVal]);
                                  //set value of ItemDialog
                                    setDialogValue(mItemImageNoiseReduction,mNRModeVal);
                                }
                                return true;
                            }
                        }
                    }
                    break;
                    
                    case R.id.itemMpegNoiseReduction:{
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    
                                    mMPEGNRModeVal = mFragment.mLogic.getMpegNR().ordinal();
                                    if(mMPEGNRModeVal == EN_MS_MPEG_NR.MS_MPEG_NR_HIGH.ordinal()){
                                        mMPEGNRModeVal = EN_MS_MPEG_NR.MS_MPEG_NR_OFF.ordinal();
                                    }else{
                                        mMPEGNRModeVal++;
                                    }
                                    mFragment.mLogic.setMpegNR(EN_MS_MPEG_NR.values()[mMPEGNRModeVal]);
                                    mMpegNoiseReductionContentTxt.setText(mMPEGNRModeStr[mMPEGNRModeVal]);
                                    //set value of ItemDialog
                                    setDialogValue(mItemMpegNoiseReduction,mMPEGNRModeVal);
                                }
                                return true;
                                
                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    mMPEGNRModeVal = mFragment.mLogic.getMpegNR().ordinal();
                                    if(mMPEGNRModeVal == EN_MS_MPEG_NR.MS_MPEG_NR_OFF.ordinal()){
                                        mMPEGNRModeVal = EN_MS_MPEG_NR.MS_MPEG_NR_HIGH.ordinal();
                                    }else{
                                        mMPEGNRModeVal--;
                                    }
                                    mFragment.mLogic.setMpegNR(EN_MS_MPEG_NR.values()[mMPEGNRModeVal]);
                                    mMpegNoiseReductionContentTxt.setText(mMPEGNRModeStr[mMPEGNRModeVal]);
                                    //set value of ItemDialog
                                    setDialogValue(mItemMpegNoiseReduction,mMPEGNRModeVal);
                                }
                                return true;
                            }
                        }
                    }
                    break;
                    
                    case R.id.itemOSDTransparency:{
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    if(mOSDTransparencyVal == 10){
                                        mOSDTransparencyVal = 0;
                                    }else{
                                        mOSDTransparencyVal++;
                                    }
                                    mOSDTransparencyContentTxt.setText(mOSDTransparencyStr[mOSDTransparencyVal]);
                                    
                                    mFragment.mLogic.setOSDTransparency(mOSDTransparencyVal);
                                    Settings.System.putInt(mFragment.getActivity().getContentResolver(),
                                            "OSDTransparency", mOSDTransparencyVal);
                                }
                                return true;
                                
                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!v.isFocusable()) {
                                        return true;
                                    }
                                    if(mOSDTransparencyVal == 0){
                                        mOSDTransparencyVal = 10;
                                    }else{
                                        mOSDTransparencyVal--;
                                    }
                                    mOSDTransparencyContentTxt.setText(mOSDTransparencyStr[mOSDTransparencyVal]);
                                    
                                    mFragment.mLogic.setOSDTransparency(mOSDTransparencyVal);
                                    Settings.System.putInt(mFragment.getActivity().getContentResolver(),
                                            "OSDTransparency", mOSDTransparencyVal);
                                }
                                return true;
                            }
                        }
                    }
                    break;
                    
                }
                return false;
            }

        };

        OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                switch (view.getId()) {
                    case R.id.itemPictureMode: {
                        if (hasFocus) {
                            mPictureModeTitleTxt.setTextColor(mR.getColor(R.color.cyan));
                            view.findViewById(R.id.enum_context).setBackgroundResource(R.drawable.bar_bg_enum_cyan);
                        } else {
                            mPictureModeTitleTxt.setTextColor(Color.WHITE);
                            view.findViewById(R.id.enum_context).setBackgroundResource(R.drawable.bar_bg_enum_grey);
                        }
                    }
                        break;
                    case R.id.itemPictureContrast: {
                        if (hasFocus) {
                            mPictureContrastTitleTxt.setTextColor(mR.getColor(R.color.cyan));
                            mPictureContrastSeekBar.setProgressDrawable(mR.getDrawable(R.drawable.seekbar_progress2));
                            mPictureContrastSeekBar.setThumb(mR.getDrawable(R.drawable.seekbar_thumb2));
                        } else {
                            mPictureContrastTitleTxt.setTextColor(Color.WHITE);
                            mPictureContrastSeekBar.setProgressDrawable(mR.getDrawable(R.drawable.seekbar_progress1));
                            mPictureContrastSeekBar.setThumb(mR.getDrawable(R.drawable.seekbar_thumb1));
                        }
                    }
                        break;
                    case R.id.itemPictureBrightness: {
                        if (hasFocus) {
                            mPictureBrightnessTitleTxt.setTextColor(mR.getColor(R.color.cyan));
                            mPictureBrightnessSeekBar.setProgressDrawable(mR.getDrawable(R.drawable.seekbar_progress2));
                            mPictureBrightnessSeekBar.setThumb(mR.getDrawable(R.drawable.seekbar_thumb2));
                        } else {
                            mPictureBrightnessTitleTxt.setTextColor(Color.WHITE);
                            mPictureBrightnessSeekBar.setProgressDrawable(mR.getDrawable(R.drawable.seekbar_progress1));
                            mPictureBrightnessSeekBar.setThumb(mR.getDrawable(R.drawable.seekbar_thumb1));
                        }
                    }
                        break;
                    case R.id.itemPictureHue: {
                        if (hasFocus) {
                            mPictureHueTitleTxt.setTextColor(mR.getColor(R.color.cyan));
                            mPictureHueSeekBar.setProgressDrawable(mR.getDrawable(R.drawable.seekbar_progress2));
                            mPictureHueSeekBar.setThumb(mR.getDrawable(R.drawable.seekbar_thumb2));
                        } else {
                            mPictureHueTitleTxt.setTextColor(Color.WHITE);
                            mPictureHueSeekBar.setProgressDrawable(mR.getDrawable(R.drawable.seekbar_progress1));
                            mPictureHueSeekBar.setThumb(mR.getDrawable(R.drawable.seekbar_thumb1));
                        }
                    }
                        break;
                    case R.id.itemPictureSharpness: {
                        if (hasFocus) {
                            mPictureSharpnessTitleTxt.setTextColor(mR.getColor(R.color.cyan));
                            mPictureSharpnessSeekBar.setProgressDrawable(mR.getDrawable(R.drawable.seekbar_progress2));
                            mPictureSharpnessSeekBar.setThumb(mR.getDrawable(R.drawable.seekbar_thumb2));
                        } else {
                            mPictureSharpnessTitleTxt.setTextColor(Color.WHITE);
                            mPictureSharpnessSeekBar.setProgressDrawable(mR.getDrawable(R.drawable.seekbar_progress1));
                            mPictureSharpnessSeekBar.setThumb(mR.getDrawable(R.drawable.seekbar_thumb1));
                        }
                    }
                        break;
                    case R.id.itemPictureSaturation: {
                        if (hasFocus) {
                            mPictureSaturationTitleTxt.setTextColor(mR.getColor(R.color.cyan));
                            mPictureSaturationSeekBar.setProgressDrawable(mR.getDrawable(R.drawable.seekbar_progress2));
                            mPictureSaturationSeekBar.setThumb(mR.getDrawable(R.drawable.seekbar_thumb2));
                        } else {
                            mPictureSaturationTitleTxt.setTextColor(Color.WHITE);
                            mPictureSaturationSeekBar.setProgressDrawable(mR.getDrawable(R.drawable.seekbar_progress1));
                            mPictureSaturationSeekBar.setThumb(mR.getDrawable(R.drawable.seekbar_thumb1));
                        }
                    }
                        break;
                    case R.id.itemPictureBackLight: {
                        if (hasFocus) {
                            mPictureBackLightTitleTxt.setTextColor(mR.getColor(R.color.cyan));
                            mPictureBackLightSeekBar.setProgressDrawable(mR.getDrawable(R.drawable.seekbar_progress2));
                            mPictureBackLightSeekBar.setThumb(mR.getDrawable(R.drawable.seekbar_thumb2));
                        } else {
                            mPictureBackLightTitleTxt.setTextColor(Color.WHITE);
                            mPictureBackLightSeekBar.setProgressDrawable(mR.getDrawable(R.drawable.seekbar_progress1));
                            mPictureBackLightSeekBar.setThumb(mR.getDrawable(R.drawable.seekbar_thumb1));
                        }
                    }
                        break;
                    case R.id.itemColorTemperature: {
                        if (hasFocus) {
                            mColorTemperatureTitleTxt.setTextColor(mR.getColor(R.color.cyan));
                            view.findViewById(R.id.enum_context).setBackgroundResource(R.drawable.bar_bg_enum_cyan);
                        } else {
                            mColorTemperatureTitleTxt.setTextColor(Color.WHITE);
                            view.findViewById(R.id.enum_context).setBackgroundResource(R.drawable.bar_bg_enum_grey);
                        }
                    }
                        break;
                    case R.id.itemRed: {
                        if (hasFocus) {
                            mRedTitleTxt.setTextColor(mR.getColor(R.color.cyan));
                            mRedSeekBar.setProgressDrawable(mR.getDrawable(R.drawable.seekbar_progress2));
                            mRedSeekBar.setThumb(mR.getDrawable(R.drawable.seekbar_thumb2));
                        } else {
                            mRedTitleTxt.setTextColor(Color.WHITE);
                            mRedSeekBar.setProgressDrawable(mR.getDrawable(R.drawable.seekbar_progress1));
                            mRedSeekBar.setThumb(mR.getDrawable(R.drawable.seekbar_thumb1));
                        }
                    }
                        break;
                    case R.id.itemGreen: {
                        if (hasFocus) {
                            mGreenTitleTxt.setTextColor(mR.getColor(R.color.cyan));
                            mGreenSeekBar.setProgressDrawable(mR.getDrawable(R.drawable.seekbar_progress2));
                            mGreenSeekBar.setThumb(mR.getDrawable(R.drawable.seekbar_thumb2));
                        } else {
                            mGreenTitleTxt.setTextColor(Color.WHITE);
                            mGreenSeekBar.setProgressDrawable(mR.getDrawable(R.drawable.seekbar_progress1));
                            mGreenSeekBar.setThumb(mR.getDrawable(R.drawable.seekbar_thumb1));
                        }
                    }
                        break;
                    case R.id.itemBlue: {
                        if (hasFocus) {
                            mBlueTitleTxt.setTextColor(mR.getColor(R.color.cyan));
                            mBlueSeekBar.setProgressDrawable(mR.getDrawable(R.drawable.seekbar_progress2));
                            mBlueSeekBar.setThumb(mR.getDrawable(R.drawable.seekbar_thumb2));
                        } else {
                            mBlueTitleTxt.setTextColor(Color.WHITE);
                            mBlueSeekBar.setProgressDrawable(mR.getDrawable(R.drawable.seekbar_progress1));
                            mBlueSeekBar.setThumb(mR.getDrawable(R.drawable.seekbar_thumb1));
                        }
                    }
                        break;
                    case R.id.itemHdmiFull: {
                        if (hasFocus) {
                            mHdmiFullTitleTxt.setTextColor(mR.getColor(R.color.cyan));
                            view.findViewById(R.id.enum_context).setBackgroundResource(R.drawable.bar_bg_enum_cyan);
                        } else {
                            mHdmiFullTitleTxt.setTextColor(Color.WHITE);
                            view.findViewById(R.id.enum_context).setBackgroundResource(R.drawable.bar_bg_enum_grey);
                        }
                    }
                        break;
                    case R.id.itemHdmiRgb: {
                        if (hasFocus) {
                            mHdmiRgbTitleTxt.setTextColor(mR.getColor(R.color.cyan));
                            view.findViewById(R.id.enum_context).setBackgroundResource(R.drawable.bar_bg_enum_cyan);
                        } else {
                            mHdmiRgbTitleTxt.setTextColor(Color.WHITE);
                            view.findViewById(R.id.enum_context).setBackgroundResource(R.drawable.bar_bg_enum_grey);
                        }
                    }
                        break;
                    case R.id.itemZoomMode: {
                        if (hasFocus) {
                            mZoomModeTitleTxt.setTextColor(mR.getColor(R.color.cyan));
                            view.findViewById(R.id.enum_context).setBackgroundResource(R.drawable.bar_bg_enum_cyan);
                        } else {
                            mZoomModeTitleTxt.setTextColor(Color.WHITE);
                            view.findViewById(R.id.enum_context).setBackgroundResource(R.drawable.bar_bg_enum_grey);
                        }
                    }
                        break;
                    case R.id.itemImageNoiseReduction: {
                        if (hasFocus) {
                            mImageNoiseReductionTitleTxt.setTextColor(mR.getColor(R.color.cyan));
                            view.findViewById(R.id.enum_context).setBackgroundResource(R.drawable.bar_bg_enum_cyan);
                        } else {
                            mImageNoiseReductionTitleTxt.setTextColor(Color.WHITE);
                            view.findViewById(R.id.enum_context).setBackgroundResource(R.drawable.bar_bg_enum_grey);
                        }
                    }
                        break;
                    case R.id.itemMpegNoiseReduction: {
                        if (hasFocus) {
                            mMpegNoiseReductionTitleTxt.setTextColor(mR.getColor(R.color.cyan));
                            view.findViewById(R.id.enum_context).setBackgroundResource(R.drawable.bar_bg_enum_cyan);
                        } else {
                            mMpegNoiseReductionTitleTxt.setTextColor(Color.WHITE);
                            view.findViewById(R.id.enum_context).setBackgroundResource(R.drawable.bar_bg_enum_grey);
                        }
                    }
                        break;
                    case R.id.itemOSDTransparency: {
                        if (hasFocus) {
                            mOSDTransparencyTitleTxt.setTextColor(mR.getColor(R.color.cyan));
                            view.findViewById(R.id.enum_context).setBackgroundResource(R.drawable.bar_bg_enum_cyan);
                        } else {
                            mOSDTransparencyTitleTxt.setTextColor(Color.WHITE);
                            view.findViewById(R.id.enum_context).setBackgroundResource(R.drawable.bar_bg_enum_grey);
                        }
                    }
                        break;
                    default:
                }
            }
        };

        mPictureModeLl.setOnKeyListener(onKeyListener);
        mPictureContrastLl.setOnKeyListener(onKeyListener);
        mPictureBrightnessLl.setOnKeyListener(onKeyListener);
        mPictureHueLl.setOnKeyListener(onKeyListener);
        mPictureSharpnessLl.setOnKeyListener(onKeyListener);
        mPictureSaturationLl.setOnKeyListener(onKeyListener);
        mPictureBackLightLl.setOnKeyListener(onKeyListener);
        mColorTemperatureLl.setOnKeyListener(onKeyListener);
        mRedLl.setOnKeyListener(onKeyListener);
        mGreenLl.setOnKeyListener(onKeyListener);
        mBlueLl.setOnKeyListener(onKeyListener);
        mHdmiFullLl.setOnKeyListener(onKeyListener);
        mHdmiRgbLl.setOnKeyListener(onKeyListener);
        mZoomModeLl.setOnKeyListener(onKeyListener);
        mImageNoiseReductionLl.setOnKeyListener(onKeyListener);
        mMpegNoiseReductionLl.setOnKeyListener(onKeyListener);
        mOSDTransparencyLl.setOnKeyListener(onKeyListener);

        mPictureModeLl.setOnFocusChangeListener(onFocusChangeListener);
        mPictureContrastLl.setOnFocusChangeListener(onFocusChangeListener);
        mPictureBrightnessLl.setOnFocusChangeListener(onFocusChangeListener);
        mPictureHueLl.setOnFocusChangeListener(onFocusChangeListener);
        mPictureSharpnessLl.setOnFocusChangeListener(onFocusChangeListener);
        mPictureSaturationLl.setOnFocusChangeListener(onFocusChangeListener);
        mPictureBackLightLl.setOnFocusChangeListener(onFocusChangeListener);
        mColorTemperatureLl.setOnFocusChangeListener(onFocusChangeListener);
        mRedLl.setOnFocusChangeListener(onFocusChangeListener);
        mGreenLl.setOnFocusChangeListener(onFocusChangeListener);
        mBlueLl.setOnFocusChangeListener(onFocusChangeListener);
        if(mItemHdmiFull.getFocusable()){
            mHdmiFullLl.setOnFocusChangeListener(onFocusChangeListener);
        } else {
            mHdmiFullLl.setFocusable(false);
            mHdmiFullLl.findViewById(R.id.enum_context).setBackgroundResource(R.drawable.bar_bg_enum_unfocusable);
        }
        if(mItemHdmiRgb.getFocusable()){
            mHdmiRgbLl.setOnFocusChangeListener(onFocusChangeListener);
        } else {
            mHdmiRgbLl.setFocusable(false);
            mHdmiRgbLl.findViewById(R.id.enum_context).setBackgroundResource(R.drawable.bar_bg_enum_unfocusable);
        }
        mZoomModeLl.setOnFocusChangeListener(onFocusChangeListener);
        mImageNoiseReductionLl.setOnFocusChangeListener(onFocusChangeListener);
        mMpegNoiseReductionLl.setOnFocusChangeListener(onFocusChangeListener);
        mOSDTransparencyLl.setOnFocusChangeListener(onFocusChangeListener);
        
    }

    /**
     * update item view containing value and focused change.
     * 
     * @param position index of item view need to be updated
     */
    void updateView(int index) {
        updateValue(index);
        updateFocus(index);
    }
    
    /**
     * update item view's value.
     * 
     * @param position index of item view need to be updated
     */
    void updateValue(int index){
        SettingItem item = mItems.get(index);
        switch (index) {
            case PictureConstants.INDEX_PICTURE_MODE:
                mPictureModeContentTxt.setText(item.getValues()[item.getCurValue()]);
                break;
            case PictureConstants.INDEX_PICTURE_CONTRAST:
                updateSeekBar(item, mPictureContrastSeekBar, mPictureContrastSeekBarNum);
                break;
            case PictureConstants.INDEX_PICTURE_BRIGHTNESS:
                updateSeekBar(item, mPictureBrightnessSeekBar, mPictureBrightnessSeekBarNum);
                break;
            case PictureConstants.INDEX_PICTURE_HUE:
                updateSeekBar(item, mPictureHueSeekBar, mPictureHueSeekBarNum);
                break;
            case PictureConstants.INDEX_PICTURE_SHARPNESS:
                updateSeekBar(item, mPictureSharpnessSeekBar, mPictureSharpnessSeekBarNum);
                break;
            case PictureConstants.INDEX_PICTURE_SATURATION:
                updateSeekBar(item, mPictureSaturationSeekBar, mPictureSaturationSeekBarNum);
                break;
            case PictureConstants.INDEX_PICTURE_BACKLIGHT:
                updateSeekBar(item, mPictureBackLightSeekBar, mPictureBackLightSeekBarNum);
                break;
            case PictureConstants.INDEX_PICTURE_TEMPERATURE:
                mColorTemperatureContentTxt.setText(item.getValues()[item.getCurValue()]);
                break;
            case PictureConstants.INDEX_RED:
                updateSeekBar(item, mRedSeekBar, mRedSeekBarNum);
                break;
            case PictureConstants.INDEX_GREEN:
                updateSeekBar(item, mGreenSeekBar, mGreenSeekBarNum);
                break;
            case PictureConstants.INDEX_BLUE:
                updateSeekBar(item, mBlueSeekBar, mBlueSeekBarNum);
                break;
            case PictureConstants.INDEX_HDMI_FULL:
                mHdmiFullContentTxt.setText(item.getValues()[item.getCurValue()]);
                break;
            case PictureConstants.INDEX_ZOOM_MODE:
                mZoomModeContentTxt.setText(item.getValues()[item.getCurValue()]);
                break;
            case PictureConstants.INDEX_IMAGE_NOISE_REDUCTION:
                mImageNoiseReductionContentTxt.setText(item.getValues()[item.getCurValue()]);
                break;
            case PictureConstants.INDEX_MPEG_NOISE_REDUCTION:
                mMpegNoiseReductionContentTxt.setText(item.getValues()[item.getCurValue()]);
            case PictureConstants.INDEX_OSD_TRANSPARENCY:
                mOSDTransparencyContentTxt.setText(item.getValues()[item.getCurValue()]);
                break;
        }
    }
    
    /**
     * update item view's focus change.
     * 
     * @param position index of item view need to be updated
     */
    void updateFocus(int index){
        switch (index) {
            case PictureConstants.INDEX_PICTURE_MODE:
                enumItemFocused(mPictureModeTitleTxt,mPictureModeLl);
                break;
            case PictureConstants.INDEX_PICTURE_CONTRAST:
                seekbarItemFocused(mPictureContrastTitleTxt,mPictureContrastSeekBar,mPictureContrastLl);
                break;
            case PictureConstants.INDEX_PICTURE_BRIGHTNESS:
                seekbarItemFocused(mPictureBrightnessTitleTxt,mPictureBrightnessSeekBar,mPictureBrightnessLl);
                break;
            case PictureConstants.INDEX_PICTURE_HUE:
                seekbarItemFocused(mPictureHueTitleTxt,mPictureHueSeekBar,mPictureHueLl);
                break;
            case PictureConstants.INDEX_PICTURE_SHARPNESS:
                seekbarItemFocused(mPictureSharpnessTitleTxt,mPictureSharpnessSeekBar,mPictureSharpnessLl);
                break;
            case PictureConstants.INDEX_PICTURE_SATURATION:
                seekbarItemFocused(mPictureSaturationTitleTxt,mPictureSaturationSeekBar,mPictureSaturationLl);
                break;
            case PictureConstants.INDEX_PICTURE_BACKLIGHT:
                seekbarItemFocused(mPictureBackLightTitleTxt,mPictureBackLightSeekBar,mPictureBackLightLl);
                break;
            case PictureConstants.INDEX_PICTURE_TEMPERATURE:
                enumItemFocused(mColorTemperatureTitleTxt,mColorTemperatureLl);
                break;
            case PictureConstants.INDEX_RED:
                seekbarItemFocused(mRedTitleTxt,mRedSeekBar,mRedLl);
                break;
            case PictureConstants.INDEX_GREEN:
                seekbarItemFocused(mGreenTitleTxt,mGreenSeekBar,mGreenLl);
                break;
            case PictureConstants.INDEX_BLUE:
                seekbarItemFocused(mBlueTitleTxt,mBlueSeekBar,mBlueLl);
                break;
            case PictureConstants.INDEX_HDMI_FULL:
            	enumItemFocused(mHdmiFullTitleTxt,mHdmiFullLl);
                break;
            case PictureConstants.INDEX_ZOOM_MODE:
                enumItemFocused(mZoomModeTitleTxt,mZoomModeLl);
                break;
            case PictureConstants.INDEX_IMAGE_NOISE_REDUCTION:
            	enumItemFocused(mImageNoiseReductionTitleTxt,mImageNoiseReductionLl);
                break;
            case PictureConstants.INDEX_MPEG_NOISE_REDUCTION:
                enumItemFocused(mMpegNoiseReductionTitleTxt,mMpegNoiseReductionLl);
            case PictureConstants.INDEX_OSD_TRANSPARENCY:
                enumItemFocused(mOSDTransparencyTitleTxt,mOSDTransparencyLl);
                break;
        }
    }

	/**
     * change the UI when seekbarItem has focused
     * @param seekBar
     * @param titleTxt
	 * @param linearLayout 
     */
    private void seekbarItemFocused(TextView titleTxt, SeekBar seekBar, LinearLayout linearLayout) {
    	titleTxt.setTextColor(mR.getColor(R.color.cyan));
    	seekBar.setProgressDrawable(mR.getDrawable(R.drawable.seekbar_progress2));
    	seekBar.setThumb(mR.getDrawable(R.drawable.seekbar_thumb2));
    	linearLayout.requestFocus();
    }
	
    /**
	  * change the UI when EnumItem dosen't has focused
	  * @param titleTxt
	  * @param linearLayout
	  */
    private void enumItemFocused(TextView titleTxt, LinearLayout linearLayout) {
    	titleTxt.setTextColor(mR.getColor(R.color.cyan));
    	linearLayout.findViewById(R.id.enum_context).setBackgroundResource(R.drawable.bar_bg_enum_cyan);
    	linearLayout.requestFocus();
    }
    
    /**
     * update seekbar item view. there has 3 type of the seekbar which has
     * different MAX attribute, so adjust separately
     * 
     * @param item corresponding data structure of seekbar item view
     * @param sb seekbar view
     * @param tv seekbar's value view
     */
    void updateSeekBar(SettingItem item, SeekBar sb, TextView tv) {
        int curValue = item.getCurValue();
        FrameLayout.LayoutParams paramsStrength = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        if (item.getStartValue() == 0 && item.getEndValue() == 100) {
            sb.setMax(Constants.MAX100);
            sb.setProgress(curValue);
            paramsStrength.leftMargin = curValue * 601 / 100 - 4;
        } else if (item.getStartValue() == -50 && item.getEndValue() == 50) {
            sb.setMax(Constants.MAX100);
            sb.setProgress(curValue + 50);
            paramsStrength.leftMargin = (curValue + 50) * 601 / 100 - 4;
        } else if (item.getStartValue() == 0 && item.getEndValue() == 128) {
            sb.setMax(Constants.MAX128);
            sb.setProgress(curValue);
            paramsStrength.leftMargin = curValue * 601 / 128 - 4;
        }
        tv.setText(String.valueOf(curValue));
        tv.setLayoutParams(paramsStrength);
        tv.setWidth(70);
    }
    
    private void setSeekBarView(TextView seekBarNum, int seekBarVal,LayoutParams params) {
        seekBarNum.setText(Integer.toString(seekBarVal));
        params.leftMargin = seekBarVal * Constants.ESEEKBAR_LAYOUTPARAMS + Constants.ESEEKBAR_ADJUST_LAYOUTPARAMS;
        seekBarNum.setLayoutParams(params);
        seekBarNum.setWidth(70);
    }
    private void setColorSeekBarView(TextView seekBarNum, int seekBarVal,LayoutParams params){
        seekBarNum.setText(Integer.toString(seekBarVal));
        params.leftMargin = seekBarVal * 601 / 128 + Constants.ESEEKBAR_ADJUST_LAYOUTPARAMS;
        seekBarNum.setLayoutParams(params);
        seekBarNum.setWidth(70);
    }
    
    private void setDialogValue(SettingItem item, int val) {
        item.setCurValue(val);
    }
    
}
