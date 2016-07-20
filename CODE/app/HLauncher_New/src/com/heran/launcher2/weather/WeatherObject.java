
package com.heran.launcher2.weather;

public class WeatherObject {

    public String cityName;

    public String startTime;

    public String endTime;

    public String temperature;

    public String weatherTypeBitmapUrl;

    public String weatherType;

    public String currentTemp;

    public WeatherObject(String cityName, String startTime, String endTime, String temperature,
            String weatherTypeBitmapUrl, String weatherType, String currentTemp) {
        this.cityName = cityName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.temperature = temperature;
        this.weatherTypeBitmapUrl = weatherTypeBitmapUrl;
        this.weatherType = weatherType;
        this.currentTemp = currentTemp;
    }

}
