package com.aitangba.testproject.horizonscroll;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by fhf11991 on 2016/6/23.
 */
public class HorizonScrollTestView extends ViewGroup {

    private Scroller mScroller;
    private GestureDetector mGestureDetector;

    public HorizonScrollTestView(Context context) {
        this(context, null);
    }

    public HorizonScrollTestView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizonScrollTestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);
        mGestureDetector = new GestureDetector(context, new OnGestureListener());
    }

    private int mOffsetX = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            childView.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(getChildCount() == 0)return;

        int widthTemp = 0;
        for(int i=0;i<getChildCount();i++) {
            View child = getChildAt(i);
            final int left = i == 0 ? mOffsetX : widthTemp;
            final int right = left + child.getMeasuredWidth();
            child.layout(left, 0, right, b);
            widthTemp = right;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {// 会更新Scroller中的当前x,y位置
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    private class OnGestureListener implements GestureDetector.OnGestureListener {

        private int mLastX;
        @Override
        public boolean onDown(MotionEvent e) {
            mLastX = (int)e.getX();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            final int widthTotal = getMeasuredWidth() * getChildCount();
            final int x = (int)e2.getX();
            final int distance = x - mLastX;
            final int startScrollX = getScrollX();
            int stopScrollX; //

            int offsetXTemp = mOffsetX + distance;
            if(distance < 0 ) {  //向左滑动
                if(Math.abs(offsetXTemp) > widthTotal) {
                    stopScrollX = widthTotal - mOffsetX;
                }else {
                    stopScrollX = -distance;
                }
            } else {             //向右滑动
                if(offsetXTemp > 0) {
                    stopScrollX = mOffsetX;
                } else {
                    stopScrollX = -distance;
                }
            }
            mScroller.startScroll(startScrollX, 0, stopScrollX, 0);
            mOffsetX -= stopScrollX;
            mLastX = x;
            invalidate();
            return true;
        }


        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }
}
