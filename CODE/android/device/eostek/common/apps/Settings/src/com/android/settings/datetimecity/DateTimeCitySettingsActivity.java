package com.android.settings.datetimecity;

import com.android.settings.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class DateTimeCitySettingsActivity extends Activity {

	private static final String TAG = "TIMEZONE";
	public static int NTP_SERVER_ACTIVITY_RESULT = RESULT_FIRST_USER + 99;

	protected DateTimeCitySettingsHolder mHolder;
	protected DateTimeCitySettingsLogic mLogic;

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(DateTimeCitySettingsActivity.this,
						getString(R.string.correct_time_success),
						Toast.LENGTH_LONG).show();
				break;
			case 1:
				Toast.makeText(DateTimeCitySettingsActivity.this,
						getString(R.string.correct_time_fail),
						Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_area_time_main);
		initUI();

	}

	public void initUI() {
		Intent intent = getIntent();
		mLogic = new DateTimeCitySettingsLogic(this);
		mHolder = new DateTimeCitySettingsHolder(this);
		mHolder.findViews();
		mHolder.initData(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		mHolder.initData(this);
		mHolder.registerListener();
		mHolder.refreshTime();
		mHolder.sendTimeChangeBroadcast();
	}

	@Override
	protected void onPause() {
		mHolder.sendTimeChangeBroadcast();
		super.onPause();
	}

	@Override
	protected void onStop() {
		mHolder.sendTimeChangeBroadcast();
		super.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "<<<<<<<resultCode<<<<<<<<<<<<<<" + resultCode);
		if (resultCode == NTP_SERVER_ACTIVITY_RESULT) {
			mHolder.startTimeSync();
		}
	}
}
