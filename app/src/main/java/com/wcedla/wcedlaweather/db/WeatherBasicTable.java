package com.wcedla.wcedlaweather.db;

import org.litepal.crud.LitePalSupport;

/*
* 存放天气信息的basic
* */

public class WeatherBasicTable extends LitePalSupport {

    private String cityName;

    private String parentName;

    private String adminName;

    private String cid;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
}
