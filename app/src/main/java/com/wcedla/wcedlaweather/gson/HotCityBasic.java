package com.wcedla.wcedlaweather.gson;

import com.google.gson.annotations.SerializedName;

/*
* 解析热门城市的json解析类
* */

public class HotCityBasic {

    public String location;

    @SerializedName("parent_city")
    public String parentcity;

    @SerializedName("admin_area")
    public String admincity;
}
