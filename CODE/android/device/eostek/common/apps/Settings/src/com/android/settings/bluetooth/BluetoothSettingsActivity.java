
package com.android.settings.bluetooth;

import com.android.settings.R;
import com.android.settings.widget.TitleWidget;

import android.app.Activity;
import android.os.Bundle;

public class BluetoothSettingsActivity extends Activity {
    private TitleWidget mTitleWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_settings);
        initUI();
    }

    private void initUI() {
        mTitleWidget = (TitleWidget) findViewById(R.id.activity_bluetooth_settings_title);
        mTitleWidget.setMainTitleText(getString(R.string.action_settings));
        mTitleWidget.setFirstSubTitleText(getString(R.string.network_settings), false);
        mTitleWidget.setSecondSubTitleText(getString(R.string.bluetooth_settings));
        getFragmentManager().beginTransaction().replace(R.id.bluetooth_container, new BluetoothSettings()).commit();
    }
}
