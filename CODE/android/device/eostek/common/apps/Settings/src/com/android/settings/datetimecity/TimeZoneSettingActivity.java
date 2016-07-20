package com.android.settings.datetimecity;


import com.android.settings.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;


public class TimeZoneSettingActivity extends Activity {

	private TimeZoneHolder mTimeZoneHolder;
	private TimeZoneLogic mTimeZoneLogic;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time_zone);
		mTimeZoneLogic =new TimeZoneLogic(this);
		mTimeZoneHolder = new TimeZoneHolder(this,mTimeZoneLogic);
		mTimeZoneHolder.findview();
		mTimeZoneHolder.registerAdapter();
	}

}


