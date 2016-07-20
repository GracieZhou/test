
package com.eostek.location;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.os.SystemClock;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.content.Intent;
import android.text.TextUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import scifly.provider.SciflyStore.Global;
import scifly.intent.IntentExtra;

/**
 * LocationUtil provide method to start/stop Baidu Location Service & Google Location Service.
 * 
 * @author frank.zhang
 * @since API 2.0
 */
public class LocationUtil {

    private static final String TAG = LocationService.TAG;

    private boolean D = LocationService.D;

    private LocationClient mLocationClient;

    private LocationMode tempMode = LocationMode.Battery_Saving;

    private String tempcoor = "gcj02";

    private int BD_NETWORK_RESULT = 161;

    private MyLocationListenner myListener = new MyLocationListenner();

    private Context mContext;

    /**
     * @param ctx the context of the application
     */
    public LocationUtil(Context ctx) {
        mContext = ctx;
    }

    /**
     * To start Baidu location service.
     *@since API 2.0
     */
    public void startBaiduLocation() {
        mLocationClient = new LocationClient(mContext);
        mLocationClient.registerLocationListener(myListener);
        InitLocation();
        mLocationClient.start();
    }

    /**
     * To stop Baidu location service.
     *@since API 2.0
     */
    public void stopBaiduLocation() {
        mLocationClient.unRegisterLocationListener(myListener);
        mLocationClient.stop();
        stopLocationService();
    }

    private void stopLocationService() {
        ((LocationService)mContext).stopSelf();
    }

    /**
     * @comment These part are for BD Location
     */
    private void InitLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(tempMode);
        option.setCoorType(tempcoor);
        option.setIsNeedAddress(true);
        // Fix bugs of request failed when wifi not connected to internet
        //option.setScanSpan(5000);
        mLocationClient.setLocOption(option);
    }
    
    private void sendLocationChangedBroadcast(String city) {
        if(!TextUtils.isEmpty(city)) {
            Intent intent = new Intent(IntentExtra.ACTION_LOCATION_CHANGED);
            intent.putExtra("city", city);
            mContext.sendBroadcast(intent);
        }
    }

    /**
     * Implementation of BDLocationListener .
     * @author frank.zhang
     * @since API 2.0
     * @date 2014-11-7
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                 return;
            }
            
            HashMap<String, String> info = new HashMap<String, String>();
            int ret = location.getLocType();

            if (ret == BD_NETWORK_RESULT) {
                Log.d(TAG, "Location ok >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                if (setSystemClock(getTime(location.getTime()))) {
                    Log.i(TAG, "set system clock successful !");
                }
                
                info.put("source", "Baidu");
                info.put("address", location.getAddrStr());
                info.put("latitude", String.valueOf(location.getLatitude()));
                info.put("lontitude", String.valueOf(location.getLongitude()));
                info.put("altitude", String.valueOf(location.getAltitude()));
                info.put("coordType", tempcoor);
                info.put("province", location.getProvince());
                info.put("city", location.getCity());
                info.put("district", location.getDistrict());
                info.put("street", location.getStreet());
                info.put("streetNumber", location.getStreetNumber());
                info.put("time", location.getTime());
                info.put("returnCode", String.valueOf(location.getLocType()));
                info.put("result", "true");

                String loc = getJsonString(info);

                if (!writeLocationToStore(loc)) {
                    Log.e(TAG, "write location to store failed .");
                }
                
                sendLocationChangedBroadcast(location.getCity());

                if (D) {
                    Log.d(TAG, "getLocationFromStore:" + getLocationFromStore());
                }

            } else {
                Log.d(TAG, "Location failed >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                info.put("source", "Baidu");
                info.put("time", location.getTime());
                info.put("returnCode", String.valueOf(location.getLocType()));
                info.put("result", "false");
                
                if (!writeLocationToStore(getJsonString(info))) {
                    Log.e(TAG, "write location to store failed .");
                }
            }
            stopBaiduLocation();
        }
    }

    // End for BD Location

    // Begin SET SYSTEM CLOCK
    private boolean setSystemClock(long timestamp) {

        if (timestamp < 0) {
            return false;
        }

        boolean result = false;

        final long systemCurrentTime = System.currentTimeMillis();
        if (Math.abs(timestamp - systemCurrentTime) > 5 * 1000) {
            // Make sure we don't overflow, since it's going to be converted
            // to an int
            if (timestamp / 1000 < Integer.MAX_VALUE) {
                result = SystemClock.setCurrentTimeMillis(timestamp);
                Log.i(TAG, "SystemClock.setCurrentTimeMillis = " + timestamp + ", result = " + result);
            }
        }
        return result;
    }

    private static long getTime(String timeStr) {
        Log.i(TAG, "getTimeFromBaiDu:" + timeStr);
        long timeStamp = -1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        try {
            Date d = sdf.parse(timeStr);
            timeStamp = d.getTime();
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }

        return timeStamp;
    }

    // End SET SYSTEM CLOCK
    /**
     * SciflyStore put / get lacation data .
     */
    private boolean writeLocationToStore(String location) {
        return Global.putString(mContext.getContentResolver(), Global.LOCATION, location);
    }

    private String getLocationFromStore() {
        String location = Global.getString(mContext.getContentResolver(), Global.LOCATION);
        if (location != null && !location.equals("")) {
            return location;
        } else {
            return new String("unknown");
        }
    }

    private String getJsonString(HashMap<String, String> info) {
        if (info == null) {
            return "unknown";
        }

        JSONObject location = new JSONObject();
        for (Map.Entry<String, String> entry : info.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            try {
                location.put(key, value);
            } catch (JSONException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }

        return location.toString();
    }
}
