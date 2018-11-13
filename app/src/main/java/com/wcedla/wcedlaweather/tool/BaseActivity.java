package com.wcedla.wcedlaweather.tool;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.wcedla.wcedlaweather.R;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences settingXml = getSharedPreferences("color", MODE_PRIVATE);
        int themeId=settingXml.getInt("changeTheme",R.style.AppTheme);
        setTheme(themeId);
        super.onCreate(savedInstanceState);
    }
}
