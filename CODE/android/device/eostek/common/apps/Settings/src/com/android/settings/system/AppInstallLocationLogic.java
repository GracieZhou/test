
package com.android.settings.system;

import android.annotation.SuppressLint;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.util.Log;

public class AppInstallLocationLogic {
    public static final String TAG = "InstallLocationLogic";

    private AppInstallLocationActivity mActivity;

    public AppInstallLocationLogic(AppInstallLocationActivity activity) {
        this.mActivity = activity;
    }

    @SuppressLint("NewApi")
    public int getDefaultInstallLocationId() {
        int installLocationId = Settings.Global.getInt(mActivity.getContentResolver(), Global.DEFAULT_INSTALL_LOCATION,
                AppInstallLocationActivity.INSTALL_LOCATION_AUTO);
        return installLocationId;
    }

    @SuppressLint("NewApi")
    public boolean setDefaultInstallLocation(int installLoation) {
        Log.d(TAG, "setDefaultInstallLocation " + installLoation);
        return Settings.Global.putInt(mActivity.getContentResolver(), Global.DEFAULT_INSTALL_LOCATION, installLoation);

    }
}
