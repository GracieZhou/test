
package com.eostek.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import java.util.List;
import android.content.ComponentName;

/**
 * NetworkChangeReceiver receive wifi connected broadcast to start LocationService.
 * @author frank.zhang
 * @since API 2.0
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    private static final String TAG = LocationService.TAG;

    private boolean D = LocationService.D;

    private static final String TARGET_SERVICE_NAME = "com.eostek.location.LocationService";

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        if (D) {
            Log.i(TAG, "received:" + arg1);
        }

        if (isWifiConnected(arg0)) {
            if (!isServiceExisted(arg0, TARGET_SERVICE_NAME)) {
                if (D) {
                    Log.d(TAG, "Service starting :" + TARGET_SERVICE_NAME);
                }

                Intent mIntent = new Intent(arg0, LocationService.class);
                arg0.startService(mIntent);
            } else {
                if (D) {
                    Log.d(TAG, "Service already existed :" + TARGET_SERVICE_NAME);
                }
            }
        }

    }

    private boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if ((mWiFiNetworkInfo != null) && (mWiFiNetworkInfo.getState() == NetworkInfo.State.CONNECTED)) {
                if (D) {
                    Log.d(TAG, "WIFIConnected");
                }
                return true;
            }
        }
        return false;
    }

    private boolean isServiceExisted(Context context, String className) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);

        if (!(serviceList.size() > 0)) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            RunningServiceInfo serviceInfo = serviceList.get(i);
            ComponentName serviceName = serviceInfo.service;

            if (serviceName.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }

}
