package com.wcedla.wcedlaweather.db;

import org.litepal.crud.LitePalSupport;

/*
* 存放天气信息的生活质量的表
* */

public class WeatherLifeStyleTable extends LitePalSupport {

    private String cityName;

    private String lifeStyleType;

    private String shortText;

    private String lifeStyleText;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getLifeStyleType() {
        return lifeStyleType;
    }

    public void setLifeStyleType(String lifeStyleType) {
        this.lifeStyleType = lifeStyleType;
    }

    public String getShortText() {
        return shortText;
    }

    public void setShortText(String shortText) {
        this.shortText = shortText;
    }

    public String getLifeStyleText() {
        return lifeStyleText;
    }

    public void setLifeStyleText(String lifeStyleText) {
        this.lifeStyleText = lifeStyleText;
    }
}
