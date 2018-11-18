package com.wcedla.wcedlaweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wcedla.wcedlaweather.db.CityTable;
import com.wcedla.wcedlaweather.db.CountryTable;
import com.wcedla.wcedlaweather.db.ProvinceTable;
import com.wcedla.wcedlaweather.gson.AdminGson;
import com.wcedla.wcedlaweather.gson.ChinaGson;
import com.wcedla.wcedlaweather.gson.CityGson;
import com.wcedla.wcedlaweather.gson.ParentGson;
import com.wcedla.wcedlaweather.tool.BaseActivity;
import com.wcedla.wcedlaweather.tool.HttpTool;
import com.wcedla.wcedlaweather.tool.JsonTool;
import com.wcedla.wcedlaweather.tool.SystemTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


import static org.litepal.LitePalBase.TAG;

public class LoadData extends BaseActivity {

    ImageView upgradeimage;
    TextView upgradetext;
    ProgressBar progressBar;
    Button upgradeokbtn;
    Boolean isfirstrun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("jsonupgrade", MODE_PRIVATE);
        isfirstrun = sharedPreferences.getBoolean("upgradeok", false);

        if (!isfirstrun) {
            setChinaData();
        } else {
            skipUpgrade();
        }
        super.onCreate(savedInstanceState);
        hideBottomUIMenu();
        setContentView(R.layout.activity_load_data);
        upgradetext = (TextView) findViewById(R.id.upgrade_text);
        progressBar = (ProgressBar) findViewById(R.id.upgrade_progress);
        upgradeimage = (ImageView) findViewById(R.id.upgrade_image);
        upgradeokbtn = (Button) findViewById(R.id.upgrade_okbtn);
        upgradeokbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipUpgrade();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        hideBottomUIMenu();
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void setChinaData() {

        String url = "https://search.heweather.com/top?key=c864606856d54eedb9f63a6cc0edd91f&group=cn";
        HttpTool.doHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        upgradetext.setText("很抱歉，数据出现了异常，请等待软件后续更新！");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responsedata = response.body().string();
                JsonTool.dealHotCityJson(responsedata);
            }
        });


        url = "http://wcedla.oss-cn-shanghai.aliyuncs.com/city_json/wcedlachina.json";
        HttpTool.doHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        upgradetext.setText("很抱歉，数据出现了异常，请等待软件后续更新！");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responsedata = response.body().string();
                try {
                    LitePal.deleteAll(ProvinceTable.class);
                    LitePal.deleteAll(CityTable.class);
                    LitePal.deleteAll(CountryTable.class);
                    double i = 0, temp = 0, progress = 0;
                    Log.d(TAG, "china表赋值前的状态: " + LitePal.findAll(ProvinceTable.class).size() + "," + LitePal.findAll(CityTable.class).size() + "," + LitePal.findAll(CountryTable.class).size());
                    JSONArray jsonArray = new JSONArray(responsedata);
                    String chinastring = jsonArray.getJSONObject(0).toString();
                    ChinaGson chinaGson = new Gson().fromJson(chinastring, ChinaGson.class);
                    List<AdminGson> adminGsonList = chinaGson.adminGsonList;
                    for (AdminGson adminGson : adminGsonList) {
                        ProvinceTable provinceTable = new ProvinceTable();
                        provinceTable.setProvincename(adminGson.adminname);
                        provinceTable.save();
                        List<ParentGson> parentGsonList = adminGson.parentGsonList;
                        for (ParentGson parentGson : parentGsonList) {
                            CityTable cityTable = new CityTable();
                            cityTable.setCityname(parentGson.parentname);
                            cityTable.setAdminname(adminGson.adminname);
                            cityTable.save();
                            List<CityGson> cityGsonList = parentGson.cityGsonList;
                            for (CityGson cityGson : cityGsonList) {
                                CountryTable countryTable = new CountryTable();
                                countryTable.setCountryname(cityGson.cityname);
                                countryTable.setParentname(parentGson.parentname);
                                countryTable.setAdminname(adminGson.adminname);
                                countryTable.save();
                            }
                        }
                        i += 1;
                        progress = ((int) (i / adminGsonList.size() * 1000.0)) / 10.0;
                        if (progress > temp) {
                            temp = progress;
                            upgradeProgressBar(temp);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void upgradeProgressBar(final double progresss) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress((int) progresss);
                if ((int) progresss >= 100) {
                    upgradetext.setText("数据缓存完成，请点击完成进入软件");
                    upgradeimage.setImageResource(R.drawable.upgrade_ok);
                    upgradeokbtn.setVisibility(View.VISIBLE);
                    SharedPreferences.Editor editor = getSharedPreferences("jsonupgrade", MODE_PRIVATE).edit();
                    editor.putBoolean("upgradeok", true);
                    editor.apply();


                } else {
                    upgradetext.setText("我们正在将数据缓存到手机..." + String.valueOf(progresss) + "%");
                }
            }
        });
    }

    private void skipUpgrade() {
        Intent intent = new Intent(this, CitySelectActivity.class);
        startActivity(intent);
        finish();
    }
}
