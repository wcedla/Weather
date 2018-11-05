package com.wcedla.wcedlaweather.db;

import org.litepal.crud.LitePalSupport;

/*
* 存放当前天气的现在天气的表
* */

public class WeatherNowTable extends LitePalSupport {

    private String cityName;

    private String iconId;

    private String weatherText;

    private String bodyTemperature;

    private String humidity;

    private String rainCount;

    private String temperature;

    private String couldSee;

    private String windDirection;

    private String windLevel;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getIconId() {
        return iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }

    public String getWeatherText() {
        return weatherText;
    }

    public void setWeatherText(String weatherText) {
        this.weatherText = weatherText;
    }

    public String getBodyTemperature() {
        return bodyTemperature;
    }

    public void setBodyTemperature(String bodyTemperature) {
        this.bodyTemperature = bodyTemperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getRainCount() {
        return rainCount;
    }

    public void setRainCount(String rainCount) {
        this.rainCount = rainCount;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getCouldSee() {
        return couldSee;
    }

    public void setCouldSee(String couldSee) {
        this.couldSee = couldSee;
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
