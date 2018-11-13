package com.wcedla.wcedlaweather;

import android.app.Instrumentation;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wcedla.wcedlaweather.adapter.CityManageAdapter;
import com.wcedla.wcedlaweather.adapter.CityManageList;
import com.wcedla.wcedlaweather.db.CityListTable;
import com.wcedla.wcedlaweather.db.WeatherBasicTable;
import com.wcedla.wcedlaweather.db.WeatherForecastTable;
import com.wcedla.wcedlaweather.db.WeatherHourlyTable;
import com.wcedla.wcedlaweather.db.WeatherLifeStyleTable;
import com.wcedla.wcedlaweather.db.WeatherNowTable;
import com.wcedla.wcedlaweather.db.WeatherUpdateTable;
import com.wcedla.wcedlaweather.tool.BaseActivity;
import com.wcedla.wcedlaweather.tool.SystemTool;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class CityManage extends BaseActivity {

    String where="";
    ListView cityManageListview;
    ImageView addCity;
    Button cityManageBack;
    CityManageAdapter adapter = null;
    List<CityManageList> cityManageLists = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
        where = bundle.getString("where");//记住是从哪个界面跳转而来
        setContentView(R.layout.activity_city_manage);
        SystemTool.setNavigationBarStatusBarTranslucent(this);
        cityManageListview = findViewById(R.id.citymangelistview);
        addCity=findViewById(R.id.addcity);
        addCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("wcedlalog", "cityManageActivity跳转天气选择界面（添加城市按钮点击，置城市选择和城市管理界面有城市标志位false，为了能够屏蔽城市选择界面的跳转），当前跳转到城市管理的状态为"+where);
                    SharedPreferences.Editor editor = getSharedPreferences("cityselect", MODE_PRIVATE).edit();
                    editor.putBoolean("cityselect", false);
                    editor.putBoolean("havecity",false);
                    editor.apply();
                    Intent intent=new Intent(CityManage.this,CitySelectActivity.class);
                    startActivity(intent);
                    finish();
                }
        });
        cityManageBack=findViewById(R.id.citymanageback);
        cityManageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    public void run() {
                        try {
                            Instrumentation inst = new Instrumentation();
                            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                        } catch (Exception e) {
                            Log.d("wcedlalog", "状态栏返回键点击出错"+e.toString());
                        }
                    }
                }.start();
            }
        });

        final ListView listView = findViewById(R.id.citymangelistview);
        List<CityListTable> cityListTableList = LitePal.findAll(CityListTable.class);//读取城市管理表，得到已经添加的城市

        String cityname;
        String weathertext;
        String temperaturetext;
        //final CityManageAdapter adapter=null;

        for (int i = 0; i < cityListTableList.size(); i++) {
            cityname = cityListTableList.get(i).getCityName();
            List<WeatherNowTable> weatherNowTableList = LitePal.where("cityName=?", cityname).find(WeatherNowTable.class);
            if (weatherNowTableList.size() < 1) {
                weathertext = "N/A";
                temperaturetext = "N/A";

            } else if (weatherNowTableList.get(0).getWeatherText().equals("")) {
                weathertext = "N/A";
                temperaturetext = "N/A";
            } else {
                weathertext = weatherNowTableList.get(0).getWeatherText();
                temperaturetext = weatherNowTableList.get(0).getTemperature();
            }
            //Log.d("wcedlalog", "温度" + cityListTableList.get(i).getCityName() + "," + weathertext + "," + temperaturetext);
            CityManageList cityManageList = new CityManageList(cityname, weathertext, temperaturetext);
            cityManageLists.add(cityManageList);
        }
        adapter = new CityManageAdapter(this, R.layout.city_manage_item, cityManageLists, new myCallBack());
        listView.setAdapter(adapter);

    }

    class myCallBack implements Handler.Callback {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1://收到删除按钮点击的消息
                    String value = (String) message.obj;
                    String cityname = value.split("_")[0];
                    //首先根据删除的城市名，删除所有表中关于这个城市的信息
                    LitePal.deleteAll(CityListTable.class, "cityName=?", cityname);
                    LitePal.deleteAll(WeatherBasicTable.class, "cityName=?", cityname);
                    LitePal.deleteAll(WeatherUpdateTable.class, "cityName=?", cityname);
                    LitePal.deleteAll(WeatherNowTable.class, "cityName=?", cityname);
                    LitePal.deleteAll(WeatherForecastTable.class, "cityName=?", cityname);
                    LitePal.deleteAll(WeatherHourlyTable.class, "cityName=?", cityname);
                    LitePal.deleteAll(WeatherLifeStyleTable.class, "cityName=?", cityname);
                    int i = Integer.valueOf(value.split("_")[1]);
                    cityManageLists.remove(i);//从listview中删除这个城市
                    SharedPreferences sharedPreferences = getSharedPreferences("cityselect", MODE_PRIVATE);
                    //如果删除的是最后一个城市则删除一切存储在文件中的关于城市选择的数据，以便下次跳转到城市选择界面
                    if(cityManageLists.size()<1)
                    {
                        Log.d("wcedlalog", "把最后一个城市删除了，置空所有与城市选择有关的存在文件中的信息");
                        SharedPreferences.Editor editor = getSharedPreferences("cityselect", MODE_PRIVATE).edit();
                        editor.putBoolean("cityselect", false);
                        editor.putString("cityname","");
                        editor.putBoolean("havecity",false);
                        editor.apply();
                    }
                    else if(cityManageLists.size()>0&&sharedPreferences.getString("cityname","").equals(cityname))
                    {//删除了已经选择显示的城市，但是城市管理界面还有其他城市存在,置城市选择为已选择，城市名字为当前城市管理界面的第一个城市，以便等会按返回跳转到城市选择界面的时候直接跳转到天气界面
                        Log.d("wcedlalog", "删除了已经选择显示的城市，但是城市管理界面还有其他城市存在");
                        SharedPreferences.Editor editor = getSharedPreferences("cityselect", MODE_PRIVATE).edit();
                        editor.putBoolean("cityselect", true);
                        editor.putString("cityname",cityManageLists.get(0).getCityName());
                        editor.putBoolean("havecity",true);
                        editor.apply();
                    }
                    adapter.notifyDataSetChanged();
                    break;
                case 2://城市点击，设置城市选择标志位为true，以便下次启动时直接跳转至天气界面
                    Log.d("wcedlalog", "在城市管理界面点击选择了城市，跳转至天气界面，将城市选择标志位全部置true");
                    String selectCity=(String)message.obj;
                    SharedPreferences.Editor editor = getSharedPreferences("cityselect", MODE_PRIVATE).edit();
                    editor.putBoolean("cityselect", true);
                    editor.putString("cityname",selectCity);
                    editor.putBoolean("havecity",true);
                    editor.apply();
                    Intent intent = new Intent(CityManage.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("cityname", selectCity);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                    break;
            }
            return true;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) //当返回按键被按下
        {
            if(where.equals("Main"))
            {
                SharedPreferences sharedPreferences = getSharedPreferences("cityselect", MODE_PRIVATE);
                String cityname=sharedPreferences.getString("cityname","");
                if(cityname.equals(""))//如果当前选择的城市已经被删除或者没有已经选择的城市
                {
                    if(cityManageLists.size()>0)
                    {
                        //如果没有已经选择的城市，并且城市管理列表城市数目大于0，不配置城市选择是因为，前面删除操作已经做了相关操作了
                        Log.d("wcedlalog", "城市管理返回键按下，并且当前状态是没有已经选择的城市，城市管理列表存在的城市大于0，设置当前城市列表的第一个城市跳转天气界面显示天气");
                        Intent intent = new Intent(CityManage.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("cityname", cityManageLists.get(0).getCityName());
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                    else {//如果没有已经选择的城市，并且城市列表的城市数目为0，则跳转至城市选择界面，不配置城市选择是因为，前面删除操作已经做了相关操作了
                        Log.d("wcedlalog", "城市管理界面下，没有已经选择的城市，有可能执行了删除了所有城市，则直接跳转至城市选择界面");
                        Intent intent = new Intent(CityManage.this, CitySelectActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                else {
                    //啥都没动呗，然后是从主界面跳转过来的那我就回到主界面，cityname从配置文件中拿。
                    Log.d("wcedlalog", "城市管理界面，返回键按下，估计没有执行任何的操作或者删除了前一个已选择的城市，但是前面的逻辑已经重新把城市管理的第一个城市写入到配置文件了，并且使从主界面跳转过来的，那就回到主界面，城市名"+cityname);
                    Intent intent = new Intent(CityManage.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("cityname", cityname);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
            }
            else if(where.equals("Select"))
            {//如果是从城市选择界面进到城市管理界面的，那就跳转回城市选择界面，但是这时配置文件已经会影响界面跳转了，因为从城市选择界面进到城市管理界面，配置文件的havecity已经给置true了，如果进入城市管理没有执行任何操作的话就会跳转回城市选择，受到havecity的影响直接跳转到天气界面，如果存在多个城市直接跳转到城市管理界面
                Log.d("wcedlalog", "城市管理界面，从城市选择界面过来的，按了返回键，修改了配置文件的值，进入到了城市选择界面，为了能够成功的进入到城市选择界面，城市选择界面finish时会复原配置文件");
                SharedPreferences.Editor editor = getSharedPreferences("cityselect", MODE_PRIVATE).edit();
                editor.putBoolean("cityselect", false);
                editor.putBoolean("havecity",false);
                editor.apply();

                Intent intent=new Intent(CityManage.this,CitySelectActivity.class);
                startActivity(intent);
                finish();
            }
            else
                //特殊情况，结束城市管理
                finish();
        }

        return false;
    }


}
