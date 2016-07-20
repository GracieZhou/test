
package com.android.settings.datetimecity;

import com.android.settings.R;
import android.app.Activity;
import android.os.Bundle;

/**
 * TimeZoneSettingActivity
 */
public class TimeZoneSettingActivity extends Activity {

    private TimeZoneHolder mHolder;

    public TimeZoneLogic mLogic;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_zone);
        mLogic = new TimeZoneLogic(this);
        mHolder = new TimeZoneHolder(this);
        mHolder.findViews();
        mHolder.registerAdapter();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_out_left, R.anim.fade_out);
    }

}
