
package com.android.settings.deviceinfo;

import com.android.settings.deviceinfo.StorageInfoFragment;
import com.android.settings.widget.TitleWidget;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.settings.R;

public class StorageInfoActivity extends Activity {
    private TitleWidget mTitleWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_info);
        initUI();
    }

    private void initUI() {
        mTitleWidget = (TitleWidget) findViewById(R.id.activity_storage_info_title);
        mTitleWidget.setMainTitleText(getString(R.string.action_settings));
        mTitleWidget.setFirstSubTitleText(getString(R.string.about), false);
        mTitleWidget.setSecondSubTitleText(getString(R.string.about_storage_info));
        getFragmentManager().beginTransaction().replace(R.id.container, new StorageInfoFragment()).commit();
    }
}
