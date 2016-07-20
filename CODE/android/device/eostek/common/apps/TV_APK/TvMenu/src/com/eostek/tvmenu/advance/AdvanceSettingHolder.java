
package com.eostek.tvmenu.advance;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eostek.tvmenu.R;
import com.eostek.tvmenu.TvMenuActivity;
import com.eostek.tvmenu.utils.Constants;
import com.mstar.android.tv.TvFactoryManager;
import com.mstar.android.tv.TvParentalControlManager;
import com.mstar.android.tv.TvTimerManager;
import com.mstar.android.tvapi.common.vo.EnumSleepTimeState;
import com.mstar.android.tvapi.common.vo.StandardTime;

public class AdvanceSettingHolder {

    private AdvanceSettingFragment mFragment;

    public AdvanceSettingLogic mLogic;
    
    public OffTimeFragment mOffTime;
    
    private TvTimerManager tvTimerManager = null;

    private final static int TRADITIONAL_CHINESE = 0;

    private final static int ENGLISH = 1;

    private final static int SIMPLE_CHINESE = 2;

    //the layout of Item
//    private LinearLayout mItemVersionLl;

    private LinearLayout mItemLanguageSelectLl;

    private LinearLayout mItemResetLl;

    private LinearLayout mItemLocalUpdateLl;

    private LinearLayout mItemNetworkUpdateLl;

    private LinearLayout mItemSleepModeLl;

    private LinearLayout mItemSaveModeLl;

    private LinearLayout mItemAutoPowerOnLl;

    private LinearLayout mItemResetPwdLl;
    
    private LinearLayout mItemDaylightSavingModeLl;
    
    private LinearLayout mItemTimezoneLl;
    
//    private LinearLayout mItemPVRFileSystemLl;
    
    private LinearLayout mOSDTimeOutLl;
    
    private LinearLayout mItemOffTimeLl;
    
    private LinearLayout mItemOnTimeLl;
    
    private LinearLayout mItemParentalGuidanceLl;

    //the title of enumItem
    private TextView mVersionTitleTxt;

    //the value of enumItem
    private TextView mVersionContentTxt;

    private TextView mLanguageSelectTitleTxt;

    private TextView mLanguageSelectContentTxt;

    private TextView mResetTitleTxt;

    private TextView mLocalUpdateTitleTxt;

    private TextView mNetworkUpdateTitleTxt;

    private TextView mSleepModeTitleTxt;

    private TextView mSleepModeContentTxt;

    private TextView mSaveModeTitleTxt;

    private TextView mSaveModeContentTxt;

    private TextView mAutoPowerOnTitleTxt;

    private TextView mAutoPowerOnContentTxt;

    private TextView mResetPwdTitleTxt;
    
    private TextView mDaylightSavingModeTitleTxt;

    private TextView mDaylightSavingModeContentTxt;
    
    private TextView mTimezoneTitleTxt;
    
//    private TextView mPVRFileSystemTitleTxt;
    
    private TextView mOSDTimeOutTitleTxt;
    private TextView mOSDTimeOutContentTxt;
    
    private TextView mOffTimeTitleTxt;
    private TextView mOffTimeContentTxt;
    
    private TextView mOnTimeTitleTxt;
    private TextView mOnTimeContentTxt;
    
    private TextView mParentalGuidanceTitleTxt;
    private TextView mParentalGuidanceContentTxt;

    //string array of AdvanceSetting title
    private String[] mTitleAdvanceSettingStr;

    //string array of language
    private String[] mLanguageSelectStr;

    //string array of SleepMode
    private String[] mSleepModeStr;

    //string array of SaveMode
    private String[] mSaveModeStr;

 	//string array of AutoPowerOn
    private String[] mAutoPowerOnStr;
    
    private String[] mDSTStr;
    
    private String[] mOSDTimeOutStr;
    
    private String[] mParentalGuidanceStr;

    private int mLanguageSelectVal;

    private int mSleepModeVal;

    private int mSaveModeVal;

    private int mAutoPowerOnVal;
    
    private int mDSTVal;
    
    private int mOSDTimeOutVal;
    
    private int mParentalGuidanceVal;
    
    private String mOffTimeStr;
    
    private int mOffTimeVal;
    
    private int mOffTimeHourVal;
    
    private int mOffTimeMinutesVal;
    
    private String mOnTimeStr;
    
    private int mOnTimeVal;
    
    private int mOnTimeHourVal;
    
    private int mOnTimeMinutesVal;
    
    private String mLocaleCountry;

    private Locale mCurrentLocale;
    
    Resources mR;

    public AdvanceSettingHolder(AdvanceSettingFragment f) {
        mFragment = f;
        mR = mFragment.getActivity().getResources();
    }
    
    public AdvanceSettingHolder(OffTimeFragment offTimeFragment) {
        mOffTime = offTimeFragment;
    }

    /**
     * init the view of all items and item_titles and item_values
     * 
     * @param view
     */
    protected void initView(View view) {
        tvTimerManager = TvTimerManager.getInstance();
        StandardTime mCurTime = tvTimerManager.getCurTimer();
        
        Log.e("test", "tvTimerManager.isOffTimerEnable() =" + tvTimerManager.isOffTimerEnable());
        Log.e("test", "OffTime =" + (tvTimerManager.getOffTimer().year + "-" + tvTimerManager.getOffTimer().month + "-" + tvTimerManager.getOffTimer().monthDay + "  " + tvTimerManager.getOffTimer().hour + ":" + tvTimerManager.getOffTimer().minute));
        Log.e("test", "system time" + formatDate(System.currentTimeMillis(), "yyyy-MM-dd HH:mm") );
        Log.e("test", "tvTimerManager.getCurTimer()" +mCurTime.year + "-" + mCurTime.month + "-" + mCurTime.monthDay+ mCurTime.hour + mCurTime.minute);
        
        mTitleAdvanceSettingStr = mFragment.getActivity().getResources().getStringArray(R.array.setting_advance);
        mLanguageSelectStr = mFragment.getActivity().getResources().getStringArray(R.array.language);
        mSleepModeStr = mFragment.getActivity().getResources().getStringArray(R.array.time_setting_sleep_time);
        mSaveModeStr = mFragment.getActivity().getResources().getStringArray(R.array.turnon_off);
        mAutoPowerOnStr = mFragment.getActivity().getResources().getStringArray(R.array.turnon_off);
        mDSTStr = mFragment.getActivity().getResources().getStringArray(R.array.dst);
        mOSDTimeOutStr = mFragment.getActivity().getResources().getStringArray(R.array.osd_time_out_vals);
        mParentalGuidanceStr = mFragment.getActivity().getResources().getStringArray(R.array.parental_guidance_vals);

        // Version
//        mItemVersionLl = (LinearLayout) view.findViewById(R.id.item_version_ll);
//        mVersionTitleTxt = (TextView) mItemVersionLl.findViewById(R.id.title_txt);
//        mVersionContentTxt = (TextView) mItemVersionLl.findViewById(R.id.value);

        // LanguageSelect
        mItemLanguageSelectLl = (LinearLayout) view.findViewById(R.id.item_language_select_ll);
        mLanguageSelectTitleTxt = (TextView) mItemLanguageSelectLl.findViewById(R.id.title_txt);
        mLanguageSelectContentTxt = (TextView) mItemLanguageSelectLl.findViewById(R.id.value);

        // Reset
        mItemResetLl = (LinearLayout) view.findViewById(R.id.item_reset_ll);
        mResetTitleTxt = (TextView) mItemResetLl.findViewById(R.id.title_txt);

        // LocalUpdate
        mItemLocalUpdateLl = (LinearLayout) view.findViewById(R.id.item_local_update_ll);
        mLocalUpdateTitleTxt = (TextView) mItemLocalUpdateLl.findViewById(R.id.title_txt);

        // NetworkUpdate
        mItemNetworkUpdateLl = (LinearLayout) view.findViewById(R.id.item_network_update_ll);
        mNetworkUpdateTitleTxt = (TextView) mItemNetworkUpdateLl.findViewById(R.id.title_txt);

        // SleepMode
        mItemSleepModeLl = (LinearLayout) view.findViewById(R.id.item_sleep_mode_ll);
        mSleepModeTitleTxt = (TextView) mItemSleepModeLl.findViewById(R.id.title_txt);
        mSleepModeContentTxt = (TextView) mItemSleepModeLl.findViewById(R.id.value);

        // SaveMode
        mItemSaveModeLl = (LinearLayout) view.findViewById(R.id.item_save_mode_ll);
        mSaveModeTitleTxt = (TextView) mItemSaveModeLl.findViewById(R.id.title_txt);
        mSaveModeContentTxt = (TextView) mItemSaveModeLl.findViewById(R.id.value);

        // AutoPowerOn
        mItemAutoPowerOnLl = (LinearLayout) view.findViewById(R.id.item_auto_poweron_ll);
        mAutoPowerOnTitleTxt = (TextView) mItemAutoPowerOnLl.findViewById(R.id.title_txt);
        mAutoPowerOnContentTxt = (TextView) mItemAutoPowerOnLl.findViewById(R.id.value);

        // ResetPwd
        mItemResetPwdLl = (LinearLayout) view.findViewById(R.id.item_reset_pwd_ll);
        mResetPwdTitleTxt = (TextView) mItemResetPwdLl.findViewById(R.id.title_txt);
        
        //DaylightSavingMode
        mItemDaylightSavingModeLl = (LinearLayout) view.findViewById(R.id.item_daylight_saving_mode_ll);
        mDaylightSavingModeTitleTxt = (TextView) mItemDaylightSavingModeLl.findViewById(R.id.title_txt);
        mDaylightSavingModeContentTxt = (TextView) mItemDaylightSavingModeLl.findViewById(R.id.value);
        
        //Timezone
        mItemTimezoneLl = (LinearLayout) view.findViewById(R.id.item_timezone_ll);
        mTimezoneTitleTxt = (TextView) mItemTimezoneLl.findViewById(R.id.title_txt);
        
        //PVRFileSystem
//        mItemPVRFileSystemLl = (LinearLayout) view.findViewById(R.id.item_pvr_file_system_ll);
//        mPVRFileSystemTitleTxt = (TextView) mItemPVRFileSystemLl.findViewById(R.id.title_txt);
        
        //OSDTimeOut
        mOSDTimeOutLl = (LinearLayout) view.findViewById(R.id.itemOSDTimeOut);
        mOSDTimeOutContentTxt = (TextView) mOSDTimeOutLl.findViewById(R.id.value);
        mOSDTimeOutTitleTxt = (TextView) mOSDTimeOutLl.findViewById(R.id.title_txt);
        
        //OffTime
        mItemOffTimeLl = (LinearLayout) view.findViewById(R.id.item_off_time_ll);
        mOffTimeTitleTxt = (TextView) mItemOffTimeLl.findViewById(R.id.title_txt);
        mOffTimeContentTxt = (TextView) mItemOffTimeLl.findViewById(R.id.value);
        
        //OnTime
        mItemOnTimeLl = (LinearLayout) view.findViewById(R.id.item_on_time_ll);
        mOnTimeTitleTxt = (TextView) mItemOnTimeLl.findViewById(R.id.title_txt);
        mOnTimeContentTxt = (TextView) mItemOnTimeLl.findViewById(R.id.value);
        
        //mItemParentalGuidanceLl
        mItemParentalGuidanceLl = (LinearLayout) view.findViewById(R.id.item_parental_guidance_ll);
        mParentalGuidanceTitleTxt = (TextView) mItemParentalGuidanceLl.findViewById(R.id.title_txt);
        mParentalGuidanceContentTxt = (TextView) mItemParentalGuidanceLl.findViewById(R.id.value);
        
//        mVersionTitleTxt.setText(mTitleAdvanceSettingStr[Constants.VERSION_TITLE]);
        mLanguageSelectTitleTxt.setText(mTitleAdvanceSettingStr[Constants.LANGUAGE_SELECT_TITLE]);
        mResetTitleTxt.setText(mTitleAdvanceSettingStr[Constants.RESET_TITLE]);
        mLocalUpdateTitleTxt.setText(mTitleAdvanceSettingStr[Constants.LOCAL_UPDATE_TITLE]);
        mNetworkUpdateTitleTxt.setText(mTitleAdvanceSettingStr[Constants.NETWORK_UPDATE_TITLE]);
        mSleepModeTitleTxt.setText(mTitleAdvanceSettingStr[Constants.SLEEP_MODE_TITLE]);
        mSaveModeTitleTxt.setText(mTitleAdvanceSettingStr[Constants.SAVE_MODE_TITLE]);
        mAutoPowerOnTitleTxt.setText(mTitleAdvanceSettingStr[Constants.AUTO_POWERON_TITLE]);
        mResetPwdTitleTxt.setText(mTitleAdvanceSettingStr[Constants.RESET_PWD_TITLE]);
        mDaylightSavingModeTitleTxt.setText(mTitleAdvanceSettingStr[Constants.DST_MODE_TITLE]);
        mTimezoneTitleTxt.setText(mTitleAdvanceSettingStr[Constants.TIMEZONE_TITLE]); 
//        mPVRFileSystemTitleTxt.setText(mTitleAdvanceSettingStr[Constants.PVR_FILE_SYSTEM_TITLE]); 
        mOSDTimeOutTitleTxt.setText(mTitleAdvanceSettingStr[Constants.OSD_TIME_OUT_TITLE]); 
        mOffTimeTitleTxt.setText(mTitleAdvanceSettingStr[Constants.OFF_TIME_TITLE]); 
        mOnTimeTitleTxt.setText(mTitleAdvanceSettingStr[Constants.ON_TIME_TITLE]);
        mParentalGuidanceTitleTxt.setText(mTitleAdvanceSettingStr[Constants.PARENTAL_GUIDANCE_TITLE]);
    }

    /**
     * get data from system and init this data to each item
     */
    protected void initData() {

        // get Version
        // mVersionContentTxt.setText(mFragment.mLogic.readSysIni());
        //mVersionContentTxt.setText("MS2289-LCC-A-NST-ND4");

        // get LanguageSelect state
        Configuration conf = mFragment.getActivity().getResources().getConfiguration();
        mCurrentLocale = conf.locale;
        mLocaleCountry = mCurrentLocale.getLanguage();
        String country = mCurrentLocale.getCountry();
        if (country.equals("TW")) {
            mLanguageSelectVal = TRADITIONAL_CHINESE;
            mLanguageSelectContentTxt.setText(mLanguageSelectStr[TRADITIONAL_CHINESE]);
        } else if (country.equals("CN")) {
            mLanguageSelectVal = SIMPLE_CHINESE;
            mLanguageSelectContentTxt.setText(mLanguageSelectStr[SIMPLE_CHINESE]);
        } else if (country.equals("US")) {
            mLanguageSelectVal = ENGLISH;
            mLanguageSelectContentTxt.setText(mLanguageSelectStr[ENGLISH]);
        }

        // get SleepMode state
        mSleepModeVal = TvTimerManager.getInstance().getSleepMode().ordinal();
        
        mSleepModeContentTxt.setText(mSleepModeStr[mSleepModeVal]);

        // get SaveMode state
        mSaveModeVal = Settings.System.getInt(mFragment.getActivity().getContentResolver(), "savemode", 0);
        mSaveModeContentTxt.setText(mSaveModeStr[mSaveModeVal]);

        // get AutoPowerOn state
        if (TvFactoryManager.getInstance().getAcPowerOnMode() == Constants.AUTO_POWERON_ON) {
            mAutoPowerOnVal = Constants.ON;
            mAutoPowerOnContentTxt.setText(mAutoPowerOnStr[mAutoPowerOnVal]);
        } else {
            mAutoPowerOnVal = Constants.OFF;
            mAutoPowerOnContentTxt.setText(mAutoPowerOnStr[mAutoPowerOnVal]);
        }
        
        //get DST stateprivate TvTimerManager tvTimerManager = null;
        mDSTVal = TvTimerManager.getInstance().getDaylightSavingMode();
        switch(mDSTVal){
            case Constants.DAYLIGHT_SAVING_AUTO:{
                mDaylightSavingModeContentTxt.setText(mDSTStr[Constants.DAYLIGHT_SAVING_AUTO]);
            }
            break;
            case Constants.DAYLIGHT_SAVING_USER_OFF:{
                mDaylightSavingModeContentTxt.setText(mDSTStr[Constants.DAYLIGHT_SAVING_USER_OFF]);
            }
            break;
            case Constants.DAYLIGHT_SAVING_USER_ON:{
                mDaylightSavingModeContentTxt.setText(mDSTStr[Constants.DAYLIGHT_SAVING_USER_ON]);
            }
            break;
            default:
                break;
        }
        
        mOSDTimeOutVal = getOSDTimeOutVal();
        mOSDTimeOutContentTxt.setText(mOSDTimeOutStr[mOSDTimeOutVal]);
        
        //OffTime
        if (tvTimerManager.isOffTimerEnable() == false) {
            mOffTimeVal = 0;
            mOffTimeContentTxt.setText(mAutoPowerOnStr[mOffTimeVal]);
        }else{
            mOffTimeHourVal = tvTimerManager.getOffTimer().hour;
            mOffTimeMinutesVal = tvTimerManager.getOffTimer().minute;
            mOffTimeStr = Integer.toString(mOffTimeHourVal) + ":" + Integer.toString(mOffTimeMinutesVal);
            mOffTimeContentTxt.setText(mOffTimeStr);
        }
        
      //OnTime
        if (tvTimerManager.isOnTimerEnable() == false) {
            mOnTimeVal = 0;
            mOnTimeContentTxt.setText(mAutoPowerOnStr[mOnTimeVal]);
        }else{
            mOnTimeHourVal = tvTimerManager.getOnTimer().hour;
            mOnTimeMinutesVal = tvTimerManager.getOnTimer().minute;
            mOnTimeStr = Integer.toString(mOnTimeHourVal) + ":" + Integer.toString(mOnTimeMinutesVal);
            mOnTimeContentTxt.setText(mOnTimeStr);
        }
        
        //ParentalGuidance
        mParentalGuidanceVal = mFragment.mLogic.getParentalControlRating();
        mParentalGuidanceContentTxt.setText(mParentalGuidanceStr[mParentalGuidanceVal]);
        
    }

    /**
     * set OnKeyListener and OnFocusListener
     */
    protected void setListener() {

        OnKeyListener OnKeyListener = new OnKeyListener() {

            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                switch (view.getId()) {
//	                case R.id.item_version_ll: {
//	                	 if (event.getAction() == KeyEvent.ACTION_DOWN) {
//	                            switch (keyCode) {
//	                                case KeyEvent.KEYCODE_DPAD_RIGHT: 
//	                                	return true;
//	                            }
//	                        }
//	                }
//	                break;
                
                    case R.id.item_language_select_ll: {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                    // 0:TW 1:US 2:CN
                                    if (mLanguageSelectVal == TRADITIONAL_CHINESE) {
                                        mLanguageSelectContentTxt.setText(mLanguageSelectStr[++mLanguageSelectVal]);
                                        mFragment.mLogic.setLanguage("en_US");
                                    } else if (mLanguageSelectVal == ENGLISH) {
                                        mLanguageSelectContentTxt.setText(mLanguageSelectStr[++mLanguageSelectVal]);
                                        mFragment.mLogic.setLanguage("zh_CN");
                                    } else if (mLanguageSelectVal == SIMPLE_CHINESE) {
                                        mLanguageSelectContentTxt.setText(mLanguageSelectStr[TRADITIONAL_CHINESE]);
                                        mFragment.mLogic.setLanguage("zh_TW");// set language
                                        mLanguageSelectVal = TRADITIONAL_CHINESE;
                                    }

                                }
                                return true;
                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                    if (mLanguageSelectVal == SIMPLE_CHINESE) {
                                        mLanguageSelectContentTxt.setText(mLanguageSelectStr[--mLanguageSelectVal]);
                                        mFragment.mLogic.setLanguage("en_US");
                                    } else if (mLanguageSelectVal == ENGLISH) {
                                        mLanguageSelectContentTxt.setText(mLanguageSelectStr[--mLanguageSelectVal]);
                                        mFragment.mLogic.setLanguage("zh_TW");
                                    } else if (mLanguageSelectVal == TRADITIONAL_CHINESE) {
                                        mLanguageSelectContentTxt.setText(mLanguageSelectStr[SIMPLE_CHINESE]);
                                        mFragment.mLogic.setLanguage("zh_CN");
                                        mLanguageSelectVal = SIMPLE_CHINESE;
                                    }

                                }
                                    break;
                            }
                        }
                    }
                        break;

                    case R.id.item_reset_ll: {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_ENTER:{
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                    mFragment.mLogic.update(SystemRestoreFactoryActivity.class);
                                }
                                case KeyEvent.KEYCODE_DPAD_RIGHT: 
                                	return true;
                            }
                        }
                    }
                        break;

                    case R.id.item_local_update_ll: {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_ENTER:{
                                    if (!view.isFocusable()) {
                                        return true;
                                    }

                                    mFragment.mLogic.update(SystemLocalUpdateActivity.class);
                                }
                                case KeyEvent.KEYCODE_DPAD_RIGHT: 
                                	return true;
                            }
                        }
                    }
                        break;

                    case R.id.item_network_update_ll: {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_ENTER: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }

                                    mFragment.mLogic.startNetUpdate();
                                }
                                case KeyEvent.KEYCODE_DPAD_RIGHT: 
                                	return true;
                            }
                        }
                    }
                        break;

                    case R.id.item_sleep_mode_ll: {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                    // 0:off 1:10min 2:20min 3:30min 4:60min
                                    // 5:90min6:120min 7:180min 8:240min
                                    if(mSleepModeVal < Constants.MIN_240){
                                    	mSleepModeContentTxt.setText(mSleepModeStr[++mSleepModeVal]);
                                    	setSleepMode(mSleepModeVal);
                                    } else if (mSleepModeVal == Constants.MIN_240) {
                                    	mSleepModeVal = Constants.OFF;
                                        mSleepModeContentTxt.setText(mSleepModeStr[mSleepModeVal]);
                                        setSleepMode(mSleepModeVal);
                                    }
                                }
                                return true;

                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                	
                                	if(mSleepModeVal <= Constants.MIN_240 && mSleepModeVal > Constants.OFF){
                                    	mSleepModeContentTxt.setText(mSleepModeStr[--mSleepModeVal]);
                                    	setSleepMode(mSleepModeVal);
                                    } else if (mSleepModeVal == Constants.OFF) {
                                    	mSleepModeVal = Constants.MIN_240;
                                        mSleepModeContentTxt.setText(mSleepModeStr[mSleepModeVal]);
                                        setSleepMode(mSleepModeVal);
                                    }
                                }
                            }
                        }
                    }
                        break;

                    case R.id.item_save_mode_ll: {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_LEFT:
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                    if (mSaveModeVal == Constants.SAVE_MODE_OFF) {
                                        mSaveModeContentTxt.setText(mAutoPowerOnStr[Constants.SAVE_MODE_ON]);
                                        // save SaveMode state to deb
                                        Settings.System.putInt(mFragment.getActivity().getContentResolver(),
                                                "savemode", 1);
                                        mSaveModeVal = Constants.SAVE_MODE_ON;
                                    } else {
                                        mSaveModeContentTxt.setText(mAutoPowerOnStr[Constants.SAVE_MODE_OFF]);
                                        Settings.System.putInt(mFragment.getActivity().getContentResolver(),
                                                "savemode", 0);
                                        mSaveModeVal = Constants.SAVE_MODE_OFF;
                                    }
                                }
                                return true;
                            }
                        }
                    }
                        break;

                    case R.id.item_auto_poweron_ll: {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_LEFT:
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }

                                    if (mAutoPowerOnVal == Constants.ON) {
                                        mAutoPowerOnContentTxt.setText(mAutoPowerOnStr[Constants.AUTO_POWERON_OFF]);
                                        // set PowerOnMode to off
                                        TvFactoryManager.getInstance().setAcPowerOnMode(Constants.AUTO_POWERON_OFF);
                                        // TvFactoryManager.getInstance().setPowerOnLogoMode(0);
                                        mAutoPowerOnVal = Constants.AUTO_POWERON_OFF;
                                    } else {
                                        mAutoPowerOnContentTxt.setText(mAutoPowerOnStr[Constants.AUTO_POWERON_ON]);
                                        // set PowerOnMode to on
                                        TvFactoryManager.getInstance().setAcPowerOnMode(Constants.AUTO_POWERON_ON);
                                        // TvFactoryManager.getInstance().setPowerOnLogoMode(1);
                                        mAutoPowerOnVal = Constants.AUTO_POWERON_ON;
                                    }
                                }
                                    return true;
                            }
                        }
                    }
                        break;

                    case R.id.item_reset_pwd_ll: {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_ENTER: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                    // start PasswordDialog
                                    mFragment.creatChangePasswordDialog();
                                }
                                case KeyEvent.KEYCODE_DPAD_RIGHT: 
                                	return true;
                            }
                        }
                    }
                        break;
                        
                    case R.id.item_daylight_saving_mode_ll: {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                   
                                    mDSTVal = TvTimerManager.getInstance().getDaylightSavingMode();
                                    if(mDSTVal == 2){
                                        TvTimerManager.getInstance().setDaylightSavingMode(Constants.DAYLIGHT_SAVING_AUTO);
                                        mDSTVal = 0;
                                    }else{
                                        TvTimerManager.getInstance().setDaylightSavingMode(++mDSTVal);
                                    }
                                    mDaylightSavingModeContentTxt.setText(mDSTStr[mDSTVal]);
                                }
                                return true;

                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                   
                                    mDSTVal = TvTimerManager.getInstance().getDaylightSavingMode();
                                    if(mDSTVal == 0){
                                        TvTimerManager.getInstance().setDaylightSavingMode(Constants.DAYLIGHT_SAVING_USER_ON);
                                        mDSTVal = 2;
                                    }else{
                                        TvTimerManager.getInstance().setDaylightSavingMode(--mDSTVal);
                                    }
                                    mDaylightSavingModeContentTxt.setText(mDSTStr[mDSTVal]);
                                }
                            }
                        }
                    }
                        break;
                        
                    case R.id.item_timezone_ll: {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_ENTER: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                    
                                    mFragment.mLogic.startSettingActivity();
                                }
                                case KeyEvent.KEYCODE_DPAD_RIGHT: 
                                    return true;
                            }
                        }
                    }
                        break;
                        
//                    case R.id.item_pvr_file_system_ll: {
//                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                            switch (keyCode) {
//                                case KeyEvent.KEYCODE_ENTER: {
//                                    if (!view.isFocusable()) {
//                                        return true;
//                                    }
//                                    
//                                    mFragment.mLogic.startPVROptionActivity();
//                                }
//                                case KeyEvent.KEYCODE_DPAD_RIGHT: 
//                                    return true;
//                            }
//                        }
//                    }
//                        break;
                        
                    case R.id.itemOSDTimeOut:{
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                    if(mOSDTimeOutVal == Constants.OSD_TIMEOUT_VAL_ALWAYS){
                                        mOSDTimeOutVal = Constants.OSD_TIMEOUT_VAL_5S;
                                    }else{
                                        mOSDTimeOutVal++;
                                    }
                                    
                                    mOSDTimeOutContentTxt.setText(mOSDTimeOutStr[mOSDTimeOutVal]);
                                    setOSDTimeOutVal(mOSDTimeOutVal);
                                }
                                return true;
                                
                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                    if(mOSDTimeOutVal == Constants.OSD_TIMEOUT_VAL_5S){
                                        mOSDTimeOutVal = Constants.OSD_TIMEOUT_VAL_ALWAYS;
                                    }else{
                                        mOSDTimeOutVal--;
                                    }
                                    
                                    mOSDTimeOutContentTxt.setText(mOSDTimeOutStr[mOSDTimeOutVal]);
                                    setOSDTimeOutVal(mOSDTimeOutVal);
                                }
                                return true;
                            }
                        }
                    }
                    break;
                    
                    case R.id.item_off_time_ll: {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT:
                                case KeyEvent.KEYCODE_DPAD_LEFT:
                                case KeyEvent.KEYCODE_ENTER: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }

                                  //start OffTimeFragment
                                    OffTimeFragment offTime = new OffTimeFragment(mFragment);
                                    FragmentManager fm = mFragment.getFragmentManager();
                                    // fm.popBackStack();
                                    FragmentTransaction ft = fm.beginTransaction();
                                    ft.setCustomAnimations(R.anim.menu_anim_rightin,R.anim.menu_anim_leftout);
                                    ft.replace(R.id.content_setting, offTime);
                                    ft.commit();
                                }
                                    return true;
                            }
                        }
                    }
                        break;
                        
                    case R.id.item_on_time_ll: {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT:
                                case KeyEvent.KEYCODE_DPAD_LEFT:
                                case KeyEvent.KEYCODE_ENTER: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }

                                    //start OnTimeFragment
                                    OnTimeFragment onTime = new OnTimeFragment(mFragment);
                                    FragmentManager fm = mFragment.getFragmentManager();
                                    // fm.popBackStack();
                                    FragmentTransaction ft = fm.beginTransaction();
                                    ft.setCustomAnimations(R.anim.menu_anim_rightin,R.anim.menu_anim_leftout);
                                    ft.replace(R.id.content_setting, onTime);
                                    ft.commit();
                                }
                                    return true;
                            }
                        }
                    }
                        break;
                        
                    case R.id.item_parental_guidance_ll:{
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                    if(mParentalGuidanceVal == Constants.PARENTAL_CONTENT_VIOLENCE_SEX_DRUGS){
                                        mParentalGuidanceVal = Constants.PARENTAL_CONTENT_NONE;
                                    }else{
                                        mParentalGuidanceVal++;
                                    }
                                    
                                    mParentalGuidanceContentTxt.setText(mParentalGuidanceStr[mParentalGuidanceVal]);
                                    mFragment.mLogic.SetParentalControlRating(mParentalGuidanceVal);
                                }
                                return true;
                                
                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                    if(mParentalGuidanceVal == Constants.PARENTAL_CONTENT_NONE){
                                        mParentalGuidanceVal = Constants.PARENTAL_CONTENT_VIOLENCE_SEX_DRUGS;
                                    }else{
                                        mParentalGuidanceVal--;
                                    }
                                    
                                    mParentalGuidanceContentTxt.setText(mParentalGuidanceStr[mParentalGuidanceVal]);
                                    mFragment.mLogic.SetParentalControlRating(mParentalGuidanceVal);
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

        // set Items OnKeyListener
        mItemLanguageSelectLl.setOnKeyListener(OnKeyListener);
        mItemResetLl.setOnKeyListener(OnKeyListener);
        mItemLocalUpdateLl.setOnKeyListener(OnKeyListener);
        mItemNetworkUpdateLl.setOnKeyListener(OnKeyListener);
        mItemSleepModeLl.setOnKeyListener(OnKeyListener);
        mItemSaveModeLl.setOnKeyListener(OnKeyListener);
        mItemAutoPowerOnLl.setOnKeyListener(OnKeyListener);
        mItemResetPwdLl.setOnKeyListener(OnKeyListener);
        mItemDaylightSavingModeLl.setOnKeyListener(OnKeyListener);
        mItemTimezoneLl.setOnKeyListener(OnKeyListener);
//        mItemPVRFileSystemLl.setOnKeyListener(OnKeyListener);
        mOSDTimeOutLl.setOnKeyListener(OnKeyListener);
        mItemOffTimeLl.setOnKeyListener(OnKeyListener);
        mItemOnTimeLl.setOnKeyListener(OnKeyListener);
        mItemParentalGuidanceLl.setOnKeyListener(OnKeyListener);
        
        OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                switch (view.getId()) {
//                    case R.id.item_version_ll: {
//                        if (hasFocus) {
//                            // change Title,Background to blue
//                            enumItemFocused(view, mVersionTitleTxt);
//                        } else {
//                            // change Title,Background to grey
//                            enumItemUnfocused(view, mVersionTitleTxt);
//                        }
//                    }
//                        break;
                    case R.id.item_language_select_ll: {
                        if (hasFocus) {
                            enumItemFocused(view, mLanguageSelectTitleTxt);
                        } else {
                            enumItemUnfocused(view, mLanguageSelectTitleTxt);
                        }
                    }
                        break;
                    case R.id.item_reset_ll: {
                        if (hasFocus) {
                            buttomItemFocused(view, mResetTitleTxt);
                        } else {
                            buttomItemUnfocused(view, mResetTitleTxt);
                        }
                    }
                        break;
                    case R.id.item_local_update_ll: {
                        if (hasFocus) {
                            buttomItemFocused(view, mLocalUpdateTitleTxt);
                        } else {
                            buttomItemUnfocused(view, mLocalUpdateTitleTxt);
                        }
                    }
                        break;
                    case R.id.item_network_update_ll: {
                        if (hasFocus) {
                            buttomItemFocused(view, mNetworkUpdateTitleTxt);
                        } else {
                            buttomItemUnfocused(view, mNetworkUpdateTitleTxt);
                        }
                    }
                        break;
                    case R.id.item_sleep_mode_ll: {
                        if (hasFocus) {
                            enumItemFocused(view, mSleepModeTitleTxt);
                        } else {
                            enumItemUnfocused(view, mSleepModeTitleTxt);
                        }
                    }
                        break;
                    case R.id.item_save_mode_ll: {
                        if (hasFocus) {
                            enumItemFocused(view, mSaveModeTitleTxt);
                        } else {
                            enumItemUnfocused(view, mSaveModeTitleTxt);
                        }
                    }
                        break;
                    case R.id.item_auto_poweron_ll: {
                        if (hasFocus) {
                            enumItemFocused(view, mAutoPowerOnTitleTxt);
                        } else {
                            enumItemUnfocused(view, mAutoPowerOnTitleTxt);
                        }
                    }
                        break;
                    case R.id.item_reset_pwd_ll: {
                        if (hasFocus) {
                            buttomItemFocused(view, mResetPwdTitleTxt);
                        } else {
                            buttomItemUnfocused(view, mResetPwdTitleTxt);
                        }
                    }
                        break;
                    case R.id.item_daylight_saving_mode_ll:{
                        if (hasFocus) {
                            enumItemFocused(view, mDaylightSavingModeTitleTxt);
                        } else {
                            enumItemUnfocused(view, mDaylightSavingModeTitleTxt);
                        }
                    }
                        break;
                    case R.id.item_timezone_ll:{
                        if (hasFocus) {
                            buttomItemFocused(view, mTimezoneTitleTxt);
                        } else {
                            buttomItemUnfocused(view, mTimezoneTitleTxt);
                        }
                    }
                        break;
//                    case R.id.item_pvr_file_system_ll:{
//                        if (hasFocus) {
//                            buttomItemFocused(view, mPVRFileSystemTitleTxt);
//                        } else {
//                            buttomItemUnfocused(view, mPVRFileSystemTitleTxt);
//                        }
//                    }
//                        break;
                    case R.id.itemOSDTimeOut:{
                        if (hasFocus) {
                            enumItemFocused(view, mOSDTimeOutTitleTxt);
                        } else {
                            enumItemUnfocused(view, mOSDTimeOutTitleTxt);
                        }
                    }
                        break;
                    case R.id.item_off_time_ll:{
                        if (hasFocus) {
                            enumItemFocused(view, mOffTimeTitleTxt);
                        } else {
                            enumItemUnfocused(view, mOffTimeTitleTxt);
                        }
                    }
                        break;
                    case R.id.item_on_time_ll:{
                        if (hasFocus) {
                            enumItemFocused(view, mOnTimeTitleTxt);
                        } else {
                            enumItemUnfocused(view, mOnTimeTitleTxt);
                        }
                    }
                        break;
                    case R.id.item_parental_guidance_ll:{
                        if (hasFocus) {
                            enumItemFocused(view, mParentalGuidanceTitleTxt);
                        } else {
                            enumItemUnfocused(view, mParentalGuidanceTitleTxt);
                        }
                    }
                        break;

                }
            }
        };

        // set Items OnFocusChangeListener
//        mItemVersionLl.setOnFocusChangeListener(onFocusChangeListener);
        mItemLanguageSelectLl.setOnFocusChangeListener(onFocusChangeListener);
        mItemResetLl.setOnFocusChangeListener(onFocusChangeListener);
        mItemLocalUpdateLl.setOnFocusChangeListener(onFocusChangeListener);
        mItemNetworkUpdateLl.setOnFocusChangeListener(onFocusChangeListener);
        mItemSleepModeLl.setOnFocusChangeListener(onFocusChangeListener);
        mItemSaveModeLl.setOnFocusChangeListener(onFocusChangeListener);
        mItemAutoPowerOnLl.setOnFocusChangeListener(onFocusChangeListener);
        mItemResetPwdLl.setOnFocusChangeListener(onFocusChangeListener);
        mItemDaylightSavingModeLl.setOnFocusChangeListener(onFocusChangeListener);
        mItemTimezoneLl.setOnFocusChangeListener(onFocusChangeListener);
//        mItemPVRFileSystemLl.setOnFocusChangeListener(onFocusChangeListener);
        mOSDTimeOutLl.setOnFocusChangeListener(onFocusChangeListener);
        mItemOffTimeLl.setOnFocusChangeListener(onFocusChangeListener);
        mItemOnTimeLl.setOnFocusChangeListener(onFocusChangeListener);
        mItemParentalGuidanceLl.setOnFocusChangeListener(onFocusChangeListener);
    }

    private void setParentalGuidanc(int ParentalGuidanceVal) {
        mParentalGuidanceContentTxt.setText(mParentalGuidanceStr[ParentalGuidanceVal]);
    }
    
    /**
     * set the time of sleepMOde
     * 
     * @param sleepModeVal
     */
    private void setSleepMode(int sleepModeVal) {
        TvTimerManager.getInstance().setSleepMode(EnumSleepTimeState.values()[sleepModeVal]);
    }

    /**
     * change the UI when buttomItem dosen't has focused
     * 
     * @param view
     * @param titleTxt
     */
    private void buttomItemUnfocused(View view, TextView titleTxt) {
        titleTxt.setTextColor(android.graphics.Color.WHITE);
        view.findViewById(R.id.button_context).setBackgroundResource(R.drawable.bar_bg_btn_grey);
    }

    /**
     * change the UI when buttomItem has focused
     * 
     * @param view
     * @param titleTxt
     */
    private void buttomItemFocused(View view, TextView titleTxt) {
        titleTxt.setTextColor(mR.getColor(R.color.cyan));
        view.findViewById(R.id.button_context).setBackgroundResource(R.drawable.bar_bg_btn_cyan);
    }

    /**
     * change the UI when EnumItem dosen't has focused
     * 
     * @param view
     * @param titleTxt
     */
    private void enumItemUnfocused(View view, TextView titleTxt) {
        titleTxt.setTextColor(android.graphics.Color.WHITE);
        view.findViewById(R.id.enum_context).setBackgroundResource(R.drawable.bar_bg_enum_grey);
    }

    /**
     * change the UI when EnumItem has focused
     * 
     * @param view
     * @param titleTxt
     */
    private void enumItemFocused(View view, TextView titleTxt) {
        titleTxt.setTextColor(mR.getColor(R.color.cyan));
        view.findViewById(R.id.enum_context).setBackgroundResource(R.drawable.bar_bg_enum_cyan);
    }

   /**
    * get current OSD TimeOut Value
    * @return  OSD TimeOut Value
    */
    private int getOSDTimeOutVal() {
    	int OSDTimeOutVal = 0;
        int OSDTime = Settings.System.getInt(mFragment.getActivity().getContentResolver(), "osdtime", 0);
        
        switch(OSDTime){
            case (int) Constants.DEALY_TIME_5S:{
                OSDTimeOutVal = Constants.OSD_TIMEOUT_VAL_5S;
            }
            break;
            case (int) Constants.DEALY_TIME_10S:{
                OSDTimeOutVal = Constants.OSD_TIMEOUT_VAL_10S;
            }
            break;
            case (int) Constants.DEALY_TIME_15S:{
                OSDTimeOutVal = Constants.OSD_TIMEOUT_VAL_15S;
            }
            break;
            case (int) Constants.DEALY_TIME_20S:{
                OSDTimeOutVal = Constants.OSD_TIMEOUT_VAL_20S;
            }
            break;
            case (int) Constants.DEALY_TIME_30S:{
                OSDTimeOutVal = Constants.OSD_TIMEOUT_VAL_30S;
            }
            break;
            case (int) Constants.DEALY_TIME_ALWAYS:{
                OSDTimeOutVal = Constants.OSD_TIMEOUT_VAL_ALWAYS;
            }
            break;
        }
        return OSDTimeOutVal;
    }
    
    /**
     * set OSD Time Out Value
     * @param mOSDTimeOutVal
     */
    private void setOSDTimeOutVal(int OSDTimeOutVal) {
       switch(OSDTimeOutVal){
           case Constants.OSD_TIMEOUT_VAL_5S:{
               Settings.System.putInt(mFragment.getActivity().getContentResolver(), "osdtime", (int) Constants.DEALY_TIME_5S);
               TvMenuActivity.DIMISS_DELAY_TIME = Constants.DEALY_TIME_5S;
           }
           break;
           case Constants.OSD_TIMEOUT_VAL_10S:{
               Settings.System.putInt(mFragment.getActivity().getContentResolver(), "osdtime", (int) Constants.DEALY_TIME_10S);
               TvMenuActivity.DIMISS_DELAY_TIME = Constants.DEALY_TIME_10S;
           }
           break;
           case Constants.OSD_TIMEOUT_VAL_15S:{
               Settings.System.putInt(mFragment.getActivity().getContentResolver(), "osdtime", (int) Constants.DEALY_TIME_15S);
               TvMenuActivity.DIMISS_DELAY_TIME = Constants.DEALY_TIME_15S;
           }
           break;
           case Constants.OSD_TIMEOUT_VAL_20S:{
               Settings.System.putInt(mFragment.getActivity().getContentResolver(), "osdtime", (int) Constants.DEALY_TIME_20S);
               TvMenuActivity.DIMISS_DELAY_TIME = Constants.DEALY_TIME_20S;
           }
           break;
           case Constants.OSD_TIMEOUT_VAL_30S:{
               Settings.System.putInt(mFragment.getActivity().getContentResolver(), "osdtime", (int) Constants.DEALY_TIME_30S);
               TvMenuActivity.DIMISS_DELAY_TIME = Constants.DEALY_TIME_30S;
           }
           break;
           case Constants.OSD_TIMEOUT_VAL_ALWAYS:{
               Settings.System.putInt(mFragment.getActivity().getContentResolver(), "osdtime", (int) Constants.DEALY_TIME_ALWAYS);
               TvMenuActivity.DIMISS_DELAY_TIME = Constants.DEALY_TIME_ALWAYS;
           }
           break;
       }
    }
    
    public static String formatDate(long time, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date date = new Date(time);
        String curTimeStr = formatter.format(date);
        return curTimeStr;
    }
}
