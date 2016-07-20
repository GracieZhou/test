
package com.android.settings.system;

import com.android.settings.R;

import android.app.Activity;
import android.os.Bundle;

public class SystemSettingsActivity extends Activity {

    private SystemSettingsHolder mHolder;

    private SystemSettingsLogic mLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_system_settings);
        mLogic = new SystemSettingsLogic(this);
        mHolder = new SystemSettingsHolder(this);
    }
	
    public SystemSettingsHolder getmHolder() {
        return mHolder;
    }

    @Override
    protected void onRestart() {
        mHolder.refreshUI();
        super.onRestart();
    }

    public SystemSettingsLogic getmLogic() {
        return mLogic;
    }
}
