package com.android.settings.system;

import android.app.Activity;
import android.os.Bundle;

import com.android.settings.R;

public class LanguageSettingsActivity extends Activity {
private LanguageSettingsHolder mHolder;
private LanguageSettingsLogic mLogic;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_settings_inputmethod);
        mHolder=new LanguageSettingsHolder(this);
        mLogic=new LanguageSettingsLogic(this);
        mHolder.findViews();
        mHolder.initViews();
        mHolder.registerAdapter();
        mHolder.registerListener();
    }
    public LanguageSettingsLogic getmLogic(){
        return mLogic;
    }
    public LanguageSettingsHolder getmHolder(){
        return mHolder;
    }
    

}
