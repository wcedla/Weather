package com.wcedla.wcedlaweather;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.wcedla.wcedlaweather.db.WeatherBasicTable;
import com.wcedla.wcedlaweather.db.WeatherForecastTable;
import com.wcedla.wcedlaweather.db.WeatherHourlyTable;
import com.wcedla.wcedlaweather.db.WeatherLifeStyleTable;
import com.wcedla.wcedlaweather.db.WeatherNowTable;
import com.wcedla.wcedlaweather.db.WeatherUpdateTable;
import com.wcedla.wcedlaweather.tool.HttpTool;
import com.wcedla.wcedlaweather.tool.JsonTool;
import com.wcedla.wcedlaweather.tool.SystemTool;
import com.wcedla.wcedlaweather.view.DayLine;
import com.wcedla.wcedlaweather.view.HourlyForcast;
import com.wcedla.wcedlaweather.view.SunCustomView;
import com.wcedla.wcedlaweather.view.TemperatureCurve;
import com.wcedla.wcedlaweather.view.WindMill;

import org.litepal.LitePal;
import org.litepal.LitePalBase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.support.constraint.Constraints.TAG;

public class WeatherInfo extends Fragment {

    String cityName;
    Activity myActivity;
    View view;

    SwipeRefreshLayout swipeRefreshLayout;
    NestedScrollView scrollView;

    TextView tbCityName;
    TextView tbTemperature;
    TextView tbWeatherText;
    TextView updateTimeText;
    TextView temperatureText;
    ImageView weatherIcon;
    TextView weatherText;
    TextView bodyTemperature;
    TextView randomWeatherInfo;
    TextView hourlyTitleText;
    TextView windText;
    TextView windLevel;
    TextView humidityText;

    SunCustomView sunCustomView;
    WindMill windMillbig;
    WindMill windMillsmall;
    TemperatureCurve temperatureCurveMax;
    TemperatureCurve temperatureCurveMin;
    DayLine dayLine;
    HourlyForcast hourlyForcas;
    LinearLayout windView;
    LinearLayout sunlayout;
    LinearLayout lifeStyleLayout;
    LinearLayout dataProvider;

    AnimatorSet animatorSetCancel;
    AnimatorSet animatorSetGo;
    Boolean needShow = true;
    Boolean sunShow = true;
    int refreshTimes=0;

    List<WeatherBasicTable> weatherBasicTableList;
    List<WeatherUpdateTable> weatherUpdateTableList;
    List<WeatherNowTable> weatherNowTableList;
    List<WeatherForecastTable> weatherForecastTableList;
    List<WeatherHourlyTable> weatherHourlyTableList;
    List<WeatherLifeStyleTable> weatherLifeStyleTableList;

    boolean needRefresh;


    @Override
    public void onAttach(Context context) {

        myActivity = (Activity) context;
        cityName = getArguments().getString("cityName");  //获取参数
        //mainActivity=(MainActivity)myActivity;
        needRefresh=true;
        super.onAttach(context);
    }

    public static WeatherInfo newInstance(String str) {
        WeatherInfo weatherInfo = new WeatherInfo();
        Bundle bundle = new Bundle();
        bundle.putString("cityName", str);
        weatherInfo.setArguments(bundle);   //设置参数
        return weatherInfo;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //cityName="武夷山";
        //Log.d("wcedlalog", "有没有: "+cityName);
        tbCityName=myActivity.findViewById(R.id.tbcityname);
        tbTemperature=myActivity.findViewById(R.id.tbtemperature);
        tbWeatherText=myActivity.findViewById(R.id.tbweathertext);

        view=getLayoutInflater().inflate(R.layout.weather_info,container,false);
        swipeRefreshLayout = view.findViewById(R.id.refresh);
        scrollView = view.findViewById(R.id.scrollview);
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView nestedScrollView, int i, int i1, int i2, int i3) {

                if (i1 > 590) {
                    if (weatherText != null) {
                        if (needShow) {
                            if (animatorSetCancel != null)
                                stopAnimatorCancel();
                            tbWeatherText.setText(weatherText.getText());
                            tbTemperature.setText(temperatureText.getText());
                            startAnimatorGo();
                            needShow = false;
                        }
                    }
                    if (i1 > 2110) {
                        if (sunCustomView != null) {
                            if (sunShow) {
                                sunCustomView.setTime(weatherForecastTableList.get(0).getSunRaise(), weatherForecastTableList.get(0).getSunSet());
                                sunShow = false;
                            }
                        }

                    } else if (i1 < 2110) {
                        if (sunCustomView != null) {
                            if (!sunShow) {
                                sunCustomView.resetSun();
                                sunShow = true;
                            }
                        }
                    }

                } else if (i1 < 590 && !needShow) {
                    if (animatorSetGo != null)
                        stopAnimatorGo();
                    startAnimatorCancel();
                    needShow = true;

                }
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });

        List<WeatherBasicTable> weatherBasicTables = LitePal.where("cityName=?", cityName).find(WeatherBasicTable.class);
        if (weatherBasicTables.size() > 0) {
            showWeatherInfo();
        } else {

            saveWeatherInfo();
        }

//

        return view;
    }

    public void doRefresh()
    {
        if(refreshTimes<1) {
            refreshTimes+=1;
            // Log.d(TAG, "空"+getActivity());
            myActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    saveWeatherInfo();
                }
            });
        }
    }

//    public void test()
//    {
//        myActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                swipeRefreshLayout.setRefreshing(true);
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
////                weatherUpdateTableList = LitePal.where("cityName=?", cityName).find(WeatherUpdateTable.class);
////                if(weatherUpdateTableList.get(0).getUpdateTime().equals(updateTimeText));
//                    showWeatherInfo();
//                swipeRefreshLayout.setRefreshing(false);
//
//            }
//        });
//
//    }

    private void saveWeatherInfo()
    {
        Log.d(TAG, "联网获取城市名为"+cityName);
        swipeRefreshLayout.setRefreshing(true);
        String url = "https://free-api.heweather.com/s6/weather?key=c864606856d54eedb9f63a6cc0edd91f&location=" + Uri.encode(cityName);
        HttpTool.doHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                myActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responsedata = null;
                if (response.body() != null) {
                    responsedata = response.body().string();

                }
                Boolean result = JsonTool.dealWeatherJson(responsedata, cityName);
                if (result) {
                    Message saveOk = new Message();
                    saveOk.what = 1;
                    myHandler.sendMessage(saveOk);
                }
            }
        });
//        }
    }

    @SuppressLint( "HandlerLeak" )
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    refreshTimes=0;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            myActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MainActivity mainActivity=(MainActivity)myActivity;
                                    mainActivity.setNotification();
                                    showWeatherInfo();
                                    swipeRefreshLayout.setRefreshing(false);
                                    ((MainActivity) myActivity).weatherBinder.refreshAlarmManger();
                                }
                            });

                        }
                    }).start();
                    break;

            }
        }
    };

    private void showWeatherInfo() {
        weatherBasicTableList = LitePal.where("cityName=?", cityName).find(WeatherBasicTable.class);
        weatherUpdateTableList = LitePal.where("cityName=?", cityName).find(WeatherUpdateTable.class);
        weatherNowTableList = LitePal.where("cityName=?", cityName).find(WeatherNowTable.class);
        weatherForecastTableList = LitePal.where("cityName=?", cityName).find(WeatherForecastTable.class);
        weatherHourlyTableList = LitePal.where("cityName=?", cityName).find(WeatherHourlyTable.class);
        weatherLifeStyleTableList = LitePal.where("cityName=?", cityName).find(WeatherLifeStyleTable.class);
//            Log.d(TAG,
//                    "查错" + "basic:" + weatherBasicTableList.size()
//                            + ",update:" + weatherUpdateTableList.get(0).getUpdateTime()
//                            + ",now:" + weatherNowTableList.get(0).getWeatherText()
//                            + ",forecast:" + weatherForecastTableList.get(0).getSunRaise()
//                            + ",lifestyle:" + weatherLifeStyleTableList.get(0).getLifeStyleText());

        if (weatherBasicTableList.size() > 0 && weatherUpdateTableList.size() > 0 &&
                weatherNowTableList.size() > 0 && weatherForecastTableList.size() > 0 &&
                weatherLifeStyleTableList.size() > 0&&weatherHourlyTableList.size()>0) {
            temperatureText = view.findViewById(R.id.temperaturetext);
            weatherIcon = view.findViewById(R.id.weathericon);
            weatherText = view.findViewById(R.id.weathertext);
            updateTimeText = view.findViewById(R.id.updatetime);
            bodyTemperature = view.findViewById(R.id.bodytemperature);
            randomWeatherInfo = view.findViewById(R.id.randomweatherinfo);
            temperatureCurveMax = view.findViewById(R.id.temperaturecurvemax);
            temperatureCurveMin = view.findViewById(R.id.temperaturecurvemin);
            dayLine = view.findViewById(R.id.dayline);
            hourlyForcas = view.findViewById(R.id.hourlyview);
            hourlyTitleText = view.findViewById(R.id.hourlytitletext);
            windMillbig = view.findViewById(R.id.windnillbig);
            windMillsmall = view.findViewById(R.id.windnillsmall);
            windView = view.findViewById(R.id.windview);
            windText = view.findViewById(R.id.windtext);
            windLevel = view.findViewById(R.id.windleveltext);
            humidityText = view.findViewById(R.id.humiditytext);
            sunlayout = view.findViewById(R.id.sunlayout);
            sunCustomView = view.findViewById(R.id.sunview);
            lifeStyleLayout = view.findViewById(R.id.lifestylelayout);
            dataProvider = view.findViewById(R.id.dataprovider);
            temperatureText.setText(weatherNowTableList.get(0).getTemperature() + "°");
            weatherIcon.setImageResource(SystemTool.getResourceByReflect("weather_" + weatherNowTableList.get(0).getIconId()));
            weatherText.setText(weatherNowTableList.get(0).getWeatherText());
            updateTimeText.setText(weatherUpdateTableList.get(0).getUpdateTime().split(" ")[1] + " 更新");
            bodyTemperature.setText("体感温度" + weatherNowTableList.get(0).getBodyTemperature() + "°");
            if (weatherText.getText().toString().contains("晴") ||
                    weatherText.getText().toString().contains("云") ||
                    weatherText.getText().toString().contains("阴") ||
                    weatherText.getText().toString().contains("风") ||
                    weatherText.getText().toString().contains("雾") ||
                    weatherText.getText().toString().contains("尘") ||
                    weatherText.getText().toString().contains("霾"))
                randomWeatherInfo.setText("能见度" + weatherNowTableList.get(0).getCouldSee() + "公里");
            else
                randomWeatherInfo.setText("降水量" + weatherNowTableList.get(0).getRainCount() + "毫米");

            List<String> temparatureMaxList = new ArrayList<>();
            List<String> temperatureMinList = new ArrayList<>();
            //Log.d(TAG, "frame界面查看温度曲线数据数量"+weatherForecastTableList.size());
            for (int i = 0; i < weatherForecastTableList.size(); i++) {
                temparatureMaxList.add(weatherForecastTableList.get(i).getMaxTemperature());
                temperatureMinList.add(weatherForecastTableList.get(i).getMinTemperature());
            }

            temperatureCurveMax.setData(temparatureMaxList, "up");
            temperatureCurveMin.setData(temperatureMinList, "down");
            dayLine.setData(weatherForecastTableList);
            hourlyForcas.setData(weatherHourlyTableList);
            hourlyTitleText.setText("逐三小时天气预报");
            windMillsmall.startWindmill();
            windMillbig.startWindmill();
            windText.setText(weatherNowTableList.get(0).getWindDirection());
            windLevel.setText(weatherNowTableList.get(0).getWindLevel() + "级");
            humidityText.setText(weatherNowTableList.get(0).getHumidity() + "%");
            windView.setVisibility(View.VISIBLE);
//            sunCustomView.setTime(weatherForecastTableList.get(0).getSunRaise(),weatherForecastTableList.get(0).getSunSet());
            sunlayout.setVisibility(View.VISIBLE);

            int drawableId;
            String lifestyleText;
            if(lifeStyleLayout.getChildCount()>1)
            {
                lifeStyleLayout.removeViews(1,lifeStyleLayout.getChildCount()-1);
            }
            for (int i = 0; i < weatherLifeStyleTableList.size(); i++) {
                drawableId = getDrawableResource(weatherLifeStyleTableList.get(i).getLifeStyleType());
                lifestyleText = getTypeText(weatherLifeStyleTableList.get(i).getLifeStyleType());
                if (drawableId == -1) {
                    continue;
                }
                if (lifestyleText.equals("")) {
                    continue;
                }

                View view = LayoutInflater.from(myActivity).inflate(R.layout.lifestyle_item, lifeStyleLayout, false);
                ImageView imageView = view.findViewById(R.id.lifestyleicon);
                TextView textView1 = view.findViewById(R.id.lifestyleshorttext);
                TextView textView2 = view.findViewById(R.id.lifestyletext);
                View bottomLine = view.findViewById(R.id.bottomline);
                View bottomLine2 = view.findViewById(R.id.bottomlinefull);
                imageView.setImageResource(drawableId);
                textView1.setText(lifestyleText + " " + weatherLifeStyleTableList.get(i).getShortText());
                textView2.setText(weatherLifeStyleTableList.get(i).getLifeStyleText());
                if (i == weatherLifeStyleTableList.size() - 2) {
                    bottomLine.setVisibility(View.GONE);
                    bottomLine2.setVisibility(View.VISIBLE);
                }
                lifeStyleLayout.addView(view);
            }
            lifeStyleLayout.setVisibility(View.VISIBLE);
            dataProvider.setVisibility(View.VISIBLE);
            //Log.d(TAG, "变量输出"+refreshTimes);
            //refreshtime主要是控制只有在第一次打开主界面并且上次刷新时间大于三小时前才会触发，不然不控制第一次的话会一直无限刷新
            Log.d(TAG, "变量判断"+needRefresh);
            if(needRefresh&&
                    (SystemTool.timeDifference(weatherUpdateTableList.get(0).getUpdateTime(), "hour") > 3||
                    SystemTool.timeDifference(weatherUpdateTableList.get(0).getUpdateTime(), "day")>0))
            {
                needRefresh=false;
                Log.d(TAG, "frame三小时为刷新且为刚打开界面"+cityName);
                doRefresh();
            }


        }
    }

    /*获取文本宽度和高度*/
    private int getTextSize(String text, String type) {
        Rect rect = new Rect();
        tbCityName.getPaint().getTextBounds(text, 0, text.length(), rect);//通过构造能容纳下文本长度的矩形，获得文本的宽度高度
        int width = rect.width();
        int height = rect.height();
        if (type.equals("width")) {
            return width;
        } else if (type.equals("height")) {
            return height;
        }
        return 0;
    }

    private int getDrawableResource(String str) {
        if (str.equals("comf")) {
            return R.drawable.ssd;
        } else if (str.equals("drsg")) {
            return R.drawable.cyzs;
        } else if (str.equals("flu")) {
            return R.drawable.gmzs;

        } else if (str.equals("sport")) {
            return R.drawable.ydzs;

        } else if (str.equals("uv")) {
            return R.drawable.zwx;

        } else if (str.equals("cw")) {
            return R.drawable.xczs;
        } else {
            return -1;
        }

    }

    private String getTypeText(String str) {
        if (str.equals("comf")) {
            return "舒适度";
        } else if (str.equals("drsg")) {
            return "穿衣指数";
        } else if (str.equals("flu")) {
            return "感冒指数";

        } else if (str.equals("sport")) {
            return "运动指数";

        } else if (str.equals("uv")) {
            return "紫外线强度";

        } else if (str.equals("cw")) {
            return "洗车指数";
        } else {
            return "";
        }

    }

    private void startAnimatorGo() {

        animatorSetGo = new AnimatorSet();
        float myDenisty = getResources().getDisplayMetrics().density;
        float translateDistance = -(20 * myDenisty + getTextSize(cityName, "width") / 2 + getTextSize(tbWeatherText.getText().toString(), "width") / 2);
        ObjectAnimator translate = ObjectAnimator.ofFloat(tbCityName, "translationX", tbCityName.getTranslationX(), translateDistance);
        final ObjectAnimator alpha1 = ObjectAnimator.ofFloat(tbWeatherText, "alpha", 0f, 1f);
        final ObjectAnimator alpha2 = ObjectAnimator.ofFloat(tbTemperature, "alpha", 0f, 1f);
        alpha1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if ((float) alpha1.getAnimatedValue() > 0.01) {
                    tbWeatherText.setVisibility(View.VISIBLE);
                }

            }
        });
        alpha2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if ((float) alpha2.getAnimatedValue() > 0.01) {
                    tbTemperature.setVisibility(View.VISIBLE);
                }
            }
        });
        animatorSetGo.play(alpha1).with(alpha2).after(translate);
        animatorSetGo.setDuration(500);
        animatorSetGo.start();
    }

    private void stopAnimatorGo() {
        animatorSetGo.cancel();
    }

    private void startAnimatorCancel() {
        animatorSetCancel = new AnimatorSet();
        ObjectAnimator translate = ObjectAnimator.ofFloat(tbCityName, "translationX", tbCityName.getTranslationX(), 0f);
        ObjectAnimator alpha1 = ObjectAnimator.ofFloat(tbWeatherText, "alpha", 1f, 0f);
        ObjectAnimator alpha2 = ObjectAnimator.ofFloat(tbTemperature, "alpha", 1f, 0f);

        alpha1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if ((float) valueAnimator.getAnimatedValue() == 0f) {
                    tbWeatherText.setVisibility(View.INVISIBLE);
                }

            }
        });
        alpha2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if ((float) valueAnimator.getAnimatedValue() == 0f) {
                    tbTemperature.setVisibility(View.INVISIBLE);
                }
            }
        });
        animatorSetCancel.play(alpha1).with(alpha2).before(translate);
        animatorSetCancel.setDuration(500);
        animatorSetCancel.start();
    }

    private void stopAnimatorCancel() {
        animatorSetCancel.cancel();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "frame终结"+cityName);
        super.onDestroy();
    }
}
