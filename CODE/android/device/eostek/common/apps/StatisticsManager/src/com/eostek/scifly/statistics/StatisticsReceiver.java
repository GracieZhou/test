
package com.eostek.scifly.statistics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import scifly.provider.SciflyStatistics;

public class StatisticsReceiver extends BroadcastReceiver {
    private static final String TAG = "StatisticsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (StatisticsService.DBG) {
            Log.d(TAG, "onReceive: " + action);
        }
        if (SciflyStatistics.ACTION_STATISTICS_TRIGGER_UPLOAD.equals(action)
                || SciflyStatistics.ACTION_STATISTICS_UPDATE_CONFIGURATION.equals(action)
                || SciflyStatistics.ACTION_BIGDATA_UPDATENOTIFY.equals(action)
                || ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            intent.setClass(context, StatisticsService.class);
            context.startService(intent);
        }
    }

}
