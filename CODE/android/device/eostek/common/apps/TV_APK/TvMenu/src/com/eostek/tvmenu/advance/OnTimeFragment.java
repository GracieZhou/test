package com.eostek.tvmenu.advance;

import java.util.Timer;
import java.util.TimerTask;

import com.eostek.tvmenu.R;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvTimerManager;
import com.mstar.android.tvapi.common.vo.OnTimeTvDescriptor;
import com.mstar.android.tvapi.common.vo.StandardTime;
import com.mstar.android.tvapi.common.vo.EnumTimeOnTimerSource;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OnTimeFragment extends Fragment {
    
    private AdvanceSettingFragment mFragment;

    private TvTimerManager tvTimerManager = null;
    
    private LinearLayout mItemAdjustOnTimeLl;

    private LinearLayout mItemHourLl;

    private LinearLayout mItemMinutesLl;
    
    private LinearLayout mItemDateLl;
    
    private TextView mAdjustOnTimeTitleTxt;
    private TextView mAdjustOnTimeContentTxt;
    
    private TextView mHourTitleTxt;
    private TextView mHourContentTxt;
    
    private TextView mMinutesTitleTxt;
    private TextView mMinutesContentTxt;
    
    private TextView mDateTitleTxt;
    private TextView mDateContentTxt;
    
    private int mAdjustOnTimeVal;
    private int mHourVal;
    private int mMinutesVal;
    
    private static final int TITLE_SET_ON_TIME = 0;
    private static final int TITLE_HOUR = 1;
    private static final int TITLE_MINUTES = 2;
    private static final int TITLE_CURRENT_TIME = 3;
    
    private static final int ON_TIME_IS_DISABLE = 0;
    private static final int ON_TIME_IS_ENABLE = 1;
    
    private String mHourText;
    private String mMinutesText;
    
    private String[] mTitleOnTime;
    private String[] mTurnOn_OffStr;
    
    StandardTime mDateTime;
    StandardTime mCurTime;
    
    Resources mR;
    
    private static final int SET_ON_TIME = 0;
    
    private static final int UPDATE_CURRENT_TIME = 1;
    
    private static final int DELAY_TIME = 500;
    
    private boolean enableOnTimer = false;
    
    private Timer timer = new Timer();
    
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == SET_ON_TIME) {
                tvTimerManager.setOnTimerEnable(true);
            } else if (msg.what == UPDATE_CURRENT_TIME) {
                StandardTime curTime = tvTimerManager.getCurTimer();
                mDateContentTxt.setText(String.format("%4d/%02d/%02d", curTime.year, curTime.month, curTime.monthDay) + " " + String.format("%2d:%02d:%02d", curTime.hour, curTime.minute, curTime.second));
            }
        }
        
    };
    
    public OnTimeFragment(AdvanceSettingFragment fragment) {
        this.mFragment = fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.on_time_fragment, null);
        
        mR = getActivity().getResources();
        tvTimerManager = TvTimerManager.getInstance();
        
      //get right time to set off time
//        tvTimerManager.updateTimeZone();
        
        mDateTime = tvTimerManager.getOffTimer();
        mCurTime = tvTimerManager.getCurTimer();
        
        mTitleOnTime = getActivity().getResources().getStringArray(R.array.on_time_val);
        mTurnOn_OffStr = getActivity().getResources().getStringArray(R.array.turnon_off);
        
        mItemAdjustOnTimeLl = (LinearLayout) view.findViewById(R.id.item_adjust_on_time_ll);
        mAdjustOnTimeTitleTxt = (TextView) mItemAdjustOnTimeLl.findViewById(R.id.title_txt);
        mAdjustOnTimeContentTxt = (TextView) mItemAdjustOnTimeLl.findViewById(R.id.value);
        
        mItemHourLl = (LinearLayout) view.findViewById(R.id.item_hour_ll);
        mHourTitleTxt = (TextView) mItemHourLl.findViewById(R.id.title_txt);
        mHourContentTxt = (TextView) mItemHourLl.findViewById(R.id.value);
        
        mItemMinutesLl = (LinearLayout) view.findViewById(R.id.item_minutes_ll);
        mMinutesTitleTxt = (TextView) mItemMinutesLl.findViewById(R.id.title_txt);
        mMinutesContentTxt = (TextView) mItemMinutesLl.findViewById(R.id.value);
        
        mItemDateLl = (LinearLayout) view.findViewById(R.id.item_current_date_ll);
        mDateTitleTxt = (TextView) mItemDateLl.findViewById(R.id.title_txt);
        mDateContentTxt = (TextView) mItemDateLl.findViewById(R.id.value);
        
        mAdjustOnTimeTitleTxt.setText(mTitleOnTime[TITLE_SET_ON_TIME]);
        mHourTitleTxt.setText(mTitleOnTime[TITLE_HOUR]);
        mMinutesTitleTxt.setText(mTitleOnTime[TITLE_MINUTES]);
        mDateTitleTxt.setText(mTitleOnTime[TITLE_CURRENT_TIME]);
        
        initData();
        setListener();
        
        return view;
    }

    private void initData() {
        enableOnTimer = tvTimerManager.isOnTimerEnable();
        if (enableOnTimer) {
            mAdjustOnTimeVal = ON_TIME_IS_ENABLE;
        } else {
            Time curTime = tvTimerManager.getCurrentTvTime();
            StandardTime dateTime = null;
            dateTime = tvTimerManager.getOnTimer();
            dateTime.year = curTime.year;
            dateTime.month = curTime.month + 1;
            dateTime.monthDay = curTime.monthDay;
            dateTime.hour = curTime.hour;
            dateTime.minute = curTime.minute;
            tvTimerManager.setOnTimer(dateTime);
            
            mAdjustOnTimeVal = ON_TIME_IS_DISABLE;
        }
        mAdjustOnTimeContentTxt.setText(mTurnOn_OffStr[mAdjustOnTimeVal]);

        StandardTime dateTime = tvTimerManager.getCurTimer();

        mHourVal = tvTimerManager.getOnTimer().hour;
        mHourText = Integer.toString(mHourVal);
        mHourContentTxt.setText(mHourText);

        mMinutesVal = tvTimerManager.getOnTimer().minute;
        mMinutesText = Integer.toString(mMinutesVal);
        mMinutesContentTxt.setText(mMinutesText);
        
        mHandler.sendEmptyMessage(UPDATE_CURRENT_TIME);
        
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(UPDATE_CURRENT_TIME);
            }
        }, 1000, 1000);

    }
    
    private void updateSetTimeOnChannelInfo() {
        int curSource = TvCommonManager.getInstance().getCurrentTvInputSource();
        OnTimeTvDescriptor stEvent;
        stEvent = tvTimerManager.getOnTimeEvent();
        switch (curSource) {
            case TvCommonManager.INPUT_SOURCE_ATV:
                stEvent.enTVSrc = EnumTimeOnTimerSource.EN_Time_OnTimer_Source_ATV;
                stEvent.mChNo = 0xFFFF;
                break;
            case TvCommonManager.INPUT_SOURCE_DTV:
                stEvent.enTVSrc = EnumTimeOnTimerSource.EN_Time_OnTimer_Source_DTV;
                stEvent.mChNo = 0xFFFF;
                break;
            case TvCommonManager.INPUT_SOURCE_CVBS:
                stEvent.enTVSrc = EnumTimeOnTimerSource.EN_Time_OnTimer_Source_AV;
                break;
            case TvCommonManager.INPUT_SOURCE_CVBS2:
                stEvent.enTVSrc = EnumTimeOnTimerSource.EN_Time_OnTimer_Source_AV2;
                break;
            case TvCommonManager.INPUT_SOURCE_CVBS3:
                stEvent.enTVSrc = EnumTimeOnTimerSource.EN_Time_OnTimer_Source_AV3;
                break;
            case TvCommonManager.INPUT_SOURCE_HDMI:
                stEvent.enTVSrc = EnumTimeOnTimerSource.EN_Time_OnTimer_Source_HDMI;
                break;
            case TvCommonManager.INPUT_SOURCE_HDMI2:
                stEvent.enTVSrc = EnumTimeOnTimerSource.EN_Time_OnTimer_Source_HDMI2;
                break;
            case TvCommonManager.INPUT_SOURCE_HDMI3:
                stEvent.enTVSrc = EnumTimeOnTimerSource.EN_Time_OnTimer_Source_HDMI3;
                break;
            case TvCommonManager.INPUT_SOURCE_HDMI4:
                stEvent.enTVSrc = EnumTimeOnTimerSource.EN_Time_OnTimer_Source_HDMI4;
                break;
            case TvCommonManager.INPUT_SOURCE_VGA:
                stEvent.enTVSrc = EnumTimeOnTimerSource.EN_Time_OnTimer_Source_RGB;
                break;
            case TvCommonManager.INPUT_SOURCE_YPBPR:
                stEvent.enTVSrc = EnumTimeOnTimerSource.EN_Time_OnTimer_Source_COMPONENT;
                break;
            default:
                break;
        }
        tvTimerManager.setOnTimeEvent(stEvent);
        enableOnTimer = true;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        tvTimerManager.setOnTimerEnable(enableOnTimer);
    }
    
    private void setListener() {
        OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                switch (view.getId()) {
                    case R.id.item_adjust_on_time_ll: {
                        if (hasFocus) {
                            enumItemFocused(view, mAdjustOnTimeTitleTxt);
                        } else {
                            enumItemUnfocused(view, mAdjustOnTimeTitleTxt);
                        }
                    }
                        break;
                    case R.id.item_hour_ll: {
                        if (hasFocus) {
                            enumItemFocused(view, mHourTitleTxt);
                        } else {
                            enumItemUnfocused(view, mHourTitleTxt);
                        }
                    }
                        break;
                    case R.id.item_minutes_ll:{
                        if (hasFocus) {
                            enumItemFocused(view, mMinutesTitleTxt);
                        } else {
                            enumItemUnfocused(view, mMinutesTitleTxt);
                        }
                    }
                        break;
                    case R.id.item_current_date_ll:{
                        if (hasFocus) {
                            enumItemFocused(view, mDateTitleTxt);
                        } else {
                            enumItemUnfocused(view, mDateTitleTxt);
                        }
                    }
                    break;
                }
            }
        };

        // set Items OnFocusChangeListener
        mItemAdjustOnTimeLl.setOnFocusChangeListener(onFocusChangeListener);
        mItemHourLl.setOnFocusChangeListener(onFocusChangeListener);
        mItemMinutesLl.setOnFocusChangeListener(onFocusChangeListener);
        mItemDateLl.setOnFocusChangeListener(onFocusChangeListener);
        

        OnKeyListener OnKeyListener = new OnKeyListener() {

            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                switch (view.getId()) {
                    case R.id.item_adjust_on_time_ll:{
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_LEFT:
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                    if (mAdjustOnTimeVal == ON_TIME_IS_DISABLE) {
                                        mAdjustOnTimeVal = ON_TIME_IS_ENABLE;
                                        tvTimerManager.setOnTimerEnable(true);
                                        enableOnTimer = true;
                                        updateSetTimeOnChannelInfo();
                                    } else if (mAdjustOnTimeVal == ON_TIME_IS_ENABLE) {
                                        mAdjustOnTimeVal = ON_TIME_IS_DISABLE;
                                        tvTimerManager.setOnTimerEnable(false);
                                        enableOnTimer = false;
                                    }
                                    Log.v("tag", "on time");
                                    mAdjustOnTimeContentTxt.setText(mTurnOn_OffStr[mAdjustOnTimeVal]);
                                }
                                    return true;

                            }
                        }
                    }
                    break;
                    
                    case R.id.item_hour_ll:{
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                    setOnTimerToEnable();

                                    if (mHourVal < 23) {
                                        ++mHourVal;
                                        mHourText = Integer.toString(mHourVal);
                                        mHourContentTxt.setText(mHourText);
                                    } else {
                                        return true;
                                    }
                                    StandardTime dateTime;
                                    dateTime = tvTimerManager.getOnTimer();
                                    dateTime.hour = (byte) (mHourVal);
                                    tvTimerManager.setOnTimer(dateTime);
                                    enableOnTimer = true;
                                    mHandler.sendEmptyMessageDelayed(SET_ON_TIME, 500);
                                    updateSetTimeOnChannelInfo();
                                }
                                    return true;

                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                    setOnTimerToEnable();

                                    if (mHourVal > 0) {
                                        --mHourVal;
                                        mHourText = Integer.toString(mHourVal);
                                        mHourContentTxt.setText(mHourText);
                                    } else {
                                        return true;
                                    }
                                    StandardTime dateTime;
                                    dateTime = tvTimerManager.getOnTimer();
                                    dateTime.hour = (byte) (mHourVal);
                                    tvTimerManager.setOnTimer(dateTime);
                                    enableOnTimer = true;
                                    mHandler.sendEmptyMessageDelayed(SET_ON_TIME, 500);
                                    updateSetTimeOnChannelInfo();
                                }
                                    return true;

                            }
                        }
                    }
                    break;
                    
                    case R.id.item_minutes_ll: {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                    setOnTimerToEnable();
                                    if (mMinutesVal < 60) {
                                        ++mMinutesVal;
                                        mMinutesText = Integer.toString(mMinutesVal);
                                        mMinutesContentTxt.setText(mMinutesText);
                                    } else {
                                        return true;
                                    }
                                    StandardTime dateTime;
                                    Time curDateTime;

                                    curDateTime = tvTimerManager.getCurrentTvTime();
                                    dateTime = tvTimerManager.getOnTimer();
                                    dateTime.minute = (byte) (mMinutesVal);
                                    tvTimerManager.setOnTimer(dateTime);
                                    enableOnTimer = true;
                                    updateSetTimeOnChannelInfo();
                                    if ((dateTime.toMillis(true) - curDateTime.toMillis(true)) > 1000 * 60) {
                                        mHandler.sendEmptyMessageDelayed(SET_ON_TIME, 500);

                                    }
                                }
                                    return true;

                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                    setOnTimerToEnable();

                                    if (mMinutesVal > 0) {
                                        --mMinutesVal;
                                        mMinutesText = Integer.toString(mMinutesVal);
                                        mMinutesContentTxt.setText(mMinutesText);
                                    } else {
                                        return true;
                                    }
                                    StandardTime dateTime;
                                    Time curDateTime;

                                    curDateTime = tvTimerManager.getCurrentTvTime();
                                    dateTime = tvTimerManager.getOnTimer();
                                    dateTime.minute = (byte) (mMinutesVal);
                                    tvTimerManager.setOnTimer(dateTime);
                                    enableOnTimer = true;
                                    updateSetTimeOnChannelInfo();
                                    if ((dateTime.toMillis(true) - curDateTime.toMillis(true)) > 1000 * 60) {
                                        mHandler.sendEmptyMessageDelayed(SET_ON_TIME, 500);

                                    }
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
        mItemAdjustOnTimeLl.setOnKeyListener(OnKeyListener);
        mItemHourLl.setOnKeyListener(OnKeyListener);
        mItemMinutesLl.setOnKeyListener(OnKeyListener);
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
    
    private void setOnTimerToEnable() {
        mAdjustOnTimeVal = 1;
        mAdjustOnTimeContentTxt.setText(mTurnOn_OffStr[mAdjustOnTimeVal]);
    }
}
