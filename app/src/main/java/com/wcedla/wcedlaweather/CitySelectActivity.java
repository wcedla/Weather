package com.wcedla.wcedlaweather;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.wcedla.wcedlaweather.db.CityListTable;
import com.wcedla.wcedlaweather.tool.SystemTool;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class CitySelectActivity extends AppCompatActivity {

    Boolean isSelect;
    String cityName;
    Boolean haveCity;
    List<CityListTable> cityListTableList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemTool.setNavigationBarStatusBarTranslucent(this);
        setContentView(R.layout.activity_city_select);
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
        cityListTableList = LitePal.findAll(CityListTable.class);//用于获取城市管理表的城市数目
        SharedPreferences sharedPreferences = getSharedPreferences("cityselect", MODE_PRIVATE);
        isSelect = sharedPreferences.getBoolean("cityselect", false);//判断一下是否有选中城市
        cityName=sharedPreferences.getString("cityname","");//城市名字
        haveCity=sharedPreferences.getBoolean("havecity",false);//城市管理是否有城市
        if(isSelect&&!cityName.equals(""))//如果选择了城市并且城市信息已经有了
        {
            goToShow(cityName);//转到城市天气界面
        }
        else if(haveCity)//城市已经添加到了城市管理，但是没有刷新城市
        {
            if(cityListTableList.size()==1)//如果城市管理只有一个城市
            {
                Log.d("wcedlalog", "citySelectActivity跳转天气界面（城市已选择但是城市名为空，并且城市管理只添加了一个城市），城市为"+cityListTableList.get(0).getCityName());
                SharedPreferences.Editor editor = getSharedPreferences("cityselect", MODE_PRIVATE).edit();
                editor.putBoolean("cityselect", true);//城市选择置true
                editor.putString("cityname",cityListTableList.get(0).getCityName());//置城市名字就是这个唯一一个的城市的名字
                editor.apply();
                Intent intent = new Intent(CitySelectActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("cityname", cityListTableList.get(0).getCityName());
                intent.putExtras(bundle);
                startActivity(intent);//转到天气信息界面
                finish();//结束当前活动
            }
            else//如果城市管理不止一个城市，则直接跳转到城市管理界面，由用户自己选择
            {
                Log.d("wcedlalog", "citySelectActivity跳转天气管理界面（城市已选择但是城市名为空，并且城市管理存在多个城市），城市为"+cityName);
                Intent intent=new Intent(CitySelectActivity.this,CityManage.class);//不需要重新进入到天气界面或者城市选择界面，所以不需要带bundel参数
                startActivity(intent);
                finish();//结束本活动
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) //当返回按键被按下
        {
            ListView allcityListview = (ListView) findViewById(R.id.all_city_listview);
            Button backbtn = (Button) findViewById(R.id.back_button);//后退按钮
            if (allcityListview.getVisibility() == View.VISIBLE) {
                backbtn.performClick();
            }
            else if (cityListTableList.size()>0)
            {//如果当前城市管理有城市存在则根据配置文件是否有城市，如果有则跳转到该城市的天气界面，如果没有，则显示城市管理的第一个城市的天气界面

                SharedPreferences sharedPreferences = getSharedPreferences("cityselect", MODE_PRIVATE);
                String preferencesCityName=sharedPreferences.getString("cityname","");
                if(!preferencesCityName.equals(""))
                {
                    Log.d("wcedlalog", "城市选择返回键按下，当前状态城市管理界面有多个城市 ，并且配置文件有城市存在，显示配置文件的天气界面");
                    Intent intent = new Intent(CitySelectActivity.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("cityname", preferencesCityName);
                    intent.putExtras(bundle);
                    startActivity(intent);//转到天气信息界面
                    finish();
                }
                else
                {
                    Log.d("wcedlalog", "城市选择返回键按下，当前状态城市管理界面有多个城市 ，并且配置文件没有城市存在，显示城市管理界面的第一个城市的天气界面");
                    Intent intent = new Intent(CitySelectActivity.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("cityname", cityListTableList.get(0).getCityName());
                    intent.putExtras(bundle);
                    startActivity(intent);//转到天气信息界面
                    finish();
                }

            }
            else
            {
                super.onBackPressed();
            }
        }
            return false;
    }

    private void goToShow(String cityname) {

        Log.d("wcedlalog", "citySelectActivity跳转天气界面（城市选择和城市名都存在直接跳转），城市为"+cityName);
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("cityname", cityname);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    @Override
    public void finish() {
        cityListTableList = LitePal.findAll(CityListTable.class);
        SharedPreferences sharedPreferences = getSharedPreferences("cityselect", MODE_PRIVATE);
        //isSelect = sharedPreferences.getBoolean("cityselect", false);
        String saveCityName=sharedPreferences.getString("cityname","");

        if(cityListTableList.size()>0||!saveCityName.equals(""))//如果是城市名存在，则应该重新把城市已选择置true，配合城市管理界面在城市存在的情况下添加城市使用，重新置true后，下次再启动城市选择的时候才会直接进入天气界面
        {
            Log.d("wcedlalog", "城市选择界面结束，配置文件有城市，城市管理界面有城市存在，置配置文件状态为true");
            SharedPreferences.Editor editor = getSharedPreferences("cityselect", MODE_PRIVATE).edit();
            editor.putBoolean("cityselect", true);
            editor.putBoolean("havecity",true);
            editor.apply();

        }
        super.finish();
    }
}
