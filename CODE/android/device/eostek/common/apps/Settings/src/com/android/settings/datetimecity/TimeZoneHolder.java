package com.android.settings.datetimecity;

import java.util.Calendar;
import java.util.TimeZone;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.widget.TitleWidget;

public class TimeZoneHolder {

	private static final String TAG = "TIMEZONE";
	private Activity mActivity;
	private TimeZoneLogic mTimeZoneLogic;
	private ListView mTimeZoneListView;
	private TitleWidget mTitleWidget;
	private TextView mOriginKeyTV;
	private TextView mOriginValueTV;
	private TextView mKeyTv;
	private TextView mValueTv;
	private ImageView mOriginImage;
	private ImageView mImage;
	private TimeZoneAdapter mSimpleAdapter;
	private int mPosition;

	public TimeZoneHolder(Activity Activity, TimeZoneLogic timeZoneLogic) {
		this.mActivity = Activity;
		this.mTimeZoneLogic = timeZoneLogic;
	}

	public void findview() {
		mTimeZoneListView = (ListView) mActivity
				.findViewById(R.id.time_zone_list);
		mTimeZoneLogic.updateResource();
		mTitleWidget = (TitleWidget) mActivity
				.findViewById(R.id.set_timezone_title);
		mKeyTv = (TextView) mActivity.findViewById(R.id.timezone_key);
		mValueTv = (TextView) mActivity.findViewById(R.id.timezone_value);
		mTitleWidget.setMainTitleText(mActivity.getResources().getString(
				R.string.setting));
		mTitleWidget.setFirstSubTitleText(
				mActivity.getResources().getString(R.string.area_time), false);
		mTitleWidget.setSecondSubTitleText(mActivity.getResources().getString(
				R.string.set_time_zone));
		mPosition = mActivity.getIntent().getIntExtra("mposition", 0);

	}

	public void registerAdapter() {

		mSimpleAdapter = new TimeZoneAdapter(mActivity,
				mTimeZoneLogic.AdapterGetData());
		mTimeZoneListView.setAdapter(mSimpleAdapter);

		mTimeZoneListView
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						mKeyTv = (TextView) view
								.findViewById(R.id.timezone_key);
						mValueTv = (TextView) view
								.findViewById(R.id.timezone_value);
						if (mOriginKeyTV == null) {
							mOriginKeyTV = mKeyTv;
						}
						if (mOriginValueTV == null) {
							mOriginValueTV = mValueTv;
						}
						mOriginKeyTV.setTextColor(mOriginKeyTV.getResources()
								.getColor(R.color.white));
						mKeyTv.setTextColor(view.getResources().getColor(
								R.color.green));
						mOriginKeyTV = mKeyTv;
						mOriginValueTV.setTextColor(mOriginValueTV
								.getResources().getColor(R.color.white));
						mValueTv.setTextColor(view.getResources().getColor(
								R.color.green));
						mOriginValueTV = mValueTv;
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});

		mTimeZoneListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {

				TimeZone tz = Calendar.getInstance().getTimeZone();
				int id = TimeZoneLogic.getCurrentTimeZone(mSimpleAdapter, tz);
				Log.d("TIMEZONE", "<<<<<<< tz<<<<<<id<<<<<<<<<<<" + tz
						+ "<<<<<" + id);
				Log.d("TIMEZONE",
						"<<<<<<<<<mTimeZoneListView.getChildAt(id)<<<<<<<<<"
								+ mTimeZoneListView.getChildAt(id));
				if (mTimeZoneListView.getChildAt(id) != null) {
					mTimeZoneListView.getChildAt(id)
							.findViewById(R.id.timezone_image)
							.setVisibility(View.INVISIBLE);
				}
				mImage = (ImageView) view.findViewById(R.id.timezone_image);
				if (mOriginImage == null) {
					mOriginImage = mImage;
				}
				mOriginImage.setVisibility(View.INVISIBLE);
				mImage.setVisibility(View.VISIBLE);
				mOriginImage = mImage;
				mKeyTv = (TextView) view.findViewById(R.id.timezone_key);
				mValueTv = (TextView) view.findViewById(R.id.timezone_value);
				mTimeZoneLogic
						.setTimeZone(mTimeZoneLogic.timezoneIdStr[position]);
				Log.d("TIMEZONE", "<<<<<<< tz<<<<<<id<<<<<<<<<<<"
						+ Calendar.getInstance().getTimeZone());
				// Intent intent = new Intent(mActivity,
				// DateTimeCitySettingsActivity.class);
				String orignalKey = (String) mKeyTv.getText();
				String orignalValue = (String) mValueTv.getText();
				orignalValue = orignalValue.substring(
						orignalValue.indexOf("T") + 1, orignalValue.length());
				if (orignalKey.contains("(")) {
					orignalKey = orignalKey.substring(0,
							orignalKey.indexOf("("));
				}
				SharedPreferences mySharedPreferences = mActivity
						.getSharedPreferences("test", mActivity.MODE_PRIVATE);
				SharedPreferences.Editor editor = mySharedPreferences.edit();
				editor.putString("timezonekey", orignalKey);
				editor.putString("timezonevalue", mValueTv.getText() + "");
				editor.putString("orignalValue", orignalValue);
				editor.commit();

				// intent.putExtra("timezonekey",orignalKey);
				// intent.putExtra("timezonevalue", mValueTv.getText());
				// intent.putExtra("orignalValue", orignalValue);

				// mActivity.startActivity(intent);
				// mActivity.finish();

			}
		});

	}

}
