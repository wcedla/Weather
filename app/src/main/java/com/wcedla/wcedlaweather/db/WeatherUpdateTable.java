package com.wcedla.wcedlaweather.db;

import org.litepal.crud.LitePalSupport;

/*
* 存放天气的更新时间的表
* */

public class WeatherUpdateTable extends LitePalSupport {

    private String updateTime;

    private String cityName;

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
