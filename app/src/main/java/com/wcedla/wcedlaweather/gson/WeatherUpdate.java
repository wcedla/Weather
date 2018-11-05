package com.wcedla.wcedlaweather.gson;

import com.google.gson.annotations.SerializedName;

/*解析天气的更新时间的json解析类*/

public class WeatherUpdate {

    @SerializedName("loc")
    public String updatetime;
}
