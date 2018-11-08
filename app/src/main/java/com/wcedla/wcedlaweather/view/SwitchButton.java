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
import android.view.animation.LinearInterpolator;

import com.wcedla.wcedlaweather.R;

import static org.litepal.LitePalBase.TAG;

public class SwitchButton extends View {

    TypedArray array;
    Paint myPaint;
    Paint circlePaint;
    Path myPath;
    int width, height;
    float density;
    int shapeColor;
    int circleColor;
    ValueAnimator slideAnimator;
    float changeLength;
    float startPosition;
    float circleRadius;
    boolean isSelect = true;

    public boolean isSelect() {
        return isSelect;
    }


    public SwitchButton(Context context) {
        this(context, null);
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SwitchButton, defStyleAttr, 0);
        initData();//初始化各种属性
        array.recycle();//注意回收内存，防止内存泄漏
    }

    private void initData() {
        shapeColor = array.getColor(R.styleable.SwitchButton_ShapeColor, Color.LTGRAY);//获取底层圆条颜色
        circleColor = array.getColor(R.styleable.SwitchButton_CircleColor, Color.GREEN);//获取圆条里面的圆的颜色
        Log.d(TAG, "initData: "+circleColor);
        DisplayMetrics outMetrics = getResources().getDisplayMetrics();//获取屏幕的参数矩阵
        width = outMetrics.widthPixels;//获取屏幕宽度
        height = outMetrics.heightPixels;//获取屏幕高度
        density = outMetrics.density;//获取屏幕像素
        myPaint = new Paint();//初始化底层圆条的画笔
        circlePaint = new Paint();//初始化圆条里面的圆的画笔
        myPath = new Path();//初始化圆条的两条横线
        myPaint.setAntiAlias(true);
        myPaint.setDither(true);
        myPaint.setColor(shapeColor);
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(5);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setAntiAlias(true);
        circlePaint.setDither(true);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mywidth, myheight;
        mywidth = getMySize(50 * (int) density, widthMeasureSpec);
        myheight = getMySize(25 * (int) density, heightMeasureSpec);//懒得具体测算了，25px差不多刚好了
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
        drawShape(canvas);
        drawCircle(canvas);


    }

    /**
     * 画底层圆条的方法
     *
     * @param canvas
     */
    private void drawShape(Canvas canvas) {
        //初始化前面的半圆和后面的半圆的矩形，并使用画椭圆的方法画出两个半圆，然后使用path的lineto连接两个半圆
        RectF frontRectf = new RectF(density * 2, density * 2, density * 22, density * 22);
        canvas.drawArc(frontRectf, 90, 180, false, myPaint);
        RectF backRectf = new RectF(density * 28, density * 2, density * 48, density * 22);
        canvas.drawArc(backRectf, 270, 180, false, myPaint);
        myPath.moveTo(density * 12, density * 2);
        myPath.lineTo(density * 38, density * 2);
        myPath.moveTo(density * 12, density * 22);
        myPath.lineTo(density * 38, density * 22);
        canvas.drawPath(myPath, myPaint);
    }


    /**
     * 画圆条里面的圆的方法
     *
     * @param canvas
     */
    private void drawCircle(Canvas canvas) {
        if (startPosition == 0)//就是一开始并未触发动画的时候，就是当前未改变状态时
        {
            if (isSelect)//如果当前状态是打开状态
            {
                circlePaint.setColor(circleColor);
                canvas.drawCircle(38 * density, 12 * density, 7 * density, circlePaint);
            } else//如果当前是关闭状态
            {
                circlePaint.setColor(Color.LTGRAY);
                canvas.drawCircle(12 * density, 12 * density, 7 * density, circlePaint);
            }
        }
        //如果触发了动画，就是需要更改状态了，就开始动态改变圆条里面的圆形的x轴位置以及半径
        else if (startPosition > 0) {
            canvas.drawCircle(changeLength, 12 * density, circleRadius * density, circlePaint);
        }
    }


    /**
     * 状态改变时的动画
     *
     * @param startPosition 开始x轴位置
     * @param destPosition  结束x轴位置
     * @param duration      动画时间
     */
    private void setAnimation(float startPosition, float destPosition, int duration) {
        if (slideAnimator != null)//如果之前已经有动画了，就先关闭动画，就是你快速切换状态时的情况
            slideAnimator.cancel();
        slideAnimator = ValueAnimator.ofFloat(startPosition, destPosition);
        slideAnimator.setDuration(duration);
        slideAnimator.setTarget(destPosition);
        slideAnimator.setInterpolator(new LinearInterpolator());//线性差速器
        this.startPosition = startPosition;//保存开始x轴位置用来判断是否开启了动画
        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //每次移动结束后的x轴位置
                changeLength = (float) animation.getAnimatedValue();//变换的度数
                invalidateView();//改变绘制条件
            }
        });
        slideAnimator.start();//开启动画
    }


    /**
     * 计算绘制圆条里面的圆形的位置以及半径
     */
    private void invalidateView() {
        //根据位置获取圆的半径，获取两圆心之间的距离的一半，然后在计算成具体的半径值
        circleRadius = Math.round(Math.abs((changeLength - 25 * density) / (2 * density)));
        if (changeLength < 25 * density)//如果是打开状态到关闭状态时画灰色的圆
        {
            circlePaint.setColor(Color.LTGRAY);
        } else if (changeLength > 25 * density)//如果是从关闭状态到打开状态时，根据设置的颜色绘制圆形
        {
            circlePaint.setColor(circleColor);
        }
        //Log.d(TAG, "圆半径"+circleRadius);
        invalidate();//重绘图形
    }

    /**
     * 外部接口，用于设置滑动按钮的状态
     *
     * @param checked 状态,true或者false
     */
    public void setCheck(boolean checked) {
        if (!isSelect)//如果当前是关闭状态
        {
            //更改状态为打开状态
            if (checked) {
                isSelect = true;//置标志位为true
                setAnimation(12 * density, 38 * density, 300);
            }
        } else if (isSelect)//如果当前状态时打开状态
        {
            //更改状态为关闭状态
            if (!checked) {
                isSelect = false;//标志位置false
                setAnimation(38 * density, 12 * density, 300);
            }
        }
    }
}
