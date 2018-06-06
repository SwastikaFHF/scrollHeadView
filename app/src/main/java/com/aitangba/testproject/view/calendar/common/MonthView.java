package com.aitangba.testproject.view.calendar.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by fhf11991 on 2018/3/27.
 */

public class MonthView extends ViewGroup {

    private static final String TAG = "MonthView";
    private static final int COLUMN_SIZE = 7;
    private BaseCellAdapter mAdapter;
    private LayoutInflater mLayoutInflater;
    private static final int MAX_CELL_SIZE = 31;

    public MonthView(Context context) {
        this(context, null);
    }

    public MonthView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MonthView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void checkCells() {
        if(mAdapter == null) {
            return;
        }

        final int childCount = getChildCount();
        if(childCount < MAX_CELL_SIZE) {
            for (int i = Math.max(0, childCount - 1); i < MAX_CELL_SIZE; i++) {
                View view = mAdapter.onCreateView(getLayoutInflater(), this);
                addView(view);
            }
        } else if(childCount > MAX_CELL_SIZE) {
            for (int i = Math.max(0, MAX_CELL_SIZE - 1); i < childCount; i++) {
                removeViewAt(getChildCount() - 1);
            }
        }
    }

    public void setAdapter(@NonNull BaseCellAdapter adapter) {
        mAdapter = adapter;

        checkCells();
        refreshCells();
    }

    private void refreshCells() {
        if(mAdapter == null) {
            return;
        }

        final int dataSize = mAdapter.getCount();
        final int childCount = getChildCount();
        Log.d(TAG, " dataSize = " + dataSize + " childCount = " + childCount);

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (i >= dataSize) {
                child.setVisibility(GONE);
            } else {
                child.setVisibility(VISIBLE);
                mAdapter.onBindView(child, i);
            }
        }
    }

    private LayoutInflater getLayoutInflater() {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(getContext());
        }
        return mLayoutInflater;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();

        if (childCount == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int widthLimit = widthSize / COLUMN_SIZE;
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(widthLimit, MeasureSpec.EXACTLY);

            int itemHeight = -1;
            int invisibleChildCount = 0;
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if(child.getVisibility() == GONE) {
                    invisibleChildCount ++;
                    continue;
                }

                if (itemHeight == -1) { // 获取第一个可见的cell的高度
                    child.measure(childWidthMeasureSpec, heightMeasureSpec);
                    itemHeight = child.getMeasuredHeight();
                } else {
                    int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(itemHeight, MeasureSpec.EXACTLY);
                    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
                }
            }

            int spaceSize = mAdapter == null ? 0 : mAdapter.getSpaceCount();
            double heightTemp = Math.ceil((double) (childCount - invisibleChildCount + spaceSize) / COLUMN_SIZE) * itemHeight;
            setMeasuredDimension(widthSize, getPaddingTop() + (int) heightTemp + getPaddingBottom());
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();
        final int spaceSize = mAdapter == null ? 0 : mAdapter.getSpaceCount();

        int widthTemp = getChildAt(0).getMeasuredWidth() * spaceSize;
        int heightTemp = getPaddingTop();
        int index = spaceSize;
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if(childView.getVisibility() == GONE) {
                continue;
            }

            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();

            int left = widthTemp;
            int top = heightTemp;
            int right = widthTemp + childWidth;
            int bottom = top + childHeight;

            childView.layout(left, top, right, bottom);

            index += 1;
            if (index % COLUMN_SIZE == 0) {
                widthTemp = 0;
                heightTemp += childHeight;
            } else {
                widthTemp += childWidth;
            }
        }
    }

}
