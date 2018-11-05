package com.wcedla.wcedlaweather.adapter;

/*
* 热门城市的listview的数据绑定类，设置数据格式
**/

public class CityInfo {

    private String cityname;//城市名

    private String parentcityname;//市区名

    private String admincityname;//省份名

    public CityInfo(String cityname,String parentcityname,String admincityname)
    {
        this.cityname=cityname;
        this.parentcityname=parentcityname;
        this.admincityname=admincityname;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getParentcityname() {
        return parentcityname;
    }

    public void setParentcityname(String parentcityname) {
        this.parentcityname = parentcityname;
    }

    public String getAdmincityname() {
        return admincityname;
    }

    public void setAdmincityname(String admincityname) {
        this.admincityname = admincityname;
    }
}
