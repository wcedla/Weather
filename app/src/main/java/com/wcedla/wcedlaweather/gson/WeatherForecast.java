package com.wcedla.wcedlaweather.gson;

import com.google.gson.annotations.SerializedName;

/*解析天气的未来天气的json解析类*/

public class WeatherForecast {

    @SerializedName("cond_code_d")
    public String daycode;

    @SerializedName("cond_code_n")
    public String nightcode;

    @SerializedName("cond_txt_d")
    public String daytext;

    @SerializedName("cond_txt_n")
    public String nighttext;

    public String date;

    @SerializedName("hum")
    public String humidity;

    @SerializedName("pop")
    public String israinornot;

    @SerializedName("sr")
    public String sunraise;

    @SerializedName("ss")
    public String sunset;

    @SerializedName("tmp_max")
    public String maxtemperature;

    @SerializedName("tmp_min")
    public String mintemperature;

    @SerializedName("wind_dir")
    public String winddirection;

    @SerializedName("wind_sc")
    public String windlevel;


}
