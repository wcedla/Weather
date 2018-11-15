package com.wcedla.wcedlaweather.db;

import org.litepal.crud.LitePalSupport;

public class VersionTable extends LitePalSupport {

    private String versionCode;

    private String versionName;

    private String downloadUrl;

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
