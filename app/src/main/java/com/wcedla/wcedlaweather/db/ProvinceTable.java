package com.wcedla.wcedlaweather.db;

import org.litepal.crud.LitePalSupport;

/*
* 存放按省份查找的所有省份的表
* */

public class ProvinceTable extends LitePalSupport {

    private String provincename;

    public String getProvincename() {
        return provincename;
    }

    public void setProvincename(String provincename) {
        this.provincename = provincename;
    }
}
