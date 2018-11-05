package com.wcedla.wcedlaweather.db;

import org.litepal.crud.LitePalSupport;

/*
* 存放天气的未来天气的表
* */

public class WeatherForecastTable extends LitePalSupport {

    private String cityName;

    private String dayIconCode;

    private String nightIconCode;

    private String dayWeatherText;

    private String nightWeatherText;

    private String forecastDate;

    private String humidity;

    private String whetherRain;

    private String sunRaise;

    private String sunSet;

    private String maxTemperature;

    private String minTemperature;

    private String windDirection;

    private String windLevel;

    public String getCityname() {
        return cityName;
    }

    public void setCityname(String cityName) {
        this.cityName = cityName;
    }

    public String getDayIconCode() {
        return dayIconCode;
    }

    public void setDayIconCode(String dayIconCode) {
        this.dayIconCode = dayIconCode;
    }

    public String getNightIconCode() {
        return nightIconCode;
    }

    public void setNightIconCode(String nightIconCode) {
        this.nightIconCode = nightIconCode;
    }

    public String getDayWeatherText() {
        return dayWeatherText;
    }

    public void setDayWeatherText(String dayWeatherText) {
        this.dayWeatherText = dayWeatherText;
    }

    public String getNightWeatherText() {
        return nightWeatherText;
    }

    public void setNightWeatherText(String nightWeatherText) {
        this.nightWeatherText = nightWeatherText;
    }

    public String getForecastDate() {
        return forecastDate;
    }

    public void setForecastDate(String forecastDate) {
        this.forecastDate = forecastDate;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getWhetherRain() {
        return whetherRain;
    }

    public void setWhetherRain(String whetherRain) {
        this.whetherRain = whetherRain;
    }

    public String getSunRaise() {
        return sunRaise;
    }

    public void setSunRaise(String sunRaise) {
        this.sunRaise = sunRaise;
    }

    public String getSunSet() {
        return sunSet;
    }

    public void setSunSet(String sunSet) {
        this.sunSet = sunSet;
    }

    public String getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(String maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public String getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(String minTemperature) {
        this.minTemperature = minTemperature;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getWindLevel() {
        return windLevel;
    }

    public void setWindLevel(String windLevel) {
        this.windLevel = windLevel;
    }
}
