package com.wcedla.wcedlaweather.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.wcedla.wcedlaweather.R;

import static android.support.constraint.Constraints.TAG;
import static android.view.MotionEvent.ACTION_POINTER_DOWN;

/**
 *
 * 支持滑动的viewgroup
 *
 */

public class SlideLayout extends FrameLayout {

    private int menuWidth=0;//菜单栏的宽度也就是右划呼出的view的宽度

    private int width,height;
    private DisplayMetrics outMetrics;

    //滑动器
    private Scroller scroller;

    public SlideLayout(Context context) {
        this(context,null);
    }

    public SlideLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SlideLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
        scroller = new Scroller(context);
    }
    private void initData()
    {
        outMetrics = getResources().getDisplayMetrics();//获取屏幕的参数矩阵
        width = outMetrics.widthPixels;//获取屏幕宽度
        height = outMetrics.heightPixels;//获取屏幕高度
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        menuWidth=0;
        int childCount = getChildCount();
        int myLeft = getPaddingLeft();
        int myRight = 0;
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() != GONE) {
                if (i == 0) {//第一个子View是内容 宽度设置为全屏
                    childView.layout(myLeft, getPaddingTop(), myLeft + width, getPaddingTop() + childView.getMeasuredHeight());
                    myLeft = myLeft + width;
                } else {
                    //if (true) {
                        //if(layoutCount<1)
                    menuWidth+=childView.getMeasuredWidth();//设置菜单位置为左边主题文本的宽度
                    childView.layout(myLeft, getPaddingTop(), myLeft + childView.getMeasuredWidth(), getPaddingTop() + childView.getMeasuredHeight());
                    myLeft = myLeft + childView.getMeasuredWidth();//设置最新的左边位置
                   // } else {
//                        childView.layout(right - childView.getMeasuredWidth(), getPaddingTop(), right, getPaddingTop() + childView.getMeasuredHeight());
//                        right = right - childView.getMeasuredWidth();
                    //}

                }
            }
        }
    }

    private int startX;
    private int startY;

    private int downX;
    private int downY;

    int moveX;
    Boolean isMove=false;


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = downX = (int)ev.getX();
                startY = downY = (int)ev.getY();
                //如果当前状态为布局已经滑动，并且点击的位置是左边文本部分，则消耗本次点击，即使点击无效化
                if(getScrollX()>0&&startX<(width-menuWidth))
                {
                    return true;
                }
                //点击是判断如果点击的不是同一个view就会关闭上一个view，多指屏蔽在上一层父布局，即listview设置系统属性了
                if (onStateChangeListener != null)
                {
                    onStateChangeListener.onMove(this);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                moveX = (int) ev.getX();
                //如果滑动距离超过5的话就阻止点击事件继续下传，有swiplayout消耗本次事件，即响应滑动操作
                if (Math.abs(moveX - downX) > 5) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onInterceptTouchEvent(ev);//左右滑动一般情况下会不中断让子控件运行，上下滑动到不了这一层
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                downX = startX = (int)event.getX();
                downY = startY = (int)event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float endX = event.getX();
                float endY = event.getY();
                //计算偏移量
                float distanceX = endX - startX;//滑动的距离
                int toScrollX = (int) (getScrollX()-distanceX);//滑动终点
                //屏蔽非法值
                if (toScrollX < 0 )
                {
                    toScrollX = 0;
                }
                if (toScrollX > menuWidth)
                {
                    toScrollX = menuWidth;
                }
                //getScrollX表示当前已经滑动的距离
                scrollTo(toScrollX,getScrollY());

                startX = (int)event.getX();//默认将当前滑动结束点当成下一次的开始点

                float dx = Math.abs(event.getX()-downX);
                float dy = Math.abs(event.getY()-downY);
                if (dy > 0 && dx > 6)
                {
                    //事件反拦截，使父ListView的事件传递到自身SlideLayout，使屏幕滑出后不能上下滑动
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                //设置布局自动滑出，或者自动关闭
                if (getScrollX() > menuWidth/2)
                {
                    openMenu();
                }else {
                    closeMenu();
                }
                break;
        }
        return true;
    }


    /**
     * 打开menu菜单
     */
    public void openMenu() {
        int dx = menuWidth-getScrollX();
        scroller.startScroll(getScrollX(), getScrollY(),dx, getScrollY());
        if (onStateChangeListener != null)
        {
            onStateChangeListener.onOpen(this);
        }
        invalidate();
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        //0表示menu移动到的目标距离,目标位置-起始位置
        int dx = 0-getScrollX();
        scroller.startScroll(getScrollX(), getScrollY(),dx, getScrollY());
        if (onStateChangeListener != null)
        {
            onStateChangeListener.onClose(this);
        }
        invalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        //使view的滑动更加平滑
        if (scroller.computeScrollOffset())
        {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
    }

    //设置view的状态监听器
    public interface OnStateChangeListener
    {
        void onOpen(SlideLayout slideLayout);
        void onMove(SlideLayout slideLayout);
        void onClose(SlideLayout slideLayout);
    }

    private OnStateChangeListener onStateChangeListener;

    //暴露方法用于设置监听器
    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
        this.onStateChangeListener = onStateChangeListener;
    }
}

