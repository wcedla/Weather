package com.wcedla.wcedlaweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/*解析天气的完整的json的json解析类*/

public class WeatherGson {

    public WeatherBasic basic;

    public WeatherUpdate update;

    public WeatherNow now;

    @SerializedName("daily_forecast")
    public List<WeatherForecast> forecasts;

    @SerializedName("hourly")
    public List<WeatherHourly> hourlyList;

    @SerializedName("lifestyle")
    public List<WeatherLifeStyle> lifeStyles;

}
