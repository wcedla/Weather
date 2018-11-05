package com.wcedla.wcedlaweather.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wcedla.wcedlaweather.R;

import java.util.List;

/*
* 热门城市listview数据适配器，主要是给每个view设置城市名，市区名，省份名
* */

public class CityAdapter extends ArrayAdapter<CityInfo> {

    int resourceid;


    public CityAdapter( Context context,int resource,List<CityInfo> objects) {
        super(context, resource, objects);
        resourceid=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CityInfo cityInfo=getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null) {
            view =LayoutInflater.from(getContext()).inflate(resourceid,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.cityname=(TextView)view.findViewById(R.id.city_name);
            viewHolder.parentcityname=(TextView)view.findViewById(R.id.parent_city);
            viewHolder.admincityname=(TextView)view.findViewById(R.id.admin_city);
            view.setTag(viewHolder);
        }
        else
        {
            view=convertView;
            viewHolder=(ViewHolder)convertView.getTag();
        }
        viewHolder.cityname.setText(cityInfo.getCityname());//设置城市名
        viewHolder.parentcityname.setText(cityInfo.getParentcityname());//设置市区名
        viewHolder.admincityname.setText(cityInfo.getAdmincityname());//设置省份名


        return view;
    }

    class ViewHolder
    {
        TextView cityname;
        TextView parentcityname;
        TextView admincityname;
    }
}
