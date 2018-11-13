package com.wcedla.wcedlaweather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.wcedla.wcedlaweather.R;

import java.util.List;

public class ThemeGridviewAdapter extends BaseAdapter {

    private int checkItem;
    Context context;
    List<Integer> list;

    public ThemeGridviewAdapter(Context context, List<Integer> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.theme_item, null);
            holder = new Holder();
            holder.imageView1 = (ImageView) convertView.findViewById(R.id.theme_circle_img);
            holder.imageView2 = (ImageView) convertView.findViewById(R.id.theme_done_img);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.imageView1.setImageResource(list.get(position));
        if (checkItem == position) {
            holder.imageView2.setImageResource(R.drawable.theme_done);
        }
        return convertView;
    }
    public void setCheckItem(int checkItem) {
        this.checkItem = checkItem;
    }
    static class Holder {
        ImageView imageView1;
        ImageView imageView2;
    }
}
