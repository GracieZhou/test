
package com.heran.launcher2.weather;

import java.util.ArrayList;
import java.util.List;

public class WeatherWeek {
    private WeatherWeekBean currentWeatherWeek = new WeatherWeekBean();

    private List<WeatherWeekBean> weather7DaysDatas = new ArrayList<WeatherWeekBean>();

    public List<WeatherWeekBean> get7DaysWeatherDatas() {
        return weather7DaysDatas;
    }

    public void set7DaysWeatherDatas(int location, String time, String temperature, String weather_num) {
        if (weather7DaysDatas.size() <= location) {
            weather7DaysDatas.add(location, new WeatherWeekBean(time, temperature, weather_num));
        } else {
            weather7DaysDatas.set(location, new WeatherWeekBean(time, temperature, weather_num));
        }
    }

    public WeatherWeekBean getCurrentWeatherWeek() {
        return this.currentWeatherWeek;
    }

    public void setCurrentWeatherWeek(WeatherWeekBean mWeatherToday) {
        this.currentWeatherWeek = mWeatherToday;
    }

}
