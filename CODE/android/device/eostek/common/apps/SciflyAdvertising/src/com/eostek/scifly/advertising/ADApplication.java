
package com.eostek.scifly.advertising;

import android.app.Application;
import android.util.Log;

public class ADApplication extends Application {

    private static final String TAG = "ADApplication";

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        AdvertisingManager adManager = AdvertisingManager.getInstance();
        adManager.mContext = getApplicationContext();
        adManager.initDataCache();
        super.onCreate();
    }
}
