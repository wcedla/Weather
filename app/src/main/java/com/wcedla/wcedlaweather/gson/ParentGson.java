package com.wcedla.wcedlaweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/*按省份查找的城市的解析市区的json解析类*/

public class ParentGson {

    public String parentname;

    @SerializedName("citylist")
    public List<CityGson> cityGsonList;
}
