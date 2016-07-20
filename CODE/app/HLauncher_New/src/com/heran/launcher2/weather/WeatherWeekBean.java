
package com.heran.launcher2.weather;

public class WeatherWeekBean {

    // json
    private String time;

    private String weather_num;

    private String temperature;

    public WeatherWeekBean() {
        // TODO Auto-generated constructor stub
    }

    public WeatherWeekBean(String time, String temperature, String weather_num) {
        this.time = time;
        this.weather_num = weather_num;
        this.temperature = temperature;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setWeather_num(String weather_num) {
        this.weather_num = weather_num;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getTime() {
        return this.time;
    }

    public String getTemperature() {
        return this.temperature;
    }

    public String getWeather_num() {
        return this.weather_num;
    }
}
