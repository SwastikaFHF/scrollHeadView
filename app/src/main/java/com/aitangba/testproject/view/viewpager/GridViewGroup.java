package com.aitangba.testproject.view.viewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by fhf11991 on 2016/7/1.
 */
public class GridViewGroup extends ViewGroup{

    private int mSizeLimit = 1;

    public GridViewGroup(Context context) {
        super(context);
    }

    public GridViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSizeLimit(int sizeLimit) {
        if(sizeLimit <= 0)return;
        mSizeLimit = sizeLimit;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);
        final int childCount = getChildCount();
        final int row = (int) Math.ceil( ((double) childCount) / mSizeLimit);
        int heightTemp = 0;
        int widthLimit = widthSize / 3;
        for(int i = 0 ; i < row ; i++) {
            int childHeight = getMaxHeightAtSameRom(i, childCount);

            for(int j = 0; j < mSizeLimit; j++) {
                int index = i * mSizeLimit + j;
                if(index >= childCount)break;
                View childView = getChildAt(index);
                int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(widthLimit, MeasureSpec.EXACTLY);
                int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);
                childView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
            heightTemp += childHeight;
        }
        setMeasuredDimension(widthSize, heightTemp);
    }

    private int getMaxHeightAtSameRom(int row, int childCount) {
        int childViewHeightTemp  = 0;
        for(int i = 0; i < mSizeLimit; i++) {
            int index = row * mSizeLimit + i;
            if(index >= childCount)break;
            View childView = getChildAt(index);
            int childViewHeight = childView.getMeasuredHeight();
            childViewHeightTemp = childViewHeightTemp > childViewHeight ? childViewHeightTemp : childViewHeight;
        }
        return childViewHeightTemp;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();
        final int row = (int) Math.ceil( ((double) childCount) / mSizeLimit);

        int heightTemp = 0;
        for(int i = 0 ; i < row ; i++) {
            int widthTemp = 0;
            for(int j = 0; j < mSizeLimit; j++) {
                final int index = i * mSizeLimit + j;
                if(index >= childCount)break;

                View childView = getChildAt(index);
                int childWidth = childView.getMeasuredWidth();
                int childHeight = childView.getMeasuredHeight();

                int left = widthTemp;
                int top = heightTemp;
                int right = widthTemp + childWidth;
                int bottom = top + childHeight;

                childView.layout(left, top, right, bottom);

                if(j % mSizeLimit  == mSizeLimit - 1) {
                    widthTemp = 0;
                    heightTemp += childHeight;
                } else {
                    widthTemp += childWidth;
                }
            }
        }
    }
}
