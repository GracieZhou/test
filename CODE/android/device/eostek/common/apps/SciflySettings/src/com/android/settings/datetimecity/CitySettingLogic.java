
package com.android.settings.datetimecity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import scifly.device.Device;
import scifly.provider.SciflyStore;
import scifly.provider.SciflyStore.Global;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.util.Utils;

/**
 * main function: search city list,auto get city.
 */
public class CitySettingLogic {

	private static final String PLATFORM = SystemProperties.get("ro.scifly.platform", "");
    private static String TAG = "CitySetting";

    private CitySettingActivity mActivity;

    public static ArrayList<String> mFullCitiesList = new ArrayList<String>();

    public CitySettingLogic(Activity activity) {
        super();
        this.mActivity = (CitySettingActivity) activity;
        getFullcities();
    }

    /**
     * return currentCity if Global.CITY_NAME don't exit return example city if
     * Global.CITY_NAME.length()>1 return Global.CITY_NAME else return
     * Global.LOCATION.
     * 
     * @param mActivity
     * @return current city
     */
    public static String getCurrentCity(Activity mActivity) {
        String city_name = SciflyStore.Global.getString(mActivity.getContentResolver(), Global.CITY_NAME);
        if (city_name.length() == 0) {
            Log.d(TAG, ">>>>>>>>the scifly store have not save city name,return example city:taibei or shanghai");
            if (PLATFORM.equals(Device.SCIFLY_PLATFORM_BOX)) {
                return mActivity.getResources().getString(R.string.example_city_for_826);
            } else {
                return mActivity.getResources().getString(R.string.example_city);
            }
        } else {
            String[] str = city_name.split(",");
            if (str.length - 1 > 0) {
                Log.d(TAG, ">>>>>the city is get from scifly store");
                return getCurrentLanguageCity(city_name, mActivity);
            } else {
                String location_city = "";
                Log.d(TAG, ">>>>>the city is get from location");
                String retStr = "";
                try {
                    JSONObject json = new JSONObject(Global.getString(mActivity.getContentResolver(), Global.LOCATION));
                    location_city = json.getString("city");
                    Log.d(TAG, "city_name from location is >>" + location_city);
                    // identify the encode
                    if (Utils.isChineseEncoding(location_city)) {
                        location_city = location_city.substring(0,
                                location_city.indexOf(mActivity.getString(R.string.city)));
                    }
                    String locationSpecies = "";
                    for (int i = 0; i < mFullCitiesList.size(); i++) {
                        String temp = mFullCitiesList.get(i);
                        if (temp.contains(location_city) && location_city != null) {
                            locationSpecies = temp;
                            break;
                        }
                    }

                    if (!TextUtils.isEmpty(locationSpecies)) {
                        Log.i(TAG, "locationSpecies= " + locationSpecies);
                        retStr = getCurrentLanguageCity(locationSpecies, mActivity);
                    }
                    Log.d(TAG, ">>>>>>>>>save location_city to scify store = " + location_city);
                    SciflyStore.Global.putString(mActivity.getContentResolver(), Global.CITY_NAME, locationSpecies);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                // if the city exit in the list of fullcities,return the city
                // according to current language
                // else return the city from location"
                return retStr.equals("") ? location_city : retStr;
            }
        }
    }

    /**
     * get autogetcity from db.
     * 
     * @return the autogetcity
     */
    public String autoGetCity() {
        String location_city = "";
        Log.e(TAG, ">>>>>the city is auto get from location");
        String retStr = "";
        try {
            JSONObject json = new JSONObject(Global.getString(mActivity.getContentResolver(), Global.LOCATION));
            Log.d(TAG, ">>>>>>>>>Json = " + json);
            location_city = json.getString("city");
            Log.e(TAG, "city_name from location is >>" + location_city);
            // identify the encode
            location_city = location_city.substring(0, location_city.indexOf(mActivity.getString(R.string.city)));
            String locationSpecies = "";
            // Confirm whether the autogetcity in the list of Fullcities
            // if exist continue the operation
            // beacuse if don't exist,the city can't get weather
            for (int i = 0; i < mFullCitiesList.size(); i++) {
                String temp = mFullCitiesList.get(i);
                if (temp.contains(location_city) && location_city != null) {
                    locationSpecies = temp;
                    break;
                }
            }
            // shows the auto city according to the current language
            if (!TextUtils.isEmpty(locationSpecies)) {
                Log.i(TAG, "locationSpecies= " + locationSpecies);
                retStr = getCurrentLanguageCity(locationSpecies, mActivity);
            }
            Log.d(TAG, ">>>>>>>>>save location_city to scify store = " + location_city);
            SciflyStore.Global.putString(mActivity.getContentResolver(), Global.CITY_NAME, locationSpecies);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        // if the city exit in the list of fullcities,return the city according
        // to current language
        // else return the city from location"
        return retStr.equals("") ? location_city : retStr;
    }

    /**
     * Get the list of cities in the file.
     */
    private void getFullcities() {
        InputStream inputStream = null;
		int weather_province = R.raw.weather_province_full;
		Log.d(TAG,"--------------"+SystemProperties.get("ro.product.model",""));
		if("H638".equals(SystemProperties.get("ro.product.model",""))){
			weather_province = R.raw.weather_province_tw;			
		}
        inputStream = mActivity.getResources().openRawResource(weather_province);
        mFullCitiesList = new ArrayList<String>();
        String str = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            // readLine save formate as CN,US,TW
            while ((str = bufferedReader.readLine()) != null) {
                str = str.trim().substring(str.lastIndexOf("=") + 1).replace("(", ",");
                mFullCitiesList.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param searchStr
     * @return the city list found by searchstr
     */

    @SuppressLint("DefaultLocale")
    public List<String> citySearching(String searchStr) {
        List<String> citySearchResults = new ArrayList<String>();
        List<String> citySearchReturn = new ArrayList<String>();
        if (!TextUtils.isEmpty(searchStr)) {
            for (int i = 0; i < mFullCitiesList.size(); i++) {
                String newSearch = searchStr.substring(0, 1).toUpperCase() + searchStr.substring(1);
                if (mFullCitiesList.get(i).contains(searchStr) || mFullCitiesList.get(i).contains(newSearch)) {
                    citySearchResults.add(mFullCitiesList.get(i));
                }
            }
        }
        // shows the corresponding city list according to the current language
        for (int i = 0; i < citySearchResults.size(); i++) {
            citySearchReturn.add(getCurrentLanguageCity(citySearchResults.get(i), mActivity));
        }
        return citySearchReturn;
    }

    /**
     * @param mActivity
     * @return the current language
     */
    private static String getCurrentLanguage(Activity mActivity) {
        Configuration conf = mActivity.getResources().getConfiguration();
        String language = conf.locale.getCountry();
        return language;
    }

    /**
     * @param city
     * @return the current city according to current language
     */
    public static String getCurrentLanguageCity(String city, Activity mActivity) {
        String[] str = city.split(",");
        if ("TW".equals(getCurrentLanguage(mActivity))) {
            return str[2];
        } else if ("US".equals(getCurrentLanguage(mActivity)) || "FR".equals(getCurrentLanguage(mActivity))) {
            return str[1];
        } else {
            return str[0];
        }
    }
}
