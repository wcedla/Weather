package com.wcedla.wcedlaweather.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.wcedla.wcedlaweather.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class SunCustomView extends View {

    Paint mypaint;
    TypedArray array;
    int sunsrc;
    Bitmap sunicon;
    int width;
    int height;
    int radius;
    float changedangle;
    float sunx, suny;
    String sunraisetime;
    String sunsettime;
    String nowtime;
    int suntextcolor;
    float suntextsize;
    RectF circleRectF;
    RectF circlebackground;
    float finalangle;
    ValueAnimator sunAnimator;


    public SunCustomView(Context context) {
        this(context, null);//全部转到第三种构造方法中
    }

    public SunCustomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);//全部转到第三种构造方法中
    }

    public SunCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        /*获取xml文件中的属性数组*/
        array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SunCustomView, defStyleAttr, 0);
        initData();//初始化各种属性
        array.recycle();//注意回收内存，防止内存泄漏
    }

    private void initData() {
        mypaint = new Paint();//初始化画笔
        DisplayMetrics outMetrics = getResources().getDisplayMetrics();//获取屏幕的参数矩阵
        width = outMetrics.widthPixels;//获取屏幕宽度
        height = outMetrics.heightPixels;//获取屏幕高度
        sunsrc = array.getResourceId(R.styleable.SunCustomView_SunSrc, R.drawable.sun);//获取xml文件设置的图片id
        sunicon = BitmapFactory.decodeResource(getResources(), sunsrc);//解析xml中设定的图片为bitmap
        /*获取曲线的半径，默认值为150dp，math内为dp到px的转变*/
        radius = (int) array.getDimension(R.styleable.SunCustomView_Radius, Math.round(outMetrics.density * 150));
        /*获取设置日出日落时间文本颜色，默认为白色*/
        suntextcolor = array.getColor(R.styleable.SunCustomView_SunTextColor, Color.WHITE);
        /*获取设置日落日出文本的文本大小，默认为12dp，math内为dp转px*/
        suntextsize = array.getDimension(R.styleable.SunCustomView_SunTextSize, Math.round(outMetrics.density * 12));
        /*设置太阳的中心点在半圆的左起点，注意bitmap绘图是从图片左上角开始画的，所以图片中心位置需要减去图片高宽的一半；注意顶部预留的空间*/
        sunx = width / 2 - radius - sunicon.getWidth() / 2;
        suny = radius - sunicon.getHeight() / 2 + sunicon.getHeight();
        /*设置画半圆的矩阵，应该是个正方形，注意顶部预留的空间*/
        circleRectF = new RectF(width / 2 - radius, sunicon.getHeight(), width / 2 + radius, radius * 2 + sunicon.getHeight());

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mywidth, myheight;
        mywidth = getMySize(width, widthMeasureSpec);
        myheight = getMySize(radius + 300, heightMeasureSpec);//懒得具体测算了，300px差不多刚好了
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
        drawSemicircle(canvas);//画半圆
        //drawBottomLine(canvas);//画圆底部的线，美化作用
        drawText(canvas);//画日出日落的时间文字
        drawCircleBackground(canvas);//画半圆底部的背影
        drawSunPosition(canvas);//画太阳，在圆底部背影之后画太阳，为了不遮挡显示太阳
    }

    /*
    画半圆方法，主要使用循环绘制虚线效果
    * */
    private void drawSemicircle(Canvas canvas) {
        mypaint.setStyle(Paint.Style.STROKE);//画空心的半圆
        mypaint.setStrokeWidth(5);//画的弧线线多宽
        mypaint.setDither(true);//防止抖动
        mypaint.setColor(Color.WHITE);//画白色的弧线
        int startangle = 181;//初始从181度开始，美观作用
        while (startangle < 360)//半个圆
        {
            /*画半圆，且每隔一段距离画一次，实现虚线效果*/
            canvas.drawArc(circleRectF, startangle, 3, false, mypaint);
            startangle += 7;//跳过一定绘制距离实现虚线效果
        }
    }

    /*底部黑线，美化作用*/
    private void drawBottomLine(Canvas canvas) {
        mypaint.setColor(Color.parseColor("#81ececec"));//画黑线
        mypaint.setStyle(Paint.Style.STROKE);
        mypaint.setStrokeWidth(1);//不要太粗
        Path mypath = new Path();//路径
        mypath.moveTo(0, radius + sunicon.getHeight() + 1);
        mypath.lineTo(width, radius + sunicon.getHeight() + 1);
        canvas.drawPath(mypath, mypaint);
    }

    /*将太阳显示在半圆上，主要是控制sunx和suny*/
    private void drawSunPosition(Canvas canvas) {
        canvas.drawBitmap(sunicon, sunx, suny, mypaint);
    }

    /*画半圆背景*/
    private void drawCircleBackground(Canvas canvas) {
        canvas.save();//先保存一下当前画布的属性
        mypaint.setStyle(Paint.Style.FILL);//填充背景
        mypaint.setColor(Color.parseColor("#F2828488"));//设置填充色
        /*比画圆的矩形小一点，小了5px，因为要显示出画的弧线，美观作用*/
        circlebackground = new RectF(width / 2 - radius + 5, sunicon.getHeight() + 5, width / 2 + radius - 5, radius * 2 + sunicon.getHeight());
        /*大于90度之后，如果不控制矩形的高度，那么高度会一直减小，达不到指定的要求*/
        if (changedangle <= 90) {
            /*关键的方法，就是指定能绘制的大小，然后最后一个参数指定画完之后与之前所画的画布的合并方式，取交集就能实现需要的效果*/
            canvas.clipRect(width / 2 - radius, suny + sunicon.getHeight() / 2, sunx + sunicon.getWidth() / 2, sunicon.getHeight() + radius, Region.Op.INTERSECT);
        } else {
            canvas.clipRect(width / 2 - radius, sunicon.getHeight(), sunx + sunicon.getWidth() / 2, sunicon.getHeight() + radius, Region.Op.INTERSECT);
        }
        /*控制好绘制大小之后，就画一个半圆，达到填充之前画的半圆的效果*/
        canvas.drawArc(circlebackground, 180, 180, false, mypaint);
        canvas.restore();//画完了，就恢复之前保存的画布状态
    }

    /*画日出日落的文本*/
    private void drawText(Canvas canvas) {
        String leftstr = "日出时间:" + sunraisetime;//日出时间文本
        String rightstr = "日落时间:" + sunsettime;//日落时间文本
        mypaint.setColor(suntextcolor);//根据xml文件设置的颜色设置文本颜色
        mypaint.setTextSize(suntextsize);//根据xml文件设置的文本大小设置文本大小
        mypaint.setStyle(Paint.Style.FILL);//设置填充文本
        /*绘制的距离用到了测量文本宽度高度的方法，主要是为了排版好看，不能遮挡太阳*/
        canvas.drawText(leftstr, width / 2 - radius + sunicon.getWidth() / 2, radius + sunicon.getHeight() + getTextSize(leftstr, "height") + sunicon.getHeight() / 4, mypaint);
        canvas.drawText(rightstr, width / 2 + radius - sunicon.getWidth() / 2 - getTextSize(rightstr, "width"), radius + sunicon.getHeight() + getTextSize(leftstr, "height") + sunicon.getHeight() / 4, mypaint);
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

    /*设置属性动画，改变度数的时候改变太阳的位置，实现动画效果*/
    private void setAnimation(float startAngle, float currentAngle, int duration) {
        sunAnimator= ValueAnimator.ofFloat(startAngle, currentAngle);
        sunAnimator.setDuration(duration);
        sunAnimator.setTarget(currentAngle);
        sunAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //每次要绘制的圆弧角度
                changedangle = (float) animation.getAnimatedValue();//变换的度数
                invalidateView();
            }

        });
        sunAnimator.start();//开启动画
    }

    private void stopAnimation()
    {
        sunAnimator.cancel();
    }

    /*重绘太阳的坐标位置*/
    private void invalidateView() {

        //绘制太阳的x坐标和y坐标
        sunx = width / 2 - (float) (radius * Math.cos((changedangle) * Math.PI / 180)) - sunicon.getWidth() / 2;
        suny = radius - (float) (radius * Math.sin((changedangle) * Math.PI / 180)) - sunicon.getHeight() / 2 + sunicon.getHeight();
        invalidate();//刷新绘制，重新执行ondraw()方法
    }


    /*设置日落日出的时间，然后开始绘制太阳到指定位置*/
    public void setTime(String sunrasisetime, String sunsettime) {
        this.sunraisetime = sunrasisetime;
        this.sunsettime = sunsettime;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        nowtime = (simpleDateFormat.format(date));//系统当前时间
        int nowtimehour = Integer.valueOf(nowtime.split(":")[0]);//当前时间小时
        int nowtimeminute = Integer.valueOf(nowtime.split(":")[1]);//当前时间分钟
        int sunraisehour = Integer.valueOf(sunrasisetime.split(":")[0]);//日出时间小时
        int sunraiseminute = Integer.valueOf(sunrasisetime.split(":")[1]);//日出时间分钟
        int sunsethour = Integer.valueOf(sunsettime.split(":")[0]);//日落时间小时
        int sunsetminute = Integer.valueOf(sunsettime.split(":")[1]);//日落时间分钟
        int totalminute = sunsethour * 60 + sunsetminute - sunraisehour * 60 - sunraiseminute;//日落日出之间的总分钟数
        int nowminute = nowtimehour * 60 + nowtimeminute - sunraisehour * 60 - sunraiseminute;//当前时间距离日出时间的分钟数
        float passpencent = ((float) nowminute) / totalminute;//当前时间距离日出时间的分钟数与日落距离日出时间的分钟数的比值
        if (passpencent > 1)//如果当前已经大于日落时间了，不能绘制大于半个圆
        {
            passpencent = 1f;
        }
        if (passpencent < 0)//如果还没到日出时间那么就显示在半圆起点
        {
            passpencent = 0f;
        }
        finalangle = passpencent * 180;//得到具体的度数
        if(sunAnimator!=null)
        {
            stopAnimation();
        }
        setAnimation(0, finalangle, 5000);//开始绘制
    }

    public void resetSun()
    {
        if(sunAnimator!=null)
        {
            stopAnimation();
        }
        setAnimation(0, 0, 100);//开始绘制
    }
}