package com.wcedla.wcedlaweather.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/*
* 主界面页面切换的viewpager的适配器
* */
public class WeatherPagerAdapter extends FragmentStatePagerAdapter
{
    public List<Fragment> fragmentList=new ArrayList<>();

    public WeatherPagerAdapter(FragmentManager fm, List<Fragment> fragmentList)
    {
        super(fm);
        this.fragmentList=fragmentList;
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void terst()
    {

    }
}
