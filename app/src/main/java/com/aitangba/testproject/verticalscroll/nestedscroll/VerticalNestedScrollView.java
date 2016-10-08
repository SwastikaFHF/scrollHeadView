package com.aitangba.testproject.verticalscroll.nestedscroll;

import android.content.Context;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by fhf11991 on 2016/9/30.
 */

public class VerticalNestedScrollView extends ViewGroup implements NestedScrollingParent, NestedScrollingChild {
    private static final String TAG = "VerticalView";

    private NestedScrollingParentHelper mParentHelper;
    private NestedScrollingChildHelper mNestedScrollingChildHelper;
    private int headerHeight = 0;
    private int offsetY = 0;
    private final int[] mParentOffsetInWindow = new int[2];

    public VerticalNestedScrollView(Context context) {
        this(context, null);
    }

    public VerticalNestedScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mParentHelper = new NestedScrollingParentHelper(this);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int heightTemp = 0;
        int widthTemp = 0;

        for (int i = 0; i < getChildCount(); i++) {
            final View childView = getChildAt(i);
            final int childHeight = childView.getMeasuredHeight();
            final int childWidth = childView.getMeasuredWidth();

            if(childView.getVisibility() != GONE) {
                heightTemp += childHeight;
                widthTemp += childWidth;
            }
        }

        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();
        final int paddingRight = getPaddingRight();
        final int paddingBottom = getPaddingBottom();
        int width = widthMode == MeasureSpec.EXACTLY ?
                widthSize : Math.min(widthSize , widthTemp + paddingLeft + paddingRight);
        int height = heightMode == MeasureSpec.EXACTLY ?
                heightSize : Math.min(heightSize , heightTemp + paddingTop + paddingBottom);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        final int limitHeight = getMeasuredHeight();
        int heightTemp = -offsetY;

        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            final int childHeight = childView.getMeasuredHeight();

            final int top = heightTemp;
            final int heightNeed = top + childHeight ;
            final int bottom = heightNeed > limitHeight ? limitHeight : heightNeed;

            childView.layout(l, top, r, bottom);
            heightTemp = heightNeed;
        }
    }

    /**
     * 重置所有子View的位置
     * @param offsetY
     */
    private void requestLayout(int offsetY) {
        for (int i = 0; i < getChildCount(); i ++){
            View childView = getChildAt(i);
            childView.offsetTopAndBottom(offsetY);
        }
        requestLayout();
    }

    //--------------------- Do nestedScroll as a parent -----------------------------

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        int upViewsHeight = 0;
        for(int i = 0; i < getChildCount() ; i ++) {
            View childView = getChildAt(i);
            if(childView == child) {
                break;
            } else {
                int childHeight = childView.getMeasuredHeight();
                upViewsHeight += childHeight;
            }
        }
        headerHeight = upViewsHeight;
        return true;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        mParentHelper.onNestedScrollAccepted(child, target, axes);

        // Dispatch up to the nested parent
        startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
    }

    private final int[] mParentScrollConsumed = new int[2];
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        // Dispatch up to the nested parent first
        final int[] parentConsumed = mParentScrollConsumed;
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0];
            consumed[1] += parentConsumed[1];
        }

        //than do something myself
        if (dy > 0) {                  //向上滑动
            final int offsetYTemp = offsetY + dy;
            final int headerHeight = this.headerHeight;
            if(offsetYTemp <= headerHeight){
                offsetY = offsetYTemp;
                requestLayout(-dy);
                consumed[1] += dy;
            }else{
                if(offsetY != headerHeight){
                    final int offY = headerHeight - offsetY;
                    offsetY = headerHeight;
                    requestLayout(-offY);
                    consumed[1] += offY;
                }
            }
        }
    }

    @Override
    public void onNestedScroll (View target,int dxConsumed, int dyConsumed, int dxUnconsumed,
                                int dyUnconsumed){
        final int dy = dyUnconsumed;

        int middleViewDyConsumed = 0;
        int middleViewDyUnconsumed = 0;

        if (dy < 0) {            //向下滑动
            final int offsetYTemp = offsetY + dy;
            if(offsetYTemp >= 0) {
                offsetY = offsetYTemp;

                middleViewDyConsumed = dy;
                middleViewDyUnconsumed = 0;
                requestLayout(-middleViewDyConsumed);
            }else {
                if(offsetY != 0){
                    final int offY = 0 - offsetY;
                    offsetY = 0;
                    middleViewDyConsumed = offY;
                    middleViewDyUnconsumed = dxUnconsumed + offY;
                    requestLayout(-middleViewDyConsumed);
                } else {
                    middleViewDyConsumed = 0;
                    middleViewDyUnconsumed = dy;
                }
            }
        }

        // Now let our nested parent consume the leftovers
        dispatchNestedScroll(dxConsumed, dyConsumed, middleViewDyConsumed, middleViewDyUnconsumed,
                mParentOffsetInWindow);
    }

    @Override
    public void onStopNestedScroll (View target){
        // Dispatch up our nested parent
        stopNestedScroll();
    }

    @Override
    public boolean onNestedPreFling (View target,float velocityX, float velocityY){
        Log.d(TAG, "onNestedPreFling");
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling (View target, float velocityX, float velocityY,
                                  boolean consumed){
        Log.d(TAG, "onNestedFling" + "    velocityY = " + velocityY);
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }


    //--------------------- Do nestedScroll as a child -----------------------------
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled(){
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll(){
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed,
                                        int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
         return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                 dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
}
