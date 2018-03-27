package com.aitangba.testproject.view.flowlayout;

import android.content.Context;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * Created by fhf11991 on 2017/5/18.
 */

public class FlowLayout extends ViewGroup {

    private final static String TAG = "FlowLayout";

    private @Gravity int mGravity = CENTER;
    private SparseIntArray mRowInfoList = new SparseIntArray(); // just record the max height of every row
    private int mHorizontalSpace = 10; // the same as leftMargin,every first child do not use leftMargin
    private int mVerticalSpace = 10;  // the same as topMargin,every first row do not use topMargin

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setChildGravity(@Gravity int gravity) {
        mGravity = gravity;
        requestLayout();
    }

    public void setHorizontalSpace(int horizontalSpace) {
        mHorizontalSpace = horizontalSpace;
        requestLayout();
    }

    public void setVerticalSpace(int verticalSpace) {
        mVerticalSpace = verticalSpace;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mRowInfoList.clear();

        int childCount = getChildCount();
        if(childCount == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        final int leftMargin = mHorizontalSpace;
        final int topMargin = mVerticalSpace;
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthSpace = widthSize - getPaddingLeft() - getPaddingRight();

        int widthSpaceNeed = 0;
        int rowMaxWidth = 0;

        int row = -1; // in case,first child is out of bounds
        int index = 0;

        for(int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            if (child.getVisibility() == View.GONE) {
                continue;
            }

            final int childHeight = child.getMeasuredHeight();
            final int childWidth = child.getMeasuredWidth();
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();

            widthSpaceNeed = widthSpaceNeed + leftMargin + childWidth;

            if(row == -1 || widthSpaceNeed > widthSpace) { // current row can not contain this child, so turn next row !!!
                row = row + 1;
                index = 0;
                widthSpaceNeed = 0 + leftMargin + childWidth;
            }

            layoutParams.setPosition(row, index);
            index = index + 1;

            int height = mRowInfoList.get(row, 0);
            mRowInfoList.put(row, Math.max(height, childHeight));
            rowMaxWidth = Math.max(rowMaxWidth, widthSpaceNeed);
        }

        int maxHeight = 0;
        for(int i = 0; i < mRowInfoList.size(); i ++) {
            int height = mRowInfoList.get(i, 0);
            int margin = i == 0 ? 0 : topMargin; // first row do not need topMargin,so do not add topMargin
            maxHeight = maxHeight + height + margin;
        }


        // first child in every row do not need leftMargin,so remove it
        final int maxWidth = rowMaxWidth - leftMargin + getPaddingLeft() + getPaddingRight();

        int width = maxWidth;

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if(widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
            if(getLayoutParams() instanceof LinearLayout.LayoutParams) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) getLayoutParams();
                if(layoutParams.weight != 0) {
                    width = maxWidth;
                }
            }
        }

        setMeasuredDimension(width, maxHeight + getPaddingTop() + getPaddingBottom());

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int childCount = getChildCount();
        if(childCount == 0) {
            return;
        }

        int rowTemp = 0;
        int widthTemp = getPaddingLeft();
        int heightTemp = getPaddingTop();

        int previousRowHeight = 0; // just record, and adjust the height of every row !!!
        for(int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() == View.GONE) {
                continue;
            }

            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            int childHeight = child.getMeasuredHeight();
            int childWidth = child.getMeasuredWidth();
            int currentRowHeight = mRowInfoList.get(layoutParams.row, 0);

            if(layoutParams.row != rowTemp) { // not the same row, reset some params
                widthTemp = getPaddingLeft();
                heightTemp = heightTemp + previousRowHeight + mVerticalSpace; // in this situation, every row must add extra vertical space !!!
            }

            int left = widthTemp;
            int top = heightTemp + getChildTopDiff(currentRowHeight, childHeight);
            child.layout(left, top, left + childWidth, top + childHeight);

            widthTemp = widthTemp + childWidth + mHorizontalSpace;
            previousRowHeight = currentRowHeight;

            rowTemp = layoutParams.row;
        }
    }

    private int getChildTopDiff(int rowHeight, int childHeight) {
        int diff;
        switch (mGravity) {
            case TOP:
                diff = 0;
                break;
            case CENTER:
                diff = (rowHeight - childHeight) / 2;
                break;
            case BOTTOM:
                diff = rowHeight - childHeight;
                break;
            default:
                diff = 0;
                break;
        }
        return Math.max(0, diff);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    private static class LayoutParams extends ViewGroup.MarginLayoutParams {

        public int row;
        public int index;

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public void setPosition(int row, int index) {
            this.row = row;
            this.index = index;
        }
    }

    @IntDef({TOP, CENTER, BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Gravity {

    }

    public final static int TOP = 1;
    public final static int CENTER = 2;
    public final static int BOTTOM = 3;

}
