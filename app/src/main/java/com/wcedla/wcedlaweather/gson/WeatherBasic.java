package com.wcedla.wcedlaweather.gson;

import com.google.gson.annotations.SerializedName;

/*解析天气的基本信息的json解析类*/

public class WeatherBasic {

    @SerializedName("location")
    public String cityname;

    @SerializedName("parent_city")
    public String parentname;

    @SerializedName("admin_area")
    public String adminname;

    @SerializedName("cid")
    public String cityid;
}
