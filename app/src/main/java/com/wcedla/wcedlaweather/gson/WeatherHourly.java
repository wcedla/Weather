package com.wcedla.wcedlaweather.gson;

import com.google.gson.annotations.SerializedName;

/*解析天气的逐小时预报的json解析类*/

public class WeatherHourly {

    public String time;

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond_txt")
    public String weatherText;

}
