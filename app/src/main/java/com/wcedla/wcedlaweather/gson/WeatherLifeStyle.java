package com.wcedla.wcedlaweather.gson;

import com.google.gson.annotations.SerializedName;

/*解析天气的生活质量的json解析类*/

public class WeatherLifeStyle {

    public String type;

    @SerializedName("brf")
    public String shorttext;

    @SerializedName("txt")
    public String lifestyletext;
}
