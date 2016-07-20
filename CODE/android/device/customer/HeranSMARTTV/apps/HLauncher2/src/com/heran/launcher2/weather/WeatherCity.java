
package com.heran.launcher2.weather;

public class WeatherCity {

    private String TAG = "WeatherCity";

    private String city;

    // json
    private String temperature; // 溫度

    private String weather_num; // 氣象圖代號

    private String rain; // 降雨機率

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setWeatherNum(String weather_num) {
        this.weather_num = weather_num;
    }

    public void setRain(String rain) {
        this.rain = rain;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public WeatherCity(String temperature, String weather_num, String rain) {
        this.temperature = temperature;
        this.weather_num = weather_num;
        this.rain = rain;
    }

    public WeatherCity() {
    }

    public String getTemperature() {
        return this.temperature;
    }

    public String getRian() {
        return this.rain;
    }

    public String getWeatherNum() {
        return this.weather_num;
    }

    public String getCityName() {
        return this.city;
    }

    @Override
    public String toString() {
        return "city:" + city + "--Temperature:" + temperature + "--Rain:" + rain + "--WeatherNum:" + weather_num;
    }
}
