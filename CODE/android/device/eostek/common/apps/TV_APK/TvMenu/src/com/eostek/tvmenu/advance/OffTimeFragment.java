
package com.eostek.tvmenu.advance;

import java.util.Timer;
import java.util.TimerTask;

import com.eostek.tvmenu.R;
import com.mstar.android.tv.TvTimerManager;
import com.mstar.android.tvapi.common.vo.StandardTime;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OffTimeFragment extends Fragment {
    
    private TvTimerManager tvTimerManager = null;

    private LinearLayout mItemAdjustOffTimeLl;

    private LinearLayout mItemHourLl;

    private LinearLayout mItemMinutesLl;
    
    private LinearLayout mItemDateLl;

    private TextView mAdjustOffTimeTitleTxt;

    private TextView mAdjustOffTimeContentTxt;

    private TextView mHourTitleTxt;

    private TextView mHourContentTxt;

    private TextView mMinutesTitleTxt;

    private TextView mMinutesContentTxt;
    
    private TextView mDateTitleTxt;
    
    private TextView mDateContentTxt;

    private int mAdjustOffTimeVal;

    private int mHourVal;

    private int mMinutesVal;

    private String mHourText;

    private String mMinutesText;

    private String[] mTitleOffTime;

    private String[] mTurnOn_OffStr;

    private static final int TITLE_SET_OFF_TIME = 0;

    private static final int TITLE_HOUR = 1;

    private static final int TITLE_MINUTES = 2;
    
    private static final int TITLE_CURRENT_TIME = 3;

    private static final int OFF_TIME_IS_DISABLE = 0;

    private static final int OFF_TIME_IS_ENABLE = 1;

    private Resources mR;
    
    private boolean enableOffTimer = false;
    
    private final int SET_OFF_TIME = 0;
    
    private final int UPDATE_CURRENT_TIME = 1;
    
    private Timer timer = new Timer();
    
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == SET_OFF_TIME) {
                tvTimerManager.setOffTimerEnable(true);
            } else if (msg.what == UPDATE_CURRENT_TIME) {
                StandardTime curTime = tvTimerManager.getCurTimer();
                mDateContentTxt.setText(String.format("%4d/%02d/%02d", curTime.year, curTime.month, curTime.monthDay) + " " + String.format("%2d:%02d:%02d", curTime.hour, curTime.minute, curTime.second));
            }
        }
        
    };

    public OffTimeFragment(AdvanceSettingFragment fragment) {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.off_time_fragment, null);

        mR = getActivity().getResources();
        tvTimerManager = TvTimerManager.getInstance();
        
        //get right time to set off time
//        tvTimerManager.updateTimeZone();


        mTitleOffTime = getActivity().getResources().getStringArray(R.array.on_time_val);
        mTurnOn_OffStr = getActivity().getResources().getStringArray(R.array.turnon_off);

        mItemAdjustOffTimeLl = (LinearLayout) view.findViewById(R.id.item_adjust_off_time_ll);
        mAdjustOffTimeTitleTxt = (TextView) mItemAdjustOffTimeLl.findViewById(R.id.title_txt);
        mAdjustOffTimeContentTxt = (TextView) mItemAdjustOffTimeLl.findViewById(R.id.value);

        mItemHourLl = (LinearLayout) view.findViewById(R.id.item_hour_ll);
        mHourTitleTxt = (TextView) mItemHourLl.findViewById(R.id.title_txt);
        mHourContentTxt = (TextView) mItemHourLl.findViewById(R.id.value);

        mItemMinutesLl = (LinearLayout) view.findViewById(R.id.item_minutes_ll);
        mMinutesTitleTxt = (TextView) mItemMinutesLl.findViewById(R.id.title_txt);
        mMinutesContentTxt = (TextView) mItemMinutesLl.findViewById(R.id.value);

        mItemDateLl = (LinearLayout) view.findViewById(R.id.item_current_date_ll);
        mDateTitleTxt = (TextView) mItemDateLl.findViewById(R.id.title_txt);
        mDateContentTxt = (TextView) mItemDateLl.findViewById(R.id.value);
        
        mAdjustOffTimeTitleTxt.setText(mTitleOffTime[TITLE_SET_OFF_TIME]);
        mHourTitleTxt.setText(mTitleOffTime[TITLE_HOUR]);
        mMinutesTitleTxt.setText(mTitleOffTime[TITLE_MINUTES]);
        mDateTitleTxt.setText(mTitleOffTime[TITLE_CURRENT_TIME]);
        

        initData();
        setListener();

        return view;
    }

    private void initData() {
        if (tvTimerManager.isOffTimerEnable() == false) {
            mAdjustOffTimeVal = OFF_TIME_IS_DISABLE;
        }else{
            mAdjustOffTimeVal = OFF_TIME_IS_ENABLE;
        }
        mAdjustOffTimeContentTxt.setText(mTurnOn_OffStr[mAdjustOffTimeVal]);
        
        mHourVal = tvTimerManager.getOffTimer().hour;
        mHourText = Integer.toString(mHourVal); 
        mHourContentTxt.setText(mHourText);
        
        mMinutesVal = tvTimerManager.getOffTimer().minute;
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

    private void setListener() {
        OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                switch (view.getId()) {
                    case R.id.item_adjust_off_time_ll: {
                        if (hasFocus) {
                            enumItemFocused(view, mAdjustOffTimeTitleTxt);
                        } else {
                            enumItemUnfocused(view, mAdjustOffTimeTitleTxt);
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
                    case R.id.item_minutes_ll: {
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
        mItemAdjustOffTimeLl.setOnFocusChangeListener(onFocusChangeListener);
        mItemHourLl.setOnFocusChangeListener(onFocusChangeListener);
        mItemMinutesLl.setOnFocusChangeListener(onFocusChangeListener);
        mItemDateLl.setOnFocusChangeListener(onFocusChangeListener);

        OnKeyListener OnKeyListener = new OnKeyListener() {

            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                switch (view.getId()) {
                    case R.id.item_adjust_off_time_ll: {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_LEFT:
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                    if (mAdjustOffTimeVal == OFF_TIME_IS_DISABLE) {
                                        mAdjustOffTimeVal = OFF_TIME_IS_ENABLE;
                                        tvTimerManager.setOffTimerEnable(true);
                                        enableOffTimer = true;
                                    } else if (mAdjustOffTimeVal == OFF_TIME_IS_ENABLE) {
                                        mAdjustOffTimeVal = OFF_TIME_IS_DISABLE;
                                        tvTimerManager.setOffTimerEnable(false);
                                        enableOffTimer = false;
                                    }
                                    mAdjustOffTimeContentTxt.setText(mTurnOn_OffStr[mAdjustOffTimeVal]);
                                }
                                    return true;

                            }
                        }
                    }
                        break;

                    case R.id.item_hour_ll: {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                    setOffTimerToEnable();

                                    if (mHourVal < 23) {
                                        ++mHourVal;
                                        mHourText = Integer.toString(mHourVal);
                                        mHourContentTxt.setText(mHourText);
                                    } else {
                                        return true;
                                    }

                                    StandardTime dateTime = tvTimerManager.getCurTimer();
                                    dateTime.hour = (byte) (mHourVal);
                                    tvTimerManager.setOffTimer(dateTime);
                                    enableOffTimer = true;
                                    mHandler.sendEmptyMessageDelayed(SET_OFF_TIME, 500);
                                }
                                    return true;

                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                    setOffTimerToEnable();
                                    
                                    if (mHourVal > 0) {
                                        --mHourVal;
                                        mHourText = Integer.toString(mHourVal);
                                        mHourContentTxt.setText(mHourText);
                                    } else {
                                        return true;
                                    }
                                    StandardTime dateTime = tvTimerManager.getCurTimer();
                                    dateTime.hour = (byte) (mHourVal);
                                    tvTimerManager.setOffTimer(dateTime);
                                    enableOffTimer = true;
                                    mHandler.sendEmptyMessageDelayed(SET_OFF_TIME, 500);
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
                                    setOffTimerToEnable();
                                    
                                    if (mMinutesVal < 60) {
                                        ++mMinutesVal;
                                        mMinutesText = Integer.toString(mMinutesVal);
                                        mMinutesContentTxt.setText(mMinutesText);
                                    } else {
                                        return true;
                                    }
                                    StandardTime dateTime;
                                    StandardTime curDateTime;

                                    curDateTime = tvTimerManager.getCurTimer();
                                    dateTime = tvTimerManager.getOffTimer();
                                    dateTime.minute = (byte) (mMinutesVal);
                                    tvTimerManager.setOffTimer(dateTime);
                                    if ((dateTime.toMillis(true) - curDateTime.toMillis(true)) > 1000 * 60) {
                                        mHandler.sendEmptyMessageDelayed(SET_OFF_TIME, 500);
                                    }
                                    enableOffTimer = true;

                                }
                                    return true;

                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                    setOffTimerToEnable();
                                    
                                    if (mMinutesVal > 0) {
                                        --mMinutesVal;
                                        mMinutesText = Integer.toString(mMinutesVal);
                                        mMinutesContentTxt.setText(mMinutesText);
                                    } else {
                                        return true;
                                    }

                                    StandardTime dateTime;
                                    StandardTime curDateTime;

                                    curDateTime = tvTimerManager.getCurTimer();
                                    dateTime = tvTimerManager.getOffTimer();
                                    dateTime.minute = (byte) (mMinutesVal);
                                    tvTimerManager.setOffTimer(dateTime);
                                    if ((dateTime.toMillis(true) - curDateTime.toMillis(true)) > 1000 * 60) {
                                        mHandler.sendEmptyMessageDelayed(SET_OFF_TIME, 500);
                                    }
                                    enableOffTimer = true;
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
        mItemAdjustOffTimeLl.setOnKeyListener(OnKeyListener);
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

    private void setOffTimerToEnable() {
        mAdjustOffTimeVal = OFF_TIME_IS_ENABLE;
//        tvTimerManager.setOffTimerEnable(true);
        mAdjustOffTimeContentTxt.setText(mTurnOn_OffStr[mAdjustOffTimeVal]);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        tvTimerManager.setOffTimerEnable(enableOffTimer);
    }

}
