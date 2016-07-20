
package com.android.settings.datetimecity;

import com.android.settings.R;
import android.app.Activity;
import android.os.Bundle;

/**
 * CitySettingActivity
 */
public class CitySettingActivity extends Activity {

    private CitySettingHolder mHolder;

    public CitySettingLogic mLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_setting);
        mLogic = new CitySettingLogic(this);
        mHolder = new CitySettingHolder(this);
        mHolder.findViews();
        mHolder.registerListener();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_out_left, R.anim.fade_out);
    }
}
