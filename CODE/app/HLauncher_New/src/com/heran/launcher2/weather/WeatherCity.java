
package com.heran.launcher2.weather;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class WeatherCity {

    private String TAG = "WeatherCity";

    // json
    private String temperature; // 溫度

    private String weather_num; // 氣象圖代號

    private String rain; // 降雨機率

    private List<String> data1 = new ArrayList<String>();

    private List<String> data2 = new ArrayList<String>();

    private List<String> data3 = new ArrayList<String>();

    public List<String> getData1() {
        return data1;
    }

    public List<String> getData2() {
        return data2;
    }

    public List<String> getData3() {
        return data3;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setWeather_num(String weather_num) {
        this.weather_num = weather_num;
    }

    public void setRain(String rain) {
        this.rain = rain;
    }

    public void setData(int i, String data) {
        // 溫度
        if (i == 0) {

            data1.add(data);

            Log.d(TAG, "t : " + data);
        }
        // 天氣圖
        if (i == 1) {

            data3.add(data);

        }
        // 降雨機率
        if (i == 2) {
            data2.add(data);

        }

    }

}
