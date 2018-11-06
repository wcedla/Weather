package com.wcedla.wcedlaweather.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.wcedla.wcedlaweather.MainActivity;
import com.wcedla.wcedlaweather.R;
import com.wcedla.wcedlaweather.db.WeatherNowTable;
import com.wcedla.wcedlaweather.db.WeatherUpdateTable;
import com.wcedla.wcedlaweather.gson.WeatherUpdate;
import com.wcedla.wcedlaweather.tool.HttpTool;
import com.wcedla.wcedlaweather.tool.JsonTool;
import com.wcedla.wcedlaweather.tool.SystemTool;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.support.constraint.Constraints.TAG;

public class WeatherUpdateService extends Service {

    WeatherBinder weatherBinder = new WeatherBinder();//binder和maninactivity关联

    Boolean isFirstRun=true;

    @Override
    public IBinder onBind(Intent intent) {
        return weatherBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "更新服务启动");
        //主要是为了第一次启动服务的时候不执行更新数据操作，一是因为没有必要，二是，如果数据很久没有更新并且前台更新服务给杀掉了
        //那就会发生重复刷新的问题，即监测是不是很久没刷新会监测到，这个启动时又更新的话就冲突了。
        if(!isFirstRun) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    updateWeather();
                    //Log.d(TAG, "服务更新天气，开了个线程,上面有执行说明成功刷新了，没有说明没有执行");
                }
            }).start();
        }
        isFirstRun=false;

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);//定时器
        int rate = 3;
        long updateTime = SystemClock.elapsedRealtime() + rate *60*60* 1000;
        Intent weatherIntent = new Intent(this, WeatherUpdateService.class);//重新执行一遍本段代码段
        PendingIntent updatePI = PendingIntent.getService(this, 0, weatherIntent, 0);
        alarmManager.cancel(updatePI);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, updateTime, updatePI);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        SharedPreferences sharedPreferences = getSharedPreferences("cityselect", MODE_PRIVATE);
        final String cityName = sharedPreferences.getString("cityname", "");
        List<WeatherNowTable> weatherNowTableList = LitePal.where("cityName=?", cityName).find(WeatherNowTable.class);
        //更新是要看一下是不是已经有天气数据了
        if(weatherNowTableList.size()>0) {
            Log.d(TAG, "服务更新天气执行" + cityName);
            String url = "https://free-api.heweather.com/s6/weather?key=c864606856d54eedb9f63a6cc0edd91f&location=" + Uri.encode(cityName);
            HttpTool.doHttpRequest(url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(WeatherUpdateService.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responsedata = null;
                    if (response.body() != null) {
                        responsedata = response.body().string();
                    }
                    JsonTool.dealWeatherJson(responsedata, cityName);
                }
            });
        }
    }

    //数据绑定类
    public class WeatherBinder extends Binder {

        public void SetNotification() {
            SharedPreferences sharedPreferences = getSharedPreferences("cityselect", MODE_PRIVATE);
            String cityName = sharedPreferences.getString("cityname", "");
            Intent foregroundIntent = new Intent(WeatherUpdateService.this, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("cityname", cityName);
            //自定义通知栏布局
            RemoteViews remoteView = new RemoteViews(getPackageName(), R.layout.weather_notification);
            List<WeatherNowTable> weatherNowTableList = LitePal.where("cityName=?", cityName).find(WeatherNowTable.class);
            List<WeatherUpdateTable> weatherUpdateTableList = LitePal.where("cityName=?", cityName).find(WeatherUpdateTable.class);
            //如果天气表有数据就显示数据，没有数据就显示null
            if(weatherNowTableList.size()<1||weatherUpdateTableList.size()<1)
            {
                remoteView.setTextViewText(R.id.notification_weather, "Null");
                remoteView.setImageViewResource(R.id.notification_weather_image, R.drawable.weather_null);
                remoteView.setTextViewText(R.id.notification_city, cityName);
                remoteView.setTextViewText(R.id.notification_temperature, "Null");
                remoteView.setTextViewText(R.id.notification_updatetime, "Null");
            }
            else if(weatherNowTableList.size()>0&&weatherUpdateTableList.size()>0) {
                remoteView.setTextViewText(R.id.notification_weather, weatherNowTableList.get(0).getWeatherText());
                remoteView.setImageViewResource(R.id.notification_weather_image, SystemTool.getResourceByReflect("weather_" + weatherNowTableList.get(0).getIconId()));
                remoteView.setTextViewText(R.id.notification_city, cityName);
                remoteView.setTextViewText(R.id.notification_temperature, weatherNowTableList.get(0).getTemperature() + "℃");
                remoteView.setTextViewText(R.id.notification_updatetime, weatherUpdateTableList.get(0).getUpdateTime().split(" ")[1] + "更新");
            }
            foregroundIntent.putExtras(bundle);//mainactivity启动需要带参数。，需要配合pendingintent的upgrade的flag参数
            PendingIntent foregroundPI = PendingIntent.getActivity(WeatherUpdateService.this, 0, foregroundIntent, PendingIntent.FLAG_UPDATE_CURRENT);


            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = null;
            //适配安卓8.0的通知
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                channel = new NotificationChannel("wcedla_1",
                        "wcedlaNotification", NotificationManager.IMPORTANCE_LOW);//显示在任何位置没有声音
//                channel.enableVibration(false);
//                channel.setVibrationPattern(new long[]{0});
//                    channel.enableLights(true); //是否在桌面icon右上角展示小红点
//                    channel.setLightColor(Color.GREEN); //小红点颜色
//                    channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
                notificationManager.createNotificationChannel(channel);
                Notification.Builder builder = new Notification.Builder(WeatherUpdateService.this, "wcedla_1"); //与channelId对应
                //icon title text必须包含，不然影响桌面图标小红点的展示
                builder.setSmallIcon(R.drawable.wcedla_notification)
                        .setContentTitle("xxx")
                        .setContentText("xxx")
                        .setCustomContentView(remoteView)
                        .setContentIntent(foregroundPI)
                        .setOngoing(true);
                //.setNumber(3); //久按桌面图标时允许的此条通知的数量
                //NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                startForeground(13, builder.build());

                Log.d(TAG, "前台服务启动完成");
            } else {
                Notification notification = new NotificationCompat.Builder(WeatherUpdateService.this, "1")
                        .setContentTitle("wcedla")
                        .setContentText("看到这个说明你看到bug了")
                        .setSmallIcon(R.drawable.wcedla_notification)
                        .setContentIntent(foregroundPI)
                        .setContent(remoteView)
                        .build();
                startForeground(13, notification);
            }


        }

    }
}
