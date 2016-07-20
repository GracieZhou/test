package com.android.settings.datetimecity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.android.settings.R;
import com.android.settings.util.Utils;

import scifly.provider.SciflyStore;
import scifly.provider.SciflyStore.Global;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
/**
 * 
 * LocationChangeReceiver
 *
 */
public class LocationChangeReceiver extends BroadcastReceiver{

    private static final String TAG = "LocationChangeReceiver";
    
    private static final String CITY = "city";
    
    private static ArrayList<String> mFullProvinceList;
    
    private static ArrayList<String> mFullCitiesList;
    
    @Override
    public void onReceive(Context context, Intent intent) {
        String value = intent.getStringExtra(CITY);
        Log.d(TAG, "receive value=" + value);
        
        String cityName = SciflyStore.Global.getString(context.getContentResolver(), CITY);
        Log.d(TAG, "city name from 'city_name' value=" + cityName);
        if (!TextUtils.isEmpty(value) && TextUtils.isEmpty(cityName)) {

            String city = getCity(context, value);
            Log.d(TAG, "shirley current city=" + city);
            if (!TextUtils.isEmpty(city)) {
                // save city to DB.
                SciflyStore.Global.putString(context.getContentResolver(), Global.CITY_NAME, city);
            }
        }
    }
    
    private String getCity(Context context, String tempCity) {
        if (TextUtils.isEmpty(tempCity)) {
            return null;
        }
        
        if (Utils.isChineseEncoding(tempCity)) {
            tempCity = tempCity.substring(0, tempCity.indexOf(context.getResources().getString(R.string.city)));
        }
        String locationSpecies = "";
        getFullAllProvinces(context);
        
        for (int i = 0; i < mFullCitiesList.size(); i++) {
            String temp = mFullCitiesList.get(i);
            if (temp.contains(tempCity)) {
                locationSpecies = temp;
                break;
            }
        }
        
        if (!TextUtils.isEmpty(locationSpecies)) {
            Log.i(TAG, "locationSpecies= " + locationSpecies);
            String[] strings = locationSpecies.split("\\(");
            strings[1] = strings[1].replace(")", "");
            strings[2] = strings[2].replace(")", "");
           
            StringBuffer strBuffer = new StringBuffer();
            strBuffer.append(strings[0] + ",");
            strBuffer.append(strings[1] + ",");
            strBuffer.append(strings[2]);
            locationSpecies = strBuffer.toString();
        }
        return locationSpecies;
    }
    
    private void getFullAllProvinces(Context context) {
        if (isEmptyOfList(mFullCitiesList) || isEmptyOfList(mFullProvinceList)) {
            HashMap<String, LinkedHashMap<String, String>> v2 = new HashMap<String, LinkedHashMap<String, String>>();
            InputStream inputStream = null;
            inputStream = context.getResources().openRawResource(
                    R.raw.weather_province_full);
            mFullProvinceList = new ArrayList<String>();
            mFullCitiesList = new ArrayList<String>();
            String str = null;
            String key = null;
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(
                        inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(
                        inputStreamReader);
                while ((str = bufferedReader.readLine()) != null) {
                    str = str.trim();
                    if (str.indexOf("=") == -1
                            && (str.indexOf("[") == -1 && str.indexOf("]") == -1)) {
                        continue;
                    }
                    if (str.indexOf("[") != -1 && str.indexOf("]") != -1) {
                        key = str.replace("[", "").replace("]", "");
                    }
                    String[] strs = null;
                    if (v2.containsKey(key)) {
                        LinkedHashMap<String, String> mp = v2.get(key);
                        strs = str.split("=");
                        if (strs == null || strs.length != 2) {
                            continue;
                        }
                        mp.put(strs[0].trim(), strs[1].trim());
                        if (key.length() == 13) {
                            mFullProvinceList.add(strs[1].trim());
                        } else {
                            mFullCitiesList.add(strs[1].trim());
                        }
                    } else {
                        LinkedHashMap<String, String> mp = new LinkedHashMap<String, String>();
                        v2.put(key, mp);
                        strs = str.split("=");
                        if (strs == null || strs.length != 2) {
                            continue;
                        }
                        mp.put(strs[0].trim(), strs[1].trim());
                    }
                }
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isEmptyOfList(List<String> list) {
        if (list != null && list.size() > 0) {
            return false;
        }
        return true;
    }
}
