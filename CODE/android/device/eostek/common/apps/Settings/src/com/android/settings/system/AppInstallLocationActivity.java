
package com.android.settings.system;

import android.app.Activity;
import android.os.Bundle;

import com.android.settings.R;

public class AppInstallLocationActivity extends Activity {
    public static final int INSTALL_LOCATION_AUTO = 0;

    public static final int INSTALL_LOCATION_INTERNAL = 1;

    public static final int INSTALL_LOCATION_SDCARD = 2;


    private AppInstallLocationHolder mHolder;

    private AppInstallLocationLogic mLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_settings_inputmethod);
        mHolder = new AppInstallLocationHolder(this);
        mLogic = new AppInstallLocationLogic(this);
        mHolder.findViews();
        mHolder.registerAdapter();
        mHolder.registerListener();
    }

    public AppInstallLocationHolder getmHolder() {
        return mHolder;
    }

    public AppInstallLocationLogic getmLogic() {
        return mLogic;
    }

}
