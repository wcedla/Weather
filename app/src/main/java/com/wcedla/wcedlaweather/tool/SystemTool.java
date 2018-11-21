package com.wcedla.wcedlaweather.tool;

import android.app.Activity;
import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.wcedla.wcedlaweather.MainActivity;
import com.wcedla.wcedlaweather.R;
import com.wcedla.wcedlaweather.myWidget;

import org.litepal.LitePalBase;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.Constraints.TAG;

public class SystemTool {

    private static final int MIN_CLICK_DELAY_TIME = 300;//检测点击间隔时间，防止多次点击多次执行方法体
    private static long lastClickTime;//存取开始点击的时间

    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }



    /**
     * 导航栏，状态栏透明
     * @param activity
     */
    public static void setNavigationBarStatusBarTranslucent(Activity activity){
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 获取图片名称获取图片的资源id的方法
     * @param imageName
     * @return
     */
    public static int getResourceByReflect(String imageName){
        Class drawable  = R.drawable.class;
        Field field = null;
        int r_id;
        try {
            field = drawable.getField(imageName);
            r_id = field.getInt(field.getName());
        } catch (Exception e) {
            r_id=R.drawable.weather_null;
        }
        return r_id;
    }

    /**
     * 判断当前日期是星期几
     *
     * @param  pTime     设置的需要判断的时间  //格式如2012-09-08
     *

     * @return dayForWeek 判断结果
     * @Exception 发生异常
     */

//  String pTime = "2012-03-12";
    public static String getWeek(String pTime) {


        String Week = "星期";


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            Week += "天";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 2) {
            Week += "一";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 3) {
            Week += "二";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 4) {
            Week += "三";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 5) {
            Week += "四";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 6) {
            Week += "五";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 7) {
            Week += "六";
        }
        return Week;
    }

    public static Long getMillsForTimeStr(String pTime)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Log.d(TAG, "时间2"+c.getTime());
        return c.getTimeInMillis();
    }


    /**
     *获取给定参数和当前时间的时间差
     *
     */

    public static long timeDifference(String time,String type)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String nowTime=format.format(new Date());
        try {
            long diff=format.parse(nowTime).getTime()-format.parse(time).getTime();
            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
            long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
            switch (type)
            {
                case "second":
                    return diff;
                case "minute":
                    return minutes;
                case "hour":
                    return hours;
                case "day":
                    return days;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;

    }

    /**
     * 判断服务是否开启
     * 名称带包名全路径
     *
     * @return
     */
    public static boolean isServiceRunning(Context context, String ServiceName) {

        if (("").equals(ServiceName) || ServiceName == null)
            return false;
        ActivityManager myManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++)
        {
            if (runningService.get(i).service.getClassName().toString()
                    .equals(ServiceName)) {
                return true;
            }
        }
        return false;
    }

    public static Intent getAlarmClockIntent(Context context) {
        Intent intent = null;
        String activityName = "";
        String packageName = "";
        String alarmPackageName = "";
        List<PackageInfo> allPackageInfos = context.getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES); // 取得系统安装所有软件信息
        List<PackageInfo> sysPackageInfos = new ArrayList<>();
        if (!allPackageInfos.isEmpty()) {
            for (PackageInfo packageInfo : allPackageInfos) {
                ApplicationInfo appInfo = packageInfo.applicationInfo;// 得到每个软件信息
                if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    sysPackageInfos.add(packageInfo);// 系统软件
                }
            }
        }
        for (int i = 0; i < sysPackageInfos.size(); i++) {
            PackageInfo packageInfo = sysPackageInfos.get(i);
            packageName = packageInfo.packageName;

            if (packageName.contains("clock") && !packageName.contains("widget")) {
                ActivityInfo activityInfo = packageInfo.activities[0];
                if (activityInfo.name.contains("Alarm") || activityInfo.name.contains("DeskClock")) {
                    activityName = activityInfo.name;
                    alarmPackageName = packageName;
                }
            }
        }
        if ((activityName != "") && (alarmPackageName != ""))
        {
            intent= new Intent();
            Log.d(LitePalBase.TAG, "包信息"+alarmPackageName+","+activityName);
            intent.setComponent(new ComponentName(alarmPackageName, activityName));
            //startActivity(intent);
            return intent;
        }
        else
        {
            return intent;
        }
    }

    public static void updateWidgetForActivity(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("cityselect", MODE_PRIVATE);
        String cityName = sharedPreferences.getString("cityname", "null");
        SharedPreferences.Editor editor = context.getSharedPreferences("widgetCity", MODE_PRIVATE).edit();
        editor.putString("nowCity", cityName);
        editor.apply();
        AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(context);
        int[] ids=appWidgetManager.getAppWidgetIds(new ComponentName(context,myWidget.class));
        for(int id : ids)
        {
            myWidget.updateAppWidget(context, appWidgetManager,id);
        }
    }






}
