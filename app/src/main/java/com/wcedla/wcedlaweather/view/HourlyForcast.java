package com.wcedla.wcedlaweather.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.baidu.location.Poi;
import com.wcedla.wcedlaweather.R;
import com.wcedla.wcedlaweather.db.WeatherHourlyTable;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class HourlyForcast extends View {

    TypedArray array;
    Paint myPaint;
    Paint cutLinePaint;
    Path myPath;
    DisplayMetrics outMetrics;
    int width,height;
    float density;

    List<WeatherHourlyTable> weatherHourlyTableList=new ArrayList<>();
    List<PointF> pointList=new ArrayList<>();


    public HourlyForcast(Context context) {
        this(context,null);
    }

    public HourlyForcast(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HourlyForcast(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HourlyForcast, defStyleAttr, 0);
        initData();//初始化各种属性
        array.recycle();//注意回收内存，防止内存泄漏
    }

    private void initData()
    {
        weatherHourlyTableList.clear();
        pointList.clear();
        myPaint=new Paint();
        cutLinePaint=new Paint();
        myPath=new Path();
        outMetrics=getResources().getDisplayMetrics();//获取屏幕参数矩阵
        width=outMetrics.widthPixels;//屏幕宽度
        height=outMetrics.heightPixels;//屏幕高度
        density=outMetrics.density;//屏幕分辨率

        myPaint.setStyle(Paint.Style.FILL);
        myPaint.setColor(Color.WHITE);
        myPaint.setTextSize(45);
        myPaint.setDither(true);
        myPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mywidth, myheight;
        mywidth = getMySize(width+181*(int)density, widthMeasureSpec);
        myheight = getMySize((int)(187*density), heightMeasureSpec);//40dp文字向上显示的+预留温度差最高5度，加上50dp，再加上温度文字大小
        mywidth = mywidth + getPaddingLeft() + getPaddingRight();
        myheight = myheight + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(mywidth, myheight);

    }


    private int getMySize(int defaultSize, int measureSpec) {
        int mySize = defaultSize;

        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case MeasureSpec.UNSPECIFIED: {//如果没有指定大小，就设置为默认大小
                mySize = defaultSize;
                break;
            }
            case MeasureSpec.AT_MOST: {//如果测量模式是wrap_content,取值为父view给定最大大小和默认大小的小得那个
                mySize = Math.min(defaultSize, size);
                break;
            }
            case MeasureSpec.EXACTLY: {//如果是固定的大小，那就不要去改变它，match_parent和指定具体大小
                mySize = size;
                break;
            }
        }
        return mySize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTimeLine(canvas);//绘制温度曲线
        drawText(canvas);//绘制具体温度文本
        drawCutLine(canvas,185);//绘制分割线
    }

    /**
     * 绘制温度曲线
     *
     * @param canvas  画布
     */

    private void drawTimeLine(Canvas canvas)
    {
        if(weatherHourlyTableList.size()<=0)//没有数据，返回不绘制
        {
            return;
        }

        PointF pointF;
        int i;
        for(i=0;i<pointList.size();i++)
        {
            myPath.reset();
            myPaint.setStyle(Paint.Style.FILL);
            pointF=pointList.get(i);
            canvas.drawCircle(pointF.x,pointF.y,12,myPaint);
            myPaint.setStyle(Paint.Style.STROKE);
            myPaint.setStrokeWidth(5);
            myPath.moveTo(pointF.x, pointF.y);//移动到第一个点
            if(i+1<pointList.size())
            {
                pointF = pointList.get(i + 1);
                myPath.lineTo(pointF.x,pointF.y);
                canvas.drawPath(myPath,myPaint);
            }
        }

    }


    /**
     * 绘制天气文本，获取文本的大小，是文字居中绘制
     *
     * @param canvas
     */
    private void drawText(Canvas canvas)
    {
        if(weatherHourlyTableList.size()<=0)//无数据。不绘制
        {
            return;
        }
        myPaint.setStyle(Paint.Style.FILL);
        PointF pointF;
        String text;
        for(int i=0;i<pointList.size();i++)
        {
            pointF=pointList.get(i);
            text=weatherHourlyTableList.get(i).getTemperature()+"℃";
            //使用了gettextsize方法获取文本的宽度，然后除而是文字居中绘制
            canvas.drawText(text,pointF.x-getTextSize(text,"width")/2,pointF.y-15*density,myPaint);
            text=weatherHourlyTableList.get(i).getTime().split(" ")[1];
            canvas.drawText(text,pointF.x-getTextSize(text,"width")/2,150*density,myPaint);
            text=weatherHourlyTableList.get(i).getWeather();
            canvas.drawText(text,pointF.x-getTextSize(text,"width")/2,170*density,myPaint);
        }

    }

    /**
     * 绘制分割线
     *
     * @param canvas     画布
     * @param position   绘制的位置
     */
    private void drawCutLine(Canvas canvas,int position)
    {
        if(weatherHourlyTableList.size()<=0)//list集合还没有赋值时不绘制
        {
            return;
        }
        cutLinePaint.setColor(Color.parseColor("#81ececec"));
        cutLinePaint.setStyle(Paint.Style.STROKE);
        cutLinePaint.setStrokeWidth(1);
        canvas.drawLine(0,position*density,width+181*density,position*density,cutLinePaint);
    }


    /**
     *
     * 根据数据设置具体的每个点的位置
     *
     * @param weatherHourlyTableList  天气数据源
     */
    public void setData(List<WeatherHourlyTable> weatherHourlyTableList)
    {
        this.weatherHourlyTableList=weatherHourlyTableList;//存储数据源
        List<String> temperatureStringList=new ArrayList<>();
        for(int i=0;i<weatherHourlyTableList.size();i++)//得到每个时间点的数据
        {
            temperatureStringList.add(weatherHourlyTableList.get(i).getTemperature());
        }
        //获取最大最小值，用于计算高度
        int max=Integer.valueOf(temperatureStringList.get(0)),min=Integer.valueOf(temperatureStringList.get(0));
        for(int i=1;i<temperatureStringList.size();i++)
        {
            if(Integer.valueOf(temperatureStringList.get(i))>max)
            {
                max=Integer.valueOf(temperatureStringList.get(i));
            }

            if(Integer.valueOf(temperatureStringList.get(i))<min)
            {
                min=Integer.valueOf(temperatureStringList.get(i));
            }
        }

        int singleHight=80/(max-min);//设置在限定变化范围之后每度天气应该占用的高度，以便适应极端天气，比如温度差10度的天气
        pointList=new ArrayList<>();//格式化后的点的集合list
        int singlesize=(int)(width+100*density)/(temperatureStringList.size()-1);//每个点的x轴的距离量，7个点就只需要6段所以减1

        for( int i=0;i<temperatureStringList.size();i++)//格式化每个点使其能够被绘制
        {
            PointF point = new PointF();
            point.x=outMetrics.density*30+singlesize*i;//x轴上的点间隔距离都是一样的，使用循环变量乘以距离量刚好
            point.y = outMetrics.density * 40 + Math.abs(Integer.valueOf(temperatureStringList.get(i)) - max) * singleHight *density;
            pointList.add(point);
        }
        invalidate();
    }

    /*获取文本宽度和高度*/
    private int getTextSize(String text, String type) {
        Rect rect = new Rect();
        myPaint.getTextBounds(text, 0, text.length(), rect);//通过构造能容纳下文本长度的矩形，获得文本的宽度高度
        int width = rect.width();
        int height = rect.height();
        if (type.equals("width")) {
            return width;
        } else if (type.equals("height")) {
            return height;
        }
        return 0;
    }
}
