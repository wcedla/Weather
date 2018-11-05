package com.wcedla.wcedlaweather.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.design.canvas.CanvasCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.wcedla.wcedlaweather.R;
import com.wcedla.wcedlaweather.db.WeatherForecastTable;
import com.wcedla.wcedlaweather.tool.SystemTool;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class DayLine extends View {

    TypedArray array;
    Paint myPaint;
    Paint cutLinePaint;
    Paint rainProbabilityPaint;
    DisplayMetrics outMetrics;
    int width,height;
    float density;
    float textSize;
    int textColor;
    List<PointF> pointList=new ArrayList<>();//格式化后的点的集合list
    List<WeatherForecastTable> weatherForecastTableList=new ArrayList<>();


    public DayLine(Context context) {
        this(context,null);
    }

    public DayLine(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DayLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DayLine, defStyleAttr, 0);
        initdata();
        array.recycle();
    }

    private void initdata()
    {
        outMetrics = getResources().getDisplayMetrics();//获取屏幕的参数矩阵
        width = outMetrics.widthPixels;//获取屏幕宽度
        height = outMetrics.heightPixels;//获取屏幕高度
        density=outMetrics.density;//获取分辨率
        textSize=array.getDimension(R.styleable.DayLine_LineTextSize,40);//获取文本大小属性
        textColor=array.getColor(R.styleable.DayLine_LineTextColor,Color.WHITE);//获取文本颜色

        myPaint=new Paint();
        cutLinePaint=new Paint();
        rainProbabilityPaint=new Paint();
        myPaint.setTextSize(textSize);
        myPaint.setDither(true);
        myPaint.setAntiAlias(true);
        myPaint.setStyle(Paint.Style.FILL);
        myPaint.setColor(textColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mywidth, myheight;
        mywidth = getMySize(width, widthMeasureSpec);
        myheight = getMySize(200*(int)density, heightMeasureSpec);//懒得具体测算了
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
        drawDate(canvas);//绘制日期
        drawIcon(canvas);//绘制天气图标
        drawWeatherText(canvas);//绘制天气文本
        drawCutLine(canvas,110);//绘制分割线
        drawRainProbability(canvas);//绘制降雨概率
        drawCutLine(canvas,170);//绘制分割线
    }

    /*绘制日期和星期*/
    private void drawDate(Canvas canvas)
    {
        if(weatherForecastTableList.size()<=0)//list集合还没有赋值时不绘制
        {
            return;
        }

        int singlesize=(int)(width-density*60)/6;//每个点的x轴的距离量，7个点就只需要6段所以减1
        pointList.clear();
        for( int i=0;i<7;i++)//格式化每个点使其能够被绘制
        {
            PointF point = new PointF();
            point.x = density * 30 + singlesize * i;//x轴上的点间隔距离都是一样的，使用循环变量乘以距离量刚好
            point.y = 15*density;
            pointList.add(point);
        }
        PointF pointF;
        String text;
        for(int i=0;i<7;i++)//遍历点的数值list
        {
            pointF = pointList.get(i);
            text=SystemTool.getWeek(weatherForecastTableList.get(i).getForecastDate());//获取星期数
            //如果是在曲线上部绘制文字，，绘制的时候要考虑一下绘制的文本的大小，使绘制文字居中，然后控制文字和曲线之间要有一定距离
            canvas.drawText(text, pointF.x - getTextSize(text, "width")/2 , pointF.y, myPaint);//绘制星期
            //获取table里面的实际日期2018-9-8，取后面两位的月份，去除年份
            text=weatherForecastTableList.get(i).getForecastDate().split("-")[1]+"-"+weatherForecastTableList.get(i).getForecastDate().split("-")[2];
            canvas.drawText(text, pointF.x - getTextSize(text, "width")/2 , pointF.y+20*density, myPaint);//绘制实际日日期
        }
    }


    /*绘制天气图标*/
    private void drawIcon(Canvas canvas)
    {
        if(weatherForecastTableList.size()<=0)//list集合还没有赋值时不绘制
        {
            return;
        }
        int singlesize=(int)(width-density*60)/6;//每个点的x轴的距离量，7个点就只需要6段所以减1
        pointList.clear();
        for( int i=0;i<7;i++)//格式化每个点使其能够被绘制
        {
            PointF point = new PointF();
            point.x = density * 30 + singlesize * i;//x轴上的点间隔距离都是一样的，使用循环变量乘以距离量刚好
            point.y = 30*density;
            pointList.add(point);
        }
        PointF pointF;
        for(int i=0;i<7;i++)
        {
            pointF=pointList.get(i);
            Bitmap weatherIcon = BitmapFactory.decodeResource(getResources(), SystemTool.getResourceByReflect("weather_" + weatherForecastTableList.get(i).getDayIconCode()));//解析xml中设定的图片为bitmap
            Bitmap newbitmap = changeBitmapSize(weatherIcon, 30 * density, 30 * density);//重新生成30*30的图片
            canvas.drawBitmap(newbitmap, pointF.x-newbitmap.getWidth()/2, pointF.y+newbitmap.getHeight()/2,myPaint);//绘制图片
        }
    }

    /*绘制天气文本*/
    private void drawWeatherText(Canvas canvas)
    {
        if(weatherForecastTableList.size()<=0)//list集合还没有赋值时不绘制
        {
            return;
        }
        int singlesize=(int)(width-density*60)/6;//每个点的x轴的距离量，7个点就只需要6段所以减1
        pointList.clear();
        for( int i=0;i<7;i++)//格式化每个点使其能够被绘制
        {
            PointF point = new PointF();
            point.x = density * 30 + singlesize * i;//x轴上的点间隔距离都是一样的，使用循环变量乘以距离量刚好
            point.y = 100*density;
            pointList.add(point);
        }
        PointF pointF;
        String text;
        for(int i=0;i<7;i++)
        {
            pointF=pointList.get(i);
            text=weatherForecastTableList.get(i).getDayWeatherText();//获取天气文本
            canvas.drawText(text,pointF.x-getTextSize(text,"width")/2,pointF.y-getTextSize(text,"height")/2,myPaint);//绘制文本
        }
    }

    private void drawCutLine(Canvas canvas,int position)
    {
        if(weatherForecastTableList.size()<=0)//list集合还没有赋值时不绘制
        {
            return;
        }
        cutLinePaint.setColor(Color.parseColor("#81ececec"));
        cutLinePaint.setStyle(Paint.Style.STROKE);
        cutLinePaint.setStrokeWidth(1);
        canvas.drawLine(0,position*density,width,position*density,cutLinePaint);
    }

    private void drawRainProbability (Canvas canvas)
    {
        if(weatherForecastTableList.size()<=0)//list集合还没有赋值时不绘制
        {
            return;
        }

        rainProbabilityPaint.setStyle(Paint.Style.FILL);
        rainProbabilityPaint.setColor(textColor);
        rainProbabilityPaint.setTextSize(45);
        rainProbabilityPaint.setDither(true);
        rainProbabilityPaint.setAntiAlias(true);
        canvas.drawText("降水概率",width/2-getTextSize("降水概率","width")/2,130*density,rainProbabilityPaint);
        int singlesize=(int)(width-density*60)/6;//每个点的x轴的距离量，7个点就只需要6段所以减1
        pointList.clear();
        for( int i=0;i<7;i++)//格式化每个点使其能够被绘制
        {
            PointF point = new PointF();
            point.x = density * 30 + singlesize * i;//x轴上的点间隔距离都是一样的，使用循环变量乘以距离量刚好
            point.y = 160*density;
            pointList.add(point);
        }
        PointF pointF;
        String text;
        for(int i=0;i<7;i++)
        {
            pointF=pointList.get(i);
            text=weatherForecastTableList.get(i).getWhetherRain()+"%";
            canvas.drawText(text,pointF.x-getTextSize(text,"width")/2,pointF.y,rainProbabilityPaint);


        }

    }

    /*外部接口，用于设置绘制点的数据源*/
    public void setData(List<WeatherForecastTable> forecastTableList)
    {
        this.weatherForecastTableList=forecastTableList;
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

    /*压缩图片，设置新尺寸*/
    private Bitmap changeBitmapSize(Bitmap myBitmap,float myWidth,float myHeight)
    {
        Bitmap bitmap = myBitmap;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        //计算压缩的比率
        float scaleWidth= myWidth /width;
        float scaleHeight= myHeight /height;

        //获取想要缩放的matrix
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);

        //获取新的bitmap
        bitmap=Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
        bitmap.getWidth();
        bitmap.getHeight();
        return bitmap;
    }
}
