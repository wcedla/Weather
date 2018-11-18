package com.wcedla.wcedlaweather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.BaseAdapter;

import com.wcedla.wcedlaweather.tool.BaseActivity;
import com.wcedla.wcedlaweather.tool.SystemTool;

public class AboutMe extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemTool.setNavigationBarStatusBarTranslucent(this);
        setContentView(R.layout.activity_about_me);
    }
}
