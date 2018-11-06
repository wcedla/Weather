package com.wcedla.wcedlaweather.view;

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
import android.util.Log;
import android.view.View;

import com.wcedla.wcedlaweather.R;

import static org.litepal.LitePalBase.TAG;

public class SwitchButton extends View {

    TypedArray array;
    Paint myPaint;
    Paint circlePaint;
    Path myPath;
    int width,height;
    float density;


    public SwitchButton(Context context) {
        this(context,null);
    }

    public SwitchButton(Context context,AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SwitchButton(Context context,AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SunCustomView, defStyleAttr, 0);
        initData();//初始化各种属性
        array.recycle();//注意回收内存，防止内存泄漏
    }

    private void initData()
    {
        DisplayMetrics outMetrics = getResources().getDisplayMetrics();//获取屏幕的参数矩阵
        width = outMetrics.widthPixels;//获取屏幕宽度
        height = outMetrics.heightPixels;//获取屏幕高度
        density=outMetrics.density;
        myPaint=new Paint();
        circlePaint=new Paint();
        myPath=new Path();
        myPaint.setAntiAlias(true);
        myPaint.setDither(true);
        myPaint.setColor(Color.BLACK);
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(5);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setAntiAlias(true);
        circlePaint.setDither(true);
        circlePaint.setColor(getResources().getColor(R.color.colorPrimary));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



    }

    private void drawShape(Canvas canvas)
    {
        RectF frontRectf=new RectF(density*20,density*30,density*40,density*50);
        canvas.drawArc(frontRectf,90,180,false,myPaint);
        RectF backRectf=new RectF(density*46,density*30,density*66,density*50);
        canvas.drawArc(backRectf,270,180,false,myPaint);
        myPath.moveTo(density*30,density*30);
        myPath.lineTo(density*58,density*30);
        myPath.moveTo(density*30,density*50);
        myPath.lineTo(density*58,density*50);
        canvas.drawPath(myPath,myPaint);
    }

    private void drawCircle(Canvas canvas)
    {
        canvas.drawCircle(density*56,density*40,density*7,circlePaint);
    }

    ValueAnimator slideAnimator;
    float changeLength;
    int xPosition;
    float circleRadius;
    boolean isSelect=true;


    private void setAnimation(float startPosition, float destPosition, int duration) {
        slideAnimator= ValueAnimator.ofFloat(startPosition, destPosition);
        slideAnimator.setDuration(duration);
        slideAnimator.setTarget(destPosition);
        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //每次要绘制的圆弧角度
                changeLength = (float) animation.getAnimatedValue();//变换的度数
                invalidateView();
            }

        });
        slideAnimator.start();//开启动画
    }

    private void invalidateView()
    {
       circleRadius=changeLength-40*density;
        Log.d(TAG, "圆半径"+circleRadius);
    }

    public void setCheck(boolean checked)
    {
        if(checked)
        {
            setAnimation(30*density,56*density,2000);
        }
    }
}
