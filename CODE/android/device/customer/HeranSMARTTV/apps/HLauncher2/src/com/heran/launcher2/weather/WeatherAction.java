
package com.heran.launcher2.weather;

import org.json.JSONArray;
import org.json.JSONObject;

import com.heran.launcher2.HomeApplication;
import com.heran.launcher2.R;
import com.heran.launcher2.util.Constants;
import com.heran.launcher2.util.ServiceJson;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class WeatherAction extends ServiceJson {

    private final static String TAG = "WeatherAction";

    private final Handler mHandler;

    public WeatherAction(Context mContext, Handler mHandler) {
        this.mHandler = mHandler;
    }

    // 開始解析資料
    public void getWeatherTodayData() {
        HomeApplication.getInstance().addWeatherTask(wather_today);
    }

    public void getWeatherWeekData() {
        HomeApplication.getInstance().addWeatherTask(wather_week);
    }

    public void getWeatherCity() {
        HomeApplication.getInstance().addWeatherTask(wather_city);
    }

    public void getWeatherLife() {
        HomeApplication.getInstance().addWeatherTask(wather_life);
    }

    // 今日氣象資料解析
    private final Runnable wather_today = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "check_date ");
            String json = "";
            try {
                json = getJson("http://www.jowinwin.com/tedswitch/weather/today.php", "", false);
                if (json.equals("error")) {
                    Log.d(TAG, "wather_today getJson == error!");
                } else {
                    JSONArray array = new JSONArray(json);
                    Log.d(TAG, "wather_today array size : " + array.length());
                    for (int i = 0; i < array.length(); i++) {
                        JSONArray jar = array.getJSONArray(i);
                        JSONObject obj = jar.getJSONObject(0);
                        String city = obj.getString("城市");
                        Log.d(TAG, city);
                        HomeApplication.getInstance().setSharePreference("weather_today",city,
                                jar.toString());
                    }
                    Log.d(TAG, "wather_today data update succuss ");
                    mHandler.sendEmptyMessage(Constants.WEARHER_TODAY);
                }
            } catch (Exception e) {
                Log.d(TAG, "debug 1: " + e.toString());
            }

        }
    };

    // 一周氣象解析
    private final Runnable wather_week = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "check_date ");
            String json = "";
            try {
                json = getJson("http://www.jowinwin.com/tedswitch/weather/week.php", "", false);
                if (json.equals("error")) {
                    Log.d(TAG, "wather_week getJson == error!");
                } else {
                    JSONArray array = new JSONArray(json);
                    Log.d(TAG, "wather_week array size : " + array.length());
                    for (int i = 0; i < array.length(); i++) {
                        JSONArray jar = array.getJSONArray(i);
                        JSONObject obj = jar.getJSONObject(0);
                        String city = obj.getString("城市");                       
                        HomeApplication.getInstance().setSharePreference("weather_week",city,
                                jar.toString());
                    }
                    Log.d(TAG, "wather_week data update succuss ");
                    mHandler.sendEmptyMessage(Constants.WEARHER_WEEK);
                }
            } catch (Exception e) {
                Log.d(TAG, "debug 1: " + e.toString());
            }

        }
    };

    // 城市氣象解析
    private final Runnable wather_city = new Runnable() {
        @Override
        public void run() {
            String json = "";
            try {
                json = getJson("http://www.jowinwin.com/tedswitch/weather/city.php", "", false);
                if (json.equals("error")) {
                    Log.d(TAG, "wather_city getJson == error!");
                } else {
                    JSONArray array = new JSONArray(json);
                    JSONObject mJsonObject = new JSONObject();
                    Log.d(TAG, "wather_city array size : " + array.length() + "");
                    for (int i = 0; i < array.length(); i = i + 4) {

                        mJsonObject.put("tmp", array.getString(i));
                        mJsonObject.put("tmp_num", array.getString(i + 1));
                        mJsonObject.put("rain", array.getString(i + 2));
                        mJsonObject.put("city", array.getString(i + 3));
                        String city = mJsonObject.getString("city");
                        if (i % 4 == 0) {
                            HomeApplication.getInstance().setSharePreference("weather_city", city,
                                    mJsonObject.toString());
                        }
                    }
                    Log.d(TAG, "wather_city data update succuss ");
                    mHandler.sendEmptyMessage(Constants.WEARHER_City);
                }

            } catch (Exception e) {
                Log.d(TAG, "debug 1: " + e.toString());
            }
        }

    };

    // 生活氣象解析
    private final Runnable wather_life = new Runnable() {
        @Override
        public void run() {
            String json = "";
            try {
                json = getJson("http://www.jowinwin.com/tedswitch/weather/life.php", "", false);
                if (json.equals("error")) {
                    Log.d(TAG, "wather_city getJson == error!");
                } else {
                    JSONArray array = new JSONArray(json);
                    Log.d(TAG, "array : " + array.length());
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject job = array.getJSONObject(i);

                        String cityName = job.names().getString(0);
                        Log.d(TAG, "job[" + i + "]: " + cityName);
                        // Log.d(TAG, "json:" + job);

                        JSONObject jobb = job.getJSONObject(cityName);

                        HomeApplication.getInstance().setSharePreference("weather_life", cityName, jobb.toString());
                    }

                }
                Log.d(TAG, "wather_life data update succuss ");
                mHandler.sendEmptyMessage(Constants.WEARHER_Life);

            } catch (Exception e) {
                Log.d(TAG, "debug 1: " + e.toString());
            }
        }

    };

}
