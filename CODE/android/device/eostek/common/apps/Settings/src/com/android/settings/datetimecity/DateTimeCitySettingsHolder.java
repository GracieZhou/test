
package com.android.settings.datetimecity;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import scifly.provider.SciflyStore;
import scifly.provider.SciflyStore.Global;
import com.android.settings.R;
import com.android.settings.userbackup.BackUpData;
import com.android.settings.widget.TextSelectorWidget;
import com.android.settings.widget.TextTextWidget;
import com.android.settings.widget.TitleWidget;
import com.android.settings.widget.ValueChangeListener;
import com.android.settings.datetimecity.NtpServerActivity;
import com.android.settings.datetimecity.TimeZoneSettingActivity;
import com.android.settings.datetimecity.CitySettingActivity;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings;
import android.os.UserHandle;

public class DateTimeCitySettingsHolder {

    private DateTimeCitySettingsActivity mActivity;

    private TitleWidget mTitleWidget;

    private TextTextWidget mCorrectTimeTitleWidget;

    private TextSelectorWidget mIs24HourTitleWidget;

    private TextTextWidget mSetTimeZoneTitleWidget;

    private TextTextWidget mSetCityTitleWidget;

    private TextView mTimeTextView;

    public static TextView mCorrectTimeTextView;

    public static TextView mCurrentCityTextView;

    private ListView mdateTimeCityListView;

    private static final int MIN24HOUR = 0;

    private static final int MAX24HOUR = 1;

    private static final int EVENT_UPDATE_STATS = 500;

    private static final int CORRECT_UPDATE_TIME = 60 * 1000;

    private static final String HOURS_12 = "12";

    private static final String HOURS_24 = "24";

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_UPDATE_STATS:
                    refreshTime();
            }
        }
    };

    public DateTimeCitySettingsHolder(DateTimeCitySettingsActivity activity) {
        this.mActivity = activity;
    }

    public void findViews() {
        mTitleWidget = (TitleWidget) mActivity.findViewById(R.id.datetime_city_title);
        mCorrectTimeTitleWidget = (TextTextWidget) mActivity.findViewById(R.id.correct_time);
        mCorrectTimeTextView = (TextView) mCorrectTimeTitleWidget.findViewById(R.id.left_textview);
        mTimeTextView = (TextView) mCorrectTimeTitleWidget.findViewById(R.id.right_textview);
        mIs24HourTitleWidget = (TextSelectorWidget) mActivity.findViewById(R.id.is24_hour);
        mSetTimeZoneTitleWidget = (TextTextWidget) mActivity.findViewById(R.id.set_timezone);
        mSetCityTitleWidget = (TextTextWidget) mActivity.findViewById(R.id.set_city);
        mCurrentCityTextView = (TextView) mSetCityTitleWidget.findViewById(R.id.right_textview);
    }

	public void registerListener() {

		mCorrectTimeTitleWidget.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mActivity, NtpServerActivity.class);
                // the second param must more than 0(here is 100) so that
                // callback function would run
                mActivity.startActivityForResult(intent, 100);
			}
		});

		mIs24HourTitleWidget.setValueChangeListener(new ValueChangeListener() {

			@Override
			public void onValueChanged(int value) {
                sendTimeChangeBroadcast();
                mIs24HourTitleWidget.setSelectorText(is24HourFormat(value));
                if (value == 0) {
                    mActivity.mLogic.setis24Hour(true);

                } else {
                    mActivity.mLogic.setis24Hour(false);

                }
                mCorrectTimeTitleWidget.setText(mActivity.getString(R.string.network_correct_time),
                        mActivity.mLogic.getSystemTime());
            }
        });
        
        mIs24HourTitleWidget.setOnSelectWidgetClickedListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                sendTimeChangeBroadcast();
                mIs24HourTitleWidget.getSelectorWidget().calculateValue(1);
            }
        }, new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                sendTimeChangeBroadcast();
                mIs24HourTitleWidget.getSelectorWidget().calculateValue(0);
            }
        });

		mSetTimeZoneTitleWidget.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mActivity,
						TimeZoneSettingActivity.class);
				intent.putExtra("systemCurrentTime", System.currentTimeMillis());
				mActivity.startActivity(intent);
			}
		});

		mSetCityTitleWidget.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mActivity, CitySettingActivity.class);
				mActivity.startActivity(intent);
			}
		});

	}

    public void initData(Context context) {
        mTitleWidget.setMainTitleText(mActivity.getString(R.string.action_settings));
        mTitleWidget.setFirstSubTitleText(mActivity.getString(R.string.area_time), true);

        mCorrectTimeTitleWidget.setTextColor(Color.rgb(255, 255, 255), Color.rgb(255, 255, 255));
        mCorrectTimeTitleWidget.setFocusTextColor(Color.rgb(255, 255, 255), Color.rgb(0, 255, 0));
        mCorrectTimeTextView.setText(mActivity.getString(R.string.network_correct_time));

        final Calendar now = Calendar.getInstance();
        mSetTimeZoneTitleWidget.setText(context.getString(R.string.set_time_zone), getTimeZoneText(now.getTimeZone()));
        BackUpData.backupData("timezone", "time_zone", TimeZone.getDefault().getID());

        mSetTimeZoneTitleWidget.setTextColor(Color.rgb(255, 255, 255), Color.rgb(255, 255, 255));
        mSetTimeZoneTitleWidget.setFocusTextColor(Color.rgb(255, 255, 255), Color.rgb(0, 255, 0));

        mSetCityTitleWidget.setText(context.getString(R.string.set_city), mActivity.mLogic.getCurrentCity(mActivity));

        mSetCityTitleWidget.setTextColor(Color.rgb(255, 255, 255), Color.rgb(255, 255, 255));
        mSetCityTitleWidget.setFocusTextColor(Color.rgb(255, 255, 255), Color.rgb(0, 255, 0));

        mIs24HourTitleWidget.setText(context.getString(R.string.is24_hour));
        mIs24HourTitleWidget.setValue(1, 0, 1);
        mIs24HourTitleWidget.setSelectorText(is24HourFormat(getIs24HourFormat()));
    }

	private int getIs24HourFormat() {
	    return mActivity.mLogic.getis24Hour()?1:0;
	}

	private String is24HourFormat(int value) {
		String is24HourFormat = null;
		if (value == 1) {
			is24HourFormat = mActivity.getString(R.string.yes);
			Settings.System.putStringForUser(mActivity.getContentResolver(),
					Settings.System.TIME_12_24, HOURS_24,
					UserHandle.USER_CURRENT);
		} else if (value == 0) {
			is24HourFormat = mActivity.getString(R.string.no);
			Settings.System.putStringForUser(mActivity.getContentResolver(),
					Settings.System.TIME_12_24, HOURS_12,
					UserHandle.USER_CURRENT);
		}
		return is24HourFormat;
	}

	private static String getTimeZoneText(TimeZone tz) {
		SimpleDateFormat sdf = new SimpleDateFormat("ZZZZ");
		sdf.setTimeZone(tz);
		return sdf.format(new Date()) + " " + tz.getDisplayName();
	}

	public void refreshTime() {
		mTimeTextView.setText(mActivity.mLogic.getSystemTime());
		mHandler.sendEmptyMessageDelayed(EVENT_UPDATE_STATS,
				CORRECT_UPDATE_TIME);
	}

    public void startTimeSync() {
        mActivity.mLogic.startTimeSync();
    }

    public void sendTimeChangeBroadcast() {
        // Register for time ticks and other reasons for time change
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_TIME_CHANGED);
        mActivity.sendBroadcast(intent);
    }

}
