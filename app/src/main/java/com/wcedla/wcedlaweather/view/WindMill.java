package com.wcedla.wcedlaweather.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.wcedla.wcedlaweather.R;

public class WindMill extends View {

    TypedArray array;
    Paint mypaint;
    Path mypath;
    int width;
    int height;
    int radius;
    int centerx,centery;
    DisplayMetrics outMetrics;
    RectF stick;
    float angle;
    float speed;

    public WindMill(Context context) {
        this(context,null);
    }

    public WindMill(Context context,AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WindMill(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WindMill, defStyleAttr, 0);
        initdata();
        array.recycle();
    }

    private void initdata()
    {
        mypaint=new Paint();
        mypath=new Path();
        outMetrics = getResources().getDisplayMetrics();//获取屏幕的参数矩阵
        width = outMetrics.widthPixels;//获取屏幕宽度
        height = outMetrics.heightPixels;//获取屏幕高度
        radius=(int)array.getDimension(R.styleable.WindMill_WindmillRadius,Math.round(outMetrics.density*25));
        stick=new RectF(-radius*1/3,0,radius*1/3,radius*3.5f);
        angle=0;
        speed=array.getFloat(R.styleable.WindMill_Speed,3000);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mywidth, myheight;
        mywidth = getMySize(4*radius+Math.round(outMetrics.density*20), widthMeasureSpec);
        myheight = getMySize(radius*35/6+Math.round(outMetrics.density*20), heightMeasureSpec);//懒得具体测算了，300px差不多刚好了
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
        drawWindmill(canvas);
    }

    private void drawWindmill(Canvas canvas)
    {
        centerx=getWidth()/2;//设置圆心位置，也就是风车的旋转中心
        centery=Math.round(outMetrics.density*10)+2*radius;
        canvas.translate(centerx,centery);//将画布坐标轴移动到风车中心点，为了能够达到旋转的动画移动坐标轴
        mypaint.setStyle(Paint.Style.FILL);//填充
        mypaint.setColor(Color.parseColor("#e9e8e8"));//画杆子颜色
        canvas.drawRect(stick,mypaint);//画杆子
        canvas.drawCircle(0,radius*3.5f,radius*1/3,mypaint);//美化杆子，在底部加一个半圆
        canvas.rotate(angle);//用于实现动画的作用，默认值为0
        mypaint.setDither(true);//防止抖动
        mypaint.setAntiAlias(true);//抗锯齿
        mypath.reset();//路径设置重置
        mypaint.setColor(Color.parseColor("#f19b12"));
        mypath.moveTo(0,0);
        mypath.lineTo(-radius,0);
        mypath.lineTo(-radius,-radius);
        mypath.close();//闭合路径
        canvas.drawPath(mypath,mypaint);
        mypath.reset();
        mypaint.setColor(Color.parseColor("#efc30f"));
        mypath.moveTo(-radius,-radius);
        mypath.lineTo(0,-2*radius);
        mypath.lineTo(0,0);
        mypath.close();
        canvas.drawPath(mypath,mypaint);
        mypath.reset();
        mypaint.setColor(Color.parseColor("#169f84"));
        mypath.moveTo(0,0);
        mypath.lineTo(0,0-radius);
        mypath.lineTo(0+radius,0-radius);
        mypath.close();
        canvas.drawPath(mypath,mypaint);
        mypath.reset();
        mypaint.setColor(Color.parseColor("#1abb9b"));
        mypath.moveTo(0+radius,0-radius);
        mypath.lineTo(0+2*radius,0);
        mypath.lineTo(0,0);
        mypath.close();
        canvas.drawPath(mypath,mypaint);
        mypath.reset();
        mypaint.setColor(Color.parseColor("#27ad60"));
        mypath.moveTo(0,0);
        mypath.lineTo(0+radius,0);
        mypath.lineTo(0+radius,0+radius);
        mypath.close();
        canvas.drawPath(mypath,mypaint);
        mypath.reset();
        mypaint.setColor(Color.parseColor("#2ecb71"));
        mypath.moveTo(0+radius,0+radius);
        mypath.lineTo(0,0+2*radius);
        mypath.lineTo(0,0);
        mypath.close();
        canvas.drawPath(mypath,mypaint);
        mypath.reset();
        mypaint.setColor(Color.parseColor("#bf392b"));
        mypath.moveTo(0,0);
        mypath.lineTo(0,0+radius);
        mypath.lineTo(0-radius,0+radius);
        mypath.close();
        canvas.drawPath(mypath,mypaint);
        mypath.reset();
        mypaint.setColor(Color.parseColor("#e64c3c"));
        mypath.moveTo(0-radius,0+radius);
        mypath.lineTo(0-2*radius,0);
        mypath.lineTo(0,0);
        mypath.close();
        canvas.drawPath(mypath,mypaint);
        mypaint.setColor(Color.parseColor("#e9e8e8"));
        canvas.drawCircle(0,0,radius*1/10,mypaint);
    }

    /*开启动画，实则就是获取0度到360度的变化值用于旋转画布达到旋转效果 ，在开启无限循环*/
    private void setAnimation() {
        ValueAnimator windmillAnimator = ValueAnimator.ofFloat(0, 360);
        windmillAnimator.setDuration((long)speed);
        windmillAnimator.setTarget(angle);//设置变化对象为角度
        windmillAnimator.setInterpolator(new LinearInterpolator());//线性差值器，匀速转动
        windmillAnimator.setRepeatCount(ObjectAnimator.INFINITE);//无限循环
        windmillAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                angle = (float) animation.getAnimatedValue();//变换的度数，画布旋转的度数
                postInvalidate();//重绘风车
            }

        });
        windmillAnimator.start();//开启动画
    }

    public void startWindmill()
    {
        setAnimation();
    }

}
