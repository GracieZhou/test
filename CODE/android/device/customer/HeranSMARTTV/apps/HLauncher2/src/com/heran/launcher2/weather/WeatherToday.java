
package com.heran.launcher2.weather;

import java.util.ArrayList;
import java.util.List;

public class WeatherToday {

    private WeatherTodayBean currentWeatherToday = new WeatherTodayBean();

    private List<WeatherTodayBean> weather24Datas = new ArrayList<WeatherTodayBean>();

    public WeatherToday() {
        // TODO Auto-generated constructor stub
    }

    public List<WeatherTodayBean> get24hWeatherTodayDatas() {
        return weather24Datas;
    }

    public void set24hWeatherDatas(int location, String time, String temperature, String weather_num,
            String wind_direction, String rain, String humidity, String weather_str, String wind_lv) {
        if (weather24Datas.size() <= location) {
            weather24Datas.add(location, new WeatherTodayBean(time, temperature, weather_num, wind_direction, rain,
                    humidity, weather_str, wind_lv));
        } else {
            weather24Datas.set(location, new WeatherTodayBean(time, temperature, weather_num, wind_direction, rain,
                    humidity, weather_str, wind_lv));
        }
    }

    public WeatherTodayBean getCurrentWeatherToday() {
        return this.currentWeatherToday;
    }

    public void setCurrentWeatherToday(WeatherTodayBean mWeatherToday) {
        this.currentWeatherToday = mWeatherToday;
    }

}
