package com.wcedla.wcedlaweather.tool;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wcedla.wcedlaweather.db.HotCityTable;
import com.wcedla.wcedlaweather.db.TypeSearchtable;
import com.wcedla.wcedlaweather.db.VersionTable;
import com.wcedla.wcedlaweather.db.WeatherBasicTable;
import com.wcedla.wcedlaweather.db.WeatherForecastTable;
import com.wcedla.wcedlaweather.db.WeatherHourlyTable;
import com.wcedla.wcedlaweather.db.WeatherLifeStyleTable;
import com.wcedla.wcedlaweather.db.WeatherNowTable;
import com.wcedla.wcedlaweather.db.WeatherUpdateTable;
import com.wcedla.wcedlaweather.gson.HotCityBasic;
import com.wcedla.wcedlaweather.gson.HotCityGson;
import com.wcedla.wcedlaweather.gson.WeatherForecast;
import com.wcedla.wcedlaweather.gson.WeatherGson;
import com.wcedla.wcedlaweather.gson.WeatherHourly;
import com.wcedla.wcedlaweather.gson.WeatherLifeStyle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static org.litepal.LitePalBase.TAG;

/*解析json类*/

public class JsonTool {

    //解析热门城市json
    public static Boolean dealHotCityJson(String jsondata) {
        try {
            JSONObject jsonObject = new JSONObject(jsondata);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String hotcitystring = jsonArray.getJSONObject(0).toString();
            HotCityGson hotCityGson=new Gson().fromJson(hotcitystring, HotCityGson.class);
            List<HotCityBasic> hotCityList=hotCityGson.basic;
            LitePal.deleteAll(HotCityTable.class);
            Log.d(TAG, "热门城市表赋值前状态: "+LitePal.findAll(HotCityTable.class).size());
            for(HotCityBasic hotCityBasic : hotCityList)
            {
                HotCityTable hotCityTable=new HotCityTable();
                hotCityTable.setCityname(hotCityBasic.location);
                hotCityTable.setParentcityname(hotCityBasic.parentcity);
                hotCityTable.setAdmincityname(hotCityBasic.admincity);
                hotCityTable.save();
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }


    //处理搜索结果json
    public static Boolean dealTypeSearchJson(String jsondata) {
        try {
            JSONObject jsonObject = new JSONObject(jsondata);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String typesearchstring = jsonArray.getJSONObject(0).toString();
            Log.d(TAG, "我1"+typesearchstring);
            HotCityGson typesearchgson = new Gson().fromJson(typesearchstring, HotCityGson.class);
            List<HotCityBasic> typeSearchtableList = typesearchgson.basic;
            LitePal.deleteAll(TypeSearchtable.class);
            if (typeSearchtableList != null) {
                for (HotCityBasic typesearchbasic : typeSearchtableList) {
                    TypeSearchtable typeSearchtable = new TypeSearchtable();
                    typeSearchtable.setCityname(typesearchbasic.location);
                    typeSearchtable.setParentcityname(typesearchbasic.parentcity);
                    typeSearchtable.setAdmincityname(typesearchbasic.admincity);
                    typeSearchtable.save();
                }
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    //处理天气信息json
    public static Boolean dealWeatherJson(String jsondata,String cityname) {
        try {
            JSONObject jsonObject = new JSONObject(jsondata);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String jsonstring = jsonArray.getJSONObject(0).toString();
            final WeatherGson weatherGson = new Gson().fromJson(jsonstring, WeatherGson.class);
            LitePal.deleteAll(WeatherBasicTable.class,"cityName=?",cityname);
            LitePal.deleteAll(WeatherUpdateTable.class,"cityName=?",cityname);
            LitePal.deleteAll(WeatherNowTable.class,"cityName=?",cityname);
            LitePal.deleteAll(WeatherForecastTable.class,"cityName=?",cityname);
            LitePal.deleteAll(WeatherHourlyTable.class,"cityName=?",cityname);
            LitePal.deleteAll(WeatherLifeStyleTable.class,"cityName=?",cityname);

            WeatherBasicTable weatherBasicTable = new WeatherBasicTable();
            weatherBasicTable.setCityName(weatherGson.basic.cityname);
            weatherBasicTable.setParentName(weatherGson.basic.parentname);
            weatherBasicTable.setAdminName(weatherGson.basic.adminname);
            weatherBasicTable.setCid(weatherGson.basic.cityid);
            weatherBasicTable.save();
//            List<WeatherBasicTable> weatherBasicTableList = LitePal.where("cityName=?", cityname).find(WeatherBasicTable.class);
//            Log.d(TAG, "表" + weatherBasicTableList.get(0).getCityName());
            WeatherUpdateTable weatherUpdateTable = new WeatherUpdateTable();
            weatherUpdateTable.setCityName(weatherGson.basic.cityname);
            weatherUpdateTable.setUpdateTime(weatherGson.update.updatetime);
            weatherUpdateTable.save();
//            List<WeatherUpdateTable> weatherUpdateTableList = LitePal.where("cityName=?", cityname).find(WeatherUpdateTable.class);
//            Log.d(TAG, "更新表" + weatherUpdateTableList.get(0).getUpdateTime());
            WeatherNowTable weatherNowTable = new WeatherNowTable();
            weatherNowTable.setCityName(weatherGson.basic.cityname);
            weatherNowTable.setIconId(weatherGson.now.weatherpicturecode);
            weatherNowTable.setWeatherText(weatherGson.now.weathertext);
            weatherNowTable.setBodyTemperature(weatherGson.now.bodytemperature);
            weatherNowTable.setHumidity(weatherGson.now.humidity);
            weatherNowTable.setRainCount(weatherGson.now.raincount);
            weatherNowTable.setTemperature(weatherGson.now.temperature);
            weatherNowTable.setCouldSee(weatherGson.now.couldsee);
            weatherNowTable.setWindDirection(weatherGson.now.winddirection);
            weatherNowTable.setWindLevel(weatherGson.now.windlevel);
            weatherNowTable.save();
//            List<WeatherNowTable> weatherNowTableList = LitePal.where("cityName=?", cityname).find(WeatherNowTable.class);
//            Log.d(TAG, "当前表" + weatherNowTableList.get(0).getWeatherText() + ",,," + weatherNowTableList.size());
            List<WeatherForecast> weatherForecastList = weatherGson.forecasts;
            for (int i = 0; i < weatherForecastList.size(); i++) {
                WeatherForecastTable weatherForecastTable = new WeatherForecastTable();
                weatherForecastTable.setCityname(weatherGson.basic.cityname);
                weatherForecastTable.setDayIconCode(weatherForecastList.get(i).daycode);
                weatherForecastTable.setNightIconCode(weatherForecastList.get(i).nightcode);
                weatherForecastTable.setDayWeatherText(weatherForecastList.get(i).daytext);
                weatherForecastTable.setNightWeatherText(weatherForecastList.get(i).nighttext);
                weatherForecastTable.setForecastDate(weatherForecastList.get(i).date);
                weatherForecastTable.setHumidity(weatherForecastList.get(i).humidity);
                weatherForecastTable.setWhetherRain(weatherForecastList.get(i).israinornot);
                weatherForecastTable.setSunRaise(weatherForecastList.get(i).sunraise);
                weatherForecastTable.setSunSet(weatherForecastList.get(i).sunset);
                weatherForecastTable.setMaxTemperature(weatherForecastList.get(i).maxtemperature);
                weatherForecastTable.setMinTemperature(weatherForecastList.get(i).mintemperature);
                weatherForecastTable.setWindDirection(weatherForecastList.get(i).winddirection);
                weatherForecastTable.setWindLevel(weatherForecastList.get(i).windlevel);
                weatherForecastTable.save();
//                List<WeatherForecastTable> weatherForecastTableList = LitePal.where("cityName=?", cityname).find(WeatherForecastTable.class);
//                Log.d(TAG, "预报表" + weatherForecastList.get(i).date);
            }

            List<WeatherHourly> weatherHourlyList=weatherGson.hourlyList;
            for(int i=0;i<weatherHourlyList.size();i++)
            {
                WeatherHourlyTable weatherHourlyTable=new WeatherHourlyTable();
                weatherHourlyTable.setCityName(weatherGson.basic.cityname);
                weatherHourlyTable.setTime(weatherHourlyList.get(i).time);
                weatherHourlyTable.setTemperature(weatherHourlyList.get(i).temperature);
                weatherHourlyTable.setWeather(weatherHourlyList.get(i).weatherText);
                weatherHourlyTable.save();
            }

            List<WeatherLifeStyle> weatherLifeStyleList = weatherGson.lifeStyles;
            for (int i = 0; i < weatherLifeStyleList.size(); i++) {
                WeatherLifeStyleTable weatherLifeStyleTable = new WeatherLifeStyleTable();
                weatherLifeStyleTable.setCityName(weatherGson.basic.cityname);
                weatherLifeStyleTable.setLifeStyleType(weatherGson.lifeStyles.get(i).type);
                weatherLifeStyleTable.setShortText(weatherGson.lifeStyles.get(i).shorttext);
                weatherLifeStyleTable.setLifeStyleText(weatherGson.lifeStyles.get(i).lifestyletext);
                weatherLifeStyleTable.save();
//                List<WeatherLifeStyleTable> weatherLifeStyleTableList = LitePal.where("cityName=?", cityname).find(WeatherLifeStyleTable.class);
//                Log.d(TAG, "生活质量表" + weatherLifeStyleList.get(i).shorttext);

            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean dealVersionJson(String jsonData)
    {
        try {
            JSONArray jsonArray=new JSONArray(jsonData);
            LitePal.deleteAll(VersionTable.class);
            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String versionCode=jsonObject.getString("versionCode");
                String versionName=jsonObject.getString("versionName");
                String fileName=jsonObject.getString("fileName");
                String downloadUrl=jsonObject.getString("downloadUrl");
                Log.d(TAG, "json解析的版本信息"+versionCode+","+versionName+","+downloadUrl);
                VersionTable versionTable=new VersionTable();
                versionTable.setVersionCode(versionCode);
                versionTable.setVersionName(versionName);
                versionTable.setFileName(fileName);
                versionTable.setDownloadUrl(downloadUrl);
                versionTable.save();
            }
            return true;

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

    }
}
