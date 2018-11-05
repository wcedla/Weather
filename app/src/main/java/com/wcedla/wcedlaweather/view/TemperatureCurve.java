package com.wcedla.wcedlaweather.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.wcedla.wcedlaweather.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class TemperatureCurve extends View {

    int saveWidth,saveHeight;
    TypedArray array;
    Paint mypaint;
    Path mypath;
    DisplayMetrics outMetrics;
    int width,height;
    float density;
    float smoothness;
    int linecolor;
    int textcolor;
    Float textsize;
    String drawtype;

    int needHeight=50;

    List<PointF> pointList=new ArrayList<>();
    List<String> pointvaluelist=new ArrayList<>();

    public TemperatureCurve(Context context) {
        this(context,null);
    }

    public TemperatureCurve(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TemperatureCurve(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TemperatureCurve, defStyleAttr, 0);
        initdata();
        array.recycle();
    }

    /*初始化各种属性*/
    private void initdata()
    {
        mypaint=new Paint();
        mypath=new Path();
        outMetrics = getResources().getDisplayMetrics();//获取屏幕的参数矩阵
        width = outMetrics.widthPixels;//获取屏幕宽度
        height = outMetrics.heightPixels;//获取屏幕高度
        density=outMetrics.density;
        smoothness=0.35f;//曲线弯曲程度
        linecolor=array.getColor(R.styleable.TemperatureCurve_LineColor,Color.WHITE);//曲线颜色
        textcolor=array.getColor(R.styleable.TemperatureCurve_TextColor,Color.WHITE);//温度文字曲线
        textsize=array.getDimension(R.styleable.TemperatureCurve_TmpTextSize, density * 10);//温度文字大小

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        saveWidth=widthMeasureSpec;
        saveHeight=heightMeasureSpec;
        int mywidth, myheight;
        mywidth = getMySize(width, widthMeasureSpec);
        myheight = getMySize((int)((density*(90))+textsize), heightMeasureSpec);//40dp文字向上显示的+预留温度差最高5度，加上50dp，再加上温度文字大小
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
        drawBendLine(canvas);
        drawtext(canvas);
    }


    /*画曲线*/
    private void drawBendLine(Canvas canvas)
    {
        if(pointList.size()<=0)//如果点的ist还没有赋值则不绘制曲线
        {
            //Log.d(TAG, "数组为空"+pointList);
            return;
        }

        float offectx = 0;//设置控制点位置偏移量，
        float offecty = 0;

        mypaint.setAntiAlias(true);
        mypaint.setDither(true);
        mypaint.setStyle(Paint.Style.STROKE);
        mypaint.setStrokeWidth(3);
        mypaint.setColor(linecolor);
        mypaint.setStrokeCap(Paint.Cap.ROUND);
        mypath.reset();

        mypath.moveTo(pointList.get(0).x, pointList.get(0).y);//移动到第一个点

        for (int i = 1; i < pointList.size(); i++)//从第二个点开始循环
        {
            PointF beconnectpoint = pointList.get(i);//即将要连接的点

            PointF previouspoint = pointList.get(i - 1);//即将要连接的点的前一个点
            float firstcontrolpointx = previouspoint.x + offectx;//第一个控制点
            float firstcontrolpointy = previouspoint.y + offecty;

            PointF nextpoint = pointList.get(i + 1 < pointList.size() ? i + 1 : i);//即将要连接的点的下一个点，如果已经是最后一个点了，则就选择最后一个点
            offectx = (nextpoint.x - previouspoint.x) / 2 * smoothness;//即将要连接的点的前一个点和后一个点的距离的一半乘以曲线的弯曲程度
            offecty = (nextpoint.y - previouspoint.y) / 2 * smoothness;
            float secondcontrolpointx = beconnectpoint.x - offectx;//第二个控制点
            float secondcontrolpointy = beconnectpoint.y - offecty;

            if (firstcontrolpointy == beconnectpoint.y) {//如果第一个控制点的y轴数值与即将连接的点的y轴数值一样，则让第二个控制点得到y轴数值也等于即将连接的点的y轴数值
                secondcontrolpointy = firstcontrolpointy;
            }

            mypath.cubicTo(firstcontrolpointx, firstcontrolpointy, secondcontrolpointx, secondcontrolpointy, beconnectpoint.x, beconnectpoint.y);//贝塞尔曲线
        }
        canvas.drawPath(mypath, mypaint);
    }


    /*画温度数值文字*/
    private void drawtext(Canvas canvas)
    {
        if(pointvaluelist.size()<=0)//如果点的list还没有复制则不绘制温度文字
        {
            //Log.d(TAG, "数组为空"+pointvaluelist);
            return;
        }
        mypaint.setColor(textcolor);
        mypaint.setStyle(Paint.Style.FILL);
        mypaint.setTextSize(textsize);
        PointF pointF;
        String text;
        for(int i=0;i<pointvaluelist.size();i++)//遍历点的数值list
        {
            pointF = pointList.get(i);
            text=pointvaluelist.get(i)+"℃";
            if(drawtype.equals("up")) {//如果是在曲线上部绘制文字，，绘制的时候要考虑一下绘制的文本的大小，使绘制文字居中，然后控制文字和曲线之间要有一定距离
                canvas.drawText(text, pointF.x - getTextSize(text, "width")/2 , pointF.y - getTextSize(text, "height") - density * 5, mypaint);
            }
            else if(drawtype.equals("down"))//如果是在曲线下面绘制温度文字，然后下部绘制文字要考虑到绘制文本的方法是比较特殊的，坐标位置表示的是标准线不是文字中线，然后控制文字和曲线之间要有一定距离
            {
                canvas.drawText(text, pointF.x - getTextSize(text, "width")/2 , pointF.y + getTextSize(text, "height")*2  + density * 5, mypaint);
            }
        }
    }


    /*赋值点的集合*/
    public void setData(List<String> pointstringlist,String type)
    {
        pointvaluelist=pointstringlist;//保存点的集合list
        drawtype=type;//保存文字的绘制方向
        /*获取到绘制数据的最大值最小值*/
        int max=Integer.valueOf(pointstringlist.get(0)),min=Integer.valueOf(pointstringlist.get(0));
        for(int i=1;i<pointstringlist.size();i++)
        {
            if(Integer.valueOf(pointstringlist.get(i))>max)
            {
                max=Integer.valueOf(pointstringlist.get(i));
            }

            if(Integer.valueOf(pointstringlist.get(i))<min)
            {
                min=Integer.valueOf(pointstringlist.get(i));
            }
        }
       // Log.d(TAG, "最大最小值 "+max+","+min);
        int singleHight=50/(max-min);//设置在限定变化范围之后每度天气应该占用的高度，以便适应极端天气，比如温度差10度的天气
        pointList=new ArrayList<>();//格式化后的点的集合list
        int singlesize=(int)(width-density*60)/(pointstringlist.size()-1);//每个点的x轴的距离量，7个点就只需要6段所以减1
       // Log.d(TAG, "距离量"+(int)(singlesize/density));
        for( int i=0;i<pointstringlist.size();i++)//格式化每个点使其能够被绘制
        {
            PointF point = new PointF();
            point.x=outMetrics.density*30+singlesize*i;//x轴上的点间隔距离都是一样的，使用循环变量乘以距离量刚好
            if(drawtype.equals("up")) {//如果是在曲线上部绘制文字，则上部空间应该要比较多，然后温度差为1度乘以10dp
                point.y = outMetrics.density * 40 + Math.abs(Integer.valueOf(pointstringlist.get(i)) - max) * singleHight *density;
            }else if(drawtype.equals("down"))//如果是在曲线下面绘制温度文字，则上部空间不要那么多，温度差为1度乘以10dp
            {
                point.y = outMetrics.density * 20 + Math.abs(Integer.valueOf(pointstringlist.get(i)) - max) * singleHight*density;
            }
            //Log.d(TAG, "单个"+i+","+(int)(point.x/density)+","+(int)(point.y/density));
            pointList.add(point);
        }
        invalidate();//赋值完成之后重绘曲线
    }

    /*设置曲线的弯曲程度*/
    public void setSmoothness(float size)
    {
        this.smoothness=size;
    }

    /*获取文本宽度和高度*/
    private int getTextSize(String text, String type) {
        Rect rect = new Rect();
        mypaint.getTextBounds(text, 0, text.length(), rect);//通过构造能容纳下文本长度的矩形，获得文本的宽度高度
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

