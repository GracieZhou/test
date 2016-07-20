
package com.android.settings.deviceinfo;

import com.android.settings.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class DeviceInfoActivity extends Activity {
    protected static final int STORAGE_INFO_ACTIVITY = 0;

    protected static final int MORE_INFO_ACTIVITY = 1;

    protected DeviceInfoHolder mHolder;

    protected DeviceInfoLogic mLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        mHolder = new DeviceInfoHolder(this);
        mLogic = new DeviceInfoLogic(this);
        mHolder.findViews();
        mHolder.initViews();
        mHolder.registerListener();
    }

    public void startActivity(int id) {
        if (id == STORAGE_INFO_ACTIVITY) {
            Intent intent = new Intent(this, StorageInfoActivity.class);
            startActivity(intent);
        } else if (id == MORE_INFO_ACTIVITY) {
            Intent intent = new Intent(this, MoreInfoActivity.class);
            startActivity(intent);
        }
    }
}
