
package com.eostek.location;

import java.util.Locale;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * LocationService application holds a Location Service to detect the location of our brand device .
 * 
 * @author frank.zhang
 * @since API 2.0 (For example 2.0)
 */

public class LocationService extends Service {

    /**
     * declare global TAG for this application debug use.
     */
    public static final String TAG = "LocationService";

    /**
     * switch for debug.
     */
    public static boolean D = true;

    private LocationUtil mLocationUtil = null;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationUtil = new LocationUtil(this);
        Log.d(TAG, String.format("Module : %s, Version : %s, Date : %s, Publisher : %s, Revision : %s",
                "LocationService", "1.0.1", "2015-8-17", "frank.zhang", "39747"));
    }

    @Override
    public void onStart(Intent intent, int startId) {
        if (D) {
            Log.i(TAG, "start id " + startId + ": " + intent);
        }

        String country = Locale.getDefault().getCountry();
        if (D) {
            Log.d(TAG, "country:" + country);
        }
        
        mLocationUtil.startBaiduLocation();
    };

    @Override
    public void onDestroy() {
        if (D) {
            Log.d(TAG, "on destroy");
        }

        super.onDestroy();
    }
}
