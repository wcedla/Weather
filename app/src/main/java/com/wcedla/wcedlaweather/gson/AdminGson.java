package com.wcedla.wcedlaweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/*按省份查找的城市的解析省份的json解析类*/
public class AdminGson {

    public String adminname;

    @SerializedName("parentlist")
    public List<ParentGson> parentGsonList;
}
