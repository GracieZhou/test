
package com.eostek.scifly.statistics;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import scifly.provider.SciflyStatistics;

public class StatisticsService extends IntentService {
    private static final String TAG = "StatisticsService";

    public static final boolean DBG = true;

    public StatisticsService() {
        super(TAG);
    }

    public StatisticsService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Name: StatisticsService, Version:1.0.0 Date:2015-01-25, Publisher:Psso.Song REV:00000");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (DBG) {
            Log.d(TAG, "onHandleIntent: " + action);
        }
        if (SciflyStatistics.ACTION_STATISTICS_TRIGGER_UPLOAD.equals(action)) {
            SciflyStatistics.getInstance(getApplicationContext()).triggerUpload();
        } else if (SciflyStatistics.ACTION_STATISTICS_UPDATE_CONFIGURATION.equals(action)
              || SciflyStatistics.ACTION_BIGDATA_UPDATENOTIFY.equals(action)) {
            SciflyStatistics.getInstance(getApplicationContext()).updateConfiguration();
        } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) {
                return;
            }
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info == null) {
                return;
            }
            if (info.isConnected() && info.isAvailable()) {
                SharedPreferences preference = getSharedPreferences(SciflyStatistics.SHARED_PREFERENCE_NAME,
                        Context.MODE_PRIVATE);
                boolean needTrigger = preference.getBoolean(SciflyStatistics.PREFERENCE_NEED_TRIGGER, false);
                if (needTrigger) {
                    if (DBG) {
                        Log.d(TAG, "Network is OK, we need to trigger another uploading!");
                    }
                    SciflyStatistics.getInstance(getApplicationContext()).triggerUpload();
                    SciflyStatistics.getInstance(getApplicationContext()).enableTriggerLater(false);
                }
            }
        }
    }
}
