package com.wcedla.wcedlaweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/*按省份查找的城市的解析的完整json的json解析类*/

public class ChinaGson {

    @SerializedName("china")
    public List<AdminGson> adminGsonList;
}
