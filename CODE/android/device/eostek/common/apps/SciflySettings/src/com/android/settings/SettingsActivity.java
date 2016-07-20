
package com.android.settings;

import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity {

    private SettingsHolder mSettingsHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSettingsHolder = new SettingsHolder(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSettingsHolder.findViews();
        mSettingsHolder.registerListener();
        mSettingsHolder.mGridView.requestFocus();

        mSettingsHolder.mGridView.setSelection(mSettingsHolder.mPosition);
    }
}
