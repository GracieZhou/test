
package com.heran.launcher2.weather;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.heran.launcher2.HomeApplication;
import com.heran.launcher2.util.Constants;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class WeatherAction {

    private final static String TAG = "WeatherAction";

    private final Handler mHandler;

    public boolean isRuning;

    String[] citys = {
            "基隆市", "臺北市", "新北市", "桃園市", "新竹市", "新竹縣", "苗栗縣", "臺中市", "彰化縣", "雲林縣", "南投縣", "嘉義縣", "嘉義市", "臺南市", "高雄市",
            "屏東縣", "宜蘭縣", "花蓮縣", "臺東縣", "澎湖縣", "金門縣", "連江縣"
    };

    private final ArrayList<WeatherToday> weatherTodayInfoList = new ArrayList<WeatherToday>();

    private final ArrayList<WeatherWeek> weatherweekInfoList = new ArrayList<WeatherWeek>();

    private final ArrayList<WeatherCity> weatherCityInfoList = new ArrayList<WeatherCity>();

    private final ArrayList<WeatherLife> weatherLifeInfoList = new ArrayList<WeatherLife>();

    public WeatherAction(Context mContext, Handler mHandler) {
        this.mHandler = mHandler;

        // 預設22筆今日氣象清單
        for (int i = 0; i < 22; i++) {
            WeatherToday today = new WeatherToday();

            today.getCurrentWeatherToday().setTime("");
            today.getCurrentWeatherToday().setTemperature("");
            today.getCurrentWeatherToday().setWeather_num("");
            today.getCurrentWeatherToday().setWind_direction("");
            today.getCurrentWeatherToday().setRain("");
            today.getCurrentWeatherToday().setHumidity("");
            today.getCurrentWeatherToday().setWeather_str("");
            today.getCurrentWeatherToday().setWind_lv("");
            weatherTodayInfoList.add(today);
        }

        // 預設22筆一周氣象清單
        for (int i = 0; i < 22; i++) {
            WeatherWeek week = new WeatherWeek();
            week.getCurrentWeatherWeek().setTime("");
            week.getCurrentWeatherWeek().setTemperature("");
            week.getCurrentWeatherWeek().setWeather_num("");
            weatherweekInfoList.add(week);
        }

        // 預設22筆城市氣象清單
        for (int i = 0; i < 22; i++) {
            WeatherCity city = new WeatherCity();
            city.setTemperature("");
            city.setRain("");
            city.setWeather_num("");
            weatherCityInfoList.add(city);
        }

        // 預設22筆生活氣象清單
        for (int i = 0; i < 22; i++) {
            WeatherLife life = new WeatherLife();
            life.setClothes_color("");
            life.setClothes("");
            life.setCar_color("");
            life.setCar("");
            life.setOutdoor_color("");
            life.setOutdoor("");
            life.setClothesline_color("");
            life.setClothesline("");
            weatherLifeInfoList.add(life);
        }

    }

    // 供外部取得資料
    public ArrayList<WeatherToday> getWeatherTodayInfoList() {
        return weatherTodayInfoList;
    }

    public ArrayList<WeatherWeek> getWeatherWeekInfoList() {
        return weatherweekInfoList;
    }

    public ArrayList<WeatherCity> getWeatherCityInfoList() {
        return weatherCityInfoList;
    }

    public ArrayList<WeatherLife> getWeatherLifeInfoList() {
        return weatherLifeInfoList;
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
                json = getJson("http://www.jowinwin.com/tedswitch/weather/today.php");
                if (json.equals("error")) {
                    Log.d(TAG, "wather_today getJson == error!");
                } else {
                    JSONArray array = new JSONArray(json);
                    Log.d(TAG, "wather_today array size : " + array.length());
                    for (int i = 0; i < array.length(); i++) {
                        WeatherToday today = weatherTodayInfoList.get(i);
                        JSONArray jar = array.getJSONArray(i);
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
                            today.set24hWeatherDatas(j, time, temperature, weather_num, wind_direction, rain, humidity,
                                    weather_str, wind_lv);
                        }
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
                json = getJson("http://www.jowinwin.com/tedswitch/weather/week.php");
                if (json.equals("error")) {
                    Log.d(TAG, "wather_week getJson == error!");
                } else {
                    JSONArray array = new JSONArray(json);
                    Log.d(TAG, "wather_week array size : " + array.length());
                    for (int i = 0; i < array.length(); i++) {
                        WeatherWeek week = weatherweekInfoList.get(i);
                        JSONArray jar = array.getJSONArray(i);
                        Log.d(TAG, "------week:lenth" + jar.length());
                        for (int j = 0; j < jar.length(); j++) {
                            JSONObject obj_data = jar.getJSONObject(j);
                            String time = obj_data.getString("時間");
                            String temperature = obj_data.getString("溫度");
                            String weather_num = obj_data.getString("氣象狀態");
                            week.set7DaysWeatherDatas(j, time, temperature, weather_num);
                        }
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
                json = getJson("http://www.jowinwin.com/tedswitch/weather/city.php");
                if (json.equals("error")) {
                    Log.d(TAG, "wather_city getJson == error!");
                } else {

                    JSONArray array = new JSONArray(json);
                    Log.d(TAG, "wather_city array size : " + array.length());

                    for (int i = 0; i < 3; i++) {
                        WeatherCity city = weatherCityInfoList.get(i);
                        if (i == 0) {
                            for (int j = 0; j < array.length(); j += 3) {
                                String t = array.getString(j);
                                city.setData(i, t);
                            }
                        }

                        if (i == 1) {
                            for (int j = 1; j < array.length(); j += 3) {
                                String w = array.getString(j);
                                city.setData(i, w);
                            }
                        }

                        if (i == 2) {
                            for (int j = 2; j < array.length(); j += 3) {
                                String r = array.getString(j);
                                city.setData(i, r);
                            }
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
        /*
         * (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            String json = "";
            try {
                json = getJson("http://www.jowinwin.com/tedswitch/weather/life.php");
                if (json.equals("error")) {
                    Log.d(TAG, "wather_city getJson == error!");
                } else {

                    JSONArray array = new JSONArray(json);
                    Log.d("life", "array : " + array.length());
                    for (int i = 0; i < array.length(); i++) {
                        WeatherLife life = weatherLifeInfoList.get(i);

                        JSONObject job = array.getJSONObject(i);
                        Log.d("life", "job[" + i + "]: " + job.length());
                        JSONObject jobb = job.getJSONObject(citys[i]);
                        Log.d("life", "jobb : " + jobb.length());
                        String clothes_color = jobb.getString("穿衣顏色");
                        Log.d("life", "jobb[" + i + "]: " + clothes_color);
                        life.setClothes_color(clothes_color);
                        String clothes = jobb.getString("穿衣字串");
                        life.setClothes(clothes);
                        String car_color = jobb.getString("行車顏色");
                        Log.d("life", "jobb[" + i + "]: " + car_color);
                        life.setCar_color(car_color);
                        String car = jobb.getString("行車字串");
                        life.setCar(car);
                        String outdoor_color = jobb.getString("戶外顏色");
                        Log.d("life", "jobb[" + i + "]: " + outdoor_color);
                        life.setOutdoor_color(outdoor_color);
                        String outdoor = jobb.getString("戶外字串");
                        life.setOutdoor(outdoor);
                        String Clothesline_color = jobb.getString("曬衣顏色");
                        Log.d("life", "jobb[" + i + "]: " + Clothesline_color);
                        life.setClothesline_color(Clothesline_color);
                        String Clothesline = jobb.getString("曬衣字串");
                        life.setClothesline(Clothesline);

                    }

                }

                mHandler.sendEmptyMessage(Constants.WEARHER_Life);

            } catch (Exception e) {
                Log.d(TAG, "debug 1: " + e.toString());
            }
        }

    };

    public String getJson(String url) {
        Log.d("jack", "getJson ");
        String result = "";
        InputStream is = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            HttpResponse response = httpclient.execute(httppost);
            int state = response.getStatusLine().getStatusCode();
            if (state != 200) {
                Log.d(TAG, "getJson != 200");
                result = "error";
            } else {
                Log.d(TAG, "getJson = 200");

                HttpEntity entity = response.getEntity();
                is = entity.getContent();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf8"), 9999999);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();

        } catch (Exception e) {
            Log.d(TAG, "error 1!!" + e.toString());
        }

        return result;
    }
}
