package com.android.settings.system;

import android.app.Activity;
import android.os.Bundle;

import com.android.settings.R;

public class DeviceNameSettingsActivity extends Activity {
    private DeviceNameSettingsHolder mHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_settings_devicename);
        mHolder=new DeviceNameSettingsHolder(this);
        mHolder.findViews();
        mHolder.initViews();
        mHolder.registerListener();
    }
    
}
