package com.wcedla.wcedlaweather.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wcedla.wcedlaweather.CityManage;
import com.wcedla.wcedlaweather.R;
import com.wcedla.wcedlaweather.db.CityListTable;
import com.wcedla.wcedlaweather.view.SlideLayout;

import org.litepal.LitePal;

import java.util.List;

import javax.security.auth.callback.Callback;

/*
* 城市管理listview数据适配器主要设置城市名，城市图标，now的天气文本和温度
* */

public class CityManageAdapter extends ArrayAdapter<CityManageList> {

    int resourceid;
    Handler.Callback callback;
    View saveView;

    public CityManageAdapter(Context context, int resource, List<CityManageList> objects, Handler.Callback callback) {
        super(context, resource, objects);
        this.callback=callback;
        resourceid=resource;//获取到listview的布局文件
    }



    public View getView(final int position, View convertView, ViewGroup parent) {
        final CityManageList cityManageList=getItem(position);//获取该view对应的数据类，通过它来设置数据
        View view;//构造view
        final ViewHolder viewHolder;
        if(convertView==null) {
            view =LayoutInflater.from(getContext()).inflate(resourceid,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.contentLayout=view.findViewById(R.id.contentlayout);//左边显示整个天气信息的包括城市名天气文本的view
            viewHolder.cityName=view.findViewById(R.id.content1);//城市名
            viewHolder.weatherText=view.findViewById(R.id.content2);//天气文本
            viewHolder.temperatureText=view.findViewById(R.id.content3);//温度文本
            viewHolder.deleteButton=view.findViewById(R.id.deletebutton);//删除按钮
            view.setTag(viewHolder);
        }
        else
        {
            view=convertView;
            viewHolder=(ViewHolder)convertView.getTag();
        }
        final SlideLayout slideLayout = (SlideLayout) view;//获取支持滑动的layoutview
        slideLayout.setOnStateChangeListener(new MyOnStateChangeListener());//设置监听

        viewHolder.cityName.setText(cityManageList.getCityName());
        viewHolder.weatherText.setText(cityManageList.getWeatherText());
        viewHolder.temperatureText.setText(cityManageList.getTemperatureText());
        viewHolder.contentLayout.setOnClickListener(new View.OnClickListener() {//为整个左边的view设置点击事件
            @Override
            public void onClick(View view) {
                Message message=new Message();
                message.obj=viewHolder.cityName.getText();
                message.what=2;
                callback.handleMessage(message);//跨进程通信
            }
        });
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideLayout.closeMenu();
                Message message=new Message();
                message.obj=viewHolder.cityName.getText()+"_"+position;
                message.what=1;
                callback.handleMessage(message);//跨进程通信
            }
        });
        return view;
    }

    class ViewHolder
    {
        LinearLayout contentLayout;
        TextView cityName;
        TextView weatherText;
        TextView temperatureText;
        TextView deleteButton;
    }

    public SlideLayout slideLayout = null;
    class MyOnStateChangeListener implements SlideLayout.OnStateChangeListener
    {

        @Override
        public void onOpen(SlideLayout layout) {

            slideLayout = layout;//获取view
        }

        @Override
        public void onMove(SlideLayout layout) {
            if (slideLayout != null && slideLayout !=layout)//如果滑动则关闭上一个打开的view
            {
                slideLayout.closeMenu();
            }
        }

        @Override
        public void onClose(SlideLayout layout) {
            if (slideLayout == layout)//关闭了view
            {
                slideLayout = null;
            }
        }
    }

}
