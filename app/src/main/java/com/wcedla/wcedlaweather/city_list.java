package com.wcedla.wcedlaweather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.KeyEventDispatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wcedla.wcedlaweather.adapter.CityAdapter;
import com.wcedla.wcedlaweather.adapter.CityInfo;
import com.wcedla.wcedlaweather.db.CityListTable;
import com.wcedla.wcedlaweather.db.CityTable;
import com.wcedla.wcedlaweather.db.CountryTable;
import com.wcedla.wcedlaweather.db.HotCityTable;
import com.wcedla.wcedlaweather.db.ProvinceTable;
import com.wcedla.wcedlaweather.db.TypeSearchtable;
import com.wcedla.wcedlaweather.tool.HttpTool;
import com.wcedla.wcedlaweather.tool.JsonTool;
import com.wcedla.wcedlaweather.tool.LocationTool;
import com.wcedla.wcedlaweather.tool.SystemTool;

import org.json.JSONArray;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static org.litepal.LitePalBase.TAG;


public class city_list extends Fragment {

    /*按省份查找listview内容的级别的常量定义*/
    final int PROVINCE = 1;
    final int CITY = 2;
    final int COUNTRY = 3;


    int currentlevel = PROVINCE;//flag标志位，标志按省份查找listview的当前显示的级别是省份。
    String provincename;
    String cityname;

    ListView listView;//热门城市listview
    TextView titletext;//顶部状态栏文本
    Button backbtn;//后退按钮
    Button locatebtn;//定位按钮
    LinearLayout searchlayout;//热门城市布局
    ListView allcityListview;//按省份查找布局，实则一个listview
    ImageView hotimage;//热门gif图标
    TextView allcitytext;//热门搜索布局中的按省份查找textview
    ArrayAdapter hotcityadapter, allcityadapter;//设置热门城市，按省份查找listview的adapter
    EditText searcheditortext;
    TextView hottext;

    List<ProvinceTable> provinceList;//省份数据库
    List<CityTable> cityTableList;
    List<CountryTable> countryTableList;
    List<HotCityTable> hotCityTablesList;//热门城市数据库
    List<TypeSearchtable> typeSearchtableList;

    List<CityInfo> cityInfoList = new ArrayList<>();//热门城市布局listview的adapter适配器的显示资源
    List<String> showlist = new ArrayList<>();//按省份查找布局listview的adapter适配器的显示资源

    String editstring;
    String locatestring;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.city_list, container, false);//将选择城市布局加载进活动
        searchlayout = (LinearLayout) view.findViewById(R.id.search_layout);//城市搜索布局界面
        allcityListview = (ListView) view.findViewById(R.id.all_city_listview);//按省份查找listview
        listView = (ListView) view.findViewById(R.id.city_listview);//城市搜索listview
        titletext = (TextView) view.findViewById(R.id.title);//顶部状态栏文本
        backbtn = (Button) view.findViewById(R.id.back_button);//后退按钮
        locatebtn = (Button) view.findViewById(R.id.location_button);//定位按钮
        hotimage = (ImageView) view.findViewById(R.id.hot_gif);//热门gif图像
        Glide.with(getContext()).load(R.drawable.hot).into(hotimage);
        allcitytext = (TextView) view.findViewById(R.id.all_city_text);//搜索界面按省份查找textview
        searcheditortext = (EditText) view.findViewById(R.id.search_editor);
        hottext = (TextView) view.findViewById(R.id.hot_text);
        hotcityadapter = new CityAdapter(getContext(), R.layout.cityinfo_layout, cityInfoList);//热门城市listview适配器
        listView.setAdapter(hotcityadapter);//设置热门城市适配器

        locatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemTool.isFastClick()) {
                    LocationTool locationTool = new LocationTool();
                    locationTool.initLocation(getActivity().getApplicationContext(), new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message message) {
                            locatestring = message.obj.toString();
                            Log.d(TAG, "定位"+locatestring);
//                            SharedPreferences.Editor editor = getActivity().getSharedPreferences("cityselect", MODE_PRIVATE).edit();
//                            editor.putBoolean("cityselect", true);
//                            editor.putString("cityname",locatestring.split(" ")[2].replace("市",""));
//                            editor.apply();

                            //goToShow(locatestring.split(" ")[2].replace("市",""));
                            goToCityManage(locatestring.split(" ")[2].replace("市",""));
                            return true;
                        }
                    });

                }
            }
        });

        /*返回按钮点击监听*/
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemTool.isFastClick())
                {
                    if (currentlevel == PROVINCE) {
                        if (allcityListview.getVisibility() == View.VISIBLE) {
                            searchHotCity();//切换界面到城市搜索界面
                        } else {
                            getActivity().finish();
                        }
                    } else if (currentlevel == CITY) {
                        searchProvince();
                    } else if (currentlevel == COUNTRY) {
                        searchCity();
                    }
            }
            }
        });
        /*城市搜索布局中的按省份查找textview的点击事件*/
        allcitytext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*关闭输入法，不然印象体验*/
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null)
                    if (inputMethodManager.isActive())
                        inputMethodManager.hideSoftInputFromWindow(searcheditortext.getWindowToken(), 0);
                searchProvince();//调用显示省份信息的方法

            }
        });

        searcheditortext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                handler.removeCallbacks(mRunnable);
                editstring = charSequence.toString();
                //200毫秒没有输入认为输入完毕
                handler.postDelayed(mRunnable, 200);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {//加载碎片的活动的oncreated执行完毕后执行的操作
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentlevel == PROVINCE) {
                    //Toast.makeText(getContext(), cityInfoList.get(i).getCityname(), Toast.LENGTH_SHORT).show();
//                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("cityselect", MODE_PRIVATE).edit();
//                    editor.putBoolean("cityselect", true);
//                    editor.putString("cityname",cityInfoList.get(i).getCityname());
//                    editor.apply();
                    //goToShow(cityInfoList.get(i).getCityname());
                    goToCityManage(cityInfoList.get(i).getCityname());

                }
            }
        });

        allcityListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentlevel == PROVINCE) {
                    provincename = showlist.get(i);
                    searchCity();
                } else if (currentlevel == CITY) {
                    cityname = showlist.get(i);
                    searchCountry();
                } else if (currentlevel == COUNTRY) {
                    //Toast.makeText(getContext(), showlist.get(i), Toast.LENGTH_SHORT).show();
//                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("cityselect", MODE_PRIVATE).edit();
//                    editor.putBoolean("cityselect", true);
//                    editor.putString("cityname",showlist.get(i));
//                    editor.apply();
                    //goToShow(showlist.get(i));
                    goToCityManage(showlist.get(i));
                }
            }
        });

        searchHotCity();//界面创建完成之后，默认加载城市搜索界面
    }


    @SuppressLint( "HandlerLeak" )
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                if (editstring.equals("")) {
                    hottext.setText("热门搜索");
                    hotimage.setVisibility(View.VISIBLE);
                    searchHotCity();
                } else {
                    hottext.setText("搜索结果");
                    hotimage.setVisibility(View.INVISIBLE);
                    typeToSearch(editstring);
                }
            } else if (msg.what == 1) {

            }
        }
    };

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(0);
        }
    };


    private void searchProvince() {
        /*将城市搜索界面隐藏，按省份查找界面显示出来，并设置其listview的适配器*/
        searchlayout.setVisibility(View.GONE);
        allcityListview.setVisibility(View.VISIBLE);
        if (allcityadapter == null) {
            allcityadapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, showlist);
            allcityListview.setAdapter(allcityadapter);
        }
        titletext.setText("中国");//设置顶部状态栏文本
        provinceList = LitePal.findAll(ProvinceTable.class);//从数据库中取出所有内容
        if (provinceList.size() > 0) {//如果数据库有内容
            showlist.clear();//清空显示资源list
            for (ProvinceTable province : provinceList) {
                showlist.add(province.getProvincename());//遍历数据库内容添加到显示list
            }
            allcityadapter.notifyDataSetChanged();//刷新显示
            allcityListview.setSelection(0);//默认listview从头开始显示
            currentlevel = PROVINCE;//设置当前listview显示模式
        }
    }

    private void searchCity() {
        titletext.setText(provincename);//设置状态栏文本
        cityTableList = LitePal.where("adminname=?", provincename).find(CityTable.class);
        if (cityTableList.size() > 0) {
            showlist.clear();//清空显示资源list
            for (CityTable cityTable : cityTableList) {
                showlist.add(cityTable.getCityname());//遍历数据库内容添加到显示list
            }
            allcityadapter.notifyDataSetChanged();//刷新显示
            allcityListview.setSelection(0);//默认listview从头开始显示
            currentlevel = CITY;//设置当前listview显示模式
        }
    }

    private void searchCountry() {
        titletext.setText(cityname);
        countryTableList = LitePal.where("parentname=? and adminname=?", cityname, provincename).find(CountryTable.class);
        if (countryTableList.size() > 0) {
            showlist.clear();
            for (CountryTable countryTable : countryTableList) {
                showlist.add(countryTable.getCountryname());
            }
            allcityadapter.notifyDataSetChanged();
            allcityListview.setSelection(0);
            currentlevel = COUNTRY;
        }
    }

    private void searchHotCity() {
        /*将按省份查找listview隐藏，重新显示城市搜索界面*/
        searchlayout.setVisibility(View.VISIBLE);
        allcityListview.setVisibility(View.GONE);
        titletext.setText("搜索城市");//设置状态栏文本
        hotCityTablesList = LitePal.findAll(HotCityTable.class);//提取数据库中的所有内容
        if (hotCityTablesList.size() > 0) {//如果数据库不为空
            cityInfoList.clear();//清空显示list
            for (HotCityTable hotCityTable : hotCityTablesList) {//遍历list，并添加到显示list
                /*城市搜索适配器的view的显示泛型类*/
                CityInfo cityInfo = new CityInfo(hotCityTable.getCityname(), hotCityTable.getParentcityname(), hotCityTable.getAdmincityname());
                cityInfoList.add(cityInfo);
            }
            hotcityadapter.notifyDataSetChanged();//刷新listview显示
            allcityListview.setSelection(0);//默认从头开始显示
        }
    }

    private void typeToSearch(final String name) {
        String url = "https://search.heweather.com/find?key=c864606856d54eedb9f63a6cc0edd91f&number=20&group=cn&mode=match&location=" + Uri.encode(name);
        HttpTool.doHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "数据请求失败！", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responsedata = response.body().string();
                Boolean success = JsonTool.dealTypeSearchJson(responsedata);
                if (success) {
                    if (hottext.getText() != "热门搜索") {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                typeSearchtableList = LitePal.findAll(TypeSearchtable.class);//提取数据库中的所有内容
                                cityInfoList.clear();//清空显示list
                                for (TypeSearchtable typeSearchtable : typeSearchtableList) {//遍历list，并添加到显示list
                                    /*城市搜索适配器的view的显示泛型类*/
                                    CityInfo cityInfo = new CityInfo(typeSearchtable.getCityname(), typeSearchtable.getParentcityname(), typeSearchtable.getAdmincityname());
                                    cityInfoList.add(cityInfo);
                                }
                                hotcityadapter.notifyDataSetChanged();//刷新listview显示
                                allcityListview.setSelection(0);//默认从头开始显示
                            }
                        });
                    }

                }
            }
        });
    }

    /**
     * 转到天气信息主界面
     *
     * @param cityname   选择的城市名
     */
    private void goToCityManage(String cityname)
    {
        //将选择的城市名写入到城市管理的表中
        List<CityListTable> cityListTableList=LitePal.where("cityName=?",cityname).find(CityListTable.class);
        if(cityListTableList.size()<1)
        {
            CityListTable cityListTable=new CityListTable();
            cityListTable.setCityName(cityname);
            cityListTable.save();
        }
        //将选择的城市写入到配置文件中
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("cityselect", MODE_PRIVATE).edit();
        editor.putBoolean("cityselect", true);//城市选择置true
        editor.putString("cityname",cityname);//置城市名字就是这个唯一一个的城市的名字
        editor.apply();
        Log.d("wcedlalog", "city_list跳转天气管理界面,添加选择的城市名到城市管理表，城市为"+cityname);
        Intent intent=new Intent(getActivity(),CityManage.class);
        Bundle bundle = new Bundle();
        bundle.putString("where", "Select");//告诉城市管理界面我是城市选择界面，后面你退出的时候记得回到我的界面
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().finish();
    }


}
