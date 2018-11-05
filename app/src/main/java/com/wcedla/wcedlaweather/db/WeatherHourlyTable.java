package com.wcedla.wcedlaweather.db;

import org.litepal.crud.LitePalSupport;

/*存放天气的逐小时预报的表*/

public class WeatherHourlyTable extends LitePalSupport {

    private String cityName;

    private String time;

    private  String temperature;

    private String Weather;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWeather() {
        return Weather;
    }

    public void setWeather(String weather) {
        Weather = weather;
    }
}
