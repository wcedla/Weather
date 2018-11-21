package com.wcedla.wcedlaweather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.wcedla.wcedlaweather.db.CityListTable;
import com.wcedla.wcedlaweather.db.WeatherNowTable;
import com.wcedla.wcedlaweather.tool.SystemTool;

import org.litepal.LitePal;

import java.time.Clock;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.Constraints.TAG;

public class myWidget extends AppWidgetProvider {
    static Intent alarmClockIntent;


    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_widget);
        if (alarmClockIntent == null) {
            alarmClockIntent = SystemTool.getAlarmClockIntent(context);
        }
        if (alarmClockIntent != null) {
            PendingIntent clockPendingIntent = PendingIntent.getActivity(context, 10, alarmClockIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_time, clockPendingIntent);
        }
        Intent cityIntent = new Intent("com.wcedla.widget.clock.CITY");
        cityIntent.setComponent(new ComponentName(context, myWidget.class));
        PendingIntent cityPendingIntent = PendingIntent.getBroadcast(context, 11, cityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        SharedPreferences sharedPreferences = context.getSharedPreferences("widgetCity", MODE_PRIVATE);
        String cityName = sharedPreferences.getString("nowCity", "null");
        List<WeatherNowTable> weatherNowTableList = LitePal.where("cityName=?", cityName).find(WeatherNowTable.class);
        String weatherText = "null";
        String temperatureText = "null";
        if (weatherNowTableList.size() > 0) {
            weatherText = weatherNowTableList.get(0).getWeatherText();
            temperatureText = weatherNowTableList.get(0).getTemperature() + "℃";
        }
        views.setTextViewText(R.id.widget_city, cityName);
        views.setOnClickPendingIntent(R.id.widget_city, cityPendingIntent);
        views.setTextViewText(R.id.widget_weather, weatherText);
        views.setTextViewText(R.id.widget_temperature, temperatureText);
        Intent weatherIntent=new Intent(context,MainActivity.class);
        SharedPreferences weatherPreference = context.getSharedPreferences("cityselect", MODE_PRIVATE);
        String weatherCity = weatherPreference.getString("cityname", "");
        Bundle bundle=new Bundle();
        bundle.putString("cityname", weatherCity);
        weatherIntent.putExtras(bundle);
        PendingIntent weatherPendingIntent=PendingIntent.getActivity(context, 100, weatherIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_temperature,weatherPendingIntent);
        views.setOnClickPendingIntent(R.id.widget_weather,weatherPendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("cityselect", MODE_PRIVATE);
        String cityName = sharedPreferences.getString("cityname", "null");
        SharedPreferences.Editor editor = context.getSharedPreferences("widgetCity", MODE_PRIVATE).edit();
        editor.putString("nowCity", cityName);
        editor.apply();
    }

    @Override
    public void onDisabled(Context context) {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        //Log.d(TAG, "情况1" + action);
        if (action.equals("com.wcedla.widget.clock.CITY")) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("widgetCity", MODE_PRIVATE);
            String cityName = sharedPreferences.getString("nowCity", "");
            List<CityListTable> cityListTableList = LitePal.findAll(CityListTable.class);//读取城市管理表，得到已经添加的城市
            int index = 0;
            for (int i = 0; i < cityListTableList.size(); i++) {
                if (cityListTableList.get(i).getCityName().equals(cityName)) {
                    index = i;
                }
            }
            index += 1;
            if (index >= cityListTableList.size()) {
                index = 0;
            }
            //Log.d(TAG, "城市" + cityListTableList.get(index).getCityName());
            SharedPreferences.Editor editor = context.getSharedPreferences("widgetCity", MODE_PRIVATE).edit();
            editor.putString("nowCity", cityListTableList.get(index).getCityName());
            editor.apply();
            int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context, myWidget.class));
            for (int id : ids) {
                updateAppWidget(context, appWidgetManager, id);
            }
        }
        super.onReceive(context, intent);
    }


}

