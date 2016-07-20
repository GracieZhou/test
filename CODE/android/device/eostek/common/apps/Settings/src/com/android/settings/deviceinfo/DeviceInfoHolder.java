
package com.android.settings.deviceinfo;

import scifly.device.Device;

import com.android.settings.widget.TextTextWidget;
import com.android.settings.widget.TitleWidget;
import com.android.settings.R;

import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;

public class DeviceInfoHolder {
    public static final String TAG = "DeviceInfoHolder";

    private DeviceInfoActivity mActivity;

    private TitleWidget mTitleWidget;

    private TextTextWidget mDeviceIDWidget;

    private TextTextWidget mDeviceNameWidget;
    
    private TextTextWidget mDeviceModelWidget;

    private TextTextWidget mBuildNumberWidget;

    private TextTextWidget mStorageWidget;

    private TextTextWidget mMoreWidget;

    public DeviceInfoHolder(DeviceInfoActivity activity) {
        this.mActivity = activity;
    }

    public void findViews() {
        mTitleWidget = (TitleWidget) mActivity.findViewById(R.id.activity_device_info_title);
        mDeviceIDWidget = (TextTextWidget) mActivity.findViewById(R.id.widget_device_id);
        mDeviceNameWidget = (TextTextWidget) mActivity.findViewById(R.id.widget_device_name);
        mDeviceModelWidget= (TextTextWidget) mActivity.findViewById(R.id.widget_device_model);
        mBuildNumberWidget = (TextTextWidget) mActivity.findViewById(R.id.widget_build_number);
        mStorageWidget = (TextTextWidget) mActivity.findViewById(R.id.widget_storage);
        mMoreWidget = (TextTextWidget) mActivity.findViewById(R.id.widget_more);
    }

    public void initViews() {
        mTitleWidget.setMainTitleText(getString(R.string.action_settings));
        mTitleWidget.setFirstSubTitleText(getString(R.string.about), true);

        mDeviceIDWidget.setText(getString(R.string.about_more_device_id), Device.getBb());
        mDeviceIDWidget.setLeftTextViewGravity(Gravity.LEFT);

        mDeviceNameWidget.setText(getString(R.string.device_name), mActivity.mLogic.getDeviceName());
        mDeviceNameWidget.setLeftTextViewGravity(Gravity.LEFT);

        if(Build.DEVICE.equalsIgnoreCase("Leader")||Build.DEVICE.equalsIgnoreCase("soniq")){
        	mDeviceModelWidget.setVisibility(View.VISIBLE);
        	mDeviceModelWidget.setText(getString(R.string.device_model), mActivity.mLogic.getProductModel());
        	mDeviceModelWidget.setLeftTextViewGravity(Gravity.LEFT);
        }
        
        mBuildNumberWidget.setText(getString(R.string.about_build_number), mActivity.mLogic.getBuildNumber());
        mBuildNumberWidget.setLeftTextViewGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);

        mStorageWidget.setText(getString(R.string.about_storage_info), " ");
        mStorageWidget.setTextColor(Color.rgb(255, 255, 255), Color.rgb(255, 255, 255));
        mStorageWidget.setFocusTextColor(Color.rgb(255, 255, 255), Color.rgb(255, 255, 255));
        mStorageWidget.setLeftTextViewGravity(Gravity.LEFT);

        mMoreWidget.setText(getString(R.string.about_more_info), " ");
        mMoreWidget.setTextColor(Color.rgb(255, 255, 255), Color.rgb(255, 255, 255));
        mMoreWidget.setFocusTextColor(Color.rgb(255, 255, 255), Color.rgb(255, 255, 255));
        mMoreWidget.setLeftTextViewGravity(Gravity.LEFT);
    }

    public void registerListener() {
        mStorageWidget.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mActivity.startActivity(mActivity.STORAGE_INFO_ACTIVITY);
            }
        });
        mMoreWidget.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mActivity.startActivity(mActivity.MORE_INFO_ACTIVITY);
            }
        });
    }

    private String getString(int resId) {
        return mActivity.getResources().getString(resId);
    }
}
