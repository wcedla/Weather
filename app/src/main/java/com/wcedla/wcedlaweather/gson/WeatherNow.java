package com.wcedla.wcedlaweather.gson;

import com.google.gson.annotations.SerializedName;

/*解析天气的当前天气信息的json解析类*/

public class WeatherNow {

    @SerializedName("cond_code")
    public String weatherpicturecode;

    @SerializedName("cond_txt")
    public String weathertext;

    @SerializedName("fl")
    public String bodytemperature;

    @SerializedName("hum")
    public String humidity;

    @SerializedName("pcpn")
    public String raincount;

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("vis")
    public String couldsee;

    @SerializedName("wind_dir")
    public String winddirection;

    @SerializedName("wind_sc")
    public String windlevel;
}
