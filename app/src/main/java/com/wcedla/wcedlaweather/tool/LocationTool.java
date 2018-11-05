package com.wcedla.wcedlaweather.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.wcedla.wcedlaweather.MainActivity;

import java.util.concurrent.Callable;

import static org.litepal.LitePalBase.TAG;

//百度定位服务获取定位信息类

public class LocationTool {

    private LocationClient mLocationClient;//位置客户端
    MyLocationListener myLocationListener;//位置监听器
    Handler.Callback callback;//回调通信用，获取位置信息后用于返回位置信息

    public  void initLocation(Context context, Handler.Callback callback)
    {
        this.callback=callback;//获取回调
        mLocationClient = new LocationClient(context);
        myLocationListener=new MyLocationListener();
        mLocationClient.registerNotifyLocationListener(myLocationListener);
        LocationClientOption option = new LocationClientOption();//设置定位参数
        option.setIsNeedAddress(true);//显示地址
        mLocationClient.setLocOption(option);
        mLocationClient.start();//开启定位

    }

    public  class MyLocationListener implements BDLocationListener
    {
        private String locationstring;

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append(bdLocation.getProvince()).append(" ");
            currentPosition.append(bdLocation.getCity()).append(" ");
            currentPosition.append(bdLocation.getDistrict()).append(" ");
            locationstring=currentPosition.toString();
            Message message=new Message();
            message.obj=locationstring;
            callback.handleMessage(message);//通过回调返回定位信息
            mLocationClient.stop();
        }

    }


}
