package com.wcedla.wcedlaweather.adapter;

public interface DownloadListener {

    String getFileName();

    void onProgress(int progress);

    void onSuccess();

    void onFailed();

    void onPaused();

    void onCanceled();
}
