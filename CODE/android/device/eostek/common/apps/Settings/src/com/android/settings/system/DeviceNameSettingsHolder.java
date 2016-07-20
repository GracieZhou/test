
package com.android.settings.system;

import scifly.device.Device;
import scifly.provider.SciflyStore;
import scifly.provider.SciflyStore.Global;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.settings.R;
import com.android.settings.userbackup.BackUpData;
import com.android.settings.widget.TitleWidget;

public class DeviceNameSettingsHolder {
    public static final String TAG = "DeviceNameSettingsHolder";

    private DeviceNameSettingsActivity mActivity;

    private TitleWidget mTitle;


    private EditText mDeviceName;

    private Button mBtn;

    public DeviceNameSettingsHolder(DeviceNameSettingsActivity activity) {
        this.mActivity = activity;
    }

    public void findViews() {
        mTitle = (TitleWidget) mActivity.findViewById(R.id.activity_system_settings_devicename_title);
        mDeviceName = (EditText) mActivity.findViewById(R.id.device_name_et);
        mBtn = (Button) mActivity.findViewById(R.id.device_btn_ok);
    }

    public void initViews() {
        mTitle.setMainTitleText(mActivity.getString(R.string.action_settings));
        mTitle.setFirstSubTitleText(mActivity.getString(R.string.system_settings), false);
        mTitle.setSecondSubTitleText(mActivity.getString(R.string.device_name));
        mBtn.setText(mActivity.getResources().getString(R.string.button_ok));
        String deviceName = Device.getDeviceName(mActivity);
        mDeviceName.setText(deviceName == null ? "" : deviceName);
    }

    public void registerListener() {
        mBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String newDeviceName = mDeviceName.getText().toString();
                	if (!newDeviceName.equals(mDeviceName.getText())) {
                		SciflyStore.Global.putString(mActivity.getContentResolver(), Global.DEVICE_NAME, newDeviceName);
//                		BackUpData.backupData("device", "device_name", newDeviceName);
                	}
                	mActivity.finish();
                }
        });
        
        mBtn.setOnFocusChangeListener(new android.view.View. OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasfocus) {
				if(hasfocus){
					judgeEditLength();
				}else{
					Log.d(TAG, "<<<<<<<<<<<setClickable(true)<<<<<<<<<<<<<");
					mBtn.setClickable(true);
				}
			}
		});
        mDeviceName.setOnFocusChangeListener(new android.view.View. OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasfocus) {
				if(hasfocus){
					mBtn.setClickable(true);
				}else{
					judgeEditLength();
				}
			}
		});
//        mBtn.setOnHoverListener(new android.view.View.OnHoverListener() {
//			
//			@Override
//			public boolean onHover(View v, MotionEvent event) {
//				 int what = event.getAction();  
//	                switch(what){  
//	                 case MotionEvent.ACTION_HOVER_ENTER:  //鼠标进入view  
////	                	judgeEditLength();
//	                     break;  
//	                 case MotionEvent.ACTION_HOVER_MOVE:  //鼠标在view上  
//	                	 judgeEditLength();
//	                     break;  
//	                 case MotionEvent.ACTION_HOVER_EXIT:  //鼠标离开view  
//	                     mBtn.setClickable(true);
//	                     break;  
//	                }  
//	                return false;  
//			}
//    });
    }
    private void judgeEditLength(){
  		String newDeviceName = mDeviceName.getText().toString();
			 if(TextUtils.isEmpty(newDeviceName)){
              	 Toast.makeText(mActivity, R.string.disable_input_null, Toast.LENGTH_SHORT).show();
              	 Log.d(TAG, "<<<<<<<<<<<setClickable(false)<<<<<<<<<<<<<");
              	 mBtn.setClickable(false);
              }else if(newDeviceName.length()==20){
             	 Toast.makeText(mActivity, R.string.string_outbound, Toast.LENGTH_SHORT).show();
             	 mBtn.setClickable(true);
             	 }else{
             		Log.d(TAG, "<<<<<<<<<<<setClickable(true)<<<<<<<<<<<<<");
             		mBtn.setClickable(true);
             		}
    }
}
