package com.wcedla.wcedlaweather;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wcedla.wcedlaweather.service.WeatherUpdateService;
import com.wcedla.wcedlaweather.tool.BaseActivity;
import com.wcedla.wcedlaweather.tool.SystemTool;
import com.wcedla.wcedlaweather.view.MyPopWindow;
import com.wcedla.wcedlaweather.view.SwitchButton;

import static org.litepal.LitePalBase.TAG;

public class WeatherSetting extends BaseActivity {

    MyPopWindow myPopWindow;
    View popupView;
    WeatherUpdateService.WeatherBinder weatherBinder;

    ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            weatherBinder=(WeatherUpdateService.WeatherBinder)iBinder;
            weatherBinder.SetNotification();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=new Intent(this,WeatherUpdateService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        SystemTool.setNavigationBarStatusBarTranslucent(this);
        SharedPreferences settingXml = getSharedPreferences("weatherSetting", MODE_PRIVATE);
        boolean isShowBar = settingXml.getBoolean("notificationBar", true);//是否显示通知栏
        boolean isUpdateWeather= settingXml.getBoolean("updateWeather", true);
        String updateString=settingXml.getString("updateTime","3小时");

        setContentView(R.layout.activity_weather_setting);
        final SharedPreferences.Editor editor = getSharedPreferences("weatherSetting", MODE_PRIVATE).edit();
        final SwitchButton notificationButton=findViewById(R.id.notification_switch_button);
        RelativeLayout notificationSwitchLayout=findViewById(R.id.notification_button_layout);
        final SwitchButton updateButton=findViewById(R.id.update_switch_button);
        RelativeLayout updateSwitchLayout=findViewById(R.id.setting_update_layout);
        final RelativeLayout updateTimeLayout=findViewById(R.id.setting_update_time_layout);
        final TextView updateTimeText=findViewById(R.id.update_time_text);

        if(isShowBar)
        {
            notificationButton.setSelect(true);
        }
        else
        {
            notificationButton.setSelect(false);
        }

        if(isUpdateWeather)
        {
            updateButton.setSelect(true);
        }
        else
        {
            updateButton.setSelect(false);
        }
        updateTimeText.setText(updateString);

        notificationSwitchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notificationButton.isSelect())
                {
                    editor.putBoolean("notificationBar", false);//状态栏选择置false
                    editor.apply();
                    notificationButton.setCheck(false);
                    weatherBinder.stopNotification();
                    weatherBinder.SetNotification();
                }
                else if(!notificationButton.isSelect())
                {
                    editor.putBoolean("notificationBar", true);//状态栏选择置true
                    editor.apply();
                    notificationButton.setCheck(true);
                    weatherBinder.SetNotification();
                }
            }
        });

        updateSwitchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(updateButton.isSelect())
                {
                    editor.putBoolean("updateWeather",false);
                    editor.apply();
                    updateButton.setCheck(false);
                    updateTimeLayout.setVisibility(View.GONE);

                }
                else if(!updateButton.isSelect())
                {
                    editor.putBoolean("updateWeather",true);
                    editor.apply();
                    updateButton.setCheck(true);
                    updateTimeLayout.setVisibility(View.VISIBLE);

                }
            }
        });

        updateTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupView=getLayoutInflater().inflate(R.layout.setting_update_time_select,null);
                myPopWindow=new MyPopWindow(popupView,RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                myPopWindow.showAtLocation(getLayoutInflater().inflate(R.layout.activity_weather_setting,null),Gravity.BOTTOM,0,0);
                lightOff();
                myPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();

                        layoutParams.alpha=1.0f;

                        getWindow().setAttributes(layoutParams);
                    }
                });
                final TextView oneHour=popupView.findViewById(R.id.update_time_one);
                final TextView twoHour=popupView.findViewById(R.id.update_time_two);
                final TextView threeHour=popupView.findViewById(R.id.update_time_three);
                oneHour.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(WeatherSetting.this,oneHour.getText(),Toast.LENGTH_SHORT).show();
                        editor.putString("updateTime",oneHour.getText().toString());
                        editor.apply();
                        updateTimeText.setText(oneHour.getText());
                        weatherBinder.refreshAlarmManger();
                        myPopWindow.dismiss();
                    }
                });
                twoHour.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(WeatherSetting.this,twoHour.getText(),Toast.LENGTH_SHORT).show();
                        editor.putString("updateTime",twoHour.getText().toString());
                        editor.apply();
                        updateTimeText.setText(twoHour.getText());
                        weatherBinder.refreshAlarmManger();
                        myPopWindow.dismiss();

                    }
                });
                threeHour.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //oast.makeText(WeatherSetting.this,threeHour.getText(),Toast.LENGTH_SHORT).show();
                        editor.putString("updateTime",threeHour.getText().toString());
                        editor.apply();
                        updateTimeText.setText(threeHour.getText());
                        weatherBinder.refreshAlarmManger();
                        myPopWindow.dismiss();

                    }
                });
            }
        });
    }


    /**
     * 显示时屏幕变暗
     */
    private void lightOff() {

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();

        layoutParams.alpha=0.7f;

        getWindow().setAttributes(layoutParams);

    }

    private void startService()
    {

        Intent intent=new Intent(this,WeatherUpdateService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
        }
        else
        {
                startService(intent);
        }


    }

    @Override
    protected void onDestroy()
    {
        unbindService(serviceConnection);
        super.onDestroy();
    }
}
