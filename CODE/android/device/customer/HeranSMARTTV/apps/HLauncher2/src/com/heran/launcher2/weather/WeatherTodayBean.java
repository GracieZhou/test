
package com.heran.launcher2.weather;

public class WeatherTodayBean {

    public String time; // 時間

    private String temperature; // 溫度

    private String weather_num; // 天氣圖代號

    private String wind_direction; // 風向

    private String rain; // 降雨機率

    private String humidity; // 相對濕度

    private String weather_str; // 天氣狀態

    private String wind_lv; // 風向強度

    public String weatherString;

    public WeatherTodayBean() {
        // TODO Auto-generated constructor stub
    }

    WeatherTodayBean(String time, String temperature, String weather_num, String wind_direction, String rain,
            String humidity, String weather_str, String wind_lv) {
        this.time = time;
        this.temperature = temperature;
        this.weather_num = weather_num;
        this.wind_direction = wind_direction;
        this.rain = rain;
        this.humidity = humidity;
        this.weather_str = weather_str;
        this.wind_lv = wind_lv;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setWeatherNum(String weather_num) {
        this.weather_num = weather_num;
    }

    public String getWeather_num() {
        return weather_num;
    }

    public void setWind_direction(String wind_direction) {
        this.wind_direction = wind_direction;
    }

    public String getWind_direction() {
        return wind_direction;
    }

    public void setRain(String rain) {
        this.rain = rain;
    }

    public String getRain() {
        return rain;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setWeather_str(String weather_str) {
        this.weather_str = weather_str;
    }

    public String getWeather_str() {
        return weather_str;
    }

    public void setWind_lv(String wind_lv) {
        this.wind_lv = wind_lv;
    }

    public String getWind_lv() {
        return wind_lv;
    }

}
