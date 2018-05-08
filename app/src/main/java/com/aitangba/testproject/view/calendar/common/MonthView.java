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
    private DataSetObserver mDataSetObserver;

    public MonthView(Context context) {
        this(context, null);
    }

    public MonthView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MonthView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDataSetObserver = new DataSetObserver() {
            @Override
            public void notifyDataSetChanged() {
                for (int i = 0, count = getChildCount(); i < count; i++) {
                    mAdapter.onBindView(getChildAt(i), i);
                }
            }
        };
    }

    public void setAdapter(@NonNull BaseCellAdapter adapter) {
        mAdapter = adapter;
        mAdapter.setDataSetObserver(mDataSetObserver);

        final int dataSize = mAdapter.getCount(); // 30
        Log.d(TAG, "dataSize = " + dataSize);
        for (int i = 0; i < dataSize; i++) {
            if (i >= getChildCount()) {
                View view = mAdapter.onCreateView(getLayoutInflater(), this);
                addView(view);
            }
            mAdapter.onBindView(getChildAt(i), i);
        }

        final int childCount = getChildCount(); // 28
        if (childCount > dataSize) { // remove children
            for (int i = 0, dis = childCount - dataSize; i < dis; i++) {
                removeViewAt(getChildCount() - 1);
            }
        }
        requestLayout();
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
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (itemHeight == -1) {
                    if (child.getVisibility() == View.GONE) {
                        continue;
                    } else {
                        child.measure(childWidthMeasureSpec, heightMeasureSpec);
                        itemHeight = child.getMeasuredHeight();
                    }
                } else {
                    int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(itemHeight, MeasureSpec.EXACTLY);
                    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
                }
            }

            int spaceSize = mAdapter == null ? 0 : mAdapter.getSpaceCount();
            double heightTemp = Math.ceil((double) (childCount + spaceSize) / COLUMN_SIZE) * itemHeight;
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

    public static abstract class BaseCellAdapter {

        private DataSetObserver mDataSetObserver;

        private void setDataSetObserver(DataSetObserver dataSetObserver) {
            mDataSetObserver = dataSetObserver;
        }

        protected abstract View onCreateView(LayoutInflater layoutInflater, ViewGroup parent);

        protected abstract void onBindView(View child, int position);

        public abstract int getSpaceCount();

        public abstract int getCount();

        public void notifyDataSetChanged() {
            if (mDataSetObserver != null) {
                mDataSetObserver.notifyDataSetChanged();
            }
        }
    }

    public interface DataSetObserver {
        void notifyDataSetChanged();
    }
}