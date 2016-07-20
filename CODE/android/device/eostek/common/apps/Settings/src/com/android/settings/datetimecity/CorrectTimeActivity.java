package com.android.settings.datetimecity;

import com.android.settings.R;
import com.android.settings.widget.TextCheckWidget;
import com.android.settings.widget.TitleWidget;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class CorrectTimeActivity extends Activity {

	private TitleWidget mTitleWidget;
	private TextCheckWidget mNtpChinaWidget;
	private TextCheckWidget mNtpTaiWanWidget;
	private TextCheckWidget mNtpAmericanWidget;
	private static final int NTP_SERVER_CHINA = 1;
	private static final int NTP_SERVER_TAIWAN = 2;
	private static final int NTP_SERVER_AMERICAN = 3;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_correct_time);
		initUI();
		registerListener();

	}

	private void initUI() {
		mTitleWidget = (TitleWidget) this.findViewById(R.id.correct_time_title);
		mNtpChinaWidget = (TextCheckWidget) this
				.findViewById(R.id.ntp_server_china);
		mNtpTaiWanWidget = (TextCheckWidget) this
				.findViewById(R.id.ntp_server_taiwan);
		mNtpAmericanWidget = (TextCheckWidget) this
				.findViewById(R.id.ntp_server_american);

		mTitleWidget.setMainTitleText(getString(R.string.setting));
		mTitleWidget.setFirstSubTitleText(getString(R.string.area_time), false);
		mTitleWidget.setSecondSubTitleText(getString(R.string.server));

		mNtpChinaWidget.setText(getString(R.string.ntpserver_china));
		mNtpTaiWanWidget.setText(getString(R.string.ntpserver_taiwan));
		mNtpAmericanWidget.setText(getString(R.string.ntpserver_american));
		setCheckedItem(getNtpServerItem());

	}

	private void registerListener() {
		mNtpChinaWidget.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setCheckedItem(NTP_SERVER_CHINA);
			}
		});
		mNtpTaiWanWidget.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setCheckedItem(NTP_SERVER_TAIWAN);
			}
		});
		mNtpAmericanWidget.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setCheckedItem(NTP_SERVER_AMERICAN);
			}
		});

	}

	private int getNtpServerItem() {
		return NTP_SERVER_CHINA;
	}

	private void setCheckedItem(int id) {
		switch (id) {
		case NTP_SERVER_CHINA:
			mNtpChinaWidget.setChecked(true);
			mNtpTaiWanWidget.setChecked(false);
			mNtpAmericanWidget.setChecked(false);
			break;
		case NTP_SERVER_TAIWAN:
			mNtpChinaWidget.setChecked(false);
			mNtpTaiWanWidget.setChecked(true);
			mNtpAmericanWidget.setChecked(false);
			break;
		case NTP_SERVER_AMERICAN:
			mNtpChinaWidget.setChecked(false);
			mNtpTaiWanWidget.setChecked(false);
			mNtpAmericanWidget.setChecked(true);
			break;
		default:
			break;
		}
	}

}
