package com.wcedla.wcedlaweather.db;

import org.litepal.crud.LitePalSupport;

/*
* 存所有热门城市的表
* */

public class HotCityTable extends LitePalSupport {

    private String cityname;

    private String parentcityname;

    private String admincityname;

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getParentcityname() {
        return parentcityname;
    }

    public void setParentcityname(String parentcityname) {
        this.parentcityname = parentcityname;
    }

    public String getAdmincityname() {
        return admincityname;
    }

    public void setAdmincityname(String admincityname) {
        this.admincityname = admincityname;
    }
}
