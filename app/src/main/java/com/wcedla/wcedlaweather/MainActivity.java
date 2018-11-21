package com.wcedla.wcedlaweather;

import android.Manifest;
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
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Constraints;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Visibility;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationSet;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
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
import com.wcedla.wcedlaweather.adapter.ThemeGridviewAdapter;
import com.wcedla.wcedlaweather.adapter.WeatherPagerAdapter;
import com.wcedla.wcedlaweather.db.CityListTable;
import com.wcedla.wcedlaweather.db.ProvinceTable;
import com.wcedla.wcedlaweather.db.VersionTable;
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
import com.wcedla.wcedlaweather.service.DownloadService;
import com.wcedla.wcedlaweather.service.WeatherUpdateService;
import com.wcedla.wcedlaweather.tool.BaseActivity;
import com.wcedla.wcedlaweather.tool.HttpTool;
import com.wcedla.wcedlaweather.tool.JsonTool;
import com.wcedla.wcedlaweather.tool.SystemTool;
import com.wcedla.wcedlaweather.view.DayLine;
import com.wcedla.wcedlaweather.view.HourlyForcast;
import com.wcedla.wcedlaweather.view.MyPopWindow;
import com.wcedla.wcedlaweather.view.SunCustomView;
import com.wcedla.wcedlaweather.view.SwitchButton;
import com.wcedla.wcedlaweather.view.TemperatureCurve;
import com.wcedla.wcedlaweather.view.WindMill;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static org.litepal.LitePalBase.TAG;

public class MainActivity extends BaseActivity {

    DrawerLayout drawerLayout;
    LinearLayout weatherDetial;
    NestedScrollView scrollView;
    String cityname;
    TextView tbCityName;
    TextView tbTemperature;
    TextView tbWeatherText;
    ViewPager viewPager;
    WeatherPagerAdapter weatherPagerAdapter;
    NavigationView navigationView;

    List<WeatherBasicTable> weatherBasicTableList;
    List<WeatherUpdateTable> weatherUpdateTableList;
    List<WeatherNowTable> weatherNowTableList;
    List<WeatherForecastTable> weatherForecastTableList;
    List<WeatherHourlyTable> weatherHourlyTableList;
    List<WeatherLifeStyleTable> weatherLifeStyleTableList;
    List<Fragment> fragmentList = new ArrayList<>();
    List<CityListTable> cityListTableList;

    Intent intent;
    DisplayMetrics displayMetrics;
    int displayWidth, displayHeight;
    float denisty;

    WeatherUpdateService.WeatherBinder weatherBinder;
    boolean updateChecking=false;

    File file;
    List<VersionTable> versionTableList;

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            weatherBinder = (WeatherUpdateService.WeatherBinder) iBinder;
            weatherBinder.SetNotification();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "服务绑定断开");
        }
    };

    DownloadService.DownloadBinder downloadBinder;

    ServiceConnection downloadConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
            downloadBinder.setFileName(versionTableList.get(0).getFileName());
            downloadBinder.startDownload(versionTableList.get(0).getDownloadUrl());
            downloadBinder.setHandler(myHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayMetrics = getResources().getDisplayMetrics();
        displayWidth = displayMetrics.widthPixels;
        displayHeight = displayMetrics.heightPixels;
        denisty = displayMetrics.density;

        SystemTool.setNavigationBarStatusBarTranslucent(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            cityname = bundle.getString("cityname");
        }


        intent = new Intent(this, WeatherUpdateService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!SystemTool.isServiceRunning(this, "com.wcedla.wcedlaweather.service.WeatherUpdateService")) {
                startForegroundService(intent);


            }
        } else {
            if (!SystemTool.isServiceRunning(this, "com.wcedla.wcedlaweather.service.WeatherUpdateService"))
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
        viewPager = findViewById(R.id.viewpager);
        cityListTableList = LitePal.findAll(CityListTable.class);
        int cityCount = cityListTableList.size();
        for (int i = 0; i < cityCount; i++) {
            fragmentList.add(new WeatherInfo().newInstance(cityListTableList.get(i).getCityName()));
        }
        weatherPagerAdapter = new WeatherPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(weatherPagerAdapter);
        Log.d(TAG, "索引" + getCityNameIndex());
        viewPager.setCurrentItem(getCityNameIndex());
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                Log.d(TAG, "滑动变化b" + i + (WeatherInfo) (fragmentList.get(0)));
                tbCityName.setText(cityListTableList.get(i).getCityName());
                weatherNowTableList = LitePal.where("cityName=?", cityListTableList.get(i).getCityName()).find(WeatherNowTable.class);
                if (weatherNowTableList.size() > 0 && tbWeatherText != null && tbTemperature != null) {
                    tbWeatherText.setText(weatherNowTableList.get(0).getWeatherText());
                    tbTemperature.setText(weatherNowTableList.get(0).getTemperature() + "°");
                }

                SharedPreferences.Editor editor = getSharedPreferences("cityselect", MODE_PRIVATE).edit();
                editor.putString("cityname", cityListTableList.get(i).getCityName());
                editor.apply();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                      SystemTool.updateWidgetForActivity(MainActivity.this);
                    }
                }).start();


                weatherUpdateTableList = LitePal.where("cityName=?", cityListTableList.get(i).getCityName()).find(WeatherUpdateTable.class);

                if (weatherNowTableList.size() > 0) {
                    if (SystemTool.timeDifference(weatherUpdateTableList.get(0).getUpdateTime(), "hour") > 3 || SystemTool.timeDifference(weatherUpdateTableList.get(0).getUpdateTime(), "day") > 0) {
                        WeatherInfo wf = (WeatherInfo) fragmentList.get(i);
                        wf.doRefresh();
                    }
                }
                weatherBinder.SetNotification();
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_city_manage:
                        goToCityManage();
                        break;
                    case R.id.nav_theme:


//
                        final Integer[] res = new Integer[]{R.drawable.theme_default, R.drawable.theme_red, R.drawable.theme_pink, R.drawable.theme_brown, R.drawable.theme_blue, R.drawable.theme_bluegrey, R.drawable.theme_yellow, R.drawable.theme_deeppurple, R.drawable.theme_green, R.drawable.theme_deeporange, R.drawable.theme_grey, R.drawable.theme_cyan, R.drawable.theme_amber};
                        List<Integer> list = Arrays.asList(res);
                        ThemeGridviewAdapter adapter = new ThemeGridviewAdapter(MainActivity.this, list);
                        SharedPreferences settingXml = getSharedPreferences("color", MODE_PRIVATE);
                        int themePosition = settingXml.getInt("themePosition", 0);
                        adapter.setCheckItem(themePosition);
                        View gridListView = getLayoutInflater().inflate(R.layout.theme_list_item, null);
                        GridView gridView = gridListView.findViewById(R.id.theme_item);
//                        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
//                        gridView.setCacheColorHint(0);
                        gridView.setAdapter(adapter);


                        drawerLayout.closeDrawer(GravityCompat.START);
                        final MyPopWindow popWindow = new MyPopWindow(gridListView, displayWidth - 80 * (int) denisty, WindowManager.LayoutParams.WRAP_CONTENT);
                        popWindow.showAtLocation(getLayoutInflater().inflate(R.layout.activity_main, null), Gravity.CENTER, 0, 0);

                        gridView.setOnItemClickListener(
                                new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        popWindow.dismiss();
                                        int themeId = getThemeName(position);
                                        SharedPreferences.Editor editor = getSharedPreferences("color", MODE_PRIVATE).edit();
                                        editor.putInt("changeTheme", themeId);
                                        editor.putInt("themePosition", position);
                                        editor.apply();
                                        Intent themeIntent = new Intent(MainActivity.this, CitySelectActivity.class);
                                        finish();
                                        startActivity(themeIntent);

                                        //Toast.makeText(MainActivity.this,String.valueOf(themeId),Toast.LENGTH_SHORT).show();
                                    }
                                }

                        );

                        break;
                    case R.id.nav_update:
                        if(!updateChecking)
                        {
                            drawerLayout.closeDrawer(GravityCompat.START);
                            updateChecking = true;
                            Toast.makeText(MainActivity.this, "正在检查更新...", Toast.LENGTH_SHORT).show();
                            String url = "https://wcedla.oss-cn-shanghai.aliyuncs.com/city_json/weatherversion.json";
                            HttpTool.doHttpRequest(url, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MainActivity.this, "版本检查失败", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String responseData = response.body().string();
                                    //Log.d(TAG, "获取到的原生json文本"+responseData);

                                    boolean result = JsonTool.dealVersionJson(responseData);
                                    if (result) {
                                        int versionCode = 0;
                                        List<VersionTable> versionTableList = LitePal.findAll(VersionTable.class);
                                        int newVersionCode = Integer.valueOf(versionTableList.get(0).getVersionCode());
                                        try {
                                            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                                            versionCode = packageInfo.versionCode;
                                            //Toast.makeText(MainActivity.this,String.valueOf(versionCode),Toast.LENGTH_SHORT).show();
                                        } catch (PackageManager.NameNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                        if (newVersionCode > versionCode) {
                                            Message message = new Message();
                                            message.what = 1;
                                            message.obj = versionTableList;
                                            myHandler.sendMessage(message);
                                        } else {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(MainActivity.this, "暂时没有更新！", Toast.LENGTH_LONG).show();
                                                    updateChecking=false;
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this,"程序已经在检查更新了，请不要连续点击更新！",Toast.LENGTH_SHORT).show();
                        }


                        break;
                    case R.id.nav_setting:
                        Intent intent = new Intent(MainActivity.this, WeatherSetting.class);
                        startActivity(intent);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_about:
                        Intent aboutintent = new Intent(MainActivity.this, AboutMe.class);
                        startActivity(aboutintent);
                        drawerLayout.closeDrawer(GravityCompat.START);
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
                .transform(new MultiTransformation(new BlurTransformation(40, 5), new CenterCrop()));
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

    String fileName;
    public MyHandler myHandler = new MyHandler(this);

    public static class MyHandler extends Handler {
        // WeakReference to the outer class's instance.
        private WeakReference<MainActivity> mOuter;

        public MyHandler(MainActivity activity) {
            mOuter = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final MainActivity outer = mOuter.get();
            if (outer != null) {
                switch (msg.what) {
                    case 1:
                        final Intent downloadIntent = new Intent(outer, DownloadService.class);


                        final List<VersionTable> versionTableList = (List<VersionTable>) msg.obj;
                        outer.versionTableList = versionTableList;
                        outer.fileName = versionTableList.get(0).getFileName();
                        Log.d(TAG, "收到消息,版本有更新！");
                        AlertDialog.Builder dialog = new AlertDialog.Builder(outer);
                        dialog.setTitle("发现新版本！");
                        dialog.setMessage("是否下载最新版本？");
                        dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    outer.startForegroundService(downloadIntent);
                                } else {
                                    outer.startService(downloadIntent);
                                }
                                outer.bindService(downloadIntent, outer.downloadConnection, BIND_AUTO_CREATE);

//                                    outer.downloadBinder.setFileName(versionTableList.get(0).getFileName());
//                                    outer.downloadBinder.startDownload(versionTableList.get(0).getDownloadUrl());
//                                    outer.downloadBinder.setHandler(outer.myHandler);

                                //Toast.makeText(outer, "点击了是").show();
                            }
                        });
                        dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                outer.updateChecking=false;
//                                outer.downloadBinder.stopService();
//                                outer.unbindService(outer.downloadConnection);
                                //Toast.makeText(outer, "点击了否", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.show();
                        break;
                    case 2:
                        outer.file = (File) msg.obj;
                        outer.checkInstallPermission();
                        outer.downloadBinder.stopService();
                        outer.unbindService(outer.downloadConnection);
                        outer.updateChecking=false;
                        //Toast.makeText(outer, "有用啊", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
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
                Intent intent = new Intent(MainActivity.this, WeatherSetting.class);
                startActivity(intent);
                Toast.makeText(this, "点击了设置选项", Toast.LENGTH_SHORT).show();
                break;

            case R.id.share:
                //Toast.makeText(this,"点击了分享选项",Toast.LENGTH_SHORT).show();
                break;

            case R.id.exit:
                Toast.makeText(this, "点击了退出选项", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
        return true;
    }


    private int getCityNameIndex() {
        for (int i = 0; i < cityListTableList.size(); i++) {
            if (cityListTableList.get(i).getCityName().equals(cityname)) {
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

    public void setNotification() {
        weatherBinder.SetNotification();
    }


    private int getThemeName(int position) {
        switch (position) {
            case 0:
                return R.style.AppTheme;
            case 1:
                return R.style.RedTheme;
            case 2:
                return R.style.PinkTheme;
            case 3:
                return R.style.BrownTheme;
            case 4:
                return R.style.BlueTheme;
            case 5:
                return R.style.BlueGreyTheme;
            case 6:
                return R.style.YellowTheme;
            case 7:
                return R.style.DeepPurpleTheme;
            case 8:
                return R.style.GreenTheme;
            case 9:
                return R.style.DeepOrangeTheme;
            case 10:
                return R.style.GreyTheme;
            case 11:
                return R.style.CyanTheme;
            case 12:
                return R.style.AmberTheme;
            default:
                return R.style.AppTheme;

        }
    }


    public void checkInstallPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            boolean permit = getPackageManager().canRequestPackageInstalls();
            if (!permit) {
                //请求安装未知应用来源的权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, 11);
            } else {
                installApk();
            }

        } else {
            installApk();
        }
    }

    public void installApk() {

        Uri uri;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // "net.csdn.blog.ruancoder.fileprovider"即是在清单文件中配置的authorities
            uri = FileProvider.getUriForFile(this, "com.wcedla.wcedlaweather.fileprovider", file);
            // 给目标应用一个临时授权

        } else {
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 11:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    installApk();
                } else {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                    startActivityForResult(intent, 15);
                }
                break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        checkInstallPermission();
    }


    @Override
    protected void onDestroy() {
        if (SystemTool.isServiceRunning(this, "com.wcedla.wcedlaweather.service.WeatherUpdateService")) {
            unbindService(serviceConnection);
        }
        if (SystemTool.isServiceRunning(this, "com.wcedla.wcedlaweather.service.DownloadService")) {
            downloadBinder.stopService();
            unbindService(downloadConnection);
        }

        super.onDestroy();
    }




}
