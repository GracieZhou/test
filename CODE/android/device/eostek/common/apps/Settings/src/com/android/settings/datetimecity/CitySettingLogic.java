
package com.android.settings.datetimecity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.settings.R;
import com.android.settings.userbackup.BackUpData;
import com.android.settings.util.Utils;
import android.app.Activity;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import scifly.provider.SciflyStore;
import scifly.provider.SciflyStore.Global;

public class CitySettingLogic {

    private String TAG = "CITY";

    private CitySettingActivity mActivity;

    private List<String> mAllCityList;

    private List<String> mCitySearchResults;

    private List<String> mFullProvinceList;

    private List<String> mFullCitiesList;

    public ArrayList<String> mCityListZHCN = new ArrayList<String>();

    public ArrayList<String> mCityListEN = new ArrayList<String>();

    public ArrayList<String> mCityListZHTW = new ArrayList<String>();

    private String mZHCityName;

    private String mENCityName;

    private String mTWCityName;

    public CitySettingLogic(Activity activity) {
        super();
        this.mActivity = (CitySettingActivity) activity;
        getFullAllProvinces();
    }



    public String getCurrentCity() {
        String city_name = SciflyStore.Global.getString(mActivity.getContentResolver(), Global.CITY_NAME);
        Log.e(TAG, ">>>>>>>>city_name.length = " + city_name.length());
        if(city_name.length() == 0){
            Log.e(TAG, ">>>>>>>>the scifly store have not save city name,return example city:taibei");
            return mActivity.getResources().getString(R.string.example_city);
        }else{
            String[] str = city_name.split(",");
            Log.e(TAG, ">>>>>>>>str.length = " + str.length);
            if (str.length -1  > 0) {
                Log.e(TAG, ">>>>>the city is get from scifly store");
                if ("US".equals(getCurrentLanguage())) {
                    return str[1];
                } else if ("TW".equals(getCurrentLanguage())) {
                    return str[2];
                } else {
                    return str[0];
                }
            } else {
                String location_city = null;
                Log.e(TAG, ">>>>>the city is get from location");
                String retStr="";
                try {
                    JSONObject json = new JSONObject(Global.getString(mActivity.getContentResolver(), Global.LOCATION));
                    Log.d(TAG, ">>>>>>>>>get from location Json = " + json);
                    location_city = json.getString("city");
                    Log.e(TAG, "city_name from location is >>"+location_city);
                    // identify the encode
                    if (Utils.isChineseEncoding(location_city)) {
                        location_city = location_city.substring(0,
                                location_city.indexOf(mActivity.getString(R.string.city)));
                    }
                    String locationSpecies="";
                    for(int i=0;i<mFullCitiesList.size();i++){
                        String temp=mFullCitiesList.get(i);
                        if(temp.contains(location_city)&&location_city!=null){
                            locationSpecies=temp;
                            break;
                        }
                    }
                    
                    if(locationSpecies!=null&&!locationSpecies.equals("")){
                        Log.i(TAG,"locationSpecies= " + locationSpecies);
                        String[] strings = locationSpecies.split("\\(");
                        strings[1]=strings[1].replace(")", "");
                        strings[2]=strings[2].replace(")", "");
                        if ("TW".equals(getCurrentLanguage())) {
                            retStr = strings[2];
                        } else if ("US".equals(getCurrentLanguage())) {
                            retStr = strings[1];
                        } else {
                            retStr = strings[0];
                        }
                        StringBuffer strBuffer=new StringBuffer();
                        strBuffer.append(strings[0]+",");
                        strBuffer.append(strings[1]+",");
                        strBuffer.append(strings[2]);
                        locationSpecies=strBuffer.toString();
                    }
                    
                    Log.d(TAG, ">>>>>>>>>save location_city to scify store = " + location_city);
                    SciflyStore.Global.putString(mActivity.getContentResolver(), Global.CITY_NAME, locationSpecies);
                } catch (JSONException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                return retStr.equals("")?location_city:retStr;
            }
        }
    }

    public String autoGetCity() {
        String location_city = "";
        Log.e(TAG, ">>>>>the city is auto get from location");
        String retStr="";
        try {
            JSONObject json = new JSONObject(Global.getString(mActivity.getContentResolver(), Global.LOCATION));
            Log.d(TAG, ">>>>>>>>>Json = " + json);
            location_city = json.getString("city");
            Log.e(TAG, "city_name from location is >>"+location_city);
            // identify the encode
            if (Utils.isChineseEncoding(location_city)) {
                        location_city = location_city.substring(0,
                                location_city.indexOf(mActivity.getString(R.string.city)));
            }
            String locationSpecies="";
            for(int i=0;i<mFullCitiesList.size();i++){
                String temp=mFullCitiesList.get(i);
                if(temp.contains(location_city)&&location_city!=null){
                    locationSpecies=temp;
                    break;
                }
            }
            
            if(locationSpecies!=null&&!locationSpecies.equals("")){
                Log.i(TAG,"locationSpecies= " + locationSpecies);
                String[] strings = locationSpecies.split("\\(");
                strings[1]=strings[1].replace(")", "");
                strings[2]=strings[2].replace(")", "");
                if ("TW".equals(getCurrentLanguage())) {
                    retStr = strings[2];
                } else if ("US".equals(getCurrentLanguage())) {
                    retStr = strings[1];
                } else {
                    retStr = strings[0];
                }
                StringBuffer strBuffer=new StringBuffer();
                strBuffer.append(strings[0]+",");
                strBuffer.append(strings[1]+",");
                strBuffer.append(strings[2]);
                locationSpecies=strBuffer.toString();
            }
            
            Log.d(TAG, ">>>>>>>>>save location_city to scify store = " + location_city);
            SciflyStore.Global.putString(mActivity.getContentResolver(), Global.CITY_NAME, locationSpecies);
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return retStr.equals("")?location_city:retStr;
    }

    private void getFullAllProvinces() {
        HashMap<String, LinkedHashMap<String, String>> v2 = new HashMap<String, LinkedHashMap<String, String>>();
        InputStream inputStream = null;
        inputStream = mActivity.getResources().openRawResource(R.raw.weather_province_full);
        mFullProvinceList = new ArrayList<String>();
        mFullCitiesList = new ArrayList<String>();
        String str = null;
        String key = null;
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while ((str = bufferedReader.readLine()) != null) {
                str = str.trim();
                if (str.indexOf("=") == -1 && (str.indexOf("[") == -1 && str.indexOf("]") == -1)) {
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

    public List<String> citySearching(String searchStr) {
        mCitySearchResults = new ArrayList<String>();
        try {
            getFullAllProvinces();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(searchStr)) {
            for (int i = 0; i < mFullCitiesList.size(); i++) {
                String newSearch = searchStr.substring(0, 1).toUpperCase() + searchStr.substring(1);
                if (mFullCitiesList.get(i).contains(searchStr) || mFullCitiesList.get(i).contains(newSearch)) {
                    String city = mFullCitiesList.get(i);
                    mCitySearchResults.add(mFullCitiesList.get(i));

                }
            }
            getZHAndENCitys();
            if ("TW".equals(getCurrentLanguage())) {
                mCitySearchResults = mCityListZHTW;
            } else if ("US".equals(getCurrentLanguage())) {
                mCitySearchResults = mCityListEN;
            } else {
                mCitySearchResults = mCityListZHCN;
            }
        }
        return mCitySearchResults;

    }

    private void getZHAndENCitys() {
        for (int i = 0; i < mCitySearchResults.size(); i++) {
            String[] strings = mCitySearchResults.get(i).toString().split("\\(");
            mCityListZHCN.add(strings[0]);
            mCityListEN.add(strings[1].replace(")", ""));
            mCityListZHTW.add(strings[2].replace(")", ""));
        }
    }

    public String getCurrentLanguage() {
        Configuration conf = mActivity.getResources().getConfiguration();
        String language = conf.locale.getCountry();
        return language;
    }

    public String getcountry() {
        String country = mActivity.getResources().getString(R.string.country);
        return country;
    }
}
