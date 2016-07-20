package com.android.settings.datetimecity;

import com.android.settings.R;

import android.app.Activity;
import android.os.Bundle;
public class CitySettingActivity extends Activity {

	private CitySettingHolder mHolder;
	public CitySettingLogic mLogic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city_setting);
		mLogic = new CitySettingLogic(this);
		mHolder = new CitySettingHolder(this);
		mHolder.findview();
		mHolder.initData();
		mHolder.registerListener();

    }
    
}
