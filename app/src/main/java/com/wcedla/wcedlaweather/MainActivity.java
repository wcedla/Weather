package com.wcedla.wcedlaweather;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Constraints;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Visibility;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationSet;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.wcedla.wcedlaweather.adapter.WeatherPagerAdapter;
import com.wcedla.wcedlaweather.db.CityListTable;
import com.wcedla.wcedlaweather.db.ProvinceTable;
import com.wcedla.wcedlaweather.db.WeatherBasicTable;
import com.wcedla.wcedlaweather.db.WeatherForecastTable;
import com.wcedla.wcedlaweather.db.WeatherHourlyTable;
import com.wcedla.wcedlaweather.db.WeatherLifeStyleTable;
import com.wcedla.wcedlaweather.db.WeatherNowTable;
import com.wcedla.wcedlaweather.db.WeatherUpdateTable;
import com.wcedla.wcedlaweather.gson.WeatherBasic;
import com.wcedla.wcedlaweather.gson.WeatherForecast;
import com.wcedla.wcedlaweather.gson.WeatherGson;
import com.wcedla.wcedlaweather.gson.WeatherLifeStyle;
import com.wcedla.wcedlaweather.gson.WeatherNow;
import com.wcedla.wcedlaweather.gson.WeatherUpdate;
import com.wcedla.wcedlaweather.service.WeatherUpdateService;
import com.wcedla.wcedlaweather.tool.HttpTool;
import com.wcedla.wcedlaweather.tool.JsonTool;
import com.wcedla.wcedlaweather.tool.SystemTool;
import com.wcedla.wcedlaweather.view.DayLine;
import com.wcedla.wcedlaweather.view.HourlyForcast;
import com.wcedla.wcedlaweather.view.SunCustomView;
import com.wcedla.wcedlaweather.view.SwitchButton;
import com.wcedla.wcedlaweather.view.TemperatureCurve;
import com.wcedla.wcedlaweather.view.WindMill;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static org.litepal.LitePalBase.TAG;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    LinearLayout weatherDetial;
    //SwipeRefreshLayout swipeRefreshLayout;
    NestedScrollView scrollView;
    String cityname;
    TextView tbCityName;
    TextView tbTemperature;
    TextView tbWeatherText;
    ViewPager viewPager;
    WeatherPagerAdapter weatherPagerAdapter;
//    TextView updateTimeText;
//    TextView temperatureText;
//    ImageView weatherIcon;
//    TextView weatherText;
//    TextView bodyTemperature;
//    TextView humidity;
//    TextView windInfo;
//    TextView randomWeatherInfo;
//    TextView hourlyTitleText;
//    TextView windText;
//    TextView windLevel;
//    TextView humidityText;

//    SunCustomView sunCustomView;
//    WindMill windMillbig;
//    WindMill windMillsmall;
//    TemperatureCurve temperatureCurveMax;
//    TemperatureCurve temperatureCurveMin;
//    DayLine dayLine;
//    HourlyForcast hourlyForcas;
//    LinearLayout windView;
//   // LinearLayout sunlayout;
//    LinearLayout lifeStyleLayout;
//    LinearLayout dataProvider;
    NavigationView navigationView;

    List<WeatherBasicTable> weatherBasicTableList;
    List<WeatherUpdateTable> weatherUpdateTableList;
    List<WeatherNowTable> weatherNowTableList;
    List<WeatherForecastTable> weatherForecastTableList;
    List<WeatherHourlyTable> weatherHourlyTableList;
    List<WeatherLifeStyleTable> weatherLifeStyleTableList;
    List<Fragment> fragmentList=new ArrayList<>();
    List<CityListTable> cityListTableList;

//    AnimatorSet animatorSetGo;
//    AnimatorSet animatorSetCancel;
//    Boolean needShow = true;
//    Boolean sunShow = true;

    Intent intent;

    WeatherUpdateService.WeatherBinder weatherBinder;

    ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            weatherBinder=(WeatherUpdateService.WeatherBinder)iBinder;
            weatherBinder.SetNotification();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "服务绑定断开");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SystemTool.setNavigationBarStatusBarTranslucent(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            cityname = bundle.getString("cityname");
        }
        //SystemTool.isServiceRunning(this,"com.wcedla.wcedlaweather.service.WeatherUpdateService");
        intent=new Intent(this,WeatherUpdateService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(!SystemTool.isServiceRunning(this,"com.wcedla.wcedlaweather.service.WeatherUpdateService")) {
                startForegroundService(intent);

            }
        }
        else
        {
            if(!SystemTool.isServiceRunning(this,"com.wcedla.wcedlaweather.service.WeatherUpdateService"))
                startService(intent);
        }
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);

        tbCityName = findViewById(R.id.tbcityname);
        tbCityName.setText(cityname);
        tbTemperature = findViewById(R.id.tbtemperature);
        tbWeatherText = findViewById(R.id.tbweathertext);

        drawerLayout = findViewById(R.id.drawerlayout);
        weatherDetial = findViewById(R.id.weatherdetial);
        navigationView = findViewById(R.id.nav_view);
        viewPager=findViewById(R.id.viewpager);
        cityListTableList=LitePal.findAll(CityListTable.class);
        int cityCount=cityListTableList.size();
        for(int i=0;i<cityCount;i++)
        {
            fragmentList.add(new WeatherInfo().newInstance(cityListTableList.get(i).getCityName()));
        }
        weatherPagerAdapter=new WeatherPagerAdapter(getSupportFragmentManager(),fragmentList);
        viewPager.setAdapter(weatherPagerAdapter);
        Log.d(TAG, "索引"+getCityNameIndex());
        viewPager.setCurrentItem(getCityNameIndex());
        viewPager.setOffscreenPageLimit(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1)
            {

                //Log.d(TAG, "滑动变化a"+i+","+v+","+i1+","+viewPager.getCurrentItem());
            }

            @Override
            public void onPageSelected(int i) {
                Log.d(TAG, "滑动变化b"+i+(WeatherInfo)(fragmentList.get(0)));
                tbCityName.setText(cityListTableList.get(i).getCityName());
                weatherNowTableList=LitePal.where("cityName=?", cityListTableList.get(i).getCityName()).find(WeatherNowTable.class);
                if(weatherNowTableList.size()>0&&tbWeatherText!=null&&tbTemperature!=null)
                {
                    tbWeatherText.setText(weatherNowTableList.get(0).getWeatherText());
                    tbTemperature.setText(weatherNowTableList.get(0).getTemperature()+"°");
                }

                SharedPreferences.Editor editor = getSharedPreferences("cityselect", MODE_PRIVATE).edit();
                editor.putString("cityname", cityListTableList.get(i).getCityName());
                editor.apply();



                weatherUpdateTableList=LitePal.where("cityName=?", cityListTableList.get(i).getCityName()).find(WeatherUpdateTable.class);
//
//                Intent intent=new Intent(MainActivity.this,WeatherUpdateService.class);
//                startService(intent);

                if(weatherNowTableList.size()>0)
                {
                    if (SystemTool.timeDifference(weatherUpdateTableList.get(0).getUpdateTime(), "hour") > 3 || SystemTool.timeDifference(weatherUpdateTableList.get(0).getUpdateTime(), "day") > 0) {
                        WeatherInfo wf = (WeatherInfo) fragmentList.get(i);
                        wf.doRefresh();
                    }
                }
                weatherBinder.SetNotification();
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                //Log.d(TAG, "滑动变化c"+i);
            }
        });
//        weatherNowTableList=LitePal.where("cityName=?", cityname).find(WeatherNowTable.class);
//        Log.d(TAG, "主界面判断frame布局加载完成没有"+cityname+","+weatherNowTableList.size());
//        if(weatherNowTableList.size()>0)
//        {
//            weatherUpdateTableList=LitePal.where("cityName=?", cityname).find(WeatherUpdateTable.class);
//            if(SystemTool.timeDifference(weatherUpdateTableList.get(0).getUpdateTime(),"hour")>3||
//                    SystemTool.timeDifference(weatherUpdateTableList.get(0).getUpdateTime(),"day")>0)
//            {
//                WeatherInfo wf=(WeatherInfo) fragmentList.get(getCityNameIndex());
//                wf.doRefresh();
//            }
//        }


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_city_manage:
                        goToCityManage();
                        break;
                    case R.id.nav_theme:
                        break;
                    case R.id.nav_update:
                        break;
                    case R.id.nav_setting:
                        Intent intent=new Intent(MainActivity.this,WeatherSetting.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_about:
                        break;
                }
                return true;
            }
        });


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setContentInsetsRelative(toolbar.getContentInsetStartWithNavigation(), toolbar.getContentInsetStartWithNavigation());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        RequestOptions options = new RequestOptions()
                .override(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels)
                //.centerCrop()
                .transform(new MultiTransformation(new BlurTransformation(40,5),new CenterCrop()))
                ;
        Glide.with(this)
                .load(R.drawable.background)
                .apply(options)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, Transition<? super Drawable> transition) {
                        weatherDetial.setBackground(resource);
                    }
                });

        Toast.makeText(this, cityname, Toast.LENGTH_SHORT).show();





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;

            case R.id.setting:
                Toast.makeText(this,"点击了设置选项",Toast.LENGTH_SHORT).show();
                break;

            case R.id.share:
                Toast.makeText(this,"点击了分享选项",Toast.LENGTH_SHORT).show();
                break;

            case R.id.exit:
                Toast.makeText(this,"点击了退出选项",Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }


    private int getCityNameIndex()
    {
        for(int i=0;i<cityListTableList.size();i++)
        {
            if(cityListTableList.get(i).getCityName().equals(cityname))
            {
                return i;
            }
        }
        return -1;
    }

    private void goToCityManage() {
        drawerLayout.closeDrawer(GravityCompat.START);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("wcedlalog", "从主界面进入到城市管理界面，设置where为main");
                Intent intent = new Intent(MainActivity.this, CityManage.class);
                Bundle bundle = new Bundle();
                bundle.putString("where", "Main");
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        }).start();


    }

    public void setNotification()
    {
        weatherBinder.SetNotification();
    }


    @Override
    protected void onDestroy()
    {
        unbindService(serviceConnection);
        super.onDestroy();
    }

}
