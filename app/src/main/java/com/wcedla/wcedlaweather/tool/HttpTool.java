package com.wcedla.wcedlaweather.tool;

import android.util.Log;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static org.litepal.LitePalBase.TAG;

/*网络工具类*/

public class HttpTool {

    /*根据网址联网获取参数*/
    public static void doHttpRequest(String url, Callback callback)
    {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(callback);//enqueue方法内部已经实现了创建子线程处理网络连接服务，并把数据返回给回调函数。
    }

}
