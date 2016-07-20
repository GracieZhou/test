
package com.android.settings.system;

import android.app.Activity;
import android.os.Bundle;

import com.android.settings.R;

public class InputMethodSettingsActivity extends Activity {
    private InputMethodSettingsHolder mHolder;

    private InputMethodSettingsLogic mLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_settings_inputmethod);
        mLogic = new InputMethodSettingsLogic(this);
        mHolder = new InputMethodSettingsHolder(this);
        mHolder.findView();
        mHolder.registerAdapter();
        mHolder.registerListener();
    }

    public InputMethodSettingsHolder getmHolder() {
        return mHolder;
    }

    public InputMethodSettingsLogic getmLogic() {
        return mLogic;
    }

}
