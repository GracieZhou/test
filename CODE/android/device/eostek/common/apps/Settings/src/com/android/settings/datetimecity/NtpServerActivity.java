package com.android.settings.datetimecity;

import com.android.settings.R;
import com.android.settings.widget.TextCheckWidget;
import com.android.settings.widget.TitleWidget;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class NtpServerActivity extends Activity{
    public static int NTP_SERVER_ACTIVITY_RESULT = RESULT_FIRST_USER + 99;

    public static final String TAG = "NtpServerActivity";
	private TitleWidget mTitleWidget;
	private TextCheckWidget mNtpChinaWidget;
	private TextCheckWidget mNtpTaiWanWidget;
	private TextCheckWidget mNtpAmericanWidget;
    private static final int NTP_SERVER_CHINA = 0;

    private static final int NTP_SERVER_TAIWAN = 1;

    private static final int NTP_SERVER_AMERICAN = 2;
    private boolean mIsCorrected=false;
	 protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_correct_time);
	        initUI();
	        registerListener();
	        
	    }
	 
	 private void initUI(){
		 mTitleWidget =(TitleWidget)this.findViewById(R.id.correct_time_title);
		 mNtpChinaWidget =(TextCheckWidget)this.findViewById(R.id.ntp_server_china);
		 mNtpTaiWanWidget =(TextCheckWidget)this.findViewById(R.id.ntp_server_taiwan);
		 mNtpAmericanWidget =(TextCheckWidget)this.findViewById(R.id.ntp_server_american);
		 mTitleWidget.setMainTitleText(getString(R.string.setting));
		 mTitleWidget.setFirstSubTitleText(getString(R.string.area_time), false);
		 mTitleWidget.setSecondSubTitleText(getString(R.string.server));
		 mNtpChinaWidget.setText(getString(R.string.ntpserver_china));
		 mNtpTaiWanWidget.setText(getString(R.string.ntpserver_taiwan));
		 mNtpAmericanWidget.setText(getString(R.string.ntpserver_american));
		 setCheckedItem(getNtpServerItem());
		 
	 }
	
	 
	 private void registerListener(){

		 mNtpChinaWidget.setOnClickListener(mOnClickListener);
		 mNtpTaiWanWidget.setOnClickListener(mOnClickListener);
		 mNtpAmericanWidget.setOnClickListener(mOnClickListener);
		}
		
		private int getNtpServerItem(){
        int mNtpServerId = getSharedPreferences("settings", 0).getInt("ntp_server_id", 0);
        return mNtpServerId;
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
		
		private  OnClickListener mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
            Log.d(TAG, arg0.toString() + " is clicked");
            SharedPreferences sp = getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
            int originNtpId=sp.getInt("ntp_server_id", 0);
            Editor spEditor = sp.edit();
				switch (arg0.getId()) {
				 case R.id.ntp_server_china:
					 setCheckedItem(NTP_SERVER_CHINA);
                    spEditor.putInt("ntp_server_id", 0);
				 break;
				 case R.id.ntp_server_taiwan:
					 setCheckedItem(NTP_SERVER_TAIWAN);
                    spEditor.putInt("ntp_server_id", 1);
				 break;
				 case R.id.ntp_server_american:
					 setCheckedItem(NTP_SERVER_AMERICAN);
                    spEditor.putInt("ntp_server_id", 2);

				 break;
				}
            spEditor.commit();
            int newNtpId=sp.getInt("ntp_server_id",0);
            mIsCorrected=!(originNtpId==newNtpId);
            finish();
			}
			
		};
    @Override
    public void finish() {
        Intent intent =new Intent();
        Bundle bundle=new Bundle();
        bundle.putBoolean("isNtpCorrected", mIsCorrected);
        intent.putExtras(bundle);
        this.setResult(NTP_SERVER_ACTIVITY_RESULT,intent);
        super.finish();
    }
}
