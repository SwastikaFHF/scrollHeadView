package com.aitangba.testproject.verticalscroll;

import android.content.Context;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;


/**
 * Created by fhf11991 on 2016/6/2.
 */
public class ScrollHeadView extends LinearLayout implements NestedScrollingParent {

    private static final String TAG = "ScrollHeadView";

    private NestedScrollingParentHelper mParentHelper;
    private int headerHeight = 0;
    private int offsetY = 0;

    public ScrollHeadView(Context context) {
        this(context, null);
    }

    public ScrollHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mParentHelper = new NestedScrollingParentHelper(this);
    }

    public void setHeadView(View headView) {
        offsetY = 0;
        addView(headView, 0);
    }

    public void setHeadView(int headViewId) {
        View headView = LayoutInflater.from(getContext()).inflate(headViewId, this, false);
        setHeadView(headView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(getChildCount() < 2) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        /**
         * 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
         */
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int heightTemp = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            LayoutParams marginLayoutParams = (LayoutParams) childView.getLayoutParams();
            final int topMargin = marginLayoutParams.topMargin;
            final int bottomMargin = marginLayoutParams.bottomMargin;
            final int childHeight = childView.getMeasuredHeight();

            heightTemp = heightTemp + childHeight;
            if (i == 0 && childView.getVisibility() != GONE) {
                headerHeight = childHeight + topMargin;
            }
            heightTemp += (topMargin + bottomMargin);
        }

        int height;
        if (heightMode == MeasureSpec.EXACTLY) {
            height = sizeHeight;
        } else {
            height = Math.max(heightTemp, sizeHeight);
        }

//        height = height + offsetY;
        setMeasuredDimension(sizeWidth, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(getChildCount() < 2) {
            super.onLayout(changed, l, t, r, b);
            return;
        }

        final int limitHeight = getMeasuredHeight();
        int heightTemp = -offsetY;

        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            LayoutParams marginLayoutParams = (LayoutParams) childView.getLayoutParams();
            final int topMargin = marginLayoutParams.topMargin;
            final int bottomMargin = marginLayoutParams.bottomMargin;

            final int childHeight = childView.getMeasuredHeight();

            final int top = heightTemp + topMargin;
            final int heightNeed = top + childHeight ;
            final int bottom = heightNeed > limitHeight ? limitHeight : heightNeed;

            childView.layout(l, top, r, bottom);
            heightTemp = heightNeed + bottomMargin;
        }
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return true;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        mParentHelper.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if(getChildCount() < 2) {
            return;
        }

        final int offsetYTemp = offsetY + dy;
        final int headerHeight = this.headerHeight;
        if (dy > 0) {                  //向上滑动
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
        } else if (dy < 0) {            //向下滑动
            if(offsetYTemp >= 0) {
                offsetY = offsetYTemp;
                requestLayout(-dy);
                consumed[1] += dy;
            }else {
                if(offsetY != 0){
                    final int offY = 0 - offsetY;
                    offsetY = 0;
                    requestLayout(-offY);
                    consumed[1] += offY;
                }
            }
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

    @Override
    public void onNestedScroll (View target,int dxConsumed, int dyConsumed, int dxUnconsumed,
                                int dyUnconsumed){
    }

    @Override
    public void onStopNestedScroll (View target){
    }

    @Override
    public boolean onNestedPreFling (View target,float velocityX, float velocityY){
        Log.d(TAG, "onNestedPreFling");
        return false;
    }

    @Override
    public boolean onNestedFling (View target,float velocityX, float velocityY,
                                  boolean consumed){
        Log.d(TAG, "onNestedFling");
        return false;
    }

    /**
     * Utility method to check whether a {@link View} can scroll up from it's current position.
     * Handles platform version differences, providing backwards compatible functionality where
     * needed.
     */
    private static boolean canViewScrollUp(View view) {
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            // For ICS and above we can call canScrollVertically() to determine this
            return ViewCompat.canScrollVertically(view, -1);
        } else {
            if (view instanceof AbsListView) {
                // Pre-ICS we need to manually check the first visible item and the child view's top
                // value
                final AbsListView listView = (AbsListView) view;
                return listView.getChildCount() > 0 &&
                        (listView.getFirstVisiblePosition() > 0
                                || listView.getChildAt(0).getTop() < listView.getPaddingTop());
            } else {
                // For all other view types we just check the getScrollY() value
                return view.getScrollY() > 0;
            }
        }
    }
}