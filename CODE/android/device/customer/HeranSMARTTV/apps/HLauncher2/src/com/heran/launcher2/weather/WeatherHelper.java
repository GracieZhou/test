
package com.heran.launcher2.weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.heran.launcher2.HomeApplication;
import com.heran.launcher2.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import scifly.provider.SciflyStore.Global;

public class WeatherHelper {

    private final static String TAG = "WeatherHelper";

    public static String getCurrentCityName(Context mContext) {
        String[] citys = mContext.getResources().getStringArray(R.array.city_array);
        String cityName = citys[0];
        SharedPreferences sp = mContext.getSharedPreferences("SP", 0);
        String city = sp.getString("city","臺北市");
        
            for (int i = 0; i < citys.length; i++) {
                if (city.equals(citys[i])) {
                    Log.d("city", "city_num[" +i+ "] : " + citys[i].substring(0, 2));
                    cityName = citys[i];
            }
        }
        Log.d(TAG, cityName);
        return cityName;
    }

    public static WeatherToday getWeatherTodayDataFromSp(String key) {
        WeatherToday mWeatherToday = new WeatherToday();
        String data = HomeApplication.getInstance().getSharePrefrerence("weather_today", key);
        try {
            JSONArray jar = new JSONArray(data);
            Log.d(TAG, "jobb:" + jar.toString());
            for (int j = 0; j < jar.length(); j++) {
                JSONObject obj_data = jar.getJSONObject(j);
                String time = obj_data.getString("時間");
                String temperature = obj_data.getString("溫度");
                String weather_num = obj_data.getString("氣象狀態");
                String wind_direction = obj_data.getString("風向");
                String rain = obj_data.getString("降雨機率");
                String humidity = obj_data.getString("濕度");
                String weather_str = obj_data.getString("氣象描述");
                String wind_lv = obj_data.getString("風級");
                mWeatherToday.set24hWeatherDatas(j, time, temperature, weather_num, wind_direction, rain, humidity,
                        weather_str, wind_lv);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mWeatherToday;
    }

    public static WeatherWeek getWeatherWeekDataFromSp(String key) {
        WeatherWeek mWeatherWeek = new WeatherWeek();
        String data = HomeApplication.getInstance().getSharePrefrerence("weather_week", key);
        try {
            JSONArray jar = new JSONArray(data);
            Log.d(TAG, "jobb:" + jar.toString());
            for (int j = 0; j < jar.length(); j++) {
                JSONObject obj_data = jar.getJSONObject(j);
                String time = obj_data.getString("時間");
                String temperature = obj_data.getString("溫度");
                String weather_num = obj_data.getString("氣象狀態");
                mWeatherWeek.set7DaysWeatherDatas(j, time, temperature, weather_num);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mWeatherWeek;
    }
}
