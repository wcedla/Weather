package com.wcedla.wcedlaweather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.wcedla.wcedlaweather.tool.SystemTool;
import com.wcedla.wcedlaweather.view.SwitchButton;

public class WeatherSetting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemTool.setNavigationBarStatusBarTranslucent(this);
        setContentView(R.layout.activity_weather_setting);
        final SwitchButton switchButton=findViewById(R.id.switch_button);
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchButton.isSelect())
                switchButton.setCheck(false);
                else if(!switchButton.isSelect())
                {
                    switchButton.setCheck(true);
                }
            }
        });
    }
}
