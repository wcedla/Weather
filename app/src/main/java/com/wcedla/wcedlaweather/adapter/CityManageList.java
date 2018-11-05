package com.wcedla.wcedlaweather.adapter;

import com.wcedla.wcedlaweather.CityManage;

/*
* 城市管理listview的数据类型类
* */

public class CityManageList {

    private String cityName;

    private String weatherText;

    private String temperatureText;

    public CityManageList(String cityName,String weatherText,String temperatureText)
    {
        this.cityName=cityName;//城市名
        this.weatherText=weatherText;//天气
        this.temperatureText=temperatureText;//温度
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getWeatherText() {
        return weatherText;
    }

    public void setWeatherText(String weatherText) {
        this.weatherText = weatherText;
    }

    public String getTemperatureText() {
        return temperatureText;
    }

    public void setTemperatureText(String temperatureText) {
        this.temperatureText = temperatureText;
    }
}
